/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.quests.whileguthixsleeps;

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class WeightStep extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	private QuestStep crossOverBrokenWall, takeWeights, take1Kg, take2Kg, take5Kg, crossOverBrokenWallNorth, useWeights, openDoor, takeWeightFromStatue, dropWeights;

	ItemRequirement weight1Kg, weight2Kg, weight5Kg, weights;

	protected QuestStep currentStep;
	private Zone weightRoom;
	private ZoneRequirement inWeightRoom;

	public WeightStep(QuestHelper questHelper)
	{
		super(questHelper, "");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		int goal = client.getVarbitValue(VarbitID.WGS_WEIGHT_VAR);
		int weightOnStatue = client.getVarbitValue(VarbitID.WGS_WEIGHT_STATUE_VAR);
		int totalWeightGoal = goal + weightOnStatue;
		int playerWeight = client.getWeight();

		ItemContainer inventory = client.getItemContainer(InventoryID.INV);

		int weightInInventoryFromWeights = 0;
		for (Item item : inventory.getItems())
		{
			if (item.getId() == ItemID.WGS_WEIGHT_1KG)
			{
				weightInInventoryFromWeights += 1;
			}
			else if (item.getId() == ItemID.WGS_WEIGHT_2KG)
			{
				weightInInventoryFromWeights += 2;
			}
			else if (item.getId() == ItemID.WGS_WEIGHT_5KG)
			{
				weightInInventoryFromWeights += 5;
			}
		}

		int playerWeightWithoutWeights = playerWeight - weightInInventoryFromWeights;

		if (playerWeight == totalWeightGoal)
		{
			startUpStep(openDoor);
		}
		else if (playerWeightWithoutWeights == totalWeightGoal + weightInInventoryFromWeights)
		{
			if (inWeightRoom.check(client))
			{
				startUpStep(crossOverBrokenWallNorth);
			}
			else
			{
				startUpStep(useWeights);
				// go use weights on statue
			}
		}
		else if (playerWeight < totalWeightGoal)
		{
			if (!inWeightRoom.check(client))
			{
				startUpStep(crossOverBrokenWall);
			}
			else
			{
				int diffPlayerWeightToGoal = totalWeightGoal - playerWeight;
				if (diffPlayerWeightToGoal / 5 >= 1)
				{
					startUpStep(take5Kg);
				}
				else if (diffPlayerWeightToGoal / 2 >= 1)
				{
					startUpStep(take2Kg);
				}
				else
				{
					startUpStep(take1Kg);
				}
			}
		}
		else if (playerWeightWithoutWeights < totalWeightGoal + weightInInventoryFromWeights)
		{
			if (weightInInventoryFromWeights > 0)
			{
				startUpStep(dropWeights);
			}
			// if X, dropWeights
			else if (weightOnStatue > 0)
			{
				startUpStep(takeWeightFromStatue);
			}
			// Drop weights/remove weights from the statue
		}
		else if (playerWeightWithoutWeights > totalWeightGoal + weightInInventoryFromWeights)
		{
			if (!inWeightRoom.check(client))
			{
				startUpStep(crossOverBrokenWall);
			}
			else
			{
				int diff = playerWeightWithoutWeights - (totalWeightGoal + weightInInventoryFromWeights);
				if (diff / 5 >= 1)
				{
					startUpStep(take5Kg);
				}
				else if (diff / 2 >= 1)
				{
					startUpStep(take2Kg);
				}
				else
				{
					startUpStep(take1Kg);
				}
			}
		}

		// Player weighs more than goal (thus need to put weights on)
		// Player weighs less than goal (thus need to hold weights in inventory)

		// 10935 = number of weights
		// 10937 = total weight on statue
	}

	private void setupItemRequirements()
	{
		weight1Kg = new ItemRequirement("Weight (1kg)", ItemID.WGS_WEIGHT_1KG);
		weight2Kg = new ItemRequirement("Weight (2kg)", ItemID.WGS_WEIGHT_2KG);
		weight5Kg = new ItemRequirement("Weight (5kg)", ItemID.WGS_WEIGHT_5KG);

		weights = new ItemRequirement("Weights", ItemID.WGS_WEIGHT_1KG);
		weights.addAlternates(ItemID.WGS_WEIGHT_2KG, ItemID.WGS_WEIGHT_5KG);
	}

	@Override
	protected void setupSteps()
	{
		setupItemRequirements();
		
		weightRoom = new Zone(new WorldPoint(4177, 4944, 1), new WorldPoint(4181, 4946, 1));
		inWeightRoom = new ZoneRequirement(weightRoom);

		take1Kg = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOV_WEIGHTS, new WorldPoint(4177, 4945, 1), "Take 1 kg from the pile of weights in the room.");
		take1Kg.addDialogStep("1kg weight.");
		take2Kg = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOV_WEIGHTS, new WorldPoint(4177, 4945, 1), "Take 2 kg from the pile of weights in the room.");
		take2Kg.addDialogStep("2kg weight.");
		take5Kg = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOV_WEIGHTS, new WorldPoint(4177, 4945, 1), "Take 5 kg from the pile of weights in the room.");
		take5Kg.addDialogStep("5kg weight.");
		crossOverBrokenWallNorth = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOVARIO_BASE_PAINTING_WALL_CLICK, new WorldPoint(4179, 4947, 1),
			"Cross over the broken wall into the north room.");
		useWeights = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOV_STATUE_ATLAS_MULTI, new WorldPoint(4182, 4956, 1), "Use all your weights on the statue in the north-east of the room.", weights.highlighted());
		useWeights.addSubSteps(crossOverBrokenWallNorth);
		openDoor = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOV_BARRACKS_DOOR_BACKING_01, new WorldPoint(4187, 4953, 1), "Leave through the door in the north-east.");
		takeWeightFromStatue = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOV_STATUE_ATLAS_MULTI, new WorldPoint(4182, 4956, 1), "Remove weights from the statue in the north-east of the room.");
		takeWeightFromStatue.addDialogStep("Remove the weights from the statue.");
		dropWeights = new DetailedQuestStep(getQuestHelper(), "Drop some of the weights on you.");

		crossOverBrokenWall = new ObjectStep(getQuestHelper(), ObjectID.LUC2_MOVARIO_BASE_PAINTING_WALL_CLICK, new WorldPoint(4179, 4947, 1),
			"Cross over the broken wall into the south room.");

		takeWeights = new DetailedQuestStep(getQuestHelper(), "Take weights to equal the difference between you on entering and now.");
		takeWeights.addSubSteps(crossOverBrokenWall, take1Kg, take2Kg, take5Kg, takeWeightFromStatue, dropWeights);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return List.of(takeWeights, useWeights, openDoor);
	}


	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(crossOverBrokenWall, take1Kg, take2Kg, take5Kg, crossOverBrokenWallNorth, useWeights, openDoor, takeWeightFromStatue,
				dropWeights, takeWeights);
	}
}
