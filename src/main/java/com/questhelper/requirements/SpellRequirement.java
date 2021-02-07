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

import com.questhelper.questhelpers.BankItemHolder;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.util.InventorySlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.spells.MagicSpell;
import com.questhelper.spells.Rune;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.Skill;

public class SpellRequirement extends ItemRequirement implements BankItemHolder
{
	@Getter
	private final MagicSpell spell;

	private boolean hasTabletItem = false;
	private int numberOfCasts = 1;
	private ItemRequirement tabletRequirement = null;

	private final List<Requirement> requirements;
	private final List<RuneRequirement> runeRequirements = new ArrayList<>();
	private final Map<Rune, Integer> runesPerCastMap;
	public SpellRequirement(MagicSpell spell, Map<Rune, Integer> runesPerCastMap, List<Requirement> requirements)
	{
		super(spell.getName(), -1);
		this.spell = spell;
		this.requirements = new LinkedList<>(requirements); // make it mutable for now
		this.requirements.add(new SpellbookRequirement(spell.getSpellbook()));
		this.requirements.add(new SkillRequirement(Skill.MAGIC, spell.getRequiredMagicLevel()));
		this.runesPerCastMap = runesPerCastMap;
		registerAlternateItems(this.requirements);
		setNumberOfCasts(1);
		super.setDisplayItemId(-1);
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

	private void setupRuneRequirements(int numberOfCasts)
	{
		runeRequirements.clear();
		for (Map.Entry<Rune, Integer> entry : runesPerCastMap.entrySet())
		{
			Rune rune = entry.getKey();
			int runesPerCast = entry.getValue();
			RuneRequirement runeReq = new RuneRequirement(rune, runesPerCast);
			runeReq.setNumberOfCasts(numberOfCasts);
			runeRequirements.add(runeReq);
			List<Integer> allIds = runeReq.getAllIds();
			alternateItems.addAll(allIds);
		}
	}

	private List<ItemRequirement> getItemRequirements(List<Requirement> requirements)
	{
		return requirements.stream()
			.filter(ItemRequirement.class::isInstance)
			.map(ItemRequirement.class::cast)
			.collect(Collectors.toList());
	}

	private void registerAlternateItems(List<Requirement> requirements)
	{
		alternateItems.clear();
		alternateItems.addAll(buildAllIds(requirements));
	}

	private List<Integer> buildAllIds(List<Requirement> requirements)
	{
		List<ItemRequirement> itemRequirements = getItemRequirements(requirements);

		return itemRequirements.stream()
			.filter(Objects::nonNull)
			.map(ItemRequirement::getAllIds)
			.flatMap(Collection::stream)
			.distinct()
			.collect(QuestUtil.collectToArrayList());
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return spell.getName(); //TODO: Re-evaluate when we show the spell name versus the tablet
	}

	@Override
	public boolean isActualItem()
	{
		return false; // TODO: Redo when/if we determine when to show the tablet
	}

	@Override
	public boolean showQuantity()
	{
		return true;
	}

	@Override
	public Color getColor(Client client)
	{
		return meetsRequirements(client, false, null) ? Color.GREEN : Color.RED;
	}

	@Override
	public Color getColorConsideringBank(Client client, boolean checkConsideringSlotRestrictions, Item[] bankItems)
	{
		Color color = meetsRequirements(client, checkConsideringSlotRestrictions, null) ? Color.GREEN : Color.RED;
		if (color == Color.RED && bankItems != null)
		{
			if (meetsRequirements(client, false, bankItems))
			{
				color = Color.WHITE;
			}
		}
		return color;
	}

	@Override
	public List<Integer> getDisplayItemIds()
	{
		// LinkedList so we can maintain what should get priority for displaying
		List<Integer> ids = new LinkedList<>(buildAllIds(requirements));
		runeRequirements.forEach(req -> alternateItems.addAll(req.getAllIds()));
		if (tabletRequirement != null)
		{
			ids.add(tabletRequirement.getDisplayItemId());
		}
		return ids;
	}

	@Override
	public boolean check(Client client)
	{
		return meetsRequirements(client, false, null);
	}

	@Override
	public boolean check(Client client, boolean checkConsideringSlotRestrictions)
	{
		return meetsRequirements(client, checkConsideringSlotRestrictions, null);
	}

	@Override
	public boolean check(Client client, boolean checkConsideringSlotRestrictions, Item[] items)
	{
		return meetsRequirements(client, checkConsideringSlotRestrictions, items);
	}

	private boolean checkInventorySlot(InventorySlots slot, Client client, ItemRequirement requirement)
	{
		return slot.contains(client, i -> requirement.getAllIds().contains(i.getId()) && i.getQuantity() >= requirement.getQuantity());
	}

	private boolean meetsRequirements(Client client, boolean checkConsideringSlotRestrictions, Item[] items)
	{
		updateInternalRequirements(client);
		boolean itemRequirementsMet = itemRequirementsMet(requirements, client, checkConsideringSlotRestrictions, items);
		boolean nonItemRequirementsMet = nonItemRequirementsMet(requirements, client);
		boolean runeRequirementsMet = hasRunes(runeRequirements, client, checkConsideringSlotRestrictions, items);
		boolean hasTabletItem = hasTabletItem(client);
		return (nonItemRequirementsMet && itemRequirementsMet && runeRequirementsMet) || hasTabletItem;
	}

	private boolean hasRunes(List<RuneRequirement> runes, Client client, boolean checkWithSlotRestrictions, Item[] items)
	{
		return runes.stream().allMatch(req -> req.check(client, checkWithSlotRestrictions, items));
	}

	private boolean itemRequirementsMet(List<Requirement> requirements, Client client, boolean checkConsideringSlotRestrictions, Item[] items)
	{
		return getItemRequirements(requirements).stream().allMatch(req -> req.check(client, checkConsideringSlotRestrictions, items));
	}

	private boolean nonItemRequirementsMet(List<Requirement> requirements, Client client)
	{
		return requirements.stream()
			.filter(((Predicate<Requirement>) ItemRequirement.class::isInstance).negate())
			.allMatch(req -> req.check(client));
	}

	private boolean hasTabletItem(Client client)
	{
		return tabletRequirement != null && tabletRequirement.check(client);
	}

	/** This is not for deciding if the player meets this SpellRequirement. This is only for the internal state of this requirement. */
	private void updateInternalRequirements(Client client)
	{
		if (tabletRequirement != null)
		{
			if (this.tabletRequirement.getName() == null || this.tabletRequirement.getName().isEmpty())
			{
				if (tabletRequirement.getId() > -1)
				{
					ItemComposition tablet = client.getItemDefinition(tabletRequirement.getId());
					this.tabletRequirement = new ItemRequirement(client.getItemDefinition(tablet.getId()).getName(), tablet.getId());
					tabletRequirement.setDisplayItemId(tablet.getId());
				}
				else
				{
					//TODO: throw error?
				}
			}
			hasTabletItem = tabletRequirement.check(client);
		}
		List<ItemRequirement> itemRequirements = getItemRequirements(requirements);
		for (ItemRequirement item : itemRequirements)
		{
			if (item.getName() == null || item.getName().isEmpty())
			{
				String name = client.getItemDefinition(item.getId()).getName();
				item.setName(name);
			}
		}
	}

	@Override
	public List<ItemRequirement> getRequirements(Client client, boolean checkConsideringSlotRestrictions, Item[] bankItems)
	{
		List<ItemRequirement> bankTabItemRequirements = new LinkedList<>();
		if (tabletRequirement != null && tabletRequirement.check(client, checkConsideringSlotRestrictions, bankItems))
		{
			bankTabItemRequirements.add(tabletRequirement);
		}
		else
		{
			List<ItemRequirement> runeItemRequirements = runeRequirements.stream()
				.map(req -> req.getRequirements(client, checkConsideringSlotRestrictions, bankItems))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
			bankTabItemRequirements.addAll(runeItemRequirements);
		}
		List<ItemRequirement> itemRequirements = getItemRequirements(this.requirements).stream()
			.filter(req -> req.check(client, checkConsideringSlotRestrictions, bankItems))
			.collect(Collectors.toList());
		bankTabItemRequirements.addAll(itemRequirements);
		return bankTabItemRequirements;
	}
}
