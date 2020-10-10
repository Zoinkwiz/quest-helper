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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.choice.DialogChoiceSteps;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class AskAboutFishCake extends NpcStep
{
	public AskAboutFishCake(QuestHelper questHelper)
	{
		super(questHelper, NpcID.COOK_4626, new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook and ask him all the options about Pirate Pete.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateCorrectChoice();
	}

	private void updateCorrectChoice()
	{
		boolean askedAboutKelp = client.getVarbitValue(1873) == 1; // And 1874 = 1
		boolean askedAboutCrab = client.getVarbitValue(1874) == 1;
		boolean askedAboutBread = client.getVarbitValue(1876) == 1;
		boolean askedAboutCod = client.getVarbitValue(1877) == 1;

		choices = new DialogChoiceSteps();
		addDialogStep("Protecting the Pirate");
		if (!askedAboutCod)
		{
			addDialogStep("Where do I get Ground Cod?");
		}
		if (!askedAboutKelp)
		{
			addDialogStep("Where do I get Ground Kelp?");
		}
		if (!askedAboutCrab)
		{
			addDialogStep("Where do I get Ground Giant Crab Meat?");
		}
		if (!askedAboutBread)
		{
			addDialogStep("Where do I get Breadcrumbs?");
		}
	}
}
