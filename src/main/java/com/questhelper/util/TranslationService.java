/*
 * Copyright (c) 2026, Quest Helper contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves quest string translations using the English-as-key pattern: the
 * English text lives in the code and translations live per quest in
 * {@code translations/<lang>/<quest>.properties}, loaded on first use.
 * <p>
 * Never translate quest names, game entity names (items, NPCs, locations) or
 * dialog choices that match live game text.
 */
@Slf4j
@Singleton
public class TranslationService
{
	@Getter
	private volatile Language language = Language.ENGLISH;

	private final Map<String, Map<String, String>> loadedMessages = new ConcurrentHashMap<>();

	public void setLanguage(Language language)
	{
		this.language = language;
		loadedMessages.clear();
	}

	/**
	 * Returns the translation of {@code text} from the messages file for
	 * {@code namespace}, or {@code text} itself when the language is native,
	 * the file does not exist, or the file has no entry for {@code text}.
	 * <p>
	 * Keys may contain {@code =} (write it as {@code \=}) and newlines
	 * (write them as {@code \n}), so any English string can be a key.
	 *
	 * @param namespace per-quest file name under {@code translations/<lang>/},
	 *                  or {@code null} to get the native string
	 * @param text      the English string as it appears in the code
	 */
	public String t(@Nullable String namespace, @Nonnull String text)
	{
		assert text != null;

		String folder = language.getMessagesFolder();
		if (folder == null || namespace == null)
		{
			return text;
		}

		Map<String, String> messages = loadedMessages.computeIfAbsent(
			"/translations/" + folder + "/" + namespace + ".properties", TranslationService::loadMessages);
		String translated = messages.get(text);
		return translated != null ? translated : text;
	}

	private static Map<String, String> loadMessages(String resource)
	{
		try (InputStream in = TranslationService.class.getResourceAsStream(resource))
		{
			if (in == null)
			{
				return Collections.emptyMap();
			}

			Map<String, String> messages = new HashMap<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					String content = line.stripLeading();
					if (content.isEmpty() || content.startsWith("#") || content.startsWith("!"))
					{
						continue;
					}

					int separator = indexOfUnescapedEquals(content);
					if (separator <= 0)
					{
						continue;
					}

					messages.put(unescape(content.substring(0, separator)),
						unescape(content.substring(separator + 1).stripLeading()));
				}
			}
			return messages;
		}
		catch (IOException e)
		{
			log.warn("Failed to load translation file {}", resource, e);
			return Collections.emptyMap();
		}
	}

	private static int indexOfUnescapedEquals(String line)
	{
		boolean escaped = false;
		for (int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			if (escaped)
			{
				escaped = false;
			}
			else if (c == '\\')
			{
				escaped = true;
			}
			else if (c == '=')
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Resolves {@code \n}, {@code \t}, {@code \r}, {@code \\}, {@code \=} and
	 * {@code \:} so keys can contain any character but a newline in the file.
	 */
	private static String unescape(String text)
	{
		if (text.indexOf('\\') < 0)
		{
			return text;
		}

		StringBuilder result = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c != '\\' || i + 1 >= text.length())
			{
				result.append(c);
				continue;
			}

			char next = text.charAt(++i);
			switch (next)
			{
				case 'n':
					result.append('\n');
					break;
				case 't':
					result.append('\t');
					break;
				case 'r':
					result.append('\r');
					break;
				case '=':
				case ':':
				case '\\':
					result.append(next);
					break;
				default:
					result.append(c).append(next);
			}
		}
		return result.toString();
	}
}
