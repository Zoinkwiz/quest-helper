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
package com.questhelper.quests.akingdomdivided;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class XericsLookoutStepper extends ConditionalStep
{
	int floor;

	QuestStep speakToWho;

	ObjectStep goDownLookoutF3toF2, goDownLookoutF2toF1, goDownLookoutF1toF0, goDownLookoutF0toBasement,
		goUpLookoutBasementtoF0, goUpLookoutF0toF1, goUpLookoutF1toF2, goUpLookoutF2toF3;

	Requirement inLookoutBasement, inLookoutF0, inLookoutF1, inLookoutF2, inLookoutF3;

	Zone lookoutBasement, lookoutF1, lookoutF2, lookoutF3;

	public XericsLookoutStepper(QuestHelper questHelper, QuestStep speakToWho, int floor, QuestStep sidebar) {
		super(questHelper, speakToWho);

		this.speakToWho = speakToWho;
		this.floor = floor;

		setupZones();
		setupConditions();
		setupSteps();

		sidebar.addSubSteps(goDownLookoutF3toF2, goDownLookoutF2toF1, goDownLookoutF1toF0, goDownLookoutF0toBasement,
			goUpLookoutBasementtoF0, goUpLookoutF0toF1, goUpLookoutF1toF2, goUpLookoutF2toF3);

		addSteppers();
	}

	public XericsLookoutStepper(QuestHelper questHelper, QuestStep speakToWho, int floor) {
		super(questHelper, speakToWho);

		this.speakToWho = speakToWho;
		this.floor = floor;

		setupZones();
		setupConditions();
		setupSteps();

		speakToWho.addSubSteps(goDownLookoutF3toF2, goDownLookoutF2toF1, goDownLookoutF1toF0, goDownLookoutF0toBasement,
			goUpLookoutBasementtoF0, goUpLookoutF0toF1, goUpLookoutF1toF2, goUpLookoutF2toF3);

		addSteppers();
	}

	private void addSteppers()
	{
		if (floor == 3) addStep(new Conditions(inLookoutF3), speakToWho);
		else if (floor == 2) addStep(new Conditions(inLookoutF2), speakToWho);
		else if (floor == 1) addStep(new Conditions(inLookoutF1), speakToWho);
		else if (floor == 0) addStep(new Conditions(inLookoutF0), speakToWho);
		else if (floor == -1) addStep(new Conditions(inLookoutBasement), speakToWho);

		if (floor > -1) addStep(new Conditions(inLookoutBasement), goUpLookoutBasementtoF0);
		if (floor < 0) addStep(new Conditions(inLookoutF0), goDownLookoutF0toBasement);
		else if (floor > 0) addStep(inLookoutF0, goUpLookoutF0toF1);
		if (floor < 1) addStep(new Conditions(inLookoutF1), goDownLookoutF1toF0);
		else if (floor > 1) addStep(new Conditions(inLookoutF1), goUpLookoutF1toF2);
		if (floor < 2) addStep(new Conditions(inLookoutF2), goDownLookoutF2toF1);
		else if (floor > 2) addStep(new Conditions(inLookoutF2), goUpLookoutF2toF3);
		if (floor < 3) addStep(new Conditions(inLookoutF3), goDownLookoutF3toF2);
	}

	public void setupZones()
	{
		lookoutF3 = new Zone(new WorldPoint(1589, 3533, 3), new WorldPoint(1595, 3527, 3));
		lookoutF2 = new Zone(new WorldPoint(1589, 3533, 2), new WorldPoint(1595, 3527, 2));
		lookoutF1 = new Zone(new WorldPoint(1589, 3533, 1), new WorldPoint(1595, 3527, 1));
		lookoutBasement = new Zone(new WorldPoint(1558, 9960, 0), new WorldPoint(1572, 9945, 0));

	}

	public void setupConditions()
	{
		inLookoutBasement = new ZoneRequirement(lookoutBasement);
		inLookoutF1 = new ZoneRequirement(lookoutF1);
		inLookoutF2 = new ZoneRequirement(lookoutF2);
		inLookoutF3 = new ZoneRequirement(lookoutF3);

		inLookoutF0 = new Conditions(LogicType.NOR, inLookoutBasement, inLookoutF1, inLookoutF2, inLookoutF3);
	}

	public void setupSteps()
	{
		goDownLookoutF3toF2 = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_11890, new WorldPoint(1592, 3530, 3), this.getStepText(), getStepRequirements());
		goDownLookoutF2toF1 = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_11889, new WorldPoint(1592, 3529, 2), this.getStepText(), getStepRequirements());
		goDownLookoutF2toF1.addDialogSteps("Climb down");
		goDownLookoutF1toF0 = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_11889, new WorldPoint(1592, 3529, 1), this.getStepText(), getStepRequirements());
		goDownLookoutF1toF0.addDialogSteps("Climb down");
		goDownLookoutF0toBasement = new ObjectStep(getQuestHelper(), ObjectID.LADDER_41916, new WorldPoint(1590, 3526, 0), this.getStepText(), getStepRequirements());
		goUpLookoutBasementtoF0 = new ObjectStep(getQuestHelper(), ObjectID.LADDER_41917, new WorldPoint(1564, 9949, 0), this.getStepText(), getStepRequirements());
		goUpLookoutF0toF1 = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_11888, new WorldPoint(1592, 3529, 0), this.getStepText(), getStepRequirements());
		goUpLookoutF1toF2 = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_11889, new WorldPoint(1592, 3529, 1), this.getStepText(), getStepRequirements());
		goUpLookoutF1toF2.addDialogSteps("Climb up");
		goUpLookoutF2toF3 = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_11889, new WorldPoint(1592, 3529, 2), this.getStepText(), getStepRequirements());
		goUpLookoutF2toF3.addDialogSteps("Climb up");
	}

	private String getStepText()
	{
		return this.speakToWho.getText().toString().replaceAll("\\[|\\]", "");
	}

	private Requirement[] getStepRequirements()
	{
		DetailedQuestStep step = (DetailedQuestStep) this.speakToWho;
		return step.getRequirements().toArray(new Requirement[0]);
	}

}
