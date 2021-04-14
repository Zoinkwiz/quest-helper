package com.questhelper.steps;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperWorldMapPoint;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import java.awt.Graphics2D;
import java.awt.Polygon;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TileStep extends DetailedQuestStep
{
	private final WorldPoint worldPoint;

	public TileStep(QuestHelper questHelper, WorldPoint worldPoint, String text, Requirement... requirements)
	{
		super(questHelper, text, requirements);
		this.worldPoint = worldPoint;
	}

	@Override
	public void startUp()
	{
		worldMapPointManager.add(new QuestHelperWorldMapPoint(worldPoint, getQuestImage()));
	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
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
