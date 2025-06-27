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
package com.questhelper.helpers.quests.olafsquest;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.and;

public class OlafsQuest extends BasicQuestHelper
{
	//Items Required
	ItemRequirement axe, tinderbox, spade, dampPlanks, windsweptLogs, crudeCarving, cruderCarving, key, rottenBarrels2, rottenBarrel, ropes6, ropes3, crossKey, squareKey,
		triangleKey, circleKey, starKey;

	//Items Recommended
	ItemRequirement prayerPotions, food, combatGear;

	Requirement givenIngridCarving, inFirstArea, inSecondArea, inThirdArea, keyNearby, puzzleOpen, has2Barrels6Ropes, hasBarrel3Ropes, placedBarrel1, placedBarrel2,
		keyInterfaceOpen, ulfricNearby, killedUlfric, tenFreeSlots;

	QuestStep talkToOlaf, chopTree, giveLogToOlaf, talkToIngrid, talkToVolf, returnToOlaf, useDampPlanks, talkToOlafAfterPlanks, digHole, pickUpKey, searchPainting, doPuzzle, pickUpItems,
		pickUpItems2, useBarrel, useBarrel2, openGate, chooseSquare, chooseCross, chooseTriangle, chooseCircle, chooseStar, killUlfric;

	NpcStep killSkeleton;

	ObjectStep searchChest, searchChestAgain;

	//Zones
	Zone firstArea, firstArea2, secondArea, secondArea2, thirdArea;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToOlaf);

		ConditionalStep getLogs = new ConditionalStep(this, chopTree);
		getLogs.addStep(windsweptLogs, giveLogToOlaf);

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
		solvePuzzleSteps.addStep(new Conditions(starKey, keyInterfaceOpen), chooseStar);
		solvePuzzleSteps.addStep(new Conditions(circleKey, keyInterfaceOpen), chooseCircle);
		solvePuzzleSteps.addStep(new Conditions(triangleKey, keyInterfaceOpen), chooseTriangle);
		solvePuzzleSteps.addStep(new Conditions(squareKey, keyInterfaceOpen), chooseSquare);
		solvePuzzleSteps.addStep(new Conditions(crossKey, keyInterfaceOpen), chooseCross);
		solvePuzzleSteps.addStep(new Conditions(key, inSecondArea, placedBarrel2), openGate);
		solvePuzzleSteps.addStep(new Conditions(key, placedBarrel1, inSecondArea, hasBarrel3Ropes), useBarrel2);
		solvePuzzleSteps.addStep(new Conditions(placedBarrel1, inSecondArea, key), pickUpItems2);
		solvePuzzleSteps.addStep(new Conditions(has2Barrels6Ropes, inSecondArea, key), useBarrel);
		solvePuzzleSteps.addStep(new Conditions(inSecondArea, key), pickUpItems);
		solvePuzzleSteps.addStep(puzzleOpen, doPuzzle);
		solvePuzzleSteps.addStep(and(inFirstArea, key), searchPainting);
		solvePuzzleSteps.addStep(and(inFirstArea, keyNearby), pickUpKey);
		solvePuzzleSteps.addStep(new Conditions(inFirstArea), killSkeleton);

		steps.put(60, solvePuzzleSteps);
		steps.put(70, solvePuzzleSteps);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		food.setUrlSuffix("Food");

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);

		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed();
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();

		dampPlanks = new ItemRequirement("Damp planks", ItemID.OLAF_WOODPLANK);
		windsweptLogs = new ItemRequirement("Windswept logs", ItemID.OLAF_WINDSWEPT_LOGS);

		crudeCarving = new ItemRequirement("Crude carving", ItemID.OLAF_WOODCARVINGA);
		crudeCarving.setTooltip("You can get another from Olaf");
		cruderCarving = new ItemRequirement("Cruder carving", ItemID.OLAF_WOODCARVINGB);
		cruderCarving.setTooltip("You can get another from Olaf");

		key = new ItemRequirement("Key", ItemID.OLAF2_GATE_KEY_1);
		key.addAlternates(ItemID.OLAF2_GATE_KEY_2, ItemID.OLAF2_GATE_KEY_3, ItemID.OLAF2_GATE_KEY_4, ItemID.OLAF2_GATE_KEY_5);

		crossKey = new ItemRequirement("Key", ItemID.OLAF2_GATE_KEY_1);
		squareKey = new ItemRequirement("Key", ItemID.OLAF2_GATE_KEY_2);
		triangleKey = new ItemRequirement("Key", ItemID.OLAF2_GATE_KEY_3);
		circleKey = new ItemRequirement("Key", ItemID.OLAF2_GATE_KEY_4);
		starKey = new ItemRequirement("Key", ItemID.OLAF2_GATE_KEY_5);

		tenFreeSlots = new FreeInventorySlotRequirement(10);
		tenFreeSlots.setTooltip("Only need 4 slots free if bringing 6 rope with you.");

		rottenBarrel = new ItemRequirement("Rotten barrel", ItemID.OLAF2_WALKWAY_REPAIR_BARREL);
		rottenBarrel.addAlternates(ItemID.OLAF2_WALKWAY_REPAIR_BARREL_INV);
		rottenBarrels2 = new ItemRequirement("Rotten barrel", ItemID.OLAF2_WALKWAY_REPAIR_BARREL, 2);
		rottenBarrels2.addAlternates(ItemID.OLAF2_WALKWAY_REPAIR_BARREL_INV);

		ropes3 = new ItemRequirement("Rope", ItemID.ROPE, 3);
		ropes3.addAlternates(ItemID.OLAF2_WALKWAY_REPAIR_ROPE);
		ropes6 = new ItemRequirement("Rope", ItemID.ROPE, 6);
		ropes6.addAlternates(ItemID.OLAF2_WALKWAY_REPAIR_ROPE);
	}

	public void setupConditions()
	{
		givenIngridCarving = new VarbitRequirement(VarbitID.OLAF_INGRID_QUEST, 1, Operation.GREATER_EQUAL);
		inFirstArea = new ZoneRequirement(firstArea, firstArea2);
		inSecondArea = new ZoneRequirement(secondArea, secondArea2);
		inThirdArea = new ZoneRequirement(thirdArea);
		keyNearby = new ItemOnTileRequirement(key);
		puzzleOpen = new WidgetModelRequirement(253, 0, 24126);
		hasBarrel3Ropes = new Conditions(rottenBarrel, ropes3);
		has2Barrels6Ropes = new Conditions(rottenBarrels2, ropes6);
		placedBarrel1 = new VarbitRequirement(3547, 1);
		placedBarrel2 = new VarbitRequirement(3548, 1);
		keyInterfaceOpen = new WidgetModelRequirement(252, 0, 24124);

		ulfricNearby = new NpcCondition(NpcID.OLAF2_ULFRIC);

		killedUlfric = new VarbitRequirement(3539, 1);
	}

	@Override
	protected void setupZones()
	{
		firstArea = new Zone(new WorldPoint(2689, 10116, 0), new WorldPoint(2707, 10141, 0));
		firstArea2 = new Zone(new WorldPoint(2707, 10118, 0), new WorldPoint(2739, 10148, 0));
		secondArea = new Zone(new WorldPoint(2691, 10143, 0), new WorldPoint(2706, 10170, 0));
		secondArea2 = new Zone(new WorldPoint(2707, 10149, 0), new WorldPoint(2735, 10170, 0));
		thirdArea = new Zone(new WorldPoint(2726, 10154, 0), new WorldPoint(2749, 10170, 0));
	}

	public void setupSteps()
	{
		talkToOlaf = new NpcStep(this, NpcID.OLAF, new WorldPoint(2722, 3727, 0), "Talk to Olaf Hradson north east of Rellekka.");
		talkToOlaf.addDialogStep("Yes.");
		chopTree = new ObjectStep(this, ObjectID.OLAF_WINDSWEPT_TREE, new WorldPoint(2749, 3735, 0), "Chop a log from the Windswept Tree east of Olaf.", axe);
		giveLogToOlaf = new NpcStep(this, NpcID.OLAF, new WorldPoint(2722, 3727, 0), "Bring the logs to Olaf Hradson north east of Rellekka.", windsweptLogs);
		talkToIngrid = new NpcStep(this, NpcID.OLAF_INGRID, new WorldPoint(2670, 3670, 0), "Talk to Ingrid Hradson near the well in southeast Rellekka.", crudeCarving);
		talkToVolf = new NpcStep(this, NpcID.OLAF_VOLF, new WorldPoint(2662, 3700, 0), "Talk to Volf Olafson north of the longhall in Rellekka.", cruderCarving);

		returnToOlaf = new NpcStep(this, NpcID.OLAF, new WorldPoint(2722, 3727, 0), "Return to Olaf Hradson north east of Rellekka.");
		useDampPlanks = new ObjectStep(this, ObjectID.OLAF_MULTI_FIRE, new WorldPoint(2724, 3728, 0), "Use the damp planks on Olaf's embers.", dampPlanks);
		talkToOlafAfterPlanks = new NpcStep(this, NpcID.OLAF, new WorldPoint(2722, 3727, 0), "Talk to Olaf again, and give him some food.", food);
		talkToOlafAfterPlanks.addDialogStep("Alright, here, have some food. Now give me the map.");
		digHole = new DigStep(this, new WorldPoint(2748, 3732, 0), "Dig next to the Windswept Tree.");

		pickUpKey = new ItemStep(this, "Pick up the dropped key.", key);

		killSkeleton = new NpcStep(this, NpcID.OLAF2_UNDEAD_VIKING_LVL40, new WorldPoint(2727, 10141, 0), "Go deeper into the caverns and kill a Skeleton Fremennik for a key.", true);
		killSkeleton.addAlternateNpcs(NpcID.OLAF2_UNDEAD_VIKING_LVL40_B, NpcID.OLAF2_UNDEAD_VIKING_LVL40_C, NpcID.OLAF2_UNDEAD_VIKING_LVL50, NpcID.OLAF2_UNDEAD_VIKING_LVL50_B,
			NpcID.OLAF2_UNDEAD_VIKING_LVL50_C, NpcID.OLAF2_UNDEAD_VIKING_LVL60, NpcID.OLAF2_UNDEAD_VIKING_LVL60_B, NpcID.OLAF2_UNDEAD_VIKING_LVL60_C);
		killSkeleton.addSubSteps(pickUpKey);

		searchPainting = new ObjectStep(this, ObjectID.OLAF2_SKULL_PUZZLE_WALL, new WorldPoint(2707, 10147, 0), "Search the picture wall in the north room.");

		doPuzzle = new PaintingWall(this);

		pickUpItems = new DetailedQuestStep(this, "Pick up 2 rotten barrels and 6 ropes from around the room.", rottenBarrels2, ropes6);
		pickUpItems2 = new DetailedQuestStep(this, "Pick up 1 rotten barrels and 3 ropes from around the room.", rottenBarrel, ropes3);
		pickUpItems.addSubSteps(pickUpItems2);

		useBarrel = new ObjectStep(this, ObjectID.OLAF2_INVIS_HOTSPOT_BARREL1, new WorldPoint(2722, 10168, 0), "WALK onto the walkway to the east, and use a barrel on it to repair it.", rottenBarrel.highlighted(), ropes3);
		useBarrel.addIcon(ItemID.OLAF2_WALKWAY_REPAIR_BARREL_INV);
		useBarrel2 = new ObjectStep(this, ObjectID.OLAF2_INVIS_HOTSPOT_BARREL2, new WorldPoint(2724, 10168, 0), "WALK on the walkway and repair the next hole in it.", rottenBarrel.highlighted(), ropes3);
		useBarrel2.addIcon(ItemID.OLAF2_WALKWAY_REPAIR_BARREL_INV);

		openGate = new ObjectStep(this, ObjectID.OLAF2_RUSTY_GATE_PUZZLE, new WorldPoint(2725, 10168, 0), "Open the gate on the walkway, clicking the key hole which matches your key.", key);

		chooseSquare = new WidgetStep(this, "Click the square key hole.", 252, 3);
		chooseCross = new WidgetStep(this, "Click the cross key hole.", 252, 4);
		chooseTriangle = new WidgetStep(this, "Click the triangle key hole.", 252, 5);
		chooseCircle = new WidgetStep(this, "Click the circle key hole.", 252, 6);
		chooseStar = new WidgetStep(this, "Click the star key hole.", 252, 7);
		openGate.addSubSteps(chooseCircle, chooseCross, chooseSquare, chooseStar, chooseTriangle);

		searchChest = new ObjectStep(this, ObjectID.OLAF2_CHEST_CLOSED, new WorldPoint(2740, 10164, 0), "WALK off the remaining walkway, and search the chest in the wreck. Be prepared to fight Ulfric.");
		searchChest.addAlternateObjects(ObjectID.OLAF2_CHEST_OPEN);

		killUlfric = new NpcStep(this, NpcID.OLAF2_ULFRIC, new WorldPoint(2740, 10164, 0), "Kill Ulfric.");

		searchChestAgain = new ObjectStep(this, ObjectID.OLAF2_CHEST_CLOSED, new WorldPoint(2740, 10164, 0), "Search the chest again to finish the quest.");
		searchChestAgain.addAlternateObjects(ObjectID.OLAF2_CHEST_OPEN);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(axe, tinderbox, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food, prayerPotions);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Skeleton fremennik (level 40)", "Ulfric (level 100)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.FIREMAKING, 40, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 50, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.DEFENCE, 12000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Rubies", ItemID.RUBY, 4));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Brine Rats and the ability to receive them as a slayer task."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToOlaf, chopTree, giveLogToOlaf, talkToIngrid,
				talkToVolf, returnToOlaf, useDampPlanks, talkToOlafAfterPlanks), axe, tinderbox, spade));

		allSteps.add(new PanelDetails("Finding treasure",
			Arrays.asList(digHole, killSkeleton, searchPainting, doPuzzle, pickUpItems,
				useBarrel, useBarrel2, openGate, searchChest, killUlfric, searchChestAgain), spade, tenFreeSlots, combatGear));
		return allSteps;
	}
}
