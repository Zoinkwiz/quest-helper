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
import com.questhelper.rewards.ItemReward;
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
	ItemRequirement sixLogs;
	ItemRequirement combatGear;

	// Recommended items
	ItemRequirement food;

	// Miscellaneous requirement
	VarplayerRequirement givenWood;

	VarbitRequirement needToTalkToFirstTracker;
	VarbitRequirement needToTalkToSecondTracker;
	VarbitRequirement needToTalkToThirdTracker;
	VarbitRequirement shouldFireBallista1;
	VarbitRequirement shouldFireBallista2;
	VarbitRequirement shouldFireBallista3;
	VarbitRequirement shouldFireBallista4;
	NpcHintArrowRequirement fightingWarlord;
	ItemOnTileRequirement orbsOfProtectionNearby;
	/// Has handed in all orbs to King Bolren
	VarbitRequirement handedInOrbs;

	ZoneRequirement isUpstairsTower;
	ZoneRequirement insideGnomeVillage;

	/// First orb of protection from the battlefield
	ItemRequirement firstOrb;
	/// Remaining orbs of protection from the Khazard warlord
	ItemRequirement orbsOfProtection;

	// Zones
	Zone upstairsTower;
	Zone zoneVillage;

	// Steps
	ConditionalStep talkToBolrenAtCentreOfMaze;
	NpcStep talkToCommanderMontai;
	NpcStep bringWoodToCommanderMontai;
	NpcStep talkToCommanderMontaiAgain;
	NpcStep firstTracker;
	NpcStep secondTracker;
	NpcStep thirdTracker;
	ObjectStep fireBallista;
	ObjectStep fireBallista1;
	ObjectStep fireBallista2;
	ObjectStep fireBallista3;
	ObjectStep fireBallista4;
	ConditionalStep fireBalistaConditional;
	ObjectStep climbTheLadder;
	ConditionalStep cRetrieveOrb;
	NpcStep elkoySkip;
	NpcStep talkToKingBolrenFirstOrb;
	ConditionalStep returnFirstOrb;
	NpcStep talkToTheWarlord;
	NpcStep fightTheWarlord;
	ItemStep pickupOrb;
	NpcStep returnOrbs;
	NpcStep finishQuestDialog;
	NpcStep elkoySkip2;
	ConditionalStep cReturnOrbs;

	@Override
	protected void setupZones()
	{
		upstairsTower = new Zone(new WorldPoint(2500, 3251, 1), new WorldPoint(2506, 3259, 1));
		zoneVillage = new Zone(new WorldPoint(2514, 3158, 0), new WorldPoint(2542, 3175, 0));
	}

	@Override
	protected void setupRequirements()
	{
		givenWood = new VarplayerRequirement(QuestVarPlayer.QUEST_TREE_GNOME_VILLAGE.getId(), 3, Operation.GREATER_EQUAL);

		needToTalkToFirstTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_H, 0);
		needToTalkToSecondTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_Y, 0);
		needToTalkToThirdTracker = new VarbitRequirement(VarbitID.GNOMETRACKER_X, 0);

		insideGnomeVillage = new ZoneRequirement(zoneVillage);
		isUpstairsTower = new ZoneRequirement(upstairsTower);

		shouldFireBallista1 = new VarbitRequirement(VarbitID.BALLISTA, 0);
		shouldFireBallista2 = new VarbitRequirement(VarbitID.BALLISTA, 1);
		shouldFireBallista3 = new VarbitRequirement(VarbitID.BALLISTA, 2);
		shouldFireBallista4 = new VarbitRequirement(VarbitID.BALLISTA, 3);

		fightingWarlord = new NpcHintArrowRequirement(NpcID.KHAZARD_WARLORD_COMBAT);

		orbsOfProtectionNearby = new ItemOnTileRequirement(ItemID.ORBS_OF_PROTECTION);
		handedInOrbs = new VarbitRequirement(VarbitID.BOLREN_GOT_ORBS, 1, Operation.GREATER_EQUAL);


		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		combatGear = new ItemRequirement("Combat gear (magic is best)", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());

		sixLogs = new ItemRequirement("Logs", ItemID.LOGS, 6).hideConditioned(givenWood);

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

		bringWoodToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523, 3208, 0), "Speak with Commander Montai again to give him the wood.", sixLogs);

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

		fireBalistaConditional = new ConditionalStep(this, fireBallista, "Fire the ballista at the tower.");
		fireBalistaConditional.addStep(shouldFireBallista1, fireBallista1);
		fireBalistaConditional.addStep(shouldFireBallista2, fireBallista2);
		fireBalistaConditional.addStep(shouldFireBallista3, fireBallista3);
		fireBalistaConditional.addStep(shouldFireBallista4, fireBallista4);

		climbTheLadder = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2503, 3252, 0), "Climb the ladder");

		var getOrbFromChest = new ObjectStep(this, ObjectID.CHESTCLOSED_KHAZARD, new WorldPoint(2506, 3259, 1), "Retrieve the first orb from chest.");
		getOrbFromChest.addAlternateObjects(ObjectID.CHESTOPEN_KHAZARD);

		cRetrieveOrb = new ConditionalStep(this, climbTheLadder, "Enter the tower by the Crumbled wall and climb the ladder to retrieve the first orb from chest.");
		cRetrieveOrb.addStep(isUpstairsTower, getOrbFromChest);
		cRetrieveOrb.addSubSteps(getOrbFromChest, climbTheLadder);

		elkoySkip = new NpcStep(this, NpcID.ELKOY_2OPS, new WorldPoint(2505, 3191, 0),
			"Talk to Elkoy outside the maze to travel to the centre.", firstOrb);
		elkoySkip.addDialogStep("Yes please.");

		talkToKingBolrenFirstOrb = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0),
			"", firstOrb);
		talkToKingBolrenFirstOrb.addDialogStep("I will find the warlord and bring back the orbs.");
		returnFirstOrb = new ConditionalStep(this, elkoySkip,
			"Return the Orb of protection to King Bolren in the centre of the Tree Gnome Maze.");
		returnFirstOrb.addStep(insideGnomeVillage, talkToKingBolrenFirstOrb);
		returnFirstOrb.addSubSteps(talkToKingBolrenFirstOrb, elkoySkip);

		talkToTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_CHAT, new WorldPoint(2456, 3301, 0),
			"Talk to the Khazard warlord, south west of West Ardougne, ready to fight him.");

		fightTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_COMBAT, new WorldPoint(2456, 3301, 0),
			"Defeat the Khazard warlord and retrieve orbs.");

		pickupOrb = new ItemStep(this, "Pick up the nearby Orbs of Protection.", orbsOfProtection);

		returnOrbs = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "", orbsOfProtection);

		finishQuestDialog = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "");

		elkoySkip2 = new NpcStep(this, NpcID.ELKOY_2OPS, new WorldPoint(2505, 3191, 0),
			"Talk to Elkoy outside the maze to travel to the centre.", orbsOfProtection);
		elkoySkip2.addDialogStep("Yes please.");

		cReturnOrbs = new ConditionalStep(this, elkoySkip2, "Return the Orbs of protection to King Bolren in the centre of the Tree Gnome Maze.");
		cReturnOrbs.addStep(orbsOfProtectionNearby, pickupOrb);
		cReturnOrbs.addStep(and(insideGnomeVillage, handedInOrbs), finishQuestDialog);
		cReturnOrbs.addStep(insideGnomeVillage, returnOrbs);
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

		var cTalkToTrackers = new ConditionalStep(this, fireBalistaConditional);
		cTalkToTrackers.addStep(needToTalkToFirstTracker, firstTracker);
		cTalkToTrackers.addStep(needToTalkToSecondTracker, secondTracker);
		cTalkToTrackers.addStep(needToTalkToThirdTracker, thirdTracker);
		steps.put(4, cTalkToTrackers);

		steps.put(5, cRetrieveOrb);
		steps.put(6, returnFirstOrb);

		var cDefeatTheWarlord = new ConditionalStep(this, talkToTheWarlord, food, combatGear);
		cDefeatTheWarlord.addStep(fightingWarlord, fightTheWarlord);
		steps.put(7, cDefeatTheWarlord);

		steps.put(8, cReturnOrbs);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			sixLogs,
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
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Gnome amulet", ItemID.GNOME_AMULET)
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
			sixLogs
		)));

		sections.add(new PanelDetails("Retrieving the orbs", List.of(
			cRetrieveOrb,
			returnFirstOrb,
			talkToTheWarlord,
			fightTheWarlord,
			cReturnOrbs
		), List.of(
			combatGear,
			food
		)));

		return sections;
	}
}
