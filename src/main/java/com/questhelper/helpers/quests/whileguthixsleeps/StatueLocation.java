/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.quests.whileguthixsleeps;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public enum StatueLocation
{
	NW(10868, new WorldPoint(4041, 4454, 0), "north-west path."),
	W(10869, new WorldPoint(4037, 4419, 0), "west path."),
	SW_N(10913, new WorldPoint(4040, 4375, 0), "south-west path, up the north branch."),
	SW_S(10924, new WorldPoint(4091, 4374, 0), "south-west path, down the south branch."),
	NE(10925, new WorldPoint(4101, 4468, 0), "north-east path."),
	E(10926, new WorldPoint(4142, 4469, 0), "east path."),
	SE_N(10927, new WorldPoint(4153, 4450, 0), "south-east path, up the north branch."),
	SE_S(10928, new WorldPoint(4151, 4421, 0), "south-east path, down the south branch.");

	@Getter
	private final int varbitID;
	private final WorldPoint location;
	private final String directionText;

	StatueLocation(int varbitID, WorldPoint location, String directionText)
	{
		this.varbitID = varbitID;
		this.location = location;
		this.directionText = directionText;
	}

	public WorldPoint getLocation()
	{
		return location;
	}

	public String getDirectionText()
	{
		return directionText;
	}
}
