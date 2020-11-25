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

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
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
		firstTracker, secondTracker, thirdTracker, fireBallista, climbTheLadder, talkToKingBolrenFirstOrb,
		talkToTheWarlord, fightTheWarlord, returnOrbs;

	private ConditionForStep completeFirstTracker, completeSecondTracker, completeThirdTracker,
		notCompleteFirstTracker, notCompleteSecondTracker, notCompleteThirdTracker;

	private Conditions talkToFirstTracker, talkToSecondTracker, talkToThirdTracker, shouldFireBallista;

	private ConditionalStep retrieveOrb;

	Zone upstairsTower;
	ZoneCondition isUpstairsTower;
	ItemRequirement logRequirement;

	private final int TRACKER_1_VARBITID = 599;
	private final int TRACKER_2_VARBITID = 600;
	private final int TRACKER_3_VARBITID = 601;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupSteps();
		setupZones();
		setupConditions();
		setupItemRequirements();

		return CreateSteps();
	}

	private Map<Integer, QuestStep> CreateSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToKingBolren);
		steps.put(1, talkToCommanderMontai);
		steps.put(2, bringWoodToCommanderMontai);
		steps.put(3, talkToCommanderMontaiAgain);
		steps.put(4, talkToTrackersStep());
		steps.put(5, retrieveOrbStep());
		steps.put(6, talkToKingBolrenFirstOrb);
		steps.put(7, defeatWarlordStep());
		steps.put(8, returnOrbs);
		return steps;
	}

	private QuestStep talkToTrackersStep()
	{
		ConditionalStep talkToTrackers = new ConditionalStep(this, firstTracker);
		talkToTrackers.addStep(talkToSecondTracker, secondTracker);
		talkToTrackers.addStep(talkToThirdTracker, thirdTracker);
		talkToTrackers.addStep(shouldFireBallista, fireBallista);
		return talkToTrackers;
	}

	private QuestStep retrieveOrbStep()
	{
		retrieveOrb = new ConditionalStep(this, climbTheLadder, "Retrieve the first orb from chest.");
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

		ConditionalStep defeatTheWarlord = new ConditionalStep(this, talkToTheWarlord,
			new ItemRequirement("Food", -1),
			new ItemRequirement("Armor & Weapons", -1));
		defeatTheWarlord.addStep(fightingWarlord, fightTheWarlord);

		return defeatTheWarlord;
	}

	private void setupItemRequirements()
	{
		logRequirement = new ItemRequirement("Logs", ItemID.LOGS, 6);
	}

	private void setupZones()
	{
		upstairsTower = new Zone(new WorldPoint(2500, 3251, 1), new WorldPoint(2506, 3259, 1));
	}

	public void setupConditions()
	{
		notCompleteFirstTracker = new VarbitCondition(TRACKER_1_VARBITID, 0);
		notCompleteSecondTracker = new VarbitCondition(TRACKER_2_VARBITID, 0);
		notCompleteThirdTracker = new VarbitCondition(TRACKER_3_VARBITID, 0);

		completeFirstTracker = new VarbitCondition(TRACKER_1_VARBITID, 1);
		completeSecondTracker = new VarbitCondition(TRACKER_2_VARBITID, 1);
		completeThirdTracker = new VarbitCondition(TRACKER_3_VARBITID, 1);

		isUpstairsTower = new ZoneCondition(upstairsTower);

		talkToFirstTracker = new Conditions(notCompleteFirstTracker);
		talkToSecondTracker = new Conditions(LogicType.AND, completeFirstTracker, notCompleteSecondTracker);
		talkToThirdTracker = new Conditions(LogicType.AND, completeFirstTracker, notCompleteThirdTracker);
		shouldFireBallista = new Conditions(LogicType.AND, completeFirstTracker, completeSecondTracker, completeThirdTracker);
	}

	private void setupSteps()
	{
		talkToKingBolren = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");
		talkToKingBolren.addDialogStep("Can I help at all?");
		talkToKingBolren.addDialogStep("I would be glad to help.");

		talkToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai.");
		talkToCommanderMontai.addDialogStep("Ok, I'll gather some wood.");

		bringWoodToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai again to give him the wood.", logRequirement);

		talkToCommanderMontaiAgain = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai.");
		talkToCommanderMontaiAgain.addDialogStep("I'll try my best.");

		firstTracker = new NpcStep(this, NpcID.TRACKER_GNOME_1, new WorldPoint(2501, 3261, 0), "Talk to the first tracker gnome to the northwest.");
		secondTracker = new NpcStep(this, NpcID.TRACKER_GNOME_2, new WorldPoint(2524, 3257, 0), "Talk to the second tracker gnome inside the jail.");
		thirdTracker = new NpcStep(this, NpcID.TRACKER_GNOME_3, new WorldPoint(2497, 3234, 0), "Talk to the third tracker gnome to the southwest.");

		fireBallista = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509, 3211, 0), "Fire the ballista.");

		climbTheLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2503, 3252, 0), "Climb the ladder");

		ItemRequirement firstOrb = new ItemRequirement("Orb of protection", ItemID.ORB_OF_PROTECTION, 1);
		firstOrb.setTip("If you have lost the orb you can get another from the chest");
		talkToKingBolrenFirstOrb = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0),
			"Speak to King Bolren in the centre of the Tree Gnome Maze. You can do can talk to Elkoy outside the maze to travel to the centre.",
			firstOrb);
		talkToKingBolrenFirstOrb.addDialogStep("I will find the warlord and bring back the orbs.");

		returnOrbs = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(logRequirement));
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

		steps.add(new PanelDetails("Getting started", new ArrayList<>(Arrays.asList(talkToKingBolren))));
		steps.add(new PanelDetails("The three trackers", new ArrayList<>(Arrays.asList(
			talkToCommanderMontai, bringWoodToCommanderMontai, talkToCommanderMontaiAgain,
			firstTracker, secondTracker, thirdTracker, fireBallista)), logRequirement));
		steps.add(new PanelDetails("Retrieving the orbs", new ArrayList<>(Arrays.asList(retrieveOrb, talkToKingBolrenFirstOrb,
			talkToTheWarlord, fightTheWarlord, returnOrbs))));
		return steps;
	}
}
