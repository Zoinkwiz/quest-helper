package com.questhelper.requirements.conditional;

import net.runelite.api.Client;

public class NpcInteractingCondition extends ConditionForStep
{
	final int npcID;

	public NpcInteractingCondition(int npcID)
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