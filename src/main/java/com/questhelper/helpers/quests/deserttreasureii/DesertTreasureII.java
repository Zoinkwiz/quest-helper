/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_TREASURE_II
)
public class DesertTreasureII extends BasicQuestHelper
{
	DetailedQuestStep comingSoon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, comingSoon);
		return steps;
	}

	@Override
	public void setupRequirements()
	{

	}

	public void loadZones()
	{

	}

	public void setupConditions()
	{

	}

	public void setupSteps()
	{
		comingSoon = new DetailedQuestStep(this,
			"There will not be a helper for this quest for a month. Good luck and have fun with the quest!");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(new ItemRequirement("???", ItemID.BLOOD_DIAMOND));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(new ItemRequirement("???", ItemID.SHADOW_DIAMOND));
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("???");
		return reqs;
	}

//	@Override
//	public QuestPointReward getQuestPointReward()
//	{
//		return new QuestPointReward(3);
//	}

//	@Override
//	public List<ExperienceReward> getExperienceRewards()
//	{
//		return Collections.singletonList(new ExperienceReward(Skill.MAGIC, 20000));
//	}

//	@Override
//	public List<ItemReward> getItemRewards()
//	{
//		return Collections.singletonList(new ItemReward("Ring of Visibility", ItemID.RING_OF_VISIBILITY, 1));
//	}

//	@Override
//	public List<UnlockReward> getUnlockRewards()
//	{
//		return Arrays.asList(
//			new UnlockReward("Ability to use Ancient Magicks."),
//			new UnlockReward("Ability to purchase an Ancient Staff."),
//			new UnlockReward("Access to Smoke Dungeon."));
//	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Exploring the unknown",
			Arrays.asList(comingSoon)));


		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SECRETS_OF_THE_NORTH, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ENAKHRAS_LAMENT, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TEMPLE_OF_THE_EYE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_GARDEN_OF_DEATH, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.BELOW_ICE_MOUNTAIN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HIS_FAITHFUL_SERVANTS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MAGIC, 75));
		req.add(new SkillRequirement(Skill.FIREMAKING, 75));
		req.add(new SkillRequirement(Skill.THIEVING, 70));
		req.add(new SkillRequirement(Skill.HERBLORE, 62));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 60));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 60));

		return req;
	}
}
