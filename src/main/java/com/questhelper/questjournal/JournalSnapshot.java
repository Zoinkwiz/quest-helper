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
package com.questhelper.questjournal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Immutable view data for one rendering of the quest journal.
 */
@Getter
public final class JournalSnapshot
{
	private final List<QuestListItem> quests;
	private final SelectedQuest selectedQuest;
	private final ActiveQuest activeQuest;
	private final QuestListOptions listOptions;
	private final QuestProgress questProgress;

	public JournalSnapshot(
		List<QuestListItem> quests,
		SelectedQuest selectedQuest,
		ActiveQuest activeQuest,
		QuestListOptions listOptions,
		QuestProgress questProgress)
	{
		this.quests = immutableCopy(quests, "quests");
		this.selectedQuest = selectedQuest;
		this.activeQuest = activeQuest;
		this.listOptions = Objects.requireNonNull(listOptions, "listOptions");
		this.questProgress = Objects.requireNonNull(questProgress, "questProgress");
	}

	private static <T> List<T> immutableCopy(List<? extends T> values, String name)
	{
		Objects.requireNonNull(values, name);
		List<T> copy = new ArrayList<>(values.size());
		for (T value : values)
		{
			copy.add(Objects.requireNonNull(value, name + " cannot contain null values"));
		}
		return Collections.unmodifiableList(copy);
	}

	private static <T> Set<T> immutableSetCopy(Set<? extends T> values, String name)
	{
		Objects.requireNonNull(values, name);
		Set<T> copy = new LinkedHashSet<>();
		for (T value : values)
		{
			copy.add(Objects.requireNonNull(value, name + " cannot contain null values"));
		}
		return Collections.unmodifiableSet(copy);
	}

	private static String requiredText(String value, String name)
	{
		String text = Objects.requireNonNull(value, name).trim();
		if (text.isEmpty())
		{
			throw new IllegalArgumentException(name + " cannot be blank");
		}
		return text;
	}

	private static String optionalText(String value)
	{
		return value == null ? "" : value.trim();
	}

	public enum QuestState
	{
		NOT_STARTED,
		IN_PROGRESS,
		COMPLETE
	}

	public enum QuestDifficulty
	{
		NOVICE,
		EASY,
		INTERMEDIATE,
		MEDIUM,
		EXPERIENCED,
		HARD,
		MASTER,
		ELITE,
		GRANDMASTER,
		SPECIAL
	}

	/** The kind of helper, kept separate from a quest's actual difficulty. */
	public enum QuestType
	{
		QUEST,
		MINIQUEST,
		ACHIEVEMENT_DIARY,
		GENERIC,
		SKILL,
		PLAYER_QUEST
	}

	/** Membership classification of a helper. */
	public enum QuestMembership
	{
		FREE_TO_PLAY,
		MEMBERS
	}

	public enum QuestOrder
	{
		A_TO_Z,
		Z_TO_A,
		OPTIMAL,
		OPTIMAL_IRONMAN,
		RELEASE_DATE,
		QUEST_POINTS_ASC,
		QUEST_POINTS_DESC
	}

	public enum ObjectiveState
	{
		LOCKED,
		AVAILABLE,
		FADED,
		COMPLETE
	}

	public enum RequirementState
	{
		MET,
		BANKED,
		GROUP_BANKED,
		PARTIAL,
		BOOSTABLE,
		UNMET,
		UNKNOWN
	}

	public enum IconType
	{
		NONE,
		QUEST,
		ITEM,
		SKILL,
		QUEST_POINTS
	}

	@Getter
	@EqualsAndHashCode
	public static final class IconIdentity
	{
		private static final IconIdentity NONE = new IconIdentity(IconType.NONE, null, "", 1);
		private static final IconIdentity QUEST = new IconIdentity(IconType.QUEST, null, "", 1);
		private static final IconIdentity QUEST_POINTS =
			new IconIdentity(IconType.QUEST_POINTS, null, "", 1);

		private final IconType type;
		private final Integer itemId;
		private final String skill;
		private final int quantity;

		private IconIdentity(IconType type, Integer itemId, String skill, int quantity)
		{
			this.type = Objects.requireNonNull(type, "type");
			this.itemId = itemId;
			this.skill = optionalText(skill);
			this.quantity = quantity;
			if ((type == IconType.ITEM) != (itemId != null))
			{
				throw new IllegalArgumentException("Only item icons can have an item ID");
			}
			if (itemId != null && itemId < 0)
			{
				throw new IllegalArgumentException("itemId cannot be negative");
			}
			if (quantity < 1)
			{
				throw new IllegalArgumentException("quantity must be positive");
			}
			if (type != IconType.ITEM && quantity != 1)
			{
				throw new IllegalArgumentException("Only item icons can have a quantity");
			}
			if ((type == IconType.SKILL) == this.skill.isEmpty())
			{
				throw new IllegalArgumentException("Only skill icons can have a skill name");
			}
		}

		public static IconIdentity none()
		{
			return NONE;
		}

		public static IconIdentity quest()
		{
			return QUEST;
		}

		public static IconIdentity item(int itemId, int quantity)
		{
			return new IconIdentity(IconType.ITEM, itemId, "", quantity);
		}

		public static IconIdentity skill(String skill)
		{
			return new IconIdentity(IconType.SKILL, null, requiredText(skill, "skill"), 1);
		}

		public static IconIdentity questPoints()
		{
			return QUEST_POINTS;
		}

	}

	@Getter
	public static final class QuestProgress
	{
		private final int completedQuestCount;
		private final int totalQuestCount;
		private final int currentQuestPoints;
		private final int totalQuestPoints;

		public QuestProgress(
			int completedQuestCount,
			int totalQuestCount,
			int currentQuestPoints,
			int totalQuestPoints)
		{
			if (completedQuestCount < 0
				|| totalQuestCount < completedQuestCount
				|| currentQuestPoints < 0
				|| totalQuestPoints < currentQuestPoints)
			{
				throw new IllegalArgumentException("Quest progress values are inconsistent");
			}
			this.completedQuestCount = completedQuestCount;
			this.totalQuestCount = totalQuestCount;
			this.currentQuestPoints = currentQuestPoints;
			this.totalQuestPoints = totalQuestPoints;
		}

	}

	/** The Quest Helper quest currently driving live guidance. */
	@Getter
	@EqualsAndHashCode
	public static final class ActiveQuest
	{
		private final String id;
		private final String title;
		private final QuestState state;

		public ActiveQuest(String id, String title, QuestState state)
		{
			this.id = requiredText(id, "id");
			this.title = requiredText(title, "title");
			this.state = Objects.requireNonNull(state, "state");
		}

	}

	@Getter
	public enum ItemLocation
	{
		EQUIPPED("Equipped"),
		INVENTORY("Inventory"),
		BANK("Bank"),
		POTION_STORAGE("Potion storage"),
		GROUP_STORAGE("Group storage"),
		RUNE_POUCH("Rune pouch"),
		KEY_RING("Key ring"),
		UNDEFINED("Unknown container");

		private final String displayName;

		ItemLocation(String displayName)
		{
			this.displayName = displayName;
		}

	}

	public static final class QuestListItem
	{
		@Getter
		private final String id;
		@Getter
		private final String title;
		private final String normalizedTitle;
		@Getter
		private final QuestType type;
		@Getter
		private final QuestState state;
		@Getter
		private final QuestDifficulty difficulty;
		@Getter
		private final boolean members;
		@Getter
		private final Map<QuestOrder, Integer> orderRanks;

		public QuestListItem(
			String id,
			String title,
			QuestType type,
			QuestState state,
			QuestDifficulty difficulty,
			boolean members,
			Map<QuestOrder, Integer> orderRanks)
		{
			this.id = requiredText(id, "id");
			this.title = requiredText(title, "title");
			this.normalizedTitle = this.title.toLowerCase(Locale.ROOT);
			this.type = Objects.requireNonNull(type, "type");
			this.state = Objects.requireNonNull(state, "state");
			this.difficulty = Objects.requireNonNull(difficulty, "difficulty");
			this.members = members;
			Objects.requireNonNull(orderRanks, "orderRanks");
			EnumMap<QuestOrder, Integer> ranks = new EnumMap<>(QuestOrder.class);
			for (Map.Entry<QuestOrder, Integer> entry : orderRanks.entrySet())
			{
				QuestOrder order = Objects.requireNonNull(entry.getKey(), "orderRanks key");
				Integer rank = Objects.requireNonNull(entry.getValue(), "orderRanks value");
				if (rank < 0)
				{
					throw new IllegalArgumentException("order rank cannot be negative");
				}
				ranks.put(order, rank);
			}
			this.orderRanks = Collections.unmodifiableMap(ranks);
		}

		public QuestMembership getMembership()
		{
			return members ? QuestMembership.MEMBERS : QuestMembership.FREE_TO_PLAY;
		}

		public int getOrderRank(QuestOrder order)
		{
			Integer rank = orderRanks.get(Objects.requireNonNull(order, "order"));
			return rank == null ? Integer.MAX_VALUE : rank;
		}
	}

	public static final class QuestListOptions
	{
		private static final List<QuestType> TYPES = Collections.unmodifiableList(
			java.util.Arrays.asList(QuestType.values()));
		private static final List<QuestDifficulty> DIFFICULTIES = Collections.unmodifiableList(
			java.util.Arrays.asList(QuestDifficulty.values()));
		private static final List<QuestMembership> MEMBERSHIPS = Collections.unmodifiableList(
			java.util.Arrays.asList(QuestMembership.values()));
		private static final List<QuestOrder> ORDERS = Collections.unmodifiableList(
			java.util.Arrays.asList(QuestOrder.values()));
		private static final Set<QuestType> ALL_TYPES = immutableSetCopy(
			EnumSet.allOf(QuestType.class), "types");
		private static final Set<QuestDifficulty> ALL_DIFFICULTIES =
			immutableSetCopy(EnumSet.allOf(QuestDifficulty.class), "difficulties");
		private static final Set<QuestMembership> ALL_MEMBERSHIPS =
			immutableSetCopy(EnumSet.allOf(QuestMembership.class), "memberships");
		private static final QuestListOptions DEFAULTS =
			new QuestListOptions(null, QuestOrder.OPTIMAL);

		@Getter
		private final Set<QuestMembership> configuredMemberships;
		@Getter
		private final QuestOrder configuredOrder;

		public QuestListOptions(
			QuestMembership configuredMembership,
			QuestOrder configuredOrder)
		{
			this.configuredMemberships = configuredMembership == null
				? ALL_MEMBERSHIPS : Collections.singleton(configuredMembership);
			this.configuredOrder = Objects.requireNonNull(configuredOrder, "configuredOrder");
		}

		public static QuestListOptions defaults()
		{
			return DEFAULTS;
		}

		public List<QuestType> getTypes()
		{
			return TYPES;
		}

		public List<QuestDifficulty> getDifficulties()
		{
			return DIFFICULTIES;
		}

		public List<QuestOrder> getOrders()
		{
			return ORDERS;
		}

		public List<QuestMembership> getMemberships()
		{
			return MEMBERSHIPS;
		}

		public Set<QuestType> getConfiguredTypes()
		{
			return ALL_TYPES;
		}

		public Set<QuestDifficulty> getConfiguredDifficulties()
		{
			return ALL_DIFFICULTIES;
		}

	}

	@Getter
	@EqualsAndHashCode
	public static final class QuestOverview
	{
		private final String id;
		private final String title;
		private final QuestType type;
		private final QuestState state;
		private final QuestDifficulty difficulty;
		private final boolean members;

		public QuestOverview(
			String id,
			String title,
			QuestType type,
			QuestState state,
			QuestDifficulty difficulty,
			boolean members)
		{
			this.id = requiredText(id, "id");
			this.title = requiredText(title, "title");
			this.type = Objects.requireNonNull(type, "type");
			this.state = Objects.requireNonNull(state, "state");
			this.difficulty = Objects.requireNonNull(difficulty, "difficulty");
			this.members = members;
		}

	}

	@Getter
	@EqualsAndHashCode
	public static final class Objective
	{
		private final String sectionId;
		private final String section;
		private final String text;
		private final ObjectiveState state;
		private final boolean current;
		private final List<Requirement> sectionRequirements;

		public Objective(
			String sectionId,
			String section,
			String text,
			ObjectiveState state,
			boolean current,
			List<Requirement> sectionRequirements)
		{
			this.sectionId = optionalText(sectionId);
			this.section = optionalText(section);
			this.text = requiredText(text, "text");
			this.state = Objects.requireNonNull(state, "state");
			this.current = current;
			this.sectionRequirements = immutableCopy(
				sectionRequirements,
				"sectionRequirements");
			if (!this.sectionRequirements.isEmpty() && this.sectionId.isEmpty())
			{
				throw new IllegalArgumentException(
					"objective sections with requirements need a stable id");
			}
		}

	}

	@Getter
	@EqualsAndHashCode
	public static final class Requirement
	{
		private final String text;
		private final RequirementState state;
		/** Quest Helper's configured ARGB display color, when supplied. */
		private final Integer displayColor;
		private final List<ItemLocation> locations;
		private final String linkedQuestId;
		private final String linkedQuestTitle;
		private final IconIdentity icon;
		/** An approved OSRS Wiki HTTPS destination, or an empty string. */
		private final String wikiUrl;
		/** Quest Helper's own extra guidance for this requirement. */
		private final String helpText;

		public Requirement(
			String text,
			RequirementState state,
			Integer displayColor,
			List<ItemLocation> locations,
			String linkedQuestId,
			String linkedQuestTitle,
			IconIdentity icon,
			String wikiUrl,
			String helpText)
		{
			this.text = requiredText(text, "text");
			this.state = Objects.requireNonNull(state, "state");
			this.displayColor = displayColor;
			this.locations = immutableCopy(locations, "locations");
			this.linkedQuestId = optionalText(linkedQuestId);
			this.linkedQuestTitle = optionalText(linkedQuestTitle);
			this.icon = Objects.requireNonNull(icon, "icon");
			this.wikiUrl = validatedWikiUrl(wikiUrl);
			this.helpText = optionalText(helpText);
			if (this.linkedQuestId.isEmpty() != this.linkedQuestTitle.isEmpty())
			{
				throw new IllegalArgumentException(
					"linkedQuestId and linkedQuestTitle must either both be set or both be blank");
			}
		}

		public String getLocationHint()
		{
			return locations.isEmpty() ? "" : "Located in: " + locations.stream()
				.map(ItemLocation::getDisplayName)
				.collect(Collectors.joining(", "));
		}

		public boolean hasLinkedQuest()
		{
			return !linkedQuestId.isEmpty();
		}

		public boolean hasWikiUrl()
		{
			return !wikiUrl.isEmpty();
		}

		private static String validatedWikiUrl(String value)
		{
			String url = optionalText(value);
			if (url.isEmpty())
			{
				return "";
			}
			try
			{
				if (url.chars().anyMatch(Character::isISOControl))
				{
					return "";
				}
				int authorityStart = url.indexOf("://") + 3;
				if (authorityStart < 3)
				{
					return "";
				}
				int authorityEnd = url.length();
				for (int index = authorityStart; index < url.length(); index++)
				{
					char character = url.charAt(index);
					if (character == '/' || character == '?' || character == '#')
					{
						authorityEnd = index;
						break;
					}
				}
				URI uri = URI.create(url.substring(0, authorityEnd));
				return "https".equalsIgnoreCase(uri.getScheme())
					&& "oldschool.runescape.wiki".equalsIgnoreCase(uri.getHost())
					&& uri.getUserInfo() == null
					&& (uri.getPort() == -1 || uri.getPort() == 443)
					? url
					: "";
			}
			catch (IllegalArgumentException ignored)
			{
				return "";
			}
		}
	}

	@Getter
	@EqualsAndHashCode
	public static final class Reward
	{
		private final String detail;
		private final IconIdentity icon;

		public Reward(String detail, IconIdentity icon)
		{
			this.detail = requiredText(detail, "detail");
			this.icon = Objects.requireNonNull(icon, "icon");
		}

	}

	@Getter
	@EqualsAndHashCode
	public static final class SelectedQuest
	{
		private final QuestOverview overview;
		private final List<Objective> objectives;
		private final List<Requirement> requirements;
		private final List<Requirement> recommendations;
		private final List<String> enemies;
		private final List<Reward> rewards;
		private final List<String> notes;

		public SelectedQuest(
			QuestOverview overview,
			List<Objective> objectives,
			List<Requirement> requirements,
			List<Requirement> recommendations,
			List<String> enemies,
			List<Reward> rewards,
			List<String> notes)
		{
			this.overview = Objects.requireNonNull(overview, "overview");
			this.objectives = immutableCopy(objectives, "objectives");
			this.requirements = immutableCopy(requirements, "requirements");
			this.recommendations = immutableCopy(recommendations, "recommendations");
			this.enemies = immutableCopy(enemies, "enemies");
			this.rewards = immutableCopy(rewards, "rewards");
			this.notes = immutableCopy(notes, "notes");
		}

	}

	/** Immutable quest-list filters. Starred quests are matched with selected types. */
	public static final class QuestFilter
	{
		private static final QuestFilter ALL = new QuestFilter(
			"",
			new LinkedHashSet<>(java.util.Arrays.asList(QuestState.values())),
			new LinkedHashSet<>(java.util.Arrays.asList(QuestType.values())),
			true,
			Collections.emptySet(),
			new LinkedHashSet<>(java.util.Arrays.asList(QuestDifficulty.values())),
			new LinkedHashSet<>(java.util.Arrays.asList(QuestMembership.values())),
			QuestOrder.A_TO_Z);

		@Getter
		private final String searchText;
		private final String normalizedSearchText;
		@Getter
		private final Set<QuestState> states;
		@Getter
		private final Set<QuestType> types;
		@Getter
		private final boolean starredSelected;
		private final Set<String> starredQuestIds;
		@Getter
		private final Set<QuestDifficulty> difficulties;
		private final boolean allDifficultiesSelected;
		@Getter
		private final Set<QuestMembership> memberships;
		@Getter
		private final QuestOrder order;

		private QuestFilter(
			String searchText,
			Set<QuestState> states,
			Set<QuestType> types,
			boolean starredSelected,
			Set<String> starredQuestIds,
			Set<QuestDifficulty> difficulties,
			Set<QuestMembership> memberships,
			QuestOrder order)
		{
			this.searchText = searchText == null ? "" : searchText;
			this.normalizedSearchText = this.searchText.trim().toLowerCase(Locale.ROOT);
			this.states = immutableSetCopy(states, "states");
			this.types = immutableSetCopy(types, "types");
			this.starredSelected = starredSelected;
			this.starredQuestIds = immutableSetCopy(starredQuestIds, "starredQuestIds");
			this.difficulties = immutableSetCopy(difficulties, "difficulties");
			this.allDifficultiesSelected =
				this.difficulties.size() == QuestDifficulty.values().length;
			this.memberships = immutableSetCopy(memberships, "memberships");
			this.order = Objects.requireNonNull(order, "order");
		}

		public static QuestFilter all()
		{
			return ALL;
		}

		public QuestFilter withSearchText(String value)
		{
			return new QuestFilter(value, states, types, starredSelected,
				starredQuestIds, difficulties, memberships, order);
		}

		public QuestFilter withStateSelections(Set<QuestState> values)
		{
			return new QuestFilter(searchText, values, types, starredSelected,
				starredQuestIds, difficulties, memberships, order);
		}

		public QuestFilter withTypeSelections(Set<QuestType> values)
		{
			return new QuestFilter(searchText, states, values, starredSelected,
				starredQuestIds, difficulties, memberships, order);
		}

		public QuestFilter withStarredSelected(boolean value)
		{
			return new QuestFilter(searchText, states, types, value,
				starredQuestIds, difficulties, memberships, order);
		}

		public QuestFilter withStarredQuestIds(Set<String> values)
		{
			return new QuestFilter(searchText, states, types, starredSelected,
				values, difficulties, memberships, order);
		}

		public QuestFilter withDifficultySelections(Set<QuestDifficulty> values)
		{
			return new QuestFilter(searchText, states, types, starredSelected,
				starredQuestIds, values, memberships, order);
		}

		public QuestFilter withMembershipSelections(Set<QuestMembership> values)
		{
			return new QuestFilter(searchText, states, types, starredSelected,
				starredQuestIds, difficulties, values, order);
		}

		public QuestFilter withOrder(QuestOrder value)
		{
			return new QuestFilter(searchText, states, types, starredSelected,
				starredQuestIds, difficulties, memberships, value);
		}

		public boolean matches(QuestListItem quest)
		{
			Objects.requireNonNull(quest, "quest");
			if (!states.contains(quest.getState())
				|| !memberships.contains(quest.getMembership()))
			{
				return false;
			}
			boolean selectedByType = types.contains(quest.getType());
			boolean selectedByStar = starredSelected && starredQuestIds.contains(quest.getId());
			if (!selectedByType && !selectedByStar)
			{
				return false;
			}
			if (!allDifficultiesSelected
				&& ((quest.getType() != QuestType.QUEST
					&& quest.getType() != QuestType.ACHIEVEMENT_DIARY)
					|| !difficulties.contains(quest.getDifficulty())))
			{
				return false;
			}
			return normalizedSearchText.isEmpty()
				|| quest.normalizedTitle.contains(normalizedSearchText);
		}

		public Comparator<QuestListItem> comparator()
		{
			return Comparator
				.comparingInt((QuestListItem quest) -> quest.getOrderRank(order))
				.thenComparing(QuestListItem::getTitle, String.CASE_INSENSITIVE_ORDER)
				.thenComparing(QuestListItem::getId);
		}
	}
}
