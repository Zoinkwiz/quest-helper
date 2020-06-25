package com.questhelper.steps.conditional;

import net.runelite.api.Client;
import net.runelite.api.NPC;

public class NpcCondition extends ConditionForStep
{
	private int npcID;
	private boolean npcInScene = false;

	public NpcCondition(int npcID) {
		this.npcID = npcID;
	}

	@Override
	public void initialize(Client client)
	{
		for (NPC npc : client.getNpcs())
		{
			if (npcID == npc.getId())
			{
				npcInScene = true;
			}
		}
	}

	public boolean checkCondition(Client client)
	{
		return npcInScene;
	}

	public void checkNpcSpawned(int npcID)
	{
		if (npcID == this.npcID)
		{
			npcInScene = true;
		}
	}

	public void checkNpcDespawned(int npcID)
	{
		if (npcID == this.npcID)
		{
			npcInScene = false;
		}
	}

	@Override
	public void loadingHandler() {
		npcInScene = false;
	}
}
