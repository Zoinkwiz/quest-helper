package com.questhelper.helpers.mischelpers.farmruns;

import net.runelite.client.config.ConfigManager;
import java.util.function.Consumer;

public class FarmingConfigChangeHandler
{
	public static void handleConfigChange(ConfigManager configManager, String configGroupName,
			Enum<?> defaultValue, Consumer<Enum<?>> updateFunction)
	{
		String newValue = configManager.getRSProfileConfiguration(configGroupName, defaultValue.name());

		if (newValue != null)
		{
			try
			{
				Enum<?> enumValue = Enum.valueOf(defaultValue.getDeclaringClass(), newValue);
				updateFunction.accept(enumValue);
			}
			catch (IllegalArgumentException e)
			{
				configManager.setRSProfileConfiguration(configGroupName, defaultValue.name(), defaultValue);
			}
		}
		else
		{
			configManager.setConfiguration(configGroupName, defaultValue.name(), defaultValue);
		}
	}

}
