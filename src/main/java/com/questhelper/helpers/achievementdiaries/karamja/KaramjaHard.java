/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.helpers.achievementdiaries.karamja;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarPlayer;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

public class KaramjaHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement oomlieWrap, pureEssence, natureTalismanOrAbyss, coins, rawKarambwan, axe, machete, pickaxe, lockpick, crossbow, mithGrapple, antidragonShield, antifirePotions, combatGear, fightCaveCombatGear;

	// Items recommended
	ItemRequirement food, antipoison;

	// Other reqs
	Requirement combat100, agility53, cooking30, magic59, mining52, ranged42, runecrafting44,
		slayer50, smithing40, strength50, thieving50, woodcutting34;

	Requirement taiBwoWannaiTrio, legendsQuest, shiloVillage;

	Requirement notBecomeChampion, notKilledZek, notEatenWrap, notCraftedNature, notCookedKarambwan,
		notKilledDeathwing, notUsedShortcut, notCollectedLeaves, notAssignedTask, notKilledDragon;

	QuestStep enterHole, enterTzhaar, enterHoleChampion, enterTzhaarChampion, becomeChampion,
		enterFightCaves, defeatZek, eatOomlie, enterNatureAltar, craftNatureRune, cookKarambwan;

	QuestStep enterKharaziHole, enterBookcase, enterGates, killDeathwing;

	QuestStep useShortcut, collectPalmLeaves, goUpToDuradel, getTask, enterBrimhavenDungeon,
		killDragon;

	QuestStep claimReward;

	Zone cave, tzhaar, fightCaves, brimhavenDungeon, duradelRoom, natureAltar, deathwingArea1,
		deathwingArea2, deathwingArea3;

	ZoneRequirement inCave, inTzhaar, inFightCaves, inBrimhavenDungeon, atDuradel, inNatureAltar,
		inDeathwingArea1, inDeathwingArea2, inDeathwingArea3;

	ConditionalStep becomeChampionTask, killedZekTask, eatenWrapTask, craftedNatureTask, cookedKarambwanTask,
		killedDeathwingTask, usedShortcutTask, collectedLeavesTask, assignedTaskTask, killedDragonTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		becomeChampionTask = new ConditionalStep(this, enterHoleChampion);
		becomeChampionTask.addStep(inCave, enterTzhaarChampion);
		becomeChampionTask.addStep(inTzhaar, becomeChampion);
		doHard.addStep(notBecomeChampion, becomeChampionTask);

		killedZekTask = new ConditionalStep(this, enterHole);
		killedZekTask.addStep(inCave, enterTzhaar);
		killedZekTask.addStep(inTzhaar, enterFightCaves);
		killedZekTask.addStep(inFightCaves, defeatZek);
		doHard.addStep(notKilledZek, killedZekTask);

		cookedKarambwanTask = new ConditionalStep(this, cookKarambwan);
		doHard.addStep(notCookedKarambwan, cookedKarambwanTask);

		killedDragonTask = new ConditionalStep(this, enterBrimhavenDungeon);
		killedDragonTask.addStep(inBrimhavenDungeon, killDragon);
		doHard.addStep(notKilledDragon, killedDragonTask);

		craftedNatureTask = new ConditionalStep(this, enterNatureAltar);
		craftedNatureTask.addStep(inNatureAltar, craftNatureRune);
		doHard.addStep(notCraftedNature, craftedNatureTask);

		assignedTaskTask = new ConditionalStep(this, goUpToDuradel);
		assignedTaskTask.addStep(atDuradel, getTask);
		doHard.addStep(notAssignedTask, assignedTaskTask);

		collectedLeavesTask = new ConditionalStep(this, collectPalmLeaves);
		doHard.addStep(notCollectedLeaves, collectedLeavesTask);

		killedDeathwingTask = new ConditionalStep(this, enterKharaziHole);
		killedDeathwingTask.addStep(inDeathwingArea1, enterBookcase);
		killedDeathwingTask.addStep(inDeathwingArea2, enterGates);
		killedDeathwingTask.addStep(inDeathwingArea3, killDeathwing);
		doHard.addStep(notKilledDeathwing, killedDeathwingTask);

		usedShortcutTask = new ConditionalStep(this, useShortcut);
		doHard.addStep(notUsedShortcut, usedShortcutTask);

		eatenWrapTask = new ConditionalStep(this, eatOomlie);
		doHard.addStep(notEatenWrap, eatOomlie);

		return doHard;
	}

	@Override
	protected void setupRequirements()
	{
		notBecomeChampion = new VarbitRequirement(3600, 0);
		notKilledZek = new VarbitRequirement(3601, 0);
		notEatenWrap = new VarbitRequirement(3602, 0);
		notCraftedNature = new VarbitRequirement(3603, 0);
		notCookedKarambwan = new VarbitRequirement(3604, 0);
		notKilledDeathwing = new VarbitRequirement(3605, 0);
		notUsedShortcut = new VarbitRequirement(3606, 0);
		notCollectedLeaves = new VarbitRequirement(3607, 4, Operation.LESS_EQUAL);
		notAssignedTask = new VarbitRequirement(3608, 0);
		notKilledDragon = new VarbitRequirement(3609, 0);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notKilledDeathwing).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notKilledDragon);
		oomlieWrap = new ItemRequirement("Oomlie wrap", ItemID.COOKED_OOMLIE_WRAP).showConditioned(notEatenWrap);
		oomlieWrap.setTooltip("You can make one by using a palm leaf on a raw oomlie and cooking it. Both are " +
			"obtained from the Kharazi Jungle");
		pureEssence = new ItemRequirement("Pure essence", ItemID.PURE_ESSENCE).showConditioned(notCraftedNature);
		natureTalismanOrAbyss = new ItemRequirement("Access to the Nature Altar", ItemID.NATURE_TALISMAN)
			.showConditioned(notCraftedNature).isNotConsumed();
		natureTalismanOrAbyss.addAlternates(ItemID.NATURE_TIARA);
		natureTalismanOrAbyss.setTooltip("Nature talisman or tiara");
		rawKarambwan = new ItemRequirement("Raw karambwan", ItemID.RAW_KARAMBWAN).showConditioned(notCookedKarambwan);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(new Conditions(LogicType.OR,
			notCollectedLeaves, notKilledDeathwing, notKilledDragon)).isNotConsumed();
		machete = new ItemRequirement("Any machete", ItemID.MACHETE).showConditioned(new Conditions(LogicType.OR,
			notCollectedLeaves, notKilledDeathwing)).isNotConsumed();
		machete.addAlternates(ItemID.OPAL_MACHETE, ItemID.JADE_MACHETE, ItemID.RED_TOPAZ_MACHETE);
		lockpick = new ItemRequirement("Lockpick, more in case it breaks", ItemID.LOCKPICK)
			.showConditioned(notKilledDeathwing).isNotConsumed();
		crossbow = new ItemRequirement("Any crossbow", ItemID.CROSSBOW).showConditioned(notUsedShortcut).isNotConsumed();
		crossbow.addAlternates(ItemID.BRONZE_CROSSBOW, ItemID.IRON_CROSSBOW, ItemID.STEEL_CROSSBOW,
			ItemID.MITHRIL_CROSSBOW, ItemID.ADAMANT_CROSSBOW, ItemID.RUNE_CROSSBOW, ItemID.DRAGON_CROSSBOW,
			ItemID.BLURITE_CROSSBOW, ItemID.DORGESHUUN_CROSSBOW, ItemID.ARMADYL_CROSSBOW, ItemID.ZARYTE_CROSSBOW);
		mithGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notUsedShortcut).isNotConsumed();
		antidragonShield =
			new ItemRequirement("Anti-dragon shield or DFS", ItemCollections.ANTIFIRE_SHIELDS)
				.showConditioned(notKilledDragon).isNotConsumed();
		antifirePotions = new ItemRequirement("Antifire potions", ItemCollections.ANTIFIRE)
			.showConditioned(notKilledDragon);

		combatGear = new ItemRequirement("Combat gear to defeat a deathwing and a metal dragon", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		fightCaveCombatGear = new ItemRequirement("Combat gear to reach wave 31 in the Fight Caves and defeat a " +
			"Ket-Zek", -1, -1).isNotConsumed();
		fightCaveCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS, -1);

		inCave = new ZoneRequirement(cave);
		inTzhaar = new ZoneRequirement(tzhaar);
		inFightCaves = new ZoneRequirement(fightCaves);
		inBrimhavenDungeon = new ZoneRequirement(brimhavenDungeon);
		atDuradel = new ZoneRequirement(duradelRoom);
		inNatureAltar = new ZoneRequirement(natureAltar);
		inDeathwingArea1 = new ZoneRequirement(deathwingArea1);
		inDeathwingArea2 = new ZoneRequirement(deathwingArea2);
		inDeathwingArea3 = new ZoneRequirement(deathwingArea3);

		combat100 = new CombatLevelRequirement(100);
		agility53 = new SkillRequirement(Skill.AGILITY, 53);
		cooking30 = new SkillRequirement(Skill.COOKING, 30);
		magic59 = new SkillRequirement(Skill.MAGIC, 59);
		mining52 = new SkillRequirement(Skill.MINING, 52);
		ranged42 = new SkillRequirement(Skill.RANGED, 42);
		runecrafting44 = new SkillRequirement(Skill.RUNECRAFT, 44);
		slayer50 = new SkillRequirement(Skill.SLAYER, 50);
		smithing40 = new SkillRequirement(Skill.SMITHING, 40);
		strength50 = new SkillRequirement(Skill.STRENGTH, 50);
		thieving50 = new SkillRequirement(Skill.THIEVING, 50);
		woodcutting34 = new SkillRequirement(Skill.WOODCUTTING, 34);

		taiBwoWannaiTrio = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED);
		legendsQuest = new VarplayerRequirement(QuestVarPlayer.QUEST_LEGENDS_QUEST.getId(), 1,
			Operation.GREATER_EQUAL, "Partial completion of Legends' Quest");
		shiloVillage = new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		cave = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		tzhaar = new Zone(new WorldPoint(2360, 5056, 0), new WorldPoint(2560, 5185, 0));
		fightCaves = new Zone(new WorldPoint(2368, 5052, 0), new WorldPoint(2430, 5121, 0));
		brimhavenDungeon = new Zone(new WorldPoint(2560, 9411, 0), new WorldPoint(2752, 9599, 2));
		duradelRoom = new Zone(new WorldPoint(2863, 2964, 1), new WorldPoint(2876, 2986, 1));
		natureAltar = new Zone(new WorldPoint(2374, 4809, 0), new WorldPoint(2421, 4859, 0));
		deathwingArea1 = new Zone(new WorldPoint(2764, 9316, 0), new WorldPoint(2798, 9345, 0));
		deathwingArea2 = new Zone(new WorldPoint(2799, 9314, 0), new WorldPoint(2815, 9343, 0));
		deathwingArea3 = new Zone(new WorldPoint(2748, 9272, 0), new WorldPoint(2818, 9313, 0));
	}

	public void setupSteps()
	{
		enterHole = new ObjectStep(this, ObjectID.ROCKS_11441, new WorldPoint(2857, 3169, 0),
			"Enter Mor Ul Rek under the Karamja Volcano.");
		enterTzhaar = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_11835, new WorldPoint(2864, 9572, 0),
			"Enter Mor Ul Rek under the Karamja Volcano.");
		enterFightCaves = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_11833, new WorldPoint(2438, 5167, 0),
			"Enter the fight caves in Mor Ul Rek, ready to reach at least wave 31 to defeat a Ket-Zek.",
			fightCaveCombatGear);
		enterFightCaves.addSubSteps(enterHole, enterTzhaar);

		enterHoleChampion = new ObjectStep(this, ObjectID.ROCKS_11441, new WorldPoint(2857, 3169, 0),
			"Enter Mor Ul Rek under the Karamja Volcano.");
		enterTzhaarChampion = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_11835, new WorldPoint(2864, 9572, 0),
			"Enter the cave that leads to Mor Ul Rek under the Karamja Volcano.");
		becomeChampion = new DetailedQuestStep(this, new WorldPoint(2399, 5173, 0), "Win in the Fight Pits in the " +
			"north west of Mor Ul Rek. You can ask a friend to come lose to you.");
		becomeChampion.addSubSteps(enterHoleChampion, enterTzhaarChampion);
		defeatZek = new NpcStep(this, NpcID.KETZEK, "Reach at least wave 31 to defeat Ket-Zek.");
		eatOomlie = new DetailedQuestStep(this, "Eat an oomlie wrap.", oomlieWrap.highlighted());
		enterNatureAltar = new ObjectStep(this, NullObjectID.NULL_34821, new WorldPoint(2869, 3019, 0),
			"Enter the nature altar, either from the ruin or through the Abyss.", natureTalismanOrAbyss, pureEssence);
		craftNatureRune = new ObjectStep(this, ObjectID.ALTAR_34768, new WorldPoint(2400, 4841, 0),
			"Craft a nature rune.", pureEssence);
		cookKarambwan = new DetailedQuestStep(this, "Cook a raw karambwan.", rawKarambwan);

		enterKharaziHole = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the " +
			"Mossy Rocks in the north west of the Kharazi, and follow the cavern to kill a deathwing.", machete, axe,
			pickaxe, lockpick);
		enterKharaziHole.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		enterBookcase = new ObjectStep(this, ObjectID.BOOKCASE_2911, new WorldPoint(2796, 9339, 0), "Right-click search the bookcase and slide past it.");
		enterBookcase.addDialogStep("Yes please!");
		enterGates = new ObjectStep(this, ObjectID.ANCIENT_GATE_2922, new WorldPoint(2810, 9314, 0), "Smash through " +
			"the boulders and enter the gate at the end of the corridor.", pickaxe, lockpick);
		enterGates.addDialogStep("Yes, I'm very strong, I'll force them open.");
		killDeathwing = new NpcStep(this, NpcID.DEATH_WING, new WorldPoint(2810, 9300, 0),
			"Kill a death wing.");
		enterKharaziHole.addSubSteps(enterBookcase, enterGates, killDeathwing);

		useShortcut = new ObjectStep(this, ObjectID.STRONG_TREE_17074, new WorldPoint(2874, 3135, 0),
			"Grapple across the shortcut south of Musa Point.", crossbow.equipped(), mithGrapple.equipped());
		collectPalmLeaves = new ObjectStep(this, ObjectID.LEAFY_PALM_TREE, new WorldPoint(2845, 2915, 0),
			"Shake leafy palm trees in the Kharazi Jungle and pick up 5 palm leaves. You can pick up and drop the " +
				"same leaf for this task."
			, axe, machete, pickaxe, lockpick);
		goUpToDuradel = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2871, 2971, 0),
			"Climb the ladder to Duradel.");
		getTask = new NpcStep(this, NpcID.DURADEL, new WorldPoint(2869, 2982, 1),
			"Get a task from Duradel.");
		enterBrimhavenDungeon = new ObjectStep(this, ObjectID.DUNGEON_ENTRANCE_20876, new WorldPoint(2745, 3155, 0),
			"Enter Brimhaven Dungeon.", axe, coins.quantity(875), combatGear, antidragonShield);
		killDragon = new NpcStep(this, NpcID.BRONZE_DRAGON, new WorldPoint(2735, 9488, 0),
			"Kill any metal dragon in the south of the dungeon.", combatGear, antidragonShield);

		claimReward = new NpcStep(this, NpcID.PIRATE_JACKIE_THE_FRUIT, new WorldPoint(2810, 3192, 0),
			"Talk to Pirate Jackie the Fruit in Brimhaven to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return Arrays.asList(oomlieWrap, pureEssence, natureTalismanOrAbyss, coins.quantity(875), rawKarambwan, axe,
			machete, pickaxe, lockpick, crossbow, mithGrapple, antidragonShield, combatGear, fightCaveCombatGear);
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return Arrays.asList(food, antipoison, antifirePotions);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(100));
		reqs.add(new SkillRequirement(Skill.AGILITY, 53));
		reqs.add(new SkillRequirement(Skill.COOKING, 30));
		reqs.add(new SkillRequirement(Skill.MINING, 52));
		reqs.add(new SkillRequirement(Skill.RANGED, 42));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 44));
		reqs.add(new SkillRequirement(Skill.SLAYER, 50));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 50));
		reqs.add(new SkillRequirement(Skill.THIEVING, 50));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 15));

		reqs.add(new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED));
		reqs.add(new VarplayerRequirement(QuestVarPlayer.QUEST_LEGENDS_QUEST.getId(), 1,
			Operation.GREATER_EQUAL, "Partial completion of Legends' Quest"));

		return reqs;
	}

	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Karamja Gloves (3)", ItemID.KARAMJA_GLOVES_3, 1),
			new ItemReward("10,000 Exp. Lamp (Any skill above level 40)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited Teleports to the underground Shilo Village mine"));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Multiple enemies in the Fight Caves up to a Ket-Zek (level 360)",
			"Death wing (level 83)", "Bronze (level 131), iron (level 189) or steel dragon (level 246)");
	}
	
	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails championSteps = new PanelDetails("Fight Pit Champion", Arrays.asList(enterHoleChampion,
			enterTzhaarChampion, becomeChampion));
		championSteps.setDisplayCondition(notBecomeChampion);
		championSteps.setLockingStep(becomeChampionTask);
		allSteps.add(championSteps);

		PanelDetails ketZekSteps = new PanelDetails("Kill Ket-Zek", Arrays.asList(enterTzhaar, enterFightCaves,
			defeatZek), fightCaveCombatGear, food);
		ketZekSteps.setDisplayCondition(notKilledZek);
		ketZekSteps.setLockingStep(killedZekTask);
		allSteps.add(ketZekSteps);

		PanelDetails cookedKaramSteps = new PanelDetails("Thoroughly Cook Karambwan",
			Collections.singletonList(cookKarambwan), cooking30, taiBwoWannaiTrio, rawKarambwan);
		cookedKaramSteps.setDisplayCondition(notCookedKarambwan);
		cookedKaramSteps.setLockingStep(cookedKarambwanTask);
		allSteps.add(cookedKaramSteps);

		PanelDetails killDragonSteps = new PanelDetails("Kill Metal Dragon", Arrays.asList(enterBrimhavenDungeon,
			killDragon), combatGear, antidragonShield, axe, coins.quantity(875));
		killDragonSteps.setDisplayCondition(notKilledDragon);
		killDragonSteps.setLockingStep(killedDragonTask);
		allSteps.add(killDragonSteps);

		PanelDetails natureRuneSteps = new PanelDetails("Craft Nature Rune", Arrays.asList(enterNatureAltar,
			craftNatureRune), runecrafting44, pureEssence, natureTalismanOrAbyss);
		natureRuneSteps.setDisplayCondition(notCraftedNature);
		natureRuneSteps.setLockingStep(craftedNatureTask);
		allSteps.add(natureRuneSteps);

		PanelDetails assignedTaskSteps = new PanelDetails("Get Task From Duradel", Arrays.asList(goUpToDuradel,
			getTask), combat100, slayer50, shiloVillage);
		assignedTaskSteps.setDisplayCondition(notAssignedTask);
		assignedTaskSteps.setLockingStep(assignedTaskTask);
		allSteps.add(assignedTaskSteps);

		PanelDetails collectLeavesSteps = new PanelDetails("Collect 5 Palm Leaves",
			Collections.singletonList(collectPalmLeaves), legendsQuest, axe, machete);
		collectLeavesSteps.setDisplayCondition(notCollectedLeaves);
		collectLeavesSteps.setLockingStep(collectedLeavesTask);
		allSteps.add(collectLeavesSteps);

		PanelDetails deathwingSteps = new PanelDetails("Kill a Deathwing", Arrays.asList(enterKharaziHole,
			enterBookcase, enterGates, killDeathwing), woodcutting34, strength50, agility53, thieving50, mining52,
			legendsQuest, axe, machete, pickaxe, lockpick);
		deathwingSteps.setDisplayCondition(notKilledDeathwing);
		deathwingSteps.setLockingStep(killedDeathwingTask);
		allSteps.add(deathwingSteps);

		PanelDetails shortcutSteps = new PanelDetails("Use Crossbow Shortcut", Collections.singletonList(useShortcut),
			crossbow, mithGrapple);
		shortcutSteps.setDisplayCondition(notUsedShortcut);
		shortcutSteps.setLockingStep(usedShortcutTask);
		allSteps.add(shortcutSteps);

		PanelDetails eatOomlieWrapSteps = new PanelDetails("Eat Oomlie Wrap", Collections.singletonList(eatOomlie),
			oomlieWrap);
		eatOomlieWrapSteps.setDisplayCondition(notEatenWrap);
		eatOomlieWrapSteps.setLockingStep(eatenWrapTask);
		allSteps.add(eatOomlieWrapSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
