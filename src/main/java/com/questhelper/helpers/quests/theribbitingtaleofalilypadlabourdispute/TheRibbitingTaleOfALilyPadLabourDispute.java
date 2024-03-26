/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.theribbitingtaleofalilypadlabourdispute;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestDescriptor;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_RIBBITING_TALE_OF_A_LILY_PAD_LABOUR_DISPUTE
)
public class TheRibbitingTaleOfALilyPadLabourDispute extends BasicQuestHelper
{
	//Items Required
	ItemRequirement axe;
	Requirement r;

	QuestStep talkToMarcellus, talkToBlueFrog, talkToMarcellus2, talkToGary, talkToYellowFrogs,
		chopOrangeTree, sabotageLilyPad, talkToGary2, talkToMarcellus3, talkToGaryToBlame,
		openChest, talkToGaryAfterChest, plantPlushy, defeatCuthbert, talkToMarcellusEnd,
		talkToGaryEnd;

	//Zones
	Zone z;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupRequirements();
		setupConditions();
		setupSteps();

//		steps.put(1, step);

		return steps;

		// NpcID.REGULUS_CENTO_12883
		// Told about going to place, 9652 1->2
		// Let's do it!

		// Gone to place:

		// 9650 0->1
		// 9652 2->3
		// Varp 4066 238 -> 494

		// Talked about twins to west
		// 9652 3->4
	}

	private void setupZones()
	{

	}

	@Override
	public void setupRequirements()
	{
		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		axe.canBeObtainedDuringQuest();
	}

	private void setupConditions()
	{

	}

	private void setupSteps()
	{


	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(axe);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.WOODCUTTING, 15),
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("Cuthbert, Lord of Dread (level 1)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(
			new ExperienceReward(Skill.WOODCUTTING, 2000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to a new Hardwood Farming Patch"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

//		allSteps.add(new PanelDetails("", Arrays.asList());

		return allSteps;
	}
}
