package com.questhelper.requirements;

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
		int skillLevel = canBeBoosted ? client.getBoostedSkillLevel(skill) : client.getRealSkillLevel(skill);
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
