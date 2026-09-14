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

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.zone.Zone;
import net.runelite.api.coords.WorldPoint;

/**
 * Route data for the Troll Stronghold goutweed storeroom.
 *
 * <p>The guards walk fixed, scripted loops, so "is it safe to start running?" is just a question
 * about where they are standing right now. Each leg of the route names the stretches of floor
 * ("lanes") that must be free of guards before the player sets off; when they all are, the
 * destination tile turns green.</p>
 *
 * <h2>Filling this in</h2>
 * <ol>
 *     <li>Turn on RuneLite's developer tools. The NPC overlay gives you guard IDs, and the tile
 *     indicators give you world coordinates.</li>
 *     <li>Set {@link #GUARD_ID}. If the guards share one ID that is all you need — the lane checks
 *     ask "is <i>any</i> guard standing here", which is equivalent to naming individual guards as
 *     long as only one guard can ever reach a given lane.</li>
 *     <li>Set the three destination tiles.</li>
 *     <li>For each leg, add the lanes that have to be clear. <b>Size these to cover the run
 *     itself, not just the instant you click.</b> A lane should extend far enough back along the
 *     guard's approach that a guard entering it during your run is still caught by the check —
 *     otherwise the tile goes green just as a guard is about to walk into you.</li>
 * </ol>
 *
 * <p>Until every field is filled in, {@link #isConfigured()} returns false and Eadgar's Ruse falls
 * back to the plain "search the crates and avoid the guards" step, exactly as it behaved before.</p>
 */
public final class StoreroomRoute
{
	private static final int UNSET_ID = -1;
	private static final WorldPoint UNSET_TILE = new WorldPoint(0, 0, 0);

	private StoreroomRoute()
	{
	}

	// ------------------------------------------------------------------
	// 1. The guards
	// ------------------------------------------------------------------

	/** TODO: the patrolling guards' NPC ID. */
	public static final int GUARD_ID = UNSET_ID;

	// ------------------------------------------------------------------
	// 2. The route: two safe spots, then the crates
	// ------------------------------------------------------------------

	/** TODO: first safe spot, reached from the storeroom entrance. */
	public static final WorldPoint SAFE_SPOT_1 = UNSET_TILE;

	/** TODO: second safe spot. */
	public static final WorldPoint SAFE_SPOT_2 = UNSET_TILE;

	/**
	 * TODO: the tile to stand on to search the crates.
	 *
	 * <p>Run to this tile rather than clicking the crate directly — clicking the crate lets the
	 * client path you into a guard.</p>
	 */
	public static final WorldPoint CRATE_APPROACH_TILE = UNSET_TILE;

	// ------------------------------------------------------------------
	// 3. The lanes that must be clear before each leg
	// ------------------------------------------------------------------

	/** TODO: lanes that must be clear before leaving the entrance for {@link #SAFE_SPOT_1}. */
	public static final Zone[] LEG_1_LANES = {};

	/** TODO: lanes that must be clear before leaving {@link #SAFE_SPOT_1} for {@link #SAFE_SPOT_2}. */
	public static final Zone[] LEG_2_LANES = {};

	/** TODO: lanes that must be clear before leaving {@link #SAFE_SPOT_2} for the crates. */
	public static final Zone[] LEG_3_LANES = {};

	// ------------------------------------------------------------------

	/**
	 * @return a requirement that passes while no guard stands in any of {@code lanes}
	 */
	public static Requirement clearOf(Zone... lanes)
	{
		Requirement[] checks = new Requirement[lanes.length];
		for (int i = 0; i < lanes.length; i++)
		{
			checks[i] = new GuardInZoneRequirement(GUARD_ID, lanes[i], true);
		}
		return new Conditions(LogicType.AND, checks);
	}

	/**
	 * @return true once the route above has actually been filled in. While this is false the quest
	 * helper keeps its original single-step behaviour, so a half-finished route can never show the
	 * player a permanently green tile.
	 */
	public static boolean isConfigured()
	{
		return GUARD_ID != UNSET_ID
			&& !UNSET_TILE.equals(SAFE_SPOT_1)
			&& !UNSET_TILE.equals(SAFE_SPOT_2)
			&& !UNSET_TILE.equals(CRATE_APPROACH_TILE)
			&& LEG_1_LANES.length > 0
			&& LEG_2_LANES.length > 0
			&& LEG_3_LANES.length > 0;
	}
}
