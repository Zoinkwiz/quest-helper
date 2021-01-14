package com.questhelper.requirements;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Skill;

@Getter
public class SkillRequirement extends Requirement
{
	private Skill skill;
	private int requiredLevel;
	private boolean canBeBoosted;
	private String displayText;

	public SkillRequirement(Skill skill, int requiredLevel)
	{
		this.skill = skill;
		this.requiredLevel = requiredLevel;
		this.displayText = getDisplayText();
	}

	public SkillRequirement(Skill skill, int requiredLevel, boolean canBeBoosted)
	{
		this(skill, requiredLevel);
		this.canBeBoosted = canBeBoosted;
	}

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
