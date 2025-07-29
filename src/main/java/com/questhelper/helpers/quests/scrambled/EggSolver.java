package com.questhelper.helpers.quests.scrambled;

import com.questhelper.QuestHelperPlugin;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.SimpleRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import java.awt.*;
import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

@Slf4j
public class EggSolver extends DetailedOwnerStep
{

	private ConditionalStep solvePuzzle;


	public EggSolver(Scrambled parentHelper)
	{
		super(parentHelper, "Solve the puzzle by following the instructions in the overlay.");
	}

	/**
	 * Find the widget with the given interface ID and, if it exists, look through its dynamic children for a widget
	 * matching the given model ID
	 */
	private static @Nullable Widget findWidgetByModelID(Client client, int interfaceID, int modelID)
	{
		var widget = client.getWidget(interfaceID);

		if (widget == null)
		{
			return null;
		}

		var children = widget.getChildren();

		if (children != null)
		{
			for (var w : children)
			{
				if (w != null && w.getModelId() == modelID)
				{
					return w;
				}
			}
		}

		return null;
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
		var unreachable = new DetailedQuestStep(h, "Something went wrong with the puzzle solver, please report this in the Quest Helper Discord with a screenshot of your puzzle.");

		solvePuzzle = new ConditionalStep(getQuestHelper(), unreachable);

		addEggPair(solvePuzzle, 57106, 194, 121);
		addEggPair(solvePuzzle, 57109, 217, 27);
		addEggPair(solvePuzzle, 57111, 260, 22);
		addEggPair(solvePuzzle, 57107, 250, 41);
		addEggPair(solvePuzzle, 57123, 175, 58);
		addEggPair(solvePuzzle, 57125, 198, 60);
		addEggPair(solvePuzzle, 57118, 288, 71);
		addEggPair(solvePuzzle, 57104, 231, 71);
		addEggPair(solvePuzzle, 57127, 231, 99);
		addEggPair(solvePuzzle, 57115, 257, 113);
		addEggPair(solvePuzzle, 57122, 273, 115);
		addEggPair(solvePuzzle, 57116, 306, 119);
		addEggPair(solvePuzzle, 57103, 243, 149);
		addEggPair(solvePuzzle, 57120, 309, 160);
		addEggPair(solvePuzzle, 57114, 269, 177);
		addEggPair(solvePuzzle, 57102, 297, 217);
		addEggPair(solvePuzzle, 57124, 252, 213);
		addEggPair(solvePuzzle, 57126, 244, 255);
		addEggPair(solvePuzzle, 57112, 199, 248);
		addEggPair(solvePuzzle, 57121, 222, 199);
		addEggPair(solvePuzzle, 57119, 187, 210);
		addEggPair(solvePuzzle, 57105, 149, 179);
		addEggPair(solvePuzzle, 57108, 209, 160);
		addEggPair(solvePuzzle, 57101, 165, 141);
		addEggPair(solvePuzzle, 57110, 157, 103);
		addEggPair(solvePuzzle, 57117, 196, 105);
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

	private void addEggPair(ConditionalStep c, int modelID, int positionX, int positionY)
	{
		var step = new EggPieceStep(getQuestHelper(), modelID, positionX, positionY);
		var requirement = new EggPieceRequirement(modelID);

		c.addStep(requirement, step);
	}

	private static class EggPieceStep extends QuestStep
	{
		/// The rotationY required for the widget to be considered correctly rotated
		private static final int REQUIRED_ROTATION = 0;
		/// The width of the square we mark for the correct spot
		private static final int SPOT_WIDTH = 50;
		/// The height of the square we mark for the correct spot
		private static final int SPOT_HEIGHT = 50;

		/// The model ID of the egg piece this step is handling
		int modelID;

		/// The final correct X position for this egg piece
		int positionX;
		/// The final correct Y position for this egg piece
		int positionY;

		public EggPieceStep(QuestHelper questHelper, int modelID, int positionX, int positionY)
		{
			super(questHelper, "Rotate the piece until you're prompted to move it to the correct spot. The square of the egg piece and the square marking the correct spot need to roughly line up.");
			var modelHighlighter = new WidgetHighlight(InterfaceID.Jigsaw.PIECES, true);
			modelHighlighter.setModelIdRequirement(57106);
			this.addWidgetHighlight(modelHighlighter);

			this.modelID = modelID;
			this.positionX = positionX;
			this.positionY = positionY;
		}

		@Override
		public void makeWidgetOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
		{
			super.makeWidgetOverlayHint(graphics, plugin);
			var root = client.getWidget(InterfaceID.Jigsaw.UNIVERSE);
			if (root == null)
			{
				return;
			}
			var rootBounds = root.getBounds();

			var w = EggSolver.findWidgetByModelID(client, InterfaceID.Jigsaw.PIECES, modelID);
			if (w == null)
			{
				return;
			}

			// NOTE: We could hardcode Cyan here
			graphics.setColor(getQuestHelper().getConfig().targetOverlayColor());

			graphics.drawRect(w.getBounds().x, w.getBounds().y, w.getBounds().width, w.getBounds().height);

			// 256 per rotation, goal is
			int rotationsLeft = (2048 - w.getRotationY()) / 256;
			if (w.getRotationY() == REQUIRED_ROTATION)
			{
				graphics.drawString("move here", rootBounds.x + this.positionX, rootBounds.y + this.positionY);
				graphics.drawRect(rootBounds.x + this.positionX, rootBounds.y + this.positionY, SPOT_WIDTH, SPOT_HEIGHT);
			}
			else
			{
				// TODO: We could technically prompt the user with exactly how many times to click the egg piece, but that's overkill imo
				graphics.drawString("click to rotate " + rotationsLeft + " times", w.getBounds().x, w.getBounds().y);
			}

		}
	}

	private static class EggPieceRequirement extends SimpleRequirement
	{
		private final int modelID;

		public EggPieceRequirement(int modelID)
		{
			this.modelID = modelID;
		}

		@Override
		public boolean check(Client client)
		{
			var w = EggSolver.findWidgetByModelID(client, InterfaceID.Jigsaw.PIECES, modelID);
			return w != null;
		}
	}
}
