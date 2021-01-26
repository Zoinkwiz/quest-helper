package com.questhelper.requirements.conditional;

import net.runelite.api.Client;

public class InInstanceCondition extends ConditionForStep
{
	@Override
	public boolean check(Client client)
	{
		return client.isInInstancedRegion();
	}
}
