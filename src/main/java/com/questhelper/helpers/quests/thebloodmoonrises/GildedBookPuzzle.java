// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

/// This step is for the gilded bookcase swapping puzzle.
public class GildedBookPuzzle extends QuestStep
{
	int firstTileForSwapping = -1;
	int secondTileForSwapping = -1;

	public GildedBookPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Rearrange the books to the correct order.");
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
		var slot1 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_1);
		var slot2 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_2);
		var slot3 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_3);
		var slot4 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_4);
		var slot5 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_5);
		var slot6 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_6);
		var slot7 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_7);
		var slot8 = client.getVarbitValue(VarbitID.CASTLE_DRAKAN_PUZZLE_BOOK_SLOT_8);
		if (slot1 != 1)
		{
			secondTileForSwapping = 0;
			if (slot2 == 1)
			{
				firstTileForSwapping = 1;
			}
			else if (slot3 == 1)
			{
				firstTileForSwapping = 2;
			}
			else if (slot4 == 1)
			{
				firstTileForSwapping = 3;
			}
			else if (slot5 == 1)
			{
				firstTileForSwapping = 4;
			}
			else if (slot6 == 1)
			{
				firstTileForSwapping = 5;
			}
			else if (slot7 == 1)
			{
				firstTileForSwapping = 6;
			}
			else if (slot8 == 1)
			{
				firstTileForSwapping = 7;
			}
		}
		else if (slot2 != 2)
		{
			secondTileForSwapping = 1;
			if (slot3 == 2)
			{
				firstTileForSwapping = 2;
			}
			else if (slot4 == 2)
			{
				firstTileForSwapping = 3;
			}
			else if (slot5 == 2)
			{
				firstTileForSwapping = 4;
			}
			else if (slot6 == 2)
			{
				firstTileForSwapping = 5;
			}
			else if (slot7 == 2)
			{
				firstTileForSwapping = 6;
			}
			else if (slot8 == 2)
			{
				firstTileForSwapping = 7;
			}
		}
		else if (slot3 != 3)
		{
			secondTileForSwapping = 2;
			if (slot4 == 3)
			{
				firstTileForSwapping = 3;
			}
			else if (slot5 == 3)
			{
				firstTileForSwapping = 4;
			}
			else if (slot6 == 3)
			{
				firstTileForSwapping = 5;
			}
			else if (slot7 == 3)
			{
				firstTileForSwapping = 6;
			}
			else if (slot8 == 3)
			{
				firstTileForSwapping = 7;
			}
		}
		else if (slot4 != 4)
		{
			secondTileForSwapping = 3;
			if (slot5 == 4)
			{
				firstTileForSwapping = 4;
			}
			else if (slot6 == 4)
			{
				firstTileForSwapping = 5;
			}
			else if (slot7 == 4)
			{
				firstTileForSwapping = 6;
			}
			else if (slot8 == 4)
			{
				firstTileForSwapping = 7;
			}
		}
		else if (slot5 != 5)
		{
			secondTileForSwapping = 4;
			if (slot6 == 5)
			{
				firstTileForSwapping = 5;
			}
			else if (slot7 == 5)
			{
				firstTileForSwapping = 6;
			}
			else if (slot8 == 5)
			{
				firstTileForSwapping = 7;
			}
		}
		else if (slot6 != 6)
		{
			secondTileForSwapping = 5;
			if (slot7 == 6)
			{
				firstTileForSwapping = 6;
			}
			else if (slot8 == 6)
			{
				firstTileForSwapping = 7;
			}
		}
		else if (slot7 != 7)
		{
			secondTileForSwapping = 6;
			if (slot8 == 7)
			{
				firstTileForSwapping = 7;
			}
		}
		else
		{
			setText("Close the widget");
			firstTileForSwapping = -1;
			secondTileForSwapping = -1;
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(InterfaceID.CastleDrakanBookcase.SHELF);
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
				return;
			}
		}
	}

	private void drawArrowHead(Graphics2D g2d, Line2D.Double line)
	{
		AffineTransform tx = new AffineTransform();

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint(0, 4);
		arrowHead.addPoint(-6, -5);
		arrowHead.addPoint(6, -5);

		tx.setToIdentity();
		double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
		tx.translate(line.x2, line.y2);
		tx.rotate((angle - Math.PI / 2d));

		Graphics2D g = (Graphics2D) g2d.create();
		g.setTransform(tx);
		g.fill(arrowHead);
		g.dispose();
	}
}
