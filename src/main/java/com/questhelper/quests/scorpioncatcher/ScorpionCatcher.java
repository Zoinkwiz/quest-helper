package com.questhelper.quests.scorpioncatcher;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.*;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.steps.*;
import com.questhelper.requirements.conditional.*;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@QuestDescriptor(
		quest = QuestHelperQuest.SCORPION_CATCHER
)
public class ScorpionCatcher extends BasicQuestHelper
{
	ItemRequirement dustyKey, jailKey, scorpionCageEmpty, scorpionCageTaverley, scorpionCageEmptyOrTaverley, scorpionCageTaverleyAndMonastery, scorpionCageFull, food,
            antiDragonShield, antiPoison, teleRunes, gamesNecklace, gloryOrCombatBracelet;
	QuestRequirement fairyRingAccess;
	Zone sorcerersTower3, sorcerersTower2, sorcerersTower1, taverleyDungeon, deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4,
            jailCell, taverleyScorpionRoom, upstairsMonastery, barbarianOutpost;
	ConditionForStep has70Agility, hasScorpionCageEmpty, hasScorpionCageTaverley, hasScorpionCageEmptyOrTaverley, hasScorpionCageTaverleyAndMonastery,
            hasScorpionCageFull, hasDustyKey, inTaverleyDungeon, inDeepTaverleyDungeon, inJailCell, hasJailKey, inSorcerersTower1, inSorcerersTower2,
            inSorcerersTower3, inTaverleyScorpionRoom, inUpstairsMonastery, inBarbarianOutpost;
	QuestStep speakToThormac, speakToSeer1, speakToSeer2, enterTaverleyDungeon, goThroughPipe, killJailerForKey, getDustyFromAdventurer, enterDeeperTaverley,
            searchOldWall, catchTaverleyScorpion, sorcerersTowerLadder0, sorcerersTowerLadder1, sorcerersTowerLadder2, enterMonastery, catchMonasteryScorpion,
            catchOutpostScorpion, enterOutpost, returnToThormac;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep beginQuest = new ConditionalStep(this, sorcerersTowerLadder0);
		beginQuest.addStep(inSorcerersTower1, sorcerersTowerLadder1);
		beginQuest.addStep(inSorcerersTower2, sorcerersTowerLadder2);
		beginQuest.addStep(inSorcerersTower3, speakToThormac);

		ConditionalStep speakToASeer = new ConditionalStep(this, speakToSeer1);

		ConditionalStep scorpions = new ConditionalStep(this, enterTaverleyDungeon);
		scorpions.addStep(hasScorpionCageTaverley, speakToSeer2); // possibly not necessary
		scorpions.addStep(new Conditions(hasScorpionCageEmpty, inTaverleyScorpionRoom), catchTaverleyScorpion);
		scorpions.addStep(new Conditions(hasScorpionCageEmpty, inDeepTaverleyDungeon), searchOldWall);
		scorpions.addStep(new Conditions(hasScorpionCageEmpty, inTaverleyDungeon, has70Agility), goThroughPipe);
		scorpions.addStep(new Conditions(hasScorpionCageEmpty, inTaverleyDungeon, hasDustyKey), enterDeeperTaverley);
		scorpions.addStep(new Conditions(hasScorpionCageEmpty, inTaverleyDungeon, new Conditions(LogicType.OR, inJailCell, hasJailKey)), getDustyFromAdventurer);
		scorpions.addStep(new Conditions(hasScorpionCageEmpty, inTaverleyDungeon), killJailerForKey);
		scorpions.addStep(hasScorpionCageTaverley, enterMonastery);
		scorpions.addStep(new Conditions(hasScorpionCageTaverley, inUpstairsMonastery), catchMonasteryScorpion);
		scorpions.addStep(new Conditions(hasScorpionCageTaverleyAndMonastery, inBarbarianOutpost), catchOutpostScorpion);
		scorpions.addStep(hasScorpionCageTaverleyAndMonastery, enterOutpost);
		scorpions.addStep(hasScorpionCageFull, returnToThormac);

		steps.put(0, beginQuest);
		steps.put(1, speakToASeer);
		steps.put(2, scorpions);
		steps.put(3, scorpions);

		return steps;
	}

	private void setupZones()
	{
		sorcerersTower3 = new Zone(new WorldPoint(2699, 3408, 3), new WorldPoint(2705, 3402, 3));
		sorcerersTower2 = new Zone(new WorldPoint(2699, 3408, 2), new WorldPoint(2705, 3402, 2));
		sorcerersTower1 = new Zone(new WorldPoint(2699, 3408, 1), new WorldPoint(2705, 3402, 1));

		taverleyDungeon = new Zone(new WorldPoint(2816, 9668, 0), new WorldPoint(2973, 9855, 0));
		deepTaverleyDungeon1 = new Zone(new WorldPoint(2816, 9856, 0), new WorldPoint(2880, 9760, 0));
		deepTaverleyDungeon2 = new Zone(new WorldPoint(2880, 9760, 0), new WorldPoint(2907, 9793, 0));
		deepTaverleyDungeon3 = new Zone(new WorldPoint(2889, 9793, 0), new WorldPoint(2923, 9815, 0));
		deepTaverleyDungeon4 = new Zone(new WorldPoint(2907, 9772, 0), new WorldPoint(2928, 9793, 0));
		jailCell = new Zone(new WorldPoint(2928, 9683, 0), new WorldPoint(2934, 9689, 0));
		taverleyScorpionRoom = new Zone(new WorldPoint(2874, 9798, 0), new WorldPoint(2880, 9793, 0));

		upstairsMonastery = new Zone(new WorldPoint(3043, 3499, 1), new WorldPoint(3060, 3481, 1));

		barbarianOutpost = new Zone(new WorldPoint(2546, 3573, 0), new WorldPoint(2555, 3560, 0));
	}

	private void setupItemRequirements()
	{
		dustyKey = new ItemRequirement("Dusty Key", ItemID.DUSTY_KEY);
		dustyKey.setTooltip("Not needed if you have level 70 Agility, can be obtained during the quest");
		jailKey = new ItemRequirement("Jail Key", ItemID.JAIL_KEY);

		scorpionCageEmpty = new ItemRequirement("Scorpion Cage", ItemID.SCORPION_CAGE);
		scorpionCageTaverley = new ItemRequirement("Scorpion Cage", ItemID.SCORPION_CAGE_457);
		scorpionCageEmptyOrTaverley = new ItemRequirement("Scorpion Cage", ItemID.SCORPION_CAGE);
		scorpionCageEmptyOrTaverley.addAlternates(ItemID.SCORPION_CAGE_457);
		scorpionCageTaverleyAndMonastery = new ItemRequirement("Scorpion Cage", ItemID.SCORPION_CAGE_459);
		scorpionCageFull = new ItemRequirement("Scorpion Cage", ItemID.SCORPION_CAGE_463);

		// Recommended
		antiDragonShield = new ItemRequirement("Anti-dragon shield or DFS", ItemCollections.getAntifireShields());
		antiPoison = new ItemRequirement("Antipoison", ItemCollections.getAntipoisons());
		food = new ItemRequirement("Food", -1, -1);
		teleRunes = new ItemRequirement("Runes to teleport to Camelot and Falador", -1, -1);
		gamesNecklace = new ItemRequirement("Games Necklace", ItemCollections.getGamesNecklaces());
		gloryOrCombatBracelet = new ItemRequirement("A charged glory or a combat bracelet", ItemCollections.getAmuletOfGlories());
		gloryOrCombatBracelet.addAlternates(ItemCollections.getCombatBracelets());
		fairyRingAccess = new QuestRequirement(QuestHelperQuest. FAIRYTALE_II__CURE_A_QUEEN, QuestState. IN_PROGRESS, "Fairy ring access");
		fairyRingAccess.setTooltip(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN.getName() + " is required to at least be started in order to use fairy rings");

	}

	private void setupConditions()
	{
		has70Agility = new SkillCondition(Skill.AGILITY, 70, Operation.GREATER_EQUAL);
		hasDustyKey = new ItemRequirementCondition(dustyKey);

		inSorcerersTower1  = new ZoneCondition(sorcerersTower1);
		inSorcerersTower2 = new ZoneCondition(sorcerersTower2);
		inSorcerersTower3 = new ZoneCondition(sorcerersTower3);

		hasScorpionCageEmpty = new ItemRequirementCondition(scorpionCageEmpty);
		hasScorpionCageTaverley = new ItemRequirementCondition(scorpionCageTaverley);
		hasScorpionCageEmptyOrTaverley = new ItemRequirementCondition(scorpionCageEmptyOrTaverley);
		hasScorpionCageTaverleyAndMonastery = new ItemRequirementCondition(scorpionCageTaverleyAndMonastery);
		hasScorpionCageFull = new ItemRequirementCondition(scorpionCageFull);

		inTaverleyDungeon = new ZoneCondition(taverleyDungeon);
		inDeepTaverleyDungeon = new ZoneCondition(deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4);
		inJailCell = new ZoneCondition(jailCell);
		hasJailKey = new ItemRequirementCondition(jailKey);

		inTaverleyScorpionRoom = new ZoneCondition(taverleyScorpionRoom);
		inUpstairsMonastery = new ZoneCondition(upstairsMonastery);
		inBarbarianOutpost = new ZoneCondition(barbarianOutpost);
	}

	private void setupSteps()
	{
		speakToThormac = new NpcStep(this, NpcID.THORMAC, new WorldPoint(2702, 3405, 3), "Speak to Thormac on the top floor of the Sorcerer's Tower south of Seer's Village.");
		speakToThormac.addDialogStep("What do you need assistance with?");
		speakToThormac.addDialogStep("So how would I go about catching them then?");
		speakToThormac.addDialogStep("Ok, I will do it then");

		sorcerersTowerLadder0 = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2701, 3408, 0), "Climb the 0 floor ladder of Sorcerer's Tower");
		sorcerersTowerLadder1 = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2704, 3403, 1), "Climb the 1 floor ladder of Sorcerer's Tower");
		sorcerersTowerLadder2 = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2699, 3405, 2), "Climb the 2 floor ladder of Sorcerer's Tower");
		speakToThormac.addSubSteps(sorcerersTowerLadder0, sorcerersTowerLadder1, sorcerersTowerLadder2);

		speakToSeer1 = new NpcStep(this, NpcID.SEER, new WorldPoint(2710, 3484, 0), "Speak to a seer in Seer's Village");
		speakToSeer1.addDialogStep("I need to locate some scorpions.");
		speakToSeer1.addDialogStep("Your friend Thormac sent me to speak to you.");

		if (client.getRealSkillLevel(Skill.AGILITY) >= 70)
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
					"Go to Taverley Dungeon. As you're 70 Agility, you don't need a dusty key.", scorpionCageEmpty);
		}
		else
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
					"Go to Taverley Dungeon. Bring a dusty key if you have one, otherwise you can get one in the dungeon.", scorpionCageEmpty, dustyKey);
		}

		goThroughPipe = new ObjectStep(this, ObjectID.OBSTACLE_PIPE_16509, new WorldPoint(2888, 9799, 0), "Squeeze through the obstacle pipe.");
		killJailerForKey = new NpcStep(this, NpcID.JAILER, new WorldPoint(2930, 9692, 0), "Travel through Taverley Dungeon until you reach the Black Knights' Base. Kill the Jailer in the east side of the base for a jail key.");
		getDustyFromAdventurer = new NpcStep(this, NpcID.VELRAK_THE_EXPLORER, new WorldPoint(2930, 9685, 0), "Use the jail key on the south door and talk to Velrak for a dusty key.", jailKey);
		getDustyFromAdventurer.addDialogStep("So... do you know anywhere good to explore?");
		getDustyFromAdventurer.addDialogStep("Yes please!");
		enterDeeperTaverley = new ObjectStep(this, ObjectID.GATE_2623, new WorldPoint(2924, 9803, 0), "Enter the gate to the deeper Taverley dungeon.", dustyKey);
		enterTaverleyDungeon.addSubSteps(goThroughPipe, killJailerForKey, getDustyFromAdventurer, enterDeeperTaverley);
		searchOldWall = new ObjectStep(this, ObjectID.OLD_WALL, new WorldPoint(2875, 9799, 0), "Search the Old wall");
		catchTaverleyScorpion = new NpcStep(this, NpcID.KHARID_SCORPION, "Use the scorpion cage on the scorpion.", scorpionCageEmpty);
		catchTaverleyScorpion.addIcon(ItemID.SCORPION_CAGE);

		speakToSeer2 = new NpcStep(this, NpcID.SEER, new WorldPoint(2710, 3484, 0), "Speak to a seer in Seer's Village");
		speakToSeer2.addDialogStep("I've retrieved the scorpion from near the spiders.");

		enterMonastery = new ObjectStep(this, ObjectID.LADDER_2641, new WorldPoint(3057, 3483, 0), "Enter the Edgeville Monastery");
		catchMonasteryScorpion = new NpcStep(this, NpcID.KHARID_SCORPION_5230, "Use the scorpion cage on the scorpion", scorpionCageTaverley);
		catchMonasteryScorpion.addIcon(ItemID.SCORPION_CAGE);

		enterOutpost = new ObjectStep(this, ObjectID.GATE_2115, new WorldPoint(2545, 3570, 0), "Enter the Barbarian Outpost");
		catchOutpostScorpion = new NpcStep(this, NpcID.KHARID_SCORPION_5229, new WorldPoint(2553, 3570, 0), "Use the scorpion cage on the scorpion", scorpionCageTaverleyAndMonastery);
		catchOutpostScorpion.addIcon(ItemID.SCORPION_CAGE);

		returnToThormac = new NpcStep(this, NpcID.THORMAC, "Return the scorpions to Thormac on the top floor of the Sorcerer's Tower", scorpionCageFull);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		if (client.getRealSkillLevel(Skill.AGILITY) < 70)
		{
			reqs.add(dustyKey);
		}

		return reqs.isEmpty() ? null : reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(antiDragonShield, antiPoison, food, teleRunes, gamesNecklace, gloryOrCombatBracelet));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("The ability to run past level 172 black demons and level 64 poison spiders"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Talk to Thormac", new ArrayList<>(Collections.singletonList(speakToThormac))));
		allSteps.add(new PanelDetails("The first scorpion", new ArrayList<>(Arrays.asList(speakToSeer1, enterTaverleyDungeon, searchOldWall, catchTaverleyScorpion, speakToSeer2)), scorpionCageEmptyOrTaverley));
		allSteps.add(new PanelDetails("The second scorpion", new ArrayList<>(Arrays.asList(enterMonastery, catchMonasteryScorpion)), scorpionCageTaverley));
		allSteps.add(new PanelDetails("The third scorpion", new ArrayList<>(Arrays.asList(enterOutpost, catchOutpostScorpion)), scorpionCageTaverleyAndMonastery));
		allSteps.add(new PanelDetails("Finishing up", new ArrayList<>(Collections.singletonList(returnToThormac))));

		return allSteps;
	}

	@Override
	public ArrayList<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL, QuestState.FINISHED));
		reqs.add(new SkillRequirement(Skill.PRAYER, 31));

		return reqs;
	}
}