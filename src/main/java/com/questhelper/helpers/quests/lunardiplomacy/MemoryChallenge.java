/*
 * Copyright (c) 2021, Zoinkwiz
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

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MemoryChallenge extends DetailedQuestStep
{
	List<WorldPoint> wps = new ArrayList<>();
	List<WorldPoint> currentPath = new ArrayList<>();
	int column1, column2, column3, column4, lastPos;

	private static final int ROWS = 8;
	private static final int COLS = 4;
	private static final int[] LEDGE_X = {1731, 1734, 1737, 1740};
	private static final int LEDGE_Y_START = 5108;
	private static final int LEDGE_Y_END = 5083;

	public MemoryChallenge(QuestHelper questHelper)
	{
		super(questHelper, new WorldPoint(1737, 5083, 2),
			"Work out the route across the cloud tiles through trial and error.");
	}

	@Override
	public void startUp()
	{
		super.startUp();
		setupPaths();
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		setupPaths();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (wps.size() == 0)
		{
			return;
		}

		if (lastPos + 1 == wps.size())
		{
			return;
		}

		// If start of path, check first point in legit path
		if (currentPath.size() == 0)
		{
			checkNextTile(0);
			return;
		}

		// If started path, check furthest we've reached in path
		WorldPoint lastPoint = currentPath.get(currentPath.size() - 1);

		WorldPoint instanceWp = wps.get(lastPos);
		if (instanceWp == null)
		{
			return;
		}

		if (instanceWp.distanceTo(lastPoint) == 0 && wps.get(lastPos + 1) != null)
		{
			checkNextTile(lastPos + 1);
		}
	}

	public void checkNextTile(int wpsPos)
	{
		WorldPoint instanceWp = QuestPerspective.getWorldPointConsideringWorldView(client, client.getTopLevelWorldView(), wps.get(wpsPos));
		if (instanceWp == null)
		{
			return;
		}

		// If on same tiles as wpsPos
		if (client.getLocalPlayer() != null &&
			client.getLocalPlayer().getWorldLocation().distanceTo(instanceWp) == 0)
		{
			currentPath.add(wps.get(wpsPos));
			lastPos = wpsPos;
			setLinePoints(currentPath);
		}
	}

	private void setupPaths()
	{
		int current1 = client.getVarbitValue(VarbitID.LUNAR_FLOOR_COL_A);
		int current2 = client.getVarbitValue(VarbitID.LUNAR_FLOOR_COL_B);
		int current3 = client.getVarbitValue(VarbitID.LUNAR_FLOOR_COL_C);
		int current4 = client.getVarbitValue(VarbitID.LUNAR_FLOOR_COL_D);

		if (current1 == column1 &&
			current2 == column2 &&
			current3 == column3 &&
			current4 == column4
		)
		{
			return;
		}

		column1 = current1;
		column2 = current2;
		column3 = current3;
		column4 = current4;

		int[][] grid = createGrid(column1, column2, column3, column4);
		wps = findPath(grid);
	}

	private static int[][] createGrid(int west, int middleWest, int middleEast, int east)
	{
		int[][] grid = new int[ROWS][COLS];

		for (int row = 0; row < ROWS; row++)
		{
			grid[ROWS - 1 - row][0] = (west >> (ROWS - 1 - row)) & 1;
			grid[ROWS - 1 - row][1] = (middleWest >> (ROWS - 1 - row)) & 1;
			grid[ROWS - 1 - row][2] = (middleEast >> (ROWS - 1 - row)) & 1;
			grid[ROWS - 1 - row][3] = (east >> (ROWS - 1 - row)) & 1;
		}

		return grid;
	}

	public static List<WorldPoint> findPath(int[][] grid)
	{
		List<WorldPoint> path = new ArrayList<>();

		// Find the starting point in the first row
		int startCol = -1;
		for (int col = 0; col < COLS; col++)
		{
			if (grid[ROWS - 1][col] == 1)
			{
				startCol = col;
				break;
			}
		}

		if (startCol == -1)
		{
			return new ArrayList<>();
		}

		path.add(new WorldPoint(LEDGE_X[startCol], LEDGE_Y_START, 2));
		// Perform the pathfinding
		addNewTileToPath(grid, ROWS - 1, startCol, path);
		path.add(new WorldPoint(path.get(path.size() - 1).getX(), LEDGE_Y_END, 2));

		return path;
	}

	private static boolean addNewTileToPath(int[][] grid, int row, int col, List<WorldPoint> path)
	{
		// Add new tile to path, and remove, so it won't be re-considered
		path.add(new WorldPoint(LEDGE_X[col], LEDGE_Y_START - 2 - ((ROWS - row - 1) * 3), 2));
		grid[row][col] = 0;

		// If reached end, return
		if (row == 0)
		{
			return true;
		}

		// Check south, then east, then west
		int[][] directions = {{-1, 0}, {0, 1}, {0, -1}};
		for (int[] dir : directions)
		{
			int newRow = row + dir[0];
			int newCol = col + dir[1];

			if (isValidNextTile(grid, newRow, newCol))
			{
				if (addNewTileToPath(grid, newRow, newCol, path))
				{
					return true;
				}
			}
		}

		// Remove if something went wrong
		grid[row][col] = 1;
		path.remove(path.size() - 1);
		return false;
	}

	private static boolean isValidNextTile(int[][] grid, int row, int col)
	{
		return row >= 0 && row < ROWS && col >= 0 && col < COLS && grid[row][col] == 1;
	}
}
