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
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import java.util.*;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_PATH_OF_GLOUPHRIE
)
public class ThePathOfGlouphrie extends BasicQuestHelper
{
	ZoneRequirement inTreeGnomeVillageMiddle;


	/// Required items
	private ItemRequirement crossbow;
	private ItemRequirement mithGrapple;
	private ItemRequirement treeGnomeVillageDungeonKey;
	private ItemRequirement combatGear;
	private ItemRequirement food;

	/// Recommended items
	private ItemRequirement earmuffsOrSlayerHelmet;
	private TeleportItemRequirement royalSeedPod;
	private TeleportItemRequirement tpToSpiritTree;
	private TeleportItemRequirement fairyRing;
	private ItemRequirement runRestoreItems;
	private FreeInventorySlotRequirement freeInventorySlots;

	/// Starting off
	// Talk to King Bolren
	private NpcStep talkToKingBolren;
	// Talk to King Bolren again
	private NpcStep talkToKingBolrenAgain;
	// Talk to Golrie in the dungeon
	private ObjectStep enterTreeGnomeVillageMazeFromMiddle;
	private ObjectStep climbDownIntoTreeGnomeVillageDungeon;
	private NpcStep talkToGolrie;
	// Enter the storeroom to the east
	private ObjectStep enterStoreroomEnterTreeGnomeVillageMazeFromMiddle;
	private ObjectStep enterStoreroomClimbDownIntoTreeGnomeVillageDungeon;
	private ObjectStep enterStoreroom;
	// Solve the puzzle
	private MonolithPuzzle solveMonolithPuzzle;
	private MonolithPuzzle solveYewnocksMachinePuzzle;
	private NpcStep talkToGianneJnr;
	private NpcStep talkToLongramble;
	private Zone treeGnomeVillageMiddle1;
	private Zone treeGnomeVillageMiddle2;
	private Zone treeGnomeVillageMiddle3;
	private Zone treeGnomeVillageDungeon;
	private ZoneRequirement inTreeGnomeVillageDungeon;
	private ZoneRequirement inStoreroom;
	private Zone storeroomZone;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		var startQuest = new ConditionalStep(this, talkToKingBolren);

		var convinceBolren = new ConditionalStep(this, talkToKingBolrenAgain);

		var golrie = new ConditionalStep(this, climbDownIntoTreeGnomeVillageDungeon);
		golrie.addStep(inTreeGnomeVillageDungeon, talkToGolrie);
		golrie.addStep(inTreeGnomeVillageMiddle, enterTreeGnomeVillageMazeFromMiddle);

		var storeroom = new ConditionalStep(this, enterStoreroomClimbDownIntoTreeGnomeVillageDungeon);
		storeroom.addStep(inTreeGnomeVillageDungeon, enterStoreroom);
		storeroom.addStep(inTreeGnomeVillageMiddle, enterStoreroomEnterTreeGnomeVillageMazeFromMiddle);

		var solveMonolithPuzzleStep = new ConditionalStep(this, enterStoreroom);
		solveMonolithPuzzleStep.addStep(inStoreroom, solveMonolithPuzzle);

		var solveYewnocksMachinePuzzleStep = new ConditionalStep(this, enterStoreroom);
		solveYewnocksMachinePuzzleStep.addStep(inStoreroom, solveYewnocksMachinePuzzle);

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(0, startQuest)
			.put(2, convinceBolren)
			.put(4, golrie)
			.put(6, storeroom)
			.put(8, solveMonolithPuzzleStep)
			.put(10, solveMonolithPuzzleStep)
			.put(12, solveMonolithPuzzleStep)
			.put(14, solveYewnocksMachinePuzzleStep)
			.build();
	}

	public void setupItemRequirements()
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
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		// Recommended items
		var lumbridgeEliteComplete = new QuestRequirement(QuestHelperQuest.LUMBRIDGE_ELITE, QuestState.FINISHED);
		earmuffsOrSlayerHelmet = new ItemRequirement("Earmuffs or a Slayer helmet", ItemCollections.EAR_PROTECTION, 1, true).highlighted();
		// TODO: Change this to a "TP to the grand tree"
		royalSeedPod = new TeleportItemRequirement("Royal seed pod", ItemID.ROYAL_SEED_POD, 1);
		// TODO: Fix quantity
		tpToSpiritTree = new TeleportItemRequirement("Teleport to nearby Spirit Tree", ItemID.VARROCK_TELEPORT, -1);
		fairyRing = new TeleportItemRequirement("Dramen staff", ItemCollections.FAIRY_STAFF, 1);
		fairyRing.setConditionToHide(lumbridgeEliteComplete);
		runRestoreItems = new ItemRequirement("Several run restore items", ItemCollections.RUN_RESTORE_ITEMS, -1);
		freeInventorySlots = new FreeInventorySlotRequirement(11);
		// TODO: recommend the toad legs to get a mint cake?
	}

	public void setupZones()
	{
		treeGnomeVillageMiddle1 = new Zone(new WorldPoint(2514, 3161, 0), new WorldPoint(2542, 3175, 0));
		treeGnomeVillageMiddle2 = new Zone(new WorldPoint(2543, 3167, 0), new WorldPoint(2547, 3172, 0));
		treeGnomeVillageMiddle3 = new Zone(new WorldPoint(2522, 3158, 0), new WorldPoint(2542, 3160, 0));
		treeGnomeVillageDungeon = new Zone(new WorldPoint(2560, 4426, 0), new WorldPoint(2627, 4477, 0));
		storeroomZone = new Zone(11074);
	}

	public void setupConditions()
	{
		inTreeGnomeVillageMiddle = new ZoneRequirement(treeGnomeVillageMiddle1, treeGnomeVillageMiddle2, treeGnomeVillageMiddle3);
		inTreeGnomeVillageDungeon = new ZoneRequirement(treeGnomeVillageDungeon);
		inStoreroom = new ZoneRequirement(storeroomZone);
	}

	public void setupSteps()
	{
		/// Starting off
		// Talk to King Bolren
		var teleToBolren = new TeleportItemRequirement("Spirit tree to Tree Gnome Village [1]", -1, -1);
		talkToKingBolren = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren in the Tree Gnome Village");
		talkToKingBolren.addDialogSteps("Yes.");
		talkToKingBolren.addTeleport(teleToBolren);

		// Talk to King Bolren again
		talkToKingBolrenAgain = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2542, 3169, 0), "Talk to King Bolren again");

		// TODO: Add step for freeing Golrie if the user hasn't started Roving Elves

		// Talk to Golrie
		enterTreeGnomeVillageMazeFromMiddle = new ObjectStep(this, ObjectID.LOOSE_RAILING_2186, new WorldPoint(2515, 3161, 0), "Talk to Golrie in the Tree Gnome Village dungeon");
		climbDownIntoTreeGnomeVillageDungeon = new ObjectStep(this, ObjectID.LADDER_5250, new WorldPoint(2533, 3155, 0), "Talk to Golrie in the Tree Gnome Village dungeon");
		talkToGolrie = new NpcStep(this, NpcID.GOLRIE, new WorldPoint(2580,  4450, 0), "Talk to Golrie in the Tree Gnome Village dungeon");
		talkToGolrie.addDialogSteps("I need your help with a device.");
		talkToGolrie.addSubSteps(enterTreeGnomeVillageMazeFromMiddle, climbDownIntoTreeGnomeVillageDungeon);
		// TODO: Substep to squeeze through the loose railing if you're inside the village
		// TODO: Substep to climb down the dungeon

		enterStoreroomEnterTreeGnomeVillageMazeFromMiddle = enterTreeGnomeVillageMazeFromMiddle.copy();
		enterStoreroomClimbDownIntoTreeGnomeVillageDungeon = climbDownIntoTreeGnomeVillageDungeon.copy();
		enterStoreroom = new ObjectStep(this, ObjectID.TUNNEL_49620, new WorldPoint(2608, 4451, 0), "Enter the storeroom to the east in the Tree Gnome Village dungeon");
		enterStoreroomEnterTreeGnomeVillageMazeFromMiddle.setText(enterStoreroom.getText());
		enterStoreroomClimbDownIntoTreeGnomeVillageDungeon.setText(enterStoreroom.getText());

		/// Storeroom monolith puzzle
		solveMonolithPuzzle = new MonolithPuzzle(this);

		solveYewnocksMachinePuzzle = new MonolithPuzzle(this);

		enterStoreroom.addSubSteps(enterStoreroomEnterTreeGnomeVillageMazeFromMiddle, enterStoreroomClimbDownIntoTreeGnomeVillageDungeon);

		// Push first monolith once
		// Search chest, drop items, search chest, drop items, search chest, pick up discs from the ground
		// Push west monolith north once
		// Push the northern one east once
		// Search the white chest for key & book
		// Push small monolith
		// Push northern monolith west
		// Open golden chest
		// Inspect then click the singing bowl to create a crystal chime (dialog step "Yes.")
		// Push final monolith west
		// Open the gate
		// Click the lectern
		// View chapter one (need to inspect widget)
		// Wait for cutscene to finish
		// View chapter two (need to inspect widget)
		// Wait for cutscene to finish
		// View chapter three (need to inspect widget)
		// Wait for cutscene to finish

		// Click Yewnock's Machine to unlock it

		/// Move back to King Bolren
		// Add guide for walking back, but mention that you can also teleport there with a spirit tree
		// Attack the Evil Creature
		// Talk to KingBolren again
		// Gear up for combat

		// Talk to Gianne Junior in Tree Gnome Stronghold
		// TODO: Add WorldPoint
		talkToGianneJnr = new NpcStep(this, NpcID.GIANNE_JNR, "Talk to Gianne jnr. in Tree Gnome Stronghold to ask for Longramble's whereabouts.");
		// TODO: Add dialog step

		talkToLongramble = new NpcStep(this, NpcID.LONGRAMBLE, "Talk to Longramble");
		// TODO: Add substep to grapple over the river
		// TODO: Add teleport step (castle wars teleport or fairy ring to BKP

		// Talk to the spirit tree
		// Watch cutscene
		// Use the Crystal chime on the spirit tree
		// Watch cutscene
		// Equip combat gear, head west and enter the dungeon (activate Protect from Missiles)
		// Open gate, keep heading east
		// Climb down ladder
		// Climb up ladder xd
		// Walk west / north-west (don't step in the tar)
		// Open metal gates
		// Climb down ladder
		// Climb up ladder
		// Go north, west, north
		// At crossroads, go east
		// They attack with both ranged & melee, you can safespot them
		// Once they're dead, enter the eastern room
	}

	@Override
	public void setupRequirements()
	{

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(
			crossbow,
			mithGrapple,
			treeGnomeVillageDungeonKey,
			combatGear,
			food
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(
			earmuffsOrSlayerHelmet,
			royalSeedPod,
			tpToSpiritTree,
			fairyRing,
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
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		var startingOff = new PanelDetails(
			"Starting off",
			List.of(talkToKingBolren, talkToKingBolrenAgain, talkToGolrie),
			List.of(freeInventorySlots),
			List.of()
		);

		var puzzleSteps = new ArrayList<QuestStep>();
		puzzleSteps.add(enterStoreroom);
		puzzleSteps.add(solveMonolithPuzzle);
		puzzleSteps.add(solveMonolithPuzzle);
		// puzzleSteps.addAll(solvePuzzle.getSteps());
		var craftCrystalChime = new PanelDetails(
			"Crafting the Crystal chime",
			puzzleSteps,
			List.of(),
			List.of(freeInventorySlots)
		);

		panels.add(startingOff);
		panels.add(craftCrystalChime);

		return panels;
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
}
