/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.helpers.quests.theforsakentower;

import com.google.inject.Inject;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AltarPuzzle extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	ItemRequirement ring1, ring2, ring3, ring4;

	Zone secondFloor, floor1, basement;

	Requirement inSecondFloor, inFloor1, inBasement;

	QuestStep goUpLadder, goUpStairs, goUpToSecondFloor, restartStep, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15;

	ArrayList<QuestStep> rebalanceW;
	ArrayList<QuestStep> rebalanceE;
	ArrayList<QuestStep> rebalanceC;

	List<QuestStep> moves = new ArrayList<>();

	public AltarPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		if (inBasement.check(client))
		{
			startUpStep(goUpLadder);
		}
		else if (inFloor1.check(client))
		{
			startUpStep(goUpToSecondFloor);
		}
		else if (inSecondFloor.check(client))
		{
			int currentW = client.getVarbitValue(VarbitID.LOVAQUEST_PYLON_1);
			int currentC = client.getVarbitValue(VarbitID.LOVAQUEST_PYLON_2);
			int currentE = client.getVarbitValue(VarbitID.LOVAQUEST_PYLON_3);

			if (currentW == 15)
			{
				startUpStep(rebalanceW.get(0));
			}
			else if (currentW == 14 && currentC == 0 && currentE == 0)
			{
				startUpStep(rebalanceE.get(0));
			}

			/* W to C */
			else if (currentW == 14 && currentC == 0 && currentE == 1)
			{
				startUpStep(rebalanceW.get(1));
			}
			else if (currentW == 12 && currentC == 0 && currentE == 1)
			{
				startUpStep(rebalanceC.get(0));
			}

			/* E to C */
			else if (currentW == 12 && currentC == 2 && currentE == 1)
			{
				startUpStep(rebalanceE.get(1));
			}
			else if (currentW == 12 && currentC == 2 && currentE == 0)
			{
				startUpStep(rebalanceC.get(1));
			}

			/* W to E */
			else if (currentW == 12 && currentC == 3 && currentE == 0)
			{
				startUpStep(rebalanceW.get(2));
			}
			else if (currentW == 8 && currentC == 3 && currentE == 0)
			{
				startUpStep(rebalanceE.get(2));
			}

			/* C to W */
			else if (currentW == 8 && currentC == 3 && currentE == 4)
			{
				startUpStep(rebalanceC.get(2));
			}
			else if (currentW == 8 && currentC == 2 && currentE == 4)
			{
				startUpStep(rebalanceW.get(3));
			}

			/* C to E */
			else if (currentW == 9 && currentC == 2 && currentE == 4)
			{
				startUpStep(rebalanceC.get(3));
			}
			else if (currentW == 9 && currentC == 0 && currentE == 4)
			{
				startUpStep(rebalanceE.get(3));
			}

			/* W to E */
			else if (currentW == 9 && currentC == 0 && currentE == 6)
			{
				startUpStep(rebalanceW.get(4));
			}
			else if (currentW == 8 && currentC == 0 && currentE == 6)
			{
				startUpStep(rebalanceE.get(4));
			}

			/* W to C */
			else if (currentW == 8 && currentC == 0 && currentE == 7)
			{
				startUpStep(rebalanceW.get(5));
			}
			else if (currentW == 0 && currentC == 0 && currentE == 7)
			{
				startUpStep(rebalanceC.get(4));
			}

			/* E to C */
			else if (currentW == 0 && currentC == 8 && currentE == 7)
			{
				startUpStep(rebalanceE.get(5));
			}
			else if (currentW == 0 && currentC == 8 && currentE == 6)
			{
				startUpStep(rebalanceC.get(5));
			}

			/* E to W */
			else if (currentW == 0 && currentC == 9 && currentE == 6)
			{
				startUpStep(rebalanceE.get(6));
			}
			else if (currentW == 0 && currentC == 9 && currentE == 4)
			{
				startUpStep(rebalanceW.get(6));
			}

			/* C to W */
			else if (currentW == 2 && currentC == 9 && currentE == 4)
			{
				startUpStep(rebalanceC.get(6));
			}
			else if (currentW == 2 && currentC == 8 && currentE == 4)
			{
				startUpStep(rebalanceW.get(7));
			}

			/* E to C */
			else if (currentW == 3 && currentC == 8 && currentE == 4)
			{
				startUpStep(rebalanceE.get(7));
			}
			else if (currentW == 3 && currentC == 8 && currentE == 0)
			{
				startUpStep(rebalanceC.get(7));
			}

			/* W to E */
			else if (currentW == 3 && currentC == 12 && currentE == 0)
			{
				startUpStep(rebalanceW.get(8));
			}
			else if (currentW == 2 && currentC == 12 && currentE == 0)
			{
				startUpStep(rebalanceE.get(8));
			}

			/* W to C */
			else if (currentW == 2 && currentC == 12 && currentE == 1)
			{
				startUpStep(rebalanceW.get(9));
			}
			else if (currentW == 0 && currentC == 12 && currentE == 1)
			{
				startUpStep(rebalanceC.get(8));
			}

			/* E to C */
			else if (currentW == 0 && currentC == 14 && currentE == 1)
			{
				startUpStep(rebalanceE.get(9));
			}
			else if (currentW == 0 && currentC == 14 && currentE == 0)
			{
				startUpStep(rebalanceC.get(9));
			}


			else
			{
				startUpStep(restartStep);
			}
		}
		else
		{
			startUpStep(goUpStairs);
		}
	}

	private void setupItemRequirements()
	{
		ring1 = new ItemRequirement("Energy disk (level 1)", ItemID.LOVAQUEST_DISK_1);
		ring2 = new ItemRequirement("Energy disk (level 2)", ItemID.LOVAQUEST_DISK_2);
		ring3 = new ItemRequirement("Energy disk (level 3)", ItemID.LOVAQUEST_DISK_3);
		ring4 = new ItemRequirement("Energy disk (level 4)", ItemID.LOVAQUEST_DISK_4);
	}

	private void setupConditions()
	{
		inSecondFloor = new ZoneRequirement(secondFloor);
		inFloor1 = new ZoneRequirement(floor1);
		inBasement = new ZoneRequirement(basement);
	}

	private void setupZones()
	{
		secondFloor = new Zone(new WorldPoint(1377, 3821, 2), new WorldPoint(1386, 3828, 2));
		floor1 = new Zone(new WorldPoint(1376, 3817, 1), new WorldPoint(1388, 3829, 1));
		basement = new Zone(new WorldPoint(1374, 10217, 0), new WorldPoint(1389, 10231, 0));
	}

	@Override
	protected void setupSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();

		rebalanceW = new ArrayList<>();
		rebalanceC = new ArrayList<>();
		rebalanceE = new ArrayList<>();

		goUpLadder = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_DUNGEON_EXIT, new WorldPoint(1382, 10229, 0), "Leave the tower's basement.");
		goUpStairs = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_SPIRAL_STAIRS_M, new WorldPoint(1378, 3825, 0), "Climb up the staircase to the tower's 1st floor.");
		goUpToSecondFloor = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_LADDER_UP, new WorldPoint(1382, 3827, 1), "Climb up the ladder to the top floor.");
		for (int i = 0; i < 10; i++)
		{
			rebalanceW.add(new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_PYLON_1, new WorldPoint(1380, 3824, 2), "Rebalance the west pylon."));
			rebalanceC.add(new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_PYLON_2, new WorldPoint(1382, 3824, 2), "Rebalance the central pylon."));
			rebalanceE.add(new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_PYLON_3, new WorldPoint(1384, 3824, 2), "Rebalance the east pylon."));
		}
		m1 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the east pylon.");
		m1.addSubSteps(rebalanceW.get(0), rebalanceE.get(0));
		m2 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the centre pylon.");
		m2.addSubSteps(rebalanceW.get(1), rebalanceC.get(0));
		m3 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the east pylon to the centre pylon.");
		m3.addSubSteps(rebalanceE.get(1), rebalanceC.get(1));
		m4 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the east pylon.");
		m4.addSubSteps(rebalanceW.get(2), rebalanceE.get(2));
		m5 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the centre pylon to the west pylon.");
		m5.addSubSteps(rebalanceC.get(2), rebalanceW.get(3));
		m6 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the centre pylon to the east pylon.");
		m6.addSubSteps(rebalanceC.get(3), rebalanceE.get(3));
		m7 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the east pylon.");
		m7.addSubSteps(rebalanceW.get(4), rebalanceE.get(4));
		m8 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the centre pylon.");
		m8.addSubSteps(rebalanceW.get(5), rebalanceC.get(4));
		m9 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the east pylon to the centre pylon.");
		m9.addSubSteps(rebalanceE.get(5), rebalanceC.get(5));
		m10 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the east pylon to the west pylon.");
		m10.addSubSteps(rebalanceE.get(6), rebalanceW.get(6));
		m11 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the centre pylon to the west pylon.");
		m11.addSubSteps(rebalanceC.get(6), rebalanceW.get(7));
		m12 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the east pylon to the centre pylon.");
		m12.addSubSteps(rebalanceE.get(7), rebalanceC.get(7));
		m13 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the east pylon.");
		m13.addSubSteps(rebalanceW.get(8), rebalanceE.get(8));
		m14 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the west pylon to the centre pylon.");
		m14.addSubSteps(rebalanceW.get(9), rebalanceC.get(8));
		m15 = new DetailedQuestStep(getQuestHelper(), "Move a disc from the east pylon to the centre pylon.");
		m15.addSubSteps(rebalanceE.get(9), rebalanceC.get(9));

		moves = new ArrayList<>(Arrays.asList(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15));
		moves.replaceAll(step -> new PuzzleWrapperStep(getQuestHelper(), step, "Solve the altar puzzle."));
		rebalanceC.replaceAll(step -> new PuzzleWrapperStep(getQuestHelper(), step, "Solve the altar puzzle."));
		rebalanceW.replaceAll(step -> new PuzzleWrapperStep(getQuestHelper(), step, "Solve the altar puzzle."));
		rebalanceE.replaceAll(step -> new PuzzleWrapperStep(getQuestHelper(), step, "Solve the altar puzzle."));

		restartStep = new PuzzleWrapperStep(getQuestHelper(), new DetailedQuestStep(getQuestHelper(), "Unknown state. Restart the puzzle to start again."));
	}

	public List<PanelDetails> panelDetails()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails altarPuzzle = new PanelDetails("Altar puzzle",
			new ArrayList<>(List.of(goUpToSecondFloor)));
		moves.forEach((altarPuzzle::addSteps));
		altarPuzzle.setLockingStep(this);
		allSteps.add(altarPuzzle);
		return allSteps;
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		List<QuestStep> steps = new ArrayList<>();
		steps.addAll(rebalanceC);
		steps.addAll(rebalanceW);
		steps.addAll(rebalanceE);
		steps.addAll(Arrays.asList(goUpLadder, goUpStairs, goUpToSecondFloor, restartStep));

		return steps;
	}
}
