/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.panel;

import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

public class PanelDetails
{
	@Getter
	String header;

	@Getter
	private final List<QuestStep> steps;

	@Getter
	private QuestStep lockingQuestSteps;

	@Setter
	@Getter
	private Requirement hideCondition;

	@Getter
	private List<Requirement> requirements;

	@Getter
	private List<Requirement> recommended;

	@Getter
	private List<Integer> vars;

	public PanelDetails(String header)
	{
		this.header = header;
		this.steps = new ArrayList<>();
	}

	public PanelDetails(String header, QuestStep... steps)
	{
		this.header = header;
		this.steps = QuestUtil.toArrayList(steps);
		this.requirements = new ArrayList<>();
	}

	public PanelDetails(String header, List<QuestStep> steps, List<Requirement> requirements)
	{
		this.header = header;
		this.steps = steps;
		this.requirements = requirements;
	}

	public PanelDetails(String header, List<QuestStep> steps, Requirement... requirements)
	{
		this(header, steps, Arrays.asList(requirements));
	}

	public PanelDetails(String header, List<QuestStep> steps, List<Requirement> requirements, List<Requirement> recommended)
	{
		this(header, steps, requirements);
		this.recommended = recommended;
	}

	public void setDisplayCondition(Requirement req)
	{
		setHideCondition(new Conditions(LogicType.NOR, req));
	}

	public void setVars(Integer... vars)
	{
		this.vars = Arrays.asList(vars);
	}

	public void setLockingStep(QuestStep lockingStep)
	{
		this.lockingQuestSteps = lockingStep;
	}

	public void addSteps(QuestStep... steps)
	{
		this.steps.addAll(Arrays.asList(steps));
	}

	public boolean contains(QuestStep currentStep)
	{
		if (getSteps().contains(currentStep))
		{
			return true;
		}
		else
		{
			return getSteps().stream()
				    .filter(Objects::nonNull)
					.map(QuestStep::getSubsteps)
					.flatMap(Collection::stream)
					.anyMatch(step -> containsSubStep(currentStep, step));
		}
	}

	private boolean containsSubStep(QuestStep currentStep, QuestStep check)
	{
		if (currentStep.getSubsteps().contains(check) || currentStep == check)
		{
			return true;
		}
		return currentStep.getSubsteps().stream().anyMatch(step -> containsSubStep(step, check));
	}
}
