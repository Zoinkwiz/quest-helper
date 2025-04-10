package com.questhelper.helpers.quests.scorpioncatcher;

import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.KeyringCollection;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.KeyringRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class ScorpionCatcher extends BasicQuestHelper
{
	ItemRequirement dustyKey, jailKey, scorpionCageMissingTaverley, scorpionCageMissingMonastery, scorpionCageEmptyOrTaverley, scorpionCageTaverleyAndMonastery, scorpionCageFull, food,
		antiDragonShield, antiPoison, teleRunesFalador, gamesNecklace, gloryOrCombatBracelet, camelotTeleport;
	QuestRequirement fairyRingAccess;
	Zone sorcerersTower3, sorcerersTower2, sorcerersTower1, taverleyDungeon, deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4,
		jailCell, taverleyScorpionRoom, upstairsMonastery, barbarianOutpost;
	Requirement has70Agility, has80Agility, inTaverleyDungeon, inDeepTaverleyDungeon, inJailCell, inSorcerersTower1, inSorcerersTower2,
		inSorcerersTower3, inTaverleyScorpionRoom, inUpstairsMonastery, inBarbarianOutpost, jailKeyNearby;
	QuestStep speakToThormac, speakToSeer1, enterTaverleyDungeon, goThroughPipe, goOverStrangeFloor, killJailerForKey,
		getDustyFromAdventurer, enterDeeperTaverley, pickUpJailKey,
		searchOldWall, catchTaverleyScorpion, sorcerersTowerLadder0, sorcerersTowerLadder1, sorcerersTowerLadder2, enterMonastery, catchMonasteryScorpion,
		catchOutpostScorpion, enterOutpost, returnToThormac;

	ConditionalStep finishQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goToTopOfTower = new ConditionalStep(this, sorcerersTowerLadder0);
		goToTopOfTower.addStep(inSorcerersTower1, sorcerersTowerLadder1);
		goToTopOfTower.addStep(inSorcerersTower2, sorcerersTowerLadder2);

		ConditionalStep beginQuest = new ConditionalStep(this, goToTopOfTower);
		beginQuest.addStep(inSorcerersTower3, speakToThormac);

		finishQuest = new ConditionalStep(this, goToTopOfTower,
			"Return to Thormac to finish the quest.",	scorpionCageFull);
		finishQuest.addStep(inSorcerersTower3, returnToThormac);

		ConditionalStep goGetTaverleyScorpion = new ConditionalStep(this, enterTaverleyDungeon);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyScorpionRoom), catchTaverleyScorpion);
		goGetTaverleyScorpion.addStep(new Conditions(inDeepTaverleyDungeon), searchOldWall);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyDungeon, has80Agility), goOverStrangeFloor);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyDungeon, has70Agility), goThroughPipe);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyDungeon, dustyKey), enterDeeperTaverley);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyDungeon, new Conditions(LogicType.OR, inJailCell, jailKey)), getDustyFromAdventurer);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyDungeon, jailKeyNearby), pickUpJailKey);
		goGetTaverleyScorpion.addStep(new Conditions(inTaverleyDungeon), killJailerForKey);

		ConditionalStep scorpions = new ConditionalStep(this, finishQuest);
		scorpions.addStep(scorpionCageMissingTaverley.alsoCheckBank(questBank), goGetTaverleyScorpion);

		scorpions.addStep(new Conditions(scorpionCageMissingMonastery, inUpstairsMonastery), catchMonasteryScorpion);
		scorpions.addStep(scorpionCageMissingMonastery.alsoCheckBank(questBank), enterMonastery);

		scorpions.addStep(new Conditions(scorpionCageTaverleyAndMonastery, inBarbarianOutpost), catchOutpostScorpion);
		scorpions.addStep(scorpionCageTaverleyAndMonastery.alsoCheckBank(questBank), enterOutpost);

		steps.put(0, beginQuest);
		steps.put(1, speakToSeer1);
		steps.put(2, scorpions);
		steps.put(3, scorpions);

		return steps;
	}

	@Override
	protected void setupZones()
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

	@Override
	protected void setupRequirements()
	{
		dustyKey = new KeyringRequirement("Dusty Key", configManager, KeyringCollection.DUSTY_KEY).isNotConsumed();
		dustyKey.setTooltip("Not needed if you have level 70 Agility, can be obtained during the quest");
		jailKey = new ItemRequirement("Jail Key", ItemID.JAIL_KEY);

		scorpionCageMissingTaverley = new ItemRequirement("Scorpion Cage", ItemID.SCORPIONCAGEEMPTY);
		// The 3 below cages are combos of cages without the taverley scorpion
		scorpionCageMissingTaverley.addAlternates(ItemID.SCORPIONCAGEB, ItemID.SCORPIONCAGEBC, ItemID.SCORPIONCAGEC);
		scorpionCageMissingTaverley.setTooltip("You can get another from Thormac");

		scorpionCageMissingMonastery = new ItemRequirement("Scorpion Cage", ItemID.SCORPIONCAGEA);
		scorpionCageMissingMonastery.addAlternates(ItemID.SCORPIONCAGEAB);

		scorpionCageEmptyOrTaverley = new ItemRequirement("Scorpion Cage", ItemID.SCORPIONCAGEEMPTY);
		// Alternative is taverley + barb
		scorpionCageEmptyOrTaverley.addAlternates(ItemID.SCORPIONCAGEA);

		scorpionCageTaverleyAndMonastery = new ItemRequirement("Scorpion Cage", ItemID.SCORPIONCAGEAC);

		scorpionCageFull = new ItemRequirement("Scorpion Cage", ItemID.SCORPIONCAGEFULL);

		// Recommended
		antiDragonShield = new ItemRequirement("Anti-dragon shield or DFS", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();
		antiPoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		teleRunesFalador = new ItemRequirement("Teleport to Falador", ItemID.POH_TABLET_FALADORTELEPORT, -1);
		camelotTeleport = new ItemRequirement("Teleport to Camelot", ItemID.POH_TABLET_CAMELOTTELEPORT, -1);
		gamesNecklace = new ItemRequirement("Games Necklace", ItemCollections.GAMES_NECKLACES);
		gloryOrCombatBracelet = new ItemRequirement("A charged glory or a combat bracelet", ItemCollections.AMULET_OF_GLORIES);
		gloryOrCombatBracelet.addAlternates(ItemCollections.COMBAT_BRACELETS);
		fairyRingAccess = new QuestRequirement(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.IN_PROGRESS, "Fairy ring access");
		fairyRingAccess.setTooltip(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN.getName() + " is required to at least be started in order to use fairy rings");

	}

	private void setupConditions()
	{
		has70Agility = new SkillRequirement(Skill.AGILITY, 70, true);
		has80Agility = new SkillRequirement(Skill.AGILITY, 80, true);

		inSorcerersTower1 = new ZoneRequirement(sorcerersTower1);
		inSorcerersTower2 = new ZoneRequirement(sorcerersTower2);
		inSorcerersTower3 = new ZoneRequirement(sorcerersTower3);


		inTaverleyDungeon = new ZoneRequirement(taverleyDungeon);
		inDeepTaverleyDungeon = new ZoneRequirement(deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4);
		inJailCell = new ZoneRequirement(jailCell);
		jailKeyNearby = new ItemOnTileRequirement(jailKey);

		inTaverleyScorpionRoom = new ZoneRequirement(taverleyScorpionRoom);
		inUpstairsMonastery = new ZoneRequirement(upstairsMonastery);
		inBarbarianOutpost = new ZoneRequirement(barbarianOutpost);
	}

	private void setupSteps()
	{
		speakToThormac = new NpcStep(this, NpcID.THORMAC, new WorldPoint(2702, 3405, 3), "Speak to Thormac on the top floor of the Sorcerer's Tower south of Seer's Village.");
		speakToThormac.addDialogStep("What do you need assistance with?");
		speakToThormac.addDialogStep("So how would I go about catching them then?");
		speakToThormac.addDialogStep("Ok, I will do it then");

		sorcerersTowerLadder0 = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2701, 3408, 0),
			"Climb to the top of the Sorcerer's Tower south of Seers' Village.");
		sorcerersTowerLadder1 = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2704, 3403, 1),
			"Climb to the top of the Sorcerer's Tower south of Seers' Village.");
		sorcerersTowerLadder2 = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2699, 3405, 2),
			"Climb to the top of the Sorcerer's Tower south of Seers' Village.");
		speakToThormac.addSubSteps(sorcerersTowerLadder0, sorcerersTowerLadder1, sorcerersTowerLadder2);

		speakToSeer1 = new NpcStep(this, NpcID.SEER, new WorldPoint(2710, 3484, 0),
			"Speak to a seer in Seer's Village.");
		speakToSeer1.addDialogStep("I need to locate some scorpions.");
		speakToSeer1.addDialogStep("Your friend Thormac sent me to speak to you.");

		if (client.getRealSkillLevel(Skill.AGILITY) >= 70)
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
				"Go to Taverley Dungeon. As you're 70 Agility, you don't need a dusty key.", scorpionCageMissingTaverley);
		}
		else
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
				"Go to Taverley Dungeon. Bring a dusty key if you have one, otherwise you can get one in the dungeon.", scorpionCageMissingTaverley, dustyKey);
		}

		goOverStrangeFloor = new ObjectStep(this, ObjectID.TAVERLY_DUNGEON_FLOOR_SPIKES_SC, new WorldPoint(2879, 9813, 0), "Go over the strange floor.");

		goThroughPipe = new ObjectStep(this, ObjectID.TAVERLY_DUNGEON_PIPE_SC, new WorldPoint(2888, 9799, 0),
			"Squeeze through the obstacle pipe.");
		killJailerForKey = new NpcStep(this, NpcID.JAILER, new WorldPoint(2930, 9692, 0),
			"Travel through Taverley Dungeon until you reach the Black Knights' Base. Kill the Jailer in the east side of the base for a jail key.");
		pickUpJailKey = new ItemStep(this, "Pick up the jail key.", jailKey);
		getDustyFromAdventurer = new NpcStep(this, NpcID.VELRAK_THE_EXPLORER, new WorldPoint(2930, 9685, 0),
			"Use the jail key on the south door and talk to Velrak for a dusty key.", jailKey);
		getDustyFromAdventurer.addDialogStep("So... do you know anywhere good to explore?");
		getDustyFromAdventurer.addDialogStep("Yes please!");
		enterDeeperTaverley = new ObjectStep(this, ObjectID.DEEPDUNGEONDOOR, new WorldPoint(2924, 9803, 0),
			"Enter the gate to the deeper Taverley dungeon.", dustyKey);
		enterTaverleyDungeon.addSubSteps(goThroughPipe, goOverStrangeFloor, killJailerForKey, getDustyFromAdventurer, enterDeeperTaverley);
		searchOldWall = new ObjectStep(this, ObjectID.SCORPIONWALL, new WorldPoint(2875, 9799, 0), "Search the Old wall.");
		// TODO: Highlight item
		catchTaverleyScorpion = new NpcStep(this, NpcID.QUESTSCORPIONA, "Use the scorpion cage on the scorpion.", scorpionCageMissingTaverley);
		catchTaverleyScorpion.addIcon(ItemID.SCORPIONCAGEEMPTY);

		enterMonastery = new ObjectStep(this, ObjectID.MONASTERYLADDER, new WorldPoint(3057, 3483, 0),
			"Enter the Edgeville Monastery.");
		// TODO: Highlight item
		catchMonasteryScorpion = new NpcStep(this, NpcID.QUESTSCORPIONC, "Use the scorpion cage on the scorpion.",
			scorpionCageMissingMonastery);
		catchMonasteryScorpion.addIcon(ItemID.SCORPIONCAGEEMPTY);

		enterOutpost = new ObjectStep(this, ObjectID.BARBARIANGATEL, new WorldPoint(2545, 3570, 0),
			"Enter the Barbarian Outpost.");
		// TODO: Highlight item
		catchOutpostScorpion = new NpcStep(this, NpcID.QUESTSCORPIONB, new WorldPoint(2553, 3570, 0),
			"Use the scorpion cage on the scorpion.", scorpionCageTaverleyAndMonastery);
		catchOutpostScorpion.addIcon(ItemID.SCORPIONCAGEEMPTY);

		returnToThormac = new NpcStep(this, NpcID.THORMAC,
			"", scorpionCageFull);
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
		return new ArrayList<>(Arrays.asList(antiDragonShield, antiPoison, food, teleRunesFalador, camelotTeleport, gamesNecklace,
			gloryOrCombatBracelet));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("The ability to run past level 172 black demons and level 64 poison spiders"));
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.STRENGTH, 6625));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to have Thormac turn a Battlestaff into a Mystic Staff for 40,000 Coins."));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Talk to Thormac", new ArrayList<>(Collections.singletonList(speakToThormac))));
		allSteps.add(new PanelDetails("The first scorpion", new ArrayList<>(Arrays.asList(speakToSeer1, enterTaverleyDungeon, searchOldWall, catchTaverleyScorpion))));
		allSteps.add(new PanelDetails("The second scorpion", new ArrayList<>(Arrays.asList(enterMonastery, catchMonasteryScorpion))));
		allSteps.add(new PanelDetails("The third scorpion", new ArrayList<>(Arrays.asList(enterOutpost, catchOutpostScorpion))));
		allSteps.add(new PanelDetails("Finishing up", new ArrayList<>(Collections.singletonList(finishQuest))));

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
