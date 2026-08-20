/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.sinsofthefather;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoorPuzzleStepTest
{
	private static final int SIZE = 5;
	private static final int FILLED = 2;

	@Test
	public void testValidPuzzleHasCorrectSums()
	{
		int[] rowSums = new int[]{8, 8, 6, 6, 9};
		int[] colSums = new int[]{8, 8, 11, 6, 6};

		DoorPuzzleStep.PuzzleLine[] result = DoorPuzzleStep.solveForSums(rowSums, colSums);

		assertNotNull(result, "Expected a solution for a valid door puzzle.");
		assertEquals(SIZE, result.length, "Grid should have 5 rows.");

		// Check each row sum matches the expected value
		for (int r = 0; r < SIZE; r++)
		{
			int sum = 0;
			for (int c = 0; c < SIZE; c++)
			{
				if (result[r].cells[c] == FILLED)
				{
					sum += (c + 1);
				}
			}
			assertEquals(rowSums[r], sum, "Row " + r + " sum mismatch.");
		}

		// Check each column sum matches the expected value
		for (int c = 0; c < SIZE; c++)
		{
			int sum = 0;
			for (int r = 0; r < SIZE; r++)
			{
				if (result[r].cells[c] == FILLED)
				{
					sum += (r + 1);
				}
			}
			assertEquals(colSums[c], sum, "Column " + c + " sum mismatch.");
		}
	}

	@Test
	public void testInvalidPuzzleReturnsNull()
	{
		// Impossible puzzle: all rows must be empty (sum 0),
		// but columns demand non-zero sums.
		int[] rowSums = new int[]{0, 0, 0, 0, 0};
		int[] colSums = new int[]{1, 1, 1, 1, 1};

		DoorPuzzleStep.PuzzleLine[] result = DoorPuzzleStep.solveForSums(rowSums, colSums);

		assertNull(result, "Expected no solution for an invalid door puzzle.");
	}
}

