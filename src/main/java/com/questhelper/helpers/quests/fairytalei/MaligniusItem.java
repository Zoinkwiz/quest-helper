/*
 * Copyright (c) 2026, pajlada <https://github.com/pajlada>
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

package com.questhelper.helpers.quests.fairytalei;

import com.questhelper.requirements.item.ItemRequirement;
import net.runelite.api.Client;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;

public class MaligniusItem extends ItemRequirement
{
	@Varbit private final int varbitID;

	/// The value as received by the given varbit ID
	private int itemIndex = -1;

	public MaligniusItem(@Varbit int varbitID, String name)
	{
		super(name, -1);

		this.varbitID = varbitID;
	}

	@Override
	public boolean check(Client client)
	{
		if (itemIndex == -1)
		{
			itemIndex = client.getVarbitValue(varbitID);
			rebuild();
		}

		return super.check(client);
	}

	private void rebuild()
	{
		if (itemIndex == -1)
		{
			// Not ready to build requirements yet
			return;
		}

		// TODO: Use buildRequirement on item1Index item2Index and item3Index to build a list of requirements to update our requirements with
		// Might also need to set some "dirty" variable to make sure we force call check again. Probably knownContainerStates?
		// TODO: Items link to https://oldschool.runescape.wiki/w/Fairytale_I_-_Growing_Pains#Blessed_secateurs
		switch (itemIndex)
		{
			case 0:
				this.setName("White berries");
				this.setId(ItemID.WHITE_BERRIES);
				break;

			case 1:
				this.setName("Mort myre pear");
				this.setId(ItemID.MORTMYREPEAR);
				break;

			case 2:
				this.setName("Mort myre stem");
				this.setId(ItemID.MORTMYREBUDDINGSTEM);
				break;

			case 3:
				this.setName("Mort myre fungus");
				this.setId(ItemID.MORTMYREMUSHROOM);
				break;

			case 4:
				this.setName("Nature talisman");
				this.setId(ItemID.NATURE_TALISMAN);
				break;

			case 5:
				this.setName("Avantoe");
				this.setId(ItemID.AVANTOE);
				break;

			case 6:
				this.setName("Irit leaf");
				this.setId(ItemID.IRIT_LEAF);
				break;

			case 7:
				this.setName("Blue dragon scale");
				this.setId(ItemID.BLUE_DRAGON_SCALE);
				break;

			case 8:
				this.setName("Proboscis");
				this.setId(ItemID.MOSQUITO_PROBOSCIS);
				break;

			case 9:
				this.setName("Jangerberries");
				this.setId(ItemID.JANGERBERRIES);
				break;

			case 10:
				this.setName("Potato cactus");
				this.setId(ItemID.CACTUS_POTATO);
				break;

			case 11:
				this.setName("Crushed gem");
				this.setId(ItemID.CRUSHED_GEMSTONE);
				break;

			case 12:
				this.setName("Snapdragon");
				this.setId(ItemID.SNAPDRAGON);
				break;

			case 13:
				this.setName("Supercompost");
				this.setId(ItemID.BUCKET_SUPERCOMPOST);
				break;

			case 14:
				this.setName("Volencia moss");
				this.setId(ItemID.VOLENCIA_MOSS);
				break;

			case 15:
				this.setName("Babydragon bones");
				this.setId(ItemID.BABYDRAGON_BONES);
				break;

			case 16:
				this.setName("Uncut diamond");
				this.setId(ItemID.UNCUT_DIAMOND);
				break;

			case 17:
				this.setName("Raw cave eel");
				this.setId(ItemID.RAW_CAVE_EEL);
				break;

			case 18:
				this.setName("Edible seaweed");
				this.setId(ItemID.EDIBLE_SEAWEED);
				break;

			case 19:
				this.setName("Oyster (unopened)");
				this.setId(ItemID.OYSTERSHELL);
				break;

			case 20:
				this.setName("Charcoal");
				this.setId(ItemID.CHARCOAL);
				break;

			case 21:
				this.setName("Red vine worm");
				this.setId(ItemID.RED_VINE_WORM);
				break;

			case 22:
				this.setName("Fat snail");
				this.setId(ItemID.SNAIL_CORPSE3);
				break;

			case 23:
				this.setName("Red spiders' eggs");
				this.setId(ItemID.RED_SPIDERS_EGGS);
				break;

			case 24:
				this.setName("Raw slimy eel");
				this.setId(ItemID.MORT_SLIMEY_EEL);
				break;

			case 25:
				this.setName("Grapes");
				this.setId(ItemID.GRAPES);
				break;

			case 26:
				this.setName("Uncut ruby");
				this.setId(ItemID.UNCUT_RUBY);
				break;

			case 27:
				this.setName("Jogre bones");
				this.setId(ItemID.TBWT_JOGRE_BONES);
				break;

			case 28:
				this.setName("King worm");
				this.setId(ItemID.KING_WORM);
				break;

			case 29:
				this.setName("Snape grass");
				this.setId(ItemID.SNAPE_GRASS);
				break;

			case 30:
				this.setName("Lime");
				this.setId(ItemID.LIME);
				break;

			default:
				// This should never happen
		}
	}

	public void varbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == varbitID)
		{
			itemIndex = event.getValue();
			rebuild();
		}
	}
}
