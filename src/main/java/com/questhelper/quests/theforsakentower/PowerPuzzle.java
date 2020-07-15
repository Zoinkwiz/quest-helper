package com.questhelper.quests.theforsakentower;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

/* Possible improvement would be to show a number on each square indicating turns remaining to solved position */
public class PowerPuzzle extends QuestStep
{
	int[] solvedPositions =
		{
			0, 0, 1, 0, 4, 1,
			3, 0, 4, 3, 0, 1,
			0, 3, 4, 3, 3, 1,
			1, 4, 2, 0, 2, 2,
			2, 3, 2, 0, 0, 1,
			3, 4, 4, 0, 1, 2
		};

	boolean[] currentPositionCorrect = new boolean[36];

	public PowerPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted boxes to turn the squares to solve the puzzle.");
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
		for (int i=0; i < 36; i++)
		{
			int currentPos = client.getVarbitValue(7811+i);
			if (solvedPositions[i] == 4)
			{
				currentPositionCorrect[i] = currentPos == 0 || currentPos == 2;
			}
			else
			{
				currentPositionCorrect[i] = client.getVarbitValue(7811 + i) == solvedPositions[i];
			}
		}
	}

	@Override
	public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin) {
		super.makeWidgetOverlayHint(graphics, plugin);
		Widget widgetWrapper = client.getWidget(624, 2);
		if (widgetWrapper != null)
		{
			for (int i = 0; i < 36; i++)
			{
				Widget widget = widgetWrapper.getChild(i);
				if (widget != null && !currentPositionCorrect[i])
				{
					graphics.setColor(new Color(0, 255, 255, 65));
					graphics.fill(widget.getBounds());
					graphics.setColor(Color.CYAN);
					graphics.draw(widget.getBounds());
				}
			}
		}
	}
}
