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
package com.questhelper.quests.olafsquest;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.WidgetStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetModelCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.OLAFS_QUEST
)
public class OlafsQuest extends BasicQuestHelper
{
	ItemRequirement combatGear, axe, tinderbox, spade, dampPlanks, windsweptLogs, crudeCarving, cruderCarving, food, key, rottenBarrels2, rottenBarrel, ropes6, ropes3, crossKey, squareKey,
		triangleKey, circleKey, starKey;

	ConditionForStep hasWindsweptLogs, givenIngridCarving, inFirstArea, inSecondArea, inThirdArea, keyNearby, puzzleOpen, hasKey, has2Barrels6Ropes, hasBarrel3Ropes, placedBarrel1, placedBarrel2,
		keyInterfaceOpen, hasCrossKey, hasSquareKey, hasTriangleKey, hasCircleKey, hasStarKey, ulfricNearby, killedUlfric;

	QuestStep talkToOlaf, chopTree, giveLogToOlaf, talkToIngrid, talkToVolf, returnToOlaf, useDampPlanks, talkToOlafAfterPlanks, digHole, pickUpKey, searchPainting, doPuzzle, pickUpItems,
		pickupItems2, useBarrel, useBarrel2, openGate, chooseSquare, chooseCross, chooseTriangle, chooseCircle, chooseStar, killUlfric;

	NpcStep killSkeleton;

	ObjectStep searchChest, searchChestAgain;

	Zone firstArea, firstArea2, secondArea, secondArea2, thirdArea;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToOlaf);

		ConditionalStep getLogs = new ConditionalStep(this, chopTree);
		getLogs.addStep(hasWindsweptLogs, giveLogToOlaf);

		steps.put(10, getLogs);

		ConditionalStep bringCarvings = new ConditionalStep(this, talkToIngrid);
		bringCarvings.addStep(givenIngridCarving, talkToVolf);

		steps.put(20, bringCarvings);

		steps.put(30, returnToOlaf);

		steps.put(40, useDampPlanks);

		steps.put(50, talkToOlafAfterPlanks);

		ConditionalStep solvePuzzleSteps = new ConditionalStep(this, digHole);
		solvePuzzleSteps.addStep(new Conditions(inThirdArea, killedUlfric), searchChestAgain);
		solvePuzzleSteps.addStep(new Conditions(ulfricNearby, inThirdArea), killUlfric);
		solvePuzzleSteps.addStep(inThirdArea, searchChest);
		solvePuzzleSteps.addStep(new Conditions(hasStarKey, keyInterfaceOpen), chooseStar);
		solvePuzzleSteps.addStep(new Conditions(hasCircleKey, keyInterfaceOpen), chooseCircle);
		solvePuzzleSteps.addStep(new Conditions(hasTriangleKey, keyInterfaceOpen), chooseTriangle);
		solvePuzzleSteps.addStep(new Conditions(hasSquareKey, keyInterfaceOpen), chooseSquare);
		solvePuzzleSteps.addStep(new Conditions(hasCrossKey, keyInterfaceOpen), chooseCross);
		solvePuzzleSteps.addStep(new Conditions(hasKey, placedBarrel2), openGate);
		solvePuzzleSteps.addStep(new Conditions(hasKey, placedBarrel1, hasBarrel3Ropes), useBarrel2);
		solvePuzzleSteps.addStep(new Conditions(placedBarrel1, inSecondArea, hasKey), pickupItems2);
		solvePuzzleSteps.addStep(new Conditions(has2Barrels6Ropes, hasKey), useBarrel);
		solvePuzzleSteps.addStep(new Conditions(inSecondArea, hasKey), pickUpItems);
		solvePuzzleSteps.addStep(puzzleOpen, doPuzzle);
		solvePuzzleSteps.addStep(hasKey, searchPainting);
		solvePuzzleSteps.addStep(keyNearby, pickUpKey);
		solvePuzzleSteps.addStep(inFirstArea, killSkeleton);

		steps.put(60, solvePuzzleSteps);
		steps.put(70, solvePuzzleSteps);

		return steps;
	}

	public void setupItemRequirements()
	{
		combatGear = new ItemRequirement("Combat gear, food + prayer potions", -1, -1);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		spade = new ItemRequirement("Spade", ItemID.SPADE);

		dampPlanks = new ItemRequirement("Damp planks", ItemID.DAMP_PLANKS);
		windsweptLogs = new ItemRequirement("Windswept logs", ItemID.WINDSWEPT_LOGS);

		crudeCarving = new ItemRequirement("Crude carving", ItemID.CRUDE_CARVING);
		crudeCarving.setTip("You can get another from Olaf");
		cruderCarving = new ItemRequirement("Cruder carving", ItemID.CRUDER_CARVING);
		cruderCarving.setTip("You can get another from Olaf");

		food = new ItemRequirement("Food", -1, -1);

		key = new ItemRequirement("Key", ItemID.KEY_11039);
		key.addAlternates(ItemID.KEY_11040, ItemID.KEY_11041, ItemID.KEY_11042, ItemID.KEY_11043);

		crossKey = new ItemRequirement("Key", ItemID.KEY_11039);
		squareKey = new ItemRequirement("Key", ItemID.KEY_11040);
		triangleKey = new ItemRequirement("Key", ItemID.KEY_11041);
		circleKey = new ItemRequirement("Key", ItemID.KEY_11042);
		starKey = new ItemRequirement("Key", ItemID.KEY_11043);

		rottenBarrel = new ItemRequirement("Rotten barrel", ItemID.ROTTEN_BARREL);
		rottenBarrel.addAlternates(ItemID.ROTTEN_BARREL_11045);
		rottenBarrels2 = new ItemRequirement("Rotten barrel", ItemID.ROTTEN_BARREL, 2);
		rottenBarrels2.addAlternates(ItemID.ROTTEN_BARREL_11045);

		ropes3 = new ItemRequirement("Rope", ItemID.ROPE, 3);
		ropes3.addAlternates(ItemID.ROPE_11046);
		ropes6 = new ItemRequirement("Rope", ItemID.ROPE, 6);
		ropes6.addAlternates(ItemID.ROPE_11046);
	}

	public void setupConditions()
	{
		hasWindsweptLogs = new ItemRequirementCondition(windsweptLogs);
		givenIngridCarving = new VarbitCondition(3536, 1, Operation.GREATER_EQUAL);
		inFirstArea = new ZoneCondition(firstArea, firstArea2);
		inSecondArea = new ZoneCondition(secondArea, secondArea2);
		inThirdArea = new ZoneCondition(thirdArea);
		keyNearby = new ItemCondition(key);
		puzzleOpen = new WidgetModelCondition(253, 0, 24126);
		hasKey = new ItemRequirementCondition(key);
		hasBarrel3Ropes = new ItemRequirementCondition(rottenBarrel, ropes3);
		has2Barrels6Ropes = new ItemRequirementCondition(rottenBarrels2, ropes6);
		placedBarrel1 = new VarbitCondition(3547, 1);
		placedBarrel2 = new VarbitCondition(3548, 1);
		keyInterfaceOpen = new WidgetModelCondition(252, 0, 24124);
		hasSquareKey = new ItemRequirementCondition(squareKey);
		hasCrossKey = new ItemRequirementCondition(crossKey);
		hasTriangleKey = new ItemRequirementCondition(triangleKey);
		hasCircleKey = new ItemRequirementCondition(circleKey);
		hasStarKey = new ItemRequirementCondition(starKey);

		ulfricNearby = new NpcCondition(NpcID.ULFRIC);

		killedUlfric = new VarbitCondition(3539, 1);
	}

	public void loadZones()
	{
		firstArea = new Zone(new WorldPoint(2689, 10116, 0), new WorldPoint(2707, 10141, 0));
		firstArea2 = new Zone(new WorldPoint(2707, 10118, 0), new WorldPoint(2739, 10148, 0));
		secondArea = new Zone(new WorldPoint(2691, 10143, 0), new WorldPoint(2706, 10170, 0));
		secondArea2 = new Zone(new WorldPoint(2707, 10149, 0), new WorldPoint(2735, 10170, 0));
		thirdArea = new Zone(new WorldPoint(2726, 10154, 0), new WorldPoint(2749, 10170, 0));
	}

	public void setupSteps()
	{
		talkToOlaf = new NpcStep(this, NpcID.OLAF_HRADSON, new WorldPoint(2722, 3727, 0), "Talk to Olaf Hradson north east of Rellekka.");
		talkToOlaf.addDialogStep("Okay, I'll help you out.");
		chopTree = new ObjectStep(this, ObjectID.WINDSWEPT_TREE_18137, new WorldPoint(2749, 3735, 0), "Chop a log from the Swaying Tree east of Olaf.", axe);
		giveLogToOlaf = new NpcStep(this, NpcID.OLAF_HRADSON, new WorldPoint(2722, 3727, 0), "Bring the logs to to Olaf Hradson north east of Rellekka.", windsweptLogs);
		talkToIngrid = new NpcStep(this, NpcID.INGRID_HRADSON, new WorldPoint(2670, 3670, 0), "Talk to Ingrid Hradson in Rellekka.", crudeCarving);
		talkToVolf = new NpcStep(this, NpcID.VOLF_OLAFSON, new WorldPoint(2662, 3700, 0), "Talk to Volf Hradson in Rellekka.", cruderCarving);

		returnToOlaf = new NpcStep(this, NpcID.OLAF_HRADSON, new WorldPoint(2722, 3727, 0), "Return to Olaf Hradson north east of Rellekka.");
		useDampPlanks = new ObjectStep(this, NullObjectID.NULL_14172, new WorldPoint(2724, 3728, 0), "Use the damp planks on Olaf's embers.", dampPlanks);
		talkToOlafAfterPlanks = new NpcStep(this, NpcID.OLAF_HRADSON, new WorldPoint(2722, 3727, 0), "Talk to Olaf again, and give him some food.", food);
		talkToOlafAfterPlanks.addDialogStep("Alright, here, have some food. Now give me the map.");
		digHole = new DigStep(this, new WorldPoint(2748, 3732, 0), "Dig next to the windswept tree.");

		killSkeleton = new NpcStep(this, NpcID.SKELETON_FREMENNIK, new WorldPoint(2727, 10141, 0), "Go deeper into the caverns and kill a Skeleton Fremennik for a key.", true);
		killSkeleton.addAlternateNpcs(NpcID.SKELETON_FREMENNIK_4492, NpcID.SKELETON_FREMENNIK_4493, NpcID.SKELETON_FREMENNIK_4494, NpcID.SKELETON_FREMENNIK_4495,
			NpcID.SKELETON_FREMENNIK_4496, NpcID.SKELETON_FREMENNIK_4497, NpcID.SKELETON_FREMENNIK_4498, NpcID.SKELETON_FREMENNIK_4499);

		pickUpKey = new ItemStep(this, "Pick up the dropped key.", key);

		searchPainting = new ObjectStep(this, ObjectID.PICTURE_WALL, new WorldPoint(2707, 10147, 0), "Search the picture wall in the north room.");

		doPuzzle = new PaintingWall(this);

		pickUpItems = new DetailedQuestStep(this, "Pick up 2 rotten barrels and 6 ropes from around the room.", rottenBarrels2, ropes6);

		useBarrel = new ObjectStep(this, ObjectID.WALKWAY, new WorldPoint(2722, 10168, 0), "WALK onto the walkway to the east, and use a barrel on it to repair it.", rottenBarrel, ropes3);
		useBarrel.addIcon(ItemID.ROTTEN_BARREL_11045);
		useBarrel2 = new ObjectStep(this, ObjectID.WALKWAY_23214, new WorldPoint(2724, 10168, 0), "WALK on the walkway and repair the next hole in it.", rottenBarrel, ropes3);
		useBarrel2.addIcon(ItemID.ROTTEN_BARREL_11045);

		openGate = new ObjectStep(this, ObjectID.GATE_23216, new WorldPoint(2725, 10168, 0), "Open the gate on the walkway, clicking the key hole which matches your key.", key);

		chooseSquare = new WidgetStep(this,  "Click the square key hole.", 252, 3);
		chooseCross = new WidgetStep(this,  "Click the cross key hole.", 252, 4);
		chooseTriangle = new WidgetStep(this,  "Click the triangle key hole.", 252, 5);
		chooseCircle = new WidgetStep(this,  "Click the circle key hole.", 252, 6);
		chooseStar = new WidgetStep(this,  "Click the star key hole.", 252, 7);
		openGate.addSubSteps(chooseCircle, chooseCross, chooseSquare, chooseStar, chooseTriangle);

		searchChest = new ObjectStep(this, ObjectID.CHEST_14197, new WorldPoint(2740, 10164, 0), "WALK off the remaining walkway, and search the chest in the wreck. Be prepared to fight Ulfric.");
		searchChest.addAlternateObjects(ObjectID.CHEST_14196);

		killUlfric = new NpcStep(this, NpcID.ULFRIC, new WorldPoint(2740, 10164, 0), "Kill Ulfric.");

		searchChestAgain = new ObjectStep(this, ObjectID.CHEST_14197, new WorldPoint(2740, 10164, 0), "Search the chest again to finish the quest.");
		searchChestAgain.addAlternateObjects(ObjectID.CHEST_14196);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(axe, tinderbox, spade));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Collections.singletonList(combatGear));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Skeleton fremennik (level 40)", "Ulfric (level 100)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToOlaf, chopTree, giveLogToOlaf, talkToIngrid, talkToVolf, returnToOlaf, useDampPlanks, talkToOlafAfterPlanks)), axe, tinderbox, spade));

		allSteps.add(new PanelDetails("Finding treasure",
			new ArrayList<>(Arrays.asList(digHole, killSkeleton, searchPainting, doPuzzle, pickUpItems, useBarrel, useBarrel2, openGate, searchChest, killUlfric, searchChestAgain))));
		return allSteps;
	}
}
