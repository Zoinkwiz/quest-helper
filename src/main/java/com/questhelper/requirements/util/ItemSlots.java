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

import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

@Getter
public enum ItemSlots
{
	ANY_EQUIPPED_AND_INVENTORY(-3, "inventory or equipped", InventorySlots.EQUIPMENT_AND_INVENTORY_SLOTS),
	ANY_INVENTORY(-2, "inventory slots", InventorySlots.INVENTORY_SLOTS),
	ANY_EQUIPPED(-1, "equipped slots", InventorySlots.EQUIPMENT_SLOTS),
	HEAD(0, "head slot"),
	CAPE(1, "cape slot"),
	AMULET(2, "amulet slot"),
	WEAPON(3, "weapon slot"),
	BODY(4, "body slot"),
	SHIELD(5, "shield slot"),
	LEGS(7, "legs slot"),
	GLOVES(9, "gloves slot"),
	BOOTS(10, "boots slot"),
	RING(12, "ring slot"),
	AMMO(13, "ammo slot");

	private final int slotIdx;
	private final String name;
	private final InventorySlots inventorySlots;

	ItemSlots(int slotIdx, String name)
	{
		this.slotIdx = slotIdx;
		this.name = name;
		this.inventorySlots = null;
	}
	ItemSlots(int slotIdx, String name, InventorySlots slots)
	{
		this.slotIdx = slotIdx;
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
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null || getSlotIdx() < 0) // unknown slot
		{
			return false;
		}
		Item item = equipment.getItem(getSlotIdx());

		return predicate.test(item);
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
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null || getSlotIdx() < 0) // unknown slot
		{
			return false;
		}
		Item item = equipment.getItem(getSlotIdx());
		if (item == null)
		{
			return false;
		}

		return predicate.test(item);
	}
}