/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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

import com.questhelper.MockedTest;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import net.runelite.api.GameState;
import net.runelite.client.events.PluginMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers the inbound {@link PluginMessage} listener added to {@link QuestManager} for GOLIVE-02 --
 * another plugin (or a script, or a test like this one) can start a named quest by posting
 * {@code new PluginMessage("questhelper", "start", Map.of("quest", <display name>))} on the shared
 * event bus, without reflecting into Quest Helper's internals.
 */
class QuestManagerPluginMessageTest extends MockedTest
{
	private static final QuestHelperQuest TEST_QUEST = QuestHelperQuest.COOKS_ASSISTANT;

	private QuestManager questManager;

	@Override
	@BeforeEach
	protected void setUp()
	{
		super.setUp();

		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.isClientThread()).thenReturn(true);

		QuestHelper helper = TEST_QUEST.getQuestHelper();
		helper.setQuest(TEST_QUEST);
		this.injector.injectMembers(helper);
		helper.setInjector(this.injector);
		helper.setQuestHelperPlugin(questHelperPlugin);
		helper.setConfig(questHelperConfig);
		helper.init();

		questManager = new QuestManager();
		this.injector.injectMembers(questManager);
		questManager.startUp(mock(QuestHelperPanel.class));
	}

	private static PluginMessage startMessage(String questName)
	{
		Map<String, Object> data = new HashMap<>();
		data.put("quest", questName);
		return new PluginMessage("questhelper", "start", data);
	}

	@Test
	void aStartMessageInTheQuestHelperNamespaceWithAResolvableNameStartsThatQuest()
	{
		questManager.onPluginMessage(startMessage(TEST_QUEST.getName()));

		assertEquals(TEST_QUEST, questManager.getSelectedQuest().getQuest());
	}

	@Test
	void aMessageFromAnotherNamespaceIsIgnored()
	{
		PluginMessage event = new PluginMessage("shortestpath", "start", startMessage(TEST_QUEST.getName()).getData());

		assertDoesNotThrow(() -> questManager.onPluginMessage(event));
		assertNull(questManager.getSelectedQuest());
	}

	@Test
	void aMessageWithAMissingOrUnresolvableQuestNameIsIgnoredWithoutThrowing()
	{
		assertDoesNotThrow(() -> questManager.onPluginMessage(new PluginMessage("questhelper", "start")));
		assertNull(questManager.getSelectedQuest());

		assertDoesNotThrow(() -> questManager.onPluginMessage(startMessage("Definitely Not A Real Quest Name")));
		assertNull(questManager.getSelectedQuest());
	}

	/**
	 * {@link QuestManager#startUpQuest(QuestHelper, boolean)}'s own restart semantics (shut down
	 * whatever is selected, then re-initialize) are pre-existing behaviour this PR does not touch,
	 * and re-running them for real here would mean production-wiring every quest in the game (what
	 * {@code QuestHelperPlugin.startUp()} does at real plugin startup, not what a unit test for one
	 * listener should need). What this listener is responsible for -- and what this test isolates --
	 * is that two identical posted messages resolve to starting the *same* {@link QuestHelper}
	 * instance both times, which is exactly the precondition that makes
	 * {@code startUpQuest}'s own restart-to-identical-state behaviour idempotent from a caller's
	 * point of view: posting twice never picks a different quest than posting once did.
	 */
	@Test
	void postingTheSameStartMessageTwiceResolvesToStartingTheSameQuestBothTimes()
	{
		QuestManager spyManager = Mockito.spy(questManager);
		doNothing().when(spyManager).startUpQuest(any(), anyBoolean());

		spyManager.onPluginMessage(startMessage(TEST_QUEST.getName()));
		spyManager.onPluginMessage(startMessage(TEST_QUEST.getName()));

		ArgumentCaptor<QuestHelper> captor = ArgumentCaptor.forClass(QuestHelper.class);
		verify(spyManager, times(2)).startUpQuest(captor.capture(), eq(true));

		List<QuestHelper> resolvedEachPost = captor.getAllValues();
		assertSame(resolvedEachPost.get(0), resolvedEachPost.get(1));
	}
}
