package com.questhelper.panel.questmaking;

public enum RequirementType
{
	SKILL("SkillRequirement"),
	ITEM("ItemRequirement");

	private final String displayName;

	RequirementType(String displayName)
	{
		this.displayName = displayName;
	}

	public static RequirementType fromString(String text)
	{
		for (RequirementType type : RequirementType.values())
		{
			if (type.displayName.equalsIgnoreCase(text))
			{
				return type;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}

