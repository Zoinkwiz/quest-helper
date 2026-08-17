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

import com.google.gson.Gson;
import com.questhelper.QuestHelperConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;

/** Reads and writes quest-journal view state against the current RuneScape profile. */
final class ViewStateStore
{
	static final String CONFIG_KEY = "journalViewState";

	private final ConfigManager configManager;
	private final Gson gson;

	@Inject
	ViewStateStore(ConfigManager configManager, Gson gson)
	{
		this.configManager = Objects.requireNonNull(configManager, "configManager");
		this.gson = Objects.requireNonNull(gson, "gson");
	}

	ViewState load()
	{
		if (configManager.getRSProfileKey() == null)
		{
			return ViewState.empty();
		}

		String raw = configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			CONFIG_KEY);
		if (raw == null || raw.isBlank())
		{
			return ViewState.empty();
		}

		try
		{
			StoredState stored = gson.fromJson(raw, StoredState.class);
			if (stored == null || stored.version != ViewState.SCHEMA_VERSION)
			{
				return ViewState.empty();
			}
			return new ViewState(
				stored.selectedQuestId,
				stored.listScrollOffset,
				stored.overviewScrollOffset,
				stored.detailsScrollOffset,
				parseEnumSelection(stored.types, JournalSnapshot.QuestType.class),
				parseEnumSelection(stored.difficulties, JournalSnapshot.QuestDifficulty.class),
				parseEnumSelection(stored.memberships, JournalSnapshot.QuestMembership.class),
				parseEnumSelection(stored.states, JournalSnapshot.QuestState.class),
				parseEnum(stored.order, JournalSnapshot.QuestOrder.class, JournalSnapshot.QuestOrder.A_TO_Z),
				stored.starredSelected,
				stored.filtersVisible,
				stored.expandedChecklistIds == null
					? Collections.emptySet()
					: new LinkedHashSet<>(stored.expandedChecklistIds));
		}
		catch (RuntimeException ignored)
		{
			return ViewState.empty();
		}
	}

	void save(ViewState state)
	{
		Objects.requireNonNull(state, "state");
		if (configManager.getRSProfileKey() == null)
		{
			return;
		}
		if (!state.isPresent())
		{
			configManager.unsetRSProfileConfiguration(
				QuestHelperConfig.QUEST_HELPER_GROUP,
				CONFIG_KEY);
			return;
		}

		StoredState stored = new StoredState();
		stored.version = ViewState.SCHEMA_VERSION;
		stored.selectedQuestId = state.getSelectedQuestId();
		stored.listScrollOffset = state.getListScrollOffset();
		stored.overviewScrollOffset = state.getOverviewScrollOffset();
		stored.detailsScrollOffset = state.getDetailsScrollOffset();
		stored.types = enumNames(state.getTypeSelections());
		stored.difficulties = enumNames(state.getDifficultySelections());
		stored.memberships = enumNames(state.getMembershipSelections());
		stored.states = enumNames(state.getStateSelections());
		stored.order = state.getOrder().name();
		stored.starredSelected = state.isStarredSelected();
		stored.filtersVisible = state.isFiltersVisible();
		stored.expandedChecklistIds = new ArrayList<>(state.getExpandedChecklistIds());

		configManager.setRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			CONFIG_KEY,
			gson.toJson(stored));
	}

	private static <E extends Enum<E>> Set<E> parseEnumSelection(List<String> names, Class<E> type)
	{
		if (names == null)
		{
			return EnumSet.allOf(type);
		}
		EnumSet<E> parsed = EnumSet.noneOf(type);
		for (String name : names)
		{
			E value = parseEnum(name, type, null);
			if (value != null)
			{
				parsed.add(value);
			}
		}
		if (parsed.isEmpty() && !names.isEmpty())
		{
			parsed.addAll(EnumSet.allOf(type));
		}
		return parsed;
	}

	private static <E extends Enum<E>> E parseEnum(String name, Class<E> type, E fallback)
	{
		if (name == null)
		{
			return fallback;
		}
		try
		{
			return Enum.valueOf(type, name.trim().toUpperCase(Locale.ROOT));
		}
		catch (IllegalArgumentException ignored)
		{
			return fallback;
		}
	}

	private static <E extends Enum<E>> List<String> enumNames(Set<E> values)
	{
		if (values.isEmpty())
		{
			return Collections.emptyList();
		}
		List<String> names = new ArrayList<>(values.size());
		for (E value : EnumSet.copyOf(values))
		{
			names.add(value.name());
		}
		return names;
	}

	@SuppressWarnings("unused")
	private static final class StoredState
	{
		private int version;
		private String selectedQuestId;
		private int listScrollOffset;
		private int overviewScrollOffset;
		private int detailsScrollOffset;
		private List<String> types;
		private List<String> difficulties;
		private List<String> memberships;
		private List<String> states;
		private String order;
		private boolean starredSelected;
		private boolean filtersVisible;
		private List<String> expandedChecklistIds;
	}
}
