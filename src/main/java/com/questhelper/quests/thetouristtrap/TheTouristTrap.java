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
package com.questhelper.quests.thetouristtrap;

import com.questhelper.BankSlotIcons;
import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.VarplayerCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
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
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_TOURIST_TRAP
)
public class TheTouristTrap extends BasicQuestHelper
{
	ItemRequirement combatGear, desertTop, desertBottom, desertBoot, desertTopWorn, desertBottomWorn, desertBootWorn, bronzeBar3, hammer, feather50,
		metalKey, slaveTop, slaveRobe, slaveBoot, slaveTopWorn, slaveRobeWorn, slaveBootWorn, bedabinKey, technicalPlans, prototypeDart, prototypeDartTip,
		feather10, bronzeBar, tentiPineapple, bronzeBarHighlighted, barrel, anaInABarrel, anaInABarrelHighlighted, waterskins, knife, pickaxe, coins100,
		barrelHighlighted;

	ConditionForStep inJail, onSlope, inCamp, hasSlaveClothes, inUpstairs, onCliff, onSecondCliff, inJailEscape, inMine1, hasPlans, distractedSiad, searchedBookcase,
		hasTip, hasDart, hasBarrel, inDeepMine, inDeepMineP1, inDeepMineP2, hasAnaInBarrel, anaOnCart, anaOnSurfaceInBarrel, anaOnSurface, anaPlacedOnCartOfLift,
		inMiningRoom, anaFree;

	DetailedQuestStep talkToIrena, talkToCaptain, killCaptain, enterCamp, talkToSlave, enterMine, talkToGuard, leaveCamp, talkToShabim, enterCampForTask,
		goUpToSiad, searchBookcase, talkToSiad, searchChest, returnToShabim, useAnvil, bringPrototypeToShabim, enterCampWithPineapple, enterMineWithPineapple,
		talkToGuardWithPineapple, enterCampAfterPineapple, enterMineAfterPineapple, enterDeepMine, getBarrel, enterMineCart, useBarrelOnAna, useBarrelOnMineCart,
		returnInMineCart, searchBarrelsForAna, sendAnaUp, leaveDeepMine, leaveMineForAna, operateWinch, searchWinchBarrel, useBarrelOnCart, talkToDriver,
		returnToIrena, useFeatherOnTip, leaveMine, mineRocks, talkToAna, talkToIrenaToFinish;

	DetailedQuestStep escapeJail, climbSlope, climbCliff, climbDownCliff;

	Zone jail, slope, camp, upstairs, cliff, secondCliff, mine1, deepMine, deepMineP1, deepMineP2P1, deepMineP2P2, miningRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToIrena);
		steps.put(1, talkToCaptain);
		steps.put(2, talkToCaptain);
		steps.put(3, talkToCaptain);

		ConditionalStep escapeJailSteps = new ConditionalStep(this, escapeJail);
		escapeJailSteps.addStep(onSecondCliff, climbDownCliff);
		escapeJailSteps.addStep(onCliff, climbCliff);
		escapeJailSteps.addStep(onSlope, climbSlope);

		ConditionalStep getSlaveClothes = new ConditionalStep(this, enterCamp);
		getSlaveClothes.addStep(inJailEscape, escapeJailSteps);
		getSlaveClothes.addStep(inCamp, talkToSlave);
		steps.put(4, getSlaveClothes);
		steps.put(5, getSlaveClothes);
		steps.put(6, getSlaveClothes);
		steps.put(7, getSlaveClothes);

		ConditionalStep goTalkToGuard = new ConditionalStep(this, enterCamp);
		goTalkToGuard.addStep(inJailEscape, escapeJailSteps);
		goTalkToGuard.addStep(inMine1, talkToGuard);
		goTalkToGuard.addStep(inCamp, enterMine);
		steps.put(8, goTalkToGuard);
		steps.put(9, goTalkToGuard);

		ConditionalStep goTalkToShabim = new ConditionalStep(this, talkToShabim);
		goTalkToShabim.addStep(inJailEscape, escapeJailSteps);
		goTalkToShabim.addStep(inCamp, leaveCamp);
		goTalkToShabim.addStep(inMine1, leaveMine);
		steps.put(10, goTalkToShabim);

		ConditionalStep goGetPlans = new ConditionalStep(this, enterCampForTask);
		goGetPlans.addStep(inJailEscape, escapeJailSteps);
		goGetPlans.addStep(hasPlans, returnToShabim);
		goGetPlans.addStep(new Conditions(inUpstairs, distractedSiad), searchChest);
		goGetPlans.addStep(new Conditions(inUpstairs, searchedBookcase), talkToSiad);
		goGetPlans.addStep(inUpstairs, searchBookcase);
		goGetPlans.addStep(inCamp, goUpToSiad);
		goGetPlans.addStep(inMine1, leaveMine);
		steps.put(11, goGetPlans);
		steps.put(12, goGetPlans);

		ConditionalStep makeDart = new ConditionalStep(this, useAnvil);
		makeDart.addStep(inJailEscape, escapeJailSteps);
		makeDart.addStep(hasDart, bringPrototypeToShabim);
		makeDart.addStep(hasTip, useFeatherOnTip);
		steps.put(13, makeDart);
		steps.put(14, makeDart);
		steps.put(15, makeDart);

		ConditionalStep returnWithPineapple = new ConditionalStep(this, enterCampWithPineapple);
		returnWithPineapple.addStep(inJailEscape, escapeJailSteps);
		returnWithPineapple.addStep(inMine1, talkToGuardWithPineapple);
		returnWithPineapple.addStep(inCamp, enterMineWithPineapple);
		steps.put(16, returnWithPineapple);

		ConditionalStep captureAna = new ConditionalStep(this, enterCampAfterPineapple);
		captureAna.addStep(inJailEscape, escapeJailSteps);
		captureAna.addStep(new Conditions(anaFree), talkToAna);
		captureAna.addStep(new Conditions(inCamp, anaOnCart), talkToDriver);
		captureAna.addStep(new Conditions(inCamp, hasAnaInBarrel), useBarrelOnCart);
		captureAna.addStep(new Conditions(inCamp, anaPlacedOnCartOfLift, anaOnSurfaceInBarrel), searchWinchBarrel);
		captureAna.addStep(new Conditions(inCamp, anaOnSurface, anaPlacedOnCartOfLift), operateWinch);
		captureAna.addStep(new Conditions(inMine1, anaOnSurface, anaPlacedOnCartOfLift), leaveMineForAna);
		captureAna.addStep(new Conditions(inDeepMineP1, hasAnaInBarrel), sendAnaUp);
		captureAna.addStep(new Conditions(inDeepMineP1, anaOnSurface, anaPlacedOnCartOfLift), leaveDeepMine);
		captureAna.addStep(new Conditions(inDeepMineP1, anaPlacedOnCartOfLift), searchBarrelsForAna);
		captureAna.addStep(new Conditions(inDeepMineP2, hasAnaInBarrel), useBarrelOnMineCart);
		captureAna.addStep(new Conditions(inDeepMineP2, anaPlacedOnCartOfLift), returnInMineCart);
		captureAna.addStep(new Conditions(inDeepMineP2, hasBarrel), useBarrelOnAna);
		captureAna.addStep(new Conditions(inDeepMineP1, hasBarrel), enterMineCart);
		captureAna.addStep(inMiningRoom, mineRocks);
		captureAna.addStep(hasAnaInBarrel, returnToIrena);
		captureAna.addStep(inDeepMine, getBarrel);
		captureAna.addStep(inMine1, enterDeepMine);
		captureAna.addStep(inCamp, enterMineAfterPineapple);
		steps.put(17, captureAna);
		steps.put(18, captureAna);
		steps.put(19, captureAna);
		steps.put(20, captureAna);
		steps.put(21, captureAna);
		steps.put(22, captureAna);
		steps.put(23, captureAna);
		steps.put(24, captureAna);
		steps.put(25, captureAna);
		steps.put(26, captureAna);

		steps.put(27, talkToIrenaToFinish);
		steps.put(28, talkToIrenaToFinish);
		steps.put(29, talkToIrenaToFinish);

		return steps;
	}

	public void setupItemRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		desertTop = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT);
		desertBottom = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE);
		desertBoot = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS);
		desertTopWorn = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT, 1, true);
		desertBottomWorn = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE, 1, true);
		desertBootWorn = new ItemRequirement("Desert boots", ItemID.DESERT_BOOTS, 1, true);
		bronzeBar3 = new ItemRequirement("Bronze bars", ItemID.BRONZE_BAR, 3);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		feather50 = new ItemRequirement("Feather", ItemID.FEATHER, 50);

		metalKey = new ItemRequirement("Metal key", ItemID.METAL_KEY);
		metalKey.setTip("You can get another by killing the Mercenary Guard outside the Desert Mining Camp");
		slaveTop = new ItemRequirement("Slave shirt", ItemID.SLAVE_SHIRT);
		slaveTop.setTip("You can trade in a desert robe set for slave clothes with the Male Slave");
		slaveTopWorn = new ItemRequirement("Slave shirt", ItemID.SLAVE_SHIRT, 1, true);
		slaveTopWorn.setTip("You can trade in a desert robe set for slave clothes with the Male Slave");
		slaveRobe = new ItemRequirement("Slave robe", ItemID.SLAVE_ROBE);
		slaveRobe.setTip("You can trade in a desert robe set for slave clothes with the Male Slave");
		slaveRobeWorn = new ItemRequirement("Slave robe", ItemID.SLAVE_ROBE, 1, true);
		slaveRobeWorn.setTip("You can trade in a desert robe set for slave clothes with the Male Slave");
		slaveBoot = new ItemRequirement("Slave boots", ItemID.SLAVE_BOOTS);
		slaveBoot.setTip("You can trade in a desert robe set for slave clothes with the Male Slave");
		slaveBootWorn = new ItemRequirement("Slave boots", ItemID.SLAVE_BOOTS, 1, true);
		slaveBootWorn.setTip("You can trade in a desert robe set for slave clothes with the Male Slave");

		bedabinKey = new ItemRequirement("Bedabin key", ItemID.BEDABIN_KEY);
		bedabinKey.setTip("You can get another from Al Shabim in the Bedabin Camp");
		technicalPlans = new ItemRequirement("Technical plans", ItemID.TECHNICAL_PLANS);
		technicalPlans.setTip("You'll need to get another plan from Siad's chest");
		prototypeDart = new ItemRequirement("Prototype dart", ItemID.PROTOTYPE_DART);
		prototypeDartTip = new ItemRequirement("Prototype dart tip", ItemID.PROTOTYPE_DART_TIP);
		prototypeDartTip.setHighlightInInventory(true);

		feather10 = new ItemRequirement("Feather", ItemID.FEATHER, 10);
		feather10.setHighlightInInventory(true);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		bronzeBarHighlighted = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		bronzeBarHighlighted.setHighlightInInventory(true);

		tentiPineapple = new ItemRequirement("Tenti pineapple", ItemID.TENTI_PINEAPPLE);
		tentiPineapple.setTip("You can get another from Al Shabim in the Bedabin Camp");

		barrel = new ItemRequirement("Barrel", ItemID.BARREL);
		barrelHighlighted = new ItemRequirement("Barrel", ItemID.BARREL);
		barrelHighlighted.setHighlightInInventory(true);
		anaInABarrel = new ItemRequirement("Ana in a barrel", ItemID.ANA_IN_A_BARREL);
		anaInABarrelHighlighted = new ItemRequirement("Ana in a barrel", ItemID.ANA_IN_A_BARREL);
		anaInABarrelHighlighted.setHighlightInInventory(true);

		waterskins = new ItemRequirement("Waterskins, preferably more", ItemID.WATERSKIN4);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		coins100 = new ItemRequirement("Coins", ItemID.COINS_995, 100);
	}

	public void loadZones()
	{
		jail = new Zone(new WorldPoint(3284, 3031, 0), new WorldPoint(3287, 3037, 0));
		camp = new Zone(new WorldPoint(3274, 3014, 0), new WorldPoint(3305, 3037, 1));
		upstairs = new Zone(new WorldPoint(3284, 3031, 1), new WorldPoint(3293, 3037, 1));
		slope = new Zone(new WorldPoint(3282, 3032, 0), new WorldPoint(3283, 3037, 0));
		cliff = new Zone(new WorldPoint(3279, 3037, 0), new WorldPoint(3281, 3038, 0));
		secondCliff = new Zone(new WorldPoint(3273, 3035, 0), new WorldPoint(3278, 3039, 0));
		mine1 = new Zone(new WorldPoint(3266,9410, 0), new WorldPoint(3282, 9466, 0));
		deepMine = new Zone(new WorldPoint(3282, 9408, 0), new WorldPoint(3326, 9470, 0));
		deepMineP1 = new Zone(new WorldPoint(3283, 9409, 0), new WorldPoint(3314, 9427, 0));
		deepMineP2P1 = new Zone(new WorldPoint(3315, 9416, 0), new WorldPoint(3326, 9470, 0));
		deepMineP2P2 = new Zone(new WorldPoint(3293, 9429, 0), new WorldPoint(3314, 9470, 0));
		miningRoom = new Zone(new WorldPoint(3283, 9427, 0), new WorldPoint(3292, 9454, 0));
	}

	public void setupConditions()
	{
		inJail = new ZoneCondition(jail);
		inCamp = new ZoneCondition(camp);
		inMine1 = new ZoneCondition(mine1);
		inUpstairs = new ZoneCondition(upstairs);
		inDeepMine = new ZoneCondition(deepMine);
		inDeepMineP1 = new ZoneCondition(deepMineP1);
		inDeepMineP2 = new ZoneCondition(deepMineP2P1, deepMineP2P2);
		inMiningRoom = new ZoneCondition(miningRoom);

		onSlope = new ZoneCondition(slope);
		onCliff = new ZoneCondition(cliff);
		onSecondCliff = new ZoneCondition(secondCliff);
		inJailEscape = new ZoneCondition(jail, slope, cliff, secondCliff);

		hasSlaveClothes = new ItemRequirementCondition(slaveTop, slaveBoot, slaveRobe);
		hasPlans = new ItemRequirementCondition(technicalPlans);
		hasTip = new ItemRequirementCondition(prototypeDartTip);
		hasDart = new ItemRequirementCondition(prototypeDart);
		hasBarrel = new ItemRequirementCondition(barrel);
		hasAnaInBarrel = new ItemRequirementCondition(anaInABarrel);

		searchedBookcase = new Conditions(true, new WidgetTextCondition(WidgetInfo.DIALOG_SPRITE_TEXT, "You notice several books on the subject of sailing."));
		distractedSiad = new Conditions(true, new WidgetTextCondition(229, 1, "The captain starts rambling on about his days as a salty sea dog. He<br>looks quite distracted..."));

		anaPlacedOnCartOfLift = new VarbitCondition(2805, 1);
		// TODO: Better detection of if Ana is on the surface or in the underground barrel
		anaOnSurface = new VarplayerCondition(197, 22, Operation.GREATER_EQUAL);
		// TODO: This only gets set the first time. If you somehow lose Ana between here and the cart it remains set. Need to add more logic around this
		anaOnSurfaceInBarrel = new VarbitCondition(2808, 1);
		anaOnCart = new VarbitCondition(2809, 1);
		anaFree = new VarbitCondition(3733, 1);
	}

	public void setupSteps()
	{
		talkToIrena = new NpcStep(this, NpcID.IRENA, new WorldPoint(3304, 3112, 0), "Talk to Irena south of the Shanty Pass.");
		talkToIrena.addDialogSteps("What's the matter?", "Is there a reward if I get her back?", "I'll look for your daughter.", "Okay Irena, calm down. I'll get your daughter back for you.", "Yes, I'll go on this quest!");
		talkToCaptain = new NpcStep(this, NpcID.MERCENARY_CAPTAIN, new WorldPoint(3271, 3029, 0), "Talk the Mercenary Captain outside the Desert Mining Camp. When he attacks you, kill him for a key.", combatGear);
		talkToCaptain.addDialogSteps("Wow! A real captain!", "I'd love to work for a tough guy like you!", "Can't I do something for a strong Captain like you?", "Sorry Sir, I don't think I can do that.", "It's a funny captain who can't fight his own battles!");
		killCaptain = new NpcStep(this, NpcID.MERCENARY_CAPTAIN, new WorldPoint(3271, 3029, 0), "Kill the Mercenary Captain outside the Desert Mining Camp.", combatGear);

		escapeJail = new ObjectStep(this, NullObjectID.NULL_2679, new WorldPoint(3283, 3034, 0), "Bend the cell window and escape through it.");
		climbSlope = new ObjectStep(this, ObjectID.ROCK_18871, new WorldPoint(3281, 3037, 0), "Climb the rocks to escape.");
		climbCliff = new ObjectStep(this, ObjectID.CLIFF, new WorldPoint(3278, 3037, 0), "Climb the cliff.");
		climbDownCliff = new ObjectStep(this, ObjectID.CLIFF_18924, new WorldPoint(3273, 3039, 0), "Climb down the west of the cliff.");

		enterCamp = new ObjectStep(this, ObjectID.GATE_2673, new WorldPoint(3273, 3029, 0), "UNEQUIP ALL COMBAT GEAR and enter the camp.", desertTop, desertBottom, desertBoot, metalKey);
		talkToSlave = new NpcStep(this, NpcID.MALE_SLAVE, new WorldPoint(3302, 3027, 0), "Talk to the Male Slave to the east to trade your desert robes for his slave robes.", desertTop, desertBottom, desertBoot);
		talkToSlave.addDialogSteps("I've just arrived.", "Okay, you caught me out.", "Oh yes, that sounds interesting.", "What's that then?", "I can try to undo them for you.", "It's funny you should say that...", "Yeah okay, let's give it a go.", "Yes, I'll trade.");
		enterMine = new ObjectStep(this, ObjectID.MINE_DOOR_ENTRANCE, new WorldPoint(3301, 3036, 0), "Equip the slave clothes, and enter the mine door in the north east of the camp.", slaveTopWorn, slaveRobeWorn, slaveBootWorn);
		talkToGuard = new NpcStep(this, NpcID.GUARD_4667, new WorldPoint(3278, 9415, 0), "Follow the cave around and talk to a guard guarding the deeper cave.", slaveTopWorn, slaveRobeWorn, slaveBootWorn);
		talkToGuard.addDialogSteps("I'd like to mine in a different area.", "Yes sir, you're quite right sir.", "Yes sir, we understand each other perfectly.");
		leaveMine = new ObjectStep(this, ObjectID.MINE_DOOR_ENTRANCE_2690, new WorldPoint(3278, 9426, 0), "Return to the surface.");
		leaveMine.addDialogStep("Yes sir, we understand each other perfectly.");
		leaveCamp = new ObjectStep(this, ObjectID.GATE_2673, new WorldPoint(3273, 3029, 0), "UNEQUIP THE SLAVE CLOTHES and leave the camp.");
		talkToShabim = new NpcStep(this, NpcID.AL_SHABIM, new WorldPoint(3171, 3028, 0), "Talk to Al Shabim in the Bedabin Camp.");
		talkToShabim.addDialogSteps("I am looking for a pineapple.", "Yes, I'm interested.");
		talkToShabim.addSubSteps(leaveMine, leaveCamp);

		enterCampForTask = new ObjectStep(this, ObjectID.GATE_2673, new WorldPoint(3273, 3029, 0), "UNEQUIP ALL COMBAT GEAR and enter the camp.", metalKey, bedabinKey);
		goUpToSiad = new ObjectStep(this, ObjectID.LADDER_18903, new WorldPoint(3290, 3036, 0), "Climb up the ladder in the building in the camp.", bedabinKey);
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_2678, new WorldPoint(3284, 3033, 1), "Search the south west bookcase.");
		talkToSiad = new NpcStep(this, NpcID.CAPTAIN_SIAD, new WorldPoint(3292, 3032, 1), "Talk to Captain Siad about sailing to distract him.", bedabinKey);
		talkToSiad.addDialogSteps("I wanted to have a chat?", "You seem to have a lot of books!", "So, you're interested in sailing?", "I could tell by the cut of your jib.");
		searchChest = new ObjectStep(this, ObjectID.CHEST_2677, new WorldPoint(3292, 3033, 1), "Search the chest.");
		searchChest.addDialogSteps("I wanted to have a chat?", "You seem to have a lot of books!", "So, you're interested in sailing?", "I could tell by the cut of your jib.");
		returnToShabim = new NpcStep(this, NpcID.AL_SHABIM, new WorldPoint(3171, 3028, 0), "Return to Al Shabim in the Bedabin Camp with the plans.", technicalPlans, bronzeBar, hammer, feather10);
		returnToShabim.addDialogSteps("Yes, I'm very interested.", "Yes, I'm kind of curious.");
		useAnvil = new ObjectStep(this, ObjectID.AN_EXPERIMENTAL_ANVIL, new WorldPoint(3171, 3048, 0), "Enter the north tent and attempt to make an prototype dart tip on the anvil.", technicalPlans, bronzeBarHighlighted, hammer, feather10);
		useAnvil.addDialogStep("Yes. I'd like to try.");
		useAnvil.addIcon(ItemID.BRONZE_BAR);
		useFeatherOnTip = new DetailedQuestStep(this, "Add 10 feathers to the prototype dart tip.", prototypeDartTip, feather10);
		bringPrototypeToShabim = new NpcStep(this, NpcID.AL_SHABIM, new WorldPoint(3171, 3028, 0), "Bring the prototype dart to Al Shabim.", prototypeDart);

		enterCampWithPineapple = new ObjectStep(this, ObjectID.GATE_2673, new WorldPoint(3273, 3029, 0), "UNEQUIP ALL COMBAT GEAR and enter the camp.", metalKey, tentiPineapple, slaveTop, slaveRobe, slaveBoot);
		enterMineWithPineapple = new ObjectStep(this, ObjectID.MINE_DOOR_ENTRANCE, new WorldPoint(3301, 3036, 0), "Equip the slave clothes, and enter the mine door in the north east of the camp.", slaveTopWorn, slaveRobeWorn, slaveBootWorn, tentiPineapple);
		talkToGuardWithPineapple = new NpcStep(this, NpcID.GUARD_4667, new WorldPoint(3278, 9415, 0), "Follow the cave around and talk to a guard guarding the deeper cave.", slaveTopWorn, slaveRobeWorn, slaveBootWorn, tentiPineapple);
		enterCampAfterPineapple = new ObjectStep(this, ObjectID.GATE_2673, new WorldPoint(3273, 3029, 0), "UNEQUIP ALL COMBAT GEAR and enter the camp.", metalKey, slaveTop, slaveRobe, slaveBoot);
		enterMineAfterPineapple = new ObjectStep(this, ObjectID.MINE_DOOR_ENTRANCE, new WorldPoint(3301, 3036, 0), "Equip the slave clothes, and enter the mine door in the north east of the camp.", slaveTopWorn, slaveRobeWorn, slaveBootWorn);

		enterDeepMine = new ObjectStep(this, ObjectID.MINE_CAVE, new WorldPoint(3281, 9414, 0), "Enter the deeper mines.");
		getBarrel = new ObjectStep(this, ObjectID.BARREL_2681, new WorldPoint(3303, 9418, 0), "Right-click search the barrel next to the mine cart for a barrel.");
		getBarrel.addDialogStep("Yeah, cool!");
		enterMineCart = new ObjectStep(this, ObjectID.MINE_CART_2684, new WorldPoint(3303, 9417, 0), "Right-click search the mine cart to ride it to the next room.", barrel);
		enterMineCart.addDialogStep("Yes, of course.");
		useBarrelOnAna = new NpcStep(this, NpcID.ANA, new WorldPoint(3297, 9464, 0), "Follow the path west then north until you find Ana. Use the barrel on her.", barrelHighlighted);
		useBarrelOnAna.addIcon(ItemID.BARREL);
		useBarrelOnMineCart = new ObjectStep(this, ObjectID.MINE_CART_2684, new WorldPoint(3318, 9430, 0), "Use Ana on the mine cart you came in on.", anaInABarrelHighlighted);
		useBarrelOnMineCart.addIcon(ItemID.ANA_IN_A_BARREL);
		returnInMineCart = new ObjectStep(this, ObjectID.MINE_CART_2684, new WorldPoint(3318, 9430, 0), "Return in the mine cart you came in on.");
		returnInMineCart.addDialogStep("Yes, of course.");

		searchBarrelsForAna = new ObjectStep(this, ObjectID.BARREL_2681, new WorldPoint(3303, 9418, 0), "Right-click search the barrel next to the mine cart for Ana.");
		sendAnaUp = new ObjectStep(this, ObjectID.WINCH_BUCKET_18951, new WorldPoint(3292, 9423, 0), "Right-click use the winch bucket to send Ana to the surface.");
		sendAnaUp.addDialogSteps("Yes please.", "I said you were very gregarious!");
		leaveDeepMine = new ObjectStep(this, ObjectID.MINE_CAVE, new WorldPoint(3283, 9415, 0), "Return to the surface. If Ana was captured again, search the barrel next to the mine cart for her.");
		leaveMineForAna = new ObjectStep(this, ObjectID.MINE_DOOR_ENTRANCE_2690, new WorldPoint(3278, 9426, 0), "Return to the surface.");
		leaveDeepMine.addSubSteps(leaveMineForAna);
		operateWinch = new ObjectStep(this, ObjectID.WINCH_18888, new WorldPoint(3279, 3018, 0), "Right-click operate the winch in the south west of the camp.");
		searchWinchBarrel = new ObjectStep(this, NullObjectID.NULL_18963, new WorldPoint(3278, 3017, 0), "Right-click search the barrel west of the winch. If it's empty, operate the winch again.");
		useBarrelOnCart = new ObjectStep(this, NullObjectID.NULL_2682, new WorldPoint(3288, 3024, 0), "Use Ana on the cart in the middle of the camp.", anaInABarrelHighlighted);
		useBarrelOnCart.addIcon(ItemID.ANA_IN_A_BARREL);
		talkToDriver = new NpcStep(this, NpcID.MINE_CART_DRIVER, new WorldPoint(3287, 3021, 0), "Talk to the Mine Cart Driver to escape. Once he's agreed to take you, right-click search the wooden cart to escape.");
		talkToDriver.addDialogSteps("Nice cart.", "One wagon wheel says to the other, 'I'll see you around'.", "'One good turn deserves another'", "Fired... no, shot perhaps!",
			"In for a penny in for a pound.", "Well, you see, it's like this...", "Prison riot in ten minutes, get your cart out of here!", "You can't leave me here, I'll get killed!", "Yes, I'll get on.");
		returnToIrena = new NpcStep(this, NpcID.IRENA, new WorldPoint(3304, 3112, 0), "Bring Ana to Irena south of the Shanty Pass.", anaInABarrel);
		talkToAna = new NpcStep(this, NpcID.ANA, new WorldPoint(3302, 3110, 0), "Talk to Ana outside the Shanty Pass.");
		talkToIrenaToFinish = new NpcStep(this, NpcID.IRENA, new WorldPoint(3304, 3112, 0), "Talk to Irena south of the Shanty Pass to finish the quest!");

		mineRocks = new DetailedQuestStep(this, "Mine 15 rocks to be able to leave.");
	}

	@Override
	public ArrayList<String> getNotes()
	{
		return new ArrayList<>(Collections.singletonList("Almost any deviation from the steps listed here will often result in you being thrown into jail, or to an inconvenient location. If you'd wish to avoid this, try to follow the helper to the letter."));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(desertTop, desertBottom, desertBoot, bronzeBar3, hammer, feather50));
	}


	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(combatGear, waterskins, knife, pickaxe, coins100));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Mercenary Captain (level 47)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating the trap",
			new ArrayList<>(Arrays.asList(talkToIrena, talkToCaptain, enterCamp, talkToSlave, enterMine, talkToGuard)),
			desertTop, desertBottom, desertBoot, bronzeBar3, hammer, feather50));
		allSteps.add(new PanelDetails("Helping out",
			new ArrayList<>(Arrays.asList(talkToShabim, enterCampForTask, goUpToSiad, searchBookcase, talkToSiad, searchChest, returnToShabim, useAnvil, useFeatherOnTip, bringPrototypeToShabim)),
			bronzeBar3, hammer, feather50));
		allSteps.add(new PanelDetails("Freeing Ana", new ArrayList<>(
			Arrays.asList(enterCampWithPineapple, enterMineWithPineapple, talkToGuardWithPineapple, enterDeepMine, getBarrel, enterMineCart, useBarrelOnAna, useBarrelOnMineCart,
				returnInMineCart, searchBarrelsForAna, sendAnaUp, leaveDeepMine, operateWinch, searchWinchBarrel, useBarrelOnCart, talkToDriver, returnToIrena, talkToAna, talkToIrenaToFinish)),
			slaveTop, slaveRobe, slaveBoot));

		return allSteps;
	}
}
