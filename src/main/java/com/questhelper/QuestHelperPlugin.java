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

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.common.reflect.ClassPath;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.questhelper.banktab.QuestBankTab;
import com.questhelper.banktab.QuestHelperBankTagService;
import com.questhelper.overlays.QuestHelperDebugOverlay;
import com.questhelper.overlays.QuestHelperOverlay;
import com.questhelper.overlays.QuestHelperWidgetOverlay;
import com.questhelper.overlays.QuestHelperWorldArrowOverlay;
import com.questhelper.overlays.QuestHelperWorldLineOverlay;
import com.questhelper.overlays.QuestHelperWorldOverlay;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.Quest;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.QuestStep;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
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
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "Quest Helper",
	description = "Helps you with questing",
	tags = { "quest", "helper", "overlay" }
)
@Slf4j
public class QuestHelperPlugin extends Plugin
{
	private static final int RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID = 164;
	private static final int QUESTTAB_GROUP_ID = 629;

	private static final int[] QUESTLIST_WIDGET_IDS = new int[]
		{
			WidgetInfo.QUESTLIST_FREE_CONTAINER.getId(),
			WidgetInfo.QUESTLIST_MEMBERS_CONTAINER.getId(),
			WidgetInfo.QUESTLIST_MINIQUEST_CONTAINER.getId(),
		};

	private static final int[] QUESTTAB_WIDGET_IDS = new int[]
		{
			RESIZABLE_VIEWPORT_BOTTOM_LINE_GROUP_ID,
			WidgetInfo.RESIZABLE_VIEWPORT_QUESTS_TAB.getId(),
			WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB.getId(),
			QUESTTAB_GROUP_ID
		};

	private static final String QUEST_PACKAGE = "com.questhelper.quests";
	private static final String ACHIEVEMENT_PACKAGE = "com.questhelper.achievementdiaries";

	private static final String MENUOP_STARTHELPER = "Start Quest Helper";
	private static final String MENUOP_STOPHELPER = "Stop Quest Helper";

	private static final String MENUOP_PHOENIXGANG = "Start Quest Helper (Phoenix Gang)";
	private static final String MENUOP_BLACKARMGANG = "Start Quest Helper (Black Arm Gang)";

	private static final String MENUOP_RFD_START = "Start Quest Helper (Starting off)";
	private static final String MENUOP_RFD_SKRACH_UGLOGWEE = "Start Quest Helper (Skrach Uglogwee)";
	private static final String MENUOP_RFD_GOBLINS = "Start Quest Helper (Wartface & Bentnoze)";
	private static final String MENUOP_RFD_EVIL_DAVE = "Start Quest Helper (Evil Dave)";
	private static final String MENUOP_RFD_DWARF = "Start Quest Helper (Dwarf)";
	private static final String MENUOP_RFD_PIRATE_PETE = "Start Quest Helper (Pirate Pete)";
	private static final String MENUOP_RFD_LUMBRIDGE_GUIDE = "Start Quest Helper (Lumbridge Guide)";
	private static final String MENUOP_RFD_SIR_AMIK_VARZE = "Start Quest Helper (Sir Amik Varze)";
	private static final String MENUOP_RFD_MONKEY_AMBASSADOR = "Start Quest Helper (Monkey Ambassador)";
	private static final String MENUOP_RFD_FINALE = "Start Quest Helper (Finale)";

	private static final Zone PHOENIX_START_ZONE = new Zone(new WorldPoint(3204, 3488, 0), new WorldPoint(3221, 3501, 0));

	@Inject
	private QuestBank questBank;

	@Getter
	private QuestHelperBankTagService bankTagService;

	private QuestBankTab bankTagsMain;

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

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private QuestHelperOverlay questHelperOverlay;

	@Inject
	private QuestHelperWidgetOverlay questHelperWidgetOverlay;

	@Inject
	private QuestHelperWorldOverlay questHelperWorldOverlay;

	@Inject
	private QuestHelperWorldArrowOverlay questHelperWorldArrowOverlay;

	@Inject
	private QuestHelperWorldLineOverlay questHelperWorldLineOverlay;

	@Getter
	@Inject
	private BankSearch bankSearch;

	@Getter
	@Inject
	private ItemManager itemManager;

	@Getter
	@Inject
	ChatMessageManager chatMessageManager;

	@Inject
	private QuestHelperDebugOverlay questHelperDebugOverlay;

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

	private Map<String, QuestHelper> quests;

	@Inject
	SpriteManager spriteManager;

	@Getter
	@Inject
	private ColorPickerManager colorPickerManager;

	@Getter
	@Inject
	ConfigManager configManager;

	private QuestHelperPanel panel;

	private NavigationButton navButton;

	private boolean loadQuestList;

	private boolean displayNameKnown;

	@Provides
	QuestHelperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(QuestHelperConfig.class);
	}

	@Override
	protected void startUp() throws IOException
	{
		bankTagService = new QuestHelperBankTagService(this);
		bankTagsMain = new QuestBankTab(this);
		bankTagsMain.startUp();

		injector.injectMembers(bankTagsMain);
		eventBus.register(bankTagsMain);

		quests = scanAndInstantiate(getClass().getClassLoader());
		overlayManager.add(questHelperOverlay);
		overlayManager.add(questHelperWorldOverlay);
		overlayManager.add(questHelperWorldArrowOverlay);
		overlayManager.add(questHelperWorldLineOverlay);
		overlayManager.add(questHelperWidgetOverlay);
		if (isDeveloperMode())
		{
			overlayManager.add(questHelperDebugOverlay);
		}

		final BufferedImage icon = Icon.QUEST_ICON.getImage();

		panel = new QuestHelperPanel(this);
		navButton = NavigationButton.builder()
			.tooltip("Quest Helper")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			loadQuestList = true;
		}
	}

	@Override
	protected void shutDown()
	{
		eventBus.unregister(bankTagsMain);
		overlayManager.remove(questHelperOverlay);
		overlayManager.remove(questHelperWorldOverlay);
		overlayManager.remove(questHelperWorldArrowOverlay);
		overlayManager.remove(questHelperWorldLineOverlay);
		overlayManager.remove(questHelperWidgetOverlay);
		if (isDeveloperMode())
		{
			overlayManager.remove(questHelperDebugOverlay);
		}
		clientToolbar.removeNavigation(navButton);
		shutDownQuest(false);
		bankTagService = null;
		bankTagsMain = null;
		quests = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!displayNameKnown)
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer != null && localPlayer.getName() != null) {
				displayNameKnown = true;
				questBank.loadState();
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
					panel.updateHighlight(currentStep);
				}
				panel.updateLocks();
			}
		}
		if (loadQuestList)
		{
			loadQuestList = false;
			updateQuestList();
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.BANK))
		{
			questBank.updateLocalBank(event.getItemContainer().getItems());
		}
		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			clientThread.invokeLater(() -> panel.updateItemRequirements(client, questBank.getBankItems()));
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged event)
	{
		final GameState state = event.getGameState();

		if (state == GameState.LOGIN_SCREEN)
		{
			questBank.saveBankToConfig();
			panel.refresh(Collections.emptyList(), true, new HashMap<>());
			questBank.emptyState();
			if (selectedQuest != null && selectedQuest.getCurrentStep() != null)
			{
				shutDownQuest(true);
			}
		}

		if (state == GameState.LOGGED_IN)
		{
			loadQuestList = true;
			displayNameKnown = false;
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

	private final Collection<String> configEvents = Arrays.asList("orderListBy", "filterListBy", "questDifficulty", "showCompletedQuests");

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("questhelper") && configEvents.contains(event.getKey()))
		{
			clientThread.invokeLater(this::updateQuestList);
		}
	}

	@Subscribe(priority = 100)
	private void onClientShutdown(ClientShutdown e)
	{
		questBank.saveBankToConfig();
	}

	public void updateQuestList()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			List<QuestHelper> filteredQuests = quests.values()
				.stream()
				.filter(config.filterListBy())
				.filter(config.difficulty())
				.filter(Quest::showCompletedQuests)
				.sorted(config.orderListBy())
				.collect(Collectors.toList());
			Map<QuestHelperQuest, QuestState> completedQuests = quests.values()
				.stream()
				.collect(Collectors.toMap(QuestHelper::getQuest, q -> q.getState(client)));
			SwingUtilities.invokeLater(() -> {
				panel.refresh(filteredQuests, false, completedQuests, config.orderListBy().getSections());
			});
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuAction() == MenuAction.RUNELITE)
		{
			switch (event.getMenuOption())
			{
				case MENUOP_STARTHELPER:
					event.consume();
					String quest = Text.removeTags(event.getMenuTarget());
					startUpQuest(quests.get(quest));
					break;
				case MENUOP_STOPHELPER:
					event.consume();
					shutDownQuest(true);
					break;
				case MENUOP_PHOENIXGANG:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName()));
					break;
				case MENUOP_BLACKARMGANG:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName()));
					break;
				case MENUOP_RFD_START:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()));
					break;

				case MENUOP_RFD_DWARF:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_DWARF.getName()));
					break;
				case MENUOP_RFD_EVIL_DAVE:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_EVIL_DAVE.getName()));
					break;
				case MENUOP_RFD_GOBLINS:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE.getName()));
					break;
				case MENUOP_RFD_PIRATE_PETE:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_PIRATE_PETE.getName()));
					break;
				case MENUOP_RFD_LUMBRIDGE_GUIDE:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE.getName()));
					break;
				case MENUOP_RFD_SKRACH_UGLOGWEE:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE.getName()));
					break;
				case MENUOP_RFD_SIR_AMIK_VARZE:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_SIR_AMIK_VARZE.getName()));
					break;
				case MENUOP_RFD_MONKEY_AMBASSADOR:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR.getName()));
					break;
				case MENUOP_RFD_FINALE:
					event.consume();
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE.getName()));
					break;
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int widgetIndex = event.getActionParam0();
		int widgetID = event.getActionParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();
		String target = Text.removeTags(event.getTarget());

		if (Ints.contains(QUESTLIST_WIDGET_IDS, widgetID) && "Read Journal:".equals(event.getOption()))
		{
			if (target.equals("Shield of Arrav"))
			{
				QuestHelper questHelperPhoenix = quests.get(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName());
				QuestHelper questHelperBlackArm = quests.get(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName());
				if (questHelperBlackArm != null && !questHelperBlackArm.isCompleted()
					|| questHelperPhoenix != null && !questHelperPhoenix.isCompleted())
				{
					if (selectedQuest != null &&
						(selectedQuest.getQuest().getName().equals(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName()) ||
							selectedQuest.getQuest().getName().equals(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName())))
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
					}
					else
					{
						if (!questHelperPhoenix.isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_PHOENIXGANG, event.getTarget(), widgetIndex, widgetID);
						}
						if (!questHelperBlackArm.isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_BLACKARMGANG, event.getTarget(), widgetIndex, widgetID);
						}
					}
				}
			}
			else if (target.equals("Recipe for Disaster"))
			{
				if (selectedQuest != null &&
					(selectedQuest.getQuest().getId() == QuestHelperQuest.RECIPE_FOR_DISASTER.getId()))
				{
					menuEntries = addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
				}
				else
				{
					if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()).isCompleted())
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_RFD_START, event.getTarget(), widgetIndex, widgetID);
					}
					else
					{
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_LUMBRIDGE_GUIDE, event.getTarget(), widgetIndex, widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_PIRATE_PETE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_PIRATE_PETE, event.getTarget(), widgetIndex, widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_SKRACH_UGLOGWEE, event.getTarget(), widgetIndex, widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_GOBLINS, event.getTarget(), widgetIndex,
								widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_EVIL_DAVE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_EVIL_DAVE, event.getTarget(), widgetIndex,
								widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_DWARF.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_DWARF, event.getTarget(), widgetIndex,
								widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_SIR_AMIK_VARZE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_SIR_AMIK_VARZE, event.getTarget(), widgetIndex, widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_MONKEY_AMBASSADOR, event.getTarget(), widgetIndex, widgetID);
						}
						if (!quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_FINALE.getName()).isCompleted())
						{
							menuEntries = addNewEntry(menuEntries, MENUOP_RFD_FINALE, event.getTarget(), widgetIndex, widgetID);
						}
					}
				}
			}
			else
			{
				QuestHelper questHelper = quests.get(target);
				if (questHelper != null && !questHelper.isCompleted())
				{
					if (selectedQuest != null && selectedQuest.getQuest().getName().equals(target))
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
					}
					else
					{
						menuEntries = addNewEntry(menuEntries, MENUOP_STARTHELPER, event.getTarget(), widgetIndex, widgetID);
					}
				}
			}
		}

		if (Ints.contains(QUESTTAB_WIDGET_IDS, widgetID)
			&& "Quest List".equals(event.getOption())
			&& selectedQuest != null)
		{
			addNewEntry(menuEntries, MENUOP_STOPHELPER, event.getTarget(), widgetIndex, widgetID);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (config.autoStartQuests() && chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (selectedQuest == null && chatMessage.getMessage().contains("You've started a new quest"))
			{
				String questName = chatMessage.getMessage().substring(chatMessage.getMessage().indexOf(">") + 1);
				questName = questName.substring(0, questName.indexOf("<"));
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
						startUpQuest(quests.get(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName()));
					}
					else
					{
						startUpQuest(quests.get(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName()));
					}
				}
				else if (questName.equals("Recipe for Disaster"))
				{
					startUpQuest(quests.get(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()));
				}
				else
				{
					QuestHelper questHelper = quests.get(questName);
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

		MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();
		menuEntry.setTarget(target);
		menuEntry.setParam0(widgetIndex);
		menuEntry.setParam1(widgetID);
		menuEntry.setType(MenuAction.RUNELITE.getId());
		menuEntry.setOption(newEntry);

		client.setMenuEntries(menuEntries);

		return menuEntries;
	}

	public void startUpQuest(QuestHelper questHelper)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		shutDownQuest(true);

		if (!questHelper.isCompleted())
		{
			if (config.autoOpenSidebar())
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
			bankTagsMain.startUp();
			SwingUtilities.invokeLater(() -> {
				panel.removeQuest();
				panel.addQuest(questHelper, true);
				clientThread.invokeLater(() -> panel.updateItemRequirements(client, questBank.getBankItems()));
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
			bankTagsMain.shutDown();
			SwingUtilities.invokeLater(() -> panel.removeQuest());
			eventBus.unregister(selectedQuest);
			selectedQuest = null;
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
			}
			if (bankTagsMain != null)
			{
				bankTagsMain.shutDown();
			}
			SwingUtilities.invokeLater(() -> panel.removeQuest());
			eventBus.unregister(selectedQuest);
			selectedQuest = null;
		}
	}

	private Map<String, QuestHelper> scanAndInstantiate(ClassLoader classLoader) throws IOException
	{

		Map<String, QuestHelper> scannedQuests = new HashMap<>();
		ClassPath classPath = ClassPath.from(classLoader);

		scannedQuests.putAll(instantiate(classPath, QuestHelperPlugin.QUEST_PACKAGE));
		scannedQuests.putAll(instantiate(classPath, QuestHelperPlugin.ACHIEVEMENT_PACKAGE));

		return scannedQuests;
	}

	private Map<String, QuestHelper> instantiate(ClassPath classPath, String packageName)
	{
		Map<String, QuestHelper> scannedQuests = new HashMap<>();
		Map<QuestHelperQuest, Class<? extends QuestHelper>> tmpQuests = new HashMap<>();

		ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive(packageName);
		for (ClassPath.ClassInfo classInfo : classes)
		{
			Class<?> clazz = classInfo.load();
			QuestDescriptor questDescriptor = clazz.getAnnotation(QuestDescriptor.class);

			if (questDescriptor == null)
			{
				if (clazz.getSuperclass() == QuestHelper.class)
				{
					log.warn("Class {} is a quest helper, but has no quest descriptor",
						clazz);
				}
				continue;
			}

			if (clazz.isAssignableFrom(QuestHelper.class))
			{
				log.warn("Class {} has quest descriptor, but is not a quest helper",
					clazz);
				continue;
			}

			Class<QuestHelper> questClass = (Class<QuestHelper>) clazz;
			tmpQuests.put(questDescriptor.quest(), questClass);
		}

		for (Map.Entry<QuestHelperQuest, Class<? extends QuestHelper>> questClazz : tmpQuests.entrySet())
		{
			QuestHelper questHelper;
			try
			{
				questHelper = instantiate((Class<QuestHelper>) questClazz.getValue(), questClazz.getKey());
			}
			catch (QuestInstantiationException ex)
			{
				log.warn("Error instantiating quest helper!", ex);
				continue;
			}

			scannedQuests.put(questClazz.getKey().getName(), questHelper);
		}

		return scannedQuests;
	}

	private QuestHelper instantiate(Class<QuestHelper> clazz, QuestHelperQuest quest) throws QuestInstantiationException
	{
		QuestHelper questHelper;
		try
		{
			questHelper = clazz.newInstance();
			Module questModule = (Binder binder) ->
			{
				binder.bind(clazz).toInstance(questHelper);
				binder.install(questHelper);
			};
			Injector questInjector = RuneLite.getInjector().createChildInjector(questModule);
			questInjector.injectMembers(questHelper);
			questHelper.setInjector(questInjector);
			questHelper.setQuest(quest);
			questHelper.setConfig(config);
		}
		catch (InstantiationException | IllegalAccessException | CreationException ex)
		{
			throw new QuestInstantiationException(ex);
		}

		log.debug("Loaded quest helper {}", clazz.getSimpleName());
		return questHelper;
	}
}
