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
package com.questhelper.quests.theforsakentower;

import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetModelCondition;
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
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_FORSAKEN_TOWER
)
public class TheForsakenTower extends BasicQuestHelper
{
	ItemRequirement tinderbox, gamesNecklace, fiveGallon, eightGallon, crank, oldNotes, dinhsHammer;

	ConditionForStep has5Gallon, has8Gallon, hasTinderbox, inFirstFloor, inSecondFloor, inBasement, inspectedDisplayCase, finishedFurnacePuzzle, hasCrank, generatorStarted,
		powerPuzzleVisible, finishedPowerPuzzle, hasOldNotes, finishedPotionPuzzle, finishedAltarPuzzle, hasDinhsHammer;

	QuestStep talkToVulcana, talkToUndor, enterTheForsakenTower, inspectDisplayCase, goDownLadderToBasement, searchCrate, inspectGenerator, inspectPowerGrid, doPowerPuzzle,
		goDownToGroundFloor, goDownToFirstFloor, getHammer, goUpToGroundFloor, returnToUndor, returnToVulcana;

	PotionPuzzle potionPuzzle;
	JugPuzzle furnacePuzzleSteps;
	AltarPuzzle altarPuzzle;

	ConditionalStep powerPuzzle;

	Zone firstFloor, secondFloor, basement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToVulcana);
		steps.put(1, talkToVulcana);
		steps.put(2, talkToUndor);
		steps.put(3, enterTheForsakenTower);

		powerPuzzle = new ConditionalStep(this, goDownLadderToBasement);
		powerPuzzle.addStep(powerPuzzleVisible, doPowerPuzzle);
		powerPuzzle.addStep(new Conditions(inBasement, generatorStarted), inspectPowerGrid);
		powerPuzzle.addStep(new Conditions(inBasement, hasCrank), inspectGenerator);
		powerPuzzle.addStep(inBasement, searchCrate);
		powerPuzzle.setLockingCondition(finishedPowerPuzzle);
		powerPuzzle.addStep(inFirstFloor, goDownToGroundFloor);
		powerPuzzle.addStep(inSecondFloor, goDownToFirstFloor);

		ConditionalStep puzzleSteps = new ConditionalStep(this, inspectDisplayCase);
		puzzleSteps.addStep(new Conditions(inspectedDisplayCase, finishedFurnacePuzzle, finishedPowerPuzzle, finishedPotionPuzzle), altarPuzzle);
		puzzleSteps.addStep(new Conditions(inspectedDisplayCase, finishedFurnacePuzzle, finishedPowerPuzzle), potionPuzzle);
		puzzleSteps.addStep(new Conditions(inspectedDisplayCase, finishedFurnacePuzzle), powerPuzzle);
		puzzleSteps.addStep(inspectedDisplayCase, furnacePuzzleSteps);

		steps.put(4, puzzleSteps);
		steps.put(5, puzzleSteps);
		steps.put(6, puzzleSteps);
		steps.put(7, puzzleSteps);

		ConditionalStep gettingHammer = new ConditionalStep(this, getHammer);
		gettingHammer.addStep(hasDinhsHammer, returnToUndor);
		gettingHammer.addStep(inBasement, goUpToGroundFloor);
		gettingHammer.addStep(inFirstFloor, goDownToGroundFloor);
		gettingHammer.addStep(inSecondFloor, goDownToFirstFloor);

		steps.put(8, gettingHammer);
		steps.put(9, gettingHammer);
		steps.put(10, returnToVulcana);

		return steps;
	}

	public void setupItemRequirements()
	{
		crank = new ItemRequirement("Generator crank", ItemID.GENERATOR_CRANK);
		oldNotes = new ItemRequirement("Old notes", ItemID.OLD_NOTES_22774);
		dinhsHammer = new ItemRequirement("Dinh's hammer", ItemID.DINHS_HAMMER);
		gamesNecklace = new ItemRequirement("Games necklace for accessing Wintertodt", ItemID.GAMES_NECKLACE8);
	}

	public void setupConditions()
	{
		inFirstFloor = new ZoneCondition(firstFloor);
		inSecondFloor = new ZoneCondition(secondFloor);
		inBasement = new ZoneCondition(basement);

		has5Gallon = new ItemRequirementCondition(fiveGallon);
		has8Gallon = new ItemRequirementCondition(eightGallon);
		hasTinderbox = new ItemRequirementCondition(tinderbox);
		inspectedDisplayCase = new VarbitCondition(7804, 1);
		finishedPowerPuzzle = new VarbitCondition(7797, 4);
		finishedFurnacePuzzle = new VarbitCondition(7798, 4);
		finishedPotionPuzzle = new VarbitCondition(7799, 4);
		finishedAltarPuzzle = new VarbitCondition(7800, 2);
		hasCrank = new ItemRequirementCondition(crank);
		generatorStarted = new VarbitCondition(7797, 2, Operation.GREATER_EQUAL);
		powerPuzzleVisible = new WidgetModelCondition(624, 2, 0, 36246);
		hasOldNotes = new ItemRequirementCondition(oldNotes);
		hasDinhsHammer = new ItemRequirementCondition(dinhsHammer);
	}

	public void setupZones()
	{
		basement = new Zone(new WorldPoint(1374, 10217, 0), new WorldPoint(1389, 10231, 0));
		firstFloor = new Zone(new WorldPoint(1376, 3817, 1), new WorldPoint(1388, 3829, 1));
		secondFloor = new Zone(new WorldPoint(1377, 3821, 2), new WorldPoint(1386, 3828, 2));
	}

	public void setupSteps()
	{
		talkToVulcana = new NpcStep(this, NpcID.LADY_VULCANA_LOVAKENGJ, new WorldPoint(1483, 3747, 0), "Talk to Lady Vulcana Lovakengj in the south of Lovakengj.");
		talkToVulcana.addDialogStep("I'm looking for a quest.");
		talkToVulcana.addDialogStep("I'll get going.");
		talkToUndor = new NpcStep(this, NpcID.UNDOR, new WorldPoint(1624, 3942, 0), "Talk to Undor at the entrance to Wintertodt. You can teleport there using a Games Necklace, or run north through Arceuus.");
		talkToUndor.addDialogStep("I've been sent to help you.");

		enterTheForsakenTower = new ObjectStep(this, ObjectID.DOOR_33491, new WorldPoint(1382, 3817, 0), "Enter the Forsaken Tower, west of Lovakengj.");
		inspectDisplayCase = new ObjectStep(this, NullObjectID.NULL_34588, new WorldPoint(1382, 3821, 0), "Inspect the display case in the Forsaken Tower.");

		goDownToFirstFloor = new ObjectStep(this, ObjectID.LADDER_33485, new WorldPoint(1382, 3827, 2), "Go down from the top floor.");
		goDownToGroundFloor = new ObjectStep(this, ObjectID.STAIRCASE_33552, new WorldPoint(1378, 3825, 1), "Go down to the ground floor.");
		goUpToGroundFloor = new ObjectStep(this, ObjectID.LADDER_33484, new WorldPoint(1382, 10229, 0), "Leave the tower's basement.");

		furnacePuzzleSteps = new JugPuzzle(this);
		furnacePuzzleSteps.setLockingCondition(finishedFurnacePuzzle);

		goDownLadderToBasement = new ObjectStep(this, ObjectID.LADDER_33483, new WorldPoint(1382, 3825, 0), "Climb down the ladder into the tower's basement.");
		inspectPowerGrid = new ObjectStep(this, NullObjectID.NULL_34590, new WorldPoint(1382, 10225, 0), "Inspect the power grid.");
		inspectPowerGrid.addDialogStep("Yes.");

		searchCrate = new ObjectStep(this, ObjectID.CRATE_33498, new WorldPoint(1387, 10228, 0), "Search a crate in the north eastern cell for a generator crank");
		inspectGenerator = new ObjectStep(this, NullObjectID.NULL_34589, new WorldPoint(1382, 10219, 0), "Inspect the steam generator in the south of the room", crank);
		inspectGenerator.addDialogStep("Start the generator.");

		doPowerPuzzle = new PowerPuzzle(this);

		potionPuzzle = new PotionPuzzle(this);
		potionPuzzle.setLockingCondition(finishedPotionPuzzle);

		altarPuzzle = new AltarPuzzle(this);
		altarPuzzle.setLockingCondition(finishedAltarPuzzle);

		getHammer = new ObjectStep(this, NullObjectID.NULL_34588, new WorldPoint(1382, 3821, 0), "Get the hammer from the display case in the Forsaken Tower.");

		returnToUndor = new NpcStep(this, NpcID.UNDOR, new WorldPoint(1624, 3942, 0), "Return Dinh's Hammer to Undor at the entrance to Wintertodt.", dinhsHammer);
		returnToUndor.addDialogStep("Let's talk about my quest.");
		returnToUndor.addDialogStep("Yes.");
		returnToVulcana = new NpcStep(this, NpcID.LADY_VULCANA_LOVAKENGJ, new WorldPoint(1483, 3747, 0), "Return to Lady Vulcana in south Lovakengj to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(gamesNecklace);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Stating off", new ArrayList<>(Arrays.asList(talkToVulcana, talkToUndor))));
		allSteps.add(new PanelDetails("To the Forsaken Tower", new ArrayList<>(Arrays.asList(enterTheForsakenTower, inspectDisplayCase))));
		allSteps.addAll(furnacePuzzleSteps.panelDetails());
		PanelDetails powerPuzzlePanel = new PanelDetails("Power puzzle", new ArrayList<>(Arrays.asList(goDownLadderToBasement, searchCrate, inspectGenerator, inspectPowerGrid, doPowerPuzzle)));
		powerPuzzlePanel.setLockingStep(powerPuzzle);
		allSteps.add(powerPuzzlePanel);
		allSteps.addAll(potionPuzzle.panelDetails());
		allSteps.addAll(altarPuzzle.panelDetails());
		allSteps.add(new PanelDetails("Finishing off", new ArrayList<>(Arrays.asList(getHammer, returnToUndor, returnToVulcana))));
		return allSteps;
	}
}
