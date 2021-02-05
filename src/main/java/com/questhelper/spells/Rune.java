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
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;

public enum Rune
{
	AIR("Air Rune", ItemCollections.getAirRune(), ItemCollections.getAirStaff()),
	WATER("Water Rune", ItemCollections.getWaterRune(), ItemCollections.getWaterStaff()),
	EARTH("Earth Rune", ItemCollections.getEarthRune(), ItemCollections.getEarthStaff()),
	FIRE("Fire Rune", ItemCollections.getFireRune(), ItemCollections.getFireStaff()),
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
	LAVA("Lava Rune", ItemID.LAVA_RUNE, ItemCollections.getLavaStaff()),
	MUD("Mud Rune", ItemID.DUST_RUNE, ItemCollections.getDustStaff()),
	STEAM("Steam Rune", ItemID.STEAM_RUNE, ItemCollections.getSteamStaff()),
	SMOKE("Smoke Rune", ItemID.SMOKE_RUNE, ItemCollections.getSmokeStaff()),
	MIST("Mist Rune", ItemID.MIST_RUNE, ItemCollections.getMistStaff()),
	DUST("Dust Rune", ItemID.DUST_RUNE, ItemCollections.getDustStaff()),
	;

	@Getter
	private final String runeName;
	private final List<Integer> runes;
	private final List<Integer> staves;
	Rune(String runeName, List<Integer> runes, List<Integer> staves)
	{
		this.runeName = runeName;
		this.runes = runes;
		this.staves = staves;
	}

	Rune(String runeName, int itemID)
	{
		this.runeName = runeName;
		this.runes = new ArrayList<>(itemID);
		this.staves = null;
	}

	Rune(String runeName, int itemID, List<Integer> staves)
	{
		this.runeName = runeName;
		this.runes = new ArrayList<>(itemID);
		this.staves = staves;
	}

	public ItemRequirements getRunes(int quantity)
	{
		if (runes != null && staves != null)
		{
			return new ItemRequirements(
				LogicType.OR,
				getRuneName(),
				new ItemRequirement("Runes", runes, quantity),
				new ItemRequirement("Staff", staves, 1, true)
			);
		}

		if (runes != null)
		{
			return new ItemRequirements(
				LogicType.OR,
				getRuneName(),
				new ItemRequirement("Runes", runes, quantity)
			);
		}
		return null;
	}
}
