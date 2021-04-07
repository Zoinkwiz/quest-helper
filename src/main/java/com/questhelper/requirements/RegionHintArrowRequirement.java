/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.requirements;

import com.questhelper.Zone;
import com.questhelper.steps.tools.QuestPerspective;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

public class RegionHintArrowRequirement extends SimpleRequirement
{
	private final Zone zone;

	public RegionHintArrowRequirement(WorldPoint worldPoint)
	{
		this.zone = new Zone(worldPoint, worldPoint);
	}

	public RegionHintArrowRequirement(Zone zone)
	{
		this.zone = zone;
	}

	public boolean check(Client client)
	{
		WorldPoint hintArrowPoint = client.getHintArrowPoint();
		if (hintArrowPoint == null)
		{
			return false;
		}

		WorldPoint wp = QuestPerspective.getInstanceWorldPoint(client, hintArrowPoint);
		if (wp == null)
		{
			return false;
		}

		return zone.contains(wp);
	}
}
