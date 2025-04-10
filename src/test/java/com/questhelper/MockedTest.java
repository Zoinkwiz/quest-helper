/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
 * Copyright (c) 2023, pajlads <https://github.com/pajlads>
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
package com.questhelper;

import com.google.inject.testing.fieldbinder.Bind;
import com.questhelper.domain.AccountType;
import com.questhelper.managers.QuestOverlayManager;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import com.questhelper.statemanagement.AchievementDiaryStepManager;
import com.questhelper.statemanagement.PlayerStateManager;
import net.runelite.api.*;
import net.runelite.api.gameval.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Based on <a href="https://github.com/pajlads/DinkPlugin/blob/d7c0d4d3f044c25bcff256efc5217955ec1c1494/src/test/java/dinkplugin/notifiers/MockedNotifierTest.java">Dink's MockedNotifierTest</a>
 */
public abstract class MockedTest extends MockedTestBase
{
	@Bind
	protected Client client = Mockito.mock(Client.class);

	@Bind
	protected ConfigManager configManager = Mockito.mock(ConfigManager.class);

	@Bind
	protected ChatMessageManager chatMessageManager = Mockito.mock(ChatMessageManager.class);

	@Bind
	protected ItemManager itemManager = Mockito.mock(ItemManager.class);

	@Bind
	protected OverlayManager overlayManager = Mockito.mock(OverlayManager.class);

	@Bind
	protected QuestHelperConfig questHelperConfig = Mockito.mock(QuestHelperConfig.class);

	@Bind
	protected RuneLiteConfig runeLiteConfig = Mockito.mock(RuneLiteConfig.class);

	@Bind
	protected QuestOverlayManager questOverlayManager = Mockito.mock(QuestOverlayManager.class);

	@Bind
	protected SpriteManager spriteManager = Mockito.mock(SpriteManager.class);

	@Bind
	protected RuneliteObjectManager runeliteObjectManager = Mockito.mock(RuneliteObjectManager.class);

	@Bind
	protected Hooks hooks = Mockito.mock(Hooks.class);

	@Bind
	protected PlayerStateManager playerStateManager = Mockito.mock(PlayerStateManager.class);

	@Bind
	protected QuestHelperPlugin questHelperPlugin = Mockito.spy(QuestHelperPlugin.class);

	@Bind
	protected ClientToolbar clientToolbar = Mockito.mock(ClientToolbar.class);

	@Bind
	protected ClientThread clientThread = Mockito.mock(ClientThread.class);

	@Bind
	protected EventBus eventBus = Mockito.mock(EventBus.class);

	@Bind
	protected ScheduledExecutorService scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);

	@Bind
	@Named("developerMode")
	private boolean developerMode;


	@Override
	@BeforeEach
	protected void setUp()
	{
		super.setUp();

		when(questHelperPlugin.getPlayerStateManager()).thenReturn(playerStateManager);
		when(playerStateManager.getAccountType()).thenReturn(AccountType.NORMAL);
		when(client.getIntStack()).thenReturn(new int[] {1, 1, 1, 1});
		when(questHelperConfig.solvePuzzles()).thenReturn(true);
		when(spriteManager.getSprite(SpriteID.TAB_QUESTS, 0)).thenReturn(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));

		AchievementDiaryStepManager.setup(configManager);

		WorldView mockedWorldView = Mockito.mock(WorldView.class);

		@SuppressWarnings("unchecked")
		IndexedObjectSet<? extends NPC> npcSetMock = (IndexedObjectSet<? extends NPC>) Mockito.mock(IndexedObjectSet.class);
		when(npcSetMock.iterator()).thenReturn(Collections.emptyIterator());
		doReturn(npcSetMock).when(mockedWorldView).npcs();
		when(client.getTopLevelWorldView()).thenReturn(mockedWorldView);
	}

}
