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
package com.questhelper.helpers.guides;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;

import java.util.List;

@AllArgsConstructor
@Getter
public class Unlock
{
	private final String id;
	private final String name;
	private final UnlockCategory category;
	private final String description;
	private final int iconSpriteId;
	private final List<QuestHelperQuest> prerequisiteQuests;

	/**
	 * Get the next incomplete quest in the prerequisite chain
	 */
	public QuestHelper getNextIncompleteQuestHelper(Client client)
	{
		if (client == null || prerequisiteQuests == null) return null;

		for (QuestHelperQuest quest : prerequisiteQuests)
		{
			if (quest.getState(client) != QuestState.FINISHED)
			{
				return quest.getQuestHelper();
			}
		}
		return null; // All complete
	}

	/**
	 * Get the number of completed prerequisites
	 */
	public int getCompletedCount(Client client)
	{
		if (client == null || prerequisiteQuests == null) return 0;

		int count = 0;
		for (QuestHelperQuest quest : prerequisiteQuests)
		{
			if (quest.getState(client) == QuestState.FINISHED)
			{
				count++;
			}
		}
		return count;
	}

	/**
	 * Check if the unlock is fully completed
	 */
	public boolean isCompleted(Client client)
	{
		return getNextIncompleteQuestHelper(client) == null;
	}

	/**
	 * Get total number of prerequisite quests
	 */
	public int getTotalCount()
	{
		return prerequisiteQuests == null ? 0 : prerequisiteQuests.size();
	}
}
