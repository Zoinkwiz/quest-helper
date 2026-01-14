/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz/>
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
import com.questhelper.steps.NpcStep;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

public class MsHynnAnswerDialogQuizStep extends NpcStep
{
	String[] answers = {
		"unknown",
		"10",
		"three false statements",
		"the wolves",
		"bucket A",
		"zero"
	};

	public MsHynnAnswerDialogQuizStep(QuestHelper questHelper)
	{
		super(questHelper, NpcID.RD_OBSERVER_ROOM_7, "Talk to Ms Hynn Terprett and answer the riddle.");

		this.addDialogSteps(
			"The wolves.",
			"Bucket A (32 degrees)",
			"The number of false statements here is three.",
			"Zero."
		);
	}

	private void updateAnswer(int answerID)
	{
		if (answerID == 0 || answerID > answers.length)
		{
			this.setText("Talk to Ms Hynn Terprett and answer the riddle.");
			return;
		}
		var answer = "The answer is " + answers[answerID] + ".";
		this.setText("Talk to Ms Hynn Terprett and answer the riddle. " + answer);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		this.updateAnswer(client.getVarbitValue(VarbitID.RD_TEMPLOCK_2));
	}

	@Override
	public void onVarbitChanged(VarbitChanged event)
	{
		super.onVarbitChanged(event);

		if (event.getVarbitId() != VarbitID.RD_TEMPLOCK_2)
		{
			return;
		}

		this.updateAnswer(event.getValue());
	}
}
