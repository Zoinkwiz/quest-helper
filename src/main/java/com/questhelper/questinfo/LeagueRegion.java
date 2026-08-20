/*
 * Copyright (c) 2026, Syrif <https://github.com/syrifgit>
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
package com.questhelper.questinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.gameval.SpriteID;

/**
 * Regions available in OSRS leagues. Area codes match param 1017 values from the game cache.
 */
@AllArgsConstructor
@Getter
public enum LeagueRegion
{
	MISTHALIN("Misthalin", 1, SpriteID.TrailblazerMapShields._0),
	KARAMJA("Karamja", 2, SpriteID.TrailblazerMapShields._1),
	ASGARNIA("Asgarnia", 3, SpriteID.TrailblazerMapShields._2),
	KANDARIN("Kandarin", 4, SpriteID.TrailblazerMapShields._6),
	MORYTANIA("Morytania", 5, SpriteID.TrailblazerMapShields._4),
	DESERT("Desert", 6, SpriteID.TrailblazerMapShields._3),
	TIRANNWN("Tirannwn", 7, SpriteID.TrailblazerMapShields._8),
	FREMENNIK("Fremennik Province", 8, SpriteID.TrailblazerMapShields._7),
	KOUREND("Kourend & Kebos", 10, SpriteID.League4MapShields01._9),
	WILDERNESS("Wilderness", 11, SpriteID.TrailblazerMapShields._5),
	VARLAMORE("Varlamore", 21, SpriteID.League5MapShields01._10);

	private final String displayName;
	private final int areaCode;
	private final int spriteId;
}
