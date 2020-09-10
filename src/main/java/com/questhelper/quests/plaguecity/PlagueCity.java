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
package com.questhelper.quests.plaguecity;


import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.ObjectCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.PLAGUE_CITY
)
public class PlagueCity extends BasicQuestHelper
{
	ItemRequirement spade, dwellberries, rope, fourBucketsOfWater, threeBucketsOfWater, twoBucketsOfWater, bucketOfWater, bucketOfMilk,
		chocolateDust, snapeGrass, pictureOfElena, gasMask, book, bucketOfChocolateMilk, hangoverCure, warrant, key;

	ConditionForStep inUnderground, hasPictureOfElena, hasTriedToPullGrill, inWestArdougne, hasBook, inUpstairsMathasHouse, hasBucketOfChocolateMilk, hasHangoverCure,
	hasWarrant, inPlagueHouse, inDownstairsOfPlagueHouse, hasKey, manholeClosed;

	QuestStep talkToEdmond, talkToAlrena, talkToEdmondAgain, useWaterOnMudPatch1, useWaterOnMudPatch2, useWaterOnMudPatch3, useWaterOnMudPatch4,
		digHole, grabPictureOfElena, goDownHole, attemptToPullGrill, climbMudPile, talkToEdmondUnderground, useRopeOnGrill, climbThroughPipe, talkToJethick,
		enterMarthasHouse, talkToMartha, talkToMilli, goUpstairsInMarthasHouse, tryToEnterPlagueHouse, talkToClerk, talkToBravek, useDustOnMilk,
		useSnapeGrassOnChocolateMilk, giveHangoverCureToBravek, talkToBravekAgain, tryToEnterPlagueHouseAgain, searchBarrel, goDownstairsInPlagueHouse,
		goUpstairsInPlagueHouse, talkToElena, goUpstairsInPlagueHouseToFinish, goDownManhole, goDownManhole2, climbMudPileToFinish, talkToEdmondToFinish;

	Zone underground, westArdougne1, westArdougne2, westArdougne3, upstairsMathasHouse, plagueHouse1, plagueHouse2, downstairsOfPlagueHouse;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToEdmond);
		steps.put(1, talkToAlrena);
		steps.put(2, talkToEdmondAgain);
		steps.put(3, useWaterOnMudPatch1);
		steps.put(4, useWaterOnMudPatch2);
		steps.put(5, useWaterOnMudPatch3);
		steps.put(6, useWaterOnMudPatch4);
		steps.put(7, digHole);
		ConditionalStep attemptToOpenGrill = new ConditionalStep(this, grabPictureOfElena);
		attemptToOpenGrill.addStep(new Conditions(hasTriedToPullGrill, inUnderground, hasPictureOfElena), useRopeOnGrill);
		attemptToOpenGrill.addStep(new Conditions(inUnderground, hasPictureOfElena), attemptToPullGrill);
		attemptToOpenGrill.addStep(inUnderground, climbMudPile);
		attemptToOpenGrill.addStep(hasPictureOfElena, goDownHole);

		steps.put(8, attemptToOpenGrill);

		ConditionalStep pullOffGrill = new ConditionalStep(this, grabPictureOfElena);
		pullOffGrill.addStep(new Conditions(inUnderground, hasPictureOfElena), talkToEdmondUnderground);
		pullOffGrill.addStep(inUnderground, climbMudPile);
		pullOffGrill.addStep(hasPictureOfElena, goDownHole);

		steps.put(9, pullOffGrill);

		ConditionalStep enterWestArdougne = new ConditionalStep(this, grabPictureOfElena);
		enterWestArdougne.addStep(new Conditions(inWestArdougne, hasPictureOfElena), talkToJethick);
		enterWestArdougne.addStep(new Conditions(inUnderground, hasPictureOfElena), climbThroughPipe);
		enterWestArdougne.addStep(inUnderground, climbMudPile);
		enterWestArdougne.addStep(hasPictureOfElena, goDownHole);

		steps.put(10, enterWestArdougne);

		ConditionalStep goToMarthasHouse = new ConditionalStep(this, goDownHole);
		goToMarthasHouse.addStep(new Conditions(inWestArdougne, hasBook), enterMarthasHouse);
		goToMarthasHouse.addStep(inWestArdougne, talkToJethick);
		goToMarthasHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(20, goToMarthasHouse);

		ConditionalStep talkToMarthaInHouse = new ConditionalStep(this, goDownHole);
		talkToMarthaInHouse.addStep(inWestArdougne, talkToMartha);
		talkToMarthaInHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(21, talkToMarthaInHouse);

		ConditionalStep talkToMilliInHouse = new ConditionalStep(this, goDownHole);
		talkToMilliInHouse.addStep(inUpstairsMathasHouse, talkToMilli);
		talkToMilliInHouse.addStep(inWestArdougne, goUpstairsInMarthasHouse);
		talkToMilliInHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(22, talkToMilliInHouse);

		ConditionalStep goToPlagueHouse = new ConditionalStep(this, goDownHole);
		goToPlagueHouse.addStep(inWestArdougne, tryToEnterPlagueHouse);
		goToPlagueHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(23, goToPlagueHouse);

		ConditionalStep goSpeakToClerk = new ConditionalStep(this, goDownHole);
		goSpeakToClerk.addStep(inWestArdougne, talkToClerk);
		goSpeakToClerk.addStep(inUnderground, climbThroughPipe);

		steps.put(24, goSpeakToClerk);

		ConditionalStep goTalkToBravek = new ConditionalStep(this, goDownHole);
		goTalkToBravek.addStep(inWestArdougne, talkToBravek);
		goTalkToBravek.addStep(inUnderground, climbThroughPipe);

		steps.put(25, goTalkToBravek);

		ConditionalStep createHangoverCureForBravek = new ConditionalStep(this, useDustOnMilk);
		createHangoverCureForBravek.addStep(new Conditions(inWestArdougne, hasHangoverCure), giveHangoverCureToBravek);
		createHangoverCureForBravek.addStep(new Conditions(hasHangoverCure, inUnderground), climbThroughPipe);
		createHangoverCureForBravek.addStep(hasHangoverCure, goDownHole);
		createHangoverCureForBravek.addStep(hasBucketOfChocolateMilk, useSnapeGrassOnChocolateMilk);

		steps.put(26, createHangoverCureForBravek);

		ConditionalStep continueTalkingToBravek = new ConditionalStep(this, goDownHole);
		continueTalkingToBravek.addStep(new Conditions(inDownstairsOfPlagueHouse, hasKey), talkToElena);
		continueTalkingToBravek.addStep(new Conditions(inPlagueHouse, hasKey), goDownstairsInPlagueHouse);
		continueTalkingToBravek.addStep(inPlagueHouse, searchBarrel);
		continueTalkingToBravek.addStep(inDownstairsOfPlagueHouse, goUpstairsInPlagueHouse);
		continueTalkingToBravek.addStep(new Conditions(hasWarrant, inWestArdougne), tryToEnterPlagueHouseAgain);
		continueTalkingToBravek.addStep(inWestArdougne, talkToBravekAgain);
		continueTalkingToBravek.addStep(inUnderground, climbThroughPipe);

		steps.put(27, continueTalkingToBravek);

		ConditionalStep finishQuest = new ConditionalStep(this, talkToEdmondToFinish);
		finishQuest.addStep(inDownstairsOfPlagueHouse, goUpstairsInPlagueHouseToFinish);
		finishQuest.addStep(new Conditions(inWestArdougne, manholeClosed), goDownManhole2);
		finishQuest.addStep(inWestArdougne, goDownManhole);
		finishQuest.addStep(inUnderground, climbMudPileToFinish);

		steps.put(28, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		dwellberries = new ItemRequirement("Dwellberries", ItemID.DWELLBERRIES);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		fourBucketsOfWater = new ItemRequirement("Buckets of water", ItemID.BUCKET_OF_WATER, 4);
		fourBucketsOfWater.setTip("You can use the nearby bucket on the sink nearby");
		threeBucketsOfWater = new ItemRequirement("Buckets of water", ItemID.BUCKET_OF_WATER, 3);
		threeBucketsOfWater.setTip("You can use the nearby bucket on the sink nearby");
		twoBucketsOfWater = new ItemRequirement("Buckets of water", ItemID.BUCKET_OF_WATER, 2);
		twoBucketsOfWater.setTip("You can use the nearby bucket on the sink nearby");
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.addAlternates(ItemID.BUCKET);
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		chocolateDust = new ItemRequirement("Chocolate dust", ItemID.CHOCOLATE_DUST);
		snapeGrass = new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS);
		pictureOfElena = new ItemRequirement("Picture", ItemID.PICTURE);
		gasMask = new ItemRequirement("Gas mask", ItemID.GAS_MASK, 1, true);
		gasMask.setTip("You can get another from the cupboard in Edmond's house.");
		book = new ItemRequirement("Book", ItemID.BOOK_1509);
		bucketOfChocolateMilk = new ItemRequirement("Chocolatey milk", ItemID.CHOCOLATEY_MILK);
		hangoverCure = new ItemRequirement("Hangover cure", ItemID.HANGOVER_CURE);
		warrant = new ItemRequirement("Warrant", ItemID.WARRANT);
		inPlagueHouse = new ZoneCondition(plagueHouse1, plagueHouse2);
		inDownstairsOfPlagueHouse = new ZoneCondition(downstairsOfPlagueHouse);
		key = new ItemRequirement("A small key", ItemID.A_SMALL_KEY);
	}

	public void loadZones() {
		underground = new Zone(new WorldPoint(2506,9737,0), new WorldPoint(2532,9781,0));
		westArdougne1 = new Zone(new WorldPoint(2460,3279,0), new WorldPoint(2556, 3334,2));
		westArdougne2 = new Zone(new WorldPoint(2434,3305,0), new WorldPoint(2464, 3323,2));
		westArdougne3 = new Zone(new WorldPoint(2510,3265,0), new WorldPoint(2556, 3280,2));
		upstairsMathasHouse = new Zone(new WorldPoint(2527,3329,1), new WorldPoint(2533, 3333,1));
		plagueHouse1 = new Zone(new WorldPoint(2532,3268,0), new WorldPoint(2541, 3271,0));
		plagueHouse2 = new Zone(new WorldPoint(2535,3272,0), new WorldPoint(2541, 3272,0));
		downstairsOfPlagueHouse = new Zone(new WorldPoint(2535, 9670,0), new WorldPoint(2542, 9673,0));
	}

	public void setupConditions() {
		inUnderground = new ZoneCondition(underground);
		hasPictureOfElena = new ItemRequirementCondition(pictureOfElena);
		hasTriedToPullGrill = new VarbitCondition(1786, 1);
		inWestArdougne = new ZoneCondition(westArdougne1, westArdougne2, westArdougne3);
		hasBook = new ItemRequirementCondition(book);
		inUpstairsMathasHouse = new ZoneCondition(upstairsMathasHouse);
		hasBucketOfChocolateMilk = new ItemRequirementCondition(bucketOfChocolateMilk);
		hasHangoverCure = new ItemRequirementCondition(hangoverCure);
		hasWarrant = new ItemRequirementCondition(warrant);
		hasKey = new ItemRequirementCondition(key);
		manholeClosed = new ObjectCondition(ObjectID.MANHOLE_2543);
	}

	public void setupSteps()
	{
		talkToEdmond = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2568, 3333, 0), "Talk to Edmond in the north-west corner of East Ardougne.");
		talkToEdmond.addDialogStep("What's happened to her?");
		talkToEdmond.addDialogStep("Yes.");
		talkToAlrena = new NpcStep(this, NpcID.ALRENA, new WorldPoint(2573, 3333, 0), "Talk to Alrena nearby.", dwellberries);
		talkToEdmondAgain = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2568, 3332, 0), "Talk to Edmond again.");
		useWaterOnMudPatch1 = new ObjectStep(this, NullObjectID.NULL_2532, new WorldPoint(2566, 3332, 0),
			"Use four buckets of water on the mud patch in Edmond's garden patch.", fourBucketsOfWater);
		useWaterOnMudPatch1.addIcon(ItemID.BUCKET_OF_WATER);
		useWaterOnMudPatch2 = new ObjectStep(this, NullObjectID.NULL_2532, new WorldPoint(2566, 3332, 0),
			"Use three more buckets of water on the mud patch in Edmond's garden patch.", threeBucketsOfWater);
		useWaterOnMudPatch2.addIcon(ItemID.BUCKET_OF_WATER);
		useWaterOnMudPatch3 = new ObjectStep(this, NullObjectID.NULL_2532, new WorldPoint(2566, 3332, 0),
			"Use two more buckets of water on the mud patch in Edmond's garden patch.", twoBucketsOfWater);
		useWaterOnMudPatch3.addIcon(ItemID.BUCKET_OF_WATER);
		useWaterOnMudPatch4 = new ObjectStep(this, NullObjectID.NULL_2532, new WorldPoint(2566, 3332, 0),
			"Use one more bucket of water on the mud patch in Edmond's garden patch.", bucketOfWater);
		useWaterOnMudPatch4.addIcon(ItemID.BUCKET_OF_WATER);

		useWaterOnMudPatch1.addSubSteps(useWaterOnMudPatch2, useWaterOnMudPatch3, useWaterOnMudPatch4);

		digHole = new ObjectStep(this, NullObjectID.NULL_2532, new WorldPoint(2566, 3332, 0),
			"Use a spade on the mud patch.", spade);
		digHole.addIcon(ItemID.SPADE);

		grabPictureOfElena = new DetailedQuestStep(this, new WorldPoint(2576, 3334, 0),
			"Grab the Picture from Edmond's house.", pictureOfElena);
		goDownHole = new ObjectStep(this, NullObjectID.NULL_2532, new WorldPoint(2566, 3332, 0),
			"Go down the hole.");

		attemptToPullGrill = new ObjectStep(this, NullObjectID.NULL_11422, new WorldPoint(2514,9739,0), "Attempt to pull the grill in the east of the sewer.");
		climbMudPile = new ObjectStep(this, ObjectID.MUD_PILE_2533, new WorldPoint(2519,9760,0), "Climb the mud pile.");

		grabPictureOfElena.addSubSteps(climbMudPile);

		useRopeOnGrill = new ObjectStep(this, NullObjectID.NULL_11422, new WorldPoint(2514,9739,0), "Use a rope on the grill.", rope);
		useRopeOnGrill.addIcon(ItemID.ROPE);

		talkToEdmondUnderground = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2517, 9753, 0), "Talk to Edmond.");

		climbThroughPipe = new ObjectStep(this, ObjectID.PIPE, new WorldPoint(2514, 9738, 0), "Climb through the pipe.", gasMask);

		talkToJethick = new NpcStep(this, NpcID.JETHICK_8974, new WorldPoint(2540, 3305, 0), "Talk to Jethick east of where you emerge.", gasMask, pictureOfElena);
		talkToJethick.addDialogStep("Yes, I'll return it for you.");

		enterMarthasHouse = new ObjectStep(this, ObjectID.DOOR_2537, new WorldPoint(2531, 3328, 0), "Enter the tall house in north West Ardougne.");

		talkToMartha = new NpcStep(this, NpcID.MARTHA_REHNISON, new WorldPoint(2531, 3331, 0), "Talk to Martha or Ted Renison");

		goUpstairsInMarthasHouse = new ObjectStep(this, ObjectID.STAIRS_2539, new WorldPoint(2528, 3333, 0), "Talk to Milli upstairs.");
		talkToMilli = new NpcStep(this, NpcID.MILLI_REHNISON, new WorldPoint(2531, 3331, 1), "Talk to Milli.");
		goUpstairsInMarthasHouse.addSubSteps(talkToMilli);

		tryToEnterPlagueHouse = new ObjectStep(this, NullObjectID.NULL_37321, new WorldPoint(2540, 3273, 0), "Try to enter the house in the south-east corner of West Ardougne.");
		tryToEnterPlagueHouse.addDialogStep("I fear not a mere plague.");
		tryToEnterPlagueHouse.addDialogStep("I want to check anyway.");

		talkToClerk = new NpcStep(this, NpcID.CLERK, new WorldPoint(2528, 3317, 0), "Talk to the Clerk in the large building north of the manhole.");
		talkToClerk.addDialogStep("I need permission to enter a plague house.");
		talkToClerk.addDialogStep("This is urgent though! Someone's been kidnapped!");

		talkToBravek = new NpcStep(this, NpcID.BRAVEK, new WorldPoint(2534, 3314, 0), "Talk to the Bravek in the room to the east.");

		talkToBravek.addDialogStep("This is really important though!");
		talkToBravek.addDialogStep("Do you know what's in the cure?");

		useDustOnMilk = new DetailedQuestStep(this, "Use your chocolate dust on the bucket of milk.", bucketOfMilk, chocolateDust);
		useSnapeGrassOnChocolateMilk = new DetailedQuestStep(this, "Use the snape grass on the chocolatey milk", bucketOfChocolateMilk, snapeGrass);

		giveHangoverCureToBravek = new NpcStep(this, NpcID.BRAVEK, new WorldPoint(2534, 3314, 0), "Talk to the Bravek again.", hangoverCure);

		talkToBravekAgain = new NpcStep(this, NpcID.BRAVEK, new WorldPoint(2534, 3314, 0), "Talk to the Bravek again.", warrant);
		talkToBravekAgain.addDialogStep("They won't listen to me!");

		giveHangoverCureToBravek.addSubSteps(talkToBravekAgain);

		tryToEnterPlagueHouseAgain = new ObjectStep(this, NullObjectID.NULL_37321, new WorldPoint(2540, 3273, 0), "Try to enter the plague house again.", warrant);

		searchBarrel = new ObjectStep(this, ObjectID.BARREL_2530, new WorldPoint(2534, 3268, 0), "Search the barrel in the room for a small key.");

		goDownstairsInPlagueHouse = new ObjectStep(this, ObjectID.SPOOKY_STAIRS, new WorldPoint(2537, 3269, 0), "Go downstairs.", key);
		goUpstairsInPlagueHouse = new ObjectStep(this, ObjectID.SPOOKY_STAIRS_2523, new WorldPoint(2537, 9672, 0), "Go back upstairs to get the key for Elena's cell.");
		searchBarrel.addSubSteps(goUpstairsInPlagueHouse);

		talkToElena = new NpcStep(this, NpcID.ELENA_4257, new WorldPoint(2541, 9671, 0), "Enter the jail and talk to Elena.", key);

		goUpstairsInPlagueHouseToFinish = new ObjectStep(this, ObjectID.SPOOKY_STAIRS_2523, new WorldPoint(2537, 9672, 0), "Go back upstairs and return to Edmond to finish the quest.");

		goDownManhole = new ObjectStep(this, ObjectID.MANHOLE_2544, new WorldPoint(2529, 3303, 0), "Go back down the manhole to return to Edmond.");
		goDownManhole2 = new ObjectStep(this, ObjectID.MANHOLE_2543, new WorldPoint(2529, 3303, 0), "Go back down the manhole to return to Edmond.");

		climbMudPileToFinish = new ObjectStep(this, ObjectID.MUD_PILE_2533, new WorldPoint(2519,9760,0), "Climb the mud pile to return to Edmond.");

		talkToEdmondToFinish = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2568, 3333, 0), "Return to Edmond to finish the quest.");
		talkToEdmondToFinish.addSubSteps(goUpstairsInPlagueHouseToFinish, goDownManhole, goDownManhole2, climbMudPileToFinish);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(dwellberries);
		reqs.add(spade);
		reqs.add(rope);
		reqs.add(bucketOfMilk);
		reqs.add(chocolateDust);
		reqs.add(snapeGrass);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest", new ArrayList<>(Arrays.asList(talkToEdmond)), dwellberries, spade, rope, bucketOfMilk, chocolateDust, snapeGrass));
		allSteps.add(new PanelDetails("Infiltrate West Ardougne", new ArrayList<>(Arrays.asList(talkToAlrena, talkToEdmondAgain, useWaterOnMudPatch1,
			grabPictureOfElena, digHole, goDownHole, attemptToPullGrill, useRopeOnGrill, talkToEdmondUnderground, climbThroughPipe))));
		allSteps.add(new PanelDetails("Discover Elena's location", new ArrayList<>(Arrays.asList(talkToJethick, enterMarthasHouse, talkToMartha,
			goUpstairsInMarthasHouse))));
		allSteps.add(new PanelDetails("Freeing Elena", new ArrayList<>(Arrays.asList(tryToEnterPlagueHouse, talkToClerk, talkToBravek, useDustOnMilk, useSnapeGrassOnChocolateMilk,
			giveHangoverCureToBravek, tryToEnterPlagueHouseAgain, searchBarrel, goDownstairsInPlagueHouse, talkToElena))));
		allSteps.add(new PanelDetails("Finishing off", new ArrayList<>(Arrays.asList(talkToEdmondToFinish))));
		return allSteps;
	}
}
