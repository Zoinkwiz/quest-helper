/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.steps.overlay;

import com.questhelper.QuestHelperWorldMapPoint;
import com.questhelper.steps.tools.QuestPerspective;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class DirectionArrow
{
	public static void renderMinimapArrow(Graphics2D graphics, Client client, WorldPoint worldPoint, Color color)
	{
		final int MAX_DRAW_DISTANCE = 16;
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		WorldPoint playerLocation = player.getWorldLocation();

		WorldPoint wp = QuestPerspective.getInstanceWorldPoint(client, worldPoint);

		if (wp == null)
		{
			return;
		}

		if (wp.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE)
		{
			createMinimapDirectionArrow(graphics, client, wp, color);
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, wp);
		if (lp == null)
		{
			return;
		}

		Point posOnMinimap = Perspective.localToMinimap(client, lp);
		if (posOnMinimap == null)
		{
			return;
		}

		Line2D.Double line = new Line2D.Double(posOnMinimap.getX(), posOnMinimap.getY() - 18, posOnMinimap.getX(),
			posOnMinimap.getY() - 8);

		drawMinimapArrow(graphics, line, color);
	}

	protected static void createMinimapDirectionArrow(Graphics2D graphics, Client client, WorldPoint wp, Color color)
	{
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return;
		}

		if (wp == null)
		{
			return;
		}

		Point playerPosOnMinimap = player.getMinimapLocation();

		Point destinationPosOnMinimap = QuestPerspective.getMinimapPoint(client, player.getWorldLocation(), wp);

		if (playerPosOnMinimap == null || destinationPosOnMinimap == null)
		{
			return;
		}

		double xDiff = playerPosOnMinimap.getX() - destinationPosOnMinimap.getX();
		double yDiff = destinationPosOnMinimap.getY() - playerPosOnMinimap.getY();
		double angle = Math.atan2(yDiff, xDiff);

		int startX = (int) (playerPosOnMinimap.getX() - (Math.cos(angle) * 55));
		int startY = (int) (playerPosOnMinimap.getY() + (Math.sin(angle) * 55));

		int endX = (int) (playerPosOnMinimap.getX() - (Math.cos(angle) * 65));
		int endY = (int) (playerPosOnMinimap.getY() + (Math.sin(angle) * 65));

		Line2D.Double line = new Line2D.Double(startX, startY, endX, endY);

		drawMinimapArrow(graphics, line, color);
	}

	public static void drawWorldArrow(Graphics2D graphics, Color color, int startX, int startY)
	{
		Line2D.Double line = new Line2D.Double(startX, startY - 13, startX, startY);

		int headWidth = 5;
		int headHeight = 4;
		int lineWidth = 9;

		drawArrow(graphics, line, color, lineWidth, headHeight, headWidth);
	}

	public static void drawMinimapArrow(Graphics2D graphics, Line2D.Double line, Color color)
	{
		drawArrow(graphics, line, color, 6, 2, 2);
	}

	public static void drawArrow(Graphics2D graphics, Line2D.Double line, Color color, int width, int tipHeight,
								 int tipWidth)
	{
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(width));
		graphics.draw(line);
		drawWorldArrowHead(graphics, line, tipHeight, tipWidth);

		graphics.setColor(color);
		graphics.setStroke(new BasicStroke(width - 3));
		graphics.draw(line);
		drawWorldArrowHead(graphics, line, tipHeight - 2, tipWidth - 2);
		graphics.setStroke(new BasicStroke(1));
	}


	public static void drawWorldArrowHead(Graphics2D g2d, Line2D.Double line, int extraSizeHeight, int extraSizeWidth)
	{
		AffineTransform tx = new AffineTransform();

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint(0, 6 + extraSizeHeight);
		arrowHead.addPoint(-6 - extraSizeWidth, -1 - extraSizeHeight);
		arrowHead.addPoint(6 + extraSizeWidth, -1 - extraSizeHeight);

		tx.setToIdentity();
		double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle - Math.PI / 2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}

	public static void drawLineArrowHead(Graphics2D g2d, Line2D.Double line) {
		AffineTransform tx = new AffineTransform();

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint( 0,0);
		arrowHead.addPoint( -3, -6);
		arrowHead.addPoint( 3,-6);

		tx.setToIdentity();
		double angle = Math.atan2(line.y2-line.y1, line.x2-line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle-Math.PI/2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}

	public static void drawLine(Graphics2D graphics, Line2D.Double line, Color color, Rectangle clippingRegion)
	{
		graphics.setStroke(new BasicStroke(1));
		graphics.setClip(clippingRegion);
		graphics.setColor(color);
		graphics.draw(line);

		drawLineArrowHead(graphics, line);
	}

	public static void renderWorldMapArrow(Rectangle mapViewArea, Point drawPoint, QuestHelperWorldMapPoint mapPoint)
	{
		if (mapViewArea != null &&  drawPoint != null && !mapViewArea.contains(drawPoint.getX(), drawPoint.getY()))
		{
			if (drawPoint.getX() < mapViewArea.getMinX())
			{
				if (drawPoint.getY() < mapViewArea.getMinY())
				{
					mapPoint.rotateArrow(225);
				}
				else if (drawPoint.getY() > mapViewArea.getMaxY())
				{
					mapPoint.rotateArrow(135);
				}
				else
				{
					mapPoint.rotateArrow(180);
				}
			}
			else if (drawPoint.getX() > mapViewArea.getMaxX())
			{
				if (drawPoint.getY() < mapViewArea.getMinY())
				{
					mapPoint.rotateArrow(315);
				}
				else if (drawPoint.getY() > mapViewArea.getMaxY())
				{
					mapPoint.rotateArrow(45);
				}
				else
				{
					mapPoint.rotateArrow(0);
				}
			}
			else
			{
				if (drawPoint.getY() < mapViewArea.getMinY())
				{
					mapPoint.rotateArrow(270);
				}
				else if (drawPoint.getY() > mapViewArea.getMaxY())
				{
					mapPoint.rotateArrow(90);
				}
			}
		}
	}
}
