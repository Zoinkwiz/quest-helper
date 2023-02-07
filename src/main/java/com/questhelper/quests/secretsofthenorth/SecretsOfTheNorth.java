/*
 * Copyright (c) 2023, Obasill <https://github.com/Obasill>
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
package com.questhelper.quests.secretsofthenorth;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_SECRETS_OF_THE_NORTH
)
public class SecretsOfTheNorth extends BasicQuestHelper
{
	// Required
	ItemRequirement coins, lockpick, tinderbox, combatGear, food;

	// Recommended
	ItemRequirement antipoison, ardyCloak, icyBasalt, prayerPot;

	// Quest items
	ItemRequirement dustyScroll, leverHandle, icyChest, settlementsNote, jewelShard1, jewelShard2, ancientJewel, icyKey;

	QuestStep startQuest, goUpstairs, inspectBody, inspectWall, inspectWindow, climbHiddenLadder, inspectChest,
		goDownToGuard, talkToGuardInvestigated, talkToGuardCompleted, downStaircaseToKhaz, speakToBarman,
		inspectBarrel, inspectBoulder, inspectBush, inspectStump, inspectBoulder2, inspectBush2, fightEvelot,
		speakToEvelot, enterCave, boardRaft, returnRaft, returnStairs, returnGuard, ladderToClaus, talkToClaus,
		examineShelves, examineWall, lockpickChest, returnToHazeel, returnToHazeelWall, returnToHazeelLadder,
		returnToHazeelCave, returnToHazeelRaft, inspectScroll, goNorth, talkToSnowflake, talkToTroll, talkToTrollFinish,
		moveToWeissCave, enterWeissCave, talkToHazeelWeiss, searchBarrel, openCentreGate, openNorthChest, getTinderbox,
		lightNW, lightSE, lightNE, lightSW, openWestChest, openNorthGate, useLeverOnMechanism, pullLever, inspectPillar,
		combineShards, openIcyChest, openSouthGate, enterCrevice, talkToJallan, continueCutscene;

	NpcStep talkToHazeel, finishQuest;

	Requirement notTalkedToGuard, notGoneUpstairs, notInspectCeril, notInspectWall, notInspectWindow, notInspectChest,
		inspectedCrimeScene, toldWindow, toldCeril, toldWall, checkedBarrel, checkedBoulder, checkedBush, checkedStump,
		checkedBoulder2, checkedBush2, evelotDefeated, talkedToHazeel, talkedToGuard, talkedToClaus, buttonPressed,
		chestPicked, scrollInspected, talkedNorth, talkedToSnowflake, questioned1, questioned2, assassinFight,
		assassinDefeated, puzzleStart, centreGateUnlocked, nwBrazier, seBrazier, neBrazier, swBrazier, brazierFin,
		northGateUnlocked, fixedLever, leverPulled, southGateOpened, jhallanTalkedTo, returnToGuard;

	Requirement notInspectedCrimeScene, onTheTrail;

	Zone mansionSecond, mansionThird, hidingSpace, raftZone, hazeelZone, basement, hiddenRoom, weissCave;

	Requirement inMansionSecond, inMansionThird, inHidingSpace, inRaftZone, inHazeelZone, inBasement, inHiddenRoom,
		inWeissCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		/*
		14739 - idk 0->1 on quest pickup
		14740 - idk 0-> 1 on quest pickup
		14769 - idk  1 -> 2 on quest pickup
		14722 - part 1 house?

		 */

		ConditionalStep startingOff = new ConditionalStep(this, startQuest);
		startingOff.addStep(new Conditions(inMansionSecond, notInspectedCrimeScene, inspectedCrimeScene,
			toldCeril, toldWall, toldWindow), talkToGuardCompleted);
		startingOff.addStep(new Conditions(inMansionSecond, notInspectedCrimeScene, inspectedCrimeScene), talkToGuardInvestigated);
		startingOff.addStep(new Conditions(inMansionThird, notInspectedCrimeScene, inspectedCrimeScene), goDownToGuard);
		startingOff.addStep(new Conditions(inMansionThird, notInspectedCrimeScene, notInspectChest), inspectChest);
		startingOff.addStep(new Conditions(inHidingSpace, notInspectedCrimeScene, notInspectWall, notInspectChest), climbHiddenLadder);
		startingOff.addStep(new Conditions(inMansionSecond, notInspectedCrimeScene, notInspectWall), inspectWall);
		startingOff.addStep(new Conditions(inMansionSecond, notInspectedCrimeScene, notInspectWindow), inspectWindow);
		startingOff.addStep(new Conditions(inMansionSecond, notInspectedCrimeScene, notInspectCeril), inspectBody);
		startingOff.addStep(notGoneUpstairs, goUpstairs);
		startingOff.addStep(notTalkedToGuard, startQuest);

		steps.put(0, startingOff);
		steps.put(2, startingOff);
		steps.put(4, startingOff);
		steps.put(6, startingOff);
		steps.put(8, startingOff);
		steps.put(10, startingOff);
		steps.put(12, startingOff);

		ConditionalStep huntingEvelot = new ConditionalStep(this, speakToBarman);
		huntingEvelot.addStep(new Conditions(onTheTrail, evelotDefeated), speakToEvelot);
		huntingEvelot.addStep(new Conditions(onTheTrail, checkedBush2), fightEvelot);
		huntingEvelot.addStep(new Conditions(onTheTrail, checkedBoulder2), inspectBush2);
		huntingEvelot.addStep(new Conditions(onTheTrail, checkedStump), inspectBoulder2);
		huntingEvelot.addStep(new Conditions(onTheTrail, checkedBush), inspectStump);
		huntingEvelot.addStep(new Conditions(onTheTrail, checkedBoulder), inspectBush);
		huntingEvelot.addStep(new Conditions(onTheTrail, checkedBarrel), inspectBoulder);
		huntingEvelot.addStep(onTheTrail, inspectBarrel);
		huntingEvelot.addStep(inMansionSecond, downStaircaseToKhaz);

		steps.put(14, huntingEvelot);
		steps.put(16, huntingEvelot);
		steps.put(18, huntingEvelot);
		steps.put(20, huntingEvelot);
		steps.put(22, huntingEvelot);
		steps.put(24, huntingEvelot);
		steps.put(26, huntingEvelot);
		steps.put(28, huntingEvelot);
		steps.put(30, huntingEvelot);


		ConditionalStep questionsForHazeel = new ConditionalStep(this, enterCave);
		questionsForHazeel.addStep(new Conditions(scrollInspected, inHazeelZone, dustyScroll), returnToHazeel);
		questionsForHazeel.addStep(new Conditions(scrollInspected, inRaftZone, dustyScroll), returnToHazeelRaft);
		questionsForHazeel.addStep(new Conditions(scrollInspected, inBasement, dustyScroll), returnToHazeelLadder);
		questionsForHazeel.addStep(new Conditions(scrollInspected, inHiddenRoom, dustyScroll), returnToHazeelWall);
		questionsForHazeel.addStep(new Conditions(scrollInspected, dustyScroll), returnToHazeelCave);
		questionsForHazeel.addStep(new Conditions(buttonPressed, inHiddenRoom, dustyScroll), inspectScroll);
		questionsForHazeel.addStep(new Conditions(buttonPressed, inHiddenRoom), lockpickChest);
		questionsForHazeel.addStep(new Conditions(buttonPressed, inBasement), examineWall);
		questionsForHazeel.addStep(new Conditions(talkedToClaus, inBasement), examineShelves);
		questionsForHazeel.addStep(new Conditions(talkedToGuard, inBasement), talkToClaus);
		questionsForHazeel.addStep(talkedToGuard, ladderToClaus);
		questionsForHazeel.addStep(talkedToHazeel, returnGuard);
		questionsForHazeel.addStep(new Conditions(inRaftZone, talkedToHazeel), returnStairs);
		questionsForHazeel.addStep(new Conditions(inHazeelZone, talkedToHazeel), returnRaft);
		questionsForHazeel.addStep(inHazeelZone, talkToHazeel);
		questionsForHazeel.addStep(inRaftZone, boardRaft);

		steps.put(32, questionsForHazeel);
		steps.put(34, questionsForHazeel);
		steps.put(36, questionsForHazeel);
		steps.put(38, questionsForHazeel);
		steps.put(40, questionsForHazeel);
		steps.put(42, questionsForHazeel);
		steps.put(44, questionsForHazeel);
		steps.put(46, questionsForHazeel);
		steps.put(48, questionsForHazeel);
		steps.put(50, questionsForHazeel);
		steps.put(52, questionsForHazeel);


		ConditionalStep goingNorth = new ConditionalStep(this, goNorth);
		goingNorth.addStep(southGateOpened, enterCrevice);
		goingNorth.addStep(new Conditions(puzzleStart, brazierFin, settlementsNote, icyKey), openSouthGate);
		goingNorth.addStep(new Conditions(puzzleStart, icyChest, brazierFin, settlementsNote, ancientJewel), openIcyChest);
		goingNorth.addStep(new Conditions(puzzleStart, icyChest, brazierFin, settlementsNote, jewelShard1, jewelShard2), combineShards);
		goingNorth.addStep(new Conditions(puzzleStart, icyChest, brazierFin, settlementsNote, jewelShard1, northGateUnlocked, leverPulled), inspectPillar);
		goingNorth.addStep(new Conditions(puzzleStart, icyChest, brazierFin, settlementsNote, jewelShard1, northGateUnlocked, fixedLever), pullLever);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, brazierFin, settlementsNote, jewelShard1, northGateUnlocked), useLeverOnMechanism);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, brazierFin, settlementsNote, jewelShard1), openNorthGate);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, brazierFin), openWestChest);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, tinderbox, nwBrazier, seBrazier, neBrazier), lightSW);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, tinderbox, nwBrazier, seBrazier), lightNE);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, tinderbox, nwBrazier), lightSE);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest, tinderbox), lightNW);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, icyChest), getTinderbox);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle, centreGateUnlocked), openNorthChest);
		goingNorth.addStep(new Conditions(puzzleStart, leverHandle), openCentreGate);
		goingNorth.addStep(puzzleStart, searchBarrel);
		goingNorth.addStep(assassinDefeated, talkToHazeelWeiss);
		goingNorth.addStep(new Conditions(assassinFight, inWeissCave), enterWeissCave);
		goingNorth.addStep(assassinFight, moveToWeissCave);
		goingNorth.addStep(new Conditions(talkedToSnowflake, questioned1, questioned2), talkToTrollFinish);
		goingNorth.addStep(talkedToSnowflake, talkToTroll);
		goingNorth.addStep(talkedNorth, talkToSnowflake);

		steps.put(54, goingNorth);
		steps.put(56, goingNorth);
		steps.put(58, goingNorth);
		steps.put(60, goingNorth);
		steps.put(62, goingNorth);
		steps.put(64, goingNorth);
		steps.put(66, goingNorth);
		steps.put(68, goingNorth);
		steps.put(70, goingNorth);
		steps.put(72, goingNorth);
		steps.put(74, goingNorth);
		steps.put(76, goingNorth);
		steps.put(78, goingNorth);
		steps.put(80, goingNorth);

		ConditionalStep finishingUp = new ConditionalStep(this, talkToJallan);
		finishingUp.addStep(returnToGuard, finishQuest);
		finishingUp.addStep(jhallanTalkedTo, continueCutscene);

		steps.put(82, finishingUp);
		steps.put(84, finishingUp);
		steps.put(86, finishingUp); // talk to hazeel
		steps.put(88, finishingUp);





		return steps;
	}

	public void setupRequirements()
	{
		// Required
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 100);
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat Gear", -1, -1);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPot = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		dustyScroll = new ItemRequirement("Dusty Scroll", ItemID.DUSTY_SCROLL_27595);
		leverHandle = new ItemRequirement("Lever Handle", ItemID.LEVER_HANDLE);
		icyChest = new ItemRequirement("Icy chest", ItemID.ICY_CHEST);
		settlementsNote = new ItemRequirement("Settlements note", ItemID.SETTLEMENTS_NOTE);
		jewelShard1 = new ItemRequirement("Jewel shard", ItemID.JEWEL_SHARD_27606);
		jewelShard2 = new ItemRequirement("Jewel shard", ItemID.JEWEL_SHARD);
		ancientJewel = new ItemRequirement("Ancient jewel", ItemID.ANCIENT_JEWEL);
		icyKey = new ItemRequirement("Icy key", ItemID.ICY_KEY);


		// Recommended
		antipoison = new ItemRequirement("Antipoisons", ItemCollections.ANTIPOISONS);
		ardyCloak = new ItemRequirement("Any Ardougne Cloak", ItemCollections.ARDY_CLOAKS);
		icyBasalt = new ItemRequirement("Icy Basalt", ItemID.ICY_BASALT);

		notTalkedToGuard = new VarbitRequirement(14722, 2, Operation.GREATER_EQUAL);
		notGoneUpstairs = new VarbitRequirement(14722, 4, Operation.GREATER_EQUAL);
		notInspectedCrimeScene = new VarbitRequirement(14722, 6, Operation.GREATER_EQUAL);

		notInspectWall = new VarbitRequirement(14726, 0);
		notInspectCeril = new VarbitRequirement(14727, 0);
		notInspectWindow = new VarbitRequirement(14728, 0);
		notInspectChest = new VarbitRequirement(14729, 0);

		inspectedCrimeScene = new VarbitRequirement(14722, 8, Operation.GREATER_EQUAL);

		toldWindow = new VarbitRequirement(14731, 1);
		toldCeril = new VarbitRequirement(14730, 1);
		toldWall = new VarbitRequirement(14732, 1);

		onTheTrail = new VarbitRequirement(14722, 20, Operation.GREATER_EQUAL);

		checkedBarrel = new VarbitRequirement(14733, 1);
		checkedBoulder = new VarbitRequirement(14734, 1);
		checkedBush = new VarbitRequirement(14735, 1);
		checkedStump = new VarbitRequirement(14736, 1);
		checkedBoulder2 = new VarbitRequirement(14737, 1);
		checkedBush2 = new VarbitRequirement(14738, 1);

		evelotDefeated = new VarbitRequirement(14722, 28, Operation.GREATER_EQUAL);

		talkedToHazeel = new VarbitRequirement(14722, 36, Operation.GREATER_EQUAL);

		talkedToGuard = new VarbitRequirement(14722, 38, Operation.GREATER_EQUAL);

		talkedToClaus = new VarbitRequirement(14722, 40, Operation.GREATER_EQUAL);

		buttonPressed = new VarbitRequirement(14722, 42, Operation.GREATER_EQUAL);

		chestPicked = new VarbitRequirement(14722, 48, Operation.GREATER_EQUAL);

		scrollInspected = new VarbitRequirement(14722, 50, Operation.GREATER_EQUAL);

		talkedToSnowflake = new VarbitRequirement(14722, 58, Operation.GREATER_EQUAL);

		questioned1 = new VarbitRequirement(14743, 1, Operation.GREATER_EQUAL);
		questioned2 = new VarbitRequirement(14744, 1, Operation.GREATER_EQUAL);

		assassinFight = new VarbitRequirement(14722, 62, Operation.GREATER_EQUAL);

		assassinDefeated = new VarbitRequirement(14722, 68, Operation.GREATER_EQUAL);

		puzzleStart = new VarbitRequirement(14722, 72, Operation.GREATER_EQUAL);

		centreGateUnlocked = new VarbitRequirement(14760, 1, Operation.GREATER_EQUAL);

		nwBrazier = new VarbitRequirement(14754, 4, Operation.EQUAL);
		seBrazier = new VarbitRequirement(14755, 1, Operation.EQUAL);
		neBrazier = new VarbitRequirement(14756, 3, Operation.EQUAL);
		swBrazier = new VarbitRequirement(14757, 2, Operation.EQUAL);
		brazierFin = new VarbitRequirement(14758, 1, Operation.GREATER_EQUAL);

		northGateUnlocked = new VarbitRequirement(14759, 1, Operation.GREATER_EQUAL);

		fixedLever = new VarbitRequirement(14753, 1, Operation.GREATER_EQUAL);
		leverPulled = new VarbitRequirement(14753, 2, Operation.GREATER_EQUAL);

		southGateOpened = new VarbitRequirement(14722, 78, Operation.GREATER_EQUAL);

		jhallanTalkedTo = new VarbitRequirement(14722, 82, Operation.GREATER_EQUAL);
		returnToGuard = new VarbitRequirement(14722, 88, Operation.GREATER_EQUAL);
	}

	public void loadZones()
	{
		mansionSecond = new Zone(new WorldPoint(2562, 3276, 1), new WorldPoint(2577, 3266, 1));
		mansionThird = new Zone(new WorldPoint(2562, 3276, 2), new WorldPoint(2577, 3266, 2));
		hidingSpace = new Zone(new WorldPoint(2571, 3271, 1), new WorldPoint(2753, 3271, 1));
		raftZone = new Zone(new WorldPoint(2564, 9686, 0), new WorldPoint(2572, 9680, 0));
		hazeelZone = new Zone(new WorldPoint(2598, 9696,  0), new WorldPoint(2616, 9664, 0));
		basement = new Zone(new WorldPoint(2536, 9701,  0), new WorldPoint(2550, 9692, 0));
		hiddenRoom = new Zone(new WorldPoint(2531, 9623,  0), new WorldPoint(2539, 9616, 0));
		weissCave = new Zone(new WorldPoint(2825, 10355,  0), new WorldPoint(2860, 10323, 0));
	}

	public void setupConditions()
	{
		inMansionSecond = new ZoneRequirement(mansionSecond);
		inMansionThird = new ZoneRequirement(mansionThird);
		inHidingSpace = new ZoneRequirement(hidingSpace);
		inRaftZone = new ZoneRequirement(raftZone);
		inHazeelZone = new ZoneRequirement(hazeelZone);
		inBasement = new ZoneRequirement(basement);
		inHiddenRoom = new ZoneRequirement(hiddenRoom);
		inWeissCave = new ZoneRequirement(weissCave);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.GUARD_12087, new WorldPoint(2570, 3276, 0),
			"Talk to the guard outside of the Carnillean Mansion in East Ardougne.");
		startQuest.addDialogStep("Yes.");
		goUpstairs = new ObjectStep(this, ObjectID.STAIRCASE_46704, new WorldPoint(2568, 3269, 0),
			"Climb the stairs in Carnillean Mansion.");
		inspectBody = new ObjectStep(this, ObjectID.CERIL_CARNILLEAN, "Inspect Ceril Carnillean's body.");
		inspectWall = new ObjectStep(this, ObjectID.WALL_46707, "Knock-at the wall on the North side of the room.");
		inspectWall.addDialogStep("Yes.");
		inspectWindow = new ObjectStep(this, ObjectID.BROKEN_WINDOW, "Inspect the broken Window on the East side of the room.");
		climbHiddenLadder = new ObjectStep(this, ObjectID.LADDER_16683, "Climb the Ladder.");
		inspectChest = new ObjectStep(this, ObjectID.CHEST_46572, "Inspect the chest.");
		goDownToGuard = new ObjectStep(this, ObjectID.LADDER_16679, "Descend the ladder and talk to the guard");
		talkToGuardInvestigated = new NpcStep(this, NpcID.GUARD_12045, "Talk to the guard.");
		talkToGuardInvestigated.addDialogSteps("The window was broken from the outside.",
			"Ceril was stabbed from behind.", "A hidden room above us was broken into.");
		talkToGuardCompleted = new NpcStep(this, NpcID.GUARD_12045, "Talk to the guard.");
		talkToGuardCompleted.addDialogSteps("I think that's everything.", "I think I've gone over everything.");

		downStaircaseToKhaz = new ObjectStep(this, ObjectID.STAIRCASE_46705, "Climb down the staircase.");
		speakToBarman = new NpcStep(this, NpcID.KHAZARD_BARMAN, new WorldPoint(2566, 3140, 0),
			"Head to the Fight Arena Bar and talk to th Khazard Barman.", coins, combatGear);
		speakToBarman.addDialogStep("Do you know anyone called Evelot?");
		inspectBarrel = new ObjectStep(this, 46873, new WorldPoint(2568, 3152, 0),
			"Inspect the barrels.",  combatGear);
		inspectBoulder = new ObjectStep(this, 46874, new WorldPoint(2573, 3180, 0),
			"Inspect the boulder.", combatGear);
		inspectBush = new ObjectStep(this, 46878, new WorldPoint(2567, 3202, 0),
			"Inspect the bush.", combatGear);
		inspectStump = new ObjectStep(this, 46877, new WorldPoint(2584, 3197, 0),
			"Inspect the stump.", combatGear);
		inspectBoulder2 = new ObjectStep(this, 46875, new WorldPoint(2605, 3188, 0),
			"Inspect the boulder.", combatGear);
		inspectBush2 = new ObjectStep(this, 46878, new WorldPoint(2621, 3193, 0),
			"Inspect the bush.", combatGear);
		fightEvelot = new NpcStep(this, NpcID.EVELOT, new WorldPoint(2643, 3202, 0),
			"Approach Evelot to start the fight.\n\n" +
				"Protect from melee and reactivate prayer after she special attacks.",
			combatGear);
		speakToEvelot = new NpcStep(this, NpcID.EVELOT, new WorldPoint(2643, 3202, 0),
			"Talk to Evelot.");

		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2852, new WorldPoint(2586, 3234, 0),
			"Enter the cave that leads to the Ardougne sewers.", lockpick);
		boardRaft = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2567, 9679, 0),
			"Board the raft.");
		// TODO check if player killed Alomone and direct accordingly
		talkToHazeel = new NpcStep(this, NpcID.HAZEEL_12050, new WorldPoint(2607, 9672, 0),
			"Talk to Hazeel. If you didn't kill Alomone previously, talk to him first to summon Hazeel.");
		talkToHazeel.addAlternateNpcs(NpcID.ALOMONE, NpcID.ALOMONE_12093, NpcID.ALOMONE_12094);
		returnRaft = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2606, 9693, 0),
			"Return to the Carnillean Mansion.");
		returnStairs = new ObjectStep(this, ObjectID.STAIRS_2853, new WorldPoint(2570, 9684, 0),
			"Return to the Carnillean Mansion.");
		returnGuard = new NpcStep(this, NpcID.GUARD_12087, new WorldPoint(2570, 3276, 0),
			"Return to the Carnillean Mansion and speak to the Guard.");
		ladderToClaus = new ObjectStep(this, ObjectID.LADDER_46717, new WorldPoint(2570, 3267, 0),
			"Climb the ladder to the basement.");
		talkToClaus = new NpcStep(this, NpcID.CLAUS_THE_CHEF, new WorldPoint(2542, 9696, 0),
			"Talk to Claus the Chef.");
		examineShelves = new ObjectStep(this, ObjectID.COOKING_SHELVES_46589, new WorldPoint(2540, 9694, 0),
			"Examine the cooking shelves for a button.");
		examineShelves.addDialogStep("Yes.");
		examineWall = new ObjectStep(this, 46897, new WorldPoint(2544, 9698, 0),
			"Inspect the wall next to the noticeboard.");
		examineWall.addDialogStep("Enter the passage.");
		lockpickChest = new ObjectStep(this, 46899, new WorldPoint(2535, 9621, 0),
			"Lock pick the chest. " +
				"\n Green means it's the right number and position. " +
				"\nBlue means it's the right position but wrong number. " +
				"\n The puzzle's solution does not change attempt to attempt.");
		inspectScroll = new ItemStep(this, "Inspect the dusty scroll.", dustyScroll.highlighted());
		returnToHazeel = new NpcStep(this, NpcID.HAZEEL_12050, new WorldPoint(2607, 9672, 0),
		"Return to Hazeel and tell him about the scroll.", dustyScroll);
		returnToHazeelWall = new ObjectStep(this, ObjectID.WALL_46590, new WorldPoint(2533, 9617, 0),
			"Return to Hazeel and tell him about the scroll.", dustyScroll);
		returnToHazeelWall.addDialogStep("Enter the passage.");
		returnToHazeelLadder = new ObjectStep(this, ObjectID.LADDER_46716, new WorldPoint(2544, 9694, 0),
			"Return to Hazeel and tell him about the scroll.", dustyScroll);
		returnToHazeelCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2852, new WorldPoint(2586, 3234, 0),
			"Return to Hazeel and tell him about the scroll.", dustyScroll);
		returnToHazeelRaft = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2567, 9679, 0),
			"Return to Hazeel and tell him about the scroll.", dustyScroll);

		goNorth = new NpcStep(this, NpcID.BIG_FISH, new WorldPoint(2880, 3946, 0),
			"Talk to Big Fish at the north entrance to Weiss.");
		talkToTroll = new NpcStep(this, NpcID.BIG_FISH, new WorldPoint(2880, 3946, 0),
			"Talk to Big Fish (General Khazard) at the north entrance to Weiss.");
		talkToTroll.addDialogSteps("Why would the killer come here?", "What is this ritual you keep talking about?");
		talkToTrollFinish = new NpcStep(this, NpcID.BIG_FISH, new WorldPoint(2880, 3946, 0),
			"Talk to Big Fish (General Khazard) at the north entrance to Weiss.");
		talkToTrollFinish.addDialogSteps("Okay, I'm done asking questions. What now?", "I'm done asking questions. What now?");
		talkToSnowflake = new NpcStep(this, NpcID.SNOWFLAKE, new WorldPoint(2872, 3934, 0),
			"Talk to Snowflake");
		talkToSnowflake.addDialogStep("Have you seen anything odd around here recently?");
		moveToWeissCave = new ObjectStep(this, ObjectID.STAIRS_33234, new WorldPoint(2867, 3940, 0),
			"Prepare for a fight and enter the main building in Weiss.", combatGear, antipoison);
		enterWeissCave = new ObjectStep(this, 46905, new WorldPoint(2846, 10332, 0),
			"Prepare for a fight and enter the cave. " +
				"\n Put the Assasin in the smoke bombs so you can hit him and dodge the poison vials he throws out.",
			combatGear, antipoison);
		enterWeissCave.addDialogStep("Yes.");
		talkToHazeelWeiss = new NpcStep(this, NpcID.HAZEEL_12051, "Talk to Hazeel and tell him about what happened.");
		searchBarrel = new ObjectStep(this, ObjectID.BARREL_46609,
			"Search the barrel South of the room you fought the assasin.");
		openCentreGate = new ObjectStep(this, ObjectID.GATE_46602,
			"Enter the centre room. Use the code \"BLOOD\" to unlock the gate.");
		openNorthChest = new ObjectStep(this, ObjectID.CHEST_46619, "Open the north chest using the code \"7402\".");
		getTinderbox = new ObjectStep(this, ObjectID.CRATE_46608, "Get a tinderbox from the crate.");
		lightNW = new DetailedQuestStep(this, "Light the North West Brazier.");
		lightSE = new DetailedQuestStep(this, "Light the South East Brazier.");
		lightNE = new DetailedQuestStep(this, "Light the North East Brazier.");
		lightSW = new DetailedQuestStep(this, "Light the South West Brazier.");
		openWestChest = new ObjectStep(this, ObjectID.CHEST_46618, "Open the western Chest.");
		openNorthGate = new ObjectStep(this, ObjectID.GATE_46601,
			"Open the north gate using the code LEFT - UP - LEFT - DOWN.");
		useLeverOnMechanism = new ObjectStep(this, 46900, "Use the handle on the Lever mechanism",
			leverHandle.highlighted());
		useLeverOnMechanism.addIcon(leverHandle.getId());
		useLeverOnMechanism.addDialogStep("Yes.");
		pullLever = new ObjectStep(this, ObjectID.LEVER_MECHANISM, "Pull the lever.");
		inspectPillar = new ObjectStep(this, ObjectID.PILLAR_46613,
			"Inspect the pillar in the room where you fought the assassin.");
		combineShards = new DetailedQuestStep(this, "Use the jewel shards on one another.",
			jewelShard1. highlighted(), jewelShard2.highlighted());
		openIcyChest = new DetailedQuestStep(this, "Use the ancient jewel on the icy chest.",
			ancientJewel.highlighted(), icyChest.highlighted());
		openSouthGate = new ObjectStep(this, ObjectID.GATE_46603,
			"Open the gate near the barrel you found the lever handle in.", icyKey);
		enterCrevice = new ObjectStep(this, ObjectID.CREVICE_46597,
			"Resupply if you haven't already. Prepare yourself for a challenging fight then enter the crevice.");

		talkToJallan = new NpcStep(this, NpcID.JHALLAN, "Speak to Jhallan.");
		continueCutscene = new NpcStep(this, NpcID.HAZEEL_12051,
			"Continue the cutscene and then talk to Hazeel for a teleport to Ardougne.");
		continueCutscene.addDialogStep("Yes.");
		finishQuest = new NpcStep(this, NpcID.GUARD_12087, new WorldPoint(2570, 3276, 0),
			"Return to the guard outside of the Carnillean Mansion in East Ardougne to finish the quest. " +
				"Talk to Hazeel for a teleport to Ardougne");
		finishQuest.addAlternateNpcs(NpcID.HAZEEL_12051);
		finishQuest.addDialogStep("Yes.");


	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins, lockpick, tinderbox, combatGear, food);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(antipoison, ardyCloak, icyBasalt);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Evelot (level 148)", "Assassin (level 262)", "Strange Creature (level 368)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.AGILITY, 69, false));
		req.add(new SkillRequirement(Skill.THIEVING, 64, false));
		req.add(new SkillRequirement(Skill.HUNTER, 56, false));
		return req;
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
			new ExperienceReward(Skill.AGILITY, 60000),
			new ExperienceReward(Skill.FARMING, 50000),
			new ExperienceReward(Skill.FARMING, 40000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Ability to mine ancient essence crystals in the Ghorrock Dungeon"),
			new UnlockReward("Access to a new boss, the Phantom Muspah"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting Off",
			Arrays.asList()));

		allSteps.add(new PanelDetails("Hunting Down Evelot",
			Arrays.asList(), coins, combatGear));

		allSteps.add(new PanelDetails("The Mysterious Benefactor",
			Arrays.asList(), lockpick));

		allSteps.add(new PanelDetails("In The North",
			Arrays.asList(), lockpick));


		allSteps.add(new PanelDetails("Secrets of the Dungeon",
			Arrays.asList(), combatGear));

		return allSteps;
	}
}
