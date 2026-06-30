// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.helpers.quests.thebloodmoonrises;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.FollowerRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
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
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.steps.WidgetStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nand;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;

/**
 * The quest guide for the "The Blood Moon Rises" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/The_Blood_Moon_Rises">The OSRS wiki guide</a> was referenced for this guide
 */
public class TheBloodMoonRises extends BasicQuestHelper
{
	// Required items
	ItemRequirement sampleRequirement;

	// Recommended items
	ItemRequirement energyRestorePotion;
	ItemRequirement drakanMedallion;

	// Mid-quest item requirements
	ItemRequirement itemAcquiredDuringQuest;

	// Zones
	Zone sampleZone;

	// Miscellaneous requirements
	VarbitRequirement hasFinishedCertainTask;

	// Steps
	// TODO: Remove
	DetailedQuestStep todo;


	/// 0
	NpcStep startQuest;

	@Override
	protected void setupZones()
	{
		sampleZone = new Zone(new WorldPoint(10, 10, 0), new WorldPoint(20, 20, 0));
	}

	@Override
	protected void setupRequirements()
	{
		sampleRequirement = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);

		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		drakanMedallion = new ItemRequirement("Drakan's medallion", ItemID.DRAKANS_MEDALLION);

		itemAcquiredDuringQuest = new ItemRequirement("Book of Lore", ItemID.BOOK_OF_ASTROLOGY);

		hasFinishedCertainTask = new VarbitRequirement(VarbitID.MYQ5, 2);
	}

	void setupSteps()
	{
		todo = new DetailedQuestStep(this, "todo");

		startQuest = new NpcStep(this, 15839, new WorldPoint(3697, 3184, 0), "Talk to Sarius Guile in the Icyene Graveyard to start the quest.");
		startQuest.addDialogStep("Yes.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		// TODO: Remove
		for (var i = 0; i < 2000; ++i) {
			steps.put(i, todo);
		}

		steps.put(0, startQuest);
		steps.put(2, startQuest);

		var blisterwoodFlail = new ItemRequirement("Blisterwood flail", ItemID.BLISTERWOOD_FLAIL);
		blisterwoodFlail.setTooltip("You can get another Blisterwood Flail from Ivan in the Myreque Hideout in Old" +
			" Man Ral's basement or Veliaf Hurtz at the Icyene Graveyard(?)");

		var newBase = new Zone(new WorldPoint(3588, 9609, 0), new WorldPoint(3606, 9619, 0));
		var inNewBase = new ZoneRequirement(newBase);
		var talkToIvan = new NpcStep(this, 1,  new WorldPoint(3599, 9612, 0),"Look for Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch.");
		var goDownToIvan = new ObjectStep(this, ObjectID.MYQ4_HIDEOUT_TRAPDOOR_OPEN, new WorldPoint(3605, 3215, 0), "Look for Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch.", blisterwoodFlail);
		goDownToIvan.addAlternateObjects(ObjectID.MYQ4_HIDEOUT_TRAPDOOR);
		// goDownToIvan.addDialogStep("Meiyerditch.");
		var cTalkToIvan = new ConditionalStep(this, goDownToIvan);
		cTalkToIvan.addStep(inNewBase, talkToIvan);
		steps.put(4, cTalkToIvan);

		var inspectShrine = new ObjectStep(this, 61177, new WorldPoint(3601, 9614, 0), "Inspect the makeshift shrine");
		var cLookAround = new ConditionalStep(this, goDownToIvan);
		cLookAround.addStep(inNewBase, inspectShrine);

		steps.put(6, cLookAround);
		steps.put(8, cLookAround);

		var vyrewatchOutfit = new ItemRequirements("Vyre noble outfit",
			new ItemRequirement("Vyre noble top", ItemID.VYRELORD_TORSO),
			new ItemRequirement("Vyre noble legs", ItemID.VYRELORD_LEGS),
			new ItemRequirement("Vyre noble shoes", ItemID.VYRELORD_SHOES));
		vyrewatchOutfit.setHighlightInInventory(true);
		vyrewatchOutfit.setMustBeEquipped(true);
		vyrewatchOutfit.setTooltip("Can be obtained from the chest next to Ivan");


		var actuallyTalkToIvan = new NpcStep(this, 9532,  new WorldPoint(3599, 9612, 0),"Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch while wearing the vyre noble outfit.", vyrewatchOutfit);
		actuallyTalkToIvan.addDialogStep("Are you ready to go to Darkmeyer?");
		actuallyTalkToIvan.addDialogStep("I'm ready.");
		var cActuallyTalkToIvanInBase = new ConditionalStep(this, goDownToIvan);
		cActuallyTalkToIvanInBase.addStep(and(inNewBase, vyrewatchOutfit), actuallyTalkToIvan);
		cActuallyTalkToIvanInBase.addStep(inNewBase, actuallyTalkToIvan);

		steps.put(10, cActuallyTalkToIvanInBase);

		var atCastleDrakan = new ZoneRequirement(new Zone(new WorldPoint(3589, 3347, 0), new WorldPoint(3561, 3367, 0)));
		var fightOffVyreWatches = new DetailedQuestStep(this, "Defend Ivan Strom safe. Kill the approaching acidic bloodvelds with a ranged weapon. TODO: Bring lots of food and some prayer potions.");
		var talkToIvanToReturnToCastleDrakan = new NpcStep(this, 9532,  new WorldPoint(3599, 9612, 0),"Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch to return to Castle Drakan.", blisterwoodFlail);
		talkToIvanToReturnToCastleDrakan.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanToReturnToCastleDrakan.addDialogStep("I'm ready.");
		var cEscapeCastleDrakan = new ConditionalStep(this, goDownToIvan);
		// cEscapeCastleDrakan.addStep(and(inNewBase, vyrewatchOutfit), actuallyTalkToIvan);
		cEscapeCastleDrakan.addStep(atCastleDrakan, fightOffVyreWatches);
		cEscapeCastleDrakan.addStep(inNewBase, talkToIvanToReturnToCastleDrakan);
		steps.put(12, cEscapeCastleDrakan);

		var talkToIvanAfterEscaping = new NpcStep(this, 15835,  new WorldPoint(3599, 9612, 0),"Talk to Ivan in the Myreque Hideout below Old Man Ral's home in Meiyerditch after escaping.");
		talkToIvanAfterEscaping.addDialogStep("We'd better get back to Darkmeyer.");
		talkToIvanAfterEscaping.addDialogStep("I'm ready.");
		var cTalkToIvanAfterEscaping = new ConditionalStep(this, goDownToIvan);
		cTalkToIvanAfterEscaping.addStep(inNewBase, talkToIvanAfterEscaping);
		steps.put(14, cTalkToIvanAfterEscaping);

		var talkToIvanOutsideSlepeChurch = new NpcStep(this, 15855, new WorldPoint(3727, 3310, 0), "Talk to Ivan Strom in the graveyard outside the Slepe church.");
		steps.put(16, talkToIvanOutsideSlepeChurch);

		var followedByIvan = new VarplayerRequirement(VarPlayerID.FOLLOWER_NPC, 15854 /* myq6_ivan_follower */, 16);

		var coins99 = new ItemRequirement("Coins for a drink", ItemID.COINS, 99);
		var askRoyAboutVeriaf = new NpcStep(this, NpcID.SLEPE_BARTENDER_ROY, "Talk to Roy the bartender in Slepe and ask him about Veriaf's whereabouts.");
		// askRoyAboutVeriaf.addDialogStepWithExclusion("One Bloody Bracer please.", "What happened to Carl?");
		// askRoyAboutVeriaf.addDialogStep("What happened to Carl?");
		// askRoyAboutVeriaf.addDialogStep("Here you go.");
		askRoyAboutVeriaf.addDialogStep("We're looking for a friend of ours.");
		// NOTE: This costs 99 coins

		var cAskRoyAboutVeriaf = new ConditionalStep(this, talkToIvanOutsideSlepeChurch);
		cAskRoyAboutVeriaf.addStep(followedByIvan, askRoyAboutVeriaf);
		steps.put(18, cAskRoyAboutVeriaf);

		var slepeChurchDungeon1 = new Zone(14999);
		var slepeChurchDungeon2 = new Zone(15000);
		var slepeChurchDungeon3 = new Zone(15000);
		var slepeChurchDungeon4 = new Zone(15255);
		var slepeChurchDungeon5 = new Zone(15256);
		var slepeChurchDungeon6 = new Zone(15257);
		var slepeChurchDungeon7 = new Zone(15511);
		var slepeChurchDungeon8 = new Zone(15512);
		var slepeChurchDungeon9 = new Zone(15513);
		var inSlepeChurchDungeon = new ZoneRequirement(slepeChurchDungeon1, slepeChurchDungeon2, slepeChurchDungeon3, slepeChurchDungeon4, slepeChurchDungeon5, slepeChurchDungeon6, slepeChurchDungeon7, slepeChurchDungeon8, slepeChurchDungeon9);

		var lookIntoCommotion = new ObjectStep(this, ObjectID.SLP_CHURCH_CRYPT_SOUTH_LADDER_DOWN, new WorldPoint(3727, 3301, 0), "Head to the Crombwick Manor through the church dungeon.");
		var climbUpToCrombwickManor = new ObjectStep(this, ObjectID.SLP_BASEMENT_MANOR_EXIT, new WorldPoint(3726, 9756, 1), "Head to the Crombwick Manor through the church dungeon.");
		var cLookIntoCommotionAtCrombwickManor = new ConditionalStep(this, talkToIvanOutsideSlepeChurch);
		cLookIntoCommotionAtCrombwickManor.addStep(and(followedByIvan, inSlepeChurchDungeon), climbUpToCrombwickManor);
		cLookIntoCommotionAtCrombwickManor.addStep(followedByIvan, lookIntoCommotion);
		steps.put(20, cLookIntoCommotionAtCrombwickManor);

		var crombwickManor1 = new Zone(new WorldPoint(3714, 3361, 0), new WorldPoint(3737, 3355, 0));
		var crombwickManor2 = new Zone(new WorldPoint(3721, 3366, 0), new WorldPoint(3725, 3361, 0));
		var crombwickManor3 = new Zone(new WorldPoint(3727, 3362, 0), new WorldPoint(3732, 3359, 0));
		var crombwickManor4 = new Zone(new WorldPoint(3721, 3354, 0), new WorldPoint(3729, 3351, 0));
		var inCrombwickManor = new ZoneRequirement(crombwickManor1, crombwickManor2, crombwickManor3, crombwickManor4);

		var killVampyresWithVeliaf = new NpcStep(this, new int[]{16127, 16128, 16129, 16125}, new WorldPoint(3725, 3357, 0), "Help Veliaf kill the vampyres in Crombwick Manor.", blisterwoodFlail);
		killVampyresWithVeliaf.setAllowMultipleHighlights(true);
		var cLookIntoCommotionAtCrombwickManor2 = new ConditionalStep(this, lookIntoCommotion);
		cLookIntoCommotionAtCrombwickManor2.addStep(and(inCrombwickManor), killVampyresWithVeliaf);
		cLookIntoCommotionAtCrombwickManor2.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor);
		steps.put(22, cLookIntoCommotionAtCrombwickManor2);

		// TODO: Confirm npc ID, although you can technically speak to Ivan too
		var talkToVeliaf = new NpcStep(this, 15885, new WorldPoint(3731, 3359, 0), "Talk to Veliaf after helping him kill the vampyres in Crombwick Manor.");

		var cLookIntoCommotionAtCrombwickManor3 = new ConditionalStep(this, lookIntoCommotion);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inCrombwickManor), talkToVeliaf);
		cLookIntoCommotionAtCrombwickManor3.addStep(and(inSlepeChurchDungeon), climbUpToCrombwickManor);
		steps.put(24, cLookIntoCommotionAtCrombwickManor3);

		var paterdormusDungeon1 = new Zone(13466);
		var paterdormusDungeon2 = new Zone(13722);
		var inPaterdormusDungeon = new ZoneRequirement(paterdormusDungeon1, paterdormusDungeon2);

		var enterPaterdormus = new ObjectStep(this, ObjectID.PIPEASTSIDETRAPDOOR_OPEN, new WorldPoint(3422, 3485, 0), "Enter Paterdormus and talk with Ivan");
		enterPaterdormus.addAlternateObjects(ObjectID.PIPEASTSIDETRAPDOOR);
		var talkToIvanPaterdormus1 = new NpcStep(this, 15855, new WorldPoint(3441, 9897, 0), "Talk with Ivan in the Paterdormus dungeon.");
		var cHeadToPaterdormus = new ConditionalStep(this, enterPaterdormus);
		cHeadToPaterdormus.addStep(inPaterdormusDungeon, talkToIvanPaterdormus1);
		steps.put(26, cHeadToPaterdormus);

		var book = new ItemRequirement("Squire's journal", 33701);
		var readBook = new DetailedQuestStep(this, "Read the Squire's journal Ivan just gave you.", book.highlighted());
		var cReadBook = new ConditionalStep(this, cHeadToPaterdormus);
		cReadBook.addStep(book, readBook);

		steps.put(28, cReadBook);

		var talkToIvanPaterdormus2 = new NpcStep(this, 15855, new WorldPoint(3441, 9897, 0), "Talk with Ivan again after reading the Squire's journal.");
		var cTalkToIvanAfterReadingTheBook = new ConditionalStep(this, enterPaterdormus);
		cTalkToIvanAfterReadingTheBook.addStep(inPaterdormusDungeon, talkToIvanPaterdormus2);
		steps.put(30, cTalkToIvanAfterReadingTheBook);

		var temple1 = new Zone(new WorldPoint(3409, 3483, 0), new WorldPoint(3411, 3494, 0));
		var temple2 = new Zone(new WorldPoint(3408, 3485, 0), new WorldPoint(3408, 3486, 0));
		var temple3 = new Zone(new WorldPoint(3408, 3491, 0), new WorldPoint(3408, 3492, 0));
		var temple4 = new Zone(new WorldPoint(3412, 3484, 0), new WorldPoint(3415, 3493, 0));
		var temple5 = new Zone(new WorldPoint(3416, 3483, 0), new WorldPoint(3417, 3494, 0));
		var temple6 = new Zone(new WorldPoint(3418, 3484, 0), new WorldPoint(3418, 3493, 0));
		var inZamarokianTempleF1 = new ZoneRequirement(temple1, temple2, temple3, temple4, temple5, temple6);

		var templeFloorMiddle = new Zone(new WorldPoint(3408, 3483, 1), new WorldPoint(3419, 3494, 1));
		var inZamarokianTempleF2 = new ZoneRequirement(templeFloorMiddle);

		var killZamarokianMonks = new NpcStep(this, new int[]{16155, 16156, 16154,16156}, "Kill the monks.");
		killZamarokianMonks.setAllowMultipleHighlights(true);
		var cKillZamarokianMonks = new ConditionalStep(this, cTalkToIvanAfterReadingTheBook);
		var headToZamarokianChurch1 = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3405, 9907, 0), "Climb up from the Paterdormus dungeon.");
		var headToZamarokianChurch2 = new ObjectStep(this, ObjectID.PRIESTPERILTEMPLEDOORL, new WorldPoint(3408, 3489, 0),"Enter the Zamarokian temple.");
		cKillZamarokianMonks.addStep(and(inPaterdormusDungeon, followedByIvan), headToZamarokianChurch1);
		cKillZamarokianMonks.addStep(and(followedByIvan, inZamarokianTempleF1), killZamarokianMonks);
		cKillZamarokianMonks.addStep(and(followedByIvan), headToZamarokianChurch2);
		steps.put(32, cKillZamarokianMonks);

		var cKillZamarokianMonks2 = new ConditionalStep(this, headToZamarokianChurch2);
		cKillZamarokianMonks2.addStep(and(inZamarokianTempleF1), killZamarokianMonks);
		steps.put(34, cKillZamarokianMonks2);

		// TODO(FUTURE ADVENTURERS): See if this staircase is highlighted correctly right after killing the monks.
		var climbUpTemple1 = new ObjectStep(this, 61189, new WorldPoint(3417, 3492, 0), "Talk to Ivan Storm upstairs in the zamarokian temple.");

		var cReadBooksMaybe = new ConditionalStep(this, climbUpTemple1);
		var talkToIvanTemple = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan Storm upstairs in the zamarokian temple.");
		cReadBooksMaybe.addStep(inZamarokianTempleF2, talkToIvanTemple);
		steps.put(36, cReadBooksMaybe);

		var climbUpTempleForWritings = new ObjectStep(this, 61189, new WorldPoint(3417, 3492, 0), "Climb up the zamarokian temple and solve the puzzle.");

		// step 38 is just the puzzle, the squire's journal is not actually necessary
		var findTheWritings = new ConditionalStep(this, climbUpTempleForWritings, "Find the writings for Ivan.");

		var sarlsJournal = new ItemRequirement("Sarl's journal", 33703);
		var theLifeOfFriar = new ItemRequirement("The Life of Friar", 33706);
		var getSarlsJournalAndTheLifeOfFriar = new ObjectStep(this, 61304, new WorldPoint(3414, 3491, 1), "Get Sarl's journal and The Life of Friar from the bookcase.", sarlsJournal, theLifeOfFriar);
		getSarlsJournalAndTheLifeOfFriar.addDialogStep("Both.");
		getSarlsJournalAndTheLifeOfFriar.addDialogStep("Yes.");

		var piousProceedings = new ItemRequirement("Pious proceedings", 33705);
		var getPiousProceedings = new ObjectStep(this, 61305, new WorldPoint(3411, 3491, 1), "Get Pious proceedings from the bookcase.", piousProceedings);
		getPiousProceedings.addDialogStep("Yes.");

		var fromMisthalinToMorytania = new ItemRequirement("Misthalin to Morytania", 33702);
		var getFromMisthalinToMorytania = new ObjectStep(this, 61302, new WorldPoint(3411, 3492, 1), "Get Misthalin to Morytania from the bookcase.", fromMisthalinToMorytania);
		getFromMisthalinToMorytania.addDialogStep("Yes.");

		var scruffyNotebook = new ItemRequirement("Scruffy notebook", 33704);
		var essiandarsNotes = new ItemRequirement("Essiandar's notes", 33707);
		var getScruffyNotebookAndEssiandarsNotes = new ObjectStep(this, 61306, new WorldPoint(3409, 3488, 1), "Get the scruffy notebook and Essiandar's notes from the bookcase.");
		getScruffyNotebookAndEssiandarsNotes.addDialogStep("Both.");
		getScruffyNotebookAndEssiandarsNotes.addDialogStep("Yes.");

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

		var plinth1Pos = new WorldPoint(3409, 3483, 1);
		var plinth2Pos = new WorldPoint(3408, 3485, 1);
		var plinth3Pos = new WorldPoint(3409, 3487, 1);
		var plinth4Pos = new WorldPoint(3409, 3490, 1);
		var plinth5Pos = new WorldPoint(3408, 3492, 1);
		var plinth6Pos = new WorldPoint(3409, 3494, 1);

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

		var wowYouAreDone = new DetailedQuestStep(this, "You have placed all the books correctly, continue in the dialog to proceed.");

		findTheWritings.addStep(and(inZamarokianTempleF2, and(essiandarsNotesS1, sarlsJournalS2, fromMisthalinToMorytaniaS3, scruffyNotebookS4, theLifeOfFriarS5, piousProceedingsS6)), wowYouAreDone);

		findTheWritings.addStep(and(inZamarokianTempleF2, essiandarsNotesS2), essiandarsNotesS2Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, essiandarsNotesS3), essiandarsNotesS3Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, essiandarsNotesS4), essiandarsNotesS4Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, essiandarsNotesS5), essiandarsNotesS5Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, essiandarsNotesS6), essiandarsNotesS6Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, sarlsJournalS1), sarlsJournalS1Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, sarlsJournalS3), sarlsJournalS3Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, sarlsJournalS4), sarlsJournalS4Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, sarlsJournalS5), sarlsJournalS5Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, sarlsJournalS6), sarlsJournalS6Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, fromMisthalinToMorytaniaS1), fromMisthalinToMorytaniaS1Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, fromMisthalinToMorytaniaS2), fromMisthalinToMorytaniaS2Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, fromMisthalinToMorytaniaS4), fromMisthalinToMorytaniaS4Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, fromMisthalinToMorytaniaS5), fromMisthalinToMorytaniaS5Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, fromMisthalinToMorytaniaS6), fromMisthalinToMorytaniaS6Take);

		findTheWritings.addStep(and(inZamarokianTempleF2, scruffyNotebookS1), scruffyNotebookS1Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, scruffyNotebookS2), scruffyNotebookS2Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, scruffyNotebookS3), scruffyNotebookS3Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, scruffyNotebookS5), scruffyNotebookS5Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, scruffyNotebookS6), scruffyNotebookS6Take);

		findTheWritings.addStep(and(inZamarokianTempleF2, theLifeOfFriarS1), theLifeOfFriarS1Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, theLifeOfFriarS2), theLifeOfFriarS2Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, theLifeOfFriarS3), theLifeOfFriarS3Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, theLifeOfFriarS4), theLifeOfFriarS4Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, theLifeOfFriarS6), theLifeOfFriarS6Take);

		findTheWritings.addStep(and(inZamarokianTempleF2, piousProceedingsS1), piousProceedingsS1Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, piousProceedingsS2), piousProceedingsS2Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, piousProceedingsS3), piousProceedingsS3Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, piousProceedingsS4), piousProceedingsS4Take);
		findTheWritings.addStep(and(inZamarokianTempleF2, piousProceedingsS5), piousProceedingsS5Take);

		findTheWritings.addStep(and(inZamarokianTempleF2, nor(essiandarsNotes, essiandarsNotesS1)), getScruffyNotebookAndEssiandarsNotes);
		findTheWritings.addStep(and(inZamarokianTempleF2, not(essiandarsNotesS1)), essiandarsNotesS1Put);

		findTheWritings.addStep(and(inZamarokianTempleF2, nor(scruffyNotebook, scruffyNotebookS4)), getScruffyNotebookAndEssiandarsNotes);
		findTheWritings.addStep(and(inZamarokianTempleF2, not(scruffyNotebookS4)), scruffyNotebookS4Put);

		findTheWritings.addStep(and(inZamarokianTempleF2, nor(sarlsJournal, sarlsJournalS2)), getSarlsJournalAndTheLifeOfFriar);
		findTheWritings.addStep(and(inZamarokianTempleF2, not(sarlsJournalS2)), sarlsJournalS2Put);

		findTheWritings.addStep(and(inZamarokianTempleF2, nor(theLifeOfFriar, theLifeOfFriarS5)), getSarlsJournalAndTheLifeOfFriar);
		findTheWritings.addStep(and(inZamarokianTempleF2, not(theLifeOfFriarS5)), theLifeOfFriarS5Put);

		findTheWritings.addStep(and(inZamarokianTempleF2, nor(fromMisthalinToMorytania, fromMisthalinToMorytaniaS3)), getFromMisthalinToMorytania);
		findTheWritings.addStep(and(inZamarokianTempleF2, not(fromMisthalinToMorytaniaS3)), fromMisthalinToMorytaniaS3Put);

		findTheWritings.addStep(and(inZamarokianTempleF2, nor(piousProceedings, piousProceedingsS6)), getPiousProceedings);
		findTheWritings.addStep(and(inZamarokianTempleF2, not(piousProceedingsS6)), piousProceedingsS6Put);

		steps.put(38, findTheWritings.puzzleWrapStep());

		var talkToIvanAfterBookPuzzle = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan after solving the puzzle.");
		var cTalkToIvanTempleAfterBookPuzzle = new ConditionalStep(this, climbUpTemple1);
		cTalkToIvanTempleAfterBookPuzzle.addStep(inZamarokianTempleF2, talkToIvanAfterBookPuzzle);
		steps.put(40, cTalkToIvanTempleAfterBookPuzzle);

		var ivandisWritings = new ItemRequirement("Ivandis' writings", 33708);
		var readIvandisWritings = new DetailedQuestStep(this, "Read Ivandis' writings.", ivandisWritings.highlighted());
		var cReadIvandisWritings = new ConditionalStep(this, cTalkToIvanTempleAfterBookPuzzle);
		cReadIvandisWritings.addStep(ivandisWritings, readIvandisWritings);
		steps.put(42, cReadIvandisWritings);

		var talkToIvanAfterReadingIvandisWritings = new NpcStep(this, 15855, new WorldPoint(3417, 3487, 1), "Talk to Ivan after reading Ivandis' writings.");
		var cTalkToIvanAfterReadingIvandisWritings = new ConditionalStep(this, climbUpTemple1);
		cTalkToIvanAfterReadingIvandisWritings.addStep(inZamarokianTempleF2, talkToIvanAfterReadingIvandisWritings);
		steps.put(44, cTalkToIvanAfterReadingIvandisWritings);

		var talkToIvanInPaterdomus = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Talk to Ivan Strom in the Paterdomus dungeon.");
		var cTalkToIvanInPaterdomus = new ConditionalStep(this, talkToIvanInPaterdomus);
		steps.put(46, cTalkToIvanInPaterdomus);

		var combatGear = new ItemRequirement("Combat gear + food", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		var prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);

		// 48 = get there yourself or have him lead the way?
		var talkToIvanInPaterdomus2 = new NpcStep(this, 15855, new WorldPoint(3442, 9898, 0), "Stock up on combat gear and supplies, then head back to the Myreque hideout with Ivan Strom.", blisterwoodFlail, combatGear, prayerPotions);
		talkToIvanInPaterdomus2.addDialogStep("Lead the way.");
		var cTalkToIvanInPaterdomus2 = new ConditionalStep(this, talkToIvanInPaterdomus2);
		steps.put(48, cTalkToIvanInPaterdomus2);

		// TODO: on step 50: do you have to finish talking with veliaf until you say "let's have a look around and see what we can find"? this sets varb 15487 from 0 to 1
		var getToMyrequeHideout = new DetailedQuestStep(this, new WorldPoint(3500, 9864, 0), "Get to the Myreque hideout");
		var myrequeHideout = new Zone(new WorldPoint(3485, 9879, 0), new WorldPoint(3516, 9853, 0));
		var inMyrequeHideout = new ZoneRequirement(myrequeHideout);
		var anyPickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		var investigateHole = new ObjectStep(this, 61193, new WorldPoint(3505, 9857, 0), "Investigate the blockage to the south of the hideout.");
		var cInvestigateHole = new ConditionalStep(this, getToMyrequeHideout);
		cInvestigateHole.addStep(inMyrequeHideout, investigateHole);
		steps.put(50, cInvestigateHole);

		// 15486 = has talked about pickaxe
		// 15469 = has received pickaxe
		var canReceivePickaxeFromIvan = new VarbitRequirement(15469, 0);
		var getPickaxe = new NpcStep(this, 15855, new WorldPoint(3505, 9861, 0), "Ask Ivan Strom for a pickaxe", anyPickaxe);
		var mineHole = new ObjectStep(this, 61194, new WorldPoint(3505, 9857, 0), "Mine the blockage to the south of the hideout.", anyPickaxe);
		var cMineHole = new ConditionalStep(this, getToMyrequeHideout);
		cMineHole.addStep(and(inMyrequeHideout, anyPickaxe), mineHole);
		cMineHole.addStep(and(inMyrequeHideout, canReceivePickaxeFromIvan), getPickaxe);
		cMineHole.addStep(and(inMyrequeHideout), mineHole);
		steps.put(52, cMineHole);

		var headThroughHole = new ObjectStep(this, 61195, new WorldPoint(3505, 9857, 0), "Head through the cave entrance to the south of the hideout, ready for a fight.", combatGear, blisterwoodFlail, prayerPotions);
		var cHeadThroughHole = new ConditionalStep(this, getToMyrequeHideout);
		cHeadThroughHole.addStep(inMyrequeHideout, headThroughHole);
		steps.put(54, cHeadThroughHole);

		var castleMines = new Zone(new WorldPoint(3119, 7479, 2), new WorldPoint(3088, 7433, 2));
		var inCastleMines = new ZoneRequirement(castleMines);
		var enterTunnel1 = new ObjectStep(this, 61197, new WorldPoint(3117, 7472, 2), "Continue through the tunnel to the north-east.");
		var cWalkThroughCastle = new ConditionalStep(this, cHeadThroughHole);
		cWalkThroughCastle.addStep(inCastleMines, enterTunnel1);
		steps.put(56, cWalkThroughCastle);

		var vampZone = new Zone(new WorldPoint(3196, 7447, 0), new WorldPoint(3164, 7469, 0));
		var inVampZone = new ZoneRequirement(vampZone);
		var killVampsAgain = new NpcStep(this, new int[]{16125, 16126, 16137, 16136, 16137}, "Kill vampyres. Focus on the Vyrewatch Sentinels. Avoid the Blood orb. Lure Vyrewatches into the Blood orbs to deal massive damage to them.", combatGear, prayerPotions, blisterwoodFlail);
		killVampsAgain.setAllowMultipleHighlights(true);
		var cWalkThroughCastle2 = new ConditionalStep(this, cWalkThroughCastle);
		cWalkThroughCastle2.addStep(inVampZone, killVampsAgain);
		steps.put(58, cWalkThroughCastle2);

		var talkToIvanAfterKillingVamps = new NpcStep(this, 15864, new WorldPoint(3178, 7459, 0), "Talk to Ivan after killing the vampyres.");
		var cWalkThroughCastle3 = new ConditionalStep(this, cWalkThroughCastle);
		cWalkThroughCastle3.addStep(inVampZone, talkToIvanAfterKillingVamps);
		steps.put(60, cWalkThroughCastle3);

		var enterEntryXD = new ObjectStep(this, 61205, new WorldPoint(3182, 7470, 0), "Continue through the tunnels.");
		var cWalkThroughCastle4 = new ConditionalStep(this, cWalkThroughCastle3);
		cWalkThroughCastle4.addStep(inVampZone, enterEntryXD);
		steps.put(62, cWalkThroughCastle4);

		var afterVampZone = new Zone(new WorldPoint(3142, 7595, 0), new WorldPoint(3187, 7569, 0));
		var inafterVampZone = new ZoneRequirement(afterVampZone);

		var climbUpStairs = new ObjectStep(this, 61207, new WorldPoint(3147, 7578, 0), "climb up the stairs");
		var cWalkThroughCastle5 = new ConditionalStep(this, cWalkThroughCastle4);
		cWalkThroughCastle5.addStep(inafterVampZone, climbUpStairs);
		steps.put(64, cWalkThroughCastle5);

		var castleEntrance = new Zone(new WorldPoint(3172, 7724, 0), new WorldPoint(3146, 7699, 0));
		var inCastleEntrance = new ZoneRequirement(castleEntrance);

		// TODO(FOR FUTURE ADVENTURERS): Do you _need_ to bring the medallion for this?
		var prayAtShrine = new ObjectStep(this, 61226, new WorldPoint(3168, 7707, 0), "Pray at the shrine to let your Drakan's Medallion teleport you here.", drakanMedallion);
		var needTeleportUnlock = new VarbitRequirement(15470, 0);
		var doSomethingElkse = new DetailedQuestStep(this, "continue!!");
		var cWalkThroughCastle6 = new ConditionalStep(this, cWalkThroughCastle4);
		cWalkThroughCastle6.addStep(and(inCastleEntrance, needTeleportUnlock), prayAtShrine);
		cWalkThroughCastle6.addStep(and(inCastleEntrance), doSomethingElkse);
		steps.put(66, cWalkThroughCastle6);

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
			sampleRequirement
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			energyRestorePotion,
			drakanMedallion
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
			startQuest
		), List.of(
			// sampleRequirement
		)));

		sections.add(new PanelDetails("TODO", List.of(
			todo
		), List.of(
			// sampleRequirement
		)));

		return sections;
	}
}
