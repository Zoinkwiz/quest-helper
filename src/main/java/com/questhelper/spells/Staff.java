/*
 *
 *  * Copyright (c) 2021
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.spells;

import com.google.common.collect.ImmutableSet;
import com.questhelper.ItemCollections;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Staff
{
	AIR("Air Staff", ItemCollections.getAirStaff(), () -> ImmutableSet.of(Rune.AIR)),
	WATER("Water Staff", ItemCollections.getWaterStaff(), () -> ImmutableSet.of(Rune.WATER)),
	EARTH("Earth Staff", ItemCollections.getEarthStaff(), () -> ImmutableSet.of(Rune.EARTH)),
	FIRE("Fire Staff", ItemCollections.getFireStaff(), () -> ImmutableSet.of(Rune.FIRE)),
	// Keep combination staves after the elemental staves
	LAVA("Lava Staff", ItemCollections.getLavaStaff(), () -> ImmutableSet.of(Rune.LAVA, Rune.FIRE, Rune.EARTH)),
	MUD("Mud Staff", ItemCollections.getMudStaff(), () -> ImmutableSet.of(Rune.MUD, Rune.WATER, Rune.EARTH)),
	STEAM("Steam Staff", ItemCollections.getSteamStaff(), () -> ImmutableSet.of(Rune.STEAM, Rune.WATER, Rune.FIRE)),
	SMOKE("Smoke Staff", ItemCollections.getSmokeStaff(), () -> ImmutableSet.of(Rune.SMOKE, Rune.AIR, Rune.FIRE)),
	MIST("Mist Staff", ItemCollections.getMistStaff(), () -> ImmutableSet.of(Rune.MIST, Rune.WATER, Rune.AIR)),
	DUST("Dust Staff", ItemCollections.getDustStaff(), () -> ImmutableSet.of(Rune.DUST, Rune.EARTH, Rune.AIR)),
	UNKNOWN("Null Staff", Collections.emptyList(), () -> ImmutableSet.of(Rune.UNKNOWN)),
	;

	private final String name;
	private final List<Integer> staves;
	private final Supplier<Set<Rune>> sourceRunesSupplier;
	Staff(String name, List<Integer> staves, Supplier<Set<Rune>> sourceRunesSupplier)
	{
		this.name = name;
		this.staves = staves;
		this.sourceRunesSupplier = sourceRunesSupplier;
	}

	public int getItemID()
	{
		return staves.get(0);
	}

	public boolean isSourceOf(Rune rune)
	{
		return sourceRunesSupplier.get().contains(rune);
	}

	public static Staff getByItemID(int itemID)
	{
		return Stream.of(Staff.values())
			.sorted(Collections.reverseOrder())
			.filter(staff -> staff.getStaves().contains(itemID))
			.findFirst()
			.orElse(Staff.UNKNOWN);
	}

	public static boolean isStaff(int itemID)
	{
		return getByItemID(itemID) != Staff.UNKNOWN;
	}
}
