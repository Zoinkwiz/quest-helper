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

import com.google.inject.testing.fieldbinder.Bind;
import com.questhelper.MockedTest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import static com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionTest extends MockedTest
{
	/**
	 * ItemID to ItemRequirement map
	 */
	@Bind
	private final HashMap<Integer, ItemRequirement> discs = new HashMap<>();
	/**
	 * Value to ItemRequirement map
	 */
	@Bind
	private final HashMap<Integer, ItemRequirement> valueToRequirement = new HashMap<>();
	/**
	 * ItemID to Value map
	 */
	@Bind
	private final HashMap<Integer, Integer> discToValue = new HashMap<>();
	/**
	 * Value to list of possible requirements using exactly 2 different
	 */
	@Bind
	private final HashMap<Integer, List<ItemRequirements>> valueToDoubleDiscRequirement = new HashMap<>();

	private final Solution solution = new Solution();

	@Override
	@BeforeEach
	protected void setUp()
	{
		super.setUp();

		loadDiscs(discs);
		loadValueToRequirement(discs, valueToRequirement);
		loadDiscToValue(discToValue);
		loadValueToDoubleDiscRequirement(valueToRequirement, valueToDoubleDiscRequirement);

		solution.reset();
	}

	@ParameterizedTest(name = "This should be solvable {0} (P1 {1}, P2 {2})")
	@ArgumentsSource(Solvable.class)
	public void testSolvable(List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue)
	{
		solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue, valueToRequirement, valueToDoubleDiscRequirement);

		assertTrue(solution.isGood());
	}

	@ParameterizedTest(name = "This should not be solvable {0} (P1 {1}, P2 {2})")
	@ArgumentsSource(NotSolvable.class)
	public void testNotSolvable(List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue)
	{
		solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue, valueToRequirement, valueToDoubleDiscRequirement);

		assertFalse(solution.isGood());
	}

	/**
	 * List of scenarios that SHOULD be solvable
	 */
	private static class Solvable implements ArgumentsProvider
	{
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context)
		{
			return Stream.of(
				// TOP PUZZLE: GREEN CIRCLE + GREEN SQUARE (20)
				// BOTTOM PUZZLE: VIOLET SQUARE (28)
				Arguments.of(
					List.of(
						new Item(ItemID.BLUE_SQUARE, 1),
						new Item(ItemID.GREEN_TRIANGLE, 1),
						new Item(ItemID.GREEN_SQUARE, 1)
					),
					20,
					28
				),
				// TOP PUZZLE: YELLOW SQUARE + GREEN SQUARE (13)
				// BOTTOM PUZZLE: VIOLET CIRCLE (7)
				Arguments.of(
					List.of(
						new Item(ItemID.ORANGE_CIRCLE, 2),
						new Item(ItemID.RED_CIRCLE, 1),
						new Item(ItemID.GREEN_CIRCLE, 1),
						new Item(ItemID.BLUE_SQUARE, 2),
						new Item(ItemID.VIOLET_TRIANGLE, 1),
						new Item(ItemID.INDIGO_SQUARE, 1),
						new Item(ItemID.BLUE_PENTAGON, 2),
						new Item(ItemID.VIOLET_SQUARE, 1),
						new Item(ItemID.INDIGO_PENTAGON, 2),
						new Item(ItemID.GREEN_TRIANGLE, 1),
						new Item(ItemID.INDIGO_CIRCLE, 1),
						new Item(ItemID.ORANGE_PENTAGON, 1)
					),
					12,
					7
				)
			);
		}
	}

	/**
	 * List of scenarios that are NOT solvable
	 */
	private static class NotSolvable implements ArgumentsProvider
	{
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context)
		{
			return Stream.of(
				Arguments.of(
					List.of(
						new Item(ItemID.BLUE_SQUARE, 1),
						new Item(ItemID.RED_CIRCLE, 1),
						new Item(ItemID.GREEN_SQUARE, 1)
					),
					20,
					28
				),
				// TOP PUZZLE: YELLOW SQUARE + GREEN SQUARE (13)
				// BOTTOM PUZZLE: VIOLET CIRCLE (7)
				Arguments.of(
					List.of(
						new Item(ItemID.ORANGE_PENTAGON, 1),
						new Item(ItemID.GREEN_TRIANGLE, 1),
						new Item(ItemID.YELLOW_PENTAGON, 1),
						new Item(ItemID.BLUE_SQUARE, 2),
						new Item(ItemID.VIOLET_TRIANGLE, 1),
						new Item(ItemID.INDIGO_SQUARE, 1),
						new Item(ItemID.BLUE_PENTAGON, 2),
						new Item(ItemID.VIOLET_SQUARE, 1),
						new Item(ItemID.INDIGO_PENTAGON, 2)
					),
					12,
					7
				)
			);
		}
	}
}
