package com.questhelper.quests.akingdomdivided;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
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
	QuestStep talkToMartinHolt, talkToCommanderFullore, searchHomeForClue, talkToTomasLawry, searchHome, goToLovaBar,
		talkToCabinBoyHerbert, fightJudgeofYama, talkToCommanderFullore2, talkToMartinHolt2,
		teleportArcheio, pickpocketIsotoria, openRosesDiaryCase, talkToMartinHolt3, readRosesNote,
		talkToMartinHolt4;

	Requirement hasNotTalkedToTomasLowry, hasTalkedToTomasLowry, hasReadReceipt, hasBluishKey;

	ItemRequirement meleeCombatGear, bluishKey, rosesDiary, rosesNote;

	ZoneRequirement inArceuusLibrary;

	Zone arceuusLibrary;

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

		ConditionalStep homeSearch = new ConditionalStep(this, searchHomeForClue);
		homeSearch.addStep(new Conditions(hasReadReceipt, hasNotTalkedToTomasLowry), talkToTomasLawry);
		homeSearch.addStep(new Conditions(hasReadReceipt, hasTalkedToTomasLowry), goToLovaBar);

		steps.put(10, homeSearch);
		steps.put(14, talkToCabinBoyHerbert);
		// fight after cutscene
		// TODO: add portal stuff to fight boss
		steps.put(18, talkToCabinBoyHerbert);
		steps.put(20, talkToCabinBoyHerbert);

		//after fight
		steps.put(24, talkToCommanderFullore2);
		steps.put(26, talkToMartinHolt3);

		ConditionalStep gettingRosesDiary = new ConditionalStep(this, teleportArcheio);
		gettingRosesDiary.addStep(new Conditions(hasBluishKey, inArceuusLibrary), openRosesDiaryCase);
		gettingRosesDiary.addStep(inArceuusLibrary, pickpocketIsotoria);

		steps.put(28, gettingRosesDiary);
		steps.put(30, gettingRosesDiary);
		steps.put(32, gettingRosesDiary);

		// after getting roses diary
		steps.put(34, talkToMartinHolt3);
		steps.put(36, talkToMartinHolt3);
		steps.put(38, readRosesNote);
		steps.put(40, talkToMartinHolt4);

		// start of chapter 2
		steps.put(42, talkToMartinHolt4);

		return steps;
	}

	public void loadZones()
	{
		arceuusLibrary = new Zone(new WorldPoint(1535, 10236, 0), new WorldPoint(1571, 10213, 0));
	}

	public void setupItemRequirements()
	{
		meleeCombatGear = new ItemRequirement("Melee Combat gear + food", -1, -1);
		bluishKey = new ItemRequirement("Bluish Key", ItemID.BLUISH_KEY);
		rosesDiary = new ItemRequirement("Rose's diary", ItemID.ROSES_DIARY);
		rosesNote = new ItemRequirement("Rose's note", ItemID.ROSES_NOTE);
	}

	public void setupConditions()
	{
		hasReadReceipt = new VarbitRequirement(12298, 1);
		hasTalkedToTomasLowry = new VarbitRequirement(12298, 1);
		hasNotTalkedToTomasLowry = new VarbitRequirement(12298, 0);
		inArceuusLibrary = new ZoneRequirement(arceuusLibrary);
		hasBluishKey = new ItemRequirements(bluishKey);
	}

	public void setupSteps()
	{
		talkToMartinHolt = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt to start the quest.");
		talkToMartinHolt.addDialogStep("Yes.");
		talkToCommanderFullore = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1614, 3668, 0), "Talk to Commander Fullore.");
		talkToCommanderFullore.addDialogStep("Let's get going.");

		searchHomeForClue = new DetailedQuestStep(this, new WorldPoint(1676, 3679, 0), "Search the Councillors home to find a receipt and read it.");
		talkToTomasLawry = new NpcStep(this, NpcID.TOMAS_LAWRY, new WorldPoint(1677, 3682, 0), "Speak with Tomas Lawry at the ground floor of the Councillors home.");
		talkToTomasLawry.addDialogStep("I've found something that might be useful.");
		goToLovaBar = new NpcStep(this, NpcID.FUGGY, new WorldPoint(1569, 3758, 0), "Talk to Fuggy in the Lovakengj pub.");
		goToLovaBar.addDialogStep("Had any councillors stay here recently?");
		talkToCabinBoyHerbert = new NpcStep(this, NpcID.CABIN_BOY_HERBERT, new WorldPoint(1826, 3691, 0), "Talk to Cabin Boy Herbert next to Veos's ship in Port Piscarilius.", meleeCombatGear);
		talkToCabinBoyHerbert.addText("Prepare to fight Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the gaps of the fire waves to approach the boss.");
		talkToCabinBoyHerbert.addDialogStep("I'm looking for a councillor.");

		talkToCommanderFullore2 = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1604, 3654, 0), "Talk to Commander Fullore just south of the Kourend Castle.");
		talkToMartinHolt2 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again.");

		teleportArcheio = new NpcStep(this, NpcID.ARCHEIO, new WorldPoint(1625, 3808, 0), "Have Archeio in the Arceuus library teleport you.");
		pickpocketIsotoria = new NpcStep(this, NpcID.ISTORIA_11113, new WorldPoint(1551, 10222,0), "Pickpocket Istoria for the bluish key.");
		openRosesDiaryCase = new ObjectStep(this, ObjectID.DISPLAY_CASE_41811, "Search the display case in the south east corner of the room to get Rose's diary", bluishKey);

		talkToMartinHolt3 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again.", rosesDiary);
		readRosesNote = new DetailedQuestStep(this,"Read Rose's note.", rosesNote.highlighted());
		talkToMartinHolt4 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again.", rosesNote);

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(new ItemRequirement("Ring of Charos", ItemID.RING_OF_CHAROS));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(new ItemRequirement("Ring of Charos", ItemID.RING_OF_CHAROS));
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
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToMartinHolt, talkToCommanderFullore, searchHomeForClue, talkToTomasLawry)));
		return allSteps;
	}
}
