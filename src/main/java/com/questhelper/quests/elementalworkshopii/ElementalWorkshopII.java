/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.elementalworkshopii;


import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.WidgetModelRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ELEMENTAL_WORKSHOP_II
)
public class ElementalWorkshopII extends BasicQuestHelper
{
	ItemRequirement pickaxe, hammer, coal, batteredKey;

	ItemRequirement camelotTeleport, digsiteTeleport;

	ItemRequirement elementalOre, elementalBar, mindBar, primedBar, beatenBook, scroll, key, craneSchematic, claw,
		smallCog, mediumCog, largeCog, pipe;

	Requirement magic20;

	Requirement inWorkshop, inMindWorkshop, onCatwalk, earthNearby, elementalOreNearby,
		has2Ores, has2Bars, hasSmallCog, hasMediumCog, hasLargeCog, hasPipe, hasCogsAndPipe, smallCogPlaced,
		mediumCogPlaced, largeCogPlaced, inBasement;

	Requirement craneLowered, repairedClaw, inPipePuzzle, sortedPipes, repairedPipe;
	Requirement placedBar, craneRaised, craneAboveLava, craneInLava, barHotOnJig, craneHoldingBar, barUnderPress,
		barOutsideTank, barInTunnel, craneHoldingHotBar, flatHotBarOnJig, barOutsideLava, airCoolFlatBarOnJig;
	Requirement tankOpen, tankClosed, grabberOut, grabberInWithHotFlatBarDoorOpen, waterInOpen, waterInClosed, waterOutClosed,
		waterOutOpen, waterInTank, grabberOutWithCoolFlatBar, grabberInWithCoolFlatBarDoorClosed, coolFlatBarOnJig,
		grabberInWithHotFlatBarDoorClosed, grabberInWithCoolFlatBarDoorOpened, grabberOutWithHotFlatBarDoorOpen;
	Requirement fanOff, fanOn;
	Requirement primedBarPlaced, mindBarPlaced;

	Zone workshop, mindWorkshop, catwalk, basement;

	QuestStep searchBookcase, readBook, readScroll;

	QuestStep enterWorkshop, searchMachinery, mineRock, killRock, pickUpOre, forgeBars, openHatch, enterHatch;

	QuestStep takeSchematics, goUpHatch, makeClaw, lowerClaw, repairClaw;

	QuestStep climbStairs, climbDownStairs, openJunctionBox, connectPipes;

	QuestStep getCogsAndPipe, repairPipe, placeSmallCog, placeMediumCog, placeLargeCog;

	QuestStep placeBar, lowerCraneOntoBar, raiseCraneWithBar, rotateCraneToLava, lowerBarIntoLava, raiseBarOutOfLava,
		rotateCraneFromLava, lowerCraneWithBar, raiseCraneFromBar, pullLeverToMoveToPress, lowerPress, pullLeverToMoveToTank,
		pullLeverToOpenTankDoor, turnCorkscrew, turnCorkscrewAgain, pullLeverToCloseTankDoor, turnWestValve,
		turnEastValve, turnEastValveAgain, turnWestValveAgain, pullLeverToOpenTankDoorAgain, turnCorkscrewToRetrieve,
		turnCorkscrewToRetrieveAgain, pullLeverToCloseTankDoorAgain, pullLeverToMoveToFan, pullFanLever,
		pullFanLeverAgain, pullLeverToMoveToLava, pickUpBar;

	QuestStep goDownToBasement, useBarOnGun, operateHat, takeMindBar, goUpFromBasement, makeMindHelmet;

	ConditionalStep getKey, unlockHatch, goRepairCrane, goMakeClaw, goSortTubes, goGetCogsAndPipes, goRepairPipe,
		goPlaceCogs, goMakeMindHelmet;

	ConditionalStep goToWorkshopTopFloor, goToWorkshopMiddleFloor, goToWorkshopBottomFloor, goToWorkshopCatwalk;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, searchBookcase);
		steps.put(1, readBook);
		steps.put(2, readScroll);

		getKey = new ConditionalStep(this, goToWorkshopTopFloor,
			"Search the machinery in the north of the elemental workshop.");
		getKey.addStep(inWorkshop, searchMachinery);
		steps.put(3, getKey);

		unlockHatch = new ConditionalStep(this, goToWorkshopTopFloor,
			"Unlock the hatch the middle of the workshop.", key);
		unlockHatch.addStep(inWorkshop, openHatch);
		steps.put(4, unlockHatch);

		goRepairCrane = new ConditionalStep(this, goToWorkshopMiddleFloor,
			"Go back down to the deeper workshop and use the claw on the crane.");
		goRepairCrane.addStep(new Conditions(inMindWorkshop, craneLowered), repairClaw);
		goRepairCrane.addStep(inMindWorkshop, lowerClaw);

		goMakeClaw = new ConditionalStep(this, goToWorkshopTopFloor,
			"Make a claw from an elemental bar.");
		goMakeClaw.addStep(new Conditions(inWorkshop, has2Bars), makeClaw);
		goMakeClaw.addStep(new Conditions(inWorkshop, has2Ores), forgeBars);
		goMakeClaw.addStep(new Conditions(inWorkshop, elementalOreNearby), pickUpOre);
		goMakeClaw.addStep(new Conditions(inWorkshop, earthNearby), killRock);
		goMakeClaw.addStep(inWorkshop, mineRock);

		goSortTubes = new ConditionalStep(this, goToWorkshopCatwalk, "Sort the pipes in the junction box on the " +
			"catwalk.");
		goSortTubes.addStep(new Conditions(onCatwalk, inPipePuzzle), connectPipes);
		goSortTubes.addStep(onCatwalk, openJunctionBox);

		goGetCogsAndPipes = new ConditionalStep(this, goToWorkshopCatwalk);
		goGetCogsAndPipes.addStep(onCatwalk, getCogsAndPipe);
		goGetCogsAndPipes.addStep(inMindWorkshop, getCogsAndPipe);

		goRepairPipe = new ConditionalStep(this, goToWorkshopCatwalk,
			"Use the pipe on the broken pipe to the north of the catwalk.");
		goRepairPipe.addStep(onCatwalk, repairPipe);

		goPlaceCogs = new ConditionalStep(this, goToWorkshopMiddleFloor,
			"Place cogs on the pegs outside the wind tunnel.");
		goPlaceCogs.addStep(new Conditions(inMindWorkshop, smallCogPlaced, mediumCogPlaced), placeLargeCog);
		goPlaceCogs.addStep(new Conditions(inMindWorkshop, smallCogPlaced), placeMediumCog);
		goPlaceCogs.addStep(inMindWorkshop, placeSmallCog);

		ConditionalStep primingInWorkshop = new ConditionalStep(this, placeBar);
		primingInWorkshop.addStep(new Conditions(airCoolFlatBarOnJig, barOutsideLava), pickUpBar);
		primingInWorkshop.addStep(new Conditions(airCoolFlatBarOnJig, barInTunnel, fanOff), pullLeverToMoveToLava);
		primingInWorkshop.addStep(new Conditions(airCoolFlatBarOnJig, barInTunnel, fanOn), pullFanLeverAgain);
		primingInWorkshop.addStep(new Conditions(coolFlatBarOnJig, barInTunnel, fanOff), pullFanLever);
		primingInWorkshop.addStep(new Conditions(coolFlatBarOnJig, barOutsideTank, tankClosed), pullLeverToMoveToFan);
		primingInWorkshop.addStep(new Conditions(coolFlatBarOnJig, barOutsideTank, tankOpen, grabberOut),
			turnCorkscrewToRetrieveAgain);
		primingInWorkshop.addStep(new Conditions(coolFlatBarOnJig, barOutsideTank, tankOpen),
			pullLeverToCloseTankDoorAgain);

		primingInWorkshop.addStep(new Conditions(grabberOutWithCoolFlatBar), turnCorkscrewToRetrieveAgain);
		primingInWorkshop.addStep(new Conditions(grabberInWithCoolFlatBarDoorOpened), turnCorkscrewToRetrieve);
		primingInWorkshop.addStep(new Conditions(grabberInWithCoolFlatBarDoorClosed, waterInClosed, waterOutOpen), pullLeverToOpenTankDoorAgain);
		primingInWorkshop.addStep(new Conditions(grabberInWithCoolFlatBarDoorClosed, waterInOpen, waterOutOpen), turnWestValveAgain);
		primingInWorkshop.addStep(new Conditions(grabberInWithCoolFlatBarDoorClosed, waterOutClosed), turnEastValveAgain);
		primingInWorkshop.addStep(new Conditions(grabberInWithHotFlatBarDoorClosed, waterInOpen), turnEastValve);
		primingInWorkshop.addStep(new Conditions(grabberInWithHotFlatBarDoorClosed), turnWestValve);
		primingInWorkshop.addStep(new Conditions(grabberInWithHotFlatBarDoorOpen), pullLeverToCloseTankDoor);
		primingInWorkshop.addStep(new Conditions(grabberOutWithHotFlatBarDoorOpen), turnCorkscrewAgain);
		primingInWorkshop.addStep(new Conditions(flatHotBarOnJig, barOutsideTank, grabberOut), turnCorkscrewAgain);
		primingInWorkshop.addStep(new Conditions(flatHotBarOnJig, barOutsideTank, tankOpen), turnCorkscrew);
		primingInWorkshop.addStep(new Conditions(flatHotBarOnJig, barOutsideTank, waterInTank, waterOutClosed), turnEastValve);
		primingInWorkshop.addStep(new Conditions(flatHotBarOnJig, barOutsideTank, waterInOpen), turnWestValve);
		primingInWorkshop.addStep(new Conditions(flatHotBarOnJig, barOutsideTank), pullLeverToOpenTankDoor);
		primingInWorkshop.addStep(new Conditions(flatHotBarOnJig, barUnderPress), pullLeverToMoveToTank);
		primingInWorkshop.addStep(new Conditions(barHotOnJig, barUnderPress), lowerPress);
		primingInWorkshop.addStep(new Conditions(new Conditions(LogicType.OR, barHotOnJig, flatHotBarOnJig,
			coolFlatBarOnJig), barOutsideLava), pullLeverToMoveToPress);

		// Catch case someone's sent the bar around without heating
		primingInWorkshop.addStep(new Conditions(barInTunnel, fanOn), pullFanLeverAgain);
		primingInWorkshop.addStep(barInTunnel, pullLeverToMoveToLava);
		primingInWorkshop.addStep(new Conditions(barOutsideTank, grabberOut), turnCorkscrew);
		primingInWorkshop.addStep(new Conditions(barOutsideTank, tankOpen), pullLeverToCloseTankDoor);
		primingInWorkshop.addStep(barOutsideTank, pullLeverToMoveToFan);
		primingInWorkshop.addStep(barUnderPress, pullLeverToMoveToTank);
		primingInWorkshop.addStep(new Conditions(placedBar, craneInLava), raiseBarOutOfLava);
		primingInWorkshop.addStep(new Conditions(placedBar, craneAboveLava), rotateCraneFromLava);
		primingInWorkshop.addStep(new Conditions(placedBar, craneLowered), raiseCraneWithBar);

		primingInWorkshop.addStep(new Conditions(craneHoldingHotBar, craneLowered), raiseCraneFromBar);
		primingInWorkshop.addStep(new Conditions(craneHoldingHotBar, craneRaised), lowerCraneWithBar);
		primingInWorkshop.addStep(new Conditions(craneHoldingHotBar, craneAboveLava), rotateCraneFromLava);
		primingInWorkshop.addStep(new Conditions(craneHoldingHotBar, craneInLava), raiseBarOutOfLava);
		primingInWorkshop.addStep(new Conditions(craneHoldingBar, craneAboveLava), lowerBarIntoLava);
		primingInWorkshop.addStep(new Conditions(craneHoldingBar, craneRaised), rotateCraneToLava);
		primingInWorkshop.addStep(new Conditions(craneHoldingBar, craneLowered), raiseCraneWithBar);
		primingInWorkshop.addStep(placedBar, lowerCraneOntoBar);


		ConditionalStep primingABar = new ConditionalStep(this, goToWorkshopMiddleFloor);
		primingABar.addStep(inMindWorkshop, primingInWorkshop);

		ConditionalStep goMakeMindBar = new ConditionalStep(this, goToWorkshopBottomFloor);
		goMakeMindBar.addStep(new Conditions(inBasement, mindBarPlaced), takeMindBar);
		goMakeMindBar.addStep(new Conditions(inBasement, primedBarPlaced), operateHat);
		goMakeMindBar.addStep(inBasement, useBarOnGun);

		goMakeMindHelmet = new ConditionalStep(this, goToWorkshopTopFloor, "Make the mind helm.");
		goMakeMindHelmet.addStep(inWorkshop, makeMindHelmet);

		ConditionalStep makingAHelm = new ConditionalStep(this, goToWorkshopMiddleFloor);
		makingAHelm.addStep(mindBar, goMakeMindHelmet);
		makingAHelm.addStep(new Conditions(LogicType.OR, primedBar, primedBarPlaced, mindBarPlaced), goMakeMindBar);
		makingAHelm.addStep(new Conditions(sortedPipes, repairedClaw, repairedPipe, smallCogPlaced, mediumCogPlaced,
			largeCogPlaced), primingABar);
		makingAHelm.addStep(new Conditions(sortedPipes, repairedClaw, hasCogsAndPipe, repairedPipe), goPlaceCogs);
		makingAHelm.addStep(new Conditions(sortedPipes, repairedClaw, hasCogsAndPipe), goRepairPipe);
		makingAHelm.addStep(new Conditions(sortedPipes, repairedClaw), goGetCogsAndPipes);
		makingAHelm.addStep(repairedClaw, goSortTubes);
		makingAHelm.addStep(claw, goRepairCrane);
		makingAHelm.addStep(craneSchematic, goMakeClaw);
		makingAHelm.addStep(inMindWorkshop, takeSchematics);
		steps.put(5, makingAHelm);
		steps.put(6, makingAHelm);
		steps.put(7, makingAHelm);
		steps.put(8, makingAHelm);
		steps.put(9, makingAHelm);
		steps.put(10, makingAHelm);

		return steps;
	}

	public void setupItemRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		hammer = new ItemRequirement("Hammer", ItemCollections.getHammer());
		coal = new ItemRequirement("Coal", ItemID.COAL, 8);
		batteredKey = new ItemRequirement("Battered key", ItemID.BATTERED_KEY);
		batteredKey.setTooltip("You can get another by searching the bookcase in the house south of the elemental " +
			"workshop's entrance");

		camelotTeleport = new ItemRequirement("Camelot teleport", ItemID.CAMELOT_TELEPORT);
		digsiteTeleport = new ItemRequirement("Digsite teleport", ItemCollections.getDigsitePendants());
		digsiteTeleport.addAlternates(ItemID.DIGSITE_TELEPORT);

		elementalOre = new ItemRequirement("Elemental ore", ItemID.ELEMENTAL_ORE);
		elementalOre.addAlternates(ItemID.ELEMENTAL_METAL);
		elementalBar = new ItemRequirement("Elemental metal", ItemID.ELEMENTAL_METAL);
		mindBar = new ItemRequirement("Primed mind bar", ItemID.PRIMED_MIND_BAR);
		primedBar = new ItemRequirement("Primed bar", ItemID.PRIMED_BAR);

		beatenBook = new ItemRequirement("Beaten book", ItemID.BEATEN_BOOK);
		beatenBook.setTooltip("You can get another from a bookcase in the Exam Center");

		scroll = new ItemRequirement("Scroll", ItemID.SCROLL_9721);
		scroll.setTooltip("You can get another by searching the beaten book");

		key = new ItemRequirement("Key", ItemID.KEY_9722);
		key.setTooltip("You can get another from the machinery in the north of the elemental workshop");

		craneSchematic = new ItemRequirement("Crane schematic", ItemID.CRANE_SCHEMATIC);
		claw = new ItemRequirement("Crane claw", ItemID.CRANE_CLAW);
		smallCog = new ItemRequirement("Small cog", ItemID.SMALL_COG);
		mediumCog = new ItemRequirement("Medium cog", ItemID.MEDIUM_COG);
		largeCog = new ItemRequirement("Large cog", ItemID.LARGE_COG);
		pipe = new ItemRequirement("Pipe", ItemID.PIPE);

		magic20 = new SkillRequirement(Skill.MAGIC, 20, true);
	}

	public void setupZones()
	{
		workshop = new Zone(new WorldPoint(2682, 9862, 0), new WorldPoint(2747, 9927, 0));
		mindWorkshop = new Zone(new WorldPoint(1946, 5147, 2), new WorldPoint(1961, 5162, 2));
		catwalk = new Zone(new WorldPoint(1936, 5140, 3), new WorldPoint(1971, 5172, 3));
		basement = new Zone(new WorldPoint(1946, 5129, 0), new WorldPoint(1972, 5164, 0));
	}

	public void setupConditions()
	{
		inWorkshop = new ZoneRequirement(workshop);
		inMindWorkshop = new ZoneRequirement(mindWorkshop);
		onCatwalk = new ZoneRequirement(catwalk);
		inBasement = new ZoneRequirement(basement);

		// Taken book:
		// 2639 0->1
		// 2664 0->2

		// Read scroll:
		// 2640 1

		// Opened hatch:
		// 2641 0->1

		earthNearby = new NpcInteractingRequirement(NpcID.EARTH_ELEMENTAL_1367);
		elementalOreNearby = new ItemOnTileRequirement(elementalOre);
		has2Ores = new ItemRequirements(elementalOre.quantity(2));
		has2Bars = new ItemRequirements(elementalBar.quantity(2));

		repairedClaw = new VarbitRequirement(2644, 1, Operation.GREATER_EQUAL);
		inPipePuzzle = new WidgetModelRequirement(262, 37, 18794);
		sortedPipes = new Conditions(
			new Conditions(LogicType.OR,
				new VarbitRequirement(2646, 5),
				new VarbitRequirement(2647, 5),
				new VarbitRequirement(2648, 5)
			),
			new Conditions(LogicType.OR,
				new VarbitRequirement(2646, 6),
				new VarbitRequirement(2647, 6),
				new VarbitRequirement(2648, 6)
			),
			new Conditions(LogicType.OR,
				new VarbitRequirement(2646, 13),
				new VarbitRequirement(2647, 13),
				new VarbitRequirement(2648, 13)
			)
		);

		repairedPipe = new VarbitRequirement(2650, 1);

		hasSmallCog = new Conditions(LogicType.OR,
			smallCog,
			new VarbitRequirement(2655, 1),
			new VarbitRequirement(2656, 1),
			new VarbitRequirement(2657, 1)
		);
		hasMediumCog = new Conditions(LogicType.OR,
			mediumCog,
			new VarbitRequirement(2655, 2),
			new VarbitRequirement(2656, 2),
			new VarbitRequirement(2657, 2)
		);
		hasLargeCog = new Conditions(LogicType.OR,
			largeCog,
			new VarbitRequirement(2655, 3),
			new VarbitRequirement(2656, 3),
			new VarbitRequirement(2657, 3)
		);
		hasPipe = new Conditions(LogicType.OR,
			repairedPipe,
			pipe
		);

		hasCogsAndPipe = new Conditions(hasSmallCog, hasMediumCog, hasLargeCog, hasPipe);

		smallCogPlaced = new VarbitRequirement(2655, 1);
		mediumCogPlaced = new VarbitRequirement(2656, 2);
		largeCogPlaced = new VarbitRequirement(2657, 3);

		// This potentially relates to varbit 2664
		// Small cog in crate 18614
		// Large cog in crate 18615
		// Medium cog in crate 18616
		// Pipe in crate 18617

		placedBar = new VarbitRequirement(2643, 1);
		barHotOnJig = new VarbitRequirement(2643, 2);
		flatHotBarOnJig = new VarbitRequirement(2643, 3);
		coolFlatBarOnJig = new VarbitRequirement(2643, 4);
		airCoolFlatBarOnJig = new VarbitRequirement(2643, 5);
		craneLowered = new VarbitRequirement(2645, 1);
		craneRaised = new VarbitRequirement(2645, 0);
		craneHoldingBar = new VarbitRequirement(2644, 2);
		craneAboveLava = new VarbitRequirement(2645, 2);
		craneInLava = new VarbitRequirement(2645, 3);
		barOutsideLava = new VarbitRequirement(2642, 0);
		barUnderPress = new VarbitRequirement(2642, 1);
		barOutsideTank = new VarbitRequirement(2642, 2);
		barInTunnel = new VarbitRequirement(2642, 3);
		craneHoldingHotBar = new VarbitRequirement(2644, 3);

		tankClosed = new VarbitRequirement(2653, 0);
		tankOpen = new VarbitRequirement(2653, 1);
		grabberOut = new VarbitRequirement(2653, 2);
		grabberInWithHotFlatBarDoorOpen = new VarbitRequirement(2653, 4);
		grabberInWithHotFlatBarDoorClosed = new VarbitRequirement(2653, 3);
		grabberOutWithHotFlatBarDoorOpen = new VarbitRequirement(2653, 5);
		grabberInWithCoolFlatBarDoorClosed = new VarbitRequirement(2653, 6);
		grabberInWithCoolFlatBarDoorOpened = new VarbitRequirement(2653, 7);
		grabberOutWithCoolFlatBar = new VarbitRequirement(2653, 8);
		waterInOpen = new VarbitRequirement(2651, 1);
		waterInClosed = new VarbitRequirement(2651, 0);
		waterOutClosed = new VarbitRequirement(2652, 0);
		waterOutOpen = new VarbitRequirement(2652, 1);
		waterInTank = new VarbitRequirement(2654, 1);
		fanOff = new VarbitRequirement(2660, 0);
		fanOn = new VarbitRequirement(2660, 1);

		primedBarPlaced = new VarbitRequirement(2662, 1);
		mindBarPlaced = new VarbitRequirement(2662, 2);
	}

	public void setupSteps()
	{
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_17382, new WorldPoint(3367, 3335, 0),
			"Search the marked bookcase in the Exam Centre in the Dig Site.");

		readBook = new DetailedQuestStep(this, "Read the beaten book.", beatenBook.highlighted());
		readScroll = new DetailedQuestStep(this, "Read the scroll.", scroll.highlighted());

		enterWorkshop = new ObjectStep(this, ObjectID.STAIRCASE_3415, new WorldPoint(2711, 3498, 0),
			"Enter the elemental workshop.", batteredKey, beatenBook);

		searchMachinery = new ObjectStep(this, NullObjectID.NULL_18593, new WorldPoint(2715, 9903, 0),
			"");

		openHatch = new ObjectStep(this, NullObjectID.NULL_3413, new WorldPoint(2720, 9891, 0), "");

		enterHatch = new ObjectStep(this, NullObjectID.NULL_3413, new WorldPoint(2720, 9891, 0),
			"Enter the hatch the middle of the workshop.");

		takeSchematics = new ObjectStep(this, ObjectID.SCHEMATIC_CRATE, new WorldPoint(1952, 5148, 2),
			"Take crane schematics from the crate to the south.");
		takeSchematics.addDialogSteps("Crane schematic");

		goUpHatch = new ObjectStep(this, ObjectID.STAIRS_18598, new WorldPoint(1954, 5155, 2),
			"Go up the stairs.");

		mineRock = new NpcStep(this, NpcID.ELEMENTAL_ROCK, new WorldPoint(2703, 9894, 0),
			"You need 2 elemental bars. Mine one of the elemental rocks in the west room, ready to fight a level 35.",
			true, pickaxe);
		killRock = new NpcStep(this, NpcID.EARTH_ELEMENTAL_1367, new WorldPoint(2703, 9897, 0),
			"Kill the rock elemental that appeared.");
		pickUpOre = new ItemStep(this, "Pick up the elemental ore.", elementalOre);
		forgeBars = new ObjectStep(this, NullObjectID.NULL_3410, new WorldPoint(2726, 9875, 0),
			"Use the elemental ores on the furnace in the south room.", elementalOre.quantity(2), coal.quantity(8));
		forgeBars.addIcon(ItemID.ELEMENTAL_ORE);
		makeClaw = new ObjectStep(this, ObjectID.WORKBENCH_3402, new WorldPoint(2717, 9888, 0),
			"Use the bar on one of the workbenches in the central room to make a claw.", elementalBar.highlighted(),
			hammer, batteredKey, craneSchematic);
		makeClaw.addDialogStep("An elemental claw.");
		makeClaw.addIcon(ItemID.ELEMENTAL_METAL);

		lowerClaw = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to lower the claw.");

		Integer[] craneIDs = new Integer[]{ObjectID.OLD_CRANE_18624, ObjectID.OLD_CRANE_18625,
			ObjectID.OLD_CRANE_18626, ObjectID.OLD_CRANE_18627, ObjectID.OLD_CRANE_18628, ObjectID.OLD_CRANE_18629,
			ObjectID.OLD_CRANE_18630, ObjectID.OLD_CRANE_18631, ObjectID.OLD_CRANE_18632, ObjectID.OLD_CRANE_18633,
			ObjectID.OLD_CRANE_18634, ObjectID.OLD_CRANE_18635, ObjectID.OLD_CRANE_18636, ObjectID.OLD_CRANE_18637,
			ObjectID.OLD_CRANE_18638};

		repairClaw = new ObjectStep(this, ObjectID.OLD_CRANE, new WorldPoint(1954, 5145, 2),
			"Use the claw on the crane to the south.", claw.highlighted());
		repairClaw.addIcon(ItemID.CRANE_CLAW);
		((ObjectStep) repairClaw).addAlternateObjects(craneIDs);
		repairClaw.addDialogStep("An elemental claw.");

		climbStairs = new ObjectStep(this, ObjectID.STAIRS_18610, new WorldPoint(1949, 5149, 2),
			"Climb up to the catwalk.");
		climbDownStairs = new ObjectStep(this, ObjectID.STAIRS_18611, new WorldPoint(1949, 5149, 2),
			"Climb down from the catwalk.");

		openJunctionBox = new ObjectStep(this, ObjectID.JUNCTION_BOX, new WorldPoint(1943, 5154, 3),
			"Open the junction box.");

		connectPipes = new ConnectPipes(this);

		getCogsAndPipe = new ObjectStep(this, ObjectID.CRATE_18612,
			"Search the various marked crates on the catwalk and below for 3 cogs and a pipe.",
			smallCog, mediumCog, largeCog, pipe);
		((ObjectStep) getCogsAndPipe).addAlternateObjects(ObjectID.CRATE_18613, ObjectID.CRATES_18614,
			ObjectID.CRATES_18615, ObjectID.CRATE_18616, ObjectID.CRATES_18617, ObjectID.CRATE_18618,
			ObjectID.CRATE_18619);
		((ObjectStep)getCogsAndPipe).setRevalidateObjects(true);

		repairPipe = new ObjectStep(this, NullObjectID.NULL_3414, new WorldPoint(1953, 5168, 3),
			"", pipe.highlighted());
		repairPipe.addIcon(ItemID.PIPE);

		placeSmallCog = new ObjectStep(this, ObjectID.PIN_18665, new WorldPoint(1959, 5157, 2),
			"Place the small cog on the top left peg.", smallCog.highlighted());
		placeMediumCog = new ObjectStep(this, ObjectID.PIN, new WorldPoint(1959, 5157, 2),
			"Place the medium cog on the bottom left peg.", mediumCog.highlighted());
		placeLargeCog = new ObjectStep(this, ObjectID.PIN_18666, new WorldPoint(1959, 5157, 2),
			"Place the large cog on the right peg.", largeCog.highlighted());

		placeBar = new NpcStep(this, NpcID.JIG_CART, new WorldPoint(1954, 5147, 2), "Place the elemental bar on the " +
			"jig cart.", elementalBar.highlighted());
		placeBar.addIcon(ItemID.ELEMENTAL_METAL);
		lowerCraneOntoBar = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to lower the claw.");
		raiseCraneWithBar = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to raise the claw.");
		rotateCraneToLava = new ObjectStep(this, ObjectID.AN_OLD_LEVER, new WorldPoint(1955, 5148, 2),
			"Pull the south east lever to rotate the claw.");
		lowerBarIntoLava = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to lower the claw into the lava.");
		raiseBarOutOfLava = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to raise the claw out of the lava.");
		rotateCraneFromLava = new ObjectStep(this, ObjectID.AN_OLD_LEVER, new WorldPoint(1955, 5148, 2),
			"Pull the south east lever to rotate the claw back.");
		lowerCraneWithBar = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to lower the bar back onto the jig.");
		raiseCraneFromBar = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18622, new WorldPoint(1953, 5148, 2),
			"Pull the south west lever to raise the claw once more.");
		pullLeverToMoveToPress = new ObjectStep(this, ObjectID.LEVER_18620, new WorldPoint(1953, 5151, 2),
			"Pull the central lever to move the jig to the press.");
		lowerPress = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18640, new WorldPoint(1950, 5153, 2),
			"Pull the west lever to move the jig to the press.");
		pullLeverToMoveToTank = new ObjectStep(this, ObjectID.LEVER_18620, new WorldPoint(1953, 5151, 2),
			"Pull the central lever to move the jig to the water tank.");
		pullLeverToOpenTankDoor = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18648, new WorldPoint(1953, 5161, 2),
			"Pull the north lever to open the water tank.");
		turnCorkscrew = new ObjectStep(this, ObjectID.CORKSCREW_LEVER, new WorldPoint(1955, 5161, 2),
			"Turn the corkscrew.");
		turnCorkscrewAgain = new ObjectStep(this, ObjectID.CORKSCREW_LEVER, new WorldPoint(1955, 5161, 2),
			"Turn the corkscrew again.");
		pullLeverToCloseTankDoor = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18648, new WorldPoint(1953, 5161, 2),
			"Pull the north lever to close the water tank.");
		turnWestValve = new ObjectStep(this, ObjectID.WATER_VALVE_18646, new WorldPoint(1949, 5161, 2),
			"Turn the north west water valve.");
		turnEastValve = new ObjectStep(this, ObjectID.WATER_VALVE_18647, new WorldPoint(1957, 5161, 2),
			"Turn the north east water valve.");
		turnEastValveAgain = new ObjectStep(this, ObjectID.WATER_VALVE_18647, new WorldPoint(1957, 5161, 2),
			"Turn the north east water valve again.");
		turnWestValveAgain = new ObjectStep(this, ObjectID.WATER_VALVE_18646, new WorldPoint(1949, 5161, 2),
			"Turn the north west water valve again.");
		pullLeverToOpenTankDoorAgain = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18648, new WorldPoint(1953, 5161, 2),
			"Pull the north lever to open the water tank again.");
		turnCorkscrewToRetrieve = new ObjectStep(this, ObjectID.CORKSCREW_LEVER, new WorldPoint(1955, 5161, 2),
			"Turn the corkscrew.");
		turnCorkscrewToRetrieveAgain = new ObjectStep(this, ObjectID.CORKSCREW_LEVER, new WorldPoint(1955, 5161, 2),
			"Turn the corkscrew once more.");
		pullLeverToCloseTankDoorAgain = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18648, new WorldPoint(1953, 5161, 2),
			"Pull the north lever to close the water tank.");
		pullLeverToMoveToFan = new ObjectStep(this, ObjectID.LEVER_18620, new WorldPoint(1953, 5151, 2),
			"Pull the central lever to move the jig to the wind tunnel.");
		pullFanLever = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18663, new WorldPoint(1959, 5154, 2),
			"Pull the east lever to start the wind tunnel.");
		pullFanLeverAgain = new ObjectStep(this, ObjectID.AN_OLD_LEVER_18663, new WorldPoint(1959, 5154, 2),
			"Pull the east lever to stop the wind tunnel.");
		pullLeverToMoveToLava = new ObjectStep(this, ObjectID.LEVER_18620, new WorldPoint(1953, 5151, 2),
			"Pull the central lever to move the jig back to the start.");
		pickUpBar = new NpcStep(this, NpcID.JIG_CART_5226, new WorldPoint(1954, 5147, 2), "Pick up the primed bar.");

		goDownToBasement = new ObjectStep(this, ObjectID.STAIRWELL, new WorldPoint(1949, 5159, 2),
			"Go down from the priming room.");
		useBarOnGun = new ObjectStep(this, NullObjectID.NULL_18725, new WorldPoint(1962, 5148, 0),
			"Use the primed bar on the extractor gun in the east room.", primedBar.highlighted());
		useBarOnGun.addIcon(ItemID.PRIMED_BAR);
		operateHat = new ObjectStep(this, ObjectID.EXTRACTOR_HAT, new WorldPoint(1962, 5150, 0),
			"Sit in the extractor chair.", magic20);
		takeMindBar = new ObjectStep(this, NullObjectID.NULL_18725, new WorldPoint(1962, 5148, 0),
			"Take the mind bar.");
		goUpFromBasement = new ObjectStep(this, ObjectID.STAIRS_18599, new WorldPoint(1949, 5160, 0),
			"Go up the stairs.");
		makeMindHelmet = new ObjectStep(this, ObjectID.WORKBENCH_3402, new WorldPoint(2717, 9888, 0),
			"Use the mind bar on one of the workbenches in the central room.", mindBar.highlighted(), hammer,
			beatenBook);
		makeMindHelmet.addIcon(ItemID.PRIMED_MIND_BAR);

		goToWorkshopTopFloor = new ConditionalStep(this, enterWorkshop);
		goToWorkshopTopFloor.addStep(inBasement, goUpFromBasement);
		goToWorkshopTopFloor.addStep(onCatwalk, climbDownStairs);
		goToWorkshopTopFloor.addStep(inMindWorkshop, goUpHatch);

		goToWorkshopMiddleFloor = new ConditionalStep(this, enterWorkshop);
		goToWorkshopMiddleFloor.addStep(inBasement, goUpFromBasement);
		goToWorkshopMiddleFloor.addStep(onCatwalk, climbDownStairs);
		goToWorkshopMiddleFloor.addStep(inWorkshop, enterHatch);

		goToWorkshopCatwalk = new ConditionalStep(this, enterWorkshop);
		goToWorkshopCatwalk.addStep(inBasement, goUpFromBasement);
		goToWorkshopCatwalk.addStep(inMindWorkshop, climbStairs);
		goToWorkshopCatwalk.addStep(inWorkshop, enterHatch);

		goToWorkshopBottomFloor = new ConditionalStep(this, enterWorkshop);
		goToWorkshopBottomFloor.addStep(onCatwalk, climbDownStairs);
		goToWorkshopBottomFloor.addStep(inMindWorkshop, goDownToBasement);
		goToWorkshopBottomFloor.addStep(inWorkshop, enterHatch);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pickaxe, hammer, coal, batteredKey);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(digsiteTeleport, camelotTeleport);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("2 Earth elementals (level 35)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.ELEMENTAL_WORKSHOP_I, QuestState.FINISHED));
		reqs.add(new SkillRequirement(Skill.MAGIC, 20, true));
		reqs.add(new SkillRequirement(Skill.SMITHING, 30, true));
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CRAFTING, 7500),
				new ExperienceReward(Skill.SMITHING, 7500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Mind Helmet", ItemID.MIND_HELMET, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to craft and equip Mind Elemental Equipment"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(searchBookcase, readBook, readScroll)));
		allSteps.add(new PanelDetails("Unlocking the Hatch", Arrays.asList(getKey, unlockHatch), batteredKey, pickaxe,
			coal.quantity(8), hammer));
		allSteps.add(new PanelDetails("Repairing the crane", Arrays.asList(takeSchematics, goMakeClaw, goRepairCrane)));
		allSteps.add(new PanelDetails("Repairing the workshop", Arrays.asList(goSortTubes, getCogsAndPipe,
			goRepairPipe, goPlaceCogs)));
		allSteps.add(new PanelDetails("Priming a bar", Arrays.asList(placeBar, lowerCraneOntoBar, raiseCraneWithBar,
			rotateCraneToLava, lowerBarIntoLava, raiseBarOutOfLava, rotateCraneFromLava, lowerCraneWithBar, raiseCraneFromBar,
			pullLeverToMoveToPress, lowerPress, pullLeverToMoveToTank, pullLeverToOpenTankDoor, turnCorkscrew, turnCorkscrewAgain,
			pullLeverToCloseTankDoor, turnWestValve, turnEastValve, turnEastValveAgain, turnWestValveAgain, pullLeverToOpenTankDoorAgain,
			turnCorkscrewToRetrieve, turnCorkscrewToRetrieveAgain, pullLeverToCloseTankDoorAgain, pullLeverToMoveToFan, pullFanLever,
			pullFanLeverAgain, pullLeverToMoveToLava, pickUpBar), elementalBar));

		allSteps.add(new PanelDetails("Making a Mind Helm", Arrays.asList(goDownToBasement, useBarOnGun, operateHat,
			takeMindBar, goMakeMindHelmet), primedBar, hammer, beatenBook));
		return allSteps;
	}
}
