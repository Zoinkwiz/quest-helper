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
package com.questhelper.quests.royaltrouble;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
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

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ROYAL_TROUBLE
)
public class RoyalTrouble extends BasicQuestHelper
{
	//Items Required
	ItemRequirement coal5, coal4, coal3, coal2, coal1, combatGear, pickaxe, coalOrPickaxe, scroll, prop, liftManual,
		engine, pulleyBeam, longPulleyBeam, longerPulleyBeam, rope, beam, plank, diary5, box, letter;

	//Items Recommended
	ItemRequirement antipoison, food, prayerPotions;

	Requirement inMiscFloor1, inEtcFloor1, onIslands, hasCoalOrPickaxe, partnerIsAstrid, startedInvestigation, talkedToMiscSubject,
		talkedToSigrid, talkedToEtcSubject, reportedToVargas, reportedToSigrid, talkedToGhrimInInvestigation, talkedToSailor, gottenScrollFromVargas,
		enteredDungeon, inDungeon, inLiftRoom, inPath1, inPath2, inPath3, inPath4, talkedToDonal, usedProp,
		hasUsedPulley, hasUsedLongerPulley, hasUsedPulley2, hasUsedRope, hasUsedBeam, hasUsedEngine, has1CoalInEngine, has2CoalInEngine, has3CoalInEngine,
		has4CoalInEngine, hasFullEngine, hasRepairedScaffold, inPlankRoom, inLiftOrPlankOrTunnel1Room, attachedRope, onJumpIsland1, onJumpIsland2, onJumpIsland3,
		inPaths, seenFire, searchedFire1, searchedFire2, searchedFire3, searchedFire4, searchedFire5, hasReadDiary, enteredSnakeRoom, talkedToKids,
		inBossRoom, killedBoss, finishedFinalConvoWithSigrid;

	QuestStep travelToMisc, goUpToGhrim, talkToGhrim, goUpToPartner, talkToPartner, talkToVargas, goUpToVargas, goDownFromVargas, talkToGunnhild, goBackUpToVargas, talkToVargasAgain, goDownFromVargas2, goUpToSigrid,
		talkToSigrid, goDownFromSigridToMatilda, talkToMatilda, goBackUpToSigrid, talkToSigridAfterMatilda, getCoalOrPickaxe, goDownFromSigridToVargas, goBackUpToVargasFromSigrid, talkToVargasAfterSigrid, talkToGhrim2,
		goUpToGhrim2, goDownToSailor, talkToSailor, goUpToVargasAfterSailor, talkToVargasAfterSailor, goDownStairsToDungeon, goDownLadderToDungeon, goDownToDungeonNoScroll, talkToDonal, usePropOnCrevice, enterCrevice,
		pickUpEngine, putCoalIntoEngine, putCoalIntoEngine2, putCoalIntoEngine3, putCoalIntoEngine4, putCoalIntoEngine5, takePulley, usePulleyOnScaffold, takePulley2, takeBeam, useBeamOnPulley,
		takeBeam2, useBeamOnLongPulley, useLongerPulleyOnScaffold, takePulley3, usePulleyOnScaffold2, takeRope, useRopeOnScaffold, takeBeam3, useBeamOnPlatform, useEngineOnPlatform, putCoalIntoEnginePlaced, putCoalIntoEnginePlaced2,
		putCoalIntoEnginePlaced3, putCoalIntoEnginePlaced4, putCoalIntoEnginePlaced5, takeRope2, useLift, takePlank, enterTunnelFromPlankRoom, attachRope, swingOverRope, goBackToPlank, plankRock1, plankRock2, plankRock3,
		plankRock4, searchFire1, searchFire2, searchFire3, searchFire4, searchFire5, enterSnakesRoom, enterBossRoom, readDiary, talkToArmod, killBoss, pickUpBox, leaveBossRoom, goUpRope, goUpToSigridToFinish, talkToSigridToFinish,
		goDownFromSigridToFinish, goUpToVargasToFinish, talkToVargasToFinish;

	//Zones
	Zone miscFloor1, etcFloor1, islands, dungeon, liftRoom, plankRoom, path1, path2p1, path2p2, path3p1, path3p2, path3p3, path4p1, path4p2, jumpIsland1, jumpIsland2, jumpIsland3, bossRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, travelToMisc);
		startQuest.addStep(inMiscFloor1, talkToGhrim);
		startQuest.addStep(onIslands, goUpToGhrim);
		steps.put(0, startQuest);

		ConditionalStep talkWithPartner = new ConditionalStep(this, travelToMisc);
		talkWithPartner.addStep(inMiscFloor1, talkToPartner);
		talkWithPartner.addStep(onIslands, goUpToPartner);
		steps.put(10, talkWithPartner);

		ConditionalStep investigateForSigrid = new ConditionalStep(this, goUpToSigrid);
		investigateForSigrid.addStep(new Conditions(talkedToMiscSubject, talkedToSigrid, inEtcFloor1), goDownFromSigridToMatilda);
		investigateForSigrid.addStep(new Conditions(talkedToMiscSubject, talkedToSigrid), talkToMatilda);
		investigateForSigrid.addStep(new Conditions(talkedToMiscSubject, inEtcFloor1), talkToSigrid);

		ConditionalStep continueInvestigationForVargas = new ConditionalStep(this, getCoalOrPickaxe);
		continueInvestigationForVargas.addStep(new Conditions(enteredDungeon, new Conditions(LogicType.OR, hasCoalOrPickaxe, hasFullEngine)), goDownToDungeonNoScroll);
		continueInvestigationForVargas.addStep(new Conditions(gottenScrollFromVargas, inMiscFloor1), goDownStairsToDungeon);
		continueInvestigationForVargas.addStep(new Conditions(gottenScrollFromVargas, hasCoalOrPickaxe), goDownLadderToDungeon);
		continueInvestigationForVargas.addStep(new Conditions(gottenScrollFromVargas), getCoalOrPickaxe);
		continueInvestigationForVargas.addStep(new Conditions(talkedToSailor, inMiscFloor1), talkToVargasAfterSailor);
		continueInvestigationForVargas.addStep(new Conditions(talkedToSailor), goUpToVargasAfterSailor);
		continueInvestigationForVargas.addStep(new Conditions(talkedToGhrimInInvestigation, inMiscFloor1), goDownToSailor);
		continueInvestigationForVargas.addStep(new Conditions(talkedToGhrimInInvestigation), talkToSailor);
		continueInvestigationForVargas.addStep(new Conditions(reportedToVargas, inMiscFloor1), talkToGhrim2);
		continueInvestigationForVargas.addStep(new Conditions(reportedToVargas), goUpToGhrim2);
		continueInvestigationForVargas.addStep(new Conditions(inMiscFloor1), talkToVargasAfterSigrid);
		continueInvestigationForVargas.addStep(new Conditions(hasCoalOrPickaxe), goBackUpToVargasFromSigrid);
		continueInvestigationForVargas.addStep(new Conditions(inEtcFloor1), goDownFromSigridToVargas);

		ConditionalStep repairLift = new ConditionalStep(this, takePulley);
		repairLift.addStep(new Conditions(hasRepairedScaffold, new Conditions(LogicType.OR, rope, attachedRope)), useLift);
		repairLift.addStep(new Conditions(inLiftOrPlankOrTunnel1Room, hasRepairedScaffold), takeRope2);

		repairLift.addStep(new Conditions(hasUsedEngine, has4CoalInEngine), putCoalIntoEnginePlaced5);
		repairLift.addStep(new Conditions(hasUsedEngine, has3CoalInEngine), putCoalIntoEnginePlaced4);
		repairLift.addStep(new Conditions(hasUsedEngine, has2CoalInEngine), putCoalIntoEnginePlaced3);
		repairLift.addStep(new Conditions(hasUsedEngine, has1CoalInEngine), putCoalIntoEnginePlaced2);
		repairLift.addStep(new Conditions(hasUsedEngine, engine), putCoalIntoEnginePlaced);

		repairLift.addStep(new Conditions(hasUsedBeam, engine, hasFullEngine), useEngineOnPlatform);
		repairLift.addStep(new Conditions(hasUsedBeam, engine, has4CoalInEngine), putCoalIntoEngine5);
		repairLift.addStep(new Conditions(hasUsedBeam, engine, has3CoalInEngine), putCoalIntoEngine4);
		repairLift.addStep(new Conditions(hasUsedBeam, engine, has2CoalInEngine), putCoalIntoEngine3);
		repairLift.addStep(new Conditions(hasUsedBeam, engine, has1CoalInEngine), putCoalIntoEngine2);
		repairLift.addStep(new Conditions(hasUsedBeam, engine), putCoalIntoEngine);
		repairLift.addStep(new Conditions(hasUsedBeam), pickUpEngine);

		repairLift.addStep(new Conditions(hasUsedRope, beam), useBeamOnPlatform);
		repairLift.addStep(new Conditions(hasUsedRope), takeBeam3);
		repairLift.addStep(new Conditions(hasUsedPulley2, rope), useRopeOnScaffold);
		repairLift.addStep(new Conditions(hasUsedPulley2), takeRope);
		repairLift.addStep(new Conditions(hasUsedLongerPulley, pulleyBeam), usePulleyOnScaffold2);
		repairLift.addStep(new Conditions(hasUsedLongerPulley), takePulley3);
		repairLift.addStep(new Conditions(hasUsedPulley, longerPulleyBeam), useLongerPulleyOnScaffold);
		repairLift.addStep(new Conditions(hasUsedPulley, longPulleyBeam, beam), useBeamOnLongPulley);
		repairLift.addStep(new Conditions(hasUsedPulley, longPulleyBeam), takeBeam2);
		repairLift.addStep(new Conditions(hasUsedPulley, pulleyBeam, beam), useBeamOnPulley);
		repairLift.addStep(new Conditions(hasUsedPulley, pulleyBeam), takeBeam);
		repairLift.addStep(hasUsedPulley, takePulley2);
		repairLift.addStep(pulleyBeam, usePulleyOnScaffold);

		ConditionalStep dungeonExplore = new ConditionalStep(this, talkToDonal);
		dungeonExplore.addStep(inBossRoom, killBoss);
		dungeonExplore.addStep(new Conditions(inPath4, talkedToKids), enterBossRoom);
		dungeonExplore.addStep(inPath4, talkToArmod);
		dungeonExplore.addStep(new Conditions(searchedFire5, hasReadDiary, inPath3), enterSnakesRoom);
		dungeonExplore.addStep(new Conditions(searchedFire5, inPath3, diary5), readDiary);
		dungeonExplore.addStep(new Conditions(searchedFire4, inPath3), searchFire5);
		dungeonExplore.addStep(new Conditions(searchedFire3, inPath3), searchFire4);
		dungeonExplore.addStep(new Conditions(searchedFire2, inPath3), searchFire3);
		dungeonExplore.addStep(new Conditions(searchedFire1, inPath3), searchFire2);
		dungeonExplore.addStep(new Conditions(searchedFire1, onJumpIsland3), plankRock4);
		dungeonExplore.addStep(new Conditions(searchedFire1, onJumpIsland2), plankRock3);
		dungeonExplore.addStep(new Conditions(searchedFire1, onJumpIsland1), plankRock2);
		dungeonExplore.addStep(new Conditions(searchedFire1, inPath2), plankRock1);
		dungeonExplore.addStep(new Conditions(LogicType.OR, inPath2, inPath3), searchFire1);
		dungeonExplore.addStep(new Conditions(inPath1, attachedRope, plank), swingOverRope);
		dungeonExplore.addStep(new Conditions(inPath1, rope, plank), attachRope);
		dungeonExplore.addStep(new Conditions(inPlankRoom, new Conditions(LogicType.OR, rope, attachedRope), plank), enterTunnelFromPlankRoom);
		dungeonExplore.addStep(new Conditions(inPath1, new Conditions(LogicType.OR, rope, attachedRope)), goBackToPlank);
		dungeonExplore.addStep(new Conditions(inPlankRoom, new Conditions(LogicType.OR, rope, attachedRope)), takePlank);
		dungeonExplore.addStep(inLiftRoom, repairLift);

		dungeonExplore.addStep(usedProp, enterCrevice);
		dungeonExplore.addStep(talkedToDonal, usePropOnCrevice);

		ConditionalStep reportBackToSigrid = new ConditionalStep(this, travelToMisc);
		reportBackToSigrid.addStep(inEtcFloor1, talkToSigridToFinish);
		reportBackToSigrid.addStep(onIslands, goUpToSigridToFinish);
		reportBackToSigrid.addStep(inPath4, goUpRope);
		reportBackToSigrid.addStep(new Conditions(inBossRoom, box), leaveBossRoom);
		reportBackToSigrid.addStep(inBossRoom, pickUpBox);

		ConditionalStep finishingOffVargas = new ConditionalStep(this, travelToMisc);
		finishingOffVargas.addStep(inEtcFloor1, goDownFromSigridToFinish);
		finishingOffVargas.addStep(inMiscFloor1, talkToVargasToFinish);
		finishingOffVargas.addStep(onIslands, goUpToVargasToFinish);

		ConditionalStep doQuest = new ConditionalStep(this, travelToMisc);
		doQuest.addStep(finishedFinalConvoWithSigrid, finishingOffVargas);
		doQuest.addStep(killedBoss, reportBackToSigrid);
		doQuest.addStep(new Conditions(inDungeon), dungeonExplore);
		doQuest.addStep(new Conditions(onIslands, talkedToMiscSubject, talkedToEtcSubject), continueInvestigationForVargas);
		doQuest.addStep(new Conditions(onIslands, talkedToMiscSubject), investigateForSigrid);
		doQuest.addStep(new Conditions(startedInvestigation, inMiscFloor1), goDownFromVargas);
		doQuest.addStep(new Conditions(startedInvestigation, onIslands), talkToGunnhild);
		doQuest.addStep(inMiscFloor1, talkToVargas);
		doQuest.addStep(onIslands, goUpToVargas);

		steps.put(20, doQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		coal5 = new ItemRequirement("Coal", ItemID.COAL, 5);
		coal5.setHighlightInInventory(true);
		coal4 = new ItemRequirement("Coal", ItemID.COAL, 4);
		coal4.setHighlightInInventory(true);
		coal3 = new ItemRequirement("Coal", ItemID.COAL, 3);
		coal3.setHighlightInInventory(true);
		coal2 = new ItemRequirement("Coal", ItemID.COAL, 2);
		coal2.setHighlightInInventory(true);
		coal1 = new ItemRequirement("Coal", ItemID.COAL, 1);
		coal1.setHighlightInInventory(true);

		if (client.getRealSkillLevel(Skill.MINING) >= 30)
		{
			coalOrPickaxe = new ItemRequirements(LogicType.OR, "Either 5 coal or a pickaxe", coal5, pickaxe);
			coal5.setTooltip("You can mine some from the rocks in the room. There's a pickaxe you can find stuck in a rock just outside the lift room.");
			coal4.setTooltip("You can mine some from the rocks in the room. There's a pickaxe you can find stuck in a rock just outside the lift room.");
			coal3.setTooltip("You can mine some from the rocks in the room. There's a pickaxe you can find stuck in a rock just outside the lift room.");
			coal2.setTooltip("You can mine some from the rocks in the room. There's a pickaxe you can find stuck in a rock just outside the lift room.");
			coal1.setTooltip("You can mine some from the rocks in the room. There's a pickaxe you can find stuck in a rock just outside the lift room.");
		}
		else
		{
			coalOrPickaxe = coal5;
		}

		pickaxe = new ItemRequirement("A pickaxe", ItemCollections.getPickaxes());
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.getPrayerPotions(), -1);
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		antipoison = new ItemRequirement("Any antipoison", ItemCollections.getAntipoisons(), 1);
		scroll = new ItemRequirement("Scroll", ItemID.SCROLL_7968);
		scroll.setTooltip("You can get another from King Vargas");
		prop = new ItemRequirement("Mining prop", ItemID.MINING_PROP);
		prop.setHighlightInInventory(true);
		prop.setTooltip("You can get another from Donal");
		liftManual = new ItemRequirement("Lift manual", ItemID.LIFT_MANUAL);
		liftManual.setHighlightInInventory(true);
		engine = new ItemRequirement("Engine", ItemID.ENGINE);
		engine.setHighlightInInventory(true);
		pulleyBeam = new ItemRequirement("Pulley beam", ItemID.PULLEY_BEAM);
		pulleyBeam.setHighlightInInventory(true);
		longPulleyBeam = new ItemRequirement("Long pulley beam", ItemID.LONG_PULLEY_BEAM);
		longPulleyBeam.setHighlightInInventory(true);
		longerPulleyBeam = new ItemRequirement("Longer pulley beam", ItemID.LONGER_PULLEY_BEAM);
		longerPulleyBeam.setHighlightInInventory(true);
		beam = new ItemRequirement("Beam", ItemID.BEAM);
		beam.setHighlightInInventory(true);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);

		plank = new ItemRequirement("Plank", ItemID.PLANK);
		plank.setHighlightInInventory(true);

		diary5 = new ItemRequirement("Burnt diary", ItemID.BURNT_DIARY_7965);
		diary5.setHighlightInInventory(true);

		box = new ItemRequirement("Heavy box", ItemID.HEAVY_BOX);
		box.setTooltip("You can get another from the Guard outside the hole in south east Etceteria");

		letter = new ItemRequirement("Letter", ItemID.LETTER_7966);
		letter.setTooltip("You can get another from Queen Sigrid");
	}

	public void loadZones()
	{
		islands = new Zone(new WorldPoint(2491, 3835, 0), new WorldPoint(2627, 3904, 3));
		miscFloor1 = new Zone(new WorldPoint(2497, 3845, 1), new WorldPoint(2511, 3875, 1));
		etcFloor1 = new Zone(new WorldPoint(2607, 3864, 1), new WorldPoint(2618, 3886, 1));
		dungeon = new Zone(new WorldPoint(2494, 10240, 0), new WorldPoint(2625, 10304, 2));
		liftRoom = new Zone(new WorldPoint(2501, 10282, 0), new WorldPoint(2509, 10291, 0));
		plankRoom = new Zone(new WorldPoint(2508, 10282, 1), new WorldPoint(2512, 10293, 1));
		path1 = new Zone(new WorldPoint(2509, 10290, 0), new WorldPoint(2540, 10301, 0));
		path2p1 = new Zone(new WorldPoint(2540, 10292, 0), new WorldPoint(2557, 10301, 0));
		path2p2 = new Zone(new WorldPoint(2549, 10286, 0), new WorldPoint(2557, 10291, 0));
		path3p1 = new Zone(new WorldPoint(2540, 10242, 0), new WorldPoint(2561, 10285, 0));
		path3p2 = new Zone(new WorldPoint(2562, 10242, 0), new WorldPoint(2590, 10260, 0));
		path3p3 = new Zone(new WorldPoint(2529, 10275, 0), new WorldPoint(2539, 10288, 0));
		path4p1 = new Zone(new WorldPoint(2561, 10260, 0), new WorldPoint(2607, 10300, 0));
		path4p2 = new Zone(new WorldPoint(2608, 10244, 0), new WorldPoint(2622, 10272, 0));
		jumpIsland1 = new Zone(new WorldPoint(2546, 10286, 0), new WorldPoint(2548, 10288, 0));
		jumpIsland2 = new Zone(new WorldPoint(2543, 10286, 0), new WorldPoint(2545, 10288, 0));
		jumpIsland3 = new Zone(new WorldPoint(2540, 10286, 0), new WorldPoint(2542, 10288, 0));
		bossRoom = new Zone(new WorldPoint(2608, 10272, 0), new WorldPoint(2622, 10290, 0));
	}

	public void setupConditions()
	{
		onIslands = new ZoneRequirement(islands);
		inMiscFloor1 = new ZoneRequirement(miscFloor1);
		inEtcFloor1 = new ZoneRequirement(etcFloor1);
		inDungeon = new ZoneRequirement(dungeon);
		inLiftRoom = new ZoneRequirement(liftRoom);
		inLiftOrPlankOrTunnel1Room = new ZoneRequirement(liftRoom, plankRoom, path1);
		inPath1 = new ZoneRequirement(path1);
		inPath2 = new ZoneRequirement(path2p1, path2p2);
		inPath3 = new ZoneRequirement(path3p1, path3p2, path3p3);
		inPath4 = new ZoneRequirement(path4p1, path4p2);
		inPaths = new ZoneRequirement(path1, path2p1, path2p2, path3p1, path3p2, path3p3, path4p1, path4p2);
		onJumpIsland1 = new ZoneRequirement(jumpIsland1);
		onJumpIsland2 = new ZoneRequirement(jumpIsland2);
		onJumpIsland3 = new ZoneRequirement(jumpIsland3);
		inBossRoom = new ZoneRequirement(bossRoom);

		if (client.getRealSkillLevel(Skill.MINING) >= 30)
		{
			hasCoalOrPickaxe = new Conditions(LogicType.OR, pickaxe, coal5);
		}
		else
		{
			hasCoalOrPickaxe = coal5;
		}
		partnerIsAstrid = new VarbitRequirement(98, 0);

		startedInvestigation = new VarbitRequirement(2141, 10);
		reportedToVargas = new VarbitRequirement(2141, 20, Operation.GREATER_EQUAL);
		talkedToGhrimInInvestigation = new VarbitRequirement(2141, 30);
		talkedToSailor = new VarbitRequirement(2141, 40);
		gottenScrollFromVargas = new VarbitRequirement(2141, 50);
		enteredDungeon = new VarbitRequirement(2141, 60, Operation.GREATER_EQUAL);
		// Missing 70
		talkedToDonal = new VarbitRequirement(2141, 80, Operation.GREATER_EQUAL);

		talkedToKids = new VarbitRequirement(2141, 110, Operation.GREATER_EQUAL);
		killedBoss = new VarbitRequirement(2141, 120, Operation.GREATER_EQUAL);

		talkedToSigrid = new VarbitRequirement(2142, 10);
		reportedToSigrid = new VarbitRequirement(2142, 20, Operation.GREATER_EQUAL);
		finishedFinalConvoWithSigrid = new VarbitRequirement(2142, 40);

		talkedToMiscSubject = new VarbitRequirement(2143, 1);
		talkedToEtcSubject = new VarbitRequirement(2144, 1, Operation.GREATER_EQUAL);
		usedProp = new VarbitRequirement(2145, 1);
		hasUsedPulley = new VarbitRequirement(2146, 1);
		hasUsedLongerPulley = new VarbitRequirement(2146, 2);
		hasUsedPulley2 = new VarbitRequirement(2146, 3);
		hasUsedRope = new VarbitRequirement(2146, 4);
		hasUsedBeam = new VarbitRequirement(2146, 5);
		hasUsedEngine = new VarbitRequirement(2146, 6);
		hasRepairedScaffold = new VarbitRequirement(2146, 7, Operation.GREATER_EQUAL);

		has1CoalInEngine = new VarbitRequirement(2156, 1);
		has2CoalInEngine = new VarbitRequirement(2156, 2);
		has3CoalInEngine = new VarbitRequirement(2156, 3);
		has4CoalInEngine = new VarbitRequirement(2156, 4);
		hasFullEngine = new VarbitRequirement(2156, 5);

		attachedRope = new VarbitRequirement(2147, 1);

		inPlankRoom = new ZoneRequirement(plankRoom);

		seenFire = new VarbitRequirement(2154, 1);
		searchedFire1 = new VarbitRequirement(2148, 1, Operation.GREATER_EQUAL);
		searchedFire2 = new VarbitRequirement(2148, 2);
		searchedFire3 = new VarbitRequirement(2148, 3);
		searchedFire4 = new VarbitRequirement(2148, 4, Operation.GREATER_EQUAL);
		searchedFire5 = new VarbitRequirement(2148, 5, Operation.GREATER_EQUAL);

		// TODO: hasReadyDiary probably wrong varbit, need to verify
		hasReadDiary = new VarbitRequirement(2149, 1);
		enteredSnakeRoom = new VarbitRequirement(2157, 1);
	}

	public void setupSteps()
	{
		String travelText = "Travel to Miscellania. You can take a boat from Rellekka. You can also use Fairy Rings to teleport there with the code CIP If you've unlocked them.";
		travelToMisc = new NpcStep(this, NpcID.SAILOR_3936, new WorldPoint(2629, 3693, 0), travelText);
		goUpToGhrim = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Talk to Advisor Ghrim in Miscellania castle.");
		talkToGhrim = new NpcStep(this, NpcID.ADVISOR_GHRIM_5448, new WorldPoint(2499, 3857, 1), "Talk to Advisor Ghrim in Miscellania castle.");
		talkToGhrim.addDialogStep("Has anything been happening in the kingdom recently?");
		talkToGhrim.addDialogStep("Very well, I'll sort it out.");
		talkToGhrim.addSubSteps(goUpToGhrim);

		if (partnerIsAstrid.check(client))
		{
			talkToPartner = new NpcStep(this, NpcID.PRINCESS_ASTRID, new WorldPoint(2502, 3867, 1), "Talk to Princess Astrid  in Miscellania castle.");
			goUpToPartner = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3872, 0), "Talk to Princess Astrid in Miscellania castle.");
		}
		else
		{
			talkToPartner = new NpcStep(this, NpcID.PRINCE_BRAND, new WorldPoint(2502, 3852, 1), "Talk to Prince Brand in Miscellania castle.");
			goUpToPartner = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Talk to Prince Brand in Miscellania castle.");

		}
		talkToPartner.addSubSteps(goUpToPartner);

		goUpToVargas = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Talk to Vargas in Miscellania castle.");
		talkToVargas = new NpcStep(this, NpcID.KING_VARGAS, new WorldPoint(2501, 3860, 1), "Talk to King Vargas.");
		talkToVargas.addDialogStep("Right away, your Majesty.");
		talkToVargas.addSubSteps(goUpToVargas);

		goDownFromVargas = new ObjectStep(this, ObjectID.STAIRCASE_16676, new WorldPoint(2506, 3849, 1), "Talk to Gardener Gunnhild outside the castle.");
		talkToGunnhild = new NpcStep(this, NpcID.GARDENER_GUNNHILD, new WorldPoint(2527, 3855, 0), "Talk to Gardener Gunnhild outside the castle.");
		talkToGunnhild.addSubSteps(goDownFromVargas);

		goBackUpToVargas = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Return to King Vargas in Miscellania castle.");
		talkToVargasAgain = new NpcStep(this, NpcID.KING_VARGAS, new WorldPoint(2501, 3860, 1), "Return to King Vargas in Miscellania castle.");
		talkToVargasAgain.addSubSteps(goBackUpToVargas);

		goDownFromVargas2 = new ObjectStep(this, ObjectID.STAIRCASE_16676, new WorldPoint(2506, 3849, 1), "Talk to Queen Sigrid in Etceteria Castle.");
		goUpToSigrid = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2614, 3868, 0), "Talk to Queen Sigrid in Etceteria Castle.");
		talkToSigrid = new NpcStep(this, NpcID.QUEEN_SIGRID, new WorldPoint(2612, 3875, 1), "Talk to Queen Sigrid in Etceteria Castle.");
		talkToSigrid.addDialogStep("Of course, it's my duty.");
		talkToSigrid.addSubSteps(goDownFromVargas2, goUpToSigrid);

		goDownFromSigridToMatilda = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(2614, 3867, 1), "Talk to Matilda in the south of Etceteria Castle.");
		talkToMatilda = new NpcStep(this, NpcID.MATILDA, new WorldPoint(2606, 3869, 0), "Talk to Matilda in the south of Etceteria Castle.");
		talkToMatilda.addSubSteps(goDownFromSigridToMatilda);

		goBackUpToSigrid = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2614, 3868, 0), "Return to Queen Sigrid in Etceteria Castle.");
		talkToSigridAfterMatilda = new NpcStep(this, NpcID.QUEEN_SIGRID, new WorldPoint(2612, 3875, 1), "Return to Queen Sigrid in Etceteria Castle.");
		talkToSigridAfterMatilda.addSubSteps(goBackUpToSigrid);

		if (client.getRealSkillLevel(Skill.MINING) >= 30)
		{
			getCoalOrPickaxe = new ObjectStep(this, ObjectID.BANK_BOOTH, new WorldPoint(2612, 3900, 0), "Now is a good point to get your combat gear, antipoisons, and either 5 coal or a pickaxe out.", coal5, pickaxe, combatGear, antipoison);
		}
		else
		{
			getCoalOrPickaxe = new ObjectStep(this, ObjectID.BANK_BOOTH, new WorldPoint(2612, 3900, 0), "Now is where you should get your combat gear, antipoisons, and 5 coal.", coal5, combatGear, antipoison);
		}

		goDownFromSigridToVargas = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(2614, 3867, 1), "Go downstairs from Queen Sigrid.");
		goBackUpToVargasFromSigrid = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Return to King Vargas in Miscellania castle.");
		talkToVargasAfterSigrid = new NpcStep(this, NpcID.KING_VARGAS, new WorldPoint(2501, 3860, 1), "Return to King Vargas in Miscellania castle.");
		talkToVargas.addSubSteps(goBackUpToVargasFromSigrid, goDownFromSigridToVargas);

		goUpToGhrim2 = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Talk to Advisor Ghrim in Miscellania castle.");
		talkToGhrim2 = new NpcStep(this, NpcID.ADVISOR_GHRIM_5448, new WorldPoint(2499, 3857, 1), "Talk to Advisor Ghrim in Miscellania castle.");
		talkToGhrim2.addDialogStep("King Vargas asked me to talk to you.");
		talkToGhrim2.addSubSteps(goUpToGhrim2);

		goDownToSailor = new ObjectStep(this, ObjectID.STAIRCASE_16676, new WorldPoint(2506, 3849, 1), "Talk to the Sailor on the Miscellania docks.");
		talkToSailor = new NpcStep(this, NpcID.SAILOR_3936, new WorldPoint(2578, 3845, 0), "Talk to the Sailor on the Miscellania docks.");
		talkToSailor.addDialogStep("I'm looking for a sailor...");
		talkToSailor.addSubSteps(goDownToSailor);

		goUpToVargasAfterSailor = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Return to King Vargas in Miscellania castle.");
		talkToVargasAfterSailor = new NpcStep(this, NpcID.KING_VARGAS, new WorldPoint(2501, 3860, 1), "Return to King Vargas in Miscellania castle to get a scroll.");

		goDownStairsToDungeon = new ObjectStep(this, ObjectID.STAIRCASE_16676, new WorldPoint(2506, 3849, 1), "Go down the ladder south of Miscellania castle.", scroll);
		goDownLadderToDungeon = new ObjectStep(this, ObjectID.LADDER_15116, new WorldPoint(2509, 3846, 0), "Go down the ladder south of Miscellania castle.", scroll);
		goDownToDungeonNoScroll = new ObjectStep(this, ObjectID.LADDER_15116, new WorldPoint(2509, 3846, 0), "Go down the ladder south of Miscellania castle.");
		goDownLadderToDungeon.addSubSteps(goDownStairsToDungeon, goDownToDungeonNoScroll);

		talkToDonal = new NpcStep(this, NpcID.DONAL, new WorldPoint(2528, 10257, 0), "Talk to Donal in the pub in the dungeon.");
		talkToDonal.addDialogStep("Of course. Dealing with monsters is what I do best!");

		usePropOnCrevice = new ObjectStep(this, ObjectID.CREVICE_15186, new WorldPoint(2505, 10281, 0), "Use the mining prop on the crevice in the north west corner of the dungeon.", prop);
		usePropOnCrevice.addIcon(ItemID.MINING_PROP);
		enterCrevice = new ObjectStep(this, ObjectID.CREVICE_15186, new WorldPoint(2505, 10281, 0), "Enter the crevice in the north west corner of the dungeon.");

		pickUpEngine = new ItemStep(this, "Pick up an Engine", engine);
		putCoalIntoEngine = new DetailedQuestStep(this, "Read the nearby lift manual, then put 5 coal into the engine.", engine, coal5);
		putCoalIntoEngine2 = new DetailedQuestStep(this, "Read the nearby lift manual, then put 4 coal into the engine.", engine, coal4);
		putCoalIntoEngine3 = new DetailedQuestStep(this, "Put 3 more coal into the engine.", engine, coal3);
		putCoalIntoEngine4 = new DetailedQuestStep(this, "Put 2 more coal into the engine.", engine, coal2);
		putCoalIntoEngine5 = new DetailedQuestStep(this, "Put 1 more coal into the engine.", engine, coal1);
		putCoalIntoEngine.addSubSteps(putCoalIntoEngine2, putCoalIntoEngine3, putCoalIntoEngine4, putCoalIntoEngine5);

		putCoalIntoEnginePlaced = new ObjectStep(this, NullObjectID.NULL_15238, new WorldPoint(2508, 10287, 0), "Read the nearby lift manual, then put 5 coal into the engine.", coal5);
		putCoalIntoEnginePlaced2 = new ObjectStep(this, NullObjectID.NULL_15238, new WorldPoint(2508, 10287, 0), "Put 4 more coal into the engine.", coal4);
		putCoalIntoEnginePlaced3 = new ObjectStep(this, NullObjectID.NULL_15238, new WorldPoint(2508, 10287, 0), "Put 3 more coal into the engine.", coal3);
		putCoalIntoEnginePlaced4 = new ObjectStep(this, NullObjectID.NULL_15238, new WorldPoint(2508, 10287, 0), "Put 2 more coal into the engine.", coal2);
		putCoalIntoEnginePlaced5 = new ObjectStep(this, NullObjectID.NULL_15238, new WorldPoint(2508, 10287, 0), "Put 1 more coal into the engine.", coal1);

		takePulley = new ObjectStep(this, ObjectID.CRATE_15244, new WorldPoint(2504, 10285, 0), "Take a Pulley Beam.");
		usePulleyOnScaffold = new ObjectStep(this, NullObjectID.NULL_15240, new WorldPoint(2508, 10286, 0), "Use the Pulley Beam on the Broken Scaffold", pulleyBeam);
		usePulleyOnScaffold.addIcon(ItemID.PULLEY_BEAM);
		takePulley2 = new ObjectStep(this, ObjectID.CRATE_15244, new WorldPoint(2504, 10285, 0), "Take a Pulley Beam.");
		takeBeam = new ObjectStep(this, ObjectID.CRATE_15243, new WorldPoint(2504, 10284, 0), "Take a Beam.");
		useBeamOnPulley = new DetailedQuestStep(this, "Use a beam on a pulley beam.", beam, pulleyBeam);
		takeBeam2 = new ObjectStep(this, ObjectID.CRATE_15243, new WorldPoint(2504, 10284, 0), "Take another Beam.");
		useBeamOnLongPulley = new DetailedQuestStep(this, "Use a beam on the long pulley beam.", beam, longPulleyBeam);

		useLongerPulleyOnScaffold = new ObjectStep(this, NullObjectID.NULL_15240, new WorldPoint(2508, 10286, 0), "Use the Longer pulley beam on the Scaffold", longerPulleyBeam);
		useLongerPulleyOnScaffold.addIcon(ItemID.LONGER_PULLEY_BEAM);
		takePulley3 = new ObjectStep(this, ObjectID.CRATE_15244, new WorldPoint(2504, 10285, 0), "Take a Pulley Beam.");
		usePulleyOnScaffold2 = new ObjectStep(this, NullObjectID.NULL_15240, new WorldPoint(2508, 10286, 0), "Use the pulley beam on the Scaffold", pulleyBeam);
		usePulleyOnScaffold2.addIcon(ItemID.PULLEY_BEAM);

		takeRope = new ObjectStep(this, ObjectID.CRATE_15245, new WorldPoint(2506, 10289, 0), "Take a rope from the crate.");
		useRopeOnScaffold = new ObjectStep(this, NullObjectID.NULL_15240, new WorldPoint(2508, 10286, 0), "Use the rope on the broken scaffold", rope);
		useRopeOnScaffold.addIcon(ItemID.ROPE);

		takeBeam3 = new ObjectStep(this, ObjectID.CRATE_15243, new WorldPoint(2504, 10284, 0), "Take another Beam.");
		useBeamOnPlatform = new ObjectStep(this, NullObjectID.NULL_15239, new WorldPoint(2508, 10288, 0), "Use a beam on the platform.", beam);
		useBeamOnPlatform.addIcon(ItemID.BEAM);

		useEngineOnPlatform = new ObjectStep(this, NullObjectID.NULL_15238, new WorldPoint(2508, 10287, 0), "Use the Engine on the engine platform", engine);

		useLift = new ObjectStep(this, NullObjectID.NULL_15239, new WorldPoint(2508, 10288, 0), "Take the lift up.");

		takeRope2 = new ObjectStep(this, ObjectID.CRATE_15245, new WorldPoint(2506, 10289, 0), "Take a rope from the crate in the lift room.", rope);
		takePlank = new ItemStep(this, "Pick up the plank.", plank);
		enterTunnelFromPlankRoom = new ObjectStep(this, ObjectID.TUNNEL_15188, new WorldPoint(2511, 10287, 1), "Enter the tunnel.");

		goBackToPlank = new ObjectStep(this, ObjectID.TUNNEL_15189, new WorldPoint(2514, 10290, 0), "Go back through the tunnel to get a plank.", plank);

		attachRope = new ObjectStep(this, NullObjectID.NULL_15252, new WorldPoint(2540, 10299, 0), "Use a rope on the rock over the water blocking the path.", rope);
		swingOverRope = new ObjectStep(this, NullObjectID.NULL_15252, new WorldPoint(2540, 10299, 0), "Swing over the water on the ropeswing.");

		searchFire1 = new ObjectStep(this, ObjectID.FIRE_REMAINS_15206, new WorldPoint(2555, 10295, 0), "Search the fire remains.");
		searchFire2 = new ObjectStep(this, ObjectID.FIRE_REMAINS_15207, new WorldPoint(2534, 10281, 0), "Search the 2nd fire remains further along the path.");
		searchFire3 = new ObjectStep(this, ObjectID.FIRE_REMAINS_15208, new WorldPoint(2555, 10279, 0), "Search the 3rd fire remains further along the path.");
		searchFire4 = new ObjectStep(this, ObjectID.FIRE_REMAINS_15209, new WorldPoint(2548, 10261, 0), "Search the 4th fire remains further along the path.");
		searchFire5 = new ObjectStep(this, ObjectID.FIRE_REMAINS_15210, new WorldPoint(2573, 10246, 0), "Search the 5th fire remains further along the path.");

		plankRock1 = new ObjectStep(this, ObjectID.ROCKS_15213, new WorldPoint(2548, 10288, 0), "Use a plank on the rocks to cross them.", plank);
		plankRock1.addIcon(ItemID.PLANK);
		plankRock2 = new ObjectStep(this, ObjectID.ROCKS_15213, new WorldPoint(2545, 10287, 0), "Use a plank on the rocks to cross them.", plank);
		plankRock2.addIcon(ItemID.PLANK);
		plankRock3 = new ObjectStep(this, ObjectID.ROCKS_15213, new WorldPoint(2542, 10287, 0), "Use a plank on the rocks to cross them.", plank);
		plankRock3.addIcon(ItemID.PLANK);
		plankRock4 = new ObjectStep(this, ObjectID.ROCKS_15213, new WorldPoint(2539, 10286, 0), "Use a plank on the rocks to cross them.", plank);
		plankRock4.addIcon(ItemID.PLANK);

		plankRock1.addSubSteps(plankRock2, plankRock3, plankRock4);

		readDiary = new DetailedQuestStep(this, "Read the Diary", diary5);

		enterSnakesRoom = new ObjectStep(this, ObjectID.CREVICE_15194, new WorldPoint(2585, 10260, 0), "Enter the crevice at the end of the path.");
		enterBossRoom = new ObjectStep(this, ObjectID.CREVICE_15196, new WorldPoint(2617, 10272, 0), "Prepare to fight the Giant Sea Snake. It will only use ranged attacks if you stand at a distance. It can poison you.");

		killBoss = new NpcStep(this, NpcID.GIANT_SEA_SNAKE, new WorldPoint(2615, 10280, 0), "Kill the Giant Sea Snake.");

		talkToArmod = new NpcStep(this, NpcID.ARMOD, new WorldPoint(2572, 10277, 0), "Talk to Armod.");

		pickUpBox = new ItemStep(this, "Pick up the heavy box", box);
		leaveBossRoom = new ObjectStep(this, ObjectID.CREVICE_15197, new WorldPoint(2617, 10273, 0), "Leave the room.");
		goUpRope = new ObjectStep(this, NullObjectID.NULL_15193, new WorldPoint(2618, 10265, 0), "Go up the rope that's appeared.");

		goUpToSigridToFinish = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2614, 3868, 0), "Talk to Queen Sigrid in Etceteria Castle.", box);
		talkToSigridToFinish = new NpcStep(this, NpcID.QUEEN_SIGRID, new WorldPoint(2612, 3875, 1), "Talk to Queen Sigrid in Etceteria Castle.", box);
		talkToSigridToFinish.addSubSteps(goUpToSigridToFinish);

		goDownFromSigridToFinish = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(2614, 3867, 1), "Return to King Vargas to finish the quest.", letter);
		goUpToVargasToFinish = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2506, 3849, 0), "Return to King Vargas to finish the quest.", letter);
		talkToVargasToFinish = new NpcStep(this, NpcID.KING_VARGAS, new WorldPoint(2501, 3860, 1), "Return to King Vargas to finish the quest.", letter);
		talkToVargasToFinish.addSubSteps(goDownFromSigridToFinish, goUpToVargasToFinish);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(coalOrPickaxe);
		reqs.add(combatGear);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Giant Sea Snake (level 149)");
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(antipoison);
		reqs.add(food);
		reqs.add(prayerPotions);
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THRONE_OF_MISCELLANIA, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 40, true));
		req.add(new SkillRequirement(Skill.SLAYER, 40, true));
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
				new ExperienceReward(Skill.AGILITY, 5000),
				new ExperienceReward(Skill.SLAYER, 5000),
				new ExperienceReward(Skill.HITPOINTS, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(
			new ItemReward("20,000 Coins", ItemID.COINS_995, 20000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Increased rewards from Managing Miscellania"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(travelToMisc, talkToGhrim)));
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(talkToPartner, talkToVargas, talkToGunnhild, talkToVargasAgain, talkToSigrid, talkToMatilda)));
		if (client.getRealSkillLevel(Skill.MINING) >= 30)
		{
			allSteps.add(new PanelDetails("Digging deeper", Arrays.asList(getCoalOrPickaxe, talkToVargasAfterSigrid, talkToGhrim2, talkToSailor, talkToVargasAfterSailor, goDownLadderToDungeon), coalOrPickaxe, combatGear, antipoison));
		}
		else
		{
			allSteps.add(new PanelDetails("Digging deeper", Arrays.asList(getCoalOrPickaxe, talkToVargasAfterSigrid, talkToGhrim2, talkToSailor, talkToVargasAfterSailor, goDownLadderToDungeon), coal5, combatGear, antipoison));
		}
		allSteps.add(new PanelDetails("Repair the lift", Arrays.asList(talkToDonal, usePropOnCrevice, enterCrevice, takePulley, usePulleyOnScaffold, takePulley2, takeBeam, useBeamOnPulley, takeBeam2, useBeamOnLongPulley, useLongerPulleyOnScaffold, takePulley3, usePulleyOnScaffold2, takeRope, useRopeOnScaffold, takeBeam3, useBeamOnPlatform, pickUpEngine, useEngineOnPlatform, putCoalIntoEngine)));
		allSteps.add(new PanelDetails("Investigate the caves", Arrays.asList(useLift, takePlank, enterTunnelFromPlankRoom, attachRope, swingOverRope, searchFire1, plankRock1, searchFire2, searchFire3, searchFire4, searchFire5, readDiary, enterSnakesRoom)));
		allSteps.add(new PanelDetails("Investigate the caves", Arrays.asList(talkToArmod, enterBossRoom, killBoss, pickUpBox, leaveBossRoom, goUpRope, talkToSigridToFinish, talkToVargasToFinish)));

		return allSteps;
	}
}
