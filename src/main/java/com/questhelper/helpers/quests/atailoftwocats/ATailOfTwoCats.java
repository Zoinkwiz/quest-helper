/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.helpers.quests.atailoftwocats;

import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.NpcCollections;
import com.questhelper.collections.TeleportCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.FollowerItemRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class ATailOfTwoCats extends BasicQuestHelper
{
	//Items Required
	ItemRequirement catspeak, catspeakE, deathRune5, chocolateCake, logs, tinderbox, milk, shears,
		potatoSeed4, rake, dibber, vialOfWater, desertTop, desertBottom, hat, catspeakEWorn, cat;

	ItemRequirement burthorpeTeleport, varrockTeleport, sophanemTeleport, staminaPotion;

	Requirement bobNearby, rakedPatch, madeBed, plantedSeed, placedLogs, litLogs, placedCake, placedMilk, usedShears, grownPotatoes;

	DetailedQuestStep talkToUnferth, talkToHild, findBob, talkToBob, talkToGertrude, talkToReldo, findBobAgain, talkToBobAgain, talkToSphinx, useRake, plantSeeds, makeBed, useLogsOnFireplace, lightLogs,
		useChocolateCakeOnTable, useMilkOnTable, useShearsOnUnferth, reportToUnferth, talkToApoth, talkToUnferthAsDoctor, findBobToFinish, talkToBobToFinish, talkToUnferthToFinish, waitForPotatoesToGrow;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToUnferth);

		steps.put(5, talkToHild);
		steps.put(10, talkToHild);

		ConditionalStep findbob1 = new ConditionalStep(this, findBob);
		findbob1.addStep(bobNearby, talkToBob);

		steps.put(15, findbob1);

		steps.put(20, talkToGertrude);

		steps.put(25, talkToReldo);
		steps.put(28, talkToReldo);

		ConditionalStep findbob2 = new ConditionalStep(this, findBobAgain);
		findbob2.addStep(bobNearby, talkToBobAgain);

		steps.put(30, findbob2);

		steps.put(35, talkToSphinx);

		ConditionalStep doChores = new ConditionalStep(this, useRake);
		doChores.addStep(new Conditions(plantedSeed, madeBed, litLogs, placedMilk, usedShears), waitForPotatoesToGrow);
		doChores.addStep(new Conditions(plantedSeed, madeBed, litLogs, placedMilk), useShearsOnUnferth);
		doChores.addStep(new Conditions(plantedSeed, madeBed, litLogs, placedCake), useMilkOnTable);
		doChores.addStep(new Conditions(plantedSeed, madeBed, litLogs), useChocolateCakeOnTable);
		doChores.addStep(new Conditions(plantedSeed, madeBed, placedLogs), lightLogs);
		doChores.addStep(new Conditions(plantedSeed, madeBed), useLogsOnFireplace);
		doChores.addStep(plantedSeed, makeBed);
		doChores.addStep(rakedPatch, plantSeeds);

		steps.put(40, doChores);

		steps.put(45, reportToUnferth);

		steps.put(50, talkToApoth);

		steps.put(55, talkToUnferthAsDoctor);

		ConditionalStep findbob3 = new ConditionalStep(this, findBobToFinish);
		findbob3.addStep(bobNearby, talkToBobToFinish);
		steps.put(60, findbob3);

		steps.put(65, talkToUnferthToFinish);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		catspeak = new ItemRequirement("Catspeak amulet", ItemID.ICS_LITTLE_AMULET_OF_CATSPEAK, 1, true).isNotConsumed();
		catspeak.setTooltip("You can get another from the Sphinx in Sophanem");
		catspeakE = new ItemRequirement("Catspeak amulet (e)", ItemID.TWOCATS_AMULETOFCATSPEAK).isNotConsumed();
		catspeakEWorn = catspeakE.equipped();
		catspeakE.setHighlightInInventory(true);
		deathRune5 = new ItemRequirement("Death runes", ItemID.DEATHRUNE, 5);
		cat = new FollowerItemRequirement("A cat",
			ItemCollections.CATS,
			NpcCollections.getCats()).isNotConsumed();

		chocolateCake = new ItemRequirement("Chocolate cake", ItemID.CHOCOLATE_CAKE);
		chocolateCake.setHighlightInInventory(true);
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		logs.addAlternates(ItemCollections.LOGS_FOR_FIRE);
		logs.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tinderbox.setHighlightInInventory(true);
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK).isNotConsumed();
		milk.setHighlightInInventory(true);
		shears = new ItemRequirement("Shears", ItemID.SHEARS).isNotConsumed();
		shears.setHighlightInInventory(true);
		potatoSeed4 = new ItemRequirement("Potato seeds", ItemID.POTATO_SEED, 4);
		potatoSeed4.setHighlightInInventory(true);
		rake = new ItemRequirement("Rake", ItemID.RAKE).isNotConsumed();
		rake.setHighlightInInventory(true);
		dibber = new ItemRequirement("Seed dibber", ItemID.DIBBER).isNotConsumed();

		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_WATER);
		desertBottom = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE, 1, true).isNotConsumed();
		desertTop = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT, 1, true).isNotConsumed();
		hat = new ItemRequirement("Doctor's or Nurse hat", ItemID.TWOCATS_DOCTORS_HAT, 1, true).isNotConsumed();
		hat.addAlternates(ItemID.TWOCATS_NURSES_HAT);
		hat.setDisplayMatchedItemName(true);

		burthorpeTeleport = TeleportCollections.BURTHORPE_TELEPORT.getItemRequirement();
		varrockTeleport = TeleportCollections.VARROCK_TELEPORT.getItemRequirement();
		sophanemTeleport = TeleportCollections.SOPHANEM_TELEPORT.getItemRequirement();
		staminaPotion = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);
	}

	public void setupConditions()
	{
		bobNearby = new NpcRequirement("Bob nearby", NpcID.DEATH_GROWNCAT_BLACK_VIS);

		rakedPatch = new VarbitRequirement(1033, 3);
		plantedSeed = new VarbitRequirement(VarbitID.TWOCATS_CHORES_TIDYGARDEN, 4, Operation.GREATER_EQUAL);
		grownPotatoes = new VarbitRequirement(1033, 8);
		madeBed = new VarbitRequirement(1029, 1);
		placedLogs = new VarbitRequirement(1030, 1);
		litLogs = new VarbitRequirement(1030, 2);
		placedCake = new VarbitRequirement(1031, 3);
		placedMilk = new VarbitRequirement(1031, 4);
		usedShears = new VarbitRequirement(1032, 8);
	}

	public void setupSteps()
	{
		talkToUnferth = new NpcStep(this, NpcID.TWOCATS_UNFERTH_BALD, new WorldPoint(2919, 3559, 0), "Talk to Unferth in north east Burthorpe.", cat, catspeak);
		talkToUnferth.addDialogSteps("I'll help you.", "Yes.");
		talkToUnferth.addTeleport(burthorpeTeleport);
		talkToHild = new NpcStep(this, NpcID.DEATH_WOMAN_INDOORS1, new WorldPoint(2930, 3568, 0), "Talk to Hild in the house north east of Unferth.", deathRune5, catspeak);
		findBob = new DetailedQuestStep(this, "Operate the catspeak amulet (e) to locate Bob the Cat. He's often in Catherby Archery Shop or at the Varrock Anvil.", catspeakE);
		findBob.addTeleport(varrockTeleport);
		talkToBob = new NpcStep(this, NpcID.DEATH_GROWNCAT_BLACK_VIS, "Talk to Bob the Cat.", cat, catspeakEWorn);
		talkToGertrude = new NpcStep(this, NpcID.GERTRUDE_POST, new WorldPoint(3151, 3413, 0), "Talk to Gertrude west of Varrock.", cat, catspeakEWorn);
		talkToGertrude.addDialogStep("Ask about Bob's parents.");
		talkToGertrude.addTeleport(varrockTeleport);

		talkToReldo = new NpcStep(this, NpcID.RELDO_NORMAL, new WorldPoint(3211, 3494, 0), "Talk to Reldo in the Varrock Castle's library.", cat, catspeakEWorn);
		((NpcStep) talkToReldo).addAlternateNpcs(NpcID.RELDO);
		talkToReldo.addDialogSteps("I have a cat related question.", "Ask about Robert the Strong.");
		findBobAgain = new DetailedQuestStep(this, "Use the catspeak amulet (e) again to locate Bob the Cat.", catspeakE);
		talkToBobAgain = new NpcStep(this, NpcID.DEATH_GROWNCAT_BLACK_VIS, "Talk to Bob the Cat again.", cat, catspeakEWorn);
		talkToSphinx = new NpcStep(this, NpcID.ICS_LITTLE_SPHINX, new WorldPoint(3302, 2784, 0), "Talk to the Sphinx in Sophanem.", cat, catspeakEWorn);
		talkToSphinx.addDialogStep("Ask the Sphinx for help for Bob.");
		talkToSphinx.addTeleport(sophanemTeleport);
		useRake = new ObjectStep(this, ObjectID.TWOCATS_PATCH, new WorldPoint(2919, 3562, 0), "Rake Unferth's patch", rake);
		useRake.addIcon(ItemID.RAKE);
		useRake.addTeleport(burthorpeTeleport);
		plantSeeds = new ObjectStep(this, ObjectID.TWOCATS_PATCH, new WorldPoint(2919, 3562, 0), "Plant 4 potato seeds in Unferth's patch. These can take 15-35 minutes to grow.", dibber, potatoSeed4);
		plantSeeds.addIcon(ItemID.POTATO_SEED);
		makeBed = new ObjectStep(this, ObjectID.TWOCATS_BED, new WorldPoint(2917, 3557, 0), "Make Unferth's bed.");
		useLogsOnFireplace = new ObjectStep(this, ObjectID.TWOCATS_FIREPLACE, new WorldPoint(2919, 3557, 0), "Use logs on Unferth's fireplace", logs);
		useLogsOnFireplace.addIcon(ItemID.LOGS);
		lightLogs = new ObjectStep(this, ObjectID.TWOCATS_FIREPLACE, new WorldPoint(2919, 3557, 0), "Use a tinderbox on Unferth's fireplace.", tinderbox.highlighted());
		lightLogs.addIcon(ItemID.TINDERBOX);
		useChocolateCakeOnTable = new ObjectStep(this, ObjectID.TWOCATS_TABLE, new WorldPoint(2921, 3556, 0), "USE a chocolate cake on Unferth's table.", chocolateCake);
		useChocolateCakeOnTable.addIcon(ItemID.CHOCOLATE_CAKE);
		useMilkOnTable = new ObjectStep(this, ObjectID.TWOCATS_TABLE, new WorldPoint(2921, 3556, 0), "Use a bucket of milk on Unferth's table.", milk);
		useMilkOnTable.addIcon(ItemID.BUCKET_MILK);
		useShearsOnUnferth = new NpcStep(this, NpcID.TWOCATS_UNFERTH_LONGHAIR, new WorldPoint(2919, 3559, 0), "Use some shears on Unferth in north east Burthorpe.", shears);
		((NpcStep) (useShearsOnUnferth)).addAlternateNpcs(NpcID.TWOCATS_UNFERTH_BALD, NpcID.TWOCATS_UNFERTH_SHORTHAIR, NpcID.TWOCATS_UNFERTH_MEDIUMHAIR, NpcID.TWOCATS_UNFERTH_SPIKEYHAIR);
		useShearsOnUnferth.addIcon(ItemID.SHEARS);

		waitForPotatoesToGrow = new DetailedQuestStep(this, "You now need to wait 15-35 minutes for the potatoes to grow.");

		reportToUnferth = new NpcStep(this, NpcID.TWOCATS_UNFERTH_BALD, new WorldPoint(2919, 3559, 0), "Talk to Unferth in north east Burthorpe again.", cat, catspeakEWorn);
		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3405, 0), "Talk to the Apothecary in south west Varrock.", cat, catspeakEWorn);
		talkToApoth.addDialogStep("Talk about A Tail of Two Cats.");
		talkToApoth.addTeleport(varrockTeleport);

		talkToUnferthAsDoctor = new NpcStep(this, NpcID.TWOCATS_UNFERTH_BALD, new WorldPoint(2919, 3559, 0),
			"Talk to Unferth whilst wearing the doctor/nurse hat, a desert shirt and a desert robe, and no weapon/shield.", cat, catspeakEWorn, hat, desertTop, desertBottom, vialOfWater);
		talkToUnferthAsDoctor.addTeleport(burthorpeTeleport);
		findBobToFinish = new DetailedQuestStep(this, "Use the catspeak amulet (e) to locate Bob once more.", catspeakE);
		findBobToFinish.addTeleport(varrockTeleport);
		talkToBobToFinish = new NpcStep(this, NpcID.DEATH_GROWNCAT_BLACK_VIS, "Talk to Bob the Cat again.", cat, catspeakEWorn);
		talkToUnferthToFinish = new NpcStep(this, NpcID.TWOCATS_UNFERTH_BALD, new WorldPoint(2919, 3559, 0), "Talk to Unferth to complete the quest.");
		talkToUnferthToFinish.addTeleport(burthorpeTeleport);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(cat, catspeak, deathRune5, chocolateCake, logs, tinderbox, milk, shears, potatoSeed4, rake, dibber, vialOfWater, desertTop, desertBottom);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(burthorpeTeleport.quantity(4), varrockTeleport.quantity(4), sophanemTeleport);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("2,500 Experience Lamps (Any skill over level 30).", ItemID.THOSF_REWARD_LAMP, 2),
				new ItemReward("A Doctors hat", ItemID.TWOCATS_DOCTORS_HAT, 1),
				new ItemReward("A Nurse hat", ItemID.TWOCATS_NURSES_HAT, 1),
				new ItemReward("A Mouse Toy", ItemID.TWOCATS_MOUSE_TOY, 1)); //4447 Is Placeholder.
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToUnferth, talkToHild, findBob, talkToBob), cat, catspeak, deathRune5));

		allSteps.add(new PanelDetails("Bob's past",
			Arrays.asList(talkToGertrude, talkToReldo, findBobAgain, talkToBobAgain, talkToSphinx), cat));

		allSteps.add(new PanelDetails("Helping Unferth",
			Arrays.asList(useRake, plantSeeds, makeBed, useLogsOnFireplace, lightLogs,
				useChocolateCakeOnTable, useMilkOnTable, useShearsOnUnferth, reportToUnferth),
			cat, catspeakE, rake, dibber, potatoSeed4, logs, tinderbox, chocolateCake, milk, shears));

		allSteps.add(new PanelDetails("'Curing' Unferth",
			Arrays.asList(talkToApoth, talkToUnferthAsDoctor, findBobToFinish, talkToBobToFinish,
				talkToUnferthToFinish), cat, catspeakE, vialOfWater, desertTop, desertBottom));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER, QuestState.FINISHED));
	}
}
