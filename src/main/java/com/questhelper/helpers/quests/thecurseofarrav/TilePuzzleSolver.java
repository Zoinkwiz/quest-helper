/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thecurseofarrav;

import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.tools.QuestPerspective;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public class TilePuzzleSolver extends DetailedOwnerStep
{
	/**
	 * Width & height of the tile puzzle
	 */
	private static final int SIZE = 12;
	private static final int GREEN_TILE = ObjectID.MOM2_GREEN_TILES;
	private static final int BLUE_TILE = ObjectID.MOM2_BLUE_TILES;
	private static final int RED_TILE = ObjectID.MOM2_RED_TILES;
	private static final int YELLOW_TILE = ObjectID.MOM2_YELLOW_TILES;
	private static final Set<Integer> VALID_TILES = Set.of(GREEN_TILE, BLUE_TILE, RED_TILE, YELLOW_TILE);

	/**
	 * a 2-dimensional array of the tiles. [x][y]
	 * The value is the object ID (i.e. the color of the tile)
	 */
	private final int[][] tiles = new int[SIZE][SIZE];

	@Inject
	Client client;

	/**
	 * State of the tiles array.
	 * False if tiles have not had their object IDs filled in.
	 * True if they have had their object IDs filled in.
	 */
	private boolean tilesConfigured = false;

	List<WorldPoint> shortestPath = null;
	private DetailedQuestStep fallbackStep;
	private QuestStep finishPuzzleStep;
	private DetailedQuestStep pathStep;

	public TilePuzzleSolver(TheCurseOfArrav theCurseOfArrav)
	{
		super(theCurseOfArrav, "Solve the floor tile puzzle. Follow the instructions in the overlay.");
	}

	/**
	 * @param startY local Y coordinate of the tiles array for where to start searching
	 * @return A list of local X/Y coordinates if a path to the end was found
	 */
	private @Nullable List<int[]> findPath(int startY)
	{
		int startX = 11;

		assert (startY >= 0 && startY < SIZE);
		assert (this.tilesConfigured);

		int startObjectId = this.tiles[startX][startY];
		var visited = new boolean[SIZE][SIZE];
		var queue = new LinkedList<int[]>();
		queue.add(new int[]{startX, startY});
		visited[startX][startY] = true;

		var parent = new int[SIZE][SIZE][2];
		parent[startX][startY] = new int[]{-1, -1};

		int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};

		while (!queue.isEmpty())
		{
			var current = queue.poll();
			var x = current[0];
			var y = current[1];

			if (x == 0)
			{
				var path = new ArrayList<int[]>();
				var retracedStep = new int[]{x, y};
				while (retracedStep[0] != -1 && retracedStep[1] != -1)
				{
					path.add(0, retracedStep);
					retracedStep = parent[retracedStep[0]][retracedStep[1]];
				}
				return path;
			}

			for (var direction : directions)
			{
				int newX = x + direction[0];
				int newY = y + direction[1];

				if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && !visited[newX][newY] && areObjectIdsCompatible(this.tiles[newX][newY], startObjectId))
				{
					queue.add(new int[]{newX, newY});
					visited[newX][newY] = true;
					parent[newX][newY] = new int[]{x, y};
				}
			}
		}

		return null;
	}

	private boolean areObjectIdsCompatible(int a, int b)
	{
		assert (VALID_TILES.contains(a));
		assert (VALID_TILES.contains(b));

		if (a == GREEN_TILE || a == BLUE_TILE)
		{
			return b == GREEN_TILE || b == BLUE_TILE;
		}

		if (a == RED_TILE || a == YELLOW_TILE)
		{
			return b == RED_TILE || b == YELLOW_TILE;
		}

		return false;
	}

	private Pair<Integer, Integer> findPuzzleStart(Tile[][] wvTiles)
	{
		// stupidly search for first tile with a green, red, blue, or yellow tile
		for (int x = 0; x < wvTiles.length; x++)
		{
			for (int y = 0; y < wvTiles[x].length; y++)
			{
				var tile = wvTiles[x][y];
				var groundObject = tile.getGroundObject();
				if (groundObject != null)
				{
					if (VALID_TILES.contains(groundObject.getId()))
					{
						return Pair.of(x, y);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Look through the world view and attempt to fill up the tiles array
	 */
	private void tryFillTiles()
	{
		var localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return;
		}

		var worldView = localPlayer.getWorldView();
		var squareOfTiles = worldView.getScene().getTiles()[worldView.getPlane()];

		var puzzleStart = this.findPuzzleStart(squareOfTiles);
		if (puzzleStart == null)
		{
			return;
		}

		var firstPuzzleX = puzzleStart.getLeft();
		var firstPuzzleY = puzzleStart.getRight();

		assert (firstPuzzleX != null);
		assert (firstPuzzleY != null);

		log.debug("Found first puzzle tile at {}/{}", firstPuzzleX, firstPuzzleY);

		for (int x = 0; x < 12; x++)
		{
			var offsetX = x + firstPuzzleX;
			if (offsetX >= squareOfTiles.length)
			{
				log.debug("X({} + {}) out of bounds when mapping puzzle tiles", x, firstPuzzleX);
				return;
			}

			for (int y = 0; y < 12; y++)
			{
				var offsetY = y + firstPuzzleY;
				if (offsetY >= squareOfTiles[offsetX].length)
				{
					log.debug("Y({} + {}) out of bounds when mapping puzzle tiles", y, firstPuzzleY);
					return;
				}

				var tile = squareOfTiles[offsetX][offsetY];
				var groundObject = tile.getGroundObject();
				if (groundObject == null)
				{
					log.debug("X({} + {}) Y({} + {}) had no ground object", x, firstPuzzleX, y, firstPuzzleY);
					return;
				}

				if (!VALID_TILES.contains(groundObject.getId()))
				{
					log.debug("X({} + {}) Y({} + {}) had an invalid ground object ({})", x, firstPuzzleX, y, firstPuzzleY, groundObject.getId());
					return;
				}

				this.tiles[x][y] = groundObject.getId();
			}
		}

		this.tilesConfigured = true;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!this.tilesConfigured)
		{
			this.tryFillTiles();
		}

		if (this.tilesConfigured && this.shortestPath == null)
		{
			// Figure out the shortest path
			var possiblePaths = new ArrayList<List<int[]>>();
			for (int y = 0; y < 12; y++)
			{
				var path = this.findPath(y);
				if (path != null)
				{
					log.debug("Found possible path starting at {}/{}. Length {}", 11, y, path.size());
					possiblePaths.add(path);
				}
			}

			var baseX = 3737;
			var baseY = 4709;

			for (var possiblePath : possiblePaths)
			{
				if (this.shortestPath == null || possiblePath.size() < this.shortestPath.size())
				{
					List<WorldPoint> line = new ArrayList<>();
					for (int[] tileXY : possiblePath)
					{
						var wp = new WorldPoint(baseX + tileXY[0], baseY + tileXY[1], 0);
						line.add(wp);
					}
					this.shortestPath = line;
					this.pathStep.setLinePoints(this.shortestPath);
				}
			}
		}

		updateSteps();
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		pathStep = new DetailedQuestStep(getQuestHelper(), new WorldPoint(3734, 4714, 0), "Click the tiles to pass through the puzzle.");

		fallbackStep = new DetailedQuestStep(getQuestHelper(), new WorldPoint(3734, 4714, 0), "Unable to figure out a path, click your way across lol"); // TODO

		finishPuzzleStep = new ObjectStep(getQuestHelper(), ObjectID.COA_MASTABA_LEVER_OFF, new WorldPoint(3735, 4719, 0), "Finish the puzzle by clicking the lever.");
	}

	protected void updateSteps()
	{
		if (this.shortestPath == null) {
			startUpStep(fallbackStep);
			return;
		}

		var localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			startUpStep(fallbackStep);
			return;
		}

		var playerWp = localPlayer.getWorldLocation();
		var worldPoint = QuestPerspective.getWorldPointConsideringWorldView(client, client.getTopLevelWorldView(), localPlayer.getWorldLocation());
		if (worldPoint == null) {
			startUpStep(fallbackStep);
			return;
		}

		var baseX = 3737;
		var baseY = 4709;

		var xInPuzzle = worldPoint.getX() - baseX;
		var yInPuzzle = worldPoint.getY() - baseY;

		if (xInPuzzle > 0 && xInPuzzle < SIZE && yInPuzzle >= 0 && yInPuzzle < SIZE) {
			log.debug("Player is in the puzzle, at {}/{}", xInPuzzle, yInPuzzle);
			startUpStep(pathStep);
		} else {
			log.debug("player is outside of puzzle: {} / {} / {}/{}", playerWp, worldPoint, xInPuzzle, yInPuzzle);
			var userIsPastPuzzle = worldPoint.getX() <= 3730 || (worldPoint.getX() <= baseX && worldPoint.getY() >= 4701);
			if (userIsPastPuzzle) {
				// highlight lever
				startUpStep(finishPuzzleStep);
			} else {
				// highlight puzzle start
				startUpStep(pathStep);
			}
		}
	}

	@Override
	public List<QuestStep> getSteps()
	{
		var steps = new ArrayList<QuestStep>();
		steps.add(fallbackStep);
		steps.add(pathStep);
		steps.add(finishPuzzleStep);

		return steps;
	}
}
