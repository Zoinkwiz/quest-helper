/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.enlightenedjourney;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BalloonFlightStep extends DetailedOwnerStep
{
	WidgetStep dropSand, burnLog, pullRope, pullRedRope, goStraight;

	NpcStep startFlight;

	// Shift is to get the 'section'
	HashMap<Integer, List<Integer>> sections;

	WidgetTextRequirement flying;

	public BalloonFlightStep(QuestHelper questHelper, String text, HashMap<Integer, List<Integer>> sections, ItemRequirement... itemRequirements)
	{
		super(questHelper, text, itemRequirements);
		this.sections = sections;
		flying = new WidgetTextRequirement(471, 1, "Balloon Controls");
	}

	@Override
	protected void setupSteps()
	{
		startFlight = new NpcStep(getQuestHelper(), NpcID.ZEP_PICCARD, new WorldPoint(2809, 3354, 0), "");
		dropSand = new WidgetStep(getQuestHelper(),  "Drop a sandbag.", 471, 2);
		burnLog = new WidgetStep(getQuestHelper(),  "Burn a log.", 471, 3);
		pullRope = new WidgetStep(getQuestHelper(),  "Pull the brown rope.", 471, 6);
		pullRedRope = new WidgetStep(getQuestHelper(),  "Pull the red rope.", 471, 9);
		goStraight = new WidgetStep(getQuestHelper(),  "Press relax.", 471, 4);
	}

	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		updateSteps();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		if (!flying.check(client))
		{
			startUpStep(startFlight);
			return;
		}

		int section = client.getVarbitValue(2884);
		int xPos = client.getVarbitValue(2882);
		int yPos = client.getVarbitValue(2883);

		if (sections.get(section) == null) return;
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
		return Arrays.asList(startFlight, dropSand, burnLog, pullRope, pullRedRope, goStraight);
	}
}
