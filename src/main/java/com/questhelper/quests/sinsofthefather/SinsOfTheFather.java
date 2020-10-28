package com.questhelper.quests.sinsofthefather;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;

@QuestDescriptor(
        quest = QuestHelperQuest.SINS_OF_THE_FATHER
)
public class SinsOfTheFather extends BasicQuestHelper
{
	DetailedQuestStep startQuest, talkToHameln, talkToCarl, inspectBarrel, talkToCarlAfterBarrel, followCarl, killKroy, destroyLab, talkToVeliafAfterKroy, talkToVeliafInPater,
		talkToIvan, listenToMeeting, talkToIvanAfterMeeting, talkToIvanAfterTrek, talkToVeliafInBoatHouse, travelToGraveyard, talkToVeliafInGraveyard, talkToVanescula,
		talkToVanesculaAfterPuzzle, talkToVanesculaAfterTeam, talkToSafaalanInLab, goWithSafalaanIntoDeepLab, killBloodveld, talkToSafalaanInDeepLab, searchLabBookcase,
		takeBookToSafalaan, talkToVanesculaAfterLab, talkToPolmafi, talkToPolmafiMore, bringUnscentedToVanescula, talkToVeliafForFight, killDamien, talkToVeliafAfterDamien,
		talkToVanesculaAfterDamien, enterDarkmeyer, talkToDesmodus, talkToMordan, talkToMaria, talkToDesmodusAgain, readNote, bringVanesculaLogs, bringVertidaLogs,
		talkToVertidaForFlail, talkToVanesculaWithFlail, talkToSafalaanWithFlail, talkToVanesculaBeforeFight, talkToVanesculaForFight, talkToVeliafAfterFight,
		talkToSafalaanInLab, finishQuest, templeTrek, talkToTeamSteps, valveStep, createFlailSteps, doDoorPuzzle, cutLogs;

	ItemRequirement haemBook, unscentedTop, unscentedLegs, 	unscentedShoes, vyreTop, vyreLegs, vyreShoes, ivandisFlail, blisterwoodFlail, scentedTop, scentedLegs, scentedShoes,
		blisterwood8, axe;

	ConditionForStep has8Logs;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

        steps.put(0, startQuest);
        steps.put(2,startQuest);
        steps.put(4, talkToHameln);
        steps.put(6, talkToCarl);

        steps.put(8, inspectBarrel);
        steps.put(10, inspectBarrel);

        steps.put(12, talkToCarlAfterBarrel);

        steps.put(14, followCarl);

        steps.put(16, killKroy);

        steps.put(18, destroyLab);

        steps.put(20, talkToVeliafAfterKroy);

        steps.put(22, talkToVeliafInPater);

        steps.put(24, talkToVeliafInPater);

        steps.put(26, talkToVeliafInPater);

        steps.put(28, talkToIvan);

        /* CONVERT TO A BASIC STATEMENT */
        steps.put(30, listenToMeeting);

        steps.put(32, listenToMeeting);

        steps.put(34, talkToIvanAfterMeeting);

        steps.put(36,  templeTrek);

        steps.put(38,  talkToIvanAfterTrek);

        steps.put(40,  talkToVeliafInBoatHouse);

        steps.put(42, travelToGraveyard);

        steps.put(44, talkToVeliafInGraveyard);

        steps.put(46, talkToVeliafInGraveyard);

        steps.put(48, talkToVanescula);

        steps.put(50, doDoorPuzzle);

        steps.put(52, talkToVanesculaAfterPuzzle);

        steps.put(54, talkToTeamSteps);

        steps.put(56, talkToVanesculaAfterTeam);

        steps.put(58, talkToSafaalanInLab);

        steps.put(60, goWithSafalaanIntoDeepLab);

        /* Keep arrow on Safalaan in case person leaves. Kinda obvious where the Bloodveld is in the cutscene. */
        steps.put(62, killBloodveld);

        steps.put(64, talkToSafalaanInDeepLab);

        steps.put(66, searchLabBookcase);

        steps.put(68, takeBookToSafalaan);

        steps.put(70, talkToVanesculaAfterLab);

        steps.put(72, talkToPolmafi);
        steps.put(74, talkToPolmafi);

        steps.put(76, talkToPolmafiMore);

        steps.put(78, bringUnscentedToVanescula);

        steps.put(80, talkToVeliafForFight);
        steps.put(82, talkToVeliafForFight);

        steps.put(84, killDamien);

        steps.put(86, talkToVeliafAfterDamien);

        steps.put(88, talkToVanesculaAfterDamien);

        steps.put(90, enterDarkmeyer);

        steps.put(92, talkToDesmodus);

        steps.put(94, talkToMordan);

        steps.put(96, talkToMaria);

        steps.put(98, talkToMaria);

        steps.put(100, talkToDesmodusAgain);

        // TODO: Good conditional to add, 'if note, show read'
        steps.put(102, readNote);

        steps.put(104, valveStep);
        steps.put(106, steps.get(104));
        steps.put(108, steps.get(104));
        steps.put(110, steps.get(104));

        ConditionalStep getLogs = new ConditionalStep(this, cutLogs);
        getLogs.addStep(has8Logs, bringVanesculaLogs);

        steps.put(112, getLogs);

		ConditionalStep bringItemsToVertida = new ConditionalStep(this, cutLogs);
		bringItemsToVertida.addStep(has8Logs, bringVertidaLogs);
        steps.put(114, bringItemsToVertida);

        steps.put(116, talkToVertidaForFlail);

        steps.put(118, createFlailSteps);

        steps.put(120, talkToVanesculaWithFlail);

        steps.put(122, talkToSafalaanWithFlail);

        steps.put(124, talkToVanesculaBeforeFight);

        steps.put(126, talkToVanesculaForFight);

        steps.put(128, talkToVeliafForFight);

        steps.put(130, talkToVeliafForFight);

        // TODO: This could actually be in the Myreque hideout. Need to verify.
        steps.put(132, talkToVeliafAfterFight);

        steps.put(134, finishQuest);

        steps.put(136, finishQuest);

        return steps;
    }

	private void setupZones()
	{

	}

	public void setupItemRequirements()
	{
		haemBook = new ItemRequirement("Haemalchemy volume 2", ItemID.HAEMALCHEMY_VOLUME_2);
		haemBook.setTip("If you lost the book, search the bookshelf in the room west of Safalaan to get it back");

		vyreTop = new ItemRequirement("Vyrewatch top", ItemID.VYREWATCH_TOP);
		vyreTop.setTip("You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp");
		vyreLegs = new ItemRequirement("Vyrewatch legs", ItemID.VYREWATCH_LEGS);
		vyreLegs.setTip("You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp");
		vyreShoes = new ItemRequirement("Vyrewatch shoes", ItemID.VYREWATCH_SHOES);
		vyreShoes.setTip("You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp");

		unscentedTop =  new ItemRequirement("Vyre noble top unscented", ItemID.VYRE_NOBLE_TOP_UNSCENTED);
		unscentedTop.setTip("You can get a replacement from a chest in Old Man Ral's basement");
		unscentedLegs = new ItemRequirement("Vyre noble legs unscented", ItemID.VYRE_NOBLE_LEGS_UNSCENTED);
		unscentedLegs.setTip("You can get a replacement from a chest in Old Man Ral's basement");
		unscentedShoes = new ItemRequirement("Vyre noble shoes unscented", ItemID.VYRE_NOBLE_SHOES_UNSCENTED);
		unscentedShoes.setTip("You can get a replacement from a chest in Old Man Ral's basement");

		ivandisFlail = new ItemRequirement("Ivandis flail", ItemID.IVANDIS_FLAIL, 1, true);

		scentedTop =  new ItemRequirement("Vyre noble top", ItemID.VYRE_NOBLE_TOP, 1, true);
		scentedTop.setTip("You can get a replacement from a chest in Old Man Ral's basement");
		scentedLegs = new ItemRequirement("Vyre noble legs", ItemID.VYRE_NOBLE_LEGS, 1, true);
		scentedLegs.setTip("You can get a replacement from a chest in Old Man Ral's basement");
		scentedShoes = new ItemRequirement("Vyre noble shoes", ItemID.VYRE_NOBLE_SHOES, 1, true);
		scentedShoes.setTip("You can get a replacement from a chest in Old Man Ral's basement");

		blisterwood8 = new ItemRequirement("Blisterwood logs", ItemID.BLISTERWOOD_LOGS, 8);

		blisterwoodFlail = new ItemRequirement("Blisterwood flail", ItemID.BLISTERWOOD_FLAIL);
		blisterwoodFlail.setTip("You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old Man Ral's basement");

		axe	= new ItemRequirement("Any axe", ItemCollections.getAxes());
	}

	public void setupConditions()
	{
		has8Logs = new ItemRequirementCondition(blisterwood8);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Talk to Veliaf to start the quest.");
		startQuest.addDialogStep("Yes.");

		talkToHameln = new NpcStep(this, NpcID.HAMELN_THE_JESTER_9505, new WorldPoint(3736, 3316, 0),
			"Go to the church and talk Hameln the Jester about how his friend fell ill.");
		talkToHameln.addDialogStep("I'd better get going.");
		talkToHameln.addDialogStep("Do you know how he fell ill?");

		talkToCarl = new NpcStep(this, NpcID.CARL_9767, new WorldPoint(3736, 3316, 0),
			"Go to The Rat & Bat pub south east of the church and speak to Carl.");
		talkToCarl.addDialogStep("Where do you get your Bloody Bracers from?");

		inspectBarrel = new ObjectStep(this, ObjectID.BARREL_37980, new WorldPoint(3749, 3291, 0),
			"Inspect the barrel south of the Rat & Bat Pub.");

		talkToCarlAfterBarrel = new NpcStep(this, NpcID.CARL, new WorldPoint(3736, 3316, 1),
			"Go to The Rat & Bat pub south east of the church and speak to Carl.");

		followCarl = new ObjectStep(this, ObjectID.BARREL_37980, new WorldPoint(3749, 3291, 1),
			"Follow Carl, hiding behind objects when he turns around.");

		killKroy = new NpcStep(this, 9560, new WorldPoint(6672, 4165, 1),
			"Kill Kroy.");

		destroyLab = new ObjectStep(this, ObjectID.LAB_TABLE_37983, new WorldPoint(3729, 9760, 0),
			"Destroy both the Lab table.");

		talkToVeliafAfterKroy = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Go talk to Veliaf.");

		talkToVeliafInPater = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3438, 9897, 0),
			"Go to Paterdomus and speak to Veliaf.");
		talkToVeliafInPater.addDialogStep("I see. So that's why you wanted to keep him safe, Veliaf?");

		talkToIvan = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
			"Speak to Ivan Strom south of Fenkenstrain's Castle.");

		listenToMeeting = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
			"Listen to the meeting.");

		talkToIvanAfterMeeting = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3444, 3485, 0),
			"Speak to Ivan Strom outside the east entrance of Paterdomus.");

		talkToIvanAfterTrek = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3486, 3241, 0),
			"Finish speaking to Ivan Strom.");

		talkToVeliafInBoatHouse = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3529, 3168, 0),
			"Talk to Veliaf at the boat house in the south of Burgh de Rott.");

		travelToGraveyard = new ObjectStep(this, ObjectID.BOAT_17955, new WorldPoint(3522, 3169, 0),
			"Get into the boat to the Icyene Graveyard.");
		travelToGraveyard.addDialogStep("Icyene Graveyard.");


		talkToVeliafInGraveyard = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3684, 3181, 0),
			"Finish talking to Veliaf and Safalaan.");

		talkToVanescula = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3705, 3188, 0),
			"Talk with Vanescula Drakan.");

		talkToVanesculaAfterPuzzle = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3705, 3188, 0),
			"Finish the cutscene in the Icyene Graveyard.");


		talkToVanesculaAfterTeam = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Speak to Vanescula in the Icyene Graveyard.");

		talkToSafalaanInLab = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3643, 3305, 0),
			"Speak to Safalaan at the Lab. The easiest way to get here is to have a Vyrewatch send you to the mines, and head south from there.");

		goWithSafalaanIntoDeepLab = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3643, 3305, 0),
			"Go with Safalaan into the deeper Lab.");
		goWithSafalaanIntoDeepLab.addDialogStep("Shall we get going then?");
		goWithSafalaanIntoDeepLab.addDialogStep("Let's go.");

		killBloodveld = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3643, 3305, 0),
			"Defeat the Mutated Bloodveld (lvl-123).");

		talkToSafalaanInDeepLab = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3611, 9737, 0),
			"Finish speaking with Safalaan in the Lab.");

		searchLabBookcase = new ObjectStep(this, ObjectID.BOOKSHELF_38017, new WorldPoint(3589, 9745, 0),
			"Search the bookshelf in the room west of Safalaan in the Lab.");
		searchLabBookcase.addDialogStep("I'll be back soon.");


		takeBookToSafalaan = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3611, 9737, 0),
			"Go show Safalaan the Haemalchemy book 2 you found.", haemBook);
		takeBookToSafalaan.addDialogStep("Sure. Here you go.");

		talkToVanesculaAfterLab = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Speak to Vanescula in the Icyene Graveyard. Take the boat south of Burgh de Rott to get there.");
		talkToVanesculaAfterLab.addDialogStep("Okay. I'll see you soon.");
		talkToVanesculaAfterLab.addDialogStep("Icyene Graveyard.");

		talkToPolmafi = new NpcStep(this, NpcID.POLMAFI_FERDYGRIS_9554, new WorldPoint(3605, 3215, 0),
			"Bring a Vyrewatch disguise to Polmafi in the Meiyerditch hideout in Old Man Ral's basement.", vyreTop, vyreLegs, vyreShoes);
		talkToPolmafi.addDialogStep("Here you go.");

		talkToPolmafiMore =  new NpcStep(this, 9554, new WorldPoint(3600, 9611, 0),
			"Finish speaking to Polmafi in the Meiyerditch hideout.");
		talkToPolmafiMore.addDialogStep("Here you go.");
		talkToPolmafi.addSubSteps(talkToPolmafiMore);

		bringUnscentedToVanescula = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the Vyre noble outfit",
			unscentedTop, unscentedLegs, unscentedShoes);
		bringUnscentedToVanescula.addDialogStep("Icyene Graveyard.");

		talkToVeliafForFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Talk to Veliaf outside the church in Slepe.",
			unscentedTop, unscentedLegs, unscentedShoes, ivandisFlail);
		talkToVeliafForFight.addDialogSteps("Slepe.", "Let's do this.");

		killDamien = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Kill Damien Leucurte (lvl-204). He can poison you. If you leave the fight, talk to Veliaf in Slepe to return to it.",
			unscentedTop, unscentedLegs, unscentedShoes, ivandisFlail);
		killDamien.addDialogSteps("Slepe.", "Let's do this.");

		talkToVeliafAfterDamien = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3720, 3357, 1),
			"Talk to Veliaf in Slepe.");

		talkToVanesculaAfterDamien = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the Vyre noble outfit",
			scentedTop, scentedLegs, scentedShoes);
		talkToVanesculaAfterDamien.addDialogSteps("I'll see you there.", "Icyene Graveyard.");

		enterDarkmeyer = new ObjectStep(this, ObjectID.CRACKED_WALL, new WorldPoint(3627, 3329, 0),
			"Go to Darkmeyer. You can take the boat to Meiyerditch, talk to a Vyrewatch to be sent to the mines, get out and you'll be right next to the entrance.",
			scentedTop, scentedLegs, scentedShoes);
		enterDarkmeyer.addDialogSteps("Meiyerditch.", "Send me to the mines.");

		talkToDesmodus = new NpcStep(this, NpcID.DESMODUS_LASIURUS, new WorldPoint(3612, 3362, 0),
			"Speak to Desmodus Lasiurus outside the Aboretum in Darkmeyer.",
			scentedTop, scentedLegs, scentedShoes);

		talkToMordan = new NpcStep(this, NpcID.MORDAN_NIKAZSI, new WorldPoint(3662, 3347, 0),
			"Speak to Mordan Mikazsi in lower Darkmeyer.", scentedTop, scentedLegs, scentedShoes);

		talkToMaria = new NpcStep(this, NpcID.MARIA_GADDERANKS, new WorldPoint(3618, 3378, 0),
			"Speak to Maria Gadderanks in the jail north of the Aboretum in Darkmeyer.",
			scentedTop, scentedLegs, scentedShoes);

		talkToDesmodusAgain = new NpcStep(this, NpcID.DESMODUS_LASIURUS, new WorldPoint(3612, 3362, 0),
			"Speak to Desmodus Lasiurus outside the Aboretum in Darkmeyer.",
			scentedTop, scentedLegs, scentedShoes);

		readNote = new ObjectStep(this, ObjectID.SHELVES_37999, new WorldPoint(3626, 3359, 0),
			"Search the shelves in the Aboretum in Darkmeyer. Read the Old Note you get.",
			scentedTop, scentedLegs, scentedShoes);

		bringVanesculaLogs = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the 8 Blisterwood logs",
			blisterwood8);
		bringVanesculaLogs.addDialogStep("Icyene Graveyard.");

		bringVertidaLogs = new NpcStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3605, 3215, 0),
			"Go speak to Vertida in Old Man Ral's basement with the 8 Blisterwood logs",
			blisterwood8);
		bringVertidaLogs.addDialogStep("Meiyerditch.");

		talkToVertidaForFlail = new NpcStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3605, 3215, 0),
			"Speak to Vertida Sefalatis.");

		talkToVanesculaWithFlail = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the Blisterwood Flail", blisterwoodFlail);
		talkToVanesculaWithFlail.addDialogStep("Icyene Graveyard.");

		talkToSafalaanWithFlail = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3719, 3215, 0),
			"Speak to Safalaan north of the Icyene Graveyard.", blisterwoodFlail);
		talkToSafalaanWithFlail.addDialogStep("Icyene Graveyard.");

		talkToVanesculaBeforeFight = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Speak to Vanescula in the Icyene Graveyard.", blisterwoodFlail);
		talkToVanesculaBeforeFight.addDialogStep("Icyene Graveyard.");

		talkToVanesculaForFight = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Prepare for a challenging fight. Speak to Vanescula to enter the fight.", blisterwoodFlail);
		talkToVanesculaForFight.addDialogSteps("Icyene Graveyard.", "I'm ready.");

		talkToVeliafForFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3707, 3188, 0),
			"Prepare for a challenging fight. Speak to Veliaf to enter the fight.", blisterwoodFlail);
		talkToVeliafForFight.addDialogSteps("Icyene Graveyard.", "Let's go.");

		talkToVeliafAfterFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3707, 3188, 0),
			"Speak to Veliaf in the Icyene graveyard to re-enter the cutscene.");

		talkToVeliafAfterFight.addDialogSteps("Icyene Graveyard.", "Let's go.");
		talkToVanesculaBeforeFight.addSubSteps(talkToVanesculaForFight, talkToVeliafForFight, talkToVeliafAfterFight);

		finishQuest = new NpcStep(this, NpcID.VELIAF_HURTZ_9522, new WorldPoint(3605, 3215, 0),
			"Speak to Veliaf in the Myreque Hideout.");

		cutLogs = new ObjectStep(this, ObjectID.BLISTERWOOD_TREE, new WorldPoint(3633, 3359, 0),
			"Gather 8 logs from the Blisterwood tree.", scentedTop, scentedLegs, scentedShoes, blisterwood8, axe);

		templeTrek = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3444, 3485, 0),
			"Speak to Ivan Strom outside the east entrance of Paterdomus to go temple treking with him.");
		talkToTeamSteps = new DetailedQuestStep(this, "Convince the Myreque to take on Drakan."); // new SpeakToTeamStep(this);
		valveStep = new DetailedQuestStep(this, "Solve the valve puzzle."); // new ValveStep(this);
		createFlailSteps = new DetailedQuestStep(this, "Create the blisterwood flail.", blisterwoodFlail); // new CreateFlailStep(this);
		doDoorPuzzle = new DoorPuzzleStep(this);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList());
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList());
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Kroy (level 133)", "Vampyre juveniles (level 122 and 119)", "Nail beasts (level 143 and 67)", "Mutated bloodveld (level 123)", "Damien Leucurte (level 204)", "Vanstrom Klause (level 413)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating Slepe",
			new ArrayList<>(Arrays.asList(startQuest, talkToHameln, talkToCarl, inspectBarrel, followCarl, killKroy, destroyLab, talkToVeliafAfterKroy))));
		allSteps.add(new PanelDetails("Helping the Myreque",
			new ArrayList<>(Arrays.asList(talkToVeliafInPater, talkToIvan, listenToMeeting, talkToIvanAfterMeeting, templeTrek, talkToIvanAfterTrek, talkToVeliafInBoatHouse,
				travelToGraveyard, talkToVeliafInGraveyard, talkToVanescula, doDoorPuzzle, talkToTeamSteps, talkToVanesculaAfterTeam))));
		allSteps.add(new PanelDetails("Investigating the lab",
			new ArrayList<>(Arrays.asList(talkToSafaalanInLab, goWithSafalaanIntoDeepLab, killBloodveld,
			talkToSafalaanInDeepLab, searchLabBookcase, takeBookToSafalaan))));
		allSteps.add(new PanelDetails("Making a disguise",
			new ArrayList<>(Arrays.asList(talkToVanesculaAfterLab, talkToPolmafi, bringUnscentedToVanescula, talkToVeliafForFight, killDamien, talkToVeliafAfterDamien,
				talkToVanesculaAfterDamien))));
		allSteps.add(new PanelDetails("Infiltrating Darkmeyer",
			new ArrayList<>(Arrays.asList(enterDarkmeyer, talkToDesmodus, talkToMordan, talkToMaria, talkToDesmodusAgain, readNote, valveStep, cutLogs, bringVanesculaLogs, bringVertidaLogs,
				talkToVertidaForFlail, createFlailSteps))));
		allSteps.add(new PanelDetails("Taking on Drakan",
			new ArrayList<>(Arrays.asList(talkToVanesculaWithFlail, talkToSafalaanWithFlail, talkToVanesculaBeforeFight, finishQuest))));


		return allSteps;
	}
}
