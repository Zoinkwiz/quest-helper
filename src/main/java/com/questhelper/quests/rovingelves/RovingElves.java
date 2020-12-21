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
package com.questhelper.quests.rovingelves;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.ROVING_ELVES
)
public class RovingElves extends BasicQuestHelper
{
	ItemRequirement glarialsPebble, pebbleHint, keyHint, key, spade, rope, prayerPotions, food, seed, blessedSeed, highlightRope, blessedSeedHighlight;

	ConditionForStep inGlarialsTomb, onDeadTreeIsland, onLedge, onHudonIsland, inFalls, seedNearby, hasSeed, hadBlessedSeed, hasKey, inThroneRoom;

	QuestStep talkToIslwyn, talkToEluned, enterGlarialsTombstone, killGuardian, pickUpSeed, returnSeedToEluned, boardRaft, useRopeOnRock, useRopeOnTree, enterFalls,
		searchFallsCrate, useKeyOnFallsDoor, plantSeed, returnToIslwyn;

	Zone glarialTomb, deadTreeIsland, ledge, hudonIsland, falls, throneRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToIslwyn);
		steps.put(1, talkToIslwyn);
		steps.put(2, talkToEluned);

		ConditionalStep getTheSeed = new ConditionalStep(this, enterGlarialsTombstone);
		getTheSeed.addStep(hasSeed, returnSeedToEluned);
		getTheSeed.addStep(seedNearby, pickUpSeed);
		getTheSeed.addStep(inGlarialsTomb, killGuardian);

		steps.put(3, getTheSeed);

		ConditionalStep plantingTheSeed = new ConditionalStep(this, boardRaft);
		plantingTheSeed.addStep(inThroneRoom, plantSeed);
		plantingTheSeed.addStep(new Conditions(inFalls, hasKey), useKeyOnFallsDoor);
		plantingTheSeed.addStep(inFalls, searchFallsCrate);
		plantingTheSeed.addStep(onLedge, enterFalls);
		plantingTheSeed.addStep(onDeadTreeIsland, useRopeOnTree);
		plantingTheSeed.addStep(onHudonIsland, useRopeOnRock);

		steps.put(4, plantingTheSeed);

		steps.put(5, returnToIslwyn);

		return steps;
	}

	public void setupItemRequirements()
	{
		seed = new ItemRequirement("Consecration seed", ItemID.CONSECRATION_SEED);
		blessedSeed = new ItemRequirement("Consecration seed", ItemID.CONSECRATION_SEED_4206);
		blessedSeed.setTip("You can get another from Eluned");

		blessedSeedHighlight = new ItemRequirement("Consecration seed", ItemID.CONSECRATION_SEED_4206);
		blessedSeedHighlight.setTip("You can get another from Eluned");
		blessedSeedHighlight.setHighlightInInventory(true);

		glarialsPebble = new ItemRequirement("Glarial's pebble", ItemID.GLARIALS_PEBBLE);
		glarialsPebble.setTip("You can get another from Golrie under Tree Gnome Village");
		key = new ItemRequirement("Key", ItemID.KEY_298);
		key.setTip("You can get another from inside Baxtorian Falls");

		keyHint = new ItemRequirement("Key (obtainable in quest)", ItemID.KEY_293);

		pebbleHint = new ItemRequirement("Glarial's pebble (obtainable in quest)", ItemID.GLARIALS_PEBBLE);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		highlightRope = new ItemRequirement("Rope", ItemID.ROPE);
		highlightRope.setHighlightInInventory(true);
		prayerPotions = new ItemRequirement("Prayer potions", ItemID.PRAYER_POTION4);
		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
	}

	public void setupConditions()
	{
		onDeadTreeIsland = new ZoneCondition(deadTreeIsland);
		onHudonIsland = new ZoneCondition(hudonIsland);
		onLedge = new ZoneCondition(ledge);
		inFalls = new ZoneCondition(falls);
		inGlarialsTomb = new ZoneCondition(glarialTomb);
		inThroneRoom = new ZoneCondition(throneRoom);
		hasSeed = new ItemRequirementCondition(seed);
		hadBlessedSeed = new Conditions(true, LogicType.OR, new ItemRequirementCondition(blessedSeed));
		seedNearby = new ItemCondition(seed);
		hasKey = new ItemRequirementCondition(key);

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
		talkToIslwyn = new NpcStep(this, NpcID.ISLWYN, new WorldPoint(2207, 3159, 0), "Talk to Islwyn in Isfadar. If he's not at the marked location, try hopping worlds to find him here.");
		talkToIslwyn.addDialogStep("Yes.");
		talkToEluned = new NpcStep(this, NpcID.ELUNED_8766, new WorldPoint(2207, 3159, 0), "Talk to Eluned.");
		enterGlarialsTombstone = new ObjectStep(this, ObjectID.GLARIALS_TOMBSTONE, new WorldPoint(2559, 3445, 0),
			"Bank everything besides the pebble, some potions, and some food. After, go use Glarial's pebble to Glarial's Tombstone east of Baxtorian Falls. Be prepared to fight a level 84 Moss Guardian bare-handed.",
			glarialsPebble);
		enterGlarialsTombstone.addIcon(ItemID.GLARIALS_PEBBLE);

		killGuardian = new NpcStep(this, NpcID.MOSS_GUARDIAN, new WorldPoint(2515, 9844, 0), "Kill the Moss Guardian for a Consecration seed.");

		pickUpSeed = new ItemStep(this, "Pick up the consecration seed.", seed);

		returnSeedToEluned = new NpcStep(this, NpcID.ELUNED_8766, new WorldPoint(2207, 3159, 0), "Return the seed to Eluned.", seed);

		boardRaft = new ObjectStep(this, ObjectID.LOG_RAFT, new WorldPoint(2509, 3494, 0), "Board the log raft on the top of Baxtorian Falls.", blessedSeed, rope, spade);
		useRopeOnRock = new ObjectStep(this, ObjectID.ROCK, new WorldPoint(2512, 3468, 0), "Use a rope on the rock to the south.", highlightRope);
		useRopeOnRock.addIcon(ItemID.ROPE);
		useRopeOnTree = new ObjectStep(this, ObjectID.DEAD_TREE_2020, new WorldPoint(2512, 3465, 0), "Use a rope on the dead tree.", highlightRope);
		useRopeOnTree.addIcon(ItemID.ROPE);
		enterFalls = new ObjectStep(this, ObjectID.DOOR_2010, new WorldPoint(2511, 3464, 0), "Enter the falls.");

		searchFallsCrate = new ObjectStep(this, ObjectID.CRATE_1999, new WorldPoint(2589, 9888, 0), "Search the crate in the east room for a key.");
		useKeyOnFallsDoor = new ObjectStep(this, ObjectID.DOOR_2002, new WorldPoint(2566, 9901, 0), "Go through the doors from the west room.", key);

		plantSeed = new DetailedQuestStep(this, "Plant the consecrated seed anywhere in the room.", blessedSeedHighlight, spade);

		returnToIslwyn = new NpcStep(this, NpcID.ISLWYN, new WorldPoint(2207, 3159, 0), "Return to Islwyn in Isfadar to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(spade, rope, pebbleHint, keyHint));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(prayerPotions, food));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Moss Guardian (level 84) whilst bare-handed"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToIslwyn, talkToEluned))));

		allSteps.add(new PanelDetails("Get the seed",
			new ArrayList<>(Arrays.asList(enterGlarialsTombstone, killGuardian, pickUpSeed, returnSeedToEluned)), glarialsPebble));

		allSteps.add(new PanelDetails("Plant the seed",
			new ArrayList<>(Arrays.asList(boardRaft, useRopeOnRock, useRopeOnTree, enterFalls, searchFallsCrate, useKeyOnFallsDoor, plantSeed, returnToIslwyn)), spade, rope, blessedSeed));
		return allSteps;
	}
}
