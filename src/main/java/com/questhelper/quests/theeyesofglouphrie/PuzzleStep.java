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
package com.questhelper.quests.theeyesofglouphrie;

import com.google.inject.Inject;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetDetails;
import com.questhelper.steps.WidgetStep;
import com.questhelper.steps.OwnerStep;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class PuzzleStep extends QuestStep implements OwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	protected QuestStep currentStep;

	HashMap<Integer, ItemRequirement> shapeValues = new HashMap<>();

	HashMap<Integer, List<List<ItemRequirement>>> shapeValues3 = new HashMap<>();
	HashMap<Integer, List<List<ItemRequirement>>> shapeValues4 = new HashMap<>();

	ItemRequirement slot1Item, slot2Item, slot3Item, yellowCircleRedTri, greenCircleRedSquare, blueCircleRedPentagon, blueSquareGreenPentagon, indigoCircleOrangeTriangle, yellowSquareGreenTriangle, yellowPentagonBlueTriangle;

	HashMap<Integer, ItemRequirement> shapes = new HashMap<>();

	WidgetStep clickAnswer1, insertDisc, clickDiscHole, clickDiscHole2, clickDiscHole3, clickDiscHole4, clickAnswer2;

	ObjectStep solvePuzzle, getPieces;

	int answer1, answer2, answer3, answer4;

	int items1;

	List<ItemRequirement> items2 = new ArrayList<>();
	List<ItemRequirement> items3 = new ArrayList<>();
	List<ItemRequirement> items4 = new ArrayList<>();

	Item[] lastInv;

	Item[] currentInv;

	int mostMatch2 = 0;
	int newMostMatch3 = -1;
	int mostMatch4 = -1;

	public PuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Insert and swap discs to make the sum indicated on the machine");
		solvePuzzle = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17282, new WorldPoint(2390, 9826, 0), "Put in the correct pieces.");
		getPieces = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17283, new WorldPoint(2391, 9826, 0), "Swap in" +
			" your pieces for the indicated pieces. You can also drop the discs then talk to Brimstail for more " +
			"tokens.");
		clickAnswer1 = new WidgetStep(getQuestHelper(), "Click the submit button.", 445, 36);
		clickAnswer2 = new WidgetStep(getQuestHelper(), "Click the submit button.", 189, 39);
		insertDisc = new WidgetStep(getQuestHelper(), "Insert the correct discs.", 449, 0);
		clickDiscHole = new WidgetStep(getQuestHelper(), "Insert the disc.", 445, 31);
		clickDiscHole2 = new WidgetStep(getQuestHelper(), "Insert the disc.", 189, 24);
		clickDiscHole3 = new WidgetStep(getQuestHelper(), "Insert the disc.", 189, 25);
		clickDiscHole4 = new WidgetStep(getQuestHelper(), "Insert the disc.", 189, 26);
		setupShapes();
	}

	@Override
	public void startUp()
	{
		super.startUp();
		answer1 = client.getVarbitValue(2510); // 1 input
		answer2 = client.getVarbitValue(2511); // 2 input
		answer3 = client.getVarbitValue(2512); // 3 input
		answer4 = client.getVarbitValue(2513); // 4 input

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

	public void updateSteps()
	{
		if (client.getVarbitValue(2502) == 2)
		{
			solvePuzzle2();
		}
		else
		{
			solvePuzzle1();
		}
	}

	public void solvePuzzle1()
	{
		int heldDisc = client.getVarpValue(856);
		Widget insertWidget = client.getWidget(447, 0);

		if (client.getVarbitValue(2539) == answer1)
		{
			startUpStep(clickAnswer1);
			return;
		}

		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (itemContainer == null)
		{
			return;
		}
		Item[] inventoryItemsArr = itemContainer.getItems();

		items1 = -1;
		List<Item> inventoryItems = Arrays.asList(inventoryItemsArr);

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

				ids = getClickableItems(ids, new ArrayList<>(items1));

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
		int heldDisc = client.getVarpValue(856);

		if (client.getVarbitValue(2540) == answer2 && client.getVarbitValue(2541) == answer3 && client.getVarbitValue(2542) == answer4)
		{
			startUpStep(clickAnswer2);
			return;
		}

		ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
		if (itemContainer == null)
		{
			return;
		}

		Item[] inventoryItemsArr = itemContainer.getItems();

		List<Item> inventoryItems = new ArrayList<>(Arrays.asList(inventoryItemsArr));

		List<ItemRequirement> allRequirements = new ArrayList<>();

		Widget insertWidget = client.getWidget(189, 0);

		int slot1 = client.getVarpValue(850);

		int slot2 = client.getVarpValue(851);
		int slot3 = client.getVarpValue(852);

		int slot4 = client.getVarpValue(853);
		int slot5 = client.getVarpValue(854);
		int slot6 = client.getVarpValue(855);

		mostMatch2 = 0;
		newMostMatch3 = -1;
		mostMatch4 = -1;

		lastInv = inventoryItemsArr;

		// Puzzle 2
		for (Integer id : shapeValues.get(answer2).getAllIds())
		{
			items2 = Collections.singletonList(shapeValues.get(answer2));
			int match = checkForItems(inventoryItems, id);
			if (match != -1)
			{
				mostMatch2++;

				inventoryItems.remove(match);
				if (inventoryItems.get(match).getQuantity() > 1)
				{
					Item newItem = new Item(inventoryItems.get(match).getId(), inventoryItems.get(match).getQuantity() - 1);
					inventoryItems.set(match, newItem);
				}
				else
				{
					inventoryItems.remove(match);
				}

				if (id == slot1)
				{
					items2 = new ArrayList<>();
				}
				break;
			}
		}

		allRequirements.addAll(items2);

		// Puzzle 3
		List<ItemRequirement> newReq3 = new ArrayList<>();
		List<Item> newInventory3 = new ArrayList<>(inventoryItems);
		List<Item> tmpInventory3;
		for (List<ItemRequirement> reqs : shapeValues3.get(answer3))
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
						if (currentSlotIDs3.contains(req.getAllIds().get(i)))
						{
							tmpReqs.remove(req);
							currentSlotIDs3.remove(req.getAllIds().get(i));
						}
						break;
					}
				}
			}
			// If have more of a piece, OR have same amount of pieces already but more already in the machine
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
		for (List<ItemRequirement> reqs : shapeValues4.get(answer4))
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
				if (itemRequirement.getId() == heldDisc)
				{
					startUpStep(clickDiscHole2);
					return;
				}
			}
			for (ItemRequirement itemRequirement : items3)
			{
				if (itemRequirement.getId() == heldDisc)
				{
					startUpStep(clickDiscHole3);
					return;
				}
			}
			for (ItemRequirement itemRequirement : items4)
			{
				if (itemRequirement.getId() == heldDisc)
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
				ids.add(new WidgetDetails(449, 0, j));
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
		if (event.getContainerId() == 440)
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

		shapes.put(ItemID.A_RED_CIRCLE, new ItemRequirement("Red circle", ItemID.A_RED_CIRCLE));
		shapes.put(ItemID.AN_ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.AN_ORANGE_CIRCLE));
		shapes.put(ItemID.A_YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.A_YELLOW_CIRCLE));
		shapes.put(ItemID.A_GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.A_GREEN_CIRCLE));
		shapes.put(ItemID.A_BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.A_BLUE_CIRCLE));
		shapes.put(ItemID.AN_INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.AN_INDIGO_CIRCLE));
		shapes.put(ItemID.A_VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.A_VIOLET_CIRCLE));

		shapes.put(ItemID.A_RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.A_RED_TRIANGLE));
		shapes.put(ItemID.AN_ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.AN_ORANGE_TRIANGLE));
		shapes.put(ItemID.A_YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.A_YELLOW_TRIANGLE));
		shapes.put(ItemID.A_GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.A_GREEN_TRIANGLE));
		shapes.put(ItemID.A_BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.A_BLUE_TRIANGLE));
		shapes.put(ItemID.AN_INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.AN_INDIGO_TRIANGLE));
		shapes.put(ItemID.A_VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.A_VIOLET_TRIANGLE));

		shapes.put(ItemID.A_RED_SQUARE, new ItemRequirement("Red square", ItemID.A_RED_SQUARE));
		shapes.put(ItemID.AN_ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.AN_ORANGE_SQUARE));
		shapes.put(ItemID.A_YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.A_YELLOW_SQUARE));
		shapes.put(ItemID.A_GREEN_SQUARE, new ItemRequirement("Green square", ItemID.A_GREEN_SQUARE));
		shapes.put(ItemID.A_BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.A_BLUE_SQUARE));
		shapes.put(ItemID.AN_INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.AN_INDIGO_SQUARE));
		shapes.put(ItemID.A_VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.A_VIOLET_SQUARE));

		shapes.put(ItemID.A_RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.A_RED_PENTAGON));
		shapes.put(ItemID.ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.ORANGE_PENTAGON));
		shapes.put(ItemID.A_YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.A_YELLOW_PENTAGON));
		shapes.put(ItemID.A_GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.A_GREEN_PENTAGON));
		shapes.put(ItemID.A_BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.A_BLUE_PENTAGON));
		shapes.put(ItemID.AN_INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.AN_INDIGO_PENTAGON));
		shapes.put(ItemID.A_VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.A_VIOLET_PENTAGON));

		yellowCircleRedTri = new ItemRequirement("Yellow circle/red triangle", ItemID.A_RED_TRIANGLE);
		yellowCircleRedTri.addAlternates(ItemID.A_YELLOW_CIRCLE);
		greenCircleRedSquare = new ItemRequirement("Green circle/red square", ItemID.A_GREEN_CIRCLE);
		greenCircleRedSquare.addAlternates(ItemID.A_RED_SQUARE);
		blueCircleRedPentagon = new ItemRequirement("Blue circle/red pentagon", ItemID.A_BLUE_CIRCLE);
		blueCircleRedPentagon.addAlternates(ItemID.A_RED_PENTAGON);
		indigoCircleOrangeTriangle = new ItemRequirement("Indigo circle/orange triangle", ItemID.AN_INDIGO_CIRCLE);
		indigoCircleOrangeTriangle.addAlternates(ItemID.AN_ORANGE_TRIANGLE);
		yellowSquareGreenTriangle = new ItemRequirement("Yellow square/green triangle", ItemID.A_YELLOW_SQUARE);
		yellowSquareGreenTriangle.addAlternates(ItemID.A_GREEN_TRIANGLE);
		yellowPentagonBlueTriangle = new ItemRequirement("Yellow pentagon/blue triangle", ItemID.A_YELLOW_PENTAGON);
		yellowPentagonBlueTriangle.addAlternates(ItemID.A_BLUE_TRIANGLE);
		blueSquareGreenPentagon = new ItemRequirement("Blue square/green pentagon", ItemID.A_BLUE_SQUARE);
		blueSquareGreenPentagon.addAlternates(ItemID.A_GREEN_PENTAGON);

		shapeValues.put(1, shapes.get(ItemID.A_RED_CIRCLE));
		shapeValues.put(2, shapes.get(ItemID.AN_ORANGE_CIRCLE));
		shapeValues.put(3, yellowCircleRedTri);
		shapeValues.put(4, greenCircleRedSquare);
		shapeValues.put(5, blueCircleRedPentagon);
		shapeValues.put(6, indigoCircleOrangeTriangle);
		shapeValues.put(7, shapes.get(ItemID.A_VIOLET_CIRCLE));
		shapeValues.put(8, shapes.get(ItemID.AN_ORANGE_SQUARE));
		shapeValues.put(9, shapes.get(ItemID.A_YELLOW_TRIANGLE));
		shapeValues.put(10, shapes.get(ItemID.ORANGE_PENTAGON));
		shapeValues.put(12, yellowSquareGreenTriangle);
		shapeValues.put(15, yellowPentagonBlueTriangle);
		shapeValues.put(16, shapes.get(ItemID.A_GREEN_SQUARE));
		shapeValues.put(18, shapes.get(ItemID.AN_INDIGO_TRIANGLE));
		shapeValues.put(20, blueSquareGreenPentagon);
		shapeValues.put(21, shapes.get(ItemID.A_VIOLET_TRIANGLE));
		shapeValues.put(24, shapes.get(ItemID.AN_INDIGO_SQUARE));
		shapeValues.put(25, shapes.get(ItemID.A_BLUE_PENTAGON));
		shapeValues.put(28, shapes.get(ItemID.A_VIOLET_SQUARE));
		shapeValues.put(30, shapes.get(ItemID.AN_INDIGO_PENTAGON));
		shapeValues.put(35, shapes.get(ItemID.A_VIOLET_PENTAGON));

		for (int i = 0; i < 35; i++)
		{
			for (int j = 0; j < 35; j++)
			{
				ItemRequirement shape1 = shapeValues.get(i);
				ItemRequirement shape2 = shapeValues.get(j);
				for (int k = 0; k < 35; k++)
				{
					ItemRequirement shape3 = shapeValues.get(k);
					if (shape1 == null || shape2 == null || shape3 == null)
					{
						continue;
					}

					shapeValues4.computeIfAbsent(i + j + k, sv3 -> new ArrayList<>());
					shapeValues4.get(i + j + k).add(Arrays.asList(shape1, shape2, shape3));
				}

				if (shape1 == null || shape2 == null)
				{
					continue;
				}
				shapeValues3.computeIfAbsent(i + j, sv2 -> new ArrayList<>());
				shapeValues3.get(i + j).add(Arrays.asList(shape1, shape2));
			}
		}
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
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, Requirement... requirements)
	{
		if (currentStep != null)
		{
			currentStep.makeOverlayHint(panelComponent, plugin, requirements);
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
		if (currentStep != null)
		{
			return currentStep.getActiveStep();
		}
		else
		{
			return this;
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(solvePuzzle, getPieces, clickAnswer1, clickAnswer2, insertDisc, clickDiscHole, clickDiscHole2, clickDiscHole3, clickDiscHole4);
	}
}
