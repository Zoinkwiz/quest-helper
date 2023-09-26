/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
 * Copyright (c) 2017, Seth <Sethtroll3@gmail.com>
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
package com.questhelper.collections;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import static net.runelite.api.ItemID.*;

/**
 * Based off of RuneLite's <a href="https://github.com/runelite/runelite/blob/4700013ffbd089899df9cb675ff53b00d381cae5/runelite-client/src/main/java/net/runelite/client/plugins/itemcharges/ItemWithCharge.java">ItemWithCharge</a> enum
 */
@Getter
public enum ItemWithCharge
{
	BURNING1(BURNING_AMULET1, 1),
	BURNING2(BURNING_AMULET2, 2),
	BURNING3(BURNING_AMULET3, 3),
	BURNING4(BURNING_AMULET4, 4),
	BURNING5(BURNING_AMULET5, 5),
	CBRACE1(COMBAT_BRACELET1, 1),
	CBRACE2(COMBAT_BRACELET2, 2),
	CBRACE3(COMBAT_BRACELET3, 3),
	CBRACE4(COMBAT_BRACELET4, 4),
	CBRACE5(COMBAT_BRACELET5, 5),
	CBRACE6(COMBAT_BRACELET6, 6),
	DIGSITE1(DIGSITE_PENDANT_1, 1),
	DIGSITE2(DIGSITE_PENDANT_2, 2),
	DIGSITE3(DIGSITE_PENDANT_3, 3),
	DIGSITE4(DIGSITE_PENDANT_4, 4),
	DIGSITE5(DIGSITE_PENDANT_5, 5),
	ELYRE1(ENCHANTED_LYRE1, 1),
	ELYRE2(ENCHANTED_LYRE2, 2),
	ELYRE3(ENCHANTED_LYRE3, 3),
	ELYRE4(ENCHANTED_LYRE4, 4),
	ELYRE5(ENCHANTED_LYRE5, 5),
	GAMES1(GAMES_NECKLACE1, 1),
	GAMES2(GAMES_NECKLACE2, 2),
	GAMES3(GAMES_NECKLACE3, 3),
	GAMES4(GAMES_NECKLACE4, 4),
	GAMES5(GAMES_NECKLACE5, 5),
	GAMES6(GAMES_NECKLACE6, 6),
	GAMES7(GAMES_NECKLACE7, 7),
	GAMES8(GAMES_NECKLACE8, 8),
	GLORY1(AMULET_OF_GLORY1, 1),
	GLORY2(AMULET_OF_GLORY2, 2),
	GLORY3(AMULET_OF_GLORY3, 3),
	GLORY4(AMULET_OF_GLORY4, 4),
	GLORY5(AMULET_OF_GLORY5, 5),
	GLORY6(AMULET_OF_GLORY6, 6),
	GLORYT1(AMULET_OF_GLORY_T1, 1),
	GLORYT2(AMULET_OF_GLORY_T2, 2),
	GLORYT3(AMULET_OF_GLORY_T3, 3),
	GLORYT4(AMULET_OF_GLORY_T4, 4),
	GLORYT5(AMULET_OF_GLORY_T5, 5),
	GLORYT6(AMULET_OF_GLORY_T6, 6),
	PASSAGE1(NECKLACE_OF_PASSAGE1, 1),
	PASSAGE2(NECKLACE_OF_PASSAGE2, 2),
	PASSAGE3(NECKLACE_OF_PASSAGE3, 3),
	PASSAGE4(NECKLACE_OF_PASSAGE4, 4),
	PASSAGE5(NECKLACE_OF_PASSAGE5, 5),
	RETURNING1(RING_OF_RETURNING1, 1),
	RETURNING2(RING_OF_RETURNING2, 2),
	RETURNING3(RING_OF_RETURNING3, 3),
	RETURNING4(RING_OF_RETURNING4, 4),
	RETURNING5(RING_OF_RETURNING5, 5),
	ROD1(RING_OF_DUELING1, 1),
	ROD2(RING_OF_DUELING2, 2),
	ROD3(RING_OF_DUELING3, 3),
	ROD4(RING_OF_DUELING4, 4),
	ROD5(RING_OF_DUELING5, 5),
	ROD6(RING_OF_DUELING6, 6),
	ROD7(RING_OF_DUELING7, 7),
	ROD8(RING_OF_DUELING8, 8),
	ROS1(SLAYER_RING_1, 1),
	ROS2(SLAYER_RING_2, 2),
	ROS3(SLAYER_RING_3, 3),
	ROS4(SLAYER_RING_4, 4),
	ROS5(SLAYER_RING_5, 5),
	ROS6(SLAYER_RING_6, 6),
	ROS7(SLAYER_RING_7, 7),
	ROS8(SLAYER_RING_8, 8),
	ROW1(RING_OF_WEALTH_1, 1),
	ROW2(RING_OF_WEALTH_2, 2),
	ROW3(RING_OF_WEALTH_3, 3),
	ROW4(RING_OF_WEALTH_4, 4),
	ROW5(RING_OF_WEALTH_5, 5),
	ROWI1(RING_OF_WEALTH_I1, 1),
	ROWI2(RING_OF_WEALTH_I2, 2),
	ROWI3(RING_OF_WEALTH_I3, 3),
	ROWI4(RING_OF_WEALTH_I4, 4),
	ROWI5(RING_OF_WEALTH_I5, 5),
	SKILLS1(SKILLS_NECKLACE1, 1),
	SKILLS2(SKILLS_NECKLACE2, 2),
	SKILLS3(SKILLS_NECKLACE3, 3),
	SKILLS4(SKILLS_NECKLACE4, 4),
	SKILLS5(SKILLS_NECKLACE5, 5),
	SKILLS6(SKILLS_NECKLACE6, 6),
	TCRYSTAL1(TELEPORT_CRYSTAL_1, 1),
	TCRYSTAL2(TELEPORT_CRYSTAL_2, 2),
	TCRYSTAL3(TELEPORT_CRYSTAL_3, 3),
	TCRYSTAL4(TELEPORT_CRYSTAL_4, 4),
	TCRYSTAL5(TELEPORT_CRYSTAL_5, 5),

	// Infinite charges
	TP_ECTOPHIAL(ECTOPHIAL, 10000000),
	FAIRY_RING_STAFF(ItemCollections.FAIRY_STAFF, 10000000)
	;

	private final List<Integer> ids;
	private final int charges;

	ItemWithCharge(int ids, int charges)
	{
		this.ids = ImmutableList.of(ids);
		this.charges = charges;
	}

	ItemWithCharge(List<Integer> ids, int charges)
	{
		this.ids = ids;
		this.charges = charges;
	}

	ItemWithCharge(ItemCollections ids, int charges)
	{
		this.ids = ids.getItems();
		this.charges = charges;
	}

	private static final Map<Integer, ItemWithCharge> ID_MAP;

	static {
		ID_MAP = Arrays.stream(values())
			.flatMap(item -> item.ids.stream().map(id -> new AbstractMap.SimpleEntry<>(id, item)))
			.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (e1, e2) -> e1));
	}

	@Nullable
	static public ItemWithCharge findItem(int itemId)
	{
		return ID_MAP.get(itemId);
	}

}
