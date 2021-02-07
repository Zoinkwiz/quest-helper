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
import com.questhelper.spells.Rune;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;

/*
 * LOGIC:
 * We need to be able to get the first rune/staff that was found in the player's inventory/bank so
 * we can display that item as a bank icon.
 *
 * Prioritize runes over staves because most quests require some sort of combat gear and that would,
 * most likely, eliminate stave as an option for most quests.
 */
@Getter
public class RuneRequirement extends ItemRequirement implements BankItemHolder
{
	private final Rune rune;
	private int costPerCast;
	private int requiredAmount;

	private final ItemRequirement runeItemRequirement;
	private ItemRequirement staffItemRequirement;

	public RuneRequirement(Rune rune, int costPerCast)
	{
		this(rune, costPerCast, 1);
	}

	public RuneRequirement(Rune rune, int costPerCast, int numberOfCasts)
	{
		super(rune.getRuneName(), rune.getItemID(), (costPerCast * numberOfCasts));
		this.rune = rune;
		this.costPerCast = costPerCast;
		this.requiredAmount = costPerCast * numberOfCasts;
		this.runeItemRequirement = new ItemRequirement(rune.getRuneName(), rune.getRunes(), this.requiredAmount);
		if (rune.getStaves() != null)
		{
			this.staffItemRequirement = new ItemRequirement(rune.getRuneName(), rune.getStaves(), 1, true);
		}
	}

	public void setNumberOfCasts(int numberOfCasts)
	{
		this.requiredAmount = costPerCast * numberOfCasts;
		updateRequirements(this.requiredAmount);
	}

	private void updateRequirements(int numRunesRequired)
	{
		this.runeItemRequirement.setQuantity(numRunesRequired);
		setQuantity(numRunesRequired);
	}

	@Override
	public boolean isActualItem()
	{
		return true;
	}

	@Override
	public Integer getDisplayItemId()
	{
		return null; // use our requirements to determine the id to display
	}

	@Override
	public void setDisplayItemId(Integer displayItemId)
	{
		// Don't set so we use our requirements to determine what to show in the bank
	}

	@Override
	public List<Integer> getAllIds()
	{
		List<Integer> ids = new LinkedList<>(runeItemRequirement.getAllIds());
		if (staffItemRequirement != null)
		{
			ids.addAll(staffItemRequirement.getAllIds());
		}
		return QuestUtil.removeDuplicates(ids);
	}

	@Override
	public boolean checkBank(Client client)
	{
		List<Integer> ids = runeItemRequirement.getAllIds();
		boolean hasRunes = InventorySlots.BANK.contains(client, i -> ids.contains(i.getId()) && i.getQuantity() >= this.requiredAmount);
		if (hasRunes)
		{
			return true;
		}
		if (staffItemRequirement != null)
		{
			List<Integer> staffIDs = staffItemRequirement.getAllIds();
			return InventorySlots.BANK.contains(client, i -> staffIDs.contains(i.getId()) && i.getQuantity() >= 1);
		}
		return false;
	}

	@Override
	public boolean check(Client client, boolean checkWithSlotRestrictions, Item[] items)
	{
		int id = findFirstItemID(client, runeItemRequirement.getAllIds(), this.requiredAmount, checkWithSlotRestrictions, items);
		if (id >= 0)
		{
			return true;
		}
		if (staffItemRequirement != null)
		{
			return findFirstItemID(client, staffItemRequirement.getAllIds(), 1, checkWithSlotRestrictions, items) >= 0;
		}
		return false;
	}

	private int findFirstItemID(Client client, List<Integer> itemIDList, int requiredAmount, boolean checkWithSlotRestrictions, Item[] items)
	{
		int remainder = requiredAmount;
		for (int id : itemIDList)
		{
			remainder -= (requiredAmount - getRequiredItemDifference(client, id, checkWithSlotRestrictions, items));
			if (remainder <= 0)
			{
				return id;
			}
		}
		return -1;
	}

	@Override
	public List<ItemRequirement> getRequirements(Client client, boolean checkWithSlotRestrictions, Item[] bankItems)
	{
		if (hasItem(client, runeItemRequirement.getAllIds(), this.requiredAmount, checkWithSlotRestrictions, bankItems))
		{
			return Collections.singletonList(runeItemRequirement);
		}
		if (staffItemRequirement != null && hasItem(client, staffItemRequirement.getAllIds(), 1, checkWithSlotRestrictions, bankItems))
		{
			return Collections.singletonList(staffItemRequirement);
		}
		return Collections.emptyList();
	}

	private boolean hasItem(Client client, List<Integer> ids, int amount, boolean checkWithSlotRestrictions, Item[] items)
	{
		return findFirstItemID(client, ids, amount, checkWithSlotRestrictions, items) >= 0;
	}
}
