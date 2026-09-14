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
 */package com.questhelper.helpers.quests.eadgarsruse;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.zone.Zone;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;

public final class StoreroomRoute
{
	public static final int[] GUARD_IDS = {
		NpcID.EADGAR_STOREROOM_GUARD,
		NpcID.TROLL_SGUARD1, NpcID.TROLL_SGUARD2, NpcID.TROLL_SGUARD3, NpcID.TROLL_SGUARD4,
		NpcID.TROLL_SGUARD5, NpcID.TROLL_SGUARD6, NpcID.TROLL_SGUARD7, NpcID.TROLL_SGUARD8,
	};

	public static final WorldPoint START_TILE = new WorldPoint(2861, 10092, 0);
	public static final WorldPoint SAFE_SPOT_1 = new WorldPoint(2857, 10084, 0);
	public static final WorldPoint SAFE_SPOT_2 = new WorldPoint(2859, 10084, 0);
	public static final WorldPoint CRATE_APPROACH_TILE = new WorldPoint(2858, 10074, 0);

	public static final Zone[] ROOM = {
		new Zone(new WorldPoint(2850, 10074, 0), new WorldPoint(2864, 10086, 0)),
		new Zone(new WorldPoint(2850, 10087, 0), new WorldPoint(2860, 10092, 0)),
	};

	public static final Zone CRATE_SIDE = new Zone(new WorldPoint(2850, 10074, 0), new WorldPoint(2864, 10082, 0));

	public static final Zone[] LEG_1_LANES = {
		new Zone(new WorldPoint(2859, 10086, 0), new WorldPoint(2859, 10089, 0)),
		new Zone(new WorldPoint(2855, 10086, 0), new WorldPoint(2859, 10086, 0)),
		new Zone(new WorldPoint(2850, 10092, 0), new WorldPoint(2859, 10092, 0)),
		new Zone(new WorldPoint(2855, 10086, 0), new WorldPoint(2855, 10088, 0)),
	};

	public static final Zone[] LEG_2_LANES = {
		new Zone(new WorldPoint(2855, 10086, 0), new WorldPoint(2855, 10089, 0)),
		new Zone(new WorldPoint(2861, 10085, 0), new WorldPoint(2864, 10085, 0)),
		new Zone(new WorldPoint(2860, 10077, 0), new WorldPoint(2864, 10080, 0)),
	};

	public static final Zone[] LEG_3_LANES = {
		new Zone(new WorldPoint(2855, 10076, 0), new WorldPoint(2855, 10079, 0)),
		new Zone(new WorldPoint(2856, 10076, 0), new WorldPoint(2858, 10076, 0)),
		new Zone(new WorldPoint(2858, 10077, 0), new WorldPoint(2858, 10081, 0)),
	};

	private StoreroomRoute()
	{
	}

	public static Requirement clearOf(Zone... lanes)
	{
		Requirement[] checks = new Requirement[lanes.length];
		for (int i = 0; i < lanes.length; i++)
		{
			checks[i] = new GuardInZoneRequirement(lanes[i], true, GUARD_IDS);
		}
		return new Conditions(LogicType.AND, checks);
	}
}
