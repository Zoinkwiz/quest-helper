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

import com.questhelper.QuestHelperConfig;
import com.questhelper.bank.QuestBank;
import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.ItemWithCharge;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.util.Text;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a requirement for a specific item within the quest helper.
 * <p>
 * This class checks if the player possesses a certain item (or one of its alternate items)
 * in various containers (inventory, equipped, bank, etc.) while also supporting additional options,
 * quantity checks (including charged items), and display customizations.
 * </p>
 */
public class ItemRequirement extends AbstractRequirement
{
	/**
	 * The primary item id for this requirement.
	 */
	@Setter
	@Getter
	private int id;

	/**
	 * The display name for this item requirement.
	 */
	@Setter
	private String name;

	/**
	 * The item id used for display purposes, if specified.
	 */
	@Setter
	@Getter
	private Integer displayItemId;

	/**
	 * The required quantity of the item.
	 */
	@Setter
	@Getter
	protected int quantity;

	/**
	 * Indicates whether the item must be equipped.
	 */
	@Getter
	@Setter
	protected boolean equip;

	/**
	 * Whether the item should be highlighted in the inventory.
	 */
	@Setter
	protected boolean highlightInInventory;

	/**
	 * Alternate item ids that satisfy this requirement.
	 */
	protected final List<Integer> alternateItems = new ArrayList<>();

	/**
	 * Determines whether the matched item name should be displayed.
	 */
	@Setter
	@Getter
	private boolean displayMatchedItemName;

	/**
	 * A condition that, if met, hides this requirement.
	 * Defaulted to a ManualRequirement to allow a null state to be better detected.
	 */
	@Setter
	@Getter
	protected Requirement conditionToHide = new ManualRequirement();

	/**
	 * The quest bank used for additional bank checks.
	 */
	@Setter
	@Getter
	private QuestBank questBank;

	/**
	 * Flag to indicate whether the bank should be checked.
	 */
	@Setter
	@Getter
	private boolean shouldCheckBank;

	/**
	 * Indicates whether this item is consumed.
	 */
	@Getter
	protected boolean isConsumedItem = true;

	/**
	 * Determines whether the item count should be aggregated from multiple sources.
	 */
	protected boolean shouldAggregate = true;

	/**
	 * Denotes whether the quantity-check should consider item charges.
	 * For example, a charged item like a Ring of dueling (7) will count as 7 quantity.
	 */
	@Setter
	@Getter
	protected boolean isChargedItem = false;

	/**
	 * Additional options that can further modify the requirement.
	 */
	protected Requirement additionalOptions;

	/**
	 * Stores the last known state for each container type for this requirement.
	 */
	Map<TrackedContainers, ContainerStateForRequirement> knownContainerStates = new HashMap<>();
	{
		for (TrackedContainers value : TrackedContainers.values())
		{
			knownContainerStates.put(value, new ContainerStateForRequirement());
		}
	}

	/**
	 * Constructs a new ItemRequirement with the specified name and item id,
	 * requiring a default quantity of 1.
	 *
	 * @param name the display name of the item requirement
	 * @param id   the primary item id for the requirement
	 */
	public ItemRequirement(String name, int id)
	{
		this(name, id, 1);
	}

	/**
	 * Constructs a new ItemRequirement with the specified name, item id, and required quantity.
	 *
	 * @param name     the display name of the item requirement
	 * @param id       the primary item id for the requirement
	 * @param quantity the required quantity of the item
	 */
	public ItemRequirement(String name, int id, int quantity)
	{
		this.id = id;
		this.quantity = quantity;
		this.name = name;
		equip = false;
	}

	/**
	 * Constructs a new ItemRequirement with the specified name, item id, required quantity, and equipment requirement.
	 *
	 * @param name     the display name of the item requirement
	 * @param id       the primary item id for the requirement
	 * @param quantity the required quantity of the item
	 * @param equip    {@code true} if the item must be equipped, {@code false} otherwise
	 */
	public ItemRequirement(String name, int id, int quantity, boolean equip)
	{
		this(name, id, quantity);
		this.equip = equip;
	}

	/**
	 * Constructs a new ItemRequirement with an option to highlight the item in the inventory.
	 *
	 * @param highlightInInventory {@code true} if the item should be highlighted in the inventory
	 * @param name                 the display name of the item requirement
	 * @param id                   the primary item id for the requirement
	 */
	public ItemRequirement(boolean highlightInInventory, String name, int id)
	{
		this(name, id);
		this.highlightInInventory = highlightInInventory;
	}

	/**
	 * Constructs a new ItemRequirement using a list of item ids. The first id is used as the primary id,
	 * and the remaining ids are considered alternate items.
	 *
	 * @param name  the display name of the item requirement
	 * @param items the list of item ids, where the first element is the primary id
	 * @throws AssertionError if any item in the list is {@code null}
	 */
	public ItemRequirement(String name, List<Integer> items)
	{
		this(name, items.get(0), 1);
		assert (items.stream().noneMatch(Objects::isNull));
		this.addAlternates(items.subList(1, items.size()));
	}

	/**
	 * Constructs a new ItemRequirement using a list of item ids with a specified quantity.
	 * The first id in the list is used as the primary id, and the remaining are considered alternates.
	 *
	 * @param name     the display name of the item requirement
	 * @param items    the list of item ids, where the first element is the primary id
	 * @param quantity the required quantity of the item
	 * @throws AssertionError if any item in the list is {@code null}
	 */
	public ItemRequirement(String name, List<Integer> items, int quantity)
	{
		this(name, items.get(0), quantity);
		assert (items.stream().noneMatch(Objects::isNull));
		this.addAlternates(items.subList(1, items.size()));
	}

	/**
	 * Constructs a new ItemRequirement using a list of item ids with a specified quantity and equipment flag.
	 * The first id in the list is used as the primary id, and the remaining are considered alternates.
	 *
	 * @param name     the display name of the item requirement
	 * @param items    the list of item ids, where the first element is the primary id
	 * @param quantity the required quantity of the item
	 * @param equip    {@code true} if the item must be equipped, {@code false} otherwise
	 * @throws AssertionError if any item in the list is {@code null}
	 */
	public ItemRequirement(String name, List<Integer> items, int quantity, boolean equip)
	{
		this(name, items.get(0), quantity);
		assert (items.stream().noneMatch(Objects::isNull));
		this.equip = equip;
		this.addAlternates(items.subList(1, items.size()));
	}

	/**
	 * Constructs a new ItemRequirement using an {@link ItemCollections} object.
	 * The primary item id is the first item in the collection, and alternate items are added from the remainder.
	 * Also sets the URL suffix for the wiki lookup.
	 *
	 * @param name           the display name of the item requirement
	 * @param itemCollection the {@link ItemCollections} containing item ids and wiki term information
	 */
	public ItemRequirement(String name, ItemCollections itemCollection)
	{
		this(name, itemCollection.getItems().get(0), 1);
		this.setUrlSuffix(itemCollection.getWikiTerm());
		this.addAlternates(itemCollection.getItems().subList(1, itemCollection.getItems().size()));
	}

	/**
	 * Constructs a new ItemRequirement using an {@link ItemCollections} object and a specified quantity.
	 * The primary item id is the first item in the collection, and alternate items are added from the remainder.
	 * Also sets the URL suffix for the wiki lookup.
	 *
	 * @param name           the display name of the item requirement
	 * @param itemCollection the {@link ItemCollections} containing item ids and wiki term information
	 * @param quantity       the required quantity of the item
	 */
	public ItemRequirement(String name, ItemCollections itemCollection, int quantity)
	{
		this(name, itemCollection.getItems().get(0), quantity);
		this.setUrlSuffix(itemCollection.getWikiTerm());
		this.addAlternates(itemCollection.getItems().subList(1, itemCollection.getItems().size()));
	}

	/**
	 * Constructs a new ItemRequirement using an {@link ItemCollections} object, a specified quantity, and an equipment flag.
	 * The primary item id is the first item in the collection, and alternate items are added from the remainder.
	 * Also sets the URL suffix for the wiki lookup.
	 *
	 * @param name           the display name of the item requirement
	 * @param itemCollection the {@link ItemCollections} containing item ids and wiki term information
	 * @param quantity       the required quantity of the item
	 * @param equip          {@code true} if the item must be equipped, {@code false} otherwise
	 */
	public ItemRequirement(String name, ItemCollections itemCollection, int quantity, boolean equip)
	{
		this(name, itemCollection.getItems().get(0), quantity);
		this.setUrlSuffix(itemCollection.getWikiTerm());
		this.equip = equip;
		this.addAlternates(itemCollection.getItems().subList(1, itemCollection.getItems().size()));
	}

	/**
	 * Adds a list of alternate item ids to this requirement.
	 *
	 * @param alternates the list of alternate item ids
	 */
	public void addAlternates(List<Integer> alternates)
	{
		this.alternateItems.addAll(alternates);
	}

	/**
	 * Adds alternate item ids from an {@link ItemCollections} object.
	 *
	 * @param alternates the {@link ItemCollections} containing alternate item ids
	 */
	public void addAlternates(ItemCollections alternates)
	{
		this.alternateItems.addAll(alternates.getItems());
	}

	/**
	 * Adds alternate item ids to this requirement.
	 *
	 * @param alternates an array of alternate item ids
	 */
	public void addAlternates(Integer... alternates)
	{
		this.alternateItems.addAll(Arrays.asList(alternates));
	}

	/**
	 * Determines whether the quantity should be displayed.
	 *
	 * @return {@code true} if the quantity is not -1, {@code false} otherwise
	 */
	public boolean showQuantity()
	{
		return quantity != -1;
	}

	/**
	 * Returns a copy of this requirement with the {@code highlightInInventory} flag set to {@code true}.
	 *
	 * @return a new {@link ItemRequirement} instance with highlighting enabled
	 */
	public ItemRequirement highlighted()
	{
		ItemRequirement newItem = copy();
		newItem.setHighlightInInventory(true);
		return newItem;
	}

	/**
	 * Configures this requirement to check the specified quest bank. Needs to not need {@link QuestBank} in the future as it no longer uses it.
	 *
	 * @param questBank the {@link QuestBank} to use for bank checks; if {@code null}, bank checks are disabled
	 */
	public void useQuestBank(QuestBank questBank)
	{
		this.shouldCheckBank = questBank != null;
		this.questBank = questBank;
	}

	/**
	 * Returns a copy of this requirement that also checks the specified quest bank. Needs to not need {@link QuestBank} in the future as it no longer uses it.
	 *
	 * @param questBank the {@link QuestBank} to use for additional bank checks
	 * @return a new {@link ItemRequirement} instance configured to check the bank
	 */
	public ItemRequirement alsoCheckBank(QuestBank questBank)
	{
		ItemRequirement newItem = copy();
		newItem.useQuestBank(questBank);
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with a new display name.
	 *
	 * @param name the new display name
	 * @return a new {@link ItemRequirement} instance with the updated name
	 */
	public ItemRequirement named(String name)
	{
		ItemRequirement newItem = copy();
		newItem.setName(name);
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with the equipment flag set to {@code true}.
	 *
	 * @return a new {@link ItemRequirement} instance marked as equipped
	 */
	public ItemRequirement equipped()
	{
		ItemRequirement newItem = copy();
		newItem.setEquip(true);
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with the consumed flag set to {@code false}.
	 *
	 * @return a new {@link ItemRequirement} instance that is not considered a consumed item
	 */
	public ItemRequirement isNotConsumed()
	{
		ItemRequirement newItem = copy();
		newItem.isConsumedItem = false;
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with aggregation disabled.
	 *
	 * @return a new {@link ItemRequirement} instance with aggregation turned off
	 */
	public ItemRequirement doNotAggregate()
	{
		ItemRequirement newItem = copy();
		newItem.shouldAggregate = false;
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with an updated required quantity.
	 *
	 * @param newQuantity the new required quantity
	 * @return a new {@link ItemRequirement} instance with the updated quantity
	 */
	public ItemRequirement quantity(int newQuantity)
	{
		ItemRequirement newItem = copy();
		newItem.setQuantity(newQuantity);
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with a condition that hides the requirement if met.
	 *
	 * @param condition the condition that determines whether to hide this requirement
	 * @return a new {@link ItemRequirement} instance with the hide condition applied
	 */
	public ItemRequirement hideConditioned(Requirement condition)
	{
		ItemRequirement newItem = copy();
		newItem.setConditionToHide(condition);
		return newItem;
	}

	/**
	 * Returns a copy of this requirement with a condition that shows the requirement only when not met.
	 *
	 * @param condition the condition to evaluate for showing the requirement
	 * @return a new {@link ItemRequirement} instance with the show condition applied
	 */
	public ItemRequirement showConditioned(Requirement condition)
	{
		ItemRequirement newItem = copy();
		newItem.setConditionToHide(new Conditions(LogicType.NOR, condition));
		return newItem;
	}

	/**
	 * Creates a copy of this requirement.
	 * <p>
	 * Subclasses should override this method to return a copy of their own type.
	 * </p>
	 *
	 * @return a copy of this {@link ItemRequirement}
	 * @throws UnsupportedOperationException if the class is not exactly {@link ItemRequirement}
	 */
	protected ItemRequirement copyOfClass()
	{
		if (this.getClass() != ItemRequirement.class)
		{
			throw new UnsupportedOperationException("Subclasses must override copy()");
		}
		return new ItemRequirement(name, id, quantity, equip);
	}

	/**
	 * Creates a deep copy of this {@link ItemRequirement}, including all alternate items and additional options.
	 *
	 * @return a new {@link ItemRequirement} instance that is a copy of this one
	 */
	public ItemRequirement copy()
	{
		ItemRequirement newItem = copyOfClass();
		newItem.setName(name);
		newItem.setId(id);
		newItem.setEquip(equip);
		newItem.setQuantity(quantity);
		newItem.addAlternates(alternateItems);
		newItem.setDisplayItemId(displayItemId);
		newItem.setHighlightInInventory(highlightInInventory);
		newItem.setDisplayMatchedItemName(displayMatchedItemName);
		newItem.setConditionToHide(conditionToHide);
		newItem.questBank = questBank;
		newItem.isConsumedItem = isConsumedItem;
		newItem.shouldAggregate = shouldAggregate;
		// Need to get actual tooltip or we get the appended containers info
		newItem.setTooltip(tooltip);
		newItem.setUrlSuffix(getUrlSuffix());
		newItem.additionalOptions = additionalOptions;
		newItem.isChargedItem = isChargedItem;
		newItem.shouldCheckBank = shouldCheckBank;

		return newItem;
	}

	/**
	 * Determines if this requirement represents a valid item.
	 *
	 * @return {@code true} if the item id and quantity are valid (not -1), {@code false} otherwise
	 */
	public boolean isActualItem()
	{
		return id != -1 && quantity != -1;
	}

	/**
	 * Appends a tooltip indicating that the item can be obtained during the quest.
	 */
	public void canBeObtainedDuringQuest()
	{
		appendToTooltip("Can be obtained during the quest.");
	}

	/**
	 * Returns the display name of this item requirement.
	 *
	 * @return the name of the item requirement
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Retrieves a list of all item ids associated with this requirement, including alternates.
	 *
	 * @return a {@link List} of unique item ids
	 */
	public List<Integer> getAllIds()
	{
		List<Integer> items = new ArrayList<>(Collections.singletonList(id));
		items.addAll(alternateItems);

		return items.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Generates the overlay display text for this item requirement.
	 *
	 * @param client the {@link Client} for which to generate the display text
	 * @param config the {@link QuestHelperConfig} containing configuration settings
	 * @return a {@link List} of {@link LineComponent} objects representing the display text
	 */
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

	/**
	 * Returns the display text for this item requirement, which includes the quantity (if applicable) and the item name.
	 *
	 * @return the display text for this requirement
	 */
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

	/**
	 * Sets additional options for this requirement.
	 *
	 * @param additionalOptions an additional {@link Requirement} to be applied
	 */
	public void setAdditionalOptions(Requirement additionalOptions)
	{
		this.additionalOptions = additionalOptions;
	}

	/**
	 * Retrieves the wiki URL for this item based on the URL suffix or item id.
	 *
	 * @return the wiki URL as a {@link String}, or {@code null} if not available
	 */
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

	/**
	 * Determines whether the text for this requirement should be displayed,
	 * based on the hide condition.
	 *
	 * @param client the {@link Client} for which to evaluate the condition
	 * @return {@code true} if the text should be displayed, {@code false} otherwise
	 */
	@Override
	public boolean shouldDisplayText(Client client)
	{
		return conditionToHide == null || !conditionToHide.check(client);
	}

	/**
	 * Determines the display color for this item requirement based on the client's state.
	 *
	 * @param client the {@link Client} for which the color is determined
	 * @param config the {@link QuestHelperConfig} containing color configuration
	 * @return the {@link Color} representing the state of the requirement
	 */
	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		Color color = config.failColour();
		if (!this.isActualItem())
		{
			color = Color.GRAY;
		}
		else if (additionalOptions != null && additionalOptions.check(client))
		{
			color = config.passColour();
		}
		else if (this.checkContainersOnPlayer(client))
		{
			color = config.passColour();
		}
		else if (this.checkWithAllContainers())
		{
			color = config.partialSuccessColour();
		}
		return color;
	}

	/**
	 * Generates a tooltip for this item requirement, indicating which containers may have the required items.
	 *
	 * @return the tooltip text as a {@link String}, or {@code null} if not applicable
	 */
	@Override
	public String getTooltip()
	{
		Set<TrackedContainers> containers = getContainersWithItem();
		if (containers.size() == 0) return super.getTooltip();

		if (!getOnPlayerContainers().containsAll(containers))
		{
			return getTooltipFromEnumSet(containers);
		}

		return null;
	}

	/**
	 * Constructs a tooltip message from a set of tracked containers.
	 *
	 * @param containers the set of {@link TrackedContainers} where the item is found
	 * @return a tooltip {@link String} indicating which containers contain the item
	 */
	protected String getTooltipFromEnumSet(Set<TrackedContainers> containers)
	{
		containers.removeAll(getOnPlayerContainers());
		String basicTooltip = super.getTooltip();
		if (basicTooltip == null)
		{
			basicTooltip = "";
		}
		else
		{
			basicTooltip += "\n";
		}

		return basicTooltip + "Items can be found in your: " + containers.stream()
				// Convert enum name to title case
				.map(Text::titleCase)
				.collect(Collectors.joining(", "));
	}

	/**
	 * Finds the first item id from the list of allowed items that the player has,
	 * based on the quantity required.
	 *
	 * @return the matching item id if found; otherwise, -1
	 */
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
			remainder -= (quantity - getNumberOfItemFound(alternate, null));
			if (remainder <= 0)
			{
				return alternate;
			}
		}
		return -1;
	}

	/**
	 * Checks if the provided list of items satisfies this requirement.
	 *
	 * @param client the {@link Client} for which the check is performed
	 * @param items  a {@link List} of {@link Item} objects to check
	 * @return {@code true} if the total matching items meet or exceed the required quantity, {@code false} otherwise
	 */
	public boolean checkItems(Client client, List<Item> items)
	{
		return getMaxMatchingItems(items.toArray(new Item[0])) >= quantity;
	}

	/**
	 * Checks the total number of matching items across the specified containers.
	 *
	 * @param containers an array of {@link ItemAndLastUpdated} representing different item containers
	 * @return the total number of matching items found in the containers
	 */
	public int checkTotalMatchesInContainers(ItemAndLastUpdated... containers)
	{
		int totalFound = 0;

		// Consideration: If any container has changed and a unique item is required,
		// we need to aggregate all the results.
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
				totalFound += getMaxMatchingItems(container.getItems());
			}
			else if (stateForItemInContainer.getLastCheckedTick() <= container.getLastUpdated())
			{
				int matchesInContainer = getMaxMatchingItems(container.getItems());
				// Update the container state (won't represent the actual last checked tick,
				// but ensures it's after the current state for comparison)
				stateForItemInContainer.set(matchesInContainer, container.getLastUpdated() + 1);
				totalFound += matchesInContainer;
			}
			else
			{
				totalFound += stateForItemInContainer.getMatchesFound();
			}
		}

		return totalFound;
	}

	/**
	 * Checks whether the total number of matching items across the specified containers meets the required quantity.
	 *
	 * @param containers an array of {@link ItemAndLastUpdated} representing different item containers
	 * @return {@code true} if the total matches are greater than or equal to the required quantity, {@code false} otherwise
	 */
	public boolean checkContainers(ItemAndLastUpdated... containers)
	{
		return checkTotalMatchesInContainers(containers) >= quantity;
	}

	/**
	 * Checks whether the player has the required items in their equipped and inventory containers.
	 *
	 * @param client the {@link Client} for which to perform the check
	 * @return {@code true} if the required items are found on the player, {@code false} otherwise
	 */
	private boolean checkContainersOnPlayer(Client client)
	{
		return checkContainers(QuestContainerManager.getEquippedData(), QuestContainerManager.getInventoryData());
	}

	/**
	 * Checks whether the total number of matching items across all relevant containers (equipped, inventory, bank, potion, group storage)
	 * meets the required quantity.
	 *
	 * @return {@code true} if the total matches are sufficient, {@code false} otherwise
	 */
	public boolean checkWithAllContainers()
	{
		return checkContainers(QuestContainerManager.getOrderedListOfContainers().toArray(new ItemAndLastUpdated[0]));
	}

	/**
	 * Retrieves a set of tracked containers that contain the required items.
	 * <p>
	 * The method checks the containers in order and returns once the cumulative count meets the required quantity.
	 * </p>
	 *
	 * @return a {@link Set} of {@link TrackedContainers} where the item is present, or an empty set if not enough items are found
	 */
	public Set<TrackedContainers> getContainersWithItem()
	{
		if (!isActualItem())
		{
			return new LinkedHashSet<>();
		}

		Set<TrackedContainers> containersWithItem = new LinkedHashSet<>();
		int totalFoundAcrossContainers = 0;

		// This relies on the order in the enum to determine the priority of container checks.
		for (ItemAndLastUpdated container : QuestContainerManager.getOrderedListOfContainers())
		{
			int totalFoundInCurrentContainer = checkTotalMatchesInContainers(container);
			if (totalFoundInCurrentContainer > 0)
			{
				totalFoundAcrossContainers += totalFoundInCurrentContainer;
				containersWithItem.add(container.getContainerType());
				if (totalFoundAcrossContainers >= quantity)
				{
					return containersWithItem;
				}
			}
		}

		// Not enough items found across containers; return an empty set.
		return new LinkedHashSet<>();
	}

	/**
	 * Generates additional overlay text components for this item requirement, such as equipped status and tooltip.
	 *
	 * @param client         the {@link Client} for which the overlay is generated
	 * @param includeTooltip {@code true} to include tooltip information if applicable
	 * @param config         the {@link QuestHelperConfig} containing display configurations
	 * @return an {@link ArrayList} of {@link LineComponent} objects representing the additional text
	 */
	protected ArrayList<LineComponent> getAdditionalText(Client client, boolean includeTooltip, QuestHelperConfig config)
	{
		Color equipColor = config.passColour();
		ArrayList<LineComponent> lines = new ArrayList<>();

		if (this.isEquip())
		{
			String equipText = "(equipped)";
			if (!checkContainers(QuestContainerManager.getEquippedData()))
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
					.leftColor(Color.GRAY)
					.build());
		}

		return lines;
	}

	/**
	 * Checks whether this item requirement is satisfied for the given client,
	 * taking into account additional options and various containers.
	 *
	 * @param client the {@link Client} for which the check is performed
	 * @return {@code true} if the requirement is met, {@code false} otherwise
	 */
	@Override
	public boolean check(Client client)
	{
		// If additional options are present and pass, then the requirement is met.
		if (additionalOptions != null && additionalOptions.check(client))
		{
			return true;
		}

		List<ItemAndLastUpdated> containers = new ArrayList<>();
		containers.add(QuestContainerManager.getEquippedData());

		if (!equip)
		{
			containers.add(QuestContainerManager.getInventoryData());
		}
		if (shouldCheckBank)
		{
			containers.add(QuestContainerManager.getBankData());
			containers.add(QuestContainerManager.getPotionData());
			containers.add(QuestContainerManager.getGroupStorageData());
		}

		return checkContainers(containers.toArray(new ItemAndLastUpdated[0]));
	}

	/**
	 * Determines the maximum number of matching items from the given array.
	 *
	 * @param items an array of {@link Item} objects to evaluate
	 * @return the total count of matching items found
	 */
	private int getMaxMatchingItems(@NonNull Item[] items)
	{
		List<Item> allItems = new ArrayList<>(List.of(items));

		int foundQuantity = 0;
		List<Integer> ids = getAllIds();
		for (int alternate : ids)
		{
			foundQuantity += getNumberOfItemFound(alternate, allItems);
		}

		return foundQuantity;
	}

	/**
	 * Calculates the number of items found for the specified item id in the given list.
	 * Any value <= 0 indicates the required quantity is not met.
	 *
	 * @param itemID the item id to search for
	 * @param items  a {@link List} of {@link Item} objects; can be {@code null}
	 * @return the total quantity found for the specified item id
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

	/**
	 * Counts the number of matches for a specific item id in the provided list of items.
	 * <p>
	 * If the item is charged, the method sums up the charges; otherwise, it sums the item quantities.
	 * </p>
	 *
	 * @param items  a {@link List} of {@link Item} objects to search through
	 * @param itemID the item id to match
	 * @return the total quantity or charge count for the matching items
	 */
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

	/**
	 * Retrieves the list of item ids to display, prioritizing the display item id if set.
	 *
	 * @return a {@link List} of item ids for display purposes
	 */
	public List<Integer> getDisplayItemIds()
	{
		if (displayItemId == null)
		{
			return getAllIds();
		}

		return Collections.singletonList(displayItemId);
	}

	/**
	 * Determines whether item highlights should be rendered for the client,
	 * based on the hide condition.
	 *
	 * @param client the {@link Client} for which to evaluate the condition
	 * @return {@code true} if highlights should be rendered, {@code false} otherwise
	 */
	public boolean shouldRenderItemHighlights(Client client)
	{
		return conditionToHide == null || !conditionToHide.check(client);
	}

	/**
	 * Determines whether the item should be highlighted in the player's inventory.
	 *
	 * @param client the {@link Client} for which to perform the check
	 * @return {@code true} if highlighting is enabled and conditions are met, {@code false} otherwise
	 */
	public boolean shouldHighlightInInventory(Client client)
	{
		return highlightInInventory && shouldRenderItemHighlights(client);
	}

	/**
	 * Retrieves the set of containers considered as belonging to the player (typically equipped and inventory).
	 *
	 * @return a {@link Set} of {@link TrackedContainers} representing the player's containers
	 */
	// TODO: This may later include additional conditions (e.g., checking for a Rune Pouch on the player)
	protected Set<TrackedContainers> getOnPlayerContainers()
	{
		return new LinkedHashSet<>(List.of(TrackedContainers.EQUIPPED, TrackedContainers.INVENTORY));
	}
}
