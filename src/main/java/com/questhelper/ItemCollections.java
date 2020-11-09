/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;

public class ItemCollections
{
	@Getter
	private static final List<Integer> axes = new ArrayList<>(Arrays.asList(
		ItemID.BRONZE_AXE,
		ItemID.IRON_AXE,
		ItemID.STEEL_AXE,
		ItemID.BLACK_AXE,
		ItemID.MITHRIL_AXE,
		ItemID.ADAMANT_AXE,
		ItemID.GILDED_AXE,
		ItemID.RUNE_AXE,
		ItemID.DRAGON_AXE,
		ItemID.INFERNAL_AXE,
		ItemID._3RD_AGE_AXE,
		ItemID.CRYSTAL_AXE)
	);

	@Getter
	private static final List<Integer> pickaxes = new ArrayList<>(Arrays.asList(
			ItemID.BRONZE_PICKAXE,
			ItemID.IRON_PICKAXE,
			ItemID.STEEL_PICKAXE,
			ItemID.BLACK_PICKAXE,
			ItemID.MITHRIL_PICKAXE,
			ItemID.ADAMANT_PICKAXE,
			ItemID.GILDED_PICKAXE,
			ItemID.RUNE_PICKAXE,
			ItemID.DRAGON_PICKAXE,
			ItemID.DRAGON_PICKAXE_OR,
			ItemID.INFERNAL_PICKAXE,
			ItemID._3RD_AGE_PICKAXE,
			ItemID.CRYSTAL_PICKAXE)
	);

	@Getter
	private static final List<Integer> cats = new ArrayList<>(Arrays.asList(
		ItemID.PET_KITTEN,
		ItemID.PET_KITTEN_1556,
		ItemID.PET_KITTEN_1557,
		ItemID.PET_KITTEN_1558,
		ItemID.PET_KITTEN_1559,
		ItemID.PET_KITTEN_1560,
		ItemID.PET_CAT,
		ItemID.PET_CAT_1562,
		ItemID.PET_CAT_1563,
		ItemID.PET_CAT_1564,
		ItemID.PET_CAT_1565,
		ItemID.PET_CAT_1566,
		ItemID.PET_CAT_1567,
		ItemID.PET_CAT_1568,
		ItemID.PET_CAT_1569,
		ItemID.PET_CAT_1570,
		ItemID.PET_CAT_1571,
		ItemID.PET_CAT_1572,
		ItemID.LAZY_CAT,
		ItemID.LAZY_CAT_6550,
		ItemID.LAZY_CAT_6551,
		ItemID.LAZY_CAT_6552,
		ItemID.LAZY_CAT_6553,
		ItemID.LAZY_CAT_6554,
		ItemID.WILY_CAT,
		ItemID.WILY_CAT_6556,
		ItemID.WILY_CAT_6557,
		ItemID.WILY_CAT_6558,
		ItemID.WILY_CAT_6559,
		ItemID.WILY_CAT_6560,
		ItemID.HELLKITTEN,
		ItemID.HELL_CAT,
		ItemID.OVERGROWN_HELLCAT,
		ItemID.LAZY_HELL_CAT,
		ItemID.WILY_HELLCAT)
	);

	@Getter
    private static final List<Integer> bows = new ArrayList<>(Arrays.asList(
            ItemID.SHORTBOW,
            ItemID.LONGBOW,
            ItemID.MAGIC_SHORTBOW,
            ItemID.MAGIC_LONGBOW,
            ItemID.MAPLE_SHORTBOW,
            ItemID.MAPLE_LONGBOW,
            ItemID.OAK_SHORTBOW,
            ItemID.OAK_LONGBOW,
            ItemID.WILLOW_SHORTBOW,
            ItemID.WILLOW_LONGBOW,
            ItemID.YEW_SHORTBOW,
            ItemID.YEW_LONGBOW
    ));

	@Getter
    private static final List<Integer> metalArrows = new ArrayList<>(Arrays.asList(
            ItemID.BRONZE_ARROW,
            ItemID.IRON_ARROW,
            ItemID.STEEL_ARROW,
            ItemID.MITHRIL_ARROW,
            ItemID.ADAMANT_ARROW,
            ItemID.RUNE_ARROW
    ));

	@Getter
	private static final List<Integer> airRune = new ArrayList<>(Arrays.asList(
		ItemID.AIR_RUNE,
		ItemID.MIST_RUNE,
		ItemID.SMOKE_RUNE,
		ItemID.DUST_RUNE
	));

	@Getter
	private static final List<Integer> airStaff = new ArrayList<>(Arrays.asList(
		ItemID.AIR_BATTLESTAFF,
		ItemID.MYSTIC_AIR_STAFF,
		ItemID.STAFF_OF_AIR,
		ItemID.SMOKE_BATTLESTAFF,
		ItemID.MYSTIC_SMOKE_STAFF,
		ItemID.DUST_BATTLESTAFF,
		ItemID.MYSTIC_DUST_STAFF,
		ItemID.MIST_BATTLESTAFF,
		ItemID.MYSTIC_MIST_STAFF
	));

	@Getter
	private static final List<Integer> fireRune = new ArrayList<>(Arrays.asList(
		ItemID.FIRE_RUNE,
		ItemID.LAVA_RUNE,
		ItemID.SMOKE_RUNE,
		ItemID.STEAM_RUNE
	));

	@Getter
	private static final List<Integer> fireStaff = new ArrayList<>(Arrays.asList(
		ItemID.FIRE_BATTLESTAFF,
		ItemID.MYSTIC_FIRE_STAFF,
		ItemID.STAFF_OF_FIRE,
		ItemID.SMOKE_BATTLESTAFF,
		ItemID.MYSTIC_SMOKE_STAFF,
		ItemID.LAVA_BATTLESTAFF,
		ItemID.MYSTIC_LAVA_STAFF,
		ItemID.STEAM_BATTLESTAFF,
		ItemID.MYSTIC_STEAM_STAFF
	));

	@Getter
	private static final List<Integer> waterRune = new ArrayList<>(Arrays.asList(
		ItemID.WATER_RUNE,
		ItemID.MUD_RUNE,
		ItemID.MIST_RUNE,
		ItemID.STEAM_RUNE
	));

	@Getter
	private static final List<Integer> waterStaff = new ArrayList<>(Arrays.asList(
		ItemID.FIRE_BATTLESTAFF,
		ItemID.MYSTIC_FIRE_STAFF,
		ItemID.STAFF_OF_FIRE,
		ItemID.MUD_BATTLESTAFF,
		ItemID.MYSTIC_MUD_STAFF,
		ItemID.MIST_BATTLESTAFF,
		ItemID.MYSTIC_MIST_STAFF,
		ItemID.STEAM_BATTLESTAFF,
		ItemID.MYSTIC_STEAM_STAFF
	));

	@Getter
	private static final List<Integer> earthRune = new ArrayList<>(Arrays.asList(
		ItemID.EARTH_RUNE,
		ItemID.MUD_RUNE,
		ItemID.LAVA_RUNE,
		ItemID.DUST_RUNE
	));

	@Getter
	private static final List<Integer> earthStaff = new ArrayList<>(Arrays.asList(
		ItemID.EARTH_BATTLESTAFF,
		ItemID.MYSTIC_EARTH_STAFF,
		ItemID.STAFF_OF_EARTH,
		ItemID.MUD_BATTLESTAFF,
		ItemID.MYSTIC_MUD_STAFF,
		ItemID.DUST_BATTLESTAFF,
		ItemID.MYSTIC_DUST_STAFF,
		ItemID.LAVA_BATTLESTAFF,
		ItemID.MYSTIC_LAVA_STAFF
	));

	@Getter
	private static final List<Integer> antipoisons = new ArrayList<>(Arrays.asList(
		ItemID.ANTIPOISON1,
		ItemID.ANTIPOISON2,
		ItemID.ANTIPOISON3,
		ItemID.ANTIPOISON4,
		ItemID.ANTIDOTE1,
		ItemID.ANTIDOTE2,
		ItemID.ANTIDOTE3,
		ItemID.ANTIDOTE4,
		ItemID.ANTIDOTE1_5958,
		ItemID.ANTIDOTE2_5956,
		ItemID.ANTIDOTE3_5954,
		ItemID.ANTIDOTE4_5952,
		ItemID.ANTIPOISON_MIX1,
		ItemID.ANTIPOISON_MIX2,
		ItemID.ANTIDOTE_MIX1,
		ItemID.ANTIDOTE_MIX2,
		ItemID.SUPERANTIPOISON1,
		ItemID.SUPERANTIPOISON2,
		ItemID.SUPERANTIPOISON3,
		ItemID.SUPERANTIPOISON4,
		ItemID.SUPERANTIPOISON1,
		ItemID.RELICYMS_BALM1,
		ItemID.RELICYMS_BALM2,
		ItemID.RELICYMS_BALM3,
		ItemID.RELICYMS_BALM4,
		ItemID.RELICYMS_MIX1,
		ItemID.RELICYMS_MIX2,
		ItemID.SANFEW_SERUM1,
		ItemID.SANFEW_SERUM2,
		ItemID.SANFEW_SERUM3,
		ItemID.SANFEW_SERUM4,
		ItemID.ANTIVENOM1,
		ItemID.ANTIVENOM2,
		ItemID.ANTIVENOM3,
		ItemID.ANTIVENOM4,
		ItemID.ANTIVENOM1_12919,
		ItemID.ANTIVENOM2_12917,
		ItemID.ANTIVENOM3_12915,
		ItemID.ANTIVENOM4_12913
	));

	@Getter
	private static final List<Integer> gamesNecklaces = new ArrayList<>(Arrays.asList(
			ItemID.GAMES_NECKLACE1,
			ItemID.GAMES_NECKLACE2,
			ItemID.GAMES_NECKLACE3,
			ItemID.GAMES_NECKLACE4,
			ItemID.GAMES_NECKLACE5,
			ItemID.GAMES_NECKLACE6,
			ItemID.GAMES_NECKLACE7,
			ItemID.GAMES_NECKLACE8
	));

	@Getter
	private static final List<Integer> ringOfDuelings = new ArrayList<>(Arrays.asList(
			ItemID.RING_OF_DUELING1,
			ItemID.RING_OF_DUELING2,
			ItemID.RING_OF_DUELING3,
			ItemID.RING_OF_DUELING4,
			ItemID.RING_OF_DUELING5,
			ItemID.RING_OF_DUELING6,
			ItemID.RING_OF_DUELING7,
			ItemID.RING_OF_DUELING8
	));

	@Getter
	private static final List<Integer> burningAmulets = new ArrayList<>(Arrays.asList(
			ItemID.BURNING_AMULET1,
			ItemID.BURNING_AMULET2,
			ItemID.BURNING_AMULET3,
			ItemID.BURNING_AMULET4,
			ItemID.BURNING_AMULET5
	));

	@Getter
	private static final List<Integer> necklaceOfPassages = new ArrayList<>(Arrays.asList(
			ItemID.NECKLACE_OF_PASSAGE1,
			ItemID.NECKLACE_OF_PASSAGE2,
			ItemID.NECKLACE_OF_PASSAGE3,
			ItemID.NECKLACE_OF_PASSAGE4,
			ItemID.NECKLACE_OF_PASSAGE5
	));

	@Getter
	private static final List<Integer> skillsNecklaces = new ArrayList<>(Arrays.asList(
			ItemID.SKILLS_NECKLACE1,
			ItemID.SKILLS_NECKLACE2,
			ItemID.SKILLS_NECKLACE3,
			ItemID.SKILLS_NECKLACE4,
			ItemID.SKILLS_NECKLACE5
	));

	@Getter
	private static final List<Integer> ringOfWealths = new ArrayList<>(Arrays.asList(
			ItemID.RING_OF_WEALTH_1,
			ItemID.RING_OF_WEALTH_2,
			ItemID.RING_OF_WEALTH_3,
			ItemID.RING_OF_WEALTH_4,
			ItemID.RING_OF_WEALTH_5,
			ItemID.RING_OF_WEALTH_I1,
			ItemID.RING_OF_WEALTH_I2,
			ItemID.RING_OF_WEALTH_I3,
			ItemID.RING_OF_WEALTH_I4,
			ItemID.RING_OF_WEALTH_I5
	));

	@Getter
	private static final List<Integer> combatBracelets = new ArrayList<>(Arrays.asList(
			ItemID.COMBAT_BRACELET1,
			ItemID.COMBAT_BRACELET2,
			ItemID.COMBAT_BRACELET3,
			ItemID.COMBAT_BRACELET4,
			ItemID.COMBAT_BRACELET5,
			ItemID.COMBAT_BRACELET6
	));

	@Getter
	private static final List<Integer> amuletOfGlories = new ArrayList<>(Arrays.asList(
			ItemID.AMULET_OF_ETERNAL_GLORY,
			ItemID.AMULET_OF_GLORY1,
			ItemID.AMULET_OF_GLORY2,
			ItemID.AMULET_OF_GLORY3,
			ItemID.AMULET_OF_GLORY4,
			ItemID.AMULET_OF_GLORY5,
			ItemID.AMULET_OF_GLORY6,
			ItemID.AMULET_OF_GLORY_T1,
			ItemID.AMULET_OF_GLORY_T2,
			ItemID.AMULET_OF_GLORY_T3,
			ItemID.AMULET_OF_GLORY_T4,
			ItemID.AMULET_OF_GLORY_T5,
			ItemID.AMULET_OF_GLORY_T6
	));

	@Getter
	private static final List<Integer> digsitePendants = new ArrayList<>(Arrays.asList(
			ItemID.DIGSITE_PENDANT_1,
			ItemID.DIGSITE_PENDANT_2,
			ItemID.DIGSITE_PENDANT_3,
			ItemID.DIGSITE_PENDANT_4,
			ItemID.DIGSITE_PENDANT_5
	));
	
}
