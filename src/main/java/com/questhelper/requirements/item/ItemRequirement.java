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

import com.questhelper.collections.ItemCollections;
import com.questhelper.bank.QuestBank;
import com.questhelper.collections.ItemWithCharge;
import com.questhelper.QuestHelperConfig;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.ui.overlay.components.LineComponent;
import org.jetbrains.annotations.Nullable;
import javax.annotation.Nonnull;

public class ItemRequirement extends AbstractRequirement
{
	@Setter
	@Getter
	private int id;

	@Setter
	private String name;

	@Setter
	@Getter
	private Integer displayItemId;

	@Setter
	@Getter
	protected int quantity;

	@Getter
	@Setter
	protected boolean equip;

	@Setter
	protected boolean highlightInInventory;

	protected final List<Integer> alternateItems = new ArrayList<>();

	@Setter
	protected boolean exclusiveToOneItemType;

	@Setter
	@Getter
	private boolean displayMatchedItemName;

	// This is defaulted to a ManualRequirement so that we can use a null state to better detect issues
	@Setter
	@Getter
	protected Requirement conditionToHide = new ManualRequirement();

	@Setter
	@Getter
	private QuestBank questBank;

	@Setter
	@Getter
	private boolean shouldCheckBank;

	@Getter
	protected boolean isConsumedItem = true;

	protected boolean shouldAggregate = true;

	/**
	 * Denotes whether the quantity-check should take into consideration item charges.
	 * With this enabled, 1xRing of dueling(7) will count as 7 quantity.
	 */
	@Setter
	@Getter
	protected boolean isChargedItem = false;

	protected Requirement additionalOptions;


	Map<TrackedContainers, ContainerStateForRequirement> knownContainerStates = new HashMap<>();
	{
		for (TrackedContainers value : TrackedContainers.values())
		{
			knownContainerStates.put(value, new ContainerStateForRequirement());
		}
	}

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

		assert(items.stream().noneMatch(Objects::isNull));

		this.addAlternates(items.subList(1, items.size()));
	}

	public ItemRequirement(String name, List<Integer> items, int quantity)
	{
		this(name, items.get(0), quantity);

		assert(items.stream().noneMatch(Objects::isNull));

		this.addAlternates(items.subList(1, items.size()));
	}

	public ItemRequirement(String name, List<Integer> items, int quantity, boolean equip)
	{
		this(name, items.get(0), quantity);

		assert(items.stream().noneMatch(Objects::isNull));

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

	public void useQuestBank(QuestBank questBank)
	{
		this.shouldCheckBank = questBank != null;
		this.questBank = questBank;
	}

	public ItemRequirement alsoCheckBank(QuestBank questBank)
	{
		ItemRequirement newItem = copy();
		newItem.useQuestBank(questBank);
		return newItem;
	}

	public ItemRequirement named(String name)
	{
		ItemRequirement newItem = copy();
		newItem.setName(name);
		return newItem;
	}

	public ItemRequirement equipped()
	{
		ItemRequirement newItem = copy();
		newItem.setEquip(true);
		return newItem;
	}

	public ItemRequirement isNotConsumed()
	{
		ItemRequirement newItem = copy();
		newItem.isConsumedItem = false;
		return newItem;
	}

	public ItemRequirement doNotAggregate()
	{
		ItemRequirement newItem = copy();
		newItem.shouldAggregate = false;
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

	protected ItemRequirement copyOfClass()
	{
		if (this.getClass() != ItemRequirement.class)
		{
			throw new UnsupportedOperationException("Subclasses must override copy()");
		}
		return new ItemRequirement(name, id, quantity, equip);
	}

	public ItemRequirement copy()
	{
		ItemRequirement newItem = copyOfClass();
		newItem.setName(name);
		newItem.setId(id);
		newItem.setEquip(equip);
		newItem.setQuantity(quantity);
		newItem.addAlternates(alternateItems);
		newItem.setDisplayItemId(displayItemId);
		newItem.setExclusiveToOneItemType(exclusiveToOneItemType);
		newItem.setHighlightInInventory(highlightInInventory);
		newItem.setDisplayMatchedItemName(displayMatchedItemName);
		newItem.setConditionToHide(conditionToHide);
		newItem.questBank = questBank;
		newItem.isConsumedItem = isConsumedItem;
		newItem.shouldAggregate = shouldAggregate;
		newItem.setTooltip(getTooltip());
		newItem.setUrlSuffix(getUrlSuffix());
		newItem.additionalOptions = additionalOptions;
		newItem.isChargedItem = isChargedItem;

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

		if (!shouldDisplayText(client))
		{
			return lines;
		}

		StringBuilder text = new StringBuilder();
		if (this.showQuantity())
		{
			text.append(this.getQuantity()).append(" x ");
		}

		int itemID = findItemID();
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

	@Nonnull
	@Override
	public String getDisplayText()
	{
		StringBuilder text = new StringBuilder();

		if (showQuantity())
		{
			text.append(getQuantity()).append(" x ");
		}

		text.append(getName());

		return text.toString();
	}

	public void setAdditionalOptions(Requirement additionalOptions)
	{
		this.additionalOptions = additionalOptions;
	}

	@Nullable
	@Override
	public String getWikiUrl()
	{
		if (getUrlSuffix() != null) {
			return "https://oldschool.runescape.wiki/w/" + getUrlSuffix();
		}

		if (getId() != -1) {
			return "https://oldschool.runescape.wiki/w/Special:Lookup?type=item&id=" + getId();
		}

		return null;
	}

	@Override
	public boolean shouldDisplayText(Client client)
	{
		return conditionToHide == null || !conditionToHide.check(client);
	}

	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		Color color = config.failColour();
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (this.checkContainersOnPlayer(client))
		{
			color = config.passColour();
		}
		return color;
	}

	/** Find the first item that this requirement allows that the player has, or -1 if they don't have any item(s) */
	private int findItemID()
	{
		int remainder = getNumberOfItemFound(id, null);
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
			remainder -= (quantity - getNumberOfItemFound(alternate, null));
			if (remainder <= 0)
			{
				return alternate;
			}
		}
		return -1;
	}

	public boolean checkItems(Client client, List<Item> items)
	{
		return getMaxMatchingItems(client, items.toArray(new Item[0])) >= quantity;
	}

	public int checkTotalMatchesInContainers(Client client, ItemAndLastUpdated... containers)
	{
		// If exclusive, we need to check all containers together
		if (exclusiveToOneItemType)
		{
			return getTotalMatchesInContainersIfExclusive(client, containers);
		}

		int totalFound = 0;

		// Consideration: If any have changed, AND ItemRequirement requires unique item, then we do need to aggregate all the results
		for (ItemAndLastUpdated container : containers)
		{
			if (container.getItems() == null)
			{
				continue;
			}
			ContainerStateForRequirement stateForItemInContainer = knownContainerStates.get(container.getContainerType());
			// Generic container, always check
			if (container.getContainerType() == TrackedContainers.UNDEFINED)
			{
				totalFound += getMaxMatchingItems(client, container.getItems());
			}
			else if (stateForItemInContainer.getLastCheckedTick() < container.getLastUpdated())
			{
				int matchesInContainer = getMaxMatchingItems(client, container.getItems());
				stateForItemInContainer.set(matchesInContainer, client.getTickCount());
				totalFound += matchesInContainer;
			}
			else
			{
				totalFound += stateForItemInContainer.getMatchesFound();
			}
		}

		return totalFound;
	}

	// This will ignore any defined conditions for what to consider, and will check across all
	// containers passed in for the item
	public boolean checkContainers(Client client, ItemAndLastUpdated... containers)
	{
		// If exclusive, we need to check all containers together
		if (exclusiveToOneItemType)
		{
			return checkContainersIfExclusive(client, containers);
		}

		return checkTotalMatchesInContainers(client, containers) >= quantity;
	}

	private boolean checkContainersIfExclusive(Client client, ItemAndLastUpdated... containers)
	{
		int total = getTotalMatchesInContainersIfExclusive(client, containers);
		return total >= quantity;
	}

	private int getTotalMatchesInContainersIfExclusive(Client client, ItemAndLastUpdated... containers)
	{
		List<Item> allItems = new ArrayList<>();
		for (ItemAndLastUpdated container : containers)
		{
			if (container.getItems() == null)
			{
				continue;
			}
			allItems.addAll(List.of(container.getItems()));
		}

		return getMaxMatchingItems(client, allItems.toArray(new Item[0]));
	}

	private boolean checkContainersOnPlayer(Client client)
	{
		return checkContainers(client, QuestContainerManager.getEquippedData(), QuestContainerManager.getInventoryData());
	}

	public boolean checkWithBank(Client client)
	{
		return checkContainers(client, QuestContainerManager.getEquippedData(), QuestContainerManager.getInventoryData(), QuestContainerManager.getBankData()
				, QuestContainerManager.getPotionData());
	}

	public Color getColorConsideringBank(Client client, QuestHelperConfig config)
	{
		Color color = config.failColour();
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (this.checkContainersOnPlayer(client))
		{
			color = config.passColour();
		}

		if (color == config.failColour() && this.checkContainers(client, QuestContainerManager.getBankData()))
		{
			color = Color.WHITE;
		}
		if (color == config.failColour() && this.checkContainers(client, QuestContainerManager.getPotionData()))
		{
			color = Color.CYAN;
		}

		return color;
	}

	protected ArrayList<LineComponent> getAdditionalText(Client client, boolean includeTooltip, QuestHelperConfig config)
	{
		Color equipColor = config.passColour();

		ArrayList<LineComponent> lines = new ArrayList<>();

		if (this.isEquip())
		{
			String equipText = "(equipped)";
			if (!checkContainers(client, QuestContainerManager.getEquippedData()))
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

	@Override
	public boolean check(Client client)
	{
		ItemAndLastUpdated[] containers = containersToCheckDefault();

		return checkContainers(client, containers);
	}

	private ItemAndLastUpdated[] containersToCheckDefault()
	{
		List<ItemAndLastUpdated> containers = new ArrayList<>();
		containers.add(QuestContainerManager.getEquippedData());

		if (!equip) containers.add(QuestContainerManager.getInventoryData());
		if (shouldCheckBank)
		{
			containers.add(QuestContainerManager.getBankData());
			containers.add(QuestContainerManager.getPotionData());
		}

		return containers.toArray(new ItemAndLastUpdated[0]);
	}

	private int getMaxMatchingItems(Client client, @NonNull Item[] items)
	{
		// TODO: Is this right to do? Misleading on number for some scenarios
		// Perhaps additionalOptions should have some text change instead assosciated
		if (additionalOptions != null && additionalOptions.check(client))
		{
			return quantity;
		}

		List<Item> allItems = new ArrayList<>(List.of(items));

		int foundQuantity = 0;

		List<Integer> ids = getAllIds();
		for (int alternate : ids)
		{
			int tmpQuantity = foundQuantity;
			if (exclusiveToOneItemType)
			{
				tmpQuantity = 0;
			}
			tmpQuantity += getNumberOfItemFound(alternate, allItems);

			if (foundQuantity < tmpQuantity) foundQuantity = tmpQuantity;
		}

		return foundQuantity;
	}

	/**
	 * Get the difference between the required quantity for this requirement and the amount the client has.
	 * Any value <= 0 indicates they have the required amount
	 */
	public int getNumberOfItemFound(int itemID, List<Item> items)
	{
		int tempQuantity = 0;

		if (items != null)
		{
			tempQuantity += getNumMatches(items, itemID);
		}

		return tempQuantity;
	}

	public int getNumMatches(List<Item> items, int itemID)
	{
		if (isChargedItem)
		{
			return items.stream()
					.filter(Objects::nonNull)
					.filter(i -> i.getId() == itemID)
					.mapToInt(i -> {
						ItemWithCharge itemWithCharge = ItemWithCharge.findItem(i.getId());
						if (itemWithCharge != null)
						{
							return itemWithCharge.getCharges();
						}

						// Fall back to using the item's quantity
						return i.getQuantity();
					})
					.sum();
		}

		return items.stream()
				.filter(Objects::nonNull)
				.filter(i -> i.getId() == itemID)
				.mapToInt(Item::getQuantity)
				.sum();
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
