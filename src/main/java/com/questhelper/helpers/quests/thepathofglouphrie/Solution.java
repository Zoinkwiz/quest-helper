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

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

	public static boolean inventoryHas(final List<Item> haystack, int needle) {
		for (var item : haystack) {
			if (item.getId() == needle) {
				return true;
			}
		}
		return false;
	}

	public void load(Client client, List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue,
					 final HashMap<Integer, ItemRequirement> discs,
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

		puzzleNeeds.clear();
		toExchange.clear();

		if (!puzzle1Requirement.check(client, false, items))
		{
			puzzleNeeds.add(puzzle1Requirement);
			var singleDiscExchange = valuePossibleSingleDiscExchangesRequirements.get(puzzle1SolutionValue).stream().filter(requirement -> inventoryHas(items, requirement.getId())).collect(Collectors.toUnmodifiableList());
			if (!singleDiscExchange.isEmpty())
			{
				// There's a possible exchange to get a partial solution
				toExchange.add(singleDiscExchange.get(0));
			}
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
			// Found a decent exchanger solution
			var itemsAfterPuzzle2 = new ArrayList<Item>();

			for (var item : itemsAfterPuzzle1)
			{
				if (consumed != null && consumed.getAllIds().contains(item.getId()))
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
					if (!singleDiscExchange.isEmpty())
					{
						toExchange.add(singleDiscExchange.get(0));
						return;
					}
				}
			}
		}

		if (partialPuzzle2Solution == null)
		{
			// Second pass, try to find a shit exchanger solution

			var looseNeeds = new HashSet<Integer>();
			var looseExchanges = new HashSet<Integer>();
			for (var possiblePuzzle2Solution : possiblePuzzle2Solutions)
			{
				var req1 = possiblePuzzle2Solution.getItemRequirements().get(0);
				var req2 = possiblePuzzle2Solution.getItemRequirements().get(1);
				var req1Value = discToValue.get(req1.getId());

				var singleDiscExchange = valuePossibleSingleDiscExchangesRequirements.get(req1Value)
					.stream().filter(requirement -> !req2.getAllIds().contains(requirement.getId()))
					.filter(requirement -> inventoryHas(items, requirement.getId())).collect(Collectors.toUnmodifiableList());
				if (!singleDiscExchange.isEmpty())
				{
					looseNeeds.addAll(req1.getAllIds());
					for (var ex : singleDiscExchange) {
						looseExchanges.addAll(ex.getAllIds());
					}
				}

				var req2Value = discToValue.get(req2.getId());

				singleDiscExchange = valuePossibleSingleDiscExchangesRequirements.get(req2Value)
					.stream().filter(requirement -> !req1.getAllIds().contains(requirement.getId()))
					.filter(requirement -> inventoryHas(items, requirement.getId())).collect(Collectors.toUnmodifiableList());
				if (!singleDiscExchange.isEmpty())
				{
					looseNeeds.addAll(req2.getAllIds());
					for (var ex : singleDiscExchange) {
						looseExchanges.addAll(ex.getAllIds());
					}
				}
			}

			if (!looseNeeds.isEmpty() && !looseExchanges.isEmpty())
			{
				toExchange.clear();
				for (var id : looseExchanges) {
					var itemReq = discs.get(id);
					if (itemReq != null) {
						toExchange.add(itemReq);
					}
				}

				puzzleNeeds.clear();
				for (var id : looseNeeds) {
					var itemReq = discs.get(id);
					if (itemReq != null) {
						puzzleNeeds.add(itemReq);
					}
				}
			}
		}
	}

	public void reset()
	{
		puzzle1Requirement = null;
		puzzle2UpperRequirement = null;
		puzzle2LowerRequirement = null;
		puzzleNeeds.clear();
		toExchange.clear();
	}

	public boolean isGood()
	{
		return (puzzle1Requirement != null & puzzle2UpperRequirement != null & puzzle2LowerRequirement != null);
	}
}
