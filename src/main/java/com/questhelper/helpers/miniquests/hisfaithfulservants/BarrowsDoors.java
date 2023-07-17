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

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum BarrowsDoors
{
	// Door route
	// Most doors have 2 WPs for lines. Need to specify which way things connect.
	NW_NORTH_DOOR(0, 2, 469,
		Arrays.asList(new WorldPoint(3534, 9716, 0), new WorldPoint(3535, 9720, 0),
			new WorldPoint(3538, 9721, 0), new WorldPoint(3567, 9721, 0), new WorldPoint(3569, 9718, 0),
			new WorldPoint(3569, 9715, 0))),
	NW_EAST_DOOR(0, 1, 472, Arrays.asList(new WorldPoint(3537, 9712, 0), new WorldPoint(3549, 9712, 0))),
	NW_SOUTH_DOOR(0, 3, 471, Arrays.asList(new WorldPoint(3534, 9708, 0), new WorldPoint(3534, 9698, 0))),
	NW_WEST_DOOR(0, 6, 470, Arrays.asList(new WorldPoint(3531, 9711, 0), new WorldPoint(3526, 9711, 0),
		new WorldPoint(3525, 9708, 0), new WorldPoint(3525, 9680, 0), new WorldPoint(3528, 9677, 0),
		new WorldPoint(3532, 9677, 0))),
	NORTH_SOUTH_DOOR(1, 4, 473, Arrays.asList(new WorldPoint(3552, 9708, 0), new WorldPoint(3552, 9696, 0))),
	NE_WEST_DOOR(1, 2, 474, Arrays.asList(new WorldPoint(3554, 9711, 0), new WorldPoint(3568, 9711, 0))),
	NE_SOUTH_DOOR(2, 5, 475, Arrays.asList(new WorldPoint(3568, 9710, 0), new WorldPoint(3568, 9696, 0))),
	NE_EAST_DOOR(2, 8, 476, Arrays.asList(new WorldPoint(3572, 9711, 0), new WorldPoint(3576, 9711, 0),
		new WorldPoint(3578, 9708, 0), new WorldPoint(3578, 9680, 0), new WorldPoint(3576, 9678, 0),
		new WorldPoint(3571, 9678, 0))),
	WEST_EAST_DOOR(3, 4, 477, Arrays.asList(new WorldPoint(3536, 9694, 0), new WorldPoint(3551, 9694, 0))),
	EAST_WEST_DOOR(4, 5, 478, Arrays.asList(new WorldPoint(3552, 9694, 0), new WorldPoint(3567, 9694, 0))),
	SW_NORTH_DOOR(6, 3, 479, Arrays.asList(new WorldPoint(3534, 9679, 0), new WorldPoint(3534, 9693, 0))),
	SW_EAST_DOOR(6, 7, 482, Arrays.asList(new WorldPoint(3536, 9677, 0), new WorldPoint(3549, 9677, 0))),
	SW_SOUTH_DOOR(6, 8, 484, Arrays.asList(new WorldPoint(3534, 9675, 0), new WorldPoint(3534, 9670, 0),
		new WorldPoint(3536, 9668, 0), new WorldPoint(3567, 9668, 0), new WorldPoint(3569, 9671, 0),
		new WorldPoint(3569, 9675, 0))),
	SOUTH_NORTH_DOOR(7, 4, 480, Arrays.asList(new WorldPoint(3551, 9679, 0), new WorldPoint(3551, 9694, 0))),
	SE_WEST_DOOR(8, 7, 483, Arrays.asList(new WorldPoint(3567, 9677, 0), new WorldPoint(3554, 9677, 0))),
	SE_NORTH_DOOR(8, 5, 481, Arrays.asList(new WorldPoint(3568, 9679, 0), new WorldPoint(3568, 9692, 0)));

	private final int startRoom;
	private final int endRoom;

	private final int varbit;
	private final List<WorldPoint> path;
}
