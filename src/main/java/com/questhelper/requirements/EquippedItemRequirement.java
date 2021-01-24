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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;

/**
 * Checks if the player has supplied item(s) in the given slot.
 * Unlike {@link ItemRequirement}, this only checks given slots, it does not check bank items.
 */
@Getter
public class EquippedItemRequirement extends Requirement
{
	private final ItemSlots slot;
	private final String name;
	private final List<Integer> itemIDs = new ArrayList<>();

	/**
	 * Check if the player has a certain item in a certain {@link ItemSlots}.
	 *
	 * @param name the display text
	 * @param slot the slot to check
	 * @param id the item id
	 */
	public EquippedItemRequirement(String name, ItemSlots slot, int id)
	{
		this.slot = slot;
		this.name = name;
		this.itemIDs.add(id);
	}

	/**
	 * Check if the player has any of the given items in a certain {@link ItemSlots}
	 *
	 * @param name the display text
	 * @param slot the slot to check
	 * @param items list of valid item ids
	 */
	public EquippedItemRequirement(String name, ItemSlots slot, List<Integer> items)
	{
		this.slot = slot;
		this.name = name;
		this.itemIDs.addAll(items);
	}

	/**
	 * Check if the player has any of the given items in a certain {@link ItemSlots}
	 *
	 * @param name the display text
	 * @param slot the slot to check
	 * @param items list of valid item ids
	 */
	public EquippedItemRequirement(String name, ItemSlots slot, Integer... items)
	{
		this.slot = slot;
		this.name = name;
		this.itemIDs.addAll(Arrays.asList(items));
	}

	@Override
	public boolean check(Client client)
	{
		return slot.checkInventory(client, i -> getItemIDs().contains(i.getId()));
	}

	@Override
	public String getDisplayText()
	{
		return name;
	}
}
