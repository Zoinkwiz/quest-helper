/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.enchantedkey;

import java.awt.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum EnchantedKeyDigLocation
{
	RELLEKKA(new WorldPoint(2715, 3610, 0), "South east of Rellekka.", 0),
	SOUTH_EAST_VARROCK(new WorldPoint(3303, 3345, 0), "South east of Varrock.", 1),
	SOUTH_FALADOR(new WorldPoint(2969, 3300, 0), "South of Falador", 2),
	// True Al Kharid centre is 3294, 3224, 0
	AL_KHARID(new WorldPoint(3295, 3222, 0), "North of Al Kharid", 3),
	LUMBRIDGE_SWAMP(new WorldPoint(3158, 3178, 0), "Lumbridge Swamp.", 4),
	GRAND_EXCHANGE(new WorldPoint(3161, 3490, 0), "In the Grand Exchange.", 5),
	BODY_ALTAR(new WorldPoint(3034, 3437, 0), "Near the Body Altar.", 6),
	OUTPOST(new WorldPoint(2419, 3378, 0), "South west of the Tree Gnome Stronghold.", 7),
	MUDSKIPPER(new WorldPoint(3018, 3162, 0), "North of Mudskipper Point.", 8),
	SOUTH_ARDOUGNE(new WorldPoint(2617, 3243, 0), "South of East Ardougne", 9),
	GNOME_STRONGHOLD(new WorldPoint(2444, 3447, 0), "Centre of the Tree Gnome Stronghold.", 10);


	private final WorldPoint worldPoint;
	private final String area;
	private final int bit;

	public Rectangle getRect()
	{
		final int digRadius = 6;
		return new Rectangle(worldPoint.getX() - digRadius, worldPoint.getY() - digRadius, digRadius * 2 + 1, digRadius * 2 + 1);
	}
}
