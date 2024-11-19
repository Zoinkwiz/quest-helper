/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements.runelite;

import com.questhelper.questinfo.PlayerQuests;
import com.questhelper.requirements.util.Operation;
import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;

public class PlayerQuestStateRequirement extends RuneliteRequirement
{
	private final int expectedIntValue;
	private Operation operation;

	private final PlayerQuests runeliteConfigIdentifier;

	public PlayerQuestStateRequirement(ConfigManager configManager, PlayerQuests runeliteIdentifier, int expectedValue)
	{
		super(configManager, runeliteIdentifier.getConfigValue(), Integer.toString(expectedValue));
		expectedIntValue = expectedValue;
		runeliteConfigIdentifier = runeliteIdentifier;
	}

	public PlayerQuestStateRequirement(ConfigManager configManager, PlayerQuests runeliteIdentifier, int expectedValue, Operation operation)
	{
		super(configManager, runeliteIdentifier.getConfigValue(), Integer.toString(expectedValue));
		this.operation = operation;
		expectedIntValue = expectedValue;
		runeliteConfigIdentifier = runeliteIdentifier;
	}

	public PlayerQuestStateRequirement getNewState(int incrementedStateQuantity)
	{
		return new PlayerQuestStateRequirement(configManager, runeliteConfigIdentifier, expectedIntValue + incrementedStateQuantity);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		validateCondition(client);
		checkState();
	}

	private void checkState()
	{
		String value = getConfigValue();
		if (operation == null)
		{
			setState(expectedValue.equals(value));
			return;
		}

		try
		{
			int intValue;
			intValue = Integer.parseInt(value);
			setState(operation.check(intValue, expectedIntValue));
		}
		catch (NumberFormatException err)
		{
			System.out.println(err.getMessage());
			setState(false);
		}
	}

	public RuneliteConfigSetter getSetter()
	{
		return new RuneliteConfigSetter(configManager, runeliteIdentifier, expectedValue);
	}
}
