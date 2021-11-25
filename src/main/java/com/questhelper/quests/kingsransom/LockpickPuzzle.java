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
package com.questhelper.quests.kingsransom;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class LockpickPuzzle extends QuestStep
{
	int[] TUMBLER_ANSWERS = new int[]{3894, 3895, 3896, 3897};
	int[] TUMBLER_WIDGETS = new int[]{20, 21, 22, 23};
	int[] TUMBLER_CURRENT = new int[]{3901, 3902, 3903, 3904};
	int CURRENT_TUMBLER = 3905;
	int UP_WIDGET = 12;
	int DOWN_WIDGET = 13;
	int TRY_LOCK = 14;

	int highlightChildID;

	public LockpickPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted boxes to solve the puzzle. The solution is:");
	}

	@Override
	public void startUp()
	{
		updateSolvedPositionState();
		this.setText("Click the highlighted boxes to solve the puzzle. The solution is:");
		this.addText("Tumbler 1: " + client.getVarbitValue(TUMBLER_ANSWERS[0]) + ".");
		this.addText("Tumbler 2: " + client.getVarbitValue(TUMBLER_ANSWERS[1]) + ".");
		this.addText("Tumbler 3: " + client.getVarbitValue(TUMBLER_ANSWERS[2]) + ".");
		this.addText("Tumbler 4: " + client.getVarbitValue(TUMBLER_ANSWERS[3]) + ".");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		int current0 = client.getVarbitValue(TUMBLER_CURRENT[0]);
		int answer0 = client.getVarbitValue(TUMBLER_ANSWERS[0]);
		if (current0 != answer0)
		{
			updateWidget(0, current0, answer0);
			return;
		}
		int current1 = client.getVarbitValue(TUMBLER_CURRENT[1]);
		int answer1 = client.getVarbitValue(TUMBLER_ANSWERS[1]);
		if (current1 != answer1)
		{
			updateWidget(1, current1, answer1);
			return;
		}

		int current2 = client.getVarbitValue(TUMBLER_CURRENT[2]);
		int answer2 = client.getVarbitValue(TUMBLER_ANSWERS[2]);
		if (current2 != answer2)
		{
			updateWidget(2, current2, answer2);
			return;
		}
		int current3 = client.getVarbitValue(TUMBLER_CURRENT[3]);
		int answer3 = client.getVarbitValue(TUMBLER_ANSWERS[3]);
		if (current3 != answer3)
		{
			updateWidget(3, current3, answer3);
			return;
		}

		highlightChildID = TRY_LOCK;
	}

	private void updateWidget(int widgetID, int currentVal, int answer)
	{
		int currentTumbler = client.getVarbitValue(CURRENT_TUMBLER);
		if (currentTumbler != widgetID + 1)
		{
			highlightChildID = TUMBLER_WIDGETS[widgetID];
		}
		else if (currentVal > answer)
		{
			highlightChildID = DOWN_WIDGET;
		}
		else
		{
			highlightChildID = UP_WIDGET;
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(588, highlightChildID);
		if (widgetWrapper != null)
		{
			graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
				questHelper.getConfig().targetOverlayColor().getGreen(),
				questHelper.getConfig().targetOverlayColor().getBlue(), 65));
			graphics.fill(widgetWrapper.getBounds());
			graphics.setColor(questHelper.getConfig().targetOverlayColor());
			graphics.draw(widgetWrapper.getBounds());
		}
	}
}
