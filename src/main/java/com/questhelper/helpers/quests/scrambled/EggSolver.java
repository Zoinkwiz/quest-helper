package com.questhelper.helpers.quests.scrambled;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import com.questhelper.steps.widget.WidgetHighlight;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

@Slf4j
public class EggSolver extends DetailedOwnerStep
{
	@Inject
	Client client;

	private ConditionalStep solvePuzzle;

	public EggSolver(Scrambled parentHelper)
	{
		super(parentHelper, "Solve the puzzle by following the instructions in the overlay.");
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		var h = getQuestHelper();
		var unreachable = new DetailedQuestStep(h, "this step should be unreachable, info for an egg piece is missing!");

		solvePuzzle = new ConditionalStep(getQuestHelper(), unreachable);

		addEggPair(solvePuzzle, 57101, 0, 165, 141);
		addEggPair(solvePuzzle, 57102, 0, 297, 217);
		addEggPair(solvePuzzle, 57103, 0/*todo: confirm*/, 243, 149);
		addEggPair(solvePuzzle, 57104, 0/*todo confirm*/, 231, 71);
		addEggPair(solvePuzzle, 57105, 0, 149, 179);
		addEggPair(solvePuzzle, 57106, 0 /* todo confirm */, 194, 121);
		addEggPair(solvePuzzle, 57107, 0, 250, 41);
		addEggPair(solvePuzzle, 57108, 0, 209, 160);
		addEggPair(solvePuzzle, 57109, 0, 217, 27);
		addEggPair(solvePuzzle, 57110, 0, 157, 103);
		addEggPair(solvePuzzle, 57111, 0, 260, 22);
		addEggPair(solvePuzzle, 57112, 0, 199, 248);
		// 57113 doesn't exist?
		addEggPair(solvePuzzle, 57114, 0, 269, 177);
		addEggPair(solvePuzzle, 57115, 0, 257, 113);
		addEggPair(solvePuzzle, 57116, 0, 306, 119);
		addEggPair(solvePuzzle, 57117, 0, 196, 105);
		addEggPair(solvePuzzle, 57118, 0, 288, 71);
		addEggPair(solvePuzzle, 57119, 0, 187, 210);
		addEggPair(solvePuzzle, 57120, 0, 309, 160);
		addEggPair(solvePuzzle, 57121, 0, 222, 199);
		addEggPair(solvePuzzle, 57122, 0, 273, 115);
		addEggPair(solvePuzzle, 57123, 0, 175, 58);
		addEggPair(solvePuzzle, 57124, 0, 252, 213);
		addEggPair(solvePuzzle, 57125, 0, 198, 60);
		addEggPair(solvePuzzle, 57126, 0, 244, 255);
		addEggPair(solvePuzzle, 57127, 0, 231, 99);
	}

	protected void updateSteps()
	{
		startUpStep(solvePuzzle);
	}

	@Override
	public List<QuestStep> getSteps()
	{
		return List.of(
			this.solvePuzzle
		);
	}

	// TODO: should also accept a position & required rotation
	private void addEggPair(ConditionalStep c, int modelID, int requiredRotation, int positionX, int positionY)
	{
		var step = new EggPieceStep(getQuestHelper(), modelID, requiredRotation, positionX, positionY);
		var requirement = new EggPieceRequirement(modelID);

		c.addStep(requirement, step);
	}

	private class EggPieceStep extends QuestStep
	{
		int modelID;
		int requiredRotation;

		int positionX;
		int positionY;

		public EggPieceStep(QuestHelper questHelper, int modelID, int requiredRotation, int positionX, int positionY)
		{
			super(questHelper, "Move the highlighted piece");
			// TODO: required rotation
			var modelHighlighter = new WidgetHighlight(922, 7, true);
			modelHighlighter.setModelIdRequirement(57106);
			this.addWidgetHighlight(modelHighlighter);

			this.modelID = modelID;
			this.requiredRotation = requiredRotation;
			this.positionX = positionX;
			this.positionY = positionY;
		}

		@Override
		public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
		{
			super.makeWidgetOverlayHint(graphics, plugin);
			var root = client.getWidget(922, 0);
			if (root == null) {
				return;
			}
			var rootBounds = root.getBounds();

			var w = this.getWidget(client);
			if (w == null) {return;}

			graphics.setColor(Color.CYAN);

			graphics.drawRect(w.getBounds().x, w.getBounds().y, w.getBounds().width, w.getBounds().height);

			graphics.drawString(String.format("%d", w.getRotationY()), w.getBounds().x, w.getBounds().y + w.getBounds().height);

			if (w.getRotationY() == requiredRotation) {
				graphics.drawString("move here", rootBounds.x + this.positionX, rootBounds.y + this.positionY);
				graphics.drawRect(rootBounds.x + this.positionX, rootBounds.y + this.positionY, 50, 50);
			} else {
				// TODO: rotate how many times?
				graphics.drawString("rotate", w.getBounds().x, w.getBounds().y);
			}

		}

		@Nullable
		protected Widget getWidget(Client client)
		{
			var widget = client.getWidget(922, 7);

			if (widget == null)
			{
				return null;
			}

			var children = widget.getChildren();
			var staticChildren = widget.getStaticChildren();

			// TODO: do we need to check static widgets AND dynamic widgets?
			if (children != null)
			{
				for (var w : children)
				{
					if (w != null && w.getModelId() == modelID) {
						return w;
					}
				}
				// for (var w : staticChildren)
				// {
				// 	if (w != null && w.getModelId() == modelID) {
				// 		return w;
				// 	}
				// }
			}

			return null;
		}
	}

	private class EggPieceRequirement extends WidgetPresenceRequirement
	{
		private int modelID;
		public EggPieceRequirement(int modelID) {
			super(922, 7);
			this.modelID = modelID;
		}

		@Override
		@Nullable
		protected Widget getWidget(Client client)
		{
			var widget = client.getWidget(groupId, childId);

			if (widget == null)
			{
				return null;
			}

			var children = widget.getChildren();
			var staticChildren = widget.getStaticChildren();

			// TODO: do we need to check static widgets AND dynamic widgets?
			if (children != null)
			{
				for (var w : children)
				{
					if (w != null && w.getModelId() == modelID) {
						return w;
					}
				}
				// for (var w : staticChildren)
				// {
				// 	if (w != null && w.getModelId() == modelID) {
				// 		return w;
				// 	}
				// }
			}

			return null;
		}
	}
}
