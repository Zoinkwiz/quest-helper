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

/**
 * The tiles, rooms and guards which make up the run through the goutweed storeroom.
 * <p>
 * The route is broken into legs, each ending on a tile the player can stand on indefinitely without
 * being spotted. Every guard in the room is a separate NPC ID with its own fixed patrol, so the
 * handful of guards that can actually intercept a given leg are listed per leg below. Which leg the
 * player is on is decided purely by where they are standing; nothing here reads guard positions, so
 * the helper never tries to tell the player when to move.
 */
public final class StoreroomRoute
{
	/** For a leg with nothing to watch out for, such as standing still on the staging tile. */
	public static final int[] NO_GUARDS = {};

	/** Every guard patrolling the storeroom. */
	public static final int[] ALL_GUARDS = {
		NpcID.EADGAR_STOREROOM_GUARD,
		NpcID.TROLL_SGUARD1, NpcID.TROLL_SGUARD2, NpcID.TROLL_SGUARD3, NpcID.TROLL_SGUARD4,
		NpcID.TROLL_SGUARD5, NpcID.TROLL_SGUARD6, NpcID.TROLL_SGUARD7, NpcID.TROLL_SGUARD8,
	};

	/**
	 * Guards which can catch the run from the staging tile to the first safe spot.
	 * <p>
	 * Set to every guard in the room until the ones that actually matter for this leg have been
	 * confirmed in game. Narrowing it to that subset is a one-line edit here, and is what keeps the
	 * player watching two or three guards rather than all nine.
	 */
	public static final int[] LEG_1_GUARDS = ALL_GUARDS;

	/** Guards which can catch the run from the first safe spot to the second. See {@link #LEG_1_GUARDS}. */
	public static final int[] LEG_2_GUARDS = ALL_GUARDS;

	/** Guards which can catch the run from the second safe spot to the crates. See {@link #LEG_1_GUARDS}. */
	public static final int[] LEG_3_GUARDS = ALL_GUARDS;

	/** Where to gather after coming down the stairs, before starting the first leg. */
	public static final WorldPoint START_TILE = new WorldPoint(2861, 10092, 0);

	public static final WorldPoint SAFE_SPOT_1 = new WorldPoint(2857, 10084, 0);
	public static final WorldPoint SAFE_SPOT_2 = new WorldPoint(2859, 10084, 0);

	/** The tile to run to alongside the crates, rather than clicking the crates from range. */
	public static final WorldPoint CRATE_APPROACH_TILE = new WorldPoint(2858, 10074, 0);

	public static final Zone[] ROOM = {
		new Zone(new WorldPoint(2850, 10074, 0), new WorldPoint(2864, 10086, 0)),
		new Zone(new WorldPoint(2850, 10087, 0), new WorldPoint(2860, 10092, 0)),
	};

	public static final Zone[] CRATE_SIDE = {
		new Zone(new WorldPoint(2850, 10074, 0), new WorldPoint(2864, 10083, 0)),
		new Zone(SAFE_SPOT_2),
	};

	public static final Zone NORTH_ROOM = new Zone(new WorldPoint(2850, 10086, 0), new WorldPoint(2859, 10092, 0));

	private StoreroomRoute()
	{
	}
}
