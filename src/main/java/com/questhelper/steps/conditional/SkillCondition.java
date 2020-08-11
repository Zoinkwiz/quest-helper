package com.questhelper.steps.conditional;

import net.runelite.api.Client;
import net.runelite.api.Skill;

public class SkillCondition extends ConditionForStep
{

	private final Skill skill;
	private final int value;
	private final Operation operation;


	public SkillCondition(Skill skill, int value)
	{
		this.skill = skill;
		this.value = value;
		this.operation = Operation.EQUAL;
	}

	public SkillCondition(Skill skill, int value, Operation operation)
	{
		this.skill = skill;
		this.value = value;
		this.operation = operation;
	}

	@Override
	public boolean checkCondition(Client client)
	{
		if (operation == Operation.EQUAL)
		{
			return client.getRealSkillLevel(skill) == value;
		}
		else if (operation == Operation.NOT_EQUAL)
		{
			return client.getRealSkillLevel(skill) != value;
		}
		else if (operation == Operation.LESS_EQUAL)
		{
			return client.getRealSkillLevel(skill) <= value;
		}

		else if (operation == Operation.GREATER_EQUAL)
		{
			return client.getRealSkillLevel(skill) >= value;
		}

		return false;
	}
}