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

