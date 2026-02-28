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

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
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
import com.questhelper.steps.QuestSyncStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

public class TheDigSite extends BasicQuestHelper
{
	// Required items
	ItemRequirement pestleAndMortar;
	ItemRequirement vial;
	ItemRequirement tinderbox;
	ItemRequirement tea;
	ItemRequirement ropes2;
	ItemRequirement rope;
	ItemRequirement opal;
	ItemRequirement charcoal;

	// Recommended items
	ItemRequirement varrock2;
	ItemRequirement digsiteTeleports;

	// Mid-quest item requirements
	ItemRequirement specimenBrush;
	ItemRequirement specimenJar;
	ItemRequirement panningTray;
	ItemRequirement panningTrayFull;
	ItemRequirement trowel;
	ItemRequirement sealedLetter;
	ItemRequirement specialCup;
	ItemRequirement teddybear;
	ItemRequirement skull;
	ItemRequirement nitro;
	ItemRequirement nitrate;
	ItemRequirement chemicalCompound;
	ItemRequirement groundCharcoal;
	ItemRequirement invitation;
	ItemRequirement talisman;
	ItemRequirement mixedChemicals;
	ItemRequirement mixedChemicals2;
	ItemRequirement arcenia;

	ItemRequirement powder;
	ItemRequirement liquid;
	ItemRequirement tablet;
	ItemRequirement key;
	ItemRequirement unstampedLetter;
	ItemRequirement trowelHighlighted;
	ItemRequirement chemicalCompoundHighlighted;

	// Zones
	Zone undergroundTemple1;
	Zone undergroundTemple2;
	Zone dougRoom;

	// Miscellaneous requirements
	Conditions talkedToFemaleStudent;
	Conditions talkedToOrangeStudent;
	Conditions talkedToGreenStudent;
	VarbitRequirement talkedToGuide;
	VarbitRequirement letterStamped;
	Conditions hasTeddy;
	Conditions hasSkull;
	Conditions hasSpecialCup;
	Conditions femaleStudentQ1Learnt;
	Conditions orangeStudentQ1Learnt;
	Conditions greenStudentQ1Learnt;
	Conditions femaleStudentQ2Learnt;
	Conditions orangeStudentQ2Learnt;
	Conditions greenStudentQ2Learnt;
	Conditions femaleStudentQ3Learnt;
	Conditions femaleExtorting;
	Conditions orangeStudentQ3Learnt;
	Conditions greenStudentQ3Learnt;
	Conditions syncedUp;
	Conditions syncedUp2;
	Conditions syncedUp3;
	VarbitRequirement givenTalismanIn;
	VarbitRequirement rope1Added;
	VarbitRequirement rope2Added;
	ZoneRequirement inUndergroundTemple1;
	ZoneRequirement inDougRoom;
	VarbitRequirement openedBarrel;
	VarbitRequirement searchedBricks;
	Conditions hasKeyOrPowderOrMixtures;
	ObjectCondition openPowderChestNearby;
	ZoneRequirement inUndergroundTemple2;
	Conditions knowStateAsJustStartedQuest;
	Conditions knowStateAsJustCompletedFirstExam;
	Conditions knowStateAsJustCompletedSecondExam;

	// Steps
	NpcStep talkToExaminer;
	NpcStep talkToHaig;
	NpcStep talkToExaminer2;

	ObjectStep searchBush;
	DetailedQuestStep takeTray;
	NpcStep talkToGuide;
	ObjectStep panWater;
	DetailedQuestStep searchPanningTray;

	NpcStep pickpocketWorkmen;
	NpcStep talkToFemaleStudent;
	NpcStep talkToOrangeStudent;
	NpcStep talkToGreenStudent;
	NpcStep talkToFemaleStudent2;
	NpcStep talkToOrangeStudent2;
	NpcStep talkToGreenStudent2;
	NpcStep takeTest1;

	NpcStep talkToFemaleStudent3;
	NpcStep talkToOrangeStudent3;
	NpcStep talkToGreenStudent3;
	NpcStep takeTest2;

	NpcStep talkToFemaleStudent5;
	NpcStep talkToOrangeStudent4;
	NpcStep talkToGreenStudent4;
	NpcStep talkToFemaleStudent4;
	NpcStep takeTest3;

	ObjectStep getJar;
	NpcStep getBrush;
	ObjectStep digForTalisman;
	NpcStep talkToExpert;

	NpcStep useInvitationOnWorkman;
	ObjectStep useRopeOnWinch;
	ObjectStep goDownWinch;
	ItemStep pickUpRoot;
	ObjectStep searchBricks;

	ObjectStep goUpRope;
	ObjectStep useRopeOnWinch2;
	ObjectStep goDownToDoug;
	NpcStep talkToDoug;
	ObjectStep goUpFromDoug;
	ObjectStep unlockChest;
	ObjectStep searchChest;
	ObjectStep useTrowelOnBarrel;
	ObjectStep useVialOnBarrel;
	NpcStep usePowderOnExpert;
	NpcStep useLiquidOnExpert;
	DetailedQuestStep mixNitroWithNitrate;
	DetailedQuestStep grindCharcoal;
	DetailedQuestStep addCharcoal;
	DetailedQuestStep addRoot;
	ObjectStep goDownToExplode;
	ObjectStep goDownToExplode2;
	ObjectStep useCompound;

	ObjectStep useTinderbox;
	ObjectStep takeTablet;
	ObjectStep goDownForTablet;

	ObjectStep goUpWithTablet;
	NpcStep useTabletOnExpert;

	QuestSyncStep syncStep;

	@Override
	protected void setupZones()
	{
		undergroundTemple1 = new Zone(new WorldPoint(3359, 9800, 0), new WorldPoint(3393, 9855, 0));
		undergroundTemple2 = new Zone(new WorldPoint(3360, 9734, 0), new WorldPoint(3392, 9790, 0));
		dougRoom = new Zone(new WorldPoint(3340, 9809, 0), new WorldPoint(3357, 9826, 0));
	}

	@Override
	protected void setupRequirements()
	{
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortar.setHighlightInInventory(true);
		vial = new ItemRequirement("Vial", ItemID.VIAL_EMPTY);
		vial.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tea = new ItemRequirement("Cup of tea", ItemID.CUP_OF_TEA);
		ropes2 = new ItemRequirement("Rope", ItemID.ROPE, 2);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);
		opal = new ItemRequirement("Opal", ItemID.OPAL);
		opal.setTooltip("You can get one by panning at the Digsite");
		opal.addAlternates(ItemID.UNCUT_OPAL);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		charcoal.setTooltip("Obtainable during quest by searching specimen trays");
		charcoal.setHighlightInInventory(true);

		varrock2 = new ItemRequirement("Varrock teleports", ItemID.POH_TABLET_VARROCKTELEPORT, 2);
		digsiteTeleports = new ItemRequirement("Digsite teleports", ItemID.TELEPORTSCROLL_DIGSITE, 2);

		specimenBrush = new ItemRequirement("Specimen brush", ItemID.SPECIMEN_BRUSH).isNotConsumed();
		specimenJar = new ItemRequirement("Specimen jar", ItemID.SPECIMEN_JAR).isNotConsumed();
		panningTray = new ItemRequirement("Panning tray", ItemID.TRAY_EMPTY).isNotConsumed();
		panningTray.addAlternates(ItemID.TRAY_GOLD, ItemID.TRAY_MUD);
		panningTrayFull = new ItemRequirement("Panning tray", ItemID.TRAY_MUD).isNotConsumed();
		trowel = new ItemRequirement("Trowel", ItemID.TROWEL).isNotConsumed();
		trowel.setTooltip("You can get another from one of the Examiners");
		trowelHighlighted = new ItemRequirement("Trowel", ItemID.TROWEL).isNotConsumed();
		trowelHighlighted.setHighlightInInventory(true);
		trowelHighlighted.setTooltip("You can get another from one of the Examiners");
		sealedLetter = new ItemRequirement("Sealed letter", ItemID.RECOMMENDEDLETTER);
		sealedLetter.setTooltip("You can get another from Curator Haig in the Varrock Museum");
		specialCup = new ItemRequirement("Special cup", ItemID.ROCK_SAMPLE2);
		teddybear = new ItemRequirement("Teddy", ItemID.ROCK_SAMPLE3);
		skull = new ItemRequirement("Animal skull", ItemID.ROCK_SAMPLE1);
		nitro = new ItemRequirement("Nitroglycerin", ItemID.NITROGLYCERIN);
		nitro.setHighlightInInventory(true);
		nitrate = new ItemRequirement("Ammonium nitrate", ItemID.AMMONIUM_NITRATE);
		nitrate.setHighlightInInventory(true);
		chemicalCompound = new ItemRequirement("Chemical compound", ItemID.DIGCOMPOUND);
		chemicalCompoundHighlighted = new ItemRequirement("Chemical compound", ItemID.DIGCOMPOUND);
		chemicalCompoundHighlighted.setHighlightInInventory(true);
		groundCharcoal = new ItemRequirement("Ground charcoal", ItemID.GROUND_CHARCOAL);
		groundCharcoal.setTooltip("You can make this by use a pestle and mortar on some charcoal. You can get charcoal from one of the specimen trays in the Digsite");
		groundCharcoal.setHighlightInInventory(true);
		invitation = new ItemRequirement("Invitation letter", ItemID.DIGEXPERTSCROLL);
		invitation.setTooltip("You can get another from the Archaeological expert");
		invitation.setHighlightInInventory(true);
		talisman = new ItemRequirement("Ancient talisman", ItemID.DIGTALISMAN);
		mixedChemicals = new ItemRequirement("Mixed chemicals", ItemID.PRECHARCOALMIXTURE);
		mixedChemicals.setHighlightInInventory(true);
		mixedChemicals2 = new ItemRequirement("Mixed chemicals", ItemID.POSTCHARCOALMIXTURE);
		mixedChemicals2.setHighlightInInventory(true);
		arcenia = new ItemRequirement("Arcenia root", ItemID.ARCENIA_ROOT);
		arcenia.setHighlightInInventory(true);

		powder = new ItemRequirement("Chemical powder", ItemID.UNIDENTIFIED_POWDER);
		powder.setHighlightInInventory(true);
		liquid = new ItemRequirement("Unidentified liquid", ItemID.UNIDENTIFIED_LIQUID);
		liquid.setHighlightInInventory(true);
		tablet = new ItemRequirement("Stone tablet", ItemID.ZAROSSTONETABLET);
		tablet.setHighlightInInventory(true);
		key = new ItemRequirement("Chest key", ItemID.DIGCHESTKEY);
		key.setHighlightInInventory(true);
		unstampedLetter = new ItemRequirement("Unstamped letter", ItemID.DIGPLAINLETTER);
		unstampedLetter.setTooltip("You can get another from the Exam Centre's examiners");

		inUndergroundTemple1 = new ZoneRequirement(undergroundTemple1);
		inUndergroundTemple2 = new ZoneRequirement(undergroundTemple2);
		inDougRoom = new ZoneRequirement(dougRoom);

		knowStateAsJustStartedQuest = new Conditions(true, new VarplayerRequirement(VarPlayerID.ITEXAMLEVEL, 1, Operation.LESS_EQUAL));
		knowStateAsJustCompletedFirstExam = new Conditions(true, new VarplayerRequirement(VarPlayerID.ITEXAMLEVEL, 2, Operation.LESS_EQUAL));
		knowStateAsJustCompletedSecondExam = new Conditions(true, new VarplayerRequirement(VarPlayerID.ITEXAMLEVEL, 3, Operation.LESS_EQUAL));

		syncedUp = new Conditions(true, LogicType.OR, knowStateAsJustStartedQuest,
			new WidgetTextRequirement(InterfaceID.Questjournal.TITLE, "The Dig Site"));

		syncedUp2 = new Conditions(true, LogicType.OR, knowStateAsJustCompletedFirstExam,
			new WidgetTextRequirement(InterfaceID.Questjournal.TITLE, "The Dig Site"),
			new DialogRequirement("You got all the questions correct. Well done!"),
			new DialogRequirement("Hey! Excellent!"));

		syncedUp3 = new Conditions(true, LogicType.OR, knowStateAsJustCompletedSecondExam,
			new WidgetTextRequirement(InterfaceID.Questjournal.TITLE, "The Dig Site"),
			new DialogRequirement("You got all the questions correct, well done!"),
			new DialogRequirement("Great, I'm getting good at this."));

		talkedToGuide = new VarbitRequirement(VarbitID.ITDIGSITETEA, 1);
		tea = tea.hideConditioned(talkedToGuide);

		// Exam questions 1
		talkedToFemaleStudent = new Conditions(true, LogicType.OR,
			new DialogRequirement("Hey! My lucky mascot!"),
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I should talk to her to see if she can help"));
		femaleStudentQ1Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The proper health and safety points are"),
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "She gave me an answer"));

		WidgetTextRequirement orangeGivenAnswer1Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "He gave me an answer to one of the questions");
		orangeGivenAnswer1Diary.addRange(20, 35);
		talkedToOrangeStudent = new Conditions(true, LogicType.OR,
			new DialogRequirement("Look what I found!"),
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>He has lost his Special Cup"));
		orangeStudentQ1Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The people eligible to use the digsite are:"),
			orangeGivenAnswer1Diary);

		WidgetTextRequirement greenGivenAnswer1Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "He gave me an answer to one of the questions");
		greenGivenAnswer1Diary.addRange(0, 19);

		talkedToGreenStudent = new Conditions(true, LogicType.OR,
			new DialogRequirement("Oh wow! You've found it!"),
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>He has lost his Animal Skull"));
		greenStudentQ1Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The study of Earth Sciences is:"),
			greenGivenAnswer1Diary);

		// Exam questions 2
		WidgetTextRequirement femaleGivenAnswer2Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I need to speak to the student in the purple skirt about");
		femaleGivenAnswer2Diary.addRange(43, 52);
		femaleStudentQ2Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Finds handling: Finds must"),
			femaleGivenAnswer2Diary);

		WidgetTextRequirement orangeGivenAnswer2Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I need to speak to the student in the orange top about the");
		orangeGivenAnswer2Diary.addRange(43, 52);
		orangeStudentQ2Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Correct sample transportation: "),
			orangeGivenAnswer2Diary);

		WidgetTextRequirement greenGivenAnswer2Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I need to speak to the student in the green top about the");
		greenGivenAnswer2Diary.addRange(43, 52);
		greenStudentQ2Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Correct rock pick usage: Always handle"),
			greenGivenAnswer2Diary);

		// Exam questions 3
		femaleExtorting = new Conditions(true, LogicType.OR,
			new DialogRequirement("OK, I'll see what I can turn up for you."),
			new DialogRequirement("Well, I have seen people get them from panning"),
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I need to bring her an opal"));
		WidgetTextRequirement femaleGivenAnswer3Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I need to bring her an opal");
		femaleGivenAnswer3Diary.addRange(56, 63);
		femaleStudentQ3Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Sample preparation: Samples cleaned"),
			femaleGivenAnswer3Diary);

		WidgetTextRequirement orangeGivenAnswer3Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I need to speak to the student in the orange top about the");
		orangeGivenAnswer3Diary.addRange(56, 63);
		orangeStudentQ3Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("The proper technique for handling bones is: Handle"),
			orangeGivenAnswer3Diary);

		WidgetTextRequirement greenGivenAnswer3Diary = new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I need to speak to the student in the green top about the");
		greenGivenAnswer3Diary.addRange(54, 63);
		greenStudentQ3Learnt = new Conditions(true, LogicType.OR,
			new DialogRequirement("Specimen brush use: Brush carefully"),
			greenGivenAnswer3Diary);

		// 2550 = 1, gotten invite
		// 3644 = 1, gotten invite
		givenTalismanIn = new VarbitRequirement(VarbitID.ITEXPERTLETTER, 1);
		rope1Added = new VarbitRequirement(VarbitID.ITDIGSITEWINCH1, 1);
		rope2Added = new VarbitRequirement(VarbitID.ITDIGSITEWINCH2, 1);

		// 45 - 54
		hasTeddy = or(teddybear, talkedToFemaleStudent);
		hasSkull = or(skull, talkedToGreenStudent);
		hasSpecialCup = or(specialCup, talkedToOrangeStudent);
		letterStamped = new VarbitRequirement(VarbitID.ITCURATORLETTER, 1);

		searchedBricks = new VarbitRequirement(VarbitID.ITDIGSITEHINT, 1);
		openPowderChestNearby = new ObjectCondition(ObjectID.DIGCHESTOPEN);
		openedBarrel = new VarbitRequirement(VarbitID.ITDIGSITEBARREL, 1);

		hasKeyOrPowderOrMixtures = or(key, powder, nitrate, mixedChemicals, mixedChemicals2, chemicalCompound, openPowderChestNearby);
	}

	public void setupSteps()
	{
		talkToExaminer = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre south east of Varrock.");
		talkToExaminer.addDialogStep("Can I take an exam?");
		talkToExaminer.addDialogStep("Yes.");
		talkToExaminer.addAlternateNpcs(NpcID.QIP_DIGSITE_EXAMINER_02, NpcID.QIP_DIGSITE_EXAMINER_03);
		talkToHaig = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3257, 3448, 0),
			"Talk to Curator Haig in the Varrock Museum.", unstampedLetter);
		talkToExaminer2 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Return to an Examiner in the Exam Centre south east of Varrock.", sealedLetter);

		searchBush = new ObjectStep(this, ObjectID.DIGSITEBUSHSAMPLE, new WorldPoint(3357, 3372, 0), "Search the bushes north of the Exam Centre for a teddy.");
		takeTray = new DetailedQuestStep(this, new WorldPoint(3369, 3378, 0), "Pick up the tray in the south east of the dig site.", panningTray);
		talkToGuide = new NpcStep(this, NpcID.PANNING_GUIDE, new WorldPoint(3385, 3386, 0), "Talk to the Panning Guide nearby.", tea);
		panWater = new ObjectStep(this, ObjectID.PANNING_POINT, new WorldPoint(3384, 3381, 0), "Pan in the river for a special cup.", panningTray);
		searchPanningTray = new DetailedQuestStep(this, "Search the panning tray.", panningTrayFull.highlighted());
		panWater.addSubSteps(searchPanningTray);

		pickpocketWorkmen = new NpcStep(this, NpcID.DIGWORKMAN1, new WorldPoint(3372, 3390, 0), "Pickpocket workmen until you get an animal skull and a specimen brush.", true);
		pickpocketWorkmen.addAlternateNpcs(NpcID.QIP_DIGSITE_DIGWORKMAN_03, NpcID.QIP_DIGSITE_DIGWORKMAN_04);
		pickpocketWorkmen.setMaxRoamRange(100);
		talkToFemaleStudent = new NpcStep(this, NpcID.STUDENT2, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite twice.", teddybear);
		talkToOrangeStudent = new NpcStep(this, NpcID.STUDENT3, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite twice.", specialCup);
		talkToGreenStudent = new NpcStep(this, NpcID.STUDENT1, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite twice.", skull);
		talkToFemaleStudent2 = new NpcStep(this, NpcID.STUDENT2, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite.");
		talkToOrangeStudent2 = new NpcStep(this, NpcID.STUDENT3, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite.");
		talkToGreenStudent2 = new NpcStep(this, NpcID.STUDENT1, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite.");
		talkToFemaleStudent.addSubSteps(talkToFemaleStudent2);
		talkToOrangeStudent.addSubSteps(talkToOrangeStudent2);
		talkToGreenStudent.addSubSteps(talkToGreenStudent2);
		takeTest1 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre to take the first test.");
		takeTest1.addDialogSteps("Yes, I certainly am.", "The study of the earth, its contents and history.",
			"All that have passed the appropriate Earth Sciences exam.",
			"Gloves and boots to be worn at all times; proper tools must be used.");

		talkToFemaleStudent3 = new NpcStep(this, NpcID.STUDENT2, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite.");
		talkToOrangeStudent3 = new NpcStep(this, NpcID.STUDENT3, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite.");
		talkToGreenStudent3 = new NpcStep(this, NpcID.STUDENT1, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite.");
		takeTest2 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre to take the second test.");
		takeTest2.addDialogSteps("I am ready for the next exam.", "Samples taken in rough form; kept only in sealed containers.",
			"Finds must be carefully handled, and gloves worn.", "Always handle with care; strike cleanly on its cleaving point.");

		talkToFemaleStudent4 = new NpcStep(this, NpcID.STUDENT2, new WorldPoint(3345, 3425, 0), "Talk to the female student in the north west of the Digsite.", opal);
		talkToFemaleStudent5 = new NpcStep(this, NpcID.STUDENT2, new WorldPoint(3345, 3425, 0), "Talk to the female student again.", opal);
		talkToOrangeStudent4 = new NpcStep(this, NpcID.STUDENT3, new WorldPoint(3369, 3419, 0), "Talk to the student in an orange shirt in the north east of the Digsite.");
		talkToGreenStudent4 = new NpcStep(this, NpcID.STUDENT1, new WorldPoint(3362, 3398, 0), "Talk to the student in a green shirt in the south of the Digsite.");
		takeTest3 = new NpcStep(this, NpcID.EXAMINER, new WorldPoint(3362, 3337, 0), "Talk to an Examiner in the Exam Centre to take the third test.");
		takeTest3.addDialogSteps("I am ready for the last exam...", "Samples cleaned, and carried only in specimen jars.",
			"Brush carefully and slowly using short strokes.", "Handle bones very carefully and keep them away from other samples.");

		getJar = new ObjectStep(this, ObjectID.QIP_DIGSITE_CUPBOARDSHUT, new WorldPoint(3355, 3332, 0), "Search the cupboard on the south wall of the west room of the Exam Centre for a specimen jar.");
		getJar.addAlternateObjects(ObjectID.QIP_DIGSITE_CUPBOARDOPEN);
		getBrush = new NpcStep(this, NpcID.DIGWORKMAN1, new WorldPoint(3372, 3390, 0), "Pickpocket workmen until you get a specimen brush.", true);
		getBrush.addAlternateNpcs(NpcID.QIP_DIGSITE_DIGWORKMAN_03, NpcID.QIP_DIGSITE_DIGWORKMAN_04);
		getBrush.setMaxRoamRange(100);
		// NOTE: May not need pick
		digForTalisman = new ObjectStep(this, ObjectID.DIGDUGUPSOIL2, new WorldPoint(3374, 3438, 0), "Dig in the north east dig spot in the Digsite until you get a talisman.",
			trowelHighlighted, specimenJar, specimenBrush);
		digForTalisman.addIcon(ItemID.TROWEL);
		digForTalisman.addSubSteps(getBrush);
		talkToExpert = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3357, 3334, 0), "Talk to the Archaeological expert in the Exam Centre.", talisman);

		useInvitationOnWorkman = new NpcStep(this, NpcID.DIGWORKMAN1, new WorldPoint(3360, 3415, 0), "Use the invitation on any workman.", true, invitation);
		useInvitationOnWorkman.addIcon(ItemID.DIGEXPERTSCROLL);
		useInvitationOnWorkman.addDialogStep("I lost the letter you gave me.");
		useInvitationOnWorkman.addAlternateNpcs(NpcID.QIP_DIGSITE_DIGWORKMAN_03, NpcID.QIP_DIGSITE_DIGWORKMAN_04);
		useInvitationOnWorkman.setMaxRoamRange(100);
		useRopeOnWinch = new ObjectStep(this, ObjectID.DIGWINCH1, new WorldPoint(3353, 3417, 0), "Use a rope on the west winch.", rope);
		useRopeOnWinch.addIcon(ItemID.ROPE);
		goDownWinch = new ObjectStep(this, ObjectID.DIGWINCH1, new WorldPoint(3353, 3417, 0), "Climb down the west winch.");
		pickUpRoot = new ItemStep(this, "Pick up some arcenia root.", arcenia);
		searchBricks = new ObjectStep(this, ObjectID.DIGBLASTBRICK, new WorldPoint(3378, 9824, 0), "Search the bricks to the south.");

		goUpRope = new ObjectStep(this, ObjectID.WINCHLADDER2, new WorldPoint(3369, 9826, 0), "Climb back to the surface.");
		useRopeOnWinch2 = new ObjectStep(this, ObjectID.DIGWINCH2, new WorldPoint(3370, 3429, 0), "Use a rope on the north east winch.", rope);
		useRopeOnWinch2.addIcon(ItemID.ROPE);
		goDownToDoug = new ObjectStep(this, ObjectID.DIGWINCH2, new WorldPoint(3370, 3429, 0), "Climb down the north east winch.");
		talkToDoug = new NpcStep(this, NpcID.DIGWORKMAN2, new WorldPoint(3351, 9819, 0), "Talk to Doug Deeping.");
		talkToDoug.addDialogStep("How could I move a large pile of rocks?");
		goUpFromDoug = new ObjectStep(this, ObjectID.WINCHLADDER1, new WorldPoint(3352, 9816, 0), "Leave Doug's cave.");
		unlockChest = new ObjectStep(this, ObjectID.DIGCHESTCLOSED, new WorldPoint(3374, 3378, 0), "Use the key on the chest in the tent in the south of the Digsite.", key);
		unlockChest.addIcon(ItemID.DIGCHESTKEY);
		searchChest = new ObjectStep(this, ObjectID.DIGCHESTOPEN, new WorldPoint(3374, 3378, 0), "Search the chest.");
		useTrowelOnBarrel = new ObjectStep(this, ObjectID.DIGBARRELCLOSED, new WorldPoint(3364, 3378, 0),
			"Use a trowel on the barrel west of the chest's tent.", trowelHighlighted);
		useTrowelOnBarrel.addIcon(ItemID.TROWEL);
		useVialOnBarrel = new ObjectStep(this, ObjectID.DIGBARRELCLOSED, new WorldPoint(3364, 3378, 0), "Use a vial on the barrel west of the chest's tent.", vial);
		useVialOnBarrel.addIcon(ItemID.VIAL_EMPTY);
		usePowderOnExpert = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3357, 3334, 0), "Use the powder on the Archaeological expert in the Exam Centre.", powder);
		usePowderOnExpert.addIcon(ItemID.UNIDENTIFIED_POWDER);
		useLiquidOnExpert = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3357, 3334, 0), "(DO NOT LEFT CLICK) Right-click use the liquid on the Archaeological expert in the Exam Centre.", liquid);
		useLiquidOnExpert.addIcon(ItemID.UNIDENTIFIED_LIQUID);
		mixNitroWithNitrate = new DetailedQuestStep(this, "Mix the nitroglycerin and ammonium nitrate together.", nitro, nitrate);
		grindCharcoal = new DetailedQuestStep(this, "Grind charcoal with a pestle and mortar.", pestleAndMortar, charcoal);
		addCharcoal = new DetailedQuestStep(this, "Add charcoal to the vial.", groundCharcoal, mixedChemicals);
		addRoot = new DetailedQuestStep(this, "Add arcenia root to the vial.", arcenia, mixedChemicals2);
		goDownToExplode = new ObjectStep(this, ObjectID.DIGWINCH1, new WorldPoint(3353, 3417, 0), "Climb down the rope on the west winch.", chemicalCompound, tinderbox);
		goDownToExplode2 = new ObjectStep(this, ObjectID.DIGWINCH1, new WorldPoint(3353, 3417, 0), "Climb down the rope on the west winch.", tinderbox);
		goDownToExplode.addSubSteps(goDownToExplode2);
		useCompound = new ObjectStep(this, ObjectID.DIGBLASTBRICK, new WorldPoint(3378, 9824, 0), "Use the compound on the bricks to the south.", chemicalCompoundHighlighted);
		useCompound.addIcon(ItemID.DIGCOMPOUND);

		useTinderbox = new ObjectStep(this, ObjectID.DIGBLASTBRICK, new WorldPoint(3378, 9824, 0), "Use a tinderbox on the bricks to the south.", tinderbox.highlighted());
		useTinderbox.addIcon(ItemID.TINDERBOX);
		takeTablet = new ObjectStep(this, ObjectID.QIP_DIGSITE_ZAROS_STONE_TABLET_MULTILOC, new WorldPoint(3373, 9746, 0), "Take the stone tablet in the south room.");
		goDownForTablet = new ObjectStep(this, ObjectID.DIGWINCH1, new WorldPoint(3353, 3417, 0), "Climb down the rope on the west winch.");
		takeTablet.addSubSteps(goDownForTablet);

		goUpWithTablet = new ObjectStep(this, ObjectID.WINCHLADDER2, new WorldPoint(3369, 9762, 0), "Use the tablet on the Archaeological expert in the Exam Centre to complete the quest.", tablet);
		useTabletOnExpert = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3357, 3334, 0), "Use the tablet on the Archaeological expert in the Exam Centre to complete the quest.", tablet);
		useTabletOnExpert.addIcon(ItemID.ZAROSSTONETABLET);
		useTabletOnExpert.addSubSteps(goUpWithTablet);

		syncStep = new QuestSyncStep(this, getQuest(), "Open the quest's journal to sync your current quest state.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToExaminer);

		var returnWithLetter = new ConditionalStep(this, talkToHaig);
		// This is used to set knowStateAsJustStartedQuest to true
		returnWithLetter.addStep(nor(knowStateAsJustStartedQuest), talkToExaminer);
		returnWithLetter.addStep(letterStamped, talkToExaminer2);
		steps.put(1, returnWithLetter);

		var goTakeTest1 = new ConditionalStep(this, syncStep);
		// Sets the state of knowStateAsJustCompleted to true for ConditionalStep after this
		goTakeTest1.addStep(nor(knowStateAsJustCompletedFirstExam), takeTest1);

		goTakeTest1.addStep(and(femaleStudentQ1Learnt, orangeStudentQ1Learnt, greenStudentQ1Learnt), takeTest1);
		goTakeTest1.addStep(and(femaleStudentQ1Learnt, orangeStudentQ1Learnt, talkedToGreenStudent), talkToGreenStudent2);
		goTakeTest1.addStep(and(femaleStudentQ1Learnt, orangeStudentQ1Learnt, hasSkull, specimenBrush), talkToGreenStudent);

		goTakeTest1.addStep(and(femaleStudentQ1Learnt, talkedToOrangeStudent, hasSkull, specimenBrush), talkToOrangeStudent2);
		goTakeTest1.addStep(and(femaleStudentQ1Learnt, hasSpecialCup, hasSkull, specimenBrush), talkToOrangeStudent);

		goTakeTest1.addStep(and(talkedToFemaleStudent, hasSpecialCup, hasSkull, specimenBrush), talkToFemaleStudent2);
		goTakeTest1.addStep(and(hasTeddy, hasSpecialCup, hasSkull, specimenBrush), talkToFemaleStudent);

		goTakeTest1.addStep(and(syncedUp, hasTeddy, hasSpecialCup), pickpocketWorkmen);
		goTakeTest1.addStep(and(syncedUp, hasTeddy, panningTrayFull, talkedToGuide), searchPanningTray);
		goTakeTest1.addStep(and(syncedUp, hasTeddy, panningTray, talkedToGuide), panWater);
		goTakeTest1.addStep(and(syncedUp, hasTeddy, panningTray), talkToGuide);
		goTakeTest1.addStep(and(syncedUp, hasTeddy), takeTray);
		goTakeTest1.addStep(syncedUp, searchBush);
		steps.put(2, goTakeTest1);

		var goTakeTest2 = new ConditionalStep(this, syncStep);
		// Sets the state of knowStateAsJustCompletedFirstExam to true for ConditionalStep after this
		goTakeTest1.addStep(nor(knowStateAsJustCompletedSecondExam), takeTest2);

		goTakeTest2.addStep(and(syncedUp2, femaleStudentQ2Learnt, orangeStudentQ2Learnt, greenStudentQ2Learnt), takeTest2);
		goTakeTest2.addStep(and(syncedUp2, femaleStudentQ2Learnt, orangeStudentQ2Learnt), talkToGreenStudent3);
		goTakeTest2.addStep(and(syncedUp2, femaleStudentQ2Learnt), talkToOrangeStudent3);
		goTakeTest2.addStep(syncedUp2, talkToFemaleStudent3);
		steps.put(3, goTakeTest2);

		var goTakeTest3 = new ConditionalStep(this, syncStep);
		goTakeTest3.addStep(and(femaleStudentQ3Learnt, orangeStudentQ3Learnt, greenStudentQ3Learnt), takeTest3);
		goTakeTest3.addStep(and(syncedUp3, femaleStudentQ3Learnt, orangeStudentQ3Learnt), talkToGreenStudent4);
		goTakeTest3.addStep(and(syncedUp3, femaleStudentQ3Learnt), talkToOrangeStudent4);
		goTakeTest3.addStep(and(syncedUp3, femaleExtorting), talkToFemaleStudent5);
		goTakeTest3.addStep(syncedUp3, talkToFemaleStudent4);
		steps.put(4, goTakeTest3);

		var findTalisman = new ConditionalStep(this, getJar);
		findTalisman.addStep(and(givenTalismanIn), useInvitationOnWorkman);
		findTalisman.addStep(and(talisman), talkToExpert);
		findTalisman.addStep(and(specimenJar, specimenBrush), digForTalisman);
		findTalisman.addStep(and(specimenJar), getBrush);
		steps.put(5, findTalisman);

		var learnHowToMakeExplosives = new ConditionalStep(this, useRopeOnWinch2);
		learnHowToMakeExplosives.addStep(inDougRoom, talkToDoug);
		learnHowToMakeExplosives.addStep(and(inUndergroundTemple1, arcenia), goUpRope);
		learnHowToMakeExplosives.addStep(inUndergroundTemple1, pickUpRoot);
		learnHowToMakeExplosives.addStep(and(rope2Added), goDownToDoug);

		var makeExplosives = new ConditionalStep(this, goDownWinch);
		makeExplosives.addStep(and(chemicalCompound, inUndergroundTemple1), useCompound);

		makeExplosives.addStep(inDougRoom, goUpFromDoug);
		makeExplosives.addStep(and(inUndergroundTemple1, arcenia), goUpRope);
		makeExplosives.addStep(inUndergroundTemple1, pickUpRoot);

		makeExplosives.addStep(chemicalCompound, goDownToExplode);
		makeExplosives.addStep(and(arcenia, mixedChemicals2), addRoot);
		makeExplosives.addStep(and(arcenia, mixedChemicals, groundCharcoal), addCharcoal);
		makeExplosives.addStep(and(arcenia, mixedChemicals), grindCharcoal);
		makeExplosives.addStep(and(arcenia, nitrate, nitro), mixNitroWithNitrate);
		makeExplosives.addStep(and(arcenia, powder, nitro), usePowderOnExpert);
		makeExplosives.addStep(and(arcenia, powder, liquid), useLiquidOnExpert);
		makeExplosives.addStep(and(arcenia, powder, liquid), useVialOnBarrel);
		makeExplosives.addStep(and(arcenia, powder, openedBarrel), useVialOnBarrel);
		makeExplosives.addStep(and(arcenia, powder), useTrowelOnBarrel);
		makeExplosives.addStep(and(openPowderChestNearby, arcenia), searchChest);
		makeExplosives.addStep(arcenia, unlockChest);

		var discovery = new ConditionalStep(this, useRopeOnWinch);
		discovery.addStep(hasKeyOrPowderOrMixtures, makeExplosives);
		discovery.addStep(searchedBricks, learnHowToMakeExplosives);
		discovery.addStep(and(inUndergroundTemple1, arcenia), searchBricks);
		discovery.addStep(inUndergroundTemple1, pickUpRoot);
		discovery.addStep(rope1Added, goDownWinch);
		steps.put(6, discovery);

		var explodeWall = new ConditionalStep(this, goDownToExplode2);
		explodeWall.addStep(inUndergroundTemple1, useTinderbox);
		steps.put(7, explodeWall);

		var completeQuest = new ConditionalStep(this, goDownForTablet);
		completeQuest.addStep(and(tablet.alsoCheckBank(), inUndergroundTemple2), goUpWithTablet);
		completeQuest.addStep(and(tablet.alsoCheckBank()), useTabletOnExpert);
		completeQuest.addStep(inUndergroundTemple2, takeTablet);
		steps.put(8, completeQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.AGILITY, 10, true),
			new SkillRequirement(Skill.HERBLORE, 10, true),
			new SkillRequirement(Skill.THIEVING, 25)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			pestleAndMortar,
			vial,
			tinderbox,
			tea,
			ropes2,
			opal,
			charcoal
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			digsiteTeleports,
			varrock2
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"This quest helper is susceptible to getting out of sync with the actual quest. If this happens to you, opening up the quest's journal should fix it."
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
			new ExperienceReward(Skill.MINING, 15300),
			new ExperienceReward(Skill.HERBLORE, 2000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Gold Bars", ItemID.GOLD_BAR, 2)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to clean specimens in the Varrock Museum")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToExaminer,
			talkToHaig,
			talkToExaminer2,
			searchBush,
			takeTray,
			talkToGuide,
			panWater,
			pickpocketWorkmen,
			talkToFemaleStudent,
			talkToFemaleStudent2,
			talkToOrangeStudent,
			talkToOrangeStudent2,
			talkToGreenStudent,
			talkToGreenStudent2,
			takeTest1
		), List.of(
			tea
		)));

		sections.add(new PanelDetails("Exam 2", List.of(
			talkToFemaleStudent3,
			talkToOrangeStudent3,
			talkToGreenStudent3,
			takeTest2
		)));

		sections.add(new PanelDetails("Exam 3", List.of(
			talkToFemaleStudent4,
			talkToFemaleStudent5,
			talkToOrangeStudent4,
			talkToGreenStudent4,
			takeTest3
		), List.of(
			opal
		)));

		sections.add(new PanelDetails("Discovery", List.of(
			getJar,
			digForTalisman,
			talkToExpert,
			useInvitationOnWorkman
		), List.of(
			trowel,
			specimenBrush
		)));

		sections.add(new PanelDetails("Digging deeper", List.of(
			useRopeOnWinch,
			goDownWinch,
			pickUpRoot,
			searchBricks,
			goUpRope,
			useRopeOnWinch2,
			goDownToDoug,
			talkToDoug,
			goUpFromDoug,
			unlockChest,
			searchChest,
			useTrowelOnBarrel,
			useVialOnBarrel,
			useLiquidOnExpert,
			usePowderOnExpert,
			mixNitroWithNitrate,
			grindCharcoal,
			addCharcoal,
			addRoot,
			goDownToExplode,
			useCompound,
			useTinderbox,
			takeTablet,
			useTabletOnExpert
		), List.of(
			ropes2,
			trowelHighlighted,
			pestleAndMortar,
			vial,
			tinderbox,
			charcoal
		)));

		return sections;
	}
}
