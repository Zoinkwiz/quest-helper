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
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PotionPuzzle extends DetailedOwnerStep
{
	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	// Potion 1
	private static final Pattern LINE1 = Pattern.compile("^(.*) is directly left of");
	// Potion 2, potion 3
	private static final Pattern LINE2 = Pattern.compile("^(.*) is next to (.*)[.]");
	// Potion 5, potion 4
	private static final Pattern LINE3 = Pattern.compile("^(.*) is directly right of (.*)[.]");

	private static final String CLEANSING_FLUID = "Cleansing fluid";

	private boolean fluidFound;
	private int correctFluid = -1;

	protected QuestStep currentStep;

	ItemRequirement oldNotes, fluid1, fluid2, fluid3, fluid4, fluid5;

	ItemRequirement[] fluids;

	Requirement inFirstFloor, inBasement, triedToActivate, cleanedRefinery, inSecondFloor;

	Requirement[] hasFluids;

	DetailedQuestStep goUpLadder, goUpStairs, goDownToFirstFloor, searchPotionCupboard, inspectRefinery, readNote, activateRefinery;

	ObjectStep useFluidOnRefineryRealStep, getFluidRealStep;

	QuestStep getFluid, useFluidOnRefinery;

	Zone firstFloor, basement, secondFloor;

	public PotionPuzzle(QuestHelper questHelper)
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
		else if (inSecondFloor.check(client))
		{
			startUpStep(goDownToFirstFloor);
		}
		else if (inFirstFloor.check(client))
		{
			if (cleanedRefinery.check(client))
			{
				startUpStep(activateRefinery);
			}
			else if (!triedToActivate.check(client))
			{
				startUpStep(inspectRefinery);
			}
			else if (correctFluid != -1)
			{
				if (!fluidFound)
				{
					getFluidRealStep.addWidgetChoice(correctFluid-1, 187, 3);
					getFluidRealStep.setText("Take Fluid " + correctFluid + " from the table.");

					useFluidOnRefineryRealStep.addRequirement(fluids[correctFluid]);
					useFluidOnRefineryRealStep.addIcon(fluids[correctFluid].getId());

					fluidFound = true;
				}

				if (hasFluids[correctFluid].check(client))
				{
					startUpStep(useFluidOnRefinery);
				}
				else
				{
					startUpStep(getFluid);
				}
			}
			else if (oldNotes.check(client))
			{
				startUpStep(readNote);
			}
			else
			{
				startUpStep(searchPotionCupboard);
			}
		}
		else
		{
			startUpStep(goUpStairs);
		}
	}

	@Override
	public QuestStep getActiveStep()
	{
		if (currentStep != null && currentStep != this)
		{
			return currentStep.getActiveStep();
		}
		else
		{
			return this;
		}
	}

	private void setupItemRequirements()
	{
		oldNotes = new ItemRequirement("Old notes", ItemID.LOVAQUEST_FLUID_NOTE);
		oldNotes.setHighlightInInventory(true);
		fluid1 = new ItemRequirement("Unknown fluid 1", ItemID.LOVAQUEST_CLEANSING_FLUID_1);
		fluid1.setHighlightInInventory(true);
		fluid2 = new ItemRequirement("Unknown fluid 2", ItemID.LOVAQUEST_CLEANSING_FLUID_2);
		fluid2.setHighlightInInventory(true);
		fluid3 = new ItemRequirement("Unknown fluid 3", ItemID.LOVAQUEST_CLEANSING_FLUID_3);
		fluid3.setHighlightInInventory(true);
		fluid4 = new ItemRequirement("Unknown fluid 4", ItemID.LOVAQUEST_CLEANSING_FLUID_4);
		fluid4.setHighlightInInventory(true);
		fluid5 = new ItemRequirement("Unknown fluid 5", ItemID.LOVAQUEST_CLEANSING_FLUID_5);
		fluid5.setHighlightInInventory(true);

		fluids = new ItemRequirement[]{null, fluid1, fluid2, fluid3, fluid4, fluid5};
	}

	private void setupConditions()
	{
		inFirstFloor = new ZoneRequirement(firstFloor);
		inSecondFloor = new ZoneRequirement(secondFloor);
		inBasement = new ZoneRequirement(basement);

		triedToActivate = new VarbitRequirement(VarbitID.LOVAQUEST_REFINERY, 2);
		cleanedRefinery = new VarbitRequirement(VarbitID.LOVAQUEST_REFINERY, 3);
		hasFluids = new Requirement[]{null, fluid1, fluid2, fluid3, fluid4, fluid5 };
	}

	private void setupZones()
	{
		basement = new Zone(new WorldPoint(1374, 10217, 0), new WorldPoint(1389, 10231, 0));
		firstFloor = new Zone(new WorldPoint(1376, 3817, 1), new WorldPoint(1388, 3829, 1));
		secondFloor = new Zone(new WorldPoint(1377, 3821, 2), new WorldPoint(1386, 3828, 2));
	}

	@Override
	protected void setupSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();

		goUpLadder = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_DUNGEON_EXIT, new WorldPoint(1382, 10229, 0), "Leave the tower's basement.");
		goUpStairs = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_SPIRAL_STAIRS_M, new WorldPoint(1378, 3825, 0), "Go to the tower's 1st floor.");
		goDownToFirstFloor = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_LADDER_DOWN, new WorldPoint(1382, 3827, 2), "Go down from the top floor.");
		goUpStairs.addSubSteps(goUpLadder, goDownToFirstFloor);
		searchPotionCupboard = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_SHELVES_NOTES, new WorldPoint(1387, 3820, 1), "Search the cupboard on the east wall.");
		inspectRefinery = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_REFINERY, new WorldPoint(1382, 3819, 1), "Inspect the refinery.");
		inspectRefinery.addDialogStep("Yes.");
		readNote = new DetailedQuestStep(getQuestHelper(), "Read the old notes.", oldNotes);
		getFluidRealStep = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_FLUID_TABLE, new WorldPoint(1382, 3826, 1), "Attempt to take the correct fluid from the table.");
		getFluid = new PuzzleWrapperStep(getQuestHelper(), getFluidRealStep, getFluidRealStep.copy());
		useFluidOnRefineryRealStep = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_REFINERY, new WorldPoint(1382, 3819, 1), "Use the fluid on the refinery.");
		useFluidOnRefinery = new PuzzleWrapperStep(getQuestHelper(), useFluidOnRefineryRealStep, useFluidOnRefineryRealStep.copy());
		useFluidOnRefinery.addDialogStep("Yes.");

		activateRefinery = new ObjectStep(getQuestHelper(), ObjectID.LOVAQUEST_TOWER_REFINERY, new WorldPoint(1382, 3819, 1), "Activate the refinery.");
		activateRefinery.addDialogStep("Yes.");
	}

	public List<PanelDetails> panelDetails()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails potionPanel = new PanelDetails("Potion puzzle",
			Arrays.asList(goUpStairs, inspectRefinery, searchPotionCupboard, readNote, getFluid, useFluidOnRefinery, activateRefinery));
		potionPanel.setLockingStep(this);
		allSteps.add(potionPanel);
		return allSteps;
	}


	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(goUpLadder, goUpStairs, goDownToFirstFloor, inspectRefinery, searchPotionCupboard, readNote, getFluid, useFluidOnRefinery, activateRefinery);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == 291)
		{
			Widget line1Widget = client.getWidget(InterfaceID.Note.LINE5);
			if (line1Widget != null)
			{
				Matcher matcher = LINE1.matcher(line1Widget.getText());
				if (matcher.find())
				{
					if (matcher.group(1).equals(CLEANSING_FLUID))
					{
						correctFluid = 1;
					}
				}
			}
			Widget line2Widget = client.getWidget(InterfaceID.Note.LINE6);
			if (line2Widget != null)
			{
				Matcher matcher = LINE2.matcher(line2Widget.getText());
				if (matcher.find())
				{
					if (matcher.group(1).equals(CLEANSING_FLUID))
					{
						correctFluid = 2;
					}
					else if (matcher.group(2).equals(CLEANSING_FLUID))
					{
						correctFluid = 3;
					}
				}
			}
			Widget line3Widget = client.getWidget(InterfaceID.Note.LINE7);
			if (line3Widget != null)
			{
				Matcher matcher = LINE3.matcher(line3Widget.getText());
				if (matcher.find())
				{
					if (matcher.group(1).equals(CLEANSING_FLUID))
					{
						correctFluid = 5;
					}
					else if (matcher.group(2).equals(CLEANSING_FLUID))
					{
						correctFluid = 4;
					}
				}
			}
		}
	}
}
