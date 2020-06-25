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
}
