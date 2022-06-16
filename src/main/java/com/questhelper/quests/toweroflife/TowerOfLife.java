/*
 * Copyright (c) 2020, RobertDIV <https://github.com/RobertDIV>
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
package com.questhelper.quests.toweroflife;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.TOWER_OF_LIFE
)
public class TowerOfLife extends BasicQuestHelper
{
	//Items Required
	ItemRequirement
		beer, hammer, saw, //Pre-requirements
		buildersHat, buildersShirt, buildersTrousers, buildersBoots,
		buildersHatEquipped, buildersShirtEquipped, buildersTrousersEquipped, buildersBootsEquipped,
		pressureMachineSheets, pressureMachineBalls, pressureMachineWheels,
		pipeMachinePipes, pipeMachineRings, pipeMachineRivets,
		cageMetalBar, cageBindingFluid;

	//Items Recommended
	ItemRequirement rawSwordfish, rawChicken;

	Requirement  hasSpokenToNoFingers, isPressureMachineBuilt, isPressureMachineFixed,
		isPipeMachineBuilt, isPipeMachineFixed, isCageBuilt, isCageFixed, isTowerFixed;

	QuestStep
		talkToEffigy, talkToBonafido,
		talkToBlackeye, talkToNoFingers, pickpocketNoFingers, getBeerForGuns, talkToGuns, getTrousers, talkToBonafidoWithOutfit,
		enterTower,
		fixPressureMachineGetSheets, fixPressureMachineGetBalls, fixPressureMachineGetWheels, buildPressureMachine, calibratePressureMachine, solvePressureMachinePuzzle,
		fixPipeMachineGetPipes, fixPipeMachineGetRings, fixPipeMachineGetRivets, buildPipeMachine, solvePipeMachinePuzzle,
		fixCageGetBars, fixCageGetFluid, buildCage, solveCagePuzzle,
		talkToEffigyAgain, talkToHomunculusTopOfTower, talkToHomunculusBasement,
		climbUpToFloor1, climbUpToFloor2, climbUpToFloor3, climbDownToGround, climbDownToFloor1, climbDownToFloor2, climbDownToBasement,
		climbBackDownToGround, climbBackDownToFloor1, climbBackDownToFloor2, enterTowerAgain, climbBackUpToFloor1, climbBackUpToFloor2, climbBackUpToFloor3;

	ConditionalStep
		getBuildersOutfit, fixTheTower, fixPressureMachine, fixPipeMachine, fixCage, followTheAlchemists, confrontEffigy, confrontTheHomunculus, scareTheAlchemists, talkToHomunculusInDungeon;

	//Zones
	Zone
			towerBasement,
			tower1, tower2, tower3, tower4,
			tower11, tower12, tower13, tower14,
			tower21, tower22, tower23, tower24,
			tower31, tower32, tower33, tower34,
			tower41, tower42, tower43, tower44;

	ZoneRequirement
			inTower, inTowerBasement, inTowerGround, inTowerFloor1, inTowerFloor2, inTowerFloor3;
	
	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToEffigy);
		steps.put(2, talkToBonafido);
		steps.put(4, getBuildersOutfit);
		steps.put(6, enterTower);
		steps.put(8, fixTheTower);
		steps.put(10, followTheAlchemists);
		steps.put(11, confrontEffigy);
		steps.put(12, confrontTheHomunculus);
		steps.put(14, confrontTheHomunculus);
		steps.put(16, scareTheAlchemists);
		steps.put(17, talkToHomunculusInDungeon);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		rawSwordfish = new ItemRequirement("Raw swordfish (for diary task)", ItemID.RAW_SWORDFISH);
		rawChicken = new ItemRequirement("Raw chicken (for diary task)", ItemID.RAW_CHICKEN);

		beer = new ItemRequirement("Beer", ItemID.BEER);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		saw = new ItemRequirement("Saw", ItemCollections.SAW).isNotConsumed();

		buildersHat = new ItemRequirement("Hard Hat", ItemID.HARD_HAT);
		buildersShirt = new ItemRequirement("Builder's Shirt", ItemID.BUILDERS_SHIRT);
		buildersTrousers = new ItemRequirement("Builder's Trousers", ItemID.BUILDERS_TROUSERS);
		buildersBoots = new ItemRequirement("Builder's Boots", ItemID.BUILDERS_BOOTS);

		buildersHatEquipped = new ItemRequirement("Hard Hat", ItemID.HARD_HAT, 1, true);
		buildersShirtEquipped = new ItemRequirement("Builder's Shirt", ItemID.BUILDERS_SHIRT, 1, true);
		buildersTrousersEquipped = new ItemRequirement("Builder's Trousers", ItemID.BUILDERS_TROUSERS, 1, true);
		buildersBootsEquipped = new ItemRequirement("Builder's Boots", ItemID.BUILDERS_BOOTS, 1, true);

		pressureMachineSheets = new ItemRequirement("Metal sheets", ItemID.METAL_SHEET, 3);
		pressureMachineBalls = new ItemRequirement("Coloured balls", ItemID.COLOURED_BALL, 4);
		pressureMachineWheels = new ItemRequirement("Valve wheels", ItemID.VALVE_WHEEL, 4);

		pipeMachinePipes = new ItemRequirement("Pipes", ItemID.PIPE_10871, 4);
		pipeMachineRings = new ItemRequirement("Pipe rings", ItemID.PIPE_RING, 5);
		pipeMachineRivets = new ItemRequirement("Rivets", ItemID.RIVETS, 6);

		cageMetalBar = new ItemRequirement("Metal bars", ItemID.METAL_BAR, 5);
		cageBindingFluid = new ItemRequirement("Binding fluid", ItemID.BINDING_FLUID, 4);
	}

	public void setupZones()
	{
		WorldPoint z1a = new WorldPoint(2652, 3224, 0);
		WorldPoint z1b = new WorldPoint(2646, 3212, 0);
		WorldPoint z2a = new WorldPoint(2653, 3223, 0);
		WorldPoint z2b = new WorldPoint(2645, 3213, 0);
		WorldPoint z3a = new WorldPoint(2654, 3222, 0);
		WorldPoint z3b = new WorldPoint(2644, 3214, 0);
		WorldPoint z4a = new WorldPoint(2655, 3221, 0);
		WorldPoint z4b = new WorldPoint(2643, 3215, 0);

		towerBasement = new Zone(new WorldPoint(3010, 4354, 0), new WorldPoint(3068, 4412, 0));

		tower1 = new Zone(z1a, z1b.dz(3));
		tower2 = new Zone(z2a, z2b.dz(3));
		tower3 = new Zone(z3a, z3b.dz(3));
		tower4 = new Zone(z4a, z4b.dz(3));

		tower11 = new Zone(z1a, z1b);
		tower12 = new Zone(z2a, z2b);
		tower13 = new Zone(z3a, z3b);
		tower14 = new Zone(z4a, z4b);

		tower21 = new Zone(z1a.dz(1), z1b.dz(1));
		tower22 = new Zone(z2a.dz(1), z2b.dz(1));
		tower23 = new Zone(z3a.dz(1), z3b.dz(1));
		tower24 = new Zone(z4a.dz(1), z4b.dz(1));

		tower31 = new Zone(z1a.dz(2), z1b.dz(2));
		tower32 = new Zone(z2a.dz(2), z2b.dz(2));
		tower33 = new Zone(z3a.dz(2), z3b.dz(2));
		tower34 = new Zone(z4a.dz(2), z4b.dz(2));

		tower41 = new Zone(z1a.dz(3), z1b.dz(3));
		tower42 = new Zone(z2a.dz(3), z2b.dz(3));
		tower43 = new Zone(z3a.dz(3), z3b.dz(3));
		tower44 = new Zone(z4a.dz(3), z4b.dz(3));
	}

	public void setupConditions()
	{
		inTower = new ZoneRequirement(tower1, tower2, tower3, tower4);
		inTowerBasement = new ZoneRequirement(towerBasement);
		inTowerGround = new ZoneRequirement(tower11, tower12, tower13, tower14);
		inTowerFloor1 = new ZoneRequirement(tower21, tower22, tower23, tower24);
		inTowerFloor2 = new ZoneRequirement(tower31, tower32, tower33, tower34);
		inTowerFloor3 = new ZoneRequirement(tower41, tower42, tower43, tower44);

		isPressureMachineBuilt = new VarbitRequirement(3338, 1);
		isPressureMachineFixed = new VarbitRequirement(3338, 2);

		isPipeMachineBuilt = new VarbitRequirement(3339, 1);
		isPipeMachineFixed = new VarbitRequirement(3339, 2);

		isCageBuilt = new VarbitRequirement(3340, 1);
		isCageFixed = new VarbitRequirement(3340, 2);

		isTowerFixed = new VarbitRequirement(3354, 1);
	}

	public void setupSteps()
	{
		talkToEffigy = new NpcStep(this, NpcID.EFFIGY, new WorldPoint(2637, 3218, 0), "Talk to Effigy outside the Tower of Life");
		talkToEffigy.addDialogStep("Sure, why not.");

		talkToBonafido = new NpcStep(this, NpcID.BONAFIDO, new WorldPoint(2651, 3228, 0), "Talk to Bonafido");

		setupGetBuildersCostume();

		enterTower = new ObjectStep(this, ObjectID.TOWER_DOOR, new WorldPoint(2649, 3225, 0), "Enter the tower.",
			buildersHatEquipped, buildersShirtEquipped, buildersTrousersEquipped, buildersBootsEquipped);
		climbUpToFloor1 = new ObjectStep(this, ObjectID.STAIRS_21871, new WorldPoint(2645, 3220, 0), "Go upstairs.");
		climbUpToFloor2 = new ObjectStep(this, ObjectID.STAIRS_21871, new WorldPoint(2653, 3220, 1), "Go upstairs.");
		climbUpToFloor3 = new ObjectStep(this, ObjectID.LADDER_17974, new WorldPoint(2647, 3221, 2), "Go upstairs.");
		climbDownToGround = new ObjectStep(this, ObjectID.STAIRS_21872, new WorldPoint(2645, 3220, 1), "Go downstairs" +
			".");
		climbDownToFloor1 = new ObjectStep(this, ObjectID.STAIRS_21872, new WorldPoint(2653, 3220, 2), "Go downstairs" +
			".");
		climbDownToFloor2 = new ObjectStep(this, ObjectID.LADDER_17975, new WorldPoint(2647, 3221, 3), "Go downstairs" +
			".");

		climbDownToBasement = new ObjectStep(this, NullObjectID.NULL_21944, new WorldPoint(2648, 3212, 0), "Go " +
			"downstairs.");

		enterTowerAgain = new ObjectStep(this, ObjectID.TOWER_DOOR, new WorldPoint(2649, 3225, 0), "Go back into the " +
			"tower.");
		climbBackUpToFloor1 = new ObjectStep(this, ObjectID.STAIRS_21871, new WorldPoint(2645, 3220, 0), "Go back " +
			"upstairs.");
		climbBackUpToFloor2 = new ObjectStep(this, ObjectID.STAIRS_21871, new WorldPoint(2653, 3220, 1), "Go back " +
			"upstairs.");
		climbBackUpToFloor3 = new ObjectStep(this, ObjectID.LADDER_17974, new WorldPoint(2647, 3221, 2), "Go back " +
			"upstairs.");
		climbBackDownToGround = new ObjectStep(this, ObjectID.STAIRS_21872, new WorldPoint(2645, 3220, 1), "Go back " +
			"downstairs.");
		climbBackDownToFloor1 = new ObjectStep(this, ObjectID.STAIRS_21872, new WorldPoint(2653, 3220, 2), "Go back " +
			"downstairs.");
		climbBackDownToFloor2 = new ObjectStep(this, ObjectID.LADDER_17975, new WorldPoint(2647, 3221, 3), "Go back " +
			"downstairs.");

		setupFixTower();

		followTheAlchemists = new ConditionalStep(this, enterTowerAgain, "Follow the alchemists.");
		followTheAlchemists.addStep(inTowerGround, climbBackUpToFloor1);
		followTheAlchemists.addStep(inTowerFloor1, climbBackUpToFloor2);
		followTheAlchemists.addStep(inTowerFloor2, climbBackUpToFloor3);

		confrontEffigy = new ConditionalStep(this, talkToEffigyAgain, "Confront the alchemists.");
		confrontEffigy.addStep(inTowerFloor3, climbBackDownToFloor2);
		confrontEffigy.addStep(inTowerFloor2, climbBackDownToFloor1);
		confrontEffigy.addStep(inTowerFloor1, climbBackDownToGround);

		talkToHomunculusTopOfTower = new NpcStep(this, NpcID.HOMUNCULUS_3590, new WorldPoint(2649, 3218, 3), "Talk to" +
			" the homunculus.");
		confrontTheHomunculus = new ConditionalStep(this, enterTowerAgain, "Confront the homunculus.");
		confrontTheHomunculus.addStep(inTowerGround, climbBackUpToFloor1);
		confrontTheHomunculus.addStep(inTowerFloor1, climbBackUpToFloor2);
		confrontTheHomunculus.addStep(inTowerFloor2, climbBackUpToFloor3);
		confrontTheHomunculus.addStep(inTowerFloor3, talkToHomunculusTopOfTower);

		talkToHomunculusTopOfTower.addDialogStep("With the aid of 5 fire runes.");
		talkToHomunculusTopOfTower.addDialogStep("With the help of the magical dragonstones!");
		talkToHomunculusTopOfTower.addDialogStep("Runecraft, enchant jewellery, perform alchemy.");
		talkToHomunculusTopOfTower.addDialogStep("Turn them into bananas or peaches!");
		talkToHomunculusTopOfTower.addDialogStep("Depends where you are headed, but teleport spells are a safe bet.");
		talkToHomunculusTopOfTower.addDialogStep("Yes, you can make magic potions to boost your skills.");
		talkToHomunculusTopOfTower.addDialogStep("By harnessing the power of the gods!");
		talkToHomunculusTopOfTower.addDialogStep("Yep, you can use the Telekinetic Grab spell.");
		talkToHomunculusTopOfTower.addDialogStep("Through the power of alchemy.");
		talkToHomunculusTopOfTower.addDialogStep("You have special powers - no surprise seeing how you were created.");
		talkToHomunculusTopOfTower.addDialogStep("How about Magic and Runecrafting?");
		talkToHomunculusTopOfTower.addDialogStep("Can't see why not, anything is possible.");
		talkToHomunculusTopOfTower.addDialogStep("Your very existense speaks of mystical forces.");
		talkToHomunculusTopOfTower.addDialogStep("Magic.");

		scareTheAlchemists = new ConditionalStep(this, talkToEffigyAgain, "Scare the alchemists.");
		scareTheAlchemists.addStep(inTowerFloor3, climbBackDownToFloor2);
		scareTheAlchemists.addStep(inTowerFloor2, climbBackDownToFloor1);
		scareTheAlchemists.addStep(inTowerFloor1, climbBackDownToGround);

		talkToHomunculusBasement = new NpcStep(this, NpcID.HOMUNCULUS, new WorldPoint(3040, 4400, 0), "Talk to the " +
			"homunculus.");
		talkToHomunculusInDungeon = new ConditionalStep(this, enterTower, "Talk to the homunculus in the basement.");
		talkToHomunculusInDungeon.addStep(inTowerBasement, talkToHomunculusBasement);
		talkToHomunculusInDungeon.addStep(inTowerGround, climbDownToBasement);
	}

	private void setupFixTower()
	{
		fixPressureMachineGetSheets = new ObjectStep(this, ObjectID.CRATE_21913, new WorldPoint(2643, 3219, 0), "Get " +
			"3 Metal sheets.");
		fixPressureMachineGetBalls = new ObjectStep(this, ObjectID.CRATE_21914, new WorldPoint(2644, 3216, 0), "Get 4" +
			" Coloured balls.");
		fixPressureMachineGetWheels = new ObjectStep(this, ObjectID.CRATE_21912, new WorldPoint(2655, 3217, 0), "Get " +
			"4 Valve wheels.");
		Conditions hasAllPressureItems = new Conditions(pressureMachineSheets, pressureMachineBalls, pressureMachineWheels);

		buildPressureMachine = new ObjectStep(this, ObjectID.PRESSURE_MACHINE, new WorldPoint(2649, 3223, 1), "Build " +
			"the Pressure Machine.", saw, hammer);
		buildPressureMachine.addDialogStep("Yes");
		buildPressureMachine.addSubSteps(
			fixPressureMachineGetSheets, fixPressureMachineGetBalls, fixPressureMachineGetWheels,
			climbUpToFloor1, climbUpToFloor2, climbUpToFloor3, climbDownToGround, climbDownToFloor1, climbDownToFloor2
		);
		calibratePressureMachine = new ObjectStep(this, ObjectID.PRESSURE_MACHINE, new WorldPoint(2649, 3223, 1),
			"Calibrate the Pressure Machine.");
		solvePressureMachinePuzzle = new PuzzleStep(this, "Click the wheels to calibrate the machine", new PuzzleSolver(client)::pressureSolver);

		fixPressureMachine = new ConditionalStep(this, fixPressureMachineGetSheets);
		fixPressureMachine.addStep(isPressureMachineBuilt, solvePressureMachinePuzzle);
		fixPressureMachine.addStep(new Conditions(hasAllPressureItems, inTowerFloor1), buildPressureMachine);
		fixPressureMachine.addStep(new Conditions(hasAllPressureItems, inTowerFloor2), climbDownToFloor1);
		fixPressureMachine.addStep(new Conditions(hasAllPressureItems, inTowerFloor3), climbDownToFloor2);
		fixPressureMachine.addStep(new Conditions(hasAllPressureItems, inTowerGround), climbUpToFloor1);
		fixPressureMachine.addStep(inTowerFloor1, climbDownToGround);
		fixPressureMachine.addStep(inTowerFloor2, climbDownToFloor1);
		fixPressureMachine.addStep(inTowerFloor3, climbDownToFloor2);
		fixPressureMachine.addStep(new Conditions(pressureMachineSheets, pressureMachineBalls), fixPressureMachineGetWheels);
		fixPressureMachine.addStep(pressureMachineSheets, fixPressureMachineGetBalls);

		fixPipeMachineGetPipes = new ObjectStep(this, ObjectID.CRATE_21909, new WorldPoint(2648, 3222, 0), "Get 4 " +
			"Pipes.");
		fixPipeMachineGetRings = new ObjectStep(this, ObjectID.CRATE_21910, new WorldPoint(2652, 3222, 0), "Get 5 " +
			"Pipe rings.");
		fixPipeMachineGetRivets = new ObjectStep(this, ObjectID.CRATE_21911, new WorldPoint(2654, 3220, 0), "Get 6 " +
			"Rivets.");
		Conditions hasAllPipeItems = new Conditions(pipeMachinePipes, pipeMachineRings, pipeMachineRivets);

		buildPipeMachine = new ObjectStep(this, 21943, new WorldPoint(2650, 3214, 2), "Build the Pipe Machine.");
		buildPipeMachine.addDialogStep("Yes");
		buildPipeMachine.addSubSteps(fixPipeMachineGetPipes, fixPipeMachineGetRings, fixPipeMachineGetRivets,
			climbUpToFloor1, climbUpToFloor2, climbUpToFloor3, climbDownToGround, climbDownToFloor1, climbDownToFloor2);
		solvePipeMachinePuzzle = new PuzzleStep(this,
			"Calibrate the pipe machine. Select pipe pieces on the right side of the UI to see where to put them.",
			new PuzzleSolver(client)::pipeSolver);

		fixPipeMachine = new ConditionalStep(this, fixPipeMachineGetPipes);
		fixPipeMachine.addStep(isPipeMachineBuilt, solvePipeMachinePuzzle);
		fixPipeMachine.addStep(new Conditions(hasAllPipeItems, inTowerFloor2), buildPipeMachine);
		fixPipeMachine.addStep(new Conditions(hasAllPipeItems, inTowerFloor3), climbDownToFloor2);
		fixPipeMachine.addStep(new Conditions(hasAllPipeItems, inTowerFloor1), climbUpToFloor2);
		fixPipeMachine.addStep(new Conditions(hasAllPipeItems, inTowerGround), climbUpToFloor1);
		fixPipeMachine.addStep(inTowerFloor1, climbDownToGround);
		fixPipeMachine.addStep(inTowerFloor2, climbDownToFloor1);
		fixPipeMachine.addStep(inTowerFloor3, climbDownToFloor2);
		fixPipeMachine.addStep(new Conditions(pipeMachinePipes, pipeMachineRings), fixPipeMachineGetRivets);
		fixPipeMachine.addStep(pipeMachinePipes, fixPipeMachineGetRings);


		fixCageGetBars = new ObjectStep(this, ObjectID.CRATE_21917, new WorldPoint(2650, 3212, 0), "Get 5 Metal bars.");
		fixCageGetFluid = new ObjectStep(this, ObjectID.CRATE_21915, new WorldPoint(2651, 3213, 0), "Get 4 Binding " +
			"fluid.");
		Conditions hasAllCageItems = new Conditions(cageMetalBar, cageBindingFluid);

		buildCage = new ObjectStep(this, 21941, new WorldPoint(2649, 3218, 3), "Build the cage.");
		buildCage.addDialogStep("Yes");
		buildCage.addSubSteps(fixCageGetBars, fixCageGetFluid,
			climbUpToFloor1, climbUpToFloor2, climbUpToFloor3, climbDownToGround, climbDownToFloor1, climbDownToFloor2);
		solveCagePuzzle = new PuzzleStep(this, "Assemble the cage.", new PuzzleSolver(client)::cageSolver);

		fixCage = new ConditionalStep(this, fixCageGetBars);
		fixCage.addStep(isCageBuilt, solveCagePuzzle);
		fixCage.addStep(new Conditions(hasAllCageItems, inTowerFloor3), buildCage);
		fixCage.addStep(new Conditions(hasAllCageItems, inTowerFloor2), climbUpToFloor3);
		fixCage.addStep(new Conditions(hasAllCageItems, inTowerFloor1), climbUpToFloor2);
		fixCage.addStep(new Conditions(hasAllCageItems, inTowerGround), climbUpToFloor1);
		fixCage.addStep(inTowerFloor1, climbDownToGround);
		fixCage.addStep(inTowerFloor2, climbDownToFloor1);
		fixCage.addStep(inTowerFloor3, climbDownToFloor2);
		fixCage.addStep(cageMetalBar, fixCageGetFluid);

		talkToEffigyAgain = new NpcStep(this, NpcID.EFFIGY, new WorldPoint(2637, 3218, 0), "Go back and talk to " +
			"Effigy.");
		talkToEffigyAgain.addSubSteps(climbBackDownToGround, climbBackDownToFloor1, climbBackDownToFloor2);

		fixTheTower = new ConditionalStep(this, enterTower);
		fixTheTower.addStep(new Conditions(isTowerFixed, inTowerFloor1), climbBackDownToGround);
		fixTheTower.addStep(new Conditions(isTowerFixed, inTowerFloor2), climbBackDownToFloor1);
		fixTheTower.addStep(new Conditions(isTowerFixed, inTowerFloor3), climbBackDownToFloor2);
		fixTheTower.addStep(isTowerFixed, talkToEffigyAgain);
		fixTheTower.addStep(new Conditions(inTower, isPressureMachineFixed, isPipeMachineFixed), fixCage);
		fixTheTower.addStep(new Conditions(inTower, isPressureMachineFixed), fixPipeMachine);
		fixTheTower.addStep(inTower, fixPressureMachine);
	}

	private void setupGetBuildersCostume()
	{
		talkToBlackeye = new NpcStep(this, NpcID.BLACKEYE, "Get the Hard Hat from 'Black-eye'.");
		talkToBlackeye.addDialogStep(3, "Three");
		talkToBlackeye.addDialogStep(1, "Torn curtains");
		talkToBlackeye.addDialogStep(2, "10 clay pieces");

		talkToNoFingers = new NpcStep(this, NpcID.NO_FINGERS, new WorldPoint(2645, 3224, 0), "Talk to 'No fingers'.");
		pickpocketNoFingers = new NpcStep(this, NpcID.NO_FINGERS, new WorldPoint(2645, 3224, 0), "Pickpocket 'No " +
			"fingers'.");
		hasSpokenToNoFingers = new VarbitRequirement(3376, 1);
		ConditionalStep getBoots = new ConditionalStep(this, talkToNoFingers); // "Get the Builder's Boots from 'No fingers'"
		getBoots.addStep(hasSpokenToNoFingers, pickpocketNoFingers);

		talkToGuns = new NpcStep(this, NpcID.THE_GUNS, new WorldPoint(2643, 3226, 0), "Talk to 'The Guns'.");
		getBeerForGuns = new ItemStep(this, "Get a Beer for 'The Guns'.", beer);
		ConditionalStep getShirt = new ConditionalStep(this, getBeerForGuns);
		getShirt.addStep(beer, talkToGuns);

		getTrousers = new ObjectStep(this, ObjectID.PLANT_21924, "Search the bushes to the south for some Builder's " +
			"Trousers.");
		getTrousers.addText("(Hint: try the south-east bushes first).");
		((DetailedQuestStep) getTrousers).setHideWorldArrow(true);

		talkToBonafidoWithOutfit = new NpcStep(this, NpcID.BONAFIDO, "Speak to Bonafido.",
			buildersHatEquipped, buildersShirtEquipped, buildersTrousersEquipped, buildersBootsEquipped);
		talkToBonafidoWithOutfit.addDialogStep(2, "Tea");
		talkToBonafidoWithOutfit.addDialogStep(3, "Whistle for attention");
		talkToBonafidoWithOutfit.addDialogStep(1, "Your legs are getting a bit cold");
		talkToBonafidoWithOutfit.addDialogStep(2, "Carry on, it'll fix itself");

		getBuildersOutfit = new ConditionalStep(this, talkToBlackeye);
		getBuildersOutfit.addStep(new Conditions(buildersHat, buildersBoots, buildersShirt, buildersTrousers), talkToBonafidoWithOutfit);
		getBuildersOutfit.addStep(new Conditions(buildersHat, buildersBoots, buildersShirt), getTrousers);
		getBuildersOutfit.addStep(new Conditions(buildersHat, buildersBoots), getShirt);
		getBuildersOutfit.addStep(buildersHat, getBoots);

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(beer, hammer, saw);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(rawSwordfish, rawChicken);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.CONSTRUCTION, 10));
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return Collections.singletonList(new FreeInventorySlotRequirement(InventoryID.INVENTORY, 11));
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CONSTRUCTION, 1000),
				new ExperienceReward(Skill.CRAFTING, 500),
				new ExperienceReward(Skill.THIEVING, 500));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Creature Creation."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToEffigy, talkToBonafido), saw, hammer, beer));
		PanelDetails getBuildersOutfitPanel = new PanelDetails("Get the Builders' outfit", Arrays.asList(
			talkToBlackeye, //Get hat
			talkToNoFingers, pickpocketNoFingers, //Get Boots
			getBeerForGuns, talkToGuns, //Get shirt
			getTrousers,
			talkToBonafidoWithOutfit
		), beer);
		getBuildersOutfitPanel.setLockingStep(getBuildersOutfit);
		allSteps.add(getBuildersOutfitPanel);


		allSteps.add(new PanelDetails("Fix the tower",
			Arrays.asList(enterTower,
			buildPressureMachine, solvePressureMachinePuzzle,
			buildPipeMachine, solvePipeMachinePuzzle,
			buildCage, solveCagePuzzle), saw, hammer));

		allSteps.add(new PanelDetails("The Alchemists' Secret", talkToEffigyAgain, followTheAlchemists,
			confrontEffigy, confrontTheHomunculus, scareTheAlchemists, talkToHomunculusInDungeon));

		return allSteps;
	}
}
