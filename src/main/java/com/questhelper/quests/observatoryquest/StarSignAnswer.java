/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.observatoryquest;

import com.questhelper.QuestVarPlayer;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.NpcStep;
import java.util.HashMap;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class StarSignAnswer extends NpcStep
{
	HashMap<Integer, String> starSign = new HashMap<>();
	int currentValue = -1;

	public StarSignAnswer(QuestHelper questHelper)
	{
		super(questHelper, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2440, 3159,
			1), "Tell the professor the constellation you observed.");
		starSign.put(0, "Aquarius");
		starSign.put(1, "Capricorn");
		starSign.put(2, "Sagittarius");
		starSign.put(3, "Scorpio");
		starSign.put(4, "Libra");
		starSign.put(5, "Virgo");
		starSign.put(6, "Leo");
		starSign.put(7, "Cancer");
		starSign.put(8, "Gemini");
		starSign.put(9, "Taurus");
		starSign.put(10, "Aries");
		starSign.put(11, "Pisces");
	}

	@Override
	public void startUp()
	{
		super.startUp();
		updateCorrectChoice();
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateCorrectChoice();
	}

	private void updateCorrectChoice()
	{
		addDialogSteps("Talk about the Observatory quest.");
		int currentStep = client.getVarpValue(QuestVarPlayer.QUEST_OBSERVATORY_QUEST.getId());
		if (currentStep < 2)
		{
			return;
		}

		int newValue = client.getVarbitValue(3828);
		if (currentValue != newValue)
		{
			currentValue = newValue;
			String constellation = starSign.get(newValue);
			setText("Tell the professor you observed " + constellation + ".");
			addDialogStep(constellation);
		}

	}
}
