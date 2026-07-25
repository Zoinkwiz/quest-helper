// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver5;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver4;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver1;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver6;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver3;
import com.questhelper.helpers.quests.thebloodmoonrises.treesolvers.TreeSolver2;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.steps.WidgetStep;
import com.questhelper.util.QuestStepIcon;
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

	// Miscellaneous requirements
	VarplayerRequirement followedByIvan;
	VarbitRequirement canReceivePickaxeFromIvan;
	VarbitRequirement needTeleportUnlock;
	VarbitRequirement onF0;
	VarbitRequirement onF1;
	VarbitRequirement onF2;

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

	VarbitRequirement needToStartThroneRoomPuzzle;
	VarbitRequirement needToPullBusts;
	VarbitRequirement needToGetKey;
	VarbitRequirement doneWithThroneRoomPuzzle;
	VarbitRequirement needToPullBust3;
	VarbitRequirement needToPullBust4;
	VarbitRequirement needToPullBust1;

	VarbitRequirement placedEmblemInVanesculasHallway;
	VarbitRequirement placedEmblemInVanesculasStudy;

	// Steps
	// TODO: Remove
	DetailedQuestStep todo;
	DetailedQuestStep todo1;
	DetailedQuestStep todo2;
	DetailedQuestStep todo3;
	DetailedQuestStep todo4;
	DetailedQuestStep todoVampyriumPuzzle;

	/// 0 + 2
	NpcStep startQuest;

	/// 4
	ObjectStep goDownToIvan;
	ConditionalStep cLookForIvan;

	/// 6 + 8
	ObjectStep inspectShrine;

	/// 10
	NpcStep talkToIvanGoingToDarkmeyer;

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
	// Half moon key
	ConditionalStep cGetHalfMoonKey;
	PuzzleWrapperStep cGetHalfMoonKeyPW;
	ObjectStep getKeyFromThroneRoom;

	// aaaaaaa
	DetailedQuestStep pickupCrankWheel;
	DetailedQuestStep pickupCrankWheelFromWhereYouDied;


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
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		blisterwoodFlail = new ItemRequirement("Blisterwood flail", ItemID.BLISTERWOOD_FLAIL);
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
		followedByIvan = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);
		canReceivePickaxeFromIvan = new VarbitRequirement(15469, 0);
		needTeleportUnlock = new VarbitRequirement(15470, 0);

		// Vampyrium Castle Drakan requirements
		var castleDrakanFloor = new VarbitBuilder(15489);
		var castleDrakanRoom = new VarbitBuilder(15499);

		onF0 = castleDrakanFloor.eq(1);
		onF1 = castleDrakanFloor.eq(2);
		onF2 = castleDrakanFloor.eq(3);

		// unspecific
		var inLobby = castleDrakanRoom.eq(1);

		// GROUND FLOOR
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

		// FIRST FLOOR
		inVanesculasStudy = castleDrakanRoom.eq(23);
		inVanesculasChamber = castleDrakanRoom.eq(24);
		inDrakanEmblemRoomSouthOfExplosiveRoom =  castleDrakanRoom.eq(25);
		inExplosiveRoom = castleDrakanRoom.eq(28);
		inHallwayEastOfExplosiveRoom =  castleDrakanRoom.eq(31);
		inLobbyF1 = and(onF1, inLobby);
		inHallwayNorthOfLobby = castleDrakanRoom.eq(34);
		inVanesculasHallway = castleDrakanRoom.eq(35);
		inRanisHallway = castleDrakanRoom.eq(36);
		inRanisParlour = castleDrakanRoom.eq(26);
		inVenatorRoom = castleDrakanRoom.eq(37);

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
	}

	void setupSteps()
	{
		// TODO: Remove
		todo = new DetailedQuestStep(this, "todo");
		todo1 = new DetailedQuestStep(this, "todo1");
		todo2 = new DetailedQuestStep(this, "todo2");
		todo3 = new DetailedQuestStep(this, "todo3");
		todo4 = new DetailedQuestStep(this, "todo4");
		todoVampyriumPuzzle = new DetailedQuestStep(this, "todo do some vampyrium puzzle");

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

		/// 12
		// Return-step in case the user died / left / teleported out
		talkToIvanToReturnToCastleDrakan = new NpcStep(this, NpcID.MYQ5_IVAN_CHILD_BLISTERWOOD_TRADE, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch to return to the Castle Drakan courtyard.", blisterwoodFlail, combatGear, food, prayerPotions);
		talkToIvanToReturnToCastleDrakan.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanToReturnToCastleDrakan.addDialogStep("I'm ready.");
		talkToIvanGoingToDarkmeyer.addSubSteps(talkToIvanToReturnToCastleDrakan);

		defendIvanFromVyres = new DetailedQuestStep(this, "Kill the vyrewatches and defend Ivan Strom until he can teleport you both out. Kill the approaching acidic bloodvelds with a ranged weapon. Prioritize the Vyrewatch Sentinels first.", blisterwoodFlail, combatGear, food, prayerPotions);

		/// 14
		talkToIvanAfterEscaping = new NpcStep(this, 15835, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch after escaping.");
		talkToIvanAfterEscaping.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanAfterEscaping.addDialogStep("I'm ready.");

		/// 16
		talkToIvanOutsideSlepeChurch = new NpcStep(this, 15855, new WorldPoint(3727, 3310, 0), "Talk to Ivan Strom in the graveyard outside the Slepe church.");

		/// 18
		askRoyAboutVeliaf = new NpcStep(this, NpcID.SLEPE_BARTENDER_ROY, "Talk to Roy the bartender in Slepe and ask him about Veliaf's whereabouts.");
		askRoyAboutVeliaf.addDialogStep("We're looking for a friend of ours.");

		/// 20
		lookIntoCommotion = new ObjectStep(this, ObjectID.SLP_CHURCH_CRYPT_SOUTH_LADDER_DOWN, new WorldPoint(3727, 3301, 0), "Head to the Crombwick Manor through the church dungeon.", blisterwoodFlail, combatGear, food, prayerPotions);
		climbUpToCrombwickManor = new ObjectStep(this, ObjectID.SLP_BASEMENT_MANOR_EXIT, new WorldPoint(3726, 9756, 1), "Head to the Crombwick Manor through the church dungeon.", blisterwoodFlail, combatGear, food, prayerPotions);
		cLookIntoCommotionAtCrombwickManor = new ConditionalStep(this, talkToIvanOutsideSlepeChurch);
		cLookIntoCommotionAtCrombwickManor.addStep(and(followedByIvan, inSlepeChurchDungeon), climbUpToCrombwickManor);
		cLookIntoCommotionAtCrombwickManor.addStep(followedByIvan, lookIntoCommotion);

		/// 22
		killVampyresWithVeliaf = new NpcStep(this, new int[]{16127, 16128, 16129, 16125}, new WorldPoint(3725, 3357, 0), "Help Veliaf kill the vampyres in Crombwick Manor.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampyresWithVeliaf.setAllowMultipleHighlights(true);

		/// 24
		// TODO: Confirm npc ID, although you can technically speak to Ivan too
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
		talkToIvanInPaterdomus = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.");

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
		enterCastleDrakanCellar = new ObjectStep(this, 61205, new WorldPoint(3182, 7470, 0), "Enter the Castle Drakan cellars through the entry to the east (TODO CHECK CARDINAL DIRECTION).");

		/// 64
		climbUpToCastleDrakanLobby = new ObjectStep(this, 61207, new WorldPoint(3147, 7578, 0), "Climb up the stairs to the Castle Drakan lobby.");

		/// 66 + 68
		// TODO(FOR FUTURE ADVENTURERS): Do you _need_ to bring the medallion for this?
		prayAtShrine = new ObjectStep(this, 61226, new WorldPoint(3168, 7707, 0), "Pray at the shrine to let your Drakan's Medallion teleport you here.", drakansMedallion);

		enterPortalInCastleDrakanLobby = new ObjectStep(this, 61216, new WorldPoint(3161, 7710, 0), "Click the ominous red portal in the Castle Drakan lobby.", blisterwoodFlail, combatGearMelee);
		enterPortalInCastleDrakanLobby.addDialogStep("Yes.");
		enterPortalInCastleDrakanLobby.addTeleport(drakansMedallionToCastleDrakan);

		/// 70
		youAreInVampyrium = new DetailedQuestStep(this, "Watch the cutscene.");
		enterPortalInCastleDrakanLobby.addSubSteps(youAreInVampyrium);

		var enterThroneRoomFromDiningRoom = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 0), "Enter the throne room.");
		var enterThroneRoomFromStudy = new ObjectStep(this, 61576, new WorldPoint(2358, 7380, 0), "Enter the throne room.");

		var cHmkEnterThroneRoom = new ConditionalStep(this, enterThroneRoomFromDiningRoom);
		cHmkEnterThroneRoom.addStep(inDiningRoom, enterThroneRoomFromDiningRoom);
		cHmkEnterThroneRoom.addStep(inStudy, enterThroneRoomFromStudy);

		var investigateThrone = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Investigate the throne again.");
		var investigateThroneAgain = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Investigate the throne again.");

		var pullBust1 = new ObjectStep(this, 61645, new WorldPoint(2317, 7393, 0), "Pull the northern-most bust.");
		var pullBust2 = new ObjectStep(this, 61648, new WorldPoint(2317, 7392, 0), "Pull the second northern-most bust.");
		var pullBust3 = new ObjectStep(this, 61651, new WorldPoint(2317, 7391, 0), "Pull the second southern-most bust.");
		var pullBust4 = new ObjectStep(this, 61654, new WorldPoint(2317, 7390, 0), "Pull the southern-most bust.");

		var pullBusts = new ConditionalStep(this, pullBust2);
		pullBusts.addStep(needToPullBust3, pullBust3);
		pullBusts.addStep(needToPullBust4, pullBust4);
		pullBusts.addStep(needToPullBust1, pullBust1);

		getKeyFromThroneRoom = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Head back to the throne room and search the throne for a half moon key.");

		cGetHalfMoonKey = new ConditionalStep(this, cHmkEnterThroneRoom, "\nFind the half moon key.");
		cGetHalfMoonKey.addStep(and(inThroneRoom, needToStartThroneRoomPuzzle), investigateThrone);
		cGetHalfMoonKey.addStep(and(inThroneRoom, needToPullBusts), pullBusts);
		cGetHalfMoonKey.addStep(and(inThroneRoom, needToGetKey), investigateThroneAgain);
		cGetHalfMoonKey.addSubSteps(getKeyFromThroneRoom);
		cGetHalfMoonKeyPW = cGetHalfMoonKey.puzzleWrapStep("Find the half moon key.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		// TODO: Remove
		for (var i = 0; i < 2000; ++i)
		{
			steps.put(i, todo);
		}

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

		// TODO: 48 = get there yourself or have him lead the way? What if a user selects "i will take my own route!!!"?
		var cTalkToIvanInPaterdomus2 = new ConditionalStep(this, talkToIvanInPaterdomus2);
		steps.put(48, cTalkToIvanInPaterdomus2);

		// TODO: on step 50: do you have to finish talking with Veliaf until you say "let's have a look around and see what we can find"? this sets varbit 15487 from 0 to 1
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
		// [2026-07-01T11:54:31Z 2958] varbit CUTSCENE_STATUS (542) 1 -> 0
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_LAST_ROOM (15499) 0 -> 2
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_LAST_FLOOR (15549) 0 -> 1
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_WORLD_MAP_FLOOR (15489) 0 -> 1
		// [2026-07-01T11:54:31Z 2958] varbit CASTLE_DRAKAN_LAST_FACING (15500) 0 -> 4
		// [2026-07-01T11:54:32Z 2960] varbit IN_CASTLE_DRAKAN_INSTANCE (15498) 0 -> 1
		// [2026-07-01T11:54:32Z 2960] varbit IN_VAMPYRIUM (15482) 0 -> 1
		// [2026-07-01T11:56:27Z 3151] varp CASTLE_DRAKAN_ROOM_STATUS_1 (5632) 5 -> 6
		// [2026-07-01T11:56:27Z 3151] varbit CASTLE_DRAKAN_WORLDMAP_NOTIFICATION (15566) 0 -> 1

		// TODO: Instruct the user to pick up supplies as they walk along

		var vanesculasChamberWallDestroyed = new VarbitRequirement(15511, 1);

		var inVampyriumVarbit = new VarbitRequirement(15482, 1);


		// This could _technically_ be a conditional step guiding the user from _any_ room back to the throne room,
		// but they should only ever get to this step if they've manually destroyed the key. Their punishment
		// is that they need to read the text to get back to the throne room.

		var cVampyriumCastleDrakan = new ConditionalStep(this, enterPortalInCastleDrakanLobby, "Solve the puzzles inside Vampyrium's Castle Drakan. Supplies are littered around the castle.");
		// TODO: Can I add a note on the sidebar or something, saying: DO NOT DROP AN ITEM UNLESS INSTRUCTED. EVERYTHING YOU ARE TOLD TO GET IS IMPORTANT!!!

		var searchShelvesForSmallClockHand = new ObjectStep(this, 61752, new WorldPoint(2323, 7387, 0), "Search the shelves for a small clock hand in the room south of the throne room.");
		var enterRoomSouthOfThroneRoom = new ObjectStep(this, 61587, new WorldPoint(2310, 7386, 0), "Enter the room south of the throne room.");

		var getSmallClockHand = new ConditionalStep(this, searchShelvesForSmallClockHand);
		getSmallClockHand.addStep(and(inVampyriumVarbit, inThroneRoom), enterRoomSouthOfThroneRoom);

		var enterThroneRoomFromRoomSouthOfThroneRoom = new ObjectStep(this, 61587, new WorldPoint(2327, 7391, 0), "Enter the throne room.");
		var enterDiningRoomFromThroneRoom = new ObjectStep(this, 61572, new WorldPoint(2304, 7391, 0), "Enter the dining room.");
		var enterHallwayWestOfDiningRoom = new ObjectStep(this, 61586, new WorldPoint(2336, 7370, 0), "Enter the hallway west of the dining room.");
		var enterWestRoomFromHallwayWestOfDiningRoom = new ObjectStep(this, 61572, new WorldPoint(2323, 7395, 0), "Enter the door to the west of the hallway, avoiding the traps on the floor.");
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

		var pickUpTinderbox = new ObjectStep(this, 61691, new WorldPoint(2344, 7387, 0), "Search the sparkling chest for a tinderbox.");

		var goUpFromStorageRoom = new ObjectStep(this, 61602, new WorldPoint(2340, 7384, 0), "Climb up the stairs.", tinderbox);

		var pickUpExplosiveBarrel = new ItemStep(this, new WorldPoint(2439, 7388, 0), "Pick up the explosive barrel", explosiveBarrel, tinderbox);

		var enterSouthDoorFromExplosiveRoom = new ObjectStep(this, 61573, new WorldPoint(2439, 7384, 0), "Enter the room south of where you picked up the explosive barrel.", explosiveBarrel, tinderbox);

		var searchCrateForDrakanEmblem1 = new ObjectStep(this, 61751, new WorldPoint(2454, 7378, 0), "Search the crate for a drakan emblem");

		var exitEmblemRoom1 = new ObjectStep(this, 61572, new WorldPoint(2455, 7381, 0), "Return to the explosive room.", explosiveBarrel, tinderbox, drakanEmblem1);
		var exitEastExplosiveRoom = new ObjectStep(this, 61572, new WorldPoint(2444, 7386, 0), "Exit the explosive room through the east door.", explosiveBarrel, tinderbox, drakanEmblem1);
		var enterNorthDoorInHallwayNextToExplosiveRoom = new ObjectStep(this, 61572, new WorldPoint(2474, 7398, 0), "Exit the explosive room through the east door, avoiding traps on the floor.", explosiveBarrel, tinderbox, drakanEmblem1);
		enterNorthDoorInHallwayNextToExplosiveRoom.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2471, 7397, 0),
			new WorldPoint(2472, 7397, 0),
			new WorldPoint(2471, 7396, 0),
			new WorldPoint(2472, 7396, 0)
		);
		var enterIntoHallwayNorthOfLobbyF1 = new ObjectStep(this, 61572, new WorldPoint(2314, 7382, 1), "Enter the room to the north.", explosiveBarrel, tinderbox, drakanEmblem1);
		var climbUpToLobbyF1 = new ObjectStep(this, 61599, new WorldPoint(2318, 7371, 0), "Climb up the stairs.");
		enterIntoHallwayNorthOfLobbyF1.addSubSteps(climbUpToLobbyF1);

		var enterUnmarkedNorthDoor = new ObjectStep(this, 61576, new WorldPoint(2466, 7422, 0), "Enter the northern unmarked door.", explosiveBarrel, tinderbox, drakanEmblem1);

		var placeEmblem1OnReceptacle = new ObjectStep(this, 61638, new WorldPoint(2469, 7408, 0), "Place the drakan emblem on the empty receptacle next to the southern door down the hall, avoiding traps on the way.", explosiveBarrel, tinderbox, anyOneEmblemHighlighted);
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

		var enterVanesculasStudy = new ObjectStep(this, 61576, new WorldPoint(2468, 7407, 0), "Enter Vanescula's study.", explosiveBarrel, tinderbox);

		var searchCrateInVanesculasStudyForLargeClockHand = new ObjectStep(this, 61751, new WorldPoint(2466, 7370, 0), "Search the crate in the north-west corner of Vanescula's study for a large clock hand.", explosiveBarrel, tinderbox);

		var getLargeClockHand = new ConditionalStep(this, todo1);
		getLargeClockHand.addStep(inVanesculasStudy, searchCrateInVanesculasStudyForLargeClockHand);
		getLargeClockHand.addStep(and(inVanesculasHallway, placedEmblemInVanesculasHallway), enterVanesculasStudy);
		getLargeClockHand.addStep(inVanesculasHallway, placeEmblem1OnReceptacle);
		getLargeClockHand.addStep(inHallwayNorthOfLobby, enterUnmarkedNorthDoor);
		getLargeClockHand.addStep(inLobbyF1, enterIntoHallwayNorthOfLobbyF1);
		getLargeClockHand.addStep(inLobbyF0, climbUpToLobbyF1);
		getLargeClockHand.addStep(inHallwayEastOfExplosiveRoom, enterNorthDoorInHallwayNextToExplosiveRoom);
		getLargeClockHand.addStep(and(inExplosiveRoom, explosiveBarrel, drakanEmblem1), exitEastExplosiveRoom);
		getLargeClockHand.addStep(and(inDrakanEmblemRoomSouthOfExplosiveRoom, drakanEmblem1), exitEmblemRoom1);
		getLargeClockHand.addStep(inDrakanEmblemRoomSouthOfExplosiveRoom, searchCrateForDrakanEmblem1);
		getLargeClockHand.addStep(and(inExplosiveRoom, explosiveBarrel), enterSouthDoorFromExplosiveRoom);
		getLargeClockHand.addStep(inExplosiveRoom, pickUpExplosiveBarrel);
		getLargeClockHand.addStep(and(tinderbox, inStorageRoom), goUpFromStorageRoom);
		getLargeClockHand.addStep(inStorageRoom, pickUpTinderbox);
		getLargeClockHand.addStep(inHallwayWestOfDiningRoom, enterWestRoomFromHallwayWestOfDiningRoom);
		getLargeClockHand.addStep(inDiningRoom, enterHallwayWestOfDiningRoom);
		getLargeClockHand.addStep(inThroneRoom, enterDiningRoomFromThroneRoom);
		getLargeClockHand.addStep(inRoomSouthOfThroneRoom, enterThroneRoomFromRoomSouthOfThroneRoom);

		// TODO: is this the correct hand?
		var smallClockHandNeedsReplacing = new VarbitRequirement(15509, 0);
		var largeClockHandNeedsReplacing = new VarbitRequirement(15510, 0);

		var returnToVanesculasHallway = new ObjectStep(this, 61573, new WorldPoint(2474, 7372, 0), "Return to the dining room.");
		var returnToHallwayNorthOfLobbyF1 = new ObjectStep(this, 61573, new WorldPoint(2450, 7402, 0), "Return to the dining room, avoiding the traps on the floor.");
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

		var returnToLobbyF1 = new ObjectStep(this, 61576, new WorldPoint(2457, 7417, 0), "Return to the dining room.");
		var climbDownstairsToLobbyF0 = new ObjectStep(this, 61608, new WorldPoint(2315, 7371, 1), "Return to the dining room.");
		var enterDiningRoomFromLobby = new ObjectStep(this, 61577, new WorldPoint(2327, 7360, 0), "Return to the dining room.");

		var getBackToDiningRoom = new ConditionalStep(this, todo);
		getBackToDiningRoom.addStep(inLobbyF0, enterDiningRoomFromLobby);
		getBackToDiningRoom.addStep(inLobbyF1, climbDownstairsToLobbyF0);
		getBackToDiningRoom.addStep(inHallwayNorthOfLobby, returnToLobbyF1);
		getBackToDiningRoom.addStep(inVanesculasHallway, returnToHallwayNorthOfLobbyF1);
		getBackToDiningRoom.addStep(inVanesculasStudy, returnToVanesculasHallway);

		var placeSmallClockHandOnWesternClock = new ObjectStep(this, 61661, new WorldPoint(2344, 7372, 0), "Place the small clock hand on the western Grandfather clock.", smallClockHand.highlighted());

		var westernClockLargeHandCorrect = new VarbitRequirement(15514, 11);
		var westernClockSmallHandCorrect = new VarbitRequirement(15513, 9);

		var clockWidgetOpen = new WidgetPresenceRequirement(963, 0);

		var westernClockNeedsFixing = not(and(westernClockLargeHandCorrect, westernClockSmallHandCorrect));
		var solveWesternClock = new DrakanClockSolver(this, 15514, 11, 15513, 9);
		// var solveWesternClock = new DrakanClockSolver(this, 15514, 5, 15513, 9);
		var playerAtWesternClock = new ZoneRequirement(new WorldPoint(2344, 7371, 0));
		var clickWesternClock = new ObjectStep(this, 61658, new WorldPoint(2344, 7372, 0), "Click the western clock");

		var placeLargeClockHandOnEasternClock = new ObjectStep(this, 61660, new WorldPoint(2350, 7372, 0), "Place the large clock hand on the eastern Grandfather clock.", largeClockHand.highlighted());

		var easternClockLargeHandCorrect = new VarbitRequirement(15517, 0);
		var easternClockSmallHandCorrect = new VarbitRequirement(15515, 4);
		var easternClockNeedsFixing = not(and(easternClockLargeHandCorrect, easternClockSmallHandCorrect));
		var solveEasternClock = new DrakanClockSolver(this, 15517, 0, 15515, 4);
		var playerAtEasternClock = new ZoneRequirement(new WorldPoint(2350, 7371, 0));
		var clickEasternClock = new ObjectStep(this, 61658, new WorldPoint(2350, 7372, 0), "Click the eastern clock");

		var closeClock = new WidgetStep(this, "Close the clock", 963, 16);

		var getKeyFromFireplace = new ObjectStep(this, 61662, new WorldPoint(2347, 7372, 0), "Get the key from the Fireplace in the dining room.");
		getKeyFromFireplace.addDialogStep("Search the fireplace.");
		var solveClockPuzzle = new ConditionalStep(this, getKeyFromFireplace, "Solve the clock puzzle");
		solveClockPuzzle.addStep(smallClockHandNeedsReplacing, placeSmallClockHandOnWesternClock);
		solveClockPuzzle.addStep(and(westernClockNeedsFixing, playerAtWesternClock, clockWidgetOpen), solveWesternClock);
		solveClockPuzzle.addStep(and(playerAtWesternClock, clockWidgetOpen), closeClock);
		solveClockPuzzle.addStep(westernClockNeedsFixing, clickWesternClock);
		solveClockPuzzle.addStep(largeClockHandNeedsReplacing, placeLargeClockHandOnEasternClock);
		solveClockPuzzle.addStep(and(easternClockNeedsFixing, playerAtEasternClock, clockWidgetOpen), solveEasternClock);
		solveClockPuzzle.addStep(and(playerAtEasternClock, clockWidgetOpen), closeClock);
		solveClockPuzzle.addStep(easternClockNeedsFixing, clickEasternClock);

		var hasGottenDrakanEmblemFromFireplace = new VarplayerRequirement(5632, true, 3);
		var needToFinishClockPuzzle = nand(westernClockNeedsFixing, easternClockNeedsFixing);

		var cmkToLobby = new ObjectStep(this, 61573, new WorldPoint(2342, 7373, 0), "Head to Vanescula's study.", explosiveBarrel, tinderbox);
		var cmkToLobbyF1 = new ObjectStep(this, 61599, new WorldPoint(2318, 7371, 0),"Head to Vanescula's study.", explosiveBarrel, tinderbox );
		var cmkToHallway1 = new ObjectStep(this, 61572, new WorldPoint(2314, 7382, 1),"Head to Vanescula's study.", explosiveBarrel, tinderbox);
		var cmkToVanesculasHallwayFromHallwayNorthOfLobby = new ObjectStep(this, 61576, new WorldPoint(2466, 7422, 0),"Head to Vanescula's study.", explosiveBarrel, tinderbox);
		var cmkToVanesculasStudy = new ObjectStep(this, 61576, new WorldPoint(2468, 7407, 0), "Head to Vanescula's study.", explosiveBarrel, tinderbox);
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
		var cmkPlaceEmblemInVanesculasStudy = new ObjectStep(this, 61638, new WorldPoint(2476, 7367, 0), "Place the emblem in Vanescula's study", anyOneEmblemHighlighted);
		var cmkToVanesculasChamber = new ObjectStep(this, 61572, new WorldPoint(2477, 7366, 0), "Enter Vanescula's chamber.");
		var cmkBlowUpWallInVanesculasChamber = new ObjectStep(this, 61613, new WorldPoint(2492, 7364, 0), "Place the explosive barrel on the cracked wall in Vanescula's chamber.", explosiveBarrel.highlighted(), tinderbox);
		var cmkEnterThroughHole = new ObjectStep(this, 61614, new WorldPoint(2492, 7364, 0), "Enter hole in the wall.");
		var cmkTakeEmblem3 = new ObjectStep(this, 61639, new WorldPoint(2486, 7421, 0), "Remove the emblem from the room with the Venator.");
		var cmkLeaveVenatorRoom = new ObjectStep(this, 61614, new WorldPoint(2482, 7412, 0), "Leave the venator room through the hole in the wall.");
		var cmkLeaveVanesculasChamber = new ObjectStep(this, 61573, new WorldPoint(2483, 7368, 0), "Leave Vanescula's room.", drakanEmblem3);
		var cmkTakeEmblemFromVanesculasStudy = new ObjectStep(this, 61639, new WorldPoint(2476, 7367, 0), "Take emblem from vanescula's study", drakanEmblem3);
		var cmkLeaveVanesculasStudy = new ObjectStep(this, 61573, new WorldPoint(2474, 7372, 0), "Leave vanescula's study", anyTwoEmblemHighlighted);
		var cmkRetrieveThirdEmblem = new ObjectStep(this, 61634, new WorldPoint(2469, 7408, 0), "Remove the third emblem from the receptacle.");
		var cmkPutEmblemInEastDoor = new ObjectStep(this, 61636, new WorldPoint(2476, 7410, 0), "Place an emblem in the empty receptacle by the east wall.", anyThreeEmblemHighlighted);
		var cmkEnterEastDoor = new ObjectStep(this, 61572, new WorldPoint(2477, 7409, 0), "Enter the door to Ranis' hallway.", anyTwoEmblemHighlighted);
		var cmkPutEmblemInRanisHallwayNorth = new ObjectStep(this, 61635, new WorldPoint(2486, 7404, 0), "Place an emblem in the empty receptacle at the north door.", anyTwoEmblemHighlighted);
		var cmkEnterRanisParlour = new ObjectStep(this, 61576, new WorldPoint(2485, 7405, 0), "Enter Ranis' parlour");
		var ornateSkull = new ItemRequirement("Ornate skull", 33741);
		var cmkGetSkull = new DetailedQuestStep(this, new WorldPoint(2471, 7384, 0), "Get the ornate skull from the table in the room.", ornateSkull);
		var cmkLeaveRanisParlourRoom = new ObjectStep(this, 61577, new WorldPoint(2475, 7379, 0), "Leave Ranis' parlour room", ornateSkull);
		var cmkRemoveEmblemRanisNorth = new ObjectStep(this, 61635, new WorldPoint(2486, 7404, 0), "Remove emblem from the receptacle.", ornateSkull);
		var cmkClimbDownStairsRanisHallway = new ObjectStep(this, 61604, new WorldPoint(2491, 7402, 0), "Climb-down Stairs.", ornateSkull, anyTwoEmblemHighlighted);
		var cmkPlaceEmblemDownstairs = new ObjectStep(this, 61632, new WorldPoint(2371, 7410, 0), "Place an emblem in the receptacle to the west, avoiding the traps on the floor.", ornateSkull, anyTwoEmblemHighlighted);
		cmkPlaceEmblemDownstairs.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2378, 7411, 0),
			new WorldPoint(2379, 7411, 0),
			new WorldPoint(2379, 7412, 0),
			new WorldPoint(2378, 7412, 0)
		);
		var cmkEnterWestChapelHallway = new ObjectStep(this, 61572, new WorldPoint(2372, 7409, 0), "Enter the west chapel hallway.", ornateSkull, anyOneEmblemHighlighted);
		var cmkPutEmblemInWestChapelHallway = new ObjectStep(this, 61631, new WorldPoint(2370, 7383, 0), "Place an emblem in the empty receptacle by the western door.", anyOneEmblemHighlighted);
		var cmkEnterEmblemGallery = new ObjectStep(this, 61572, new WorldPoint(2369, 7384, 0), "Enter the emblem gallery.");
		var cmkTalkToVeliaf = new NpcStep(this, 15878, new WorldPoint(2379, 7367, 0), "Talk-to Veliaf Hurtz.");
		var cmkOpenEmblemGalleryChest = new ObjectStep(this, 61681, new WorldPoint(2379, 7372, 0), "Search Chest.");
		var inArrowPuzzle = new WidgetTextRequirement(810, 15, 9, "Confirm");
		// TODO: puzzle wrap?
		var cmkArrowChestPuzzleStep = new ArrowChestPuzzleStep(this);
		cmkArrowChestPuzzleStep.setSolution(1,2,1,3,3);
		var cmkGetTheKeyFromTheChest = new ObjectStep(this, 61681, new WorldPoint(2379, 7372, 0), "Search the chest in the emblem gallery in Castle Drakan where you first spoke with Veliaf for the Crescent moon key. You will need 3 emblems to get all the way there again.");

		var anyEmblemInVanesculasHallwayEast = new VarbitRequirement(15506, 1, Operation.GREATER_EQUAL);
		var anyEmblemInRanisHallwayNorth = new VarbitRequirement(15505, 1, Operation.GREATER_EQUAL);
		var noEmblemInRanisHallwayNorth = new VarbitRequirement(15505, 0);
		var anyEmblemInNorthChapelHallway = new VarbitRequirement(15502, 1, Operation.GREATER_EQUAL);
		var anyEmblemInWestChapelHallway = new VarbitRequirement(15501, 1, Operation.GREATER_EQUAL);
		var cmkHasSpokenWithVeliaf = new VarbitRequirement(15464, 74, Operation.GREATER_EQUAL);
		var cmkSolvedChestPuzzle = new VarbitRequirement(15512, 1);
		var crescentMoonKey = new ItemRequirement("Crescent moon key", 33726);
		var newMoonKey = new ItemRequirement("New moon key", 33728);

		var getCrescentMoonKey = new ConditionalStep(this, todo2, "Get the Crescent Moon Key.");
		getCrescentMoonKey.addStep(cmkSolvedChestPuzzle, cmkGetTheKeyFromTheChest);
		getCrescentMoonKey.addStep(and(inEmblemGallery, cmkHasSpokenWithVeliaf, inArrowPuzzle), cmkArrowChestPuzzleStep);
		getCrescentMoonKey.addStep(and(inEmblemGallery, cmkHasSpokenWithVeliaf), cmkOpenEmblemGalleryChest);
		getCrescentMoonKey.addStep(and(inEmblemGallery), cmkTalkToVeliaf);
		getCrescentMoonKey.addStep(and(inWestChapelHallway, anyEmblemInWestChapelHallway), cmkEnterEmblemGallery);
		getCrescentMoonKey.addStep(and(inWestChapelHallway), cmkPutEmblemInWestChapelHallway);
		getCrescentMoonKey.addStep(and(inNorthChapelHallway, anyEmblemInNorthChapelHallway), cmkEnterWestChapelHallway);
		getCrescentMoonKey.addStep(and(inNorthChapelHallway), cmkPlaceEmblemDownstairs);
		getCrescentMoonKey.addStep(and(inRanisHallway, noEmblemInRanisHallwayNorth, ornateSkull, anyTwoEmblemHighlighted), cmkClimbDownStairsRanisHallway);
		getCrescentMoonKey.addStep(and(inRanisHallway, anyEmblemInRanisHallwayNorth, ornateSkull), cmkRemoveEmblemRanisNorth);
		getCrescentMoonKey.addStep(and(inRanisParlour, ornateSkull), cmkLeaveRanisParlourRoom);
		getCrescentMoonKey.addStep(and(inRanisParlour), cmkGetSkull);
		getCrescentMoonKey.addStep(and(inRanisHallway, anyEmblemInRanisHallwayNorth), cmkEnterRanisParlour);
		getCrescentMoonKey.addStep(and(inRanisHallway), cmkPutEmblemInRanisHallwayNorth);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, anyEmblemInVanesculasHallwayEast), cmkEnterEastDoor);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, anyThreeEmblemHighlighted), cmkPutEmblemInEastDoor);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, anyTwoEmblemHighlighted), cmkRetrieveThirdEmblem);
		getCrescentMoonKey.addStep(and(inVanesculasStudy, anyTwoEmblemHighlighted), cmkLeaveVanesculasStudy);
		getCrescentMoonKey.addStep(and(inVanesculasStudy, drakanEmblem3), cmkTakeEmblemFromVanesculasStudy);
		getCrescentMoonKey.addStep(and(inVanesculasChamber, drakanEmblem3), cmkLeaveVanesculasChamber);
		getCrescentMoonKey.addStep(and(inVenatorRoom, drakanEmblem3), cmkLeaveVenatorRoom);
		getCrescentMoonKey.addStep(inVenatorRoom, cmkTakeEmblem3);
		getCrescentMoonKey.addStep(and(inVanesculasChamber, vanesculasChamberWallDestroyed), cmkEnterThroughHole);
		getCrescentMoonKey.addStep(inVanesculasChamber, cmkBlowUpWallInVanesculasChamber);
		getCrescentMoonKey.addStep(and(inVanesculasStudy, placedEmblemInVanesculasStudy), cmkToVanesculasChamber);
		getCrescentMoonKey.addStep(inVanesculasStudy, cmkPlaceEmblemInVanesculasStudy);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, placedEmblemInVanesculasHallway), cmkToVanesculasStudy);
		getCrescentMoonKey.addStep(inVanesculasHallway, cmkPlaceEmblemInVanesculasHallway);
		getCrescentMoonKey.addStep(inHallwayNorthOfLobby, cmkToVanesculasHallwayFromHallwayNorthOfLobby);
		getCrescentMoonKey.addStep(inLobbyF1, cmkToHallway1);
		getCrescentMoonKey.addStep(inLobbyF0, cmkToLobbyF1);
		getCrescentMoonKey.addStep(inDiningRoom, cmkToLobby);

		var castleDrakanRoomTemporary = new VarbitBuilder(15499);
		var inKitchen = castleDrakanRoomTemporary.eq(11);
		var inLarder = castleDrakanRoomTemporary.eq(10);
		var inEmblemGalleryHallway = castleDrakanRoomTemporary.eq(12);
		var inLobbyBasementHallway =  castleDrakanRoomTemporary.eq(48);
		var inLobbyBasementVenator =  castleDrakanRoomTemporary.eq(49);
		var inBasementPrison =  castleDrakanRoomTemporary.eq(51);

		var syringeBarrel = new ItemRequirement("Syringe barrel", 33752);
		var venatorStomach = new ItemRequirement("Venator stomach", 33756);
		var sinkPlug = new ItemRequirement("Sink plug", 33747);
		var brokenPipe = new ItemRequirement("Broken pipe", 33748);
		var sharpKitchenKnife = new ItemRequirement("Sharp knife", 33749);
		var syringePlunger = new ItemRequirement("Syringe plunger", 33753);
		var tongs = new ItemRequirement("Tongs", 33750);
		var syringeNeedle = new ItemRequirement("Syringe needle", 33751);
		var emptySyringe = new ItemRequirement("Empty syringe", 33754);
		var fullSyringe = new ItemRequirement("Full syringe", 33755);

		var leftHalfOfCrest = new ItemRequirement("Left crest half", 33734);
		var rightHalfOfCrest = new ItemRequirement("Right crest half", 33735);
		var fullCrest = new ItemRequirement("Full crest", 33736);

		var enterEmblemGalleryHallway = new ObjectStep(this, 61593, new WorldPoint(2370, 7370, 0), "Leave the emblem gallery using the western door.");
		var enterRoomBehindLobby = new ObjectStep(this, 61593, new WorldPoint(2325, 7403, 0), "Enter the south-west room, ready to kill a few bugs.");
		var killBugsAndTakeSyringeBarrel = new DetailedQuestStep(this, new WorldPoint(2315, 7418, 0), "Kill the bugs and take the Syringe barrel.", syringeBarrel);
		var searchCrateForVenatorStomach = new ObjectStep(this, 61751, new WorldPoint(2315, 7408, 0), "Search the create in the south-east corner for a venator stomach.", syringeBarrel);
		var inspectSink = new ObjectStep(this, 61719, new WorldPoint(2308, 7411, 0), "Inspect the sink to the west.", syringeBarrel, venatorStomach);
		inspectSink.addDialogStep("Remove the plug.");
		var enterLarder = new ObjectStep(this, 61576, new WorldPoint(2309, 7421, 0), "Enter the larder through the north-west door.");
		var useSinkPlugOnSinkInLarder = new ObjectStep(this, 61722, new WorldPoint(2356, 7400, 0), "Put the sink plug into the sink.", sinkPlug.highlighted());
		var turnSinkTapOn = new ObjectStep(this, 61722, new WorldPoint(2356, 7400, 0), "Turn the tap on in the sink.");
		turnSinkTapOn.addDialogStep("Turn the tap on.");
		var takePipe = new ObjectStep(this, 61722, new WorldPoint(2356, 7400, 0), "Take the pipe from the now-broken sink.");
		takePipe.addDialogStep("Yes.");
		var reachBehindCabinetWithPipe = new ObjectStep(this, 61726, new WorldPoint(2360, 7403, 0), "Use the broken pipe on the cabinet to reach behind it.", brokenPipe.highlighted());
		var searchLarderCrateForSyringePlunger = new ObjectStep(this, 61751, new WorldPoint(2358, 7397, 0), "Search the crate for a syringe plunger.");
		var leaveLarder = new ObjectStep(this, 61577, new WorldPoint(2361, 7393, 0), "Return to the Kitchen.");
		var useSharpKnifeOnVenatorCorpse = new ObjectStep(this, 61727, new WorldPoint(2311, 7417, 0), "Use the sharp kitchen knife on the venator corpse.", sharpKitchenKnife.highlighted());
		var getTongsFromVenatorCorpse = new ObjectStep(this, 61727, new WorldPoint(2311, 7417, 0), "Search the venator corpse for tongs.");
		useSharpKnifeOnVenatorCorpse.addSubSteps(getTongsFromVenatorCorpse);
		var getNeedleFromSink = new ObjectStep(this, 61719, new WorldPoint(2308, 7411, 0), "Use the tongs on the sink to get a syringe needle.", tongs.highlighted());
		var assembleSyringe = new DetailedQuestStep(this, "Combine the syringe parts to assemble a syringe.", syringeBarrel.highlighted(), syringePlunger.highlighted(), syringeNeedle.highlighted());
		var drawBloodFromVenatorStomach = new DetailedQuestStep(this, "Use the empty syringe on the venator stomach in your inventory to draw blood from it.", emptySyringe.highlighted(), venatorStomach.highlighted());
		var useSyringeOnChest = new ObjectStep(this, 61683, new WorldPoint(2308, 7415, 0), "Use the full syringe on the chest to receive the left crest half.", fullSyringe.highlighted());
		var getLeftCrestHalfFromKitchenChest = new ObjectStep(this, 61683, new WorldPoint(2308, 7415, 0), "Search the chest in the kitchen for the left crest half.");
		getLeftCrestHalfFromKitchenChest.addDialogStep("Yes.");
		useSyringeOnChest.addSubSteps(getLeftCrestHalfFromKitchenChest);

		var larderSinkNeedsPlugging = new VarbitRequirement(15537, 0);
		var larderSinkPlugged = new VarbitRequirement(15537, 1);
		var larderSinkCollapsed = new VarbitRequirement(15537, 2);

		var hasCutVenatorStomachUp = new VarbitRequirement(15538, 1);

		var unlockedKitchenChest = new VarbitRequirement(15518, 1);



		var leaveKitchen = new ObjectStep(this, 61592, new WorldPoint(2314, 7421, 0), "Leave the kitchen and head to the lobby.", leftHalfOfCrest);
		var enterLobbyFromEmblemGalleryHallway = new ObjectStep(this, 61577, new WorldPoint(2330, 7407, 0), "Enter the lobby.", leftHalfOfCrest);
		var climbDownstairsToLobbyBasement = new ObjectStep(this, 61606, new WorldPoint(2311, 7373, 0), "Climb down to the basement.", leftHalfOfCrest);
		var enterAnotherVenatorRoom = new ObjectStep(this, 61593, new WorldPoint(2570, 7365, 0), "Enter Crescent moon door to your east, ready to kill another Venator. Turn off your protection prayer when it's about to screech.", leftHalfOfCrest);
		// i could technically use this varp to see if the venator in that room is dead
		// [2026-07-05T13:27:48Z 5913] varp CASTLE_DRAKAN_ENEMY_STATUS_2 (5641) 32830 -> 98366
		var venatorAlive = new NpcCondition(16217);
		var killVenator = new NpcStep(this, 16217, new WorldPoint(2569, 7384, 0), "Kill the Venator. Remove your protection prayer when it's about to screech.");
		var battleAxe = new ItemRequirement("Battleaxe", 33759);
		var searchWeaponRackForOneAxe = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for one battleaxe.");
		searchWeaponRackForOneAxe.addDialogStep("Take a battleaxe.");
		var placeBattleAxeOnStatue = new ObjectStep(this, 61743, new WorldPoint(2577, 7380, 0), "Place the battleaxe on the east-most empty statue.", battleAxe.highlighted());
		var needToPutAxeOnStatue = new VarbitRequirement(15546, 0);
		var getMace1 = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for a mace.");
		getMace1.addDialogStep("Take a mace.");
		var getMace2 = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for two maces.");
		getMace2.addDialogStep("Take a mace.");
		var getSword = new ObjectStep(this, 61741, new WorldPoint(2579, 7382, 0), "Search the weapons rack to the east for a sword.");
		getSword.addDialogStep("Take a sword.");

		var mace = new ItemRequirement("Mace", 33760);
		var mace2 = mace.quantity(2);

		var sword = new ItemRequirement("Sword", 33757);

		var placeMaceOnStatueN = new ObjectStep(this, 61746, new WorldPoint(2569, 7386, 0), "Place a mace on the second pair of statues from the west.", mace.highlighted());
		var placeMaceOnStatueS = new ObjectStep(this, 61746, new WorldPoint(2569, 7380, 0), "Place a mace on the second pair of statues from the west.", mace.highlighted());

		var needToPutMaceOnStatue1 = new VarbitRequirement(15548, 0);
		var needToPutMaceOnStatue2 = new VarbitRequirement(15547, 0);

		var needToPutSwordOnStatue = new VarbitRequirement(15545, 0);

		var placeSwordOnStatue = new ObjectStep(this, 61746, new WorldPoint(2565, 7386, 0), "Place a mace on the western empty statue.", sword.highlighted());

		var getNewMoonKey = new ConditionalStep(this, todo2, "\nGet the new moon key.");

		var doneWithWeaponPuzzle = and(new VarbitRequirement(15545, 1), new VarbitRequirement(15546, 1), new VarbitRequirement(15547, 1), new VarbitRequirement(15548, 1));

		var openWeaponPuzzleChest = new ObjectStep(this, 61685, new WorldPoint(2570, 7380, 0), "Search the chest for an item.");

		var combineCrests = new DetailedQuestStep(this, "Combine the two pieces of crests in your inventory.", leftHalfOfCrest.highlighted(), rightHalfOfCrest.highlighted());

		var leaveWithFullCrest1 = new ObjectStep(this, 61592, new WorldPoint(2566, 7387, 0), "Head to the study, north of the throne room.", fullCrest);
		var leaveWithFullCrest2 = new ObjectStep(this, 61600, new WorldPoint(2564, 7362, 0), "Head to the study, north of the throne room.", fullCrest);
		var leaveWithFullCrest3 = new ObjectStep(this, 61577, new WorldPoint(2327, 7360, 0), "Head to the study, north of the throne room.", fullCrest);
		var leaveWithFullCrest4 = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 0), "Head to the study, north of the throne room.", fullCrest);
		var leaveWithFullCrest5 = new ObjectStep(this, 61576, new WorldPoint(2309, 7397, 0), "Head to the study, north of the throne room.", fullCrest);
		var putFullCrestOnFireplaceInStudy = new ObjectStep(this, 61665, new WorldPoint(2358, 7386, 0), "Place the full crest on the fireplace in the study, north of the throne room.", fullCrest.highlighted());

		var fullCrestInStudy = new VarbitRequirement(15535, 1);

		var getNewMoonKeyFromFireplace = new ObjectStep(this, 61665, new WorldPoint(2358, 7386, 0), "Search the fireplace for the new moon key in the study, north of the throne room.");
		getNewMoonKeyFromFireplace.addDialogStep("Yes.");

		getNewMoonKey.addStep(fullCrestInStudy, getNewMoonKeyFromFireplace);
		getNewMoonKey.addStep(and(inStudy, fullCrest), putFullCrestOnFireplaceInStudy);
		getNewMoonKey.addStep(and(inThroneRoom, fullCrest), leaveWithFullCrest5);
		getNewMoonKey.addStep(and(inDiningRoom, fullCrest), leaveWithFullCrest4);
		getNewMoonKey.addStep(and(inLobbyF0, fullCrest), leaveWithFullCrest3);
		getNewMoonKey.addStep(and(inLobbyBasementHallway, fullCrest), leaveWithFullCrest2);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, fullCrest), leaveWithFullCrest1);

		getNewMoonKey.addStep(and(leftHalfOfCrest, rightHalfOfCrest), combineCrests);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, venatorAlive), killVenator);

		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, doneWithWeaponPuzzle), openWeaponPuzzleChest);

		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutAxeOnStatue, battleAxe), placeBattleAxeOnStatue);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutAxeOnStatue), searchWeaponRackForOneAxe);

		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, needToPutMaceOnStatue2, mace2), placeMaceOnStatueN);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, needToPutMaceOnStatue2, mace), getMace2);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, needToPutMaceOnStatue2), getMace2);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue2, mace), placeMaceOnStatueN);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutMaceOnStatue1, mace), placeMaceOnStatueS);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, or(needToPutMaceOnStatue1, needToPutMaceOnStatue2)), getMace1);

		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutSwordOnStatue, sword), placeSwordOnStatue);
		getNewMoonKey.addStep(and(inLobbyBasementVenator, leftHalfOfCrest, needToPutSwordOnStatue), getSword);

		getNewMoonKey.addStep(and(inLobbyBasementHallway, leftHalfOfCrest), enterAnotherVenatorRoom);
		getNewMoonKey.addStep(and(inLobbyF0, leftHalfOfCrest), climbDownstairsToLobbyBasement);
		getNewMoonKey.addStep(and(inEmblemGalleryHallway, leftHalfOfCrest), enterLobbyFromEmblemGalleryHallway);

		getNewMoonKey.addStep(and(inKitchen, leftHalfOfCrest), leaveKitchen);
		getNewMoonKey.addStep(and(inKitchen, unlockedKitchenChest), getLeftCrestHalfFromKitchenChest);
		getNewMoonKey.addStep(and(inKitchen, fullSyringe), useSyringeOnChest);
		getNewMoonKey.addStep(and(inKitchen, venatorStomach, emptySyringe), drawBloodFromVenatorStomach);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, syringePlunger, syringeNeedle), assembleSyringe);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, syringePlunger, tongs), getNeedleFromSink);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, syringePlunger, hasCutVenatorStomachUp), getTongsFromVenatorCorpse);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, sharpKitchenKnife, syringePlunger), useSharpKnifeOnVenatorCorpse);
		getNewMoonKey.addStep(and(inLarder, sharpKitchenKnife, syringePlunger), leaveLarder);
		getNewMoonKey.addStep(and(inLarder, sharpKitchenKnife), searchLarderCrateForSyringePlunger);
		getNewMoonKey.addStep(and(inLarder, brokenPipe), reachBehindCabinetWithPipe);
		getNewMoonKey.addStep(and(inLarder, larderSinkCollapsed), takePipe);
		getNewMoonKey.addStep(and(inLarder, larderSinkPlugged), turnSinkTapOn);
		getNewMoonKey.addStep(and(inLarder, larderSinkNeedsPlugging), useSinkPlugOnSinkInLarder);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach, sinkPlug), enterLarder);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel, venatorStomach), inspectSink);
		getNewMoonKey.addStep(and(inKitchen, syringeBarrel), searchCrateForVenatorStomach);
		getNewMoonKey.addStep(inKitchen, killBugsAndTakeSyringeBarrel);
		getNewMoonKey.addStep(inEmblemGalleryHallway, enterRoomBehindLobby);
		getNewMoonKey.addStep(inEmblemGallery, enterEmblemGalleryHallway);
		// TODO: Get the new moon key

		var getGildedAndGibbousKeys = new ConditionalStep(this, todo2, "\nGet the gilded and gibbous keys.");

		var ggkGoToThroneRoom = new ObjectStep(this, 61576, new WorldPoint(2358, 7380, 0), "Head to the basement.");
		var ggkGoToDiningRoom = new ObjectStep(this, 61573, new WorldPoint(2304, 7392, 0), "Head to the basement.");
		var ggkGoToLobby1 = new ObjectStep(this, 61573, new WorldPoint(2342, 7373, 0), "Head to the basement.");
		var ggkGoToBasement = new ObjectStep(this, 61606, new WorldPoint(2311, 7366, 0), "Climb down the stairs to the basement.");
		var ggkEnterBasementNorthRoom = new ObjectStep(this, 61584, new WorldPoint(2570, 7369, 0), "Enter through the new moon door to the north to free Safalaan and Vanescula.");
		var gibbousMoonKey = new ItemRequirement("Gibbous moon key", 33727);
		var ggkTakeGibbousMoonKey = new DetailedQuestStep(this, new WorldPoint(2573, 7395, 0), "Take the gibbous moon key from the bench.", gibbousMoonKey);
		var ggkReturnToBasementHallway = new ObjectStep(this, 61585, new WorldPoint(2566, 7394, 0), "Leave the prison.");
		var ggkReturnToLobbyF0 = new ObjectStep(this, 61600, new WorldPoint(2564, 7369, 0), "Climb-up Stairs.");
		var ggkClimbUpToLobbyF1 = new ObjectStep(this, 61599, new WorldPoint(2315, 7370, 0), "Climb-up Stairs.");
		var ggkEnterLobbyF1GibbousMoonDoor = new ObjectStep(this, 61588, new WorldPoint(2327, 7360, 1), "Enter Gibbous moon door.");
		var inDiningRoomF1 =  and(inDiningRoom, onF1);
		var ggkEnterEastDoor = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 1), "Enter Door.");
		var inThroneRoomF1 =  and(inThroneRoom, onF1);
		var ggkEnterSouthDoor = new ObjectStep(this, 61572, new WorldPoint(2306, 7386, 1), "Enter Door.");
		var inHallway5 = castleDrakanRoomTemporary.eq(18);
		var ggkEnterSouthEastDoor = new ObjectStep(this, 61572, new WorldPoint(2442, 7361, 0), "Enter the south-east door, avoiding the traps on the floor.");
		ggkEnterSouthEastDoor.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2438, 7363, 0),
			new WorldPoint(2438, 7364, 0),
			new WorldPoint(2439, 7363, 0),
			new WorldPoint(2439, 7364, 0)
		);
		var inOrnateKnifeRoom = castleDrakanRoomTemporary.eq(21);
		var ornateKnife = new ItemRequirement("Ornate knife", 33740);
		var ggkGetOrnateKnife = new ObjectStep(this, 61751, new WorldPoint(2454, 7372, 0), "Search the eastern crate for an ornate knife.");
		var ggkPullLeverInOrnateKnifeRoom = new ObjectStep(this, 61776, new WorldPoint(2449, 7371, 0), "Pull the lever to the west.");

		var pulledUpperStoreroomLever = new VarbitRequirement(15532, 1);

		var ggkEnterUpperStoreroomPortal = new ObjectStep(this, 61771, new WorldPoint(2450, 7372, 0), "Enter Portal.");

		var inGuestChamberStoreroom = castleDrakanRoomTemporary.eq(46);

		var ggkLeaveGuestChamberStoreroom = new ObjectStep(this, 61589, new WorldPoint(2522, 7397, 0), "Enter Gibbous moon door.");

		var inRoomOutsideGuestChamberStoreroom = castleDrakanRoomTemporary.eq(38);
		var ggkEnterRoomWithVenator = new ObjectStep(this, 61588, new WorldPoint(2510, 7370, 0), "Enter Gibbous moon door, ready to fight another Venator.");
		var ggkKillVenator = new NpcStep(this, 16217, new WorldPoint(2522, 7368, 0), "Kill the Venator.");

		var inVenatorPuzzleroom = castleDrakanRoomTemporary.eq(39);
		var inVenatorPuzzleroomLibrary = castleDrakanRoomTemporary.eq(44);

		var ggkpLightFireplace = new ObjectStep(this, 61730, new WorldPoint(2521, 7371, 0), "Light Fireplace.", tinderbox);
		var isFireplaceLit = new VarbitRequirement(15543, 1);
		var ggkpSearchChest = new ObjectStep(this, 61734, new WorldPoint(2526, 7371, 0), "Search Chest.");

		var smallLockbox = new ItemRequirement("Lockbox", 33761);
		var combinationLockWidgetOpen = new WidgetPresenceRequirement(InterfaceID.CombinationLock.CONTENTS);
		var ggkpSolveLockboxPuzzle = new ChestCodeStep(this, "small lockbox", "⠿ ᴟ ⁘", 10, 2, 3, 7);

		var ggkpTryOpenLockbox = new DetailedQuestStep(this, "Open the small lockbox", smallLockbox.highlighted());

		var fancyGem1 = new ItemRequirement("Fancy gem", 33765);
		var fancyGem2 = new ItemRequirement("Fancy gem", 33766);

		var ggkpPlaceFancyGemInHead = new ObjectStep(this, 61687, new WorldPoint(2531, 7367, 0), "Place the fancy gem on the venator head.", fancyGem1.highlighted());

		var ggkpSolveDoorPuzzle = new ChestCodeStep(this, "door", "SPEAR", 10, 1, 1, 1, 3, 0);

		var venatorHeadOneEyePlaced = new VarbitRequirement(15523, 1);
		var ggkpEnterSouthWestDoor = new ObjectStep(this, 61625, new WorldPoint(2519, 7363, 0), "Enter the south-west door.");

		var ggkSolvePuzzle = new ConditionalStep(this, ggkpLightFireplace, "\nSolve the room puzzle.", ornateSkull, ornateKnife);
		var playerNextToDoorPuzzle = new ZoneRequirement(new WorldPoint(2519, 7364, 0));

		var doorPuzzleSolved = new VarbitRequirement(15519, 1);

		var ggkpSearchBookcaseForBook = new ObjectStep(this, 61757, new WorldPoint(2549, 7381, 0), "Search Bookcase.");

		var mysteriousBook = new ItemRequirement("Mysterious book", 33764);

		var ggkpOpenMysteriousBook = new DetailedQuestStep(this, "Open the mysterious book.", mysteriousBook.highlighted());

		var ggkpLeaveLibrary = new ObjectStep(this, 61577, new WorldPoint(2551, 7387, 0), "Leave the library.", fancyGem2);

		var ggkpPlaceFancyGemInHead2 = new ObjectStep(this, 61687, new WorldPoint(2531, 7367, 0), "Place the fancy gem on the venator head.", fancyGem2.highlighted());

		var venatorHeadBothEyePlaced = new VarbitRequirement(15523, 3);

		var ggkpSearchVenatorHead = new ObjectStep(this, 61687, new WorldPoint(2531, 7367, 0), "Search Mounted head.");
		ggkpSearchVenatorHead.addDialogStep("Yes.");

		var ornateHourglass = new ItemRequirement("Ornate hourglass", 33742);
		ornateHourglass.setTooltip("You can return to the puzzle room with the venator and search the venator head to get a new one");

		var ggkpLeavePuzzleRoom = new ObjectStep(this, 61589, new WorldPoint(2515, 7367, 0), "Enter Gibbous moon door.", ornateHourglass);

		ggkSolvePuzzle.addStep(and(inVenatorPuzzleroom, ornateHourglass), ggkpLeavePuzzleRoom);
		ggkSolvePuzzle.addStep(venatorHeadBothEyePlaced, ggkpSearchVenatorHead);
		ggkSolvePuzzle.addStep(and(fancyGem2, inVenatorPuzzleroom), ggkpPlaceFancyGemInHead2);
		ggkSolvePuzzle.addStep(and(fancyGem2, inVenatorPuzzleroomLibrary), ggkpLeaveLibrary);
		ggkSolvePuzzle.addStep(mysteriousBook, ggkpOpenMysteriousBook);
		ggkSolvePuzzle.addStep(and(doorPuzzleSolved, inVenatorPuzzleroomLibrary), ggkpSearchBookcaseForBook);
		ggkSolvePuzzle.addStep(and(playerNextToDoorPuzzle, combinationLockWidgetOpen), ggkpSolveDoorPuzzle);
		ggkSolvePuzzle.addStep(venatorHeadOneEyePlaced, ggkpEnterSouthWestDoor);
		ggkSolvePuzzle.addStep(fancyGem1, ggkpPlaceFancyGemInHead);
		ggkSolvePuzzle.addStep(and(smallLockbox, combinationLockWidgetOpen), ggkpSolveLockboxPuzzle);
		ggkSolvePuzzle.addStep(smallLockbox, ggkpTryOpenLockbox);
		ggkSolvePuzzle.addStep(isFireplaceLit, ggkpSearchChest);

		// This is in an attempt to hide the requirement if it's already been placed in the display case
		ornateSkull.setConditionToHide(new VarbitRequirement(15563, true, 0));
		ornateKnife.setConditionToHide(new VarbitRequirement(15563, true, 1));
		ornateHourglass.setConditionToHide(new VarbitRequirement(15563, true, 2));
		var allItemsPlacedInDisplayCase = new VarbitRequirement(15563, 7);

		// I placed the ornate knife, then hourglass, then skull
		// Varbit went to 0b010 after placing knife
		// Varbit went to 0b110 after placing hourglass
		// Varbit went to 0b111 after placing skull


		var gildedKey = new ItemRequirement("Gilded key", 33729);

		var ggkPlaceItemsOnDisplayCase = new ObjectStep(this, 61672, new WorldPoint(2497, 7374, 0), "Place the items on the display case", ornateHourglass.highlighted(), ornateKnife.highlighted(), ornateSkull.highlighted());

		var ggkSearchDisplayCaseForGildedKey = new ObjectStep(this, 61672, new WorldPoint(2497, 7374, 0), "Search the display case for the gilded key.");
		ggkSearchDisplayCaseForGildedKey.addDialogStep("Yes.");

		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom, allItemsPlacedInDisplayCase), ggkSearchDisplayCaseForGildedKey);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom, venatorHeadBothEyePlaced), ggkPlaceItemsOnDisplayCase);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom), ggkEnterRoomWithVenator);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inVenatorPuzzleroom, venatorAlive), ggkKillVenator);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, or(inVenatorPuzzleroom, inVenatorPuzzleroomLibrary)), ggkSolvePuzzle);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inRoomOutsideGuestChamberStoreroom), ggkEnterRoomWithVenator);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inGuestChamberStoreroom), ggkLeaveGuestChamberStoreroom);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, ornateKnife, inOrnateKnifeRoom, pulledUpperStoreroomLever), ggkEnterUpperStoreroomPortal);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, ornateKnife, inOrnateKnifeRoom), ggkPullLeverInOrnateKnifeRoom);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inOrnateKnifeRoom), ggkGetOrnateKnife);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inHallway5), ggkEnterSouthEastDoor);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inThroneRoomF1), ggkEnterSouthDoor);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inDiningRoomF1), ggkEnterEastDoor);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inLobbyF1), ggkEnterLobbyF1GibbousMoonDoor);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inLobbyF0), ggkClimbUpToLobbyF1);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inLobbyBasementHallway), ggkReturnToLobbyF0);
		getGildedAndGibbousKeys.addStep(and(gibbousMoonKey, inBasementPrison), ggkReturnToBasementHallway);
		getGildedAndGibbousKeys.addStep(inBasementPrison, ggkTakeGibbousMoonKey);
		getGildedAndGibbousKeys.addStep(inLobbyBasementHallway, ggkEnterBasementNorthRoom);
		getGildedAndGibbousKeys.addStep(inLobbyF0, ggkGoToBasement);
		getGildedAndGibbousKeys.addStep(inDiningRoom, ggkGoToLobby1);
		getGildedAndGibbousKeys.addStep(inThroneRoom, ggkGoToDiningRoom);
		getGildedAndGibbousKeys.addStep(inStudy, ggkGoToThroneRoom);

		var fmkClimbDownstairsFromDisplayCase = new ObjectStep(this, 61609, new WorldPoint(2503, 7369, 0), "Climb-down Stairs.");
		var fmkGoThroughLobbyF1NorthDoor = new ObjectStep(this, 61572, new WorldPoint(2314, 7382, 1), "Enter Door.");
		var fmkEnterGildedDoorToWest = new ObjectStep(this, 61627, new WorldPoint(2452, 7420, 0), "Enter Gilded door - avoid the bugs.");
		var inSmallHallway = castleDrakanRoomTemporary.eq(33);
		var fmkNorthWest = new ObjectStep(this, 61573, new WorldPoint(2441, 7417, 0), "Enter the north-western door.");

		var inRoomWithIvanAndVenator = castleDrakanRoomTemporary.eq(29);

		var hasUsedGildedKey = new VarplayerRequirement(5638, true, 3);
		gildedKey.setConditionToHide(hasUsedGildedKey);

		var fmkKillVenator = new NpcStep(this, 16213, new WorldPoint(2456, 7392, 0), "Attack Venator  (level-195).");

		// could also use [2026-07-11T11:49:32Z 9968] varp CASTLE_DRAKAN_ENEMY_STATUS_1 (5640) 1238587111 -> 1507022567
		var fmkVenatorAlive = new NpcCondition(16213);

		var fmkTalkToIvan1 = new NpcStep(this, 15855, new WorldPoint(2455, 7388, 0), "Talk-to Ivan Strom.");

		var fmkIvanFollowingYou = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);

		var gildedBookPuzzleOpen = new WidgetPresenceRequirement(InterfaceID.CastleDrakanBookcase.CONTENTS);
		var fmkGildedBookPuzzle = new GildedBookPuzzle(this);

		var gildedBook = new ItemRequirement("Gilded book", 33767);
		var fmkGetGildedBook = new ObjectStep(this, 61756, new WorldPoint(2455, 7395, 0), "Search Bookcase for gilded book.");

		var fmkUseGildedBookOnWesternGildedBookcase = new ObjectStep(this, 61692, new WorldPoint(2451, 7391, 0), "Search Bookcase.", gildedBook.highlighted());

		var startedLibraryPuzzle = new VarbitRequirement(15525, 1);
		var finishedLibraryPuzzle = new VarbitRequirement(15525, 2);

		var fmkClickGildedBookcase = new ObjectStep(this, 61692, new WorldPoint(2451, 7391, 0), "Click the gilded bookcase to start rearranging the books.");
		fmkClickGildedBookcase.addDialogStep("Yes.");

		var fmkEnterSecretPassage = new ObjectStep(this, 61615, new WorldPoint(2455, 7386, 0), "Enter the secret passage to the south.");

		var inSecretRoom = castleDrakanRoomTemporary.eq(32);

		var fullMoonKey = new ItemRequirement("Full moon key", 33724);

		var fmkTakeFullMoonKey = new DetailedQuestStep(this, new WorldPoint(2433, 7416, 0), "Take the full moon key from the table.", fullMoonKey.highlighted());

		var getFullMoonKey = new ConditionalStep(this, todo2, "\nGet the full moon key.");
		getFullMoonKey.addStep(and(inSecretRoom, fmkIvanFollowingYou), fmkTakeFullMoonKey);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, finishedLibraryPuzzle), fmkEnterSecretPassage);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkVenatorAlive), fmkKillVenator);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, startedLibraryPuzzle, gildedBookPuzzleOpen), fmkGildedBookPuzzle);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, startedLibraryPuzzle), fmkClickGildedBookcase);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou, gildedBook), fmkUseGildedBookOnWesternGildedBookcase);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator, fmkIvanFollowingYou), fmkGetGildedBook);
		getFullMoonKey.addStep(and(inRoomWithIvanAndVenator), fmkTalkToIvan1);
		getFullMoonKey.addStep(inSmallHallway, fmkNorthWest);
		getFullMoonKey.addStep(inHallwayNorthOfLobby, fmkEnterGildedDoorToWest);
		getFullMoonKey.addStep(inLobbyF1, fmkGoThroughLobbyF1NorthDoor);
		getFullMoonKey.addStep(inRoomOutsideGuestChamberStoreroom, fmkClimbDownstairsFromDisplayCase);

		var skLeaveEastDoor = new ObjectStep(this, 61572, new WorldPoint(2438, 7419, 0), "Enter Door.");
		var skLeaveGildedDoor = new ObjectStep(this, 61626, new WorldPoint(2445, 7418, 0), "Enter Gilded door.");
		var skEnterScratchedDoor = new ObjectStep(this, 61576, new WorldPoint(2466, 7422, 0), "Enter Door.");

		var skEnterEasternDoor = new ObjectStep(this, 61572, new WorldPoint(2477, 7409, 0), "Enter the eastern door, avoiding the traps on the floor.");
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

		var skClimbDownStairs = new ObjectStep(this, 61604, new WorldPoint(2491, 7402, 0), "Climb-down Stairs.");
		var skEnterWesternDoor = new ObjectStep(this, 61572, new WorldPoint(2372, 7409, 0), "Enter the western door, avoiding the traps on the floor.");
		skEnterWesternDoor.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2378, 7411, 0),
			new WorldPoint(2379, 7411, 0),
			new WorldPoint(2379, 7412, 0),
			new WorldPoint(2378, 7412, 0)
		);
		var skEnterEastFullMoonDoor = new ObjectStep(this, 61591, new WorldPoint(2373, 7392, 0), "Enter Full moon door.");

		var inBottleRoom = castleDrakanRoomTemporary.eq(16);

		var cloudyGreyPotion = new ItemRequirement("Cloudy grey potion", 33769);
		var weightlessBlackPotion = new ItemRequirement("Weightless black potion", 33770);
		var thickRedPotion = new ItemRequirement("Thick red potion", 33771);
		var coldBlueishWhitePotion = new ItemRequirement("Cold bluish-white potion", 33772);

		// requires 4 inv slots
		var skGetCloudyGreyPotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetCloudyGreyPotion.addDialogStep("Take a cloudy grey potion.");
		var skGetWeightlessBlackPotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetWeightlessBlackPotion.addDialogStep("Take a weightless black potion.");
		var skGetThickRedPotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetThickRedPotion.addDialogStep("Take a thick red potion.");
		var skGetColdBluishWhitePotion = new ObjectStep(this, 61706, new WorldPoint(2380, 7396, 0), "Search Shelves, taking one of each bottle.");
		skGetColdBluishWhitePotion.addDialogStep("Take a cold bluish-white potion.");

		var smokeBasin = new ObjectStep(this, 61702,new WorldPoint(2380, 7385, 0), "Pour the cloudy grey potion into the south-west basin.", cloudyGreyPotion.highlighted());
		var iceBasin = new ObjectStep(this, 61705, new WorldPoint(2387, 7384, 0), "Pour the cold bluish-white potion into the south-east basin.", coldBlueishWhitePotion.highlighted());
		var shadowBasin = new ObjectStep(this, 61703, new WorldPoint(2387, 7389, 0), "Pour the weightless black potion into the north-east basin.", weightlessBlackPotion.highlighted());
		var bloodBasin = new ObjectStep(this, 61704, new WorldPoint(2380, 7389, 0), "Pour the thick red potion into the north-west basin.", thickRedPotion.highlighted());

		var solvedSmokeBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_SMOKE_BASIN, 1);
		var solvedShadowBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_SHADOW_BASIN, 1);
		var solvedBloodBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_BLOOD_BASIN, 1);
		var solvedIceBasin = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_CHAPEL_ICE_BASIN, 1);

		var getSolidKey = new ConditionalStep(this, todo3, "\nGet the solid key.");

		// Intent is:
		// Ask the user to get all 4 potions
		// Once the user has all 4 potions:
		// Pour thick red potion in north-west basin (blood)
		// Pour cloudy grey potion in south-west basin (smoke)
		// Pour cold bluish-white potion in south-east basin (ice)
		// Pour weightless black potion in north-east basin (shadow)

		var solvedAllBasins = and(solvedBloodBasin, solvedShadowBasin, solvedIceBasin, solvedSmokeBasin);

		var skSearchAltarForAncientSymbol = new ObjectStep(this, 61701, new WorldPoint(2382, 7393, 0), "Search the altar for an ancient symbol.");

		var skEnterEastDoorFromBottleRoom = new ObjectStep(this, 61590, new WorldPoint(2388, 7383, 0), "Enter the south-east door.");

		var ancientSymbol = new ItemRequirement("Ancient symbol", 33737);

		var inChapelLibrary = castleDrakanRoomTemporary.eq(14);

		var skPullLever = new ObjectStep(this, 61778, new WorldPoint(2393, 7372, 0), "Pull Lever.");

		var openedPortalFromChapelLibraryToServantsQuarters = new VarbitRequirement(15533, 1);

		var skEnterPortal = new ObjectStep(this, 61773, new WorldPoint(2393, 7370, 0), "Enter Portal.");

		var inServantsQuarters = castleDrakanRoomTemporary.eq(30);

		var skLeaveServantsQuarters = new ObjectStep(this, 61585, new WorldPoint(2438, 7394, 0), "Leave the servants quarters.");

		var skLeaveExplosiveRoom = new ObjectStep(this, 61572, new WorldPoint(2444, 7386, 0), "Enter Door.");

		var skLeaveExplosiveRoomHallway = new ObjectStep(this, 61572, new WorldPoint(2474, 7398, 0), "Enter Door.");

		var skLeaveLobbyF1 = new ObjectStep(this, 61588, new WorldPoint(2327, 7360, 1), "Enter Gibbous moon door.");

		var skLeaveDiningRoomF1 = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 1), "Enter Door.");

		var skLeaveThroneRoomF1 = new ObjectStep(this, 61590, new WorldPoint(2306, 7397, 1), "Enter Full moon door.");

		var inRoomAboveStudy = castleDrakanRoomTemporary.eq(19);

		var skGetAncientShield = new ObjectStep(this, 61751, new WorldPoint(2439, 7378, 0), "Search Crate.");

		var ancientShield = new ItemRequirement("Ancient shield", 33738);

		var skCombineAncientShieldAndAncientSymbol = new DetailedQuestStep(this, "Combine the ancient shield and ancient symbol.", ancientShield.highlighted(), ancientSymbol.highlighted());

		var shieldWithSymbol = new ItemRequirement("Shield with symbol", 33739);

		var skLeaveStudy = new ObjectStep(this, 61591, new WorldPoint(2434, 7372, 0), "Leave the study.");

		var skLeaveThroneRoomF1Again = new ObjectStep(this, 61577, new WorldPoint(2304, 7392, 1), "Enter Door.");

		var skLeaveDiningRoomF1Again = new ObjectStep(this, 61588, new WorldPoint(2342, 7373, 1), "Enter Gibbous moon door.");

		var skLeaveLobbyF1Again = new ObjectStep(this, 61599, new WorldPoint(2308, 7365, 1), "Climb-up Stairs.");

		var skEnterGibbousMoonDoor = new ObjectStep(this, 61588, new WorldPoint(2510, 7370, 0), "Enter Gibbous moon door.");

		var skEnterFullMoonDoor = new ObjectStep(this, 61590, new WorldPoint(2525, 7363, 0), "Enter Full moon door.");

		var inSolidKeyRoom = castleDrakanRoomTemporary.eq(40);

		var skUseShieldOnEmptyMount = new ObjectStep(this, 61698, new WorldPoint(2543, 7361, 0), "Use the shield with symbol on the empty mount on the southern wall.", shieldWithSymbol.highlighted());

		var hasMountedShield = new VarbitRequirement(15526, 1);

		var skGetSolidKey = new ObjectStep(this, 61698, new WorldPoint(2543, 7361, 0), "Search the mounted shield for the solid key.");
		skGetSolidKey.addDialogStep("Yes.");

		var solidKey = new ItemRequirement("Solid key", 33730);

		getSolidKey.addStep(and(inSolidKeyRoom, hasMountedShield), skGetSolidKey);
		getSolidKey.addStep(and(inSolidKeyRoom, shieldWithSymbol), skUseShieldOnEmptyMount);
		getSolidKey.addStep(and(inVenatorPuzzleroom, or(shieldWithSymbol, hasMountedShield)), skEnterFullMoonDoor);
		getSolidKey.addStep(and(inRoomOutsideGuestChamberStoreroom, shieldWithSymbol), skEnterGibbousMoonDoor);
		getSolidKey.addStep(and(inLobbyF1, shieldWithSymbol), skLeaveLobbyF1Again);
		getSolidKey.addStep(and(inDiningRoomF1, shieldWithSymbol), skLeaveDiningRoomF1Again);
		getSolidKey.addStep(and(inThroneRoomF1, shieldWithSymbol), skLeaveThroneRoomF1Again);
		getSolidKey.addStep(and(inRoomAboveStudy, shieldWithSymbol), skLeaveStudy);
		getSolidKey.addStep(and(ancientShield, ancientSymbol), skCombineAncientShieldAndAncientSymbol);
		getSolidKey.addStep(and(inRoomAboveStudy, ancientSymbol), skGetAncientShield);
		getSolidKey.addStep(and(inThroneRoomF1, ancientSymbol), skLeaveThroneRoomF1);
		getSolidKey.addStep(and(inDiningRoomF1, ancientSymbol), skLeaveDiningRoomF1);
		getSolidKey.addStep(and(inLobbyF1, ancientSymbol), skLeaveLobbyF1);
		getSolidKey.addStep(and(inHallwayEastOfExplosiveRoom, ancientSymbol), skLeaveExplosiveRoomHallway);
		getSolidKey.addStep(and(inExplosiveRoom, ancientSymbol), skLeaveExplosiveRoom);
		getSolidKey.addStep(and(inServantsQuarters, ancientSymbol), skLeaveServantsQuarters);
		getSolidKey.addStep(and(inChapelLibrary, ancientSymbol, openedPortalFromChapelLibraryToServantsQuarters), skEnterPortal);
		getSolidKey.addStep(and(inChapelLibrary, ancientSymbol), skPullLever);
		getSolidKey.addStep(and(inBottleRoom, ancientSymbol), skEnterEastDoorFromBottleRoom);
		getSolidKey.addStep(and(inBottleRoom, solvedAllBasins), skSearchAltarForAncientSymbol);
		getSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, solvedIceBasin, not(solvedShadowBasin), weightlessBlackPotion), shadowBasin);
		getSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, solvedIceBasin, not(solvedShadowBasin)), skGetWeightlessBlackPotion);
		getSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, not(solvedIceBasin), coldBlueishWhitePotion), iceBasin);
		getSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, solvedSmokeBasin, not(solvedIceBasin)), skGetColdBluishWhitePotion);
		getSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, not(solvedSmokeBasin), cloudyGreyPotion), smokeBasin);
		getSolidKey.addStep(and(inBottleRoom, solvedBloodBasin, not(solvedSmokeBasin)), skGetCloudyGreyPotion);
		getSolidKey.addStep(and(inBottleRoom, not(solvedBloodBasin), thickRedPotion, or(cloudyGreyPotion, solvedSmokeBasin), or(weightlessBlackPotion, solvedShadowBasin), or(coldBlueishWhitePotion, solvedIceBasin)), bloodBasin);
		getSolidKey.addStep(and(inBottleRoom, not(cloudyGreyPotion)), skGetCloudyGreyPotion);
		getSolidKey.addStep(and(inBottleRoom, not(weightlessBlackPotion)), skGetWeightlessBlackPotion);
		getSolidKey.addStep(and(inBottleRoom, not(thickRedPotion)), skGetThickRedPotion);
		getSolidKey.addStep(and(inBottleRoom, not(coldBlueishWhitePotion)), skGetColdBluishWhitePotion);

		getSolidKey.addStep(inWestChapelHallway, skEnterEastFullMoonDoor);
		getSolidKey.addStep(inNorthChapelHallway, skEnterWesternDoor);
		getSolidKey.addStep(inRanisHallway, skClimbDownStairs);
		getSolidKey.addStep(inVanesculasHallway, skEnterEasternDoor);
		getSolidKey.addStep(inHallwayNorthOfLobby, skEnterScratchedDoor);
		getSolidKey.addStep(inSmallHallway, skLeaveGildedDoor);
		getSolidKey.addStep(inSecretRoom, skLeaveEastDoor);

		// if you follow the guide, this is the last position you'll use the key
		var hasUsedNewMoonKey = new VarplayerRequirement(5638, true, 1);

		var dtsLeaveSolidKeyRoom = new ObjectStep(this, 61590, new WorldPoint(2541, 7374, 0), "Enter Full moon door.");
		var dtsLeavePuzzleRoom = new ObjectStep(this, 61589, new WorldPoint(2515, 7367, 0), "Enter Gibbous moon door.");
		var dtsLeaveDisplayCaseRoom = new ObjectStep(this, 61590, new WorldPoint(2510, 7379, 0), "Enter Full moon door.");

		var hasUsedFullMoonKey = new VarplayerRequirement(5638, true, 29);

		var inSolidDoorHallway = castleDrakanRoomTemporary.eq(45);

		var dtsOpenSolidDoor = new ObjectStep(this, 61575, new WorldPoint(2503, 7400, 0), "Enter Solid door.");

		var usedSolidKey =  new VarplayerRequirement(5639, true, 5);

		var inLaboratory = castleDrakanRoomTemporary.eq(42);


		var vialOfWater = new ItemRequirement("Vial of water", 33774);
		var vialsOfWater2 = vialOfWater.quantity(2);
		var vialOfBlood = new ItemRequirement("Vial of blood", 33775);
		var vialsOfBlood3 = vialOfBlood.quantity(3);
		var pureEssence = new ItemRequirement("Pure essence", 33776);
		var pureEssence3 = pureEssence.quantity(3);

		var dtsSearchShelvesForSupplies = new ObjectStep(this, 61708, new WorldPoint(2513, 7387, 0), "Search the shelves for 2 vials of water, 3 vials of blood, and 3 pure essence. You can destroy all keys, the tinderbox, and the pickaxe to make room in your inventory.", vialsOfWater2, vialsOfBlood3, pureEssence3);
		var dtsSearchShelvesForSuppliesWater = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesWater.addDialogStep("Take a vial of water.");
		var dtsSearchShelvesForSuppliesBlood = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesBlood.addDialogStep("Take a vial of blood.");
		var dtsSearchShelvesForSuppliesEssence = dtsSearchShelvesForSupplies.copy();
		dtsSearchShelvesForSuppliesEssence.addDialogStep("Take a piece of pure essence.");

		var refinerVb = new VarbitBuilder(VarbitID.CASTLE_DRAKAN_REFINER_ITEM_1);
		var refinerWater = refinerVb.eq(1);
		var refinerBlood = refinerVb.eq(2);
		var refinerEssence = refinerVb.eq(3);

		var chemicalVial = new ItemRequirement("Chemical vial", 33778);
		var chemicalVial2 = chemicalVial.quantity(2);
		var chemicalVial3 = chemicalVial.quantity(3);
		var chemicalVial4 = chemicalVial.quantity(4);

		var makeFirstVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", vialOfBlood.highlighted(), pureEssence.highlighted());
		var makeFirstVialEssence = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", pureEssence.highlighted());
		var makeFirstVialBlood = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the first vial.", vialOfBlood.highlighted());

		var makeSecondVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", vialOfBlood.highlighted(), pureEssence.highlighted());
		var makeSecondVialEssence = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", pureEssence.highlighted());
		var makeSecondVialBlood = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and pure essence on the refiner to make the second vial.", vialOfBlood.highlighted());

		var makeThirdVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfBlood.highlighted(), vialOfWater.highlighted());
		var makeThirdVialBlood = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfBlood.highlighted());
		var makeThirdVialWater = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a vial of blood and vial of water on the refiner to make the third vial.", vialOfWater.highlighted());

		var makeFourthVial = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", pureEssence.highlighted(), vialOfWater.highlighted());
		var makeFourthVialEssence = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", pureEssence.highlighted());
		var makeFourthVialWater = new ObjectStep(this, 61709, new WorldPoint(2520, 7385, 0), "Combine a pure essence and vial of water on the refiner to make the fourth vial.", vialOfWater.highlighted());

		var pourAllVialsIntoTheBasin = new ObjectStep(this, 61707, new WorldPoint(2518, 7389, 0), "Pour all the chemical vials into the basin", chemicalVial.highlighted());

		var hasPouredAnything = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LAB_BASIN, 0, Operation.GREATER);

		var dtsLaboratoryStep = new ConditionalStep(this, dtsSearchShelvesForSupplies, "\nFill the basin to the north until it reads 53. If something has gone wrong, overfill the basin and start over.");
		dtsLaboratoryStep.addStep(and(chemicalVial, hasPouredAnything), pourAllVialsIntoTheBasin);
		dtsLaboratoryStep.addStep(and(chemicalVial4), pourAllVialsIntoTheBasin);
		dtsLaboratoryStep.addStep(and(chemicalVial3, refinerWater), makeFourthVialEssence);
		dtsLaboratoryStep.addStep(and(chemicalVial3, refinerEssence), makeFourthVialWater);
		dtsLaboratoryStep.addStep(and(chemicalVial3), makeFourthVial);
		dtsLaboratoryStep.addStep(and(chemicalVial2, refinerWater), makeThirdVialBlood);
		dtsLaboratoryStep.addStep(and(chemicalVial2, refinerBlood), makeThirdVialWater);
		dtsLaboratoryStep.addStep(and(chemicalVial2), makeThirdVial);
		dtsLaboratoryStep.addStep(and(chemicalVial, refinerBlood), makeSecondVialEssence);
		dtsLaboratoryStep.addStep(and(chemicalVial, refinerEssence), makeSecondVialBlood);
		dtsLaboratoryStep.addStep(and(chemicalVial), makeSecondVial);
		dtsLaboratoryStep.addStep(and(refinerBlood), makeFirstVialEssence);
		dtsLaboratoryStep.addStep(and(refinerEssence), makeFirstVialBlood);
		dtsLaboratoryStep.addStep(and(vialsOfWater2, vialsOfBlood3, pureEssence3), makeFirstVial);
		dtsLaboratoryStep.addStep(not(vialsOfWater2), dtsSearchShelvesForSuppliesWater);
		dtsLaboratoryStep.addStep(not(vialsOfBlood3), dtsSearchShelvesForSuppliesBlood);
		dtsLaboratoryStep.addStep(not(pureEssence3), dtsSearchShelvesForSuppliesEssence);

		var destroyingTheStockpile = new ConditionalStep(this, todo4, "\nFind and destroy the stockpile.");

		var finishedLabPuzzle = new VarbitRequirement(VarbitID.CASTLE_DRAKAN_PUZZLE_LAB_BASIN, 53);

		var dtsLeaveLaboratory = new ObjectStep(this, 61572, new WorldPoint(2523, 7389, 0), "Enter Door.");

		var dtsDestroyBloodStockpile = new ObjectStep(this, 61711, new WorldPoint(2535, 7385, 0), "Destroy the blood stockpile.");

		var inLaboratoryStorageRoom = castleDrakanRoomTemporary.eq(43);

		destroyingTheStockpile.addStep(inLaboratoryStorageRoom, dtsDestroyBloodStockpile);
		destroyingTheStockpile.addStep(and(inLaboratory, finishedLabPuzzle), dtsLeaveLaboratory);
		destroyingTheStockpile.addStep(inLaboratory, dtsLaboratoryStep);
		destroyingTheStockpile.addStep(inSolidDoorHallway, dtsOpenSolidDoor);
		destroyingTheStockpile.addStep(inRoomOutsideGuestChamberStoreroom, dtsLeaveDisplayCaseRoom);
		destroyingTheStockpile.addStep(inVenatorPuzzleroom, dtsLeavePuzzleRoom);
		destroyingTheStockpile.addStep(inSolidKeyRoom, dtsLeaveSolidKeyRoom);

		cVampyriumCastleDrakan.addStep(and(or(solidKey, usedSolidKey), or(fullMoonKey, hasUsedFullMoonKey)), destroyingTheStockpile);

		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, doneWithThroneRoomPuzzle, not(halfMoonKey)), getKeyFromThroneRoom);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, not(doneWithThroneRoomPuzzle)), cGetHalfMoonKey);

		cVampyriumCastleDrakan.addStep(and(or(newMoonKey, hasUsedNewMoonKey), gibbousMoonKey, fullMoonKey), getSolidKey);
		cVampyriumCastleDrakan.addStep(and(crescentMoonKey, newMoonKey, or(gildedKey, hasUsedGildedKey), gibbousMoonKey), getFullMoonKey);
		cVampyriumCastleDrakan.addStep(and(crescentMoonKey, newMoonKey), getGildedAndGibbousKeys);
		cVampyriumCastleDrakan.addStep(crescentMoonKey, getNewMoonKey);
		cVampyriumCastleDrakan.addStep(hasGottenDrakanEmblemFromFireplace, getCrescentMoonKey);

		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, not(smallClockHand), smallClockHandNeedsReplacing), getSmallClockHand);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, not(largeClockHand), largeClockHandNeedsReplacing), getLargeClockHand);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, needToFinishClockPuzzle, not(inDiningRoom)), getBackToDiningRoom);
		cVampyriumCastleDrakan.addStep(and(inVampyriumVarbit, needToFinishClockPuzzle), solveClockPuzzle);

		// TODO: do we need to prompt the user to pick up the poem scroll?

		cVampyriumCastleDrakan.addStep(inVampyriumVarbit, todoVampyriumPuzzle);

		steps.put(72, cVampyriumCastleDrakan);
		// 72 -> 74 after talking to Veliaf in the emblem gallery
		steps.put(74, cVampyriumCastleDrakan);
		// 74 -> 76 after freeing Safalaan and Vanescula
		steps.put(76, cVampyriumCastleDrakan);
		// 74 -> 76 after talking to Ivan
		steps.put(78, cVampyriumCastleDrakan);


		var inCutscene = new VarbitRequirement(VarbitID.CUTSCENE_STATUS, 1);
		var tryToLeave = new ObjectStep(this, 61221, new WorldPoint(2323, 7370, 0), "");
		var cVampyriumCastleDrakanDestroyedBloodStockpile = new ConditionalStep(this, tryToLeave, "Try to leave Castle Drakan through the portal.");
		var vampyriumCastleDrakanDestroyedBloodStockpile = new DetailedQuestStep(this, "Watch the cutscene.");
		cVampyriumCastleDrakanDestroyedBloodStockpile.addStep(inCutscene, vampyriumCastleDrakanDestroyedBloodStockpile);
		steps.put(80, cVampyriumCastleDrakanDestroyedBloodStockpile);

		var leaveCastleDrakan = new ObjectStep(this, 61595, new WorldPoint(2332, 7370, 0), "Leave Castle Drakan.");

		var watchLeaveCastleDrakanCutscene = new DetailedQuestStep(this, "Watch the cutscene.");
		var cLeaveCastleDrakan = new ConditionalStep(this, leaveCastleDrakan);
		cLeaveCastleDrakan.addStep(inCutscene, watchLeaveCastleDrakanCutscene);
		steps.put(82, cLeaveCastleDrakan);

		var hasDeathPos = new VarplayerRequirement(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS, 0, Operation.GREATER_EQUAL);

		var crankWheel = new ItemRequirement("Crank wheel", 33788);
		// TODO: I changed this _after_ I tested it.
		var crankedTheWheel = new VarbitRequirement(VarbitID.SANGVESTI_FANCY_HOUSE_3_DOOR, 1);
		var crankedTheWheelInTheBank = new VarbitRequirement(VarbitID.SANGVESTI_INTERIOR_BANK_DOOR, 1);
		var needCrankWheel = nand(crankedTheWheel, crankedTheWheelInTheBank);
		var crankWheelForBank = crankWheel.hideConditioned(crankedTheWheelInTheBank);

		pickupCrankWheel = new DetailedQuestStep(this, new WorldPoint(2603, 7847, 0), "Pick up the crank wheel from the house east of the altar, north-west of where you started after the cutscene.", crankWheel);
		pickupCrankWheelFromWhereYouDied = new DetailedQuestStep(this, "Pick up the crank wheel from where you died lol!", crankWheel);
		pickupCrankWheelFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);
		var crankWheel1 = new ObjectStep(this, 61852, new WorldPoint(2588, 7855, 0), "Spam click the crank base. Drakan will start chasing you - feel free to run away if he gets too close.", crankWheel);
		var enterHouseNextToCrankWheel = new ObjectStep(this, 61844, new WorldPoint(2587, 7857, 0), "Enter the door north of the crank base.");


		var jovkaiKey = new ItemRequirement("Jovkai key", 33780);
		var unlockedSmith = new VarbitRequirement(VarbitID.SANGVESTI_BLACKSMITH_DOOR, 1);
		jovkaiKey.setConditionToHide(unlockedSmith);
		var needJovkaiKey = nand(unlockedSmith);
		var jovkaiKeyInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_CHAPEL, 1);

		var pickupJovkaiKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the jovkai key from where you died lol!", jovkaiKey);
		pickupJovkaiKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		var houseNorthOfCrankBase = new Zone(new WorldPoint(2589, 7864, 0), new WorldPoint(2585, 7857, 0));
		var inHouseNorthOfCrankBase = new ZoneRequirement(houseNorthOfCrankBase);
		var searchBookCase = new ObjectStep(this, 61818, new WorldPoint(2585, 7861, 0), "Search the bookshelf for a dusty book.");
		var dustyBook = new ItemRequirement("Dusty book", 33789);
		var readDustyBook = new DetailedQuestStep(this, "Read the dusty book and retrieve the vitur key.", dustyBook.highlighted());

		var viturKey = new ItemRequirement("Vitur key", 33785);

		// Important vitur key use:
		var unlockedBoltCutterHouse = new VarbitRequirement(VarbitID.SANGVESTI_VITUR_MANOR_DOOR, 1);
		var viturKeyForBoltCutterHouse = viturKey.hideConditioned(unlockedBoltCutterHouse);
		var viturKeyOrUnlockedBoltCutterHouse = or(viturKey, unlockedBoltCutterHouse);

		// Important vitur key use:
		var unlockedToothHalfOfKeyHouse = new VarbitRequirement(VarbitID.SANGVESTI_FOOD_SHOP_DOOR, 1);
		var viturKeyForToothHalfOfKeyHouse = viturKey.hideConditioned(unlockedToothHalfOfKeyHouse);
		var viturKeyOrUnlockedToothHalfOfKeyHouse = or(viturKey, unlockedToothHalfOfKeyHouse);

		var needViturKey = nand(unlockedBoltCutterHouse, unlockedToothHalfOfKeyHouse);

		var leaveBookcaseHouse = new ObjectStep(this, 61844, new WorldPoint(2587, 7857, 0), "Leave the house and enter the vitur manor to the east.", viturKeyForBoltCutterHouse);
		var openViturDoorEastOfBookcaseHouse = new ObjectStep(this, 61834, new WorldPoint(2612, 7853, 0), "Enter the vitur manor to the east.", viturKeyForBoltCutterHouse);

		var needBoltCutters = new VarbitRequirement(VarbitID.SANGVESTI_CLOTHES_SHOP_DOOR, 0);
		var hasUsedBoltCutters = new VarbitRequirement(VarbitID.SANGVESTI_CLOTHES_SHOP_DOOR, 1);
		var boltCutters = new ItemRequirement("Bolt cutters", 33787);
		boltCutters.setConditionToHide(hasUsedBoltCutters);

		var oldCog = new ItemRequirement("Old cog", 33779);

		var boltCutterHouse1 = new Zone(new WorldPoint(2609, 7860, 0), new WorldPoint(2615, 7856, 0));
		var boltCutterHouse2 = new Zone(new WorldPoint(2612, 7855, 0), new WorldPoint(2619, 7850, 0));
		var boltCutterHouse3 = new Zone(new WorldPoint(2621, 7859, 0), new WorldPoint(2616, 7855, 0));
		var inBoltCutterHouse = new ZoneRequirement(boltCutterHouse1, boltCutterHouse2, boltCutterHouse3);
		var searchShedBoltCutter = new ObjectStep(this, 61858, new WorldPoint(2616, 7855, 0), "Search the shed in the north-eastern room for a pair of bolt cutters.");
		var boltCutterShedCombinationLock = new ChestCodeStep(this, "shed", "TOOLS", 10, 8, 2, 9, 1, 2);
		var leaveBoltCutterHouse = new ObjectStep(this, 61835, new WorldPoint(2612, 7852, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", boltCutters, viturKeyForToothHalfOfKeyHouse);
		var enterToothHalfOfKeyHouse = new ObjectStep(this, 61838, new WorldPoint(2598, 7843, 0), "Enter the building east of the altar and pick up the tooth half of key from the table.", viturKeyForToothHalfOfKeyHouse);

		var unlockedTrapdoor = new VarbitRequirement(VarbitID.SANGVESTI_TRAPDOOR, 1);

		var trapdoorKey = new ItemRequirement("Trapdoor key", 33786);
		trapdoorKey.setConditionToHide(unlockedTrapdoor);

		// TODO: This should potentially have a "get back to vampyrium" step
		var cCog = new ConditionalStep(this, todo2, "Find the old cog for the drawbridge.");

		var pickupViturKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the vitur key from where you died lol!", viturKey);
		pickupViturKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		var pickupBoltCuttersFromWhereYouDied = new DetailedQuestStep(this, "Pick up the bolt cutters from where you died lol!", boltCutters);
		pickupBoltCuttersFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		var pickupOldCogFromWhereYouDied = new DetailedQuestStep(this, "Pick up the old cog from where you died lol!", oldCog);
		pickupOldCogFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		var pickupTrapdoorKeyFromWhereYouDied = new DetailedQuestStep(this, "Pick up the trapdoor key from where you died lol!", trapdoorKey);
		pickupTrapdoorKeyFromWhereYouDied.setWorldPointVarp(VarPlayerID.SANGVESTI_PLAYER_LAST_DEATH_POS);

		var toothHalfOfKeyHouse = new Zone(new WorldPoint(2596, 7849, 0), new WorldPoint(2600, 7843, 0));
		var inToothHalfOfKeyHouse = new ZoneRequirement(toothHalfOfKeyHouse);

		var toothHalfOfKey = new ItemRequirement("Tooth half of key", 33782);
		var pickupToothHalfOfKey = new DetailedQuestStep(this, new WorldPoint(2600, 7845, 0), "Pick up the tooth half of key from the table.", toothHalfOfKey);

		var loopHalfOfKey = new ItemRequirement("Loop half of key", 33781);
		var pickupLoopHalfOfKey = new DetailedQuestStep(this, new WorldPoint(2588, 7816, 0), "Pick up the loop half of key", loopHalfOfKey);

		var openChainedDoor = new ObjectStep(this, 61840, new WorldPoint(2590, 7818, 0), "Open the chained door south of the well.", boltCutters, toothHalfOfKey);
		var clothery = new Zone(new WorldPoint(2592, 7812, 0), new WorldPoint(2588, 7818, 0));
		var inClothery = new ZoneRequirement(clothery);

		var isCrankWheelInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_BASIC_HOUSE_3, 1);

		var needTrapdoorKey = nand(unlockedTrapdoor);

		var pickupOldCog = new DetailedQuestStep(this, new WorldPoint(2582, 7817, 0), "Pick up the old cog from the table in the smithy.", oldCog);

		var oldCogInOriginalPosition = new VarbitRequirement(VarbitID.SANGVESTI_HINT_BLACKSMITH, 1);

		cCog.addStep(and(hasDeathPos, not(oldCogInOriginalPosition), not(oldCog)), pickupOldCogFromWhereYouDied);
		cCog.addStep(and(hasDeathPos, needBoltCutters, not(boltCutters)), pickupBoltCuttersFromWhereYouDied);
		cCog.addStep(and(hasDeathPos, needViturKey, not(viturKey)), pickupViturKeyFromWhereYouDied);
		cCog.addStep(and(hasDeathPos, not(isCrankWheelInOriginalPosition), needCrankWheel, not(crankWheel)), pickupCrankWheelFromWhereYouDied);
		cCog.addStep(and(needCrankWheel, not(crankWheel)), pickupCrankWheel);
		cCog.addStep(and(hasDeathPos, not(jovkaiKeyInOriginalPosition), needJovkaiKey), pickupJovkaiKeyFromWhereYouDied);

		var makeMyrmelKey = new DetailedQuestStep(this, "Combine the loop and tooth half of key into the myrmel key.", loopHalfOfKey.highlighted(), toothHalfOfKey.highlighted());
		var myrmelKey = new ItemRequirement("Myrmel key", 33783);

		var unlockedBucketHouse = new VarbitRequirement(VarbitID.SANGVESTI_PUB_DOOR, 1);
		var myrmelKeyForBucketHouse = myrmelKey.hideConditioned(unlockedBucketHouse);

		var unlockedBank = new VarbitRequirement(VarbitID.SANGVESTI_BANK_DOOR, 1);
		var myrmelKeyForBank = myrmelKey.hideConditioned(unlockedBank);

		var needMyrmelKey = nand(unlockedBucketHouse, unlockedBank);

		var bucketOfWater = new ItemRequirement("Bucket of water", 1929);

		var bucketHouse1 = new Zone(new WorldPoint(2592, 7834, 0), new WorldPoint(2606, 7830, 0));
		var bucketHouse2 = new Zone(new WorldPoint(2596, 7834, 0), new WorldPoint(2602, 7826, 0));
		var inBucketHouse = new ZoneRequirement(bucketHouse1, bucketHouse2);
		var bucket = new ItemRequirement("Bucket", 1925);
		var enterBucketHouse = new ObjectStep(this, 61825, new WorldPoint(2599, 7826, 0), "Open Myrmel door.", myrmelKeyForBucketHouse);
		var enterBucketHouseAgain = new ObjectStep(this, 61825, new WorldPoint(2599, 7826, 0), "Enter the pub with the bucket of water.", bucketOfWater);

		var pickupBucket = new DetailedQuestStep(this, new WorldPoint(2596, 7833, 0), "Pick up the bucket.", bucket);
		var useBucketOnWell = new ObjectStep(this, 60892, new WorldPoint(2592, 7825, 0), "Use the bucket on the well outside the house.", bucket.highlighted());
		var useBucketOfWaterOnNorthernBarrel = new ObjectStep(this, 61811, new WorldPoint(2592, 7833, 0), "Use the bucket of water on the northern barrel", bucketOfWater.highlighted());

		var shadumKey = new ItemRequirement("Shadum key", 33784);
		var unlockedTrapdoorHouse =  new VarbitRequirement(VarbitID.SANGVESTI_FANCY_HOUSE_1_DOOR, 1);
		var shadumKeyForTrapdoorHouse = shadumKey.hideConditioned(unlockedTrapdoorHouse);

		var enterShadumDoor = new ObjectStep(this, 61832, new WorldPoint(2614, 7814, 0), "Enter the shadum door south-west of the drawbridge and pick up the trapdoor key.", shadumKeyForTrapdoorHouse);
		var pickupTrapdoorKey = new DetailedQuestStep(this, new WorldPoint(2619, 7812, 0), "Pick up the trapdoor key.", trapdoorKey);

		// TODO
		var needShadumKey = nand(unlockedTrapdoorHouse);

		var trapdoorHouse = new Zone(new WorldPoint(2620, 7816, 0), new WorldPoint(2614, 7812, 0));
		var inTrapdoorHouse = new ZoneRequirement(trapdoorHouse);

		var bank = new Zone(new WorldPoint(2582, 7835, 0), new WorldPoint(2578, 7829, 0));
		var inBank = new ZoneRequirement(bank);

		var enterBank = new ObjectStep(this, 61829, new WorldPoint(2580, 7835, 0), "Enter the bank.", myrmelKeyForBank, trapdoorKey, crankWheelForBank);

		var operateBankCrank = new ObjectStep(this, 61853, new WorldPoint(2582, 7834, 0), "Spam click the crank base.", trapdoorKey, crankWheelForBank);

		var enterTrapdoor = new ObjectStep(this, 61848, new WorldPoint(2579, 7830, 0), "Enter the trapdoor.", trapdoorKey);

		var altarHouse1 = new Zone(new WorldPoint(2575, 7850, 0), new WorldPoint(2578, 7846, 0));
		var altarHouse2 = new Zone(new WorldPoint(2579, 7850, 0), new WorldPoint(2583, 7842, 0));
		var altarHouse3 = new Zone(new WorldPoint(2584, 7850, 0), new WorldPoint(2592, 7844, 0));
		var inAltarHouse = new ZoneRequirement(altarHouse1, altarHouse2, altarHouse3);

		var searchAltarChest = new ObjectStep(this, 61807, new WorldPoint(2576, 7846, 0), "Search the chest in the western room of the altar house.");
		var enterAltarHouseThroughDoor = new ObjectStep(this, 61847, new WorldPoint(2581, 7842, 0), "Search the chest in the western room of the altar house.");

		var solveAltarChestLock = new ChestCodeStep(this, "altar chest", "35158", 10, 3, 5, 1, 5, 8);

		var leaveAltarThroughDoor = new ObjectStep(this, 61847, new WorldPoint(2581, 7842, 0), "Leave the altar room, then head to the smithy to the south.", jovkaiKey);
		var enterSmith = new ObjectStep(this, 61821, new WorldPoint(2580, 7812, 0), "Enter the smithy to the south.", jovkaiKey);

		var smithy = new Zone(new WorldPoint(2578, 7818, 0), new WorldPoint(2582, 7812, 0));
		var inSmithy = new ZoneRequirement(smithy);

		var returnToVanescula = new NpcStep(this, 15909, new WorldPoint(2633, 7830, 0), "Return to Vanescula by the drawbridge to the south-east. Drakan will chase you when you leave the smithy.", oldCog);

		var foundTheCog = new VarbitRequirement(VarbitID.MYQ6_VANESCULA_DRAKAN_FOUND_THE_COG, 1);

		var talkToIvanForSupplies = new NpcStep(this, 15856, new WorldPoint(2636, 7825, 0), "Talk-to Ivan Strom for some supplies. You can drop the bucket.");
		talkToIvanForSupplies.addDialogStep("Could I have those supplies you found?");

		var xd2 = new VarbitRequirement(VarbitID.MYQ6_SANGVESTI_SUPPLY_WARNING, 0);

		var needToTalkToIvanForSupplies = and(foundTheCog, xd2);

		var returnToVanesculaReadyToLeave = new NpcStep(this, 15909, new WorldPoint(2633, 7830, 0), "Return to Vanescula by the drawbridge to the south-east when you're ready to leave. Drakan will chase you through the woods. Dodge his attacks, protect from melee.", oldCog);

		// oldCog.setConditionToHide(foundTheCog);

		// cCog.addStep(foundTheCog, returnToVanescula);

		cCog.addStep(and(oldCog, not(needToTalkToIvanForSupplies)), returnToVanesculaReadyToLeave);
		cCog.addStep(and(oldCog, needToTalkToIvanForSupplies), talkToIvanForSupplies);
		cCog.addStep(and(oldCog), returnToVanescula);

		var unlockedAltarHouse = new VarbitRequirement(VarbitID.SANGVESTI_CHAPEL_BACK_DOOR_2, 1);
		cCog.addStep(and(not(oldCog), inSmithy), pickupOldCog);
		cCog.addStep(and(not(oldCog), inAltarHouse, or(jovkaiKey, unlockedSmith), not(unlockedAltarHouse)), leaveAltarThroughDoor);
		cCog.addStep(and(not(oldCog), or(jovkaiKey, unlockedSmith)), enterSmith);

		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, inAltarHouse, not(jovkaiKey), unlockedAltarHouse), searchAltarChest);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, not(jovkaiKey), unlockedAltarHouse), enterAltarHouseThroughDoor);
		cCog.addStep(and(jovkaiKeyInOriginalPosition, needJovkaiKey, inAltarHouse, combinationLockWidgetOpen), solveAltarChestLock);
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
		cCog.addStep(and(needMyrmelKey, viturKeyOrUnlockedToothHalfOfKeyHouse, or(boltCutters, hasUsedBoltCutters), not(myrmelKey)), enterToothHalfOfKeyHouse);
		cCog.addStep(and(needMyrmelKey, inBoltCutterHouse, or(boltCutters, hasUsedBoltCutters)), leaveBoltCutterHouse);
		cCog.addStep(and(needMyrmelKey, inBoltCutterHouse, combinationLockWidgetOpen), boltCutterShedCombinationLock);
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
		var watchTheCutscene = new DetailedQuestStep(this, "Watch the cutscene.");

		var repairedBridge = new ConditionalStep(this, todo3, "you have repaired the bridge!");
		repairedBridge.addStep(inCutscene, watchTheCutscene);

		steps.put(88, repairedBridge);

		// TODO: add doge instructions
		var fightDrakan1 = new NpcStep(this, 16211, new WorldPoint(2711, 7847, 0), "Attack Lowerniel Drakan  (level-927).");

		var resupplyZone = new Zone(new WorldPoint(2853, 7640, 0), new WorldPoint(2837, 7655, 0));
		var resupplyZone2 = new Zone(new WorldPoint(2950, 7831, 0), new WorldPoint(2973, 7813, 0));
		var inResupplyZone = new ZoneRequirement(resupplyZone, resupplyZone2);

		steps.put(90, fightDrakan1);

		var resupplyIfNeeded = new ObjectStep(this, 61941, new WorldPoint(2843, 7646, 0), "Resupply at the shelter remains in the middle. When you've resupplied, click the trees around you to continue.");
		var resupplyIfNeeded2 = new ObjectStep(this, 61047, new WorldPoint(2951, 7821, 0), "Resupply at the shelter remains in the middle. When you've resupplied, click the trees to continue.");
		// resupplyIfNeeded2.addAlternateObjects(61047);

		var cFlee1 = new ConditionalStep(this, todo3);
		cFlee1.addStep(inCutscene, watchTheCutscene);
		cFlee1.addStep(inResupplyZone, resupplyIfNeeded);
		steps.put(92, cFlee1);

		var inSotfa1 = new ZoneRequirement(new Zone(new WorldPoint(2950, 7846, 0), new WorldPoint(2971, 7867, 0)));
		// TODO: instruct on their swapping
		var anyNearbyFeralVyres1 = new NpcCondition(16229);
		var anyNearbyFeralVyres2 = new NpcCondition(16230);
		var anyNearbyFeralVyres3 = new NpcCondition(16231);
		var anyNearbyFeralVyres4 = new NpcCondition(16232);
		var anyNearbyFeralVyres = or( anyNearbyFeralVyres1, anyNearbyFeralVyres2, anyNearbyFeralVyres3, anyNearbyFeralVyres4 );
		var sotfa1 = new NpcStep(this, new int[]{16229, 16230, 16231, 16232}, new WorldPoint(2961, 7851, 0), "Kill the ancient feral vyres. They swap positions and heal, so try to focus one at a time.", true);
		var sotfa1Done = new ObjectStep(this, 61047, new WorldPoint(2960, 7864, 0), "Enter Darkwood trees to continue.");

		var inSotfa2 = new ZoneRequirement(new Zone(new WorldPoint(2997, 7927, 0), new WorldPoint(2953, 7876, 0)));

		var anyAxe = new ItemRequirement("Any axe", ItemCollections.AXES);

		var takeAxe = new ObjectStep(this, 61961, new WorldPoint(2960, 7890, 0), "Take-axe Stump.");

		// [2026-07-12T13:54:14Z 14140] varbit SOTFA_FOREST_ENCOUNTER_COMPLETED (15605) 0 -> 1 = feral vyres

		// TODO: Mention that if your inventory is full, you won't get ticks in your inventory.
		// If you get ticks in your inventory, you have to throw them away to stop taking damage.
		var ts1Zone = new ZoneRequirement(new Zone(new WorldPoint(2955, 7896, 0), new WorldPoint(2968, 7879, 0)));
		var ts1 = new TreeSolver1(this);
		var ts2Zone = new ZoneRequirement(new Zone(new WorldPoint(2968, 7900, 0), new WorldPoint(2965, 7897, 0)));
		var ts2 = new TreeSolver2(this);
		var ts3Zone = new ZoneRequirement(new Zone(new WorldPoint(2965, 7900, 0), new WorldPoint(2960, 7903, 0)), new Zone(new WorldPoint(2966, 7903, 0), new WorldPoint(2977, 7901, 0)), new Zone(new WorldPoint(2976, 7900, 0), new WorldPoint(2970, 7897, 0)));
		var ts3 = new TreeSolver3(this);
		var ts4Zone = new ZoneRequirement(new Zone(new WorldPoint(2965, 7904, 0), new WorldPoint(2973, 7911, 0)), new Zone(new WorldPoint(2974, 7907, 0), new WorldPoint(2997, 7904, 0)), new Zone(new WorldPoint(2980, 7903, 0), new WorldPoint(2994, 7886, 0)), new Zone(new WorldPoint(2969, 7893, 0), new WorldPoint(2983, 7884, 0)));
		var ts4 = new TreeSolver4(this);
		var ts5Zone = new ZoneRequirement(new Zone(new WorldPoint(2975, 7908, 0), new WorldPoint(2980, 7910, 0)));
		var ts5 = new TreeSolver5(this);
		var ts6Zone = new ZoneRequirement(new Zone(new WorldPoint(2981, 7909, 0), new WorldPoint(2982, 7912, 0)), new Zone(new WorldPoint(2981, 7912, 0), new WorldPoint(2980, 7912, 0)), new Zone(new WorldPoint(2978, 7911, 0), new WorldPoint(2981, 7915, 0)));
		var ts6 = new TreeSolver6(this);
		var ts7ZoneSouth = new ZoneRequirement(new WorldPoint(2982, 7914, 0));
		var ts7ZoneWest = new ZoneRequirement(new Zone(new WorldPoint(2981, 7916, 0), new WorldPoint(2981, 7915, 0)));
		var ts8ZoneSouth = new ZoneRequirement(new Zone(new WorldPoint(2982, 7916, 0), new WorldPoint(2984, 7916, 0)));
		var ts8ZoneWest = new ZoneRequirement(new Zone(new WorldPoint(2982, 7917, 0), new WorldPoint(2983, 7917, 0)), new Zone(new WorldPoint(2982, 7915, 0)));
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

		var sotfa2 = new ConditionalStep(this, tsFinalStep, "Make your way through the tangle of trees to the darkwood trees in the north-east, chopping down and climbing over trees on the way. Trees can only be chopped once per direction.");
		sotfa2.addStep(not(anyAxe), takeAxe);

		var tree6ChoppedDown = new ObjectCondition(61954, new WorldPoint(2980, 7913, 0));
		var tree7Untouched = new ObjectCondition(61955, new WorldPoint(2982, 7915, 0));
		var tree7SlightlyChopped = new ObjectCondition(61956, new WorldPoint(2982, 7915, 0));
		var tree7Chopped = new ObjectCondition(61957, new WorldPoint(2982, 7915, 0));
		var tree8Untouched = new ObjectCondition(61955, new WorldPoint(2984, 7917, 0));
		var tree8SlightlyChopped = new ObjectCondition(61956, new WorldPoint(2984, 7917, 0));
		var tree8Chopped = new ObjectCondition(61957, new WorldPoint(2984, 7917, 0));

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

		var inSotfa3 = new ZoneRequirement(new Zone(new WorldPoint(2899, 7924, 0), new WorldPoint(2932, 7886, 0)));
		// NOTE: This is a _dangerous_ step. Hardcore ironmen should be careful. If we add an indicator for dangerous steps in the future, this one should be marked.
		var avoidAnimals = new NpcStep(this, 16233, "Turn off run. Avoid the Maxilla beasts. Running nearby, walking within 2 tiles, or standing on the maxilla beast will cause them to attack you and probably kill you.\nThe first and third follow predetermined paths, while the second one moves randomly. Be extra careful when passing it.", true);
		// avoidAnimals.addTileMarkers(SpriteID.LOAD, new WorldPoint(2914, 7920, 0));
		var sotfa3 = new ConditionalStep(this, avoidAnimals, "sotfa3");
		var nearSotfa3Exit = new ZoneRequirement(new Zone(new WorldPoint(2924, 7895, 0), new WorldPoint(2929, 7889, 0)));
		var leaveSotfa3 = new ObjectStep(this, 61992, new WorldPoint(2928, 7892, 0), "Turn off run. Avoid the Maxilla beasts. Running nearby, walking within 2 tiles, or standing on the maxilla beast will cause them to attack you and probably kill you.\nThe first and third follow predetermined paths, while the second one moves randomly. Be extra careful when passing it.");
		sotfa3.addStep(nearSotfa3Exit, leaveSotfa3);

		var inSotfa4 = new ZoneRequirement(new Zone(new WorldPoint(2986, 7844, 0), new WorldPoint(3006, 7867, 0)));
		var sotfa4 = new ObjectStep(this, 61995, new WorldPoint(2993, 7866, 0), "Continue through the cave.");

		// nylocas room
		var inSotfa5 = new ZoneRequirement(new Zone(new WorldPoint(2903, 7829, 0), new WorldPoint(2927, 7855, 0)));
		var anyNearbyNylocas = or(new NpcRequirement(16236), new NpcRequirement(16237));
		var killNylocas = new NpcStep(this, 16236, "Kill the nylocas. Gray ones with melee, yellow ones with a ranged weapon. You can pick up the Spine near the Venator corpse to use as darts. If you do not kill them fast enough, they explode dealing 15 damage.", true);
		killNylocas.addAlternateNpcs(16237);
		killNylocas.addCustomIcon(new QuestStepIcon(ItemID.DRAGON_SCIMITAR, 16236, 0.75));
		killNylocas.addCustomIcon(new QuestStepIcon(ItemID.SOTFA_FOREST_TALON, 16237, 0.75));
		// TODO: For some reason, the npc condition failed to detect more npcs at some point. Not sure what happened, but a few ticks later the npcs were highlighted.
		var sotfa5 = new ConditionalStep(this, killNylocas);
		var sotfa5Leave = new ObjectStep(this, 61047, new WorldPoint(2912, 7852, 0), "Kill the nylocas. Gray ones with melee, yellow ones with a ranged weapon. You can pick up the Spine near the Venator corpse to use as darts. If you do not kill them fast enough, they explode dealing 15 damage. When all are dead, leave through the trees.");
		sotfa5.addStep(not(anyNearbyNylocas), sotfa5Leave);

		var deadSnake3 = new ItemRequirement("Dead blood serpent", 33791, 3);

		var inSotfa6 = new ZoneRequirement(new Zone(new WorldPoint(3018, 7926, 0), new WorldPoint(3055, 7884, 0)));
		var wrangleSnakes = new NpcStep(this, 16238, "Wrangle the snakes, then combine them into one long snake. You must stand behind the snake when attempting to wrangle it.", true, deadSnake3);
		var sotfa6 = new ConditionalStep(this, wrangleSnakes, "sotfa6 xd");
		var combineSnakes = new DetailedQuestStep(this, "Combine the snakes into one long snake.", deadSnake3.highlighted());
		var serpentRope = new ItemRequirement("Serpent rope", 33792);
		var useRopeOnLongBranchedTree = new ObjectStep(this, 61951, new WorldPoint(3042, 7895, 0), "Use the serpent rope on the long branched tree.", serpentRope.highlighted());
		var ropedTree = new ObjectCondition(61949, new WorldPoint(3040, 7895, 0));
		var swingLikeTarzan = new ObjectStep(this, 61949, new WorldPoint(3040, 7894, 0), "Swing-across Long branched tree.");
		var acrossSotfa6Pond = new ZoneRequirement(new Zone(new WorldPoint(3047, 7892, 0), new WorldPoint(3033, 7885, 0)));
		var leaveSotfa6 = new ObjectStep(this, 61047, new WorldPoint(3041, 7885, 0), "Enter Darkwood trees.");
		sotfa6.addStep(acrossSotfa6Pond, leaveSotfa6);
		sotfa6.addStep(ropedTree, swingLikeTarzan);
		sotfa6.addStep(serpentRope, useRopeOnLongBranchedTree);
		sotfa6.addStep(deadSnake3, combineSnakes);


		var cFlee2 = new ConditionalStep(this, todo3);
		cFlee2.addStep(and(inSotfa6), sotfa6);
		cFlee2.addStep(and(inSotfa5), sotfa5);
		cFlee2.addStep(and(inSotfa4), sotfa4);
		cFlee2.addStep(and(inSotfa3), sotfa3);
		cFlee2.addStep(and(inSotfa2), sotfa2);
		cFlee2.addStep(and(inSotfa1, anyNearbyFeralVyres), sotfa1);
		cFlee2.addStep(and(inSotfa1), sotfa1Done);
		cFlee2.addStep(inCutscene, watchTheCutscene);
		cFlee2.addStep(inResupplyZone, resupplyIfNeeded2);
		steps.put(94, cFlee2);

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
			new SkillRequirement(Skill.SLAYER, 74, false /* ?? */),
			new SkillRequirement(Skill.WOODCUTTING, 74, false /* ?? */),
			new SkillRequirement(Skill.SMITHING, 72, false /* ?? */),
			new SkillRequirement(Skill.COOKING, 72, false /* ?? */),
			new SkillRequirement(Skill.FLETCHING, 70, false /* ?? */),
			new SkillRequirement(Skill.MINING, 66, false /* ?? */),
			new SkillRequirement(Skill.HUNTER, 65, false /* ?? */),
			new SkillRequirement(Skill.CRAFTING, 64, false /* ?? */),
			new SkillRequirement(Skill.HERBLORE, 64, false /* ?? */),
			new SkillRequirement(Skill.MAGIC, 57, false /* ?? */)
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
	public List<String> getNotes()
	{
		return List.of(
			"This is a note to appear in the sidebar"
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
			new ItemReward("30,000 Experience Tomes (Any skill above 70)", ItemID.THOSF_REWARD_LAMP /* TODO */, 6),
			new ItemReward("The Flail Upgrade", ItemID.THOSF_REWARD_LAMP /* TODO */),
			new ItemReward("The New Spec Weapon", ItemID.THOSF_REWARD_LAMP /* TODO */)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Vampyrium"),
			new UnlockReward("Access to the Maggot King boss")
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

		sections.add(new PanelDetails("Escaping Castle Drakan", List.of(
			cGetHalfMoonKeyPW
		), List.of(
			blisterwoodFlail,
			combatGear,
			food,
			prayerPotions,
			drakansMedallion
		)));

		sections.add(new PanelDetails("TODO", List.of(
			todo
		), List.of(
		)));

		return sections;
	}
}
