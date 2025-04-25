/*
 * Copyright (c) 2023, JesperBodin <https://github.com/JesperBodin, jLereback <https://github.com/jLereback>
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
package com.questhelper.helpers.skills.agility;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

import java.util.Arrays;
import java.util.Collections;

public class Ardougne extends AgilityCourse

{
	QuestStep ardougneSidebar;
	QuestStep climbWoodenBeam, jumpFirstGap, walkOnPlank, jumpSecondGap, jumpThirdGap, balanceRoof, jumpForthGap;
	Zone firstGapZone, plankZone, secondGapZone, thirdGapZone, balanceZone, forthGapZone;
	ZoneRequirement inFirstGapZone, inPlankZone, inSecondGapZone, inThirdGapZone, inBalanceZone, inForthGapZone;

	ConditionalStep ardougneStep;
	PanelDetails ardougnePanels;

	public Ardougne(QuestHelper questHelper)
	{
		super(questHelper);
	}

	@Override
	protected ConditionalStep loadStep()
	{
		setupZones();
		setupConditions();
		setupSteps();
		addSteps();

		return ardougneStep;
	}

	@Override
	protected void setupConditions()
	{
		inFirstGapZone = new ZoneRequirement(firstGapZone);
		inPlankZone = new ZoneRequirement(plankZone);
		inSecondGapZone = new ZoneRequirement(secondGapZone);
		inThirdGapZone = new ZoneRequirement(thirdGapZone);
		inBalanceZone = new ZoneRequirement(balanceZone);
		inForthGapZone = new ZoneRequirement(forthGapZone);


	}

	@Override
	protected void setupZones()
	{
		firstGapZone = new Zone(new WorldPoint(2671, 3299, 3), new WorldPoint(2666, 3318, 3));
		plankZone = new Zone(new WorldPoint(2665, 3318, 3), new WorldPoint(2657, 3318, 3));
		secondGapZone = new Zone(new WorldPoint(2656, 3318, 3), new WorldPoint(2653, 3315, 3));
		thirdGapZone = new Zone(new WorldPoint(2653, 3314, 3), new WorldPoint(2653, 3310, 3));
		balanceZone = new Zone(new WorldPoint(2651, 3309, 3), new WorldPoint(2655, 3298, 3));
		forthGapZone = new Zone(new WorldPoint(2656, 3297, 3), new WorldPoint(2667, 3297, 0));


	}

	@Override
	protected void setupSteps()
	{
		//Ardougne obstacles
		climbWoodenBeam = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_WALLCLIMB, new WorldPoint(2729, 3489, 0),
			"Climb up the wooden beams on the outside of the house south east of the East Ardougne Marketplace.",
			Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
		jumpFirstGap = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_JUMP, new WorldPoint(2729, 3489, 0),
			"Jump across the gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
		walkOnPlank = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_PLANK, new WorldPoint(2729, 3489, 0),
			"Walk across the plank.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
		jumpSecondGap = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_JUMP_2, new WorldPoint(2729, 3489, 0),
			"Jump across the gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
		jumpThirdGap = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_JUMP_3, new WorldPoint(2729, 3489, 0),
			"Jump across another gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
		balanceRoof = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_WALLCROSSING, new WorldPoint(2729, 3489, 0),
			"Balance across the steep roof.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
		jumpForthGap = new ObjectStep(this.questHelper, ObjectID.ROOFTOPS_ARDY_JUMP_4, new WorldPoint(2729, 3489, 0),
			"Balance across the final steep roof.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));


	}

	@Override
	protected void addSteps()
	{
		ardougneStep = new ConditionalStep(this.questHelper, climbWoodenBeam);
		ardougneStep.addStep(new Conditions(inFirstGapZone), jumpFirstGap);
		ardougneStep.addStep(new Conditions(inPlankZone), walkOnPlank);
		ardougneStep.addStep(new Conditions(inSecondGapZone), jumpSecondGap);
		ardougneStep.addStep(new Conditions(inThirdGapZone), jumpThirdGap);
		ardougneStep.addStep(new Conditions(inBalanceZone), balanceRoof);
		ardougneStep.addStep(new Conditions(inForthGapZone), jumpForthGap);

		ardougneSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Ardougne Rooftop Course, " +
			"starting outside of the house south east of the East Ardougne Marketplace.");
		ardougneSidebar.addSubSteps(climbWoodenBeam, jumpFirstGap, walkOnPlank, jumpSecondGap, jumpThirdGap, balanceRoof, jumpForthGap);

	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		ardougnePanels = new PanelDetails("90 - 99: Ardougne", Collections.singletonList(ardougneSidebar)
		);
		return ardougnePanels;
	}
}
