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
package com.questhelper.quests.atailoftwocats;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.A_TAIL_OF_TWO_CATS
)
public class ATailOfTwoCats extends BasicQuestHelper
{
	ItemRequirement catspeak, catspeakE, deathRune5, cat, chocolateCake, logs, tinderbox, milk, shears, potatoSeed4, rake, dibber, vialOfWater, desertTop, desertBottom, hat, catspeakEWorn;

	ConditionForStep bobNearby, rakedPatch, madeBed, plantedSeed, placedLogs, litLogs, placedCake, placedMilk, usedShears, grownPotatoes;

	QuestStep talkToUnferth, talkToHild, findBob, talkToBob, talkToGertrude, talkToReldo, findBobAgain, talkToBobAgain, talkToSphinx, useRake, plantSeeds, makeBed, useLogsOnFireplace, lightLogs,
		useChocolateCakeOnTable, useMilkOnTable, useShearsOnUnferth, reportToUnferth, talkToApoth, talkToUnferthAsDoctor, findBobToFinish, talkToBobToFinish, talkToUnferthToFinish, waitForPotatoesToGrow;

	Zone bank;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
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

	public void setupItemRequirements()
	{
		catspeak = new ItemRequirement("Catspeak amulet", ItemID.CATSPEAK_AMULET);
		catspeakE = new ItemRequirement("Catspeak amulet (e)", ItemID.CATSPEAK_AMULETE);
		catspeakEWorn = new ItemRequirement("Catspeak amulet (e)", ItemID.CATSPEAK_AMULETE, 1, true);
		catspeakE.setHighlightInInventory(true);
		deathRune5 = new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 5);
		cat = new ItemRequirement("Any cat", ItemCollections.getCats());
		chocolateCake = new ItemRequirement("Chocolate cake", ItemID.CHOCOLATE_CAKE);
		chocolateCake.setHighlightInInventory(true);
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		logs.setHighlightInInventory(true);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.setHighlightInInventory(true);
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milk.setHighlightInInventory(true);
		shears = new ItemRequirement("Shears", ItemID.SHEARS);
		shears.setHighlightInInventory(true);
		potatoSeed4 = new ItemRequirement("Potato seeds", ItemID.POTATO_SEED);
		potatoSeed4.setHighlightInInventory(true);
		rake = new ItemRequirement("Rake", ItemID.RAKE);
		rake.setHighlightInInventory(true);
		dibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);

		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER);
		desertBottom = new ItemRequirement("Desert robe", ItemID.DESERT_ROBE, 1, true);
		desertTop = new ItemRequirement("Desert shirt", ItemID.DESERT_SHIRT, 1, true);
		hat = new ItemRequirement("Doctor's or Nurse hat", ItemID.DOCTORS_HAT, 1, true);
		hat.addAlternates(ItemID.NURSE_HAT);
	}

	public void setupZones()
	{

	}

	public void setupConditions()
	{
		bobNearby = new NpcCondition(NpcID.BOB_8034);

		rakedPatch = new VarbitCondition(1033, 3);
		plantedSeed = new VarbitCondition(1033, 4, Operation.GREATER_EQUAL);
		grownPotatoes = new VarbitCondition(1033, 8);
		madeBed = new VarbitCondition(1029, 1);
		placedLogs = new VarbitCondition(1030, 1);
		litLogs = new VarbitCondition(1030, 2);
		placedCake = new VarbitCondition(1031, 3);
		placedMilk = new VarbitCondition(1031, 4);
		usedShears = new VarbitCondition(1032, 8);
	}

	public void setupSteps()
	{
		talkToUnferth = new NpcStep(this, NpcID.UNFERTH, new WorldPoint(2919, 3559, 0), "Talk to Unferth in north east Burthorpe.", cat, catspeak);
		talkToUnferth.addDialogStep("I'll help you.");
		talkToHild = new NpcStep(this, NpcID.HILD_4112, new WorldPoint(2930, 3568, 0), "Talk to Hild in the house north east of Unferth.", deathRune5, catspeak);
		findBob = new DetailedQuestStep(this, "Operate the catspeak amulet (e) to locate Bob the Cat.", catspeakE);
		talkToBob = new NpcStep(this, NpcID.BOB_8034, "Talk to Bob the Cat.", cat, catspeakEWorn);
		talkToGertrude = new NpcStep(this, NpcID.GERTRUDE_7723, new WorldPoint(3151, 3413, 0), "Talk to Gertrude west of Varrock.", cat, catspeakEWorn);
		talkToGertrude.addDialogStep("Ask about Bob's parents.");

		talkToReldo = new NpcStep(this, NpcID.RELDO_4243, new WorldPoint(3211, 3494, 0), "Talk to Reldo in the Varrock Museum.", cat, catspeakEWorn);
		talkToReldo.addDialogStep("Ask about Robert the Strong.");
		findBobAgain = new DetailedQuestStep(this, "Use the catspeak amulet (e) again to locate Bob the Cat.", catspeakE);
		talkToBobAgain = new NpcStep(this, NpcID.BOB_8034, "Talk to Bob the Cat again.", cat, catspeakEWorn);
		talkToSphinx = new NpcStep(this, NpcID.SPHINX_4209, new WorldPoint(3302, 2784, 0), "Talk to the Sphinx in Sophanem.", cat, catspeakEWorn);
		talkToSphinx.addDialogStep("Ask the Sphinx for help for Bob.");
		talkToSphinx.addDialogStep("View the cutscene (5 mins 20 secs).");
		useRake = new ObjectStep(this, NullObjectID.NULL_9399, new WorldPoint(2919, 3562, 0), "Rake Unferth's patch", rake);
		useRake.addIcon(ItemID.RAKE);
		plantSeeds = new ObjectStep(this, NullObjectID.NULL_9399, new WorldPoint(2919, 3562, 0), "Plant 4 potato seeds in Unferth's patch", dibber, potatoSeed4);
		plantSeeds.addIcon(ItemID.POTATO_SEED);
		makeBed = new ObjectStep(this, NullObjectID.NULL_9438, new WorldPoint(2917, 3557, 0), "Make Unferth's bed.");
		useLogsOnFireplace = new ObjectStep(this, NullObjectID.NULL_9442, new WorldPoint(2919, 3557, 0), "Use logs on Unferth's fireplace", logs);
		useLogsOnFireplace.addIcon(ItemID.LOGS);
		lightLogs = new ObjectStep(this, NullObjectID.NULL_9442, new WorldPoint(2919, 3557, 0), "Use a tinderbox on Unferth's fireplace.");
		lightLogs.addIcon(ItemID.TINDERBOX);
		useChocolateCakeOnTable = new ObjectStep(this, NullObjectID.NULL_9435, new WorldPoint(2921, 3556, 0), "Use a chocolate cake on Unferth's table.", chocolateCake);
		useChocolateCakeOnTable.addIcon(ItemID.CHOCOLATE_CAKE);
		useMilkOnTable = new ObjectStep(this, NullObjectID.NULL_9435, new WorldPoint(2921, 3556, 0), "Use a bucket of milk on Unferth's table.", milk);
		useMilkOnTable.addIcon(ItemID.BUCKET_OF_MILK);
		useShearsOnUnferth = new ObjectStep(this, NpcID.UNFERTH_4241, new WorldPoint(2919, 3559, 0), "Use some shears on Unferth in north east Burthorpe.", shears);
		useShearsOnUnferth.addIcon(ItemID.SHEARS);

		waitForPotatoesToGrow = new DetailedQuestStep(this, "You now need to wait 15-35 minutes for the potatoes to grow.");

		reportToUnferth = new NpcStep(this, NpcID.UNFERTH, new WorldPoint(2919, 3559, 0), "Talk to Unferth in north east Burthorpe again.", cat, catspeakEWorn);
		talkToApoth = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3405, 0), "Talk to the Apothecary in south west Varrock.", cat, catspeakEWorn);
		talkToApoth.addDialogStep("Talk about A Tail of Two Cats.");

		talkToUnferthAsDoctor = new NpcStep(this, NpcID.UNFERTH, new WorldPoint(2919, 3559, 0),
			"Talk to Unferth whilst wearing the doctor/nurse hat, a desert shirt and a desert robe, and no weapon/shield.", cat, catspeakEWorn, hat, desertTop, desertBottom, vialOfWater);
		findBobToFinish = new DetailedQuestStep(this, "Use the catspeak amulet (e) to locate Bob once more.", catspeakE);
		talkToBobToFinish = new NpcStep(this, NpcID.BOB_8034, "Talk to Bob the Cat again.", cat, catspeakEWorn);
		talkToUnferthToFinish = new NpcStep(this, NpcID.UNFERTH, new WorldPoint(2919, 3559, 0), "Talk to Unferth to complete the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(cat, catspeak, deathRune5, chocolateCake, logs, tinderbox, milk, shears, potatoSeed4, rake, dibber, vialOfWater, desertTop, desertBottom));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToUnferth, talkToHild, findBob, talkToBob)), cat, catspeak, deathRune5));

		allSteps.add(new PanelDetails("Bob's past", new ArrayList<>(Arrays.asList(talkToGertrude, talkToReldo, findBobAgain, talkToBobAgain, talkToSphinx)), cat, catspeakE));

		allSteps.add(new PanelDetails("Helping Unferth", new ArrayList<>(Arrays.asList(useRake, plantSeeds, makeBed, useLogsOnFireplace, lightLogs, useChocolateCakeOnTable, useMilkOnTable, useShearsOnUnferth, reportToUnferth)),
			cat, catspeakE, rake, dibber, potatoSeed4, logs, tinderbox, chocolateCake, milk, shears));

		allSteps.add(new PanelDetails("'Curing' Unferth", new ArrayList<>(Arrays.asList(talkToApoth, talkToUnferthAsDoctor, findBobToFinish, talkToBobToFinish, talkToUnferthToFinish)), cat, catspeakE, vialOfWater));

		return allSteps;
	}
}