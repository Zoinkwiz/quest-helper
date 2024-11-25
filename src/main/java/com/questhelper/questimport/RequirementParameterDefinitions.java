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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequirementParameterDefinitions
{
	private static final Map<String, List<String>> requirementParameterMap = new HashMap<>();

	static
	{
		requirementParameterMap.put(JsonConstants.SKILL_REQUIREMENT, Arrays.asList(
			JsonConstants.PARAM_SKILL,
			JsonConstants.PARAM_LEVEL,
			JsonConstants.PARAM_BOOSTABLE
		));
		
		requirementParameterMap.put(JsonConstants.ITEM_REQUIREMENT, Arrays.asList(
			JsonConstants.PARAM_ITEM_ID,
			JsonConstants.PARAM_QUANTITY,
			JsonConstants.PARAM_EQUIPPED
		));
	}

	public static List<String> getParametersForRequirementType(String requirementType)
	{
		return requirementParameterMap.getOrDefault(requirementType, Collections.emptyList());
	}

	public static String[] getAllRequirementTypes()
	{
		Set<String> requirementTypes = requirementParameterMap.keySet();
		return requirementTypes.toArray(new String[0]);
	}
}
