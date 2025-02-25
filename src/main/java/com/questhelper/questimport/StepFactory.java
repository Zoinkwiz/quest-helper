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

import com.questhelper.requirements.Requirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepFactory
{
	public static QuestStep createStep(JsonQuestHelper helper, StepData stepData, Map<RequirementData, Requirement> requirementMap)
	{
		String stepType = stepData.getType();
		Map<String, Object> params = stepData.getParameters();

		List<String> requiredParams = StepParameterDefinitions.getParametersForStepType(stepType);

		for (String param : requiredParams)
		{
			if (!params.containsKey(param))
			{
				throw new IllegalArgumentException("Missing parameter '" + param + "' for step type '" + stepType + "'");
			}
		}

		List<RequirementData> reqIds = stepData.getStepRequirements();
		List<Requirement> requirements = getRequirements(reqIds, requirementMap);

		switch (stepType)
		{
			case JsonConstants.DETAILED_QUEST_STEP:
				return createDetailedQuestStep(helper, params, requirements);

			case JsonConstants.NPC_STEP:
				return createNpcStep(helper, params, requirements);

			case JsonConstants.OBJECT_STEP:
				return createObjectStep(helper, params, requirements);

			default:
				throw new IllegalArgumentException("Unknown step type: " + stepType);
		}
	}

	private static DetailedQuestStep createDetailedQuestStep(JsonQuestHelper helper, Map<String, Object> params, List<Requirement> requirements)
	{
		String text = (String) params.get(JsonConstants.PARAM_TEXT);
		WorldPoint wp = getWorldPoint(params);

		DetailedQuestStep step = new DetailedQuestStep(helper, text);
		if (wp != null)
		{
			step.setWorldPoint(wp);
		}
		if (requirements != null)
		{
			step.setRequirements(requirements);
		}
		return step;
	}

	private static NpcStep createNpcStep(JsonQuestHelper helper, Map<String, Object> params, List<Requirement> requirements)
	{
		int npcId = ((Number) params.get(JsonConstants.PARAM_NPC_ID)).intValue();
		String text = (String) params.get(JsonConstants.PARAM_TEXT);
		WorldPoint wp = getWorldPoint(params);

		NpcStep step = new NpcStep(helper, npcId, text);
		if (wp != null)
		{
			step.setWorldPoint(wp);
		}
		if (requirements != null)
		{
			step.setRequirements(requirements);
		}
		return step;
	}

	private static ObjectStep createObjectStep(JsonQuestHelper helper, Map<String, Object> params, List<Requirement> requirements)
	{
		int objectId = ((Number) params.get(JsonConstants.PARAM_OBJECT_ID)).intValue();
		String text = (String) params.get(JsonConstants.PARAM_TEXT);
		WorldPoint wp = getWorldPoint(params);

		ObjectStep step = new ObjectStep(helper, objectId, text);
		if (wp != null)
		{
			step.setWorldPoint(wp);
		}
		if (requirements != null)
		{
			step.setRequirements(requirements);
		}
		return step;
	}
	private static WorldPoint getWorldPoint(Map<String, Object> params)
	{
		if (!params.containsKey(JsonConstants.PARAM_WORLD_POINT_X) ||
			!params.containsKey(JsonConstants.PARAM_WORLD_POINT_Y) ||
			!params.containsKey(JsonConstants.PARAM_WORLD_POINT_Z))
		{
			return null;
		}
		int x = ((Number) params.get(JsonConstants.PARAM_WORLD_POINT_X)).intValue();
		int y = ((Number) params.get(JsonConstants.PARAM_WORLD_POINT_Y)).intValue();
		int z = ((Number) params.get(JsonConstants.PARAM_WORLD_POINT_Z)).intValue();
		return new WorldPoint(x, y, z);
	}

	private static List<Requirement> getRequirements(List<RequirementData> reqIds, Map<RequirementData, Requirement> requirementMap)
	{
		if (reqIds == null)
		{
			return null;
		}
		List<Requirement> requirements = new ArrayList<>();
		for (RequirementData reqData : reqIds)
		{
			Requirement req = requirementMap.get(reqData);
			if (req != null)
			{
				requirements.add(req);
			}
			else
			{
				System.err.println("Requirement not found: " + reqData);
			}
		}
		return requirements;
	}
}
