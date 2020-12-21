/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.requirements;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.client.ui.overlay.components.LineComponent;

public class ItemRequirement extends Requirement
{
	@Getter
	private final int id;

	@Setter
	private Integer displayItemId;

	private final String name;

	@Setter
	@Getter
	private int quantity;

	@Getter
	private boolean equip;

	@Setter
	@Getter
	private String tip;

	@Getter
	@Setter
	private boolean highlightInInventory;

	private final List<Integer> alternates = new ArrayList<>();

	@Setter
	private boolean exclusiveToOneItemType;

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

	public ItemRequirement(boolean highlightInInventory, String name, int id)
	{
		this(name, id);
		this.highlightInInventory = highlightInInventory;
	}

	public ItemRequirement(String name, List<Integer> items)
	{
		this(name, items.get(0), 1);
		this.addAlternates(items.subList(1, items.size()));
	}

	public ItemRequirement(String name, List<Integer> items, int quantity)
	{
		this(name, items.get(0), quantity);
		this.addAlternates(items.subList(1, items.size()));
	}

	public ItemRequirement(String name, List<Integer> items, int quantity, boolean equip)
	{
		this(name, items.get(0), quantity);
		this.equip = equip;
		this.addAlternates(items.subList(1, items.size()));
	}

	public void addAlternates(List<Integer> alternates)
	{
		this.alternates.addAll(alternates);
	}

	public void addAlternates(Integer... alternates)
	{
		Collections.addAll(this.alternates, alternates);
	}

	public boolean showQuantity()
	{
		return quantity != -1;
	}

	public boolean isActualItem()
	{
		return id != -1;
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<Integer> getAllIds()
	{
		ArrayList<Integer> ids = new ArrayList<>(alternates);
		ids.add(id);
		return ids;
	}

	public ArrayList<LineComponent> getDisplayTextWithChecks(Client client)
	{
		ArrayList<LineComponent> lines = new ArrayList<>();

		String text = "";
		if (this.showQuantity())
		{
			text = this.getQuantity() + " x ";
		}
		text = text + this.getName();

		Color color = getColor(client);

		lines.add(LineComponent.builder()
			.left(text)
			.leftColor(color)
			.build());

		lines.addAll(getAdditionalText(client));

		return lines;
	}

	@Override
	public String getDisplayText()
	{
		return getName();
	}

	protected Color getColor(Client client)
	{
		Color color;
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (this.check(client))
		{
			color = Color.GREEN;
		}
		else
		{
			color = Color.RED;
		}
		return color;
	}

	private ArrayList<LineComponent> getAdditionalText(Client client)
	{
		Color equipColor = Color.GREEN;

		ArrayList<LineComponent> lines = new ArrayList<>();

		if (this.isEquip())
		{
			String equipText = "(equipped)";
			if (!this.check(client, true))
			{
				equipColor = Color.RED;
			}
			lines.add(LineComponent.builder()
				.left(equipText)
				.leftColor(equipColor)
				.build());
		}

		if (this.getTip() != null && !check(client))
		{
			lines.add(LineComponent.builder()
				.left("- " + this.getTip())
				.leftColor(Color.WHITE)
				.build());
		}

		return lines;
	}

	public boolean check(Client client, boolean checkEquippedOnly)
	{
		int remainder = checkSpecificItem(client, id, checkEquippedOnly);
		if (remainder <= 0)
		{
			return true;
		}

		for (int alternate : alternates)
		{
			if (exclusiveToOneItemType)
			{
				remainder = quantity;
			}
			remainder = remainder - (quantity - checkSpecificItem(client, alternate, checkEquippedOnly));
			if (remainder <= 0)
			{
				return true;
			}
		}
		return false;
	}

	public int checkSpecificItem(Client client, int itemID, boolean checkEquippedOnly)
	{
		ItemContainer equipped = client.getItemContainer(InventoryID.EQUIPMENT);
		int tempQuantity = quantity;

		if (equipped != null)
		{
			tempQuantity -= getNumMatches(equipped, itemID);
		}

		if (!checkEquippedOnly)
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory != null)
			{
				tempQuantity -= getNumMatches(inventory, itemID);
			}
		}

		return tempQuantity;
	}

	public int getNumMatches(ItemContainer items, int itemID)
	{
		int tempQuantity = 0;

		Item[] containerItems = items.getItems();

		for (Item item : containerItems)
		{
			if (item.getId() == itemID)
			{
				tempQuantity += item.getQuantity();
			}
		}
		return tempQuantity;
	}

	public boolean check(Client client)
	{
		return check(client, false);
	}

	public boolean checkBank(Client client)
	{
		ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);
		if (bankContainer == null)
		{
			return false;
		}

		for (Integer itemId : getDisplayItemIds())
		{
			if (bankContainer.contains(itemId))
			{
				return true;
			}
		}

		return false;
	}

	public ArrayList<Integer> getDisplayItemIds()
	{
		if (displayItemId == null)
		{
			return getAllIds();
		}

		return new ArrayList<>(Collections.singletonList(displayItemId));
	}

	public boolean checkConsideringSlot(Client client)
	{
		return check(client, equip);
	}
}
