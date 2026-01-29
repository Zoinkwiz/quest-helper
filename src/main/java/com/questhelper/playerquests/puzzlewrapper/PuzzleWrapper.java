package com.questhelper.playerquests.puzzlewrapper;

import com.google.common.collect.ImmutableMap;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PuzzleWrapper extends BasicQuestHelper
{
	QuestStep stepA;
	QuestStep stepB;
	QuestStep stepC;
	ConditionalStep cSteps;
	PuzzleWrapperStep pwSteps;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		stepA = new DetailedQuestStep(this, "A");
		stepB = new DetailedQuestStep(this, "B");
		stepC = new DetailedQuestStep(this, "C");
		cSteps = new ConditionalStep(this, stepA, "Conditional");
		cSteps.addStep(new ManualRequirement(), stepB);
		cSteps.addStep(new ManualRequirement(), stepC);
		pwSteps = cSteps.puzzleWrapStepWithDefaultText("Puzzle wrap conditional");

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(-1, pwSteps)
			.build();
	}

	@Override
	protected void setupRequirements()
	{

	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			//
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Section", List.of(
			pwSteps,
			// cSteps,
			stepA,
			stepB,
			stepC
		)));

		return panels;
	}
}
