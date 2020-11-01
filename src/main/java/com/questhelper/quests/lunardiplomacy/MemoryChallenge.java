package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedQuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class MemoryChallenge extends DetailedQuestStep
{
	public MemoryChallenge(QuestHelper questHelper)
	{
		super(questHelper, "Follow the path to the end.");
	}

	@Override
	public void startUp()
	{
		super.startUp();
		setupPaths();
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		setupPaths();
	}

	public void setupPaths()
	{
		// Path 1
		if (client.getVarbitValue(2414) == 83)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1731, 5106, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			)));
			setWorldPoint(1737, 5083, 2);
		}

		// Path 2
		if (client.getVarbitValue(2414) == 192)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1737, 5106, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1731, 5100, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1731, 5085, 2),
				new WorldPoint(1731, 5083, 2)
			)));
			setWorldPoint(1731, 5083, 2);
		}

		// Path 3
		if (client.getVarbitValue(2415) == 7)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1731, 5106, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1737, 5091, 2),
				new WorldPoint(1740, 5091, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1740, 5085, 2),
				new WorldPoint(1740, 5083, 2)
			)));
			setWorldPoint(1740, 5083, 2);
		}

		// Path 4
		if (client.getVarbitValue(2412) == 28)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			)));
			setWorldPoint(1731, 5083, 2);
		}

		// Path 5
		if (client.getVarbitValue(2412) == 14)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1740, 5094, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1740, 5085, 2),
				new WorldPoint(1740, 5083, 2)
			)));
			setWorldPoint(1740, 5083, 2);
		}

		// Path 6
		if (client.getVarbitValue(2414) == 42)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1731, 5106, 2),
				new WorldPoint(1731, 5103, 2),
				new WorldPoint(1731, 5100, 2),
				new WorldPoint(1734, 5100, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1740, 5094, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1740, 5085, 2),
				new WorldPoint(1740, 5083, 2)
			)));
			setWorldPoint(1740, 5083, 2);
		}

		// Path 7
		if (client.getVarbitValue(2412) == 14)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			)));
			setWorldPoint(1737, 5083, 2);
		}

		// Path 8
		if (client.getVarbitValue(2414) == 91)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),
				new WorldPoint(1740, 5097, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1737, 5094, 2),
				new WorldPoint(1734, 5094, 2),
				new WorldPoint(1734, 5091, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			)));
			setWorldPoint(1737, 5083, 2);
		}

		// Path 9
		if (client.getVarbitValue(2413) == 3)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1740, 5106, 2),
				new WorldPoint(1740, 5103, 2),
				new WorldPoint(1740, 5100, 2),

				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1737, 5094, 2),

				new WorldPoint(1740, 5094, 2),
				new WorldPoint(1740, 5091, 2),
				new WorldPoint(1740, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1734, 5085, 2),
				new WorldPoint(1734, 5083, 2)
			)));
			setWorldPoint(1734, 5083, 2);
		}

		// Path 10
		if (client.getVarbitValue(2412) == 30)
		{
			setLinePoints(new ArrayList<>(Arrays.asList(
				new WorldPoint(1734, 5106, 2),
				new WorldPoint(1734, 5103, 2),
				new WorldPoint(1737, 5103, 2),
				new WorldPoint(1737, 5100, 2),
				new WorldPoint(1737, 5097, 2),
				new WorldPoint(1734, 5097, 2),
				new WorldPoint(1731, 5097, 2),
				new WorldPoint(1731, 5094, 2),
				new WorldPoint(1731, 5091, 2),
				new WorldPoint(1731, 5088, 2),
				new WorldPoint(1734, 5088, 2),
				new WorldPoint(1737, 5088, 2),
				new WorldPoint(1737, 5085, 2),
				new WorldPoint(1737, 5083, 2)
			)));
			setWorldPoint(1737, 5083, 2);
		}
	}
}
