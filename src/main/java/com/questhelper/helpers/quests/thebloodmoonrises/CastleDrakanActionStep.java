/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;

/// ConditionalStep that guides the user to a specific room in Castle Drakan, with a specific action step once they arrive
class CastleDrakanActionStep extends ConditionalStep
{
	CastleDrakanActionStep(QuestHelper questHelper, CastleDrakanRoomNetwork network,
	                       CastleDrakanRoomNetwork.RoomKey destination, QuestStep action, String routeText)
	{
		this(questHelper, network, destination, network.inRoom(destination), action, routeText);
	}

	CastleDrakanActionStep(QuestHelper questHelper, CastleDrakanRoomNetwork network,
	                       CastleDrakanRoomNetwork.RoomKey destination, Requirement actionLocation, QuestStep action,
	                       String routeText)
	{
		this(questHelper, action, new CastleDrakanRoomStep(questHelper, network, destination, routeText),
			actionLocation);
	}

	private CastleDrakanActionStep(QuestHelper questHelper, QuestStep action,
	                               CastleDrakanRoomStep route, Requirement actionLocation)
	{
		super(questHelper, route);
		setShouldPassthroughText(true);
		if (action instanceof ConditionalStep && !(action instanceof PuzzleWrapperStep))
		{
			((ConditionalStep) action).setShouldPassthroughText(true);
		}
		addStep(actionLocation, action);
	}
}
