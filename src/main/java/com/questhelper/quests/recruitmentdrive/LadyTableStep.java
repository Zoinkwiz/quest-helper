package com.questhelper.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;

public class LadyTableStep extends ConditionalStep
{
	public LadyTableStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper, step, requirements);
	}

	class GetMissingStatueStep extends DetailedQuestStep
	{
		private List<WorldPoint> statuePoints;

		private final List<TileObject> objects = new ArrayList<>();

		public GetMissingStatueStep(QuestHelper questHelper, String text, Requirement... requirements)
		{
			super(questHelper, text, requirements);
			statuePoints = Arrays.asList(new WorldPoint(2450, 4982, 0),
				new WorldPoint(2452,4982, 0), new WorldPoint(2454, 4982, 0),
				new WorldPoint(2456,4982, 0), new WorldPoint(2450, 4979, 0),
				new WorldPoint(2452,4979,0), new WorldPoint(2454,4979, 0),
				new WorldPoint(2456,4979, 0), new WorldPoint(2450, 4976, 0),
				new WorldPoint(2452, 4976, 0), new WorldPoint(2452, 4976, 0),
				new WorldPoint(2454, 4976, 0), new WorldPoint(2456, 4976, 0));
		}

		@Override
		public void onGameTick(final GameTick event)
		{
			if (worldPoint == null)
			{
				return;
			}
			objects.clear();
			//	checkTiles();
		}/*

		private void checkTiles() {
			client.getGraphicsObjects()


		}*/

	}
}
