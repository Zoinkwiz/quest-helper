/*
 * Copyright (c) 2026, Ruined Heir
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
package com.questhelper.managers;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class QuestMenuHandlerTest
{
	@Mock
	private Client client;

	@Mock
	private QuestManager questManager;

	@InjectMocks
	private QuestMenuHandler questMenuHandler;

	private AutoCloseable mocks;

	@BeforeEach
	public void setUp()
	{
		mocks = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	public void cleanUp() throws Exception
	{
		mocks.close();
	}

	@Test
	public void resolvesGenericQuestByName()
	{
		QuestHelper resolved = questMenuHandler.resolveQuestHelper(QuestHelperQuest.COOKS_ASSISTANT.getName());

		assertSame(QuestHelperQuest.COOKS_ASSISTANT.getQuestHelper(), resolved);
		verifyNoInteractions(questManager);
	}

	@Test
	public void resolvesRecipeForDisasterToStartHelper()
	{
		QuestHelper resolved = questMenuHandler.resolveQuestHelper("Recipe for Disaster");

		assertSame(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getQuestHelper(), resolved);
		verifyNoInteractions(questManager);
	}

	@Test
	public void resolvesShieldOfArravToPhoenixGangInsidePhoenixStartZone()
	{
		Player player = playerAt(new WorldPoint(3210, 3494, 0));

		QuestHelper resolved = questMenuHandler.resolveQuestHelper("Shield of Arrav");

		assertSame(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getQuestHelper(), resolved);
		verifyNoInteractions(questManager);
		verify(client).getLocalPlayer();
		verify(player).getWorldLocation();
	}

	@Test
	public void resolvesShieldOfArravToBlackArmGangOutsidePhoenixStartZone()
	{
		Player player = playerAt(new WorldPoint(3200, 3480, 0));

		QuestHelper resolved = questMenuHandler.resolveQuestHelper("Shield of Arrav");

		assertSame(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getQuestHelper(), resolved);
		verifyNoInteractions(questManager);
		verify(client).getLocalPlayer();
		verify(player).getWorldLocation();
	}

	@Test
	public void startsResolvedSpecialCaseHelper()
	{
		QuestHelper expected = QuestHelperQuest.RECIPE_FOR_DISASTER_START.getQuestHelper();

		questMenuHandler.startUpQuest("Recipe for Disaster");

		verify(questManager).startUpQuest(expected, true);
	}

	private Player playerAt(WorldPoint location)
	{
		Player player = mock(Player.class);
		when(client.getLocalPlayer()).thenReturn(player);
		when(player.getWorldLocation()).thenReturn(location);
		return player;
	}
}
