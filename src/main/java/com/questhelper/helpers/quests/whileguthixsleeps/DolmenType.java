/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.quests.whileguthixsleeps;

import com.questhelper.requirements.item.ItemRequirement;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;

public enum DolmenType
{
	AGILITY(ObjectID.STATUE_53640, new ItemRequirement("Toadflax", ItemID.TOADFLAX).highlighted(), new ItemRequirement("Toad's legs", ItemID.TOADS_LEGS).highlighted()),
	ENERGY(ObjectID.STATUE_53644, new ItemRequirement("Harralander", ItemID.HARRALANDER).highlighted(), new ItemRequirement("Chocolate dust", ItemID.CHOCOLATE_DUST).highlighted()),
	RESTORATION(ObjectID.STATUE_53646, new ItemRequirement("Snapdragon", ItemID.SNAPDRAGON).highlighted(), new ItemRequirement("Red spider's eggs", ItemID.RED_SPIDERS_EGGS).highlighted()),
	ATTACK(ObjectID.STATUE_53648, new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF).highlighted(), new ItemRequirement("Eye of newt", ItemID.EYE_OF_NEWT).highlighted()),
	STRENGTH(ObjectID.STATUE_53650, new ItemRequirement("Tarromin", ItemID.TARROMIN).highlighted(), new ItemRequirement("Limpwurt root", ItemID.LIMPWURT_ROOT).highlighted()),
	DEFENCE(ObjectID.STATUE_53652, new ItemRequirement("Ranarr weed", ItemID.RANARR_WEED).highlighted(), new ItemRequirement("White berries", ItemID.WHITE_BERRIES).highlighted()),
	COMBAT(ObjectID.STATUE_53654, new ItemRequirement("Harralander", ItemID.HARRALANDER).highlighted(), new ItemRequirement("Goat horn dust", ItemID.GOAT_HORN_DUST).highlighted()),
	RANGED(ObjectID.STATUE_53656, new ItemRequirement("Dwarf weed", ItemID.DWARF_WEED).highlighted(), new ItemRequirement("Wine of zamorak", ItemID.WINE_OF_ZAMORAK).highlighted()),
	PRAYER(ObjectID.STATUE_53658, new ItemRequirement("Ranarr weed", ItemID.RANARR_WEED).highlighted(), new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS).highlighted()),
	HUNTER(ObjectID.STATUE_53660, new ItemRequirement("Avantoe", ItemID.AVANTOE).highlighted(), new ItemRequirement("Kebbit teeth dust", ItemID.KEBBIT_TEETH_DUST).highlighted()),
	FISHING(ObjectID.STATUE_53662, new ItemRequirement("Avantoe", ItemID.AVANTOE).highlighted(), new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS).highlighted()),
	MAGIC(ObjectID.STATUE_53664, new ItemRequirement("Lantadyme", ItemID.LANTADYME).highlighted(), new ItemRequirement("Potato cactus", ItemID.POTATO_CACTUS).highlighted()),
	BALANCE(ObjectID.STATUE_53642, new ItemRequirement("Harralander", ItemID.HARRALANDER).highlighted(), new ItemRequirement("Red spider's eggs", ItemID.RED_SPIDERS_EGGS).highlighted(),
		new ItemRequirement("Garlic", ItemID.GARLIC).highlighted(), new ItemRequirement("Silver dust", ItemID.SILVER_DUST).highlighted());

	@Getter
	private final int objectID;

	@Getter
	private final ItemRequirement[] itemRequirements;

	DolmenType(int objectID, ItemRequirement... itemRequirements)
	{
		this.objectID = objectID;
		this.itemRequirements = itemRequirements;
	}
}
