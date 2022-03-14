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
package com.questhelper.quests.myarmsbigadventure;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.MY_ARMS_BIG_ADVENTURE
)
public class MyArmsBigAdventure extends BasicQuestHelper
{
	//Items Required
	ItemRequirement goutLump, bucket, bucketHighlight, farmingManual, ugthanki3, rake, dibber, spade, hardyGout, superCompost,
		rakeHighlight, dibberHighlight, hardyGoutHighlight, superCompostHighlight, spadeHighlight, plantCureHighlight,
		supercompost7, cureOrCompost, rakeHead, rakeHandle, climbingBoots, superCompost8;

	//Items Recommended
	ItemRequirement food, prayerPotions, combatGear, gamesNecklace;

	Requirement inStrongholdFloor1, inStrongholdFloor2, inPrison, onRoof, added3Dung, added7Comp, usedRake, givenCompost, givenHardy, givenDibber,
		givenCure, hasRakeHeadAndHandle, rakeHeadNearby, babyNearby, giantNearby;

	DetailedQuestStep enterStronghold, goDownToChef, goUpToChef, talkToBurntmeat, talkToMyArm, useBucketOnPot, enterStrongholdWithLump, goDownToArmWithLump, goUpToArmWithLump, talkToArmWithLump,
		enterStrongholdAfterLump, goDownAfterLump, goUpAfterLump, talkToArmAfterLump, goUpFromF1ToMyArm, goUpToMyArm, talkToMyArmUpstairs, readBook, talkToMyArmAfterReading, useUgthankiDung,
		useCompost, talkToMyArmAfterFertilising, talkToBarnaby, talkToMyArmAtTai, talkToMurcaily, talkToMyArmAfterMurcaily, enterStrongholdForFight, goUpToRoofForFight, talkToMyArmForFight, giveRake,
		goUpFromF1ForFight, goUpFromPrisonForFight, giveSupercompost, giveHardyGout, giveDibber, giveCure, talkAfterBoat, pickUpRakeHead, repairRake, talkToMyArmAfterBaby,
		talkToMyArmAfterGrow, killBabyRoc, killGiantRoc, giveSpade, goDownFromMyArmToBurntmeat, goDownToBurntmeat, talkToBurntmeatAgain, goUpToMyArmFinish, talkToMyArmAfterHarvest,
		goUpFromBurntmeatFinish, talkToMyArmFinish, enterStrongholdFinish;

	//Zones
	Zone strongholdFloor1, strongholdFloor2, prison, roof;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startingOffSteps = new ConditionalStep(this, enterStronghold);
		startingOffSteps.addStep(inStrongholdFloor1, talkToBurntmeat);
		startingOffSteps.addStep(inPrison, goUpToChef);
		startingOffSteps.addStep(inStrongholdFloor2, goDownToChef);

		steps.put(0, startingOffSteps);
		steps.put(10, startingOffSteps);
		steps.put(20, startingOffSteps);
		steps.put(30, startingOffSteps);

		ConditionalStep goTalkToBurntmeat = new ConditionalStep(this, enterStronghold);
		goTalkToBurntmeat.addStep(inStrongholdFloor1, talkToMyArm);
		goTalkToBurntmeat.addStep(inPrison, goUpToChef);
		goTalkToBurntmeat.addStep(inStrongholdFloor2, goDownToChef);


		steps.put(40, goTalkToBurntmeat);
		steps.put(50, goTalkToBurntmeat);

		ConditionalStep getGout = new ConditionalStep(this, useBucketOnPot);
		getGout.addStep(new Conditions(goutLump.alsoCheckBank(questBank), inStrongholdFloor1), talkToArmWithLump);
		getGout.addStep(new Conditions(goutLump.alsoCheckBank(questBank), inStrongholdFloor2), goDownToArmWithLump);
		getGout.addStep(new Conditions(goutLump.alsoCheckBank(questBank), inPrison), goUpToArmWithLump);
		getGout.addStep(goutLump.alsoCheckBank(questBank), enterStrongholdWithLump);

		steps.put(60, getGout);

		ConditionalStep talkAfterLump = new ConditionalStep(this, enterStrongholdAfterLump);
		talkAfterLump.addStep(new Conditions(inStrongholdFloor1), talkToArmWithLump);
		talkAfterLump.addStep(new Conditions(inStrongholdFloor2), goDownToArmWithLump);
		talkAfterLump.addStep(new Conditions(inPrison), goUpAfterLump);

		steps.put(70, talkAfterLump);

		ConditionalStep talkToArmOnRoofSteps = new ConditionalStep(this, enterStrongholdAfterLump);
		talkToArmOnRoofSteps.addStep(new Conditions(onRoof), talkToMyArmUpstairs);
		talkToArmOnRoofSteps.addStep(new Conditions(inStrongholdFloor2), goUpToMyArm);
		talkToArmOnRoofSteps.addStep(new Conditions(inStrongholdFloor1), goUpFromF1ToMyArm);
		talkToArmOnRoofSteps.addStep(new Conditions(inPrison), goUpAfterLump);

		steps.put(80, talkToArmOnRoofSteps);

		steps.put(90, readBook);

		ConditionalStep readBookForArm = new ConditionalStep(this, enterStrongholdAfterLump);
		readBookForArm.addStep(new Conditions(onRoof), talkToMyArmAfterReading);
		readBookForArm.addStep(new Conditions(inStrongholdFloor2), goUpToMyArm);
		readBookForArm.addStep(new Conditions(inStrongholdFloor1), goUpFromF1ToMyArm);
		readBookForArm.addStep(new Conditions(inPrison), goUpAfterLump);

		steps.put(100, readBookForArm);

		ConditionalStep treatPatch = new ConditionalStep(this, useUgthankiDung);
		treatPatch.addStep(added3Dung, useCompost);

		steps.put(110, treatPatch);

		ConditionalStep talkToArmAfterMakingPatch = new ConditionalStep(this, enterStrongholdAfterLump);
		talkToArmAfterMakingPatch.addStep(new Conditions(onRoof), talkToMyArmAfterFertilising);
		talkToArmAfterMakingPatch.addStep(new Conditions(inStrongholdFloor2), goUpToMyArm);
		talkToArmAfterMakingPatch.addStep(new Conditions(inStrongholdFloor1), goUpFromF1ToMyArm);
		talkToArmAfterMakingPatch.addStep(new Conditions(inPrison), goUpAfterLump);

		steps.put(120, talkToArmAfterMakingPatch);
		steps.put(130, talkToArmAfterMakingPatch);
		steps.put(140, talkToArmAfterMakingPatch);

		steps.put(150, talkToBarnaby);
		steps.put(160, talkAfterBoat);

		steps.put(170, talkToMyArmAtTai);

		steps.put(180, talkToMurcaily);
		steps.put(190, talkToMurcaily);
		steps.put(200, talkToMurcaily);

		steps.put(210, talkToMyArmAfterMurcaily);

		ConditionalStep prepareToFight = new ConditionalStep(this, enterStrongholdForFight);
		prepareToFight.addStep(new Conditions(onRoof), talkToMyArmForFight);
		prepareToFight.addStep(new Conditions(inStrongholdFloor2), goUpToRoofForFight);
		prepareToFight.addStep(new Conditions(inStrongholdFloor1), goUpFromF1ForFight);
		prepareToFight.addStep(new Conditions(inPrison), goUpFromPrisonForFight);

		steps.put(220, prepareToFight);
		steps.put(230, prepareToFight);

		ConditionalStep growGout = new ConditionalStep(this, giveRake);
		growGout.addStep(new Conditions(givenHardy, givenCompost), giveDibber);
		growGout.addStep(givenCompost, giveHardyGout);
		growGout.addStep(usedRake, giveSupercompost);
		growGout.addStep(hasRakeHeadAndHandle, repairRake);
		growGout.addStep(rakeHeadNearby, pickUpRakeHead);

		steps.put(240, growGout);

		ConditionalStep dealWithSmallBird = new ConditionalStep(this, talkToMyArmAfterGrow);
		dealWithSmallBird.addStep(babyNearby, killBabyRoc);

		steps.put(250, dealWithSmallBird);

		ConditionalStep dealWithBigBird = new ConditionalStep(this, talkToMyArmAfterBaby);
		dealWithBigBird.addStep(giantNearby, killGiantRoc);

		steps.put(260, dealWithBigBird);

		steps.put(270, giveSpade);

		steps.put(280, talkToMyArmAfterHarvest);

		ConditionalStep talkBurntForEnd = new ConditionalStep(this, goDownFromMyArmToBurntmeat);
		talkBurntForEnd.addStep(inStrongholdFloor1, talkToBurntmeatAgain);
		talkBurntForEnd.addStep(inStrongholdFloor2, goDownToBurntmeat);

		steps.put(290, talkBurntForEnd);
		steps.put(300, talkBurntForEnd);

		ConditionalStep talkArmForEnd = new ConditionalStep(this, enterStrongholdFinish);
		talkArmForEnd.addStep(onRoof, talkToMyArmFinish);
		talkArmForEnd.addStep(inStrongholdFloor1, goUpFromBurntmeatFinish);
		talkArmForEnd.addStep(inStrongholdFloor2, goUpToMyArmFinish);

		steps.put(310, talkArmForEnd);

		return steps;
	}

	public void setupItemRequirements()
	{
		goutLump = new ItemRequirement("Goutweedy lump", ItemID.GOUTWEEDY_LUMP);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight.setHighlightInInventory(true);
		farmingManual = new ItemRequirement("Farming manual", ItemID.FARMING_MANUAL);
		farmingManual.setTooltip("You can get another from My Arm on the Troll Stronghold roof");
		farmingManual.setHighlightInInventory(true);
		ugthanki3 = new ItemRequirement("Ugthanki dung", ItemID.UGTHANKI_DUNG, 3);
		ugthanki3.setHighlightInInventory(true);

		rake = new ItemRequirement("Rake", ItemID.RAKE);
		dibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		superCompost = new ItemRequirement("Supercompost", ItemID.SUPERCOMPOST);
		hardyGout = new ItemRequirement("Hardy gout tubers", ItemID.HARDY_GOUT_TUBERS);
		hardyGout.setTooltip("You can get more from Murcaily");
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.getPrayerPotions(), -1);

		rakeHighlight = new ItemRequirement("Rake", ItemID.RAKE);
		rakeHighlight.setHighlightInInventory(true);
		dibberHighlight = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);
		dibberHighlight.setHighlightInInventory(true);
		spadeHighlight = new ItemRequirement("Spade", ItemID.SPADE);
		spadeHighlight.setHighlightInInventory(true);
		superCompostHighlight = new ItemRequirement("Supercompost", ItemID.SUPERCOMPOST);
		superCompostHighlight.setHighlightInInventory(true);
		hardyGoutHighlight = new ItemRequirement("Hardy gout tubers", ItemID.HARDY_GOUT_TUBERS);
		hardyGoutHighlight.setTooltip("You can get more from Murcaily");
		hardyGoutHighlight.setHighlightInInventory(true);
		plantCureHighlight = new ItemRequirement("Plant cure", ItemID.PLANT_CURE);
		plantCureHighlight.setHighlightInInventory(true);

		supercompost7 = new ItemRequirement("Supercompost", ItemID.SUPERCOMPOST, 7);
		superCompost8 = new ItemRequirement("Supercompost", ItemID.SUPERCOMPOST, 8);
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS);

		cureOrCompost = new ItemRequirement("Either super/ultra compost, or a plant cure", ItemID.PLANT_CURE);
		cureOrCompost.addAlternates(ItemID.SUPERCOMPOST, ItemID.ULTRACOMPOST);

		rakeHead = new ItemRequirement("Rake head", ItemID.RAKE_HEAD);
		rakeHead.setHighlightInInventory(true);
		rakeHandle = new ItemRequirement("Rake handle", ItemID.RAKE_HANDLE);
		rakeHandle.setHighlightInInventory(true);

		gamesNecklace = new ItemRequirement("Games necklace for Burthorpe teleport",
			ItemCollections.getGamesNecklaces());
	}

	public void loadZones()
	{
		strongholdFloor1 = new Zone(new WorldPoint(2820, 10048, 1), new WorldPoint(2862, 10110, 1));
		strongholdFloor2 = new Zone(new WorldPoint(2820, 10048, 2), new WorldPoint(2862, 10110, 2));
		prison = new Zone(new WorldPoint(2822, 10049, 0), new WorldPoint(2859, 10110, 0));
		roof = new Zone(new WorldPoint(2822, 3665, 0), new WorldPoint(2838, 3701, 0));
	}

	public void setupConditions()
	{
		inStrongholdFloor1 = new ZoneRequirement(strongholdFloor1);
		inStrongholdFloor2 = new ZoneRequirement(strongholdFloor2);
		inPrison = new ZoneRequirement(prison);
		onRoof = new ZoneRequirement(roof);

		added3Dung = new VarbitRequirement(2791, 3);
		added7Comp = new VarbitRequirement(2792, 7);

		givenHardy = new VarbitRequirement(2794, 1);
		usedRake = new VarbitRequirement(2799, 6);
		givenCompost = new VarbitRequirement(2799, 7);

		givenDibber = new VarbitRequirement(2799, 9, Operation.GREATER_EQUAL);
		givenCure = new VarbitRequirement(2798, 1);

		hasRakeHeadAndHandle = new Conditions(rakeHead, rakeHandle);
		rakeHeadNearby = new ItemOnTileRequirement(rakeHead);

		babyNearby = new NpcCondition(NpcID.BABY_ROC);
		giantNearby = new NpcCondition(NpcID.GIANT_ROC);
	}

	public void setupSteps()
	{
		enterStronghold = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Enter the Troll Stronghold.");

		goDownToChef = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Go down the south staircase.");
		goDownToChef.setWorldMapPoint(new WorldPoint(2971, 10115, 1));

		goUpToChef = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Go up the stairs from the prison.");
		goUpToChef.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToBurntmeat = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1), "Talk to Burntmeat in the Troll Stronghold.");
		talkToBurntmeat.setWorldMapPoint(new WorldPoint(2911, 10087, 1));
		talkToBurntmeat.addSubSteps(enterStronghold, goDownToChef, goUpToChef);
		talkToBurntmeat.addDialogStep("What do you want now?");
		talkToMyArm = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2855, 10053, 1), "Talk to My Arm near Burntmeat.");
		talkToMyArm.setWorldMapPoint(new WorldPoint(2919, 10084, 1));
		talkToMyArm.addDialogStep("Alright, I'll lend him a hand.");

		useBucketOnPot = new ObjectStep(this, ObjectID.COOKING_POT, new WorldPoint(2864, 3591, 0),
			"Use a bucket on the cooking pot on the Death Plateau. You can find a bucket next to the pot.", bucketHighlight);
		useBucketOnPot.addIcon(ItemID.BUCKET);

		enterStrongholdWithLump = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Return to My Arm with the goutweedy lump.", goutLump);

		goDownToArmWithLump = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Return to My Arm with the goutweedy lump.", goutLump);
		goDownToArmWithLump.setWorldMapPoint(new WorldPoint(2971, 10115, 1));

		goUpToArmWithLump = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Return to My Arm with the goutweedy lump.", goutLump);
		goUpToArmWithLump.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToArmWithLump = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2855, 10053, 1), "Return to My Arm with the goutweedy lump.", goutLump);
		talkToArmWithLump.setWorldMapPoint(new WorldPoint(2919, 10084, 1));
		talkToArmWithLump.addSubSteps(goUpToArmWithLump, goDownToArmWithLump, enterStrongholdWithLump);

		enterStrongholdAfterLump = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Return to My Arm.");

		goDownAfterLump = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Return to My Arm.");
		goDownAfterLump.setWorldMapPoint(new WorldPoint(2971, 10115, 1));

		goUpAfterLump = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Return to My Arm.");
		goUpAfterLump.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToArmAfterLump = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2855, 10053, 1), "Return to My Arm.");
		talkToArmAfterLump.setWorldMapPoint(new WorldPoint(2919, 10084, 1));

		talkToArmWithLump.addSubSteps(goUpToArmWithLump, goDownToArmWithLump, enterStrongholdWithLump, goUpAfterLump, goDownAfterLump, enterStrongholdAfterLump, talkToArmAfterLump);

		goUpFromF1ToMyArm = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10052, 1), "Go up to the roof of the Stronghold and talk to My Arm.");
		goUpFromF1ToMyArm.setWorldMapPoint(new WorldPoint(2907, 10083, 1));
		goUpToMyArm = new ObjectStep(this, ObjectID.TROLL_LADDER_18834, new WorldPoint(2831, 10077, 2), "Climb back up to My Arm.");
		goUpToMyArm.setWorldMapPoint(new WorldPoint(2959, 10140, 0));
		talkToMyArmUpstairs = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2835, 3694, 0), "Talk to My Arm on the roof of the Troll Stronghold.");
		talkToMyArmUpstairs.addSubSteps(goUpFromF1ToMyArm, goUpToMyArm);

		readBook = new DetailedQuestStep(this, "Read the farming manual.", farmingManual);
		talkToMyArmAfterReading = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2835, 3694, 0), "Talk to My Arm on the roof of the Troll Stronghold.");
		useUgthankiDung = new AddDung(this);
		useCompost = new AddCompost(this);

		talkToMyArmAfterFertilising = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2835, 3694, 0), "Talk to My Arm on the roof of the Troll Stronghold.");

		talkToBarnaby = new NpcStep(this, NpcID.CAPTAIN_BARNABY, new WorldPoint(2683, 3275, 0), "Talk to Captain Barnaby on Ardougne dock.");
		talkToBarnaby.addDialogStep("This is My Arm. We'd like to go to Karamja.");

		talkAfterBoat = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2772, 3223, 0), "Talk to My Arm on Brimhaven dock.");

		talkToMyArmAtTai = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2781, 3123, 0), "Talk to My Arm east of the Tai Bwo Wannai general store.");

		talkToMurcaily = new NpcStep(this, NpcID.MURCAILY_6430, new WorldPoint(2815, 3083, 0), "Talk to Murcaily in east Tai Bwo Wannai.");
		talkToMurcaily.addDialogSteps("A troll called My Arm wants a favour...", "I was asking you about goutweed...", "I was asking you about hardy goutweed...");
		talkToMyArmAfterMurcaily = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2781, 3123, 0), "Talk to My Arm again east of the Tai Bwo Wannai general store.");

		enterStrongholdForFight = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0),
			"Return to My Arm on the roof. Be prepared to fight a baby and giant Roc.", combatGear, rake, superCompost, hardyGout, dibber, spade);
		goUpToRoofForFight = new ObjectStep(this, ObjectID.TROLL_LADDER_18834, new WorldPoint(2831, 10077, 2), "Climb back up to My Arm.");
		goUpToRoofForFight.setWorldMapPoint(new WorldPoint(2959, 10140, 0));
		goUpFromF1ForFight = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10052, 1), "Go up to the roof of the Stronghold and talk to My Arm.");
		goUpFromF1ForFight.setWorldMapPoint(new WorldPoint(2907, 10083, 1));
		goUpFromPrisonForFight = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Return to My Arm, ready to fight.");
		goUpFromPrisonForFight.setWorldMapPoint(new WorldPoint(2853, 10106, 1));
		talkToMyArmForFight = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Talk to My Arm on the roof of the Troll Stronghold.");
		talkToMyArmForFight.addSubSteps(enterStrongholdForFight, goUpToRoofForFight, goUpFromF1ForFight, goUpFromPrisonForFight);

		giveRake = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Give My Arm a rake.", rakeHighlight);
		giveRake.addIcon(ItemID.RAKE);
		pickUpRakeHead = new ItemStep(this, "Pick up the rake head and repair the rake.", rakeHead);
		repairRake = new DetailedQuestStep(this, "Repair the rake.", rakeHead, rakeHandle);
		giveRake.addSubSteps(pickUpRakeHead, repairRake);
		giveSupercompost = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Give My Arm some supercompost.", superCompostHighlight);
		giveSupercompost.addIcon(ItemID.SUPERCOMPOST);
		giveHardyGout = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Give My Arm some hardy gout tubers.", hardyGoutHighlight);
		giveHardyGout.addIcon(ItemID.HARDY_GOUT_TUBERS);
		giveDibber = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Give My Arm a seed dibber.", dibberHighlight);
		giveDibber.addIcon(ItemID.SEED_DIBBER);
		giveCure = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Give My Arm some plant cure.", plantCureHighlight);
		giveCure.addIcon(ItemID.PLANT_CURE);

		talkToMyArmAfterGrow = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0),
			"Talk to My Arm. Be prepared to fight a baby and giant Roc.");
		killBabyRoc = new NpcStep(this, NpcID.BABY_ROC, "Kill the Baby Roc.");
		talkToMyArmAfterBaby = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0),
			"Talk to My Arm. Be prepared to fight the Giant Roc.");

		killGiantRoc = new NpcStep(this, NpcID.GIANT_ROC, "Kill the Giant Roc.");
		talkToMyArmAfterHarvest = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Talk to My Arm.");
		giveSpade = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Give My Arm a spade.", spadeHighlight);
		giveSpade.addIcon(ItemID.SPADE);
		goDownFromMyArmToBurntmeat = new ObjectStep(this, ObjectID.TROLL_LADDER, new WorldPoint(2831, 3677, 0), "Go talk to Burntmeat.");

		goDownToBurntmeat = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Go talk to Burntmeat.");
		goDownToBurntmeat.setWorldMapPoint(new WorldPoint(2971, 10115, 1));

		talkToBurntmeatAgain = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1),
			"Talk to Burntmeat in the Troll Stronghold.");
		talkToBurntmeatAgain.setWorldMapPoint(new WorldPoint(2911, 10087, 1));
		talkToBurntmeatAgain.addSubSteps(goDownFromMyArmToBurntmeat, goDownToBurntmeat);

		enterStrongholdFinish = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0),
			"Talk to My Arm to finish the quest.");
		goUpToMyArmFinish = new ObjectStep(this, ObjectID.TROLL_LADDER_18834, new WorldPoint(2831, 10077, 2), "Climb back up to My Arm.");
		goUpToMyArmFinish.setWorldMapPoint(new WorldPoint(2959, 10140, 0));
		goUpFromBurntmeatFinish = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10052, 1), "Go up to the roof of the Stronghold and talk to My Arm.");
		goUpFromBurntmeatFinish.setWorldMapPoint(new WorldPoint(2907, 10083, 1));

		talkToMyArmFinish = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2829, 3695, 0), "Talk to My Arm to finish the quest.");
		talkToMyArmFinish.addSubSteps(goUpToMyArmFinish, goUpFromBurntmeatFinish, enterStrongholdFinish);

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(climbingBoots, ugthanki3, superCompost8, rake, dibber, spade, bucket);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food, prayerPotions, gamesNecklace);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Baby Roc (level 75)");
		reqs.add("Giant Roc (level 172)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.EADGARS_RUSE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_FEUD, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.JUNGLE_POTION, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 10));
		req.add(new SkillRequirement(Skill.FARMING, 29, true));
		// 907 is the Varbit for tai bwo wannai cleanup favour
		req.add(new VarbitRequirement(907, Operation.GREATER_EQUAL, 60, "At least 60% favor in the Tai Bwo Wannai Cleanup minigame"));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.HERBLORE, 10000),
				new ExperienceReward(Skill.FARMING, 5000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to a disease-free herb patch on top of the Troll Stronghold."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToBurntmeat, talkToMyArm), climbingBoots));
		allSteps.add(new PanelDetails("Preparing to grow", Arrays.asList(useBucketOnPot, talkToArmWithLump,
			talkToMyArmUpstairs, readBook, talkToMyArmAfterReading, useUgthankiDung, useCompost, talkToMyArmAfterFertilising),
			climbingBoots, bucket, spade, supercompost7, ugthanki3));
		allSteps.add(new PanelDetails("Karamja adventure", Arrays.asList(talkToBarnaby, talkAfterBoat, talkToMyArmAtTai, talkToMurcaily, talkToMyArmAfterMurcaily)));
		allSteps.add(new PanelDetails("Troll farming", Arrays.asList(talkToMyArmForFight, giveRake, giveSupercompost, giveHardyGout,
			giveDibber, talkToMyArmAfterGrow, killBabyRoc, killGiantRoc, giveSpade, talkToMyArmAfterHarvest, talkToBurntmeatAgain,
			talkToMyArmFinish), combatGear, rake, superCompost, dibber, spade, hardyGout));
		return allSteps;
	}
}
