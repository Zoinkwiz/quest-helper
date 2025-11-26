package com.questhelper.steps;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.tools.DefinedPoint;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;

public class TileStep extends DetailedQuestStep
{
	public TileStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.definedPoint = DefinedPoint.of(worldPoint);
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		LocalPoint lp = definedPoint.resolveLocalPoint(client);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygon(graphics, poly, questHelper.getConfig().targetOverlayColor());
	}
}
