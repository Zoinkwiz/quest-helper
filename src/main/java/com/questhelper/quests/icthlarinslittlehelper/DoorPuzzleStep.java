/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.icthlarinslittlehelper;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class DoorPuzzleStep extends QuestStep
{
	int[] currentState = new int[20];

	int[] lastState = new int[20];

	int[] clickSquares = new int[20];

	RowSolution[] solutions =
		{
			new RowSolution(new int[]{0, 0, 1, 0, 0}, new int[]{1, 0, 0, 0, 1}),
			new RowSolution(new int[]{1, 0, 0, 0, 1}, new int[]{0, 0, 1, 0, 0}),
			new RowSolution(new int[]{0, 1, 1, 1, 0}, new int[]{1, 0, 1, 1, 0}),
			new RowSolution(new int[]{1, 1, 0, 1, 1}, new int[]{0, 0, 0, 1, 1}),
			new RowSolution(new int[]{0, 1, 0, 1, 0}, new int[]{1, 0, 1, 0, 1}),

			new RowSolution(new int[]{1, 1, 0, 0, 0}, new int[]{0, 0, 0, 1, 0}),
			new RowSolution(new int[]{0, 0, 0, 1, 1}, new int[]{0, 1, 0, 0, 0}),

			new RowSolution(new int[]{1, 1, 1, 0, 0}, new int[]{0, 0, 0, 0, 1}),
			new RowSolution(new int[]{0, 0, 1, 1, 1}, new int[]{1, 0, 0, 0, 0}),

			new RowSolution(new int[]{0, 1, 1, 0, 1}, new int[]{0, 1, 1, 0, 0}),
			new RowSolution(new int[]{1, 0, 1, 1, 0}, new int[]{0, 0, 1, 1, 0}),

			new RowSolution(new int[]{1, 0, 0, 1, 0}, new int[]{0, 0, 1, 0, 1}),
			new RowSolution(new int[]{0, 1, 0, 0, 1}, new int[]{1, 0, 1, 0, 0}),

			new RowSolution(new int[]{1, 0, 1, 1, 0}, new int[]{0, 0, 1, 1, 0}),
			new RowSolution(new int[]{0, 1, 1, 0, 1}, new int[]{0, 1, 1, 0, 0}),

			new RowSolution(new int[]{0, 0, 0, 0, 0}, new int[]{0, 1, 0, 0, 1}),
			new RowSolution(new int[]{1, 1, 1, 1, 1}, new int[]{0, 0, 0, 0, 0})
		};

	public DoorPuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted boxes to turn the squares to solve the puzzle.");
	}

	@Override
	public void startUp()
	{
		updateSolvedPositionState();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		int START_VARBIT_ID = 420;
		for (int i = 0; i < 20; i++)
		{
			currentState[i] = (1 + client.getVarbitValue(START_VARBIT_ID + i)) % 2;
		}

		if (Arrays.equals(currentState, lastState))
		{
			return;
		}
		else
		{
			lastState = currentState.clone();
		}

		for (int y = 0; y < 4; y++)
		{
			int[] row = new int[5];
			System.arraycopy(currentState, y * 5, row, 0, 5);

			for (RowSolution solution : solutions)
			{
				int[] result = solution.checkMatch(row);
				if (result != null)
				{
					System.arraycopy(result, 0, clickSquares, y * 5, 5);

					updateCurrentState(result, y + 1);
					break;
				}
			}
		}
	}

	public void updateCurrentState(int[] result, int clickY)
	{
		for (int cell = 0; cell < result.length; cell++)
		{
			if (result[cell] == 1)
			{

				if (clickY <= 3)
				{
					currentState[clickY * 5 + cell] = (currentState[clickY * 5 + cell] + 1) % 2;
				}
				if (clickY < 3)
				{
					currentState[(clickY + 1) * 5 + cell] = (currentState[(clickY + 1) * 5 + cell] + 1) % 2;
				}
				if (cell != 4)
				{
					if (clickY <= 3)
					{
						currentState[clickY * 5 + cell + 1] = (currentState[clickY * 5 + cell + 1] + 1) % 2;
					}
					if (clickY < 3)
					{
						currentState[(clickY + 1) * 5 + cell + 1] = (currentState[(clickY + 1) * 5 + cell + 1] + 1) % 2;
					}
				}

				if (cell != 0)
				{
					if (clickY <= 3)
					{
						currentState[clickY * 5 + cell - 1] = (currentState[clickY * 5 + cell - 1] + 1) % 2;
					}
					if (clickY < 3)
					{
						currentState[(clickY + 1) * 5 + cell - 1] = (currentState[(clickY + 1) * 5 + cell - 1] + 1) % 2;
					}
				}
			}
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(147, 0);
		if (widgetWrapper != null)
		{
			for (int i = 0; i < 20; i++)
			{
				if (clickSquares[i] == 1)
				{
					int START_WIDGET_ID = 9;
					Widget widget = client.getWidget(147, START_WIDGET_ID + i);
					if (widget != null)
					{
						graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
							questHelper.getConfig().targetOverlayColor().getGreen(),
							questHelper.getConfig().targetOverlayColor().getBlue(), 65));
						graphics.fill(widget.getBounds());
						graphics.setColor(questHelper.getConfig().targetOverlayColor());
						graphics.draw(widget.getBounds());
					}
				}
			}
		}
	}
}

class RowSolution
{
	int[] rowValue;
	int[] solution;

	public RowSolution(int[] rowValue, int[] solution)
	{
		this.rowValue = rowValue;
		this.solution = solution;
	}

	public int[] checkMatch(int[] values)
	{
		if (Arrays.equals(rowValue, values))
		{
			return solution;
		}
		return null;
	}
}
