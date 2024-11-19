/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.requirements.npc;

import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.AbstractRequirement;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// TODO: REMOVE/MERGE WITH NpcCondition
public class NpcRequirement extends AbstractRequirement
{
	private final int npcID;
	private final String npcName;

	@Setter
	short[] npcColorOverrides;

	/**
	 * If zone is null, the check won't take any zone into account
	 */
	private final @Nullable Zone zone;
	private final String displayText;
	private final boolean checkNotInZone;

	/**
	 * Check for the existence of an NPC within your canvas.
	 *
	 * @param npcID the NPC to check for
	 */
	public NpcRequirement(int npcID)
	{
		this(String.valueOf(npcID), npcID, false, null);
	}


	/**
	 * Check for the existence of an NPC within your canvas.
	 *
	 * @param displayText the display text
	 * @param npcID the NPC to check for
	 */
	public NpcRequirement(String displayText, int npcID)
	{
		this(displayText, npcID, false, null);
	}

	/**
	 * Check if a given NPC is in a specified {@link Zone}.
	 *
	 * @param displayText the display text
	 * @param npcID the {@link NPC} to check for
	 * @param worldPoint the location to check for the NPC
	 */
	public NpcRequirement(String displayText, int npcID, WorldPoint worldPoint)
	{
		this(displayText, npcID, false, new Zone(worldPoint));
	}

	/**
	 * Check if a given NPC is in a specified {@link Zone}.
	 *
	 * @param displayText the display text
	 * @param npcID the {@link NPC} to check for
	 * @param zone the zone to check.
	 */
	public NpcRequirement(String displayText, int npcID, Zone zone)
	{
		this(displayText, npcID, false, zone);
	}

	/**
	 * Check if a given NPC is in a specified {@link Zone}.
	 * <br>
	 * If {@param checkNotInZone} is true, this will check if the NPC is NOT in the zone.
	 *
	 * @param displayText the display text
	 * @param npcID the {@link NPC} to check for
	 * @param checkNotInZone determines whether to check if the NPC is in the zone or not
	 * @param zone the zone to check.
	 */
	public NpcRequirement(String displayText, int npcID, boolean checkNotInZone, Zone zone)
	{
		this(displayText, npcID, null, checkNotInZone, zone);
	}

	public NpcRequirement(String displayText, int npcID, String npcName, boolean checkNotInZone, Zone zone)
	{
		this.displayText = displayText;
		this.npcName = npcName;
		this.npcID = npcID;
		this.zone = zone;
		this.checkNotInZone = checkNotInZone;
	}

	public NpcRequirement(int npcID, String npcName)
	{
		this("DO NOT DISPLAY", npcID, npcName, false, null);
	}

	// TODO: Either remove/merge entire Requirement for NpcCondition, or make this instead be onNpcSpawned/Despawned/Changed.
	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		List<NPC> found = client.getTopLevelWorldView().npcs().stream()
				.filter(npc -> npc.getId() == npcID)
				.filter(npc -> npcName == null || (npc.getName() != null && npc.getName().equals(npcName)))
				.collect(Collectors.toList());

		if (!found.isEmpty())
		{
			if (zone != null)
			{
				for (NPC npc : found)
				{
					WorldPoint npcLocation = WorldPoint.fromLocalInstance(client,  npc.getLocalLocation(), 2);
					if (npcLocation != null)
					{
						boolean inZone = zone.contains(npcLocation);
						setState(inZone && !checkNotInZone || (!inZone && checkNotInZone));
						return;
					}
				}
				setState(checkNotInZone);
				return;
			}
			setState(true); // the NPC exists, and we aren't checking for its location
			return;
		}
		setState(false); // npc not in scene
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return displayText;
	}
}
