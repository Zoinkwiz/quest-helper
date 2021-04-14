/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.sinsofthefather;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;

public class DoorPuzzleStep extends DetailedQuestStep
{
	@Inject
	Client client;

	private final int UNKNOWN_VALUE = 0;
	private final int EMPTY = 1;
	private final int FILLED = 2;

	private final int SIZE = 5;

	private PuzzleLine[] result = null;

	private boolean solving = false;

	static class PuzzleLine
	{
		public int[] cells;

		PuzzleLine(int... cells)
		{
			this.cells = cells;
		}

		PuzzleLine(int size)
		{
			this.cells = new int[size];
		}
	}

	static class PuzzleState
	{
		PuzzleLine[][] rowSolutions;
		PuzzleLine[][] columnSolutions;
		PuzzleLine[] grid;
		int numberOfGray;

		PuzzleState(PuzzleLine[][] row, PuzzleLine[][] column, PuzzleLine[] grid, int numberOfGray)
		{
			this.rowSolutions = row;
			this.columnSolutions = column;
			this.grid = grid;
			this.numberOfGray = numberOfGray;
		}
	}

	public DoorPuzzleStep(BasicQuestHelper questHelper)
	{
		super(questHelper, "Solve the puzzle by marking the highlighted squares.");
	}

	private PuzzleLine[] solve()
	{
		int[] rowSums = new int[SIZE];
		int[] colSums = new int[SIZE];
		try
		{
			for (int i = 0; i < SIZE; i++)
			{
				colSums[i] = Integer.parseInt(client.getWidget(665, 19 + i).getText());
				rowSums[i] = Integer.parseInt(client.getWidget(665, 26 + i).getText());
			}
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("NumberFormatException: " + nfe.getMessage());
		}

		PuzzleState solved = newSolveState(rowSums, colSums);
		return solveGrid(solved);
	}

	private PuzzleLine[] solveGrid(PuzzleState puzzleState)
	{
		int iterations = 0;
		Queue<PuzzleState> puzzleStates = new LinkedList<>();
		puzzleStates.add(puzzleState);
		int MAX_ITERATIONS = 20;
		while (iterations < MAX_ITERATIONS)
		{
			PuzzleState solution = checkSolutionOverlaps(puzzleStates.remove());
			if (solution == null && puzzleStates.isEmpty())
			{
				return null;
			}
			else if (solution != null && solution.numberOfGray == 0)
			{ // If solved
				return solution.grid;
			}
			/* If unable to find answer, assume a square if filled or empty and then try solving again */
			if (solution != null)
			{
				puzzleStates.add(setFirstUnknownTo(solution, FILLED));
				puzzleStates.add(setFirstUnknownTo(solution, EMPTY));
			}
			iterations++;
		}
		return null;
	}

	private PuzzleState setFirstUnknownTo(PuzzleState puzzleState, int state)
	{
		int x = -1, y = -1;

		PuzzleState newPuzzleState = cloneState(puzzleState);

		boolean found = false;
		for (int r = 0; r < SIZE; r++)
		{
			for (int c = 0; c < SIZE; c++)
			{
				if (!found)
				{
					if (newPuzzleState.grid[r].cells[c] == UNKNOWN_VALUE)
					{
						x = r;
						y = c;
						found = true;
					}
				}
			}
		}

		newPuzzleState.grid[x].cells[y] = state;
		newPuzzleState.numberOfGray--;
		newPuzzleState.rowSolutions[x] = removeIncorrectSolutions(newPuzzleState.rowSolutions[x], y, state);
		newPuzzleState.rowSolutions[y] = removeIncorrectSolutions(newPuzzleState.rowSolutions[y], x, state);
		return newPuzzleState;
	}

	private PuzzleLine[] cloneGrid(PuzzleLine[] oldPuzzle)
	{
		PuzzleLine[] newGrid = generateNewGrid();
		for (int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				newGrid[i].cells[j] = oldPuzzle[i].cells[j];
			}
		}
		return newGrid;
	}

	private PuzzleLine[][] cloneSolutions(PuzzleLine[][] oldSolution)
	{
		PuzzleLine[][] newSolution = new PuzzleLine[SIZE][];
		for (int i = 0; i < SIZE; i++)
		{
			newSolution[i] = new PuzzleLine[oldSolution[i].length];
			for (int j = 0; j < oldSolution[i].length; j++)
			{
				PuzzleLine newLine = new PuzzleLine(SIZE);
				for (int k = 0; k < SIZE; k++)
				{
					newLine.cells[k] = oldSolution[i][j].cells[k];
				}
				newSolution[i][j] = newLine;
			}
		}
		return newSolution;
	}

	private PuzzleState cloneState(PuzzleState oldState)
	{
		return new PuzzleState(
			cloneSolutions(oldState.rowSolutions),
			cloneSolutions(oldState.columnSolutions),
			cloneGrid(oldState.grid),
			oldState.numberOfGray);
	}

	private PuzzleLine deduceColorsFromSols(PuzzleLine[] sols)
	{
		int numSolutions = sols.length;

		if (numSolutions == 0)
		{
			return null;
		}
		if (numSolutions == 1)
		{
			return sols[0];
		}
		PuzzleLine c = new PuzzleLine(SIZE);
		for (int i = 0; i < SIZE; i++)
		{
			int acc = 0;
			for (PuzzleLine sol : sols)
			{
				if (sol.cells[i] == FILLED)
				{
					acc++;
				}
			}

			if (acc == 0)
			{ /* If no solutions contain this cell */
				c.cells[i] = EMPTY;
			}
			else if (acc == numSolutions)
			{ /* If all solutions contain this cell */
				c.cells[i] = FILLED;
			}
			else
			{
				c.cells[i] = UNKNOWN_VALUE;
			}
		}
		return c;
	}


	private PuzzleState checkSolutionOverlaps(PuzzleState oldPuzzleState)
	{
		boolean hasFoundASolution = true;

		/* Loop until either we're finished, or are unable to find another change */
		while (hasFoundASolution && oldPuzzleState.numberOfGray > 0)
		{
			hasFoundASolution = false;
			for (int r = 0; r < SIZE; r++)
			{
				PuzzleLine sol = deduceColorsFromSols(oldPuzzleState.rowSolutions[r]);
				if (sol == null)
				{
					return null;
				}

				for (int c = 0; c < SIZE; c++)
				{
					if (sol.cells[c] != UNKNOWN_VALUE && oldPuzzleState.grid[r].cells[c] == UNKNOWN_VALUE)
					{
						hasFoundASolution = true;
						oldPuzzleState.numberOfGray--;
						oldPuzzleState.grid[r].cells[c] = sol.cells[c];
						oldPuzzleState.columnSolutions[c] = removeIncorrectSolutions(oldPuzzleState.columnSolutions[c], r, sol.cells[c]);
					}
				}
			}
			for (int c = 0; c < SIZE; c++)
			{
				PuzzleLine sol = deduceColorsFromSols(oldPuzzleState.columnSolutions[c]);
				if (sol == null)
				{
					return null;
				}

				for (int r = 0; r < SIZE; r++)
				{
					if (sol.cells[r] != UNKNOWN_VALUE && oldPuzzleState.grid[r].cells[c] == UNKNOWN_VALUE)
					{
						hasFoundASolution = true;
						oldPuzzleState.numberOfGray--;
						oldPuzzleState.grid[r].cells[c] = sol.cells[r];
						oldPuzzleState.rowSolutions[r] = removeIncorrectSolutions(oldPuzzleState.rowSolutions[r], c, sol.cells[r]);
					}
				}
			}
		}
		return oldPuzzleState;
	}

	private PuzzleLine[] removeIncorrectSolutions(PuzzleLine[] oldSolutions, int k, int c)
	{
		ArrayList<PuzzleLine> newSolutions = new ArrayList<>();

		for (PuzzleLine solution : oldSolutions)
		{
			if (solution.cells[k] == c)
			{
				newSolutions.add(solution);
			}
		}

		return newSolutions.toArray(new PuzzleLine[0]);
	}

	private PuzzleState newSolveState(int[] rowSums, int[] columnSums)
	{
		return new PuzzleState(createAllSolutions(rowSums), createAllSolutions(columnSums), generateNewGrid(), SIZE * SIZE);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget xAxis = client.getWidget(665, 25);
		Widget yAxis = client.getWidget(665, 18);
		if (xAxis != null && yAxis != null && !solving)
		{
			solving = true;

			result = solve();
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		Widget panels = client.getWidget(665, 32);
		if (result != null && panels != null)
		{
			for (int i = 0; i < result.length; i++)
			{
				for (int j = 0; j < result[i].cells.length; j++)
				{
					if (result[i].cells[j] == 2)
					{
						Widget panel = panels.getChild(i * 5 + j);
						graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
							questHelper.getConfig().targetOverlayColor().getGreen(),
							questHelper.getConfig().targetOverlayColor().getBlue(), 65));
						graphics.fill(panel.getBounds());
						graphics.setColor(questHelper.getConfig().targetOverlayColor());
						graphics.draw(panel.getBounds());
					}
				}
			}
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	@Override
	public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin, Requirement... requirement)
	{
		super.makeOverlayHint(panelComponent, plugin, requirement);
		Widget panels = client.getWidget(665, 32);
		if (result == null && panels != null)
		{
			String text = "Unable to calculate an answer for this puzzle. Good luck!";
			Color color = Color.RED;
			panelComponent.getChildren().add(LineComponent.builder()
				.left(text)
				.leftColor(color)
				.build());
		}
	}

	private PuzzleLine[][] createAllSolutions(int[] goalValues)
	{
		HashMap<Integer, PuzzleLine[]> allSolutions = constructSolutions();
		PuzzleLine[][] solutionsForGoals = new PuzzleLine[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++)
		{
			solutionsForGoals[i] = allSolutions.get(goalValues[i]);
		}
		return solutionsForGoals;
	}

	private PuzzleLine[] generateNewGrid()
	{
		PuzzleLine[] g = new PuzzleLine[SIZE];
		for (int i = 0; i < SIZE; i++)
		{
			g[i] = new PuzzleLine(5);
		}
		return g;
	}

	private HashMap<Integer, PuzzleLine[]> constructSolutions()
	{
		HashMap<Integer, PuzzleLine[]> allSolutions = new HashMap<>();
		allSolutions.put(0, getSolutions(new PuzzleLine(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)));
		allSolutions.put(1, getSolutions(new PuzzleLine(FILLED, EMPTY, EMPTY, EMPTY, EMPTY)));
		allSolutions.put(2, getSolutions(new PuzzleLine(EMPTY, FILLED, EMPTY, EMPTY, EMPTY)));
		allSolutions.put(3, getSolutions(new PuzzleLine(FILLED, FILLED, EMPTY, EMPTY, EMPTY), new PuzzleLine(EMPTY, EMPTY, FILLED, EMPTY, EMPTY)));
		allSolutions.put(4, getSolutions(new PuzzleLine(FILLED, EMPTY, FILLED, EMPTY, EMPTY), new PuzzleLine(EMPTY, EMPTY, EMPTY, FILLED, EMPTY)));
		allSolutions.put(5, getSolutions(new PuzzleLine(EMPTY, FILLED, FILLED, EMPTY, EMPTY), new PuzzleLine(FILLED, EMPTY, EMPTY, FILLED, EMPTY), new PuzzleLine(EMPTY, EMPTY, EMPTY, EMPTY, FILLED)));
		allSolutions.put(6, getSolutions(new PuzzleLine(FILLED, FILLED, FILLED, EMPTY, EMPTY), new PuzzleLine(EMPTY, FILLED, EMPTY, FILLED, EMPTY), new PuzzleLine(FILLED, EMPTY, EMPTY, EMPTY, FILLED)));
		allSolutions.put(7, getSolutions(new PuzzleLine(FILLED, FILLED, EMPTY, FILLED, EMPTY), new PuzzleLine(EMPTY, EMPTY, FILLED, FILLED, EMPTY), new PuzzleLine(EMPTY, FILLED, EMPTY, EMPTY, FILLED)));
		allSolutions.put(8, getSolutions(new PuzzleLine(FILLED, EMPTY, FILLED, FILLED, EMPTY), new PuzzleLine(FILLED, FILLED, EMPTY, EMPTY, FILLED), new PuzzleLine(EMPTY, EMPTY, FILLED, EMPTY, FILLED)));
		allSolutions.put(9, getSolutions(new PuzzleLine(EMPTY, FILLED, FILLED, FILLED, EMPTY), new PuzzleLine(FILLED, EMPTY, FILLED, EMPTY, FILLED), new PuzzleLine(EMPTY, EMPTY, EMPTY, FILLED, FILLED)));
		allSolutions.put(10, getSolutions(new PuzzleLine(FILLED, FILLED, FILLED, FILLED, EMPTY), new PuzzleLine(EMPTY, FILLED, FILLED, EMPTY, FILLED), new PuzzleLine(FILLED, EMPTY, EMPTY, FILLED, FILLED)));
		allSolutions.put(11, getSolutions(new PuzzleLine(FILLED, FILLED, FILLED, EMPTY, FILLED), new PuzzleLine(EMPTY, FILLED, EMPTY, FILLED, FILLED)));
		allSolutions.put(12, getSolutions(new PuzzleLine(FILLED, FILLED, EMPTY, FILLED, FILLED), new PuzzleLine(EMPTY, EMPTY, FILLED, FILLED, FILLED)));
		allSolutions.put(13, getSolutions(new PuzzleLine(FILLED, EMPTY, FILLED, FILLED, FILLED)));
		allSolutions.put(14, getSolutions(new PuzzleLine(EMPTY, FILLED, FILLED, FILLED, FILLED)));
		allSolutions.put(15, getSolutions(new PuzzleLine(FILLED, FILLED, FILLED, FILLED, FILLED)));

		return allSolutions;
	}


	private PuzzleLine[] getSolutions(PuzzleLine... solution)
	{

		return solution;
	}
}
