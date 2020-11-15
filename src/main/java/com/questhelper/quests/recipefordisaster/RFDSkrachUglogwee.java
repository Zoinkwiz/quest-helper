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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE
)
public class RFDSkrachUglogwee extends BasicQuestHelper
{
	ItemRequirement rawJubbly, cookedJubbly, axeHighlighted, ironSpit, log, tinderbox, pickaxe, ogreBellows, ballOfWool, ogreBowAndArrows,
		ogreBow, ogreArrows, chompy, chompySpitted, ogreBellowsFilled, toad, toadReady, rock, cookedJubblyHighlighted;

	ConditionForStep inDiningRoom, hasSpitChompy, hasToad, hasRock, hasBalloonToad, hasRawJubbly, hasCookedJubbly, jubblyNearby,
		jubblyCarcassNearby, hasFilledBellows, rawJubblyOnFloor;

	QuestStep enterDiningRoom, inspectSkrach, talkToRantz, talkToRantzOnCoast, useAxeOnTree, useAxeOnTreeAgain, talkToRantzOnCoastAgain,
		useSpitOnChompy, lightFire, talkToRantzAfterReturn, getToad, getRock, useBellowOnToadInInv, dropBalloonToad, killJubbly, lootJubbly,
		fillUpBellows, cookJubbly, enterDiningRoomAgain, useJubblyOnSkrach, pickUpRawJubbly;

	Zone diningRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectSkrach = new ConditionalStep(this, enterDiningRoom);
		goInspectSkrach.addStep(inDiningRoom, inspectSkrach);
		steps.put(0, goInspectSkrach);
		steps.put(10, talkToRantz);
		steps.put(20, talkToRantz);

		steps.put(30, talkToRantzOnCoast);
		steps.put(40, talkToRantzOnCoast);

		steps.put(50, useAxeOnTree);
		steps.put(60, useAxeOnTreeAgain);

		steps.put(70, talkToRantzOnCoastAgain);

		ConditionalStep getChildrenToKaramja = new ConditionalStep(this, useSpitOnChompy);
		getChildrenToKaramja.addStep(hasSpitChompy, lightFire);

		steps.put(80, getChildrenToKaramja);

		steps.put(90, talkToRantzAfterReturn);
		steps.put(100, talkToRantzAfterReturn);

		ConditionalStep getJubbly = new ConditionalStep(this, fillUpBellows);
		getJubbly.addStep(hasRawJubbly, cookJubbly);
		getJubbly.addStep(rawJubblyOnFloor, pickUpRawJubbly);
		getJubbly.addStep(jubblyCarcassNearby, lootJubbly);
		getJubbly.addStep(jubblyNearby, killJubbly);
		getJubbly.addStep(hasBalloonToad, dropBalloonToad);
		getJubbly.addStep(new Conditions(hasToad, hasRock, hasFilledBellows), useBellowOnToadInInv);
		getJubbly.addStep(new Conditions(hasToad, hasFilledBellows), getRock);
		getJubbly.addStep(hasFilledBellows, getToad);
		steps.put(110, getJubbly);
		steps.put(120, getJubbly);
		steps.put(130, getJubbly);
		steps.put(140, getJubbly);
		steps.put(150, getJubbly);

		ConditionalStep saveSkrach = new ConditionalStep(this, enterDiningRoomAgain);
		saveSkrach.addStep(inDiningRoom, useJubblyOnSkrach);
		steps.put(160, saveSkrach);

		return steps;
	}

	public void setupRequirements()
	{
		rawJubbly = new ItemRequirement("Raw jubbly", ItemID.RAW_JUBBLY);
		rawJubbly.setHighlightInInventory(true);
		cookedJubbly = new ItemRequirement("Cooked jubbly", ItemID.COOKED_JUBBLY);
		cookedJubblyHighlighted = new ItemRequirement("Cooked jubbly", ItemID.COOKED_JUBBLY);
		cookedJubblyHighlighted.setHighlightInInventory(true);
		axeHighlighted = new ItemRequirement("Any axe", ItemCollections.getAxes());
		axeHighlighted.setHighlightInInventory(true);
		ironSpit = new ItemRequirement("Iron spit", ItemID.IRON_SPIT);
		ironSpit.setHighlightInInventory(true);
		log = new ItemRequirement("Any log to burn", ItemID.LOGS);
		log.addAlternates(ItemID.OAK_LOGS, ItemID.WILLOW_LOGS, ItemID.MAPLE_LOGS, ItemID.YEW_LOGS, ItemID.TEAK_LOGS,
			ItemID.MAHOGANY_LOGS, ItemID.ARCTIC_PINE_LOGS, ItemID.MAGIC_LOGS, ItemID.REDWOOD_LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		ogreBellows = new ItemRequirement("Ogre bellows", ItemID.OGRE_BELLOWS);
		ogreBellows.addAlternates(ItemID.OGRE_BELLOWS_1, ItemID.OGRE_BELLOWS_2, ItemID.OGRE_BELLOWS_3);
		ogreBellowsFilled = new ItemRequirement("Ogre bellows", ItemID.OGRE_BELLOWS_1);
		ogreBellowsFilled.addAlternates(ItemID.OGRE_BELLOWS_2, ItemID.OGRE_BELLOWS_3);
		ogreBellowsFilled.setHighlightInInventory(true);
		ballOfWool = new ItemRequirement("Balls of wool", ItemID.BALL_OF_WOOL);
		ogreBow = new ItemRequirement("Ogre bow", ItemID.OGRE_BOW);
		ogreBow.addAlternates(ItemID.COMP_OGRE_BOW);
		ogreArrows = new ItemRequirement("Ogre arrow", ItemID.OGRE_ARROW);
		ogreArrows.addAlternates(ItemID.BRONZE_BRUTAL, ItemID.IRON_BRUTAL, ItemID.STEEL_BRUTAL, ItemID.BLACK_BRUTAL,
			ItemID.MITHRIL_BRUTAL, ItemID.ADAMANT_BRUTAL, ItemID.RUNE_BRUTAL);
		ogreBowAndArrows = new ItemRequirements("Ogre bow + ogre arrows", ogreBow, ogreArrows);

		chompy = new ItemRequirement("Raw chompy", ItemID.RAW_CHOMPY);
		chompy.setHighlightInInventory(true);
		chompySpitted = new ItemRequirement("Skewered chompy", ItemID.SKEWERED_CHOMPY);
		chompySpitted.setHighlightInInventory(true);

		toad = new ItemRequirement("Bloated toad", ItemID.BLOATED_TOAD);
		toad.setHighlightInInventory(true);
		toadReady = new ItemRequirement("Balloon toad", ItemID.BALLOON_TOAD);

		rock = new ItemRequirement("Rock", ItemID.ROCK_1480);
		rock.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneCondition(diningRoom);
		hasSpitChompy = new ItemRequirementCondition(chompySpitted);
		hasToad = new ItemRequirementCondition(toad);
		hasBalloonToad = new Conditions(true, new ItemRequirementCondition(toadReady));
		hasRawJubbly = new ItemRequirementCondition(rawJubbly);
		hasCookedJubbly = new ItemRequirementCondition(cookedJubbly);
		hasRock = new ItemRequirementCondition(rock);
		jubblyNearby = new NpcCondition(NpcID.JUBBLY_BIRD);
		jubblyCarcassNearby = new NpcCondition(NpcID.JUBBLY_BIRD_4864);
		hasFilledBellows = new ItemRequirementCondition(ogreBellowsFilled);
		rawJubblyOnFloor = new ItemCondition(rawJubbly);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0), "Go inspect Skrach Uglogwee in Lumbridge Castle.");
		inspectSkrach = new ObjectStep(this, ObjectID.SKRACH_UGLOGWEE_12343, new WorldPoint(1864, 5329, 0), "Inspect Skrach Uglogwee.");
		inspectSkrach.addDialogSteps("Yes, I'm sure I can get some Jubbly Chompy.", "Oh Ok then, I guess I'll talk to Rantz.");
		inspectSkrach.addSubSteps(enterDiningRoom);

		talkToRantz = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2630, 2984, 0), "Talk to Rantz in Feldip Hills.");
		talkToRantz.addDialogSteps("I'm trying to free Skrach, can you help?", "Ok, I'll do it.");
		talkToRantzOnCoast = new NpcStep(this, NpcID.RANTZ_4855, new WorldPoint(2649, 2964, 0), "Talk to Rantz on the east coast of Feldip Hills.");
		talkToRantzOnCoast.addDialogStep("Ok, here I am...I guess this is the watery place? What now?");
		useAxeOnTree = new ObjectStep(this, NullObjectID.NULL_12549, new WorldPoint(2655, 2963, 0), "Use an axe on the old tree near Rantz.", axeHighlighted);
		useAxeOnTree.addIcon(ItemID.RUNE_AXE);
		useAxeOnTreeAgain = new ObjectStep(this, NullObjectID.NULL_12549, new WorldPoint(2655, 2963, 0), "Use an axe on the old tree near Rantz again.", axeHighlighted);
		useAxeOnTreeAgain.addIcon(ItemID.RUNE_AXE);
		talkToRantzOnCoastAgain = new NpcStep(this, NpcID.RANTZ_4855, new WorldPoint(2649, 2964, 0), "Talk to Rantz again on the east coast of Feldip Hills.");
		talkToRantzOnCoastAgain.addDialogStep("Ok, the boat's ready, now tell me how to get a Jubbly?");
		useSpitOnChompy = new DetailedQuestStep(this, "Use an iron spit on a chompy.", ironSpit, chompy);
		lightFire = new DetailedQuestStep(this, new WorldPoint(2760, 3080, 0), "Light a fire on karamja's west coast. Afterwards, use your skewered chompy on it.", log, tinderbox, chompySpitted);
		talkToRantzAfterReturn = new NpcStep(this, NpcID.RANTZ_4855, new WorldPoint(2649, 2964, 0), "Travel back with Rantz's kids and talk to Rantz again.");
		talkToRantzAfterReturn.addDialogSteps("Yes please, I'll get a lift back with you.", "Ok, now tell me how to get Jubbly!");
		fillUpBellows = new ObjectStep(this, ObjectID.SWAMP_BUBBLES, new WorldPoint(2601, 2967, 0), "Fill some ogre bellows on some swamp bubbles.", ogreBellows);
		getToad = new NpcStep(this, NpcID.SWAMP_TOAD, new WorldPoint(2601, 2967, 0), "Blow up a toad with the bellows.", ogreBellowsFilled);
		getToad.addSubSteps(fillUpBellows);
		getRock = new ObjectStep(this, ObjectID.PILE_OF_ROCK_12564, new WorldPoint(2567, 2960, 0), "Mine a pile of rocks near the Feldip Hills Fairy Ring for a rock.", pickaxe);
		useBellowOnToadInInv = new DetailedQuestStep(this, "Use the bellows on your toad with a ball of wool in your inventory.", ogreBellowsFilled, toad, ballOfWool);
		dropBalloonToad = new DetailedQuestStep(this, new WorldPoint(2593, 2964, 0), "Drop the balloon toad near a swamp and wait for a Jubbly to arrive.", toadReady, ogreBowAndArrows);
		killJubbly = new NpcStep(this, NpcID.JUBBLY_BIRD, "Kill then pluck jubbly.", ogreBowAndArrows);
		pickUpRawJubbly = new ItemStep(this, "Pick up the raw jubbly.", rawJubbly);
		lootJubbly = new NpcStep(this, NpcID.JUBBLY_BIRD_4864, "Pluck the jubbly's carcass.");
		cookJubbly = new ObjectStep(this, NullObjectID.NULL_6895, new WorldPoint(2631, 2990, 0),"Cook the raw jubbly on Rantz's spit.", rawJubbly);
		cookJubbly.addIcon(ItemID.RAW_JUBBLY);

		enterDiningRoomAgain = new ObjectStep(this, ObjectID.DOOR_12348, new WorldPoint(3207, 3217, 0), "Go give the jubbly to Skrach Uglogwee to finish the quest.", cookedJubbly);
		useJubblyOnSkrach = new ObjectStep(this, ObjectID.SKRACH_UGLOGWEE_12343, new WorldPoint(1864, 5329, 0), "Give the jubbly to Skrach Uglogwee to finish the quest.", cookedJubblyHighlighted);
		useJubblyOnSkrach.addIcon(ItemID.COOKED_JUBBLY);
		useJubblyOnSkrach.addSubSteps(enterDiningRoomAgain);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(axeHighlighted, chompy, ironSpit, log, tinderbox, pickaxe, ogreBellows, ballOfWool, ogreBowAndArrows));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Jubbly (level 9)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(inspectSkrach))));
		allSteps.add(new PanelDetails("Help Rantz", new ArrayList<>(Arrays.asList(talkToRantz, talkToRantzOnCoast, useAxeOnTree, useAxeOnTreeAgain,
			talkToRantzOnCoastAgain, useSpitOnChompy, lightFire, talkToRantzAfterReturn)), axeHighlighted, log, tinderbox, chompy, ironSpit, ogreBowAndArrows, pickaxe, ogreBellowsFilled, ballOfWool));
		allSteps.add(new PanelDetails("Save Skrach", new ArrayList<>(Arrays.asList(getToad, getRock, useBellowOnToadInInv, dropBalloonToad, killJubbly, lootJubbly, pickUpRawJubbly, cookJubbly,
			useJubblyOnSkrach)), ogreBowAndArrows, pickaxe, ogreBellowsFilled, ballOfWool));

		return allSteps;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SKRACH_UGLOGWEE.getId()) >= 170 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
