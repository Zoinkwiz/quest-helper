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
import net.runelite.api.events.VarClientIntChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrowChestPuzzleStep extends QuestStep
{
	final int GROUP_ID = 810;

	final int RESET_BUTTON_ID = 14;
	final int CONFIRM_BUTTON_ID = 15;

	int CURRENT_STATE = 0;
	int CURRENT_VALUE = 0;

	Integer[] expectedValues;

	Integer[] expectedButtonToPress;

	int BUTTON_ID_SHIFT = 6;

	Map<Integer, String> directionsFromValue = new HashMap<>();
	Map<Integer, Integer> directionToWidgetId = new HashMap<>();

	public ArrowChestPuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Solve the arrow puzzle by clicking them in the right order.");
		directionsFromValue.put(0, "Up");
		directionsFromValue.put(1, "Right");
		directionsFromValue.put(2, "Down");
		directionsFromValue.put(3, "Left");

		directionToWidgetId.put(0, 6);
		directionToWidgetId.put(1, 9);
		directionToWidgetId.put(2, 7);
		directionToWidgetId.put(3, 8);
	}

	public void setSolution(int... order)
	{
		List<Integer> solutionSteps = new ArrayList<>();
		solutionSteps.add(0);

		List<Integer> buttonsToPress = new ArrayList<>();

		int currentSum = 0;

		StringBuilder text = new StringBuilder("The solution to this puzzle is");
		// Worked out from order
		for (int i=0; i < order.length; i++) {
			double multiple = Math.pow(2, i*2);
			currentSum += (order[i] * (int) multiple);
			solutionSteps.add(currentSum);
			buttonsToPress.add(directionToWidgetId.get(order[i]));
			text.append(" ").append(directionsFromValue.get(order[i]));
		}

		text.append(".");
		setText(text.toString());

		buttonsToPress.add(CONFIRM_BUTTON_ID);
		expectedValues = solutionSteps.toArray(new Integer[0]);
		expectedButtonToPress = buttonsToPress.toArray(new Integer[0]);
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
		if (expectedButtonToPress == null) return;

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
