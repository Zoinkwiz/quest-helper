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
package com.questhelper.quests.druidicritual;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.DRUIDIC_RITUAL
)
public class DruidicRitual extends BasicQuestHelper
{
	//Items Required
	ItemRequirement rawRat, rawBear, rawBeef, rawChicken, rawRatHighlighted, rawBearHighlighted, rawBeefHighlighted,
		rawChickenHighlighted, enchantedBear, enchantedBeef, enchantedChicken, enchantedRat;

	Requirement inDungeon, inSanfewRoom;

	QuestStep talkToKaqemeex, goUpToSanfew, talkToSanfew, enterDungeon, enchantMeats, useRatOnCauldron, useBeefOnCauldron,
		useBearOnCauldron, useChickenOnCauldron, goUpToSanfewWithMeat, talkToSanfewWithMeat, talkToKaqemeexToFinish;

	//Zones
	Zone dungeon, sanfewRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToKaqemeex);

		ConditionalStep goTalkToSanfew = new ConditionalStep(this, goUpToSanfew);
		goTalkToSanfew.addStep(inSanfewRoom, talkToSanfew);
		steps.put(1, goTalkToSanfew);

		ConditionalStep prepareMeats = new ConditionalStep(this, enterDungeon);
		prepareMeats.addStep(new Conditions(inSanfewRoom, enchantedRat, enchantedBear, enchantedBeef, enchantedChicken), talkToSanfewWithMeat);
		prepareMeats.addStep(new Conditions(enchantedRat, enchantedBear, enchantedBeef, enchantedChicken), goUpToSanfewWithMeat);
		prepareMeats.addStep(new Conditions(inDungeon, enchantedRat, enchantedBear, enchantedBeef), useChickenOnCauldron);
		prepareMeats.addStep(new Conditions(inDungeon, enchantedRat, enchantedBear), useBeefOnCauldron);
		prepareMeats.addStep(new Conditions(inDungeon, enchantedRat), useBearOnCauldron);
		prepareMeats.addStep(inDungeon, useRatOnCauldron);
		steps.put(2, prepareMeats);

		steps.put(3, talkToKaqemeexToFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		rawRat = new ItemRequirement("Raw rat meat", ItemID.RAW_RAT_MEAT);
		rawRat.addAlternates(ItemID.ENCHANTED_RAT);
		rawBear = new ItemRequirement("Raw bear meat", ItemID.RAW_BEAR_MEAT);
		rawBear.addAlternates(ItemID.ENCHANTED_BEAR);
		rawBeef = new ItemRequirement("Raw beef", ItemID.RAW_BEEF);
		rawBeef.addAlternates(ItemID.ENCHANTED_BEEF);
		rawChicken = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN);
		rawChicken.addAlternates(ItemID.ENCHANTED_CHICKEN);

		rawRatHighlighted = new ItemRequirement("Raw rat meat", ItemID.RAW_RAT_MEAT);
		rawRatHighlighted.setHighlightInInventory(true);
		rawBearHighlighted = new ItemRequirement("Raw bear meat", ItemID.RAW_BEAR_MEAT);
		rawBearHighlighted.setHighlightInInventory(true);
		rawBeefHighlighted = new ItemRequirement("Raw beef", ItemID.RAW_BEEF);
		rawBeefHighlighted.setHighlightInInventory(true);
		rawChickenHighlighted = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN);
		rawChickenHighlighted.setHighlightInInventory(true);

		enchantedBear = new ItemRequirement("Enchanted bear", ItemID.ENCHANTED_BEAR);
		enchantedBeef = new ItemRequirement("Enchanted beef", ItemID.ENCHANTED_BEEF);
		enchantedChicken = new ItemRequirement("Enchanted chicken", ItemID.ENCHANTED_CHICKEN);
		enchantedRat = new ItemRequirement("Enchanted rat", ItemID.ENCHANTED_RAT);
	}

	public void loadZones()
	{
		sanfewRoom = new Zone(new WorldPoint(2893, 3423, 1), new WorldPoint(2903, 3433, 1));
		dungeon = new Zone(new WorldPoint(2816, 9668, 0), new WorldPoint(2973, 9855, 0));
	}

	public void setupConditions()
	{
		inSanfewRoom = new ZoneRequirement(sanfewRoom);
		inDungeon = new ZoneRequirement(dungeon);
	}

	public void setupSteps()
	{
		talkToKaqemeex = new NpcStep(this, NpcID.KAQEMEEX, new WorldPoint(2925, 3486, 0), "Talk to Kaqemeex in the Druid Circle in Taverley.");
		talkToKaqemeex.addDialogSteps("I'm in search of a quest.", "Okay, I will try and help.");
		goUpToSanfew = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2899, 3429, 0), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew.addDialogStep("I've been sent to help purify the Varrock stone circle.");
		talkToSanfew.addSubSteps(goUpToSanfew);

		enterDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
			"Enter Taverley Dungeon south of Taverley.", rawBear, rawBeef, rawChicken, rawRat);
		enterDungeon.addDialogStep("Ok, I'll do that then.");
		useRatOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the rat meat on the cauldron. To enter the room, spam-click the gate to get in.", rawRatHighlighted);
		useRatOnCauldron.addIcon(ItemID.RAW_RAT_MEAT);
		useBeefOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the beef meat on the cauldron. To enter the room, spam-click the gate to get in.", rawBeefHighlighted);
		useBeefOnCauldron.addIcon(ItemID.RAW_BEEF);
		useBearOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the bear meat on the cauldron. To enter the room, spam-click the gate to get in.", rawBearHighlighted);
		useBearOnCauldron.addIcon(ItemID.RAW_BEAR_MEAT);
		useChickenOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the chicken meat on the cauldron. To enter the room, spam-click the gate to get in.", rawChickenHighlighted);
		useChickenOnCauldron.addIcon(ItemID.RAW_CHICKEN);
		enchantMeats = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the four meats on the cauldron. To enter the room, spam-click the gate to get in.");
		enchantMeats.addSubSteps(useRatOnCauldron, useChickenOnCauldron, useBeefOnCauldron, useBearOnCauldron);

		goUpToSanfewWithMeat = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2899, 3429, 0),
			"Bring the enchanted meats to Sanfew upstairs in the Taverley herblore store.", enchantedBear, enchantedBeef, enchantedChicken, enchantedRat);
		talkToSanfewWithMeat = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1),
			"Bring the enchanted meats to Sanfew upstairs in the Taverley herblore store.", enchantedBear, enchantedBeef, enchantedChicken, enchantedRat);
		talkToSanfewWithMeat.addSubSteps(goUpToSanfewWithMeat);
		talkToKaqemeexToFinish = new NpcStep(this, NpcID.KAQEMEEX, new WorldPoint(2925, 3486, 0), "Return to Kaqemeex in the Druid Circle to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(rawBear, rawBeef, rawChicken, rawRat);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.HERBLORE, 250));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Herblore Skill"));
	}


	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Helping the druids",
			Arrays.asList(talkToKaqemeex, talkToSanfew, enterDungeon, enchantMeats, talkToSanfewWithMeat, talkToKaqemeexToFinish),
				rawBear, rawBeef, rawChicken, rawRat));

		return allSteps;
	}
}
