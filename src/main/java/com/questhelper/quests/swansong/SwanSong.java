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
package com.questhelper.quests.swansong;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.SWAN_SONG
)
public class SwanSong extends BasicQuestHelper
{
	ItemRequirement mist10, lava10, blood5, bones7, pot, potLid, ironBar5, log, tinderbox, hammer, brownApron, monkfish5, rawMonkfish5, combatGear, potHiglight,
		potLidHiglight, tinderboxHiglight, ironBar5Higlight, logHiglight, ironSheet5, smallNet, airtightPot, combatGearRanged, boneSeeds, hammerPanel;

	ConditionForStep inColonyEntrance, talkedToFranklin, addedLog, litLog, has5Sheets, wall1Fixed, wall2Fixed, wall3Fixed, wall4Fixed, wall5Fixed, wallsFixed, has5RawMonk,
		has5CookedMonk, talkedToArnold, finishedFranklin, inBasement, queenNearby, hasAirtightPot;

	DetailedQuestStep talkToHerman, talkToWom, talkToWomAtColony, kill79Trolls, talkToHermanInBuilding, talkToFranklin, enterColony, useLog, useTinderbox, talkToArnold,
		talkToFranklinAgain, talkToHermanAfterTasks, enterWizardsBasement, talkToFruscone, talkToMalignius, talkToCrafter, makeAirtightPot, talkToMaligniusWithPot,
		talkToHermanForFinalFight, killQueen, talkToHermanToFinish, talkToHermanWithPot;

	FixWall repairWall;

	FishMonkfish fishAndCookMonkfish;

	Zone colonyEntrance, basement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHerman);
		steps.put(5, talkToHerman);
		steps.put(10, talkToWom);
		steps.put(15, talkToWom);
		steps.put(20, talkToWom);
		steps.put(30, talkToWomAtColony);
		steps.put(40, talkToWomAtColony);

		ConditionalStep defeatTrolls = new ConditionalStep(this, enterColony);
		defeatTrolls.addStep(inColonyEntrance, kill79Trolls);
		steps.put(50, defeatTrolls);

		steps.put(55, talkToHermanInBuilding);
		steps.put(60, talkToHermanInBuilding);
		steps.put(65, talkToHermanInBuilding);

		ConditionalStep completeTasks = new ConditionalStep(this, talkToFranklin);
		completeTasks.addStep(new Conditions(talkedToArnold, finishedFranklin), fishAndCookMonkfish);
		completeTasks.addStep(finishedFranklin, talkToArnold);
		completeTasks.addStep(wallsFixed, talkToFranklinAgain);
		completeTasks.addStep(litLog, repairWall);
		completeTasks.addStep(addedLog, useTinderbox);
		completeTasks.addStep(talkedToFranklin, useLog);

		steps.put(70, completeTasks);

		steps.put(80, talkToHermanAfterTasks);

		ConditionalStep goTalkToFru = new ConditionalStep(this, enterWizardsBasement);
		goTalkToFru.addStep(inBasement, talkToFruscone);

		steps.put(90, goTalkToFru);
		steps.put(95, goTalkToFru);

		steps.put(100, talkToMalignius);
		steps.put(110, talkToMalignius);
		steps.put(120, talkToCrafter);

		ConditionalStep getPotToMal = new ConditionalStep(this, makeAirtightPot);
		getPotToMal.addStep(hasAirtightPot, talkToMaligniusWithPot);

		steps.put(130, getPotToMal);

		steps.put(140, talkToHermanWithPot);

		ConditionalStep bossFight = new ConditionalStep(this, talkToHermanForFinalFight);
		bossFight.addStep(queenNearby, killQueen);
		steps.put(150, talkToHermanForFinalFight);
		steps.put(160, talkToHermanForFinalFight);

		steps.put(170, killQueen);

		steps.put(180, talkToHermanToFinish);
		steps.put(190, talkToHermanToFinish);
		return steps;
	}

	public void setupItemRequirements()
	{
		mist10 = new ItemRequirement("Mist rune", ItemID.MIST_RUNE, 10);
		lava10 = new ItemRequirement("Lava rune", ItemID.LAVA_RUNE, 10);
		blood5 = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE, 5);
		bones7 = new ItemRequirement("Bones", ItemID.BONES, 7);

		pot = new ItemRequirement("Pot", ItemID.POT);
		potLid = new ItemRequirement("Pot lid", ItemID.POT_LID);
		airtightPot = new ItemRequirement("Airtight pot", ItemID.AIRTIGHT_POT);
		ironBar5 = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 5);
		log = new ItemRequirement("Any log", ItemID.LOGS);
		log.addAlternates(ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.TEAK_LOGS, ItemID.MAHOGANY_LOGS, ItemID.REDWOOD_LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);

		potHiglight = new ItemRequirement("Pot", ItemID.POT);
		potHiglight.setHighlightInInventory(true);
		potLidHiglight = new ItemRequirement("Pot lid", ItemID.POT_LID);
		potLidHiglight.setTip("You can make one from wet clay on a potter's wheel");
		potLidHiglight.setHighlightInInventory(true);
		ironBar5Higlight = new ItemRequirement("Iron bar", ItemID.IRON_BAR, 5);
		ironBar5Higlight.setHighlightInInventory(true);
		logHiglight = new ItemRequirement("Any log", ItemID.LOGS);
		logHiglight.addAlternates(ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.MAGIC_LOGS, ItemID.TEAK_LOGS, ItemID.MAHOGANY_LOGS, ItemID.REDWOOD_LOGS);
		logHiglight.setHighlightInInventory(true);
		tinderboxHiglight = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHiglight.setHighlightInInventory(true);

		smallNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET);
		smallNet.setTip("You can get one from Arnold");

		hammerPanel = new ItemRequirement("Hammer (obtainable in quest)", ItemID.HAMMER);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		hammer.setTip("Franklin will give you one");
		brownApron = new ItemRequirement("Brown apron", ItemID.BROWN_APRON, 1, true);
		brownApron.setTip("Malignius will give you one");
		monkfish5 = new ItemRequirement("Fresh monkfish", ItemID.FRESH_MONKFISH_7943, 5);
		rawMonkfish5 = new ItemRequirement("Fresh monkfish", ItemID.FRESH_MONKFISH, 5);
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		combatGearRanged = new ItemRequirement("Ranged or melee combat gear", -1, -1);
		combatGearRanged.setDisplayItemId(BankSlotIcons.getCombatGear());
		ironSheet5 = new ItemRequirement("Iron sheet", ItemID.IRON_SHEET, 5);

		boneSeeds = new ItemRequirement("Bone seed", ItemID.BONE_SEEDS);
		boneSeeds.setTip("You can get more by bringing Malignius another airtight pot");
	}

	public void loadZones()
	{
		colonyEntrance = new Zone(new WorldPoint(2338, 3653, 0), new WorldPoint(2349, 3662, 0));
		basement = new Zone(new WorldPoint(2582, 9484, 0), new WorldPoint(2594, 9489, 0));
	}

	public void setupConditions()
	{
		inColonyEntrance = new ZoneCondition(colonyEntrance);
		inBasement = new ZoneCondition(basement);
		// 2111 is number of trolls killed

		talkedToFranklin = new VarbitCondition(2099, 1);
		addedLog = new VarbitCondition(2099, 2);
		litLog = new VarbitCondition(2099, 3);
		has5Sheets = new ItemRequirementCondition(ironSheet5);
		hasAirtightPot = new ItemRequirementCondition(airtightPot);
		wall1Fixed = new VarbitCondition(2100, 1);
		wall2Fixed = new VarbitCondition(2101, 1);
		wall3Fixed = new VarbitCondition(2102, 1);
		wall4Fixed = new VarbitCondition(2103, 1);
		wall5Fixed = new VarbitCondition(2104, 1);
		wallsFixed = new Conditions(wall1Fixed, wall2Fixed, wall3Fixed, wall4Fixed, wall5Fixed);

		has5RawMonk = new ItemRequirementCondition(rawMonkfish5);
		has5CookedMonk = new ItemRequirementCondition(monkfish5);
		talkedToArnold = new VarbitCondition(2105, 1, Operation.GREATER_EQUAL);
		finishedFranklin = new VarbitCondition(2099, 4);
		queenNearby = new NpcCondition(NpcID.SEA_TROLL_QUEEN);

		// 2108 = number of bones given to Mort
	}

	public void setupSteps()
	{
		talkToHerman = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2345, 3651, 0), "Talk to Herman Carnos outside the Fishing Colony.");
		talkToHerman.addDialogSteps("What's the rush?", "Do you need any help?", "I'm a brave adventurer! Can I try?");
		talkToWom = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3089, 3253, 0), "Talk to the Wise Old Man in Draynor Village.", blood5, lava10, mist10);
		talkToWomAtColony = new NpcStep(this, NpcID.WISE_OLD_MAN_2112, new WorldPoint(2345, 3651, 0), "Talk to the Wise Old Man outside the Fishing Colony. Be prepared to fight sea trolls.", combatGear);
		talkToWomAtColony.addDialogStep("I'm ready to fight.");
		enterColony = new ObjectStep(this, ObjectID.HOLE_12656, new WorldPoint(2344, 3651, 0), "Enter the Fishing Colony, prepared to fight trolls.", combatGear, log, tinderbox, ironBar5, hammer);
		talkToWomAtColony.addSubSteps(enterColony);
		kill79Trolls = new NpcStep(this, NpcID.SEA_TROLL, new WorldPoint(2343, 3657, 0), "Kill the sea trolls.", true);
		talkToHermanInBuilding = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0), "Talk to Herman in the east of the Fishing Colony.");
		talkToFranklin = new NpcStep(this, NpcID.FRANKLIN_CARANOS, new WorldPoint(2341, 3667, 0), "Talk to Franklin in the middle of the Fishing Colony.");
		useLog = new ObjectStep(this, NullObjectID.NULL_13702, new WorldPoint(2344, 3676, 0), "Add some logs to the firebox in the building with a furnace.", logHiglight);
		useLog.addIcon(ItemID.LOGS);
		useTinderbox = new ObjectStep(this, NullObjectID.NULL_13702, new WorldPoint(2344, 3676, 0), "Light the logs in the firebox in the building with a furnace.", tinderboxHiglight);
		useTinderbox.addIcon(ItemID.TINDERBOX);
		repairWall = new FixWall(this);

		talkToArnold = new NpcStep(this, NpcID.ARNOLD_LYDSPOR, new WorldPoint(2329, 3688, 0), "Talk to Arnold at the bank in the Fishing Colony.");
		fishAndCookMonkfish = new FishMonkfish(this);

		talkToFranklinAgain = new NpcStep(this, NpcID.FRANKLIN_CARANOS, new WorldPoint(2341, 3667, 0), "Talk to Franklin again.");
		talkToHermanAfterTasks = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0), "Talk to the Herman in the east of the colony again.");

		enterWizardsBasement = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2594, 3085, 0), "Talk to Wizard Frumscone in the Wizards' Guild basement.");

		talkToFruscone = new NpcStep(this, NpcID.WIZARD_FRUMSCONE, new WorldPoint(2587, 9488, 0), "Talk to Wizard Frumscone in the Wizards' Guild basement.");
		talkToFruscone.addDialogStep("I'll see what the necromancer needs me to do.");
		talkToFruscone.addSubSteps(enterWizardsBasement);
		talkToMalignius = new NpcStep(this, NpcID.MALIGNIUS_MORTIFER, new WorldPoint(2993, 3269, 0), "Talk to Malignius Mortifer near Port Sarim.", bones7);
		talkToMalignius.addDialogStep("I need help with saving a fishing colony.");
		talkToCrafter = new NpcStep(this, NpcID.MASTER_CRAFTER_5812, new WorldPoint(2937, 3290, 0), "Talk to a Master Crafter with dreadlocks in the Crafting Guild.", brownApron);
		talkToCrafter.addDialogStep("Swan Song.");
		makeAirtightPot = new DetailedQuestStep(this, "Add a pot lid to a pot.", potHiglight, potLidHiglight);
		talkToMaligniusWithPot = new NpcStep(this, NpcID.MALIGNIUS_MORTIFER, new WorldPoint(2993, 3269, 0), "Bring the airtight pot and 7 bones to Malignius Mortifer near Port Sarim.", airtightPot);
		talkToMaligniusWithPot.addDialogStep("I've spoken to the master crafter...");
		talkToHermanWithPot = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(9394, 4515, 0),
			"Be prepared to fight, and talk to Herman in the colony.", combatGearRanged, boneSeeds);
		talkToHermanWithPot.addDialogSteps("I've lost the army!");

		talkToHermanForFinalFight = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(9394, 4515, 0),
			"Be prepared to fight, and talk to Herman in the colony.", combatGearRanged);
		talkToHermanForFinalFight.addDialogStep("I'm ready. Let's fight!");
		talkToHermanForFinalFight.addSubSteps(talkToHermanWithPot);

		killQueen = new NpcStep(this, NpcID.SEA_TROLL_QUEEN, new WorldPoint(2347, 3704, 0), "Kill the Sea Troll Queen.", combatGearRanged);
		killQueen.addDialogStep("No, I'm ready to move on with the quest.");
		talkToHermanToFinish = new NpcStep(this, NpcID.HERMAN_CARANOS, new WorldPoint(2354, 3683, 0), "Talk to the Herman to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(mist10, lava10, blood5, bones7, pot, potLid, ironBar5, log, tinderbox, hammerPanel));
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("11 Sea trolls (levels 65/79/87/101)");
		reqs.add("Sea troll queen (level 170)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToHerman, talkToWom)), blood5, mist10, lava10));
		allSteps.add(new PanelDetails("Entering the colony", new ArrayList<>(Arrays.asList(talkToWomAtColony, kill79Trolls, talkToHermanInBuilding)), combatGear, log, tinderbox, ironBar5, hammerPanel));
		ArrayList<QuestStep> helpingSteps = new ArrayList<>(Arrays.asList(talkToFranklin, useLog, useTinderbox));
		helpingSteps.addAll(repairWall.getDisplaySteps());
		allSteps.add(new PanelDetails("Helping Franklin", helpingSteps, combatGear, log, tinderbox, ironBar5, hammerPanel));

		ArrayList<QuestStep> helpingArnoldSteps = new ArrayList<>(Collections.singletonList(talkToArnold));
		helpingArnoldSteps.addAll(fishAndCookMonkfish.getSteps());
		helpingArnoldSteps.add(talkToHermanAfterTasks);

		allSteps.add(new PanelDetails("Helping Arnold", helpingArnoldSteps, combatGear));
		allSteps.add(new PanelDetails("Making an army", new ArrayList<>(Arrays.asList(talkToFruscone, talkToMalignius, talkToCrafter, makeAirtightPot, talkToMaligniusWithPot)), bones7, pot, potLid, combatGearRanged));
		allSteps.add(new PanelDetails("Defeating the trolls", new ArrayList<>(Arrays.asList(talkToHermanForFinalFight, killQueen, talkToHermanToFinish)), combatGearRanged));
		return allSteps;
	}
}

