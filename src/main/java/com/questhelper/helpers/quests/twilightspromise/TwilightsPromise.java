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
package com.questhelper.helpers.quests.twilightspromise;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestDescriptor;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuestDescriptor(
	quest = QuestHelperQuest.TWILIGHTS_PROMISE
)
public class TwilightsPromise extends BasicQuestHelper
{
	//Items Required
	ItemRequirement twoCombatStyles;

	ItemRequirement staminatPotion;

	Requirement r;

	QuestStep talkToEnnius, talkToMetzli, enterCrypt, talkToPrince, leaveCrypt, talkToEnnius2;

	// Knights
	QuestStep talkToArrun, pickpocketCitizen, returnAmulet;

	QuestStep talkToNel, searchCrate, returnToNel;

	QuestStep talkToMezan, defeatMezan;

	QuestStep talkToVelam, leadVelanToFountain;

	QuestStep talkToRegulus, feedRenu, travelToTeomat, enterTemple, talkToPrinceInTemple,
		talkToMetzliNearTemple, defeat8Cultists, finishQuest;

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
		twoCombatStyles = new ItemRequirement("Two combat styles", -1, -1).isNotConsumed();
		twoCombatStyles.setDisplayItemId(BankSlotIcons.getCombatGear());
		staminatPotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);
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
		return Arrays.asList(twoCombatStyles);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(staminatPotion);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("Knight of Varlamore (level 81)", "8 Cultists (level 34)");
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
			new ExperienceReward(Skill.THIEVING, 3000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to use the Civitas illa Fortis Teleport spell"),
			new UnlockReward("Ability to use the Quetzal Transport System")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

//		allSteps.add(new PanelDetails("", Arrays.asList());

		return allSteps;
	}
}
