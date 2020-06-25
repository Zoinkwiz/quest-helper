/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.game.ItemManager;

public class ItemRequirement
{
	@Getter
	private final int id;

	private String name;

	@Getter
	private final int quantity;

	@Getter
	private boolean equip;

	@Setter
	@Getter
	private String tip;

	private final List<Integer> alternates = new ArrayList<>();

	public ItemRequirement(String name, int id)
	{
		this(name, id, 1);
	}

	public ItemRequirement(String name, int id, int quantity)
	{
		this.id = id;
		this.quantity = quantity;
		this.name = name;
		equip = false;
	}

	public ItemRequirement(String name, int id, int quantity, boolean equip)
	{
		this(name, id, quantity);
		this.equip = equip;
	}

	public void addAlternates(List<Integer> alternates) {
		this.alternates.addAll(alternates);
	}

	public void addAlternates(Integer... alternates) {
		Collections.addAll(this.alternates, alternates);
	}

	public void addName(String name)
	{
		this.name = name;
	}

	public boolean showQuantity() {
		return quantity != -1;
	}

	public boolean isActualItem() {
		return id != -1;
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<Integer> getAllIds() {
		ArrayList<Integer> ids = new ArrayList<>(alternates);
		ids.add(id);
		return ids;
	}

	public boolean check(Client client, boolean checkEquippedOnly)
	{
		ItemContainer equipped = client.getItemContainer(InventoryID.EQUIPMENT);
		int tempQuantity = quantity;

		if(equipped != null)
		{
			Item[] equippedItems = equipped.getItems();

			for (Item item : equippedItems)
			{
				if (item.getId() == id || alternates.contains(item.getId()))
				{
					if (item.getQuantity() >= tempQuantity)
					{
						return true;
					}
					else
					{
						tempQuantity -= item.getQuantity();
					}
				}
			}
		}

		if(!checkEquippedOnly) {
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if(inventory != null)
			{
				Item[] inventoryItems = inventory.getItems();
				for (Item item : inventoryItems)
				{
					if (item.getId() == id || alternates.contains(item.getId()))
					{
						if (item.getQuantity() >= tempQuantity)
						{
							return true;
						}
						else
						{
							tempQuantity -= item.getQuantity();
						}
					}
				}
			}
		}
		return false;
	}

	public boolean check(Client client) {
		return check(client, false);
	}
}
