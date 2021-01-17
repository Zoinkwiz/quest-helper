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
	// Tools

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
		ItemID.INFERNAL_AXE_OR,
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
		ItemID.DRAGON_PICKAXE_12797,
		ItemID.INFERNAL_PICKAXE,
		ItemID._3RD_AGE_PICKAXE,
		ItemID.CRYSTAL_PICKAXE,
		ItemID.TRAILBLAZER_PICKAXE,
		ItemID.INFERNAL_PICKAXE_OR)
	);

	@Getter
	private static final List<Integer> harpoons = new ArrayList<>(Arrays.asList(
		ItemID.HARPOON,
		ItemID.BARBTAIL_HARPOON,
		ItemID.DRAGON_HARPOON,
		ItemID.INFERNAL_HARPOON,
		ItemID.TRAILBLAZER_HARPOON,
		ItemID.INFERNAL_HARPOON_OR)
	);

	@Getter
	private static final List<Integer> machete = new ArrayList<>(Arrays.asList(
		ItemID.RED_TOPAZ_MACHETE,
		ItemID.JADE_MACHETE,
		ItemID.OPAL_MACHETE,
		ItemID.MACHETE
	));

	@Getter
	private static final List<Integer> nails = new ArrayList<>(Arrays.asList(
		ItemID.STEEL_NAILS,
		ItemID.IRON_NAILS,
		ItemID.BRONZE_NAILS,
		ItemID.BLACK_NAILS,
		ItemID.MITHRIL_NAILS,
		ItemID.ADAMANTITE_NAILS,
		ItemID.RUNE_NAILS
	));

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

	// Teleports

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

	// Potions

	@Getter
	private static final List<Integer> antipoisons = new ArrayList<>(Arrays.asList(
		ItemID.ANTIVENOM4_12913,
		ItemID.ANTIVENOM3_12915,
		ItemID.ANTIVENOM4_12913,
		ItemID.ANTIVENOM2_12917,
		ItemID.ANTIVENOM1_12919,
		ItemID.ANTIVENOM4,
		ItemID.ANTIVENOM3,
		ItemID.ANTIVENOM2,
		ItemID.ANTIVENOM1,
		ItemID.ANTIDOTE4_5952,
		ItemID.ANTIDOTE3_5954,
		ItemID.ANTIDOTE2_5956,
		ItemID.ANTIDOTE1_5958,
		ItemID.ANTIDOTE4,
		ItemID.ANTIDOTE3,
		ItemID.ANTIDOTE2,
		ItemID.ANTIDOTE1,
		ItemID.SUPERANTIPOISON4,
		ItemID.SUPERANTIPOISON3,
		ItemID.SUPERANTIPOISON2,
		ItemID.SUPERANTIPOISON1,
		ItemID.ANTIPOISON4,
		ItemID.ANTIPOISON3,
		ItemID.ANTIPOISON2,
		ItemID.ANTIPOISON1,
		ItemID.RELICYMS_BALM4,
		ItemID.RELICYMS_BALM3,
		ItemID.RELICYMS_BALM2,
		ItemID.RELICYMS_BALM1,
		ItemID.SANFEW_SERUM4,
		ItemID.SANFEW_SERUM3,
		ItemID.SANFEW_SERUM2,
		ItemID.SANFEW_SERUM1,
		ItemID.RELICYMS_MIX2,
		ItemID.RELICYMS_MIX1,
		ItemID.ANTIPOISON_MIX2,
		ItemID.ANTIPOISON_MIX1,
		ItemID.ANTIDOTE_MIX2,
		ItemID.ANTIDOTE_MIX1
	));

	@Getter
	private static final List<Integer> antivenoms = new ArrayList<>(Arrays.asList(
		ItemID.ANTIVENOM4_12913,
		ItemID.ANTIVENOM3_12915,
		ItemID.ANTIVENOM2_12917,
		ItemID.ANTIVENOM1_12919,
		ItemID.ANTIVENOM4,
		ItemID.ANTIVENOM3,
		ItemID.ANTIVENOM2,
		ItemID.ANTIVENOM1
	));

	@Getter
	private static final List<Integer> prayerPotions = new ArrayList<>(Arrays.asList(
		ItemID.PRAYER_POTION4,
		ItemID.PRAYER_POTION1,
		ItemID.PRAYER_POTION2,
		ItemID.PRAYER_POTION3
	));

	@Getter
	private static final List<Integer> restorePotions = new ArrayList<>(Arrays.asList(
		ItemID.SUPER_RESTORE4,
		ItemID.RESTORE_POTION4,
		ItemID.RESTORE_POTION1,
		ItemID.RESTORE_POTION2,
		ItemID.RESTORE_POTION3,
		ItemID.SUPER_RESTORE1,
		ItemID.SUPER_RESTORE2,
		ItemID.SUPER_RESTORE3
	));

	@Getter
	private static final List<Integer> staminaPotions = new ArrayList<>(Arrays.asList(
		ItemID.STAMINA_POTION4,
		ItemID.STAMINA_POTION1,
		ItemID.STAMINA_POTION2,
		ItemID.STAMINA_POTION3,
		ItemID.STAMINA_MIX1,
		ItemID.STAMINA_MIX2
	));

	@Getter
	private static final List<Integer> agilityPotions = new ArrayList<>(Arrays.asList(
		ItemID.AGILITY_POTION4,
		ItemID.AGILITY_POTION3,
		ItemID.AGILITY_POTION2,
		ItemID.AGILITY_POTION1
	));

	@Getter
	private static final List<Integer> flowers = new ArrayList<>(Arrays.asList(
		ItemID.RED_FLOWERS,
		ItemID.YELLOW_FLOWERS,
		ItemID.PURPLE_FLOWERS,
		ItemID.ORANGE_FLOWERS,
		ItemID.MIXED_FLOWERS,
		ItemID.ASSORTED_FLOWERS,
		ItemID.BLACK_FLOWERS,
		ItemID.WHITE_FLOWERS
	));

	// Teleport items

	@Getter
	private static final List<Integer> gamesNecklaces = new ArrayList<>(Arrays.asList(
		ItemID.GAMES_NECKLACE8,
		ItemID.GAMES_NECKLACE7,
		ItemID.GAMES_NECKLACE6,
		ItemID.GAMES_NECKLACE5,
		ItemID.GAMES_NECKLACE4,
		ItemID.GAMES_NECKLACE3,
		ItemID.GAMES_NECKLACE2,
		ItemID.GAMES_NECKLACE1
	));

	@Getter
	private static final List<Integer> ringOfDuelings = new ArrayList<>(Arrays.asList(
		ItemID.RING_OF_DUELING8,
		ItemID.RING_OF_DUELING7,
		ItemID.RING_OF_DUELING6,
		ItemID.RING_OF_DUELING5,
		ItemID.RING_OF_DUELING4,
		ItemID.RING_OF_DUELING3,
		ItemID.RING_OF_DUELING2,
		ItemID.RING_OF_DUELING1
	));

	@Getter
	private static final List<Integer> burningAmulets = new ArrayList<>(Arrays.asList(
		ItemID.BURNING_AMULET5,
		ItemID.BURNING_AMULET4,
		ItemID.BURNING_AMULET3,
		ItemID.BURNING_AMULET2,
		ItemID.BURNING_AMULET1
	));

	@Getter
	private static final List<Integer> necklaceOfPassages = new ArrayList<>(Arrays.asList(
		ItemID.NECKLACE_OF_PASSAGE5,
		ItemID.NECKLACE_OF_PASSAGE4,
		ItemID.NECKLACE_OF_PASSAGE3,
		ItemID.NECKLACE_OF_PASSAGE2,
		ItemID.NECKLACE_OF_PASSAGE1
	));

	@Getter
	private static final List<Integer> skillsNecklaces = new ArrayList<>(Arrays.asList(
		ItemID.SKILLS_NECKLACE5,
		ItemID.SKILLS_NECKLACE4,
		ItemID.SKILLS_NECKLACE3,
		ItemID.SKILLS_NECKLACE2,
		ItemID.SKILLS_NECKLACE1
	));

	@Getter
	private static final List<Integer> ringOfWealths = new ArrayList<>(Arrays.asList(
		ItemID.RING_OF_WEALTH_5,
		ItemID.RING_OF_WEALTH_I5,
		ItemID.RING_OF_WEALTH_4,
		ItemID.RING_OF_WEALTH_I4,
		ItemID.RING_OF_WEALTH_3,
		ItemID.RING_OF_WEALTH_I3,
		ItemID.RING_OF_WEALTH_2,
		ItemID.RING_OF_WEALTH_I2,
		ItemID.RING_OF_WEALTH_1,
		ItemID.RING_OF_WEALTH_I1
	));

	@Getter
	private static final List<Integer> combatBracelets = new ArrayList<>(Arrays.asList(
		ItemID.COMBAT_BRACELET6,
		ItemID.COMBAT_BRACELET5,
		ItemID.COMBAT_BRACELET4,
		ItemID.COMBAT_BRACELET3,
		ItemID.COMBAT_BRACELET2,
		ItemID.COMBAT_BRACELET1
	));

	@Getter
	private static final List<Integer> amuletOfGlories = new ArrayList<>(Arrays.asList(
		ItemID.AMULET_OF_ETERNAL_GLORY,
		ItemID.AMULET_OF_GLORY6,
		ItemID.AMULET_OF_GLORY_T6,
		ItemID.AMULET_OF_GLORY5,
		ItemID.AMULET_OF_GLORY_T5,
		ItemID.AMULET_OF_GLORY4,
		ItemID.AMULET_OF_GLORY_T4,
		ItemID.AMULET_OF_GLORY3,
		ItemID.AMULET_OF_GLORY_T3,
		ItemID.AMULET_OF_GLORY2,
		ItemID.AMULET_OF_GLORY_T2,
		ItemID.AMULET_OF_GLORY1,
		ItemID.AMULET_OF_GLORY_T1
	));

	@Getter
	private static final List<Integer> digsitePendants = new ArrayList<>(Arrays.asList(
		ItemID.DIGSITE_PENDANT_5,
		ItemID.DIGSITE_PENDANT_4,
		ItemID.DIGSITE_PENDANT_3,
		ItemID.DIGSITE_PENDANT_2,
		ItemID.DIGSITE_PENDANT_1
	));

	@Getter
	private static final List<Integer> slayerRings = new ArrayList<>(Arrays.asList(
		ItemID.SLAYER_RING_ETERNAL,
		ItemID.SLAYER_RING_8,
		ItemID.SLAYER_RING_7,
		ItemID.SLAYER_RING_6,
		ItemID.SLAYER_RING_5,
		ItemID.SLAYER_RING_4,
		ItemID.SLAYER_RING_3,
		ItemID.SLAYER_RING_2,
		ItemID.SLAYER_RING_1
	));

	// Logs

	@Getter
	private static final List<Integer> logsForFire = new ArrayList<>(Arrays.asList(
		ItemID.LOGS,
		ItemID.OAK_LOGS,
		ItemID.WILLOW_LOGS,
		ItemID.TEAK_LOGS,
		ItemID.REDWOOD_LOGS,
		ItemID.MAPLE_LOGS,
		ItemID.MAHOGANY_LOGS,
		ItemID.YEW_LOGS,
		ItemID.MAGIC_LOGS,
		ItemID.BLISTERWOOD_LOGS,
		ItemID.ARCTIC_PINE_LOGS,
		ItemID.ACHEY_TREE_LOGS,
		ItemID.REDWOOD_PYRE_LOGS,
		ItemID.MAGIC_PYRE_LOGS,
		ItemID.YEW_PYRE_LOGS,
		ItemID.MAHOGANY_PYRE_LOGS,
		ItemID.MAPLE_PYRE_LOGS,
		ItemID.ARCTIC_PYRE_LOGS,
		ItemID.TEAK_PYRE_LOGS,
		ItemID.WILLOW_PYRE_LOGS,
		ItemID.OAK_PYRE_LOGS,
		ItemID.PYRE_LOGS,
		ItemID.GREEN_LOGS,
		ItemID.RED_LOGS,
		ItemID.PURPLE_LOGS,
		ItemID.WHITE_LOGS,
		ItemID.BLUE_LOGS
	));


	// Other

	@Getter
	private static final List<Integer> greegrees = new ArrayList<>(Arrays.asList(
		ItemID.KARAMJAN_MONKEY_GREEGREE,
		ItemID.GORILLA_GREEGREE,
		ItemID.ANCIENT_GORILLA_GREEGREE,
		ItemID.BEARDED_GORILLA_GREEGREE,
		ItemID.KRUK_MONKEY_GREEGREE,
		ItemID.NINJA_MONKEY_GREEGREE,
		ItemID.ZOMBIE_MONKEY_GREEGREE,
		ItemID.ZOMBIE_MONKEY_GREEGREE_4030,
		ItemID.NINJA_MONKEY_GREEGREE_4025
	));

	@Getter
	private static final List<Integer> antifireShields = new ArrayList<>(Arrays.asList(
		ItemID.DRAGONFIRE_SHIELD,
		ItemID.DRAGONFIRE_SHIELD_11284,
		ItemID.DRAGONFIRE_WARD,
		ItemID.DRAGONFIRE_WARD_22003,
		ItemID.ANCIENT_WYVERN_SHIELD,
		ItemID.ANCIENT_WYVERN_SHIELD_21634,
		ItemID.ANTIDRAGON_SHIELD,
		ItemID.ANTIDRAGON_SHIELD_8282
	));

	@Getter
	private static final List<Integer> ghostspeak = new ArrayList<>(Arrays.asList(
		ItemID.GHOSTSPEAK_AMULET,
		ItemID.GHOSTSPEAK_AMULET_4250,
		ItemID.MORYTANIA_LEGS_1,
		ItemID.MORYTANIA_LEGS_2,
		ItemID.MORYTANIA_LEGS_3,
		ItemID.MORYTANIA_LEGS_4
	));

	@Getter
	private static final List<Integer> lightSources = new ArrayList<>(Arrays.asList(
		ItemID.FIREMAKING_CAPET,
		ItemID.FIREMAKING_CAPE,
		ItemID.BRUMA_TORCH,
		ItemID.KANDARIN_HEADGEAR_4,
		ItemID.KANDARIN_HEADGEAR_3,
		ItemID.KANDARIN_HEADGEAR_2,
		ItemID.KANDARIN_HEADGEAR_1,
		ItemID.BULLSEYE_LANTERN_4550,
		ItemID.SAPPHIRE_LANTERN_4702,
		ItemID.EMERALD_LANTERN_9065,
		ItemID.OIL_LANTERN_4539,
		ItemID.CANDLE_LANTERN_4531,
		ItemID.CANDLE_LANTERN_4534,
		ItemID.MINING_HELMET_5014,
		ItemID.OIL_LAMP_4524,
		ItemID.LIT_TORCH,
		ItemID.LIT_CANDLE,
		ItemID.LIT_BLACK_CANDLE
	));

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
}
