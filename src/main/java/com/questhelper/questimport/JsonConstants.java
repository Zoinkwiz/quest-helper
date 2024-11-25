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

public class JsonConstants
{
	// Requirement Types
	public static final String SKILL_REQUIREMENT = "SkillRequirement";
	public static final String ITEM_REQUIREMENT = "ItemRequirement";
	public static final String VARBIT_REQUIREMENT = "VarbitRequirement";
	public static final String VARPLAYER_REQUIREMENT = "VarplayerRequirement";

	// Generic Parameters
	public static final String ID = "id";

	// Requirement Parameters

	// Skill Requirement
	public static final String PARAM_SKILL = "Skill";
	public static final String PARAM_LEVEL = "Level";
	public static final String PARAM_BOOSTABLE = "Boostable";

	// ItemRequirement
	public static final String PARAM_ITEM_ID = "Item ID";
	public static final String PARAM_QUANTITY = "Quantity";
	public static final String PARAM_EQUIPPED = "Equipped";

	// VarbitRequirement
	public static final String VAR_ID = "Var ID";
	public static final String VAR_VALUE = "Var Value";

	// Step Types
	public static final String DETAILED_QUEST_STEP = "DetailedQuestStep";
	public static final String NPC_STEP = "NpcStep";
	public static final String OBJECT_STEP = "ObjectStep";
	// Add other step types as needed

	// Step Parameters
	public static final String PARAM_TEXT = "Text";
	public static final String PARAM_NPC_ID = "NPC ID";
	public static final String PARAM_OBJECT_ID = "Object ID";
	public static final String PARAM_WORLD_POINT_X = "WorldPoint X";
	public static final String PARAM_WORLD_POINT_Y = "WorldPoint Y";
	public static final String PARAM_WORLD_POINT_Z = "WorldPoint Z";
}
