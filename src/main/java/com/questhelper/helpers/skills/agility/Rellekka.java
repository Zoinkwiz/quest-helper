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

import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.Collections;

public class Rellekka extends AgilityCourse
{
	QuestStep rellekkaSidebar;
	QuestStep climbRoughWall, leapFirstGap, walkFirstRope, leapSecondGap, hurdleGap, walkSecondRope, jumpInFish;
	Zone firstGapZone, firstRopeZone, secondGapZone, thirdGapZone, secondRopeZone, fishZone;
	ZoneRequirement inFirstGapZone, inFirstRopeZone, inSecondGapZone, inThirdGapZone, inSecondRopeZone, inFishZone;

	ConditionalStep rellekkaStep;
	PanelDetails rellekkaPanels;


	protected Rellekka(QuestHelper questHelper)
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

		return rellekkaStep;
	}

	@Override
	protected void setupConditions()
	{
		inFirstGapZone = new ZoneRequirement(firstGapZone);
		inFirstRopeZone = new ZoneRequirement(firstRopeZone);
		inSecondGapZone = new ZoneRequirement(secondGapZone);
		inThirdGapZone = new ZoneRequirement(thirdGapZone);
		inSecondRopeZone = new ZoneRequirement(secondRopeZone);
		inFishZone = new ZoneRequirement(fishZone);
	}

	@Override
	protected void setupZones()
	{
		firstGapZone = new Zone(new WorldPoint(2626, 3676, 3), new WorldPoint(2621, 3669, 3));
		firstRopeZone = new Zone(new WorldPoint(2615, 3668, 3), new WorldPoint(2626, 3655, 3));
		secondGapZone = new Zone(new WorldPoint(2626, 3651, 3), new WorldPoint(2637, 3659, 3));
		thirdGapZone = new Zone(new WorldPoint(2638, 3649, 3), new WorldPoint(2644, 3655, 3));
		secondRopeZone = new Zone(new WorldPoint(2643, 3656, 3), new WorldPoint(2653, 3670, 3));
		fishZone = new Zone(new WorldPoint(2649, 3665, 3), new WorldPoint(2664, 3685, 3));
	}

	@Override
	protected void setupSteps()
	{
		//Rellekka obstacles
		climbRoughWall = new ObjectStep(this.questHelper, ObjectID.ROUGH_WALL_14946, new WorldPoint(2625, 3677, 0),
			"Climb the rough wall in the north-western part of Rellekka, just south of the westernmost dock.",
			Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapFirstGap = new ObjectStep(this.questHelper, ObjectID.GAP_14947, new WorldPoint(2622, 3670, 3),
			"Leap across the gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		walkFirstRope = new ObjectStep(this.questHelper, ObjectID.TIGHTROPE_14987, new WorldPoint(2623, 3658, 3),
			"Cross the tightrope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapSecondGap = new ObjectStep(this.questHelper, ObjectID.GAP_14990, new WorldPoint(2630, 3656, 3),
			"Leap across the gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		hurdleGap = new ObjectStep(this.questHelper, ObjectID.GAP_14991, new WorldPoint(2643, 3654, 3),
			"Hurdle across the gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		walkSecondRope = new ObjectStep(this.questHelper, ObjectID.TIGHTROPE_14992, new WorldPoint(2647, 3663, 3),
			"Cross the tightrope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpInFish = new ObjectStep(this.questHelper, ObjectID.PILE_OF_FISH, new WorldPoint(2654, 3676, 3),
			"Jump down into the pile of fish.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		rellekkaStep = new ConditionalStep(this.questHelper, climbRoughWall);
		rellekkaStep.addStep(new Conditions(inFirstGapZone), leapFirstGap);
		rellekkaStep.addStep(new Conditions(inFirstRopeZone), walkFirstRope);
		rellekkaStep.addStep(new Conditions(inSecondGapZone), leapSecondGap);
		rellekkaStep.addStep(new Conditions(inThirdGapZone), hurdleGap);
		rellekkaStep.addStep(new Conditions(inSecondRopeZone), walkSecondRope);
		rellekkaStep.addStep(new Conditions(inFishZone), jumpInFish);

		rellekkaSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Rellekka Rooftop Course, " +
			"starting in the north-western part of Rellekka, just south of the westernmost dock.");
		rellekkaSidebar.addSubSteps(climbRoughWall, leapFirstGap, walkFirstRope, leapSecondGap, hurdleGap, walkSecondRope, jumpInFish);
	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		rellekkaPanels = new PanelDetails("80 - 90: Rellekka", Collections.singletonList(rellekkaSidebar)
		);
		return rellekkaPanels;
	}
}
