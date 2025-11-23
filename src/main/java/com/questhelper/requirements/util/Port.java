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
	PORT_SARIM(0, "Port Sarim", ObjectID.SAILING_DOCKING_BUOY_PORT_SARIM, new WorldPoint(3048, 3186, 0), new Zone(new WorldPoint(3045, 3183, 0), new WorldPoint(3061, 3208, 0)), new WorldPoint(3051, 3193, 0), new WorldPoint(3028, 3194, 0), ObjectID.PORT_TASK_BOARD_PORT_SARIM, new WorldPoint(3030, 3197, 0))
	, PANDEMONIUM(1, "the Pandemonium", ObjectID.SAILING_DOCKING_BUOY_THE_PANDEMONIUM, new WorldPoint(3069, 2981, 0), new Zone(new WorldPoint(3065, 2974, 0), new WorldPoint(3084, 2998, 0)), new WorldPoint(3070, 2987, 0), new WorldPoint(3061,2985, 0), ObjectID.PORT_TASK_BOARD_PANDEMONIUM, new WorldPoint(3058, 2958, 0))
//	, LANDS_END(2, "Land's End", ObjectID.SAILING_DOCKING_BUOY_LANDS_END, null, null, null, null, ObjectID.PORT_TASK_BOARD_LANDS_END, null)
	, MUSA_POINT(3, "Musa Point", ObjectID.SAILING_DOCKING_BUOY_MUSA_POINT, new WorldPoint(2961, 3152, 0), new Zone(new WorldPoint(2974, 3156, 0), new WorldPoint(2956, 3144, 0)), new WorldPoint(2961, 3146, 0), new WorldPoint(2952, 3150, 0), ObjectID.PORT_TASK_BOARD_MUSA_POINT, new WorldPoint(2956, 3144, 0))
//	, HOSIDIUS(4, "Hosidius", ObjectID.SAILING_DOCKING_BUOY_HOSIDIUS, null, null, null, null, -1, null)
//	, PORT_PISCARILIUS(-1, "Port Piscarilius", ObjectID.SAILING_DOCKING_BUOY_PORT_PISCARILIUS, null, null, null, null, ObjectID.PORT_TASK_BOARD_PORT_PISCARILIUS, null)
//	, RIMMINGTON(-1, "Rimmington", ObjectID.SAILING_DOCKING_BUOY_RIMMINGTON, null, null, null, null, -1, null)
//	, CATHERBY(-1, "Catherby", ObjectID.SAILING_DOCKING_BUOY_CATHERBY, null, null, null, null, ObjectID.PORT_TASK_BOARD_CATHERBY, null)
//	, BRIMHAVEN(-1, "Brimhaven", ObjectID.SAILING_DOCKING_BUOY_BRIMHAVEN, null, null, null, null, ObjectID.PORT_TASK_BOARD_BRIMHAVEN, null)
//	, ARDOUGNE(-1, "Ardougne", ObjectID.SAILING_DOCKING_BUOY_ARDOUGNE, null, null, null, null, ObjectID.PORT_TASK_BOARD_ARDOUGNE, null)
//	, PORT_KHAZARD(-1, "Port Khazard", ObjectID.SAILING_DOCKING_BUOY_PORT_KHAZARD, null, null, null, null, ObjectID.PORT_TASK_BOARD_PORT_KHAZARD, null)
//	, WITCHAVEN(-1, "Witchaven", ObjectID.SAILING_DOCKING_BUOY_WITCHAVEN, null, null, null, null, -1, null)
//	, ENTRANA(-1, "Entrana", ObjectID.SAILING_DOCKING_BUOY_ENTRANA, null, null, null, null, -1, null)
//	, CIVITAS_ILLA_FORTIS(-1, "Civitas illa Fortis", ObjectID.SAILING_DOCKING_BUOY_CIVITAS_ILLA_FORTIS, null, null, null, null, ObjectID.PORT_TASK_BOARD_CIVITAS_ILLA_FORTIS, null)
//	, CORSAIR_COVE(-1, "Corsair Cove", ObjectID.SAILING_DOCKING_BUOY_CORSAIR_COVE, null, null, null, null, ObjectID.PORT_TASK_BOARD_CORSAIR_COVE, null)
//	, CAIRN_ISLE(-1, "Cairn Isle", ObjectID.SAILING_DOCKING_BUOY_CAIRN_ISLE, null, null, null, null, -1, null)
//	, THE_SUMMER_SHORE(-1, "the Summer Shore", ObjectID.SAILING_DOCKING_BUOY_THE_SUMMER_SHORE, null, null, null, null, ObjectID.PORT_TASK_BOARD_THE_SUMMER_SHORE, null)
//	, ALDARIN(-1, "Aldarin", ObjectID.SAILING_DOCKING_BUOY_ALDARIN, null, null, null, null, ObjectID.PORT_TASK_BOARD_ALDARIN, null)
//	, RUINS_OF_UNKAH(-1, "Ruins of Unkah", ObjectID.SAILING_DOCKING_BUOY_RUINS_OF_UNKAH, null, null, null, null, ObjectID.PORT_TASK_BOARD_RUINS_OF_UNKAH, null)
//	, VOID_KNIGHTS_OUTPOST(-1, "Void Knights Outpost", ObjectID.SAILING_DOCKING_BUOY_VOID_KNIGHTS_OUTPOST, null, null, null, null, ObjectID.PORT_TASK_BOARD_VOID_KNIGHTS_OUTPOST, null)
//	, PORT_ROBERTS(-1, "Port Roberts", ObjectID.SAILING_DOCKING_BUOY_PORT_ROBERTS, null, null, null, null, ObjectID.PORT_TASK_BOARD_PORT_ROBERTS, null)
//	, RELLEKKA(-1, "Rellekka", ObjectID.SAILING_DOCKING_BUOY_RELLEKKA, null, null, null, null, ObjectID.PORT_TASK_BOARD_RELLEKKA, null)
//	, ETCETERIA(-1, "Etceteria", ObjectID.SAILING_DOCKING_BUOY_ETCETERIA, null, null, null, null, ObjectID.PORT_TASK_BOARD_ETCETERIA, null)
//	, PORT_TYRAS(-1, "Port Tyras", ObjectID.SAILING_DOCKING_BUOY_PORT_TYRAS, null, null, null, null, ObjectID.PORT_TASK_BOARD_PORT_TYRAS, null)
//	, DEEPFIN_POINT(-1, "Deepfin Point", ObjectID.SAILING_DOCKING_BUOY_DEEPFIN_POINT, null, null, null, null, ObjectID.PORT_TASK_BOARD_DEEPFIN_POINT, null)
//	, JATIZSO(-1, "Jatizso", ObjectID.SAILING_DOCKING_BUOY_JATIZSO, null, null, null, null, -1, null)
//	, NEITIZNOT(-1, "Neitiznot", ObjectID.SAILING_DOCKING_BUOY_NEITIZNOT, null, null, null, null, -1, null)
//	, PRIFDDINAS(-1, "Prifddinas", ObjectID.SAILING_DOCKING_BUOY_PRIFDDINAS, null, null, null, null, ObjectID.PORT_TASK_BOARD_PRIFDDINAS, null)
//	, PISCATORIS(-1, "Piscatoris", ObjectID.SAILING_DOCKING_BUOY_PISCATORIS, null, null, null, null, -1, null)
//	, LUNAR_ISLE(-1, "Lunar Isle", ObjectID.SAILING_DOCKING_BUOY_LUNAR_ISLE, null, null, null, null, ObjectID.PORT_TASK_BOARD_LUNAR_ISLE, null)
	;


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