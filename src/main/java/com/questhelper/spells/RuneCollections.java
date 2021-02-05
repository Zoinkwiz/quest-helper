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

import com.questhelper.questhelpers.QuestUtil;
import java.util.List;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import static net.runelite.api.ItemID.*;

@UtilityClass
@Getter
public class RuneCollections
{
	private int item(int itemID, String name)
	{
		return itemID;
	}


	private List<Integer> airRune = QuestUtil.toLinkedList(
		item(AIR_RUNE, "Air Rune"),
		item(MIST_RUNE, "Mist Rune"),
		item(DUST_RUNE, "Dust Rune"),
		item(SMOKE_RUNE, "Smoke Rune"),
		item(STAFF_OF_AIR, "Staff of Air"),
		item(AIR_BATTLESTAFF, "Air Battlestaff"),
		item(MYSTIC_AIR_STAFF, "Mystic Air Staff"),
		item(SMOKE_BATTLESTAFF, "Smoke Battlestaff"),
		item(MYSTIC_SMOKE_STAFF, "Mystic Smoke Staff"),
		item(MIST_BATTLESTAFF, "Mist Battlestaff"),
		item(MYSTIC_MIST_STAFF, "Mystic Mist Staff"),
		item(DUST_BATTLESTAFF, "Dust Battlestaff"),
		item(MYSTIC_DUST_STAFF, "Mystic Dust Staff")
	);

	private List<Integer> waterRune = QuestUtil.toLinkedList(
		item(WATER_RUNE, "Water Rune"),
		item(MIST_RUNE, "Mist Rune"),
		item(MUD_RUNE, "Mud Rune"),
		item(STEAM_RUNE, "Steam Rune"),
		item(STAFF_OF_WATER, "Staff of Water"),
		item(WATER_BATTLESTAFF, "Water Battlestaff"),
		item(MYSTIC_WATER_STAFF, "Mystic Water Staff"),
		item(MUD_BATTLESTAFF, "Mud Battlestaff"),
		item(MYSTIC_MUD_STAFF, "Mystic Mud Staff"),
		item(STEAM_BATTLESTAFF, "Steam Battlestaff"),
		item(MYSTIC_STEAM_STAFF, "Mystic Steam Staff"),
		item(MIST_BATTLESTAFF, "Mist Battlestaff"),
		item(MYSTIC_MIST_STAFF, "Mystic Mist Staff")
	);

	private List<Integer> earthRune = QuestUtil.toLinkedList();

	private List<Integer> fireRune = QuestUtil.toLinkedList();

	private List<Integer> mistRune = QuestUtil.toLinkedList();
	private List<Integer> dustRune = QuestUtil.toLinkedList();
	private List<Integer> mudRune = QuestUtil.toLinkedList();
	private List<Integer> smokeRune = QuestUtil.toLinkedList();
	private List<Integer> steamRune = QuestUtil.toLinkedList();
	private List<Integer> lavaRune = QuestUtil.toLinkedList();
}
