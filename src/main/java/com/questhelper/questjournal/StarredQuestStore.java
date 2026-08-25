/*
 * Copyright (c) 2026, nanopink
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.questhelper.QuestHelperConfig;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;

final class StarredQuestStore
{
	// Retain the original key so existing profile-scoped stars remain available.
	static final String STARRED_QUESTS_CONFIG_KEY = "journalSavedQuests";
	private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>()
	{
	}.getType();

	private final ConfigManager configManager;
	private final Gson gson;

	@Inject
	StarredQuestStore(ConfigManager configManager, Gson gson)
	{
		this.configManager = Objects.requireNonNull(configManager, "configManager");
		this.gson = Objects.requireNonNull(gson, "gson");
	}

	Set<String> load()
	{
		if (configManager.getRSProfileKey() == null)
		{
			return Collections.emptySet();
		}

		String raw = configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			STARRED_QUESTS_CONFIG_KEY);
		if (raw == null || raw.isBlank())
		{
			return Collections.emptySet();
		}

		try
		{
			List<String> storedIds = gson.fromJson(raw, STRING_LIST_TYPE);
			return immutableIds(storedIds);
		}
		catch (RuntimeException ignored)
		{
			return Collections.emptySet();
		}
	}

	void save(Set<String> starredQuestIds)
	{
		Objects.requireNonNull(starredQuestIds, "starredQuestIds");
		if (configManager.getRSProfileKey() == null)
		{
			return;
		}

		List<String> normalizedIds = normalizedIds(starredQuestIds);
		if (normalizedIds.isEmpty())
		{
			configManager.unsetRSProfileConfiguration(
				QuestHelperConfig.QUEST_HELPER_GROUP,
				STARRED_QUESTS_CONFIG_KEY);
			return;
		}

		configManager.setRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			STARRED_QUESTS_CONFIG_KEY,
			gson.toJson(normalizedIds));
	}

	void clear()
	{
		save(Collections.emptySet());
	}

	private static Set<String> immutableIds(Iterable<String> ids)
	{
		List<String> normalizedIds = normalizedIds(ids);
		if (normalizedIds.isEmpty())
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(new LinkedHashSet<>(normalizedIds));
	}

	private static List<String> normalizedIds(Iterable<String> ids)
	{
		if (ids == null)
		{
			return Collections.emptyList();
		}

		Set<String> sortedIds = new TreeSet<>();
		for (String id : ids)
		{
			if (id == null)
			{
				continue;
			}
			String normalized = id.trim();
			if (!normalized.isEmpty())
			{
				sortedIds.add(normalized);
			}
		}
		return new ArrayList<>(sortedIds);
	}
}
