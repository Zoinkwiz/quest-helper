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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Immutable, profile-owned state needed to restore a quest-journal view. */
@Getter(AccessLevel.PACKAGE)
@EqualsAndHashCode
final class ViewState
{
	static final int SCHEMA_VERSION = 1;
	static final int MAX_SCROLL_OFFSET = 1_000_000;
	static final int MAX_EXPANDED_CHECKLIST_IDS = 512;
	private static final int MAX_QUEST_ID_LENGTH = 256;
	private static final int MAX_CHECKLIST_ID_LENGTH = 512;

	private static final ViewState EMPTY = new ViewState(
		false,
		null,
		0,
		0,
		0,
		immutableEnumSelection(null, JournalSnapshot.QuestType.class),
		immutableEnumSelection(null, JournalSnapshot.QuestDifficulty.class),
		immutableEnumSelection(null, JournalSnapshot.QuestMembership.class),
		immutableEnumSelection(null, JournalSnapshot.QuestState.class),
		JournalSnapshot.QuestOrder.A_TO_Z,
		false,
		false,
		Collections.emptySet());

	private final boolean present;
	private final String selectedQuestId;
	private final int listScrollOffset;
	private final int overviewScrollOffset;
	private final int detailsScrollOffset;
	private final Set<JournalSnapshot.QuestType> typeSelections;
	private final Set<JournalSnapshot.QuestDifficulty> difficultySelections;
	private final Set<JournalSnapshot.QuestMembership> membershipSelections;
	private final Set<JournalSnapshot.QuestState> stateSelections;
	private final JournalSnapshot.QuestOrder order;
	private final boolean starredSelected;
	private final boolean filtersVisible;
	private final Set<String> expandedChecklistIds;

	ViewState(
		String selectedQuestId,
		int listScrollOffset,
		int overviewScrollOffset,
		int detailsScrollOffset,
		Set<JournalSnapshot.QuestType> typeSelections,
		Set<JournalSnapshot.QuestDifficulty> difficultySelections,
		Set<JournalSnapshot.QuestMembership> membershipSelections,
		Set<JournalSnapshot.QuestState> stateSelections,
		JournalSnapshot.QuestOrder order,
		boolean starredSelected,
		boolean filtersVisible,
		Set<String> expandedChecklistIds)
	{
		this(
			true,
			selectedQuestId,
			listScrollOffset,
			overviewScrollOffset,
			detailsScrollOffset,
			typeSelections,
			difficultySelections,
			membershipSelections,
			stateSelections,
			order,
			starredSelected,
			filtersVisible,
			expandedChecklistIds);
	}

	static ViewState fromFilter(
		String selectedQuestId,
		int listScrollOffset,
		int overviewScrollOffset,
		int detailsScrollOffset,
		JournalSnapshot.QuestFilter filter,
		boolean filtersVisible,
		Set<String> expandedChecklistIds)
	{
		Objects.requireNonNull(filter, "filter");
		return new ViewState(
			selectedQuestId,
			listScrollOffset,
			overviewScrollOffset,
			detailsScrollOffset,
			filter.getTypes(),
			filter.getDifficulties(),
			filter.getMemberships(),
			filter.getStates(),
			filter.getOrder(),
			filter.isStarredSelected(),
			filtersVisible,
			expandedChecklistIds);
	}

	private ViewState(
		boolean present,
		String selectedQuestId,
		int listScrollOffset,
		int overviewScrollOffset,
		int detailsScrollOffset,
		Set<JournalSnapshot.QuestType> typeSelections,
		Set<JournalSnapshot.QuestDifficulty> difficultySelections,
		Set<JournalSnapshot.QuestMembership> membershipSelections,
		Set<JournalSnapshot.QuestState> stateSelections,
		JournalSnapshot.QuestOrder order,
		boolean starredSelected,
		boolean filtersVisible,
		Set<String> expandedChecklistIds)
	{
		this.present = present;
		this.selectedQuestId = normalizeId(selectedQuestId, MAX_QUEST_ID_LENGTH);
		this.listScrollOffset = normalizeOffset(listScrollOffset);
		this.overviewScrollOffset =
			this.selectedQuestId == null ? 0 : normalizeOffset(overviewScrollOffset);
		this.detailsScrollOffset =
			this.selectedQuestId == null ? 0 : normalizeOffset(detailsScrollOffset);
		this.typeSelections = immutableEnumSelection(typeSelections, JournalSnapshot.QuestType.class);
		this.difficultySelections = immutableEnumSelection(
			difficultySelections, JournalSnapshot.QuestDifficulty.class);
		this.membershipSelections = immutableEnumSelection(
			membershipSelections, JournalSnapshot.QuestMembership.class);
		this.stateSelections = immutableEnumSelection(stateSelections, JournalSnapshot.QuestState.class);
		this.order = order == null ? JournalSnapshot.QuestOrder.A_TO_Z : order;
		this.starredSelected = starredSelected;
		this.filtersVisible = filtersVisible;
		this.expandedChecklistIds = immutableChecklistIds(expandedChecklistIds);
	}

	static ViewState empty()
	{
		return EMPTY;
	}

	private static int normalizeOffset(int offset)
	{
		return Math.max(0, Math.min(MAX_SCROLL_OFFSET, offset));
	}

	private static String normalizeId(String id, int maximumLength)
	{
		if (id == null)
		{
			return null;
		}
		String normalized = id.trim();
		return normalized.isEmpty() || normalized.length() > maximumLength ? null : normalized;
	}

	private static <E extends Enum<E>> Set<E> immutableEnumSelection(Set<E> values, Class<E> type)
	{
		EnumSet<E> normalized = values == null
			? EnumSet.allOf(type)
			: EnumSet.noneOf(type);
		if (values != null)
		{
			for (E value : values)
			{
				if (value != null)
				{
					normalized.add(value);
				}
			}
		}
		return normalized.isEmpty()
			? Collections.emptySet()
			: Collections.unmodifiableSet(new LinkedHashSet<>(normalized));
	}

	private static Set<String> immutableChecklistIds(Set<String> ids)
	{
		if (ids == null || ids.isEmpty())
		{
			return Collections.emptySet();
		}
		Set<String> normalized = new TreeSet<>();
		for (String id : ids)
		{
			String value = normalizeId(id, MAX_CHECKLIST_ID_LENGTH);
			if (value != null)
			{
				normalized.add(value);
			}
		}
		if (normalized.isEmpty())
		{
			return Collections.emptySet();
		}
		LinkedHashSet<String> limited = new LinkedHashSet<>();
		for (String value : normalized)
		{
			limited.add(value);
			if (limited.size() == MAX_EXPANDED_CHECKLIST_IDS)
			{
				break;
			}
		}
		return Collections.unmodifiableSet(limited);
	}

}
