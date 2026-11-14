/*
 * Copyright (c) 2026, phillocity <https://github.com/phillocity>
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
package com.questhelper.managers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.runelite.client.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.questhelper.QuestHelperConfig.QUEST_HELPER_GROUP;
import static com.questhelper.managers.QuestQueueManager.CONFIG_KEY;
import static com.questhelper.questinfo.QuestHelperQuest.AGILITY;
import static com.questhelper.questinfo.QuestHelperQuest.ARDOUGNE_EASY;
import static com.questhelper.questinfo.QuestHelperQuest.CHECK_ITEMS;
import static com.questhelper.questinfo.QuestHelperQuest.COOKS_ASSISTANT;
import static com.questhelper.questinfo.QuestHelperQuest.ENTER_THE_ABYSS;
import static com.questhelper.questinfo.QuestHelperQuest.RUNE_MYSTERIES;
import static com.questhelper.questinfo.QuestHelperQuest.SHEEP_SHEARER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuestQueueManagerTest
{
	private final ConfigManager configManager = mock(ConfigManager.class);
	private final AtomicInteger changes = new AtomicInteger();
	private QuestQueueManager queue;

	@BeforeEach
	void setUp()
	{
		queue = new QuestQueueManager(configManager);
		queue.addListener(changes::incrementAndGet);
	}

	@Test
	void firstAddedQuestIsFirst()
	{
		queue.add(COOKS_ASSISTANT);
		queue.add(SHEEP_SHEARER);
		queue.add(RUNE_MYSTERIES);

		assertEquals(List.of(COOKS_ASSISTANT, SHEEP_SHEARER, RUNE_MYSTERIES), queue.getQueue());
		assertEquals(3, changes.get());
	}

	@Test
	void addingAQuestAgainKeepsItsPlace()
	{
		queue.add(COOKS_ASSISTANT);
		queue.add(SHEEP_SHEARER);
		queue.add(COOKS_ASSISTANT);

		assertEquals(List.of(COOKS_ASSISTANT, SHEEP_SHEARER), queue.getQueue());
		assertEquals(2, changes.get());
	}

	@Test
	void moveUpAndDownStayInBounds()
	{
		queue.add(COOKS_ASSISTANT);
		queue.add(SHEEP_SHEARER);

		queue.moveUp(COOKS_ASSISTANT); // already first, no change
		queue.moveDown(COOKS_ASSISTANT);
		assertEquals(List.of(SHEEP_SHEARER, COOKS_ASSISTANT), queue.getQueue());

		queue.moveDown(COOKS_ASSISTANT); // already last, no change
		assertEquals(List.of(SHEEP_SHEARER, COOKS_ASSISTANT), queue.getQueue());
	}

	@Test
	void removeIfOnlyNotifiesWhenSomethingWasRemoved()
	{
		queue.add(COOKS_ASSISTANT);
		queue.add(SHEEP_SHEARER);
		changes.set(0);

		queue.removeIf(RUNE_MYSTERIES::equals);
		assertEquals(0, changes.get());

		queue.removeIf(COOKS_ASSISTANT::equals);
		assertEquals(1, changes.get());
		assertEquals(List.of(SHEEP_SHEARER), queue.getQueue());
	}

	@Test
	void savesInOrderAndClearsWhenEmpty()
	{
		queue.add(COOKS_ASSISTANT);
		queue.add(SHEEP_SHEARER);
		verify(configManager)
			.setRSProfileConfiguration(QUEST_HELPER_GROUP, CONFIG_KEY, "COOKS_ASSISTANT,SHEEP_SHEARER");

		queue.clear();
		assertTrue(queue.isEmpty());
		verify(configManager).unsetRSProfileConfiguration(QUEST_HELPER_GROUP, CONFIG_KEY);
	}

	@Test
	void loadSkipsUnknownAndDuplicateQuests()
	{
		when(configManager.getRSProfileConfiguration(QUEST_HELPER_GROUP, CONFIG_KEY))
			.thenReturn("SHEEP_SHEARER,NOT_A_REAL_QUEST,COOKS_ASSISTANT,SHEEP_SHEARER");

		queue.load();

		assertEquals(List.of(SHEEP_SHEARER, COOKS_ASSISTANT), queue.getQueue());
	}

	@Test
	void onlyThingsThatCanBeCompletedCanBeQueued()
	{
		assertTrue(QuestQueueManager.canQueue(COOKS_ASSISTANT));
		assertTrue(QuestQueueManager.canQueue(ENTER_THE_ABYSS));
		assertTrue(QuestQueueManager.canQueue(ARDOUGNE_EASY));

		assertFalse(QuestQueueManager.canQueue(CHECK_ITEMS));
		assertFalse(QuestQueueManager.canQueue(AGILITY));
	}
}
