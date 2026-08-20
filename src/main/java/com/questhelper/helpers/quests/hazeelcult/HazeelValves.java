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
package com.questhelper.helpers.quests.hazeelcult;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HazeelValves extends DetailedOwnerStep
{

	DetailedQuestStep turnValve1, turnValve2, turnValve3, turnValve4, turnValve5, catchState;
	DetailedQuestStep turnValve1NoDialog, turnValve2NoDialog,
		turnValve3NoDialog, turnValve4NoDialog, turnValve5NoDialog;

	private Zone valve1, valve2, valve3, valve4, valve5;

	private ZoneRequirement atValve1, atValve2, atValve3, atValve4, atValve5;

	private RuneliteRequirement solved1, solved2, solved3, solved4, solved5;

	public ManualRequirement solved;

	public HazeelValves(QuestHelper questHelper)
	{
		super(questHelper, "Turn the valves near the cave to direct the underground water.");
	}

	@Override
	protected void updateSteps()
	{
		if (!solved1.check(client))
		{
			if (atValve1.check(client))
			{
				startUpStep(turnValve1);
			}
			else
			{
				startUpStep(turnValve1NoDialog);
			}
		}
		else if (!solved2.check(client))
		{
			if (atValve2.check(client))
			{
				startUpStep(turnValve2);
			}
			else
			{
				startUpStep(turnValve2NoDialog);
			}
		}
		else if (!solved3.check(client))
		{
			if (atValve3.check(client))
			{
				startUpStep(turnValve3);
			}
			else
			{
				startUpStep(turnValve3NoDialog);
			}
		}
		else if (!solved4.check(client))
		{
			if (atValve4.check(client))
			{
				startUpStep(turnValve4);
			}
			else
			{
				startUpStep(turnValve4NoDialog);
			}
		}
		else if (!solved5.check(client))
		{
			if (atValve5.check(client))
			{
				startUpStep(turnValve5);
			}
			else
			{
				startUpStep(turnValve5NoDialog);
			}
		}
		else
		{
			startUpStep(catchState);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget currentStateWidget = client.getWidget(InterfaceID.Chatmenu.OPTIONS);
		String currentStateText = null;
		if (currentStateWidget != null)
		{
			Widget textContainingWidget = currentStateWidget.getChild(0);
			if (textContainingWidget != null)
			{
				currentStateText = textContainingWidget.getText();
			}
		}
		// Could also use MESBOX here
		// Chat message type MESBOX: You turn the valve to the right. Beneath your feet you hear the sudden sound of rushing water.
		Widget wheelTurnedWidget = client.getWidget(InterfaceID.Messagebox.TEXT);
		String wheelTurnedText = null;
		if (wheelTurnedWidget != null)
		{
			wheelTurnedText = wheelTurnedWidget.getText();
		}
		String text = null;
		if (wheelTurnedText != null) text = wheelTurnedText;
		if (currentStateText != null) text = currentStateText;
		if (text == null) return;

		boolean turnedLeft = text.contains("left");

		if (!turnedLeft && !text.contains("right"))
		{
			return;
		}

		if (atValve1.check(client))
		{
			updateState(!turnedLeft, solved1);
		}
		else if (atValve2.check(client))
		{
			updateState(!turnedLeft, solved2);
		}
		else if (atValve3.check(client))
		{
			updateState(turnedLeft, solved3);
		}
		else if (atValve4.check(client))
		{
			updateState(!turnedLeft, solved4);
		}
		else if (atValve5.check(client))
		{
			updateState(!turnedLeft, solved5);
		}

		solved.setShouldPass(solved1.check(client) && solved2.check(client) && solved3.check(client)
			&& solved4.check(client) && solved5.check(client));

		updateSteps();
	}

	private void updateState(boolean shouldPass, RuneliteRequirement req)
	{
		if (shouldPass)
		{
			req.setConfigValue("true");
		}
		else
		{
			req.setConfigValue("false");
		}
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
		solved1 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "hazeelvalves1", "true");
		solved1.initWithValue("false");
		solved2 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "hazeelvalves2", "true");
		solved2.initWithValue("false");
		solved3 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "hazeelvalves3", "true");
		solved3.initWithValue("false");
		solved4 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "hazeelvalves4", "true");
		solved4.initWithValue("false");
		solved5 = new RuneliteRequirement(getQuestHelper().getConfigManager(), "hazeelvalves5", "true");
		solved5.initWithValue("false");

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

		catchState = new DetailedQuestStep(getQuestHelper(), "You've entered an unknown state.");

		turnValve1 = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE1, new WorldPoint(2562, 3247, 0),
			"Turn the valve west of the Clocktower to the right.");
		turnValve1.addDialogStep("Turn it to the right.");
		turnValve1.addSubSteps(catchState);

		turnValve2 = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE2, new WorldPoint(2572, 3263, 0),
			"Turn the valve next to the Carnillean home to the right.");
		turnValve2.addDialogStep("Turn it to the right.");

		turnValve3 = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE3, new WorldPoint(2585, 3245, 0),
			"Turn the valve east of the Clocktower to the left.");
		turnValve3.addDialogStep("Turn it to the left.");

		turnValve4 = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE4, new WorldPoint(2597, 3263, 0),
			"Turn the valve next to the zoo to the right.");
		turnValve4.addDialogStep("Turn it to the right.");

		turnValve5 = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE5, new WorldPoint(2611, 3242, 0),
			"Turn the valve north of the monastery to the right.");
		turnValve5.addDialogStep("Turn it to the right.");

		turnValve1NoDialog = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE1, new WorldPoint(2562, 3247, 0),
			"Turn the valve west of the Clocktower to the right.");
		turnValve1.addSubSteps(turnValve1NoDialog);

		turnValve2NoDialog = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE2, new WorldPoint(2572, 3263, 0),
			"Turn the valve next to the Carnillean home to the right.");
		turnValve2.addSubSteps(turnValve2NoDialog);

		turnValve3NoDialog = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE3, new WorldPoint(2585, 3245, 0),
			"Turn the valve east of the Clocktower to the left.");
		turnValve3.addSubSteps(turnValve3NoDialog);

		turnValve4NoDialog = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE4, new WorldPoint(2597, 3263, 0),
			"Turn the valve next to the zoo to the right.");
		turnValve4.addSubSteps(turnValve4NoDialog);

		turnValve5NoDialog = new ObjectStep(getQuestHelper(), ObjectID.SEWERVALVE5, new WorldPoint(2611, 3242, 0),
			"Turn the valve north of the monastery to the right.");
		turnValve5.addSubSteps(turnValve5NoDialog);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(catchState, turnValve1, turnValve2, turnValve3, turnValve4, turnValve5, turnValve1NoDialog, turnValve2NoDialog, turnValve3NoDialog, turnValve4NoDialog, turnValve5NoDialog);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return Arrays.asList(turnValve1, turnValve2, turnValve3, turnValve4, turnValve5);
	}
}
