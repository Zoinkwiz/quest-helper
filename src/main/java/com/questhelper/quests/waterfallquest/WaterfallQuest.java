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
package com.questhelper.quests.waterfallquest;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.VarbitCondition;
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
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WATERFALL_QUEST
)
public class WaterfallQuest extends BasicQuestHelper
{
	ItemRequirement rope, highlightRope, glarialsPebble, glarialsUrn, glarialsAmulet, unequippedAmulet, book, key, baxKey, airRunes, waterRunes, earthRunes, airRune, waterRune,
		earthRune, gamesNecklace, food;

	ConditionForStep inGnomeBasement, inGlarialTomb, inFalls, onHudonIsland, onDeadTreeIsland, onLedge, hasGlarialsPebble, hasGlarialsUrn, hasGlarialsAmulet,
		hasBook, inUpstairsInHouse, hasKey, hasBaxKey, inGolrieRoom, gotPebble, hasAirRunes, hasEarthRunes, hasWaterRunes, hasRope, inEndRoom, inEnd2;

	QuestStep talkToAlmera, boardRaft, talkToHudon, useRopeOnRock, useRopeOnTree, getInBarrel, goUpstairsHadley, searchBookcase,
		readBook, enterGnomeDungeon, searchGnomeCrate, enterGnomeDoor, talkToGolrie, usePebble, searchGlarialChest, searchGlarialCoffin,
		getFinalItems, boardRaftFinal, useRopeOnRockFinal, useRopeOnTreeFinal, enterFalls, searchFallsCrate, useKeyOnFallsDoor,
		useRunes, useAmuletOnStatue, useUrnOnChalice;

	ConditionalStep goGetPebble, getGlarialStuff;

	Zone gnomeBasement, glarialTomb, falls, endRoom, end2, hudonIsland, deadTreeIsland, ledge, upstairsInHouse, golrieRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToAlmera);

		ConditionalStep goTalkToHudon = new ConditionalStep(this, boardRaft);
		goTalkToHudon.addStep(onHudonIsland, talkToHudon);

		steps.put(1, goTalkToHudon);

		ConditionalStep goReadBook = new ConditionalStep(this, goUpstairsHadley);
		goReadBook.addStep(hasBook, readBook);
		goReadBook.addStep(inUpstairsInHouse, searchBookcase);
		goReadBook.addStep(onLedge, getInBarrel);
		goReadBook.addStep(onDeadTreeIsland, useRopeOnTree);
		goReadBook.addStep(onHudonIsland, useRopeOnRock);

		steps.put(2, goReadBook);

		goGetPebble = new ConditionalStep(this, enterGnomeDungeon);
		goGetPebble.addStep(inGolrieRoom, talkToGolrie);
		goGetPebble.addStep(new Conditions(inGnomeBasement, hasKey), enterGnomeDoor);
		goGetPebble.addStep(inGnomeBasement, searchGnomeCrate);
		goGetPebble.setLockingCondition(gotPebble);

		getGlarialStuff = new ConditionalStep(this, usePebble);
		getGlarialStuff.addStep(new Conditions(hasGlarialsAmulet, inGlarialTomb), searchGlarialCoffin);
		getGlarialStuff.addStep(inGlarialTomb, searchGlarialChest);
		getGlarialStuff.setLockingCondition(new Conditions(new Conditions(hasGlarialsAmulet, hasGlarialsUrn)));

		ConditionalStep puttingToRest = new ConditionalStep(this, getFinalItems);
		puttingToRest.addStep(inEnd2, useUrnOnChalice);
		puttingToRest.addStep(inEndRoom, useRunes);
		puttingToRest.addStep(new Conditions(inFalls, hasBaxKey), useKeyOnFallsDoor);
		puttingToRest.addStep(inFalls, searchFallsCrate);
		puttingToRest.addStep(onLedge, enterFalls);
		puttingToRest.addStep(onDeadTreeIsland, useRopeOnTreeFinal);
		puttingToRest.addStep(onHudonIsland, useRopeOnRockFinal);
		puttingToRest.addStep(new Conditions(hasGlarialsUrn, hasGlarialsAmulet, hasAirRunes, hasEarthRunes, hasWaterRunes, hasRope), boardRaftFinal);

		ConditionalStep finishingSteps = new ConditionalStep(this, goGetPebble);
		finishingSteps.addStep(new Conditions(hasGlarialsUrn, hasGlarialsAmulet), puttingToRest);
		finishingSteps.addStep(gotPebble, getGlarialStuff);

		steps.put(3, finishingSteps);
		steps.put(4, finishingSteps);
		steps.put(5, puttingToRest);
		steps.put(6, puttingToRest);
		steps.put(7, puttingToRest); // 7 didn't occur during testing
		steps.put(8, puttingToRest);

		return steps;
	}

	public void setupItemRequirements()
	{
		highlightRope = new ItemRequirement("Rope", ItemID.ROPE);
		highlightRope.setHighlightInInventory(true);
		rope = new ItemRequirement("Rope", ItemID.ROPE);

		book = new ItemRequirement("Book on baxtorian", ItemID.BOOK_ON_BAXTORIAN);
		book.setHighlightInInventory(true);
		glarialsPebble = new ItemRequirement("Glarial's pebble", ItemID.GLARIALS_PEBBLE);
		glarialsPebble.setHighlightInInventory(true);
		glarialsPebble.setTip("You can get another from Golrie under the Tree Gnome Village");
		glarialsUrn = new ItemRequirement("Glarial's urn", ItemID.GLARIALS_URN);
		glarialsUrn.setTip("You can get another from Glarial's tomb");
		glarialsAmulet = new ItemRequirement("Glarial's amulet", ItemID.GLARIALS_AMULET, 1, true);
		glarialsAmulet.setTip("You can get another from Glarial's tomb");
		unequippedAmulet = new ItemRequirement("Glarial's amulet", ItemID.GLARIALS_AMULET);
		key = new ItemRequirement("Key", ItemID.KEY_293);
		baxKey = new ItemRequirement("Key", ItemID.KEY_298);

		airRunes = new ItemRequirement("Air runes", ItemID.AIR_RUNE, 6);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		earthRunes = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 6);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		waterRunes = new ItemRequirement("Water runes", ItemID.WATER_RUNE, 6);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE);

		gamesNecklace = new ItemRequirement("Games necklace", ItemID.GAMES_NECKLACE8);
		food = new ItemRequirement("Food", -1, -1);
	}

	public void loadZones()
	{
		gnomeBasement = new Zone(new WorldPoint(2497, 9552, 0), new WorldPoint(2559, 9593, 0));
		glarialTomb = new Zone(new WorldPoint(2524, 9801, 0), new WorldPoint(2557, 9849, 0));
		golrieRoom = new Zone(new WorldPoint(2502, 9576, 0), new WorldPoint(2523, 9593, 0));
		hudonIsland = new Zone(new WorldPoint(2510, 3476, 0), new WorldPoint(2515, 3482, 0));
		deadTreeIsland = new Zone(new WorldPoint(2512, 3465, 0), new WorldPoint(2513, 3475, 0));
		ledge = new Zone(new WorldPoint(2510, 3462, 0), new WorldPoint(2513, 3464, 0));
		upstairsInHouse = new Zone(new WorldPoint(2516, 3424, 1), new WorldPoint(2520, 3431, 1));
		falls = new Zone(new WorldPoint(2556, 9861, 0), new WorldPoint(2595, 9920, 0));
		endRoom = new Zone(new WorldPoint(2561, 9902, 0), new WorldPoint(2570, 9917, 0));
		end2 = new Zone(new WorldPoint(2599, 9890, 0), new WorldPoint(2608, 9916, 0));
	}

	public void setupConditions()
	{
		onDeadTreeIsland = new ZoneCondition(deadTreeIsland);
		onHudonIsland = new ZoneCondition(hudonIsland);
		onLedge = new ZoneCondition(ledge);
		inUpstairsInHouse = new ZoneCondition(upstairsInHouse);
		hasBook = new ItemRequirementCondition(book);
		inGnomeBasement = new ZoneCondition(gnomeBasement);
		inGolrieRoom = new ZoneCondition(golrieRoom);
		inGlarialTomb = new ZoneCondition(glarialTomb);
		inFalls = new ZoneCondition(falls);
		inEndRoom = new ZoneCondition(endRoom);
		inEnd2 = new ZoneCondition(end2);
		hasGlarialsAmulet = new ItemRequirementCondition(glarialsAmulet);
		hasGlarialsPebble = new ItemRequirementCondition(glarialsPebble);
		hasGlarialsUrn = new ItemRequirementCondition(glarialsUrn);
		hasKey = new ItemRequirementCondition(key);
		hasBaxKey = new ItemRequirementCondition(baxKey);
		gotPebble = new VarbitCondition(9110, 1);
		hasEarthRunes = new ItemRequirementCondition(earthRunes);
		hasWaterRunes = new ItemRequirementCondition(waterRunes);
		hasAirRunes = new ItemRequirementCondition(airRunes);
		hasRope = new ItemRequirementCondition(highlightRope);
	}

	public void setupSteps()
	{
		talkToAlmera = new NpcStep(this, NpcID.ALMERA, new WorldPoint(2521, 3495, 0), "Talk to Almera on top of Baxtorian Falls.");
		talkToAlmera.addDialogStep("How can I help?");
		boardRaft = new ObjectStep(this, ObjectID.LOG_RAFT, new WorldPoint(2509, 3494, 0), "Board the log raft west of Almera.");
		talkToHudon = new NpcStep(this, NpcID.HUDON, new WorldPoint(2511, 3484, 0), "Talk to Hudon.");
		useRopeOnRock = new ObjectStep(this, ObjectID.ROCK, new WorldPoint(2512, 3468, 0), "Use a rope on the rock to the south", highlightRope);
		useRopeOnRock.addIcon(ItemID.ROPE);
		useRopeOnTree = new ObjectStep(this, ObjectID.DEAD_TREE_2020, new WorldPoint(2512, 3465, 0), "Use a rope on the dead tree.", highlightRope);
		useRopeOnTree.addIcon(ItemID.ROPE);
		getInBarrel = new ObjectStep(this, ObjectID.BARREL_2022, new WorldPoint(2512, 3463, 0), "Get in the barrel.");

		goUpstairsHadley = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2518, 3430, 0), "Go up the stairs in the house south of Almera's house.");
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_1989, new WorldPoint(2520, 3427, 1), "Search the south east bookcase.");
		readBook = new DetailedQuestStep(this, "Read the book.", book);

		enterGnomeDungeon = new ObjectStep(this, ObjectID.LADDER_5250, new WorldPoint(2533, 3155, 0), "Go to the centre of the Tree Gnome Village and go down the ladder at the entrance.");
		searchGnomeCrate = new ObjectStep(this, ObjectID.CRATE_1990, new WorldPoint(2548, 9565, 0), "Search the off-coloured crate in the east room.");
		enterGnomeDoor = new ObjectStep(this, ObjectID.DOOR_1991, new WorldPoint(2515, 9575, 0), "Go through the gate in the west room.", key);
		talkToGolrie = new NpcStep(this, NpcID.GOLRIE_4183, new WorldPoint(2514, 9580, 0), "Talk to Bolrie.");
		usePebble = new ObjectStep(this, ObjectID.GLARIALS_TOMBSTONE, new WorldPoint(2559, 3445, 0), "Bank everything besides the pebble and some food. After, go use Glarial's pebble to Glarial's Tombstone east of Baxtorian Falls.", glarialsPebble);
		usePebble.addIcon(ItemID.GLARIALS_PEBBLE);

		searchGlarialChest = new ObjectStep(this, ObjectID.CHEST_1994, new WorldPoint(2530, 9844, 0), "Search the chest in the western room.");
		searchGlarialCoffin = new ObjectStep(this, ObjectID.GLARIALS_TOMB, new WorldPoint(2542, 9812, 0), "Search Glarial's Tomb in the south room.");
		getFinalItems = new DetailedQuestStep(this, "Leave Glarial's Tomb and get 6 air, water and earth runes, a rope, glarial's amulet, glarial's urn, a rope, and some food.", airRunes, earthRunes, waterRunes, glarialsAmulet, glarialsUrn, rope);

		boardRaftFinal = new ObjectStep(this, ObjectID.LOG_RAFT, new WorldPoint(2509, 3494, 0), "Board the log raft west of Almera.");
		useRopeOnRockFinal = new ObjectStep(this, ObjectID.ROCK, new WorldPoint(2512, 3468, 0), "Use a rope on the rock to the south", highlightRope);
		useRopeOnRockFinal.addIcon(ItemID.ROPE);
		useRopeOnTreeFinal = new ObjectStep(this, ObjectID.DEAD_TREE_2020, new WorldPoint(2512, 3465, 0), "Use a rope on the dead tree.", highlightRope);
		useRopeOnTreeFinal.addIcon(ItemID.ROPE);
		enterFalls = new ObjectStep(this, ObjectID.DOOR_2010, new WorldPoint(2511, 3464, 0), "EQUIP Glarial's amulet, then enter the falls.", glarialsAmulet);

		searchFallsCrate = new ObjectStep(this, ObjectID.CRATE_1999, new WorldPoint(2589, 9888, 0), "Search the crate in the east room for a key.");
		useKeyOnFallsDoor = new ObjectStep(this, ObjectID.DOOR_2002, new WorldPoint(2566, 9901, 0), "Go through the doors from the west room.", baxKey);

		useRunes = new DetailedQuestStep(this, "Use 1 earth, water and air rune on each of the 6 pillars in the room. Afterwards, use Glarial's amulet on the statue of Glarial.", airRune, waterRune, earthRune);

		useAmuletOnStatue = new ObjectStep(this, ObjectID.STATUE_OF_GLARIAL, new WorldPoint(2603, 9915, 0), "Use Glarial's amulet on the Statue of Glarial", unequippedAmulet);
		useAmuletOnStatue.addIcon(ItemID.GLARIALS_AMULET);

		useUrnOnChalice = new ObjectStep(this, ObjectID.CHALICE, new WorldPoint(2604, 9911, 0), "Use Glarial's urn on the Chalice to finish the quest.", glarialsUrn);
		useUrnOnChalice.addIcon(ItemID.GLARIALS_URN);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(highlightRope);
		reqs.add(airRunes);
		reqs.add(earthRunes);
		reqs.add(waterRunes);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Able to survive enemies up to level 86 attacking you");
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(gamesNecklace);
		reqs.add(food);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToAlmera))));
		allSteps.add(new PanelDetails("Investigate", new ArrayList<>(Arrays.asList(boardRaft, talkToHudon, useRopeOnRock, useRopeOnTree, getInBarrel, goUpstairsHadley, searchBookcase, readBook)), rope));

		PanelDetails getPebblePanel = new PanelDetails("Get Glarial's Pebble", new ArrayList<>(Arrays.asList(enterGnomeDungeon, searchGnomeCrate, enterGnomeDoor, talkToGolrie)));
		getPebblePanel.setLockingStep(goGetPebble);
		allSteps.add(getPebblePanel);

		PanelDetails getGlarialStuffPanel = new PanelDetails("Loot Glarial's tomb", new ArrayList<>(Arrays.asList(usePebble, searchGlarialChest, searchGlarialCoffin)));
		getGlarialStuffPanel.setLockingStep(getGlarialStuff);

		allSteps.add(getGlarialStuffPanel);

		PanelDetails finishOffPanel = new PanelDetails("Put Glarial to rest", new ArrayList<>(Arrays.asList(getFinalItems, boardRaftFinal, useRopeOnRockFinal, useRopeOnTreeFinal, enterFalls, searchFallsCrate, useKeyOnFallsDoor, useRunes, useUrnOnChalice)), rope, airRunes, earthRunes, waterRunes, glarialsUrn, glarialsAmulet);
		allSteps.add(finishOffPanel);
		return allSteps;
	}
}
