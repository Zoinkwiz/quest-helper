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

import com.questhelper.requirements.AbstractRequirement;
import com.questhelper.requirements.util.Operation;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;

/**
 * Requirement that checks if a player has a required number of quest points.
 */
public class QuestPointRequirement extends AbstractRequirement
{
	@Getter
	private int requiredQuestPoints;
	private Operation operation;

	/**
	 * Checks if a player has a required number of quest points.
	 * By default, it uses {@link Operation#GREATER_EQUAL}.
	 *
	 * @param requiredQuestPoints the required number of quest points
	 */
	public QuestPointRequirement(int requiredQuestPoints)
	{
		this.requiredQuestPoints = requiredQuestPoints;
		this.operation = Operation.GREATER_EQUAL;
		shouldCountForFilter = true;
	}

	/**
	 * Checks if a player has a required number of quest points.
	 *
	 * @param requiredQuestPoints the required number of quest points
	 * @param operation           the {@link Operation} to use.
	 */
	public QuestPointRequirement(int requiredQuestPoints, Operation operation)
	{
		this.requiredQuestPoints = requiredQuestPoints;
		this.operation = operation;
	}

	@Override
	public boolean check(Client client)
	{
		return operation.check(client.getVar(VarPlayer.QUEST_POINTS), requiredQuestPoints);
	}

	@Override
	public String getDisplayText()
	{
		return getRequiredQuestPoints() + " Quest Points";
	}
}
