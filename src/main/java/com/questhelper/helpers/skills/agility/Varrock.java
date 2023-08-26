/*
 * Copyright (c) 2023, jLereback <https://github.com/jLereback>
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
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collections;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class Varrock extends AgilityCourse
{
	QuestStep varrockSidebar;
	QuestStep climbRoughWall, crossClothesLine, leapFirstGap, balanceWall, leapSecondGap, leapThirdGap, leapFourthGap, hurdleLedge, jumpEdge;
	Zone clothesLineZone, firstGapZone, wallZone, secondGapZone, thirdGapZone, fourthGapZone, ledgeZone, edgeZone;
	ZoneRequirement inClothesLineZone, inFirstGapZone, inWallZone, inSecondGapZone, inThirdGapZone, inFourthGapZone, inLedgeZone, inEdgeZone;

	ConditionalStep varrockStep;
	PanelDetails varrockPanels;

	public Varrock(QuestHelper questHelper)
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
		return varrockStep;
	}

	@Override
	protected void setupConditions()
	{
		inClothesLineZone = new ZoneRequirement(clothesLineZone);
		inFirstGapZone = new ZoneRequirement(firstGapZone);
		inWallZone = new ZoneRequirement(wallZone);
		inSecondGapZone = new ZoneRequirement(secondGapZone);
		inThirdGapZone = new ZoneRequirement(thirdGapZone);
		inFourthGapZone = new ZoneRequirement(fourthGapZone);
		inLedgeZone = new ZoneRequirement(ledgeZone);
		inEdgeZone = new ZoneRequirement(edgeZone);
	}

	@Override
	protected void setupZones()
	{
		clothesLineZone = new Zone(new WorldPoint(3210, 3410, 3), new WorldPoint(3219, 3419, 3));
		firstGapZone = new Zone(new WorldPoint(3201, 3413, 3), new WorldPoint(3209, 3419, 1));
		wallZone = new Zone(new WorldPoint(3190, 3407, 1), new WorldPoint(3197, 3416, 3));
		secondGapZone = new Zone(new WorldPoint(3191, 3399, 3), new WorldPoint(3198, 3406, 3));
		thirdGapZone = new Zone(new WorldPoint(3183, 3383, 3), new WorldPoint(3217, 3403, 3));
		fourthGapZone = new Zone(new WorldPoint(3218, 3393, 3), new WorldPoint(3234, 3403, 3));
		ledgeZone = new Zone(new WorldPoint(3235, 3403, 3), new WorldPoint(3240, 3409, 3));
		edgeZone = new Zone(new WorldPoint(3236, 3410, 3), new WorldPoint(3240, 3416, 3));
	}

	@Override
	protected void setupSteps()
	{
		//Varrock obstacles
		climbRoughWall = new ObjectStep(this.questHelper, ObjectID.ROUGH_WALL_14412, new WorldPoint(3221, 3414, 0),
			"Climb the rough wall on the east side of the General Store.",
			Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossClothesLine = new ObjectStep(this.questHelper, ObjectID.CLOTHES_LINE, new WorldPoint(3213, 3414, 3),
			"Cross the clothes line.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapFirstGap = new ObjectStep(this.questHelper, ObjectID.GAP_14414, new WorldPoint(3200, 3416, 3),
			"Leap off the first gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		balanceWall = new ObjectStep(this.questHelper, ObjectID.WALL_14832, new WorldPoint(3192, 3416, 1),
			"Balance across the wall.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapSecondGap = new ObjectStep(this.questHelper, ObjectID.GAP_14833, new WorldPoint(3195, 3401, 3),
			"Leap across second gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapThirdGap = new ObjectStep(this.questHelper, ObjectID.GAP_14834, new WorldPoint(3209, 3399, 3),
			"Leap across third gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		leapFourthGap = new ObjectStep(this.questHelper, ObjectID.GAP_14835, new WorldPoint(3233, 3402, 3),
			"Leap across fourth gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		hurdleLedge = new ObjectStep(this.questHelper, ObjectID.LEDGE_14836, new WorldPoint(3238, 3409, 3),
			"Hurdle across the ledge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpEdge = new ObjectStep(this.questHelper, ObjectID.EDGE, new WorldPoint(3238, 3416, 3),
			"Jump off the edge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		varrockStep = new ConditionalStep(this.questHelper, climbRoughWall);
		varrockStep.addStep(inClothesLineZone, crossClothesLine);
		varrockStep.addStep(inFirstGapZone, leapFirstGap);
		varrockStep.addStep(inWallZone, balanceWall);
		varrockStep.addStep(inSecondGapZone, leapSecondGap);
		varrockStep.addStep(inThirdGapZone, leapThirdGap);
		varrockStep.addStep(inFourthGapZone, leapFourthGap);
		varrockStep.addStep(inLedgeZone, hurdleLedge);
		varrockStep.addStep(inEdgeZone, jumpEdge);

		varrockSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Varrock Rooftop Course, " +
			"starting on the east side of the General Store.");
		varrockSidebar.addSubSteps(climbRoughWall, crossClothesLine, leapFirstGap, balanceWall, leapSecondGap, leapThirdGap, leapFourthGap, hurdleLedge, jumpEdge, varrockStep);

	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		varrockPanels = new PanelDetails("30 - 40: Varrock", Collections.singletonList(varrockSidebar)
		);
		return varrockPanels;
	}
}
