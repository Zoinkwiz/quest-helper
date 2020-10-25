package com.questhelper.steps.conditional;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;

public class FollowerCondition extends ConditionForStep
{
	final int npcID;

	public FollowerCondition(int npcID)
	{
		this.npcID = npcID;
	}

	@Override
	public boolean checkCondition(Client client)
	{
		for (NPC npc : client.getNpcs())
		{
			Actor ta = npc.getInteracting();
			if (ta != null && client.getLocalPlayer() == ta)
			{
				if (npcID == npc.getId())
				{
					return true;
				}
			}
		}

		return false;
	}
}