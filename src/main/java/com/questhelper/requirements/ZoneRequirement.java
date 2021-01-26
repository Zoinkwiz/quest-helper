/*
 *
 *  * Copyright (c) 2021, Senmori
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

package com.questhelper.requirements;

import com.questhelper.Zone;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

public class ZoneRequirement extends AbstractRequirement
{
	private final Zone zone;
	private final boolean checkNotInZone;
	private final String displayText;

	/**
	 * Check if the player is either in the specified zone.
	 *
	 * @param displayText display text
	 * @param zone the zone to check
	 */
	public ZoneRequirement(String displayText, Zone zone)
	{
		this(displayText, false, zone);
	}

	/**
	 * Check if the player is either in, or not in, the specified zone.
	 *
	 * @param displayText display text
	 * @param checkNotInZone true to negate this requirement check (i.e. it will check if the player is NOT in the zone)
	 * @param zone the zone to check
	 */
	public ZoneRequirement(String displayText, boolean checkNotInZone, Zone zone)
	{
		this.displayText = displayText;
		this.checkNotInZone = checkNotInZone;
		this.zone = zone;
	}

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		if (player != null && zone != null)
		{
			WorldPoint location = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
			boolean isInZone = zone.contains(location);
			return isInZone && !checkNotInZone || (!isInZone && checkNotInZone);
		}
		return false;
	}

	@Override
	public String getDisplayText()
	{
		return displayText;
	}
}
