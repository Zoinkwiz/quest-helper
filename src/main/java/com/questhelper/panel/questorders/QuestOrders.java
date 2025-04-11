/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.panel.questorders;

import com.questhelper.questhelpers.QuestHelper;

import java.util.Comparator;
import java.util.regex.Pattern;

public class QuestOrders
{
	// Test for 'The', 'A', 'An' at the start of a string followed by a word boundary (space/tab); this ignores case sensitivity
	private static final Pattern QUEST_NAME_PATTERN = Pattern.compile("(?i)(a\\b)|(the\\b)|(an\\b)", Pattern.CASE_INSENSITIVE);

	public static String normalizeQuestName(String questName)
	{
		return QUEST_NAME_PATTERN.matcher(questName).replaceAll("").trim();
	}

	public static Comparator<QuestHelper> sortOptimalOrder()
	{
		return Comparator.comparing(q -> OptimalQuestGuide.getQuestList().indexOf(q.getQuest()));
	}

	public static Comparator<QuestHelper> sortOptimalIronmanOrder()
	{
		return Comparator.comparing(q -> IronmanOptimalQuestGuide.getQuestList().indexOf(q.getQuest()));
	}

	public static Comparator<QuestHelper> sortAToZ()
	{
		return Comparator.comparing(q -> normalizeQuestName(q.getQuest().getName()));
	}

	public static Comparator<QuestHelper> sortZToA()
	{
		return Comparator.comparing(q -> normalizeQuestName(q.getQuest().getName()), Comparator.reverseOrder());
	}

	public static Comparator<QuestHelper> sortByRelease()
	{
		return Comparator.comparing(q -> ReleaseDate.getQuestList().indexOf(q.getQuest()));
	}

	public static Comparator<QuestHelper> sortByQuestPointRewardAscending()
	{
		return Comparator.comparing(q -> q.getQuestPointReward() != null ? q.getQuestPointReward().getPoints() : 0);
	}

	public static Comparator<QuestHelper> sortByQuestPointRewardDescending()
	{
		return Comparator.comparing(q -> q.getQuestPointReward() != null ? q.getQuestPointReward().getPoints() : 0, Comparator.reverseOrder());
	}
}
