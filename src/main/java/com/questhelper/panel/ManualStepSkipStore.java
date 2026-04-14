package com.questhelper.panel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.questhelper.QuestHelperConfig;
import net.runelite.client.config.ConfigManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Persists per-quest manual step skips under {@link QuestHelperConfig#QUEST_HELPER_GROUP} using one key per displayed helper name.
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
		if (cm == null)
		{
			return new HashMap<>();
		}
		String raw = cm.getConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, configKeyForQuest(displayedQuestName));
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
		if (cm == null || slotKey == null || slotKey.isBlank())
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
			cm.unsetConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck);
		}
		else
		{
			cm.setConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ck, gson.toJson(map));
		}
	}

	public static void clearAll(ConfigManager cm, String displayedQuestName)
	{
		if (cm == null)
		{
			return;
		}
		cm.unsetConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, configKeyForQuest(displayedQuestName));
	}
}
