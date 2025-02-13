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
package com.questhelper.helpers.quests.contact;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;

import java.util.*;

import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

public class Contact extends BasicQuestHelper
{
	//Items Required
	ItemRequirement lightSource, tinderbox, combatGear, parchment, keris, food, prayerPotions;

	// Item recommended
	ItemRequirement coins, glory, antipoison, waterskin;

	Requirement inBank, inDungeon, inChasm, hasReadParchment, kerisNearby;

	QuestStep talkToHighPriest, talkToJex, goDownToBank, goDownToDungeon, goDownToChasm, searchKaleef, readParchment, talkToMaisa, talkToOsman, talkToOsmanOutsideSoph, goDownToBankAgain, goDownToDungeonAgain, goDownToChasmAgain,
		killGiantScarab, pickUpKeris, talkToOsmanChasm, returnToHighPriest;

	//Zones
	Zone bank, dungeon, chasm;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToHighPriest);
		steps.put(10, talkToHighPriest);
		steps.put(20, talkToHighPriest);
		steps.put(30, talkToJex);

		ConditionalStep goInvestigate = new ConditionalStep(this, goDownToBank);
		goInvestigate.addStep(new Conditions(inChasm, hasReadParchment), talkToMaisa);
		goInvestigate.addStep(parchment, readParchment);
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

		ConditionalStep talkToOsmanInCrevice = new ConditionalStep(this, goDownToBankAgain);
		talkToOsmanInCrevice.addStep(kerisNearby, pickUpKeris);
		talkToOsmanInCrevice.addStep(inChasm, talkToOsmanChasm);
		talkToOsmanInCrevice.addStep(inDungeon, goDownToChasmAgain);
		talkToOsmanInCrevice.addStep(inBank, goDownToDungeonAgain);

		steps.put(110, talkToOsmanInCrevice);

		ConditionalStep finishOff = new ConditionalStep(this, returnToHighPriest);
		finishOff.addStep(kerisNearby, pickUpKeris);

		steps.put(120, finishOff);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		lightSource = new ItemRequirement("A light source", ItemCollections.LIGHT_SOURCES).isNotConsumed();
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		parchment = new ItemRequirement("Parchment", ItemID.PARCHMENT);
		parchment.setHighlightInInventory(true);

		combatGear = new ItemRequirement("Combat gear, preferably magic/ranged", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		antipoison = new ItemRequirement("Antipoisons", ItemCollections.ANTIPOISONS);
		waterskin = new ItemRequirement("Waterskins for desert travel", ItemCollections.WATERSKIN);

		keris = new ItemRequirement("Keris", ItemID.KERIS);

		coins = new ItemRequirement("Coins for carpet rides", ItemCollections.COINS);
		glory = new ItemRequirement("Amulet of glory for getting to Osman", ItemCollections.AMULET_OF_GLORIES);
	}

	@Override
	protected void setupZones()
	{
		bank = new Zone(new WorldPoint(2772, 5129, 0), new WorldPoint(2758, 5145, 0));
		dungeon = new Zone(8516);
		chasm = new Zone(new WorldPoint(3216, 9217, 0), new WorldPoint(3265, 9277, 0));
	}

	public void setupConditions()
	{
		inBank = new ZoneRequirement(bank);
		inDungeon = new ZoneRequirement(dungeon);
		inChasm = new ZoneRequirement(chasm, new Zone(9027));
		hasReadParchment = new VarbitRequirement(3274, 50, Operation.GREATER_EQUAL);
		kerisNearby = new ItemOnTileRequirement(keris);
	}

	public void setupSteps()
	{
		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in Sophanem.");
		talkToHighPriest.addDialogStep("Yes.");
		talkToHighPriest.addDialogStep("Sounds like a quest for me; I can't turn that down!");
		talkToHighPriest.addDialogStep("Is there any way into Menaphos from below?");
		talkToJex = new NpcStep(this, NpcID.JEX, new WorldPoint(3312, 2799, 0), "Talk to Jex in the building in north east Sophanem.");

		goDownToBank = new ObjectStep(this, ObjectID.LADDER_20275, new WorldPoint(3315, 2797, 0), "Go down the ladder east of Jex.", lightSource);
		goDownToDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_20340, new WorldPoint(2766, 5130, 0), "Go down the trapdoor.", lightSource);
		goDownToChasm = new ObjectStep(this, ObjectID.LADDER_20287, new WorldPoint(2116, 4365, 2),
			"Be careful of traps, and follow the path to the south west ladder. Disarm traps where the path breaks, " +
				"and use protection prayers against the monsters.");

		List<WorldPoint> path = Arrays.asList(
			new WorldPoint(2166, 4409, 2),
			new WorldPoint(2166, 4401, 2),
			new WorldPoint(2161, 4401, 2),
			new WorldPoint(2161, 4408, 2),
			new WorldPoint(2154, 4408, 2),
			new WorldPoint(2154, 4410, 2),
			new WorldPoint(2144, 4410, 2),
			new WorldPoint(2144, 4404, 2),
			new WorldPoint(2147, 4404, 2),
			new WorldPoint(0, 0, 0),
			new WorldPoint(2150, 4404, 2),
			new WorldPoint(2152, 4404, 2),
			new WorldPoint(2152, 4400, 2),
			new WorldPoint(2156, 4400, 2),
			new WorldPoint(2156, 4396, 2),
			new WorldPoint(2158, 4396, 2),
			new WorldPoint(2159, 4395, 2),
			new WorldPoint(2160, 4395, 2),
			new WorldPoint(2161, 4396, 2),
			new WorldPoint(2162, 4396, 2),
			new WorldPoint(2163, 4395, 2),
			new WorldPoint(2164, 4395, 2),
			new WorldPoint(2165, 4396, 2),
			new WorldPoint(2169, 4396, 2),
			new WorldPoint(2169, 4390, 2),

			new WorldPoint(0, 0, 0),

			new WorldPoint(2169, 4387, 2),
			new WorldPoint(2169, 4382, 2),
			new WorldPoint(2160, 4382, 2),
			new WorldPoint(2160, 4388, 2),
			new WorldPoint(2153, 4388, 2),
			new WorldPoint(2153, 4391, 2),
			new WorldPoint(2145, 4391, 2),
			new WorldPoint(2145, 4387, 2),
			new WorldPoint(2149, 4387, 2),
			new WorldPoint(2149, 4374, 2),
			new WorldPoint(2151, 4374, 2),

			new WorldPoint(0, 0, 0),

			new WorldPoint(2154, 4374, 2),
			new WorldPoint(2157, 4374, 2),
			new WorldPoint(2157, 4369, 2),
			new WorldPoint(2145, 4369, 2),
			new WorldPoint(2145, 4365, 2),
			new WorldPoint(2144, 4364, 2),
			new WorldPoint(2144, 4361, 2),
			new WorldPoint(2132, 4361, 2),
			new WorldPoint(2132, 4363, 2),
			new WorldPoint(2127, 4363, 2),
			new WorldPoint(2127, 4362, 2),
			new WorldPoint(2124, 4362, 2),
			new WorldPoint(2124, 4364, 2),
			new WorldPoint(2116, 4364, 2)
		);

		((DetailedQuestStep) goDownToChasm).setLinePoints(path);

		searchKaleef = new ObjectStep(this, NullObjectID.NULL_44597, new WorldPoint(2284, 4315, 0),
			"Follow the path along, and search Kaleef's corpse there.");

		readParchment = new DetailedQuestStep(this, "Read the parchment", parchment);
		talkToMaisa = new NpcStep(this, NpcID.MAISA, new WorldPoint(2258, 4317, 0), "Talk to Maisa on the west side of the chasm.");
		talkToMaisa.addDialogStep("Draynor Village.");
		talkToMaisa.addDialogStep("Leela.");

		talkToOsman = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3287, 3179, 0), "Talk to Osman in Al Kharid.");
		talkToOsman.addDialogStep("I want to talk to you about Sophanem.");
		talkToOsman.addDialogStep("It could drive a wedge between the Menaphite cities.");
		talkToOsmanOutsideSoph = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(3285, 2812, 0), "Talk to Osman north of Sophanem.");
		talkToOsmanOutsideSoph.addDialogStep("I know of a secret entrance to the north.");

		goDownToBankAgain = new ObjectStep(this, ObjectID.LADDER_20275, new WorldPoint(3315, 2797, 0), "Prepare to fight a level 191 Giant Scarab. Go down the ladder east of Jex.", lightSource, combatGear);
		goDownToDungeonAgain = new ObjectStep(this, ObjectID.TRAPDOOR_20340, new WorldPoint(2766, 5130, 0), "Go down the trapdoor.", lightSource);
		goDownToChasmAgain = new ObjectStep(this, ObjectID.LADDER_20287, new WorldPoint(2116, 4365, 2), "Be careful of traps, and follow the path to the south west ladder. Disarm traps where the path breaks, " +
				"and use protection prayers against the monsters.");
		((DetailedQuestStep) goDownToChasmAgain).setLinePoints(path);

		killGiantScarab = new NpcStep(this, NpcID.GIANT_SCARAB, new WorldPoint(2272, 4323, 0),
			"Kill the Giant Scarab near the chasm. It can extinguish your light source and poison you, so be careful. Pray melee if you are meleeing it, range if you are attacking it from a distance.");

		pickUpKeris = new ItemStep(this, "Pick up the Keris.", keris);

		talkToOsmanChasm = new NpcStep(this, NpcID.OSMAN_4286, new WorldPoint(2272, 4323, 0), "Talk to Osman in the chasm.");

		returnToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Report back to the High Priest in Sophanem.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(lightSource);
		reqs.add(tinderbox);
		reqs.add(combatGear);
		reqs.add(food);
		reqs.add(prayerPotions);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(coins.quantity(1000));
		reqs.add(glory);
		reqs.add(antipoison);
		reqs.add(waterskin);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Giant Scarab (level 191)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 7000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("7,000 Experience Lamps (Combat Skills)", ItemID.ANTIQUE_LAMP, 2),
				new ItemReward("Keris", ItemID.KERIS, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Sophanem's Bank"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToHighPriest, talkToJex), lightSource, tinderbox, antipoison));

		allSteps.add(new PanelDetails("Explore the dungeon",
			Arrays.asList(goDownToBank, goDownToDungeon, goDownToChasm, searchKaleef, readParchment,
				talkToMaisa, talkToOsman), lightSource, tinderbox, antipoison));

		allSteps.add(new PanelDetails("Help Osman",
			Arrays.asList(talkToOsmanOutsideSoph, goDownToBankAgain, goDownToDungeonAgain,
				goDownToChasmAgain, killGiantScarab, talkToOsmanChasm, pickUpKeris, returnToHighPriest), combatGear, food, prayerPotions,
			lightSource, tinderbox, antipoison));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.PRINCE_ALI_RESCUE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER, QuestState.FINISHED));
		return req;
	}
}
