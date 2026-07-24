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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StarredQuestStoreTest
{
	private ConfigManager configManager;
	private StarredQuestStore store;

	@BeforeEach
	void setUp()
	{
		configManager = mock(ConfigManager.class);
		store = new StarredQuestStore(configManager, new Gson());
	}

	@Test
	void loadUsesTheCurrentRuneScapeProfileAndNormalizesStoredIds()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		when(configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY))
			.thenReturn("[\"DRAGON_SLAYER_I\",\" COOKS_ASSISTANT \",null,\"DRAGON_SLAYER_I\",\"\"]");

		Set<String> loaded = store.load();

		assertEquals(
			new LinkedHashSet<>(Arrays.asList("COOKS_ASSISTANT", "DRAGON_SLAYER_I")),
			loaded);
		assertThrows(UnsupportedOperationException.class, () -> loaded.add("DEMON_SLAYER"));
	}

	@Test
	void malformedOrMissingStoredDataLoadsAsEmpty()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		when(configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY))
			.thenReturn("not json", "", null);

		assertTrue(store.load().isEmpty());
		assertTrue(store.load().isEmpty());
		assertTrue(store.load().isEmpty());
	}

	@Test
	void saveWritesDeterministicJsonToTheCurrentRuneScapeProfile()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");
		Set<String> starred = new LinkedHashSet<>(Arrays.asList(
			"DRAGON_SLAYER_I",
			"COOKS_ASSISTANT",
			" DRAGON_SLAYER_I ",
			""));

		store.save(starred);

		verify(configManager).setRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY,
			"[\"COOKS_ASSISTANT\",\"DRAGON_SLAYER_I\"]");
		verify(configManager, never()).setConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY,
			"[\"COOKS_ASSISTANT\",\"DRAGON_SLAYER_I\"]");
	}

	@Test
	void saveUnsetsTheProfileKeyWhenNoIdsRemain()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");

		store.save(Collections.emptySet());

		verify(configManager).unsetRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY);
		verify(configManager, never()).setRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY,
			"[]");
	}

	@Test
	void clearUnsetsTheCurrentProfileKey()
	{
		when(configManager.getRSProfileKey()).thenReturn("profile");

		store.clear();

		verify(configManager).unsetRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY);
	}

	@Test
	void absentRuneScapeProfileDoesNotReadOrWriteGlobalConfiguration()
	{
		when(configManager.getRSProfileKey()).thenReturn(null);

		assertTrue(store.load().isEmpty());
		store.save(Collections.singleton("COOKS_ASSISTANT"));
		store.clear();

		verify(configManager, never()).getRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY);
		verify(configManager, never()).setRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY,
			"[\"COOKS_ASSISTANT\"]");
		verify(configManager, never()).unsetRSProfileConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			StarredQuestStore.STARRED_QUESTS_CONFIG_KEY);
	}

	@Test
	void saveRejectsNullStateInsteadOfSilentlyClearingIt()
	{
		assertThrows(NullPointerException.class, () -> store.save(null));
	}
}
