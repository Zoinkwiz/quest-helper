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
import com.questhelper.managers.QuestOverlayManager;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import javax.inject.Named;
import java.util.concurrent.ScheduledExecutorService;


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
	protected QuestOverlayManager questOverlayManager = Mockito.mock(QuestOverlayManager.class);

	@Bind
	protected RuneliteObjectManager runeliteObjectManager = Mockito.mock(RuneliteObjectManager.class);

	@Bind
	protected Hooks hooks = Mockito.mock(Hooks.class);

	@Bind
	protected QuestHelperPlugin questHelperPlugin = Mockito.mock(QuestHelperPlugin.class);

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

		// init client mocks
		// when(client.getWorldType()).thenReturn(EnumSet.noneOf(WorldType.class));
	}

}
