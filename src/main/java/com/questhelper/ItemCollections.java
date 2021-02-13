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

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;

/**
 * These are useful item collections.
 * These lists are ORDERED.
 * Order items from highest tier to lowest tier,
 * or highest dose to lowest dose, etc.
 * As long as the list is consistent in how it is ordered.
 */
public class ItemCollections
{
	// Tools

	@Getter
	private static final List<Integer> axes = ImmutableList.of(
		ItemID.CRYSTAL_AXE,
		ItemID._3RD_AGE_AXE,
		ItemID.TRAILBLAZER_AXE,
		ItemID.INFERNAL_AXE_OR,
		ItemID.INFERNAL_AXE,
		ItemID.DRAGON_AXE,
		ItemID.RUNE_AXE,
		ItemID.GILDED_AXE,
		ItemID.ADAMANT_AXE,
		ItemID.MITHRIL_AXE,
		ItemID.BLACK_AXE,
		ItemID.STEEL_AXE,
		ItemID.IRON_AXE,
		ItemID.BRONZE_AXE
	);

	@Getter
	private static final List<Integer> pickaxes = ImmutableList.of(
		ItemID.INFERNAL_PICKAXE_OR,
		ItemID.TRAILBLAZER_PICKAXE,
		ItemID.CRYSTAL_PICKAXE,
		ItemID._3RD_AGE_PICKAXE,
		ItemID.INFERNAL_PICKAXE,
		ItemID.DRAGON_PICKAXE_12797,
		ItemID.DRAGON_PICKAXE_OR,
		ItemID.DRAGON_PICKAXE,
		ItemID.RUNE_PICKAXE,
		ItemID.GILDED_PICKAXE,
		ItemID.ADAMANT_PICKAXE,
		ItemID.MITHRIL_PICKAXE,
		ItemID.BLACK_PICKAXE,
		ItemID.STEEL_PICKAXE,
		ItemID.IRON_PICKAXE,
		ItemID.BRONZE_PICKAXE
	);

	@Getter
	private static final List<Integer> harpoons = ImmutableList.of(
		ItemID.INFERNAL_HARPOON_OR,
		ItemID.TRAILBLAZER_HARPOON,
		ItemID.INFERNAL_HARPOON,
		ItemID.DRAGON_HARPOON,
		ItemID.BARBTAIL_HARPOON,
		ItemID.HARPOON
	);

	@Getter
	private static final List<Integer> machete = ImmutableList.of(
		ItemID.RED_TOPAZ_MACHETE,
		ItemID.JADE_MACHETE,
		ItemID.OPAL_MACHETE,
		ItemID.MACHETE
	);

	@Getter
	private static final List<Integer> nails = ImmutableList.of(
		ItemID.STEEL_NAILS,
		ItemID.IRON_NAILS,
		ItemID.BRONZE_NAILS,
		ItemID.BLACK_NAILS,
		ItemID.MITHRIL_NAILS,
		ItemID.ADAMANTITE_NAILS,
		ItemID.RUNE_NAILS
	);

	@Getter
	private static final List<Integer> bows = ImmutableList.of(
		ItemID.MAGIC_SHORTBOW,
		ItemID.MAGIC_SHORTBOW_I,
		ItemID.MAGIC_LONGBOW,
		ItemID.YEW_SHORTBOW,
		ItemID.YEW_LONGBOW,
		ItemID.MAPLE_SHORTBOW,
		ItemID.MAPLE_LONGBOW,
		ItemID.WILLOW_SHORTBOW,
		ItemID.WILLOW_LONGBOW,
		ItemID.OAK_SHORTBOW,
		ItemID.OAK_LONGBOW,
		ItemID.SHORTBOW,
		ItemID.LONGBOW
	);

	@Getter
	private static final List<Integer> swords = ImmutableList.of(
		ItemID.BRONZE_SWORD,
		ItemID.BRONZE_LONGSWORD,
		ItemID.IRON_SWORD,
		ItemID.IRON_LONGSWORD,
		ItemID.STEEL_SWORD,
		ItemID.STEEL_LONGSWORD,
		ItemID.BLACK_SWORD,
		ItemID.BLACK_LONGSWORD,
		ItemID.WHITE_SWORD,
		ItemID.WHITE_LONGSWORD,
		ItemID.MITHRIL_SWORD,
		ItemID.MITHRIL_LONGSWORD,
		ItemID.ADAMANT_SWORD,
		ItemID.ADAMANT_LONGSWORD,
		ItemID.WILDERNESS_SWORD_1,
		ItemID.WILDERNESS_SWORD_2,
		ItemID.WILDERNESS_SWORD_3,
		ItemID.WILDERNESS_SWORD_4
	);

	// Teleports

	@Getter
	private static final List<Integer> metalArrows = ImmutableList.of(
		ItemID.RUNE_ARROW,
		ItemID.ADAMANT_ARROW,
		ItemID.MITHRIL_ARROW,
		ItemID.STEEL_ARROW,
		ItemID.IRON_ARROW,
		ItemID.BRONZE_ARROW
	);

	@Getter
	private static final List<Integer> arrows = ImmutableList.of(
		ItemID.DRAGON_ARROW,
		ItemID.AMETHYST_ARROW,
		ItemID.RUNE_ARROW,
		ItemID.ADAMANT_ARROW,
		ItemID.MITHRIL_ARROW,
		ItemID.STEEL_ARROW,
		ItemID.IRON_ARROW,
		ItemID.BRONZE_ARROW
	);

	@Getter
	private static final List<Integer> brutalArrows = ImmutableList.of(
		ItemID.RUNE_BRUTAL,
		ItemID.ADAMANT_BRUTAL,
		ItemID.MITHRIL_BRUTAL,
		ItemID.BLACK_BRUTAL,
		ItemID.STEEL_BRUTAL,
		ItemID.IRON_BRUTAL,
		ItemID.BRONZE_BRUTAL
	);

	@Getter
	private static final List<Integer> fireArrows = ImmutableList.of(
		ItemID.DRAGON_FIRE_ARROW,
		ItemID.DRAGON_FIRE_ARROW_LIT,
		ItemID.AMETHYST_FIRE_ARROW,
		ItemID.AMETHYST_FIRE_ARROW_LIT,
		ItemID.RUNE_FIRE_ARROW,
		ItemID.RUNE_FIRE_ARROW_LIT,
		ItemID.ADAMANT_FIRE_ARROW,
		ItemID.ADAMANT_FIRE_ARROW_LIT,
		ItemID.MITHRIL_FIRE_ARROW,
		ItemID.MITHRIL_FIRE_ARROW_LIT,
		ItemID.STEEL_FIRE_ARROW,
		ItemID.STEEL_FIRE_ARROW_LIT,
		ItemID.IRON_FIRE_ARROW,
		ItemID.IRON_FIRE_ARROW_LIT,
		ItemID.BRONZE_FIRE_ARROW,
		ItemID.BRONZE_FIRE_ARROW_LIT
	);

	@Getter
	private static final List<Integer> specialArrows = ImmutableList.of(
		ItemID.BROAD_ARROWS,
		ItemID.OGRE_ARROW,
		ItemID.TRAINING_ARROWS,
		ItemID.ICE_ARROWS
	);

	@Getter
	private static final List<Integer> arrowtips = ImmutableList.of(
		ItemID.DRAGON_ARROWTIPS,
		ItemID.AMETHYST_ARROWTIPS,
		ItemID.RUNE_ARROWTIPS,
		ItemID.ADAMANT_ARROWTIPS,
		ItemID.MITHRIL_ARROWTIPS,
		ItemID.STEEL_ARROWTIPS,
		ItemID.IRON_ARROWTIPS,
		ItemID.BRONZE_ARROWTIPS
	);

	@Getter
	private static final List<Integer> airRune = ImmutableList.of(
		ItemID.AIR_RUNE,
		ItemID.MIST_RUNE,
		ItemID.SMOKE_RUNE,
		ItemID.DUST_RUNE
	);

	@Getter
	private static final List<Integer> airStaff = ImmutableList.of(
		ItemID.AIR_BATTLESTAFF,
		ItemID.MYSTIC_AIR_STAFF,
		ItemID.STAFF_OF_AIR,
		ItemID.SMOKE_BATTLESTAFF,
		ItemID.MYSTIC_SMOKE_STAFF,
		ItemID.DUST_BATTLESTAFF,
		ItemID.MYSTIC_DUST_STAFF,
		ItemID.MIST_BATTLESTAFF,
		ItemID.MYSTIC_MIST_STAFF
	);

	@Getter
	private static final List<Integer> fireRune = ImmutableList.of(
		ItemID.FIRE_RUNE,
		ItemID.LAVA_RUNE,
		ItemID.SMOKE_RUNE,
		ItemID.STEAM_RUNE
	);

	@Getter
	private static final List<Integer> fireStaff = ImmutableList.of(
		ItemID.FIRE_BATTLESTAFF,
		ItemID.MYSTIC_FIRE_STAFF,
		ItemID.STAFF_OF_FIRE,
		ItemID.SMOKE_BATTLESTAFF,
		ItemID.MYSTIC_SMOKE_STAFF,
		ItemID.LAVA_BATTLESTAFF,
		ItemID.MYSTIC_LAVA_STAFF,
		ItemID.STEAM_BATTLESTAFF,
		ItemID.MYSTIC_STEAM_STAFF
	);

	@Getter
	private static final List<Integer> waterRune = ImmutableList.of(
		ItemID.WATER_RUNE,
		ItemID.MUD_RUNE,
		ItemID.MIST_RUNE,
		ItemID.STEAM_RUNE
	);

	@Getter
	private static final List<Integer> waterStaff = ImmutableList.of(
		ItemID.FIRE_BATTLESTAFF,
		ItemID.MYSTIC_FIRE_STAFF,
		ItemID.STAFF_OF_FIRE,
		ItemID.MUD_BATTLESTAFF,
		ItemID.MYSTIC_MUD_STAFF,
		ItemID.MIST_BATTLESTAFF,
		ItemID.MYSTIC_MIST_STAFF,
		ItemID.STEAM_BATTLESTAFF,
		ItemID.MYSTIC_STEAM_STAFF
	);

	@Getter
	private static final List<Integer> earthRune = ImmutableList.of(
		ItemID.EARTH_RUNE,
		ItemID.MUD_RUNE,
		ItemID.LAVA_RUNE,
		ItemID.DUST_RUNE
	);

	@Getter
	private static final List<Integer> earthStaff = ImmutableList.of(
		ItemID.EARTH_BATTLESTAFF,
		ItemID.MYSTIC_EARTH_STAFF,
		ItemID.STAFF_OF_EARTH,
		ItemID.MUD_BATTLESTAFF,
		ItemID.MYSTIC_MUD_STAFF,
		ItemID.DUST_BATTLESTAFF,
		ItemID.MYSTIC_DUST_STAFF,
		ItemID.LAVA_BATTLESTAFF,
		ItemID.MYSTIC_LAVA_STAFF
	);

	// Potions

	@Getter
	private static final List<Integer> antipoisons = ImmutableList.of(
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
	);

	@Getter
	private static final List<Integer> antivenoms = ImmutableList.of(
		ItemID.ANTIVENOM4_12913,
		ItemID.ANTIVENOM3_12915,
		ItemID.ANTIVENOM2_12917,
		ItemID.ANTIVENOM1_12919,
		ItemID.ANTIVENOM4,
		ItemID.ANTIVENOM3,
		ItemID.ANTIVENOM2,
		ItemID.ANTIVENOM1
	);

	@Getter
	private static final List<Integer> prayerPotions = ImmutableList.of(
		ItemID.PRAYER_POTION4,
		ItemID.PRAYER_POTION3,
		ItemID.PRAYER_POTION2,
		ItemID.PRAYER_POTION1
	);

	@Getter
	private static final List<Integer> restorePotions = ImmutableList.of(
		ItemID.SUPER_RESTORE4,
		ItemID.SUPER_RESTORE3,
		ItemID.SUPER_RESTORE2,
		ItemID.SUPER_RESTORE1,
		ItemID.RESTORE_POTION4,
		ItemID.RESTORE_POTION3,
		ItemID.RESTORE_POTION2,
		ItemID.RESTORE_POTION1
	);

	@Getter
	private static final List<Integer> superRestorePotions = ImmutableList.of(
		ItemID.SUPER_RESTORE4,
		ItemID.SUPER_RESTORE3,
		ItemID.SUPER_RESTORE2,
		ItemID.SUPER_RESTORE1
	);

	@Getter
	private static final List<Integer> saradominBrews = ImmutableList.of(
		ItemID.SARADOMIN_BREW4,
		ItemID.SARADOMIN_BREW3,
		ItemID.SARADOMIN_BREW2,
		ItemID.SARADOMIN_BREW1
	);

	@Getter
	private static final List<Integer> runRestoreItems = ImmutableList.of(
		ItemID.AGILITY_CAPE,
		ItemID.AGILITY_CAPET,
		ItemID.EXPLORERS_RING_4,
		ItemID.EXPLORERS_RING_3,
		ItemID.EXPLORERS_RING_2,
		ItemID.EXPLORERS_RING_1,
		ItemID.STAMINA_POTION4,
		ItemID.STAMINA_POTION3,
		ItemID.STAMINA_POTION2,
		ItemID.STAMINA_POTION1,
		ItemID.MINT_CAKE,
		ItemID.STRANGE_FRUIT,
		ItemID.STAMINA_MIX2,
		ItemID.STAMINA_MIX1,
		ItemID.SUPER_ENERGY4,
		ItemID.SUPER_ENERGY3,
		ItemID.SUPER_ENERGY2,
		ItemID.SUPER_ENERGY1,
		ItemID.ENERGY_POTION4,
		ItemID.ENERGY_POTION3,
		ItemID.ENERGY_POTION2,
		ItemID.ENERGY_POTION1,
		ItemID.PURPLE_SWEETS,
		ItemID.WHITE_TREE_FRUIT,
		ItemID.SUMMER_PIE,
		ItemID.GUTHIX_REST4,
		ItemID.GUTHIX_REST3,
		ItemID.GUTHIX_REST2,
		ItemID.GUTHIX_REST1,
		ItemID.PAPAYA_FRUIT
	);

	@Getter
	private static final List<Integer> staminaPotions = ImmutableList.of(
		ItemID.STAMINA_POTION4,
		ItemID.STAMINA_POTION3,
		ItemID.STAMINA_POTION2,
		ItemID.STAMINA_POTION1,
		ItemID.STAMINA_MIX2,
		ItemID.STAMINA_MIX1
	);

	@Getter
	private static final List<Integer> agilityPotions = ImmutableList.of(
		ItemID.AGILITY_POTION4,
		ItemID.AGILITY_POTION3,
		ItemID.AGILITY_POTION2,
		ItemID.AGILITY_POTION1
	);

	// Food

	@Getter
	private static final List<Integer> goodEatingFood = ImmutableList.of(
		ItemID.DARK_CRAB,
		ItemID.TUNA_POTATO,
		ItemID.MANTA_RAY,
		ItemID.SEA_TURTLE,
		ItemID.PINEAPPLE_PIZZA,
		ItemID.SHARK,
		ItemID.MUSHROOM_POTATO,
		ItemID.UGTHANKI_KEBAB_1885,
		ItemID.CURRY,
		ItemID.COOKED_KARAMBWAN,
		ItemID.ANCHOVY_PIZZA,
		ItemID.ANGLERFISH,
		ItemID.MONKFISH,
		ItemID.POTATO_WITH_CHEESE,
		ItemID.MEAT_PIZZA,
		ItemID.POTATO_WITH_BUTTER,
		ItemID.SWORDFISH,
		ItemID.PLAIN_PIZZA,
		ItemID.BASS,
		ItemID.LOBSTER,
		ItemID.CHOCOLATE_CAKE,
		ItemID.CAKE,
		ItemID.STEW,
		ItemID.TUNA,
		ItemID.SALMON,
		ItemID.PIKE,
		ItemID.COD,
		ItemID.TROUT,
		ItemID.MACKEREL,
		ItemID.HERRING,
		ItemID.BREAD,
		ItemID.SARDINE,
		ItemID.COOKED_MEAT,
		ItemID.COOKED_CHICKEN,
		ItemID.SHRIMPS
	);


	@Getter
	private static final List<Integer> fishFood = ImmutableList.of(
		ItemID.DARK_CRAB,
		ItemID.MANTA_RAY,
		ItemID.ANGLERFISH,
		ItemID.SEA_TURTLE,
		ItemID.SHARK,
		ItemID.COOKED_KARAMBWAN,
		ItemID.MONKFISH,
		ItemID.COOKED_JUBBLY,
		ItemID.LAVA_EEL,
		ItemID.SWORDFISH,
		ItemID.BASS,
		ItemID.LOBSTER,
		ItemID.RAINBOW_FISH,
		ItemID.TUNA,
		ItemID.CAVE_EEL,
		ItemID.SALMON,
		ItemID.PIKE,
		ItemID.COD,
		ItemID.TROUT,
		ItemID.MACKEREL,
		ItemID.HERRING,
		ItemID.SARDINE,
		ItemID.SHRIMPS
	);

	@Getter
	private static final List<Integer> gnomeFood = ImmutableList.of(
		ItemID.TANGLED_TOADS_LEGS,
		ItemID.TANGLED_TOADS_LEGS_9551,
		ItemID.CHOCOLATE_BOMB,
		ItemID.CHOCOLATE_BOMB_9553,
		ItemID.WORM_HOLE,
		ItemID.WORM_HOLE_9547,
		ItemID.VEG_BALL,
		ItemID.VEG_BALL_9549,
		ItemID.FRUIT_BATTA,
		ItemID.FRUIT_BATTA_9527,
		ItemID.TOAD_BATTA,
		ItemID.TOAD_BATTA_9529,
		ItemID.WORM_BATTA,
		ItemID.WORM_BATTA_9531,
		ItemID.VEGETABLE_BATTA,
		ItemID.VEGETABLE_BATTA_9533,
		ItemID.CHEESETOM_BATTA,
		ItemID.CHEESETOM_BATTA_9535,
		ItemID.FRUIT_BLAST,
		ItemID.FRUIT_BLAST_9514,
		ItemID.PINEAPPLE_PUNCH,
		ItemID.PINEAPPLE_PUNCH_9512,
		ItemID.TOAD_CRUNCHIES,
		ItemID.TOAD_CRUNCHIES_9538,
		ItemID.WORM_CRUNCHIES,
		ItemID.WORM_CRUNCHIES_9542,
		ItemID.SPICY_CRUNCHIES,
		ItemID.SPICY_CRUNCHIES_9540,
		ItemID.CHOCCHIP_CRUNCHIES,
		ItemID.CHOCCHIP_CRUNCHIES_9544,
		ItemID.BLURBERRY_SPECIAL,
		ItemID.BLURBERRY_SPECIAL_9520,
		ItemID.WIZARD_BLIZZARD,
		ItemID.WIZARD_BLIZZARD_9487,
		ItemID.WIZARD_BLIZZARD_9489,
		ItemID.WIZARD_BLIZZARD_9508,
		ItemID.SHORT_GREEN_GUY,
		ItemID.SHORT_GREEN_GUY_9510,
		ItemID.DRUNK_DRAGON,
		ItemID.DRUNK_DRAGON_9516,
		ItemID.CHOC_SATURDAY,
		ItemID.CHOC_SATURDAY_9518
	);

	@Getter
	private static final List<Integer> stews = ImmutableList.of(
		ItemID.STEW,
		ItemID.CURRY
	);

	@Getter
	private static final List<Integer> pizzas = ImmutableList.of(
		ItemID.PINEAPPLE_PIZZA,
		ItemID.ANCHOVY_PIZZA,
		ItemID.MEAT_PIZZA,
		ItemID.PLAIN_PIZZA
	);


	@Getter
	private static final List<Integer> potatoFood = ImmutableList.of(
		ItemID.TUNA_POTATO,
		ItemID.MUSHROOM_POTATO,
		ItemID.EGG_POTATO,
		ItemID.POTATO_WITH_CHEESE,
		ItemID.CHILLI_POTATO,
		ItemID.POTATO_WITH_BUTTER,
		ItemID.BAKED_POTATO
	);

	@Getter
	private static final List<Integer> pies = ImmutableList.of(
		ItemID.SUMMER_PIE,
		ItemID.WILD_PIE,
		ItemID.DRAGONFRUIT_PIE,
		ItemID.ADMIRAL_PIE,
		ItemID.MUSHROOM_PIE,
		ItemID.BOTANICAL_PIE,
		ItemID.FISH_PIE,
		ItemID.GARDEN_PIE,
		ItemID.APPLE_PIE,
		ItemID.MUD_PIE,
		ItemID.MEAT_PIE,
		ItemID.REDBERRY_PIE
	);



	// Teleport items

	@Getter
	private static final List<Integer> gamesNecklaces = ImmutableList.of(
		ItemID.GAMES_NECKLACE8,
		ItemID.GAMES_NECKLACE7,
		ItemID.GAMES_NECKLACE6,
		ItemID.GAMES_NECKLACE5,
		ItemID.GAMES_NECKLACE4,
		ItemID.GAMES_NECKLACE3,
		ItemID.GAMES_NECKLACE2,
		ItemID.GAMES_NECKLACE1
	);

	@Getter
	private static final List<Integer> ringOfDuelings = ImmutableList.of(
		ItemID.RING_OF_DUELING8,
		ItemID.RING_OF_DUELING7,
		ItemID.RING_OF_DUELING6,
		ItemID.RING_OF_DUELING5,
		ItemID.RING_OF_DUELING4,
		ItemID.RING_OF_DUELING3,
		ItemID.RING_OF_DUELING2,
		ItemID.RING_OF_DUELING1
	);

	@Getter
	private static final List<Integer> burningAmulets = ImmutableList.of(
		ItemID.BURNING_AMULET5,
		ItemID.BURNING_AMULET4,
		ItemID.BURNING_AMULET3,
		ItemID.BURNING_AMULET2,
		ItemID.BURNING_AMULET1
	);

	@Getter
	private static final List<Integer> necklaceOfPassages = ImmutableList.of(
		ItemID.NECKLACE_OF_PASSAGE5,
		ItemID.NECKLACE_OF_PASSAGE4,
		ItemID.NECKLACE_OF_PASSAGE3,
		ItemID.NECKLACE_OF_PASSAGE2,
		ItemID.NECKLACE_OF_PASSAGE1
	);

	@Getter
	private static final List<Integer> skillsNecklaces = ImmutableList.of(
		ItemID.SKILLS_NECKLACE5,
		ItemID.SKILLS_NECKLACE4,
		ItemID.SKILLS_NECKLACE3,
		ItemID.SKILLS_NECKLACE2,
		ItemID.SKILLS_NECKLACE1
	);

	@Getter
	private static final List<Integer> ringOfWealths = ImmutableList.of(
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
	);

	@Getter
	private static final List<Integer> combatBracelets = ImmutableList.of(
		ItemID.COMBAT_BRACELET6,
		ItemID.COMBAT_BRACELET5,
		ItemID.COMBAT_BRACELET4,
		ItemID.COMBAT_BRACELET3,
		ItemID.COMBAT_BRACELET2,
		ItemID.COMBAT_BRACELET1
	);

	@Getter
	private static final List<Integer> amuletOfGlories = ImmutableList.of(
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
	);

	@Getter
	private static final List<Integer> digsitePendants = ImmutableList.of(
		ItemID.DIGSITE_PENDANT_5,
		ItemID.DIGSITE_PENDANT_4,
		ItemID.DIGSITE_PENDANT_3,
		ItemID.DIGSITE_PENDANT_2,
		ItemID.DIGSITE_PENDANT_1
	);

	@Getter
	private static final List<Integer> slayerRings = ImmutableList.of(
		ItemID.SLAYER_RING_ETERNAL,
		ItemID.SLAYER_RING_8,
		ItemID.SLAYER_RING_7,
		ItemID.SLAYER_RING_6,
		ItemID.SLAYER_RING_5,
		ItemID.SLAYER_RING_4,
		ItemID.SLAYER_RING_3,
		ItemID.SLAYER_RING_2,
		ItemID.SLAYER_RING_1
	);

	// Logs

	@Getter
	private static final List<Integer> logsForFire = ImmutableList.of(
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
	);


	// Other

	@Getter
	private static final List<Integer> greegrees = ImmutableList.of(
		ItemID.KARAMJAN_MONKEY_GREEGREE,
		ItemID.GORILLA_GREEGREE,
		ItemID.ANCIENT_GORILLA_GREEGREE,
		ItemID.BEARDED_GORILLA_GREEGREE,
		ItemID.KRUK_MONKEY_GREEGREE,
		ItemID.NINJA_MONKEY_GREEGREE,
		ItemID.ZOMBIE_MONKEY_GREEGREE,
		ItemID.ZOMBIE_MONKEY_GREEGREE_4030,
		ItemID.NINJA_MONKEY_GREEGREE_4025
	);

	@Getter
	private static final List<Integer> antifireShields = ImmutableList.of(
		ItemID.DRAGONFIRE_SHIELD,
		ItemID.DRAGONFIRE_SHIELD_11284,
		ItemID.DRAGONFIRE_WARD,
		ItemID.DRAGONFIRE_WARD_22003,
		ItemID.ANCIENT_WYVERN_SHIELD,
		ItemID.ANCIENT_WYVERN_SHIELD_21634,
		ItemID.ANTIDRAGON_SHIELD,
		ItemID.ANTIDRAGON_SHIELD_8282
	);

	@Getter
	private static final List<Integer> ghostspeak = ImmutableList.of(
		ItemID.GHOSTSPEAK_AMULET,
		ItemID.GHOSTSPEAK_AMULET_4250,
		ItemID.MORYTANIA_LEGS_2,
		ItemID.MORYTANIA_LEGS_3,
		ItemID.MORYTANIA_LEGS_4
	);

	@Getter
	private static final List<Integer> lightSources = ImmutableList.of(
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
	);

	@Getter
	private static final List<Integer> cats = ImmutableList.of(
		ItemID.WILY_HELLCAT,
		ItemID.WILY_CAT,
		ItemID.WILY_CAT_6556,
		ItemID.WILY_CAT_6557,
		ItemID.WILY_CAT_6558,
		ItemID.WILY_CAT_6559,
		ItemID.WILY_CAT_6560,

		ItemID.LAZY_HELL_CAT,
		ItemID.LAZY_CAT,
		ItemID.LAZY_CAT_6550,
		ItemID.LAZY_CAT_6551,
		ItemID.LAZY_CAT_6552,
		ItemID.LAZY_CAT_6553,
		ItemID.LAZY_CAT_6554,

		ItemID.HELL_CAT,
		ItemID.PET_CAT,
		ItemID.PET_CAT_1562,
		ItemID.PET_CAT_1563,
		ItemID.PET_CAT_1564,
		ItemID.PET_CAT_1565,
		ItemID.PET_CAT_1566,

		ItemID.HELLKITTEN,
		ItemID.PET_KITTEN,
		ItemID.PET_KITTEN_1556,
		ItemID.PET_KITTEN_1557,
		ItemID.PET_KITTEN_1558,
		ItemID.PET_KITTEN_1559,
		ItemID.PET_KITTEN_1560,
		// Overgrown cats
		ItemID.OVERGROWN_HELLCAT,
		ItemID.PET_CAT_1567,
		ItemID.PET_CAT_1568,
		ItemID.PET_CAT_1569,
		ItemID.PET_CAT_1570,
		ItemID.PET_CAT_1571,
		ItemID.PET_CAT_1572
	);

	@Getter
	private static final List<Integer> huntingCats = ImmutableList.of(
		ItemID.WILY_HELLCAT,
		ItemID.WILY_CAT,
		ItemID.WILY_CAT_6556,
		ItemID.WILY_CAT_6557,
		ItemID.WILY_CAT_6558,
		ItemID.WILY_CAT_6559,
		ItemID.WILY_CAT_6560,

		ItemID.LAZY_HELL_CAT,
		ItemID.LAZY_CAT,
		ItemID.LAZY_CAT_6550,
		ItemID.LAZY_CAT_6551,
		ItemID.LAZY_CAT_6552,
		ItemID.LAZY_CAT_6553,
		ItemID.LAZY_CAT_6554,

		ItemID.HELL_CAT,
		ItemID.PET_CAT,
		ItemID.PET_CAT_1562,
		ItemID.PET_CAT_1563,
		ItemID.PET_CAT_1564,
		ItemID.PET_CAT_1565,
		ItemID.PET_CAT_1566,

		ItemID.HELLKITTEN,
		ItemID.PET_KITTEN,
		ItemID.PET_KITTEN_1556,
		ItemID.PET_KITTEN_1557,
		ItemID.PET_KITTEN_1558,
		ItemID.PET_KITTEN_1559,
		ItemID.PET_KITTEN_1560
	);


	@Getter
	private static final List<Integer> flowers = ImmutableList.of(
		ItemID.RED_FLOWERS,
		ItemID.YELLOW_FLOWERS,
		ItemID.PURPLE_FLOWERS,
		ItemID.ORANGE_FLOWERS,
		ItemID.MIXED_FLOWERS,
		ItemID.ASSORTED_FLOWERS,
		ItemID.BLACK_FLOWERS,
		ItemID.WHITE_FLOWERS,
		ItemID.RED_FLOWERS_8938,
		ItemID.BLUE_FLOWERS_8936
	);

	@Getter
	private static final List<Integer> rodOfIvandis = ImmutableList.of(
		ItemID.ROD_OF_IVANDIS_10,
		ItemID.ROD_OF_IVANDIS_9,
		ItemID.ROD_OF_IVANDIS_8,
		ItemID.ROD_OF_IVANDIS_7,
		ItemID.ROD_OF_IVANDIS_6,
		ItemID.ROD_OF_IVANDIS_5,
		ItemID.ROD_OF_IVANDIS_4,
		ItemID.ROD_OF_IVANDIS_3,
		ItemID.ROD_OF_IVANDIS_2,
		ItemID.ROD_OF_IVANDIS_1
	);

	@Getter
	private static final List<Integer> salveAmulet = ImmutableList.of(
		ItemID.SALVE_AMULETEI,
		ItemID.SALVE_AMULET_E,
		ItemID.SALVE_AMULETI,
		ItemID.SALVE_AMULET,
		ItemID.SALVE_AMULETEI_25278,
		ItemID.SALVE_AMULETI_25250
	);

	@Getter
	private static final List<Integer> wateringCans = ImmutableList.of(
		ItemID.GRICOLLERS_CAN,
		ItemID.WATERING_CAN8,
		ItemID.WATERING_CAN7,
		ItemID.WATERING_CAN6,
		ItemID.WATERING_CAN5,
		ItemID.WATERING_CAN4,
		ItemID.WATERING_CAN3,
		ItemID.WATERING_CAN2,
		ItemID.WATERING_CAN1
	);

	@Getter
	private static final List<Integer> enchantedLyre = ImmutableList.of(
		ItemID.ENCHANTED_LYREI,
		ItemID.ENCHANTED_LYRE5,
		ItemID.ENCHANTED_LYRE4,
		ItemID.ENCHANTED_LYRE3,
		ItemID.ENCHANTED_LYRE2,
		ItemID.ENCHANTED_LYRE1
	);

	@Getter
	private static final List<Integer> slayerHelmets = ImmutableList.of(
		ItemID.SLAYER_HELMET,
		ItemID.SLAYER_HELMET_I,
		ItemID.SLAYER_HELMET_I_25177,
		ItemID.BLACK_SLAYER_HELMET,
		ItemID.BLACK_SLAYER_HELMET_I,
		ItemID.BLACK_SLAYER_HELMET_I_25179,
		ItemID.GREEN_SLAYER_HELMET,
		ItemID.GREEN_SLAYER_HELMET_I,
		ItemID.GREEN_SLAYER_HELMET_I_25181,
		ItemID.RED_SLAYER_HELMET,
		ItemID.RED_SLAYER_HELMET_I,
		ItemID.RED_SLAYER_HELMET_I_25183,
		ItemID.PURPLE_SLAYER_HELMET,
		ItemID.PURPLE_SLAYER_HELMET_I,
		ItemID.PURPLE_SLAYER_HELMET_I_25185,
		ItemID.TURQUOISE_SLAYER_HELMET,
		ItemID.TURQUOISE_SLAYER_HELMET_I,
		ItemID.TURQUOISE_SLAYER_HELMET_I_25187,
		ItemID.HYDRA_SLAYER_HELMET,
		ItemID.HYDRA_SLAYER_HELMET_I,
		ItemID.HYDRA_SLAYER_HELMET_I_25189,
		ItemID.TWISTED_SLAYER_HELMET,
		ItemID.TWISTED_SLAYER_HELMET_I,
		ItemID.TWISTED_SLAYER_HELMET_I_25191
	);
}
