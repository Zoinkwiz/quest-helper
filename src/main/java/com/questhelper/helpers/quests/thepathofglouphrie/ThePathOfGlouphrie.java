/*
 * Copyright (c) 2023, pajlada <https://github.com/pajlada>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.quests.thepathofglouphrie;

import com.google.common.collect.ImmutableMap;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.InformKingBolren;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.StartingOff;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.UnveilEvil;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_PATH_OF_GLOUPHRIE
)
public class ThePathOfGlouphrie extends BasicQuestHelper
{
	private final StartingOff startingOff = new StartingOff();
	private final UnveilEvil unveilEvil = new UnveilEvil();
	private final InformKingBolren informKingBolren = new InformKingBolren();
	public ZoneRequirement inTreeGnomeVillageMiddle;
	public ZoneRequirement inTreeGnomeVillageDungeon;
	public TeleportItemRequirement teleToBolren;
	public ZoneRequirement inStoreroom;
	public Conditions inCutscene;
	public WidgetTextRequirement lecternWidgetActive;
	public VarbitRequirement learnedAboutChapter1;
	public VarbitRequirement learnedAboutChapter2;
	public ObjectStep enterTreeGnomeVillageMazeFromMiddle;
	public ObjectStep climbDownIntoTreeGnomeVillageDungeon;
	public ZoneRequirement inGnomeStrongholdFloor1;
	/// Required items
	private ItemRequirement crossbow;
	private ItemRequirement mithGrapple;
	private ItemRequirement treeGnomeVillageDungeonKey;
	private ItemRequirement combatGear;
	private ItemRequirement prayerPotions;
	private ItemRequirement food;
	/// Recommended items
	private ItemRequirement earmuffsOrSlayerHelmet;
	private TeleportItemRequirement fairyRingOrCastleWars;
	private ItemRequirement runRestoreItems;
	private FreeInventorySlotRequirement freeInventorySlots;
	private ConditionalStep talkToLongramble;
	private Zone treeGnomeVillageMiddle1;
	private Zone treeGnomeVillageMiddle2;
	private Zone treeGnomeVillageMiddle3;
	private Zone treeGnomeVillageDungeon;
	private Zone storeroomZone;
	private Zone gnomeStrongholdFloor1;
	private Zone longrambleZone;
	private ZoneRequirement nearLongramble;
	private ConditionalStep talkToSpiritTree;
	private ConditionalStep talkToSpiritTreeAgain;
	private ItemRequirement crystalChime;
	private ObjectStep useCrystalChime;
	private ObjectStep enterSewer;
	private Zone sewer1;
	private Zone sewer2;
	private Zone sewer3;
	private Zone sewer4Section1;
	private Zone sewer4Section2;
	private Zone sewer5;
	private Zone sewer6Section1;
	private Zone sewer6Section2;
	private Zone bossRoom;
	private Requirement inSewer1, inSewer2, inSewer3, inSewer4, inSewer5, inSewer6, inBossRoom;
	private ObjectStep sewer1Ladder;
	private ObjectStep sewer5Ladder;
	private ObjectStep sewer2Ladder;
	private ObjectStep sewer3Ladder;
	private ObjectStep sewer4Ladder;
	private ObjectStep bossDoor;
	private NpcStep bossStep;
	private DetailedQuestStep watchFinalCutscene;
	private NpcStep talkToHazelmere;
	private PrayerRequirement protectMissiles;
	private ObjectStep peekHeavyDoor;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupRequirements();
		setupConditions();
		setupSteps();


		var talkToLongrambleStep = new ConditionalStep(this, talkToLongramble);

		var talkToSpiritTreeStep = new ConditionalStep(this, talkToSpiritTree);

		var useCrystalChimeStep = new ConditionalStep(this, useCrystalChime);

		var talkToSpiritTreeAgainStep = new ConditionalStep(this, talkToSpiritTreeAgain);

		var enterSewerStep = new ConditionalStep(this, enterSewer);

		enterSewerStep.addStep(inBossRoom, bossStep);
		enterSewerStep.addStep(inSewer6, bossDoor);
		enterSewerStep.addStep(inSewer5, sewer5Ladder);
		enterSewerStep.addStep(inSewer4, sewer4Ladder);
		enterSewerStep.addStep(inSewer3, sewer3Ladder);
		enterSewerStep.addStep(inSewer2, sewer2Ladder);
		enterSewerStep.addStep(inSewer1, sewer1Ladder);

		var watchFinalCutsceneStep = new ConditionalStep(this, peekHeavyDoor);
		watchFinalCutsceneStep.addStep(inCutscene, watchFinalCutscene);

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(0, startingOff.talkToKingBolren)
			.put(2, startingOff.talkToKingBolrenAgain)
			.put(4, startingOff.golrie)
			.put(6, unveilEvil.enterStoreroom)
			.put(8, unveilEvil.solveMonolithPuzzleStep)
			.put(10, unveilEvil.solveMonolithPuzzleStep)
			.put(12, unveilEvil.solveMonolithPuzzleStep)
			.put(14, unveilEvil.learnLoreStep)
			.put(16, unveilEvil.solveYewnocksMachinePuzzleStep)
			.put(18, unveilEvil.watchCutscene)
			.put(20, informKingBolren.killEvilCreature)
			.put(22, informKingBolren.informKingBolren)
			.put(24, informKingBolren.talkToGianneJnrStep)
			.put(26, talkToLongrambleStep)
			.put(28, talkToLongrambleStep)
			.put(30, talkToSpiritTreeStep)
			.put(32, talkToSpiritTreeStep)
			.put(34, talkToSpiritTreeStep)
			.put(36, useCrystalChimeStep)
			.put(38, talkToSpiritTreeAgainStep)
			.put(40, enterSewerStep)
			.put(42, enterSewerStep)
			.put(44, enterSewerStep)
			.put(46, watchFinalCutsceneStep)
			.put(48, talkToHazelmere)
			.build();
	}

	public void setupZones()
	{
		treeGnomeVillageMiddle1 = new Zone(new WorldPoint(2514, 3161, 0), new WorldPoint(2542, 3175, 0));
		treeGnomeVillageMiddle2 = new Zone(new WorldPoint(2543, 3167, 0), new WorldPoint(2547, 3172, 0));
		treeGnomeVillageMiddle3 = new Zone(new WorldPoint(2522, 3158, 0), new WorldPoint(2542, 3160, 0));
		treeGnomeVillageDungeon = new Zone(new WorldPoint(2560, 4426, 0), new WorldPoint(2627, 4477, 0));
		storeroomZone = new Zone(11074);
		gnomeStrongholdFloor1 = new Zone(new WorldPoint(2437, 3474, 1), new WorldPoint(2493, 3511, 1));
		longrambleZone = new Zone(new WorldPoint(2328, 3082, 0), new WorldPoint(2346, 3103, 0));
		sewer1 = new Zone(new WorldPoint(1472, 4236, 0), new WorldPoint(1480, 4239, 0));
		sewer2 = new Zone(new WorldPoint(1472, 4226, 1), new WorldPoint(1534, 4247, 1));
		sewer3 = new Zone(new WorldPoint(1526, 4235, 0), new WorldPoint(1529, 4254, 0));
		sewer4Section1 = new Zone(new WorldPoint(1472, 4245, 1), new WorldPoint(1534, 4273, 1));
		sewer4Section2 = new Zone(new WorldPoint(1479, 4273, 1), new WorldPoint(1489, 4285, 1));
		sewer5 = new Zone(new WorldPoint(1484, 4279, 0), new WorldPoint(1501, 4282, 0));
		sewer6Section1 = new Zone(new WorldPoint(1496, 4276, 1), new WorldPoint(1513, 4288, 1));
		sewer6Section2 = new Zone(5955, 1);

		bossRoom = new Zone(WorldPoint.fromRegion(5955, 35, 24, 1), WorldPoint.fromRegion(5955, 48, 44, 1));
	}

	@Override
	public void setupRequirements()
	{
		// Required items
		var rovingElvesNotStarted = new QuestRequirement(QuestHelperQuest.ROVING_ELVES, QuestState.NOT_STARTED);
		crossbow = new ItemRequirement("Any crossbow", ItemID.CROSSBOW).isNotConsumed();
		crossbow.addAlternates(ItemID.BRONZE_CROSSBOW, ItemID.IRON_CROSSBOW, ItemID.STEEL_CROSSBOW,
			ItemID.MITHRIL_CROSSBOW, ItemID.ADAMANT_CROSSBOW, ItemID.RUNE_CROSSBOW, ItemID.DRAGON_CROSSBOW,
			ItemID.BLURITE_CROSSBOW, ItemID.DORGESHUUN_CROSSBOW, ItemID.ARMADYL_CROSSBOW, ItemID.ZARYTE_CROSSBOW);
		mithGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).isNotConsumed();
		treeGnomeVillageDungeonKey = new ItemRequirement("Tree Gnome Village dungeon key", ItemID.KEY_293).showConditioned(rovingElvesNotStarted);
		treeGnomeVillageDungeonKey.canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat equipment", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		// Recommended items
		var lumbridgeEliteComplete = new QuestRequirement(QuestHelperQuest.LUMBRIDGE_ELITE, QuestState.FINISHED);
		earmuffsOrSlayerHelmet = new ItemRequirement("Earmuffs or a Slayer helmet", ItemCollections.EAR_PROTECTION, 1, true).highlighted();
		earmuffsOrSlayerHelmet.setTooltip("You will take a lot more damage without these");
		fairyRingOrCastleWars = new TeleportItemRequirement("Teleport to Castle Wars (Fairy Ring BKP or Ring of Dueling [2])", ItemCollections.FAIRY_STAFF, 1);
		fairyRingOrCastleWars.addAlternates(ItemCollections.RING_OF_DUELINGS);
		fairyRingOrCastleWars.setConditionToHide(lumbridgeEliteComplete);
		runRestoreItems = new ItemRequirement("Several run restore items", ItemCollections.RUN_RESTORE_ITEMS, -1);
		freeInventorySlots = new FreeInventorySlotRequirement(11);
		// TODO: recommend the toad legs to get a mint cake?

		// Teleports
		teleToBolren = new TeleportItemRequirement("Spirit tree to Tree Gnome Village [1]", -1, -1);

		// Items that are made during the quest
		crystalChime = new ItemRequirement("Crystal chime", ItemID.CRYSTAL_CHIME, 1);

		// Non-item requirements
		lecternWidgetActive = new WidgetTextRequirement(854, 5, "Chapter 1. Bad advice");
		protectMissiles = new PrayerRequirement("Protect from Missiles to reduce damage taken by the Terrorbirds", Prayer.PROTECT_FROM_MISSILES);
	}

	private void setupConditions()
	{
		inTreeGnomeVillageMiddle = new ZoneRequirement(treeGnomeVillageMiddle1, treeGnomeVillageMiddle2, treeGnomeVillageMiddle3);
		inTreeGnomeVillageDungeon = new ZoneRequirement(treeGnomeVillageDungeon);
		inStoreroom = new ZoneRequirement(storeroomZone);
		inGnomeStrongholdFloor1 = new ZoneRequirement(gnomeStrongholdFloor1);
		nearLongramble = new ZoneRequirement(longrambleZone);

		inCutscene = new Conditions(LogicType.OR,
			new VarbitRequirement(4606, 3),
			new VarbitRequirement(12139, 1)
		);

		learnedAboutChapter1 = new VarbitRequirement(15291, 1);
		learnedAboutChapter2 = new VarbitRequirement(15292, 1);
		// learnedAboutChapter3 = new VarbitRequirement(15293, 1);

		inSewer1 = new ZoneRequirement(sewer1);
		inSewer2 = new ZoneRequirement(sewer2);
		inSewer3 = new ZoneRequirement(sewer3);
		inSewer4 = new Conditions(LogicType.OR, new ZoneRequirement(sewer4Section1), new ZoneRequirement(sewer4Section2));
		inSewer5 = new ZoneRequirement(sewer5);
		inSewer6 = new Conditions(LogicType.OR, new ZoneRequirement(sewer6Section1), new ZoneRequirement(sewer6Section2));
		inBossRoom = new ZoneRequirement(bossRoom);
	}

	private void setupSteps()
	{
		// Shared base steps

		enterTreeGnomeVillageMazeFromMiddle = new ObjectStep(this, ObjectID.LOOSE_RAILING_2186, new WorldPoint(2515, 3161, 0), "");
		climbDownIntoTreeGnomeVillageDungeon = new ObjectStep(this, ObjectID.LADDER_5250, new WorldPoint(2533, 3155, 0), "");

		startingOff.setup(this);

		unveilEvil.setup(this);

		informKingBolren.setup(this);


		/// Find Longramble
		var teleToLongramble = new TeleportItemRequirement("Fairy Ring BKP or Ring of Dueling to Castle Wars", ItemCollections.RING_OF_DUELINGS, 1);
		teleToLongramble.addAlternates(ItemCollections.FAIRY_STAFF);


		var goToLongramble = new ObjectStep(this, ObjectID.TREE_49590, new WorldPoint(2333, 3081, 0), "");
		goToLongramble.addRecommended(earmuffsOrSlayerHelmet);
		goToLongramble.addDialogStep("Castle Wars Arena.");
		goToLongramble.addTeleport(teleToLongramble);
		var actuallyTalkToLongramble = new NpcStep(this, NpcID.LONGRAMBLE, new WorldPoint(2340, 3094, 0), "");
		actuallyTalkToLongramble.addRecommended(earmuffsOrSlayerHelmet);


		talkToLongramble = new ConditionalStep(this, goToLongramble, "Go to Longramble, make sure to head to a bank & gear up first. You can drop all leftover discs.", combatGear, crossbow.equipped().highlighted(), mithGrapple.equipped().highlighted(), prayerPotions, food, crystalChime);
		talkToLongramble.addStep(nearLongramble, actuallyTalkToLongramble);

		{
			var talk = new ObjectStep(this, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "");
			talkToSpiritTree = new ConditionalStep(this, talk, "Talk to the Spirit Tree", combatGear, prayerPotions, food, crystalChime);
		}

		useCrystalChime = new ObjectStep(this, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "Use the Crystal Chime on the Spirit Tree", crystalChime.highlighted());
		useCrystalChime.addIcon(ItemID.CRYSTAL_CHIME);

		{
			var talk = new ObjectStep(this, NullObjectID.NULL_49598, new WorldPoint(2339, 3111, 0), "");
			talkToSpiritTreeAgain = new ConditionalStep(this, talk, "Talk to the Spirit Tree again", combatGear, prayerPotions, food, crystalChime);
		}

		/// The Warped Depths
		enterSewer = new ObjectStep(this, ObjectID.SEWER_ENTRANCE, new WorldPoint(2322, 3101, 0),
			"Enter the sewer to the west of the Spirit tree");
		enterSewer.addRequirement(combatGear, prayerPotions, food, crystalChime);

		sewer1Ladder = new ObjectStep(this, ObjectID.LADDER_49700, "Climb up the ladder");
		sewer2Ladder = new ObjectStep(this, ObjectID.LADDER_49701, new WorldPoint(1529, 4236, 1),
			"Climb down the ladder.");
		sewer2Ladder.addRecommended(protectMissiles);
		sewer3Ladder = new ObjectStep(this, ObjectID.LADDER_49700, new WorldPoint(1529, 4253, 0),
			"Climb up the ladder.");
		sewer3Ladder.addRecommended(protectMissiles);
		sewer4Ladder = new ObjectStep(this, ObjectID.LADDER_49701, new WorldPoint(1486, 4283, 1),
			"Climb down the ladder. Re-activate your run if you step in any puddles.");
		sewer4Ladder.addRecommended(protectMissiles);
		sewer4Ladder.setLinePoints(List.of(
			new WorldPoint(1530, 4253, 1),
			new WorldPoint(1530, 4256, 1),
			new WorldPoint(1512, 4256, 1),
			new WorldPoint(1512, 4256, 1),
			new WorldPoint(1512, 4260, 1),
			new WorldPoint(1512, 4260, 1),
			new WorldPoint(1510, 4260, 1),
			new WorldPoint(1510, 4264, 1),
			new WorldPoint(1503, 4264, 1),
			new WorldPoint(1503, 4262, 1),
			new WorldPoint(1496, 4262, 1),
			new WorldPoint(1496, 4264, 1),
			new WorldPoint(1484, 4264, 1),
			new WorldPoint(1484, 4277, 1),
			new WorldPoint(1482, 4277, 1),
			new WorldPoint(1482, 4284, 1),
			new WorldPoint(1486, 4284, 1)
		));
		sewer5Ladder = new ObjectStep(this, ObjectID.LADDER_49700, new WorldPoint(1499, 4282, 0),
			"Climb up the ladder");
		sewer5Ladder.addRecommended(protectMissiles);
		bossDoor = new ObjectStep(this, ObjectID.METAL_GATE_49889, new WorldPoint(1506, 4319, 1),
			"Go to the boss room. Re-activate your run if you step in any puddles.");
		bossDoor.addRecommended(protectMissiles);
		bossDoor.setLinePoints(List.of(
			new WorldPoint(1499, 4283, 1),
			new WorldPoint(1506, 4283, 1),
			new WorldPoint(1506, 4291, 1),
			new WorldPoint(1503, 4291, 1),
			new WorldPoint(1503, 4292, 1),
			new WorldPoint(1497, 4292, 1),
			new WorldPoint(1497, 4291, 1),
			new WorldPoint(1487, 4291, 1),
			new WorldPoint(1487, 4302, 1),
			new WorldPoint(1492, 4302, 1),
			new WorldPoint(1492, 4304, 1),
			new WorldPoint(1495, 4304, 1),
			new WorldPoint(1495, 4316, 1),
			new WorldPoint(1498, 4316, 1),
			new WorldPoint(1498, 4319, 1),
			new WorldPoint(1506, 4319, 1)
		));
		// NOTE: If the user logs out, they will be in a non-instanced area of the boss are with the wrong terrorbirds
		bossStep = new NpcStep(this, new int[]{NpcID.WARPED_TERRORBIRD_12499, NpcID.WARPED_TERRORBIRD_12500, NpcID.WARPED_TERRORBIRD_12501},
			"Kill the Terrorbirds. You can use the pillars around the room to only fight one at a time. They fight with both Melee and Ranged.");
		bossStep.setAllowMultipleHighlights(true);

		peekHeavyDoor = new ObjectStep(this, NullObjectID.NULL_49909, WorldPoint.fromRegion(5955, 49, 31, 1), "Peek the heavy door");
		watchFinalCutscene = new DetailedQuestStep(this, "Watch the final cutscene");

		talkToHazelmere = new NpcStep(this, NpcID.HAZELMERE, new WorldPoint(2678, 3086, 1),
			"Talk to Hazelmere. Any lamps that don't fit in your inventory will land on the ground. You can speak to Hazelmere after the quest to recover any lost lamps.");
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(
			crossbow,
			mithGrapple,
			treeGnomeVillageDungeonKey,
			combatGear,
			prayerPotions,
			food
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(
			earmuffsOrSlayerHelmet,
			fairyRingOrCastleWars,
			runRestoreItems
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList(
			"3 Warped Terrorbirds (level 138)",
			"Evil Creature (level 1)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.STRENGTH, 30000, true),
			new ExperienceReward(Skill.SLAYER, 20000, true),
			new ExperienceReward(Skill.THIEVING, 5000, true),
			new ExperienceReward(Skill.MAGIC, 5000, true)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Access to the Poison Waste Dungeon"),
			new UnlockReward("Ability to unlock warped creatures as a Slayer task"),
			new UnlockReward("Access to a new spirit tree destination in the Poison Waste")
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new QuestRequirement(QuestHelperQuest.THE_EYES_OF_GLOUPHRIE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.STRENGTH, 60));
		req.add(new SkillRequirement(Skill.SLAYER, 56));
		req.add(new SkillRequirement(Skill.THIEVING, 56));
		req.add(new SkillRequirement(Skill.RANGED, 47));
		req.add(new SkillRequirement(Skill.AGILITY, 45));

		return req;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		var startingOffPanel = new PanelDetails(
			"Starting off",
			startingOff.getSteps(),
			List.of(freeInventorySlots)
		);

		var unveilTheEvilCreature = new PanelDetails(
			"Unveil the Evil creature",
			unveilEvil.getSteps(),
			List.of(freeInventorySlots)
		);

		var informKingBolrenPanel = new PanelDetails(
			"Inform King Bolren",
			informKingBolren.getSteps()
		);

		var findLongramble = new PanelDetails(
			"Find Longramble",
			List.of(talkToLongramble, talkToSpiritTree, useCrystalChime, talkToSpiritTreeAgain),
			List.of(crossbow, mithGrapple, combatGear, food, crystalChime),
			List.of(earmuffsOrSlayerHelmet, runRestoreItems)
		);

		var theWarpedDepths = new PanelDetails(
			"The Warped Depths",
			List.of(enterSewer, sewer1Ladder, sewer2Ladder, sewer3Ladder, sewer4Ladder, sewer5Ladder, bossDoor, bossStep, peekHeavyDoor, watchFinalCutscene, talkToHazelmere),
			List.of(crossbow, mithGrapple, combatGear, food, crystalChime),
			List.of(earmuffsOrSlayerHelmet, runRestoreItems)
		);

		panels.add(startingOffPanel);
		panels.add(unveilTheEvilCreature);
		panels.add(informKingBolrenPanel);
		panels.add(findLongramble);
		panels.add(theWarpedDepths);

		return panels;
	}
}
