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
package com.questhelper.quests.trollromance;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.TROLL_ROMANCE
)
public class TrollRomance extends BasicQuestHelper
{
	ItemRequirement ironBar, mapleLog, rope, cakeTin, swampTar, bucketOfWax, wax, sled, waxedSled, trollweissFlowers, combatGear, sledEquipped;

	ConditionForStep inStrongholdFloor1, inStrongholdFloor2, inPrison, inTrollweiss, atFlowerLocation, hasWax, hasWaxedSled, hasFlower, inTrollCave,
		isSledEquipped, fightableArrgNearby;

	DetailedQuestStep enterStronghold, goDownToUg, goUpToUg, talkToUg, talkToAga, talkToTenzing, talkToDunstan, talkToDunstanAgain, useTarOnWax,
		useWaxOnSled, enterTrollCave, leaveTrollCave, equipSled, sledSouth, goDownToUgAgain, goUpToUgAgain, enterStrongholdAgain, talkToUgWithFlowers,
		goDownToUgForFight, goUpToUgForFight, enterStrongholdForFight, goDownToUgForEnd, goUpToUgForEnd, enterStrongholdForEnd, challengeArrg, killArrg, returnToUg;

	ObjectStep pickFlowers;

	Zone strongholdFloor1, strongholdFloor2, prison, trollweiss, flowerLocation, trollCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
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
		getSled.addStep(hasWax, useWaxOnSled);

		steps.put(22, getSled);

		ConditionalStep getFlower = new ConditionalStep(this, enterTrollCave);
		getFlower.addStep(atFlowerLocation, pickFlowers);
		getFlower.addStep(new Conditions(inTrollweiss, isSledEquipped), sledSouth);
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

	public void setupItemRequirements()
	{
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		mapleLog = new ItemRequirement("Maple/yew logs", ItemID.MAPLE_LOGS);
		mapleLog.addAlternates(ItemID.YEW_LOGS);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		cakeTin = new ItemRequirement("Cake tin", ItemID.CAKE_TIN);
		swampTar = new ItemRequirement("Swamp tar", ItemID.SWAMP_TAR);
		swampTar.setHighlightInInventory(true);
		bucketOfWax = new ItemRequirement("Bucket of wax", ItemID.BUCKET_OF_WAX);
		bucketOfWax.setHighlightInInventory(true);
		wax = new ItemRequirement("Wax", ItemID.WAX);
		wax.setHighlightInInventory(true);
		sled = new ItemRequirement("Sled", ItemID.SLED);
		sled.setTip("You can have Dunstan make another. Bring him a maple log, a rope and an iron bar");
		sled.setHighlightInInventory(true);
		waxedSled = new ItemRequirement("Sled", ItemID.SLED_4084);
		waxedSled.setTip("You can have Dunstan make another. Bring him a maple log, a rope and an iron bar. You then can apply some wax to it");
		sledEquipped = new ItemRequirement("Sled", ItemID.SLED_4084, 1, true);
		sledEquipped.setHighlightInInventory(true);
		sledEquipped.setTip("You can have Dunstan make another. Bring him a maple log, a rope and an iron bar. You then can apply some wax to it");
		trollweissFlowers = new ItemRequirement("Trollweiss", ItemID.TROLLWEISS);
		trollweissFlowers.setTip("You can get another from the Trollweiss mountain");
		combatGear = new ItemRequirement("Combat gear, food, and potions", -1, -1);
	}

	public void loadZones()
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
		inStrongholdFloor1 = new ZoneCondition(strongholdFloor1);
		inStrongholdFloor2 = new ZoneCondition(strongholdFloor2);
		inPrison = new ZoneCondition(prison);
		inTrollweiss = new ZoneCondition(trollweiss);
		inTrollCave = new ZoneCondition(trollCave);
		atFlowerLocation = new ZoneCondition(flowerLocation);
		hasWax = new ItemRequirementCondition(wax);
		hasWaxedSled = new ItemRequirementCondition(waxedSled);
		hasFlower = new ItemRequirementCondition(trollweissFlowers);
		isSledEquipped = new ItemRequirementCondition(sledEquipped);
		fightableArrgNearby = new NpcCondition(NpcID.ARRG_643);
	}

	public void setupSteps()
	{
		enterStronghold = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Enter the Troll Stronghold.");

		goDownToUg = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Climb down the north staircase.");
		goDownToUg.setWorldMapPoint(new WorldPoint(2971, 10172, 1));

		goUpToUg = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Go up the stairs from the prison.");
		goUpToUg.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToUg = new NpcStep(this, NpcID.UG, new WorldPoint(2827, 10064, 1), "Talk to Ug in the south west room of the Troll Stronghold's first floor.");
		talkToUg.addDialogSteps("Awww, you poor troll. What seems to be the problem?", "Don't worry now, I'll see what I can do.");
		talkToUg.setWorldMapPoint(new WorldPoint(2891, 10097, 0));
		talkToUg.addSubSteps(enterStronghold, goDownToUg, goUpToUg);

		talkToAga = new NpcStep(this, NpcID.AGA, new WorldPoint(2828, 10104, 1), "Talk to Aga north of Ug.");
		talkToAga.addDialogStep("So... how's your... um... love life?");
		talkToAga.setWorldMapPoint(new WorldPoint(2892, 10136, 0));

		talkToTenzing = new NpcStep(this, NpcID.TENZING, new WorldPoint(2820, 3555, 0), "Talk to Tenzing west of Burthorpe.");
		talkToTenzing.addDialogSteps("Do you know where I can find Trollweiss?", "What would I need to make such a sled?");
		talkToDunstan = new NpcStep(this, NpcID.DUNSTAN, new WorldPoint(2919, 3574, 0),
			"Talk to Dunstan in north east Burthorpe.", ironBar, mapleLog, rope);
		talkToDunstan.addDialogSteps("Talk about a quest.", "I need a sled!!", "No.");
		talkToDunstanAgain = new NpcStep(this, NpcID.DUNSTAN, new WorldPoint(2919, 3574, 0), "Talk to Dunstan again.", ironBar, mapleLog, rope);
		talkToDunstanAgain.addDialogSteps("Talk about a quest.");
		useTarOnWax = new DetailedQuestStep(this, "Use some swamp tar on a bucket of wax.", swampTar, bucketOfWax, cakeTin);
		useWaxOnSled = new DetailedQuestStep(this, "Use the wax on the sled.", wax, sled);
		enterTrollCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5007, new WorldPoint(2821, 3744, 0), "Enter the cave north of Trollheim.", waxedSled);
		leaveTrollCave = new ObjectStep(this, ObjectID.CREVASSE, new WorldPoint(2772, 10233, 0), "Leave the cave via the north crevice.");
		equipSled = new DetailedQuestStep(this, "Equip the sled", sledEquipped);
		sledSouth = new ObjectStep(this, ObjectID.SLOPE, new WorldPoint(2773, 3835, 0), "Sled to the south.");
		pickFlowers = new ObjectStep(this, ObjectID.RARE_FLOWERS, new WorldPoint(2781, 3783, 0), "Pick a rare flower.");

		enterStrongholdAgain = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Return to Ug with the trollweiss flowers.", trollweissFlowers, combatGear);

		goDownToUgAgain = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Return to Ug with the trollweiss flowers.");
		goDownToUgAgain.setWorldMapPoint(new WorldPoint(2971, 10172, 1));

		goUpToUgAgain = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Return to Ug with the trollweiss flowers.");
		goUpToUgAgain.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToUgWithFlowers = new NpcStep(this, NpcID.UG, new WorldPoint(2827, 10064, 1), "Return to Ug with the trollweiss flowers.", trollweissFlowers);
		talkToUgWithFlowers.setWorldMapPoint(new WorldPoint(2891, 10097, 0));
		talkToUgWithFlowers.addSubSteps(enterStrongholdAgain, goDownToUgAgain, goUpToUgAgain);

		enterStrongholdForEnd = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Return to Ug to finish.");

		goDownToUgForEnd = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Return to Ug to finish.");
		goDownToUgForEnd.setWorldMapPoint(new WorldPoint(2971, 10172, 1));

		goUpToUgForEnd = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Return to Ug to finish.");
		goUpToUgForEnd.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		enterStrongholdForFight = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Challenge Arrg to a fight.", combatGear);

		goDownToUgForFight = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Challenge Arrg to a fight.", combatGear);
		goDownToUgForFight.setWorldMapPoint(new WorldPoint(2971, 10172, 1));

		goUpToUgForFight = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Challenge Arrg to a fight.", combatGear);
		goUpToUgForFight.setWorldMapPoint(new WorldPoint(2853, 10106, 1));


		challengeArrg = new NpcStep(this, NpcID.ARRG, new WorldPoint(2829, 10095, 1), "Challenge to Arrg to fight.", combatGear);
		challengeArrg.addDialogStep("I am here to kill you!");
		challengeArrg.setWorldMapPoint(new WorldPoint(2892, 10127, 0));
		challengeArrg.addSubSteps(enterStrongholdForFight, goUpToUgForFight, goDownToUgForFight);

		killArrg = new NpcStep(this, NpcID.ARRG_643, "Kill Arrg.");
		returnToUg = new NpcStep(this, NpcID.UG, new WorldPoint(2827, 10064, 1), "Talk to Ug in the south west room to finish.");
		returnToUg.setWorldMapPoint(new WorldPoint(2891, 10097, 0));
		returnToUg.addSubSteps(goDownToUgForEnd, goUpToUgForEnd, enterStrongholdForEnd);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(ironBar, mapleLog, rope, cakeTin, swampTar, bucketOfWax, combatGear));
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Arrg (level 113)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToUg, talkToAga))));
		allSteps.add(new PanelDetails("Make a sled", new ArrayList<>(Arrays.asList(talkToTenzing, talkToDunstan, talkToDunstanAgain, useTarOnWax, useWaxOnSled)), mapleLog, ironBar, rope, swampTar, bucketOfWax, cakeTin));
		allSteps.add(new PanelDetails("Get flowers", new ArrayList<>(Arrays.asList(enterTrollCave, leaveTrollCave, equipSled, sledSouth, pickFlowers)), waxedSled));
		allSteps.add(new PanelDetails("Fighting for Aga", new ArrayList<>(Arrays.asList(talkToUgWithFlowers, challengeArrg, killArrg, returnToUg)), trollweissFlowers, combatGear));


		return allSteps;
	}
}
