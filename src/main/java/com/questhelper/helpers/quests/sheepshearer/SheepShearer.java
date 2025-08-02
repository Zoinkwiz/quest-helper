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
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
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
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import com.questhelper.util.QHObjectID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.eventbus.Subscribe;

public class SheepShearer extends BasicQuestHelper
{
	// Required items
	ItemRequirement ballOfWool;
	ItemRequirement shears;
	ItemRequirement woolOrBalls;
	ItemRequirement onlyWool;
	ItemRequirement totalWoolNeeded;
	ItemRequirement totalBallsNeeded;

	// Zones
	Zone castleSecond;

	// Miscellaneous requirements
	Requirement inCastleSecond;
	// TODO: We should be able to use a EmptyInvSlot requirement here
	ManualRequirement skipIfFullInventory;

	NpcStep startStep;
	ItemStep getSheers;
	NpcStep shearSheep;
	ObjectStep climbStairsUp;
	ObjectStep climbStairsDown;
	ObjectStep spinBalls;
	NpcStep turnInBalls;

	int woolNeeded = 20;

	@Override
	protected void setupZones()
	{
		castleSecond = new Zone(new WorldPoint(3200, 3232, 1), new WorldPoint(3220, 3205, 1));
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

		totalWoolNeeded = woolOrBalls.quantity(20);
		totalBallsNeeded = ballOfWool.quantity(20);

		inCastleSecond = new ZoneRequirement(castleSecond);
		skipIfFullInventory = new ManualRequirement();

		var inventory = client.getItemContainer(InventoryID.INV);
		if (inventory != null)
		{
			int itemsInInventory = inventory.count();
			skipIfFullInventory.setShouldPass(itemsInInventory == 28);
		}

		updateWoolNeeded();
	}

	public void setupSteps()
	{
		startStep = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0),
			"Talk to Fred the Farmer north of Lumbridge to start the quest. Bring 20 balls of wool to autocomplete the quest.");
		startStep.addDialogSteps("I'm looking for a quest.", "Yes, okay. I can do that.", "Yes.");
		getSheers = new ItemStep(this, new WorldPoint(3190, 3273, 0),
			"Pickup the shears in Fred's house.", shears);
		shearSheep = new NpcStep(this, NpcID.SHEEPUNSHEERED3G, new WorldPoint(3201, 3268, 0),
			"Shear sheep in the nearby field.", true, shears, totalWoolNeeded);
		shearSheep.addAlternateNpcs(NpcID.SHEEPUNSHEERED3, NpcID.SHEEPUNSHEERED3W, NpcID.SHEEPUNSHEERED, NpcID.SHEEPUNSHEEREDG, NpcID.SHEEPUNSHEERED3, NpcID.SHEEPUNSHEEREDW);
		climbStairsUp = new ObjectStep(this, QHObjectID.LUMBRIDGE_CASTLE_F0_SOUTH_STAIRCASE, new WorldPoint(3204, 3207, 0),
			"Climb the staircase in the Lumbridge Castle to spin the wool in your inventory into balls of wool.", totalWoolNeeded);
		spinBalls = new ObjectStep(this, ObjectID.SPINNINGWHEEL, new WorldPoint(3209, 3212, 1),
			"Spin your wool into balls.", totalWoolNeeded);
		spinBalls.addWidgetHighlight(WidgetHighlight.createMultiskillByItemId(ItemID.BALL_OF_WOOL));
		climbStairsDown = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(3204, 3207, 1),
			"Climb down the staircase.", totalBallsNeeded);
		climbStairsDown.addDialogSteps("Climb down the stairs.");
		turnInBalls = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0),
			"Bring Fred the Farmer north of Lumbridge your balls of wool (UNNOTED) to progress the quest. If you only have some of the balls needed, you can still deposit them with him.",
			totalBallsNeeded);
		turnInBalls.addDialogSteps("I need to talk to you about shearing these sheep!");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		// If you have all the wool you need, OR you have filled your inventory with wool
		var hasAllWoolOrFullInv = or(totalWoolNeeded, and(woolOrBalls, skipIfFullInventory));
		// If you have all the balls needed, OR you've made all the wool you had in your inventory into balls of wool
		var hasAllBallsOrFullInv = or(totalBallsNeeded, and(nor(onlyWool), ballOfWool));
		var craftingBalls = new ConditionalStep(this, getSheers);
		craftingBalls.addStep(and(hasAllBallsOrFullInv, inCastleSecond), climbStairsDown);
		craftingBalls.addStep(hasAllBallsOrFullInv, turnInBalls);
		craftingBalls.addStep(and(hasAllWoolOrFullInv, inCastleSecond), spinBalls);
		craftingBalls.addStep(hasAllWoolOrFullInv, climbStairsUp);
		craftingBalls.addStep(shears, shearSheep);

		steps.put(0, startStep);
		IntStream.range(1, 21).forEach(i -> steps.put(i, craftingBalls));

		return steps;
	}

	private void updateWoolNeeded()
	{
		var sheepVarp = client.getVarpValue(VarPlayerID.SHEEP);
		var newWoolNeeded = sheepVarp > 1 ? 21 - sheepVarp : 20;
		if (newWoolNeeded == woolNeeded)
		{
			return;
		}
		woolNeeded = newWoolNeeded;
		totalBallsNeeded.setQuantity(woolNeeded);
		totalWoolNeeded.setQuantity(woolNeeded);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INV)
		{
			return;
		}

		// If inventory full
		skipIfFullInventory.setShouldPass(event.getItemContainer().count() == 28);

		updateWoolNeeded();
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			totalBallsNeeded,
			shears
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 150)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 60)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Bring Fred Some Wool", List.of(
			startStep,
			getSheers,
			shearSheep,
			climbStairsUp,
			spinBalls,
			climbStairsDown,
			turnInBalls
		), List.of(
			totalBallsNeeded
		)));

		return steps;
	}
}
