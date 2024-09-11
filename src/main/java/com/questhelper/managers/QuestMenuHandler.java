/*
 *  * Copyright (c) 2023, Zoinkwiz (https://www.github.com/Zoinkwiz)
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.questhelper.managers;

import com.google.common.primitives.Ints;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.tools.QuestWidgets;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Manages the quest menu options within the game. This class is responsible for
 * mapping quest names to their respective helper objects and also for setting up
 * contextual menu options related to quests and achievements.
 * <p>
 * The class makes use of dependency injection to gain access to the game client
 * and the quest manager.
 * </p>
 */
@Singleton
public class QuestMenuHandler
{
	@Inject
	private Client client;

	@Inject
	private QuestManager questManager;

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

	private static final Zone PHOENIX_START_ZONE = new Zone(new WorldPoint(3204, 3488, 0), new WorldPoint(3221, 3501, 0));

	private static final int[] ACHIEVEMENTLIST_WIDGET_IDS = new int[]
		{
			ComponentID.ACHIEVEMENT_DIARY_CONTAINER
		};

	private static final String[] ACHIEVEMENT_TIERS = new String[]
		{
			"Elite",
			"Hard",
			"Medium",
			"Easy"
		};

	private static final String MENUOP_QUESTHELPER = "Quest Helper";
	private static final String MENUOP_GENERICHELPER = "Helper";
	private static final String MENUOP_STOPHELPER = "Stop Quest Helper";

	/**
	 * Starts up the quest helper for the given quest name.
	 * <p>
	 * Special cases like "Shield of Arrav" and "Recipe for Disaster" are
	 * handled separately.
	 * </p>
	 *
	 * @param questName The name of the quest to start.
	 */
	public void startUpQuest(String questName)
	{
		if ("Shield of Arrav".equals(questName))
		{
			handleShieldOfArrav();
		}
		else if ("Recipe for Disaster".equals(questName))
		{
			handleRecipeForDisaster();
		}
		else
		{
			handleGenericQuest(questName);
		}
	}

	/**
	 * Handles the special case for starting up the "Shield of Arrav" quest.
	 */
	private void handleShieldOfArrav()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		WorldPoint location = player.getWorldLocation();
		QuestHelperQuest questToStart = PHOENIX_START_ZONE.contains(location) ?
			QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG :
			QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG;

		questManager.startUpQuest(QuestHelperQuest.getByName(questToStart.getName()));
	}

	/**
	 * Handles the special case for starting up the "Recipe for Disaster" quest.
	 */
	private void handleRecipeForDisaster()
	{
		questManager.startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()));
	}

	/**
	 * Handles the general case for starting up a quest.
	 *
	 * @param questName The name of the quest to start.
	 */
	private void handleGenericQuest(String questName)
	{
		QuestHelper questHelper = QuestHelperQuest.getByName(questName);
		if (questHelper != null)
		{
			questManager.startUpQuest(questHelper);
		}
	}

	/**
	 * Sets up the quest menu options based on the provided parameters.
	 * <p>
	 * The method handles both achievement lists and quest lists to provide
	 * the appropriate menu options.
	 * </p>
	 *
	 * @param menuEntries An array of existing menu entries.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 * @param target      The target quest or achievement.
	 * @param option      The menu option being selected.
	 */
	public void setupQuestMenuOptions(MenuEntry[] menuEntries, int widgetIndex, int widgetID, String target, String option)
	{
		if (Ints.contains(ACHIEVEMENTLIST_WIDGET_IDS, widgetID) && option.contains("Open "))
		{
			handleAchievementList(menuEntries, widgetIndex, widgetID, option);
		}

		if (Ints.contains(QUESTLIST_WIDGET_IDS, widgetID) && "Read journal:".equals(option))
		{
			handleQuestList(menuEntries, widgetIndex, widgetID, target);
		}
	}

	/**
	 * Handles the setup of menu options related to achievement lists.
	 *
	 * @param menuEntries An array of existing menu entries.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 * @param option      The menu option being selected.
	 */
	private void handleAchievementList(MenuEntry[] menuEntries, int widgetIndex, int widgetID, String option)
	{
		String diary = option.replace("Journal", "").replace("Open ", "");
		diary = Text.removeTags(diary);
		for (String achievementTier : ACHIEVEMENT_TIERS)
		{
			addRightClickMenuOptions(diary + achievementTier + " Diary", MENUOP_GENERICHELPER,
				 menuEntries, widgetIndex, widgetID);
		}
	}

	/**
	 * Handles the setup of menu options related to quest lists.
	 *
	 * @param menuEntries An array of existing menu entries.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 * @param target      The target quest or achievement.
	 */
	private void handleQuestList(MenuEntry[] menuEntries, int widgetIndex, int widgetID, String target)
	{
		if ("Shield of Arrav".equals(target))
		{
			handleShieldOfArravInMenu(menuEntries, widgetIndex, widgetID);
		}
		else if ("Recipe for Disaster".equals(target))
		{
			handleRecipeForDisasterInMenu(menuEntries, widgetIndex, widgetID);
		}
		else
		{
			addRightClickMenuOptions(target, "Quest Helper", menuEntries, widgetIndex, widgetID);
		}
	}

	/**
	 * Handles the special case of the "Shield of Arrav" quest in the menu.
	 *
	 * @param menuEntries An array of existing menu entries.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 */
	private void handleShieldOfArravInMenu(MenuEntry[] menuEntries, int widgetIndex, int widgetID)
	{
		if (questManager.getSelectedQuest() != null &&
			(questManager.getSelectedQuest().getQuest().getId() == QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getId()))
		{
			addNewEntry(menuEntries, MENUOP_STOPHELPER, "Shield of Arrav", widgetIndex, widgetID);
		}
		else
		{
			String phoenixName = QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName();
			String blackArmName = QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName();
			QuestHelper questHelperPhoenix = QuestHelperQuest.getByName(phoenixName);
			QuestHelper questHelperBlackArm = QuestHelperQuest.getByName(blackArmName);
			if (questHelperPhoenix != null && !questHelperPhoenix.isCompleted())
			{
				addRightClickMenuOptions(phoenixName, MENUOP_QUESTHELPER,
					 menuEntries, widgetIndex, widgetID);
			}
			if (questHelperBlackArm != null && !questHelperBlackArm.isCompleted())
			{
				addRightClickMenuOptions(blackArmName, MENUOP_QUESTHELPER,
					 menuEntries, widgetIndex, widgetID);
			}
		}
	}

	/**
	 * Handles the special case of the "Recipe for Disaster" quest in the menu.
	 *
	 * @param menuEntries An array of existing menu entries.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 */
	private void handleRecipeForDisasterInMenu(MenuEntry[] menuEntries, int widgetIndex, int widgetID)
	{
		if (questManager.getSelectedQuest() != null &&
			(questManager.getSelectedQuest().getQuest().getId() == QuestHelperQuest.RECIPE_FOR_DISASTER_START.getId()))
		{
			addRightClickMenuOptions(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName(), MENUOP_QUESTHELPER,
				 menuEntries, widgetIndex, widgetID);
		}
		else
		{
			for (String rfdName : RFD_NAMES)
			{
				addRightClickMenuOptions(rfdName, MENUOP_QUESTHELPER,
					 menuEntries, widgetIndex, widgetID);
			}
		}
	}

	/**
	 * Adds new menu entries for given helper name and entry name.
	 *
	 * @param helperName  The name of the helper.
	 * @param entryName   The name of the entry.
	 * @param menuEntries An array of existing menu entries.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 * @return An array of updated menu entries.
	 */
	private void addRightClickMenuOptions(String helperName, String entryName,
												 MenuEntry[] menuEntries,
												 int widgetIndex, int widgetID)
	{
		QuestHelper questHelper = QuestHelperQuest.getByName(helperName);

		if (questHelper != null && !questHelper.isCompleted())
		{
			String menuOption;
			if (questManager.getSelectedQuest() != null && questManager.getSelectedQuest().getQuest().getName().equals(helperName))
			{
				menuOption = "Stop " + entryName;
			}
			else
			{
				menuOption = "Start " + entryName;
			}
			addNewEntry(menuEntries, menuOption, helperName, widgetIndex, widgetID);
		}
	}

	/**
	 * Adds a new entry to the menu.
	 *
	 * @param menuEntries An array of existing menu entries.
	 * @param newEntry    The new entry to add.
	 * @param target      The target quest or achievement.
	 * @param widgetIndex The index of the widget being interacted with.
	 * @param widgetID    The ID of the widget being interacted with.
	 * @return An array of updated menu entries.
	 */
	private void addNewEntry(MenuEntry[] menuEntries, String newEntry, String target,
									int widgetIndex, int widgetID)
	{
		Menu menu = client.getMenu();
		menu.createMenuEntry(menuEntries.length - 1)
			.setOption(newEntry)
			.setTarget("<col=ff9040>" + target + "</col>")
			.onClick((menuEntry -> handleMenuEntryClick(newEntry, target)))
			.setType(MenuAction.RUNELITE)
			.setParam0(widgetIndex)
			.setParam1(widgetID);
	}

	/**
	 * Handles the click event for a menu entry.
	 *
	 * @param newEntry The clicked menu entry.
	 * @param target   The target quest or achievement.
	 */
	private void handleMenuEntryClick(String newEntry, String target)
	{
		if (newEntry.startsWith("Start"))
		{
			String quest = Text.removeTags(target);
			questManager.startUpQuest(QuestHelperQuest.getByName(quest));
		}
		else
		{
			questManager.shutDownQuest(true);
		}
	}
}
