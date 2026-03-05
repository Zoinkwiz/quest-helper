/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2020, Twinkle <https://github.com/twinkle-is-dum>
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
package com.questhelper.helpers.quests.tribaltotem;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PuzzleStep extends QuestStep
{
	private final int ENTRY_ONE = 11; // K
	private final int ENTRY_TWO = 21; // U
	private final int ENTRY_THREE = 18; // R
	private final int ENTRY_FOUR = 20; // T

	private final int ARROW_ONE_LEFT = InterfaceID.TribalDoor.TRIBALA_LEFT;
	private final int ARROW_ONE_RIGHT = InterfaceID.TribalDoor.TRIBALA_RIGHT;
	private final int ARROW_TWO_LEFT = InterfaceID.TribalDoor.TRIBALB_LEFT;
	private final int ARROW_TWO_RIGHT = InterfaceID.TribalDoor.TRIBALB_RIGHT;
	private final int ARROW_THREE_LEFT = InterfaceID.TribalDoor.TRIBALC_LEFT;
	private final int ARROW_THREE_RIGHT = InterfaceID.TribalDoor.TRIBALC_RIGHT;
	private final int ARROW_FOUR_LEFT = InterfaceID.TribalDoor.TRIBALD_LEFT;
	private final int ARROW_FOUR_RIGHT = InterfaceID.TribalDoor.TRIBALD_RIGHT;

	private final int COMPLETE = InterfaceID.TribalDoor.TRIBALENTER;

	private final HashMap<Integer, Integer> highlightButtons = new HashMap<>();
	private final HashMap<Integer, Integer> distance = new HashMap<>();

	public PuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted arrows to move the slots to the solution 'KURT'.");
		highlightButtons.put(1, ARROW_ONE_RIGHT);
		highlightButtons.put(2, ARROW_TWO_RIGHT);
		highlightButtons.put(3, ARROW_THREE_RIGHT);
		highlightButtons.put(4, ARROW_FOUR_RIGHT);

		distance.put(1, 0);
		distance.put(2, 0);
		distance.put(3, 0);
		distance.put(4, 0);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		highlightButtons.replace(1, matchStateToSolution(VarbitID.TOTEMQUEST_COMBODOOR_CODE1, ENTRY_ONE, ARROW_ONE_RIGHT, ARROW_ONE_LEFT));
		highlightButtons.replace(2, matchStateToSolution(VarbitID.TOTEMQUEST_COMBODOOR_CODE2, ENTRY_TWO, ARROW_TWO_RIGHT, ARROW_TWO_LEFT));
		highlightButtons.replace(3, matchStateToSolution(VarbitID.TOTEMQUEST_COMBODOOR_CODE3, ENTRY_THREE, ARROW_THREE_RIGHT, ARROW_THREE_LEFT));
		highlightButtons.replace(4, matchStateToSolution(VarbitID.TOTEMQUEST_COMBODOOR_CODE4, ENTRY_FOUR, ARROW_FOUR_RIGHT, ARROW_FOUR_LEFT));

		distance.replace(1, matchStateToDistance(VarbitID.TOTEMQUEST_COMBODOOR_CODE1, ENTRY_ONE));
		distance.replace(2, matchStateToDistance(VarbitID.TOTEMQUEST_COMBODOOR_CODE2, ENTRY_TWO));
		distance.replace(3, matchStateToDistance(VarbitID.TOTEMQUEST_COMBODOOR_CODE3, ENTRY_THREE));
		distance.replace(4, matchStateToDistance(VarbitID.TOTEMQUEST_COMBODOOR_CODE4, ENTRY_FOUR));


		if (highlightButtons.get(1) + highlightButtons.get(2) + highlightButtons.get(3) + highlightButtons.get(4) == 0)
		{
			highlightButtons.put(5, COMPLETE);
		}
		else
		{
			highlightButtons.put(5, 0);
		}
	}

	private int matchStateToSolution(int varbitID, int target, int arrowRightId, int arrowLeftId)
	{
		int currentPos = client.getVarbitValue(varbitID);
		int id = Math.floorMod(currentPos - target, 26) < Math.floorMod(target - currentPos, 26) ? arrowLeftId : arrowRightId;
		if (currentPos != target) return id;
		return 0;
	}

	private int matchStateToDistance(int varbitID, int target)
	{
		int current = client.getVarbitValue(varbitID);
		return Math.min(Math.floorMod(current - target, 26), Math.floorMod(target - current, 26));
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

			Widget widget = client.getWidget(entry.getValue());
			if (widget != null)
			{
				graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
					questHelper.getConfig().targetOverlayColor().getGreen(),
					questHelper.getConfig().targetOverlayColor().getBlue(), 65));
				graphics.fill(widget.getBounds());
				graphics.setColor(questHelper.getConfig().targetOverlayColor());
				graphics.draw(widget.getBounds());

				if (distance.get(entry.getKey()) != null)
				{
					int widgetX = widget.getCanvasLocation().getX() + (widget.getWidth() / 2) - 4;
					int widgetY = widget.getCanvasLocation().getY() + (widget.getHeight() / 2) + 4;
					Font font = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
					graphics.setFont(font);
					graphics.drawString(Integer.toString(distance.get(entry.getKey())), widgetX, widgetY);
				}
			}
		}
	}
}
