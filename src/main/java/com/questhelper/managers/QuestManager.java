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

import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.QuestStep;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.QuestState;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*
** This class is intended to maintain the creation of Helpers, deletion, and other assets involved in this process
 */
@Singleton
public class QuestManager
{
	@Inject
	Client client;

	@Inject
	ClientThread clientThread;

	@Inject
	EventBus eventBus;

	@Inject
	QuestBankManager questBankManager;

	@Inject
	QuestHelperConfig config;

	@Inject
	QuestHelperPlugin questHelperPlugin;

	@Getter
	@Inject
	@Named("developerMode")
	private boolean developerMode;

	@Setter
	private QuestHelper sidebarSelectedQuest;

	@Getter
	private QuestHelper selectedQuest;

	private QuestStep lastStep = null;

	private boolean loadQuestList = false;

	public Map<String, QuestHelper> backgroundHelpers = new HashMap<>();
	public SortedMap<QuestHelperQuest, List<ItemRequirement>> itemRequirements = new TreeMap<>();
	public SortedMap<QuestHelperQuest, List<ItemRequirement>> itemRecommended = new TreeMap<>();

	private QuestHelperPanel panel;

	public void startUp(QuestHelperPanel panel)
	{
		this.panel = panel;
	}

	public void shutDown()
	{
		this.panel = null;
		shutDownQuest(false);
	}

	public void setupOnLogin()
	{
		QuestHelperQuest.CHECK_ITEMS.getQuestHelper().init();
		getAllItemRequirements();
		loadQuestList = true;
	}

	public void updateQuestState()
	{
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

	public void handleVarbitChanged()
	{
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

	public void handleConfigChanged()
	{
		// Catches Player Made Quests
		clientThread.invokeLater(() -> {
			if ((selectedQuest != null) && selectedQuest.isCompleted())
			{
				shutDownQuest(true);
			}
		});
	}

	public void updateQuestList()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			System.out.println(config.showCompletedQuests());
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

	/* Startup quests */
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
				questHelperPlugin.displayPanel();
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
			System.out.println(selectedQuest);
		}
		else
		{
			panel.removeQuest();
			selectedQuest = null;
		}
	}

	/* Shutdown quests */
	public void shutDownQuestFromSidebar()
	{
		if (selectedQuest != null)
		{
			selectedQuest.shutDown();
			questBankManager.shutDownQuest();
			SwingUtilities.invokeLater(panel::removeQuest);
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

	public void shutDownQuest(boolean shouldUpdateList)
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
			SwingUtilities.invokeLater(panel::removeQuest);
			eventBus.unregister(selectedQuest);
			selectedQuest = null;
		}
	}


	/* Background Helpers Functions */
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

	public void updateAllItemsHelper()
	{
		getAllItemRequirements();
		if (selectedQuest != null && selectedQuest.getQuest() == QuestHelperQuest.CHECK_ITEMS)
		{
			clientThread.invokeLater(() -> startUpQuest(QuestHelperQuest.CHECK_ITEMS.getQuestHelper(), false));
		}
	}

	public void updateAllItemsBackgroundHelper(String shouldRun)
	{
		// If shouldn't highlight, shut down highlights
		if (Objects.equals(shouldRun, "false"))
		{
			shutDownBackgroundQuest(backgroundHelpers.get(QuestHelperQuest.CHECK_ITEMS.getName()));
		}
		else
		{
			startUpBackgroundQuest(QuestHelperQuest.CHECK_ITEMS.getName());
		}
	}
}
