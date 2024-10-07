/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.tools;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class VarSaver
{
	private final Map<Integer, Integer> oldVarplayer = new HashMap<>();
	private final Map<Integer, Integer> oldVarbit = new HashMap<>();
	private final Map<Integer, Integer> oldVarClientInt = new HashMap<>();
	private final Map<Integer, String> oldVarClientStr = new HashMap<>();
	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	private FileOutputStream varbitFileStream;

	public void startUp() throws FileNotFoundException
	{
		var questHelperDir = new File(RuneLite.RUNELITE_DIR, "quest-helper");
		//noinspection ResultOfMethodCallIgnored
		questHelperDir.mkdir();
		var path = Path.of(questHelperDir.getPath()).resolve(String.format("%d-varbits.txt", System.currentTimeMillis())).toString();
		varbitFileStream = new FileOutputStream(path);
	}

	public void shutDown()
	{
		try
		{
			varbitFileStream.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		varbitFileStream = null;
	}

	private void onVarbitChanged(VarbitChanged event)
	{
		var varIndex = event.getVarbitId();

		var oldValue = oldVarbit.getOrDefault(varIndex, 0);
		var newValue = event.getValue();
		oldVarbit.put(varIndex, newValue);

		if (oldValue != newValue)
		{
			addVarLog(VarType.VARBIT, varIndex, oldValue, newValue);
		}
	}

	private void onVarplayerChanged(VarbitChanged event)
	{
		var varIndex = event.getVarpId();

		var oldValue = oldVarplayer.getOrDefault(varIndex, 0);
		var newValue = event.getValue();
		oldVarplayer.put(varIndex, newValue);

		if (oldValue != newValue)
		{
			addVarLog(VarType.VARP, varIndex, oldValue, newValue);
		}
	}

	public void onVarChanged(VarbitChanged event)
	{
		int varIndex = event.getVarbitId();
		if (varIndex == -1)
		{
			onVarplayerChanged(event);
		}
		else
		{
			onVarbitChanged(event);
		}
	}

	private void addVarLog(VarType type, int name, int oldValue, int newValue)
	{
		addVarLog(type, name, Integer.toString(oldValue), Integer.toString(newValue));
	}

	private void addVarLog(VarType type, int name, String oldValue, String newValue)
	{
		int tick = client.getTickCount();
		LocalDateTime now = LocalDateTime.now();
		String body = String.format("[%s] [%s] %s %d changed: %s -> %s",
			now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
			tick,
			type.getName(),
			name,
			oldValue,
			newValue);
		if (varbitFileStream != null)
		{
			try
			{
				varbitFileStream.write(body.getBytes());
				varbitFileStream.write('\n');
			}
			catch (IOException e)
			{
				log.warn("failed to write to varbit file", e);
			}
		}
	}

	public void onVarClientIntChanged(VarClientIntChanged event)
	{
		int varIndex = event.getIndex();

		int newValue = client.getVarcIntValue(varIndex);
		int oldValue = oldVarClientInt.getOrDefault(varIndex, 0);
		oldVarClientInt.put(varIndex, newValue);

		if (oldValue != newValue)
		{
			addVarLog(VarType.VARCINT, varIndex, oldValue, newValue);
		}
	}

	public void onVarClientStrChanged(VarClientStrChanged event)
	{
		int varIndex = event.getIndex();
		var newValue = client.getVarcStrValue(varIndex);
		var oldValue = oldVarClientStr.getOrDefault(varIndex, "");
		oldVarClientStr.put(varIndex, newValue);

		if (!Objects.equals(oldValue, newValue))
		{
			if (oldValue != null)
			{
				oldValue = "\"" + oldValue + "\"";
			}
			else
			{
				oldValue = "null";
			}
			if (newValue != null)
			{
				newValue = "\"" + newValue + "\"";
			}
			else
			{
				newValue = "null";
			}
			addVarLog(VarType.VARCSTR, varIndex, oldValue, newValue);
		}
	}

	@Getter
	private enum VarType
	{
		VARBIT("Varbit"),
		VARP("VarPlayer"),
		VARCINT("VarClientInt"),
		VARCSTR("VarClientStr");

		private final String name;

		VarType(String name)
		{
			this.name = name;
		}
	}
}
