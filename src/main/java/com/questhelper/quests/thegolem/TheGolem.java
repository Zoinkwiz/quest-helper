package com.questhelper.quests.thegolem;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
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
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_GOLEM
)
public class TheGolem extends BasicQuestHelper
{
	ItemRequirement strangeImplement, strangeImplementHighlight, programHighlight, pestleAndMortarHighlight, mushroomHighlight, vial, inkHighlight, pestleAndMortar,
		papyrus, letter, clay4Highlight, notesHighlight, phoenixFeather, quill, clay3Highlight, clay2Highlight, clay1Highlight, key, statuette, statuetteHighlight,
		papyrusHighlight, varrockTeleport, digsiteTeleport, waterskins;

	ConditionForStep inRuin, turnedStatue1, turnedStatue2, turnedStatue3, turnedStatue4, hasLetter, hasReadLetter, added1Clay, added2Clay, added3Clay,
		talkedToElissa, hasVarmenNotes, hasReadNotes, talkedToCurator, hasKey, inUpstairsMuseum, stolenStatuette, inThroneRoom, hasImplement, openedHead,
		hasMushroom, hasInk, hasFeather, hasQuill, hasProgram, enteredRuins;

	Zone ruin, upstairsMuseum, throneRoom;

	QuestStep pickUpLetter, readLetter, talkToGolem, useClay, useClay2, useClay3, useClay4, talkToElissa, searchBookcase, readBook, talkToCurator, pickpocketCurator, goUpInMuseum, openCabinet,
		stealFeather, enterRuin, useStatuette, turnStatue1, turnStatue2, turnStatue3, turnStatue4, enterThroneRoom, leaveThroneRoom, leaveRuin, pickBlackMushroom, grindMushroom,
		useFeatherOnInk, talkToGolemAfterPortal, useQuillOnPapyrus, useImplementOnGolem, useProgramOnGolem, pickUpImplement, enterRuinWithoutStatuette, enterRuinForFirstTime;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, talkToGolem);

		ConditionalStep repairGolem = new ConditionalStep(this, useClay);
		repairGolem.addStep(added3Clay, useClay4);
		repairGolem.addStep(added2Clay, useClay3);
		repairGolem.addStep(added1Clay, useClay2);

		steps.put(1, repairGolem);

		ConditionalStep goTalkToElissa = new ConditionalStep(this, pickUpLetter);
		goTalkToElissa.addStep(talkedToCurator, pickpocketCurator);
		goTalkToElissa.addStep(new Conditions(hasReadNotes, enteredRuins), talkToCurator);
		goTalkToElissa.addStep(new Conditions(hasVarmenNotes, enteredRuins), readBook);
		goTalkToElissa.addStep(new Conditions(talkedToElissa, enteredRuins), searchBookcase);
		goTalkToElissa.addStep(new Conditions(inRuin, hasImplement), talkToElissa);
		goTalkToElissa.addStep(new Conditions(inRuin), pickUpImplement);
		goTalkToElissa.addStep(new Conditions(hasReadLetter, enteredRuins), talkToElissa);
		goTalkToElissa.addStep(hasReadLetter, enterRuinForFirstTime);
		goTalkToElissa.addStep(hasLetter, readLetter);

		steps.put(2, goTalkToElissa);

		ConditionalStep getStatuette = new ConditionalStep(this, pickpocketCurator);
		getStatuette.addStep(new Conditions(stolenStatuette, inRuin), useStatuette);
		getStatuette.addStep(stolenStatuette, enterRuin);
		getStatuette.addStep(new Conditions(hasKey, inUpstairsMuseum), openCabinet);
		getStatuette.addStep(hasKey, goUpInMuseum);

		steps.put(3, getStatuette);

		ConditionalStep openPortal = new ConditionalStep(this, enterRuin);
		openPortal.addStep(new Conditions(turnedStatue4, turnedStatue3, turnedStatue2, inRuin), turnStatue1);
		openPortal.addStep(new Conditions(turnedStatue4, turnedStatue3, inRuin), turnStatue2);
		openPortal.addStep(new Conditions(turnedStatue4, inRuin), turnStatue3);
		openPortal.addStep(new Conditions(inRuin), turnStatue4);

		steps.put(4, openPortal);

		ConditionalStep goEnterPortal = new ConditionalStep(this, enterRuin);
		goEnterPortal.addStep(new Conditions(inRuin, hasImplement), enterThroneRoom);
		goEnterPortal.addStep(new Conditions(inRuin), pickUpImplement);

		steps.put(5, goEnterPortal);

		ConditionalStep returnToTheGolem = new ConditionalStep(this, talkToGolemAfterPortal);
		returnToTheGolem.addStep(new Conditions(inRuin, hasImplement), leaveRuin);
		returnToTheGolem.addStep(new Conditions(inRuin), pickUpImplement);
		returnToTheGolem.addStep(inThroneRoom, leaveThroneRoom);

		steps.put(6, returnToTheGolem);

		ConditionalStep reprogramTheGolem = new ConditionalStep(this, enterRuinWithoutStatuette);
		reprogramTheGolem.addStep(new Conditions(openedHead, hasProgram), useProgramOnGolem);
		reprogramTheGolem.addStep(new Conditions(hasImplement, hasProgram), useImplementOnGolem);
		reprogramTheGolem.addStep(new Conditions(hasImplement, hasQuill), useQuillOnPapyrus);
		reprogramTheGolem.addStep(new Conditions(hasImplement, hasFeather, hasInk), useFeatherOnInk);
		reprogramTheGolem.addStep(new Conditions(hasImplement, hasInk), stealFeather);
		reprogramTheGolem.addStep(new Conditions(hasImplement, hasMushroom), grindMushroom);
		reprogramTheGolem.addStep(new Conditions(inRuin, hasImplement), leaveRuin);
		reprogramTheGolem.addStep(new Conditions(inRuin), pickUpImplement);
		reprogramTheGolem.addStep(new Conditions(hasImplement), pickBlackMushroom);
		reprogramTheGolem.addStep(inThroneRoom, leaveThroneRoom);

		steps.put(7, reprogramTheGolem);

		return steps;
	}

	private void setupZones()
	{
		ruin = new Zone(new WorldPoint(2706, 4881, 0), new WorldPoint(2738, 4918, 0));
		upstairsMuseum = new Zone(new WorldPoint(3249, 3440, 1), new WorldPoint(3269, 3457, 1));
		throneRoom = new Zone(new WorldPoint(2709, 4879, 2), new WorldPoint(2731, 4919, 2));
	}

	private void setupItemRequirements()
	{
		letter = new ItemRequirement("Letter", ItemID.LETTER_4615);
		letter.setHighlightInInventory(true);

		strangeImplement = new ItemRequirement("Strange implement", ItemID.STRANGE_IMPLEMENT);
		strangeImplementHighlight = new ItemRequirement("Strange implement", ItemID.STRANGE_IMPLEMENT);
		strangeImplementHighlight.setHighlightInInventory(true);

		programHighlight = new ItemRequirement("Golem program", ItemID.GOLEM_PROGRAM);
		programHighlight.setHighlightInInventory(true);

		pestleAndMortarHighlight = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlight.setHighlightInInventory(true);

		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);

		mushroomHighlight = new ItemRequirement("Black mushroom", ItemID.BLACK_MUSHROOM);
		mushroomHighlight.setHighlightInInventory(true);

		vial = new ItemRequirement("Vial", ItemID.VIAL);

		inkHighlight = new ItemRequirement("Black mushroom ink", ItemID.BLACK_MUSHROOM_INK);
		inkHighlight.setHighlightInInventory(true);

		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		papyrusHighlight = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		papyrusHighlight.setHighlightInInventory(true);

		clay4Highlight = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY, 4);
		clay4Highlight.setHighlightInInventory(true);
		clay3Highlight = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY, 3);
		clay3Highlight.setHighlightInInventory(true);
		clay2Highlight = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY, 2);
		clay2Highlight.setHighlightInInventory(true);
		clay1Highlight = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY, 1);
		clay1Highlight.setHighlightInInventory(true);

		notesHighlight = new ItemRequirement("Varmen's notes", ItemID.VARMENS_NOTES);
		notesHighlight.setHighlightInInventory(true);

		phoenixFeather = new ItemRequirement("Phoenix feather", ItemID.PHOENIX_FEATHER);
		phoenixFeather.setHighlightInInventory(true);
		quill = new ItemRequirement("Phoenix quill pen", ItemID.PHOENIX_QUILL_PEN);
		quill.setHighlightInInventory(true);

		key = new ItemRequirement("Display cabinet key", ItemID.DISPLAY_CABINET_KEY);

		statuette = new ItemRequirement("Statuette", ItemID.STATUETTE);
		statuette.setTip("If you've lost it, talk to the Curator in the Varrock museum again");

		statuetteHighlight = new ItemRequirement("Statuette", ItemID.STATUETTE);
		statuetteHighlight.setHighlightInInventory(true);
		statuetteHighlight.setTip("If you've lost it, talk to the Curator in the Varrock museum again");

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		digsiteTeleport = new ItemRequirement("Digsite teleport", ItemID.DIGSITE_TELEPORT);
		digsiteTeleport.addAlternates(ItemCollections.getDigsitePendants());
		waterskins = new ItemRequirement("Waterskins", ItemID.WATERSKIN4, -1);
	}

	private void setupConditions()
	{
		inRuin = new ZoneCondition(ruin);
		inUpstairsMuseum = new ZoneCondition(upstairsMuseum);
		inThroneRoom = new ZoneCondition(throneRoom);

		hasReadLetter = new VarbitCondition(347, 1, Operation.GREATER_EQUAL);
		talkedToElissa = new VarbitCondition(347, 2, Operation.GREATER_EQUAL);
		hasReadNotes = new VarbitCondition(347, 3, Operation.GREATER_EQUAL);
		talkedToCurator = new VarbitCondition(347, 4, Operation.GREATER_EQUAL);

		added1Clay = new VarbitCondition(348, 1);
		added2Clay = new VarbitCondition(348, 2);
		added3Clay = new VarbitCondition(348, 3);

		turnedStatue1 = new VarbitCondition(349, 1);
		turnedStatue2 = new VarbitCondition(350, 1);
		turnedStatue3 = new VarbitCondition(351, 0);
		turnedStatue4 = new VarbitCondition(352, 2);

		openedHead = new VarbitCondition(353, 1);

		stolenStatuette = new Conditions(LogicType.OR, new VarbitCondition(355, 1), new ItemRequirementCondition(statuette));

		enteredRuins = new VarbitCondition(356, 1);

		hasLetter = new ItemRequirementCondition(letter);
		hasVarmenNotes = new ItemRequirementCondition(notesHighlight);
		hasKey = new ItemRequirementCondition(key);
		hasImplement = new ItemRequirementCondition(strangeImplement);
		hasMushroom = new ItemRequirementCondition(mushroomHighlight);
		hasInk = new ItemRequirementCondition(inkHighlight);
		hasFeather = new ItemRequirementCondition(phoenixFeather);
		hasQuill = new ItemRequirementCondition(quill);
		hasProgram = new ItemRequirementCondition(programHighlight);
	}

	private void setupSteps()
	{
		pickUpLetter = new DetailedQuestStep(this, new WorldPoint(3479, 3092, 0), "Pick up the letter on the floor in Uzer and read it.", letter);
		readLetter = new DetailedQuestStep(this, "Read the letter.", letter);
		pickUpLetter.addSubSteps(readLetter);

		talkToGolem = new NpcStep(this, NpcID.BROKEN_CLAY_GOLEM, new WorldPoint(3485, 3088, 0), "Talk to the Golem in Uzer.");
		talkToGolem.addDialogStep("Shall I try to repair you?");
		useClay = new NpcStep(this, NpcID.BROKEN_CLAY_GOLEM, new WorldPoint(3485, 3088, 0), "Use 4 soft clay on the Golem in Uzer.", clay4Highlight);
		useClay.addIcon(ItemID.SOFT_CLAY);
		useClay2 = new NpcStep(this, NpcID.BROKEN_CLAY_GOLEM, new WorldPoint(3485, 3088, 0), "Use 3 soft clay on the Golem in Uzer.", clay3Highlight);
		useClay2.addIcon(ItemID.SOFT_CLAY);
		useClay3 = new NpcStep(this, NpcID.DAMAGED_CLAY_GOLEM, new WorldPoint(3485, 3088, 0), "Use 2 soft clay on the Golem in Uzer.", clay2Highlight);
		useClay3.addIcon(ItemID.SOFT_CLAY);
		useClay4 = new NpcStep(this, NpcID.DAMAGED_CLAY_GOLEM, new WorldPoint(3485, 3088, 0), "Use 1 soft clay on the Golem in Uzer.", clay1Highlight);
		useClay4.addIcon(ItemID.SOFT_CLAY);

		enterRuinForFirstTime = new ObjectStep(this, ObjectID.STAIRCASE_6373, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.");

		talkToElissa = new NpcStep(this, NpcID.ELISSA, new WorldPoint(3378, 3428, 0), "Talk to Elissa in the north east of the Digsite.");
		talkToElissa.addDialogStep("I found a letter in the desert with your name on.");
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_6292, new WorldPoint(3367, 3332, 0), "Search the bookcase in the south east corner of the Digsite Exam Centre.");
		readBook = new DetailedQuestStep(this, "Read the notes.", notesHighlight);
		talkToCurator = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3256, 3449, 0), "Talk to Curator Haig in the Varrock Museum.");
		talkToCurator.addDialogStep("I'm looking for a statuette recovered from the city of Uzer.");
		pickpocketCurator = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3256, 3449, 0), "Pickpocket Curator Haig.");
		goUpInMuseum = new ObjectStep(this, ObjectID.STAIRCASE_11798, new WorldPoint(3267, 3453, 0), "Go to the first floor of the Varrock Museum and right-click open the golem statue's display case.", key);
		openCabinet = new ObjectStep(this, NullObjectID.NULL_24626, new WorldPoint(3257, 3453, 1), "Right-click open the golem statue's display case.", key);

		stealFeather = new NpcStep(this, NpcID.DESERT_PHOENIX, new WorldPoint(3414, 3154, 0), "Steal a feather from the desert phoenix north of Uzer.");

		enterRuin = new ObjectStep(this, ObjectID.STAIRCASE_6373, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.", statuette, pestleAndMortar, vial, papyrus);
		enterRuinWithoutStatuette = new ObjectStep(this, ObjectID.STAIRCASE_6373, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.");
		enterRuin.addSubSteps(enterRuinWithoutStatuette);

		useImplementOnGolem = new NpcStep(this, NpcID.CLAY_GOLEM_5136, new WorldPoint(3485, 3088, 0), "Use the strange implement on the Golem in Uzer.", strangeImplementHighlight);
		useImplementOnGolem.addIcon(ItemID.STRANGE_IMPLEMENT);
		useProgramOnGolem = new NpcStep(this, NpcID.CLAY_GOLEM_5136, new WorldPoint(3485, 3088, 0), "Use the strange implement on the Golem in Uzer.", programHighlight);
		useProgramOnGolem.addIcon(ItemID.GOLEM_PROGRAM);

		useStatuette = new ObjectStep(this, NullObjectID.NULL_6306, new WorldPoint(2725, 4896, 0), "Use the statue on the empty alcove.", statuetteHighlight);
		useStatuette.addIcon(ItemID.STATUETTE);
		turnStatue1 = new ObjectStep(this, NullObjectID.NULL_6303, new WorldPoint(2718, 4899, 0), "Turn each of the statuettes to face the door.");
		turnStatue2 = new ObjectStep(this, NullObjectID.NULL_6304, new WorldPoint(2718, 4896, 0), "Turn each of the statuettes to face the door.");
		turnStatue3 = new ObjectStep(this, NullObjectID.NULL_6305, new WorldPoint(2725, 4899, 0), "Turn each of the statuettes to face the door.");
		turnStatue4 = new ObjectStep(this, NullObjectID.NULL_6306, new WorldPoint(2725, 4896, 0), "Turn each of the statuettes to face the door.");
		turnStatue1.addSubSteps(turnStatue2, turnStatue3, turnStatue4);

		enterThroneRoom = new ObjectStep(this, NullObjectID.NULL_6310, new WorldPoint(2722, 4913, 0), "Enter the portal.");
		leaveThroneRoom = new ObjectStep(this, ObjectID.PORTAL_6282, new WorldPoint(2720, 4883, 2), "Leave the throne room and return to the Golem.");
		pickUpImplement = new DetailedQuestStep(this, new WorldPoint(2713, 4913, 0), "Pick up the strange implement in the north west corner of the ruin.", strangeImplement);

		leaveRuin = new ObjectStep(this, ObjectID.STAIRCASE_6372, new WorldPoint(2722, 4885, 0), "Leave the ruins.");

		pickBlackMushroom = new ObjectStep(this, ObjectID.BLACK_MUSHROOMS, new WorldPoint(3495, 3088, 0), "Pick up some black mushrooms.");
		grindMushroom = new DetailedQuestStep(this, "Grind the mushrooms into a vial.", pestleAndMortarHighlight, mushroomHighlight, vial);
		useFeatherOnInk = new DetailedQuestStep(this, "Use the phoenix feather on the ink.", phoenixFeather, inkHighlight);
		useQuillOnPapyrus = new DetailedQuestStep(this, "Use the phoenix quill on the papyrus.", quill, papyrusHighlight);
		talkToGolemAfterPortal = new NpcStep(this, NpcID.CLAY_GOLEM_5136, new WorldPoint(3485, 3088, 0), "Talk to the Golem in Uzer.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(clay4Highlight, vial, pestleAndMortar, papyrus));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(varrockTeleport, digsiteTeleport, waterskins));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToGolem, useClay, pickUpLetter, enterRuinForFirstTime, pickUpImplement)), clay4Highlight));
		allSteps.add(new PanelDetails("Finding the statuette", new ArrayList<>(Arrays.asList(talkToElissa, searchBookcase, readBook, talkToCurator, pickpocketCurator, goUpInMuseum, openCabinet))));
		allSteps.add(new PanelDetails("Opening the portal", new ArrayList<>(Arrays.asList(enterRuin, useStatuette, turnStatue1, enterThroneRoom, leaveThroneRoom, talkToGolemAfterPortal, pickBlackMushroom, grindMushroom,
			stealFeather, useFeatherOnInk, useQuillOnPapyrus, useProgramOnGolem)), vial, pestleAndMortar, papyrus));

		return allSteps;
	}
}
