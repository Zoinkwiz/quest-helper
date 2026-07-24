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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViewStateStoreTest
{
	private ConfigManager configManager;
	private Gson gson;
	private ViewStateStore store;

	@BeforeEach
	void setUp()
	{
		configManager = mock(ConfigManager.class);
		gson = new Gson();
		store = new ViewStateStore(configManager, gson);
	}

	@Test
	void loadSanitizesPersistedStateWithoutLosingIntentionalEmptySelections()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		when(configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY))
			.thenReturn("{"
				+ "\"version\":1,"
				+ "\"selectedQuestId\":\" COOKS_ASSISTANT \","
				+ "\"listScrollOffset\":-4,"
				+ "\"overviewScrollOffset\":2000000,"
				+ "\"detailsScrollOffset\":-9,"
				+ "\"types\":[\"SKILL\",\"QUEST\",\"QUEST\",\"future_type\",null],"
				+ "\"memberships\":[\" members \",\"FREE_TO_PLAY\"],"
				+ "\"states\":[],"
				+ "\"order\":\" optimal_ironman \","
				+ "\"starredSelected\":true,"
				+ "\"filtersVisible\":true,"
				+ "\"expandedChecklistIds\":["
				+ "\" section:COOKS_ASSISTANT:1 \",null,\"\","
				+ "\"section:COOKS_ASSISTANT:0\",\"section:COOKS_ASSISTANT:1\"]"
				+ "}");

		ViewState loaded = store.load();

		assertTrue(loaded.isPresent());
		assertEquals("COOKS_ASSISTANT", loaded.getSelectedQuestId());
		assertEquals(0, loaded.getListScrollOffset());
		assertEquals(ViewState.MAX_SCROLL_OFFSET, loaded.getOverviewScrollOffset());
		assertEquals(0, loaded.getDetailsScrollOffset());
		assertEquals(
			new LinkedHashSet<>(Arrays.asList(
				JournalSnapshot.QuestType.QUEST,
				JournalSnapshot.QuestType.SKILL)),
			loaded.getTypeSelections());
		assertEquals(
			new LinkedHashSet<>(Arrays.asList(JournalSnapshot.QuestDifficulty.values())),
			loaded.getDifficultySelections());
		assertEquals(
			new LinkedHashSet<>(Arrays.asList(
				JournalSnapshot.QuestMembership.FREE_TO_PLAY,
				JournalSnapshot.QuestMembership.MEMBERS)),
			loaded.getMembershipSelections());
		assertTrue(loaded.getStateSelections().isEmpty());
		assertEquals(JournalSnapshot.QuestOrder.OPTIMAL_IRONMAN, loaded.getOrder());
		assertTrue(loaded.isStarredSelected());
		assertTrue(loaded.isFiltersVisible());
		assertEquals(
			new LinkedHashSet<>(Arrays.asList(
				"section:COOKS_ASSISTANT:0",
				"section:COOKS_ASSISTANT:1")),
			loaded.getExpandedChecklistIds());
		assertThrows(
			UnsupportedOperationException.class,
			() -> loaded.getTypeSelections().add(JournalSnapshot.QuestType.MINIQUEST));
		assertThrows(
			UnsupportedOperationException.class,
			() -> loaded.getExpandedChecklistIds().add("section:OTHER:0"));
	}

	@Test
	void unknownOnlyEnumSelectionsFallBackToAllWhileLiteralEmptyRemainsNone()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		when(configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY))
			.thenReturn("{"
				+ "\"version\":1,"
				+ "\"types\":[\"FUTURE_TYPE\"],"
				+ "\"difficulties\":[],"
				+ "\"memberships\":[\"FUTURE_MEMBERSHIP\"]"
				+ "}");

		ViewState loaded = store.load();

		assertEquals(
			new LinkedHashSet<>(Arrays.asList(JournalSnapshot.QuestType.values())),
			loaded.getTypeSelections());
		assertTrue(loaded.getDifficultySelections().isEmpty());
		assertEquals(
			new LinkedHashSet<>(Arrays.asList(JournalSnapshot.QuestMembership.values())),
			loaded.getMembershipSelections());
	}

	@Test
	void malformedMissingAndUnsupportedDataLoadAsAbsentState()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		when(configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY))
			.thenReturn(
				"not json",
				"{}",
				"{\"version\":2}",
				"",
				null);

		assertFalse(store.load().isPresent());
		assertFalse(store.load().isPresent());
		assertFalse(store.load().isPresent());
		assertFalse(store.load().isPresent());
		assertFalse(store.load().isPresent());
	}

	@Test
	void saveWritesDeterministicVersionedJsonToTheCurrentProfile()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		ViewState state = new ViewState(
			"DRAGON_SLAYER_I",
			21,
			34,
			55,
			new LinkedHashSet<>(Arrays.asList(
				JournalSnapshot.QuestType.SKILL,
				JournalSnapshot.QuestType.QUEST)),
			Collections.singleton(JournalSnapshot.QuestDifficulty.GRANDMASTER),
			Collections.singleton(JournalSnapshot.QuestMembership.MEMBERS),
			new LinkedHashSet<>(Arrays.asList(
				JournalSnapshot.QuestState.COMPLETE,
				JournalSnapshot.QuestState.IN_PROGRESS)),
			JournalSnapshot.QuestOrder.RELEASE_DATE,
			true,
			false,
			new LinkedHashSet<>(Arrays.asList(
				"section:DRAGON_SLAYER_I:2",
				" section:DRAGON_SLAYER_I:0 ")));

		store.save(state);

		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);
		verify(configManager).setRSProfileConfiguration(
			eq(QuestHelperConfig.QUEST_HELPER_GROUP),
			eq(ViewStateStore.CONFIG_KEY),
			json.capture());
		assertEquals(
			"{\"version\":1,"
				+ "\"selectedQuestId\":\"DRAGON_SLAYER_I\","
				+ "\"listScrollOffset\":21,"
				+ "\"overviewScrollOffset\":34,"
				+ "\"detailsScrollOffset\":55,"
				+ "\"types\":[\"QUEST\",\"SKILL\"],"
				+ "\"difficulties\":[\"GRANDMASTER\"],"
				+ "\"memberships\":[\"MEMBERS\"],"
				+ "\"states\":[\"IN_PROGRESS\",\"COMPLETE\"],"
				+ "\"order\":\"RELEASE_DATE\","
				+ "\"starredSelected\":true,"
				+ "\"filtersVisible\":false,"
				+ "\"expandedChecklistIds\":["
				+ "\"section:DRAGON_SLAYER_I:0\",\"section:DRAGON_SLAYER_I:2\"]}",
			json.getValue());
		verify(configManager, never()).setConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY,
			json.getValue());
	}

	@Test
	void savedStateRoundTripsThroughItsJsonRepresentation()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		ViewState expected = new ViewState(
			"COOKS_ASSISTANT",
			7,
			8,
			9,
			Collections.singleton(JournalSnapshot.QuestType.QUEST),
			Collections.singleton(JournalSnapshot.QuestDifficulty.NOVICE),
			Collections.singleton(JournalSnapshot.QuestMembership.FREE_TO_PLAY),
			Collections.singleton(JournalSnapshot.QuestState.NOT_STARTED),
			JournalSnapshot.QuestOrder.OPTIMAL,
			false,
			true,
			Collections.singleton("section:COOKS_ASSISTANT:0"));
		ArgumentCaptor<String> json = ArgumentCaptor.forClass(String.class);

		store.save(expected);
		verify(configManager).setRSProfileConfiguration(
			eq(QuestHelperConfig.QUEST_HELPER_GROUP),
			eq(ViewStateStore.CONFIG_KEY),
			json.capture());
		when(configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY))
			.thenReturn(json.getValue());

		assertEquals(expected, store.load());
	}

	@Test
	void absentStateUnsetsTheProfileKeyAndNullStateIsRejected()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");

		store.save(ViewState.empty());

		verify(configManager).unsetRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY);
		assertThrows(NullPointerException.class, () -> store.save(null));
	}

	@Test
	void absentRuneScapeProfileDoesNotReadOrWriteAnyConfiguration()
	{
		when(configManager.getRSProfileKey()).thenReturn(null);
		ViewState state = new ViewState(
			null,
			0,
			0,
			0,
			null,
			null,
			null,
			null,
			null,
			false,
			false,
			Collections.emptySet());

		assertFalse(store.load().isPresent());
		store.save(state);

		verify(configManager, never()).getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY);
		verify(configManager, never()).setRSProfileConfiguration(
			eq(QuestHelperConfig.QUEST_HELPER_GROUP),
			eq(ViewStateStore.CONFIG_KEY),
			anyString());
		verify(configManager, never()).unsetRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			ViewStateStore.CONFIG_KEY);
	}
}
