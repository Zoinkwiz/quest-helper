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
package com.questhelper.helpers.quests.piratestreasure;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.npc.NpcHintArrowRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class PiratesTreasure extends BasicQuestHelper
{
	// Required items
	ItemRequirement sixtyCoins;
	ItemRequirement spade;
	ItemRequirement tenBananas;

	// Recommended items
	ItemRequirement teleportVarrock;
	ItemRequirement teleportFalador;

	// Mid-quest requirements
	ItemRequirement pirateMessage;
	ItemRequirement chestKey;

	// Zones
	Zone blueMoonFirst;

	// Miscellaneous requirements
	ZoneRequirement inBlueMoonFirst;

	// Steps
	NpcStep speakToRedbeard;

	QuestStep readPirateMessage;

	RumSmugglingStep smuggleRum;

	ObjectStep openChest;
	ObjectStep climbStairs;

	QuestStep digUpTreasure;
	NpcStep killGardener;

	@Override
	protected void setupZones()
	{
		blueMoonFirst = new Zone(new WorldPoint(3213, 3405, 1), new WorldPoint(3234, 3391, 1));
	}

	@Override
	protected void setupRequirements()
	{
		sixtyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 60);
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();

		teleportVarrock = new ItemRequirement("A teleport to Varrock", ItemID.POH_TABLET_VARROCKTELEPORT);
		teleportFalador = new ItemRequirement("A teleport to Falador", ItemID.POH_TABLET_FALADORTELEPORT);
		tenBananas = new ItemRequirement("Bananas", ItemID.BANANA, 10).canBeObtainedDuringQuest();

		pirateMessage = new ItemRequirement("Pirate message", ItemID.PIRATEMESSAGE);
		chestKey = new ItemRequirement("Chest key", ItemID.CHEST_KEY);
		chestKey.setTooltip("You can get another one from Redbeard Frank");

		inBlueMoonFirst = new ZoneRequirement(blueMoonFirst);
	}

	private void setupSteps()
	{
		speakToRedbeard = new NpcStep(this, NpcID.REDBEARD_FRANK, new WorldPoint(3053, 3251, 0),
			"Talk to Redbeard Frank in Port Sarim.");
		speakToRedbeard.addDialogSteps("I'm in search of treasure.", "Yes.");


		readPirateMessage = new DetailedQuestStep(this, "Read the Pirate message.", pirateMessage.highlighted());


		climbStairs = new ObjectStep(this, ObjectID.FAI_VARROCK_STAIRS, new WorldPoint(3228, 3393, 0), "Climb up the stairs in The Blue Moon Inn in Varrock.", chestKey);
		climbStairs.addTeleport(teleportVarrock);
		openChest = new ObjectStep(this, ObjectID.PIRATECHEST, new WorldPoint(3219, 3396, 1), "Open the chest by using the key on it.", chestKey.highlighted());
		openChest.addDialogStep("Ok thanks, I'll go and get it.");
		openChest.addIcon(ItemID.CHEST_KEY);

		smuggleRum = new RumSmugglingStep(this);

		digUpTreasure = new DigStep(this, new WorldPoint(2999, 3383, 0), "Dig in the middle of the cross in Falador Park, and kill the Gardener (level 4) who appears. Once killed, dig again.");
		// TODO: Add a teleport to DigStep

		killGardener = new NpcStep(this, NpcID.PIRATE_IRATE_GARDENER, new WorldPoint(2999, 3383, 0), "Kill the Gardener (level 4).");
		digUpTreasure.addSubSteps(killGardener);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, speakToRedbeard);

		steps.put(1, smuggleRum);

		var getTreasureMap = new ConditionalStep(this, climbStairs);
		getTreasureMap.addStep(pirateMessage, readPirateMessage);
		getTreasureMap.addStep(inBlueMoonFirst, openChest);

		steps.put(2, getTreasureMap);

		var cDigUpTreasure = new ConditionalStep(this, digUpTreasure);
		cDigUpTreasure.addStep(new NpcHintArrowRequirement(NpcID.PIRATE_IRATE_GARDENER), killGardener);
		steps.put(3, cDigUpTreasure);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			sixtyCoins,
			spade,
			tenBananas
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			teleportVarrock,
			teleportFalador
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Gardener (level 4)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("A Gold Ring", ItemID.GOLD_RING, 1),
			new ItemReward("An Emerald", ItemID.EMERALD, 1),
			new ItemReward("Coins", ItemID.COINS, 450)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Talk to Redbeard Frank", List.of(
			speakToRedbeard
		), List.of(
			sixtyCoins
		)));

		sections.addAll(smuggleRum.panelDetails());

		sections.add(new PanelDetails("Discover the treasure", List.of(
			climbStairs,
			openChest,
			readPirateMessage,
			digUpTreasure
		), List.of(
			spade
		)));

		return sections;
	}
}
