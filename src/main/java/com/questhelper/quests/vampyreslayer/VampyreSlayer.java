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
package com.questhelper.quests.vampyreslayer;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.VAMPYRE_SLAYER
)
public class VampyreSlayer extends BasicQuestHelper
{
	//Items Required
	ItemRequirement hammer, beer, garlic, garlicObtainable, stake, combatGear;

	//Items Recommended
	ItemRequirement varrockTeleport, draynorManorTeleport;

	Requirement inManor, inBasement, isUpstairsInMorgans, draynorNearby;

	QuestStep talkToMorgan, goUpstairsMorgan, getGarlic, ifNeedGarlic, talkToHarlow, talkToHarlowAgain, enterDraynorManor, goDownToBasement, openCoffin, killDraynor;

	//Zones
	Zone manor, basement, upstairsInMorgans;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMorgan);

		ConditionalStep getGarlicAndStake = new ConditionalStep(this, goUpstairsMorgan);
		getGarlicAndStake.addStep(garlic, talkToHarlow);
		getGarlicAndStake.addStep(isUpstairsInMorgans, getGarlic);

		steps.put(1, getGarlicAndStake);

		ConditionalStep prepareAndKillDraynor = new ConditionalStep(this, getGarlicAndStake);
		prepareAndKillDraynor.addStep(draynorNearby, killDraynor);
		prepareAndKillDraynor.addStep(inBasement, openCoffin);
		prepareAndKillDraynor.addStep(inManor, goDownToBasement);
		prepareAndKillDraynor.addStep(stake, enterDraynorManor);

		steps.put(2, prepareAndKillDraynor);


		return steps;
	}

	@Override
	public void setupRequirements()
	{
		varrockTeleport = new ItemRequirement("Teleport to Varrock", ItemID.VARROCK_TELEPORT);
		draynorManorTeleport = new ItemRequirement("Draynor manor teleport", ItemID.DRAYNOR_MANOR_TELEPORT);
		stake = new ItemRequirement("Stake", ItemID.STAKE);
		stake.setTooltip("You can get another from Dr. Harlow in the Blue Moon Inn in Varrock.");
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC);
		garlic.setTooltip("Optional, makes Count Draynor weaker");
		beer = new ItemRequirement("A beer, or 2 coins to buy one", ItemID.BEER);
		combatGear = new ItemRequirement("Combat gear + food to defeat Count Draynor", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		garlicObtainable = new ItemRequirement("Garlic (Obtainable during quest)", ItemID.GARLIC);
	}

	public void setupConditions()
	{
		inBasement = new ZoneRequirement(basement);
		inManor = new ZoneRequirement(manor);
		isUpstairsInMorgans = new ZoneRequirement(upstairsInMorgans);
		draynorNearby = new NpcCondition(NpcID.COUNT_DRAYNOR);
	}

	public void setupZones()
	{
		basement = new Zone(new WorldPoint(3074, 9767, 0), new WorldPoint(3081, 9779, 0));
		manor = new Zone(new WorldPoint(3097, 3354, 0), new WorldPoint(3119, 3373, 0));
		upstairsInMorgans = new Zone(new WorldPoint(3096, 3266, 1), new WorldPoint(3102, 3270, 1));
	}

	public void setupSteps()
	{
		talkToMorgan = new NpcStep(this, NpcID.MORGAN, new WorldPoint(3098, 3268, 0), "Talk to Morgan in the north of Draynor Village.");
		talkToMorgan.addDialogStep("Ok, I'm up for an adventure.");
		talkToMorgan.addDialogStep("Accept quest");
		goUpstairsMorgan = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(3100, 3267, 0), "Go upstairs in Morgan's house and search the cupboard for some garlic.");
		getGarlic = new ObjectStep(this, ObjectID.CUPBOARD_2613, new WorldPoint(3096, 3270, 1), "Search the cupboard upstairs in Morgan's house.");
		ifNeedGarlic = new DetailedQuestStep(this, "If you need garlic, you can get some from the cupboard upstairs in Morgan's house.");
		ifNeedGarlic.addSubSteps(goUpstairsMorgan, getGarlic);

		talkToHarlow = new NpcStep(this, NpcID.DR_HARLOW, new WorldPoint(3222, 3399, 0), "Talk to Dr. Harlow in the Blue Moon Inn in Varrock.", beer);
		talkToHarlow.addDialogStep("Morgan needs your help!");
		talkToHarlowAgain = new NpcStep(this, NpcID.DR_HARLOW, new WorldPoint(3222, 3399, 0), "Talk to Dr. Harlow again with a beer. You can buy one for 2gp in the Blue Moon Inn.", beer);
		enterDraynorManor = new ObjectStep(this, ObjectID.LARGE_DOOR_134, new WorldPoint(3108, 3353, 0), "Prepare to fight Count Draynor (level 34), and enter Draynor Manor.", combatGear, stake, hammer, garlic);
		goDownToBasement = new ObjectStep(this, ObjectID.STAIRS_2616, new WorldPoint(3116, 3358, 0), "Enter Draynor Manor's basement.", combatGear, stake, hammer, garlic);
		openCoffin = new ObjectStep(this, ObjectID.COFFIN_46237, new WorldPoint(3078, 9776, 0), "Open the coffin and kill Count Draynor.", combatGear, stake, hammer, garlic);
		killDraynor = new NpcStep(this, NpcID.COUNT_DRAYNOR, new WorldPoint(3077, 9769, 0), "Kill Count Draynor.", combatGear, stake, hammer, garlic);
		openCoffin.addSubSteps(killDraynor);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(hammer);
		reqs.add(beer);
		reqs.add(combatGear);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(varrockTeleport);
		reqs.add(draynorManorTeleport);
		reqs.add(garlicObtainable);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Count Draynor (level 34)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.ATTACK, 4825));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToMorgan, ifNeedGarlic)));
		allSteps.add(new PanelDetails("Get a stake", Arrays.asList(talkToHarlow, talkToHarlowAgain), beer));
		allSteps.add(new PanelDetails("Kill Count Draynor", Arrays.asList(enterDraynorManor, goDownToBasement, openCoffin), hammer, stake, garlic, combatGear));
		return allSteps;
	}
}
