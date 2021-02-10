/*
 *
 *  * Copyright (c) 2021
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
package com.questhelper.requirements.magic;

import com.questhelper.ItemSearch;
import com.questhelper.QuestHelperConfig;
import com.questhelper.banktab.BankItemHolder;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.spells.Rune;
import com.questhelper.spells.Staff;
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

/**
 * Represents a single rune requirement that is used in {@link SpellRequirement}.
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
		this.runeItemRequirement = new ItemRequirement("", rune.getRunes(), this.requiredAmount);
		if (rune.getStaff() != Staff.UNKNOWN)
		{
			this.staffItemRequirement = new ItemRequirement(rune.getStaff().getName(), rune.getStaff().getStaves(), 1);
		}
	}

	/**
	 * Set the new number of times the spell will be cast.
	 *
	 * @param numberOfCasts the new number of casts
	 */
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
		boolean hasStaves = (staffItemRequirement != null && ItemSearch.hasItemsInBank(client, staffItemRequirement));
		return ItemSearch.hasItemsInBank(client, runeItemRequirement) || hasStaves;
	}

	@Override
	public boolean check(Client client, boolean checkWithSlotRestrictions, Item[] items)
	{
		boolean hasStaves = (staffItemRequirement != null && ItemSearch.hasItemsAnywhere(client, staffItemRequirement));
		return ItemSearch.hasItemsAnywhere(client, runeItemRequirement) || hasStaves;
	}

	@Override
	public List<ItemRequirement> getRequirements(Client client, QuestHelperConfig config)
	{
		boolean hasRunes = ItemSearch.hasItemsAnywhere(client, runeItemRequirement);
		boolean hasStaves = staffItemRequirement != null && ItemSearch.hasItemsAnywhere(client, staffItemRequirement);
		ItemRequirement requirement = config.bankFilterSearchPreference().getPreference(this, () -> hasRunes, () -> hasStaves);
		return Collections.singletonList(requirement);
	}
}
