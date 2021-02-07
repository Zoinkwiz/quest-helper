/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.enlightenedjourney;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class BalloonFlight1 extends DetailedOwnerStep
{
	WidgetStep dropSand, burnLog, pullRope, pullRedRope, goStraight;

//	HashMap<Integer, WidgetStep> actions = new HashMap<>();

	// Current position, next position
	ArrayList<Integer> section1;
	ArrayList<Integer> section2;
	ArrayList<Integer> section3;

	ArrayList<ArrayList<Integer>> sections;

	public BalloonFlight1(QuestHelper questHelper)
	{
		super(questHelper, "Navigate the balloon.");
	}

	@Override
	protected void setupSteps()
	{
		dropSand = new WidgetStep(getQuestHelper(),  "Drop a sandbag.", 471, 17);
		burnLog = new WidgetStep(getQuestHelper(),  "Burn a log.", 471, 24);
		pullRope = new WidgetStep(getQuestHelper(),  "Pull the brown rope.", 471, 6);
		pullRedRope = new WidgetStep(getQuestHelper(),  "Pull the red rope.", 471, 9);
		goStraight = new WidgetStep(getQuestHelper(),  "Press relax.", 471, 27);

		section1 = new ArrayList<>();
		section1.add(5);
		// Drop sandbag
		section1.add(7);
		// Burn log
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		section1.add(8);
		// Drop down 2
		section1.add(6);
		section1.add(6);
		section1.add(6);
		// Drop down 1
		section1.add(5);
		section1.add(5);
		section1.add(5);
		section1.add(5);
		section1.add(5);
		// Off screen
		section1.add(5);

		section2 = new ArrayList<>();
		// Not gone to
		section2.add(5);

		section2.add(5);
		// Burn log
		section2.add(6);
		section2.add(6);
		// Burn log
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		section2.add(7);
		// Burn log
		section2.add(8);
		section2.add(8);
		section2.add(8);
		section2.add(8);
		section2.add(8);
		// Off screen
		section2.add(8);

		section3 = new ArrayList<>();
		// Not gone to
		section3.add(8);

		section3.add(8);
		section3.add(8);
		section3.add(8);
		section3.add(8);
		section3.add(8);
		section3.add(8);
		section3.add(8);
		section3.add(8);
		section3.add(6);
		section3.add(5);
		section3.add(5);
		section3.add(5);
		section3.add(5);
		section3.add(6);
		section3.add(6);
		section3.add(6);
		section3.add(6);
		section3.add(6);
		section3.add(5);
		section3.add(5);

		sections = new ArrayList<>(Arrays.asList(section1, section2, section3));
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateSteps();
	}

	protected void updateSteps()
	{
		int section = client.getVarbitValue(2884) - 1;
		int xPos = client.getVarbitValue(2882);
		int yPos = client.getVarbitValue(2883);

		// If we've gone to next section before updating the pos, return
		if (sections.get(section).size() <= xPos + 1)
		{
			return;
		}
		int diffBetweenCurrentAndNextPos = sections.get(section).get(xPos + 1) - yPos;

		if (diffBetweenCurrentAndNextPos == 0)
		{
			startUpStep(goStraight);
		}
		else if (diffBetweenCurrentAndNextPos == 1)
		{
			startUpStep(burnLog);
		}
		else if (diffBetweenCurrentAndNextPos > 1)
		{
			startUpStep(dropSand);
		}
		else if (diffBetweenCurrentAndNextPos == -1)
		{
			startUpStep(pullRope);
		}
		else
		{
			startUpStep(pullRedRope);
		}
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(dropSand, burnLog, pullRope, pullRedRope, goStraight);
	}
}
