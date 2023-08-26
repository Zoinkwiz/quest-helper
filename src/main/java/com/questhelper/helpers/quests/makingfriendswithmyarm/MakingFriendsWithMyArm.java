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
package com.questhelper.helpers.quests.makingfriendswithmyarm;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
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
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM
)
public class MakingFriendsWithMyArm extends BasicQuestHelper
{
	//Items Required
	ItemRequirement saw, boltOfCloth, mahogPlanks5, cadavaBerries, combatRangeMelee, pickaxe, rope, ropeHighlight, hammer,
		potion, coffin, bucketHighlight, bucketOfWaterHighlight, fireNotes, goatDung;

	//Items Recommended
	ItemRequirement trollTele, draynorTele, varrockTele;

	Requirement inStrongholdFloor1, inStrongholdFloor2, inPrison, onRoof, inWeissArrivalArea, onCliff1, onCliff2, onCliff3,
		onCliff4, onCliff5, isOutsideWeiss, inWeiss, inInstance, inSneakArea1, inSneakArea2, inSneakArea3, inSneakArea4, inSneakArea5, inCave1, inCave2,
		inWater1, inWater2, inWater3, inWater4, inCave3, inCave4, rockR0C0, rockR0C1, rockR0C2, rockR0C3, rockR0C4, rockR1C0, rockR1C1, rockR1C2, rockR1C3,
		rockR1C4, rockR2C0, rockR2C1, rockR2C2, rockR2C3, rockR2C4, row0Made, row1Made, row2Made, rowMade, pickedUpWom, inWeissPrison, oddMushroomDied,
		firstBossNearby, defeatedBoss1, secondBossNearby, hasPutOutFire;

	DetailedQuestStep enterStronghold, goDownToBurntmeat, goUpToBurntmeat, talkToBurntmeat, enterStrongholdAfterStart, goUpToMyArmAfterStart,
		goUpFromF1ToMyArm, goUpAfterStart, talkToMyArmUpstairs, talkToLarry, talkToLarryAgain, boardBoat, attemptToMine, searchBoatForRopeAndPickaxe,
		searchBoatForPickaxe, searchBoatForRope, climbRocks, climbRocks2, useRope, climbRope, crossLedge, climbRocks3, passTree, talkToBoulder, crossFence,
		goSouthSneak, goWestSneak1, goWestSneak2, goNorth, goWestSneak3, enterHole, enterNarrowHole, enterWater, waterSpot1, leaveWater1, enterWater2,
		mineCave, placeRocks, talkToMother, talkToMyArmAfterMeeting, talkToWom, buildCoffin, talkToApoth, talkToWomAfterPrep, pickUpCoffin, takeBoatWithWom,
		enterCaveWithWom, talkToMyArmWithWom, talkToMyArmAfterGivingWom, takeBoatToPrison, enterCaveToPrison, talkToOddMushroom, talkToBoulderToEnterPrison,
		talkToSnowflake, killDontKnowWhat, pickUpBucket, useBucketOnWater, useBucketOnFire, talkToMyArmAfterFight, talkToWomAfterFight, talkToSnowflakeAfterFight,
		pickUpGoatDung, pickUpBucketForDung, emptyBucket, bringDungToSnowflake, talkToSnowflakeAfterDung, readNotes, talkToSnowflakeToFinish;

	NpcStep killMother;

	//Zones
	Zone strongholdFloor1, strongholdFloor2, prison, roof, weissArrivalArea, cliff1, cliff2, cliff3, cliff4, cliff5, outsideWeiss1, outsideWeiss2, outsideWeiss3,
		outsideWeiss4, outsideWeiss5, weiss, sneakArea1, sneakArea2, sneakArea3, sneakArea4, sneakArea5, cave1, cave2, cave3, cave4, water1, water2, water3, water4,
		weissPrison;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startingOffSteps = new ConditionalStep(this, enterStronghold);
		startingOffSteps.addStep(inStrongholdFloor1, talkToBurntmeat);
		startingOffSteps.addStep(inPrison, goUpToBurntmeat);
		startingOffSteps.addStep(inStrongholdFloor2, goDownToBurntmeat);

		steps.put(0, startingOffSteps);
		steps.put(1, startingOffSteps);
		steps.put(5, startingOffSteps);
		ConditionalStep talkToArmOnRoofSteps = new ConditionalStep(this, enterStrongholdAfterStart);
		talkToArmOnRoofSteps.addStep(new Conditions(onRoof), talkToMyArmUpstairs);
		talkToArmOnRoofSteps.addStep(new Conditions(inStrongholdFloor2), goUpToMyArmAfterStart);
		talkToArmOnRoofSteps.addStep(new Conditions(inStrongholdFloor1), goUpFromF1ToMyArm);
		talkToArmOnRoofSteps.addStep(new Conditions(inPrison), goUpAfterStart);

		steps.put(10, talkToArmOnRoofSteps);
		steps.put(15, talkToArmOnRoofSteps);

		steps.put(20, talkToLarry);
		steps.put(25, talkToLarryAgain);

		steps.put(30, boardBoat);

		ConditionalStep arrivingInWeissSteps = new ConditionalStep(this, boardBoat);
		arrivingInWeissSteps.addStep(inWeissArrivalArea, attemptToMine);

		steps.put(35, arrivingInWeissSteps);

		ConditionalStep addTheRope = new ConditionalStep(this, boardBoat);
		addTheRope.addStep(new Conditions(onCliff2, rope), useRope);
		addTheRope.addStep(new Conditions(onCliff1, pickaxe, rope), climbRocks2);
		addTheRope.addStep(new Conditions(inWeissArrivalArea, pickaxe, rope), climbRocks);
		addTheRope.addStep(new Conditions(inWeissArrivalArea, rope), searchBoatForPickaxe);
		addTheRope.addStep(new Conditions(inWeissArrivalArea, pickaxe), searchBoatForRope);
		addTheRope.addStep(inWeissArrivalArea, searchBoatForRopeAndPickaxe);

		steps.put(40, addTheRope);
		steps.put(41, addTheRope);

		ConditionalStep scalingTheMountain = new ConditionalStep(this, boardBoat);
		scalingTheMountain.addStep(new Conditions(onCliff5), passTree);
		scalingTheMountain.addStep(new Conditions(onCliff4), climbRocks3);
		scalingTheMountain.addStep(new Conditions(onCliff3), crossLedge);
		scalingTheMountain.addStep(new Conditions(onCliff2), climbRope);
		scalingTheMountain.addStep(new Conditions(onCliff1), climbRocks2);
		scalingTheMountain.addStep(new Conditions(inWeissArrivalArea, pickaxe), climbRocks);
		scalingTheMountain.addStep(new Conditions(inWeissArrivalArea), searchBoatForRope);

		ConditionalStep goTalkToBoulder = new ConditionalStep(this, scalingTheMountain);
		goTalkToBoulder.addStep(isOutsideWeiss, talkToBoulder);

		steps.put(45, goTalkToBoulder);
		steps.put(46, goTalkToBoulder);

		ConditionalStep sneakIntoWeiss = new ConditionalStep(this, scalingTheMountain);
		sneakIntoWeiss.addStep(new Conditions(inCave1, inInstance), enterNarrowHole);
		sneakIntoWeiss.addStep(new Conditions(inSneakArea5, inInstance), enterHole);
		sneakIntoWeiss.addStep(new Conditions(inSneakArea4, inInstance), goNorth);
		sneakIntoWeiss.addStep(new Conditions(inSneakArea3, inInstance), goWestSneak3);
		sneakIntoWeiss.addStep(new Conditions(inSneakArea2, inInstance), goWestSneak2);
		sneakIntoWeiss.addStep(new Conditions(inSneakArea1, inInstance), goWestSneak1);
		sneakIntoWeiss.addStep(new Conditions(inWeiss, inInstance), goSouthSneak);
		sneakIntoWeiss.addStep(inWeiss, crossFence);

		steps.put(50, sneakIntoWeiss);
		steps.put(55, sneakIntoWeiss);
		steps.put(60, sneakIntoWeiss);

		ConditionalStep sneakThroughCaves = new ConditionalStep(this, scalingTheMountain);
		sneakThroughCaves.addStep(new Conditions(new Conditions(LogicType.OR, inWater3, inCave4), rowMade), mineCave);
		sneakThroughCaves.addStep(new Conditions(LogicType.OR, inWater3, inCave4), placeRocks);
		sneakThroughCaves.addStep(new Conditions(inCave3, inInstance), enterWater2);
		sneakThroughCaves.addStep(new Conditions(inWater2, inInstance), leaveWater1);
		sneakThroughCaves.addStep(new Conditions(inWater1, inInstance), waterSpot1);
		sneakThroughCaves.addStep(new Conditions(inCave2, inInstance), enterWater);
		sneakThroughCaves.addStep(inWeiss, crossFence);

		steps.put(65, sneakThroughCaves);
		steps.put(70, sneakThroughCaves);

		steps.put(75, talkToMother);
		steps.put(80, talkToMother);
		steps.put(81, talkToMother);
		steps.put(82, talkToMother);

		steps.put(85, talkToMyArmAfterMeeting);
		steps.put(86, talkToMyArmAfterMeeting);

		steps.put(90, talkToMyArmAfterMeeting);
		steps.put(95, talkToMyArmAfterMeeting);
		steps.put(100, talkToMyArmAfterMeeting);
		steps.put(101, talkToMyArmAfterMeeting);
		steps.put(105, talkToMyArmAfterMeeting);

		steps.put(110, talkToWom);
		steps.put(115, talkToWom);

		steps.put(120, buildCoffin);
		steps.put(122, talkToApoth);
		steps.put(125, buildCoffin);
		steps.put(127, talkToApoth);
		steps.put(132, talkToWomAfterPrep);


		ConditionalStep takeWomToWeiss = new ConditionalStep(this, pickUpCoffin);
		takeWomToWeiss.addStep(new Conditions(pickedUpWom, inWeiss), talkToMyArmWithWom);
		takeWomToWeiss.addStep(new Conditions(pickedUpWom,
			new Conditions(LogicType.OR, onCliff1, onCliff2, onCliff3, onCliff4, onCliff5, inWeissArrivalArea)), enterCaveWithWom);
		takeWomToWeiss.addStep(pickedUpWom, takeBoatWithWom);

		steps.put(135, takeWomToWeiss);

		steps.put(140, talkToMyArmAfterGivingWom);

		ConditionalStep prisonSteps = new ConditionalStep(this, takeBoatToPrison);
		prisonSteps.addStep(new Conditions(secondBossNearby, hasPutOutFire), killMother);
		prisonSteps.addStep(new Conditions(secondBossNearby, bucketOfWaterHighlight), useBucketOnFire);
		prisonSteps.addStep(new Conditions(secondBossNearby, bucketHighlight), useBucketOnWater);
		prisonSteps.addStep(secondBossNearby, pickUpBucket);
		prisonSteps.addStep(new Conditions(inWeissPrison, firstBossNearby), killDontKnowWhat);
		prisonSteps.addStep(new Conditions(inWeissPrison, oddMushroomDied), talkToSnowflake);
		prisonSteps.addStep(inWeissPrison, talkToOddMushroom);
		prisonSteps.addStep(inWeiss, talkToBoulderToEnterPrison);
		prisonSteps.addStep(new Conditions(LogicType.OR, onCliff1, onCliff2, onCliff3, onCliff4, onCliff5, inWeissArrivalArea), enterCaveToPrison);

		steps.put(145, prisonSteps);
		steps.put(146, prisonSteps);
		steps.put(147, prisonSteps);
		steps.put(148, prisonSteps);
		steps.put(149, prisonSteps);
		steps.put(150, prisonSteps);
		steps.put(155, prisonSteps);
		steps.put(160, prisonSteps);
		steps.put(165, prisonSteps);
		steps.put(170, prisonSteps);

		steps.put(175, talkToMyArmAfterFight);
		steps.put(178, talkToWomAfterFight);
		steps.put(180, talkToSnowflakeAfterFight);

		ConditionalStep getDungForSnowflake = new ConditionalStep(this, pickUpBucketForDung);
		getDungForSnowflake.addStep(goatDung, bringDungToSnowflake);
		getDungForSnowflake.addStep(bucketHighlight, pickUpGoatDung);
		getDungForSnowflake.addStep(bucketOfWaterHighlight, emptyBucket);

		steps.put(185, getDungForSnowflake);
		steps.put(186, getDungForSnowflake);

		steps.put(190, talkToSnowflakeAfterDung);
		steps.put(195, readNotes);

		steps.put(196, talkToSnowflakeToFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		saw = new ItemRequirement("Saw", ItemCollections.SAW).isNotConsumed();
		boltOfCloth = new ItemRequirement("Bolt of cloth", ItemID.BOLT_OF_CLOTH);
		mahogPlanks5 = new ItemRequirement("Mahogany plank", ItemID.MAHOGANY_PLANK, 5);
		cadavaBerries = new ItemRequirement("Cadava berries", ItemID.CADAVA_BERRIES);
		combatRangeMelee = new ItemRequirement("Combat gear, preferably ranged or melee", -1, -1).isNotConsumed();
		combatRangeMelee.setDisplayItemId(BankSlotIcons.getCombatGear());
		trollTele = new ItemRequirement("Trollheim teleports", ItemID.TROLLHEIM_TELEPORT);
		varrockTele = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		draynorTele = new ItemRequirement("Draynor teleport", ItemID.DRAYNOR_MANOR_TELEPORT, 2);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlight = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlight.setHighlightInInventory(true);

		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		potion = new ItemRequirement("Reduced cadava potion", ItemID.REDUCED_CADAVA_POTION);
		potion.setTooltip("You can get another by bringing the Apothecary a cadava berry");

		coffin = new ItemRequirement("Old man's coffin", ItemID.OLD_MANS_COFFIN);

		bucketHighlight = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight.setHighlightInInventory(true);

		bucketOfWaterHighlight = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWaterHighlight.setHighlightInInventory(true);

		fireNotes = new ItemRequirement("Weiss fire notes", ItemID.WEISS_FIRE_NOTES);
		fireNotes.setHighlightInInventory(true);

		goatDung = new ItemRequirement("Goat dung", ItemID.GOAT_DUNG);
	}

	public void loadZones()
	{
		strongholdFloor1 = new Zone(new WorldPoint(2820, 10048, 1), new WorldPoint(2862, 10110, 1));
		strongholdFloor2 = new Zone(new WorldPoint(2820, 10048, 2), new WorldPoint(2862, 10110, 2));
		prison = new Zone(new WorldPoint(2822, 10049, 0), new WorldPoint(2859, 10110, 0));
		roof = new Zone(new WorldPoint(2822, 3665, 0), new WorldPoint(2838, 3701, 0));
		weissArrivalArea = new Zone(new WorldPoint(2844, 3958, 0), new WorldPoint(2861, 3972, 0));
		cliff1 = new Zone(new WorldPoint(2852, 3964, 0), new WorldPoint(2852, 3964, 0));
		cliff2 = new Zone(new WorldPoint(2853, 3964, 0), new WorldPoint(2855, 3964, 0));
		cliff3 = new Zone(new WorldPoint(2853, 3961, 0), new WorldPoint(2855, 3963, 0));
		cliff4 = new Zone(new WorldPoint(2857, 3961, 0), new WorldPoint(2860, 3962, 0));
		cliff5 = new Zone(new WorldPoint(2857, 3956, 0), new WorldPoint(2860, 3960, 0));
		outsideWeiss1 = new Zone(new WorldPoint(2851, 3948, 0), new WorldPoint(2856, 3959, 0));
		outsideWeiss2 = new Zone(new WorldPoint(2857, 3948, 0), new WorldPoint(2858, 3954, 0));
		outsideWeiss3 = new Zone(new WorldPoint(2859, 3948, 0), new WorldPoint(2861, 3956, 0));
		outsideWeiss4 = new Zone(new WorldPoint(2862, 3948, 0), new WorldPoint(2881, 3958, 0));
		outsideWeiss5 = new Zone(new WorldPoint(2882, 3949, 0), new WorldPoint(2895, 3958, 0));
		weiss = new Zone(new WorldPoint(2835, 3915, 0), new WorldPoint(2893, 3948, 0));

		sneakArea1 = new Zone(new WorldPoint(2880, 3922, 0), new WorldPoint(2893, 3935, 0));
		sneakArea2 = new Zone(new WorldPoint(2866, 3921, 0), new WorldPoint(2879, 3928, 0));
		sneakArea3 = new Zone(new WorldPoint(2852, 3917, 0), new WorldPoint(2865, 3931, 0));
		sneakArea4 = new Zone(new WorldPoint(2853, 3923, 0), new WorldPoint(2862, 3938, 0));
		sneakArea5 = new Zone(new WorldPoint(2848, 3939, 0), new WorldPoint(2862, 3948, 0));

		cave1 = new Zone(new WorldPoint(2699, 5795, 0), new WorldPoint(2709, 5807, 0));
		cave2 = new Zone(new WorldPoint(2700, 5781, 0), new WorldPoint(2709, 5794, 0));
		cave3 = new Zone(new WorldPoint(2731, 5779, 0), new WorldPoint(2738, 5791, 0));
		cave4 = new Zone(new WorldPoint(2734, 5809, 0), new WorldPoint(2740, 5816, 0));

		water1 = new Zone(new WorldPoint(2710, 5777, 0), new WorldPoint(2716, 5785, 0));
		water2 = new Zone(new WorldPoint(2717, 5777, 0), new WorldPoint(2730, 5785, 0));
		water3 = new Zone(new WorldPoint(2731, 5792, 0), new WorldPoint(2741, 5809, 0));

		water4 = new Zone(new WorldPoint(2726, 5783, 0), new WorldPoint(2730, 5784, 0));

		weissPrison = new Zone(new WorldPoint(2830, 10326, 0), new WorldPoint(2856, 10351, 0));
	}

	public void setupConditions()
	{
		inStrongholdFloor1 = new ZoneRequirement(strongholdFloor1);
		inStrongholdFloor2 = new ZoneRequirement(strongholdFloor2);
		inPrison = new ZoneRequirement(prison);
		onRoof = new ZoneRequirement(roof);
		inWeissArrivalArea = new ZoneRequirement(weissArrivalArea);
		isOutsideWeiss = new ZoneRequirement(outsideWeiss1, outsideWeiss2, outsideWeiss3, outsideWeiss4, outsideWeiss5);
		inWeiss = new ZoneRequirement(outsideWeiss1, outsideWeiss2, outsideWeiss3, outsideWeiss4, outsideWeiss5, weiss);
		inInstance = new InInstanceRequirement();

		inSneakArea1 = new ZoneRequirement(sneakArea1);
		inSneakArea2 = new ZoneRequirement(sneakArea2);
		inSneakArea3 = new ZoneRequirement(sneakArea3);
		inSneakArea4 = new ZoneRequirement(sneakArea4);
		inSneakArea5 = new ZoneRequirement(sneakArea5);

		onCliff1 = new ZoneRequirement(cliff1);
		onCliff2 = new ZoneRequirement(cliff2);
		onCliff3 = new ZoneRequirement(cliff3);
		onCliff4 = new ZoneRequirement(cliff4);
		onCliff5 = new ZoneRequirement(cliff5);

		inCave1 = new ZoneRequirement(cave1);
		inCave2 = new ZoneRequirement(cave2);
		inCave3 = new ZoneRequirement(cave3);
		inCave4 = new ZoneRequirement(cave4);

		inWater1 = new ZoneRequirement(water1);
		inWater2 = new ZoneRequirement(water2);
		inWater3 = new ZoneRequirement(water3);
		inWater4 = new ZoneRequirement(water4);

		inWeissPrison = new ZoneRequirement(weissPrison);

		// 33240, 33242, 33244, 33240, 33242
		// 33241, 33243, 33245, 33241, 33243

		rockR0C0 = new ObjectCondition(ObjectID.STEPPING_STONE_33240, new WorldPoint(2737, 5804, 0));
		rockR0C1 = new ObjectCondition(ObjectID.STEPPING_STONE_33242, new WorldPoint(2737, 5805, 0));
		rockR0C2 = new ObjectCondition(ObjectID.STEPPING_STONE_33244, new WorldPoint(2737, 5806, 0));
		rockR0C3 = new ObjectCondition(ObjectID.STEPPING_STONE_33240, new WorldPoint(2737, 5807, 0));
		rockR0C4 = new ObjectCondition(ObjectID.STEPPING_STONE_33242, new WorldPoint(2737, 5808, 0));
		row0Made = new Conditions(rockR0C0, rockR0C1, rockR0C2, rockR0C3, rockR0C4);

		rockR1C0 = new ObjectCondition(ObjectID.STEPPING_STONE_33241, new WorldPoint(2738, 5804, 0));
		rockR1C1 = new ObjectCondition(ObjectID.STEPPING_STONE_33243, new WorldPoint(2738, 5805, 0));
		rockR1C2 = new ObjectCondition(ObjectID.STEPPING_STONE_33245, new WorldPoint(2738, 5806, 0));
		rockR1C3 = new ObjectCondition(ObjectID.STEPPING_STONE_33241, new WorldPoint(2738, 5807, 0));
		rockR1C4 = new ObjectCondition(ObjectID.STEPPING_STONE_33243, new WorldPoint(2738, 5808, 0));
		row1Made = new Conditions(rockR1C0, rockR1C1, rockR1C2, rockR1C3, rockR1C4);

		rockR2C0 = new ObjectCondition(ObjectID.STEPPING_STONE_33242, new WorldPoint(2739, 5804, 0));
		rockR2C1 = new ObjectCondition(ObjectID.STEPPING_STONE_33244, new WorldPoint(2739, 5805, 0));
		rockR2C2 = new ObjectCondition(ObjectID.STEPPING_STONE_33240, new WorldPoint(2739, 5806, 0));
		rockR2C3 = new ObjectCondition(ObjectID.STEPPING_STONE_33242, new WorldPoint(2739, 5807, 0));
		rockR2C4 = new ObjectCondition(ObjectID.STEPPING_STONE_33244, new WorldPoint(2739, 5808, 0));
		row2Made = new Conditions(rockR2C0, rockR2C1, rockR2C2, rockR2C3, rockR2C4);


		rowMade = new Conditions(LogicType.OR, row0Made, row1Made, row2Made);
		// Started, 2796 1->0
		// 6528 1->5

		// Random at entrance, 4371 0->9
		// 4372 0->1


		// 6536 0->1 coffin out
		// 6530 2->0 asked about potion
		// 6540 0->1 made potion

		// 2098 200 -> 205 (SWAN SONG???) when WOM dies

		pickedUpWom = new VarbitRequirement(6536, 0);

		oddMushroomDied = new VarbitRequirement(6528, 150, Operation.GREATER_EQUAL);
		defeatedBoss1 = new VarbitRequirement(6528, 160, Operation.GREATER_EQUAL);
		hasPutOutFire = new VarbitRequirement(6528, 170, Operation.GREATER_EQUAL);
		firstBossNearby = new NpcCondition(NpcID.DONT_KNOW_WHAT_8439);
		secondBossNearby = new Conditions(LogicType.OR, new NpcCondition(NpcID.MOTHER_8428), new NpcCondition(NpcID.MOTHER_8429), new NpcCondition(NpcID.MOTHER_8430));
	}

	public void setupSteps()
	{
		enterStronghold = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Talk to Burntmeat in the Troll Stronghold's kitchen.");

		goDownToBurntmeat = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Go down the south staircase.");
		goDownToBurntmeat.setWorldMapPoint(new WorldPoint(2971, 10115, 1));

		goUpToBurntmeat = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Go up the stairs from the prison.");
		goUpToBurntmeat.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		talkToBurntmeat = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1), "Talk to Burntmeat in the Troll Stronghold's kitchen.");
		talkToBurntmeat.setWorldMapPoint(new WorldPoint(2911, 10087, 1));
		talkToBurntmeat.addSubSteps(enterStronghold, goDownToBurntmeat, goUpToBurntmeat);
		talkToBurntmeat.addDialogSteps("Yes, I'll take your quest.", "Why in the heck would you choose My Arm?");

		enterStrongholdAfterStart = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Talk to My Arm on the roof of the Troll Stronghold.");

		goUpAfterStart = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Talk to My Arm on the roof of the Troll Stronghold.");
		goUpAfterStart.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		goUpFromF1ToMyArm = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10052, 1), "Talk to My Arm on the roof of the Troll Stronghold.");
		goUpFromF1ToMyArm.setWorldMapPoint(new WorldPoint(2907, 10083, 1));
		goUpToMyArmAfterStart = new ObjectStep(this, ObjectID.TROLL_LADDER_18834, new WorldPoint(2831, 10077, 2), "Talk to My Arm on the roof of the Troll Stronghold.");
		goUpToMyArmAfterStart.setWorldMapPoint(new WorldPoint(2959, 10140, 0));
		talkToMyArmUpstairs = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2830, 3698, 0), "Talk to My Arm on the roof of the Troll Stronghold.");
		talkToMyArmUpstairs.addDialogSteps("I'm doing another quest for Burntmeat.", "Wolfbone said we should go by sea.");
		talkToMyArmUpstairs.addSubSteps(enterStrongholdAfterStart, goUpAfterStart, goUpFromF1ToMyArm, goUpToMyArmAfterStart);

		talkToLarry = new NpcStep(this, NpcID.LARRY_828, new WorldPoint(2707, 3733, 0), "Talk to Larry north east of Rellekka.");
		talkToLarry.addDialogSteps("Can I transport My Arm in your boat?", "Never mind, I just need to borrow your boat.");
		talkToLarryAgain = new NpcStep(this, NpcID.LARRY_828, new WorldPoint(2707, 3733, 0), "Talk to Larry again.");
		talkToLarryAgain.addDialogSteps("Can I transport My Arm in your boat?", "Never mind, I just need to borrow your boat.");

		boardBoat = new ObjectStep(this, NullObjectID.NULL_21176, new WorldPoint(2709, 3735, 0), "Board Larry's boat to Weiss.");
		boardBoat.addDialogStep("Travel to Weiss.");
		// 6719 0->2 on boat

		attemptToMine = new ObjectStep(this, NullObjectID.NULL_33329, new WorldPoint(2859, 3967, 0), "Attempt to mine the cave entrance.");
		searchBoatForRopeAndPickaxe = new ObjectStep(this, ObjectID.WRECKED_BOAT, new WorldPoint(2856, 3971, 0), "Search the wrecked boat for a pickaxe and rope.");
		searchBoatForRopeAndPickaxe.addDialogSteps("Take rope", "Take pickaxe");
		searchBoatForPickaxe = new ObjectStep(this, ObjectID.WRECKED_BOAT, new WorldPoint(2856, 3971, 0), "Search the wrecked boat for a pickaxe.");
		searchBoatForPickaxe.addDialogStep("Take pickaxe");
		searchBoatForRope = new ObjectStep(this, ObjectID.WRECKED_BOAT, new WorldPoint(2856, 3971, 0), "Search the wrecked boat for a rope.");
		searchBoatForRope.addDialogStep("Take rope");
		climbRocks = new ObjectStep(this, ObjectID.ROCKSLIDE_33184, new WorldPoint(2852, 3965, 0), "Climb the rockslide.");

		climbRocks2 = new ObjectStep(this, ObjectID.ROCKSLIDE_33185, new WorldPoint(2853, 3964, 0), "Climb the rockslide.");
		climbRocks.addSubSteps(climbRocks2);
		useRope = new ObjectStep(this, NullObjectID.NULL_33327, new WorldPoint(2853, 3962, 0), "Use a rope on the nearby tree.", ropeHighlight);
		useRope.addIcon(ItemID.ROPE);

		climbRope = new ObjectStep(this, NullObjectID.NULL_33328, new WorldPoint(2855, 3963, 0), "Climb the rope.");
		crossLedge = new ObjectStep(this, ObjectID.LEDGE_33190, new WorldPoint(2855, 3961, 0), "Cross the ledge.");
		climbRocks3 = new ObjectStep(this, ObjectID.ROCKSLIDE_33191, new WorldPoint(2859, 3961, 0), "Continue climbing.");
		passTree = new ObjectStep(this, ObjectID.FALLEN_TREE_33192, new WorldPoint(2857, 3956, 0), "Pass the fallen tree.");

		talkToBoulder = new NpcStep(this, NpcID.BOULDER_8442, new WorldPoint(2865, 3947, 0), "Talk to Boulder.");
		talkToBoulder.addDialogStep("You only need to let My Arm in, not me.");
		crossFence = new ObjectStep(this, ObjectID.FENCE_33219, new WorldPoint(2890, 3948, 0), "Sneak into Weiss via the fence to the east.");
		crossFence.addDialogStep("I'll be back!");

		goSouthSneak = new TileStep(this, new WorldPoint(2887, 3935, 0), "You need to reach the hole at the north west " +
			"corner, avoiding the trolls and the boulders they throw. Go south, then west, then north.");
		goWestSneak1 = new TileStep(this, new WorldPoint(2879, 3922, 0), "Wait a few seconds, then run west to the next point.");
		goWestSneak2 = new TileStep(this, new WorldPoint(2865, 3928, 0), "Wait a few seconds, then run west to the next point.");
		goWestSneak3 = new TileStep(this, new WorldPoint(2856, 3923, 0), "Wait a few seconds, then run west to the next point.");
		goNorth = new TileStep(this, new WorldPoint(2859, 3939, 0), "Wait a few seconds, then run north to the next point.");

		enterHole = new ObjectStep(this, ObjectID.HOLE_33227, new WorldPoint(2854, 3944,0), "Wait a few seconds, and run into the hole.");
		goSouthSneak.addSubSteps(goWestSneak1, goWestSneak2, goWestSneak3, goNorth, enterHole);
		enterNarrowHole = new ObjectStep(this, ObjectID.NARROW_GAP, new WorldPoint(2704, 5794, 0), "Enter the narrow gap to the south quickly.");

		enterWater = new ObjectStep(this, ObjectID.WATERS_EDGE, new WorldPoint(2710, 5782, 0), "Enter the water.");

		waterSpot1 = new DetailedQuestStep(this, new WorldPoint(2717, 5780, 0), "Swim across the water.");
		leaveWater1 = new ObjectStep(this, ObjectID.WATERS_EDGE, new WorldPoint(2730, 5781, 0), "Wait a few seconds, then swim to the east shore.");
		waterSpot1.addSubSteps(enterWater, leaveWater1);

		enterWater2 = new ObjectStep(this,  NullObjectID.NULL_33331, new WorldPoint(2734, 5792, 0), "Enter the water.");

		mineCave = new ObjectStep(this, ObjectID.CAVE_EXIT_33247, new WorldPoint(2737, 5817, 0), "Mine the cave exit.", pickaxe);

		placeRocks = new DetailedQuestStep(this, "You now need to make a path with the stones thrown at you in the water. Have the troll throw 5 rocks next to each other going out from the north shore.");
		placeRocks.addSubSteps(enterWater2);

		talkToMother = new NpcStep(this, NpcID.MOTHER_8426, new WorldPoint(2872, 3932, 0), "Talk to Mother in Weiss.");
		talkToMother.addDialogSteps("Let's move on with the chat.", "Tell him goutweed is delicious.", "Tell him how tough Stronghold trolls are.",
			"Tell him to show respect to his daughter.", "Tell him you love his daughter!");

		talkToMyArmAfterMeeting = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2877, 3947, 0), "Talk to My Arm in Weiss.");
		talkToMyArmAfterMeeting.addDialogSteps("Odd Mushroom, why are you here with us?", "Does Mother respect anything except fighting?", "I did some fishing and fetched some stuff.");

		talkToWom = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3088, 3255, 0), "Talk to the Wise Old Man in Draynor Village.");
		talkToWom.addDialogSteps("Ask about My Arm.", "Can you pretend you're dead?", "You owe me a favour after the Fishing Colony quest.");

		buildCoffin = new ObjectStep(this, NullObjectID.NULL_33332, new WorldPoint(3090, 3254, 0), "Build the coffin in the Wise Old Man's house.", saw, hammer, mahogPlanks5, boltOfCloth);

		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3196, 3403, 0), "Talk to the Apothecary in Varrock.", cadavaBerries);
		talkToApoth.addDialogStep("Talk about Making Friends with My Arm.");

		talkToWomAfterPrep = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3088, 3255, 0), "Bring the cadava potion to the Wise Old Man in Draynor Village.", potion);
		talkToWomAfterPrep.addDialogStep("Ask about My Arm.");

		pickUpCoffin = new ObjectStep(this, NullObjectID.NULL_33332, new WorldPoint(3090, 3254, 0), "Pick up the coffin in the Wise Old Man's house.");

		takeBoatWithWom = new ObjectStep(this, NullObjectID.NULL_21176, new WorldPoint(2709, 3735, 0), "Board Larry's boat to Weiss.", coffin, combatRangeMelee);
		takeBoatWithWom.addDialogStep("Travel to Weiss.");

		enterCaveWithWom = new ObjectStep(this, NullObjectID.NULL_33329, new WorldPoint(2859, 3967, 0), "Enter the cave entrance.", coffin, combatRangeMelee);

		talkToMyArmWithWom = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2877, 3947, 0), "Talk to My Arm in Weiss.", coffin, combatRangeMelee);

		talkToMyArmAfterGivingWom = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2877, 3947, 0), "Talk to My Arm in Weiss.");
		talkToMyArmWithWom.addSubSteps(takeBoatWithWom, enterCaveWithWom, talkToMyArmAfterGivingWom);

		takeBoatToPrison = new ObjectStep(this, NullObjectID.NULL_21176, new WorldPoint(2709, 3735, 0), "Board Larry's boat to Weiss.");
		takeBoatToPrison.addDialogStep("Travel to Weiss.");

		enterCaveToPrison = new ObjectStep(this, NullObjectID.NULL_33329, new WorldPoint(2859, 3967, 0), "Enter the cave entrance. Be prepared to fight.", combatRangeMelee);
		talkToBoulderToEnterPrison = new NpcStep(this, NpcID.BOULDER_8442, new WorldPoint(2865, 3947, 0), "Talk to Boulder in Weiss. Be prepared to fight.", combatRangeMelee);

		talkToOddMushroom = new NpcStep(this, NpcID.ODD_MUSHROOM_8435, new WorldPoint(2852, 10332, 0), "Talk to Odd Mushroom in the prison.");
		talkToOddMushroom.addSubSteps(takeBoatToPrison, enterCaveToPrison, talkToBoulderToEnterPrison);
		talkToOddMushroom.addDialogStep("I'll never leave you.");

		talkToSnowflake = new NpcStep(this, NpcID.SNOWFLAKE, new WorldPoint(2852, 10334, 0), "Talk to Snowflake. Be prepared to fight Don't Know What. Protect from Ranged, and move around to avoid his attacks.", combatRangeMelee);
		talkToSnowflake.addDialogStep("So I can't teleport, and I may lose stuff? Okay.");

		killDontKnowWhat = new NpcStep(this, NpcID.DONT_KNOW_WHAT_8439, "Kill Don't Know What. Move around and Protect from Ranged to avoid his attacks.");

		killMother = new NpcStep(this, NpcID.MOTHER_8430, "Kill Mother. Protect from Ranged, keep moving and keep your distance.");
		killMother.addAlternateNpcs(NpcID.MOTHER_8428, NpcID.MOTHER_8429);

		pickUpBucket = new ObjectStep(this, ObjectID.PILE_OF_BUCKETS, new WorldPoint(2867, 3934, 0), "You now need to put out the fire. Pick up a bucket from the bucket pile.");
		useBucketOnWater = new ObjectStep(this, ObjectID.BARREL_OF_WATER, new WorldPoint(2869, 3933, 0), "Fill the bucket on the barrel of water.", bucketHighlight);
		useBucketOnWater.addIcon(ItemID.BUCKET);

		useBucketOnFire = new ObjectStep(this, NullObjectID.NULL_33333, new WorldPoint(2876, 3933, 0), "Use the full bucket on the Fire of Domination.", bucketOfWaterHighlight);
		useBucketOnFire.addIcon(ItemID.BUCKET_OF_WATER);

		talkToMyArmAfterFight = new NpcStep(this, NpcID.MY_ARM_742, new WorldPoint(2874, 3934, 0), "Talk to My Arm in Weiss.");
		talkToWomAfterFight = new NpcStep(this, NpcID.WISE_OLD_MAN_8407, new WorldPoint(2873, 3936, 0), "Talk to the Wise Old Man in Weiss.");
		talkToSnowflakeAfterFight = new NpcStep(this, NpcID.SNOWFLAKE, new WorldPoint(2872, 3934, 0), "Talk to Snowflake in Weiss.");
		talkToSnowflakeAfterFight.addDialogStep("Okay, I'll be back.");
		pickUpGoatDung = new ObjectStep(this, ObjectID.GOAT_POO_33214, new WorldPoint(2888, 3944, 0), "Pick up some goat poo from the north east building of Weiss.", bucketHighlight);
		pickUpBucketForDung = new ItemStep(this, "Pick up a bucket of water from the north east building of Weiss.", bucketOfWaterHighlight);
		emptyBucket = new DetailedQuestStep(this, "Empty the bucket of water", bucketOfWaterHighlight);
		pickUpGoatDung.addSubSteps(pickUpBucketForDung, emptyBucket);
		talkToSnowflakeAfterDung = new NpcStep(this, NpcID.SNOWFLAKE, new WorldPoint(2872, 3934, 0), "Talk to Snowflake.");
		bringDungToSnowflake = new NpcStep(this, NpcID.SNOWFLAKE, new WorldPoint(2872, 3934, 0), "Bring the goat poo to Snowflake.", goatDung);
		bringDungToSnowflake.addSubSteps(talkToSnowflakeAfterDung);

		readNotes = new DetailedQuestStep(this, "Read the Weiss fire notes.", fireNotes);

		talkToSnowflakeToFinish = new NpcStep(this, NpcID.SNOWFLAKE, new WorldPoint(2872, 3934, 0), "Talk to Snowflake to finish.");
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(draynorTele, trollTele, varrockTele);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(hammer, saw, boltOfCloth, mahogPlanks5, cadavaBerries, combatRangeMelee);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Don't Know What (level 163)", "Mother (level 198)");
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
				new ExperienceReward(Skill.CONSTRUCTION, 10000),
				new ExperienceReward(Skill.FIREMAKING, 40000),
				new ExperienceReward(Skill.MINING, 50000),
				new ExperienceReward(Skill.AGILITY, 50000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Salt Mines."),
				new UnlockReward("Ability to build fire pits"),
				new UnlockReward("Ability to tune a house portal to Troll Stronghold."),
				new UnlockReward("Access to a disease free herb patch in Weiss."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToBurntmeat, talkToMyArmUpstairs)));
		allSteps.add(new PanelDetails("Getting to Weiss", Arrays.asList(talkToLarry, talkToLarryAgain, boardBoat, attemptToMine,
			searchBoatForRopeAndPickaxe, climbRocks, useRope, climbRope, crossLedge, climbRocks3, passTree, talkToBoulder)));
		allSteps.add(new PanelDetails("Infiltrating Weiss", Arrays.asList(crossFence, goSouthSneak, enterHole, enterNarrowHole,
			waterSpot1, placeRocks, mineCave, talkToMother, talkToMyArmAfterMeeting), pickaxe));
		allSteps.add(new PanelDetails("Faking death", Arrays.asList(talkToWom, buildCoffin, talkToApoth, talkToWomAfterPrep, pickUpCoffin),
			saw, hammer, mahogPlanks5, boltOfCloth, cadavaBerries));
		allSteps.add(new PanelDetails("Rising up", Arrays.asList(talkToMyArmWithWom, talkToOddMushroom, talkToSnowflake, killDontKnowWhat,
			pickUpBucket, useBucketOnWater, useBucketOnFire, killMother), coffin, combatRangeMelee));
		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(talkToMyArmAfterFight, talkToWomAfterFight, talkToSnowflakeAfterFight,
			pickUpGoatDung, bringDungToSnowflake, readNotes, talkToSnowflakeToFinish)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.MY_ARMS_BIG_ADVENTURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SWAN_SONG, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.COLD_WAR, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ROMEO__JULIET, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.FIREMAKING, 66));
		req.add(new SkillRequirement(Skill.MINING, 72, true));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 35, true));
		req.add(new SkillRequirement(Skill.AGILITY, 68, true, " 68 Agility (but higher is better)"));
		return req;
	}
}
