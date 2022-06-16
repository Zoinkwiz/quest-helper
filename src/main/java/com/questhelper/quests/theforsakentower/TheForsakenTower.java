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

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_FORSAKEN_TOWER
)
public class TheForsakenTower extends BasicQuestHelper
{
	//Items Required
	ItemRequirement crank, oldNotes, dinhsHammer;

	//Items Recommended
	ItemRequirement gamesNecklace;

	Requirement inFirstFloor, inSecondFloor, inBasement, inspectedDisplayCase, finishedFurnacePuzzle, generatorStarted,
		powerPuzzleVisible, finishedPowerPuzzle, finishedPotionPuzzle, finishedAltarPuzzle;

	QuestStep talkToVulcana, talkToUndor, enterTheForsakenTower, inspectDisplayCase, goDownLadderToBasement, searchCrate, inspectGenerator, inspectPowerGrid, doPowerPuzzle,
		goDownToGroundFloor, goDownToFirstFloor, getHammer, goUpToGroundFloor, returnToUndor, returnToVulcana;

	PotionPuzzle potionPuzzle;
	JugPuzzle furnacePuzzleSteps;
	AltarPuzzle altarPuzzle;

	ConditionalStep powerPuzzle;

	//Zones
	Zone firstFloor, secondFloor, basement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
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
		powerPuzzle.addStep(new Conditions(inBasement, crank), inspectGenerator);
		powerPuzzle.addStep(inBasement, searchCrate);
		powerPuzzle.addStep(inFirstFloor, goDownToGroundFloor);
		powerPuzzle.addStep(inSecondFloor, goDownToFirstFloor);
		powerPuzzle.setLockingCondition(finishedPowerPuzzle);

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
		gettingHammer.addStep(dinhsHammer, returnToUndor);
		gettingHammer.addStep(inBasement, goUpToGroundFloor);
		gettingHammer.addStep(inFirstFloor, goDownToGroundFloor);
		gettingHammer.addStep(inSecondFloor, goDownToFirstFloor);

		steps.put(8, gettingHammer);
		steps.put(9, gettingHammer);
		steps.put(10, returnToVulcana);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		crank = new ItemRequirement("Generator crank", ItemID.GENERATOR_CRANK);
		oldNotes = new ItemRequirement("Old notes", ItemID.OLD_NOTES_22774);
		dinhsHammer = new ItemRequirement("Dinh's hammer", ItemID.DINHS_HAMMER);
		gamesNecklace = new ItemRequirement("Games necklace for accessing Wintertodt", ItemCollections.GAMES_NECKLACES);
	}

	public void setupConditions()
	{
		inFirstFloor = new ZoneRequirement(firstFloor);
		inSecondFloor = new ZoneRequirement(secondFloor);
		inBasement = new ZoneRequirement(basement);

		inspectedDisplayCase = new VarbitRequirement(7804, 1);
		finishedPowerPuzzle = new VarbitRequirement(7797, 4);
		finishedFurnacePuzzle = new VarbitRequirement(7798, 4);
		finishedPotionPuzzle = new VarbitRequirement(7799, 4);
		finishedAltarPuzzle = new VarbitRequirement(7800, 2);
		generatorStarted = new VarbitRequirement(7797, 2, Operation.GREATER_EQUAL);
		powerPuzzleVisible = new WidgetModelRequirement(624, 2, 0, 36246);
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
		talkToUndor = new NpcStep(this, NpcID.UNDOR, new WorldPoint(1624, 3942, 0),
			"Talk to Undor at the entrance to Wintertodt. If you've never talked to Ignisia before, you'll need " +
				"to talk to her first. She is just north east of Undor. You can " +
				"teleport there using a Games Necklace, or run" +
				" north through Arceuus.");
		talkToUndor.addDialogStep("I've been sent to help you.");

		enterTheForsakenTower = new ObjectStep(this, ObjectID.DOOR_33491, new WorldPoint(1382, 3817, 0), "Enter the Forsaken Tower, west of Lovakengj.");
		inspectDisplayCase = new ObjectStep(this, NullObjectID.NULL_34588, new WorldPoint(1382, 3821, 0), "Inspect the display case in the Forsaken Tower.");

		goDownToFirstFloor = new ObjectStep(this, ObjectID.LADDER_33485, new WorldPoint(1382, 3827, 2), "Go down from the top floor.");
		goDownToGroundFloor = new ObjectStep(this, ObjectID.STAIRCASE_33552, new WorldPoint(1378, 3825, 1), "Go down to the ground floor.");
		goUpToGroundFloor = new ObjectStep(this, ObjectID.LADDER_33484, new WorldPoint(1382, 10229, 0), "Leave the tower's basement.");

		furnacePuzzleSteps = new JugPuzzle(this);
		furnacePuzzleSteps.setLockingCondition(finishedFurnacePuzzle);
		furnacePuzzleSteps.setBlocker(true);

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
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(gamesNecklace);
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(new FavourRequirement(Favour.LOVAKENGJ, 20),
			new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.CLIENT_OF_KOUREND, QuestState.FINISHED));
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
				new ExperienceReward(Skill.MINING, 500),
				new ExperienceReward(Skill.SMITHING, 500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("6000 Coins", ItemID.COINS_995, 6000),
				new ItemReward("Lovakenj Favour Certificate", ItemID.LOVAKENGJ_FAVOUR_CERTIFICATE, 1),
				new ItemReward("A page for Kharedst's memoirs.", ItemID.KHAREDSTS_MEMOIRS, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToVulcana, talkToUndor)));
		allSteps.add(new PanelDetails("To the Forsaken Tower", Arrays.asList(enterTheForsakenTower, inspectDisplayCase)));
		allSteps.addAll(furnacePuzzleSteps.panelDetails());
		PanelDetails powerPuzzlePanel = new PanelDetails("Power puzzle",
			Arrays.asList(goDownLadderToBasement, searchCrate, inspectGenerator, inspectPowerGrid, doPowerPuzzle));
		powerPuzzlePanel.setLockingStep(powerPuzzle);
		allSteps.add(powerPuzzlePanel);
		allSteps.addAll(potionPuzzle.panelDetails());
		allSteps.addAll(altarPuzzle.panelDetails());
		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(getHammer, returnToUndor, returnToVulcana)));
		return allSteps;
	}
}
