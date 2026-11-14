/*
 * Copyright (c) 2026, phillocity <https://github.com/phillocity>
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
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.questinfo.QuestHelperQuest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

/**
 * Quests the player has lined up to do, in the order they were added.
 * Changed from the panel (Swing thread) and read when building the bank tab (client thread), hence the syncing.
 */
@Singleton
@Slf4j
public class QuestQueueManager
{
	static final String CONFIG_KEY = "questQueue";

	// Generic and skill helpers never get completed, so they'd sit in the queue forever
	private static final Set<QuestDetails.Type> QUEUEABLE_TYPES = EnumSet.of(
		QuestDetails.Type.F2P,
		QuestDetails.Type.P2P,
		QuestDetails.Type.MINIQUEST,
		QuestDetails.Type.ACHIEVEMENT_DIARY,
		QuestDetails.Type.PLAYER_QUEST
	);

	private final ConfigManager configManager;

	private final LinkedList<QuestHelperQuest> queue = new LinkedList<>();

	private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

	@Inject
	public QuestQueueManager(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	public static boolean canQueue(QuestHelperQuest quest)
	{
		return QUEUEABLE_TYPES.contains(quest.getQuestType());
	}

	public void addListener(Runnable listener)
	{
		listeners.add(listener);
	}

	public void removeListener(Runnable listener)
	{
		listeners.remove(listener);
	}

	public synchronized List<QuestHelperQuest> getQueue()
	{
		return new ArrayList<>(queue);
	}

	public synchronized boolean isEmpty()
	{
		return queue.isEmpty();
	}

	public synchronized int size()
	{
		return queue.size();
	}

	public void add(QuestHelperQuest quest)
	{
		synchronized (this)
		{
			if (queue.contains(quest))
			{
				return;
			}
			queue.addLast(quest);
		}
		onChanged();
	}

	public void remove(QuestHelperQuest quest)
	{
		removeIf(quest::equals);
	}

	public void removeIf(Predicate<QuestHelperQuest> predicate)
	{
		boolean changed;
		synchronized (this)
		{
			changed = queue.removeIf(predicate);
		}
		if (changed)
		{
			onChanged();
		}
	}

	public void moveUp(QuestHelperQuest quest)
	{
		move(quest, -1);
	}

	public void moveDown(QuestHelperQuest quest)
	{
		move(quest, 1);
	}

	private void move(QuestHelperQuest quest, int offset)
	{
		synchronized (this)
		{
			int index = queue.indexOf(quest);
			int newIndex = index + offset;
			if (index == -1 || newIndex < 0 || newIndex >= queue.size())
			{
				return;
			}
			queue.remove(index);
			queue.add(newIndex, quest);
		}
		onChanged();
	}

	public void clear()
	{
		synchronized (this)
		{
			if (queue.isEmpty())
			{
				return;
			}
			queue.clear();
		}
		onChanged();
	}

	public void load()
	{
		List<QuestHelperQuest> loaded = new ArrayList<>();
		String saved = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONFIG_KEY);
		if (saved != null && !saved.isEmpty())
		{
			for (String name : saved.split(","))
			{
				try
				{
					QuestHelperQuest quest = QuestHelperQuest.valueOf(name.trim());
					if (!loaded.contains(quest))
					{
						loaded.add(quest);
					}
				}
				catch (IllegalArgumentException e)
				{
					// quest got renamed or removed since this was saved
					log.debug("Ignoring unknown quest '{}' in saved quest queue", name);
				}
			}
		}

		synchronized (this)
		{
			queue.clear();
			queue.addAll(loaded);
		}
		notifyListeners();
	}

	// Used on logout. Leaves the saved queue alone so it's there next login.
	public void unload()
	{
		synchronized (this)
		{
			queue.clear();
		}
		notifyListeners();
	}

	// Saving is kept out of the synchronized blocks, as ConfigManager fires ConfigChanged on this thread
	private void onChanged()
	{
		save();
		notifyListeners();
	}

	private void save()
	{
		List<QuestHelperQuest> quests = getQueue();
		if (quests.isEmpty())
		{
			configManager.unsetRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONFIG_KEY);
			return;
		}
		String value = quests.stream().map(Enum::name).collect(Collectors.joining(","));
		configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_HELPER_GROUP, CONFIG_KEY, value);
	}

	private void notifyListeners()
	{
		for (Runnable listener : listeners)
		{
			try
			{
				listener.run();
			}
			catch (Exception e)
			{
				log.warn("Quest queue listener failed", e);
			}
		}
	}
}
