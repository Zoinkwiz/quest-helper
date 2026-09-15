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

import com.questhelper.requirements.zone.Zone;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;

/** The tiles, rooms and guards which make up the run through the goutweed storeroom. */
public final class StoreroomRoute
{
	/** Guards which can catch the run to the first safe spot. */
	public static final int[] LEG_1_GUARDS = { NpcID.TROLL_SGUARD1, NpcID.TROLL_SGUARD3 };

	/** Guards which can catch the run from the first safe spot to the second. */
	public static final int[] LEG_2_GUARDS = { NpcID.TROLL_SGUARD3, NpcID.TROLL_SGUARD5, NpcID.TROLL_SGUARD8 };

	/** Guards which can catch the run from the second safe spot to the crates. */
	public static final int[] LEG_3_GUARDS = { NpcID.TROLL_SGUARD7 };

	public static final WorldPoint SAFE_SPOT_1 = new WorldPoint(2857, 10084, 0);
	public static final WorldPoint SAFE_SPOT_2 = new WorldPoint(2859, 10084, 0);

	/** The tile to run to alongside the crates, rather than clicking the crates from range. */
	public static final WorldPoint CRATE_APPROACH_TILE = new WorldPoint(2858, 10074, 0);

	public static final Zone[] CRATE_SIDE = {
		new Zone(new WorldPoint(2850, 10074, 0), new WorldPoint(2864, 10083, 0)),
		new Zone(SAFE_SPOT_2),
	};

	private StoreroomRoute()
	{
	}
}
