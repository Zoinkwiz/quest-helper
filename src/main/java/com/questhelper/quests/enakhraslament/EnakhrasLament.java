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
package com.questhelper.quests.enakhraslament;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.requirements.Spellbook;
import com.questhelper.requirements.SpellbookRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
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
	quest = QuestHelperQuest.ENAKHRAS_LAMENT
)
public class EnakhrasLament extends BasicQuestHelper
{
	ItemRequirement pickaxe, chiselHighlighted, sandstone32, sandstone20, base, body, head, granite2, granite, leftArm, rightArm, leftLeg,
		rightLeg, kSigil, rSigil, mSigil, zSigil, softClay, camelMould, camelHead, breadOrCake, fireSpellRunes, airSpellRunes,
		mapleLog, log, oakLog, willowLog, coal, candle, air2, chaos, earth2, sandstone5, tinderbox, crumbleUndeadRunes, sandstone52,
		airStaff, airRuneOrStaff, earthRuneOrStaff, earthStaff;

	SpellbookRequirement onNormals;

	ConditionForStep hasBase, hasBody, has32, has20, hasPlacedBase, hasTalkedToLazimAfterBase, hasPlacedBody, chiseledStatue,
		canChooseHead, has2Granite, hasGranite, hasHead, inTempleEntranceRoom, inTempleGroundFloor, startedTemple, hasMSigil,
		gottenLimbs, hasKSigil, hasRSigil, hasZSigil, openedDoor1, openedDoor2, openedDoor3, openedDoor4, mPlaced, kPlaced,
		rPlaced, zPlaced, goneUpstairs, hasCamelMould, hasCamelHead, hasGottenRightArm, hasGottenRightLeg, inCentreRoom, inPuzzleFloor,
		fedBread, meltedFountain, cleanedFurnace, litBraziers, litLog, litOak, litWillow, litMaple, litCandle, litCoal, inNorthPuzzleRoom,
		inTopRoom, inLastRoom, wallNeedsChisel, finishedWall;

	DetailedQuestStep talkToLazim, bringLazim32Sandstone, useChiselOn32Sandstone, placeBase, bringLazim20Sandstone,
		useChiselOn20Sandstone, placeBody, talkToLazimToChooseHead, getGranite, craftHead, talkToLazimAboutBody,
		chiselStatue, giveLazimHead, talkToLazimInTemple, enterTemple, enterTempleDownLadder, cutOffLimb, takeM,
		talkToLazimForHead, enterDoor1, enterDoor2, enterDoor3, enterDoor4, enterKDoor, enterRDoor, enterMDoor, enterZDoor,
		takeZ, takeK, takeR, useStoneHeadOnPedestal, useSoftClayOnPedestal, useChiselOnGranite, goUpToPuzzles, useBread, castAirSpell,
		castFireSpell, useMapleLog, useOakLog, useLog, useWillowLog, useCoal, useCandle, passBarrier, goUpFromPuzzleRoom, castCrumbleUndead,
		goDownToFinalRoom, protectThenTalk, repairWall, useChiselOnWall, talkToAkthankos;

	Zone templeEntranceRoom, templeGroundFloor, centreRoom, puzzleFloor, northPuzzleRoom, topRoom, lastRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToLazim);

		ConditionalStep makeAndPlaceBase = new ConditionalStep(this, bringLazim32Sandstone);
		makeAndPlaceBase.addStep(new Conditions(hasHead, hasGranite), giveLazimHead);
		makeAndPlaceBase.addStep(new Conditions(has2Granite, canChooseHead), craftHead);
		makeAndPlaceBase.addStep(canChooseHead, getGranite);
		makeAndPlaceBase.addStep(chiseledStatue, talkToLazimToChooseHead);
		makeAndPlaceBase.addStep(hasPlacedBody, chiselStatue);
		makeAndPlaceBase.addStep(hasBody, placeBody);
		makeAndPlaceBase.addStep(has20, useChiselOn20Sandstone);
		makeAndPlaceBase.addStep(hasTalkedToLazimAfterBase, bringLazim20Sandstone);
		makeAndPlaceBase.addStep(hasPlacedBase, talkToLazimAboutBody);
		makeAndPlaceBase.addStep(hasBase, placeBase);
		makeAndPlaceBase.addStep(has32, useChiselOn32Sandstone);

		steps.put(10, makeAndPlaceBase);

		ConditionalStep exploreBottomLayer = new ConditionalStep(this, enterTemple);
		exploreBottomLayer.addStep(new Conditions(hasCamelHead, inPuzzleFloor), useStoneHeadOnPedestal);
		exploreBottomLayer.addStep(hasCamelMould, useChiselOnGranite);
		exploreBottomLayer.addStep(inPuzzleFloor, useSoftClayOnPedestal);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, openedDoor3, openedDoor4), goUpToPuzzles);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, openedDoor3, hasKSigil), enterDoor4);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, openedDoor3), takeK);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, hasRSigil), enterDoor3);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2), takeR);
		// It's possible to skip the rest of this, but it skips some of the quest story and leaves doors locked after you finish, so this encourages players to explore
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, hasZSigil), enterDoor2);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1), takeZ);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, hasMSigil), enterDoor1);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor), takeM);
		exploreBottomLayer.addStep(new Conditions(startedTemple, inTempleGroundFloor), cutOffLimb);
		exploreBottomLayer.addStep(inTempleGroundFloor, talkToLazimInTemple);
		exploreBottomLayer.addStep(inTempleEntranceRoom, enterTempleDownLadder);

		steps.put(20, exploreBottomLayer);

		ConditionalStep puzzles = new ConditionalStep(this, enterTemple);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain, cleanedFurnace, litLog, litOak, litWillow, litMaple, litCandle), useCoal);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain, cleanedFurnace, litLog, litOak, litWillow, litMaple), useCandle);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain, cleanedFurnace, litLog, litOak, litWillow), useMapleLog);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain, cleanedFurnace, litLog, litOak), useWillowLog);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain, cleanedFurnace, litLog), useOakLog);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain, cleanedFurnace), useLog);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor, meltedFountain), castAirSpell);
		puzzles.addStep(new Conditions(fedBread, inPuzzleFloor), castFireSpell);
		puzzles.addStep(inPuzzleFloor, useBread);
		puzzles.addStep(inTempleGroundFloor, goUpToPuzzles);
		puzzles.addStep(inTempleEntranceRoom, enterTempleDownLadder);

		steps.put(30, puzzles);

		ConditionalStep topFloorPuzzle = new ConditionalStep(this, enterTemple);
		topFloorPuzzle.addStep(inTopRoom, castCrumbleUndead);
		topFloorPuzzle.addStep(inNorthPuzzleRoom, goUpFromPuzzleRoom);
		topFloorPuzzle.addStep(inPuzzleFloor, passBarrier);
		topFloorPuzzle.addStep(inTempleGroundFloor, goUpToPuzzles);
		topFloorPuzzle.addStep(inTempleEntranceRoom, enterTempleDownLadder);

		steps.put(40, topFloorPuzzle);

		ConditionalStep protectMeleePuzzle = new ConditionalStep(this, enterTemple);
		protectMeleePuzzle.addStep(inLastRoom, protectThenTalk);
		protectMeleePuzzle.addStep(inTopRoom, goDownToFinalRoom);
		protectMeleePuzzle.addStep(inNorthPuzzleRoom, goUpFromPuzzleRoom);
		protectMeleePuzzle.addStep(inPuzzleFloor, passBarrier);
		protectMeleePuzzle.addStep(inTempleGroundFloor, goUpToPuzzles);
		protectMeleePuzzle.addStep(inTempleEntranceRoom, enterTempleDownLadder);

		steps.put(50, protectMeleePuzzle);

		ConditionalStep repairWallForAkthankos = new ConditionalStep(this, enterTemple);
		repairWallForAkthankos.addStep(new Conditions(inLastRoom, wallNeedsChisel), useChiselOnWall);
		repairWallForAkthankos.addStep(new Conditions(inLastRoom, finishedWall), talkToAkthankos);
		repairWallForAkthankos.addStep(inLastRoom, repairWall);
		repairWallForAkthankos.addStep(inTopRoom, goDownToFinalRoom);
		repairWallForAkthankos.addStep(inNorthPuzzleRoom, goUpFromPuzzleRoom);
		repairWallForAkthankos.addStep(inPuzzleFloor, passBarrier);
		repairWallForAkthankos.addStep(inTempleGroundFloor, goUpToPuzzles);
		repairWallForAkthankos.addStep(inTempleEntranceRoom, enterTempleDownLadder);

		steps.put(60, repairWallForAkthankos);

		return steps;
	}

	public void setupItemRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		chiselHighlighted = new ItemRequirement("Chisel", ItemID.CHISEL);
		chiselHighlighted.setHighlightInInventory(true);

		sandstone52 = new ItemRequirement("52 kg of sandstone", -1, -1);
		sandstone52.setDisplayItemId(ItemID.SANDSTONE_5KG);

		sandstone32 = new ItemRequirement("Sandstone 32kg", ItemID.SANDSTONE_32KG);
		sandstone32.setHighlightInInventory(true);
		sandstone20 = new ItemRequirement("Sandstone 20kg", ItemID.SANDSTONE_20KG);
		sandstone20.setHighlightInInventory(true);

		base = new ItemRequirement("Sandstone base", ItemID.SANDSTONE_BASE);
		base.setHighlightInInventory(true);
		body = new ItemRequirement("Sandstone body", ItemID.SANDSTONE_BODY);
		body.setHighlightInInventory(true);

		granite2 = new ItemRequirement("Granite (5kg)", ItemID.GRANITE_5KG, 2);
		granite = new ItemRequirement("Granite (5kg)", ItemID.GRANITE_5KG);
		granite.setHighlightInInventory(true);

		head = new ItemRequirement("Stone head", ItemID.STONE_HEAD);
		head.addAlternates(ItemID.STONE_HEAD_6990, ItemID.STONE_HEAD_6991, ItemID.STONE_HEAD_6992);

		mSigil = new ItemRequirement("M sigil", ItemID.M_SIGIL);
		zSigil = new ItemRequirement("Z sigil", ItemID.Z_SIGIL);
		kSigil = new ItemRequirement("K sigil", ItemID.K_SIGIL);
		rSigil = new ItemRequirement("R sigil", ItemID.R_SIGIL);

		leftLeg = new ItemRequirement("Stone left leg", ItemID.STONE_LEFT_LEG);
		leftLeg.setTip("You can get another from Lazim");
		leftArm = new ItemRequirement("Stone left arm", ItemID.STONE_LEFT_ARM);
		leftArm.setTip("You can get another from Lazim");
		rightLeg = new ItemRequirement("Stone right leg", ItemID.STONE_RIGHT_LEG);
		rightLeg.setTip("You can get another from Lazim");
		rightArm = new ItemRequirement("Stone right arm", ItemID.STONE_RIGHT_ARM);
		rightArm.setTip("You can get another from Lazim");

		softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);

		camelMould = new ItemRequirement("Camel mould (p)", ItemID.CAMEL_MOULD_P);
		camelHead = new ItemRequirement("Stone head", ItemID.STONE_HEAD_7002);
		camelHead.setHighlightInInventory(true);

		breadOrCake = new ItemRequirement("Bread or cake", ItemID.BREAD);
		breadOrCake.addAlternates(ItemID.CAKE);
		breadOrCake.setHighlightInInventory(true);

		airSpellRunes = new ItemRequirement("Runes for an air spell", -1, -1);
		airSpellRunes.setDisplayItemId(ItemID.AIR_RUNE_7558);
		fireSpellRunes = new ItemRequirement("Runes for a fire spell", -1, -1);
		fireSpellRunes.setDisplayItemId(ItemID.FIRE_RUNE_7554);
		crumbleUndeadRunes = new ItemRequirement("Runes for crumbe undead spell", -1, -1);
		crumbleUndeadRunes.setDisplayItemId(ItemID.SKULL_965);

		log = new ItemRequirement("Logs", ItemID.LOGS);
		log.setHighlightInInventory(true);
		mapleLog = new ItemRequirement("Maple logs", ItemID.MAPLE_LOGS);
		mapleLog.setHighlightInInventory(true);
		willowLog = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS);
		willowLog.setHighlightInInventory(true);
		oakLog = new ItemRequirement("Oak logs", ItemID.OAK_LOGS);
		oakLog.setHighlightInInventory(true);
		coal = new ItemRequirement("Coal", ItemID.COAL);
		coal.setHighlightInInventory(true);
		candle = new ItemRequirement("Candle", ItemID.CANDLE);
		candle.setHighlightInInventory(true);

		air2 = new ItemRequirement("Air rune", ItemCollections.getAirRune(), 2);
		airStaff = new ItemRequirement("Air staff", ItemCollections.getAirStaff(), 1, true);
		airRuneOrStaff = new ItemRequirements(LogicType.OR, "2 air runes", air2, airStaff);
		earth2 = new ItemRequirement("Earth rune", ItemCollections.getEarthRune(), 2);
		earthStaff = new ItemRequirement("Air staff", ItemCollections.getEarthStaff(), 1, true);
		earthRuneOrStaff = new ItemRequirements(LogicType.OR, "2 earth runes", earth2, earthStaff);
		chaos = new ItemRequirement("Chaos rune", ItemID.CHAOS_RUNE);

		sandstone5 = new ItemRequirement("Sandstone (5kg)", ItemID.SANDSTONE_5KG);
		sandstone5.setHighlightInInventory(true);

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);

		onNormals = new SpellbookRequirement(Spellbook.NORMAL);
	}

	public void loadZones()
	{
		templeEntranceRoom = new Zone(new WorldPoint(3124, 9328, 1), new WorldPoint(3128, 9330, 1));
		templeGroundFloor = new Zone(new WorldPoint(3074, 9282, 0), new WorldPoint(3133, 9341, 0));
		centreRoom = new Zone(new WorldPoint(3098, 9306, 0), new WorldPoint(3110, 9318, 0));
		puzzleFloor = new Zone(new WorldPoint(3086, 9305, 1), new WorldPoint(3121, 9326, 1));
		northPuzzleRoom = new Zone(new WorldPoint(2095, 9319, 1), new WorldPoint(3112, 9335, 1));
		topRoom = new Zone(new WorldPoint(3097, 9299, 2), new WorldPoint(3113, 9334, 2));
		lastRoom = new Zone(new WorldPoint(3096, 9291, 1), new WorldPoint(3112, 9302, 1));
	}

	public void setupConditions()
	{
		hasBase = new ItemRequirementCondition(base);
		has32 = new ItemRequirementCondition(sandstone32);
		hasBody = new ItemRequirementCondition(body);
		has20 = new ItemRequirementCondition(sandstone20);
		hasPlacedBase = new VarbitCondition(1593, 1);
		hasPlacedBody = new VarbitCondition(1593, 2);
		chiseledStatue = new VarbitCondition(1593, 3);
		canChooseHead = new VarbitCondition(1563, 1);
		hasTalkedToLazimAfterBase = new VarbitCondition(1562, 1);
		has2Granite = new ItemRequirementCondition(granite2);
		hasGranite = new ItemRequirementCondition(granite);

		hasHead = new ItemRequirementCondition(head);
		hasCamelMould = new ItemRequirementCondition(camelMould);
		hasCamelHead = new ItemRequirementCondition(camelHead);
		hasGottenRightArm = new VarbitCondition(1590, 1);
		hasGottenRightLeg = new VarbitCondition(1592, 1);

		inTempleEntranceRoom = new ZoneCondition(templeEntranceRoom);
		inTempleGroundFloor = new ZoneCondition(templeGroundFloor);
		inCentreRoom = new ZoneCondition(centreRoom);
		inPuzzleFloor = new ZoneCondition(puzzleFloor);
		inNorthPuzzleRoom = new ZoneCondition(northPuzzleRoom);
		inTopRoom = new ZoneCondition(topRoom);
		inLastRoom = new ZoneCondition(lastRoom);

		startedTemple = new VarbitCondition(1566, 1);

		gottenLimbs = new VarbitCondition(1587, 63);
		hasMSigil = new ItemRequirementCondition(mSigil);
		hasZSigil = new ItemRequirementCondition(zSigil);
		hasKSigil = new ItemRequirementCondition(kSigil);
		hasRSigil = new ItemRequirementCondition(rSigil);

		openedDoor1 = new VarbitCondition(1608, 1);
		openedDoor2 = new VarbitCondition(1609, 1);
		openedDoor3 = new VarbitCondition(1610, 1);
		openedDoor4 = new VarbitCondition(1611, 1);

		zPlaced = new VarbitCondition(1611, 1);
		mPlaced = new VarbitCondition(1612, 1);
		rPlaced = new VarbitCondition(1613, 1);
		kPlaced = new VarbitCondition(1614, 1);

		goneUpstairs = new VarbitCondition(1618, 1);

		fedBread = new VarbitCondition(1576, 1);
		meltedFountain = new VarbitCondition(1577, 1);
		cleanedFurnace = new VarbitCondition(1578, 1);
		litBraziers = new VarbitCondition(1579, 1);

		litLog = new VarbitCondition(1581, 1);
		litOak = new VarbitCondition(1582, 1);
		litWillow = new VarbitCondition(1583, 1);
		litMaple = new VarbitCondition(1584, 1);
		litCandle = new VarbitCondition(1585, 1);
		litCoal = new VarbitCondition(1586, 1);

		wallNeedsChisel = new VarbitCondition(1620, 1);
		finishedWall = new VarbitCondition(1602, 3);
	}

	public void setupSteps()
	{
		talkToLazim = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Talk to Lazim in the quarry south of the Bandit Camp.", pickaxe, onNormals);
		talkToLazim.addDialogStep("Of course!");
		bringLazim32Sandstone = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Get 32kg of sandstone and give it to Lazim. This can be done in batches, and you can mine some nearby.");
		bringLazim32Sandstone.addDialogStep("Okay, I'll get on with it.");
		bringLazim32Sandstone.addDialogStep("Yes, I have more stone.");
		bringLazim32Sandstone.addDialogStep("Here's a large 10 kg block.");
		bringLazim32Sandstone.addDialogStep("Here's a medium 5 kg block.");
		bringLazim32Sandstone.addDialogStep("Here's a small 2 kg block.");
		bringLazim32Sandstone.addDialogStep("Here's a tiny 1 kg block.");
		useChiselOn32Sandstone = new DetailedQuestStep(this, "Use a chisel on the sandstone 32kg.", chiselHighlighted, sandstone32);
		placeBase = new ObjectStep(this, NullObjectID.NULL_10952, new WorldPoint(3190, 2926, 0), "Place the base on the flat ground nearby.", base);
		talkToLazimAboutBody = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Talk to Lazim again.");
		talkToLazimAboutBody.addDialogStep("I'll do it right away!");

		bringLazim20Sandstone = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Get 20kg of sandstone and give it to Lazim. This can be done in batches, and you can mine some nearby.");
		bringLazim20Sandstone.addDialogStep("I'll do it right away!");
		bringLazim20Sandstone.addDialogStep("Yes, I have more stone.");
		bringLazim20Sandstone.addDialogStep("Here's a large 10 kg block.");
		bringLazim20Sandstone.addDialogStep("Here's a medium 5 kg block.");
		bringLazim20Sandstone.addDialogStep("Here's a small 2 kg block.");
		bringLazim20Sandstone.addDialogStep("Here's a tiny 1 kg block.");

		useChiselOn20Sandstone = new DetailedQuestStep(this, "Use a chisel on the sandstone 20kg.", chiselHighlighted, sandstone20);
		placeBody = new ObjectStep(this, NullObjectID.NULL_10952, new WorldPoint(3190, 2926, 0), "Place the body on the sandstone base.", body);
		talkToLazimToChooseHead = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Talk to Lazim and choose the head you'd like the statue to have.");
		getGranite = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Get 2 x granite 5kg, and then craft one into the head you chose. You can mine some nearby.", granite2);

		// TODO: Change head highlight text based on choice
		craftHead = new DetailedQuestStep(this, "Use a chisel on a piece of granite 5kg, and choose the head you decided on to craft.", chiselHighlighted, granite);

		chiselStatue = new ObjectStep(this, NullObjectID.NULL_10952, new WorldPoint(3190, 2926, 0), "Use a chisel on the headless statue.", chiselHighlighted);
		chiselStatue.addIcon(ItemID.CHISEL);

		giveLazimHead = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3190, 2925, 0), "Give Lazim the head.", head);

		enterTemple = new ObjectStep(this, NullObjectID.NULL_11046, new WorldPoint(3194, 2925, 0), "Enter the temple south of the Bandit's Camp.");
		enterTempleDownLadder = new ObjectStep(this, ObjectID.LADDER_11042, new WorldPoint(3127, 9329, 1), "Enter the temple south of the Bandit's Camp.");
		talkToLazimInTemple = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3127, 9324, 0), "Talk to Lazim in the temple.");

		cutOffLimb = new ObjectStep(this, NullObjectID.NULL_10970, new WorldPoint(3130, 9326, 0), "Use a chisel on the fallen statue to get all its limbs.", chiselHighlighted);
		cutOffLimb.addDialogSteps("Remove the statue's left arm", "Remove the statue's right arm", "Remove the statue's left leg", "Remove the statue's right leg");

		takeM = new ObjectStep(this, ObjectID.PEDESTAL_11061, new WorldPoint(3128, 9319, 0), "Take the M sigil from the pedestal in the room.");
		takeZ = new ObjectStep(this, ObjectID.PEDESTAL_11060, new WorldPoint(3097, 9336, 0), "Take the Z sigil from the pedestal in the north room.");
		takeK = new ObjectStep(this, ObjectID.PEDESTAL_11063, new WorldPoint(3080, 9305, 0), "Take the K sigil from the pedestal in the west room.");
		takeR = new ObjectStep(this, ObjectID.PEDESTAL_11062, new WorldPoint(3111, 9288, 0), "Take the R sigil from the pedestal in the south room.");
		talkToLazimForHead = new NpcStep(this, NpcID.LAZIM, new WorldPoint(3127, 9324, 0), "Talk to Lazim in the temple for the stone head.");
		talkToLazimForHead.addDialogStep("Do you know where the statue's head is?");

		enterDoor1 = new ObjectStep(this, ObjectID.DOOR_11066, new WorldPoint(3126, 9337, 0), "Enter the right arm door.", rightArm);
		enterDoor2 = new ObjectStep(this, ObjectID.DOOR_11068, new WorldPoint(3079, 9334, 0), "Enter the left leg door.", leftLeg);
		enterDoor3 = new ObjectStep(this, ObjectID.DOOR_11064, new WorldPoint(3082, 9387, 0), "Enter the left arm door.", leftArm);
		enterDoor4 = new ObjectStep(this, ObjectID.DOOR_11070, new WorldPoint(3129, 9290, 0), "Enter the right leg door.", rightLeg);

		enterKDoor = new ObjectStep(this, ObjectID.DOOR_11057, new WorldPoint(3111, 9312, 0), "Enter the door with a K.", kSigil);
		enterRDoor = new ObjectStep(this, ObjectID.DOOR_11055, new WorldPoint(3104, 9319, 0), "Enter the door with an R.", rSigil);
		enterMDoor = new ObjectStep(this, ObjectID.DOOR_11053, new WorldPoint(3097, 9312, 0), "Enter the door with an M.", mSigil);
		enterZDoor = new ObjectStep(this, ObjectID.DOOR_11051, new WorldPoint(3104, 9305, 0), "Enter the door with a Z.", zSigil);

		goUpToPuzzles = new ObjectStep(this, ObjectID.LADDER_11041, new WorldPoint(3104, 9309, 0), "Open the central room's doors using the metal letters. Go up the ladder in the central room.");

		useSoftClayOnPedestal = new ObjectStep(this, NullObjectID.NULL_10987, new WorldPoint(3104, 9312, 1), "Use soft clay on the pedestal.", softClay);
		useChiselOnGranite = new DetailedQuestStep(this, "Use a chisel on granite (5kg).", granite, chiselHighlighted);
		useStoneHeadOnPedestal = new ObjectStep(this, NullObjectID.NULL_10987, new WorldPoint(3104, 9312, 1), "Use the camel stone head on the pedestal.", camelHead);
		useStoneHeadOnPedestal.addIcon(ItemID.STONE_HEAD_7002);

		useBread = new NpcStep(this, NpcID.PENTYN, new WorldPoint(3091, 9324, 1), "Use bread or cake on Pentyn.", breadOrCake);
		castFireSpell = new NpcStep(this, NpcID.CRUST_OF_ICE, new WorldPoint(3092, 9308, 1), "Cast a fire spell on the frozen fountain.", fireSpellRunes, onNormals);
		castAirSpell = new NpcStep(this, NpcID.FURNACE_GRATE, new WorldPoint(3116, 9323, 1), "Cast an air spell on the furnace.", airSpellRunes, onNormals);
		useMapleLog = new ObjectStep(this, NullObjectID.NULL_11014, new WorldPoint(3114, 9309, 1), "Use a maple log on the north west brazier.", mapleLog);
		useMapleLog.addIcon(ItemID.MAPLE_LOGS);
		useOakLog = new ObjectStep(this, NullObjectID.NULL_11012, new WorldPoint(3116, 9306, 1), "Use an oak log on the south brazier.", oakLog);
		useOakLog.addIcon(ItemID.OAK_LOGS);
		useLog = new ObjectStep(this, NullObjectID.NULL_11011, new WorldPoint(3114, 9306, 1), "Use a normal log on the south east brazier.", log);
		useLog.addIcon(ItemID.LOGS);
		useWillowLog = new ObjectStep(this, NullObjectID.NULL_11013, new WorldPoint(3118, 9306, 1), "Use a willow log on the south west brazier.", willowLog);
		useWillowLog.addIcon(ItemID.WILLOW_LOGS);
		useCoal = new ObjectStep(this, NullObjectID.NULL_11016, new WorldPoint(3118, 9309, 1), "Use coal on the north east brazier.", coal);
		useCoal.addIcon(ItemID.COAL);
		useCandle = new ObjectStep(this, NullObjectID.NULL_11015, new WorldPoint(3116, 9309, 1), "Use a candle on the north brazier.", candle);
		useCandle.addIcon(ItemID.CANDLE);

		passBarrier = new ObjectStep(this, ObjectID.MAGIC_BARRIER, new WorldPoint(3104, 9319, 1), "Pass through the magic barrier and go up the ladder.");
		goUpFromPuzzleRoom = new ObjectStep(this, ObjectID.LADDER_11041, new WorldPoint(3104, 9332, 1), "Go up the ladder.");
		passBarrier.addSubSteps(goUpFromPuzzleRoom);

		castCrumbleUndead = new NpcStep(this, NpcID.BONEGUARD, new WorldPoint(3104, 9307, 2), "Cast crumble undead on the Boneguard.", earth2, airRuneOrStaff, chaos, onNormals);

		goDownToFinalRoom = new ObjectStep(this, ObjectID.STONE_LADDER_11044, new WorldPoint(3105, 9300, 2), "Climb down the stone ladder past the Boneguard.");

		protectThenTalk = new NpcStep(this, NpcID.BONEGUARD_3577, new WorldPoint(3105, 9297, 1), "Put on Protect from Melee, then talk to the Boneguard.");
		repairWall = new ObjectStep(this, NullObjectID.NULL_11027, new WorldPoint(3107, 9291, 1), "Take sandstone from the nearby rubble, and use it to repair the south wall. For each piece added, use a chisel on the wall.", sandstone5);
		repairWall.addDialogStep("Of course I'll help you out.");
		repairWall.addIcon(ItemID.SANDSTONE_5KG);

		useChiselOnWall =  new ObjectStep(this, NullObjectID.NULL_11027, new WorldPoint(3107, 9291, 1), "Use a chisel on the wall.", chiselHighlighted);
		useChiselOnWall.addDialogStep("Of course I'll help you out.");
		useChiselOnWall.addIcon(ItemID.CHISEL);

		talkToAkthankos = new NpcStep(this, NpcID.BONEGUARD_3577, new WorldPoint(3105, 9297, 1), "Talk to the Boneguard to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(pickaxe);
		reqs.add(chiselHighlighted);
		reqs.add(softClay);
		reqs.add(breadOrCake);
		reqs.add(tinderbox);
		reqs.add(log);
		reqs.add(oakLog);
		reqs.add(willowLog);
		reqs.add(mapleLog);
		reqs.add(candle);
		reqs.add(coal);
		reqs.add(fireSpellRunes);
		reqs.add(airSpellRunes);
		reqs.add(crumbleUndeadRunes);
		int miningLevel = client.getRealSkillLevel(Skill.MINING);
		if (miningLevel < 45)
		{
			reqs.add(granite2);
		}
		if (miningLevel < 35)
		{
			reqs.add(sandstone52);
		}
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToLazim))));
		allSteps.add(new PanelDetails("Craft a statue", new ArrayList<>(Arrays.asList(bringLazim32Sandstone, useChiselOn32Sandstone, placeBase, talkToLazimAboutBody,
			bringLazim20Sandstone, useChiselOn20Sandstone, placeBody, chiselStatue, talkToLazimToChooseHead, getGranite, craftHead, giveLazimHead)),
			pickaxe, chiselHighlighted, softClay, breadOrCake, tinderbox, log, oakLog, willowLog, mapleLog, candle, coal, fireSpellRunes, airSpellRunes, earth2, air2, chaos));
		allSteps.add(new PanelDetails("Explore the ground floor", new ArrayList<>(Arrays.asList(talkToLazimInTemple, cutOffLimb, takeM, enterDoor1, enterDoor2, enterMDoor, goUpToPuzzles))));
		allSteps.add(new PanelDetails("Solve the puzzles", new ArrayList<>(Arrays.asList(useSoftClayOnPedestal, useChiselOnGranite, useStoneHeadOnPedestal, useBread, castFireSpell, castAirSpell,
			useLog, useOakLog, useWillowLog, useMapleLog, useCandle, useCoal))));
		allSteps.add(new PanelDetails("Free Akthankos", new ArrayList<>(Arrays.asList(passBarrier, goUpFromPuzzleRoom, castCrumbleUndead, goDownToFinalRoom, protectThenTalk, repairWall))));


		return allSteps;
	}
}
