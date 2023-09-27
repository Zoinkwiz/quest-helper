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
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class QuestNameToHelper
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
			WidgetInfo.ACHIEVEMENT_DIARY_CONTAINER.getId()
		};

	private static final String[] achievementTiers = new String[]
		{
			"Elite",
			"Hard",
			"Medium",
			"Easy"
		};

	private static final String MENUOP_QUESTHELPER = "Quest Helper";
	private static final String MENUOP_GENERICHELPER = "Helper";
	private static final String MENUOP_STARTHELPER = "Start Quest Helper";
	private static final String MENUOP_STOPHELPER = "Stop Quest Helper";

	public void startUpQuest(String questName, QuestManager questManager)
	{
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
				questManager.startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getName()));
			}
			else
			{
				questManager.startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG.getName()));
			}
		}
		else if (questName.equals("Recipe for Disaster"))
		{
			questManager.startUpQuest(QuestHelperQuest.getByName(QuestHelperQuest.RECIPE_FOR_DISASTER_START.getName()));
		}
		else
		{
			QuestHelper questHelper = QuestHelperQuest.getByName(questName);
			if (questHelper != null)
			{
				questManager.startUpQuest(questHelper);
			}
		}
	}

	public void setupQuestMenuOptions(MenuEntry[] menuEntries, int widgetIndex, int widgetID, String target, String option)
	{
		if (Ints.contains(ACHIEVEMENTLIST_WIDGET_IDS, widgetID) && option.contains("Open "))
		{
			String diary = option.replace("Journal", "");
			diary = diary.replace("Open ", "");
			diary = Text.removeTags(diary);
			for (String achievementTier : achievementTiers)
			{
				menuEntries = addRightClickMenuOptions(diary + achievementTier + " Diary", MENUOP_GENERICHELPER,
					 diary + achievementTier + " Diary", menuEntries, widgetIndex, widgetID);
			}
		}

		if (Ints.contains(QUESTLIST_WIDGET_IDS, widgetID) && "Read journal:".equals(option))
		{
			if (target.equals("Shield of Arrav"))
			{
				if (questManager.getSelectedQuest() != null &&
					(questManager.getSelectedQuest().getQuest().getId() == QuestHelperQuest.SHIELD_OF_ARRAV_PHOENIX_GANG.getId()))
				{
					addNewEntry(menuEntries, MENUOP_STOPHELPER, target, widgetIndex, widgetID, false);
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
							phoenixName, menuEntries, widgetIndex, widgetID);
					}
					if (questHelperBlackArm != null &&  !questHelperBlackArm.isCompleted())
					{
						addRightClickMenuOptions(blackArmName, MENUOP_QUESTHELPER,
							blackArmName, menuEntries, widgetIndex, widgetID);
					}
				}
			}
			else if (target.equals("Recipe for Disaster"))
			{
				if (questManager.getSelectedQuest() != null &&
					(questManager.getSelectedQuest().getQuest().getId() == QuestHelperQuest.RECIPE_FOR_DISASTER.getId()))
				{
					addRightClickMenuOptions(QuestHelperQuest.RECIPE_FOR_DISASTER.getName(), MENUOP_QUESTHELPER,
						target, menuEntries, widgetIndex, widgetID);
				}
				else
				{
					for (String rfdName : RFD_NAMES)
					{
						menuEntries = addRightClickMenuOptions(rfdName, MENUOP_QUESTHELPER,
							rfdName, menuEntries, widgetIndex, widgetID);
					}
				}
			}
			else
			{
				QuestHelper questHelper = QuestHelperQuest.getByName(target);
				if (questHelper != null && !questHelper.isCompleted())
				{
					if (questManager.getSelectedQuest() != null && questManager.getSelectedQuest().getQuest().getName().equals(target))
					{
						addNewEntry(menuEntries, MENUOP_STOPHELPER, target, widgetIndex, widgetID, false);
					}
					else
					{
						addNewEntry(menuEntries, MENUOP_STARTHELPER, target, widgetIndex, widgetID, true);
					}
				}
			}
		}
	}

	private MenuEntry[] addRightClickMenuOptions(String helperName, String entryName, String target,
														MenuEntry[] menuEntries,
														int widgetIndex, int widgetID)
	{
		QuestHelper questHelper = QuestHelperQuest.getByName(helperName);
		if (questHelper != null && !questHelper.isCompleted())
		{
			if (questManager.getSelectedQuest() != null && questManager.getSelectedQuest().getQuest().getName().equals(helperName))
			{
				return addNewEntry(menuEntries, "Stop " + entryName, target, widgetIndex, widgetID, false);
			}
			else
			{
				return addNewEntry(menuEntries, "Start " + entryName, target, widgetIndex, widgetID, true);
			}
		}

		return menuEntries;
	}

	private MenuEntry[] addNewEntry(MenuEntry[] menuEntries, String newEntry, String target, int widgetIndex, int widgetID, boolean startUp)
	{
		menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

		client.createMenuEntry(menuEntries.length - 1)
			.setOption(newEntry)
			.setTarget("<col=ff9040>" + target + "</col>")
			.onClick((menuEntry -> {
				if (startUp)
				{
					String quest = Text.removeTags(target);
					questManager.startUpQuest(QuestHelperQuest.getByName(quest));
				}
				else
				{
					questManager.shutDownQuest(true);
				}
			}))
			.setType(MenuAction.RUNELITE)
			.setParam0(widgetIndex)
			.setParam1(widgetID);

		return menuEntries;
	}
}
