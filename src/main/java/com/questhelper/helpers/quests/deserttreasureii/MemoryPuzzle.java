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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;

public class MemoryPuzzle extends DetailedOwnerStep
{
	// Second time:
	// 15212 1->3
	// 15213 0->1
	// 15214 0->0
	// 15215 3->2

	// 0 = SW = 1903, 6431, 0
	// 1 = NW = 1900, 6442, 0
	// 2 = NE = 1909, 6441, 0
	// 3 = SE = 1914, 6434, 0

	// Touched 1, 15217 = 1
	// ++

	DetailedQuestStep neStep, nwStep, seStep, swStep;
	QuestStep[] steps;
	public MemoryPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "Solve the memory puzzle. Make sure to go into air bubbles whenever your air meter is low.");
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	protected void setupSteps()
	{
		swStep = new ObjectStep(getQuestHelper(), ObjectID.ABYSSAL_GROWTH,
			new WorldPoint(1903, 6431, 0), "Touch the south-western growth.");
		nwStep = new ObjectStep(getQuestHelper(), ObjectID.ABYSSAL_GROWTH,
			new WorldPoint(1900, 6442, 0), "Touch the north-western growth.");
		neStep = new ObjectStep(getQuestHelper(), ObjectID.ABYSSAL_GROWTH,
		new WorldPoint(1909, 6441, 0), "Touch the north-eastern growth.");
		seStep = new ObjectStep(getQuestHelper(), ObjectID.ABYSSAL_GROWTH,
			new WorldPoint(1914, 6434, 0), "Touch the south-eastern growth.");

		steps = new QuestStep[4];
		// SW
		steps[0] = swStep;
		// NW
		steps[1] = nwStep;
		// NE
		steps[2] = neStep;
		// SE
		steps[3] = seStep;
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
	}

	protected void updateSteps()
	{
		// 0-1-2-3-4
		int currentStep = client.getVarbitValue(15217);
		int thingToPress = client.getVarbitValue(15212 + currentStep);
		startUpStep(steps[thingToPress]);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(seStep, swStep, neStep, nwStep);
	}
}
