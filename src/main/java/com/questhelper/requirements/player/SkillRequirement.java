/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.questhelper.requirements.player;

import com.questhelper.requirements.AbstractRequirement;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Skill;

/**
 * Requirement that checks if a player meets a certain skill level.
 */
@Getter
public class SkillRequirement extends AbstractRequirement
{
	private final Skill skill;
	private final int requiredLevel;
	private boolean canBeBoosted;
	private String displayText;

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 */
	public SkillRequirement(Skill skill, int requiredLevel)
	{
		this.skill = skill;
		this.requiredLevel = requiredLevel;
		this.displayText = getDisplayText();
	}

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 * @param canBeBoosted if the skill can be boosted to meet this requirement
	 */
	public SkillRequirement(Skill skill, int requiredLevel, boolean canBeBoosted)
	{
		this(skill, requiredLevel);
		this.canBeBoosted = canBeBoosted;
	}

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 * @param canBeBoosted if this skill check can be boosted to meet this requirement
	 * @param displayText the display text
	 */
	public SkillRequirement(Skill skill, int requiredLevel, boolean canBeBoosted, String displayText)
	{
		this(skill, requiredLevel, canBeBoosted);
		this.displayText = displayText;
	}

	@Override
	public boolean check(Client client)
	{
		int skillLevel = canBeBoosted ? Math.max(client.getBoostedSkillLevel(skill), client.getRealSkillLevel(skill)) :
			client.getRealSkillLevel(skill);
		return skillLevel >= requiredLevel;
	}

	@Override
	public String getDisplayText()
	{
		String returnText;
		if (displayText != null)
		{
			returnText = displayText;
		}
		else
		{
			returnText = requiredLevel + " " + skill.getName();
		}

		if (canBeBoosted)
		{
			returnText += " (boostable)";
		}

		return returnText;
	}
}
