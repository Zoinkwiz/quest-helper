/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.witchshouse;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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
import net.runelite.api.gameval.VarPlayerID;

public class WitchsHouse extends BasicQuestHelper
{
	// Required items
	ItemRequirement cheese;
	ItemRequirement leatherGloves;
	ItemRequirement armourAndWeapon;

	// Mid-quest item requirements
	ItemRequirement houseKey;
	ItemRequirement magnet;
	ItemRequirement witchesDiary;
	ItemRequirement shedKey;
	ItemRequirement ball;

	// Zones
	Zone house;
	Zone upstairsHouse;
	Zone downstairsHouseEast;
	Zone downstairsHouseWest;
	Zone garden1;
	Zone garden2;
	Zone garden3;
	Zone shed;

	// Miscellaneous requirements
	Requirement inHouse;
	Requirement inUpstairsHouse;
	Requirement inDownstairsHouseWest;
	Requirement inDownstairsHouseEast;
	Requirement inDownstairsHouse;
	Requirement inHouseOrGarden;
	Requirement ratHasMagnet;
	Requirement inShed;
	Requirement experimentNearby;
	VarplayerRequirement hasReadDiary;

	// Steps
	NpcStep talkToBoy;

	ObjectStep getKey;

	ObjectStep enterHouse;
	ObjectStep goDownstairsFromTop;

	ObjectStep goDownstairs;

	ObjectStep enterGate;

	ObjectStep openCupboardAndLoot;
	ObjectStep openCupboardAndLoot2;

	ObjectStep goBackUpstairs;

	ObjectStep useCheeseOnHole;

	ItemStep readDiary;

	ObjectStep searchFountain;
	ObjectStep enterShed;
	ObjectStep enterShedWithoutKey;
	DetailedQuestStep grabBall;
	DetailedQuestStep pickupBall;
	NpcStep killWitchsExperiment;
	NpcStep returnToBoy;

	@Override
	protected void setupZones()
	{
		house = new Zone(new WorldPoint(2901, 3466, 0), new WorldPoint(2907, 3476, 0));
		upstairsHouse = new Zone(new WorldPoint(2900, 3466, 1), new WorldPoint(2907, 3476, 1));
		downstairsHouseWest = new Zone(new WorldPoint(2897, 9870, 0), new WorldPoint(2902, 9878, 0));
		downstairsHouseEast = new Zone(new WorldPoint(2903, 9870, 0), new WorldPoint(2909, 9878, 0));
		garden1 = new Zone(new WorldPoint(2900, 3459, 0), new WorldPoint(2933, 3465, 0));
		garden2 = new Zone(new WorldPoint(2908, 3466, 0), new WorldPoint(2933, 3467, 0));
		garden3 = new Zone(new WorldPoint(2908, 3467, 0), new WorldPoint(2912, 3475, 0));
		shed = new Zone(new WorldPoint(2934, 3459, 0), new WorldPoint(2937, 3467, 0));
	}

	@Override
	protected void setupRequirements()
	{
		cheese = new ItemRequirement("Cheese (multiple if you mess up)", ItemID.CHEESE);
		leatherGloves = new ItemRequirement("Leather gloves", ItemID.LEATHER_GLOVES, 1, true).isNotConsumed();
		leatherGloves.setHighlightInInventory(true);
		leatherGloves.canBeObtainedDuringQuest();
		houseKey = new ItemRequirement("Door key", ItemID.WITCHES_DOORKEY);
		magnet = new ItemRequirement("Magnet", ItemID.MAGNET);
		shedKey = new ItemRequirement("Key", ItemID.WITCHES_SHEDKEY);
		shedKey.setHighlightInInventory(true);
		ball = new ItemRequirement("Ball", ItemID.BALL);
		armourAndWeapon = new ItemRequirement("Combat gear and food for monsters up to level 53", -1, -1).isNotConsumed();
		armourAndWeapon.setDisplayItemId(BankSlotIcons.getCombatGear());

		inHouse = new ZoneRequirement(house);
		inUpstairsHouse = new ZoneRequirement(upstairsHouse);
		inDownstairsHouseWest = new ZoneRequirement(downstairsHouseWest);
		inDownstairsHouseEast = new ZoneRequirement(downstairsHouseEast);
		inDownstairsHouse = new ZoneRequirement(downstairsHouseEast, downstairsHouseWest);
		inHouseOrGarden = new ZoneRequirement(house, garden1, garden2, garden3);
		ratHasMagnet = new VarplayerRequirement(VarPlayerID.BALLQUEST, 3, Operation.GREATER_EQUAL);
		inShed = new ZoneRequirement(shed);
		experimentNearby = or(
			new NpcCondition(NpcID.SHAPESHIFTERGLOB),
			new NpcCondition(NpcID.SHAPESHIFTERSPIDER),
			new NpcCondition(NpcID.SHAPESHIFTERBEAR),
			new NpcCondition(NpcID.SHAPESHIFTERWOLF)
		);

		hasReadDiary = new VarplayerRequirement(VarPlayerID.BALLQUEST, 5, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		talkToBoy = new NpcStep(this, NpcID.BALLBOY, new WorldPoint(2928, 3456, 0), "Talk to the Boy in Taverley to start the quest.");
		talkToBoy.addDialogSteps("What's the matter?", "Ok, I'll see what I can do.", "Yes.");

		getKey = new ObjectStep(this, ObjectID.WITCHPOT, new WorldPoint(2900, 3474, 0), "Look under the potted plant just outside the witch's house for the house key.");

		enterHouse = new ObjectStep(this, ObjectID.WITCHHOUSEDOOR, new WorldPoint(2900, 3473, 0), "Enter the witch's house.", houseKey);
		goDownstairsFromTop = new ObjectStep(this, ObjectID.GRIM_WITCH_HOUSE_SPOOKYSTAIRSTOP, new WorldPoint(2907, 3471, 1), "Go back downstairs.");
		enterHouse.addSubSteps(goDownstairsFromTop);

		goDownstairs = new ObjectStep(this, ObjectID.GRIM_WITCH_LADDER_DOWN, new WorldPoint(2907, 3476, 0), "Go down the ladder to the basement.");

		enterGate = new ObjectStep(this, ObjectID.SHOCKGATER, new WorldPoint(2902, 9873, 0), "Go through the gate whilst wearing gloves. If you don't have gloves, search the nearby boxes until you get a pair.", leatherGloves);

		openCupboardAndLoot = new ObjectStep(this, ObjectID.MAGNETCBSHUT, new WorldPoint(2898, 9874, 0), "Open the cupboard and get a magnet from it");
		openCupboardAndLoot2 = new ObjectStep(this, ObjectID.MAGNETCBOPEN, new WorldPoint(2898, 9874, 0), "Open the cupboard and get a magnet from it");
		openCupboardAndLoot.addSubSteps(openCupboardAndLoot2);

		goBackUpstairs = new ObjectStep(this, ObjectID.GRIM_WITCH_LADDER_UP, new WorldPoint(2907, 9876, 0), "Climb back up the ladder.");
		useCheeseOnHole = new ObjectStep(this, ObjectID.WITCHMOUSEHOLE, new WorldPoint(2903, 3466, 0), "Use the cheese on the mouse hole in the south room, then use the magnet on the mouse which emerges.", cheese, magnet);

		witchesDiary = new ItemRequirement("Witch's diary", ItemID.WITCHES_DIARY);

		readDiary = new ItemStep(this, "Pick up the witch's diary from the table and read it to ensure the door stays unlocked. Check the \"Checkpoint\" section if you want to skip this step.", witchesDiary);

		searchFountain = new ObjectStep(this, ObjectID.WITCHFOUNTAIN, new WorldPoint(2910, 3471, 0), "Enter the garden and sneak around the perimeter to search the fountain. If the witch spots you, you'll be teleported outside.");
		enterShed = new ObjectStep(this, ObjectID.WITCHSHEDDOOR, new WorldPoint(2934, 3463, 0), "Use the shed key on the shed door to enter.", shedKey);
		// NOTE: Testing 2025-09-20, it seems like the door does _not_ stay unlocked. I'm not sure if this is a temporary bug, so I'm leaving this state checking as is, and just adding text that makes sure the user knows to get the shed key from the fountain.
		enterShedWithoutKey = new ObjectStep(this, ObjectID.WITCHSHEDDOOR, new WorldPoint(2934, 3463, 0), "Enter the shed. If the door doesn't open, you need to get the shed key from the fountain again.");
		enterShed.addSubSteps(enterShedWithoutKey);
		enterShed.addIcon(ItemID.WITCHES_SHEDKEY);

		grabBall = new DetailedQuestStep(this, new WorldPoint(2936, 3470, 0), "If an experiment hasn't spawned, attempt to pick up the ball once.", ball);
		killWitchsExperiment = new NpcStep(this, NpcID.SHAPESHIFTERGLOB, new WorldPoint(2935, 3463, 0), "Kill all four forms of the Witch's experiment (levels 19, 30, 42, and 53). You can safe spot the last two forms from the crate in the south of the room.");
		killWitchsExperiment.addAlternateNpcs(NpcID.SHAPESHIFTERSPIDER, NpcID.SHAPESHIFTERBEAR, NpcID.SHAPESHIFTERWOLF);
		killWitchsExperiment.addSubSteps(grabBall);
		killWitchsExperiment.addSafeSpots(new WorldPoint(2936, 3459, 0));

		pickupBall = new ItemStep(this, new WorldPoint(2935, 3460, 0), "Pick up the ball.", ball);
		returnToBoy = new NpcStep(this, NpcID.BALLBOY, new WorldPoint(2928, 3456, 0), "Return the ball to the boy. Make sure the witch doesn't spot you or you'll have to get the ball back again..");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToBoy);

		var getTheMagnet = new ConditionalStep(this, getKey);
		getTheMagnet.addStep(and(inHouse, magnet), useCheeseOnHole);
		getTheMagnet.addStep(and(inDownstairsHouse, magnet), goBackUpstairs);
		getTheMagnet.addStep(and(inDownstairsHouseWest, new ObjectCondition(ObjectID.MAGNETCBOPEN)), openCupboardAndLoot2);
		getTheMagnet.addStep(inDownstairsHouseWest, openCupboardAndLoot);
		getTheMagnet.addStep(inDownstairsHouseEast, enterGate);
		getTheMagnet.addStep(inHouse, goDownstairs);
		getTheMagnet.addStep(inUpstairsHouse, goDownstairsFromTop);
		getTheMagnet.addStep(houseKey.alsoCheckBank(), enterHouse);

		steps.put(1, getTheMagnet);
		steps.put(2, getTheMagnet);

		var killExperiment = new ConditionalStep(this, getKey);
		killExperiment.addStep(not(hasReadDiary), readDiary);
		killExperiment.addStep(and(inShed, experimentNearby), killWitchsExperiment);
		killExperiment.addStep(inShed, grabBall);
		killExperiment.addStep(and(ratHasMagnet, inHouseOrGarden, shedKey), enterShed);
		killExperiment.addStep(and(ratHasMagnet, inHouseOrGarden), searchFountain);
		killExperiment.addStep(and(ratHasMagnet, inDownstairsHouse), goBackUpstairs);
		killExperiment.addStep(and(inHouse, magnet), useCheeseOnHole);
		killExperiment.addStep(and(inDownstairsHouse, magnet), goBackUpstairs);
		killExperiment.addStep(and(inDownstairsHouseWest, new ObjectCondition(ObjectID.MAGNETCBOPEN)), openCupboardAndLoot2);
		killExperiment.addStep(inDownstairsHouseWest, openCupboardAndLoot);
		killExperiment.addStep(inDownstairsHouseEast, enterGate);
		killExperiment.addStep(inHouse, goDownstairs);
		killExperiment.addStep(inUpstairsHouse, goDownstairsFromTop);
		killExperiment.addStep(houseKey.alsoCheckBank(), enterHouse);

		steps.put(3, killExperiment);
		// TODO: Add 'pick up diary', 'read diary' after step 3
		steps.put(5, killExperiment);

		var returnBall = new ConditionalStep(this, getKey);
		returnBall.addStep(ball.alsoCheckBank(), returnToBoy);
		returnBall.addStep(inShed, pickupBall);
		returnBall.addStep(inHouseOrGarden, enterShedWithoutKey);
		returnBall.addStep(houseKey.alsoCheckBank(), enterHouse);
		returnBall.addStep(inDownstairsHouse, goBackUpstairs);
		returnBall.addStep(inUpstairsHouse, goDownstairsFromTop);

		steps.put(6, returnBall);
		return steps;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new FreeInventorySlotRequirement(2)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			cheese,
			leatherGloves,
			armourAndWeapon
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Witch's experiment (level 19, 30, 42 and 53)"
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
			new ExperienceReward(Skill.HITPOINTS, 6325)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Start the quest", List.of(
			talkToBoy
		), List.of(
			cheese,
			leatherGloves,
			armourAndWeapon
		)));

		sections.add(new PanelDetails("Accessing the garden", List.of(
			getKey,
			enterHouse,
			goDownstairs,
			enterGate,
			openCupboardAndLoot,
			goBackUpstairs,
			useCheeseOnHole
		)));

		var checkpoint = new PanelDetails("Checkpoint", List.of(
			readDiary
		));
		checkpoint.setLockingStep(readDiary);
		// It's not relevant for the user to be able to check off the step if they've already reached beyond quest state 3
		checkpoint.setVars(0, 1, 2, 3, 4);
		sections.add(checkpoint);

		sections.add(new PanelDetails("Defeat the witch's experiment", List.of(
			searchFountain,
			enterShed,
			killWitchsExperiment,
			pickupBall,
			returnToBoy
		)));

		return sections;
	}
}
