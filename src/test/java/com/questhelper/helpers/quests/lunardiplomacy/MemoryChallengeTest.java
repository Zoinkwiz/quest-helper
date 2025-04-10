/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.lunardiplomacy;

import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemoryChallengeTest
{
	@Test
	public void testNoValidPath()
	{
		int west = 0b00000000;
		int middleWest = 0b00000000;
		int middleEast = 0b00000000;
		int east = 0b00000000;

		List<WorldPoint> path = runPathTest(west, middleWest, middleEast, east);

		assertTrue(path.isEmpty(), "Path should be empty when there are no valid tiles.");
	}

	@Test
	public void testSingleValidPath()
	{
		int west = 0b00000111;
		int middleWest = 0b01110100;
		int middleEast = 0b01010100;
		int east = 0b11011100;

		List<WorldPoint> path = runPathTest(west, middleWest, middleEast, east);

		assertEquals(17, path.size(), "Path should consist of 17 points including start and end ledges.");
	}

	@Test
	public void testMultipleValidPaths()
	{
		int west = 0b11111111;
		int middleWest = 0b00000000;
		int middleEast = 0b11111111;
		int east = 0b00000000;

		List<WorldPoint> path = runPathTest(west, middleWest, middleEast, east);

		assertEquals(10, path.size(), "Path should consist of 10 points including start and end ledges.");
	}

	@Test
	public void testAllSafeTiles()
	{
		int west = 0b11111111;
		int middleWest = 0b11111111;
		int middleEast = 0b11111111;
		int east = 0b11111111;

		List<WorldPoint> path = runPathTest(west, middleWest, middleEast, east);

		assertEquals(10, path.size(), "Path should consist of 10 points including start and end ledges.");
	}

	private List<WorldPoint> runPathTest(int west, int middleWest, int middleEast, int east)
	{
		int[][] grid = createGrid(west, middleWest, middleEast, east);
		return MemoryChallenge.findPath(grid);
	}

	int[][] createGrid(int west, int middleWest, int middleEast, int east)
	{
		int[][] grid = new int[8][4];

		for (int row = 0; row < 8; row++)
		{
			grid[row][0] = (west >> (7 - row)) & 1;
			grid[row][1] = (middleWest >> (7 - row)) & 1;
			grid[row][2] = (middleEast >> (7 - row)) & 1;
			grid[row][3] = (east >> (7 - row)) & 1;
		}

		return grid;
	}
}
