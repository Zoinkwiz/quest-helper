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
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.Collections;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class SeersVillage extends AgilityCourse
{
	QuestStep seersSidebar;
	QuestStep climbWall, jumpFirstGap, crossTightrope, jumpSecondGap, jumpThirdGap, jumpEdgeZone;
	Zone firstGapZone, tightropeZone, secondGapZone, thirdGapZone, edgeZone;
	ZoneRequirement inFirstGapZone, inTightropeZone, inSecondGapZone, inThirdGapZone, inEdgeZone;

	ConditionalStep seersStep;
	PanelDetails seersPanels;

	protected SeersVillage(QuestHelper questHelper)
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

		return seersStep;
	}

	@Override
	protected void setupConditions()
	{
		inFirstGapZone = new ZoneRequirement(firstGapZone);
		inTightropeZone = new ZoneRequirement(tightropeZone);
		inSecondGapZone = new ZoneRequirement(secondGapZone);
		inThirdGapZone = new ZoneRequirement(thirdGapZone);
		inEdgeZone = new ZoneRequirement(edgeZone);
	}

	@Override
	protected void setupZones()
	{
		firstGapZone = new Zone(new WorldPoint(2730, 3490, 3), new WorldPoint(2715, 3495, 2));
		tightropeZone = new Zone(new WorldPoint(2704, 3483, 2), new WorldPoint(2714, 3498, 2));
		secondGapZone = new Zone(new WorldPoint(2709, 3476, 2), new WorldPoint(2716, 3482, 2));
		thirdGapZone = new Zone(new WorldPoint(2699, 3468, 3), new WorldPoint(2716, 3475, 3));
		edgeZone = new Zone(new WorldPoint(2690, 3459, 3), new WorldPoint(2703, 3467, 2));
	}

	@Override
	protected void setupSteps()
	{
		//Seer's village obstacles
		climbWall = new ObjectStep(this.questHelper, ObjectID.WALL_14927, new WorldPoint(2729, 3489, 0),
			"Climb up the wall outside the Seers' Bank.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFirstGap = new ObjectStep(this.questHelper, ObjectID.GAP_14928, new WorldPoint(2720, 3494, 3),
			"Jump across first gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		crossTightrope = new ObjectStep(this.questHelper, ObjectID.TIGHTROPE_14932, new WorldPoint(2710, 3489, 2),
			"Cross the tightrope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpSecondGap = new ObjectStep(this.questHelper, ObjectID.GAP_14929, new WorldPoint(2712, 3476, 2),
			"Jump across second gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpThirdGap = new ObjectStep(this.questHelper, ObjectID.GAP_14930, new WorldPoint(2702, 3469, 3),
			"Jump across third gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpEdgeZone = new ObjectStep(this.questHelper, ObjectID.EDGE_14931, new WorldPoint(2703, 3463, 2),
			"Jump off the edge.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		//Conditional step to group up the obstacles
		seersStep = new ConditionalStep(this.questHelper, climbWall);
		seersStep.addStep(new Conditions(inFirstGapZone), jumpFirstGap);
		seersStep.addStep(new Conditions(inTightropeZone), crossTightrope);
		seersStep.addStep(new Conditions(inSecondGapZone), jumpSecondGap);
		seersStep.addStep(new Conditions(inThirdGapZone), jumpThirdGap);
		seersStep.addStep(new Conditions(inEdgeZone), jumpEdgeZone);

		seersSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Seer's Village Rooftop Course, starting just outside the Seer's Bank.\n\n" +
			"60-80 Agility: If completed Kandarin Hard Diary, configure the Camelot Teleport Spell to Seer's and stay on Seer's rooftop course until 80 Agility. " +
			"After each completed lap, use the teleport spell to get close to the course starting point");
		seersSidebar.addSubSteps(climbWall, jumpFirstGap, crossTightrope, jumpSecondGap, jumpThirdGap, jumpEdgeZone, seersStep);
	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		seersPanels = new PanelDetails("60 - 70/80: Seer's Village", Collections.singletonList(seersSidebar)
		);
		seersPanels.setLockingStep(this.seersStep);
		return seersPanels;
	}
}
