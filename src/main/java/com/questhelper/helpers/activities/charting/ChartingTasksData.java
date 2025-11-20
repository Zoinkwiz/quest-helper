/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.activities.charting;

import java.util.List;

import net.runelite.api.gameval.VarbitID;

import net.runelite.api.coords.WorldPoint;

public final class ChartingTasksData
{
	private ChartingTasksData() {}

	// All data generated from the OSRS Wiki (https://oldschool.runescape.wiki/w/Sea_charting)
	private static final List<ChartingSeaSection> SECTIONS = List.of(
		new ChartingSeaSection("Kharidian Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find a crashed glider south of the Karamja Shipyard.", new WorldPoint(2987, 3012, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_GLIDER_KHARIDIAN_SEA_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Pandemonium II west of the Pandemonium.", new WorldPoint(2973, 2996, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_PANDEMONIUM_2_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Pandemonium III west of the Kharidian Bandit Camp.", new WorldPoint(3143, 2981, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_PANDEMONIUM_3_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to admire Chartin' Charles McAtless in the cave on the Pandemonium.", new WorldPoint(3051, 9390, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_CHARTING_TUTOR_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Pandemonium from the cave entrance on the island.", new WorldPoint(3045, 2997, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_THE_PANDEMONIUM_COMPLETE)
		)),
		new ChartingSeaSection("Bay of Sarim", List.of(
			new ChartingTaskDefinition("Generic", "Find a treacherous rock formation near Draynor Village.", new WorldPoint(3079, 3233, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROCK_BAY_OF_SARIM_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Grandyozenaym north east of the Wizards' Tower.", new WorldPoint(3126, 3204, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROWBOAT_BAY_OF_SARIM_COMPLETE),
			new ChartingTaskDefinition("Generic", "Interact with the salvaging station in Port Sarim.", new WorldPoint(3029, 3209, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_SALVAGE_STATION_PORT_SARIM_COMPLETE),
			new ChartingTaskDefinition("Generic", "Look at the port task board in Port Sarim.", new WorldPoint(3030, 3200, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_BOARD_PORT_SARIM_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Wizards' Tower from the north.", new WorldPoint(3104, 3183, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_WIZARDS_TOWER_COMPLETE)
		)),
		new ChartingSeaSection("Lumbridge Basin", List.of(
			new ChartingTaskDefinition("Generic", "Find a treacherous rock formation south of Lumbridge Swamp.", new WorldPoint(3182, 3138, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROCK_LUMBRIDGE_BASIN_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find an unfortunate corpse on a small island south east of Tutorial Island.", new WorldPoint(3133, 3055, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_CORPSE_LUMBRIDGE_BASIN_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the western mine in Lumbridge Swamp from the south.", new WorldPoint(3145, 3136, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_LUMBRIDGE_MINE_COMPLETE)
		)),
		new ChartingSeaSection("Mudskipper Sound", List.of(
			new ChartingTaskDefinition("Generic", "Find a strange stone tablet south east of Musa Point.", new WorldPoint(2944, 3140, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_CRUNCH_POSTER_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Salty Grouper south west of Tutorial Island.", new WorldPoint(3054, 3053, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROWBOAT_MUDSKIPPER_SOUND_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Karamja Shipyard from the east.", new WorldPoint(3008, 3051, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_SHIPYARD_COMPLETE)
		)),
		new ChartingSeaSection("Rimmington Strait", List.of(
			new ChartingTaskDefinition("Generic", "Find a treacherous rock formation west of the Crafting Guild.", new WorldPoint(2905, 3283, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROCK_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a warning about dragons east of Crandor.", new WorldPoint(2873, 3262, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_DRAGON_STATUE_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Karamja Volcano from the north.", new WorldPoint(2839, 3212, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_KARAMJA_VOLCANO_COMPLETE)
		)),
		new ChartingSeaSection("Catherby Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find evidence of megashrimp activity east of the Legends' Guild.", new WorldPoint(2760, 3366, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_MEGASHRIMP_CATHERBY_BAY_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some odd aquatic plant life west of the Dark Wizards' Tower.", new WorldPoint(2897, 3337, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_THORNS_CATHERBY_BAY_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Keep le Faye from the east.", new WorldPoint(2784, 3403, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_KEEP_LE_FAYE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Entrana and sample the contents.", new WorldPoint(2869, 3380, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_MARROW_WINE_COMPLETE)
		)),
		new ChartingSeaSection("Brimhaven Passage", List.of(
			new ChartingTaskDefinition("Generic", "Find a set of someone else's charts south of the Fishing Platform and copy them.", new WorldPoint(2769, 3269, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_BOTTLE_BRIMHAVEN_PASSAGE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Shelled Snail east of Witchaven.", new WorldPoint(2741, 3272, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_WRECK_BRIMHAVEN_PASSAGE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Fishing Platform from the south.", new WorldPoint(2779, 3267, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_FISHING_PLATFORM_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south east of Ardougne Zoo.", new WorldPoint(2648, 3246, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_BRIMHAVEN_PASSAGE_COMPLETE)
		)),
		new ChartingSeaSection("Strait of Khazard", List.of(
			new ChartingTaskDefinition("Generic", "Find a notice from General Khazard south east of Port Khazard.", new WorldPoint(2681, 3136, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_SIGN_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a rowboat off the west coast of Karamja near Port Khazard.", new WorldPoint(2702, 3153, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROWBOAT_PORT_KHAZARD_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find what remains of Colbansea Island in the middle of the Strait of Khazard.", new WorldPoint(2720, 3105, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ROCK_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Tower of Life from the east.", new WorldPoint(2664, 3221, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_TWO_TOWERS_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Yanille Chain and sample the contents.", new WorldPoint(2674, 3105, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SLUG_BALM_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of the trawler in Port Khazard.", new WorldPoint(2664, 3174, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_STRAIT_OF_KHAZARD_COMPLETE)
		)),
		new ChartingSeaSection("Gu'tanoth Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find the wreck of an ogre ship in Gu'tanoth Bay.", new WorldPoint(2633, 3041, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_OGRE_BOAT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the island in Gu'tanoth Bay from the east.", new WorldPoint(2597, 3029, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_OGRE_ISLAND_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Yanille and sample the contents.", new WorldPoint(2624, 3071, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_OGRE_PRAYER_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of the Yanille Chain.", new WorldPoint(2676, 3037, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GUTANOTH_BAY_COMPLETE)
		)),
		new ChartingSeaSection("Feldip Gulf", List.of(
			new ChartingTaskDefinition("Generic", "Find an odd geological phenomenon in the middle of the Feldip Gulf.", new WorldPoint(2695, 2983, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_FELDIP_RIDGE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of the Feldip Gulf and sample the contents.", new WorldPoint(2691, 2942, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_POINT_PUNCH_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south east of Rantz's cave.", new WorldPoint(2645, 2976, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_FELDIP_GULF_COMPLETE)
		)),
		new ChartingSeaSection("Kharazi Strait", List.of(
			new ChartingTaskDefinition("Generic", "Find a crashed glider north of Ape Atoll.", new WorldPoint(2781, 2817, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_GLIDER_KHARAZI_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a map south west of the Kharazi Jungle and copy it.", new WorldPoint(2798, 2893, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_MAP_BOTTLE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some curious rapids west of Rock Island Prison.", new WorldPoint(2843, 2839, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_RAPIDLESS_RAPID_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Rock Island Prison and sample the contents.", new WorldPoint(2888, 2859, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_KHARAZI_COOLER_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents off the south western coast of the Kharazi Jungle.", new WorldPoint(2767, 2897, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_KHARAZI_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of a cave in the Kharazi Jungle from the south. Watch out for Stormy seas!", new WorldPoint(2945, 2880, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_KHARAZI_CAVE_COMPLETE)
		)),
		new ChartingSeaSection("Oo'glog Channel", List.of(
			new ChartingTaskDefinition("Generic", "Find the corpse of a sea monster north east of Corsair Cove.", new WorldPoint(2634, 2884, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_DEAD_MONSTER_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the bananaless boat west of Ape Atoll.", new WorldPoint(2693, 2795, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_BANANA_BOAT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the throne in Corsair Cove from the east.", new WorldPoint(2605, 2869, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_COVE_THRONE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate south of Corsair Cove and sample the contents.", new WorldPoint(2595, 2789, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_OOGLUG_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south west of Corsair Cove.", new WorldPoint(2529, 2831, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_OOGLOG_CHANNEL_COMPLETE)
		)),
		new ChartingSeaSection("Arrow Passage", List.of(
			new ChartingTaskDefinition("Generic", "Find some interesting aquatic life north west of Crash Island.", new WorldPoint(2881, 2752, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_SEA_MONKEYS_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the temple on Ape Atoll from the east.", new WorldPoint(2828, 2787, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_MONKEY_TEMPLE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the eastern coast of Ape Atoll and sample the contents.", new WorldPoint(2816, 2735, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_BANANA_DAIQUIRI_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of Crash Island.", new WorldPoint(2917, 2767, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_ARROW_PASSAGE_COMPLETE)
		)),
		new ChartingSeaSection("Menaphite Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find some collected sand on an island west of Menaphos.", new WorldPoint(3143, 2772, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_SAND_PIT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the pyramid in Menaphos from the west.", new WorldPoint(3145, 2736, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_MENAPHOS_PYRAMID_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Menaphos and sample the contents.", new WorldPoint(3190, 2708, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_CROCODILE_TEARS_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north of the island south of Menaphos.", new WorldPoint(3185, 2656, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_MENAPHITE_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of the Menaphite Sea.", new WorldPoint(3044, 2724, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MENAPHITE_SEA_COMPLETE)
		)),
		new ChartingSeaSection("The Simian Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find a makeshift flag near Cape Atoll.", new WorldPoint(2681, 2716, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_ATOLL_CAPE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Void Knights' Outpost from the north.", new WorldPoint(2654, 2684, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_VOID_KNIGHTS_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Void Knights' Outpost and sample the contents.", new WorldPoint(2677, 2656, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SPINNERS_GASP_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south west of Cape Atoll.", new WorldPoint(2691, 2705, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_THE_SIMIAN_SEA_COMPLETE)
		)),
		new ChartingSeaSection("The Storm Tempor", List.of(
			new ChartingTaskDefinition("Generic", "Find something trying to leech power from the Storm Tempor. Watch out for Stormy seas!", new WorldPoint(3065, 2887, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_LIGHTNING_ROD_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Desert Trout south of Tempoross Cove. Watch out for Stormy seas!", new WorldPoint(3033, 2798, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_GENERIC_DESERT_TROUT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of Tempoross Cove. Watch out for Stormy seas!", new WorldPoint(3001, 2849, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_STORM_TEMPOR_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Ruins of Unkah from north of the docks. Watch out for Stormy seas!", new WorldPoint(3144, 2856, 0), "Ardent Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_UNKAH_SHIP_COMPLETE)
		)),
		new ChartingSeaSection("Turtle Belt", List.of(
			new ChartingTaskDefinition("Generic", "Find a treasure chest south east of Dognose Island.", new WorldPoint(3075, 2603, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_TREASURE_CHEST_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the surrounding sea from Dognose Island.", new WorldPoint(3050, 2658, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_DOGNOSE_ISLAND_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Dognose Island and sample the contents.", new WorldPoint(3046, 2667, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_DOGNOSE_DRAUGHT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south west of the remote island in the Turtle Belt.", new WorldPoint(2947, 2586, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_TURTLE_BELT_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of the Turtle Belt.", new WorldPoint(3014, 2558, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_TURTLE_BELT_COMPLETE)
		)),
		new ChartingSeaSection("Sea of Shells", List.of(
			new ChartingTaskDefinition("Generic", "Find the nest of a fearless bird on the Onyx Crest.", new WorldPoint(2963, 2266, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_BIRD_NEST_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Summer Shore from the cliffs above it on the Great Conch.", new WorldPoint(3154, 2550, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_TORTUGAN_VILLAGE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the western coast of the Great Conch and sample the contents.", new WorldPoint(3098, 2460, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_WAY_HOME_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents near the Summer Shore docks on the Great Conch.", new WorldPoint(3204, 2369, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_GREAT_CONCH_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of the Onyx Crest.", new WorldPoint(2963, 2205, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SEA_OF_SHELLS_COMPLETE)
		)),
		new ChartingSeaSection("Red Reef", List.of(
			new ChartingTaskDefinition("Generic", "Find the corpse of a wayward shifter on a small island south of Red Rock.", new WorldPoint(2804, 2462, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_DEAD_SHIFTER_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Red Rock and sample the contents.", new WorldPoint(2778, 2525, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_REDDEST_RUM_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north east of Red Rock.", new WorldPoint(2902, 2550, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_RED_REEF_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south of the pest-filled island near the Void Knights' Outpost.", new WorldPoint(2665, 2562, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_PEST_ISLAND_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of Red Rock.", new WorldPoint(2788, 2550, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_RED_REEF_COMPLETE)
		)),
		new ChartingSeaSection("Anglerfish's Light", List.of(
			new ChartingTaskDefinition("Generic", "Find an odd light south west of Last Light.", new WorldPoint(2719, 2298, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_LARGE_LIGHT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Last Light from the east.", new WorldPoint(2895, 2328, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_ANGLERFISHS_LIGHTHOUSE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Last Light and sample the contents.", new WorldPoint(2840, 2282, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_LIGHT_DARK_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of Anglerfish's Light.", new WorldPoint(2766, 2384, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_ANGLERFISHS_LIGHT_COMPLETE)
		)),
		new ChartingSeaSection("Bay of Elidinis", List.of(
			new ChartingTaskDefinition("Generic", "Find a damaged golem east of Menaphos.", new WorldPoint(3243, 2704, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_GOLEM_CORPSE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Abalone Cliffs from the north east.", new WorldPoint(3187, 2432, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_CONCH_MOUNTAIN_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate south of Menaphos and sample the contents.", new WorldPoint(3239, 2600, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_LIFE_WATER_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents to the south west of the Elid Delta.", new WorldPoint(3248, 2705, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_BAY_OF_ELIDINIS_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the south east of the Bay of Elidinis.", new WorldPoint(3338, 2624, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_BAY_OF_ELIDINIS_COMPLETE)
		)),
		new ChartingSeaSection("Tortugan Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find a notable small shell north east of the Great Conch.", new WorldPoint(3284, 2516, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_MINOR_CONCH_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a strange apple tree somewhere on the Great Conch.", new WorldPoint(3216, 2468, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_CRAB_APPLE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the coast of the Great Conch from the north east.", new WorldPoint(3261, 2508, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_TORTUGAN_SEA_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents to the east of the eastern pier on the Great Conch.", new WorldPoint(3279, 2465, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_TORTUGAN_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth east of the Great Conch.", new WorldPoint(3282, 2447, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GREAT_CONCH_COMPLETE)
		)),
		new ChartingSeaSection("Pearl Bank", List.of(
			new ChartingTaskDefinition("Generic", "Find a ship in a bottle south of the Little Pearl.", new WorldPoint(3375, 2137, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_SHIP_BOTTLE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate south west of the Little Pearl and sample the contents.", new WorldPoint(3301, 2148, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_POSSIBLE_ALBUMEN_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents to the south west of the Little Pearl.", new WorldPoint(3337, 2181, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_PEARL_BANK_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the south of the Pearl Bank.", new WorldPoint(3276, 2132, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PEARL_BANK_COMPLETE)
		)),
		new ChartingSeaSection("The Lonely Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find some dragon scales on Charred Island.", new WorldPoint(2634, 2394, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_GENERIC_DRAGON_SCALES_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near some shipwrecks in the Lonely Sea and sample the contents.", new WorldPoint(2522, 2250, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ALONE_AT_SEA_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of Charred Island.", new WorldPoint(2624, 2417, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_THE_LONELY_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the far south of the Lonely Sea.", new WorldPoint(2528, 2151, 0), "Unquiet Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_THE_LONELY_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Fortis Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find a nest on Vatrachos Island.", new WorldPoint(1896, 2996, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_SEAGULL_NEST_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Fortis Colosseum from the south.", new WorldPoint(1823, 3048, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_FORTIS_BAY_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate east of Stonecutter Outpost and sample the contents.", new WorldPoint(1826, 2959, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ALCO_SOL_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north east of Stonecutter Outpost.", new WorldPoint(1779, 2990, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_FORTIS_BAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of Vatrachos Island.", new WorldPoint(1898, 3050, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_FORTIS_BAY_COMPLETE)
		)),
		new ChartingSeaSection("Aureum Coast", List.of(
			new ChartingTaskDefinition("Generic", "Find the wreck of the Counterweight north west of Deepfin Point.", new WorldPoint(1845, 2826, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_WRECK_WEIGHT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the south eastern temple in the Avium Savannah from the south.", new WorldPoint(1737, 2909, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_AUREUM_COAST_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of the Aureum Coast and sample the contents.", new WorldPoint(1762, 2856, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_PORTAL_PERRY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of Vatrachos Island.", new WorldPoint(1889, 2884, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_AUREUM_COAST_COMPLETE)
		)),
		new ChartingSeaSection("Wyrm's Waters", List.of(
			new ChartingTaskDefinition("Generic", "Find an interesting book on the Shimmering Atoll.", new WorldPoint(1593, 2757, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_ATOLL_DICTIONARY_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Colossal Wyrm Remains from the south.", new WorldPoint(1634, 2885, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_WYRMS_WATERS_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Shimmering Atoll and sample the contents.", new WorldPoint(1570, 2810, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_CONGRATULATION_WINE_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents within the Shimmering Atoll.", new WorldPoint(1543, 2787, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_WYRMS_WATERS_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth east of the Shimmering Atoll.", new WorldPoint(1711, 2820, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WYRMS_WATERS_COMPLETE)
		)),
		new ChartingSeaSection("Mythic Sea", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate west of the Void Knights' Outpost and sample the contents.", new WorldPoint(2556, 2666, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_MYTHS_MIXER_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find an abandoned camp on Anglers' Retreat. Watch out for Fetid waters!", new WorldPoint(2476, 2709, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_ABANDONED_CAMP_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents east of Anglers' Retreat. Watch out for Fetid waters!", new WorldPoint(2340, 2898, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_MYTHIC_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of the Myths' Guild. Watch out for Fetid waters!", new WorldPoint(2446, 2786, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MYTHIC_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Breakbone Strait", List.of(
			new ChartingTaskDefinition("Generic", "Find a chest of equipment north of the Myths' Guild. Watch out for Fetid waters!", new WorldPoint(2460, 2883, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_ARMY_ATTIRE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Tear of the Soul and sample the contents. Watch out for Fetid waters!", new WorldPoint(2334, 2783, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_GOLDLESS_ALE_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents off the eastern coast of the Isle of Souls. Watch out for Fetid waters!", new WorldPoint(2341, 2899, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_BREAKBONE_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Myths' Guild from the west. Watch out for Fetid waters!", new WorldPoint(2429, 2854, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_MYTHS_GUILD_COMPLETE)
		)),
		new ChartingSeaSection("Backwater", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the north eastern coast of the Isle of Souls and sample the contents. Watch out for Fetid waters!", new WorldPoint(2318, 2968, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_DESTRUCTORS_COCKTAIL_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the western coast of the Feldip Hills and sample the contents. Watch out for Fetid waters!", new WorldPoint(2476, 2971, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ZOGRES_KISS_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some interesting coral south east of the Poison Waste. Watch out for Fetid waters!", new WorldPoint(2300, 3045, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_DISEASED_CORAL_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents at the mouth of the River Dougne west of Chompy Marsh. Watch out for Fetid waters!", new WorldPoint(2318, 3059, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_BACKWATER_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Chompy Marsh from the south. Watch out for Fetid waters!", new WorldPoint(2399, 3029, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_TOAD_PONDS_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of Jiggig. Watch out for Fetid waters!", new WorldPoint(2469, 3008, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_BACKWATER_COMPLETE)
		)),
		new ChartingSeaSection("Zul-Egil", List.of(
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the ruined tower on the Isle of Souls from the south.", new WorldPoint(2128, 2985, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_RUINED_TOWER_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate south of the poison waste and sample the contents. Watch out for Fetid waters!", new WorldPoint(2279, 3038, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ZUL_RYE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a toxic hazard in the middle of Zul-Egil. Watch out for Fetid waters!", new WorldPoint(2165, 3032, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_POISON_SPILL_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Zul-Andra from the south. Watch out for Fetid waters!", new WorldPoint(2199, 3038, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_ZUL_ANDRA_COMPLETE)
		)),
		new ChartingSeaSection("The Skullhorde", List.of(
			new ChartingTaskDefinition("Generic", "Find the wreck of the Balanced Ballast south west of the Void Knights' Outpost.", new WorldPoint(2587, 2595, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_PEST_SHIPWRECK_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of the Skullhorde and sample the contents.", new WorldPoint(2387, 2479, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_FISH_STOUTIER_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of the Isle of Bones.", new WorldPoint(2488, 2532, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_THE_SKULLHORDE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south west of the Isle of Bones.", new WorldPoint(2460, 2451, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_THE_SKULLHORDE_COMPLETE)
		)),
		new ChartingSeaSection("Sea of Souls", List.of(
			new ChartingTaskDefinition("Generic", "Find the wreck of the Soul Searcher south west of the Tear of the Soul.", new WorldPoint(2289, 2714, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_SOUL_SHIPWRECK_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the southern coast of the Isle of Souls and sample the contents.", new WorldPoint(2221, 2767, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SOUL_BOTTLE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth east of the shipyard in the Sea of Souls.", new WorldPoint(2118, 2718, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SEA_OF_SOULS_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the surrounding sea from the Tear of the Soul. Watch out for Fetid waters!", new WorldPoint(2342, 2770, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_CAPE_SOUL_ISLAND_COMPLETE)
		)),
		new ChartingSeaSection("Soul Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find some interesting sea life in the middle of Soul Bay.", new WorldPoint(2089, 2799, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_LARGE_JELLYFISH_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the western coast of the Isle of Souls and sample the contents.", new WorldPoint(2086, 2884, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_CREATORS_COCKTAIL_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents off the western coast of the Isle of Souls.", new WorldPoint(2080, 2861, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_SOUL_BAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of the Isle of Souls.", new WorldPoint(2012, 2876, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SOUL_BAY_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on the south western coast of the Isle of Souls.", new WorldPoint(2140, 2808, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_SOUL_BAY_COMPLETE)
		)),
		new ChartingSeaSection("Barracuda Belt", List.of(
			new ChartingTaskDefinition("Generic", "Find a spool of Barracuda grade rope east of Wintumber Island.", new WorldPoint(2149, 2617, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_BARRACUDA_ROPE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find an odd statue on Wintumber Island.", new WorldPoint(2066, 2610, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_CRAB_STATUE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of a Barracuda ship north east of Sunbleak Island.", new WorldPoint(2281, 2449, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_BARRACUDA_PORTION_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the bank boat in the Barracuda Belt and sample the contents.", new WorldPoint(2282, 2508, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_BARRACUDA_BREW_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the bank boat in the Barracuda Belt.", new WorldPoint(2276, 2600, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_BARRACUDA_HQ_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south east of Wintumber Island.", new WorldPoint(2082, 2592, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_BARRACUDA_BELT_COMPLETE)
		)),
		new ChartingSeaSection("The Everdeep", List.of(
			new ChartingTaskDefinition("Generic", "Find an odd shadow south of Deepfin Point.", new WorldPoint(1976, 2682, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_SCARY_SHADOW_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Deepfin Point from the south.", new WorldPoint(1943, 2739, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_THE_EVERDEEP_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate west of Deepfin Point and sample the contents.", new WorldPoint(1854, 2773, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_CRYSTAL_VODKA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the far south east of the Everdeep.", new WorldPoint(1942, 2652, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_THE_EVERDEEP_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on the western coast of Deepfin Point.", new WorldPoint(1928, 2793, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_THE_EVERDEEP_COMPLETE)
		)),
		new ChartingSeaSection("Sapphire Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find some odd sea life in the middle of the Sapphire Sea.", new WorldPoint(1762, 2582, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_SEA_SAPPHIRES_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Crown Jewel from the east.", new WorldPoint(1796, 2666, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_SAPPHIRE_SEA_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate where three waters meet in the Sapphire Sea and sample the contents.", new WorldPoint(1675, 2480, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_COMP_KVASS_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of the Crown Jewel.", new WorldPoint(1745, 2664, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_SAPPHIRE_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of the Crown Jewel.", new WorldPoint(1762, 2613, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SAPPHIRE_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Western Gate", List.of(
			new ChartingTaskDefinition("Generic", "Find evidence of the Sharhai east of Vatrachos Island.", new WorldPoint(1947, 2987, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_SHARHAIS_PURSE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some interesting sea life in the middle of the Western Gate.", new WorldPoint(1996, 2949, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_SNAKE_EGGS_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of the Western Gate and sample the contents.", new WorldPoint(1993, 2974, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_MANGO_GIN_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents east of Minotaurs' Rest.", new WorldPoint(1978, 3108, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_WESTERN_GATE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of the Western Gate.", new WorldPoint(2041, 3025, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WESTERN_GATE_COMPLETE)
		)),
		new ChartingSeaSection("Rainbow Reef", List.of(
			new ChartingTaskDefinition("Generic", "Find a lost friend on Rainbow's End. Watch out for Tangled kelp!", new WorldPoint(2336, 2269, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_GNOME_BALL_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a rowboat to the far south of Rainbow's End. Watch out for Tangled kelp!", new WorldPoint(2334, 2120, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_EDGE_BOAT_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Sunbleak Island and sample the contents. Watch out for Tangled kelp!", new WorldPoint(2205, 2305, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_PLATINUM_RUM_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north of Rainbow's End. Watch out for Tangled kelp!", new WorldPoint(2335, 2288, 0), "Shrouded Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_RAINBOW_REEF_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of Rainbow Reef. Watch out for Tangled kelp!", null, "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_RAINBOW_REEF_COMPLETE)
		)),
		new ChartingSeaSection("Southern Expanse", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Isle of Serpents and sample the contents. Watch out for Tangled kelp!", null, "Shrouded Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_PUZZLERS_POTEEN_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a useful sign to the far south of the Isle of Serpents. Watch out for Tangled kelp!", null, "Shrouded Ocean", VarbitID.SAILING_CHARTING_GENERIC_NOTHING_SIGN_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small islands east of the Isle of Serpents. Watch out for Tangled kelp!", null, "Shrouded Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_SOUTHERN_EXPANSE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of Sunbleak Island. Watch out for Tangled kelp!", null, "Shrouded Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SOUTHERN_EXPANSE_COMPLETE)
		)),
		new ChartingSeaSection("Sunset Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find a wrecked shiplike thing south of Villa Lucens.", new WorldPoint(1448, 2898, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_GENERIC_PROP_WRECK_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Sunset Coast from the west.", new WorldPoint(1470, 2982, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_SUNSET_BAY_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Mistrock and sample the contents.", new WorldPoint(1426, 2867, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_DRUNK_IMPLING_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents at the mouth of the River Varla.", new WorldPoint(1422, 3048, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_SUNSET_BAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of the Colossal Wyrm Remains.", new WorldPoint(1575, 2944, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SUNSET_BAY_COMPLETE)
		)),
		new ChartingSeaSection("Misty Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find some discarded fishing equipment in the middle of the Misty Sea.", new WorldPoint(1439, 2718, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_GENERIC_FISHING_NETS_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Mistrock from the south.", new WorldPoint(1387, 2842, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_MISTY_SEA_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near some shipwrecks in the Misty Sea and sample the contents.", new WorldPoint(1561, 2657, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_MYSTERY_CIDER_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the south west of the Misty Sea.", new WorldPoint(1322, 2630, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MISTY_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Dusk's Maw", List.of(
			new ChartingTaskDefinition("Generic", "Find a collapsed cave on a small island south west of the Tlati Rainforest.", new WorldPoint(1232, 3007, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_GENERIC_CRAB_HOLE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the far west of Dusk's Maw and sample the contents.", new WorldPoint(1050, 2888, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SAILING_CAT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south west of the Darkmoon Ravine.", new WorldPoint(1259, 2878, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_DUSKS_MAW_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the Darkmoon Ravine.", new WorldPoint(1314, 2933, 0), "Sunset Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_DUSKS_MAW_COMPLETE)
		)),
		new ChartingSeaSection("Great Sound", List.of(
			new ChartingTaskDefinition("Generic", "Find an advert on a small island north east of Salvager Overlook.", new WorldPoint(1656, 3306, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_BARRACUDA_ADVERT_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find an interesting sign south east of Land's End.", new WorldPoint(1529, 3407, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_BEGINNING_SIGN_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Auburnvale from the north.", new WorldPoint(1413, 3396, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_GREAT_SOUND_COMPLETE)
		)),
		new ChartingSeaSection("Crabclaw Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find a sabotaged mooring point on Crabclaw Island.", new WorldPoint(1789, 3403, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_SABOTAGED_MOORING_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Salvager Overlook from the west.", new WorldPoint(1600, 3312, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_CRABCLAW_BAY_1_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Crabclaw Island from the south west.", new WorldPoint(1744, 3407, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_CRABCLAW_BAY_2_COMPLETE)
		)),
		new ChartingSeaSection("Gulf of Kourend", List.of(
			new ChartingTaskDefinition("Generic", "Find something in desperate need of repair north east of Port Piscarilius.", new WorldPoint(1847, 3798, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_BROKEN_CRANE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Port Piscarilius from north of the Foodhall.", new WorldPoint(1846, 3771, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_PORT_PISCARILIUS_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of the Gulf of Kourend and sample the contents.", new WorldPoint(1934, 3744, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_HEADLESS_UNICORNMAN_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north west of the Dibber.", new WorldPoint(1813, 3666, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_GULF_OF_KOUREND_COMPLETE)
		)),
		new ChartingSeaSection("Hosidian Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find some discarded farming equipment east of Hosidius.", new WorldPoint(1855, 3588, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_DISCARDED_PLOUGH_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Tithe Farm from the north east.", new WorldPoint(1861, 3530, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_TITHE_FARM_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the eastern coast of Hosidius and sample the contents.", new WorldPoint(1887, 3558, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_BANKERS_DRAUGHT_COMPLETE)
		)),
		new ChartingSeaSection("Pilgrims' Passage", List.of(
			new ChartingTaskDefinition("Generic", "Find evidence of an experiment on Chinchompa Island.", new WorldPoint(1887, 3426, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_CHINCHOMPA_TABLE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of Pilgrims' Passage and sample the contents.", new WorldPoint(1926, 3396, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ROBERTS_PORT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of Chinchompa Island.", new WorldPoint(1871, 3432, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_PILGRIMS_PASSAGE_COMPLETE)
		)),
		new ChartingSeaSection("Litus Lucis", List.of(
			new ChartingTaskDefinition("Generic", "Find a strange corpse north east of Civitas illa Fortis.", new WorldPoint(1871, 3179, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_KRAKEN_SKELETON_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of Litus Lucis and sample the contents.", new WorldPoint(1783, 3254, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SEA_SHANDY_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents east of the Twilight Temple.", new WorldPoint(1721, 3239, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_LITUS_LUCIS_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the Fortis Colosseum.", new WorldPoint(1821, 3168, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_LITUS_LUCIS_COMPLETE)
		)),
		new ChartingSeaSection("Porth Neigwl", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate off the western coast of Isafdar and sample the contents. Watch out for Crystal-flecked waters!", new WorldPoint(2161, 3205, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_CRYSTAL_WATER_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some lost cargo south east of Lledrith Island. Watch out for Crystal-flecked waters!", new WorldPoint(2118, 3160, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_HALBERD_POLES_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island west of Zul-Andra. Watch out for Crystal-flecked waters!", new WorldPoint(2149, 3070, 0), "Western Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_ZUL_EGIL_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island west of the Tyras Camp. Watch out for Crystal-flecked waters!", new WorldPoint(2118, 3161, 0), "Western Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_PORTH_NEIGWL_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of Lledrith Island. Watch out for Crystal-flecked waters!", new WorldPoint(2071, 3188, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_PORTH_NEIGWL_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Tyras Camp from the west. Watch out for Crystal-flecked waters!", new WorldPoint(2160, 3157, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_TYRAS_CAMP_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of Port Tyras. Watch out for Crystal-flecked waters!", new WorldPoint(2113, 3125, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PORTH_NEIGWL_COMPLETE)
		)),
		new ChartingSeaSection("Tirannwn Bight", List.of(
			new ChartingTaskDefinition("Generic", "Find a deadly trap west of the Iorwerth Camp. Watch out for Crystal-flecked waters!", new WorldPoint(2157, 3276, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_RIVER_MINE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Mynydd and sample the contents. Watch out for Crystal-flecked waters!", new WorldPoint(2120, 3420, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ELVEN_WINE_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather south west of Mynydd. Watch out for Crystal-flecked waters!", new WorldPoint(2130, 3397, 0), "Western Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_TIRANNWN_BIGHT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents at the mouth of the Afon Ganol west of the Iorwerth Camp. Watch out for Crystal-flecked waters!", new WorldPoint(2165, 3262, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_TIRANNWN_BIGHT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Prifddinas ' western gates from the south west. Watch out for Crystal-flecked waters!", new WorldPoint(2163, 3320, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_PRIFDDINAS_GATE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of the Tirannwn Bight. Watch out for Crystal-flecked waters!", new WorldPoint(2120, 3347, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_TIRANNWN_BIGHT_COMPLETE)
		)),
		new ChartingSeaSection("Crystal Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find an elven shipwreck in the middle of the Crystal Sea.", new WorldPoint(1980, 3234, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_ELVEN_SHIP_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the visitors book for the Port Roberts jail.", new WorldPoint(1890, 3273, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_JAIL_BOOK_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Port Roberts from the south.", new WorldPoint(1886, 3268, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_CRYSTAL_SEA_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of the Crystal Sea and sample the contents.", new WorldPoint(2008, 3263, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SNAKE_GRAVY_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents east of Port Roberts.", new WorldPoint(1920, 3298, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_CRYSTAL_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of the Crystal Sea.", new WorldPoint(1979, 3213, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_CRYSTAL_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Porth Gwenith", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Ynysdail and sample the contents. Watch out for Crystal-flecked waters!", null, "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_UNDERGROUND_MILK_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find an unusual boat north of Gwenith. Watch out for Crystal-flecked waters!", new WorldPoint(2210, 3447, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_CRYSTAL_DINGHY_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north west of Eagles' Peak. Watch out for Crystal-flecked waters!", new WorldPoint(2280, 3520, 0), "Western Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_VAGABONDS_REST_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north of Gwenith. Watch out for Crystal-flecked waters!", new WorldPoint(2220, 3431, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_PORTH_GWENITH_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of Gorlah. Watch out for Crystal-flecked waters!", new WorldPoint(2283, 3461, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PORTH_GWENITH_COMPLETE)
		)),
		new ChartingSeaSection("Piscatoris Sea", List.of(
			new ChartingTaskDefinition("Generic", "Find evidence of kraken activity south west of the Piscatoris Fishing Colony.", new WorldPoint(2300, 3664, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_KRAKEN_SLIME_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the Piscatoris Fishing Colony from the north west.", new WorldPoint(2303, 3702, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_FISHING_COLONY_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate north west of the Piscatoris Fishing Colony and sample the contents.", new WorldPoint(2248, 3746, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_MONKFISH_STOUT_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south west of Kraken Cove. Watch out for Crystal-flecked waters!", new WorldPoint(2260, 3591, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PISCATORIS_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Vagabonds Rest", List.of(
			new ChartingTaskDefinition("Generic", "Find a crate of discarded goods north of Drumstick Isle.", new WorldPoint(2133, 3591, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_HUNTER_OUTFITS_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a half-built ship west of Buccaneers' Haven.", new WorldPoint(2026, 3671, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_HALF_BUILT_SHIP_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Buccaneers' Haven from the north.", new WorldPoint(2101, 3704, 0), "Western Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_VAGABONDS_REST_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Buccaneers' Haven and sample the contents.", new WorldPoint(2079, 3657, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SEA_SPRAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of Buccaneers' Haven.", new WorldPoint(2018, 3707, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_VAGABONDS_REST_COMPLETE)
		)),
		new ChartingSeaSection("Moonshadow", List.of(
			new ChartingTaskDefinition("Generic", "Find the wreck of the Lunar Longship south west of Pirates' Cove.", new WorldPoint(2170, 3775, 0), "Western Ocean", VarbitID.SAILING_CHARTING_GENERIC_LUNAR_WRECK_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate south west of Lunar Isle and sample the contents.", new WorldPoint(2030, 3831, 0), "Western Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_LUNARSHINE_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of Pirates' Cove.", new WorldPoint(2122, 3798, 0), "Western Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_MOONSHADOW_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth isouth west of Lunar Isle.", new WorldPoint(2044, 3794, 0), "Western Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MOONSHADOW_COMPLETE)
		)),
		new ChartingSeaSection("Fremensund", List.of(
			new ChartingTaskDefinition("Generic", "Find some suspicious eyes west of Rellekka.", new WorldPoint(2587, 3659, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_CRAB_EYES_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the market in Rellekka from the north.", new WorldPoint(2636, 3709, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_FREMENSUND_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate north of Rellekka and sample the contents.", new WorldPoint(2689, 3734, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_FISHIER_STOUT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north of Rower's Arm.", new WorldPoint(2561, 3628, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_FREMENSUND_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of Waterbirth Island.", new WorldPoint(2518, 3692, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_FREMENSUND_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south west of Rellekka.", new WorldPoint(2594, 3646, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_FREMENSUND_COMPLETE)
		)),
		new ChartingSeaSection("Grandroot Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find the roots of the Grand Tree in Grandroot Bay.", new WorldPoint(2479, 3548, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_TREE_ROOTS_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the lighthouse on Rower's Arm from the south.", new WorldPoint(2504, 3614, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_GRANDROOT_BAY_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of Grandroot Bay and sample the contents.", new WorldPoint(2453, 3599, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_TOAD_CIDER_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents in the River Crannmor.", new WorldPoint(2369, 3509, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_GRANDROOT_BAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of the Barbarian Outpost.", new WorldPoint(2513, 3573, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GRANDROOT_BAY_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather west of the Barbarian Outpost.", new WorldPoint(2498, 3548, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_GRANDROOT_BAY_COMPLETE)
		)),
		new ChartingSeaSection("V's Belt", List.of(
			new ChartingTaskDefinition("Generic", "Find an informative sign on a small island south of Miscellania.", new WorldPoint(2528, 3820, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_GHRIM_SIGN_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the castle on Miscellania from the west.", new WorldPoint(2477, 3864, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_VS_BELT_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of V's Belt and sample the contents.", new WorldPoint(2474, 3800, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_BLUE_LAGOON_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north east of Jatizso.", new WorldPoint(2428, 3833, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_VS_BELT_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north west of Miscellania. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_VS_BELT_COMPLETE)
		)),
		new ChartingSeaSection("Fremennik Strait", List.of(
			new ChartingTaskDefinition("Generic", "Find a discarded book south of Jatizso.", new WorldPoint(2383, 3771, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_NEDS_BOOK_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the towers on the Fremennik Isles from the south.", new WorldPoint(2366, 3779, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_FREMENNIK_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate north west of the Piscatoris Fishing Colony and sample the contents.", new WorldPoint(2273, 3747, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_FISHTONGUE_TONIC_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of Neitiznot.", new WorldPoint(2305, 3797, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_FREMENNIK_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on the southern coast of Neitiznot.", new WorldPoint(2341, 3792, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_FREMENNIK_STRAIT_COMPLETE)
		)),
		new ChartingSeaSection("Idestia Strait", List.of(
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the castle on Etceteria from the east.", new WorldPoint(2636, 3876, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_IDESTIA_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate south east of Etceteria and sample the contents.", new WorldPoint(2651, 3813, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_ENDLESS_NIGHT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of the hunter area near Trollweiss Mountain.", new WorldPoint(2695, 3830, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_IDESTIA_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a hunter free haven on a small island west of Trollweiss Mountain. Watch out for Icy seas!", new WorldPoint(2726, 3857, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_KEBBIT_BURROW_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north of Etceteria. Watch out for Icy seas!", new WorldPoint(2600, 3912, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_IDESTIA_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of the Iceberg. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_IDESTIA_STRAIT_COMPLETE)
		)),
		new ChartingSeaSection("Lunar Bay", List.of(
			new ChartingTaskDefinition("Generic", "Find evidence of some strong magical spells east of Lunar Isle.", new WorldPoint(2225, 3894, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_MAGIC_WARDS_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the pirate galleon docked at Lunar Isle from the east.", new WorldPoint(2160, 3903, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_LUNAR_BAY_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Lunar Isle docks and sample the contents. Reverse into the pier to reach it.", new WorldPoint(2142, 3876, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_EXILES_WELCOME_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south of the Astral Altar on Lunar Isle.", new WorldPoint(2159, 3851, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_LUNAR_BAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north east of Pirates' Cove.", new WorldPoint(2221, 3835, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_LUNAR_BAY_COMPLETE)
		)),
		new ChartingSeaSection("Winter's Edge", List.of(
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the town on Lunar Isle from the west.", new WorldPoint(2051, 3915, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_WINTERS_EDGE_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Brittle Isle and sample the contents. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SOUL_JUICE_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find an odd dead plant on a small island north west of Lunar Isle. Watch out for Icy seas!", new WorldPoint(2083, 3955, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_DEAD_LIVID_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north of the River of Souls. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_WINTERS_EDGE_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north east of the River of Souls. Watch out for Icy seas!", new WorldPoint(1866, 3913, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_WINTERS_EDGE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the middle of Winter's Edge. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WINTERS_EDGE_COMPLETE)
		)),
		new ChartingSeaSection("Lunar Sea", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the far north of the Lunar Sea and sample the contents. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_SUQAH_COLA_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some cleaning supplies north of Lunar Isle. Watch out for Icy seas!", new WorldPoint(2144, 4069, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_LUNAR_BROOMS_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Ungael from the west. Watch out for Icy seas!", new WorldPoint(2237, 4066, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_LUNAR_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the north west of the Lunar Sea. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_LUNAR_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Everwinter Sea", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the far north of the Everwinter Sea and sample the contents. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_WINTER_SUN_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some odd eggs north east of the Fishing Hamlet. Watch out for Icy seas!", new WorldPoint(1762, 4007, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_TOAD_SPAWN_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on Brittle Isle. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_EVERWINTER_SEA_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents near the shipwrecks in the Everwinter Sea. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_EVERWINTER_SEA_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the area north of the River of Souls from the east. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_RIVER_OF_SOULS_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the River of Souls. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_EVERWINTER_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Kannski Tides", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the waters of the Fremennik Isles and sample the contents.", new WorldPoint(2359, 3880, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_BLACK_LOBSTER_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find the wreck of the Fearless Fremennik south west of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2426, 3963, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_FEARLESS_FREMENNIK_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north of the western cave on the Fremennik Isles. Watch out for Icy seas!", new WorldPoint(2316, 3905, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_KANNSKI_TIDES_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the ice trolls of the Fremennik Isles from the north. Watch out for Icy seas!", new WorldPoint(2351, 3906, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_KANNSKI_TIDES_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the Fremennik Isles. Watch out for Icy seas!", new WorldPoint(2505, 3894, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_KANNSKI_TIDES_COMPLETE)
		)),
		new ChartingSeaSection("Weissmere", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the middle of Weissmere and sample the contents. Watch out for Icy seas!", new WorldPoint(2775, 3937, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_DWARVERN_WIZARD_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find some missing mountaineering equipment north of Trollweiss Mountain. Watch out for Icy seas!", new WorldPoint(2783, 3884, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_DISCARDED_SLED_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north west of Weiss. Watch out for Icy seas!", new WorldPoint(2833, 3962, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_WEISSMERE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the large rock formations in Weissmere from their centre. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_WEISSMERE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north west of Weiss. Watch out for Icy seas!", new WorldPoint(2816, 3977, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WEISSMERE_COMPLETE)
		)),
		new ChartingSeaSection("Stoneheart Sea", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the far north of the Stoneheart Sea and sample the contents. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_KGP_MARTINI_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find a shark corpse on a small island north west of Etceteria. Watch out for Icy seas!", new WorldPoint(2587, 3921, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_SHARK_CORPSE_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south west of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2447, 3997, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_STONEHEART_SEA_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2433, 4008, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_STONEHEART_SEA_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the KGP training area from the west. Watch out for Icy seas!", new WorldPoint(2620, 4061, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_STONEHEART_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the Island of Stone. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_STONEHEART_SEA_COMPLETE)
		)),
		new ChartingSeaSection("Shiverwake Expanse", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the large rock formations in Weissmere and sample the contents. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_CORPSE_REVIVER_COMPLETE),
			new ChartingTaskDefinition("Generic", "Find evidence of spying north east of the Iceberg. Watch out for Icy seas!", new WorldPoint(2709, 4126, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_KGP_PERISCOPE_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on the large rock formations in Weissmere. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_SHIVERWAKE_EXPANSE_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of Grimstone from the west. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_SHIVERWAKE_EXPANSE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the east of the Shiverwake Expanse. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SHIVERWAKE_EXPANSE_COMPLETE)
		)),
		new ChartingSeaSection("Weiss Melt", List.of(
			new ChartingTaskDefinition("Generic", "Find a failing boat north east of Weiss. Watch out for Icy seas!", new WorldPoint(2902, 3967, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_GENERIC_ICE_SHIP_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate north of Grimstone and sample the contents. Watch out for Icy seas!", null, "Northern Ocean", VarbitID.SAILING_CHARTING_DRINK_CRATE_WEISS_MELTWATER_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north of the Frozen Waste Plateau. Watch out for Icy seas!", new WorldPoint(2951, 3964, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_WEATHER_TROLL_WEISS_MELT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents north of Weiss. Watch out for Icy seas!", new WorldPoint(2880, 3987, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_CURRENT_DUCK_WEISS_MELT_COMPLETE),
			new ChartingTaskDefinition("Spyglass", "Use your spyglass to get a good view of the agility course in the Wilderness from the north. Watch out for Icy seas!", new WorldPoint(2990, 3973, 0), "Northern Ocean", VarbitID.SAILING_CHARTING_SPYGLASS_WEISS_MELT_COMPLETE)
		)),
		new ChartingSeaSection("Bonus Drinks", List.of(
			new ChartingTaskDefinition("Crate", "Find a Sealed crate in the Lum Lagoon and sample the contents.", new WorldPoint(3277, 3137, 0), "", VarbitID.SAILING_CHARTING_DRINK_CRATE_SORODAMIN_BRU_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near Mudskipper Point and sample the contents.", new WorldPoint(2994, 3136, 0), "", VarbitID.SAILING_CHARTING_DRINK_CRATE_SMUGGLED_RUM_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate near the Pandemonium and sample the contents.", new WorldPoint(3013, 3000, 0), "", VarbitID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES_COMPLETE),
			new ChartingTaskDefinition("Crate", "Find a Sealed crate west of Land's End and sample the contents.", new WorldPoint(1437, 3432, 0), "", VarbitID.SAILING_CHARTING_DRINK_CRATE_WILD_WHISKY_COMPLETE)
		)),
		new ChartingSeaSection("Bonus Currents", List.of(
			new ChartingTaskDefinition("Current", "Test the currents at the mouth of the River Amja south of Musa Point.", new WorldPoint(2890, 3123, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_MUSA_POINT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south of Lacerta Falls.", new WorldPoint(1384, 3467, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_GREAT_SOUND_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south of the small island in the Bay of Sarim.", new WorldPoint(3082, 3203, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_FAIRY_RING_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south west of the Kharidian Bandit Camp.", new WorldPoint(3145, 2965, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_KHARIDIAN_SEA_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south west of the Makeover Mage 's home.", new WorldPoint(2902, 3319, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents south west of the Obelisk of Water.", new WorldPoint(2835, 3419, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_CATHERBY_BAY_COMPLETE),
			new ChartingTaskDefinition("Current", "Test the currents west of the Lum Lagoon.", new WorldPoint(3209, 3136, 0), "", VarbitID.SAILING_CHARTING_CURRENT_DUCK_LUMBRIDGE_BASIN_COMPLETE)
		)),
		new ChartingSeaSection("Bonus Dives", List.of(
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the east of the Hosidian Sea.", new WorldPoint(1969, 3599, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_HOSIDIAN_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth in the south of Crabclaw Bay.", new WorldPoint(1681, 3382, 0), "Miscellanious", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_CRABCLAW_BAY_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of Port Roberts.", new WorldPoint(1880, 3374, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PILGRIMS_PASSAGE_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth north of the Dibber.", new WorldPoint(1851, 3670, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GULF_OF_KOUREND_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south of Mudskipper Point.", new WorldPoint(2979, 3087, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MUDSKIPPER_SOUND_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south west of Brimhaven.", new WorldPoint(2757, 3140, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth south west of the Pandemonium.", new WorldPoint(2988, 2951, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_KHARIDIAN_SEA_COMPLETE),
			new ChartingTaskDefinition("Diving", "With help from a mermaid guide, document the water depth west of the entrance to Taverley Dungeon.", new WorldPoint(2873, 3399, 0), "", VarbitID.SAILING_CHARTING_MERMAID_GUIDE_CATHERBY_BAY_COMPLETE)
		)),
		new ChartingSeaSection("Bonus Weather", List.of(
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather at the south east tip of Cape Conch.", new WorldPoint(3321, 2329, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_GREAT_CONCH_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather north east of Rantz's cave.", new WorldPoint(2638, 3011, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_GUTANOTH_BAY_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island east of the Kharazi Jungle. Watch out for Stormy seas!", new WorldPoint(2979, 2907, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_STORM_TEMPOR_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north of Civitas illa Fortis.", new WorldPoint(1714, 3195, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_LITUS_LUCIS_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north of Laguna Aurorae.", new WorldPoint(1188, 2824, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_DUSKS_MAW_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island north of Port Khazard.", new WorldPoint(2656, 3197, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south east of the Tithe Farm.", new WorldPoint(1867, 3460, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_PILGRIMS_PASSAGE_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south of Crash Island.", new WorldPoint(2903, 2700, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_ARROW_PASSAGE_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south of Last Light.", new WorldPoint(2864, 2313, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_ANGLERFISHS_LIGHT_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island south of the Woodcutting Guild.", new WorldPoint(1651, 3463, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_CRABCLAW_BAY_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on a small island west of Rimmington.", new WorldPoint(2912, 3215, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on the Isle of Bones.", new WorldPoint(2542, 2543, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_THE_SKULLHORDE_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather on the eastern coast of the Isle of Souls. Watch out for Fetid waters!", new WorldPoint(2335, 2923, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_BREAKBONE_STRAIT_COMPLETE),
			new ChartingTaskDefinition("Weather", "Help the meteorologist document the local weather south east of Stonecutter Outpost.", new WorldPoint(1779, 2942, 0), "", VarbitID.SAILING_CHARTING_WEATHER_TROLL_AUREUM_COAST_COMPLETE)
		))
	);

	public static List<ChartingSeaSection> getSections()
	{
		return SECTIONS;
	}
}
