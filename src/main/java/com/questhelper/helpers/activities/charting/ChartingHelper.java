/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.activities.charting;

import com.questhelper.helpers.activities.charting.steps.ChartingCaveTelescopeStep;
import com.questhelper.helpers.activities.charting.steps.ChartingCrateStep;
import com.questhelper.helpers.activities.charting.steps.ChartingCurrentStep;
import com.questhelper.helpers.activities.charting.steps.ChartingDivingStep;
import com.questhelper.helpers.activities.charting.steps.ChartingGenericObjectStep;
import com.questhelper.helpers.activities.charting.steps.ChartingPuzzleWrapStep;
import com.questhelper.helpers.activities.charting.steps.ChartingTaskStep;
import com.questhelper.helpers.activities.charting.steps.ChartingTelescopeStep;
import com.questhelper.helpers.activities.charting.steps.ChartingWeatherStep;
import net.runelite.api.gameval.VarbitID;
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.StepIsActiveRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.ReorderableConditionalStep;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.questhelper.requirements.util.LogicHelper.not;

public class ChartingHelper extends ComplexStateQuestHelper
{
	private final DetailedQuestStep overviewStep = new DetailedQuestStep(this, "You have no more things you can chart at your current level.");
	private final List<QuestStep> chartingSteps = new ArrayList<>();
	private List<PanelDetails> panelDetails = new ArrayList<>();

	@Override
	protected void setupRequirements()
	{

	}

	@Override
	public List<PanelDetails> getPanels()
	{
		return panelDetails;
	}

	private void buildSteps()
	{
		chartingSteps.clear();
		Map<String, List<ChartingTaskInterface>> stepsBySea = new LinkedHashMap<>();

		for (ChartingSeaSection section : ChartingTasksData.getSections())
		{
			List<ChartingTaskInterface> steps = new ArrayList<>();
			for (ChartingTaskDefinition definition : section.getTasks())
			{
				ChartingTaskInterface step = createStep(definition);

				chartingSteps.add((QuestStep) step);
				steps.add(step);
			}
			if (!steps.isEmpty())
			{
				stepsBySea.put(section.getSea(), steps);
			}
		}

		buildSidePanel(stepsBySea);
	}

	private void buildSidePanel(Map<String, List<ChartingTaskInterface>> stepsBySea)
	{
		var topLevelPanel = new TopLevelPanelDetails("Sea charting");

		// Map sea names to their sections to get IDs
		Map<String, ChartingSeaSection> seaNameToSection = new LinkedHashMap<>();
		for (ChartingSeaSection section : ChartingTasksData.getSections())
		{
			seaNameToSection.put(section.getSea(), section);
		}

		for (Map.Entry<String, List<ChartingTaskInterface>> entry : stepsBySea.entrySet())
		{
			List<QuestStep> steps = new ArrayList<>();
			for (ChartingTaskInterface chartingStep : entry.getValue())
			{
				steps.add((QuestStep) chartingStep);
			}
			ChartingSeaSection section = seaNameToSection.get(entry.getKey());
			int panelId = section != null ? section.getId() : -1;
			var panel = new PanelDetails(entry.getKey(), steps).withId(panelId);
			var displayCondition = createDisplayCondition(entry.getValue());
			if (displayCondition != null)
			{
				panel.setDisplayCondition(displayCondition);
			}
			topLevelPanel.addPanelDetails(panel);
		}
		var allDonePanel = new PanelDetails("Done for now", overviewStep).withHideCondition(not(new StepIsActiveRequirement(overviewStep)));
		panelDetails = new ArrayList<>(List.of(allDonePanel, topLevelPanel));
	}

	private ChartingTaskInterface createStep(ChartingTaskDefinition definition)
	{
		switch (definition.getType())
		{
			case GENERIC:
				return new ChartingGenericObjectStep(this, definition);
			case CRATE:
				return new ChartingCrateStep(this, definition);
			case SPYGLASS:
				if (definition.getVarbitId() == VarbitID.SAILING_CHARTING_SPYGLASS_CHARTING_TUTOR_COMPLETE)
				{
					return new ChartingCaveTelescopeStep(this, definition);
				}
				return new ChartingTelescopeStep(this, definition);
			case CURRENT:
				return new ChartingCurrentStep(this, definition);
			case DIVING:
				return new ChartingPuzzleWrapStep(this,
					new ChartingDivingStep(this, definition, true),
					new ChartingDivingStep(this, definition, false),
					definition);
			case WEATHER:
				return new ChartingWeatherStep(this, definition);
			default:
				return new ChartingTaskStep(this, definition);
		}
	}

	@Override
	public QuestStep loadStep()
	{
		buildSteps();

		var chartingConditionalStep = new ReorderableConditionalStep(this, overviewStep);
		for (QuestStep step : chartingSteps)
		{
			if (step instanceof ChartingTaskInterface)
			{
				var chartingStep = (ChartingTaskInterface) step;
				chartingConditionalStep.addStep(chartingStep.getCanDoRequirement(), step);
			}
		}

		return chartingConditionalStep;
	}

	private Requirement createDisplayCondition(List<ChartingTaskInterface> steps)
	{
		if (steps.isEmpty())
		{
			return null;
		}

		if (steps.size() == 1)
		{
			return steps.get(0).getIncompleteRequirement();
		}

		var requirements = steps.stream()
			.map(ChartingTaskInterface::getIncompleteRequirement)
			.toArray(Requirement[]::new);
		return new Conditions(LogicType.OR, requirements);
	}
}
