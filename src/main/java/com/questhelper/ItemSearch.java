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

package com.questhelper;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.InventorySlots;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

@UtilityClass
public class ItemSearch
{

	public boolean hasItemAnywhere(Client client, int itemID)
	{
		return hasItemOnPlayer(client, itemID) || hasItemInBank(client, itemID);
	}

	public boolean hasItemAmountAnywhere(Client client, int itemID, int requiredAmount)
	{
		return hasItemAmountOnPlayer(client, itemID, requiredAmount) || hasItemAmountInBank(client, itemID, requiredAmount);
	}

	public boolean hasItemOnPlayer(Client client, int itemID)
	{
		return hasItemInInventory(client, itemID) || hasItemEquipped(client, itemID);
	}

	public boolean hasItemAmountOnPlayer(Client client, int itemID, int requiredAmount)
	{
		return hasItemAmountInInventory(client, itemID, requiredAmount) || hasItemAmountEquipped(client, itemID, requiredAmount);
	}

	public boolean hasItemInInventory(Client client, int itemID)
	{
		return checkItem(client, InventorySlots.INVENTORY_SLOTS, itemID);
	}

	public boolean hasItemAmountInInventory(Client client, int itemID, int requiredAmount)
	{
		return getItemAmountExact(client, InventoryID.INVENTORY, itemID) >= requiredAmount;
	}

	public boolean hasItemEquipped(Client client, int itemID)
	{
		return checkItem(client, InventorySlots.EQUIPMENT_SLOTS, itemID);
	}

	public boolean hasItemAmountEquipped(Client client, int itemID, int requiredAmount)
	{
		return getItemAmountExact(client, InventoryID.EQUIPMENT, itemID) >= requiredAmount;
	}

	public boolean hasItemInBank(Client client, int itemID)
	{
		return checkItem(client, InventorySlots.BANK, itemID);
	}

	public boolean hasItemAmountInBank(Client client, int itemID, int requiredAmount)
	{
		return getItemAmountExact(client, InventoryID.BANK, itemID) >= requiredAmount;
	}

	public long getItemCount(Client client, int itemID)
	{
		return getItemCountOnPlayer(client, itemID) + getItemCountInBank(client, itemID);
	}

	public long getItemCountOnPlayer(Client client, int itemID)
	{
		return getItemAmountExact(client, InventoryID.INVENTORY, itemID) + getItemAmountExact(client, InventoryID.EQUIPMENT, itemID);
	}

	public long getItemCountInBank(Client client, int itemID)
	{
		return getItemAmountExact(client, InventoryID.BANK, itemID);
	}

	public boolean hasItemsAnywhere(Client client, ItemRequirement requirement)
	{
		return hasItemsOnPlayer(client, requirement) || hasItemsInBank(client, requirement);
	}

	public boolean hasItemsOnPlayer(Client client, ItemRequirement requirement)
	{
		return requirement.getAllIds().stream().anyMatch(id -> hasItemAmountOnPlayer(client, id, requirement.getQuantity()));
	}

	public boolean hasItemsInBank(Client client, ItemRequirement requirement)
	{
		return requirement.getAllIds().stream().anyMatch(id -> hasItemAmountInBank(client, id, requirement.getQuantity()));
	}

	public boolean hasItemsInCachedBank(ItemRequirement requirement, Item[] items)
	{
		return Stream.of(items)
			.filter(Objects::nonNull)
			.filter(i -> i.getId() > -1 && i.getQuantity() > -1) // filter out invalid/empty items
			.anyMatch(i -> requirement.getAllIds().contains(i.getId()) && i.getQuantity() >= requirement.getQuantity());
	}

	public boolean checkItem(Client client, InventorySlots slot, int itemID)
	{
		return slot.contains(client, i -> i.getId() == itemID);
	}

	public long getItemAmountExact(Client client, InventoryID inventoryID, int itemID)
	{
		ItemContainer container = client.getItemContainer(inventoryID);
		if (container == null)
		{
			return 0L;
		}
		return Stream.of(container.getItems())
			.filter(Objects::nonNull)
			.filter(item -> item.getId() == itemID)
			.mapToLong(Item::getQuantity)
			.sum();
	}

	public int findFirstItem(Client client, Collection<Integer> itemIDs, int amount)
	{
		return itemIDs.stream()
			.filter(id -> hasItemAmountAnywhere(client, id, amount))
			.findFirst()
			.orElse(-1);
	}
}
