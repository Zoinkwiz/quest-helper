package com.questhelper.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class ChanceChallenge extends DetailedOwnerStep
{
	DetailedQuestStep talk, spinD1, spinD2, spinD3, spinD4, spinD5, spinD6;

	int currentGoal;

	HashMap<Integer, List<Integer>> solutions = new HashMap();

	public ChanceChallenge(QuestHelper questHelper)
	{
		super(questHelper, "Flip the dice to sum up to the correct number.");
		setupSolutions();
	}

	public void setupSolutions()
	{
		solutions.put(12, Arrays.asList(1, 1, 2, 2, 3, 3));
		solutions.put(13, Arrays.asList(1, 1, 2, 2, 3, 4));
		solutions.put(14, Arrays.asList(1, 1, 2, 2, 4, 4));
		solutions.put(15, Arrays.asList(1, 1, 2, 5, 3, 3));
		solutions.put(16, Arrays.asList(1, 1, 2, 5, 3, 4));
		solutions.put(17, Arrays.asList(1, 1, 2, 5, 4, 4));
		solutions.put(18, Arrays.asList(1, 1, 5, 5, 3, 3));
		solutions.put(19, Arrays.asList(1, 1, 5, 5, 3, 4));
		solutions.put(20, Arrays.asList(1, 1, 5, 5, 4, 4));
		solutions.put(21, Arrays.asList(1, 6, 2, 5, 3, 4));
		solutions.put(22, Arrays.asList(1, 6, 2, 5, 4, 4));
		solutions.put(23, Arrays.asList(1, 6, 5, 5, 3, 3));
		solutions.put(24, Arrays.asList(1, 6, 5, 5, 3, 4));
		solutions.put(25, Arrays.asList(1, 6, 5, 5, 4, 4));
		solutions.put(26, Arrays.asList(6, 6, 2, 5, 3, 4));
		solutions.put(27, Arrays.asList(6, 6, 2, 5, 4, 4));
		solutions.put(28, Arrays.asList(6, 6, 5, 5, 3, 3));
		solutions.put(29, Arrays.asList(6, 6, 5, 5, 3, 4));
		solutions.put(30, Arrays.asList(6, 6, 5, 5, 4, 4));
	}

	public void setupSteps()
	{
		talk = new NpcStep(getQuestHelper(), NpcID.ETHEREAL_FLUKE, new WorldPoint(1737, 5068, 2), "Talk to the Ethereal Fluke, and solve his puzzle.");
		talk.addDialogStep("Suppose I may as well have a go.");

		spinD1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17019, new WorldPoint(1735, 5064, 2), "Flip the centre west die.");
		spinD2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17024, new WorldPoint(1737, 5063, 2), "Flip the centre east die.");
		spinD3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17023, new WorldPoint(1732, 5067, 2), "Flip the north west die.");
		spinD4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17020, new WorldPoint(1732, 5060, 2), "Flip the south west die.");
		spinD5 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17022, new WorldPoint(1739, 5067, 2), "Flip the north east die.");
		spinD6 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_17021, new WorldPoint(1739, 5060, 2), "Flip the south east die.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		currentGoal = client.getVarbitValue(2411);

		if (currentGoal == 0)
		{
			startUpStep(talk);
			return;
		}

		int die16A = client.getVarbitValue(2399);
		if (die16A == 0)
		{
			die16A = 1;
		}
		else
		{
			die16A = 6;
		}
		int die16B = client.getVarbitValue(2404);
		if (die16B == 0)
		{
			die16B = 6;
		}
		else
		{
			die16B = 1;
		}

		int die25A = client.getVarbitValue(2403);
		if (die25A == 0)
		{
			die25A = 5;
		}
		else
		{
			die25A = 2;
		}
		int die25B = client.getVarbitValue(2400);
		if (die25B == 0)
		{
			die25B = 2;
		}
		else
		{
			die25B = 5;
		}

		int die34A = client.getVarbitValue(2402);
		if (die34A == 0)
		{
			die34A = 4;
		}
		else
		{
			die34A = 3;
		}
		int die34B = client.getVarbitValue(2401);
		if (die34B == 0)
		{
			die34B = 3;
		}
		else
		{
			die34B = 4;
		}

		checkSolutions(die16A, die16B, die25A, die25B, die34A, die34B);
	}

	public void checkSolutions(int d1, int d2, int d3, int d4, int d5, int d6)
	{
		List<Integer> solution = solutions.get(currentGoal);

		if (solution == null)
		{
			startUpStep(talk);
		}
		else if (d1 != solution.get(0))
		{
			startUpStep(spinD1);
		}
		else if (d2 != solution.get(1))
		{
			startUpStep(spinD2);
		}
		else if (d3 != solution.get(2))
		{
			startUpStep(spinD3);
		}
		else if (d4 != solution.get(3))
		{
			startUpStep(spinD4);
		}
		else if (d5 != solution.get(4))
		{
			startUpStep(spinD5);
		}
		else if (d6 != solution.get(5))
		{
			startUpStep(spinD6);
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(talk, spinD1, spinD2, spinD3, spinD4, spinD5, spinD6);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Collections.singletonList(talk);
	}
}

