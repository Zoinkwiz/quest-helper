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
package com.questhelper.helpers.quests.secretsofthenorth;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class ArrowChestPuzzleStep extends QuestStep
{
	final int GROUP_ID = 810;
	final int UP_BUTTON_ID = 6;
	final int DOWN_BUTTON_ID = 7;
	final int LEFT_BUTTON_ID = 8;
	final int RIGHT_BUTTON_ID = 9;

	final int RESET_BUTTON_ID = 14;
	final int CONFIRM_BUTTON_ID = 15;

	int CURRENT_STATE = 0;
	int CURRENT_VALUE = 0;

	int[] expectedValues = new int[] {
		0, 0, 4, 52, 180, 436, 436
	};
	int[] expectedButtonToPress = new int[] {
		UP_BUTTON_ID, RIGHT_BUTTON_ID,
		LEFT_BUTTON_ID, DOWN_BUTTON_ID,
		RIGHT_BUTTON_ID, UP_BUTTON_ID,
		CONFIRM_BUTTON_ID
	};

	public ArrowChestPuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "The solution to this puzzle is UP RIGHT LEFT DOWN RIGHT UP.");
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged varClientIntChanged)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		CURRENT_STATE = client.getVarcIntValue(1120);
		CURRENT_VALUE = client.getVarcIntValue(1119);
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		int widgetToPress;
		if (CURRENT_STATE < expectedValues.length && expectedValues[CURRENT_STATE] == CURRENT_VALUE)
		{
			widgetToPress = expectedButtonToPress[CURRENT_STATE];
		}
		else
		{
			widgetToPress = RESET_BUTTON_ID;
		}
		Widget widget = client.getWidget(GROUP_ID, widgetToPress);
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
