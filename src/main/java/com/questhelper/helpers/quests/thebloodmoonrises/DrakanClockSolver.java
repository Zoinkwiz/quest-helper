// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import java.awt.*;
import java.util.List;
import lombok.Setter;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;

public class DrakanClockSolver extends DetailedOwnerStep
{
	private final int bigHandVarbit;
	private final int bigHandTarget;
	private final int smallHandVarbit;
	private final int smallHandTarget;

	ClockRotationStep rotateBigHandClockwise;
	ClockRotationStep rotateBigHandAntiClockwise;
	ClockRotationStep rotateSmallHandClockwise;
	ClockRotationStep rotateSmallHandAntiClockwise;

	public DrakanClockSolver(QuestHelper questHelper, int bigHandVarbit, int bigHandTarget, int smallHandVarbit, int smallHandTarget)
	{
		super(questHelper, "Solve the clock puzzle.");
		this.bigHandVarbit = bigHandVarbit;
		this.bigHandTarget = bigHandTarget;
		this.smallHandVarbit = smallHandVarbit;
		this.smallHandTarget = smallHandTarget;
	}

	@Override
	public void startUp()
	{
		updateSteps();

		super.startUp();
	}

	@Override
	protected void setupSteps()
	{
		rotateBigHandClockwise = new ClockRotationStep(this.getQuestHelper(), 963, 10);
		rotateBigHandAntiClockwise = new ClockRotationStep(this.getQuestHelper(), 963, 11);
		rotateSmallHandClockwise = new ClockRotationStep(this.getQuestHelper(), 963, 14);
		rotateSmallHandAntiClockwise = new ClockRotationStep(this.getQuestHelper(), 963, 15);
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		var changedVarbitId = varbitChanged.getVarbitId();
		if (changedVarbitId == bigHandVarbit || changedVarbitId == smallHandVarbit)
		{
			updateSteps();
		}
	}

	@Subscribe
	public void onGameTick(final GameTick event)
	{
		// TODO: This is probably possible to optimize, but given how briefly this step is active I haven't bothered.
		updateSteps();
	}

	protected void updateSteps()
	{
		int bigHandCurrent = client.getVarbitValue(bigHandVarbit);

		if (bigHandCurrent != bigHandTarget)
		{
			// Big hand needs rotating
			var cwTurns = (bigHandTarget - bigHandCurrent + 12) % 12;
			var acwTurns = (bigHandCurrent - bigHandTarget + 12) % 12;
			if (cwTurns < acwTurns)
			{
				rotateBigHandClockwise.setSteps(cwTurns);
				startUpStep(rotateBigHandClockwise);
			}
			else
			{
				rotateBigHandAntiClockwise.setSteps(acwTurns);
				startUpStep(rotateBigHandAntiClockwise);
			}
			return;
		}

		int smallHandCurrent = client.getVarbitValue(smallHandVarbit);

		if (smallHandCurrent != smallHandTarget)
		{
			// Small hand needs rotating
			var cwTurns = (smallHandTarget - smallHandCurrent + 12) % 12;
			var acwTurns = (smallHandCurrent - smallHandTarget + 12) % 12;
			if (cwTurns < acwTurns)
			{
				rotateSmallHandClockwise.setSteps(cwTurns);
				startUpStep(rotateSmallHandClockwise);
			}
			else
			{
				rotateSmallHandAntiClockwise.setSteps(acwTurns);
				startUpStep(rotateSmallHandAntiClockwise);
			}
			return;
		}


		var done = new DetailedQuestStep(this.getQuestHelper(), "You're done with the clock puzzle!");
		startUpStep(done);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(
			rotateBigHandClockwise,
			rotateBigHandAntiClockwise,
			rotateSmallHandClockwise,
			rotateSmallHandAntiClockwise
		);
	}

	private static class ClockRotationStep extends QuestStep
	{
		/// The rotationY required for the widget to be considered correctly rotated
		private static final int REQUIRED_ROTATION = 0;
		/// The width of the square we mark for the correct spot
		private static final int SPOT_WIDTH = 50;
		/// The height of the square we mark for the correct spot
		private static final int SPOT_HEIGHT = 50;
		private final int groupID;
		private final int widgetID;

		@Setter
		int steps = 0;

		public ClockRotationStep(QuestHelper questHelper, int groupID, int widgetID)
		{
			super(questHelper, "Rotate the hand");
			this.groupID = groupID;
			this.widgetID = widgetID;
			var modelHighlighter = new WidgetHighlight(groupID, widgetID);
			this.addWidgetHighlight(modelHighlighter);
		}

		@Override
		public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
		{
			super.makeWidgetOverlayHint(graphics, plugin);
			var root = client.getWidget(963, 0);
			if (root == null)
			{
				return;
			}

			var w = client.getWidget(groupID, widgetID);
			if (w == null)
			{
				return;
			}

			graphics.setColor(getQuestHelper().getConfig().targetOverlayColor());

			graphics.drawRect(w.getBounds().x, w.getBounds().y, w.getBounds().width, w.getBounds().height);

			var boldFont = FontManager.getRunescapeBoldFont();
			graphics.setFont(boldFont);

			graphics.drawString(String.format("%d", steps), w.getBounds().x + w.getBounds().width / 2 - 4, w.getBounds().y + w.getBounds().height / 2 + 7);

		}
	}
}
