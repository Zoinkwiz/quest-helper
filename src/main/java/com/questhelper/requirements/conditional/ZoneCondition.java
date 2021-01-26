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
import com.questhelper.questhelpers.QuestUtil;
import java.util.List;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

public class ZoneCondition extends ConditionForStep
{

	private final List<Zone> zones;
	private final boolean checkInZone;

	public ZoneCondition(Zone... zone)
	{
		this.zones = QuestUtil.toArrayList(zone);
		this.checkInZone = true;
	}

	public ZoneCondition(WorldPoint... worldPoints)
	{
		this.zones = Stream.of(worldPoints).map(Zone::new).collect(QuestUtil.collectToArrayList());
		this.checkInZone = true;
	}

	public ZoneCondition(boolean checkInZone, Zone... zone)
	{
		this.zones = QuestUtil.toArrayList(zone);
		this.checkInZone = checkInZone;
	}

	public ZoneCondition(boolean checkInZone, WorldPoint... worldPoints)
	{
		this.zones = Stream.of(worldPoints).map(Zone::new).collect(QuestUtil.collectToArrayList());
		this.checkInZone = checkInZone;
	}

	@Override
	public boolean check(Client client)
	{
		Player player = client.getLocalPlayer();
		if (player != null)
		{
			WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

			boolean inZone = zones.stream().anyMatch(z -> z.contains(worldPoint));
			return inZone && checkInZone;
		}
		return !checkInZone;
	}
}
