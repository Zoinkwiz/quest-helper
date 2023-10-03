/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;


@Slf4j
public class YewnocksPuzzle extends DetailedOwnerStep
{
	private static final int STOREROOM_REGION = 11074;
	private final int PUZZLE_SELECTED_DISC_VARP_ID = 856;
	private final int PUZZLE1_INSERTED_DISC_VARP_ID = 3994;
	private final int PUZZLE2_UPPER_INSERTED_DISC_VARP_ID = 3995;
	private final int PUZZLE2_LOWER_INSERTED_DISC_VARP_ID = 3996;
	private final int PUZZLE1_LEFT_VARP_ID = 3997;
	private final int PUZZLE1_RIGHT_VARP_ID = 3998;
	private final int PUZZLE2_VARP_ID = 3999;
	/**
	 * ItemID to ItemRequirement map
	 */
	private final HashMap<Integer, ItemRequirement> discs = new HashMap<>();
	/**
	 * Value to ItemRequirement map
	 */
	private final HashMap<Integer, ItemRequirement> valueToRequirement = new HashMap<>();
	/**
	 * ItemID to Value map
	 */
	private final HashMap<Integer, Integer> discToValue = new HashMap<>();
	/**
	 * Value to list of possible requirements using exactly 2 different
	 */
	private final HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement = new HashMap<>();
	/**
	 * Given a Value, what single disc can be used to exchange for it
	 */
	private final HashMap<Integer, List<List<Integer>>> valuePossibleExchanges = new HashMap<>();
	private final HashMap<Integer, HashSet<Integer>> valuePossibleSingleDiscExchanges = new HashMap<>();
	private final HashMap<Integer, List<ItemRequirement>> valuePossibleSingleDiscExchangesRequirements = new HashMap<>();
	private final Solution solution = new Solution();
	private final ThePathOfGlouphrie pog;
	private ObjectStep clickMachine;
	private ObjectStep clickMachineOnce;
	private int puzzle1LeftItemID = -1;
	private int puzzle1RightItemID = -1;
	private int puzzle2ItemID = -1;
	private WidgetPresenceRequirement widgetOpen;
	private WidgetPresenceRequirement exchangerWidgetOpen;
	private ItemStep selectDisc;
	private ItemStep exchangeDisc;
	private ObjectStep getMoreDiscs;
	private ObjectStep useExchanger;

	public YewnocksPuzzle(ThePathOfGlouphrie pog)
	{
		super(pog, "Operate Yewnock's machine & solve the puzzle. All items left on the ground are lost.");

		this.pog = pog;

		loadDiscs(discs);
		loadValueToRequirement(discs, valueToRequirement);
		loadDiscToValue(discToValue);
		loadValueToDoubleDiscRequirement(valueToRequirement, valueToDoubleDiscRequirement);
		loadValuePossibleExchanges(discToValue, valueToRequirement, valuePossibleExchanges, valuePossibleSingleDiscExchanges, valuePossibleSingleDiscExchangesRequirements);
	}

	static public void loadValuePossibleExchanges(final HashMap<Integer, Integer> discToValue, final HashMap<Integer, ItemRequirement> valueToRequirement,
												  HashMap<Integer, List<List<Integer>>> valuePossibleExchanges, HashMap<Integer, HashSet<Integer>> valuePossibleSingleDiscExchanges, HashMap<Integer, List<ItemRequirement>> valuePossibleSingleDiscExchangesRequirements)
	{
		var possibleNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15, 16, 18, 20, 21, 24, 25, 28, 30, 35};

		for (var valueEntry : valueToRequirement.entrySet())
		{
			var value = valueEntry.getKey();
			var subsetSums = SubsetSum.findSubsetsWithSum(possibleNumbers, value);
			for (var subsetSum : subsetSums)
			{
				valuePossibleExchanges.computeIfAbsent(value, sv2 -> new ArrayList<>());
				valuePossibleExchanges.get(value).add(subsetSum);
			}
		}

		for (var entry : valuePossibleExchanges.entrySet())
		{
			for (var subset : entry.getValue())
			{
				for (var subsetNumber : subset)
				{
					valuePossibleSingleDiscExchanges.computeIfAbsent(subsetNumber, sv2 -> new HashSet<>());
					valuePossibleSingleDiscExchanges.get(subsetNumber).add(entry.getKey());
				}
			}
		}

		for (var entry : valuePossibleSingleDiscExchanges.entrySet())
		{
			for (var itemID : entry.getValue())
			{
				valuePossibleSingleDiscExchangesRequirements.computeIfAbsent(entry.getKey(), sv2 -> new ArrayList<>());
				valuePossibleSingleDiscExchangesRequirements.get(entry.getKey()).add(valueToRequirement.get(itemID));
			}
		}
	}

	static public void loadValueToDoubleDiscRequirement(final HashMap<Integer, ItemRequirement> valueToRequirement, HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement)
	{
		for (int i = 0; i < 35; i++)
		{
			var shape1 = valueToRequirement.get(i);
			for (int j = 0; j < 35; j++)
			{
				var shape2 = valueToRequirement.get(j);

				if (shape1 == null || shape2 == null)
				{
					continue;
				}
				valueToDoubleDiscRequirement.computeIfAbsent(i + j, sv2 -> new ArrayList<>());
				if (shape1.getId() == shape2.getId())
				{
					valueToDoubleDiscRequirement.get(i + j).add(new ItemRequirements(shape1.quantity(2)));
				}
				else
				{
					valueToDoubleDiscRequirement.get(i + j).add(new ItemRequirements(LogicType.AND, shape1, shape2));
				}
			}
		}
	}

	public static void loadDiscs(HashMap<Integer, ItemRequirement> discs)
	{
		discs.put(ItemID.RED_CIRCLE, new ItemRequirement("Red circle", ItemID.RED_CIRCLE).highlighted());
		discs.put(ItemID.ORANGE_CIRCLE, new ItemRequirement("Orange circle", ItemID.ORANGE_CIRCLE).highlighted());
		discs.put(ItemID.YELLOW_CIRCLE, new ItemRequirement("Yellow circle", ItemID.YELLOW_CIRCLE).highlighted());
		discs.put(ItemID.GREEN_CIRCLE, new ItemRequirement("Green circle", ItemID.GREEN_CIRCLE).highlighted());
		discs.put(ItemID.BLUE_CIRCLE, new ItemRequirement("Blue circle", ItemID.BLUE_CIRCLE).highlighted());
		discs.put(ItemID.INDIGO_CIRCLE, new ItemRequirement("Indigo circle", ItemID.INDIGO_CIRCLE).highlighted());
		discs.put(ItemID.VIOLET_CIRCLE, new ItemRequirement("Violet circle", ItemID.VIOLET_CIRCLE).highlighted());
		discs.put(ItemID.RED_TRIANGLE, new ItemRequirement("Red triangle", ItemID.RED_TRIANGLE).highlighted());
		discs.put(ItemID.ORANGE_TRIANGLE, new ItemRequirement("Orange triangle", ItemID.ORANGE_TRIANGLE).highlighted());
		discs.put(ItemID.YELLOW_TRIANGLE, new ItemRequirement("Yellow triangle", ItemID.YELLOW_TRIANGLE).highlighted());
		discs.put(ItemID.GREEN_TRIANGLE, new ItemRequirement("Green triangle", ItemID.GREEN_TRIANGLE).highlighted());
		discs.put(ItemID.BLUE_TRIANGLE, new ItemRequirement("Blue triangle", ItemID.BLUE_TRIANGLE).highlighted());
		discs.put(ItemID.INDIGO_TRIANGLE, new ItemRequirement("Indigo triangle", ItemID.INDIGO_TRIANGLE).highlighted());
		discs.put(ItemID.VIOLET_TRIANGLE, new ItemRequirement("Violet triangle", ItemID.VIOLET_TRIANGLE).highlighted());
		discs.put(ItemID.RED_SQUARE, new ItemRequirement("Red square", ItemID.RED_SQUARE).highlighted());
		discs.put(ItemID.ORANGE_SQUARE, new ItemRequirement("Orange square", ItemID.ORANGE_SQUARE).highlighted());
		discs.put(ItemID.YELLOW_SQUARE, new ItemRequirement("Yellow square", ItemID.YELLOW_SQUARE).highlighted());
		discs.put(ItemID.GREEN_SQUARE, new ItemRequirement("Green square", ItemID.GREEN_SQUARE).highlighted());
		discs.put(ItemID.BLUE_SQUARE, new ItemRequirement("Blue square", ItemID.BLUE_SQUARE).highlighted());
		discs.put(ItemID.INDIGO_SQUARE, new ItemRequirement("Indigo square", ItemID.INDIGO_SQUARE).highlighted());
		discs.put(ItemID.VIOLET_SQUARE, new ItemRequirement("Violet square", ItemID.VIOLET_SQUARE).highlighted());
		discs.put(ItemID.RED_PENTAGON, new ItemRequirement("Red pentagon", ItemID.RED_PENTAGON).highlighted());
		discs.put(ItemID.ORANGE_PENTAGON, new ItemRequirement("Orange pentagon", ItemID.ORANGE_PENTAGON).highlighted());
		discs.put(ItemID.YELLOW_PENTAGON, new ItemRequirement("Yellow pentagon", ItemID.YELLOW_PENTAGON).highlighted());
		discs.put(ItemID.GREEN_PENTAGON, new ItemRequirement("Green pentagon", ItemID.GREEN_PENTAGON).highlighted());
		discs.put(ItemID.BLUE_PENTAGON, new ItemRequirement("Blue pentagon", ItemID.BLUE_PENTAGON).highlighted());
		discs.put(ItemID.INDIGO_PENTAGON, new ItemRequirement("Indigo pentagon", ItemID.INDIGO_PENTAGON).highlighted());
		discs.put(ItemID.VIOLET_PENTAGON, new ItemRequirement("Violet pentagon", ItemID.VIOLET_PENTAGON).highlighted());
	}

	public static void loadValueToRequirement(final HashMap<Integer, ItemRequirement> discs, HashMap<Integer, ItemRequirement> valueToRequirement)
	{
		var yellowCircleRedTri = new ItemRequirement("Yellow circle/red triangle", ItemID.RED_TRIANGLE).highlighted();
		yellowCircleRedTri.addAlternates(ItemID.YELLOW_CIRCLE);
		var greenCircleRedSquare = new ItemRequirement("Green circle/red square", ItemID.GREEN_CIRCLE).highlighted();
		greenCircleRedSquare.addAlternates(ItemID.RED_SQUARE);
		var blueCircleRedPentagon = new ItemRequirement("Blue circle/red pentagon", ItemID.BLUE_CIRCLE).highlighted();
		blueCircleRedPentagon.addAlternates(ItemID.RED_PENTAGON);
		var indigoCircleOrangeTriangle = new ItemRequirement("Indigo circle/orange triangle", ItemID.INDIGO_CIRCLE).highlighted();
		indigoCircleOrangeTriangle.addAlternates(ItemID.ORANGE_TRIANGLE);
		var yellowSquareGreenTriangle = new ItemRequirement("Yellow square/green triangle", ItemID.YELLOW_SQUARE).highlighted();
		yellowSquareGreenTriangle.addAlternates(ItemID.GREEN_TRIANGLE);
		var yellowPentagonBlueTriangle = new ItemRequirement("Yellow pentagon/blue triangle", ItemID.YELLOW_PENTAGON).highlighted();
		yellowPentagonBlueTriangle.addAlternates(ItemID.BLUE_TRIANGLE);
		var blueSquareGreenPentagon = new ItemRequirement("Blue square/green pentagon", ItemID.BLUE_SQUARE).highlighted();
		blueSquareGreenPentagon.addAlternates(ItemID.GREEN_PENTAGON);

		valueToRequirement.put(1, discs.get(ItemID.RED_CIRCLE));
		valueToRequirement.put(2, discs.get(ItemID.ORANGE_CIRCLE));
		valueToRequirement.put(3, yellowCircleRedTri);
		valueToRequirement.put(4, greenCircleRedSquare);
		valueToRequirement.put(5, blueCircleRedPentagon);
		valueToRequirement.put(6, indigoCircleOrangeTriangle);
		valueToRequirement.put(7, discs.get(ItemID.VIOLET_CIRCLE));
		valueToRequirement.put(8, discs.get(ItemID.ORANGE_SQUARE));
		valueToRequirement.put(9, discs.get(ItemID.YELLOW_TRIANGLE));
		valueToRequirement.put(10, discs.get(ItemID.ORANGE_PENTAGON));
		valueToRequirement.put(12, yellowSquareGreenTriangle);
		valueToRequirement.put(15, yellowPentagonBlueTriangle);
		valueToRequirement.put(16, discs.get(ItemID.GREEN_SQUARE));
		valueToRequirement.put(18, discs.get(ItemID.INDIGO_TRIANGLE));
		valueToRequirement.put(20, blueSquareGreenPentagon);
		valueToRequirement.put(21, discs.get(ItemID.VIOLET_TRIANGLE));
		valueToRequirement.put(24, discs.get(ItemID.INDIGO_SQUARE));
		valueToRequirement.put(25, discs.get(ItemID.BLUE_PENTAGON));
		valueToRequirement.put(28, discs.get(ItemID.VIOLET_SQUARE));
		valueToRequirement.put(30, discs.get(ItemID.INDIGO_PENTAGON));
		valueToRequirement.put(35, discs.get(ItemID.VIOLET_PENTAGON));
	}

	public static void loadDiscToValue(HashMap<Integer, Integer> discToValue)
	{
		discToValue.put(ItemID.RED_CIRCLE, 1);
		discToValue.put(ItemID.RED_TRIANGLE, 3);
		discToValue.put(ItemID.RED_SQUARE, 4);
		discToValue.put(ItemID.RED_PENTAGON, 5);
		discToValue.put(ItemID.ORANGE_CIRCLE, 2);
		discToValue.put(ItemID.ORANGE_TRIANGLE, 6);
		discToValue.put(ItemID.ORANGE_SQUARE, 8);
		discToValue.put(ItemID.ORANGE_PENTAGON, 10);
		discToValue.put(ItemID.YELLOW_CIRCLE, 3);
		discToValue.put(ItemID.YELLOW_TRIANGLE, 9);
		discToValue.put(ItemID.YELLOW_SQUARE, 12);
		discToValue.put(ItemID.YELLOW_PENTAGON, 15);
		discToValue.put(ItemID.GREEN_CIRCLE, 4);
		discToValue.put(ItemID.GREEN_TRIANGLE, 12);
		discToValue.put(ItemID.GREEN_SQUARE, 16);
		discToValue.put(ItemID.GREEN_PENTAGON, 20);
		discToValue.put(ItemID.BLUE_CIRCLE, 5);
		discToValue.put(ItemID.BLUE_TRIANGLE, 15);
		discToValue.put(ItemID.BLUE_SQUARE, 20);
		discToValue.put(ItemID.BLUE_PENTAGON, 25);
		discToValue.put(ItemID.INDIGO_CIRCLE, 6);
		discToValue.put(ItemID.INDIGO_TRIANGLE, 18);
		discToValue.put(ItemID.INDIGO_SQUARE, 24);
		discToValue.put(ItemID.INDIGO_PENTAGON, 30);
		discToValue.put(ItemID.VIOLET_CIRCLE, 7);
		discToValue.put(ItemID.VIOLET_TRIANGLE, 21);
		discToValue.put(ItemID.VIOLET_SQUARE, 28);
		discToValue.put(ItemID.VIOLET_PENTAGON, 35);
	}

	public static WorldPoint regionPoint(int regionX, int regionY)
	{
		return WorldPoint.fromRegion(STOREROOM_REGION, regionX, regionY, 0);
	}

	@Override
	public void startUp()
	{
		puzzle1LeftItemID = client.getVarpValue(PUZZLE1_LEFT_VARP_ID);
		puzzle1RightItemID = client.getVarpValue(PUZZLE1_RIGHT_VARP_ID);
		puzzle2ItemID = client.getVarpValue(PUZZLE2_VARP_ID);

		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		getMoreDiscs = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49617, regionPoint(34, 31), "Get more discs from the chests outside. You can drop discs before you get more. You can also use the exchanger next to Yewnock's machine.", true);
		useExchanger = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_EXCHANGER, regionPoint(22, 31), "A solution has been calculated, exit the machine interface & click Yewnock's exchanger.");
		clickMachine = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_MACHINE_49662, regionPoint(22, 32), "Operate Yewnock's machine. If you run out of discs you can get new ones from the regular chests in the previous room.");
		clickMachineOnce = new ObjectStep(getQuestHelper(), ObjectID.YEWNOCKS_MACHINE_49662, regionPoint(22, 32), "Operate Yewnock's machine to calculate a solution.");
		selectDisc = new DiscInsertionStep(getQuestHelper(), "Select the highlighted disc in your inventory.");
		exchangeDisc = new DiscInsertionStep(getQuestHelper(), "Select one of the highlighted discs in your inventory.");

		widgetOpen = new WidgetPresenceRequirement(848, 0);
		exchangerWidgetOpen = new WidgetPresenceRequirement(849, 0);
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (varbitChanged.getVarbitId() == -1)
		{
			if (varbitChanged.getVarpId() == PUZZLE1_LEFT_VARP_ID)
			{
				puzzle1LeftItemID = varbitChanged.getValue();
			}
			else if (varbitChanged.getVarpId() == PUZZLE1_RIGHT_VARP_ID)
			{
				puzzle1RightItemID = varbitChanged.getValue();
			}
			else if (varbitChanged.getVarpId() == PUZZLE2_VARP_ID)
			{
				puzzle2ItemID = varbitChanged.getValue();
			}
			else
			{
				// irrelevant value changed
				return;
			}

			updateSteps();
		}
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		// TODO: don't update steps, just re-do the calculation & if its state has changed, then update steps
		// TODO: optimize
		updateSteps();
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		// TODO: optimize
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		puzzle1LeftItemID = -1;
		puzzle1RightItemID = -1;
		puzzle2ItemID = -1;
		solution.reset();
	}

	@Nullable
	private Integer getWidgetItemId(int groupId, int childId)
	{
		var widget = client.getWidget(groupId, childId);
		if (widget == null)
		{
			return null;
		}

		var itemId = widget.getItemId();
		if (itemId == -1)
		{
			return null;
		}
		return itemId;
	}

	protected void updateSteps()
	{
		if (!widgetOpen.check(client))
		{
			solution.reset();

			ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
			if (itemContainer != null)
			{
				int count = 0;

				for (var item : itemContainer.getItems())
				{
					var shape = discs.get(item.getId());
					if (shape != null)
					{
						count += item.getQuantity();
					}
				}

				if (count < 3)
				{
					startUpStep(getMoreDiscs);
					return;
				}
			}

			if (puzzle1LeftItemID <= 0 || puzzle1RightItemID <= 0 || puzzle2ItemID <= 0)
			{
				startUpStep(clickMachineOnce);
				return;
			}

			// startUpStep(clickMachine);

			// return;
		}

		if (!solution.isGood())
		{
			ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
			var items = new ArrayList<Item>();
			if (itemContainer != null)
			{
				for (var item : itemContainer.getItems())
				{
					var shape = discs.get(item.getId());
					if (shape != null)
					{
						items.add(item);
					}
				}
			}

			if (puzzle1LeftItemID <= 0 || puzzle1RightItemID <= 0 || puzzle2ItemID <= 0)
			{
				// Couldn't find the solution required, this shouldn't be the case when the widget is open
				return;
			}

			var puzzle2SolutionValue = discToValue.get(puzzle2ItemID);
			if (puzzle2SolutionValue == null)
			{
				// The item ID found in the puzzle2 box was invalid, not sure how to recover
				return;
			}

			var puzzle1SolutionValue1 = discToValue.get(puzzle1LeftItemID);
			var puzzle1SolutionValue2 = discToValue.get(puzzle1RightItemID);
			if (puzzle1SolutionValue1 == null || puzzle1SolutionValue2 == null)
			{
				// One of the item IDs found in the puzzle1 boxes were invalid, not sure how to recover
				return;
			}

			var puzzle1SolutionValue = puzzle1SolutionValue1 + puzzle1SolutionValue2;
			// Try to figure out a solution
			solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue,
				valueToRequirement, valueToDoubleDiscRequirement, discToValue, valuePossibleSingleDiscExchangesRequirements);
		}

		if (!solution.isGood())
		{
			List<? extends Requirement> a = solution.puzzleNeeds;
			// getMoreDiscs.setRequirements(solution.puzzleNeeds.stream().map(Requirement::from).collect(Collectors.toList()));
			getMoreDiscs.setRequirements(solution.puzzleNeeds);

			if (exchangerWidgetOpen.check(client))
			{
				exchangeDisc.clearWidgetHighlights();
				// Exchanger widget is open
				var exchangeResultTL = getWidgetItemId(849, 21);
				var exchangeResultTR = getWidgetItemId(849, 24);
				var exchangeResultBL = getWidgetItemId(849, 27);
				var exchangeResultBR = getWidgetItemId(849, 30);
				boolean foundGoodExchange = false;
				for (var puzzleNeed : solution.puzzleNeeds)
				{
					if (exchangeResultTL != null && puzzleNeed.getId() == exchangeResultTL)
					{
						exchangeDisc.addWidgetHighlight(849, 21);
						foundGoodExchange = true;
					}
					if (exchangeResultTR != null && puzzleNeed.getId() == exchangeResultTR)
					{
						exchangeDisc.addWidgetHighlight(849, 24);
						foundGoodExchange = true;
					}
					if (exchangeResultBL != null && puzzleNeed.getId() == exchangeResultBL)
					{
						exchangeDisc.addWidgetHighlight(849, 27);
						foundGoodExchange = true;
					}
					if (exchangeResultBR != null && puzzleNeed.getId() == exchangeResultBR)
					{
						exchangeDisc.addWidgetHighlight(849, 30);
						foundGoodExchange = true;
					}
				}

				if (foundGoodExchange)
				{
					// highlight confirm button
					exchangeDisc.addWidgetHighlight(849, 36);
				}
				exchangeDisc.setRequirements(solution.toExchange);
				startUpStep(exchangeDisc);
			}
			else
			{
				if (solution.toExchange.isEmpty())
				{
					// getMoreDiscs.setText("Get more discs at the marked chests. You need to drop all your discs first before opening the chest.");
					startUpStep(getMoreDiscs);
				}
				else
				{
					startUpStep(useExchanger);
				}
			}
		}
		else
		{
			if (!widgetOpen.check(client))
			{
				startUpStep(clickMachine);
				return;
			}
			if (!solution.puzzle1Requirement.getAllIds().contains(client.getVarpValue(PUZZLE1_INSERTED_DISC_VARP_ID)))
			{
				// Solve puzzle 1 first
				selectDisc.setText("Insert the highlighted disc into the highlighted slot");
				selectDisc.setRequirements(List.of(solution.puzzle1Requirement));
				selectDisc.clearWidgetHighlights();
				// FOR PUZZLE 1 SOLUTION
				selectDisc.addWidgetHighlight(848, 19);
			}
			else if (!solution.puzzle2UpperRequirement.getAllIds().contains(client.getVarpValue(PUZZLE2_UPPER_INSERTED_DISC_VARP_ID)))
			{
				selectDisc.setText("Insert the highlighted disc into the highlighted slot");
				selectDisc.setRequirements(List.of(solution.puzzle2UpperRequirement));
				selectDisc.clearWidgetHighlights();
				// FOR PUZZLE 2 UPPER SOLUTION
				selectDisc.addWidgetHighlight(848, 20);
				// Solve puzzle 2 upper
			}
			else if (!solution.puzzle2LowerRequirement.getAllIds().contains(client.getVarpValue(PUZZLE2_LOWER_INSERTED_DISC_VARP_ID)))
			{
				selectDisc.setText("Insert the highlighted disc into the highlighted slot");
				selectDisc.setRequirements(List.of(solution.puzzle2LowerRequirement));
				selectDisc.clearWidgetHighlights();
				// FOR PUZZLE 2 LOWER SOLUTION
				selectDisc.addWidgetHighlight(848, 21);
			}
			else
			{
				// CLICK CONFIRM
				selectDisc.setText("Click the submit button");
				selectDisc.setRequirements(List.of());
				selectDisc.clearWidgetHighlights();
				selectDisc.addWidgetHighlight(848, 12);
			}
			startUpStep(selectDisc);
		}
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(clickMachine, clickMachineOnce, selectDisc, useExchanger, exchangeDisc, getMoreDiscs);
	}

	public static class SubsetSum
	{
		public static List<List<Integer>> findSubsetsWithSum(int[] numbers, int targetSum)
		{
			List<List<Integer>> allSubsets = new ArrayList<>();
			List<Integer> currentSubset = new ArrayList<>();
			boolean[] used = new boolean[numbers.length]; // Keep track of used numbers
			findSubsets(numbers, targetSum, 0, currentSubset, allSubsets, 3, used);
			return allSubsets;
		}

		private static void findSubsets(int[] numbers, int targetSum, int currentIndex, List<Integer> currentSubset, List<List<Integer>> allSubsets, int maxValues, boolean[] used)
		{
			if (targetSum == 0 && currentSubset.size() <= maxValues)
			{
				allSubsets.add(new ArrayList<>(currentSubset));
				return;
			}
			if (currentIndex >= numbers.length || targetSum < 0 || currentSubset.size() >= maxValues)
			{
				return;
			}

			// Include the current number in the subset if it's not already used
			if (!used[currentIndex])
			{
				currentSubset.add(numbers[currentIndex]);
				used[currentIndex] = true; // Mark the number as used
				findSubsets(numbers, targetSum - numbers[currentIndex], currentIndex, currentSubset, allSubsets, maxValues, used);
				used[currentIndex] = false; // Unmark the number to backtrack
				currentSubset.remove(currentSubset.size() - 1);
			}

			// Exclude the current number from the subset
			findSubsets(numbers, targetSum, currentIndex + 1, currentSubset, allSubsets, maxValues, used);
		}
	}
}
