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

		for (String param : requiredParams)
		{
			if (!params.containsKey(param))
			{
				throw new IllegalArgumentException("Missing parameter '" + param + "' for requirement type '" + reqType + "'");
			}
		}

		switch (reqType)
		{
			case JsonConstants.SKILL_REQUIREMENT:
				return createSkillRequirement(params);

			case JsonConstants.ITEM_REQUIREMENT:
				return createItemRequirement(params, itemManager);
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
