package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.runelite.api.Client;
import net.runelite.api.Item;

public class Solution
{
	@Nonnull
	public List<ItemRequirement> puzzleNeeds = new ArrayList<>();

	/**
	 * Items that should be used in the exchanger to get an item we need
	 */
	@Nonnull
	public List<ItemRequirement> toExchange = new ArrayList<>();
	public ItemRequirement puzzle1Requirement;
	public ItemRequirement puzzle2UpperRequirement;
	public ItemRequirement puzzle2LowerRequirement;

	public void load(Client client, List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue,
					 final HashMap<Integer, ItemRequirement> valueToRequirement,
					 final HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement,
					 final HashMap<Integer, Integer> discToValue,
					 final HashMap<Integer, List<ItemRequirement>> valuePossibleSingleDiscExchangesRequirements
	)
	{
		this.reset();

		var puzzle1Requirement = valueToRequirement.get(puzzle1SolutionValue);
		if (puzzle1Requirement == null)
		{
			// No possible solution at all to this value, not sure how to proceed
			return;
		}

		puzzleNeeds.add(puzzle1Requirement);

		if (!puzzle1Requirement.check(client, false, items))
		{
			puzzleNeeds.clear();
			return;
		}

		this.puzzle1Requirement = puzzle1Requirement;

		var itemsAfterPuzzle1 = new ArrayList<Item>();

		for (var item : items)
		{
			if (item.getId() == this.puzzle1Requirement.getId())
			{
				if (item.getQuantity() > 1)
				{
					// This item was used in the puzzle 1 solution, but we had more than one of it, so it's ok :)
					var newItem = new Item(item.getId(), item.getQuantity() - 1);
					itemsAfterPuzzle1.add(newItem);
				}
			}
			else
			{
				itemsAfterPuzzle1.add(item);
			}
		}

		puzzleNeeds.clear();

		// solve puzzle 2
		var possiblePuzzle2Solutions = valueToDoubleDiscRequirement.get(puzzle2SolutionValue);
		ItemRequirements partialPuzzle2Solution = null;
		ItemRequirement findExchangeFor = null;
		ItemRequirement consumed = null;
		for (var possiblePuzzle2Solution : possiblePuzzle2Solutions)
		{
			if (possiblePuzzle2Solution.check(client, false, itemsAfterPuzzle1))
			{
				// Found a valid puzzle2 solution
				puzzle2UpperRequirement = possiblePuzzle2Solution.getItemRequirements().get(0);
				puzzle2LowerRequirement = possiblePuzzle2Solution.getItemRequirements().get(1);
				return;
			}

			// Let's check if we have one of these
			if (possiblePuzzle2Solution.getItemRequirements().get(0).check(client, false, itemsAfterPuzzle1))
			{
				// Found a partial solution
				partialPuzzle2Solution = possiblePuzzle2Solution;
				consumed = possiblePuzzle2Solution.getItemRequirements().get(0);
				findExchangeFor = possiblePuzzle2Solution.getItemRequirements().get(1);
				break;
			}

			if (possiblePuzzle2Solution.getItemRequirements().get(1).check(client, false, itemsAfterPuzzle1))
			{
				// Found a partial solution
				partialPuzzle2Solution = possiblePuzzle2Solution;
				consumed = possiblePuzzle2Solution.getItemRequirements().get(1);
				findExchangeFor = possiblePuzzle2Solution.getItemRequirements().get(0);
				break;
			}
		}


		if (partialPuzzle2Solution != null)
		{
			var itemsAfterPuzzle2 = new ArrayList<Item>();

			for (var item : itemsAfterPuzzle1)
			{
				if (consumed.getAllIds().contains(item.getId()))
				{
					if (item.getQuantity() > 1)
					{
						// This item was used in the puzzle 2 solution, but we had more than one of it, so it's ok :)
						itemsAfterPuzzle2.add(new Item(item.getId(), 1));
					}
				}
				else
				{
					// Reduce all items to 1 quantity
					itemsAfterPuzzle2.add(new Item(item.getId(), 1));
				}
			}

			puzzleNeeds.addAll(partialPuzzle2Solution.getItemRequirements());
			ItemRequirement finalFindExchangeFor = findExchangeFor;

			if (findExchangeFor != null)
			{
				// TODO: Figure out what can be exchanged for the missing item
				var requiredValue = discToValue.get(findExchangeFor.getId());
				if (requiredValue != null)
				{
					// The user needs to exchange something that can give a disc of requiredValue
					// Search for single-disc exchanges first
					var singleDiscExchange = valuePossibleSingleDiscExchangesRequirements.get(requiredValue).stream().filter(requirement -> requirement.getId() != finalFindExchangeFor.getId()).filter(requirement -> itemsAfterPuzzle2.contains(new Item(requirement.getId(), 1))).collect(Collectors.toUnmodifiableList());
					toExchange.addAll(singleDiscExchange);
					// var asd = 5;
				}
			}
		}
	}

	public void reset()
	{
		puzzle1Requirement = null;
		puzzle2UpperRequirement = null;
		puzzle2LowerRequirement = null;
	}

	public boolean isGood()
	{
		return (puzzle1Requirement != null & puzzle2UpperRequirement != null & puzzle2LowerRequirement != null);
	}
}
