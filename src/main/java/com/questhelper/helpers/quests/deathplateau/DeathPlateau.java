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
package com.questhelper.helpers.quests.deathplateau;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.DEATH_PLATEAU
)
public class DeathPlateau extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement gamesNecklace;

	//Items Required
	ItemRequirement asgarnianAle, premadeBlurbOrCoins, coins, bread, trout, ironBar, iou, iouHighlight, redStone, blueStone,
		yellowStone, pinkStone, greenStone, certificate, climbingBoots, spikedBoots, secretMap, combination;

	Requirement inCastleDownstairs, inCastleUpstairs, inBarDownstairs, inBarUpstairs, inHaroldsRoom,
		givenHaroldBlurberry, isRedStoneDone, isBlueStoneDone, isYellowStoneDone, isPinkStoneDone,
		isGreenStoneDone, inSabaCave, isFarEnough, talkedToSaba, talkedToDunstan;

	QuestStep talkToDenulth1, goToEohric1, talkToEohric1, goToHaroldStairs1, goToHaroldDoor1, talkToHarold1,
		goToEohric2, talkToEohric2, takeAsgarnianAle, goToHaroldStairs2, goToHaroldDoor2, talkToHarold2,
		giveHaroldBlurberry, gambleWithHarold, readIou, placeRedStone, placeBlueStone, placeYellowStone,
		placePinkStone, placeGreenStone, placeStones, enterSabaCave, talkToSaba, leaveSabaCave,
		talkToTenzing1, talkToDunstan1, talkToDenulthForDunstan, talkToDunstan2, talkToTenzing2,
		goNorth, talkToDenulth3, goToHaroldStairs3, goToHaroldDoor3, talkToHarold3;

	//Zones
	Zone castleDownstairs, castleUpstairs, barDownstairs, barUpstairs, haroldsRoom1, haroldsRoom2, sabaCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, talkToDenulth1);

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
		talkToHaroldSteps2.addStep(new Conditions(asgarnianAle, inHaroldsRoom), talkToHarold2);
		talkToHaroldSteps2.addStep(new Conditions(asgarnianAle, inBarUpstairs), goToHaroldDoor2);
		talkToHaroldSteps2.addStep(new Conditions(asgarnianAle), goToHaroldStairs2);
		steps.put(40, talkToHaroldSteps2);

		ConditionalStep gambleSteps = new ConditionalStep(this, giveHaroldBlurberry);
		gambleSteps.addStep(givenHaroldBlurberry, gambleWithHarold);
		steps.put(50, gambleSteps);

		steps.put(55, readIou);

		ConditionalStep stoneSteps = new ConditionalStep(this, placeStones);
		stoneSteps.addStep(new Conditions(inCastleDownstairs, isRedStoneDone, isBlueStoneDone, isYellowStoneDone, isPinkStoneDone), placeGreenStone);
		stoneSteps.addStep(new Conditions(inCastleDownstairs, isRedStoneDone, isBlueStoneDone, isYellowStoneDone), placePinkStone);
		stoneSteps.addStep(new Conditions(inCastleDownstairs, isRedStoneDone, isBlueStoneDone), placeYellowStone);
		stoneSteps.addStep(new Conditions(inCastleDownstairs, isRedStoneDone), placeBlueStone);
		stoneSteps.addStep(new Conditions(inCastleDownstairs), placeRedStone);
		steps.put(60, stoneSteps);

		ConditionalStep finalSteps = new ConditionalStep(this, enterSabaCave);
		finalSteps.addStep(new Conditions(isFarEnough, combination), talkToDenulth3);
		finalSteps.addStep(new Conditions(isFarEnough, inHaroldsRoom), talkToHarold3);
		finalSteps.addStep(new Conditions(isFarEnough, inBarUpstairs), goToHaroldDoor3);
		finalSteps.addStep(new Conditions(isFarEnough), goToHaroldDoor3);
		finalSteps.addStep(new Conditions(secretMap), goNorth);
		finalSteps.addStep(new Conditions(spikedBoots.alsoCheckBank(questBank)), talkToTenzing2);
		finalSteps.addStep(new Conditions(climbingBoots, certificate.alsoCheckBank(questBank)), talkToDunstan2);
		finalSteps.addStep(new Conditions(climbingBoots, talkedToDunstan), talkToDenulthForDunstan);
		finalSteps.addStep(new Conditions(climbingBoots), talkToDunstan1);
		finalSteps.addStep(new Conditions(talkedToSaba, inSabaCave), leaveSabaCave);
		finalSteps.addStep(talkedToSaba, talkToTenzing1);
		finalSteps.addStep(inSabaCave, talkToSaba);
		steps.put(70, finalSteps);

		// 261 varp = 7, 262 = 7

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		asgarnianAle = new ItemRequirement("Asgarnian ale", ItemID.ASGARNIAN_ALE);

		ItemRequirement premadeBlurb = new ItemRequirement("Premade blurb' sp.", ItemID.PREMADE_BLURB_SP);
		premadeBlurb.addAlternates(ItemID.BLURBERRY_SPECIAL);
		ItemRequirement coins500 = new ItemRequirement("Coins", ItemCollections.COINS, 500);

		premadeBlurbOrCoins = new ItemRequirements(LogicType.OR,
			"Premade blurb' sp. (or a Blurberry special, or 500 coins to gamble with)",
			premadeBlurb, coins500);
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 60);
		bread = new ItemRequirement("Bread (UNNOTED)", ItemID.BREAD, 10);
		trout = new ItemRequirement("Trout (UNNOTED)", ItemID.TROUT, 10);
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
		certificate = new ItemRequirement("Certificate", ItemID.CERTIFICATE_3114);
		climbingBoots = new ItemRequirement("Climbing boots", ItemCollections.CLIMBING_BOOTS);
		spikedBoots = new ItemRequirement("Spiked boots", ItemID.SPIKED_BOOTS);
		secretMap = new ItemRequirement("Secret way map", ItemID.SECRET_WAY_MAP);
		combination = new ItemRequirement("Combination", ItemID.COMBINATION);
		combination.setTooltip("You can get another by talking to Harold upstairs in Burthorpe's Pub");
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
	}

	public void setupZones()
	{
		castleDownstairs = new Zone(new WorldPoint(2893, 3558, 0), new WorldPoint(2904, 3569, 0));
		castleUpstairs = new Zone(new WorldPoint(2891, 3556, 1), new WorldPoint(2906, 3571, 1));
		barDownstairs = new Zone(new WorldPoint(2905, 3536, 0), new WorldPoint(2915, 3543, 0));
		barUpstairs = new Zone(new WorldPoint(2905, 3536, 1), new WorldPoint(2915, 3544, 1));
		haroldsRoom1 = new Zone(new WorldPoint(2905, 3536, 1), new WorldPoint(2906, 3542, 1));
		haroldsRoom2 = new Zone(new WorldPoint(2907, 3542, 1), new WorldPoint(2907, 3542, 1));
		sabaCave = new Zone(new WorldPoint(2266, 4752, 0), new WorldPoint(2273, 4762, 0));
	}

	public void setupConditions()
	{
		inCastleDownstairs = new ZoneRequirement(castleDownstairs);
		inCastleUpstairs = new ZoneRequirement(castleUpstairs);
		inBarDownstairs = new ZoneRequirement(barDownstairs);
		inBarUpstairs = new ZoneRequirement(barUpstairs);
		inHaroldsRoom = new ZoneRequirement(haroldsRoom1, haroldsRoom2);
		givenHaroldBlurberry = new ChatMessageRequirement("You give Harold a Blurberry Special.");
		isRedStoneDone = new ItemOnTileRequirement(redStone, new WorldPoint(2894, 3563, 0));
		isBlueStoneDone = new ItemOnTileRequirement(blueStone, new WorldPoint(2894, 3562, 0));
		isYellowStoneDone = new ItemOnTileRequirement(yellowStone, new WorldPoint(2895, 3562, 0));
		isPinkStoneDone = new ItemOnTileRequirement(pinkStone, new WorldPoint(2895, 3563, 0));
		isGreenStoneDone = new ItemOnTileRequirement(greenStone, new WorldPoint(2895, 3564, 0));
		inSabaCave = new ZoneRequirement(sabaCave);
		isFarEnough = new ChatMessageRequirement("You should go and speak to Denulth.");
		talkedToSaba = new Conditions(true, LogicType.OR,
			new DialogRequirement("Before the trolls came there used to be a nettlesome"),
			new DialogRequirement("Have you got rid of those pesky trolls yet?")
		);
		talkedToDunstan = new Conditions(true, LogicType.OR,
			new DialogRequirement("My son has just turned 16"),
			new DialogRequirement("Have you managed to get my son")
		);
	}

	public void setupSteps()
	{
		talkToDenulth1 = new NpcStep(this, NpcID.DENULTH, new WorldPoint(2896, 3529, 0), "Talk to Denulth in Burthorpe. You can get there with the Burthorpe minigame teleport or a games necklace.");
		talkToDenulth1.addDialogStep(1, "Do you have any quests for me?");
		talkToDenulth1.addDialogStep(1, "Yes.");

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

		takeAsgarnianAle = new DetailedQuestStep(this, new WorldPoint(2906, 3538, 0), "Take an Asgarnian ale from the bar.", coins, asgarnianAle, premadeBlurbOrCoins);

		goToHaroldStairs2 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2915, 3540, 0), "Talk to Harold.", coins, asgarnianAle, premadeBlurbOrCoins);
		goToHaroldDoor2 = new ObjectStep(this, ObjectID.DOOR_3747, new WorldPoint(2906, 3543, 1), "Talk to Harold.", coins, asgarnianAle, premadeBlurbOrCoins);
		talkToHarold2 = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Talk to Harold.", coins, asgarnianAle, premadeBlurbOrCoins);
		talkToHarold2.addSubSteps(goToHaroldStairs2, goToHaroldDoor2);

		giveHaroldBlurberry = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Talk to Harold to give him a Blurberry Special.", coins, premadeBlurbOrCoins);
		giveHaroldBlurberry.addDialogStep(3, "Can I buy you a drink?");

		gambleWithHarold = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "Gamble with Harold (101 coins to immediately bankrupt him).", coins);
		gambleWithHarold.addDialogStep(2, "Would you like to gamble?");

		readIou = new DetailedQuestStep(this, "Read the IOU, and then keep the Combination for the end of the quest.", iouHighlight);

		placeRedStone = new ObjectStep(this, ObjectID.STONE_MECHANISM_3677, new WorldPoint(2894, 3563, 0), "Use the red stone on the mechanism.", redStone);
		placeBlueStone = new ObjectStep(this, ObjectID.STONE_MECHANISM, new WorldPoint(2894, 3562, 0), "Use the blue stone on the mechanism.", blueStone);
		placeYellowStone = new ObjectStep(this, ObjectID.STONE_MECHANISM, new WorldPoint(2895, 3562, 0), "Use the yellow stone on the mechanism.", yellowStone);
		placePinkStone = new ObjectStep(this, ObjectID.STONE_MECHANISM_3677, new WorldPoint(2895, 3563, 0), "Use the pink stone on the mechanism.", pinkStone);
		placeGreenStone = new ObjectStep(this, ObjectID.STONE_MECHANISM, new WorldPoint(2895, 3564, 0), "Use the green stone on the mechanism.", greenStone);
		placeStones = new DetailedQuestStep(this, new WorldPoint(2896, 3563, 0), "Go back to Burthorpe castle, and place the coloured stone balls in the correct spots on the mechanism.");
		placeStones.addSubSteps(placeRedStone, placeBlueStone, placeYellowStone, placePinkStone, placeGreenStone);

		enterSabaCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3735, new WorldPoint(2858, 3579, 0), "Enter the cave north west of Burthorpe.");

		talkToSaba = new NpcStep(this, NpcID.SABA, new WorldPoint(2270, 4757, 0), "Talk to Saba.");
		talkToSaba.addDialogStep("Do you know of another way up Death Plateau?");

		leaveSabaCave = new ObjectStep(this, ObjectID.CAVE_EXIT_3736, new WorldPoint(2269, 4751, 0), "Leave Saba's cave.");

		talkToTenzing1 = new NpcStep(this, NpcID.TENZING, new WorldPoint(2820, 3555, 0), "Go northwest of Burthorpe, then south at the fork, and talk to Tenzing.");
		talkToTenzing1.addDialogStep(1, "OK, I'll get those for you.");
		talkToTenzing1.addSubSteps(leaveSabaCave);

		talkToDenulthForDunstan = new NpcStep(this, NpcID.DENULTH, new WorldPoint(2896, 3529, 0), "Ask Denulth about Dunstan's son.");
		talkToDunstan1 = new NpcStep(this, NpcID.DUNSTAN, new WorldPoint(2920, 3573, 0), "Talk to Dunstan at the anvil in Burthorpe, then talk to Denulth, where you started the quest.", climbingBoots);
		talkToDunstan1.addSubSteps(talkToDenulthForDunstan);
		talkToDunstan2 = new NpcStep(this, NpcID.DUNSTAN, new WorldPoint(2920, 3573, 0), "Talk to Dunstan to exchange the climbing boots for spiked boots.", climbingBoots, certificate, ironBar);

		talkToTenzing2 = new NpcStep(this, NpcID.TENZING, new WorldPoint(2820, 3555, 0), "Bring the spiked boots, along with (unnoted) 10 bread and 10 trout to Tenzing.", spikedBoots, bread, trout);

		goNorth = new DetailedQuestStep(this, new WorldPoint(2865, 3609, 0), "Exit Tenzing's house through the north door, and go north, past the Death Plateau warning, and around to the east until you stop and say you've gone far enough.", secretMap);

		goToHaroldStairs3 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2915, 3540, 0), "If you lost the Combination, retrieve it from Harold.");
		goToHaroldDoor3 = new ObjectStep(this, ObjectID.DOOR_3747, new WorldPoint(2906, 3543, 1), "If you lost the Combination, retrieve it from Harold.");
		talkToHarold3 = new NpcStep(this, NpcID.HAROLD, new WorldPoint(2906, 3540, 1), "If you lost the Combination, retrieve it from Harold.");
		talkToDenulth3 = new NpcStep(this, NpcID.DENULTH, new WorldPoint(2896, 3529, 0), "Go back and talk to Denulth, giving him the secret map and Combination.", secretMap, combination);
		talkToDenulth3.addSubSteps(goToHaroldStairs3, goToHaroldDoor3, talkToHarold3);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins, premadeBlurbOrCoins, ironBar, bread, trout);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(gamesNecklace);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.ATTACK, 3000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A pair of Steel Claws", ItemID.STEEL_CLAWS, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("The ability to craft claws."),
				new UnlockReward("The ability to purchase and equip Climbing Boots."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("The equipment room", Arrays.asList(talkToDenulth1, talkToEohric1, talkToHarold1, talkToEohric2, takeAsgarnianAle, talkToHarold2, giveHaroldBlurberry, gambleWithHarold, readIou, placeStones), coins, premadeBlurbOrCoins));
		allSteps.add(new PanelDetails("Get spiked boots", Arrays.asList(enterSabaCave, talkToSaba, talkToTenzing1, talkToDunstan1, talkToDunstan2), ironBar));
		allSteps.add(new PanelDetails("The secret path", Arrays.asList(talkToTenzing2, goNorth, talkToDenulth3), bread, trout));
		return allSteps;
	}
}
