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
package com.questhelper.helpers.quests.enakhraslament;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class EnakhrasLament extends BasicQuestHelper
{
	// Required items
	ItemRequirement pickaxe;
	ItemRequirement chiselHighlighted;
	ItemRequirement sandstone32;
	ItemRequirement sandstone20;
	ItemRequirement granite2;
	ItemRequirement granite;
	ItemRequirement softClay;
	ItemRequirement breadOrCake;
	ItemRequirement fireSpellRunes;
	ItemRequirement airSpellRunes;
	ItemRequirement mapleLog;
	ItemRequirement log;
	ItemRequirement oakLog;
	ItemRequirement willowLog;
	ItemRequirement coal;
	ItemRequirement candle;
	ItemRequirement air2;
	ItemRequirement chaos;
	ItemRequirement earth2;
	ItemRequirement sandstone5;
	ItemRequirement tinderbox;
	ItemRequirement crumbleUndeadRunes;
	ItemRequirement sandstone52;
	ItemRequirement airStaff;
	ItemRequirement airRuneOrStaff;
	ItemRequirement earthRuneOrStaff;
	ItemRequirement earthStaff;

	// Mid-quest requirements
	ItemRequirement base;
	ItemRequirement body;
	ItemRequirement head;
	ItemRequirement leftArm;
	ItemRequirement rightArm;
	ItemRequirement leftLeg;
	ItemRequirement rightLeg;
	ItemRequirement kSigil;
	ItemRequirement rSigil;
	ItemRequirement mSigil;
	ItemRequirement zSigil;
	ItemRequirement camelMould;
	ItemRequirement camelHead;

	// Miscellaneous requirements
	SpellbookRequirement onNormals;
	VarbitRequirement hasPlacedBase;
	VarbitRequirement hasTalkedToLazimAfterBase;
	VarbitRequirement hasPlacedBody;
	VarbitRequirement chiseledStatue;
	VarbitRequirement canChooseHead;
	ZoneRequirement inTempleEntranceRoom;
	ZoneRequirement inTempleGroundFloor;
	VarbitRequirement startedTemple;
	VarbitRequirement gottenLimbs;
	VarbitRequirement openedDoor1;
	VarbitRequirement openedDoor2;
	VarbitRequirement openedDoor3;
	VarbitRequirement openedDoor4;
	VarbitRequirement mPlaced;
	VarbitRequirement kPlaced;
	VarbitRequirement rPlaced;
	VarbitRequirement zPlaced;
	VarbitRequirement goneUpstairs;
	VarbitRequirement hasGottenRightArm;
	VarbitRequirement hasGottenRightLeg;
	ZoneRequirement inCentreRoom;
	ZoneRequirement inPuzzleFloor;
	VarbitRequirement fedBread;
	VarbitRequirement meltedFountain;
	VarbitRequirement cleanedFurnace;
	VarbitRequirement litBraziers;
	VarbitRequirement litLog;
	VarbitRequirement litOak;
	VarbitRequirement litWillow;
	VarbitRequirement litMaple;
	VarbitRequirement litCandle;
	VarbitRequirement litCoal;
	ZoneRequirement inNorthPuzzleRoom;
	ZoneRequirement inTopRoom;
	ZoneRequirement inLastRoom;
	VarbitRequirement wallNeedsChisel;
	VarbitRequirement finishedWall;
	PrayerRequirement protectFromMelee;

	// Steps
	NpcStep talkToLazim;
	NpcStep bringLazim32Sandstone;
	DetailedQuestStep useChiselOn32Sandstone;
	ObjectStep placeBase;
	NpcStep bringLazim20Sandstone;
	DetailedQuestStep useChiselOn20Sandstone;
	ObjectStep placeBody;
	NpcStep talkToLazimToChooseHead;
	NpcStep getGranite;
	DetailedQuestStep craftHead;
	NpcStep talkToLazimAboutBody;
	DetailedQuestStep chiselStatue;
	NpcStep giveLazimHead;
	NpcStep talkToLazimInTemple;
	ObjectStep enterTemple;
	ObjectStep enterTempleDownLadder;
	ObjectStep cutOffLimb;
	ObjectStep takeM;
	NpcStep talkToLazimForHead;
	ObjectStep enterDoor1;
	ObjectStep enterDoor2;
	ObjectStep enterDoor3;
	ObjectStep enterDoor4;
	ObjectStep enterKDoor;
	ObjectStep enterRDoor;
	ObjectStep enterMDoor;
	ObjectStep enterZDoor;
	ObjectStep takeZ;
	ObjectStep takeK;
	ObjectStep takeR;
	ObjectStep useStoneHeadOnPedestal;
	ObjectStep useSoftClayOnPedestal;
	DetailedQuestStep useChiselOnGranite;
	ObjectStep goUpToPuzzles;
	NpcStep useBread;
	NpcStep castAirSpell;
	NpcStep castFireSpell;
	ObjectStep useMapleLog;
	ObjectStep useOakLog;
	ObjectStep useLog;
	ObjectStep useWillowLog;
	ObjectStep useCoal;
	ObjectStep useCandle;
	ObjectStep passBarrier;
	ObjectStep goUpFromPuzzleRoom;
	NpcStep castCrumbleUndead;
	ObjectStep goDownToFinalRoom;
	NpcStep protectThenTalk;
	ObjectStep repairWall;
	ObjectStep useChiselOnWall;
	NpcStep talkToAkthankos;

	// Zones
	Zone templeEntranceRoom;
	Zone templeGroundFloor;
	Zone centreRoom;
	Zone puzzleFloor;
	Zone northPuzzleRoom;
	Zone topRoom;
	Zone lastRoom;

	@Override
	protected void setupZones()
	{
		templeEntranceRoom = new Zone(new WorldPoint(3124, 9328, 1), new WorldPoint(3128, 9330, 1));
		templeGroundFloor = new Zone(new WorldPoint(3074, 9282, 0), new WorldPoint(3133, 9341, 0));
		centreRoom = new Zone(new WorldPoint(3098, 9306, 0), new WorldPoint(3110, 9318, 0));
		puzzleFloor = new Zone(new WorldPoint(3086, 9305, 1), new WorldPoint(3121, 9326, 1));
		northPuzzleRoom = new Zone(new WorldPoint(2095, 9319, 1), new WorldPoint(3112, 9335, 1));
		topRoom = new Zone(new WorldPoint(3097, 9299, 2), new WorldPoint(3113, 9334, 2));
		lastRoom = new Zone(new WorldPoint(3096, 9291, 1), new WorldPoint(3112, 9302, 1));
	}

	@Override
	protected void setupRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		chiselHighlighted = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed();
		chiselHighlighted.setHighlightInInventory(true);

		sandstone52 = new ItemRequirement("52 kg of sandstone", -1, -1);
		sandstone52.setDisplayItemId(ItemID.ENAKH_SANDSTONE_MEDIUM);

		sandstone32 = new ItemRequirement("Sandstone 32kg", ItemID.ENAKH_SANDSTONE_HUGE_BASE_LEGS);
		sandstone32.setHighlightInInventory(true);
		sandstone20 = new ItemRequirement("Sandstone 20kg", ItemID.ENAKH_SANDSTONE_HUGE_BODY);
		sandstone20.setHighlightInInventory(true);

		base = new ItemRequirement("Sandstone base", ItemID.ENAKH_SANDSTONE_CRAFTED_BASE_LEGS);
		base.setHighlightInInventory(true);
		body = new ItemRequirement("Sandstone body", ItemID.ENAKH_SANDSTONE_CRAFTED_BODY);
		body.setHighlightInInventory(true);

		granite2 = new ItemRequirement("Granite (5kg)", ItemID.ENAKH_GRANITE_MEDIUM, 2);
		granite = new ItemRequirement("Granite (5kg)", ItemID.ENAKH_GRANITE_MEDIUM);
		granite.setHighlightInInventory(true);

		head = new ItemRequirement("Stone head", ItemID.ENAKH_STATUE_HEAD_LAZIM);
		head.addAlternates(ItemID.ENAKH_STATUE_HEAD_ZAMORAK, ItemID.ENAKH_STATUE_HEAD_ICTHLARIN, ItemID.ENAKH_STATUE_HEAD_CAMEL);

		mSigil = new ItemRequirement("M sigil", ItemID.ENAKH_SIGIL_M);
		zSigil = new ItemRequirement("Z sigil", ItemID.ENAKH_SIGIL_Z);
		kSigil = new ItemRequirement("K sigil", ItemID.ENAKH_SIGIL_K);
		rSigil = new ItemRequirement("R sigil", ItemID.ENAKH_SIGIL_R);

		leftLeg = new ItemRequirement("Stone left leg", ItemID.ENAKH_LEG_LEFT);
		leftLeg.setTooltip("You can get another from Lazim");
		leftArm = new ItemRequirement("Stone left arm", ItemID.ENAKH_ARM_LEFT);
		leftArm.setTooltip("You can get another from Lazim");
		rightLeg = new ItemRequirement("Stone right leg", ItemID.ENAKH_LEG_RIGHT);
		rightLeg.setTooltip("You can get another from Lazim");
		rightArm = new ItemRequirement("Stone right arm", ItemID.ENAKH_ARM_RIGHT);
		rightArm.setTooltip("You can get another from Lazim");

		softClay = new ItemRequirement("Soft clay", ItemID.SOFTCLAY);

		camelMould = new ItemRequirement("Camel mould (p)", ItemID.ENAKH_CAMEL_MOULD_POSITIVE);
		camelHead = new ItemRequirement("Stone head", ItemID.ENAKH_STONE_HEAD_AKTHANAKOS);
		camelHead.setHighlightInInventory(true);

		breadOrCake = new ItemRequirement("Bread or cake", ItemID.BREAD);
		breadOrCake.addAlternates(ItemID.CAKE);
		breadOrCake.setHighlightInInventory(true);
		breadOrCake.setDisplayMatchedItemName(true);

		airSpellRunes = new ItemRequirement("Runes to cast Wind Bolt or stronger", -1, -1);
		airSpellRunes.setDisplayItemId(ItemID.AIRRUNE);
		fireSpellRunes = new ItemRequirement("Runes to cast Fire Bolt or stronger", -1, -1);
		fireSpellRunes.setDisplayItemId(ItemID.FIRERUNE);
		crumbleUndeadRunes = new ItemRequirement("Runes for crumble undead spell", -1, -1);
		crumbleUndeadRunes.setDisplayItemId(ItemID.SKULL);

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
		candle = new ItemRequirement("Candle", ItemID.UNLIT_CANDLE);
		candle.setHighlightInInventory(true);

		air2 = new ItemRequirement("Air rune", ItemCollections.AIR_RUNE, 2);
		airStaff = new ItemRequirement("Air staff", ItemCollections.AIR_STAFF, 1, true);
		airRuneOrStaff = new ItemRequirements(LogicType.OR, "2 air runes", air2, airStaff);
		earth2 = new ItemRequirement("Earth rune", ItemCollections.EARTH_RUNE, 2);
		earthStaff = new ItemRequirement("Air staff", ItemCollections.EARTH_STAFF, 1, true);
		earthRuneOrStaff = new ItemRequirements(LogicType.OR, "2 earth runes", earth2, earthStaff);
		chaos = new ItemRequirement("Chaos rune", ItemID.CHAOSRUNE);

		sandstone5 = new ItemRequirement("Sandstone (5kg)", ItemID.ENAKH_SANDSTONE_MEDIUM);
		sandstone5.setHighlightInInventory(true);

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();

		onNormals = new SpellbookRequirement(Spellbook.NORMAL);

		hasPlacedBase = new VarbitRequirement(VarbitID.ENAKH_STATUE_MULTIVAR, 1);
		hasPlacedBody = new VarbitRequirement(VarbitID.ENAKH_STATUE_MULTIVAR, 2);
		chiseledStatue = new VarbitRequirement(VarbitID.ENAKH_STATUE_MULTIVAR, 3);
		canChooseHead = new VarbitRequirement(VarbitID.ENAKH_LAZIM_STATUE_HEAD_BLURB, 1);
		hasTalkedToLazimAfterBase = new VarbitRequirement(VarbitID.ENAKH_LAZIM_STATUE_BODY_BLURB, 1);

		hasGottenRightArm = new VarbitRequirement(VarbitID.ENAKH_RIGHT_ARM_TAKEN, 1);
		hasGottenRightLeg = new VarbitRequirement(VarbitID.ENAKH_RIGHT_LEG_TAKEN, 1);

		inTempleEntranceRoom = new ZoneRequirement(templeEntranceRoom);
		inTempleGroundFloor = new ZoneRequirement(templeGroundFloor);
		inCentreRoom = new ZoneRequirement(centreRoom);
		inPuzzleFloor = new ZoneRequirement(puzzleFloor);
		inNorthPuzzleRoom = new ZoneRequirement(northPuzzleRoom);
		inTopRoom = new ZoneRequirement(topRoom);
		inLastRoom = new ZoneRequirement(lastRoom);

		startedTemple = new VarbitRequirement(VarbitID.ENAKH_LAZIM_REALLYAMAGE, 1);

		gottenLimbs = new VarbitRequirement(VarbitID.ENAKH_FALLEN_STATUE_MULTIVAR, 63);

		openedDoor1 = new VarbitRequirement(VarbitID.ENAKH_RIGHT_ARMLOCK, 1);
		openedDoor2 = new VarbitRequirement(VarbitID.ENAKH_LEFT_LEGLOCK, 1);
		openedDoor3 = new VarbitRequirement(VarbitID.ENAKH_LEFT_ARMLOCK, 1);
		openedDoor4 = new VarbitRequirement(VarbitID.ENAKH_RIGHT_LEGLOCK, 1);

		zPlaced = new VarbitRequirement(VarbitID.ENAKH_Z_DOOR, 1);
		mPlaced = new VarbitRequirement(VarbitID.ENAKH_M_DOOR, 1);
		rPlaced = new VarbitRequirement(VarbitID.ENAKH_R_DOOR, 1);
		kPlaced = new VarbitRequirement(VarbitID.ENAKH_K_DOOR, 1);

		goneUpstairs = new VarbitRequirement(VarbitID.ENAKH_SEEN_PEDESTAL, 1);

		fedBread = new VarbitRequirement(VarbitID.ENAKH_BLOOD_ROOM, 1);
		meltedFountain = new VarbitRequirement(VarbitID.ENAKH_ICE_ROOM, 1);
		cleanedFurnace = new VarbitRequirement(VarbitID.ENAKH_SMOKE_ROOM, 1);
		litBraziers = new VarbitRequirement(VarbitID.ENAKH_SHADOW_ROOM, 1);

		litLog = new VarbitRequirement(VarbitID.ENAKH_BRAZIER_1_MULTIVAR, 1);
		litOak = new VarbitRequirement(VarbitID.ENAKH_BRAZIER_2_MULTIVAR, 1);
		litWillow = new VarbitRequirement(VarbitID.ENAKH_BRAZIER_3_MULTIVAR, 1);
		litMaple = new VarbitRequirement(VarbitID.ENAKH_BRAZIER_4_MULTIVAR, 1);
		litCandle = new VarbitRequirement(VarbitID.ENAKH_BRAZIER_5_MULTIVAR, 1);
		litCoal = new VarbitRequirement(VarbitID.ENAKH_BRAZIER_6_MULTIVAR, 1);

		wallNeedsChisel = new VarbitRequirement(VarbitID.ENAKH_LARGEWALL_NEEDS_TRIMMING, 1);
		finishedWall = new VarbitRequirement(VarbitID.ENAKH_LARGEWALL_MULTIVAR, 3);

		protectFromMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
	}


	public void setupSteps()
	{
		talkToLazim = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Before you begin, ensure that you have enough prayer points to use" +
			" Protect from Melee for around five seconds (you will need this later in the temple). Talk to Lazim in the quarry south of the Bandit Camp.",
			pickaxe, onNormals);
		talkToLazim.addDialogSteps("Yes.", "Of course!");
		bringLazim32Sandstone = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Get 32kg of sandstone and give it to Lazim. This can be " +
			"done in batches, and you can mine some nearby.");
		bringLazim32Sandstone.addDialogStep("Okay, I'll get on with it.");
		bringLazim32Sandstone.addDialogStep("Yes, I have more stone.");
		bringLazim32Sandstone.addDialogStep("Here's a large 10 kg block.");
		bringLazim32Sandstone.addDialogStep("Here's a medium 5 kg block.");
		bringLazim32Sandstone.addDialogStep("Here's a small 2 kg block.");
		bringLazim32Sandstone.addDialogStep("Here's a tiny 1 kg block.");
		useChiselOn32Sandstone = new DetailedQuestStep(this, "Use a chisel on the sandstone 32kg.", chiselHighlighted, sandstone32);
		placeBase = new ObjectStep(this, ObjectID.ENAKH_STATUE_EAST_MULTILOC, new WorldPoint(3190, 2926, 0), "Place the base on the flat ground nearby.", base);
		talkToLazimAboutBody = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Talk to Lazim again.");
		talkToLazimAboutBody.addDialogStep("I'll do it right away!");

		bringLazim20Sandstone = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Get 20kg of sandstone and give it to Lazim. This can be " +
			"done in batches, and you can mine some nearby.");
		bringLazim20Sandstone.addDialogStep("I'll do it right away!");
		bringLazim20Sandstone.addDialogStep("Yes, I have more stone.");
		bringLazim20Sandstone.addDialogStep("Here's a large 10 kg block.");
		bringLazim20Sandstone.addDialogStep("Here's a medium 5 kg block.");
		bringLazim20Sandstone.addDialogStep("Here's a small 2 kg block.");
		bringLazim20Sandstone.addDialogStep("Here's a tiny 1 kg block.");

		useChiselOn20Sandstone = new DetailedQuestStep(this, "Use a chisel on the sandstone 20kg.", chiselHighlighted, sandstone20);
		placeBody = new ObjectStep(this, ObjectID.ENAKH_STATUE_EAST_MULTILOC, new WorldPoint(3190, 2926, 0), "Place the body on the sandstone base.", body);
		talkToLazimToChooseHead = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Talk to Lazim and choose the head you'd like the " +
			"statue to have.");
		getGranite = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Get 2 x granite (5kg). You can mine some nearby.", granite2);

		// TODO: Change head highlight text based on choice
		craftHead = new DetailedQuestStep(this, "Use a chisel on a piece of granite 5kg, and choose the head you decided on to craft.", chiselHighlighted,
			granite);

		chiselStatue = new ObjectStep(this, ObjectID.ENAKH_STATUE_EAST_MULTILOC, new WorldPoint(3190, 2926, 0), "Use a chisel on the headless statue.",
			chiselHighlighted);
		chiselStatue.addIcon(ItemID.CHISEL);

		giveLazimHead = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3190, 2925, 0), "Give Lazim the head.", head);

		enterTemple = new ObjectStep(this, ObjectID.ENAKH_SECRET_BOULDER_MULTILOC_E, new WorldPoint(3194, 2925, 0), "Enter the temple south of the Bandit's " +
			"Camp.");
		enterTempleDownLadder = new ObjectStep(this, ObjectID.ENAKH_TEMPLE_LADDERDOWN, new WorldPoint(3127, 9329, 1), "Enter the temple south of the Bandit's" +
			" Camp.");
		talkToLazimInTemple = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3127, 9324, 0), "Talk to Lazim in the temple.");

		cutOffLimb = new ObjectStep(this, ObjectID.ENAKH_FALLEN_STATUE_EAST_MULTILOC, new WorldPoint(3130, 9326, 0), "Use a chisel on the fallen statue to " +
			"get all its limbs.", chiselHighlighted);
		cutOffLimb.addDialogSteps("Remove the statue's left arm", "Remove the statue's right arm", "Remove the statue's left leg", "Remove the statue's right" +
			" leg");
		cutOffLimb.addIcon(ItemID.CHISEL);

		takeM = new ObjectStep(this, ObjectID.ENAKH_PEDESTAL_SIGIL_M, new WorldPoint(3128, 9319, 0), "Take the M sigil from the pedestal in the room.");
		takeZ = new ObjectStep(this, ObjectID.ENAKH_PEDESTAL_SIGIL_Z, new WorldPoint(3097, 9336, 0), "Take the Z sigil from the pedestal in the north room.");
		takeK = new ObjectStep(this, ObjectID.ENAKH_PEDESTAL_SIGIL_K, new WorldPoint(3080, 9305, 0), "Take the K sigil from the pedestal in the west room.");
		takeR = new ObjectStep(this, ObjectID.ENAKH_PEDESTAL_SIGIL_R, new WorldPoint(3111, 9288, 0), "Take the R sigil from the pedestal in the south room.");
		talkToLazimForHead = new NpcStep(this, NpcID.ENAKH_LAZIM, new WorldPoint(3127, 9324, 0), "Talk to Lazim in the temple for the stone head.");
		talkToLazimForHead.addDialogStep("Do you know where the statue's head is?");

		enterDoor1 = new ObjectStep(this, ObjectID.ENAKH_DOOR_RIGHT_ARM, new WorldPoint(3126, 9337, 0), "Enter the right arm door.", rightArm.highlighted());
		enterDoor1.addIcon(ItemID.ENAKH_ARM_RIGHT);
		enterDoor2 = new ObjectStep(this, ObjectID.ENAKH_DOOR_LEFT_LEG, new WorldPoint(3079, 9334, 0), "Enter the left leg door.", leftLeg.highlighted());
		enterDoor2.addIcon(ItemID.ENAKH_LEG_LEFT);
		enterDoor3 = new ObjectStep(this, ObjectID.ENAKH_DOOR_LEFT_ARM, new WorldPoint(3082, 9287, 0), "Enter the left arm door.", leftArm.highlighted());
		enterDoor3.addIcon(ItemID.ENAKH_ARM_LEFT);
		enterDoor4 = new ObjectStep(this, ObjectID.ENAKH_DOOR_RIGHT_LEG, new WorldPoint(3129, 9290, 0), "Enter the right leg door.", rightLeg.highlighted());
		enterDoor4.addIcon(ItemID.ENAKH_LEG_RIGHT);

		enterKDoor = new ObjectStep(this, ObjectID.ENAKH_DOOR_K_SIGIL, new WorldPoint(3111, 9312, 0), "Enter the door with a K.", kSigil);
		enterRDoor = new ObjectStep(this, ObjectID.ENAKH_DOOR_R_SIGIL, new WorldPoint(3104, 9319, 0), "Enter the door with an R.", rSigil);
		enterMDoor = new ObjectStep(this, ObjectID.ENAKH_DOOR_M_SIGIL, new WorldPoint(3097, 9312, 0), "Enter the door with an M.", mSigil);
		enterZDoor = new ObjectStep(this, ObjectID.ENAKH_DOOR_Z_SIGIL, new WorldPoint(3104, 9305, 0), "Enter the door with a Z.", zSigil);

		goUpToPuzzles = new ObjectStep(this, ObjectID.ENAKH_TEMPLE_LADDERUP, new WorldPoint(3104, 9309, 0), "Open the central room's doors using the metal " +
			"letters. Go up the ladder in the central room.");

		useSoftClayOnPedestal = new ObjectStep(this, ObjectID.ENAKH_PEDESTAL_MULTILOC, new WorldPoint(3104, 9312, 1),
			"Use soft clay on the pedestal.", softClay.highlighted());
		useChiselOnGranite = new DetailedQuestStep(this, "Use a chisel on granite (5kg).", granite, chiselHighlighted);
		useStoneHeadOnPedestal = new ObjectStep(this, ObjectID.ENAKH_PEDESTAL_MULTILOC, new WorldPoint(3104, 9312, 1), "Use the camel stone head on the " +
			"pedestal.", camelHead);
		useStoneHeadOnPedestal.addIcon(ItemID.ENAKH_STONE_HEAD_AKTHANAKOS);

		useBread = new NpcStep(this, NpcID.ENAKH_PENTYN, new WorldPoint(3091, 9324, 1), "Right-click use bread or cake on Pentyn.", breadOrCake.highlighted());
		castFireSpell = new NpcStep(this, NpcID.ENAKH_DUMMY_FOUNTAIN, new WorldPoint(3092, 9308, 1), "Cast a fire spell on the frozen fountain.",
			fireSpellRunes, onNormals);
		castAirSpell = new NpcStep(this, NpcID.ENAKH_DUMMY_FURNACE, new WorldPoint(3116, 9323, 1), "Cast an air spell on the furnace.", airSpellRunes,
			onNormals);

		// Shadow Room Puzzle
		useLog = new ObjectStep(this, ObjectID.ENAKH_BRAZIER_1_MULTILOC, new WorldPoint(3114, 9306, 1), "Use a normal log on the south west brazier.", log);
		useLog.addIcon(ItemID.LOGS);
		useOakLog = new ObjectStep(this, ObjectID.ENAKH_BRAZIER_2_MULTILOC, new WorldPoint(3116, 9306, 1), "Use an oak log on the south brazier.", oakLog);
		useOakLog.addIcon(ItemID.OAK_LOGS);
		useWillowLog = new ObjectStep(this, ObjectID.ENAKH_BRAZIER_3_MULTILOC, new WorldPoint(3118, 9306, 1), "Use a willow log on the south east brazier.",
			willowLog);
		useWillowLog.addIcon(ItemID.WILLOW_LOGS);
		useMapleLog = new ObjectStep(this, ObjectID.ENAKH_BRAZIER_4_MULTILOC, new WorldPoint(3114, 9309, 1), "Use a maple log on the north west brazier.",
			mapleLog);
		useMapleLog.addIcon(ItemID.MAPLE_LOGS);
		useCandle = new ObjectStep(this, ObjectID.ENAKH_BRAZIER_5_MULTILOC, new WorldPoint(3116, 9309, 1), "Use a candle on the north brazier.", candle);
		useCandle.addIcon(ItemID.UNLIT_CANDLE);
		useCoal = new ObjectStep(this, ObjectID.ENAKH_BRAZIER_6_MULTILOC, new WorldPoint(3118, 9309, 1), "Use coal on the north east brazier.", coal);
		useCoal.addIcon(ItemID.COAL);

		passBarrier = new ObjectStep(this, ObjectID.ENAKH_MAGIC_WALL, new WorldPoint(3104, 9319, 1), "Pass through the magic barrier and go up the ladder.");
		goUpFromPuzzleRoom = new ObjectStep(this, ObjectID.ENAKH_TEMPLE_LADDERUP, new WorldPoint(3104, 9332, 1), "Go up the ladder.");
		passBarrier.addSubSteps(goUpFromPuzzleRoom);

		castCrumbleUndead = new NpcStep(this, NpcID.ENAKH_BONEGUARD, new WorldPoint(3104, 9307, 2), "Cast crumble undead on the Boneguard.", earth2,
			airRuneOrStaff, chaos, onNormals);

		goDownToFinalRoom = new ObjectStep(this, ObjectID.ENAKH_TEMPLE_PILLAR_LADDER_TOP, new WorldPoint(3105, 9300, 2), "Climb down the stone ladder past " +
			"the Boneguard.");

		protectThenTalk = new NpcStep(this, NpcID.ENAKH_AKTHANAKOS_BONEGUARD, new WorldPoint(3105, 9297, 1),
			"Put on Protect from Melee, then talk to the Boneguard.", protectFromMelee);
		repairWall = new ObjectStep(this, ObjectID.ENAKH_LARGEWALL_L_MULTILOC, new WorldPoint(3107, 9291, 1), "Take sandstone from the nearby rubble, and use" +
			" it to repair the south wall. For each piece added, use a chisel on the wall.", sandstone5);
		repairWall.addDialogSteps("Of course, I'll help you out.", "Okay, I'll start building.");
		repairWall.addIcon(ItemID.ENAKH_SANDSTONE_MEDIUM);

		useChiselOnWall = new ObjectStep(this, ObjectID.ENAKH_LARGEWALL_L_MULTILOC, new WorldPoint(3107, 9291, 1), "Use a chisel on the wall.",
			chiselHighlighted);
		useChiselOnWall.addDialogSteps("Of course, I'll help you out.", "Okay, I'll start building.");
		useChiselOnWall.addIcon(ItemID.CHISEL);
		repairWall.addSubSteps(useChiselOnWall);

		talkToAkthankos = new NpcStep(this, NpcID.ENAKH_AKTHANAKOS_BONEGUARD, new WorldPoint(3105, 9297, 1), "Talk to the Boneguard to finish the quest.");
		talkToAkthankos.addAlternateNpcs(NpcID.ENAKH_AKTHANAKOS_FREED);

	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToLazim);

		ConditionalStep makeAndPlaceBase = new ConditionalStep(this, bringLazim32Sandstone);
		makeAndPlaceBase.addStep(new Conditions(head, granite), giveLazimHead);
		makeAndPlaceBase.addStep(new Conditions(granite2, canChooseHead), craftHead);
		makeAndPlaceBase.addStep(canChooseHead, getGranite);
		makeAndPlaceBase.addStep(chiseledStatue, talkToLazimToChooseHead);
		makeAndPlaceBase.addStep(hasPlacedBody, chiselStatue);
		makeAndPlaceBase.addStep(body, placeBody);
		makeAndPlaceBase.addStep(sandstone20, useChiselOn20Sandstone);
		makeAndPlaceBase.addStep(hasTalkedToLazimAfterBase, bringLazim20Sandstone);
		makeAndPlaceBase.addStep(hasPlacedBase, talkToLazimAboutBody);
		makeAndPlaceBase.addStep(base, placeBase);
		makeAndPlaceBase.addStep(sandstone32, useChiselOn32Sandstone);

		steps.put(10, makeAndPlaceBase);

		ConditionalStep exploreBottomLayer = new ConditionalStep(this, enterTemple);
		exploreBottomLayer.addStep(new Conditions(camelHead, inPuzzleFloor), useStoneHeadOnPedestal);
		exploreBottomLayer.addStep(camelMould, useChiselOnGranite);
		exploreBottomLayer.addStep(inPuzzleFloor, useSoftClayOnPedestal);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, openedDoor3, openedDoor4), goUpToPuzzles);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, openedDoor3, rSigil), enterDoor4);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, openedDoor3), takeR);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2, kSigil), enterDoor3);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, openedDoor2), takeK);
		// It's possible to skip the rest of this, but it skips some of the quest story and leaves doors locked after you finish, so this encourages players
		// to explore
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1, zSigil), enterDoor2);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, openedDoor1), takeZ);
		exploreBottomLayer.addStep(new Conditions(gottenLimbs, inTempleGroundFloor, mSigil), enterDoor1);
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

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			pickaxe,
			chiselHighlighted,
			softClay,
			breadOrCake,
			tinderbox,
			log,
			oakLog,
			willowLog,
			mapleLog,
			candle,
			coal,
			fireSpellRunes,
			airSpellRunes,
			crumbleUndeadRunes,
			granite2.hideConditioned(new SkillRequirement(Skill.MINING, 45)),
			sandstone52.hideConditioned(new SkillRequirement(Skill.MINING, 35))
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(onNormals);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.CRAFTING, 50),
			new SkillRequirement(Skill.FIREMAKING, 45, true),
			new SkillRequirement(Skill.PRAYER, 43),
			new SkillRequirement(Skill.MAGIC, 39)
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 7000),
			new ExperienceReward(Skill.MINING, 7000),
			new ExperienceReward(Skill.FIREMAKING, 7000),
			new ExperienceReward(Skill.MAGIC, 7000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Akthanakos's Camulet", ItemID.CAMULET, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var allSteps = new ArrayList<PanelDetails>();

		allSteps.add(new PanelDetails("Starting off", List.of(
			talkToLazim
		)));

		allSteps.add(new PanelDetails("Craft a statue", List.of(
			bringLazim32Sandstone, useChiselOn32Sandstone, placeBase, talkToLazimAboutBody, bringLazim20Sandstone,
			useChiselOn20Sandstone, placeBody, chiselStatue, talkToLazimToChooseHead, getGranite, craftHead, giveLazimHead
		), List.of(pickaxe, chiselHighlighted, softClay, breadOrCake, tinderbox, log, oakLog, willowLog, mapleLog, candle, coal, fireSpellRunes,
			airSpellRunes, earth2, air2, chaos
		)));

		allSteps.add(new PanelDetails("Explore the ground floor", List.of(
			talkToLazimInTemple, cutOffLimb, takeM, enterDoor1, enterDoor2, enterMDoor, goUpToPuzzles
		)));

		allSteps.add(new PanelDetails("Solve the puzzles", List.of(
			useSoftClayOnPedestal, useChiselOnGranite, useStoneHeadOnPedestal, useBread, castFireSpell, castAirSpell,
			useLog, useOakLog, useWillowLog, useMapleLog, useCandle, useCoal
		)));

		allSteps.add(new PanelDetails("Free Akthankos", List.of(
			passBarrier, goUpFromPuzzleRoom, castCrumbleUndead, goDownToFinalRoom, protectThenTalk, repairWall, talkToAkthankos
		)));

		return allSteps;
	}
}
