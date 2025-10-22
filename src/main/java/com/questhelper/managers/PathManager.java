/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.managers;

import com.questhelper.QuestHelperConfig;
import com.questhelper.helpers.guides.Unlock;
import com.questhelper.helpers.guides.UnlockRegistry;
import com.questhelper.questhelpers.QuestHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages active path state and progression
 * Handles path activation, pausing, resuming, and automatic quest progression
 */
@Singleton
@Slf4j
public class PathManager
{
	@Inject
	private Client client;
	
	@Inject
	private ConfigManager configManager;
	
	@Inject
	private QuestManager questManager;
	
	// Path state constants
	public static final String PATH_STATE_ACTIVE = "active";
	public static final String PATH_STATE_PAUSED = "paused";
	public static final String PATH_STATE_INACTIVE = "inactive";
	
	// Config keys
	private static final String ACTIVE_PATH_ID_KEY = "activePathId";
	private static final String PATH_STATE_KEY = "pathState";
	
	@Getter
	private Unlock activePath = null;
	
	@Getter
	private String pathState = PATH_STATE_INACTIVE;
	
	/**
	 * Set a path as active
	 * @param pathId The unlock ID to set as active
	 * @return true if successfully set, false if path not found
	 */
	public boolean setActivePath(String pathId)
	{
		Unlock unlock = UnlockRegistry.getUnlockById(pathId);
		if (unlock == null)
		{
			log.warn("Attempted to set unknown path ID: {}", pathId);
			return false;
		}
		
		// Stop any existing active path
		stopActivePath();
		
		// Set new active path
		activePath = unlock;
		pathState = PATH_STATE_ACTIVE;
		
		// Save to config
		savePathState();
		
		// Start the first incomplete quest in the path
		progressPath();
		
		log.info("Set active path: {}", unlock.getName());
		return true;
	}
	
	/**
	 * Pause the current active path
	 */
	public void pauseActivePath()
	{
		if (activePath == null)
		{
			return;
		}
		
		pathState = PATH_STATE_PAUSED;
		savePathState();
		
		log.info("Paused active path: {}", activePath.getName());
	}
	
	/**
	 * Resume a paused path
	 */
	public void resumeActivePath()
	{
		if (activePath == null || !PATH_STATE_PAUSED.equals(pathState))
		{
			return;
		}
		
		pathState = PATH_STATE_ACTIVE;
		savePathState();
		
		// Start the next quest in the path
		progressPath();
		
		log.info("Resumed active path: {}", activePath.getName());
	}
	
	/**
	 * Stop and clear the active path
	 */
	public void stopActivePath()
	{
		if (activePath == null)
		{
			return;
		}
		
		String pathName = activePath.getName();
		activePath = null;
		pathState = PATH_STATE_INACTIVE;
		
		// Clear from config
		configManager.unsetRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ACTIVE_PATH_ID_KEY);
		configManager.unsetRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, PATH_STATE_KEY);
		
		log.info("Stopped active path: {}", pathName);
	}
	
	/**
	 * Progress to the next quest in the active path
	 * Called when a quest completes
	 */
	public boolean progressPath()
	{
		if (activePath == null || !PATH_STATE_ACTIVE.equals(pathState))
		{
			return false;
		}
		
		QuestHelper nextQuest = activePath.getNextIncompleteQuestHelper(client);
		if (nextQuest == null)
		{
			// Path is complete
			log.info("Active path completed: {}", activePath.getName());
			stopActivePath();
			return false;
		}
		
		// Start the next quest
		questManager.startUpQuest(nextQuest, true);
		log.info("Progressed active path to: {}", nextQuest.getQuest().getName());
		return true;
	}
	
	/**
	 * Check if a quest is part of the active path
	 * @param questHelper The quest helper to check
	 * @return true if the quest is in the active path
	 */
	public boolean isQuestInActivePath(QuestHelper questHelper)
	{
		if (activePath == null || !PATH_STATE_ACTIVE.equals(pathState))
		{
			return false;
		}
		
		return activePath.getPrerequisiteQuests().stream()
			.anyMatch(quest -> quest.getQuestHelper() == questHelper);
	}
	
	/**
	 * Check if a path is currently active
	 */
	public boolean isPathActive()
	{
		return activePath != null && PATH_STATE_ACTIVE.equals(pathState);
	}
	
	/**
	 * Check if a path is currently paused
	 */
	public boolean isPathPaused()
	{
		return activePath != null && PATH_STATE_PAUSED.equals(pathState);
	}
	
	/**
	 * Check if any path is active or paused
	 */
	public boolean hasActivePath()
	{
		return activePath != null && !PATH_STATE_INACTIVE.equals(pathState);
	}
	
	/**
	 * Load path state from config
	 * Called on plugin startup or login
	 */
	public void loadPathState()
	{
		String savedPathId = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ACTIVE_PATH_ID_KEY);
		String savedState = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, PATH_STATE_KEY);
		
		// Debug: Log what's being loaded
		System.out.println("Loading path state - ID: " + savedPathId + ", State: " + savedState);
		
		if (savedPathId == null || savedState == null)
		{
			activePath = null;
			pathState = PATH_STATE_INACTIVE;
			return;
		}
		
		Unlock unlock = UnlockRegistry.getUnlockById(savedPathId);
		if (unlock == null)
		{
			log.warn("Saved path ID not found: {}", savedPathId);
			activePath = null;
			pathState = PATH_STATE_INACTIVE;
			return;
		}
		
		activePath = unlock;
		pathState = savedState;
		
		log.info("Loaded path state: {} - {}", unlock.getName(), pathState);
		
		// If path was active, start the next quest
		if (PATH_STATE_ACTIVE.equals(pathState))
		{
			progressPath();
		}
	}
	
	/**
	 * Save current path state to config
	 */
	private void savePathState()
	{
		if (activePath != null)
		{
			configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, ACTIVE_PATH_ID_KEY, activePath.getId());
			configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, PATH_STATE_KEY, pathState);
		}
	}
	
	/**
	 * Clear the path state (for debugging/reset)
	 */
	public void clearPathState()
	{
		activePath = null;
		pathState = PATH_STATE_INACTIVE;
		savePathState();
		log.info("Path state cleared");
	}
}
