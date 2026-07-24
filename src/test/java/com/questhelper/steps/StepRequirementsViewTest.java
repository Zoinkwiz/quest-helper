/*
 * Copyright (c) 2026, Ruined Heir
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class StepRequirementsViewTest
{
	@Test
	public void conditionalStepRequirementsAreReadOnlyLiveView()
	{
		Requirement first = mock(Requirement.class);
		Requirement second = mock(Requirement.class);
		ConditionalStep step = new ConditionalStep(
			mock(QuestHelper.class),
			mock(QuestStep.class),
			first);
		List<Requirement> requirements = step.getRequirements();

		step.addRequirement(second);

		assertEquals(List.of(first, second), requirements);
		assertThrows(UnsupportedOperationException.class, requirements::clear);
	}

	@Test
	public void detailedOwnerStepRequirementsAreReadOnlyLiveView()
	{
		Requirement first = mock(Requirement.class);
		Requirement second = mock(Requirement.class);
		MutableDetailedOwnerStep step = new MutableDetailedOwnerStep(
			mock(QuestHelper.class),
			first);
		List<Requirement> requirements = step.getRequirements();

		step.addRequirement(second);

		assertEquals(List.of(first, second), requirements);
		assertThrows(UnsupportedOperationException.class, requirements::clear);
	}

	private static final class MutableDetailedOwnerStep extends DetailedOwnerStep
	{
		private MutableDetailedOwnerStep(QuestHelper questHelper, Requirement... requirements)
		{
			super(questHelper, requirements);
		}

		private void addRequirement(Requirement requirement)
		{
			requirements.add(requirement);
		}
	}
}
