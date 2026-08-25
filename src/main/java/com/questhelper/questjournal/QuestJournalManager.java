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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;
import net.runelite.client.ui.JagexColors;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.LinkBrowser;

/**
 * Manages Quest Journal lifecycle and input. Rendering and quest mapping stay
 * inactive while the journal is closed.
 */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class QuestJournalManager implements MouseListener, MouseWheelListener, KeyListener
{
	private static final String ENABLE_KEY = "enableQuestJournal";
	private static final String SHOW_QUEST_BUTTON_KEY = "journalShowButton";
	private static final String CHOOSE_ACTIVE_QUEST_MANUALLY_KEY =
		"journalChooseActiveQuestManually";
	private static final String OPEN_HOTKEY_KEY = "journalOpenHotkey";
	private static final String KEEP_WITHIN_GAME_AREA_KEY = "journalKeepWithinGameArea";
	private static final String SHOW_FULL_REQUIREMENTS_KEY = "showFullRequirements";
	private static final String HIDE_QUEST_REWARDS_KEY = "hideQuestRewards";
	private static final String PASS_COLOUR_KEY = "passColour";
	private static final String FAIL_COLOUR_KEY = "failColour";
	private static final String PARTIAL_SUCCESS_COLOUR_KEY = "partialSuccessColour";
	private static final String BOOST_COLOUR_KEY = "boostColour";
	private static final String HIDE_BUTTON_MENU_OPTION = "Hide button";
	private static final String HIDE_BUTTON_MENU_TARGET = "Quest Journal";

	@NonNull
	private final Client client;
	@NonNull
	private final ClientThread clientThread;
	@NonNull
	private final ConfigManager configManager;
	@NonNull
	private final PanelController panelController;
	@NonNull
	private final SearchController searchController;
	@NonNull
	private final FilterController filterController;
	@NonNull
	private final QuestSelectionController questController;
	@NonNull
	private final ViewStateController viewStateController;
	@NonNull
	private final EventBus eventBus;
	@NonNull
	private final QuestHelperConfig config;
	@NonNull
	private final OverlayManager overlayManager;
	@NonNull
	private final MouseManager mouseManager;
	@NonNull
	private final KeyManager keyManager;
	@NonNull
	private final Provider<LauncherOverlay> buttonOverlayProvider;
	@NonNull
	private final Provider<JournalOverlay> journalOverlayProvider;
	@NonNull
	private final Provider<JournalDataSource> dataSourceProvider;
	@NonNull
	private final Provider<QuestHelperPlugin> ownerProvider;

	private final AtomicLong mutationGeneration = new AtomicLong();
	private Consumer<String> wikiBrowser = LinkBrowser::browse;

	private volatile boolean started;
	private volatile boolean enabledRequested;
	private volatile boolean openRequested;
	private volatile boolean enabled;
	private volatile boolean eventBusRegistered;
	private volatile boolean keyRegistered;
	private volatile boolean mouseRegistered;
	private volatile boolean buttonOverlayRegistered;
	private volatile boolean journalOverlayRegistered;
	private volatile boolean journalOpen;
	private volatile JournalSnapshot journalSnapshot;
	private volatile Point pointerCanvas = new Point(-1, -1);
	private volatile boolean hotkeyDown;
	private volatile boolean altOverlayMouseInteraction;
	private volatile boolean middleMouseInteraction;
	private volatile boolean clientMenuOpen;
	private volatile boolean draggingPanel;
	private volatile boolean resizingPanel;
	private volatile boolean titleIconClickCandidate;
	private volatile boolean panelDragOccurred;
	private volatile boolean consumeNextClick;
	private volatile Point panelDragOffset = new Point();
	private volatile PressTarget pressTarget = PressTarget.NONE;
	private volatile String pressedQuestId;
	private volatile String pressedChecklistId;
	private volatile String pressedWikiUrl;
	private volatile JournalOverlay.FilterControl pressedFilterControl;
	private volatile JournalOverlay.FilterSelection pressedFilterSelection;
	private volatile JournalOverlay.ScrollRegion scrollbarRegion;
	private volatile int scrollbarPointerOffset;

	private LauncherOverlay buttonOverlay;
	private JournalOverlay journalOverlay;
	private JournalDataSource dataSource;

	public void startUp()
	{
		if (started)
		{
			return;
		}
		started = true;
		if (!eventBusRegistered)
		{
			eventBus.register(this);
			eventBusRegistered = true;
		}
		requestEnabled(config.enableQuestJournal());
	}

	public void shutDown()
	{
		if (!started && !eventBusRegistered)
		{
			return;
		}
		started = false;
		enabledRequested = false;
		openRequested = false;
		long generation = mutationGeneration.incrementAndGet();
		if (eventBusRegistered)
		{
			eventBus.unregister(this);
			eventBusRegistered = false;
		}
		runOnClientThread(generation, false, () -> disableOnClientThread(true));
	}

	/** Resets persisted journal layout and view preferences. */
	public void resetConfiguration()
	{
		mutationGeneration.incrementAndGet();
		if (client.isClientThread())
		{
			resetConfigurationOnClientThread();
		}
		else
		{
			clientThread.invoke(this::resetConfigurationOnClientThread);
		}
	}

	/**
	 * Runs after Quest Helper's priority -1 tick handler has published its state.
	 */
	@Subscribe(priority = -2.0f)
	public void onGameTick(GameTick event)
	{
		afterQuestUpdate(client.getTickCount());
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		clientMenuOpen = enabled && client.isMenuOpen();
	}

	void afterQuestUpdate(int tick)
	{
		if (!client.isClientThread())
		{
			long generation = mutationGeneration.get();
			clientThread.invokeLater(() ->
			{
				if (generation == mutationGeneration.get())
				{
					afterQuestUpdate(tick);
				}
			});
			return;
		}
		if (!started || !enabledRequested || !openRequested
			|| !enabled || !journalOpen || dataSource == null)
		{
			return;
		}
		dataSource.afterQuestUpdate(tick);
		boolean stateRestored = viewStateController.applyRestoredViewState(dataSource);
		questController.clearFinishedAutomaticQuest(tick);
		refreshSnapshotOnClientThread(false);
		viewStateController.applyPendingOverlayState(journalOverlay, stateRestored);
		viewStateController.flushIfDirty(
			enabled,
			journalOpen,
			journalOverlay,
			questController.getBrowsedQuestId());
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event == null || !QuestHelperConfig.QUEST_HELPER_GROUP.equals(event.getGroup()))
		{
			return;
		}

		String key = event.getKey();
		if (ENABLE_KEY.equals(key))
		{
			requestEnabled(config.enableQuestJournal());
			return;
		}
		if (!enabledRequested)
		{
			return;
		}
		if (!handlesConfigKey(key))
		{
			return;
		}

		long generation = mutationGeneration.get();
		runOnClientThread(generation, true, () -> applyConfigChangeOnClientThread(key));
	}

	@Subscribe(priority = -100)
	public void onPostMenuSort(PostMenuSort event)
	{
		if (!enabled)
		{
			clientMenuOpen = false;
			return;
		}
		clientMenuOpen = client.isMenuOpen();
		if (clientMenuOpen)
		{
			return;
		}

		Point point = getPointerCanvasPoint();
		boolean launcherHit = isLauncherPoint(point);
		if (!launcherHit && !isJournalPoint(point))
		{
			return;
		}

		Menu menu = sanitizeMenuEntries();
		if (launcherHit && menu != null)
		{
			menu.createMenuEntry(-1)
				.setOption(HIDE_BUTTON_MENU_OPTION)
				.setTarget(ColorUtil.wrapWithColorTag(HIDE_BUTTON_MENU_TARGET, JagexColors.MENU_TARGET))
				.setType(MenuAction.RUNELITE_OVERLAY)
				.onClick(entry -> hideQuestJournalButton());
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		markViewedQuestDirty();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		markViewedQuestDirty();
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		markViewedQuestDirty();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event == null)
		{
			return;
		}
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			clientMenuOpen = enabled && client.isMenuOpen();
			return;
		}
		clientMenuOpen = false;
		boolean close = event.getGameState() != GameState.LOADING;
		openRequested = close ? false : journalOpen;
		long generation = mutationGeneration.incrementAndGet();
		runOnClientThread(generation, true, () -> resetForGameStateOnClientThread(close));
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		if (!started || !enabledRequested)
		{
			filterController.unloadStarredQuests();
			return;
		}
		long generation = mutationGeneration.get();
		runOnClientThread(generation, true, this::reloadProfileJournalState);
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		clientMenuOpen = enabled;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event != null)
		{
			clientMenuOpen = false;
		}
		if (event == null
			|| event.isConsumed()
			|| !enabledRequested
			|| !enabled
			|| !config.journalReplaceNativeQuestJournal()
			|| !"Read journal:".equals(event.getMenuOption())
			|| event.getWidgetId() != InterfaceID.Questlist.LIST
			|| dataSource == null)
		{
			return;
		}

		String questId = dataSource.findQuestIdByTitle(event.getMenuTarget());
		if (questId == null)
		{
			return;
		}

		event.consume();
		requestJournalOpenAndBrowseQuest(questId);
	}

	private void requestEnabled(boolean requestedEnabled)
	{
		enabledRequested = requestedEnabled;
		if (!requestedEnabled)
		{
			openRequested = false;
		}
		long generation = mutationGeneration.incrementAndGet();
		runOnClientThread(generation, requestedEnabled, () ->
		{
			if (requestedEnabled && enabledRequested)
			{
				enableOnClientThread();
			}
			else
			{
				disableOnClientThread(false);
			}
		});
	}

	private void runOnClientThread(long generation, boolean requireStarted, Runnable action)
	{
		Runnable guarded = () ->
		{
			if (generation != mutationGeneration.get() || requireStarted && !started)
			{
				return;
			}
			action.run();
		};
		if (client.isClientThread())
		{
			guarded.run();
		}
		else
		{
			clientThread.invokeLater(guarded);
		}
	}

	private static boolean handlesConfigKey(String key)
	{
		return SHOW_QUEST_BUTTON_KEY.equals(key)
			|| OPEN_HOTKEY_KEY.equals(key)
			|| CHOOSE_ACTIVE_QUEST_MANUALLY_KEY.equals(key)
			|| KEEP_WITHIN_GAME_AREA_KEY.equals(key)
			|| SHOW_FULL_REQUIREMENTS_KEY.equals(key)
			|| HIDE_QUEST_REWARDS_KEY.equals(key)
			|| PASS_COLOUR_KEY.equals(key)
			|| FAIL_COLOUR_KEY.equals(key)
			|| PARTIAL_SUCCESS_COLOUR_KEY.equals(key)
			|| BOOST_COLOUR_KEY.equals(key);
	}

	private void markViewedQuestDirty()
	{
		if (started && enabledRequested && openRequested
			&& enabled && journalOpen && dataSource != null)
		{
			dataSource.markViewedQuestDirty();
		}
	}

	private void enableOnClientThread()
	{
		if (!started || !enabledRequested || enabled)
		{
			return;
		}
		enabled = true;
		resetTransientState();
		clientMenuOpen = client.isMenuOpen();
		filterController.reloadStarredQuests();
		viewStateController.load(false);
		filterController.resetToAll();
		panelController.loadConfiguration();

		dataSource = dataSourceProvider.get();
		dataSource.startUp();
		dataSource.setOpen(false);
		questController.attach(dataSource);
		journalSnapshot = dataSource.getSnapshot();

		if (!keyRegistered)
		{
			keyManager.registerKeyListener(this);
			keyRegistered = true;
		}
		syncQuestButtonOverlayVisibilityOnClientThread();
		log.debug("Quest Journal feature enabled");
	}

	private void disableOnClientThread(boolean shuttingDown)
	{
		persistJournalViewState(false);
		journalOpen = false;
		searchController.close(this::updateQuestSearch);
		if (journalOverlayRegistered && journalOverlay != null)
		{
			journalOverlay.release();
			overlayManager.remove(journalOverlay);
			journalOverlayRegistered = false;
		}
		if (buttonOverlayRegistered && buttonOverlay != null)
		{
			buttonOverlay.resetHitState();
			overlayManager.remove(buttonOverlay);
			buttonOverlayRegistered = false;
		}
		if (mouseRegistered)
		{
			mouseManager.unregisterMouseWheelListener(this);
			mouseManager.unregisterMouseListener(this);
			mouseRegistered = false;
		}
		if (keyRegistered)
		{
			keyManager.unregisterKeyListener(this);
			keyRegistered = false;
		}
		if (dataSource != null)
		{
			dataSource.shutDown();
		}
		questController.detach();
		enabled = false;
		journalSnapshot = null;
		filterController.unloadStarredQuests();
		viewStateController.reset();
		resetTransientState();
		if (shuttingDown)
		{
			buttonOverlay = null;
			journalOverlay = null;
			dataSource = null;
		}
		log.debug("Quest Journal feature disabled");
	}

	private void resetTransientState()
	{
		hotkeyDown = false;
		altOverlayMouseInteraction = false;
		middleMouseInteraction = false;
		clientMenuOpen = false;
		consumeNextClick = false;
		filterController.resetListPreferences();
		panelController.resetTransientState();
		pointerCanvas = new Point(-1, -1);
		resetInteraction();
	}

	private void resetConfigurationOnClientThread()
	{
		boolean shouldEnable = started && config.enableQuestJournal();
		enabledRequested = shouldEnable;
		if (shouldEnable && !enabled)
		{
			enableOnClientThread();
		}
		else if (!shouldEnable && enabled)
		{
			disableOnClientThread(false);
		}

		searchController.close(this::updateQuestSearch);
		hotkeyDown = false;
		resetInteraction();
		JournalOverlay resetJournalOverlay = journalOverlay();
		LauncherOverlay resetButtonOverlay = buttonOverlay();
		panelController.resetConfiguration(resetJournalOverlay);
		overlayManager.resetOverlay(resetButtonOverlay);
		resetButtonOverlay.resetHitState();

		viewStateController.resetConfiguration();
		filterController.resetConfiguration();
		questController.clearBrowsedQuest();
		if (!journalOpen)
		{
			resetJournalOverlay.clearPersistentViewState();
		}

		if (!enabled || dataSource == null)
		{
			return;
		}
		syncQuestButtonOverlayVisibilityOnClientThread();
		if (journalOpen)
		{
			dataSource.markViewedQuestDirty();
			refreshOpenedJournalState();
		}
	}

	private void applyConfigChangeOnClientThread(String key)
	{
		if (!enabledRequested || !enabled)
		{
			return;
		}
		switch (key)
		{
			case SHOW_QUEST_BUTTON_KEY:
				syncQuestButtonOverlayVisibilityOnClientThread();
				break;
			case OPEN_HOTKEY_KEY:
				hotkeyDown = false;
				break;
			case CHOOSE_ACTIVE_QUEST_MANUALLY_KEY:
				resetInteraction();
				if (!isManualActiveQuestSelection())
				{
					synchronizeFocusedQuestAutomaticallyOnClientThread();
				}
				break;
			case KEEP_WITHIN_GAME_AREA_KEY:
				if (draggingPanel || resizingPanel)
				{
					panelController.saveBounds();
				}
				resetInteraction();
				panelController.invalidateMovementConstraint();
				break;
			case SHOW_FULL_REQUIREMENTS_KEY:
				if (dataSource != null)
				{
					dataSource.invalidate();
					if (journalOpen)
					{
						dataSource.afterQuestUpdate(client.getTickCount());
						refreshSnapshotOnClientThread(false);
					}
				}
				break;
			case HIDE_QUEST_REWARDS_KEY:
			case PASS_COLOUR_KEY:
			case FAIL_COLOUR_KEY:
			case PARTIAL_SUCCESS_COLOUR_KEY:
			case BOOST_COLOUR_KEY:
				markViewedQuestDirty();
				break;
			default:
				break;
		}
	}

	private void resetForGameStateOnClientThread(boolean close)
	{
		if (!enabled)
		{
			return;
		}
		if (close)
		{
			applyJournalOpenOnClientThread(false);
		}
		else
		{
			persistJournalViewState(false);
			markViewedQuestDirty();
		}
		if (close)
		{
			filterController.resetListPreferences();
		}
		hotkeyDown = false;
		panelController.invalidateContentBoundsCache();
		altOverlayMouseInteraction = false;
		middleMouseInteraction = false;
		consumeNextClick = false;
		if (buttonOverlay != null)
		{
			buttonOverlay.resetHitState();
		}
		resetInteraction();
	}

	private void requestJournalOpen(boolean open)
	{
		openRequested = open;
		long generation = mutationGeneration.incrementAndGet();
		runOnClientThread(generation, true, () ->
		{
			if (openRequested == open && enabledRequested)
			{
				applyJournalOpenOnClientThread(open);
			}
		});
	}

	private void applyJournalOpenOnClientThread(boolean open)
	{
		if (!enabledRequested || !enabled || openRequested != open || journalOpen == open)
		{
			return;
		}
		if (open && client.getGameState() != GameState.LOGGED_IN)
		{
			openRequested = false;
			return;
		}
		if (!open)
		{
			persistJournalViewState(true);
		}
		journalOpen = open;
		JournalDataSource directSource = dataSource;
		if (open)
		{
			JournalOverlay overlay = journalOverlay();
			if (!journalOverlayRegistered)
			{
				overlayManager.add(overlay);
				journalOverlayRegistered = true;
			}
			syncMouseInputRegistration();
			directSource.setOpen(true);
			refreshOpenedJournalState();
		}
		else
		{
			searchController.close(this::updateQuestSearch);
			if (journalOverlay != null)
			{
				journalOverlay.release();
			}
			resetInteraction();
			if (directSource != null)
			{
				directSource.setOpen(false);
			}
			if (journalOverlayRegistered && journalOverlay != null)
			{
				overlayManager.remove(journalOverlay);
				journalOverlayRegistered = false;
			}
			syncMouseInputRegistration();
		}
	}

	private void toggleJournal()
	{
		requestJournalOpen(!openRequested);
	}

	private void requestJournalOpenAndBrowseQuest(String questId)
	{
		openRequested = true;
		long generation = mutationGeneration.incrementAndGet();
		runOnClientThread(generation, true, () ->
		{
			if (!enabledRequested || !enabled || !openRequested)
			{
				return;
			}
			applyJournalOpenOnClientThread(true);
			applyQuestMutation(() -> questController.browseQuest(questId));
		});
	}

	private LauncherOverlay buttonOverlay()
	{
		if (buttonOverlay == null)
		{
			buttonOverlay = buttonOverlayProvider.get();
		}
		return buttonOverlay;
	}

	private JournalOverlay journalOverlay()
	{
		if (journalOverlay == null)
		{
			journalOverlay = journalOverlayProvider.get();
		}
		return journalOverlay;
	}

	private void syncQuestButtonOverlayVisibilityOnClientThread()
	{
		boolean shouldRegister = enabled && config.journalShowButton();
		if (shouldRegister == buttonOverlayRegistered)
		{
			return;
		}
		if (shouldRegister)
		{
			overlayManager.add(buttonOverlay());
			buttonOverlayRegistered = true;
			syncMouseInputRegistration();
			return;
		}
		if (pressTarget == PressTarget.LAUNCHER)
		{
			consumeNextClick = false;
			resetInteraction();
		}
		if (buttonOverlay != null)
		{
			buttonOverlay.resetHitState();
			overlayManager.remove(buttonOverlay);
		}
		buttonOverlayRegistered = false;
		syncMouseInputRegistration();
	}

	private void syncMouseInputRegistration()
	{
		boolean shouldRegister = enabled && (buttonOverlayRegistered || journalOpen);
		if (shouldRegister == mouseRegistered)
		{
			return;
		}
		if (shouldRegister)
		{
			mouseManager.registerMouseListener(this);
			mouseManager.registerMouseWheelListener(this);
			mouseRegistered = true;
		}
		else
		{
			mouseManager.unregisterMouseWheelListener(this);
			mouseManager.unregisterMouseListener(this);
			mouseRegistered = false;
		}
	}

	boolean isJournalOpen()
	{
		return enabled && journalOpen;
	}

	boolean isManualActiveQuestSelection()
	{
		return questController.isManualActiveQuestSelection();
	}

	JournalSnapshot getJournalSnapshot()
	{
		return journalSnapshot;
	}

	JournalSnapshot.QuestFilter getQuestFilter()
	{
		return filterController.getFilter();
	}

	boolean isQuestStarred(String questId)
	{
		return filterController.isQuestStarred(questId);
	}

	Set<String> getStarredQuestIds()
	{
		return filterController.getStarredQuestIds();
	}

	private void reloadProfileJournalState()
	{
		filterController.reloadStarredQuests();
		viewStateController.load(true);
		questController.clearBrowsedQuest();
		filterController.resetListPreferences();
		filterController.resetToAll();
		if (!journalOpen || dataSource == null)
		{
			return;
		}
		if (journalOverlay != null)
		{
			journalOverlay.clearPersistentViewState();
		}
		refreshOpenedJournalState();
	}

	private void refreshOpenedJournalState()
	{
		dataSource.afterQuestUpdate(client.getTickCount());
		boolean stateRestored =
			viewStateController.applyRestoredViewState(dataSource);
		refreshSnapshotOnClientThread(true);
		viewStateController.applyPendingOverlayState(journalOverlay, stateRestored);
	}

	private void persistJournalViewState(boolean restoreOnNextOpen)
	{
		viewStateController.persist(
			enabled,
			journalOpen,
			journalOverlay,
			questController.getBrowsedQuestId(),
			restoreOnNextOpen);
	}

	private void toggleStarredQuest(String questId)
	{
		if (filterController.toggleStarredQuest(
			questId,
			questController.getSourceSnapshot()))
		{
			requestRefilter();
		}
	}

	boolean shouldAlwaysExpandChecklists()
	{
		return !config.journalExpandChecklistsOnDemand();
	}

	boolean shouldOpenMissingItemWikiLinks()
	{
		return config.journalOpenMissingItemWikiLinks();
	}

	Point getPointerCanvasPoint()
	{
		return new Point(pointerCanvas);
	}

	boolean isDirectlyManagingJournal()
	{
		return panelController.isMaximized() || draggingPanel || resizingPanel;
	}

	JournalPanelRenderState getJournalRenderState(Rectangle viewportBounds)
	{
		return panelController.getRenderState(viewportBounds);
	}

	boolean isJournalRenderStateCurrent(long revision)
	{
		return panelController.isRenderStateCurrent(revision);
	}

	boolean commitJournalRender(long revision, Runnable commit)
	{
		return panelController.commitRender(revision, commit);
	}

	private void refreshSnapshotOnClientThread(boolean force)
	{
		if (!enabled || !journalOpen || dataSource == null)
		{
			return;
		}
		JournalSnapshot nextSource = dataSource.getSnapshot();
		if (filterController.initializeListPreferences(nextSource.getListOptions()))
		{
			force = true;
		}
		JournalSnapshot.QuestFilter filter = filterController.getFilter();
		JournalSnapshot next = dataSource.getSnapshot(filter);
		if (!force && next == journalSnapshot)
		{
			return;
		}
		journalSnapshot = next;
		JournalSnapshot.SelectedQuest selected = journalSnapshot.getSelectedQuest();
		if (questController.publishSnapshot(nextSource, selected) && journalOverlay != null)
		{
			journalOverlay.resetSelectedQuestScroll();
			viewStateController.markDirty(enabled, journalOpen);
		}
	}

	private void requestRefilter()
	{
		long generation = mutationGeneration.get();
		clientThread.invokeLater(() ->
		{
			if (generation == mutationGeneration.get() && started && enabled && journalOpen)
			{
				refreshSnapshotOnClientThread(true);
				if (journalOverlay != null)
				{
					journalOverlay.resetQuestListScroll();
				}
				viewStateController.markDirty(enabled, journalOpen);
			}
		});
	}

	private void synchronizeFocusedQuestAutomaticallyOnClientThread()
	{
		if (journalOpen)
		{
			applyQuestMutation(questController::synchronizeFocusedQuestAutomatically);
		}
	}

	private void requestQuestMutation(Supplier<Boolean> mutation)
	{
		long generation = mutationGeneration.incrementAndGet();
		runOnClientThread(generation, true, () -> applyQuestMutation(mutation));
	}

	private void applyQuestMutation(Supplier<Boolean> mutation)
	{
		if (!enabled || !journalOpen || dataSource == null)
		{
			return;
		}
		if (mutation.get())
		{
			refreshSnapshotOnClientThread(true);
		}
	}

	void activateQuestIfReleased(String pressedId, String releasedId)
	{
		mutateQuestIfReleased(
			pressedId,
			releasedId,
			true,
			() -> questController.activateQuest(pressedId));
	}

	void clearActiveQuestIfReleased(String pressedId, String releasedId)
	{
		mutateQuestIfReleased(
			pressedId,
			releasedId,
			true,
			() -> questController.stopActiveQuest(pressedId));
	}

	void returnToActiveQuestIfReleased(String pressedId, String releasedId)
	{
		mutateQuestIfReleased(
			pressedId,
			releasedId,
			false,
			questController::returnToActiveQuest);
	}

	void selectLinkedQuestIfReleased(String pressedId, String releasedId)
	{
		mutateQuestIfReleased(
			pressedId,
			releasedId,
			false,
			() -> questController.browseQuest(pressedId));
	}

	void toggleBrowsedQuestIfReleased(String pressedId, String releasedId)
	{
		mutateQuestIfReleased(
			pressedId,
			releasedId,
			false,
			() -> questController.toggleBrowsedQuest(pressedId));
	}

	private void mutateQuestIfReleased(
		String pressedId,
		String releasedId,
		boolean playSound,
		Supplier<Boolean> mutation)
	{
		if (!matchesRelease(pressedId, releasedId))
		{
			return;
		}
		if (playSound)
		{
			searchController.playUiBoop(this::isUiSoundAllowed);
		}
		requestQuestMutation(mutation);
	}

	void openMissingItemWikiIfReleased(String pressedUrl, String releasedUrl)
	{
		if (shouldOpenMissingItemWikiLinks()
			&& matchesRelease(pressedUrl, releasedUrl))
		{
			wikiBrowser.accept(pressedUrl);
		}
	}

	private static boolean matchesRelease(String pressedValue, String releasedValue)
	{
		return pressedValue != null && pressedValue.equals(releasedValue);
	}

	void openPluginSettings()
	{
		QuestHelperPlugin owner = ownerProvider.get();
		OverlayMenuEntry entry = new OverlayMenuEntry(
			MenuAction.RUNELITE_OVERLAY_CONFIG,
			OverlayManager.OPTION_CONFIGURE,
			owner.getName());
		eventBus.post(new OverlayMenuClicked(entry, journalOverlay()));
	}

	private JournalSnapshot.QuestListOptions currentListOptions()
	{
		JournalSnapshot snapshot = journalSnapshot;
		return snapshot == null
			? JournalSnapshot.QuestListOptions.defaults()
			: snapshot.getListOptions();
	}

	private void updateQuestSearch(String query)
	{
		requestRefilterIfChanged(filterController.updateSearch(query));
	}

	private void requestRefilterIfChanged(boolean changed)
	{
		if (changed)
		{
			requestRefilter();
		}
	}

	void toggleQuestSearch()
	{
		if (!journalOpen)
		{
			return;
		}
		long generation = mutationGeneration.get();
		runOnClientThread(generation, true, () ->
		{
			if (enabled && journalOpen)
			{
				searchController.toggle(
					filterController.getFilter().getSearchText(),
					this::updateQuestSearch,
					this::isUiSoundAllowed);
			}
		});
	}

	private boolean isUiSoundAllowed()
	{
		return started && enabled;
	}

	boolean isClientMenuOpen()
	{
		return clientMenuOpen;
	}

	Rectangle getJournalContentBounds(Rectangle canvasBounds)
	{
		return panelController.getContentBounds(canvasBounds);
	}

	private JournalGeometry currentJournalGeometry(Point point)
	{
		JournalOverlay overlay = journalOverlay;
		if (overlay == null)
		{
			return null;
		}
		Rectangle publishedBounds = overlay.getPanelBounds();
		Rectangle managedBounds = new Rectangle(overlay.getBounds());
		Rectangle currentBounds = !publishedBounds.isEmpty() && publishedBounds.contains(point)
			? publishedBounds
			: !managedBounds.isEmpty() ? managedBounds : publishedBounds;
		Rectangle viewport = overlay.getViewportBounds();
		if (currentBounds.isEmpty() || viewport.isEmpty())
		{
			return null;
		}
		return JournalGeometry.create(
			currentBounds,
			viewport,
			overlay.areFiltersVisible(),
			isManualActiveQuestSelection());
	}

	void onJournalOverlayReset()
	{
		panelController.onOverlayReset(journalOverlay);
	}

	private static boolean isMaximizeControl(
		Point point,
		boolean publishedControlHit,
		JournalGeometry currentGeometry)
	{
		return publishedControlHit
			|| point != null
			&& currentGeometry != null
			&& currentGeometry.maximizeButtonBounds().contains(point);
	}

	private static boolean isSettingsControl(
		Point point,
		boolean publishedHit,
		JournalGeometry currentGeometry)
	{
		return publishedHit
			|| point != null
			&& currentGeometry != null
			&& JournalOverlay.settingsButtonBounds(
				currentGeometry.maximizeButtonBounds()).contains(point);
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent event)
	{
		updatePointer(event);
		if (consumeNextClick)
		{
			consumeNextClick = false;
			event.consume();
			return event;
		}
		if (!enabled || isMiddleButtonEvent(event) || event.isAltDown() || clientMenuOpen)
		{
			return event;
		}
		if (isSecondaryButtonEvent(event) && isLauncherPoint(event.getPoint()))
		{
			consumeNextClick = false;
			return event;
		}
		if (isInteractivePoint(event.getPoint()))
		{
			event.consume();
		}
		return event;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent event)
	{
		updatePointer(event);
		if (!enabled)
		{
			return event;
		}
		if (isMiddleButtonEvent(event))
		{
			middleMouseInteraction = true;
			return event;
		}
		consumeNextClick = false;
		titleIconClickCandidate = false;
		panelDragOccurred = false;
		if (event.isAltDown())
		{
			altOverlayMouseInteraction = true;
			resetInteraction();
			return event;
		}
		altOverlayMouseInteraction = false;
		if (clientMenuOpen)
		{
			resetInteraction();
			return event;
		}

		Point point = event.getPoint();
		JournalOverlay overlay = journalOverlay;
		JournalOverlay.FilterSelection filterSelection = journalOpen && overlay != null
			? overlay.filterSelectionAt(point) : null;
		JournalOverlay.FilterControl filterControl = journalOpen && overlay != null
			? overlay.filterControlAt(point) : null;
		String setActiveQuestId = journalOpen && overlay != null
			? overlay.setActiveQuestIdAt(point) : null;
		String clearActiveQuestId = journalOpen && overlay != null
			? overlay.clearActiveQuestIdAt(point) : null;
		String returnToActiveQuestId = journalOpen && overlay != null
			? overlay.returnToActiveQuestIdAt(point) : null;
		String starControlQuestId = journalOpen && overlay != null
			? overlay.starControlQuestIdAt(point) : null;
		boolean filterPopupSurface = journalOpen
			&& overlay != null
			&& overlay.isFilterDropdownOpen()
			&& overlay.isFilterPopupSurface(point);
		if (filterPopupSurface && filterSelection == null)
		{
			filterControl = null;
		}
		if (isPrimaryButtonEvent(event)
			&& overlay != null
			&& overlay.isFilterDropdownOpen()
			&& filterSelection == null
			&& filterControl == null)
		{
			overlay.closeFilterDropdown();
		}

		JournalGeometry currentGeometry = currentJournalGeometry(point);
		boolean journalHit = journalOpen
			&& overlay != null
			&& overlay.isRendered()
			&& overlay.isRenderedForMaximizedState(panelController.isMaximized())
			&& (overlay.contains(point)
				|| currentGeometry != null && currentGeometry.panelBounds().contains(point));
		if (journalHit)
		{
			if (!isPrimaryButtonEvent(event))
			{
				clearPressedTargets();
				if (isSecondaryButtonEvent(event) && filterControl != null)
				{
					pressedFilterControl = filterControl;
					pressTarget = PressTarget.RESET_FILTER;
				}
				event.consume();
				return event;
			}
			titleIconClickCandidate = overlay.isTitleIcon(point);
			panelDragOccurred = false;

			if (overlay.isCloseButton(point)
				|| currentGeometry != null && currentGeometry.closeButtonBounds().contains(point))
			{
				pressTarget = PressTarget.CLOSE;
			}
			else if (isSettingsControl(point, overlay.isSettingsButton(point), currentGeometry))
			{
				pressTarget = PressTarget.SETTINGS;
			}
			else if (isMaximizeControl(point, overlay.isMaximizeButton(point), currentGeometry))
			{
				pressTarget = PressTarget.MAXIMIZE;
			}
			else if (!panelController.isMaximized()
				&& (overlay.isResizeHandle(point)
					|| currentGeometry != null && currentGeometry.resizeHandleHitBounds().contains(point)))
			{
				resizingPanel = panelController.beginResize(
					journalOverlay,
					currentGeometry,
					point);
			}
			else if (!panelController.isMaximized()
				&& (overlay.isHeader(point)
					|| currentGeometry != null && currentGeometry.draggableHeaderBounds().contains(point)))
			{
				panelController.adoptManagedBounds(journalOverlay, currentGeometry);
				draggingPanel = true;
				Rectangle panelBounds = currentGeometry == null
					? overlay.getPanelBounds() : currentGeometry.panelBounds();
				panelDragOffset = new Point(point.x - panelBounds.x, point.y - panelBounds.y);
			}
			else if (filterSelection != null)
			{
				pressedFilterSelection = filterSelection;
				pressTarget = PressTarget.FILTER_OPTION;
			}
			else if (filterPopupSurface)
			{
				pressTarget = PressTarget.FILTER_POPUP_SURFACE;
			}
			else if (overlay.isSearchControl(point))
			{
				pressTarget = PressTarget.QUEST_SEARCH;
			}
			else if (starControlQuestId != null)
			{
				pressedQuestId = starControlQuestId;
				pressTarget = PressTarget.STAR_TOGGLE;
			}
			else if (overlay.isFilterVisibilityControl(point))
			{
				pressTarget = PressTarget.FILTER_VISIBILITY;
			}
			else if (filterControl != null)
			{
				pressedFilterControl = filterControl;
				pressTarget = PressTarget.FILTER_CONTROL;
			}
			else if (setActiveQuestId != null)
			{
				pressedQuestId = setActiveQuestId;
				pressTarget = PressTarget.SET_ACTIVE_QUEST;
			}
			else if (clearActiveQuestId != null)
			{
				pressedQuestId = clearActiveQuestId;
				pressTarget = PressTarget.CLEAR_ACTIVE_QUEST;
			}
			else if (returnToActiveQuestId != null)
			{
				pressedQuestId = returnToActiveQuestId;
				pressTarget = PressTarget.RETURN_TO_ACTIVE_QUEST;
			}
			else
			{
				JournalOverlay.ScrollbarInteraction scrollbar = overlay.scrollbarInteractionAt(point);
				if (scrollbar != null)
				{
					pressTarget = PressTarget.SCROLLBAR;
					scrollbarRegion = scrollbar.region();
					scrollbarPointerOffset = scrollbar.pointerOffset(point);
					overlay.dragScrollbar(scrollbarRegion, point.y, scrollbarPointerOffset);
				}
				else
				{
					pressedChecklistId = overlay.checklistToggleIdAt(point);
					if (pressedChecklistId != null)
					{
						pressTarget = PressTarget.CHECKLIST_TOGGLE;
					}
					else
					{
						pressedQuestId = overlay.linkedQuestIdAt(point);
						if (pressedQuestId != null)
						{
							pressTarget = PressTarget.LINKED_QUEST;
						}
						else
						{
							pressedWikiUrl = shouldOpenMissingItemWikiLinks()
								? overlay.missingItemWikiUrlAt(point) : null;
							if (pressedWikiUrl != null)
							{
								pressTarget = PressTarget.MISSING_ITEM_WIKI;
							}
							else
							{
								pressedQuestId = overlay.questIdAt(point);
								if (pressedQuestId != null)
								{
									pressTarget = PressTarget.QUEST;
								}
							}
						}
					}
				}
			}
			event.consume();
			return event;
		}

		if (isLauncherPoint(point))
		{
			if (isPrimaryButtonEvent(event))
			{
				pressTarget = PressTarget.LAUNCHER;
				event.consume();
			}
		}
		return event;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent event)
	{
		updatePointer(event);
		if (!enabled)
		{
			return event;
		}
		if (pressTarget == PressTarget.LAUNCHER)
		{
			Point point = event.getPoint();
			if (isPrimaryButtonEvent(event) && buttonOverlay != null && buttonOverlay.contains(point))
			{
				searchController.playUiBoop(this::isUiSoundAllowed);
				toggleJournal();
			}
			consumeNextClick = true;
			resetInteraction();
			event.consume();
			return event;
		}
		if (middleMouseInteraction || isMiddleButtonEvent(event))
		{
			if (SwingUtilities.isMiddleMouseButton(event)
				|| middleMouseInteraction && event.getButton() == MouseEvent.NOBUTTON)
			{
				middleMouseInteraction = false;
			}
			return event;
		}
		if (event.isAltDown() || altOverlayMouseInteraction)
		{
			altOverlayMouseInteraction = false;
			consumeNextClick = false;
			resetInteraction();
			return event;
		}
		if (clientMenuOpen)
		{
			boolean consume = hasActiveInteraction();
			resetInteraction();
			consumeNextClick = false;
			if (consume)
			{
				event.consume();
			}
			return event;
		}

		Point point = event.getPoint();
		boolean leftButton = isPrimaryButtonEvent(event);
		boolean rightButton = isSecondaryButtonEvent(event);
		JournalOverlay overlay = journalOverlay;
		boolean titleIconClick = leftButton
			&& titleIconClickCandidate
			&& !panelDragOccurred
			&& overlay != null
			&& overlay.isTitleIcon(point);
		boolean panelWasDragged = panelDragOccurred;
		titleIconClickCandidate = false;
		panelDragOccurred = false;
		if (draggingPanel)
		{
			draggingPanel = false;
			if (panelWasDragged)
			{
				panelController.saveBounds();
			}
			if (titleIconClick)
			{
				overlay.recordTitleIconClick();
			}
			event.consume();
			return event;
		}
		if (resizingPanel)
		{
			resizingPanel = false;
			panelController.saveBounds();
			event.consume();
			return event;
		}

		PressTarget releasedTarget = pressTarget;
		if (rightButton && releasedTarget == PressTarget.NONE && isLauncherPoint(point))
		{
			consumeNextClick = false;
			clearPressedTargets();
			return event;
		}
		if (leftButton && overlay != null)
		{
			switch (releasedTarget)
			{
			case CLOSE:
					if (overlay.isCloseButton(point))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						requestJournalOpen(false);
					}
					break;
				case MAXIMIZE:
					if (isMaximizeControl(point, overlay.isMaximizeButton(point), currentJournalGeometry(point)))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						panelController.toggleMaximized(journalOverlay);
					}
					break;
				case SETTINGS:
					if (isSettingsControl(point, overlay.isSettingsButton(point), currentJournalGeometry(point)))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						openPluginSettings();
					}
					break;
				case QUEST:
					toggleBrowsedQuestIfReleased(
						pressedQuestId,
						overlay.questIdAt(point));
					break;
				case SET_ACTIVE_QUEST:
					activateQuestIfReleased(pressedQuestId, overlay.setActiveQuestIdAt(point));
					break;
				case CLEAR_ACTIVE_QUEST:
					clearActiveQuestIfReleased(pressedQuestId, overlay.clearActiveQuestIdAt(point));
					break;
				case RETURN_TO_ACTIVE_QUEST:
					returnToActiveQuestIfReleased(
						pressedQuestId,
						overlay.returnToActiveQuestIdAt(point));
					break;
				case CHECKLIST_TOGGLE:
					if (pressedChecklistId != null
						&& pressedChecklistId.equals(overlay.checklistToggleIdAt(point)))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						overlay.toggleChecklist(pressedChecklistId);
						viewStateController.markDirty(enabled, journalOpen);
					}
					break;
				case LINKED_QUEST:
					selectLinkedQuestIfReleased(pressedQuestId, overlay.linkedQuestIdAt(point));
					break;
				case MISSING_ITEM_WIKI:
					openMissingItemWikiIfReleased(pressedWikiUrl, overlay.missingItemWikiUrlAt(point));
					break;
				case FILTER_CONTROL:
					if (pressedFilterControl != null && pressedFilterControl == overlay.filterControlAt(point))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						overlay.toggleFilterDropdown(pressedFilterControl);
					}
					break;
				case FILTER_OPTION:
					if (pressedFilterSelection != null && pressedFilterSelection.equals(overlay.filterSelectionAt(point)))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						requestRefilterIfChanged(filterController.applyFilterSelection(
							pressedFilterSelection,
							currentListOptions()));
						if (FilterController.shouldCloseFilterDropdown(
							pressedFilterSelection))
						{
							overlay.closeFilterDropdown();
						}
					}
					break;
				case QUEST_SEARCH:
					if (overlay.isSearchControl(point)) toggleQuestSearch();
					break;
				case STAR_TOGGLE:
					if (pressedQuestId != null
						&& pressedQuestId.equals(overlay.starControlQuestIdAt(point)))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						toggleStarredQuest(pressedQuestId);
					}
					break;
				case FILTER_VISIBILITY:
					if (overlay.isFilterVisibilityControl(point))
					{
						searchController.playUiBoop(this::isUiSoundAllowed);
						overlay.toggleFilterVisibility();
						viewStateController.markDirty(enabled, journalOpen);
					}
					break;
				default:
					break;
			}
			if (releasedTarget == PressTarget.SCROLLBAR)
			{
				viewStateController.markDirty(enabled, journalOpen);
			}
		}
		else if (rightButton && overlay != null && releasedTarget == PressTarget.RESET_FILTER
			&& pressedFilterControl != null
			&& pressedFilterControl == overlay.filterControlAt(point))
		{
			requestRefilterIfChanged(filterController.resetFilter(
				pressedFilterControl,
				currentListOptions()));
			overlay.closeFilterDropdown();
		}
		if (titleIconClick)
		{
			overlay.recordTitleIconClick();
		}

		boolean consumed = releasedTarget != PressTarget.NONE || isInteractivePoint(point);
		consumeNextClick = releasedTarget != PressTarget.NONE;
		clearPressedTargets();
		if (consumed)
		{
			event.consume();
		}
		return event;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent event)
	{
		updatePointer(event);
		if (draggingPanel)
		{
			panelDragOccurred = true;
		}
		if (titleIconClickCandidate)
		{
			titleIconClickCandidate = false;
			panelDragOccurred = true;
		}
		if (!enabled || middleMouseInteraction || isMiddleButtonEvent(event))
		{
			return event;
		}
		if (event.isAltDown() || altOverlayMouseInteraction)
		{
			consumeNextClick = false;
			resetInteraction();
			return event;
		}
		if (clientMenuOpen)
		{
			boolean consume = hasActiveInteraction();
			resetInteraction();
			if (consume) event.consume();
			return event;
		}
		JournalOverlay overlay = journalOverlay;
		if (pressTarget == PressTarget.SCROLLBAR && overlay != null)
		{
			overlay.dragScrollbar(scrollbarRegion, event.getPoint().y, scrollbarPointerOffset);
			event.consume();
			return event;
		}
		if (draggingPanel && overlay != null)
		{
			panelController.dragTo(
				event.getPoint(),
				panelDragOffset,
				overlay.getViewportBounds());
			event.consume();
			return event;
		}
		if (resizingPanel && overlay != null)
		{
			panelController.resizeTo(event.getPoint(), overlay.getViewportBounds());
			event.consume();
			return event;
		}
		if (pressTarget != PressTarget.NONE || isInteractivePoint(event.getPoint()))
		{
			event.consume();
		}
		return event;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent event)
	{
		updatePointer(event);
		return event;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent event)
	{
		updatePointer(event);
		return event;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent event)
	{
		updatePointer(event);
		return event;
	}

	@Override
	public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event)
	{
		pointerCanvas = event.getPoint();
		if (!enabled || clientMenuOpen)
		{
			return event;
		}
		if (journalOpen && journalOverlay != null
			&& journalOverlay.isRendered() && journalOverlay.contains(event.getPoint()))
		{
			if (journalOverlay.scrollAt(event.getPoint(), event.getWheelRotation()))
			{
				viewStateController.markDirty(enabled, journalOpen);
			}
			event.consume();
		}
		else if (buttonOverlayRegistered && buttonOverlay != null
			&& buttonOverlay.isRendered() && buttonOverlay.contains(event.getPoint()))
		{
			event.consume();
		}
		return event;
	}

	@Override
	public void keyTyped(KeyEvent event)
	{
		if (!enabled)
		{
			return;
		}
		if (searchController.isInputActiveOrPending())
		{
			return;
		}
		if (hotkeyDown)
		{
			event.consume();
		}
	}

	@Override
	public void keyPressed(KeyEvent event)
	{
		if (!enabled)
		{
			return;
		}
		if (searchController.isInputActiveOrPending())
		{
			return;
		}
		Keybind hotkey = config.journalOpenHotkey();
		boolean matchesHotkey = hotkey.matches(event);
		if (hotkeyDown && matchesHotkey)
		{
			event.consume();
			return;
		}
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE && journalOpen)
		{
			hotkeyDown = matchesHotkey;
			requestJournalOpen(false);
			event.consume();
			return;
		}
		if (!hotkeyDown && matchesHotkey)
		{
			hotkeyDown = true;
			toggleJournal();
			event.consume();
		}
	}

	@Override
	public void keyReleased(KeyEvent event)
	{
		if (!enabled)
		{
			return;
		}
		if (searchController.isInputActiveOrPending())
		{
			hotkeyDown = false;
			return;
		}
		if (hotkeyDown && config.journalOpenHotkey().matches(event))
		{
			hotkeyDown = false;
			event.consume();
		}
	}

	@Override
	public void focusLost()
	{
		hotkeyDown = false;
		altOverlayMouseInteraction = false;
		middleMouseInteraction = false;
		if (draggingPanel || resizingPanel)
		{
			panelController.saveBounds();
		}
		consumeNextClick = false;
		resetInteraction();
	}

	private boolean isInteractivePoint(Point point)
	{
		return isLauncherPoint(point) || isJournalPoint(point);
	}

	private boolean isLauncherPoint(Point point)
	{
		return buttonOverlayRegistered
			&& buttonOverlay != null
			&& buttonOverlay.isRendered()
			&& buttonOverlay.contains(point);
	}

	private boolean isJournalPoint(Point point)
	{
		return journalOpen
			&& journalOverlay != null
			&& journalOverlay.isRendered()
			&& journalOverlay.isRenderedForMaximizedState(panelController.isMaximized())
			&& journalOverlay.contains(point);
	}

	private void updatePointer(MouseEvent event)
	{
		pointerCanvas = event.getPoint();
	}

	private Menu sanitizeMenuEntries()
	{
		Menu menu = client.getMenu();
		if (menu == null)
		{
			return null;
		}
		MenuEntry[] entries = menu.getMenuEntries();
		MenuEntry cancel = null;
		if (entries != null)
		{
			for (MenuEntry entry : entries)
			{
				if (entry != null && entry.getType() == MenuAction.CANCEL)
				{
					cancel = entry;
					break;
				}
			}
		}
		menu.setMenuEntries(cancel == null ? new MenuEntry[0] : new MenuEntry[]{cancel});
		if (cancel == null)
		{
			menu.createMenuEntry(-1)
				.setOption("Cancel")
				.setTarget("")
				.setType(MenuAction.CANCEL);
		}
		return menu;
	}

	private void hideQuestJournalButton()
	{
		configManager.setConfiguration(
			QuestHelperConfig.QUEST_HELPER_GROUP,
			SHOW_QUEST_BUTTON_KEY,
			false);
	}

	static boolean isPrimaryButtonEvent(MouseEvent event)
	{
		return SwingUtilities.isLeftMouseButton(event)
			|| (event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0;
	}

	static boolean isSecondaryButtonEvent(MouseEvent event)
	{
		return SwingUtilities.isRightMouseButton(event)
			|| (event.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0;
	}

	static boolean isMiddleButtonEvent(MouseEvent event)
	{
		return SwingUtilities.isMiddleMouseButton(event)
			|| (event.getModifiersEx() & InputEvent.BUTTON2_DOWN_MASK) != 0;
	}

	private void clearPressedTargets()
	{
		pressTarget = PressTarget.NONE;
		pressedQuestId = null;
		pressedChecklistId = null;
		pressedWikiUrl = null;
		pressedFilterControl = null;
		pressedFilterSelection = null;
	}

	private void resetInteraction()
	{
		draggingPanel = false;
		resizingPanel = false;
		titleIconClickCandidate = false;
		panelDragOccurred = false;
		clearPressedTargets();
		scrollbarRegion = null;
		scrollbarPointerOffset = 0;
	}

	private boolean hasActiveInteraction()
	{
		return draggingPanel || resizingPanel || pressTarget != PressTarget.NONE;
	}

	static final class JournalPanelRenderState
	{
		private final Rectangle bounds;
		private final boolean maximized;
		private final long revision;

		JournalPanelRenderState(Rectangle bounds, boolean maximized, long revision)
		{
			this.bounds = new Rectangle(bounds);
			this.maximized = maximized;
			this.revision = revision;
		}

		Rectangle bounds()
		{
			return new Rectangle(bounds);
		}

		boolean maximized()
		{
			return maximized;
		}

		long revision()
		{
			return revision;
		}
	}

	private enum PressTarget
	{
		NONE,
		LAUNCHER,
		CLOSE,
		SETTINGS,
		MAXIMIZE,
		QUEST_SEARCH,
		STAR_TOGGLE,
		FILTER_VISIBILITY,
		FILTER_CONTROL,
		FILTER_OPTION,
		FILTER_POPUP_SURFACE,
		RESET_FILTER,
		QUEST,
		SET_ACTIVE_QUEST,
		CLEAR_ACTIVE_QUEST,
		RETURN_TO_ACTIVE_QUEST,
		CHECKLIST_TOGGLE,
		LINKED_QUEST,
		MISSING_ITEM_WIKI,
		SCROLLBAR
	}
}
