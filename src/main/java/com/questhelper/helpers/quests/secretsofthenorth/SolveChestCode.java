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
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SolveChestCode extends QuestStep
{

	private final int ARROW_ONE_DOWN = 3;
	private final int ARROW_ONE_UP = 4;
	private final int ARROW_TWO_DOWN = 10;
	private final int ARROW_TWO_UP = 11;
	private final int ARROW_THREE_DOWN = 17;
	private final int ARROW_THREE_UP = 18;
	private final int ARROW_FOUR_DOWN = 24;
	private final int ARROW_FOUR_UP = 25;

	private final int COMPLETE = 5;

	private final HashMap<Integer, Integer> highlightButtons = new HashMap<>();
	private final HashMap<Integer, Integer> distance = new HashMap<>();

	public SolveChestCode(QuestHelper questHelper)
	{
		super(questHelper, "Open the north chest using the code \"7402\".");
		highlightButtons.put(1, ARROW_ONE_DOWN);
		highlightButtons.put(2, ARROW_TWO_DOWN);
		highlightButtons.put(3, ARROW_THREE_DOWN);
		highlightButtons.put(4, ARROW_FOUR_DOWN);

		distance.put(1, 0);
		distance.put(2, 0);
		distance.put(3, 0);
		distance.put(4, 0);
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged varClientIntChanged)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		final int SLOT_ONE = 1113;
		final int SLOT_TWO = 1114;
		final int SLOT_THREE = 1115;
		final int SLOT_FOUR = 1116;

		final int ENTRY_ONE = 7;
		final int ENTRY_TWO = 4;
		final int ENTRY_THREE = 0;
		final int ENTRY_FOUR = 2;

		highlightButtons.replace(1, matchStateToSolution(SLOT_ONE, ENTRY_ONE, ARROW_ONE_DOWN, ARROW_ONE_UP));
		highlightButtons.replace(2, matchStateToSolution(SLOT_TWO, ENTRY_TWO, ARROW_TWO_DOWN, ARROW_TWO_UP));
		highlightButtons.replace(3, matchStateToSolution(SLOT_THREE, ENTRY_THREE, ARROW_THREE_DOWN, ARROW_THREE_UP));
		highlightButtons.replace(4, matchStateToSolution(SLOT_FOUR, ENTRY_FOUR, ARROW_FOUR_DOWN, ARROW_FOUR_UP));

		distance.replace(1, matchStateToDistance(SLOT_ONE, ENTRY_ONE));
		distance.replace(2, matchStateToDistance(SLOT_TWO, ENTRY_TWO));
		distance.replace(3, matchStateToDistance(SLOT_THREE, ENTRY_THREE));
		distance.replace(4, matchStateToDistance(SLOT_FOUR, ENTRY_FOUR));

		if (highlightButtons.get(1) + highlightButtons.get(2) + highlightButtons.get(3) + highlightButtons.get(4) == 0)
		{
			highlightButtons.put(5, COMPLETE);
		}
		else
		{
			highlightButtons.put(5, 0);
		}
	}

	private int matchStateToSolution(final int slot, final int target, int arrowRightId, int arrowLeftId)
	{
		int currentValue = client.getVarcIntValue(slot);
		int id = Math.floorMod(currentValue - target, 10) < Math.floorMod(target - currentValue, 10) ? arrowRightId : arrowLeftId;
		if (currentValue != target) return id;
		return 0;
	}

	private int matchStateToDistance(final int slot, final int target)
	{
		int currentValue = client.getVarcIntValue(slot);
		return Math.min(Math.floorMod(currentValue - target, 10), Math.floorMod(target - currentValue, 10));
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		for (Map.Entry<Integer, Integer> entry : highlightButtons.entrySet())
		{
			if (entry.getValue() == 0)
			{
				continue;
			}

			if (entry.getKey() == 5)
			{
				Widget widget = client.getWidget(InterfaceID.CombinationLock.CONFIRM_BUTTON);
				if (widget != null)
				{
					graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
						questHelper.getConfig().targetOverlayColor().getGreen(),
						questHelper.getConfig().targetOverlayColor().getBlue(), 65));
					graphics.fill(widget.getBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(widget.getBounds());
				}
				continue;
			}

			Widget widget = client.getWidget(InterfaceID.CombinationLock.LOCK);
			if (widget != null)
			{
				Widget arrow = widget.getChild(entry.getValue());
				if (arrow == null) break;
				graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
					questHelper.getConfig().targetOverlayColor().getGreen(),
					questHelper.getConfig().targetOverlayColor().getBlue(), 65));
				graphics.fill(arrow.getBounds());
				graphics.setColor(questHelper.getConfig().targetOverlayColor());
				graphics.draw(arrow.getBounds());

				if (distance.get(entry.getKey()) != null)
				{
					int widgetX = arrow.getCanvasLocation().getX() + (arrow.getWidth() / 2) - 30;
					int widgetY = arrow.getCanvasLocation().getY() + (arrow.getHeight() / 2) + 4;
					Font font = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
					graphics.setFont(font);
					graphics.drawString(Integer.toString(distance.get(entry.getKey())), widgetX, widgetY);
				}
			}
		}
	}
}
