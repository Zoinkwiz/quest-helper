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
package com.questhelper.quests.theforsakentower;

import com.google.inject.Inject;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.OwnerStep;
import com.questhelper.steps.QuestStep;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class JugPuzzle extends QuestStep implements OwnerStep
{
	private static final Pattern JUG_VALUES_MATCHER = Pattern.compile("^You add ([0-9]) gallons* of coolant to your ([0-9])-gallon jug.(?: It now contains ([0-9]) gallons* of coolant[.])*(?: Your ([0-9])-gallon jug is left(?: (empty)| with ([0-9]) gallons* of coolant))*");
	private static final Pattern JUG_FILLED = Pattern.compile("^You fill up your ([0-9])-gallon jug");
	private static final Pattern JUG_EMPTIED = Pattern.compile("^You empty the ([0-9])-gallon jug");
	private static final Pattern JUG_CHECKED = Pattern.compile("^The ([0-9])-gallon jug(?: contains ([0-9]) gallons* of coolant| is empty)");

	@Inject
	protected EventBus eventBus;

	@Inject
	protected Client client;

	protected QuestStep currentStep;

	ItemRequirement tinderbox, fiveGallon, eightGallon;

	Requirement missingTinderbox, hasFilledWithFuel, inFirstFloor, inSecondFloor, inBasement;

	DetailedQuestStep syncStep, searchCupboardTinderbox, searchCupboardJug, fill5Gallon, use5GallonOn8, fill5Gallon2, use5GallonOn82, empty8Gallon, use5GallonOn83, fill5Gallon3, use5GallonOn84, fill5Gallon4, use5GallonOn85, use5GallonOnFurnace,
		lightFurnace, restartPuzzle, goUpToGroundFloor, goDownToGroundFloor, goDownToFirstFloor;

	Zone firstFloor, secondFloor, basement;

	private final HashMap<String, Integer> jugs = new HashMap<>();

	public JugPuzzle(QuestHelper questHelper)
	{
		super(questHelper, "");
		jugs.put("5", -1);
		jugs.put("8", -1);

		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	protected void updateSteps()
	{
		Widget widget = client.getWidget(WidgetInfo.DIALOG_SPRITE_TEXT);

		if (widget != null)
		{
			String text = widget.getText().replace("<br>", " ");
			Matcher jugOnJugMatcher = JUG_VALUES_MATCHER.matcher(text);
			Matcher jugEmptiedMatcher = JUG_EMPTIED.matcher(text);
			Matcher jugFilledMatcher = JUG_FILLED.matcher(text);
			Matcher jugCheckedMatcher = JUG_CHECKED.matcher(text);
			if (jugOnJugMatcher.find())
			{
				String targetJug = jugOnJugMatcher.group(2);
				String sourceJug = jugOnJugMatcher.group(4);
				Integer amountAdded = Integer.parseInt(jugOnJugMatcher.group(1));

				if (jugOnJugMatcher.group(3) != null)
				{
					jugs.put(targetJug, Integer.parseInt(jugOnJugMatcher.group(3)));
				}
				else
				{
					jugs.put(targetJug, amountAdded);
				}

				if (jugOnJugMatcher.group(5) != null)
				{
					jugs.put(sourceJug, 0);
				}
				else
				{
					jugs.put(sourceJug, Integer.parseInt(jugOnJugMatcher.group(6)));
				}
			}
			else if (jugEmptiedMatcher.find())
			{
				jugs.put(jugEmptiedMatcher.group(1), 0);
			}
			else if (jugFilledMatcher.find())
			{
				jugs.put(jugFilledMatcher.group(1), Integer.parseInt(jugFilledMatcher.group(1)));
			}
			else if (jugCheckedMatcher.find())
			{
				if (jugCheckedMatcher.group(2) == null)
				{
					jugs.put(jugCheckedMatcher.group(1), 0);
				}
				else
				{
					jugs.put(jugCheckedMatcher.group(1), Integer.parseInt(jugCheckedMatcher.group(2)));
				}
			}
		}

		if (!fiveGallon.check(client))
		{
			jugs.put("5", 0);
		}

		if (!eightGallon.check(client))
		{
			jugs.put("8", 0);
		}


		if (inBasement.check(client))
		{
			startUpStep(goUpToGroundFloor);
		}
		else if (inFirstFloor.check(client))
		{
			startUpStep(goDownToGroundFloor);
		}
		else if (inSecondFloor.check(client))
		{
			startUpStep(goDownToFirstFloor);
		}
		else if (missingTinderbox.check(client))
		{
			startUpStep(searchCupboardTinderbox);
		}
		else if (hasFilledWithFuel.check(client))
		{
			startUpStep(lightFurnace);
		}
		else if (!fiveGallon.check(client) || !eightGallon.check(client))
		{
			startUpStep(searchCupboardJug);
		}
		else if (jugs.get("5") == -1 && jugs.get("8") == -1)
		{
			startUpStep(syncStep);
		}
		else if (jugs.get("5") == 0 && jugs.get("8") == 0)
		{
			startUpStep(fill5Gallon);
		}
		else if (jugs.get("5") == 5 && jugs.get("8") == 0)
		{
			startUpStep(use5GallonOn8);
		}
		else if (jugs.get("5") == 0 && jugs.get("8") == 5)
		{
			startUpStep(fill5Gallon2);
		}
		else if (jugs.get("5") == 5 && jugs.get("8") == 5)
		{
			startUpStep(use5GallonOn82);
		}
		else if (jugs.get("5") == 2 && jugs.get("8") == 8)
		{
			startUpStep(empty8Gallon);
		}
		else if (jugs.get("5") == 2 && jugs.get("8") == 0)
		{
			startUpStep(use5GallonOn83);
		}
		else if (jugs.get("5") == 0 && jugs.get("8") == 2)
		{
			startUpStep(fill5Gallon3);
		}
		else if (jugs.get("5") == 5 && jugs.get("8") == 2)
		{
			startUpStep(use5GallonOn84);
		}
		else if (jugs.get("5") == 0 && jugs.get("8") == 7)
		{
			startUpStep(fill5Gallon4);
		}
		else if (jugs.get("5") == 5 && jugs.get("8") == 7)
		{
			startUpStep(use5GallonOn85);
		}
		else if (jugs.get("5") == 4)
		{
			startUpStep(use5GallonOnFurnace);
		}
		else
		{
			startUpStep(restartPuzzle);
		}
	}

	protected void startUpStep(QuestStep step)
	{
		if (currentStep == null)
		{
			currentStep = step;
			eventBus.register(currentStep);
			currentStep.startUp();
			return;
		}

		if (!step.equals(currentStep))
		{
			shutDownStep();
			eventBus.register(step);
			step.startUp();
			currentStep = step;
		}
	}

	protected void shutDownStep()
	{
		if (currentStep != null)
		{
			eventBus.unregister(currentStep);
			currentStep.shutDown();
			currentStep = null;
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin,Requirement ... requirements)
	{
		if (currentStep != null)
		{
			currentStep.makeOverlayHint(panelComponent, plugin, requirements);
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWorldArrowOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldArrowOverlayHint(graphics, plugin);
		}
	}

	@Override
	public void makeWorldLineOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		if (currentStep != null)
		{
			currentStep.makeWorldLineOverlayHint(graphics, plugin);
		}
	}

	@Override
	public QuestStep getActiveStep()
	{
		if (currentStep != null)
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
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		fiveGallon = new ItemRequirement("5-gallon jug", ItemID._5GALLON_JUG);
		fiveGallon.setHighlightInInventory(true);
		eightGallon = new ItemRequirement("8-gallon jug", ItemID._8GALLON_JUG);
		eightGallon.setHighlightInInventory(true);
	}

	private void setupZones()
	{
		basement = new Zone(new WorldPoint(1374, 10217, 0), new WorldPoint(1389, 10231, 0));
		firstFloor = new Zone(new WorldPoint(1376, 3817, 1), new WorldPoint(1388, 3829, 1));
		secondFloor = new Zone(new WorldPoint(1377, 3821, 2), new WorldPoint(1386, 3828, 2));
	}

	private void setupConditions()
	{
		missingTinderbox = new ItemRequirements(LogicType.NAND, tinderbox);
		hasFilledWithFuel = new VarbitRequirement(7798, 3);
		inFirstFloor = new ZoneRequirement(firstFloor);
		inSecondFloor = new ZoneRequirement(secondFloor);
		inBasement = new ZoneRequirement(basement);
	}

	private void setupSteps()
	{
		syncStep = new DetailedQuestStep(getQuestHelper(), "Please check both the jugs to continue.");
		searchCupboardTinderbox = new ObjectStep(getQuestHelper(), ObjectID.CUPBOARD_33515, new WorldPoint(1381, 3829, 0), "Search the cupboard on the north wall for a tinderbox.");
		searchCupboardJug = new ObjectStep(getQuestHelper(), ObjectID.CUPBOARD_33514, new WorldPoint(1378, 3826, 0), "Search the cupboard in the south east corner of the north room for a 5 and an 8 gallon jug.");
		searchCupboardJug.addDialogStep("Take both.");
		fill5Gallon = new ObjectStep(getQuestHelper(), NullObjectID.NULL_34593, new WorldPoint(1377, 3828, 0), "Fill the 5-gallon jug on the Coolant Dispenser.", fiveGallon);
		fill5Gallon.addDialogStep("5-gallon jug.");

		use5GallonOn8 = new DetailedQuestStep(getQuestHelper(), "Use the 5-gallon jug on the 8-gallon jug.", fiveGallon, eightGallon);

		fill5Gallon2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_34593, new WorldPoint(1377, 3828, 0), "Fill the 5-gallon jug on the Coolant Dispenser.", fiveGallon);
		fill5Gallon2.addDialogStep("5-gallon jug.");
		use5GallonOn82 = new DetailedQuestStep(getQuestHelper(), "Use the 5-gallon jug on the 8-gallon jug.", fiveGallon, eightGallon);
		empty8Gallon = new DetailedQuestStep(getQuestHelper(), "Check the 8-gallon jug and empty it.");
		use5GallonOn83 = new DetailedQuestStep(getQuestHelper(), "Use the 5-gallon jug on the 8-gallon jug.", fiveGallon, eightGallon);
		fill5Gallon3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_34593, new WorldPoint(1377, 3828, 0), "Fill the 5-gallon jug on the Coolant Dispenser.", fiveGallon);
		fill5Gallon3.addDialogStep("5-gallon jug.");
		use5GallonOn84 = new DetailedQuestStep(getQuestHelper(), "Use the 5-gallon jug on the 8-gallon jug.", fiveGallon, eightGallon);
		fill5Gallon4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_34593, new WorldPoint(1377, 3828, 0), "Fill the 5-gallon jug on the Coolant Dispenser.", fiveGallon);
		fill5Gallon4.addDialogStep("5-gallon jug.");
		use5GallonOn85 = new DetailedQuestStep(getQuestHelper(), "Use the 5-gallon jug on the 8-gallon jug.", fiveGallon, eightGallon);
		use5GallonOnFurnace = new ObjectStep(getQuestHelper(), NullObjectID.NULL_34594, new WorldPoint(1383, 3829, 0),
			"Use the 5-gallon jug, which should contain 4 gallons, on the Furnace Coolant.", fiveGallon);
		restartPuzzle = new DetailedQuestStep(getQuestHelper(), "Unknown puzzle state. Empty both the jugs to continue.");
		lightFurnace = new ObjectStep(getQuestHelper(), NullObjectID.NULL_34591, new WorldPoint(1385, 3829, 0), "Light the furnace.", tinderbox);

		goDownToFirstFloor = new ObjectStep(getQuestHelper(), ObjectID.LADDER_33485, new WorldPoint(1382, 3827, 2), "Go down from the top floor.");
		goDownToGroundFloor = new ObjectStep(getQuestHelper(), ObjectID.STAIRCASE_33552, new WorldPoint(1378, 3825, 1), "Go down to the ground floor.");
		goUpToGroundFloor = new ObjectStep(getQuestHelper(), ObjectID.LADDER_33484, new WorldPoint(1382, 10229, 0), "Leave the tower's basement.");

	}

	public List<PanelDetails> panelDetails()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails furnacePanel = new PanelDetails("Furnace puzzle",
			Arrays.asList(searchCupboardTinderbox, searchCupboardJug, fill5Gallon, use5GallonOn8, fill5Gallon2, use5GallonOn82, empty8Gallon, use5GallonOn83,
				fill5Gallon3, use5GallonOn84, fill5Gallon4, use5GallonOn85, use5GallonOnFurnace, lightFurnace));
		furnacePanel.setLockingStep(this);
		allSteps.add(furnacePanel);
		return allSteps;
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(syncStep, searchCupboardTinderbox, searchCupboardJug, fill5Gallon, use5GallonOn8, fill5Gallon2, use5GallonOn82, empty8Gallon, use5GallonOn83,
			fill5Gallon3, use5GallonOn84, fill5Gallon4, use5GallonOn85, use5GallonOnFurnace, restartPuzzle, lightFurnace, goUpToGroundFloor, goDownToFirstFloor, goDownToGroundFloor);
	}
}
