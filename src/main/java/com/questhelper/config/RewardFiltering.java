package com.questhelper.config;

import com.questhelper.QuestHelperConfig;
import com.questhelper.questhelpers.QuestHelper;
import net.runelite.api.Skill;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * When no skills are selected, all quests pass. When skills are selected,
 * only quests which reward experience in at least one of them are shown.
 */
public class RewardFiltering
{
	public static final String CONFIG_KEY_PREFIX = "rewardfilter";

	public static boolean passesRewardFilter(QuestHelper questHelper)
	{
		List<Skill> selectedSkills = Arrays.stream(Skill.values())
			.filter(skill -> "true".equals(questHelper.getConfigManager().getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, CONFIG_KEY_PREFIX + skill.getName())))
			.collect(Collectors.toList());

		if (selectedSkills.isEmpty())
		{
			return true;
		}

		return questHelper.getExperienceRewards() != null && questHelper.getExperienceRewards().stream()
			.anyMatch(reward -> selectedSkills.contains(reward.getSkill()));
	}
}
