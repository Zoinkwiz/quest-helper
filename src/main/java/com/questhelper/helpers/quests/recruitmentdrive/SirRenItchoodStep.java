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
package com.questhelper.helpers.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class SirRenItchoodStep extends ConditionalStep
{
	private final String[] answers = {
		"NULL", "TIME", "FISH", "RAIN", "BITE", "MEAT", "LAST"
	};
	private final QuestStep talkToRen;

	private DoorPuzzle enterDoorCode;
	private PuzzleWrapperStep pwEnterDoorCode;
	private QuestStep tryOpenDoor;
	private QuestStep leaveRoom;

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
		int answerID = client.getVarbitValue(VarbitID.RD_TEMPLOCK_1);
		if (answerID == 0)
		{
			return;
		}
		String answer = answers[answerID];
		enterDoorCode.updateWord(answer);
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);

		if (varbitChanged.getVarbitId() != VarbitID.RD_TEMPLOCK_1)
		{
			return;
		}
		var answerID = varbitChanged.getValue();
		if (answerID == 0)
		{
			return;
		}
		var answer = answers[answerID];
		enterDoorCode.updateWord(answer);
	}

	private void addRenSteps()
	{
		var finishedRoomCondition = new VarbitRequirement(VarbitID.RD_ROOM5_COMPLETE, 1);
		var answerWidgetOpen = new WidgetTextRequirement(InterfaceID.RdCombolock.RDCOMBOLOCK, "Combination Lock Door");
		tryOpenDoor = new ObjectStep(questHelper, ObjectID.RD_ROOM5_EXITDOOR, "Open the door to be prompted to enter a code.");
		enterDoorCode = new DoorPuzzle(questHelper, "NONE");
		pwEnterDoorCode = enterDoorCode.puzzleWrapStepWithDefaultText("Solve the door combination lock using the hints from Sir Ren Itchood.");
		leaveRoom = new ObjectStep(questHelper, ObjectID.RD_ROOM5_EXITDOOR, "Leave through the door to enter the portal and continue.");

		addStep(finishedRoomCondition, leaveRoom);
		addStep(answerWidgetOpen, pwEnterDoorCode);
		addStep(null, tryOpenDoor);
	}

	public List<QuestStep> getPanelSteps()
	{
		return List.of(
			talkToRen,
			tryOpenDoor,
			pwEnterDoorCode,
			leaveRoom
		);
	}
}
