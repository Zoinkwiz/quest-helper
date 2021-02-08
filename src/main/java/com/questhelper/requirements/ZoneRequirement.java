/*
 *
 *  * Copyright (c) 2021, Zoinkwiz
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
import com.questhelper.questhelpers.QuestUtil;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

public class ZoneRequirement extends AbstractRequirement
{
	private final List<Zone> zones;
	private final boolean checkInZone;
	private String displayText;

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
		this.checkInZone = !checkNotInZone; // This was originally 'checkNotInZone' so we have to maintain that behavior
		this.zones = QuestUtil.toArrayList(zone);
	}

	public ZoneRequirement(WorldPoint... worldPoints)
	{
		this.zones = Stream.of(worldPoints).map(Zone::new).collect(QuestUtil.collectToArrayList());
		this.checkInZone = true;
	}

	public ZoneRequirement(Zone... zone)
	{
		this.zones = QuestUtil.toArrayList(zone);
		this.checkInZone = true;
	}

	public ZoneRequirement(boolean checkInZone, Zone... zone)
	{
		this.zones = QuestUtil.toArrayList(zone);
		this.checkInZone = checkInZone;
	}

	public ZoneRequirement(boolean checkInZone, WorldPoint... worldPoints)
	{
		this.zones = Stream.of(worldPoints).map(Zone::new).collect(QuestUtil.collectToArrayList());
		this.checkInZone = checkInZone;
	}

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		if (player != null && zones != null)
		{
			WorldPoint location = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
			boolean inZone = zones.stream().anyMatch(z -> z.contains(location));
			return inZone == checkInZone;
		}
		return false;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return displayText == null ? "" : displayText;
	}
}
