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
import static com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle.loadDiscToValue;
import static com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle.loadDiscs;
import static com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle.loadValuePossibleExchanges;
import static com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle.loadValueToDoubleDiscRequirement;
import static com.questhelper.helpers.quests.thepathofglouphrie.YewnocksPuzzle.loadValueToRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

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

	private final HashMap<Integer, List<ItemRequirement>> valuePossibleSingleDiscExchangesRequirements = new HashMap<>();

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
		loadValuePossibleExchanges(discs, discToValue, valueToRequirement, valuePossibleSingleDiscExchangesRequirements);

		solution.reset();
	}

	@ParameterizedTest(name = "This should be solvable {0} (P1 {1})")
	@ArgumentsSource(Subsets.class)
	public void testSubsets(int sum, List<List<Integer>> expected)
	{
		var possibleNumbers = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 15, 16, 18, 20, 21, 24, 25, 28, 30, 35};
		var actual = YewnocksPuzzle.SubsetSum.findSubsetsWithSum(possibleNumbers, sum);
		assertEquals(expected, actual);
	}

	@ParameterizedTest(name = "This should be solvable {0} (P1 {1})")
	@ArgumentsSource(SubsetRequirements.class)
	public void testSubsetRequirements(int sum, List<Integer> expected)
	{
		var actualPossibleExchanges = valuePossibleSingleDiscExchangesRequirements.get(sum).stream().map(ItemRequirement::getId).collect(Collectors.toList());
		assertEquals(
			expected.stream().sorted().collect(Collectors.toList()),
			actualPossibleExchanges.stream().sorted().collect(Collectors.toList())
		);
	}

	@ParameterizedTest(name = "This should be solvable {0} (P1 {1}, P2 {2})")
	@ArgumentsSource(Solvable.class)
	public void testSolvable(List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue)
	{
		solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue, discs, valueToRequirement, valueToDoubleDiscRequirement, discToValue, valuePossibleSingleDiscExchangesRequirements);

		assertTrue(solution.isGood());
	}

	@ParameterizedTest(name = "This should not be solvable {0} (P1 {1}, P2 {2})")
	@ArgumentsSource(NotSolvable.class)
	public void testNotSolvable(List<Item> items, int puzzle1SolutionValue, int puzzle2SolutionValue)
	{
		solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue, discs, valueToRequirement, valueToDoubleDiscRequirement, discToValue, valuePossibleSingleDiscExchangesRequirements);
		assertFalse(solution.isGood());
	}

	@ParameterizedTest(name = "This should not be solvable, but it should be exchangable {0} (P1 {1}, P2 {2})")
	@ArgumentsSource(Exchangable.class)
	public void testExchangable(final List<Item> items,
								int puzzle1SolutionValue, int puzzle2SolutionValue,
								final List<Integer> expectedExchanges,
								final List<Integer> expectedExchangesFor)
	{
		solution.load(client, items, puzzle1SolutionValue, puzzle2SolutionValue, discs, valueToRequirement, valueToDoubleDiscRequirement, discToValue, valuePossibleSingleDiscExchangesRequirements);

		assertFalse(solution.isGood());

		var actualExchangesIds = new ArrayList<Integer>();
		for (var toExchangeRequirement : solution.toExchange)
		{
			actualExchangesIds.addAll(toExchangeRequirement.getAllIds());
		}
		assertEquals(
			expectedExchanges.stream().sorted().collect(Collectors.toUnmodifiableList()),
			actualExchangesIds.stream().sorted().collect(Collectors.toUnmodifiableList())
		);

		var actualExchangesForIds = new ArrayList<Integer>();
		for (var toExchangeRequirement : solution.puzzleNeeds)
		{
			actualExchangesForIds.addAll(toExchangeRequirement.getAllIds());
		}
		assertEquals(
			expectedExchangesFor.stream().sorted().collect(Collectors.toUnmodifiableList()),
			actualExchangesForIds.stream().sorted().collect(Collectors.toUnmodifiableList())
		);
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
				// TOP PUZZLE: YELLOW CIRCLE (3) + GREEN TRIANGLE (12) = 15
				// BOTTOM PUZZLE: INDIGO TRIANGLE (18)
				Arguments.of(
					List.of(
						new Item(ItemID.RED_CIRCLE, 1),
						new Item(ItemID.RED_TRIANGLE, 1),
						new Item(ItemID.RED_SQUARE, 1),
						new Item(ItemID.YELLOW_SQUARE, 1),
						new Item(ItemID.YELLOW_PENTAGON, 1),
						new Item(ItemID.YELLOW_TRIANGLE, 1),
						new Item(ItemID.BLUE_TRIANGLE, 1),
						new Item(ItemID.BLUE_SQUARE, 1),
						new Item(ItemID.BLUE_PENTAGON, 2),
						new Item(ItemID.VIOLET_SQUARE, 3)
					),
					15,
					18
				),
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
				),
				// TOP PUZZLE: VIOLET_SQUARE (28) + VIOLET_SQUARE (28) = 56
				// BOTTOM PUZZLE: INDIGO TRIANGLE (18)
				Arguments.of(
					List.of(
						new Item(ItemID.RED_CIRCLE, 1),
						new Item(ItemID.RED_TRIANGLE, 1),
						new Item(ItemID.RED_SQUARE, 1),
						new Item(ItemID.YELLOW_SQUARE, 1),
						new Item(ItemID.YELLOW_PENTAGON, 1),
						new Item(ItemID.YELLOW_TRIANGLE, 1),
						new Item(ItemID.BLUE_TRIANGLE, 1),
						new Item(ItemID.BLUE_SQUARE, 1),
						new Item(ItemID.BLUE_PENTAGON, 2),
						new Item(ItemID.VIOLET_SQUARE, 3)
					),
					15,
					56
				),
				// TOP PUZZLE: RED CIRCLE + ORANGE SQUARE (9)
				// BOTTOM PUZZLE: GREEN PENTAGON (20)
				Arguments.of(
					List.of(
						new Item(ItemID.GREEN_SQUARE, 1),
						new Item(ItemID.INDIGO_TRIANGLE, 2),
						new Item(ItemID.VIOLET_TRIANGLE, 1),
						new Item(ItemID.INDIGO_SQUARE, 1),
						new Item(ItemID.BLUE_PENTAGON, 1),
						new Item(ItemID.VIOLET_SQUARE, 1),
						new Item(ItemID.INDIGO_PENTAGON, 1),
						new Item(ItemID.RED_CIRCLE, 1),
						new Item(ItemID.ORANGE_CIRCLE, 1),
						new Item(ItemID.YELLOW_TRIANGLE, 1),
						new Item(ItemID.RED_SQUARE, 1)
					),
					9,
					20
				)
			);
		}
	}

	/**
	 * List of valid subsets
	 */
	private static class Subsets implements ArgumentsProvider
	{
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context)
		{
			return Stream.of(
				Arguments.of(
					4,
					List.of(List.of(1, 3), List.of(4))
				)
			);
		}
	}

	/**
	 * List of valid subsets
	 */
	private static class SubsetRequirements implements ArgumentsProvider
	{
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context)
		{
			return Stream.of(
				Arguments.of(
					1,
					List.of(
						ItemID.RED_PENTAGON,
						ItemID.ORANGE_TRIANGLE,
						ItemID.ORANGE_SQUARE,
						ItemID.ORANGE_PENTAGON,
						ItemID.YELLOW_TRIANGLE,
						ItemID.YELLOW_SQUARE,
						ItemID.YELLOW_PENTAGON,
						ItemID.GREEN_TRIANGLE,
						ItemID.GREEN_SQUARE,
						ItemID.GREEN_PENTAGON,
						ItemID.BLUE_CIRCLE,
						ItemID.BLUE_TRIANGLE,
						ItemID.BLUE_SQUARE,
						ItemID.BLUE_PENTAGON,
						ItemID.INDIGO_CIRCLE,
						ItemID.INDIGO_TRIANGLE,
						ItemID.INDIGO_SQUARE,
						ItemID.INDIGO_PENTAGON,
						ItemID.VIOLET_CIRCLE,
						ItemID.VIOLET_TRIANGLE,
						ItemID.VIOLET_SQUARE,
						ItemID.VIOLET_PENTAGON
					)
				),
				Arguments.of(
					30,
					List.of(ItemID.VIOLET_PENTAGON)
				),
				Arguments.of(
					28,
					List.of(ItemID.VIOLET_PENTAGON, ItemID.INDIGO_PENTAGON)
				),
				Arguments.of(
					25,
					List.of(ItemID.VIOLET_PENTAGON, ItemID.VIOLET_SQUARE, ItemID.INDIGO_PENTAGON)
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
				),
				// TOP PUZZLE: BLUE CIRCLE + YELLOW PENTAGON (20)
				// BOTTOM PUZZLE: VIOLET CIRCLE (7)
				Arguments.of(
					List.of(
						new Item(ItemID.ORANGE_PENTAGON, 3),
						new Item(ItemID.GREEN_SQUARE, 3),
						new Item(ItemID.INDIGO_TRIANGLE, 1),
						new Item(ItemID.GREEN_PENTAGON, 1),
						new Item(ItemID.INDIGO_SQUARE, 1)
					),
					20,
					7
				)
			);
		}
	}

	/**
	 * List of scenarios that are NOT solvable, but are exchangable
	 */
	private static class Exchangable implements ArgumentsProvider
	{
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context)
		{
			return Stream.of(
				// TOP PUZZLE: YELLOW CIRCLE (3) + GREEN TRIANGLE (12) = 15
				// BOTTOM PUZZLE: INDIGO TRIANGLE (18)
				Arguments.of(
					List.of(
						new Item(ItemID.RED_CIRCLE, 1),			// 1
						new Item(ItemID.RED_TRIANGLE, 1),		// 3
						new Item(ItemID.RED_SQUARE, 1),			// 4
						new Item(ItemID.YELLOW_SQUARE, 1),		// 12
						new Item(ItemID.YELLOW_PENTAGON, 1),	// 15
						new Item(ItemID.YELLOW_TRIANGLE, 1),	// 9
						new Item(ItemID.BLUE_SQUARE, 1),		// 20
						new Item(ItemID.BLUE_PENTAGON, 2),		// 25
						new Item(ItemID.VIOLET_SQUARE, 3)		// 28
					),
					15,
					18,
					List.of(ItemID.BLUE_SQUARE),
					List.of(
						ItemID.YELLOW_PENTAGON, // Would make a valid solution
						ItemID.BLUE_TRIANGLE, // Would make a valid solution
						ItemID.YELLOW_CIRCLE, // Would NOT make a fully valid solution
						ItemID.RED_TRIANGLE // Would NOT make a fully valid solution
					)
				),
				Arguments.of(
					List.of(
						new Item(ItemID.BLUE_SQUARE, 1),
						new Item(ItemID.RED_CIRCLE, 1),
						new Item(ItemID.GREEN_SQUARE, 1),
						new Item(ItemID.INDIGO_PENTAGON, 1)
					),
					20, // blue square gets consumed here
					28,
					List.of(ItemID.INDIGO_PENTAGON),
					List.of(ItemID.YELLOW_SQUARE, ItemID.GREEN_TRIANGLE, ItemID.GREEN_SQUARE)
				),
				// TOP PUZZLE: YELLOW TRIANGLE (9) + GREEN TRIANGLE (12)
				// BOTTOM PUZZLE: BLUE TRIANGLE (15)
				Arguments.of(
					List.of(
						new Item(ItemID.YELLOW_TRIANGLE, 5),
						new Item(ItemID.ORANGE_PENTAGON, 6),
						new Item(ItemID.YELLOW_SQUARE, 7),
						new Item(ItemID.GREEN_TRIANGLE, 5),
						new Item(ItemID.YELLOW_PENTAGON, 2),
						new Item(ItemID.GREEN_SQUARE, 1),
						new Item(ItemID.GREEN_PENTAGON, 1),
						new Item(ItemID.BLUE_SQUARE, 1),
						new Item(ItemID.VIOLET_TRIANGLE, 6),
						new Item(ItemID.INDIGO_SQUARE, 2)
					),
					21,
					15,
					List.of(ItemID.ORANGE_PENTAGON),
					List.of(ItemID.RED_TRIANGLE, ItemID.YELLOW_CIRCLE, ItemID.YELLOW_SQUARE, ItemID.GREEN_TRIANGLE)
				),
				// TOP PUZZLE: BLUE CIRCLE + YELLOW PENTAGON (20)
				// BOTTOM PUZZLE: VIOLET CIRCLE (7)
				Arguments.of(
					List.of(
						new Item(ItemID.ORANGE_PENTAGON, 3),
						new Item(ItemID.GREEN_SQUARE, 3),
						new Item(ItemID.INDIGO_TRIANGLE, 1),
						new Item(ItemID.GREEN_PENTAGON, 1),
						new Item(ItemID.INDIGO_SQUARE, 1)
					),
					20,
					7,
					List.of(ItemID.INDIGO_TRIANGLE, ItemID.INDIGO_SQUARE, ItemID.ORANGE_PENTAGON, ItemID.GREEN_SQUARE, ItemID.GREEN_PENTAGON),
					List.of(ItemID.RED_CIRCLE, ItemID.RED_TRIANGLE, ItemID.RED_SQUARE, ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE, ItemID.ORANGE_TRIANGLE, ItemID.YELLOW_CIRCLE, ItemID.GREEN_CIRCLE, ItemID.BLUE_CIRCLE, ItemID.INDIGO_CIRCLE)
				),
				// TOP PUZZLE: BLUE CIRCLE + YELLOW PENTAGON (20)
				// BOTTOM PUZZLE: VIOLET CIRCLE (7)
				Arguments.of(
					List.of(
						new Item(ItemID.YELLOW_TRIANGLE, 3),
						new Item(ItemID.ORANGE_PENTAGON, 5),
						new Item(ItemID.YELLOW_SQUARE, 6),
						new Item(ItemID.GREEN_TRIANGLE, 6),
						new Item(ItemID.YELLOW_PENTAGON, 2),
						new Item(ItemID.GREEN_SQUARE, 8),
						new Item(ItemID.INDIGO_TRIANGLE, 4),
						new Item(ItemID.GREEN_PENTAGON, 5),
						new Item(ItemID.BLUE_SQUARE, 4),
						new Item(ItemID.VIOLET_TRIANGLE, 2),
						new Item(ItemID.INDIGO_SQUARE, 4),
						new Item(ItemID.BLUE_PENTAGON, 1),
						new Item(ItemID.VIOLET_SQUARE, 1),
						new Item(ItemID.INDIGO_PENTAGON, 3)
					),
					20,
					7,
					List.of(
						ItemID.YELLOW_TRIANGLE,
						ItemID.ORANGE_PENTAGON,
						ItemID.YELLOW_SQUARE,
						ItemID.YELLOW_PENTAGON,
						ItemID.GREEN_SQUARE,
						ItemID.INDIGO_TRIANGLE,
						ItemID.GREEN_PENTAGON,
						ItemID.GREEN_TRIANGLE,
						ItemID.BLUE_SQUARE,
						ItemID.VIOLET_TRIANGLE,
						ItemID.INDIGO_SQUARE,
						ItemID.BLUE_PENTAGON,
						ItemID.VIOLET_SQUARE,
						ItemID.INDIGO_PENTAGON
					),
					List.of(ItemID.RED_CIRCLE, ItemID.RED_TRIANGLE, ItemID.RED_SQUARE, ItemID.RED_PENTAGON, ItemID.ORANGE_CIRCLE, ItemID.ORANGE_TRIANGLE, ItemID.YELLOW_CIRCLE, ItemID.GREEN_CIRCLE, ItemID.BLUE_CIRCLE, ItemID.INDIGO_CIRCLE)
				),
				// TOP PUZZLE: RED CIRCLE + ORANGE SQUARE (9)
				// BOTTOM PUZZLE: GREEN PENTAGON (20)
				Arguments.of(
					List.of(
						new Item(ItemID.GREEN_SQUARE, 2),
						new Item(ItemID.INDIGO_TRIANGLE, 2),
						new Item(ItemID.VIOLET_TRIANGLE, 1),
						new Item(ItemID.INDIGO_SQUARE, 1),
						new Item(ItemID.BLUE_PENTAGON, 1),
						new Item(ItemID.VIOLET_SQUARE, 1),
						new Item(ItemID.INDIGO_PENTAGON, 1)
					),
					9,
					20,
					List.of(ItemID.GREEN_SQUARE),
					List.of(ItemID.YELLOW_TRIANGLE)
				)
			);
		}
	}
}
