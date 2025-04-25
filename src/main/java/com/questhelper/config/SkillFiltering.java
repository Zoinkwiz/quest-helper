/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2024, Harrison Tarr <https://github.com/harrison-tarr>
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
package com.questhelper.config;

import com.questhelper.QuestHelperConfig;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import net.runelite.api.Skill;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SkillFiltering
{
	/**
	 * Recursively get all quest requirements
	 *
	 * @param questHelper the quest to be tested
	 * @return a boolean indicating if any of the quests give defense experience
	 */
	public static boolean passesSkillFilter(QuestHelper questHelper)
	{
		if (!questPassesSkillFilter(questHelper))
		{
			return false;
		}
		List<QuestRequirement> questRequirements = questHelper.getGeneralRequirements() != null
			? questHelper.getGeneralRequirements().stream()
			.filter(QuestRequirement.class::isInstance)
			.map(QuestRequirement.class::cast)
			.collect(Collectors.toList())
			: Collections.emptyList();

		if (questRequirements.isEmpty())
		{
			return true;
		}
		else
		{
			return questRequirements.stream()
				.map(QuestRequirement::getQuest)
				.map(QuestHelperQuest::getQuestHelper)
				.allMatch(SkillFiltering::passesSkillFilter);
		}
	}

	public static boolean questPassesSkillFilter(QuestHelper questHelper)
	{
		List<Skill> skillsToFilterOut = Arrays.stream(Skill.values())
			.filter(skill -> "true".equals(questHelper.getConfigManager().getConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "skillfilter" + skill.getName())))
			.collect(Collectors.toList());

		boolean passesAll = true;
		if (questHelper.getGeneralRequirements() != null)
		{
			passesAll = questHelper.getGeneralRequirements().stream()
				.filter(SkillRequirement.class::isInstance)
				.map(SkillRequirement.class::cast)
				.noneMatch(req -> skillsToFilterOut.contains(req.getSkill()));
		}
		if (passesAll && questHelper.getExperienceRewards() != null)
		{
			passesAll = questHelper.getExperienceRewards().stream()
				.noneMatch(req -> skillsToFilterOut.contains(req.getSkill()));
		}
		return passesAll;
	}
}
