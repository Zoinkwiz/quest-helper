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

import com.google.gson.Gson;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import net.runelite.client.game.ItemManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToQuest
{
	private final ItemManager itemManager;

	public JsonToQuest(ItemManager itemManager)
	{
		this.itemManager = itemManager;
	}

	public JsonQuestHelper importQuestFromJson(Gson gson, String jsonContent)
	{
		JsonQuestHelper newHelper = new JsonQuestHelper();
		QuestData questData = gson.fromJson(jsonContent, QuestData.class);

		Map<RequirementData, Requirement> requirementMap = getRequirementMap(questData.getRequirements());

		Map<StepData, QuestStep> stepMap = getStepMap(newHelper, questData.getSteps(), requirementMap);

		ConditionalStep questSteps = assembleQuestSteps(newHelper, questData.getQuestSteps(), stepMap, requirementMap);

		newHelper.setSteps(questSteps);
		return newHelper;
	}

	private Map<RequirementData, Requirement> getRequirementMap(List<RequirementData> requirementDataList)
	{
		Map<RequirementData, Requirement> requirementMap = new HashMap<>();
		for (RequirementData reqData : requirementDataList)
		{
			try
			{
				Requirement requirement = RequirementFactory.createRequirement(reqData, itemManager);
				if (requirement != null)
				{
					requirementMap.put(reqData, requirement);
				}
			}
			catch (IllegalArgumentException e)
			{
				System.err.println("Error creating requirement with ID " + reqData.getId() + ": " + e.getMessage());
			}
		}
		return requirementMap;
	}

	private Map<StepData, QuestStep> getStepMap(JsonQuestHelper helper, List<StepData> stepDataList, Map<RequirementData, Requirement> requirementMap)
	{
		Map<StepData, QuestStep> stepMap = new HashMap<>();
		for (StepData stepData : stepDataList)
		{
			try
			{
				QuestStep questStep = StepFactory.createStep(helper, stepData, requirementMap);
				if (questStep != null)
				{
					stepMap.put(stepData, questStep);
				}
			}
			catch (IllegalArgumentException e)
			{
				System.err.println("Error creating step with ID " + stepData.getId() + ": " + e.getMessage());
			}
		}
		return stepMap;
	}

	private ConditionalStep assembleQuestSteps(JsonQuestHelper helper, List<QuestStepData> questStepDataList, Map<StepData, QuestStep> stepMap, Map<RequirementData, Requirement> requirementMap)
	{
		ConditionalStep rootStep = new ConditionalStep(helper, new DetailedQuestStep(helper, "Default step."));

		for (QuestStepData questStepData : questStepDataList)
		{
			StepData stepId = questStepData.getStepData();
			QuestStep questStep = stepMap.get(stepId);

			if (questStep == null)
			{
				System.err.println("Step ID not found: " + stepId);
				continue;
			}

			List<Requirement> condRequirements = getRequirements(questStepData.getConditionalRequirements(), requirementMap);
			Conditions conditions = null;
			if (condRequirements != null && !condRequirements.isEmpty())
			{
				conditions = new Conditions(LogicType.NAND, condRequirements);
			}

			rootStep.addStep(conditions, questStep);
		}

		return rootStep;
	}

	private List<Requirement> getRequirements(List<RequirementData> reqDatum, Map<RequirementData, Requirement> requirementMap)
	{
		if (reqDatum == null)
		{
			return null;
		}
		List<Requirement> requirements = new ArrayList<>();
		for (RequirementData reqData : reqDatum)
		{
			Requirement req = requirementMap.get(reqData);
			if (req != null)
			{
				requirements.add(req);
			}
			else
			{
				System.err.println("Requirement ID not found: " + reqData);
			}
		}
		return requirements;
	}
}
