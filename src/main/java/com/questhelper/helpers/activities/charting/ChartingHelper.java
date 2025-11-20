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

import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.ReorderableConditionalStep;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartingHelper extends ComplexStateQuestHelper
{
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
		Map<String, List<ChartingTaskStep>> stepsBySea = new LinkedHashMap<>();

		for (ChartingSeaSection section : ChartingTasksData.getSections())
		{
			List<ChartingTaskStep> steps = new ArrayList<>();
			for (ChartingTaskDefinition definition : section.getTasks())
			{
				var step = new ChartingTaskStep(this, definition);
				chartingSteps.add(step);
				steps.add(step);
			}
			if (!steps.isEmpty())
			{
				stepsBySea.put(section.getSea(), steps);
			}
		}

		buildSidePanel(stepsBySea);
	}

	private void buildSidePanel(Map<String, List<ChartingTaskStep>> stepsBySea)
	{
		var topLevelPanel = new TopLevelPanelDetails("Sea charting");

		// We're ordering based on the in-game order in the cache, so this should remain consistent
		int panelId = 0;

		for (Map.Entry<String, List<ChartingTaskStep>> entry : stepsBySea.entrySet())
		{
			List<QuestStep> steps = new ArrayList<>(entry.getValue());
			var panel = new PanelDetails(entry.getKey(), steps).withId(panelId++);
			var displayCondition = createDisplayCondition(entry.getValue());
			if (displayCondition != null)
			{
				panel.setDisplayCondition(displayCondition);
			}
			topLevelPanel.addPanelDetails(panel);
		}

		panelDetails = new ArrayList<>(List.of(topLevelPanel));
	}

	@Override
	public QuestStep loadStep()
	{
		buildSteps();

		var overviewStep = new DetailedQuestStep(this,
			"Do any incomplete Sea charting task from the sidebar.");

		var chartingConditionalStep = new ReorderableConditionalStep(this, overviewStep);
		for (QuestStep step : chartingSteps)
		{
			if (step instanceof ChartingTaskStep)
			{
				chartingConditionalStep.addStep(((ChartingTaskStep) step).getIncompleteRequirement(), step);
			}
		}

		return chartingConditionalStep;
	}

	private Requirement createDisplayCondition(List<ChartingTaskStep> steps)
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
			.map(ChartingTaskStep::getIncompleteRequirement)
			.toArray(Requirement[]::new);
		return new Conditions(LogicType.OR, requirements);
	}
}
