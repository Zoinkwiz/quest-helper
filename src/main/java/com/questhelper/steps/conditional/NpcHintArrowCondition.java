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
package com.questhelper.steps.conditional;

import com.questhelper.Zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

public class NpcHintArrowCondition extends ConditionForStep
{
	private final ArrayList<Integer> npcIDs;

	private final Zone zone;

	public NpcHintArrowCondition(int... npcIDs) {
		this.npcIDs = Arrays.stream(npcIDs).boxed().collect(Collectors.toCollection(ArrayList::new));
		this.zone = null;
	}

	public NpcHintArrowCondition(WorldPoint worldPoint, int... npcIDs) {
		this.npcIDs = Arrays.stream(npcIDs).boxed().collect(Collectors.toCollection(ArrayList::new));
		this.zone = new Zone(worldPoint, worldPoint);
	}

	public NpcHintArrowCondition(Zone zone, int... npcIDs) {
		this.npcIDs = Arrays.stream(npcIDs).boxed().collect(Collectors.toCollection(ArrayList::new));
		this.zone = zone;
	}

	public boolean checkCondition(Client client)
	{
		NPC currentNPC = client.getHintArrowNpc();
		if (currentNPC == null)
		{
			return false;
		}
		WorldPoint wp = WorldPoint.fromLocalInstance(client, currentNPC.getLocalLocation());

		if (zone != null && !zone.contains(wp))
		{
			return false;
		}

		if (npcIDs.contains(currentNPC.getId()))
		{
			return true;
		}

		return false;
	}
}
