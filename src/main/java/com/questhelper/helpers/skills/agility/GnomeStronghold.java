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

public class GnomeStronghold extends AgilityCourse
{
	QuestStep gnomeSidebar;
	QuestStep walkLog, climbFirstNet, climbFirstTree, walkRope, climbSecondTree, climbSecondNet, squeezePipe;
	Zone firstNetZone, firstTreeZone, ropeZone, secondTreeZone, secondNetZone, pipeZone;
	ZoneRequirement inFirstNetZone, inFirstTreeZone, inRopeZone, inSecondTreeZone, inSecondNetZone, inPipeZone;

	ConditionalStep gnomeStep;
	PanelDetails gnomePanels;

	public GnomeStronghold(QuestHelper questHelper)
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

		return gnomeStep;
	}

	@Override
	protected void setupConditions()
	{
		inFirstNetZone = new ZoneRequirement(firstNetZone);
		inFirstTreeZone = new ZoneRequirement(firstTreeZone);
		inRopeZone = new ZoneRequirement(ropeZone);
		inSecondTreeZone = new ZoneRequirement(secondTreeZone);
		inSecondNetZone = new ZoneRequirement(secondNetZone);
		inPipeZone = new ZoneRequirement(pipeZone);
	}

	@Override
	protected void setupZones()
	{
		firstNetZone = new Zone(new WorldPoint(2471, 3425, 0), new WorldPoint(2476, 3429, 1));
		firstTreeZone = new Zone(new WorldPoint(2471, 3422, 1), new WorldPoint(2476, 3424, 2));
		ropeZone = new Zone(new WorldPoint(2472, 3418, 2), new WorldPoint(2477, 3421, 2));
		secondTreeZone = new Zone(new WorldPoint(2478, 3418, 1), new WorldPoint(2488, 3421, 2));
		secondNetZone = new Zone(new WorldPoint(2482, 3418, 0), new WorldPoint(2490, 3426, 0));
		pipeZone = new Zone(new WorldPoint(2482, 3427, 0), new WorldPoint(2490, 3436, 0));
	}

	@Override
	protected void setupSteps()
	{
		//Gnome Stronghold obstacles
		walkLog = new ObjectStep(this.questHelper, ObjectID.LOG_BALANCE_23145, new WorldPoint(2474, 3435, 0),
			"Walk-across log south-east of the Spirit tree in Gnome Stronghold.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		climbFirstNet = new ObjectStep(this.questHelper, ObjectID.OBSTACLE_NET_23134, new WorldPoint(2473, 3425, 0),
			"Climb over the west net.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		climbFirstTree = new ObjectStep(this.questHelper, ObjectID.TREE_BRANCH_23559, new WorldPoint(2473, 3422, 1),
			"Climb up the tree branch.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		walkRope = new ObjectStep(this.questHelper, ObjectID.BALANCING_ROPE_23557, new WorldPoint(2478, 3420, 2),
			"Walk on the balancing rope.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		climbSecondTree = new ObjectStep(this.questHelper, ObjectID.TREE_BRANCH_23560, new WorldPoint(2486, 3419, 2),
			"Climb down the tree branch.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		climbSecondNet = new ObjectStep(this.questHelper, ObjectID.OBSTACLE_NET_23135, new WorldPoint(2485, 3426, 0),
			"Climb over the east net.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		squeezePipe = new ObjectStep(this.questHelper, ObjectID.OBSTACLE_PIPE_23138, new WorldPoint(2484, 3431, 0),
			"Squeeze through the pipe.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		gnomeStep = new ConditionalStep(this.questHelper, walkLog);
		gnomeStep.addStep(inFirstNetZone, climbFirstNet);
		gnomeStep.addStep(inFirstTreeZone, climbFirstTree);
		gnomeStep.addStep(inRopeZone, walkRope);
		gnomeStep.addStep(inSecondTreeZone, climbSecondTree);
		gnomeStep.addStep(inSecondNetZone, climbSecondNet);
		gnomeStep.addStep(inPipeZone, squeezePipe);

		gnomeSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Gnome Stronghold Agility Course, south-east of the Spirit tree in Gnome Stronghold.");
		gnomeSidebar.addSubSteps(walkLog, climbFirstNet, climbFirstTree, walkRope, climbSecondTree, climbSecondNet, squeezePipe);
	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		gnomePanels = new PanelDetails("1 - 10: Gnome Stronghold", Collections.singletonList(gnomeSidebar)
		);
		return gnomePanels;
	}
}
