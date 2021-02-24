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
package com.questhelper.quests.recruitmentdrive;

import com.google.inject.Inject;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

public class LadyTableStep extends DetailedOwnerStep
{
	@Inject
	protected Client client;

	private Statue[] statues;
	private final int VARBIT_FINISHED_ROOM = 660;
	private final int VARBIT_STATUE_ANSWER = 667;

	private ObjectStep clickMissingStatue, leaveRoom;

	VarbitRequirement finishedRoom = new VarbitRequirement(VARBIT_FINISHED_ROOM, 1);

	public LadyTableStep(QuestHelper questHelper, Requirement... requirements)
	{
		super(questHelper, requirements);
	}

	@Override
	public void startUp()
	{
		super.startUp();
		Statue answerStatue = statues[client.getVarbitValue(VARBIT_STATUE_ANSWER)];
		clickMissingStatue.setText("Click the " + answerStatue.text + " once it appears.");
		clickMissingStatue.setWorldPoint(answerStatue.point);
	}

	@Subscribe
	@Override
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		super.onVarbitChanged(varbitChanged);
		Statue answerStatue = statues[client.getVarbitValue(VARBIT_STATUE_ANSWER)];
		clickMissingStatue.setText("Click the " + answerStatue.text + " once it appears.");
		clickMissingStatue.setWorldPoint(answerStatue.point);
	}

	@Override
	public void setupSteps()
	{
		statues = new Statue[]{
			new Statue("Unknown", new WorldPoint(0, 0, 0)),
			new Statue("Bronze Halberd", new WorldPoint(2452, 4976, 0)),
			new Statue("Silver Halberd", new WorldPoint(2452, 4979, 0)),
			new Statue("Gold Halberd", new WorldPoint(2452, 4982, 0)),

			new Statue("Bronze 2H", new WorldPoint(2450, 4976, 0)),
			new Statue("Silver 2H", new WorldPoint(2450, 4979, 0)),
			new Statue("Gold 2H", new WorldPoint(2450, 4982, 0)),

			new Statue("Gold Mace", new WorldPoint(2456, 4982, 0)),
			new Statue("Silver Mace", new WorldPoint(2456, 4979, 0)),
			new Statue("Bronze mace", new WorldPoint(2456, 4976, 0)),

			new Statue("Bronze axe", new WorldPoint(2454, 4976, 0)),
			new Statue("Silver axe", new WorldPoint(2454, 4979, 0)),
			new Statue("Gold axe", new WorldPoint(2454, 4972, 0))
		};

		leaveRoom = new ObjectStep(questHelper, 7302, "Leave through the door to enter the portal and continue.");
		clickMissingStatue = new ObjectStep(questHelper, 0, statues[0].point, "CLick the missing statue.");
		clickMissingStatue.addAlternateObjects(ObjectID.STATUE_7303, ObjectID.STATUE_7304, ObjectID.STATUE_7305,
			ObjectID.STATUE_7306, ObjectID.STATUE_7307, ObjectID.STATUE_7308, ObjectID.STATUE_7309,
			ObjectID.STATUE_7310, ObjectID.STATUE_7311, ObjectID.STATUE_7312, ObjectID.STATUE_7314);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		List<QuestStep> step = new ArrayList<>();

		step.add(clickMissingStatue);
		step.add(leaveRoom);
		return step;
	}

	@Override
	protected void updateSteps()
	{
		if (finishedRoom.check(client))
		{
			startUpStep(leaveRoom);
		}
		startUpStep(clickMissingStatue);
	}

	public List<QuestStep> getPanelSteps()
	{
		List<QuestStep> steps = new ArrayList<>();
		steps.add(clickMissingStatue);
		steps.add(leaveRoom);

		return steps;
	}

	static class Statue
	{
		private final String text;
		private final WorldPoint point;

		public Statue(String text, WorldPoint point)
		{
			this.text = text;
			this.point = point;
		}
	}
}
