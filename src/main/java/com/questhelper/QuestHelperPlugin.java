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

import com.google.common.primitives.Ints;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.questhelper.bank.banktab.BankTabItems;
import com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan;
import com.questhelper.managers.QuestBankManager;
import com.questhelper.managers.QuestOverlayManager;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.runeliteobjects.Cheerer;
import com.questhelper.runeliteobjects.GlobalFakeObjects;
import com.questhelper.statemanagement.GameStateManager;
import com.questhelper.steps.QuestStep;
import com.questhelper.runeliteobjects.RuneliteConfigSetter;
import com.questhelper.runeliteobjects.extendedruneliteobjects.RuneliteObjectManager;
import com.google.inject.Module;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.SwingUtilities;
import com.questhelper.tools.Icon;
import com.questhelper.tools.QuestWidgets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
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
	private static final int[] QUESTLIST_WIDGET_IDS = new int[]
		{
			QuestWidgets.QUEST_CONTAINER.getId()
		};

	private static final String[] RFD_NAMES = new String[]
		{
			QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_SIR_AMIK_VARZE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_DWARF.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_EVIL_DAVE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_PIRATE_PETE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE.getName(),
			QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()
		};

	private static final int[] ACHIEVEMENTLIST_WIDGET_IDS = new int[]
		{
			WidgetInfo.ACHIEVEMENT_DIARY_CONTAINER.getId()
		};

	private static final String[] achievementTiers = new String[]
		{
			"Elite",
			"Hard",
			"Medium",
			"Easy"
		};

	private static final String MENUOP_STARTHELPER = "Start Quest Helper";
	private static final String MENUOP_STOPHELPER = "Stop Quest Helper";
	private static final String MENUOP_QUESTHELPER = "Quest Helper";

	private static final String MENUOP_STARTGENERICHELPER = "Start Helper";
	private static final String MENUOP_STOPGENERICHELPER = "Stop Helper";
	private static final String MENUOP_GENERICHELPER = "Helper";

	private static final Zone PHOENIX_START_ZONE = new Zone(new WorldPoint(3204, 3488, 0), new WorldPoint(3221, 3501, 0));

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
	private QuestHelper selectedQuest = null;

	@Getter
	@Inject
	@Named("developerMode")
	private boolean developerMode;

	@Setter
	private QuestHelper sidebarSelectedQuest = null;

	private QuestStep lastStep = null;

	@Getter
	@Inject
	RuneliteObjectManager runeliteObjectManager;

	@Getter
	@Inject
	private ColorPickerManager colorPickerManager;

	@Getter
	@Inject
	ConfigManager configManager;

	@Getter
	@Inject
	GameStateManager gameStateManager;

	private QuestHelperPanel panel;

	private NavigationButton navButton;

	private boolean loadQuestList;

	private boolean displayNameKnown;

	public Map<String, QuestHelper> backgroundHelpers = new HashMap<>();
	public SortedMap<QuestHelperQuest, List<ItemRequirement>> itemRequirements = new TreeMap<>();
	public SortedMap<QuestHelperQuest, List<ItemRequirement>> itemRecommended = new TreeMap<>();


	// TODO: Use this for item checks
	@Getter
	private int lastTickInventoryUpdated = -1;

	@Getter
	private int lastTickBankUpdated = -1;

	@Inject
	private QuestOverlayManager questOverlayManager;

	@Inject
	private QuestBankManager questBankManager;

	@Provides
	QuestHelperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuestHelperConfig.class);
	}

	@Override
	protected void startUp() throws IOException
	{
		questBankManager.startUp(injector, eventBus);

		injector.injectMembers(gameStateManager);
		eventBus.register(gameStateManager);
		gameStateManager.startUp();

		eventBus.register(runeliteObjectManager);
		runeliteObjectManager.startUp();

		scanAndInstantiate();

		questOverlayManager.startUp();

		final BufferedImage icon = Icon.QUEST_ICON.getImage();

		panel = new QuestHelperPanel(this);
		navButton = NavigationButton.builder()
			.tooltip("Quest Helper")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		clientThread.invokeLater(() -> {
			for (QuestHelperQuest questHelperQuest : QuestHelperQuest.values())
			{
				questHelperQuest.getQuestHelper().setupRequirements();
			}

			if (client.getGameState() == GameState.LOGGED_IN)
			{
				// Update with new items
				QuestHelperQuest.CHECK_ITEMS.getQuestHelper().init();
				getAllItemRequirements();
				loadQuestList = true;
				GlobalFakeObjects.createNpcs(client, runeliteObjectManager, configManager, config);
			}
		});
	}

	@Override
	protected void shutDown()
	{
		runeliteObjectManager.shutDown();

		eventBus.unregister(gameStateManager);
		eventBus.unregister(runeliteObjectManager);
		questOverlayManager.shutDown();

		clientToolbar.removeNavigation(navButton);
		shutDownQuest(false);
		questBankManager.shutDown(eventBus);

		GlobalFakeObjects.setInitialized(false);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!displayNameKnown)
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer != null && localPlayer.getName() != null)
			{
				displayNameKnown = true;
				questBankManager.loadState();
			}
		}
		if (sidebarSelectedQuest != null)
		{
			startUpQuest(sidebarSelectedQuest);
			sidebarSelectedQuest = null;
		}
		else if (selectedQuest != null)
		{
			if (selectedQuest.getCurrentStep() != null)
			{
				panel.updateSteps();
				QuestStep currentStep = selectedQuest.getCurrentStep().getSidePanelStep();
				if (currentStep != null && currentStep != lastStep)
				{
					lastStep = currentStep;
					panel.updateHighlight(client, currentStep);
				}
				if (panel.questActive)
				{
					clientThread.invokeLater(() -> panel.updateItemRequirements(client, questBankManager.getBankItems()));
				}
				panel.updateLocks();
			}
		}
		if (loadQuestList)
		{
			loadQuestList = false;
			updateQuestList();
			getAllItemRequirements();
		}
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
			if (selectedQuest != null && selectedQuest.getCurrentStep() != null)
			{
				shutDownQuest(true);
			}
		}

		if (state == GameState.LOGGED_IN)
		{
			GlobalFakeObjects.createNpcs(client, runeliteObjectManager, configManager, config);
			loadQuestList = true;
			displayNameKnown = false;
			clientThread.invokeLater(() -> {
				QuestHelperQuest.CHECK_ITEMS.getQuestHelper().init();
				getAllItemRequirements();
			});
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		if (selectedQuest == null)
		{
			return;
		}

		if (selectedQuest.updateQuest() && selectedQuest.getCurrentStep() == null)
		{
			shutDownQuest(true);
		}

		clientThread.invokeLater(() -> {
			if ((selectedQuest != null) && selectedQuest.isCompleted())
			{
				shutDownQuest(true);
			}
		});
	}

	private final Collection<String> configEvents = Arrays.asList("orderListBy", "filterListBy", "questDifficulty", "showCompletedQuests", "");
	private final Collection<String> configItemEvents = Arrays.asList("highlightNeededQuestItems", "highlightNeededMiniquestItems", "highlightNeededAchievementDiaryItems");

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		// Catches Player Made Quests
		clientThread.invokeLater(() -> {
			if ((selectedQuest != null) && selectedQuest.isCompleted())
			{
				shutDownQuest(true);
			}
		});

		if (event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			clientThread.invokeLater(this::updateQuestList);
		}

		if (!event.getGroup().equals(QuestHelperConfig.QUEST_HELPER_GROUP))
		{
			return;
		}

		if (event.getKey().equals("showRuneliteObjects"))
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

		if (configEvents.contains(event.getKey()))
		{
			clientThread.invokeLater(this::updateQuestList);
		}

		if (configItemEvents.contains(event.getKey()))
		{
			getAllItemRequirements();
			if (selectedQuest != null && selectedQuest.getQuest() == QuestHelperQuest.CHECK_ITEMS)
			{
				clientThread.invokeLater(() -> startUpQuest(QuestHelperQuest.CHECK_ITEMS.getQuestHelper(), false));
			}
		}

		if ("highlightItemsBackground".equals(event.getKey()))
		{
			// If shouldn't highlight, shut down highlights
			if (Objects.equals(event.getNewValue(), "false"))
			{
				shutDownBackgroundQuest(backgroundHelpers.get(QuestHelperQuest.CHECK_ITEMS.getName()));
			}
			else
			{
				startUpBackgroundQuest(QuestHelperQuest.CHECK_ITEMS.getName());
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

	public List<BankTabItems> getPluginBankTagItemsForSections(boolean onlyGetMissingItems)
	{
		return questBankManager.getBankTagService().getPluginBankTagItemsForSections(false);
	}

	public void updateQuestList()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			List<QuestHelper> filteredQuests = QuestHelperQuest.getQuestHelpers()
				.stream()
				.filter(config.filterListBy())
				.filter(config.difficulty())
				.filter(QuestDetails::showCompletedQuests)
				.sorted(config.orderListBy())
				.collect(Collectors.toList());
			Map<QuestHelperQuest, QuestState> completedQuests = QuestHelperQuest.getQuestHelpers()
				.stream()
				.collect(Collectors.toMap(QuestHelper::getQuest, q -> q.getState(client)));
			SwingUtilities.invokeLater(() -> panel.refresh(filteredQuests, false, completedQuests, config.orderListBy().getSections()));
		}
	}

	public List<Integer> itemsToTag()
	{
		return questBankManager.getBankTagService().itemsToTag();
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() != MenuAction.RUNELITE)
		{
			return;
		}

		switch (event.getMenuOption())
		{
			case MENUOP_STARTHELPER:
			case MENUOP_STARTGENERICHELPER:
				event.consume();
				String quest = Text.removeTags(event.getMenuTarget());
				startUpQuest(QuestHelperQuest.getByName(quest));
				break;
			case MENUOP_STOPHELPER:
			case MENUOP_STOPGENERICHELPER:
				event.consume();
				shutDownQuest(true);
				break;
		}
	}

	private MenuEntry[] addRightClickMenuOptions(String helperName, String entryName, String target,
												 MenuEntry[] menuEntries,
												 int widgetIndex, int widgetID)
	{
		QuestHelper questHelper = QuestHelperQuest.getByName(helperName);
		if (questHelper != null && !questHelper.isCompleted())
		{
			if (selectedQuest != null && selectedQuest.getQuest().getName().equals(helperName))
			{
				return addNewEntry(menuEntries, "Stop " + entryName, target, widgetIndex, widgetID);
			}
			else
			{
				return addNewEntry(menuEntries, "Start " + entryName, target, widgetIndex, widgetID);
			}
		}

		return menuEntries;
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
		MenuEntry[] menuEntries = client.getMenuEntries();

		String target = Text.removeTags(event.getTarget());

		if (Ints.contains(ACHIEVEMENTLIST_WIDGET_IDS, widgetID) && event.getOption().contains("Open "))
		{
			String diary = event.getOption().replace("Journal", "");
			diary = diary.replace("Open ", "");
			diary = Text.removeTags(diary);
			for (String achievementTier : achievementTiers)
			{
				menuEntries = addRightClickMenuOptions(diary + achievementTier + " Diary", MENUOP_GENERICHELPER,
					"<col=ff9040>" + diary + achievementTier + " Diary</col>", menuEntries, widgetIndex, widgetID);
			}
		}

		if (Ints.contains(QUESTLIST_WIDGET_IDS, widgetID) && "Read journal:".equals(event.getOption()))
		{
			if (target.equals("Shield of Arrav"))
			{
				if (selectedQuest != null &&
					(selectedQuest.getQuest().getId() == QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getId()))
				{
					addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
				}
				else
				{
					String phoenixName = QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName();
					String blackArmName = QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName();
					QuestHelper questHelperPhoenix = QuestHelperQuest.getByName(phoenixName);
					QuestHelper questHelperBlackArm = QuestHelperQuest.getByName(blackArmName);
					if (questHelperPhoenix != null && !questHelperPhoenix.isCompleted())
					{
						menuEntries = addRightClickMenuOptions(phoenixName, MENUOP_QUESTHELPER,
							"<col=ff9040>" + phoenixName + "</col>", menuEntries, widgetIndex, widgetID);
					}
					if (questHelperBlackArm != null &&  !questHelperBlackArm.isCompleted())
					{
						addRightClickMenuOptions(blackArmName, MENUOP_QUESTHELPER,
							"<col=ff9040>" + blackArmName + "</col>", menuEntries, widgetIndex, widgetID);
					}
				}
			}
			else if (target.equals("Recipe for Disaster"))
			{
				if (selectedQuest != null &&
					(selectedQuest.getQuest().getId() == QuestHelperQuest.RECIPE_FOR_DISASTER.getId()))
				{
					addRightClickMenuOptions(QuestHelperQuest.RECIPE_FOR_DISASTER.getName(), MENUOP_QUESTHELPER,
						event.getTarget(), menuEntries, widgetIndex, widgetID);
				}
				else
				{
					for (String rfdName : RFD_NAMES)
					{
						menuEntries = addRightClickMenuOptions(rfdName, MENUOP_QUESTHELPER,
							"<col=ff9040>" + rfdName + "</col>", menuEntries, widgetIndex, widgetID);
					}
				}
			}
			else
			{
				QuestHelper questHelper = QuestHelperQuest.getByName(target);
				if (questHelper != null && !questHelper.isCompleted())
				{
					if (selectedQuest != null && selectedQuest.getQuest().getName().equals(target))
					{
						addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
					}
					else
					{
						addNewEntry(menuEntries, MENUOP_STARTHELPER, event.getTarget(), widgetIndex, widgetID);
					}
				}
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (config.showFan() && chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
			if (chatMessage.getMessage().contains("Congratulations! Quest complete!") ||
			chatMessage.getMessage().contains("you've completed a quest"))
			{
				addCheerer();
			}
		}
		if (config.autoStartQuests() && chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (selectedQuest == null && chatMessage.getMessage().contains("You've started a new quest"))
			{
				String questName = chatMessage.getMessage().substring(chatMessage.getMessage().indexOf(">") + 1);
				questName = questName.substring(0, questName.indexOf("<"));

				// Prompt for starting Shield of Arrav is the same for both routes. Display actual route started
				if (questName.equals("Shield of Arrav"))
				{
					Player player = client.getLocalPlayer();
					if (player == null)
					{
						return;
					}
					WorldPoint location = player.getWorldLocation();

					if (PHOENIX_START_ZONE.contains(location))
					{
						startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName()));
					}
					else
					{
						startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName()));
					}
				}
				else if (questName.equals("Recipe for Disaster"))
				{
					startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()));
				}
				else
				{
					QuestHelper questHelper = QuestHelperQuest.getByName(questName);
					if (questHelper != null)
					{
						startUpQuest(questHelper);
					}
				}
			}
		}
	}

	private void displayPanel()
	{
		SwingUtilities.invokeLater(() -> {
			if (!navButton.isSelected())
			{
				navButton.getOnSelect().run();
			}
		});
	}

	private MenuEntry[] addNewEntry(MenuEntry[] menuEntries, String newEntry, String target, int widgetIndex, int widgetID)
	{
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(menuEntries.length - 1)
			.setOption(newEntry)
			.setTarget(target)
			.setType(MenuAction.RUNELITE)
			.setParam0(widgetIndex)
			.setParam1(widgetID);

		return menuEntries;
	}

	public void startUpQuest(QuestHelper questHelper)
	{
		startUpQuest(questHelper, true);
	}

	public void startUpQuest(QuestHelper questHelper, boolean shouldOpenSidebarIfConfig)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		shutDownQuest(true);

		if (!questHelper.isCompleted())
		{
			// If running in background, close it
			if (backgroundHelpers.containsValue(questHelper))
			{
				shutDownBackgroundQuest(questHelper);
			}

			if (shouldOpenSidebarIfConfig && config.autoOpenSidebar())
			{
				displayPanel();
			}
			selectedQuest = questHelper;
			eventBus.register(selectedQuest);
			if (isDeveloperMode())
			{
				selectedQuest.debugStartup(config);
			}
			selectedQuest.startUp(config);
			if (selectedQuest.getCurrentStep() == null)
			{
				shutDownQuest(false);
				return;
			}
			questBankManager.startUpQuest();
			SwingUtilities.invokeLater(() -> {
				panel.removeQuest();
				panel.addQuest(questHelper, true);
			});
		}
		else
		{
			panel.removeQuest();
			selectedQuest = null;
		}
	}

	public void shutDownQuestFromSidebar()
	{
		if (selectedQuest != null)
		{
			selectedQuest.shutDown();
			questBankManager.shutDownQuest();
			SwingUtilities.invokeLater(() -> panel.removeQuest());
			eventBus.unregister(selectedQuest);

			// If closing the item checking helper and should still check in background, start it back up in background
			if (selectedQuest.getQuest() == QuestHelperQuest.CHECK_ITEMS && config.highlightItemsBackground())
			{
				selectedQuest = null;
				startUpBackgroundQuest(QuestHelperQuest.CHECK_ITEMS.getName());
			}
			else
			{
				selectedQuest = null;
			}
		}
	}

	private void shutDownQuest(boolean shouldUpdateList)
	{
		if (selectedQuest != null)
		{
			selectedQuest.shutDown();
			if (shouldUpdateList)
			{
				updateQuestList();
				getAllItemRequirements();
			}
			questBankManager.shutDownQuest();
			SwingUtilities.invokeLater(() -> panel.removeQuest());
			eventBus.unregister(selectedQuest);
			selectedQuest = null;
		}
	}

	// Helpers to run in the background without UI
	public void startUpBackgroundQuest(String questHelperName)
	{
		if (!config.highlightItemsBackground())
		{
			return;
		}

		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		if (backgroundHelpers.containsKey(questHelperName))
		{
			return;
		}

		if (selectedQuest != null && selectedQuest.getQuest().getName().equals(questHelperName))
		{
			return;
		}

		QuestHelper questHelper = QuestHelperQuest.getByName(questHelperName);

		if (questHelper == null)
		{
			return;
		}
		clientThread.invokeLater(() -> {
			if (!questHelper.isCompleted())
			{
				eventBus.register(questHelper);
				questHelper.startUp(config);
				backgroundHelpers.put(questHelperName, questHelper);
				if (questHelper.getCurrentStep() == null)
				{
					questHelper.shutDown();
					eventBus.unregister(questHelper);
					backgroundHelpers.remove(questHelperName);
				}

			}
		});
	}

	private void shutDownBackgroundQuest(QuestHelper questHelper)
	{
		if (questHelper == null)
		{
			return;
		}

		if (!backgroundHelpers.containsKey(questHelper.getQuest().getName()))
		{
			return;
		}

		if (questHelper == selectedQuest)
		{
			// Is active quest, so don't close it
			return;
		}

		questHelper.shutDown();
		eventBus.unregister(questHelper);
		backgroundHelpers.remove(questHelper.getQuest().getName());

	}

	private void getAllItemRequirements()
	{
		clientThread.invokeLater(() -> {
			Predicate<QuestHelper> pred = (questHelper) -> false;
			if (config.highlightNeededQuestItems())
			{
				pred = pred.or(QuestHelperConfig.QuestFilter.QUEST);
			}
			if (config.highlightNeededMiniquestItems())
			{
				pred = pred.or(QuestHelperConfig.QuestFilter.MINIQUEST);
			}
			if (config.highlightNeededAchievementDiaryItems())
			{
				pred = pred.or(QuestHelperConfig.QuestFilter.ACHIEVEMENT_DIARY);
			}

			List<QuestHelper> filteredQuests = QuestHelperQuest.getQuestHelpers()
				.stream()
				.filter(pred)
				.filter(QuestDetails::isNotCompleted)
				.sorted(config.orderListBy())
				.collect(Collectors.toList());

			clientThread.invokeLater(() -> {
				SortedMap<QuestHelperQuest, List<ItemRequirement>> newReqs = new TreeMap<>();
				SortedMap<QuestHelperQuest, List<ItemRequirement>> newRecommended = new TreeMap<>();
				filteredQuests.forEach((QuestHelper questHelper) -> {
					if (questHelper.getItemRequirements() != null)
					{
						newReqs.put(questHelper.getQuest(), questHelper.getItemRequirements());
					}
					if (questHelper.getItemRecommended() != null)
					{
						newRecommended.put(questHelper.getQuest(), questHelper.getItemRecommended());
					}
				});
				itemRequirements = newReqs;
				itemRecommended = newRecommended;

				String checkItemsName = QuestHelperQuest.CHECK_ITEMS.getName();
				if (config.highlightItemsBackground())
				{
					shutDownBackgroundQuest(backgroundHelpers.get(checkItemsName));
					startUpBackgroundQuest(checkItemsName);
				}
			});
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
