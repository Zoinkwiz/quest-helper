package com.questhelper.steps.conditional;

import net.runelite.api.Client;

public class WeightCondition extends ConditionForStep
{

	private final int weight;
	private final Operation operation;


	public WeightCondition(int weight)
	{
		this.weight = weight;
		this.operation = Operation.EQUAL;
	}

	public WeightCondition(int weight,  Operation operation)
	{
		this.weight = weight;
		this.operation = operation;
	}

	@Override
	public boolean checkCondition(Client client)
	{
		if (operation == Operation.EQUAL)
		{
			return client.getWeight() == weight;
		}
		else if (operation == Operation.LESS_EQUAL)
		{
			return client.getWeight() <= weight;
		}

		else if (operation == Operation.GREATER_EQUAL)
		{
			return client.getWeight() >= weight;
		}

		return false;
	}
}