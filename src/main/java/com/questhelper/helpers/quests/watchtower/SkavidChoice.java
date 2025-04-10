/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.helpers.quests.watchtower;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.choice.DialogChoiceSteps;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class SkavidChoice extends NpcStep
{
	public SkavidChoice(QuestHelper questHelper)
	{
		super(questHelper, NpcID.MAD_SKAVID, new WorldPoint(2526, 9413, 0), "Talk to the mad skavid.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateCorrectChoice();
	}

	private void updateCorrectChoice()
	{
		Widget widget = client.getWidget(InterfaceID.ChatLeft.TEXT);
		if (widget == null)
		{
			return;
		}

		switch (widget.getText())
		{
			case "Bidith ig...":
				choices = new DialogChoiceSteps();
				addDialogStep("Cur.");
				break;
			case "Ar cur...":
				choices = new DialogChoiceSteps();
				addDialogStep("Gor.");
				break;
			case "Gor nod...":
				choices = new DialogChoiceSteps();
				addDialogStep("Tanath.");
				break;
			case "Cur tanath...":
				choices = new DialogChoiceSteps();
				addDialogStep("Bidith.");
				break;
		}
	}
}
