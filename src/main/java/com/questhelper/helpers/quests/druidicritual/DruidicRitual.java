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
package com.questhelper.helpers.quests.druidicritual;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

/**
 * The quest guide for the "Druidic Ritual" OSRS quest
 * <p>
 * <a href="https://oldschool.runescape.wiki/w/Druidic_Ritual">The OSRS wiki guide</a> was referenced for this guide
 */
public class DruidicRitual extends BasicQuestHelper
{
	// Required items
	ItemRequirement rawRat;
	ItemRequirement rawBear;
	ItemRequirement rawBeef;
	ItemRequirement rawChicken;

	// Mid-quest item requirements
	ItemRequirement rawRatHighlighted;
	ItemRequirement rawBearHighlighted;
	ItemRequirement rawBeefHighlighted;
	ItemRequirement rawChickenHighlighted;
	ItemRequirement enchantedBear;
	ItemRequirement enchantedBeef;
	ItemRequirement enchantedChicken;
	ItemRequirement enchantedRat;

	// Zones
	Zone dungeon;
	Zone cauldronRoom;
	Zone sanfewRoom;

	// Miscellaneous requirements
	ZoneRequirement inDungeon;
	ZoneRequirement inCauldronRoom;
	ZoneRequirement inSanfewRoom;

	// Steps
	NpcStep talkToKaqemeex;
	ObjectStep goUpToSanfew;
	NpcStep talkToSanfew;
	ObjectStep enterDungeon;
	ObjectStep enterCauldronRoom;
	ObjectStep enchantMeats;
	ObjectStep useRatOnCauldron;
	ObjectStep useBeefOnCauldron;
	ObjectStep useBearOnCauldron;
	ObjectStep useChickenOnCauldron;
	ObjectStep goUpToSanfewWithMeat;
	NpcStep talkToSanfewWithMeat;
	NpcStep talkToKaqemeexToFinish;

	ObjectStep climbDownToEnterDungeon;

	@Override
	protected void setupZones()
	{
		sanfewRoom = new Zone(new WorldPoint(2893, 3423, 1), new WorldPoint(2903, 3433, 1));
		dungeon = new Zone(new WorldPoint(2816, 9668, 0), new WorldPoint(2973, 9855, 0));
		cauldronRoom = new Zone(new WorldPoint(2889, 9825, 0), new WorldPoint(2898, 9839, 0));
	}

	@Override
	protected void setupRequirements()
	{
		rawRat = new ItemRequirement("Raw rat meat", ItemID.RAW_RAT_MEAT);
		rawRat.setTooltip("Can be acquired from a giant rat slightly north of the Varrock south-east mine");
		rawRat.addAlternates(ItemID.ENCHANTED_RAT_MEAT);
		rawBear = new ItemRequirement("Raw bear meat", ItemID.RAW_BEAR_MEAT);
		rawBear.setTooltip("Can be acquired from a bear south-east of Varrock");
		rawBear.addAlternates(ItemID.ENCHANTED_BEAR_MEAT);
		rawBeef = new ItemRequirement("Raw beef", ItemID.RAW_BEEF);
		rawBeef.setTooltip("Can be acquired from a farm north of Lumbridge");
		rawBeef.addAlternates(ItemID.ENCHANTED_BEEF);
		rawChicken = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN);
		rawChicken.setTooltip("Can be acquired from a farm north of Lumbridge");
		rawChicken.addAlternates(ItemID.ENCHANTED_CHICKEN);

		rawRatHighlighted = rawRat.highlighted();
		rawBearHighlighted = rawBear.highlighted();
		rawBeefHighlighted = rawBeef.highlighted();
		rawChickenHighlighted = rawChicken.highlighted();

		enchantedBear = new ItemRequirement("Enchanted bear", ItemID.ENCHANTED_BEAR_MEAT);
		enchantedBeef = new ItemRequirement("Enchanted beef", ItemID.ENCHANTED_BEEF);
		enchantedChicken = new ItemRequirement("Enchanted chicken", ItemID.ENCHANTED_CHICKEN);
		enchantedRat = new ItemRequirement("Enchanted rat", ItemID.ENCHANTED_RAT_MEAT);

		inSanfewRoom = new ZoneRequirement(sanfewRoom);
		inDungeon = new ZoneRequirement(dungeon);
		inCauldronRoom = new ZoneRequirement(cauldronRoom);
	}

	public void setupSteps()
	{
		talkToKaqemeex = new NpcStep(this, NpcID.KAQEMEEX, new WorldPoint(2925, 3486, 0), "Talk to Kaqemeex in the Druids' Circle in Taverley.");
		talkToKaqemeex.addDialogStep("I'm in search of a quest.");
		talkToKaqemeex.addDialogStep("Yes.");
		goUpToSanfew = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2899, 3429, 0), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew.addDialogStep("I've been sent to help purify the Varrock stone circle.");
		talkToSanfew.addSubSteps(goUpToSanfew);

		enterDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
			"Enter Taverley Dungeon south of Taverley.", rawRat, rawBear, rawBeef, rawChicken);
		enterDungeon.addDialogStep("Ok, I'll do that then.");
		climbDownToEnterDungeon = new ObjectStep(this, ObjectID.SPIRALSTAIRSTOP, new WorldPoint(2898, 3428, 1), "Enter Taverley Dungeon south of Taverley.", rawRat, rawBear, rawBeef, rawChicken);
		enterDungeon.addSubSteps(climbDownToEnterDungeon);

		enterCauldronRoom = new ObjectStep(this, ObjectID.CAULDRONDOOR_L, new WorldPoint(2889, 9830, 0),
			"Spam-click the Prison door to enter the room with the cauldron in Taverley dungeon. Spam-clicking lets you skip the fight with the suits of armour standing nearby.", rawRat, rawBear, rawBeef, rawChicken);
		enterCauldronRoom.addAlternateObjects(ObjectID.CAULDRONDOOR);

		useRatOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the rat meat on the cauldron in Taverley dungeon.", rawRatHighlighted);
		useRatOnCauldron.addIcon(ItemID.RAW_RAT_MEAT);
		useBeefOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the beef meat on the cauldron in Taverley dungeon.", rawBeefHighlighted);
		useBeefOnCauldron.addIcon(ItemID.RAW_BEEF);
		useBearOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the bear meat on the cauldron in Taverley dungeon.", rawBearHighlighted);
		useBearOnCauldron.addIcon(ItemID.RAW_BEAR_MEAT);
		useChickenOnCauldron = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the chicken meat on the cauldron in Taverley dungeon.", rawChickenHighlighted);
		useChickenOnCauldron.addIcon(ItemID.RAW_CHICKEN);
		enchantMeats = new ObjectStep(this, ObjectID.CAULDRON_OF_THUNDER, new WorldPoint(2893, 9831, 0),
			"Use the four meats on the cauldron in Taverley dungeon.");
		enchantMeats.addSubSteps(useRatOnCauldron, useChickenOnCauldron, useBeefOnCauldron, useBearOnCauldron);

		goUpToSanfewWithMeat = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2899, 3429, 0),
			"Bring the enchanted meats to Sanfew upstairs in the Taverley herblore store.", enchantedRat, enchantedBear, enchantedBeef, enchantedChicken);
		talkToSanfewWithMeat = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1),
			"Bring the enchanted meats to Sanfew upstairs in the Taverley herblore store.", enchantedRat, enchantedBear, enchantedBeef, enchantedChicken);
		talkToSanfewWithMeat.addSubSteps(goUpToSanfewWithMeat);
		talkToKaqemeexToFinish = new NpcStep(this, NpcID.KAQEMEEX, new WorldPoint(2925, 3486, 0), "Return to Kaqemeex in the Druids' Circle to finish the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToKaqemeex);

		var goTalkToSanfew = new ConditionalStep(this, goUpToSanfew);
		goTalkToSanfew.addStep(inSanfewRoom, talkToSanfew);
		steps.put(1, goTalkToSanfew);

		var prepareMeats = new ConditionalStep(this, enterDungeon);
		prepareMeats.addStep(and(inSanfewRoom, enchantedRat, enchantedBear, enchantedBeef, enchantedChicken), talkToSanfewWithMeat);
		prepareMeats.addStep(and(enchantedRat, enchantedBear, enchantedBeef, enchantedChicken), goUpToSanfewWithMeat);
		prepareMeats.addStep(and(inCauldronRoom, enchantedRat, enchantedBear, enchantedBeef), useChickenOnCauldron);
		prepareMeats.addStep(and(inCauldronRoom, enchantedRat, enchantedBear), useBeefOnCauldron);
		prepareMeats.addStep(and(inCauldronRoom, enchantedRat), useBearOnCauldron);
		prepareMeats.addStep(inCauldronRoom, useRatOnCauldron);
		prepareMeats.addStep(inDungeon, enterCauldronRoom);
		prepareMeats.addStep(inSanfewRoom, climbDownToEnterDungeon);
		steps.put(2, prepareMeats);

		steps.put(3, talkToKaqemeexToFinish);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			rawRat,
			rawBear,
			rawBeef,
			rawChicken
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.HERBLORE, 250)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Herblore Skill")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Helping the druids", List.of(
			talkToKaqemeex,
			talkToSanfew,
			enterDungeon,
			enterCauldronRoom,
			enchantMeats,
			talkToSanfewWithMeat,
			talkToKaqemeexToFinish
		), List.of(
			rawRat,
			rawBear,
			rawBeef,
			rawChicken
		)));

		return sections;
	}
}
