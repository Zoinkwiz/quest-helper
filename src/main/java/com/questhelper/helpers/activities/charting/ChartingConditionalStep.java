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

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.ReorderableConditionalStep;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import java.util.Map;

public class ChartingConditionalStep extends ReorderableConditionalStep
{
	@Inject
	protected Client client;

	private final RuneliteRequirement proximityModeRequirement;

	public ChartingConditionalStep(QuestHelper questHelper, QuestStep step, String selectionMethodConfigKey, Requirement... requirements)
	{
		super(questHelper, step, requirements);
		this.proximityModeRequirement = new RuneliteRequirement(questHelper.getConfigManager(), selectionMethodConfigKey, "PROXIMITY");
	}

	@Override
	protected void updateSteps()
	{
		if (proximityModeRequirement.check(client))
		{
			updateStepsProximity();
		}
		else
		{
			super.updateSteps();
		}
	}

	private void updateStepsProximity()
	{
		WorldPoint playerLocation = getPlayerLocation();
		if (playerLocation == null)
		{
			super.updateSteps();
			return;
		}

		QuestStep closestStep = null;
		int closestDistance = Integer.MAX_VALUE;

		for (Map.Entry<Requirement, QuestStep> entry : steps.entrySet())
		{
			var condition = entry.getKey();
			var step = entry.getValue();
			DetailedQuestStep detailedStep;
			if (!(step instanceof DetailedQuestStep))
			{
				if (step instanceof PuzzleWrapperStep && ((PuzzleWrapperStep) step).getSolvingStep() instanceof DetailedQuestStep)
				{
					detailedStep = (DetailedQuestStep) ((PuzzleWrapperStep) step).getSolvingStep();
				}
				else
				{
					continue;
				}
			}
			else
			{
				detailedStep = (DetailedQuestStep) step;
			}

			if (condition != null && !condition.check(client))
			{
				continue;
			}

			if (detailedStep.isLocked())
			{
				continue;
			}

			if (detailedStep instanceof ChartingTaskInterface)
			{
				ChartingTaskInterface chartingStep = (ChartingTaskInterface) step;
				if (!chartingStep.getCanDoRequirement().check(client))
				{
					continue;
				}
			}

			var stepLocation = detailedStep.getDefinedPoint();
			if (stepLocation == null) continue;
			int distance = stepLocation.distanceTo(playerLocation);
			if (distance < closestDistance)
			{
				closestDistance = distance;
				closestStep = step;
			}
		}

		if (closestStep != null)
		{
			startUpStep(closestStep);
		}
		else
		{
			if (steps.get(null) != null && !steps.get(null).isLocked())
			{
				startUpStep(steps.get(null));
			}
		}
	}

	private WorldPoint getPlayerLocation()
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		return QuestPerspective.getWorldPointConsideringWorldView(
			client,
			client.getLocalPlayer().getWorldView(),
			client.getLocalPlayer().getWorldLocation()
		);
	}
}

