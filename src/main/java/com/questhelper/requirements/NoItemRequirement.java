/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.requirements;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

public class NoItemRequirement extends ItemRequirement
{
	public static final int ALL_EQUIPMENT_SLOTS = -1;
	public static final int ALL_INVENTORY_SLOTS = -2;
	public static final int ALL_EQUIPMENT_AND_INVENTORY_SLOTS = -3;
	int slot;

	public NoItemRequirement(String text, int slot)
	{
		super(text, -1);
		this.slot = slot;
	}

	@Override
	public boolean check(Client client)
	{
		if (slot == ALL_EQUIPMENT_SLOTS)
		{
			ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
			if (equipment == null)
			{
				return true;
			}
			for (Item item : equipment.getItems())
			{
				if (item.getId() != -1)
				{
					return false;
				}
			}
			return true;
		}
		if (slot == ALL_INVENTORY_SLOTS)
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory == null)
			{
				return true;
			}
			for (Item item : inventory.getItems())
			{
				if (item.getId() != -1)
				{
					return false;
				}
			}
			return true;
		}
		if (slot == ALL_EQUIPMENT_AND_INVENTORY_SLOTS)
		{
			ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

			if (inventory != null)
			{
				for (Item item : inventory.getItems())
				{
					if (item.getId() != -1)
					{
						return false;
					}
				}
			}

			if (equipment != null)
			{
				for (Item item : equipment.getItems())
				{
					if (item.getId() != -1)
					{
						return false;
					}
				}
			}
			return true;
		}

		if (client.getLocalPlayer() == null || client.getLocalPlayer().getPlayerComposition() == null)
		{
			return true;
		}

		int[] equipment = client.getLocalPlayer().getPlayerComposition().getEquipmentIds();

		if (equipment == null)
		{
			return true;
		}

		return equipment[slot] <= 512;
	}

	@Override
	public String getDisplayText()
	{
		return "Nothing in your " + ItemSlots.getById(slot);
	}
}
