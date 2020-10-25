package com.questhelper.steps.conditional;

import com.questhelper.Zone;
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
	private final Zone zone;

	public NpcCondition(int npcID) {
		this.npcID = npcID;
		this.zone = null;
	}

	public NpcCondition(int npcID, WorldPoint worldPoint) {
		this.npcID = npcID;
		this.zone = new Zone(worldPoint, worldPoint);
	}

	public NpcCondition(int npcID, Zone zone) {
		this.npcID = npcID;
		this.zone = zone;
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
		if (zone != null)
		{
			if (npc != null)
			{
				WorldPoint wp = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
				if (wp != null)
				{
					return zone.contains(wp);
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
