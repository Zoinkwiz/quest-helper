/*
 *  * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 *  * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.requirements.item;

import com.questhelper.ItemCollections;
import com.questhelper.QuestBank;
import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.InventorySlots;
import com.questhelper.requirements.util.LogicType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.ui.overlay.components.LineComponent;

public class ItemRequirement extends AbstractRequirement
{
	@Getter
	private final int id;

	private final String name;

	@Setter
	@Getter
	private Integer displayItemId;

	@Setter
	@Getter
	protected int quantity;

	@Getter
	@Setter
	private boolean equip;

	@Setter
	protected boolean highlightInInventory;

	protected final List<Integer> alternateItems = new ArrayList<>();

	@Setter
	protected boolean exclusiveToOneItemType;

	@Setter
	@Getter
	private boolean displayMatchedItemName;

	@Setter
	@Getter
	private Requirement conditionToHide;

	@Getter
	@Setter
	private QuestBank questBank;

	@Getter
	protected boolean hadItemLastCheck;

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

	public ItemRequirement(String name, ItemCollections itemCollection)
	{
		this(name, itemCollection.getItems().get(0), 1);
		this.setUrlSuffix(itemCollection.getWikiTerm());
		this.addAlternates(itemCollection.getItems().subList(1, itemCollection.getItems().size()));
	}

	public ItemRequirement(String name, ItemCollections itemCollection, int quantity)
	{
		this(name, itemCollection.getItems().get(0), quantity);
		this.setUrlSuffix(itemCollection.getWikiTerm());
		this.addAlternates(itemCollection.getItems().subList(1, itemCollection.getItems().size()));
	}

	public ItemRequirement(String name, ItemCollections itemCollection, int quantity, boolean equip)
	{
		this(name, itemCollection.getItems().get(0), quantity);
		this.setUrlSuffix(itemCollection.getWikiTerm());
		this.equip = equip;
		this.addAlternates(itemCollection.getItems().subList(1, itemCollection.getItems().size()));
	}

	public void addAlternates(List<Integer> alternates)
	{
		this.alternateItems.addAll(alternates);
	}

	public void addAlternates(ItemCollections alternates)
	{
		this.alternateItems.addAll(alternates.getItems());
	}

	public void addAlternates(Integer... alternates)
	{
		this.alternateItems.addAll(Arrays.asList(alternates));
	}

	public boolean showQuantity()
	{
		return quantity != -1;
	}

	public ItemRequirement highlighted()
	{
		ItemRequirement newItem = copy();
		newItem.setHighlightInInventory(true);
		return newItem;
	}

	public ItemRequirement alsoCheckBank(QuestBank questBank)
	{
		ItemRequirement newItem = copy();
		newItem.questBank = questBank;
		return newItem;
	}

	public ItemRequirement equipped()
	{
		ItemRequirement newItem = copy();
		newItem.setEquip(true);
		return newItem;
	}

	public ItemRequirement quantity(int newQuantity)
	{
		ItemRequirement newItem = copy();
		newItem.setQuantity(newQuantity);
		return newItem;
	}

	public ItemRequirement hideConditioned(Requirement condition)
	{
		ItemRequirement newItem = copy();
		newItem.setConditionToHide(condition);
		return newItem;
	}

	public ItemRequirement showConditioned(Requirement condition)
	{
		ItemRequirement newItem = copy();
		newItem.setConditionToHide(new Conditions(LogicType.NOR, condition));
		return newItem;
	}

	public ItemRequirement copy()
	{
		ItemRequirement newItem = new ItemRequirement(name, id, quantity, equip);
		newItem.addAlternates(alternateItems);
		newItem.setDisplayItemId(displayItemId);
		newItem.setExclusiveToOneItemType(exclusiveToOneItemType);
		newItem.setHighlightInInventory(highlightInInventory);
		newItem.setDisplayMatchedItemName(displayMatchedItemName);
		newItem.setConditionToHide(conditionToHide);
		newItem.questBank = questBank;
		newItem.hadItemLastCheck = hadItemLastCheck;
		newItem.setTooltip(getTooltip());
		newItem.setUrlSuffix(getUrlSuffix());

		return newItem;
	}

	public boolean isActualItem()
	{
		return id != -1 && quantity != -1;
	}

	public void canBeObtainedDuringQuest()
	{
		appendToTooltip("Can be obtained during the quest.");
	}

	public String getName()
	{
		return name;
	}

	public List<Integer> getAllIds()
	{
		List<Integer> items = new ArrayList<>(Collections.singletonList(id));
		items.addAll(alternateItems);

		return items.stream().distinct().collect(Collectors.toList());
	}

	@Override
	protected List<LineComponent> getOverlayDisplayText(Client client, QuestHelperConfig config)
	{
		List<LineComponent> lines = new ArrayList<>();

		if (conditionToHide != null && conditionToHide.check(client))
		{
			return lines;
		}

		StringBuilder text = new StringBuilder();
		if (this.showQuantity())
		{
			text.append(this.getQuantity()).append(" x ");
		}

		int itemID = findItemID(client, false);
		if (displayMatchedItemName && ((alternateItems.contains(itemID)) || id == itemID))
		{
			text.append(client.getItemDefinition(itemID).getName());
		}
		else
		{
			text.append(this.getName());
		}

		Color color = getColor(client, config);
		lines.add(LineComponent.builder()
			.left(text.toString())
			.leftColor(color)
			.build());
		lines.addAll(getAdditionalText(client, true, config));
		return lines;
	}

	@Override
	public String getDisplayText()
	{
		return getName();
	}

	@Override
	public boolean shouldDisplayText(Client client)
	{
		return !conditionToHide.check(client);
	}

	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		Color color = config.failColour();
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (this.check(client))
		{
			color = config.passColour();
		}
		return color;
	}

	/** Find the first item that this requirement allows that the player has, or -1 if they don't have any item(s) */
	private int findItemID(Client client, boolean checkConsideringSlotRestrictions)
	{
		int remainder = getRequiredItemDifference(client, id, checkConsideringSlotRestrictions, null);
		if (remainder <= 0)
		{
			return id;
		}
		List<Integer> ids = getAllIds();
		for (int alternate : ids)
		{
			if (exclusiveToOneItemType)
			{
				remainder = quantity;
			}
			remainder -= (quantity - getRequiredItemDifference(client, alternate, checkConsideringSlotRestrictions, null));
			if (remainder <= 0)
			{
				return alternate;
			}
		}
		return -1;
	}

	public Color getColorConsideringBank(Client client, boolean checkConsideringSlotRestrictions,
										 List<Item> bankItems, QuestHelperConfig config)
	{
		Color color = config.failColour();
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (this.check(client, checkConsideringSlotRestrictions))
		{
			color = config.passColour();
		}

		if (color == config.failColour() && bankItems != null)
		{
			if (check(client, false, bankItems))
			{
				color = Color.WHITE;
			}
		}

		return color;
	}

	protected ArrayList<LineComponent> getAdditionalText(Client client, boolean includeTooltip,
														 QuestHelperConfig config)
	{
		Color equipColor = config.passColour();

		ArrayList<LineComponent> lines = new ArrayList<>();

		if (this.isEquip())
		{
			String equipText = "(equipped)";
			if (!this.check(client, true))
			{
				equipColor = config.failColour();
			}
			lines.add(LineComponent.builder()
				.left(equipText)
				.leftColor(equipColor)
				.build());
		}

		if (includeTooltip && this.getTooltip() != null && !check(client))
		{
			lines.add(LineComponent.builder()
				.left("- " + this.getTooltip())
				.leftColor(Color.WHITE)
				.build());
		}

		return lines;
	}

	public int getMatches(Client client)
	{
		return getMatches(client, false, new ArrayList<>());
	}

	public int getMatches(Client client, boolean checkConsideringSlotRestrictions, List<Item> items)
	{
		List<Item> allItems = new ArrayList<>(items);
		if (questBank != null && questBank.getBankItems() != null)
		{
			allItems.addAll(questBank.getBankItems());
		}

		int remainder = 0;

		List<Integer> ids = getAllIds();
		for (int alternate : ids)
		{
			if (exclusiveToOneItemType)
			{
				remainder = quantity;
			}
			remainder += (quantity - getRequiredItemDifference(client, alternate, checkConsideringSlotRestrictions,
				allItems));
		}
		return remainder;
	}

	public boolean check(Client client, boolean checkConsideringSlotRestrictions, List<Item> items)
	{
		List<Item> allItems = new ArrayList<>(items);
		if (questBank != null && questBank.getBankItems() != null)
		{
			allItems.addAll(questBank.getBankItems());
		}

		int remainder = quantity;

		List<Integer> ids = getAllIds();
		for (int alternate : ids)
		{
			if (exclusiveToOneItemType)
			{
				remainder = quantity;
			}
			remainder -= (quantity - getRequiredItemDifference(client, alternate, checkConsideringSlotRestrictions,
				allItems));
			if (remainder <= 0)
			{
				hadItemLastCheck = true;
				return true;
			}
		}
		hadItemLastCheck = false;
		return false;
	}

	/**
	 * Get the difference between the required quantity for this requirement and the amount the client has.
	 * Any value <= 0 indicates they have the required amount
	 */
	public int getRequiredItemDifference(Client client, int itemID, boolean checkConsideringSlotRestrictions,
										 List<Item> items)
	{
		ItemContainer equipped = client.getItemContainer(InventoryID.EQUIPMENT);
		int tempQuantity = quantity;

		if (equipped != null)
		{
			tempQuantity -= getNumMatches(equipped, itemID);
		}

		if (!checkConsideringSlotRestrictions || !equip)
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			if (inventory != null)
			{
				tempQuantity -= getNumMatches(inventory, itemID);
			}
		}

		if (items != null)
		{
			tempQuantity -= getNumMatches(items, itemID);
		}

		return tempQuantity;
	}

	public int getNumMatches(ItemContainer items, int itemID)
	{
		return getNumMatches(Arrays.asList(items.getItems().clone()), itemID);
	}

	public int getNumMatches(List<Item> items, int itemID)
	{
		return items.stream()
			.filter(Objects::nonNull)
			.filter(i -> i.getId() == itemID)
			.mapToInt(Item::getQuantity)
			.sum();
	}

	public boolean check(Client client)
	{
		return check(client, false);
	}

	public boolean check(Client client, boolean checkConsideringSlotRestrictions)
	{
		return check(client, checkConsideringSlotRestrictions, new ArrayList<>());
	}

	public boolean checkBank(Client client)
	{
		return InventorySlots.BANK.contains(client, item -> getDisplayItemIds().contains(item.getId()));
	}

	public List<Integer> getDisplayItemIds()
	{
		if (displayItemId == null)
		{
			return getAllIds();
		}

		return Collections.singletonList(displayItemId);
	}

	public boolean shouldRenderItemHighlights(Client client)
	{
		return conditionToHide == null || !conditionToHide.check(client);
	}

	public boolean shouldHighlightInInventory(Client client)
	{
		return highlightInInventory && shouldRenderItemHighlights(client);
	}
}
