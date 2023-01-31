/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.darknessofhallowvale;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	quest = QuestHelperQuest.DARKNESS_OF_HALLOWVALE
)
public class DarknessOfHallowvale extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement airRune, lawRune;

	//Items Required
	ItemRequirement nails8, nails4, planks2, planks1, hammer, pickaxe, knife, doorKey, ladderPiece, message, charcoal, papyrus, sketch1, sketch2, sketch3,
		largeOrnateKey, messageFromFireplace, haemBook, sealedMessage;

	Requirement normalSpellbook;

	Requirement inNewBase, inTemple, inMeiyditch, inMyrequeBase, inMine, inNorthMeiy, inRandomRoom, atBarricade, pushedBoat, onEntryWall, onSecondWall, onThirdWall,
		onFourthWall, onDrakanWalls, inVanstromFight, knockedDownBoard, pathDoorOpen, fixedLadder, wallPressed, searchedRockySurface, hasSketches,
		cutPortrait, handedInSketches, tapestryCut, keyPlaced, hasTeleGrabRunesOrSearchedCase, searchedRuneCase, inLab;

	DetailedQuestStep enterBurghPubBasement, talkToVeliaf, leavePubBasement, usePlankOnBoat, usePlankOnChute, pushBoat, boardBoat;

	DetailedQuestStep kickBoard, climbDownBoard, talkToCitizen, talkToRal, travelToPots, travelToLadderPart, travelToFixLadder, travelToMyrequeBase, pressDecoratedWall, enterRug;

	DetailedQuestStep talkToVertida;

	DetailedQuestStep goDownToDrezel, talkToDrezel, leaveDrezelToBushes, searchBushes, returnFromBushesToDrezel, talkToRoald;

	DetailedQuestStep goToMines, mineDaeyaltThenLeave, returnToMeiyBase;

	DetailedQuestStep goDownFromRandomRoom, climbUpFloor, climbDownWallLadder, searchRockySurface, goThroughBarricade, climbLadderSecondWall, climbDownFromThirdWall,
		climbUpDrakanWalls, talkToSafalaan, drawNorthWall, drawWestWall,
		drawSouthWall, tankVanstrom, finishSouthSketch, talkToSarius, talkToSafalaanInBase;

	DetailedQuestStep useKnifeOnFireplace, readMessage, inspectPortrait, useKnifeOnPortrait, leaveMeiyerBase, useKnifeOnTapestry, useKeyOnStatue, goDownToLab,
		getRunes, telegrabBook, leaveLab;

	ConditionalStep startQuest, goTravelToMyrequeBase, talkToVeliafAfterContact, talkToVeliafAfterLetter, talkToDrezelAfterVeliaf, talkToDrezelAfterBushes,
		talkToVeliafAfterDrezel, returnToMeiyerditch, goToSafalaan, goSketchNorth, goSketchWest, goSketchSouth, goTalkToSarius, goFinishSouthSketch,
		goOpenFireplace, returnToSafalaanInBase, returnToSafalaanInBaseNoSketches, goUnlockLab, getHaemBook, bringSafalaanBook, bringMessageToVeliafToFinish;

	//Zones
	Zone newBase, temple, entryWall, entryWall2, meiyerditch, myrequeBase, mine, northMeiy, randomRoom, barricade1, barricade2, barricade3, secondWall, secondWall2, thirdWall,
		fourthWall, fourthWall2, drakanWalls, drakanWalls2, lab;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);

		ConditionalStep goRepairBoat = new ConditionalStep(this, usePlankOnBoat);
		goRepairBoat.addStep(inNewBase, leavePubBasement);
		steps.put(10, goRepairBoat);
		steps.put(20, usePlankOnChute);

		ConditionalStep infiltrateMeiyer = new ConditionalStep(this, pushBoat);
		infiltrateMeiyer.addStep(new Conditions(onEntryWall, knockedDownBoard), climbDownBoard);
		infiltrateMeiyer.addStep(onEntryWall, kickBoard);
		infiltrateMeiyer.addStep(inMeiyditch, talkToCitizen);
		infiltrateMeiyer.addStep(pushedBoat, boardBoat);
		steps.put(30, infiltrateMeiyer);
		steps.put(40, infiltrateMeiyer);
		steps.put(50, infiltrateMeiyer);
		steps.put(52, infiltrateMeiyer);
		steps.put(54, infiltrateMeiyer);

		ConditionalStep goTalkToRal = new ConditionalStep(this, pushBoat);
		goTalkToRal.addStep(onEntryWall, climbDownBoard);
		goTalkToRal.addStep(inMeiyditch, talkToRal);
		goTalkToRal.addStep(pushedBoat, boardBoat);
		steps.put(60, goTalkToRal);

		steps.put(65, goTravelToMyrequeBase);

		ConditionalStep enterBase = new ConditionalStep(this, pressDecoratedWall);
		enterBase.addStep(inMyrequeBase, talkToVertida);
		enterBase.addStep(wallPressed, enterRug);
		steps.put(70, enterBase);
		steps.put(80, enterBase);

		steps.put(90, talkToVeliafAfterContact);
		steps.put(100, talkToVeliafAfterLetter);
		steps.put(110, talkToDrezelAfterVeliaf);

		ConditionalStep goSearchBushes = new ConditionalStep(this, searchBushes);
		goSearchBushes.addStep(inTemple, leaveDrezelToBushes);
		steps.put(120, goSearchBushes);

		steps.put(130, talkToDrezelAfterBushes);
		steps.put(135, talkToRoald); // Not part of main path, need to verify
		steps.put(140, talkToRoald); // Verify you can't go to veliaf on 140-160
		steps.put(150, talkToRoald);
		steps.put(160, talkToRoald);
		steps.put(170, talkToVeliafAfterDrezel);

		steps.put(180, returnToMeiyerditch);

		steps.put(190, goToSafalaan);
		steps.put(195, goToSafalaan);

		steps.put(200, goSketchNorth);
		steps.put(210, goSketchWest);
		steps.put(220, goSketchSouth);
		steps.put(230, goSketchSouth);

		steps.put(240, goTalkToSarius);

		ConditionalStep finishSketchAndGetMessage = new ConditionalStep(this, goFinishSouthSketch);
		finishSketchAndGetMessage.addStep(hasSketches, goOpenFireplace);
		steps.put(250, finishSketchAndGetMessage);

		ConditionalStep goGiveSafalaanItems = new ConditionalStep(this, returnToSafalaanInBase);
		goGiveSafalaanItems.addStep(handedInSketches, returnToSafalaanInBaseNoSketches);
		steps.put(260, goGiveSafalaanItems);

		steps.put(270, goUnlockLab);
		steps.put(280, goUnlockLab);

		steps.put(290, getHaemBook);
		steps.put(300, getHaemBook);

		steps.put(310, bringMessageToVeliafToFinish);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		planks2 = new ItemRequirement("Plank", ItemID.PLANK, 2);
		planks1 = new ItemRequirement("Plank", ItemID.PLANK);
		nails8 = new ItemRequirement("Nails", ItemCollections.NAILS, 8);
		nails4 = new ItemRequirement("Nails", ItemCollections.NAILS, 4);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		pickaxe.setTooltip("You can get one from one of the miners in the mine");
		knife = new ItemRequirement("Knife", ItemID.KNIFE).isNotConsumed();
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE);

		doorKey = new ItemRequirement("Door key", ItemID.DOOR_KEY_9654);
		ladderPiece = new ItemRequirement("Ladder top", ItemID.LADDER_TOP);

		message = new ItemRequirement("Message", ItemID.MESSAGE_9633);
		message.setTooltip("You can get another from Vertida in the Meiyerditch Myreque base");

		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		charcoal.setHighlightInInventory(true);
		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		papyrus.setHighlightInInventory(true);
		sketch1 = new ItemRequirement("Castle sketch 1", ItemID.CASTLE_SKETCH_1);
		sketch2 = new ItemRequirement("Castle sketch 2", ItemID.CASTLE_SKETCH_2);
		sketch3 = new ItemRequirement("Castle sketch 3", ItemID.CASTLE_SKETCH_3);

		largeOrnateKey = new ItemRequirement("Large ornate key", ItemID.LARGE_ORNATE_KEY);
		messageFromFireplace = new ItemRequirement("Message", ItemID.MESSAGE_9649);
		messageFromFireplace.setHighlightInInventory(true);
		haemBook = new ItemRequirement("Haemalchemy volume 1", ItemID.HAEMALCHEMY_VOLUME_1);
		sealedMessage = new ItemRequirement("Sealed message", ItemID.SEALED_MESSAGE);
		sealedMessage.setTooltip("You can get another from Safalaan");

		normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);
	}

	public void loadZones()
	{
		newBase = new Zone(new WorldPoint(3489, 9622, 0), new WorldPoint(3500, 9632, 1));
		temple = new Zone(new WorldPoint(3402, 9880, 0), new WorldPoint(3443, 9907, 0));
		entryWall = new Zone(new WorldPoint(3586, 3160, 1), new WorldPoint(3611, 3192, 1));
		entryWall2 = new Zone(new WorldPoint(3587, 3193, 1), new WorldPoint(3592, 3212, 1));
		meiyerditch = new Zone(new WorldPoint(3586, 3157, 0), new WorldPoint(3643, 3330, 3));
		myrequeBase = new Zone(new WorldPoint(3616, 9616, 0), new WorldPoint(3640, 9647, 0));
		mine = new Zone(new WorldPoint(2376, 4616, 2), new WorldPoint(2399, 4638, 2));
		northMeiy = new Zone(new WorldPoint(3595, 3240, 0), new WorldPoint(3644, 3328, 2));
		randomRoom = new Zone(new WorldPoint(3594, 3198, 1), new WorldPoint(3608, 3216, 1));
		barricade1 = new Zone(new WorldPoint(3588, 3205, 0), new WorldPoint(3590, 3233, 0));
		barricade2 = new Zone(new WorldPoint(3591, 3230, 0), new WorldPoint(3593, 3234, 0));
		barricade3 = new Zone(new WorldPoint(3591, 3211, 0), new WorldPoint(3591, 3216, 0));
		secondWall = new Zone(new WorldPoint(3585, 3215, 1), new WorldPoint(3592, 3257, 1));
		secondWall2 = new Zone(new WorldPoint(3593, 3229, 1), new WorldPoint(3613, 3243, 1));
		thirdWall = new Zone(new WorldPoint(3587, 3252, 2), new WorldPoint(3588, 3259, 2));
		fourthWall = new Zone(new WorldPoint(3587, 3252, 1), new WorldPoint(3588, 3289, 1));
		fourthWall2 = new Zone(new WorldPoint(3589, 3258, 1), new WorldPoint(3604, 3310, 1));
		drakanWalls = new Zone(new WorldPoint(3583, 3311, 0), new WorldPoint(3605, 3400, 0));
		drakanWalls2 = new Zone(new WorldPoint(3520, 3331, 0), new WorldPoint(3582, 3387, 0));
		lab = new Zone(new WorldPoint(3623, 9681, 0), new WorldPoint(3639, 9702, 0));
	}

	public void setupConditions()
	{
		inNewBase = new ZoneRequirement(newBase);
		inTemple = new ZoneRequirement(temple);
		onEntryWall = new ZoneRequirement(entryWall, entryWall2);
		inMeiyditch = new ZoneRequirement(meiyerditch, drakanWalls, drakanWalls2);
		inMyrequeBase = new ZoneRequirement(myrequeBase);
		inMine = new ZoneRequirement(mine);
		inNorthMeiy = new ZoneRequirement(northMeiy);
		inRandomRoom = new ZoneRequirement(randomRoom);
		atBarricade = new ZoneRequirement(barricade1, barricade2, barricade3);
		onSecondWall = new ZoneRequirement(secondWall, secondWall2);
		onThirdWall = new ZoneRequirement(thirdWall);
		onFourthWall = new ZoneRequirement(fourthWall, fourthWall2);
		onDrakanWalls = new ZoneRequirement(drakanWalls, drakanWalls2);
		inVanstromFight = new Conditions(onDrakanWalls, new InInstanceRequirement());
		inLab = new ZoneRequirement(lab);

		pushedBoat = new VarbitRequirement(2587, 1);
		knockedDownBoard = new VarbitRequirement(2589, 1);

		pathDoorOpen = new VarbitRequirement(2578, 1);

		fixedLadder = new VarbitRequirement(2598, 2);
		wallPressed = new VarbitRequirement(2590, 1, Operation.GREATER_EQUAL);

		searchedRockySurface = new Conditions(true, new WidgetTextRequirement(229, 1, "a mechanical click."));

		hasSketches = new ItemRequirements(sketch1, sketch2, sketch3);

		cutPortrait = new VarbitRequirement(2595, 1, Operation.GREATER_EQUAL);

		handedInSketches = new VarbitRequirement(2575, 1);
		tapestryCut = new VarbitRequirement(2594, 1);
		keyPlaced = new VarbitRequirement(2596, 1);

		searchedRuneCase = new VarbitRequirement(2584, 1);
		hasTeleGrabRunesOrSearchedCase = new Conditions(LogicType.OR, searchedRuneCase, new ItemRequirements(lawRune, airRune));

		// Repaired boat, 2585 = 1
		// Repaired chute 2586 = 1
		// Pushed boat, 2587 = 1, 2585 = 2
		// Pushed wall 1 down: 2599 0=>1=>2
		// Wall2: 2600 0-1-2
		// Table moved, 2597 = 1, opened = 2
		// Wall3, 2601 0=>1=>2
		// 2598 =1, ladder taken from wall

		// 2591 =1, bush searchable

		// Safalaan appeared, 2592 = 1
		// 2592 = 2, sketched north wall, probably Safalaan going back to base

		// Talked to Sarius, 2580 0->2, maybe chimney searchable?

		// 2595 0->1->2, cut portrait
	}

	public void setupSteps()
	{
		enterBurghPubBasement = new ObjectStep(this, NullObjectID.NULL_12743, new WorldPoint(3490, 3232, 0), "");
		talkToVeliaf = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3494, 9628, 0), "");
		talkToVeliaf.addDialogSteps("Is there something I can do to help out?", "Yes.");

		leavePubBasement = new ObjectStep(this, ObjectID.LADDER_12779, new WorldPoint(3490, 9632, 0), "Leave the Myreque base.");
		usePlankOnBoat = new ObjectStep(this, NullObjectID.NULL_12944, new WorldPoint(3524, 3178, 0), "Fix the boat in south Burgh de Rott.", hammer, planks2, nails8);
		usePlankOnBoat.addDialogStep("Yes.");
		usePlankOnChute = new ObjectStep(this, NullObjectID.NULL_12947, new WorldPoint(3523, 3175, 0), "Fix the boat chute.", hammer, planks1, nails4);
		usePlankOnChute.addDialogStep("Yes.");

		pushBoat = new ObjectStep(this, NullObjectID.NULL_12944, new WorldPoint(3524, 3178, 0), "Push the boat.");
		boardBoat = new ObjectStep(this, NullObjectID.NULL_12945, new WorldPoint(3523, 3170, 0), "Board the boat.");

		kickBoard = new ObjectStep(this, NullObjectID.NULL_18122, new WorldPoint(3589, 3173, 1), "Climb up the walls and search the marked floor.");
		kickBoard.addDialogStep("Yes.");
		climbDownBoard = new ObjectStep(this, NullObjectID.NULL_18122, new WorldPoint(3589, 3173, 1), "Climb down the floorboard.");

		talkToCitizen = new NpcStep(this, NpcID.MEIYERDITCH_CITIZEN, new WorldPoint(3601, 3189, 0), "Talk to a Meiyerditch citizen.", true);
		talkToCitizen.addDialogSteps("(whisper) Do you know about the Myreque?", "(whisper) I really need to meet the Myreque.", "How can Old Man Ral help me?");
		((NpcStep) talkToCitizen).addAlternateNpcs(NpcID.MEIYERDITCH_CITIZEN_3781, NpcID.MEIYERDITCH_CITIZEN_3782, NpcID.MEIYERDITCH_CITIZEN_3783, NpcID.MEIYERDITCH_CITIZEN_3784, NpcID.MEIYERDITCH_CITIZEN_3785,
			NpcID.MEIYERDITCH_CITIZEN_3786, NpcID.MEIYERDITCH_CITIZEN_3787, NpcID.MEIYERDITCH_CITIZEN_3788, NpcID.MEIYERDITCH_CITIZEN_3789, NpcID.MEIYERDITCH_CITIZEN_3790, NpcID.MEIYERDITCH_CITIZEN_3791, NpcID.MEIYERDITCH_CITIZEN_3792,
			NpcID.MEIYERDITCH_CITIZEN_3793, NpcID.MEIYERDITCH_CITIZEN_3794, NpcID.MEIYERDITCH_CITIZEN_3795, NpcID.MEIYERDITCH_CITIZEN_3796, NpcID.MEIYERDITCH_CITIZEN_3797, NpcID.MEIYERDITCH_CITIZEN_3798, NpcID.MEIYERDITCH_CITIZEN_3799,
			NpcID.MEIYERDITCH_CITIZEN_3800, NpcID.MEIYERDITCH_CITIZEN_3801, NpcID.MEIYERDITCH_CITIZEN_3802, NpcID.MEIYERDITCH_CITIZEN_3803, NpcID.MEIYERDITCH_CITIZEN_3804, NpcID.MEIYERDITCH_CITIZEN_3805, NpcID.MEIYERDITCH_CITIZEN_3806,
			NpcID.MEIYERDITCH_CITIZEN_3807, NpcID.MEIYERDITCH_CITIZEN_3808);
		talkToRal = new NpcStep(this, NpcID.OLD_MAN_RAL, new WorldPoint(3604, 3208, 0), "Talk to Old Man Ral in south western Meiyerditch.");
		talkToRal.addDialogSteps("Someone said you could help me.", "Old Man Ral, the sage of Sanguinesti.");

		travelToPots = new ObjectStep(this, ObjectID.POTS_18065, new WorldPoint(3608, 3222, 0), "Follow the path to the Meiyerditch Myreque base. When you reach some pots, search them for a key to open the door.");

		List<WorldPoint> pathToPots = Arrays.asList(
			new WorldPoint(3602, 3207, 0),
			new WorldPoint(3597, 3207, 0),
			new WorldPoint(3597, 3204, 0),
			new WorldPoint(3595, 3204, 0),
			new WorldPoint(3595, 3204, 1),
			new WorldPoint(3598, 3203, 1),
			new WorldPoint(3598, 3201, 1),
			new WorldPoint(3599, 3200, 1),
			new WorldPoint(3601, 3200, 1),
			new WorldPoint(3605, 3203, 1),
			new WorldPoint(3605, 3206, 1),
			new WorldPoint(3606, 3207, 1),
			new WorldPoint(3606, 3214, 1),
			new WorldPoint(3601, 3214, 1),
			new WorldPoint(3601, 3215, 1),
			new WorldPoint(3601, 3214, 0),
			new WorldPoint(3598, 3214, 0),
			new WorldPoint(3598, 3216, 0),

			new WorldPoint(3598, 3216, 3),

			new WorldPoint(3598, 3219, 0),
			new WorldPoint(3594, 3223, 0),
			new WorldPoint(3594, 3223, 1),
			new WorldPoint(3597, 3223, 1),
			new WorldPoint(3598, 3222, 1),
			new WorldPoint(3601, 3222, 1),
			new WorldPoint(3601, 3223, 1),
			new WorldPoint(3603, 3223, 1),
			new WorldPoint(3603, 3221, 0),
			new WorldPoint(3608, 3221, 0)
		);
		travelToPots.setLinePoints(pathToPots);

		List<WorldPoint> pathToLadderDivision = new ArrayList<>();
		pathToLadderDivision.addAll(pathToPots);
		pathToLadderDivision.addAll(Arrays.asList(
			new WorldPoint(3612, 3221, 0),
			new WorldPoint(3615, 3223, 0),
			new WorldPoint(3615, 3220, 0),
			new WorldPoint(3618, 3220, 0),
			new WorldPoint(3618, 3219, 0),
			new WorldPoint(3617, 3219, 1),
			new WorldPoint(3615, 3218, 1),
			new WorldPoint(3615, 3216, 1),
			new WorldPoint(3618, 3215, 1),
			new WorldPoint(3618, 3212, 1),
			new WorldPoint(3615, 3210, 1),
			new WorldPoint(3614, 3210, 2),
			new WorldPoint(3610, 3210, 2),
			new WorldPoint(3610, 3209, 3),
			new WorldPoint(3610, 3208, 3),
			new WorldPoint(3613, 3208, 3),
			new WorldPoint(3613, 3203, 3),
			new WorldPoint(3612, 3203, 3),
			new WorldPoint(3611, 3203, 2),
			new WorldPoint(3611, 3202, 2),
			new WorldPoint(3625, 3202, 2),
			new WorldPoint(3625, 3203, 2),
			new WorldPoint(3625, 3204, 1),
			new WorldPoint(3623, 3204, 1),
			new WorldPoint(3623, 3217, 1),
			new WorldPoint(3623, 3218, 2),
			new WorldPoint(3625, 3221, 2),
			new WorldPoint(3626, 3221, 1),
			new WorldPoint(3626, 3223, 1),
			new WorldPoint(3623, 3223, 1),
			new WorldPoint(3623, 3226, 1),
			new WorldPoint(3622, 3230, 1),
			new WorldPoint(3622, 3235, 1),
			new WorldPoint(3623, 3235, 1),
			new WorldPoint(3623, 3238, 1),
			new WorldPoint(3621, 3238, 1),
			new WorldPoint(3621, 3240, 1),
			new WorldPoint(3626, 3240, 1)
		));

		List<WorldPoint> pathToLadderPiece = new ArrayList<>();
		pathToLadderPiece.addAll(pathToLadderDivision);
		pathToLadderPiece.addAll(Arrays.asList(
			new WorldPoint(3626, 3238, 1),
			new WorldPoint(3628, 3238, 1),
			new WorldPoint(3629, 3238, 1),
			new WorldPoint(3629, 3239, 1),
			new WorldPoint(3630, 3239, 1),
			new WorldPoint(3631, 3239, 2),
			new WorldPoint(3631, 3241, 2),
			new WorldPoint(3627, 3241, 2),
			new WorldPoint(3626, 3240, 2)
		));

		travelToLadderPart = new ObjectStep(this, NullObjectID.NULL_18115, new WorldPoint(3625, 3240, 2),
			"Continue until you reach a wall with a ladder piece, and take it.");
		travelToLadderPart.setLinePoints(pathToLadderPiece);

		List<WorldPoint> pathToPlaceLadder = Arrays.asList(
			new WorldPoint(3627, 3241, 2),
			new WorldPoint(3631, 3241, 2),
			new WorldPoint(3631, 3239, 2),
			new WorldPoint(3630, 3239, 2),
			new WorldPoint(3629, 3239, 1),
			new WorldPoint(3629, 3238, 1),
			new WorldPoint(3626, 3238, 1),
			new WorldPoint(3626, 3240, 1),
			new WorldPoint(3629, 3240, 1)
		);

		travelToFixLadder = new ObjectStep(this, NullObjectID.NULL_18116, new WorldPoint(3629, 3240, 1),
			"Repair the ladder downstairs.");
		travelToFixLadder.setLinePoints(pathToPlaceLadder);

		List<WorldPoint> pathToBase = new ArrayList<>();
		pathToBase.addAll(pathToLadderDivision);
		pathToBase.addAll(Arrays.asList(
			new WorldPoint(3629, 3240, 1),
			new WorldPoint(3630, 3240, 0),
			new WorldPoint(3633, 3240, 0),
			new WorldPoint(3635, 3242, 0),
			new WorldPoint(3635, 3247, 0),
			new WorldPoint(3631, 3247, 0),
			new WorldPoint(3629, 3250, 0),
			new WorldPoint(3625, 3250, 0),
			new WorldPoint(3625, 3252, 0),
			new WorldPoint(3624, 3252, 0),
			new WorldPoint(3624, 3261, 0),
			new WorldPoint(3631, 3261, 0),
			new WorldPoint(3631, 3258, 0),
			new WorldPoint(3630, 3258, 1),
			new WorldPoint(3630, 3259, 1),
			new WorldPoint(3633, 3259, 1),
			new WorldPoint(3633, 3256, 1),
			new WorldPoint(3636, 3256, 1),
			new WorldPoint(3638, 3256, 1),
			new WorldPoint(3638, 3255, 1),
			new WorldPoint(3639, 3255, 1),
			new WorldPoint(3639, 3256, 1),
			new WorldPoint(3639, 3258, 0),
			new WorldPoint(3640, 3258, 0),
			new WorldPoint(3640, 3253, 0)
		));

		travelToMyrequeBase = new ObjectStep(this, ObjectID.WALL_17980, new WorldPoint(3640, 3253, 0), "Use a knife on the wall you eventually reach.");
		travelToMyrequeBase.setLinePoints(pathToBase);
		travelToMyrequeBase.addIcon(ItemID.KNIFE);
		pressDecoratedWall = new ObjectStep(this, NullObjectID.NULL_18146, new WorldPoint(3638, 3251, 0), "Press the decorated wall.");
		pressDecoratedWall.setLinePoints(pathToBase);
		enterRug = new ObjectStep(this, NullObjectID.NULL_18120, new WorldPoint(3639, 3249, 0), "Open the rug and enter the trapdoor.");
		talkToVertida = new NpcStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3627, 9644, 0), "Talk to Vertida in the north room.");

		goDownToDrezel = new ObjectStep(this, ObjectID.TRAPDOOR_3432, new WorldPoint(3422, 3485, 0), "");
		((ObjectStep) (goDownToDrezel)).addAlternateObjects(ObjectID.TRAPDOOR_3433);
		talkToDrezel = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3439, 9896, 0), "");


		leaveDrezelToBushes = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(3405, 9907, 0), "Go up the west ladder.");
		searchBushes = new ObjectStep(this, NullObjectID.NULL_18121, new WorldPoint(3390, 3480, 0), "Search the bushes west of Paterdomus.");
		searchBushes.addSubSteps(leaveDrezelToBushes);
		returnFromBushesToDrezel = new ObjectStep(this, ObjectID.TRAPDOOR_1579, new WorldPoint(3405, 3507, 0), "");
		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_5215, new WorldPoint(3222, 3473, 0), "Talk to King Roald in Varrock Castle.");
		talkToRoald.addDialogSteps("Talk to the king about Morytania.", "What should I do now?", "Yes thanks. I'll accept the free teleport.");

		goToMines = new NpcStep(this, NpcID.VYREWATCH, new WorldPoint(3615, 3263, 0), "Talk to a Vyrewatch to be sent to the mines.", true);
		goToMines.addDialogSteps("Send me to the mines.", "Send me to the mines! (Do a bit of menial work)");
		((NpcStep) (goToMines)).setMaxRoamRange(1000);
		((NpcStep) (goToMines)).addAlternateNpcs(NpcID.VYREWATCH_3710, NpcID.VYREWATCH_3711, NpcID.VYREWATCH_3712, NpcID.VYREWATCH_3713, NpcID.VYREWATCH_3714, NpcID.VYREWATCH_3715, NpcID.VYREWATCH_3716,
			NpcID.VYREWATCH_3717, NpcID.VYREWATCH_3718, NpcID.VYREWATCH_3719, NpcID.VYREWATCH_3720, NpcID.VYREWATCH_3721, NpcID.VYREWATCH_3722, NpcID.VYREWATCH_3723, NpcID.VYREWATCH_3724,
			NpcID.VYREWATCH_3725, NpcID.VYREWATCH_3726, NpcID.VYREWATCH_3727, NpcID.VYREWATCH_3728, NpcID.VYREWATCH_3729, NpcID.VYREWATCH_3730, NpcID.VYREWATCH_3748, NpcID.VYREWATCH_3749,
			NpcID.VYREWATCH_3750, NpcID.VYREWATCH_3751, NpcID.VYREWATCH_3752, NpcID.VYREWATCH_3753, NpcID.VYREWATCH_3754, NpcID.VYREWATCH_3755, NpcID.VYREWATCH_3756, NpcID.VYREWATCH_3757,
			NpcID.VYREWATCH_3758, NpcID.VYREWATCH_3759, NpcID.VYREWATCH_3760, NpcID.VYREWATCH_3761, NpcID.VYREWATCH_3762, NpcID.VYREWATCH_3763);
		mineDaeyaltThenLeave = new NpcStep(this, NpcID.VAMPYRE_JUVINATE, new WorldPoint(2389, 4624, 2),
			"Mine Daeyalt ore from the walls and put them into the mine carts. If you have no pickaxe, talk to a " +
				"miner to obtain a bronze one. Once you've mined 15, talk to the vampyres to leave.", pickaxe);
		mineDaeyaltThenLeave.addDialogStep("Do you have a spare pick?");

		List<WorldPoint> pathFromMineToBase = Arrays.asList(
			new WorldPoint(3623, 3324, 0),
			new WorldPoint(3631, 3324, 0),
			new WorldPoint(3631, 3303, 0),
			new WorldPoint(3628, 3300, 0),
			new WorldPoint(3628, 3294, 0),
			new WorldPoint(3633, 3294, 0),
			new WorldPoint(3633, 3288, 0),
			new WorldPoint(3635, 3288, 0),
			new WorldPoint(3635, 3284, 0),
			new WorldPoint(3632, 3284, 0),
			new WorldPoint(3632, 3277, 0),
			new WorldPoint(3634, 3277, 0),
			new WorldPoint(3634, 3267, 0),
			new WorldPoint(3631, 3267, 0),
			new WorldPoint(3631, 3258, 0),
			new WorldPoint(3631, 3258, 1),
			new WorldPoint(3633, 3256, 1),
			new WorldPoint(3639, 3256, 1),
			new WorldPoint(3639, 3256, 0),
			new WorldPoint(3640, 3256, 0),
			new WorldPoint(3640, 3250, 0)
		);

		returnToMeiyBase = new ObjectStep(this, NullObjectID.NULL_18146, new WorldPoint(3638, 3251, 0), "Press the decorated wall to unlock the base's entrance.");
		returnToMeiyBase.setLinePoints(pathFromMineToBase);

		goDownFromRandomRoom = new ObjectStep(this, ObjectID.LADDER_17975, new WorldPoint(3595, 3204, 1), "Climb down the ladder.");
		climbUpFloor = new ObjectStep(this, NullObjectID.NULL_18124, new WorldPoint(3589, 3173, 0), "Climb up to the floor in the wall to the south west.");
		climbDownWallLadder = new ObjectStep(this, ObjectID.LADDER_17999, new WorldPoint(3588, 3210, 1), "Climb down the ladder to the north.");
		searchRockySurface = new ObjectStep(this, ObjectID.ROCKY_SURFACE, new WorldPoint(3592, 3211, 0), "Search the rocky surface nearby.");
		goThroughBarricade = new ObjectStep(this, ObjectID.LADDER_18000, new WorldPoint(3593, 3230, 0), "Go through the barricade and up the ladder to the north.");
		climbLadderSecondWall = new ObjectStep(this, ObjectID.LADDER_18002, new WorldPoint(3588, 3251, 1), "Climb up the ladder to the north.");

		climbDownFromThirdWall = new ObjectStep(this, ObjectID.LADDER_18001, new WorldPoint(3588, 3259, 2), "Climb down the ladder to the north.");
		climbUpDrakanWalls = new ObjectStep(this, ObjectID.WALL_39173, new WorldPoint(3595, 3310, 1), "Climb up the wall to the north.");
		talkToSafalaan = new NpcStep(this, NpcID.SAFALAAN_HALLOW, new WorldPoint(3585, 3331, 0), "Talk to Safalaan on the walls.");
		drawNorthWall = new DetailedQuestStep(this, new WorldPoint(3556, 3379, 0), "");
		drawWestWall = new DetailedQuestStep(this, new WorldPoint(3522, 3357, 0), "");
		drawSouthWall = new DetailedQuestStep(this, new WorldPoint(3572, 3331, 0), "");
		tankVanstrom = new NpcStep(this, NpcID.VANSTROM_KLAUSE_3735, new WorldPoint(3572, 3331, 0), "Tank Vanstrom for 5 hits or use Protect from Melee.");
		((NpcStep) tankVanstrom).addAlternateNpcs(NpcID.VANSTROM_KLAUSE_3734);
		finishSouthSketch = new DetailedQuestStep(this, new WorldPoint(3572, 3331, 0), "");
		talkToSarius = new NpcStep(this, NpcID.SARIUS_GUILE, new WorldPoint(3572, 3331, 0), "");
		talkToSafalaanInBase = new NpcStep(this, NpcID.SAFALAAN_HALLOW, new WorldPoint(3627, 9644, 0), "Talk to Safalaan in the north room.");

		List<WorldPoint> pathFromMineToFireplace = Arrays.asList(
			new WorldPoint(3623, 3324, 0),
			new WorldPoint(3631, 3324, 0),
			new WorldPoint(3631, 3303, 0),
			new WorldPoint(3628, 3300, 0),
			new WorldPoint(3628, 3294, 0),
			new WorldPoint(3633, 3294, 0),
			new WorldPoint(3633, 3288, 0),
			new WorldPoint(3635, 3288, 0),
			new WorldPoint(3635, 3284, 0),
			new WorldPoint(3632, 3284, 0),
			new WorldPoint(3632, 3277, 0),
			new WorldPoint(3634, 3277, 0),
			new WorldPoint(3634, 3267, 0),
			new WorldPoint(3631, 3267, 0),
			new WorldPoint(3631, 3261, 0),
			new WorldPoint(3624, 3261, 0),
			new WorldPoint(3624, 3252, 0),
			new WorldPoint(2627, 3252, 0)
		);

		useKnifeOnFireplace = new ObjectStep(this, ObjectID.FIREPLACE_18039, new WorldPoint(3627, 3253, 0), "");
		useKnifeOnFireplace.setLinePoints(pathFromMineToFireplace);
		useKnifeOnFireplace.addIcon(ItemID.KNIFE);
		readMessage = new DetailedQuestStep(this, "Read the message.", messageFromFireplace);
		useKnifeOnPortrait = new ObjectStep(this, NullObjectID.NULL_18126, new WorldPoint(3627, 3248, 0), "Use a knife on the portrait south of the fireplace.");
		useKnifeOnPortrait.addIcon(ItemID.KNIFE);
		inspectPortrait = new ObjectStep(this, NullObjectID.NULL_18126, new WorldPoint(3627, 3248, 0), "Inspect the portrait south of the fireplace.");
		useKnifeOnPortrait.addSubSteps(inspectPortrait);
		leaveMeiyerBase = new ObjectStep(this, ObjectID.LADDER_17986, new WorldPoint(3626, 9617, 0), "Go up to the surface.");

		List<WorldPoint> pathFromBaseToTapestry = Arrays.asList(
			new WorldPoint(3640, 3253, 0),
			new WorldPoint(3640, 3258, 0),
			new WorldPoint(3639, 3258, 0),
			new WorldPoint(3639, 3255, 1),
			new WorldPoint(3636, 3256, 1),
			new WorldPoint(3632, 3256, 1),
			new WorldPoint(3631, 3258, 1),
			new WorldPoint(3631, 3259, 0),
			new WorldPoint(3631, 3267, 0),
			new WorldPoint(3634, 3267, 0),
			new WorldPoint(3634, 3278, 0),
			new WorldPoint(3632, 3278, 0),
			new WorldPoint(3632, 3283, 0),
			new WorldPoint(3635, 3283, 0),
			new WorldPoint(3635, 3289, 0),
			new WorldPoint(3633, 3289, 0),
			new WorldPoint(3633, 3293, 0),
			new WorldPoint(3635, 3293, 0),
			new WorldPoint(3635, 3300, 0),
			new WorldPoint(3640, 3300, 0),
			new WorldPoint(3640, 3302, 0)
		);

		useKnifeOnTapestry = new ObjectStep(this, NullObjectID.NULL_18125, new WorldPoint(3638, 3304, 0), "Slash the tapestry in the building in north east Meiyerditch.", knife.highlighted());
		useKnifeOnTapestry.setLinePoints(pathFromBaseToTapestry);
		useKnifeOnTapestry.addIcon(ItemID.KNIFE);
		useKeyOnStatue = new ObjectStep(this, NullObjectID.NULL_18127, new WorldPoint(3641, 3304, 0), "Use the ornate key on the nearby statue.", largeOrnateKey.highlighted());
		useKeyOnStatue.addIcon(ItemID.LARGE_ORNATE_KEY);
		goDownToLab = new ObjectStep(this, ObjectID.STAIRCASE_18049, new WorldPoint(3643, 3305, 0), "Go down the staircase to the lab.");
		getRunes = new ObjectStep(this, ObjectID.BROKEN_RUNE_CASE, new WorldPoint(3629, 9695, 0), "Search the broken rune case.");
		telegrabBook = new DetailedQuestStep(this, new WorldPoint(3624, 9690, 0), "Telegrab the Haemalchemy book.", haemBook, lawRune, airRune);
		leaveLab = new ObjectStep(this, ObjectID.STAIRCASE_18050, new WorldPoint(3637, 9697, 0), "Leave the lab.");
	}

	public void setupConditionalSteps()
	{
		startQuest = new ConditionalStep(this, enterBurghPubBasement, "Talk to Veliaf in the Myreque base under Burgh de Rott.");
		startQuest.addStep(inNewBase, talkToVeliaf);

		goTravelToMyrequeBase = new ConditionalStep(this, travelToPots, "Follow the path to the Meiyerditch Myreque base.", knife);
		goTravelToMyrequeBase.addStep(fixedLadder, travelToMyrequeBase);
		goTravelToMyrequeBase.addStep(ladderPiece, travelToFixLadder);
		goTravelToMyrequeBase.addStep(new Conditions(LogicType.OR, doorKey, pathDoorOpen), travelToLadderPart);

		talkToVeliafAfterContact = new ConditionalStep(this, enterBurghPubBasement, "Bring the message to Veliaf in the Myreque base under Burgh de Rott.", message);
		talkToVeliafAfterContact.addStep(inNewBase, talkToVeliaf);
		talkToVeliafAfterContact.addDialogStep("What should I do now?");

		talkToVeliafAfterLetter = new ConditionalStep(this, enterBurghPubBasement, "Talk to Veliaf in the Myreque base under Burgh de Rott.");
		talkToVeliafAfterLetter.addStep(inNewBase, talkToVeliaf);
		talkToVeliafAfterContact.addSubSteps(talkToVeliafAfterLetter);

		talkToDrezelAfterVeliaf = new ConditionalStep(this, goDownToDrezel, "Talk to Drezel under the Paterdomus Temple.");
		talkToDrezelAfterVeliaf.addStep(inTemple, talkToDrezel);

		talkToDrezelAfterBushes = new ConditionalStep(this, returnFromBushesToDrezel, "Return to Drezel.");
		talkToDrezelAfterBushes.addStep(inTemple, talkToDrezel);

		talkToVeliafAfterDrezel = new ConditionalStep(this, enterBurghPubBasement, "Return to Veliaf in the Myreque base under Burgh de Rott.");
		talkToVeliafAfterDrezel.addStep(inNewBase, talkToVeliaf);

		ConditionalStep getToNorthMeiy = new ConditionalStep(this, boardBoat);
		getToNorthMeiy.addStep(inMine, mineDaeyaltThenLeave);
		getToNorthMeiy.addStep(inMeiyditch, goToMines);

		returnToMeiyerditch = new ConditionalStep(this, getToNorthMeiy, "Return to the Meiyerditch Myreque base.");
		returnToMeiyerditch.addStep(inMyrequeBase, talkToVertida);
		returnToMeiyerditch.addStep(wallPressed, enterRug);
		returnToMeiyerditch.addStep(inNorthMeiy, returnToMeiyBase);

		ConditionalStep navigateWalls = new ConditionalStep(this, boardBoat);
		navigateWalls.addStep(onFourthWall, climbUpDrakanWalls);
		navigateWalls.addStep(onThirdWall, climbDownFromThirdWall);
		navigateWalls.addStep(onSecondWall, climbLadderSecondWall);
		navigateWalls.addStep(new Conditions(atBarricade, searchedRockySurface), goThroughBarricade);
		navigateWalls.addStep(atBarricade, searchRockySurface);
		navigateWalls.addStep(inRandomRoom, goDownFromRandomRoom);
		navigateWalls.addStep(onEntryWall, climbDownWallLadder);
		navigateWalls.addStep(inMeiyditch, climbUpFloor);

		goToSafalaan = new ConditionalStep(this, navigateWalls, "Climb along the western wall until you reach Safalaan.");
		goToSafalaan.addStep(onDrakanWalls, talkToSafalaan);
		goToSafalaan.addDialogStep("Okay, lead the way.");

		goSketchNorth = new ConditionalStep(this, navigateWalls, "Sketch the north side of Castle Drakan by using the charcoal on some papyrus.", papyrus, charcoal);
		goSketchNorth.addStep(onDrakanWalls, drawNorthWall);

		goSketchWest = new ConditionalStep(this, navigateWalls, "Sketch the west side of Castle Drakan by using the charcoal on some papyrus.", papyrus, charcoal);
		goSketchWest.addStep(onDrakanWalls, drawWestWall);

		goSketchSouth = new ConditionalStep(this, navigateWalls, "Sketch the south side of Castle Drakan. Be prepared to tank some hits from Vanstrom Klause.", papyrus, charcoal);
		goSketchSouth.addStep(inVanstromFight, tankVanstrom);
		goSketchSouth.addStep(onDrakanWalls, drawSouthWall);

		goTalkToSarius = new ConditionalStep(this, navigateWalls, "Talk to Sarius on the walls around Castle Drakan.");
		goTalkToSarius.addStep(onDrakanWalls, talkToSarius);

		goFinishSouthSketch = new ConditionalStep(this, navigateWalls, "Finish sketching the south side of Castle Drakan.", papyrus, charcoal);
		goFinishSouthSketch.addStep(onDrakanWalls, finishSouthSketch);

		goOpenFireplace = new ConditionalStep(this, getToNorthMeiy, "Go use a knife on the fireplace near the Meiyerditch Myreque base.", knife, sketch1, sketch2, sketch3);
		goOpenFireplace.addStep(inNorthMeiy, useKnifeOnFireplace);

		returnToSafalaanInBaseNoSketches = new ConditionalStep(this, getToNorthMeiy, "Return to Safalaan in the Meiyerditch Myreque base.", knife);
		returnToSafalaanInBaseNoSketches.addStep(inMyrequeBase, talkToSafalaanInBase);
		returnToSafalaanInBaseNoSketches.addStep(wallPressed, enterRug);
		returnToSafalaanInBaseNoSketches.addStep(new Conditions(inNorthMeiy, largeOrnateKey), returnToMeiyBase);
		returnToSafalaanInBaseNoSketches.addStep(new Conditions(inNorthMeiy, cutPortrait), inspectPortrait);
		returnToSafalaanInBaseNoSketches.addStep(inNorthMeiy, useKnifeOnPortrait);

		returnToSafalaanInBase = new ConditionalStep(this, returnToSafalaanInBaseNoSketches, sketch1, sketch2, sketch3);

		ConditionalStep travelToLab = new ConditionalStep(this, getToNorthMeiy, "Travel to the labs in Meiyerditch.", knife);

		goUnlockLab = new ConditionalStep(this, travelToLab);
		goUnlockLab.addStep(new Conditions(inNorthMeiy, largeOrnateKey, tapestryCut), useKeyOnStatue);
		goUnlockLab.addStep(new Conditions(inNorthMeiy, largeOrnateKey), useKnifeOnTapestry);
		goUnlockLab.addStep(new Conditions(inNorthMeiy, cutPortrait), inspectPortrait);
		goUnlockLab.addStep(inNorthMeiy, useKnifeOnPortrait);
		goUnlockLab.addStep(inMyrequeBase, leaveMeiyerBase);

		bringSafalaanBook = new ConditionalStep(this, returnToMeiyBase, "Return to Safalaan with the Haemalchemy book.", haemBook);
		bringSafalaanBook.addStep(inMyrequeBase, talkToSafalaanInBase);
		bringSafalaanBook.addStep(inLab, leaveLab);
		bringSafalaanBook.addStep(wallPressed, enterRug);

		getHaemBook = new ConditionalStep(this, travelToLab);
		getHaemBook.addStep(haemBook, bringSafalaanBook);
		getHaemBook.addStep(new Conditions(inLab, hasTeleGrabRunesOrSearchedCase), telegrabBook);
		getHaemBook.addStep(inLab, getRunes);
		getHaemBook.addStep(inNorthMeiy, goDownToLab);

		bringMessageToVeliafToFinish = new ConditionalStep(this, enterBurghPubBasement, "Bring the letter to Veliaf in the Myreque base under Burgh de Rott to complete the quest.", sealedMessage);
		bringMessageToVeliafToFinish.addStep(inNewBase, talkToVeliaf);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(normalSpellbook);
		return req;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.IN_AID_OF_THE_MYREQUE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 5));
		req.add(new SkillRequirement(Skill.MINING, 20));
		req.add(new SkillRequirement(Skill.THIEVING, 22));
		req.add(new SkillRequirement(Skill.AGILITY, 26));
		req.add(new SkillRequirement(Skill.CRAFTING, 32));
		req.add(new SkillRequirement(Skill.MAGIC, 33));
		req.add(new SkillRequirement(Skill.STRENGTH, 40));
		return req;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(hammer, planks2, nails8, knife);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(lawRune, airRune, pickaxe);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Able to survive 5 hits from Vanstrom Klause (level 169) or use Protect from Melee to negate all his damage.");
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
				new ExperienceReward(Skill.AGILITY, 7000),
				new ExperienceReward(Skill.THIEVING, 6000),
				new ExperienceReward(Skill.CONSTRUCTION, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("2,000 Experience Tomes (Any skill over level 30).", ItemID.ANTIQUE_LAMP, 3)); //4447 is placeholder
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Infiltrating Meiyerditch", Arrays.asList(startQuest, leavePubBasement, usePlankOnBoat, usePlankOnChute, pushBoat,
			boardBoat, kickBoard, climbDownBoard, talkToCitizen, talkToRal, goTravelToMyrequeBase, pressDecoratedWall, enterRug, talkToVertida, talkToVeliafAfterContact), hammer, planks2, nails8, knife));

		allSteps.add(new PanelDetails("Murder at Paterdomus", Arrays.asList(talkToDrezelAfterVeliaf, searchBushes, talkToDrezelAfterBushes, talkToRoald,
			talkToVeliafAfterDrezel)));

		allSteps.add(new PanelDetails("Mapping Castle Drakan", Arrays.asList(returnToMeiyerditch, goToSafalaan, goSketchNorth, goSketchWest, goSketchSouth,
			tankVanstrom, goTalkToSarius, goFinishSouthSketch, goOpenFireplace, useKnifeOnPortrait, readMessage,
			returnToSafalaanInBaseNoSketches), knife));

		allSteps.add(new PanelDetails("Investigate the lab", Arrays.asList(useKnifeOnTapestry, useKeyOnStatue, goDownToLab, telegrabBook, bringSafalaanBook, bringMessageToVeliafToFinish), knife));

		return allSteps;
	}
}
