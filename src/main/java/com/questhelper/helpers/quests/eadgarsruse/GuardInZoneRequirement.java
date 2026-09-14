/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.eadgarsruse;

import com.questhelper.requirements.SimpleRequirement;
import com.questhelper.requirements.zone.Zone;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

/**
 * Passes when <i>any</i> NPC with the given ID is inside (or, inverted, outside) a {@link Zone}.
 *
 * <p>The existing {@link com.questhelper.requirements.npc.NpcRequirement} cannot be used for this:
 * its zone check returns on the first matching NPC it finds rather than considering them all, so
 * with several identically-IDed guards in the scene it only ever consults whichever one the world
 * view happens to list first. The storeroom guards all share an ID, so we need a true "any" test.</p>
 */
public class GuardInZoneRequirement extends SimpleRequirement
{
	private final int npcId;
	private final Zone zone;
	private final boolean checkNotInZone;

	public GuardInZoneRequirement(int npcId, Zone zone)
	{
		this(npcId, zone, false);
	}

	/**
	 * @param npcId          the guard's NPC ID
	 * @param zone           the area to test against
	 * @param checkNotInZone when true, this passes only while no matching guard is in the zone
	 */
	public GuardInZoneRequirement(int npcId, Zone zone, boolean checkNotInZone)
	{
		assert (zone != null);
		this.npcId = npcId;
		this.zone = zone;
		this.checkNotInZone = checkNotInZone;
	}

	@Override
	public boolean check(Client client)
	{
		boolean anyInZone = false;

		for (NPC npc : client.getTopLevelWorldView().npcs())
		{
			if (npc.getId() != npcId)
			{
				continue;
			}

			// The storeroom is not instanced, so the NPC's world location needs no translation.
			WorldPoint location = npc.getWorldLocation();
			if (location != null && zone.contains(location))
			{
				anyInZone = true;
				break;
			}
		}

		return anyInZone != checkNotInZone;
	}
}
