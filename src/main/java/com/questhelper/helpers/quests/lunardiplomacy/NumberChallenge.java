package com.questhelper.helpers.quests.lunardiplomacy;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ObjectID;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NumberChallenge extends DetailedOwnerStep
{
	DetailedQuestStep press0, press1, press2, press3, press4, press5, press6, press7, press8, press9, catchStep;

	public NumberChallenge(QuestHelper questHelper)
	{
		super(questHelper, "Select the correct numbers to finish the pattern.");
	}

	@Override
	protected void setupSteps()
	{
		press0 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_0, new WorldPoint(1783, 5062, 2),
			"Press the zero.");
		press1 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_1, new WorldPoint(1786, 5065, 2),
			"Press the one.");
		press2 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_2, new WorldPoint(1787, 5063, 2),
			"Press the two.");
		press3 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_3, new WorldPoint(1786, 5061, 2),
			"Press the three.");
		press4 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_4, new WorldPoint(1784, 5060, 2),
			"Press the four.");
		press5 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_5, new WorldPoint(1781, 5061, 2),
			"Press the five.");
		press6 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_6, new WorldPoint(1780, 5063, 2),
			"Press the six.");
		press7 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_7, new WorldPoint(1781, 5065, 2),
			"Press the seven.");
		press8 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_8, new WorldPoint(1782, 5066, 2),
			"Press the eight.");
		press9 = new ObjectStep(getQuestHelper(), ObjectID.LUNAR_DREAM_NUMBER_9, new WorldPoint(1784, 5067, 2),
			"Press the nine.");

		catchStep = new DetailedQuestStep(getQuestHelper(), "Press the numbers to finish the pattern.");
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		switch (client.getVarbitValue(2417))
		{
			case 0:
				setupStepFromState(press7, press9);
				break;
			case 1:
				setupStepFromState(press3, press7);
				break;
			case 2:
				setupStepFromState(press6, press7);
				break;
			case 3:
				setupStepFromState(press1, press5);
				break;
			case 4:
				setupStepFromState(press2, press0);
				break;
			case 5:
				setupStepFromState(press4, press5);
				break;
			case 6:
				setupStepFromState(press1, press6);
				break;
			case 7:
			case 8:
				setupStepFromState(press3, press4);
				break;
			case 9:
				setupStepFromState(press3, press6);
				break;
			case 10:
				setupStepFromState(press3, press1);
				break;
			case 11:
				setupStepFromState(press8, press9);
				break;
			case 12:
				setupStepFromState(press4, press8);
				break;
			case 13:
			case 15:
				setupStepFromState(press5, press1);
				break;
			case 14:
				setupStepFromState(press5, press4);
				break;
			default:
				startUpStep(catchStep);
				break;
		}
	}

	private void setupStepFromState(QuestStep choice1, QuestStep choice2)
	{
		if (client.getVarbitValue(2421) == 0)
		{
			startUpStep(choice1);
		}
		else
		{
			startUpStep(choice2);
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(press0, press1, press2, press3, press4, press5, press6, press7, press8, press9, catchStep);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Collections.emptyList();
	}
}
