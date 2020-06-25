/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.steps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import com.questhelper.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.QuestHelperPlugin;
import static com.questhelper.QuestHelperWorldOverlay.IMAGE_Z_OFFSET;
import net.runelite.client.ui.overlay.OverlayUtil;

public class NpcTalkStep extends DetailedQuestStep
{
	@Inject
	protected Client client;

	private final int npcID;
	private NPC npc;

	public NpcTalkStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, worldPoint, text, itemRequirements);
		this.npcID = npcID;
	}

	@Override
	public void startUp()
	{
		super.startUp();
		for (NPC npc : client.getNpcs())
		{
			if (npcID == npc.getId())
			{
				this.npc = npc;
				client.setHintArrow(npc);
			}
		}
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		npc = null;
		client.clearHintArrow();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == npcID && npc == null)
		{
			npc = event.getNpc();
			client.setHintArrow(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().equals(npc))
		{
			npc = null;
		}
	}

	@Override
	public void setArrow()
	{
		if (npc != null
			&& (client.getHintArrowNpc() == null
			|| !client.getHintArrowNpc().equals(npc)))
		{
			client.setHintArrow(npc);
		} else if (worldPoint != null
			&& client.getHintArrowPoint() == null
			&& (client.getHintArrowNpc() == null
			|| !client.getHintArrowNpc().equals(npc)))
		{
			Collection<WorldPoint> localWorldPoints = WorldPoint.toLocalInstance(client, worldPoint);
			if (localWorldPoints.isEmpty())
			{
				return;
			}
			client.setHintArrow(localWorldPoints.iterator().next());
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		setArrow();
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		super.makeWorldOverlayHint(graphics, plugin);

		if (inCutscene)
		{
			return;
		}

		Collection<WorldPoint> localWorldPoints = WorldPoint.toLocalInstance(client, worldPoint);
		if (localWorldPoints.isEmpty())
		{
			return;
		}

		if (npc == null)
		{
			return;
		}

		OverlayUtil.renderActorOverlayImage(graphics, npc, getQuestImage(), Color.CYAN, IMAGE_Z_OFFSET);
	}
}
