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
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.questhelper.requirements.util.LogicHelper.*;

/**
 * The quest guide for the "The Curse of Arrav" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/The_Curse_of_Arrav">The OSRS wiki guide</a> and <a href="https://www.youtube.com/watch?v=IIMTpgD0BUY">Slayermusiq1's Quest Guide</a> was referenced for this guide
 */
@SuppressWarnings("FieldCanBeLocal")
public class TheCurseOfArrav extends BasicQuestHelper
{
	static final @Varbit int VARBIT_SOUTH_LEVER_STATE = 11482;
	static final @Varbit int VARBIT_NORTH_LEVER_STATE = 11481;

	// Required items
	private ItemRequirement dwellberries3;
	private ItemRequirement ringOfLife;
	public ItemRequirement anyPickaxe;
	private ItemRequirement anyGrappleableCrossbow;
	private ItemRequirement mithrilGrapple;
	private ItemRequirement insulatedBoots;

	// Recommended items
	private TeleportItemRequirement fairyRingDLQ;
	private TeleportItemRequirement trollheimTeleport;
	private TeleportItemRequirement lumberyardTeleport;
	private ItemRequirement golemCombatGear;
	private ItemRequirement arravCombatGear;
	private ItemRequirement food;
	private ItemRequirement staminaPotion;
	private ItemRequirement prayerPotion;
	private ItemRequirement antiVenom;
	private FreeInventorySlotRequirement twoFreeInventorySlots;

	// Mid-quest item requirements
	private ItemRequirement firstMastabaKey;
	private ItemRequirement secondMastabaKey;
	private ItemRequirement canopicJarFullForHeist;
	private ItemRequirement basePlans;
	private ItemRequirement baseKey;

	// Zones & their requirements
	/// Top floor of the tomb (Uzer Mastaba)
	private ZoneRequirement insideTomb;
	/// Top floor of the tomb (Uzer Mastaba) by the south lever
	ZoneRequirement bySouthLever;
	/// Top floor of the tomb (Uzer Mastaba) by the north lever
	ZoneRequirement byNorthLever;
	/// Top floor of the tomb (Uzer Mastaba) in the room with the Golem guard
	ZoneRequirement insideGolemArena;
	/// Second floor of the tomb (Uzer Mastaba)
	private ZoneRequirement insideTombSecondFloor;
	ZoneRequirement inTrollheimCave;
	ZoneRequirement onTrollweissMountain;
	ZoneRequirement inTrollweissCave;
	ZoneRequirement inArravHouseFirstRoom;
	ZoneRequirement inArravHouseSecondRoom;
	private ZoneRequirement inZemouregalsBaseSection1;
	private ZoneRequirement inZemouregalsBaseSection2;
	private ZoneRequirement inZemouregalsBaseSection3;
	private ZoneRequirement inZemouregalsBaseSection4;
	private ZoneRequirement inZemouregalsBaseKitchen;
	private ZoneRequirement inZemouregalsBaseSewer;
	/// After exiting the sewer of Zemouregal's base
	ZoneRequirement inZemouregalsBaseSecondPart;
	/// In the storage room after exiting the sewer of Zemouregal's base
	ZoneRequirement inStorageRoom;
	/// Past the Metal door in Zemouregal's base
	ZoneRequirement inGrapplePuzzleRoom;
	/// Past the Grapple puzzle in Zemouregal's base
	ZoneRequirement pastGrapplePuzzleRoom;

	// Steps
	/// 0 + 2
	NpcStep startQuest;

	/// 4
	ObjectStep enterTomb;

	/// 6 + 8
	ConditionalStep unlockImposingDoors;
	ObjectStep getFirstKey;
	ObjectStep getSecondKey;
	ObjectStep pullSouthLever;
	ObjectStep pullNorthLever;

	/// 10
	ConditionalStep fightGolemCond;
	ObjectStep enterGolemArena;
	NpcStep fightGolemGuard;

	/// 12 + 14
	private ConditionalStep finishTilePuzzleAndGetCanopicJar;
	private ObjectStep enterTombBasement;
	private PuzzleWrapperStep solveTilePuzzle;
	private ObjectStep searchShelvesForUrn;
	private ObjectStep inspectMurals;
	private ConditionalStep fillCanopicJar;
	private ConditionalStep showCanopicJarToElias;
	private ConditionalStep goThroughTrollweissCave1;
	private ConditionalStep goThroughTrollweissCave2;
	private ConditionalStep goThroughTrollweissCave3;
	private ConditionalStep goThroughTrollweissCave4;
	private ConditionalStep climbUpToZemouregalsFort;
	private ConditionalStep confrontArravInZemouregalsFort;
	private ConditionalStep stealBaseKeyForHeist;
	private ConditionalStep unlockDoorInZemouregalsBase;
	private ConditionalStep getToBackroomsOfZemouregalsBase;
	private ConditionalStep unlockMetalDoor;
	private ConditionalStep attemptToStealHeart;
	private ConditionalStep actuallyConfrontArrav;
	private ConditionalStep watchYourVictoryDialog;
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
	ObjectStep getToSouthLever;
	ObjectStep leaveSouthLever;
	ObjectStep getToNorthLever;
	ObjectStep leaveNorthLever;
	ItemStep combineJarWithDwellberries;
	ItemStep combineJarWithRingOfLife;
	NpcStep returnToElias;
	ObjectStep headToTrollheim;
	ObjectStep continueThroughTrollheimCave;
	ObjectStep enterTrollweissCave;
	ItemRequirement canopicJarFull;
	ObjectStep climbUpstairsAndTalkToArrav;
	NpcStep talkToArrav;
	ObjectStep goToNextRoom;
	ObjectStep searchTapestry;
	VarbitRequirement haveUsedKeyOnSouthLever;
	VarbitRequirement haveFlippedSouthLever;
	VarbitRequirement haveUsedKeyOnNorthLever;
	VarbitRequirement haveFlippedNorthLever;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(2, startQuest);
		steps.put(4, enterTomb);
		steps.put(6, unlockImposingDoors);
		steps.put(8, unlockImposingDoors);
		steps.put(10, fightGolemCond);
		steps.put(12, finishTilePuzzleAndGetCanopicJar);
		steps.put(14, finishTilePuzzleAndGetCanopicJar);
		steps.put(16, fillCanopicJar);
		steps.put(18, showCanopicJarToElias);
		steps.put(20, goThroughTrollweissCave1);
		steps.put(22, goThroughTrollweissCave1);
		steps.put(24, goThroughTrollweissCave2);
		steps.put(26, goThroughTrollweissCave3);
		steps.put(28, goThroughTrollweissCave4);
		steps.put(30, climbUpToZemouregalsFort);
		steps.put(32, confrontArravInZemouregalsFort);
		steps.put(34, stealBaseKeyForHeist);
		steps.put(36, returnToEliasWithBaseItems);
		steps.put(38, interpretPlansWithElias);
		steps.put(40, interpretPlansWithElias);
		steps.put(42, headToZemouregalsBaseAndTalkToElias);
		steps.put(44, unlockDoorInZemouregalsBase);
		steps.put(46, getToBackroomsOfZemouregalsBase);
		steps.put(48, unlockMetalDoor);
		steps.put(50, unlockMetalDoor);
		steps.put(52, attemptToStealHeart);
		steps.put(54, actuallyConfrontArrav);
		steps.put(56, watchYourVictoryDialog);
		steps.put(58, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		insideTomb = new ZoneRequirement(new Zone(new WorldPoint(3842, 4547, 0), new WorldPoint(3900, 4603, 0)));
		bySouthLever = new ZoneRequirement(new Zone(new WorldPoint(3893, 4554, 0), new WorldPoint(3894, 4552, 0)));
		byNorthLever = new ZoneRequirement(new Zone(new WorldPoint(3894, 4597, 0), new WorldPoint(3893, 4599, 0)));
		insideGolemArena = new ZoneRequirement(new Zone(new WorldPoint(3856, 4592, 0), new WorldPoint(3884, 4599, 0)));
		insideTombSecondFloor = new ZoneRequirement(
			new Zone(new WorldPoint(3719, 4674, 0), new WorldPoint(3770, 4732, 0)),
			new Zone(new WorldPoint(3845, 4674, 0), new WorldPoint(3900, 4732, 0))
		);

		inTrollheimCave = new ZoneRequirement(new Zone(11167));
		onTrollweissMountain = new ZoneRequirement(new Zone(11068));
		inTrollweissCave = new ZoneRequirement(new Zone(11168));
		inArravHouseFirstRoom = new ZoneRequirement(new Zone(new WorldPoint(2848, 3868, 0), new WorldPoint(2858, 3873, 0)));
		inArravHouseSecondRoom = new ZoneRequirement(new Zone(new WorldPoint(2863, 3865, 0), new WorldPoint(2859, 3873, 0)));

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

		inZemouregalsBaseSecondPart = new ZoneRequirement(new Zone(new WorldPoint(3590, 4538, 0), new WorldPoint(3622, 4597, 0)));
		inStorageRoom = new ZoneRequirement(new Zone(new WorldPoint(3614, 4571, 0), new WorldPoint(3605, 4563, 0)));
		inGrapplePuzzleRoom = new ZoneRequirement(new Zone(new WorldPoint(3613, 4587, 0), new WorldPoint(3625, 4579, 0)));
		pastGrapplePuzzleRoom = new ZoneRequirement(new Zone(new WorldPoint(3621, 4589, 0), new WorldPoint(3645, 4578, 0)));
	}

	@Override
	protected void setupRequirements()
	{
		haveUsedKeyOnSouthLever = new VarbitRequirement(VARBIT_SOUTH_LEVER_STATE, 1, Operation.GREATER_EQUAL);
		haveFlippedSouthLever = new VarbitRequirement(VARBIT_SOUTH_LEVER_STATE, 2);
		haveUsedKeyOnNorthLever = new VarbitRequirement(VARBIT_NORTH_LEVER_STATE, 1, Operation.GREATER_EQUAL);
		haveFlippedNorthLever = new VarbitRequirement(VARBIT_NORTH_LEVER_STATE, 2);
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
		mithrilGrapple = new ItemRequirement("Mith grapple", ItemID.XBOWS_GRAPPLE_TIP_BOLT_MITHRIL_ROPE).isNotConsumed();
		mithrilGrapple.setConditionToHide(haveMetArrav);
		insulatedBoots = new ItemRequirement("Insulated boots", ItemID.SLAYER_BOOTS).isNotConsumed();

		// Recommended items
		fairyRingDLQ = new TeleportItemRequirement("Fairy Ring [DLQ]", ItemCollections.FAIRY_STAFF);
		trollheimTeleport = new TeleportItemRequirement("Trollheim Teleport", ItemID.NZONE_TELETAB_TROLLHEIM);
		trollheimTeleport.addAlternates(ItemCollections.GHOMMALS_HILT);
		lumberyardTeleport = new TeleportItemRequirement("Lumberyard teleport", ItemID.TELEPORTSCROLL_LUMBERYARD);
		staminaPotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS, 1);
		prayerPotion = new ItemRequirement("Prayer potion", ItemCollections.PRAYER_POTIONS, 1);
		antiVenom = new ItemRequirement("Anti-venom", ItemCollections.ANTIVENOMS, 1);
		golemCombatGear = new ItemRequirement("Crush or ranged combat gear to fight the Golem guard", -1, -1);
		golemCombatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		golemCombatGear.setConditionToHide(haveKilledGolem);
		arravCombatGear = new ItemRequirement("Ranged or melee combat gear for killing Arrav", -1, -1);
		arravCombatGear.setTooltip("If you bring Melee gear, it's advisable to bring some ranged weapon swap like darts for killing the Zombies as they spawn");
		arravCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		twoFreeInventorySlots = new FreeInventorySlotRequirement(2);

		// Mid-quest item requirements
		firstMastabaKey = new ItemRequirement("Mastaba Key", ItemID.COA_MASTABA_KEY_1);
		secondMastabaKey = new ItemRequirement("Mastaba Key", ItemID.COA_MASTABA_KEY_2);
		canopicJarFull = new ItemRequirement("Canopic jar (full)", ItemID.COA_CANOPIC_JAR_COMPLETE);
		canopicJarFullForHeist = new ItemRequirement("Canopic jar (full)", ItemID.COA_CANOPIC_JAR_COMPLETE);
		canopicJarFullForHeist.setTooltip("You can get a new one from Elias at the entrance of Zemouregal's base if you've lost it.");
	}

	public void setupSteps()
	{
		var unreachableState = new DetailedQuestStep(this, "This state should not be reachable, please make a report with a screenshot in the Quest Helper discord.");

		startQuest = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Talk to Elias south of Ruins of Uzer to start the quest.");
		startQuest.addTeleport(fairyRingDLQ);
		startQuest.addDialogStep("Yes.");

		enterTomb = new ObjectStep(this, ObjectID.COA_MASTABA_ENTRY, new WorldPoint(3486, 3023, 0), "Enter the tomb south-west of Elias.", dwellberries3, ringOfLife, twoFreeInventorySlots, golemCombatGear);

		getFirstKey = new ObjectStep(this, ObjectID.MOM2_SKELETON_LOOTER_03, new WorldPoint(3875, 4554, 0), "Get the first Mastaba key from the skeleton in the cave south of the entrance.");
		getSecondKey = new ObjectStep(this, ObjectID.MOM2_SKELETON_LOOTER_06, new WorldPoint(3880, 4585, 0), "Get the second Mastaba key from the skeleton east of the entrance.");
		getToSouthLever = new ObjectStep(this, ObjectID.COA_MASTABA_SPEAR_TRAP_1, new WorldPoint(3891, 4554, 0), "Search the Odd markings to the south to get to the south lever. Search the markings again if you fail.");
		pullSouthLever = new ObjectStep(this, ObjectID.COA_MASTABA_LEVER_OFF, new WorldPoint(3894, 4553, 0), "Pull the lever to the south-east.",
				secondMastabaKey.hideConditioned(haveUsedKeyOnSouthLever));
		pullSouthLever.addDialogStep("Yes.");
		leaveSouthLever = new ObjectStep(this, ObjectID.COA_MASTABA_SPEAR_TRAP_2, new WorldPoint(3892, 4554, 0), "Search the Odd markings next to you to get out.");
		pullSouthLever.addSubSteps(getToSouthLever, leaveSouthLever);

		getToNorthLever = new ObjectStep(this, ObjectID.COA_MASTABA_SPEAR_TRAP_2, new WorldPoint(3891, 4597, 0), "Search the Odd markings to the north to get to the north lever. Search the markings again if you fail.");
		pullNorthLever = new ObjectStep(this, ObjectID.COA_MASTABA_LEVER_OFF, new WorldPoint(3894, 4598, 0), "Pull the lever to the north-east.", firstMastabaKey.hideConditioned(haveUsedKeyOnNorthLever));
		pullNorthLever.addDialogStep("Yes.");
		leaveNorthLever = new ObjectStep(this, ObjectID.COA_MASTABA_SPEAR_TRAP_1, new WorldPoint(3892, 4597, 0), "Search the Odd markings next to you to get out.");
		pullNorthLever.addSubSteps(getToNorthLever, leaveNorthLever);

		var haveOrUsedFirstKey = or(firstMastabaKey, haveUsedKeyOnNorthLever);
		var haveOrUsedSecondKey = or(secondMastabaKey, haveUsedKeyOnSouthLever);
		var haveOrUsedBothKeys = and(haveOrUsedFirstKey, haveOrUsedSecondKey);

		var stepsNearNorthLever = new ConditionalStep(this, leaveNorthLever);
		stepsNearNorthLever.addStep(and(haveOrUsedFirstKey, not(haveFlippedNorthLever)), pullNorthLever);

		var stepsNearSouthLever = new ConditionalStep(this, leaveSouthLever);
		stepsNearSouthLever.addStep(and(haveOrUsedSecondKey, not(haveFlippedSouthLever)), pullSouthLever);

		unlockImposingDoors = new ConditionalStep(this, enterTomb);
		unlockImposingDoors.addStep(not(insideTomb), enterTomb);
		unlockImposingDoors.addStep(bySouthLever, stepsNearSouthLever);
		unlockImposingDoors.addStep(byNorthLever, stepsNearNorthLever);
		unlockImposingDoors.addStep(and(haveOrUsedBothKeys, not(haveFlippedSouthLever)), getToSouthLever);
		unlockImposingDoors.addStep(and(haveOrUsedBothKeys, not(haveFlippedNorthLever)), getToNorthLever);
		unlockImposingDoors.addStep(not(haveOrUsedFirstKey), getFirstKey);
		unlockImposingDoors.addStep(not(haveOrUsedSecondKey), getSecondKey);

		// Once the north lever is pulled, quest varbit changed from 6 to 8, then 8 to 10 at the same tick
		// This might have to do with which order you pulled the levers in

		enterGolemArena = new ObjectStep(this, ObjectID.COA_MASTABA_SLIDE_DOOR, new WorldPoint(3885, 4597, 0), "Open the imposing doors, ready to fight the Golem guard.");
		fightGolemGuard = new NpcStep(this, NpcID.COA_MASTABA_GOLEM, new WorldPoint(3860, 4595, 0), "Fight the Golem guard. It is weak to crush style weapons. Use Protect from Melee to avoid damage from his attacks. When the screen shakes, step away from him to avoid taking damage.");
		fightGolemCond = new ConditionalStep(this, enterGolemArena);
		// Get inside the tomb if you're not already inside. In case the user has teleported out or died to golem?
		fightGolemCond.addStep(not(insideTomb), enterTomb);
		fightGolemCond.addStep(byNorthLever, leaveNorthLever);
		fightGolemCond.addStep(bySouthLever, leaveSouthLever);
		fightGolemCond.addStep(insideGolemArena, fightGolemGuard);

		var enterGolemArenaWithoutFight = new ObjectStep(this, ObjectID.COA_MASTABA_SLIDE_DOOR, new WorldPoint(3885, 4597, 0), "Open the imposing doors to the north-east of the tomb.");
		enterTombBasement = new ObjectStep(this, ObjectID.COA_MASTABA_INNER_STAIRS_DOWN, new WorldPoint(3860, 4596, 0), "Climb the stairs down the tomb basement.");
		enterTombBasement.addSubSteps(enterGolemArenaWithoutFight);

		solveTilePuzzle = new TilePuzzleSolver(this).puzzleWrapStep("Move across the floor tile puzzle.");

		searchShelvesForUrn = new ObjectStep(this, ObjectID.COA_CANOPIC_SHELVES, new WorldPoint(3854, 4722, 0), "Search the shelves to the west for an oil-filled canopic jar.");
		var oilFilledCanopicJar = new ItemRequirement("Oil-filled canopic jar", ItemID.COA_CANOPIC_JAR_OIL);

		inspectMurals = new ObjectStep(this, ObjectID.COA_MURAL_MIDDLE_2, new WorldPoint(3852, 4687, 0), "Inspect the murals in the room to the south.", oilFilledCanopicJar);

		finishTilePuzzleAndGetCanopicJar = new ConditionalStep(this, enterTomb);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTombSecondFloor, finishedTilePuzzle, oilFilledCanopicJar), inspectMurals);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTombSecondFloor, finishedTilePuzzle), searchShelvesForUrn);
		finishTilePuzzleAndGetCanopicJar.addStep(insideTombSecondFloor, solveTilePuzzle);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTomb, insideGolemArena), enterTombBasement);
		finishTilePuzzleAndGetCanopicJar.addStep(and(insideTomb), enterGolemArenaWithoutFight);

		var oilAndBerryFilledCanopicJar = new ItemRequirement("Canopic jar (oil and berries)", ItemID.COA_CANOPIC_JAR_OIL_BERRIES);

		combineJarWithDwellberries = new ItemStep(this, "Put the Dwellberries in the Canopic jar.", oilFilledCanopicJar.highlighted(), dwellberries3.highlighted(), ringOfLife);
		combineJarWithRingOfLife = new ItemStep(this, "Put the Ring of life in the Canopic jar.", oilAndBerryFilledCanopicJar.highlighted(), ringOfLife.highlighted());

		fillCanopicJar = new ConditionalStep(this, unreachableState);
		fillCanopicJar.addStep(oilAndBerryFilledCanopicJar, combineJarWithRingOfLife);
		fillCanopicJar.addStep(oilFilledCanopicJar, combineJarWithDwellberries);
		fillCanopicJar.addStep(and(insideTombSecondFloor, finishedTilePuzzle), searchShelvesForUrn);
		fillCanopicJar.addStep(not(insideTomb), enterTomb);
		fillCanopicJar.addStep(and(insideTomb, insideGolemArena), enterTombBasement);
		fillCanopicJar.addStep(and(insideTomb), enterGolemArenaWithoutFight);


		returnToElias = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.", canopicJarFull);
		returnToElias.addTeleport(fairyRingDLQ);
		var returnToEliasByWalking = new ObjectStep(this, ObjectID.COA_MASTABA_INNER_STAIRS_UP, new WorldPoint(3894, 4714, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalking.addTeleport(fairyRingDLQ);
		var returnToEliasByWalkingMidway = new ObjectStep(this, ObjectID.COA_MASTABA_EXIT, new WorldPoint(3848, 4577, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalkingMidway.addTeleport(fairyRingDLQ);
		var returnToEliasByWalkingMidwayGolem = new ObjectStep(this, ObjectID.COA_MASTABA_SLIDE_DOOR, new WorldPoint(3885, 4597, 0), "Return to Elias south of Ruins of Uzer, either by walking out of the tomb or using the fairy ring.");
		returnToEliasByWalkingMidwayGolem.addTeleport(fairyRingDLQ);

		returnToElias.addSubSteps(returnToEliasByWalking, returnToEliasByWalkingMidway, returnToEliasByWalkingMidwayGolem);

		showCanopicJarToElias = new ConditionalStep(this, returnToElias);
		showCanopicJarToElias.addStep(insideTombSecondFloor, returnToEliasByWalking);
		showCanopicJarToElias.addStep(insideGolemArena, returnToEliasByWalkingMidwayGolem);
		showCanopicJarToElias.addStep(insideTomb, returnToEliasByWalkingMidway);
		// ardy cloak + fairy ring takes 50s, walking takes 1m12s

		headToTrollheim = new ObjectStep(this, ObjectID.TROLLROMANCE_CAVEENTRANCE, new WorldPoint(2821, 3744, 0), "Enter the cave next to Trollheim. You can use a Trollheim teleport tablet or the GWD Ghommal's Hilt teleport to get close.", anyPickaxe);
		headToTrollheim.addTeleport(trollheimTeleport);

		continueThroughTrollheimCave = new ObjectStep(this, ObjectID.TROLLROMANCE_SNOW_CAVEWALL_CREVIS, new WorldPoint(2772, 10233, 0), "Continue through the Trollheim cave, exiting at the Crevasse to the north-west. Use Protect from Melee to avoid taking damage from the Ice Trolls.", anyPickaxe);

		enterTrollweissCave = new ObjectStep(this, ObjectID.MAH3_UNBLOCKEDTUNNEL, new WorldPoint(2809, 3861, 0), "Enter the Trollweiss cave to the east.", anyPickaxe);

		var rubbleMiner1Real = new RubbleSolverOne(this);
		var rubbleMiner2Real = new RubbleSolverTwo(this);
		var rubbleMiner3Real = new RubbleSolverThree(this);
		var rubbleMiner4Real = new RubbleSolverFour(this);

		rubbleMiner1 = rubbleMiner1Real.puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner2 = rubbleMiner2Real.puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner3 = rubbleMiner3Real.puzzleWrapStep("Mine the rubble and make your way through the cave.");
		rubbleMiner4 = rubbleMiner4Real.puzzleWrapStep("Mine the rubble and make your way through the cave.");

		rubbleMiner1Real.addSubSteps(rubbleMiner2, rubbleMiner3, rubbleMiner4);


		goThroughTrollweissCave1 = new ConditionalStep(this, headToTrollheim);
		goThroughTrollweissCave1.addStep(inTrollweissCave, rubbleMiner1);
		goThroughTrollweissCave1.addStep(onTrollweissMountain, enterTrollweissCave);
		goThroughTrollweissCave1.addStep(inTrollheimCave, continueThroughTrollheimCave);

		goThroughTrollweissCave2 = new ConditionalStep(this, headToTrollheim);
		goThroughTrollweissCave2.addStep(inTrollweissCave, rubbleMiner2);
		goThroughTrollweissCave2.addStep(onTrollweissMountain, enterTrollweissCave);
		goThroughTrollweissCave2.addStep(inTrollheimCave, continueThroughTrollheimCave);

		goThroughTrollweissCave3 = new ConditionalStep(this, headToTrollheim);
		goThroughTrollweissCave3.addStep(inTrollweissCave, rubbleMiner3);
		goThroughTrollweissCave3.addStep(onTrollweissMountain, enterTrollweissCave);
		goThroughTrollweissCave3.addStep(inTrollheimCave, continueThroughTrollheimCave);

		goThroughTrollweissCave4 = new ConditionalStep(this, headToTrollheim);
		goThroughTrollweissCave4.addStep(inTrollweissCave, rubbleMiner4);
		goThroughTrollweissCave4.addStep(onTrollweissMountain, enterTrollweissCave);
		goThroughTrollweissCave4.addStep(inTrollheimCave, continueThroughTrollheimCave);

		climbUpstairsAndTalkToArrav = new ObjectStep(this, ObjectID.MAH3_CELLAR_STAIRS, new WorldPoint(2811, 10267, 0), "Climb up the stairs in the room with the red tile floor and talk to Arrav.");
		climbUpToZemouregalsFort = new ConditionalStep(this, headToTrollheim);
		climbUpToZemouregalsFort.addStep(inTrollweissCave, climbUpstairsAndTalkToArrav);
		climbUpToZemouregalsFort.addStep(onTrollweissMountain, enterTrollweissCave);
		climbUpToZemouregalsFort.addStep(inTrollheimCave, continueThroughTrollheimCave);

		talkToArrav = new NpcStep(this, NpcID.COA_ARRAV_VIS, new WorldPoint(2856, 3871, 0), "Talk to Arrav.");

		confrontArravInZemouregalsFort = new ConditionalStep(this, headToTrollheim);
		confrontArravInZemouregalsFort.addStep(inArravHouseFirstRoom, talkToArrav);
		confrontArravInZemouregalsFort.addStep(inTrollweissCave, climbUpstairsAndTalkToArrav);
		confrontArravInZemouregalsFort.addStep(onTrollweissMountain, enterTrollweissCave);
		confrontArravInZemouregalsFort.addStep(inTrollheimCave, continueThroughTrollheimCave);

		goToNextRoom = new ObjectStep(this, ObjectID.MAH3_DOOR_TO_TAPESTRY, new WorldPoint(2859, 3870, 0), "Enter the room to your east and search the tapestry for something to help you with your heist.");
		searchTapestry = new ObjectStep(this, ObjectID.MAH3_FORT_TAPESTRY1, new WorldPoint(2861, 3865, 0), "Search the tapestry in the south of the room.");
		stealBaseKeyForHeist = new ConditionalStep(this, headToTrollheim);
		stealBaseKeyForHeist.addStep(inArravHouseSecondRoom, searchTapestry);
		stealBaseKeyForHeist.addStep(inArravHouseFirstRoom, goToNextRoom);
		stealBaseKeyForHeist.addStep(inTrollweissCave, climbUpstairsAndTalkToArrav);
		stealBaseKeyForHeist.addStep(onTrollweissMountain, enterTrollweissCave);
		stealBaseKeyForHeist.addStep(inTrollheimCave, continueThroughTrollheimCave);

		var tapestryFindText = "Can be acquired by heading back to Zemouregal's Fort past the Trollweiss mining dungeon and searching the tapestry.";
		basePlans = new ItemRequirement("Base plans", ItemID.COA_BASE_PLANS);
		basePlans.setConditionToHide(haveUsedPlans);
		basePlans.setTooltip(tapestryFindText); // only needed until varbit 38
		baseKey = new ItemRequirement("Base key", ItemID.COA_BASE_KEY);
		baseKey.setConditionToHide(haveUsedKey);
		baseKey.setTooltip(tapestryFindText); // no longer needed when varbit hits 46

		returnToEliasWithBaseItems = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Return to Elias south of Ruins of Uzer and ask him for help interpreting the plans.", basePlans, baseKey);
		returnToEliasWithBaseItems.addTeleport(fairyRingDLQ);

		interpretPlansWithElias = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Return to Elias south of Ruins of Uzer and ask him for help interpreting the plans.", baseKey);
		interpretPlansWithElias.addTeleport(fairyRingDLQ);

		returnToEliasWithBaseItems.addSubSteps(interpretPlansWithElias);

		// 40 -> 42
		// 9658: 5 -> 6

		headToZemouregalsBaseAndTalkToElias = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3341, 3516, 0), "Head to Zemouregal's base east of Varrock's sawmill and talk to Elias.", anyGrappleableCrossbow, mithrilGrapple, arravCombatGear, insulatedBoots, canopicJarFullForHeist, baseKey);
		headToZemouregalsBaseAndTalkToElias.addDialogStep("Ready when you are.");
		headToZemouregalsBaseAndTalkToElias.addTeleport(lumberyardTeleport);
		enterZemouregalsBase = new ObjectStep(this, ObjectID.DOV_BASE_ENTRY, new WorldPoint(3343, 3515, 0), "Enter Zemouregal's base east of Varrock's sawmill.", anyGrappleableCrossbow, mithrilGrapple, arravCombatGear, insulatedBoots, canopicJarFullForHeist, baseKey);
		enterZemouregalsBase.addTeleport(lumberyardTeleport);


		getToBackOfZemouregalsBase = new DetailedQuestStep(this, "Make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.");
		var passZemouregalsBaseDoor1 = new ObjectStep(this, ObjectID.DOV_BASE_GATE_CLOSED_1, new WorldPoint(3536, 4571, 0), "Open the gate and make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.", baseKey, canopicJarFullForHeist, insulatedBoots);
		var passZemouregalsBaseDoor2 = new ObjectStep(this, ObjectID.DOV_BASE_GATE_CLOSED_2, new WorldPoint(3540, 4597, 0), "Open the gate and make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.", baseKey, canopicJarFullForHeist, insulatedBoots);
		var passZemouregalsBaseDoor3 = new ObjectStep(this, ObjectID.DOV_BASE_DOOR_CLOSED, new WorldPoint(3576, 4604, 0), "Open the door and make your way to the back of Zemouregal's base. Protect from Melee against the zombies to avoid most damage.", baseKey, canopicJarFullForHeist, insulatedBoots);
		var passZemouregalsBaseDoor4 = new ObjectStep(this, ObjectID.MAH3_BASE_GATE_CLOSED2, new WorldPoint(3605, 4603, 0), "Open the gate and make your way to the back of Zemouregal's base.", canopicJarFullForHeist, insulatedBoots);

		unlockDoorInZemouregalsBase = new ConditionalStep(this, enterZemouregalsBase);
		unlockDoorInZemouregalsBase.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unlockDoorInZemouregalsBase.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unlockDoorInZemouregalsBase.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		var enterZemouregalsBaseSewer = new ObjectStep(this, ObjectID.MAH3_SEWER_WALL_ENTRANCE, new WorldPoint(3609, 4598, 0), "Enter the sewers and make your way to the back of Zemouregal's base.", canopicJarFullForHeist, insulatedBoots.highlighted().equipped());

		var exitZemouregalsBaseSewer = new ObjectStep(this, ObjectID.MAH3_SEWER_WALL_EXIT2, new WorldPoint(3741, 4573, 0), "Head south to exit the sewers and make your way to the back of Zemouregal's base.", canopicJarFullForHeist, insulatedBoots.highlighted().equipped());

		getToBackOfZemouregalsBase.addSubSteps(passZemouregalsBaseDoor1, passZemouregalsBaseDoor2, passZemouregalsBaseDoor3, passZemouregalsBaseDoor4, enterZemouregalsBaseSewer, exitZemouregalsBaseSewer);

		// 44 -> 46 when opening door, consuming the base key

		getToBackroomsOfZemouregalsBase = new ConditionalStep(this, enterZemouregalsBase);
		getToBackroomsOfZemouregalsBase.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		getToBackroomsOfZemouregalsBase.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		getToBackroomsOfZemouregalsBase.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		getToBackroomsOfZemouregalsBase.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		getToBackroomsOfZemouregalsBase.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		getToBackroomsOfZemouregalsBase.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		searchTableForDecoderStrips = new ObjectStep(this, ObjectID.MAH3_BASE_TABLE_CHARTS2, "Search the table for some decoder strips.");
		enterStorageRoom = new ObjectStep(this, ObjectID.MAH3_BASE_GATE_CLOSED2, new WorldPoint(3609, 4572, 0), "Enter the storage room to the south-east.");
		var exitStorageRoom = new ObjectStep(this, ObjectID.MAH3_BASE_GATE_CLOSED2, new WorldPoint(3609, 4572, 0), "Exit the storage room.");
		var codeKey = new ItemRequirement("Code key", ItemID.COA_CODE_KEY);

		openChestForCodeKey = new ObjectStep(this, ObjectID.MAH3_BASE_CHEST_CLOSED, new WorldPoint(3609, 4565, 0), "Open the chest for the code key.");

		var metalDoorSolverReal = new MetalDoorSolver(this);
		metalDoorSolverReal.addSubSteps(exitStorageRoom);

		this.metalDoorSolver = metalDoorSolverReal.puzzleWrapStep();


		// also used for 50
		unlockMetalDoor = new ConditionalStep(this, enterZemouregalsBase);
		unlockMetalDoor.addStep(and(inZemouregalsBaseSecondPart, not(inStorageRoom), codeKey), metalDoorSolver);
		unlockMetalDoor.addStep(and(inStorageRoom, codeKey), exitStorageRoom);
		unlockMetalDoor.addStep(and(inStorageRoom), openChestForCodeKey);
		unlockMetalDoor.addStep(and(inZemouregalsBaseSecondPart, not(codeKey)), enterStorageRoom);
		unlockMetalDoor.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		unlockMetalDoor.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		unlockMetalDoor.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		unlockMetalDoor.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		unlockMetalDoor.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		unlockMetalDoor.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		openMetalDoors = new ObjectStep(this, ObjectID.MAH3_BASE_DOOR, new WorldPoint(3612, 4582, 0), "Step through through the metal doors.", canopicJarFullForHeist, anyGrappleableCrossbow, mithrilGrapple);

		grappleAcross = new ObjectStep(this, ObjectID.MAH3_BASE_PIPE_JUNCTION, new WorldPoint(3615, 4582, 0), "Grapple across the pipe", canopicJarFullForHeist, anyGrappleableCrossbow.highlighted().equipped(), mithrilGrapple.highlighted().equipped());


		enterBossRoom = new ObjectStep(this, ObjectID.MAH3_BASE_PEDESTAL_HEART, new WorldPoint(3638, 4582, 0), "Attempt to take Arrav's heart from the pedestal, ready for a fight with Arrav. Kill zombies as they appear (ranged weapons are handy here). Avoid the venom pools they spawn. Use Protect from Melee to avoid some of the incoming damage. When Arrav throws an axe towards you, step to the side or behind him.", canopicJarFullForHeist, arravCombatGear);
		enterBossRoom.setOverlayText("Attempt to take Arrav's heart from the pedestal, ready for a fight with Arrav. Some hints are available in the sidebar.");

		attemptToStealHeart = new ConditionalStep(this, enterZemouregalsBase);
		attemptToStealHeart.addStep(pastGrapplePuzzleRoom, enterBossRoom);
		attemptToStealHeart.addStep(inGrapplePuzzleRoom, grappleAcross);
		attemptToStealHeart.addStep(inZemouregalsBaseSecondPart, openMetalDoors);
		attemptToStealHeart.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		attemptToStealHeart.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		attemptToStealHeart.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		attemptToStealHeart.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		attemptToStealHeart.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		attemptToStealHeart.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		// User has engaged Arrav

		fightArrav = new NpcStep(this, NpcID.COA_ARRAV_COMBAT, new WorldPoint(3635, 4582, 0), "Fight Arrav. Kill zombies as they appear (ranged weapons are handy here). Avoid the venom pools they spawn. Use Protect from Melee to avoid some of the incoming damage. When Arrav throws an axe towards you, step to the side or behind him.", canopicJarFullForHeist);
		fightArrav.setOverlayText("Fight Arrav. Some hints are available in the sidebar.");
		actuallyConfrontArrav = new ConditionalStep(this, enterZemouregalsBase);
		actuallyConfrontArrav.addStep(or(pastGrapplePuzzleRoom, inGrapplePuzzleRoom), fightArrav);
		actuallyConfrontArrav.addStep(inZemouregalsBaseSecondPart, openMetalDoors);
		actuallyConfrontArrav.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		actuallyConfrontArrav.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		actuallyConfrontArrav.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		actuallyConfrontArrav.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		actuallyConfrontArrav.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		actuallyConfrontArrav.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);


		// 54 -> 56 when beating arrav

		var watchTheDialog = new DetailedQuestStep(this, "Watch the dialog.");
		fightArrav.addSubSteps(watchTheDialog);
		watchYourVictoryDialog = new ConditionalStep(this, enterZemouregalsBase);
		watchYourVictoryDialog.addStep(or(pastGrapplePuzzleRoom, inGrapplePuzzleRoom), watchTheDialog);
		watchYourVictoryDialog.addStep(inZemouregalsBaseSecondPart, openMetalDoors);
		watchYourVictoryDialog.addStep(inZemouregalsBaseSewer, exitZemouregalsBaseSewer);
		watchYourVictoryDialog.addStep(inZemouregalsBaseKitchen, enterZemouregalsBaseSewer);
		watchYourVictoryDialog.addStep(inZemouregalsBaseSection4, passZemouregalsBaseDoor4);
		watchYourVictoryDialog.addStep(inZemouregalsBaseSection3, passZemouregalsBaseDoor3);
		watchYourVictoryDialog.addStep(inZemouregalsBaseSection2, passZemouregalsBaseDoor2);
		watchYourVictoryDialog.addStep(inZemouregalsBaseSection1, passZemouregalsBaseDoor1);

		finishQuest = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Talk to Elias to finish the quest.");
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
			antiVenom,
			golemCombatGear,
			arravCombatGear,
			food,
			fairyRingDLQ,
			trollheimTeleport,
			lumberyardTeleport
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
			combineJarWithDwellberries,
			combineJarWithRingOfLife,
			returnToElias
		), List.of(
			dwellberries3,
			ringOfLife,
			golemCombatGear
			// Requirements
		), List.of(
			// Recommended
			twoFreeInventorySlots,
			fairyRingDLQ,
			staminaPotion,
			prayerPotion,
			food
		)));
		panels.add(new PanelDetails("Fort Invasion", List.of(
			headToTrollheim,
			continueThroughTrollheimCave,
			enterTrollweissCave,
			rubbleMiner1,
			climbUpstairsAndTalkToArrav,
			talkToArrav,
			goToNextRoom,
			searchTapestry,
			returnToEliasWithBaseItems
		), List.of(
			// Requirements
			anyPickaxe
		), List.of(
			// Recommended
			fairyRingDLQ,
			trollheimTeleport,
			staminaPotion
		)));
		panels.add(new PanelDetails("Hearty Heist", List.of(
			// Steps
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
		), List.of(
			// Requirements
			basePlans,
			baseKey,
			anyGrappleableCrossbow,
			mithrilGrapple,
			arravCombatGear,
			insulatedBoots,
			canopicJarFullForHeist
		), List.of(
			// Recommended
			lumberyardTeleport,
			staminaPotion,
			prayerPotion,
			antiVenom,
			food
		)));

		return panels;
	}
}
