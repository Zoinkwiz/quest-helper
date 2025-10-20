/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.questhelper.bank.banktab.BankTabItems;
import com.questhelper.bank.banktab.PotionStorage;
import com.questhelper.helpers.guides.EarlyGameGuide;
import com.questhelper.managers.*;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.runeliteobjects.Cheerer;
import com.questhelper.runeliteobjects.GlobalFakeObjects;
import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import com.questhelper.statemanagement.PlayerStateManager;
import com.questhelper.tools.Icon;
import com.questhelper.util.worldmap.WorldMapAreaManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.Text;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@PluginDescriptor(
	name = "Quest Helper",
	description = "Helps you with questing",
	tags = { "quest", "helper", "overlay" }
)
@Slf4j
public class QuestHelperPlugin extends Plugin
{
	@Getter
	@Inject
	@Named("developerMode")
	private boolean developerMode;

	@Getter
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Getter
	@Inject
	private ClientThread clientThread;

	@Inject
	private net.runelite.client.input.KeyManager keyManager;

	@Inject
	private EventBus eventBus;

	@Getter
	@Inject
	private BankSearch bankSearch;

	@Getter
	@Inject
	private ItemManager itemManager;

	@Getter
	@Inject
	ChatMessageManager chatMessageManager;

	@Getter
	@Inject
	private QuestHelperConfig config;

	@Getter
	@Inject
	RuneliteObjectManager runeliteObjectManager;

	@Getter
	@Inject
	private QuestOverlayManager questOverlayManager;

	@Inject
	private QuestBankManager questBankManager;

	@Inject
	@Getter
	private QuestManager questManager;

	@Inject
	private WorldMapAreaManager worldMapAreaManager;

	@Inject
	private QuestMenuHandler questMenuHandler;

	@Inject
	private NewVersionManager newVersionManager;

	@Getter
	@Inject
	private ColorPickerManager colorPickerManager;

	@Getter
	@Inject
	ConfigManager configManager;

	@Getter
	@Inject
	PlayerStateManager playerStateManager;

	@Inject
	EarlyGameGuide earlyGameGuide;

	@Inject
	PotionStorage potionStorage;

	@Inject
	public SkillIconManager skillIconManager;

	private QuestHelperPanel panel;

	private NavigationButton navButton;

	boolean profileChanged;

	private final Collection<String> configEvents = Arrays.asList("orderListBy", "filterListBy", "questDifficulty", "showCompletedQuests");
	private final Collection<String> configItemEvents = Arrays.asList("highlightNeededQuestItems", "highlightNeededMiniquestItems", "highlightNeededAchievementDiaryItems");

	private final net.runelite.client.util.HotkeyListener openGuideHotkey = new net.runelite.client.util.HotkeyListener(() -> config.openGuideHotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			clientThread.invokeLater(() -> openEarlyGameGuide());
		}
	};

	public void openEarlyGameGuide()
	{
		if (client == null) return;
		earlyGameGuide.show(client);
	}

	private boolean shouldShowOnboarding()
	{
		// Basic heuristic: show if LOGGED_IN and not on Tutorial Island map regions (fallback)
		// More precise TI detection can be added if a varbit is available in this project later
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return false;
		}
		return true;
	}

	@Provides
	QuestHelperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuestHelperConfig.class);
	}

	@Override
	protected void startUp() throws IOException
	{
		keyManager.registerKeyListener(openGuideHotkey);
		earlyGameGuide.setPlugin(this);
		clientThread.invokeLater(() -> {
			if (!Boolean.parseBoolean(String.valueOf(configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "earlyGuideSeen")))
				&& config.showOnboardingPrompt()
				&& shouldShowOnboarding())
			{
				openEarlyGameGuide();
				configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, "earlyGuideSeen", true);
			}
		});
		questBankManager.startUp(injector, eventBus);
		QuestContainerManager.getBankData().setSpecialMethodToObtainItems(() -> questBankManager.getBankItems().toArray(new Item[0]));
		QuestContainerManager.getGroupStorageData().setSpecialMethodToObtainItems(() -> questBankManager.getGroupBankItems().toArray(new Item[0]));
		eventBus.register(worldMapAreaManager);

		injector.injectMembers(playerStateManager);
		eventBus.register(playerStateManager);
		playerStateManager.startUp();


		eventBus.register(runeliteObjectManager);
		runeliteObjectManager.startUp();

		scanAndInstantiate();

		questOverlayManager.startUp();

		if (developerMode)
		{
			if (config.devShowOverlayOnLaunch())
			{
				questOverlayManager.addDebugOverlay();
			}
		}

		final BufferedImage icon = Icon.QUEST_ICON.getImage();

		panel = new QuestHelperPanel(this, questManager, configManager);
		questManager.startUp(panel);
		navButton = NavigationButton.builder()
			.tooltip("Quest Helper")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		clientThread.invokeAtTickEnd(() -> {
			if (client.getGameState() == GameState.LOGGED_IN)
			{
				questManager.setupRequirements();
				questManager.setupOnLogin();
				GlobalFakeObjects.createNpcs(client, runeliteObjectManager, configManager, config);
			}
		});
	}

	@Override
	protected void shutDown()
	{
		try
		{
			keyManager.unregisterKeyListener(openGuideHotkey);
		}
		catch (Exception ignored)
		{
		}
		clientThread.invokeLater(earlyGameGuide::destroy);
		runeliteObjectManager.shutDown();

		eventBus.unregister(playerStateManager);
		eventBus.unregister(runeliteObjectManager);
		eventBus.unregister(worldMapAreaManager);
		if (developerMode)
		{
			// We don't check if it was added, since removing an unadded overlay is a no-op
			questOverlayManager.removeDebugOverlay();
		}
		questOverlayManager.shutDown();
		playerStateManager.shutDown();

		clientToolbar.removeNavigation(navButton);
		questManager.shutDown();
		questBankManager.shutDown(eventBus);

		GlobalFakeObjects.setInitialized(false);
	}

	// Run our base game tick checks later than other Quest Helper checks
	// This allows steps/requirements/conditions to run their checks first before we try to update the side panel
	@Subscribe(priority=-1.0f)
	public void onGameTick(GameTick event)
	{
		questBankManager.loadInitialStateFromConfig(client);
		questManager.updateQuestState();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		Item[] items = event.getItemContainer().getItems();
		if (event.getContainerId() == InventoryID.BANK)
		{
			ItemAndLastUpdated bankData = QuestContainerManager.getBankData();
			bankData.update(client.getTickCount(), items);
			questBankManager.updateLocalBank(event.getItemContainer());
		}

		if (event.getContainerId() == InventoryID.INV)
		{
			QuestContainerManager.updateInventory(client);

			// Check if it matches last known group inventory state to know if we should update group storage
			boolean bankChanged = questBankManager.updateGroupBankOnInventoryChange(items);
			if (bankChanged)
			{
				ItemAndLastUpdated groupBankData = QuestContainerManager.getGroupStorageData();
				groupBankData.update(client.getTickCount(), items);
			}
		}

		if (event.getContainerId() == InventoryID.WORN)
		{
			ItemAndLastUpdated equippedData = QuestContainerManager.getEquippedData();
			equippedData.update(client.getTickCount(), items);
		}
		if (event.getContainerId() == InventoryID.INV_GROUP_TEMP)
		{
			ItemAndLastUpdated groupBankData = QuestContainerManager.getGroupStorageData();
			groupBankData.update(client.getTickCount(), items);
			questBankManager.updateLocalGroupBank(client, event.getItemContainer());
		}
		if (event.getContainerId() == InventoryID.INV_PLAYER_TEMP)
		{
			questBankManager.updateLocalGroupInventory(items);
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		final GameState state = event.getGameState();

		if (state == GameState.LOGIN_SCREEN)
		{
			questBankManager.saveBankToConfig();
			SwingUtilities.invokeLater(() -> panel.refresh(Collections.emptyList(), true, new HashMap<>()));
			questBankManager.emptyState();
			questManager.shutDownQuest(true);
			profileChanged = true;
			earlyGameGuide.destroy();
		}
		else if (state == GameState.HOPPING)
		{
			earlyGameGuide.destroy();
		}

		if (state == GameState.LOGGED_IN && profileChanged)
		{
			profileChanged = false;
			questManager.shutDownQuest(true);
			GlobalFakeObjects.createNpcs(client, runeliteObjectManager, configManager, config);
			newVersionManager.updateChatWithNotificationIfNewVersion();
			questBankManager.setUnknownInitialState();
			potionStorage.updateCachedPotions = true;
			clientThread.invokeAtTickEnd(() -> {
				questManager.setupRequirements();
				questManager.setupOnLogin();
			});
		}
	}

	@Subscribe
	private void onRuneScapeProfileChanged(RuneScapeProfileChanged ev)
	{
		profileChanged = true;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		if (client.getWorldType().contains(WorldType.QUEST_SPEEDRUNNING)
			&& event.getVarpId() == VarPlayerID.RAIDS_PARTY_GROUPHOLDER
			&& event.getValue() == 0
			&& client.getGameState() == GameState.LOGGED_IN)
		{
			questBankManager.updateBankForQuestSpeedrunningWorld();
		}

		QuestContainerManager.updateRunePouch(client, event.getVarbitId());
		questManager.handleVarbitChanged();
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		questManager.handleConfigChanged();

		if (event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			clientThread.invokeLater(questManager::updateQuestList);
			SwingUtilities.invokeLater(panel::refreshSkillFiltering);
		}

		if (!event.getGroup().equals(QuestHelperConfig.QUEST_HELPER_GROUP))
		{
			return;
		}

		if (event.getKey().equals("showRuneliteObjects") && client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(() -> {
				if (config.showRuneliteObjects())
				{
					GlobalFakeObjects.createNpcs(client, runeliteObjectManager, configManager, config);
				}
				else
				{
					GlobalFakeObjects.disableNpcs(runeliteObjectManager);
				}
			});
		}

		if (configEvents.contains(event.getKey()) || event.getKey().contains("skillfilter"))
		{
			clientThread.invokeLater(questManager::updateQuestList);
		}

		if (configItemEvents.contains(event.getKey()))
		{
			questManager.updateAllItemsHelper();
		}

		if ("highlightItemsBackground".equals(event.getKey()))
		{
			questManager.updateAllItemsBackgroundHelper(event.getNewValue());
		}

		if ("useShortestPath".equals(event.getKey()))
		{
			if ("true".equals(event.getNewValue()))
			{
				questManager.activateShortestPath();
			}
			else
			{
				questManager.disableShortestPath();
			}
		}

		if (event.getKey().contains(QuestHelperConfig.QUEST_HELPER_SIDEBAR_ORDER_KEY_START))
		{
			if (questManager.getSelectedQuest() != null)
			{
				questManager.getSelectedQuest().setSidebarOrder(loadSidebarOrder(questManager.getSelectedQuest()));
			}
		}

		if (developerMode && "devShowOverlayOnLaunch".equals(event.getKey()))
		{
			if (config.devShowOverlayOnLaunch())
			{
				questOverlayManager.addDebugOverlay();
			}
			else
			{
				questOverlayManager.removeDebugOverlay();
			}
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		if (commandExecuted.getCommand().equals("qhguide"))
		{
			clientThread.invokeLater(this::openEarlyGameGuide);
		}
		else if (developerMode && commandExecuted.getCommand().equals("questhelperdebug"))
		{
			if (commandExecuted.getArguments().length == 0 ||
				(Arrays.stream(commandExecuted.getArguments()).toArray()[0]).equals("disable"))
			{
				questOverlayManager.removeDebugOverlay();
			}
			else if ((Arrays.stream(commandExecuted.getArguments()).toArray()[0]).equals("enable"))
			{
				questOverlayManager.addDebugOverlay();
			}
		}
		else if (developerMode && commandExecuted.getCommand().equals("reset-cooks-helper"))
		{
			String step = (String) (Arrays.stream(commandExecuted.getArguments()).toArray()[0]);
			new RuneliteConfigSetter(configManager, QuestHelperQuest.COOKS_HELPER.getPlayerQuests().getConfigValue(), step).setConfigValue();
		}
		else if (developerMode && commandExecuted.getCommand().equals("qh-inv"))
		{
			ItemContainer inventory = client.getItemContainer(InventoryID.INV);
			StringBuilder inv = new StringBuilder();
			if (inventory != null)
			{
				for (Item item : inventory.getItems())
				{
					inv.append(item.getId()).append("\n");
				}
			}
			log.debug(String.valueOf(inv));
		}
	}

	@Subscribe(priority = 100)
	private void onClientShutdown(ClientShutdown e)
	{
		questBankManager.saveBankToConfig();
	}

	private MainBufferProvider lastBufferProvider;

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		var currentBufferProvider = (MainBufferProvider) client.getBufferProvider();
		if (lastBufferProvider != currentBufferProvider)
		{
			// Only rebuild if the dialog is currently open
			if (earlyGameGuide.isOpen)
			{
				// Buffer provider changed means container type changed or UI scaled, so we need full rebuild
				// Possibly should for rescale detect that and if is rescale only move it don't rebuild it
				earlyGameGuide.destroy();
				earlyGameGuide.setup(client);
			}
		}
		lastBufferProvider = currentBufferProvider;
	}

	public void refreshBank()
	{
		clientThread.invokeLater(() -> questBankManager.refreshBankTab());
	}

	public List<BankTabItems> getPluginBankTagItemsForSections()
	{
		return questBankManager.getBankTagService().getPluginBankTagItemsForSections(false);
	}

	public @Nullable QuestHelper getSelectedQuest()
	{
		return questManager.getSelectedQuest();
	}

	public Map<String, QuestHelper> getBackgroundHelpers()
	{
		return questManager.backgroundHelpers;
	}

	public Map<QuestHelperQuest, List<ItemRequirement>> getItemRequirements()
	{
		return questManager.itemRequirements;
	}

	public Map<QuestHelperQuest, List<ItemRequirement>> getItemRecommended()
	{
		return questManager.itemRecommended;
	}

	public List<Integer> itemsToTag()
	{
		return questBankManager.getBankTagService().itemsToTag();
	}

	private void addCheerer()
	{
		Cheerer.activateCheerer(client, chatMessageManager);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();

		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();
		String option = event.getOption();

		String target = Text.removeTags(event.getTarget());

		questMenuHandler.setupQuestMenuOptions(menuEntries, widgetIndex, widgetID, target, option);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (config.showFan() && chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (chatMessage.getMessage().contains("Congratulations! Quest complete!") ||
				chatMessage.getMessage().contains("you've completed a quest"))
			{
				addCheerer();
			}
		}
		if (config.autoStartQuests() && chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (questManager.getSelectedQuest() == null && chatMessage.getMessage().contains("You've started a new quest"))
			{
				String questName = chatMessage.getMessage().substring(chatMessage.getMessage().indexOf(">") + 1);
				questName = questName.substring(0, questName.indexOf("<"));
				questMenuHandler.startUpQuest(questName);
			}
		}
	}

	public void displayPanel()
	{
		SwingUtilities.invokeLater(() -> {
			clientToolbar.openPanel(navButton);
		});
	}

	private void scanAndInstantiate()
	{
		for (QuestHelperQuest qhq : QuestHelperQuest.values())
		{
			instantiate(qhq);
		}
	}

	private void instantiate(QuestHelperQuest quest)
	{
		QuestHelper questHelper = quest.getQuestHelper();

		Module questModule = (Binder binder) ->
		{
			binder.bind(QuestHelper.class).toInstance(questHelper);
			binder.install(questHelper);
		};
		Injector questInjector = RuneLite.getInjector().createChildInjector(questModule);
		injector.injectMembers(questHelper);
		questHelper.setInjector(questInjector);
		questHelper.setQuest(quest);
		questHelper.setConfig(config);
		questHelper.setQuestHelperPlugin(this);

		log.debug("Loaded quest helper {}", quest.name());
	}

    public void saveSidebarOrder(QuestHelper currentQuest, List<Integer> newOrderIds)
    {
		if (currentQuest == null || currentQuest.getQuest() == null) return;
		configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, QuestHelperConfig.QUEST_HELPER_SIDEBAR_ORDER_KEY_START + currentQuest.getQuest().name(), newOrderIds);
    }

	public List<Integer> loadSidebarOrder(QuestHelper currentQuest)
	{
		if (currentQuest == null || currentQuest.getQuest() == null) return null;
		String order = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP,
				QuestHelperConfig.QUEST_HELPER_SIDEBAR_ORDER_KEY_START + currentQuest.getQuest().name());
		if (order == null) return null;
		order = order.trim();
		order = order.substring(1, order.length() - 1);
		return Arrays.stream(order.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}
}
