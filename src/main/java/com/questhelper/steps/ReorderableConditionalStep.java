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
package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReorderableConditionalStep extends ConditionalStep
{

    List<Integer> sideOrder = new ArrayList<>();
    public ReorderableConditionalStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
    {
        super(questHelper, step, requirements);
    }

    private void organiseSteps()
    {
        LinkedHashMap<Requirement, QuestStep> newSteps = new LinkedHashMap<>();
        for (Integer sidebarId : sideOrder)
        {
            for (Requirement req : steps.keySet())
            {
                QuestStep step = steps.get(req);
                if (step.getId() == null) continue;
                if (step.getId().equals(sidebarId))
                {
					newSteps.put(req, step);
                }
            }
        }
        for (Requirement req : steps.keySet())
        {
            QuestStep step = steps.get(req);
            if (step.getId() == null) newSteps.put(req, step);
        }
        steps = newSteps;
    }

    @Override
    protected void updateSteps()
    {
        List<Integer> sidebarOrder = questHelper.getSidebarOrder();
        if (sidebarOrder != this.sideOrder)
        {
            this.sideOrder = sidebarOrder;
            if (sideOrder != null) organiseSteps();
        }

		Requirement lastPossibleCondition = null;

		for (Requirement conditions : steps.keySet())
		{
			boolean stepIsLocked = steps.get(conditions).isLocked();
			// Null condition usually skipped until the end. Here it can be reordered to earlier so we allow it
			if (conditions == null && !stepIsLocked)
			{
				startUpStep(steps.get(null));
				return;
			}
			else if (conditions != null && conditions.check(client) && !stepIsLocked)
			{
				startUpStep(steps.get(conditions));
				return;
			}
			else if (steps.get(conditions).isBlocker() && stepIsLocked)
			{
				startUpStep(steps.get(lastPossibleCondition));
				return;
			}
			else if (conditions != null && !stepIsLocked)
			{
				lastPossibleCondition = conditions;
			}
		}

		if (!steps.get(null).isLocked())
		{
			startUpStep(steps.get(null));
		}
		else
		{
			startUpStep(steps.get(lastPossibleCondition));
		}
    }
}
