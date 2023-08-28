/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import net.runelite.client.ui.FontManager;

public class ChestCodeStep extends QuestStep
{

	private final int NUMBER_OF_DIALS;
	private final int SIZE_OF_LOOP;

	private final int[] buttonToPress;
	private final int[] distance;
	private final int[] goalValues;

	private boolean SHOULD_PRESS_CONFIRM;

	public ChestCodeStep(QuestHelper questHelper, String answer, int sizeOfLoop, int... targets)
	{
		super(questHelper, "Open the chest using the code " + answer + ".");
		SIZE_OF_LOOP = sizeOfLoop;
		NUMBER_OF_DIALS = targets.length;
		buttonToPress = new int[NUMBER_OF_DIALS];
		distance = new int[NUMBER_OF_DIALS];
		goalValues = new int[NUMBER_OF_DIALS];

		for (int i = 0; i < NUMBER_OF_DIALS; i++)
		{
			buttonToPress[i] = 0;
			distance[i] = 0;
			goalValues[i] = targets[i];
		}
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged varClientIntChanged)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		for (int i = 0; i < NUMBER_OF_DIALS; i++)
		{
			int START_VARCLIENTINT_POS = 1113;
			int varcIntID = START_VARCLIENTINT_POS + i;
			int START_DOWN_ARROW = 3;
			int ARROW_INTERVAL = 7;
			int arrowDownID = START_DOWN_ARROW + (ARROW_INTERVAL * i);
			int arrowUPID = START_DOWN_ARROW + 1 + (ARROW_INTERVAL * i);
			buttonToPress[i] = matchStateToSolution(varcIntID, goalValues[i], arrowDownID, arrowUPID);
			distance[i] = matchStateToDistance(varcIntID, goalValues[i]);
		}

		SHOULD_PRESS_CONFIRM = true;
		for (int d : distance)
		{
			if (d != 0)
			{
				SHOULD_PRESS_CONFIRM = false;
				break;
			}
		}
	}

	private int matchStateToSolution(final int slot, final int target, int arrowDownId, int arrowUpId)
	{
		int currentValue = client.getVarcIntValue(slot);
		int id = Math.floorMod(currentValue - target, SIZE_OF_LOOP) < Math.floorMod(target - currentValue, SIZE_OF_LOOP) ? arrowDownId : arrowUpId;
		if (currentValue != target) return id;
		return 0;
	}

	private int matchStateToDistance(final int slot, final int target)
	{
		int currentValue = client.getVarcIntValue(slot);
		return Math.min(Math.floorMod(currentValue - target, SIZE_OF_LOOP), Math.floorMod(target - currentValue, SIZE_OF_LOOP));
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		if (SHOULD_PRESS_CONFIRM)
		{
			Widget widget = client.getWidget(809, 5);
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
		for (int i = 0; i < NUMBER_OF_DIALS; i++)
		{
			int button = buttonToPress[i];
			if (button == 0) continue;
			Widget widget = client.getWidget(809, 4);
			if (widget != null)
			{
				Widget arrow = widget.getChild(button);
				if (arrow == null) break;
				graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
					questHelper.getConfig().targetOverlayColor().getGreen(),
					questHelper.getConfig().targetOverlayColor().getBlue(), 65));
				graphics.fill(arrow.getBounds());
				graphics.setColor(questHelper.getConfig().targetOverlayColor());
				graphics.draw(arrow.getBounds());

				int widgetX = arrow.getCanvasLocation().getX() + (arrow.getWidth() / 2) - 30;
				int widgetY = arrow.getCanvasLocation().getY() + (arrow.getHeight() / 2) + 4;
				Font font = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
				graphics.setFont(font);
				graphics.drawString(Integer.toString(distance[i]), widgetX, widgetY);

			}
		}
	}
}
