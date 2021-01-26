package com.questhelper.requirements.conditional;

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
	public boolean check(Client client)
	{
		return operation.check(client.getRealSkillLevel(skill), value);
	}
}