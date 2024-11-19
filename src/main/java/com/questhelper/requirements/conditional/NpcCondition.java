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

import com.questhelper.managers.ActiveRequirementsManager;
import com.questhelper.requirements.zone.Zone;
import java.util.ArrayList;

import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class NpcCondition extends ConditionForStep
{
	private final int npcID;
	private final ArrayList<NPC> npcs = new ArrayList<>();
	private boolean npcInScene = false;
	private final Zone zone;

	@Setter
	private Integer animationIDRequired;

	public NpcCondition(int npcID)
	{
		this.npcID = npcID;
		this.zone = null;
	}

	public NpcCondition(int npcID, WorldPoint worldPoint)
	{
		assert(worldPoint != null);

		this.npcID = npcID;
		this.zone = new Zone(worldPoint, worldPoint);
	}

	public NpcCondition(int npcID, Zone zone)
	{
		assert(zone != null);

		this.npcID = npcID;
		this.zone = zone;
	}

	@Override
	public void register(Client client, EventBus eventBus, ActiveRequirementsManager activeRequirementsManager)
	{
		super.register(client, eventBus, activeRequirementsManager);
		initialize(client);
	}

	@Override
	public void unregister(EventBus eventBus)
	{
		super.unregister(eventBus);
		this.npcs.clear();
		npcInScene = false;
	}

	public void initialize(Client client)
	{
		for (NPC npc : client.getTopLevelWorldView().npcs())
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
					if (isInZone(client, npc) && hasCorrectAnimation(npc)) return true;
				}
			}
			return false;
		}
		else
		{
			return npcInScene;
		}
	}

	@NonNull
	@Override
	public String getDisplayText()
	{
		return "Need NPC with ID " + npcID;
	}

	private boolean isInZone(Client client, NPC npc)
	{
		if (zone == null) return true;

		WorldPoint wp = WorldPoint.fromLocalInstance(client, npc.getLocalLocation());
		if (wp != null)
		{
			return zone.contains(wp);
		}
		return false;
	}

	private boolean hasCorrectAnimation(NPC npc)
	{
		return animationIDRequired == null || npc.getAnimation() == animationIDRequired;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getId() == this.npcID)
		{
			npcs.add(npc);
			npcInScene = true;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
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

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
		{
			npcInScene = false;
		}
	}
}
