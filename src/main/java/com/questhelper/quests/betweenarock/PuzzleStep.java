package com.questhelper.quests.betweenarock;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedQuestStep;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class PuzzleStep extends DetailedQuestStep
{
	private final int DOWN_BUTTON = 34;
	private final int LEFT_BUTTON = 33;
	private final int RIGHT_BUTTON = 32;
	private final int UP_BUTTON = 31;
	private final int ROTATE_BUTTON = 30;

	private final int HORIZONTAL = 0;
	private final int VERTICAL = 1;
	private final int FLIP = 2;
	private final int ROTATE = 3;
	private final int SELECTED = 4;
	private final int SELECT_BUTTON = 5;
	private final int PIECE_ID = 6;

	private HashMap<Integer, Integer> highlightButtons = new HashMap<>();

	private final HashMap<Integer, Integer>[] pieces = new HashMap[3];

	private final HashMap<Integer, Integer>[] solvedPieces = new HashMap[3];

	public PuzzleStep(QuestHelper questHelper, ItemRequirement item)
	{
		super(questHelper, "Click the highlighted boxes to move the pieces to the correct spots.", item);

		pieces[0] = new HashMap<>();
		pieces[0].put(PIECE_ID, 11);
		pieces[0].put(SELECTED, 261);
		pieces[0].put(SELECT_BUTTON, 24);

		solvedPieces[0] = new HashMap<>();
		solvedPieces[0].put(HORIZONTAL, 240);
		solvedPieces[0].put(VERTICAL, 170);
		solvedPieces[0].put(ROTATE, 1856);

		pieces[1] = new HashMap<>();
		pieces[1].put(PIECE_ID, 6);
		pieces[1].put(SELECTED, 262);
		pieces[1].put(SELECT_BUTTON, 25);

		solvedPieces[1] = new HashMap<>();
		solvedPieces[1].put(HORIZONTAL, 235);
		solvedPieces[1].put(VERTICAL, 170);
		solvedPieces[1].put(ROTATE, 1860);

		pieces[2] = new HashMap<>();
		pieces[2].put(PIECE_ID, 8);
		pieces[2].put(SELECTED, 263);
		pieces[2].put(SELECT_BUTTON, 26);

		solvedPieces[2] = new HashMap<>();
		solvedPieces[2].put(HORIZONTAL, 235);
		solvedPieces[2].put(VERTICAL, 175);
		solvedPieces[2].put(ROTATE, 1864);

		highlightButtons.put(DOWN_BUTTON, 0);
		highlightButtons.put(LEFT_BUTTON, 0);
		highlightButtons.put(RIGHT_BUTTON, 0);
		highlightButtons.put(UP_BUTTON, 0);
		highlightButtons.put(ROTATE_BUTTON, 0);
		int SELECT_1_BUTTON = 23;
		highlightButtons.put(SELECT_1_BUTTON, 0);
		int SELECT_2_BUTTON = 25;
		highlightButtons.put(SELECT_2_BUTTON, 0);
		int SELECT_3_BUTTON = 26;
		highlightButtons.put(SELECT_3_BUTTON, 0);
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
		int currentPiece;

		HashMap<Integer, Integer>[] piecesCurrentState = new HashMap[3];

		HashMap<Integer, Integer> highlightButtonsTmp = new HashMap<>();

		Widget widget0 = client.getWidget(113, pieces[0].get(PIECE_ID));
		Widget widget1 = client.getWidget(113, pieces[1].get(PIECE_ID));
		Widget widget2 = client.getWidget(113, pieces[2].get(PIECE_ID));

		piecesCurrentState[0] = new HashMap<>();
		if (widget0 == null)
		{
			return;
		}
		piecesCurrentState[0].put(HORIZONTAL, widget0.getOriginalX());
		piecesCurrentState[0].put(VERTICAL, widget0.getOriginalY());
		piecesCurrentState[0].put(ROTATE, widget0.getAnimationId());

		if (widget1 == null)
		{
			return;
		}
		piecesCurrentState[1] = new HashMap<>();
		piecesCurrentState[1].put(HORIZONTAL, widget1.getOriginalX());
		piecesCurrentState[1].put(VERTICAL, widget1.getOriginalY());
		piecesCurrentState[1].put(ROTATE, widget1.getAnimationId());

		if (widget2 == null)
		{
			return;
		}
		piecesCurrentState[2] = new HashMap<>();
		piecesCurrentState[2].put(HORIZONTAL, widget2.getOriginalX());
		piecesCurrentState[2].put(VERTICAL, widget2.getOriginalY());
		piecesCurrentState[2].put(ROTATE, widget2.getAnimationId());

		if (Math.abs(piecesCurrentState[0].get(HORIZONTAL) - solvedPieces[0].get(HORIZONTAL)) > 4 ||
			Math.abs(piecesCurrentState[0].get(VERTICAL) - solvedPieces[0].get(VERTICAL)) > 4 ||
			!piecesCurrentState[0].get(ROTATE).equals(solvedPieces[0].get(ROTATE)))
		{
			currentPiece = 0;
		}
		else if (Math.abs(piecesCurrentState[1].get(HORIZONTAL) - solvedPieces[1].get(HORIZONTAL)) > 4 ||
			Math.abs(piecesCurrentState[1].get(VERTICAL) - solvedPieces[1].get(VERTICAL)) > 4 ||
			!piecesCurrentState[1].get(ROTATE).equals(solvedPieces[1].get(ROTATE)))
		{
			currentPiece = 1;
		}
		else
		{
			currentPiece = 2;
		}

		boolean onlyCurrentSelected = true;
		if (client.getVarpValue(pieces[currentPiece].get(SELECTED)) == 0)
		{
			highlightButtonsTmp.put(pieces[currentPiece].get(SELECT_BUTTON), 1);
			onlyCurrentSelected = false;
		}

		if (client.getVarpValue(pieces[(currentPiece + 1) % 3].get(SELECTED)) == 1)
		{
			highlightButtonsTmp.put(pieces[(currentPiece + 1) % 3].get(SELECT_BUTTON), 1);
			onlyCurrentSelected = false;
		}

		if (client.getVarpValue(pieces[Math.floorMod(currentPiece - 1, 3)].get(SELECTED)) == 1)
		{
			highlightButtonsTmp.put(pieces[Math.floorMod(currentPiece - 1, 3)].get(SELECT_BUTTON), 1);
			onlyCurrentSelected = false;
		}


		if (onlyCurrentSelected)
		{
			if (!piecesCurrentState[currentPiece].get(ROTATE).equals(solvedPieces[currentPiece].get(ROTATE)))
			{
				highlightButtonsTmp.put(ROTATE_BUTTON, 1);
			}
			else if (piecesCurrentState[currentPiece].get(HORIZONTAL) < solvedPieces[currentPiece].get(HORIZONTAL) - 4)
			{
				highlightButtonsTmp.put(RIGHT_BUTTON, 1);
			}
			else if (piecesCurrentState[currentPiece].get(HORIZONTAL) > solvedPieces[currentPiece].get(HORIZONTAL) + 4)
			{
				highlightButtonsTmp.put(LEFT_BUTTON, 1);
			}
			else if (piecesCurrentState[currentPiece].get(VERTICAL) > solvedPieces[currentPiece].get(VERTICAL) + 4)
			{
				highlightButtonsTmp.put(UP_BUTTON, 1);
			}
			else if (piecesCurrentState[currentPiece].get(VERTICAL) < solvedPieces[currentPiece].get(VERTICAL) - 4)
			{
				highlightButtonsTmp.put(DOWN_BUTTON, 1);
			}
		}

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

			Widget widget = client.getWidget(114, entry.getKey());
			if (widget != null)
			{
				graphics.setColor(new Color(getQuestHelper().getConfig().targetOverlayColor().getRed(),
					getQuestHelper().getConfig().targetOverlayColor().getGreen(),
					getQuestHelper().getConfig().targetOverlayColor().getBlue(), 65));
				graphics.fill(widget.getBounds());
				graphics.setColor(questHelper.getConfig().targetOverlayColor());
				graphics.draw(widget.getBounds());
			}
		}
	}
}

