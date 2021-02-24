/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.WidgetTextRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class SirRenItchoodStep extends ConditionalStep
{
	private String answer = null;

	private String[] answers = {
		"NULL", "TIME", "FISH", "RAIN", "BITE", "MEAT", "LAST"
	};

	private final int VARBIT_FINISHED_ROOM = 663;
	private final int VARBIT_ANSWER = 666;

	private Requirement answerWidgetOpen;
	private DoorPuzzle enterDoorcode;
	private QuestStep talkToRen, openAnswerWidget, leaveRoom;
	private VarbitRequirement finishedRoomCondition;

	public SirRenItchoodStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper, step, requirements);

		talkToRen = step;
		addRenSteps();
	}

	@Override
	public void startUp()
	{
		super.startUp();
		int answerID = client.getVarbitValue(VARBIT_ANSWER);
		if (answerID == 0)
		{
			return;
		}
		String answer = answers[answerID];
		enterDoorcode.updateWord(answer);
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		int answerID = client.getVarbitValue(VARBIT_ANSWER);
		if (answerID == 0)
		{
			return;
		}
		String answer = answers[answerID];
		enterDoorcode.updateWord(answer);
	}

	private void addRenSteps()
	{
		finishedRoomCondition = new VarbitRequirement(VARBIT_FINISHED_ROOM, 1);
		openAnswerWidget = new ObjectStep(questHelper, 7323, "Open the door to be prompted to enter a code.");
		answerWidgetOpen = new WidgetTextRequirement(285, 55, "Combination Lock Door");
		enterDoorcode = new DoorPuzzle(questHelper, "NONE");
		leaveRoom = new ObjectStep(questHelper, 7323, "Leave through the door to enter the portal and continue.");

		addStep(finishedRoomCondition, leaveRoom);
		addStep(new Conditions(answerWidgetOpen), enterDoorcode);
		addStep(null, openAnswerWidget);
	}

	public List<QuestStep> getPanelSteps()
	{
		List<QuestStep> steps = new ArrayList<>();
		steps.add(talkToRen);
		steps.add(openAnswerWidget);
		steps.add(enterDoorcode);
		steps.add(leaveRoom);
		return steps;
	}
}
