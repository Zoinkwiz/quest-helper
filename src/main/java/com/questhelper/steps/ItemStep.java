package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.overlay.DirectionArrow;
import java.awt.Graphics2D;
import java.awt.Polygon;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class ItemStep extends DetailedQuestStep
{
	public ItemStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, worldPoint, text, requirements);
	}

	public ItemStep(QuestHelper questHelper, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
	}

	@Override
	public void renderArrow(Graphics2D graphics)
	{
		tileHighlights.forEach((tile, ids) -> {
			LocalPoint lp = tile.getLocalLocation();

			Polygon poly = Perspective.getCanvasTilePoly(client, lp, 30);
			if (poly == null || poly.getBounds() == null)
			{
				return;
			}

			int startX = poly.getBounds().x + (poly.getBounds().width / 2);
			int startY =  poly.getBounds().y + (poly.getBounds().height / 2);

			DirectionArrow.drawWorldArrow(graphics, getQuestHelper().getConfig().targetOverlayColor(), startX, startY);
		});
	}
}
