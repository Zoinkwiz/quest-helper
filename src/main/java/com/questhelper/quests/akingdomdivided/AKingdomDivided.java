package com.questhelper.quests.akingdomdivided;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;


@QuestDescriptor(
	quest = QuestHelperQuest.A_KINGDOM_DIVIDED
)

public class AKingdomDivided extends BasicQuestHelper
{
	QuestStep talkToMartinHolt, talkToCommanderFullore, talkToTomasLawry, goToLovakengjPub,
		talkToCabinBoyHerbert, fightJudgeofYama, talkToCommanderFullore2, talkToMartinHolt2,
		teleportArcheio, pickpocketIstoria, openRosesDiaryCase, talkToMartinHolt3, readRosesNote,
		talkToMartinHolt4, enterHomeForClueSearch, inspectReceipt, getReceipt, goDownCouncillorsHomeF3toF2,
		goDownCouncillorsHomeF2toF1, goUpCouncillorsHomeF1toF2, goUpCouncillorsHomeF2toF3, judgeOfYamaDetailedStep,
		enterJudgeOfYamaFightPortal, talkToMartinHolt5;

	Requirement hasTalkedToTomasLowry, hasBluishKey, inArceuusLibraryHistoricalArchive, inCouncillorsHouseF1, inCouncillorsHouseF2,
		inCouncillorsHouseF3, hasReceipt, hasInspectedReceipt, freeInventorySlots, judgeOfYamaNearby;

	ItemRequirement meleeCombatGear, bluishKey, rosesDiary, rosesNote, receipt, kharedstsmemoirs;

	Zone arceuusLibraryHistoricalArchive, councillorsHouseF1, councillorsHouseF2, councillorsHouseF3;

	QuestStep notYetImplemented;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMartinHolt);
		steps.put(2, talkToMartinHolt);
		steps.put(4, talkToCommanderFullore);
		steps.put(6, talkToCommanderFullore);
		steps.put(8, talkToCommanderFullore);

		ConditionalStep homeSearch = new ConditionalStep(this, enterHomeForClueSearch);

		homeSearch.addStep(hasTalkedToTomasLowry, goToLovakengjPub);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF3, hasInspectedReceipt), goDownCouncillorsHomeF3toF2);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF2, hasInspectedReceipt), goDownCouncillorsHomeF2toF1);
		homeSearch.addStep(hasInspectedReceipt, talkToTomasLawry);

		homeSearch.addStep(new Conditions(inCouncillorsHouseF3, new Conditions(LogicType.NAND, hasReceipt)), goDownCouncillorsHomeF3toF2);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF1, new Conditions(LogicType.NAND, hasReceipt)), goUpCouncillorsHomeF1toF2);
		homeSearch.addStep(new Conditions(hasReceipt, new Conditions(LogicType.NAND, hasInspectedReceipt)), inspectReceipt);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF2, new Conditions(LogicType.NAND, hasReceipt)), getReceipt);

		steps.put(10, homeSearch);
		steps.put(12, homeSearch);
		steps.put(14, talkToCabinBoyHerbert);

		ConditionalStep doTheFightWithYama = new ConditionalStep(this, fightJudgeofYama);
		doTheFightWithYama.addStep(new Conditions(LogicType.NAND, judgeOfYamaNearby), enterJudgeOfYamaFightPortal);

		steps.put(16, doTheFightWithYama);
		steps.put(18, doTheFightWithYama);
		// after yama fight
		steps.put(20, talkToCommanderFullore2);
		steps.put(22, talkToCommanderFullore2);
		steps.put(24, talkToCommanderFullore2);
		steps.put(26, talkToMartinHolt2);

		ConditionalStep gettingRosesDiary = new ConditionalStep(this, teleportArcheio);
		gettingRosesDiary.addStep(new Conditions(inArceuusLibraryHistoricalArchive, hasBluishKey), openRosesDiaryCase);
		gettingRosesDiary.addStep(inArceuusLibraryHistoricalArchive, pickpocketIstoria);

		steps.put(28, gettingRosesDiary);
		steps.put(30, gettingRosesDiary);
		steps.put(32, gettingRosesDiary);

		// after getting roses diary
		steps.put(34, talkToMartinHolt3);
		steps.put(36, talkToMartinHolt3);
		steps.put(38, readRosesNote);
		steps.put(40, talkToMartinHolt4);

		steps.put(42, talkToMartinHolt5);
		steps.put(44, notYetImplemented);

		return steps;
	}

	public void loadZones()
	{
		arceuusLibraryHistoricalArchive = new Zone(new WorldPoint(1535, 10236, 0), new WorldPoint(1571, 10213, 0));
		councillorsHouseF1 = new Zone(new WorldPoint(1670, 3684, 0), new WorldPoint(1683, 3677, 0));
		councillorsHouseF2 = new Zone(new WorldPoint(1670, 3684, 1), new WorldPoint(1683, 3677, 1));
		councillorsHouseF3 = new Zone(new WorldPoint(1670, 3684, 2), new WorldPoint(1683, 3677, 2));
	}

	public void setupItemRequirements()
	{
		meleeCombatGear = new ItemRequirement("Melee Combat gear + food", -1, -1);
		bluishKey = new ItemRequirement("Bluish Key", ItemID.BLUISH_KEY);
		rosesDiary = new ItemRequirement("Rose's diary", ItemID.ROSES_DIARY);
		rosesNote = new ItemRequirement("Rose's note", ItemID.ROSES_NOTE);
		receipt = new ItemRequirement("Receipt", ItemID.RECEIPT_25793);
		freeInventorySlots = new ItemRequirement("Free inventory slot", -1, 1);
		kharedstsmemoirs = new ItemRequirement("Kharedst's Memoirs for teleports", ItemID.KHAREDSTS_MEMOIRS);
	}

	public void setupConditions()
	{
		// after speaking with tomas lawry
		// 12302: 0 > 1 (I'm guessing its this one)
		// 12144: 2 > 0
		hasTalkedToTomasLowry = new VarbitRequirement(12302, 1);

		inArceuusLibraryHistoricalArchive = new ZoneRequirement(arceuusLibraryHistoricalArchive);
		judgeOfYamaNearby = new NpcCondition(NpcID.JUDGE_OF_YAMA_10938);
		hasBluishKey = new ItemRequirements(bluishKey);
		inCouncillorsHouseF1 = new ZoneRequirement(councillorsHouseF1);
		inCouncillorsHouseF2 = new ZoneRequirement(councillorsHouseF2);
		inCouncillorsHouseF3 = new ZoneRequirement(councillorsHouseF3);
		hasReceipt = new ItemRequirements(receipt);
		hasInspectedReceipt = new VarbitRequirement(12298, 1);
	}

	public void setupSteps()
	{
		notYetImplemented = new DetailedQuestStep(this, "Quest step not yet implemented.");

		talkToMartinHolt = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt east of Kourend Castle.");
		talkToMartinHolt.addDialogStep("Yes.");
		talkToCommanderFullore = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1614, 3668, 0), "Talk to Commander Fullore in Kourend Castle.");
		talkToCommanderFullore.addDialogStep("Let's get going.");

		goUpCouncillorsHomeF1toF2 = new ObjectStep(this, ObjectID.STAIRCASE_11796, new WorldPoint(1671, 3681, 0), "Climb up the stairs of the Councillors home.");
		goUpCouncillorsHomeF2toF3 = new ObjectStep(this, ObjectID.STAIRCASE_41806, new WorldPoint(1676, 3679, 1), "Climb up the stairs of the Councillors home.");
		goDownCouncillorsHomeF2toF1 = new ObjectStep(this, ObjectID.STAIRCASE_11799, new WorldPoint(1671, 3681, 1), "Climb down the stairs of the Councillors home.");
		goDownCouncillorsHomeF3toF2 = new ObjectStep(this, ObjectID.STAIRCASE_11793, new WorldPoint(1676, 3679, 2), "Climb down the stairs of the Councillors home.");

		getReceipt = new ObjectStep(this, ObjectID.DRAWERS_41795, new WorldPoint(1679, 3680, 1), "Search the drawers for the Receipt.");
		inspectReceipt = new DetailedQuestStep(this, "Inspect the Receipt.", receipt.highlighted());

		talkToTomasLawry = new NpcStep(this, NpcID.TOMAS_LAWRY, new WorldPoint(1677, 3682, 0), "Speak with Tomas Lawry on the ground floor of the Councillors home.");
		talkToTomasLawry.addDialogStep("I've found something that might be useful.");
		goToLovakengjPub = new NpcStep(this, NpcID.FUGGY, new WorldPoint(1569, 3758, 0), "Talk to Fuggy in the pub in southeast Lovakengj.");
		goToLovakengjPub.addDialogStep("Had any councillors stay here recently?");

		enterHomeForClueSearch = new DetailedQuestStep(this, new WorldPoint(1676, 3680, 0), "Searcg the Councillors home east of Kourend castle for clues.");
		enterHomeForClueSearch.addSubSteps(getReceipt, inspectReceipt, goDownCouncillorsHomeF2toF1,
			goDownCouncillorsHomeF3toF2, goUpCouncillorsHomeF1toF2, goUpCouncillorsHomeF2toF3);

		talkToCabinBoyHerbert = new NpcStep(this, NpcID.CABIN_BOY_HERBERT, new WorldPoint(1826, 3691, 0),
			"Prepare to fight the Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the " +
				"gaps of the fire waves to approach the boss.\n\nTalk to Cabin Boy Herbert next to Veos's ship in Port" +
				"Piscarilius to initiate the fight.",
			meleeCombatGear);

		talkToCabinBoyHerbert.addDialogStep("I'm looking for a councillor.");

		fightJudgeofYama = new NpcStep(this, NpcID.JUDGE_OF_YAMA_10938,
			"Fight the Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the gaps of " +
				"the fire waves to approach the boss.", meleeCombatGear);

		enterJudgeOfYamaFightPortal = new ObjectStep(this, ObjectID.PORTAL_41808, new WorldPoint(1823, 3686, 0),
			"Prepare to fight the Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the " +
				"gaps of the fire waves to approach the boss.\n\nEnter the Portal near Veos's ship to fight the Judge of Yama.",
			meleeCombatGear);

		judgeOfYamaDetailedStep = new DetailedQuestStep(this, "Fight the Judge of Yama. This boss uses magic + range " +
			"prayer so melee is required. Run in the gaps of the fire waves to approach the boss.", meleeCombatGear);

		judgeOfYamaDetailedStep.addSubSteps(talkToCabinBoyHerbert, fightJudgeofYama, enterJudgeOfYamaFightPortal);

		talkToCommanderFullore2 = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1604, 3654, 0), "Talk to Commander Fullore just south of the Kourend Castle.");
		talkToMartinHolt2 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again east of the Kourend Castle.");

		teleportArcheio = new NpcStep(this, NpcID.ARCHEIO, new WorldPoint(1625, 3808, 0), "Have Archeio in the Arceuus library teleport you to the Historical Archives.");
		pickpocketIstoria = new NpcStep(this, NpcID.ISTORIA_11113, new WorldPoint(1551, 10222, 0), "Pickpocket Istoria for the bluish key.");
		openRosesDiaryCase = new ObjectStep(this, ObjectID.DISPLAY_CASE_41811, "Search the display case in the south east corner of the room to get Rose's diary", bluishKey);

		talkToMartinHolt3 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again east of the Kourend Castle.", rosesDiary);
		readRosesNote = new DetailedQuestStep(this, "Read Rose's note that fell out of the diary.", rosesNote.highlighted());
		talkToMartinHolt4 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again east of the Kourend Castle.", rosesNote);

		talkToMartinHolt5 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1673, 3580, 0), "Talk to Martin Holt on the north side of the Forthos Ruins.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(meleeCombatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(new ItemRequirement("Kharedst's Memoirs for teleports", ItemID.KHAREDSTS_MEMOIRS));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Judge of Yama (level 168)");

		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 54));
		reqs.add(new SkillRequirement(Skill.THIEVING, 52));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 52));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 50));
		reqs.add(new SkillRequirement(Skill.MINING, 42));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 38));
		reqs.add(new SkillRequirement(Skill.MAGIC, 35));

		reqs.add(new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_QUEEN_OF_THIEVES, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_DEPTHS_OF_DESPAIR, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_ASCENT_OF_ARCEUUS, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_FORSAKEN_TOWER, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.TALE_OF_THE_RIGHTEOUS, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.ARCHITECTURAL_ALLIANCE, QuestState.FINISHED));

		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("The Disgraced Councillor's Escape", Arrays.asList(talkToMartinHolt, talkToCommanderFullore, enterHomeForClueSearch,
			talkToTomasLawry, goToLovakengjPub, judgeOfYamaDetailedStep, talkToCommanderFullore2, talkToMartinHolt2, teleportArcheio, pickpocketIstoria,
			openRosesDiaryCase, talkToMartinHolt3, readRosesNote, talkToMartinHolt4), meleeCombatGear, kharedstsmemoirs, freeInventorySlots));

		allSteps.add(new PanelDetails("Kourend's Last Princess", Arrays.asList(talkToMartinHolt5)));

		return allSteps;
	}
}
