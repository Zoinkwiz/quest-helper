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

import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.AbstractRequirement;
import java.util.Locale;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;

/**
 * Requirement that checks if a {@link net.runelite.api.Quest} has a certain state.
 * Usually {@link QuestState#FINISHED}.
 */
@Getter
public class QuestRequirement extends AbstractRequirement
{
	private final QuestHelperQuest quest;
	private final QuestState requiredState;
	private String displayText = null;

	/**
	 * Check if a {@link net.runelite.api.Quest} meets the required {@link QuestState}
	 *
	 * @param quest the quest to check
	 * @param requiredState the required quest state
	 */
	public QuestRequirement(QuestHelperQuest quest, QuestState requiredState)
	{
		this.quest = quest;
		this.requiredState = requiredState;
	}

	/**
	 * Check if a {@link net.runelite.api.Quest} meets the required {@link QuestState}.
	 *
	 * @param quest the quest to check
	 * @param requiredState the required quest state
	 * @param displayText display text
	 */
	public QuestRequirement(QuestHelperQuest quest, QuestState requiredState, String displayText)
	{
		this(quest, requiredState);
		this.displayText = displayText;
	}

	@Override
	public boolean check(Client client)
	{
		QuestState state = quest.getState(client);
		if (requiredState == QuestState.IN_PROGRESS && state == QuestState.FINISHED)
		{
			return true;
		}
		return state == requiredState;
	}

	@Override
	public String getDisplayText()
	{
		if (displayText != null && !displayText.isEmpty())
		{
			return displayText;
		}
		String text = Character.toUpperCase(requiredState.name().charAt(0)) + requiredState.name().toLowerCase(Locale.ROOT).substring(1);
		return text.replaceAll("_", " ") + " " + quest.getName();
	}
}
