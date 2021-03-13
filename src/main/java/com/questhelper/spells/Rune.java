/*
 *
 *  * Copyright (c) 2021, Senmori
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
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.ItemID;

/**
 * Represents a rune that can be used to cast spells.
 */
@Getter
public enum Rune
{
	AIR("Air Rune", ItemCollections.getAirRune(), () -> Staff.AIR),
	WATER("Water Rune", ItemCollections.getWaterRune(), () -> Staff.WATER),
	EARTH("Earth Rune", ItemCollections.getEarthRune(), () -> Staff.EARTH),
	FIRE("Fire Rune", ItemCollections.getFireRune(), () -> Staff.FIRE),
	MIND("Mind Rune", ItemID.MIND_RUNE),
	BODY("Body Rune", ItemID.BODY_RUNE),
	COSMIC("Cosmic Rune", ItemID.COSMIC_RUNE),
	CHAOS("Chaos Rune", ItemID.CHAOS_RUNE),
	NATURE("Nature Rune", ItemID.NATURE_RUNE),
	LAW("Law Rune", ItemID.LAW_RUNE),
	DEATH("Death Rune", ItemID.DEATH_RUNE),
	ASTRAL("Astral Rune", ItemID.ASTRAL_RUNE),
	BLOOD("Blood Rune", ItemID.BLOOD_RUNE),
	SOUL("Soul Rune", ItemID.SOUL_RUNE),
	WRATH("Wrath Rune", ItemID.WRATH_RUNE),
	// Keep combination runes after the non-combination runes
	LAVA("Lava Rune", ItemID.LAVA_RUNE, () -> Staff.LAVA),
	MUD("Mud Rune", ItemID.MUD_RUNE, () -> Staff.MUD),
	STEAM("Steam Rune", ItemID.STEAM_RUNE, () -> Staff.STEAM),
	SMOKE("Smoke Rune", ItemID.SMOKE_RUNE, () -> Staff.SMOKE),
	MIST("Mist Rune", ItemID.MIST_RUNE, () -> Staff.MIST),
	DUST("Dust Rune", ItemID.DUST_RUNE, () -> Staff.DUST),
	UNKNOWN("Null Rune", -1),
	;

	@Nonnull
	private final String runeName;
	@Nonnull
	private final List<Integer> runes;
	private final Supplier<Staff> staffSupplier;
	Rune(@Nonnull String runeName, @Nonnull List<Integer> runes, Supplier<Staff> staffSupplier)
	{
		this.runeName = runeName;
		this.runes = runes;
		this.staffSupplier = staffSupplier;
	}

	Rune(@Nonnull String runeName, int itemID)
	{
		this.runeName = runeName;
		this.runes = Collections.singletonList(itemID);
		this.staffSupplier = () -> Staff.UNKNOWN;
	}

	Rune(@Nonnull String runeName, int itemID, Supplier<Staff> staffSupplier)
	{
		this.runeName = runeName;
		this.runes = Collections.singletonList(itemID);
		this.staffSupplier = staffSupplier;
	}

	public Staff getStaff()
	{
		return staffSupplier.get();
	}

	public int getItemID()
	{
		return runes.get(0);
	}

	public static boolean isRuneItem(int itemID)
	{
		return getByItemID(itemID) != Rune.UNKNOWN;
	}

	public static Rune getByItemID(int itemID)
	{
		return Stream.of(values())
			.sorted(Collections.reverseOrder())
			.filter(r -> r.getRunes().contains(itemID))
			.findFirst()
			.orElse(Rune.UNKNOWN);
	}
}
