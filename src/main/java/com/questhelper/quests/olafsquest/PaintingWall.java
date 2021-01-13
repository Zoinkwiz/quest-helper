package com.questhelper.quests.olafsquest;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class PaintingWall extends QuestStep
{
	private final int RIGHT_VARBIT = 3541;
	private final int BOTTOM_VARBIT = 3542;
	private final int LEFT_VARBIT = 3543;
	private final int TOP_VARBIT = 3544;

	private int highlightWidget = -1;

	int rightPiece, bottomPiece, leftPiece, topPiece;

	public PaintingWall(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted boxes to turn the squares to solve the puzzle.");
	}

	@Override
	public void startUp()
	{
		updateSolvedPositionState();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSolvedPositionState();
	}

	private void updateSolvedPositionState()
	{
		rightPiece = client.getVarbitValue(RIGHT_VARBIT);
		bottomPiece = client.getVarbitValue(BOTTOM_VARBIT);
		leftPiece = client.getVarbitValue(LEFT_VARBIT);
		topPiece = client.getVarbitValue(TOP_VARBIT);

		if (rightPiece != 4)
		{
			highlightWidget = 14;
		}
		else if (bottomPiece == 2 && topPiece == 2 && leftPiece == 2)
		{
			highlightWidget = 15;
		}
		else if (bottomPiece == 3 && topPiece == 3 && leftPiece == 2)
		{
			highlightWidget = 13;
		}
		else if (bottomPiece == 3 && topPiece == 4 && leftPiece == 3)
		{
			highlightWidget = 16;
		}
		else if (bottomPiece != 4)
		{
			highlightWidget = 15;
		}
		else if (topPiece != 4)
		{
			highlightWidget = 13;
		}
		else if (leftPiece != 4)
		{
			highlightWidget = 16;
		}
		else
		{
			highlightWidget = 17;
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(253, 0);
		if (widgetWrapper != null)
		{
			Widget widget = client.getWidget(253, highlightWidget);
			if (widget != null)
			{
				graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
					questHelper.getConfig().targetOverlayColor().getGreen(),
					questHelper.getConfig().targetOverlayColor().getBlue(), 65));
				graphics.fill(widget.getBounds());
				graphics.setColor(questHelper.getConfig().targetOverlayColor());
				graphics.draw(widget.getBounds());
			}
		}
	}
}
