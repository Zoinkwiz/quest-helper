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

public class StepParameterDefinitions
{
	private static final Map<String, List<String>> stepParameterMap = new HashMap<>();

	static
	{
		stepParameterMap.put(JsonConstants.DETAILED_QUEST_STEP, Arrays.asList(
			JsonConstants.PARAM_TEXT,
			JsonConstants.PARAM_WORLD_POINT_X,
			JsonConstants.PARAM_WORLD_POINT_Y,
			JsonConstants.PARAM_WORLD_POINT_Z
		));

		stepParameterMap.put(JsonConstants.NPC_STEP, Arrays.asList(
			JsonConstants.PARAM_NPC_ID,
			JsonConstants.PARAM_TEXT,
			JsonConstants.PARAM_WORLD_POINT_X,
			JsonConstants.PARAM_WORLD_POINT_Y,
			JsonConstants.PARAM_WORLD_POINT_Z
		));

		stepParameterMap.put(JsonConstants.OBJECT_STEP, Arrays.asList(
			JsonConstants.PARAM_OBJECT_ID,
			JsonConstants.PARAM_TEXT,
			JsonConstants.PARAM_WORLD_POINT_X,
			JsonConstants.PARAM_WORLD_POINT_Y,
			JsonConstants.PARAM_WORLD_POINT_Z
		));
	}

	public static List<String> getParametersForStepType(String stepType)
	{
		return stepParameterMap.getOrDefault(stepType, Collections.emptyList());
	}

	public static String[] getAllStepTypes()
	{
		Set<String> stepTypes = stepParameterMap.keySet();
		return stepTypes.toArray(new String[0]);
	}
}
