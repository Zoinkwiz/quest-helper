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

import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.requirements.util.LogicType;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StandardSpellBuilder
{
	private final MagicSpell spell;
	private final List<ItemRequirement> requirementList = new LinkedList<>();

	private StandardSpellBuilder(MagicSpell spell)
	{
		this.spell = spell;
	}

	/**
	 * Get a new instance of this StandardSpellBuilder
	 *
	 * @param spell the spell to build
	 * @return this
	 */
	public static StandardSpellBuilder builder(MagicSpell spell)
	{
		return new StandardSpellBuilder(spell);
	}

	/**
	 * Add a specified quantity of a given {@link Rune}
	 *
	 * @param quantity the number of Rune(s) required
	 * @param rune the Rune
	 * @return this
	 */
	public StandardSpellBuilder rune(int quantity, Rune rune)
	{
		requirementList.add(rune.getRunes(quantity));
		return this;
	}

	/**
	 * Add a single Rune to this spell
	 *
	 * @param rune the Rune
	 * @return this
	 */
	public StandardSpellBuilder rune(Rune rune)
	{
		return rune(1, rune);
	}

	/**
	 * Add an item this spell needs in order to be cast.
	 *
	 * @param quantity the number of items required
	 * @param itemID the item required
	 * @return this
	 */
	public StandardSpellBuilder item(int quantity, int itemID)
	{
		requirementList.add(new ItemRequirement("", itemID, quantity));
		return this;
	}

	/**
	 * Add a single item required for this spell to be cast
	 *
	 * @param itemID the item required
	 * @return this
	 */
	public StandardSpellBuilder item(int itemID)
	{
		return item(1, itemID);
	}

	/**
	 * Add an item (and it's suitable equivalents) that should be equipped in order to cast this spell
	 *
	 * @param equipped if this item should be equipped
	 * @param itemIDs the items that should be equipped (only one is required to be equipped)
	 * @return this
	 */
	public StandardSpellBuilder item(boolean equipped, Integer... itemIDs)
	{
		requirementList.add(new ItemRequirement("", Arrays.asList(itemIDs), 1, equipped));
		return this;
	}

	/**
	 * @return a new {@link ItemRequirements} containing all this spell information
	 */
	public ItemRequirements build()
	{
		return new ItemRequirements(LogicType.AND, spell.getName(), requirementList);
	}

}
