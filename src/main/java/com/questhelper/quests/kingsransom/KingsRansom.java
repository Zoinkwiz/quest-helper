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
package com.questhelper.quests.kingsransom;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.KINGS_RANSOM
)
public class KingsRansom extends BasicQuestHelper
{
	//Items Required
	ItemRequirement scrapPaper, addressForm, blackHelm, criminalsThread, hairclip, lawRune, airRune, bronzeMed, ironChain, bronzeMedWorn, ironChainWorn,
		blackKnightLeg, blackKnightLegWorn, blackKnightBody, blackKnightBodyWorn, blackKnightHelm, blackKnightHelmWorn, animateRock, lockpick, grabOrLockpick,
		hairclipOrLockpick, holyGrail, granite, telegrab;

	//Items Recommended
	ItemRequirement ardougneTeleport, camelotTeleport, edgevilleTeleport;

	Requirement hasBlackHelm, hasScrapPaper, hasForm, inUpstairsManor, inDownstairsManor, inTrialRoom, handlerInRoom, butlerInRoom,
		maidInRoom, askedAboutThread, askedAboutDagger, askedAboutNight, askedAboutPoison, inPrison, inBasement, inPuzzle, hasTelegrabItems,
		inBoxWidget, inKeepF0, inKeepF1, inKeepF2, inFortressEntrance, inSecretRoom;

	QuestStep talkToGossip, talkToGuard, breakWindow, grabPaper, goUpstairsManor, takeForm, searchBookcase, goDownstairsManor, goDownstairsForPaper,
		leaveWindow, returnToGuard, talkToGossipAgain, talkToAnna, goIntoTrial, callHandlerAboutPoison, callButlerAboutDagger, callMaidAboutNight,
		talkToHandlerAboutPoison, talkToButlerAboutDagger, talkToMaidAboutNight, waitForVerdict, leaveCourt, talkToAnnaAfterTrial, enterStatue,
		talkToMerlin, reachForVent, useGrabOnGuard, useHairClipOnOnDoor, solvePuzzle, climbF0ToF1, climbF1ToF2, searchTable, selectPurpleBox,
		goDownToArthur, getLockpickOrRunes, openMetalDoor, enterStatueForGrail, enterFortress, enterWallInFortress, freeArthur, talkToArthur,
		talkToArthurInCamelot, enterFortressAfterFreeing, enterWallInFortressAfterFreeing, enterBasementAfterFreeing;

	NpcStep callAboutThread, talkToCromperty;

	//Zones
	Zone upstairsManor, downstairsManor, downstairsManor2, trialRoom, prison, basement, keepF0, keepF1, keepF2, secretRoomFloor0, mainEntrance1, mainEntrance2,
		mainEntrance3, mainEntrance4, secretBasement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGossip);
		steps.put(5, talkToGuard);

		ConditionalStep collectItems = new ConditionalStep(this, breakWindow);
		collectItems.addStep(new Conditions(inDownstairsManor, hasScrapPaper, hasForm, hasBlackHelm), leaveWindow);
		collectItems.addStep(new Conditions(inUpstairsManor, hasScrapPaper, hasForm, hasBlackHelm), goDownstairsManor);
		collectItems.addStep(new Conditions(hasScrapPaper, hasForm, hasBlackHelm), returnToGuard);
		collectItems.addStep(new Conditions(inUpstairsManor, hasScrapPaper, hasForm), searchBookcase);
		collectItems.addStep(new Conditions(inUpstairsManor, hasScrapPaper), takeForm);
		collectItems.addStep(inUpstairsManor, goDownstairsForPaper);
		collectItems.addStep(new Conditions(inDownstairsManor, hasScrapPaper), goUpstairsManor);
		collectItems.addStep(inDownstairsManor, grabPaper);

		steps.put(10, collectItems);

		steps.put(15, talkToGossipAgain);
		steps.put(20, talkToGossipAgain);

		steps.put(25, talkToAnna);

		ConditionalStep trialsSteps = new ConditionalStep(this, talkToAnna);
		trialsSteps.addStep(new Conditions(askedAboutPoison, askedAboutDagger, askedAboutNight, askedAboutThread), waitForVerdict);
		trialsSteps.addStep(new Conditions(criminalsThread, askedAboutPoison, askedAboutDagger, askedAboutNight), callAboutThread);
		trialsSteps.addStep(new Conditions(criminalsThread, askedAboutPoison, askedAboutDagger, maidInRoom), talkToMaidAboutNight);
		trialsSteps.addStep(new Conditions(criminalsThread, askedAboutPoison, askedAboutDagger), callMaidAboutNight);
		trialsSteps.addStep(new Conditions(criminalsThread, askedAboutPoison, butlerInRoom), talkToButlerAboutDagger);
		trialsSteps.addStep(new Conditions(criminalsThread, askedAboutPoison), callButlerAboutDagger);
		trialsSteps.addStep(new Conditions(criminalsThread, handlerInRoom), talkToHandlerAboutPoison);
		trialsSteps.addStep(new Conditions(criminalsThread, inTrialRoom), callHandlerAboutPoison);
		trialsSteps.addStep(criminalsThread, goIntoTrial);

		steps.put(30, trialsSteps);

		ConditionalStep talkToAnnaAfterTrialSteps = new ConditionalStep(this, talkToAnnaAfterTrial);
		talkToAnnaAfterTrialSteps.addStep(inTrialRoom, leaveCourt);

		steps.put(35, talkToAnnaAfterTrialSteps);

		steps.put(40, enterStatue);

		ConditionalStep goTalkToMerlin = new ConditionalStep(this, enterStatue);
		goTalkToMerlin.addStep(inPrison, talkToMerlin);

		steps.put(45, goTalkToMerlin);

		ConditionalStep findMerlinEscape = new ConditionalStep(this, enterStatue);
		findMerlinEscape.addStep(inPrison, reachForVent);

		steps.put(50, findMerlinEscape);

		ConditionalStep freeKnights = new ConditionalStep(this, enterStatue);
		freeKnights.addStep(inPuzzle, solvePuzzle);
		freeKnights.addStep(new Conditions(inPrison, hairclipOrLockpick), useHairClipOnOnDoor);
		freeKnights.addStep(new Conditions(inPrison, hasTelegrabItems), useGrabOnGuard);
		freeKnights.addStep(inPrison, getLockpickOrRunes);

		steps.put(55, freeKnights);
		steps.put(60, freeKnights);

		ConditionalStep getGrail = new ConditionalStep(this, enterStatueForGrail);
		getGrail.addStep(inBoxWidget, selectPurpleBox);
		getGrail.addStep(inKeepF2, searchTable);
		getGrail.addStep(inKeepF1, climbF1ToF2);
		getGrail.addStep(inKeepF0, climbF0ToF1);
		getGrail.addStep(inPrison, openMetalDoor);

		steps.put(65, getGrail);

		steps.put(70, talkToCromperty);

		ConditionalStep goFreeArthur = new ConditionalStep(this, enterFortress);
		goFreeArthur.addStep(inBasement, freeArthur);
		goFreeArthur.addStep(inSecretRoom, goDownToArthur);
		goFreeArthur.addStep(inFortressEntrance, enterWallInFortress);

		steps.put(75, goFreeArthur);

		ConditionalStep talkToArthurInBasement = new ConditionalStep(this, enterFortressAfterFreeing);
		talkToArthurInBasement.addStep(inBasement, talkToArthur);
		talkToArthurInBasement.addStep(inSecretRoom, enterBasementAfterFreeing);
		talkToArthurInBasement.addStep(inFortressEntrance, enterWallInFortressAfterFreeing);

		steps.put(80, talkToArthurInBasement);

		steps.put(85, talkToArthurInCamelot);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		scrapPaper = new ItemRequirement("Scrap paper", ItemID.SCRAP_PAPER);
		addressForm = new ItemRequirement("Address form", ItemID.ADDRESS_FORM);
		blackHelm = new ItemRequirement("Black knight helm", ItemID.BLACK_KNIGHT_HELM);
		criminalsThread = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD_1809);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		airRune = new ItemRequirement("Air rune", ItemCollections.AIR_RUNE);
		airRune.addAlternates(ItemCollections.AIR_STAFF);
		hairclip = new ItemRequirement("Hair clip", ItemID.HAIR_CLIP);
		hairclip.setHighlightInInventory(true);

		ironChain = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY);
		ironChainWorn = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY, 1, true);

		bronzeMed = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM);
		bronzeMedWorn = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM, 1, true);

		blackKnightBody = new ItemRequirement("Black platebody", ItemID.BLACK_PLATEBODY).isNotConsumed();
		blackKnightBodyWorn = blackKnightBody.equipped();

		blackKnightLeg = new ItemRequirement("Black platelegs", ItemID.BLACK_PLATELEGS).isNotConsumed();
		blackKnightLegWorn = blackKnightLeg.equipped();

		blackKnightHelm = new ItemRequirement("Black full helm", ItemID.BLACK_FULL_HELM).isNotConsumed();
		blackKnightHelmWorn = blackKnightHelm.equipped();

		animateRock = new ItemRequirement("Animate rock scroll", ItemID.ANIMATE_ROCK_SCROLL);
		animateRock.setTooltip("If you don't have one, you can get another from Wizard Cromperty in Ardougne during " +
			"the quest");

		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK);

		telegrab = new ItemRequirements("Telegrab runes", new ItemRequirement("Law rune", ItemID.LAW_RUNE),
			new ItemRequirements(LogicType.OR, "Air runes or staff", new ItemRequirement("Air runes", ItemCollections.AIR_RUNE), new ItemRequirement("Air staff", ItemCollections.AIR_STAFF)));

		grabOrLockpick = new ItemRequirements(LogicType.OR, "Runes for telekinetic grab or a lockpick", new ItemRequirement("Lockpick", ItemID.LOCKPICK), telegrab);

		hairclipOrLockpick = new ItemRequirement("Hair clip or Lockpick", ItemID.LOCKPICK);
		hairclipOrLockpick.addAlternates(ItemID.HAIR_CLIP);

		holyGrail = new ItemRequirement("Holy grail", ItemID.HOLY_GRAIL);
		holyGrail.setTooltip("You can get another from the purple box on the table in Morgan la Faye's Keep");

		granite = new ItemRequirement("Any granite", ItemID.GRANITE_2KG);
		granite.setDisplayMatchedItemName(true);
		granite.addAlternates(ItemID.GRANITE_5KG, ItemID.GRANITE_500G);

		ardougneTeleport = new ItemRequirement("Ardougne teleport", ItemID.ARDOUGNE_TELEPORT);
		camelotTeleport = new ItemRequirement("Camelot teleport", ItemID.CAMELOT_TELEPORT);
		edgevilleTeleport = new ItemRequirement("Edgeville teleport", ItemID.AMULET_OF_GLORY6);
	}

	public void loadZones()
	{
		upstairsManor = new Zone(new WorldPoint(2729, 3572, 1), new WorldPoint(2749, 3584, 1));
		downstairsManor = new Zone(new WorldPoint(2733, 3574, 0), new WorldPoint(2747, 3582, 0));
		downstairsManor2 = new Zone(new WorldPoint(2739, 3573, 0), new WorldPoint(2742, 3573, 0));
		trialRoom = new Zone(new WorldPoint(1815, 4260, 0), new WorldPoint(1825, 4276, 0));
		prison = new Zone(new WorldPoint(1690, 4250, 0), new WorldPoint(1909, 4283, 0));
		keepF0 = new Zone(new WorldPoint(1689, 4250, 0), new WorldPoint(1701, 4264, 0));
		keepF1 = new Zone(new WorldPoint(1689, 4250, 1), new WorldPoint(1701, 4264, 1));
		keepF2 = new Zone(new WorldPoint(1689, 4250, 2), new WorldPoint(1701, 4264, 2));
		basement = new Zone(new WorldPoint(1862, 4231, 0), new WorldPoint(1871, 4246, 0));
		secretRoomFloor0 = new Zone(new WorldPoint(3015, 3517, 0), new WorldPoint(3016, 3519, 0));
		secretBasement = new Zone(new WorldPoint(1862, 4264, 0), new WorldPoint(1873, 4229, 0));
		mainEntrance1 = new Zone(new WorldPoint(3008, 3513, 0), new WorldPoint(3012, 3518, 0));
		mainEntrance2 = new Zone(new WorldPoint(3012, 3514, 0), new WorldPoint(3014, 3516, 0));
		mainEntrance3 = new Zone(new WorldPoint(3015, 3515, 0), new WorldPoint(3019, 3516, 0));
		mainEntrance4 = new Zone(new WorldPoint(3019, 3513, 0), new WorldPoint(3019, 3517, 0));
	}

	public void setupConditions()
	{
		hasForm = new Conditions(LogicType.OR, addressForm, new VarbitRequirement(3890, 1));
		hasScrapPaper = new Conditions(LogicType.OR, scrapPaper, new VarbitRequirement(3891, 1));
		hasBlackHelm = new Conditions(LogicType.OR, blackHelm, new VarbitRequirement(3892, 1));
		inUpstairsManor = new ZoneRequirement(upstairsManor);
		inDownstairsManor = new ZoneRequirement(downstairsManor, downstairsManor2);
		inTrialRoom = new ZoneRequirement(trialRoom);
		inPrison = new ZoneRequirement(prison);
		inKeepF0 = new ZoneRequirement(keepF0);
		inKeepF1 = new ZoneRequirement(keepF1);
		inKeepF2 = new ZoneRequirement(keepF2);
		inBasement = new ZoneRequirement(basement);
		inSecretRoom = new ZoneRequirement(secretRoomFloor0);
		inFortressEntrance = new ZoneRequirement(mainEntrance1, mainEntrance2, mainEntrance3, mainEntrance4);

		handlerInRoom = new VarbitRequirement(3907, 2);
		butlerInRoom = new VarbitRequirement(3907, 3);
		maidInRoom = new VarbitRequirement(3907, 5);

		askedAboutThread = new VarbitRequirement(3900, 1);
		askedAboutPoison = new VarbitRequirement(3912, 1);
		askedAboutDagger = new VarbitRequirement(3913, 1);
		askedAboutNight = new VarbitRequirement(3915, 1);

		inPuzzle = new WidgetModelRequirement(588, 1, 27214);
		
		hasTelegrabItems = new Conditions(airRune, lawRune);

		inBoxWidget = new WidgetModelRequirement(390, 0, 27488);
	}

	public void setupSteps()
	{
		talkToGossip = new NpcStep(this, NpcID.GOSSIP, new WorldPoint(2741, 3557, 0), "Talk to Gossip, just south of the Sinclair Mansion.");
		talkToGossip.addDialogStep("How curious. Maybe I should investigate it.");
		talkToGuard = new NpcStep(this, NpcID.GUARD_4218, new WorldPoint(2741, 3561, 0), "Talk to the Guard in the Sinclair Manor.");

		breakWindow = new ObjectStep(this, NullObjectID.NULL_26123, new WorldPoint(2748, 3577, 0), "Right-click break the east window of the mansion, and enter it.");
		goUpstairsManor = new ObjectStep(this, ObjectID.STAIRCASE_25682, new WorldPoint(2737, 3582, 0), "Go upstairs.");

		goDownstairsForPaper = new ObjectStep(this, ObjectID.STAIRCASE_25683, new WorldPoint(2736, 3581, 1), "Pick up the scrap paper downstairs.");
		grabPaper = new DetailedQuestStep(this, new WorldPoint(2746, 3580, 0), "Pick up the scrap paper.", scrapPaper);
		grabPaper.addSubSteps(goDownstairsForPaper);

		takeForm = new DetailedQuestStep(this, new WorldPoint(2739, 3581, 1), "Pick up the address form.", addressForm);
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_26053, new WorldPoint(2738, 3580, 1), "Search the west bookcase for a black knight helm.");
		goDownstairsManor = new ObjectStep(this, ObjectID.STAIRCASE_25683, new WorldPoint(2736, 3581, 1), "Go down the staircase.");
		leaveWindow = new ObjectStep(this, NullObjectID.NULL_26123, new WorldPoint(2748, 3577, 0), "Step out of the window.");
		returnToGuard = new NpcStep(this, NpcID.GUARD_4218, new WorldPoint(2741, 3561, 0), "Return to the guard with the 3 items.");
		returnToGuard.addDialogSteps("I have proof that the Sinclairs have left.", "I have proof that links the Sinclairs to Camelot.", "I have proof of foul play.");

		talkToGossipAgain = new NpcStep(this, NpcID.GOSSIP, new WorldPoint(2741, 3557, 0), "Ask Gossip all 3 chat options.");

		talkToAnna = new NpcStep(this, NpcID.ANNA, new WorldPoint(2737, 3466, 0), "Talk to Anna in the Seers' Village Court House.");
		talkToAnna.addDialogStep("Okay, I guess I don't have much of a choice.");
		talkToAnna.setAllowInCutscene(true);

		goIntoTrial = new ObjectStep(this, ObjectID.STAIRS_26017, new WorldPoint(2738, 3470, 0), "Go down the stairs to the court room.");
		goIntoTrial.addDialogStep("Yes, I'm ready.");
		goIntoTrial.setAllowInCutscene(true);

		callHandlerAboutPoison = new ObjectStep(this, ObjectID.COURT_JUDGE, new WorldPoint(1820, 4276, 0), "Talk to the judge to call the Dog Handler and ask them about the poison.");
		callHandlerAboutPoison.addDialogSteps("Dog handler", "Previous page");
		callHandlerAboutPoison.setAllowInCutscene(true);

		talkToHandlerAboutPoison = new NpcStep(this, NpcID.PIERRE, "Ask Pierre about the poison.");
		talkToHandlerAboutPoison.addDialogStep("Ask about the poison");
		talkToHandlerAboutPoison.setAllowInCutscene(true);
		callHandlerAboutPoison.addSubSteps(talkToHandlerAboutPoison);

		callButlerAboutDagger = new ObjectStep(this, ObjectID.COURT_JUDGE, new WorldPoint(1820, 4276, 0), "Talk to the judge to call the Butler and ask them about the dagger.");
		callButlerAboutDagger.addDialogSteps("Butler", "Previous page");
		callButlerAboutDagger.setAllowInCutscene(true);

		talkToButlerAboutDagger = new NpcStep(this, NpcID.HOBBES, "Ask Hobbes about the dagger.");
		talkToButlerAboutDagger.addDialogStep("Ask about the dagger");
		talkToButlerAboutDagger.setAllowInCutscene(true);
		callButlerAboutDagger.addSubSteps(talkToButlerAboutDagger);

		callMaidAboutNight = new ObjectStep(this, ObjectID.COURT_JUDGE, new WorldPoint(1820, 4276, 0), "Talk to the judge to call the Maid and ask them about the night of the murder.");
		callMaidAboutNight.addDialogSteps("Maid", "Next page");
		callMaidAboutNight.setAllowInCutscene(true);

		talkToMaidAboutNight = new NpcStep(this, NpcID.MARY, "Ask Mary about the night of the murder.");
		talkToMaidAboutNight.addDialogStep("Ask about the night of the murder");
		talkToMaidAboutNight.setAllowInCutscene(true);
		callMaidAboutNight.addSubSteps(talkToMaidAboutNight);

		callAboutThread = new NpcStep(this, NpcID.MARY, "Ask anyone about the thread.");
		callAboutThread.addDialogStep("Ask about the thread");
		callAboutThread.addAlternateNpcs(NpcID.HOBBES, NpcID.PIERRE, NpcID.DONOVAN_THE_FAMILY_HANDYMAN, NpcID.LOUISA, NpcID.STANFORD);
		callAboutThread.setAllowInCutscene(true);

		waitForVerdict = new DetailedQuestStep(this, "Wait for the jury to reach their verdict.");
		callAboutThread.addSubSteps(waitForVerdict);

		leaveCourt = new ObjectStep(this, ObjectID.GATE_26042, new WorldPoint(1820, 4268, 0), "Leave the court room.");

		talkToAnnaAfterTrial = new NpcStep(this, NpcID.ANNA, new WorldPoint(2737, 3466, 0), "Talk to Anna in the Seers' Village Court House.");

		enterStatue = new ObjectStep(this, ObjectID.STATUE_26073, new WorldPoint(2780, 3508, 0), "Search the statue east of Camelot.", grabOrLockpick);

		talkToMerlin = new NpcStep(this, NpcID.MERLIN, new WorldPoint(1907, 4281, 0), "Talk to Merlin.");
		talkToMerlin.addDialogStep("What do we do now?");
		reachForVent = new ObjectStep(this, ObjectID.VENT_25880, new WorldPoint(1904, 4283, 0), "Reach for the vent on the north wall.");
		useGrabOnGuard = new NpcStep(this, NpcID.GUARD_4332, new WorldPoint(1906, 4270, 0), "Use telekinetic grab on the guard grooming their hair.", lawRune, airRune);
		useHairClipOnOnDoor = new ObjectStep(this, ObjectID.METAL_DOOR, new WorldPoint(1904, 4273, 0), "Attempt to open the cell door.", hairclipOrLockpick);

		getLockpickOrRunes = new DetailedQuestStep(this, "Get a lockpick or runes for telegrab. Talking to the knights in the room can give you a lockpick.");
		useGrabOnGuard.addSubSteps(getLockpickOrRunes);

		goDownToArthur = new ObjectStep(this, ObjectID.LADDER_25843, new WorldPoint(3016, 3519, 0), "Enter the Black Knight Fortress basement.");

		solvePuzzle = new LockpickPuzzle(this);

		enterStatueForGrail = new ObjectStep(this, ObjectID.STATUE_26073, new WorldPoint(2780, 3508, 0), "Search the statue east of Camelot.");

		openMetalDoor = new ObjectStep(this, ObjectID.METAL_DOOR, new WorldPoint(1904, 4273, 0), "Go through the cell door.");

		climbF0ToF1 = new ObjectStep(this, ObjectID.STAIRCASE_25786, new WorldPoint(1696, 4260, 0), "Climb to the top of the keep.");
		climbF1ToF2 = new ObjectStep(this, ObjectID.STAIRCASE_25786, new WorldPoint(1696, 4254, 1), "Climb to the top of the keep.");
		climbF0ToF1.addSubSteps(climbF1ToF2);
		searchTable = new ObjectStep(this, ObjectID.TABLE_2650, new WorldPoint(1696, 4259, 2), "Search the table and take the purple round box.");
		selectPurpleBox = new WidgetStep(this, "Take the purple round box.", 390, 16);
		searchTable.addSubSteps(selectPurpleBox);

		talkToCromperty = new NpcStep(this, NpcID.WIZARD_CROMPERTY, new WorldPoint(2684, 3323, 0), "Talk to Wizard Cromperty in East Ardougne.");
		talkToCromperty.addAlternateNpcs(NpcID.WIZARD_CROMPERTY_8481);
		talkToCromperty.addDialogSteps("Chat.");

		enterFortress = new ObjectStep(this, ObjectID.STURDY_DOOR, new WorldPoint(3016, 3514, 0),
				"Enter the Black Knight Fortress wearing the bronze med helm and iron chainbody.",
			bronzeMedWorn, ironChainWorn, blackKnightHelm, blackKnightBody, blackKnightLeg, animateRock, holyGrail, granite);
		enterWallInFortress = new ObjectStep(this, ObjectID.WALL_2341, new WorldPoint(3016, 3517, 0), "Enter the secret room wearing the black knight armour.",
				blackKnightHelmWorn, blackKnightBodyWorn, blackKnightLegWorn, animateRock, holyGrail, granite);
		freeArthur = new ObjectStep(this, NullObjectID.NULL_25943, new WorldPoint(1867, 4233, 0), "Free King Arthur " +
			"by using the animate rock scroll.", animateRock.highlighted(), holyGrail, granite);

		enterFortressAfterFreeing = new ObjectStep(this, ObjectID.STURDY_DOOR, new WorldPoint(3016, 3514, 0), "Enter the Black Knight Fortress.",
			bronzeMedWorn, ironChainWorn, blackKnightHelm, blackKnightBody, blackKnightLeg);
		enterWallInFortressAfterFreeing = new ObjectStep(this, ObjectID.WALL_2341, new WorldPoint(3016, 3517, 0), "Enter the secret room.",
			blackKnightHelmWorn, blackKnightBodyWorn, blackKnightLegWorn, bronzeMed, ironChain);

		enterBasementAfterFreeing = new ObjectStep(this, ObjectID.LADDER_25843, new WorldPoint(3016, 3519, 0), "Enter the Black Knight Fortress basement.");
		talkToArthur = new NpcStep(this, NpcID.KING_ARTHUR, new WorldPoint(1867, 4235, 0), "Talk to King Arthur in the basement.", bronzeMed, ironChain);
		talkToArthur.addSubSteps(enterBasementAfterFreeing, enterFortressAfterFreeing, enterWallInFortressAfterFreeing);

		talkToArthurInCamelot = new NpcStep(this, NpcID.KING_ARTHUR, new WorldPoint(2763, 3512, 0), "Talk to King Arthur in Camelot to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(grabOrLockpick, granite, blackKnightHelm, blackKnightBody, blackKnightLeg, bronzeMed, ironChain);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ardougneTeleport, camelotTeleport, edgevilleTeleport);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.BLACK_KNIGHTS_FORTRESS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HOLY_GRAIL, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.MURDER_MYSTERY, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ONE_SMALL_FAVOUR, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MAGIC, 45));
		req.add(new SkillRequirement(Skill.DEFENCE, 65));
		return req;
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
				new ExperienceReward(Skill.DEFENCE, 33000),
				new ExperienceReward(Skill.MAGIC, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("5,000 Experience Lamp (any skill over 50).", ItemID.ANTIQUE_LAMP, 1)); //4447 is Placeholder
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(talkToGossip, talkToGuard, breakWindow, grabPaper,
			goUpstairsManor, takeForm, searchBookcase, goDownstairsManor, leaveWindow, returnToGuard, talkToGossipAgain)));
		allSteps.add(new PanelDetails("Freeing Anna", Arrays.asList(talkToAnna, goIntoTrial, callHandlerAboutPoison,
			callButlerAboutDagger, callMaidAboutNight, callAboutThread, leaveCourt, talkToAnnaAfterTrial)));
		allSteps.add(new PanelDetails("Saving Merlin and Knights", Arrays.asList(enterStatue, talkToMerlin, reachForVent,
			useGrabOnGuard, useHairClipOnOnDoor, solvePuzzle, climbF0ToF1, searchTable, talkToCromperty),
			grabOrLockpick));
		allSteps.add(new PanelDetails("Saving Arthur", Arrays.asList(enterFortress, enterWallInFortress, goDownToArthur,
			freeArthur, talkToArthur, talkToArthurInCamelot), bronzeMed, ironChain, blackKnightHelm, blackKnightBody,
			blackKnightLeg, granite, animateRock));

		return allSteps;
	}
}
