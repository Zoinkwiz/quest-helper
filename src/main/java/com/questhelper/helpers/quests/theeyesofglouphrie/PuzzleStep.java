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
package com.questhelper.helpers.quests.theeyesofglouphrie;

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import com.questhelper.steps.widget.WidgetDetails;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;

public class PuzzleStep extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	HashMap<Integer, ItemRequirement> shapeValues = new HashMap<>();

	HashMap<Integer, List<List<ItemRequirement>>> shapeValues3 = new HashMap<>();
	HashMap<Integer, List<List<ItemRequirement>>> shapeValues4 = new HashMap<>();

	ItemRequirement slot1Item, slot2Item, slot3Item, yellowCircleRedTri, greenCircleRedSquare, blueCircleRedPentagon, blueSquareGreenPentagon, indigoCircleOrangeTriangle, yellowSquareGreenTriangle, yellowPentagonBlueTriangle;

	HashMap<Integer, ItemRequirement> shapes = new HashMap<>();

	WidgetStep clickAnswer1, insertDisc, clickDiscHole, clickDiscHole2, clickDiscHole3, clickDiscHole4, clickAnswer2;

	ObjectStep solvePuzzle, getPieces;

	int items1;

	List<ItemRequirement> items2 = new ArrayList<>();
	List<ItemRequirement> items3 = new ArrayList<>();
	List<ItemRequirement> items4 = new ArrayList<>();

	Item[] lastInv;

	Item[] currentInv;

	int newMostMatch3 = -1;
	int mostMatch4 = -1;

	public PuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Insert and swap discs to make the sum indicated on the machine.");
		setupShapes();
	}

	@Override
	public void startUp()
	{
		super.startUp();
		updateSteps();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	public void updateSteps()
	{
		if (client.getVarbitValue(VarbitID.EYEGLO_MACHINE_BROKEN) == 2)
		{
			solvePuzzle2();
		}
		else
		{
			solvePuzzle1();
		}
	}

	@Override
	public void setupSteps()
	{
		solvePuzzle = new ObjectStep(getQuestHelper(), ObjectID.EYEGLO_GNOME_MACHINE_02_MULTILOC, new WorldPoint(2390, 9826, 0), "Put in the correct pieces.");
		getPieces = new ObjectStep(getQuestHelper(), ObjectID.EYEGLO_CHANGE_MACHINE_MULTILOC, new WorldPoint(2391, 9826, 0), "Swap in" +
				" your pieces for the indicated pieces. You can also drop the discs then talk to Brimstail for more tokens.");
		clickAnswer1 = new WidgetStep(getQuestHelper(), "Click the submit button.", InterfaceID.EyegloGnomeMachineLocked.ARROW_MIDDLE);
		clickAnswer1.setShouldOverlayWidget(true);
		clickAnswer2 = new WidgetStep(getQuestHelper(), "Click the submit button.", InterfaceID.EyegloGnomeMachineUnlocked.CORRECT);
		clickAnswer2.setShouldOverlayWidget(true);
		insertDisc = new WidgetStep(getQuestHelper(), "Insert the correct discs.", InterfaceID.EyegloGnomeMachineLocked.UNLOCK_COMP_INSERT);
		insertDisc.setShouldOverlayWidget(true);
		clickDiscHole = new WidgetStep(getQuestHelper(), "Insert the disc.", InterfaceID.EyegloGnomeMachineLocked.UNLOCK_COMP_INSERT);
		clickDiscHole.setShouldOverlayWidget(true);
		clickDiscHole2 = new WidgetStep(getQuestHelper(), "Insert the disc.", InterfaceID.EyegloGnomeMachineUnlocked.SET_1_COINSLOT);
		clickDiscHole2.setShouldOverlayWidget(true);
		clickDiscHole3 = new WidgetStep(getQuestHelper(), "Insert the disc.", InterfaceID.EyegloGnomeMachineUnlocked.SET_2_COINSLOT);
		clickDiscHole3.setShouldOverlayWidget(true);
		clickDiscHole4 = new WidgetStep(getQuestHelper(), "Insert the disc.", InterfaceID.EyegloGnomeMachineUnlocked.SET_3_COINSLOT);
		clickDiscHole4.setShouldOverlayWidget(true);
	}

	public void solvePuzzle1()
	{
		int answer1 = client.getVarbitValue(VarbitID.EYEGLO_COIN_VALUE_1);
		int heldDisc = client.getVarpValue(VarPlayerID.EYEGLO_COIN_SELECTED);
		Widget insertWidget = client.getWidget(InterfaceID.EyegloSide.INV_LAYER);

		if (client.getVarbitValue(VarbitID.EYEGLO_UNLOCK_VAL) == answer1)
		{
			startUpStep(clickAnswer1);
			return;
		}

		ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
		if (itemContainer == null)
		{
			return;
		}
		Item[] inventoryItemsArr = itemContainer.getItems();

		items1 = -1;
		List<Item> inventoryItems = new ArrayList<>(Arrays.asList(inventoryItemsArr));

		for (Integer id : shapeValues.get(answer1).getAllIds())
		{
			int match = checkForItems(inventoryItems, id);
			if (match != -1)
			{
				items1 = shapes.get(id).getId();
				break;
			}
		}

		if (items1 == -1)
		{
			getPieces.emptyRequirements();
			getPieces.addRequirement(shapeValues.get(answer1));
			startUpStep(getPieces);
			return;
		}

		// If in interface and have all disks needed
		if (insertWidget != null && !insertWidget.isHidden())
		{
			if (items1 == heldDisc)
			{
				startUpStep(clickDiscHole);
				return;
			}

			// Not holding a disk to insert, should click a disk to put into hand
			if (currentInv != null)
			{
				List<WidgetDetails> ids = new ArrayList<>();
				ids = getClickableItems(ids, List.of(items1));

				insertDisc.setWidgetDetails(ids);
				insertDisc.setRequirements(Collections.singletonList(shapes.get(items1)));

				startUpStep(insertDisc);
			}
			return;
		}

		solvePuzzle.setRequirements(Collections.singletonList(shapes.get(items1)));
		startUpStep(solvePuzzle);
	}

	public void solvePuzzle2()
	{
		int heldDisc = client.getVarpValue(VarPlayerID.EYEGLO_COIN_SELECTED);

		int answer1 = client.getVarbitValue(VarbitID.EYEGLO_COIN_VALUE_2); // 1 input
		int answer2 = client.getVarbitValue(VarbitID.EYEGLO_COIN_VALUE_3); // 2 input
		int answer3 = client.getVarbitValue(VarbitID.EYEGLO_COIN_VALUE_4); // 3 input


		if (client.getVarbitValue(VarbitID.EYEGLO_OPERATE1_VAL) == answer1 && client.getVarbitValue(VarbitID.EYEGLO_OPERATE2_VAL) == answer2 && client.getVarbitValue(VarbitID.EYEGLO_OPERATE3_VAL) == answer3)
		{
			startUpStep(clickAnswer2);
			return;
		}

		ItemContainer itemContainer = client.getItemContainer(InventoryID.INV);
		if (itemContainer == null)
		{
			return;
		}

		Item[] inventoryItemsArr = itemContainer.getItems();

		List<Item> inventoryItems = new ArrayList<>(Arrays.asList(inventoryItemsArr));

		Widget insertWidget = client.getWidget(InterfaceID.EyegloGnomeMachineUnlocked.MACHINE_UNLOACKED_BACKGROUND);

		int slot1 = client.getVarpValue(VarPlayerID.EYEGLO_OPERATE1_A);

		int slot2 = client.getVarpValue(VarPlayerID.EYEGLO_OPERATE2_A);
		int slot3 = client.getVarpValue(VarPlayerID.EYEGLO_OPERATE2_B);

		int slot4 = client.getVarpValue(VarPlayerID.EYEGLO_OPERATE3_A);
		int slot5 = client.getVarpValue(VarPlayerID.EYEGLO_OPERATE3_B);
		int slot6 = client.getVarpValue(VarPlayerID.EYEGLO_OPERATE3_C);

		newMostMatch3 = -1;
		mostMatch4 = -1;

		lastInv = inventoryItemsArr;

		// Puzzle 2

		// Loop through all shapes which have equal value to goal
		for (Integer id : shapeValues.get(answer1).getAllIds())
		{
			items2 = Collections.singletonList(shapeValues.get(answer1));
			int match = checkForItems(inventoryItems, id);

			// If found item in inventory
			if (match != -1)
			{

				if (inventoryItems.get(match).getQuantity() > 1)
				{
					Item newItem = new Item(inventoryItems.get(match).getId(), inventoryItems.get(match).getQuantity() - 1);
					inventoryItems.set(match, newItem);
				}
				else
				{
					inventoryItems.remove(match);
				}
			}

			// If the item is already inserted into the slot, don't indicate to add it
			if (id == slot1)
			{
				items2 = new ArrayList<>();
				break;
			}
		}

		List<ItemRequirement> allRequirements = new ArrayList<>(items2);

		// Puzzle 3
		List<ItemRequirement> newReq3 = new ArrayList<>();
		List<Item> newInventory3 = new ArrayList<>(inventoryItems);
		List<Item> tmpInventory3;
		for (List<ItemRequirement> reqs : shapeValues3.get(answer2))
		{
			// Each duo
			List<Integer> currentSlotIDs3 = new ArrayList<>(Arrays.asList(slot2, slot3));
			tmpInventory3 = new ArrayList<>(inventoryItems);
			List<ItemRequirement> tmpReqs = new ArrayList<>(reqs);
			int currentMatches = 0;
			for (ItemRequirement req : reqs)
			{
				for (int i = 0; i < req.getAllIds().size(); i++)
				{
					int match = checkForItems(tmpInventory3, req.getAllIds().get(i));
					if (match != -1)
					{
						currentMatches++;
						if (tmpInventory3.get(match).getQuantity() > 1)
						{
							Item newItem = new Item(tmpInventory3.get(match).getId(), tmpInventory3.get(match).getQuantity() - 1);
							tmpInventory3.set(match, newItem);
						}
						else
						{
							tmpInventory3.remove(match);
						}

						// If this shape has already been added, remove it from requirements as well as slot?
						if (currentSlotIDs3.contains(req.getAllIds().get(i)))
						{
							tmpReqs.remove(req);
							currentSlotIDs3.remove(req.getAllIds().get(i));
						}
						break;
					}
				}
			}
			// If we have more pieces, OR have same amount of pieces already but more already in the machine
			if (currentMatches > newMostMatch3 || (currentMatches == newMostMatch3 && newReq3.size() > tmpReqs.size()))
			{
				newMostMatch3 = currentMatches;
				newReq3 = tmpReqs;
				newInventory3 = tmpInventory3;
			}
		}

		inventoryItems = newInventory3;
		items3 = newReq3;

		for (ItemRequirement requirement : items3)
		{
			boolean foundPreviousItem = false;
			for (int j = 0; j < allRequirements.size(); j++)
			{
				// If duplicate item, aggregate
				if (requirement.getId() == allRequirements.get(j).getId())
				{
					allRequirements.set(j, requirement.quantity(requirement.getQuantity() + allRequirements.get(j).getQuantity()));
					foundPreviousItem = true;
					break;
				}
			}
			if (!foundPreviousItem)
			{
				allRequirements.add(requirement);
			}
		}

		// Puzzle 4
		List<ItemRequirement> newReq4 = new ArrayList<>();
		List<Item> tmpInventory4;
		for (List<ItemRequirement> reqs : shapeValues4.get(answer3))
		{
			List<Integer> currentSlotIDs4 = new ArrayList<>(Arrays.asList(slot4, slot5, slot6));
			// Each duo
			tmpInventory4 = new ArrayList<>(inventoryItems);
			List<ItemRequirement> tmpReqs = new ArrayList<>(reqs);
			int currentMatches = 0;
			for (ItemRequirement req : reqs)
			{
				for (int i = 0; i < req.getAllIds().size(); i++)
				{
					int match = checkForItems(tmpInventory4, req.getAllIds().get(i));
					if (match != -1)
					{
						currentMatches++;
						if (tmpInventory4.get(match).getQuantity() > 1)
						{
							Item newItem = new Item(tmpInventory4.get(match).getId(), tmpInventory4.get(match).getQuantity() - 1);
							tmpInventory4.set(match, newItem);
						}
						else
						{
							tmpInventory4.remove(match);
						}

						if (currentSlotIDs4.contains(req.getAllIds().get(i)))
						{
							tmpReqs.remove(req);
							currentSlotIDs4.remove(req.getAllIds().get(i));
						}
						break;
					}
				}
			}
			// If have more of a piece, OR have same amount of pieces already but more already in the machine
			if (currentMatches > mostMatch4 || (currentMatches == mostMatch4 && newReq4.size() > tmpReqs.size()))
			{
				mostMatch4 = currentMatches;
				newReq4 = tmpReqs;
			}
		}

		items4 = newReq4;

		for (ItemRequirement newRequirement : items4)
		{
			boolean foundPreviousItem = false;
			for (int j = 0; j < allRequirements.size(); j++)
			{
				// If duplicate item, aggregate
				if (newRequirement.getId() == allRequirements.get(j).getId())
				{
					allRequirements.set(j, newRequirement.quantity(newRequirement.getQuantity() + allRequirements.get(j).getQuantity()));
					foundPreviousItem = true;
					break;
				}
			}
			if (!foundPreviousItem)
			{
				allRequirements.add(newRequirement);
			}
		}

		boolean hasAllDiscs = true;
		for (ItemRequirement requirement : allRequirements)
		{
			if (!requirement.check(client))
			{
				hasAllDiscs = false;
				break;
			}
		}
		if (!hasAllDiscs)
		{
			getPieces.emptyRequirements();
			getPieces.addItemRequirements(allRequirements);
			startUpStep(getPieces);
			return;
		}

		// If in interface and have all disks needed
		if (insertWidget != null && !insertWidget.isHidden())
		{
			for (ItemRequirement itemRequirement : items2)
			{
				if (itemRequirement.getAllIds().contains(heldDisc))
				{
					startUpStep(clickDiscHole2);
					return;
				}
			}
			for (ItemRequirement itemRequirement : items3)
			{
				if (itemRequirement.getAllIds().contains(heldDisc))
				{
					startUpStep(clickDiscHole3);
					return;
				}
			}
			for (ItemRequirement itemRequirement : items4)
			{
				if (itemRequirement.getAllIds().contains(heldDisc))
				{
					startUpStep(clickDiscHole4);
					return;
				}
			}
			// Not holding a disk to insert, should click a disk to put into hand
			if (currentInv != null)
			{
				List<WidgetDetails> ids = new ArrayList<>();
				if (!items2.isEmpty())
				{
					ids = getClickableItems(ids, items2.iterator().next().getAllIds());
				}
				else if (!items3.isEmpty())
				{
					ids = getClickableItems(ids, items3.iterator().next().getAllIds());
				}
				else if (!items4.isEmpty())
				{
					ids = getClickableItems(ids, items4.iterator().next().getAllIds());
				}
				insertDisc.setWidgetDetails(ids);
				insertDisc.emptyRequirements();
				insertDisc.addItemRequirements(allRequirements);
				startUpStep(insertDisc);
			}
			return;
		}
		solvePuzzle.setRequirements(Collections.singletonList(slot1Item));
		solvePuzzle.emptyRequirements();
		solvePuzzle.addItemRequirements(allRequirements);
		startUpStep(solvePuzzle);
	}

	public List<WidgetDetails> getClickableItems(List<WidgetDetails> ids, List<Integer> items)
	{
		for (int j = 0; j < currentInv.length; j++)
		{
			if (items.contains(currentInv[j].getId()))
			{
				ids.add(new WidgetDetails(InterfaceID.EYEGLO_SIDE, 0, j));
			}
		}
		return ids;
	}

	public int checkForItems(List<Item> items, int potentialMatch)
	{
		for (int i = 0; i < items.size(); i++)
		{
			if (items.get(i).getId() == potentialMatch)
			{
				return i;
			}
		}
		return -1;
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.EYEGLO_INV_SIDE)
		{
			ItemContainer container = event.getItemContainer();
			currentInv = container.getItems();
		}
	}

	public void setupShapes()
	{
		slot1Item = new ItemRequirement("For part 1 get:", -1, -1);
		slot2Item = new ItemRequirement("For part 2 get:", -1, -1);
		slot3Item = new ItemRequirement("For part 3 get:", -1, -1);

		shapes.put(ItemID.EYEGLO_RED_CIRCLE, new ItemRequirement("Red circle", ItemID.EYEGLO_RED_CIRCLE));
		shapes.put(ItemID.EYEGLO_ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.EYEGLO_ORANGE_CIRCLE));
		shapes.put(ItemID.EYEGLO_YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.EYEGLO_YELLOW_CIRCLE));
		shapes.put(ItemID.EYEGLO_GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.EYEGLO_GREEN_CIRCLE));
		shapes.put(ItemID.EYEGLO_BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.EYEGLO_BLUE_CIRCLE));
		shapes.put(ItemID.EYEGLO_INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.EYEGLO_INDIGO_CIRCLE));
		shapes.put(ItemID.EYEGLO_VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.EYEGLO_VIOLET_CIRCLE));

		shapes.put(ItemID.EYEGLO_RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.EYEGLO_RED_TRIANGLE));
		shapes.put(ItemID.EYEGLO_ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.EYEGLO_ORANGE_TRIANGLE));
		shapes.put(ItemID.EYEGLO_YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.EYEGLO_YELLOW_TRIANGLE));
		shapes.put(ItemID.EYEGLO_GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.EYEGLO_GREEN_TRIANGLE));
		shapes.put(ItemID.EYEGLO_BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.EYEGLO_BLUE_TRIANGLE));
		shapes.put(ItemID.EYEGLO_INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.EYEGLO_INDIGO_TRIANGLE));
		shapes.put(ItemID.EYEGLO_VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.EYEGLO_VIOLET_TRIANGLE));

		shapes.put(ItemID.EYEGLO_RED_SQUARE, new ItemRequirement("Red square", ItemID.EYEGLO_RED_SQUARE));
		shapes.put(ItemID.EYEGLO_ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.EYEGLO_ORANGE_SQUARE));
		shapes.put(ItemID.EYEGLO_YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.EYEGLO_YELLOW_SQUARE));
		shapes.put(ItemID.EYEGLO_GREEN_SQUARE, new ItemRequirement("Green square", ItemID.EYEGLO_GREEN_SQUARE));
		shapes.put(ItemID.EYEGLO_BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.EYEGLO_BLUE_SQUARE));
		shapes.put(ItemID.EYEGLO_INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.EYEGLO_INDIGO_SQUARE));
		shapes.put(ItemID.EYEGLO_VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.EYEGLO_VIOLET_SQUARE));

		shapes.put(ItemID.EYEGLO_RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.EYEGLO_RED_PENTAGON));
		shapes.put(ItemID.EYEGLO_ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.EYEGLO_ORANGE_PENTAGON));
		shapes.put(ItemID.EYEGLO_YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.EYEGLO_YELLOW_PENTAGON));
		shapes.put(ItemID.EYEGLO_GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.EYEGLO_GREEN_PENTAGON));
		shapes.put(ItemID.EYEGLO_BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.EYEGLO_BLUE_PENTAGON));
		shapes.put(ItemID.EYEGLO_INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.EYEGLO_INDIGO_PENTAGON));
		shapes.put(ItemID.EYEGLO_VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.EYEGLO_VIOLET_PENTAGON));

		yellowCircleRedTri = new ItemRequirement("Yellow circle/red triangle", ItemID.EYEGLO_RED_TRIANGLE);
		yellowCircleRedTri.addAlternates(ItemID.EYEGLO_YELLOW_CIRCLE);
		greenCircleRedSquare = new ItemRequirement("Green circle/red square", ItemID.EYEGLO_GREEN_CIRCLE);
		greenCircleRedSquare.addAlternates(ItemID.EYEGLO_RED_SQUARE);
		blueCircleRedPentagon = new ItemRequirement("Blue circle/red pentagon", ItemID.EYEGLO_BLUE_CIRCLE);
		blueCircleRedPentagon.addAlternates(ItemID.EYEGLO_RED_PENTAGON);
		indigoCircleOrangeTriangle = new ItemRequirement("Indigo circle/orange triangle", ItemID.EYEGLO_INDIGO_CIRCLE);
		indigoCircleOrangeTriangle.addAlternates(ItemID.EYEGLO_ORANGE_TRIANGLE);
		yellowSquareGreenTriangle = new ItemRequirement("Yellow square/green triangle", ItemID.EYEGLO_YELLOW_SQUARE);
		yellowSquareGreenTriangle.addAlternates(ItemID.EYEGLO_GREEN_TRIANGLE);
		yellowPentagonBlueTriangle = new ItemRequirement("Yellow pentagon/blue triangle", ItemID.EYEGLO_YELLOW_PENTAGON);
		yellowPentagonBlueTriangle.addAlternates(ItemID.EYEGLO_BLUE_TRIANGLE);
		blueSquareGreenPentagon = new ItemRequirement("Blue square/green pentagon", ItemID.EYEGLO_BLUE_SQUARE);
		blueSquareGreenPentagon.addAlternates(ItemID.EYEGLO_GREEN_PENTAGON);

		shapeValues.put(1, shapes.get(ItemID.EYEGLO_RED_CIRCLE));
		shapeValues.put(2, shapes.get(ItemID.EYEGLO_ORANGE_CIRCLE));
		shapeValues.put(3, yellowCircleRedTri);
		shapeValues.put(4, greenCircleRedSquare);
		shapeValues.put(5, blueCircleRedPentagon);
		shapeValues.put(6, indigoCircleOrangeTriangle);
		shapeValues.put(7, shapes.get(ItemID.EYEGLO_VIOLET_CIRCLE));
		shapeValues.put(8, shapes.get(ItemID.EYEGLO_ORANGE_SQUARE));
		shapeValues.put(9, shapes.get(ItemID.EYEGLO_YELLOW_TRIANGLE));
		shapeValues.put(10, shapes.get(ItemID.EYEGLO_ORANGE_PENTAGON));
		shapeValues.put(12, yellowSquareGreenTriangle);
		shapeValues.put(15, yellowPentagonBlueTriangle);
		shapeValues.put(16, shapes.get(ItemID.EYEGLO_GREEN_SQUARE));
		shapeValues.put(18, shapes.get(ItemID.EYEGLO_INDIGO_TRIANGLE));
		shapeValues.put(20, blueSquareGreenPentagon);
		shapeValues.put(21, shapes.get(ItemID.EYEGLO_VIOLET_TRIANGLE));
		shapeValues.put(24, shapes.get(ItemID.EYEGLO_INDIGO_SQUARE));
		shapeValues.put(25, shapes.get(ItemID.EYEGLO_BLUE_PENTAGON));
		shapeValues.put(28, shapes.get(ItemID.EYEGLO_VIOLET_SQUARE));
		shapeValues.put(30, shapes.get(ItemID.EYEGLO_INDIGO_PENTAGON));
		shapeValues.put(35, shapes.get(ItemID.EYEGLO_VIOLET_PENTAGON));

		for (int i = 0; i < 35; i++)
		{
			ItemRequirement shape1 = shapeValues.get(i);
			for (int j = 0; j < 35; j++)
			{
				ItemRequirement shape2 = shapeValues.get(j);

				if (shape1 == null || shape2 == null)
				{
					continue;
				}
				shapeValues3.computeIfAbsent(i + j, sv2 -> new ArrayList<>());
				shapeValues3.get(i + j).add(Arrays.asList(shape1, shape2));

				for (int k = 0; k < 35; k++)
				{
					ItemRequirement shape3 = shapeValues.get(k);
					if (shape3 == null)
					{
						continue;
					}

					shapeValues4.computeIfAbsent(i + j + k, sv3 -> new ArrayList<>());
					shapeValues4.get(i + j + k).add(Arrays.asList(shape1, shape2, shape3));
				}
			}
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(solvePuzzle, getPieces, clickAnswer1, clickAnswer2, insertDisc, clickDiscHole, clickDiscHole2, clickDiscHole3, clickDiscHole4);
	}
}
