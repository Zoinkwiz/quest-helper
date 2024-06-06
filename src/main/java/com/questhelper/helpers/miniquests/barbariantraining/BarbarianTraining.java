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
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.MesBoxRequirement;
import com.questhelper.requirements.MultiChatMessageRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

public class BarbarianTraining extends BasicQuestHelper
{
	// Items Required
	ItemRequirement barbFishingRod, tinderbox, bow, knife, fish, combatGear, antifireShield, chewedBones, bronzeBar, logs, hammer,
		roe, attackPotion, sapling, seed, spade, oakLogs, axe, feathers, barbarianAttackPotion;

	// Items recommended
	ItemRequirement gamesNecklace, catherbyTeleport;

	Requirement fishing48, agility15, strength15, fishing55, strength35, firemaking35, crafting11, farming15, smithing5;

	QuestRequirement druidicRitual, taiBwoWannaiTrio;

	// TODO: Finish off requirements
	Requirement taskedWithFishing, taskedWithHarpooning, taskedWithFarming, taskedWithBowFiremaking, taskedWithPyre, taskedWithPotSmashing,
		taskedWithSpears, taskedWithHastae, taskedWithHerblore;

	Requirement chewedBonesNearby;

	Requirement plantedSeed, smashedPot, litFireWithBow, sacrificedRemains, caughtBarbarianFish,
		caughtFishWithoutHarpoon, madePotion, madeSpear, madeHasta;

	DetailedQuestStep talkToOttoAboutFishing, searchBed, catchFish, talkToOttoAfterFish;

	DetailedQuestStep talkToOttoAboutBarehanded, fishHarpoon, talkToOttoAfterHarpoon;

	DetailedQuestStep talkToOttoAboutBow, lightLogWithBow, talkToOttoAfterBow;

	DetailedQuestStep talkToOttoAboutPyre, enterWhirlpool, goDownToBrutalGreen, goUpToMithrilDragons, killMithrilDragons,
		pickupChewedBones, useLogOnPyre, useBonesOnPyre, lightPyre, talkToOttoAfterPyre;

	DetailedQuestStep talkToOttoAboutFarming, plantSeed, talkToOttoAfterPlantingSeed;

	DetailedQuestStep talkToOttoAboutPots, plantSapling, talkToOttoAfterSmashingPot;

	DetailedQuestStep talkToOttoAboutSpears, makeBronzeSpear, talkToOttoAfterBronzeSpear, talkToOttoAboutHastae, makeBronzeHasta, talkToOttoAfterMakingHasta;

	DetailedQuestStep talkToOttoAboutHerblore, getBarbRodForHerblore, fishForHerblore, dissectFish, useRoeOnAttackPotion, talkToOttoAfterPotion;

	ConditionalStep fishingSteps, harpoonSteps, seedSteps, potSmashingSteps, firemakingSteps, pyreSteps, spearSteps,
		spearAndHastaeSteps, herbloreSteps;

	Requirement finishedFishing, finishedHarpoon, finishedSeedPlanting, finishedPotSmashing, finishedFiremaking, finishedPyre, finishedSpear, finishedHasta, finishedHerblore;

	Zone ancientCavernF0, ancientCavernF1, ancientCavernArrivalRoom;

	ZoneRequirement inAncientCavernF0, inAncientCavernF1, inAncientCavernArrivalRoom;

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
		fishingSteps.addStep(and(taskedWithFishing, barbFishingRod.alsoCheckBank(questBank)), catchFish);
		fishingSteps.addStep(taskedWithFishing, searchBed);
		fishingSteps.setLockingCondition(finishedFishing);

		// Herblore
		herbloreSteps = new ConditionalStep(this, talkToOttoAboutHerblore);
		herbloreSteps.addStep(madePotion, talkToOttoAfterPotion);
		herbloreSteps.addStep(and(taskedWithHerblore, roe), useRoeOnAttackPotion);
		herbloreSteps.addStep(and(taskedWithHerblore, fish), dissectFish);
		herbloreSteps.addStep(and(taskedWithHerblore, barbFishingRod.alsoCheckBank(questBank)), fishForHerblore);
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
//		pyreSteps.addStep(and(taskedWithPyre, chewedBones.alsoCheckBank(questBank), pyreReady), lightPyre);
//		pyreSteps.addStep(and(taskedWithPyre, chewedBones.alsoCheckBank(questBank), logPlaced), useBonesOnPyre);
		pyreSteps.addStep(and(taskedWithPyre, chewedBones.alsoCheckBank(questBank)), useLogOnPyre);
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
		spearAndHastaeSteps.addStep(finishedSpear, talkToOttoAboutHastae);
		spearAndHastaeSteps.setLockingCondition(finishedHasta);

		ConditionalStep allSteps = new ConditionalStep(this, fishingSteps);
		allSteps.addStep(nor(finishedFishing), fishingSteps);
		allSteps.addStep(nor(finishedHerblore), herbloreSteps);
		allSteps.addStep(nor(finishedHarpoon), harpoonSteps);
		allSteps.addStep(nor(finishedSeedPlanting), seedSteps);
		allSteps.addStep(nor(finishedPotSmashing), potSmashingSteps);
		allSteps.addStep(nor(finishedFiremaking), firemakingSteps);
		allSteps.addStep(nor(finishedSpear, finishedHasta), spearAndHastaeSteps);
		allSteps.addStep(nor(finishedPyre), pyreSteps);
		allSteps.addDialogSteps("Let's talk about my training.", "I seek more knowledge.");
		allSteps.setCheckAllChildStepsOnListenerCall(true);


		// Started, 9613
		steps.put(0, allSteps);
		steps.put(1, allSteps);
		// Increments after doing a task
		steps.put(2, allSteps);
		// 9610, 0->1 at some point??? Probably for pot smashing, or for whirlpool?
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
		barbFishingRod = new ItemRequirement("Barbarian rod", ItemID.BARBARIAN_ROD);
		barbFishingRod.addAlternates(ItemID.PEARL_BARBARIAN_ROD);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		bow = new ItemRequirement("Any bow", ItemCollections.BOWS);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		fish = new ItemRequirement("Leaping trout/salmon/sturgeon", ItemID.LEAPING_STURGEON);
		fish.addAlternates(ItemID.LEAPING_SALMON, ItemID.LEAPING_TROUT);
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		antifireShield = new ItemRequirement("Anti-dragon shield or DFS", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();
		chewedBones = new ItemRequirement("Chewed bones", ItemID.CHEWED_BONES);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		roe = new ItemRequirement("Roe", ItemID.ROE);
		attackPotion = new ItemRequirement("Attack potion (2)", ItemID.ATTACK_POTION2);
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
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES, 5);
		gamesNecklace.setChargedItem(true);
		catherbyTeleport = new ItemRequirement("Catherby teleport for fishing", ItemID.CATHERBY_TELEPORT);
		catherbyTeleport.addAlternates(ItemID.CAMELOT_TELEPORT);

		// Quest items
		barbarianAttackPotion = new ItemRequirement("Attack mix (2)", ItemID.ATTACK_MIX2);

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
	}

	public void setupConditions()
	{
		// Started tasks

		taskedWithFishing = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedfishing",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Certainly. Take the rod from under my bed and fish in the lake. When you have caught a few fish, I am sure you will be ready to talk more with me."),
				new DialogRequirement("Alas, I do not sense that you have been successful in your fishing yet. The look in your eyes is not that of the osprey."),
				new WidgetTextRequirement(119, 3, true, "fish with a new")
			)
		);
		taskedWithHarpooning = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedharpoon",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("... and I thought fishing was a safe way to pass the time."),
				new DialogRequirement("I see you need encouragement in learning the ways of fishing without a harpoon."),
				new WidgetTextRequirement(119, 3, true, "fish with my")
			)
		);
		taskedWithFarming = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedseedplanting",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Remember to be calm, and good luck."),
				new DialogRequirement("I see you have yet to be successful in planting a seed with your fists."),
				new WidgetTextRequirement(119, 3, true, "plant a seed with")
			)
		);
		taskedWithPotSmashing = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedpotsmashing",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("May the spirits guide you into success."),
				new DialogRequirement("You have not yet attempted to plant a tree. Why not?"),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smash pots after")
			)
		);

		taskedWithBowFiremaking = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedfiremaking",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The spirits will aid you. The power they supply will guide your hands. Go and benefit from their guidance upon oak logs."),
				new DialogRequirement("By now you know my response."),
				new WidgetTextRequirement(119, 3, true, "light a fire with")
			)
		);

		taskedWithPyre = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedpyremaking",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Dive into the whirlpool in the lake to the east. The spirits will use their abilities to ensure you arrive in the correct location. Be warned, their influence fades, so you must find y"),
				new DialogRequirement("I will repeat myself fully, since this is quite complex. Listen well."),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to <col=800000>create pyre ships")
			)
		);

		taskedWithHerblore = new RuneliteRequirement(getConfigManager(), "barbariantrainingstartedherblore",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Have I become so predictable? But yes, I do indeed require a potion. It is of the highest importance that you bring me a lesser attack potion combined with fish roe."),
				new DialogRequirement("Do you have my potion?"),
				new WidgetTextRequirement(119, 3, true, "Otto<col=000080> has tasked me with learning how to make a <col=800000>new type")
			)
		);

		// TODO: Get details for starting smithing tasks
		// taskedWithSpears =
		// taskedWithHastae =

		// Finished tasks

		finishedFishing = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedfishing",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Patience young one. These are fish which are fat with eggs rather than fat of flesh. It is these eggs that are the thing to make use of."),
				new WidgetTextRequirement(119, 3, true, "I managed to catch a fish with the new rod!")
			),
			"Finished Barbarian Fishing"
		);
		finishedHarpoon = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedharpoon",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("I mean that when you eventually die and find peace, at least the spirits you encounter will be your friends. Alas for you adventurous sort, the natural ways of passing are close to imp"),
				new WidgetTextRequirement(119, 3, true, "I managed to fish with my hands!")
			),
			"Finished Barbarian Harpooning"
		);

		finishedSeedPlanting = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedseedplanting",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("No child, but we all have potential to improve our strength."),
				new WidgetTextRequirement(119, 3, true, "<str>I managed to plant a seed with my fists!")
			),
			"Finished Barbarian Seed Planting"
		);
		finishedPotSmashing = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedpotsmashing",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("It will become more natural with practice."),
				new WidgetTextRequirement(119, 3, true, "<str>I managed to smash a plant pot without littering!")
			),
			"Finished Barbarian Pot Smashing"
		);

		finishedFiremaking = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedfiremaking",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Fine news indeed!"),
				new WidgetTextRequirement(119, 3, true, "I managed to light a fire with a bow!")
			),
			"Finished Barbarian Firemaking"
		);
		// TODO: See what pyre widget + dialog finished is
		finishedPyre = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedpyremaking",
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(119, 3, true, "TODO")
			),
			"Finished Barbarian Pyremaking"
		);
		// TODO: Work out finished spear dialog
		finishedSpear = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedspear",
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(119, 3, true, "I managed to smith a spear!")
			),
			"Finished Barbarian Spear Smithing"
		);
		// TODO: Work out finished hasta dialog
		finishedHasta = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedhasta",
			new Conditions(true, LogicType.OR,
				new WidgetTextRequirement(119, 3, true, "I managed to create a hasta!")
			),
			"Finished Barbarian Hasta Smithing"
		);
		finishedHerblore = new RuneliteRequirement(getConfigManager(), "barbariantrainingfinishedherblore",
			new Conditions(true, LogicType.OR,
				new DialogRequirement("I will take that off your hands now. I will say no more than that I am eternally grateful."),
				new WidgetTextRequirement(119, 3, true, "I managed to create a new potion!")
			),
			"Finished Barbarian Herblore"
		);

		// Mid-conditions

		plantedSeed = new RuneliteRequirement(getConfigManager(), "barbariantrainingplantedseed",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You plant "),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true, "I've managed to <col=800000>plant a seed with my fists<col=000080>!")
			)
		);

		smashedPot = new RuneliteRequirement(getConfigManager(), "barbariantrainingsmashedpot",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You plant "),
					new ChatMessageRequirement(" sapling"),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true, "I've managed to <col=800000>smash a pot without littering<col=000080>!")
			)
		);

		litFireWithBow = new RuneliteRequirement(getConfigManager(), "barbariantrainingbowfire",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The fire catches and the logs begin to burn."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true,
					"I've managed to <col=800000>light a fire with a bow<col=000080>!")
			)
		);

		// TODO: Confirm Quest Log text
		sacrificedRemains = new RuneliteRequirement(getConfigManager(), "barbariantrainingpyremade",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The ancient barbarian is laid to rest."),
					// TODO: Confirm this is a ChatMessageRequirement, not MesBox
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true,
					"TODO NEED TO FIND OUT")
			)
		);
		// 13093 0->100 when barb fished??
		caughtBarbarianFish = new RuneliteRequirement(getConfigManager(), "barbariantrainingbarbfished",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You catch a leaping trout.", "You catch a leaping salmon.", "You catch a leaping sturgeon."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true,
					"I've managed to catch a <col=800000>fish with the new rod<col=000080>! I should let")
			)
		);
		// 13093 0->100 when harpooned fish?
		caughtFishWithoutHarpoon = new RuneliteRequirement(getConfigManager(), "barbariantrainingharpoonedfish",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You catch a tuna.", "You catch a swordfish.", "You catch a shark."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true,
					"I've managed to <col=800000>fish with my hands<col=000080>! I should let <col=800000>Otto <col=000080>know")
			)
		);
		madePotion = new RuneliteRequirement(getConfigManager(), "barbariantrainingmadepotion",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You combine your potion with the fish eggs."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true,
					"I've managed to make a <col=800000>new type of potion<col=000080>! I should let")
			)
		);
		madeSpear = new RuneliteRequirement(getConfigManager(), "barbariantrainingmadespear",
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You make a "),
					new ChatMessageRequirement(" spear."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(119, 3, true,
					"I've managed to <col=800000>smith a spear<col=000080>!")
			)
		);
		madeHasta = new RuneliteRequirement(getConfigManager(), "barbariantrainingmadehasta",
		new Conditions(true, LogicType.OR,
			new MultiChatMessageRequirement(
				new ChatMessageRequirement("You make a "),
				new ChatMessageRequirement(" hasta."),
				new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
			),
			new WidgetTextRequirement(119, 3, true,
				"I've managed to <col=800000>smith a hasta<col=000080>!")
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
		talkToOttoAboutFishing = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about fishing.");
		talkToOttoAboutFishing.addDialogSteps("You think so?", "What can I learn about the use of a fishing rod?");

		searchBed = new ObjectStep(this, ObjectID.BARBARIAN_BED, new WorldPoint(2500, 3490, 0), "Search the bed in Otto's hut.");
		catchFish = new NpcStep(this, NpcID.FISHING_SPOT_1542, new WorldPoint(2500, 3510, 0),
			"Fish from one of the fishing spots near Otto's hut.", true, barbFishingRod, feathers);

		talkToOttoAfterFish = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Return to Otto and tell him about your success.");
		talkToOttoAfterFish.addDialogStep("I've fished with a barbarian rod!");

		// Barbarian Harpooning
		talkToOttoAboutBarehanded = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about barehand fishing.");
		talkToOttoAboutBarehanded.addDialogSteps("You think so?", "Please teach me of your cunning with harpoons.");
		// TODO: Update ID and WorldPoint
		fishHarpoon = new NpcStep(this, NpcID.FISHING_SPOT_1519, new WorldPoint(2845, 3429, 0), "Fish in a fishing spot which requires a harpoon WITHOUT a harpoon.", true, fishing55);
		talkToOttoAfterHarpoon = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterHarpoon.addDialogSteps("Barbarian Outpost.", "I've fished with my hands!");
		talkToOttoAfterHarpoon.addTeleport(gamesNecklace);

		// Barbarian Firemaking
		talkToOttoAboutBow = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about firemaking.");
		talkToOttoAboutBow.addDialogSteps("You think so?", "I'm ready for your firemaking wisdom. Please instruct me.");
		lightLogWithBow = new DetailedQuestStep(this, "Use a bow on some oak logs to light it.", bow.highlighted(), oakLogs.highlighted());
		talkToOttoAfterBow = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterBow.addDialogSteps("I've set fire to logs with a bow!");

		talkToOttoAboutPyre = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about advanced firemaking.");
		talkToOttoAboutPyre.addDialogStep("I have completed firemaking with a bow. What follows this?");
		enterWhirlpool = new ObjectStep(this, ObjectID.WHIRLPOOL_25274, new WorldPoint(2513, 3509, 0),
			"Jump into the whirlpool north of Otto. Be prepared to fight mithril dragons.", combatGear, antifireShield);
		goDownToBrutalGreen = new ObjectStep(this, ObjectID.STAIRS_25338, new WorldPoint(1769, 5365, 1),
			"Climb down the stairs. There are brutal green dragons there, so have antifire protection and Protect from Magic on!");
		goUpToMithrilDragons = new ObjectStep(this, ObjectID.STAIRS_25339, new WorldPoint(1778, 5344, 0),
			"Climb up the stairs to the south-east. Be ready to fight the mithril dragons!");
		killMithrilDragons = new NpcStep(this, NpcID.MITHRIL_DRAGON, new WorldPoint(1773, 5348, 1),
			"Kill mithril dragons until you get some chewed bones.");
		pickupChewedBones = new ItemStep(this, "Pick up the chewed bones.", chewedBones);
		useLogOnPyre = new ObjectStep(this, ObjectID.BOAT_STATION, new WorldPoint(2506, 3517, 0),
			"Construct a pyre on the lake near Otto.", logs, chewedBones, tinderbox);
		useBonesOnPyre = new ObjectStep(this, ObjectID.BOAT_STATION, new WorldPoint(2506, 3517, 0),
			"Add the chewed bones to the pyre on the lake near Otto.", chewedBones, tinderbox);
		lightPyre = new ObjectStep(this, ObjectID.BOAT_STATION, new WorldPoint(2506, 3517, 0),
			"Light the pyre on the lake near Otto.", tinderbox);
		// TODO: Need dialog highlight
		talkToOttoAfterPyre = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Return to Otto and tell him about the succesful pyre-making.");

		// Barbarian Farming
		talkToOttoAboutFarming = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about farming.");
		talkToOttoAboutFarming.addDialogSteps("You think so?", "How can I use my strength in the ways of agriculture?");
		plantSeed = new DetailedQuestStep(this, "Plant a seed in a patch WITHOUT a dibber. This can fail, so bring multiple and preferably use cheap seeds.",
			seed.quantity(9));
		talkToOttoAfterPlantingSeed = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterPlantingSeed.addDialogStep("I've planted seeds with my fists!");

		// Farming P2
		talkToOttoAboutPots = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about advanced farming.");
		talkToOttoAboutPots.addDialogStep("Are there any other fist related farming activities I can learn?");
		// TODO: Add dialog
		plantSapling = new DetailedQuestStep(this, "Plant a sapling in a tree or fruit tree patch.", sapling, spade);
		talkToOttoAfterSmashingPot = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterSmashingPot.addDialogStep("I've smashed a pot whilst farming!");

		// Barbarian Smithing, REQUIRES barbarian firemaking P1
		talkToOttoAboutSpears = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about spears.");
		talkToOttoAboutSpears.addDialogSteps("Tell me more about the use of spears.");
		makeBronzeSpear = new ObjectStep(this, ObjectID.BARBARIAN_ANVIL, new WorldPoint(2502, 3485, 0),
			"Make a bronze spear on the anvil south of Otto.", bronzeBar, logs, hammer);
		talkToOttoAfterBronzeSpear = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.");

		talkToOttoAboutHastae = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about hastae.");
		// TODO: Add dialog
		makeBronzeHasta = new ObjectStep(this, ObjectID.BARBARIAN_ANVIL, new WorldPoint(2502, 3485, 0),
			"Make a bronze hasta on the anvil south of Otto.", bronzeBar, logs, hammer);
		talkToOttoAfterMakingHasta = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.");

		// Barbarian Herblore, REQUIRES Barbarian Fishing
		talkToOttoAboutHerblore = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about herblore.");
		getBarbRodForHerblore = new ObjectStep(this, ObjectID.BARBARIAN_BED, new WorldPoint(2500, 3490, 0), "Search the bed in Otto's hut.");
		fishForHerblore = new NpcStep(this, NpcID.FISHING_SPOT_1542, new WorldPoint(2500, 3510, 0), "Fish from one of the fishing spots near Otto's hut.", true, barbFishingRod);
		fishForHerblore.addSubSteps(getBarbRodForHerblore);
		dissectFish = new DetailedQuestStep(this, "Dissect the fish with a knife until you get some roe.", knife.highlighted(), fish.highlighted());
		talkToOttoAboutHerblore.addDialogSteps("What was that secret knowledge of herblore we talked of?");
		useRoeOnAttackPotion = new DetailedQuestStep(this, "Use roe on an attack potion (2).", roe.highlighted(), attackPotion.highlighted());
		talkToOttoAfterPotion = new NpcStep(this, NpcID.OTTO_GODBLESSED, new WorldPoint(2500, 3488, 0),
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
			attackPotion, roe, knife);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gamesNecklace, catherbyTeleport);
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
				pickupChewedBones, useLogOnPyre, useBonesOnPyre, lightPyre, talkToOttoAfterPyre),
			finishedFiremaking, logs, tinderbox, axe, combatGear, antifireShield);
		barbPyremaking.setLockingStep(pyreSteps);
		allSteps.add(barbPyremaking);

		return allSteps;
	}
}
