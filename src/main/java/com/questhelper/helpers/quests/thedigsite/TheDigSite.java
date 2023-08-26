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
package com.questhelper.helpers.quests.thedigsite;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_DIG_SITE
)
public class TheDigSite extends BasicQuestHelper
{
	//Items Required
	ItemRequirement pestleAndMortar, vialHighlighted, tinderbox, tea, ropes2, rope, opal, charcoal, specimenBrush, specimenJar, panningTray,
		panningTrayFull, trowel, varrock2, digsiteTeleports, sealedLetter, specialCup, teddybear, skull, nitro, nitrate, chemicalCompound, groundCharcoal, invitation, talisman,
		mixedChemicals, mixedChemicals2, arcenia, powder, liquid, tablet, key, unstampedLetter, trowelHighlighted, tinderboxHighlighted, chemicalCompoundHighlighted;

	Requirement talkedToFemaleStudent, talkedToOrangeStudent, talkedToGreenStudent, talkedToGuide, letterStamped, hasTeddy, hasSkull, hasSpecialCup,
		femaleStudentQ1Learnt, orangeStudentQ1Learnt, greenStudentQ1Learnt, femaleStudentQ2Learnt, orangeStudentQ2Learnt, greenStudentQ2Learnt, femaleStudentQ3Learnt,
		femaleExtorting, orangeStudentQ3Learnt, greenStudentQ3Learnt, syncedUp, syncedUp2, syncedUp3, givenTalismanIn, rope1Added, rope2Added,
		inUndergroundTemple1, inDougRoom,openedBarrel, searchedBricks, hasKeyOrPowderOrMixtures, openPowderChestNearby, inUndergroundTemple2,
		knowStateAsJustStartedQuest, knowStateAsJustCompletedFirstExam, knowStateAsJustCompletedSecondExam;

	QuestStep talkToExaminer, talkToHaig, talkToExaminer2, searchBush, takeTray, talkToGuide, panWater, pickpocketWorkmen, talkToFemaleStudent, talkToFemaleStudent2,
		talkToOrangeStudent, talkToOrangeStudent2, talkToGreenStudent, talkToGreenStudent2, takeTest1, talkToFemaleStudent3, talkToOrangeStudent3, talkToGreenStudent3,
		takeTest2, talkToFemaleStudent4, takeTest3, getJar, getBrush, digForTalisman, talkToExpert, useInvitationOnWorkman, useRopeOnWinch, goDownWinch, pickUpRoot,
		searchBricks, goUpRope, goDownToDoug, talkToDoug, goUpFromDoug, unlockChest, searchChest, useTrowelOnBarrel, useVialOnBarrel, usePowderOnExpert, useLiquidOnExpert,
		mixNitroWithNitrate, grindCharcoal, addCharcoal, addRoot, goDownToExplode, useCompound, useTinderbox, takeTablet, useTabletOnExpert, syncStep, talkToFemaleStudent5,
		talkToOrangeStudent4, talkToGreenStudent4, useRopeOnWinch2, goDownToExplode2, goDownForTablet, goUpWithTablet, searchPanningTray;

	//Zones
	Zone undergroundTemple1, dougRoom, undergroundTemple2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToExaminer);

		ConditionalStep returnWithLetter = new ConditionalStep(this, talkToHaig);
		// This is used to set knowStateAsJustStartedQuest to true
		returnWithLetter.addStep(new Conditions(LogicType.NOR, knowStateAsJustStartedQuest), talkToExaminer);
		returnWithLetter.addStep(letterStamped, talkToExaminer2);
		steps.put(1, returnWithLetter);

		ConditionalStep goTakeTest1 = new ConditionalStep(this, syncStep);
		// Sets the state of knowStateAsJustCompleted to true for ConditionalStep after this
		goTakeTest1.addStep(new Conditions(LogicType.NOR, knowStateAsJustCompletedFirstExam), takeTest1);

		goTakeTest1.addStep(new Conditions(femaleStudentQ1Learnt, orangeStudentQ1Learnt, greenStudentQ1Learnt), takeTest1);
		goTakeTest1.addStep(new Conditions(femaleStudentQ1Learnt, orangeStudentQ1Learnt, talkedToGreenStudent), talkToGreenStudent2);
		goTakeTest1.addStep(new Conditions(femaleStudentQ1Learnt, orangeStudentQ1Learnt, hasSkull, specimenBrush),
			talkToGreenStudent);

		goTakeTest1.addStep(new Conditions(femaleStudentQ1Learnt, talkedToOrangeStudent, hasSkull, specimenBrush), talkToOrangeStudent2);
		goTakeTest1.addStep(new Conditions(femaleStudentQ1Learnt, hasSpecialCup, hasSkull, specimenBrush), talkToOrangeStudent);

		goTakeTest1.addStep(new Conditions(talkedToFemaleStudent, hasSpecialCup, hasSkull, specimenBrush), talkToFemaleStudent2);
		goTakeTest1.addStep(new Conditions(hasTeddy, hasSpecialCup, hasSkull, specimenBrush), talkToFemaleStudent);

		goTakeTest1.addStep(new Conditions(syncedUp, hasTeddy, hasSpecialCup), pickpocketWorkmen);
		goTakeTest1.addStep(new Conditions(syncedUp, hasTeddy, panningTrayFull, talkedToGuide), searchPanningTray);
		goTakeTest1.addStep(new Conditions(syncedUp, hasTeddy, panningTray, talkedToGuide), panWater);
		goTakeTest1.addStep(new Conditions(syncedUp, hasTeddy, panningTray), talkToGuide);
		goTakeTest1.addStep(new Conditions(syncedUp, hasTeddy), takeTray);
		goTakeTest1.addStep(syncedUp, searchBush);
		steps.put(2, goTakeTest1);

		ConditionalStep goTakeTest2 = new ConditionalStep(this, syncStep);
		// Sets the state of knowStateAsJustCompletedFirstExam to true for ConditionalStep after this
		goTakeTest1.addStep(new Conditions(LogicType.NOR, knowStateAsJustCompletedSecondExam), takeTest2);

		goTakeTest2.addStep(new Conditions(syncedUp2, femaleStudentQ2Learnt, orangeStudentQ2Learnt, greenStudentQ2Learnt), takeTest2);
		goTakeTest2.addStep(new Conditions(syncedUp2, femaleStudentQ2Learnt, orangeStudentQ2Learnt), talkToGreenStudent3);
		goTakeTest2.addStep(new Conditions(syncedUp2, femaleStudentQ2Learnt), talkToOrangeStudent3);
		goTakeTest2.addStep(syncedUp2, talkToFemaleStudent3);
		steps.put(3, goTakeTest2);

		ConditionalStep goTakeTest3 = new ConditionalStep(this, syncStep);
		goTakeTest3.addStep(new Conditions(femaleStudentQ3Learnt, orangeStudentQ3Learnt, greenStudentQ3Learnt), takeTest3);
		goTakeTest3.addStep(new Conditions(syncedUp3, femaleStudentQ3Learnt, orangeStudentQ3Learnt), talkToGreenStudent4);
		goTakeTest3.addStep(new Conditions(syncedUp3, femaleStudentQ3Learnt), talkToOrangeStudent4);
		goTakeTest3.addStep(new Conditions(syncedUp3, femaleExtorting), talkToFemaleStudent5);
		goTakeTest3.addStep(syncedUp3, talkToFemaleStudent4);
		steps.put(4, goTakeTest3);

		ConditionalStep findTalisman = new ConditionalStep(this, getJar);
		findTalisman.addStep(new Conditions(givenTalismanIn), useInvitationOnWorkman);
		findTalisman.addStep(new Conditions(talisman), talkToExpert);
		findTalisman.addStep(new Conditions(specimenJar, specimenBrush), digForTalisman);
		findTalisman.addStep(new Conditions(specimenJar), getBrush);
		steps.put(5, findTalisman);

		ConditionalStep learnHowToMakeExplosives = new ConditionalStep(this, useRopeOnWinch2);
		learnHowToMakeExplosives.addStep(inDougRoom, talkToDoug);
		learnHowToMakeExplosives.addStep(new Conditions(inUndergroundTemple1, arcenia), goUpRope);
		learnHowToMakeExplosives.addStep(inUndergroundTemple1, pickUpRoot);
		learnHowToMakeExplosives.addStep(new Conditions(rope2Added), goDownToDoug);

		ConditionalStep makeExplosives = new ConditionalStep(this, goDownWinch);
		makeExplosives.addStep(new Conditions(chemicalCompound, inUndergroundTemple1), useCompound);

		makeExplosives.addStep(inDougRoom, goUpFromDoug);
		makeExplosives.addStep(new Conditions(inUndergroundTemple1, arcenia), goUpRope);
		makeExplosives.addStep(inUndergroundTemple1, pickUpRoot);

		makeExplosives.addStep(new Conditions(chemicalCompound), goDownToExplode);
		makeExplosives.addStep(new Conditions(arcenia, mixedChemicals2), addRoot);
		makeExplosives.addStep(new Conditions(arcenia, mixedChemicals, groundCharcoal), addCharcoal);
		makeExplosives.addStep(new Conditions(arcenia, mixedChemicals, charcoal), grindCharcoal);
		makeExplosives.addStep(new Conditions(arcenia, nitrate, nitro), mixNitroWithNitrate);
		makeExplosives.addStep(new Conditions(arcenia, powder, nitro), usePowderOnExpert);
		makeExplosives.addStep(new Conditions(arcenia, powder, liquid), useLiquidOnExpert);
		makeExplosives.addStep(new Conditions(arcenia, powder, liquid), useVialOnBarrel);
		makeExplosives.addStep(new Conditions(arcenia, powder, openedBarrel), useVialOnBarrel);
		makeExplosives.addStep(new Conditions(arcenia, powder), useTrowelOnBarrel);
		makeExplosives.addStep(new Conditions(openPowderChestNearby, arcenia), searchChest);
		makeExplosives.addStep(arcenia, unlockChest);

		ConditionalStep discovery = new ConditionalStep(this, useRopeOnWinch);
		discovery.addStep(hasKeyOrPowderOrMixtures, makeExplosives);
		discovery.addStep(searchedBricks, learnHowToMakeExplosives);
		discovery.addStep(new Conditions(inUndergroundTemple1, arcenia), searchBricks);
		discovery.addStep(inUndergroundTemple1, pickUpRoot);
		discovery.addStep(rope1Added, goDownWinch);
		steps.put(6, discovery);

		ConditionalStep explodeWall = new ConditionalStep(this, goDownToExplode2);
		explodeWall.addStep(inUndergroundTemple1, useTinderbox);
		steps.put(7, explodeWall);

		ConditionalStep completeQuest = new ConditionalStep(this, goDownForTablet);
		completeQuest.addStep(new Conditions(tablet.alsoCheckBank(questBank), inUndergroundTemple2), goUpWithTablet);
		completeQuest.addStep(new Conditions(tablet.alsoCheckBank(questBank)), useTabletOnExpert);
		completeQuest.addStep(inUndergroundTemple2, takeTablet);
		steps.put(8, completeQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortar.setHighlightInInventory(true);
		vialHighlighted = new ItemRequirement("Vial", ItemID.VIAL);
		vialHighlighted.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tinderboxHighlighted = tinderbox.highlighted();
		tea = new ItemRequirement("Cup of tea", ItemID.CUP_OF_TEA_1978);
		ropes2 = new ItemRequirement("Rope", ItemID.ROPE, 2);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);
		opal = new ItemRequirement("Opal", ItemID.OPAL);
		opal.setTooltip("You can get one by panning at the Digsite");
		opal.addAlternates(ItemID.UNCUT_OPAL);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		charcoal.setTooltip("Obtainable during quest by searching specimen trays");
		charcoal.setHighlightInInventory(true);
		specimenBrush = new ItemRequirement("Specimen brush", ItemID.SPECIMEN_BRUSH).isNotConsumed();
		specimenJar = new ItemRequirement("Specimen jar", ItemID.SPECIMEN_JAR).isNotConsumed();
		panningTray = new ItemRequirement("Panning tray", ItemID.PANNING_TRAY).isNotConsumed();
		panningTray.addAlternates(ItemID.PANNING_TRAY_678, ItemID.PANNING_TRAY_679);
		panningTrayFull = new ItemRequirement("Panning tray", ItemID.PANNING_TRAY_679).isNotConsumed();
		trowel = new ItemRequirement("Trowel", ItemID.TROWEL).isNotConsumed();
		trowel.setTooltip("You can get another from one of the Examiners");
		trowelHighlighted = new ItemRequirement("Trowel", ItemID.TROWEL).isNotConsumed();
		trowelHighlighted.setHighlightInInventory(true);
		trowelHighlighted.setTooltip("You can get another from one of the Examiners");
		varrock2 = new ItemRequirement("Varrock teleports", ItemID.VARROCK_TELEPORT, 2);
		digsiteTeleports = new ItemRequirement("Digsite teleports", ItemID.DIGSITE_TELEPORT, 2);
		sealedLetter = new ItemRequirement("Sealed letter", ItemID.SEALED_LETTER);
		sealedLetter.setTooltip("You can get another from Curator Haig in the Varrock Museum");
		specialCup = new ItemRequirement("Special cup", ItemID.SPECIAL_CUP);
		teddybear = new ItemRequirement("Teddy", ItemID.TEDDY);
		skull = new ItemRequirement("Animal skull", ItemID.ANIMAL_SKULL);
		nitro = new ItemRequirement("Nitroglycerin", ItemID.NITROGLYCERIN);
		nitro.setHighlightInInventory(true);
		nitrate = new ItemRequirement("Ammonium nitrate", ItemID.AMMONIUM_NITRATE);
		nitrate.setHighlightInInventory(true);
		chemicalCompound = new ItemRequirement("Chemical compound", ItemID.CHEMICAL_COMPOUND);
		chemicalCompoundHighlighted = new ItemRequirement("Chemical compound", ItemID.CHEMICAL_COMPOUND);
		chemicalCompoundHighlighted.setHighlightInInventory(true);
		groundCharcoal = new ItemRequirement("Ground charcoal", ItemID.GROUND_CHARCOAL);
		groundCharcoal.setTooltip("You can make this by use a pestle and mortar on some charcoal. You can get charcoal from one of the specimen trays in the Digsite");
		groundCharcoal.setHighlightInInventory(true);
		invitation = new ItemRequirement("Invitation letter", ItemID.INVITATION_LETTER);
		invitation.setTooltip("You can get another from the Archaeological expert");
		invitation.setHighlightInInventory(true);
		talisman = new ItemRequirement("Ancient talisman", ItemID.ANCIENT_TALISMAN);
		mixedChemicals = new ItemRequirement("Mixed chemicals", ItemID.MIXED_CHEMICALS);
		mixedChemicals.setHighlightInInventory(true);
		mixedChemicals2 = new ItemRequirement("Mixed chemicals", ItemID.MIXED_CHEMICALS_706);
		mixedChemicals2.setHighlightInInventory(true);
		arcenia = new ItemRequirement("Arcenia root", ItemID.ARCENIA_ROOT);
		arcenia.setHighlightInInventory(true);

		powder = new ItemRequirement("Chemical powder", ItemID.CHEMICAL_POWDER);
		powder.setHighlightInInventory(true);
		liquid = new ItemRequirement("Unidentified liquid", ItemID.UNIDENTIFIED_LIQUID);
		liquid.setHighlightInInventory(true);
		tablet = new ItemRequirement("Stone tablet", ItemID.STONE_TABLET);
		tablet.setHighlightInInventory(true);
		key = new ItemRequirement("Chest key", ItemID.CHEST_KEY_709);
		key.setHighlightInInventory(true);
		unstampedLetter = new ItemRequirement("Unstamped letter", ItemID.UNSTAMPED_LETTER);
		unstampedLetter.setTooltip("You can get another from the Exam Centre's examiners");
	}

	public void loadZones()
	{
		undergroundTemple1 = new Zone(new WorldPoint(3359, 9800, 0), new WorldPoint(3393, 9855, 0));
		undergroundTemple2 = new Zone(new WorldPoint(3360, 9734, 0), new WorldPoint(3392, 9790, 0));
		dougRoom = new Zone(new WorldPoint(3340, 9809, 0), new WorldPoint(3357, 9826, 0));
	}

	public void setupConditions()
	{
		inUndergroundTemple1 = new ZoneRequirement(undergroundTemple1);
		inUndergroundTemple2 = new ZoneRequirement(undergroundTemple2);
		inDougRoom = new ZoneRequirement(dougRoom);


		knowStateAsJustStartedQuest = new Conditions(true, new VarplayerRequirement(131, 1, Operation.LESS_EQUAL));
		knowStateAsJustCompletedFirstExam = new Conditions(true, new VarplayerRequirement(131, 2, Operation.LESS_EQUAL));
		knowStateAsJustCompletedSecondExam = new Conditions(true, new VarplayerRequirement(131, 3, Operation.LESS_EQUAL));


		syncedUp = new Conditions(true, LogicType.OR, knowStateAsJustStartedQuest,
			new WidgetTextRequirement(119, 2, "The Dig Site"));

		syncedUp2 = new Conditions(true, LogicType.OR, knowStateAsJustCompletedFirstExam,
			new WidgetTextRequirement(119, 2, "The Dig Site"),
			new DialogRequirement("You got all the questions correct. Well done!"),
			new DialogRequirement("Hey! Excellent!"));

		syncedUp3 = new Conditions(true, LogicType.OR, knowStateAsJustCompletedSecondExam,
			new WidgetTextRequirement(119,	2, "The Dig Site"),
			new DialogRequirement("You got all the questions correct, well done!"),
			new DialogRequirement("Great, I'm getting good at this."));

		talkedToGuide = new VarbitRequirement(2544, 1);
		tea = tea.hideConditioned(talkedToGuide);


		// Exam questions 1
		talkedToFemaleStudent = new Conditions(true, LogicType.OR,
			new DialogRequirement("Hey! My lucky mascot!"),
			new WidgetTextRequirement(119, 3, true, "I should talk to her to see if she can help"));
		femaleStudentQ1Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The proper health and safety points are"),
			new WidgetTextRequirement(119, 3, true, "She gave me an answer"));

		WidgetTextRequirement orangeGivenAnswer1Diary = new WidgetTextRequirement(119, 3, true, "He gave me an answer to one of the questions");
		orangeGivenAnswer1Diary.addRange(20, 35);
		talkedToOrangeStudent = new Conditions(true, LogicType.OR,
			new DialogRequirement("Look what I found!"),
			new WidgetTextRequirement(119, 3, true, "<str>to find it and return it to him."));
		orangeStudentQ1Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The people eligible to use the digsite are:"),
			orangeGivenAnswer1Diary);

		WidgetTextRequirement greenGivenAnswer1Diary = new WidgetTextRequirement(119, 3, true, "He gave me an answer to one of the questions");
		greenGivenAnswer1Diary.addRange(0, 19);

		talkedToGreenStudent = new Conditions(true, LogicType.OR,
			new DialogRequirement("Oh wow! You've found it!"),
			new WidgetTextRequirement(119, 3, true, "<str>to him; maybe someone has picked it up?"));
		greenStudentQ1Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The study of Earth Sciences is:"),
			greenGivenAnswer1Diary);

		// Exam questions 2
		WidgetTextRequirement femaleGivenAnswer2Diary = new WidgetTextRequirement(119, 3, true, "<str>I need to speak to the student in the purple skirt about");
		femaleGivenAnswer2Diary.addRange(43, 52);
		femaleStudentQ2Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Finds handling: Finds must"),
			femaleGivenAnswer2Diary);

		WidgetTextRequirement orangeGivenAnswer2Diary = new WidgetTextRequirement(119, 3, true, "<str>I need to speak to the student in the orange top about the");
		orangeGivenAnswer2Diary.addRange(43, 52);
		orangeStudentQ2Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Correct sample transportation: "),
			orangeGivenAnswer2Diary);

		WidgetTextRequirement greenGivenAnswer2Diary = new WidgetTextRequirement(119, 3, true, "<str>I need to speak to the student in the green top about the");
		greenGivenAnswer2Diary.addRange(43, 52);
		greenStudentQ2Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Correct rock pick usage: Always handle"),
			greenGivenAnswer2Diary);

		// Exam questions 3
		femaleExtorting = new Conditions(true, LogicType.OR,
			new DialogRequirement("OK, I'll see what I can turn up for you."),
			new DialogRequirement("Well, I have seen people get them from panning"),
			new WidgetTextRequirement(119, 3, true, "I need to bring her an opal"));
		WidgetTextRequirement femaleGivenAnswer3Diary = new WidgetTextRequirement(119, 3, true, "<str>I need to speak to the student in the purple skirt about");
		femaleGivenAnswer3Diary.addRange(56, 63);
		femaleStudentQ3Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Sample preparation: Samples cleaned"),
			femaleGivenAnswer3Diary);

		WidgetTextRequirement orangeGivenAnswer3Diary = new WidgetTextRequirement(119, 3, true, "<str>I need to speak to the student in the orange top about the");
		orangeGivenAnswer3Diary.addRange(56, 63);
		orangeStudentQ3Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The proper technique for handling bones is: Handle"),
			orangeGivenAnswer3Diary);

		WidgetTextRequirement greenGivenAnswer3Diary = new WidgetTextRequirement(119, 3, true, "<str>I need to speak to the student in the green top about the");
		greenGivenAnswer3Diary.addRange(56, 63);
		greenStudentQ3Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Specimen brush use: Brush carefully"),
			greenGivenAnswer3Diary);

		// 2550 = 1, gotten invite
		// 3644 = 1, gotten invite
		givenTalismanIn = new VarbitRequirement(2550, 1);
		rope1Added = new VarbitRequirement(2545, 1);
		rope2Added = new VarbitRequirement(2546, 1);

		// 45 - 54
		hasTeddy = new Conditions(LogicType.OR, teddybear, talkedToFemaleStudent);
		hasSkull = new Conditions(LogicType.OR, skull, talkedToGreenStudent);
		hasSpecialCup = new Conditions(LogicType.OR, specialCup, talkedToOrangeStudent);
		letterStamped = new VarbitRequirement(2552, 1);


		searchedBricks = new VarbitRequirement(2549, 1);
		openPowderChestNearby = new ObjectCondition(ObjectID.CHEST_2360);
		openedBarrel = new VarbitRequirement(2547, 1);

		hasKeyOrPowderOrMixtures = new Conditions(LogicType.OR,
			key, powder, nitrate, mixedChemicals, mixedChemicals2, chemicalCompound, openPowderChestNearby);
	}

	public void setupSteps()
	{
		talkToExaminer = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre south east of Varrock.");
		talkToExaminer.addDialogStep("Can I take an exam?");
		((NpcStep) (talkToExaminer)).addAlternateNpcs(NpcID.EXAMINER_3636, NpcID.EXAMINER_3637);
		talkToHaig = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3448, 0),
			"Talk to Curator Haig in the Varrock Museum.", unstampedLetter);
		talkToExaminer2 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Return to an Examiner in the Exam Centre south east of Varrock.", sealedLetter);

		searchBush = new ObjectStep(this, ObjectID.BUSH_2358, new WorldPoint(3357, 3372, 0), "Search the bushes north of the Exam Centre for a teddy.");
		takeTray = new DetailedQuestStep(this, new WorldPoint(3369, 3378, 0), "Pick up the tray in the south east of the dig site.", panningTray);
		talkToGuide = new NpcStep(this, NpcID.PANNING_GUIDE, new WorldPoint(3385, 3386, 0), "Talk to the Panning Guide nearby.", tea);
		panWater = new ObjectStep(this, ObjectID.PANNING_POINT, new WorldPoint(3384, 3381, 0), "Pan in the river for a special cup.", panningTray);
		searchPanningTray = new DetailedQuestStep(this, "Search the panning tray.", panningTrayFull.highlighted());
		panWater.addSubSteps(searchPanningTray);

		pickpocketWorkmen = new NpcStep(this, NpcID.DIGSITE_WORKMAN, new WorldPoint(3372, 3390, 0), "Pickpocket workmen until you get an animal skull and a specimen brush.", true);
		((NpcStep) (pickpocketWorkmen)).addAlternateNpcs(NpcID.DIGSITE_WORKMAN_3630, NpcID.DIGSITE_WORKMAN_3631);
		((NpcStep) (pickpocketWorkmen)).setMaxRoamRange(100);
		talkToFemaleStudent = new NpcStep(this, NpcID.STUDENT_3634, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite twice.", teddybear);
		talkToOrangeStudent = new NpcStep(this, NpcID.STUDENT_3633, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite twice.", specialCup);
		talkToGreenStudent = new NpcStep(this, NpcID.STUDENT, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite twice.", skull);
		talkToFemaleStudent2 = new NpcStep(this, NpcID.STUDENT_3634, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite.");
		talkToOrangeStudent2 = new NpcStep(this, NpcID.STUDENT_3633, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite.");
		talkToGreenStudent2 = new NpcStep(this, NpcID.STUDENT, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite.");
		talkToFemaleStudent.addSubSteps(talkToFemaleStudent2);
		talkToOrangeStudent.addSubSteps(talkToOrangeStudent2);
		talkToGreenStudent.addSubSteps(talkToGreenStudent2);
		takeTest1 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre to take the first test.");
		takeTest1.addDialogSteps("Yes, I certainly am.", "The study of the earth, its contents and history.",
			"All that have passed the appropriate Earth Sciences exam.",
			"Gloves and boots to be worn at all times; proper tools must be used.");

		talkToFemaleStudent3 = new NpcStep(this, NpcID.STUDENT_3634, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite.");
		talkToOrangeStudent3 = new NpcStep(this, NpcID.STUDENT_3633, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite.");
		talkToGreenStudent3 = new NpcStep(this, NpcID.STUDENT, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite.");
		takeTest2 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre to take the second test.");
		takeTest2.addDialogSteps("I am ready for the next exam.", "Samples taken in rough form; kept only in sealed containers.",
			"Finds must be carefully handled, and gloves worn.", "Always handle with care; strike cleanly on its cleaving point.");

		talkToFemaleStudent4 = new NpcStep(this, NpcID.STUDENT_3634, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite.", opal);
		talkToFemaleStudent5 = new NpcStep(this, NpcID.STUDENT_3634, new WorldPoint(3345, 3425, 0), "Talk to the female student again.", opal);
		talkToOrangeStudent4 = new NpcStep(this, NpcID.STUDENT_3633, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite.");
		talkToGreenStudent4 = new NpcStep(this, NpcID.STUDENT, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite.");
		takeTest3 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre to take the third test.");
		takeTest3.addDialogSteps("I am ready for the last exam...", "Samples cleaned, and carried only in specimen jars.",
			"Brush carefully and slowly using short strokes.", "Handle bones very carefully and keep them away from other samples.");

		getJar = new ObjectStep(this, ObjectID.CUPBOARD_17302, new WorldPoint(3355, 3332, 0), "Search the cupboard on the south wall of the west room of the Exam Centre for a specimen jar.");
		((ObjectStep) (getJar)).addAlternateObjects(ObjectID.CUPBOARD_17303);
		getBrush = new NpcStep(this, NpcID.DIGSITE_WORKMAN, new WorldPoint(3372, 3390, 0), "Pickpocket workmen until you get a specimen brush.", true);
		((NpcStep) (getBrush)).addAlternateNpcs(NpcID.DIGSITE_WORKMAN_3630, NpcID.DIGSITE_WORKMAN_3631);
		((NpcStep) (getBrush)).setMaxRoamRange(100);
		// NOTE: May not need pick
		digForTalisman = new ObjectStep(this, ObjectID.SOIL_2377, new WorldPoint(3374, 3438, 0), "Dig in the north east dig spot in the Digsite until you get a talisman.",
			trowelHighlighted, specimenJar, specimenBrush);
		digForTalisman.addIcon(ItemID.TROWEL);
		talkToExpert = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3357, 3334, 0), "Talk Archaeological expert in the Exam Centre.", talisman);

		useInvitationOnWorkman = new NpcStep(this, NpcID.DIGSITE_WORKMAN, new WorldPoint(3360, 3415, 0), "Use the invitation on any workman.", true, invitation);
		useInvitationOnWorkman.addIcon(ItemID.INVITATION_LETTER);
		useInvitationOnWorkman.addDialogStep("I lost the letter you gave me.");
		((NpcStep) (useInvitationOnWorkman)).addAlternateNpcs(NpcID.DIGSITE_WORKMAN_3630, NpcID.DIGSITE_WORKMAN_3631);
		((NpcStep) (useInvitationOnWorkman)).setMaxRoamRange(100);
		useRopeOnWinch = new ObjectStep(this, ObjectID.WINCH_2350, new WorldPoint(3353, 3417, 0), "Use a rope on the west winch.", rope);
		useRopeOnWinch.addIcon(ItemID.ROPE);
		goDownWinch = new ObjectStep(this, ObjectID.WINCH_2350, new WorldPoint(3353, 3417, 0), "Climb down the west winch.");
		pickUpRoot = new ItemStep(this, "Pick up some arcenia root.", arcenia);
		searchBricks = new ObjectStep(this, ObjectID.BRICK_2362, new WorldPoint(3378, 9824, 0), "Search the bricks to the south.");

		goUpRope = new ObjectStep(this, ObjectID.ROPE_2353, new WorldPoint(3369, 9826, 0), "Climb back to the surface.");
		useRopeOnWinch2 = new ObjectStep(this, ObjectID.WINCH_2351, new WorldPoint(3370, 3429, 0), "Use a rope on the north east winch.", rope);
		useRopeOnWinch2.addIcon(ItemID.ROPE);
		goDownToDoug = new ObjectStep(this, ObjectID.WINCH_2351, new WorldPoint(3370, 3429, 0), "Climb down the north east winch.");
		talkToDoug = new NpcStep(this, NpcID.DOUG_DEEPING, new WorldPoint(3351, 9819, 0), "Talk to Doug Deeping.");
		talkToDoug.addDialogStep("How could I move a large pile of rocks?");
		goUpFromDoug = new ObjectStep(this, ObjectID.ROPE_2352, new WorldPoint(3352, 9816, 0), "Leave Doug's cave.");
		unlockChest = new ObjectStep(this, ObjectID.CHEST_2361, new WorldPoint(3374, 3378, 0), "Use the key on the chest in the tent in the south of the Digsite.", key);
		unlockChest.addIcon(ItemID.CHEST_KEY_709);
		searchChest = new ObjectStep(this, ObjectID.CHEST_2360, new WorldPoint(3374, 3378, 0), "Search the chest.");
		useTrowelOnBarrel = new ObjectStep(this, NullObjectID.NULL_2359, new WorldPoint(3364, 3378, 0),
			"Use a trowel on the barrel west of the chest's tent.", trowelHighlighted);
		useTrowelOnBarrel.addIcon(ItemID.TROWEL);
		useVialOnBarrel = new ObjectStep(this, NullObjectID.NULL_2359, new WorldPoint(3364, 3378, 0), "Use a vial on the barrel west of the chest's tent.", vialHighlighted);
		useVialOnBarrel.addIcon(ItemID.VIAL);
		usePowderOnExpert = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3357, 3334, 0), "Use the powder on the Archaeological expert in the Exam Centre.", powder);
		usePowderOnExpert.addIcon(ItemID.CHEMICAL_POWDER);
		useLiquidOnExpert = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3357, 3334, 0), "(DO NOT LEFT CLICK) Right-click use the liquid on the Archaeological expert in the Exam Centre.", liquid);
		useLiquidOnExpert.addIcon(ItemID.UNIDENTIFIED_LIQUID);
		mixNitroWithNitrate = new DetailedQuestStep(this, "Mix the nitroglycerin and ammonium nitrate together.", nitro, nitrate);
		grindCharcoal = new DetailedQuestStep(this, "Grind charcoal with a pestle and mortar.", pestleAndMortar, charcoal);
		addCharcoal = new DetailedQuestStep(this, "Add charcoal to the vial.", groundCharcoal, mixedChemicals);
		addRoot = new DetailedQuestStep(this, "Add arcenia root to the vial.", arcenia, mixedChemicals2);
		goDownToExplode = new ObjectStep(this, ObjectID.WINCH_2350, new WorldPoint(3353, 3417, 0), "Climb down the rope on the west winch.", chemicalCompound, tinderbox);
		goDownToExplode2 = new ObjectStep(this, ObjectID.WINCH_2350, new WorldPoint(3353, 3417, 0), "Climb down the rope on the west winch.", tinderbox);
		goDownToExplode.addSubSteps(goDownToExplode2);
		useCompound = new ObjectStep(this, ObjectID.BRICK_2362, new WorldPoint(3378, 9824, 0), "Use the compound on the bricks to the south.", chemicalCompoundHighlighted);
		useCompound.addIcon(ItemID.CHEMICAL_COMPOUND);

		useTinderbox = new ObjectStep(this, ObjectID.BRICK_2362, new WorldPoint(3378, 9824, 0), "Use a tinderbox on the bricks to the south.", tinderboxHighlighted);
		useTinderbox.addIcon(ItemID.TINDERBOX);
		takeTablet = new ObjectStep(this, NullObjectID.NULL_17369, new WorldPoint(3373, 9746, 0), "Take the stone tablet in the south room.");
		goDownForTablet = new ObjectStep(this, ObjectID.WINCH_2350, new WorldPoint(3353, 3417, 0), "Climb down the rope on the west winch.");
		takeTablet.addSubSteps(goDownForTablet);

		goUpWithTablet = new ObjectStep(this, ObjectID.ROPE_2353, new WorldPoint(3369, 9762, 0), "Use the tablet on the Archaeological expert in the Exam Centre to complete the quest.", tablet);
		useTabletOnExpert = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3357, 3334, 0), "Use the tablet on the Archaeological expert in the Exam Centre to complete the quest.", tablet);
		useTabletOnExpert.addIcon(ItemID.STONE_TABLET);
		useTabletOnExpert.addSubSteps(goUpWithTablet);

		syncStep = new DetailedQuestStep(this, "Open the quest's journal to sync your current quest state.");
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("This quest helper is susceptible to getting out of sync with the actual quest. If this happens to you, opening up the quest's journal should fix it.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pestleAndMortar, vialHighlighted, tinderbox, tea, ropes2, opal, charcoal);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(digsiteTeleports, varrock2);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.AGILITY, 10, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 10, true));
		req.add(new SkillRequirement(Skill.THIEVING, 25));
		return req;
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
				new ExperienceReward(Skill.MINING, 15300),
				new ExperienceReward(Skill.HERBLORE, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("2 x Gold Bars", ItemID.GOLD_BAR, 2));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to clean specimens in the Varrock Museum"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToExaminer, talkToHaig, talkToExaminer2, searchBush, takeTray, talkToGuide, panWater, pickpocketWorkmen, talkToFemaleStudent, talkToFemaleStudent2,
			talkToOrangeStudent, talkToOrangeStudent2, talkToGreenStudent, talkToGreenStudent2, takeTest1), tea));

		allSteps.add(new PanelDetails("Exam 2", Arrays.asList(talkToFemaleStudent3, talkToOrangeStudent3, talkToGreenStudent3, takeTest2)));
		allSteps.add(new PanelDetails("Exam 3", Arrays.asList(talkToFemaleStudent4, talkToFemaleStudent5, talkToOrangeStudent4,
			talkToGreenStudent4, takeTest3), opal));
		allSteps.add(new PanelDetails("Discovery", Arrays.asList(getJar, digForTalisman, talkToExpert, useInvitationOnWorkman), trowel, specimenBrush));
		allSteps.add(new PanelDetails("Digging deeper", Arrays.asList(useRopeOnWinch, goDownWinch, pickUpRoot, searchBricks, goUpRope, useRopeOnWinch2, goDownToDoug,
			talkToDoug, goUpFromDoug, unlockChest, searchChest, useTrowelOnBarrel, useVialOnBarrel, useLiquidOnExpert, usePowderOnExpert, mixNitroWithNitrate, grindCharcoal, addCharcoal, addRoot, goDownToExplode,
			useCompound, useTinderbox, takeTablet, useTabletOnExpert), ropes2, pestleAndMortar, vialHighlighted, tinderboxHighlighted, charcoal));

		return allSteps;
	}
}
