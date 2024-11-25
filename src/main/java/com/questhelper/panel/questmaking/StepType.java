package com.questhelper.panel.questmaking;

public enum StepType
{
	NPC_STEP("NpcStep"),
	OBJECT_STEP("ObjectStep");

	private final String displayName;

	StepType(String displayName)
	{
		this.displayName = displayName;
	}

	public static StepType fromString(String text)
	{
		for (StepType type : StepType.values())
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
