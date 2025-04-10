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
package com.questhelper.helpers.quests.trollromance;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TrollRomance extends BasicQuestHelper
{
	//Items Required
	ItemRequirement ironBar, mapleLog, rope, cakeTin, swampTar, bucketOfWax, wax, sled, waxedSled, trollweissFlowers,
		combatGear, sledEquipped, climbingBoots, gamesNeck;

	Requirement inStrongholdFloor1, inStrongholdFloor2, inPrison, inTrollweiss, atFlowerLocation, inTrollCave, fightableArrgNearby;

	DetailedQuestStep enterStronghold, goDownToUg, goUpToUg, talkToUg, talkToAga, talkToTenzing, talkToDunstan, talkToDunstanAgain, useTarOnWax,
		useWaxOnSled, enterTrollCave, leaveTrollCave, equipSled, sledSouth, goDownToUgAgain, goUpToUgAgain, enterStrongholdAgain, talkToUgWithFlowers,
		goDownToUgForFight, goUpToUgForFight, enterStrongholdForFight, goDownToUgForEnd, goUpToUgForEnd, enterStrongholdForEnd, challengeArrg, killArrg, returnToUg;

	ObjectStep pickFlowers;

	//Zones
	Zone strongholdFloor1, strongholdFloor2, prison, trollweiss, flowerLocation, trollCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startingOffSteps = new ConditionalStep(this, enterStronghold);
		startingOffSteps.addStep(inStrongholdFloor1, talkToUg);
		startingOffSteps.addStep(inPrison, goUpToUg);
		startingOffSteps.addStep(inStrongholdFloor2, goDownToUg);

		steps.put(0, startingOffSteps);

		steps.put(5, talkToAga);
		steps.put(10, talkToTenzing);
		steps.put(15, talkToDunstan);
		steps.put(20, talkToDunstanAgain);

		ConditionalStep getSled = new ConditionalStep(this, useTarOnWax);
		getSled.addStep(wax, useWaxOnSled);

		steps.put(22, getSled);

		ConditionalStep getFlower = new ConditionalStep(this, enterTrollCave);
		getFlower.addStep(atFlowerLocation, pickFlowers);
		getFlower.addStep(new Conditions(inTrollweiss, sledEquipped), sledSouth);
		getFlower.addStep(inTrollweiss, equipSled);
		getFlower.addStep(inTrollCave, leaveTrollCave);

		steps.put(25, getFlower);

		ConditionalStep bringFlowerToUg = new ConditionalStep(this, enterStrongholdAgain);
		bringFlowerToUg.addStep(inStrongholdFloor1, talkToUgWithFlowers);
		bringFlowerToUg.addStep(inPrison, goUpToUgAgain);
		bringFlowerToUg.addStep(inStrongholdFloor2, goDownToUgAgain);

		steps.put(30, bringFlowerToUg);

		ConditionalStep defeatArrg = new ConditionalStep(this, enterStrongholdForFight);
		defeatArrg.addStep(fightableArrgNearby, killArrg);
		defeatArrg.addStep(inStrongholdFloor1, challengeArrg);
		defeatArrg.addStep(inPrison, goUpToUgForFight);
		defeatArrg.addStep(inStrongholdFloor2, goDownToUgForFight);

		steps.put(35, defeatArrg);

		ConditionalStep finishQuest = new ConditionalStep(this, enterStrongholdForEnd);
		finishQuest.addStep(inStrongholdFloor1, returnToUg);
		finishQuest.addStep(inPrison, goUpToUgForEnd);
		finishQuest.addStep(inStrongholdFloor2, goDownToUgForEnd);

		steps.put(40, finishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		mapleLog = new ItemRequirement("Maple/yew logs", ItemID.MAPLE_LOGS);
		mapleLog.addAlternates(ItemID.YEW_LOGS);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		cakeTin = new ItemRequirement("Cake tin", ItemID.CAKE_TIN);
		swampTar = new ItemRequirement("Swamp tar", ItemID.SWAMP_TAR);
		swampTar.setHighlightInInventory(true);
		bucketOfWax = new ItemRequirement("Bucket of wax", ItemID.BUCKET_WAX);
		bucketOfWax.setHighlightInInventory(true);
		wax = new ItemRequirement("Wax", ItemID.TROLLROMANCE_WAX);
		wax.setHighlightInInventory(true);
		sled = new ItemRequirement("Sled", ItemID.TROLLROMANCE_TOBOGGON);
		sled.setTooltip("You can have Dunstan make another. Bring him a maple log, a rope and an iron bar");
		sled.setHighlightInInventory(true);
		waxedSled = new ItemRequirement("Sled", ItemID.TROLLROMANCE_TOBOGGON_WAXED);
		waxedSled.setTooltip("You can have Dunstan make another. Bring him a maple log, a rope and an iron bar. You then can apply some wax to it");
		sledEquipped = new ItemRequirement("Sled", ItemID.TROLLROMANCE_TOBOGGON_WAXED, 1, true);
		sledEquipped.setHighlightInInventory(true);
		sledEquipped.setTooltip("You can have Dunstan make another. Bring him a maple log, a rope and an iron bar. You then can apply some wax to it");
		trollweissFlowers = new ItemRequirement("Trollweiss", ItemID.TROLLROMANCE_RARE_FLOWER);
		trollweissFlowers.setTooltip("You can get another from the Trollweiss mountain");
		climbingBoots = new ItemRequirement("Climbing boots", ItemCollections.CLIMBING_BOOTS).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear, food, and potions", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		gamesNeck = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
	}

	@Override
	protected void setupZones()
	{
		strongholdFloor1 = new Zone(new WorldPoint(2820, 10048, 1), new WorldPoint(2862, 10110, 1));
		strongholdFloor2 = new Zone(new WorldPoint(2820, 10048, 2), new WorldPoint(2862, 10110, 2));
		prison = new Zone(new WorldPoint(2822, 10049, 0), new WorldPoint(2859, 10110, 0));
		trollweiss = new Zone(new WorldPoint(2753, 3801, 0), new WorldPoint(2816, 3877, 0));
		flowerLocation = new Zone(new WorldPoint(2771, 3771, 0), new WorldPoint(2817, 3800, 0));
		trollCave = new Zone(new WorldPoint(2764, 10184, 0), new WorldPoint(2808, 10237, 0));
	}

	public void setupConditions()
	{
		inStrongholdFloor1 = new ZoneRequirement(strongholdFloor1);
		inStrongholdFloor2 = new ZoneRequirement(strongholdFloor2);
		inPrison = new ZoneRequirement(prison);
		inTrollweiss = new ZoneRequirement(trollweiss);
		inTrollCave = new ZoneRequirement(trollCave);
		atFlowerLocation = new ZoneRequirement(flowerLocation);
		fightableArrgNearby = new NpcCondition(NpcID.TROLLROMANCE_ARRG_ATTACKABLE);
	}

	public void setupSteps()
	{
		enterStronghold = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_DOOR, new WorldPoint(2839, 3690, 0), "Enter the Troll Stronghold.");

		goDownToUg = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRSTOP, new WorldPoint(2844, 10109, 2), "Climb down the north staircase.");

		goUpToUg = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRS, new WorldPoint(2853, 10107, 0), "Go up the stairs from the prison.");

		talkToUg = new NpcStep(this, NpcID.TROLLROMANCE_UG, new WorldPoint(2827, 10064, 1), "Talk to Ug in the south west room of the Troll Stronghold's first floor.");
		talkToUg.addDialogSteps("Awww, you poor troll. What seems to be the problem?", "Don't worry now, I'll see what I can do.");
		talkToUg.addSubSteps(enterStronghold, goDownToUg, goUpToUg);

		talkToAga = new NpcStep(this, NpcID.TROLLROMANCE_AGA, new WorldPoint(2828, 10104, 1), "Talk to Aga north of Ug.");
		talkToAga.addDialogStep("So... how's your... um... love life?");

		talkToTenzing = new NpcStep(this, NpcID.DEATH_SHERPA, new WorldPoint(2820, 3555, 0), "Talk to Tenzing west of Burthorpe.");
		talkToTenzing.addDialogSteps("Do you know where I can find Trollweiss?", "What would I need to make such a sled?");
		talkToDunstan = new NpcStep(this, NpcID.DEATH_SMITHY, new WorldPoint(2919, 3574, 0),
			"Talk to Dunstan in north east Burthorpe.", ironBar, mapleLog, rope);
		talkToDunstan.addDialogSteps("Talk about a quest.", "I need a sled!!", "No.");
		talkToDunstanAgain = new NpcStep(this, NpcID.DEATH_SMITHY, new WorldPoint(2919, 3574, 0), "Talk to Dunstan again.", ironBar, mapleLog, rope);
		talkToDunstanAgain.addDialogSteps("Talk about a quest.");
		useTarOnWax = new DetailedQuestStep(this, "Use some swamp tar on a bucket of wax.", swampTar, bucketOfWax, cakeTin);
		useWaxOnSled = new DetailedQuestStep(this, "Use the wax on the sled.", wax, sled);
		enterTrollCave = new ObjectStep(this, ObjectID.TROLLROMANCE_CAVEENTRANCE, new WorldPoint(2821, 3744, 0),
			"Enter the cave north of Trollheim. There are high leveled ice trolls in here, so Protect from Melee and " +
				"be careful!", waxedSled);
		leaveTrollCave = new ObjectStep(this, ObjectID.TROLLROMANCE_SNOW_CAVEWALL_CREVIS, new WorldPoint(2772, 10233, 0), "Leave the cave via the north crevice.");
		equipSled = new DetailedQuestStep(this, "Equip the sled.", sledEquipped);
		sledSouth = new ObjectStep(this, ObjectID.TROLLROMANCE_PISTE_WALK_BARRIER_DOWN, new WorldPoint(2773, 3835, 0), "Sled to the south.", sledEquipped);
		pickFlowers = new ObjectStep(this, ObjectID.TROLLROMANCE_RAREFLOWERS, new WorldPoint(2781, 3783, 0), "Pick a rare flower.");

		enterStrongholdAgain = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_DOOR, new WorldPoint(2839, 3690, 0), "Return to Ug with the trollweiss flowers.", trollweissFlowers, combatGear);

		goDownToUgAgain = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRSTOP, new WorldPoint(2844, 10109, 2), "Return to Ug with the trollweiss flowers.");

		goUpToUgAgain = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRS, new WorldPoint(2853, 10107, 0), "Return to Ug with the trollweiss flowers.");

		talkToUgWithFlowers = new NpcStep(this, NpcID.TROLLROMANCE_UG, new WorldPoint(2827, 10064, 1), "Return to Ug with the trollweiss flowers.", trollweissFlowers);
		talkToUgWithFlowers.addSubSteps(enterStrongholdAgain, goDownToUgAgain, goUpToUgAgain);

		enterStrongholdForEnd = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_DOOR, new WorldPoint(2839, 3690, 0), "Return to Ug to finish.");

		goDownToUgForEnd = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRSTOP, new WorldPoint(2844, 10109, 2), "Return to Ug to finish.");

		goUpToUgForEnd = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRS, new WorldPoint(2853, 10107, 0), "Return to Ug to finish.");

		enterStrongholdForFight = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_DOOR, new WorldPoint(2839, 3690, 0), "Challenge Arrg to a fight." +
			" Please check the wiki for instructions to setup a safe spot.", combatGear);

		goDownToUgForFight = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRSTOP, new WorldPoint(2844, 10109, 2), "Challenge Arrg to a fight." +
			" Please check the wiki for instructions to setup a safe spot.", combatGear);

		goUpToUgForFight = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_STAIRS, new WorldPoint(2853, 10107, 0), "Challenge Arrg to a fight." +
			" Please check the wiki for instructions to setup a safe spot.", combatGear);


		challengeArrg = new NpcStep(this, NpcID.TROLLROMANCE_ARRG, new WorldPoint(2829, 10095, 1), "Challenge Arrg to a fight." +
			" Please check the wiki for instructions to setup a safe spot.", combatGear);
		challengeArrg.addDialogStep("I am here to kill you!");
		challengeArrg.addSubSteps(enterStrongholdForFight, goUpToUgForFight, goDownToUgForFight);

		killArrg = new NpcStep(this, NpcID.TROLLROMANCE_ARRG_ATTACKABLE, "Kill Arrg.");
		((NpcStep) killArrg).addSafeSpots(new WorldPoint(2897, 3619, 0), new WorldPoint(2917, 3625, 0));

		returnToUg = new NpcStep(this, NpcID.TROLLROMANCE_UG, new WorldPoint(2827, 10064, 1), "Talk to Ug in the south west room to finish the quest.");
		returnToUg.addSubSteps(goDownToUgForEnd, goUpToUgForEnd, enterStrongholdForEnd);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ironBar, mapleLog, rope, cakeTin, swampTar, bucketOfWax, combatGear, climbingBoots);
	}
	
	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gamesNeck);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Arrg (level 113) can be safe spotted. Please check the wiki for instructions to setup a safe spot.");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.TROLL_STRONGHOLD, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 28));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
			new ExperienceReward(Skill.AGILITY, 8000),
			new ExperienceReward(Skill.STRENGTH, 4000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Diamond", ItemID.DIAMOND, 1),
			new ItemReward("Rubies", ItemID.RUBY, 2),
			new ItemReward("Emeralds", ItemID.EMERALD, 4),
			new ItemReward("A Sled", ItemID.TROLLROMANCE_TOBOGGON, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Sledding route from Trollweiss Mountain."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToUg, talkToAga), climbingBoots));
		allSteps.add(new PanelDetails("Make a sled", Arrays.asList(talkToTenzing, talkToDunstan, talkToDunstanAgain, useTarOnWax, useWaxOnSled), mapleLog, ironBar, rope, swampTar, bucketOfWax, cakeTin));
		allSteps.add(new PanelDetails("Get flowers", Arrays.asList(enterTrollCave, leaveTrollCave, equipSled, sledSouth, pickFlowers), waxedSled));
		allSteps.add(new PanelDetails("Fighting for Aga", Arrays.asList(talkToUgWithFlowers, challengeArrg, killArrg, returnToUg), trollweissFlowers, combatGear));


		return allSteps;
	}
}
