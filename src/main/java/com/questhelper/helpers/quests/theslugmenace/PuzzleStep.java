package com.questhelper.helpers.quests.theslugmenace;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PuzzleStep extends QuestStep
{
	private final int FLIP_BUTTON = 8;
	private final int DOWN_BUTTON = 7;
	private final int LEFT_BUTTON = 6;
	private final int RIGHT_BUTTON = 5;
	private final int UP_BUTTON = 4;
	private final int ROTATE_BUTTON = 3;

	private final int HORIZONTAL = 0;
	private final int VERTICAL = 1;
	private final int FLIP = 2;
	private final int ROTATE = 3;
	private final int SELECTED = 4;
	private final int SELECT_BUTTON = 5;

	private HashMap<Integer, Integer> highlightButtons = new HashMap<>();
	private HashMap<Integer, Integer> highlightButtonSelection = new HashMap<>();

	private final HashMap<Integer, Integer>[] pieces = new HashMap[3];

	private final HashMap<Integer, Integer>[] solvedPieces = new HashMap[3];

	public PuzzleStep(QuestHelper questHelper)
	{
		super(questHelper, "Click the highlighted boxes to move the pieces to the correct spots.");

		final int SELECT_1_BUTTON = 13;
		final int SELECT_2_BUTTON = 14;
		final int SELECT_3_BUTTON = 15;

		pieces[0] = new HashMap<>();
		pieces[0].put(HORIZONTAL, 876);
		pieces[0].put(VERTICAL, 877);
		pieces[0].put(FLIP, 878);
		pieces[0].put(ROTATE, 879);
		pieces[0].put(SELECTED, 12155);
		pieces[0].put(SELECT_BUTTON, SELECT_1_BUTTON); // 3?

		solvedPieces[0] = new HashMap<>();
		solvedPieces[0].put(HORIZONTAL, 33);
		solvedPieces[0].put(VERTICAL, 33);
		solvedPieces[0].put(FLIP, 1);
		solvedPieces[0].put(ROTATE, 1);

		pieces[1] = new HashMap<>();
		pieces[1].put(HORIZONTAL, 880);
		pieces[1].put(VERTICAL, 881);
		pieces[1].put(FLIP, 882);
		pieces[1].put(ROTATE, 883);
		pieces[1].put(SELECTED, 12157);
		pieces[1].put(SELECT_BUTTON, SELECT_2_BUTTON); // 3?

		solvedPieces[1] = new HashMap<>();
		solvedPieces[1].put(HORIZONTAL, 33);
		solvedPieces[1].put(VERTICAL, 33);
		solvedPieces[1].put(FLIP, 1);
		solvedPieces[1].put(ROTATE, 1);

		pieces[2] = new HashMap<>();
		pieces[2].put(HORIZONTAL, 884);
		pieces[2].put(VERTICAL, 885);
		pieces[2].put(FLIP, 886);
		pieces[2].put(ROTATE, 887);
		pieces[2].put(SELECTED, 12159);
		pieces[2].put(SELECT_BUTTON, SELECT_3_BUTTON);

		solvedPieces[2] = new HashMap<>();
		solvedPieces[2].put(HORIZONTAL, 33);
		solvedPieces[2].put(VERTICAL, 33);
		solvedPieces[2].put(FLIP, 1);
		solvedPieces[2].put(ROTATE, 1);

		highlightButtons.put(FLIP_BUTTON, 0);
		highlightButtons.put(DOWN_BUTTON, 0);
		highlightButtons.put(LEFT_BUTTON, 0);
		highlightButtons.put(RIGHT_BUTTON, 0);
		highlightButtons.put(UP_BUTTON, 0);
		highlightButtons.put(ROTATE_BUTTON, 0);
		highlightButtons.put(SELECT_1_BUTTON, 0);
		highlightButtons.put(SELECT_2_BUTTON, 0);
		highlightButtons.put(SELECT_3_BUTTON, 0);
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
		int currentPiece;

		HashMap<Integer, Integer>[] piecesCurrentState = new HashMap[3];

		HashMap<Integer, Integer> highlightButtonsTmp = new HashMap<>();

		piecesCurrentState[0] = new HashMap<>();
		piecesCurrentState[0].put(HORIZONTAL, client.getVarpValue(pieces[0].get(HORIZONTAL)));
		piecesCurrentState[0].put(VERTICAL, client.getVarpValue(pieces[0].get(VERTICAL)));
		piecesCurrentState[0].put(FLIP, client.getVarpValue(pieces[0].get(FLIP)));
		piecesCurrentState[0].put(ROTATE, client.getVarpValue(pieces[0].get(ROTATE)));

		piecesCurrentState[1] = new HashMap<>();
		piecesCurrentState[1].put(HORIZONTAL, client.getVarpValue(pieces[1].get(HORIZONTAL)));
		piecesCurrentState[1].put(VERTICAL, client.getVarpValue(pieces[1].get(VERTICAL)));
		piecesCurrentState[1].put(FLIP, client.getVarpValue(pieces[1].get(FLIP)));
		piecesCurrentState[1].put(ROTATE, client.getVarpValue(pieces[1].get(ROTATE)));

		piecesCurrentState[2] = new HashMap<>();
		piecesCurrentState[2].put(HORIZONTAL, client.getVarpValue(pieces[2].get(HORIZONTAL)));
		piecesCurrentState[2].put(VERTICAL, client.getVarpValue(pieces[2].get(VERTICAL)));
		piecesCurrentState[2].put(FLIP, client.getVarpValue(pieces[2].get(FLIP)));
		piecesCurrentState[2].put(ROTATE, client.getVarpValue(pieces[2].get(ROTATE)));

		// 877 33 V
		// 876 40 H

		// 881 32 V
		// 880 40 H

		// 885 35 V
		// 884 40 H

		int goalH = piecesCurrentState[0].get(HORIZONTAL);
		int goalV = piecesCurrentState[0].get(VERTICAL);
		int[] goalRangeH = new int[5];
		int[] goalRangeV = new int[5];
		for (int i=0; i < 5; i++)
		{
			goalRangeH[i] = goalH - 2 + i;
			goalRangeV[i] = goalV - 2 + i;

		}
		if (!(piecesCurrentState[0].get(FLIP) == 1 && piecesCurrentState[0].get(ROTATE) == 1))
		{
			currentPiece = 0;
		}
		else if (!(
			piecesCurrentState[1].get(ROTATE) == 1
				&& piecesCurrentState[1].get(FLIP) == 1
				&& Arrays.stream(goalRangeH).anyMatch((h) -> h == piecesCurrentState[1].get(HORIZONTAL))
				&& Arrays.stream(goalRangeV).anyMatch((v) -> v == piecesCurrentState[1].get(VERTICAL))
		))
		{
			currentPiece = 1;
		}
		else
		{
			currentPiece = 2;
		}

		boolean onlyCurrentSelected = true;
		if (client.getVarbitValue(pieces[currentPiece].get(SELECTED)) == 0)
		{
			highlightButtonsTmp.put(pieces[currentPiece].get(SELECT_BUTTON), 1);
			onlyCurrentSelected = false;
		}

		if (client.getVarbitValue(pieces[(currentPiece + 1) % 3].get(SELECTED)) == 1)
		{
			highlightButtonsTmp.put(pieces[(currentPiece + 1) % 3].get(SELECT_BUTTON), 1);
			onlyCurrentSelected = false;
		}

		if (client.getVarbitValue(pieces[Math.floorMod(currentPiece - 1, 3)].get(SELECTED)) == 1)
		{
			highlightButtonsTmp.put(pieces[Math.floorMod(currentPiece - 1, 3)].get(SELECT_BUTTON), 1);
			onlyCurrentSelected = false;
		}


		if (!onlyCurrentSelected)
		{
			highlightButtonSelection = highlightButtonsTmp;
			highlightButtons = new HashMap<>();
			return;
		}

		if (!piecesCurrentState[currentPiece].get(FLIP).equals(1))
		{
			highlightButtonsTmp.put(FLIP_BUTTON, 1);
		}
		else if (!piecesCurrentState[currentPiece].get(ROTATE).equals(1))
		{
			highlightButtonsTmp.put(ROTATE_BUTTON, 1);
		}
		else if (piecesCurrentState[currentPiece].get(HORIZONTAL) < goalH - 2)
		{
			highlightButtonsTmp.put(RIGHT_BUTTON, 1);
		}
		else if (piecesCurrentState[currentPiece].get(HORIZONTAL) > goalH + 2)
		{
			highlightButtonsTmp.put(LEFT_BUTTON, 1);
		}
		else if (piecesCurrentState[currentPiece].get(VERTICAL) > goalV + 2)
		{
			highlightButtonsTmp.put(UP_BUTTON, 1);
		}
		else if (piecesCurrentState[currentPiece].get(VERTICAL) < goalV - 2)
		{
			highlightButtonsTmp.put(DOWN_BUTTON, 1);
		}

		highlightButtonSelection = new HashMap<>();
		highlightButtons = highlightButtonsTmp;
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

			Widget widget = client.getWidget(462, entry.getKey());
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

		for (Map.Entry<Integer, Integer> entry : highlightButtonSelection.entrySet())
		{
			if (entry.getValue() == 0)
			{
				continue;
			}

			Widget widget = client.getWidget(462, entry.getKey());
			if (widget != null)
			{
				Widget widgetSelectionButton = widget.getChild(3);
				if (widgetSelectionButton != null)
				{
					graphics.setColor(new Color(questHelper.getConfig().targetOverlayColor().getRed(),
						questHelper.getConfig().targetOverlayColor().getGreen(),
						questHelper.getConfig().targetOverlayColor().getBlue(), 65));
					graphics.fill(widgetSelectionButton.getBounds());
					graphics.setColor(questHelper.getConfig().targetOverlayColor());
					graphics.draw(widgetSelectionButton.getBounds());
				}
			}
		}
	}
}
