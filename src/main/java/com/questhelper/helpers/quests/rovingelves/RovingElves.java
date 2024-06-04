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
package com.questhelper.helpers.quests.rovingelves;

import com.questhelper.collections.ItemCollections;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class RovingElves extends BasicQuestHelper
{
	//Items Required
	ItemRequirement glarialsPebble, pebbleHint, keyHint, key, spade, rope, seed, blessedSeed, highlightRope, blessedSeedHighlight;

	//Items Recommended
	ItemRequirement prayerPotions, food, elvenForestTeleport, baxTeleport, superCombatPotion, antipoison;

	Requirement inGlarialsTomb, onDeadTreeIsland, onLedge, onHudonIsland, inFalls, seedNearby, inThroneRoom;

	DetailedQuestStep talkToIslwyn, talkToEluned, enterGlarialsTombstone, killGuardian, pickUpSeed, returnSeedToEluned, boardRaft, useRopeOnRock, useRopeOnTree, enterFalls,
		searchFallsCrate, useKeyOnFallsDoor, plantSeed, returnToIslwyn;

	//Zones
	Zone glarialTomb, deadTreeIsland, ledge, hudonIsland, falls, throneRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToIslwyn);
		steps.put(1, talkToIslwyn);
		steps.put(2, talkToEluned);

		ConditionalStep getTheSeed = new ConditionalStep(this, enterGlarialsTombstone);
		getTheSeed.addStep(seed.alsoCheckBank(questBank), returnSeedToEluned);
		getTheSeed.addStep(seedNearby, pickUpSeed);
		getTheSeed.addStep(inGlarialsTomb, killGuardian);

		steps.put(3, getTheSeed);

		ConditionalStep plantingTheSeed = new ConditionalStep(this, boardRaft);
		plantingTheSeed.addStep(inThroneRoom, plantSeed);
		plantingTheSeed.addStep(new Conditions(inFalls, key), useKeyOnFallsDoor);
		plantingTheSeed.addStep(inFalls, searchFallsCrate);
		plantingTheSeed.addStep(onLedge, enterFalls);
		plantingTheSeed.addStep(onDeadTreeIsland, useRopeOnTree);
		plantingTheSeed.addStep(onHudonIsland, useRopeOnRock);

		steps.put(4, plantingTheSeed);

		steps.put(5, returnToIslwyn);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		seed = new ItemRequirement("Consecration seed", ItemID.CONSECRATION_SEED);
		seed.addAlternates(ItemID.CONSECRATION_SEED_4206);
		
		blessedSeed = new ItemRequirement("Consecration seed", ItemID.CONSECRATION_SEED_4206);
		blessedSeed.setTooltip("You can get another from Eluned");

		blessedSeedHighlight = new ItemRequirement("Consecration seed", ItemID.CONSECRATION_SEED_4206);
		blessedSeedHighlight.setTooltip("You can get another from Eluned");
		blessedSeedHighlight.setHighlightInInventory(true);

		glarialsPebble = new ItemRequirement("Glarial's pebble", ItemID.GLARIALS_PEBBLE).isNotConsumed();
		glarialsPebble.setTooltip("You can get another from Golrie under Tree Gnome Village");
		key = new ItemRequirement("Key", ItemID.KEY_298).isNotConsumed();
		key.setTooltip("You can get another from inside Baxtorian Falls");

		keyHint = new ItemRequirement("Key (obtainable in quest)", ItemID.KEY_293).isNotConsumed();

		pebbleHint = new ItemRequirement("Glarial's pebble (obtainable in quest)", ItemID.GLARIALS_PEBBLE).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		rope = new ItemRequirement("Rope", ItemID.ROPE).isNotConsumed();
		highlightRope = rope.highlighted();
		prayerPotions = new ItemRequirement("A few prayer potions", ItemID.PRAYER_POTION4);
		baxTeleport = new ItemRequirement("Teleport to Baxtorian Falls. Skills necklace (Fishing Guild [1]), Games necklace (Barbarian Outpost [2])", ItemCollections.SKILLS_NECKLACES);
		baxTeleport.addAlternates(ItemCollections.GAMES_NECKLACES);

		elvenForestTeleport =
			new ItemRequirement("Teleport near to Elven Forest. Iorwerth camp teleport, Charter Ship to Port Tyras", ItemID.IORWERTH_CAMP_TELEPORT);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		superCombatPotion = new ItemRequirement("Super combat potion", ItemCollections.SUPER_COMBAT_POTIONS);
		antipoison = new ItemRequirement("Antipoison potion", ItemCollections.ANTIPOISONS);
	}

	public void setupConditions()
	{
		onDeadTreeIsland = new ZoneRequirement(deadTreeIsland);
		onHudonIsland = new ZoneRequirement(hudonIsland);
		onLedge = new ZoneRequirement(ledge);
		inFalls = new ZoneRequirement(falls);
		inGlarialsTomb = new ZoneRequirement(glarialTomb);
		inThroneRoom = new ZoneRequirement(throneRoom);
		seedNearby = new ItemOnTileRequirement(seed);

		// 8374 0->1 when leaving?
	}

	public void loadZones()
	{
		glarialTomb = new Zone(new WorldPoint(2524, 9801, 0), new WorldPoint(2557, 9849, 0));
		hudonIsland = new Zone(new WorldPoint(2510, 3476, 0), new WorldPoint(2515, 3482, 0));
		deadTreeIsland = new Zone(new WorldPoint(2512, 3465, 0), new WorldPoint(2513, 3475, 0));
		ledge = new Zone(new WorldPoint(2510, 3462, 0), new WorldPoint(2513, 3464, 0));
		falls = new Zone(new WorldPoint(2556, 9861, 0), new WorldPoint(2595, 9920, 0));
		throneRoom = new Zone(new WorldPoint(2599, 9901, 0), new WorldPoint(2608, 9916, 0));
	}

	public void setupSteps()
	{
		talkToIslwyn = new NpcStep(this, NpcID.ISLWYN, new WorldPoint(2207, 3159, 0),
			"Talk to Islwyn in Isafdar. If he's not at the marked location, try hopping worlds to find him here.", antipoison);
		talkToIslwyn.addTeleport(elvenForestTeleport);
		talkToIslwyn.addDialogStep("Yes.");
		talkToEluned = new NpcStep(this, NpcID.ELUNED_8766, new WorldPoint(2207, 3159, 0), "Talk to Eluned.");
		enterGlarialsTombstone = new ObjectStep(this, ObjectID.GLARIALS_TOMBSTONE, new WorldPoint(2559, 3445, 0),
			"Bank everything besides the pebble, some potions, and some food. After, go use Glarial's pebble to Glarial's Tombstone east of Baxtorian Falls. Be prepared to fight a level 84 Moss Guardian bare-handed.",
			Collections.singletonList(glarialsPebble.highlighted()), Arrays.asList(food, prayerPotions, superCombatPotion, elvenForestTeleport));
		enterGlarialsTombstone.addIcon(ItemID.GLARIALS_PEBBLE);
		enterGlarialsTombstone.addTeleport(baxTeleport);

		killGuardian = new NpcStep(this, NpcID.MOSS_GUARDIAN, new WorldPoint(2515, 9844, 0), "Kill the Moss Guardian for a Consecration seed.");

		pickUpSeed = new ItemStep(this, "Pick up the consecration seed.", seed);

		returnSeedToEluned = new NpcStep(this, NpcID.ELUNED_8766, new WorldPoint(2207, 3159, 0), "Return the seed to Eluned.",
			Collections.singletonList(seed), Collections.singletonList(antipoison));
		returnSeedToEluned.addTeleport(elvenForestTeleport);
		boardRaft = new ObjectStep(this, ObjectID.LOG_RAFT, new WorldPoint(2509, 3494, 0), "Board the log raft on the top of Baxtorian Falls.", blessedSeed, rope, spade);
		boardRaft.addTeleport(baxTeleport);
		useRopeOnRock = new ObjectStep(this, ObjectID.ROCK, new WorldPoint(2512, 3468, 0), "Use a rope on the rock to the south.", highlightRope);
		useRopeOnRock.addIcon(ItemID.ROPE);
		useRopeOnTree = new ObjectStep(this, ObjectID.DEAD_TREE_2020, new WorldPoint(2512, 3465, 0), "Use a rope on the dead tree.", highlightRope);
		useRopeOnTree.addIcon(ItemID.ROPE);
		enterFalls = new ObjectStep(this, ObjectID.DOOR_2010, new WorldPoint(2511, 3464, 0), "Enter the falls.");

		searchFallsCrate = new ObjectStep(this, ObjectID.CRATE_1999, new WorldPoint(2589, 9888, 0), "Search the crate in the east room for a key.");
		useKeyOnFallsDoor = new ObjectStep(this, ObjectID.DOOR_2002, new WorldPoint(2566, 9901, 0), "Go through the doors from the west room.", key);

		plantSeed = new DetailedQuestStep(this, "Plant the consecrated seed anywhere in the room.", blessedSeedHighlight, spade);

		returnToIslwyn = new NpcStep(this, NpcID.ISLWYN, new WorldPoint(2207, 3159, 0), "Return to Islwyn in Isafdar to finish the quest.");
		returnToIslwyn.addTeleport(elvenForestTeleport);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, rope, pebbleHint, keyHint);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(antipoison, prayerPotions, food, superCombatPotion, baxTeleport.quantity(2), elvenForestTeleport.quantity(3));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Moss Guardian (level 84) without runes, weapons, or armour");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.REGICIDE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 56, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.STRENGTH, 10000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("A used Crystal Shield", ItemID.CRYSTAL_SHIELD, 1),
				new ItemReward("or Crystal Bow", ItemID.CRYSTAL_BOW, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToIslwyn, talkToEluned), null, Arrays.asList(antipoison, elvenForestTeleport, baxTeleport)));

		allSteps.add(new PanelDetails("Get the seed",
			Arrays.asList(enterGlarialsTombstone, killGuardian, pickUpSeed, returnSeedToEluned), Collections.singletonList(glarialsPebble),
			Arrays.asList(food, prayerPotions, baxTeleport, elvenForestTeleport, antipoison)));

		allSteps.add(new PanelDetails("Plant the seed",
			Arrays.asList(boardRaft, useRopeOnRock, useRopeOnTree, enterFalls, searchFallsCrate, useKeyOnFallsDoor,
				plantSeed, returnToIslwyn), Arrays.asList(spade, rope, blessedSeed), Arrays.asList(baxTeleport, elvenForestTeleport, antipoison)));
		return allSteps;
	}
}
