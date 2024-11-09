/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thecurseofarrav;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers.RubbleSolverOne;
import com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers.RubbleSolverThree;
import com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers.RubbleSolverTwo;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;

/**
 * The quest guide for the "The Curse of Arrav" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/The_Curse_of_Arrav">The OSRS wiki guide</a> and <a href="https://www.youtube.com/watch?v=IIMTpgD0BUY">Slayermusiq1's Quest Guide</a> was referenced for this guide
 */
public class TheCurseOfArrav extends BasicQuestHelper
{
	/// Required items
	private ItemRequirement dwellberries3;
	private ItemRequirement ringOfLife;
	private ItemRequirement anyPickaxe;
	private ItemRequirement anyGrappleableCrossbow;
	private ItemRequirement mithrilGrapple;
	private ItemRequirement insulatedBoots;

	/// Recommended items
	// teleport to fairy ring
	// fairy ring staff
	// trollheim teleport / ghommal's hilt
	// antivenom
	// lumberyard teleport
	// melee (crush) combat gear for golem
	// ranged combat gear for arrav
	private ItemRequirement crushCombatGear;
	private ItemRequirement rangedCombatGear;
	private ItemRequirement food;
	private ItemRequirement staminaPotion;
	private ItemRequirement prayerPotion;
	// 2 inv slots

	/// Mid-quest item requirements
	private ItemRequirement experimentalKebab;
	private ItemRequirement goodTestKebab;
	private ItemRequirement goodTestKebabs;

	/// Zones & their requirements
	private Zone wolfDen;
	private ZoneRequirement inWolfDen;
	private Zone fortisColosseum;
	private ZoneRequirement inFortisColosseum;

	/// Steps
	private QuestStep startQuest;
	private QuestStep enterTomb;
	private ObjectStep getFirstKey;
	private ObjectStep getSecondKey;
	private ObjectStep pullSouthLever;
	private ObjectStep pullNorthLever;
	private ObjectStep enterGolemArena;
	private NpcStep fightGolemGuard;
	private ObjectStep enterTombBasement;
	private QuestStep unsortedStep6;
	private ConditionalStep unsortedStep10;
	private PuzzleWrapperStep solveTilePuzzle;
	private QuestStep unsortedStep12;
	private QuestStep unsortedStep14;
	private QuestStep unsortedStep16;
	private QuestStep unsortedStep18;
	private QuestStep unsortedStep20;
	private QuestStep unsortedStep22;
	private QuestStep unsortedStep24;
	private QuestStep unsortedStep26;
	private QuestStep unsortedStep28;
	private QuestStep unsortedStep30;
	private QuestStep unsortedStep32;
	private QuestStep unsortedStep34;
	private QuestStep unsortedStep36;
	private QuestStep unsortedStep38;
	private QuestStep unsortedStep40;
	private QuestStep unsortedStep42;
	private QuestStep unsortedStep44;
	private QuestStep unsortedStep46;
	private QuestStep unsortedStep48;
	private QuestStep unsortedStep50;
	private QuestStep unsortedStep52;
	private QuestStep unsortedStep54;
	private QuestStep unsortedStep56;
	private QuestStep unsortedStep58;
	private PuzzleWrapperStep rubbleMiner1;
	private PuzzleWrapperStep rubbleMiner2;
	private PuzzleWrapperStep rubbleMiner3;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(2, startQuest);
		steps.put(4, enterTomb);
		steps.put(6, unsortedStep6);
		steps.put(8, unsortedStep6);
		steps.put(10, unsortedStep10);
		steps.put(12, unsortedStep12);
		steps.put(14, unsortedStep12);
		steps.put(16, unsortedStep16);
		steps.put(18, unsortedStep18);
		steps.put(20, unsortedStep20);
		steps.put(22, unsortedStep20);
		steps.put(24, unsortedStep24);
		steps.put(26, unsortedStep26);
		steps.put(28, unsortedStep28);
		steps.put(30, unsortedStep30);
		steps.put(32, unsortedStep32);
		steps.put(34, unsortedStep34);
		steps.put(36, unsortedStep36);
		steps.put(38, unsortedStep38);
		steps.put(40, unsortedStep40);
		steps.put(42, unsortedStep42);
		steps.put(44, unsortedStep44);
		steps.put(46, unsortedStep46);
		steps.put(48, unsortedStep48);
		steps.put(50, unsortedStep50);
		steps.put(52, unsortedStep52);
		steps.put(54, unsortedStep54);
		steps.put(56, unsortedStep56);
		steps.put(58, unsortedStep58);

		return steps;
	}

	@Override
	protected void setupZones()
	{
	}

	@Override
	protected void setupRequirements()
	{
		dwellberries3 = new ItemRequirement("Dwellberries", ItemID.DWELLBERRIES, 3);
		ringOfLife = new ItemRequirement("Ring of life", ItemID.RING_OF_LIFE);
		anyPickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		anyGrappleableCrossbow = new ItemRequirement("Any crossbow", ItemCollections.CROSSBOWS).isNotConsumed();
		mithrilGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).isNotConsumed();
		// TODO: Check if the other insulated boots can be used
		insulatedBoots = new ItemRequirement("Insulated boots", ItemID.INSULATED_BOOTS).isNotConsumed();

		staminaPotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS, 1);
		prayerPotion = new ItemRequirement("Prayer potion", ItemCollections.PRAYER_POTIONS, 1);
		crushCombatGear = new ItemRequirement("Melee combat gear (crush preferred)", -1, -1);
		crushCombatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
	}

	public void setupSteps()
	{
		var todo = new NpcStep(this, 5, "TODO XD");

		startQuest = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Talk to Elias south of Ruins of Uzer (DLQ FAIRY RING).");
		startQuest.addDialogStep("Yes.");

		enterTomb = new ObjectStep(this, ObjectID.ENTRY_50201, new WorldPoint(3486, 3023, 0), "Enter the tomb south-west of Elias.");
		// TODO: Ensure player can get hint to return

		var insideTomb = new Zone(new WorldPoint(3842, 4603, 0), new WorldPoint(3900, 4547, 0));
		var insideTombReq = new ZoneRequirement(insideTomb);
		var hasFirstKey = new ItemRequirement("Mastaba Key", ItemID.MASTABA_KEY);
		getFirstKey = new ObjectStep(this, ObjectID.SKELETON_50350, new WorldPoint(3875, 4554, 0), "Get the first Mastaba key from the skeleton to the south of the entrance.");
		var hasSecondKey = new ItemRequirement("Mastaba Key", ItemID.MASTABA_KEY_30309);
		getSecondKey = new ObjectStep(this, ObjectID.SKELETON_50353, new WorldPoint(3880, 4585, 0), "Get the second Mastaba key from the skeleton east of the entrance.");
		var bySouthLever = new Zone(new WorldPoint(3893, 4554, 0), new WorldPoint(3894, 4552, 0));
		var bySouthLeverReq = new ZoneRequirement(bySouthLever);
		pullSouthLever = new ObjectStep(this, ObjectID.LEVER_50205, new WorldPoint(3894, 4553, 0), "Pull the lever to the south-east.", hasSecondKey);
		pullSouthLever.addDialogStep("Yes.");
		var getToSouthLever = new ObjectStep(this, ObjectID.ODD_MARKINGS_50207, new WorldPoint(3891, 4554, 0), "Search the Odd markings to the south to get to the south lever. Search the markings again if you fail.");
		var needToInsertKeyInSouthLever = new VarbitRequirement(11482, 0);
		var needToFlipSouthLever = new VarbitRequirement(11482, 1);
		var haveFlippedSouthLever = new VarbitRequirement(11482, 2);
		var leaveSouthLever = new ObjectStep(this, ObjectID.ODD_MARKINGS_50208, new WorldPoint(3892, 4554, 0), "Search the Odd markings next to you to get out.");
		pullSouthLever.addSubSteps(getToSouthLever, leaveSouthLever);

		var byNorthLever = new Zone(new WorldPoint(3894, 4597, 0), new WorldPoint(3893, 4599, 0));
		var byNorthLeverReq = new ZoneRequirement(byNorthLever);
		var getToNorthLever = new ObjectStep(this, ObjectID.ODD_MARKINGS_50208, new WorldPoint(3891, 4597, 0), "Search the Odd markings to the north to get to the north lever. Search the markings again if you fail.");
		pullNorthLever = new ObjectStep(this, ObjectID.LEVER_50205, new WorldPoint(3894, 4598, 0), "Pull the lever to the north-east.", hasFirstKey);
		pullNorthLever.addDialogStep("Yes.");
		var needToInsertKeyInNorthLever = new VarbitRequirement(11481, 0);
		var needToFlipNorthLever = new VarbitRequirement(11481, 1);
		var haveFlippedNorthLever = new VarbitRequirement(11481, 2);
		var leaveNorthLever = new ObjectStep(this, ObjectID.ODD_MARKINGS_50207, new WorldPoint(3892, 4597, 0), "Search the Odd markings next to you to get out.");
		pullNorthLever.addSubSteps(getToNorthLever, leaveNorthLever);

		unsortedStep6 = new ConditionalStep(this, enterTomb);

		// Get inside the tomb if you're not already inside. In case the user has teleported out or died to golem?
		((ConditionalStep) unsortedStep6).addStep(not(insideTombReq), enterTomb);

		// If the user has flipped the south lever & needs to get out of the little room
		((ConditionalStep) unsortedStep6).addStep(and(haveFlippedSouthLever, bySouthLeverReq), leaveSouthLever);
		// If the user has flipped the north lever & needs to get out of the little room
		((ConditionalStep) unsortedStep6).addStep(and(haveFlippedNorthLever, byNorthLeverReq), leaveNorthLever);

		// If the user has not already put the key in the south lever, and does not have the key
		((ConditionalStep) unsortedStep6).addStep(and(needToInsertKeyInSouthLever, not(hasSecondKey)), getSecondKey);
		// If the user has not already put the key in the north lever, and does not have the key
		((ConditionalStep) unsortedStep6).addStep(and(needToInsertKeyInNorthLever, not(hasFirstKey)), getFirstKey);

		// If the user has the key & stands by the south lever
		((ConditionalStep) unsortedStep6).addStep(and(needToFlipSouthLever, bySouthLeverReq), pullSouthLever);
		// If the user needs to flip the south lever, but is not inside the little room, get to the little room
		((ConditionalStep) unsortedStep6).addStep(not(haveFlippedSouthLever), getToSouthLever);

		// If the user has the key & stands by the north lever
		((ConditionalStep) unsortedStep6).addStep(and(needToFlipNorthLever, byNorthLeverReq), pullNorthLever);
		// If the user needs to flip the north lever, but is not inside the little room, get to the little room
		((ConditionalStep) unsortedStep6).addStep(needToFlipNorthLever, getToNorthLever);

		// Once last lever was pulled, quest varbit changed from 6 to 8, then 8 to 10 at the same tick
		// This might have to do with which order you pulled the levers in

		var golemArenaZone = new Zone(new WorldPoint(3856, 4592, 0), new WorldPoint(3884, 4599, 0));
		var insideGolenArena = new ZoneRequirement(golemArenaZone);
		enterGolemArena = new ObjectStep(this, ObjectID.IMPOSING_DOORS_50211, new WorldPoint(3885, 4597, 0), "Open the imposing doors, ready to fight the Golem guard.");
		fightGolemGuard = new NpcStep(this, NpcID.GOLEM_GUARD, new WorldPoint(3860, 4595, 0), "Fight the Golem guard. He is weak to crush style weapons. Use Protect from Melee to avoid damage from his attacks. When the screen shakes, step away from him to avoid taking damage.");
		unsortedStep10 = new ConditionalStep(this, enterGolemArena);
		// Get inside the tomb if you're not already inside. In case the user has teleported out or died to golem?
		unsortedStep10.addStep(not(insideTombReq), enterTomb);
		unsortedStep10.addStep(byNorthLeverReq, leaveNorthLever);
		unsortedStep10.addStep(bySouthLeverReq, leaveSouthLever);
		unsortedStep10.addStep(insideGolenArena, fightGolemGuard);

		var enterGolemArenaWithoutFight = new ObjectStep(this, ObjectID.IMPOSING_DOORS_50211, new WorldPoint(3885, 4597, 0), "Open the imposing doors to the north-east of the tomb.");
		enterTombBasement = new ObjectStep(this, ObjectID.STAIRS_55785, new WorldPoint(3860, 4596, 0), "Climb the stairs down the tomb basement.");
		enterTombBasement.addSubSteps(enterGolemArenaWithoutFight);

		solveTilePuzzle = new TilePuzzleSolver(this).puzzleWrapStep("Move across the floor tile puzzle.");

		var insideTombSecondFloor = new Zone(new WorldPoint(3719, 4674, 0), new WorldPoint(3770, 4732, 0));
		var insideTombSecondFloorAfterFinishingPuzzle = new Zone(new WorldPoint(3845, 4674, 0), new WorldPoint(3900, 4732, 0));
		var insideTombSecondFloorReq = new ZoneRequirement(insideTombSecondFloor, insideTombSecondFloorAfterFinishingPuzzle);

		var searchShelvesForUrn = new ObjectStep(this, ObjectID.SHELVES_55796, new WorldPoint(3854, 4722, 0), "Search the shelves to the west for an oil-filled canopic jar.");
		var oilFilledCanopicJar = new ItemRequirement("Oil-filled canopic jar", ItemID.CANOPIC_JAR_OIL);

		var inspectMurals = new ObjectStep(this, ObjectID.MURAL_55790, new WorldPoint(3852, 4687, 0), "Inspect the murals in the room to the south.", oilFilledCanopicJar);

		var finishedTilePuzzle = new VarbitRequirement(11483, 1);
		unsortedStep12 = new ConditionalStep(this, todo);
		((ConditionalStep) unsortedStep12).addStep(and(insideTombSecondFloorReq, finishedTilePuzzle, oilFilledCanopicJar), inspectMurals);
		((ConditionalStep) unsortedStep12).addStep(and(insideTombSecondFloorReq, finishedTilePuzzle, oilFilledCanopicJar), todo);
		((ConditionalStep) unsortedStep12).addStep(and(insideTombSecondFloorReq, finishedTilePuzzle), searchShelvesForUrn);
		((ConditionalStep) unsortedStep12).addStep(insideTombSecondFloorReq, solveTilePuzzle);
		((ConditionalStep) unsortedStep12).addStep(not(insideTombReq), enterTomb);
		((ConditionalStep) unsortedStep12).addStep(and(insideTombReq, insideGolenArena), enterTombBasement);
		((ConditionalStep) unsortedStep12).addStep(and(insideTombReq), enterGolemArenaWithoutFight);

		var oilAndBerryFilledCanopicJar = new ItemRequirement("Canopic jar (oil and berries)", ItemID.CANOPIC_JAR_OIL_AND_BERRIES);

		var combineJarWithDwellberries = new ItemStep(this, "Combine the Dwellberries with the Canopic jar.", oilFilledCanopicJar.highlighted(), dwellberries3.highlighted(), ringOfLife);
		var combineJarWithRingOfLife = new ItemStep(this, "Combine the Dwellberries with the Ring of life.", oilAndBerryFilledCanopicJar.highlighted(), ringOfLife.highlighted());

		unsortedStep16 = new ConditionalStep(this, todo);
		((ConditionalStep) unsortedStep16).addStep(oilAndBerryFilledCanopicJar, combineJarWithRingOfLife);
		((ConditionalStep) unsortedStep16).addStep(oilFilledCanopicJar, combineJarWithDwellberries);
		((ConditionalStep) unsortedStep16).addStep(and(insideTombSecondFloorReq, finishedTilePuzzle), searchShelvesForUrn);
		((ConditionalStep) unsortedStep16).addStep(not(insideTombReq), enterTomb);
		((ConditionalStep) unsortedStep16).addStep(and(insideTombReq, insideGolenArena), enterTombBasement);
		((ConditionalStep) unsortedStep16).addStep(and(insideTombReq), enterGolemArenaWithoutFight);

		var fairyRingDLQ = new TeleportItemRequirement("Fairy Ring [DLQ]", ItemCollections.FAIRY_STAFF);

		var returnToElias = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Return to to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToElias.addTeleport(fairyRingDLQ);
		var returnToEliasByWalking = new ObjectStep(this, ObjectID.STAIRS_55786, new WorldPoint(3894, 4714, 0), "Return to to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalking.addTeleport(fairyRingDLQ);
		var returnToEliasByWalkingMidway = new ObjectStep(this, ObjectID.STAIRS_50202, new WorldPoint(3848, 4577, 0), "Return to to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalkingMidway.addTeleport(fairyRingDLQ);
		var returnToEliasByWalkingMidwayGolem = new ObjectStep(this, ObjectID.IMPOSING_DOORS_50211, new WorldPoint(3885, 4597, 0), "Return to to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalkingMidwayGolem.addTeleport(fairyRingDLQ);

		returnToElias.addSubSteps(returnToEliasByWalking, returnToEliasByWalkingMidway, returnToEliasByWalkingMidwayGolem);

		unsortedStep18 = new ConditionalStep(this, returnToElias);
		((ConditionalStep)unsortedStep18).addStep(insideTombSecondFloorReq, returnToEliasByWalking);
		((ConditionalStep)unsortedStep18).addStep(insideGolenArena, returnToEliasByWalkingMidwayGolem);
		((ConditionalStep)unsortedStep18).addStep(insideTombReq, returnToEliasByWalkingMidway);
		// ardy cloak + fairy ring takes 50s, walking takes 1m12s

		var trollheimTeleport = new TeleportItemRequirement("Trollheim Teleport", ItemID.TROLLHEIM_TELEPORT);
		trollheimTeleport.addAlternates(ItemCollections.GHOMMALS_HILT);
		var headToTrollheim = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5007, new WorldPoint(2821, 3744, 0), "Enter the cave next to Trollheim. You can use a Trollheim teleport tablet or the GWD Ghommal's Hilt teleport to get close.");
		headToTrollheim.addTeleport(trollheimTeleport);

		var trollheimCave = new Zone(11167);
		var inTrollheimCave = new ZoneRequirement(trollheimCave);
		var continueThroughTrollheimCave = new ObjectStep(this, ObjectID.CREVASSE, new WorldPoint(2772, 10233, 0), "Continue through the Trollheim cave, exiting at the Crevasse to the north-west. Use Protect from Melee to avoid taking damage from the Ice Trolls.");

		var trollweissMountain = new Zone(11068);
		var onTrollweissMountain = new ZoneRequirement(trollweissMountain);
		var enterTrollweissCave = new ObjectStep(this, ObjectID.CAVE_55779, new WorldPoint(2809, 3861, 0), "Enter the Trollweiss cave to the east.");

		var trollweissCave1 = new Zone(11168);
		var inTrollweissCave = new ZoneRequirement(trollweissCave1);

		var whereToStandSprite = SpriteID.COMBAT_STYLE_PICKAXE_SMASH;

		// Rubble 50598 = 2 hits
		// Rubble 50587/50589 = 1 hit

		// var mineRubble1FromSouth = new ObjectStep(this, ObjectID.RUBBLE_50598, new WorldPoint(2764, 10266, 0), "Mine the rubble from the south.");
		// mineRubble1FromSouth.addTileMarker(new WorldPoint(2764, 10265, 0), whereToStandSprite);

		// var rubble1MinedOnce = new ObjectCondition(ObjectID.RUBBLE_50589, new WorldPoint(2764, 10266, 0));
		// var rubble2Mined = not(new ObjectCondition(ObjectID.RUBBLE_50589, new WorldPoint(2775, 10258, 0)));

		// var mineRubble2FromSouth = new ObjectStep(this, ObjectID.RUBBLE_50587, new WorldPoint(2775, 10258, 0), "Mine the rubble from the south.");
		// mineRubble2FromSouth.addTileMarker(new WorldPoint(2775, 10257, 0), whereToStandSprite);
		// mineRubble2FromSouth.setLinePoints(List.of(new WorldPoint(2763, 10264, 0), new WorldPoint(2769, 10254, 0), new WorldPoint(2775, 10255, 0)));

		rubbleMiner1 = new RubbleSolverOne(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner2 = new RubbleSolverTwo(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner3 = new RubbleSolverThree(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");

		unsortedStep20 = new ConditionalStep(this, headToTrollheim);
		((ConditionalStep) unsortedStep20).addStep(inTrollweissCave, rubbleMiner1);
		((ConditionalStep) unsortedStep20).addStep(onTrollweissMountain, enterTrollweissCave);
		((ConditionalStep) unsortedStep20).addStep(inTrollheimCave, continueThroughTrollheimCave);

		unsortedStep24 = new ConditionalStep(this, headToTrollheim);
		((ConditionalStep) unsortedStep24).addStep(inTrollweissCave, rubbleMiner2);
		((ConditionalStep) unsortedStep24).addStep(onTrollweissMountain, enterTrollweissCave);
		((ConditionalStep) unsortedStep24).addStep(inTrollheimCave, continueThroughTrollheimCave);

		unsortedStep26 = new ConditionalStep(this, headToTrollheim);
		((ConditionalStep) unsortedStep26).addStep(inTrollweissCave, rubbleMiner3);
		((ConditionalStep) unsortedStep26).addStep(onTrollweissMountain, enterTrollweissCave);
		((ConditionalStep) unsortedStep26).addStep(inTrollheimCave, continueThroughTrollheimCave);

		unsortedStep26 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 26");
		unsortedStep28 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 28");
		unsortedStep30 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 30");
		unsortedStep32 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 32");
		unsortedStep34 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 34");
		unsortedStep36 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 36");
		unsortedStep38 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 38");
		unsortedStep40 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 40");
		unsortedStep42 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 42");
		unsortedStep44 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 44");
		unsortedStep46 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 46");
		unsortedStep48 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 48");
		unsortedStep50 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 50");
		unsortedStep52 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 52");
		unsortedStep54 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 54");
		unsortedStep56 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 56");
		unsortedStep58 = new NpcStep(this, NpcID.YELLOW_FORTUNE_SECRETARY, "Step 58");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			dwellberries3,
			ringOfLife,
			anyPickaxe,
			anyGrappleableCrossbow,
			mithrilGrapple,
			insulatedBoots
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			staminaPotion,
			prayerPotion,
			crushCombatGear,
			food
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new CombatLevelRequirement(85),
			new SkillRequirement(Skill.PRAYER, 43, false, "43+ Prayer to use protection prayers")
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.DEFENDER_OF_VARROCK, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.TROLL_ROMANCE, QuestState.FINISHED),
			new SkillRequirement(Skill.MINING, 64),
			new SkillRequirement(Skill.RANGED, 62),
			new SkillRequirement(Skill.THIEVING, 62),
			new SkillRequirement(Skill.AGILITY, 61),
			new SkillRequirement(Skill.STRENGTH, 58),
			new SkillRequirement(Skill.SLAYER, 37)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Golem guard (lvl 141)",
			"Arrav (lvl 339)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.MINING, 40_000),
			new ExperienceReward(Skill.THIEVING, 40_000),
			new ExperienceReward(Skill.AGILITY, 40_000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Zemouregal's Fort")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Tomb Raiding", List.of(
			startQuest,
			enterTomb,
			getFirstKey,
			getSecondKey,
			pullSouthLever,
			pullNorthLever,
			enterGolemArena,
			fightGolemGuard,
			enterTombBasement,
			solveTilePuzzle,
			unsortedStep16,
			unsortedStep18
		), List.of(
			new FreeInventorySlotRequirement(2)
			// TODO
		)));
		panels.add(new PanelDetails("Fort Invasion", List.of(
			unsortedStep20,
			rubbleMiner1,
			unsortedStep22,
			unsortedStep24,
			unsortedStep26,
			unsortedStep28,
			unsortedStep30,
			unsortedStep32,
			unsortedStep34,
			unsortedStep36,
			unsortedStep38,
			unsortedStep40,
			unsortedStep42,
			unsortedStep44,
			unsortedStep46,
			unsortedStep48,
			unsortedStep50,
			unsortedStep52,
			unsortedStep54,
			unsortedStep56,
			unsortedStep58
			// TODO
		), List.of(staminaPotion, prayerPotion, crushCombatGear, food)));
		panels.add(new PanelDetails("Hearty Heist", List.of(
			// TODO
		), List.of(
			// TODO
		)));

		panels.add(new PanelDetails("The Hero of Avarrocka", List.of(
			// TODO
		), List.of(
			// TODO
		)));

		return panels;
	}

}
