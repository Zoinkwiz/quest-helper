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
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.coords.WorldPoint;

// All data generated from the OSRS Wiki (https://oldschool.runescape.wiki/w/Sea_charting)
public final class ChartingTasksData
{
	private ChartingTasksData() {}

	private static final List<ChartingSeaSection> SECTIONS = List.of(
		new ChartingSeaSection(0, "Kharidian Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a crashed glider south of the Karamja Shipyard.", new WorldPoint(2987, 3010, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_GLIDER_KHARIDIAN_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Pandemonium II west of the Pandemonium.", new WorldPoint(2973, 2994, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_PANDEMONIUM_2_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Pandemonium III west of the Kharidian Bandit Camp.", new WorldPoint(3143, 2979, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_PANDEMONIUM_3_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to admire Chartin' Charles McAtless in the cave on the Pandemonium.", new WorldPoint(3051, 9388, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_CHARTING_TUTOR_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Pandemonium from the cave entrance on the island.", new WorldPoint(3045, 2995, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_THE_PANDEMONIUM_COMPLETE)
		)),
		new ChartingSeaSection(1, "Bay of Sarim", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a treacherous rock formation near Draynor Village.", new WorldPoint(3079, 3231, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROCK_BAY_OF_SARIM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Grandyozenaym north east of the Wizards' Tower.", new WorldPoint(3126, 3202, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROWBOAT_BAY_OF_SARIM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Interact with the salvaging station in Port Sarim.", new WorldPoint(3029, 3207, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SALVAGE_STATION_PORT_SARIM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Look at the port task board in Port Sarim.", new WorldPoint(3030, 3198, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BOARD_PORT_SARIM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Wizards' Tower from the north.", new WorldPoint(3104, 3181, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_WIZARDS_TOWER_COMPLETE)
		)),
		new ChartingSeaSection(2, "Lumbridge Basin", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a treacherous rock formation south of Lumbridge Swamp.", new WorldPoint(3182, 3136, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROCK_LUMBRIDGE_BASIN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an unfortunate corpse on a small island south east of Tutorial Island.", new WorldPoint(3133, 3053, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CORPSE_LUMBRIDGE_BASIN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the western mine in Lumbridge Swamp from the south.", new WorldPoint(3145, 3134, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_LUMBRIDGE_MINE_COMPLETE)
		)),
		new ChartingSeaSection(3, "Mudskipper Sound", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a strange stone tablet south east of Musa Point.", new WorldPoint(2944, 3138, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CRUNCH_POSTER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Salty Grouper south west of Tutorial Island.", new WorldPoint(3054, 3051, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROWBOAT_MUDSKIPPER_SOUND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Karamja Shipyard from the east.", new WorldPoint(3008, 3049, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_SHIPYARD_COMPLETE)
		)),
		new ChartingSeaSection(4, "Rimmington Strait", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a treacherous rock formation west of the Crafting Guild.", new WorldPoint(2905, 3281, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROCK_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a warning about dragons east of Crandor.", new WorldPoint(2873, 3260, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_DRAGON_STATUE_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Karamja Volcano from the north.", new WorldPoint(2839, 3210, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_KARAMJA_VOLCANO_COMPLETE)
		)),
		new ChartingSeaSection(43, "Great Sound", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an advert on a small island north east of Salvager Overlook.", new WorldPoint(1656, 3304, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BARRACUDA_ADVERT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an interesting sign south east of Land's End.", new WorldPoint(1529, 3405, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BEGINNING_SIGN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Auburnvale from the north.", new WorldPoint(1413, 3394, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_GREAT_SOUND_COMPLETE)
		)),
		new ChartingSeaSection(44, "Crabclaw Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a sabotaged mooring point on Crabclaw Island.", new WorldPoint(1789, 3401, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SABOTAGED_MOORING_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Salvager Overlook from the west.", new WorldPoint(1600, 3310, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_CRABCLAW_BAY_1_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Crabclaw Island from the south west.", new WorldPoint(1744, 3405, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_CRABCLAW_BAY_2_COMPLETE)
		)),
		new ChartingSeaSection(5, "Catherby Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find evidence of megashrimp activity east of the Legends' Guild.", new WorldPoint(2760, 3364, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_MEGASHRIMP_CATHERBY_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some odd aquatic plant life west of the Dark Wizards' Tower.", new WorldPoint(2897, 3335, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_THORNS_CATHERBY_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Keep le Faye from the east.", new WorldPoint(2784, 3401, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_KEEP_LE_FAYE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Entrana and sample the contents.", new WorldPoint(2869, 3378, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_MARROW_WINE_COMPLETE)
		)),
		new ChartingSeaSection(46, "Hosidian Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some discarded farming equipment east of Hosidius.", new WorldPoint(1855, 3586, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_DISCARDED_PLOUGH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Tithe Farm from the north east.", new WorldPoint(1861, 3528, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_TITHE_FARM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the eastern coast of Hosidius and sample the contents.", new WorldPoint(1887, 3556, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_BANKERS_DRAUGHT_COMPLETE)
		)),
		new ChartingSeaSection(70, "Bonus Drinks", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the Lum Lagoon and sample the contents.", new WorldPoint(3277, 3135, 0), "", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SORODAMIN_BRU_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Mudskipper Point and sample the contents.", new WorldPoint(2994, 3134, 0), "", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SMUGGLED_RUM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Pandemonium and sample the contents.", new WorldPoint(3013, 2998, 0), "", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate west of Land's End and sample the contents.", new WorldPoint(1437, 3430, 0), "", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_WILD_WHISKY_COMPLETE)
		)),
		new ChartingSeaSection(6, "Brimhaven Passage", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a set of someone else's charts south of the Fishing Platform and copy them.", new WorldPoint(2769, 3267, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BOTTLE_BRIMHAVEN_PASSAGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Shelled Snail east of Witchaven.", new WorldPoint(2741, 3270, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_WRECK_BRIMHAVEN_PASSAGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Fishing Platform from the south.", new WorldPoint(2779, 3265, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_FISHING_PLATFORM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south east of Ardougne Zoo.", new WorldPoint(2648, 3244, 0), "Ardent Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_BRIMHAVEN_PASSAGE_COMPLETE)
		)),
		new ChartingSeaSection(7, "Strait of Khazard", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a notice from General Khazard south east of Port Khazard.", new WorldPoint(2681, 3134, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SIGN_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a rowboat off the west coast of Karamja near Port Khazard.", new WorldPoint(2702, 3151, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROWBOAT_PORT_KHAZARD_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find what remains of Colbansea Island in the middle of the Strait of Khazard.", new WorldPoint(2720, 3103, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ROCK_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Tower of Life from the east.", new WorldPoint(2664, 3219, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_TWO_TOWERS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Yanille Chain and sample the contents.", new WorldPoint(2674, 3103, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SLUG_BALM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of the trawler in Port Khazard.", new WorldPoint(2664, 3172, 0), "Ardent Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_STRAIT_OF_KHAZARD_COMPLETE)
		)),
		new ChartingSeaSection(9, "Feldip Gulf", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an odd geological phenomenon in the middle of the Feldip Gulf.", new WorldPoint(2695, 2981, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_FELDIP_RIDGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of the Feldip Gulf and sample the contents.", new WorldPoint(2691, 2940, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_POINT_PUNCH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south east of Rantz's cave.", new WorldPoint(2645, 2974, 0), "Ardent Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_FELDIP_GULF_COMPLETE)
		)),
		new ChartingSeaSection(14, "The Simian Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a makeshift flag near Cape Atoll.", new WorldPoint(2681, 2714, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ATOLL_CAPE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Void Knights' Outpost from the north.", new WorldPoint(2654, 2682, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_VOID_KNIGHTS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Void Knights' Outpost and sample the contents.", new WorldPoint(2677, 2654, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SPINNERS_GASP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south west of Cape Atoll.", new WorldPoint(2691, 2703, 0), "Ardent Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_THE_SIMIAN_SEA_COMPLETE)
		)),
		new ChartingSeaSection(45, "Gulf of Kourend", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find something in desperate need of repair north east of Port Piscarilius.", new WorldPoint(1847, 3796, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BROKEN_CRANE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Port Piscarilius from north of the Foodhall.", new WorldPoint(1846, 3769, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_PORT_PISCARILIUS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of the Gulf of Kourend and sample the contents.", new WorldPoint(1934, 3742, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_HEADLESS_UNICORNMAN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north west of the Dibber.", new WorldPoint(1813, 3664, 0), "Western Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_GULF_OF_KOUREND_COMPLETE)
		)),
		new ChartingSeaSection(47, "Pilgrims' Passage", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find evidence of an experiment on Chinchompa Island.", new WorldPoint(1887, 3424, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CHINCHOMPA_TABLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of Pilgrims' Passage and sample the contents.", new WorldPoint(1926, 3394, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_ROBERTS_PORT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of Chinchompa Island.", new WorldPoint(1871, 3430, 0), "Western Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_PILGRIMS_PASSAGE_COMPLETE)
		)),
		new ChartingSeaSection(71, "Bonus Currents", List.of(
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents at the mouth of the River Amja south of Musa Point.", new WorldPoint(2890, 3121, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_MUSA_POINT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south of Lacerta Falls.", new WorldPoint(1384, 3465, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_GREAT_SOUND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south of the small island in the Bay of Sarim.", new WorldPoint(3082, 3201, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_FAIRY_RING_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south west of the Kharidian Bandit Camp.", new WorldPoint(3145, 2963, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_KHARIDIAN_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south west of the Makeover Mage 's home.", new WorldPoint(2902, 3317, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south west of the Obelisk of Water.", new WorldPoint(2835, 3417, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_CATHERBY_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of the Lum Lagoon.", new WorldPoint(3209, 3134, 0), "", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_LUMBRIDGE_BASIN_COMPLETE)
		)),
		new ChartingSeaSection(10, "Kharazi Strait", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a crashed glider north of Ape Atoll.", new WorldPoint(2781, 2815, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_GLIDER_KHARAZI_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a map south west of the Kharazi Jungle and copy it.", new WorldPoint(2798, 2891, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_MAP_BOTTLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some curious rapids west of Rock Island Prison.", new WorldPoint(2843, 2837, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_RAPIDLESS_RAPID_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Rock Island Prison and sample the contents.", new WorldPoint(2888, 2857, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_KHARAZI_COOLER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents off the south western coast of the Kharazi Jungle.", new WorldPoint(2767, 2895, 0), "Ardent Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_KHARAZI_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of a cave in the Kharazi Jungle from the south. Watch out for Stormy seas!", new WorldPoint(2945, 2878, 0), "Ardent Ocean", 24, VarbitID.SAILING_CHARTING_SPYGLASS_KHARAZI_CAVE_COMPLETE)
		)),
		new ChartingSeaSection(15, "The Storm Tempor", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find something trying to leech power from the Storm Tempor. Watch out for Stormy seas!", new WorldPoint(3065, 2885, 0), "Ardent Ocean", 24, VarbitID.SAILING_CHARTING_GENERIC_LIGHTNING_ROD_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Desert Trout south of Tempoross Cove. Watch out for Stormy seas!", new WorldPoint(3033, 2796, 0), "Ardent Ocean", 24, VarbitID.SAILING_CHARTING_GENERIC_DESERT_TROUT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of Tempoross Cove. Watch out for Stormy seas!", new WorldPoint(3001, 2847, 0), "Ardent Ocean", 24, VarbitID.SAILING_CHARTING_CURRENT_DUCK_STORM_TEMPOR_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Ruins of Unkah from north of the docks. Watch out for Stormy seas!", new WorldPoint(3144, 2854, 0), "Ardent Ocean", 24, VarbitID.SAILING_CHARTING_SPYGLASS_UNKAH_SHIP_COMPLETE)
		)),
		new ChartingSeaSection(8, "Gu'tanoth Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of an ogre ship in Gu'tanoth Bay.", new WorldPoint(2633, 3039, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_OGRE_BOAT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the island in Gu'tanoth Bay from the east.", new WorldPoint(2597, 3027, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_OGRE_ISLAND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Yanille and sample the contents.", new WorldPoint(2624, 3069, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_OGRE_PRAYER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of the Yanille Chain.", new WorldPoint(2676, 3035, 0),  "Ardent Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GUTANOTH_BAY_COMPLETE, "The answer is 'Earth Impling'.", List.of(ItemID.II_CAPTURED_IMPLING_4))
		)),
		new ChartingSeaSection(12, "Arrow Passage", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some interesting aquatic life north west of Crash Island.", new WorldPoint(2881, 2750, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SEA_MONKEYS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the temple on Ape Atoll from the east.", new WorldPoint(2828, 2785, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_MONKEY_TEMPLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the eastern coast of Ape Atoll and sample the contents.", new WorldPoint(2816, 2733, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_BANANA_DAIQUIRI_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of Crash Island.", new WorldPoint(2917, 2765, 0), "Ardent Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_ARROW_PASSAGE_COMPLETE, "The answer is 'Harralander potion (unf)'.", List.of(ItemID.HARRALANDERVIAL))
		)),
		new ChartingSeaSection(13, "Menaphite Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some collected sand on an island west of Menaphos.", new WorldPoint(3143, 2770, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SAND_PIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the pyramid in Menaphos from the west.", new WorldPoint(3145, 2734, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_MENAPHOS_PYRAMID_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Menaphos and sample the contents.", new WorldPoint(3190, 2706, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_CROCODILE_TEARS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north of the island south of Menaphos.", new WorldPoint(3185, 2654, 0), "Ardent Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_MENAPHITE_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of the Menaphite Sea.", new WorldPoint(3044, 2722, 0), "Ardent Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MENAPHITE_SEA_COMPLETE, "The answer is 'Sandwich lady bottom'.", List.of(ItemID.SANDWICH_LADY_BOTTOM))
		)),
		new ChartingSeaSection(18, "Red Reef", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the corpse of a wayward shifter on a small island south of Red Rock.", new WorldPoint(2804, 2460, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_DEAD_SHIFTER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Red Rock and sample the contents.", new WorldPoint(2778, 2523, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_REDDEST_RUM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north east of Red Rock.", new WorldPoint(2902, 2548, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_RED_REEF_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south of the pest-filled island near the Void Knights' Outpost.", new WorldPoint(2665, 2560, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_PEST_ISLAND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of Red Rock.", new WorldPoint(2788, 2548, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_RED_REEF_COMPLETE, "The answer is 10 watermelons.", List.of(ItemID.WATERMELON))
		)),
		new ChartingSeaSection(19, "Anglerfish's Light", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an odd light south west of Last Light.", new WorldPoint(2719, 2296, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_LARGE_LIGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Last Light from the east.", new WorldPoint(2895, 2326, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_ANGLERFISHS_LIGHTHOUSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Last Light and sample the contents.", new WorldPoint(2840, 2280, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_LIGHT_DARK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of Anglerfish's Light.", new WorldPoint(2766, 2382, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_ANGLERFISHS_LIGHT_COMPLETE, "The answer is 'Barley'.", List.of(ItemID.BARLEY))
		)),
		new ChartingSeaSection(20, "Bay of Elidinis", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a damaged golem east of Menaphos.", new WorldPoint(3243, 2702, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_GOLEM_CORPSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Abalone Cliffs from the north east.", new WorldPoint(3154, 2548, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_CONCH_MOUNTAIN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate south of Menaphos and sample the contents.", new WorldPoint(3239, 2598, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_LIFE_WATER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents to the south west of the Elid Delta.", new WorldPoint(3248, 2703, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_BAY_OF_ELIDINIS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the south east of the Bay of Elidinis.", new WorldPoint(3338, 2622, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_BAY_OF_ELIDINIS_COMPLETE, "The answer is 'Charcoal'.", List.of(ItemID.CHARCOAL))
		)),
		new ChartingSeaSection(21, "Tortugan Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a notable small shell north east of the Great Conch.", new WorldPoint(3284, 2514, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_MINOR_CONCH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a strange apple tree somewhere on the Great Conch.", new WorldPoint(3216, 2466, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CRAB_APPLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the coast of the Great Conch from the north east.", new WorldPoint(3261, 2506, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_TORTUGAN_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents to the east of the eastern pier on the Great Conch.", new WorldPoint(3279, 2463, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_TORTUGAN_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth east of the Great Conch.", new WorldPoint(3282, 2445, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GREAT_CONCH_COMPLETE, "The answer is 'Ashes'.", List.of(ItemID.ASHES))
		)),
		new ChartingSeaSection(22, "Pearl Bank", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a ship in a bottle south of the Little Pearl.", new WorldPoint(3375, 2135, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SHIP_BOTTLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate south west of the Little Pearl and sample the contents.", new WorldPoint(3301, 2146, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_POSSIBLE_ALBUMEN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents to the south west of the Little Pearl.", new WorldPoint(3337, 2179, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_PEARL_BANK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the south of the Pearl Bank.", new WorldPoint(3276, 2130, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PEARL_BANK_COMPLETE, "The answer is 2 woad leaf, 2 onion.", List.of(ItemID.WOADLEAF, ItemID.ONION))
		)),
		new ChartingSeaSection(23, "The Lonely Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some dragon scales on Charred Island.", new WorldPoint(2634, 2392, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_DRAGON_SCALES_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near some shipwrecks in the Lonely Sea and sample the contents.", new WorldPoint(2522, 2248, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_ALONE_AT_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of Charred Island.", new WorldPoint(2624, 2415, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_THE_LONELY_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the far south of the Lonely Sea.", new WorldPoint(2528, 2149, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_THE_LONELY_SEA_COMPLETE, "The answer is 'Swamp weed'.", List.of(ItemID.DORGESH_SWAMP_WEED))
		)),
		new ChartingSeaSection(24, "Fortis Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a nest on Vatrachos Island.", new WorldPoint(1896, 2994, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SEAGULL_NEST_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Fortis Colosseum from the south.", new WorldPoint(1823, 3046, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_FORTIS_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate east of Stonecutter Outpost and sample the contents.", new WorldPoint(1826, 2957, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_ALCO_SOL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north east of Stonecutter Outpost.", new WorldPoint(1779, 2988, 0), "Shrouded Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_FORTIS_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of Vatrachos Island.", new WorldPoint(1898, 3048, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_FORTIS_BAY_COMPLETE, "The answer is 'Stripy feather'.", List.of(ItemID.HUNTING_STRIPY_BIRD_FEATHER))
		)),
		new ChartingSeaSection(25, "Aureum Coast", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Counterweight north west of Deepfin Point.", new WorldPoint(1845, 2824, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_WRECK_WEIGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the south eastern temple in the Avium Savannah from the south.", new WorldPoint(1737, 2907, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_AUREUM_COAST_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of the Aureum Coast and sample the contents.", new WorldPoint(1762, 2854, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_PORTAL_PERRY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of Vatrachos Island.", new WorldPoint(1889, 2882, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_AUREUM_COAST_COMPLETE, "The answer is 1 Equa leaves, 1 Batta tin, 2 Tomato, 1 Cheese, 1 Dwellberries, 1 Onion, 1 Cabbage, 1 Gianne dough.", List.of(ItemID.EQUA_LEAVES, ItemID.BATTA_TIN, ItemID.TOMATO, ItemID.CHEESE, ItemID.DWELLBERRIES, ItemID.ONION, ItemID.CABBAGE, ItemID.GIANNE_DOUGH))
		)),
		new ChartingSeaSection(26, "Wyrm's Waters", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an interesting book on the Shimmering Atoll.", new WorldPoint(1593, 2755, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ATOLL_DICTIONARY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Colossal Wyrm Remains from the south.", new WorldPoint(1634, 2883, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_WYRMS_WATERS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Shimmering Atoll and sample the contents.", new WorldPoint(1570, 2808, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_CONGRATULATION_WINE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents within the Shimmering Atoll.", new WorldPoint(1543, 2785, 0), "Shrouded Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_WYRMS_WATERS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth east of the Shimmering Atoll.", new WorldPoint(1711, 2818, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WYRMS_WATERS_COMPLETE, "The answer is 'Lime'.", List.of(ItemID.LIME))
		)),
		new ChartingSeaSection(31, "The Skullhorde", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Balanced Ballast south west of the Void Knights' Outpost.", new WorldPoint(2587, 2593, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_PEST_SHIPWRECK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of the Skullhorde and sample the contents.", new WorldPoint(2387, 2477, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_FISH_STOUTIER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of the Isle of Bones.", new WorldPoint(2488, 2530, 0), "Shrouded Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_THE_SKULLHORDE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south west of the Isle of Bones.", new WorldPoint(2460, 2449, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_THE_SKULLHORDE_COMPLETE, "The answer is 'Fedora'.", List.of(ItemID.FEDORA))
		)),
		new ChartingSeaSection(36, "Sapphire Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some odd sea life in the middle of the Sapphire Sea.", new WorldPoint(1762, 2580, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SEA_SAPPHIRES_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Crown Jewel from the east.", new WorldPoint(1796, 2664, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_SAPPHIRE_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate where three waters meet in the Sapphire Sea and sample the contents.", new WorldPoint(1675, 2478, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_COMP_KVASS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of the Crown Jewel.", new WorldPoint(1745, 2662, 0), "Shrouded Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_SAPPHIRE_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of the Crown Jewel.", new WorldPoint(1762, 2611, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SAPPHIRE_SEA_COMPLETE, "The answer is 'Vial', 'Coconut milk', 'Toadflax', Yew roots'.", List.of(ItemID.VIAL_EMPTY, ItemID.VIAL_COCONUT_MILK, ItemID.TOADFLAX, ItemID.YEW_ROOTS))
		)),
		new ChartingSeaSection(37, "Western Gate", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find evidence of the Sharhai east of Vatrachos Island.", new WorldPoint(1947, 2985, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SHARHAIS_PURSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some interesting sea life in the middle of the Western Gate.", new WorldPoint(1996, 2947, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SNAKE_EGGS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of the Western Gate and sample the contents.", new WorldPoint(1993, 2972, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_MANGO_GIN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents east of Minotaurs' Rest.", new WorldPoint(1978, 3106, 0), "Shrouded Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_WESTERN_GATE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of the Western Gate.", new WorldPoint(2041, 3023, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WESTERN_GATE_COMPLETE, "The answer is 'Black flowers'.", List.of(ItemID.FLOWERS_WATERFALL_QUEST_BLACK))
		)),
		new ChartingSeaSection(40, "Sunset Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a wrecked shiplike thing south of Villa Lucens.", new WorldPoint(1448, 2896, 0), "Sunset Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_PROP_WRECK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Sunset Coast from the west.", new WorldPoint(1470, 2980, 0), "Sunset Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_SUNSET_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Mistrock and sample the contents.", new WorldPoint(1426, 2865, 0), "Sunset Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_DRUNK_IMPLING_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents at the mouth of the River Varla.", new WorldPoint(1422, 3046, 0), "Sunset Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_SUNSET_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of the Colossal Wyrm Remains.", new WorldPoint(1575, 2942, 0), "Sunset Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SUNSET_BAY_COMPLETE, "The answer is 'Ring mould'.", List.of(ItemID.RING_MOULD))
		)),
		new ChartingSeaSection(41, "Misty Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some discarded fishing equipment in the middle of the Misty Sea.", new WorldPoint(1439, 2716, 0), "Sunset Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_FISHING_NETS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Mistrock from the south.", new WorldPoint(1387, 2840, 0), "Sunset Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_MISTY_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near some shipwrecks in the Misty Sea and sample the contents.", new WorldPoint(1561, 2655, 0), "Sunset Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_MYSTERY_CIDER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the south west of the Misty Sea.", new WorldPoint(1322, 2628, 0), "Sunset Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MISTY_SEA_COMPLETE, "The answer is `Bob's blue shirt`, `Bob's purple shirt`.", List.of(ItemID.TRAIL_BOB_SHIRT_BLUE, ItemID.TRAIL_BOB_SHIRT_PURPLE))
		)),
		new ChartingSeaSection(42, "Dusk's Maw", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a collapsed cave on a small island south west of the Tlati Rainforest.", new WorldPoint(1232, 3005, 0), "Sunset Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CRAB_HOLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the far west of Dusk's Maw and sample the contents.", new WorldPoint(1050, 2886, 0), "Sunset Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SAILING_CAT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south west of the Darkmoon Ravine.", new WorldPoint(1259, 2876, 0), "Sunset Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_DUSKS_MAW_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the Darkmoon Ravine.", new WorldPoint(1314, 2931, 0), "Sunset Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_DUSKS_MAW_COMPLETE, "The answer is 'Tarromin'.", List.of(ItemID.TARROMIN))
		)),
		new ChartingSeaSection(48, "Litus Lucis", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a strange corpse north east of Civitas illa Fortis.", new WorldPoint(1871, 3177, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_KRAKEN_SKELETON_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of Litus Lucis and sample the contents.", new WorldPoint(1783, 3252, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SEA_SHANDY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents east of the Twilight Temple.", new WorldPoint(1721, 3237, 0), "Western Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_LITUS_LUCIS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the Fortis Colosseum.", new WorldPoint(1821, 3166, 0), "Western Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_LITUS_LUCIS_COMPLETE, "The answer is 'Dark bow tie'.", List.of(ItemID.TUXEDO_BOWTIE))
		)),
		new ChartingSeaSection(51, "Crystal Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an elven shipwreck in the middle of the Crystal Sea.", new WorldPoint(1980, 3232, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_ELVEN_SHIP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the visitors book for the Port Roberts jail.", new WorldPoint(1890, 3271, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_JAIL_BOOK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Port Roberts from the south.", new WorldPoint(1886, 3266, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_CRYSTAL_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of the Crystal Sea and sample the contents.", new WorldPoint(2008, 3261, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SNAKE_GRAVY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents east of Port Roberts.", new WorldPoint(1920, 3296, 0), "Western Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_CRYSTAL_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of the Crystal Sea.", new WorldPoint(1979, 3211, 0), "Western Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_CRYSTAL_SEA_COMPLETE, "The answer is 'Butterfly jar'.", List.of(ItemID.BUTTERFLY_JAR))
		)),
		new ChartingSeaSection(54, "Vagabonds Rest", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a crate of discarded goods north of Drumstick Isle. Go backwards with your ship to reach it.", new WorldPoint(2133, 3589, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_HUNTER_OUTFITS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a half-built ship west of Buccaneers' Haven.", new WorldPoint(2026, 3669, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_HALF_BUILT_SHIP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Buccaneers' Haven from the north.", new WorldPoint(2101, 3702, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_VAGABONDS_REST_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Buccaneers' Haven and sample the contents.", new WorldPoint(2079, 3655, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SEA_SPRAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of Buccaneers' Haven.", new WorldPoint(2018, 3705, 0), "Western Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_VAGABONDS_REST_COMPLETE, "The answer is 'Double eye patch'.", List.of(ItemID.DOUBLE_EYE_PATCH))
		)),
		new ChartingSeaSection(55, "Moonshadow", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Lunar Longship south west of Pirates' Cove.", new WorldPoint(2170, 3773, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_LUNAR_WRECK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate south west of Lunar Isle and sample the contents.", new WorldPoint(2030, 3829, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_LUNARSHINE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of Pirates' Cove.", new WorldPoint(2122, 3796, 0), "Western Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_MOONSHADOW_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south west of Lunar Isle.", new WorldPoint(2044, 3792, 0), "Western Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MOONSHADOW_COMPLETE, "The answer is 'Bucket helm (g)'.", List.of(ItemID.BUCKET_HELM_GOLD))
		)),
		new ChartingSeaSection(61, "Lunar Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find evidence of some strong magical spells east of Lunar Isle.", new WorldPoint(2225, 3892, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_MAGIC_WARDS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the pirate galleon docked at Lunar Isle from the east.", new WorldPoint(2160, 3901, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_LUNAR_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Lunar Isle docks and sample the contents. Reverse into the pier to reach it.", new WorldPoint(2142, 3874, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_EXILES_WELCOME_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south of the Astral Altar on Lunar Isle.", new WorldPoint(2159, 3849, 0), "Northern Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_LUNAR_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north east of Pirates' Cove.", new WorldPoint(2221, 3833, 0), "Northern Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_LUNAR_BAY_COMPLETE, "The answer is 'Torstol'.", List.of(ItemID.TORSTOL))
		)),
		new ChartingSeaSection(72, "Bonus Dives", List.of(
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the east of the Hosidian Sea.", new WorldPoint(1969, 3597, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_HOSIDIAN_SEA_COMPLETE, "The answer is 'Potato', 'Potato cactus'.", List.of(ItemID.POTATO, ItemID.CACTUS_POTATO)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the south of Crabclaw Bay.", new WorldPoint(1681, 3380, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_CRABCLAW_BAY_COMPLETE, "The answer is 1 silver ore, 1 chisel, 1 uncut jade, 1 ring mould, 1 cosmic rune, 3 air runes.", List.of(ItemID.SILVER_ORE, ItemID.CHISEL, ItemID.UNCUT_JADE, ItemID.RING_MOULD, ItemID.COSMICRUNE, ItemID.AIRRUNE)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of Port Roberts.", new WorldPoint(1880, 3372, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PILGRIMS_PASSAGE_COMPLETE, "The answer is 'Sandstone (2kg)', 'Sandstone (1kg)', 'Sandstone (10kg)'.", List.of(ItemID.ENAKH_SANDSTONE_SMALL, ItemID.ENAKH_SANDSTONE_TINY, ItemID.ENAKH_SANDSTONE_LARGE)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the Dibber.", new WorldPoint(1851, 3668, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GULF_OF_KOUREND_COMPLETE, "The answer is 2 'Malicious ash'.", List.of(ItemID.MALICIOUS_ASHES)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of Mudskipper Point.", new WorldPoint(2979, 3085, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MUDSKIPPER_SOUND_COMPLETE, "The answer is 'Pie dish', 'Pot of flour', 'Cooking apple'.", List.of(ItemID.PIEDISH, ItemID.POT_FLOUR, ItemID.COOKING_APPLE)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south west of Brimhaven.", new WorldPoint(2757, 3138, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_STRAIT_OF_KHAZARD_COMPLETE, "The answer is 5 'Cabbage seeds'.", List.of(ItemID.CABBAGE_SEED)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south west of the Pandemonium.", new WorldPoint(2988, 2949, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_KHARIDIAN_SEA_COMPLETE, "The answer is 'Willow stock'.", List.of(ItemID.XBOWS_CROSSBOW_STOCK_WILLOW)),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of the entrance to Taverley Dungeon.", new WorldPoint(2873, 3397, 0), "", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_CATHERBY_BAY_COMPLETE, "The answer is 'Iron med helm', 'Bronze chainbody'.", List.of(ItemID.IRON_MED_HELM, ItemID.BRONZE_CHAINBODY))
		)),
		new ChartingSeaSection(11, "Oo'glog Channel", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the corpse of a sea monster north east of Corsair Cove.", new WorldPoint(2634, 2882, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_DEAD_MONSTER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the bananaless boat west of Ape Atoll.", new WorldPoint(2693, 2793, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BANANA_BOAT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the throne in Corsair Cove from the east.", new WorldPoint(2605, 2867, 0), "Ardent Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_COVE_THRONE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate south of Corsair Cove and sample the contents.", new WorldPoint(2595, 2787, 0), "Ardent Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_OOGLUG_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south west of Corsair Cove.", new WorldPoint(2529, 2829, 0), "Ardent Ocean", 40, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_OOGLOG_CHANNEL_COMPLETE, "The answer is 'coal'.", List.of(ItemID.COAL))
		)),
		new ChartingSeaSection(16, "Turtle Belt", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a treasure chest south east of Dognose Island.", new WorldPoint(3075, 2601, 0), "Unquiet Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_TREASURE_CHEST_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the surrounding sea from Dognose Island.", new WorldPoint(3050, 2656, 0), "Unquiet Ocean", 40, VarbitID.SAILING_CHARTING_SPYGLASS_DOGNOSE_ISLAND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Dognose Island and sample the contents.", new WorldPoint(3046, 2665, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_DOGNOSE_DRAUGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents south west of the remote island in the Turtle Belt.", new WorldPoint(2947, 2584, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_TURTLE_BELT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of the Turtle Belt.", new WorldPoint(3014, 2556, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_TURTLE_BELT_COMPLETE, "The answer is 'Papaya fruit'.", List.of(ItemID.PAPAYA))
		)),
		new ChartingSeaSection(28, "Breakbone Strait", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a chest of equipment north of the Myths' Guild. Watch out for Fetid waters!", new WorldPoint(2460, 2881, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_GENERIC_ARMY_ATTIRE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Tear of the Soul and sample the contents. Watch out for Fetid waters!", new WorldPoint(2334, 2781, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_DRINK_CRATE_GOLDLESS_ALE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents off the eastern coast of the Isle of Souls. Watch out for Fetid waters!", new WorldPoint(2340, 2896, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_CURRENT_DUCK_BREAKBONE_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Myths' Guild from the west. Watch out for Fetid waters!", new WorldPoint(2429, 2852, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_SPYGLASS_MYTHS_GUILD_COMPLETE)
		)),
		new ChartingSeaSection(29, "Backwater", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the north eastern coast of the Isle of Souls and sample the contents. Watch out for Fetid waters!", new WorldPoint(2318, 2966, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_DRINK_CRATE_DESTRUCTORS_COCKTAIL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the western coast of the Feldip Hills and sample the contents. Watch out for Fetid waters!", new WorldPoint(2476, 2969, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_DRINK_CRATE_ZOGRES_KISS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some interesting coral south east of the Poison Waste. Watch out for Fetid waters!", new WorldPoint(2300, 3043, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_GENERIC_DISEASED_CORAL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents at the mouth of the River Dougne west of Chompy Marsh. Watch out for Fetid waters!", new WorldPoint(2318, 3057, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_CURRENT_DUCK_BACKWATER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Chompy Marsh from the south. Watch out for Fetid waters!", new WorldPoint(2399, 3027, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_SPYGLASS_TOAD_PONDS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of Jiggig. Watch out for Fetid waters!", new WorldPoint(2469, 3006, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_BACKWATER_COMPLETE, "The answer is 'Grimy kwuarm'.", List.of(ItemID.UNIDENTIFIED_KWUARM))
		)),
		new ChartingSeaSection(30, "Zul-Egil", List.of(
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the ruined tower on the Isle of Souls from the south.", new WorldPoint(2128, 2983, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_RUINED_TOWER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate south of the poison waste and sample the contents. Watch out for Fetid waters!", new WorldPoint(2279, 3036, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_DRINK_CRATE_ZUL_RYE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a toxic hazard in the middle of Zul-Egil. Watch out for Fetid waters!", new WorldPoint(2165, 3030, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_GENERIC_POISON_SPILL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Zul-Andra from the south. Watch out for Fetid waters!", new WorldPoint(2199, 3036, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_SPYGLASS_ZUL_ANDRA_COMPLETE)
		)),
		new ChartingSeaSection(17, "Sea of Shells", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the nest of a fearless bird on the Onyx Crest.", new WorldPoint(2963, 2264, 0), "Unquiet Ocean", 47, VarbitID.SAILING_CHARTING_GENERIC_BIRD_NEST_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Summer Shore from the cliffs above it on the Great Conch.", new WorldPoint(3187, 2430, 0), "Unquiet Ocean", 45, VarbitID.SAILING_CHARTING_SPYGLASS_TORTUGAN_VILLAGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the western coast of the Great Conch and sample the contents.", new WorldPoint(3098, 2458, 0), "Unquiet Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_WAY_HOME_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents near the Summer Shore docks on the Great Conch.", new WorldPoint(3204, 2367, 0), "Unquiet Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_GREAT_CONCH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of the Onyx Crest.", new WorldPoint(2963, 2203, 0), "Unquiet Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SEA_OF_SHELLS_COMPLETE, "The answer is 'Nose peg'.", List.of(ItemID.SLAYER_NOSEPEG))
		)),
		new ChartingSeaSection(27, "Mythic Sea", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate west of the Void Knights' Outpost and sample the contents.", new WorldPoint(2556, 2664, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_MYTHS_MIXER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an abandoned camp on Anglers' Retreat. Watch out for Fetid waters!", new WorldPoint(2476, 2707, 0), "Shrouded Ocean", 51, VarbitID.SAILING_CHARTING_GENERIC_ABANDONED_CAMP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents east of Anglers' Retreat. Watch out for Fetid waters!", new WorldPoint(2495, 2715, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_CURRENT_DUCK_MYTHIC_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of the Myths' Guild. Watch out for Fetid waters!", new WorldPoint(2446, 2784, 0), "Shrouded Ocean", 40, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_MYTHIC_SEA_COMPLETE, "The answer is 'Cabbage', 'Onion', 'Tomato'.", List.of(ItemID.CABBAGE, ItemID.ONION, ItemID.TOMATO))
		)),
		new ChartingSeaSection(33, "Soul Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some interesting sea life in the middle of Soul Bay.", new WorldPoint(2089, 2797, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_LARGE_JELLYFISH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the western coast of the Isle of Souls and sample the contents.", new WorldPoint(2086, 2882, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_CREATORS_COCKTAIL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents off the western coast of the Isle of Souls.", new WorldPoint(2080, 2859, 0), "Shrouded Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_SOUL_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of the Isle of Souls.", new WorldPoint(2012, 2874, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SOUL_BAY_COMPLETE, "The answer is 'Dwellberries'.", List.of(ItemID.DWELLBERRIES)),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on the south western coast of the Isle of Souls.", new WorldPoint(2140, 2806, 0), new WorldPoint(2021, 2681, 0), "Shrouded Ocean", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_SOUL_BAY_COMPLETE)
		)),
		new ChartingSeaSection(34, "Barracuda Belt", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a spool of Barracuda grade rope east of Wintumber Island.", new WorldPoint(2149, 2615, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BARRACUDA_ROPE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an odd statue on Wintumber Island.", new WorldPoint(2066, 2608, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CRAB_STATUE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of a Barracuda ship north east of Sunbleak Island.", new WorldPoint(2281, 2447, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_BARRACUDA_PORTION_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the bank boat in the Barracuda Belt and sample the contents.", new WorldPoint(2282, 2506, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_BARRACUDA_BREW_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the bank boat in the Barracuda Belt.", new WorldPoint(2276, 2598, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_BARRACUDA_HQ_COMPLETE, "The answer is 'Vial', 'Avantoe', 'Snape grass', 'Caviar'.", List.of(ItemID.VIAL_EMPTY, ItemID.AVANTOE, ItemID.SNAPE_GRASS, ItemID.BRUT_CAVIAR)),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south east of Wintumber Island.", new WorldPoint(2082, 2590, 0), new WorldPoint(2154, 2440, 0), "Shrouded Ocean", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_BARRACUDA_BELT_COMPLETE)
		)),
		new ChartingSeaSection(35, "The Everdeep", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an odd shadow south of Deepfin Point.", new WorldPoint(1976, 2680, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SCARY_SHADOW_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Deepfin Point from the south.", new WorldPoint(1943, 2737, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_THE_EVERDEEP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate west of Deepfin Point and sample the contents.", new WorldPoint(1854, 2771, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_CRYSTAL_VODKA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the far south east of the Everdeep.", new WorldPoint(1942, 2650, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_THE_EVERDEEP_COMPLETE, "The answer is 'Plank'.", List.of(ItemID.WOODPLANK)),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on the western coast of Deepfin Point.", new WorldPoint(1928, 2791, 0), new WorldPoint(1895, 2665, 0), "Shrouded Ocean", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_THE_EVERDEEP_COMPLETE)
		)),
		new ChartingSeaSection(56, "Fremensund", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some suspicious eyes west of Rellekka.", new WorldPoint(2587, 3657, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_CRAB_EYES_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the market in Rellekka from the north.", new WorldPoint(2636, 3707, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_FREMENSUND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate north of Rellekka and sample the contents.", new WorldPoint(2689, 3732, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_FISHIER_STOUT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north of Rower's Arm.", new WorldPoint(2561, 3626, 0), "Northern Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_FREMENSUND_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of Waterbirth Island.", new WorldPoint(2518, 3690, 0), "Northern Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_FREMENSUND_COMPLETE, "The answer is 'Kharyll teleport'.", List.of(ItemID.TABLET_KHARYLL)),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south west of Rellekka.", new WorldPoint(2594, 3644, 0), new WorldPoint(2513, 3738, 0), "Northern Ocean", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_FREMENSUND_COMPLETE)
		)),
		new ChartingSeaSection(57, "Grandroot Bay", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the roots of the Grand Tree in Grandroot Bay.", new WorldPoint(2479, 3546, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_TREE_ROOTS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the lighthouse on Rower's Arm from the south.", new WorldPoint(2504, 3612, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_GRANDROOT_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of Grandroot Bay and sample the contents.", new WorldPoint(2453, 3597, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_TOAD_CIDER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents in the River Crannmor.", new WorldPoint(2369, 3507, 0), "Northern Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_GRANDROOT_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of the Barbarian Outpost.", new WorldPoint(2513, 3571, 0), "Northern Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_GRANDROOT_BAY_COMPLETE, "The answer is 'Raw cod'.", List.of(ItemID.RAW_COD)),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather west of the Barbarian Outpost.", new WorldPoint(2498, 3546, 0), new WorldPoint(2439, 3625, 0), "Northern Ocean", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_GRANDROOT_BAY_COMPLETE)
		)),
		new ChartingSeaSection(59, "Fremennik Strait", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a discarded book south of Jatizso.", new WorldPoint(2383, 3769, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_NEDS_BOOK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the towers on the Fremennik Isles from the south.", new WorldPoint(2366, 3777, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_FREMENNIK_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate north west of the Piscatoris Fishing Colony and sample the contents.", new WorldPoint(2273, 3745, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_FISHTONGUE_TONIC_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of Neitiznot.", new WorldPoint(2305, 3795, 0), "Northern Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_FREMENNIK_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on the southern coast of Neitiznot.", new WorldPoint(2341, 3790, 0), new WorldPoint(2426, 3841, 0), "Northern Ocean", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_FREMENNIK_STRAIT_COMPLETE)
		)),
		new ChartingSeaSection(73, "Bonus Weather", List.of(
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather at the south east tip of Cape Conch.", new WorldPoint(3321, 2327, 0), new WorldPoint(3177, 2456, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_GREAT_CONCH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather north east of Rantz's cave.", new WorldPoint(2638, 3009, 0), new WorldPoint(2618, 2879, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_GUTANOTH_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island east of the Kharazi Jungle. Watch out for Stormy seas!", new WorldPoint(2979, 2905, 0), new WorldPoint(3062, 2862, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_STORM_TEMPOR_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north of Civitas illa Fortis.", new WorldPoint(1714, 3193, 0), new WorldPoint(1895, 3178, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_LITUS_LUCIS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north of Laguna Aurorae.", new WorldPoint(1188, 2822, 0), new WorldPoint(1184, 3000, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_DUSKS_MAW_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north of Port Khazard.", new WorldPoint(2656, 3195, 0), new WorldPoint(2742, 3100, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_STRAIT_OF_KHAZARD_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south east of the Tithe Farm.", new WorldPoint(1867, 3458, 0), new WorldPoint(1852, 3532, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_PILGRIMS_PASSAGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south of Crash Island.", new WorldPoint(2903, 2698, 0), new WorldPoint(2869, 2805, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_ARROW_PASSAGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south of Last Light.", new WorldPoint(2864, 2311, 0), new WorldPoint(2789, 2193, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_ANGLERFISHS_LIGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south of the Woodcutting Guild.", new WorldPoint(1651, 3461, 0), new WorldPoint(1579, 3365, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_CRABCLAW_BAY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island west of Rimmington.", new WorldPoint(2912, 3213, 0), new WorldPoint(2840, 3315, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_RIMMINGTON_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on the Isle of Bones.", new WorldPoint(2542, 2541, 0), new WorldPoint(2732, 2641, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_THE_SKULLHORDE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on the eastern coast of the Isle of Souls. Watch out for Fetid waters!", new WorldPoint(2335, 2921, 0), new WorldPoint(2473, 2725, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_BREAKBONE_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather south east of Stonecutter Outpost.", new WorldPoint(1779, 2940, 0), new WorldPoint(1802, 2808, 0), "", 57, VarbitID.SAILING_CHARTING_WEATHER_TROLL_AUREUM_COAST_COMPLETE)
		)),
		new ChartingSeaSection(32, "Sea of Souls", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Soul Searcher south west of the Tear of the Soul.", new WorldPoint(2289, 2712, 0), "Shrouded Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_SOUL_SHIPWRECK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the southern coast of the Isle of Souls and sample the contents.", new WorldPoint(2221, 2765, 0), "Shrouded Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_SOUL_BOTTLE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth east of the shipyard in the Sea of Souls.", new WorldPoint(2118, 2716, 0), "Shrouded Ocean", 38, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SEA_OF_SOULS_COMPLETE, "The answer is 'Common tench'.", List.of(ItemID.AERIAL_FISHING_COMMON_TENCH)),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the surrounding sea from the Tear of the Soul. Watch out for Fetid waters!", new WorldPoint(2342, 2768, 0), "Shrouded Ocean", 61, VarbitID.SAILING_CHARTING_SPYGLASS_CAPE_SOUL_ISLAND_COMPLETE)
		)),
		new ChartingSeaSection(49, "Porth Neigwl", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate off the western coast of Isafdar and sample the contents. Watch out for Crystal-flecked waters!", new WorldPoint(2161, 3203, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_DRINK_CRATE_CRYSTAL_WATER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some lost cargo south east of Lledrith Island. Watch out for Crystal-flecked waters!", new WorldPoint(2118, 3158, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_GENERIC_HALBERD_POLES_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island west of Zul-Andra. Watch out for Crystal-flecked waters!", new WorldPoint(2149, 3068, 0), new WorldPoint(2242, 2986, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_WEATHER_TROLL_ZUL_EGIL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island west of the Tyras Camp. Watch out for Crystal-flecked waters!", new WorldPoint(2118, 3159, 0), new WorldPoint(2028, 3220, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_WEATHER_TROLL_PORTH_NEIGWL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of Lledrith Island. Watch out for Crystal-flecked waters!", new WorldPoint(2071, 3186, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_CURRENT_DUCK_PORTH_NEIGWL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Tyras Camp from the west. Watch out for Crystal-flecked waters!", new WorldPoint(2160, 3155, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_SPYGLASS_TYRAS_CAMP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of Port Tyras. Watch out for Crystal-flecked waters!", new WorldPoint(2113, 3123, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PORTH_NEIGWL_COMPLETE, "The answer is 2 Calquat keg, 1 Ale yeast, 1 Oak roots, 2 barley malt.", List.of(ItemID.CALQUAT_FRUIT_KEG_EMPTY, ItemID.ALE_YEAST, ItemID.OAK_ROOTS, ItemID.BARLEY_MALT))
		)),
		new ChartingSeaSection(50, "Tirannwn Bight", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a deadly trap west of the Iorwerth Camp. Watch out for Crystal-flecked waters!", new WorldPoint(2157, 3274, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_GENERIC_RIVER_MINE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Mynydd and sample the contents. Watch out for Crystal-flecked waters!", new WorldPoint(2120, 3418, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_DRINK_CRATE_ELVEN_WINE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather south west of Mynydd. Watch out for Crystal-flecked waters!", new WorldPoint(2130, 3395, 0), new WorldPoint(2041, 3328, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_WEATHER_TROLL_TIRANNWN_BIGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents at the mouth of the Afon Ganol west of the Iorwerth Camp. Watch out for Crystal-flecked waters!", new WorldPoint(2165, 3260, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_CURRENT_DUCK_TIRANNWN_BIGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Prifddinas ' western gates from the south west. Watch out for Crystal-flecked waters!", new WorldPoint(2163, 3318, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_SPYGLASS_PRIFDDINAS_GATE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of the Tirannwn Bight. Watch out for Crystal-flecked waters!", new WorldPoint(2120, 3345, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_TIRANNWN_BIGHT_COMPLETE, "The answer is 'Soiled page'.", List.of(ItemID.SOILED_PAGE))
		)),
		new ChartingSeaSection(52, "Porth Gwenith", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Ynysdail and sample the contents. Watch out for Crystal-flecked waters!", new WorldPoint(2206, 3484, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_DRINK_CRATE_UNDERGROUND_MILK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an unusual boat north of Gwenith. Watch out for Crystal-flecked waters!", new WorldPoint(2210, 3445, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_GENERIC_CRYSTAL_DINGHY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north west of Eagles' Peak. Watch out for Crystal-flecked waters!", new WorldPoint(2280, 3518, 0), new WorldPoint(2118, 3587, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_WEATHER_TROLL_VAGABONDS_REST_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north of Gwenith. Watch out for Crystal-flecked waters!", new WorldPoint(2220, 3429, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_CURRENT_DUCK_PORTH_GWENITH_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of Gorlah. Watch out for Crystal-flecked waters!", new WorldPoint(2283, 3459, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PORTH_GWENITH_COMPLETE, "The answer is 'Thatch spar dense'.", List.of(ItemID.THATCHING_SPAR_DENSE))
		)),
		new ChartingSeaSection(53, "Piscatoris Sea", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find evidence of kraken activity south west of the Piscatoris Fishing Colony.", new WorldPoint(2300, 3662, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_KRAKEN_SLIME_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the Piscatoris Fishing Colony from the north west.", new WorldPoint(2303, 3700, 0), "Western Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_FISHING_COLONY_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate north west of the Piscatoris Fishing Colony and sample the contents.", new WorldPoint(2248, 3744, 0), "Western Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_MONKFISH_STOUT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south west of Kraken Cove. Watch out for Crystal-flecked waters!", new WorldPoint(2260, 3589, 0), "Western Ocean", 66, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_PISCATORIS_SEA_COMPLETE, "The answer is 'Gold ore'.", List.of(ItemID.GOLD_ORE))
		)),
		new ChartingSeaSection(38, "Rainbow Reef", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a lost friend on Rainbow's End. Watch out for Tangled kelp!", new WorldPoint(2336, 2267, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_GENERIC_GNOME_BALL_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a rowboat to the far south of Rainbow's End. Watch out for Tangled kelp!", new WorldPoint(2334, 2118, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_GENERIC_EDGE_BOAT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Sunbleak Island and sample the contents. Watch out for Tangled kelp!", new WorldPoint(2205, 2303, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_DRINK_CRATE_PLATINUM_RUM_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north of Rainbow's End. Watch out for Tangled kelp!", new WorldPoint(2335, 2286, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_CURRENT_DUCK_RAINBOW_REEF_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of Rainbow Reef. Watch out for Tangled kelp!", new WorldPoint(2250, 2315, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_RAINBOW_REEF_COMPLETE, "The answer is 'Bucket of sap', 'Raw slimy eel'.", List.of(ItemID.ICS_LITTLE_SAP_BUCKET, ItemID.MORT_SLIMEY_EEL)) // TODO
		)),
		new ChartingSeaSection(39, "Southern Expanse", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the Isle of Serpents and sample the contents. Watch out for Tangled kelp!", new WorldPoint(1822, 2439, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_DRINK_CRATE_PUZZLERS_POTEEN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a useful sign to the far south of the Isle of Serpents. Watch out for Tangled kelp!", new WorldPoint(1829, 2139, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_GENERIC_NOTHING_SIGN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small islands [ sic ] east of the Isle of Serpents. Watch out for Tangled kelp!", new WorldPoint(1881, 2445, 0), new WorldPoint(1813, 2191, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_WEATHER_TROLL_SOUTHERN_EXPANSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth west of Sunbleak Island. Watch out for Tangled kelp!", new WorldPoint(2053, 2315, 0), "Shrouded Ocean", 72, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SOUTHERN_EXPANSE_COMPLETE, "The answer is 'Ghrazi rapier'.", List.of(ItemID.GHRAZI_RAPIER))
		)),
		new ChartingSeaSection(58, "V's Belt", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an informative sign on a small island south of Miscellania.", new WorldPoint(2528, 3818, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_GENERIC_GHRIM_SIGN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the castle on Miscellania from the west.", new WorldPoint(2477, 3862, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_VS_BELT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of V's Belt and sample the contents.", new WorldPoint(2474, 3798, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_BLUE_LAGOON_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north east of Jatizso.", new WorldPoint(2428, 3831, 0), "Northern Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_VS_BELT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north west of Miscellania. Watch out for Icy seas!", new WorldPoint(2505, 3892, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_VS_BELT_COMPLETE, "The answer is 'Bronze limbs'.", List.of(ItemID.XBOWS_CROSSBOW_LIMBS_BRONZE))
		)),
		new ChartingSeaSection(60, "Idestia Strait", List.of(
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the castle on Etceteria from the east.", new WorldPoint(2636, 3874, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_IDESTIA_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate south east of Etceteria and sample the contents.", new WorldPoint(2651, 3811, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_ENDLESS_NIGHT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of the hunter area near Trollweiss Mountain.", new WorldPoint(2695, 3828, 0), "Northern Ocean", 22, VarbitID.SAILING_CHARTING_CURRENT_DUCK_IDESTIA_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a hunter free haven on a small island west of Trollweiss Mountain. Watch out for Icy seas!", new WorldPoint(2726, 3855, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_KEBBIT_BURROW_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north of Etceteria. Watch out for Icy seas!", new WorldPoint(2600, 3908, 0), new WorldPoint(2625, 3795, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_WEATHER_TROLL_IDESTIA_STRAIT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth south of the Iceberg. Watch out for Icy seas!", new WorldPoint(2664, 3954, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_IDESTIA_STRAIT_COMPLETE, "The answer is 'Onion'.", List.of(ItemID.ONION)) // TODO
		)),
		new ChartingSeaSection(62, "Winter's Edge", List.of(
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the town on Lunar Isle from the west.", new WorldPoint(2051, 3913, 0), "Northern Ocean", 1, VarbitID.SAILING_CHARTING_SPYGLASS_WINTERS_EDGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near Brittle Isle and sample the contents. Watch out for Icy seas! You will take major damage.", new WorldPoint(1898, 4069, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_SOUL_JUICE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find an odd dead plant on a small island north west of Lunar Isle. Watch out for Icy seas!", new WorldPoint(2083, 3953, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_DEAD_LIVID_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north of the River of Souls. Watch out for Icy seas!", new WorldPoint(1817, 3965, 0), new WorldPoint(1869, 3829, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_WEATHER_TROLL_WINTERS_EDGE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north east of the River of Souls. Watch out for Icy seas!", new WorldPoint(1866, 3911, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_CURRENT_DUCK_WINTERS_EDGE_COMPLETE), // goal: 2054, 3889, 0
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the middle of Winter's Edge. Watch out for Icy seas!", new WorldPoint(1970, 3966, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WINTERS_EDGE_COMPLETE, "The answer is 'needle'.", List.of(ItemID.NEEDLE))
		)),
		new ChartingSeaSection(63, "Lunar Sea", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the far north of the Lunar Sea and sample the contents. Watch out for Icy seas!", new WorldPoint(2113, 4142, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_SUQAH_COLA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some cleaning supplies north of Lunar Isle. Watch out for Icy seas!", new WorldPoint(2144, 4067, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_LUNAR_BROOMS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Ungael from the west. Watch out for Icy seas!", new WorldPoint(2237, 4064, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_LUNAR_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the north west of the Lunar Sea. Watch out for Icy seas!", new WorldPoint(2061, 4118, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_LUNAR_SEA_COMPLETE, "The answer is 'Clockwork'.", List.of(ItemID.POH_CLOCKWORK_MECHANISM))
		)),
		new ChartingSeaSection(64, "Everwinter Sea", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the far north of the Everwinter Sea and sample the contents. Watch out for Icy seas!", new WorldPoint(1757, 4150, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_WINTER_SUN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some odd eggs north east of the Fishing Hamlet. Watch out for Icy seas! You will be sent to the desert when you drink it.", new WorldPoint(1762, 4005, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_TOAD_SPAWN_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on Brittle Isle. Watch out for Icy seas!", new WorldPoint(1959, 4065, 0), new WorldPoint(2041, 4124, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_WEATHER_TROLL_EVERWINTER_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents near the shipwrecks in the Everwinter Sea. Watch out for Icy seas!", new WorldPoint(1792, 4137, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_CURRENT_DUCK_EVERWINTER_SEA_COMPLETE), // end: 1783, 4086, 0
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the area north of the River of Souls from the east. Watch out for Icy seas!", new WorldPoint(1793, 3978, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_RIVER_OF_SOULS_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the River of Souls. Watch out for Icy seas!", new WorldPoint(1823, 4018, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_EVERWINTER_SEA_COMPLETE, "The answer is 'Shield left half'.", List.of(ItemID.DRAGONSHIELD_A))
		)),
		new ChartingSeaSection(65, "Kannski Tides", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the waters of the Fremennik Isles and sample the contents.", new WorldPoint(2359, 3878, 0), "Northern Ocean", 12, VarbitID.SAILING_CHARTING_DRINK_CRATE_BLACK_LOBSTER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find the wreck of the Fearless Fremennik south west of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2426, 3961, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_FEARLESS_FREMENNIK_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north of the western cave on the Fremennik Isles. Watch out for Icy seas!", new WorldPoint(2316, 3903, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_CURRENT_DUCK_KANNSKI_TIDES_COMPLETE), // end: 2277, 4028, 0
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the ice trolls of the Fremennik Isles from the north. Watch out for Icy seas!", new WorldPoint(2351, 3904, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_KANNSKI_TIDES_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the Fremennik Isles. Watch out for Icy seas!", new WorldPoint(2357, 3977, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_KANNSKI_TIDES_COMPLETE,
				"The answer is 1 'Vial of blood', 1 'Cadantine', 1 'Wine of zamorak'.", List.of(ItemID.VIAL_BLOOD, ItemID.CADANTINE, ItemID.WINE_OF_ZAMORAK))
		)),
		new ChartingSeaSection(66, "Weissmere", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the middle of Weissmere and sample the contents. Watch out for Icy seas!", new WorldPoint(2775, 3935, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_DWARVERN_WIZARD_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find some missing mountaineering equipment north of Trollweiss Mountain. Watch out for Icy seas!", new WorldPoint(2783, 3882, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_DISCARDED_SLED_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north west of Weiss. Watch out for Icy seas!", new WorldPoint(2833, 3960, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_CURRENT_DUCK_WEISSMERE_COMPLETE), // end: 2801, 3887, 0
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the large rock formations in Weissmere from their centre. Watch out for Icy seas!", new WorldPoint(2781, 3987, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_WEISSMERE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north west of Weiss. Watch out for Icy seas!", new WorldPoint(2816, 3975, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_WEISSMERE_COMPLETE, "The answer is 'Dragon bitter'.", List.of(ItemID.DRAGON_BITTER))
		)),
		new ChartingSeaSection(67, "Stoneheart Sea", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate in the far north of the Stoneheart Sea and sample the contents. Watch out for Icy seas! Drinking it will send you to the icy hunter area.", new WorldPoint(2612, 4146, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_KGP_MARTINI_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a shark corpse on a small island north west of Etceteria. Watch out for Icy seas!", new WorldPoint(2587, 3919, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_SHARK_CORPSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island south west of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2447, 3995, 0), new WorldPoint(2371, 4058, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_WEATHER_TROLL_STONEHEART_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents west of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2433, 4006, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_CURRENT_DUCK_STONEHEART_SEA_COMPLETE), // 2459, 3996, 0 end
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the KGP training area from the west. Watch out for Icy seas!", new WorldPoint(2620, 4059, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_STONEHEART_SEA_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth north of the Island of Stone. Watch out for Icy seas!", new WorldPoint(2475, 4133, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_STONEHEART_SEA_COMPLETE, "The answer is 'Rain bow'.", List.of(ItemID.RAIN_BOW))
		)),
		new ChartingSeaSection(68, "Shiverwake Expanse", List.of(
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate near the large rock formations in Weissmere and sample the contents. Watch out for Icy seas! You will have your attack, strength, and defence drained and be attacked by 'A corpse (level-103)'.", new WorldPoint(2785, 4046, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_CORPSE_REVIVER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find evidence of spying north east of the Iceberg. Watch out for Icy seas!", new WorldPoint(2709, 4124, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_KGP_PERISCOPE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on the large rock formations in Weissmere. Watch out for Icy seas!", new WorldPoint(2793, 4020, 0), new WorldPoint(2784, 4108, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_WEATHER_TROLL_SHIVERWAKE_EXPANSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of Grimstone from the west. Watch out for Icy seas!", new WorldPoint(2879, 4078, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_SHIVERWAKE_EXPANSE_COMPLETE),
			new ChartingTaskDefinition(ChartingType.DIVING, "With help from a mermaid guide, document the water depth in the east of the Shiverwake Expanse. Watch out for Icy seas!", new WorldPoint(2824, 4092, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_MERMAID_GUIDE_SHIVERWAKE_EXPANSE_COMPLETE,"The answer is 'Royal crown'.", List.of(ItemID.ROYAL_CROWN))
		)),
		new ChartingSeaSection(69, "Weiss Melt", List.of(
			new ChartingTaskDefinition(ChartingType.GENERIC, "Find a failing boat north east of Weiss. Watch out for Icy seas!", new WorldPoint(2902, 3965, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_GENERIC_ICE_SHIP_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CRATE, "Find a Sealed crate north of Grimstone and sample the contents. Watch out for Icy seas!", new WorldPoint(2912, 4154, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_DRINK_CRATE_WEISS_MELTWATER_COMPLETE),
			new ChartingTaskDefinition(ChartingType.WEATHER, "Help the meteorologist document the local weather on a small island north of the Frozen Waste Plateau. Watch out for Icy seas!", new WorldPoint(2951, 3962, 0), new WorldPoint(2861, 4021, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_WEATHER_TROLL_WEISS_MELT_COMPLETE),
			new ChartingTaskDefinition(ChartingType.CURRENT, "Test the currents north of Weiss. Watch out for Icy seas!", new WorldPoint(2880, 3985, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_CURRENT_DUCK_WEISS_MELT_COMPLETE), // end: 2919, 4058, 0
			new ChartingTaskDefinition(ChartingType.SPYGLASS, "Use your spyglass to get a good view of the agility course in the Wilderness from the north. Watch out for Icy seas!", new WorldPoint(2990, 3971, 0), "Northern Ocean", 78, VarbitID.SAILING_CHARTING_SPYGLASS_WEISS_MELT_COMPLETE,"The answer is ''.", List.of())
		))
	);

	public static List<ChartingSeaSection> getSections()
	{
		return SECTIONS;
	}
}
