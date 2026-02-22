/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.helpers.quests.vampyreslayer;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class VampyreSlayer extends BasicQuestHelper
{
	// Required items
	ItemRequirement hammer;
	ItemRequirement beer;
	ItemRequirement combatGear;

	// Recommended items
	ItemRequirement varrockTeleport;
	ItemRequirement draynorManorTeleport;
	ItemRequirement garlic;
	ItemRequirement garlicObtainable;

	// Mid-quest item requirements
	ItemRequirement stake;

	// Zones
	Zone manor;
	Zone basement;
	Zone upstairsInMorgans;

	// Miscellaneous requirements
	ZoneRequirement inManor;
	ZoneRequirement inBasement;
	ZoneRequirement isUpstairsInMorgans;
	NpcCondition draynorNearby;

	// Steps
	NpcStep talkToMorgan;

	ObjectStep goUpstairsMorgan;
	ObjectStep getGarlic;
	ConditionalStep cGetGarlic;

	NpcStep talkToHarlow;
	NpcStep talkToHarlowAgain;
	ObjectStep enterDraynorManor;
	ObjectStep goDownToBasement;
	ObjectStep openCoffin;
	NpcStep killDraynor;

	@Override
	protected void setupZones()
	{
		basement = new Zone(new WorldPoint(3074, 9767, 0), new WorldPoint(3081, 9779, 0));
		manor = new Zone(new WorldPoint(3097, 3354, 0), new WorldPoint(3119, 3373, 0));
		upstairsInMorgans = new Zone(new WorldPoint(3096, 3266, 1), new WorldPoint(3102, 3270, 1));
	}

	@Override
	protected void setupRequirements()
	{
		varrockTeleport = new ItemRequirement("Teleport to Varrock", ItemID.POH_TABLET_VARROCKTELEPORT);
		draynorManorTeleport = new ItemRequirement("Draynor manor teleport", ItemID.TELETAB_DRAYNOR);
		stake = new ItemRequirement("Stake", ItemID.STAKE);
		stake.setTooltip("You can get another from Dr. Harlow in the Blue Moon Inn in Varrock.");
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC);
		garlic.setTooltip("Optional, makes Count Draynor weaker");
		beer = new ItemRequirement("A beer, or 2 coins to buy one", ItemID.BEER);
		combatGear = new ItemRequirement("Combat gear + food to defeat Count Draynor", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		garlicObtainable = new ItemRequirement("Garlic", ItemID.GARLIC);
		garlicObtainable.canBeObtainedDuringQuest();

		inBasement = new ZoneRequirement(basement);
		inManor = new ZoneRequirement(manor);
		isUpstairsInMorgans = new ZoneRequirement(upstairsInMorgans);
		draynorNearby = new NpcCondition(NpcID.COUNT_DRAYNOR);
	}

	public void setupSteps()
	{
		talkToMorgan = new NpcStep(this, NpcID.MORGAN, new WorldPoint(3098, 3268, 0), "Talk to Morgan in the north of Draynor Village.");
		talkToMorgan.addDialogStep("Yes.");

		goUpstairsMorgan = new ObjectStep(this, ObjectID.STAIRS, new WorldPoint(3100, 3267, 0), "Climb the stairs.");
		getGarlic = new ObjectStep(this, ObjectID.GARLICCUPBOARDOPEN, new WorldPoint(3096, 3270, 1), "Search the cupboard.");
		getGarlic.addAlternateObjects(ObjectID.GARLICCUPBOARDSHUT);
		cGetGarlic = new ConditionalStep(this, goUpstairsMorgan, "Get garlic from the cupboard upstairs in Morgan's house.\nYou can tick off this section to skip the garlic acquisition.");
		cGetGarlic.addStep(isUpstairsInMorgans, getGarlic);

		talkToHarlow = new NpcStep(this, NpcID.DR_HARLOW, new WorldPoint(3222, 3399, 0), "Talk to Dr. Harlow in the Blue Moon Inn in Varrock.", beer);
		talkToHarlow.addDialogStep("Morgan needs your help!");
		talkToHarlowAgain = new NpcStep(this, NpcID.DR_HARLOW, new WorldPoint(3222, 3399, 0), "Talk to Dr. Harlow again with a beer. You can buy one for 2gp in the Blue Moon Inn.", beer);
		enterDraynorManor = new ObjectStep(this, ObjectID.HAUNTEDDOORL, new WorldPoint(3108, 3353, 0), "Prepare to fight Count Draynor (level 34), and enter Draynor Manor.", combatGear, stake, hammer, garlic);
		goDownToBasement = new ObjectStep(this, ObjectID.CRYPTSTAIRSDOWN, new WorldPoint(3116, 3358, 0), "Enter Draynor Manor's basement.", combatGear, stake, hammer, garlic);
		openCoffin = new ObjectStep(this, ObjectID.VAMPCOFFIN, new WorldPoint(3078, 9776, 0), "Open the coffin and kill Count Draynor.", combatGear, stake, hammer, garlic);
		killDraynor = new NpcStep(this, NpcID.COUNT_DRAYNOR, new WorldPoint(3077, 9769, 0), "Kill Count Draynor.", combatGear, stake, hammer, garlic);
		openCoffin.addSubSteps(killDraynor);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToMorgan);

		var getGarlicAndStake = new ConditionalStep(this, cGetGarlic);
		getGarlicAndStake.addStep(garlic, talkToHarlow);

		steps.put(1, getGarlicAndStake);

		var prepareAndKillDraynor = new ConditionalStep(this, getGarlicAndStake);
		prepareAndKillDraynor.addStep(draynorNearby, killDraynor);
		prepareAndKillDraynor.addStep(inBasement, openCoffin);
		prepareAndKillDraynor.addStep(inManor, goDownToBasement);
		prepareAndKillDraynor.addStep(stake, enterDraynorManor);

		steps.put(2, prepareAndKillDraynor);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			hammer,
			beer,
			combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			draynorManorTeleport,
			garlicObtainable
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Count Draynor (level 34)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.ATTACK, 4825)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToMorgan
		)));

		var garlicSection = new PanelDetails("Get garlic", List.of(
			cGetGarlic
		));
		garlicSection.setLockingStep(cGetGarlic);
		sections.add(garlicSection);

		sections.add(new PanelDetails("Get a stake", List.of(
			talkToHarlow,
			talkToHarlowAgain
		), List.of(
			beer
		)));

		sections.add(new PanelDetails("Kill Count Draynor", List.of(
			enterDraynorManor,
			goDownToBasement,
			openCoffin
		), List.of(
			hammer,
			stake,
			garlic,
			combatGear
		)));

		return sections;
	}
}
