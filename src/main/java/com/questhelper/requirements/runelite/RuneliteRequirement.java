/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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

import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.Requirement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;

public class RuneliteRequirement extends AbstractRequirement
{
	@Getter
	protected final String CONFIG_GROUP = QuestHelperConfig.QUEST_BACKGROUND_GROUP;

	protected final String displayText;
	protected final String runeliteIdentifier;

	@Getter
	protected final String expectedValue;
	protected final ConfigManager configManager;

	@Getter
	protected final Map<String, Requirement> requirements;

	protected String initValue;

	public RuneliteRequirement(ConfigManager configManager, String id, String expectedValue, String text, Map<String, Requirement> requirements)
	{
		this(configManager, id, "false", expectedValue, text, requirements);
	}

	public RuneliteRequirement(ConfigManager configManager, String id, String expectedValue)
	{
		this(configManager, id, expectedValue, new HashMap<>());
	}

	public RuneliteRequirement(ConfigManager configManager, String id, String expectedValue,
							   Map<String, Requirement> requirements)
	{
		this(configManager, id, expectedValue, "Expecting " + expectedValue, requirements);
	}

	public RuneliteRequirement(ConfigManager configManager, String id, String expectedValue,
							  Requirement requirement)
	{
		this(configManager, id, expectedValue, Collections.singletonMap(expectedValue, requirement));
	}

	public RuneliteRequirement(ConfigManager configManager, String id, Requirement requirement)
	{
		this(configManager, id, "true", Collections.singletonMap("true", requirement));
	}

	public RuneliteRequirement(ConfigManager configManager, String id, Requirement requirement, String displayText)
	{
		this(configManager, id, "true", displayText, Collections.singletonMap("true", requirement));
	}

	public RuneliteRequirement(ConfigManager configManager, String id, String initValue, String expectedValue,
							   String text, Map<String, Requirement> requirements)
	{
		this.configManager = configManager;
		this.runeliteIdentifier = id;
		this.displayText = text;
		this.expectedValue = expectedValue;
		this.requirements = requirements;
		this.initValue = initValue;
		initWithValue(initValue);
	}

	// Used for the KeyringRequirement
	public RuneliteRequirement(ConfigManager configManager, String id, String expectedValue, String text)
	{
		this.configManager = configManager;
		this.runeliteIdentifier = id;
		this.displayText = text;
		this.expectedValue = expectedValue;
		this.requirements = new HashMap<>();
	}

	@Override
	public boolean check(Client client)
	{
		String value = getConfigValue();
		return expectedValue.equals(value);
	}

	public void validateCondition(Client client)
	{
		requirements.forEach((value, req) -> {
			if (req.check(client)) setConfigValue(value);
		});
	}

	@Override
	public String getDisplayText()
	{
		String returnText;
		if (displayText != null)
		{
			returnText = displayText;
		}
		else
		{
			returnText = "You need " + runeliteIdentifier;
		}

		return returnText;
	}

	public String getConfigValue()
	{
		String value = configManager.getRSProfileConfiguration(CONFIG_GROUP, runeliteIdentifier);
		if (initValue != null && value == null)
		{
			setConfigValue(initValue);
			return initValue;
		}
		return configManager.getRSProfileConfiguration(CONFIG_GROUP, runeliteIdentifier);
	}

	public void setConfigValue(String obj)
	{
		if (configManager.getRSProfileKey() == null) return;
		configManager.setRSProfileConfiguration(CONFIG_GROUP, runeliteIdentifier, obj);
	}

	public boolean configExists()
	{
		return configManager.getRSProfileConfiguration(CONFIG_GROUP, runeliteIdentifier) != null;
	}

	public void initWithValue(String value)
	{
		if (!configExists())
		{
			setConfigValue(value);
		}
	}
}

