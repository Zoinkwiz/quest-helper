package com.questhelper.steps.conditional;

import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class NpcCondition extends ConditionForStep
{
	private final int npcID;
	private NPC npc;
	private boolean npcInScene = false;
	private final WorldPoint worldPoint;

	public NpcCondition(int npcID) {
		this.npcID = npcID;
		this.worldPoint = null;
	}

	public NpcCondition(int npcID, WorldPoint worldPoint) {
		this.npcID = npcID;
		this.worldPoint = worldPoint;
	}

	@Override
	public void initialize(Client client)
	{
		for (NPC npc : client.getNpcs())
		{
			if (npcID == npc.getId())
			{
				this.npc = npc;
				npcInScene = true;
			}
		}
	}

	public boolean checkCondition(Client client)
	{
		if (worldPoint != null)
		{
			if (npc != null)
			{
				WorldPoint wp = npc.getWorldLocation();
				if (wp != null)
				{
					Collection<WorldPoint> wps = WorldPoint.toLocalInstance(client, worldPoint);
					return wps.contains(wp);
				}
			}
			return false;
		}
		else
		{
			return npcInScene;
		}
	}

	public void checkNpcSpawned(NPC npc)
	{
		if (npc.getId() == this.npcID)
		{
			this.npc = npc;
			npcInScene = true;
		}
	}

	public void checkNpcDespawned(int npcID)
	{
		if (npcID == this.npcID)
		{
			npc = null;
			npcInScene = false;
		}
	}

	@Override
	public void loadingHandler() {
		npcInScene = false;
	}
}
