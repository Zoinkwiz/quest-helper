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
	private static List<Integer> axes = new ArrayList<>(Arrays.asList(
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
	private static List<Integer> pickaxes = new ArrayList<>(Arrays.asList(
			ItemID.BRONZE_PICKAXE,
			ItemID.IRON_PICKAXE,
			ItemID.STEEL_PICKAXE,
			ItemID.BLACK_PICKAXE,
			ItemID.MITHRIL_PICKAXE,
			ItemID.ADAMANT_PICKAXE,
			ItemID.GILDED_PICKAXE,
			ItemID.RUNE_PICKAXE,
			ItemID.DRAGON_PICKAXE,
			ItemID.INFERNAL_PICKAXE,
			ItemID._3RD_AGE_PICKAXE,
			ItemID.CRYSTAL_PICKAXE)
	);

	@Getter
	private static List<Integer> cats = new ArrayList<>(Arrays.asList(
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
