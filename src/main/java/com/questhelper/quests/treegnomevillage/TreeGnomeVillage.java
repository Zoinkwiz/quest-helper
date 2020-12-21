/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
package com.questhelper.quests.treegnomevillage;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.*;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

@QuestDescriptor(
	quest = QuestHelperQuest.TREE_GNOME_VILLAGE
)
public class TreeGnomeVillage extends BasicQuestHelper
{
	private QuestStep talkToKingBolren, talkToCommanderMontai, bringWoodToCommanderMontai, talkToCommanderMontaiAgain,
		firstTracker, secondTracker, thirdTracker, fireBallista, fireBallista1, fireBallista2, fireBallista3, fireBallista4, climbTheLadder,
		talkToKingBolrenFirstOrb, talkToTheWarlord, fightTheWarlord, returnOrbs, finishQuestDialog;

	ConditionForStep completeFirstTracker, completeSecondTracker, completeThirdTracker, handedInOrbs,
		notCompleteFirstTracker, notCompleteSecondTracker, notCompleteThirdTracker, pickupOrbsOfProtection;

	private Conditions talkToSecondTracker, talkToThirdTracker, completedTrackers,
		shouldFireBallista1, shouldFireBallista2, shouldFireBallista3, shouldFireBallista4;

	private ConditionalStep retrieveOrb, talkToBolrenAtCentreOfMaze, fireBalistaConditional;

	Zone upstairsTower, zoneVillage;
	ZoneCondition isUpstairsTower, insideGnomeVillage;
	ItemRequirement logRequirement, orbsOfProtection;

	private final int TRACKER_1_VARBITID = 599;
	private final int TRACKER_2_VARBITID = 600;
	private final int TRACKER_3_VARBITID = 601;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		return CreateSteps();
	}

	private Map<Integer, QuestStep> CreateSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToBolrenAtCentreOfMaze);
		steps.put(1, talkToCommanderMontai);
		steps.put(2, bringWoodToCommanderMontai);
		steps.put(3, talkToCommanderMontaiAgain);
		steps.put(4, talkToTrackersStep());
		steps.put(5, retrieveOrbStep());
		steps.put(6, talkToKingBolrenFirstOrb);
		steps.put(7, defeatWarlordStep());
		steps.put(8, returnOrbsStep());
		return steps;
	}

	private QuestStep talkToTrackersStep()
	{
		fireBalistaConditional = new ConditionalStep(this, fireBallista, "Fire the ballista at the tower.");
		fireBalistaConditional.addStep(shouldFireBallista1, fireBallista1);
		fireBalistaConditional.addStep(shouldFireBallista2, fireBallista2);
		fireBalistaConditional.addStep(shouldFireBallista3, fireBallista3);
		fireBalistaConditional.addStep(shouldFireBallista4, fireBallista4);
		fireBalistaConditional.addSubSteps(fireBallista, fireBallista1, fireBallista2, fireBallista3, fireBallista4);

		ConditionalStep talkToTrackers = new ConditionalStep(this, firstTracker);
		talkToTrackers.addStep(talkToSecondTracker, secondTracker);
		talkToTrackers.addStep(talkToThirdTracker, thirdTracker);
		talkToTrackers.addStep(completedTrackers, fireBalistaConditional);

		return talkToTrackers;
	}

	private QuestStep retrieveOrbStep()
	{
		retrieveOrb = new ConditionalStep(this, climbTheLadder, "Enter the tower by the Crumbled wall and climb the ladder to retrieve the first orb from chest.");
		ObjectStep getOrbFromChest = new ObjectStep(this, ObjectID.CLOSED_CHEST_2183, new WorldPoint(2506, 3259, 1), "Retrieve the first orb from chest.");
		getOrbFromChest.addAlternateObjects(ObjectID.OPEN_CHEST_2182);
		retrieveOrb.addStep(isUpstairsTower, getOrbFromChest);
		retrieveOrb.addSubSteps(getOrbFromChest, climbTheLadder);
		return retrieveOrb;
	}

	private QuestStep defeatWarlordStep()
	{
		NpcCondition fightingWarlord = new NpcCondition(NpcID.KHAZARD_WARLORD_7622);

		fightTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_7622, new WorldPoint(2456, 3301, 0), "Defeat the warlord and retrieve orbs");
		talkToTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_7621, new WorldPoint(2456, 3301, 0), "Retrieve the orbs from the warlord.");

		ItemStep pickupOrb = new ItemStep(this,
			"Pick up the nearby \"Orbs of Protection\".", orbsOfProtection);

		ItemRequirement food = new ItemRequirement("Food", -1);
		food.setDisplayItemId(BankSlotIcons.getFood());

		ItemRequirement combatGear = new ItemRequirement("Armor & Weapons", -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		ConditionalStep defeatTheWarlord = new ConditionalStep(this, talkToTheWarlord,
			food,
			combatGear);

		pickupOrbsOfProtection = new ItemCondition(ItemID.ORBS_OF_PROTECTION);
		defeatTheWarlord.addStep(fightingWarlord, fightTheWarlord);
		defeatTheWarlord.addStep(pickupOrbsOfProtection, pickupOrb);
		defeatTheWarlord.addSubSteps(pickupOrb);

		return defeatTheWarlord;
	}

	private QuestStep returnOrbsStep()
	{
		handedInOrbs = new VarbitCondition(598, 1, Operation.GREATER_EQUAL);

		ConditionalStep returnOrbsSteps = new ConditionalStep(this, returnOrbs);
		returnOrbsSteps.addStep(handedInOrbs, finishQuestDialog);

		return returnOrbs;
	}

	private void setupItemRequirements()
	{
		logRequirement = new ItemRequirement("Logs", ItemID.LOGS, 6);
		orbsOfProtection = new ItemRequirement("Orbs of protection", ItemID.ORBS_OF_PROTECTION);
		orbsOfProtection.setTip("You can retrieve the orbs of protection again by killing the Khazard Warlord again.");
	}

	private void setupZones()
	{
		upstairsTower = new Zone(new WorldPoint(2500, 3251, 1), new WorldPoint(2506, 3259, 1));
		zoneVillage = new Zone(new WorldPoint(2514, 3158, 0), new WorldPoint(2542, 3175, 0));
	}

	public void setupConditions()
	{
		notCompleteFirstTracker = new VarbitCondition(TRACKER_1_VARBITID, 0);
		notCompleteSecondTracker = new VarbitCondition(TRACKER_2_VARBITID, 0);
		notCompleteThirdTracker = new VarbitCondition(TRACKER_3_VARBITID, 0);

		completeFirstTracker = new VarbitCondition(TRACKER_1_VARBITID, 1);
		completeSecondTracker = new VarbitCondition(TRACKER_2_VARBITID, 1);
		completeThirdTracker = new VarbitCondition(TRACKER_3_VARBITID, 1);

		insideGnomeVillage = new ZoneCondition(zoneVillage);
		isUpstairsTower = new ZoneCondition(upstairsTower);

		talkToSecondTracker = new Conditions(LogicType.AND, completeFirstTracker, notCompleteSecondTracker);
		talkToThirdTracker = new Conditions(LogicType.AND, completeFirstTracker, notCompleteThirdTracker);

		completedTrackers = new Conditions(LogicType.AND, completeFirstTracker, completeSecondTracker, completeThirdTracker);

		shouldFireBallista1 = new Conditions(LogicType.AND, completedTrackers, new VarbitCondition(602, 0));
		shouldFireBallista2 = new Conditions(LogicType.AND, completedTrackers, new VarbitCondition(602, 1));
		shouldFireBallista3 = new Conditions(LogicType.AND, completedTrackers, new VarbitCondition(602, 2));
		shouldFireBallista4 = new Conditions(LogicType.AND, completedTrackers, new VarbitCondition(602, 3));
	}

	private void setupSteps()
	{
		talkToKingBolren = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "");
		talkToKingBolren.addDialogStep("Can I help at all?");
		talkToKingBolren.addDialogStep("I would be glad to help.");

		DetailedQuestStep goThroughMaze = new DetailedQuestStep(this, new WorldPoint(2541, 3170, 0), "Follow the marked path to walk through the maze.");
		ArrayList<WorldPoint> pathThroughMaze = new ArrayList<>(Arrays.asList(
			new WorldPoint(2505, 3190, 0),
			new WorldPoint(2512, 3190, 0),
			new WorldPoint(2512, 3188, 0),
			new WorldPoint(2532, 3188, 0),
			new WorldPoint(2532, 3182, 0),
			new WorldPoint(2523, 3181, 0),
			new WorldPoint(2523, 3185, 0),
			new WorldPoint(2521, 3185, 0),
			new WorldPoint(2520, 3179, 0),
			new WorldPoint(2514, 3179, 0),
			new WorldPoint(2514, 3177, 0),
			new WorldPoint(2527, 3177, 0),
			new WorldPoint(2527, 3179, 0),
			new WorldPoint(2529, 3179, 0),
			new WorldPoint(2529, 3177, 0),
			new WorldPoint(2531, 3177, 0),
			new WorldPoint(2531, 3179, 0),
			new WorldPoint(2533, 3179, 0),
			new WorldPoint(2533, 3177, 0),
			new WorldPoint(2544, 3177, 0),
			new WorldPoint(2544, 3174, 0),
			new WorldPoint(2549, 3174, 0),
			new WorldPoint(2549, 3165, 0),
			new WorldPoint(2545, 3165, 0),
			new WorldPoint(2545, 3159, 0),
			new WorldPoint(2550, 3159, 0),
			new WorldPoint(2550, 3156, 0),
			new WorldPoint(2548, 3156, 0),
			new WorldPoint(2548, 3145, 0),
			new WorldPoint(2538, 3145, 0),
			new WorldPoint(2538, 3150, 0),
			new WorldPoint(2541, 3150, 0),
			new WorldPoint(2541, 3148, 0),
			new WorldPoint(2544, 3148, 0),
			new WorldPoint(2544, 3150, 0),
			new WorldPoint(2545, 3150, 0),
			new WorldPoint(2545, 3156, 0),
			new WorldPoint(2520, 3156, 0),
			new WorldPoint(2520, 3159, 0),
			new WorldPoint(2515, 3159, 0)));
		goThroughMaze.setLinePoints(pathThroughMaze);

		talkToBolrenAtCentreOfMaze = new ConditionalStep(this, goThroughMaze,
			"Speak to King Bolren in the centre of the Tree Gnome Maze.");
		talkToBolrenAtCentreOfMaze.addStep(insideGnomeVillage, talkToKingBolren);
		talkToBolrenAtCentreOfMaze.addSubSteps(talkToKingBolren, goThroughMaze);

		talkToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai.");
		talkToCommanderMontai.addDialogStep("Ok, I'll gather some wood.");

		bringWoodToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai again to give him the wood.", logRequirement);

		talkToCommanderMontaiAgain = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai.");
		talkToCommanderMontaiAgain.addDialogStep("I'll try my best.");

		firstTracker = new NpcStep(this, NpcID.TRACKER_GNOME_1, new WorldPoint(2501, 3261, 0), "Talk to the first tracker gnome to the northwest.");
		secondTracker = new NpcStep(this, NpcID.TRACKER_GNOME_2, new WorldPoint(2524, 3257, 0), "Talk to the second tracker gnome inside the jail.");
		thirdTracker = new NpcStep(this, NpcID.TRACKER_GNOME_3, new WorldPoint(2497, 3234, 0), "Talk to the third tracker gnome to the southwest.");

		fireBallista = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509, 3211, 0), "");
		fireBallista1 = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509, 3211, 0), "");
		fireBallista1.addDialogStep("0001");
		fireBallista2 = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509, 3211, 0), "");
		fireBallista2.addDialogStep("0002");
		fireBallista3 = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509, 3211, 0), "");
		fireBallista3.addDialogStep("0003");
		fireBallista4 = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509, 3211, 0), "");
		fireBallista4.addDialogStep("0004");

		climbTheLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2503, 3252, 0), "Climb the ladder");

		ItemRequirement firstOrb = new ItemRequirement("Orb of protection", ItemID.ORB_OF_PROTECTION, 1);
		firstOrb.setTip("If you have lost the orb you can get another from the chest");
		talkToKingBolrenFirstOrb = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0),
			"Speak to King Bolren in the centre of the Tree Gnome Maze. You can do can talk to Elkoy outside the maze to travel to the centre.",
			firstOrb);
		talkToKingBolrenFirstOrb.addDialogStep("I will find the warlord and bring back the orbs.");

		returnOrbs = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.", orbsOfProtection);

		finishQuestDialog = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");
		returnOrbs.addSubSteps(finishQuestDialog);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Collections.singletonList(logRequirement));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Khazard Warlord (level 112)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> steps = new ArrayList<>();

		steps.add(new PanelDetails("Getting started", new ArrayList<>(Collections.singletonList(talkToKingBolren))));
		steps.add(new PanelDetails("The three trackers", new ArrayList<>(Arrays.asList(
			talkToCommanderMontai, bringWoodToCommanderMontai, talkToCommanderMontaiAgain,
			firstTracker, secondTracker, thirdTracker, fireBalistaConditional)), logRequirement));
		steps.add(new PanelDetails("Retrieving the orbs", new ArrayList<>(Arrays.asList(retrieveOrb, talkToKingBolrenFirstOrb,
			talkToTheWarlord, fightTheWarlord, returnOrbs))));
		return steps;
	}
}
