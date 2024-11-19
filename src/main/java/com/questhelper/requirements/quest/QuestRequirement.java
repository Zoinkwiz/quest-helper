/*
 *
 *  * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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

package com.questhelper.requirements.quest;

import com.questhelper.managers.ActiveRequirementsManager;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.AbstractRequirement;
import java.util.Locale;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.annotation.Nonnull;

/**
 * Requirement that checks if a {@link net.runelite.api.Quest} has a certain state.
 * Usually {@link QuestState#FINISHED}.
 */
@Getter
public class QuestRequirement extends AbstractRequirement
{
	private final QuestHelperQuest quest;
	private final QuestState requiredState;
	private final Integer minimumVarValue;
	private String displayText = null;

	/**
	 * Check if a {@link net.runelite.api.Quest} meets the required {@link QuestState}
	 *
	 * @param quest         the quest to check
	 * @param requiredState the required quest state
	 */
	public QuestRequirement(QuestHelperQuest quest, QuestState requiredState)
	{
		assert(quest != null);
		assert(requiredState != null);
		this.quest = quest;
		this.requiredState = requiredState;
		this.minimumVarValue = null;
		shouldCountForFilter = true;
	}

	/**
	 * Check if a {@link net.runelite.api.Quest} meets the required {@link QuestState}.
	 *
	 * @param quest         the quest to check
	 * @param requiredState the required quest state
	 * @param displayText   display text
	 */
	public QuestRequirement(QuestHelperQuest quest, QuestState requiredState, String displayText)
	{
		this(quest, requiredState);
		this.displayText = displayText;
	}

	/**
	 * Check if a {@link net.runelite.api.Quest} is past the minimum var value
	 *
	 * @param quest         the quest to check
	 * @param minimumVarValue the required quest state
	 */
	public QuestRequirement(QuestHelperQuest quest, int minimumVarValue)
	{
		assert(quest != null);
		this.quest = quest;
		this.requiredState = null;
		this.minimumVarValue = minimumVarValue;
		shouldCountForFilter = true;
	}

	/**
	 * Check if a {@link net.runelite.api.Quest} is past the minimum var value
	 *
	 * @param quest         the quest to check
	 * @param minimumVarValue the required quest state
	 * @param displayText   display text
	 */
	public QuestRequirement(QuestHelperQuest quest, int minimumVarValue, String displayText)
	{
		this(quest, minimumVarValue);
		this.displayText = displayText;
	}

	@Override
	public void register(Client client, EventBus eventBus, ActiveRequirementsManager activeRequirementsManager)
	{
		super.register(client, eventBus, activeRequirementsManager);
		checkQuestState();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		checkQuestState();
	}

	private void checkQuestState()
	{
		if (minimumVarValue != null)
		{
			setState(quest.getVar(client) >= minimumVarValue);
		}

		QuestState questState = quest.getState(client);
		if (requiredState == QuestState.IN_PROGRESS && questState == QuestState.FINISHED)
		{
			setState(true);
		}
		setState(questState == requiredState);
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		if (displayText != null && !displayText.isEmpty())
		{
			return displayText;
		}
		String text = Character.toUpperCase(requiredState.name().charAt(0)) + requiredState.name().toLowerCase(Locale.ROOT).substring(1);
		if (requiredState == QuestState.IN_PROGRESS)
		{
			text = "Started ";
		}
		return text.replaceAll("_", " ") + " " + quest.getName();
	}
}
