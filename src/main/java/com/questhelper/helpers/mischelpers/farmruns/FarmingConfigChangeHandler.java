/*
 * Copyright (c) 2024, Kerpackie <https://github.com/Kerpackie/>
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
package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import java.util.function.Consumer;

public class FarmingConfigChangeHandler
{

	public static <T extends Enum<T>> void handleFarmingEnumConfigChange(ConfigChanged event, String configKey,
		Class<T> enumClass, Consumer<T> updateAction, T defaultValue, ConfigManager configManager, QuestHelperPlugin questHelperPlugin)
	{
		if (event.getKey().equals(configKey))
		{
			try
			{
				T selectedEnumValue = Enum.valueOf(enumClass, event.getNewValue());
				updateAction.accept(selectedEnumValue);
				questHelperPlugin.refreshBank();
			}
			catch (IllegalArgumentException e)
			{
				configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configKey, defaultValue.name());
			}
		}
	}
}

