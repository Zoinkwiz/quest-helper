/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.gardenoftranquility;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class GardenOfTranquillity extends BasicQuestHelper
{
	ItemRequirement ringOfCharos, rake, dibber, spade, secateurs, wateringCan, trowel, plantCure2, marigoldSeed,
		cabbageSeed3, onionSeed3, hammer, essence, pestle, plantPot, compost2, compost;

	ItemRequirement compost5, fishingRod, varrockTeleport, draynorTeleport, phasTeleport, ardougneTeleport,
		catherbyTeleport, burthorpeTeleport, edgevilleTeleport, lumbridgeTeleport, faladorTeleport, taverleyTeleport;

	ItemRequirement ringOfCharosA, plantCure, runeShards, runeDust, whiteTreeShoot, whiteTreePot, whiteTreeWatered,
		whitetreeSapling, pinkRoseSeed, whiteRoseSeed, redRoseSeed, magicPlantCure, marigold, pinkOrchidSeed3,
		vineSeed4, yellowOrchid3, snowdropSeed4, delphiniumSeed4, trolley;

	Requirement noRing;

	Requirement talkedToElstan, plantedMarigold, harvestedMarigold, givenMarigold;
	Requirement talkedToLyra, plantedOnions, onionsGrown, talkedToLyraAgain;
	Requirement talkedToKragen, plantedCabbages, cabbagesGrown, talkedToKragenAgain;
	Requirement talkedToDantaera, cutShoot, hasPlantedShoot, hasWateredShoot;
	Requirement talkedToAlthric, ringInWell, canPickRoses, hasRedRoseSeed, hasWhiteRoseSeed, hasPinkRoseSeed,
		hasRoseSeeds;
	Requirement ringNotInWell;
	Requirement talkedToBernald, usedCureOnVines, talkedToAlain, hasShards, hasDust,
		hasEnhancedCure, curedVine, gotVineSeeds;
	Requirement notAddedCompost1, notAddedCompost2, notPlantedDelphinium, notPlantedYellowOrchid, notPlantedPinkOrchid,
		notPlantedSnowdrop, notPlantedWhiteTree, notPlantedRedRose, notPlantedPinkRose, notPlantedWhiteRose, notPlantedVine;
	Requirement hasTrolley, plantedEverything, lumbridgeStatueOnTrolley, faladorStatueOnTrolley,
		placedLumbridgeStatue, placedFaladorStatue;

	QuestStep talkToEllamaria, talkToEllamariaForTrolley, talkToWom;
	QuestStep talkToElstan, plantMarigolds, collectMarigold, giveElstanMarigold;
	QuestStep talkToLyra, plantOnions, waitForLyraToGrow, talkToLyraAgain;
	QuestStep talkToKragen, plantCabbage, waitForKragenToGrow, talkToKragenAgain;
	QuestStep talkToDantaera, useSecateursOnWhiteTree, useShootOnPot, useCanOnPot;
	QuestStep talkToAlthric, useCharosOnWell, pickRedRoses, pickPinkRoses, pickWhiteRoses, fishForRing;
	QuestStep talkToBernald, useCureOnVine, talkToAlain, useHammerOnEssence, usePestleOnShards,
		useEssenceOnCure, useMagicalCureOnVine, talkToBernaldForSeeds;

	QuestStep fillPotWithCompost, fillPotWithCompost2, plantDelphinium, plantYellowOrchid,
		plantPinkOrchid, plantSnowdrop, plantWhiteTree, plantRedRose, plantPinkRose,
		plantWhiteRose, plantVine;

	QuestStep useTrolleyOnLumbridgeStatue, pushLumbridgeStatue;

	QuestStep useTrolleyOnFaladorStatue, pushFaladorStatue;

	QuestStep talkToEllmariaAfterGrown, talkToRoald;

	ConditionalStep helpingElstan, helpingLyra, helpingKragen, helpingDantaera, helpingAlthric, gettingRing,
		helpingBernald, gettingLastSeeds, plantGarden;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToEllamaria);
		steps.put(10, talkToWom);
		steps.put(20, talkToWom);
		steps.put(30, talkToWom);

		helpingElstan = new ConditionalStep(this, talkToElstan);
		helpingElstan.addStep(talkedToElstan, plantMarigolds);
		helpingElstan.setLockingCondition(plantedMarigold);

		helpingLyra = new ConditionalStep(this, talkToLyra);
		helpingLyra.addStep(talkedToLyra, plantOnions);
		helpingLyra.setLockingCondition(plantedOnions);

		helpingKragen = new ConditionalStep(this, talkToKragen);
		helpingKragen.addStep(talkedToKragen, plantCabbage);
		helpingKragen.setLockingCondition(plantedCabbages);

		helpingDantaera = new ConditionalStep(this, talkToDantaera);
		helpingDantaera.addStep(hasPlantedShoot, useCanOnPot);
		helpingDantaera.addStep(cutShoot, useShootOnPot);
		helpingDantaera.addStep(talkedToDantaera, useSecateursOnWhiteTree);
		helpingDantaera.setLockingCondition(hasWateredShoot);

		helpingAlthric = new ConditionalStep(this, talkToAlthric);
		helpingAlthric.addStep(new Conditions(talkedToAlthric, canPickRoses, hasWhiteRoseSeed, hasPinkRoseSeed), pickRedRoses);
		helpingAlthric.addStep(new Conditions(talkedToAlthric, canPickRoses, hasWhiteRoseSeed), pickPinkRoses);
		helpingAlthric.addStep(new Conditions(talkedToAlthric, canPickRoses), pickWhiteRoses);
		helpingAlthric.addStep(talkedToAlthric, useCharosOnWell);
		helpingAlthric.setLockingCondition(hasRoseSeeds);

		helpingBernald = new ConditionalStep(this, talkToBernald);
		helpingBernald.addStep(curedVine, talkToBernaldForSeeds);
		helpingBernald.addStep(new Conditions(talkedToAlain, hasEnhancedCure), useMagicalCureOnVine);
		helpingBernald.addStep(new Conditions(talkedToAlain, hasDust), useEssenceOnCure);
		helpingBernald.addStep(new Conditions(talkedToAlain, hasShards), usePestleOnShards);
		helpingBernald.addStep(talkedToAlain, useHammerOnEssence);
		helpingBernald.addStep(usedCureOnVines, talkToAlain);
		helpingBernald.addStep(talkedToBernald, useCureOnVine);
		helpingBernald.setLockingCondition(gotVineSeeds);

		gettingRing = new ConditionalStep(this, fishForRing);
		gettingRing.setLockingCondition(plantedEverything);

		gettingLastSeeds = new ConditionalStep(this, collectMarigold);
		gettingLastSeeds.addStep(new Conditions(cabbagesGrown, talkedToLyraAgain, givenMarigold), talkToKragenAgain);
		gettingLastSeeds.addStep(new Conditions(talkedToLyraAgain, givenMarigold), waitForKragenToGrow);
		gettingLastSeeds.addStep(new Conditions(onionsGrown, givenMarigold), talkToLyraAgain);
		gettingLastSeeds.addStep(givenMarigold, waitForLyraToGrow);
		gettingLastSeeds.addStep(harvestedMarigold, giveElstanMarigold);
		gettingLastSeeds.setLockingCondition(new Conditions(talkedToKragenAgain, talkedToLyraAgain, givenMarigold));

		plantGarden = new ConditionalStep(this, plantWhiteRose);
		plantGarden.addStep(notAddedCompost1, fillPotWithCompost);
		plantGarden.addStep(notAddedCompost2, fillPotWithCompost2);
		plantGarden.addStep(notPlantedPinkOrchid, plantPinkOrchid);
		plantGarden.addStep(notPlantedYellowOrchid, plantYellowOrchid);
		plantGarden.addStep(notPlantedVine, plantVine);
		plantGarden.addStep(notPlantedSnowdrop, plantSnowdrop);
		plantGarden.addStep(notPlantedWhiteTree, plantWhiteTree);
		plantGarden.addStep(notPlantedDelphinium, plantDelphinium);
		plantGarden.addStep(notPlantedPinkRose, plantPinkRose);
		plantGarden.addStep(notPlantedRedRose, plantRedRose);
		plantGarden.addStep(notPlantedWhiteRose, plantWhiteRose);
		plantGarden.setLockingCondition(plantedEverything);

		ConditionalStep getStatues = new ConditionalStep(this, talkToEllamariaForTrolley);
		getStatues.addStep(faladorStatueOnTrolley, pushFaladorStatue);
		getStatues.addStep(new Conditions(placedLumbridgeStatue, hasTrolley), useTrolleyOnFaladorStatue);
		getStatues.addStep(lumbridgeStatueOnTrolley, pushLumbridgeStatue);
		getStatues.addStep(hasTrolley, useTrolleyOnLumbridgeStatue);

		ConditionalStep makingAGarden = new ConditionalStep(this, helpingElstan);
		makingAGarden.addStep(new Conditions(plantedEverything, placedLumbridgeStatue, placedFaladorStatue),
			talkToEllmariaAfterGrown);
		makingAGarden.addStep(plantedEverything, getStatues);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions, plantedCabbages,
			hasRoseSeeds, gotVineSeeds, talkedToKragenAgain, talkedToLyraAgain, givenMarigold), plantGarden);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions, plantedCabbages,
			hasRoseSeeds, gotVineSeeds), gettingLastSeeds);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions, plantedCabbages,
			hasRoseSeeds, ringNotInWell), helpingBernald);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions, plantedCabbages,
			hasRoseSeeds), gettingRing);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions, plantedCabbages, hasWateredShoot),
			helpingAlthric);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions, plantedCabbages), helpingDantaera);
		makingAGarden.addStep(new Conditions(plantedMarigold, plantedOnions), helpingKragen);
		makingAGarden.addStep(plantedMarigold, helpingLyra);
		steps.put(40, makingAGarden);

		steps.put(50, talkToRoald);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		ringOfCharos = new ItemRequirement("Ring of Charos", ItemID.RING_OF_CHAROS).isNotConsumed();
		rake = new ItemRequirement("Rake", ItemID.RAKE).isNotConsumed();
		dibber = new ItemRequirement("Seed dibber", ItemID.DIBBER).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		secateurs = new ItemRequirement("Secateurs", ItemID.SECATEURS).isNotConsumed();
		secateurs.addAlternates(ItemID.FAIRY_ENCHANTED_SECATEURS);
		wateringCan = new ItemRequirement("Watering can", ItemCollections.WATERING_CANS).isNotConsumed();
		trowel = new ItemRequirement("Gardening trowel", ItemID.GARDENING_TROWEL).isNotConsumed();
		plantCure2 = new ItemRequirement("Plant cure", ItemID.PLANT_CURE, 2);
		marigoldSeed = new ItemRequirement("Marigold seed", ItemID.MARIGOLD_SEED);
		cabbageSeed3 = new ItemRequirement("Cabbage seeds (6 to be safe)", ItemID.CABBAGE_SEED, 3);
		onionSeed3 = new ItemRequirement("Onion seeds (6 to be safe)", ItemID.ONION_SEED, 3);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		essence = new ItemRequirement("Rune/Pure essence", ItemID.BLANKRUNE);
		essence.addAlternates(ItemID.BLANKRUNE_HIGH);
		pestle = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		plantPot = new ItemRequirement("Filled plant pot", ItemID.PLANTPOT_COMPOST);
		compost2 = new ItemRequirement("Normal/Super/Ultra compost", ItemID.BUCKET_COMPOST, 2);
		compost2.addAlternates(ItemID.BUCKET_SUPERCOMPOST, ItemID.BUCKET_ULTRACOMPOST);
		compost2.setTooltip("Bottomless bucket will not work for this compost.");
		compost = new ItemRequirement("Normal/Super/Ultra compost", ItemID.BUCKET_COMPOST);
		compost.addAlternates(ItemID.BUCKET_SUPERCOMPOST, ItemID.BUCKET_ULTRACOMPOST);
		compost.setTooltip("Bottomless bucket will not work for these two required composts.");

		compost5 = new ItemRequirement("Normal/Super/Ultra compost", ItemID.BUCKET_COMPOST, 5);
		compost5.addAlternates(ItemID.BUCKET_SUPERCOMPOST, ItemID.BUCKET_ULTRACOMPOST);
		compost5.setTooltip("Bottomless bucket will not work for these five required composts.");
		fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD).isNotConsumed();
		fishingRod.addAlternates(ItemID.FLY_FISHING_ROD);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		draynorTeleport = new ItemRequirement("Draynor teleport", ItemCollections.AMULET_OF_GLORIES);
		draynorTeleport.addAlternates(ItemID.TELETAB_DRAYNOR);
		phasTeleport = new ItemRequirement("Port Phasmatys teleport", ItemID.ECTOPHIAL);
		phasTeleport.addAlternates(ItemID.TELETAB_FENK);
		ardougneTeleport = new ItemRequirement("Ardougne teleport", ItemID.ARDY_CAPE_HARD);
		ardougneTeleport.addAlternates(ItemID.ARDY_CAPE_ELITE, ItemID.POH_TABLET_ARDOUGNETELEPORT, ItemID.ARDY_CAPE_MEDIUM,
			ItemID.ARDY_CAPE_EASY);
		catherbyTeleport = new ItemRequirement("Camelot teleport", ItemID.POH_TABLET_CAMELOTTELEPORT);
		burthorpeTeleport = new ItemRequirement("Burthorpe teleport", ItemCollections.GAMES_NECKLACES);
		edgevilleTeleport = new ItemRequirement("Edgeville teleport", ItemCollections.AMULET_OF_GLORIES);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.POH_TABLET_FALADORTELEPORT);
		taverleyTeleport = new ItemRequirement("Taverley teleport", ItemID.NZONE_TELETAB_TAVERLEY);

		ringOfCharosA = new ItemRequirement("Ring of charos (a)", ItemID.RING_OF_CHAROS_UNLOCKED).isNotConsumed();
		whiteTreeShoot = new ItemRequirement("White tree shoot", ItemID.GARDEN_WHITE_TREE_SHOOT);
		whiteTreePot = new ItemRequirement("White tree shoot (pot)", ItemID.GARDEN_WHITE_TREE_PLANTPOT_SHOOT);
		whiteTreeWatered = new ItemRequirement("White tree shoot (watered)", ItemID.GARDEN_WHITE_TREE_PLANTPOT_SHOOT_WATERED);
		whitetreeSapling = new ItemRequirement("White tree sapling", ItemID.GARDEN_WHITE_TREE_PLANTPOT_SAPLING);
		whitetreeSapling.setTooltip("You can grow another from a cutting from the white tree on Ice Mountain");
		pinkRoseSeed = new ItemRequirement("Pink rose seed", ItemID.GARDEN_ROSEBUSH_SEED_PINK, 4);
		whiteRoseSeed = new ItemRequirement("White rose seed", ItemID.GARDEN_ROSEBUSH_SEED_WHITE, 4);
		redRoseSeed = new ItemRequirement("Red rose seed", ItemID.GARDEN_ROSEBUSH_SEED_RED, 4);
		plantCure = new ItemRequirement("Plant cure", ItemID.PLANT_CURE);
		runeShards = new ItemRequirement("Rune shards", ItemID.RUNE_SHARDS);
		runeDust = new ItemRequirement("Rune dust", ItemID.RUNE_DUST);
		magicPlantCure = new ItemRequirement("Plant cure (improved)", ItemID.PLANT_CURE_STRONG);
		marigold = new ItemRequirement("Marigold", ItemID.MARIGOLD);

		pinkOrchidSeed3 = new ItemRequirement("Orchid seeds (pink)", ItemID.GARDEN_ORCHID_PINK_SEED, 3);
		pinkOrchidSeed3.setTooltip("You can get more from Lyra");
		vineSeed4 = new ItemRequirement("Vine seeds", ItemID.GARDEN_VINE_SEED, 4);
		vineSeed4.setTooltip("You can get more from Bernald");
		yellowOrchid3 = new ItemRequirement("Orchid seeds (yellow)", ItemID.GARDEN_ORCHID_YELLOW_SEED, 3);
		yellowOrchid3.setTooltip("You can get more from Lyra");
		snowdropSeed4 = new ItemRequirement("Snowdrop seeds", ItemID.GARDEN_SNOWDROP_SEED, 4);
		snowdropSeed4.setTooltip("You can get more from Kragen");
		delphiniumSeed4 = new ItemRequirement("Delphinium seeds", ItemID.GARDEN_DELPHINIUM_SEED, 4);
		delphiniumSeed4.setTooltip("You can get more from Elstan");
		trolley = new ItemRequirement("Trolley", ItemID.GARDEN_TROLLEY_OBJ);

		noRing = new NoItemRequirement("No ring equipped", ItemSlots.RING);
	}

	public void setupConditions()
	{
		talkedToElstan = new VarbitRequirement(967, 1);
		plantedMarigold = new VarbitRequirement(VarbitID.GARDEN_ELSTAN_VARBIT, 2, Operation.GREATER_EQUAL);
		harvestedMarigold = new VarbitRequirement(967, 3);
		givenMarigold = new VarbitRequirement(967, 4);

		talkedToLyra = new VarbitRequirement(968, 1);
		plantedOnions = new Conditions(LogicType.OR,
			new VarbitRequirement(969, 1), // West patch
			new VarbitRequirement(970, 1)); // East patch
		// Planted onion, 4771 3->13
		onionsGrown = new VarbitRequirement(VarbitID.GARDEN_LYRA_VARBIT, 2, Operation.GREATER_EQUAL);
		talkedToLyraAgain = new VarbitRequirement(968, 3);
		talkedToKragen = new VarbitRequirement(971, 1);
		plantedCabbages = new Conditions(LogicType.OR,
			new VarbitRequirement(974, 1), // North patch
			new VarbitRequirement(975, 1)); // South patch
		cabbagesGrown = new VarbitRequirement(VarbitID.GARDEN_KRAGEN_VARBIT, 2, Operation.GREATER_EQUAL);
		talkedToKragenAgain = new VarbitRequirement(971, 3);

		talkedToDantaera = new VarbitRequirement(976, 1);
		cutShoot = new VarbitRequirement(976, 2);
		hasPlantedShoot = new Conditions(whiteTreePot);
		hasWateredShoot = new Conditions(LogicType.OR,
			new VarbitRequirement(VarbitID.GARDEN_WHITE_TREE_VARBIT, 4, Operation.GREATER_EQUAL),
			whiteTreeWatered,
			whitetreeSapling);

		talkedToAlthric = new VarbitRequirement(VarbitID.GARDEN_ALTHRIC_VARBIT, 1, Operation.GREATER_EQUAL);
		ringInWell = new VarbitRequirement(966, 1);
		canPickRoses = new Conditions(LogicType.OR,
			new VarbitRequirement(VarbitID.GARDEN_ALTHRIC_VARBIT, 2, Operation.GREATER_EQUAL),
			ringInWell
		);
		ringNotInWell = new VarbitRequirement(966, 0);

		hasRedRoseSeed = new Conditions(LogicType.OR,
			redRoseSeed,
			new VarbitRequirement(VarbitID.GARDEN_ROSEBUSH_RED_VARBIT, 4, Operation.GREATER_EQUAL)
		);
		hasWhiteRoseSeed = new Conditions(LogicType.OR,
			whiteRoseSeed,
			new VarbitRequirement(VarbitID.GARDEN_ROSEBUSH_WHITE_VARBIT, 4, Operation.GREATER_EQUAL)
		);
		hasPinkRoseSeed = new Conditions(LogicType.OR,
			pinkRoseSeed,
			new VarbitRequirement(VarbitID.GARDEN_ROSEBUSH_PINK_VARBIT, 4, Operation.GREATER_EQUAL)
		);
		hasRoseSeeds = new Conditions(hasRedRoseSeed, hasWhiteRoseSeed, hasPinkRoseSeed);

		talkedToBernald = new VarbitRequirement(988, 1);
		usedCureOnVines = new VarbitRequirement(988, 2);
		talkedToAlain = new VarbitRequirement(988, 3);
		curedVine = new VarbitRequirement(988, 4);
		hasShards = runeShards;
		hasDust = runeDust;
		hasEnhancedCure = magicPlantCure;
		gotVineSeeds = new VarbitRequirement(988, 5);

		notAddedCompost1 = new VarbitRequirement(984, 0);
		notAddedCompost2 = new VarbitRequirement(986, 0);
		notPlantedDelphinium =  new VarbitRequirement(VarbitID.GARDEN_DELPHINIUMS_VARBIT, 3, Operation.LESS_EQUAL);
		notPlantedYellowOrchid = new VarbitRequirement(VarbitID.GARDEN_ORCHIDS_YELLOW_VARBIT, 1, Operation.LESS_EQUAL);
		notPlantedPinkOrchid = new VarbitRequirement(VarbitID.GARDEN_ORCHIDS_PINK_VARBIT, 1, Operation.LESS_EQUAL);
		notPlantedSnowdrop = new VarbitRequirement(VarbitID.GARDEN_SNOWDROPS_VARBIT, 3, Operation.LESS_EQUAL);
		notPlantedWhiteTree = new VarbitRequirement(VarbitID.GARDEN_WHITE_TREE_VARBIT, 3, Operation.LESS_EQUAL);
		notPlantedRedRose = new VarbitRequirement(VarbitID.GARDEN_ROSEBUSH_RED_VARBIT, 3, Operation.LESS_EQUAL);
		notPlantedPinkRose = new VarbitRequirement(VarbitID.GARDEN_ROSEBUSH_PINK_VARBIT, 3, Operation.LESS_EQUAL);
		notPlantedWhiteRose = new VarbitRequirement(VarbitID.GARDEN_ROSEBUSH_WHITE_VARBIT, 3, Operation.LESS_EQUAL);
		notPlantedVine = new VarbitRequirement(VarbitID.GARDEN_VINES_VARBIT, 3, Operation.LESS_EQUAL);
		plantedEverything = new Conditions(LogicType.NOR, notPlantedDelphinium, notPlantedYellowOrchid,
			notPlantedPinkRose, notPlantedSnowdrop, notPlantedWhiteTree, notPlantedRedRose, notPlantedPinkOrchid,
			notPlantedWhiteRose, notPlantedVine);

		hasTrolley = new Conditions(LogicType.OR,
			trolley,
			new NpcCondition(NpcID.GARDEN_TROLLEY_SARADOMIN));

		lumbridgeStatueOnTrolley = new VarbitRequirement(965, 2);
		faladorStatueOnTrolley = new VarbitRequirement(965, 1);

		placedLumbridgeStatue = new VarbitRequirement(964, 2);
		placedFaladorStatue = new VarbitRequirement(963, 2);
	}

	public void setupSteps()
	{
		talkToEllamaria = new NpcStep(this, NpcID.QUEEN_ELLAMARIA, new WorldPoint(3230, 3478, 0),
			"Talk to Ellamaria east of Varrock Castle.");
		talkToEllamaria.addDialogStep("I would be happy to help someone who is so in touch with the people.");
		talkToEllamariaForTrolley = new NpcStep(this, NpcID.QUEEN_ELLAMARIA, new WorldPoint(3230, 3478, 0),
			"Talk to Ellamaria for a trolley.");
		talkToEllamariaForTrolley.addDialogSteps("How am I supposed to move statues all the way here?");
		talkToWom = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3089, 3254, 0),
			"Talk to the Wise Old Man in Draynor Village.", ringOfCharos);
		talkToWom.addDialogSteps("Queen Ellamaria has sent me to seek your guidance.", "I have returned to you with " +
			"the Ring of Charos.", "Can I retake the diplomacy test?");
		talkToWom.addWidgetChoice("Show them a range of colours so that they can come to a compromise.", 133, 566, 133);
		talkToWom.addWidgetChoice("Take his generous gift even though you have no need for it.", 162, 566, 133);
		talkToWom.addWidgetChoice("It's absolutely, unquestionably the most interesting thing I've ever done!", 162, 566, 133);
		talkToWom.addWidgetChoice("Put on the silly helmet and jump into the cannon.", 162, 566, 133);
		talkToWom.addWidgetChoice("Put on the silly helmet and jump into the cannon.", 162, 566, 133);
		talkToWom.addWidgetChoice("You of course Pkmaster0036, no one could ever challenge your greatness!", 162, 566
			, 133);
		talkToWom.addWidgetChoice("I'll do whatever you ask - I just love the monarchy!", 162, 566, 133);
		talkToWom.addWidgetChoice("No, especially not that wise old man, who doesn't look at all suspicious.", 162,
			566, 133);

		talkToElstan = new NpcStep(this, NpcID.ELSTAN, new WorldPoint(3056, 3311, 0),
			"Talk to Elstan north west of Draynor Village.", ringOfCharosA.equipped());
		talkToElstan.addDialogSteps("Do you have any delphinium seeds to spare?", "[Charm] That is why I have come to" +
			" an expert for advice.", "[Charm] Not just AN expert, Elstan - they say you are THE expert.", "[Charm] " +
			"Oh no, I love listening to gardening stories...", "[Charm] Millions? Ah, just what I wanted to hear...", "Okay, I'll grow you some marigolds.");
		plantMarigolds = new ObjectStep(this, ObjectID.FARMING_FLOWER_PATCH_1, new WorldPoint(3055, 3308,
			0), "Plant a marigold near to Elstan.", marigoldSeed.highlighted(), rake, dibber, spade);
		plantMarigolds.addIcon(ItemID.MARIGOLD_SEED);
		collectMarigold = new ObjectStep(this, ObjectID.FARMING_FLOWER_PATCH_1, new WorldPoint(3055, 3308, 0),
			"Harvest marigolds from the Falador Allotment patches. If yours died, you'll need to grow some more there.",
			spade);
		giveElstanMarigold = new NpcStep(this, NpcID.ELSTAN, new WorldPoint(3056, 3311, 0),
			"Give Elstan a marigold for his seeds.", marigold);
		giveElstanMarigold.addDialogSteps("I have those marigolds for you.");

		talkToLyra = new NpcStep(this, NpcID.LYRA, new WorldPoint(3608, 3528, 0), "Talk to Lyra west of Port " +
			"Phasmatys.", ringOfCharosA.equipped());
		talkToLyra.addDialogSteps("Do you have any orchid seeds to spare?", "[Charm] If you tell me your problems, I " +
			"may be able to help you.", "[Charm] Yes, I was wondering about that.", "[Charm] Times must be very hard " +
			"for you.", "[Charm] Whatever you've done, I'm sure you had just cause.", "[Charm] And what is the nature" +
			" of this fee that you pay?", "[Charm] If not yours, then whose blood are you offering?", "[Charm] How " +
			"can you deal with so much guilt?", "That's a deal - I'll grow a patch of onions for you.");
		plantOnions = new ObjectStep(this, ObjectID.FARMING_VEG_PATCH_7, new WorldPoint(3601, 3529, 0),
			"Plant onions in a patch near Lyra. Plant in both for the best chance of one not dying.", onionSeed3.highlighted(),
			rake, dibber, spade);
		plantOnions.addIcon(ItemID.ONION_SEED);
		waitForLyraToGrow = new ObjectStep(this, ObjectID.FARMING_VEG_PATCH_7, new WorldPoint(3601, 3529, 0),
			"Wait for your onions to finish growing. If they died you'll need to plant more in Lyra's patches.", spade);
		talkToLyraAgain = new NpcStep(this, NpcID.LYRA, new WorldPoint(3608, 3528, 0), "Talk to Lyra west of Port " +
			"Phasmatys again for her seeds.");
		talkToLyraAgain.addDialogSteps("Okay, I've grown those onions like you asked.");
		talkToLyraAgain.addSubSteps(waitForLyraToGrow);

		talkToKragen = new NpcStep(this, NpcID.KRAGEN, new WorldPoint(2668, 3376, 0),
			"Talk to Kragen northeast of Ardougne.", ringOfCharosA.equipped());
		talkToKragen.addDialogSteps("Do you have any snowdrop seeds to spare?", "[Charm] You seem to be a little " +
			"irritable, my friend.", "[Charm] I don't like to see a fellow human being so upset.", "[Charm] So what " +
			"ails you, my friend?", "[Charm] Well, is there anything I can do for you?", "[Charm] So what can I do " +
			"for you?", "That's a deal - I'll let you know when your cabbages are ready.");
		plantCabbage = new ObjectStep(this, ObjectID.FARMING_VEG_PATCH_5, new WorldPoint(2669, 3378, 0),
			"Plant cabbage seeds in the patches near Kragen. Plant in both to be safe.", cabbageSeed3.highlighted(),
			rake, dibber);
		plantCabbage.addIcon(ItemID.CABBAGE_SEED);
		waitForKragenToGrow = new ObjectStep(this, ObjectID.FARMING_VEG_PATCH_5, new WorldPoint(2669, 3378, 0),
			"Wait for your cabbages to finish growing. If they died you'll need to plant more in Kragen's patches.", spade);
		talkToKragenAgain = new NpcStep(this, NpcID.KRAGEN, new WorldPoint(2668, 3376, 0),
			"Talk to Kragen northeast of Ardougne for his seeds.");
		talkToKragenAgain.addDialogSteps("Okay, I've grown those cabbages like you asked.");
		talkToKragenAgain.addSubSteps(waitForKragenToGrow);

		talkToDantaera = new NpcStep(this, NpcID.DANTAERA, new WorldPoint(2812, 3464, 0),
			"Talk to Dantaera north of Catherby.", ringOfCharosA.equipped());
		talkToDantaera.addDialogSteps("Do you know how I could grow a White Tree?", "[Charm] I think that there is " +
			"something that you are not telling me.", "[Charm] A secret is a dreadful burden to have to keep to " +
			"yourself.", "[Charm] Unless you allow me to do this she will die anyway.");
		useSecateursOnWhiteTree = new ObjectStep(this, ObjectID.GARDEN_WHITE_TREE_DEAD, new WorldPoint(3008, 3498, 0), "Use " +
			"secateurs on the white tree on top of Ice Mountain.", secateurs.highlighted(), plantPot, trowel, wateringCan);
		useSecateursOnWhiteTree.addIcon(ItemID.SECATEURS);
		useShootOnPot = new DetailedQuestStep(this, "Use the shoot on a plant pot.", whiteTreeShoot.highlighted(),
			plantPot.highlighted());
		useCanOnPot =  new DetailedQuestStep(this, "Water the white tree shoot.", whiteTreePot.highlighted(),
			wateringCan.highlighted());

		talkToAlthric = new NpcStep(this, NpcID.BROTHER_ALTHRIC, new WorldPoint(3052, 3502, 0),
			"Talk to Brother Altheric in the Edgeville Monastery.", ringOfCharosA.equipped());
		talkToAlthric.addDialogSteps("[Charm] These are the most beautiful rosebushes I've ever seen.");
		useCharosOnWell = new ObjectStep(this, ObjectID.WELL, new WorldPoint(3085, 3503, 0),
			"Use the Ring of charos(a) on the well in Edgeville.", ringOfCharosA.highlighted());
		useCharosOnWell.addIcon(ItemID.RING_OF_CHAROS_UNLOCKED);
		pickRedRoses = new ObjectStep(this, ObjectID.GARDEN_ROSES_RED, new WorldPoint(3048, 3505, 0), "Take some seeds from" +
			" the red roses.");
		pickPinkRoses = new ObjectStep(this, ObjectID.GARDEN_ROSES_PINK, new WorldPoint(3051, 3506, 0), "Take some seeds " +
			"from the pink roses.");
		pickWhiteRoses = new ObjectStep(this, ObjectID.GARDEN_ROSES_WHITE, new WorldPoint(3055, 3505, 0), "Take some seeds " +
			"from the white roses.");

		fishForRing = new ObjectStep(this, ObjectID.WELL, new WorldPoint(3085, 3503, 0),
			"Use a fishing rod on the well in Edgeville to retrieve the ring of charos. This can take a few attempts.",
			fishingRod.highlighted());
		fishForRing.addIcon(ItemID.FISHING_ROD);

		talkToBernald = new NpcStep(this, NpcID.BERNALD, new WorldPoint(2915, 3534, 0),
			"Talk to Bernald in Burthorpe.", ringOfCharosA.equipped());
		talkToBernald.addDialogSteps("[Charm] But it is the only way that these vines will be cured.", "I accept the deal.");
		useCureOnVine = new ObjectStep(this, ObjectID.GARDEN_BURTHORPE_VINES, new WorldPoint(2915, 3534, 0), "Use plant cure " +
			"on Bernald's grapevines.", plantCure.highlighted());
		useCureOnVine.addIcon(ItemID.PLANT_CURE);
		talkToAlain = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2933, 3441, 0),
			"Talk to Alain in Taverley with your ring of charos UNEQUIPPED.", noRing);
		talkToAlain.addDialogSteps("I need to ask you about strong plant cures.");
		useHammerOnEssence = new DetailedQuestStep(this, "Use a hammer on some essence.", hammer.highlighted(),
			essence.highlighted());
		usePestleOnShards = new DetailedQuestStep(this, "Use a pestle and mortar on the essence shards.",
			pestle.highlighted(), runeShards.highlighted());
		useEssenceOnCure = new DetailedQuestStep(this, "Use the rune dust on some plant cure.",
			runeDust.highlighted(), plantCure.highlighted());
		useMagicalCureOnVine = new ObjectStep(this, ObjectID.GARDEN_BURTHORPE_VINES, new WorldPoint(2915, 3534, 0), "Use " +
			"the enhanced plant cure on Bernald's grapevines.", magicPlantCure.highlighted());
		useMagicalCureOnVine.addIcon(ItemID.PLANT_CURE_STRONG);
		talkToBernaldForSeeds = new NpcStep(this, NpcID.BERNALD, new WorldPoint(2915, 3534, 0),
			"Talk to Bernald for his seeds.", ringOfCharosA.equipped());

		fillPotWithCompost = new ObjectStep(this, ObjectID.GARDEN_ORCHID_PINK_PATCH, new WorldPoint(3229, 3486, 0), "Fill the " +
			"plantpots in Ellamaria's garden with compost.", compost.highlighted());
		fillPotWithCompost.addIcon(ItemID.BUCKET_COMPOST);
		fillPotWithCompost2 = new ObjectStep(this, ObjectID.GARDEN_ORCHID_YELLOW_PATCH, new WorldPoint(3231, 3486, 0), "Fill the " +
		"plantpots in Ellamaria's garden with compost.", compost.highlighted());
		fillPotWithCompost2.addIcon(ItemID.BUCKET_COMPOST);
		plantDelphinium = new ObjectStep(this, ObjectID.GARDEN_DELPHINIUM_PATCH, new WorldPoint(3226, 3477, 0), "Plant the " +
			"delphinum seeds.", delphiniumSeed4.highlighted(), rake, dibber);
		plantDelphinium.addIcon(ItemID.GARDEN_DELPHINIUM_SEED);
		plantPinkOrchid = new ObjectStep(this, ObjectID.GARDEN_ORCHID_PINK_PATCH, new WorldPoint(3229, 3486, 0), "Plant the " +
			"pink orchids in the marked plantpot.", pinkOrchidSeed3.highlighted(), dibber);
		plantPinkOrchid.addIcon(ItemID.GARDEN_ORCHID_PINK_SEED);
		plantYellowOrchid = new ObjectStep(this,ObjectID.GARDEN_ORCHID_YELLOW_PATCH, new WorldPoint(3231, 3486, 0), "Plant the " +
			"yellow orchids in the marked plantpot.", yellowOrchid3.highlighted(), dibber);
		plantYellowOrchid.addIcon(ItemID.GARDEN_ORCHID_YELLOW_SEED);
		plantSnowdrop = new ObjectStep(this, ObjectID.GARDEN_SNOWDROP_PATCH, new WorldPoint(3232, 3483, 0), "Plant snowdrops " +
			"in the east patch.", snowdropSeed4.highlighted(), rake, dibber);
		plantSnowdrop.addIcon(ItemID.GARDEN_SNOWDROP_SEED);
		plantWhiteTree = new ObjectStep(this, ObjectID.GARDEN_WHITE_TREE_PATCH, new WorldPoint(3230, 3475, 0), "Plant the white" +
			" tree.", whitetreeSapling.highlighted(), rake, spade);
		plantWhiteTree.addIcon(ItemID.GARDEN_WHITE_TREE_PLANTPOT_SAPLING);
		plantRedRose = new ObjectStep(this, ObjectID.GARDEN_ROSEBUSH_PATCH_RED, new WorldPoint(3229, 3472, 0), "Plant the rose " +
			"seeds.", redRoseSeed.highlighted(), rake, dibber);
		plantRedRose.addIcon(ItemID.GARDEN_ROSEBUSH_SEED_RED);
		plantPinkRose = new ObjectStep(this, ObjectID.GARDEN_ROSEBUSH_PATCH_PINK, new WorldPoint(3227, 3472, 0), "Plant the rose " +
			"seeds.", pinkRoseSeed.highlighted(), rake, dibber);
		plantPinkRose.addIcon(ItemID.GARDEN_ROSEBUSH_SEED_PINK);
		plantWhiteRose = new ObjectStep(this, ObjectID.GARDEN_ROSEBUSH_PATCH_WHITE, new WorldPoint(3232, 3472, 0), "Plant the rose " +
			"seeds.", whiteRoseSeed.highlighted(), rake, dibber);
		plantWhiteRose.addIcon(ItemID.GARDEN_ROSEBUSH_SEED_WHITE);
		plantRedRose.addSubSteps(plantPinkRose, plantWhiteRose);
		plantVine = new ObjectStep(this, ObjectID.GARDEN_VINE_PATCH, new WorldPoint(3227, 3483, 0),
			"Plant the vine seeds.", vineSeed4.highlighted(), rake, dibber);
		plantVine.addIcon(ItemID.GARDEN_VINE_SEED);

		useTrolleyOnLumbridgeStatue = new ObjectStep(this, ObjectID.GARDEN_LUMBRIDGE_STATUE, new WorldPoint(3231, 3217, 0),
			"Use a trolley on the king statue outside Lumbridge Castle.", trolley.highlighted());
		useTrolleyOnLumbridgeStatue.addIcon(ItemID.GARDEN_TROLLEY_OBJ);
		pushLumbridgeStatue = new ObjectStep(this, ObjectID.GARDEN_KING_STATUE_MULTI, new WorldPoint(3233, 3487, 0),
			"Push the trolley across the River Lum, then to the marked plinth in the garden. Right-click 'place' the" +
				" statue when it's next to the plinth.");
		((DetailedQuestStep) pushLumbridgeStatue).setLinePoints(new ArrayList<>(Arrays.asList(
			new WorldPoint(3232, 3218, 0),
			new WorldPoint(3235, 3218, 0),
			new WorldPoint(3235, 3226, 0),
			new WorldPoint(3252, 3226, 0),
			// Varrock
			new WorldPoint(3215, 3500, 0),
			new WorldPoint(3226, 3500, 0),
			new WorldPoint(3226, 3495, 0),
			new WorldPoint(3230, 3495, 0),
			new WorldPoint(3230, 3487, 0),
			new WorldPoint(3232, 3487, 0)
		)));

		useTrolleyOnFaladorStatue = new ObjectStep(this, ObjectID.FALADOR_STATUE_SARADOMIN, new WorldPoint(2965, 3381, 0),
			"Use the trolley on the statue of Saradomin in Falador.", trolley.highlighted());
		pushFaladorStatue = new ObjectStep(this, ObjectID.GARDEN_SARADOMIN_STATUE_MULTI, new WorldPoint(3230, 3479, 0),
			"Push the trolley out of Falador, then to the marked plinth in the garden. Right-click 'place' the" +
				" statue when it's next to the plinth.");
		((DetailedQuestStep) pushFaladorStatue).setLinePoints(new ArrayList<>(Arrays.asList(
			new WorldPoint(2965, 3383, 0),
			new WorldPoint(2965, 3402, 0),
			// Varrock
			new WorldPoint(3215, 3500, 0),
			new WorldPoint(3226, 3500, 0),
			new WorldPoint(3226, 3495, 0),
			new WorldPoint(3230, 3495, 0),
			new WorldPoint(3230, 3484, 0),
			new WorldPoint(3231, 3484, 0),
			new WorldPoint(3231, 3479, 0),
			new WorldPoint(3230, 3479, 0)
		)));

		talkToEllmariaAfterGrown = new NpcStep(this, NpcID.QUEEN_ELLAMARIA, new WorldPoint(3230, 3478, 0),
		"Talk to Ellamaria once everything's finished growing.");
		talkToRoald = new NpcStep(this, NpcID.KING_ROALD, new WorldPoint(3221, 3473, 0),
			"Talk to King Roald in Varrock Castle, watch the cutscene, then finish the dialog with Ellamaria to finish" +
				" the quest!",	ringOfCharosA.equipped());
		talkToRoald.addDialogSteps("Ask King Roald to follow me.", "[Charm] Of course, your majesty - please forgive " +
			"me.", "[Charm] The Queen asked me to bring you.", "Would you like to follow me for a minute?");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ringOfCharos, rake, dibber, spade, secateurs, wateringCan, trowel, plantCure2, marigoldSeed,
			cabbageSeed3, onionSeed3, fishingRod, hammer, essence, pestle, plantPot, compost2);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(compost5, varrockTeleport, draynorTeleport, phasTeleport, ardougneTeleport,
			catherbyTeleport, burthorpeTeleport, edgevilleTeleport, lumbridgeTeleport, faladorTeleport, taverleyTeleport);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.CREATURE_OF_FENKENSTRAIN, QuestState.FINISHED));
		reqs.add(new SkillRequirement(Skill.FARMING, 25));
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.FARMING, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Compost Potion (4)", ItemID.SUPERCOMPOST_POTION_4, 1));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToEllamaria)));
		allSteps.add(new PanelDetails("Learning Persuasion", Collections.singletonList(talkToWom), ringOfCharos));
		PanelDetails elstanPanel = new PanelDetails("Delpheniums", Arrays.asList(talkToElstan, plantMarigolds), ringOfCharosA,
			marigoldSeed, spade, rake, dibber);
		elstanPanel.setLockingStep(helpingElstan);
		allSteps.add(elstanPanel);

		PanelDetails lyraPanel = new PanelDetails("Orchids", Arrays.asList(talkToLyra, plantOnions), ringOfCharosA, onionSeed3,
			spade, rake, dibber);
		lyraPanel.setLockingStep(helpingLyra);
		allSteps.add(lyraPanel);

		PanelDetails kragenPanel = new PanelDetails("Snowdrops", Arrays.asList(talkToKragen, plantCabbage), ringOfCharosA,
			cabbageSeed3, spade, rake, dibber);
		kragenPanel.setLockingStep(helpingKragen);
		allSteps.add(kragenPanel);

		PanelDetails dantaeraPanel = new PanelDetails("White Tree", Arrays.asList(talkToDantaera, useSecateursOnWhiteTree,
			useShootOnPot, useCanOnPot), ringOfCharosA,	secateurs, trowel, plantPot, wateringCan);
		dantaeraPanel.setLockingStep(helpingDantaera);
		allSteps.add(dantaeraPanel);

		PanelDetails althricPanel = new PanelDetails("Roses", Arrays.asList(talkToAlthric, useCharosOnWell,
			pickWhiteRoses, pickPinkRoses, pickRedRoses), ringOfCharosA, fishingRod);
		althricPanel.setLockingStep(helpingAlthric);
		allSteps.add(althricPanel);

		PanelDetails getRingPanel = new PanelDetails("Getting your ring back", Collections.singletonList(fishForRing),
			fishingRod);
		getRingPanel.setLockingStep(gettingRing);
		allSteps.add(getRingPanel);

		PanelDetails bernaldPanel = new PanelDetails("Vines", Arrays.asList(talkToBernald, useCureOnVine,
			talkToAlain, useHammerOnEssence, usePestleOnShards, useEssenceOnCure,
			useMagicalCureOnVine, talkToBernaldForSeeds), ringOfCharosA, plantCure2, essence, hammer, pestle);
		bernaldPanel.setLockingStep(helpingBernald);
		allSteps.add(bernaldPanel);

		PanelDetails collectCrops = new PanelDetails("Collecting the last seeds", Arrays.asList(collectMarigold,
			giveElstanMarigold, talkToLyraAgain, talkToKragenAgain), spade);
		allSteps.add(collectCrops);

		PanelDetails plantingPanel = new PanelDetails("Planting the Garden", Arrays.asList(fillPotWithCompost, fillPotWithCompost2,
			plantPinkOrchid, plantYellowOrchid, plantVine, plantSnowdrop, plantWhiteTree, plantDelphinium,
			plantRedRose), compost2, rake, dibber, spade, delphiniumSeed4, pinkOrchidSeed3, yellowOrchid3,
			snowdropSeed4, whitetreeSapling, pinkRoseSeed, whiteRoseSeed, redRoseSeed, vineSeed4);
		plantingPanel.setLockingStep(plantGarden);
		allSteps.add(plantingPanel);

		allSteps.add(new PanelDetails("Getting statues", Arrays.asList(talkToEllamariaForTrolley,
			useTrolleyOnLumbridgeStatue, pushLumbridgeStatue, useTrolleyOnFaladorStatue, pushFaladorStatue)));

		allSteps.add(new PanelDetails("Showing off the Garden", Arrays.asList(talkToEllmariaAfterGrown, talkToRoald),
			ringOfCharosA));

		return allSteps;
	}
}
