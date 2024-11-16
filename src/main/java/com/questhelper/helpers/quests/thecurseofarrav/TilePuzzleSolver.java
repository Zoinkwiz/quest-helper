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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class TilePuzzleSolver extends DetailedOwnerStep
{
	/**
	 * Width & height of the tile puzzle
	 */
	private static final int SIZE = 12;
	private static final int GREEN_TILE = NullObjectID.NULL_50296;
	private static final int BLUE_TILE = NullObjectID.NULL_50294;
	private static final int RED_TILE = NullObjectID.NULL_50295;
	private static final int YELLOW_TILE = NullObjectID.NULL_50297;
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
	private ObjectStep firstObjectStepXD;
	private ObjectStep[][] objectSteps;

	List<int[]> shortestPath = null;
	private DetailedQuestStep fallbackStep;
	private QuestStep finishPuzzleStep;
	private ObjectStep mostRecentStep = null;

	public TilePuzzleSolver(TheCurseOfArrav theCurseOfArrav)
	{
		super(theCurseOfArrav, "Solve the floor tile puzzle. Follow the instructions in the overlay.");
	}

	/**
	 * @param startX local X coordinate of the tiles array for where to start searching
	 * @param startY local Y coordinate of the tiles array for where to start searching
	 * @return A list of local X/Y coordinates if a path to the end was found
	 */
	private @Nullable List<int[]> findPath(int startX, int startY)
	{
		assert (startX >= 0 && startX < SIZE);
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
				var path = this.findPath(11, y);
				if (path != null)
				{
					log.debug("Found possible path starting at {}/{}. Length {}", 11, y, path.size());
					possiblePaths.add(path);
				}
			}

			for (var possiblePath : possiblePaths)
			{
				if (this.shortestPath == null || possiblePath.size() < this.shortestPath.size())
				{
					this.shortestPath = possiblePath;
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
		objectSteps = new ObjectStep[SIZE][SIZE];
		var baseX = 3737;
		var baseY = 4709;
		var plane = 0;
		for (int x = 0; x < SIZE; ++x)
		{
			var objectText = "Click the tile to pass through the puzzle.";
			if (x == 11) {
				objectText = "Click the tile to start the puzzle.";
			}
			for (int y = 0; y < SIZE; ++y)
			{
				var wp = new WorldPoint(baseX + x, baseY + y, plane);
				var objectStep = new ObjectStep(getQuestHelper(), GREEN_TILE, wp, objectText);
				objectStep.addAlternateObjects(BLUE_TILE, RED_TILE, YELLOW_TILE);
				if (firstObjectStepXD == null) {
					firstObjectStepXD = objectStep;
				}
				objectSteps[x][y] = objectStep;
			}
		}

		fallbackStep = new DetailedQuestStep(getQuestHelper(), new WorldPoint(3734, 4714, 0), "Unable to figure out a path, click your way across lol"); // TODO

		finishPuzzleStep = new ObjectStep(getQuestHelper(), ObjectID.LEVER_50205, new WorldPoint(3735, 4719, 0), "Finish the puzzle by clicking the lever.");
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
		var localPoint = QuestPerspective.getRealWorldPointFromLocal(client, localPlayer.getWorldLocation());
		if (localPoint == null) {
			startUpStep(fallbackStep);
			return;
		}


		var baseX = 3737;
		var baseY = 4709;

		var xInPuzzle = localPoint.getX() - baseX;
		var yInPuzzle = localPoint.getY() - baseY;

		var puzzleStartX = this.shortestPath.get(0)[0];
		var puzzleStartY = this.shortestPath.get(0)[1];

		if (xInPuzzle >= 0 && xInPuzzle < SIZE && yInPuzzle >= 0 && yInPuzzle < SIZE) {
			log.info("Player is in the puzzle, at {}/{}", xInPuzzle, yInPuzzle);
			boolean nextIsOurCoolStep = false;
			for (var pathPos : this.shortestPath) {
				if (nextIsOurCoolStep) {
					startUpStep(objectSteps[pathPos[0]][pathPos[1]]);
					return;
				}
				if (pathPos[0] == xInPuzzle && pathPos[1] == yInPuzzle) {
					nextIsOurCoolStep = true;
					this.mostRecentStep = objectSteps[pathPos[0]][pathPos[1]];
				}
			}
			if (nextIsOurCoolStep) {
				log.info("user is at end");
				startUpStep(finishPuzzleStep);
				return;
			}

			if (this.mostRecentStep != null)
			{
				log.debug("user stepped off the path, lead them back");
				startUpStep(this.mostRecentStep);
			}
		} else {
			log.debug("player is outside of puzzle: {} / {} / {}/{}", playerWp, localPoint, xInPuzzle, yInPuzzle);
			var userIsPastPuzzle = localPoint.getX() <= 3730 || (localPoint.getX() <= baseX && localPoint.getY() >= 4701);
			if (userIsPastPuzzle) {
				// highlight lever
				startUpStep(finishPuzzleStep);
			} else {
				// highlight puzzle start
				startUpStep(objectSteps[puzzleStartX][puzzleStartY]);
			}
		}
	}

	@Override
	public List<QuestStep> getSteps()
	{
		var steps = new ArrayList<QuestStep>();
		steps.add(fallbackStep);
		steps.add(firstObjectStepXD);

		for (int x = 0; x < SIZE; ++x)
		{
			for (int y = 0; y < SIZE; ++y)
			{
				steps.add(objectSteps[x][y]);
			}
		}

		steps.add(finishPuzzleStep);

		return steps;
	}
}
