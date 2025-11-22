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

package com.questhelper.requirements.zone;

import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.util.Utils;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.WorldEntity;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

public class ZoneRequirement extends AbstractRequirement
{
	@Getter
	private final List<Zone> zones;
	private final boolean checkInZone;
	private String displayText;

	/**
	 * Check if the player is either in the specified zone.
	 *
	 * @param displayText display text
	 * @param zone        the zone to check
	 */
	public ZoneRequirement(String displayText, Zone zone)
	{
		this(displayText, false, zone);
	}

	/**
	 * Check if the player is either in, or not in, the specified zone.
	 *
	 * @param displayText    display text
	 * @param checkNotInZone true to negate this requirement check (i.e. it will check if the player is NOT in the zone)
	 * @param zone           the zone to check
	 */
	public ZoneRequirement(String displayText, boolean checkNotInZone, Zone zone)
	{
		assert(zone != null);
		this.displayText = displayText;
		this.checkInZone = !checkNotInZone; // This was originally 'checkNotInZone' so we have to maintain that behavior
		this.zones = QuestUtil.toArrayList(zone);
	}

	public ZoneRequirement(WorldPoint... worldPoints)
	{
		assert(Utils.varargsNotNull(worldPoints));
		this.zones = Stream.of(worldPoints).map(Zone::new).collect(QuestUtil.collectToArrayList());
		this.checkInZone = true;
	}

	public ZoneRequirement(Zone... zone)
	{
		assert(Utils.varargsNotNull(zone));
		this.zones = QuestUtil.toArrayList(zone);
		this.checkInZone = true;
	}

	public ZoneRequirement(boolean checkInZone, Zone... zone)
	{
		assert(Utils.varargsNotNull(zone));
		this.zones = QuestUtil.toArrayList(zone);
		this.checkInZone = checkInZone;
	}

	public ZoneRequirement(boolean checkInZone, WorldPoint... worldPoints)
	{
		assert(Utils.varargsNotNull(worldPoints));
		this.zones = Stream.of(worldPoints).map(Zone::new).collect(QuestUtil.collectToArrayList());
		this.checkInZone = checkInZone;
	}

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		if (player != null && zones != null)
		{
			int worldViewId = client.getLocalPlayer().getWorldView().getId();
			boolean isOnBoat = worldViewId != -1;
			LocalPoint localLocation;
			if (isOnBoat)
			{
				WorldEntity we = client.getTopLevelWorldView().worldEntities().byIndex(worldViewId);
				localLocation = we.getLocalLocation();
			}
			else
			{
				localLocation = player.getLocalLocation();
			}
			final WorldPoint checkableLocation = WorldPoint.fromLocalInstance(client, localLocation);
			boolean inZone = zones.stream().anyMatch(z -> z.contains(checkableLocation));
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
