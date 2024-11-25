package com.questhelper.questimport;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;
import java.util.List;
import java.util.Map;

public class RequirementFactory
{
	public static Requirement createRequirement(RequirementData reqData, ItemManager itemManager)
	{
		String reqType = reqData.getType();
		Map<String, Object> params = reqData.getParameters();

		List<String> requiredParams = RequirementParameterDefinitions.getParametersForRequirementType(reqType);

		// Validate parameters
		for (String param : requiredParams)
		{
			if (!params.containsKey(param))
			{
				throw new IllegalArgumentException("Missing parameter '" + param + "' for requirement type '" + reqType + "'");
			}
		}

		// Create requirement based on type
		switch (reqType)
		{
			case JsonConstants.SKILL_REQUIREMENT:
				return createSkillRequirement(params);

			case JsonConstants.ITEM_REQUIREMENT:
				return createItemRequirement(params, itemManager);

			// Handle other requirement types as needed

			default:
				throw new IllegalArgumentException("Unknown requirement type: " + reqType);
		}
	}

	private static SkillRequirement createSkillRequirement(Map<String, Object> params)
	{
		String skillName = (String) params.get(JsonConstants.PARAM_SKILL);
		int level = ((Number) params.get(JsonConstants.PARAM_LEVEL)).intValue();
		boolean boostable = (Boolean) params.getOrDefault(JsonConstants.PARAM_BOOSTABLE, true);
		Skill skill = Skill.valueOf(skillName.toUpperCase());
		return new SkillRequirement(skill, level, boostable);
	}

	private static ItemRequirement createItemRequirement(Map<String, Object> params, ItemManager itemManager)
	{
		int itemId = ((Number) params.get(JsonConstants.PARAM_ITEM_ID)).intValue();
		int quantity = ((Number) params.getOrDefault(JsonConstants.PARAM_QUANTITY, 1)).intValue();
		boolean equipped = (Boolean) params.getOrDefault(JsonConstants.PARAM_EQUIPPED, false);
		String name = itemManager.getItemComposition(itemId).getName();
		return new ItemRequirement(name, itemId, quantity, equipped);
	}
}
