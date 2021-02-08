/*
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
import com.questhelper.requirements.util.RequirementBuilder;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import static net.runelite.api.widgets.WidgetID.DIALOG_NPC_GROUP_ID;

public class MsHynnAnswerDialogQuizStep extends ConditionalStep
{
	private QuestStep dialogQuizStep, leaveRoom, talkToMsHynnTerprett;
	private VarbitRequirement finishedRoomCondition;

	private boolean dialogEntry = false;

	private final int VARBIT_FINISHED_ROOM = 665;

	private final String DIALOG_QUIZ = "Here is my riddle:<br>I estimate there to be 1 million inhabitants in the world<br>of Gielinor, creatures and people both.";
	private final String DIALOG_QUIZ2 = "What would be the number you would get if you<br>multiply the number of fingers on everythings left hand,<br>to the nearest million?";

	private static final String DIALOG_QUIZ3 = "My husband is four times older than my daughter.<br>In twenty years time, he will be twice as old as my<br>daughter.";

	public MsHynnAnswerDialogQuizStep(QuestHelper questHelper, QuestStep step, Requirement... requirements)
	{
		super(questHelper, step, requirements);
//Talk to Ms Hynn Terprett and answer the riddle.
		talkToMsHynnTerprett = step;
		step.addDialogSteps(
			"The wolves.",
			"Bucket A (32 degrees)",
			"The number of false statements here is three.",
			"Zero.");
		AddSteps();
	}

	private void AddSteps()
	{
		finishedRoomCondition = new VarbitRequirement(VARBIT_FINISHED_ROOM, 1);
		leaveRoom = new ObjectStep(questHelper, 7354, "Leaves through the door to enter the portal and continue.");

		addStep(finishedRoomCondition, leaveRoom);
		AddDialogQuizStep();
	}

	public List<QuestStep> getPanelSteps()
	{
		List<QuestStep> steps = new ArrayList<>();
		if (dialogEntry)
		{
			steps.add(dialogQuizStep);
		}
		steps.add(leaveRoom);
		return steps;
	}

	private void AddDialogQuizStep()
	{
		Requirement step = RequirementBuilder.builder().check(client -> dialogEntry).build();
		dialogQuizStep = new DetailedQuestStep(questHelper, "Enter the answer 10 to the dialog when prompted.");
		talkToMsHynnTerprett.addSubSteps(dialogQuizStep);
		addStep(step, dialogQuizStep);
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void onWidgetLoaded(WidgetLoaded event)
	{
		int groupId = event.getGroupId();
		if (groupId == DIALOG_NPC_GROUP_ID)
		{
			clientThread.invokeLater(this::readWidget);
		}

		super.onWidgetLoaded(event);
	}

	private void readWidget()
	{
		Widget widget = client.getWidget(WidgetID.DIALOG_NPC_GROUP_ID, 4);
		if (widget == null)
		{
			return;
		}
		String characterText = widget.getText();

		if (DIALOG_QUIZ.equals(characterText) ||
			DIALOG_QUIZ2.equals(characterText))
		{
			dialogEntry = true;
			talkToMsHynnTerprett.setText("Talk to Ms Hynn Terprett and answer the riddle with the code 0");
			dialogQuizStep.setText("Enter the answer \n \n 0.");
		}
		else if (DIALOG_QUIZ3.equals(characterText))
		{
			dialogEntry = true;
			talkToMsHynnTerprett.setText("Talk to Ms Hynn Terprett and answer the riddle with the code 10");
			dialogQuizStep.setText("Enter the answer \n \n 10.");
		}
	}
}
