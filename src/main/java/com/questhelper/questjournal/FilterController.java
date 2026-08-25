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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.runelite.client.config.ConfigManager;

/** Manages quest-list filters and profile-scoped starred quests. */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
final class FilterController
{
	@NonNull
	private final ConfigManager configManager;
	@NonNull
	private final StarredQuestStore starredQuestStore;

	private volatile JournalSnapshot.QuestFilter filter = JournalSnapshot.QuestFilter.all();
	private volatile Set<String> starredQuestIds = Collections.emptySet();
	private boolean listPreferencesInitialized;

	JournalSnapshot.QuestFilter getFilter()
	{
		return filter;
	}

	Set<String> getStarredQuestIds()
	{
		return starredQuestIds;
	}

	boolean isQuestStarred(String questId)
	{
		return questId != null && starredQuestIds.contains(questId);
	}

	void reloadStarredQuests()
	{
		starredQuestIds = starredQuestStore.load();
		filter = filter.withStarredQuestIds(starredQuestIds);
	}

	void unloadStarredQuests()
	{
		starredQuestIds = Collections.emptySet();
		filter = filter.withStarredQuestIds(starredQuestIds);
	}

	void resetConfiguration()
	{
		starredQuestStore.clear();
		unloadStarredQuests();
		resetListPreferences();
		resetToAll();
	}

	void resetToAll()
	{
		filter = JournalSnapshot.QuestFilter.all()
			.withStarredQuestIds(starredQuestIds);
	}

	void resetListPreferences()
	{
		listPreferencesInitialized = false;
	}

	boolean initializeListPreferences(JournalSnapshot.QuestListOptions options)
	{
		if (listPreferencesInitialized)
		{
			return false;
		}
		filter = filter
			.withTypeSelections(options.getConfiguredTypes())
			.withDifficultySelections(options.getConfiguredDifficulties())
			.withMembershipSelections(options.getConfiguredMemberships())
			.withOrder(options.getConfiguredOrder());
		listPreferencesInitialized = true;
		return true;
	}

	void applyConfiguredProfileDefaults(JournalSnapshot.QuestListOptions options)
	{
		filter = JournalSnapshot.QuestFilter.all()
			.withSearchText("")
			.withTypeSelections(options.getConfiguredTypes())
			.withStarredSelected(true)
			.withStarredQuestIds(starredQuestIds)
			.withDifficultySelections(options.getConfiguredDifficulties())
			.withMembershipSelections(options.getConfiguredMemberships())
			.withOrder(options.getConfiguredOrder());
		listPreferencesInitialized = true;
	}

	void restoreViewState(
		ViewState state,
		JournalSnapshot.QuestListOptions options)
	{
		JournalSnapshot.QuestOrder order = options.getOrders().contains(state.getOrder())
			? state.getOrder()
			: options.getConfiguredOrder();
		filter = JournalSnapshot.QuestFilter.all()
			.withSearchText("")
			.withTypeSelections(availableSelection(
				state.getTypeSelections(), options.getTypes()))
			.withStarredSelected(state.isStarredSelected())
			.withStarredQuestIds(starredQuestIds)
			.withDifficultySelections(availableSelection(
				state.getDifficultySelections(), options.getDifficulties()))
			.withMembershipSelections(availableSelection(
				state.getMembershipSelections(), options.getMemberships()))
			.withStateSelections(availableSelection(
				state.getStateSelections(), Arrays.asList(JournalSnapshot.QuestState.values())))
			.withOrder(order);
		listPreferencesInitialized = true;
	}

	boolean toggleStarredQuest(String questId, JournalSnapshot sourceSnapshot)
	{
		if (questId == null || questId.trim().isEmpty()
			|| configManager.getRSProfileKey() == null)
		{
			return false;
		}
		Set<String> updated = new LinkedHashSet<>(starredQuestIds);
		if (!updated.remove(questId))
		{
			updated.add(questId);
		}
		starredQuestStore.save(updated);
		starredQuestIds = Collections.unmodifiableSet(updated);
		filter = filter.withStarredQuestIds(starredQuestIds);
		return starredQuestChangeAffectsVisibility(questId, sourceSnapshot);
	}

	boolean setQuestTypeSelections(
		Set<JournalSnapshot.QuestType> values,
		boolean starredSelected,
		JournalSnapshot.QuestListOptions options)
	{
		Set<JournalSnapshot.QuestType> selection = availableSelection(
			values,
			options.getTypes());
		if (filter.getTypes().equals(selection)
			&& filter.isStarredSelected() == starredSelected)
		{
			return false;
		}
		filter = filter
			.withTypeSelections(selection)
			.withStarredSelected(starredSelected);
		return true;
	}

	boolean setQuestDifficultySelections(
		Set<JournalSnapshot.QuestDifficulty> values,
		JournalSnapshot.QuestListOptions options)
	{
		Set<JournalSnapshot.QuestDifficulty> selection = availableSelection(
			values,
			options.getDifficulties());
		if (filter.getDifficulties().equals(selection))
		{
			return false;
		}
		filter = filter.withDifficultySelections(selection);
		return true;
	}

	boolean setQuestMembershipSelections(
		Set<JournalSnapshot.QuestMembership> values,
		JournalSnapshot.QuestListOptions options)
	{
		Set<JournalSnapshot.QuestMembership> selection = availableSelection(
			values,
			options.getMemberships());
		if (filter.getMemberships().equals(selection))
		{
			return false;
		}
		filter = filter.withMembershipSelections(selection);
		return true;
	}

	boolean setQuestStateSelections(Set<JournalSnapshot.QuestState> values)
	{
		Set<JournalSnapshot.QuestState> selection = availableSelection(
			values,
			Arrays.asList(JournalSnapshot.QuestState.values()));
		if (filter.getStates().equals(selection))
		{
			return false;
		}
		filter = filter.withStateSelections(selection);
		return true;
	}

	boolean setQuestOrder(JournalSnapshot.QuestOrder value)
	{
		if (value == null || filter.getOrder() == value)
		{
			return false;
		}
		filter = filter.withOrder(value);
		return true;
	}

	boolean resetFilter(
		JournalOverlay.FilterControl control,
		JournalSnapshot.QuestListOptions options)
	{
		switch (control)
		{
			case TYPE:
				return setQuestTypeSelections(options.getConfiguredTypes(), true, options);
			case DIFFICULTY:
				return setQuestDifficultySelections(options.getConfiguredDifficulties(), options);
			case MEMBERSHIP:
				return setQuestMembershipSelections(options.getConfiguredMemberships(), options);
			case ORDER:
				return setQuestOrder(options.getConfiguredOrder());
			case STATUS:
				return setQuestStateSelections(JournalSnapshot.QuestFilter.all().getStates());
			default:
				return false;
		}
	}

	boolean applyFilterSelection(
		JournalOverlay.FilterSelection selection,
		JournalSnapshot.QuestListOptions options)
	{
		if (selection == null)
		{
			return false;
		}
		switch (selection.action())
		{
			case SELECT_ALL:
				return applyChecklistBulkSelection(selection.control(), true, options);
			case SELECT_NONE:
				return applyChecklistBulkSelection(selection.control(), false, options);
			case VALUE:
				break;
			default:
				return false;
		}
		switch (selection.control())
		{
			case TYPE:
				if (selection.value() == JournalOverlay.TypeFilterOption.STARRED)
				{
					filter = filter.withStarredSelected(!filter.isStarredSelected());
					return true;
				}
				return setQuestTypeSelections(
					toggledSelection(
						filter.getTypes(),
						options.getTypes(),
						(JournalSnapshot.QuestType) selection.value()),
					filter.isStarredSelected(),
					options);
			case DIFFICULTY:
				return setQuestDifficultySelections(
					toggledSelection(
						filter.getDifficulties(),
						options.getDifficulties(),
						(JournalSnapshot.QuestDifficulty) selection.value()),
					options);
			case MEMBERSHIP:
				return setQuestMembershipSelections(
					toggledSelection(
						filter.getMemberships(),
						options.getMemberships(),
						(JournalSnapshot.QuestMembership) selection.value()),
					options);
			case STATUS:
				return setQuestStateSelections(toggledSelection(
					filter.getStates(),
					Arrays.asList(JournalSnapshot.QuestState.values()),
					(JournalSnapshot.QuestState) selection.value()));
			case ORDER:
				return setQuestOrder((JournalSnapshot.QuestOrder) selection.value());
			default:
				return false;
		}
	}

	boolean updateSearch(String query)
	{
		String value = query == null ? "" : query;
		if (value.equals(filter.getSearchText()))
		{
			return false;
		}
		filter = filter.withSearchText(value);
		return true;
	}

	static boolean shouldCloseFilterDropdown(
		JournalOverlay.FilterSelection selection)
	{
		return selection != null && !selection.control().isChecklist();
	}

	private boolean applyChecklistBulkSelection(
		JournalOverlay.FilterControl control,
		boolean selectAll,
		JournalSnapshot.QuestListOptions options)
	{
		switch (control)
		{
			case TYPE:
				return setQuestTypeSelections(
					selectAll
						? new LinkedHashSet<>(options.getTypes())
						: Collections.emptySet(),
					selectAll,
					options);
			case DIFFICULTY:
				return setQuestDifficultySelections(
					selectAll
						? new LinkedHashSet<>(options.getDifficulties())
						: Collections.emptySet(),
					options);
			case MEMBERSHIP:
				return setQuestMembershipSelections(
					selectAll
						? new LinkedHashSet<>(options.getMemberships())
						: Collections.emptySet(),
					options);
			case STATUS:
				return setQuestStateSelections(
					selectAll
						? new LinkedHashSet<>(Arrays.asList(JournalSnapshot.QuestState.values()))
						: Collections.emptySet());
			default:
				return false;
		}
	}

	private boolean starredQuestChangeAffectsVisibility(
		String questId,
		JournalSnapshot sourceSnapshot)
	{
		if (!filter.isStarredSelected())
		{
			return false;
		}
		if (sourceSnapshot == null)
		{
			return true;
		}
		for (JournalSnapshot.QuestListItem quest : sourceSnapshot.getQuests())
		{
			if (questId.equals(quest.getId()))
			{
				return !filter.getTypes().contains(quest.getType());
			}
		}
		return true;
	}

	private static <T> Set<T> availableSelection(Set<T> values, List<T> available)
	{
		Set<T> selection = new LinkedHashSet<>();
		if (values == null)
		{
			selection.addAll(available);
			return selection;
		}
		for (T value : values)
		{
			if (available.contains(value))
			{
				selection.add(value);
			}
		}
		return selection;
	}

	private static <T> Set<T> toggledSelection(
		Set<T> current,
		List<T> available,
		T value)
	{
		Set<T> selection = availableSelection(current, available);
		if (value == null || !available.contains(value))
		{
			return selection;
		}
		if (!selection.remove(value))
		{
			selection.add(value);
		}
		return selection;
	}
}
