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
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.util.Utils;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Item;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a composite item requirement that aggregates multiple
 * {@link ItemRequirement} objects and evaluates them using a specified logical operator (AND/OR).
 */
public class ItemRequirements extends ItemRequirement
{
	/**
	 * The list of item requirements that are aggregated by this composite requirement.
	 */
	@Getter
	private final ArrayList<ItemRequirement> itemRequirements = new ArrayList<>();

	/**
	 * The logical operator used to combine the results of the aggregated item requirements.
	 * Typically, this is either LogicType.AND or LogicType.OR.
	 */
	@Getter
	private final LogicType logicType;

	/**
	 * Constructs an ItemRequirements instance with no name and a default logical operator (AND).
	 *
	 * @param requirements an array of {@link ItemRequirement} objects to aggregate.
	 */
	public ItemRequirements(ItemRequirement... requirements)
	{
		this("", requirements);
	}

	/**
	 * Constructs an ItemRequirements instance with the given name and default logical operator (AND).
	 *
	 * @param name             the name of the item requirement.
	 * @param itemRequirements an array of {@link ItemRequirement} objects to aggregate.
	 * @throws AssertionError if any of the provided requirements is {@code null}.
	 */
	public ItemRequirements(String name, ItemRequirement... itemRequirements)
	{
		// Uses the id of the first requirement to initialize the parent ItemRequirement.
		super(name, itemRequirements[0].getId(), -1);

		// Ensure none of the requirements are null.
		assert (Utils.varargsNotNull(itemRequirements));

		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
		this.logicType = LogicType.AND;
	}

	/**
	 * Constructs an ItemRequirements instance with the given logical type, name, and item requirements.
	 *
	 * @param logicType      the logical operator (AND/OR) to combine the requirements.
	 * @param name           the name of the item requirement.
	 * @param itemRequirements an array of {@link ItemRequirement} objects to aggregate.
	 * @throws AssertionError if any of the provided requirements is {@code null}.
	 */
	public ItemRequirements(LogicType logicType, String name, ItemRequirement... itemRequirements)
	{
		super(name, itemRequirements[0].getId(), -1);

		assert (Utils.varargsNotNull(itemRequirements));

		this.itemRequirements.addAll(Arrays.asList(itemRequirements));
		this.logicType = logicType;
	}

	/**
	 * Constructs an ItemRequirements instance with the given logical type, name, and a list of item requirements.
	 *
	 * @param logicType      the logical operator (AND/OR) to combine the requirements.
	 * @param name           the name of the item requirement.
	 * @param itemRequirements a {@link List} of {@link ItemRequirement} objects to aggregate.
	 * @throws AssertionError if any of the requirements in the list is {@code null}.
	 */
	public ItemRequirements(LogicType logicType, String name, List<ItemRequirement> itemRequirements)
	{
		super(name, itemRequirements.get(0).getId(), -1);

		// Ensure no null requirements are present.
		assert (itemRequirements.stream().noneMatch(Objects::isNull));

		this.itemRequirements.addAll(itemRequirements);
		this.logicType = logicType;
	}

	/**
	 * Constructs an ItemRequirements instance with the given logical type and no name.
	 *
	 * @param logicType    the logical operator (AND/OR) to combine the requirements.
	 * @param requirements an array of {@link ItemRequirement} objects to aggregate.
	 */
	public ItemRequirements(LogicType logicType, ItemRequirement... requirements)
	{
		this(logicType, "", requirements);
	}

	/**
	 * Determines if this composite requirement represents an actual item.
	 * This is the case when there is at least one valid aggregated requirement.
	 *
	 * @return {@code true} if the item is considered "actual" (i.e. valid), {@code false} otherwise.
	 */
	@Override
	public boolean isActualItem()
	{
		// Check if any of the aggregated requirements represent a valid item.
		return getItemRequirements() != null &&
				LogicType.OR.test(getItemRequirements().stream(),
						item -> !item.getAllIds().contains(-1) && item.getQuantity() >= 0);
	}

	/**
	 * Checks whether the aggregated item requirements are satisfied using the provided containers.
	 *
	 * @param containers an array of {@link ItemAndLastUpdated} objects representing the item containers.
	 * @return {@code true} if the number of satisfied requirements meets the criteria defined by {@link LogicType},
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean checkContainers(ItemAndLastUpdated... containers)
	{
		Predicate<ItemRequirement> predicate = r -> r.checkContainers(containers);
		int successes = (int) itemRequirements.stream()
				.filter(Objects::nonNull)
				.filter(predicate)
				.count();
		return logicType.compare(successes, itemRequirements.size());
	}

	/**
	 * Checks whether the aggregated item requirements are satisfied for the specified client.
	 *
	 * @param client the {@link Client} for which to perform the check.
	 * @return {@code true} if the requirements are met based on the {@link LogicType}, {@code false} otherwise.
	 */
	@Override
	public boolean check(Client client)
	{
		Predicate<ItemRequirement> predicate = r -> r.check(client);
		int successes = (int) itemRequirements.stream()
				.filter(Objects::nonNull)
				.filter(predicate)
				.count();
		return logicType.compare(successes, itemRequirements.size());
	}

	/**
	 * Determines the display color for this item requirement based on the client's progress.
	 * <p>
	 * If the item is not valid, it returns {@link Color#GRAY}. Otherwise, it compares the tracked containers
	 * with the player's containers and returns the corresponding color from the configuration.
	 * </p>
	 *
	 * @param client the client for which the color is determined.
	 * @param config the {@link QuestHelperConfig} providing color configuration.
	 * @return the appropriate {@link Color} representing the requirement's state.
	 */
	@Override
	public Color getColor(Client client, QuestHelperConfig config)
	{
		if (!this.isActualItem())
		{
			return Color.GRAY;
		}

		Set<TrackedContainers> containers = getContainersWithItem();

		// If no containers contain the item, return the failure color.
		if (containers.isEmpty())
		{
			return config.failColour();
		}

		// If all required containers are on the player's list, return the pass color.
		if (getOnPlayerContainers().containsAll(containers))
		{
			return config.passColour();
		}

		// Otherwise, return the partial success color.
		return config.partialSuccessColour();
	}

	/**
	 * Finds the best set of tracked containers when using OR logic.
	 * <p>
	 * The method selects a set of containers such that if a perfect match is found (all on player's containers),
	 * that set is immediately returned. Otherwise, it selects the set with the fewest non-player containers.
	 * </p>
	 *
	 * @return a {@link Set} of {@link TrackedContainers} that best matches the requirement, or an empty set if none are found.
	 */
	private Set<TrackedContainers> findBestContainersForOr()
	{
		Set<TrackedContainers> containers = new LinkedHashSet<>();

		for (ItemRequirement itemRequirement : itemRequirements)
		{
			Set<TrackedContainers> currentItemContainers = itemRequirement.getContainersWithItem();
			if (currentItemContainers.isEmpty())
			{
				continue;
			}
			// Found perfect match on player's containers; return immediately.
			if (getOnPlayerContainers().containsAll(currentItemContainers))
			{
				return currentItemContainers;
			}

			// Count the number of containers that are not INVENTORY or EQUIPPED.
			long count = currentItemContainers.stream()
					.filter(container -> container != TrackedContainers.INVENTORY && container != TrackedContainers.EQUIPPED)
					.distinct()
					.count();

			// If this set has fewer non-player containers than the current best, update it.
			if (containers.isEmpty() || count < containers.size())
			{
				containers = currentItemContainers;
			}
		}

		return containers;
	}

	/**
	 * Retrieves the set of tracked containers that currently contain the required items.
	 * <p>
	 * If the logical type is AND, the method aggregates the containers for all requirements.
	 * If OR, it uses {@link #findBestContainersForOr()} to select the best match.
	 * </p>
	 *
	 * @return a {@link Set} of {@link TrackedContainers} containing the items; may be empty if requirements are not met.
	 */
	@Override
	public Set<TrackedContainers> getContainersWithItem()
	{
		if (!isActualItem())
		{
			return new LinkedHashSet<>();
		}

		Set<TrackedContainers> containers = new LinkedHashSet<>();

		if (logicType == LogicType.AND)
		{
			for (ItemRequirement itemRequirement : itemRequirements)
			{
				Set<TrackedContainers> containersForRequirement = itemRequirement.getContainersWithItem();
				// If any requirement fails to find a container, return an empty set.
				if (containersForRequirement.isEmpty())
				{
					return new LinkedHashSet<>();
				}
				containers.addAll(containersForRequirement);
			}
		}
		else if (logicType == LogicType.OR)
		{
			containers = findBestContainersForOr();
		}

		return containers;
	}

	/**
	 * Checks whether the provided list of items satisfies the aggregated item requirements for the given client.
	 *
	 * @param client the {@link Client} for which the check is performed.
	 * @param items  a {@link List} of {@link Item} objects representing the items to check.
	 * @return {@code true} if the check passes based on the logic defined in {@link LogicType}, {@code false} otherwise.
	 */
	@Override
	public boolean checkItems(Client client, List<Item> items)
	{
		Predicate<ItemRequirement> predicate = r -> r.checkItems(client, items);
		int successes = (int) itemRequirements.stream()
				.filter(Objects::nonNull)
				.filter(predicate)
				.count();
		return logicType.compare(successes, itemRequirements.size());
	}

	/**
	 * Creates a deep copy of this {@code ItemRequirements} instance.
	 * <p>
	 * The copy includes all alternate items, display options, and additional options.
	 * </p>
	 *
	 * @return a new {@link ItemRequirement} object that is a copy of this instance.
	 */
	@Override
	public ItemRequirement copy()
	{
		ItemRequirements newItem = new ItemRequirements(getLogicType(), getName(), getItemRequirements());
		newItem.addAlternates(alternateItems);
		newItem.setDisplayItemId(getDisplayItemId());
		newItem.setHighlightInInventory(highlightInInventory);
		newItem.setDisplayMatchedItemName(isDisplayMatchedItemName());
		newItem.setConditionToHide(getConditionToHide());
		newItem.setQuestBank(getQuestBank());
		newItem.setTooltip(tooltip);
		newItem.additionalOptions = additionalOptions;

		return newItem;
	}

	/**
	 * Retrieves a consolidated list of all IDs from the aggregated item requirements.
	 *
	 * @return a {@link List} of {@link Integer} IDs.
	 */
	@Override
	public List<Integer> getAllIds()
	{
		return itemRequirements.stream()
				.map(ItemRequirement::getAllIds)
				.flatMap(Collection::stream)
				.collect(QuestUtil.collectToArrayList());
	}

	/**
	 * Marks all aggregated item requirements as equipped and returns a new instance with the equipped flag set.
	 *
	 * @return a new {@link ItemRequirement} instance with the equip flag set.
	 */
	@Override
	public ItemRequirement equipped()
	{
		ItemRequirements newItem = (ItemRequirements) copy();

		// Set the equip flag on all aggregated requirements.
		List<ItemRequirement> reqs = newItem.itemRequirements;
		ArrayList<ItemRequirement> newReqs = new ArrayList<>();
		for (ItemRequirement req : reqs)
		{
			newReqs.add(req.equipped());
		}
		newItem.itemRequirements.clear();
		newItem.itemRequirements.addAll(newReqs);
		newItem.equip = true;
		return newItem;
	}

	/**
	 * Sets the equip flag for all aggregated item requirements.
	 *
	 * @param shouldEquip {@code true} to mark the items as equipped; {@code false} otherwise.
	 */
	@Override
	public void setEquip(boolean shouldEquip)
	{
		itemRequirements.forEach(itemRequirement -> itemRequirement.setEquip(true));
		equip = shouldEquip;
	}

	/**
	 * Generates a tooltip for this item requirement.
	 * <p>
	 * For AND logic, it checks across all containers. For OR logic, it attempts to find one which passes,
	 * favoring those with the most items already in the player's inventory or equipped.
	 * </p>
	 *
	 * @return a tooltip {@link String} if applicable; otherwise, {@code null} or the parent's tooltip.
	 */
	@Override
	public String getTooltip()
	{
		// Determine the set of containers that contain the required item(s).
		Set<TrackedContainers> containers = getContainersWithItem();

		// If no containers are found, use the default tooltip.
		if (containers.isEmpty())
		{
			return super.getTooltip();
		}

		// If not all required containers are on the player's list, generate a custom tooltip.
		if (!getOnPlayerContainers().containsAll(containers))
		{
			return getTooltipFromEnumSet(containers);
		}

		return null;
	}

	/**
	 * Checks whether the aggregated item requirements are satisfied across all available containers.
	 *
	 * @return {@code true} if all container checks pass based on the logic defined by {@link LogicType}, {@code false} otherwise.
	 */
	@Override
	public boolean checkWithAllContainers()
	{
		return logicType.test(getItemRequirements().stream(), ItemRequirement::checkWithAllContainers);
	}
}
