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
import com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers.RubbleSolverFour;
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
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

/**
 * The quest guide for the "The Curse of Arrav" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/The_Curse_of_Arrav">The OSRS wiki guide</a> and <a href="https://www.youtube.com/watch?v=IIMTpgD0BUY">Slayermusiq1's Quest Guide</a> was referenced for this guide
 */
@SuppressWarnings("FieldCanBeLocal")
public class TheCurseOfArrav extends BasicQuestHelper
{
	// Required items
	private ItemRequirement dwellberries3;
	private ItemRequirement ringOfLife;
	private ItemRequirement anyPickaxe;
	private ItemRequirement anyGrappleableCrossbow;
	private ItemRequirement mithrilGrapple;
	private ItemRequirement insulatedBoots;

	// Recommended items
	private TeleportItemRequirement fairyRingDLQ;
	// trollheim teleport / ghommal's hilt
	// antivenom
	// lumberyard teleport
	// melee (crush) combat gear for golem
	// ranged combat gear for arrav
	private ItemRequirement golemCombatGear;
	private ItemRequirement arravCombatGear;
	private ItemRequirement food;
	private ItemRequirement staminaPotion;
	private ItemRequirement prayerPotion;
	private FreeInventorySlotRequirement twoFreeInventorySlots;
	// 2 inv slots

	// Mid-quest item requirements
	private ItemRequirement canopicJarFull;
	private ItemRequirement basePlans;
	private ItemRequirement baseKey;

	// Zones & their requirements
	/// Top floor of the tomb (Uzer Mastaba)
	private ZoneRequirement insideTomb;
	/// Second floor of the tomb (Uzer Mastaba)
	private ZoneRequirement insideTombSecondFloor;
	private ZoneRequirement inZemouregalsBaseSection1;
	private ZoneRequirement inZemouregalsBaseSection2;
	private ZoneRequirement inZemouregalsBaseSection3;
	private ZoneRequirement inZemouregalsBaseSection4;
	private ZoneRequirement inZemouregalsBaseKitchen;
	private ZoneRequirement inZemouregalsBaseSewer;

	// Steps
	/// 0 + 2
	private NpcStep startQuest;

	/// 4
	private ObjectStep enterTomb;

	/// 6 + 8
	private ConditionalStep unsortedStep6;
	private ObjectStep getFirstKey;
	private ObjectStep getSecondKey;
	private ObjectStep pullSouthLever;
	private ObjectStep pullNorthLever;

	/// 10
	private ConditionalStep fightGolemCond;
	private ObjectStep enterGolemArena;
	private NpcStep fightGolemGuard;

	/// 12 + 14
	private ConditionalStep finishTilePuzzleAndGetCanopicJar;
	private ObjectStep enterTombBasement;
	private PuzzleWrapperStep solveTilePuzzle;
	private ObjectStep searchShelvesForUrn;
	private ObjectStep inspectMurals;
	private ConditionalStep unsortedStep16;
	private ConditionalStep unsortedStep18;
	private ConditionalStep unsortedStep20;
	private ConditionalStep unsortedStep24;
	private ConditionalStep unsortedStep26;
	private ConditionalStep unsortedStep28;
	private ConditionalStep unsortedStep30;
	private ConditionalStep unsortedStep32;
	private ConditionalStep unsortedStep34;
	private ConditionalStep unsortedStep42;
	private ConditionalStep unsortedStep44;
	private ConditionalStep unsortedStep46;
	private ConditionalStep unsortedStep48;
	private ConditionalStep unsortedStep52;
	private ConditionalStep unsortedStep54;
	private ConditionalStep unsortedStep56;
	private NpcStep finishQuest;

	private PuzzleWrapperStep rubbleMiner1;
	private PuzzleWrapperStep rubbleMiner2;
	private PuzzleWrapperStep rubbleMiner3;
	private PuzzleWrapperStep rubbleMiner4;
	private PuzzleWrapperStep metalDoorSolver;
	private NpcStep returnToEliasWithBaseItems;
	private NpcStep headToZemouregalsBaseAndTalkToElias;
	private ObjectStep enterZemouregalsBase;
	private DetailedQuestStep getToBackOfZemouregalsBase;
	private QuestRequirement haveKilledGolem;
	private VarbitRequirement finishedTilePuzzle;
	private QuestRequirement haveMadeCanopicJar;
	private QuestRequirement haveMinedAFullPath;
	private QuestRequirement haveUsedPlans;
	private QuestRequirement haveUsedKey;
	private QuestRequirement haveMetArrav;
	private ObjectStep searchTableForDecoderStrips;
	private ObjectStep enterStorageRoom;
	private ObjectStep openChestForCodeKey;
	private NpcStep interpretPlansWithElias;
	private NpcStep fightArrav;
	private ObjectStep enterBossRoom;
	private ObjectStep grappleAcross;
	private ObjectStep openMetalDoors;


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
		steps.put(10, fightGolemCond);
		steps.put(12, finishTilePuzzleAndGetCanopicJar);
		steps.put(14, finishTilePuzzleAndGetCanopicJar);
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
		steps.put(36, returnToEliasWithBaseItems);
		steps.put(38, interpretPlansWithElias);
		steps.put(40, interpretPlansWithElias);
		steps.put(42, unsortedStep42);
		steps.put(44, unsortedStep44);
		steps.put(46, unsortedStep46);
		steps.put(48, unsortedStep48);
		steps.put(50, unsortedStep48);
		steps.put(52, unsortedStep52);
		steps.put(54, unsortedStep54);
		steps.put(56, unsortedStep56);
		steps.put(58, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		insideTomb = new ZoneRequirement(new Zone(new WorldPoint(3842, 4603, 0), new WorldPoint(3900, 4547, 0)));
		insideTombSecondFloor = new ZoneRequirement(
			new Zone(new WorldPoint(3719, 4674, 0), new WorldPoint(3770, 4732, 0)),
			new Zone(new WorldPoint(3845, 4674, 0), new WorldPoint(3900, 4732, 0))
		);

		// Right as you head into the base
		inZemouregalsBaseSection1 = new ZoneRequirement(new Zone(new WorldPoint(3536, 4577, 0), new WorldPoint(3564, 4547, 0)));
		// After you've passed the first door
		inZemouregalsBaseSection2 = new ZoneRequirement(
			new Zone(new WorldPoint(3535, 4562, 0), new WorldPoint(3523, 4602, 0)),
			new Zone(new WorldPoint(3523, 4602, 0), new WorldPoint(3539, 4594, 0))
		);
		// After you've passed the second door
		inZemouregalsBaseSection3 = new ZoneRequirement(new Zone(new WorldPoint(3576, 4610, 0), new WorldPoint(3540, 4588, 0)));
		// After you've passed the third door (the one requiring the base key)
		inZemouregalsBaseSection4 = new ZoneRequirement(new Zone(new WorldPoint(3577, 4615, 0), new WorldPoint(3605, 4592, 0)));
		inZemouregalsBaseKitchen = new ZoneRequirement(new Zone(new WorldPoint(3613, 4604, 0), new WorldPoint(3606, 4598, 0)));
		inZemouregalsBaseSewer = new ZoneRequirement(new Zone(14919));
	}

	@Override
	protected void setupRequirements()
	{
		haveKilledGolem = new QuestRequirement(QuestHelperQuest.THE_CURSE_OF_ARRAV, 12);
		finishedTilePuzzle = new VarbitRequirement(11483, 1);
		haveMadeCanopicJar = new QuestRequirement(QuestHelperQuest.THE_CURSE_OF_ARRAV, 18);
		haveMinedAFullPath = new QuestRequirement(QuestHelperQuest.THE_CURSE_OF_ARRAV, 30);
		haveUsedPlans = new QuestRequirement(QuestHelperQuest.THE_CURSE_OF_ARRAV, 38);
		haveUsedKey = new QuestRequirement(QuestHelperQuest.THE_CURSE_OF_ARRAV, 46);
		haveMetArrav = new QuestRequirement(QuestHelperQuest.THE_CURSE_OF_ARRAV, 54);

		// Required items
		dwellberries3 = new ItemRequirement("Dwellberries", ItemID.DWELLBERRIES, 3);
		dwellberries3.setConditionToHide(haveMadeCanopicJar);
		ringOfLife = new ItemRequirement("Ring of life", ItemID.RING_OF_LIFE);
		ringOfLife.setConditionToHide(haveMadeCanopicJar);
		anyPickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		anyPickaxe.setConditionToHide(haveMinedAFullPath);
		anyGrappleableCrossbow = new ItemRequirement("Any crossbow", ItemCollections.CROSSBOWS).isNotConsumed();
		anyGrappleableCrossbow.setConditionToHide(haveMetArrav);
		mithrilGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).isNotConsumed();
		mithrilGrapple.setConditionToHide(haveMetArrav);
		insulatedBoots = new ItemRequirement("Insulated boots", ItemID.INSULATED_BOOTS).isNotConsumed();

		// Recommended items
		fairyRingDLQ = new TeleportItemRequirement("Fairy Ring [DLQ]", ItemCollections.FAIRY_STAFF);
		staminaPotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS, 1);
		prayerPotion = new ItemRequirement("Prayer potion", ItemCollections.PRAYER_POTIONS, 1);
		golemCombatGear = new ItemRequirement("Crush or ranged combat gear to fight the Golem guard", -1, -1);
		golemCombatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		golemCombatGear.setConditionToHide(haveKilledGolem);
		arravCombatGear = new ItemRequirement("Ranged or melee combat gear for killing Arrav", -1, -1);
		arravCombatGear.setTooltip("If you bring Melee gear, it's advisable to bring some ranged weapon swap like darts for killing the Zombies as they spawn");
		arravCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		twoFreeInventorySlots = new FreeInventorySlotRequirement(2);
	}

	public void setupSteps()
	{
		var todo = new NpcStep(this, 5, "TODO XD");

		startQuest = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Talk to Elias south of Ruins of Uzer to start the quest.");
		startQuest.addTeleport(fairyRingDLQ);
		startQuest.addDialogStep("Yes.");

		enterTomb = new ObjectStep(this, ObjectID.ENTRY_50201, new WorldPoint(3486, 3023, 0), "Enter the tomb south-west of Elias.");

		var hasFirstKey = new ItemRequirement("Mastaba Key", ItemID.MASTABA_KEY);
		getFirstKey = new ObjectStep(this, ObjectID.SKELETON_50350, new WorldPoint(3875, 4554, 0), "Get the first Mastaba key from the skeleton south of the entrance.");
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
		unsortedStep6.addStep(not(insideTomb), enterTomb);

		// If the user has flipped the south lever & needs to get out of the little room
		unsortedStep6.addStep(and(haveFlippedSouthLever, bySouthLeverReq), leaveSouthLever);
		// If the user has flipped the north lever & needs to get out of the little room
		unsortedStep6.addStep(and(haveFlippedNorthLever, byNorthLeverReq), leaveNorthLever);

		// If the user has not already put the key in the south lever, and does not have the key
		unsortedStep6.addStep(and(needToInsertKeyInSouthLever, not(hasSecondKey)), getSecondKey);
		// If the user has not already put the key in the north lever, and does not have the key
		unsortedStep6.addStep(and(needToInsertKeyInNorthLever, not(hasFirstKey)), getFirstKey);

		// If the user has the key & stands by the south lever
		unsortedStep6.addStep(and(needToFlipSouthLever, bySouthLeverReq), pullSouthLever);
		// If the user needs to flip the south lever, but is not inside the little room, get to the little room
		unsortedStep6.addStep(not(haveFlippedSouthLever), getToSouthLever);

		// If the user has the key & stands by the north lever
		unsortedStep6.addStep(and(needToFlipNorthLever, byNorthLeverReq), pullNorthLever);
		// If the user needs to flip the north lever, but is not inside the little room, get to the little room
		unsortedStep6.addStep(needToFlipNorthLever, getToNorthLever);

		// Once last lever was pulled, quest varbit changed from 6 to 8, then 8 to 10 at the same tick
		// This might have to do with which order you pulled the levers in

		var golemArenaZone = new Zone(new WorldPoint(3856, 4592, 0), new WorldPoint(3884, 4599, 0));
		var insideGolenArena = new ZoneRequirement(golemArenaZone);
		enterGolemArena = new ObjectStep(this, ObjectID.IMPOSING_DOORS_50211, new WorldPoint(3885, 4597, 0), "Open the imposing doors, ready to fight the Golem guard.");
		fightGolemGuard = new NpcStep(this, NpcID.GOLEM_GUARD, new WorldPoint(3860, 4595, 0), "Fight the Golem guard. It is weak to crush style weapons. Use Protect from Melee to avoid damage from his attacks. When the screen shakes, step away from him to avoid taking damage.");
		fightGolemCond = new ConditionalStep(this, enterGolemArena);
		// Get inside the tomb if you're not already inside. In case the user has teleported out or died to golem?
		fightGolemCond.addStep(not(insideTomb), enterTomb);
		fightGolemCond.addStep(byNorthLeverReq, leaveNorthLever);
		fightGolemCond.addStep(bySouthLeverReq, leaveSouthLever);
		fightGolemCond.addStep(insideGolenArena, fightGolemGuard);

		var enterGolemArenaWithoutFight = new ObjectStep(this, ObjectID.IMPOSING_DOORS_50211, new WorldPoint(3885, 4597, 0), "Open the imposing doors to the north-east of the tomb.");
		enterTombBasement = new ObjectStep(this, ObjectID.STAIRS_55785, new WorldPoint(3860, 4596, 0), "Climb the stairs down the tomb basement.");
		enterTombBasement.addSubSteps(enterGolemArenaWithoutFight);

		solveTilePuzzle = new TilePuzzleSolver(this).puzzleWrapStep("Move across the floor tile puzzle.");

		searchShelvesForUrn = new ObjectStep(this, ObjectID.SHELVES_55796, new WorldPoint(3854, 4722, 0), "Search the shelves to the west for an oil-filled canopic jar.");
		var oilFilledCanopicJar = new ItemRequirement("Oil-filled canopic jar", ItemID.CANOPIC_JAR_OIL);

		inspectMurals = new ObjectStep(this, ObjectID.MURAL_55790, new WorldPoint(3852, 4687, 0), "Inspect the murals in the room to the south.", oilFilledCanopicJar);

		finishTilePuzzleAndGetCanopicJar = new ConditionalStep(this, enterTomb);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTombSecondFloor, finishedTilePuzzle, oilFilledCanopicJar), inspectMurals);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTombSecondFloor, finishedTilePuzzle), searchShelvesForUrn);
		finishTilePuzzleAndGetCanopicJar.addStep(insideTombSecondFloor, solveTilePuzzle);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTomb, insideGolenArena), enterTombBasement);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTomb), enterGolemArenaWithoutFight);

		var oilAndBerryFilledCanopicJar = new ItemRequirement("Canopic jar (oil and berries)", ItemID.CANOPIC_JAR_OIL_AND_BERRIES);

		var combineJarWithDwellberries = new ItemStep(this, "Combine the Dwellberries with the Canopic jar.", oilFilledCanopicJar.highlighted(), dwellberries3.highlighted(), ringOfLife);
		var combineJarWithRingOfLife = new ItemStep(this, "Combine the Dwellberries with the Ring of life.", oilAndBerryFilledCanopicJar.highlighted(), ringOfLife.highlighted());

		unsortedStep16 = new ConditionalStep(this, todo);
		unsortedStep16.addStep(oilAndBerryFilledCanopicJar, combineJarWithRingOfLife);
		unsortedStep16.addStep(oilFilledCanopicJar, combineJarWithDwellberries);
		unsortedStep16.addStep(and(insideTombSecondFloor, finishedTilePuzzle), searchShelvesForUrn);
		unsortedStep16.addStep(not(insideTomb), enterTomb);
		unsortedStep16.addStep(and(insideTomb, insideGolenArena), enterTombBasement);
		unsortedStep16.addStep(and(insideTomb), enterGolemArenaWithoutFight);


		var returnToElias = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToElias.addTeleport(fairyRingDLQ);
		var returnToEliasByWalking = new ObjectStep(this, ObjectID.STAIRS_55786, new WorldPoint(3894, 4714, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalking.addTeleport(fairyRingDLQ);
		var returnToEliasByWalkingMidway = new ObjectStep(this, ObjectID.STAIRS_50202, new WorldPoint(3848, 4577, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalkingMidway.addTeleport(fairyRingDLQ);
		var returnToEliasByWalkingMidwayGolem = new ObjectStep(this, ObjectID.IMPOSING_DOORS_50211, new WorldPoint(3885, 4597, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalkingMidwayGolem.addTeleport(fairyRingDLQ);

		returnToElias.addSubSteps(returnToEliasByWalking, returnToEliasByWalkingMidway, returnToEliasByWalkingMidwayGolem);

		unsortedStep18 = new ConditionalStep(this, returnToElias);
		unsortedStep18.addStep(insideTombSecondFloor, returnToEliasByWalking);
		unsortedStep18.addStep(insideGolenArena, returnToEliasByWalkingMidwayGolem);
		unsortedStep18.addStep(insideTomb, returnToEliasByWalkingMidway);
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

		rubbleMiner1 = new RubbleSolverOne(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner2 = new RubbleSolverTwo(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner3 = new RubbleSolverThree(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner4 = new RubbleSolverFour(this).puzzleWrapStep("Mine the rubble and make your way through the cave.");

		rubbleMiner1.addSubSteps(rubbleMiner2, rubbleMiner3, rubbleMiner4);

		unsortedStep20 = new ConditionalStep(this, headToTrollheim);
		unsortedStep20.addStep(inTrollweissCave, rubbleMiner1);
		unsortedStep20.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep20.addStep(inTrollheimCave, continueThroughTrollheimCave);

		unsortedStep24 = new ConditionalStep(this, headToTrollheim);
		unsortedStep24.addStep(inTrollweissCave, rubbleMiner2);
		unsortedStep24.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep24.addStep(inTrollheimCave, continueThroughTrollheimCave);

		unsortedStep26 = new ConditionalStep(this, headToTrollheim);
		unsortedStep26.addStep(inTrollweissCave, rubbleMiner3);
		unsortedStep26.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep26.addStep(inTrollheimCave, continueThroughTrollheimCave);

		unsortedStep28 = new ConditionalStep(this, headToTrollheim);
		unsortedStep28.addStep(inTrollweissCave, rubbleMiner4);
		unsortedStep28.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep28.addStep(inTrollheimCave, continueThroughTrollheimCave);


		var climbUpstairsAndTalkToArrav = new ObjectStep(this, ObjectID.STAIRS_50508, new WorldPoint(2811, 10267, 0), "Climb up the stairs in the room with the red tile floor and talk to Arrav.");
		unsortedStep30 = new ConditionalStep(this, headToTrollheim);
		unsortedStep30.addStep(inTrollweissCave, climbUpstairsAndTalkToArrav);
		unsortedStep30.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep30.addStep(inTrollheimCave, continueThroughTrollheimCave);

		var arravHouseFirstRoom = new Zone(new WorldPoint(2848, 3868, 0), new WorldPoint(2858, 3873, 0));
		var inArravHouseFirstRoom = new ZoneRequirement(arravHouseFirstRoom);
		var talkToArrav = new NpcStep(this, NpcID.ARRAV_14129, new WorldPoint(2856, 3871, 0), "Talk to Arrav.");

		unsortedStep32 = new ConditionalStep(this, headToTrollheim);
		unsortedStep32.addStep(inArravHouseFirstRoom, talkToArrav);
		unsortedStep32.addStep(inTrollweissCave, climbUpstairsAndTalkToArrav);
		unsortedStep32.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep32.addStep(inTrollheimCave, continueThroughTrollheimCave);

		var arravHouseSecondRoom = new Zone(new WorldPoint(2863, 3865, 0), new WorldPoint(2859, 3873, 0));
		var inArravHouseSecondRoom = new ZoneRequirement(arravHouseSecondRoom);

		var goToNextRoom = new ObjectStep(this, ObjectID.DOOR_50514, new WorldPoint(2859, 3870, 0), "Enter the room to your east and search the tapestry for ?.");
		var searchTapestry = new ObjectStep(this, ObjectID.TAPESTRY_50516, new WorldPoint(2861, 3865, 0), "Search the tapestry in the south of the room.");
		unsortedStep34 = new ConditionalStep(this, headToTrollheim);
		unsortedStep34.addStep(inArravHouseSecondRoom, searchTapestry);
		unsortedStep34.addStep(inArravHouseFirstRoom, goToNextRoom);
		unsortedStep34.addStep(inTrollweissCave, climbUpstairsAndTalkToArrav);
		unsortedStep34.addStep(onTrollweissMountain, enterTrollweissCave);
		unsortedStep34.addStep(inTrollheimCave, continueThroughTrollheimCave);


		var tapestryFindText = "Can be acquired by heading back to Zemouregal's Fort past the Trollweiss mining dungeon and searching the tapestry.";
		basePlans = new ItemRequirement("Base plans", ItemID.BASE_PLANS);
		basePlans.setConditionToHide(haveUsedPlans);
		basePlans.setTooltip(tapestryFindText); // only needed until varbit 38
		baseKey = new ItemRequirement("Base key", ItemID.BASE_KEY);
		baseKey.setConditionToHide(haveUsedKey);
		baseKey.setTooltip(tapestryFindText); // no longer needed when varbit hits 46

		returnToEliasWithBaseItems = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Return to Elias south of Ruins of Uzer and ask him for help interpreting the plans.", basePlans, baseKey);
		returnToEliasWithBaseItems.addTeleport(fairyRingDLQ);

		interpretPlansWithElias = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Return to Elias south of Ruins of Uzer and ask him for help interpreting the plans.", baseKey);
		interpretPlansWithElias.addTeleport(fairyRingDLQ);

		returnToEliasWithBaseItems.addSubSteps(interpretPlansWithElias);

		// 40 -> 42
		// 9658: 5 -> 6

		canopicJarFull = new ItemRequirement("Canopic jar (full)", ItemID.CANOPIC_JAR_FULL);
		canopicJarFull.setTooltip("You can get a new one from Elias at the entrance of Zemouregal's base if you've lost it."); // this is at least true after the mining part
		// need 1 inventory slot free
		headToZemouregalsBaseAndTalkToElias = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3341, 3516, 0), "Head to Zemouregal's base east of Varrock's sawmill and talk to Elias.", anyGrappleableCrossbow, mithrilGrapple, arravCombatGear, insulatedBoots, canopicJarFull, baseKey); // todo add teleport
		headToZemouregalsBaseAndTalkToElias.addDialogStep("Ready when you are.");
		unsortedStep42 = new ConditionalStep(this, headToZemouregalsBaseAndTalkToElias);
		enterZemouregalsBase = new ObjectStep(this, NullObjectID.NULL_50689, new WorldPoint(3343, 3515, 0), "Enter Zemouregal's base east of Varrock's sawmill.", anyGrappleableCrossbow, mithrilGrapple, arravCombatGear, insulatedBoots, canopicJarFull, baseKey); // todo add teleport


		getToBackOfZemouregalsBase = new DetailedQuestStep(this, "Make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.");
		var passZemouregalsBaseDoor1 = new ObjectStep(this, ObjectID.GATE_50149, new WorldPoint(3536, 4571, 0), "Open the gate and make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.", baseKey, canopicJarFull, insulatedBoots);
		var passZemouregalsBaseDoor2 = new ObjectStep(this, ObjectID.GATE_50150, new WorldPoint(3540, 4597, 0), "Open the gate and make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.", baseKey, canopicJarFull, insulatedBoots);
		var passZemouregalsBaseDoor3 = new ObjectStep(this, ObjectID.DOOR_50152, new WorldPoint(3576, 4604, 0), "Open the door and make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.", baseKey, canopicJarFull, insulatedBoots);
		var passZemouregalsBaseDoor4 = new ObjectStep(this, ObjectID.GATE_50537, new WorldPoint(3605, 4603, 0), "Open the gate and make your way to the back of Zemouregal's base.", canopicJarFull, insulatedBoots);

		unsortedStep44 = new ConditionalStep(this, enterZemouregalsBase);
		unsortedStep44.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unsortedStep44.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unsortedStep44.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		var enterZemouregalsBaseSewer = new ObjectStep(this, ObjectID.PIPE_50523, new WorldPoint(3609, 4598, 0), "Enter the sewers and make your way to the back of Zemouregal's base.", canopicJarFull, insulatedBoots.highlighted().equipped());

		var exitZemouregalsBaseSewer = new ObjectStep(this, ObjectID.PIPE_50525, new WorldPoint(3741, 4573, 0), "Head south to exit the sewers and make your way to the back of Zemouregal's base.", canopicJarFull, insulatedBoots.highlighted().equipped());

		getToBackOfZemouregalsBase.addSubSteps(passZemouregalsBaseDoor1, passZemouregalsBaseDoor2, passZemouregalsBaseDoor3, passZemouregalsBaseDoor4, enterZemouregalsBaseSewer, exitZemouregalsBaseSewer);

		// 44 -> 46 when opening door, consuming the base key

		unsortedStep46 = new ConditionalStep(this, enterZemouregalsBase);
		unsortedStep46.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		unsortedStep46.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		unsortedStep46.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		unsortedStep46.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unsortedStep46.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unsortedStep46.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		var inSecondPart = new ZoneRequirement(new Zone(new WorldPoint(3590, 4538, 0), new WorldPoint(3622, 4597, 0)));
		var inStorageRoom = new ZoneRequirement(new Zone(new WorldPoint(3614, 4571, 0), new WorldPoint(3605, 4563, 0)));
		var decoderStrips = new ItemRequirement("Decoder strips", ItemID.DECODER_STRIPS);
		searchTableForDecoderStrips = new ObjectStep(this, ObjectID.TABLE_50533, "Search the table for some decoder strips.");
		enterStorageRoom = new ObjectStep(this, ObjectID.GATE_50537, new WorldPoint(3609, 4572, 0), "Enter the storage room to the south-east.");
		var exitStorageRoom = new ObjectStep(this, ObjectID.GATE_50537, new WorldPoint(3609, 4572, 0), "Exit the storage room.");
		var codeKey = new ItemRequirement("Code key", ItemID.CODE_KEY);

		openChestForCodeKey = new ObjectStep(this, ObjectID.CHEST_50530, new WorldPoint(3609, 4565, 0), "Open the chest for the code key.");

		var metalDoorSolverReal = new MetalDoorSolver(this);
		metalDoorSolverReal.addSubSteps(exitStorageRoom);

		this.metalDoorSolver = metalDoorSolverReal.puzzleWrapStep();


		// also used for 50
		unsortedStep48 = new ConditionalStep(this, enterZemouregalsBase);
		// TODO: DECODER STRIPS ARE NOT NECESSARY. REMOVE THEM :)
		unsortedStep48.addStep(and(inSecondPart, not(inStorageRoom), decoderStrips, codeKey), metalDoorSolver);
		unsortedStep48.addStep(and(inStorageRoom, not(decoderStrips)), searchTableForDecoderStrips);
		unsortedStep48.addStep(and(inStorageRoom, not(codeKey)), openChestForCodeKey);
		unsortedStep48.addStep(and(inStorageRoom, decoderStrips), exitStorageRoom);
		unsortedStep48.addStep(and(inSecondPart, not(codeKey)), enterStorageRoom);
		unsortedStep48.addStep(and(inSecondPart, not(decoderStrips)), enterStorageRoom);
		unsortedStep48.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		unsortedStep48.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		unsortedStep48.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		unsortedStep48.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unsortedStep48.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unsortedStep48.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		openMetalDoors = new ObjectStep(this, ObjectID.METAL_DOORS, new WorldPoint(3612, 4582, 0), "Step through through the metal doors.", canopicJarFull, anyGrappleableCrossbow, mithrilGrapple);

		var inGrapplePuzzleRoom = new ZoneRequirement(new Zone(new WorldPoint(3612, 4587, 0), new WorldPoint(3625, 4579, 0)));
		grappleAcross = new ObjectStep(this, ObjectID.PIPE_50542, new WorldPoint(3615, 4582, 0), "Grapple across the pipe", anyGrappleableCrossbow.highlighted().equipped(), mithrilGrapple.highlighted().equipped());

		var pastGrapplePuzzleRoom = new ZoneRequirement(new Zone(new WorldPoint(3621, 4589, 0), new WorldPoint(3645, 4578, 0)));

		enterBossRoom = new ObjectStep(this, ObjectID.PEDESTAL_50539, new WorldPoint(3638, 4582, 0), "Attempt to take Arrav's heart from the pedestal, ready for a fight. Kill zombies as they appear (ranged weapons are handy here). Avoid the venom pools they spawn. Use Protect from Melee to avoid some of the incoming damage. When Arrav throws an axe towards you, step to the side or behind him.", arravCombatGear);

		unsortedStep52 = new ConditionalStep(this, enterZemouregalsBase);
		unsortedStep52.addStep(pastGrapplePuzzleRoom, enterBossRoom);
		unsortedStep52.addStep(inGrapplePuzzleRoom, grappleAcross);
		unsortedStep52.addStep(inSecondPart, openMetalDoors);
		unsortedStep52.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		unsortedStep52.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		unsortedStep52.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		unsortedStep52.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unsortedStep52.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unsortedStep52.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		// User has engaged Arrav

		fightArrav = new NpcStep(this, NpcID.ARRAV_14132, new WorldPoint(3635, 4582, 0), "fight arrav xd");
		unsortedStep54 = new ConditionalStep(this, enterZemouregalsBase);
		unsortedStep54.addStep(or(pastGrapplePuzzleRoom, inGrapplePuzzleRoom), fightArrav);
		unsortedStep54.addStep(inSecondPart, openMetalDoors);
		unsortedStep54.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		unsortedStep54.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		unsortedStep54.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		unsortedStep54.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unsortedStep54.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unsortedStep54.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);


		// 54 -> 56 when beating arrav

		var watchTheDialog = new DetailedQuestStep(this, "Watch the dialog.");
		fightArrav.addSubSteps(watchTheDialog);
		unsortedStep56 = new ConditionalStep(this, enterZemouregalsBase);
		unsortedStep56.addStep(or(pastGrapplePuzzleRoom, inGrapplePuzzleRoom), watchTheDialog);
		unsortedStep56.addStep(inSecondPart, openMetalDoors);
		unsortedStep56.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		unsortedStep56.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		unsortedStep56.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		unsortedStep56.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unsortedStep56.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unsortedStep56.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		finishQuest = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3505, 3037, 0), "Talk to Elias to finish the quest.");
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
			golemCombatGear,
			arravCombatGear,
			food
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			twoFreeInventorySlots,
			new CombatLevelRequirement(85),
			new SkillRequirement(Skill.PRAYER, 43, false, "43 Prayer to use Protect from Melee")
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
			searchShelvesForUrn,
			inspectMurals,
			unsortedStep16,
			unsortedStep18
		), List.of(
			dwellberries3,
			ringOfLife,
			golemCombatGear
			// Requirements
		), List.of(
			// Recommended
			twoFreeInventorySlots,
			staminaPotion,
			prayerPotion,
			food
		)));
		panels.add(new PanelDetails("Fort Invasion", List.of(
			unsortedStep20,
			rubbleMiner1,
			rubbleMiner2,
			rubbleMiner3,
			rubbleMiner4,
			unsortedStep30,
			unsortedStep32,
			unsortedStep34
			// TODO
		), List.of(
			// Requirements
			anyPickaxe
		), List.of(
			// Recommended
			staminaPotion
		)));
		panels.add(new PanelDetails("Hearty Heist", List.of(
			// Steps
			returnToEliasWithBaseItems,
			headToZemouregalsBaseAndTalkToElias,
			enterZemouregalsBase,
			getToBackOfZemouregalsBase,
			enterStorageRoom,
			searchTableForDecoderStrips,
			openChestForCodeKey,
			metalDoorSolver,
			openMetalDoors,
			grappleAcross,
			enterBossRoom,
			fightArrav,
			finishQuest
			// TODO
		), List.of(
			// Requirements
			basePlans,
			baseKey,
			anyGrappleableCrossbow,
			mithrilGrapple,
			arravCombatGear,
			insulatedBoots,
			canopicJarFull
		), List.of(
			// Recommended
			new FreeInventorySlotRequirement(1),
			staminaPotion,
			prayerPotion,
			food
		)));

		return panels;
	}
}
