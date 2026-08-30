// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.helpers.quests.thebloodmoonrises.CastleDrakanRoomNetwork.RoomKey;
import static com.questhelper.helpers.quests.thebloodmoonrises.CastleDrakanRoomNetwork.door;
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

/**
 * The Vampyrium Castle Drakan section: its requirements, its room graph and its steps.
 */
class CastleDrakan
{
	/// Trap tiles are a property of the corridor, so connect() marks them on both directions.
	private static final WorldPoint[] WEST_DINING_HALLWAY_TRAPS = {
		new WorldPoint(2335, 7396, 0), new WorldPoint(2334, 7396, 0),
		new WorldPoint(2335, 7397, 0), new WorldPoint(2334, 7397, 0),
		new WorldPoint(2328, 7394, 0), new WorldPoint(2327, 7394, 0),
		new WorldPoint(2328, 7395, 0), new WorldPoint(2327, 7395, 0),
	};
	private static final WorldPoint[] EXPLOSIVE_HALLWAY_TRAPS = {
		new WorldPoint(2471, 7397, 0), new WorldPoint(2472, 7397, 0),
		new WorldPoint(2471, 7396, 0), new WorldPoint(2472, 7396, 0),
	};
	private static final WorldPoint[] VANESCULAS_HALLWAY_TRAPS = {
		new WorldPoint(2457, 7409, 0), new WorldPoint(2457, 7410, 0),
		new WorldPoint(2456, 7409, 0), new WorldPoint(2456, 7410, 0),
		new WorldPoint(2461, 7408, 0), new WorldPoint(2461, 7409, 0),
		new WorldPoint(2460, 7408, 0), new WorldPoint(2460, 7409, 0),
	};
	private static final WorldPoint[] NORTH_CHAPEL_HALLWAY_TRAPS = {
		new WorldPoint(2378, 7411, 0), new WorldPoint(2379, 7411, 0),
		new WorldPoint(2379, 7412, 0), new WorldPoint(2378, 7412, 0),
	};
	private static final WorldPoint[] UPPER_SOUTHERN_HALLWAY_TRAPS = {
		new WorldPoint(2438, 7363, 0), new WorldPoint(2438, 7364, 0),
		new WorldPoint(2439, 7363, 0), new WorldPoint(2439, 7364, 0),
	};
	private static final WorldPoint[] UNNAMED_HALLWAY_TRAPS = {
		new WorldPoint(2478, 7395, 0), new WorldPoint(2479, 7395, 0),
		new WorldPoint(2478, 7394, 0), new WorldPoint(2479, 7394, 0),
	};
	private final TheBloodMoonRises quest;

	ItemRequirement tinderbox;
	ItemRequirement halfMoonKey;
	ItemRequirement smallClockHand;
	ItemRequirement largeClockHand;
	ItemRequirement drakanEmblem1;
	ItemRequirement anyOneEmblem;
	ItemRequirement anyOneEmblemHighlighted;
	ItemRequirement explosiveBarrel;
	ZoneRequirement playerAtWesternClock;
	ZoneRequirement playerAtEasternClock;
	ZoneRequirement playerNextToDoorPuzzle;
	Conditions inLobbyF0;
	Conditions inDiningRoomF0;
	Conditions inThroneRoomF0;
	VarbitRequirement inRoomSouthOfThroneRoom;
	VarbitRequirement inStorageRoom;
	VarbitRequirement inStudy;
	VarbitRequirement inHallwayWestOfDiningRoom;
	VarbitRequirement inEmblemGallery;
	VarbitRequirement inWestChapelHallway;
	VarbitRequirement inNorthChapelHallway;
	Conditions inDiningRoomF1;
	Conditions inThroneRoomF1;
	VarbitRequirement inVanesculasStudy;
	VarbitRequirement inVanesculasChamber;
	VarbitRequirement inDrakanEmblemRoomSouthOfExplosiveRoom;
	VarbitRequirement inExplosiveRoom;
	VarbitRequirement inHallwayEastOfExplosiveRoom;
	Conditions inLobbyF1;
	VarbitRequirement inHallwayNorthOfLobby;
	VarbitRequirement inVanesculasHallway;
	VarbitRequirement inRanisHallway;
	VarbitRequirement inRanisParlour;
	VarbitRequirement inVenatorRoom;
	VarbitRequirement inKitchen;
	VarbitRequirement inLarder;
	VarbitRequirement inEmblemGalleryHallway;
	VarbitRequirement inLobbyBasementHallway;
	VarbitRequirement inLobbyBasementVenator;
	VarbitRequirement inBasementPrison;
	VarbitRequirement inHallway5;
	VarbitRequirement inBedroomAboveThroneRoom;
	VarbitRequirement inFirstFloorEastStaircase;
	VarbitRequirement inGroundFloorEastStaircase;
	VarbitRequirement inThroneRoomStorageRoom;
	VarbitRequirement inCrescentDoorRoom;
	VarbitRequirement inSolidDoorStoreRoom;
	VarbitRequirement inBasementStoreRoom;
	VarbitRequirement inOrnateKnifeRoom;
	VarbitRequirement inGuestChamberStoreroom;
	VarbitRequirement inRoomOutsideGuestChamberStoreroom;
	VarbitRequirement inVenatorPuzzleRoom;
	VarbitRequirement inVenatorPuzzleRoomLibrary;
	VarbitRequirement inSmallHallway;
	VarbitRequirement inRoomWithIvanAndVenator;
	VarbitRequirement inSecretRoom;
	VarbitRequirement inBottleRoom;
	VarbitRequirement inChapelLibrary;
	VarbitRequirement inServantsQuarters;
	VarbitRequirement inRoomAboveStudy;
	VarbitRequirement inSolidKeyRoom;
	VarbitRequirement inSolidDoorHallway;
	VarbitRequirement inLaboratory;
	VarbitRequirement inLaboratoryStorageRoom;
	VarbitRequirement needToStartThroneRoomPuzzle;
	VarbitRequirement needToPullBusts;
	VarbitRequirement needToGetKey;
	VarbitRequirement doneWithThroneRoomPuzzle;
	VarbitRequirement needToPullBust3;
	VarbitRequirement needToPullBust4;
	VarbitRequirement needToPullBust1;
	VarbitRequirement placedEmblemInVanesculasHallway;
	VarbitRequirement placedEmblemInVanesculasStudy;
	VarbitRequirement vanesculasChamberWallDestroyed;
	VarbitRequirement smallClockHandNeedsReplacing;
	VarbitRequirement largeClockHandNeedsReplacing;
	Conditions westernClockNeedsFixing;
	WidgetPresenceRequirement clockWidgetOpen;
	Conditions easternClockNeedsFixing;
	VarplayerRequirement hasGottenDrakanEmblemFromFireplace;
	Conditions needToFinishClockPuzzle;
	ItemRequirement ornateSkull;
	Requirement gotOrnateSkull;
	WidgetTextRequirement inArrowPuzzle;
	VarbitRequirement anyEmblemInVanesculasHallwayEast;
	VarbitRequirement emblemInVenatorRoomHolder;
	VarbitRequirement anyEmblemInRanisHallwayNorth;
	VarbitRequirement anyEmblemInNorthChapelHallway;
	VarbitRequirement anyEmblemInWestChapelHallway;
	VarbitRequirement cmkHasSpokenWithVeliaf;
	VarbitRequirement cmkSolvedChestPuzzle;
	ItemRequirement crescentMoonKey;
	ItemRequirement newMoonKey;
	ItemRequirement syringeBarrel;
	ItemRequirement venatorStomach;
	ItemRequirement sinkPlug;
	ItemRequirement brokenPipe;
	ItemRequirement sharpKitchenKnife;
	ItemRequirement syringePlunger;
	ItemRequirement tongs;
	ItemRequirement syringeNeedle;
	ItemRequirement emptySyringe;
	ItemRequirement fullSyringe;
	ItemRequirement leftHalfOfCrest;
	ItemRequirement rightHalfOfCrest;
	ItemRequirement fullCrest;
	VarbitRequirement larderSinkNeedsPlugging;
	VarbitRequirement larderSinkPlugged;
	VarbitRequirement larderSinkCollapsed;
	VarbitRequirement hasCutVenatorStomachUp;
	VarbitRequirement unlockedKitchenChest;
	ItemRequirement battleAxe;
	VarbitRequirement needToPutAxeOnStatue;
	ItemRequirement mace;
	ItemRequirement mace2;
	ItemRequirement sword;
	VarbitRequirement needToPutMaceOnStatue1;
	VarbitRequirement needToPutMaceOnStatue2;
	VarbitRequirement needToPutSwordOnStatue;
	Conditions doneWithWeaponPuzzle;
	VarbitRequirement fullCrestInStudy;
	ItemRequirement gibbousMoonKey;
	ItemRequirement ornateKnife;
	Requirement gotOrnateKnife;
	VarbitRequirement pulledUpperStoreroomLever;
	VarbitRequirement isFireplaceLit;
	ItemRequirement smallLockbox;
	ItemRequirement fancyGem1;
	ItemRequirement fancyGem2;
	VarbitRequirement venatorHeadOneEyePlaced;
	VarbitRequirement doorPuzzleSolved;
	ItemRequirement mysteriousBook;
	VarbitRequirement venatorHeadBothEyePlaced;
	ItemRequirement ornateHourglass;
	Requirement gotOrnateHourglass;
	Requirement holdingADisplayItem;
	VarbitRequirement allItemsPlacedInDisplayCase;
	ItemRequirement gildedKey;
	VarplayerRequirement hasUsedGildedKey;
	VarplayerRequirement fmkIvanFollowingYou;
	WidgetPresenceRequirement gildedBookPuzzleOpen;
	ItemRequirement gildedBook;
	VarbitRequirement startedLibraryPuzzle;
	VarbitRequirement finishedLibraryPuzzle;
	ItemRequirement fullMoonKey;
	ItemRequirement cloudyGreyPotion;
	ItemRequirement weightlessBlackPotion;
	ItemRequirement thickRedPotion;
	ItemRequirement coldBlueishWhitePotion;
	VarbitRequirement solvedSmokeBasin;
	VarbitRequirement solvedShadowBasin;
	VarbitRequirement solvedBloodBasin;
	VarbitRequirement solvedIceBasin;
	Conditions solvedAllBasins;
	ItemRequirement ancientSymbol;
	VarbitRequirement openedPortalFromChapelLibraryToServantsQuarters;
	ItemRequirement ancientShield;
	ItemRequirement shieldWithSymbol;
	VarbitRequirement hasMountedShield;
	ItemRequirement solidKey;
	/// Player has opened the new moon door in the basement, leading to the prison/dungeon where you free Safalaan and Vanescula.
	/// Part of the start of the "Escaping Castle Drakan - Gilded and gibbous keys" section.
	VarplayerRequirement openedDungeonToFreeSafalaanNewMoonDoor;
	/// Player has opened the servants quarter door after going through the portal.
	/// Part of the "Escaping Castle Drakan - Solid key" section.
	VarplayerRequirement openedServantsQuarterNewMoonDoor;
	/// Player has opened up the doors that we deem important for the new moon key.
	Conditions usedUpNewMoonKey;
	Conditions usedUpFullMoonKey;
	VarplayerRequirement usedSolidKey;
	VarplayerRequirement openedGalleryCrescentDoor;
	Conditions usedUpHalfMoonKey;
	Conditions usedUpGibbousMoonKey;
	Conditions openedAHalfMoonDoor;
	VarplayerRequirement openedLobbyHalfMoonDoor;
	VarplayerRequirement openedDiningHalfMoonDoor;
	VarplayerRequirement openedThroneHalfMoonDoor;
	VarplayerRequirement openedKitchenCrescentDoor;
	VarplayerRequirement openedBasementCrescentDoor;
	VarplayerRequirement openedDiningGibbousDoor;
	VarplayerRequirement openedVenatorPuzzleGibbousDoor;
	VarplayerRequirement openedGuestStoreroomGibbousDoor;
	VarplayerRequirement openedExplosiveHallwayGibbousDoor;
	VarplayerRequirement openedCrescentDoorRoomDoor;
	VarplayerRequirement openedBottleRoomFullMoonDoor;
	VarplayerRequirement openedRoomAboveStudyFullMoonDoor;
	VarplayerRequirement openedSolidKeyRoomFullMoonDoor;
	VarplayerRequirement openedStockpileFullMoonDoor;
	VarplayerRequirement openedBedroomFullMoonDoor;
	VarplayerRequirement openedChapelLibraryFullMoonDoor;
	VarplayerRequirement openedNorthChapelFullMoonDoor;
	ItemRequirement vialOfWater;
	ItemRequirement vialsOfWater2;
	ItemRequirement vialOfBlood;
	ItemRequirement vialsOfBlood3;
	ItemRequirement pureEssence;
	ItemRequirement pureEssence3;
	ItemRequirement chemicalVial;
	ItemRequirement chemicalVial2;
	ItemRequirement chemicalVial3;
	ItemRequirement chemicalVial4;
	VarbitRequirement hasPouredAnything;
	VarbitRequirement refinerWater;
	VarbitRequirement refinerBlood;
	VarbitRequirement refinerEssence;
	VarbitRequirement finishedLabPuzzle;
	NpcCondition venatorAlive;
	Conditions anyVenatorAlive;
	NpcCondition fmkVenatorAlive;
	ConditionalStep cGetHalfMoonKey;
	CastleDrakanGoalStep cGetHalfMoonKeyGoal;
	ConditionalStep cGetSmallClockHand;
	CastleDrakanGoalStep cFixClocksGoal;
	CastleDrakanGoalStep cGetCrescentMoonKey;
	CastleDrakanGoalStep cGetNewMoonKey;
	CastleDrakanGoalStep cGetGildedAndGibbousKeys;
	CastleDrakanRoomNetwork castleDrakanRoomNetwork;
	ObjectStep pickUpTinderbox;
	ItemStep pickUpExplosiveBarrel;
	ObjectStep searchCrateForDrakanEmblem1;
	PuzzleWrapperStep hmkPullBustsPW;
	CastleDrakanGoalStep cGetFullMoonKey;
	CastleDrakanGoalStep cGetSolidKey;
	ObjectStep dtsSearchShelvesForSuppliesWater;
	ObjectStep dtsSearchShelvesForSuppliesBlood;
	ObjectStep dtsSearchShelvesForSuppliesEssence;
	CastleDrakanGoalStep cDestroyingTheStockpileGoal;
	PuzzleWrapperStep cmkArrowChestPuzzleStepPW;
	DetailedQuestStep ggkWatchTheCutscene;
	PuzzleWrapperStep ggkpSolveLockboxPuzzlePW;
	PuzzleWrapperStep ggkpSolveDoorPuzzlePW;
	PuzzleWrapperStep fmkGildedBookPuzzlePW;
	PuzzleWrapperStep cDestroyingTheStockpileLaboratoryStepPW;
	DetailedQuestStep dtsWatchTheCutscene;

	CastleDrakan(TheBloodMoonRises quest)
	{
		this.quest = quest;
	}

	void setupRequirements()
	{
		playerAtWesternClock = new ZoneRequirement(new WorldPoint(2344, 7371, 0));
		playerAtEasternClock = new ZoneRequirement(new WorldPoint(2350, 7371, 0));
		playerNextToDoorPuzzle = new ZoneRequirement(new WorldPoint(2519, 7364, 0));

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).canBeObtainedDuringQuest();
		tinderbox.appendToTooltip("You can get another one from the storage room (south-west on floor 1)");

		// Vampyrium Castle Drakan items
		halfMoonKey = new ItemRequirement("Half moon key", ItemID.CASTLE_DRAKAN_HALF_MOON_KEY);
		smallClockHand = new ItemRequirement("Small clock hand", ItemID.CASTLE_DRAKAN_SMALL_CLOCK_HAND);
		largeClockHand = new ItemRequirement("Large clock hand", ItemID.CASTLE_DRAKAN_LARGE_CLOCK_HAND);
		drakanEmblem1 = new ItemRequirement("Drakan emblem", ItemID.CASTLE_DRAKAN_DRAKAN_EMBLEM_1);
		anyOneEmblem = new ItemRequirement("Drakan emblem", ItemID.CASTLE_DRAKAN_DRAKAN_EMBLEM_1);
		anyOneEmblem.addAlternates(ItemID.CASTLE_DRAKAN_DRAKAN_EMBLEM_2, ItemID.CASTLE_DRAKAN_DRAKAN_EMBLEM_3);
		anyOneEmblemHighlighted = anyOneEmblem.highlighted();
		explosiveBarrel = new ItemRequirement("Explosive barrel", ItemID.CASTLE_DRAKAN_POTENT_BARREL).canBeObtainedDuringQuest();
		explosiveBarrel.setTooltip("You can get another one from the room above the storage room (south-west on floor 2)");

		// Vampyrium Castle Drakan room requirements
		var castleDrakanFloor = new VarbitBuilder(VarbitID.CASTLE_DRAKAN_WORLD_MAP_FLOOR);
		var castleDrakanRoom = new VarbitBuilder(VarbitID.CASTLE_DRAKAN_LAST_ROOM);

		var onF0 = castleDrakanFloor.eq(1);
		var onF1 = castleDrakanFloor.eq(2);

		var inLobby = castleDrakanRoom.eq(1);
		var inDiningRoom = castleDrakanRoom.eq(2);
		var inThroneRoom = castleDrakanRoom.eq(3);

		inLobbyF0 = and(onF0, inLobby);
		inDiningRoomF0 = and(onF0, inDiningRoom);
		inThroneRoomF0 = and(onF0, inThroneRoom);
		inRoomSouthOfThroneRoom = castleDrakanRoom.eq(4);
		inStorageRoom = castleDrakanRoom.eq(6);
		inStudy = castleDrakanRoom.eq(7);
		inHallwayWestOfDiningRoom = castleDrakanRoom.eq(8);
		inEmblemGallery = castleDrakanRoom.eq(13);
		inWestChapelHallway = castleDrakanRoom.eq(15);
		inNorthChapelHallway = castleDrakanRoom.eq(17);
		inKitchen = castleDrakanRoom.eq(11);
		inLarder = castleDrakanRoom.eq(10);
		inEmblemGalleryHallway = castleDrakanRoom.eq(12);
		inLobbyBasementHallway = castleDrakanRoom.eq(48);
		inLobbyBasementVenator = castleDrakanRoom.eq(49);
		inBasementPrison = castleDrakanRoom.eq(51);

		inDiningRoomF1 = and(inDiningRoom, onF1);
		inThroneRoomF1 = and(inThroneRoom, onF1);
		inVanesculasStudy = castleDrakanRoom.eq(23);
		inVanesculasChamber = castleDrakanRoom.eq(24);
		inDrakanEmblemRoomSouthOfExplosiveRoom = castleDrakanRoom.eq(25);
		inExplosiveRoom = castleDrakanRoom.eq(28);
		inHallwayEastOfExplosiveRoom = castleDrakanRoom.eq(31);
		inLobbyF1 = and(onF1, inLobby);
		inHallwayNorthOfLobby = castleDrakanRoom.eq(34);
		inVanesculasHallway = castleDrakanRoom.eq(35);
		inRanisHallway = castleDrakanRoom.eq(36);
		inRanisParlour = castleDrakanRoom.eq(26);
		inVenatorRoom = castleDrakanRoom.eq(37);

		inHallway5 = castleDrakanRoom.eq(18);
		inBedroomAboveThroneRoom = castleDrakanRoom.eq(20);
		inFirstFloorEastStaircase = castleDrakanRoom.eq(22);
		inGroundFloorEastStaircase = castleDrakanRoom.eq(5);
		inThroneRoomStorageRoom = castleDrakanRoom.eq(9);
		inCrescentDoorRoom = castleDrakanRoom.eq(27);
		inSolidDoorStoreRoom = castleDrakanRoom.eq(41);
		inBasementStoreRoom = castleDrakanRoom.eq(50);
		inOrnateKnifeRoom = castleDrakanRoom.eq(21);
		inGuestChamberStoreroom = castleDrakanRoom.eq(46);
		inRoomOutsideGuestChamberStoreroom = castleDrakanRoom.eq(38);
		inVenatorPuzzleRoom = castleDrakanRoom.eq(39);
		inVenatorPuzzleRoomLibrary = castleDrakanRoom.eq(44);
		inSmallHallway = castleDrakanRoom.eq(33);
		inRoomWithIvanAndVenator = castleDrakanRoom.eq(29);
		inSecretRoom = castleDrakanRoom.eq(32);
		inBottleRoom = castleDrakanRoom.eq(16);
		inChapelLibrary = castleDrakanRoom.eq(14);
		inServantsQuarters = castleDrakanRoom.eq(30);
		inRoomAboveStudy = castleDrakanRoom.eq(19);
		inSolidKeyRoom = castleDrakanRoom.eq(40);
		inSolidDoorHallway = castleDrakanRoom.eq(45);
		inLaboratory = castleDrakanRoom.eq(42);
		inLaboratoryStorageRoom = castleDrakanRoom.eq(43);

		var throneRoomPuzzleB = new VarbitBuilder(VarbitID.CASTLE_DRAKAN_PUZZLE_THRONE_ROOM);
		needToStartThroneRoomPuzzle = throneRoomPuzzleB.eq(0);
		needToPullBusts = throneRoomPuzzleB.eq(1);
		needToGetKey = throneRoomPuzzleB.eq(2);
		doneWithThroneRoomPuzzle = throneRoomPuzzleB.eq(3);

		needToPullBust3 = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_THRONE_ROOM_LEVER_3, 1);
		needToPullBust4 = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_THRONE_ROOM_LEVER_4, 1);
		needToPullBust1 = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_THRONE_ROOM_LEVER_1, 1);

		placedEmblemInVanesculasHallway = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_VANESCULAS_HALLWAY_TO_VANESCULAS_STUDY, 0, Operation.GREATER);
		placedEmblemInVanesculasStudy = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_VANESCULAS_STUDY_TO_VANESCULAS_CHAMBER, 0, Operation.GREATER);
		vanesculasChamberWallDestroyed = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_WALL_DESTROYED, 1);
		smallClockHandNeedsReplacing = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CLOCK_1_HAND_REPLACED, 0);
		largeClockHandNeedsReplacing = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CLOCK_2_HAND_REPLACED, 0);
		var westernClockLargeHandCorrect = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CLOCK_1_LARGE_HAND_TIME, 11);
		var westernClockSmallHandCorrect = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CLOCK_1_SMALL_HAND_TIME, 9);
		westernClockNeedsFixing = not(and(westernClockLargeHandCorrect, westernClockSmallHandCorrect));
		clockWidgetOpen = new WidgetPresenceRequirement(963, 0);
		var easternClockLargeHandCorrect = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CLOCK_2_LARGE_HAND_TIME, 0);
		var easternClockSmallHandCorrect = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CLOCK_2_SMALL_HAND_TIME, 4);
		easternClockNeedsFixing = not(and(easternClockLargeHandCorrect, easternClockSmallHandCorrect));
		hasGottenDrakanEmblemFromFireplace = new VarplayerRequirement(VarPlayerID.CASTLE_DRAKAN_ROOM_STATUS_1, true, 3);
		needToFinishClockPuzzle = or(westernClockNeedsFixing, easternClockNeedsFixing);
		ornateSkull = new ItemRequirement("Ornate skull", ItemID.CASTLE_DRAKAN_ORNATE_SKULL);
		inArrowPuzzle = new WidgetTextRequirement(810, 15, 9, "Confirm");

		anyEmblemInVanesculasHallwayEast = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_VANESCULAS_HALLWAY_TO_RANIS_HALLWAY, 1, Operation.GREATER_EQUAL);
		/// The holder in the room beyond the cracked wall, which is where the third emblem comes from.
		emblemInVenatorRoomHolder = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_RANIS_HALLWAY_TO_RANIS_CHAMBER, 0, Operation.GREATER);
		anyEmblemInRanisHallwayNorth = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_RANIS_HALLWAY_TO_RANIS_PARLOUR, 1, Operation.GREATER_EQUAL);
		anyEmblemInNorthChapelHallway = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_WEST_CHAPEL_HALLWAY_TO_NORTH_CHAPEL_HALLWAY, 1, Operation.GREATER_EQUAL);
		anyEmblemInWestChapelHallway = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_EMBLEM_GALLERY_TO_WEST_CHAPEL_HALLWAY, 1, Operation.GREATER_EQUAL);
		cmkHasSpokenWithVeliaf = new VarbitRequirement(VarbitID.MYQ6, 74, Operation.GREATER_EQUAL);
		cmkSolvedChestPuzzle = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_GALLERY_CHEST, 1);
		crescentMoonKey = new ItemRequirement("Crescent moon key", ItemID.CASTLE_DRAKAN_CRESCENT_MOON_KEY);
		newMoonKey = new ItemRequirement("New moon key", ItemID.CASTLE_DRAKAN_NEW_MOON_KEY);

		syringeBarrel = new ItemRequirement("Syringe barrel", ItemID.CASTLE_DRAKAN_SYRINGE_BARREL);
		venatorStomach = new ItemRequirement("Venator stomach", ItemID.CASTLE_DRAKAN_VENATOR_STOMACH);
		sinkPlug = new ItemRequirement("Sink plug", ItemID.CASTLE_DRAKAN_SINK_PLUG);
		brokenPipe = new ItemRequirement("Broken pipe", ItemID.CASTLE_DRAKAN_BROKEN_PIPE);
		sharpKitchenKnife = new ItemRequirement("Sharp knife", ItemID.CASTLE_DRAKAN_SHARP_KNIFE);
		syringePlunger = new ItemRequirement("Syringe plunger", ItemID.CASTLE_DRAKAN_SYRINGE_PLUNGER);
		tongs = new ItemRequirement("Tongs", ItemID.CASTLE_DRAKAN_TONGS);
		syringeNeedle = new ItemRequirement("Syringe needle", ItemID.CASTLE_DRAKAN_SYRINGE_NEEDLE);
		emptySyringe = new ItemRequirement("Empty syringe", ItemID.CASTLE_DRAKAN_EMPTY_SYRINGE);
		fullSyringe = new ItemRequirement("Full syringe", ItemID.CASTLE_DRAKAN_FULL_SYRINGE);

		leftHalfOfCrest = new ItemRequirement("Left crest half", ItemID.CASTLE_DRAKAN_LEFT_CREST_HALF);
		rightHalfOfCrest = new ItemRequirement("Right crest half", ItemID.CASTLE_DRAKAN_RIGHT_CREST_HALF);
		fullCrest = new ItemRequirement("Full crest", ItemID.CASTLE_DRAKAN_FULL_CREST);

		larderSinkNeedsPlugging = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_LARDER_SINK_COLLAPSED, 0);
		larderSinkPlugged = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_LARDER_SINK_COLLAPSED, 1);
		larderSinkCollapsed = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_LARDER_SINK_COLLAPSED, 2);

		hasCutVenatorStomachUp = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_KITCHEN_VENATOR_OPENED, 1);

		unlockedKitchenChest = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_KITCHEN_CHEST, 1);

		battleAxe = new ItemRequirement("Battleaxe", ItemID.CASTLE_DRAKAN_OLD_BATTLEAXE);
		needToPutAxeOnStatue = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_2, 0);

		mace = new ItemRequirement("Mace", ItemID.CASTLE_DRAKAN_OLD_MACE);
		mace2 = mace.quantity(2);

		sword = new ItemRequirement("Sword", ItemID.CASTLE_DRAKAN_OLD_SWORD);

		needToPutMaceOnStatue1 = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_4, 0);
		needToPutMaceOnStatue2 = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_3, 0);

		needToPutSwordOnStatue = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_1, 0);
		doneWithWeaponPuzzle = and(new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_1, 1), new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_2, 1), new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_3, 1), new VarbitRequirement(VarbitID.CASTLE_DRAKAN_ARMOURY_STATUE_4, 1));

		fullCrestInStudy = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PARLOUR_FIREPLACE, 1);
		gibbousMoonKey = new ItemRequirement("Gibbous moon key", ItemID.CASTLE_DRAKAN_GIBBOUS_MOON_KEY);
		ornateKnife = new ItemRequirement("Ornate knife", ItemID.CASTLE_DRAKAN_ORNATE_KNIFE);
		pulledUpperStoreroomLever = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_TELEPORTER_UPPER_STOREROOM_TO_GUEST_CHAMBER_STOREROOM, 1);
		isFireplaceLit = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_LOWERNIEL_FIREPLACE_LIT, 1);
		smallLockbox = new ItemRequirement("Lockbox", ItemID.CASTLE_DRAKAN_LOCKBOX);
		fancyGem1 = new ItemRequirement("Fancy gem", ItemID.CASTLE_DRAKAN_FANCY_GEM_1);
		fancyGem2 = new ItemRequirement("Fancy gem", ItemID.CASTLE_DRAKAN_FANCY_GEM_2);
		venatorHeadOneEyePlaced = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_VENATOR_HEAD, 1);
		doorPuzzleSolved = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_LOWERNIEL_LIBRARY_DOOR_UNLOCKED, 1);
		mysteriousBook = new ItemRequirement("Mysterious book", ItemID.CASTLE_DRAKAN_MYSTERIOUS_BOOK);
		venatorHeadBothEyePlaced = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_VENATOR_HEAD, 3);
		ornateHourglass = new ItemRequirement("Ornate hourglass", ItemID.CASTLE_DRAKAN_ORNATE_HOURGLASS);
		ornateHourglass.setTooltip("You can return to the puzzle room with the venator and search the venator head to get a new one");
		// This is in an attempt to hide the requirement if it's already been placed in the display case
		var skullInDisplayCase = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_GALLERY_DISPLAY_CASE, true, 0);
		var knifeInDisplayCase = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_GALLERY_DISPLAY_CASE, true, 1);
		var hourglassInDisplayCase = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_GALLERY_DISPLAY_CASE, true, 2);
		ornateSkull.setConditionToHide(skullInDisplayCase);
		ornateKnife.setConditionToHide(knifeInDisplayCase);
		ornateHourglass.setConditionToHide(hourglassInDisplayCase);

		/// The display case consumes these three, so progress has to read the case as well as the
		/// inventory or the route sends the player back to an empty table.
		gotOrnateSkull = or(ornateSkull, skullInDisplayCase);
		gotOrnateKnife = or(ornateKnife, knifeInDisplayCase);
		gotOrnateHourglass = or(ornateHourglass, hourglassInDisplayCase);
		holdingADisplayItem = or(ornateSkull, ornateKnife, ornateHourglass);
		allItemsPlacedInDisplayCase = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_GALLERY_DISPLAY_CASE, 7);
		gildedKey = new ItemRequirement("Gilded key", ItemID.CASTLE_DRAKAN_GILDED_KEY);
		hasUsedGildedKey = new VarplayerRequirement(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_3, true, 3);
		gildedKey.setConditionToHide(hasUsedGildedKey);
		fmkIvanFollowingYou = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);
		gildedBookPuzzleOpen = new WidgetPresenceRequirement(InterfaceID.CastleDrakanBookcase.CONTENTS);
		gildedBook = new ItemRequirement("Gilded book", ItemID.CASTLE_DRAKAN_GILDED_BOOK);

		startedLibraryPuzzle = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LIBRARY, 1);
		finishedLibraryPuzzle = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LIBRARY, 2);

		fullMoonKey = new ItemRequirement("Full moon key", ItemID.CASTLE_DRAKAN_FULL_MOON_KEY);

		cloudyGreyPotion = new ItemRequirement("Cloudy grey potion", ItemID.CASTLE_DRAKAN_CLOUDY_GREY_POTION);
		weightlessBlackPotion = new ItemRequirement("Weightless black potion", ItemID.CASTLE_DRAKAN_WEIGHTLESS_BLACK_POTION);
		thickRedPotion = new ItemRequirement("Thick red potion", ItemID.CASTLE_DRAKAN_THICK_RED_POTION);
		coldBlueishWhitePotion = new ItemRequirement("Cold bluish-white potion", ItemID.CASTLE_DRAKAN_COLD_WHITE_POTION);

		solvedSmokeBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_SMOKE_BASIN, 1);
		solvedShadowBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_SHADOW_BASIN, 1);
		solvedBloodBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_BLOOD_BASIN, 1);
		solvedIceBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_ICE_BASIN, 1);
		solvedAllBasins = and(solvedBloodBasin, solvedShadowBasin, solvedIceBasin, solvedSmokeBasin);

		ancientSymbol = new ItemRequirement("Ancient symbol", ItemID.CASTLE_DRAKAN_ANCIENT_SYMBOL);
		openedPortalFromChapelLibraryToServantsQuarters = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_TELEPORTER_SERVANTS_QUARTERS_TO_CHAPEL_LIBRARY, 1);
		ancientShield = new ItemRequirement("Ancient shield", ItemID.CASTLE_DRAKAN_ANCIENT_SHIELD);
		shieldWithSymbol = new ItemRequirement("Shield with symbol", ItemID.CASTLE_DRAKAN_SHIELD_WITH_SYMBOL);
		hasMountedShield = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_SHIELD_MOUNT, 1);
		solidKey = new ItemRequirement("Solid key", ItemID.CASTLE_DRAKAN_LAB_KEY);

		openedDungeonToFreeSafalaanNewMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_4, 13);
		openedServantsQuarterNewMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_3, 1);
		usedUpNewMoonKey = and(openedDungeonToFreeSafalaanNewMoonDoor, openedServantsQuarterNewMoonDoor);

		usedSolidKey = new VarplayerRequirement(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_4, true, 5);

		openedLobbyHalfMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_1, 3);
		openedDiningHalfMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_1, 21);
		openedThroneHalfMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_1, 27);
		openedGalleryCrescentDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_2, 13);
		openedKitchenCrescentDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_2, 11);
		openedBasementCrescentDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_4, 9);
		openedDiningGibbousDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_1, 9);
		openedVenatorPuzzleGibbousDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_3, 27);
		openedGuestStoreroomGibbousDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_3, 23);
		openedExplosiveHallwayGibbousDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_1, 23);
		openedCrescentDoorRoomDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_3, 5);
		openedBottleRoomFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_2, 19);
		openedRoomAboveStudyFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_1, 29);
		openedSolidKeyRoomFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_4, 1);
		openedStockpileFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_3, 29);
		openedBedroomFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_2, 23);
		openedChapelLibraryFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_2, 21);
		openedNorthChapelFullMoonDoor = openedDoor(VarPlayerID.CASTLE_DRAKAN_DOOR_STATUS_2, 17);

		usedUpHalfMoonKey = and(openedLobbyHalfMoonDoor, openedDiningHalfMoonDoor, openedThroneHalfMoonDoor);
		usedUpGibbousMoonKey = and(openedDiningGibbousDoor, openedExplosiveHallwayGibbousDoor,
			openedGuestStoreroomGibbousDoor, openedVenatorPuzzleGibbousDoor);
		openedAHalfMoonDoor = or(openedLobbyHalfMoonDoor, openedDiningHalfMoonDoor, openedThroneHalfMoonDoor);
		usedUpFullMoonKey = and(openedBottleRoomFullMoonDoor, openedRoomAboveStudyFullMoonDoor,
			openedSolidKeyRoomFullMoonDoor, openedStockpileFullMoonDoor, openedBedroomFullMoonDoor,
			openedChapelLibraryFullMoonDoor, openedNorthChapelFullMoonDoor);

		vialOfWater = new ItemRequirement("Vial of water", ItemID.CASTLE_DRAKAN_VIAL_OF_WATER);
		vialsOfWater2 = vialOfWater.quantity(2);
		vialOfBlood = new ItemRequirement("Vial of blood", ItemID.CASTLE_DRAKAN_VIAL_OF_BLOOD);
		vialsOfBlood3 = vialOfBlood.quantity(3);
		pureEssence = new ItemRequirement("Pure essence", ItemID.CASTLE_DRAKAN_PURE_ESSENCE);
		pureEssence3 = pureEssence.quantity(3);

		chemicalVial = new ItemRequirement("Chemical vial", ItemID.CASTLE_DRAKAN_CHEMICAL_VIAL);
		chemicalVial2 = chemicalVial.quantity(2);
		chemicalVial3 = chemicalVial.quantity(3);
		chemicalVial4 = chemicalVial.quantity(4);

		hasPouredAnything = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LAB_BASIN, 0, Operation.GREATER);

		var refinerVb = new VarbitBuilder(VarbitID.CASTLE_DRAKAN_REFINER_ITEM_1);
		refinerWater = refinerVb.eq(1);
		refinerBlood = refinerVb.eq(2);
		refinerEssence = refinerVb.eq(3);

		finishedLabPuzzle = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LAB_BASIN, 53);

		venatorAlive = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_5);
		var venatorAlive1 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR);
		var venatorAlive2 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_2);
		var venatorAlive3 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_3);
		var venatorAlive4 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_4);
		// TODO: It would be nice if NpcCondition could accept an array of npc ids, similar to how NpcStep works. Preferably this just works with the constructor without the need for a function like "addAlternateNPCs" since there's no need for there to be a primary npc for a condition.
		anyVenatorAlive = or(venatorAlive, venatorAlive1, venatorAlive2, venatorAlive3, venatorAlive4);

		// could also use [2026-07-11T11:49:32Z 9968] varp CASTLE_DRAKAN_ENEMY_STATUS_1 (5640) 1238587111 -> 1507022567
		fmkVenatorAlive = new NpcCondition(16213);
	}

	void setupSteps()
	{
		castleDrakanRoomNetwork = new CastleDrakanRoomNetwork(quest);
		setupRoomNetwork();
		setupHalfMoonKeySteps();
		setupClockSteps();
		setupCrescentMoonKeySteps();
		setupNewMoonKeySteps();
		setupGildedAndGibbousKeySteps();
		setupFullMoonKeySteps();
		setupSolidKeySteps();
		setupBloodStockpileSteps();
	}

	private void setupHalfMoonKeySteps()
	{
		var investigateThrone = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_THRONE, new WorldPoint(2313, 7392, 0), "Investigate the throne.");
		var investigateThroneAgain = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_THRONE, new WorldPoint(2313, 7392, 0), "Investigate the throne again.");

		var pullBust1 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_BUST01_LEVER_DOWN, new WorldPoint(2317, 7393, 0), "Pull the northern-most bust.");
		var pullBust2 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_BUST02_LEVER_DOWN, new WorldPoint(2317, 7392, 0), "Pull the second northern-most bust.");
		var pullBust3 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_BUST03_LEVER_DOWN, new WorldPoint(2317, 7391, 0), "Pull the second southern-most bust.");
		var pullBust4 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_BUST04_LEVER_DOWN, new WorldPoint(2317, 7390, 0), "Pull the southern-most bust.");

		var hmkPullBusts = new ConditionalStep(quest, pullBust2, "Pull the busts in the right order.");
		hmkPullBusts.addStep(needToPullBust3, pullBust3);
		hmkPullBusts.addStep(needToPullBust4, pullBust4);
		hmkPullBusts.addStep(needToPullBust1, pullBust1);
		hmkPullBustsPW = hmkPullBusts.puzzleWrapStepWithDefaultText("Solve the puzzle in the room.");

		var getKeyFromThroneRoom = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_THRONE, new WorldPoint(2313, 7392, 0), "Search the throne in the throne room for the half moon key.");
		investigateThroneAgain.addSubSteps(getKeyFromThroneRoom);

		var solveThronePuzzle = new ConditionalStep(quest, investigateThrone, "Solve the throne-room puzzle.");
		solveThronePuzzle.addStep(needToStartThroneRoomPuzzle, investigateThrone);
		solveThronePuzzle.addStep(needToPullBusts, hmkPullBustsPW);
		solveThronePuzzle.addStep(needToGetKey, investigateThroneAgain);
		cGetHalfMoonKey = castleAction(RoomKey.THRONE_ROOM, solveThronePuzzle,
			"Head to the throne room to get the half moon key.");
		var takeHalfMoonKey = castleAction(RoomKey.THRONE_ROOM, getKeyFromThroneRoom,
			"Return to the throne room and take the half moon key.");

		cGetHalfMoonKeyGoal = new CastleDrakanGoalStep(quest, cGetHalfMoonKey,
			"Get the half moon key from the throne room.");
		cGetHalfMoonKeyGoal.addStep(and(doneWithThroneRoomPuzzle,
			not(or(halfMoonKey, usedUpHalfMoonKey))), takeHalfMoonKey);
		cGetHalfMoonKeyGoal.orderSidebar(cGetHalfMoonKey, takeHalfMoonKey);
	}

	private void setupClockSteps()
	{
		var searchShelvesForSmallClockHand = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_SHELVES, new WorldPoint(2323, 7387, 0), "Search the shelves for a small clock hand in the room south of the throne room.");
		cGetSmallClockHand = castleAction(RoomKey.ROOM_SOUTH_OF_THRONE, searchShelvesForSmallClockHand,
			"Head to the room south of the throne room to get the small clock hand.");

		pickUpTinderbox = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_TINDERBOX_CRATE, new WorldPoint(2344, 7387, 0), "Search the sparkling chest for a tinderbox.");

		pickUpExplosiveBarrel = new ItemStep(quest, new WorldPoint(2439, 7388, 0), "Pick up the explosive barrel.", explosiveBarrel, tinderbox);

		searchCrateForDrakanEmblem1 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_CRATE, new WorldPoint(2454, 7378, 0), "Search the crate for a drakan emblem.");

		var placeEmblem1OnReceptacle = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_HOLDER_EMPTY, new WorldPoint(2469, 7408, 0), "Place the drakan emblem on the empty receptacle next to the southern door down the hall, avoiding traps on the way.", explosiveBarrel, tinderbox, anyOneEmblemHighlighted);
		placeEmblem1OnReceptacle.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2457, 7409, 0),
			new WorldPoint(2457, 7410, 0),
			new WorldPoint(2456, 7409, 0),
			new WorldPoint(2456, 7410, 0),
			new WorldPoint(2461, 7408, 0),
			new WorldPoint(2461, 7409, 0),
			new WorldPoint(2460, 7408, 0),
			new WorldPoint(2460, 7409, 0)
		);

		var searchCrateInVanesculasStudyForLargeClockHand = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_CRATE, new WorldPoint(2466, 7370, 0), "Search the crate in the north-west corner of Vanescula's study for a large clock hand.", explosiveBarrel, tinderbox);

		var getTinderbox = castleAction(RoomKey.STORAGE_ROOM, pickUpTinderbox,
			"Head to the storage room to get a tinderbox.");
		var getExplosiveBarrel = castleAction(RoomKey.EXPLOSIVE_ROOM, pickUpExplosiveBarrel,
			"Head upstairs from the storage room to get an explosive barrel.");
		var getFirstEmblem = castleAction(RoomKey.FIRST_EMBLEM_ROOM, searchCrateForDrakanEmblem1,
			"Head to the room south of the explosive barrel room to get a Drakan emblem.");
		var unlockVanesculasStudy = castleAction(RoomKey.VANESCULAS_HALLWAY, placeEmblem1OnReceptacle,
			"Head to Vanescula's hallway with the tinderbox, explosive barrel, and Drakan emblem.");
		var getLargeHand = castleAction(RoomKey.VANESCULAS_STUDY,
			searchCrateInVanesculasStudyForLargeClockHand,
			"Head into Vanescula's study to find the large clock hand.");

		var cGetLargeClockHand = new ConditionalStep(quest, getLargeHand,
			"Gather the supplies needed to reach the large clock hand.");
		cGetLargeClockHand.addStep(not(tinderbox), getTinderbox);
		cGetLargeClockHand.addStep(not(explosiveBarrel), getExplosiveBarrel);
		cGetLargeClockHand.addStep(not(or(drakanEmblem1, placedEmblemInVanesculasHallway)), getFirstEmblem);
		cGetLargeClockHand.addStep(not(placedEmblemInVanesculasHallway), unlockVanesculasStudy);

		var placeSmallClockHandOnWesternClock = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CLOCK_BIG_HAND, new WorldPoint(2344, 7372, 0), "Place the small clock hand on the western Grandfather clock.", smallClockHand.highlighted());
		placeSmallClockHandOnWesternClock.addIcon(ItemID.CASTLE_DRAKAN_SMALL_CLOCK_HAND);

		var solveWesternClock = new DrakanClockSolver(quest, 15514, 11, 15513, 9);
		var solveWesternClockPW = solveWesternClock.puzzleWrapStepWithDefaultText(
			"Put the clock hands in the right orientation.");
		var clickWesternClock = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CLOCK_BOTH_HANDS, new WorldPoint(2344, 7372, 0), "Click the western clock.");
		clickWesternClock.addDialogStep("Yes.");

		var placeLargeClockHandOnEasternClock = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CLOCK_SMALL_HAND, new WorldPoint(2350, 7372, 0), "Place the large clock hand on the eastern Grandfather clock.", largeClockHand.highlighted());
		placeLargeClockHandOnEasternClock.addIcon(ItemID.CASTLE_DRAKAN_LARGE_CLOCK_HAND);

		var solveEasternClock = new DrakanClockSolver(quest, 15517, 0, 15515, 4);
		var solveEasternClockPW = solveEasternClock.puzzleWrapStepWithDefaultText(
			"Put the clock hands in the right orientation.");
		var clickEasternClock = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CLOCK_BOTH_HANDS, new WorldPoint(2350, 7372, 0), "Click the eastern clock.");
		clickEasternClock.addDialogStep("Yes.");

		var closeClock = new WidgetStep(quest, "Close the clock.", 963, 16);

		var getEmblem2FromFireplace = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_DINING_ROOM_FIREPLACE, new WorldPoint(2347, 7372, 0), "Get the emblem from the fireplace in the dining room.");
		getEmblem2FromFireplace.addDialogStep("Search the fireplace.");
		var cSolveClockPuzzle = new ConditionalStep(quest, getEmblem2FromFireplace, "Solve the clock puzzle.");
		cSolveClockPuzzle.addStep(smallClockHandNeedsReplacing, placeSmallClockHandOnWesternClock);
		cSolveClockPuzzle.addStep(and(westernClockNeedsFixing, playerAtWesternClock, clockWidgetOpen), solveWesternClockPW);
		cSolveClockPuzzle.addStep(and(playerAtWesternClock, clockWidgetOpen), closeClock);
		cSolveClockPuzzle.addStep(westernClockNeedsFixing, clickWesternClock);
		cSolveClockPuzzle.addStep(largeClockHandNeedsReplacing, placeLargeClockHandOnEasternClock);
		cSolveClockPuzzle.addStep(and(easternClockNeedsFixing, playerAtEasternClock, clockWidgetOpen), solveEasternClockPW);
		cSolveClockPuzzle.addStep(and(playerAtEasternClock, clockWidgetOpen), closeClock);
		cSolveClockPuzzle.addStep(easternClockNeedsFixing, clickEasternClock);

		var repairClocks = castleAction(RoomKey.DINING_ROOM, cSolveClockPuzzle,
			"Return to the dining room to repair the clocks.");
		cFixClocksGoal = new CastleDrakanGoalStep(quest, repairClocks,
			"Find the clock hands, repair both clocks, and search the fireplace for a Drakan emblem.");
		cFixClocksGoal.addStep(and(not(smallClockHand), smallClockHandNeedsReplacing), cGetSmallClockHand);
		cFixClocksGoal.addStep(and(not(largeClockHand), largeClockHandNeedsReplacing), cGetLargeClockHand);
		cFixClocksGoal.addStep(needToFinishClockPuzzle, repairClocks);
		cFixClocksGoal.orderSidebar(cGetSmallClockHand, getTinderbox, getExplosiveBarrel, getFirstEmblem,
			unlockVanesculasStudy, getLargeHand, repairClocks);
	}

	private void setupCrescentMoonKeySteps()
	{
		var cmkPlaceEmblemInVanesculasHallway = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_HOLDER_EMPTY, new WorldPoint(2469, 7408, 0), "Place the emblem in the receptacle in Vanescula's hallway.", anyOneEmblemHighlighted);
		var cmkPlaceEmblemInVanesculasStudy = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_HOLDER_EMPTY, new WorldPoint(2476, 7367, 0), "Place the emblem in Vanescula's study.", anyOneEmblemHighlighted);
		var cmkBlowUpWallInVanesculasChamber = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_WALL_CRACKED, new WorldPoint(2492, 7364, 0), "Place the explosive barrel on the cracked wall in Vanescula's chamber.", explosiveBarrel.highlighted(), tinderbox);
		cmkBlowUpWallInVanesculasChamber.addIcon(ItemID.CASTLE_DRAKAN_POTENT_BARREL);
		var cmkTakeEmblem3 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_HOLDER_FILLED, new WorldPoint(2486, 7421, 0), "Remove the emblem from the room with the Venator.\n\nProtect from ranged or melee depending on if you're in melee range or not.\n\nTurn off your protection prayer when it shrieks.");
		var cmkTakeEmblemFromVanesculasStudy = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_HOLDER_FILLED, new WorldPoint(2476, 7367, 0), "Take emblem from vanescula's study.");
		var cmkRetrieveThirdEmblem = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_VANESCULAS_HALLWAY_TO_VANESCULAS_STUDY, new WorldPoint(2469, 7408, 0), "Remove the third emblem from the receptacle.");
		var cmkPutEmblemInEastDoor = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_VANESCULAS_HALLWAY_TO_RANIS_HALLWAY, new WorldPoint(2476, 7410, 0), "Place an emblem in the empty receptacle by the east wall.", anyOneEmblemHighlighted);
		var cmkPutEmblemInRanisHallwayNorth = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_RANIS_HALLWAY_TO_RANIS_PARLOUR, new WorldPoint(2486, 7404, 0), "Place an emblem in the empty receptacle at the north door.", anyOneEmblemHighlighted);
		var cmkGetSkull = new DetailedQuestStep(quest, new WorldPoint(2471, 7384, 0), "Get the ornate skull from the table in the room.", ornateSkull);
		var cmkRemoveEmblemRanisNorth = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_RANIS_HALLWAY_TO_RANIS_PARLOUR, new WorldPoint(2486, 7404, 0), "Remove emblem from the receptacle.", ornateSkull);
		var cmkPlaceEmblemDownstairs = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_WEST_CHAPEL_HALLWAY_TO_NORTH_CHAPEL_HALLWAY, new WorldPoint(2371, 7410, 0), "Place an emblem in the receptacle to the west, avoiding the traps on the floor.", ornateSkull, anyOneEmblemHighlighted);
		cmkPlaceEmblemDownstairs.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2378, 7411, 0),
			new WorldPoint(2379, 7411, 0),
			new WorldPoint(2379, 7412, 0),
			new WorldPoint(2378, 7412, 0)
		);
		var cmkPutEmblemInWestChapelHallway = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_EMBLEM_GALLERY_TO_WEST_CHAPEL_HALLWAY, new WorldPoint(2370, 7383, 0), "Place an emblem in the empty receptacle by the western door.", anyOneEmblemHighlighted);
		var cmkTalkToVeliaf = new NpcStep(quest, NpcID.MYQ6_VELIAF_VIS, new WorldPoint(2379, 7367, 0), "Talk to Veliaf Hurtz in the emblem gallery.");
		var cmkOpenEmblemGalleryChest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_GALLERY_CHEST, new WorldPoint(2379, 7372, 0), "Search the chest in the northern part of the emblem gallery where you talked to Veliaf Hurtz.");
		// TODO: puzzle wrap?
		var cmkArrowChestPuzzleStep = new ArrowChestPuzzleStep(quest);
		cmkArrowChestPuzzleStep.setSolution(1, 2, 1, 3, 3);
		cmkArrowChestPuzzleStepPW = cmkArrowChestPuzzleStep.puzzleWrapStepWithDefaultText("Solve the chest puzzle.");
		var cmkGetTheKeyFromTheChest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_GALLERY_CHEST, new WorldPoint(2379, 7372, 0), "Search the chest in the emblem gallery in Castle Drakan where you first spoke with Veliaf for the Crescent moon key. You will need 3 emblems to get all the way there again.");

		var placeFirstCrescentEmblem = castleAction(RoomKey.VANESCULAS_HALLWAY, cmkPlaceEmblemInVanesculasHallway,
			"Head to Vanescula's hallway and place an emblem in the study-door receptacle.");
		var placeStudyEmblem = castleAction(RoomKey.VANESCULAS_STUDY, cmkPlaceEmblemInVanesculasStudy,
			"Enter Vanescula's study and place an emblem in its receptacle.");
		var destroyChamberWall = castleAction(RoomKey.VANESCULAS_CHAMBER, cmkBlowUpWallInVanesculasChamber,
			"Enter Vanescula's chamber and blow up the cracked wall.");
		var getThirdEmblem = castleAction(RoomKey.VANESCULAS_VENATOR_ROOM, cmkTakeEmblem3,
			"Enter the room beyond the destroyed wall and take the third emblem.");
		var recoverStudyEmblem = castleAction(RoomKey.VANESCULAS_STUDY, cmkTakeEmblemFromVanesculasStudy,
			"Return to Vanescula's study and recover the emblem from its receptacle.");
		var recoverHallwayEmblem = castleAction(RoomKey.VANESCULAS_HALLWAY, cmkRetrieveThirdEmblem,
			"Return to Vanescula's hallway and take its emblem back.");
		var unlockRanisHallway = castleAction(RoomKey.VANESCULAS_HALLWAY, cmkPutEmblemInEastDoor,
			"Return to Vanescula's hallway and open the eastern route.");
		var unlockRanisParlour = castleAction(RoomKey.RANIS_HALLWAY, cmkPutEmblemInRanisHallwayNorth,
			"Enter Ranis' hallway and place an emblem by the north door.");
		var getOrnateSkull = castleAction(RoomKey.RANIS_PARLOUR, cmkGetSkull,
			"Enter Ranis' parlour and get the ornate skull.");
		var recoverRanisEmblem = castleAction(RoomKey.RANIS_HALLWAY, cmkRemoveEmblemRanisNorth,
			"Return to Ranis' hallway and recover the emblem.");
		var unlockWestChapelHall = castleAction(RoomKey.NORTH_CHAPEL_HALLWAY, cmkPlaceEmblemDownstairs,
			"Go downstairs and place an emblem by the western door.");
		var unlockEmblemGallery = castleAction(RoomKey.WEST_CHAPEL_HALLWAY, cmkPutEmblemInWestChapelHallway,
			"Enter the west chapel hallway and place an emblem by the gallery door.");
		var solveCrescentChest = new ConditionalStep(quest, cmkTalkToVeliaf, "Solve the emblem-gallery chest.");
		solveCrescentChest.addStep(cmkSolvedChestPuzzle, cmkGetTheKeyFromTheChest);
		solveCrescentChest.addStep(and(cmkHasSpokenWithVeliaf, inArrowPuzzle), cmkArrowChestPuzzleStepPW);
		solveCrescentChest.addStep(cmkHasSpokenWithVeliaf, cmkOpenEmblemGalleryChest);
		var getCrescentKey = castleAction(RoomKey.EMBLEM_GALLERY, solveCrescentChest,
			"Enter the emblem gallery, speak to Veliaf, and solve the chest puzzle.");

		// Neither placed emblem can be borrowed: the hallway one holds the study door open.
		var stillHasAPlacementToMake = or(not(placedEmblemInVanesculasHallway),
			not(placedEmblemInVanesculasStudy));
		var needsAnEmblemBeforeTheWall = and(not(vanesculasChamberWallDestroyed), not(anyOneEmblem),
			stillHasAPlacementToMake);

		var shortForTheRemainingDoors = and(vanesculasChamberWallDestroyed,
			carryingFewerEmblemsThanTheRemainingDoorsNeed());
		var anEmblemCanBeTakenBack = or(emblemInVenatorRoomHolder, placedEmblemInVanesculasStudy,
			placedEmblemInVanesculasHallway);
		var needsAnotherEmblem = or(needsAnEmblemBeforeTheWall,
			and(shortForTheRemainingDoors, not(anEmblemCanBeTakenBack)));

		var recoverFirstEmblem = castleRecovery(RoomKey.FIRST_EMBLEM_ROOM, searchCrateForDrakanEmblem1,
			"Search the crate south of the explosive barrel room for another Drakan emblem.",
			needsAnotherEmblem);

		var needsAnotherTinderbox = and(not(vanesculasChamberWallDestroyed), not(tinderbox));
		var needsAnotherExplosiveBarrel = and(not(vanesculasChamberWallDestroyed), not(explosiveBarrel));
		var recoverTinderbox = castleRecovery(RoomKey.STORAGE_ROOM, pickUpTinderbox,
			"Return to the storage room for another tinderbox.", needsAnotherTinderbox);
		var recoverExplosiveBarrel = castleRecovery(RoomKey.EXPLOSIVE_ROOM, pickUpExplosiveBarrel,
			"Head upstairs from the storage room for another explosive barrel.", needsAnotherExplosiveBarrel);

		cGetCrescentMoonKey = new CastleDrakanGoalStep(quest, getCrescentKey, "Get the crescent moon key.");
		cGetCrescentMoonKey.addStep(needsAnotherEmblem, recoverFirstEmblem);
		cGetCrescentMoonKey.addStep(and(not(vanesculasChamberWallDestroyed),
			not(placedEmblemInVanesculasHallway)), placeFirstCrescentEmblem);
		cGetCrescentMoonKey.addStep(and(not(vanesculasChamberWallDestroyed),
			not(placedEmblemInVanesculasStudy)), placeStudyEmblem);
		cGetCrescentMoonKey.addStep(needsAnotherTinderbox, recoverTinderbox);
		cGetCrescentMoonKey.addStep(needsAnotherExplosiveBarrel, recoverExplosiveBarrel);
		cGetCrescentMoonKey.addStep(not(vanesculasChamberWallDestroyed), destroyChamberWall);
		// Top up from the nearest receptacle that still holds one, closest to the route first.
		cGetCrescentMoonKey.addStep(and(shortForTheRemainingDoors, emblemInVenatorRoomHolder), getThirdEmblem);
		cGetCrescentMoonKey.addStep(and(shortForTheRemainingDoors, placedEmblemInVanesculasStudy),
			recoverStudyEmblem);
		cGetCrescentMoonKey.addStep(and(shortForTheRemainingDoors, placedEmblemInVanesculasHallway),
			recoverHallwayEmblem);
		cGetCrescentMoonKey.addStep(not(anyEmblemInVanesculasHallwayEast), unlockRanisHallway);
		cGetCrescentMoonKey.addStep(and(not(gotOrnateSkull), not(anyEmblemInRanisHallwayNorth)), unlockRanisParlour);
		cGetCrescentMoonKey.addStep(not(gotOrnateSkull), getOrnateSkull);
		cGetCrescentMoonKey.addStep(and(gotOrnateSkull, anyEmblemInRanisHallwayNorth), recoverRanisEmblem);
		cGetCrescentMoonKey.addStep(not(anyEmblemInNorthChapelHallway), unlockWestChapelHall);
		cGetCrescentMoonKey.addStep(not(anyEmblemInWestChapelHallway), unlockEmblemGallery);
		cGetCrescentMoonKey.orderSidebar(recoverFirstEmblem, placeFirstCrescentEmblem, placeStudyEmblem,
			recoverTinderbox, recoverExplosiveBarrel, destroyChamberWall, getThirdEmblem, recoverStudyEmblem,
			recoverHallwayEmblem, unlockRanisHallway, unlockRanisParlour, getOrnateSkull, recoverRanisEmblem,
			unlockWestChapelHall, unlockEmblemGallery, getCrescentKey);
	}

	private void setupNewMoonKeySteps()
	{
		var killBugsAndTakeSyringeBarrel = new DetailedQuestStep(quest, new WorldPoint(2315, 7418, 0), "Kill the bugs and take the Syringe barrel.", syringeBarrel);
		var searchCrateForVenatorStomach = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_CRATE, new WorldPoint(2315, 7408, 0), "Search the create in the south-east corner for a venator stomach.", syringeBarrel);
		var retrievePlugFromTheSinkToTheWest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_KITCHEN_SINK, new WorldPoint(2308, 7411, 0), "Retrieve the plug from the sink to the west.", syringeBarrel, venatorStomach);
		retrievePlugFromTheSinkToTheWest.addDialogStep("Remove the plug.");
		var useSinkPlugOnSinkInLarder = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LARDER_SINK, new WorldPoint(2356, 7400, 0), "Put the sink plug into the sink.", sinkPlug.highlighted());
		useSinkPlugOnSinkInLarder.addIcon(ItemID.CASTLE_DRAKAN_SINK_PLUG);
		var turnSinkTapOn = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LARDER_SINK, new WorldPoint(2356, 7400, 0), "Turn the tap on in the sink.");
		turnSinkTapOn.addDialogStep("Turn the tap on.");
		turnSinkTapOn.addDialogStep("Yes.");
		var takePipe = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LARDER_SINK, new WorldPoint(2356, 7400, 0), "Take the pipe from the now-broken sink.");
		takePipe.addDialogStep("Yes.");
		var reachBehindCabinetWithPipe = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LARDER_CABINET, new WorldPoint(2360, 7403, 0), "Use the broken pipe on the cabinet to reach behind it.", brokenPipe.highlighted());
		reachBehindCabinetWithPipe.addIcon(ItemID.CASTLE_DRAKAN_BROKEN_PIPE);
		var searchLarderCrateForSyringePlunger = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_CRATE, new WorldPoint(2358, 7397, 0), "Search the crate for a syringe plunger.");
		var useSharpKnifeOnVenatorCorpse = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_KITCHEN_VENATOR, new WorldPoint(2311, 7417, 0), "Use the sharp kitchen knife on the venator corpse.", sharpKitchenKnife.highlighted());
		useSharpKnifeOnVenatorCorpse.addIcon(ItemID.CASTLE_DRAKAN_SHARP_KNIFE);
		var getTongsFromVenatorCorpse = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_KITCHEN_VENATOR, new WorldPoint(2311, 7417, 0), "Search the venator corpse for tongs.");
		var getNeedleFromSink = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_KITCHEN_SINK, new WorldPoint(2308, 7411, 0), "Use the tongs on the sink to get a syringe needle.", tongs.highlighted());
		getNeedleFromSink.addIcon(ItemID.CASTLE_DRAKAN_TONGS);
		var assembleSyringe = new DetailedQuestStep(quest, "Combine the syringe parts to assemble a syringe.", syringeBarrel.highlighted(), syringePlunger.highlighted(), syringeNeedle.highlighted());
		var drawBloodFromVenatorStomach = new DetailedQuestStep(quest, "Use the empty syringe on the venator stomach in your inventory to draw blood from it.", emptySyringe.highlighted(), venatorStomach.highlighted());
		var useSyringeOnChest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_KITCHEN_CHEST, new WorldPoint(2308, 7415, 0), "Use the full syringe on the chest to receive the left crest half.", fullSyringe.highlighted());
		useSyringeOnChest.addIcon(ItemID.CASTLE_DRAKAN_FULL_SYRINGE);
		var getLeftCrestHalfFromKitchenChest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_KITCHEN_CHEST, new WorldPoint(2308, 7415, 0), "Search the chest in the kitchen for the left crest half.");
		getLeftCrestHalfFromKitchenChest.addDialogStep("Yes.");

		// I could technically use this varp to see if the venator in that room is dead
		// [2026-07-05T13:27:48Z 5913] varp CASTLE_DRAKAN_ENEMY_STATUS_2 (5641) 32830 -> 98366
		var killVenator = new NpcStep(quest, NpcID.CASTLE_DRAKAN_VENATOR_5, new WorldPoint(2569, 7384, 0), "Kill the Venator.\n\nProtect from ranged or melee depending on if you're in melee range or not.\n\nTurn off your protection prayer when it shrieks.");

		// NOTE: This would be very annoying to puzzlewrap, so I'm not doing it. Feel free to change it in a future PR!
		var searchWeaponRackForOneAxe = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_ARMOURY_WEAPON_CASE, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for one battleaxe.");
		searchWeaponRackForOneAxe.addDialogStep("Take a battleaxe.");
		var placeBattleAxeOnStatue = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_ARMOURY_STATUE_2, new WorldPoint(2577, 7380, 0), "Place the battleaxe on the east-most empty statue.", battleAxe.highlighted());
		var getMace1 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_ARMOURY_WEAPON_CASE, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for a mace.");
		getMace1.addDialogStep("Take a mace.");
		var getMace2 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_ARMOURY_WEAPON_CASE, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for two maces.");
		getMace2.addDialogStep("Take a mace.");
		getMace2.addSubSteps(getMace1);
		var getSword = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_ARMOURY_WEAPON_CASE, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for a sword.");
		getSword.addDialogStep("Take a sword.");

		var placeMaceOnStatueN = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_STATUE_UNARMED, new WorldPoint(2569, 7386, 0), "Place a mace on the second pair of statues from the west.", mace.highlighted());
		var placeMaceOnStatueS = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_STATUE_UNARMED, new WorldPoint(2569, 7380, 0), "Place a mace on the second pair of statues from the west.", mace.highlighted());

		var placeSwordOnStatue = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_STATUE_UNARMED, new WorldPoint(2565, 7386, 0), "Place a sword on the western empty statue.", sword.highlighted());

		var openWeaponPuzzleChest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_ARMOURY_CHEST, new WorldPoint(2570, 7380, 0), "Search the chest for the right half of a crest.");

		var combineCrests = new DetailedQuestStep(quest, "Combine the two pieces of crests in your inventory.", leftHalfOfCrest.highlighted(), rightHalfOfCrest.highlighted());

		var putFullCrestOnFireplaceInStudy = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_PARLOUR_FIREPLACE, new WorldPoint(2358, 7386, 0), "Place the full crest on the fireplace in the study, north of the throne room.", fullCrest.highlighted());
		putFullCrestOnFireplaceInStudy.addIcon(ItemID.CASTLE_DRAKAN_FULL_CREST);
		var getNewMoonKeyFromFireplace = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_PARLOUR_FIREPLACE, new WorldPoint(2358, 7386, 0), "Search the fireplace for the new moon key in the study, north of the throne room.");
		getNewMoonKeyFromFireplace.addDialogStep("Yes.");

		// One long fetch-and-assemble chain, so every step is its own objective: only the first
		// matching row is highlighted, and a wrapper row would swallow it from the step being done.
		var takeSyringeBarrel = castleAction(RoomKey.KITCHEN, killBugsAndTakeSyringeBarrel,
			"Head through the rooms west of the emblem gallery to the kitchen, and kill the bugs.");
		var getVenatorStomach = castleAction(RoomKey.KITCHEN, searchCrateForVenatorStomach,
			"Search the south-east kitchen crate for a venator stomach.");
		var takeSinkPlug = castleAction(RoomKey.KITCHEN, retrievePlugFromTheSinkToTheWest,
			"Take the plug from the western kitchen sink.");
		var plugLarderSink = castleAction(RoomKey.LARDER, useSinkPlugOnSinkInLarder,
			"Enter the larder and plug its sink.");
		var turnOnLarderTap = castleAction(RoomKey.LARDER, turnSinkTapOn,
			"Turn the larder sink's tap on.");
		var takeBrokenPipe = castleAction(RoomKey.LARDER, takePipe,
			"Take the pipe from the collapsed larder sink.");
		var reachBehindLarderCabinet = castleAction(RoomKey.LARDER, reachBehindCabinetWithPipe,
			"Use the pipe to reach behind the larder cabinet.");
		var getSyringePlunger = castleAction(RoomKey.LARDER, searchLarderCrateForSyringePlunger,
			"Search the larder crate for the syringe plunger.");
		var cutOpenVenatorCorpse = castleAction(RoomKey.KITCHEN, useSharpKnifeOnVenatorCorpse,
			"Cut the kitchen's venator corpse open with the sharp knife.");
		var getTongs = castleAction(RoomKey.KITCHEN, getTongsFromVenatorCorpse,
			"Search the venator corpse for tongs.");
		var getSyringeNeedle = castleAction(RoomKey.KITCHEN, getNeedleFromSink,
			"Use the tongs on the kitchen sink to get a syringe needle.");
		var fillSyringeAtChest = castleAction(RoomKey.KITCHEN, useSyringeOnChest,
			"Use the full syringe on the kitchen chest.");
		var takeLeftCrestHalf = castleAction(RoomKey.KITCHEN, getLeftCrestHalfFromKitchenChest,
			"Search the kitchen chest for the left crest half.");

		var weaponCrestPuzzle = new ConditionalStep(quest, killVenator,
			"Defeat the Venator and solve the weapon-statue puzzle.");
		weaponCrestPuzzle.addStep(venatorAlive, killVenator);
		weaponCrestPuzzle.addStep(doneWithWeaponPuzzle, openWeaponPuzzleChest);
		weaponCrestPuzzle.addStep(and(needToPutAxeOnStatue, battleAxe), placeBattleAxeOnStatue);
		weaponCrestPuzzle.addStep(needToPutAxeOnStatue, searchWeaponRackForOneAxe);
		weaponCrestPuzzle.addStep(and(needToPutMaceOnStatue1, needToPutMaceOnStatue2, mace2), placeMaceOnStatueN);
		weaponCrestPuzzle.addStep(and(needToPutMaceOnStatue1, needToPutMaceOnStatue2, mace), getMace2);
		weaponCrestPuzzle.addStep(and(needToPutMaceOnStatue1, needToPutMaceOnStatue2), getMace2);
		weaponCrestPuzzle.addStep(and(needToPutMaceOnStatue2, mace), placeMaceOnStatueN);
		weaponCrestPuzzle.addStep(and(needToPutMaceOnStatue1, mace), placeMaceOnStatueS);
		weaponCrestPuzzle.addStep(or(needToPutMaceOnStatue1, needToPutMaceOnStatue2), getMace1);
		weaponCrestPuzzle.addStep(and(needToPutSwordOnStatue, sword), placeSwordOnStatue);
		weaponCrestPuzzle.addStep(needToPutSwordOnStatue, getSword);
		/// One row on purpose: splitting it would print the puzzle's solution into the sidebar.
		var getRightCrestHalf = castleAction(RoomKey.BASEMENT_VENATOR_ROOM, weaponCrestPuzzle,
			"Head to the basement weapons room and obtain the right crest half.");

		var placeFullCrest = castleAction(RoomKey.STUDY, putFullCrestOnFireplaceInStudy,
			"Head to the study north of the throne room with the full crest.");
		var takeNewMoonKey = castleAction(RoomKey.STUDY, getNewMoonKeyFromFireplace,
			"Search the study fireplace for the new moon key.");

		var needsLeftCrestHalf = not(leftHalfOfCrest);

		cGetNewMoonKey = new CastleDrakanGoalStep(quest, takeSyringeBarrel, "Get the new moon key.");
		cGetNewMoonKey.addStep(fullCrestInStudy, takeNewMoonKey);
		cGetNewMoonKey.addStep(fullCrest, placeFullCrest);
		cGetNewMoonKey.addStep(and(leftHalfOfCrest, rightHalfOfCrest), combineCrests);
		cGetNewMoonKey.addStep(and(leftHalfOfCrest, not(rightHalfOfCrest)), getRightCrestHalf);

		var needsSyringePlunger = and(needsLeftCrestHalf, not(syringePlunger), not(emptySyringe), not(fullSyringe), not(unlockedKitchenChest));
		cGetNewMoonKey.addStep(and(needsSyringePlunger, sharpKitchenKnife), getSyringePlunger);
		// TODO: This should check: "needsToCutOpenVenatorStomach" instead of "needsSyringePlunger".
		// If a user picks up the syringe plunger first, the step to get the knife will be skipped.
		cGetNewMoonKey.addStep(and(needsSyringePlunger, brokenPipe), reachBehindLarderCabinet);
		cGetNewMoonKey.addStep(and(needsSyringePlunger, not(sharpKitchenKnife), larderSinkCollapsed), takeBrokenPipe);
		cGetNewMoonKey.addStep(and(needsSyringePlunger, larderSinkPlugged), turnOnLarderTap);
		cGetNewMoonKey.addStep(and(needsSyringePlunger, sinkPlug), plugLarderSink);

		// the kitchen chain, most-progressed first
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, unlockedKitchenChest), takeLeftCrestHalf);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, fullSyringe), fillSyringeAtChest);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, venatorStomach, emptySyringe), drawBloodFromVenatorStomach);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, syringeBarrel, venatorStomach, syringePlunger, syringeNeedle), assembleSyringe);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, syringeBarrel, venatorStomach, syringePlunger, tongs), getSyringeNeedle);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, syringeBarrel, venatorStomach, syringePlunger, hasCutVenatorStomachUp), getTongs);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, syringeBarrel, venatorStomach, sharpKitchenKnife, syringePlunger), cutOpenVenatorCorpse);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, syringeBarrel, venatorStomach), takeSinkPlug);
		cGetNewMoonKey.addStep(and(needsLeftCrestHalf, syringeBarrel), getVenatorStomach);

		cGetNewMoonKey.orderSidebar(takeSyringeBarrel, getVenatorStomach, takeSinkPlug, plugLarderSink,
			turnOnLarderTap, takeBrokenPipe, reachBehindLarderCabinet, getSyringePlunger,
			cutOpenVenatorCorpse, getTongs, getSyringeNeedle, assembleSyringe, drawBloodFromVenatorStomach,
			fillSyringeAtChest, takeLeftCrestHalf, getRightCrestHalf, combineCrests, placeFullCrest,
			takeNewMoonKey);
	}

	private void setupGildedAndGibbousKeySteps()
	{
		var ggkEnterBasementNorthRoom = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_DOOR_MOON_NEW, new WorldPoint(2570, 7369, 0), "Enter through the new moon door to the north to free Safalaan and Vanescula.");
		ggkWatchTheCutscene = ggkEnterBasementNorthRoom.cutscene();
		var ggkTakeGibbousMoonKey = new DetailedQuestStep(quest, new WorldPoint(2573, 7395, 0), "Take the gibbous moon key from the bench.", gibbousMoonKey);
		var ggkGetOrnateKnife = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_CRATE, new WorldPoint(2454, 7372, 0), "Search the eastern crate for an ornate knife.");
		var ggkPullLeverInOrnateKnifeRoom = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_TELEPORT_LEVER_GUEST_CHAMBER_STOREROOM, new WorldPoint(2449, 7371, 0), "Pull the lever to the west.");

		var ggkKillVenator = new NpcStep(quest, new int[]{NpcID.CASTLE_DRAKAN_VENATOR, NpcID.CASTLE_DRAKAN_VENATOR_2, NpcID.CASTLE_DRAKAN_VENATOR_3, NpcID.CASTLE_DRAKAN_VENATOR_4, NpcID.CASTLE_DRAKAN_VENATOR_5}, new WorldPoint(2522, 7368, 0), "Kill the Venator.\n\nProtect from ranged or melee depending on if you're in melee range or not.\n\nTurn off your protection prayer when it shrieks.");

		var ggkpLightFireplace = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LOWERNIEL_FIREPLACE, new WorldPoint(2521, 7371, 0), "Light the fireplace.", tinderbox);
		var ggkpSearchChest = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LOWERNIEL_CHEST, new WorldPoint(2526, 7371, 0), "Search the chest.");

		var ggkpTryOpenLockbox = new DetailedQuestStep(quest, "Open the small lockbox.", smallLockbox.highlighted());

		var ggkpSolveLockboxPuzzle = new ChestCodeStep(quest, "small lockbox", "⠿ ᴟ ⁘", 10, 2, 3, 7);
		ggkpSolveLockboxPuzzlePW = ggkpSolveLockboxPuzzle.puzzleWrapStepWithDefaultText("Solve the lockbox puzzle.");

		var ggkpPlaceFancyGemInHead = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_VENATOR_HEAD, new WorldPoint(2531, 7367, 0), "Place the fancy gem on the venator head.", fancyGem1.highlighted());
		ggkpPlaceFancyGemInHead.addIcon(ItemID.CASTLE_DRAKAN_FANCY_GEM_1);
		var ggkpSolveDoorPuzzle = new ChestCodeStep(quest, "door", "SPEAR", 10, 1, 1, 1, 3, 0);
		ggkpSolveDoorPuzzlePW = ggkpSolveDoorPuzzle.puzzleWrapStepWithDefaultText("Solve the door puzzle.");

		var ggkpEnterSouthWestDoor = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LOWERNIEL_LIBRARY_DOOR, new WorldPoint(2519, 7363, 0), "Enter the south-west door.");

		var ggkSolvePuzzle = new ConditionalStep(quest, ggkpLightFireplace, "Solve the room puzzle.", ornateSkull, ornateKnife);

		var ggkpSearchBookcaseForBook = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_BOOKCASE_4, new WorldPoint(2549, 7381, 0), "Search the south-western bookcase.");

		var ggkpOpenMysteriousBook = new DetailedQuestStep(quest, "Open the mysterious book.", mysteriousBook.highlighted());

		var ggkpLeaveLibrary = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, new WorldPoint(2551, 7387, 0), "Leave the library.", fancyGem2);

		var ggkpPlaceFancyGemInHead2 = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_VENATOR_HEAD, new WorldPoint(2531, 7367, 0), "Place the fancy gem on the venator head.", fancyGem2.highlighted());
		ggkpPlaceFancyGemInHead2.addIcon(ItemID.CASTLE_DRAKAN_FANCY_GEM_2);
		var ggkpSearchVenatorHead = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_VENATOR_HEAD, new WorldPoint(2531, 7367, 0), "Search the venator head for the ornate hourglass.");
		ggkpSearchVenatorHead.addDialogStep("Yes.");

		ggkSolvePuzzle.addStep(venatorHeadBothEyePlaced, ggkpSearchVenatorHead);
		ggkSolvePuzzle.addStep(and(fancyGem2, inVenatorPuzzleRoom), ggkpPlaceFancyGemInHead2);
		ggkSolvePuzzle.addStep(and(fancyGem2, inVenatorPuzzleRoomLibrary), ggkpLeaveLibrary);
		ggkSolvePuzzle.addStep(mysteriousBook, ggkpOpenMysteriousBook);
		ggkSolvePuzzle.addStep(and(doorPuzzleSolved, inVenatorPuzzleRoomLibrary), ggkpSearchBookcaseForBook);
		ggkSolvePuzzle.addStep(and(playerNextToDoorPuzzle, quest.combinationLockWidgetOpen), ggkpSolveDoorPuzzlePW);
		ggkSolvePuzzle.addStep(venatorHeadOneEyePlaced, ggkpEnterSouthWestDoor);
		ggkSolvePuzzle.addStep(fancyGem1, ggkpPlaceFancyGemInHead);
		ggkSolvePuzzle.addStep(and(smallLockbox, quest.combinationLockWidgetOpen), ggkpSolveLockboxPuzzlePW);
		ggkSolvePuzzle.addStep(smallLockbox, ggkpTryOpenLockbox);
		ggkSolvePuzzle.addStep(isFireplaceLit, ggkpSearchChest);

		// I placed the ornate quest.knife, then hourglass, then skull
		// Varbit went to 0b010 after placing quest.knife
		// Varbit went to 0b110 after placing hourglass
		// Varbit went to 0b111 after placing skull

		var ggkPlaceItemsOnDisplayCase = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_GALLERY_DISPLAY_CASE, new WorldPoint(2497, 7374, 0), "Place the items on the display case.", ornateHourglass.highlighted(), ornateKnife.highlighted(), ornateSkull.highlighted());

		var ggkSearchDisplayCaseForGildedKey = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_GALLERY_DISPLAY_CASE, new WorldPoint(2497, 7374, 0), "Search the display case for the gilded key.");
		ggkSearchDisplayCaseForGildedKey.addDialogStep("Yes.");

		var getGibbousMoonKey = castleAction(RoomKey.BASEMENT_PRISON, ggkTakeGibbousMoonKey,
			"Head to the basement prison to get the gibbous moon key.");

		var getOrnateKnife = castleAction(RoomKey.ORNATE_KNIFE_ROOM, ggkGetOrnateKnife,
			"Head to the upper ornate-knife room.");

		var activateStoreroomPortal = castleAction(RoomKey.ORNATE_KNIFE_ROOM, ggkPullLeverInOrnateKnifeRoom,
			"Return to the ornate-knife room and activate its portal.");

		var solveVenatorPuzzle = castleAction(RoomKey.VENATOR_PUZZLE_ROOM,
			or(inVenatorPuzzleRoom, inVenatorPuzzleRoomLibrary), ggkSolvePuzzle,
			"Head through the portal and into the venator-head puzzle room.");

		var placeDisplayItems = castleAction(RoomKey.DISPLAY_ROOM, ggkPlaceItemsOnDisplayCase,
			"Return to the display case room and place the skull, knife and hourglass.");
		var getGildedKey = castleAction(RoomKey.DISPLAY_ROOM, ggkSearchDisplayCaseForGildedKey,
			"Return to the display case room to get the gilded key.");

		// The key breaks in the lock, so these cannot test for it in the inventory.
		var hadTheGibbousMoonKey = or(gibbousMoonKey, usedUpGibbousMoonKey);
		var needsGibbousMoonKey = not(hadTheGibbousMoonKey);
		var needsOrnateKnife = and(hadTheGibbousMoonKey, not(pulledUpperStoreroomLever), not(gotOrnateKnife));
		var needsStoreroomPortal = and(hadTheGibbousMoonKey, gotOrnateKnife, not(pulledUpperStoreroomLever));
		var needsVenatorPuzzle = and(hadTheGibbousMoonKey, pulledUpperStoreroomLever, not(gotOrnateHourglass));
		var needsDisplayItems = and(hadTheGibbousMoonKey, holdingADisplayItem, not(allItemsPlacedInDisplayCase));
		var needsGildedKey = and(hadTheGibbousMoonKey, allItemsPlacedInDisplayCase);
		var missingTinderboxForFireplace = and(not(tinderbox), not(isFireplaceLit));
		var fetchTinderboxForFireplace = castleRecovery(RoomKey.STORAGE_ROOM, pickUpTinderbox,
			"Return to the storage room for a tinderbox to light the fireplace with.",
			missingTinderboxForFireplace);
		var needsATinderboxForTheFireplace = and(needsVenatorPuzzle, missingTinderboxForFireplace);

		cGetGildedAndGibbousKeys = new CastleDrakanGoalStep(quest, getGibbousMoonKey,
			"Get the gilded and gibbous keys.");
		cGetGildedAndGibbousKeys.addStep(and(needsGibbousMoonKey, quest.inCutscene), ggkWatchTheCutscene);
		cGetGildedAndGibbousKeys.addStep(needsGibbousMoonKey, getGibbousMoonKey);
		cGetGildedAndGibbousKeys.addStep(needsOrnateKnife, getOrnateKnife);
		cGetGildedAndGibbousKeys.addStep(needsStoreroomPortal, activateStoreroomPortal);
		cGetGildedAndGibbousKeys.addStep(and(needsVenatorPuzzle, inVenatorPuzzleRoom, anyVenatorAlive),
			ggkKillVenator);
		// Lighting the fireplace needs a tinderbox, which may have been banked since the clocks.
		// After the kill branch: being attacked comes first.
		cGetGildedAndGibbousKeys.addStep(needsATinderboxForTheFireplace, fetchTinderboxForFireplace);
		cGetGildedAndGibbousKeys.addStep(needsVenatorPuzzle, solveVenatorPuzzle);
		cGetGildedAndGibbousKeys.addStep(needsDisplayItems, placeDisplayItems);
		cGetGildedAndGibbousKeys.addStep(needsGildedKey, getGildedKey);
		cGetGildedAndGibbousKeys.orderSidebar(getGibbousMoonKey, ggkWatchTheCutscene, getOrnateKnife,
			activateStoreroomPortal, ggkKillVenator, fetchTinderboxForFireplace, solveVenatorPuzzle,
			placeDisplayItems, getGildedKey);
	}

	private void setupFullMoonKeySteps()
	{
		var fmkKillVenator = new NpcStep(quest, NpcID.CASTLE_DRAKAN_VENATOR, new WorldPoint(2456, 7392, 0), "Kill the Venator.\n\nProtect from ranged or melee depending on if you're in melee range or not.\n\nTurn off your protection prayer when it shrieks.");

		var fmkTalkToIvan1 = new NpcStep(quest, NpcID.MYQ6_IVAN_VIS, new WorldPoint(2455, 7388, 0), "Talk to Ivan Strom after killing the Venator.");

		var fmkGildedBookPuzzle = new GildedBookPuzzle(quest);
		fmkGildedBookPuzzlePW = fmkGildedBookPuzzle.puzzleWrapStepWithDefaultText("Rearrange the books to the correct order.");

		var fmkGetGildedBook = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_BOOKCASE_3, new WorldPoint(2455, 7395, 0), "Search the northern bookcase for the gilded book.");

		var fmkUseGildedBookOnWesternGildedBookcase = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_GILDED_BOOKCASE, new WorldPoint(2451, 7391, 0), "Use the gilded book on the western bookcase.", gildedBook.highlighted());
		fmkUseGildedBookOnWesternGildedBookcase.addIcon(ItemID.CASTLE_DRAKAN_GILDED_BOOK);
		var fmkClickGildedBookcase = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_GILDED_BOOKCASE, new WorldPoint(2451, 7391, 0), "Click the gilded bookcase to start rearranging the books.");
		fmkClickGildedBookcase.addDialogStep("Yes.");

		var fmkTakeFullMoonKey = new DetailedQuestStep(quest, new WorldPoint(2433, 7416, 0), "Take the full moon key from the table.", fullMoonKey.highlighted());

		var solveGildedBookRoom = new ConditionalStep(quest, fmkTalkToIvan1,
			"Help Ivan reveal the secret passage.");
		solveGildedBookRoom.addStep(fmkVenatorAlive, fmkKillVenator);
		solveGildedBookRoom.addStep(and(fmkIvanFollowingYou, startedLibraryPuzzle, gildedBookPuzzleOpen),
			fmkGildedBookPuzzlePW);
		solveGildedBookRoom.addStep(and(fmkIvanFollowingYou, startedLibraryPuzzle), fmkClickGildedBookcase);
		solveGildedBookRoom.addStep(and(fmkIvanFollowingYou, gildedBook),
			fmkUseGildedBookOnWesternGildedBookcase);
		solveGildedBookRoom.addStep(fmkIvanFollowingYou, fmkGetGildedBook);
		var revealSecretPassage = castleAction(RoomKey.IVAN_ROOM, solveGildedBookRoom,
			"Head to the gilded-bookcase room and help Ivan reveal the secret passage.");
		var takeFullMoonKey = castleAction(RoomKey.SECRET_ROOM, fmkTakeFullMoonKey,
			"Enter the revealed passage and take the full moon key.");

		cGetFullMoonKey = new CastleDrakanGoalStep(quest, takeFullMoonKey, "Get the full moon key.");
		cGetFullMoonKey.addStep(not(finishedLibraryPuzzle), revealSecretPassage);
		cGetFullMoonKey.orderSidebar(revealSecretPassage, takeFullMoonKey);
	}

	private void setupSolidKeySteps()
	{
		// TODO: Should this use a puzzle wrapper step? Probably, but it will require re-organizing these steps into a conditional step which is _scary_
		// requires 4 inv slots
		var skGetCloudyGreyPotion = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_SHELVES, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetCloudyGreyPotion.addDialogStep("Take a cloudy grey potion.");
		var skGetWeightlessBlackPotion = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_SHELVES, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetWeightlessBlackPotion.addDialogStep("Take a weightless black potion.");
		var skGetThickRedPotion = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_SHELVES, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetThickRedPotion.addDialogStep("Take a thick red potion.");
		var skGetColdBluishWhitePotion = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_SHELVES, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetColdBluishWhitePotion.addDialogStep("Take a cold bluish-white potion.");
		skGetCloudyGreyPotion.addSubSteps(skGetWeightlessBlackPotion, skGetThickRedPotion, skGetColdBluishWhitePotion);

		var smokeBasin = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_SMOKE_BASIN, new WorldPoint(2380, 7385, 0), "Pour the cloudy grey potion into the south-west basin.", cloudyGreyPotion.highlighted());
		smokeBasin.addIcon(ItemID.CASTLE_DRAKAN_CLOUDY_GREY_POTION);
		var iceBasin = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_ICE_BASIN, new WorldPoint(2387, 7384, 0), "Pour the cold bluish-white potion into the south-east basin.", coldBlueishWhitePotion.highlighted());
		iceBasin.addIcon(ItemID.CASTLE_DRAKAN_COLD_WHITE_POTION);
		var shadowBasin = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_SHADOW_BASIN, new WorldPoint(2387, 7389, 0), "Pour the weightless black potion into the north-east basin.", weightlessBlackPotion.highlighted());
		shadowBasin.addIcon(ItemID.CASTLE_DRAKAN_WEIGHTLESS_BLACK_POTION);
		var bloodBasin = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_BLOOD_BASIN, new WorldPoint(2380, 7389, 0), "Pour the thick red potion into the north-west basin.", thickRedPotion.highlighted());
		bloodBasin.addIcon(ItemID.CASTLE_DRAKAN_THICK_RED_POTION);

		var skSearchAltarForAncientSymbol = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_CHAPEL_ALTAR, new WorldPoint(2382, 7393, 0), "Search the altar for an ancient symbol.");

		var skPullLever = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_TELEPORT_LEVER_CHAPEL_LIBRARY, new WorldPoint(2393, 7372, 0), "Pull the lever.");

		var skGetAncientShield = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SEARCHABLE_CRATE, new WorldPoint(2439, 7378, 0), "Search the crate to the north for the ancient shield.");

		var skCombineAncientShieldAndAncientSymbol = new DetailedQuestStep(quest, "Combine the ancient shield and ancient symbol.", ancientShield.highlighted(), ancientSymbol.highlighted());

		var skUseShieldOnEmptyMount = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SHIELD_MOUNT, new WorldPoint(2543, 7361, 0), "Use the shield with symbol on the empty mount on the southern wall.", shieldWithSymbol.highlighted());
		skUseShieldOnEmptyMount.addIcon(ItemID.CASTLE_DRAKAN_SHIELD_WITH_SYMBOL);

		var skGetSolidKey = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_SHIELD_MOUNT, new WorldPoint(2543, 7361, 0), "Search the mounted shield for the solid key.");
		skGetSolidKey.addDialogStep("Yes.");

		var solveBottleRoomPuzzle = new ConditionalStep(quest, skGetColdBluishWhitePotion,
			"Solve the four-basin puzzle in the bottle room.");
		solveBottleRoomPuzzle.addStep(and(solvedBloodBasin, solvedSmokeBasin, solvedIceBasin,
			not(solvedShadowBasin), weightlessBlackPotion), shadowBasin);
		solveBottleRoomPuzzle.addStep(and(solvedBloodBasin, solvedSmokeBasin, solvedIceBasin,
			not(solvedShadowBasin)), skGetWeightlessBlackPotion);
		solveBottleRoomPuzzle.addStep(and(solvedBloodBasin, solvedSmokeBasin,
			not(solvedIceBasin), coldBlueishWhitePotion), iceBasin);
		solveBottleRoomPuzzle.addStep(and(solvedBloodBasin, solvedSmokeBasin,
			not(solvedIceBasin)), skGetColdBluishWhitePotion);
		solveBottleRoomPuzzle.addStep(and(solvedBloodBasin, not(solvedSmokeBasin), cloudyGreyPotion), smokeBasin);
		solveBottleRoomPuzzle.addStep(and(solvedBloodBasin, not(solvedSmokeBasin)), skGetCloudyGreyPotion);
		solveBottleRoomPuzzle.addStep(and(not(solvedBloodBasin), thickRedPotion,
			or(cloudyGreyPotion, solvedSmokeBasin), or(weightlessBlackPotion, solvedShadowBasin),
			or(coldBlueishWhitePotion, solvedIceBasin)), bloodBasin);
		solveBottleRoomPuzzle.addStep(not(cloudyGreyPotion), skGetCloudyGreyPotion);
		solveBottleRoomPuzzle.addStep(not(weightlessBlackPotion), skGetWeightlessBlackPotion);
		solveBottleRoomPuzzle.addStep(not(thickRedPotion), skGetThickRedPotion);
		solveBottleRoomPuzzle.addStep(not(coldBlueishWhitePotion), skGetColdBluishWhitePotion);

		var obtainAncientSymbol = new ConditionalStep(quest, solveBottleRoomPuzzle,
			"Solve the four-basin puzzle and obtain the ancient symbol.");
		obtainAncientSymbol.addStep(solvedAllBasins, skSearchAltarForAncientSymbol);
		var getAncientSymbol = castleAction(RoomKey.BOTTLE_ROOM, obtainAncientSymbol,
			"Head to the bottle room to obtain the ancient symbol.");
		var activateChapelPortal = castleAction(RoomKey.CHAPEL_LIBRARY, skPullLever,
			"Head through the south-east door to the chapel library and activate its portal.");
		var getAncientShield = castleAction(RoomKey.ROOM_ABOVE_STUDY, skGetAncientShield,
			"Head to the room above the study to get the ancient shield.");
		var mountShield = castleAction(RoomKey.SOLID_KEY_ROOM, skUseShieldOnEmptyMount,
			"Head to the solid-key room with the shield and symbol.");
		var getSolidKey = castleAction(RoomKey.SOLID_KEY_ROOM, skGetSolidKey,
			"Return to the solid-key room and search the mounted shield.");

		cGetSolidKey = new CastleDrakanGoalStep(quest, getAncientSymbol, "Get the solid key.");
		cGetSolidKey.addStep(hasMountedShield, getSolidKey);
		cGetSolidKey.addStep(shieldWithSymbol, mountShield);
		cGetSolidKey.addStep(and(ancientShield, ancientSymbol), skCombineAncientShieldAndAncientSymbol);
		cGetSolidKey.addStep(and(ancientSymbol, not(openedPortalFromChapelLibraryToServantsQuarters)),
			activateChapelPortal);
		cGetSolidKey.addStep(ancientSymbol, getAncientShield);
		cGetSolidKey.addStep(not(ancientSymbol), getAncientSymbol);
		cGetSolidKey.orderSidebar(getAncientSymbol, activateChapelPortal, getAncientShield,
			skCombineAncientShieldAndAncientSymbol, mountShield, getSolidKey);
	}

	private void setupBloodStockpileSteps()
	{
		// if you follow the guide, this is the last position you'll use the key

		var dtsSearchShelvesForSupplies = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_SHELVES, new WorldPoint(2513, 7387, 0), "Search the shelves for 2 vials of water, 3 vials of blood, and 3 pure essence. You can destroy all keys, the tinderbox, and the pickaxe to make room in your inventory.", vialsOfWater2, vialsOfBlood3, pureEssence3);
		dtsSearchShelvesForSuppliesWater = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesWater.addDialogStep("Take a vial of water.");
		dtsSearchShelvesForSuppliesBlood = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesBlood.addDialogStep("Take a vial of blood.");
		dtsSearchShelvesForSuppliesEssence = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesEssence.addDialogStep("Take a piece of pure essence.");

		var makeFirstVial = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", vialOfBlood.highlighted(), pureEssence.highlighted());
		var makeFirstVialEssence = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", pureEssence.highlighted());
		var makeFirstVialBlood = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", vialOfBlood.highlighted());

		var makeSecondVial = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", vialOfBlood.highlighted(), pureEssence.highlighted());
		var makeSecondVialEssence = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", pureEssence.highlighted());
		var makeSecondVialBlood = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", vialOfBlood.highlighted());

		var makeThirdVial = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfBlood.highlighted(), vialOfWater.highlighted());
		var makeThirdVialBlood = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfBlood.highlighted());
		var makeThirdVialWater = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfWater.highlighted());

		var makeFourthVial = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", pureEssence.highlighted(), vialOfWater.highlighted());
		var makeFourthVialEssence = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", pureEssence.highlighted());
		var makeFourthVialWater = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_REFINER, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", vialOfWater.highlighted());

		var pourAllVialsIntoTheBasin = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_LAB_BASIN, new WorldPoint(2518, 7389, 0), "Pour all the chemical vials into the basin.", chemicalVial.highlighted());

		var cDestroyingTheStockpileLaboratoryStep = new ConditionalStep(quest, dtsSearchShelvesForSupplies, "Fill the basin to the north until it reads 53. If something has gone wrong, overfill the basin and start over.");
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial, hasPouredAnything), pourAllVialsIntoTheBasin);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial4), pourAllVialsIntoTheBasin);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial3, refinerWater), makeFourthVialEssence);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial3, refinerEssence), makeFourthVialWater);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial3), makeFourthVial);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial2, refinerWater), makeThirdVialBlood);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial2, refinerBlood), makeThirdVialWater);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial2), makeThirdVial);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial, refinerBlood), makeSecondVialEssence);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial, refinerEssence), makeSecondVialBlood);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(chemicalVial), makeSecondVial);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(refinerBlood), makeFirstVialEssence);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(refinerEssence), makeFirstVialBlood);
		cDestroyingTheStockpileLaboratoryStep.addStep(and(vialsOfWater2, vialsOfBlood3, pureEssence3), makeFirstVial);
		cDestroyingTheStockpileLaboratoryStep.addStep(not(vialsOfWater2), dtsSearchShelvesForSuppliesWater);
		cDestroyingTheStockpileLaboratoryStep.addStep(not(vialsOfBlood3), dtsSearchShelvesForSuppliesBlood);
		cDestroyingTheStockpileLaboratoryStep.addStep(not(pureEssence3), dtsSearchShelvesForSuppliesEssence);

		cDestroyingTheStockpileLaboratoryStepPW = cDestroyingTheStockpileLaboratoryStep.puzzleWrapStepWithDefaultText("Solve the puzzle in this room.");

		var dtsDestroyBloodStockpile = new ObjectStep(quest, ObjectID.CASTLE_DRAKAN_BLOOD_STOCKPILE, new WorldPoint(2535, 7385, 0), "Destroy the blood stockpile.");
		dtsWatchTheCutscene = dtsDestroyBloodStockpile.cutscene();

		var solveLaboratory = castleAction(RoomKey.LABORATORY, cDestroyingTheStockpileLaboratoryStepPW,
			"Head through the solid door to the laboratory and solve its mixture puzzle.");
		var destroyStockpile = castleAction(RoomKey.LABORATORY_STORAGE, dtsDestroyBloodStockpile,
			"Enter the unlocked storage room and destroy the blood stockpile.");
		var cDestroyingTheStockpile = new ConditionalStep(quest, solveLaboratory, "Find and destroy the stockpile.");
		cDestroyingTheStockpile.addStep(quest.inCutscene, dtsWatchTheCutscene);
		cDestroyingTheStockpile.addStep(finishedLabPuzzle, destroyStockpile);

		/// 80
		var tryToLeave = new ObjectStep(quest, ObjectID.MYQ6_VAMPYRIUM_RETURN_PORTAL, new WorldPoint(2323, 7370, 0),
			"Try to leave through the portal in the ground-floor lobby.");
		var vampyriumCastleDrakanDestroyedBloodStockpile = new DetailedQuestStep(quest, "Watch the cutscene.");
		var cVampyriumCastleDrakanDestroyedBloodStockpile = new ConditionalStep(quest, tryToLeave, "Try to leave Castle Drakan through the portal.");
		cVampyriumCastleDrakanDestroyedBloodStockpile.addStep(quest.inCutscene, vampyriumCastleDrakanDestroyedBloodStockpile);

		cDestroyingTheStockpileGoal = new CastleDrakanGoalStep(quest, cDestroyingTheStockpile,
			"Reach the laboratory, destroy the blood stockpile, then return to the portal.");
		cDestroyingTheStockpileGoal.addStep(new VarbitRequirement(VarbitID.MYQ6, 80), cVampyriumCastleDrakanDestroyedBloodStockpile);
		cDestroyingTheStockpileGoal.orderSidebar(solveLaboratory, destroyStockpile, dtsWatchTheCutscene,
			cVampyriumCastleDrakanDestroyedBloodStockpile);
	}

	private void setupRoomNetwork()
	{
		var lobbyF0 = room(RoomKey.LOBBY_F0,
			"ground-floor lobby", inLobbyF0);
		var diningF0 = room(RoomKey.DINING_ROOM,
			"ground-floor dining room", inDiningRoomF0);
		var throneF0 = room(RoomKey.THRONE_ROOM,
			"ground-floor throne room", inThroneRoomF0);
		var southOfThrone = room(RoomKey.ROOM_SOUTH_OF_THRONE,
			"room south of the throne room", inRoomSouthOfThroneRoom);
		var storage = room(RoomKey.STORAGE_ROOM,
			"storage room", inStorageRoom);
		var study = room(RoomKey.STUDY, "study", inStudy);
		var westDiningHall = room(RoomKey.WEST_DINING_HALLWAY,
			"hallway west of the dining room", inHallwayWestOfDiningRoom);
		var larder = room(RoomKey.LARDER,
			"larder", inLarder);
		var kitchen = room(RoomKey.KITCHEN,
			"kitchen", inKitchen);
		var galleryHall = room(RoomKey.EMBLEM_GALLERY_HALLWAY,
			"emblem gallery hallway", inEmblemGalleryHallway);
		var gallery = room(RoomKey.EMBLEM_GALLERY,
			"emblem gallery", inEmblemGallery);
		var westChapelHall = room(RoomKey.WEST_CHAPEL_HALLWAY,
			"west chapel hallway", inWestChapelHallway);
		var bottleRoom = room(RoomKey.BOTTLE_ROOM,
			"bottle room", inBottleRoom);
		var chapelLibrary = room(RoomKey.CHAPEL_LIBRARY,
			"chapel library", inChapelLibrary);
		var northChapelHall = room(RoomKey.NORTH_CHAPEL_HALLWAY,
			"north chapel hallway", inNorthChapelHallway);
		var bedroomAboveThrone = room(RoomKey.BEDROOM_ABOVE_THRONE_ROOM,
			"bedroom above the throne room", inBedroomAboveThroneRoom);
		var eastStaircaseF1 = room(RoomKey.FIRST_FLOOR_EAST_STAIRCASE,
			"first-floor east staircase", inFirstFloorEastStaircase);
		var eastStaircaseF0 = room(RoomKey.GROUND_FLOOR_EAST_STAIRCASE,
			"ground-floor east staircase", inGroundFloorEastStaircase);
		var throneStorage = room(RoomKey.THRONE_ROOM_STORAGE_ROOM,
			"throne room storage room", inThroneRoomStorageRoom);
		var crescentDoorRoom = room(RoomKey.CRESCENT_DOOR_ROOM,
			"crescent door room", inCrescentDoorRoom);
		var solidDoorStore = room(RoomKey.SOLID_DOOR_STORE_ROOM,
			"solid-door store room", inSolidDoorStoreRoom);
		var basementStore = room(RoomKey.BASEMENT_STORE_ROOM,
			"basement store room", inBasementStoreRoom);
		var hall5 = room(RoomKey.UPPER_SOUTHERN_HALLWAY,
			"upper southern hallway", inHallway5);
		var roomAboveStudy = room(RoomKey.ROOM_ABOVE_STUDY,
			"room above the study", inRoomAboveStudy);
		var ornateKnifeRoom = room(RoomKey.ORNATE_KNIFE_ROOM,
			"ornate knife room", inOrnateKnifeRoom);
		var vanesculaStudy = room(RoomKey.VANESCULAS_STUDY,
			"Vanescula's study", inVanesculasStudy);
		var vanesculaChamber = room(RoomKey.VANESCULAS_CHAMBER,
			"Vanescula's chamber", inVanesculasChamber);
		var emblemRoom = room(RoomKey.FIRST_EMBLEM_ROOM,
			"first emblem room", inDrakanEmblemRoomSouthOfExplosiveRoom);
		var ranisParlour = room(RoomKey.RANIS_PARLOUR,
			"Ranis' parlour", inRanisParlour);
		var explosiveRoom = room(RoomKey.EXPLOSIVE_ROOM,
			"explosive barrel room", inExplosiveRoom);
		var ivanRoom = room(RoomKey.IVAN_ROOM,
			"gilded bookcase room", inRoomWithIvanAndVenator);
		var servantsQuarters = room(RoomKey.SERVANTS_QUARTERS,
			"servants' quarters", inServantsQuarters);
		var explosiveHall = room(RoomKey.EXPLOSIVE_HALLWAY,
			"hallway east of the explosive room", inHallwayEastOfExplosiveRoom);
		var secretRoom = room(RoomKey.SECRET_ROOM,
			"secret full-moon-key room", inSecretRoom);
		var smallHall = room(RoomKey.SMALL_HALLWAY,
			"small gilded-door hallway", inSmallHallway);
		var hallNorthLobby = room(RoomKey.HALLWAY_NORTH_OF_LOBBY,
			"hallway north of the first-floor lobby", inHallwayNorthOfLobby);
		var vanesculaHall = room(RoomKey.VANESCULAS_HALLWAY,
			"Vanescula's hallway", inVanesculasHallway);
		var ranisHall = room(RoomKey.RANIS_HALLWAY,
			"Ranis' hallway", inRanisHallway);
		var venatorRoom = room(RoomKey.VANESCULAS_VENATOR_ROOM,
			"Vanescula's venator room", inVenatorRoom);
		var displayRoom = room(RoomKey.DISPLAY_ROOM,
			"display-case room", inRoomOutsideGuestChamberStoreroom);
		var venatorPuzzle = room(RoomKey.VENATOR_PUZZLE_ROOM,
			"venator-head puzzle room", inVenatorPuzzleRoom);
		var solidKeyRoom = room(RoomKey.SOLID_KEY_ROOM,
			"solid-key room", inSolidKeyRoom);
		var laboratory = room(RoomKey.LABORATORY,
			"stockpile laboratory", inLaboratory);
		var laboratoryStorage = room(RoomKey.LABORATORY_STORAGE,
			"blood stockpile room", inLaboratoryStorageRoom);
		var puzzleLibrary = room(RoomKey.VENATOR_PUZZLE_LIBRARY,
			"venator puzzle library", inVenatorPuzzleRoomLibrary);
		var solidDoorHall = room(RoomKey.SOLID_DOOR_HALLWAY,
			"solid-door hallway", inSolidDoorHallway);
		var guestStorage = room(RoomKey.GUEST_CHAMBER_STOREROOM,
			"guest chamber storeroom", inGuestChamberStoreroom);
		var basementHall = room(RoomKey.BASEMENT_HALLWAY,
			"lobby basement hallway", inLobbyBasementHallway);
		var basementVenator = room(RoomKey.BASEMENT_VENATOR_ROOM,
			"basement weapons room", inLobbyBasementVenator);
		var basementPrison = room(RoomKey.BASEMENT_PRISON,
			"basement prison", inBasementPrison);
		var lobbyF1 = room(RoomKey.LOBBY_F1,
			"first-floor lobby", inLobbyF1);
		var diningF1 = room(RoomKey.DINING_F1,
			"first-floor dining room", inDiningRoomF1);
		var throneF1 = room(RoomKey.THRONE_F1,
			"first-floor throne room", inThroneRoomF1);

		connect(lobbyF0, diningF0, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2327, 7360, 0,
				"Enter the dining room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2342, 7373, 0,
				"Enter the ground-floor lobby."));

		connect(diningF0, throneF0, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2358, 7366, 0,
				"Enter the throne room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2304, 7391, 0,
				"Enter the dining room."));

		connect(throneF0, southOfThrone, or(halfMoonKey, openedThroneHalfMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_HALF_M, 2310, 7386, 0,
				"Enter the room south of the throne room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_HALF_M, 2327, 7391, 0,
				"Enter the throne room."));

		connect(throneF0, study, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2309, 7397, 0,
				"Enter the study north of the throne room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2358, 7380, 0,
				"Enter the throne room."));

		connect(diningF0, westDiningHall, or(halfMoonKey, openedDiningHalfMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_HALF, 2336, 7370, 0,
				"Enter the hallway west of the dining room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_HALF_M, 2339, 7395, 0,
				"Go back through the half moon door to the ground-floor dining room."));

		connect(westDiningHall, storage, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2323, 7395, 0,
				"Enter the western room, avoiding the floor traps."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2348, 7383, 0,
				"Go back through the door to the hallway west of the dining room."),
			WEST_DINING_HALLWAY_TRAPS);

		connect(storage, explosiveRoom, null,
			door(ObjectID.CASTLE_DRAKAN_SPIRAL_STAIRS_UP, 2340, 7384, 0,
				"Climb up the stairs."),
			door(ObjectID.CASTLE_DRAKAN_SPIRAL_STAIRS_DOWN, 2435, 7386, 0,
				"Climb down the spiral staircase to the storage room."));

		connect(explosiveRoom, emblemRoom, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2439, 7384, 0,
				"Enter the room south of the explosive-barrel room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2455, 7381, 0,
				"Return to the explosive-barrel room."));

		connect(explosiveRoom, explosiveHall, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2444, 7386, 0,
				"Enter the eastern door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2467, 7395, 0,
				"Go through the west door to the explosive barrel room."));
		// TODO: EXPLOSIVE_HALLWAY_TRAPS?

		connect(lobbyF0, lobbyF1, null,
			door(ObjectID.CASTLE_DRAKAN_STAIRS_UP, 2318, 7371, 0,
				"Climb up to the first-floor lobby."),
			door(ObjectID.CASTLE_DRAKAN_STAIRS_DOWN_INVISIBLE, 2315, 7371, 1,
				"Climb down to the ground-floor lobby."));

		connect(lobbyF1, hallNorthLobby, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2314, 7382, 1,
				"Enter the northern room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2457, 7417, 0,
				"Return to the first-floor lobby."));

		connect(hallNorthLobby, vanesculaHall, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2466, 7422, 0,
				"Enter the scratched door on the north wall."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2452, 7403, 0,
				"Return through the western hallway, avoiding the floor traps."),
			VANESCULAS_HALLWAY_TRAPS);

		connect(vanesculaHall, vanesculaStudy, placedEmblemInVanesculasHallway,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2468, 7407, 0,
				"Enter Vanescula's study."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2474, 7372, 0,
				"Leave Vanescula's study."),
			VANESCULAS_HALLWAY_TRAPS);

		connect(vanesculaStudy, vanesculaChamber, placedEmblemInVanesculasStudy,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2477, 7366, 0,
				"Enter Vanescula's chamber."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2483, 7368, 0,
				"Leave Vanescula's chamber."));

		connect(vanesculaChamber, venatorRoom, vanesculasChamberWallDestroyed,
			door(ObjectID.CASTLE_DRAKAN_WALL_DESTROYED, 2492, 7364, 0,
				"Enter the hole in the wall."),
			door(ObjectID.CASTLE_DRAKAN_WALL_DESTROYED, 2482, 7412, 0,
				"Leave through the hole in the wall."));

		connect(vanesculaHall, ranisHall, anyEmblemInVanesculasHallwayEast,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2477, 7409, 0,
				"Enter the eastern door, avoiding the floor traps."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2481, 7403, 0,
				"Go back through the door to Vanescula's hallway."),
			VANESCULAS_HALLWAY_TRAPS);

		connect(ranisHall, ranisParlour, anyEmblemInRanisHallwayNorth,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2485, 7405, 0,
				"Enter Ranis' parlour."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2475, 7379, 0,
				"Leave Ranis' parlour."));

		connect(ranisHall, northChapelHall, null,
			door(ObjectID.CASTLE_DRAKAN_SPIRAL_STAIRS_DOWN, 2491, 7402, 0,
				"Climb down the stairs."),
			door(ObjectID.CASTLE_DRAKAN_SPIRAL_STAIRS_UP, 2385, 7403, 0,
				"Climb up the spiral staircase to Ranis' hallway."));

		connect(northChapelHall, westChapelHall, anyEmblemInNorthChapelHallway,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2372, 7409, 0,
				"Enter the western door, avoiding the floor traps."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2371, 7396, 0,
				"Go back through the door to the north chapel hallway."),
			NORTH_CHAPEL_HALLWAY_TRAPS);

		connect(westChapelHall, gallery, anyEmblemInWestChapelHallway,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2369, 7384, 0,
				"Enter the emblem gallery."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2388, 7366, 0, "Go back through the door to the west chapel hallway."));

		connect(gallery, galleryHall, or(crescentMoonKey, openedGalleryCrescentDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT_M, 2370, 7370, 0,
				"Leave the emblem gallery through the western door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT, 2343, 7416, 0, "Go back through the crescent moon door to the emblem gallery."));

		connect(galleryHall, lobbyF0, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2330, 7407, 0,
				"Enter the ground-floor lobby."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2327, 7382, 0,
				"Go back through the door to the emblem gallery hallway."));

		connect(galleryHall, kitchen, or(crescentMoonKey, openedKitchenCrescentDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT_M, 2325, 7403, 0,
				"Enter the south-west kitchen."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT, 2314, 7421, 0,
				"Leave the kitchen."));

		connect(kitchen, larder, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2309, 7421, 0,
				"Enter the larder through the north-west door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2361, 7393, 0,
				"Return to the kitchen."));

		// TODO: highlight both stairs?
		connect(lobbyF0, basementHall, null,
			door(ObjectID.CASTLE_DRAKAN_STAIRS_DOWN, 2311, 7366, 0,
				"Climb down to the basement."),
			door(ObjectID.CASTLE_DRAKAN_STAIRS_UP_NORUG, 2564, 7369, 0,
				"Climb up to the ground-floor lobby."));

		connect(basementHall, basementVenator, or(crescentMoonKey, openedBasementCrescentDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT_M, 2570, 7365, 0,
				"Enter the eastern crescent moon door, ready to fight a Venator."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT, 2566, 7387, 0,
				"Leave the basement weapons room."));

		connect(basementHall, basementPrison, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_NEW, 2570, 7369, 0,
				"Enter the northern new moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_NEW_M, 2566, 7394, 0,
				"Leave the prison."));

		connect(lobbyF1, diningF1, or(gibbousMoonKey, openedDiningGibbousDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS, 2327, 7360, 1,
				"Enter the south-east gibbous moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS, 2342, 7373, 1,
				"Return to the first-floor lobby."));

		connect(diningF1, throneF1, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2358, 7366, 1,
				"Enter the eastern door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2304, 7392, 1,
				"Return to the first-floor dining room."));

		connect(throneF1, hall5, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2306, 7386, 1,
				"Enter the southern door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2434, 7365, 0,
				"Go back through the door to the first-floor throne room."));

		connect(hall5, bedroomAboveThrone, or(fullMoonKey, openedBedroomFullMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL, 2436, 7361, 0,
				"Enter the full moon door to the bedroom above the throne room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL_M, 2452, 7365, 0,
				"Go back through the full moon door to the upper southern hallway."));

		connect(hall5, eastStaircaseF1, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2446, 7364, 0,
				"Enter the door to the first-floor east staircase."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2457, 7362, 0,
				"Go back through the door to the upper southern hallway."));

		connect(eastStaircaseF1, eastStaircaseF0, null,
			door(ObjectID.CASTLE_DRAKAN_SPIRAL_STAIRS_DOWN, 2359, 7371, 0,
				"Climb down the east staircase."),
			door(ObjectID.CASTLE_DRAKAN_SPIRAL_STAIRS_UP, 2363, 7369, 0,
				"Climb up the east staircase."));

		connect(eastStaircaseF0, throneStorage, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2363, 7362, 0,
				"Enter the door to the throne room storage room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2349, 7399, 0,
				"Go back through the door to the ground-floor east staircase."));

		connect(throneStorage, southOfThrone, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2344, 7395, 0,
				"Enter the door to the room south of the throne room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2333, 7388, 0,
				"Enter the door to the throne room storage room."));

		connect(hallNorthLobby, crescentDoorRoom, or(crescentMoonKey, openedCrescentDoorRoomDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT_M, 2471, 7422, 0,
				"Enter the crescent moon door to the crescent door room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_CRESCENT, 2492, 7377, 0,
				"Go back through the crescent moon door to the hallway north of the lobby."));

		connect(solidDoorHall, solidDoorStore, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT, 2507, 7409, 0,
				"Enter the door to the solid-door store room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2553, 7364, 0,
				"Go back through the door to the solid-door hallway."));

		connect(basementHall, basementStore, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2585, 7370, 0,
				"Enter the door to the basement store room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2588, 7380, 0,
				"Go back through the door to the lobby basement hallway."));

		connect(hall5, ornateKnifeRoom, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2442, 7361, 0,
				"Enter the south-east door, avoiding the floor traps."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2452, 7373, 0,
				"Go back through the door to the upper southern hallway."),
			UPPER_SOUTHERN_HALLWAY_TRAPS);

		connect(ornateKnifeRoom, guestStorage, pulledUpperStoreroomLever,
			door(ObjectID.CASTLE_DRAKAN_TELEPORTER_GUEST_CHAMBER_STOREROOM_TO_UPPER_STOREROOM, 2450, 7372, 0,
				"Enter the portal to the guest chamber storeroom."),
			door(ObjectID.CASTLE_DRAKAN_TELEPORTER_UPPER_STOREROOM_TO_GUEST_CHAMBER_STOREROOM, 2515, 7396, 0,
				"Enter the portal back to the ornate knife room."));

		connect(guestStorage, displayRoom, or(gibbousMoonKey, openedGuestStoreroomGibbousDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS_M, 2522, 7397, 0,
				"Leave the guest chamber storeroom."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS, 2506, 7363, 0,
				"Go back through the gibbous moon door to the guest chamber storeroom."));

		connect(diningF1, explosiveHall, or(gibbousMoonKey, openedExplosiveHallwayGibbousDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS, 2336, 7370, 1,
				"Enter the gibbous moon door to the hallway east of the explosive room."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS_M, 2483, 7395, 0,
				"Go back through the gibbous moon door to the first-floor dining room, avoiding the traps."),
			UNNAMED_HALLWAY_TRAPS
		);

		connect(displayRoom, venatorPuzzle, or(gibbousMoonKey, openedVenatorPuzzleGibbousDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS, 2510, 7370, 0,
				"Enter the gibbous moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_GIBBOUS_M, 2515, 7367, 0,
				"Leave via the western gibbous moon door."));

		connect(venatorPuzzle, puzzleLibrary, null,
			door(ObjectID.CASTLE_DRAKAN_LOWERNIEL_LIBRARY_DOOR, 2519, 7363, 0,
				"Go to the venator puzzle library."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_ALT_M, 2551, 7387, 0,
				"Go to the venator-head puzzle room."));

		connect(displayRoom, lobbyF1, null,
			door(ObjectID.CASTLE_DRAKAN_STAIRS_DOWN_INVISIBLE_1X3, 2503, 7369, 0,
				"Climb down to the first-floor lobby."),
			door(ObjectID.CASTLE_DRAKAN_STAIRS_UP, 2308, 7365, 1,
				"Climb up to the display-case room."));

		connect(hallNorthLobby, smallHall, or(gildedKey, hasUsedGildedKey),
			door(ObjectID.CASTLE_DRAKAN_GILDED_LIBRARY_DOOR_M, 2452, 7420, 0,
				"Enter the western gilded door."),
			door(ObjectID.CASTLE_DRAKAN_GILDED_LIBRARY_DOOR, 2445, 7418, 0,
				"Leave through the gilded door."));

		connect(smallHall, ivanRoom, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2441, 7417, 0,
				"Enter the north-western door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2460, 7390, 0, "Go back through the door to the small gilded-door hallway."));

		connect(ivanRoom, secretRoom, finishedLibraryPuzzle,
			door(ObjectID.CASTLE_DRAKAN_LIBRARY_PASSAGE, 2455, 7386, 0,
				"Enter the secret passage."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2438, 7419, 0,
				"Leave the secret room through the eastern door."));

		connect(westChapelHall, bottleRoom, or(fullMoonKey, openedBottleRoomFullMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL_M, 2373, 7392, 0,
				"Enter the eastern full moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL_M, 2379, 7392, 0, "Go back through the full moon door to the west chapel hallway."));

		connect(bottleRoom, chapelLibrary, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL, 2388, 7383, 0,
				"Enter the south-east door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL_M, 2392, 7363, 0,
				"Go back through the full moon door to the bottle room."));

		connect(chapelLibrary, servantsQuarters, openedPortalFromChapelLibraryToServantsQuarters,
			door(ObjectID.CASTLE_DRAKAN_TELEPORTER_CHAPEL_LIBRARY_TO_SERVANTS_QUARTERS, 2393, 7370, 0,
				"Enter the portal to the servants' quarters."),
			door(ObjectID.CASTLE_DRAKAN_TELEPORTER_SERVANTS_QUARTERS_TO_CHAPEL_LIBRARY, 2440, 7400, 0,
				"Enter the portal back to the chapel library."));

		connect(servantsQuarters, explosiveRoom, or(newMoonKey, openedServantsQuarterNewMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_NEW_M, 2438, 7394, 0,
				"Leave the servants' quarters."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_NEW, 2441, 7389, 0,
				"Go back through the new moon door to the servants' quarters."));

		connect(throneF1, roomAboveStudy, or(fullMoonKey, openedRoomAboveStudyFullMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL, 2306, 7397, 1,
				"Enter the northern full moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL_M, 2434, 7372, 0,
				"Leave the room above the study."));

		connect(venatorPuzzle, solidKeyRoom, or(fullMoonKey, openedSolidKeyRoomFullMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL, 2525, 7363, 0,
				"Enter the south-eastern full moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL, 2541, 7374, 0,
				"Leave through the northern full moon door."));

		connect(displayRoom, solidDoorHall, or(fullMoonKey, openedStockpileFullMoonDoor),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL, 2510, 7379, 0,
				"Enter the north-eastern full moon door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_MOON_FULL_M, 2499, 7400, 0,
				"Go back through the full moon door to the display-case room."));

		// TODO: Is there a return?
		connect(solidDoorHall, laboratory, or(solidKey, usedSolidKey),
			door(ObjectID.CASTLE_DRAKAN_DOOR_SOLID_M, 2503, 7400, 0,
				"Enter the eastern solid door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_SOLID, 2512, 7382, 0,
				"Go back through the solid door to the solid-door hallway."));

		connect(laboratory, laboratoryStorage, finishedLabPuzzle,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2523, 7389, 0,
				"Enter the now-unlocked north-eastern door."),
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2535, 7377, 0,
				"Go back through the door to the stockpile laboratory."));

		connect(explosiveHall, lobbyF1, null,
			door(ObjectID.CASTLE_DRAKAN_DOOR, 2474, 7398, 0,
				"Enter the northern door, avoiding the floor traps."),
			door(ObjectID.CASTLE_DRAKAN_DOOR_M, 2314, 7360, 1,
				"Enter the south-west door."));
		// TODO: add trap highlights
	}

	/**
	 * All three emblem doors have to stand open at once, so one emblem is needed per door still
	 * shut. The Ranis hallway emblem counts as supply, since it is taken back once the skull is out.
	 */
	private Requirement carryingFewerEmblemsThanTheRemainingDoorsNeed()
	{
		var spare = anyEmblemInRanisHallwayNorth;
		var shut = new Requirement[]{
			not(anyEmblemInVanesculasHallwayEast),
			not(anyEmblemInNorthChapelHallway),
			not(anyEmblemInWestChapelHallway)};

		var canSupplyOne = or(anyOneEmblem, spare);
		var canSupplyTwo = or(anyOneEmblem.quantity(2), and(anyOneEmblem, spare));
		var canSupplyThree = or(anyOneEmblem.quantity(3), and(anyOneEmblem.quantity(2), spare));

		return or(
			and(new Conditions(Operation.GREATER_EQUAL, 1, shut), not(canSupplyOne)),
			and(new Conditions(Operation.GREATER_EQUAL, 2, shut), not(canSupplyTwo)),
			and(new Conditions(Operation.GREATER_EQUAL, 3, shut), not(canSupplyThree)));
	}

	private CastleDrakanRoomNetwork.Room room(RoomKey key, String name, Requirement location)
	{
		return castleDrakanRoomNetwork.addRoom(key, name, location);
	}

	private VarplayerRequirement openedDoor(int doorStatusVarPlayer, int doorBit)
	{
		return new VarplayerRequirement(doorStatusVarPlayer, true, doorBit);
	}

	private CastleDrakanActionStep castleRecovery(RoomKey destination, QuestStep action, String routeText,
	                                              Requirement needed)
	{
		var recovery = castleAction(destination, action, routeText);
		recovery.conditionToHideInSidebar(not(needed));
		return recovery;
	}

	private CastleDrakanActionStep castleAction(RoomKey destination, QuestStep action, String routeText)
	{
		return new CastleDrakanActionStep(quest, castleDrakanRoomNetwork, destination, action, routeText);
	}

	private CastleDrakanActionStep castleAction(RoomKey destination, Requirement actionLocation,
	                                            QuestStep action, String routeText)
	{
		return new CastleDrakanActionStep(quest, castleDrakanRoomNetwork, destination, actionLocation,
			action, routeText);
	}

	private void connect(CastleDrakanRoomNetwork.Room a, CastleDrakanRoomNetwork.Room b,
	                     Requirement available, CastleDrakanRoomNetwork.Door aToB, CastleDrakanRoomNetwork.Door bToA,
	                     WorldPoint... traps)
	{
		castleDrakanRoomNetwork.connect(a, b, available, aToB, bToA, traps);
	}

	void mapSteps(Map<Integer, QuestStep> steps)
	{
		// This could _technically_ be a conditional step guiding the user from _any_ room back to the throne room,
		// but they should only ever get to this step if they've manually destroyed the key. Their punishment
		// is that they need to read the text to get back to the throne room.

		// TODO: Can I add a note on the sidebar or something, saying: DO NOT DROP AN ITEM UNLESS INSTRUCTED. EVERYTHING YOU ARE TOLD TO GET IS IMPORTANT!!!
		var cVampyriumCastleDrakan = new ConditionalStep(quest, quest.enterPortalInCastleDrakanLobby, "Solve the puzzles inside Vampyrium's Castle Drakan. Supplies are littered around the castle.");

		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, or(solidKey, usedSolidKey), or(fullMoonKey, usedUpFullMoonKey)), cDestroyingTheStockpileGoal);

		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, not(or(halfMoonKey, usedUpHalfMoonKey))), cGetHalfMoonKeyGoal);

		// TODO: It would be nice to have an "has used gibbous moon key" to be sure we don't accidentally guide the user back here when they don't need the key anymore
		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, or(newMoonKey, openedServantsQuarterNewMoonDoor), or(gibbousMoonKey, usedUpGibbousMoonKey),
			or(fullMoonKey, usedUpFullMoonKey)), cGetSolidKey);

		// TODO: It would be nice to have an "has used crescent moon key" to be sure we don't accidentally guide the user back here when they don't need the key anymore
		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, crescentMoonKey, or(newMoonKey, openedDungeonToFreeSafalaanNewMoonDoor), or(gildedKey, hasUsedGildedKey),
			or(gibbousMoonKey, usedUpGibbousMoonKey)), cGetFullMoonKey);
		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, crescentMoonKey, or(newMoonKey, openedDungeonToFreeSafalaanNewMoonDoor)), cGetGildedAndGibbousKeys);
		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, crescentMoonKey), cGetNewMoonKey);
		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, hasGottenDrakanEmblemFromFireplace), cGetCrescentMoonKey);

		cVampyriumCastleDrakan.addStep(and(quest.inVampyriumVarbit, not(hasGottenDrakanEmblemFromFireplace)), cFixClocksGoal);

		steps.put(72, cVampyriumCastleDrakan);
		// 72 -> 74 after talking to Veliaf in the emblem gallery
		steps.put(74, cVampyriumCastleDrakan);
		// 74 -> 76 after freeing Safalaan and Vanescula
		steps.put(76, cVampyriumCastleDrakan);
		// 76 -> 78 after talking to Ivan
		steps.put(78, cVampyriumCastleDrakan);

		// 78 -> 80 after destroying the blood stockpile
		steps.put(80, cDestroyingTheStockpileGoal);
	}

	/// Quest states 82-178: the forest, curing Safalaan, the theatre, and Drakan.

	List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();
		var castleDrakanKit = List.<Requirement>of(quest.blisterwoodFlail, quest.combatGear,
			quest.drakansMedallion);
		sections.add(new PanelDetails("Escaping Castle Drakan - Half moon key",
			cGetHalfMoonKeyGoal.getDisplaySteps(), castleDrakanKit));

		sections.add(new PanelDetails("Escaping Castle Drakan - Fixing the clocks",
			cFixClocksGoal.getDisplaySteps(), List.of(
			quest.blisterwoodFlail,
			quest.combatGear,
			quest.drakansMedallion,
			tinderbox,
			explosiveBarrel
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Crescent moon key",
			cGetCrescentMoonKey.getDisplaySteps(), castleDrakanKit));

		sections.add(new PanelDetails("Escaping Castle Drakan - New moon key",
			cGetNewMoonKey.getDisplaySteps(), castleDrakanKit));

		sections.add(new PanelDetails("Escaping Castle Drakan - Gilded and gibbous keys",
			cGetGildedAndGibbousKeys.getDisplaySteps(), castleDrakanKit));

		sections.add(new PanelDetails("Escaping Castle Drakan - Full moon key",
			cGetFullMoonKey.getDisplaySteps(), castleDrakanKit));

		sections.add(new PanelDetails("Escaping Castle Drakan - Solid key",
			cGetSolidKey.getDisplaySteps(), castleDrakanKit));

		sections.add(new PanelDetails("Escaping Castle Drakan - Destroying the stockpile",
			cDestroyingTheStockpileGoal.getDisplaySteps(), castleDrakanKit));

		return sections;
	}
}
