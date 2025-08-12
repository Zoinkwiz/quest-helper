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
package com.questhelper.helpers.quests.plaguecity;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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

public class PlagueCity extends BasicQuestHelper
{
	// Required items
	ItemRequirement spade;
	ItemRequirement dwellberries;
	ItemRequirement rope;
	ItemRequirement bucketOfMilk;
	ItemRequirement chocolateDust;
	ItemRequirement snapeGrass;

	// Recommended items
	ItemRequirement fourBucketsOfWater;
	ItemRequirement threeBucketsOfWater;
	ItemRequirement twoBucketsOfWater;
	ItemRequirement bucketOfWater;

	// Mid-quest requirements
	ItemRequirement pictureOfElena;
	ItemRequirement gasMask;
	ItemRequirement book;
	ItemRequirement bucketOfChocolateMilk;
	ItemRequirement hangoverCure;
	ItemRequirement warrant;
	ItemRequirement key;

	// Zones
	Zone underground;
	Zone westArdougne1;
	Zone westArdougne2;
	Zone westArdougne3;
	Zone upstairsMathasHouse;
	Zone plagueHouse1;
	Zone plagueHouse2;
	Zone downstairsOfPlagueHouse;

	// Miscellaneous requirements
	ZoneRequirement inUnderground;
	VarbitRequirement hasTriedToPullGrill;
	ZoneRequirement inWestArdougne;
	ZoneRequirement inUpstairsMathasHouse;
	ZoneRequirement inPlagueHouse;
	ZoneRequirement inDownstairsOfPlagueHouse;
	ObjectCondition manholeClosed;

	// Steps
	NpcStep talkToEdmond;
	NpcStep talkToAlrena;
	NpcStep talkToEdmondAgain;
	DetailedQuestStep useWaterOnMudPatch1;
	DetailedQuestStep useWaterOnMudPatch2;
	DetailedQuestStep useWaterOnMudPatch3;
	DetailedQuestStep useWaterOnMudPatch4;
	ObjectStep digHole;
	DetailedQuestStep grabPictureOfElena;
	ObjectStep goDownHole;
	ObjectStep attemptToPullGrill;
	ObjectStep climbMudPile;
	NpcStep talkToEdmondUnderground;
	ObjectStep useRopeOnGrill;
	ObjectStep climbThroughPipe;
	NpcStep talkToJethick;
	ObjectStep enterMarthasHouse;
	NpcStep talkToMartha;
	NpcStep talkToMilli;
	ObjectStep goUpstairsInMarthasHouse;
	ObjectStep tryToEnterPlagueHouse;
	NpcStep talkToClerk;
	NpcStep talkToBravek;
	DetailedQuestStep useDustOnMilk;
	DetailedQuestStep useSnapeGrassOnChocolateMilk;
	NpcStep giveHangoverCureToBravek;
	NpcStep talkToBravekAgain;
	ObjectStep tryToEnterPlagueHouseAgain;
	ObjectStep searchBarrel;
	ObjectStep goDownstairsInPlagueHouse;
	ObjectStep goUpstairsInPlagueHouse;
	NpcStep talkToElena;
	ObjectStep goUpstairsInPlagueHouseToFinish;
	ObjectStep goDownManhole;
	ObjectStep goDownManhole2;
	ObjectStep climbMudPileToFinish;
	NpcStep talkToEdmondToFinish;

	@Override
	protected void setupZones()
	{
		underground = new Zone(new WorldPoint(2506, 9737, 0), new WorldPoint(2532, 9781, 0));
		westArdougne1 = new Zone(new WorldPoint(2460, 3279, 0), new WorldPoint(2556, 3334, 2));
		westArdougne2 = new Zone(new WorldPoint(2434, 3305, 0), new WorldPoint(2464, 3323, 2));
		westArdougne3 = new Zone(new WorldPoint(2510, 3265, 0), new WorldPoint(2556, 3280, 2));
		upstairsMathasHouse = new Zone(new WorldPoint(2527, 3329, 1), new WorldPoint(2533, 3333, 1));
		plagueHouse1 = new Zone(new WorldPoint(2532, 3268, 0), new WorldPoint(2541, 3271, 0));
		plagueHouse2 = new Zone(new WorldPoint(2535, 3272, 0), new WorldPoint(2541, 3272, 0));
		downstairsOfPlagueHouse = new Zone(new WorldPoint(2535, 9670, 0), new WorldPoint(2542, 9673, 0));
	}

	@Override
	protected void setupRequirements()
	{
		inUnderground = new ZoneRequirement(underground);
		hasTriedToPullGrill = new VarbitRequirement(1786, 1);
		inWestArdougne = new ZoneRequirement(westArdougne1, westArdougne2, westArdougne3);
		inUpstairsMathasHouse = new ZoneRequirement(upstairsMathasHouse);
		manholeClosed = new ObjectCondition(ObjectID.PLAGUEMANHOLECLOSED);

		dwellberries = new ItemRequirement("Dwellberries", ItemID.DWELLBERRIES);
		dwellberries.setTooltip("You can get these from McGrubor's Wood west of Seers' Village");
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		spade.canBeObtainedDuringQuest();
		spade.setTooltip("A spawn is found in Edmond's garden at the start of the quest");
		spade.setHighlightInInventory(true);
		fourBucketsOfWater = new ItemRequirement("Buckets of water", ItemID.BUCKET_WATER, 4);
		fourBucketsOfWater.setHighlightInInventory(true);
		fourBucketsOfWater.setTooltip("You can use the bucket near the start of the quest on the sink nearby");
		threeBucketsOfWater = new ItemRequirement("Buckets of water", ItemID.BUCKET_WATER, 3);
		threeBucketsOfWater.setHighlightInInventory(true);
		threeBucketsOfWater.setTooltip("You can use the bucket near the start of the quest on the sink nearby");
		twoBucketsOfWater = new ItemRequirement("Buckets of water", ItemID.BUCKET_WATER, 2);
		twoBucketsOfWater.setHighlightInInventory(true);
		twoBucketsOfWater.setTooltip("You can use the bucket near the start of the quest on the sink nearby");
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_WATER);
		bucketOfWater.setHighlightInInventory(true);
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK);
		bucketOfMilk.setHighlightInInventory(true);
		chocolateDust = new ItemRequirement("Chocolate dust", ItemID.CHOCOLATE_DUST);
		chocolateDust.setHighlightInInventory(true);
		snapeGrass = new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS);
		snapeGrass.setHighlightInInventory(true);
		pictureOfElena = new ItemRequirement("Picture", ItemID.ELENA_PICTURE);
		gasMask = new ItemRequirement("Gas mask", ItemID.GASMASK, 1, true).isNotConsumed();
		gasMask.setTooltip("You can get another from the cupboard in Edmond's house.");
		book = new ItemRequirement("Book", ItemID.TURNIP_BOOK);
		bucketOfChocolateMilk = new ItemRequirement("Chocolatey milk", ItemID.CHOCOLATY_MILK);
		bucketOfChocolateMilk.setHighlightInInventory(true);
		hangoverCure = new ItemRequirement("Hangover cure", ItemID.HANGOVER_CURE);
		warrant = new ItemRequirement("Warrant", ItemID.WARRANT);
		inPlagueHouse = new ZoneRequirement(plagueHouse1, plagueHouse2);
		inDownstairsOfPlagueHouse = new ZoneRequirement(downstairsOfPlagueHouse);
		key = new ItemRequirement("A small key", ItemID.ELENAKEY);
	}

	public void setupSteps()
	{
		talkToEdmond = new NpcStep(this, NpcID.EDMOND, new WorldPoint(2568, 3333, 0), "Talk to Edmond in the north-west corner of East Ardougne.");
		talkToEdmond.addDialogStep("What's happened to her?");
		talkToEdmond.addDialogStep("Yes.");

		talkToAlrena = new NpcStep(this, NpcID.ALRENA, new WorldPoint(2573, 3333, 0), "Talk to Alrena and give her the dwellberries.", dwellberries);

		talkToEdmondAgain = new NpcStep(this, NpcID.EDMOND, new WorldPoint(2568, 3332, 0), "Talk to Edmond again.");

		useWaterOnMudPatch1 = new ObjectStep(this, ObjectID.PLAGUEMUDPATCH2, new WorldPoint(2566, 3332, 0),
			"Use four buckets of water on the mud patch in Edmond's garden patch.", fourBucketsOfWater);
		useWaterOnMudPatch1.addIcon(ItemID.BUCKET_WATER);
		useWaterOnMudPatch2 = new ObjectStep(this, ObjectID.PLAGUEMUDPATCH2, new WorldPoint(2566, 3332, 0),
			"Use three more buckets of water on the mud patch in Edmond's garden patch.", threeBucketsOfWater);
		useWaterOnMudPatch2.addIcon(ItemID.BUCKET_WATER);
		useWaterOnMudPatch3 = new ObjectStep(this, ObjectID.PLAGUEMUDPATCH2, new WorldPoint(2566, 3332, 0),
			"Use two more buckets of water on the mud patch in Edmond's garden patch.", twoBucketsOfWater);
		useWaterOnMudPatch3.addIcon(ItemID.BUCKET_WATER);
		useWaterOnMudPatch4 = new ObjectStep(this, ObjectID.PLAGUEMUDPATCH2, new WorldPoint(2566, 3332, 0),
			"Use one more bucket of water on the mud patch in Edmond's garden patch.", bucketOfWater);
		useWaterOnMudPatch4.addIcon(ItemID.BUCKET_WATER);

		useWaterOnMudPatch1.addSubSteps(useWaterOnMudPatch2, useWaterOnMudPatch3, useWaterOnMudPatch4);

		digHole = new ObjectStep(this, ObjectID.PLAGUEMUDPATCH2, new WorldPoint(2566, 3332, 0),
			"Use a spade on the mud patch.", spade);
		digHole.addIcon(ItemID.SPADE);

		grabPictureOfElena = new DetailedQuestStep(this, new WorldPoint(2576, 3334, 0),
			"Grab the Picture from Edmond's house.", pictureOfElena);
		goDownHole = new ObjectStep(this, ObjectID.PLAGUEMUDPATCH2, new WorldPoint(2566, 3332, 0),
			"Go down the hole.");

		attemptToPullGrill = new ObjectStep(this, ObjectID.PLAGUE_GRILL, new WorldPoint(2514, 9739, 0), "Attempt to pull the grill in the south of the sewer.");
		climbMudPile = new ObjectStep(this, ObjectID.PLAGUEMUDPILE, new WorldPoint(2519, 9760, 0), "Climb the mud pile.");

		grabPictureOfElena.addSubSteps(climbMudPile);

		useRopeOnGrill = new ObjectStep(this, ObjectID.PLAGUE_GRILL, new WorldPoint(2514, 9739, 0), "Use a rope on the grill.", rope);
		useRopeOnGrill.addIcon(ItemID.ROPE);

		talkToEdmondUnderground = new NpcStep(this, NpcID.EDMOND, new WorldPoint(2517, 9753, 0), "Talk to Edmond.");

		climbThroughPipe = new ObjectStep(this, ObjectID.PLAGUESEWERPIPE_OPEN, new WorldPoint(2514, 9738, 0), "Equip the gas mask and climb through the pipe.", gasMask.highlighted());

		talkToJethick = new NpcStep(this, NpcID.JETHICK_VIS, new WorldPoint(2540, 3305, 0), "Talk to Jethick east of where you emerge.", gasMask, pictureOfElena);
		talkToJethick.addDialogStep("Yes, I'll return it for you.");

		enterMarthasHouse = new ObjectStep(this, ObjectID.REHNISONDOORSHUT, new WorldPoint(2531, 3328, 0), "Enter the tall house in north West Ardougne.");

		talkToMartha = new NpcStep(this, NpcID.MARTHA_REHNISON, new WorldPoint(2531, 3331, 0), "Talk to Martha or Ted Rehnison");

		goUpstairsInMarthasHouse = new ObjectStep(this, ObjectID.REHNISONSTAIRS, new WorldPoint(2528, 3333, 0), "Talk to Milli upstairs.");
		talkToMilli = new NpcStep(this, NpcID.MILLI, new WorldPoint(2531, 3331, 1), "Talk to Milli.");
		goUpstairsInMarthasHouse.addSubSteps(talkToMilli);

		tryToEnterPlagueHouse = new ObjectStep(this, ObjectID.PLAGUEELENADOORSHUT, new WorldPoint(2540, 3273, 0), "Try to enter the house in the south-east corner of West Ardougne.");
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

		tryToEnterPlagueHouseAgain = new ObjectStep(this, ObjectID.PLAGUEELENADOORSHUT, new WorldPoint(2540, 3273, 0), "Try to enter the plague house again.", warrant);

		searchBarrel = new ObjectStep(this, ObjectID.PLAGUEKEYBARREL, new WorldPoint(2534, 3268, 0), "Search the barrel in the room for a small key.");

		goDownstairsInPlagueHouse = new ObjectStep(this, ObjectID.PLAGUEHOUSESTAIRSDOWN, new WorldPoint(2537, 3269, 0), "Go downstairs.", key);
		goUpstairsInPlagueHouse = new ObjectStep(this, ObjectID.PLAGUEHOUSESTAIRSUP, new WorldPoint(2537, 9672, 0), "Go back upstairs to get the key for Elena's cell.");
		searchBarrel.addSubSteps(goUpstairsInPlagueHouse);

		talkToElena = new NpcStep(this, NpcID.ELENAP_VIS, new WorldPoint(2541, 9671, 0), "Enter the jail and talk to Elena.", key);

		goUpstairsInPlagueHouseToFinish = new ObjectStep(this, ObjectID.PLAGUEHOUSESTAIRSUP, new WorldPoint(2537, 9672, 0), "Go back upstairs and return to Edmond to finish the quest.");

		goDownManhole = new ObjectStep(this, ObjectID.PLAGUEMANHOLEOPEN, new WorldPoint(2529, 3303, 0), "Go back down the manhole to return to Edmond.");
		goDownManhole2 = new ObjectStep(this, ObjectID.PLAGUEMANHOLECLOSED, new WorldPoint(2529, 3303, 0), "Go back down the manhole to return to Edmond.");

		climbMudPileToFinish = new ObjectStep(this, ObjectID.PLAGUEMUDPILE, new WorldPoint(2519, 9760, 0), "Climb the mud pile to return to Edmond.");

		talkToEdmondToFinish = new NpcStep(this, NpcID.EDMOND, new WorldPoint(2568, 3333, 0), "Return to Edmond to finish the quest.");
		talkToEdmondToFinish.addSubSteps(goUpstairsInPlagueHouseToFinish, goDownManhole, goDownManhole2, climbMudPileToFinish);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToEdmond);
		steps.put(1, talkToAlrena);

		var getPictureThenTalkEdmond = new ConditionalStep(this, grabPictureOfElena);
		getPictureThenTalkEdmond.addStep(pictureOfElena, talkToEdmondAgain);
		steps.put(2, getPictureThenTalkEdmond);

		steps.put(3, useWaterOnMudPatch1);
		steps.put(4, useWaterOnMudPatch2);
		steps.put(5, useWaterOnMudPatch3);
		steps.put(6, useWaterOnMudPatch4);

		var getPictureThenDig = new ConditionalStep(this, grabPictureOfElena);
		getPictureThenDig.addStep(pictureOfElena, digHole);
		steps.put(7, getPictureThenDig);

		var attemptToOpenGrill = new ConditionalStep(this, grabPictureOfElena);
		attemptToOpenGrill.addStep(and(hasTriedToPullGrill, inUnderground, pictureOfElena), useRopeOnGrill);
		attemptToOpenGrill.addStep(and(inUnderground, pictureOfElena), attemptToPullGrill);
		attemptToOpenGrill.addStep(inUnderground, climbMudPile);
		attemptToOpenGrill.addStep(pictureOfElena, goDownHole);

		steps.put(8, attemptToOpenGrill);

		var pullOffGrill = new ConditionalStep(this, grabPictureOfElena);
		pullOffGrill.addStep(and(inUnderground, pictureOfElena), talkToEdmondUnderground);
		pullOffGrill.addStep(inUnderground, climbMudPile);
		pullOffGrill.addStep(pictureOfElena, goDownHole);

		steps.put(9, pullOffGrill);

		var enterWestArdougne = new ConditionalStep(this, grabPictureOfElena);
		enterWestArdougne.addStep(and(inWestArdougne, pictureOfElena), talkToJethick);
		enterWestArdougne.addStep(and(inUnderground, pictureOfElena), climbThroughPipe);
		enterWestArdougne.addStep(inUnderground, climbMudPile);
		enterWestArdougne.addStep(pictureOfElena, goDownHole);

		steps.put(10, enterWestArdougne);

		var goToMarthasHouse = new ConditionalStep(this, goDownHole);
		goToMarthasHouse.addStep(and(inWestArdougne, book), enterMarthasHouse);
		goToMarthasHouse.addStep(inWestArdougne, talkToJethick);
		goToMarthasHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(20, goToMarthasHouse);

		var talkToMarthaInHouse = new ConditionalStep(this, goDownHole);
		talkToMarthaInHouse.addStep(inWestArdougne, talkToMartha);
		talkToMarthaInHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(21, talkToMarthaInHouse);

		var talkToMilliInHouse = new ConditionalStep(this, goDownHole);
		talkToMilliInHouse.addStep(inUpstairsMathasHouse, talkToMilli);
		talkToMilliInHouse.addStep(inWestArdougne, goUpstairsInMarthasHouse);
		talkToMilliInHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(22, talkToMilliInHouse);

		var goToPlagueHouse = new ConditionalStep(this, goDownHole);
		goToPlagueHouse.addStep(inWestArdougne, tryToEnterPlagueHouse);
		goToPlagueHouse.addStep(inUnderground, climbThroughPipe);

		steps.put(23, goToPlagueHouse);

		var goSpeakToClerk = new ConditionalStep(this, goDownHole);
		goSpeakToClerk.addStep(inWestArdougne, talkToClerk);
		goSpeakToClerk.addStep(inUnderground, climbThroughPipe);

		steps.put(24, goSpeakToClerk);

		var goTalkToBravek = new ConditionalStep(this, goDownHole);
		goTalkToBravek.addStep(inWestArdougne, talkToBravek);
		goTalkToBravek.addStep(inUnderground, climbThroughPipe);

		steps.put(25, goTalkToBravek);

		var createHangoverCureForBravek = new ConditionalStep(this, useDustOnMilk);
		createHangoverCureForBravek.addStep(and(inWestArdougne, hangoverCure), giveHangoverCureToBravek);
		createHangoverCureForBravek.addStep(and(hangoverCure, inUnderground), climbThroughPipe);
		createHangoverCureForBravek.addStep(hangoverCure, goDownHole);
		createHangoverCureForBravek.addStep(bucketOfChocolateMilk, useSnapeGrassOnChocolateMilk);

		steps.put(26, createHangoverCureForBravek);

		var continueTalkingToBravek = new ConditionalStep(this, goDownHole);
		continueTalkingToBravek.addStep(and(inDownstairsOfPlagueHouse, key), talkToElena);
		continueTalkingToBravek.addStep(and(inPlagueHouse, key), goDownstairsInPlagueHouse);
		continueTalkingToBravek.addStep(inPlagueHouse, searchBarrel);
		continueTalkingToBravek.addStep(inDownstairsOfPlagueHouse, goUpstairsInPlagueHouse);
		continueTalkingToBravek.addStep(and(warrant, inWestArdougne), tryToEnterPlagueHouseAgain);
		continueTalkingToBravek.addStep(inWestArdougne, talkToBravekAgain);
		continueTalkingToBravek.addStep(inUnderground, climbThroughPipe);

		steps.put(27, continueTalkingToBravek);

		var finishQuest = new ConditionalStep(this, talkToEdmondToFinish);
		finishQuest.addStep(inDownstairsOfPlagueHouse, goUpstairsInPlagueHouseToFinish);
		finishQuest.addStep(and(inWestArdougne, manholeClosed), goDownManhole2);
		finishQuest.addStep(inWestArdougne, goDownManhole);
		finishQuest.addStep(inUnderground, climbMudPileToFinish);

		steps.put(28, finishQuest);

		return steps;
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			dwellberries,
			spade,
			rope,
			bucketOfMilk,
			chocolateDust,
			snapeGrass
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			fourBucketsOfWater
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.MINING, 2425)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to use Ardougne teleport spell and tablets")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Start the quest", List.of(
			talkToEdmond
		), List.of(
			dwellberries,
			spade,
			rope,
			bucketOfMilk,
			chocolateDust,
			snapeGrass
		)));

		sections.add(new PanelDetails("Infiltrate West Ardougne", List.of(
			talkToAlrena,
			talkToEdmondAgain,
			useWaterOnMudPatch1,
			grabPictureOfElena,
			digHole,
			goDownHole,
			attemptToPullGrill,
			useRopeOnGrill,
			talkToEdmondUnderground,
			climbThroughPipe
		)));

		sections.add(new PanelDetails("Discover Elena's location", List.of(
			talkToJethick,
			enterMarthasHouse,
			talkToMartha,
			goUpstairsInMarthasHouse
		)));

		sections.add(new PanelDetails("Freeing Elena", List.of(
			tryToEnterPlagueHouse,
			talkToClerk,
			talkToBravek,
			useDustOnMilk,
			useSnapeGrassOnChocolateMilk,
			giveHangoverCureToBravek,
			tryToEnterPlagueHouseAgain,
			searchBarrel,
			goDownstairsInPlagueHouse,
			talkToElena
		)));

		sections.add(new PanelDetails("Finishing off", List.of(
			talkToEdmondToFinish
		)));

		return sections;
	}
}
