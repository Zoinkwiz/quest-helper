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
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.FindLongramble;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.InformKingBolren;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.StartingOff;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.TheWarpedDepths;
import com.questhelper.helpers.quests.thepathofglouphrie.sections.UnveilEvil;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
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
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class ThePathOfGlouphrie extends BasicQuestHelper
{
	private final StartingOff startingOff = new StartingOff();
	private final UnveilEvil unveilEvil = new UnveilEvil();
	private final InformKingBolren informKingBolren = new InformKingBolren();
	private final FindLongramble findLongramble = new FindLongramble();
	private final TheWarpedDepths theWarpedDepths = new TheWarpedDepths();

	/// Zones
	public Zone treeGnomeVillageMiddle1, treeGnomeVillageMiddle2, treeGnomeVillageMiddle3;
	public Zone treeGnomeVillageDungeon, storeroomZone;
	public Zone treeGnomeVillageDungeonPreRovingElves;
	public Zone gnomeStrongholdFloor1, gnomeStrongholdFloor2, gnomeStrongholdFloor3;
	public Zone longrambleZone;
	public Zone sewer1, sewer2, sewer3, sewer4Section1, sewer4Section2;
	public Zone sewer5, sewer6Section1, sewer6Section2, bossRoom;

	/// Required items
	public ItemRequirement crossbow;
	public ItemRequirement mithGrapple;
	public ItemRequirement treeGnomeVillageDungeonKey;
	public ItemRequirement combatGear;
	public ItemRequirement prayerPotions;
	public ItemRequirement food;
	public FreeInventorySlotRequirement freeInventorySlots;

	/// Recommended items
	public TeleportItemRequirement fairyRingOrCastleWars;
	public ItemRequirement runRestoreItems;
	public ItemRequirement earmuffsOrSlayerHelmet;
	public ItemRequirement earmuffsOrSlayerHelmetEquipped;

	/// Teleports
	public TeleportItemRequirement teleToBolren;

	/// Quest items
	public ItemRequirement crystalChime;

	/// Conditions
	public ZoneRequirement inTreeGnomeVillageMiddle;
	public ZoneRequirement inTreeGnomeVillageDungeon;
	public ZoneRequirement inTreeGnomeVillageDungeonPreRovingElves;
	public ZoneRequirement inStoreroom;
	public ZoneRequirement nearLongramble;
	public Requirement inSewer1, inSewer2, inSewer3, inSewer4, inSewer5, inSewer6, inBossRoom;
	public Conditions inCutscene;
	public VarbitRequirement learnedAboutChapter1;
	public VarbitRequirement learnedAboutChapter2;
	public ObjectStep enterTreeGnomeVillageMazeFromMiddle;
	public ObjectStep climbDownIntoTreeGnomeVillageDungeon;
	public ZoneRequirement inGnomeStrongholdFloor1, inGnomeStrongholdFloor2, inGnomeStrongholdFloor3;

	public WidgetTextRequirement lecternWidgetActive;
	public PrayerRequirement protectMissiles;
	public QuestRequirement rovingElvesNotStarted;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

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
			.put(26, findLongramble.talkToLongramble)
			.put(28, findLongramble.talkToLongramble)
			.put(30, findLongramble.talkToSpiritTree)
			.put(32, findLongramble.talkToSpiritTree)
			.put(34, findLongramble.talkToSpiritTree)
			.put(36, findLongramble.useCrystalChime)
			.put(38, findLongramble.talkToSpiritTreeAgain)
			.put(40, theWarpedDepths.enterSewerStep)
			.put(42, theWarpedDepths.enterSewerStep)
			.put(44, theWarpedDepths.enterSewerStep)
			.put(46, theWarpedDepths.watchFinalCutsceneStep)
			.put(48, theWarpedDepths.talkToHazelmere)
			.build();
	}

	@Override
	protected void setupZones()
	{
		treeGnomeVillageMiddle1 = new Zone(new WorldPoint(2514, 3161, 0), new WorldPoint(2542, 3175, 0));
		treeGnomeVillageMiddle2 = new Zone(new WorldPoint(2543, 3167, 0), new WorldPoint(2547, 3172, 0));
		treeGnomeVillageMiddle3 = new Zone(new WorldPoint(2522, 3158, 0), new WorldPoint(2542, 3160, 0));
		treeGnomeVillageDungeon = new Zone(new WorldPoint(2560, 4426, 0), new WorldPoint(2627, 4477, 0));
		treeGnomeVillageDungeonPreRovingElves = new Zone(new WorldPoint(2503, 9546, 0), new WorldPoint(2557, 9588, 0));
		storeroomZone = new Zone(11074);
		gnomeStrongholdFloor1 = new Zone(new WorldPoint(2437, 3474, 1), new WorldPoint(2493, 3511, 1));
		gnomeStrongholdFloor2 = new Zone(new WorldPoint(2437, 3474, 2), new WorldPoint(2493, 3511, 2));
		gnomeStrongholdFloor3 = new Zone(new WorldPoint(2437, 3474, 3), new WorldPoint(2493, 3511, 3));
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
	protected void setupRequirements()
	{
		/// Required items
		rovingElvesNotStarted = new QuestRequirement(QuestHelperQuest.ROVING_ELVES, QuestState.NOT_STARTED);
		crossbow = new ItemRequirement("Any crossbow", ItemCollections.CROSSBOWS).isNotConsumed();
		mithGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).isNotConsumed();
		// NOTE: This does NOT have a step attached
		// I didn't have any character available that hadn't started the Roving Elves quest
		treeGnomeVillageDungeonKey = new ItemRequirement("Tree Gnome Village dungeon key", ItemID.KEY_293).showConditioned(rovingElvesNotStarted);
		treeGnomeVillageDungeonKey.canBeObtainedDuringQuest();
		combatGear = new ItemRequirement("Combat equipment", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		freeInventorySlots = new FreeInventorySlotRequirement(11);

		/// Recommended items
		var lumbridgeEliteComplete = new QuestRequirement(QuestHelperQuest.LUMBRIDGE_ELITE, QuestState.FINISHED);
		earmuffsOrSlayerHelmet = new ItemRequirement("Earmuffs or a Slayer helmet", ItemCollections.EAR_PROTECTION, 1, true).highlighted();
		earmuffsOrSlayerHelmet.setTooltip("You will take a lot more damage without these");
		earmuffsOrSlayerHelmetEquipped = earmuffsOrSlayerHelmet.equipped();
		fairyRingOrCastleWars = new TeleportItemRequirement("Teleport to Castle Wars (Fairy Ring BKP or Ring of Dueling [2])", ItemCollections.FAIRY_STAFF, 1);
		fairyRingOrCastleWars.addAlternates(ItemCollections.RING_OF_DUELINGS);
		fairyRingOrCastleWars.setConditionToHide(lumbridgeEliteComplete);
		runRestoreItems = new ItemRequirement("Several run restore items", ItemCollections.RUN_RESTORE_ITEMS, -1);
		// TODO: recommend the toad legs to get a mint cake?

		/// Teleports
		teleToBolren = new TeleportItemRequirement("Spirit tree to Tree Gnome Village [1]", -1, -1);

		/// Quest items
		crystalChime = new ItemRequirement("Crystal chime", ItemID.CRYSTAL_CHIME, 1);
		crystalChime.setTooltip("You can get a new one by going back to the storeroom in the Tree Gnome Village dungeon");
	}

	private void setupConditions()
	{
		inTreeGnomeVillageMiddle = new ZoneRequirement(treeGnomeVillageMiddle1, treeGnomeVillageMiddle2, treeGnomeVillageMiddle3);
		inTreeGnomeVillageDungeon = new ZoneRequirement(treeGnomeVillageDungeon);
		inTreeGnomeVillageDungeonPreRovingElves = new ZoneRequirement(treeGnomeVillageDungeonPreRovingElves);
		inStoreroom = new ZoneRequirement(storeroomZone);
		inGnomeStrongholdFloor1 = new ZoneRequirement(gnomeStrongholdFloor1);
		inGnomeStrongholdFloor2 = new ZoneRequirement(gnomeStrongholdFloor2);
		inGnomeStrongholdFloor3 = new ZoneRequirement(gnomeStrongholdFloor3);
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

		lecternWidgetActive = new WidgetTextRequirement(854, 5, "Chapter 1. Bad advice");
		protectMissiles = new PrayerRequirement("Protect from Missiles to reduce damage taken by the Terrorbirds", Prayer.PROTECT_FROM_MISSILES);
	}

	private void setupSteps()
	{
		// Shared base steps
		enterTreeGnomeVillageMazeFromMiddle = new ObjectStep(this, ObjectID.LOOSE_RAILING_2186, new WorldPoint(2515, 3161, 0), "");
		climbDownIntoTreeGnomeVillageDungeon = new ObjectStep(this, ObjectID.LADDER_5250, new WorldPoint(2533, 3155, 0), "");

		startingOff.setup(this);

		unveilEvil.setup(this);

		informKingBolren.setup(this);

		findLongramble.setup(this);

		theWarpedDepths.setup(this);
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
			List.of(freeInventorySlots, treeGnomeVillageDungeonKey)
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

		var findLongramblePanel = new PanelDetails(
			"Find Longramble",
			findLongramble.getSteps(),
			List.of(crossbow, mithGrapple, combatGear, food, crystalChime),
			List.of(earmuffsOrSlayerHelmet, runRestoreItems)
		);

		var theWarpedDepthsPanel = new PanelDetails(
			"The Warped Depths",
			theWarpedDepths.getSteps(),
			List.of(crossbow, mithGrapple, combatGear, food, crystalChime),
			List.of(earmuffsOrSlayerHelmet, runRestoreItems)
		);

		panels.add(startingOffPanel);
		panels.add(unveilTheEvilCreature);
		panels.add(informKingBolrenPanel);
		panels.add(findLongramblePanel);
		panels.add(theWarpedDepthsPanel);

		return panels;
	}
}
