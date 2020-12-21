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
package com.questhelper.quests.theyesofglouphrie;

import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_EYES_OF_GLOUPHRIE
)
public class TheEyesOfGlouphrie extends BasicQuestHelper
{
	ItemRequirement bucketOfSap, mudRune, mapleLog, oakLog, hammer, saw, pestleAndMortar, groundMud, magicGlue, mudRuneHighlight, pestleHighlight, bucketOfSapHiglight;

	ConditionForStep inCave, inspectedMachine, inspectedBowl, inHazelmereHut, hasGroundMud, hasMagicGlue, killedCreature1, killedCreature2, killedCreature3, killedCreature4, killedCreature5,
		killedCreature6, inFloor1, inFloor2, inFloor3;

	QuestStep enterCave, talkToBrimstail, inspectBowl, inspectMachine, talkToBrimstailAgain, goUpToHazelmere, talkToHazelmere, enterCaveAgain, talkToBrimstailAfterHazelmere, grindMudRunes,
		useMudOnSap, repairMachine, talkToBrimstailAfterRepairing, talkToBrimstailForMoreDisks, operateMachine, killCreature1, talkToBrimstailAfterIllusion, killCreature2, killCreature3,
		killCreature4, killCreature5, killCreature6, climbUpToF1Tree, climbUpToF2Tree, climbUpToF3Tree, talkToNarnode;

	PuzzleStep unlockMachine;

	Zone cave, hazelmereHut, floor1, floor2, floor3;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, enterCave);
		startQuest.addStep(inCave, talkToBrimstail);

		steps.put(0, startQuest);

		ConditionalStep inspectDevices = new ConditionalStep(this, enterCave);
		inspectDevices.addStep(new Conditions(inCave, inspectedBowl), inspectMachine);
		inspectDevices.addStep(inCave, inspectBowl);

		steps.put(1, inspectDevices);

		ConditionalStep talkAfterInspect = new ConditionalStep(this, enterCave);
		talkAfterInspect.addStep(inCave, talkToBrimstailAgain);

		steps.put(2, talkAfterInspect);

		ConditionalStep goTalkToHazelmere = new ConditionalStep(this, goUpToHazelmere);
		goTalkToHazelmere.addStep(inHazelmereHut, talkToHazelmere);

		steps.put(5, goTalkToHazelmere);
		steps.put(9, goTalkToHazelmere);
		steps.put(10, goTalkToHazelmere);
		steps.put(11, goTalkToHazelmere);
		steps.put(12, goTalkToHazelmere);
		ConditionalStep talkToBrimAfterHazel = new ConditionalStep(this, enterCaveAgain);
		talkToBrimAfterHazel.addStep(inCave, talkToBrimstailAfterHazelmere);

		steps.put(15, talkToBrimAfterHazel);

		ConditionalStep fixMachine = new ConditionalStep(this, grindMudRunes);
		fixMachine.addStep(new Conditions(hasMagicGlue, inCave), repairMachine);
		fixMachine.addStep(hasMagicGlue, enterCaveAgain);
		fixMachine.addStep(hasGroundMud, useMudOnSap);

		steps.put(20, fixMachine);
		steps.put(21, fixMachine);
		steps.put(22, fixMachine);
		steps.put(23, fixMachine);

		ConditionalStep brimstailAfterFixing = new ConditionalStep(this, enterCave);
		brimstailAfterFixing.addStep(inCave, talkToBrimstailAfterRepairing);

		steps.put(25, brimstailAfterFixing);

		ConditionalStep getMoreTokens = new ConditionalStep(this, enterCave);
		getMoreTokens.addStep(inCave, unlockMachine);

		steps.put(27, getMoreTokens);
		steps.put(30, getMoreTokens);

		ConditionalStep goOperateMachine = new ConditionalStep(this, enterCave);
		goOperateMachine.addStep(inCave, operateMachine);

		steps.put(35, goOperateMachine);

		ConditionalStep prepareToKill = new ConditionalStep(this, enterCave);
		prepareToKill.addStep(inCave, talkToBrimstailAfterIllusion);

		steps.put(36, prepareToKill);

		ConditionalStep killThemAll = new ConditionalStep(this, enterCave);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5, killedCreature6, killedCreature4, killedCreature3), killCreature2);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5, killedCreature6, killedCreature4, inFloor3), killCreature3);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5, killedCreature6, killedCreature4, inFloor2), climbUpToF3Tree);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5, killedCreature6, killedCreature4, inFloor1), climbUpToF2Tree);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5, killedCreature6, killedCreature4), climbUpToF1Tree);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5, killedCreature6), killCreature4);
		killThemAll.addStep(new Conditions(killedCreature1, killedCreature5), killCreature6);
		killThemAll.addStep(killedCreature1, killCreature5);
		killThemAll.addStep(inCave, killCreature1);

		steps.put(40, killThemAll);

		steps.put(50, talkToNarnode);

		return steps;
	}

	public void setupItemRequirements()
	{
		bucketOfSap = new ItemRequirement("Bucket of sap", ItemID.BUCKET_OF_SAP);
		bucketOfSapHiglight = new ItemRequirement("Bucket of sap", ItemID.BUCKET_OF_SAP);
		bucketOfSapHiglight.setHighlightInInventory(true);

		groundMud = new ItemRequirement("Ground mud runes", ItemID.GROUND_MUD_RUNES);
		groundMud.setHighlightInInventory(true);
		mudRune = new ItemRequirement("Mud rune", ItemID.MUD_RUNE);
		mudRuneHighlight = new ItemRequirement("Mud rune", ItemID.MUD_RUNE);
		mudRuneHighlight.setHighlightInInventory(true);
		mapleLog = new ItemRequirement("Maple logs", ItemID.MAPLE_LOGS);
		oakLog = new ItemRequirement("Oak logs", ItemID.OAK_LOGS);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		saw = new ItemRequirement("Saw", ItemID.SAW);
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleHighlight = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleHighlight.setHighlightInInventory(true);
		magicGlue = new ItemRequirement("Magic glue", ItemID.MAGIC_GLUE);
		magicGlue.setHighlightInInventory(true);
	}

	public void setupZones()
	{
		cave = new Zone(new WorldPoint(2379, 9806, 0), new WorldPoint(2415, 9832, 0));
		hazelmereHut = new Zone(new WorldPoint(2673, 3085, 1), new WorldPoint(2681, 3089, 1));
		floor1 = new Zone(new WorldPoint(2437, 3474, 1), new WorldPoint(2493, 3511, 1));
		floor2 = new Zone(new WorldPoint(2437, 3474, 2), new WorldPoint(2493, 3511, 2));
		floor3 = new Zone(new WorldPoint(2437, 3474, 3), new WorldPoint(2493, 3511, 3));
	}

	public void setupConditions()
	{
		inCave = new ZoneCondition(cave);
		inspectedBowl = new VarbitCondition(2515, 1);
		inspectedMachine = new VarbitCondition(2516, 1);
		inHazelmereHut = new ZoneCondition(hazelmereHut);
		hasGroundMud = new ItemRequirementCondition(groundMud);
		hasMagicGlue = new ItemRequirementCondition(magicGlue);
		killedCreature1 = new VarbitCondition(2504, 2);
		killedCreature2 = new VarbitCondition(2505, 2);
		killedCreature3 = new VarbitCondition(2506, 2);
		killedCreature4 = new VarbitCondition(2507, 2);
		killedCreature5 = new VarbitCondition(2508, 2);
		killedCreature6 = new VarbitCondition(2509, 2);

		inFloor1 = new ZoneCondition(floor1);
		inFloor2 = new ZoneCondition(floor2);
		inFloor3 = new ZoneCondition(floor3);
	}

	public void setupSteps()
	{
		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_17209, new WorldPoint(2404, 3419, 0), "Go talk to Brimstail in his cave in west Tree Gnome Stronghold.");
		talkToBrimstail = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2410, 9818, 0), "Talk to Brimstail.");
		talkToBrimstail.addDialogStep("What's that cute creature wandering around?");
		talkToBrimstail.addDialogStep("Yes, that sounds fascinating...");
		talkToBrimstail.addDialogStep("Oh, yes I love a bit of History.");
		enterCave.addSubSteps(talkToBrimstail);
		inspectBowl = new ObjectStep(this, ObjectID.SINGING_BOWL, new WorldPoint(2388, 9813, 0), "Inspect the singing bowl in the west room.");
		inspectMachine = new ObjectStep(this, ObjectID.OAKNOCKS_MACHINE_17241, new WorldPoint(2390, 9826, 0), "Attempt to unlock oaknock's machine in the north of the cave.");
		talkToBrimstailAgain = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2410, 9818, 0), "Talk to Brimstail again.");
		talkToBrimstailAgain.addDialogStep("I've had a look in the other room now.");
		talkToBrimstailAgain.addDialogStep("Of course, I'd love to!");

		goUpToHazelmere = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2677, 3087, 0), "Go talk to Hazelmere in his hut east of Yanille.");
		talkToHazelmere = new NpcStep(this, NpcID.HAZELMERE, new WorldPoint(2677, 3087, 1), "Go talk to Hazelmere in his hut east of Yanille.");
		talkToHazelmere.addSubSteps(goUpToHazelmere);

		enterCaveAgain = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_17209, new WorldPoint(2404, 3419, 0), "Go back to Brimstail's cave in west Tree Gnome Stronghold.", pestleAndMortar, mudRune, bucketOfSap, oakLog, mapleLog, saw, hammer);
		talkToBrimstailAfterHazelmere = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2410, 9818, 0), "Talk to Brimstail.");
		talkToBrimstailAfterHazelmere.addDialogStep("I've visited Hazelmere, he told me all sorts of interesting things.");

		grindMudRunes = new DetailedQuestStep(this, "Use the pestle and mortar on some mud runes", pestleHighlight, mudRuneHighlight);
		useMudOnSap = new DetailedQuestStep(this, "Use the ground mud runes on the bucket of sap", groundMud, bucketOfSapHiglight);

		repairMachine = new ObjectStep(this, NullObjectID.NULL_17282, new WorldPoint(2390, 9826, 0), "Use the magic glue on oaknock's machine in the north of the cave.", magicGlue, oakLog, mapleLog, saw, hammer);

		talkToBrimstailAfterRepairing = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2410, 9818, 0), "Talk to Brimstail.");
		talkToBrimstailAfterRepairing.addDialogStep("I think I've fixed the machine now!");
		talkToBrimstailForMoreDisks = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2410, 9818, 0), "Talk to Brimstail for more disks.");
		talkToBrimstailForMoreDisks.addDialogStep("I can't work out what to do with these discs!");
		unlockMachine = new PuzzleStep(this);
		unlockMachine.addSubSteps(unlockMachine.getSteps());

		operateMachine = new ObjectStep(this, NullObjectID.NULL_17282, new WorldPoint(2390, 9826, 0), "Operate the machine.");
		unlockMachine.addSubSteps(operateMachine);

		killCreature1 = new NpcStep(this, NpcID.EVIL_CREATURE, new WorldPoint(3408, 9819, 0), "Kill the evil creature next to Brimstail.");
		killCreature2 = new NpcStep(this, NpcID.EVIL_CREATURE_1244, new WorldPoint(2465, 3494, 0), "Kill the evil creature next to Narode.");
		killCreature3 = new NpcStep(this, NpcID.EVIL_CREATURE_1247, new WorldPoint(2466, 3496, 3), "Kill the evil creature at the top of the Grand Tree.");
		killCreature4 = new NpcStep(this, NpcID.EVIL_CREATURE_1250, new WorldPoint(2422, 3526, 0), "Kill the evil creature in the north west of the Stronghold.");
		killCreature5 = new NpcStep(this, NpcID.EVIL_CREATURE_1253, new WorldPoint(2461, 3388, 0), "Kill the evil creature next to the Stronghold's entrance.");
		killCreature6 = new NpcStep(this, NpcID.EVIL_CREATURE_1256, new WorldPoint(2462, 3443, 0), "Kill the evil creature next to the Stronghold's Spirit Tree.");
		//killCreature7 = new NpcStep(this, NpcID.EVIL_CREATURE, new WorldPoint(3408, 9819, 0), "Kill the evil creature next to Brimstail.");

		talkToBrimstailAfterIllusion = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2410, 9818, 0), "Talk to Brimstail again.");
		talkToBrimstailAfterIllusion.addDialogStep("Phew! I've got that machine working now. What do I need to do now?");

		climbUpToF1Tree = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2466, 3495, 0), "Kill the evil creature at the top of the Grand Tree.");
		climbUpToF2Tree = new ObjectStep(this, ObjectID.LADDER_16684, new WorldPoint(2466, 3495, 1), "Kill the evil creature at the top of the Grand Tree.");
		climbUpToF3Tree = new ObjectStep(this, ObjectID.LADDER_2884, new WorldPoint(2466, 3495, 2), "Kill the evil creature at the top of the Grand Tree.");
		killCreature3.addSubSteps(climbUpToF1Tree, climbUpToF2Tree, climbUpToF3Tree);

		talkToNarnode = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 3496, 0), "Talk to King Narnode to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(bucketOfSap, mudRune, mapleLog, oakLog, hammer, saw, pestleAndMortar));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Learning", new ArrayList<>(Arrays.asList(enterCave, inspectBowl, talkToBrimstailAgain, talkToHazelmere, talkToBrimstailAfterHazelmere, repairMachine, talkToBrimstailForMoreDisks, unlockMachine)),
			bucketOfSap, mudRune, mapleLog, oakLog, hammer, saw, pestleAndMortar));

		allSteps.add(new PanelDetails("Kill the spies", new ArrayList<>(Arrays.asList(talkToBrimstailAfterIllusion, killCreature1, killCreature5, killCreature6, killCreature4, killCreature3, killCreature2, talkToNarnode))));


		return allSteps;
	}
}