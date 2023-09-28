package com.questhelper.helpers.quests.thepathofglouphrie;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.Item;

public class Solution
{
	public ItemRequirement puzzle1Requirement;
	public ItemRequirement puzzle2UpperRequirement;
	public ItemRequirement puzzle2LowerRequirement;

	public void load(Client client, List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue, final HashMap<Integer, ItemRequirement> valueToRequirement, final HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement)
	{
		this.reset();

		var puzzle1Requirement = valueToRequirement.get(puzzle1SolutionValue);
		if (puzzle1Requirement == null)
		{
			// No possible solution at all to this value, not sure how to proceed
			return;
		}

		if (!puzzle1Requirement.check(client, false, items))
		{
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

		// solve puzzle 2
		var possiblePuzzle2Solutions = valueToDoubleDiscRequirement.get(puzzle2SolutionValue);
		for (var possiblePuzzle2Solution : possiblePuzzle2Solutions)
		{
			if (possiblePuzzle2Solution.check(client, false, itemsAfterPuzzle1))
			{
				// Found a valid puzzle2 solution
				puzzle2UpperRequirement = possiblePuzzle2Solution.getItemRequirements().get(0);
				puzzle2LowerRequirement = possiblePuzzle2Solution.getItemRequirements().get(1);
				return;
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
