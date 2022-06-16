/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.thegolem;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_GOLEM
)
public class TheGolem extends BasicQuestHelper
{
	//Items Required
	ItemRequirement strangeImplement, strangeImplementHighlight, programHighlight, pestleAndMortarHighlight, mushroomHighlight, vial, inkHighlight, pestleAndMortar,
		papyrus, letter, clay4Highlight, notesHighlight, phoenixFeather, quill, clay3Highlight, clay2Highlight, clay1Highlight, key, statuette, statuetteHighlight,
		papyrusHighlight;

	//Items Recommended
	ItemRequirement varrockTeleport, digsiteTeleport, waterskins;

	Requirement inRuin, turnedStatue1, turnedStatue2, turnedStatue3, turnedStatue4,hasReadLetter, added1Clay, added2Clay, added3Clay, talkedToElissa,
		hasReadNotes, talkedToCurator, inUpstairsMuseum, stolenStatuette, inThroneRoom, openedHead, enteredRuins;

	QuestStep pickUpLetter, readLetter, talkToGolem, useClay, useClay2, useClay3, useClay4, talkToElissa, searchBookcase, readBook, talkToCurator, pickpocketCurator, goUpInMuseum, openCabinet,
		stealFeather, enterRuin, useStatuette, turnStatue1, turnStatue2, turnStatue3, turnStatue4, enterThroneRoom, leaveThroneRoom, leaveRuin, pickBlackMushroom, grindMushroom,
		useFeatherOnInk, talkToGolemAfterPortal, useQuillOnPapyrus, useImplementOnGolem, useProgramOnGolem, pickUpImplement, enterRuinWithoutStatuette, enterRuinForFirstTime;

	//Zones
	Zone ruin, upstairsMuseum, throneRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupRequirements();
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
		goTalkToElissa.addStep(new Conditions(notesHighlight, enteredRuins), readBook);
		goTalkToElissa.addStep(new Conditions(talkedToElissa, enteredRuins), searchBookcase);
		goTalkToElissa.addStep(new Conditions(inRuin, strangeImplement), talkToElissa);
		goTalkToElissa.addStep(new Conditions(inRuin), pickUpImplement);
		goTalkToElissa.addStep(new Conditions(hasReadLetter, enteredRuins), talkToElissa);
		goTalkToElissa.addStep(hasReadLetter, enterRuinForFirstTime);
		goTalkToElissa.addStep(letter, readLetter);

		steps.put(2, goTalkToElissa);

		ConditionalStep getStatuette = new ConditionalStep(this, pickpocketCurator);
		getStatuette.addStep(new Conditions(stolenStatuette, inRuin), useStatuette);
		getStatuette.addStep(stolenStatuette, enterRuin);
		getStatuette.addStep(new Conditions(key, inUpstairsMuseum), openCabinet);
		getStatuette.addStep(key, goUpInMuseum);

		steps.put(3, getStatuette);

		ConditionalStep openPortal = new ConditionalStep(this, enterRuin);
		openPortal.addStep(new Conditions(turnedStatue4, turnedStatue3, turnedStatue2, inRuin), turnStatue1);
		openPortal.addStep(new Conditions(turnedStatue4, turnedStatue3, inRuin), turnStatue2);
		openPortal.addStep(new Conditions(turnedStatue4, inRuin), turnStatue3);
		openPortal.addStep(new Conditions(inRuin), turnStatue4);

		steps.put(4, openPortal);

		ConditionalStep goEnterPortal = new ConditionalStep(this, enterRuin);
		goEnterPortal.addStep(new Conditions(inRuin, strangeImplement), enterThroneRoom);
		goEnterPortal.addStep(new Conditions(inRuin), pickUpImplement);

		steps.put(5, goEnterPortal);

		ConditionalStep returnToTheGolem = new ConditionalStep(this, talkToGolemAfterPortal);
		returnToTheGolem.addStep(new Conditions(inRuin, strangeImplement), leaveRuin);
		returnToTheGolem.addStep(new Conditions(inRuin), pickUpImplement);
		returnToTheGolem.addStep(inThroneRoom, leaveThroneRoom);

		steps.put(6, returnToTheGolem);

		ConditionalStep reprogramTheGolem = new ConditionalStep(this, enterRuinWithoutStatuette);
		reprogramTheGolem.addStep(new Conditions(openedHead, programHighlight), useProgramOnGolem);
		reprogramTheGolem.addStep(new Conditions(strangeImplement, programHighlight), useImplementOnGolem);
		reprogramTheGolem.addStep(new Conditions(strangeImplement, quill), useQuillOnPapyrus);
		reprogramTheGolem.addStep(new Conditions(strangeImplement, phoenixFeather, inkHighlight), useFeatherOnInk);
		reprogramTheGolem.addStep(new Conditions(strangeImplement, inkHighlight), stealFeather);
		reprogramTheGolem.addStep(new Conditions(strangeImplement, mushroomHighlight), grindMushroom);
		reprogramTheGolem.addStep(new Conditions(inRuin, strangeImplement), leaveRuin);
		reprogramTheGolem.addStep(new Conditions(inRuin), pickUpImplement);
		reprogramTheGolem.addStep(new Conditions(strangeImplement), pickBlackMushroom);
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

	@Override
	public void setupRequirements()
	{
		letter = new ItemRequirement("Letter", ItemID.LETTER_4615);
		letter.setHighlightInInventory(true);

		strangeImplement = new ItemRequirement("Strange implement", ItemID.STRANGE_IMPLEMENT);
		strangeImplementHighlight = new ItemRequirement("Strange implement", ItemID.STRANGE_IMPLEMENT);
		strangeImplementHighlight.setHighlightInInventory(true);

		programHighlight = new ItemRequirement("Golem program", ItemID.GOLEM_PROGRAM);
		programHighlight.setHighlightInInventory(true);

		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortarHighlight = pestleAndMortar.highlighted();

		mushroomHighlight = new ItemRequirement("Black mushroom", ItemID.BLACK_MUSHROOM);
		mushroomHighlight.setHighlightInInventory(true);

		vial = new ItemRequirement("Vial", ItemID.VIAL);

		inkHighlight = new ItemRequirement("Black mushroom ink", ItemID.BLACK_DYE);
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
		statuette.setTooltip("If you've lost it, talk to the Curator in the Varrock museum again");

		statuetteHighlight = new ItemRequirement("Statuette", ItemID.STATUETTE);
		statuetteHighlight.setHighlightInInventory(true);
		statuetteHighlight.setTooltip("If you've lost it, talk to the Curator in the Varrock museum again");

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		digsiteTeleport = new ItemRequirement("Digsite teleport", ItemCollections.DIGSITE_PENDANTS);
		digsiteTeleport.addAlternates(ItemID.DIGSITE_TELEPORT);
		waterskins = new ItemRequirement("Waterskins", ItemID.WATERSKIN4, -1);
	}

	private void setupConditions()
	{
		inRuin = new ZoneRequirement(ruin);
		inUpstairsMuseum = new ZoneRequirement(upstairsMuseum);
		inThroneRoom = new ZoneRequirement(throneRoom);

		hasReadLetter = new VarbitRequirement(347, 1, Operation.GREATER_EQUAL);
		talkedToElissa = new VarbitRequirement(347, 2, Operation.GREATER_EQUAL);
		hasReadNotes = new VarbitRequirement(347, 3, Operation.GREATER_EQUAL);
		talkedToCurator = new VarbitRequirement(347, 4, Operation.GREATER_EQUAL);

		added1Clay = new VarbitRequirement(348, 1);
		added2Clay = new VarbitRequirement(348, 2);
		added3Clay = new VarbitRequirement(348, 3);

		turnedStatue1 = new VarbitRequirement(349, 1);
		turnedStatue2 = new VarbitRequirement(350, 1);
		turnedStatue3 = new VarbitRequirement(351, 0);
		turnedStatue4 = new VarbitRequirement(352, 2);

		openedHead = new VarbitRequirement(353, 1);

		stolenStatuette = new Conditions(LogicType.OR, new VarbitRequirement(355, 1), statuette);

		enteredRuins = new VarbitRequirement(356, 1);
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
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(clay4Highlight, vial, pestleAndMortar, papyrus);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(varrockTeleport, digsiteTeleport, waterskins);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(new SkillRequirement(Skill.CRAFTING, 20),
			new SkillRequirement(Skill.THIEVING, 25, true));
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.THIEVING, 1000),
				new ExperienceReward(Skill.CRAFTING, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Rubies (by using a chisel and hammer on the throne)", ItemID.RUBY, 2),
				new ItemReward("Emeralds (by using a chisel and hammer on the throne)", ItemID.EMERALD, 2),
				new ItemReward("Sapphires (by using a chisel and hammer on the throne)", ItemID.SAPPHIRE, 2));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to take the Carpet ride from Shanty Pass to Uzer."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToGolem, useClay, pickUpLetter, enterRuinForFirstTime,
			pickUpImplement), clay4Highlight));
		allSteps.add(new PanelDetails("Finding the statuette", Arrays.asList(talkToElissa, searchBookcase, readBook,
			talkToCurator, pickpocketCurator, goUpInMuseum, openCabinet)));
		allSteps.add(new PanelDetails("Opening the portal", Arrays.asList(enterRuin, useStatuette, turnStatue1,
			enterThroneRoom, leaveThroneRoom, talkToGolemAfterPortal, pickBlackMushroom, grindMushroom,
			stealFeather, useFeatherOnInk, useQuillOnPapyrus, useProgramOnGolem), vial, pestleAndMortar, papyrus));

		return allSteps;
	}
}
