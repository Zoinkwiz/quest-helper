// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
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
import com.questhelper.requirements.player.InInstanceRequirement;
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
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
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
	final CastleDrakan castleDrakan = new CastleDrakan(this);

	// Required items
	ItemRequirement blisterwoodFlail;
	ItemRequirement vyreNobleOutfit;

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

	ZoneRequirement inTob;
	ZoneRequirement inWyrdFight;
	ZoneRequirement inBurghDeRottDungeon;

	ZoneRequirement inCastleDrakanFight;

	// Miscellaneous requirements
	VarbitRequirement inCutscene;
	VarplayerRequirement followedByIvan;
	VarbitRequirement canReceivePickaxeFromIvan;
	VarbitRequirement needTeleportUnlock;
	VarbitRequirement inVampyriumVarbit;

	WidgetPresenceRequirement combinationLockWidgetOpen;
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
	ItemRequirement viturKeyForBoltCutterHouse;
	Conditions viturKeyOrUnlockedBoltCutterHouse;
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
	Conditions anyNearbyFeralVyres;
	ObjectCondition tree6ChoppedDown;
	ObjectCondition tree7Untouched;
	ObjectCondition tree7SlightlyChopped;
	ObjectCondition tree7Chopped;
	ObjectCondition tree8Untouched;
	ObjectCondition tree8SlightlyChopped;
	ObjectCondition tree8Chopped;
	ObjectCondition ropedTree;
	VarbitRequirement canStartIvan;
	VarbitRequirement hasCraftedStakes;
	VarbitRequirement ivanProgressDone;
	VarbitRequirement spokeWithVeliaf;
	VarbitRequirement spokeWithVanescula;
	VarbitRequirement veliafProgressDone;

	// Crossing the drawbridge (82-88)
	VarplayerRequirement hasDeathPos;

	// Steps

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

	/// TODO: LATER COG WHEEL
	DetailedQuestStep pickupCrankWheel;
	DetailedQuestStep pickupCrankWheelFromWhereYouDied;

	/// 80 (Vampyrium Castle Drakan after destroying the blood stockpile)

	private ObjectStep leaveCastleDrakan;
	private DetailedQuestStep watchLeaveCastleDrakanCutscene;

	/// Shared by the clock run and the crescent run: both send the player back for these when one is lost.
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
	private ObjectStep goDownToTalkToVeliafAfterLeavingPalace;
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
	private NpcStep talkToVeliafToReturnToDrakan3;
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
		inMyrequeHideoutOldManRal = new ZoneRequirement(
			new Zone(new WorldPoint(3588, 9609, 0), new WorldPoint(3606, 9619, 0)));

		atCastleDrakanCourtyard = new ZoneRequirement(
			new Zone(new WorldPoint(3589, 3347, 0), new WorldPoint(3561, 3367, 0)));

		inSlepeChurchDungeon = new ZoneRequirement(
			new Zone(14999),
			new Zone(15000),
			new Zone(15255),
			new Zone(15256),
			new Zone(15257),
			new Zone(15511),
			new Zone(15512),
			new Zone(15513));

		inCrombwickManor = new ZoneRequirement(
			new Zone(new WorldPoint(3714, 3361, 0), new WorldPoint(3737, 3355, 0)),
			new Zone(new WorldPoint(3721, 3366, 0), new WorldPoint(3725, 3361, 0)),
			new Zone(new WorldPoint(3727, 3362, 0), new WorldPoint(3732, 3359, 0)),
			new Zone(new WorldPoint(3721, 3354, 0), new WorldPoint(3729, 3351, 0)));

		inPaterdomusTempleDungeon = new ZoneRequirement(new Zone(13466), new Zone(13722));

		inPaterdomusTempleF0 = new ZoneRequirement(
			new Zone(new WorldPoint(3409, 3483, 0), new WorldPoint(3411, 3494, 0)),
			new Zone(new WorldPoint(3408, 3485, 0), new WorldPoint(3408, 3486, 0)),
			new Zone(new WorldPoint(3408, 3491, 0), new WorldPoint(3408, 3492, 0)),
			new Zone(new WorldPoint(3412, 3484, 0), new WorldPoint(3415, 3493, 0)),
			new Zone(new WorldPoint(3416, 3483, 0), new WorldPoint(3417, 3494, 0)),
			new Zone(new WorldPoint(3418, 3484, 0), new WorldPoint(3418, 3493, 0)));

		inPaterdomusTempleF1 = new ZoneRequirement(
			new Zone(new WorldPoint(3408, 3483, 1), new WorldPoint(3419, 3494, 1)));

		inIvandisTomb = new ZoneRequirement(new Zone(new WorldPoint(3485, 9879, 0), new WorldPoint(3516, 9853, 0)));

		inCastleDrakanMines = new ZoneRequirement(
			new Zone(new WorldPoint(3119, 7479, 2), new WorldPoint(3088, 7433, 2)));

		inCastleDrakanDaeyaltProcessingArea = new ZoneRequirement(
			new Zone(new WorldPoint(3196, 7447, 0), new WorldPoint(3164, 7469, 0)));

		inCastleDrakanCellar = new ZoneRequirement(
			new Zone(new WorldPoint(3142, 7595, 0), new WorldPoint(3187, 7569, 0)));

		inCastleDrakanLobby = new ZoneRequirement(
			new Zone(new WorldPoint(3172, 7724, 0), new WorldPoint(3146, 7699, 0)));

		inVampyriumCastleDrakanLobbyCutscene = new ZoneRequirement(
			new Zone(new WorldPoint(2216, 7262, 0), new WorldPoint(2429, 7475, 0)));

		inHouseNorthOfCrankBase = new ZoneRequirement(
			new Zone(new WorldPoint(2589, 7864, 0), new WorldPoint(2585, 7857, 0)));

		inBoltCutterHouse = new ZoneRequirement(
			new Zone(new WorldPoint(2609, 7860, 0), new WorldPoint(2615, 7856, 0)),
			new Zone(new WorldPoint(2612, 7855, 0), new WorldPoint(2619, 7850, 0)),
			new Zone(new WorldPoint(2621, 7859, 0), new WorldPoint(2616, 7855, 0)));

		inToothHalfOfKeyHouse = new ZoneRequirement(
			new Zone(new WorldPoint(2596, 7849, 0), new WorldPoint(2600, 7843, 0)));

		inClothery = new ZoneRequirement(new Zone(new WorldPoint(2592, 7812, 0), new WorldPoint(2588, 7818, 0)));

		inBucketHouse = new ZoneRequirement(
			new Zone(new WorldPoint(2592, 7834, 0), new WorldPoint(2606, 7830, 0)),
			new Zone(new WorldPoint(2596, 7834, 0), new WorldPoint(2602, 7826, 0)));

		inTrapdoorHouse = new ZoneRequirement(new Zone(new WorldPoint(2620, 7816, 0), new WorldPoint(2614, 7812, 0)));

		inBank = new ZoneRequirement(new Zone(new WorldPoint(2582, 7835, 0), new WorldPoint(2578, 7829, 0)));

		inAltarHouse = new ZoneRequirement(
			new Zone(new WorldPoint(2575, 7850, 0), new WorldPoint(2578, 7846, 0)),
			new Zone(new WorldPoint(2579, 7850, 0), new WorldPoint(2583, 7842, 0)),
			new Zone(new WorldPoint(2584, 7850, 0), new WorldPoint(2592, 7844, 0)));

		inSmithy = new ZoneRequirement(new Zone(new WorldPoint(2578, 7818, 0), new WorldPoint(2582, 7812, 0)));

		inResupplyZone = new ZoneRequirement(
			new Zone(new WorldPoint(2853, 7640, 0), new WorldPoint(2837, 7655, 0)),
			new Zone(new WorldPoint(2950, 7831, 0), new WorldPoint(2973, 7813, 0)));

		// fleeing through the forest
		inSotfa1 = new ZoneRequirement(new Zone(new WorldPoint(2950, 7846, 0), new WorldPoint(2971, 7867, 0)));

		inSotfa2 = new ZoneRequirement(new Zone(new WorldPoint(2997, 7927, 0), new WorldPoint(2953, 7876, 0)));
		ts1Zone = new ZoneRequirement(new Zone(new WorldPoint(2955, 7896, 0), new WorldPoint(2968, 7879, 0)));
		ts2Zone = new ZoneRequirement(new Zone(new WorldPoint(2968, 7900, 0), new WorldPoint(2965, 7897, 0)));
		ts3Zone = new ZoneRequirement(
			new Zone(new WorldPoint(2965, 7900, 0), new WorldPoint(2960, 7903, 0)),
			new Zone(new WorldPoint(2966, 7903, 0), new WorldPoint(2977, 7901, 0)),
			new Zone(new WorldPoint(2976, 7900, 0), new WorldPoint(2970, 7897, 0)));
		ts4Zone = new ZoneRequirement(
			new Zone(new WorldPoint(2965, 7904, 0), new WorldPoint(2973, 7911, 0)),
			new Zone(new WorldPoint(2974, 7907, 0), new WorldPoint(2997, 7904, 0)),
			new Zone(new WorldPoint(2980, 7903, 0), new WorldPoint(2994, 7886, 0)),
			new Zone(new WorldPoint(2969, 7893, 0), new WorldPoint(2983, 7884, 0)));
		ts5Zone = new ZoneRequirement(new Zone(new WorldPoint(2975, 7908, 0), new WorldPoint(2980, 7910, 0)));
		ts6Zone = new ZoneRequirement(
			new Zone(new WorldPoint(2981, 7909, 0), new WorldPoint(2982, 7912, 0)),
			new Zone(new WorldPoint(2981, 7912, 0), new WorldPoint(2980, 7912, 0)),
			new Zone(new WorldPoint(2978, 7911, 0), new WorldPoint(2981, 7915, 0)));
		ts7ZoneSouth = new ZoneRequirement(new WorldPoint(2982, 7914, 0));
		ts7ZoneWest = new ZoneRequirement(new Zone(new WorldPoint(2981, 7916, 0), new WorldPoint(2981, 7915, 0)));
		ts8ZoneSouth = new ZoneRequirement(new Zone(new WorldPoint(2982, 7916, 0), new WorldPoint(2984, 7916, 0)));
		ts8ZoneWest = new ZoneRequirement(
			new Zone(new WorldPoint(2982, 7917, 0), new WorldPoint(2983, 7917, 0)),
			new Zone(new WorldPoint(2982, 7915, 0)));

		inSotfa3 = new ZoneRequirement(new Zone(new WorldPoint(2899, 7924, 0), new WorldPoint(2932, 7886, 0)));
		nearSotfa3Exit = new ZoneRequirement(new Zone(new WorldPoint(2924, 7895, 0), new WorldPoint(2929, 7889, 0)));

		inSotfa4 = new ZoneRequirement(new Zone(new WorldPoint(2986, 7844, 0), new WorldPoint(3006, 7867, 0)));

		inSotfa5 = new ZoneRequirement(new Zone(new WorldPoint(2903, 7829, 0), new WorldPoint(2927, 7855, 0)));

		inSotfa6 = new ZoneRequirement(new Zone(new WorldPoint(3018, 7926, 0), new WorldPoint(3055, 7884, 0)));
		acrossSotfa6Pond = new ZoneRequirement(new Zone(new WorldPoint(3047, 7892, 0), new WorldPoint(3033, 7885, 0)));

		var palace1 = new Zone(new WorldPoint(3155, 7823, 1), new WorldPoint(3181, 7843, 1));
		inPalace = new ZoneRequirement(palace1, new Zone(new WorldPoint(3177, 7863, 1), new WorldPoint(3159, 7844, 1)));
		inPalaceSouthernPart = new ZoneRequirement(palace1);
		inPalaceDungeon = new ZoneRequirement(
			new Zone(new WorldPoint(3155, 7823, 0), new WorldPoint(3181, 7843, 0)),
			new Zone(new WorldPoint(3177, 7863, 0), new WorldPoint(3159, 7844, 0)));

		isOutsidePalace = new ZoneRequirement(new Zone(new WorldPoint(2989, 7690, 0), new WorldPoint(2963, 7673, 0)));

		inWyrdFight = new ZoneRequirement(new Zone(11892));

		inBurghDeRottDungeon = new ZoneRequirement(
			new Zone(new WorldPoint(3489, 9632, 0), new WorldPoint(3500, 9622, 0)));

		Zone tobArea = new Zone(new WorldPoint(3155, 7980, 0), new WorldPoint(3180, 7960, 0));
		inTob = new ZoneRequirement(tobArea);

		inCastleDrakanFight = new ZoneRequirement(
			new Zone(new WorldPoint(2514, 7853, 3), new WorldPoint(2495, 7823, 3)));
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		blisterwoodFlail = new ItemRequirement("Blisterwood flail", ItemID.BLISTERWOOD_FLAIL);
		blisterwoodFlail.addAlternates(ItemID.HALLOWED_FLAIL);
		// TODO: Add Hallowed Flail as the preferred alternative in terms of item IDs actually
		blisterwoodFlail.setTooltip("You can buy another Blisterwood Flail from Ivan in the Myreque Hideout in Old Man Ral's basement, or during the quest from Old Man Ral just outside the hideout");

		vyreNobleOutfit = new ItemRequirements("Vyre noble outfit",
			new ItemRequirement("Vyre noble top", ItemID.VYRELORD_TORSO),
			new ItemRequirement("Vyre noble legs", ItemID.VYRELORD_LEGS),
			new ItemRequirement("Vyre noble shoes", ItemID.VYRELORD_SHOES)).isNotConsumed();
		vyreNobleOutfit.setHighlightInInventory(true);
		vyreNobleOutfit.setMustBeEquipped(true);
		// vyreNobleOutfit.canBeObtainedDuringQuest();
		vyreNobleOutfit.setTooltip("Can be obtained during the quest in the chest next to Ivan");

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
		squiresJournal = new ItemRequirement("Squire's journal", ItemID.MYQ6_SQUIRE_JOURNAL);
		essiandarsNotes = new ItemRequirement("Essiandar's notes", ItemID.MYQ6_ESSIANDAR_JOURNAL);
		scruffyNotebook = new ItemRequirement("Scruffy notebook", ItemID.MYQ6_DERYGULL_JOURNAL);
		sarlsJournal = new ItemRequirement("Sarl's journal", ItemID.MYQ6_SARL_JOURNAL);
		theLifeOfFriar = new ItemRequirement("The Life of Friar", ItemID.MYQ6_FRIAR_JOURNAL);
		piousProceedings = new ItemRequirement("Pious proceedings", ItemID.MYQ6_ERYSAIL_JOURNAL);
		fromMisthalinToMorytania = new ItemRequirement("Misthalin to Morytania", ItemID.MYQ6_IRIANDUL_JOURNAL);
		ivandisWritings = new ItemRequirement("Ivandis' writings", ItemID.MYQ6_IVANDIS_WRITINGS);

		// Miscellaneous requirements
		// TODO: Should this be it's own "InCutsceneRequirement"?
		inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);
		// TODO: Should this be it's own "FollowerNPCRequirement"?
		followedByIvan = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);
		canReceivePickaxeFromIvan = new VarbitRequirement(VarbitID.MYQ6_IVAN_GIVEN_PICKAXE, 0);
		needTeleportUnlock = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_TELEPORT, 0);

		inVampyriumVarbit = new VarbitRequirement(VarbitID.IN_VAMPYRIUM, 1);
		combinationLockWidgetOpen = new WidgetPresenceRequirement(InterfaceID.CombinationLock.CONTENTS);

		// Crossing the drawbridge (82-88)
		hasDeathPos = new VarplayerRequirement(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS, 0, Operation.GREATER_EQUAL);
		crankWheel = new ItemRequirement("Crank wheel", ItemID.SANGVESTI_CRANK_WHEEL);
		crankedTheWheel = new VarbitRequirement(VarbitID.SANGVESTI_FANCY_HOUSE_3_DOOR, 1);
		crankedTheWheelInTheBank = new VarbitRequirement(VarbitID.SANGVESTI_INTERIOR_BANK_DOOR, 1);
		needCrankWheel = nand(crankedTheWheel, crankedTheWheelInTheBank);
		crankWheelForBank = crankWheel.hideConditioned(crankedTheWheelInTheBank);
		jovkaiKey = new ItemRequirement("Jovkai key", ItemID.SANGVESTI_JOVKAI_KEY);
		unlockedSmith = new VarbitRequirement(VarbitID.SANGVESTI_BLACKSMITH_DOOR, 1);
		jovkaiKey.setConditionToHide(unlockedSmith);
		needJovkaiKey = nand(unlockedSmith);
		jovkaiKeyInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_CHAPEL, 1);
		dustyBook = new ItemRequirement("Dusty book", ItemID.SANGVESTI_VITUR_KEY_BOOK);
		viturKey = new ItemRequirement("Vitur key", ItemID.SANGVESTI_VITUR_KEY);
		// Important vitur key use:
		var unlockedBoltCutterHouse = new VarbitRequirement(VarbitID.SANGVESTI_VITUR_MANOR_DOOR, 1);
		viturKeyForBoltCutterHouse = viturKey.hideConditioned(unlockedBoltCutterHouse);
		viturKeyOrUnlockedBoltCutterHouse = or(viturKey, unlockedBoltCutterHouse);
		// Important vitur key use:
		var unlockedToothHalfOfKeyHouse = new VarbitRequirement(VarbitID.SANGVESTI_FOOD_SHOP_DOOR, 1);
		viturKeyForToothHalfOfKeyHouse = viturKey.hideConditioned(unlockedToothHalfOfKeyHouse);
		viturKeyOrUnlockedToothHalfOfKeyHouse = or(viturKey, unlockedToothHalfOfKeyHouse);
		needViturKey = nand(unlockedBoltCutterHouse, unlockedToothHalfOfKeyHouse);
		needBoltCutters = new VarbitRequirement(VarbitID.SANGVESTI_CLOTHES_SHOP_DOOR, 0);
		hasUsedBoltCutters = new VarbitRequirement(VarbitID.SANGVESTI_CLOTHES_SHOP_DOOR, 1);
		boltCutters = new ItemRequirement("Bolt cutters", ItemID.SANGVESTI_BOLT_CUTTERS);
		boltCutters.setConditionToHide(hasUsedBoltCutters);

		var questVB = new VarbitBuilder(VarbitID.MYQ4);
		oldCog = new ItemRequirement("Old cog", ItemID.SANGVESTI_COG).hideConditioned(questVB.ge(88));

		unlockedTrapdoor = new VarbitRequirement(VarbitID.SANGVESTI_TRAPDOOR, 1);

		trapdoorKey = new ItemRequirement("Trapdoor key", ItemID.SANGVESTI_TRAPDOOR_KEY);
		trapdoorKey.setConditionToHide(unlockedTrapdoor);

		toothHalfOfKey = new ItemRequirement("Tooth half of key", ItemID.SANGVESTI_MYRMEL_KEY_TOOTH);
		loopHalfOfKey = new ItemRequirement("Loop half of key", ItemID.SANGVESTI_MYRMEL_KEY_LOOP);

		isCrankWheelInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_BASIC_HOUSE_3, 1);

		needTrapdoorKey = nand(unlockedTrapdoor);

		oldCogInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_BLACKSMITH, 1);

		myrmelKey = new ItemRequirement("Myrmel key", ItemID.SANGVESTI_MYRMEL_KEY);

		unlockedBucketHouse = new VarbitRequirement(VarbitID.SANGVESTI_PUB_DOOR, 1);
		myrmelKeyForBucketHouse = myrmelKey.hideConditioned(unlockedBucketHouse);
		unlockedBank = new VarbitRequirement(VarbitID.SANGVESTI_BANK_DOOR, 1);
		myrmelKeyForBank = myrmelKey.hideConditioned(unlockedBank);
		needMyrmelKey = nand(unlockedBucketHouse, unlockedBank);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_WATER);

		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);

		shadumKey = new ItemRequirement("Shadum key", ItemID.SANGVESTI_SHADUM_KEY);
		unlockedTrapdoorHouse = new VarbitRequirement(VarbitID.SANGVESTI_FANCY_HOUSE_1_DOOR, 1);
		shadumKeyForTrapdoorHouse = shadumKey.hideConditioned(unlockedTrapdoorHouse);
		// TODO (what is this todo for)
		needShadumKey = nand(unlockedTrapdoorHouse);

		foundTheCog = new VarbitRequirement(VarbitID.MYQ6_VANESCULA_DRAKAN_FOUND_THE_COG, 1);
		var xd2 = new VarbitRequirement(VarbitID.MYQ6_SANGVESTI_SUPPLY_WARNING, 0);
		needToTalkToIvanForSupplies = and(foundTheCog, xd2);

		unlockedAltarHouse = new VarbitRequirement(VarbitID.SANGVESTI_CHAPEL_BACK_DOOR_2, 1);

		anyAxe = new ItemRequirement("Any axe", ItemCollections.AXES);

		anyNearbyNylocas = or(new NpcRequirement(16236), new NpcRequirement(16237));

		deadSnake3 = new ItemRequirement("Dead blood serpent", ItemID.SOTFA_FOREST_DEAD_BLOOD_SERPENT, 3);
		serpentRope = new ItemRequirement("Serpent rope", ItemID.SOTFA_FOREST_ROPE);

		amitireLeaves = new ItemRequirement("Amitire leaves", ItemID.MYQ6_LEAVES);
		bowl = new ItemRequirement("Bowl", ItemID.BOWL_EMPTY);
		bowlOfWater = new ItemRequirement("Bowl of water", ItemID.BOWL_WATER);
		potato = new ItemRequirement("Potato", ItemID.POTATO);
		rawMeat = new ItemRequirement("Raw impaler meat", ItemID.RAW_IMPALER_MEAT);
		cookedMeat = new ItemRequirement("Cooked meat", ItemID.COOKED_MEAT);
		incompleteStew = new ItemRequirement("Incomplete stew", ItemID.STEW1);
		uncookedStew = new ItemRequirement("Uncooked stew", ItemID.UNCOOKED_STEW);
		stew = new ItemRequirement("Stew", ItemID.STEW);
		amitireStew = new ItemRequirement("Amitire stew", ItemID.MYQ6_STEW);

		hallowedMarks = new ItemRequirement("Hallowed marks", ItemID.MYQ6_HALLOWED_MARKS);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		blisterwoodLogs = new ItemRequirement("Blisterwood logs", ItemID.BLISTERWOOD_LOGS);
		blessedSilverSickle = new ItemRequirement("Silver sickle (b)", ItemID.SILVER_SICKLE_BLESSED);
		diamond = new ItemRequirement("Diamond", ItemID.DIAMOND);
		diamondTablet = new ItemRequirement("Enchant diamond", ItemID.POH_TABLET_ENCHANTDIAMOND);
		diamondSickleB = new ItemRequirement("Diamond sickle (b)", ItemID.SILVER_SICKLE_DIAMOND);
		enchantedDiamondSickle = new ItemRequirement("Enchanted diamond sickle (b)", ItemID.SILVER_SICKLE_DIAMOND_ENCHANTED);
		enhancedBlisterwoodSickle = new ItemRequirement("Blisterwood sickle (e)", ItemID.BLISTERWOOD_SICKLE_ENHANCED);
		blisterwoodFlailUnequipped = blisterwoodFlail.copy();
		blisterwoodFlailUnequipped.setMustBeUnequipped(true);
		hallowedFlail = new ItemRequirement("Hallowed flail", ItemID.HALLOWED_FLAIL);

		var anyNearbyFeralVyres1 = new NpcCondition(16229);
		var anyNearbyFeralVyres2 = new NpcCondition(16230);
		var anyNearbyFeralVyres3 = new NpcCondition(16231);
		var anyNearbyFeralVyres4 = new NpcCondition(16232);
		anyNearbyFeralVyres = or(anyNearbyFeralVyres1, anyNearbyFeralVyres2, anyNearbyFeralVyres3, anyNearbyFeralVyres4);

		tree6ChoppedDown = new ObjectCondition(61954, new WorldPoint(2980, 7913, 0));
		tree7Untouched = new ObjectCondition(61955, new WorldPoint(2982, 7915, 0));
		tree7SlightlyChopped = new ObjectCondition(61956, new WorldPoint(2982, 7915, 0));
		tree7Chopped = new ObjectCondition(61957, new WorldPoint(2982, 7915, 0));
		tree8Untouched = new ObjectCondition(61955, new WorldPoint(2984, 7917, 0));
		tree8SlightlyChopped = new ObjectCondition(61956, new WorldPoint(2984, 7917, 0));
		tree8Chopped = new ObjectCondition(61957, new WorldPoint(2984, 7917, 0));

		ropedTree = new ObjectCondition(61949, new WorldPoint(3040, 7895, 0));

		var ivanProgress = new VarbitBuilder(VarbitID.MYQ6_IVAN_HIDEOUT_PROGRESS);
		canStartIvan = ivanProgress.eq(1);
		hasCraftedStakes = ivanProgress.eq(2);
		ivanProgressDone = ivanProgress.eq(3);

		var veliafProgress = new VarbitBuilder(VarbitID.MYQ6_VELIAF_HIDEOUT_PROGRESS);
		spokeWithVeliaf = veliafProgress.eq(1);
		spokeWithVanescula = veliafProgress.eq(2);
		veliafProgressDone = veliafProgress.eq(3);

		castleDrakan.setupRequirements();
	}

	void setupSteps()
	{
		setupStepsBeforeCastleDrakan();
		castleDrakan.setupSteps();
		setupStepsAfterCastleDrakan();
	}

	/**
	 * Everything after escaping Castle Drakan: the forest, curing Safalaan, the theatre, and the
	 * final confrontation with Drakan.
	 */
	private void setupStepsAfterCastleDrakan()
	{
		/// 82
		leaveCastleDrakan = new ObjectStep(this, ObjectID.CASTLE_DRAKAN_DOOR_TO_OUTSIDE_OPEN, new WorldPoint(2332, 7370, 0), "Leave Castle Drakan.");
		watchLeaveCastleDrakanCutscene = leaveCastleDrakan.cutscene();

		/// 84 + 86
		pickupCrankWheel = new DetailedQuestStep(this, new WorldPoint(2603, 7847, 0), "Pick up the crank wheel from the house east of the altar, north-west of where you started after the cutscene.", crankWheel);
		pickupCrankWheelFromWhereYouDied = new DetailedQuestStep(this, "Pick up the crank wheel from where you died!", crankWheel);
		pickupCrankWheelFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		pickupCrankWheel.addSubSteps(pickupCrankWheelFromWhereYouDied);
		crankWheel1 = new ObjectStep(this, ObjectID.SANGVESTI_FANCY_HOUSE_CRANK_BASE, new WorldPoint(2588, 7855, 0), "Spam click the crank base outside the house to the north west. Drakan will start chasing you. Run away if he gets too close.", crankWheel);
		enterHouseNextToCrankWheel = new ObjectStep(this, ObjectID.SANGVESTI_FANCY_HOUSE_3_DOOR, new WorldPoint(2587, 7857, 0), "Enter the house north of the crank base.");

		searchBookCase = new ObjectStep(this, ObjectID.SANGVESTI_BOOKSHELF_2, new WorldPoint(2585, 7861, 0), "Search the bookshelf in the house north of the crank base for a dusty book, then read it to retrieve the vitur key.");
		readDustyBook = new DetailedQuestStep(this, "Read the dusty book and retrieve the vitur key.", dustyBook.highlighted());
		pickupViturKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the vitur key from where you died!", viturKey);
		pickupViturKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		searchBookCase.addSubSteps(pickupViturKeyFromWhereYouDied);

		leaveBookcaseHouse = new ObjectStep(this, ObjectID.SANGVESTI_FANCY_HOUSE_3_DOOR, new WorldPoint(2587, 7857, 0), "Leave the bookcase house and enter the north-eastern vitur manor.", viturKeyForBoltCutterHouse);
		openViturDoorEastOfBookcaseHouse = new ObjectStep(this, ObjectID.SANGVESTI_VITUR_MANOR_DOOR, new WorldPoint(2612, 7853, 0), "Enter the north-eastern vitur manor.", viturKeyForBoltCutterHouse);

		pickupBoltCuttersFromWhereYouDied = new DetailedQuestStep(this, "Pick up the bolt cutters from where you died!", boltCutters);
		pickupBoltCuttersFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		searchShedBoltCutter = new ObjectStep(this, ObjectID.SANGVESTI_SHED, new WorldPoint(2616, 7855, 0), "Enter the north-eastern vitur manor and search the shed in the north-eastern room for a pair of bolt cutters.");
		searchShedBoltCutter.addSubSteps(leaveBookcaseHouse, openViturDoorEastOfBookcaseHouse, pickupBoltCuttersFromWhereYouDied);
		var boltCutterShedCombinationLock = new ChestCodeStep(this, "shed", "TOOLS", 10, 8, 2, 9, 1, 2);
		boltCutterShedCombinationLockPW = boltCutterShedCombinationLock.puzzleWrapStepWithDefaultText("Solve the shed puzzle.");

		leaveBoltCutterHouse = new ObjectStep(this, ObjectID.SANGVESTI_VITUR_MANOR_DOOR_M, new WorldPoint(2612, 7852, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", boltCutters, viturKeyForToothHalfOfKeyHouse);
		enterToothHalfOfKeyHouse = new ObjectStep(this, ObjectID.SANGVESTI_FOOD_SHOP_DOOR, new WorldPoint(2598, 7843, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", viturKeyForToothHalfOfKeyHouse);
		// TODO: I do not have a "pick up tooth half of key from where you died" step.
		pickupToothHalfOfKey = new DetailedQuestStep(this, new WorldPoint(2600, 7845, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", toothHalfOfKey);
		pickupToothHalfOfKey.addSubSteps(leaveBoltCutterHouse, enterToothHalfOfKeyHouse);

		openChainedDoor = new ObjectStep(this, ObjectID.SANGVESTI_CLOTHES_SHOP_DOOR, new WorldPoint(2590, 7818, 0), "Enter the house with the chained door, south of the well, and pick up the loop half of key from the table.", boltCutters, toothHalfOfKey);
		// TODO: I do not have a "pick up loop half of key from where you died" step.
		pickupLoopHalfOfKey = new DetailedQuestStep(this, new WorldPoint(2588, 7816, 0), "Enter the house with the chained door, south of the well, and pick up the loop half of key from the table.", loopHalfOfKey);
		pickupLoopHalfOfKey.addSubSteps(openChainedDoor);

		makeMyrmelKey = new DetailedQuestStep(this, "Combine the loop and tooth half of key into the myrmel key.", loopHalfOfKey.highlighted(), toothHalfOfKey.highlighted());

		enterBucketHouse = new ObjectStep(this, ObjectID.SANGVESTI_PUB_DOOR, new WorldPoint(2599, 7826, 0), "Pick up the bucket from the house with the myrmel door, east of the well.", myrmelKeyForBucketHouse);
		pickupBucket = new DetailedQuestStep(this, new WorldPoint(2596, 7833, 0), "Pick up the bucket from the house with the myrmel door, east of the well.", bucket);
		pickupBucket.addSubSteps(enterBucketHouse);

		useBucketOnWell = new ObjectStep(this, ObjectID.WELL_DRAKAN01_RUINED01, new WorldPoint(2592, 7825, 0), "Use the bucket on the well outside the house.", bucket.highlighted());
		useBucketOnWell.addIcon(ItemID.BUCKET_EMPTY);
		enterBucketHouseAgain = new ObjectStep(this, ObjectID.SANGVESTI_PUB_DOOR, new WorldPoint(2599, 7826, 0), "Return to the myrmel house with the bucket of water.", bucketOfWater);

		// NOTE: After doing this step, there is a tick where it goes back to being confused, because the dialog is slow
		// TODO: There's no "pick up shadum key from where you died" step.
		useBucketOfWaterOnNorthernBarrel = new ObjectStep(this, ObjectID.SANGVESTI_PUB_BARREL_3, new WorldPoint(2592, 7833, 0), "Use the bucket of water on the northern barrel to retrieve the shadum key.", bucketOfWater.highlighted());
		useBucketOfWaterOnNorthernBarrel.addIcon(ItemID.BUCKET_WATER);

		var pickupTrapdoorKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the trapdoor key from where you died!", trapdoorKey);
		pickupTrapdoorKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		enterShadumDoor = new ObjectStep(this, ObjectID.SANGVESTI_FANCY_HOUSE_1_DOOR, new WorldPoint(2614, 7814, 0), "Enter the shadum door south-west of the drawbridge and pick up the trapdoor key.", shadumKeyForTrapdoorHouse);
		pickupTrapdoorKey = new DetailedQuestStep(this, new WorldPoint(2619, 7812, 0), "Pick up the trapdoor key from the shadum house south-west of the drawbridge.", trapdoorKey);
		pickupTrapdoorKey.addSubSteps(enterShadumDoor, pickupTrapdoorKeyFromWhereYouDied);

		enterBank = new ObjectStep(this, ObjectID.SANGVESTI_BANK_DOOR, new WorldPoint(2580, 7835, 0), "Enter the bank north-west of the well.", myrmelKeyForBank, trapdoorKey, crankWheelForBank);

		operateBankCrank = new ObjectStep(this, ObjectID.SANGVESTI_BANK_CRANK_BASE, new WorldPoint(2582, 7834, 0), "Spam click the crank base in the bank.", trapdoorKey, crankWheelForBank);

		enterTrapdoor = new ObjectStep(this, ObjectID.SANGVESTI_TRAPDOOR, new WorldPoint(2579, 7830, 0), "Enter the trapdoor.", trapdoorKey);

		pickupJovkaiKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the jovkai key from where you died!", jovkaiKey);
		pickupJovkaiKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		searchAltarChest = new ObjectStep(this, ObjectID.SANGVESTI_CHAPEL_CHEST, new WorldPoint(2576, 7846, 0), "Search the chest in the western room of the altar house.");
		enterAltarHouseThroughDoor = new ObjectStep(this, ObjectID.SANGVESTI_CHAPEL_BACK_DOOR_2, new WorldPoint(2581, 7842, 0), "Search the chest in the western room of the altar house.");
		searchAltarChest.addSubSteps(enterAltarHouseThroughDoor, pickupJovkaiKeyFromWhereYouDied);

		var solveAltarChestLock = new ChestCodeStep(this, "altar chest", "35158", 10, 3, 5, 1, 5, 8);
		solveAltarChestLockPW = solveAltarChestLock.puzzleWrapStepWithDefaultText("Solve the chest puzzle.");

		leaveAltarThroughDoor = new ObjectStep(this, ObjectID.SANGVESTI_CHAPEL_BACK_DOOR_2, new WorldPoint(2581, 7842, 0), "Leave the altar room, then head to the smithy in the south-west.", jovkaiKey);
		enterSmith = new ObjectStep(this, ObjectID.SANGVESTI_BLACKSMITH_DOOR, new WorldPoint(2580, 7812, 0), "Enter the smithy in the south-west.", jovkaiKey);

		pickupOldCogFromWhereYouDied = new DetailedQuestStep(this, "Pick up the old cog from where you died!", oldCog);
		pickupOldCogFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		pickupOldCog = new DetailedQuestStep(this, new WorldPoint(2582, 7817, 0), "Pick up the old cog from the table in the smithy in the south-west.", oldCog);
		pickupOldCog.addSubSteps(enterSmith, pickupOldCogFromWhereYouDied);

		returnToVanescula = new NpcStep(this, NpcID.MYQ6_VANESCULA_VIS, new WorldPoint(2633, 7830, 0), "Return to Vanescula by the drawbridge to the south-east. Drakan will chase you when you leave the smithy.", oldCog);

		talkToIvanForSupplies = new NpcStep(this, NpcID.MYQ6_IVAN_DEPOSIT, new WorldPoint(2636, 7825, 0), "Return to Vanescula by the drawbridge and talk to Ivan Strom for some supplies. You can drop the bucket.");
		talkToIvanForSupplies.addDialogStep("Could I have those supplies you found?");

		returnToVanesculaReadyToLeave = new NpcStep(this, NpcID.MYQ6_VANESCULA_VIS, new WorldPoint(2633, 7830, 0), "Return to Vanescula by the drawbridge to the south-east when you're ready to leave. Drakan will chase you through the woods. Dodge his attacks, protect from melee.", oldCog);
		returnToVanesculaReadyToLeave.addSubSteps(talkToIvanForSupplies);

		/// 88
		watchCutsceneRepairedBridge = returnToVanesculaReadyToLeave.cutscene();

		/// 90
		fightDrakan1 = new NpcStep(this, NpcID.MYQ6_LOWERNIEL_COMBAT_1, new WorldPoint(2711, 7847, 0), "Learn to fight Lowerniel Drakan. Protect from melee and dodge his attacks.\n\nDuring this tutorial segment, there will be glimmering sparks that attempt to show you where to click to dodge his attacks.");
		fightDrakan1.setOverlayText("Learn to fight Lowerniel Drakan. Protect from melee and dodge his attacks. More info in sidebar.");

		/// 92
		flee1WatchTheCutscene = fightDrakan1.cutscene();
		resupplyIfNeeded = new ObjectStep(this, ObjectID.MYQ6_SOTFA_FOREST_RESUPPLY_SHELTER, new WorldPoint(2843, 7646, 0), "Resupply at the shelter remains in the middle. When you've resupplied, click the trees around you to continue.");
		fightDrakan1.addSubSteps(resupplyIfNeeded);

		/// 94
		resupplyIfNeeded2 = new ObjectStep(this, ObjectID.DARKWOOD_TREE_DOORWAY01, new WorldPoint(2951, 7821, 0), "Resupply at the shelter remains in the middle. When you've resupplied, click the trees to continue.");

		// TODO: instruct on their swapping
		sotfa1 = new NpcStep(this, new int[]{16229, 16230, 16231, 16232}, new WorldPoint(2961, 7851, 0), "Kill the ancient feral vyres. They swap positions and heal, so try to focus one at a time.", true);
		sotfa1Exit = new ObjectStep(this, ObjectID.DARKWOOD_TREE_DOORWAY01, new WorldPoint(2960, 7864, 0), "Enter Darkwood trees to continue.");

		var takeAxe = new ObjectStep(this, ObjectID.DARKWOOD_TREE_STUMP_AXE01, new WorldPoint(2960, 7890, 0), "Take the axe from the stump at the beginning.");

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

		var tsFinalStep = new ObjectStep(this, ObjectID.DARKWOOD_TREE_DOORWAY01, new WorldPoint(2993, 7924, 0), "Turn off run before continuing.");

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
		sotfa3AvoidAnimals = new NpcStep(this, NpcID.SOTFA_FOREST_MAXILLA_BEAST, "Turn off run. Avoid the Maxilla beasts. Running nearby, walking within 2 tiles, or standing on the maxilla beast will cause them to attack you and probably kill you.", true);
		sotfa3AvoidAnimals.addText("The first and third follow predetermined paths, while the second one moves randomly. Be extra careful when passing it.");
		sotfa3AvoidAnimals.addTileMarkers(SpriteID.LOAD, new WorldPoint(2914, 7920, 0), new WorldPoint(2925, 7907, 0), new WorldPoint(2900, 7893, 0), new WorldPoint(2913, 7897, 0));
		sotfa3Exit = new ObjectStep(this, ObjectID.SOTFA_CAVE_ENTRANCE_OP, new WorldPoint(2928, 7892, 0), "Leave through the cave in the south-east.");
		sotfa3Exit.setOverlayText("Turn off run. Avoid the Maxilla beasts. Running nearby, walking within 2 tiles, or standing on the maxilla beast will cause them to attack you and probably kill you.");
		sotfa3Exit.addText("The first and third follow predetermined paths, while the second one moves randomly. Be extra careful when passing it.");
		cSotfa3 = new ConditionalStep(this, sotfa3AvoidAnimals);
		cSotfa3.addStep(nearSotfa3Exit, sotfa3Exit);

		sotfa4 = new ObjectStep(this, ObjectID.SOTFA_CAVE_EXIT_OP, new WorldPoint(2993, 7866, 0), "Continue through the cave.");

		// nylocas room
		// TODO: Because it takes a little bit for the Nylocas to spawn, we immediately direct the user to the exit while they can't actually leave.
		// Is it possible to solve this? There's no varbit that denotes success
		sotfa5 = new NpcStep(this, NpcID.SOTFA_FOREST_NYLOCAS_FIGHTING_MELEE, "Kill the nylocas. Gray ones with melee, yellow ones with a ranged weapon. You can pick up the Spine near the Venator corpse to use as darts. If you do not kill them fast enough, they explode dealing 15 damage.", true);
		sotfa5.addAlternateNpcs(16237);
		sotfa5.addCustomIcon(new QuestStepIcon(ItemID.DRAGON_SCIMITAR, 16236, 0.75));
		sotfa5.addCustomIcon(new QuestStepIcon(ItemID.SOTFA_FOREST_TALON, 16237, 0.75));
		// TODO: For some reason, the npc condition failed to detect more npcs at some point. Not sure what happened, but a few ticks later the npcs were highlighted.
		sotfa5Exit = new ObjectStep(this, ObjectID.DARKWOOD_TREE_DOORWAY01, new WorldPoint(2912, 7852, 0), "When all nylocas are dead, leave through the trees.");
		sotfa5Exit.setOverlayText("Kill the nylocas. Gray ones with melee, yellow ones with a ranged weapon. You can pick up the Spine near the Venator corpse to use as darts. If you do not kill them fast enough, they explode dealing 15 damage. When all are dead, leave through the trees.");
		cSotfa5 = new ConditionalStep(this, sotfa5);
		cSotfa5.addStep(not(anyNearbyNylocas), sotfa5Exit);

		sotfa6WrangleSnakes = new NpcStep(this, NpcID.SOTFA_FOREST_BLOOD_SERPENT, "Wrangle the snakes, then combine them into one long snake. You must stand behind the snake when attempting to wrangle it.", true, deadSnake3);
		sotfa6CombineSnakes = new DetailedQuestStep(this, "Combine the snakes into one long snake.", deadSnake3.highlighted());
		sotfa6UseRopeOnBranch = new ObjectStep(this, ObjectID.SOTFA_FOREST_ROPESWING_TREE_BODY, new WorldPoint(3042, 7895, 0), "Use the serpent rope on the long branched tree.", serpentRope.highlighted());
		sotfa6UseRopeOnBranch.addIcon(ItemID.SOTFA_FOREST_ROPE);
		sotfa6SwingAcrossWater = new ObjectStep(this, ObjectID.SOTFA_FOREST_ROPESWING_TREE, new WorldPoint(3040, 7894, 0), "Swing across the water with your newly created swing.");
		sotfa6Exit = new ObjectStep(this, ObjectID.DARKWOOD_TREE_DOORWAY01, new WorldPoint(3041, 7885, 0), "Leave through the trees.");
		sotfaWatchTheCutscene = sotfa6Exit.cutscene();
		cSotfa6 = new ConditionalStep(this, sotfa6WrangleSnakes);
		cSotfa6.addStep(acrossSotfa6Pond, sotfa6Exit);
		cSotfa6.addStep(ropedTree, sotfa6SwingAcrossWater);
		cSotfa6.addStep(serpentRope, sotfa6UseRopeOnBranch);
		cSotfa6.addStep(deadSnake3, sotfa6CombineSnakes);

		/// 98
		talkToMysteriousWoman1 = new NpcStep(this, NpcID.MYQ6_EFARITAY_SUGADINTI_BRIDGE, new WorldPoint(2975, 7704, 0), "Talk to the Mysterious Woman.");
		mysteriousWomanWatchTheCutscene = talkToMysteriousWoman1.cutscene();

		/// 100
		startTalkingToEfaritay = new NpcStep(this, NpcID.MYQ6_EFARITAY_VIS, new WorldPoint(3157, 7839, 1), "Talk to Efaritay Hallow in the west room to help her with her son.");

		/// 102 + 104 + 106 + 108
		leavePalace1 = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_EXIT, new WorldPoint(3167, 7860, 1), "Pick some amitire leaves from the amitire plant outside the palace.");
		pickFromAmitirePlant = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_VINES, new WorldPoint(2982, 7679, 0), "Pick some amitire leaves from the amitire plant outside the palace.");
		pickFromAmitirePlant.addSubSteps(leavePalace1);

		enterPalace1 = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_ENTRY, new WorldPoint(2974, 7677, 0), "Enter the palace with the amitire leaves.", amitireLeaves);

		searchShelvesForBowl = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_SHELVES, new WorldPoint(3159, 7824, 1), "Search the shelves next to the sink for a bowl.", amitireLeaves);
		searchShelvesForBowl.addSubSteps(enterPalace1);

		fillBowlWithWater = new ObjectStep(this, ObjectID.MYQ6_KITCHEN01_SINK01, new WorldPoint(3157, 7825, 1), "Fill the bowl with water.", amitireLeaves, bowl.highlighted());

		getPotatoFromCupboard = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_CUPBOARD, new WorldPoint(3155, 7832, 1), "Take a potato from the cupboard.", amitireLeaves, bowlOfWater);
		getPotatoFromCupboard.addDialogStep("Potato.");

		combineStew = new DetailedQuestStep(this, "Put the potato in the bowl of water.", amitireLeaves, bowlOfWater.highlighted(), potato.highlighted());

		getRawMeatFromCupboard = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_CUPBOARD, new WorldPoint(3155, 7832, 1), "Take some raw meat the cupboard.", amitireLeaves, incompleteStew);
		getRawMeatFromCupboard.addDialogStep("Raw meat.");

		cookMeatOnRange = new ObjectStep(this, ObjectID.DARKM_POOR_RANGE, new WorldPoint(3160, 7826, 1), "Cook the raw meat on the range.", amitireLeaves, incompleteStew, rawMeat.highlighted());
		cookMeatOnRange.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Cooked meat"));

		combineStew2 = new DetailedQuestStep(this, "Put the cooked meat in the incomplete stew.", amitireLeaves, incompleteStew.highlighted(), cookedMeat.highlighted());
		cookStew = new ObjectStep(this, ObjectID.DARKM_POOR_RANGE, new WorldPoint(3160, 7826, 1), "Cook the stew on the range.", uncookedStew.highlighted());
		cookStew.addWidgetHighlight(WidgetHighlight.createMultiskillByName("Stew"));
		combineStew3 = new DetailedQuestStep(this, "Put the amitire leaves into the stew.", amitireLeaves.highlighted(), stew.highlighted());
		giveStewToSafalaan = new NpcStep(this, NpcID.MYQ6_SAFALAAN_VIS_NOOP, "Give the amitire stew to Safalaan Hallow.", amitireStew.highlighted());
		giveStewToSafalaan.addIcon(ItemID.MYQ6_STEW);

		talkToSafalaanAfterFeedingHimStew = new NpcStep(this, NpcID.MYQ6_SAFALAAN_VIS, new WorldPoint(3157, 7841, 1), "Talk to Safalaan Hallow after feeding him the stew.");

		talkToEfaritayAfterFeedingStewToSafalaan = new NpcStep(this, NpcID.MYQ6_EFARITAY_VIS, new WorldPoint(3157, 7839, 1), "Talk to Efaritay Hallow after feeding Safalaan the stew.");

		/// 110
		searchWorkbenchForHammer = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_WORKBENCH, new WorldPoint(3176, 7841, 1), "Search the workbench in the east room for a hammer.", blisterwoodFlail);
		searchWorkbenchForHammer.addDialogStep("Hammer.");
		searchWorkbenchForChisel = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_WORKBENCH, new WorldPoint(3176, 7841, 1), "Search the workbench in the east room for a chisel.", blisterwoodFlail, hammer);
		searchWorkbenchForChisel.addDialogStep("Chisel.");
		searchWorkbenchForKnife = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_WORKBENCH, new WorldPoint(3176, 7841, 1), "Search the workbench in the east room for a knife.", blisterwoodFlail, hammer, chisel);
		searchWorkbenchForKnife.addDialogStep("Knife.");
		searchCrateForBlisterwoodLogs = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_BLISTERWOOD_CRATE, new WorldPoint(3177, 7843, 1), "Search the northern-most crate for some blisterwood logs.", blisterwoodFlail, hammer, chisel, knife);
		searchCrateForBlisterwoodLogs.addWidgetHighlight(InterfaceID.Skillmulti.A);
		searchCrateForBlessedSilverSickle = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_SICKLE_CRATE, new WorldPoint(3181, 7841, 1), "Search the eastern-most crate for a blessed silver sickle.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs);
		searchChestForDiamond = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_DIAMOND_CHEST, new WorldPoint(3181, 7835, 1), "Search the eastern-most chest for a diamond.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, blessedSilverSickle);
		putDiamondInSickle = new DetailedQuestStep(this, "Put the unenchanted diamond on the blessed silver sickle.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, blessedSilverSickle.highlighted(), diamond.highlighted());
		putDiamondInSickle.addDialogStep("Yes.");
		searchChestForDiamondTablet2 = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_TABLET_CHEST, new WorldPoint(3175, 7836, 1), "Search the western-most chest for a diamond tablet.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, diamondSickleB);
		useEnchantDiamondTabletOnSickle = new DetailedQuestStep(this, "Enchant the diamond sickle with the enchant diamond tablet.", blisterwoodFlail, hammer, chisel, knife, blisterwoodLogs, diamondSickleB.highlighted(), diamondTablet.highlighted());
		useEnchantDiamondTabletOnSickle.addDialogStep("Yes.");
		createEnhancedBlisterwoodSickle = new DetailedQuestStep(this, "Combine the blisterwood logs with your newly created enchanted diamond sickle to create an enhanced blisterwood sickle.", blisterwoodFlail, hammer, knife, blisterwoodLogs.highlighted(), enchantedDiamondSickle.highlighted());
		createEnhancedBlisterwoodSickle.addDialogStep("Yes.");
		createHallowedFlail = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(3179, 7842, 1), "Smith a hallowed flail at the anvil.", blisterwoodFlailUnequipped, hammer, enhancedBlisterwoodSickle, hallowedMarks);
		createHallowedFlail.addDialogStep("Yes.");

		// 110 -> 112 after creating the hallowed flail
		// [2026-07-19T12:18:53Z 317] varbit MYQ6_ARANEI_WATCHER_CHAT (15477) 0 -> 1
		// after speaking to the random Aranei outside the palace
		speakToIvanWithHallowedFlail = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3175, 7834, 1), "Talk to Ivan Strom after smithing the hallowed flail.");

		ivanSearchWorkbenchForKnife = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_WORKBENCH, new WorldPoint(3176, 7841, 1), "Search the workbench for a knife.");
		ivanSearchWorkbenchForKnife.addDialogStep("Knife.");
		getLogsForStakes = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_BLISTERWOOD_CRATE, new WorldPoint(3177, 7843, 1), "Search the northern-most crate for some blisterwood logs to craft stakes.");
		getLogsForStakes.addWidgetHighlight(InterfaceID.Skillmulti.A);
		fletchStakes = new DetailedQuestStep(this, "Fletch the logs into stakes.", blisterwoodLogs.highlighted(), knife.highlighted());

		returnToIvan = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3175, 7834, 1), "Return to  Ivan Strom after crafting some stakes. You can craft more before continuing if you want to use them during the upcoming fights.");

		climbDownstairs = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_STAIRS_DOWN, new WorldPoint(3171, 7823, 1), "Talk to Veliaf in the dungeon downstairs.");
		talkToVeliafInDungeon = new NpcStep(this, NpcID.MYQ6_VELIAF_VIS, new WorldPoint(3168, 7842, 0), "Talk to Veliaf in the dungeon.");
		talkToVeliafInDungeon.addSubSteps(climbDownstairs);

		talkToVanescula = new NpcStep(this, NpcID.MYQ6_VANESCULA_HIDEOUT_TRAPPED_VIS, new WorldPoint(3168, 7844, 0), "Talk to Vanescula Drakan.");

		talkToVeliafInDungeonAgain = new NpcStep(this, NpcID.MYQ6_VELIAF_VIS, new WorldPoint(3168, 7842, 0), "Talk to Veliaf in the dungeon again.");

		climbUpstairs = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_STAIRS_M, new WorldPoint(3164, 7823, 0), "Climb upstairs.");
		talkToSugadintiAfterHelpingAllies = new NpcStep(this, NpcID.MYQ6_SUGADINTI_VIS, new WorldPoint(3168, 7828, 1), "Talk to Sugadinti Vitur after helping out Ivan and Veliaf.");
		talkToSugadintiAfterHelpingAllies.addSubSteps(climbUpstairs);

		/// 114
		leavePalaceForCombat = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_EXIT, new WorldPoint(3167, 7860, 1), "Get ready for combat. Resupply at the supply table in the kitchen if needed. Speak to the Aranei next to the table if you want to deposit any items into your bank. Then leave the palace.");
		getReadyForCombatWatchTheCutscene = leavePalaceForCombat.cutscene();

		/// 116
		attackPortals = new NpcStep(this, NpcID.MYQ6_BRIDGE_DEFENCE_PORTAL, "Attack the portals in the sky.", true);

		/// 118
		leaveDoors = new ObjectStep(this, ObjectID.MYQ6_SUGADINTI_HIDEOUT_EXIT, new WorldPoint(3167, 7860, 1), "Leave the palace.");
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
		passThroughBarricadeToHelp = new ObjectStep(this, ObjectID.MYQ6_ENTRY_HALL_BARRICADE_OP, new WorldPoint(3167, 7844, 1), "Pass through barricade to return to the guarding step.");
		guardThePalace.addSubSteps(passThroughBarricadeToHelp);

		/// 122
		passThroughBarricadeToFightDrakan = new ObjectStep(this, ObjectID.MYQ6_ENTRY_HALL_BARRICADE_OP, new WorldPoint(3167, 7844, 1), "Pass through the barricade, ready to fight Lowerniel Drakan. You can resupply at the supply table in the kitchen.");
		fightDrakan2 = new NpcStep(this, NpcID.MYQ6_LOWERNIEL_COMBAT_2, new WorldPoint(3168, 7853, 1), "Fight Lowerniel Drakan. Protect from melee and dodge his attacks.\n\nDodge at least one tile opposite of the telegraphed motion.\n\nWhen he holds his spear back, step to his side.");
		fightDrakan2.setOverlayText("Fight Lowerniel Drakan. Protect from melee and dodge his attacks. More info in sidebar.");
		fightDrakan2.addSubSteps(passThroughBarricadeToFightDrakan);

		/// 124
		finishedDrakan2Cutscene = new DetailedQuestStep(this, "Watch the cutscene.");

		/// 126
		talkToIvanInHauntedWoods = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3606, 3416, 0), "Talk to Ivan Strom in the haunted woods.");
		leavingPalaceCutscene = talkToIvanInHauntedWoods.cutscene();

		/// 128
		goDownToTalkToVeliafAfterLeavingPalace = new ObjectStep(this, ObjectID.BURGH_INN_TRAPDOOR_MULTILOC, new WorldPoint(3490, 3232, 0), "Enter the Burgh de Rott Myreque base.");

		talkToVeliafAfterLeavingPalace = new NpcStep(this, NpcID.MYQ6_VELIAF_VIS, new WorldPoint(3493, 9628, 0), "Talk to Veliaf Hurtz in the Burgh de Rott Myreque base.");
		talkToVeliafAfterLeavingPalaceCutscene = talkToVeliafAfterLeavingPalace.cutscene();
		talkToVeliafAfterLeavingPalace.addSubSteps(goDownToTalkToVeliafAfterLeavingPalace);

		/// 130
		talkToSugadintiInBurghDeRott = new NpcStep(this, NpcID.MYQ6_SUGADINTI_VIS, new WorldPoint(3494, 9627, 0), "Talk to Sugadinti Vitur in Burgh de Rott.");

		/// 132 + 134 + 136 + 138 (theatre of blood)
		getToTob = new NpcStep(this, NpcID.MYQ6_SUGADINTI_HUMAN_VIS, new WorldPoint(3668, 3220, 0), "Gear up for theatre of blood. Bring melee gear with a range and mage switch, some food & potions, then talk to Sugadinti Vitur in front of the theatre of blood.", combatGear, food, prayerPotions);
		getToTob.addDialogStep("I'm ready.");
		getToTobCutscene = getToTob.cutscene();

		// TODO: This text could probably be improved
		ensureNothingBothersSugadinti = new DetailedQuestStep(this, "Kill Nylocas with the correct fighting style. Fight the Maiden of Sugadinti, killing healers when they spawn. Avoid blood puddles on the ground.");

		talkToSugadintiAfterFinishingTob = new NpcStep(this, NpcID.MYQ6_SUGADINTI_HUMAN_VIS, new WorldPoint(3667, 3218, 0), "Talk to Sugadinti Vitur.");
		talkToSugadintiAfterFinishingTobCutscene = talkToSugadintiAfterFinishingTob.cutscene();

		/// 140
		headToBarrowsL = new NpcStep(this, NpcID.MYQ6_VELIAF_VIS, new WorldPoint(3540, 3256, 0), "Head to the abandoned building north-east of Burgh de Rott and talk to Veliaf.", combatGear, hallowedFlail, food, prayerPotions);

		/// 142
		headDownToVanescula = new ObjectStep(this, ObjectID.MYQ6_OLD_LAB_ENTRY, new WorldPoint(3543, 3257, 0), "Head to the abandoned building north-east of Burgh de Rott and enter the entry.");
		headDownToVanesculaCutscene = headDownToVanescula.cutscene();

		/// 144
		fightTheWyrd = new NpcStep(this, NpcID.SAFALAAN_WYRD, "Fight the Wyrd. Protect from melee against the normal attacks, and turn it off when it screeches similar to a Venator.\n\nFirst phase:\nHe will punch twice, then unleash a screech attack. After screeching twice, he will smash the ground matching which hand he hit you with the two most recent punches.\n\nSecond phase:\nInstead of the screech attach, he will use a lightning attack that spawns blood pools around you. These pools disappear over time. Turn off your protection prayer and leave his area, avoiding any pools.");
		fightTheWyrd.setOverlayText("Fight the Wyrd. Protect from melee against the normal attacks, and turn it off when it screeches similar to a Venator. More info in sidebar.");

		/// 146
		dealtWithWyrdCutscene = fightTheWyrd.cutscene();

		/// 148
		findWyrd = new ObjectStep(this, ObjectID.MYQ6_BROKEN_FENCE_M, new WorldPoint(3562, 3323, 0), "Search for the escaped wyrd. Inspect the fence north of barrows.");
		findWyrdCutscene = findWyrd.cutscene();

		/// 150
		speakWithVeliafAfterInspectingFence = new NpcStep(this, NpcID.MYQ6_VELIAF_INJURED_VIS, new WorldPoint(3493, 9628, 0), "Talk to Veliaf Hurtz in the Burgh de Rott hideout.");
		talkWithVeliafInBurghDeRottCutscene = speakWithVeliafAfterInspectingFence.cutscene();

		/// 152
		prepareFightDrakan3 = new NpcStep(this, NpcID.MYQ6_SUGADINTI_VIS, new WorldPoint(3494, 9627, 0), "Talk to Sugadinti Vitur in the Burgh de Rott hideout, ready for another fight against Drakan.");
		prepareFightDrakan3.addDialogStep("I'm ready.");
		enterBurghDeRottDungeon = new ObjectStep(this, ObjectID.BURGH_INN_TRAPDOOR_MULTILOC, new WorldPoint(3490, 3232, 0), "Talk to Sugadinti Vitur in the Burgh de Rott hideout, ready for another fight against Drakan.");
		prepareFightDrakan3.addSubSteps(enterBurghDeRottDungeon);

		/// 154
		talkToVeliafToReturnToDrakan3 = new NpcStep(this, NpcID.MYQ6_VELIAF_INJURED_VIS, new WorldPoint(3493, 9628, 0), "Talk to Veliaf Hurtz.");
		talkToVeliafToReturnToDrakan3.addDialogStep("I'll go now.");
		fightDrakan3 = new NpcStep(this, NpcID.MYQ6_LOWERNIEL_COMBAT_3, "Fight Lowerniel Drakan. Protect from melee and dodge his attacks.\n\nDodge at least one tile opposite of the telegraphed motion.\n\nWhen he holds his spear back, step to his side.");
		fightDrakan3.setOverlayText("Fight Lowerniel Drakan. More info in sidebar.");
		fightDrakan3.addSubSteps(talkToVeliafToReturnToDrakan3);

		/// 156
		talkToVeliaf = new NpcStep(this, NpcID.MYQ6_VELIAF_INJURED_VIS, new WorldPoint(3493, 9628, 0), "Talk to Veliaf Hurtz.");
		talkToVeliaf.addDialogStep("I'll go now.");
		talkToVeliafCutscene = talkToVeliaf.cutscene();

		/// 158
		talkToIvanInsideCastleDrakan = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(2845, 7387, 0), "Talk to Ivan Strom inside Castle Drakan.");

		/// 160 + 162
		talkToSugadintiInsideCastleDrakan = new NpcStep(this, NpcID.MYQ6_SUGADINTI_VIS, new WorldPoint(2846, 7389, 0), "Talk to Sugadinti Vitur inside Castle Drakan.");

		/// 164
		talkToEfaritayOnIcyene = new NpcStep(this, NpcID.MYQ6_EFARITAY_WEAPON_VIS, new WorldPoint(3701, 3186, 0), "Talk to Efaritay Hallow in Icyene Graveyard.");

		/// 166
		enterVampyriumForTheLastTime = new ObjectStep(this, ObjectID.MYQ6_VAMPYRIUM_GRAVEYARD_PORTAL, new WorldPoint(3703, 3185, 0), "Enter the portal in Icyene Graveyard to get to Vampyrium, ready for the final confrontation.", hallowedFlail, combatGearMelee);
		enterVampyriumForTheLastTimeCutscene = enterVampyriumForTheLastTime.cutscene();

		/// 168
		goToFightDrakan4 = new ObjectStep(this, ObjectID.CASTLE_DRAKAN_ROOF_DOOR, new WorldPoint(2506, 7387, 0), "Enter the door, ready to fight Drakan. You can get supplies and deposit items at the chest next to the door.", hallowedFlail, combatGearMelee);
		goToFightDrakan4Cutscene = goToFightDrakan4.cutscene();

		/// 170 + 172
		// TODO: some minor instructions + maybe a wiki link?
		fightDrakan4 = new NpcStep(this, NpcID.MYQ6_LOWERNIEL_FINAL_COMBAT, "Fight Lowerniel Drakan. Protect from melee and dodge his attacks.\n\nDodge at least one tile opposite of the telegraphed motion.\n\nWhen he holds his spear back, step to his side.\n\nDodge over the blood waves.\n\nProtect from magic when he shoots his blood spears.\n\nAvoid shadows and blood pools on the ground.\n\nStep behind him when he raises his spear above his shoulder.\n\nWhen he rushes around you, dodge in the direction of his first cloud.");
		fightDrakan4.setOverlayText("Fight Lowerniel Drakan. More info in sidebar.");
		fightDrakan4Cutscene = fightDrakan4.cutscene();

		/// 174
		talkToEfaritayAfterKillingDrakan = new NpcStep(this, NpcID.MYQ6_EFARITAY_VIS, new WorldPoint(3702, 3182, 0), "Talk to Efaritay Hallow in the icyene graveyard.");
		finalQuestCutscene = talkToEfaritayAfterKillingDrakan.cutscene();

		/// 176 + 178
		youHaveFinishedTheQuest = new DetailedQuestStep(this, "Congratulations on finishing the quest!");

	}

	/**
	 * Everything up to entering Vampyrium: finding Ivan and Veliaf, Paterdomus, and Ivandis' tomb.
	 */
	private void setupStepsBeforeCastleDrakan()
	{
		/// 0 + 2
		startQuest = new NpcStep(this, NpcID.MYQ6_SARIUS_GUILE_VIS, new WorldPoint(3697, 3184, 0), "Talk to Sarius Guile in the Icyene Graveyard to start the quest.");
		startQuest.addDialogStep("Icyene Graveyard.");
		startQuest.addDialogStep("Yes.");

		/// 4
		var talkToIvan = new NpcStep(this, 1 /* TODO */, new WorldPoint(3599, 9612, 0), "");
		goDownToIvan = new ObjectStep(this, ObjectID.MYQ4_HIDEOUT_TRAPDOOR_OPEN, new WorldPoint(3605, 3215, 0), "", blisterwoodFlail);
		goDownToIvan.addAlternateObjects(ObjectID.MYQ4_HIDEOUT_TRAPDOOR);
		cLookForIvan = new ConditionalStep(this, goDownToIvan, "Look for Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");
		cLookForIvan.addStep(inMyrequeHideoutOldManRal, talkToIvan);

		/// 6 + 8
		inspectShrine = new ObjectStep(this, ObjectID.MYQ6_HIDEOUT_SHRINE_VIS, new WorldPoint(3601, 9614, 0), "Inspect the makeshift shrine in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");

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
		talkToIvanAfterEscaping = new NpcStep(this, NpcID.MYQ5_IVAN_CHILD_BLISTERWOOD_ALT, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch after escaping.");

		/// 16
		talkToIvanOutsideSlepeChurch = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3727, 3310, 0), "Talk to Ivan Strom in the graveyard outside the Slepe church.");

		/// 18
		askRoyAboutVeliaf = new NpcStep(this, NpcID.SLEPE_BARTENDER_ROY, new WorldPoint(3750, 3296, 0), "Talk to Roy the bartender in Slepe and ask him about Veliaf's whereabouts.");
		askRoyAboutVeliaf.addDialogStep("We're looking for a friend of ours.");

		/// 20
		lookIntoCommotion = new ObjectStep(this, ObjectID.SLP_CHURCH_CRYPT_SOUTH_LADDER_DOWN, new WorldPoint(3727, 3301, 0), "", blisterwoodFlail, combatGear, food, prayerPotions);
		climbUpToCrombwickManor = new ObjectStep(this, ObjectID.SLP_BASEMENT_MANOR_EXIT, new WorldPoint(3726, 9756, 1), "", blisterwoodFlail, combatGear, food, prayerPotions);
		cLookIntoCommotionAtCrombwickManor = new ConditionalStep(this, talkToIvanOutsideSlepeChurch, "Head to the Crombwick Manor through the church dungeon.");
		cLookIntoCommotionAtCrombwickManor.addStep(and(followedByIvan, inSlepeChurchDungeon), climbUpToCrombwickManor);
		cLookIntoCommotionAtCrombwickManor.addStep(followedByIvan, lookIntoCommotion);

		/// 22
		// TODO: I'm missing one NPC ID here. Please get all npc ids!!
		killVampyresWithVeliaf = new NpcStep(this, new int[]{16127, 16128, 16129, 16125}, new WorldPoint(3725, 3357, 0), "Help Veliaf kill the vampyres in Crombwick Manor.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampyresWithVeliaf.setAllowMultipleHighlights(true);

		/// 24
		talkToVeliafInCrombwickManor = new NpcStep(this, NpcID.MYQ6_VELIAF_MANOR_COMBAT_DONE, new WorldPoint(3731, 3359, 0), "Talk to Veliaf after helping him kill the vampyres in Crombwick Manor.");

		/// 26
		var enterPaterdomus = new ObjectStep(this, ObjectID.PIPEASTSIDETRAPDOOR_OPEN, new WorldPoint(3422, 3485, 0), "Talk with Ivan Strom in the Paterdomus dungeon.");
		talkToIvanPaterdomus1 = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3441, 9897, 0), "Talk with Ivan in the Paterdomus dungeon.");
		talkToIvanPaterdomus1.addSubSteps(enterPaterdomus);
		enterPaterdomus.addAlternateObjects(ObjectID.PIPEASTSIDETRAPDOOR);
		cHeadToPaterdomus = new ConditionalStep(this, enterPaterdomus);
		cHeadToPaterdomus.addStep(inPaterdomusTempleDungeon, talkToIvanPaterdomus1);

		/// 28
		readSquiresJournal = new DetailedQuestStep(this, "Read the Squire's journal Ivan just gave you.", squiresJournal.highlighted());

		/// 30
		talkToIvanPaterdomus2 = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3441, 9897, 0), "Talk with Ivan again after reading the Squire's journal.");
		cTalkToIvanAfterReadingTheBook = new ConditionalStep(this, enterPaterdomus);
		cTalkToIvanAfterReadingTheBook.addStep(inPaterdomusTempleDungeon, talkToIvanPaterdomus2);

		/// 32 + 34
		climbUpFromPaterdomusTempleDungeon = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3405, 9907, 0), "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		headToPaterdomusTempleF0 = new ObjectStep(this, ObjectID.PRIESTPERILTEMPLEDOORL, new WorldPoint(3408, 3489, 0), "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		killMonksOfZamorak = new NpcStep(this, new int[]{16155, 16156, 16154, 16156}, "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		killMonksOfZamorak.setAllowMultipleHighlights(true);
		killMonksOfZamorak.addSubSteps(climbUpFromPaterdomusTempleDungeon, headToPaterdomusTempleF0);

		/// 36
		climbUpToPaterdomusTempleF1 = new ObjectStep(this, ObjectID.PATERDOMUS_SPIRALSTAIRS, new WorldPoint(3417, 3492, 0), "Talk to Ivan Storm on the first floor of the Paterdomus temple.");
		talkToIvanInPaterdomusTempleF1 = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3417, 3487, 1), "Talk to Ivan Storm on the first floor of the Paterdomus temple.");
		talkToIvanInPaterdomusTempleF1.addSubSteps(climbUpToPaterdomusTempleF1);

		/// 38
		var plinth1Pos = new WorldPoint(3409, 3483, 1);
		var plinth2Pos = new WorldPoint(3408, 3485, 1);
		var plinth3Pos = new WorldPoint(3409, 3487, 1);
		var plinth4Pos = new WorldPoint(3409, 3490, 1);
		var plinth5Pos = new WorldPoint(3408, 3492, 1);
		var plinth6Pos = new WorldPoint(3409, 3494, 1);

		var essiandarsNotesPlinthID = ObjectID.MYQ6_PLINTH_BOOK_ESSIANDAR;
		var essiandarsNotesName = "Essiandar's notes";

		var sarlsJournalPlinthID = ObjectID.MYQ6_PLINTH_BOOK_SARL;
		var sarlsJournalName = "Sarl's journal";

		var fromMisthalinToMorytaniaPlinthID = ObjectID.MYQ6_PLINTH_BOOK_IRIANDUL;
		var fromMisthalinToMorytaniaName = "From Misthalin to Morytania";

		var scruffyNotebookPlinthID = ObjectID.MYQ6_PLINTH_BOOK_DERYGULL;
		var scruffyNotebookName = "scruffy notebook";

		var theLifeOfFriarPlinthID = ObjectID.MYQ6_PLINTH_BOOK_FRIAR;
		var theLifeOfFriarName = "The Life of Friar";

		var piousProceedingsPlinthID = ObjectID.MYQ6_PLINTH_BOOK_ERYSAIL;
		var piousProceedingsName = "Pious proceedings";

		var plinth1VB = new VarbitBuilder(VarbitID.MYQ6_PLINTH_CONTENTS_ESSIANDAR);
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

		var plinth2VB = new VarbitBuilder(VarbitID.MYQ6_PLINTH_CONTENTS_SARL);
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

		var plinth3VB = new VarbitBuilder(VarbitID.MYQ6_PLINTH_CONTENTS_IRIANDUL);
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

		var plinth4VB = new VarbitBuilder(VarbitID.MYQ6_PLINTH_CONTENTS_DERYGULL);
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

		var plinth5VB = new VarbitBuilder(VarbitID.MYQ6_PLINTH_CONTENTS_FRIAR);
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

		var plinth6VB = new VarbitBuilder(VarbitID.MYQ6_PLINTH_CONTENTS_ERYSAIL);
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

		var climbUpTempleForWritings = new ObjectStep(this, ObjectID.PATERDOMUS_SPIRALSTAIRS, new WorldPoint(3417, 3492, 0), "Climb up the temple and solve the puzzle.");
		var cFindTheWritings = new ConditionalStep(this, climbUpTempleForWritings, "Find the writings on the first floor of the Paterdomus temple for Ivan Strom.");

		var getSarlsJournalAndTheLifeOfFriar = new ObjectStep(this, ObjectID.MYQ6_BOOKCASE_PATERDOMUS_3, new WorldPoint(3414, 3491, 1), "Get Sarl's journal and The Life of Friar from the bookcase.", sarlsJournal, theLifeOfFriar);
		getSarlsJournalAndTheLifeOfFriar.addDialogStep("Both.");
		getSarlsJournalAndTheLifeOfFriar.addDialogStep("Yes.");

		var getPiousProceedings = new ObjectStep(this, ObjectID.MYQ6_BOOKCASE_PATERDOMUS_4, new WorldPoint(3411, 3491, 1), "Get Pious proceedings from the bookcase.", piousProceedings);
		getPiousProceedings.addDialogStep("Yes.");

		var getFromMisthalinToMorytania = new ObjectStep(this, ObjectID.MYQ6_BOOKCASE_PATERDOMUS_1, new WorldPoint(3411, 3492, 1), "Get Misthalin to Morytania from the bookcase.", fromMisthalinToMorytania);
		getFromMisthalinToMorytania.addDialogStep("Yes.");

		var getScruffyNotebookAndEssiandarsNotes = new ObjectStep(this, ObjectID.MYQ6_BOOKCASE_PATERDOMUS_5, new WorldPoint(3409, 3488, 1), "Get the scruffy notebook and Essiandar's notes from the bookcase.");
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
		talkToIvanAfterFindingTheWritings = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3417, 3487, 1), "Talk to Ivan Strom after solving the puzzle.");

		/// 42
		readIvandisWritings = new DetailedQuestStep(this, "Read Ivandis' writings.", ivandisWritings.highlighted());

		/// 44
		talkToIvanAfterReadingIvandisWritings = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3417, 3487, 1), "Talk to Ivan Strom after reading Ivandis' writings.");

		/// 46
		talkToIvanInPaterdomus = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.", blisterwoodFlail, combatGear, prayerPotions);

		/// 48
		talkToIvanInPaterdomus2 = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.", blisterwoodFlail, combatGear, prayerPotions);
		talkToIvanInPaterdomus2.addDialogStep("Lead the way.");
		talkToIvanInPaterdomus.addSubSteps(talkToIvanInPaterdomus2);

		/// 50
		investigateHole = new ObjectStep(this, ObjectID.MYQ6_IVANDIS_TOMB_TUNNEL_BLOCKED_INVESTIGATE, new WorldPoint(3505, 9857, 0), "Investigate the blockage just south west of Ivandis' coffin.");

		/// 52
		getPickaxe = new NpcStep(this, NpcID.MYQ6_IVAN_VIS, new WorldPoint(3505, 9861, 0), "Ask Ivan Strom for a pickaxe.", anyPickaxe);
		mineHole = new ObjectStep(this, ObjectID.MYQ6_IVANDIS_TOMB_TUNNEL_BLOCKED_MINE, new WorldPoint(3505, 9857, 0), "Mine the blockage just south west of Ivandis' coffin.", anyPickaxe);
		mineHole.addSubSteps(getPickaxe);

		/// 54
		headThroughHole = new ObjectStep(this, ObjectID.MYQ6_IVANDIS_TOMB_TUNNEL_UNBLOCKED, new WorldPoint(3505, 9857, 0), "Head through the cave entrance just south west of Ivandis' coffin, ready for a fight.", blisterwoodFlail, combatGear, food, prayerPotions);

		/// 56
		enterDaeyaltProcessingRoom = new ObjectStep(this, ObjectID.MYQ6_MINE_CART_TUNNEL, new WorldPoint(3117, 7472, 2), "Head into the daeyalt processing room through the tunnel to the north-east.", blisterwoodFlail, combatGear, food, prayerPotions);

		/// 58
		killVampsInDaeyaltRoom = new NpcStep(this, new int[]{16125, 16126, 16137, 16136, 16137}, "Kill vampyres. Focus on the Vyrewatch Sentinels. Avoid the Blood orb. Lure Vyrewatches into the Blood orbs to deal massive damage to them.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampsInDaeyaltRoom.setAllowMultipleHighlights(true);

		/// 60
		// TODO: who do we actually talk to here. is this a "free the slave" step instead?
		talkToIvanAfterKillingVamps = new NpcStep(this, NpcID.MYQ6_IVAN_COMBAT_DONE, new WorldPoint(3178, 7459, 0), "Talk to Ivan after killing the vampyres.");

		/// 62
		enterCastleDrakanCellar = new ObjectStep(this, ObjectID.MYQ6_VAMPYRIFICATION_LAB_CASTLE_EXIT_OP, new WorldPoint(3182, 7470, 0), "Enter the Castle Drakan cellars through the entry to the north.");

		/// 64
		climbUpToCastleDrakanLobby = new ObjectStep(this, ObjectID.MYQ6_CASTLE_DRAKAN_DUNGEON_STAIRS_SOUTH, new WorldPoint(3147, 7578, 0), "Climb up the stairs to the Castle Drakan lobby.");

		/// 66 + 68
		// TODO(FOR FUTURE ADVENTURERS): Do you _need_ to bring the medallion for this?
		prayAtShrine = new ObjectStep(this, ObjectID.MYQ6_CASTLE_DRAKAN_SHRINE_OP, new WorldPoint(3168, 7707, 0), "Pray at the shrine to let your Drakan's Medallion teleport you here.", drakansMedallion);

		enterPortalInCastleDrakanLobby = new ObjectStep(this, ObjectID.MYQ6_VAMPYRIUM_PORTAL_VIS, new WorldPoint(3161, 7710, 0), "Enter the ominous red portal in the Castle Drakan lobby.", blisterwoodFlail, combatGearMelee);
		enterPortalInCastleDrakanLobby.addDialogStep("Yes.");
		enterPortalInCastleDrakanLobby.addTeleport(drakansMedallionToCastleDrakan);

		/// 70
		youAreInVampyrium = enterPortalInCastleDrakanLobby.cutscene();

	}

	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();
		mapStepsBeforeCastleDrakan(steps);
		castleDrakan.mapSteps(steps);
		mapStepsAfterCastleDrakan(steps);
		return steps;
	}

	/// Quest states 0-70: finding Ivan and Veliaf, Paterdomus, and Ivandis' tomb.
	private void mapStepsBeforeCastleDrakan(Map<Integer, QuestStep> steps)
	{
		steps.put(0, startQuest);
		steps.put(2, startQuest);

		steps.put(4, cLookForIvan);

		var cInspectShrine = new ConditionalStep(this, goDownToIvan);
		cInspectShrine.addStep(inMyrequeHideoutOldManRal, inspectShrine);
		steps.put(6, cInspectShrine);
		steps.put(8, cInspectShrine);

		var cTalkToIVanGoingToDarkmeyer = new ConditionalStep(this, cLookForIvan);
		cTalkToIVanGoingToDarkmeyer.addStep(inCutscene, watchCutsceneGoingToDarkmeyer);
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

		// TODO: Probably better handled by making cLookIntoCommotionAtCrombwickManor3 into the 'talk to veliaf' sidestep text
		var climbUpToCrombwickManor2 = climbUpToCrombwickManor.copy();
		climbUpToCrombwickManor2.setText("Climb up the stairs to the north.");
		var lookIntoCommotion2 = lookIntoCommotion.copy();
		lookIntoCommotion2.setText("Enter Crombwick Manor through the church dungeon.");
		talkToVeliafInCrombwickManor.addSubSteps(climbUpToCrombwickManor2, lookIntoCommotion2);

		var cLookIntoCommotionAtCrombwickManor3 = new ConditionalStep(this, lookIntoCommotion2);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inCrombwickManor), talkToVeliafInCrombwickManor);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor2);
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

		var goDownToIvan = new ObjectStep(this, ObjectID.PIPEASTSIDETRAPDOOR_OPEN, new WorldPoint(3422, 3485, 0), "Stock up on combat gear and supplies, then return to Ivan Strom in Paterdomus.");
		goDownToIvan.addAlternateObjects(ObjectID.PIPEASTSIDETRAPDOOR);
		talkToIvanInPaterdomus.addSubSteps(goDownToIvan);

		var cTalkToIvanInPaterdomus = new ConditionalStep(this, goDownToIvan);
		cTalkToIvanInPaterdomus.addStep(inPaterdomusTempleDungeon, talkToIvanInPaterdomus);
		steps.put(46, cTalkToIvanInPaterdomus);

		var cTalkToIvanInPaterdomus2 = new ConditionalStep(this, goDownToIvan);
		cTalkToIvanInPaterdomus2.addStep(inPaterdomusTempleDungeon, talkToIvanInPaterdomus2);
		steps.put(48, cTalkToIvanInPaterdomus2);

		var inCanifisTunnels = new ZoneRequirement(new Zone(new WorldPoint(3450, 9792, 0), new WorldPoint(3504, 9847, 2)));
		var goDownCanifisTrapdoor = new ObjectStep(this, ObjectID.THRT_TAVERN_TRAP_DOOR, new WorldPoint(3495, 3464, 0), "Enter the trapdoor behind Canifis's pub.", blisterwoodFlail, combatGear);
		var enterIvandisTomb = new ObjectStep(this, ObjectID.BURGH_IVANDIS_TOMB_ENTRANCE, new WorldPoint(3484, 9832, 0), "Enter Ivandis' Tomb.");

		var cGetToIvandisTomb = new ConditionalStep(this, goDownCanifisTrapdoor);
		cGetToIvandisTomb.addStep(inCanifisTunnels, enterIvandisTomb);
		investigateHole.addSubSteps(cGetToIvandisTomb);

		var cInvestigateHole = new ConditionalStep(this, cGetToIvandisTomb);
		cInvestigateHole.addStep(inIvandisTomb, investigateHole);
		steps.put(50, cInvestigateHole);

		// 15486 = has talked about pickaxe
		// 15469 = has received pickaxe
		var cMineHole = new ConditionalStep(this, cGetToIvandisTomb);
		cMineHole.addStep(and(inIvandisTomb, anyPickaxe), mineHole);
		cMineHole.addStep(and(inIvandisTomb, canReceivePickaxeFromIvan), getPickaxe);
		cMineHole.addStep(and(inIvandisTomb), mineHole);
		steps.put(52, cMineHole);

		var cHeadThroughHole = new ConditionalStep(this, cGetToIvandisTomb);
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
	}

	/// Quest states 72-80: one conditional over the eight Castle Drakan goals.
	private void mapStepsAfterCastleDrakan(Map<Integer, QuestStep> steps)
	{
		// 80 -> 82 after watching the cutscene with Drakan where you attempt to leave through the portal
		var cLeaveCastleDrakan = new ConditionalStep(this, leaveCastleDrakan);
		cLeaveCastleDrakan.addStep(inCutscene, watchLeaveCastleDrakanCutscene);
		steps.put(82, cLeaveCastleDrakan);

		// 82 -> 84 after leaving Vampyrium's castle drakan

		var returnToVampyrium = enterPortalInCastleDrakanLobby.copy();
		leaveCastleDrakan.addSubSteps(returnToVampyrium);
		var cCog = new ConditionalStep(this, returnToVampyrium, "Find the old cog for the drawbridge.");
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
		cCog.addStep(and(not(oldCog), inAltarHouse, or(jovkaiKey, unlockedSmith), not(unlockedAltarHouse)), leaveAltarThroughDoor);
		cCog.addStep(and(not(oldCog), or(jovkaiKey, unlockedSmith)), enterSmith);

		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, inAltarHouse, combinationLockWidgetOpen), solveAltarChestLockPW);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, inAltarHouse, not(jovkaiKey)), searchAltarChest);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, not(jovkaiKey), unlockedAltarHouse), enterAltarHouseThroughDoor);
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

		var cRepairedBridge = new ConditionalStep(this, returnToVampyrium, "You have successfully repaired the bridge!");
		cRepairedBridge.addStep(inCutscene, watchCutsceneRepairedBridge);
		cRepairedBridge.addStep(inVampyriumVarbit, returnToVanesculaReadyToLeave);
		steps.put(88, cRepairedBridge);

		steps.put(90, fightDrakan1);

		var cFlee1 = new ConditionalStep(this, resupplyIfNeeded);
		cFlee1.addStep(inCutscene, flee1WatchTheCutscene);
		steps.put(92, cFlee1);

		var returnToVampyriumFlee2 = returnToVampyrium.copy();
		resupplyIfNeeded2.addSubSteps(returnToVampyriumFlee2);

		var cFlee2 = new ConditionalStep(this, returnToVampyriumFlee2);
		cFlee2.addStep(and(inSotfa6), cSotfa6);
		cFlee2.addStep(and(inSotfa5), cSotfa5);
		cFlee2.addStep(and(inSotfa4), sotfa4);
		cFlee2.addStep(and(inSotfa3), cSotfa3);
		cFlee2.addStep(and(inSotfa2), sotfa2);
		cFlee2.addStep(and(inSotfa1, anyNearbyFeralVyres), sotfa1);
		cFlee2.addStep(and(inSotfa1), sotfa1Exit);
		cFlee2.addStep(inCutscene, sotfaWatchTheCutscene);
		cFlee2.addStep(inVampyriumVarbit, resupplyIfNeeded2);
		steps.put(94, cFlee2);

		// 94 -> 96: done with all the "running through forest" puzzles

		var cFlee3 = new ConditionalStep(this, returnToVampyriumFlee2);
		cFlee3.addStep(inResupplyZone, resupplyIfNeeded2);
		cFlee3.addStep(inVampyriumVarbit, sotfaWatchTheCutscene);
		steps.put(96, cFlee3);

		var cMysteriousWoman = new ConditionalStep(this, returnToVampyriumFlee2);
		cMysteriousWoman.addStep(inCutscene, mysteriousWomanWatchTheCutscene);
		cMysteriousWoman.addStep(inVampyriumVarbit, talkToMysteriousWoman1);
		steps.put(98, cMysteriousWoman);

		// 98 -> 100: after meeting & finishing speaking with Sugadinti

		steps.put(100, startTalkingToEfaritay);

		// 100 -> 102: spoke with Efaritay, and she told me to bring her herbs

		// Progress has this happen third, but technically can be done at any time
		var veliafSteps = new ConditionalStep(this, talkToVeliafInDungeon);
		veliafSteps.addStep(veliafProgressDone, climbUpstairs);
		veliafSteps.addStep(spokeWithVanescula, talkToVeliafInDungeonAgain);
		veliafSteps.addStep(spokeWithVeliaf, talkToVanescula);

		var returnToVampyrium3 = returnToVampyrium.copy();
		startTalkingToEfaritay.addSubSteps(returnToVampyrium3);

		var cPickUpHerbs = new ConditionalStep(this, returnToVampyrium3);
		cPickUpHerbs.addStep(inPalaceDungeon, veliafSteps);
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

		var cUpgradeWeapon = new ConditionalStep(this, returnToVampyrium3);
		cUpgradeWeapon.addStep(inPalaceDungeon, veliafSteps);
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
		cUpgradeWeapon.addStep(inVampyriumVarbit, talkToEfaritayAfterFeedingStewToSafalaan);
		steps.put(110, cUpgradeWeapon);

		// veliafSteps can happen whenever, so long as you're downstairs
		var cAfterUpgradingWeapon = new ConditionalStep(this, returnToVampyrium3);
		cAfterUpgradingWeapon.addStep(and(veliafProgressDone, ivanProgressDone), talkToSugadintiAfterHelpingAllies);
		cAfterUpgradingWeapon.addStep(inPalaceDungeon, veliafSteps);
		cAfterUpgradingWeapon.addStep(ivanProgressDone, climbDownstairs);
		cAfterUpgradingWeapon.addStep(hasCraftedStakes, returnToIvan);
		cAfterUpgradingWeapon.addStep(and(canStartIvan, knife, blisterwoodLogs), fletchStakes);
		cAfterUpgradingWeapon.addStep(and(canStartIvan, knife), getLogsForStakes);
		cAfterUpgradingWeapon.addStep(canStartIvan, ivanSearchWorkbenchForKnife);
		cAfterUpgradingWeapon.addStep(inVampyriumVarbit, speakToIvanWithHallowedFlail);
		steps.put(112, cAfterUpgradingWeapon);

		// 112 -> 114 after speaking to sugadinti and helping ivan & veliaf.

		var returnToVampyrium4 = returnToVampyrium.copy();
		leavePalaceForCombat.addSubSteps(returnToVampyrium4);

		var cGetReadyForCombat = new ConditionalStep(this, leavePalaceForCombat);
		cGetReadyForCombat.addStep(inCutscene, getReadyForCombatWatchTheCutscene);
		cGetReadyForCombat.addStep(inVampyriumVarbit, leavePalaceForCombat);
		steps.put(114, cGetReadyForCombat);

		var cAttackPortals = new ConditionalStep(this, returnToVampyrium4);
		cAttackPortals.addStep(inVampyriumVarbit, attackPortals);
		steps.put(116, cAttackPortals);

		// 116 -> 118 after helping
		var cAfterHelpingWithPortals = new ConditionalStep(this, returnToVampyrium4);
		cAfterHelpingWithPortals.addStep(inCutscene, leaveDoorsCutscene);
		cAfterHelpingWithPortals.addStep(inVampyriumVarbit, leaveDoors);
		steps.put(118, cAfterHelpingWithPortals);

		var cFinishedPortalsCutscene = new ConditionalStep(this, returnToVampyrium4, "Help Efaritay Hallow and the Aranei defend the palace.");
		cFinishedPortalsCutscene.addStep(inCutscene, barricadeCutscene);
		cFinishedPortalsCutscene.addStep(inPalaceSouthernPart, passThroughBarricadeToHelp);
		cFinishedPortalsCutscene.addStep(inVampyriumVarbit, guardThePalace);
		steps.put(120, cFinishedPortalsCutscene);

		// 120 -> 122 after drakan breaks in
		// NOTE: For this step, and many others, if IN_VAMPYRIUM is 0 we need to guide the user back to vampyrium
		// Pulling back spear = Stand to his left or right
		var cDrakanFight2 = new ConditionalStep(this, returnToVampyrium4);
		cDrakanFight2.addStep(inPalaceSouthernPart, passThroughBarricadeToFightDrakan);
		cDrakanFight2.addStep(inVampyriumVarbit, fightDrakan2);
		steps.put(122, cDrakanFight2);

		// 122 -> 124: fought off drakan
		var cFinishedDrakan2 = new ConditionalStep(this, returnToVampyrium4);
		cFinishedDrakan2.addStep(inCutscene, finishedDrakan2Cutscene);
		cFinishedDrakan2.addStep(inPalaceSouthernPart, passThroughBarricadeToFightDrakan);
		steps.put(124, cFinishedDrakan2);

		// 124 -> 126: watched cutscene in palace
		var cFinishedDrakan3 = new ConditionalStep(this, talkToIvanInHauntedWoods);
		cFinishedDrakan3.addStep(inCutscene, leavingPalaceCutscene);
		steps.put(126, cFinishedDrakan3);

		// 126 -> 128: talked to Ivan after getting teleported
		var cFinishedDrakan4 = new ConditionalStep(this, goDownToTalkToVeliafAfterLeavingPalace);
		cFinishedDrakan4.addStep(inCutscene, talkToVeliafAfterLeavingPalaceCutscene);
		cFinishedDrakan4.addStep(inBurghDeRottDungeon, talkToVeliafAfterLeavingPalace);
		steps.put(128, cFinishedDrakan4);

		// 128 -> 130: talked to Veliaf in Burgh de Rott
		var cFinishedDrakan5 = new ConditionalStep(this, goDownToTalkToVeliafAfterLeavingPalace);
		cFinishedDrakan5.addStep(inBurghDeRottDungeon, talkToSugadintiInBurghDeRott);
		steps.put(130, cFinishedDrakan5);

		var cGetToTob = new ConditionalStep(this, getToTob);
		cGetToTob.addStep(inCutscene, getToTobCutscene);
		steps.put(132, cGetToTob);
		steps.put(134, cGetToTob);

		// 134 -> 136: entering tob with sugadinti
		var cEnsureNothingBothersSugadinti = new ConditionalStep(this, getToTob);
		cEnsureNothingBothersSugadinti.addStep(inTob, ensureNothingBothersSugadinti);
		steps.put(136, cEnsureNothingBothersSugadinti);

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
		var enterBurghBasement2 = goDownToTalkToVeliafAfterLeavingPalace.copy();
		speakWithVeliafAfterInspectingFence.addSubSteps(enterBurghBasement2);
		var cSpeakWithVeliafAfterInspectingFence = new ConditionalStep(this, enterBurghBasement2);
		cSpeakWithVeliafAfterInspectingFence.addStep(inBurghDeRottDungeon, speakWithVeliafAfterInspectingFence);
		steps.put(150, cSpeakWithVeliafAfterInspectingFence);

		// 150 -> 152: spoke with veliaf and co under Burgh de Rott after inspecting fence
		var cDoSomething = new ConditionalStep(this, enterBurghDeRottDungeon, combatGear, hallowedFlail, food, prayerPotions);
		cDoSomething.addStep(inBurghDeRottDungeon, prepareFightDrakan3);
		cDoSomething.addStep(inCutscene, talkWithVeliafInBurghDeRottCutscene);
		steps.put(152, cDoSomething);

		// 152 -> 154: ?
		var cDoSomething2 = new ConditionalStep(this, enterBurghDeRottDungeon, combatGear, hallowedFlail, food, prayerPotions);
		cDoSomething2.addStep(inBurghDeRottDungeon, talkToVeliafToReturnToDrakan3);
		cDoSomething2.addStep(inCutscene, talkWithVeliafInBurghDeRottCutscene);
		cDoSomething2.addStep(and(new InInstanceRequirement(), new ZoneRequirement(new Zone(15858))), fightDrakan3);
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
	}

	private ObjectStep bookTake(WorldPoint plinthPosition, String bookName, int plinthIDWithBookOnIt)
	{
		var step = new ObjectStep(this, plinthIDWithBookOnIt, plinthPosition, "Take " + bookName + " off the plinth.");
		step.addDialogStep("Take " + bookName + ".");

		return step;
	}

	private ObjectStep bookPut(WorldPoint plinthPosition, String bookName, ItemRequirement requirement)
	{
		var step = new ObjectStep(this, ObjectID.MYQ6_PLINTH_EMPTY, plinthPosition, "Place " + bookName + " on the plinth.", requirement);
		step.addIcon(requirement.getId());
		return step;
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
			"Vyrewatch (lvl 103)",
			"Vyrewatch Sentinel (lvl 151)",
			"Monk of Zamorak (lvl 34)",
			"Sanguidae (lvl 106)",
			"Venator (lvl 195)",
			"Ancient feral vyre (lvl 89)",
			"Nylocas (lvl 46, 82, 104)",
			"The Maiden of Sugadinti (lvl 340)",
			"Nylocas Vasilias (lvl 323)",
			"Wyrd (lvl 564)",
			"Lowerniel Drakan (lvl 927, 1063)"
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
		/// What every section inside Vampyrium's Castle Drakan asks the player to bring.
		var castleDrakanKit = List.<Requirement>of(blisterwoodFlail, combatGear, drakansMedallion);

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

		sections.addAll(castleDrakan.getPanels());

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
		), castleDrakanKit));

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
		), castleDrakanKit));

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
		), castleDrakanKit));

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
		), castleDrakanKit));

		sections.add(new PanelDetails("Guarding the palace", List.of(
			leavePalaceForCombat,
			attackPortals,
			leaveDoors,
			guardThePalace,
			fightDrakan2,
			finishedDrakan2Cutscene
		), List.of(
			hallowedFlail,
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

		return sections;
	}
}
