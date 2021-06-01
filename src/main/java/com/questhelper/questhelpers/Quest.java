/*
 *  * Copyright (c) 2021, Senmori
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

package com.questhelper.questhelpers;

import com.questhelper.QuestHelperQuest;
import java.util.function.Predicate;

public interface Quest
{
	public static boolean showCompletedQuests(QuestHelper quest)
	{
		return quest.getConfig().showCompletedQuests() && quest.isCompleted() || !quest.isCompleted();
	}

	/**
	 * Describes the difficulty of a {@link QuestHelperQuest}
	 */
	public enum Difficulty implements Predicate<QuestHelper>
	{
		ALL,
		NOVICE,
		INTERMEDIATE,
		EXPERIENCED,
		MASTER,
		GRANDMASTER,
		MINIQUEST,
		ACHIEVEMENT_DIARY,
		GENERIC,
		;

		@Override
		public boolean test(QuestHelper quest) {
			return quest.getQuest().getDifficulty() == this || this == ALL;
		}
	}

	/**
	 * Describes if the quest is free-to-play (F2P), pay-to-play(P2P),
	 * or a miniquest.
	 */
	public enum Type implements Predicate<QuestHelper>
	{
		F2P,
		P2P,
		MINIQUEST,
		ACHIEVEMENT_DIARY,
		GENERIC,
		;

		@Override
		public boolean test(QuestHelper quest)
		{
			return quest.getQuest().getQuestType() == this;
		}
	}
}
