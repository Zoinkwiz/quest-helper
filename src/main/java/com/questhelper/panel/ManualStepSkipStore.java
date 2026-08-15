package com.questhelper.panel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.questhelper.QuestHelperConfig;
import net.runelite.client.config.ConfigManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Persists per-RuneScape-profile manual step skips under {@link QuestHelperConfig#QUEST_HELPER_GROUP},
 * one JSON map key per displayed helper name (same grouping as sidebar order).
 */
public final class ManualStepSkipStore
{
	private static final Type MAP_TYPE = new TypeToken<Map<String, Boolean>>()
	{
	}.getType();

	private ManualStepSkipStore()
	{
	}

	public static String configKeyForQuest(String displayedQuestName)
	{
		return QuestHelperConfig.QUEST_HELPER_MANUAL_SKIPS_KEY_PREFIX + slug(displayedQuestName);
	}

	private static String slug(String name)
	{
		if (name == null)
		{
			return "helper";
		}
		String s = name.trim().replaceAll("[^a-zA-Z0-9_-]+", "_");
		if (s.length() > 96)
		{
			s = s.substring(0, 96);
		}
		return s.isEmpty() ? "helper" : s;
	}

	public static Map<String, Boolean> load(ConfigManager cm, Gson gson, String displayedQuestName)
	{
		if (cm == null || cm.getRSProfileKey() == null)
		{
			return new HashMap<>();
		}
		String ck = configKeyForQuest(displayedQuestName);
		String raw = cm.getRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck);
		if ((raw == null || raw.isBlank()) && cm.getRSProfileKey() != null)
		{
			String legacy = cm.getConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck);
			if (legacy != null && !legacy.isBlank())
			{
				cm.setRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck, legacy);
				cm.unsetConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck);
				raw = legacy;
			}
		}
		if (raw == null || raw.isBlank())
		{
			return new HashMap<>();
		}
		try
		{
			Map<String, Boolean> m = gson.fromJson(raw, MAP_TYPE);
			return m != null ? new HashMap<>(m) : new HashMap<>();
		}
		catch (RuntimeException e)
		{
			return new HashMap<>();
		}
	}

	public static void put(ConfigManager cm, Gson gson, String displayedQuestName, String slotKey, boolean skipped)
	{
		if (cm == null || cm.getRSProfileKey() == null || slotKey == null || slotKey.isBlank())
		{
			return;
		}
		String ck = configKeyForQuest(displayedQuestName);
		Map<String, Boolean> map = load(cm, gson, displayedQuestName);
		if (skipped)
		{
			map.put(slotKey, true);
		}
		else
		{
			map.remove(slotKey);
		}
		if (map.isEmpty())
		{
			cm.unsetRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck);
		}
		else
		{
			cm.setRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck, gson.toJson(map));
		}
	}

	public static void clearAll(ConfigManager cm, String displayedQuestName)
	{
		if (cm == null || cm.getRSProfileKey() == null)
		{
			return;
		}
		cm.unsetRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, configKeyForQuest(displayedQuestName));
	}
}
