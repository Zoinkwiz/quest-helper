// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nand;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;
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
import com.questhelper.tools.QuestTile;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarPlayerID;

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

	// Recommended items
	ItemRequirement combatGear;
	ItemRequirement food;
	ItemRequirement prayerPotions;
	ItemRequirement energyRestorePotion;
	ItemRequirement drakansMedallion;
	ItemRequirement anyPickaxe;
	FreeInventorySlotRequirement freeInvSlots6;

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

	// Miscellaneous requirements
	VarplayerRequirement followedByIvan;
	VarbitRequirement canReceivePickaxeFromIvan;
	VarbitRequirement needTeleportUnlock;

	// Steps
	// TODO: Remove
	DetailedQuestStep todo;

	/// 0 + 2
	NpcStep startQuest;

	/// 4
	ConditionalStep cLookForIvan;

	/// 6 + 8
	ObjectStep inspectShrine;

	/// 10
	NpcStep talkToIvanGoingToDarkmeyer1;

	/// 12
	DetailedQuestStep defendIvanFromVyres;

	/// 14
	NpcStep talkToIvanAfterEscaping;

	/// 16
	NpcStep talkToIvanOutsideSlepeChurch;

	/// 18
	NpcStep askRoyAboutVeliaf;

	/// 20
	ConditionalStep cLookIntoCommotionAtCrombwickManor;

	/// 22
	NpcStep killVampyresWithVeliaf;

	/// 24
	NpcStep talkToVeliafInCrombwickManor;

	/// 26
	NpcStep talkToIvanPaterdomus1;

	/// 28
	DetailedQuestStep readSquiresJournal;

	/// 30
	NpcStep talkToIvanPaterdomus2;

	/// 32 + 34
	NpcStep killMonksOfZamorak;

	/// 36
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

	/// 50
	DetailedQuestStep getToIvandisTomb;
	ObjectStep investigateHole;

	/// 52
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

	/// 66
	ObjectStep prayAtShrine;

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

		// Recommended items
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Good healing food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		// TODO: Are staminas actually necessary?
		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		drakansMedallion = new ItemRequirement("Drakan's medallion", ItemID.DRAKANS_MEDALLION).isNotConsumed();

		anyPickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).canBeObtainedDuringQuest();

		freeInvSlots6 = new FreeInventorySlotRequirement(6);

		// Mid-quest item requirements
		squiresJournal = new ItemRequirement("Squire's journal", 33701);
		essiandarsNotes = new ItemRequirement("Essiandar's notes", 33707);
		scruffyNotebook = new ItemRequirement("Scruffy notebook", 33704);
		sarlsJournal = new ItemRequirement("Sarl's journal", 33703);
		theLifeOfFriar = new ItemRequirement("The Life of Friar", 33706);
		piousProceedings = new ItemRequirement("Pious proceedings", 33705);
		fromMisthalinToMorytania = new ItemRequirement("Misthalin to Morytania", 33702);
		ivandisWritings = new ItemRequirement("Ivandis' writings", 33708);

		// Miscellaneous requirements
		followedByIvan = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);
		canReceivePickaxeFromIvan = new VarbitRequirement(15469, 0);
		needTeleportUnlock = new VarbitRequirement(15470, 0);
	}

	void setupSteps()
	{
		todo = new DetailedQuestStep(this, "todo");

		startQuest = new NpcStep(this, 15839, new WorldPoint(3697, 3184, 0), "Talk to Sarius Guile in the Icyene Graveyard to start the quest.");
		startQuest.addDialogStep("Yes.");

		/// 32
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
		var todo1 = new DetailedQuestStep(this, "todo1");
		var todo2 = new DetailedQuestStep(this, "todo2");
		var todo3 = new DetailedQuestStep(this, "todo3");
		var todo4 = new DetailedQuestStep(this, "todo4");
		var todoVampyriumPuzzle = new DetailedQuestStep(this, "todo do some vampyrium puzzle");

		steps.put(0, startQuest);
		steps.put(2, startQuest);

		var talkToIvan = new NpcStep(this, 1, new WorldPoint(3599, 9612, 0), "");
		var goDownToIvan = new ObjectStep(this, ObjectID.MYQ4_HIDEOUT_TRAPDOOR_OPEN, new WorldPoint(3605, 3215, 0), "", blisterwoodFlail);
		goDownToIvan.addAlternateObjects(ObjectID.MYQ4_HIDEOUT_TRAPDOOR);
		cLookForIvan = new ConditionalStep(this, goDownToIvan, "Look for Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");
		cLookForIvan.addStep(inMyrequeHideoutOldManRal, talkToIvan);
		steps.put(4, cLookForIvan);

		inspectShrine = new ObjectStep(this, 61177, new WorldPoint(3601, 9614, 0), "Inspect the makeshift shrine in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");
		var cInspectShrine = new ConditionalStep(this, goDownToIvan);
		cInspectShrine.addStep(inMyrequeHideoutOldManRal, inspectShrine);
		steps.put(6, cInspectShrine);
		steps.put(8, cInspectShrine);

		talkToIvanGoingToDarkmeyer1 = new NpcStep(this, NpcID.MYQ5_IVAN_CHILD_BLISTERWOOD_TRADE, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch while wearing the vyre noble outfit, ready for a fight.", vyreNobleOutfit, blisterwoodFlail, combatGear, food, prayerPotions);
		talkToIvanGoingToDarkmeyer1.addDialogStep("Are you ready to go to Darkmeyer?");
		talkToIvanGoingToDarkmeyer1.addDialogStep("I'm ready.");
		var cTalkToIVanGoingToDarkmeyer = new ConditionalStep(this, goDownToIvan);
		cTalkToIVanGoingToDarkmeyer.addStep(and(inMyrequeHideoutOldManRal, vyreNobleOutfit), talkToIvanGoingToDarkmeyer1);
		cTalkToIVanGoingToDarkmeyer.addStep(inMyrequeHideoutOldManRal, talkToIvanGoingToDarkmeyer1);
		steps.put(10, cTalkToIVanGoingToDarkmeyer);

		var talkToIvanToReturnToCastleDrakan = new NpcStep(this, NpcID.MYQ5_IVAN_CHILD_BLISTERWOOD_TRADE, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch to return to the Castle Drakan courtyard.", blisterwoodFlail, combatGear, food, prayerPotions);
		talkToIvanToReturnToCastleDrakan.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanToReturnToCastleDrakan.addDialogStep("I'm ready.");
		talkToIvanGoingToDarkmeyer1.addSubSteps(talkToIvanToReturnToCastleDrakan);
		defendIvanFromVyres = new DetailedQuestStep(this, "Kill the vyrewatches and defend Ivan Strom until he can teleport you both out. Kill the approaching acidic bloodvelds with a ranged weapon. Prioritize the Vyrewatch Sentinels first.", blisterwoodFlail, combatGear, food, prayerPotions);
		var cEscapeCastleDrakan = new ConditionalStep(this, goDownToIvan);
		cEscapeCastleDrakan.addStep(atCastleDrakanCourtyard, defendIvanFromVyres);
		cEscapeCastleDrakan.addStep(inMyrequeHideoutOldManRal, talkToIvanToReturnToCastleDrakan);
		steps.put(12, cEscapeCastleDrakan);

		talkToIvanAfterEscaping = new NpcStep(this, 15835, new WorldPoint(3599, 9612, 0), "Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch after escaping.");
		talkToIvanAfterEscaping.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanAfterEscaping.addDialogStep("I'm ready.");
		var cTalkToIvanAfterEscaping = new ConditionalStep(this, goDownToIvan);
		cTalkToIvanAfterEscaping.addStep(inMyrequeHideoutOldManRal, talkToIvanAfterEscaping);
		steps.put(14, cTalkToIvanAfterEscaping);

		talkToIvanOutsideSlepeChurch = new NpcStep(this, 15855, new WorldPoint(3727, 3310, 0), "Talk to Ivan Strom in the graveyard outside the Slepe church.");
		steps.put(16, talkToIvanOutsideSlepeChurch);

		askRoyAboutVeliaf = new NpcStep(this, NpcID.SLEPE_BARTENDER_ROY, "Talk to Roy the bartender in Slepe and ask him about Veliaf's whereabouts.");
		askRoyAboutVeliaf.addDialogStep("We're looking for a friend of ours.");

		var cAskRoyAboutVeliaf = new ConditionalStep(this, talkToIvanOutsideSlepeChurch);
		cAskRoyAboutVeliaf.addStep(followedByIvan, askRoyAboutVeliaf);
		steps.put(18, cAskRoyAboutVeliaf);

		var lookIntoCommotion = new ObjectStep(this, ObjectID.SLP_CHURCH_CRYPT_SOUTH_LADDER_DOWN, new WorldPoint(3727, 3301, 0), "");
		var climbUpToCrombwickManor = new ObjectStep(this, ObjectID.SLP_BASEMENT_MANOR_EXIT, new WorldPoint(3726, 9756, 1), "");
		cLookIntoCommotionAtCrombwickManor = new ConditionalStep(this, talkToIvanOutsideSlepeChurch, "Head to the Crombwick Manor through the church dungeon.", blisterwoodFlail, combatGear, food, prayerPotions);
		cLookIntoCommotionAtCrombwickManor.addStep(and(followedByIvan, inSlepeChurchDungeon), climbUpToCrombwickManor);
		cLookIntoCommotionAtCrombwickManor.addStep(followedByIvan, lookIntoCommotion);
		steps.put(20, cLookIntoCommotionAtCrombwickManor);

		killVampyresWithVeliaf = new NpcStep(this, new int[]{16127, 16128, 16129, 16125}, new WorldPoint(3725, 3357, 0), "Help Veliaf kill the vampyres in Crombwick Manor.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampyresWithVeliaf.setAllowMultipleHighlights(true);
		var cLookIntoCommotionAtCrombwickManor2 = new ConditionalStep(this, lookIntoCommotion);
		cLookIntoCommotionAtCrombwickManor2.addStep(and(inCrombwickManor), killVampyresWithVeliaf);
		cLookIntoCommotionAtCrombwickManor2.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor);
		steps.put(22, cLookIntoCommotionAtCrombwickManor2);

		// TODO: Confirm npc ID, although you can technically speak to Ivan too
		talkToVeliafInCrombwickManor = new NpcStep(this, 15885, new WorldPoint(3731, 3359, 0), "Talk to Veliaf after helping him kill the vampyres in Crombwick Manor.");

		var cLookIntoCommotionAtCrombwickManor3 = new ConditionalStep(this, lookIntoCommotion);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inCrombwickManor), talkToVeliafInCrombwickManor);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor);
		steps.put(24, cLookIntoCommotionAtCrombwickManor3);

		var enterPaterdomus = new ObjectStep(this, ObjectID.PIPEASTSIDETRAPDOOR_OPEN, new WorldPoint(3422, 3485, 0), "Talk with Ivan Strom in the Paterdomus dungeon.");
		enterPaterdomus.addAlternateObjects(ObjectID.PIPEASTSIDETRAPDOOR);
		talkToIvanPaterdomus1 = new NpcStep(this, 15855, new WorldPoint(3441, 9897, 0), "Talk with Ivan in the Paterdomus dungeon.");
		talkToIvanPaterdomus1.addSubSteps(enterPaterdomus);
		var cHeadToPaterdomus = new ConditionalStep(this, enterPaterdomus);
		cHeadToPaterdomus.addStep(inPaterdomusTempleDungeon, talkToIvanPaterdomus1);
		steps.put(26, cHeadToPaterdomus);

		readSquiresJournal = new DetailedQuestStep(this, "Read the Squire's journal Ivan just gave you.", squiresJournal.highlighted());
		var cReadBook = new ConditionalStep(this, cHeadToPaterdomus);
		cReadBook.addStep(squiresJournal, readSquiresJournal);

		steps.put(28, cReadBook);

		talkToIvanPaterdomus2 = new NpcStep(this, 15855, new WorldPoint(3441, 9897, 0), "Talk with Ivan again after reading the Squire's journal.");
		var cTalkToIvanAfterReadingTheBook = new ConditionalStep(this, enterPaterdomus);
		cTalkToIvanAfterReadingTheBook.addStep(inPaterdomusTempleDungeon, talkToIvanPaterdomus2);
		steps.put(30, cTalkToIvanAfterReadingTheBook);

		killMonksOfZamorak = new NpcStep(this, new int[]{16155, 16156, 16154, 16156}, "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		killMonksOfZamorak.setAllowMultipleHighlights(true);
		var climbUpFromPaterdomusTempleDungeon = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3405, 9907, 0), "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		var headToPaterdomusTempleF0 = new ObjectStep(this, ObjectID.PRIESTPERILTEMPLEDOORL, new WorldPoint(3408, 3489, 0), "Kill the monks of zamorak on the ground floor of the Paterdomus temple.", combatGear, food);
		killMonksOfZamorak.addSubSteps(climbUpFromPaterdomusTempleDungeon, headToPaterdomusTempleF0);
		var cKillMonksOfZamorak = new ConditionalStep(this, cTalkToIvanAfterReadingTheBook);
		cKillMonksOfZamorak.addStep(and(inPaterdomusTempleDungeon, followedByIvan), climbUpFromPaterdomusTempleDungeon);
		cKillMonksOfZamorak.addStep(and(followedByIvan, inPaterdomusTempleF0), killMonksOfZamorak);
		cKillMonksOfZamorak.addStep(and(followedByIvan), headToPaterdomusTempleF0);
		steps.put(32, cKillMonksOfZamorak);

		var cKillMonksOfZamorak2 = new ConditionalStep(this, headToPaterdomusTempleF0);
		cKillMonksOfZamorak2.addStep(and(inPaterdomusTempleF0), killMonksOfZamorak);
		steps.put(34, cKillMonksOfZamorak2);

		var climbUpToPaterdomusTempleF1 = new ObjectStep(this, 61189, new WorldPoint(3417, 3492, 0), "Talk to Ivan Storm on the first floor of the Paterdomus temple.");

		var cReadBooksMaybe = new ConditionalStep(this, climbUpToPaterdomusTempleF1);
		talkToIvanInPaterdomusTempleF1 = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Storm on the first floor of the Paterdomus temple.");
		talkToIvanInPaterdomusTempleF1.addSubSteps(climbUpToPaterdomusTempleF1);
		cReadBooksMaybe.addStep(inPaterdomusTempleF1, talkToIvanInPaterdomusTempleF1);
		steps.put(36, cReadBooksMaybe);

		steps.put(38, cFindTheWritingsPW);

		talkToIvanAfterFindingTheWritings = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Strom after solving the puzzle.");
		var cTalkToIvanTempleAfterBookPuzzle = new ConditionalStep(this, climbUpToPaterdomusTempleF1);
		cTalkToIvanTempleAfterBookPuzzle.addStep(inPaterdomusTempleF1, talkToIvanAfterFindingTheWritings);
		steps.put(40, cTalkToIvanTempleAfterBookPuzzle);

		readIvandisWritings = new DetailedQuestStep(this, "Read Ivandis' writings.", ivandisWritings.highlighted());
		var cReadIvandisWritings = new ConditionalStep(this, cTalkToIvanTempleAfterBookPuzzle);
		cReadIvandisWritings.addStep(ivandisWritings, readIvandisWritings);
		steps.put(42, cReadIvandisWritings);

		talkToIvanAfterReadingIvandisWritings = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Strom after reading Ivandis' writings.");
		var cTalkToIvanAfterReadingIvandisWritings = new ConditionalStep(this, climbUpToPaterdomusTempleF1);
		cTalkToIvanAfterReadingIvandisWritings.addStep(inPaterdomusTempleF1, talkToIvanAfterReadingIvandisWritings);
		steps.put(44, cTalkToIvanAfterReadingIvandisWritings);

		talkToIvanInPaterdomus = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.");
		var cTalkToIvanInPaterdomus = new ConditionalStep(this, talkToIvanInPaterdomus);
		steps.put(46, cTalkToIvanInPaterdomus);

		// TODO: 48 = get there yourself or have him lead the way? What if a user selects "i will take my own route!!!"?
		var talkToIvanInPaterdomus2 = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head to Ivandis' tomb with Ivan Strom.", blisterwoodFlail, combatGear, prayerPotions);
		talkToIvanInPaterdomus2.addDialogStep("Lead the way.");
		talkToIvanInPaterdomus.addSubSteps(talkToIvanInPaterdomus2);
		var cTalkToIvanInPaterdomus2 = new ConditionalStep(this, talkToIvanInPaterdomus2);
		steps.put(48, cTalkToIvanInPaterdomus2);

		// TODO: on step 50: do you have to finish talking with Veliaf until you say "let's have a look around and see what we can find"? this sets varbit 15487 from 0 to 1
		getToIvandisTomb = new DetailedQuestStep(this, new WorldPoint(3500, 9864, 0), "Get to the Myreque hideout");
		investigateHole = new ObjectStep(this, 61193, new WorldPoint(3505, 9857, 0), "Investigate the blockage to the south of the hideout.");
		var cInvestigateHole = new ConditionalStep(this, getToIvandisTomb);
		cInvestigateHole.addStep(inIvandisTomb, investigateHole);
		steps.put(50, cInvestigateHole);

		// 15486 = has talked about pickaxe
		// 15469 = has received pickaxe
		var getPickaxe = new NpcStep(this, 15855, new WorldPoint(3505, 9861, 0), "Ask Ivan Strom for a pickaxe", anyPickaxe);
		mineHole = new ObjectStep(this, 61194, new WorldPoint(3505, 9857, 0), "Mine the blockage to the south of the hideout.", anyPickaxe);
		mineHole.addSubSteps(getPickaxe);
		var cMineHole = new ConditionalStep(this, getToIvandisTomb);
		cMineHole.addStep(and(inIvandisTomb, anyPickaxe), mineHole);
		cMineHole.addStep(and(inIvandisTomb, canReceivePickaxeFromIvan), getPickaxe);
		cMineHole.addStep(and(inIvandisTomb), mineHole);
		steps.put(52, cMineHole);

		headThroughHole = new ObjectStep(this, 61195, new WorldPoint(3505, 9857, 0), "Head through the cave entrance to the south of the hideout, ready for a fight.", blisterwoodFlail, combatGear, food, prayerPotions);
		var cHeadThroughHole = new ConditionalStep(this, getToIvandisTomb);
		cHeadThroughHole.addStep(inIvandisTomb, headThroughHole);
		steps.put(54, cHeadThroughHole);

		enterDaeyaltProcessingRoom = new ObjectStep(this, 61197, new WorldPoint(3117, 7472, 2), "Head into the daeyalt processing room through the tunnel to the north-east.", blisterwoodFlail, combatGear, food, prayerPotions);
		var cWalkThroughCastle = new ConditionalStep(this, cHeadThroughHole);
		cWalkThroughCastle.addStep(inCastleDrakanMines, enterDaeyaltProcessingRoom);
		steps.put(56, cWalkThroughCastle);

		killVampsInDaeyaltRoom = new NpcStep(this, new int[]{16125, 16126, 16137, 16136, 16137}, "Kill vampyres. Focus on the Vyrewatch Sentinels. Avoid the Blood orb. Lure Vyrewatches into the Blood orbs to deal massive damage to them.", blisterwoodFlail, combatGear, food, prayerPotions);
		killVampsInDaeyaltRoom.setAllowMultipleHighlights(true);
		var cWalkThroughCastle2 = new ConditionalStep(this, cWalkThroughCastle);
		cWalkThroughCastle2.addStep(inCastleDrakanDaeyaltProcessingArea, killVampsInDaeyaltRoom);
		steps.put(58, cWalkThroughCastle2);

		// TODO: who do we actually talk to here. is this a "free the slave" step instead?
		talkToIvanAfterKillingVamps = new NpcStep(this, 15864, new WorldPoint(3178, 7459, 0), "Talk to Ivan after killing the vampyres.");
		var cWalkThroughCastle3 = new ConditionalStep(this, cWalkThroughCastle);
		cWalkThroughCastle3.addStep(inCastleDrakanDaeyaltProcessingArea, talkToIvanAfterKillingVamps);
		steps.put(60, cWalkThroughCastle3);

		enterCastleDrakanCellar = new ObjectStep(this, 61205, new WorldPoint(3182, 7470, 0), "Enter the Castle Drakan cellars through the entry to the east (TODO CHECK CARDINAL DIRECTION).");
		var cWalkThroughCastle4 = new ConditionalStep(this, cWalkThroughCastle3);
		cWalkThroughCastle4.addStep(inCastleDrakanDaeyaltProcessingArea, enterCastleDrakanCellar);
		steps.put(62, cWalkThroughCastle4);

		climbUpToCastleDrakanLobby = new ObjectStep(this, 61207, new WorldPoint(3147, 7578, 0), "Climb up the stairs to the Castle Drakan lobby.");
		var cWalkThroughCastle5 = new ConditionalStep(this, cWalkThroughCastle4);
		cWalkThroughCastle5.addStep(inCastleDrakanCellar, climbUpToCastleDrakanLobby);
		steps.put(64, cWalkThroughCastle5);

		// TODO(FOR FUTURE ADVENTURERS): Do you _need_ to bring the medallion for this?
		prayAtShrine = new ObjectStep(this, 61226, new WorldPoint(3168, 7707, 0), "Pray at the shrine to let your Drakan's Medallion teleport you here.", drakansMedallion);

		var combatGearMelee = new ItemRequirement("Melee combat gear", -1, -1).isNotConsumed();
		combatGearMelee.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		var drakansMedallionToCastleDrakan = new ItemRequirement("Drakan's medallion to Castle Drakan", ItemID.DRAKANS_MEDALLION).isNotConsumed().highlighted();
		var enterPortalInCastleDrakanLobby = new ObjectStep(this, 61216, new WorldPoint(3161, 7710, 0), "Click the ominous red portal in the Castle Drakan lobby.", blisterwoodFlail, combatGearMelee);
		enterPortalInCastleDrakanLobby.addDialogStep("Yes.");
		enterPortalInCastleDrakanLobby.addTeleport(drakansMedallionToCastleDrakan);
		var cWalkThroughCastle6 = new ConditionalStep(this, cWalkThroughCastle4);
		cWalkThroughCastle6.addStep(and(inCastleDrakanLobby, needTeleportUnlock), prayAtShrine);
		cWalkThroughCastle6.addStep(and(inCastleDrakanLobby), enterPortalInCastleDrakanLobby);
		cWalkThroughCastle6.addStep(not(needTeleportUnlock), enterPortalInCastleDrakanLobby);
		steps.put(66, cWalkThroughCastle6);

		var cWalkThroughCastle7 = new ConditionalStep(this, enterPortalInCastleDrakanLobby);
		cWalkThroughCastle7.addStep(needTeleportUnlock, prayAtShrine);
		steps.put(68, cWalkThroughCastle7);

		var vampLobby1 = new Zone(new WorldPoint(2216, 7262, 0), new WorldPoint(2429, 7475, 0));
		var inVampyrium = new ZoneRequirement(vampLobby1);

		var youAreInVampyrium = new DetailedQuestStep(this, "Watch the cutscene.");

		var cVampyrium1 = new ConditionalStep(this, enterPortalInCastleDrakanLobby);
		cVampyrium1.addStep(inVampyrium, youAreInVampyrium);
		steps.put(70, cVampyrium1);

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

		var castleDrakanFloor = new VarbitBuilder(15489);
		var castleDrakanRoom = new VarbitBuilder(15499);

		var onF0 = castleDrakanFloor.eq(1);
		var onF1 = castleDrakanFloor.eq(2);
		var onF2 = castleDrakanFloor.eq(3);

		// unspecific
		var inLobby = castleDrakanRoom.eq(1);

		// GROUND FLOOR
		var inLobbyF0 = and(onF0, inLobby);
		var inDiningRoom = castleDrakanRoom.eq(2);
		var inThroneRoom = castleDrakanRoom.eq(3);
		var inRoomSouthOfThroneRoom = castleDrakanRoom.eq(4);
		var inStorageRoom = castleDrakanRoom.eq(6);
		var inStudy = castleDrakanRoom.eq(7);
		var inHallwayWestOfDiningRoom = castleDrakanRoom.eq(8);
		var inEmblemGallery = castleDrakanRoom.eq(13);
		var inWestChapelHallway = castleDrakanRoom.eq(15);
		var inNorthChapelHallway = castleDrakanRoom.eq(17);

		// FIRST FLOOR
		var inVanesculasStudy = castleDrakanRoom.eq(23);
		var inVanesculasChamber = castleDrakanRoom.eq(24);
		var inDrakanEmblemRoomSouthOfExplosiveRoom =  castleDrakanRoom.eq(25);
		var inExplosiveRoom = castleDrakanRoom.eq(28);
		var inHallwayEastOfExplosiveRoom =  castleDrakanRoom.eq(31);
		var inLobbyF1 = and(onF1, inLobby);
		var inHallwayNorthOfLobby = castleDrakanRoom.eq(34);
		var inVanesculasHallway = castleDrakanRoom.eq(35);
		var inRanisHallway = castleDrakanRoom.eq(36);
		var inRanisParlour = castleDrakanRoom.eq(26);
		var inVenatorRoom = castleDrakanRoom.eq(37);

		var enterThroneRoomFromDiningRoom = new ObjectStep(this, 61572, new WorldPoint(2358, 7366, 0), "Enter the throne room.");
		var enterThroneRoomFromStudy = new ObjectStep(this, 61576, new WorldPoint(2358, 7380, 0), "Enter the throne room.");
		var investigateThrone = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Investigate the throne again.");
		var investigateThroneAgain = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Investigate the throne again.");
		var throneRoomPuzzleB = new VarbitBuilder(15508);
		var needToStartThroneRoomPuzzle = throneRoomPuzzleB.eq(0);
		var needToPullBusts = throneRoomPuzzleB.eq(1);
		var needToGetKey = throneRoomPuzzleB.eq(2);
		var gotKey = throneRoomPuzzleB.eq(3);
		var notDoneWithThroneRoom = not(throneRoomPuzzleB.eq(3));

		var inVampyriumVarbit = new VarbitRequirement(15482, 1);

		var trEnter = new ConditionalStep(this, enterThroneRoomFromDiningRoom);
		trEnter.addStep(inDiningRoom, enterThroneRoomFromDiningRoom);
		trEnter.addStep(inStudy, enterThroneRoomFromStudy);

		var needToPullBust3 = new VarbitRequirement(15541, 1);
		var needToPullBust4 = new VarbitRequirement(15542, 1);
		var needToPullBust1 = new VarbitRequirement(15539, 1);

		var pullBust1 = new ObjectStep(this, 61645, new WorldPoint(2317, 7393, 0), "Pull the northern-most bust.");
		var pullBust2 = new ObjectStep(this, 61648, new WorldPoint(2317, 7392, 0), "Pull the second northern-most bust.");
		var pullBust3 = new ObjectStep(this, 61651, new WorldPoint(2317, 7391, 0), "Pull the second southern-most bust.");
		var pullBust4 = new ObjectStep(this, 61654, new WorldPoint(2317, 7390, 0), "Pull the southern-most bust.");

		var pullBusts = new ConditionalStep(this, pullBust2);
		pullBusts.addStep(needToPullBust3, pullBust3);
		pullBusts.addStep(needToPullBust4, pullBust4);
		pullBusts.addStep(needToPullBust1, pullBust1);

		var trPuzzle = new ConditionalStep(this, trEnter);
		trPuzzle.addStep(and(inThroneRoom, needToStartThroneRoomPuzzle), investigateThrone);
		trPuzzle.addStep(and(inThroneRoom, needToPullBusts), pullBusts);
		trPuzzle.addStep(and(inThroneRoom, needToGetKey), investigateThroneAgain);

		var halfMoonKey = new ItemRequirement("Half moon key", 33725);

		// This could _technically_ be a conditional step guiding the user from _any_ room back to the throne room,
		// but they should only ever get to this step if they've manually destroyed the key. Their punishment
		// is that they need to read the text to get back to the throne room.
		var getKeyFromThroneRoom = new ObjectStep(this, 61630, new WorldPoint(2313, 7392, 0), "Head back to the throne room and search the throne for a half moon key.");

		var cVampyrium2 = new ConditionalStep(this, enterPortalInCastleDrakanLobby, "Solve the puzzles inside Vampyrium's Castle Drakan. You can pick up supplies you find on the ground or in sparkling containers.");

		cVampyrium2.addStep(and(inVampyriumVarbit, notDoneWithThroneRoom), trPuzzle);
		cVampyrium2.addStep(and(inVampyriumVarbit, gotKey, not(halfMoonKey)), getKeyFromThroneRoom);

		var smallClockHand = new ItemRequirement("Small clock hand", 33744);
		var searchShelvesForSmallClockHand = new ObjectStep(this, 61752, new WorldPoint(2323, 7387, 0), "Search the shelves for a small clock hand in the room south of the throne room.");
		var enterRoomSouthOfThroneRoom = new ObjectStep(this, 61587, new WorldPoint(2310, 7386, 0), "Enter the room south of the throne room.");

		var getSmallClockHand = new ConditionalStep(this, searchShelvesForSmallClockHand);
		getSmallClockHand.addStep(and(inVampyriumVarbit, inThroneRoom), enterRoomSouthOfThroneRoom);

		var largeClockHand = new ItemRequirement("Large clock hand", 33745);

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

		var tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.setTooltip("You can get another one from the storage room (south-west on floor 1)");

		var goUpFromStorageRoom = new ObjectStep(this, 61602, new WorldPoint(2340, 7384, 0), "Climb up the stairs.", tinderbox);

		var explosiveBarrel = new ItemRequirement("Explosive barrel", 33743);
		explosiveBarrel.setTooltip("You can get another one from the room above the storage room (south-west on floor 2)");
		var pickUpExplosiveBarrel = new ItemStep(this, new WorldPoint(2439, 7388, 0), "Pick up the explosive barrel", explosiveBarrel, tinderbox);

		var enterSouthDoorFromExplosiveRoom = new ObjectStep(this, 61573, new WorldPoint(2439, 7384, 0), "Enter the room south of where you picked up the explosive barrel.", explosiveBarrel, tinderbox);

		var searchCrateForDrakanEmblem1 = new ObjectStep(this, 61751, new WorldPoint(2454, 7378, 0), "Search the crate for a drakan emblem");

		// from somewhere
		var drakanEmblem1 = new ItemRequirement("Drakan emblem", 33731);
		// from clock puzzle
		var drakanEmblem2 = new ItemRequirement("Drakan emblem", 33732);
		var drakanEmblem3 = new ItemRequirement("Drakan emblem", 33733);
		var anyEmblem = new ItemRequirement("Drakan emblem", 33731).highlighted();
		anyEmblem.addAlternates(33732, 33733);
		var anyEmblem2 = new ItemRequirement("Drakan emblem", 33731, 2).highlighted();
		anyEmblem2.addAlternates(33732, 33733);
		var anyEmblem3 = anyEmblem2.quantity(3);

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

		var placeEmblem1OnReceptacle = new ObjectStep(this, 61638, new WorldPoint(2469, 7408, 0), "Place the drakan emblem on the empty receptacle next to the southern door down the hall, avoiding traps on the way.", explosiveBarrel, tinderbox, anyEmblem);
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

		var placedEmblemInVanesculasHallway = new VarbitRequirement(15504, 0, Operation.GREATER);
		var placedEmblemInVanesculasStudy = new VarbitRequirement(15503, 0, Operation.GREATER);

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
		var cmkPlaceEmblemInVanesculasHallway = new ObjectStep(this, 61638, new WorldPoint(2469, 7408, 0), "Place the emblem in the receptacle in Vanescula's hallway.", anyEmblem);
		var cmkPlaceEmblemInVanesculasStudy = new ObjectStep(this, 61638, new WorldPoint(2476, 7367, 0), "Place the emblem in Vanescula's study", anyEmblem);
		var cmkToVanesculasChamber = new ObjectStep(this, 61572, new WorldPoint(2477, 7366, 0), "Enter Vanescula's chamber.");
		var cmkBlowUpWallInVanesculasChamber = new ObjectStep(this, 61613, new WorldPoint(2492, 7364, 0), "Place the explosive barrel on the cracked wall in Vanescula's chamber.", explosiveBarrel.highlighted(), tinderbox);
		var cmkEnterThroughHole = new ObjectStep(this, 61614, new WorldPoint(2492, 7364, 0), "Enter hole in the wall.");
		var cmkTakeEmblem3 = new ObjectStep(this, 61639, new WorldPoint(2486, 7421, 0), "Remove the emblem from the room with the Venator.");
		var cmkLeaveVenatorRoom = new ObjectStep(this, 61614, new WorldPoint(2482, 7412, 0), "Leave the venator room through the hole in the wall.");
		var cmkLeaveVanesculasChamber = new ObjectStep(this, 61573, new WorldPoint(2483, 7368, 0), "Leave Vanescula's room.", drakanEmblem3);
		var cmkTakeEmblemFromVanesculasStudy = new ObjectStep(this, 61639, new WorldPoint(2476, 7367, 0), "Take emblem from vanescula's study", drakanEmblem3);
		var cmkLeaveVanesculasStudy = new ObjectStep(this, 61573, new WorldPoint(2474, 7372, 0), "Leave vanescula's study", anyEmblem2);
		var cmkRetrieveThirdEmblem = new ObjectStep(this, 61634, new WorldPoint(2469, 7408, 0), "Remove the third emblem from the receptacle.");
		var cmkPutEmblemInEastDoor = new ObjectStep(this, 61636, new WorldPoint(2476, 7410, 0), "Place an emblem in the empty receptacle by the east wall.", anyEmblem3);
		var cmkEnterEastDoor = new ObjectStep(this, 61572, new WorldPoint(2477, 7409, 0), "Enter the door to Ranis' hallway.", anyEmblem2);
		var cmkPutEmblemInRanisHallwayNorth = new ObjectStep(this, 61635, new WorldPoint(2486, 7404, 0), "Place an emblem in the empty receptacle at the north door.", anyEmblem2);
		var cmkEnterRanisParlour = new ObjectStep(this, 61576, new WorldPoint(2485, 7405, 0), "Enter Ranis' parlour");
		var ornateSkull = new ItemRequirement("Ornate skull", 33741);
		var cmkGetSkull = new DetailedQuestStep(this, new WorldPoint(2471, 7384, 0), "Get the ornate skull from the table in the room.", ornateSkull);
		var cmkLeaveRanisParlourRoom = new ObjectStep(this, 61577, new WorldPoint(2475, 7379, 0), "Leave Ranis' parlour room", ornateSkull);
		var cmkRemoveEmblemRanisNorth = new ObjectStep(this, 61635, new WorldPoint(2486, 7404, 0), "Remove emblem from the receptacle.", ornateSkull);
		var cmkClimbDownStairsRanisHallway = new ObjectStep(this, 61604, new WorldPoint(2491, 7402, 0), "Climb-down Stairs.", ornateSkull, anyEmblem2);
		var cmkPlaceEmblemDownstairs = new ObjectStep(this, 61632, new WorldPoint(2371, 7410, 0), "Place an emblem in the receptacle to the west, avoiding the traps on the floor.", ornateSkull, anyEmblem2);
		cmkPlaceEmblemDownstairs.addTileMarkers(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
			new WorldPoint(2378, 7411, 0),
			new WorldPoint(2379, 7411, 0),
			new WorldPoint(2379, 7412, 0),
			new WorldPoint(2378, 7412, 0)
		);
		var cmkEnterWestChapelHallway = new ObjectStep(this, 61572, new WorldPoint(2372, 7409, 0), "Enter the west chapel hallway.", ornateSkull, anyEmblem);
		var cmkPutEmblemInWestChapelHallway = new ObjectStep(this, 61631, new WorldPoint(2370, 7383, 0), "Place an emblem in the empty receptacle by the western door.", anyEmblem);
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

		var getCrescentMoonKey = new ConditionalStep(this, todo3, "Get the Crescent Moon Key.");
		getCrescentMoonKey.addStep(cmkSolvedChestPuzzle, cmkGetTheKeyFromTheChest);
		getCrescentMoonKey.addStep(and(inEmblemGallery, cmkHasSpokenWithVeliaf, inArrowPuzzle), cmkArrowChestPuzzleStep);
		getCrescentMoonKey.addStep(and(inEmblemGallery, cmkHasSpokenWithVeliaf), cmkOpenEmblemGalleryChest);
		getCrescentMoonKey.addStep(and(inEmblemGallery), cmkTalkToVeliaf);
		getCrescentMoonKey.addStep(and(inWestChapelHallway, anyEmblemInWestChapelHallway), cmkEnterEmblemGallery);
		getCrescentMoonKey.addStep(and(inWestChapelHallway), cmkPutEmblemInWestChapelHallway);
		getCrescentMoonKey.addStep(and(inNorthChapelHallway, anyEmblemInNorthChapelHallway), cmkEnterWestChapelHallway);
		getCrescentMoonKey.addStep(and(inNorthChapelHallway), cmkPlaceEmblemDownstairs);
		getCrescentMoonKey.addStep(and(inRanisHallway, noEmblemInRanisHallwayNorth, ornateSkull, anyEmblem2), cmkClimbDownStairsRanisHallway);
		getCrescentMoonKey.addStep(and(inRanisHallway, anyEmblemInRanisHallwayNorth, ornateSkull), cmkRemoveEmblemRanisNorth);
		getCrescentMoonKey.addStep(and(inRanisParlour, ornateSkull), cmkLeaveRanisParlourRoom);
		getCrescentMoonKey.addStep(and(inRanisParlour), cmkGetSkull);
		getCrescentMoonKey.addStep(and(inRanisHallway, anyEmblemInRanisHallwayNorth), cmkEnterRanisParlour);
		getCrescentMoonKey.addStep(and(inRanisHallway), cmkPutEmblemInRanisHallwayNorth);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, anyEmblemInVanesculasHallwayEast), cmkEnterEastDoor);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, anyEmblem3), cmkPutEmblemInEastDoor);
		getCrescentMoonKey.addStep(and(inVanesculasHallway, anyEmblem2), cmkRetrieveThirdEmblem);
		getCrescentMoonKey.addStep(and(inVanesculasStudy, anyEmblem2), cmkLeaveVanesculasStudy);
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

		var getNewMoonKey = new ConditionalStep(this, todo3, "Get the new moon key.");
		// TODO: Get the new moon key

		cVampyrium2.addStep(crescentMoonKey, getNewMoonKey);
		cVampyrium2.addStep(hasGottenDrakanEmblemFromFireplace, getCrescentMoonKey);

		cVampyrium2.addStep(and(inVampyriumVarbit, not(smallClockHand), smallClockHandNeedsReplacing), getSmallClockHand);
		cVampyrium2.addStep(and(inVampyriumVarbit, not(largeClockHand), largeClockHandNeedsReplacing), getLargeClockHand);
		cVampyrium2.addStep(and(inVampyriumVarbit, needToFinishClockPuzzle, not(inDiningRoom)), getBackToDiningRoom);
		cVampyrium2.addStep(and(inVampyriumVarbit, needToFinishClockPuzzle), solveClockPuzzle);

		// TODO: do we need to prompt the user to pick up the poem scroll?

		cVampyrium2.addStep(inVampyriumVarbit, todoVampyriumPuzzle);

		steps.put(72, cVampyrium2);
		// 72 -> 74 after talking to Veliaf in the emblem gallery
		steps.put(74, cVampyrium2);

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
			talkToIvanGoingToDarkmeyer1,
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
			prayAtShrine
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

		sections.add(new PanelDetails("TODO", List.of(
			todo
		), List.of(
		)));

		return sections;
	}
}
