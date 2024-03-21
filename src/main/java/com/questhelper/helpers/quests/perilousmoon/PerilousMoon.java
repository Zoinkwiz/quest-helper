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
package com.questhelper.helpers.quests.perilousmoon;

import com.questhelper.bank.banktab.BankSlotIcons;
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
import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuestDescriptor(
	quest = QuestHelperQuest.PERILOUS_MOON
)
public class PerilousMoon extends BasicQuestHelper
{
	//Items Required
	ItemRequirement knife, bigFishingNet, rope, pestleAndMortar;

	ItemRequirement combatGear, staminaPotions, antipoison;

	Requirement r;

	QuestStep s;

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
		knife = new ItemRequirement("Knife", ItemID.KNIFE).isNotConsumed();
		knife.canBeObtainedDuringQuest();
		bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).isNotConsumed();
		bigFishingNet.canBeObtainedDuringQuest();
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.canBeObtainedDuringQuest();
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortar.canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		// Recommended
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
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
		return Arrays.asList(knife, bigFishingNet, rope, pestleAndMortar);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, staminaPotions, antipoison);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.SLAYER, 48),
			new SkillRequirement(Skill.HUNTER, 20),
			new SkillRequirement(Skill.FISHING,  20),
			new SkillRequirement(Skill.RUNECRAFT, 20),
			new SkillRequirement(Skill.CONSTRUCTION, 10),
			new QuestRequirement(QuestHelperQuest.TWILIGHTS_PROMISE, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Sulphur Nagua (level 98)",
			"Blue Moon (level 329)",
			"Blood Moon (level 329)",
			"Eclipse Moon (level 329)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SLAYER, 40000),
			new ExperienceReward(Skill.RUNECRAFT, 5000),
			new ExperienceReward(Skill.HUNTER, 5000),
			new ExperienceReward(Skill.FISHING, 5000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
//		return Collections.singletonList(new UnlockReward());
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

//		allSteps.add(new PanelDetails(", Arrays.asList());

		return allSteps;
	}
}
