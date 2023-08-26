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

public class Falador extends AgilityCourse
{
	QuestStep faladorSidebar;
	QuestStep climbRoughWall, crossFirstTightrope, crossHandHolds, jumpFirstGap, jumpSecondGap, crossSecondTightrope,
		crossThirdTightrope, jumpThirdGap, jumpFirstLedge, jumpSecondLedge, jumpThirdLedge, jumpFourthLedge, jumpEdge;
	Zone firstTightropeZone, handHoldsZone, firstGapZone, secondGapZone, secondTightropeZone, thirdTightropeZone,
		thirdGapZone, firstLedgeZone, secondLedgeZone, thirdLedgeZone, fourthLedgeZone, edgeZone;
	ZoneRequirement inFirstTightropeZone, inHandHoldsZone, inFirstGapZone, inSecondGapZone, inSecondTightropeZone,
		inThirdTightropeZone, inThirdGapZone, inFirstLedgeZone, inSecondLedgeZone, inThirdLedgeZone, inFourthLedgeZone, inEdgeZone;

	ConditionalStep faladorStep;
	PanelDetails faladorPanels;

	public Falador(QuestHelper questHelper)
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

		return faladorStep;
	}

	@Override
	protected void setupConditions()
	{
		inFirstTightropeZone = new ZoneRequirement(firstTightropeZone);
		inHandHoldsZone = new ZoneRequirement(handHoldsZone);
		inFirstGapZone = new ZoneRequirement(firstGapZone);
		inSecondGapZone = new ZoneRequirement(secondGapZone);
		inSecondTightropeZone = new ZoneRequirement(secondTightropeZone);
		inThirdTightropeZone = new ZoneRequirement(thirdTightropeZone);
		inThirdGapZone = new ZoneRequirement(thirdGapZone);
		inFirstLedgeZone = new ZoneRequirement(firstLedgeZone);
		inSecondLedgeZone = new ZoneRequirement(secondLedgeZone);
		inThirdLedgeZone = new ZoneRequirement(thirdLedgeZone);
		inFourthLedgeZone = new ZoneRequirement(fourthLedgeZone);
		inEdgeZone = new ZoneRequirement(edgeZone);
	}

	@Override
	protected void setupZones()
	{
		firstTightropeZone = new Zone(new WorldPoint(3036, 3342, 3), new WorldPoint(3043, 3343, 3));
		handHoldsZone = new Zone(new WorldPoint(3044, 3341, 3), new WorldPoint(3051, 3355, 2));
		firstGapZone = new Zone(new WorldPoint(3048, 3357, 3), new WorldPoint(3050, 3360, 3));
		secondGapZone = new Zone(new WorldPoint(3042, 3361, 3), new WorldPoint(3048, 3367, 3));
		secondTightropeZone = new Zone(new WorldPoint(3028, 3356, 3), new WorldPoint(3041, 3364, 3));
		thirdTightropeZone = new Zone(new WorldPoint(3022, 3352, 3), new WorldPoint(3029, 3355, 3));
		thirdGapZone = new Zone(new WorldPoint(3009, 3350, 3), new WorldPoint(3021, 3358, 3));
		firstLedgeZone = new Zone(new WorldPoint(3016, 3343, 3), new WorldPoint(3022, 3349, 3));
		secondLedgeZone = new Zone(new WorldPoint(3011, 3343, 3), new WorldPoint(3015, 3346, 3));
		thirdLedgeZone = new Zone(new WorldPoint(3009, 3335, 3), new WorldPoint(3013, 3342, 3));
		fourthLedgeZone = new Zone(new WorldPoint(3012, 3331, 3), new WorldPoint(3018, 3334, 3));
		edgeZone = new Zone(new WorldPoint(3019, 3332, 3), new WorldPoint(3024, 3335, 3));
	}

	@Override
	protected void setupSteps()
	{
		//Falador obstacles
		climbRoughWall = new ObjectStep(this.questHelper, ObjectID.ROUGH_WALL_14898, new WorldPoint(3036, 3341, 0),
			"Climb the rough wall east of the Mining Guild basement entrance.",
			Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossFirstTightrope = new ObjectStep(this.questHelper, ObjectID.TIGHTROPE_14899, new WorldPoint(3040, 3343, 3),
			"Cross the first tightrope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossHandHolds = new ObjectStep(this.questHelper, ObjectID.HAND_HOLDS_14901, new WorldPoint(3050, 3350, 3),
			"Cross the handholds.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFirstGap = new ObjectStep(this.questHelper, ObjectID.GAP_14903, new WorldPoint(3048, 3359, 3),
			"Jump across the first gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpSecondGap = new ObjectStep(this.questHelper, ObjectID.GAP_14904, new WorldPoint(3044, 3363, 3),
			"Jump across the second gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossSecondTightrope = new ObjectStep(this.questHelper, ObjectID.TIGHTROPE_14905, new WorldPoint(3034, 3362, 3),
			"Cross the second tightrope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossThirdTightrope = new ObjectStep(this.questHelper, ObjectID.TIGHTROPE_14911, new WorldPoint(3026, 3353, 3),
			"Cross the third tightrope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpThirdGap = new ObjectStep(this.questHelper, ObjectID.GAP_14919, new WorldPoint(3017, 3352, 3),
			"Jump across the third gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFirstLedge = new ObjectStep(this.questHelper, ObjectID.LEDGE_14920, new WorldPoint(3015, 3346, 3),
			"Jump across the first ledge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpSecondLedge = new ObjectStep(this.questHelper, ObjectID.LEDGE_14921, new WorldPoint(3012, 3343, 3),
			"Jump across the second ledge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpThirdLedge = new ObjectStep(this.questHelper, ObjectID.LEDGE_14922, new WorldPoint(3013, 3334, 3),
			"Jump across the third ledge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFourthLedge = new ObjectStep(this.questHelper, ObjectID.LEDGE_14924, new WorldPoint(3018, 3333, 3),
			"Jump across the fourth ledge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpEdge = new ObjectStep(this.questHelper, ObjectID.EDGE_14925, new WorldPoint(3025, 3333, 3),
			"Jump off the edge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		faladorStep = new ConditionalStep(this.questHelper, climbRoughWall);
		faladorStep.addStep(inFirstTightropeZone, crossFirstTightrope);
		faladorStep.addStep(inHandHoldsZone, crossHandHolds);
		faladorStep.addStep(inFirstGapZone, jumpFirstGap);
		faladorStep.addStep(inSecondGapZone, jumpSecondGap);
		faladorStep.addStep(inSecondTightropeZone, crossSecondTightrope);
		faladorStep.addStep(inThirdTightropeZone, crossThirdTightrope);
		faladorStep.addStep(inThirdGapZone, jumpThirdGap);
		faladorStep.addStep(inFirstLedgeZone, jumpFirstLedge);
		faladorStep.addStep(inSecondLedgeZone, jumpSecondLedge);
		faladorStep.addStep(inThirdLedgeZone, jumpThirdLedge);
		faladorStep.addStep(inFourthLedgeZone, jumpFourthLedge);
		faladorStep.addStep(inEdgeZone, jumpEdge);

		faladorSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Falador Rooftop Course, " +
			"starting east of the Mining Guild basement entrance.");
		faladorSidebar.addSubSteps(climbRoughWall, crossFirstTightrope, crossHandHolds, jumpFirstGap, jumpSecondGap, crossSecondTightrope,
			crossThirdTightrope, jumpThirdGap, jumpFirstLedge, jumpSecondLedge, jumpThirdLedge, jumpFourthLedge, jumpEdge);

	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		faladorPanels = new PanelDetails("50 - 60: Falador", Collections.singletonList(faladorSidebar)
		);
		return faladorPanels;
	}
}
