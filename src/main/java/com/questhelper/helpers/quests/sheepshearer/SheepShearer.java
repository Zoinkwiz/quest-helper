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
package com.questhelper.helpers.quests.sheepshearer;

import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;

public class SheepShearer extends BasicQuestHelper
{
	//Items Required
	ItemRequirement ballOfWool, shears, woolOrBalls, onlyWool, totalWoolNeeded, totalBallsNeeded;

	QuestStep startStep, getSheers, climbStairsUp, climbStairsDown, spinBalls, turnInBalls;

	NpcStep shearSheep;

	Zone castleSecond;

	Requirement inCastleSecond;

	ManualRequirement skipIfFullInventory;

	int woolNeeded;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		// If you have all the wool you need, OR you have filled your inventory with wool
		Requirement hasAllWoolOrFullInv = or(totalWoolNeeded, and(woolOrBalls, skipIfFullInventory));
		// If you have all the balls needed, OR you've made all the wool you had in your inventory into balls of wool
		Requirement hasAllBallsOrFullInv = or(totalBallsNeeded, and(nor(onlyWool), ballOfWool));
		ConditionalStep craftingBalls = new ConditionalStep(this, getSheers);
		craftingBalls.addStep(and(hasAllBallsOrFullInv, inCastleSecond), climbStairsDown);
		craftingBalls.addStep(hasAllBallsOrFullInv, turnInBalls);
		craftingBalls.addStep(and(hasAllWoolOrFullInv, inCastleSecond), spinBalls);
		craftingBalls.addStep(hasAllWoolOrFullInv, climbStairsUp);
		craftingBalls.addStep(shears, shearSheep);

		steps.put(0, startStep);
		IntStream.range(1, 20).forEach(i -> steps.put(i, craftingBalls));

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		ballOfWool = new ItemRequirement("Balls of wool", ItemID.BALL_OF_WOOL);
		shears = new ItemRequirement("Shears", ItemID.SHEARS).isNotConsumed();
		shears.setTooltip("If you plan on collecting wool yourself");
		woolOrBalls = new ItemRequirement("Wool", ItemID.WOOL);
		woolOrBalls.addAlternates(ItemID.BALL_OF_WOOL);
		onlyWool = new ItemRequirement("Wool", ItemID.WOOL);

		woolNeeded = client.getVarpValue(179) > 1 ? 21 - client.getVarpValue(179) : 20;
		totalWoolNeeded = woolOrBalls.quantity(woolNeeded);
		totalBallsNeeded = ballOfWool.quantity(woolNeeded);
	}

	@Override
	protected void setupZones()
	{
		castleSecond = new Zone(new WorldPoint(3200, 3232, 1), new WorldPoint(3220, 3205, 1));
	}

	public void setupConditions()
	{
		inCastleSecond = new ZoneRequirement(castleSecond);
		skipIfFullInventory = new ManualRequirement();

		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory == null) return;

		int itemsInInventory = inventory.count();
		skipIfFullInventory.setShouldPass(itemsInInventory == 28);
	}

	public void setupSteps()
	{
		startStep = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0),
			"Talk to Fred the Farmer north of Lumbridge to start the quest. Bring 20 balls of wool to autocomplete the quest.");
		startStep.addDialogSteps("I'm looking for a quest.", "Yes, okay. I can do that.", "Yes.");
		getSheers = new ItemStep(this, new WorldPoint(3190, 3273, 0),
			"Pickup the shears in Fred's house.", shears);
		shearSheep = new NpcStep(this, NpcID.SHEEP_2786, new WorldPoint(3201, 3268, 0),
			"Shear " + woolNeeded + " sheep in the nearby field.", true, shears);
		shearSheep.addAlternateNpcs(NpcID.SHEEP_2699, NpcID.SHEEP_2787, NpcID.SHEEP_2693, NpcID.SHEEP_2694, NpcID.SHEEP_2699, NpcID.SHEEP_2695);
		climbStairsUp = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(3204, 3207, 0),
			"Climb the staircase in the Lumbridge Castle to spin the wool into balls of wool.", totalWoolNeeded);
		spinBalls = new ObjectStep(this, ObjectID.SPINNING_WHEEL_14889, new WorldPoint(3209, 3212, 1),
			"Spin your wool into balls.", totalWoolNeeded);
		spinBalls.addWidgetHighlight(270, 14);
		climbStairsDown = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(3204, 3207, 1),
			"Climb down the staircase.", totalBallsNeeded);
		climbStairsDown.addDialogSteps("Climb down the stairs.");
		turnInBalls = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0),
			"Bring Fred the Farmer north of Lumbridge " + woolNeeded + " balls of wool (UNNOTED) to finish the quest. If you only have some of the balls needed, you can still deposit them with him.",
			totalBallsNeeded);
		turnInBalls.addDialogSteps("I need to talk to you about shearing these sheep!");
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INVENTORY.getId())
		{
			return;
		}

		woolNeeded = client.getVarpValue(179) > 1 ? 21 - client.getVarpValue(179) : 20;
		totalBallsNeeded.setQuantity(woolNeeded);
		totalWoolNeeded.setQuantity(woolNeeded);

		turnInBalls.setText("Bring Fred the Farmer north of Lumbridge " + woolNeeded + " balls of wool (UNNOTED) to finish the quest.");
		shearSheep.setText("Shear " + woolNeeded + " sheep in the nearby field.");

		// If inventory full
		skipIfFullInventory.setShouldPass(event.getItemContainer().count() == 28);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ballOfWool.quantity(20));
		reqs.add(shears);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.CRAFTING, 150));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS_995, 60));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Bring Fred Some Wool", Arrays.asList(startStep, getSheers, shearSheep,
			climbStairsUp, spinBalls, climbStairsDown, turnInBalls), ballOfWool.quantity(20)));
		return allSteps;
	}
}
