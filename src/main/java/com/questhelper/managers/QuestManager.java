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
import com.questhelper.config.SkillFiltering;
import com.questhelper.panel.QuestHelperPanel;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.QuestStep;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Manages the lifecycle and state of quests.
 * Responsible for initializing, updating, and shutting down quests.
 */
@Singleton
@Slf4j
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

	@Getter
	private QuestHelper selectedQuest;
	private boolean loadQuestList = false;
	private QuestHelperPanel panel;
	private QuestStep lastStep = null;

	public Map<String, QuestHelper> backgroundHelpers = new HashMap<>();
	public SortedMap<QuestHelperQuest, List<ItemRequirement>> itemRequirements = new TreeMap<>();
	public SortedMap<QuestHelperQuest, List<ItemRequirement>> itemRecommended = new TreeMap<>();

	/**
	 * Initializes the QuestManager with the given QuestHelperPanel.
	 *
	 * @param panel The QuestHelperPanel to be used.
	 */
	public void startUp(QuestHelperPanel panel)
	{
		this.panel = panel;
	}

	public void shutDown()
	{
		shutDownQuest(false);
		this.panel = null;
	}

	public void setupOnLogin()
	{
		QuestHelperQuest.CHECK_ITEMS.getQuestHelper().init();
		getAllItemRequirements();
		loadQuestList = true;
	}

	/**
	 * Updates the state of the active and sidebar quests.
	 * Delegates the tasks to handleSidebarQuest, handleSelectedQuest, and handleQuestListUpdate.
	 */
	public void updateQuestState()
	{
		handleSelectedQuest();
		handleQuestListUpdate();
	}

	/**
	 * Handles the currently selected quest.
	 * Updates steps, highlights, and item requirements.
	 */
	private void handleSelectedQuest()
	{
		if (selectedQuest != null)
		{
			if (selectedQuest.getCurrentStep() != null)
			{
				panel.updateStepsTexts();
				QuestStep currentStep = selectedQuest.getCurrentStep().getSidePanelStep();
				if (currentStep != null && currentStep != lastStep && panel.questActive)
				{
					lastStep = currentStep;
					panel.updateHighlight(client, currentStep);
				}
				if (panel.questActive)
				{
					panel.updateItemRequirements(client);
				}
				panel.updateLocks();
			}
		}
	}

	/**
	 * Updates the list of available quests.
	 * Resets the flag to avoid redundant updates.
	 */
	private void handleQuestListUpdate()
	{
		if (loadQuestList)
		{
			loadQuestList = false;
			updateQuestList();
			getAllItemRequirements();
		}
	}

	/**
	 * Handles changes in game varbits.
	 * Checks for quest completion and shuts down the quest if completed.
	 */
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

	/**
	 * Handles configuration changes.
	 * Specifically, it checks if a player-made quest has been completed.
	 */
	public void handleConfigChanged()
	{
		clientThread.invokeLater(() -> {
			if ((selectedQuest != null) && selectedQuest.isCompleted())
			{
				shutDownQuest(true);
			}
		});
	}

	/**
	 * Updates the list of quests displayed in the panel.
	 * This is based on the current game state and user configurations.
	 */
	public void updateQuestList()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			List<QuestHelper> filteredQuests = QuestHelperQuest.getQuestHelpers(isDeveloperMode())
				.stream()
				.filter(config.filterListBy())
				.filter(config.difficulty())
				.filter(QuestDetails::showCompletedQuests)
				.filter(SkillFiltering::passesSkillFilter)
				.sorted(config.orderListBy())
				.collect(Collectors.toList());
			Map<QuestHelperQuest, QuestState> completedQuests = QuestHelperQuest.getQuestHelpers(isDeveloperMode())
				.stream()
				.collect(Collectors.toMap(QuestHelper::getQuest, q -> q.getState(client)));
			SwingUtilities.invokeLater(() -> {
				if (panel != null) {
					panel.refresh(filteredQuests, false, completedQuests, config.orderListBy().getSections());
				}
			});
		}
	}

	private void doStartUpQuest(QuestHelper questHelper, boolean shouldOpenSidebarIfConfig)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		shutDownPreviousQuest();
		initializeNewQuest(questHelper, shouldOpenSidebarIfConfig);
	}

	/**
	 * Starts up a quest.
	 * Shuts down any active quest and initializes the new quest.
	 * <p>
	 * This can be called from any thread
	 *
	 * @param questHelper The quest to be started.
	 * @param shouldOpenSidebarIfConfig Flag to open the sidebar if configured.
	 */
	public void startUpQuest(QuestHelper questHelper, boolean shouldOpenSidebarIfConfig)
	{
		if (client.isClientThread()) {
			this.doStartUpQuest(questHelper, shouldOpenSidebarIfConfig);
		} else {
			clientThread.invokeLater(() -> {
				this.doStartUpQuest(questHelper, shouldOpenSidebarIfConfig);
			});
		}
	}

	private void initializeNewQuest(QuestHelper questHelper, boolean shouldOpenSidebarIfConfig)
	{
		if (!questHelper.isCompleted())
		{
			if (backgroundHelpers.containsValue(questHelper))
			{
				shutDownBackgroundQuest(questHelper);
			}

			if (shouldOpenSidebarIfConfig && config.autoOpenSidebar())
			{
				questHelperPlugin.displayPanel();
			}
			selectedQuest = questHelper;
			registerQuestToEventBus(selectedQuest);
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
			SwingUtilities.invokeLater(() ->
			{
				panel.removeQuest();
				panel.addQuest(questHelper, true);

				// Force an extra update immediately after starting a quest
				clientThread.invokeLater(() -> panel.updateItemRequirements(client));
			});
		}
		else
		{
			panel.removeQuest();
			selectedQuest = null;
		}
	}

	private void shutDownPreviousQuest()
	{
		shutDownQuest(true);
	}

	/**
	 * Shuts down the currently active quest.
	 * Also updates the quest list.
	 *
	 * @param shouldUpdateList Flag to update the quest list.
	 */
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
			unregisterQuestFromEventBus(selectedQuest);

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

		this.lastStep = null;
	}

	public void activateShortestPath()
	{
		if (selectedQuest == null) return;
		selectedQuest.getCurrentStep().getActiveStep().setShortestPath();
	}

	public void disableShortestPath()
	{
		if (selectedQuest == null) return;
		selectedQuest.getCurrentStep().getActiveStep().disableShortestPath();
	}

	/**
	 * Starts up a background quest based on the quest name.
	 *
	 * @param questHelperName The name of the quest to be started in the background.
	 */
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

		if (selectedQuest != null && selectedQuest.getQuest() != null && selectedQuest.getQuest().getName().equals(questHelperName))
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
				registerQuestToEventBus(questHelper);
				questHelper.startUp(config);
				backgroundHelpers.put(questHelperName, questHelper);
				if (questHelper.getCurrentStep() == null)
				{
					questHelper.shutDown();
					unregisterQuestFromEventBus(questHelper);
					backgroundHelpers.remove(questHelperName);
				}

			}
		});
	}

	private void registerQuestToEventBus(QuestHelper questHelper)
	{
		eventBus.register(questHelper);
	}

	private void unregisterQuestFromEventBus(QuestHelper questHelper)
	{
		eventBus.unregister(questHelper);
	}

	/**
	 * Shuts down a background quest.
	 *
	 * @param questHelper The background quest to be shut down.
	 */
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
		unregisterQuestFromEventBus(questHelper);
		backgroundHelpers.remove(questHelper.getQuest().getName());

	}

	/**
	 * Fetches all item requirements for quests based on the current configuration.
	 */
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

			List<QuestHelper> filteredQuests = QuestHelperQuest.getQuestHelpers(isDeveloperMode())
				.stream()
				.filter(pred)
				.filter(QuestDetails::isNotCompleted)
				.sorted(config.orderListBy())
				.collect(Collectors.toList());

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
			if (config.highlightItemsBackground()
				&& !(selectedQuest != null && selectedQuest.getQuest() == QuestHelperQuest.CHECK_ITEMS))
			{
				shutDownBackgroundQuest(backgroundHelpers.get(checkItemsName));
				startUpBackgroundQuest(checkItemsName);
			}
		});
	}

	/**
	 * Updates all items for the All Items helper.
	 */
	public void updateAllItemsHelper()
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		getAllItemRequirements();
		if (selectedQuest != null && selectedQuest.getQuest() == QuestHelperQuest.CHECK_ITEMS)
		{
			clientThread.invokeLater(() -> startUpQuest(QuestHelperQuest.CHECK_ITEMS.getQuestHelper(), false));
		}
	}

	/**
	 * Updates the background helper based on the shouldRun flag.
	 *
	 * @param shouldRun The flag indicating whether the background helper should run.
	 */
	public void updateAllItemsBackgroundHelper(String shouldRun)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		if (Objects.equals(shouldRun, "false"))
		{
			shutDownBackgroundQuest(backgroundHelpers.get(QuestHelperQuest.CHECK_ITEMS.getName()));
		}
		else
		{
			startUpBackgroundQuest(QuestHelperQuest.CHECK_ITEMS.getName());
		}
	}

	public void setupRequirements()
	{
		for (QuestHelperQuest questHelperQuest : QuestHelperQuest.values())
		{
			questHelperQuest.getQuestHelper().initializeRequirements();
		}
	}
}
