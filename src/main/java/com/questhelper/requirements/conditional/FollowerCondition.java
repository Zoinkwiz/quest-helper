package com.questhelper.requirements.conditional;

import net.runelite.api.Client;

public class FollowerCondition extends ConditionForStep
{
	final int npcID;

	public FollowerCondition(int npcID)
	{
		this.npcID = npcID;
	}

	@Override
	public boolean check(Client client)
	{
		return client.getNpcs().stream()
			.filter(npc -> npc.getInteracting() != null)
			.filter(npc -> npc.getInteracting() == client.getLocalPlayer())
			.anyMatch(npc -> npc.getId() == npcID);
	}
}