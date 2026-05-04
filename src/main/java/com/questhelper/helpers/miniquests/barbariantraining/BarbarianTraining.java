/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.miniquests.barbariantraining;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.config.ConfigKeys;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.MultiChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitBuilder;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.*;

public class BarbarianTraining extends BasicQuestHelper
{
	// Items Required
	ItemRequirement barbFishingRod;
	ItemRequirement tinderbox;
	ItemRequirement bow;
	ItemRequirement knife;
	ItemRequirement fish;
	ItemRequirement combatGear;
	ItemRequirement antifireShield;
	ItemRequirement chewedBones;
	ItemRequirement bronzeBar;
	ItemRequirement logs;
	ItemRequirement hammer;
	ItemRequirement roe;
	ItemRequirement attackPotion;
	ItemRequirement sapling;
	ItemRequirement seed;
	ItemRequirement spade;
	ItemRequirement oakLogs;
	ItemRequirement axe;
	ItemRequirement feathers;
	ItemRequirement barbarianAttackPotion;

	// Items recommended
	ItemRequirement gamesNecklace;
	ItemRequirement catherbyTeleport;

	Requirement fishing48;
	Requirement agility15;
	Requirement strength15;
	Requirement fishing55;
	Requirement strength35;
	Requirement firemaking35;
	Requirement crafting11;
	Requirement farming15;
	Requirement smithing5;

	QuestRequirement druidicRitual;
	QuestRequirement taiBwoWannaiTrio;

	Requirement taskedWithPyre;

	Requirement chewedBonesNearby;

	Requirement sacrificedRemains;

	DetailedQuestStep talkToOttoAboutFishing;
	DetailedQuestStep searchBed;
	DetailedQuestStep catchFish;
	DetailedQuestStep talkToOttoAfterFish;

	DetailedQuestStep talkToOttoAboutBarehanded;
	DetailedQuestStep fishHarpoon;
	DetailedQuestStep talkToOttoAfterHarpoon;

	DetailedQuestStep talkToOttoAboutBow;
	DetailedQuestStep lightLogWithBow;
	DetailedQuestStep talkToOttoAfterBow;

	DetailedQuestStep talkToOttoAboutPyre;
	DetailedQuestStep enterWhirlpool;
	DetailedQuestStep goDownToBrutalGreen;
	DetailedQuestStep goUpToMithrilDragons;
	DetailedQuestStep killMithrilDragons;
	DetailedQuestStep pickupChewedBones;
	DetailedQuestStep useLogOnPyre;
	DetailedQuestStep talkToOttoAfterPyre;

	DetailedQuestStep talkToOttoAboutFarming;
	DetailedQuestStep plantSeed;
	DetailedQuestStep talkToOttoAfterPlantingSeed;

	DetailedQuestStep talkToOttoAboutPots;
	DetailedQuestStep plantSapling;
	DetailedQuestStep talkToOttoAfterSmashingPot;

	DetailedQuestStep talkToOttoAboutSpears;
	DetailedQuestStep makeBronzeSpear;
	DetailedQuestStep talkToOttoAfterBronzeSpear;
	DetailedQuestStep talkToOttoAboutHastae;
	DetailedQuestStep makeBronzeHasta;
	DetailedQuestStep talkToOttoAfterMakingHasta;

	DetailedQuestStep talkToOttoAboutHerblore;
	DetailedQuestStep getBarbRodForHerblore;
	DetailedQuestStep fishForHerblore;
	DetailedQuestStep dissectFish;
	DetailedQuestStep useRoeOnAttackPotion;
	DetailedQuestStep talkToOttoAfterPotion;

	ConditionalStep fishingSteps;
	ConditionalStep harpoonSteps;
	ConditionalStep seedSteps;
	ConditionalStep potSmashingSteps;
	ConditionalStep firemakingSteps;
	ConditionalStep pyreSteps;
	ConditionalStep spearSteps;
	ConditionalStep spearAndHastaeSteps;
	ConditionalStep herbloreSteps;

	Requirement finishedPyre;

	Zone ancientCavernF0;
	Zone ancientCavernF1;
	Zone ancientCavernArrivalRoom;

	ZoneRequirement inAncientCavernF0;
	ZoneRequirement inAncientCavernF1;
	ZoneRequirement inAncientCavernArrivalRoom;

	VarbitRequirement taskedWithFishing;
	VarbitRequirement caughtBarbarianFish;
	VarbitRequirement finishedFishing;

	VarbitRequirement taskedWithHerblore;
	VarbitRequirement madePotion;
	VarbitRequirement finishedHerblore;

	VarbitRequirement taskedWithHarpooning;
	VarbitRequirement caughtFishWithoutHarpoon;
	VarbitRequirement finishedHarpoon;

	VarbitRequirement taskedWithFarming;
	VarbitRequirement plantedSeed;
	VarbitRequirement finishedSeedPlanting;

	VarbitRequirement taskedWithPotSmashing;
	VarbitRequirement smashedPot;
	VarbitRequirement finishedPotSmashing;

	VarbitRequirement taskedWithBowFiremaking;
	VarbitRequirement litFireWithBow;
	VarbitRequirement finishedFiremaking;

	VarbitRequirement taskedWithSpears;
	VarbitRequirement madeSpear;
	VarbitRequirement finishedSpear;

	VarbitRequirement taskedWithHastae;
	VarbitRequirement madeHasta;
	VarbitRequirement finishedHasta;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		// TODO: Finish with an account which has fishing req
		// Fishing
		fishingSteps = new ConditionalStep(this, talkToOttoAboutFishing);
		fishingSteps.addStep(caughtBarbarianFish, talkToOttoAfterFish);
		fishingSteps.addStep(and(taskedWithFishing, barbFishingRod.alsoCheckBank()), catchFish);
		fishingSteps.addStep(taskedWithFishing, searchBed);
		fishingSteps.setLockingCondition(finishedFishing);

		// Herblore
		herbloreSteps = new ConditionalStep(this, talkToOttoAboutHerblore);
		herbloreSteps.addStep(madePotion, talkToOttoAfterPotion);
		herbloreSteps.addStep(and(taskedWithHerblore, roe), useRoeOnAttackPotion);
		herbloreSteps.addStep(and(taskedWithHerblore, fish), dissectFish);
		herbloreSteps.addStep(and(taskedWithHerblore, barbFishingRod.alsoCheckBank()), fishForHerblore);
		herbloreSteps.addStep(taskedWithHerblore, getBarbRodForHerblore);
		herbloreSteps.setLockingCondition(finishedHerblore);

		// Harpoon
		harpoonSteps = new ConditionalStep(this, talkToOttoAboutBarehanded);
		harpoonSteps.addStep(caughtFishWithoutHarpoon, talkToOttoAfterHarpoon);
		harpoonSteps.addStep(taskedWithHarpooning, fishHarpoon);
		harpoonSteps.setLockingCondition(finishedHarpoon);

		// Farming
		potSmashingSteps = new ConditionalStep(this, talkToOttoAboutPots);
		potSmashingSteps.addStep(smashedPot, talkToOttoAfterSmashingPot);
		potSmashingSteps.addStep(taskedWithPotSmashing, plantSapling);
		potSmashingSteps.setLockingCondition(finishedPotSmashing);
		// Completed pot smashing, 9610 2->3


		seedSteps = new ConditionalStep(this, talkToOttoAboutFarming);
		seedSteps.addStep(plantedSeed, talkToOttoAfterPlantingSeed);
		seedSteps.addStep(taskedWithFarming, plantSeed);
		seedSteps.setLockingCondition(finishedSeedPlanting);

		// Firemaking
		firemakingSteps = new ConditionalStep(this, talkToOttoAboutBow);
		firemakingSteps.addStep(litFireWithBow, talkToOttoAfterBow);
		firemakingSteps.addStep(taskedWithBowFiremaking, lightLogWithBow);
		firemakingSteps.setLockingCondition(finishedFiremaking);

		pyreSteps = new ConditionalStep(this, talkToOttoAboutPyre);
		pyreSteps.addStep(and(sacrificedRemains), talkToOttoAfterPyre);
		pyreSteps.addStep(and(taskedWithPyre, chewedBones.alsoCheckBank()), useLogOnPyre);
		pyreSteps.addStep(and(taskedWithPyre, chewedBonesNearby), pickupChewedBones);
		pyreSteps.addStep(and(taskedWithPyre, inAncientCavernArrivalRoom), enterWhirlpool);
		pyreSteps.addStep(and(taskedWithPyre, inAncientCavernF0), goUpToMithrilDragons);
		pyreSteps.addStep(and(taskedWithPyre, inAncientCavernF1), killMithrilDragons);
		pyreSteps.addStep(taskedWithPyre, enterWhirlpool);
		pyreSteps.setLockingCondition(finishedPyre);

		// Smithing
		spearSteps = new ConditionalStep(this, talkToOttoAboutSpears);
		spearSteps.addStep(madeSpear, talkToOttoAfterBronzeSpear);
		spearSteps.addStep(taskedWithSpears, makeBronzeSpear);
		spearSteps.setLockingCondition(finishedSpear);

		spearAndHastaeSteps = new ConditionalStep(this, spearSteps);
		spearAndHastaeSteps.addStep(madeHasta, talkToOttoAfterMakingHasta);
		spearAndHastaeSteps.addStep(taskedWithHastae, makeBronzeHasta);
		spearAndHastaeSteps.addStep(finishedSpear, talkToOttoAboutHastae);
		spearAndHastaeSteps.setLockingCondition(finishedHasta);

		ConditionalStep allSteps = new ConditionalStep(this, fishingSteps);
		allSteps.addStep(nor(finishedFishing), fishingSteps);
		allSteps.addStep(nor(finishedHerblore), herbloreSteps);
		allSteps.addStep(nor(finishedHarpoon), harpoonSteps);
		allSteps.addStep(nor(finishedSeedPlanting), seedSteps);
		allSteps.addStep(nor(finishedPotSmashing), potSmashingSteps);
		allSteps.addStep(nor(finishedFiremaking), firemakingSteps);
		allSteps.addStep(nand(finishedSpear, finishedHasta), spearAndHastaeSteps);
		allSteps.addStep(nor(finishedPyre), pyreSteps);
		allSteps.addDialogSteps("Let's talk about my training.", "I seek more knowledge.");
		allSteps.setCheckAllChildStepsOnListenerCall(true);

		// Controlled by VarbitID.BRUT_MINIQUEST, specifying the number of miniquests completed
		steps.put(0, allSteps);
		steps.put(1, allSteps);
		steps.put(2, allSteps);
		steps.put(3, allSteps);
		steps.put(4, allSteps);
		steps.put(5, allSteps);
		steps.put(6, allSteps);
		steps.put(7, allSteps);
		steps.put(8, allSteps);
		steps.put(9, allSteps);
		steps.put(10, allSteps);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		barbFishingRod = new ItemRequirement("Barbarian rod", ItemID.BRUT_FISHING_ROD);
		barbFishingRod.addAlternates(ItemID.FISHINGROD_PEARL_BRUT);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		bow = new ItemRequirement("Any bow", ItemCollections.BOWS);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		fish = new ItemRequirement("Leaping trout/salmon/sturgeon", ItemID.BRUT_STURGEON);
		fish.addAlternates(ItemID.BRUT_SPAWNING_SALMON, ItemID.BRUT_SPAWNING_TROUT);
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		antifireShield = new ItemRequirement("Anti-dragon shield or DFS", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();
		chewedBones = new ItemRequirement("Chewed bones", ItemID.BRUT_BARBARIAN_BONES);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		roe = new ItemRequirement("Roe", ItemID.BRUT_ROE);
		attackPotion = new ItemRequirement("Attack potion (2)", ItemID._2DOSE1ATTACK);
		sapling = new ItemRequirement("Any sapling you can plant", ItemCollections.TREE_SAPLINGS);
		sapling.addAlternates(ItemCollections.FRUIT_TREE_SAPLINGS);
		seed = new ItemRequirement("Any seed that can be planted directly", ItemCollections.ALLOTMENT_SEEDS);
		seed.addAlternates(ItemCollections.HERB_SEEDS);
		seed.addAlternates(ItemCollections.FLOWER_SEEDS);
		seed.addAlternates(ItemCollections.BUSH_SEEDS);
		seed.addAlternates(ItemCollections.HOPS_SEEDS);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		oakLogs = new ItemRequirement("Oak logs", ItemID.OAK_LOGS);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		feathers = new ItemRequirement("Feathers", ItemID.FEATHER);

		// Recommended
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
		gamesNecklace.setChargedItem(true);
		catherbyTeleport = new ItemRequirement("Catherby teleport for fishing", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
		catherbyTeleport.addAlternates(ItemID.POH_TABLET_CAMELOTTELEPORT);

		// Quest items
		barbarianAttackPotion = new ItemRequirement("Attack mix (2)", ItemID.BRUTAL_2DOSE1ATTACK);

		druidicRitual = new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED);
		taiBwoWannaiTrio = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED);
		fishing55 = new SkillRequirement(Skill.FISHING, 55, true);
		fishing48 = new SkillRequirement(Skill.FISHING, 48, true);
		agility15 = new SkillRequirement(Skill.AGILITY, 15);
		strength15 = new SkillRequirement(Skill.STRENGTH, 15);
		strength35 = new SkillRequirement(Skill.STRENGTH, 35);
		firemaking35 = new SkillRequirement(Skill.FIREMAKING, 35);
		crafting11 = new SkillRequirement(Skill.CRAFTING, 11);
		farming15 = new SkillRequirement(Skill.FARMING, 15, true);
		smithing5 = new SkillRequirement(Skill.SMITHING, 5, true);

		var barbFishing = new VarbitBuilder(VarbitID.BRUT_FISHING_R);
		taskedWithFishing = barbFishing.eq(1);
		caughtBarbarianFish = barbFishing.eq(2);
		finishedFishing = barbFishing.eq(3);
		finishedFishing.setDisplayText("Finished Barbarian Fishing");

		var barbHerblore = new VarbitBuilder(VarbitID.BRUT_HERB_POTION);
		taskedWithHerblore = barbHerblore.eq(1);
		madePotion = barbHerblore.eq(2);
		finishedHerblore = barbHerblore.eq(3);
		finishedHerblore.setDisplayText("Finished Barbarian Herblore");

		var barbHarpooning = new VarbitBuilder(VarbitID.BRUT_FISHING_S);
		taskedWithHarpooning = barbHarpooning.eq(1);
		caughtFishWithoutHarpoon = barbHarpooning.eq(2);
		finishedHarpoon = barbHarpooning.eq(3);
		finishedHarpoon.setDisplayText("Finished Barbarian Harpooning");

		var barbPlanting = new VarbitBuilder(VarbitID.BRUT_FARMING_PLANTING);
		taskedWithFarming = barbPlanting.eq(1);
		plantedSeed = barbPlanting.eq(2);
		finishedSeedPlanting = barbPlanting.eq(3);
		finishedSeedPlanting.setDisplayText("Finished Barbarian Planting");

		var barbPotSmashing = new VarbitBuilder(VarbitID.BRUT_FARMING_SMASHING);
		taskedWithPotSmashing = barbPotSmashing.eq(1);
		smashedPot = barbPotSmashing.eq(2);
		finishedPotSmashing = barbPotSmashing.eq(3);
		finishedPotSmashing.setDisplayText("Finished Barbarian Pot Smashing");

		var barbFiremaking = new VarbitBuilder(VarbitID.BRUT_FIRE);
		taskedWithBowFiremaking = barbFiremaking.eq(1);
		litFireWithBow = barbFiremaking.eq(2);
		finishedFiremaking = barbFiremaking.eq(3);
		finishedFiremaking.setDisplayText("Finished Barbarian Firemaking");

		var barbSpear = new VarbitBuilder(VarbitID.BRUT_SMITH_SPEAR);
		taskedWithSpears = barbSpear.eq(1);
		madeSpear = barbSpear.eq(2);
		finishedSpear = barbSpear.eq(3);
		finishedSpear.setDisplayText("Finished Barbarian Spear Smithing");

		var barbHasta = new VarbitBuilder(VarbitID.BRUT_SMITH_HASTA);
		taskedWithHastae = barbHasta.eq(1);
		madeHasta = barbHasta.eq(2);
		finishedHasta = barbHasta.eq(3);
		finishedHasta.setDisplayText("Finished Barbarian Hastae Smithing");
	}

	public void setupConditions()
	{
		// Started tasks
		taskedWithPyre = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Dive into the whirlpool in the lake to the east. The spirits will use their abilities to ensure you arrive in the correct location. Be warned, their influence fades, so you must find y"),
				new DialogRequirement("I will repeat myself fully, since this is quite complex. Listen well."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>create pyre ships")
			)
		);

		// Finished tasks
		finishedPyre = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("On this great day you have my eternal thanks. May you find riches while rescuing my spiritual ancestors in the caverns for many moons to come."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a pyre ship!")
			),
			"Finished Barbarian Pyremaking"
		);

		// Mid-conditions
		sacrificedRemains = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_PYRE_MADE.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The ancient barbarian is laid to rest."),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>create a pyre ship<col=000080>! I should let")
			)
		);

		// For harpooning,
		// You catch a tuna.

		ancientCavernArrivalRoom = new Zone(new WorldPoint(1762, 5364, 1), new WorldPoint(1769, 5359, 1));
		ancientCavernF0 = new Zone(new WorldPoint(1734, 5318, 0), new WorldPoint(1800, 5400, 0));
		ancientCavernF1 = new Zone(new WorldPoint(1734, 5318, 1), new WorldPoint(1800, 5400, 1));
		inAncientCavernArrivalRoom = new ZoneRequirement(ancientCavernArrivalRoom);
		inAncientCavernF0 = new ZoneRequirement(ancientCavernF0);
		inAncientCavernF1 = new ZoneRequirement(ancientCavernF1);

		chewedBonesNearby = new ItemOnTileRequirement(chewedBones);
	}

	public void setupSteps()
	{
		// Barbarian Fishing
		talkToOttoAboutFishing = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about fishing.");
		talkToOttoAboutFishing.addDialogSteps("You think so?", "What can I learn about the use of a fishing rod?");

		searchBed = new ObjectStep(this, ObjectID.BRUT_BARBARIAN_BED, new WorldPoint(2500, 3490, 0), "Search the bed in Otto's hut.");
		catchFish = new NpcStep(this, NpcID._0_39_54_BRUT_FISHING_SPOT, new WorldPoint(2500, 3510, 0),
			"Fish from one of the fishing spots near Otto's hut.", true, barbFishingRod, feathers);

		talkToOttoAfterFish = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto and tell him about your success.");
		talkToOttoAfterFish.addDialogStep("I've fished with a barbarian rod!");

		// Barbarian Harpooning
		talkToOttoAboutBarehanded = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about barehand fishing.");
		talkToOttoAboutBarehanded.addDialogSteps("You think so?", "Please teach me of your cunning with harpoons.");
		// TODO: Update ID and WorldPoint
		fishHarpoon = new NpcStep(this, NpcID._0_44_53_RAREFISH, new WorldPoint(2845, 3429, 0), "Fish in a fishing spot which requires a harpoon WITHOUT a harpoon.", true, fishing55);
		talkToOttoAfterHarpoon = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterHarpoon.addDialogSteps("Barbarian Outpost.", "I've fished with my hands!");
		talkToOttoAfterHarpoon.addTeleport(gamesNecklace);

		// Barbarian Firemaking
		talkToOttoAboutBow = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about firemaking.");
		talkToOttoAboutBow.addDialogSteps("You think so?", "I'm ready for your firemaking wisdom. Please instruct me.");
		lightLogWithBow = new DetailedQuestStep(this, "Use a bow on some oak logs to light it.", bow.highlighted(), oakLogs.highlighted());
		talkToOttoAfterBow = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterBow.addDialogSteps("I've set fire to logs with a bow!");

		talkToOttoAboutPyre = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about advanced firemaking.");
		talkToOttoAboutPyre.addDialogStep("I have completed firemaking with a bow. What follows this?");
		enterWhirlpool = new ObjectStep(this, ObjectID.BRUT_WHIRLPOOL, new WorldPoint(2513, 3509, 0),
			"Jump into the whirlpool north of Otto. Be prepared to fight mithril dragons.", combatGear, antifireShield);
		goDownToBrutalGreen = new ObjectStep(this, ObjectID.BRUT_STAIR_LRG_TOP, new WorldPoint(1769, 5365, 1),
			"Climb down the stairs. There are brutal green dragons there, so have antifire protection and Protect from Magic on!");
		goUpToMithrilDragons = new ObjectStep(this, ObjectID.BRUT_CAVE_STAIRS_LOW, new WorldPoint(1778, 5344, 0),
			"Climb up the stairs to the south-east. Be ready to fight the mithril dragons!");
		killMithrilDragons = new NpcStep(this, NpcID.BRUT_MITHRIL_DRAGON, new WorldPoint(1773, 5348, 1),
			"Kill mithril dragons until you get some chewed bones.");
		pickupChewedBones = new ItemStep(this, "Pick up the chewed bones.", chewedBones);
		useLogOnPyre = new ObjectStep(this, ObjectID.BRUT_BURNED_GROUND, new WorldPoint(2506, 3518, 0),
			"Construct a pyre on the lake near Otto.", logs, chewedBones, tinderbox, axe);
		useLogOnPyre.addDialogStep("I've created a pyre ship!");
		talkToOttoAfterPyre = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto and tell him about the succesful pyre-making.");

		// Barbarian Farming
		talkToOttoAboutFarming = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about farming.");
		talkToOttoAboutFarming.addDialogSteps("You think so?", "How can I use my strength in the ways of agriculture?");
		plantSeed = new DetailedQuestStep(this, "Plant a seed in a patch WITHOUT a dibber. This can fail, so bring multiple and preferably use cheap seeds.",
			seed.quantity(9));
		talkToOttoAfterPlantingSeed = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterPlantingSeed.addDialogStep("I've planted seeds with my fists!");

		// Farming P2
		talkToOttoAboutPots = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about advanced farming.");
		talkToOttoAboutPots.addDialogStep("Are there any other fist related farming activities I can learn?");
		plantSapling = new DetailedQuestStep(this, "Plant a sapling in a tree or fruit tree patch.", sapling, spade);
		talkToOttoAfterSmashingPot = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterSmashingPot.addDialogStep("I've smashed a pot whilst farming!");

		// Barbarian Smithing, REQUIRES barbarian firemaking P1
		talkToOttoAboutSpears = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about spears.");
		talkToOttoAboutSpears.addDialogSteps("Tell me more about the use of spears.");
		makeBronzeSpear = new ObjectStep(this, ObjectID.BRUT_ANVIL, new WorldPoint(2502, 3485, 0),
			"Make a bronze spear on the anvil south of Otto.", bronzeBar, logs, hammer);
		talkToOttoAfterBronzeSpear = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterBronzeSpear.addDialogStep("I've created a spear!");

		talkToOttoAboutHastae = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about hastae.");
		talkToOttoAboutHastae.addDialogStep("Tell me more about the use of spears.");
		makeBronzeHasta = new ObjectStep(this, ObjectID.BRUT_ANVIL, new WorldPoint(2502, 3485, 0),
			"Make a bronze hasta on the anvil south of Otto.", bronzeBar, logs, hammer);
		makeBronzeHasta.addWidgetHighlightWithItemIdRequirement(270, 15, ItemID.BRUT_BRONZE_SPEAR_DUMMY, true);
		talkToOttoAfterMakingHasta = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterMakingHasta.addDialogStep("I've created a hasta!");

		// Barbarian Herblore, REQUIRES Barbarian Fishing
		talkToOttoAboutHerblore = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about herblore.");
		getBarbRodForHerblore = new ObjectStep(this, ObjectID.BRUT_BARBARIAN_BED, new WorldPoint(2500, 3490, 0), "Search the bed in Otto's hut.");
		fishForHerblore = new NpcStep(this, NpcID._0_39_54_BRUT_FISHING_SPOT, new WorldPoint(2500, 3510, 0), "Fish from one of the fishing spots near Otto's hut.", true, barbFishingRod);
		fishForHerblore.addSubSteps(getBarbRodForHerblore);
		dissectFish = new DetailedQuestStep(this, "Dissect the fish with a knife until you get some roe.", knife.highlighted(), fish.highlighted());
		talkToOttoAboutHerblore.addDialogSteps("What was that secret knowledge of herblore we talked of?");
		useRoeOnAttackPotion = new DetailedQuestStep(this, "Use roe on an attack potion (2).", roe.highlighted(), attackPotion.highlighted());
		talkToOttoAfterPotion = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.", barbarianAttackPotion);
		talkToOttoAfterPotion.addDialogStep("I've made a barbarian potion!");
		// LOOKED IN LOG, varplayer 3679 -1->3451
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return new ArrayList<>(
			Arrays.asList(taiBwoWannaiTrio, fishing55, firemaking35, strength15, agility15, farming15, crafting11, smithing5)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(seed, sapling, spade,
			bow, oakLogs, tinderbox, axe,
			feathers, knife,
			hammer, bronzeBar.quantity(2), logs.quantity(3),
			attackPotion, roe,
			antifireShield, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gamesNecklace.quantity(5), catherbyTeleport);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Mithril Dragon (level 304)");
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("If the helper is out of sync, open up the Quest Journal to re-sync it.");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails barbFishing = new PanelDetails("Barbarian Fishing", Arrays.asList(talkToOttoAboutFishing, searchBed, catchFish, talkToOttoAfterFish),
			Arrays.asList(fishing48, agility15, strength15, feathers), Arrays.asList(gamesNecklace, catherbyTeleport));
		barbFishing.setLockingStep(fishingSteps);
		allSteps.add(barbFishing);

		PanelDetails barbHerblore = new PanelDetails("Barbarian Herblore", Arrays.asList(talkToOttoAboutHerblore,
			fishForHerblore, dissectFish, useRoeOnAttackPotion, talkToOttoAfterPotion),
			druidicRitual, finishedFishing, attackPotion, knife, barbFishingRod, feathers);
		barbHerblore.setLockingStep(herbloreSteps);
		allSteps.add(barbHerblore);

		PanelDetails barbHarpooning = new PanelDetails("Barbarian Harpooning", Arrays.asList(talkToOttoAboutBarehanded, fishHarpoon, talkToOttoAfterHarpoon), fishing55, agility15, strength35);
		barbHarpooning.setLockingStep(harpoonSteps);
		allSteps.add(barbHarpooning);

		PanelDetails barbFarming = new PanelDetails("Barbarian Planting",
			Arrays.asList(talkToOttoAboutFarming, plantSeed, talkToOttoAfterPlantingSeed), seed);
		barbFarming.setLockingStep(seedSteps);
		allSteps.add(barbFarming);

		PanelDetails barbSmashing = new PanelDetails("Barb Plant Pot Smashing",
			Arrays.asList(talkToOttoAboutPots, plantSapling, talkToOttoAfterSmashingPot),
			finishedSeedPlanting, farming15, sapling, spade);
		barbSmashing.setLockingStep(potSmashingSteps);
		allSteps.add(barbSmashing);

		PanelDetails barbFiremaking = new PanelDetails("Barbarian Firemaking",
			Arrays.asList(talkToOttoAboutBow, lightLogWithBow, talkToOttoAfterBow),
			firemaking35, crafting11, bow, oakLogs);
		barbFiremaking.setLockingStep(firemakingSteps);
		allSteps.add(barbFiremaking);

		PanelDetails barbSmithing = new PanelDetails("Barbarian Smithing", Arrays.asList(talkToOttoAboutSpears, makeBronzeSpear, talkToOttoAfterBronzeSpear,
			talkToOttoAboutHastae, makeBronzeHasta, talkToOttoAfterMakingHasta), taiBwoWannaiTrio,
			finishedHarpoon, smithing5, hammer, bronzeBar.quantity(2), logs.quantity(2));
		barbSmithing.setLockingStep(spearAndHastaeSteps);
		allSteps.add(barbSmithing);

		PanelDetails barbPyremaking = new PanelDetails("Barbarian Pyremaking",
			Arrays.asList(talkToOttoAboutPyre, enterWhirlpool, goDownToBrutalGreen, goUpToMithrilDragons, killMithrilDragons,
				pickupChewedBones, useLogOnPyre, talkToOttoAfterPyre),
			finishedFiremaking, logs, tinderbox, axe, combatGear, antifireShield);
		barbPyremaking.setLockingStep(pyreSteps);
		allSteps.add(barbPyremaking);

		return allSteps;
	}
}
