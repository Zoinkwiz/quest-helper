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
package com.questhelper.helpers.quests.recruitmentdrive;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;

public class DoorPuzzle extends QuestStep
{
	private final HashMap<Integer, Integer> highlightButtons = new HashMap<>();
	private final HashMap<Integer, Integer> distance = new HashMap<>();

	private Character ENTRY_ONE;
	private Character ENTRY_TWO;
	private Character ENTRY_THREE;
	private Character ENTRY_FOUR;

	public DoorPuzzle(QuestHelper questHelper, String word)
	{
		super(questHelper, "Click the highlighted arrows to move the slots to the solution.");
		ENTRY_ONE = word.charAt(0);
		ENTRY_TWO = word.charAt(1);
		ENTRY_THREE = word.charAt(2);
		ENTRY_FOUR = word.charAt(3);

		highlightButtons.put(1, InterfaceID.RdCombolock.RDA_RIGHT);
		highlightButtons.put(2, InterfaceID.RdCombolock.RDB_RIGHT);
		highlightButtons.put(3, InterfaceID.RdCombolock.RDC_RIGHT);
		highlightButtons.put(4, InterfaceID.RdCombolock.RDD_RIGHT);

		distance.put(1, 0);
		distance.put(2, 0);
		distance.put(3, 0);
		distance.put(4, 0);
	}

	@Override
	public void startUp()
	{
		super.startUp();

		// Recalculate once when the step starts up to ensure highlights aren't wrong for a tick
		updateSolvedPositionState();
	}

	public void updateWord(String word)
	{
		ENTRY_ONE = word.charAt(0);
		ENTRY_TWO = word.charAt(1);
		ENTRY_THREE = word.charAt(2);
		ENTRY_FOUR = word.charAt(3);
		setText("Click the highlighted arrows to move the slots to the solution. The answer is " + word + ".");
		updateSolvedPositionState();
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{

		highlightButtons.replace(1, matchStateToSolution(InterfaceID.RdCombolock.RDA, ENTRY_ONE, InterfaceID.RdCombolock.RDA_RIGHT, InterfaceID.RdCombolock.RDA_LEFT));
		highlightButtons.replace(2, matchStateToSolution(InterfaceID.RdCombolock.RDB, ENTRY_TWO, InterfaceID.RdCombolock.RDB_RIGHT, InterfaceID.RdCombolock.RDB_LEFT));
		highlightButtons.replace(3, matchStateToSolution(InterfaceID.RdCombolock.RDC, ENTRY_THREE, InterfaceID.RdCombolock.RDC_RIGHT, InterfaceID.RdCombolock.RDC_LEFT));
		highlightButtons.replace(4, matchStateToSolution(InterfaceID.RdCombolock.RDD, ENTRY_FOUR, InterfaceID.RdCombolock.RDD_RIGHT, InterfaceID.RdCombolock.RDD_LEFT));

		distance.replace(1, matchStateToDistance(InterfaceID.RdCombolock.RDA, ENTRY_ONE));
		distance.replace(2, matchStateToDistance(InterfaceID.RdCombolock.RDB, ENTRY_TWO));
		distance.replace(3, matchStateToDistance(InterfaceID.RdCombolock.RDC, ENTRY_THREE));
		distance.replace(4, matchStateToDistance(InterfaceID.RdCombolock.RDD, ENTRY_FOUR));

		if (highlightButtons.get(1) + highlightButtons.get(2) + highlightButtons.get(3) + highlightButtons.get(4) == 0)
		{
			highlightButtons.put(5, InterfaceID.RdCombolock.RDENTER);
		}
		else
		{
			highlightButtons.put(5, 0);
		}
	}

	private int matchStateToSolution(int slot, Character target, int arrowRightId, int arrowLeftId)
	{
		Widget widget = client.getWidget(slot);
		if (widget == null)
		{
			return 0;
		}
		char current = widget.getText().charAt(0);
		int currentPos = (int) current - (int) 'A';
		int id = Math.floorMod(currentPos - target, 26) < Math.floorMod(target - currentPos, 26) ? arrowRightId : arrowLeftId;
		if (current != target)
		{
			return id;
		}
		return 0;
	}

	private int matchStateToDistance(int slot, Character target)
	{
		Widget widget = client.getWidget(slot);
		if (widget == null)
		{
			return 0;
		}
		char current = widget.getText().charAt(0);
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
