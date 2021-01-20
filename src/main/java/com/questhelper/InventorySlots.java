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

package com.questhelper;

import com.questhelper.requirements.NoItemRequirement;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

/**
 * Represent the inventories that a client can have.
 * This is not for actual slots in those inventories.
 */
//TODO: Get rid of the -1, -2, -3 in NoItemRequirement and move to using this enum
@Getter
public enum InventorySlots
{
	/** Represents the equipment slots of a player */
	EQUIPMENT_SLOTS(NoItemRequirement.ALL_EQUIPMENT_SLOTS, InventoryID.EQUIPMENT),
	/** Represents the inventory slots of a player */
	INVENTORY_SLOTS(NoItemRequirement.ALL_INVENTORY_SLOTS, InventoryID.INVENTORY),
	/** Represents both equipment and inventory slots of a player */
	EQUIPMENT_AND_INVENTORY_SLOTS(NoItemRequirement.ALL_EQUIPMENT_AND_INVENTORY_SLOTS, InventoryID.INVENTORY, InventoryID.EQUIPMENT),
	;

	private final int slotID;
	private final InventoryID[] inventoryID;
	InventorySlots(int slotID, InventoryID... inventoryID)
	{
		this.slotID = slotID;
		this.inventoryID = inventoryID;
	}

	/**
	 * Checks that all the {@link Item}s in a client's {@link ItemContainer} match the given predicate.
	 * 
	 * @param client the {@link Client} to check
	 * @param predicate the predicate to use
	 * @return true if ALL the items match the predicate via {@link Stream#allMatch(Predicate)}
	 */
	public boolean checkInventory(Client client, Predicate<Item> predicate)
	{
		return Arrays.stream(inventoryID)
			.map(client::getItemContainer)
			.filter(Objects::nonNull)
			.map(ItemContainer::getItems)
			.flatMap(Arrays::stream)
			.allMatch(predicate);
	}

	public static InventorySlots getBySlot(int slotID)
	{
		switch(slotID)
		{
			case NoItemRequirement.ALL_EQUIPMENT_SLOTS:
				return InventorySlots.EQUIPMENT_SLOTS;
			case NoItemRequirement.ALL_INVENTORY_SLOTS:
				return InventorySlots.INVENTORY_SLOTS;
			default:
				return InventorySlots.EQUIPMENT_AND_INVENTORY_SLOTS;
		}
	}
}
