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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class GolemPuzzleStep extends QuestStep
{
	int firstTileForSwapping;
	int secondTileForSwapping;
	boolean completed = false;

	int[] goalPositions = {
			47, 62, 5, 52, 33, 27, 10, 16
		};

	public GolemPuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Drag the charges to the right tiles.");
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
		for (int i = VarbitID.DT2_WARMIND_CELL_POS_0; i <= VarbitID.DT2_WARMIND_CELL_POS_7; i++)
		{
			int currentPos = client.getVarbitValue(i);
			int goalPos = goalPositions[VarbitID.DT2_WARMIND_CELL_POS_7 - i];
			if (currentPos != goalPos)
			{
				completed = false;
				firstTileForSwapping = currentPos;
				secondTileForSwapping = goalPos;
				return;
			}
		}

		completed = true;
		setText("Click the power-on button to finish the puzzle.");
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(InterfaceID.Dt2WarmindPuzzle.SQUARES);
		if (completed)
		{
			Widget powerOnButton = client.getWidget(InterfaceID.Dt2WarmindPuzzle.BUTTON);
			if (powerOnButton == null) return;
			graphics.setColor(new Color(0, 255, 255, 65));
			graphics.fill(powerOnButton.getBounds());
			graphics.setColor(questHelper.getConfig().targetOverlayColor());
			graphics.draw(powerOnButton.getBounds());
			return;
		}
		if (widgetWrapper != null)
		{
			if (firstTileForSwapping != -1 && secondTileForSwapping != -1)
			{
				Widget widget1 = widgetWrapper.getChild(firstTileForSwapping);
				Widget widget2 = widgetWrapper.getChild(secondTileForSwapping);
				if (widget1 != null && widget2 != null)
				{
					Line2D.Double line = new Line2D.Double(
						widget1.getCanvasLocation().getX() + (widget1.getWidth() / 2.0f),
						widget1.getCanvasLocation().getY() + (widget1.getHeight() / 2.0f),
						widget2.getCanvasLocation().getX() + (widget2.getWidth() / 2.0f),
						widget2.getCanvasLocation().getY() + (widget2.getHeight() / 2.0f));

					graphics.setColor(new Color(0, 255, 255, 65));
					graphics.fill(widget1.getBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(widget1.getBounds());

					graphics.setColor(new Color(0, 255, 255, 65));
					graphics.fill(widget2.getBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(widget2.getBounds());

					graphics.setStroke(new BasicStroke(3));
					graphics.draw(line);
					drawArrowHead(graphics, line);
				}
			}
		}
	}

	private void drawArrowHead(Graphics2D g2d, Line2D.Double line) {
		AffineTransform tx = new AffineTransform();

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint( 0,4);
		arrowHead.addPoint( -6, -5);
		arrowHead.addPoint( 6,-5);

		tx.setToIdentity();
		double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle-Math.PI/2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}
}
