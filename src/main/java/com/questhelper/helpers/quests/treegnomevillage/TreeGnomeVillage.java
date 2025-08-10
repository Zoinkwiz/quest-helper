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
package com.questhelper.helpers.quests.treegnomevillage;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestVarPlayer;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcHintArrowRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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

public class TreeGnomeVillage extends BasicQuestHelper
{
	// Required items
	ItemRequirement logRequirement;
	ItemRequirement combatGear;

	// Recommended items
	ItemRequirement food;

	// Miscellaneous requirement
	ItemRequirement firstOrb;
	ItemRequirement orbsOfProtection;

	VarbitRequirement completeFirstTracker;
	VarbitRequirement completeSecondTracker;
	VarbitRequirement completeThirdTracker;
	Requirement handedInOrbs;
	Requirement notCompleteFirstTracker;
	Requirement notCompleteSecondTracker;
	Requirement notCompleteThirdTracker;
	Requirement orbsOfProtectionNearby;
	Requirement givenWood;
	Conditions talkToSecondTracker;
	Conditions talkToThirdTracker;
	Conditions completedTrackers;
	Conditions shouldFireBallista1;
	Conditions shouldFireBallista2;
	Conditions shouldFireBallista3;
	Conditions shouldFireBallista4;
	NpcHintArrowRequirement fightingWarlord;

	ZoneRequirement isUpstairsTower;
	ZoneRequirement insideGnomeVillage;

	// Zones
	Zone upstairsTower;
	Zone zoneVillage;

	// Steps
	QuestStep talkToCommanderMontai;
	QuestStep bringWoodToCommanderMontai;
	QuestStep talkToCommanderMontaiAgain;
	QuestStep firstTracker;
	QuestStep secondTracker;
	QuestStep thirdTracker;
	QuestStep fireBallista;
	QuestStep fireBallista1;
	QuestStep fireBallista2;
	QuestStep fireBallista3;
	QuestStep fireBallista4;
	QuestStep climbTheLadder;
	QuestStep talkToKingBolrenFirstOrb;
	QuestStep talkToTheWarlord;
	QuestStep fightTheWarlord;
	ItemStep pickupOrb;
	QuestStep returnOrbs;
	QuestStep finishQuestDialog;
	QuestStep elkoySkip;
	ConditionalStep cRetrieveOrb;
	ConditionalStep talkToBolrenAtCentreOfMaze;
	ConditionalStep fireBalistaConditional;
	ConditionalStep returnFirstOrb;

	@Override
	protected void setupZones()
	{
		upstairsTower = new Zone(new WorldPoint(2500, 3251, 1), new WorldPoint(2506, 3259, 1));
		zoneVillage = new Zone(new WorldPoint(2514, 3158, 0), new WorldPoint(2542, 3175, 0));
	}

	@Override
	protected void setupRequirements()
	{
		notCompleteFirstTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_H, 0);
		notCompleteSecondTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_Y, 0);
		notCompleteThirdTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_X, 0);

		completeFirstTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_H, 1);
		completeSecondTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_Y, 1);
		completeThirdTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_X, 1);

		insideGnomeVillage = new ZoneRequirement(zoneVillage);
		isUpstairsTower = new ZoneRequirement(upstairsTower);

		talkToSecondTracker = and(completeFirstTracker, notCompleteSecondTracker);
		talkToThirdTracker = and(completeFirstTracker, notCompleteThirdTracker);

		completedTrackers = and(completeFirstTracker, completeSecondTracker, completeThirdTracker);

		shouldFireBallista1 = and(completedTrackers, new VarbitRequirement(VarbitID.BALLISTA, 0));
		shouldFireBallista2 = and(completedTrackers, new VarbitRequirement(VarbitID.BALLISTA, 1));
		shouldFireBallista3 = and(completedTrackers, new VarbitRequirement(VarbitID.BALLISTA, 2));
		shouldFireBallista4 = and(completedTrackers, new VarbitRequirement(VarbitID.BALLISTA, 3));

		fightingWarlord = new NpcHintArrowRequirement(NpcID.KHAZARD_WARLORD_COMBAT);

		orbsOfProtectionNearby = new ItemOnTileRequirement(ItemID.ORBS_OF_PROTECTION);
		handedInOrbs = new VarbitRequirement(VarbitID.BOLREN_GOT_ORBS, 1, Operation.GREATER_EQUAL);

		givenWood = new VarplayerRequirement(QuestVarPlayer.QUEST_TREE_GNOME_VILLAGE.getId(), 3, Operation.GREATER_EQUAL);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		combatGear = new ItemRequirement("Combat gear (magic is best)", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());

		logRequirement = new ItemRequirement("Logs", ItemID.LOGS, 6).hideConditioned(givenWood);

		firstOrb = new ItemRequirement("Orb of protection", ItemID.ORB_OF_PROTECTION, 1);
		firstOrb.setTooltip("If you have lost the orb you can get another from the chest");

		orbsOfProtection = new ItemRequirement("Orbs of protection", ItemID.ORBS_OF_PROTECTION);
		orbsOfProtection.setTooltip("You can retrieve the orbs of protection again by killing the Khazard Warlord again.");
	}

	private void setupSteps()
	{
		var talkToKingBolren = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "");
		talkToKingBolren.addDialogStep("Can I help at all?");
		talkToKingBolren.addDialogStep("I would be glad to help.");
		talkToKingBolren.addDialogStep("Yes.");

		var goThroughMaze = new DetailedQuestStep(this, new WorldPoint(2541, 3170, 0), "Follow the marked path to walk through the maze.");
		var pathThroughMaze = List.of(
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
			new WorldPoint(2515, 3159, 0)
		);
		goThroughMaze.setLinePoints(pathThroughMaze);

		talkToBolrenAtCentreOfMaze = new ConditionalStep(this, goThroughMaze,
			"Speak to King Bolren in the centre of the Tree Gnome Maze.");
		talkToBolrenAtCentreOfMaze.addStep(insideGnomeVillage, talkToKingBolren);
		talkToBolrenAtCentreOfMaze.addSubSteps(talkToKingBolren, goThroughMaze);

		talkToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai, north-east of the maze entrance.");
		talkToCommanderMontai.addDialogStep("Ok, I'll gather some wood.");

		bringWoodToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai again to give him the wood.", logRequirement);

		talkToCommanderMontaiAgain = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai.");
		talkToCommanderMontaiAgain.addDialogStep("I'll try my best.");

		firstTracker = new NpcStep(this, NpcID.TRACKER1, new WorldPoint(2501, 3261, 0), "Talk to the first tracker gnome to the north-west of the battlefield for the height coordinate.");
		secondTracker = new NpcStep(this, NpcID.TRACKER2, new WorldPoint(2524, 3257, 0), "Talk to the second tracker gnome inside the jail for the y coordinate.");
		thirdTracker = new NpcStep(this, NpcID.TRACKER3, new WorldPoint(2497, 3234, 0), "Talk to the third tracker gnome to the south-west of the jail.");

		fireBallista = new ObjectStep(this, ObjectID.CATABOW, new WorldPoint(2509, 3211, 0), "");
		fireBallista1 = new ObjectStep(this, ObjectID.CATABOW, new WorldPoint(2509, 3211, 0), "");
		fireBallista1.addDialogStep("0001");
		fireBallista2 = new ObjectStep(this, ObjectID.CATABOW, new WorldPoint(2509, 3211, 0), "");
		fireBallista2.addDialogStep("0002");
		fireBallista3 = new ObjectStep(this, ObjectID.CATABOW, new WorldPoint(2509, 3211, 0), "");
		fireBallista3.addDialogStep("0003");
		fireBallista4 = new ObjectStep(this, ObjectID.CATABOW, new WorldPoint(2509, 3211, 0), "");
		fireBallista4.addDialogStep("0004");

		climbTheLadder = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2503, 3252, 0), "Climb the ladder");

		talkToKingBolrenFirstOrb = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0),
			"", firstOrb);
		talkToKingBolrenFirstOrb.addDialogStep("I will find the warlord and bring back the orbs.");
		elkoySkip = new NpcStep(this, NpcID.ELKOY_2OPS, new WorldPoint(2505, 3191, 0),
			"Talk to Elkoy outside the maze to travel to the centre.", firstOrb);
		elkoySkip.addDialogStep("Yes please.");
		returnFirstOrb = new ConditionalStep(this, elkoySkip,
			"Return the Orb of protection to King Bolren in the centre of the Tree Gnome Maze.");
		returnFirstOrb.addStep(insideGnomeVillage, talkToKingBolrenFirstOrb);
		returnFirstOrb.addSubSteps(talkToKingBolrenFirstOrb, elkoySkip);

		fireBalistaConditional = new ConditionalStep(this, fireBallista, "Fire the ballista at the tower.");
		fireBalistaConditional.addStep(shouldFireBallista1, fireBallista1);
		fireBalistaConditional.addStep(shouldFireBallista2, fireBallista2);
		fireBalistaConditional.addStep(shouldFireBallista3, fireBallista3);
		fireBalistaConditional.addStep(shouldFireBallista4, fireBallista4);
		fireBalistaConditional.addSubSteps(fireBallista, fireBallista1, fireBallista2, fireBallista3, fireBallista4);

		cRetrieveOrb = new ConditionalStep(this, climbTheLadder, "Enter the tower by the Crumbled wall and climb the ladder to retrieve the first orb from chest.");
		ObjectStep getOrbFromChest = new ObjectStep(this, ObjectID.CHESTCLOSED_KHAZARD, new WorldPoint(2506, 3259, 1), "Retrieve the first orb from chest.");
		getOrbFromChest.addAlternateObjects(ObjectID.CHESTOPEN_KHAZARD);
		cRetrieveOrb.addStep(isUpstairsTower, getOrbFromChest);
		cRetrieveOrb.addSubSteps(getOrbFromChest, climbTheLadder);

		fightTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_COMBAT, new WorldPoint(2456, 3301, 0),
			"Defeat the warlord and retrieve orbs.");
		talkToTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_CHAT, new WorldPoint(2456, 3301, 0),
			"Talk to the Warlord south west of West Ardougne, ready to fight him.");

		pickupOrb = new ItemStep(this, "Pick up the nearby Orbs of Protection.", orbsOfProtection);

		returnOrbs = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Talk to King Bolren in the centre of the Tree Gnome Maze.", orbsOfProtection);

		finishQuestDialog = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");

		returnOrbs.addSubSteps(pickupOrb, finishQuestDialog);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToBolrenAtCentreOfMaze);

		steps.put(1, talkToCommanderMontai);
		steps.put(2, bringWoodToCommanderMontai);
		steps.put(3, talkToCommanderMontaiAgain);

		var cTalkToTrackers = new ConditionalStep(this, firstTracker);
		cTalkToTrackers.addStep(talkToSecondTracker, secondTracker);
		cTalkToTrackers.addStep(talkToThirdTracker, thirdTracker);
		cTalkToTrackers.addStep(completedTrackers, fireBalistaConditional);
		steps.put(4, cTalkToTrackers);

		steps.put(5, cRetrieveOrb);
		steps.put(6, returnFirstOrb);

		var cDefeatTheWarlord = new ConditionalStep(this, talkToTheWarlord, food, combatGear);
		cDefeatTheWarlord.addStep(fightingWarlord, fightTheWarlord);
		steps.put(7, cDefeatTheWarlord);

		var cReturnOrbs = new ConditionalStep(this, returnOrbs);
		cReturnOrbs.addStep(orbsOfProtectionNearby, pickupOrb);
		cReturnOrbs.addStep(handedInOrbs, finishQuestDialog);
		steps.put(8, cReturnOrbs);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			logRequirement,
			combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			food
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Khazard Warlord (level 112)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.ATTACK, 11450)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Use of the Spirit Tree transportation method.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Getting started", List.of(
			talkToBolrenAtCentreOfMaze
		)));

		sections.add(new PanelDetails("The three trackers", List.of(
			talkToCommanderMontai,
			bringWoodToCommanderMontai,
			talkToCommanderMontaiAgain,
			firstTracker,
			secondTracker,
			thirdTracker,
			fireBalistaConditional
		), List.of(
			logRequirement
		)));

		sections.add(new PanelDetails("Retrieving the orbs", List.of(
			cRetrieveOrb,
			returnFirstOrb,
			talkToTheWarlord,
			fightTheWarlord,
			returnOrbs
		), List.of(
			combatGear,
			food
		)));

		return sections;
	}
}
