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
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToQuest
{
	ItemManager itemManager;

	public JsonToQuest(ItemManager itemManager)
	{
		this.itemManager = itemManager;
	}
	public JsonQuestHelper importQuestFromJson(Gson gson, String jsonContent)
	{
		JsonQuestHelper newHelper = new JsonQuestHelper();
		QuestData questData = gson.fromJson(jsonContent, QuestData.class);

		// Map requirements
		Map<String, Requirement> requirementMap = getRequirementMap(questData.getRequirements());

		// Map steps and create ConditionalSteps
		ConditionalStep steps = new ConditionalStep(newHelper, new DetailedQuestStep(newHelper, "Default step."));
		for (StepData stepData : questData.getSteps())
		{
			QuestStep questStep = parseQuestStep(newHelper, stepData, requirementMap);
			LogicType logicType = getLogicType(stepData.getLogicType());
			if (stepData.isDefault())
			{
				steps.addStep(null, questStep);
			}
			else
			{
				Requirement[] reqs = getRequirements(stepData.getConditionalRequirements(), requirementMap);
				if (reqs == null)
				{
					System.out.println(questStep.getText());
				}
				Conditions conditions = new Conditions(logicType, reqs);
				steps.addStep(conditions, questStep);
			}
		}

		newHelper.setSteps(steps);
		return newHelper;
	}

	private Requirement parseRequirement(RequirementData reqData)
	{
		Requirement requirement = null;

		switch (reqData.getType())
		{
			case "SkillRequirement":
				String skillName = (String) reqData.getParameters().get("skill");
				int level = ((Number) reqData.getParameters().get("level")).intValue();
				boolean boostable = (Boolean) reqData.getParameters().getOrDefault("boostable", true);
				Skill skill = Skill.valueOf(skillName.toUpperCase());
				requirement = new SkillRequirement(skill, level, boostable);
				break;

			case "ItemRequirement":
				int itemId = ((Number) reqData.getParameters().get("itemId")).intValue();
				int quantity = ((Number) reqData.getParameters().getOrDefault("quantity", 1)).intValue();
				boolean equipped = (Boolean) reqData.getParameters().getOrDefault("equipped", false);
				String name = itemManager.getItemComposition(itemId).getName();
				requirement = new ItemRequirement(name, itemId, quantity, equipped);
				break;

			default:
				// Handle unknown requirement types
				System.err.println("Unknown requirement type: " + reqData.getType());
				break;
		}

		return requirement;
	}

	private QuestStep parseQuestStep(JsonQuestHelper newHelper, StepData stepData, Map<String, Requirement> requirementMap)
	{
		Map<String, Object> params = stepData.getParameters();

		String text;
		WorldPoint wp;
		List<String> reqIds;
		Requirement[] reqs;
		switch (stepData.getType())
		{
			case "DetailedQuestStep":
				text = (String) params.get("text");
				wp = getWorldPoint(params);
				reqIds = stepData.getStepRequirements();
				reqs = getRequirements(reqIds, requirementMap);
				DetailedQuestStep detailedQuestStep = new DetailedQuestStep(newHelper, text);
				if (wp != null) detailedQuestStep.setWorldPoint(wp);
				if (reqs != null) detailedQuestStep.setRequirements(List.of(reqs));
				return detailedQuestStep;
			case "NpcStep":
				var npcId = ((Number) params.get("npcId")).intValue();
				text = (String) params.get("text");
				wp = getWorldPoint(params);
				reqIds = stepData.getStepRequirements();
				reqs = getRequirements(reqIds, requirementMap);
				NpcStep npcStep = new NpcStep(newHelper, npcId, text);
				if (wp != null) npcStep.setWorldPoint(wp);
				if (reqs != null) npcStep.setRequirements(List.of(reqs));
				return npcStep;
			case "ObjectStep":
				var objectId = ((Number) params.get("objectId")).intValue();
				text = (String) params.get("text");
				wp = getWorldPoint(params);
				reqIds = stepData.getStepRequirements();
				reqs = getRequirements(reqIds, requirementMap);
				ObjectStep objectStep = new ObjectStep(newHelper, objectId, text);
				if (wp != null) objectStep.setWorldPoint(wp);
				if (reqs != null) objectStep.setRequirements(List.of(reqs));
				return objectStep;
			default:
				// Handle unknown step types
				System.err.println("Unknown step type: " + stepData.getType());
				break;
		}

		return new DetailedQuestStep(newHelper, "Unable to create step");
	}

	private Map<String, Requirement> getRequirementMap(List<RequirementData> requirementData)
	{
		Map<String, Requirement> requirementMap = new HashMap<>();
		for (RequirementData reqData : requirementData)
		{
			Requirement requirement = parseRequirement(reqData);
			if (requirement != null)
			{
				requirementMap.put(reqData.getId(), requirement);
			}
		}

		return requirementMap;
	}

	private Requirement[] getRequirements(List<String> reqs, Map<String, Requirement> requirementMap)
	{
		if (reqs == null) return null;
		Requirement[] requirements = new Requirement[reqs.size()];
		for (int i=0; i<reqs.size(); i++)
		{
			String req = reqs.get(i);
			requirements[i] = requirementMap.get(req);
		}

		return requirements;
	}

	private WorldPoint getWorldPoint(Map<String, Object> params)
	{
		var x = ((Number) params.getOrDefault("wpX", -1)).intValue();
		var y = ((Number) params.getOrDefault("wpY", -1)).intValue();
		var z = ((Number) params.getOrDefault("wpZ", -1)).intValue();
		if (x == -1 || y == -1 || z == -1) return null;
		return new WorldPoint(x, y, z);
	}

	private LogicType getLogicType(String logicType)
	{
		if (logicType == null) return LogicType.AND;

		switch (logicType.toLowerCase())
		{
            case "or":
				return LogicType.OR;
			case "nor":
				return LogicType.NOR;
			case "nand":
				return LogicType.NAND;
			default:
				return LogicType.AND;
		}
	}
}
