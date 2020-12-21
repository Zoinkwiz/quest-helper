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
package com.questhelper.quests.contact;

import com.questhelper.BankSlotIcons;
import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
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
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.CONTACT
)
public class Contact extends BasicQuestHelper
{
	ItemRequirement lightSource, combatGear, parchment, keris;

	ConditionForStep inBank, inDungeon, inChasm, hasParchment, hasReadParchment, kerisNearby;

	QuestStep talkToHighPriest, talkToJex, goDownToBank, goDownToDungeon, goDownToChasm, searchKaleef, readParchment, talkToMaisa, talkToOsman, talkToOsmanOutsideSoph, goDownToBankAgain, goDownToDungeonAgain, goDownToChasmAgain,
		killGiantScarab, pickUpKeris, returnToHighPriest;

	Zone bank, dungeon, chasm;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHighPriest);
		steps.put(10, talkToHighPriest);
		steps.put(20, talkToHighPriest);
		steps.put(30, talkToJex);

		ConditionalStep goInvestigate = new ConditionalStep(this, goDownToBank);
		goInvestigate.addStep(new Conditions(inChasm, hasReadParchment), talkToMaisa);
		goInvestigate.addStep(hasParchment, readParchment);
		goInvestigate.addStep(inChasm, searchKaleef);
		goInvestigate.addStep(inDungeon, goDownToChasm);
		goInvestigate.addStep(inBank, goDownToDungeon);

		steps.put(40, goInvestigate);

		steps.put(50, goInvestigate);

		steps.put(60, talkToOsman);

		steps.put(70, talkToOsmanOutsideSoph);

		ConditionalStep scarabBattle = new ConditionalStep(this, goDownToBankAgain);
		scarabBattle.addStep(inChasm, killGiantScarab);
		scarabBattle.addStep(inDungeon, goDownToChasmAgain);
		scarabBattle.addStep(inBank, goDownToDungeonAgain);

		steps.put(80, scarabBattle);
		steps.put(90, scarabBattle);
		steps.put(100, scarabBattle);

		ConditionalStep finishOff = new ConditionalStep(this, returnToHighPriest);
		finishOff.addStep(kerisNearby, pickUpKeris);

		steps.put(110, finishOff);
		steps.put(120, finishOff);

		return steps;
	}

	public void setupItemRequirements()
	{
		lightSource = new ItemRequirement("A light source", ItemCollections.getLightSources());
		parchment = new ItemRequirement("Parchment", ItemID.PARCHMENT);
		parchment.setHighlightInInventory(true);

		combatGear = new ItemRequirement("Combat gear, food, prayer potions", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		keris = new ItemRequirement("Keris", ItemID.KERIS);
	}

	public void setupZones()
	{
		bank = new Zone(new WorldPoint(2772, 5129, 0), new WorldPoint(2758, 5145, 0));
		dungeon = new Zone(new WorldPoint(3263, 9200, 2), new WorldPoint(3327, 9280, 2));
		chasm = new Zone(new WorldPoint(3216, 9217, 0), new WorldPoint(3265, 9277, 0));
	}

	public void setupConditions()
	{
		inBank = new ZoneCondition(bank);
		inDungeon = new ZoneCondition(dungeon);
		inChasm = new ZoneCondition(chasm);
		hasParchment = new ItemRequirementCondition(parchment);
		hasReadParchment = new VarbitCondition(3274, 50);
		kerisNearby = new ItemCondition(keris);
	}

	public void setupSteps()
	{
		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in Sophanem.");
		talkToHighPriest.addDialogStep("Sounds like a quest for me; I can't turn that down!");
		talkToHighPriest.addDialogStep("Is there any way into Menaphos from below?");
		talkToJex = new NpcStep(this, NpcID.JEX, new WorldPoint(3312, 2799, 0), "Talk to Jex in the building in north east Sophanem.");

		goDownToBank = new ObjectStep(this, ObjectID.LADDER_20275, new WorldPoint(3315, 2797, 0), "Go down the ladder east of Jex.", lightSource);
		goDownToDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_20340, new WorldPoint(2766, 5130, 0), "Go down the trapdoor.", lightSource);
		goDownToChasm = new ObjectStep(this, ObjectID.LADDER_20287, new WorldPoint(3268, 9229, 2), "Be careful of traps, and make your way the south west corner of the dungeon, and go down the ladder there.");
		searchKaleef = new ObjectStep(this, ObjectID.KALEEFS_BODY, new WorldPoint(3239, 9244, 0), "Follow the path along, and search Kaleef's corpse there.");

		readParchment = new DetailedQuestStep(this, "Read the parchment", parchment);
		talkToMaisa = new NpcStep(this, NpcID.MAISA, new WorldPoint(3218, 9246, 0), "Talk to Maisa on the west side of the chasm.");
		talkToMaisa.addDialogStep("Draynor Village");
		talkToMaisa.addDialogStep("Leela.");

		talkToOsman = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3287, 3179, 0), "Talk to Osman in Al Kharid.");
		talkToOsman.addDialogStep("I want to talk to you about Sophanem.");
		talkToOsman.addDialogStep("It would drive a wedge between the Menaphite cities.");
		talkToOsmanOutsideSoph = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3285, 2812, 0), "Talk to Osman north of Sophanem.");
		talkToOsmanOutsideSoph.addDialogStep("I know of a secret entrance to the north.");

		goDownToBankAgain = new ObjectStep(this, ObjectID.LADDER_20275, new WorldPoint(3315, 2797, 0), "Prepare to fight a level 191 Giant Scarab. Go down the ladder east of Jex.", lightSource, combatGear);
		goDownToDungeonAgain = new ObjectStep(this, ObjectID.TRAPDOOR_20340, new WorldPoint(2766, 5130, 0), "Go down the trapdoor.", lightSource);
		goDownToChasmAgain = new ObjectStep(this, ObjectID.LADDER_20287, new WorldPoint(3268, 9229, 2), "Be careful of traps, and make your way the south west corner of the dungeon, and go down the ladder there.");

		killGiantScarab = new NpcStep(this, NpcID.GIANT_SCARAB, new WorldPoint(3231, 9251, 0), "Kill the Giant Scarab near the chasm.");

		pickUpKeris = new ItemStep(this, "Pick up the Keris.", keris);

		returnToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Report back to the High Priest in Sophanem.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(lightSource);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Giant Scarab (level 191)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToHighPriest, talkToJex)), lightSource));

		allSteps.add(new PanelDetails("Explore the dungeon", new ArrayList<>(Arrays.asList(goDownToBank, goDownToDungeon, goDownToChasm, searchKaleef, readParchment, talkToMaisa, talkToOsman)), lightSource));

		allSteps.add(new PanelDetails("Help Osman", new ArrayList<>(Arrays.asList(talkToOsmanOutsideSoph, goDownToBankAgain, goDownToDungeonAgain, goDownToChasmAgain, killGiantScarab, returnToHighPriest)), combatGear, lightSource));

		return allSteps;
	}
}
