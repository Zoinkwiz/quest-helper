/*
 *
 *  * Copyright (c) 2025, TTvanWillegen
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
package com.questhelper.requirements.util;

import com.questhelper.requirements.zone.Zone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

@AllArgsConstructor
@Getter
public enum Port
{
	PORT_SARIM(0, "Port Sarim", ObjectID.SAILING_DOCKING_BUOY_PORT_SARIM, new WorldPoint(3048, 3186, 0), new Zone(new WorldPoint(3045, 3183, 0), new WorldPoint(3061, 3208, 0)), new WorldPoint(3051, 3193, 0), new WorldPoint(3028, 3194, 0), ObjectID.PORT_TASK_BOARD_PORT_SARIM, new WorldPoint(3030, 3197, 0)),
	PANDEMONIUM(1, "the Pandemonium", ObjectID.SAILING_DOCKING_BUOY_THE_PANDEMONIUM, new WorldPoint(3069, 2981, 0), new Zone(new WorldPoint(3065, 2974, 0), new WorldPoint(3084, 2998, 0)), new WorldPoint(3070, 2987, 0), new WorldPoint(3061,2985, 0), ObjectID.PORT_TASK_BOARD_PANDEMONIUM, new WorldPoint(3058, 2958, 0)),
	LANDS_END(2, "Land's End", ObjectID.SAILING_DOCKING_BUOY_LANDS_END, new WorldPoint(1515, 3405, 0), new Zone( new WorldPoint(1520, 3408, 0), new WorldPoint(1507, 3393, 0)), new WorldPoint(1507, 3403, 0), new WorldPoint(1506, 3407, 0), ObjectID.PORT_TASK_BOARD_LANDS_END, new WorldPoint(1502, 3407, 0)),
	MUSA_POINT(3, "Musa Point", ObjectID.SAILING_DOCKING_BUOY_MUSA_POINT, new WorldPoint(2961, 3152, 0), new Zone(new WorldPoint(2974, 3156, 0), new WorldPoint(2956, 3144, 0)), new WorldPoint(2961, 3146, 0), new WorldPoint(2952, 3150, 0), ObjectID.PORT_TASK_BOARD_MUSA_POINT, new WorldPoint(2956, 3144, 0)),
	HOSIDIUS(4, "Hosidius", ObjectID.SAILING_DOCKING_BUOY_HOSIDIUS, new WorldPoint(1719, 3450, 0), null, new WorldPoint(1726, 3452, 0), new WorldPoint(1725, 3460, 0), -1, null),
	PORT_PISCARILIUS(-1, "Port Piscarilius", ObjectID.SAILING_DOCKING_BUOY_PORT_PISCARILIUS, new WorldPoint(1840, 3681, 0), new Zone( new WorldPoint(1854, 3686, 0), new WorldPoint(1836, 3674, 0)), new WorldPoint(1845, 3687, 0), new WorldPoint(1837, 3691, 0), ObjectID.PORT_TASK_BOARD_PORT_PISCARILIUS, new WorldPoint(1839, 3691, 0)),
	RIMMINGTON(-1, "Rimmington", ObjectID.SAILING_DOCKING_BUOY_RIMMINGTON, new WorldPoint(2908, 3216, 0), null, new WorldPoint(3906, 3225, 0), null, -1, null),
	CATHERBY(6, "Catherby", ObjectID.SAILING_DOCKING_BUOY_CATHERBY, new WorldPoint(2788, 3409, 0), new Zone(new WorldPoint(2804, 3401, 0), new WorldPoint(2786, 3413, 0)), new WorldPoint(2796, 3412, 0), new WorldPoint(2799, 3413, 0), ObjectID.PORT_TASK_BOARD_CATHERBY, new WorldPoint(2803, 3418, 0)),
	BRIMHAVEN(-1, "Brimhaven", ObjectID.SAILING_DOCKING_BUOY_BRIMHAVEN, new WorldPoint(2755, 3223, 0), new Zone( new WorldPoint(2746, 3219, 0), new WorldPoint(2758, 3239, 0)), new WorldPoint(2758, 3230, 0), new WorldPoint(2769, 3225, 0), ObjectID.PORT_TASK_BOARD_BRIMHAVEN, new WorldPoint(2764, 3227, 0)),
	ARDOUGNE(-1, "Ardougne", ObjectID.SAILING_DOCKING_BUOY_ARDOUGNE, new WorldPoint(2663, 3261, 0), null, new WorldPoint(2671, 3265, 0), new WorldPoint(2674, 3269, 0), ObjectID.PORT_TASK_BOARD_ARDOUGNE, new WorldPoint(2676, 3276, 0)),
	PORT_KHAZARD(-1, "Port Khazard", ObjectID.SAILING_DOCKING_BUOY_PORT_KHAZARD, new WorldPoint(2663, 3261, 0), new Zone( new WorldPoint(2694, 3171, 0), new WorldPoint(2682, 3153, 0)), new WorldPoint(2686, 3162, 0), new WorldPoint(2684, 3164, 0), ObjectID.PORT_TASK_BOARD_PORT_KHAZARD, new WorldPoint(2678, 3162, 0)),
	WITCHAVEN(-1, "Witchaven", ObjectID.SAILING_DOCKING_BUOY_WITCHAVEN, new WorldPoint(2746, 3297, 0), new Zone( new WorldPoint(2759, 3312, 0), new WorldPoint(2741, 3294, 0)), new WorldPoint(2747, 3305, 0), null, -1, null),
	ENTRANA(-1, "Entrana", ObjectID.SAILING_DOCKING_BUOY_ENTRANA, new WorldPoint(2880, 3330, 0), new Zone( new WorldPoint(2874, 3322, 0), new WorldPoint(2892, 3340, 0)), new WorldPoint(2879, 3336, 0), new WorldPoint(2874, 3339, 0), -1, null),
	CIVITAS_ILLA_FORTIS(-1, "Civitas illa Fortis", ObjectID.SAILING_DOCKING_BUOY_CIVITAS_ILLA_FORTIS, new WorldPoint(1768, 3147, 0), new Zone( new WorldPoint(1764, 3129, 0), new WorldPoint(1774, 3149, 0)), new WorldPoint(1775, 3142, 0), new WorldPoint(1781, 3147, 0), ObjectID.PORT_TASK_BOARD_CIVITAS_ILLA_FORTIS, new WorldPoint(1782, 3142, 0)),
	CORSAIR_COVE(-1, "Corsair Cove", ObjectID.SAILING_DOCKING_BUOY_CORSAIR_COVE, new WorldPoint(2585, 3847, 0), new Zone( new WorldPoint(2581, 2834, 0), new WorldPoint(2593, 2847, 0)), new WorldPoint(2580, 2844, 0), new WorldPoint(2581, 2848, 0), ObjectID.PORT_TASK_BOARD_CORSAIR_COVE, new WorldPoint(2579, 2853, 0)),
	CAIRN_ISLE(-1, "Cairn Isle", ObjectID.SAILING_DOCKING_BUOY_CAIRN_ISLE, new WorldPoint(2748, 2944, 0), null, new WorldPoint(2750, 2952, 0), new WorldPoint(2756, 2949, 0), -1, null),
	SUNSET_COAST(-1, "the Sunset Coast", ObjectID.SAILING_DOCKING_BUOY_SUNSET_COAST, new WorldPoint(1505, 2977, 0), new Zone( new WorldPoint(1500, 2958, 0), new WorldPoint(1511, 2979, 0)), new WorldPoint(1512, 2974, 0), new WorldPoint(1515, 2977, 0), -1, null),
	THE_SUMMER_SHORE(-1, "the Summer Shore", ObjectID.SAILING_DOCKING_BUOY_THE_SUMMER_SHORE, new WorldPoint(3168, 2364, 0), new Zone( new WorldPoint(3200, 2353, 0), new WorldPoint(3160, 2372, 0)), new WorldPoint(3174, 2367, 0), new WorldPoint(3173, 2370, 0), ObjectID.PORT_TASK_BOARD_THE_SUMMER_SHORE, new WorldPoint(3183, 2368, 0)),
	ALDARIN(-1, "Aldarin", ObjectID.SAILING_DOCKING_BUOY_ALDARIN, new WorldPoint(1457, 2975, 0), new Zone( new WorldPoint(1444, 2970, 0), new WorldPoint(1461, 2984, 0)), new WorldPoint(1452, 2970, 0), new WorldPoint(1449, 2969, 0), ObjectID.PORT_TASK_BOARD_ALDARIN, new WorldPoint(1438, 2939, 0)),
	RUINS_OF_UNKAH(-1, "Ruins of Unkah", ObjectID.SAILING_DOCKING_BUOY_RUINS_OF_UNKAH, new WorldPoint(3144, 2818, 0), new Zone( new WorldPoint(3136, 2813, 0), new WorldPoint(3146, 2831, 0)), new WorldPoint(3144, 2825, 0), new WorldPoint(3148, 2826, 0), ObjectID.PORT_TASK_BOARD_RUINS_OF_UNKAH, new WorldPoint(3146, 2828, 0)),
	VOID_KNIGHTS_OUTPOST(-1, "Void Knights Outpost", ObjectID.SAILING_DOCKING_BUOY_VOID_KNIGHTS_OUTPOST, new WorldPoint(2643, 2678, 0), new Zone( new WorldPoint(2641, 2675, 0), new WorldPoint(2659, 2687, 0)), new WorldPoint(2651, 2678, 0), new WorldPoint(2652, 2673, 0), ObjectID.PORT_TASK_BOARD_VOID_KNIGHTS_OUTPOST, new WorldPoint(2659, 2672, 0)),
	PORT_ROBERTS(-1, "Port Roberts", ObjectID.SAILING_DOCKING_BUOY_PORT_ROBERTS, new WorldPoint(1859, 3302, 0), new Zone( new WorldPoint(1850, 3297, 0), new WorldPoint(1862, 3317, 0)), new WorldPoint(1861, 3307, 0), new WorldPoint(1867, 3306, 0), ObjectID.PORT_TASK_BOARD_PORT_ROBERTS, new WorldPoint(1872, 3303, 0)),
	RELLEKKA(-1, "Rellekka", ObjectID.SAILING_DOCKING_BUOY_RELLEKKA, new WorldPoint(2633, 3711, 0), new Zone( new WorldPoint(2624, 3704, 0), new WorldPoint(2636, 3716, 0)), new WorldPoint(2630, 3705, 0), new WorldPoint(2629, 3699, 0), ObjectID.PORT_TASK_BOARD_RELLEKKA, new WorldPoint(2629, 3685, 0)),
	ETCETERIA(-1, "Etceteria", ObjectID.SAILING_DOCKING_BUOY_ETCETERIA, new WorldPoint(2618, 3836, 0), new Zone( new WorldPoint(2607, 3833, 0), new WorldPoint(2619, 3839, 0)), new WorldPoint(2613, 2840, 0), new WorldPoint(2612, 3846, 0), ObjectID.PORT_TASK_BOARD_ETCETERIA, new WorldPoint(2617, 3849, 0)),
	PORT_TYRAS(-1, "Port Tyras", ObjectID.SAILING_DOCKING_BUOY_PORT_TYRAS, new WorldPoint(2149, 3117, 0), new Zone( new WorldPoint(2132, 3109, 0), new WorldPoint(2152, 3120, 0)), new WorldPoint(2144, 3120, 0), new WorldPoint(2151, 3123, 0), ObjectID.PORT_TASK_BOARD_PORT_TYRAS, new WorldPoint(2146, 3123, 0)),
	DEEPFIN_POINT(-1, "Deepfin Point", ObjectID.SAILING_DOCKING_BUOY_DEEPFIN_POINT, new WorldPoint(1930, 2755, 0), new Zone( new WorldPoint(1935, 2743, 0), new WorldPoint(1914, 2759, 0)), new WorldPoint(1923, 2758, 0), new WorldPoint(1928, 2761, 0), ObjectID.PORT_TASK_BOARD_DEEPFIN_POINT, new WorldPoint(1931, 2761, 0)),
	JATIZSO(-1, "Jatizso", ObjectID.SAILING_DOCKING_BUOY_JATIZSO, new WorldPoint(2405, 3776, 0), new Zone( new WorldPoint(2404, 3770, 0), new WorldPoint(2422, 3779, 0)), new WorldPoint(2412, 3780, 0), new WorldPoint(2401, 3788, 0), -1, null),
	NEITIZNOT(-1, "Neitiznot", ObjectID.SAILING_DOCKING_BUOY_NEITIZNOT, new WorldPoint(2304, 3786, 0), new Zone( new WorldPoint(2298, 3771, 0), new WorldPoint(2307, 3789, 0)), new WorldPoint(2309, 3782, 0), new WorldPoint(2308, 3775, 0), -1, null),
	PRIFDDINAS(-1, "Prifddinas", ObjectID.SAILING_DOCKING_BUOY_PRIFDDINAS, new WorldPoint(2165, 3321, 0), new Zone( new WorldPoint(2165, 3307, 0), new WorldPoint(2141, 3325, 0)), new WorldPoint(2158, 3324, 0), new WorldPoint(2170, 3328, 0), ObjectID.PORT_TASK_BOARD_PRIFDDINAS, new WorldPoint(2163, 3326, 0)),
	PISCATORIS(-1, "Piscatoris", ObjectID.SAILING_DOCKING_BUOY_PISCATORIS, new WorldPoint(2304, 3696, 0), new Zone( new WorldPoint(2293, 3682, 0), new WorldPoint(2306, 3700, 0)), new WorldPoint(2304, 3689, 0), new WorldPoint(2313, 3693, 0), -1, null),
	LUNAR_ISLE(-1, "Lunar Isle", ObjectID.SAILING_DOCKING_BUOY_LUNAR_ISLE, new WorldPoint(2154, 3886, 0), new Zone( new WorldPoint(2151, 3875, 0), new WorldPoint(2163, 3887, 0)), new WorldPoint(2152, 3881, 0), new WorldPoint(2146, 3879, 0), ObjectID.PORT_TASK_BOARD_LUNAR_ISLE, new WorldPoint(2139, 3884, 0));;


	private final int id;
	private final String name;
	private final int buoyId;
	private final WorldPoint buoyLocation;
	private final Zone dockZone;
	private final WorldPoint gangplankLocation; // Relevant ObjectIDs: SAILING_GANGPLANK_DISEMBARK, SAILING_GANGPLANK_EMBARK, SAILING_MOORING_DISEMBARK, SAILING_MOORING_EMBARK;
	private final WorldPoint ledgerTableLocation; // Relevant ObjectIDs: DOCK_LOADING_BAY_LEDGER_TABLE_WITHDRAW, DOCK_LOADING_BAY_LEDGER_TABLE_DEPOSIT;
	private final int portTaskBoardId;
	private final WorldPoint portTaskBoardLocation; // Relevant ObjectIDs: DOCK_LOADING_BAY_LEDGER_TABLE_WITHDRAW, DOCK_LOADING_BAY_LEDGER_TABLE_DEPOSIT;

	static Port getPort(int portId) {
		for (Port p : Port.values()) {
			if (p.id == portId) {
				return p;
			}
		}
		return null;
	}
}
//	SAILING_DOCKING_BUOY_ISLE_OF_SOULS = 59801;
//	SAILING_DOCKING_BUOY_WATERBIRTH_ISLAND = 59802;
//	SAILING_DOCKING_BUOY_WEISS = 59803;
//	SAILING_DOCKING_BUOY_DOGNOSE_ISLAND = 59804;
//	SAILING_DOCKING_BUOY_REMOTE_ISLAND = 59805;
//	SAILING_DOCKING_BUOY_THE_LITTLE_PEARL = 59806;
//	SAILING_DOCKING_BUOY_THE_ONYX_CREST = 59807;
//	SAILING_DOCKING_BUOY_LAST_LIGHT = 59808;
//	SAILING_DOCKING_BUOY_CHARRED_ISLAND = 59809;
//	SAILING_DOCKING_BUOY_VATRACHOS_ISLAND = 59810;
//	SAILING_DOCKING_BUOY_ANGLERS_RETREAT = 59811;
//	SAILING_DOCKING_BUOY_MINOTAURS_REST = 59812;
//	SAILING_DOCKING_BUOY_ISLE_OF_BONES = 59813;
//	SAILING_DOCKING_BUOY_TEAR_OF_THE_SOUL = 59814;
//	SAILING_DOCKING_BUOY_WINTUMBER_ISLAND = 59815;
//	SAILING_DOCKING_BUOY_THE_CROWN_JEWEL = 59816;
//	SAILING_DOCKING_BUOY_RAINBOWS_END = 59817;
//	SAILING_DOCKING_BUOY_SUNBLEAK_ISLAND = 59818;
//	SAILING_DOCKING_BUOY_SHIMMERING_ATOLL = 59819;
//	SAILING_DOCKING_BUOY_LAGUNA_AURORAE = 59820;
//	SAILING_DOCKING_BUOY_CHINCHOMPA_ISLAND = 59821;
//	SAILING_DOCKING_BUOY_LLEDRITH_ISLAND = 59822;
//	SAILING_DOCKING_BUOY_YNYSDAIL = 59823;
//	SAILING_DOCKING_BUOY_BUCCANEERS_HAVEN = 59824;
//	SAILING_DOCKING_BUOY_DRUMSTICK_ISLE = 59825;
//	SAILING_DOCKING_BUOY_BRITTLE_ISLE = 59826;
//	SAILING_DOCKING_BUOY_GRIMSTONE = 59827;
