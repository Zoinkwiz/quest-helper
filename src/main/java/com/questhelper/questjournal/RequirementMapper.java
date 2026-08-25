/*
 * Copyright (c) 2026, nanopink
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import com.questhelper.managers.ItemAndLastUpdated;
import com.questhelper.managers.QuestContainerManager;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.item.TrackedContainers;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.Skill;

import static com.questhelper.questjournal.CatalogMapper.normalizeText;

/** Maps Quest Helper requirements to journal snapshots. */
@Singleton
class RequirementMapper
{
	private final Client client;
	private final QuestHelperConfig config;

	@Inject
	RequirementMapper(Client client, QuestHelperConfig config)
	{
		this.client = Objects.requireNonNull(client, "client");
		this.config = Objects.requireNonNull(config, "config");
	}

	Session newSession()
	{
		return new Session();
	}

	private JournalSnapshot.RequirementState requirementState(
		Requirement requirement,
		Color displayColor,
		Set<TrackedContainers> itemContainers)
	{
		if (requirement instanceof ItemRequirement)
		{
			return itemRequirementState(
				(ItemRequirement) requirement,
				displayColor,
				itemContainers);
		}
		JournalSnapshot.RequirementState colorState = configuredColorState(displayColor);
		if (colorState != JournalSnapshot.RequirementState.UNKNOWN)
		{
			return colorState;
		}
		try
		{
			return requirement.check(client)
				? JournalSnapshot.RequirementState.MET
				: JournalSnapshot.RequirementState.UNMET;
		}
		catch (RuntimeException ignored)
		{
			return JournalSnapshot.RequirementState.UNKNOWN;
		}
	}

	private JournalSnapshot.RequirementState itemRequirementState(
		ItemRequirement requirement,
		Color displayColor,
		Set<TrackedContainers> itemContainers)
	{
		try
		{
			JournalSnapshot.RequirementState colorState = configuredColorState(displayColor);
			if (colorState == JournalSnapshot.RequirementState.MET
				|| requirement instanceof NoItemRequirement)
			{
				return colorState;
			}
			if (!requirement.isActualItem())
			{
				return colorState;
			}
			if (requirement instanceof ItemRequirements)
			{
				ItemRequirements composite = (ItemRequirements) requirement;
				boolean onPlayer = composite.checkContainers(
					QuestContainerManager.getEquippedData(),
					QuestContainerManager.getInventoryData());
				boolean allContainers = composite.checkWithAllContainers();
				return itemContainerState(
					onPlayer,
					allContainers,
					itemContainers.contains(TrackedContainers.GROUP_STORAGE),
					!itemContainers.isEmpty());
			}

			int required = Math.max(1, requirement.getQuantity());
			int onPlayer = requirement.checkTotalMatchesInContainers(
				QuestContainerManager.getEquippedData(),
				QuestContainerManager.getInventoryData());
			int total = requirement.checkTotalMatchesInContainers(
				QuestContainerManager.getOrderedListOfContainers().toArray(
					new ItemAndLastUpdated[0]));
			return itemContainerState(
				onPlayer >= required,
				total >= required,
				itemContainers.contains(TrackedContainers.GROUP_STORAGE),
				total > 0);
		}
		catch (RuntimeException ignored)
		{
			return JournalSnapshot.RequirementState.UNKNOWN;
		}
	}

	static JournalSnapshot.RequirementState itemContainerState(
		boolean onPlayerComplete,
		boolean allContainersComplete,
		boolean includesGroupStorage,
		boolean hasSomeItems)
	{
		if (onPlayerComplete)
		{
			return JournalSnapshot.RequirementState.MET;
		}
		if (allContainersComplete)
		{
			return includesGroupStorage
				? JournalSnapshot.RequirementState.GROUP_BANKED
				: JournalSnapshot.RequirementState.BANKED;
		}
		return hasSomeItems
			? JournalSnapshot.RequirementState.PARTIAL
			: JournalSnapshot.RequirementState.UNMET;
	}

	JournalSnapshot.RequirementState configuredColorState(Color color)
	{
		if (color == null)
		{
			return JournalSnapshot.RequirementState.UNKNOWN;
		}
		if (color.equals(config.passColour()))
		{
			return JournalSnapshot.RequirementState.MET;
		}
		if (color.equals(config.boostColour()))
		{
			return JournalSnapshot.RequirementState.BOOSTABLE;
		}
		if (color.equals(config.partialSuccessColour()))
		{
			return JournalSnapshot.RequirementState.PARTIAL;
		}
		if (color.equals(config.failColour()))
		{
			return JournalSnapshot.RequirementState.UNMET;
		}
		return JournalSnapshot.RequirementState.UNKNOWN;
	}

	Set<TrackedContainers> itemLocations(ItemRequirement requirement)
	{
		if (requirement == null
			|| requirement instanceof NoItemRequirement
			|| !requirement.isActualItem())
		{
			return Collections.emptySet();
		}
		try
		{
			Set<TrackedContainers> satisfying = requirement.getContainersWithItem();
			if (!satisfying.isEmpty())
			{
				return new LinkedHashSet<>(satisfying);
			}
			Set<TrackedContainers> partial = new LinkedHashSet<>();
			if (requirement instanceof ItemRequirements)
			{
				for (ItemRequirement child : ((ItemRequirements) requirement).getItemRequirements())
				{
					partial.addAll(itemLocations(child));
				}
				return partial;
			}
			for (ItemAndLastUpdated container : QuestContainerManager.getOrderedListOfContainers())
			{
				if (requirement.checkTotalMatchesInContainers(container) > 0)
				{
					partial.add(container.getContainerType());
				}
			}
			return partial;
		}
		catch (RuntimeException ignored)
		{
			return Collections.emptySet();
		}
	}

	static Integer selectItemIconId(
		List<Integer> displayItemIds,
		List<Integer> allItemIds,
		Predicate<Integer> hasItem)
	{
		Objects.requireNonNull(hasItem, "hasItem");
		List<Integer> displayCandidates = validItemIds(displayItemIds);
		List<Integer> allCandidates = validItemIds(allItemIds);
		for (Integer itemId : displayCandidates)
		{
			if (hasItem.test(itemId))
			{
				return itemId;
			}
		}
		for (Integer itemId : allCandidates)
		{
			if (hasItem.test(itemId))
			{
				return itemId;
			}
		}
		if (!displayCandidates.isEmpty())
		{
			return displayCandidates.get(0);
		}
		return allCandidates.isEmpty() ? null : allCandidates.get(0);
	}

	private static List<Integer> validItemIds(List<Integer> itemIds)
	{
		if (itemIds == null)
		{
			return Collections.emptyList();
		}
		return itemIds.stream()
			.filter(Objects::nonNull)
			.filter(itemId -> itemId >= 0)
			.distinct()
			.collect(Collectors.toList());
	}

	private JournalSnapshot.Requirement buildRequirementView(
		Requirement requirement,
		String text,
		Predicate<Integer> hasTrackedItem)
	{
		Color displayColor = requirementDisplayColor(requirement);
		Set<TrackedContainers> itemContainers = requirement instanceof ItemRequirement
			? itemLocations((ItemRequirement) requirement)
			: Collections.emptySet();
		JournalSnapshot.RequirementState state = requirementState(
			requirement,
			displayColor,
			itemContainers);
		List<JournalSnapshot.ItemLocation> locations = itemContainers.isEmpty()
			? Collections.emptyList()
			: itemContainers.stream()
				.map(RequirementMapper::itemLocation)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());
		String linkedQuestId = "";
		String linkedQuestTitle = "";
		if (requirement instanceof QuestRequirement)
		{
			QuestHelperQuest linked = ((QuestRequirement) requirement).getQuest();
			if (linked != null)
			{
				linkedQuestId = linked.name();
				linkedQuestTitle = normalizeText(linked.getName());
			}
		}
		JournalSnapshot.IconIdentity icon = requirementIcon(requirement, hasTrackedItem);
		String helpText = safeText(requirement::getTooltip);
		String wikiUrl = "";
		try
		{
			String value = requirement.getWikiUrl();
			wikiUrl = value == null ? "" : value.trim();
		}
		catch (RuntimeException ignored)
		{
			// Missing Wiki URLs do not hide requirements.
		}

		return new JournalSnapshot.Requirement(
			text,
			state,
			displayColor == null ? null : displayColor.getRGB(),
			locations,
			linkedQuestId,
			linkedQuestTitle,
			icon,
			wikiUrl,
			helpText);
	}

	private Color requirementDisplayColor(Requirement requirement)
	{
		try
		{
			return requirement.getColor(client, config);
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	private JournalSnapshot.IconIdentity requirementIcon(
		Requirement requirement,
		Predicate<Integer> hasTrackedItem)
	{
		if (requirement instanceof QuestRequirement)
		{
			return JournalSnapshot.IconIdentity.quest();
		}
		if (requirement instanceof SkillRequirement)
		{
			Skill skill = ((SkillRequirement) requirement).getSkill();
			return skill == null
				? JournalSnapshot.IconIdentity.none()
				: JournalSnapshot.IconIdentity.skill(skill.name());
		}
		if (requirement instanceof ItemRequirement)
		{
			ItemRequirement item = (ItemRequirement) requirement;
			Integer itemId = selectItemIconId(
				item.getDisplayItemIds(),
				item.getAllIds(),
				hasTrackedItem);
			if (itemId != null)
			{
				return JournalSnapshot.IconIdentity.item(
					itemId,
					Math.max(1, item.getQuantity()));
			}
		}
		return JournalSnapshot.IconIdentity.none();
	}

	private Set<Integer> collectTrackedItemIds()
	{
		Set<Integer> itemIds = new HashSet<>();
		for (ItemAndLastUpdated container : QuestContainerManager.getOrderedListOfContainers())
		{
			if (container == null || container.getItems() == null)
			{
				continue;
			}
			for (Item item : container.getItems())
			{
				if (item != null && item.getId() >= 0 && item.getQuantity() > 0)
				{
					itemIds.add(item.getId());
				}
			}
		}
		return itemIds;
	}

	private static String safeText(Supplier<String> supplier)
	{
		try
		{
			return normalizeText(supplier.get());
		}
		catch (RuntimeException ignored)
		{
			return "";
		}
	}

	private static JournalSnapshot.ItemLocation itemLocation(TrackedContainers location)
	{
		if (location == null)
		{
			return null;
		}
		try
		{
			return JournalSnapshot.ItemLocation.valueOf(location.name());
		}
		catch (IllegalArgumentException ignored)
		{
			return null;
		}
	}

	final class Session
	{
		private final Map<Requirement, JournalSnapshot.Requirement> views =
			new IdentityHashMap<>();
		private Set<Integer> trackedItemIds;

		List<JournalSnapshot.Requirement> buildViews(List<? extends Requirement> requirements)
		{
			if (requirements == null || requirements.isEmpty())
			{
				return Collections.emptyList();
			}
			Set<Requirement> seen = Collections.newSetFromMap(new IdentityHashMap<>());
			List<Requirement> ordered = requirements.stream()
				.filter(Objects::nonNull)
				.filter(seen::add)
				.sorted((left, right) -> Boolean.compare(
					right instanceof QuestRequirement,
					left instanceof QuestRequirement))
				.collect(Collectors.toList());
			List<JournalSnapshot.Requirement> result = new ArrayList<>();
			for (Requirement requirement : ordered)
			{
				JournalSnapshot.Requirement view = build(requirement);
				if (view != null)
				{
					result.add(view);
				}
			}
			return result;
		}

		JournalSnapshot.Requirement build(Requirement requirement)
		{
			if (requirement == null)
			{
				return null;
			}
			if (views.containsKey(requirement))
			{
				return views.get(requirement);
			}
			try
			{
				if (!requirement.shouldDisplayText(client))
				{
					views.put(requirement, null);
					return null;
				}
			}
			catch (RuntimeException ignored)
			{
				// Keep the requirement when visibility cannot be checked.
			}
			String text = safeText(requirement::getDisplayText);
			if (text.isEmpty())
			{
				views.put(requirement, null);
				return null;
			}
			JournalSnapshot.Requirement view = build(requirement, text);
			views.put(requirement, view);
			return view;
		}

		JournalSnapshot.Requirement build(Requirement requirement, String text)
		{
			return RequirementMapper.this.buildRequirementView(
				requirement,
				text,
				this::hasTrackedItem);
		}

		private boolean hasTrackedItem(int itemId)
		{
			if (trackedItemIds == null)
			{
				trackedItemIds = collectTrackedItemIds();
			}
			return trackedItemIds.contains(itemId);
		}
	}
}
