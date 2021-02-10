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

import com.questhelper.ItemCollections;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Staff
{
	AIR("Air Staff", ItemCollections.getAirStaff(), Rune.AIR),
	WATER("Water Staff", ItemCollections.getWaterStaff(), Rune.WATER),
	EARTH("Earth Staff", ItemCollections.getEarthStaff(), Rune.EARTH),
	FIRE("Fire Staff", ItemCollections.getFireStaff(), Rune.FIRE),
	// Keep combination staves after the elemental staves
	LAVA("Lava Staff", ItemCollections.getLavaStaff(), Rune.LAVA, Rune.FIRE, Rune.EARTH),
	MUD("Mud Staff", ItemCollections.getMudStaff(), Rune.MUD, Rune.WATER, Rune.EARTH),
	STEAM("Steam Staff", ItemCollections.getSteamStaff(), Rune.STEAM, Rune.WATER, Rune.FIRE),
	SMOKE("Smoke Staff", ItemCollections.getSmokeStaff(), Rune.SMOKE, Rune.AIR, Rune.FIRE),
	MIST("Mist Staff", ItemCollections.getMistStaff(), Rune.MIST, Rune.WATER, Rune.AIR),
	DUST("Dust Staff", ItemCollections.getDustStaff(), Rune.DUST, Rune.EARTH, Rune.AIR),
	UNKNOWN("Null Staff", Collections.emptyList(), Rune.UNKNOWN),
	;

	private final String name;
	private final List<Integer> staves;
	private final List<Rune> sourceRunes;
	Staff(String name, List<Integer> staves, Rune... sourceOf)
	{
		this.name = name;
		this.staves = staves;
		this.sourceRunes = Arrays.asList(sourceOf);
	}

	public int getItemID()
	{
		return staves.get(0);
	}

	public boolean isSourceOf(Rune rune)
	{
		List<Staff> values = Stream.of(values())
			.sorted(Comparator.reverseOrder())
			.collect(Collectors.toList());
		return values.stream().anyMatch(staff -> staff.getSourceRunes().contains(rune));
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
