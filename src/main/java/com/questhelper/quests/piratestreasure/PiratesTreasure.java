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
package com.questhelper.quests.piratestreasure;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.PIRATES_TREASURE
)
public class PiratesTreasure extends BasicQuestHelper
{
	//ItemRequirements
	ItemRequirement sixtyCoins, spade, pirateMessage, chestKey;

	private NpcStep speakToRedbeard;

	private RumSmugglingStep smuggleRum;

	private QuestStep readPirateMessage;

	private ObjectStep openChest, climbStairs;

	private QuestStep digUpTreasure;

	Zone blueMoonFirst;

	ZoneRequirement inBlueMoonFirst;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();

		Map<Integer, QuestStep> steps = new HashMap<>();

		speakToRedbeard = new NpcStep(this, NpcID.REDBEARD_FRANK, new WorldPoint(3053, 3251, 0),
			"Talk to Redbeard Frank in Port Sarim.");
		speakToRedbeard.addDialogStep("I'm in search of treasure.");
		speakToRedbeard.addDialogStep("Ok, I will bring you some rum");

		steps.put(0, speakToRedbeard);

		smuggleRum = new RumSmugglingStep(this);

		steps.put(1, smuggleRum);

		readPirateMessage = new DetailedQuestStep(this, "Read the Pirate message.", pirateMessage.highlighted());
		climbStairs = new ObjectStep(this, ObjectID.STAIRCASE_11796, new WorldPoint(3228, 3393, 0),
			"Climb up the stairs in The Blue Moon Inn in Varrock.");
		openChest = new ObjectStep(this, ObjectID.CHEST_2079, new WorldPoint(3219, 3396, 1),
			"Open the chest by using the key on it.", chestKey.highlighted());
		openChest.addDialogStep("Ok thanks, I'll go and get it.");
		openChest.addIcon(ItemID.CHEST_KEY);

		inBlueMoonFirst = new ZoneRequirement(blueMoonFirst);
		blueMoonFirst = new Zone(new WorldPoint(3213, 3405, 1), new WorldPoint(3234, 3391, 1));

		ConditionalStep getTreasureMap = new ConditionalStep(this, climbStairs);
		getTreasureMap.addStep(new Conditions(chestKey, inBlueMoonFirst), openChest);
		getTreasureMap.addStep(pirateMessage, readPirateMessage);

		steps.put(2, getTreasureMap);

		digUpTreasure = new DigStep(this, new WorldPoint(2999, 3383, 0),
			"Dig in the middle of the cross in Falador Park, and kill the Gardener (level 4) who appears. Once killed, dig again.");

		steps.put(3, digUpTreasure);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		sixtyCoins = new ItemRequirement("Coins", ItemCollections.COINS, 60);
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		pirateMessage = new ItemRequirement("Pirate message", ItemID.PIRATE_MESSAGE);
		chestKey = new ItemRequirement("Chest key", ItemID.CHEST_KEY);
		chestKey.setTooltip("You can get another one from Redbeard Frank");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(sixtyCoins);
		reqs.add(spade);

		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(new ItemRequirement("A teleport to Varrock", ItemID.VARROCK_TELEPORT));
		reqs.add(new ItemRequirement("A teleport to Falador", ItemID.FALADOR_TELEPORT));
		reqs.add(new ItemRequirement("Bananas (obtainable in quest)", ItemID.BANANA, 10));

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Gardener (level 4)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("A Gold Ring", ItemID.GOLD_RING, 1),
				new ItemReward("An Emerald", ItemID.EMERALD, 1),
				new ItemReward("450 Coins", ItemID.COINS_995, 450));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Talk to Redbeard Frank", Collections.singletonList(speakToRedbeard), sixtyCoins));
		allSteps.addAll(smuggleRum.panelDetails());
		allSteps.add(new PanelDetails("Discover the treasure", Arrays.asList(climbStairs, openChest, readPirateMessage,
			digUpTreasure), spade));

		return allSteps;
	}
}
