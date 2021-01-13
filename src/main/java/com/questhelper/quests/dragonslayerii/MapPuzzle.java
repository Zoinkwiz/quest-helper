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
package com.questhelper.quests.dragonslayerii;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;

/* Approach:
* 1. Arrow pointing what to grab and where to move it to. Do for all pieces
* 2. Once all pieces in place, highlight all which need rotating */
public class MapPuzzle extends QuestStep
{
	int[] currentPositionsVarbits =
		{
			6156, 6168, 6159, 6178,   -1, 6203,
			6173, 6167, 6158, 6169, 6208, 6199,
			6204, 6174, 6183, 6176, 6162,   -1,
			-1,   6179,   -1, 6180, 6182, 6175,
			6184, 6170, 6209, 6190, 6191, 6216,
			  -1, 6217, 6198, 6171, 6200, 6211
		};


	int[] currentRotationVarbits =
		{
			6166, 6172, 6177, 6181,   -1, 6197,
			6201, 6202, 6160, 6161, 6163, 6164,
			6165, 6205, 6206, 6207, 6185,   -1,
			  -1, 6186,   -1, 6187, 6188, 6189,
			6210, 6212, 6213, 6214, 6215, 6192,
			  -1, 6193, 6194, 6195, 6196, 6218
		};

	int[] currentRotationValue = new int[36];

	int firstTileForSwapping = -1;
	int secondTileForSwapping = -1;

	public MapPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Drag to swap the highlighted tiles.");
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
		int[] currentTilePositions = new int[36];
		int[] currentRotationValueTmp = new int[36];

		int nullSlotsPassed = 0;

		for (int i=0; i < 36; i++)
		{
			// Tile values start from 1 rather than 0, so -1 for simplicity
			if (currentPositionsVarbits[i] == -1)
			{
				nullSlotsPassed++;
				continue;
			}

			currentTilePositions[i] = client.getVarbitValue(currentPositionsVarbits[i]) - 1;

			int expectedTileValue = i - nullSlotsPassed;
			if (currentTilePositions[i] != expectedTileValue)
			{
				firstTileForSwapping = i;
				for (int j = i+1; j < 36; j++)
				{
					if (currentPositionsVarbits[j] == -1)
					{
						nullSlotsPassed++;
						continue;
					}
					currentTilePositions[j] = client.getVarbitValue(currentPositionsVarbits[j]) - 1;
					if (currentTilePositions[j] == expectedTileValue)
					{
						secondTileForSwapping = j;
						setText("Drag to swap the highlighted tiles.");
						return;
					}
				}
			}
		}

		firstTileForSwapping = -1;
		secondTileForSwapping = -1;

		for (int i=0; i < 36; i++)
		{
			if (currentRotationVarbits[i] == -1)
			{
				currentRotationValueTmp[i] = 0;
				continue;
			}
			currentRotationValueTmp[i] = client.getVarbitValue(currentRotationVarbits[i]);
		}
		currentRotationValue = currentRotationValueTmp;
		setText("Click the highlighted tiles to rotate them to complete the puzzle.");
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin) {
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(305, 2);
		if (widgetWrapper != null)
		{
			if (firstTileForSwapping != -1 && secondTileForSwapping != -1)
			{
				Widget widget1 = widgetWrapper.getChild(firstTileForSwapping);
				Widget widget2 = widgetWrapper.getChild(secondTileForSwapping);
				if (widget1 != null)
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
				return;
			}

			for (int i = 0; i < 36; i++)
			{
				Widget widget = widgetWrapper.getChild(i);
				if (widget != null && currentRotationValue[i] != 0)
				{
					graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
						questHelper.getConfig().targetOverlayColor().getGreen(),
						questHelper.getConfig().targetOverlayColor().getBlue(), 65));
					graphics.fill(widget.getBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(widget.getBounds());

					int widgetX = widget.getCanvasLocation().getX() + (widget.getWidth() / 2) - 4;
					int widgetY = widget.getCanvasLocation().getY() + (widget.getHeight() / 2) + 4;
					Font font = FontManager.getRunescapeFont().deriveFont(Font.BOLD, 16);
					graphics.setFont(font);
					graphics.drawString(Integer.toString((4 - currentRotationValue[i]) % 4), widgetX, widgetY);
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
