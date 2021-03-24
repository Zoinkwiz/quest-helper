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
package com.questhelper.quests.hazeelcult;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class HazeelValves extends DetailedOwnerStep
{

	DetailedQuestStep turnValve1, turnValve2, turnValve3, turnValve4, turnValve5;

	private Zone valve1, valve2, valve3, valve4, valve5;

	private ZoneRequirement atValve1, atValve2, atValve3, atValve4, atValve5;

	private boolean solved1, solved2, solved3, solved4, solved5;

	public ManualRequirement solved;

	public HazeelValves(QuestHelper questHelper)
	{
		super(questHelper, "Turn the valves near the cave to direct the underground water.");
	}

	@Override
	protected void updateSteps()
	{
		if (!solved1)
		{
			startUpStep(turnValve1);
		}
		else if (!solved2)
		{
			startUpStep(turnValve2);
		}
		else if (!solved3)
		{
			startUpStep(turnValve3);
		}
		else if (!solved4)
		{
			startUpStep(turnValve4);
		}
		else if (!solved5)
		{
			startUpStep(turnValve5);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget widgetText = client.getWidget(229, 1);
		if (widgetText == null)
		{
			return;
		}

		String text = widgetText.getText();

		boolean turnedLeft = text.contains("left");

		if (!turnedLeft && !text.contains("right"))
		{
			return;
		}

		if (atValve1.check(client))
		{
			solved1 = !turnedLeft;
		}
		else if (atValve2.check(client))
		{
			solved2 = !turnedLeft;
		}
		else if (atValve3.check(client))
		{
			solved3 = turnedLeft;
		}
		else if (atValve4.check(client))
		{
			solved4 = !turnedLeft;
		}
		else if (atValve5.check(client))
		{
			solved5 = !turnedLeft;
		}

		solved.setShouldPass(solved1 && solved2 && solved3 && solved4 && solved5);

		updateSteps();
	}

	protected void setupZones()
	{
		valve1 = new Zone(new WorldPoint(2560, 3245, 0), new WorldPoint(2565, 3251, 0));
		valve2 = new Zone(new WorldPoint(2567, 3262, 0), new WorldPoint(2575, 3264, 0));
		valve3 = new Zone(new WorldPoint(2581, 3242, 0), new WorldPoint(2589, 3248, 0));
		valve4 = new Zone(new WorldPoint(2594, 3261, 0), new WorldPoint(2600, 3266, 0));
		valve5 = new Zone(new WorldPoint(2607, 3239, 0), new WorldPoint(2615, 3245, 0));
	}

	protected void setupConditions()
	{
		atValve1 = new ZoneRequirement(valve1);
		atValve2 = new ZoneRequirement(valve2);
		atValve3 = new ZoneRequirement(valve3);
		atValve4 = new ZoneRequirement(valve4);
		atValve5 = new ZoneRequirement(valve5);
		solved = new ManualRequirement();
	}

	@Override
	protected void setupSteps()
	{
		setupZones();
		setupConditions();

		turnValve1 = new ObjectStep(getQuestHelper(), ObjectID.SEWER_VALVE, new WorldPoint(2562, 3247, 0),
			"Turn the valve west of the Clocktower to the right.");

		turnValve2 = new ObjectStep(getQuestHelper(), ObjectID.SEWER_VALVE_2845, new WorldPoint(2572, 3263, 0),
			"Turn the valve next to the Carnillean home to the right.");

		turnValve3 = new ObjectStep(getQuestHelper(), ObjectID.SEWER_VALVE_2846, new WorldPoint(2585, 3245, 0),
			"Turn the valve east of the Clocktower to the left.");

		turnValve4 = new ObjectStep(getQuestHelper(), ObjectID.SEWER_VALVE_2847, new WorldPoint(2597, 3263, 0),
			"Turn the valve next to the zoo to the right.");

		turnValve5 = new ObjectStep(getQuestHelper(), ObjectID.SEWER_VALVE_2848, new WorldPoint(2611, 3242, 0),
			"Turn the valve north of the monastery to the right.");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(turnValve1, turnValve2, turnValve3, turnValve4, turnValve5);
	}
}
