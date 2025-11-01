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
package com.questhelper.tools;

import java.util.Arrays;
import java.util.List;
import net.runelite.api.gameval.ItemID;

public class ItemRequirementsHelper
{
	public static List<Integer> AllBones = Arrays.asList(ItemID.BONES_BURNT, ItemID.BAT_BONES, ItemID.BIG_BONES, ItemID.BABYDRAGON_BONES,
		ItemID.DRAGON_BONES, ItemID.WOLF_BONES, ItemID.TBWT_BEAST_BONES, ItemID.TBWT_JOGRE_BONES, ItemID.TBWT_BURNT_JOGRE_BONES,
		ItemID.MM_SMALL_NINJA_MONKEY_BONES, ItemID.MM_MEDIUM_NINJA_MONKEY_BONES, ItemID.MM_NORMAL_GORILLA_MONKEY_BONES,
		ItemID.MM_BEARDED_GORILLA_MONKEY_BONES, ItemID.MM_NORMAL_MONKEY_BONES, ItemID.MM_SMALL_ZOMBIE_MONKEY_BONES,
		ItemID.MM_LARGE_ZOMBIE_MONKEY_BONES, ItemID.ZOGRE_BONES, ItemID.ZOGRE_ANCESTRAL_BONES_FAYG, ItemID.ZOGRE_ANCESTRAL_BONES_RAURG,
		ItemID.ZOGRE_ANCESTRAL_BONES_OURG, ItemID.DAGANNOTH_KING_BONES, ItemID.WYVERN_BONES, ItemID.LAVA_DRAGON_BONES,
		ItemID.DRAGON_BONES_SUPERIOR, ItemID.WYRM_BONES, ItemID.DRAKE_BONES, ItemID.HYDRA_BONES,
		ItemID.BABYWYRM_BONES, ItemID.ALAN_BONES);

	public static List<Integer> AllRawMeat = Arrays.asList(ItemID.RAW_BEAR_MEAT, ItemID.SPIT_RAW_BEAST_MEAT, ItemID.RAW_BEEF,
		ItemID.SPIT_RAW_BIRD_MEAT, ItemID.RAW_BOAR_MEAT, ItemID.RAW_CHICKEN, ItemID.RAW_OOMLIE, ItemID.RAW_RABBIT,
		ItemID.RAW_RAT_MEAT, ItemID.YAK_MEAT_RAW);

	public static List<Integer> AllCookedMeat = Arrays.asList(ItemID.COOKED_MEAT, ItemID.SPIT_ROASTED_BEAST_MEAT, ItemID.SPIT_ROASTED_BIRD_MEAT,
		ItemID.COOKED_CHICKEN, ItemID.COOKED_OOMLIE, ItemID.COOKED_RABBIT);
}
