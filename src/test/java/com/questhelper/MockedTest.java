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
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.AsyncBufferedImage;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.*;

/**
 * Based on <a href="https://github.com/pajlads/DinkPlugin/blob/d7c0d4d3f044c25bcff256efc5217955ec1c1494/src/test/java/dinkplugin/notifiers/MockedNotifierTest.java">Dink's MockedNotifierTest</a>
 */
public abstract class MockedTest extends MockedTestBase
{
	@Bind
	protected Client client = mock(Client.class);

	@Bind
	protected ConfigManager configManager = mock(ConfigManager.class);

	@Bind
	protected ChatMessageManager chatMessageManager = mock(ChatMessageManager.class);

	@Bind
	protected ItemManager itemManager = mock(ItemManager.class);

	@Bind
	protected OverlayManager overlayManager = mock(OverlayManager.class);

	@Bind
	protected QuestHelperConfig questHelperConfig = Mockito.spy(QuestHelperConfig.class);

	@Bind
	protected RuneLiteConfig runeLiteConfig = mock(RuneLiteConfig.class);

	@Bind
	protected QuestOverlayManager questOverlayManager = mock(QuestOverlayManager.class);

	@Bind
	protected SpriteManager spriteManager = mock(SpriteManager.class);

	@Bind
	protected RuneliteObjectManager runeliteObjectManager = mock(RuneliteObjectManager.class);

	@Bind
	protected Hooks hooks = mock(Hooks.class);

	@Bind
	protected PlayerStateManager playerStateManager = mock(PlayerStateManager.class);

	@Bind
	protected QuestHelperPlugin questHelperPlugin = Mockito.spy(QuestHelperPlugin.class);

	@Bind
	protected ClientToolbar clientToolbar = mock(ClientToolbar.class);

	@Bind
	protected ClientThread clientThread = mock(ClientThread.class);

	@Bind
	protected EventBus eventBus = mock(EventBus.class);

	@Bind
	protected ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

	@Bind
	protected PluginManager pluginManager = mock(PluginManager.class);

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
		when(spriteManager.getSprite(SpriteID.SideiconsInterface.QUESTS, 0)).thenReturn(new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB));


		AchievementDiaryStepManager.setup(configManager);

		var mockedScene = mock(Scene.class);
		when(mockedScene.getTiles()).thenReturn(new Tile[1][Constants.SCENE_SIZE][Constants.SCENE_SIZE]);

		WorldView mockedWorldView = mock(WorldView.class);
		when(mockedWorldView.getScene()).thenReturn(mockedScene);
		when(mockedWorldView.getPlane()).thenReturn(-1);

		@SuppressWarnings("unchecked")
		IndexedObjectSet<? extends NPC> npcSetMock = (IndexedObjectSet<? extends NPC>) mock(IndexedObjectSet.class);
		when(npcSetMock.iterator()).thenReturn(Collections.emptyIterator());
		doReturn(npcSetMock).when(mockedWorldView).npcs();
		when(client.getTopLevelWorldView()).thenReturn(mockedWorldView);
		when(client.getScene()).thenReturn(mockedScene); // TODO: We should not have to mock this
		when(client.findWorldViewFromWorldPoint(any(WorldPoint.class))).thenReturn(mockedWorldView);
		var mockedItemContainer = Mockito.mock(ItemContainer.class);
		when(mockedItemContainer.getItems()).thenReturn(new Item[0]);
		when(client.getItemContainer(anyInt())).thenReturn(mockedItemContainer);

		var mockedPlayer = Mockito.mock(Player.class);
		when(mockedPlayer.getLocalLocation()).thenReturn(new LocalPoint(1, 1, 1));
		when(client.getLocalPlayer()).thenReturn(mockedPlayer);
		when(client.getWorldView(anyInt())).thenReturn(mockedWorldView);
		when(mockedPlayer.getWorldView()).thenReturn(mockedWorldView);
		when(mockedWorldView.getId()).thenReturn(-1);

		ItemComposition itemComposition = mock(ItemComposition.class);
		when(itemComposition.getName()).thenReturn("Test item");
		when(itemManager.getItemComposition(anyInt())).thenReturn(itemComposition);
		when(itemManager.getImage(anyInt())).thenReturn(new AsyncBufferedImage(clientThread, 10, 10, BufferedImage.TYPE_INT_ARGB));

		// Varbit mocking
		// DT2
		when(client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_1)).thenReturn(1);
		when(client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_2)).thenReturn(1);
		when(client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_3)).thenReturn(1);
		when(client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_4)).thenReturn(1);
		when(client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_5)).thenReturn(1);
		when(client.getVarbitValue(VarbitID.DT2_SCAR_MAZE_1_RIFT_6)).thenReturn(1);
		// Balloon flight
		when(client.getVarbitValue(VarbitID.ZEP_IF_CURRENT_MAP)).thenReturn(1);
	}

}
