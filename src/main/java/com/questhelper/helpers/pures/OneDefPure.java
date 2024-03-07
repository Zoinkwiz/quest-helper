package com.questhelper.helpers.pures;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.runelite.api.Skill;

public class OneDefPure
{


	public boolean isSafeForPure(QuestHelper questHelper)
	{
		return !hasDefenseRewardInQuestChain(questHelper);
	}

	/**
	 * Recursively get all quest requirements
	 *
	 * @param questHelper the quest to be tested
	 * @return a boolean indicating if any of the quests give defense experience
	 */
	boolean hasDefenseRewardInQuestChain(QuestHelper questHelper)
	{
		if (hasDefenseReward(questHelper)) {
			return true;
		}
		List<QuestRequirement> questRequirements = Optional.of(questHelper).map(QuestHelper::getGeneralRequirements).orElse(Collections.emptyList()).stream()
			.filter(req -> req instanceof QuestRequirement)
			.map(req -> (QuestRequirement) req)
			.collect(Collectors.toList());
		if (questRequirements.isEmpty())
		{
			return hasDefenseReward(questHelper);
		}
		else
		{
			return questRequirements.stream()
				.map(QuestRequirement::getQuest)
				.map(QuestHelperQuest::getQuestHelper)
				.map(this::hasDefenseRewardInQuestChain)
				.reduce(false, (a, b) -> a || b);
		}
	}

	boolean hasDefenseReward(QuestHelper questHelper)
	{
		return Optional.of(questHelper).map(QuestHelper::getExperienceRewards).orElse(Collections.emptyList()).stream().map(ExperienceReward::getSkill).anyMatch(Skill.DEFENCE::equals);
	}
}
