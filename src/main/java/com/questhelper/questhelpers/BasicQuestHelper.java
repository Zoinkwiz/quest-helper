/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.questhelpers;

import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

public abstract class BasicQuestHelper extends QuestHelper
{
	protected Map<Integer, QuestStep> steps;
	protected int var;

	@Override
	public void init()
	{
		if (steps == null)
		{
			steps = loadSteps();
		}
	}

	@Override
	public void startUp(QuestHelperConfig config)
	{
		steps = loadSteps();
		this.config = config;
		instantiateSteps(steps.values());
		var = getVar();
		startUpStep(steps.get(var));
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
	}

	@Override
	public boolean updateQuest()
	{
		if (var < getVar())
		{
			var = getVar();
			shutDownStep();
			startUpStep(steps.get(var));
			return true;
		}
		return false;
	}

	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> panelSteps = new ArrayList<>();
		steps.forEach((id, step) -> panelSteps.add(new PanelDetails("", step)));
		return panelSteps;
	}

	public abstract Map<Integer, QuestStep> loadSteps();

	protected Requirement nor(Requirement... condition)
	{
		return new Conditions(LogicType.NOR, condition);
	}
}
