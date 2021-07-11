/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.themagearenaii;

import java.awt.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum MageArenaSpawnLocation
{
	// The enemies can spawn at the same location
	// Every 250 * 10 ticks they move
	SOUTH_LAVA_DRAGON(new WorldPoint(3185, 3791, 0), "South of the Lava Dragon Isle."),
	NORTH_LAVA_DRAGON(new WorldPoint(3224, 3882, 0), "North of the Lava Dragon Isle."),
	NORTH_LAVA_DRAGON_2(new WorldPoint(3233, 3867, 0), "North of the Lava Dragon Isle."),
	NORTH_LAVA_DRAGON_3(new WorldPoint(3220, 3867, 0), "North of the Lava Dragon Isle."),
	NORTH_WEST_LAVA_DRAGON(new WorldPoint(3140, 3868, 0), "North west of the Lava Dragon Isle."),
	NORTH_WEST_LAVA_DRAGON_3(new WorldPoint(3169, 3865, 0), "North west of the Lava Dragon Isle."),
	NORTH_EAST_LAVA_DRAGON(new WorldPoint(3238, 3894, 0), "North east of the Lava Dragon Isle."),
	NORTH_EAST_LAVA_DRAGON_2(new WorldPoint(3247, 3859, 0), "North east of the Lava Dragon Isle."), // Incorrect?
	NORTH_EAST_LAVA_DRAGON_3(new WorldPoint(3262, 3887, 0), "North east of the Lava Dragon Isle."),
	NORTH_EAST_LAVA_DRAGON_4(new WorldPoint(3247, 3862, 0), "North east of the Lava Dragon Isle."),
	WEST_LAVA_DRAGON(new WorldPoint(3146, 3895, 0), "West of the Lava Dragon Isle."),
	WEST_LAVA_DRAGON_2(new WorldPoint(3163, 3833, 0), "West of the Lava Dragon Isle."),
	WEST_DEMONIC_RUINS(new WorldPoint(3314, 3876, 0), "West of the Demonic Ruins."),
	EAST_LAVA_DRAGON_ISLE(new WorldPoint(3246, 3834, 0), "East of the Lava Dragon Isle."),
	EAST_LAVA_DRAGON_ISLE_2(new WorldPoint(3279, 3823, 0), "East of the Lava Dragon Isle."),
	SOUTH_EAST_LAVA_DRAGON_ISLE(new WorldPoint(3266, 3814, 0), "South east of the Lava Dragon Isle."),
	EAST_ROGUE(new WorldPoint(3306, 3936, 0), "East of the Rogues' Castle."),
	// TODO: This one isn't a precise position
	EAST_ROGUE_2(new WorldPoint(3343, 3911, 0), "East of the Rogues' Castle."),
	WEST_ROGUE(new WorldPoint(3261, 3909, 0), "West of the Rogues' Castle.");

	private final WorldPoint worldPoint;
	private final String area;

	public Rectangle getRect()
	{
		final int spawnRadius = 1;
		return new Rectangle(worldPoint.getX() - spawnRadius, worldPoint.getY() - spawnRadius, spawnRadius * 2 + 1, spawnRadius * 2 + 1);
	}
}
