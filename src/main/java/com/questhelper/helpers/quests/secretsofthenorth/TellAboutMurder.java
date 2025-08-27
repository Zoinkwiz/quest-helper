/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.secretsofthenorth;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.choice.DialogChoiceSteps;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

public class TellAboutMurder extends NpcStep
{

	public TellAboutMurder(QuestHelper questHelper)
	{
		super(questHelper, NpcID.SOTN_GUARD_CARNILLEAN_UPSTAIRS, new WorldPoint(2571, 3267, 1),
			"Tell the guard about what you found.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateCorrectChoice();
	}

	private void updateCorrectChoice()
	{
		boolean saidAboutWindow = client.getVarbitValue(VarbitID.SOTN_EXPLAINED_WINDOW) == 1;
		boolean saidAboutCeril = client.getVarbitValue(VarbitID.SOTN_EXPLAINED_BODY) == 1;
		boolean saidAboutWall = client.getVarbitValue(VarbitID.SOTN_EXPLAINED_CHEST) == 1;

		choices = new DialogChoiceSteps();

		if (!saidAboutWindow)
		{
			addDialogStep("The window was broken from the outside.");
		}
		if (!saidAboutCeril)
		{
			addDialogStep("Ceril was stabbed from behind.");
		}
		if (!saidAboutWall)
		{
			addDialogStep("A hidden room above us was broken into.");
		}
	}
}
