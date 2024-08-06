
package com.questhelper.helpers.quests.whileguthixsleeps;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.OwnerStep;
import com.questhelper.steps.QuestStep;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class WeightStep extends QuestStep implements OwnerStep
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
		setupItemRequirements();
		setupSteps();
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		int goal = client.getVarbitValue(10936);
		int weightOnStatue = client.getVarbitValue(10937);
		int totalWeightGoal = goal + weightOnStatue;
		int playerWeight = client.getWeight();
		int currentTotalWeight = playerWeight - weightOnStatue;

		ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

		int weightInInventoryFromWeights = 0;
		for (Item item : inventory.getItems())
		{
			if (item.getId() == ItemID.WEIGHT_1KG)
			{
				weightInInventoryFromWeights += 1;
			}
			else if (item.getId() == ItemID.WEIGHT_2KG)
			{
				weightInInventoryFromWeights += 2;
			}
			else if (item.getId() == ItemID.WEIGHT_5KG)
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

	protected void startUpStep(QuestStep step)
	{
		if (currentStep == null)
		{
			currentStep = step;
			eventBus.register(currentStep);
			currentStep.startUp();
			return;
		}

		if (!step.equals(currentStep))
		{
			shutDownStep();
			eventBus.register(step);
			step.startUp();
			currentStep = step;
		}
	}

	protected void shutDownStep()
	{
		if (currentStep != null)
		{
			eventBus.unregister(currentStep);
			currentStep.shutDown();
			currentStep = null;
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, List<String> additionalText, List<Requirement> requirements)
	{
		if (currentStep != null)
		{
			currentStep.makeOverlayHint(panelComponent, plugin, additionalText, requirements);
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldArrowOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWorldLineOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldLineOverlayHint(graphics, plugin);
		}
	}

	@Override
	public QuestStep getActiveStep()
	{
		if (currentStep != this)
		{
			return currentStep.getActiveStep();
		}
		else
		{
			return this;
		}
	}

	private void setupItemRequirements()
	{
		weight1Kg = new ItemRequirement("Weight (1kg)", ItemID.WEIGHT_1KG);
		weight2Kg = new ItemRequirement("Weight (2kg)", ItemID.WEIGHT_2KG);
		weight5Kg = new ItemRequirement("Weight (5kg)", ItemID.WEIGHT_5KG);

		weights = new ItemRequirement("Weights", ItemID.WEIGHT_1KG);
		weights.addAlternates(ItemID.WEIGHT_2KG, ItemID.WEIGHT_5KG);
	}

	private void setupSteps()
	{
		weightRoom = new Zone(new WorldPoint(4177, 4944, 1), new WorldPoint(4181, 4946, 1));
		inWeightRoom = new ZoneRequirement(weightRoom);

		take1Kg = new ObjectStep(getQuestHelper(), ObjectID.PILE_OF_WEIGHTS, new WorldPoint(4177, 4945, 1), "Take 1 kg from the pile of weights in the room.");
		take1Kg.addDialogStep("1kg weight.");
		take2Kg = new ObjectStep(getQuestHelper(), ObjectID.PILE_OF_WEIGHTS, new WorldPoint(4177, 4945, 1), "Take 2 kg from the pile of weights in the room.");
		take2Kg.addDialogStep("2kg weight.");
		take5Kg = new ObjectStep(getQuestHelper(), ObjectID.PILE_OF_WEIGHTS, new WorldPoint(4177, 4945, 1), "Take 5 kg from the pile of weights in the room.");
		take5Kg.addDialogStep("5kg weight.");
		crossOverBrokenWallNorth = new ObjectStep(getQuestHelper(), ObjectID.BROKEN_WALL_53884, new WorldPoint(4179, 4947, 1),
			"Cross over the broken wall into the north room.");
		useWeights = new ObjectStep(getQuestHelper(), NullObjectID.NULL_53934, new WorldPoint(4182, 4956, 1), "Use all your weights on the statue in the north-east of the room.", weights.highlighted());
		useWeights.addSubSteps(crossOverBrokenWallNorth);
		openDoor = new ObjectStep(getQuestHelper(), NullObjectID.NULL_54014, new WorldPoint(4187, 4953, 1), "Leave through the door in the north-east.");
		takeWeightFromStatue = new ObjectStep(getQuestHelper(), NullObjectID.NULL_53934, new WorldPoint(4182, 4956, 1), "Remove weights from the statue in the north-east of the room.");
		takeWeightFromStatue.addDialogStep("Remove the weights from the statue.");
		dropWeights = new DetailedQuestStep(getQuestHelper(), "Drop some of the weights on you.");

		crossOverBrokenWall = new ObjectStep(getQuestHelper(), ObjectID.BROKEN_WALL_53884, new WorldPoint(4179, 4947, 1),
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
		return Arrays.asList(crossOverBrokenWall, take1Kg, take2Kg, take5Kg, crossOverBrokenWallNorth, useWeights, openDoor, takeWeightFromStatue, dropWeights);
	}
}
