package com.questhelper.playerquests.puzzlewrapper;

import com.google.common.collect.ImmutableMap;
import com.questhelper.QuestHelperConfig;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.requirements.ConfigRequirement;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

public class PuzzleWrapper extends BasicQuestHelper
{
	QuestStep stepUnknown;
	QuestStep stepA;
	QuestStep stepB;
	QuestStep stepC;
	ConditionalStep cSteps;
	PuzzleWrapperStep pwSteps;
	ManualRequirement manualRequirementA;
	ManualRequirement manualRequirementB;
	ManualRequirement manualRequirementC;


	@Override
	public List<HelperConfig> getConfigs() {
		return List.of(
			new HelperConfig("Stage", "playerquestpuzzlewrapper", PuzzleWrapperStage.values())
		);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			return;
		}

		if (event.getKey().equals("playerquestpuzzlewrapper"))
		{
			assert event.getNewValue() != null;

			var wasA = event.getNewValue().equals("A");
			var wasB = event.getNewValue().equals("B");
			var wasC = event.getNewValue().equals("C");
			manualRequirementA.setShouldPass(wasA);
			manualRequirementB.setShouldPass(wasB);
			manualRequirementC.setShouldPass(wasC);
		}
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		stepUnknown = new DetailedQuestStep(this, "Unknown");
		stepA = new DetailedQuestStep(this, "A and B").puzzleWrapStep(true);
		stepB = new DetailedQuestStep(this, "B").puzzleWrapStep(true);
		stepC = new DetailedQuestStep(this, "C").puzzleWrapStep(true);
		cSteps = new ConditionalStep(this, stepUnknown, "Conditional");
		cSteps.addStep(manualRequirementA, stepA);
		cSteps.addStep(manualRequirementB, stepB);
		cSteps.addStep(manualRequirementC, stepC);
		// cSteps.conditionToHideInSidebar(and(new ConfigRequirement(this.getConfig()::solvePuzzles)));
		pwSteps = cSteps.puzzleWrapStepWithDefaultText("Puzzle wrap conditional");
		pwSteps.conditionToHideInSidebar(new ConfigRequirement(this.getConfig()::solvePuzzles));

		stepA.addSubSteps(stepB);
		((PuzzleWrapperStep)stepA).getSolvingStep().addSubSteps(stepB);

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(-1, pwSteps)
			.build();
	}

	@Override
	protected void setupRequirements()
	{
		manualRequirementA = new ManualRequirement();
		manualRequirementB = new ManualRequirement();
		manualRequirementC = new ManualRequirement();
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
			// stepUnknown,
			stepA,
			// stepB,
			stepC
		)));

		return panels;
	}
}
