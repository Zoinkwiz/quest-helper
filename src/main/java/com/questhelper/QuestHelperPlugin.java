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
import com.google.inject.Provides;
import com.questhelper.bank.banktab.BankTabItems;
import com.questhelper.managers.*;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.runeliteobjects.Cheerer;
import com.questhelper.runeliteobjects.GlobalFakeObjects;
import com.questhelper.statemanagement.PlayerStateManager;
import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import com.google.inject.Module;
import com.questhelper.util.worldmap.WorldMapAreaManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.SwingUtilities;
import com.questhelper.tools.Icon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.api.VarPlayer;
import net.runelite.api.WorldType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.VarbitChanged;
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

	@Inject
	private QuestOverlayManager questOverlayManager;

	@Inject
	private QuestBankManager questBankManager;

	@Inject
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

	@Getter
	@Inject
	ActiveRequirementsManager activeRequirementsManager;

	@Inject
	public SkillIconManager skillIconManager;

	private QuestHelperPanel panel;

	private NavigationButton navButton;

	boolean profileChanged;


	// TODO: Use this for item checks
	@Getter
	private int lastTickInventoryUpdated = -1;

	@Getter
	private int lastTickBankUpdated = -1;

	private final Collection<String> configEvents = Arrays.asList("orderListBy", "filterListBy", "questDifficulty", "showCompletedQuests");
	private final Collection<String> configItemEvents = Arrays.asList("highlightNeededQuestItems", "highlightNeededMiniquestItems", "highlightNeededAchievementDiaryItems");

	@Provides
	QuestHelperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuestHelperConfig.class);
	}

	@Override
	protected void startUp() throws IOException
	{
		questBankManager.startUp(injector, eventBus);
		eventBus.register(worldMapAreaManager);

		injector.injectMembers(playerStateManager);
		eventBus.register(playerStateManager);
		playerStateManager.startUp();

		eventBus.register(runeliteObjectManager);
		runeliteObjectManager.startUp();

		scanAndInstantiate();

		questOverlayManager.startUp();

		final BufferedImage icon = Icon.QUEST_ICON.getImage();

		panel = new QuestHelperPanel(this, questManager, configManager);
		questManager.startUp(panel);
		activeRequirementsManager.setPanel(panel);
		activeRequirementsManager.setQuestBankManager(questBankManager);
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
		runeliteObjectManager.shutDown();

		eventBus.unregister(playerStateManager);
		eventBus.unregister(runeliteObjectManager);
		eventBus.unregister(worldMapAreaManager);
		questOverlayManager.shutDown();
		playerStateManager.shutDown();

		clientToolbar.removeNavigation(navButton);
		questManager.shutDown();
		activeRequirementsManager.shutDown();
		questBankManager.shutDown(eventBus);

		GlobalFakeObjects.setInitialized(false);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		questBankManager.loadInitialStateFromConfig(client);
		questManager.updateQuestState();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.BANK))
		{
			lastTickBankUpdated = client.getTickCount();
			questBankManager.updateLocalBank(event.getItemContainer());
		}

		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			lastTickInventoryUpdated = client.getTickCount();
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
		}

		if (state == GameState.LOGGED_IN && profileChanged)
		{
			profileChanged = false;
			questManager.shutDownQuest(true);
			GlobalFakeObjects.createNpcs(client, runeliteObjectManager, configManager, config);
			newVersionManager.updateChatWithNotificationIfNewVersion();
			questBankManager.setUnknownInitialState();
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
			&& event.getVarpId() == VarPlayer.IN_RAID_PARTY
			&& event.getValue() == 0
			&& client.getGameState() == GameState.LOGGED_IN)
		{
			questBankManager.updateBankForQuestSpeedrunningWorld();
		}

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
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		if (developerMode && commandExecuted.getCommand().equals("questhelperdebug"))
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
			ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
			StringBuilder inv = new StringBuilder();
			if (inventory != null)
			{
				for (Item item : inventory.getItems())
				{
					inv.append(item.getId()).append("\n");
				}
			}
			System.out.println(inv);
		}
	}

	@Subscribe(priority = 100)
	private void onClientShutdown(ClientShutdown e)
	{
		questBankManager.saveBankToConfig();
	}

	public void refreshBank()
	{
		clientThread.invokeLater(() -> questBankManager.refreshBankTab());
	}

	public List<BankTabItems> getPluginBankTagItemsForSections()
	{
		return questBankManager.getBankTagService().getPluginBankTagItemsForSections(false);
	}

	public QuestHelper getSelectedQuest()
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
}
