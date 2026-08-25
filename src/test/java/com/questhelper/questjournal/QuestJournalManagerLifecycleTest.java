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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.questjournal;

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.inject.Provider;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.PostMenuSort;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class QuestJournalManagerLifecycleTest
{
	private static final ViewState SAVED_COOKS_ASSISTANT_VIEW_STATE =
		ViewState.fromFilter(
			"COOKS_ASSISTANT",
			44,
			55,
			66,
			JournalSnapshot.QuestFilter.all(),
			true,
			Collections.singleton("section:start"));

	private Client client;
	private ClientThread clientThread;
	private ConfigManager configManager;
	private StarredQuestStore starredQuestStore;
	private ViewStateStore viewStateStore;
	private EventBus eventBus;
	private QuestHelperConfig config;
	private OverlayManager overlayManager;
	private MouseManager mouseManager;
	private KeyManager keyManager;
	private ChatboxPanelManager chatboxPanelManager;
	private Provider<LauncherOverlay> launcherProvider;
	private Provider<JournalOverlay> journalProvider;
	private Provider<JournalDataSource> dataProvider;
	private Provider<QuestHelperPlugin> ownerProvider;
	private LauncherOverlay launcherOverlay;
	private JournalOverlay journalOverlay;
	private JournalDataSource dataSource;
	private Keybind hotkey;
	private QuestJournalManager manager;

	@BeforeEach
	void setUp()
	{
		client = mock(Client.class);
		clientThread = mock(ClientThread.class);
		configManager = mock(ConfigManager.class);
		starredQuestStore = mock(StarredQuestStore.class);
		viewStateStore = mock(ViewStateStore.class);
		eventBus = mock(EventBus.class);
		config = mock(QuestHelperConfig.class);
		overlayManager = mock(OverlayManager.class);
		mouseManager = mock(MouseManager.class);
		keyManager = mock(KeyManager.class);
		chatboxPanelManager = mock(ChatboxPanelManager.class);
		launcherProvider = mock(Provider.class);
		journalProvider = mock(Provider.class);
		dataProvider = mock(Provider.class);
		ownerProvider = mock(Provider.class);
		launcherOverlay = mock(LauncherOverlay.class);
		journalOverlay = mock(JournalOverlay.class);
		dataSource = mock(JournalDataSource.class);
		hotkey = mock(Keybind.class);

		when(client.isClientThread()).thenReturn(true);
		when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
		when(client.getTickCount()).thenReturn(42);
		when(config.enableQuestJournal()).thenReturn(true);
		when(config.journalShowButton()).thenReturn(true);
		when(config.journalChooseActiveQuestManually()).thenReturn(true);
		when(config.journalOpenHotkey()).thenReturn(hotkey);
		when(config.journalExpandChecklistsOnDemand()).thenReturn(true);
		when(hotkey.matches(any(KeyEvent.class))).thenAnswer(invocation ->
			invocation.<KeyEvent>getArgument(0).getKeyCode() == KeyEvent.VK_J);
		when(launcherProvider.get()).thenReturn(launcherOverlay);
		when(journalProvider.get()).thenReturn(journalOverlay);
		when(dataProvider.get()).thenReturn(dataSource);
		when(dataSource.getSnapshot()).thenReturn(emptySnapshot());
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenReturn(emptySnapshot());
		when(starredQuestStore.load()).thenReturn(Collections.emptySet());
		when(viewStateStore.load()).thenReturn(ViewState.empty());

		FilterController filterController =
			new FilterController(configManager, starredQuestStore);
		manager = new QuestJournalManager(
			client,
			clientThread,
			configManager,
			new PanelController(client, configManager, config, overlayManager),
			new SearchController(client, clientThread, chatboxPanelManager),
			filterController,
			new QuestSelectionController(client, config),
			new ViewStateController(client, viewStateStore, filterController),
			eventBus,
			config,
			overlayManager,
			mouseManager,
			keyManager,
			launcherProvider,
			journalProvider,
			dataProvider,
			ownerProvider);
	}

	@Test
	void explicitlyDisabledDoesNotInstantiateUiOrRegisterInput()
	{
		when(config.enableQuestJournal()).thenReturn(false);

		manager.startUp();

		verify(eventBus).register(manager);
		verifyNoInteractions(launcherProvider, journalProvider, dataProvider);
		verifyNoInteractions(overlayManager, mouseManager, keyManager);
		assertFalse(manager.isJournalOpen());
	}

	@Test
	void pluginResetAppliesTheDefaultEnabledStateImmediately()
	{
		when(config.enableQuestJournal()).thenReturn(false);
		manager.startUp();
		when(config.enableQuestJournal()).thenReturn(true);

		manager.resetConfiguration();

		verify(dataProvider).get();
		verify(dataSource).startUp();
		verify(launcherProvider).get();
		verify(overlayManager).add(launcherOverlay);
		verify(keyManager).registerKeyListener(manager);
		verify(mouseManager).registerMouseListener(manager);
		verify(mouseManager).registerMouseWheelListener(manager);
		assertFalse(manager.isJournalOpen());
	}

	@Test
	void disabledClientTicksDoNotReadMenuState()
	{
		clearInvocations(client);

		manager.onClientTick(mock(ClientTick.class));

		verify(client, never()).isMenuOpen();
	}

	@Test
	void enabledAndClosedOnlyStartsDataLauncherAndInput()
	{
		manager.startUp();

		verify(dataProvider).get();
		verify(dataSource).startUp();
		verify(dataSource).setOpen(false);
		verify(launcherProvider).get();
		verify(overlayManager).add(launcherOverlay);
		verify(journalProvider, never()).get();
		verify(mouseManager).registerMouseListener(manager);
		verify(mouseManager).registerMouseWheelListener(manager);
		verify(keyManager).registerKeyListener(manager);
		verify(dataSource, never()).afterQuestUpdate(anyInt());
	}

	@Test
	void pluginResetClearsPersistedJournalStateAndBothOverlayPlacements()
	{
		when(starredQuestStore.load()).thenReturn(
			Collections.singleton("COOKS_ASSISTANT"));
		manager.startUp();
		assertTrue(manager.isQuestStarred("COOKS_ASSISTANT"));
		clearInvocations(
			configManager,
			viewStateStore,
			starredQuestStore,
			overlayManager,
			launcherOverlay,
			journalProvider);

		manager.resetConfiguration();

		for (String key : new String[]{
			"journalPanelInitialized",
			"journalPanelX",
			"journalPanelY",
			"journalPanelWidth",
			"journalPanelHeight",
			"journalPanelMaximized"
		})
		{
			verify(configManager).unsetConfiguration(
				QuestHelperConfig.QUEST_HELPER_GROUP,
				key);
		}
		verify(viewStateStore).save(ViewState.empty());
		verify(overlayManager).resetOverlay(launcherOverlay);
		verify(overlayManager).resetOverlay(journalOverlay);
		verify(launcherOverlay).resetHitState();
		verify(journalProvider).get();
		verify(journalOverlay).clearPersistentViewState();
		verify(dataSource).clearBrowsedQuest();
		verify(dataSource, never()).stopActiveQuest(any());
		verify(starredQuestStore).clear();
		assertFalse(manager.isQuestStarred("COOKS_ASSISTANT"));
		assertFalse(manager.isJournalOpen());
	}

	@Test
	void queuedPluginResetCannotBeCancelledByLaterJournalWork()
	{
		manager.startUp();
		clearInvocations(viewStateStore);
		when(client.isClientThread()).thenReturn(false);
		List<Runnable> resetTasks = new ArrayList<>();
		doAnswer(invocation ->
		{
			resetTasks.add(invocation.getArgument(0));
			return null;
		}).when(clientThread).invoke(any(Runnable.class));

		manager.resetConfiguration();
		manager.onConfigChanged(configChanged("enableQuestJournal"));

		assertEquals(1, resetTasks.size());
		when(client.isClientThread()).thenReturn(true);
		resetTasks.get(0).run();
		verify(viewStateStore).save(ViewState.empty());
	}

	@Test
	void unrelatedQuestHelperConfigChangesDoNotQueueJournalWork()
	{
		manager.startUp();
		clearInvocations(clientThread, dataSource, journalOverlay, launcherOverlay);
		when(client.isClientThread()).thenReturn(false);

		manager.onConfigChanged(configChanged("journalPanelX"));
		manager.onConfigChanged(configChanged("someUnrelatedQuestHelperSetting"));

		verify(clientThread, never()).invokeLater(any(Runnable.class));
		verifyNoInteractions(dataSource, journalOverlay, launcherOverlay);
	}

	@Test
	void starredQuestsReloadWhenTheRuneScapeProfileChanges()
	{
		when(starredQuestStore.load()).thenReturn(
			Collections.singleton("COOKS_ASSISTANT"),
			Collections.singleton("DRAGON_SLAYER_I"));

		manager.startUp();

		assertTrue(manager.isQuestStarred("COOKS_ASSISTANT"));
		assertFalse(manager.isQuestStarred("DRAGON_SLAYER_I"));

		manager.onRuneScapeProfileChanged(mock(RuneScapeProfileChanged.class));

		assertFalse(manager.isQuestStarred("COOKS_ASSISTANT"));
		assertTrue(manager.isQuestStarred("DRAGON_SLAYER_I"));
		verify(starredQuestStore, times(2)).load();
	}

	@Test
	void hiddenLauncherKeepsOnlyTheHotkeyRegisteredWhileClosed()
	{
		when(config.journalShowButton()).thenReturn(false);

		manager.startUp();

		verify(keyManager).registerKeyListener(manager);
		verify(mouseManager, never()).registerMouseListener(manager);
		verify(mouseManager, never()).registerMouseWheelListener(manager);
		verify(launcherProvider, never()).get();
	}

	@Test
	void openingIsLazyAndClosingReleasesRendererAndSelectedData()
	{
		manager.startUp();

		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(journalProvider).get();
		verify(overlayManager).add(journalOverlay);
		verify(dataSource).setOpen(true);
		verify(dataSource).afterQuestUpdate(42);
		clearInvocations(dataSource, journalOverlay, overlayManager);

		manager.keyPressed(keyPressed(KeyEvent.VK_ESCAPE));

		verify(journalOverlay).release();
		verify(overlayManager).remove(journalOverlay);
		verify(dataSource).setOpen(false);
		verify(dataSource, never()).shutDown();
		assertFalse(manager.isJournalOpen());
	}

	@Test
	void subscribedGameTickRefreshesAfterQuestHelperPublishesState()
		throws ReflectiveOperationException
	{
		Subscribe subscription = QuestJournalManager.class
			.getMethod("onGameTick", GameTick.class)
			.getAnnotation(Subscribe.class);
		assertEquals(-2.0f, subscription.priority());

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);
		when(client.getTickCount()).thenReturn(43);

		manager.onGameTick(mock(GameTick.class));

		verify(dataSource).afterQuestUpdate(43);
	}

	@Test
	void closingPersistsJournalViewBeforeReleasingTheRenderer()
	{
		ViewState state = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		when(journalOverlay.captureViewState(any(), any())).thenReturn(state);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(journalOverlay, viewStateStore);

		manager.keyPressed(keyPressed(KeyEvent.VK_ESCAPE));

		InOrder closing = inOrder(journalOverlay, viewStateStore);
		closing.verify(journalOverlay).captureViewState(
			any(),
			any(JournalSnapshot.QuestFilter.class));
		closing.verify(viewStateStore).save(state);
		closing.verify(journalOverlay).release();
	}

	@Test
	void storedJournalViewRestoresFiltersSelectionAndOverlayStateOnOpen()
	{
		JournalSnapshot.QuestFilter restoredFilter = JournalSnapshot.QuestFilter.all()
			.withTypeSelections(Collections.singleton(JournalSnapshot.QuestType.MINIQUEST))
			.withDifficultySelections(Collections.singleton(
				JournalSnapshot.QuestDifficulty.INTERMEDIATE))
			.withMembershipSelections(Collections.singleton(
				JournalSnapshot.QuestMembership.MEMBERS))
			.withStateSelections(Collections.singleton(
				JournalSnapshot.QuestState.IN_PROGRESS))
			.withOrder(JournalSnapshot.QuestOrder.RELEASE_DATE);
		ViewState state = ViewState.fromFilter(
			"COOKS_ASSISTANT",
			44,
			55,
			66,
			restoredFilter.withStarredSelected(false),
			true,
			Collections.singleton("section:start"));
		when(viewStateStore.load()).thenReturn(state);
		when(dataSource.getSnapshot()).thenReturn(catalogSnapshot("COOKS_ASSISTANT"));
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(dataSource).browseQuest("COOKS_ASSISTANT");
		verify(journalOverlay).restoreViewState(state);
		assertEquals(restoredFilter.getTypes(), manager.getQuestFilter().getTypes());
		assertEquals(restoredFilter.getDifficulties(), manager.getQuestFilter().getDifficulties());
		assertEquals(restoredFilter.getMemberships(), manager.getQuestFilter().getMemberships());
		assertEquals(restoredFilter.getStates(), manager.getQuestFilter().getStates());
		assertEquals(restoredFilter.getOrder(), manager.getQuestFilter().getOrder());
		assertFalse(manager.getQuestFilter().isStarredSelected());

		GameStateChanged loading = mock(GameStateChanged.class);
		when(loading.getGameState()).thenReturn(GameState.LOADING);
		manager.onGameStateChanged(loading);
		manager.afterQuestUpdate(43);

		assertEquals(restoredFilter.getTypes(), manager.getQuestFilter().getTypes());
		assertEquals(restoredFilter.getOrder(), manager.getQuestFilter().getOrder());
		assertFalse(manager.getQuestFilter().isStarredSelected());
	}

	@Test
	void automaticModeDoesNotActivateAQuestRestoredFromPersistentViewState()
	{
		when(config.journalChooseActiveQuestManually()).thenReturn(false);
		ViewState state = ViewState.fromFilter(
			"COOKS_ASSISTANT",
			0,
			0,
			0,
			JournalSnapshot.QuestFilter.all(),
			false,
			Collections.emptySet());
		when(viewStateStore.load()).thenReturn(state);
		when(dataSource.getSnapshot()).thenReturn(catalogSnapshot("COOKS_ASSISTANT"));
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(dataSource).browseQuest("COOKS_ASSISTANT");
		verify(dataSource, never()).activateQuest(any());
	}

	@Test
	void automaticModeBrowsesThenActivatesAnExplicitQuestSelection()
	{
		when(config.journalChooseActiveQuestManually()).thenReturn(false);
		JournalSnapshot source = catalogSnapshot("COOKS_ASSISTANT");
		when(dataSource.getSnapshot()).thenReturn(source);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(source);
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);
		when(dataSource.activateQuest("COOKS_ASSISTANT")).thenReturn(true);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);

		manager.toggleBrowsedQuestIfReleased("COOKS_ASSISTANT", "COOKS_ASSISTANT");

		InOrder selection = inOrder(dataSource);
		selection.verify(dataSource).browseQuest("COOKS_ASSISTANT");
		selection.verify(dataSource).activateQuest("COOKS_ASSISTANT");
		selection.verify(dataSource).markViewedQuestDirty();
		selection.verify(dataSource).afterQuestUpdate(42);
	}

	@Test
	void automaticModeStopsGuidanceWhenAFinishedQuestIsFocused()
	{
		when(config.journalChooseActiveQuestManually()).thenReturn(false);
		JournalSnapshot.QuestListItem active = questListItem(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot.QuestListItem finished = questListItem(
			"DRAGON_SLAYER_I",
			"Dragon Slayer I",
			JournalSnapshot.QuestState.COMPLETE);
		JournalSnapshot source = selectedActiveCatalogSnapshot(
			active,
			java.util.Arrays.asList(active, finished));
		when(dataSource.getSnapshot()).thenReturn(source);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(source);
		when(dataSource.browseQuest("DRAGON_SLAYER_I")).thenReturn(true);
		when(dataSource.stopActiveQuest("COOKS_ASSISTANT")).thenReturn(true);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);

		manager.toggleBrowsedQuestIfReleased("DRAGON_SLAYER_I", "DRAGON_SLAYER_I");

		InOrder selection = inOrder(dataSource);
		selection.verify(dataSource).browseQuest("DRAGON_SLAYER_I");
		selection.verify(dataSource).stopActiveQuest("COOKS_ASSISTANT");
		selection.verify(dataSource).markViewedQuestDirty();
		selection.verify(dataSource).afterQuestUpdate(42);
		verify(dataSource, never()).activateQuest("DRAGON_SLAYER_I");
	}

	@Test
	void automaticModeStopsGuidanceWhenTheFocusedQuestIsCleared()
	{
		when(config.journalChooseActiveQuestManually()).thenReturn(false);
		JournalSnapshot.QuestListItem active = questListItem(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot source = selectedActiveCatalogSnapshot(
			active,
			Collections.singletonList(active));
		when(dataSource.getSnapshot()).thenReturn(source);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(source);
		when(dataSource.clearBrowsedQuest("COOKS_ASSISTANT")).thenReturn(true);
		when(dataSource.stopActiveQuest("COOKS_ASSISTANT")).thenReturn(true);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);

		manager.toggleBrowsedQuestIfReleased("COOKS_ASSISTANT", "COOKS_ASSISTANT");

		InOrder selection = inOrder(dataSource);
		selection.verify(dataSource).clearBrowsedQuest("COOKS_ASSISTANT");
		selection.verify(dataSource).stopActiveQuest("COOKS_ASSISTANT");
		selection.verify(dataSource).markViewedQuestDirty();
		selection.verify(dataSource).afterQuestUpdate(42);
		verify(dataSource, never()).activateQuest(any());
	}

	@Test
	void automaticModeStopsGuidanceWhenTheFocusedQuestBecomesComplete()
	{
		when(config.journalChooseActiveQuestManually()).thenReturn(false);
		JournalSnapshot.QuestListItem active = questListItem(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
		JournalSnapshot initial = selectedActiveCatalogSnapshot(
			active,
			Collections.singletonList(active));
		when(dataSource.getSnapshot()).thenReturn(initial);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(initial);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);

		JournalSnapshot.QuestListItem finished = questListItem(
			"COOKS_ASSISTANT",
			"Cook's Assistant",
			JournalSnapshot.QuestState.COMPLETE);
		JournalSnapshot completed = selectedActiveCatalogSnapshot(
			finished,
			Collections.singletonList(finished));
		when(dataSource.getSnapshot()).thenReturn(completed);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(completed);
		when(dataSource.stopActiveQuest("COOKS_ASSISTANT")).thenReturn(true);

		manager.afterQuestUpdate(43);

		InOrder completion = inOrder(dataSource);
		completion.verify(dataSource).afterQuestUpdate(43);
		completion.verify(dataSource).stopActiveQuest("COOKS_ASSISTANT");
		completion.verify(dataSource).markViewedQuestDirty();
		completion.verify(dataSource).afterQuestUpdate(43);
	}

	@Test
	void disablingManualModeActivatesTheAlreadyFocusedQuest()
	{
		JournalSnapshot source = catalogSnapshot("COOKS_ASSISTANT");
		JournalSnapshot selected = selectedCatalogSnapshot("COOKS_ASSISTANT");
		when(dataSource.getSnapshot()).thenReturn(source);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(selected);
		when(dataSource.activateQuest("COOKS_ASSISTANT")).thenReturn(true);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);
		when(config.journalChooseActiveQuestManually()).thenReturn(false);

		manager.onConfigChanged(configChanged("journalChooseActiveQuestManually"));

		verify(dataSource).activateQuest("COOKS_ASSISTANT");
		verify(dataSource).markViewedQuestDirty();
		verify(dataSource).afterQuestUpdate(42);
	}

	@Test
	void closingAndReopeningPreservesSelectedQuestAndRestoresAllThreeScrollOffsets()
	{
		ViewState state = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		JournalSnapshot selected = selectedCatalogSnapshot("COOKS_ASSISTANT");
		when(dataSource.getSnapshot()).thenReturn(selected);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenReturn(selected);
		when(journalOverlay.captureViewState(any(), any())).thenReturn(state);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		manager.keyReleased(keyReleased(KeyEvent.VK_J));
		manager.keyPressed(keyPressed(KeyEvent.VK_ESCAPE));
		clearInvocations(journalOverlay, dataSource);

		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(dataSource, never()).browseQuest("COOKS_ASSISTANT");
		verify(dataSource, never()).clearBrowsedQuest("COOKS_ASSISTANT");
		verify(journalOverlay).restoreViewState(state);
	}

	@Test
	void delayedQuestCatalogRetriesStoredViewWithoutOverwritingIt()
	{
		ViewState state = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		when(viewStateStore.load()).thenReturn(state);
		AtomicReference<JournalSnapshot> source = new AtomicReference<>(emptySnapshot());
		when(dataSource.getSnapshot()).thenAnswer(invocation -> source.get());
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenAnswer(invocation -> source.get());
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(dataSource, never()).browseQuest("COOKS_ASSISTANT");
		verify(journalOverlay, never()).restoreViewState(state);
		verify(viewStateStore, never()).save(any());

		source.set(catalogSnapshot("COOKS_ASSISTANT"));
		manager.afterQuestUpdate(43);

		verify(dataSource).browseQuest("COOKS_ASSISTANT");
		verify(journalOverlay).restoreViewState(state);
		verify(viewStateStore, never()).save(any());
	}

	@Test
	void closingBeforeTheCatalogArrivesKeepsTheStoredViewForReopen()
	{
		ViewState state = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		when(viewStateStore.load()).thenReturn(state);
		AtomicReference<JournalSnapshot> source = new AtomicReference<>(emptySnapshot());
		when(dataSource.getSnapshot()).thenAnswer(invocation -> source.get());
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenAnswer(invocation -> source.get());
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		manager.keyReleased(keyReleased(KeyEvent.VK_J));
		manager.keyPressed(keyPressed(KeyEvent.VK_ESCAPE));

		verify(journalOverlay, never()).captureViewState(any(), any());
		verify(viewStateStore, never()).save(any());
		source.set(catalogSnapshot("COOKS_ASSISTANT"));
		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(dataSource).browseQuest("COOKS_ASSISTANT");
		verify(journalOverlay).restoreViewState(state);
	}

	@Test
	void missingProfileStateWaitsForCatalogThenUsesThatProfilesDefaults()
	{
		ViewState oldProfileState = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		when(viewStateStore.load()).thenReturn(
			oldProfileState,
			ViewState.empty());
		AtomicReference<JournalSnapshot> source = new AtomicReference<>(
			catalogSnapshot("COOKS_ASSISTANT"));
		when(dataSource.getSnapshot()).thenAnswer(invocation -> source.get());
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class)))
			.thenAnswer(invocation -> source.get());
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource, journalOverlay);
		source.set(emptySnapshot());

		manager.onRuneScapeProfileChanged(mock(RuneScapeProfileChanged.class));

		verify(journalOverlay).clearPersistentViewState();
		verify(dataSource).clearBrowsedQuest();
		verify(dataSource, never()).browseActiveQuest();
		source.set(catalogSnapshotWithConfiguredDefaults("DRAGON_SLAYER_I"));
		manager.afterQuestUpdate(43);

		verify(dataSource, never()).browseActiveQuest();
		assertEquals(
			JournalSnapshot.QuestListOptions.defaults().getConfiguredTypes(),
			manager.getQuestFilter().getTypes());
		assertEquals(
			JournalSnapshot.QuestOrder.RELEASE_DATE,
			manager.getQuestFilter().getOrder());
		assertTrue(manager.getQuestFilter().isStarredSelected());
	}

	@Test
	void routineStateFlushDoesNotReapplyTheViewOnTheNextTick()
	{
		ViewState state = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		when(journalOverlay.captureViewState(any(), any())).thenReturn(state);
		when(journalOverlay.isRendered()).thenReturn(true);
		when(journalOverlay.contains(any(Point.class))).thenReturn(true);
		when(journalOverlay.scrollAt(any(Point.class), anyInt())).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		manager.mouseWheelMoved(mouseWheelMoved(200, 200, 1));
		manager.afterQuestUpdate(43);
		clearInvocations(dataSource, journalOverlay);

		manager.afterQuestUpdate(44);

		verify(dataSource, never()).browseQuest(any());
		verify(journalOverlay, never()).restoreViewState(any());
	}

	@Test
	void awtMouseInputUsesPublishedMenuStateWithoutReadingTheClient()
	{
		when(journalOverlay.isRendered()).thenReturn(true);
		when(journalOverlay.contains(any(Point.class))).thenReturn(true);
		when(journalOverlay.scrollAt(any(Point.class), anyInt())).thenReturn(true);
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		manager.onMenuOpened(mock(MenuOpened.class));
		clearInvocations(client);

		MouseWheelEvent blocked = mouseWheelMoved(200, 200, 1);
		manager.mouseWheelMoved(blocked);

		assertFalse(blocked.isConsumed());
		verify(client, never()).isMenuOpen();
		verify(client, never()).getGameState();

		manager.onMenuOptionClicked(mock(MenuOptionClicked.class));
		MouseWheelEvent available = mouseWheelMoved(200, 200, 1);
		manager.mouseWheelMoved(available);

		assertTrue(available.isConsumed());
		verify(client, never()).isMenuOpen();
		verify(client, never()).getGameState();
	}

	@Test
	void launcherUsesRuneLiteMenuWhileSuppressingSceneActions()
	{
		when(launcherOverlay.isRendered()).thenReturn(true);
		when(launcherOverlay.contains(any(Point.class))).thenReturn(true);
		Menu menu = mock(Menu.class);
		MenuEntry cancel = mock(MenuEntry.class);
		MenuEntry sceneAction = mock(MenuEntry.class);
		MenuEntry overlayAction = mock(MenuEntry.class);
		MenuEntry hideAction = mock(MenuEntry.class);
		when(client.getMenu()).thenReturn(menu);
		when(cancel.getType()).thenReturn(MenuAction.CANCEL);
		when(sceneAction.getType()).thenReturn(MenuAction.GAME_OBJECT_FIRST_OPTION);
		when(overlayAction.getType()).thenReturn(MenuAction.RUNELITE_OVERLAY);
		when(menu.getMenuEntries()).thenReturn(new MenuEntry[]{cancel, sceneAction, overlayAction});
		when(menu.createMenuEntry(-1)).thenReturn(hideAction);
		when(hideAction.setOption(any())).thenReturn(hideAction);
		when(hideAction.setTarget(any())).thenReturn(hideAction);
		when(hideAction.setType(any())).thenReturn(hideAction);
		when(hideAction.onClick(any())).thenReturn(hideAction);
		manager.startUp();
		manager.mouseMoved(mouseMoved(20, 20));

		manager.onPostMenuSort(mock(PostMenuSort.class));

		verify(menu).setMenuEntries(argThat(
			entries -> entries.length == 1 && entries[0] == cancel));
		verify(menu).createMenuEntry(-1);
		verify(hideAction).setOption("Hide button");
		verify(hideAction).setTarget(argThat(target -> target.contains("Quest Journal")));
		verify(hideAction).setType(MenuAction.RUNELITE_OVERLAY);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Consumer<MenuEntry>> callback = ArgumentCaptor.forClass(Consumer.class);
		verify(hideAction).onClick(callback.capture());

		callback.getValue().accept(hideAction);

		verify(configManager).setConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			"journalShowButton",
			false);
	}

	@Test
	void hidingLauncherThroughConfigurationRemovesItsOverlayAndClosedInput()
	{
		manager.startUp();

		when(config.journalShowButton()).thenReturn(false);
		manager.onConfigChanged(configChanged("journalShowButton"));

		verify(launcherOverlay).resetHitState();
		verify(overlayManager).remove(launcherOverlay);
		verify(mouseManager).unregisterMouseListener(manager);
		verify(mouseManager).unregisterMouseWheelListener(manager);
	}

	@Test
	void loadingPreservesAnOpenJournalButResetsInteractionState()
	{
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource, journalOverlay, overlayManager);

		GameStateChanged loading = mock(GameStateChanged.class);
		when(loading.getGameState()).thenReturn(GameState.LOADING);
		manager.onGameStateChanged(loading);

		verify(journalOverlay, never()).release();
		verify(overlayManager, never()).remove(journalOverlay);
		verify(dataSource, never()).setOpen(false);
		org.junit.jupiter.api.Assertions.assertTrue(manager.isJournalOpen());
	}

	@Test
	void loadingFlushesDirtyViewBeforeTheProfileStateChanges()
	{
		ViewState state = SAVED_COOKS_ASSISTANT_VIEW_STATE;
		when(journalOverlay.captureViewState(any(), any())).thenReturn(state);
		when(journalOverlay.isRendered()).thenReturn(true);
		when(journalOverlay.contains(any(Point.class))).thenReturn(true);
		when(journalOverlay.scrollAt(any(Point.class), anyInt())).thenReturn(true);

		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		manager.mouseWheelMoved(mouseWheelMoved(200, 200, 1));
		clearInvocations(viewStateStore);
		GameStateChanged loading = mock(GameStateChanged.class);
		when(loading.getGameState()).thenReturn(GameState.LOADING);

		manager.onGameStateChanged(loading);
		manager.onRuneScapeProfileChanged(mock(RuneScapeProfileChanged.class));

		InOrder transition = inOrder(viewStateStore);
		transition.verify(viewStateStore).save(state);
		transition.verify(viewStateStore).load();
	}

	@Test
	void fullRequirementChangeInvalidatesWhileClosedAndDefersEvaluationUntilOpen()
	{
		manager.startUp();
		clearInvocations(dataSource, journalProvider);

		manager.onConfigChanged(configChanged("showFullRequirements"));

		verify(dataSource).invalidate();
		verify(dataSource, never()).afterQuestUpdate(anyInt());
		verify(journalProvider, never()).get();

		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		verify(journalProvider).get();
		verify(dataSource).setOpen(true);
		verify(dataSource).afterQuestUpdate(42);
		assertTrue(manager.isJournalOpen());
	}

	@Test
	void dynamicEventsAreNearZeroWorkWhileClosedAndDirtyOnlyWhileOpen()
	{
		manager.startUp();
		clearInvocations(dataSource);

		manager.onItemContainerChanged(mock(ItemContainerChanged.class));
		manager.onVarbitChanged(mock(VarbitChanged.class));
		manager.onStatChanged(mock(StatChanged.class));

		verify(dataSource, never()).markViewedQuestDirty();

		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);
		manager.onItemContainerChanged(mock(ItemContainerChanged.class));
		manager.onVarbitChanged(mock(VarbitChanged.class));
		manager.onStatChanged(mock(StatChanged.class));

		verify(dataSource, times(3)).markViewedQuestDirty();
		verify(dataSource, never()).afterQuestUpdate(anyInt());
	}

	@Test
	void selectedViewConfigChangesMarkTheOpenJournalDirty()
	{
		manager.startUp();
		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		clearInvocations(dataSource);

		manager.onConfigChanged(configChanged("hideQuestRewards"));
		manager.onConfigChanged(configChanged("passColour"));
		manager.onConfigChanged(configChanged("failColour"));
		manager.onConfigChanged(configChanged("partialSuccessColour"));
		manager.onConfigChanged(configChanged("boostColour"));

		verify(dataSource, times(5)).markViewedQuestDirty();
		verify(dataSource, never()).afterQuestUpdate(anyInt());
	}

	@Test
	void rapidQueuedToggleUsesRequestedStateInsteadOfAppliedState()
	{
		when(config.journalShowButton()).thenReturn(false);
		manager.startUp();

		List<Runnable> queued = new ArrayList<>();
		when(client.isClientThread()).thenReturn(false);
		doAnswer(invocation ->
		{
			queued.add(invocation.getArgument(0));
			return null;
		}).when(clientThread).invokeLater(any(Runnable.class));

		KeyEvent key = keyPressed(KeyEvent.VK_J);
		manager.keyPressed(key);
		manager.keyReleased(new KeyEvent(
			new Canvas(), KeyEvent.KEY_RELEASED, 2L, 0,
			KeyEvent.VK_J, KeyEvent.CHAR_UNDEFINED));
		manager.keyPressed(keyPressed(KeyEvent.VK_J));

		assertEquals(2, queued.size());
		queued.forEach(Runnable::run);
		verify(journalProvider, never()).get();
		assertFalse(manager.isJournalOpen());
	}

	@Test
	void queuedOpenCannotWinAfterMasterDisable()
	{
		manager.startUp();
		clearInvocations(journalProvider, overlayManager, dataSource);

		List<Runnable> queued = new ArrayList<>();
		when(client.isClientThread()).thenReturn(false);
		doAnswer(invocation ->
		{
			queued.add(invocation.getArgument(0));
			return null;
		}).when(clientThread).invokeLater(any(Runnable.class));

		manager.keyPressed(keyPressed(KeyEvent.VK_J));
		when(config.enableQuestJournal()).thenReturn(false);
		manager.onConfigChanged(configChanged("enableQuestJournal"));
		manager.onConfigChanged(configChanged("journalKeepWithinGameArea"));

		assertEquals(2, queued.size());
		queued.get(0).run();
		verify(journalProvider, never()).get();
		when(client.isClientThread()).thenReturn(true);
		queued.get(1).run();

		verify(overlayManager).remove(launcherOverlay);
		verify(dataSource).shutDown();
		verify(keyManager).unregisterKeyListener(manager);
		verify(mouseManager).unregisterMouseListener(manager);
		verify(mouseManager).unregisterMouseWheelListener(manager);
		assertFalse(manager.isJournalOpen());
	}

	@Test
	void nativeQuestJournalClickQueuesOneOpenAndBrowseTransition()
	{
		when(config.journalReplaceNativeQuestJournal()).thenReturn(true);
		when(dataSource.findQuestIdByTitle("Cook's Assistant"))
			.thenReturn("COOKS_ASSISTANT");
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);
		manager.startUp();

		List<Runnable> queued = new ArrayList<>();
		when(client.isClientThread()).thenReturn(false);
		doAnswer(invocation ->
		{
			queued.add(invocation.getArgument(0));
			return null;
		}).when(clientThread).invokeLater(any(Runnable.class));
		MenuOptionClicked event = questJournalClick(
			"Read journal:", "Cook's Assistant", InterfaceID.Questlist.LIST);

		manager.onMenuOptionClicked(event);

		verify(dataSource).findQuestIdByTitle("Cook's Assistant");
		verify(event).consume();
		assertEquals(1, queued.size());
		verify(journalProvider, never()).get();
		verify(dataSource, never()).browseQuest(any());

		queued.get(0).run();

		verify(journalProvider).get();
		verify(overlayManager).add(journalOverlay);
		verify(dataSource).setOpen(true);
		verify(dataSource).browseQuest("COOKS_ASSISTANT");
		verify(dataSource, never()).activateQuest(any());
		assertTrue(manager.isJournalOpen());
	}

	@Test
	void nativeQuestSelectionActivatesOnlyWhenAutomaticModeIsEnabled()
	{
		when(config.journalReplaceNativeQuestJournal()).thenReturn(true);
		when(config.journalChooseActiveQuestManually()).thenReturn(false);
		JournalSnapshot source = catalogSnapshot("COOKS_ASSISTANT");
		when(dataSource.getSnapshot()).thenReturn(source);
		when(dataSource.getSnapshot(any(JournalSnapshot.QuestFilter.class))).thenReturn(source);
		when(dataSource.findQuestIdByTitle("Cook's Assistant"))
			.thenReturn("COOKS_ASSISTANT");
		when(dataSource.browseQuest("COOKS_ASSISTANT")).thenReturn(true);
		when(dataSource.activateQuest("COOKS_ASSISTANT")).thenReturn(true);
		manager.startUp();
		clearInvocations(dataSource);
		MenuOptionClicked event = questJournalClick(
			"Read journal:", "Cook's Assistant", InterfaceID.Questlist.LIST);

		manager.onMenuOptionClicked(event);

		InOrder selection = inOrder(dataSource);
		selection.verify(dataSource).browseQuest("COOKS_ASSISTANT");
		selection.verify(dataSource).activateQuest("COOKS_ASSISTANT");
		verify(event).consume();
	}

	@Test
	void nativeQuestJournalActionMustMatchExactly()
	{
		when(config.journalReplaceNativeQuestJournal()).thenReturn(true);
		manager.startUp();
		MenuOptionClicked missingColon = questJournalClick(
			"Read journal", "Cook's Assistant", InterfaceID.Questlist.LIST);
		MenuOptionClicked wrongCase = questJournalClick(
			"read journal:", "Cook's Assistant", InterfaceID.Questlist.LIST);

		manager.onMenuOptionClicked(missingColon);
		manager.onMenuOptionClicked(wrongCase);

		verify(missingColon, never()).consume();
		verify(wrongCase, never()).consume();
		verify(dataSource, never()).findQuestIdByTitle(any());
	}

	@Test
	void disabledNativeReplacementLeavesVanillaClickUntouched()
	{
		when(config.journalReplaceNativeQuestJournal()).thenReturn(false);
		manager.startUp();
		MenuOptionClicked event = questJournalClick(
			"Read journal:", "Cook's Assistant", InterfaceID.Questlist.LIST);

		manager.onMenuOptionClicked(event);

		verify(event, never()).consume();
		verify(dataSource, never()).findQuestIdByTitle(any());
	}

	@Test
	void unavailableOrUnknownNativeQuestLeavesVanillaClickUntouched()
	{
		when(config.journalReplaceNativeQuestJournal()).thenReturn(true);
		when(dataSource.findQuestIdByTitle("Unavailable Quest")).thenReturn(null);
		manager.startUp();
		MenuOptionClicked event = questJournalClick(
			"Read journal:", "Unavailable Quest", InterfaceID.Questlist.LIST);

		manager.onMenuOptionClicked(event);

		verify(dataSource).findQuestIdByTitle("Unavailable Quest");
		verify(event, never()).consume();
		verify(journalProvider, never()).get();
		verify(dataSource, never()).browseQuest(any());
	}

	@Test
	void wrongWidgetOrConsumedClickLeavesVanillaClickUntouched()
	{
		when(config.journalReplaceNativeQuestJournal()).thenReturn(true);
		manager.startUp();
		MenuOptionClicked wrongWidget = questJournalClick(
			"Read journal:", "Cook's Assistant", InterfaceID.Questlist.LIST + 1);
		MenuOptionClicked consumed = questJournalClick(
			"Read journal:", "Cook's Assistant", InterfaceID.Questlist.LIST);
		when(consumed.isConsumed()).thenReturn(true);

		manager.onMenuOptionClicked(wrongWidget);
		manager.onMenuOptionClicked(consumed);

		verify(wrongWidget, never()).consume();
		verify(consumed, never()).consume();
		verify(dataSource, never()).findQuestIdByTitle(any());
	}

	private ConfigChanged configChanged(String key)
	{
		ConfigChanged event = mock(ConfigChanged.class);
		when(event.getGroup()).thenReturn(QuestHelperConfig.QUEST_HELPER_GROUP);
		when(event.getKey()).thenReturn(key);
		return event;
	}

	private static MenuOptionClicked questJournalClick(String option, String target, int widgetId)
	{
		MenuOptionClicked event = mock(MenuOptionClicked.class);
		when(event.getMenuOption()).thenReturn(option);
		when(event.getMenuTarget()).thenReturn(target);
		when(event.getWidgetId()).thenReturn(widgetId);
		return event;
	}

	private static KeyEvent keyPressed(int keyCode)
	{
		return new KeyEvent(
			new Canvas(),
			KeyEvent.KEY_PRESSED,
			1L,
			0,
			keyCode,
			KeyEvent.CHAR_UNDEFINED);
	}

	private static KeyEvent keyReleased(int keyCode)
	{
		return new KeyEvent(
			new Canvas(),
			KeyEvent.KEY_RELEASED,
			2L,
			0,
			keyCode,
			KeyEvent.CHAR_UNDEFINED);
	}

	private static MouseWheelEvent mouseWheelMoved(int x, int y, int rotation)
	{
		return new MouseWheelEvent(
			new Canvas(),
			MouseEvent.MOUSE_WHEEL,
			1L,
			0,
			x,
			y,
			0,
			false,
			MouseWheelEvent.WHEEL_UNIT_SCROLL,
			1,
			rotation);
	}

	private static MouseEvent mouseMoved(int x, int y)
	{
		return new MouseEvent(
			new Canvas(),
			MouseEvent.MOUSE_MOVED,
			1L,
			0,
			x,
			y,
			0,
			false,
			MouseEvent.NOBUTTON);
	}

	private static JournalSnapshot emptySnapshot()
	{
		return new JournalSnapshot(
			Collections.emptyList(),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 0, 0, 0));
	}

	private static JournalSnapshot catalogSnapshot(String questId)
	{
		return new JournalSnapshot(
			Collections.singletonList(new JournalSnapshot.QuestListItem(
				questId,
				"Cook's Assistant",
				JournalSnapshot.QuestType.MINIQUEST,
				JournalSnapshot.QuestState.IN_PROGRESS,
				JournalSnapshot.QuestDifficulty.INTERMEDIATE,
				true,
				Collections.emptyMap())),
			null,
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
	}

	private static JournalSnapshot selectedCatalogSnapshot(String questId)
	{
		JournalSnapshot.QuestListItem item = questListItem(questId);
		return new JournalSnapshot(
			Collections.singletonList(item),
			new JournalSnapshot.SelectedQuest(
				new JournalSnapshot.QuestOverview(
					questId,
					item.getTitle(),
					item.getType(),
					item.getState(),
					item.getDifficulty(),
					item.isMembers()),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList()),
			null,
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
	}

	private static JournalSnapshot catalogSnapshotWithConfiguredDefaults(String questId)
	{
		JournalSnapshot.QuestListOptions options = new JournalSnapshot.QuestListOptions(
			null,
			JournalSnapshot.QuestOrder.RELEASE_DATE);
		return new JournalSnapshot(
			Collections.singletonList(questListItem(questId)),
			null,
			null,
			options,
			new JournalSnapshot.QuestProgress(0, 1, 0, 0));
	}

	private static JournalSnapshot.QuestListItem questListItem(String questId)
	{
		return questListItem(
			questId,
			"Cook's Assistant",
			JournalSnapshot.QuestState.IN_PROGRESS);
	}

	private static JournalSnapshot.QuestListItem questListItem(
		String questId,
		String title,
		JournalSnapshot.QuestState state)
	{
		return new JournalSnapshot.QuestListItem(
			questId,
			title,
			JournalSnapshot.QuestType.MINIQUEST,
			state,
			JournalSnapshot.QuestDifficulty.INTERMEDIATE,
			true,
			Collections.emptyMap());
	}

	private static JournalSnapshot selectedActiveCatalogSnapshot(
		JournalSnapshot.QuestListItem selected,
		List<JournalSnapshot.QuestListItem> quests)
	{
		JournalSnapshot.SelectedQuest selectedQuest = new JournalSnapshot.SelectedQuest(
			new JournalSnapshot.QuestOverview(
				selected.getId(),
				selected.getTitle(),
				selected.getType(),
				selected.getState(),
				selected.getDifficulty(),
				selected.isMembers()),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList(),
			Collections.emptyList());
		return new JournalSnapshot(
			quests,
			selectedQuest,
			new JournalSnapshot.ActiveQuest(
				selected.getId(),
				selected.getTitle(),
				selected.getState()),
			JournalSnapshot.QuestListOptions.defaults(),
			new JournalSnapshot.QuestProgress(0, quests.size(), 0, 0));
	}
}
