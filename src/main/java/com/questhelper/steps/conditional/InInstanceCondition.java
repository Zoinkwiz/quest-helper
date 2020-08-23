package com.questhelper.steps.conditional;

import net.runelite.api.Client;

public class InInstanceCondition extends ConditionForStep
{
	@Override
	public boolean checkCondition(Client client)
	{
		return client.isInInstancedRegion();
	}
}
