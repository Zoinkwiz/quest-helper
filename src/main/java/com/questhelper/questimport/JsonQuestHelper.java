/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.questimport;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.List;

public class JsonQuestHelper extends ComplexStateQuestHelper
{
	ConditionalStep steps;

	public void setSteps(ConditionalStep conditionalStep)
	{
		this.steps = conditionalStep;
	}

	@Override
	public QuestStep loadStep()
	{
		return steps;
	}

	@Override
	protected void setupRequirements()
	{

	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		int i = 0;
		QuestStep defaultStep = null;
		for (var questStep : steps.getSteps())
		{
			if (i == 0)
			{
				defaultStep = questStep;
				i++;
				continue;
			}

			PanelDetails details = makePanelDetail(questStep, String.valueOf(i));
			if (details == null) continue;
			allSteps.add(details);
			i++;
		}
		if (defaultStep != null)
		{
			PanelDetails details = makePanelDetail(defaultStep, "default");
			if (details != null) allSteps.add(details);
		}

		return allSteps;
	}

	private PanelDetails makePanelDetail(QuestStep questStep, String panelText)
	{
		if (questStep == null) return null;
		if (questStep.getText().get(0).equals("Default step.")) return null;
		PanelDetails details = new PanelDetails("Step " + panelText);
		if (questStep instanceof DetailedQuestStep)
		{
			var reqs = ((DetailedQuestStep) questStep).getRequirements();
			if (reqs != null) details.setRequirements(reqs);
		}
		questStep.setLockable(true);
		details.addSteps(questStep);
		details.setLockingStep(questStep);

		return details;
	}

	@Override
	public boolean isCompleted()
	{
		return false;
	}

	@Override
	public int getVar()
	{
		return 0;
	}
}
