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

import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.util.Operation;
import java.awt.Color;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.annotation.Nonnull;
import static net.runelite.api.Skill.THIEVING;

/**
 * Requirement that checks if a player meets a certain skill level.
 */
@Getter
public class SkillRequirement extends AbstractRequirement
{
	private final Skill skill;
	private final int requiredLevel;
	private final Operation operation;
	private boolean canBeBoosted;
	private String displayText;
	private Boosts selectedSkill;
	private int currentSkill;
	private int highestBoost;

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 * @param operation what type of check we're making on the stat
	 */
	public SkillRequirement(Skill skill, int requiredLevel, Operation operation)
	{
		assert(skill != null);
		this.skill = skill;
		this.requiredLevel = requiredLevel;
		this.displayText = getDisplayText();
		this.operation = operation;
		shouldCountForFilter = true;
	}

	/**
	 * Check if a player has a certain skill level
	 *
	 * @param skill the {@link Skill} to check
	 * @param requiredLevel the required level for this Requirement to pass
	 */
	public SkillRequirement(Skill skill, int requiredLevel)
	{
		this(skill, requiredLevel, Operation.GREATER_EQUAL);
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

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		int skillLevel = canBeBoosted ? Math.max(client.getBoostedSkillLevel(skill), client.getRealSkillLevel(skill)) :
				client.getRealSkillLevel(skill);
		setState(skillLevel >= requiredLevel);
	}
	
	public boolean checkRange(Skill skill, int requiredLevel, Client client, QuestHelperConfig config)
	{
		for (Boosts boostSkills : Boosts.values())
		{
			if (skill.getName().equals(boostSkills.getName()))
			{
				selectedSkill = boostSkills;
			}
		}

		currentSkill = Math.max(client.getBoostedSkillLevel(skill), client.getRealSkillLevel(skill));
		highestBoost = selectedSkill.getHighestBoost();

		if (config.stewBoosts() && highestBoost < 5)
		{
			highestBoost = 5;
		}
		else if (skill == THIEVING)
		{
			//player only has access to Summer sq'irk juice at level 65 thieving which is the default boost value for thieving, currently that's blind to player current skill level
			if (client.getRealSkillLevel(skill) < 65)
			{
				highestBoost = 2; //autumn sq'irk
			}
			else if (client.getRealSkillLevel(skill) < 45)
			{
				highestBoost = 1;  //spring sq'irk
			}
		}

		return requiredLevel - highestBoost <= currentSkill;
	}

	public int checkBoosted(Client client, QuestHelperConfig config)
	{
		int skillLevel = canBeBoosted ? Math.max(client.getBoostedSkillLevel(skill), client.getRealSkillLevel(skill)) :
			client.getRealSkillLevel(skill);

		if (skillLevel >= requiredLevel)
		{
			return 1;
		} else if (canBeBoosted && checkRange(skill, requiredLevel, client, config))
		{
			return 2;
		} else {
			return 3;
		}
	}

	@Nonnull
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

	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		switch (checkBoosted(client, config)){
			case 1:
				return config.passColour();
			case 2:
				return config.boostColour();
			case 3:
				return config.failColour();
		}
		return config.failColour();
	}
}
