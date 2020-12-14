/*
 * Copyright (c) 2020, Zantier
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
package com.questhelper.quests.deathplateau;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ChatMessageCondition;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
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

@QuestDescriptor(
	quest = QuestHelperQuest.DEATH_PLATEAU
)
public class DeathPlateau extends BasicQuestHelper
{
	ItemRequirement asgarnianAle, premadeBlurb, coins, bread, trout, ironBar, iou, iouHighlight, redStone, blueStone,
		yellowStone, pinkStone, greenStone;
	Zone castleUpstairs, barDownstairs, barUpstairs, haroldsRoom1, haroldsRoom2;
	ConditionForStep hasAsgarnianAle, inCastleUpstairs, inBarDownstairs, inBarUpstairs, inHaroldsRoom,
		givenHaroldBlurberry;
	QuestStep talkToDenulth, goToEohric1, talkToEohric1, goToHaroldStairs1, goToHaroldDoor1, talkToHarold1, goToEohric2,
		talkToEohric2, takeAsgarnianAle, goToHaroldStairs2, goToHaroldDoor2, talkToHarold2, giveHaroldBlurberry,
		gambleWithHarold;
	private QuestStep readIou;
	private ItemCondition isRedStoneDone;
	private ItemCondition isBlueStoneDone;
	private ItemCondition isYellowStoneDone;
	private ItemCondition isPinkStoneDone;
	private ItemCondition isGreenStoneDone;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToDenulth);

		ConditionalStep talkToEohricSteps1 = new ConditionalStep(this, goToEohric1);
		talkToEohricSteps1.addStep(inCastleUpstairs, talkToEohric1);
		steps.put(10, talkToEohricSteps1);

		ConditionalStep talkToHaroldSteps1 = new ConditionalStep(this, goToHaroldStairs1);
		talkToHaroldSteps1.addStep(inHaroldsRoom, talkToHarold1);
		talkToHaroldSteps1.addStep(inBarUpstairs, goToHaroldDoor1);
		steps.put(20, talkToHaroldSteps1);

		ConditionalStep talkToEohricSteps2 = new ConditionalStep(this, goToEohric2);
		talkToEohricSteps2.addStep(inCastleUpstairs, talkToEohric2);
		steps.put(30, talkToEohricSteps2);

		ConditionalStep talkToHaroldSteps2 = new ConditionalStep(this, takeAsgarnianAle);
		talkToHaroldSteps2.addStep(new Conditions(hasAsgarnianAle, inHaroldsRoom), talkToHarold2);
		talkToHaroldSteps2.addStep(new Conditions(hasAsgarnianAle, inBarUpstairs), goToHaroldDoor2);
		talkToHaroldSteps2.addStep(new Conditions(hasAsgarnianAle), goToHaroldStairs2);
		steps.put(40, talkToHaroldSteps2);

		ConditionalStep gambleSteps = new ConditionalStep(this, giveHaroldBlurberry);
		gambleSteps.addStep(givenHaroldBlurberry, gambleWithHarold);
		steps.put(50, talkToHaroldSteps2);

		steps.put(55, readIou);

		//steps.put(60, blahSteps);

		return steps;
	}

	public void setupItemRequirements()
	{
		asgarnianAle = new ItemRequirement("Asgarnian ale", ItemID.ASGARNIAN_ALE);
		premadeBlurb = new ItemRequirement("Premade blurb' sp. (or a Blurberry special, or 500 coins to gamble with)", ItemID.PREMADE_BLURB_SP);
		premadeBlurb.addAlternates(ItemID.BLURBERRY_SPECIAL);
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 60);
		bread = new ItemRequirement("Bread", ItemID.BREAD, 10);
		trout = new ItemRequirement("Trout", ItemID.TROUT, 10);
		ironBar = new ItemRequirement("Iron bar", ItemID.IRON_BAR);
		iou = new ItemRequirement("IOU", ItemID.IOU);
		iouHighlight = new ItemRequirement("IOU", ItemID.IOU);
		iouHighlight.setHighlightInInventory(true);
		redStone = new ItemRequirement("Stone ball", ItemID.STONE_BALL);
		redStone.setHighlightInInventory(true);
		blueStone = new ItemRequirement("Stone ball", ItemID.STONE_BALL_3110);
		blueStone.setHighlightInInventory(true);
		yellowStone = new ItemRequirement("Stone ball", ItemID.STONE_BALL_3111);
		yellowStone.setHighlightInInventory(true);
		pinkStone = new ItemRequirement("Stone ball", ItemID.STONE_BALL_3112);
		pinkStone.setHighlightInInventory(true);
		greenStone = new ItemRequirement("Stone ball", ItemID.STONE_BALL_3113);
		greenStone.setHighlightInInventory(true);
	}

	public void setupZones()
	{
		castleUpstairs = new Zone(new WorldPoint(2891, 3556, 1), new WorldPoint(2906, 3571, 1));
		barDownstairs = new Zone(new WorldPoint(2905, 3536, 0), new WorldPoint(2915, 3543, 0));
		barUpstairs = new Zone(new WorldPoint(2905, 3536, 1), new WorldPoint(2915, 3544, 1));
		haroldsRoom1 = new Zone(new WorldPoint(2905, 3536, 1), new WorldPoint(2906, 3542, 1));
		haroldsRoom2 = new Zone(new WorldPoint(2907, 3542, 1), new WorldPoint(2907, 3542, 1));
	}

	public void setupConditions()
	{
		hasAsgarnianAle = new ItemRequirementCondition(asgarnianAle);
		inCastleUpstairs = new ZoneCondition(castleUpstairs);
		inBarDownstairs = new ZoneCondition(barDownstairs);
		inBarUpstairs = new ZoneCondition(barUpstairs);
		inHaroldsRoom = new ZoneCondition(haroldsRoom1, haroldsRoom2);
		givenHaroldBlurberry = new ChatMessageCondition("You give Harold a Blurberry Special.");
		isRedStoneDone = new ItemCondition(redStone, new WorldPoint(2894, 3562, 0));
		isBlueStoneDone = new ItemCondition(redStone, new WorldPoint(2894, 3562, 0));
		isYellowStoneDone = new ItemCondition(redStone, new WorldPoint(2894, 3562, 0));
		isPinkStoneDone = new ItemCondition(redStone, new WorldPoint(2894, 3562, 0));
		isGreenStoneDone = new ItemCondition(redStone, new WorldPoint(2894, 3562, 0));
	}

	public void setupSteps()
	{
		talkToDenulth = new NpcStep(this, NpcID.DENULTH, new WorldPoint(2896, 3529, 0), "Talk to Denulth in Burthorpe. You can get there with the Burthorpe minigame teleport or a games necklace.");
		talkToDenulth.addDialogStep(1, "Do you have any quests for me?");
		talkToDenulth.addDialogStep(1, "Yes.");

		goToEohric1 = new ObjectStep(this, ObjectID.STAIRS_4626, new WorldPoint(2898, 3567, 0), "Ask Eohric, upstairs in Burthorpe castle, about the guard that was on duty last night.");
		talkToEohric1 = new NpcStep(this, NpcID.EOHRIC, new WorldPoint(2900, 3566, 1), "Ask Eohric, upstairs in Burthorpe castle, about the guard that was on duty last night.");
		talkToEohric1.addDialogStep(1, "I'm looking for the guard that was on last night.");
		talkToEohric1.addSubSteps(goToEohric1);

		goToHaroldStairs1 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2915, 3540, 0), "Talk to Harold, upstairs in the bar in Burthorpe.");
		goToHaroldDoor1 = new ObjectStep(this, ObjectID.DOOR_3747, new WorldPoint(2906, 3543, 1), "Talk to Harold, upstairs in the bar in Burthorpe.");
		talkToHarold1 = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Talk to Harold, upstairs in the bar in Burthorpe.");
		talkToHarold1.addDialogStep(1, "You're the guard that was on duty last night?");
		talkToHarold1.addSubSteps(goToHaroldStairs1, goToHaroldDoor1);

		goToEohric2 = new ObjectStep(this, ObjectID.STAIRS_4626, new WorldPoint(2898, 3567, 0), "Talk to Eohric.");
		talkToEohric2 = new NpcStep(this, NpcID.EOHRIC, new WorldPoint(2900, 3566, 1), "Talk to Eohric.");
		talkToEohric2.addSubSteps(goToEohric2);

		takeAsgarnianAle = new DetailedQuestStep(this, new WorldPoint(2906, 3538, 0), "Take an Asgarnian ale from the bar.", coins, asgarnianAle, premadeBlurb);

		goToHaroldStairs2 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2915, 3540, 0), "Talk to Harold.", coins, asgarnianAle, premadeBlurb);
		goToHaroldDoor2 = new ObjectStep(this, ObjectID.DOOR_3747, new WorldPoint(2906, 3543, 1), "Talk to Harold.", coins, asgarnianAle, premadeBlurb);
		talkToHarold2 = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Talk to Harold.", coins, asgarnianAle, premadeBlurb);
		talkToHarold2.addSubSteps(goToHaroldStairs2, goToHaroldDoor2);

		giveHaroldBlurberry = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Talk to Harold to give him a Blurberry Special.", coins, premadeBlurb);
		giveHaroldBlurberry.addDialogStep(3, "Can I buy you a drink?");

		gambleWithHarold = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Gamble with Harold (101 coins to immediately bankrupt him).", coins);
		gambleWithHarold.addDialogStep(2, "Would you like to gamble?");

		readIou = new DetailedQuestStep(this, "Read the IOU, and then keep the IOU for the end of the quest.", iouHighlight);

		DetailedQuestStep step = new DetailedQuestStep(this, new WorldPoint(2895, 3563, 0), "test");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Collections.singletonList(coins));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>();
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("All", new ArrayList<>(Arrays.asList(talkToDenulth, talkToEohric1, talkToHarold1, talkToEohric2, takeAsgarnianAle, talkToHarold2)), coins));
		return allSteps;
	}
}
