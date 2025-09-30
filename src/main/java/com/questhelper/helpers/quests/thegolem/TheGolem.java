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
package com.questhelper.helpers.quests.thegolem;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class TheGolem extends BasicQuestHelper
{
	// Required items
	ItemRequirement clay4Highlight;
	ItemRequirement vial;
	ItemRequirement pestleAndMortar;
	ItemRequirement papyrus;

	// Recommended items
	ItemRequirement varrockTeleport;
	ItemRequirement digsiteTeleport;
	ItemRequirement waterskins;
	ItemRequirement necklaceOfPassage;

	// Mid-quest item requirements
	ItemRequirement pestleAndMortarHighlight;
	ItemRequirement strangeImplement;
	ItemRequirement strangeImplementHighlight;
	ItemRequirement programHighlight;
	ItemRequirement mushroomHighlight;
	ItemRequirement inkHighlight;
	ItemRequirement letter;
	ItemRequirement notesHighlight;
	ItemRequirement phoenixFeather;
	ItemRequirement quill;
	ItemRequirement clay3Highlight;
	ItemRequirement clay2Highlight;
	ItemRequirement clay1Highlight;
	ItemRequirement key;
	ItemRequirement statuette;
	ItemRequirement statuetteHighlight;
	ItemRequirement papyrusHighlight;

	// Zones
	Zone ruin;
	Zone upstairsMuseum;
	Zone throneRoom;

	// Miscellaneous requirements
	ZoneRequirement inRuin;
	ZoneRequirement inUpstairsMuseum;
	ZoneRequirement inThroneRoom;
	VarbitRequirement turnedStatue1;
	VarbitRequirement turnedStatue2;
	VarbitRequirement turnedStatue3;
	VarbitRequirement turnedStatue4;
	VarbitRequirement hasReadLetter;
	VarbitRequirement added1Clay;
	VarbitRequirement added2Clay;
	VarbitRequirement added3Clay;
	VarbitRequirement talkedToElissa;
	VarbitRequirement hasReadNotes;
	VarbitRequirement talkedToCurator;
	Conditions stolenStatuette;
	VarbitRequirement openedHead;
	VarbitRequirement enteredRuins;

	// Steps
	// -- Starting off
	NpcStep talkToGolem;

	NpcStep useClay;
	NpcStep useClay2;
	NpcStep useClay3;
	NpcStep useClay4;

	DetailedQuestStep pickUpLetter;
	DetailedQuestStep readLetter;

	ObjectStep enterRuinForFirstTime;

	DetailedQuestStep pickUpImplement;

	// -- Finding the statuette
	NpcStep talkToElissa;

	ObjectStep searchBookcase;

	DetailedQuestStep readBook;

	NpcStep talkToCurator;

	NpcStep pickpocketCurator;

	ObjectStep goUpInMuseum;

	ObjectStep openCabinet;

	// -- Opening the portal
	ObjectStep enterRuin;
	ObjectStep enterRuinWithoutStatuette;

	ObjectStep useStatuette;

	ObjectStep turnStatue1;
	ObjectStep turnStatue2;
	ObjectStep turnStatue3;
	ObjectStep turnStatue4;

	ObjectStep enterThroneRoom;
	ObjectStep leaveThroneRoom;

	ObjectStep pickBlackMushroom;

	DetailedQuestStep grindMushroom;

	NpcStep stealFeather;

	DetailedQuestStep useFeatherOnInk;

	DetailedQuestStep useQuillOnPapyrus;

	NpcStep useImplementOnGolem;

	NpcStep useProgramOnGolem;

	NpcStep talkToGolemAfterPortal;

	ObjectStep leaveRuin;

	@Override
	protected void setupZones()
	{
		ruin = new Zone(new WorldPoint(2706, 4881, 0), new WorldPoint(2738, 4918, 0));
		upstairsMuseum = new Zone(new WorldPoint(3249, 3440, 1), new WorldPoint(3269, 3457, 1));
		throneRoom = new Zone(new WorldPoint(2709, 4879, 2), new WorldPoint(2731, 4919, 2));
	}

	@Override
	protected void setupRequirements()
	{
		letter = new ItemRequirement("Letter", ItemID.GOLEM_LETTER);
		letter.setHighlightInInventory(true);

		strangeImplement = new ItemRequirement("Strange implement", ItemID.GOLEM_GOLEMKEY);
		strangeImplementHighlight = new ItemRequirement("Strange implement", ItemID.GOLEM_GOLEMKEY);
		strangeImplementHighlight.setHighlightInInventory(true);

		programHighlight = new ItemRequirement("Golem program", ItemID.GOLEM_PROGRAM);
		programHighlight.setHighlightInInventory(true);

		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestleAndMortarHighlight = pestleAndMortar.highlighted();

		mushroomHighlight = new ItemRequirement("Black mushroom", ItemID.GOLEM_MUSHROOM);
		mushroomHighlight.setHighlightInInventory(true);

		vial = new ItemRequirement("Vial", ItemID.VIAL_EMPTY);

		inkHighlight = new ItemRequirement("Black mushroom ink", ItemID.GOLEM_INK);
		inkHighlight.setHighlightInInventory(true);

		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		papyrusHighlight = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		papyrusHighlight.setHighlightInInventory(true);

		clay4Highlight = new ItemRequirement("Soft clay", ItemID.SOFTCLAY, 4);
		clay4Highlight.setHighlightInInventory(true);
		clay3Highlight = new ItemRequirement("Soft clay", ItemID.SOFTCLAY, 3);
		clay3Highlight.setHighlightInInventory(true);
		clay2Highlight = new ItemRequirement("Soft clay", ItemID.SOFTCLAY, 2);
		clay2Highlight.setHighlightInInventory(true);
		clay1Highlight = new ItemRequirement("Soft clay", ItemID.SOFTCLAY, 1);
		clay1Highlight.setHighlightInInventory(true);

		notesHighlight = new ItemRequirement("Varmen's notes", ItemID.GOLEM_NOTES);
		notesHighlight.setHighlightInInventory(true);

		phoenixFeather = new ItemRequirement("Phoenix feather", ItemID.GOLEM_PHOENIXFEATHER);
		phoenixFeather.setHighlightInInventory(true);
		quill = new ItemRequirement("Phoenix quill pen", ItemID.GOLEM_PEN);
		quill.setHighlightInInventory(true);

		key = new ItemRequirement("Display cabinet key", ItemID.GOLEM_STATUETTEKEY);

		statuette = new ItemRequirement("Statuette", ItemID.GOLEM_STATUETTE);
		statuette.setTooltip("If you've lost it, talk to the Curator in the Varrock museum again");

		statuetteHighlight = new ItemRequirement("Statuette", ItemID.GOLEM_STATUETTE);
		statuetteHighlight.setHighlightInInventory(true);
		statuetteHighlight.setTooltip("If you've lost it, talk to the Curator in the Varrock museum again");

		// Recommended items
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		digsiteTeleport = new ItemRequirement("Digsite teleport", ItemCollections.DIGSITE_PENDANTS);
		digsiteTeleport.addAlternates(ItemID.TELEPORTSCROLL_DIGSITE);
		necklaceOfPassage = new ItemRequirement("Necklace of passage to Eagle's Eyrie", ItemCollections.NECKLACE_OF_PASSAGES);
		waterskins = new ItemRequirement("Waterskins", ItemID.WATER_SKIN4, -1);

		inRuin = new ZoneRequirement(ruin);
		inUpstairsMuseum = new ZoneRequirement(upstairsMuseum);
		inThroneRoom = new ZoneRequirement(throneRoom);

		var questSideState = new VarbitBuilder(VarbitID.GOLEM_B);
		hasReadLetter = questSideState.ge(1);
		talkedToElissa = questSideState.ge(2);
		hasReadNotes = questSideState.ge(3);
		talkedToCurator = questSideState.ge(4);

		var golemClayState = new VarbitBuilder(VarbitID.GOLEM_CLAY);
		added1Clay = golemClayState.eq(1);
		added2Clay = golemClayState.eq(2);
		added3Clay = golemClayState.eq(3);

		turnedStatue1 = new VarbitRequirement(VarbitID.GOLEM_STATUETTESTATUSA, 1);
		turnedStatue2 = new VarbitRequirement(VarbitID.GOLEM_STATUETTESTATUSB, 1);
		turnedStatue3 = new VarbitRequirement(VarbitID.GOLEM_STATUETTESTATUSC, 0);
		turnedStatue4 = new VarbitRequirement(VarbitID.GOLEM_STATUETTESTATUSD, 2);

		openedHead = new VarbitRequirement(VarbitID.GOLEM_HEAD_OPEN, 1);

		stolenStatuette = or(new VarbitRequirement(VarbitID.GOLEM_RETRIEVED_STATUETTE, 1), statuette);

		enteredRuins = new VarbitRequirement(VarbitID.GOLEM_SEEN_UNDERGROUND, 1);
	}

	private void setupSteps()
	{
		// -- Starting off
		talkToGolem = new NpcStep(this, NpcID.GOLEM_BROKEN_GOLEM, new WorldPoint(3485, 3088, 0), "Talk to the Golem in Uzer.");
		talkToGolem.addDialogStep("Shall I try to repair you?");

		useClay = new NpcStep(this, NpcID.GOLEM_BROKEN_GOLEM, new WorldPoint(3485, 3088, 0), "Use 4 soft clay on the Golem in Uzer.", clay4Highlight);
		useClay.addIcon(ItemID.SOFTCLAY);
		useClay2 = new NpcStep(this, NpcID.GOLEM_BROKEN_GOLEM, new WorldPoint(3485, 3088, 0), "Use 3 soft clay on the Golem in Uzer.", clay3Highlight);
		useClay2.addIcon(ItemID.SOFTCLAY);
		useClay3 = new NpcStep(this, NpcID.GOLEM_PARTIALLY_BROKEN_GOLEM, new WorldPoint(3485, 3088, 0), "Use 2 soft clay on the Golem in Uzer.", clay2Highlight);
		useClay3.addIcon(ItemID.SOFTCLAY);
		useClay4 = new NpcStep(this, NpcID.GOLEM_PARTIALLY_BROKEN_GOLEM, new WorldPoint(3485, 3088, 0), "Use 1 soft clay on the Golem in Uzer.", clay1Highlight);
		useClay4.addIcon(ItemID.SOFTCLAY);
		useClay.addSubSteps(useClay2, useClay3, useClay4);

		pickUpLetter = new DetailedQuestStep(this, new WorldPoint(3479, 3092, 0),
			"Pick up the letter on the floor in Uzer and read it.", letter);
		pickUpLetter.addTeleport(necklaceOfPassage);
		pickUpLetter.addDialogSteps("Eagle's Eyrie");
		readLetter = new DetailedQuestStep(this, "Read the letter.", letter);
		pickUpLetter.addSubSteps(readLetter);

		enterRuinForFirstTime = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.");

		pickUpImplement = new DetailedQuestStep(this, new WorldPoint(2713, 4913, 0), "Pick up the strange implement in the north west corner of the ruin.", strangeImplement);

		// -- Finding the statuette
		talkToElissa = new NpcStep(this, NpcID.GOLEM_ELISSA, new WorldPoint(3378, 3428, 0), "Talk to Elissa in the north east of the Digsite.");
		talkToElissa.addDialogStep("I found a letter in the desert with your name on.");

		searchBookcase = new ObjectStep(this, ObjectID.GOLEM_BOOKCASE, new WorldPoint(3367, 3332, 0), "Search the bookcase in the south east corner of the Digsite Exam Centre.");

		readBook = new DetailedQuestStep(this, "Read the notes.", notesHighlight);

		talkToCurator = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3256, 3449, 0), "Talk to Curator Haig in the Varrock Museum.");
		talkToCurator.addDialogStep("I'm looking for a statuette recovered from the city of Uzer.");

		pickpocketCurator = new NpcStep(this, NpcID.CURATOR, new WorldPoint(3256, 3449, 0), "Pickpocket Curator Haig.");

		goUpInMuseum = new ObjectStep(this, ObjectID.FAI_VARROCK_WOODENSTAIRS_CASTLE, new WorldPoint(3267, 3453, 0), "Go to the first floor of the Varrock Museum and right-click open the golem statue's display case.", key);

		openCabinet = new ObjectStep(this, ObjectID.VM_TIMELINE_TERRACOTTA_STATUE, new WorldPoint(3257, 3453, 1), "Right-click open the golem statue's display case.", key);

		// -- Opening the portal
		enterRuin = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.", statuette, pestleAndMortar, vial, papyrus);
		enterRuinWithoutStatuette = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_TOP, new WorldPoint(3493, 3090, 0), "Enter the Uzer ruins.");
		enterRuin.addSubSteps(enterRuinWithoutStatuette);

		useStatuette = new ObjectStep(this, ObjectID.GOLEM_STATUETTED, new WorldPoint(2725, 4896, 0), "Use the statue on the empty alcove.", statuetteHighlight);
		useStatuette.addIcon(ItemID.GOLEM_STATUETTE);

		turnStatue1 = new ObjectStep(this, ObjectID.GOLEM_STATUETTEA, new WorldPoint(2718, 4899, 0), "Turn each of the statuettes to face the door.");
		turnStatue2 = new ObjectStep(this, ObjectID.GOLEM_STATUETTEB, new WorldPoint(2718, 4896, 0), "Turn each of the statuettes to face the door.");
		turnStatue3 = new ObjectStep(this, ObjectID.GOLEM_STATUETTEC, new WorldPoint(2725, 4899, 0), "Turn each of the statuettes to face the door.");
		turnStatue4 = new ObjectStep(this, ObjectID.GOLEM_STATUETTED, new WorldPoint(2725, 4896, 0), "Turn each of the statuettes to face the door.");
		turnStatue1.addSubSteps(turnStatue2, turnStatue3, turnStatue4);

		enterThroneRoom = new ObjectStep(this, ObjectID.GOLEM_PORTAL, new WorldPoint(2722, 4913, 0), "Enter the portal.");
		leaveThroneRoom = new ObjectStep(this, ObjectID.GOLEM_DEMON_PORTAL, new WorldPoint(2720, 4883, 2), "Leave the throne room and return to the Golem.");

		pickBlackMushroom = new ObjectStep(this, ObjectID.GOLEM_BLACK_MUSHROOMS, new WorldPoint(3495, 3088, 0), "Pick up some black mushrooms.");

		grindMushroom = new DetailedQuestStep(this, "Grind the mushrooms into a vial.", pestleAndMortarHighlight, mushroomHighlight, vial);

		stealFeather = new NpcStep(this, NpcID.GOLEM_PHOENIX, new WorldPoint(3414, 3154, 0), "Steal a feather from the desert phoenix north of Uzer.");
		stealFeather.addTeleport(necklaceOfPassage);
		stealFeather.addDialogSteps("Eagle's Eyrie");

		useFeatherOnInk = new DetailedQuestStep(this, "Use the phoenix feather on the ink.", phoenixFeather, inkHighlight);

		useQuillOnPapyrus = new DetailedQuestStep(this, "Use the phoenix quill on the papyrus.", quill, papyrusHighlight);

		useImplementOnGolem = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Use the strange implement on the Golem in Uzer.", strangeImplementHighlight);
		useImplementOnGolem.addIcon(ItemID.GOLEM_GOLEMKEY);
		useProgramOnGolem = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Use the golem program on the Golem in Uzer.", programHighlight);
		useProgramOnGolem.addIcon(ItemID.GOLEM_PROGRAM);

		talkToGolemAfterPortal = new NpcStep(this, NpcID.GOLEM_FIXED_GOLEM, new WorldPoint(3485, 3088, 0), "Talk to the Golem in Uzer.");

		leaveRuin = new ObjectStep(this, ObjectID.GOLEM_INSIDESTAIRS_BASE, new WorldPoint(2722, 4885, 0), "Leave the ruins.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToGolem);

		var repairGolem = new ConditionalStep(this, useClay);
		repairGolem.addStep(added3Clay, useClay4);
		repairGolem.addStep(added2Clay, useClay3);
		repairGolem.addStep(added1Clay, useClay2);

		steps.put(1, repairGolem);

		var goTalkToElissa = new ConditionalStep(this, pickUpLetter);
		goTalkToElissa.addStep(talkedToCurator, pickpocketCurator);
		goTalkToElissa.addStep(and(hasReadNotes, enteredRuins), talkToCurator);
		goTalkToElissa.addStep(and(notesHighlight, enteredRuins), readBook);
		goTalkToElissa.addStep(and(talkedToElissa, enteredRuins), searchBookcase);
		goTalkToElissa.addStep(and(inRuin, strangeImplement), talkToElissa);
		goTalkToElissa.addStep(inRuin, pickUpImplement);
		goTalkToElissa.addStep(and(hasReadLetter, enteredRuins), talkToElissa);
		goTalkToElissa.addStep(hasReadLetter, enterRuinForFirstTime);
		goTalkToElissa.addStep(letter, readLetter);

		steps.put(2, goTalkToElissa);

		var getStatuette = new ConditionalStep(this, pickpocketCurator);
		getStatuette.addStep(and(stolenStatuette, inRuin), useStatuette);
		getStatuette.addStep(stolenStatuette, enterRuin);
		getStatuette.addStep(and(key, inUpstairsMuseum), openCabinet);
		getStatuette.addStep(key, goUpInMuseum);

		steps.put(3, getStatuette);

		var openPortal = new ConditionalStep(this, enterRuin);
		openPortal.addStep(and(turnedStatue4, turnedStatue3, turnedStatue2, inRuin), turnStatue1);
		openPortal.addStep(and(turnedStatue4, turnedStatue3, inRuin), turnStatue2);
		openPortal.addStep(and(turnedStatue4, inRuin), turnStatue3);
		openPortal.addStep(inRuin, turnStatue4);

		steps.put(4, openPortal);

		var goEnterPortal = new ConditionalStep(this, enterRuin);
		goEnterPortal.addStep(and(inRuin, strangeImplement), enterThroneRoom);
		goEnterPortal.addStep(inRuin, pickUpImplement);

		steps.put(5, goEnterPortal);

		var returnToTheGolem = new ConditionalStep(this, talkToGolemAfterPortal);
		returnToTheGolem.addStep(and(inRuin, strangeImplement), leaveRuin);
		returnToTheGolem.addStep(inRuin, pickUpImplement);
		returnToTheGolem.addStep(inThroneRoom, leaveThroneRoom);

		steps.put(6, returnToTheGolem);

		var reprogramTheGolem = new ConditionalStep(this, enterRuinWithoutStatuette);
		reprogramTheGolem.addStep(and(openedHead, programHighlight), useProgramOnGolem);
		reprogramTheGolem.addStep(and(strangeImplement, programHighlight), useImplementOnGolem);
		reprogramTheGolem.addStep(and(strangeImplement, quill), useQuillOnPapyrus);
		reprogramTheGolem.addStep(and(strangeImplement, phoenixFeather, inkHighlight), useFeatherOnInk);
		reprogramTheGolem.addStep(and(strangeImplement, inkHighlight), stealFeather);
		reprogramTheGolem.addStep(and(strangeImplement, mushroomHighlight), grindMushroom);
		reprogramTheGolem.addStep(and(inRuin, strangeImplement), leaveRuin);
		reprogramTheGolem.addStep(and(inRuin), pickUpImplement);
		reprogramTheGolem.addStep(strangeImplement, pickBlackMushroom);
		reprogramTheGolem.addStep(inThroneRoom, leaveThroneRoom);

		steps.put(7, reprogramTheGolem);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.CRAFTING, 20),
			new SkillRequirement(Skill.THIEVING, 25, true)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			clay4Highlight,
			vial,
			pestleAndMortar,
			papyrus
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			digsiteTeleport,
			necklaceOfPassage,
			waterskins
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 1000),
			new ExperienceReward(Skill.CRAFTING, 1000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Rubies (by using a chisel and hammer on the throne)", ItemID.RUBY, 2),
			new ItemReward("Emeralds (by using a chisel and hammer on the throne)", ItemID.EMERALD, 2),
			new ItemReward("Sapphires (by using a chisel and hammer on the throne)", ItemID.SAPPHIRE, 2)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to take the Carpet ride from Shantay Pass to Uzer.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToGolem,
			useClay,
			pickUpLetter,
			enterRuinForFirstTime,
			pickUpImplement
		), List.of(
			clay4Highlight
		)));

		sections.add(new PanelDetails("Finding the statuette", List.of(
			talkToElissa,
			searchBookcase,
			readBook,
			talkToCurator,
			pickpocketCurator,
			goUpInMuseum,
			openCabinet
		)));

		sections.add(new PanelDetails("Opening the portal", List.of(
			enterRuin,
			useStatuette,
			turnStatue1,
			enterThroneRoom,
			leaveThroneRoom,
			talkToGolemAfterPortal,
			pickBlackMushroom,
			grindMushroom,
			stealFeather,
			useFeatherOnInk,
			useQuillOnPapyrus,
			useImplementOnGolem,
			useProgramOnGolem
		), List.of(
			vial,
			pestleAndMortar,
			papyrus
		)));

		return sections;
	}
}
