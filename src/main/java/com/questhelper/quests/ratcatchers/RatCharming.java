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
package com.questhelper.quests.ratcatchers;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class RatCharming extends DetailedOwnerStep
{
	QuestStep goNext, goPrevious, clickOctave, clickDone;

	QuestStep[] noteSteps;
	Requirement[] noteRequirements;

	public RatCharming(QuestHelper questHelper)
	{
		super(questHelper);
	}

	@Override
	protected void updateSteps()
	{
		int currentPage = client.getVarbitValue(1420);

		for (int i = 0; i < noteSteps.length; i++)
		{
			// If not done, let's do it.
			if (!noteRequirements[i].check(client))
			{
				if (currentPage < i)
				{
					startUpStep(goNext);
					return;
				}
				if (currentPage > i)
				{
					startUpStep(goPrevious);
					return;
				}
				// If need to raise octave
				if (i == 4)
				{
					int octaveRaised = client.getVarbitValue(1413);
					if (octaveRaised == 0)
					{
						startUpStep(clickOctave);
						return;
					}
				}

				startUpStep(noteSteps[i]);
				return;
			}
		}
		startUpStep(clickDone);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	public void setupRequirements()
	{
		noteRequirements = new Requirement[8];
		noteRequirements[0] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 63),
				new VarbitRequirement(1420, 0)
			),
			new VarbitRequirement(1395, 63)
		);

		noteRequirements[1] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 56),
				new VarbitRequirement(1420, 1)
			),
			new VarbitRequirement(1396, 56)
		);

		noteRequirements[2] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 62),
				new VarbitRequirement(1420, 2)
			),
			new VarbitRequirement(1397, 62)
		);

		noteRequirements[3] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 60),
				new VarbitRequirement(1420, 3)
			),
			new VarbitRequirement(1398, 60)
		);

		noteRequirements[4] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 127),
				new VarbitRequirement(1420, 4)
			),
			new VarbitRequirement(1399, 127)
		);

		noteRequirements[5] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 32),
				new VarbitRequirement(1420, 5)
			),
			new VarbitRequirement(1400, 32)
		);

		noteRequirements[6] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 1),
				new VarbitRequirement(1420, 6)
			),
			new VarbitRequirement(1401, 1)
		);

		noteRequirements[7] = new Conditions(LogicType.OR,
			new Conditions(
				new VarbitRequirement(1411, 48),
				new VarbitRequirement(1420, 7)
			),
			new VarbitRequirement(1402, 48)
		);
	}

	@Override
	protected void setupSteps()
	{
		setupRequirements();

		noteSteps = new QuestStep[8];
		noteSteps[0] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 2);
		noteSteps[1] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 5);
		noteSteps[2] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 3);
		noteSteps[3] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 4);
		noteSteps[4] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 2);
		noteSteps[5] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 7);
		noteSteps[6] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 8);
		noteSteps[7] = new WidgetStep(getQuestHelper(), "Click the marked note.", 282, 6);

		goNext = new WidgetStep(getQuestHelper(), "Go to the next page.", 282, 24);
		goPrevious = new WidgetStep(getQuestHelper(), "Go to the previous page.", 282, 23);
		clickOctave = new WidgetStep(getQuestHelper(), "Click the raise octave button.", 282, 11);
		clickDone = new WidgetStep(getQuestHelper(), "Click play.", 282, 20);
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		List<QuestStep> returnSteps = new ArrayList<>(Arrays.asList(noteSteps));
		returnSteps.addAll(Arrays.asList(goNext, goPrevious, clickOctave, clickDone));

		return returnSteps;
	}
}
