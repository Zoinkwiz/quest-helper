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
import com.questhelper.requirements.util.InventorySlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.spells.Rune;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;

@Getter
public class RuneRequirement extends ItemRequirements implements BankItemHolder
{
	private final Rune rune;
	private ItemRequirements runeRequirements;
	private int costPerCast;
	private int requiredAmount;
	public RuneRequirement(Rune rune, int costPerCast)
	{
		this(rune, costPerCast, 1);
	}

	public RuneRequirement(Rune rune, int costPerCast, int numberOfCasts)
	{
		super(LogicType.OR, rune.getRuneName(), rune.getRunes(costPerCast * numberOfCasts).getItemRequirements());
		this.rune = rune;
		this.costPerCast = costPerCast;
		this.requiredAmount = costPerCast * numberOfCasts;
		setNumberOfCasts(numberOfCasts);
	}

	public void setNumberOfCasts(int numberOfCasts)
	{
		this.requiredAmount = costPerCast * numberOfCasts;
		updateRequirements(numberOfCasts);
	}

	private void updateRequirements(int numberOfCasts)
	{
		this.requiredAmount = costPerCast * numberOfCasts;
		runeRequirements = null;
		this.runeRequirements = rune.getRunes(this.requiredAmount);
		getItemRequirements().clear();
		getItemRequirements().addAll(runeRequirements.getItemRequirements());
	}

	@Override
	public List<Integer> getAllIds()
	{
		return runeRequirements == null ? new ArrayList<>(rune.getItemID()) : runeRequirements.getAllIds();
	}

	@Override
	public List<ItemRequirement> getRequirements(Client client, boolean checkConsideringSlotRestrictions, Item[] bankItems)
	{
		List<ItemRequirement> requirements = new LinkedList<>();
		List<Integer> runes = rune.getRunes();
		int requiredAmount = this.requiredAmount;
		if (clientHasRequiredItems(client, i -> runes.contains(i.getId()) && i.getQuantity() >= requiredAmount))
		{
			return Collections.singletonList(new ItemRequirement(rune.getRuneName(), runes, requiredAmount));
		}
		List<Integer> staves = rune.getStaves();
		if (staves == null)
		{
			return requirements;
		}
		if (clientHasRequiredItems(client, i -> staves.contains(i.getId())))
		{
			return Collections.singletonList(new ItemRequirement(rune.getRuneName(), staves, 1));
		}
		return requirements;
	}

	private boolean clientHasRequiredItems(Client client, Predicate<Item> predicate)
	{
		return InventorySlots.INVENTORY_SLOTS.contains(client, predicate) || InventorySlots.BANK.contains(client, predicate);
	}
}
