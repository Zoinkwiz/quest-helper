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
package com.questhelper.quests.theforsakentower;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

/* Possible improvement would be to show a number on each square indicating turns remaining to solved position */
public class PowerPuzzle extends QuestStep
{
	int[] solvedPositions =
		{
			0, 0, 1, 0, 4, 1,
			3, 0, 4, 3, 0, 1,
			0, 3, 4, 3, 3, 1,
			1, 4, 2, 0, 2, 2,
			2, 3, 2, 0, 0, 1,
			3, 4, 4, 0, 1, 2
		};

	boolean[] currentPositionCorrect = new boolean[36];

	public PowerPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted boxes to turn the squares to solve the puzzle.");
	}

	@Override
	public void startUp()
	{
		updateSolvedPositionState();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		for (int i=0; i < 36; i++)
		{
			int currentPos = client.getVarbitValue(7811+i);
			if (solvedPositions[i] == 4)
			{
				currentPositionCorrect[i] = currentPos == 0 || currentPos == 2;
			}
			else
			{
				currentPositionCorrect[i] = client.getVarbitValue(7811 + i) == solvedPositions[i];
			}
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin) {
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(624, 2);
		if (widgetWrapper != null)
		{
			for (int i = 0; i < 36; i++)
			{
				Widget widget = widgetWrapper.getChild(i);
				if (widget != null && !currentPositionCorrect[i])
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
