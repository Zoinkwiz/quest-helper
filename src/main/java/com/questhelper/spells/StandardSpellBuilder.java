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

import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.magic.SpellRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;

public class StandardSpellBuilder
{
	private final MagicSpell spell;
	private final List<Requirement> requirements = new LinkedList<>();
	private final Map<Rune, Integer> runeList = new HashMap<>();
	private int tabletItemID = -1;

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
		runeList.put(rune, quantity);
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
		requirements.add(new ItemRequirement("", itemID, quantity));
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
		requirements.add(new ItemRequirement("", Arrays.asList(itemIDs), 1, equipped));
		return this;
	}

	/**
	 * Set the tablet that can be used to cast this spell.
	 *
	 * @param itemID the tablet item id
	 * @return this
	 */
	public StandardSpellBuilder tablet(int itemID)
	{
		this.tabletItemID = itemID;
		return this;
	}

	/**
	 * Add a {@link QuestHelperQuest} requirement to this spell.
	 * By default, this requires the quest to be finished.<br>
	 * See {@link #quest(QuestHelperQuest, boolean)} for specifying if the quest should only
	 * be started.
	 *
	 * @param quest the quest that should be completed in order to cast this spell
	 * @return this
	 */
	public StandardSpellBuilder quest(QuestHelperQuest quest)
	{
		return quest(quest, false);
	}

	/**
	 * Add a {@link QuestHelperQuest} requirement to this spell.
	 * If {@param started} is true, this will mean the {@link QuestHelperQuest} is only required to be
	 * {@link QuestState#IN_PROGRESS}, not {@link QuestState#FINISHED}.
	 *
	 * @param quest the quest requirement to add
	 * @param started if the quest should be started, or finished
	 * @return this
	 */
	public StandardSpellBuilder quest(QuestHelperQuest quest, boolean started)
	{
		QuestState state = started ? QuestState.IN_PROGRESS : QuestState.FINISHED;
		requirements.add(new QuestRequirement(quest, state));
		return this;
	}

	/**
	 * Add a {@link Varbits} requirement to this spell.
	 *
	 * @param varbit the {@link Varbits} that is required
	 * @param value the varbit value that is required
	 * @return this
	 */
	public StandardSpellBuilder var(Varbits varbit, int value)
	{
		requirements.add(new VarbitRequirement(varbit.getId(), value));
		return this;
	}

	/**
	 * Add a {@link VarPlayer} requirement to this spell
	 *
	 * @param varPlayer the {@link VarPlayer} that is needed
	 * @param value the value that is required
	 * @return
	 */
	public StandardSpellBuilder var(VarPlayer varPlayer, int value)
	{
		requirements.add(new VarplayerRequirement(varPlayer.getId(), value));
		return this;
	}

	/**
	 * Set a varbit that is not defined via the {@link Varbits} or {@link VarPlayer} enum(s).
	 *
	 * @param varbit the varbit to test for
	 * @param value the value it should be
	 * @return this
	 */
	public StandardSpellBuilder varbit(int varbit, int value)
	{
		requirements.add(new VarbitRequirement(varbit, value));
		return this;
	}

	/**
	 * @return a new {@link ItemRequirements} containing all this spell information
	 */
	public SpellRequirement build()
	{
		SpellRequirement requirement = new SpellRequirement(spell, runeList, requirements);
		if (tabletItemID != -1)
		{
			requirement.setTablet(tabletItemID);
		}
		return requirement;
	}

}
