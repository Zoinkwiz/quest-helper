/*
 * Copyright (c) 2026, INSERT_YOUR_NAME_HERE
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
package com.questhelper.helpers.quests.mudfootsbigadventure;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

public class MudfootsBigAdventure extends BasicQuestHelper
{
	// Required items
	ItemRequirement meleeWeapon;

	// Recommended items
	ItemRequirement stabWeapon, rangedWeapon, magicWeapon, food;

	// Steps
	NpcStep talkToHarlan, talkToMudfoot, confrontRangerOutlaw, fightRangerOutlaw, speakAfterRangerOutlaw, fightMageOutlaw, speakAfterMageOutlaw,
		    fightMeleeOutlaw, speakAfterMeleeOutlaw, finishQuest;

	@Override
	protected void setupZones()
	{

	}

	@Override
	protected void setupRequirements()
	{
		stabWeapon = new ItemRequirement("A stab weapon", -1, 1 ).isNotConsumed();
		stabWeapon.canBeObtainedDuringQuest();
		stabWeapon.setTooltip("A bronze sword can be obtained from Captain Harlan during the quest");
		stabWeapon.setDisplayItemId(ItemID.BRONZE_SWORD);

		meleeWeapon = new ItemRequirement("Any melee weapon", -1, 1).isNotConsumed();
		meleeWeapon.canBeObtainedDuringQuest();
		meleeWeapon.setTooltip("A bronze sword can be obtained from Captain Harlan after confronting the ranger outlaw");
		meleeWeapon.setDisplayItemId(ItemID.BRONZE_SWORD);

		rangedWeapon = new ItemRequirement("Any ranged weapon with ammunition", -1, 1).isNotConsumed();
		rangedWeapon.canBeObtainedDuringQuest();
		rangedWeapon.setTooltip("A shortbow and arrows can be obtained from Captain Harlan after beating the ranger outlaw");
		rangedWeapon.setDisplayItemId(ItemID.SHORTBOW);

		magicWeapon = new ItemRequirement("A staff with runes to cast magic spells", -1, 1).isNotConsumed();
		magicWeapon.canBeObtainedDuringQuest();
		magicWeapon.setTooltip("A staff and runes for magic can be obtained from Captain Harlan after beating the mage outlaw");
		magicWeapon.setDisplayItemId(ItemID.MINDRUNE);

		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
	}

	void setupSteps()
	{
		talkToHarlan = new NpcStep(this, NpcID.CAPTAIN_HARLAN, new WorldPoint(3216, 3240, 0), "Speak to Captain Harlan north of Lumbridge Castle.");
		talkToHarlan.addDialogStep("Do you need help with anything?");
		talkToHarlan.addDialogStep("Yes.");

		talkToMudfoot = new NpcStep(this, NpcID.MBA_MUDFOOT_VIS, new WorldPoint(3247, 3250, 0), "Speak to Mudfoot across the river.");

		confrontRangerOutlaw = new NpcStep(this, NpcID.MBA_OUTLAW_RANGED_VIS, new WorldPoint(3219, 3247, 0), "Confront the ranger outlaw near the Lumbridge General Store.");

		fightRangerOutlaw = new NpcStep(this, NpcID.MBA_OUTLAW_RANGED_VIS, new WorldPoint(3170, 3287, 0),
			"Confront the ranger outlaw by the windmill North of Lumbridge to fight the outlaw.");
		speakAfterRangerOutlaw = new NpcStep(this, NpcID.MBA_MUDFOOT_SPEAR_VIS, new WorldPoint(3170, 3287, 0),
			"Speak to Mudfoot after defeating the ranger outlaw.");

		fightMageOutlaw = new NpcStep(this, NpcID.MBA_OUTLAW_MAGIC_VIS, new WorldPoint(3170, 3287, 0),
			"Confront the mage outlaw by the H.A.M hideout west of Lumbridge Castle to fight the outlaw.");
		speakAfterMageOutlaw = new NpcStep(this, NpcID.MBA_MUDFOOT_SPEAR_VIS, new WorldPoint(3170, 3287, 0),
			"Speak to Mudfoot after defeating the mage outlaw.");

		fightMeleeOutlaw = new NpcStep(this, NpcID.MBA_OUTLAW_MELEE_VIS, new WorldPoint(3244, 3195, 0),
			"Confront the melee outlaw in the Lumbridge graveyard to fight the outlaw.");
		speakAfterMeleeOutlaw = new NpcStep(this, NpcID.MBA_MUDFOOT_SPEAR_VIS, new WorldPoint(3244, 3195, 0),
			"Speak to Mudfoot after defeating the melee outlaw.");

		finishQuest = new NpcStep(this, NpcID.CAPTAIN_HARLAN, new WorldPoint(3216, 3240, 0),
			"Speak to Captain Harlan after defeating the melee outlaw to finish the quest!");
		finishQuest.addDialogStep("All of those thieves have been dealt with.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToHarlan);
		steps.put(2, talkToHarlan);
		steps.put(4, talkToMudfoot);
		steps.put(6, confrontRangerOutlaw);
		steps.put(8, fightRangerOutlaw);
		steps.put(10, fightRangerOutlaw);
		steps.put(12, fightRangerOutlaw);
		steps.put(14, speakAfterRangerOutlaw);
		steps.put(16, fightMageOutlaw);
		steps.put(18, fightMageOutlaw);
		steps.put(20, speakAfterMageOutlaw);
		steps.put(22, fightMeleeOutlaw);
		steps.put(24, fightMeleeOutlaw);
		steps.put(26, speakAfterMeleeOutlaw);
		steps.put(28, finishQuest);
		steps.put(30, finishQuest);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			magicWeapon
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			stabWeapon,
			rangedWeapon,
			food
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("Three outlaws (Levels 6-10)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Four antique lamps (500 combat experience each)"),
			new UnlockReward("500 coins")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToHarlan
		)));

		sections.add(new PanelDetails("Ranger Outlaw", List.of(
			talkToMudfoot,
			confrontRangerOutlaw,
			fightRangerOutlaw,
			speakAfterRangerOutlaw
		), List.of(magicWeapon), List.of(meleeWeapon, food)));

		sections.add(new PanelDetails("Mage Outlaw", List.of(
			fightMageOutlaw,
			speakAfterMageOutlaw
		), List.of(magicWeapon), List.of(rangedWeapon, food)));

		sections.add(new PanelDetails("Melee Outlaw", List.of(
			fightMeleeOutlaw,
			speakAfterMeleeOutlaw
		), List.of(magicWeapon), List.of(food)));

		sections.add(new PanelDetails("Finishing Up", List.of(
			finishQuest
		)));

		return sections;
	}
}
