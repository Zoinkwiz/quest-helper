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
package com.questhelper.requirements.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
public enum ItemSlots
{
	ANY_EQUIPPED_AND_INVENTORY("inventory or equipped", InventorySlots.EQUIPMENT_AND_INVENTORY_SLOTS, -3),
	ANY_INVENTORY("inventory slots", InventorySlots.INVENTORY_SLOTS, -2),
	ANY_EQUIPPED("equipped slots", InventorySlots.EQUIPMENT_SLOTS, -1),

	EMPTY_HANDS("weapons and shield slot", 3, 5),
	BARE_HANDS("weapon, shield and gloves slot", 3, 5, 9),
	
	HEAD("head slot", 0),
	CAPE("cape slot", 1),
	AMULET("amulet slot", 2),
	WEAPON("weapon slot", 3),
	BODY("body slot", 4),
	SHIELD("shield slot", 5),
	LEGS("legs slot", 7),
	GLOVES("gloves slot", 9),
	BOOTS("boots slot", 10),
	RING("ring slot", 12),
	AMMO("ammo slot", 13);

	private final int[] slotIdxs;
	private final String name;
	private final InventorySlots inventorySlots;

	ItemSlots(String name, int... slotIdx)
	{
		this.slotIdxs = slotIdx;
		this.name = name;
		this.inventorySlots = null;
	}
	ItemSlots(String name, InventorySlots slots, int... slotIdx)
	{
		this.slotIdxs = slotIdx;
		this.name = name;
		this.inventorySlots = slots;
	}

	/**
	 * Checks that a given item slot is empty.
	 * If checking all equipment, inventory, or both, the predicate will be used to
	 *
	 * @param client the {@link Client} to check
	 * @param predicate the predicate to use
	 * @return true if ALL the items match the predicate via {@link Stream#allMatch(Predicate)}
	 */
	public boolean checkInventory(Client client, Predicate<Item> predicate)
	{
		// if we're checking all the equipment slots, inventory slots, or both
		if (getInventorySlots() != null)
		{
			return getInventorySlots().checkInventory(client, predicate);
		}
		// otherwise check a specific slot
		if (client.getLocalPlayer() == null)
		{
			return false;
		}
		return getItems(client).allMatch(predicate);
	}

	/**
	 * Check if any of the {@link Item} in a client's {@link ItemContainer} match
	 * the given predicate.
	 *
	 * @param client the {@link Client} to check
	 * @param predicate the predicate to use
	 * @return true if ANY of the items match the predicate
	 */
	public boolean contains(Client client, Predicate<Item> predicate)
	{
		// if we're checking all the equipment slots, inventory slots, or both
		if (getInventorySlots() != null)
		{
			return getInventorySlots().contains(client, predicate);
		}
		// otherwise check a specific slot
		if (client.getLocalPlayer() == null)
		{
			return false;
		}
		return getItems(client).anyMatch(predicate);
	}

	private Stream<Item> getItems(Client client){
		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);

		List<Item> items = new ArrayList<>();
		for(int slotIdx: getSlotIdxs())
		{
			if (equipment == null || slotIdx < 0) // unknown slot
			{
				continue;
			}
			items.add(equipment.getItem(slotIdx));
		}
		return items.stream();
	}
}
