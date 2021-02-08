/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.requirements.conditional;

import com.questhelper.Zone;
import java.util.ArrayList;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcChanged;

public class NpcCondition extends ConditionForStep
{
	private final int npcID;
	private final ArrayList<NPC> npcs = new ArrayList<>();
	private boolean npcInScene = false;
	private final Zone zone;

	public NpcCondition(int npcID)
	{
		this.npcID = npcID;
		this.zone = null;
	}

	public NpcCondition(int npcID, WorldPoint worldPoint)
	{
		this.npcID = npcID;
		this.zone = new Zone(worldPoint, worldPoint);
	}

	public NpcCondition(int npcID, Zone zone)
	{
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
				this.npcs.add(npc);
				npcInScene = true;
			}
		}
	}

	public boolean check(Client client)
	{
		if (zone != null)
		{
			for (NPC npc : npcs)
			{
				if (npc != null)
				{
					WorldPoint wp = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
					if (wp != null)
					{
						if (zone.contains(wp))
						{
							return true;
						}
					}
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
			npcs.add(npc);
			npcInScene = true;
		}
	}

	public void checkNpcDespawned(NPC npc)
	{
		if (npcs.contains(npc))
		{
			npcs.remove(npc);
			npcInScene = false;
		}
	}

	public void checkNpcChanged(NpcChanged npcChanged)
	{
		if (npcs.contains(npcChanged.getNpc()) && npcChanged.getNpc().getId() != this.npcID)
		{
			this.npcs.remove(npcChanged.getNpc());
			npcInScene = false;
		}

		if (npcChanged.getNpc().getId() == this.npcID)
		{
			this.npcs.add(npcChanged.getNpc());
			npcInScene = true;
		}
	}

	@Override
	public void updateHandler()
	{
		npcInScene = false;
	}
}
