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

public class Canifis extends AgilityCourse
{
	QuestStep canifisSidebar;
	QuestStep climbTallTree, jumpFirstGap, jumpSecondGap, jumpThirdGap, jumpFourthGap, vaultPoleVault, jumpFifthGap, jumpSixthGap;
	Zone firstGapZone, secondGapZone, thirdGapZone, fourthGapZone, poleVaultZone, fifthGapZone, sixthGapzone;
	ZoneRequirement inFirstGapZone, inSecondGapZone, inThirdGapZone, inFourthGapZone, inPoleVaultZone, inFifthGapZone, inSixthGapZone;

	ConditionalStep canifisStep;
	PanelDetails canifisPanels;

	public Canifis(QuestHelper questHelper)
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

		return canifisStep;
	}

	@Override
	protected void setupConditions()
	{
		inFirstGapZone = new ZoneRequirement(firstGapZone);
		inSecondGapZone = new ZoneRequirement(secondGapZone);
		inThirdGapZone = new ZoneRequirement(thirdGapZone);
		inFourthGapZone = new ZoneRequirement(fourthGapZone);
		inPoleVaultZone = new ZoneRequirement(poleVaultZone);
		inFifthGapZone = new ZoneRequirement(fifthGapZone);
		inSixthGapZone = new ZoneRequirement(sixthGapzone);
	}

	@Override
	protected void setupZones()
	{
		firstGapZone = new Zone(new WorldPoint(3504, 3490, 2), new WorldPoint(3512, 3501, 2));
		secondGapZone = new Zone(new WorldPoint(3495, 3502, 2), new WorldPoint(3505, 3508, 2));
		thirdGapZone = new Zone(new WorldPoint(3482, 3497, 2), new WorldPoint(3494, 3506, 3));
		fourthGapZone = new Zone(new WorldPoint(3480, 3500, 3), new WorldPoint(3474, 3489, 2));
		poleVaultZone = new Zone(new WorldPoint(3476, 3478, 2), new WorldPoint(3487, 3488, 3));
		fifthGapZone = new Zone(new WorldPoint(3486, 3468, 3), new WorldPoint(3506, 3479, 2));
		sixthGapzone = new Zone(new WorldPoint(3507, 3473, 2), new WorldPoint(3517, 3484, 2));
	}

	@Override
	protected void setupSteps()
	{
		//Canifis obstacles
		climbTallTree = new ObjectStep(this.questHelper, ObjectID.TALL_TREE_14843, new WorldPoint(3507, 3489, 0),
			"Climb the tall tree just north of the bank in Canifis.",
			Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFirstGap = new ObjectStep(this.questHelper, ObjectID.GAP_14844, new WorldPoint(3506, 3498, 2),
			"Jump across the first gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpSecondGap = new ObjectStep(this.questHelper, ObjectID.GAP_14845, new WorldPoint(3497, 3504, 2),
			"Jump across the second gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpThirdGap = new ObjectStep(this.questHelper, ObjectID.GAP_14848, new WorldPoint(3486, 3499, 2),
			"Jump across the third gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFourthGap = new ObjectStep(this.questHelper, ObjectID.GAP_14846, new WorldPoint(3478, 3492, 3),
			"Jump across the fourth gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		vaultPoleVault = new ObjectStep(this.questHelper, ObjectID.POLEVAULT, new WorldPoint(3480, 3483, 2),
			"Vault across the roofs.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpFifthGap = new ObjectStep(this.questHelper, ObjectID.GAP_14847, new WorldPoint(3503, 3476, 3),
			"Jump across the fifth gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));

		jumpSixthGap = new ObjectStep(this.questHelper, ObjectID.GAP_14897, new WorldPoint(3510, 3483, 2),
			"Jump across the sixth gap.", Collections.EMPTY_LIST, Arrays.asList(recommendedItems));
	}

	@Override
	protected void addSteps()
	{
		canifisStep = new ConditionalStep(this.questHelper, climbTallTree);
		canifisStep.addStep(inFirstGapZone, jumpFirstGap);
		canifisStep.addStep(inSecondGapZone, jumpSecondGap);
		canifisStep.addStep(inThirdGapZone, jumpThirdGap);
		canifisStep.addStep(inFourthGapZone, jumpFourthGap);
		canifisStep.addStep(inPoleVaultZone, vaultPoleVault);
		canifisStep.addStep(inFifthGapZone, jumpFifthGap);
		canifisStep.addStep(inSixthGapZone, jumpSixthGap);

		canifisSidebar = new DetailedQuestStep(this.questHelper, "Train agility at the Canifis Rooftop Course, starting just north of Canifis Bank.");
		canifisSidebar.addText("40-60 Agility: Canifis Rooftop Course is the best source of Mark of Grace until 60 Agility.");
		canifisSidebar.addText("Stay on Canifis Rooftop Course for fastest spawn of Mark of Grace until 60 Agility, then go directly to Seer's Village");
		canifisSidebar.addSubSteps(climbTallTree, jumpFirstGap, jumpSecondGap, jumpThirdGap, jumpFourthGap, vaultPoleVault, jumpFifthGap, jumpSixthGap, canifisStep);

	}

	@Override
	protected PanelDetails getPanelDetails()
	{
		canifisPanels = new PanelDetails("40 - 50/60: Canifis", Collections.singletonList(canifisSidebar)
		);
		canifisPanels.setLockingStep(this.canifisStep);
		return canifisPanels;
	}

}
