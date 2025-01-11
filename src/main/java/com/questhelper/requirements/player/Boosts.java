/*
 * Copyright (c) 2022 Obasill <https://github.com/obasill>
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
package com.questhelper.requirements.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Boosts
{
	AGILITY("Agility",5),
	ATTACK("Attack",21),
	CONSTRUCTION("Construction",6),
	COOKING("Cooking",6),
	CRAFTING("Crafting",4),
	DEFENCE("Defence",21),
	FARMING("Farming",3),
	FIREMAKING("Firemaking",1),
	FISHING("Fishing",5),
	FLETCHING("Fletching",4),
	HERBLORE("Herblore",4),
	HITPOINTS("Hitpoints",22),
	HUNTER("Hunter",3),
	MAGIC("Magic",19),
	MINING("Mining",3),
	PRAYER("Prayer",0),
	RANGED("Ranged",19),
	RUNECRAFT("Runecraft",1),
	SLAYER("Slayer",5),
	SMITHING("Smithing",4),
	STRENGTH("Strength",19),
	THIEVING("Thieving",3),
	WOODCUTTING("Woodcutting",3);

	@Getter
	private final String name;

	private final int highestBoost;

	int getHighestBoost(boolean includeSpicyStew, int currentUnboostedLevel) {
		if (includeSpicyStew && highestBoost < 5) {
			return 5;
		}

		else if (name.equals("Thieving"))
		{
			// Players only have access to Summer sq'irk juice at level 65 thieving which is the default boost value for thieving
			if (currentUnboostedLevel < 45)
			{
				// Spring sq'irk
				return 1;
			}

			if (currentUnboostedLevel < 65)
			{
				// Autumn sq'irk
				return 2;
			}

		}

		return highestBoost;
	}
}
