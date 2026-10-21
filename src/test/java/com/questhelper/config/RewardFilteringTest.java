package com.questhelper.config;

import com.questhelper.QuestHelperConfig;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.rewards.ExperienceReward;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RewardFilteringTest
{
	private ConfigManager configManager;

	@BeforeEach
	void setUp()
	{
		configManager = mock(ConfigManager.class);
	}

	private QuestHelper questRewarding(List<ExperienceReward> rewards)
	{
		QuestHelper questHelper = mock(QuestHelper.class);
		when(questHelper.getConfigManager()).thenReturn(configManager);
		when(questHelper.getExperienceRewards()).thenReturn(rewards);
		return questHelper;
	}

	private void select(Skill skill)
	{
		when(configManager.getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, RewardFiltering.CONFIG_KEY_PREFIX + skill.getName()))
			.thenReturn("true");
	}

	@Test
	void passesEverythingWhenNoSkillsSelected()
	{
		assertTrue(RewardFiltering.passesRewardFilter(questRewarding(null)));
		assertTrue(RewardFiltering.passesRewardFilter(questRewarding(List.of(new ExperienceReward(Skill.COOKING, 300)))));
	}

	@Test
	void onlyPassesQuestsRewardingASelectedSkill()
	{
		select(Skill.AGILITY);
		select(Skill.THIEVING);

		assertTrue(RewardFiltering.passesRewardFilter(questRewarding(List.of(new ExperienceReward(Skill.AGILITY, 1000)))));
		assertTrue(RewardFiltering.passesRewardFilter(questRewarding(Arrays.asList(
			new ExperienceReward(Skill.COOKING, 300), new ExperienceReward(Skill.THIEVING, 500, true)))));
		assertFalse(RewardFiltering.passesRewardFilter(questRewarding(List.of(new ExperienceReward(Skill.COOKING, 300)))));
		assertFalse(RewardFiltering.passesRewardFilter(questRewarding(null)));
	}
}
