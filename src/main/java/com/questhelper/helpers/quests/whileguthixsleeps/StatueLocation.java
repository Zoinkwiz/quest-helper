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

import java.util.List;

public enum StatueLocation
{
	NW(10868, new WorldPoint(4041, 4454, 0), "north-west path.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4068, 4437, 0),
		new WorldPoint(4049, 4437, 0),
		new WorldPoint(4045, 4450, 0)
	)),
	W(10869, new WorldPoint(4037, 4419, 0), "west path.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4063, 4431, 0),
		new WorldPoint(4056, 4426, 0),
		new WorldPoint(4050, 4428, 0),
		new WorldPoint(4039, 4420, 0)
	)),
	SW_N(10913, new WorldPoint(4040, 4375, 0), "south-west path, up the north branch.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4060, 4419, 0),
		new WorldPoint(4057, 4411, 0),
		new WorldPoint(4048, 4410, 0),
		new WorldPoint(4046, 4392, 0),
		new WorldPoint(4040, 4387, 0),
		new WorldPoint(4040, 4377, 0)
	)),
	SW_S(10924, new WorldPoint(4091, 4374, 0), "south-west path, down the south branch.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4060, 4419, 0),
		new WorldPoint(4057, 4411, 0),
		new WorldPoint(4063, 4399, 0),
		new WorldPoint(4056, 4376, 0),
		new WorldPoint(4089, 4372, 0)
	)),
	NE(10925, new WorldPoint(4101, 4468, 0), "north-east path.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4083, 4437, 0),
		new WorldPoint(4086, 4451, 0),
		new WorldPoint(4100, 4458, 0),
		new WorldPoint(4111, 4450, 0),
		new WorldPoint(4117, 4456, 0),
		new WorldPoint(4116, 4467, 0),
		new WorldPoint(4103, 4468, 0)
	)),
	E(10926, new WorldPoint(4142, 4469, 0), "east path.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4095, 4442, 0),
		new WorldPoint(4108, 4439, 0),
		new WorldPoint(4128, 4446, 0),
		new WorldPoint(4130, 4464, 0),
		new WorldPoint(4140, 4467, 0)
	)),
	SE_N(10927, new WorldPoint(4153, 4450, 0), "south-east path, up the north branch.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4102, 4430, 0),
		new WorldPoint(4107, 4428, 0),
		new WorldPoint(4112, 4428, 0),
		new WorldPoint(4117, 4431, 0),
		new WorldPoint(4133, 4433, 0),
		new WorldPoint(4138, 4435, 0),
		new WorldPoint(4139, 4451, 0),
		new WorldPoint(4150, 4451, 0)
	)),
	SE_S(10928, new WorldPoint(4151, 4421, 0), "south-east path, down the south branch.", List.of(
		new WorldPoint(4076, 4431, 0),
		new WorldPoint(4102, 4430, 0),
		new WorldPoint(4107, 4428, 0),
		new WorldPoint(4112, 4428, 0),
		new WorldPoint(4115, 4420, 0),
		new WorldPoint(4140, 4421, 0),
		new WorldPoint(4146, 4430, 0),
		new WorldPoint(4152, 4430, 0),
		new WorldPoint(4153, 4424, 0)
	));

	@Getter
	private final int varbitID;
	private final WorldPoint location;
	private final String directionText;

	@Getter
	private final List<WorldPoint> path;

	StatueLocation(int varbitID, WorldPoint location, String directionText, List<WorldPoint> path)
	{
		this.varbitID = varbitID;
		this.location = location;
		this.directionText = directionText;
		this.path = path;
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
