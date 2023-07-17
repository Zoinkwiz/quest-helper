/*
 * Copyright (c) 2023, Zoinkwiz (https://www.github.com/Zoinkwiz)
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
package com.questhelper.helpers.miniquests.hisfaithfulservants;

import com.questhelper.Zone;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum BarrowsRooms
{
	NW(0, Arrays.asList(
		new Zone(new WorldPoint(3517, 9706, 0), new WorldPoint(3540, 9723, 0)),
		new Zone(new WorldPoint(3534, 9718, 0), new WorldPoint(3551, 9722, 0)),
		new Zone(new WorldPoint(3524, 9695, 0), new WorldPoint(3528, 9712, 0))
	),
		new BarrowsDoors[] { BarrowsDoors.NW_EAST_DOOR, BarrowsDoors.NW_SOUTH_DOOR, BarrowsDoors.NW_NORTH_DOOR, BarrowsDoors.NW_WEST_DOOR }),
	N(1, Collections.singletonList(new Zone(new WorldPoint(3541, 9706, 0), new WorldPoint(3562, 9716, 0))),
		new BarrowsDoors[] { BarrowsDoors.NORTH_SOUTH_DOOR, BarrowsDoors.NW_EAST_DOOR, BarrowsDoors.NE_WEST_DOOR }),
	NE(2, Arrays.asList(
		new Zone(new WorldPoint(3563, 9706, 0), new WorldPoint(3580, 9717, 0)),
		new Zone(new WorldPoint(3552, 9718, 0), new WorldPoint(3569, 9722, 0)),
		new Zone(new WorldPoint(3575, 9695, 0), new WorldPoint(3579, 9712, 0))
	),
		new BarrowsDoors[] { BarrowsDoors.NE_WEST_DOOR, BarrowsDoors.NE_SOUTH_DOOR, BarrowsDoors.NW_NORTH_DOOR, BarrowsDoors.NW_EAST_DOOR }),
	W(3, Collections.singletonList(new Zone(new WorldPoint(3530, 9684, 0), new WorldPoint(3540, 9705, 0))),
		new BarrowsDoors[] { BarrowsDoors.WEST_EAST_DOOR, BarrowsDoors.NW_SOUTH_DOOR, BarrowsDoors.SW_NORTH_DOOR }),
	C(4, Collections.singletonList(new Zone(new WorldPoint(3541, 9684, 0), new WorldPoint(3562, 9705, 0))),
		new BarrowsDoors[] { null }),
	E(5, Collections.singletonList(new Zone(new WorldPoint(3563, 9684, 0), new WorldPoint(3573, 9705, 0))),
		new BarrowsDoors[] { BarrowsDoors.EAST_WEST_DOOR, BarrowsDoors.NE_SOUTH_DOOR, BarrowsDoors.SE_NORTH_DOOR}),
	SW(6, Arrays.asList(
		new Zone(new WorldPoint(3525, 9671, 0), new WorldPoint(3540, 9683, 0)),
		new Zone(new WorldPoint(3534, 9667, 0), new WorldPoint(3551, 9668, 0)),
		new Zone(new WorldPoint(3524, 9677, 0), new WorldPoint(3528, 9694, 0))
	),
		new BarrowsDoors[] { BarrowsDoors.SW_NORTH_DOOR, BarrowsDoors.SW_EAST_DOOR, BarrowsDoors.NW_WEST_DOOR, BarrowsDoors.SW_SOUTH_DOOR }),
	S(7, Collections.singletonList(new Zone(new WorldPoint(3541, 9672, 0), new WorldPoint(3562, 9683, 0))),
		new BarrowsDoors[] { BarrowsDoors.SOUTH_NORTH_DOOR, BarrowsDoors.SW_EAST_DOOR, BarrowsDoors.SE_WEST_DOOR }),
	SE(8, Arrays.asList(
		new Zone(new WorldPoint(3563, 9667, 0), new WorldPoint(3580, 9683, 0)),
		new Zone(new WorldPoint(3575, 9677, 0), new WorldPoint(3579, 9694, 0)),
		new Zone(new WorldPoint(3551, 9667, 0), new WorldPoint(3570, 9671, 0))
	),
		new BarrowsDoors[] { BarrowsDoors.SE_WEST_DOOR, BarrowsDoors.SE_NORTH_DOOR, BarrowsDoors.SW_SOUTH_DOOR, BarrowsDoors.NE_EAST_DOOR });

	private final int id;
	private final List<Zone> area;
	private final BarrowsDoors[] paths;

	public static BarrowsRooms getRoomById(int id)
	{
		for (BarrowsRooms room : BarrowsRooms.values())
		{
			if (room.getId() == id) return room;
		}
		return BarrowsRooms.C;
	}
}
