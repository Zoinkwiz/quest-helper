// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver1;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver2;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver3;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver4;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver5;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver6;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeType;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nand;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
import com.questhelper.steps.widget.WidgetHighlight;
import com.questhelper.util.QuestStepIcon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;

/**
 * The quest guide for the "The Blood Moon Rises" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/The_Blood_Moon_Rises">The OSRS wiki guide</a> was referenced for this guide
 */
public class TheBloodMoonRises extends BasicQuestHelper
{
	// Required items
	ItemRequirement blisterwoodFlail;
	ItemRequirement vyreNobleOutfit;
	ItemRequirement tinderbox;

	// Recommended items
	ItemRequirement combatGear;
	ItemRequirement combatGearMelee;
	ItemRequirement food;
	ItemRequirement prayerPotions;
	ItemRequirement energyRestorePotion;
	ItemRequirement drakansMedallion;
	ItemRequirement anyPickaxe;
	FreeInventorySlotRequirement freeInvSlots6;
	ItemRequirement drakansMedallionToCastleDrakan;

	// Mid-quest item requirements
	ItemRequirement squiresJournal;
	ItemRequirement essiandarsNotes;
	ItemRequirement scruffyNotebook;
	ItemRequirement sarlsJournal;
	ItemRequirement theLifeOfFriar;
	ItemRequirement piousProceedings;
	ItemRequirement fromMisthalinToMorytania;
	ItemRequirement ivandisWritings;

	ItemRequirement halfMoonKey;
	ItemRequirement smallClockHand;
	ItemRequirement largeClockHand;
	ItemRequirement drakanEmblem1;
	ItemRequirement drakanEmblem2;
	ItemRequirement drakanEmblem3;
	ItemRequirement anyOneEmblem;
	ItemRequirement anyOneEmblemHighlighted;
	ItemRequirement anyTwoEmblemHighlighted;
	ItemRequirement anyThreeEmblemHighlighted;
	ItemRequirement explosiveBarrel;

	// Zones
	ZoneRequirement inMyrequeHideoutOldManRal;
	ZoneRequirement atCastleDrakanCourtyard;
	ZoneRequirement inSlepeChurchDungeon;
	ZoneRequirement inCrombwickManor;
	ZoneRequirement inPaterdomusTempleDungeon;
	ZoneRequirement inPaterdomusTempleF0;
	ZoneRequirement inPaterdomusTempleF1;
	ZoneRequirement inIvandisTomb;
	ZoneRequirement inCastleDrakanMines;
	ZoneRequirement inCastleDrakanDaeyaltProcessingArea;
	ZoneRequirement inCastleDrakanCellar;
	ZoneRequirement inCastleDrakanLobby;
	ZoneRequirement inVampyriumCastleDrakanLobbyCutscene;
	ZoneRequirement playerAtWesternClock;
	ZoneRequirement playerAtEasternClock;
	ZoneRequirement playerNextToDoorPuzzle;
	ZoneRequirement inHouseNorthOfCrankBase;
	ZoneRequirement inToothHalfOfKeyHouse;
	ZoneRequirement inBoltCutterHouse;
	ZoneRequirement inClothery;
	ZoneRequirement inBucketHouse;
	ZoneRequirement inTrapdoorHouse;
	ZoneRequirement inBank;
	ZoneRequirement inAltarHouse;
	ZoneRequirement inSmithy;
	ZoneRequirement inResupplyZone;

	ZoneRequirement inSotfa1;
	ZoneRequirement inSotfa2;
	ZoneRequirement ts1Zone;
	ZoneRequirement ts2Zone;
	ZoneRequirement ts3Zone;
	ZoneRequirement ts4Zone;
	ZoneRequirement ts5Zone;
	ZoneRequirement ts6Zone;
	ZoneRequirement ts7ZoneSouth;
	ZoneRequirement ts7ZoneWest;
	ZoneRequirement ts8ZoneSouth;
	ZoneRequirement ts8ZoneWest;
	ZoneRequirement inSotfa3;
	ZoneRequirement nearSotfa3Exit;
	ZoneRequirement inSotfa4;
	ZoneRequirement inSotfa5;
	ZoneRequirement inSotfa6;
	ZoneRequirement acrossSotfa6Pond;

	ZoneRequirement inPalace;
	ZoneRequirement inPalaceSouthernPart;
	ZoneRequirement inPalaceDungeon;
	ZoneRequirement isOutsidePalace;

	ZoneRequirement inWyrdFight;
	ZoneRequirement inBurghDeRottDungeon;

	ZoneRequirement inCastleDrakanFight;

	// Miscellaneous requirements
	VarbitRequirement inCutscene;
	VarplayerRequirement followedByIvan;
	VarbitRequirement canReceivePickaxeFromIvan;
	VarbitRequirement needTeleportUnlock;
	VarbitRequirement inVampyriumVarbit;

	// GROUND FLOOR
	Conditions inLobbyF0;
	VarbitRequirement inDiningRoom;
	VarbitRequirement inThroneRoom;
	VarbitRequirement inRoomSouthOfThroneRoom;
	VarbitRequirement inStorageRoom;
	VarbitRequirement inStudy;
	VarbitRequirement inHallwayWestOfDiningRoom;
	VarbitRequirement inEmblemGallery;
	VarbitRequirement inWestChapelHallway;
	VarbitRequirement inNorthChapelHallway;

	// FIRST FLOOR
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
	WidgetTextRequirement inArrowPuzzle;
	VarbitRequirement anyEmblemInVanesculasHallwayEast;
	VarbitRequirement anyEmblemInRanisHallwayNorth;
	VarbitRequirement noEmblemInRanisHallwayNorth;
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
	VarbitRequirement pulledUpperStoreroomLever;
	VarbitRequirement isFireplaceLit;
	ItemRequirement smallLockbox;
	WidgetPresenceRequirement combinationLockWidgetOpen;
	ItemRequirement fancyGem1;
	ItemRequirement fancyGem2;
	VarbitRequirement venatorHeadOneEyePlaced;
	VarbitRequirement doorPuzzleSolved;
	ItemRequirement mysteriousBook;
	VarbitRequirement venatorHeadBothEyePlaced;
	ItemRequirement ornateHourglass;
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
	VarplayerRequirement hasUsedNewMoonKey;
	VarplayerRequirement hasUsedFullMoonKey;
	VarplayerRequirement usedSolidKey;
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
	ItemRequirement crankWheel;
	VarbitRequirement crankedTheWheel;
	VarbitRequirement crankedTheWheelInTheBank;
	Conditions needCrankWheel;
	ItemRequirement crankWheelForBank;
	ItemRequirement jovkaiKey;
	VarbitRequirement unlockedSmith;
	Conditions needJovkaiKey;
	VarbitRequirement jovkaiKeyInOriginalPosition;
	ItemRequirement dustyBook;
	ItemRequirement viturKey;
	VarbitRequirement unlockedBoltCutterHouse;
	ItemRequirement viturKeyForBoltCutterHouse;
	Conditions viturKeyOrUnlockedBoltCutterHouse;
	VarbitRequirement unlockedToothHalfOfKeyHouse;
	ItemRequirement viturKeyForToothHalfOfKeyHouse;
	Conditions viturKeyOrUnlockedToothHalfOfKeyHouse;
	Conditions needViturKey;
	VarbitRequirement needBoltCutters;
	VarbitRequirement hasUsedBoltCutters;
	ItemRequirement boltCutters;
	ItemRequirement oldCog;
	VarbitRequirement unlockedTrapdoor;
	ItemRequirement trapdoorKey;
	ItemRequirement toothHalfOfKey;
	ItemRequirement loopHalfOfKey;
	VarbitRequirement isCrankWheelInOriginalPosition;
	Conditions needTrapdoorKey;
	VarbitRequirement oldCogInOriginalPosition;
	ItemRequirement myrmelKey;
	VarbitRequirement unlockedBucketHouse;
	ItemRequirement myrmelKeyForBucketHouse;
	VarbitRequirement unlockedBank;
	ItemRequirement myrmelKeyForBank;
	Conditions needMyrmelKey;
	ItemRequirement bucketOfWater;
	ItemRequirement bucket;
	ItemRequirement shadumKey;
	VarbitRequirement unlockedTrapdoorHouse;
	ItemRequirement shadumKeyForTrapdoorHouse;
	Conditions needShadumKey;
	VarbitRequirement foundTheCog;
	VarbitRequirement xd2;
	Conditions needToTalkToIvanForSupplies;
	VarbitRequirement unlockedAltarHouse;
	ItemRequirement anyAxe;
	Conditions anyNearbyNylocas;
	ItemRequirement deadSnake3;
	ItemRequirement serpentRope;
	ItemRequirement amitireLeaves;
	ItemRequirement bowl;
	ItemRequirement bowlOfWater;
	ItemRequirement potato;
	ItemRequirement rawMeat;
	ItemRequirement cookedMeat;
	ItemRequirement incompleteStew;
	ItemRequirement uncookedStew;
	ItemRequirement stew;
	ItemRequirement amitireStew;
	ItemRequirement hallowedMarks;
	ItemRequirement hammer;
	ItemRequirement chisel;
	ItemRequirement knife;
	ItemRequirement blisterwoodLogs;
	ItemRequirement blessedSilverSickle;
	ItemRequirement diamond;
	ItemRequirement diamondTablet;
	ItemRequirement diamondSickleB;
	ItemRequirement enchantedDiamondSickle;
	ItemRequirement enhancedBlisterwoodSickle;
	ItemRequirement blisterwoodFlailUnequipped;
	ItemRequirement hallowedFlail;
	NpcCondition anyNearbyFeralVyres1;
	NpcCondition anyNearbyFeralVyres2;
	NpcCondition anyNearbyFeralVyres3;
	NpcCondition anyNearbyFeralVyres4;
	Conditions anyNearbyFeralVyres;
	ObjectCondition tree6ChoppedDown;
	ObjectCondition tree7Untouched;
	ObjectCondition tree7SlightlyChopped;
	ObjectCondition tree7Chopped;
	ObjectCondition tree8Untouched;
	ObjectCondition tree8SlightlyChopped;
	ObjectCondition tree8Chopped;
	ObjectCondition ropedTree;
	NpcCondition venatorAlive;
	Conditions anyVenatorAlive;
	NpcCondition fmkVenatorAlive;
	VarbitRequirement canStartIvan;
	VarbitRequirement hasCraftedStakes;
	VarbitRequirement ivanProgressDone;
	VarbitRequirement spokeWithVeliaf;
	VarbitRequirement spokeWithVanescula;
	VarbitRequirement veliafProgressDone;

	// Crossing the drawbridge (82-88)
	VarplayerRequirement hasDeathPos;

	// Steps
	// TODO: Remove
	DetailedQuestStep todo;

	/// 0 + 2
	NpcStep startQuest;

	/// 4
	ObjectStep goDownToIvan;
	ConditionalStep cLookForIvan;

	/// 6 + 8
	ObjectStep inspectShrine;

	/// 10
	NpcStep talkToIvanGoingToDarkmeyer;
	DetailedQuestStep watchCutsceneGoingToDarkmeyer;

	/// 12
	NpcStep talkToIvanToReturnToCastleDrakan;
	DetailedQuestStep defendIvanFromVyres;

	/// 14
	NpcStep talkToIvanAfterEscaping;

	/// 16
	NpcStep talkToIvanOutsideSlepeChurch;

	/// 18
	NpcStep askRoyAboutVeliaf;

	/// 20
	ObjectStep lookIntoCommotion;
	ObjectStep climbUpToCrombwickManor;
	ConditionalStep cLookIntoCommotionAtCrombwickManor;

	/// 22
	NpcStep killVampyresWithVeliaf;

	/// 24
	NpcStep talkToVeliafInCrombwickManor;

	/// 26
	NpcStep talkToIvanPaterdomus1;
	ConditionalStep cHeadToPaterdomus;

	/// 28
	DetailedQuestStep readSquiresJournal;

	/// 30
	NpcStep talkToIvanPaterdomus2;
	ConditionalStep cTalkToIvanAfterReadingTheBook;

	/// 32 + 34
	ObjectStep climbUpFromPaterdomusTempleDungeon;
	ObjectStep headToPaterdomusTempleF0;
	NpcStep killMonksOfZamorak;

	/// 36
	ObjectStep climbUpToPaterdomusTempleF1;
	NpcStep talkToIvanInPaterdomusTempleF1;

	/// 38
	ConditionalStep cFindTheWritings;
	PuzzleWrapperStep cFindTheWritingsPW;

	/// 40
	NpcStep talkToIvanAfterFindingTheWritings;

	/// 42
	DetailedQuestStep readIvandisWritings;

	/// 44
	NpcStep talkToIvanAfterReadingIvandisWritings;

	/// 46 + 48
	NpcStep talkToIvanInPaterdomus;
	NpcStep talkToIvanInPaterdomus2;

	/// 50
	DetailedQuestStep getToIvandisTomb;
	ObjectStep investigateHole;

	/// 52
	NpcStep getPickaxe;
	ObjectStep mineHole;

	/// 54
	ObjectStep headThroughHole;

	/// 56
	ObjectStep enterDaeyaltProcessingRoom;

	/// 58
	NpcStep killVampsInDaeyaltRoom;

	/// 60
	NpcStep talkToIvanAfterKillingVamps;

	/// 62
	ObjectStep enterCastleDrakanCellar;

	/// 64
	ObjectStep climbUpToCastleDrakanLobby;

	/// 66 + 68 + 70
	ObjectStep prayAtShrine;
	ObjectStep enterPortalInCastleDrakanLobby;

	/// 70
	DetailedQuestStep youAreInVampyrium;

	/// 72 (Start of Vampyrium Castle Drakan puzzles)
	/// 74 (Vampyrium Castle Drakan puzzles after finding and talking to Veliaf)
	/// 76 (Vampyrium Castle Drakan puzzles after freeing Safalaan and Vanescula)
	/// 78 (Vampyrium Castle Drakan puzzles after talking to Ivan)
	// Half moon key
	private ObjectStep enterThroneRoomFromDiningRoom;
	private ObjectStep investigateThrone;
	private PuzzleWrapperStep hmkPullBustsPW;
	private ObjectStep investigateThroneAgain;
	ConditionalStep cGetHalfMoonKey;
	ObjectStep getKeyFromThroneRoom;

	/// TODO: LATER COG WHEEL
	DetailedQuestStep pickupCrankWheel;
	DetailedQuestStep pickupCrankWheelFromWhereYouDied;

	/// 80 (Vampyrium Castle Drakan after destroying the blood stockpile)
	ObjectStep tryToLeave;
	DetailedQuestStep vampyriumCastleDrakanDestroyedBloodStockpile;
	ConditionalStep cVampyriumCastleDrakanDestroyedBloodStockpile;

	ConditionalStep cGetSmallClockHand;
	ObjectStep enterThroneRoomFromRoomSouthOfThroneRoom;
	ObjectStep enterDiningRoomFromThroneRoom;
	ObjectStep enterHallwayWestOfDiningRoom;
	ObjectStep enterWestRoomFromHallwayWestOfDiningRoom;
	ObjectStep pickUpTinderbox;
	ObjectStep goUpFromStorageRoom;
	ItemStep pickUpExplosiveBarrel;
	ObjectStep enterSouthDoorFromExplosiveRoom;
	ObjectStep searchCrateForDrakanEmblem1;
	ObjectStep exitEmblemRoom1;
	ObjectStep exitEastExplosiveRoom;
	ObjectStep enterNorthDoorInHallwayNextToExplosiveRoom;
	ObjectStep enterIntoHallwayNorthOfLobbyF1;
	ObjectStep enterUnmarkedNorthDoor;
	ObjectStep placeEmblem1OnReceptacle;
	ObjectStep enterVanesculasStudy;
	ObjectStep searchCrateInVanesculasStudyForLargeClockHand;
	ConditionalStep cGetLargeClockHand;
	ObjectStep returnToVanesculasHallway;
	ObjectStep returnToHallwayNorthOfLobbyF1;
	ObjectStep returnToLobbyF1;
	ObjectStep climbDownstairsToLobbyF0;
	ObjectStep enterDiningRoomFromLobby;
	ConditionalStep cGetBackToDiningRoom;
	ObjectStep placeSmallClockHandOnWesternClock;
	PuzzleWrapperStep solveWesternClockPW;
	ObjectStep clickWesternClock;
	ObjectStep placeLargeClockHandOnEasternClock;
	PuzzleWrapperStep solveEasternClockPW;
	ObjectStep clickEasternClock;
	WidgetStep closeClock;
	ObjectStep getEmblem2FromFireplace;
	ConditionalStep cSolveClockPuzzle;
	private ObjectStep cmkToVanesculasHallwayFromHallwayNorthOfLobby;
	private ObjectStep cmkPlaceEmblemInVanesculasStudy;
	private ObjectStep cmkToVanesculasChamber;
	private ObjectStep cmkBlowUpWallInVanesculasChamber;
	private ObjectStep cmkEnterThroughHole;
	private ObjectStep cmkTakeEmblem3;
	private ObjectStep cmkLeaveVenatorRoom;
	private ObjectStep cmkLeaveVanesculasChamber;
	private ObjectStep cmkTakeEmblemFromVanesculasStudy;
	private ObjectStep cmkLeaveVanesculasStudy;
	private ObjectStep cmkRetrieveThirdEmblem;
	private ObjectStep cmkPutEmblemInEastDoor;
	private ObjectStep cmkEnterEastDoor;
	private ObjectStep cmkPutEmblemInRanisHallwayNorth;
	private ObjectStep cmkEnterRanisParlour;
	private DetailedQuestStep cmkGetSkull;
	private ObjectStep cmkLeaveRanisParlourRoom;
	private ObjectStep cmkRemoveEmblemRanisNorth;
	private ObjectStep cmkClimbDownStairsRanisHallway;
	private ObjectStep cmkPlaceEmblemDownstairs;
	private ObjectStep cmkEnterWestChapelHallway;
	private ObjectStep cmkPutEmblemInWestChapelHallway;
	private ObjectStep cmkEnterEmblemGallery;
	private NpcStep cmkTalkToVeliaf;
	private ObjectStep cmkOpenEmblemGalleryChest;
	private ArrowChestPuzzleStep cmkArrowChestPuzzleStep;
	private ObjectStep cmkGetTheKeyFromTheChest;
	private ConditionalStep cGetCrescentMoonKey;
	private ObjectStep enterEmblemGalleryHallway;
	private ObjectStep enterRoomBehindLobby;
	private DetailedQuestStep killBugsAndTakeSyringeBarrel;
	private ObjectStep searchCrateForVenatorStomach;
	private ObjectStep retrievePlugFromTheSinkToTheWest;
	private ObjectStep enterLarder;
	private ObjectStep useSinkPlugOnSinkInLarder;
	private ObjectStep turnSinkTapOn;
	private ObjectStep takePipe;
	private ObjectStep reachBehindCabinetWithPipe;
	private ObjectStep searchLarderCrateForSyringePlunger;
	private ObjectStep leaveLarder;
	private ObjectStep useSharpKnifeOnVenatorCorpse;
	private ObjectStep getTongsFromVenatorCorpse;
	private ObjectStep getNeedleFromSink;
	private DetailedQuestStep assembleSyringe;
	private DetailedQuestStep drawBloodFromVenatorStomach;
	private ObjectStep useSyringeOnChest;
	private ObjectStep getLeftCrestHalfFromKitchenChest;
	private ObjectStep leaveKitchen;
	private ObjectStep enterLobbyFromEmblemGalleryHallway;
	private ObjectStep climbDownstairsToLobbyBasement;
	private ObjectStep enterAnotherVenatorRoom;
	private NpcStep killVenator;
	private ObjectStep searchWeaponRackForOneAxe;
	private ObjectStep placeBattleAxeOnStatue;
	private ObjectStep getMace1;
	private ObjectStep getMace2;
	private ObjectStep getSword;
	private ObjectStep placeMaceOnStatueN;
	private ObjectStep placeMaceOnStatueS;
	private ObjectStep placeSwordOnStatue;
	private ObjectStep openWeaponPuzzleChest;
	private DetailedQuestStep combineCrests;
	private ObjectStep leaveWithFullCrest1;
	private ObjectStep leaveWithFullCrest2;
	private ObjectStep leaveWithFullCrest3;
	private ObjectStep leaveWithFullCrest4;
	private ObjectStep leaveWithFullCrest5;
	private ObjectStep putFullCrestOnFireplaceInStudy;
	private ObjectStep getNewMoonKeyFromFireplace;
	private ConditionalStep cGetNewMoonKey;
	private ObjectStep ggkGoToThroneRoom;
	private ObjectStep ggkGoToDiningRoom;
	private ObjectStep ggkGoToLobby1;
	private ObjectStep ggkGoToBasement;
	private ObjectStep ggkEnterBasementNorthRoom;
	private DetailedQuestStep ggkTakeGibbousMoonKey;
	private ObjectStep ggkReturnToBasementHallway;
	private ObjectStep ggkReturnToLobbyF0;
	private ObjectStep ggkClimbUpToLobbyF1;
	private ObjectStep ggkEnterLobbyF1GibbousMoonDoor;
	private ObjectStep ggkEnterEastDoor;
	private ObjectStep ggkEnterSouthDoor;
	private ObjectStep ggkEnterSouthEastDoor;
	private ObjectStep ggkGetOrnateKnife;
	private ObjectStep ggkPullLeverInOrnateKnifeRoom;
	private ObjectStep ggkEnterUpperStoreroomPortal;
	private ObjectStep ggkLeaveGuestChamberStoreroom;
	private ObjectStep ggkEnterRoomWithVenator;
	private NpcStep ggkKillVenator;
	private ObjectStep ggkpLightFireplace;
	private ObjectStep ggkpSearchChest;
	private ChestCodeStep ggkpSolveLockboxPuzzle;
	private DetailedQuestStep ggkpTryOpenLockbox;
	private ObjectStep ggkpPlaceFancyGemInHead;
	private ChestCodeStep ggkpSolveDoorPuzzle;
	private ObjectStep ggkpEnterSouthWestDoor;
	private ConditionalStep ggkSolvePuzzle;
	private ObjectStep ggkpSearchBookcaseForBook;
	private DetailedQuestStep ggkpOpenMysteriousBook;
	private ObjectStep ggkpLeaveLibrary;
	private ObjectStep ggkpPlaceFancyGemInHead2;
	private ObjectStep ggkpSearchVenatorHead;
	private ObjectStep ggkpLeavePuzzleRoom;
	private ObjectStep ggkPlaceItemsOnDisplayCase;
	private ObjectStep ggkSearchDisplayCaseForGildedKey;
	private ConditionalStep cGetGildedAndGibbousKeys;
	private ObjectStep fmkClimbDownstairsFromDisplayCase;
	private ObjectStep fmkGoThroughLobbyF1NorthDoor;
	private ObjectStep fmkEnterGildedDoorToWest;
	private ObjectStep fmkNorthWest;
	private NpcStep fmkKillVenator;
	private NpcStep fmkTalkToIvan1;
	private GildedBookPuzzle fmkGildedBookPuzzle;
	private ObjectStep fmkGetGildedBook;
	private ObjectStep fmkUseGildedBookOnWesternGildedBookcase;
	private ObjectStep fmkClickGildedBookcase;
	private ObjectStep fmkEnterSecretPassage;
	private DetailedQuestStep fmkTakeFullMoonKey;
	private ConditionalStep cGetFullMoonKey;
	private ObjectStep skLeaveEastDoor;
	private ObjectStep skLeaveGildedDoor;
	private ObjectStep skEnterScratchedDoor;
	private ObjectStep skEnterEasternDoor;
	private ObjectStep skClimbDownStairs;
	private ObjectStep skEnterWesternDoor;
	private ObjectStep skEnterEastFullMoonDoor;
	private ObjectStep skGetCloudyGreyPotion;
	private ObjectStep skGetWeightlessBlackPotion;
	private ObjectStep skGetThickRedPotion;
	private ObjectStep skGetColdBluishWhitePotion;
	private ObjectStep smokeBasin;
	private ObjectStep iceBasin;
	private ObjectStep shadowBasin;
	private ObjectStep bloodBasin;
	private ObjectStep skSearchAltarForAncientSymbol;
	private ObjectStep skEnterEastDoorFromBottleRoom;
	private ObjectStep skPullLever;
	private ObjectStep skEnterPortal;
	private ObjectStep skLeaveServantsQuarters;
	private ObjectStep skLeaveExplosiveRoom;
	private ObjectStep skLeaveExplosiveRoomHallway;
	private ObjectStep skLeaveLobbyF1;
	private ObjectStep skLeaveDiningRoomF1;
	private ObjectStep skLeaveThroneRoomF1;
	private ObjectStep skGetAncientShield;
	private DetailedQuestStep skCombineAncientShieldAndAncientSymbol;
	private ObjectStep skLeaveStudy;
	private ObjectStep skLeaveThroneRoomF1Again;
	private ObjectStep skLeaveDiningRoomF1Again;
	private ObjectStep skLeaveLobbyF1Again;
	private ObjectStep skEnterGibbousMoonDoor;
	private ObjectStep skEnterFullMoonDoor;
	private ObjectStep skUseShieldOnEmptyMount;
	private ObjectStep skGetSolidKey;
	private ConditionalStep cGetSolidKey;
	private ObjectStep dtsLeaveSolidKeyRoom;
	private ObjectStep dtsLeavePuzzleRoom;
	private ObjectStep dtsLeaveDisplayCaseRoom;
	private ObjectStep dtsOpenSolidDoor;
	private ObjectStep dtsSearchShelvesForSupplies;
	private ObjectStep dtsSearchShelvesForSuppliesWater;
	private ObjectStep dtsSearchShelvesForSuppliesBlood;
	private ObjectStep dtsSearchShelvesForSuppliesEssence;
	private ObjectStep makeFirstVial;
	private ObjectStep makeFirstVialEssence;
	private ObjectStep makeFirstVialBlood;
	private ObjectStep makeSecondVial;
	private ObjectStep makeSecondVialEssence;
	private ObjectStep makeSecondVialBlood;
	private ObjectStep makeThirdVial;
	private ObjectStep makeThirdVialBlood;
	private ObjectStep makeThirdVialWater;
	private ObjectStep makeFourthVial;
	private ObjectStep makeFourthVialEssence;
	private ObjectStep makeFourthVialWater;
	private ObjectStep pourAllVialsIntoTheBasin;
	private ConditionalStep cDestroyingTheStockpileLaboratoryStep;
	private ObjectStep dtsLeaveLaboratory;
	private ObjectStep dtsDestroyBloodStockpile;
	private ConditionalStep cDestroyingTheStockpile;
	private ObjectStep leaveCastleDrakan;
	private DetailedQuestStep watchLeaveCastleDrakanCutscene;
	private WidgetStep closeEasternClock;
	private PuzzleWrapperStep cmkArrowChestPuzzleStepPW;
	private DetailedQuestStep ggkWatchTheCutscene;
	private PuzzleWrapperStep ggkpSolveLockboxPuzzlePW;
	private PuzzleWrapperStep ggkpSolveDoorPuzzlePW;
	private PuzzleWrapperStep fmkGildedBookPuzzlePW;
	private PuzzleWrapperStep cDestroyingTheStockpileLaboratoryStepPW;
	private DetailedQuestStep dtsWatchTheCutscene;
	private ObjectStep crankWheel1;
	private ObjectStep enterHouseNextToCrankWheel;
	private ObjectStep searchBookCase;
	private DetailedQuestStep readDustyBook;
	private DetailedQuestStep pickupViturKeyFromWhereYouDied;
	private ObjectStep leaveBookcaseHouse;
	private ObjectStep openViturDoorEastOfBookcaseHouse;
	private ObjectStep searchShedBoltCutter;
	private PuzzleWrapperStep boltCutterShedCombinationLockPW;
	private ObjectStep leaveBoltCutterHouse;
	private ObjectStep enterToothHalfOfKeyHouse;
	private DetailedQuestStep pickupToothHalfOfKey;
	private ObjectStep openChainedDoor;
	private DetailedQuestStep pickupLoopHalfOfKey;
	private DetailedQuestStep makeMyrmelKey;
	private ObjectStep enterBucketHouse;
	private DetailedQuestStep pickupBucket;
	private ObjectStep useBucketOnWell;
	private ObjectStep enterBucketHouseAgain;
	private ObjectStep useBucketOfWaterOnNorthernBarrel;
	private ObjectStep enterShadumDoor;
	private DetailedQuestStep pickupTrapdoorKey;
	private DetailedQuestStep pickupTrapdoorKeyFromWhereYouDied;
	private ObjectStep enterBank;
	private ObjectStep operateBankCrank;
	private ObjectStep enterTrapdoor;
	private ObjectStep searchAltarChest;
	private ObjectStep enterAltarHouseThroughDoor;
	private PuzzleWrapperStep solveAltarChestLockPW;
	private ObjectStep leaveAltarThroughDoor;
	private ObjectStep enterSmith;
	private DetailedQuestStep pickupOldCog;
	private DetailedQuestStep pickupOldCogFromWhereYouDied;
	private DetailedQuestStep pickupBoltCuttersFromWhereYouDied;
	private NpcStep returnToVanescula;
	private NpcStep talkToIvanForSupplies;
	private NpcStep returnToVanesculaReadyToLeave;
	private DetailedQuestStep pickupJovkaiKeyFromWhereYouDied;
	private DetailedQuestStep watchCutsceneRepairedBridge;
	private NpcStep fightDrakan1;
	private DetailedQuestStep flee1WatchTheCutscene;
	private ObjectStep resupplyIfNeeded;
	private ObjectStep resupplyIfNeeded2;
	private NpcStep sotfa1;
	private ObjectStep sotfa1Exit;
	private ConditionalStep sotfa2;
	private ConditionalStep cSotfa3;
	private NpcStep sotfa3AvoidAnimals;
	private ObjectStep sotfa3Exit;
	private ObjectStep sotfa4;
	private ConditionalStep cSotfa5;
	private NpcStep sotfa5;
	private ObjectStep sotfa5Exit;
	private ConditionalStep cSotfa6;
	private NpcStep sotfa6WrangleSnakes;
	private DetailedQuestStep sotfa6CombineSnakes;
	private ObjectStep sotfa6UseRopeOnBranch;
	private ObjectStep sotfa6SwingAcrossWater;
	private ObjectStep sotfa6Exit;
	private DetailedQuestStep sotfaWatchTheCutscene;
	private DetailedQuestStep mysteriousWomanWatchTheCutscene;
	private NpcStep talkToMysteriousWoman1;
	private NpcStep startTalkingToEfaritay;
	private ObjectStep leavePalace1;
	private ObjectStep pickFromAmitirePlant;
	private ObjectStep enterPalace1;
	private ObjectStep searchShelvesForBowl;
	private ObjectStep fillBowlWithWater;
	private ObjectStep getPotatoFromCupboard;
	private ObjectStep getRawMeatFromCupboard;
	private ObjectStep cookMeatOnRange;
	private DetailedQuestStep combineStew;
	private DetailedQuestStep combineStew2;
	private ObjectStep cookStew;
	private DetailedQuestStep combineStew3;
	private NpcStep giveStewToSafalaan;
	private NpcStep talkToSafalaanAfterFeedingHimStew;
	private NpcStep talkToEfaritayAfterFeedingStewToSafalaan;
	private ObjectStep searchWorkbenchForHammer;
	private ObjectStep searchWorkbenchForChisel;
	private ObjectStep searchWorkbenchForKnife;
	private ObjectStep searchCrateForBlisterwoodLogs;
	private ObjectStep searchCrateForBlessedSilverSickle;
	private ObjectStep searchChestForDiamond;
	private DetailedQuestStep putDiamondInSickle;
	private DetailedQuestStep useEnchantDiamondTabletOnSickle;
	private ObjectStep searchChestForDiamondTablet2;
	private DetailedQuestStep createEnhancedBlisterwoodSickle;
	private ObjectStep createHallowedFlail;
	private ObjectStep ivanSearchWorkbenchForKnife;
	private ObjectStep getLogsForStakes;
	private DetailedQuestStep fletchStakes;
	private NpcStep returnToIvan;
	private ObjectStep climbDownstairs;
	private NpcStep talkToVeliafInDungeon;
	private NpcStep talkToVeliafInDungeonAgain;
	private NpcStep talkToVanescula;
	private ObjectStep climbUpstairs;
	private NpcStep talkToSugadintiAfterHelpingAllies;
	private NpcStep speakToIvanWithHallowedFlail;
	private ObjectStep leavePalaceForCombat;
	private DetailedQuestStep getReadyForCombatWatchTheCutscene;
	private NpcStep attackPortals;
	private DetailedQuestStep leaveDoorsCutscene;
	private ObjectStep leaveDoors;
	private DetailedQuestStep guardThePalace;
	private DetailedQuestStep barricadeCutscene;
	private ObjectStep passThroughBarricadeToHelp;
	private ObjectStep passThroughBarricadeToFightDrakan;
	private NpcStep fightDrakan2;
	private DetailedQuestStep finishedDrakan2Cutscene;
	private NpcStep talkToIvanInHauntedWoods;
	private DetailedQuestStep leavingPalaceCutscene;
	private DetailedQuestStep talkToVeliafAfterLeavingPalaceCutscene;
	private NpcStep talkToVeliafAfterLeavingPalace;
	private NpcStep talkToSugadintiInBurghDeRott;
	private NpcStep getToTob;
	private DetailedQuestStep getToTobCutscene;
	private DetailedQuestStep ensureNothingBothersSugadinti;
	private DetailedQuestStep talkToSugadintiAfterFinishingTobCutscene;
	private NpcStep talkToSugadintiAfterFinishingTob;
	private NpcStep headToBarrowsL;
	private DetailedQuestStep headDownToVanesculaCutscene;
	private ObjectStep headDownToVanescula;
	private NpcStep fightTheWyrd;
	private DetailedQuestStep dealtWithWyrdCutscene;
	private DetailedQuestStep findWyrdCutscene;
	private ObjectStep findWyrd;
	private NpcStep speakWithVeliafAfterInspectingFence;
	private NpcStep prepareFightDrakan3;
	private ObjectStep enterBurghDeRottDungeon;
	private NpcStep fightDrakan3;
	private NpcStep talkToVeliaf;
	private NpcStep talkToIvanInsideCastleDrakan;
	private NpcStep talkToSugadintiInsideCastleDrakan;
	private NpcStep talkToEfaritayOnIcyene;
	private ObjectStep enterVampyriumForTheLastTime;
	private DetailedQuestStep talkWithVeliafInBurghDeRottCutscene;
	private DetailedQuestStep talkToVeliafCutscene;
	private DetailedQuestStep enterVampyriumForTheLastTimeCutscene;
	private ObjectStep goToFightDrakan4;
	private DetailedQuestStep goToFightDrakan4Cutscene;
	private NpcStep fightDrakan4;
	private DetailedQuestStep fightDrakan4Cutscene;
	private NpcStep talkToEfaritayAfterKillingDrakan;
	private DetailedQuestStep finalQuestCutscene;
	private DetailedQuestStep youHaveFinishedTheQuest;

	/// 82 (Vampyrium Castle Drakan after attempting to leave through the portal)


	@Override
	protected void setupZones()
	{
		var myrequeHideoutOldManRal = new Zone(new WorldPoint(3588, 9609, 0), new WorldPoint(3606, 9619, 0));
		inMyrequeHideoutOldManRal = new ZoneRequirement(myrequeHideoutOldManRal);

		var castleDrakanCourtyard = new Zone(new WorldPoint(3589, 3347, 0), new WorldPoint(3561, 3367, 0));
		atCastleDrakanCourtyard = new ZoneRequirement(castleDrakanCourtyard);

		var slepeChurchDungeon1 = new Zone(14999);
		var slepeChurchDungeon2 = new Zone(15000);
		var slepeChurchDungeon3 = new Zone(15000);
		var slepeChurchDungeon4 = new Zone(15255);
		var slepeChurchDungeon5 = new Zone(15256);
		var slepeChurchDungeon6 = new Zone(15257);
		var slepeChurchDungeon7 = new Zone(15511);
		var slepeChurchDungeon8 = new Zone(15512);
		var slepeChurchDungeon9 = new Zone(15513);
		inSlepeChurchDungeon = new ZoneRequirement(slepeChurchDungeon1, slepeChurchDungeon2, slepeChurchDungeon3, slepeChurchDungeon4, slepeChurchDungeon5, slepeChurchDungeon6, slepeChurchDungeon7, slepeChurchDungeon8, slepeChurchDungeon9);

		var crombwickManor1 = new Zone(new WorldPoint(3714, 3361, 0), new WorldPoint(3737, 3355, 0));
		var crombwickManor2 = new Zone(new WorldPoint(3721, 3366, 0), new WorldPoint(3725, 3361, 0));
		var crombwickManor3 = new Zone(new WorldPoint(3727, 3362, 0), new WorldPoint(3732, 3359, 0));
		var crombwickManor4 = new Zone(new WorldPoint(3721, 3354, 0), new WorldPoint(3729, 3351, 0));
		inCrombwickManor = new ZoneRequirement(crombwickManor1, crombwickManor2, crombwickManor3, crombwickManor4);

		var paterdomusTempleDungeon1 = new Zone(13466);
		var paterdomusTempleDungeon2 = new Zone(13722);
		inPaterdomusTempleDungeon = new ZoneRequirement(paterdomusTempleDungeon1, paterdomusTempleDungeon2);

		var paterdomusTempleF0P1 = new Zone(new WorldPoint(3409, 3483, 0), new WorldPoint(3411, 3494, 0));
		var paterdomusTempleF0P2 = new Zone(new WorldPoint(3408, 3485, 0), new WorldPoint(3408, 3486, 0));
		var paterdomusTempleF0P3 = new Zone(new WorldPoint(3408, 3491, 0), new WorldPoint(3408, 3492, 0));
		var paterdomusTempleF0P4 = new Zone(new WorldPoint(3412, 3484, 0), new WorldPoint(3415, 3493, 0));
		var paterdomusTempleF0P5 = new Zone(new WorldPoint(3416, 3483, 0), new WorldPoint(3417, 3494, 0));
		var paterdomusTempleF0P6 = new Zone(new WorldPoint(3418, 3484, 0), new WorldPoint(3418, 3493, 0));
		inPaterdomusTempleF0 = new ZoneRequirement(paterdomusTempleF0P1, paterdomusTempleF0P2, paterdomusTempleF0P3, paterdomusTempleF0P4, paterdomusTempleF0P5, paterdomusTempleF0P6);

		var paterdomusTempleF1 = new Zone(new WorldPoint(3408, 3483, 1), new WorldPoint(3419, 3494, 1));
		inPaterdomusTempleF1 = new ZoneRequirement(paterdomusTempleF1);

		var ivandisTomb = new Zone(new WorldPoint(3485, 9879, 0), new WorldPoint(3516, 9853, 0));
		inIvandisTomb = new ZoneRequirement(ivandisTomb);

		var castleDrakanMines = new Zone(new WorldPoint(3119, 7479, 2), new WorldPoint(3088, 7433, 2));
		inCastleDrakanMines = new ZoneRequirement(castleDrakanMines);

		var castleDrakanDaeyaltProcessingArea = new Zone(new WorldPoint(3196, 7447, 0), new WorldPoint(3164, 7469, 0));
		inCastleDrakanDaeyaltProcessingArea = new ZoneRequirement(castleDrakanDaeyaltProcessingArea);

		var castleDrakanCellar = new Zone(new WorldPoint(3142, 7595, 0), new WorldPoint(3187, 7569, 0));
		inCastleDrakanCellar = new ZoneRequirement(castleDrakanCellar);

		var castleDrakanLobby = new Zone(new WorldPoint(3172, 7724, 0), new WorldPoint(3146, 7699, 0));
		inCastleDrakanLobby = new ZoneRequirement(castleDrakanLobby);

		var vampyriumCastleDrakanLobbyCutscene = new Zone(new WorldPoint(2216, 7262, 0), new WorldPoint(2429, 7475, 0));
		inVampyriumCastleDrakanLobbyCutscene = new ZoneRequirement(vampyriumCastleDrakanLobbyCutscene);

		playerAtWesternClock = new ZoneRequirement(new WorldPoint(2344, 7371, 0));
		playerAtEasternClock = new ZoneRequirement(new WorldPoint(2350, 7371, 0));
		// TODO: rename to something with ggk
		playerNextToDoorPuzzle = new ZoneRequirement(new WorldPoint(2519, 7364, 0));

		var houseNorthOfCrankBase = new Zone(new WorldPoint(2589, 7864, 0), new WorldPoint(2585, 7857, 0));
		inHouseNorthOfCrankBase = new ZoneRequirement(houseNorthOfCrankBase);

		var boltCutterHouse1 = new Zone(new WorldPoint(2609, 7860, 0), new WorldPoint(2615, 7856, 0));
		var boltCutterHouse2 = new Zone(new WorldPoint(2612, 7855, 0), new WorldPoint(2619, 7850, 0));
		var boltCutterHouse3 = new Zone(new WorldPoint(2621, 7859, 0), new WorldPoint(2616, 7855, 0));
		inBoltCutterHouse = new ZoneRequirement(boltCutterHouse1, boltCutterHouse2, boltCutterHouse3);

		var toothHalfOfKeyHouse = new Zone(new WorldPoint(2596, 7849, 0), new WorldPoint(2600, 7843, 0));
		inToothHalfOfKeyHouse = new ZoneRequirement(toothHalfOfKeyHouse);

		var clothery = new Zone(new WorldPoint(2592, 7812, 0), new WorldPoint(2588, 7818, 0));
		inClothery = new ZoneRequirement(clothery);

		var bucketHouse1 = new Zone(new WorldPoint(2592, 7834, 0), new WorldPoint(2606, 7830, 0));
		var bucketHouse2 = new Zone(new WorldPoint(2596, 7834, 0), new WorldPoint(2602, 7826, 0));
		inBucketHouse = new ZoneRequirement(bucketHouse1, bucketHouse2);

		var trapdoorHouse = new Zone(new WorldPoint(2620, 7816, 0), new WorldPoint(2614, 7812, 0));
		inTrapdoorHouse = new ZoneRequirement(trapdoorHouse);

		var bank = new Zone(new WorldPoint(2582, 7835, 0), new WorldPoint(2578, 7829, 0));
		inBank = new ZoneRequirement(bank);

		var altarHouse1 = new Zone(new WorldPoint(2575, 7850, 0), new WorldPoint(2578, 7846, 0));
		var altarHouse2 = new Zone(new WorldPoint(2579, 7850, 0), new WorldPoint(2583, 7842, 0));
		var altarHouse3 = new Zone(new WorldPoint(2584, 7850, 0), new WorldPoint(2592, 7844, 0));
		inAltarHouse = new ZoneRequirement(altarHouse1, altarHouse2, altarHouse3);

		var smithy = new Zone(new WorldPoint(2578, 7818, 0), new WorldPoint(2582, 7812, 0));
		inSmithy = new ZoneRequirement(smithy);

		var resupplyZone = new Zone(new WorldPoint(2853, 7640, 0), new WorldPoint(2837, 7655, 0));
		var resupplyZone2 = new Zone(new WorldPoint(2950, 7831, 0), new WorldPoint(2973, 7813, 0));
		inResupplyZone = new ZoneRequirement(resupplyZone, resupplyZone2);



		// fleeing through the forest
		inSotfa1 = new ZoneRequirement(new Zone(new WorldPoint(2950, 7846, 0), new WorldPoint(2971, 7867, 0)));

		inSotfa2 = new ZoneRequirement(new Zone(new WorldPoint(2997, 7927, 0), new WorldPoint(2953, 7876, 0)));
		ts1Zone = new ZoneRequirement(new Zone(new WorldPoint(2955, 7896, 0), new WorldPoint(2968, 7879, 0)));
		ts2Zone = new ZoneRequirement(new Zone(new WorldPoint(2968, 7900, 0), new WorldPoint(2965, 7897, 0)));
		ts3Zone = new ZoneRequirement(new Zone(new WorldPoint(2965, 7900, 0), new WorldPoint(2960, 7903, 0)), new Zone(new WorldPoint(2966, 7903, 0), new WorldPoint(2977, 7901, 0)), new Zone(new WorldPoint(2976, 7900, 0), new WorldPoint(2970, 7897, 0)));
		ts4Zone = new ZoneRequirement(new Zone(new WorldPoint(2965, 7904, 0), new WorldPoint(2973, 7911, 0)), new Zone(new WorldPoint(2974, 7907, 0), new WorldPoint(2997, 7904, 0)), new Zone(new WorldPoint(2980, 7903, 0), new WorldPoint(2994, 7886, 0)), new Zone(new WorldPoint(2969, 7893, 0), new WorldPoint(2983, 7884, 0)));
		ts5Zone = new ZoneRequirement(new Zone(new WorldPoint(2975, 7908, 0), new WorldPoint(2980, 7910, 0)));
		ts6Zone = new ZoneRequirement(new Zone(new WorldPoint(2981, 7909, 0), new WorldPoint(2982, 7912, 0)), new Zone(new WorldPoint(2981, 7912, 0), new WorldPoint(2980, 7912, 0)), new Zone(new WorldPoint(2978, 7911, 0), new WorldPoint(2981, 7915, 0)));
		ts7ZoneSouth = new ZoneRequirement(new WorldPoint(2982, 7914, 0));
		ts7ZoneWest = new ZoneRequirement(new Zone(new WorldPoint(2981, 7916, 0), new WorldPoint(2981, 7915, 0)));
		ts8ZoneSouth = new ZoneRequirement(new Zone(new WorldPoint(2982, 7916, 0), new WorldPoint(2984, 7916, 0)));
		ts8ZoneWest = new ZoneRequirement(new Zone(new WorldPoint(2982, 7917, 0), new WorldPoint(2983, 7917, 0)), new Zone(new WorldPoint(2982, 7915, 0)));

		inSotfa3 = new ZoneRequirement(new Zone(new WorldPoint(2899, 7924, 0), new WorldPoint(2932, 7886, 0)));
		nearSotfa3Exit = new ZoneRequirement(new Zone(new WorldPoint(2924, 7895, 0), new WorldPoint(2929, 7889, 0)));

		inSotfa4 = new ZoneRequirement(new Zone(new WorldPoint(2986, 7844, 0), new WorldPoint(3006, 7867, 0)));

		inSotfa5 = new ZoneRequirement(new Zone(new WorldPoint(2903, 7829, 0), new WorldPoint(2927, 7855, 0)));

		inSotfa6 = new ZoneRequirement(new Zone(new WorldPoint(3018, 7926, 0), new WorldPoint(3055, 7884, 0)));
		acrossSotfa6Pond = new ZoneRequirement(new Zone(new WorldPoint(3047, 7892, 0), new WorldPoint(3033, 7885, 0)));




		var palace1 = new Zone(new WorldPoint(3155, 7823, 1), new WorldPoint(3181, 7843, 1));
		var palace2 = new Zone(new WorldPoint(3177, 7863, 1), new WorldPoint(3159, 7844, 1));
		inPalace = new ZoneRequirement(palace1, palace2);
		inPalaceSouthernPart = new ZoneRequirement(palace1);
		var palaceDungeon1 = new Zone(new WorldPoint(3155, 7823, 0), new WorldPoint(3181, 7843, 0));
		var palaceDungeon2 = new Zone(new WorldPoint(3177, 7863, 0), new WorldPoint(3159, 7844, 0));
		inPalaceDungeon = new ZoneRequirement(palaceDungeon1, palaceDungeon2);

		var outsidePalace1 = new Zone(new WorldPoint(2989, 7690, 0), new WorldPoint(2963, 7673, 0));
		isOutsidePalace = new ZoneRequirement(outsidePalace1);



		inWyrdFight = new ZoneRequirement(new Zone(11892));

		inBurghDeRottDungeon = new ZoneRequirement(new Zone(new WorldPoint(3489, 9632, 0), new WorldPoint(3500, 9622, 0)));

		inCastleDrakanFight = new ZoneRequirement(new Zone(new WorldPoint(2514, 7853, 3), new WorldPoint(2495, 7823, 3)));
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		blisterwoodFlail = new ItemRequirement("Blisterwood flail", ItemID.BLISTERWOOD_FLAIL);
		// TODO: Add Hallowed Flail as the preferred alternative in terms of item IDs actually
		blisterwoodFlail.setTooltip("You can buy another Blisterwood Flail from Ivan in the Myreque Hideout in Old Man Ral's basement or Veliaf Hurtz at the Icyene Graveyard(?)");

		vyreNobleOutfit = new ItemRequirements("Vyre noble outfit",
			new ItemRequirement("Vyre noble top", ItemID.VYRELORD_TORSO),
			new ItemRequirement("Vyre noble legs", ItemID.VYRELORD_LEGS),
			new ItemRequirement("Vyre noble shoes", ItemID.VYRELORD_SHOES)).isNotConsumed();
		vyreNobleOutfit.setHighlightInInventory(true);
		vyreNobleOutfit.setMustBeEquipped(true);
		// vyreNobleOutfit.canBeObtainedDuringQuest();
		vyreNobleOutfit.setTooltip("Can be obtained during the quest in the chest next to Ivan");
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).canBeObtainedDuringQuest();
		tinderbox.appendToTooltip("You can get another one from the storage room (south-west on floor 1)");

		// Recommended items
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		combatGearMelee = new ItemRequirement("Melee combat gear", -1, -1).isNotConsumed();
		combatGearMelee.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		food = new ItemRequirement("Good healing food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		// TODO: Are staminas actually necessary?
		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		drakansMedallion = new ItemRequirement("Drakan's medallion", ItemID.DRAKANS_MEDALLION).isNotConsumed();

		anyPickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).canBeObtainedDuringQuest();

		freeInvSlots6 = new FreeInventorySlotRequirement(6);

		drakansMedallionToCastleDrakan = new ItemRequirement("Drakan's medallion to Castle Drakan", ItemID.DRAKANS_MEDALLION).isNotConsumed().highlighted();

		// Mid-quest item requirements
		squiresJournal = new ItemRequirement("Squire's journal", 33701);
		essiandarsNotes = new ItemRequirement("Essiandar's notes", 33707);
		scruffyNotebook = new ItemRequirement("Scruffy notebook", 33704);
		sarlsJournal = new ItemRequirement("Sarl's journal", 33703);
		theLifeOfFriar = new ItemRequirement("The Life of Friar", 33706);
		piousProceedings = new ItemRequirement("Pious proceedings", 33705);
		fromMisthalinToMorytania = new ItemRequirement("Misthalin to Morytania", 33702);
		ivandisWritings = new ItemRequirement("Ivandis' writings", 33708);

		// Vampyrium Castle Drakan items
		halfMoonKey = new ItemRequirement("Half moon key", 33725);
		smallClockHand = new ItemRequirement("Small clock hand", 33744);
		largeClockHand = new ItemRequirement("Large clock hand", 33745);
		drakanEmblem1 = new ItemRequirement("Drakan emblem", 33731);
		drakanEmblem2 = new ItemRequirement("Drakan emblem", 33732);
		drakanEmblem3 = new ItemRequirement("Drakan emblem", 33733);
		anyOneEmblem = new ItemRequirement("Drakan emblem", 33731);
		anyOneEmblem.addAlternates(33732, 33733);
		anyOneEmblemHighlighted = anyOneEmblem.highlighted();
		anyTwoEmblemHighlighted = anyOneEmblemHighlighted.quantity(2);
		anyThreeEmblemHighlighted = anyOneEmblemHighlighted.quantity(3);
		explosiveBarrel = new ItemRequirement("Explosive barrel", 33743);
		explosiveBarrel.setTooltip("You can get another one from the room above the storage room (south-west on floor 2)");

		// Miscellaneous requirements
		// TODO: Should this be it's own "InCutsceneRequirement"?
		inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);
		// TODO: Should this be it's own "FollowerNPCRequirement"?
		followedByIvan = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);
		canReceivePickaxeFromIvan = new VarbitRequirement(15469, 0);
		needTeleportUnlock = new VarbitRequirement(15470, 0);

		// Vampyrium Castle Drakan room requirements
		var castleDrakanFloor = new VarbitBuilder(15489);
		var castleDrakanRoom = new VarbitBuilder(15499);

		var onF0 = castleDrakanFloor.eq(1);
		var onF1 = castleDrakanFloor.eq(2);

		var inLobby = castleDrakanRoom.eq(1);

		inLobbyF0 = and(onF0, inLobby);
		inDiningRoom = castleDrakanRoom.eq(2);
		inThroneRoom = castleDrakanRoom.eq(3);
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

		var throneRoomPuzzleB = new VarbitBuilder(15508);
		needToStartThroneRoomPuzzle = throneRoomPuzzleB.eq(0);
		needToPullBusts = throneRoomPuzzleB.eq(1);
		needToGetKey = throneRoomPuzzleB.eq(2);
		doneWithThroneRoomPuzzle = throneRoomPuzzleB.eq(3);

		needToPullBust3 = new VarbitRequirement(15541, 1);
		needToPullBust4 = new VarbitRequirement(15542, 1);
		needToPullBust1 = new VarbitRequirement(15539, 1);

		placedEmblemInVanesculasHallway = new VarbitRequirement(15504, 0, Operation.GREATER);
		placedEmblemInVanesculasStudy = new VarbitRequirement(15503, 0, Operation.GREATER);

		inVampyriumVarbit = new VarbitRequirement(15482, 1);
		vanesculasChamberWallDestroyed = new VarbitRequirement(15511, 1);
		smallClockHandNeedsReplacing = new VarbitRequirement(15509, 0);
		largeClockHandNeedsReplacing = new VarbitRequirement(15510, 0);
		var westernClockLargeHandCorrect = new VarbitRequirement(15514, 11);
		var westernClockSmallHandCorrect = new VarbitRequirement(15513, 9);
		westernClockNeedsFixing = not(and(westernClockLargeHandCorrect, westernClockSmallHandCorrect));
		clockWidgetOpen = new WidgetPresenceRequirement(963, 0);
		var easternClockLargeHandCorrect = new VarbitRequirement(15517, 0);
		var easternClockSmallHandCorrect = new VarbitRequirement(15515, 4);
		easternClockNeedsFixing = not(and(easternClockLargeHandCorrect, easternClockSmallHandCorrect));
		hasGottenDrakanEmblemFromFireplace = new VarplayerRequirement(5632, true, 3);
		needToFinishClockPuzzle = or(westernClockNeedsFixing, easternClockNeedsFixing);
		ornateSkull = new ItemRequirement("Ornate skull", 33741);
		inArrowPuzzle = new WidgetTextRequirement(810, 15, 9, "Confirm");

		anyEmblemInVanesculasHallwayEast = new VarbitRequirement(15506, 1, Operation.GREATER_EQUAL);
		anyEmblemInRanisHallwayNorth = new VarbitRequirement(15505, 1, Operation.GREATER_EQUAL);
		noEmblemInRanisHallwayNorth = new VarbitRequirement(15505, 0);
		anyEmblemInNorthChapelHallway = new VarbitRequirement(15502, 1, Operation.GREATER_EQUAL);
		anyEmblemInWestChapelHallway = new VarbitRequirement(15501, 1, Operation.GREATER_EQUAL);
		cmkHasSpokenWithVeliaf = new VarbitRequirement(15464, 74, Operation.GREATER_EQUAL);
		cmkSolvedChestPuzzle = new VarbitRequirement(15512, 1);
		crescentMoonKey = new ItemRequirement("Crescent moon key", 33726);
		newMoonKey = new ItemRequirement("New moon key", 33728);

		syringeBarrel = new ItemRequirement("Syringe barrel", 33752);
		venatorStomach = new ItemRequirement("Venator stomach", 33756);
		sinkPlug = new ItemRequirement("Sink plug", 33747);
		brokenPipe = new ItemRequirement("Broken pipe", 33748);
		sharpKitchenKnife = new ItemRequirement("Sharp knife", 33749);
		syringePlunger = new ItemRequirement("Syringe plunger", 33753);
		tongs = new ItemRequirement("Tongs", 33750);
		syringeNeedle = new ItemRequirement("Syringe needle", 33751);
		emptySyringe = new ItemRequirement("Empty syringe", 33754);
		fullSyringe = new ItemRequirement("Full syringe", 33755);

		leftHalfOfCrest = new ItemRequirement("Left crest half", 33734);
		rightHalfOfCrest = new ItemRequirement("Right crest half", 33735);
		fullCrest = new ItemRequirement("Full crest", 33736);

		larderSinkNeedsPlugging = new VarbitRequirement(15537, 0);
		larderSinkPlugged = new VarbitRequirement(15537, 1);
		larderSinkCollapsed = new VarbitRequirement(15537, 2);

		hasCutVenatorStomachUp = new VarbitRequirement(15538, 1);

		unlockedKitchenChest = new VarbitRequirement(15518, 1);

		battleAxe = new ItemRequirement("Battleaxe", 33759);
		needToPutAxeOnStatue = new VarbitRequirement(15546, 0);

		mace = new ItemRequirement("Mace", 33760);
		mace2 = mace.quantity(2);

		sword = new ItemRequirement("Sword", 33757);

		needToPutMaceOnStatue1 = new VarbitRequirement(15548, 0);
		needToPutMaceOnStatue2 = new VarbitRequirement(15547, 0);

		needToPutSwordOnStatue = new VarbitRequirement(15545, 0);
		doneWithWeaponPuzzle = and(new VarbitRequirement(15545, 1), new VarbitRequirement(15546, 1), new VarbitRequirement(15547, 1), new VarbitRequirement(15548, 1));

		fullCrestInStudy = new VarbitRequirement(15535, 1);
		gibbousMoonKey = new ItemRequirement("Gibbous moon key", 33727);
		ornateKnife = new ItemRequirement("Ornate knife", 33740);
		pulledUpperStoreroomLever = new VarbitRequirement(15532, 1);
		isFireplaceLit = new VarbitRequirement(15543, 1);
		smallLockbox = new ItemRequirement("Lockbox", 33761);
		combinationLockWidgetOpen = new WidgetPresenceRequirement(InterfaceID.CombinationLock.CONTENTS);
		fancyGem1 = new ItemRequirement("Fancy gem", 33765);
		fancyGem2 = new ItemRequirement("Fancy gem", 33766);
		venatorHeadOneEyePlaced = new VarbitRequirement(15523, 1);
		doorPuzzleSolved = new VarbitRequirement(15519, 1);
		mysteriousBook = new ItemRequirement("Mysterious book", 33764);
		venatorHeadBothEyePlaced = new VarbitRequirement(15523, 3);
		ornateHourglass = new ItemRequirement("Ornate hourglass", 33742);
		ornateHourglass.setTooltip("You can return to the puzzle room with the venator and search the venator head to get a new one");
		// This is in an attempt to hide the requirement if it's already been placed in the display case
		ornateSkull.setConditionToHide(new VarbitRequirement(15563, true, 0));
		ornateKnife.setConditionToHide(new VarbitRequirement(15563, true, 1));
		ornateHourglass.setConditionToHide(new VarbitRequirement(15563, true, 2));
		allItemsPlacedInDisplayCase = new VarbitRequirement(15563, 7);
		gildedKey = new ItemRequirement("Gilded key", 33729);
		hasUsedGildedKey = new VarplayerRequirement(5638, true, 3);
		gildedKey.setConditionToHide(hasUsedGildedKey);
		fmkIvanFollowingYou = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);
		gildedBookPuzzleOpen = new WidgetPresenceRequirement(InterfaceID.CastleDrakanBookcase.CONTENTS);
		gildedBook = new ItemRequirement("Gilded book", 33767);

		startedLibraryPuzzle = new VarbitRequirement(15525, 1);
		finishedLibraryPuzzle = new VarbitRequirement(15525, 2);

		fullMoonKey = new ItemRequirement("Full moon key", 33724);

		cloudyGreyPotion = new ItemRequirement("Cloudy grey potion", 33769);
		weightlessBlackPotion = new ItemRequirement("Weightless black potion", 33770);
		thickRedPotion = new ItemRequirement("Thick red potion", 33771);
		coldBlueishWhitePotion = new ItemRequirement("Cold bluish-white potion", 33772);

		solvedSmokeBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_SMOKE_BASIN, 1);
		solvedShadowBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_SHADOW_BASIN, 1);
		solvedBloodBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_BLOOD_BASIN, 1);
		solvedIceBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_ICE_BASIN, 1);
		solvedAllBasins = and(solvedBloodBasin, solvedShadowBasin, solvedIceBasin, solvedSmokeBasin);

		ancientSymbol = new ItemRequirement("Ancient symbol", 33737);
		openedPortalFromChapelLibraryToServantsQuarters = new VarbitRequirement(15533, 1);
		ancientShield = new ItemRequirement("Ancient shield", 33738);
		shieldWithSymbol = new ItemRequirement("Shield with symbol", 33739);
		hasMountedShield = new VarbitRequirement(15526, 1);
		solidKey = new ItemRequirement("Solid key", 33730);

		hasUsedNewMoonKey = new VarplayerRequirement(5638, true, 1);
		hasUsedFullMoonKey = new VarplayerRequirement(5638, true, 29);
		usedSolidKey = new VarplayerRequirement(5639, true, 5);

		vialOfWater = new ItemRequirement("Vial of water", 33774);
		vialsOfWater2 = vialOfWater.quantity(2);
		vialOfBlood = new ItemRequirement("Vial of blood", 33775);
		vialsOfBlood3 = vialOfBlood.quantity(3);
		pureEssence = new ItemRequirement("Pure essence", 33776);
		pureEssence3 = pureEssence.quantity(3);

		chemicalVial = new ItemRequirement("Chemical vial", 33778);
		chemicalVial2 = chemicalVial.quantity(2);
		chemicalVial3 = chemicalVial.quantity(3);
		chemicalVial4 = chemicalVial.quantity(4);

		hasPouredAnything = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LAB_BASIN, 0, Operation.GREATER);

		var refinerVb = new VarbitBuilder(VarbitID.CASTLE_DRAKAN_REFINER_ITEM_1);
		refinerWater = refinerVb.eq(1);
		refinerBlood = refinerVb.eq(2);
		refinerEssence = refinerVb.eq(3);

		finishedLabPuzzle = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LAB_BASIN, 53);

		// Crossing the drawbridge (82-88)
		hasDeathPos = new VarplayerRequirement(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS, 0, Operation.GREATER_EQUAL);
		crankWheel = new ItemRequirement("Crank wheel", 33788);
		crankedTheWheel = new VarbitRequirement(VarbitID.SANGVESTI_FANCY_HOUSE_3_DOOR, 1);
		crankedTheWheelInTheBank = new VarbitRequirement(VarbitID.SANGVESTI_INTERIOR_BANK_DOOR, 1);
		needCrankWheel = nand(crankedTheWheel, crankedTheWheelInTheBank);
		crankWheelForBank = crankWheel.hideConditioned(crankedTheWheelInTheBank);
		jovkaiKey = new ItemRequirement("Jovkai key", 33780);
		unlockedSmith = new VarbitRequirement(VarbitID.SANGVESTI_BLACKSMITH_DOOR, 1);
		jovkaiKey.setConditionToHide(unlockedSmith);
		needJovkaiKey = nand(unlockedSmith);
		jovkaiKeyInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_CHAPEL, 1);
		dustyBook = new ItemRequirement("Dusty book", 33789);
		viturKey = new ItemRequirement("Vitur key", 33785);
		// Important vitur key use:
		unlockedBoltCutterHouse = new VarbitRequirement(VarbitID.SANGVESTI_VITUR_MANOR_DOOR, 1);
		viturKeyForBoltCutterHouse = viturKey.hideConditioned(unlockedBoltCutterHouse);
		viturKeyOrUnlockedBoltCutterHouse = or(viturKey, unlockedBoltCutterHouse);
		// Important vitur key use:
		unlockedToothHalfOfKeyHouse = new VarbitRequirement(VarbitID.SANGVESTI_FOOD_SHOP_DOOR, 1);
		viturKeyForToothHalfOfKeyHouse = viturKey.hideConditioned(unlockedToothHalfOfKeyHouse);
		viturKeyOrUnlockedToothHalfOfKeyHouse = or(viturKey, unlockedToothHalfOfKeyHouse);
		needViturKey = nand(unlockedBoltCutterHouse, unlockedToothHalfOfKeyHouse);
		needBoltCutters = new VarbitRequirement(VarbitID.SANGVESTI_CLOTHES_SHOP_DOOR, 0);
		hasUsedBoltCutters = new VarbitRequirement(VarbitID.SANGVESTI_CLOTHES_SHOP_DOOR, 1);
		boltCutters = new ItemRequirement("Bolt cutters", 33787);
		boltCutters.setConditionToHide(hasUsedBoltCutters);
		oldCog = new ItemRequirement("Old cog", 33779);

		unlockedTrapdoor = new VarbitRequirement(VarbitID.SANGVESTI_TRAPDOOR, 1);

		trapdoorKey = new ItemRequirement("Trapdoor key", 33786);
		trapdoorKey.setConditionToHide(unlockedTrapdoor);

		toothHalfOfKey = new ItemRequirement("Tooth half of key", 33782);
		loopHalfOfKey = new ItemRequirement("Loop half of key", 33781);

		isCrankWheelInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_BASIC_HOUSE_3, 1);

		needTrapdoorKey = nand(unlockedTrapdoor);

		oldCogInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_BLACKSMITH, 1);

		myrmelKey = new ItemRequirement("Myrmel key", 33783);

		unlockedBucketHouse = new VarbitRequirement(VarbitID.SANGVESTI_PUB_DOOR, 1);
		myrmelKeyForBucketHouse = myrmelKey.hideConditioned(unlockedBucketHouse);
		unlockedBank = new VarbitRequirement(VarbitID.SANGVESTI_BANK_DOOR, 1);
		myrmelKeyForBank = myrmelKey.hideConditioned(unlockedBank);
		needMyrmelKey = nand(unlockedBucketHouse, unlockedBank);
		bucketOfWater = new ItemRequirement("Bucket of water", 1929);

		bucket = new ItemRequirement("Bucket", 1925);

		shadumKey = new ItemRequirement("Shadum key", 33784);
		unlockedTrapdoorHouse = new VarbitRequirement(VarbitID.SANGVESTI_FANCY_HOUSE_1_DOOR, 1);
		shadumKeyForTrapdoorHouse = shadumKey.hideConditioned(unlockedTrapdoorHouse);
		// TODO (what is this todo for)
		needShadumKey = nand(unlockedTrapdoorHouse);

		foundTheCog = new VarbitRequirement(VarbitID.MYQ6_VANESCULA_DRAKAN_FOUND_THE_COG, 1);
		xd2 = new VarbitRequirement(VarbitID.MYQ6_SANGVESTI_SUPPLY_WARNING, 0);
		needToTalkToIvanForSupplies = and(foundTheCog, xd2);

		unlockedAltarHouse = new VarbitRequirement(VarbitID.SANGVESTI_CHAPEL_BACK_DOOR_2, 1);

		anyAxe = new ItemRequirement("Any axe", ItemCollections.AXES);

		anyNearbyNylocas = or(new NpcRequirement(16236), new NpcRequirement(16237));

		deadSnake3 = new ItemRequirement("Dead blood serpent", 33791, 3);
		serpentRope = new ItemRequirement("Serpent rope", 33792);

		amitireLeaves = new ItemRequirement("Amitire leaves", 33796);
		bowl = new ItemRequirement("Bowl", 1923);
		bowlOfWater = new ItemRequirement("Bowl of water", ItemID.BOWL_WATER);
		potato = new ItemRequirement("Potato", 1942);
		rawMeat = new ItemRequirement("Raw impaler meat", 33821);
		cookedMeat = new ItemRequirement("Cooked meat", 2142);
		incompleteStew = new ItemRequirement("Incomplete stew", 1997);
		uncookedStew = new ItemRequirement("Uncooked stew", 2001);
		stew = new ItemRequirement("Stew", 2003);
		amitireStew = new ItemRequirement("Amitire stew", 33797);

		hallowedMarks = new ItemRequirement("Hallowed marks", 33798);
		hammer = new ItemRequirement("Hammer", 2347);
		chisel = new ItemRequirement("Chisel", 1755);
		knife = new ItemRequirement("Knife", 946);
		blisterwoodLogs = new ItemRequirement("Blisterwood logs", 24691);
		blessedSilverSickle = new ItemRequirement("Silver sickle (b)", 2963);
		diamond = new ItemRequirement("Diamond", 1601);
		diamondTablet = new ItemRequirement("Enchant diamond", 8019);
		diamondSickleB = new ItemRequirement("Diamond sickle (b)", 33709);
		enchantedDiamondSickle = new ItemRequirement("Enchanted diamond sickle (b)", 33711);
		enhancedBlisterwoodSickle = new ItemRequirement("Blisterwood sickle (e)", 33713);
		blisterwoodFlailUnequipped = blisterwoodFlail.copy();
		blisterwoodFlailUnequipped.setMustBeUnequipped(true);
		hallowedFlail = new ItemRequirement("Hallowed flail", 33718);

		anyNearbyFeralVyres1 = new NpcCondition(16229);
		anyNearbyFeralVyres2 = new NpcCondition(16230);
		anyNearbyFeralVyres3 = new NpcCondition(16231);
		anyNearbyFeralVyres4 = new NpcCondition(16232);
		anyNearbyFeralVyres = or(anyNearbyFeralVyres1, anyNearbyFeralVyres2, anyNearbyFeralVyres3, anyNearbyFeralVyres4);

		tree6ChoppedDown = new ObjectCondition(61954, new WorldPoint(2980, 7913, 0));
		tree7Untouched = new ObjectCondition(61955, new WorldPoint(2982, 7915, 0));
		tree7SlightlyChopped = new ObjectCondition(61956, new WorldPoint(2982, 7915, 0));
		tree7Chopped = new ObjectCondition(61957, new WorldPoint(2982, 7915, 0));
		tree8Untouched = new ObjectCondition(61955, new WorldPoint(2984, 7917, 0));
		tree8SlightlyChopped = new ObjectCondition(61956, new WorldPoint(2984, 7917, 0));
		tree8Chopped = new ObjectCondition(61957, new WorldPoint(2984, 7917, 0));

		ropedTree = new ObjectCondition(61949, new WorldPoint(3040, 7895, 0));

		venatorAlive = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_5);
		var venatorAlive1 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR);
		var venatorAlive2 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_2);
		var venatorAlive3 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_3);
		var venatorAlive4 = new NpcCondition(NpcID.CASTLE_DRAKAN_VENATOR_4);
		// TODO: It would be nice if NpcCondition could accept an array of npc ids, similar to how NpcStep works. Preferably this just works with the constructor without the need for a function like "addAlternateNPCs" since there's no need for there to be a primary npc for a condition.
		anyVenatorAlive = or(venatorAlive, venatorAlive1, venatorAlive2, venatorAlive3, venatorAlive4);

		// could also use [2026-07-11T11:49:32Z 9968] varp CASTLE_DRAKAN_ENEMY_STATUS_1 (5640) 1238587111 -> 1507022567
		fmkVenatorAlive = new NpcCondition(16213);

		var ivanProgress = new VarbitBuilder(VarbitID.MYQ6_IVAN_HIDEOUT_PROGRESS);
		canStartIvan = ivanProgress.eq(1);
		hasCraftedStakes = ivanProgress.eq(2);
		ivanProgressDone = ivanProgress.eq(3);

		var veliafProgress = new VarbitBuilder(VarbitID.MYQ6_VELIAF_HIDEOUT_PROGRESS);
		spokeWithVeliaf = veliafProgress.eq(1);
		spokeWithVanescula = veliafProgress.eq(2);
		veliafProgressDone = veliafProgress.eq(3);
	}

	void setupSteps()
	{
		// TODO: Remove
		todo = new DetailedQuestStep(this, "This step should be unreachable. Please report this with a screenshot in the Quest Helper discord. If you're not sure what to do, you can use the excellent OSRS wiki guide until Quest Helper comes back.");

		/// 0 + 2
		startQuest = new NpcStep(this, 15839, new WorldPoint(3697, 3184, 0), "Talk to Sarius Guile in the Icyene Graveyard to start the quest.");
		startQuest.addDialogStep("Yes.");

		/// 4
		var talkToIvan = new NpcStep(this, 1 /* TODO */, new WorldPoint(3599, 9612, 0), "");
		goDownToIvan = new ObjectStep(this, ObjectID.MYQ4_HIDEOUT_TRAPDOOR_OPEN, new WorldPoint(3605, 3215, 0), "", blisterwoodFlail);
		goDownToIvan.addAlternateObjects(ObjectID.MYQ4_HIDEOUT_TRAPDOOR);
		cLookForIvan = new ConditionalStep(this, goDownToIvan, "Look for Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");
		cLookForIvan.addStep(inMyrequeHideoutOldManRal, talkToIvan);

		/// 6 + 8
		inspectShrine = new ObjectStep(this, 61177, new WorldPoint(3601, 9614, 0), "Inspect the makeshift shrine in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");

		/// 10
		talkToIvanGoingToDarkmeyer = new NpcStep(this, NpcID.MYQ5_IVAN_CHILD_BLISTERWOOD_TRADE, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch while wearing the vyre noble outfit, ready for a fight.", vyreNobleOutfit, blisterwoodFlail, combatGear, food, prayerPotions);
		talkToIvanGoingToDarkmeyer.addDialogStep("Are you ready to go to Darkmeyer?");
		talkToIvanGoingToDarkmeyer.addDialogStep("I'm ready.");
		watchCutsceneGoingToDarkmeyer = talkToIvanGoingToDarkmeyer.cutscene();

		/// 12
		// Return-step in case the user died / left / teleported out
		talkToIvanToReturnToCastleDrakan = new NpcStep(this, NpcID.MYQ5_IVAN_CHILD_BLISTERWOOD_TRADE, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch to return to the Castle Drakan courtyard.", blisterwoodFlail, combatGear, food, prayerPotions);
		talkToIvanToReturnToCastleDrakan.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanToReturnToCastleDrakan.addDialogStep("I'm ready.");
		talkToIvanGoingToDarkmeyer.addSubSteps(talkToIvanToReturnToCastleDrakan);

		defendIvanFromVyres = new DetailedQuestStep(this, "Kill the vyrewatches and defend Ivan Strom until he can teleport you both out. Kill the approaching acidic bloodvelds with a ranged weapon. Prioritize the Vyrewatch Sentinels.", blisterwoodFlail, combatGear, food, prayerPotions);

		/// 14
		talkToIvanAfterEscaping = new NpcStep(this, 15835, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch after escaping.");

		/// 16
		talkToIvanOutsideSlepeChurch = new NpcStep(this, 15855, new WorldPoint(3727, 3310, 0), "Talk to Ivan Strom in the graveyard outside the Slepe church.");

		/// 18
		askRoyAboutVeliaf = new NpcStep(this, NpcID.SLEPE_BARTENDER_ROY, new WorldPoint(3750, 3296, 0), "Talk to Roy the bartender in Slepe and ask him about Veliaf's whereabouts.");
		askRoyAboutVeliaf.addDialogStep("We're looking for a friend of ours.");

		/// 20
		lookIntoCommotion = new ObjectStep(this, ObjectID.SLP_CHURCH_CRYPT_SOUTH_LADDER_DOWN, new WorldPoint(3727, 3301, 0), "Head to the Crombwick Manor through the church dungeon.", blisterwoodFlail, combatGear, food, prayerPotions);
		climbUpToCrombwickManor = new ObjectStep(this, ObjectID.SLP_BASEMENT_MANOR_EXIT, new WorldPoint(3726, 9756, 1), "Head to the Crombwick Manor through the church dungeon.", blisterwoodFlail, combatGear, food, prayerPotions);
		cLookIntoCommotionAtCrombwickManor = new ConditionalStep(this, talkToIvanOutsideSlepeChurch);
		cLookIntoCommotionAtCrombwickManor.addStep(and(followedByIvan, inSlepeChurchDungeon), climbUpToCrombwickManor);
		cLookIntoCommotionAtCrombwickManor.addStep(followedByIvan, lookIntoCommotion);

		/// 22
		// TODO: I'm missing one NPC ID here. Please get all npc ids!!
		killVampyresWithVeliaf = new NpcStep(this, new int[]{16127, 16128, 16129, 16125}, new WorldPoint(3725, 3357, 0), "Help Veliaf kill the vampyres in Crombwick Manor.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampyresWithVeliaf.setAllowMultipleHighlights(true);

		/// 24
		talkToVeliafInCrombwickManor = new NpcStep(this, 15885, new WorldPoint(3731, 3359, 0), "Talk to Veliaf after helping him kill the vampyres in Crombwick Manor.");

		/// 26
		var enterPaterdomus = new ObjectStep(this, ObjectID.PIPEASTSIDETRAPDOOR_OPEN, new WorldPoint(3422, 3485, 0), "Talk with Ivan Strom in the Paterdomus dungeon.");
		talkToIvanPaterdomus1 = new NpcStep(this, 15855, new WorldPoint(3441, 9897, 0), "Talk with Ivan in the Paterdomus dungeon.");
		talkToIvanPaterdomus1.addSubSteps(enterPaterdomus);
		enterPaterdomus.addAlternateObjects(ObjectID.PIPEASTSIDETRAPDOOR);
		cHeadToPaterdomus = new ConditionalStep(this, enterPaterdomus);
		cHeadToPaterdomus.addStep(inPaterdomusTempleDungeon, talkToIvanPaterdomus1);

		/// 28
		readSquiresJournal = new DetailedQuestStep(this, "Read the Squire's journal Ivan just gave you.", squiresJournal.highlighted());

		/// 30
		talkToIvanPaterdomus2 = new NpcStep(this, 15855, new WorldPoint(3441, 9897, 0), "Talk with Ivan again after reading the Squire's journal.");
		cTalkToIvanAfterReadingTheBook = new ConditionalStep(this, enterPaterdomus);
		cTalkToIvanAfterReadingTheBook.addStep(inPaterdomusTempleDungeon, talkToIvanPaterdomus2);

		/// 32 + 34
		climbUpFromPaterdomusTempleDungeon = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3405, 9907, 0), "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		headToPaterdomusTempleF0 = new ObjectStep(this, ObjectID.PRIESTPERILTEMPLEDOORL, new WorldPoint(3408, 3489, 0), "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		killMonksOfZamorak = new NpcStep(this, new int[]{16155, 16156, 16154, 16156}, "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		killMonksOfZamorak.setAllowMultipleHighlights(true);
		killMonksOfZamorak.addSubSteps(climbUpFromPaterdomusTempleDungeon, headToPaterdomusTempleF0);

		/// 36
		climbUpToPaterdomusTempleF1 = new ObjectStep(this, 61189, new WorldPoint(3417, 3492, 0), "Talk to Ivan Storm on the first floor of the Paterdomus temple.");
		talkToIvanInPaterdomusTempleF1 = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Storm on the first floor of the Paterdomus temple.");
		talkToIvanInPaterdomusTempleF1.addSubSteps(climbUpToPaterdomusTempleF1);

		/// 38
		var plinth1Pos = new WorldPoint(3409, 3483, 1);
		var plinth2Pos = new WorldPoint(3408, 3485, 1);
		var plinth3Pos = new WorldPoint(3409, 3487, 1);
		var plinth4Pos = new WorldPoint(3409, 3490, 1);
		var plinth5Pos = new WorldPoint(3408, 3492, 1);
		var plinth6Pos = new WorldPoint(3409, 3494, 1);

		var essiandarsNotesPlinthID = 61301;
		var essiandarsNotesName = "Essiandar's notes";

		var sarlsJournalPlinthID = 61297;
		var sarlsJournalName = "Sarl's journal";

		var fromMisthalinToMorytaniaPlinthID = 61296;
		var fromMisthalinToMorytaniaName = "From Misthalin to Morytania";

		var scruffyNotebookPlinthID = 61298;
		var scruffyNotebookName = "scruffy notebook";

		var theLifeOfFriarPlinthID = 61300;
		var theLifeOfFriarName = "The Life of Friar";

		var piousProceedingsPlinthID = 61299;
		var piousProceedingsName = "Pious proceedings";

		var plinth1VB = new VarbitBuilder(15496);
		var essiandarsNotesS1 = plinth1VB.eq(6);
		var essiandarsNotesS1Put = bookPut(plinth1Pos, essiandarsNotesName, essiandarsNotes.highlighted());
		var sarlsJournalS1 = plinth1VB.eq(2);
		var sarlsJournalS1Take = bookTake(plinth1Pos, sarlsJournalName, sarlsJournalPlinthID);
		var fromMisthalinToMorytaniaS1 = plinth1VB.eq(1);
		var fromMisthalinToMorytaniaS1Take = bookTake(plinth1Pos, fromMisthalinToMorytaniaName, fromMisthalinToMorytaniaPlinthID);
		var scruffyNotebookS1 = plinth1VB.eq(3);
		var scruffyNotebookS1Take = bookTake(plinth1Pos, scruffyNotebookName, scruffyNotebookPlinthID);
		var theLifeOfFriarS1 = plinth1VB.eq(5);
		var theLifeOfFriarS1Take = bookTake(plinth1Pos, theLifeOfFriarName, theLifeOfFriarPlinthID);
		var piousProceedingsS1 = plinth1VB.eq(4);
		var piousProceedingsS1Take = bookTake(plinth1Pos, piousProceedingsName, piousProceedingsPlinthID);

		var plinth2VB = new VarbitBuilder(15492);
		var essiandarsNotesS2 = plinth2VB.eq(6);
		var essiandarsNotesS2Take = bookTake(plinth2Pos, essiandarsNotesName, essiandarsNotesPlinthID);
		var sarlsJournalS2 = plinth2VB.eq(2);
		var sarlsJournalS2Put = bookPut(plinth2Pos, sarlsJournalName, sarlsJournal.highlighted());
		var fromMisthalinToMorytaniaS2 = plinth2VB.eq(1);
		var fromMisthalinToMorytaniaS2Take = bookTake(plinth2Pos, fromMisthalinToMorytaniaName, fromMisthalinToMorytaniaPlinthID);
		var scruffyNotebookS2 = plinth2VB.eq(3);
		var scruffyNotebookS2Take = bookTake(plinth2Pos, scruffyNotebookName, scruffyNotebookPlinthID);
		var theLifeOfFriarS2 = plinth2VB.eq(5);
		var theLifeOfFriarS2Take = bookTake(plinth2Pos, theLifeOfFriarName, theLifeOfFriarPlinthID);
		var piousProceedingsS2 = plinth2VB.eq(4);
		var piousProceedingsS2Take = bookTake(plinth2Pos, piousProceedingsName, piousProceedingsPlinthID);

		var plinth3VB = new VarbitBuilder(15491);
		var essiandarsNotesS3 = plinth3VB.eq(6);
		var essiandarsNotesS3Take = bookTake(plinth3Pos, essiandarsNotesName, essiandarsNotesPlinthID);
		var sarlsJournalS3 = plinth3VB.eq(2);
		var sarlsJournalS3Take = bookTake(plinth3Pos, sarlsJournalName, sarlsJournalPlinthID);
		var fromMisthalinToMorytaniaS3 = plinth3VB.eq(1);
		var fromMisthalinToMorytaniaS3Put = bookPut(plinth3Pos, fromMisthalinToMorytaniaName, fromMisthalinToMorytania.highlighted());
		var scruffyNotebookS3 = plinth3VB.eq(3);
		var scruffyNotebookS3Take = bookTake(plinth3Pos, scruffyNotebookName, scruffyNotebookPlinthID);
		var theLifeOfFriarS3 = plinth3VB.eq(5);
		var theLifeOfFriarS3Take = bookTake(plinth3Pos, theLifeOfFriarName, theLifeOfFriarPlinthID);
		var piousProceedingsS3 = plinth3VB.eq(4);
		var piousProceedingsS3Take = bookTake(plinth3Pos, piousProceedingsName, piousProceedingsPlinthID);

		var plinth4VB = new VarbitBuilder(15493);
		var essiandarsNotesS4 = plinth4VB.eq(6);
		var essiandarsNotesS4Take = bookTake(plinth4Pos, essiandarsNotesName, essiandarsNotesPlinthID);
		var sarlsJournalS4 = plinth4VB.eq(2);
		var sarlsJournalS4Take = bookTake(plinth4Pos, sarlsJournalName, sarlsJournalPlinthID);
		var fromMisthalinToMorytaniaS4 = plinth4VB.eq(1);
		var fromMisthalinToMorytaniaS4Take = bookTake(plinth4Pos, fromMisthalinToMorytaniaName, fromMisthalinToMorytaniaPlinthID);
		var scruffyNotebookS4 = plinth4VB.eq(3);
		var scruffyNotebookS4Put = bookPut(plinth4Pos, scruffyNotebookName, scruffyNotebook.highlighted());
		var theLifeOfFriarS4 = plinth4VB.eq(5);
		var theLifeOfFriarS4Take = bookTake(plinth4Pos, theLifeOfFriarName, theLifeOfFriarPlinthID);
		var piousProceedingsS4 = plinth4VB.eq(4);
		var piousProceedingsS4Take = bookTake(plinth4Pos, piousProceedingsName, piousProceedingsPlinthID);

		var plinth5VB = new VarbitBuilder(15495);
		var essiandarsNotesS5 = plinth5VB.eq(6);
		var essiandarsNotesS5Take = bookTake(plinth5Pos, essiandarsNotesName, essiandarsNotesPlinthID);
		var sarlsJournalS5 = plinth5VB.eq(2);
		var sarlsJournalS5Take = bookTake(plinth5Pos, sarlsJournalName, sarlsJournalPlinthID);
		var fromMisthalinToMorytaniaS5 = plinth5VB.eq(1);
		var fromMisthalinToMorytaniaS5Take = bookTake(plinth5Pos, fromMisthalinToMorytaniaName, fromMisthalinToMorytaniaPlinthID);
		var scruffyNotebookS5 = plinth5VB.eq(3);
		var scruffyNotebookS5Take = bookTake(plinth5Pos, scruffyNotebookName, scruffyNotebookPlinthID);
		var theLifeOfFriarS5 = plinth5VB.eq(5);
		var theLifeOfFriarS5Put = bookPut(plinth5Pos, theLifeOfFriarName, theLifeOfFriar.highlighted());
		var piousProceedingsS5 = plinth5VB.eq(4);
		var piousProceedingsS5Take = bookTake(plinth5Pos, piousProceedingsName, piousProceedingsPlinthID);

		var plinth6VB = new VarbitBuilder(15494);
		var essiandarsNotesS6 = plinth6VB.eq(6);
		var essiandarsNotesS6Take = bookTake(plinth6Pos, essiandarsNotesName, essiandarsNotesPlinthID);
		var sarlsJournalS6 = plinth6VB.eq(2);
		var sarlsJournalS6Take = bookTake(plinth6Pos, sarlsJournalName, sarlsJournalPlinthID);
		var fromMisthalinToMorytaniaS6 = plinth6VB.eq(1);
		var fromMisthalinToMorytaniaS6Take = bookTake(plinth6Pos, fromMisthalinToMorytaniaName, fromMisthalinToMorytaniaPlinthID);
		var scruffyNotebookS6 = plinth6VB.eq(3);
		var scruffyNotebookS6Take = bookTake(plinth6Pos, scruffyNotebookName, scruffyNotebookPlinthID);
		var theLifeOfFriarS6 = plinth6VB.eq(5);
		var theLifeOfFriarS6Take = bookTake(plinth6Pos, theLifeOfFriarName, theLifeOfFriarPlinthID);
		var piousProceedingsS6 = plinth6VB.eq(4);
		var piousProceedingsS6Put = bookPut(plinth6Pos, piousProceedingsName, piousProceedings.highlighted());

		var climbUpTempleForWritings = new ObjectStep(this, 61189, new WorldPoint(3417, 3492, 0), "Climb up the temple and solve the puzzle.");
		cFindTheWritings = new ConditionalStep(this, climbUpTempleForWritings, "Find the writings on the first floor of the Paterdomus temple for Ivan Strom.");

		var getSarlsJournalAndTheLifeOfFriar = new ObjectStep(this, 61304, new WorldPoint(3414, 3491, 1), "Get Sarl's journal and The Life of Friar from the bookcase.", sarlsJournal, theLifeOfFriar);
		getSarlsJournalAndTheLifeOfFriar.addDialogStep("Both.");
		getSarlsJournalAndTheLifeOfFriar.addDialogStep("Yes.");

		var getPiousProceedings = new ObjectStep(this, 61305, new WorldPoint(3411, 3491, 1), "Get Pious proceedings from the bookcase.", piousProceedings);
		getPiousProceedings.addDialogStep("Yes.");

		var getFromMisthalinToMorytania = new ObjectStep(this, 61302, new WorldPoint(3411, 3492, 1), "Get Misthalin to Morytania from the bookcase.", fromMisthalinToMorytania);
		getFromMisthalinToMorytania.addDialogStep("Yes.");

		var getScruffyNotebookAndEssiandarsNotes = new ObjectStep(this, 61306, new WorldPoint(3409, 3488, 1), "Get the scruffy notebook and Essiandar's notes from the bookcase.");
		getScruffyNotebookAndEssiandarsNotes.addDialogStep("Both.");
		getScruffyNotebookAndEssiandarsNotes.addDialogStep("Yes.");

		var doneWithPuzzleStepNowReadTheDialog = new DetailedQuestStep(this, "You have placed all the books correctly, continue in the dialog to proceed.");

		cFindTheWritings.addStep(and(inPaterdomusTempleF1, and(essiandarsNotesS1, sarlsJournalS2, fromMisthalinToMorytaniaS3, scruffyNotebookS4, theLifeOfFriarS5, piousProceedingsS6)), doneWithPuzzleStepNowReadTheDialog);

		// Remove any book that is on the wrong plinth
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, essiandarsNotesS2), essiandarsNotesS2Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, essiandarsNotesS3), essiandarsNotesS3Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, essiandarsNotesS4), essiandarsNotesS4Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, essiandarsNotesS5), essiandarsNotesS5Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, essiandarsNotesS6), essiandarsNotesS6Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, sarlsJournalS1), sarlsJournalS1Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, sarlsJournalS3), sarlsJournalS3Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, sarlsJournalS4), sarlsJournalS4Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, sarlsJournalS5), sarlsJournalS5Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, sarlsJournalS6), sarlsJournalS6Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, fromMisthalinToMorytaniaS1), fromMisthalinToMorytaniaS1Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, fromMisthalinToMorytaniaS2), fromMisthalinToMorytaniaS2Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, fromMisthalinToMorytaniaS4), fromMisthalinToMorytaniaS4Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, fromMisthalinToMorytaniaS5), fromMisthalinToMorytaniaS5Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, fromMisthalinToMorytaniaS6), fromMisthalinToMorytaniaS6Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, scruffyNotebookS1), scruffyNotebookS1Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, scruffyNotebookS2), scruffyNotebookS2Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, scruffyNotebookS3), scruffyNotebookS3Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, scruffyNotebookS5), scruffyNotebookS5Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, scruffyNotebookS6), scruffyNotebookS6Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, theLifeOfFriarS1), theLifeOfFriarS1Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, theLifeOfFriarS2), theLifeOfFriarS2Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, theLifeOfFriarS3), theLifeOfFriarS3Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, theLifeOfFriarS4), theLifeOfFriarS4Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, theLifeOfFriarS6), theLifeOfFriarS6Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, piousProceedingsS1), piousProceedingsS1Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, piousProceedingsS2), piousProceedingsS2Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, piousProceedingsS3), piousProceedingsS3Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, piousProceedingsS4), piousProceedingsS4Take);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, piousProceedingsS5), piousProceedingsS5Take);

		// Place Essiandar's notes on the first plinth from the south
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, nor(essiandarsNotes, essiandarsNotesS1)), getScruffyNotebookAndEssiandarsNotes);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, not(essiandarsNotesS1)), essiandarsNotesS1Put);

		// Place scruffy notebook on the fourth plinth from the south
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, nor(scruffyNotebook, scruffyNotebookS4)), getScruffyNotebookAndEssiandarsNotes);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, not(scruffyNotebookS4)), scruffyNotebookS4Put);

		// Place Sarl's journal on the second plinth from the south
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, nor(sarlsJournal, sarlsJournalS2)), getSarlsJournalAndTheLifeOfFriar);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, not(sarlsJournalS2)), sarlsJournalS2Put);

		// Place The Life of Friar on the fifth plinth from the south
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, nor(theLifeOfFriar, theLifeOfFriarS5)), getSarlsJournalAndTheLifeOfFriar);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, not(theLifeOfFriarS5)), theLifeOfFriarS5Put);

		// Place From Misthalin to Morytania on the third plinth from the south
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, nor(fromMisthalinToMorytania, fromMisthalinToMorytaniaS3)), getFromMisthalinToMorytania);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, not(fromMisthalinToMorytaniaS3)), fromMisthalinToMorytaniaS3Put);

		// Place Pious proceedings on the sixth plinth from the south
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, nor(piousProceedings, piousProceedingsS6)), getPiousProceedings);
		cFindTheWritings.addStep(and(inPaterdomusTempleF1, not(piousProceedingsS6)), piousProceedingsS6Put);

		cFindTheWritingsPW = cFindTheWritings.puzzleWrapStepWithDefaultText("Find the writings on the first floor of the Paterdomus temple for Ivan Strom.");

		/// 40
		talkToIvanAfterFindingTheWritings = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Strom after solving the puzzle.");

		/// 42
		readIvandisWritings = new DetailedQuestStep(this, "Read Ivandis' writings.", ivandisWritings.highlighted());

		/// 44
		talkToIvanAfterReadingIvandisWritings = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Strom after reading Ivandis' writings.");

		/// 46
		talkToIvanInPaterdomus = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.", blisterwoodFlail, combatGear, prayerPotions);

		/// 48
		talkToIvanInPaterdomus2 = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.", blisterwoodFlail, combatGear, prayerPotions);
		talkToIvanInPaterdomus2.addDialogStep("Lead the way.");
		talkToIvanInPaterdomus.addSubSteps(talkToIvanInPaterdomus2);

		/// 50
		getToIvandisTomb = new DetailedQuestStep(this, new WorldPoint(3500, 9864, 0), "Get to the Myreque hideout");
		investigateHole = new ObjectStep(this, 61193, new WorldPoint(3505, 9857, 0), "Investigate the blockage to the south of the hideout.");

		/// 52
		getPickaxe = new NpcStep(this, 15855, new WorldPoint(3505, 9861, 0), "Ask Ivan Strom for a pickaxe", anyPickaxe);
		mineHole = new ObjectStep(this, 61194, new WorldPoint(3505, 9857, 0), "Mine the blockage to the south of the hideout.", anyPickaxe);
		mineHole.addSubSteps(getPickaxe);

		/// 54
		headThroughHole = new ObjectStep(this, 61195, new WorldPoint(3505, 9857, 0), "Head through the cave entrance to the south of the hideout, ready for a fight.", blisterwoodFlail, combatGear, food, prayerPotions);

		/// 56
		enterDaeyaltProcessingRoom = new ObjectStep(this, 61197, new WorldPoint(3117, 7472, 2), "Head into the daeyalt processing room through the tunnel to the north-east.", blisterwoodFlail, combatGear, food, prayerPotions);

		/// 58
		killVampsInDaeyaltRoom = new NpcStep(this, new int[]{16125, 16126, 16137, 16136, 16137}, "Kill vampyres. Focus on the Vyrewatch Sentinels. Avoid the Blood orb. Lure Vyrewatches into the Blood orbs to deal massive damage to them.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampsInDaeyaltRoom.setAllowMultipleHighlights(true);

		/// 60
		// TODO: who do we actually talk to here. is this a "free the slave" step instead?
		talkToIvanAfterKillingVamps = new NpcStep(this, 15864, new WorldPoint(3178, 7459, 0), "Talk to Ivan after killing the vampyres.");

		/// 62
		enterCastleDrakanCellar = new ObjectStep(this, 61205, new WorldPoint(3182, 7470, 0), "Enter the Castle Drakan cellars through the entry to the north.");

		/// 64
		climbUpToCastleDrakanLobby = new ObjectStep(this, 61207, new WorldPoint(3147, 7578, 0), "Climb up the stairs to the Castle Drakan lobby.");

		/// 66 + 68
		// TODO(FOR FUTURE ADVENTURERS): Do you _need_ to bring the medallion for this?
		prayAtShrine = new ObjectStep(this, 61226, new WorldPoint(3168, 7707, 0), "Pray at the shrine to let your Drakan's Medallion teleport you here.", drakansMedallion);

		enterPortalInCastleDrakanLobby = new ObjectStep(this, 61216, new WorldPoint(3161, 7710, 0), "Click the ominous red portal in the Castle Drakan lobby.", blisterwoodFlail, combatGearMelee);
		enterPortalInCastleDrakanLobby.addDialogStep("Yes.");
		enterPortalInCastleDrakanLobby.addTeleport(drakansMedallionToCastleDrakan);

		/// 70
		youAreInVampyrium = enterPortalInCastleDrakanLobby.cutscene();

		enterThroneRoomFromDiningRoom = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 0), "Enter the throne room.");
		var enterThroneRoomFromStudy = new ObjectStep(this, 61576, new WorldPoint(2358, 7380, 0), "Enter the throne room.");
		enterThroneRoomFromDiningRoom.addSubSteps(enterThroneRoomFromStudy);

		var cHmkEnterThroneRoom = new ConditionalStep(this, enterThroneRoomFromDiningRoom);
		cHmkEnterThroneRoom.addStep(inDiningRoom, enterThroneRoomFromDiningRoom);
		cHmkEnterThroneRoom.addStep(inStudy, enterThroneRoomFromStudy);

		investigateThrone = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Investigate the throne.");
		investigateThroneAgain = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Investigate the throne again.");

		var pullBust1 = new ObjectStep(this, 61645, new WorldPoint(2317, 7393, 0), "Pull the northern-most bust.");
		var pullBust2 = new ObjectStep(this, 61648, new WorldPoint(2317, 7392, 0), "Pull the second northern-most bust.");
		var pullBust3 = new ObjectStep(this, 61651, new WorldPoint(2317, 7391, 0), "Pull the second southern-most bust.");
		var pullBust4 = new ObjectStep(this, 61654, new WorldPoint(2317, 7390, 0), "Pull the southern-most bust.");

		var hmkPullBusts = new ConditionalStep(this, pullBust2, "Pull the busts in the right order.");
		hmkPullBusts.addStep(needToPullBust3, pullBust3);
		hmkPullBusts.addStep(needToPullBust4, pullBust4);
		hmkPullBusts.addStep(needToPullBust1, pullBust1);
		hmkPullBustsPW = hmkPullBusts.puzzleWrapStepWithDefaultText("Solve the puzzle in the room.");

		getKeyFromThroneRoom = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Head back to the throne room and search the throne for a half moon key.");
		investigateThroneAgain.addSubSteps(getKeyFromThroneRoom);

		cGetHalfMoonKey = new ConditionalStep(this, cHmkEnterThroneRoom, "\nFind the half moon key.");
		cGetHalfMoonKey.addStep(and(inThroneRoom, needToStartThroneRoomPuzzle), investigateThrone);
		cGetHalfMoonKey.addStep(and(inThroneRoom, needToPullBusts), hmkPullBustsPW);
		cGetHalfMoonKey.addStep(and(inThroneRoom, needToGetKey), investigateThroneAgain);

		/// UNCATEGORIZED
		var searchShelvesForSmallClockHand = new ObjectStep(this, 61752, new WorldPoint(2323, 7387, 0), "Search the shelves for a small clock hand in the room south of the throne room.");
		var enterRoomSouthOfThroneRoom = new ObjectStep(this, 61587, new WorldPoint(2310, 7386, 0), "Enter the room south of the throne room.");
		cGetSmallClockHand = new ConditionalStep(this, searchShelvesForSmallClockHand, "Find the small clock hand.");
		cGetSmallClockHand.addStep(and(inVampyriumVarbit, inThroneRoom), enterRoomSouthOfThroneRoom);

		enterThroneRoomFromRoomSouthOfThroneRoom = new ObjectStep(this, 61587, new WorldPoint(2327, 7391, 0), "Enter the throne room.");
		enterDiningRoomFromThroneRoom = new ObjectStep(this, 61572, new WorldPoint(2304, 7391, 0), "Enter the dining room.");
		enterHallwayWestOfDiningRoom = new ObjectStep(this, 61586, new WorldPoint(2336, 7370, 0), "Enter the hallway west of the dining room.");
		enterWestRoomFromHallwayWestOfDiningRoom = new ObjectStep(this, 61572, new WorldPoint(2323, 7395, 0), "Enter the door to the west of the hallway, avoiding the traps on the floor.");
		enterWestRoomFromHallwayWestOfDiningRoom.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2335, 7396, 0),
			new WorldPoint(2334, 7396, 0),
			new WorldPoint(2335, 7397, 0),
			new WorldPoint(2334, 7397, 0),
			new WorldPoint(2328, 7394, 0),
			new WorldPoint(2327, 7394, 0),
			new WorldPoint(2328, 7395, 0),
			new WorldPoint(2327, 7395, 0)
		);

		pickUpTinderbox = new ObjectStep(this, 61691, new WorldPoint(2344, 7387, 0), "Search the sparkling chest for a tinderbox.");

		goUpFromStorageRoom = new ObjectStep(this, 61602, new WorldPoint(2340, 7384, 0), "Climb up the stairs.", tinderbox);

		pickUpExplosiveBarrel = new ItemStep(this, new WorldPoint(2439, 7388, 0), "Pick up the explosive barrel", explosiveBarrel, tinderbox);

		enterSouthDoorFromExplosiveRoom = new ObjectStep(this, 61573, new WorldPoint(2439, 7384, 0), "Enter the room south of where you picked up the explosive barrel.", explosiveBarrel, tinderbox);

		searchCrateForDrakanEmblem1 = new ObjectStep(this, 61751, new WorldPoint(2454, 7378, 0), "Search the crate for a drakan emblem");

		exitEmblemRoom1 = new ObjectStep(this, 61572, new WorldPoint(2455, 7381, 0), "Return to the explosive room.", explosiveBarrel, tinderbox, drakanEmblem1);
		exitEastExplosiveRoom = new ObjectStep(this, 61572, new WorldPoint(2444, 7386, 0), "Exit the explosive room through the east door.", explosiveBarrel, tinderbox, drakanEmblem1);
		enterNorthDoorInHallwayNextToExplosiveRoom = new ObjectStep(this, 61572, new WorldPoint(2474, 7398, 0), "Enter the northern door in the hallway, avoiding traps on the floor.", explosiveBarrel, tinderbox, drakanEmblem1);
		enterNorthDoorInHallwayNextToExplosiveRoom.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2471, 7397, 0),
			new WorldPoint(2472, 7397, 0),
			new WorldPoint(2471, 7396, 0),
			new WorldPoint(2472, 7396, 0)
		);
		enterIntoHallwayNorthOfLobbyF1 = new ObjectStep(this, 61572, new WorldPoint(2314, 7382, 1), "Enter the room to the north.", explosiveBarrel, tinderbox, drakanEmblem1);
		var climbUpToLobbyF1 = new ObjectStep(this, 61599, new WorldPoint(2318, 7371, 0), "Climb up the stairs.");
		enterIntoHallwayNorthOfLobbyF1.addSubSteps(climbUpToLobbyF1);

		enterUnmarkedNorthDoor = new ObjectStep(this, 61576, new WorldPoint(2466, 7422, 0), "Enter the northern unmarked door.", explosiveBarrel, tinderbox, drakanEmblem1);

		placeEmblem1OnReceptacle = new ObjectStep(this, 61638, new WorldPoint(2469, 7408, 0), "Place the drakan emblem on the empty receptacle next to the southern door down the hall, avoiding traps on the way.", explosiveBarrel, tinderbox, anyOneEmblemHighlighted);
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

		enterVanesculasStudy = new ObjectStep(this, 61576, new WorldPoint(2468, 7407, 0), "Enter Vanescula's study.", explosiveBarrel, tinderbox);

		searchCrateInVanesculasStudyForLargeClockHand = new ObjectStep(this, 61751, new WorldPoint(2466, 7370, 0), "Search the crate in the north-west corner of Vanescula's study for a large clock hand.", explosiveBarrel, tinderbox);

		cGetLargeClockHand = new ConditionalStep(this, searchCrateInVanesculasStudyForLargeClockHand);
		cGetLargeClockHand.addStep(inVanesculasStudy, searchCrateInVanesculasStudyForLargeClockHand);
		cGetLargeClockHand.addStep(and(inVanesculasHallway, placedEmblemInVanesculasHallway), enterVanesculasStudy);
		cGetLargeClockHand.addStep(inVanesculasHallway, placeEmblem1OnReceptacle);
		cGetLargeClockHand.addStep(inHallwayNorthOfLobby, enterUnmarkedNorthDoor);
		cGetLargeClockHand.addStep(inLobbyF1, enterIntoHallwayNorthOfLobbyF1);
		cGetLargeClockHand.addStep(inLobbyF0, climbUpToLobbyF1);
		cGetLargeClockHand.addStep(inHallwayEastOfExplosiveRoom, enterNorthDoorInHallwayNextToExplosiveRoom);
		cGetLargeClockHand.addStep(and(inExplosiveRoom, explosiveBarrel, drakanEmblem1), exitEastExplosiveRoom);
		cGetLargeClockHand.addStep(and(inDrakanEmblemRoomSouthOfExplosiveRoom, drakanEmblem1), exitEmblemRoom1);
		cGetLargeClockHand.addStep(inDrakanEmblemRoomSouthOfExplosiveRoom, searchCrateForDrakanEmblem1);
		cGetLargeClockHand.addStep(and(inExplosiveRoom, explosiveBarrel), enterSouthDoorFromExplosiveRoom);
		cGetLargeClockHand.addStep(inExplosiveRoom, pickUpExplosiveBarrel);
		cGetLargeClockHand.addStep(and(tinderbox, inStorageRoom), goUpFromStorageRoom);
		cGetLargeClockHand.addStep(inStorageRoom, pickUpTinderbox);
		cGetLargeClockHand.addStep(inHallwayWestOfDiningRoom, enterWestRoomFromHallwayWestOfDiningRoom);
		cGetLargeClockHand.addStep(inDiningRoom, enterHallwayWestOfDiningRoom);
		cGetLargeClockHand.addStep(inThroneRoom, enterDiningRoomFromThroneRoom);
		cGetLargeClockHand.addStep(inRoomSouthOfThroneRoom, enterThroneRoomFromRoomSouthOfThroneRoom);

		// TODO: is this the correct hand?

		returnToVanesculasHallway = new ObjectStep(this, 61573, new WorldPoint(2474, 7372, 0), "Return to the dining room.");
		returnToHallwayNorthOfLobbyF1 = new ObjectStep(this, 61573, new WorldPoint(2450, 7402, 0), "Return to the dining room, avoiding the traps on the floor.");
		returnToHallwayNorthOfLobbyF1.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2457, 7409, 0),
			new WorldPoint(2457, 7410, 0),
			new WorldPoint(2456, 7409, 0),
			new WorldPoint(2456, 7410, 0),
			new WorldPoint(2461, 7408, 0),
			new WorldPoint(2461, 7409, 0),
			new WorldPoint(2460, 7408, 0),
			new WorldPoint(2460, 7409, 0)
		);

		returnToLobbyF1 = new ObjectStep(this, 61576, new WorldPoint(2457, 7417, 0), "Return to the dining room.");
		climbDownstairsToLobbyF0 = new ObjectStep(this, 61608, new WorldPoint(2315, 7371, 1), "Return to the dining room.");
		enterDiningRoomFromLobby = new ObjectStep(this, 61577, new WorldPoint(2327, 7360, 0), "Return to the dining room.");

		cGetBackToDiningRoom = new ConditionalStep(this, todo);
		cGetBackToDiningRoom.addStep(inLobbyF0, enterDiningRoomFromLobby);
		cGetBackToDiningRoom.addStep(inLobbyF1, climbDownstairsToLobbyF0);
		cGetBackToDiningRoom.addStep(inHallwayNorthOfLobby, returnToLobbyF1);
		cGetBackToDiningRoom.addStep(inVanesculasHallway, returnToHallwayNorthOfLobbyF1);
		cGetBackToDiningRoom.addStep(inVanesculasStudy, returnToVanesculasHallway);

		placeSmallClockHandOnWesternClock = new ObjectStep(this, 61661, new WorldPoint(2344, 7372, 0), "Place the small clock hand on the western Grandfather clock.", smallClockHand.highlighted());


		var solveWesternClock = new DrakanClockSolver(this, 15514, 11, 15513, 9);
		solveWesternClockPW = solveWesternClock.puzzleWrapStepWithDefaultText("Put the clock hands in the right orientation.");
		clickWesternClock = new ObjectStep(this, 61658, new WorldPoint(2344, 7372, 0), "Click the western clock");

		placeLargeClockHandOnEasternClock = new ObjectStep(this, 61660, new WorldPoint(2350, 7372, 0), "Place the large clock hand on the eastern Grandfather clock.", largeClockHand.highlighted());

		var solveEasternClock = new DrakanClockSolver(this, 15517, 0, 15515, 4);
		solveEasternClockPW = solveEasternClock.puzzleWrapStepWithDefaultText("Put the clock hands in the right orientation.");
		clickEasternClock = new ObjectStep(this, 61658, new WorldPoint(2350, 7372, 0), "Click the eastern clock");

		closeClock = new WidgetStep(this, "Close the clock.", 963, 16);
		closeEasternClock = new WidgetStep(this, "Close the clock.", 963, 16);

		getEmblem2FromFireplace = new ObjectStep(this, 61662, new WorldPoint(2347, 7372, 0), "Get the emblem from the Fireplace in the dining room.");
		getEmblem2FromFireplace.addDialogStep("Search the fireplace.");
		cSolveClockPuzzle = new ConditionalStep(this, getEmblem2FromFireplace, "Solve the clock puzzle");
		cSolveClockPuzzle.addStep(smallClockHandNeedsReplacing, placeSmallClockHandOnWesternClock);
		cSolveClockPuzzle.addStep(and(westernClockNeedsFixing, playerAtWesternClock, clockWidgetOpen), solveWesternClock);
		cSolveClockPuzzle.addStep(and(playerAtWesternClock, clockWidgetOpen), closeClock);
		cSolveClockPuzzle.addStep(westernClockNeedsFixing, clickWesternClock);
		cSolveClockPuzzle.addStep(largeClockHandNeedsReplacing, placeLargeClockHandOnEasternClock);
		cSolveClockPuzzle.addStep(and(easternClockNeedsFixing, playerAtEasternClock, clockWidgetOpen), solveEasternClock);
		cSolveClockPuzzle.addStep(and(playerAtEasternClock, clockWidgetOpen), closeEasternClock);
		cSolveClockPuzzle.addStep(easternClockNeedsFixing, clickEasternClock);

		var cmkToLobby = new ObjectStep(this, 61573, new WorldPoint(2342, 7373, 0), "Head back to Vanescula's study.", explosiveBarrel, tinderbox);
		var cmkToLobbyF1 = new ObjectStep(this, 61599, new WorldPoint(2318, 7371, 0), "Head back to Vanescula's study.", explosiveBarrel, tinderbox);
		var cmkToHallway1 = new ObjectStep(this, 61572, new WorldPoint(2314, 7382, 1), "Head back to Vanescula's study.", explosiveBarrel, tinderbox);
		cmkToVanesculasHallwayFromHallwayNorthOfLobby = new ObjectStep(this, 61576, new WorldPoint(2466, 7422, 0), "Head back to Vanescula's study.", explosiveBarrel, tinderbox);
		var cmkToVanesculasStudy = new ObjectStep(this, 61576, new WorldPoint(2468, 7407, 0), "Enter the door to Vanescula's study, avoiding the traps on the floor.", explosiveBarrel, tinderbox);
		cmkToVanesculasStudy.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2457, 7409, 0),
			new WorldPoint(2457, 7410, 0),
			new WorldPoint(2456, 7409, 0),
			new WorldPoint(2456, 7410, 0),
			new WorldPoint(2461, 7408, 0),
			new WorldPoint(2461, 7409, 0),
			new WorldPoint(2460, 7408, 0),
			new WorldPoint(2460, 7409, 0)
		);
		var cmkPlaceEmblemInVanesculasHallway = new ObjectStep(this, 61638, new WorldPoint(2469, 7408, 0), "Place the emblem in the receptacle in Vanescula's hallway.", anyOneEmblemHighlighted);
		cmkToVanesculasHallwayFromHallwayNorthOfLobby.addSubSteps(cmkToLobby, cmkToLobbyF1, cmkToHallway1, cmkToVanesculasStudy, cmkPlaceEmblemInVanesculasHallway);
		cmkPlaceEmblemInVanesculasStudy = new ObjectStep(this, 61638, new WorldPoint(2476, 7367, 0), "Place the emblem in Vanescula's study", anyOneEmblemHighlighted);
		cmkToVanesculasChamber = new ObjectStep(this, 61572, new WorldPoint(2477, 7366, 0), "Enter Vanescula's chamber.", explosiveBarrel, tinderbox);
		cmkBlowUpWallInVanesculasChamber = new ObjectStep(this, 61613, new WorldPoint(2492, 7364, 0), "Place the explosive barrel on the cracked wall in Vanescula's chamber.", explosiveBarrel.highlighted(), tinderbox);
		cmkEnterThroughHole = new ObjectStep(this, 61614, new WorldPoint(2492, 7364, 0), "Enter hole in the wall.");
		cmkTakeEmblem3 = new ObjectStep(this, 61639, new WorldPoint(2486, 7421, 0), "Remove the emblem from the room with the Venator.");
		cmkLeaveVenatorRoom = new ObjectStep(this, 61614, new WorldPoint(2482, 7412, 0), "Leave the venator room through the hole in the wall.");
		cmkLeaveVanesculasChamber = new ObjectStep(this, 61573, new WorldPoint(2483, 7368, 0), "Leave Vanescula's chamber.", drakanEmblem3);
		cmkTakeEmblemFromVanesculasStudy = new ObjectStep(this, 61639, new WorldPoint(2476, 7367, 0), "Take emblem from vanescula's study", drakanEmblem3);
		cmkLeaveVanesculasStudy = new ObjectStep(this, 61573, new WorldPoint(2474, 7372, 0), "Leave vanescula's study", anyTwoEmblemHighlighted);
		cmkRetrieveThirdEmblem = new ObjectStep(this, 61634, new WorldPoint(2469, 7408, 0), "Remove the third emblem from the receptacle.");
		cmkPutEmblemInEastDoor = new ObjectStep(this, 61636, new WorldPoint(2476, 7410, 0), "Place an emblem in the empty receptacle by the east wall.", anyThreeEmblemHighlighted);
		cmkEnterEastDoor = new ObjectStep(this, 61572, new WorldPoint(2477, 7409, 0), "Enter the door to Ranis' hallway.", anyTwoEmblemHighlighted);
		cmkPutEmblemInRanisHallwayNorth = new ObjectStep(this, 61635, new WorldPoint(2486, 7404, 0), "Place an emblem in the empty receptacle at the north door.", anyTwoEmblemHighlighted);
		cmkEnterRanisParlour = new ObjectStep(this, 61576, new WorldPoint(2485, 7405, 0), "Enter Ranis' parlour");
		cmkGetSkull = new DetailedQuestStep(this, new WorldPoint(2471, 7384, 0), "Get the ornate skull from the table in the room.", ornateSkull);
		cmkLeaveRanisParlourRoom = new ObjectStep(this, 61577, new WorldPoint(2475, 7379, 0), "Leave Ranis' parlour room", ornateSkull);
		cmkRemoveEmblemRanisNorth = new ObjectStep(this, 61635, new WorldPoint(2486, 7404, 0), "Remove emblem from the receptacle.", ornateSkull);
		cmkClimbDownStairsRanisHallway = new ObjectStep(this, 61604, new WorldPoint(2491, 7402, 0), "Climb down the stairs in Ranis' hallway.", ornateSkull, anyTwoEmblemHighlighted);
		cmkPlaceEmblemDownstairs = new ObjectStep(this, 61632, new WorldPoint(2371, 7410, 0), "Place an emblem in the receptacle to the west, avoiding the traps on the floor.", ornateSkull, anyTwoEmblemHighlighted);
		cmkPlaceEmblemDownstairs.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2378, 7411, 0),
			new WorldPoint(2379, 7411, 0),
			new WorldPoint(2379, 7412, 0),
			new WorldPoint(2378, 7412, 0)
		);
		cmkEnterWestChapelHallway = new ObjectStep(this, 61572, new WorldPoint(2372, 7409, 0), "Enter the west chapel hallway.", ornateSkull, anyOneEmblemHighlighted);
		cmkPutEmblemInWestChapelHallway = new ObjectStep(this, 61631, new WorldPoint(2370, 7383, 0), "Place an emblem in the empty receptacle by the western door.", anyOneEmblemHighlighted);
		cmkEnterEmblemGallery = new ObjectStep(this, 61572, new WorldPoint(2369, 7384, 0), "Enter the emblem gallery.");
		cmkTalkToVeliaf = new NpcStep(this, 15878, new WorldPoint(2379, 7367, 0), "Talk to Veliaf Hurtz in the emblem gallery.");
		cmkOpenEmblemGalleryChest = new ObjectStep(this, 61681, new WorldPoint(2379, 7372, 0), "Search the chest in the northern part of the emblem gallery where you talked to Veliaf Hurtz.");
		// TODO: puzzle wrap?
		cmkArrowChestPuzzleStep = new ArrowChestPuzzleStep(this);
		cmkArrowChestPuzzleStep.setSolution(1, 2, 1, 3, 3);
		cmkArrowChestPuzzleStepPW = cmkArrowChestPuzzleStep.puzzleWrapStepWithDefaultText("Solve the chest puzzle.");
		cmkGetTheKeyFromTheChest = new ObjectStep(this, 61681, new WorldPoint(2379, 7372, 0), "Search the chest in the emblem gallery in Castle Drakan where you first spoke with Veliaf for the Crescent moon key. You will need 3 emblems to get all the way there again.");

		cGetCrescentMoonKey = new ConditionalStep(this, cmkGetTheKeyFromTheChest, "\nGet the Crescent Moon Key.");
		cGetCrescentMoonKey.addStep(cmkSolvedChestPuzzle, cmkGetTheKeyFromTheChest);
		cGetCrescentMoonKey.addStep(and(inEmblemGallery, cmkHasSpokenWithVeliaf, inArrowPuzzle), cmkArrowChestPuzzleStepPW);
		cGetCrescentMoonKey.addStep(and(inEmblemGallery, cmkHasSpokenWithVeliaf), cmkOpenEmblemGalleryChest);
		cGetCrescentMoonKey.addStep(and(inEmblemGallery), cmkTalkToVeliaf);
		cGetCrescentMoonKey.addStep(and(inWestChapelHallway, anyEmblemInWestChapelHallway), cmkEnterEmblemGallery);
		cGetCrescentMoonKey.addStep(and(inWestChapelHallway), cmkPutEmblemInWestChapelHallway);
		cGetCrescentMoonKey.addStep(and(inNorthChapelHallway, anyEmblemInNorthChapelHallway), cmkEnterWestChapelHallway);
		cGetCrescentMoonKey.addStep(and(inNorthChapelHallway), cmkPlaceEmblemDownstairs);
		cGetCrescentMoonKey.addStep(and(inRanisHallway, noEmblemInRanisHallwayNorth, ornateSkull, anyTwoEmblemHighlighted), cmkClimbDownStairsRanisHallway);
		cGetCrescentMoonKey.addStep(and(inRanisHallway, anyEmblemInRanisHallwayNorth, ornateSkull), cmkRemoveEmblemRanisNorth);
		cGetCrescentMoonKey.addStep(and(inRanisParlour, ornateSkull), cmkLeaveRanisParlourRoom);
		cGetCrescentMoonKey.addStep(and(inRanisParlour), cmkGetSkull);
		cGetCrescentMoonKey.addStep(and(inRanisHallway, anyEmblemInRanisHallwayNorth), cmkEnterRanisParlour);
		cGetCrescentMoonKey.addStep(and(inRanisHallway), cmkPutEmblemInRanisHallwayNorth);
		cGetCrescentMoonKey.addStep(and(inVanesculasHallway, anyEmblemInVanesculasHallwayEast), cmkEnterEastDoor);
		cGetCrescentMoonKey.addStep(and(inVanesculasHallway, anyThreeEmblemHighlighted), cmkPutEmblemInEastDoor);
		cGetCrescentMoonKey.addStep(and(inVanesculasHallway, anyTwoEmblemHighlighted), cmkRetrieveThirdEmblem);
		cGetCrescentMoonKey.addStep(and(inVanesculasStudy, anyTwoEmblemHighlighted), cmkLeaveVanesculasStudy);
		cGetCrescentMoonKey.addStep(and(inVanesculasStudy, drakanEmblem3), cmkTakeEmblemFromVanesculasStudy);
		cGetCrescentMoonKey.addStep(and(inVanesculasChamber, drakanEmblem3), cmkLeaveVanesculasChamber);
		cGetCrescentMoonKey.addStep(and(inVenatorRoom, drakanEmblem3), cmkLeaveVenatorRoom);
		cGetCrescentMoonKey.addStep(inVenatorRoom, cmkTakeEmblem3);
		cGetCrescentMoonKey.addStep(and(inVanesculasChamber, vanesculasChamberWallDestroyed), cmkEnterThroughHole);
		cGetCrescentMoonKey.addStep(inVanesculasChamber, cmkBlowUpWallInVanesculasChamber);
		cGetCrescentMoonKey.addStep(and(inVanesculasStudy, placedEmblemInVanesculasStudy), cmkToVanesculasChamber);
		cGetCrescentMoonKey.addStep(inVanesculasStudy, cmkPlaceEmblemInVanesculasStudy);
		cGetCrescentMoonKey.addStep(and(inVanesculasHallway, placedEmblemInVanesculasHallway), cmkToVanesculasStudy);
		cGetCrescentMoonKey.addStep(inVanesculasHallway, cmkPlaceEmblemInVanesculasHallway);
		cGetCrescentMoonKey.addStep(inHallwayNorthOfLobby, cmkToVanesculasHallwayFromHallwayNorthOfLobby);
		cGetCrescentMoonKey.addStep(inLobbyF1, cmkToHallway1);
		cGetCrescentMoonKey.addStep(inLobbyF0, cmkToLobbyF1);
		cGetCrescentMoonKey.addStep(inDiningRoom, cmkToLobby);


		enterEmblemGalleryHallway = new ObjectStep(this, 61593, new WorldPoint(2370, 7370, 0), "Leave the emblem gallery using the western door.");
		enterRoomBehindLobby = new ObjectStep(this, 61593, new WorldPoint(2325, 7403, 0), "Enter the south-west room, ready to kill a few bugs.");
		killBugsAndTakeSyringeBarrel = new DetailedQuestStep(this, new WorldPoint(2315, 7418, 0), "Kill the bugs and take the Syringe barrel.", syringeBarrel);
		searchCrateForVenatorStomach = new ObjectStep(this, 61751, new WorldPoint(2315, 7408, 0), "Search the create in the south-east corner for a venator stomach.", syringeBarrel);
		retrievePlugFromTheSinkToTheWest = new ObjectStep(this, 61719, new WorldPoint(2308, 7411, 0), "Retrieve the plug from the sink to the west.", syringeBarrel, venatorStomach);
		retrievePlugFromTheSinkToTheWest.addDialogStep("Remove the plug.");
		enterLarder = new ObjectStep(this, 61576, new WorldPoint(2309, 7421, 0), "Enter the larder through the north-west door.");
		useSinkPlugOnSinkInLarder = new ObjectStep(this, 61722, new WorldPoint(2356, 7400, 0), "Put the sink plug into the sink.", sinkPlug.highlighted());
		turnSinkTapOn = new ObjectStep(this, 61722, new WorldPoint(2356, 7400, 0), "Turn the tap on in the sink.");
		turnSinkTapOn.addDialogStep("Turn the tap on.");
		takePipe = new ObjectStep(this, 61722, new WorldPoint(2356, 7400, 0), "Take the pipe from the now-broken sink.");
		takePipe.addDialogStep("Yes.");
		reachBehindCabinetWithPipe = new ObjectStep(this, 61726, new WorldPoint(2360, 7403, 0), "Use the broken pipe on the cabinet to reach behind it.", brokenPipe.highlighted());
		searchLarderCrateForSyringePlunger = new ObjectStep(this, 61751, new WorldPoint(2358, 7397, 0), "Search the crate for a syringe plunger.");
		leaveLarder = new ObjectStep(this, 61577, new WorldPoint(2361, 7393, 0), "Return to the Kitchen.");
		useSharpKnifeOnVenatorCorpse = new ObjectStep(this, 61727, new WorldPoint(2311, 7417, 0), "Use the sharp kitchen knife on the venator corpse.", sharpKitchenKnife.highlighted());
		getTongsFromVenatorCorpse = new ObjectStep(this, 61727, new WorldPoint(2311, 7417, 0), "Search the venator corpse for tongs.");
		getNeedleFromSink = new ObjectStep(this, 61719, new WorldPoint(2308, 7411, 0), "Use the tongs on the sink to get a syringe needle.", tongs.highlighted());
		assembleSyringe = new DetailedQuestStep(this, "Combine the syringe parts to assemble a syringe.", syringeBarrel.highlighted(), syringePlunger.highlighted(), syringeNeedle.highlighted());
		drawBloodFromVenatorStomach = new DetailedQuestStep(this, "Use the empty syringe on the venator stomach in your inventory to draw blood from it.", emptySyringe.highlighted(), venatorStomach.highlighted());
		useSyringeOnChest = new ObjectStep(this, 61683, new WorldPoint(2308, 7415, 0), "Use the full syringe on the chest to receive the left crest half.", fullSyringe.highlighted());
		getLeftCrestHalfFromKitchenChest = new ObjectStep(this, 61683, new WorldPoint(2308, 7415, 0), "Search the chest in the kitchen for the left crest half.");
		getLeftCrestHalfFromKitchenChest.addDialogStep("Yes.");

		leaveKitchen = new ObjectStep(this, 61592, new WorldPoint(2314, 7421, 0), "Leave the kitchen and head to the lobby.", leftHalfOfCrest);
		enterLobbyFromEmblemGalleryHallway = new ObjectStep(this, 61577, new WorldPoint(2330, 7407, 0), "Enter the lobby.", leftHalfOfCrest);
		climbDownstairsToLobbyBasement = new ObjectStep(this, 61606, new WorldPoint(2311, 7373, 0), "Climb down to the basement.", leftHalfOfCrest);
		enterAnotherVenatorRoom = new ObjectStep(this, 61593, new WorldPoint(2570, 7365, 0), "Enter Crescent moon door to your east, ready to kill another Venator. Turn off your protection prayer when it's about to screech.", leftHalfOfCrest);
		// I could technically use this varp to see if the venator in that room is dead
		// [2026-07-05T13:27:48Z 5913] varp CASTLE_DRAKAN_ENEMY_STATUS_2 (5641) 32830 -> 98366
		killVenator = new NpcStep(this, 16217, new WorldPoint(2569, 7384, 0), "Kill the Venator. Remove your protection prayer when it's about to screech.");

		// NOTE: This would be very annoying to puzzlewrap, so I'm not doing it. Feel free to change it in a future PR!
		searchWeaponRackForOneAxe = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for one battleaxe.");
		searchWeaponRackForOneAxe.addDialogStep("Take a battleaxe.");
		placeBattleAxeOnStatue = new ObjectStep(this, 61743, new WorldPoint(2577, 7380, 0), "Place the battleaxe on the east-most empty statue.", battleAxe.highlighted());
		getMace1 = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for a mace.");
		getMace1.addDialogStep("Take a mace.");
		getMace2 = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for two maces.");
		getMace2.addDialogStep("Take a mace.");
		getMace2.addSubSteps(getMace1);
		getSword = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for a sword.");
		getSword.addDialogStep("Take a sword.");

		placeMaceOnStatueN = new ObjectStep(this, 61746, new WorldPoint(2569, 7386, 0), "Place a mace on the second pair of statues from the west.", mace.highlighted());
		placeMaceOnStatueS = new ObjectStep(this, 61746, new WorldPoint(2569, 7380, 0), "Place a mace on the second pair of statues from the west.", mace.highlighted());

		placeSwordOnStatue = new ObjectStep(this, 61746, new WorldPoint(2565, 7386, 0), "Place a sword on the western empty statue.", sword.highlighted());


		openWeaponPuzzleChest = new ObjectStep(this, 61685, new WorldPoint(2570, 7380, 0), "Search the chest for the right half of a crest.");

		combineCrests = new DetailedQuestStep(this, "Combine the two pieces of crests in your inventory.", leftHalfOfCrest.highlighted(), rightHalfOfCrest.highlighted());

		leaveWithFullCrest1 = new ObjectStep(this, 61592, new WorldPoint(2566, 7387, 0), "Head to the study, north of the throne room.", fullCrest);
		leaveWithFullCrest2 = new ObjectStep(this, 61600, new WorldPoint(2564, 7362, 0), "Head to the study, north of the throne room.", fullCrest);
		leaveWithFullCrest3 = new ObjectStep(this, 61577, new WorldPoint(2327, 7360, 0), "Head to the study, north of the throne room.", fullCrest);
		leaveWithFullCrest4 = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 0), "Head to the study, north of the throne room.", fullCrest);
		leaveWithFullCrest5 = new ObjectStep(this, 61576, new WorldPoint(2309, 7397, 0), "Head to the study, north of the throne room.", fullCrest);
		leaveWithFullCrest5.addSubSteps(leaveWithFullCrest1, leaveWithFullCrest2, leaveWithFullCrest3, leaveWithFullCrest4);
		putFullCrestOnFireplaceInStudy = new ObjectStep(this, 61665, new WorldPoint(2358, 7386, 0), "Place the full crest on the fireplace in the study, north of the throne room.", fullCrest.highlighted());

		getNewMoonKeyFromFireplace = new ObjectStep(this, 61665, new WorldPoint(2358, 7386, 0), "Search the fireplace for the new moon key in the study, north of the throne room.");
		getNewMoonKeyFromFireplace.addDialogStep("Yes.");

		cGetNewMoonKey = new ConditionalStep(this, getNewMoonKeyFromFireplace, "\nGet the new moon key.");
		cGetNewMoonKey.addStep(fullCrestInStudy, getNewMoonKeyFromFireplace);
		cGetNewMoonKey.addStep(and(inStudy, fullCrest), putFullCrestOnFireplaceInStudy);
		cGetNewMoonKey.addStep(and(inThroneRoom, fullCrest), leaveWithFullCrest5);
		cGetNewMoonKey.addStep(and(inDiningRoom, fullCrest), leaveWithFullCrest4);
		cGetNewMoonKey.addStep(and(inLobbyF0, fullCrest), leaveWithFullCrest3);
		cGetNewMoonKey.addStep(and(inLobbyBasementHallway, fullCrest), leaveWithFullCrest2);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, fullCrest), leaveWithFullCrest1);

		cGetNewMoonKey.addStep(and(leftHalfOfCrest, rightHalfOfCrest), combineCrests);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, venatorAlive), killVenator);

		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, doneWithWeaponPuzzle), openWeaponPuzzleChest);

		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutAxeOnStatue, battleAxe), placeBattleAxeOnStatue);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutAxeOnStatue), searchWeaponRackForOneAxe);

		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, needToPutMaceOnStatue2, mace2), placeMaceOnStatueN);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, needToPutMaceOnStatue2, mace), getMace2);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, needToPutMaceOnStatue2), getMace2);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue2, mace), placeMaceOnStatueN);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, mace), placeMaceOnStatueS);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, or(needToPutMaceOnStatue1, needToPutMaceOnStatue2)), getMace1);

		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutSwordOnStatue, sword), placeSwordOnStatue);
		cGetNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutSwordOnStatue), getSword);

		cGetNewMoonKey.addStep(and(inLobbyBasementHallway, leftHalfOfCrest), enterAnotherVenatorRoom);
		cGetNewMoonKey.addStep(and(inLobbyF0, leftHalfOfCrest), climbDownstairsToLobbyBasement);
		cGetNewMoonKey.addStep(and(inEmblemGalleryHallway, leftHalfOfCrest), enterLobbyFromEmblemGalleryHallway);

		cGetNewMoonKey.addStep(and(inKitchen, leftHalfOfCrest), leaveKitchen);
		cGetNewMoonKey.addStep(and(inKitchen, unlockedKitchenChest), getLeftCrestHalfFromKitchenChest);
		cGetNewMoonKey.addStep(and(inKitchen, fullSyringe), useSyringeOnChest);
		cGetNewMoonKey.addStep(and(inKitchen, venatorStomach, emptySyringe), drawBloodFromVenatorStomach);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, syringePlunger, syringeNeedle), assembleSyringe);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, syringePlunger, tongs), getNeedleFromSink);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, syringePlunger, hasCutVenatorStomachUp), getTongsFromVenatorCorpse);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, sharpKitchenKnife, syringePlunger), useSharpKnifeOnVenatorCorpse);
		cGetNewMoonKey.addStep(and(inLarder, sharpKitchenKnife, syringePlunger), leaveLarder);
		cGetNewMoonKey.addStep(and(inLarder, sharpKitchenKnife), searchLarderCrateForSyringePlunger);
		cGetNewMoonKey.addStep(and(inLarder, brokenPipe), reachBehindCabinetWithPipe);
		cGetNewMoonKey.addStep(and(inLarder, larderSinkCollapsed), takePipe);
		cGetNewMoonKey.addStep(and(inLarder, larderSinkPlugged), turnSinkTapOn);
		cGetNewMoonKey.addStep(and(inLarder, larderSinkNeedsPlugging), useSinkPlugOnSinkInLarder);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, sinkPlug), enterLarder);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach), retrievePlugFromTheSinkToTheWest);
		cGetNewMoonKey.addStep(and(inKitchen, syringeBarrel), searchCrateForVenatorStomach);
		cGetNewMoonKey.addStep(inKitchen, killBugsAndTakeSyringeBarrel);
		cGetNewMoonKey.addStep(inEmblemGalleryHallway, enterRoomBehindLobby);
		cGetNewMoonKey.addStep(inEmblemGallery, enterEmblemGalleryHallway);


		ggkGoToThroneRoom = new ObjectStep(this, 61576, new WorldPoint(2358, 7380, 0), "Return to the basement.");
		ggkGoToDiningRoom = new ObjectStep(this, 61573, new WorldPoint(2304, 7392, 0), "Return to the basement.");
		ggkGoToLobby1 = new ObjectStep(this, 61573, new WorldPoint(2342, 7373, 0), "Return to the basement.");
		ggkGoToBasement = new ObjectStep(this, 61606, new WorldPoint(2311, 7366, 0), "Return to the basement.");
		ggkGoToBasement.addSubSteps(ggkGoToThroneRoom, ggkGoToDiningRoom, ggkGoToLobby1);
		ggkEnterBasementNorthRoom = new ObjectStep(this, 61584, new WorldPoint(2570, 7369, 0), "Enter through the new moon door to the north to free Safalaan and Vanescula.");
		ggkWatchTheCutscene = ggkEnterBasementNorthRoom.cutscene();
		ggkTakeGibbousMoonKey = new DetailedQuestStep(this, new WorldPoint(2573, 7395, 0), "Take the gibbous moon key from the bench.", gibbousMoonKey);
		ggkReturnToBasementHallway = new ObjectStep(this, 61585, new WorldPoint(2566, 7394, 0), "Leave the prison.");
		ggkReturnToLobbyF0 = new ObjectStep(this, 61600, new WorldPoint(2564, 7369, 0), "Climb up the stairs to the ground floor lobby.");
		ggkClimbUpToLobbyF1 = new ObjectStep(this, 61599, new WorldPoint(2315, 7370, 0), "Climb up the stairs to the first floor of the lobby.");
		ggkEnterLobbyF1GibbousMoonDoor = new ObjectStep(this, 61588, new WorldPoint(2327, 7360, 1), "Enter the gibbous moon door to the south-east.");
		ggkEnterEastDoor = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 1), "Enter the door to the east.");
		ggkEnterSouthDoor = new ObjectStep(this, 61572, new WorldPoint(2306, 7386, 1), "Enter the door to the south.");
		ggkEnterSouthEastDoor = new ObjectStep(this, 61572, new WorldPoint(2442, 7361, 0), "Enter the south-east door, avoiding the traps on the floor.");
		ggkEnterSouthEastDoor.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2438, 7363, 0),
			new WorldPoint(2438, 7364, 0),
			new WorldPoint(2439, 7363, 0),
			new WorldPoint(2439, 7364, 0)
		);
		ggkGetOrnateKnife = new ObjectStep(this, 61751, new WorldPoint(2454, 7372, 0), "Search the eastern crate for an ornate knife.");
		ggkPullLeverInOrnateKnifeRoom = new ObjectStep(this, 61776, new WorldPoint(2449, 7371, 0), "Pull the lever to the west.");


		ggkEnterUpperStoreroomPortal = new ObjectStep(this, 61771, new WorldPoint(2450, 7372, 0), "Enter the portal to the guest chamber storeroom.");


		ggkLeaveGuestChamberStoreroom = new ObjectStep(this, 61589, new WorldPoint(2522, 7397, 0), "Leave the guest chamber storeroom.");

		ggkEnterRoomWithVenator = new ObjectStep(this, 61588, new WorldPoint(2510, 7370, 0), "Enter Gibbous moon door, ready to fight another Venator.");
		ggkKillVenator = new NpcStep(this, new int[]{NpcID.CASTLE_DRAKAN_VENATOR, NpcID.CASTLE_DRAKAN_VENATOR_2, NpcID.CASTLE_DRAKAN_VENATOR_3, NpcID.CASTLE_DRAKAN_VENATOR_4, NpcID.CASTLE_DRAKAN_VENATOR_5}, new WorldPoint(2522, 7368, 0), "Kill the Venator.");


		ggkpLightFireplace = new ObjectStep(this, 61730, new WorldPoint(2521, 7371, 0), "Light the fireplace.", tinderbox);
		ggkpSearchChest = new ObjectStep(this, 61734, new WorldPoint(2526, 7371, 0), "Search the chest.");

		ggkpTryOpenLockbox = new DetailedQuestStep(this, "Open the small lockbox", smallLockbox.highlighted());

		ggkpSolveLockboxPuzzle = new ChestCodeStep(this, "small lockbox", "⠿ ᴟ ⁘", 10, 2, 3, 7);
		ggkpSolveLockboxPuzzlePW = ggkpSolveLockboxPuzzle.puzzleWrapStepWithDefaultText("Solve the lockbox puzzle.");


		ggkpPlaceFancyGemInHead = new ObjectStep(this, 61687, new WorldPoint(2531, 7367, 0), "Place the fancy gem on the venator head.", fancyGem1.highlighted());

		ggkpSolveDoorPuzzle = new ChestCodeStep(this, "door", "SPEAR", 10, 1, 1, 1, 3, 0);
		ggkpSolveDoorPuzzlePW = ggkpSolveDoorPuzzle.puzzleWrapStepWithDefaultText("Solve the door puzzle.");

		ggkpEnterSouthWestDoor = new ObjectStep(this, 61625, new WorldPoint(2519, 7363, 0), "Enter the south-west door.");

		ggkSolvePuzzle = new ConditionalStep(this, ggkpLightFireplace, "\nSolve the room puzzle.", ornateSkull, ornateKnife);


		ggkpSearchBookcaseForBook = new ObjectStep(this, 61757, new WorldPoint(2549, 7381, 0), "Search the south-western bookcase.");


		ggkpOpenMysteriousBook = new DetailedQuestStep(this, "Open the mysterious book.", mysteriousBook.highlighted());

		ggkpLeaveLibrary = new ObjectStep(this, 61577, new WorldPoint(2551, 7387, 0), "Leave the library.", fancyGem2);

		ggkpPlaceFancyGemInHead2 = new ObjectStep(this, 61687, new WorldPoint(2531, 7367, 0), "Place the fancy gem on the venator head.", fancyGem2.highlighted());


		ggkpSearchVenatorHead = new ObjectStep(this, 61687, new WorldPoint(2531, 7367, 0), "Search the venator head for the ornate hourglass.");
		ggkpSearchVenatorHead.addDialogStep("Yes.");


		ggkpLeavePuzzleRoom = new ObjectStep(this, 61589, new WorldPoint(2515, 7367, 0), "Leave via the western gibbous moon door.", ornateHourglass);

		ggkSolvePuzzle.addStep(and(inVenatorPuzzleRoom, ornateHourglass), ggkpLeavePuzzleRoom);
		ggkSolvePuzzle.addStep(venatorHeadBothEyePlaced, ggkpSearchVenatorHead);
		ggkSolvePuzzle.addStep(and(fancyGem2, inVenatorPuzzleRoom), ggkpPlaceFancyGemInHead2);
		ggkSolvePuzzle.addStep(and(fancyGem2, inVenatorPuzzleRoomLibrary), ggkpLeaveLibrary);
		ggkSolvePuzzle.addStep(mysteriousBook, ggkpOpenMysteriousBook);
		ggkSolvePuzzle.addStep(and(doorPuzzleSolved, inVenatorPuzzleRoomLibrary), ggkpSearchBookcaseForBook);
		ggkSolvePuzzle.addStep(and(playerNextToDoorPuzzle, combinationLockWidgetOpen), ggkpSolveDoorPuzzlePW);
		ggkSolvePuzzle.addStep(venatorHeadOneEyePlaced, ggkpEnterSouthWestDoor);
		ggkSolvePuzzle.addStep(fancyGem1, ggkpPlaceFancyGemInHead);
		ggkSolvePuzzle.addStep(and(smallLockbox, combinationLockWidgetOpen), ggkpSolveLockboxPuzzlePW);
		ggkSolvePuzzle.addStep(smallLockbox, ggkpTryOpenLockbox);
		ggkSolvePuzzle.addStep(isFireplaceLit, ggkpSearchChest);


		// I placed the ornate knife, then hourglass, then skull
		// Varbit went to 0b010 after placing knife
		// Varbit went to 0b110 after placing hourglass
		// Varbit went to 0b111 after placing skull


		ggkPlaceItemsOnDisplayCase = new ObjectStep(this, 61672, new WorldPoint(2497, 7374, 0), "Place the items on the display case", ornateHourglass.highlighted(), ornateKnife.highlighted(), ornateSkull.highlighted());

		ggkSearchDisplayCaseForGildedKey = new ObjectStep(this, 61672, new WorldPoint(2497, 7374, 0), "Search the display case for the gilded key.");
		ggkSearchDisplayCaseForGildedKey.addDialogStep("Yes.");

		cGetGildedAndGibbousKeys = new ConditionalStep(this, ggkSearchDisplayCaseForGildedKey, "\nGet the gilded and gibbous keys.");
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom, allItemsPlacedInDisplayCase), ggkSearchDisplayCaseForGildedKey);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom, venatorHeadBothEyePlaced), ggkPlaceItemsOnDisplayCase);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom), ggkEnterRoomWithVenator);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inVenatorPuzzleRoom, anyVenatorAlive), ggkKillVenator);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, or(inVenatorPuzzleRoom, inVenatorPuzzleRoomLibrary)), ggkSolvePuzzle);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom), ggkEnterRoomWithVenator);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inGuestChamberStoreroom), ggkLeaveGuestChamberStoreroom);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, ornateKnife, inOrnateKnifeRoom, pulledUpperStoreroomLever), ggkEnterUpperStoreroomPortal);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, ornateKnife, inOrnateKnifeRoom), ggkPullLeverInOrnateKnifeRoom);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inOrnateKnifeRoom), ggkGetOrnateKnife);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inHallway5), ggkEnterSouthEastDoor);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inThroneRoomF1), ggkEnterSouthDoor);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inDiningRoomF1), ggkEnterEastDoor);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inLobbyF1), ggkEnterLobbyF1GibbousMoonDoor);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inLobbyF0), ggkClimbUpToLobbyF1);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inLobbyBasementHallway), ggkReturnToLobbyF0);
		cGetGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inBasementPrison), ggkReturnToBasementHallway);
		cGetGildedAndGibbousKeys.addStep(inBasementPrison, ggkTakeGibbousMoonKey);
		cGetGildedAndGibbousKeys.addStep(and(inLobbyBasementHallway, inCutscene), ggkWatchTheCutscene);
		cGetGildedAndGibbousKeys.addStep(inLobbyBasementHallway, ggkEnterBasementNorthRoom);
		cGetGildedAndGibbousKeys.addStep(inLobbyF0, ggkGoToBasement);
		cGetGildedAndGibbousKeys.addStep(inDiningRoom, ggkGoToLobby1);
		cGetGildedAndGibbousKeys.addStep(inThroneRoom, ggkGoToDiningRoom);
		cGetGildedAndGibbousKeys.addStep(inStudy, ggkGoToThroneRoom);


		fmkClimbDownstairsFromDisplayCase = new ObjectStep(this, 61609, new WorldPoint(2503, 7369, 0), "Climb down the stairs to the first floor of the lobby.");
		fmkGoThroughLobbyF1NorthDoor = new ObjectStep(this, 61572, new WorldPoint(2314, 7382, 1), "Enter the northern door.");
		fmkEnterGildedDoorToWest = new ObjectStep(this, 61627, new WorldPoint(2452, 7420, 0), "Enter the gilded door to your west - avoid the bugs.");
		fmkNorthWest = new ObjectStep(this, 61573, new WorldPoint(2441, 7417, 0), "Enter the north-western door, ready to kill another Venator.");


		fmkKillVenator = new NpcStep(this, 16213, new WorldPoint(2456, 7392, 0), "Kill the Venator.");

		fmkTalkToIvan1 = new NpcStep(this, 15855, new WorldPoint(2455, 7388, 0), "Talk to Ivan Strom after killing the Venator.");


		fmkGildedBookPuzzle = new GildedBookPuzzle(this);
		fmkGildedBookPuzzlePW = fmkGildedBookPuzzle.puzzleWrapStepWithDefaultText("Rearrange the books to the correct order.");

		fmkGetGildedBook = new ObjectStep(this, 61756, new WorldPoint(2455, 7395, 0), "Search the northern bookcase for the gilded book.");

		fmkUseGildedBookOnWesternGildedBookcase = new ObjectStep(this, 61692, new WorldPoint(2451, 7391, 0), "Inspect the western bookcase.", gildedBook.highlighted());


		fmkClickGildedBookcase = new ObjectStep(this, 61692, new WorldPoint(2451, 7391, 0), "Click the gilded bookcase to start rearranging the books.");
		fmkClickGildedBookcase.addDialogStep("Yes.");

		fmkEnterSecretPassage = new ObjectStep(this, 61615, new WorldPoint(2455, 7386, 0), "Enter the secret passage to the south.");


		fmkTakeFullMoonKey = new DetailedQuestStep(this, new WorldPoint(2433, 7416, 0), "Take the full moon key from the table.", fullMoonKey.highlighted());

		cGetFullMoonKey = new ConditionalStep(this, fmkTakeFullMoonKey, "\nGet the full moon key.");
		cGetFullMoonKey.addStep(and(inSecretRoom, fmkIvanFollowingYou), fmkTakeFullMoonKey);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, finishedLibraryPuzzle), fmkEnterSecretPassage);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkVenatorAlive), fmkKillVenator);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, startedLibraryPuzzle, gildedBookPuzzleOpen), fmkGildedBookPuzzlePW);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, startedLibraryPuzzle), fmkClickGildedBookcase);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, gildedBook), fmkUseGildedBookOnWesternGildedBookcase);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou), fmkGetGildedBook);
		cGetFullMoonKey.addStep(and(inRoomWithIvanAndVenator), fmkTalkToIvan1);
		cGetFullMoonKey.addStep(inSmallHallway, fmkNorthWest);
		cGetFullMoonKey.addStep(inHallwayNorthOfLobby, fmkEnterGildedDoorToWest);
		cGetFullMoonKey.addStep(inLobbyF1, fmkGoThroughLobbyF1NorthDoor);
		cGetFullMoonKey.addStep(inRoomOutsideGuestChamberStoreroom, fmkClimbDownstairsFromDisplayCase);


		skLeaveEastDoor = new ObjectStep(this, 61572, new WorldPoint(2438, 7419, 0), "Leave the secret room through the eastern door.");
		skLeaveGildedDoor = new ObjectStep(this, 61626, new WorldPoint(2445, 7418, 0), "Enter the north-western gilded door.");
		skEnterScratchedDoor = new ObjectStep(this, 61576, new WorldPoint(2466, 7422, 0), "Enter the scratched door on the northern wall to your east.");

		skEnterEasternDoor = new ObjectStep(this, 61572, new WorldPoint(2477, 7409, 0), "Enter the eastern door, avoiding the traps on the floor.");
		skEnterEasternDoor.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2457, 7409, 0),
			new WorldPoint(2457, 7410, 0),
			new WorldPoint(2456, 7409, 0),
			new WorldPoint(2456, 7410, 0),
			new WorldPoint(2461, 7408, 0),
			new WorldPoint(2461, 7409, 0),
			new WorldPoint(2460, 7408, 0),
			new WorldPoint(2460, 7409, 0)
		);

		skClimbDownStairs = new ObjectStep(this, 61604, new WorldPoint(2491, 7402, 0), "Climb down the stairs in Ranis' hallway.");
		skEnterWesternDoor = new ObjectStep(this, 61572, new WorldPoint(2372, 7409, 0), "Enter the western door, avoiding the traps on the floor.");
		skEnterWesternDoor.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2378, 7411, 0),
			new WorldPoint(2379, 7411, 0),
			new WorldPoint(2379, 7412, 0),
			new WorldPoint(2378, 7412, 0)
		);
		skEnterEastFullMoonDoor = new ObjectStep(this, 61591, new WorldPoint(2373, 7392, 0), "Enter the full moon door to your east.");


		// TODO: Should this use a puzzle wrapper step? Probably, but it will require re-organizing these steps into a conditional step which is _scary_
		// requires 4 inv slots
		skGetCloudyGreyPotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetCloudyGreyPotion.addDialogStep("Take a cloudy grey potion.");
		skGetWeightlessBlackPotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetWeightlessBlackPotion.addDialogStep("Take a weightless black potion.");
		skGetThickRedPotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetThickRedPotion.addDialogStep("Take a thick red potion.");
		skGetColdBluishWhitePotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetColdBluishWhitePotion.addDialogStep("Take a cold bluish-white potion.");
		skGetCloudyGreyPotion.addSubSteps(skGetWeightlessBlackPotion, skGetThickRedPotion, skGetColdBluishWhitePotion);

		smokeBasin = new ObjectStep(this, 61702, new WorldPoint(2380, 7385, 0), "Pour the cloudy grey potion into the south-west basin.", cloudyGreyPotion.highlighted());
		iceBasin = new ObjectStep(this, 61705, new WorldPoint(2387, 7384, 0), "Pour the cold bluish-white potion into the south-east basin.", coldBlueishWhitePotion.highlighted());
		shadowBasin = new ObjectStep(this, 61703, new WorldPoint(2387, 7389, 0), "Pour the weightless black potion into the north-east basin.", weightlessBlackPotion.highlighted());
		bloodBasin = new ObjectStep(this, 61704, new WorldPoint(2380, 7389, 0), "Pour the thick red potion into the north-west basin.", thickRedPotion.highlighted());


		skSearchAltarForAncientSymbol = new ObjectStep(this, 61701, new WorldPoint(2382, 7393, 0), "Search the altar for an ancient symbol.");

		skEnterEastDoorFromBottleRoom = new ObjectStep(this, 61590, new WorldPoint(2388, 7383, 0), "Enter the south-east door.");


		skPullLever = new ObjectStep(this, 61778, new WorldPoint(2393, 7372, 0), "Pull the lever.");


		skEnterPortal = new ObjectStep(this, 61773, new WorldPoint(2393, 7370, 0), "Enter the portal to the servants quarters.");


		skLeaveServantsQuarters = new ObjectStep(this, 61585, new WorldPoint(2438, 7394, 0), "Leave the servants quarters.");

		skLeaveExplosiveRoom = new ObjectStep(this, 61572, new WorldPoint(2444, 7386, 0), "Enter the eastern door.");

		skLeaveExplosiveRoomHallway = new ObjectStep(this, 61572, new WorldPoint(2474, 7398, 0), "Enter northern door to the first floor of the lobby.");

		skLeaveLobbyF1 = new ObjectStep(this, 61588, new WorldPoint(2327, 7360, 1), "Enter the gibbous moon door to the south-east.");

		skLeaveDiningRoomF1 = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 1), "Enter the eastern door.");

		skLeaveThroneRoomF1 = new ObjectStep(this, 61590, new WorldPoint(2306, 7397, 1), "Enter the northern full moon door.");


		skGetAncientShield = new ObjectStep(this, 61751, new WorldPoint(2439, 7378, 0), "Search the crate to the north for the ancient shield.");


		skCombineAncientShieldAndAncientSymbol = new DetailedQuestStep(this, "Combine the ancient shield and ancient symbol.", ancientShield.highlighted(), ancientSymbol.highlighted());


		skLeaveStudy = new ObjectStep(this, 61591, new WorldPoint(2434, 7372, 0), "Leave the study.");

		skLeaveThroneRoomF1Again = new ObjectStep(this, 61577, new WorldPoint(2304, 7392, 1), "Return to the first floor of the lobby.");

		skLeaveDiningRoomF1Again = new ObjectStep(this, 61588, new WorldPoint(2342, 7373, 1), "Return to the first floor of the lobby.");
		skLeaveThroneRoomF1Again.addSubSteps(skLeaveDiningRoomF1Again);

		skLeaveLobbyF1Again = new ObjectStep(this, 61599, new WorldPoint(2308, 7365, 1), "Climb up to the display case room.");

		skEnterGibbousMoonDoor = new ObjectStep(this, 61588, new WorldPoint(2510, 7370, 0), "Enter the eastern gibbous moon door.");

		skEnterFullMoonDoor = new ObjectStep(this, 61590, new WorldPoint(2525, 7363, 0), "Enter the south-eastern full moon door.");


		skUseShieldOnEmptyMount = new ObjectStep(this, 61698, new WorldPoint(2543, 7361, 0), "Use the shield with symbol on the empty mount on the southern wall.", shieldWithSymbol.highlighted());


		skGetSolidKey = new ObjectStep(this, 61698, new WorldPoint(2543, 7361, 0), "Search the mounted shield for the solid key.");
		skGetSolidKey.addDialogStep("Yes.");


		cGetSolidKey = new ConditionalStep(this, skGetSolidKey, "\nGet the solid key.");
		cGetSolidKey.addStep(and(inSolidKeyRoom, hasMountedShield), skGetSolidKey);
		cGetSolidKey.addStep(and(inSolidKeyRoom, shieldWithSymbol), skUseShieldOnEmptyMount);
		cGetSolidKey.addStep(and(inVenatorPuzzleRoom, or(shieldWithSymbol, hasMountedShield)), skEnterFullMoonDoor);
		cGetSolidKey.addStep(and(inRoomOutsideGuestChamberStoreroom, shieldWithSymbol), skEnterGibbousMoonDoor);
		cGetSolidKey.addStep(and(inLobbyF1, shieldWithSymbol), skLeaveLobbyF1Again);
		cGetSolidKey.addStep(and(inDiningRoomF1, shieldWithSymbol), skLeaveDiningRoomF1Again);
		cGetSolidKey.addStep(and(inThroneRoomF1, shieldWithSymbol), skLeaveThroneRoomF1Again);
		cGetSolidKey.addStep(and(inRoomAboveStudy, shieldWithSymbol), skLeaveStudy);
		cGetSolidKey.addStep(and(ancientShield, ancientSymbol), skCombineAncientShieldAndAncientSymbol);
		cGetSolidKey.addStep(and(inRoomAboveStudy, ancientSymbol), skGetAncientShield);
		cGetSolidKey.addStep(and(inThroneRoomF1, ancientSymbol), skLeaveThroneRoomF1);
		cGetSolidKey.addStep(and(inDiningRoomF1, ancientSymbol), skLeaveDiningRoomF1);
		cGetSolidKey.addStep(and(inLobbyF1, ancientSymbol), skLeaveLobbyF1);
		cGetSolidKey.addStep(and(inHallwayEastOfExplosiveRoom, ancientSymbol), skLeaveExplosiveRoomHallway);
		cGetSolidKey.addStep(and(inExplosiveRoom, ancientSymbol), skLeaveExplosiveRoom);
		cGetSolidKey.addStep(and(inServantsQuarters, ancientSymbol), skLeaveServantsQuarters);
		cGetSolidKey.addStep(and(inChapelLibrary, ancientSymbol, openedPortalFromChapelLibraryToServantsQuarters), skEnterPortal);
		cGetSolidKey.addStep(and(inChapelLibrary, ancientSymbol), skPullLever);
		cGetSolidKey.addStep(and(inBottleRoom, ancientSymbol), skEnterEastDoorFromBottleRoom);
		cGetSolidKey.addStep(and(inBottleRoom, solvedAllBasins), skSearchAltarForAncientSymbol);
		cGetSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, solvedIceBasin, not(solvedShadowBasin), weightlessBlackPotion), shadowBasin);
		cGetSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, solvedIceBasin, not(solvedShadowBasin)), skGetWeightlessBlackPotion);
		cGetSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, not(solvedIceBasin), coldBlueishWhitePotion), iceBasin);
		cGetSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, not(solvedIceBasin)), skGetColdBluishWhitePotion);
		cGetSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, not(solvedSmokeBasin), cloudyGreyPotion), smokeBasin);
		cGetSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, not(solvedSmokeBasin)), skGetCloudyGreyPotion);
		cGetSolidKey.addStep(and(inBottleRoom, not(solvedBloodBasin), thickRedPotion, or(cloudyGreyPotion, solvedSmokeBasin), or(weightlessBlackPotion, solvedShadowBasin), or(coldBlueishWhitePotion, solvedIceBasin)), bloodBasin);
		cGetSolidKey.addStep(and(inBottleRoom, not(cloudyGreyPotion)), skGetCloudyGreyPotion);
		cGetSolidKey.addStep(and(inBottleRoom, not(weightlessBlackPotion)), skGetWeightlessBlackPotion);
		cGetSolidKey.addStep(and(inBottleRoom, not(thickRedPotion)), skGetThickRedPotion);
		cGetSolidKey.addStep(and(inBottleRoom, not(coldBlueishWhitePotion)), skGetColdBluishWhitePotion);

		cGetSolidKey.addStep(inWestChapelHallway, skEnterEastFullMoonDoor);
		cGetSolidKey.addStep(inNorthChapelHallway, skEnterWesternDoor);
		cGetSolidKey.addStep(inRanisHallway, skClimbDownStairs);
		// TODO: If the emblem is not there, we should guide the user to place down an emblem.
		cGetSolidKey.addStep(inVanesculasHallway, skEnterEasternDoor);
		cGetSolidKey.addStep(inHallwayNorthOfLobby, skEnterScratchedDoor);
		cGetSolidKey.addStep(inSmallHallway, skLeaveGildedDoor);
		cGetSolidKey.addStep(inSecretRoom, skLeaveEastDoor);


		// if you follow the guide, this is the last position you'll use the key
		dtsLeaveSolidKeyRoom = new ObjectStep(this, 61590, new WorldPoint(2541, 7374, 0), "Leave the room through the full moon door to the north.");
		dtsLeavePuzzleRoom = new ObjectStep(this, 61589, new WorldPoint(2515, 7367, 0), "Leave the room through the gibbous door to the west.");
		dtsLeaveDisplayCaseRoom = new ObjectStep(this, 61590, new WorldPoint(2510, 7379, 0), "Enter the full moon door to the north-east.");


		dtsOpenSolidDoor = new ObjectStep(this, 61575, new WorldPoint(2503, 7400, 0), "Enter the solid door to the east.");


		dtsSearchShelvesForSupplies = new ObjectStep(this, 61708, new WorldPoint(2513, 7387, 0), "Search the shelves for 2 vials of water, 3 vials of blood, and 3 pure essence. You can destroy all keys, the tinderbox, and the pickaxe to make room in your inventory.", vialsOfWater2, vialsOfBlood3, pureEssence3);
		dtsSearchShelvesForSuppliesWater = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesWater.addDialogStep("Take a vial of water.");
		dtsSearchShelvesForSuppliesBlood = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesBlood.addDialogStep("Take a vial of blood.");
		dtsSearchShelvesForSuppliesEssence = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesEssence.addDialogStep("Take a piece of pure essence.");


		makeFirstVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", vialOfBlood.highlighted(), pureEssence.highlighted());
		makeFirstVialEssence = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", pureEssence.highlighted());
		makeFirstVialBlood = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", vialOfBlood.highlighted());

		makeSecondVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", vialOfBlood.highlighted(), pureEssence.highlighted());
		makeSecondVialEssence = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", pureEssence.highlighted());
		makeSecondVialBlood = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", vialOfBlood.highlighted());

		makeThirdVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfBlood.highlighted(), vialOfWater.highlighted());
		makeThirdVialBlood = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfBlood.highlighted());
		makeThirdVialWater = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfWater.highlighted());

		makeFourthVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", pureEssence.highlighted(), vialOfWater.highlighted());
		makeFourthVialEssence = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", pureEssence.highlighted());
		makeFourthVialWater = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", vialOfWater.highlighted());

		pourAllVialsIntoTheBasin = new ObjectStep(this, 61707, new WorldPoint(2518, 7389, 0), "Pour all the chemical vials into the basin", chemicalVial.highlighted());


		cDestroyingTheStockpileLaboratoryStep = new ConditionalStep(this, dtsSearchShelvesForSupplies, "\nFill the basin to the north until it reads 53. If something has gone wrong, overfill the basin and start over.");
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

		dtsLeaveLaboratory = new ObjectStep(this, 61572, new WorldPoint(2523, 7389, 0), "Enter the now-unlocked door to the north-east..");

		dtsDestroyBloodStockpile = new ObjectStep(this, 61711, new WorldPoint(2535, 7385, 0), "Destroy the blood stockpile.");
		dtsWatchTheCutscene = dtsDestroyBloodStockpile.cutscene();

		cDestroyingTheStockpile = new ConditionalStep(this, dtsDestroyBloodStockpile, "\nFind and destroy the stockpile.");
		cDestroyingTheStockpile.addStep(inCutscene, dtsWatchTheCutscene);
		cDestroyingTheStockpile.addStep(inLaboratoryStorageRoom, dtsDestroyBloodStockpile);
		cDestroyingTheStockpile.addStep(and(inLaboratory, finishedLabPuzzle), dtsLeaveLaboratory);
		cDestroyingTheStockpile.addStep(inLaboratory, cDestroyingTheStockpileLaboratoryStepPW);
		cDestroyingTheStockpile.addStep(inSolidDoorHallway, dtsOpenSolidDoor);
		cDestroyingTheStockpile.addStep(inRoomOutsideGuestChamberStoreroom, dtsLeaveDisplayCaseRoom);
		cDestroyingTheStockpile.addStep(inVenatorPuzzleRoom, dtsLeavePuzzleRoom);
		cDestroyingTheStockpile.addStep(inSolidKeyRoom, dtsLeaveSolidKeyRoom);

		/// 80
		tryToLeave = new ObjectStep(this, 61221, new WorldPoint(2323, 7370, 0), "");
		vampyriumCastleDrakanDestroyedBloodStockpile = new DetailedQuestStep(this, "Watch the cutscene.");
		cVampyriumCastleDrakanDestroyedBloodStockpile = new ConditionalStep(this, tryToLeave, "Try to leave Castle Drakan through the portal.");
		cVampyriumCastleDrakanDestroyedBloodStockpile.addStep(inCutscene, vampyriumCastleDrakanDestroyedBloodStockpile);

		/// 82
		leaveCastleDrakan = new ObjectStep(this, 61595, new WorldPoint(2332, 7370, 0), "Leave Castle Drakan.");
		watchLeaveCastleDrakanCutscene = leaveCastleDrakan.cutscene();


		/// 84 + 86
		pickupCrankWheel = new DetailedQuestStep(this, new WorldPoint(2603, 7847, 0), "Pick up the crank wheel from the house east of the altar, north-west of where you started after the cutscene.", crankWheel);
		pickupCrankWheelFromWhereYouDied = new DetailedQuestStep(this, "Pick up the crank wheel from where you died!", crankWheel);
		pickupCrankWheelFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		pickupCrankWheel.addSubSteps(pickupCrankWheelFromWhereYouDied);
		crankWheel1 = new ObjectStep(this, 61852, new WorldPoint(2588, 7855, 0), "Spam click the crank base outside the house to the north west. Drakan will start chasing you. Run away if he gets too close.", crankWheel);
		enterHouseNextToCrankWheel = new ObjectStep(this, 61844, new WorldPoint(2587, 7857, 0), "Enter the house north of the crank base.");

		searchBookCase = new ObjectStep(this, 61818, new WorldPoint(2585, 7861, 0), "Search the bookshelf in the house north of the crank base for a dusty book, then read it to retrieve the vitur key.");
		readDustyBook = new DetailedQuestStep(this, "Read the dusty book and retrieve the vitur key.", dustyBook.highlighted());
		pickupViturKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the vitur key from where you died!", viturKey);
		pickupViturKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		searchBookCase.addSubSteps(pickupViturKeyFromWhereYouDied);

		leaveBookcaseHouse = new ObjectStep(this, 61844, new WorldPoint(2587, 7857, 0), "Leave the bookcase house and enter the north-eastern vitur manor.", viturKeyForBoltCutterHouse);
		openViturDoorEastOfBookcaseHouse = new ObjectStep(this, 61834, new WorldPoint(2612, 7853, 0), "Enter the north-eastern vitur manor.", viturKeyForBoltCutterHouse);

		pickupBoltCuttersFromWhereYouDied = new DetailedQuestStep(this, "Pick up the bolt cutters from where you died!", boltCutters);
		pickupBoltCuttersFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		searchShedBoltCutter = new ObjectStep(this, 61858, new WorldPoint(2616, 7855, 0), "Enter the north-eastern vitur manor and search the shed in the north-eastern room for a pair of bolt cutters.");
		searchShedBoltCutter.addSubSteps(leaveBookcaseHouse, openViturDoorEastOfBookcaseHouse, pickupBoltCuttersFromWhereYouDied);
		var boltCutterShedCombinationLock = new ChestCodeStep(this, "shed", "TOOLS", 10, 8, 2, 9, 1, 2);
		boltCutterShedCombinationLockPW = boltCutterShedCombinationLock.puzzleWrapStepWithDefaultText("Solve the shed puzzle.");

		leaveBoltCutterHouse = new ObjectStep(this, 61835, new WorldPoint(2612, 7852, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", boltCutters, viturKeyForToothHalfOfKeyHouse);
		enterToothHalfOfKeyHouse = new ObjectStep(this, 61838, new WorldPoint(2598, 7843, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", viturKeyForToothHalfOfKeyHouse);
		// TODO: I do not have a "pick up tooth half of key from where you died" step.
		pickupToothHalfOfKey = new DetailedQuestStep(this, new WorldPoint(2600, 7845, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", toothHalfOfKey);
		pickupToothHalfOfKey.addSubSteps(leaveBoltCutterHouse, enterToothHalfOfKeyHouse);

		openChainedDoor = new ObjectStep(this, 61840, new WorldPoint(2590, 7818, 0), "Enter the house with the chained door, south of the well, and pick up the loop half of key from the table.", boltCutters, toothHalfOfKey);
		// TODO: I do not have a "pick up loop half of key from where you died" step.
		pickupLoopHalfOfKey = new DetailedQuestStep(this, new WorldPoint(2588, 7816, 0), "Enter the house with the chained door, south of the well, and pick up the loop half of key from the table.", loopHalfOfKey);
		pickupLoopHalfOfKey.addSubSteps(openChainedDoor);

		makeMyrmelKey = new DetailedQuestStep(this, "Combine the loop and tooth half of key into the myrmel key.", loopHalfOfKey.highlighted(), toothHalfOfKey.highlighted());

		enterBucketHouse = new ObjectStep(this, 61825, new WorldPoint(2599, 7826, 0), "Pick up the bucket from the house with the myrmel door, east of the well.", myrmelKeyForBucketHouse);
		pickupBucket = new DetailedQuestStep(this, new WorldPoint(2596, 7833, 0), "Pick up the bucket from the house with the myrmel door, east of the well.", bucket);
		pickupBucket.addSubSteps(enterBucketHouse);

		useBucketOnWell = new ObjectStep(this, 60892, new WorldPoint(2592, 7825, 0), "Use the bucket on the well outside the house.", bucket.highlighted());
		enterBucketHouseAgain = new ObjectStep(this, 61825, new WorldPoint(2599, 7826, 0), "Return to the myrmel house with the bucket of water.", bucketOfWater);

		// NOTE: After doing this step, there is a tick where it goes back to being confused, because the dialog is slow
		// TODO: There's no "pick up shadum key from where you died" step.
		useBucketOfWaterOnNorthernBarrel = new ObjectStep(this, 61811, new WorldPoint(2592, 7833, 0), "Use the bucket of water on the northern barrel to retrieve the shadum key.", bucketOfWater.highlighted());

		pickupTrapdoorKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the trapdoor key from where you died!", trapdoorKey);
		pickupTrapdoorKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		enterShadumDoor = new ObjectStep(this, 61832, new WorldPoint(2614, 7814, 0), "Enter the shadum door south-west of the drawbridge and pick up the trapdoor key.", shadumKeyForTrapdoorHouse);
		pickupTrapdoorKey = new DetailedQuestStep(this, new WorldPoint(2619, 7812, 0), "Pick up the trapdoor key from the shadum house south-west of the drawbridge.", trapdoorKey);
		pickupTrapdoorKey.addSubSteps(enterShadumDoor, pickupTrapdoorKeyFromWhereYouDied);

		enterBank = new ObjectStep(this, 61829, new WorldPoint(2580, 7835, 0), "Enter the bank north-west of the well.", myrmelKeyForBank, trapdoorKey, crankWheelForBank);

		operateBankCrank = new ObjectStep(this, 61853, new WorldPoint(2582, 7834, 0), "Spam click the crank base in the bank.", trapdoorKey, crankWheelForBank);

		enterTrapdoor = new ObjectStep(this, 61848, new WorldPoint(2579, 7830, 0), "Enter the trapdoor.", trapdoorKey);


		pickupJovkaiKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the jovkai key from where you died!", jovkaiKey);
		pickupJovkaiKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		searchAltarChest = new ObjectStep(this, 61807, new WorldPoint(2576, 7846, 0), "Search the chest in the western room of the altar house.");
		enterAltarHouseThroughDoor = new ObjectStep(this, 61847, new WorldPoint(2581, 7842, 0), "Search the chest in the western room of the altar house.");
		searchAltarChest.addSubSteps(enterAltarHouseThroughDoor, pickupJovkaiKeyFromWhereYouDied);

		var solveAltarChestLock = new ChestCodeStep(this, "altar chest", "35158", 10, 3, 5, 1, 5, 8);
		solveAltarChestLockPW = solveAltarChestLock.puzzleWrapStepWithDefaultText("Solve the chest puzzle.");

		leaveAltarThroughDoor = new ObjectStep(this, 61847, new WorldPoint(2581, 7842, 0), "Leave the altar room, then head to the smithy in the south-west.", jovkaiKey);
		enterSmith = new ObjectStep(this, 61821, new WorldPoint(2580, 7812, 0), "Enter the smithy in the south-west.", jovkaiKey);

		pickupOldCogFromWhereYouDied = new DetailedQuestStep(this, "Pick up the old cog from where you died!", oldCog);
		pickupOldCogFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		pickupOldCog = new DetailedQuestStep(this, new WorldPoint(2582, 7817, 0), "Pick up the old cog from the table in the smithy in the south-west.", oldCog);
		pickupOldCog.addSubSteps(enterSmith, pickupOldCogFromWhereYouDied);

		returnToVanescula = new NpcStep(this, 15909, new WorldPoint(2633, 7830, 0), "Return to Vanescula by the drawbridge to the south-east. Drakan will chase you when you leave the smithy.", oldCog);

		talkToIvanForSupplies = new NpcStep(this, 15856, new WorldPoint(2636, 7825, 0), "Return to Vanescula by the drawbridge and talk to Ivan Strom for some supplies. You can drop the bucket.");
		talkToIvanForSupplies.addDialogStep("Could I have those supplies you found?");

		returnToVanesculaReadyToLeave = new NpcStep(this, 15909, new WorldPoint(2633, 7830, 0), "Return to Vanescula by the drawbridge to the south-east when you're ready to leave. Drakan will chase you through the woods. Dodge his attacks, protect from melee.", oldCog);
		returnToVanesculaReadyToLeave.addSubSteps(talkToIvanForSupplies);

		/// 88
		watchCutsceneRepairedBridge = returnToVanesculaReadyToLeave.cutscene();

		/// 90
		fightDrakan1 = new NpcStep(this, 16211, new WorldPoint(2711, 7847, 0), "Learn to fight Drakan. Protect from melee and dodge his attacks. During this tutorial segment, there will be glimmering sparks that attempt to show you where to click to dodge his attacks.");

		/// 92
		flee1WatchTheCutscene = fightDrakan1.cutscene();
		resupplyIfNeeded = new ObjectStep(this, 61941, new WorldPoint(2843, 7646, 0), "Resupply at the shelter remains in the middle. When you've resupplied, click the trees around you to continue.");
		fightDrakan1.addSubSteps(resupplyIfNeeded);

		/// 94
		resupplyIfNeeded2 = new ObjectStep(this, 61047, new WorldPoint(2951, 7821, 0), "Resupply at the shelter remains in the middle. When you've resupplied, click the trees to continue.");

		// TODO: instruct on their swapping
		sotfa1 = new NpcStep(this, new int[]{16229, 16230, 16231, 16232}, new WorldPoint(2961, 7851, 0), "Kill the ancient feral vyres. They swap positions and heal, so try to focus one at a time.", true);
		sotfa1Exit = new ObjectStep(this, 61047, new WorldPoint(2960, 7864, 0), "Enter Darkwood trees to continue.");

		var takeAxe = new ObjectStep(this, 61961, new WorldPoint(2960, 7890, 0), "Take the axe from the stump at the beginning.");

		// [2026-07-12T13:54:14Z 14140] varbit SOTFA_FOREST_ENCOUNTER_COMPLETED (15605) 0 -> 1 = feral vyres

		// TODO: Mention that if your inventory is full, you won't get ticks in your inventory.
		// If you get ticks in your inventory, you have to throw them away to stop taking damage.
		var ts1 = new TreeSolver1(this);
		var ts2 = new TreeSolver2(this);
		var ts3 = new TreeSolver3(this);
		var ts4 = new TreeSolver4(this);
		var ts5 = new TreeSolver5(this);
		var ts6 = new TreeSolver6(this);
		// Climb over tree 6 from west
		var ts7_01 = TreeSolver.createStep(this, 2980, 7914, TreeType.Stump, Direction.WEST);
		// Cut tree 7 from south
		var ts7_02 = TreeSolver.createStep(this, 2982, 7915, TreeType.Untouched, Direction.SOUTH);
		// Climb back over tree 6 from east
		var ts7_03 = TreeSolver.createStep(this, 2981, 7914, TreeType.Stump, Direction.EAST);
		// Climb over tree 6 from south
		var ts7_04 = TreeSolver.createStep(this, 2981, 7913, TreeType.Stump, Direction.SOUTH);
		// Cut tree 7 from west
		var ts7_05 = TreeSolver.createStep(this, 2982, 7916, TreeType.PartiallyChopped, Direction.WEST);
		// Climb over tree 7 from west
		var ts7_06 = TreeSolver.createStep(this, 2982, 7916, TreeType.Stump, Direction.WEST);
		// Cut tree 8 from south
		var ts7_07 = TreeSolver.createStep(this, 2984, 7917, TreeType.Untouched, Direction.SOUTH);
		// Climb back over tree 7 from east
		var ts7_08 = TreeSolver.createStep(this, 2983, 7916, TreeType.Stump, Direction.EAST);
		// Climb back over tree 6 from north
		var ts7_09 = TreeSolver.createStep(this, 2981, 7914, TreeType.Stump, Direction.NORTH);
		// Climb over tree 6 from west
		var ts7_10 = TreeSolver.createStep(this, 2980, 7914, TreeType.Stump, Direction.WEST);
		// Climb over tree 7 from south
		var ts7_11 = TreeSolver.createStep(this, 2982, 7915, TreeType.Stump, Direction.SOUTH);
		// Cut tree 8 from west
		var ts7_12 = TreeSolver.createStep(this, 2984, 7917, TreeType.PartiallyChopped, Direction.WEST);
		// Climb over tree 8 from west
		var ts7_13 = TreeSolver.createStep(this, 2984, 7917, TreeType.Stump, Direction.WEST);

		var tsFinalStep = new ObjectStep(this, 61047, new WorldPoint(2993, 7924, 0), "Turn off run before continuing.");

		sotfa2 = new ConditionalStep(this, tsFinalStep, "Make your way through the tangle of trees to the darkwood trees in the north-east, chopping down and climbing over trees on the way. Trees can only be chopped once per direction.");
		sotfa2.addStep(and(ts1Zone, not(anyAxe)), takeAxe);

		sotfa2.addStep(and(ts8ZoneWest, tree8Chopped), ts7_13);
		sotfa2.addStep(and(ts8ZoneWest, tree8SlightlyChopped), ts7_12);
		sotfa2.addStep(and(ts7ZoneSouth, or(tree8Chopped, tree8SlightlyChopped)), ts7_11);
		sotfa2.addStep(and(ts6Zone, tree8SlightlyChopped), ts7_10);
		sotfa2.addStep(and(ts7ZoneWest, tree8SlightlyChopped), ts7_09);
		sotfa2.addStep(and(ts8ZoneSouth, tree8SlightlyChopped), ts7_08);
		sotfa2.addStep(and(ts8ZoneSouth, tree8Untouched), ts7_07);
		sotfa2.addStep(and(ts7ZoneWest, tree6ChoppedDown, tree7Chopped), ts7_06);
		sotfa2.addStep(and(ts7ZoneWest, tree6ChoppedDown, tree7SlightlyChopped), ts7_05);
		sotfa2.addStep(and(ts6Zone, tree6ChoppedDown, tree7SlightlyChopped), ts7_04);
		sotfa2.addStep(and(ts7ZoneSouth, tree6ChoppedDown, tree7SlightlyChopped), ts7_03);
		sotfa2.addStep(and(ts7ZoneSouth, tree6ChoppedDown, tree7Untouched), ts7_02);
		sotfa2.addStep(and(ts6Zone, tree6ChoppedDown, tree7Untouched), ts7_01);
		sotfa2.addStep(ts6Zone, ts6);
		sotfa2.addStep(ts5Zone, ts5);
		sotfa2.addStep(ts4Zone, ts4); // ts4 is also used as the fallback since it has the biggest "free zone"
		sotfa2.addStep(ts3Zone, ts3);
		sotfa2.addStep(ts2Zone, ts2);
		sotfa2.addStep(ts1Zone, ts1);

		// NOTE: This is a _dangerous_ step. Hardcore ironmen should be careful. If we add an indicator for dangerous steps in the future, this one should be marked.
		// TODO: I'd love to maybe add a custom icon on each, or number.
		sotfa3AvoidAnimals = new NpcStep(this, 16233, "Turn off run. Avoid the Maxilla beasts. Running nearby, walking within 2 tiles, or standing on the maxilla beast will cause them to attack you and probably kill you.\nThe first and third follow predetermined paths, while the second one moves randomly. Be extra careful when passing it.", true);
		// avoidAnimals.addTileMarkers(SpriteID.LOAD, new WorldPoint(2914, 7920, 0));
		sotfa3Exit = new ObjectStep(this, 61992, new WorldPoint(2928, 7892, 0), "Turn off run. Avoid the Maxilla beasts. Running nearby, walking within 2 tiles, or standing on the maxilla beast will cause them to attack you and probably kill you.\nThe first and third follow predetermined paths, while the second one moves randomly. Be extra careful when passing it.");
		cSotfa3 = new ConditionalStep(this, sotfa3AvoidAnimals);
		cSotfa3.addStep(nearSotfa3Exit, sotfa3Exit);

		sotfa4 = new ObjectStep(this, 61995, new WorldPoint(2993, 7866, 0), "Continue through the cave.");

		// nylocas room
		// TODO: Because it takes a little bit for the Nylocas to spawn, we immediately direct the user to the exit while they can't actually leave.
		// Is it possible to solve this? There's no varbit that denotes success
		sotfa5 = new NpcStep(this, 16236, "Kill the nylocas. Gray ones with melee, yellow ones with a ranged weapon. You can pick up the Spine near the Venator corpse to use as darts. If you do not kill them fast enough, they explode dealing 15 damage.", true);
		sotfa5.addAlternateNpcs(16237);
		sotfa5.addCustomIcon(new QuestStepIcon(ItemID.DRAGON_SCIMITAR, 16236, 0.75));
		sotfa5.addCustomIcon(new QuestStepIcon(ItemID.SOTFA_FOREST_TALON, 16237, 0.75));
		// TODO: For some reason, the npc condition failed to detect more npcs at some point. Not sure what happened, but a few ticks later the npcs were highlighted.
		sotfa5Exit = new ObjectStep(this, 61047, new WorldPoint(2912, 7852, 0), "Kill the nylocas. Gray ones with melee, yellow ones with a ranged weapon. You can pick up the Spine near the Venator corpse to use as darts. If you do not kill them fast enough, they explode dealing 15 damage. When all are dead, leave through the trees.");
		cSotfa5 = new ConditionalStep(this, sotfa5);
		cSotfa5.addStep(not(anyNearbyNylocas), sotfa5Exit);


		sotfa6WrangleSnakes = new NpcStep(this, 16238, "Wrangle the snakes, then combine them into one long snake. You must stand behind the snake when attempting to wrangle it.", true, deadSnake3);
		sotfa6CombineSnakes = new DetailedQuestStep(this, "Combine the snakes into one long snake.", deadSnake3.highlighted());
		sotfa6UseRopeOnBranch = new ObjectStep(this, 61951, new WorldPoint(3042, 7895, 0), "Use the serpent rope on the long branched tree.", serpentRope.highlighted());
		sotfa6SwingAcrossWater = new ObjectStep(this, 61949, new WorldPoint(3040, 7894, 0), "Swing across the water with your newly created swing.");
		sotfa6Exit = new ObjectStep(this, 61047, new WorldPoint(3041, 7885, 0), "Leave through the trees.");
		sotfaWatchTheCutscene = sotfa6Exit.cutscene();
		cSotfa6 = new ConditionalStep(this, sotfa6WrangleSnakes);
		cSotfa6.addStep(acrossSotfa6Pond, sotfa6Exit);
		cSotfa6.addStep(ropedTree, sotfa6SwingAcrossWater);
		cSotfa6.addStep(serpentRope, sotfa6UseRopeOnBranch);
		cSotfa6.addStep(deadSnake3, sotfa6CombineSnakes);

		/// 98
		talkToMysteriousWoman1 = new NpcStep(this, 15953, new WorldPoint(2975, 7704, 0), "Talk-to Mysterious Woman.");
		mysteriousWomanWatchTheCutscene = talkToMysteriousWoman1.cutscene();

		/// 100
		startTalkingToEfaritay = new NpcStep(this, 15941, new WorldPoint(3157, 7839, 1), "Talk to Efaritay Hallow in the west room to help her with her son.");

		/// 102 + 104 + 106 + 108
		leavePalace1 = new ObjectStep(this, 62147, new WorldPoint(3167, 7860, 1), "Pick some amitire leaves from the amitire plant outside the palace.");
		pickFromAmitirePlant = new ObjectStep(this, 62135, new WorldPoint(2982, 7679, 0), "Pick some amitire leaves from the amitire plant outside the palace.");
		pickFromAmitirePlant.addSubSteps(leavePalace1);

		enterPalace1 = new ObjectStep(this, 62144, new WorldPoint(2974, 7677, 0), "Enter the palace with the amitire leaves.", amitireLeaves);

		searchShelvesForBowl = new ObjectStep(this, 62070, new WorldPoint(3159, 7824, 1), "Search the shelves next to the sink for a bowl.", amitireLeaves);
		searchShelvesForBowl.addSubSteps(enterPalace1);

		fillBowlWithWater = new ObjectStep(this, 62097, new WorldPoint(3157, 7825, 1), "Fill the bowl with water", amitireLeaves, bowl.highlighted());

		getPotatoFromCupboard = new ObjectStep(this, 62072, new WorldPoint(3155, 7832, 1), "Take a potato from the cupboard.", amitireLeaves, bowlOfWater);
		getPotatoFromCupboard.addDialogStep("Potato.");

		combineStew = new DetailedQuestStep(this, "Put the potato in the bowl of water.", amitireLeaves, bowlOfWater.highlighted(), potato.highlighted());

		getRawMeatFromCupboard = new ObjectStep(this, 62072, new WorldPoint(3155, 7832, 1), "Take some raw meat the cupboard.", amitireLeaves, incompleteStew);
		getRawMeatFromCupboard.addDialogStep("Raw meat.");

		cookMeatOnRange = new ObjectStep(this, 39391, new WorldPoint(3160, 7826, 1), "Cook the raw meat on the range.", amitireLeaves, incompleteStew, rawMeat.highlighted());
		cookMeatOnRange.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Cooked meat"));

		combineStew2 = new DetailedQuestStep(this, "Put the cooked meat in the incomplete stew.", amitireLeaves, incompleteStew.highlighted(), cookedMeat.highlighted());
		cookStew = new ObjectStep(this, 39391, new WorldPoint(3160, 7826, 1), "Cook the stew on the range.", uncookedStew.highlighted());
		cookStew.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Stew"));
		combineStew3 = new DetailedQuestStep(this, "Put the amitire leaves into the stew.", amitireLeaves.highlighted(), stew.highlighted());
		giveStewToSafalaan = new NpcStep(this, 15895, "Give the amitire stew to Safalaan Hallow.", amitireStew.highlighted());

		talkToSafalaanAfterFeedingHimStew = new NpcStep(this, 15893, new WorldPoint(3157, 7841, 1), "Talk to Safalaan Hallow after feeding him the stew.");

		talkToEfaritayAfterFeedingStewToSafalaan = new NpcStep(this, 15941, new WorldPoint(3157, 7839, 1), "Talk to Efaritay Hallow after feeding Safalaan the stew.");


		/// 110
		searchWorkbenchForHammer = new ObjectStep(this, 62073, new WorldPoint(3176, 7841, 1), "Search the workbench in the east room for a hammer.", blisterwoodFlail);
		searchWorkbenchForHammer.addDialogStep("Hammer.");
		searchWorkbenchForChisel = new ObjectStep(this, 62073, new WorldPoint(3176, 7841, 1), "Search the workbench in the east room for a chisel.", blisterwoodFlail, hammer);
		searchWorkbenchForChisel.addDialogStep("Chisel.");
		searchWorkbenchForKnife = new ObjectStep(this, 62073, new WorldPoint(3176, 7841, 1), "Search the workbench in the east room for a knife.", blisterwoodFlail, hammer, chisel);
		searchWorkbenchForKnife.addDialogStep("Knife.");
		searchCrateForBlisterwoodLogs = new ObjectStep(this, 62076, new WorldPoint(3177, 7843, 1), "Search the northern-most crate for some blisterwood logs.", blisterwoodFlail, hammer, chisel, knife);
		searchCrateForBlisterwoodLogs.addWidgetHighlight(InterfaceID.Skillmulti.A);
		searchCrateForBlessedSilverSickle = new ObjectStep(this, 62077, new WorldPoint(3181, 7841, 1), "Search the eastern-most crate for a blessed silver sickle.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs);
		searchChestForDiamond = new ObjectStep(this, 62074, new WorldPoint(3181, 7835, 1), "Search the eastern-most chest for a diamond.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, blessedSilverSickle);
		putDiamondInSickle = new DetailedQuestStep(this, "Put the unenchanted diamond on the blessed silver sickle.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, blessedSilverSickle.highlighted(), diamond.highlighted());
		putDiamondInSickle.addDialogStep("Yes.");
		searchChestForDiamondTablet2 = new ObjectStep(this, 62075, new WorldPoint(3175, 7836, 1), "Search the western-most chest for a diamond tablet.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, diamondSickleB);
		useEnchantDiamondTabletOnSickle = new DetailedQuestStep(this, "Enchant the diamond sickle with the enchant diamond tablet.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, diamondSickleB.highlighted(), diamondTablet.highlighted());
		useEnchantDiamondTabletOnSickle.addDialogStep("Yes.");
		createEnhancedBlisterwoodSickle = new DetailedQuestStep(this, "Combine the blisterwood logs with your newly created enchanted diamond sickle to create an enhanced blisterwood sickle.", blisterwoodFlail, hammer, knife, blisterwoodLogs.highlighted(), enchantedDiamondSickle.highlighted());
		createEnhancedBlisterwoodSickle.addDialogStep("Yes.");
		createHallowedFlail = new ObjectStep(this, 2097, new WorldPoint(3179, 7842, 1), "Smith a hallowed flail at the anvil.", blisterwoodFlailUnequipped, hammer, enhancedBlisterwoodSickle, hallowedMarks);
		createHallowedFlail.addDialogStep("Yes.");

		// 110 -> 112 after creating the hallowed flail
		// [2026-07-19T12:18:53Z 317] varbit MYQ6_ARANEI_WATCHER_CHAT (15477) 0 -> 1
		// after speaking to the random Aranei outside the palace
		speakToIvanWithHallowedFlail = new NpcStep(this, 15855, new WorldPoint(3175, 7834, 1), "Talk to Ivan Strom after smithing the hallowed flail.");

		ivanSearchWorkbenchForKnife = new ObjectStep(this, 62073, new WorldPoint(3176, 7841, 1), "Search the workbench for a knife.");
		ivanSearchWorkbenchForKnife.addDialogStep("Knife.");
		getLogsForStakes = new ObjectStep(this, 62076, new WorldPoint(3177, 7843, 1), "Search the northern-most crate for some blisterwood logs to craft stakes.");
		getLogsForStakes.addWidgetHighlight(InterfaceID.Skillmulti.A);
		fletchStakes = new DetailedQuestStep(this, "Fletch the logs into stakes.", blisterwoodLogs.highlighted(), knife.highlighted());

		returnToIvan = new NpcStep(this, 15855, new WorldPoint(3175, 7834, 1), "Return to  Ivan Strom after crafting some stakes. You can craft more before continuing if you want to use them during the upcoming fights.");

		climbDownstairs = new ObjectStep(this, 62152, new WorldPoint(3171, 7823, 1), "Talk to Veliaf in the dungeon downstairs.");
		talkToVeliafInDungeon = new NpcStep(this, 15878, new WorldPoint(3168, 7842, 0), "Talk to Veliaf in the dungeon.");
		talkToVeliafInDungeon.addSubSteps(climbDownstairs);

		talkToVanescula = new NpcStep(this, 15910, new WorldPoint(3168, 7844, 0), "Talk-to Vanescula Drakan.");

		talkToVeliafInDungeonAgain = new NpcStep(this, 15878, new WorldPoint(3168, 7842, 0), "Talk to Veliaf in the dungeon again.");

		climbUpstairs = new ObjectStep(this, 62151, new WorldPoint(3164, 7823, 0), "Climb upstairs and talk to Sugadinti.");
		talkToSugadintiAfterHelpingAllies = new NpcStep(this, 15960, new WorldPoint(3168, 7828, 1), "Talk to Sugadinti Vitur after helping out Ivan and Veliaf.");
		talkToSugadintiAfterHelpingAllies.addSubSteps(climbUpstairs);

		/// 114
		leavePalaceForCombat = new ObjectStep(this, 62147, new WorldPoint(3167, 7860, 1), "Get ready for combat. Resupply at the supply table in the kitchen if needed. Speak to the Aranei next to the table if you want to deposit any items into your bank. Then leave the palace.");
		getReadyForCombatWatchTheCutscene = leavePalaceForCombat.cutscene();

		/// 116
		attackPortals = new NpcStep(this, 16251, "Attack the portals in the sky.", true);

		/// 118
		leaveDoors = new ObjectStep(this, 62147, new WorldPoint(3167, 7860, 1), "Leave the palace.");
		leaveDoorsCutscene = leaveDoors.cutscene();

		/// 120

		// TODO: we _could_ highlight anything that's broken

		// FALLEN: new NpcStep(this, 16266, new WorldPoint(3173, 7855, 1), "Heal Aranei.");
		// DAMAGED PILLAR: new ObjectStep(this, 62155, new WorldPoint(3174, 7855, 1), "Repair Damaged pillar.");
		// DAMAGED WINDOW: new ObjectStep(this, 62158, new WorldPoint(3176, 7858, 1), "Repair Damaged window.");
		// DAMAGED WINDOW: new ObjectStep(this, 62158, new WorldPoint(3174, 7858, 1), "Repair Damaged window.");
		// DAMAGED WINDOW: new ObjectStep(this, 62158, new WorldPoint(3162, 7858, 1), "Repair Damaged window.");
		// DAMAGED WINDOW: new ObjectStep(this, 62158, new WorldPoint(3164, 7858, 1), "Repair Damaged window.");
		// DAMAGED PILLAR: new ObjectStep(this, 62155, new WorldPoint(3162, 7855, 1), "Repair Damaged pillar.");
		guardThePalace = new DetailedQuestStep(this, "Heal fallen Aranei, repair damaged windows, and repair damaged pillars.");

		barricadeCutscene = guardThePalace.cutscene();
		passThroughBarricadeToHelp = new ObjectStep(this, 62163, new WorldPoint(3167, 7844, 1), "Pass through barricade to return to the guarding step.");
		guardThePalace.addSubSteps(passThroughBarricadeToHelp);

		/// 122
		passThroughBarricadeToFightDrakan = new ObjectStep(this, 62163, new WorldPoint(3167, 7844, 1), "Pass through the barricade, ready to fight Drakan. You can resupply at the supply table in the kitchen.");
		fightDrakan2 = new NpcStep(this, 16210, new WorldPoint(3168, 7853, 1), "Fight Drakan. Protect from melee. Dodge attacks.");
		fightDrakan2.addSubSteps(passThroughBarricadeToFightDrakan);

		/// 124
		finishedDrakan2Cutscene = new DetailedQuestStep(this, "Watch the cutscene.");

		/// 126
		talkToIvanInHauntedWoods = new NpcStep(this, 15855, new WorldPoint(3606, 3416, 0), "Talk to Ivan Strom in the haunted woods.");
		leavingPalaceCutscene = talkToIvanInHauntedWoods.cutscene();

		/// 128
		talkToVeliafAfterLeavingPalace = new NpcStep(this, 15878, new WorldPoint(3493, 9628, 0), "Talk to Veliaf Hurtz in Burgh de Rott.");
		talkToVeliafAfterLeavingPalaceCutscene = talkToVeliafAfterLeavingPalace.cutscene();

		/// 130
		talkToSugadintiInBurghDeRott = new NpcStep(this, 15960, new WorldPoint(3494, 9627, 0), "Talk-to Sugadinti Vitur in Burgh de Rott.");

		/// 132 + 134 + 136 + 138 (theatre of blood)
		getToTob = new NpcStep(this, 15962, new WorldPoint(3668, 3220, 0), "Gear up for theatre of blood. Bring melee gear with a range and mage switch, some food & potions, then talk to Sugadinti Vitur in front of the theatre of blood.", combatGear, food, prayerPotions);
		getToTob.addDialogStep("I'm ready.");
		getToTobCutscene = getToTob.cutscene();

		// TODO: This text could probably be improved
		ensureNothingBothersSugadinti = new DetailedQuestStep(this, "Kill Nylocas with the correct fighting style. Fight the Maiden of Sugadinti, killing healers when they spawn. Avoid blood puddles on the ground.");

		talkToSugadintiAfterFinishingTob = new NpcStep(this, 15962, new WorldPoint(3667, 3218, 0), "Talk-to Sugadinti Vitur.");
		talkToSugadintiAfterFinishingTobCutscene = talkToSugadintiAfterFinishingTob.cutscene();

		/// 140
		headToBarrowsL = new NpcStep(this, 15878, new WorldPoint(3540, 3256, 0), "Head to the abandoned building north-east of Burgh de Rott and talk to Veliaf.", combatGear, hallowedFlail, food, prayerPotions);

		/// 142
		headDownToVanescula = new ObjectStep(this, 61254, new WorldPoint(3543, 3257, 0), "Head to the abandoned building north-east of Burgh de Rott and enter the entry.");
		headDownToVanesculaCutscene = headDownToVanescula.cutscene();

		/// 144
		fightTheWyrd = new NpcStep(this, 16212, "Fight the Wyrd. TODO ADD SOME INSTRUCTIONS HERE :)");

		/// 146
		dealtWithWyrdCutscene = fightTheWyrd.cutscene();

		/// 148
		findWyrd = new ObjectStep(this, 61268, new WorldPoint(3562, 3323, 0), "Search for the escaped wyrd. Inspect the fence north of barrows.");
		findWyrdCutscene = findWyrd.cutscene();

		/// 150
		speakWithVeliafAfterInspectingFence = new NpcStep(this, 15880, new WorldPoint(3493, 9628, 0), "Talk to Veliaf Hurtz in the Burgh de Rott hideout.");
		talkWithVeliafInBurghDeRottCutscene = speakWithVeliafAfterInspectingFence.cutscene();

		/// 152
		prepareFightDrakan3 = new NpcStep(this, 15960, new WorldPoint(3494, 9627, 0), "Talk-to Sugadinti Vitur in the Burgh de Rott hideout, ready for another fight against Drakan.");
		prepareFightDrakan3.addDialogStep("I'm ready.");
		enterBurghDeRottDungeon = new ObjectStep(this, 12743, new WorldPoint(3490, 3232, 0), "Talk-to Sugadinti Vitur in the Burgh de Rott hideout, ready for another fight against Drakan.");
		prepareFightDrakan3.addSubSteps(enterBurghDeRottDungeon);

		/// 154
		fightDrakan3 = new NpcStep(this, 16209, "Fight Lowerniel Drakan. Protect from melee. Dodge his attacks.");

		/// 156
		talkToVeliaf = new NpcStep(this, 15880, new WorldPoint(3493, 9628, 0), "Talk-to Veliaf Hurtz.");
		talkToVeliaf.addDialogStep("I'll go now.");
		talkToVeliafCutscene = talkToVeliaf.cutscene();

		/// 158
		talkToIvanInsideCastleDrakan = new NpcStep(this, 15855, new WorldPoint(2845, 7387, 0), "Talk-to Ivan Strom inside Castle Drakan.");

		/// 160 + 162
		talkToSugadintiInsideCastleDrakan = new NpcStep(this, 15960, new WorldPoint(2846, 7389, 0), "Talk to Sugadinti Vitur inside Castle Drakan.");

		/// 164
		talkToEfaritayOnIcyene = new NpcStep(this, 15942, new WorldPoint(3701, 3186, 0), "Talk to Efaritay Hallow in Icyene Graveyard.");

		/// 166
		enterVampyriumForTheLastTime = new ObjectStep(this, 61215, new WorldPoint(3703, 3185, 0), "Enter the portal in Icyene Graveyard to get to Vampyrium, ready for the final confrontation.", hallowedFlail, combatGearMelee);
		enterVampyriumForTheLastTimeCutscene = enterVampyriumForTheLastTime.cutscene();

		/// 168
		goToFightDrakan4 = new ObjectStep(this, 61628, new WorldPoint(2506, 7387, 0), "Enter the door, ready to fight Drakan. You can get supplies and deposit items at the chest next to the door.", hallowedFlail, combatGearMelee);
		goToFightDrakan4Cutscene = goToFightDrakan4.cutscene();

		/// 170 + 172
		// TODO: some minor instructions + maybe a wiki link?
		fightDrakan4 = new NpcStep(this, 16204, "Fight Lowerniel Drakan.");
		fightDrakan4Cutscene = fightDrakan4.cutscene();

		/// 174
		talkToEfaritayAfterKillingDrakan = new NpcStep(this, 15941, new WorldPoint(3702, 3182, 0), "Talk-to Efaritay Hallow in the icyene graveyard.");
		finalQuestCutscene = talkToEfaritayAfterKillingDrakan.cutscene();

		/// 176 + 178
		youHaveFinishedTheQuest = new DetailedQuestStep(this, "Congratulations on finishing the quest!");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(2, startQuest);

		steps.put(4, cLookForIvan);

		var cInspectShrine = new ConditionalStep(this, goDownToIvan);
		cInspectShrine.addStep(inMyrequeHideoutOldManRal, inspectShrine);
		steps.put(6, cInspectShrine);
		steps.put(8, cInspectShrine);

		var cTalkToIVanGoingToDarkmeyer = new ConditionalStep(this, goDownToIvan);
		cTalkToIVanGoingToDarkmeyer.addStep(inMyrequeHideoutOldManRal, talkToIvanGoingToDarkmeyer);
		steps.put(10, cTalkToIVanGoingToDarkmeyer);

		var cEscapeCastleDrakan = new ConditionalStep(this, goDownToIvan);
		cEscapeCastleDrakan.addStep(inCutscene, watchCutsceneGoingToDarkmeyer);
		cEscapeCastleDrakan.addStep(atCastleDrakanCourtyard, defendIvanFromVyres);
		cEscapeCastleDrakan.addStep(inMyrequeHideoutOldManRal, talkToIvanToReturnToCastleDrakan);
		steps.put(12, cEscapeCastleDrakan);

		var cTalkToIvanAfterEscaping = new ConditionalStep(this, goDownToIvan);
		cTalkToIvanAfterEscaping.addStep(inMyrequeHideoutOldManRal, talkToIvanAfterEscaping);
		steps.put(14, cTalkToIvanAfterEscaping);

		steps.put(16, talkToIvanOutsideSlepeChurch);

		var cAskRoyAboutVeliaf = new ConditionalStep(this, talkToIvanOutsideSlepeChurch);
		cAskRoyAboutVeliaf.addStep(followedByIvan, askRoyAboutVeliaf);
		steps.put(18, cAskRoyAboutVeliaf);

		steps.put(20, cLookIntoCommotionAtCrombwickManor);

		var cLookIntoCommotionAtCrombwickManor2 = new ConditionalStep(this, lookIntoCommotion);
		cLookIntoCommotionAtCrombwickManor2.addStep(and(inCrombwickManor), killVampyresWithVeliaf);
		cLookIntoCommotionAtCrombwickManor2.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor);
		steps.put(22, cLookIntoCommotionAtCrombwickManor2);

		var cLookIntoCommotionAtCrombwickManor3 = new ConditionalStep(this, lookIntoCommotion);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inCrombwickManor), talkToVeliafInCrombwickManor);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor);
		steps.put(24, cLookIntoCommotionAtCrombwickManor3);

		steps.put(26, cHeadToPaterdomus);

		var cReadBook = new ConditionalStep(this, cHeadToPaterdomus);
		cReadBook.addStep(squiresJournal, readSquiresJournal);
		steps.put(28, cReadBook);

		steps.put(30, cTalkToIvanAfterReadingTheBook);

		var cKillMonksOfZamorak = new ConditionalStep(this, cTalkToIvanAfterReadingTheBook);
		cKillMonksOfZamorak.addStep(and(inPaterdomusTempleDungeon, followedByIvan), climbUpFromPaterdomusTempleDungeon);
		cKillMonksOfZamorak.addStep(and(followedByIvan, inPaterdomusTempleF0), killMonksOfZamorak);
		cKillMonksOfZamorak.addStep(and(followedByIvan), headToPaterdomusTempleF0);
		steps.put(32, cKillMonksOfZamorak);

		var cKillMonksOfZamorak2 = new ConditionalStep(this, headToPaterdomusTempleF0);
		cKillMonksOfZamorak2.addStep(and(inPaterdomusTempleF0), killMonksOfZamorak);
		steps.put(34, cKillMonksOfZamorak2);

		var cTalkToIvanAfterKillingMonks = new ConditionalStep(this, climbUpToPaterdomusTempleF1);
		cTalkToIvanAfterKillingMonks.addStep(inPaterdomusTempleF1, talkToIvanInPaterdomusTempleF1);
		steps.put(36, cTalkToIvanAfterKillingMonks);

		steps.put(38, cFindTheWritingsPW);

		var cTalkToIvanTempleAfterBookPuzzle = new ConditionalStep(this, climbUpToPaterdomusTempleF1);
		cTalkToIvanTempleAfterBookPuzzle.addStep(inPaterdomusTempleF1, talkToIvanAfterFindingTheWritings);
		steps.put(40, cTalkToIvanTempleAfterBookPuzzle);

		var cReadIvandisWritings = new ConditionalStep(this, cTalkToIvanTempleAfterBookPuzzle);
		cReadIvandisWritings.addStep(ivandisWritings, readIvandisWritings);
		steps.put(42, cReadIvandisWritings);

		var cTalkToIvanAfterReadingIvandisWritings = new ConditionalStep(this, climbUpToPaterdomusTempleF1);
		cTalkToIvanAfterReadingIvandisWritings.addStep(inPaterdomusTempleF1, talkToIvanAfterReadingIvandisWritings);
		steps.put(44, cTalkToIvanAfterReadingIvandisWritings);

		var cTalkToIvanInPaterdomus = new ConditionalStep(this, talkToIvanInPaterdomus);
		steps.put(46, cTalkToIvanInPaterdomus);

		var cTalkToIvanInPaterdomus2 = new ConditionalStep(this, talkToIvanInPaterdomus2);
		steps.put(48, cTalkToIvanInPaterdomus2);

		var cInvestigateHole = new ConditionalStep(this, getToIvandisTomb);
		cInvestigateHole.addStep(inIvandisTomb, investigateHole);
		steps.put(50, cInvestigateHole);

		// 15486 = has talked about pickaxe
		// 15469 = has received pickaxe
		var cMineHole = new ConditionalStep(this, getToIvandisTomb);
		cMineHole.addStep(and(inIvandisTomb, anyPickaxe), mineHole);
		cMineHole.addStep(and(inIvandisTomb, canReceivePickaxeFromIvan), getPickaxe);
		cMineHole.addStep(and(inIvandisTomb), mineHole);
		steps.put(52, cMineHole);

		var cHeadThroughHole = new ConditionalStep(this, getToIvandisTomb);
		cHeadThroughHole.addStep(inIvandisTomb, headThroughHole);
		steps.put(54, cHeadThroughHole);

		var cWalkThroughCastle = new ConditionalStep(this, cHeadThroughHole);
		cWalkThroughCastle.addStep(inCastleDrakanMines, enterDaeyaltProcessingRoom);
		steps.put(56, cWalkThroughCastle);

		var cWalkThroughCastle2 = new ConditionalStep(this, cWalkThroughCastle);
		cWalkThroughCastle2.addStep(inCastleDrakanDaeyaltProcessingArea, killVampsInDaeyaltRoom);
		steps.put(58, cWalkThroughCastle2);

		var cWalkThroughCastle3 = new ConditionalStep(this, cWalkThroughCastle);
		cWalkThroughCastle3.addStep(inCastleDrakanDaeyaltProcessingArea, talkToIvanAfterKillingVamps);
		steps.put(60, cWalkThroughCastle3);

		var cWalkThroughCastle4 = new ConditionalStep(this, cWalkThroughCastle3);
		cWalkThroughCastle4.addStep(inCastleDrakanDaeyaltProcessingArea, enterCastleDrakanCellar);
		steps.put(62, cWalkThroughCastle4);

		var cWalkThroughCastle5 = new ConditionalStep(this, cWalkThroughCastle4);
		cWalkThroughCastle5.addStep(inCastleDrakanCellar, climbUpToCastleDrakanLobby);
		steps.put(64, cWalkThroughCastle5);

		var cWalkThroughCastle6 = new ConditionalStep(this, cWalkThroughCastle4);
		cWalkThroughCastle6.addStep(and(inCastleDrakanLobby, needTeleportUnlock), prayAtShrine);
		cWalkThroughCastle6.addStep(and(inCastleDrakanLobby), enterPortalInCastleDrakanLobby);
		cWalkThroughCastle6.addStep(not(needTeleportUnlock), enterPortalInCastleDrakanLobby);
		steps.put(66, cWalkThroughCastle6);

		var cWalkThroughCastle7 = new ConditionalStep(this, enterPortalInCastleDrakanLobby);
		cWalkThroughCastle7.addStep(needTeleportUnlock, prayAtShrine);
		steps.put(68, cWalkThroughCastle7);

		var cVampyriumCutscene = new ConditionalStep(this, enterPortalInCastleDrakanLobby);
		cVampyriumCutscene.addStep(inVampyriumCastleDrakanLobbyCutscene, youAreInVampyrium);
		steps.put(70, cVampyriumCutscene);

		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ROOM_STATUS_1 (5632) 0 -> 1
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ROOM_STATUS_1 (5632) 1 -> 5
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_DOOR_STATUS_1 (5636) 0 -> 1
		// [2026-07-01T11:54:30Z 2956] varbit CASTLE_DRAKAN_EMBLEM_RANIS_HALLWAY_TO_RANIS_CHAMBER (15507) 0 -> 3
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ENEMY_STATUS_1 (5640) 0 -> 2048
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ENEMY_STATUS_1 (5640) 2048 -> 8390656
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ENEMY_STATUS_2 (5641) 0 -> 16
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ENEMY_STATUS_2 (5641) 16 -> 18
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ENEMY_STATUS_1 (5640) 8390656 -> 8390658
		// [2026-07-01T11:54:30Z 2956] varp CASTLE_DRAKAN_ENEMY_STATUS_2 (5641) 18 -> 50
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_LAST_ROOM (15499) 0 -> 2
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_LAST_FLOOR (15549) 0 -> 1
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_WORLD_MAP_FLOOR (15489) 0 -> 1
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_LAST_FACING (15500) 0 -> 4
		// [2026-07-01T11:54:32Z 2960] varbit IN_CASTLE_DRAKAN_INSTANCE (15498) 0 -> 1
		// [2026-07-01T11:54:32Z 2960] varbit IN_VAMPYRIUM (15482) 0 -> 1
		// [2026-07-01T11:56:27Z 3151] varp CASTLE_DRAKAN_ROOM_STATUS_1 (5632) 5 -> 6
		// [2026-07-01T11:56:27Z 3151] varbit CASTLE_DRAKAN_WORLDMAP_NOTIFICATION (15566) 0 -> 1

		// TODO: Instruct the user to pick up supplies as they walk along


		// This could _technically_ be a conditional step guiding the user from _any_ room back to the throne room,
		// but they should only ever get to this step if they've manually destroyed the key. Their punishment
		// is that they need to read the text to get back to the throne room.

		// TODO: Can I add a note on the sidebar or something, saying: DO NOT DROP AN ITEM UNLESS INSTRUCTED. EVERYTHING YOU ARE TOLD TO GET IS IMPORTANT!!!
		var cVampyriumCastleDrakan = new ConditionalStep(this, enterPortalInCastleDrakanLobby, "Solve the puzzles inside Vampyrium's Castle Drakan. Supplies are littered around the castle.");

		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, or(solidKey, usedSolidKey), or(fullMoonKey, hasUsedFullMoonKey)), cDestroyingTheStockpile);

		// TODO: It would be nice to have an "has used half moon key" to be sure we don't accidentally guide the user back here when they don't need the key anymore
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, doneWithThroneRoomPuzzle, not(halfMoonKey)), getKeyFromThroneRoom);

		// TODO: It would be nice to have an "has used half moon key" to be sure we don't accidentally guide the user back here when they don't need the key anymore
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, not(doneWithThroneRoomPuzzle)), cGetHalfMoonKey);

		// TODO: It would be nice to have an "has used gibbous moon key" to be sure we don't accidentally guide the user back here when they don't need the key anymore
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, or(newMoonKey, hasUsedNewMoonKey), gibbousMoonKey, or(fullMoonKey, hasUsedFullMoonKey)), cGetSolidKey);

		// TODO: It would be nice to have an "has used crescent moon key" to be sure we don't accidentally guide the user back here when they don't need the key anymore
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, crescentMoonKey, newMoonKey, or(gildedKey, hasUsedGildedKey), gibbousMoonKey), cGetFullMoonKey);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, crescentMoonKey, newMoonKey), cGetGildedAndGibbousKeys);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, crescentMoonKey), cGetNewMoonKey);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, hasGottenDrakanEmblemFromFireplace), cGetCrescentMoonKey);

		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, not(smallClockHand), smallClockHandNeedsReplacing), cGetSmallClockHand);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, not(largeClockHand), largeClockHandNeedsReplacing), cGetLargeClockHand);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, needToFinishClockPuzzle, not(inDiningRoom)), cGetBackToDiningRoom);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, needToFinishClockPuzzle), cSolveClockPuzzle);

		steps.put(72, cVampyriumCastleDrakan);
		// 72 -> 74 after talking to Veliaf in the emblem gallery
		steps.put(74, cVampyriumCastleDrakan);
		// 74 -> 76 after freeing Safalaan and Vanescula
		steps.put(76, cVampyriumCastleDrakan);
		// 76 -> 78 after talking to Ivan
		steps.put(78, cVampyriumCastleDrakan);

		// 78 -> 80 after destroying the blood stockpile
		steps.put(80, cVampyriumCastleDrakanDestroyedBloodStockpile);

		// 80 -> 82 after watching the cutscene with Drakan where you attempt to leave through the portal
		var cLeaveCastleDrakan = new ConditionalStep(this, leaveCastleDrakan);
		cLeaveCastleDrakan.addStep(inCutscene, watchLeaveCastleDrakanCutscene);
		steps.put(82, cLeaveCastleDrakan);

		// 82 -> 84 after leaving Vampyrium's castle drakan

		// TODO: Get rid of base todo step - should this be "get back to vampyrium?"
		var cCog = new ConditionalStep(this, todo, "Find the old cog for the drawbridge.");
		cCog.addStep(and(hasDeathPos, not(oldCogInOriginalPosition), not(oldCog)), pickupOldCogFromWhereYouDied);
		cCog.addStep(and(hasDeathPos, needBoltCutters, not(boltCutters)), pickupBoltCuttersFromWhereYouDied);
		cCog.addStep(and(hasDeathPos, needViturKey, not(viturKey)), pickupViturKeyFromWhereYouDied);
		cCog.addStep(and(hasDeathPos, not(isCrankWheelInOriginalPosition), needCrankWheel, not(crankWheel)), pickupCrankWheelFromWhereYouDied);
		cCog.addStep(and(needCrankWheel, not(crankWheel)), pickupCrankWheel);
		cCog.addStep(and(hasDeathPos, not(jovkaiKeyInOriginalPosition), needJovkaiKey), pickupJovkaiKeyFromWhereYouDied);


		cCog.addStep(and(oldCog, not(foundTheCog)), returnToVanescula);
		cCog.addStep(and(oldCog, needToTalkToIvanForSupplies), talkToIvanForSupplies);
		cCog.addStep(and(oldCog), returnToVanesculaReadyToLeave);

		cCog.addStep(and(not(oldCog), inSmithy), pickupOldCog);
		// TODO: Not sure if we need the "not unlocked altar house" req here
		cCog.addStep(and(not(oldCog), inAltarHouse, or(jovkaiKey, unlockedSmith), not(unlockedAltarHouse)), leaveAltarThroughDoor);
		cCog.addStep(and(not(oldCog), or(jovkaiKey, unlockedSmith)), enterSmith);

		// TODO: Not 100% sure if step order is correct here
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, inAltarHouse, not(jovkaiKey)), searchAltarChest);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, not(jovkaiKey), unlockedAltarHouse), enterAltarHouseThroughDoor);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, inAltarHouse, combinationLockWidgetOpen), solveAltarChestLockPW);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, or(crankWheel, crankedTheWheelInTheBank), or(trapdoorKey, unlockedTrapdoor), inBank, crankedTheWheelInTheBank), enterTrapdoor);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, or(crankWheel, crankedTheWheelInTheBank), or(trapdoorKey, unlockedTrapdoor), inBank), operateBankCrank);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, or(crankWheel, crankedTheWheelInTheBank), or(trapdoorKey, unlockedTrapdoor), or(myrmelKey, unlockedBank)), enterBank);

		cCog.addStep(and(needTrapdoorKey, not(trapdoorKey), inTrapdoorHouse), pickupTrapdoorKey);

		cCog.addStep(and(needTrapdoorKey, not(trapdoorKey), inTrapdoorHouse), pickupTrapdoorKey);
		cCog.addStep(and(needTrapdoorKey, not(trapdoorKey), or(shadumKey, unlockedTrapdoorHouse)), enterShadumDoor);

		// get shadum key
		cCog.addStep(and(needShadumKey, not(shadumKey), or(myrmelKey, unlockedBucketHouse), inBucketHouse, bucketOfWater), useBucketOfWaterOnNorthernBarrel);
		cCog.addStep(and(needShadumKey, not(shadumKey), or(myrmelKey, unlockedBucketHouse), bucketOfWater), enterBucketHouseAgain);
		cCog.addStep(and(needShadumKey, not(shadumKey), or(myrmelKey, unlockedBucketHouse), bucket), useBucketOnWell);
		cCog.addStep(and(needShadumKey, not(shadumKey), or(myrmelKey, unlockedBucketHouse), inBucketHouse), pickupBucket);
		cCog.addStep(and(needShadumKey, not(shadumKey), or(myrmelKey, unlockedBucketHouse)), enterBucketHouse);

		cCog.addStep(and(loopHalfOfKey, toothHalfOfKey), makeMyrmelKey);
		cCog.addStep(and(needMyrmelKey, inClothery, not(loopHalfOfKey), not(myrmelKey)), pickupLoopHalfOfKey);
		cCog.addStep(and(needMyrmelKey, or(boltCutters, hasUsedBoltCutters), toothHalfOfKey), openChainedDoor);

		cCog.addStep(and(needMyrmelKey, viturKeyOrUnlockedToothHalfOfKeyHouse, or(boltCutters, hasUsedBoltCutters), inToothHalfOfKeyHouse, not(myrmelKey)), pickupToothHalfOfKey);
		cCog.addStep(and(needMyrmelKey, inBoltCutterHouse, or(boltCutters, hasUsedBoltCutters)), leaveBoltCutterHouse);
		cCog.addStep(and(needMyrmelKey, viturKeyOrUnlockedToothHalfOfKeyHouse, or(boltCutters, hasUsedBoltCutters), not(myrmelKey)), enterToothHalfOfKeyHouse);
		cCog.addStep(and(needMyrmelKey, inBoltCutterHouse, combinationLockWidgetOpen), boltCutterShedCombinationLockPW);
		cCog.addStep(and(needBoltCutters, inBoltCutterHouse), searchShedBoltCutter);
		cCog.addStep(and(needBoltCutters, not(boltCutters), viturKey, inHouseNorthOfCrankBase), leaveBookcaseHouse);
		cCog.addStep(and(needBoltCutters, not(boltCutters), viturKeyOrUnlockedBoltCutterHouse), openViturDoorEastOfBookcaseHouse);
		cCog.addStep(dustyBook, readDustyBook);
		cCog.addStep(and(needViturKey, crankWheel, crankedTheWheel, inHouseNorthOfCrankBase), searchBookCase);
		cCog.addStep(and(needViturKey, crankWheel, crankedTheWheel), enterHouseNextToCrankWheel);
		cCog.addStep(and(crankWheel, not(crankedTheWheel)), crankWheel1);

		// TODO: In the sidebar here, we need to mention
		steps.put(84, cCog);
		steps.put(86, cCog);

		// TODO: If the user cancels out of this cutscene (if they can), is there an NPC or Object they can interact with to continue?
		var cRepairedBridge = new ConditionalStep(this, todo, "You have successfully repaired the bridge!");
		cRepairedBridge.addStep(inCutscene, watchCutsceneRepairedBridge);
		steps.put(88, cRepairedBridge);


		steps.put(90, fightDrakan1);

		var cFlee1 = new ConditionalStep(this, resupplyIfNeeded);
		cFlee1.addStep(inCutscene, flee1WatchTheCutscene);
		steps.put(92, cFlee1);

		var cFlee2 = new ConditionalStep(this, resupplyIfNeeded2);
		cFlee2.addStep(and(inSotfa6), cSotfa6);
		cFlee2.addStep(and(inSotfa5), cSotfa5);
		cFlee2.addStep(and(inSotfa4), sotfa4);
		cFlee2.addStep(and(inSotfa3), cSotfa3);
		cFlee2.addStep(and(inSotfa2), sotfa2);
		cFlee2.addStep(and(inSotfa1, anyNearbyFeralVyres), sotfa1);
		cFlee2.addStep(and(inSotfa1), sotfa1Exit);
		cFlee2.addStep(inCutscene, sotfaWatchTheCutscene);
		steps.put(94, cFlee2);

		// 94 -> 96: done with all the "running through forest" puzzles

		var cFlee3 = new ConditionalStep(this, sotfaWatchTheCutscene);
		cFlee3.addStep(inResupplyZone, resupplyIfNeeded2);
		steps.put(96, cFlee3);

		var cMysteriousWoman = new ConditionalStep(this, talkToMysteriousWoman1);
		cMysteriousWoman.addStep(inCutscene, mysteriousWomanWatchTheCutscene);
		steps.put(98, cMysteriousWoman);

		// 98 -> 100: after meeting & finishing speaking with Sugadinti

		steps.put(100, startTalkingToEfaritay);

		// 100 -> 102: spoke with Efaritay, and she told me to bring her herbs


		var cPickUpHerbs = new ConditionalStep(this, giveStewToSafalaan);
		cPickUpHerbs.addStep(and(amitireStew), giveStewToSafalaan);
		cPickUpHerbs.addStep(and(stew, amitireLeaves), combineStew3);
		cPickUpHerbs.addStep(and(uncookedStew), cookStew);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves, incompleteStew, cookedMeat), combineStew2);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves, incompleteStew, rawMeat), cookMeatOnRange);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves, incompleteStew), getRawMeatFromCupboard);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves, bowlOfWater, potato), combineStew);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves, bowlOfWater), getPotatoFromCupboard);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves, bowl), fillBowlWithWater);
		cPickUpHerbs.addStep(and(inPalace, amitireLeaves), searchShelvesForBowl);
		cPickUpHerbs.addStep(and(isOutsidePalace, amitireLeaves), enterPalace1);
		cPickUpHerbs.addStep(isOutsidePalace, pickFromAmitirePlant);
		cPickUpHerbs.addStep(inPalace, leavePalace1);
		steps.put(102, cPickUpHerbs);
		// 102 -> 104 when you make the stew for the first time
		steps.put(104, cPickUpHerbs);
		// 104 -> 106 when you gave the amitire stew to Safalaan
		steps.put(106, talkToSafalaanAfterFeedingHimStew);
		// 106 -> 108 after speaking 2 words to Safalaan
		steps.put(108, talkToEfaritayAfterFeedingStewToSafalaan);
		// 108 -> 110 after speaking to Efaritay about getting my weapon upgraded

		// TODO: Can a user make it here without their blisterwood flail?
		// TODO: Could we convert a conditional step into a graph program where a developer can click each requirement whether it
		// should pass or not, and show which step would be active?
		var cUpgradeWeapon = new ConditionalStep(this, talkToEfaritayAfterFeedingStewToSafalaan);
		cUpgradeWeapon.addStep(and(hallowedMarks, hammer, enhancedBlisterwoodSickle), createHallowedFlail);
		cUpgradeWeapon.addStep(and(hallowedMarks, enhancedBlisterwoodSickle), searchWorkbenchForHammer);
		cUpgradeWeapon.addStep(and(hallowedMarks, chisel, blessedSilverSickle, diamond), putDiamondInSickle);
		cUpgradeWeapon.addStep(and(hallowedMarks, diamondSickleB, diamondTablet), useEnchantDiamondTabletOnSickle);
		cUpgradeWeapon.addStep(and(hallowedMarks, knife, blisterwoodLogs, enchantedDiamondSickle), createEnhancedBlisterwoodSickle);

		cUpgradeWeapon.addStep(and(hallowedMarks, nor(hammer)), searchWorkbenchForHammer);
		cUpgradeWeapon.addStep(and(hallowedMarks, nor(chisel, diamondSickleB, enchantedDiamondSickle, enhancedBlisterwoodSickle)), searchWorkbenchForChisel);

		cUpgradeWeapon.addStep(and(hallowedMarks, not(knife)), searchWorkbenchForKnife);
		cUpgradeWeapon.addStep(and(hallowedMarks, hammer, knife, blisterwoodLogs, diamondSickleB), searchChestForDiamondTablet2);
		cUpgradeWeapon.addStep(and(hallowedMarks, hammer, knife, blisterwoodLogs, blessedSilverSickle), searchChestForDiamond);
		cUpgradeWeapon.addStep(and(hallowedMarks, hammer, knife, blisterwoodLogs), searchCrateForBlessedSilverSickle);
		cUpgradeWeapon.addStep(and(hallowedMarks, hammer, knife), searchCrateForBlisterwoodLogs);
		steps.put(110, cUpgradeWeapon);

		var cAfterUpgradingWeapon = new ConditionalStep(this, speakToIvanWithHallowedFlail);
		cAfterUpgradingWeapon.addStep(and(veliafProgressDone, inPalaceDungeon), climbUpstairs);

		cAfterUpgradingWeapon.addStep(and(veliafProgressDone, ivanProgressDone), talkToSugadintiAfterHelpingAllies);
		cAfterUpgradingWeapon.addStep(and(spokeWithVanescula, inPalaceDungeon), talkToVeliafInDungeonAgain);
		cAfterUpgradingWeapon.addStep(and(spokeWithVeliaf, inPalaceDungeon), talkToVanescula);
		cAfterUpgradingWeapon.addStep(and(ivanProgressDone, inPalaceDungeon), talkToVeliafInDungeon);
		cAfterUpgradingWeapon.addStep(ivanProgressDone, climbDownstairs);
		cAfterUpgradingWeapon.addStep(hasCraftedStakes, returnToIvan);
		cAfterUpgradingWeapon.addStep(and(canStartIvan, knife, blisterwoodLogs), fletchStakes);
		cAfterUpgradingWeapon.addStep(and(canStartIvan, knife), getLogsForStakes);
		cAfterUpgradingWeapon.addStep(canStartIvan, ivanSearchWorkbenchForKnife);
		steps.put(112, cAfterUpgradingWeapon);

		// 112 -> 114 after speaking to sugadinti and helping ivan & veliaf.

		var cGetReadyForCombat = new ConditionalStep(this, leavePalaceForCombat);
		cGetReadyForCombat.addStep(inCutscene, getReadyForCombatWatchTheCutscene);
		steps.put(114, cGetReadyForCombat);

		// 114 -> 116 after watching cutscene, and now you have to defend!!!
		steps.put(116, attackPortals);

		// 116 -> 118 after helping
		var cAfterHelpingWithPortals = new ConditionalStep(this, leaveDoors);
		cAfterHelpingWithPortals.addStep(inCutscene, leaveDoorsCutscene);
		steps.put(118, cAfterHelpingWithPortals);

		var cFinishedPortalsCutscene = new ConditionalStep(this, guardThePalace, "Help Efaritay Hallow and the Aranei defend the palace.");
		cFinishedPortalsCutscene.addStep(inCutscene, barricadeCutscene);
		cFinishedPortalsCutscene.addStep(inPalaceSouthernPart, passThroughBarricadeToHelp);
		steps.put(120, cFinishedPortalsCutscene);

		// 120 -> 122 after drakan breaks in
		// NOTE: For this step, and many others, if IN_VAMPYRIUM is 0 we need to guide the user back to vampyrium
		// Pulling back spear = Stand to his left or right
		var cDrakanFight2 = new ConditionalStep(this, fightDrakan2);
		cDrakanFight2.addStep(inPalaceSouthernPart, passThroughBarricadeToFightDrakan);
		steps.put(122, cDrakanFight2);

		// 122 -> 124: fought off drakan


		// TODO: What happens if you cancel out of this cutscene - is there an NPC or Object you can interact with to go back to the cutscene?
		var cFinishedDrakan2 = new ConditionalStep(this, todo);
		cFinishedDrakan2.addStep(inCutscene, finishedDrakan2Cutscene);
		steps.put(124, cFinishedDrakan2);

		// 124 -> 126: watched cutscene in palace
		var cFinishedDrakan3 = new ConditionalStep(this, talkToIvanInHauntedWoods);
		cFinishedDrakan3.addStep(inCutscene, leavingPalaceCutscene);
		steps.put(126, cFinishedDrakan3);

		// 126 -> 128: talked to Ivan after getting teleported
		var cFinishedDrakan4 = new ConditionalStep(this, talkToVeliafAfterLeavingPalace);
		cFinishedDrakan4.addStep(inCutscene, talkToVeliafAfterLeavingPalaceCutscene);
		steps.put(128, cFinishedDrakan4);

		// 128 -> 130: talked to Veliaf in Burgh de Rott
		steps.put(130, talkToSugadintiInBurghDeRott);

		var cGetToTob = new ConditionalStep(this, getToTob);
		cGetToTob.addStep(inCutscene, getToTobCutscene);
		steps.put(132, cGetToTob);
		steps.put(134, cGetToTob);

		// 134 -> 136: entering tob with sugadinti
		steps.put(136, ensureNothingBothersSugadinti);

		// 136 -> 138: after fighting all the bosses
		var cDoneWithTobFights = new ConditionalStep(this, talkToSugadintiAfterFinishingTob);
		cDoneWithTobFights.addStep(inCutscene, talkToSugadintiAfterFinishingTobCutscene);
		steps.put(138, cDoneWithTobFights);

		// 138 -> 140: after talking to sugadinti after tob bosses
		steps.put(140, headToBarrowsL);

		// 140 -> 142: ?
		var cDealWithVanescula2 = new ConditionalStep(this, headDownToVanescula, combatGear, hallowedFlail, food, prayerPotions);
		cDealWithVanescula2.addStep(inCutscene, headDownToVanesculaCutscene);
		steps.put(142, cDealWithVanescula2);

		var cDealWithVanescula3 = new ConditionalStep(this, headDownToVanescula, combatGear, hallowedFlail, food, prayerPotions);
		cDealWithVanescula3.addStep(inWyrdFight, fightTheWyrd);
		steps.put(144, cDealWithVanescula3);

		// 144 -> 146: dealt with Wyrd
		// TODO: What happens if you cancel out of this?
		var cDealtWithWyrd = new ConditionalStep(this, headDownToVanescula);
		cDealtWithWyrd.addStep(inCutscene, dealtWithWyrdCutscene);
		steps.put(146, cDealtWithWyrd);

		// 146 -> 148: finished Wyrd cutscene
		var cDealtWithWyrd2 = new ConditionalStep(this, findWyrd);
		cDealtWithWyrd.addStep(inCutscene, findWyrdCutscene);
		steps.put(148, cDealtWithWyrd2);

		// 148 -> 150: inspected fence
		steps.put(150, speakWithVeliafAfterInspectingFence);

		// 150 -> 152: spoke with veliaf and co under Burgh de Rott after inspecting fence
		var cDoSomething = new ConditionalStep(this, enterBurghDeRottDungeon, combatGear, hallowedFlail, food, prayerPotions);
		cDoSomething.addStep(inBurghDeRottDungeon, prepareFightDrakan3);
		cDoSomething.addStep(inCutscene, talkWithVeliafInBurghDeRottCutscene);
		steps.put(152, cDoSomething);

		// 152 -> 154: ?
		steps.put(154, fightDrakan3);

		// 154 -> 156: beat drakan for the third time
		var justBeatDrakanSecondToLastTime = new ConditionalStep(this, talkToVeliaf);
		justBeatDrakanSecondToLastTime.addStep(inCutscene, talkToVeliafCutscene);
		steps.put(156, justBeatDrakanSecondToLastTime);

		// 156 -> 158: watched the cutscene after beating drakan for the third time
		steps.put(158, talkToIvanInsideCastleDrakan);

		// 158 -> 160: after talking to Ivan,m and sugadinti enters castle drakan
		steps.put(160, talkToSugadintiInsideCastleDrakan);

		// 160 -> 162: ivan & veliaf left
		steps.put(162, talkToSugadintiInsideCastleDrakan);

		// 162 -> 164: finished speaking with sugadinti
		steps.put(164, talkToEfaritayOnIcyene);

		// 164 -> 166: after Efaritay opens portal to vampyrium
		var cEnterVampyriumForTheLastTime = new ConditionalStep(this, enterVampyriumForTheLastTime);
		cEnterVampyriumForTheLastTime.addStep(inCutscene, enterVampyriumForTheLastTimeCutscene);
		steps.put(166, cEnterVampyriumForTheLastTime);

		// 166 -> 168: after Efaritay and Safalaan left
		var cGoToFightDrakan4 = new ConditionalStep(this, enterVampyriumForTheLastTime);
		cGoToFightDrakan4.addStep(inCutscene, goToFightDrakan4Cutscene);
		cGoToFightDrakan4.addStep(inVampyriumVarbit, goToFightDrakan4);
		steps.put(168, cGoToFightDrakan4);

		var cFightDrakanLastTime = new ConditionalStep(this, enterVampyriumForTheLastTime);
		cFightDrakanLastTime.addStep(inCastleDrakanFight, fightDrakan4);
		cFightDrakanLastTime.addStep(inVampyriumVarbit, goToFightDrakan4);
		steps.put(170, cFightDrakanLastTime);

		// 170 -> 172: death cutscene
		steps.put(172, fightDrakan4Cutscene);

		// 172 -> 174: he fell down, now we're with Efaritay
		var cTalkToEfaritayAfterKillingDrakan = new ConditionalStep(this, talkToEfaritayAfterKillingDrakan);
		cTalkToEfaritayAfterKillingDrakan.addStep(inCutscene, finalQuestCutscene);
		steps.put(174, cTalkToEfaritayAfterKillingDrakan);

		steps.put(176, youHaveFinishedTheQuest);
		steps.put(178, youHaveFinishedTheQuest);

		return steps;
	}

	private ObjectStep bookTake(WorldPoint plinthPosition, String bookName, int plinthIDWithBookOnIt)
	{
		var step = new ObjectStep(this, plinthIDWithBookOnIt, plinthPosition, "Take " + bookName + " off the plinth.");
		step.addDialogStep("Take " + bookName + ".");

		return step;
	}

	private ObjectStep bookPut(WorldPoint plinthPosition, String bookName, Requirement requirement)
	{
		return new ObjectStep(this, 61295, plinthPosition, "Place " + bookName + " on the plinth.", requirement);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.A_NIGHT_AT_THE_THEATRE, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.SINS_OF_THE_FATHER, QuestState.FINISHED),
			new SkillRequirement(Skill.SLAYER, 74, false),
			new SkillRequirement(Skill.WOODCUTTING, 74, false),
			new SkillRequirement(Skill.SMITHING, 72, false),
			new SkillRequirement(Skill.COOKING, 72, false),
			new SkillRequirement(Skill.FLETCHING, 70, false),
			new SkillRequirement(Skill.MINING, 66, false /* Not confirmed if boostable or not */),
			new SkillRequirement(Skill.HUNTER, 65, false /* Not confirmed if boostable or not */),
			new SkillRequirement(Skill.CRAFTING, 64, false /* Not confirmed if boostable or not */),
			new SkillRequirement(Skill.HERBLORE, 64, false /* Not confirmed if boostable or not */),
			new SkillRequirement(Skill.MAGIC, 57, false /* Not confirmed if boostable or not */)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new CombatLevelRequirement(110)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			blisterwoodFlail,
			vyreNobleOutfit
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			combatGear,
			energyRestorePotion,
			drakansMedallion
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Lowerniel Drakan (lvl ???)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			// tome of experience listed under item rewards
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("30,000 Experience Tomes (Any skill above 70)", 33715, 6),
			new ItemReward("Sunspear", 33722)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Vampyrium"),
			new UnlockReward("Access to Veliaf's pub in Burgh de Rott"),
			new UnlockReward("Vyres of morytania are now friendly to you without wearing a vyre noble outfit")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			startQuest,
			cLookForIvan,
			inspectShrine,
			talkToIvanGoingToDarkmeyer,
			defendIvanFromVyres,
			talkToIvanAfterEscaping
		), List.of(
			blisterwoodFlail,
			combatGear,
			food,
			prayerPotions,
			vyreNobleOutfit
		)));

		sections.add(new PanelDetails("Finding Veliaf", List.of(
			talkToIvanOutsideSlepeChurch,
			askRoyAboutVeliaf,
			cLookIntoCommotionAtCrombwickManor,
			killVampyresWithVeliaf,
			talkToVeliafInCrombwickManor
		), List.of(
			blisterwoodFlail,
			combatGear,
			food,
			prayerPotions
		)));

		sections.add(new PanelDetails("The Writings", List.of(
			talkToIvanPaterdomus1,
			readSquiresJournal,
			talkToIvanPaterdomus2,
			killMonksOfZamorak,
			talkToIvanInPaterdomusTempleF1,
			cFindTheWritingsPW,
			talkToIvanAfterFindingTheWritings,
			readIvandisWritings,
			talkToIvanAfterReadingIvandisWritings
		), List.of(
			combatGear,
			food
		), List.of(
			freeInvSlots6
		)));

		sections.add(new PanelDetails("Infiltrating Castle Drakan", List.of(
			talkToIvanInPaterdomus,
			getToIvandisTomb,
			investigateHole,
			mineHole,
			headThroughHole,
			enterDaeyaltProcessingRoom,
			killVampsInDaeyaltRoom,
			talkToIvanAfterKillingVamps,
			enterCastleDrakanCellar,
			climbUpToCastleDrakanLobby,
			prayAtShrine,
			enterPortalInCastleDrakanLobby
		), List.of(
			blisterwoodFlail,
			combatGear,
			food,
			prayerPotions,
			anyPickaxe,
			drakansMedallion
		), List.of(
			freeInvSlots6
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Half moon key", List.of(
			enterThroneRoomFromDiningRoom,
			investigateThrone,
			hmkPullBustsPW,
			investigateThroneAgain
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Fixing the clocks", List.of(
			cGetSmallClockHand,
			enterThroneRoomFromRoomSouthOfThroneRoom,
			enterDiningRoomFromThroneRoom,
			enterHallwayWestOfDiningRoom,
			enterWestRoomFromHallwayWestOfDiningRoom,
			pickUpTinderbox,
			goUpFromStorageRoom,
			pickUpExplosiveBarrel,
			enterSouthDoorFromExplosiveRoom,
			searchCrateForDrakanEmblem1,
			exitEmblemRoom1,
			exitEastExplosiveRoom,
			enterNorthDoorInHallwayNextToExplosiveRoom,
			enterIntoHallwayNorthOfLobbyF1,
			enterUnmarkedNorthDoor,
			placeEmblem1OnReceptacle,
			enterVanesculasStudy,
			searchCrateInVanesculasStudyForLargeClockHand,
			returnToVanesculasHallway,
			returnToHallwayNorthOfLobbyF1,
			returnToLobbyF1,
			climbDownstairsToLobbyF0,
			enterDiningRoomFromLobby,
			placeSmallClockHandOnWesternClock,
			clickWesternClock,
			solveWesternClockPW,
			closeClock,
			placeLargeClockHandOnEasternClock,
			clickEasternClock,
			solveEasternClockPW,
			closeEasternClock,
			getEmblem2FromFireplace
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Crescent moon key", List.of(
			cmkToVanesculasHallwayFromHallwayNorthOfLobby,
			cmkPlaceEmblemInVanesculasStudy,
			cmkToVanesculasChamber,
			cmkBlowUpWallInVanesculasChamber,
			cmkEnterThroughHole,
			cmkTakeEmblem3,
			cmkLeaveVenatorRoom,
			cmkLeaveVanesculasChamber,
			cmkTakeEmblemFromVanesculasStudy,
			cmkLeaveVanesculasStudy,
			cmkRetrieveThirdEmblem,
			cmkPutEmblemInEastDoor,
			cmkEnterEastDoor,
			cmkPutEmblemInRanisHallwayNorth,
			cmkEnterRanisParlour,
			cmkGetSkull,
			cmkLeaveRanisParlourRoom,
			cmkRemoveEmblemRanisNorth,
			cmkClimbDownStairsRanisHallway,
			cmkPlaceEmblemDownstairs,
			cmkEnterWestChapelHallway,
			cmkPutEmblemInWestChapelHallway,
			cmkEnterEmblemGallery,
			cmkTalkToVeliaf,
			cmkOpenEmblemGalleryChest,
			cmkArrowChestPuzzleStepPW,
			cmkGetTheKeyFromTheChest
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - New moon key", List.of(
			enterEmblemGalleryHallway,
			enterRoomBehindLobby,
			killBugsAndTakeSyringeBarrel,
			searchCrateForVenatorStomach,
			retrievePlugFromTheSinkToTheWest,
			enterLarder,
			useSinkPlugOnSinkInLarder,
			turnSinkTapOn,
			takePipe,
			reachBehindCabinetWithPipe,
			searchLarderCrateForSyringePlunger,
			leaveLarder,
			useSharpKnifeOnVenatorCorpse,
			getTongsFromVenatorCorpse,
			getNeedleFromSink,
			assembleSyringe,
			drawBloodFromVenatorStomach,
			useSyringeOnChest,
			getLeftCrestHalfFromKitchenChest,
			leaveKitchen,
			enterLobbyFromEmblemGalleryHallway,
			climbDownstairsToLobbyBasement,
			enterAnotherVenatorRoom,
			killVenator,
			searchWeaponRackForOneAxe,
			placeBattleAxeOnStatue,
			getMace2,
			placeMaceOnStatueN,
			placeMaceOnStatueS,
			getSword,
			placeSwordOnStatue,
			openWeaponPuzzleChest,
			combineCrests,
			leaveWithFullCrest5,
			putFullCrestOnFireplaceInStudy,
			getNewMoonKeyFromFireplace
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Gilded and gibbous keys", List.of(
			ggkGoToBasement,
			ggkEnterBasementNorthRoom,
			ggkTakeGibbousMoonKey,
			ggkReturnToBasementHallway,
			ggkReturnToLobbyF0,
			ggkClimbUpToLobbyF1,
			ggkEnterLobbyF1GibbousMoonDoor,
			ggkEnterEastDoor,
			ggkEnterSouthDoor,
			ggkEnterSouthEastDoor,
			ggkGetOrnateKnife,
			ggkPullLeverInOrnateKnifeRoom,
			ggkEnterUpperStoreroomPortal,
			ggkLeaveGuestChamberStoreroom,
			ggkEnterRoomWithVenator,
			ggkKillVenator,
			ggkpLightFireplace,
			ggkpSearchChest,
			ggkpTryOpenLockbox,
			ggkpSolveLockboxPuzzlePW,
			ggkpPlaceFancyGemInHead,
			ggkpEnterSouthWestDoor,
			ggkpSolveDoorPuzzlePW,
			ggkpSearchBookcaseForBook,
			ggkpOpenMysteriousBook,
			ggkpLeaveLibrary,
			ggkpPlaceFancyGemInHead2,
			ggkpSearchVenatorHead,
			ggkpLeavePuzzleRoom,
			ggkPlaceItemsOnDisplayCase,
			ggkSearchDisplayCaseForGildedKey
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Full moon key", List.of(
			fmkClimbDownstairsFromDisplayCase,
			fmkGoThroughLobbyF1NorthDoor,
			fmkEnterGildedDoorToWest,
			fmkNorthWest,
			fmkKillVenator,
			fmkTalkToIvan1,
			fmkGetGildedBook,
			fmkUseGildedBookOnWesternGildedBookcase,
			fmkClickGildedBookcase,
			fmkGildedBookPuzzlePW,
			fmkEnterSecretPassage,
			fmkTakeFullMoonKey
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Solid key", List.of(
			skLeaveEastDoor,
			skLeaveGildedDoor,
			skEnterScratchedDoor,
			skEnterEasternDoor,
			skClimbDownStairs,
			skEnterWesternDoor,
			skEnterEastFullMoonDoor,
			skGetCloudyGreyPotion,
			bloodBasin,
			smokeBasin,
			iceBasin,
			shadowBasin,
			skSearchAltarForAncientSymbol,
			skEnterEastDoorFromBottleRoom,
			skPullLever,
			skEnterPortal,
			skLeaveServantsQuarters,
			skLeaveExplosiveRoom,
			skLeaveExplosiveRoomHallway,
			skLeaveLobbyF1,
			skLeaveDiningRoomF1,
			skLeaveThroneRoomF1,
			skGetAncientShield,
			skCombineAncientShieldAndAncientSymbol,
			skLeaveStudy,
			skLeaveThroneRoomF1Again,
			skLeaveLobbyF1Again,
			skEnterGibbousMoonDoor,
			skEnterFullMoonDoor,
			skUseShieldOnEmptyMount,
			skGetSolidKey
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping Castle Drakan - Destroying the stockpile", List.of(
			dtsLeaveSolidKeyRoom,
			dtsLeavePuzzleRoom,
			dtsLeaveDisplayCaseRoom,
			dtsOpenSolidDoor,
			cDestroyingTheStockpileLaboratoryStepPW,
			dtsLeaveLaboratory,
			dtsDestroyBloodStockpile,
			cVampyriumCastleDrakanDestroyedBloodStockpile
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		// continue at bmr-mrchintickle.mkv 00:36:19
		sections.add(new PanelDetails("Let the hunt begin", List.of(
			leaveCastleDrakan,
			pickupCrankWheel,
			crankWheel1,
			enterHouseNextToCrankWheel,
			searchBookCase,
			readDustyBook,
			searchShedBoltCutter,
			boltCutterShedCombinationLockPW,
			pickupToothHalfOfKey,
			pickupLoopHalfOfKey,
			makeMyrmelKey,
			pickupBucket,
			useBucketOnWell,
			enterBucketHouseAgain,
			useBucketOfWaterOnNorthernBarrel,
			pickupTrapdoorKey,
			operateBankCrank,
			enterBank,
			enterTrapdoor,
			searchAltarChest,
			solveAltarChestLockPW,
			leaveAltarThroughDoor,
			pickupOldCog,
			returnToVanescula,
			returnToVanesculaReadyToLeave
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Escaping the forest", List.of(
			fightDrakan1,
			resupplyIfNeeded2,
			sotfa1,
			sotfa1Exit,
			sotfa2,
			sotfa3AvoidAnimals,
			sotfa3Exit,
			sotfa4,
			sotfa5,
			sotfa5Exit,
			sotfa6WrangleSnakes,
			sotfa6CombineSnakes,
			sotfa6UseRopeOnBranch,
			sotfa6SwingAcrossWater,
			sotfa6Exit,
			talkToMysteriousWoman1
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Curing Safalaan", List.of(
			startTalkingToEfaritay,
			pickFromAmitirePlant,
			searchShelvesForBowl,
			fillBowlWithWater,
			getPotatoFromCupboard,
			combineStew,
			getRawMeatFromCupboard,
			cookMeatOnRange,
			combineStew2,
			cookStew,
			combineStew3,
			giveStewToSafalaan,
			talkToSafalaanAfterFeedingHimStew,
			talkToEfaritayAfterFeedingStewToSafalaan
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Gearing up", List.of(
			searchWorkbenchForHammer,
			searchWorkbenchForChisel,
			searchWorkbenchForKnife,
			searchCrateForBlisterwoodLogs,
			searchCrateForBlessedSilverSickle,
			searchChestForDiamond,
			putDiamondInSickle,
			searchChestForDiamondTablet2,
			useEnchantDiamondTabletOnSickle,
			createEnhancedBlisterwoodSickle,
			createHallowedFlail,
			speakToIvanWithHallowedFlail,
			ivanSearchWorkbenchForKnife,
			getLogsForStakes,
			fletchStakes,
			returnToIvan,
			talkToVeliafInDungeon,
			talkToVanescula,
			talkToVeliafInDungeonAgain,
			talkToSugadintiAfterHelpingAllies
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Guarding the palace", List.of(
			leavePalaceForCombat,
			attackPortals,
			leaveDoors,
			guardThePalace,
			fightDrakan2,
			finishedDrakan2Cutscene
		), List.of(
			blisterwoodFlail,
			combatGear,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Night at the theatre", List.of(
			talkToIvanInHauntedWoods,
			talkToVeliafAfterLeavingPalace,
			talkToSugadintiInBurghDeRott,
			getToTob,
			ensureNothingBothersSugadinti,
			talkToSugadintiAfterFinishingTob
		), List.of(
			combatGear,
			food,
			prayerPotions,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Safalaan's fate", List.of(
			headToBarrowsL,
			headDownToVanescula,
			fightTheWyrd,
			findWyrd,
			speakWithVeliafAfterInspectingFence
		), List.of(
			hallowedFlail,
			combatGear,
			food,
			prayerPotions,
			drakansMedallion
		)));

		sections.add(new PanelDetails("Night of the blood moon", List.of(
			prepareFightDrakan3,
			fightDrakan3,
			talkToVeliaf,
			talkToIvanInsideCastleDrakan,
			talkToSugadintiInsideCastleDrakan,
			talkToEfaritayOnIcyene,
			enterVampyriumForTheLastTime,
			goToFightDrakan4,
			fightDrakan4,
			talkToEfaritayAfterKillingDrakan,
			youHaveFinishedTheQuest
		), List.of(
			hallowedFlail,
			combatGear
		)));

		sections.add(new PanelDetails("TODO", List.of(
			todo
		), List.of(
		)));

		return sections;
	}
}
