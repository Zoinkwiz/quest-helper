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
package com.questhelper.requirements;

import com.questhelper.spells.MagicSpell;
import com.questhelper.spells.Rune;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;

public class SpellRequirement extends ItemRequirement
{
	@Getter
	private final MagicSpell spell;

	private boolean hasTabletItem = false;
	private int numberOfCasts = 1;
	private ItemRequirement tabletRequirement;

	private final List<Requirement> requirements;
	private final List<Requirement> runeRequirements = new ArrayList<>();
	private final Map<Rune, Integer> runesPerCastMap;
	public SpellRequirement(MagicSpell spell, Map<Rune, Integer> runesPerCastMap, List<Requirement> requirements)
	{
		super(spell.getName(), -1);
		this.spell = spell;
		this.requirements = new LinkedList<>(requirements); // make it mutable for now
		this.requirements.add(new SpellbookRequirement(spell.getSpellbook()));
		this.runesPerCastMap = runesPerCastMap;
		registerItemRequirements(this.requirements);
		setupRuneRequirements(1);
	}

	public void setNumberOfCasts(int numberOfCasts)
	{
		this.numberOfCasts = numberOfCasts;
		setupRuneRequirements(this.numberOfCasts);
	}

	public void setTablet(int itemID)
	{
		this.tabletRequirement = new ItemRequirement("", itemID);
	}

	@Override
	public void setDisplayItemId(Integer displayItemId)
	{
		// Don't set so we always use the requirement ids for bank filters
	}

	private void registerItemRequirements(List<Requirement> requirements)
	{
		getItemRequirements(requirements).forEach(item -> addAlternates(item.getAllIds()));
	}

	private void setupRuneRequirements(int numberOfCasts)
	{
		runeRequirements.clear();
		for (Map.Entry<Rune, Integer> entry : runesPerCastMap.entrySet())
		{
			Rune rune = entry.getKey();
			int runesPerCast = entry.getValue();
			runeRequirements.add(rune.getRunes(runesPerCast * numberOfCasts));
		}
	}

	private List<ItemRequirement> getItemRequirements(List<Requirement> requirements)
	{
		return requirements.stream()
			.filter(ItemRequirement.class::isInstance)
			.map(ItemRequirement.class::cast)
			.collect(Collectors.toList());
	}

	private List<Integer> buildAllIds(List<Requirement> requirements)
	{
		List<ItemRequirement> itemRequirements = getItemRequirements(requirements);

		return itemRequirements.stream()
			.filter(Objects::nonNull)
			.map(ItemRequirement::getAllIds)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return hasTabletItem ? tabletRequirement.getDisplayText() : spell.getName();
	}

	@Override
	public boolean isActualItem()
	{
		return hasTabletItem;
	}

	@Override
	public boolean showQuantity()
	{
		return true;
	}

	@Override
	public Color getColor(Client client)
	{
		return check(client) ? Color.GREEN : Color.RED;
	}

	@Override
	public Color getColorConsideringBank(Client client, boolean checkConsideringSlotRestrictions, Item[] bankItems)
	{
		Color color = this.check(client, checkConsideringSlotRestrictions) ? Color.GREEN : Color.RED;
		if (color == Color.RED && bankItems != null)
		{
			if (check(client, false, bankItems))
			{
				color = Color.WHITE;
			}
		}
		return color;
	}

	@Override
	public boolean check(Client client)
	{
		updateInternalRequirements(client);
		return super.check(client);
	}

	@Override
	public boolean check(Client client, boolean checkConsideringSlotRestrictions)
	{
		updateInternalRequirements(client);
		return super.check(client, checkConsideringSlotRestrictions);
	}

	/** This is not for deciding if the player meets this SpellRequirement. This is only for the internal state of this requirement. */
	private void updateInternalRequirements(Client client)
	{
		if (this.tabletRequirement.getName() == null || this.tabletRequirement.getName().isEmpty())
		{
			int tabletID = tabletRequirement.getId();
			this.tabletRequirement = new ItemRequirement(client.getItemDefinition(tabletID).getName(), tabletID);
		}
		if (tabletRequirement != null)
		{
			hasTabletItem = tabletRequirement.check(client);
		}
		List<ItemRequirement> itemRequirements = getItemRequirements(requirements);
		for (ItemRequirement item : itemRequirements)
		{
			if (item.getName() == null || item.getName().isEmpty())
			{
				item.name = client.getItemDefinition(item.getId()).getName();
			}
		}
	}
}
