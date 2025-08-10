/*
 * Copyright (c) 2021, Zoinkwiz
 * Copyright (c) 2021, Haavard
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
package com.questhelper.helpers.quests.hazeelcult;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestVarPlayer;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.MultiNpcStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class HazeelCult extends BasicQuestHelper
{
	// Recommended items
	ItemRequirement ardougneCloak;

	// Zones
	Zone cultEntrance;
	Zone cultRoom;
	Zone manorBasement;
	Zone manorF1;
	Zone manorF2;
	Zone manorF2LadderRoom;

	// Miscellaneous requirements
	ItemRequirement hazeelScroll;
	ItemRequirement poison;
	ItemRequirement carnilleanArmour;
	ItemRequirement key;
	ItemRequirement hazeelMark;

	ZoneRequirement inCultEntrance;
	ZoneRequirement inCultRoom;
	ZoneRequirement inManorBasement;
	ZoneRequirement inManorF1;
	ZoneRequirement inManorF2;
	ZoneRequirement inManorF2LadderRoom;

	VarbitRequirement alomoneAttackable;
	VarbitRequirement givenAlomoneScroll;
	VarbitRequirement givenArmour;
	VarbitRequirement canSearchChest;
	VarbitRequirement sidedWithCeril;
	VarbitRequirement receivedMark;
	VarbitRequirement talkedToCerilAfterPoison;
	VarbitRequirement butlerArrested;
	ItemOnTileRequirement armourNearby;
	VarplayerRequirement onStep7;
	Requirement hadArmour;

	// Steps
	NpcStep talkToCeril;
	ObjectStep enterCave;
	NpcStep talkToClivet;

	// Ceril side steps
	ObjectStep leaveCaveForValves;
	HazeelValves valveStepsCeril;
	ObjectStep enterCaveAfterValvesForCeril;
	ObjectStep boardRaftToKill;
	NpcStep talkToAlomoneToKill;
	NpcStep killAlomone;
	ObjectStep retrieveArmourFromChest;
	ObjectStep returnOnRaftAfterKilling;
	ObjectStep leaveCaveAfterKilling;
	NpcStep talkToJonesAfterKilling;
	NpcStep talkToCerilAfterKilling;
	ObjectStep goUpToCupboard;
	ObjectStep searchCupboardForEvidence;
	NpcStep talkToCerilToFinish;

	// Hazeel side steps
	NpcStep getPoison;
	ObjectStep leaveCaveWithPoison;
	ObjectStep enterKitchen;
	ObjectStep usePoisonOnRange;
	ObjectStep leaveKitchen;
	NpcStep talkToCerilAfterPoison;
	HazeelValves valveStepsHazeel;
	ObjectStep enterCaveAfterPoison;
	NpcStep talkToClivetAfterPoison;
	ObjectStep boardRaftAfterPoison;
	NpcStep talkToAlomone;
	ObjectStep returnOnRaftAfterAlomone;
	ObjectStep leaveCaveAfterAlomone;
	ObjectStep enterKitchenAfterButler;
	ObjectStep searchCrateForKey;
	ObjectStep leaveKitchenWithKey;
	ObjectStep goToF1WithKey;
	ObjectStep knockOnWall;
	ObjectStep climbLadderWithKey;
	ObjectStep searchChestForScroll;
	ObjectStep goF2ToF1WithScroll;
	ObjectStep knockOnWallToExit;
	ObjectStep goF1ToF0WithScroll;
	ObjectStep enterCaveWithScroll;
	ObjectStep boardRaftWithScroll;
	NpcStep giveAlomoneScroll;

	@Override
	protected void setupZones()
	{
		cultEntrance = new Zone(new WorldPoint(2565, 9679, 0), new WorldPoint(2571, 9685, 0));
		cultRoom = new Zone(new WorldPoint(2600, 9666, 0), new WorldPoint(2615, 9693, 0));
		manorBasement = new Zone(new WorldPoint(2535, 9692, 0), new WorldPoint(2550, 9703, 0));
		manorF1 = new Zone(new WorldPoint(2564, 3267, 1), new WorldPoint(2576, 3275, 1));
		manorF2 = new Zone(new WorldPoint(2564, 3267, 2), new WorldPoint(2576, 3275, 2));
		manorF2LadderRoom = new Zone(new WorldPoint(2573, 3271, 1), new WorldPoint(2571, 3271, 1));
	}

	@Override
	protected void setupRequirements()
	{
		givenAlomoneScroll = new VarbitRequirement(VarbitID.HAZEELCULT_GIVEN_SCROLL, 1);
		givenArmour = new VarbitRequirement(VarbitID.HAZEELCULT_GIVEN_ARMOUR, 1);
		alomoneAttackable = new VarbitRequirement(VarbitID.HAZEELCULT_ALOMONE_VIS, 1);
		canSearchChest = new VarbitRequirement(VarbitID.HAZEELCULT_ALOMONE_VIS, 2);
		sidedWithCeril = new VarbitRequirement(VarbitID.HAZEELCULT_CLIVET_LOCATION, 1);
		receivedMark = new VarbitRequirement(VarbitID.HAZEELCULT_GIVEN_AMULET, 1);
		talkedToCerilAfterPoison = new VarbitRequirement(VarbitID.HAZEELCULT_POISON_SUCCESS, 1);
		butlerArrested = new VarbitRequirement(VarbitID.HAZEELCULT_JONES_CUTSCENE, 1);

		ardougneCloak = new ItemRequirement("Ardougne cloak for Monastery teleport", ItemID.ARDY_CAPE_EASY).isNotConsumed();
		ardougneCloak.addAlternates(ItemID.ARDY_CAPE_MEDIUM, ItemID.ARDY_CAPE_HARD, ItemID.ARDY_CAPE_ELITE);

		poison = new ItemRequirement("Poison", ItemID.POISON);
		poison.setTooltip("You can get another from Clivet");
		hazeelScroll = new ItemRequirement("Hazeel scroll", ItemID.HAZEEL_SCROLL).hideConditioned(givenAlomoneScroll);
		carnilleanArmour = new ItemRequirement("Carnillean armour", ItemID.CARNILLEAN_ARMOUR).hideConditioned(givenArmour);
		key = new ItemRequirement("Chest key", ItemID.CARNILLEANCHESTKEY);
		hazeelMark = new ItemRequirement("Hazeel's mark", ItemID.MARK_OF_HAZEEL);

		inCultEntrance = new ZoneRequirement(cultEntrance);
		inCultRoom = new ZoneRequirement(cultRoom);
		inManorBasement = new ZoneRequirement(manorBasement);
		inManorF1 = new ZoneRequirement(manorF1);
		inManorF2 = new ZoneRequirement(manorF2);
		inManorF2LadderRoom = new ZoneRequirement(manorF2LadderRoom);

		// Got armour, 14778 0->1
		hadArmour = or(givenArmour, carnilleanArmour.alsoCheckBank(questBank));

		// Talking to Ceril
		// 3679 -1 -> 71
		armourNearby = new ItemOnTileRequirement(carnilleanArmour);

		// 14779 1->2, asked to go talk to the butler
		onStep7 = new VarplayerRequirement(QuestVarPlayer.QUEST_HAZEEL_CULT.getId(), 7);

		// Butler found out
		// 14773 1
		// 14774 1

		/* Ceril var changes */
		// Sided with Ceril
		// 3->4
		// 14769 0->1

		// Talk to Ceril, he recommends the valves
		// 14783 0->1

		// Attackable Alomone
		// 14771 = 1
		// 14770 = 1
		// varp 223 4->6 when killed Alomone
		// 14770 1->2
	}

	public void setupSteps()
	{
		talkToCeril = new NpcStep(this, NpcID.SIR_CERIL_CARNILLEAN_VIS, new WorldPoint(2569, 3275, 0),
			"Talk to Ceril Carnillean in the south west of East Ardougne.");
		talkToCeril.addDialogSteps("What's wrong?", "Yes.");

		enterCave = new ObjectStep(this, ObjectID.HAZEELCULTCAVE, new WorldPoint(2587, 3235, 0),
			"Enter the cave south east of the Clock Tower entrance.");
		talkToClivet = new NpcStep(this, NpcID.CLIVET_HAZEEL_CULTIST_VIS, new WorldPoint(2569, 9682, 0),
			"Talk to Clivet. You can choose to either side with him or with the Carnilleans.");
		talkToClivet.addDialogSteps("Alright, I've made my decision.", "Actually, I have no questions.", "I have no more questions.", "What do you mean?");
		talkToClivet.addDialogChange("I won't help you.", "I won't help you. (side with Ceril)");
		talkToClivet.addDialogChange("Alright, how do I do it?", "Alright, how do I do it? (side with Hazeel)");
		talkToClivet.addDialogChange("I'll help you.", "I'll help you. (side with Hazeel)");
		leaveCaveForValves = new ObjectStep(this, ObjectID.HAZEELCULTSTAIRS, new WorldPoint(2571, 9684, 0),
			"Go back to the surface.");

		valveStepsCeril = new HazeelValves(this);
		valveStepsCeril.addSubSteps(leaveCaveForValves);

		// Hazeel side
		getPoison = new NpcStep(this, NpcID.CLIVET_HAZEEL_CULTIST_VIS, new WorldPoint(2569, 9682, 0),
			"Talk to Clivet for some poison.");
		leaveCaveWithPoison = new ObjectStep(this, ObjectID.HAZEELCULTSTAIRS, new WorldPoint(2571, 9684, 0),
			"Go back to the surface.", poison);
		enterKitchen = new ObjectStep(this, ObjectID.CARNILLEAN_LADDER_DOWN, new WorldPoint(2570, 3267, 0),
			"Climb down the ladder in the Carnillean house to the kitchen.", poison);
		usePoisonOnRange = new ObjectStep(this, ObjectID.CARNILLEANRANGE, new WorldPoint(2538, 9699, 0),
			"Use the poison on the range.", poison.highlighted());
		usePoisonOnRange.addIcon(ItemID.POISON);
		leaveKitchen = new ObjectStep(this, ObjectID.CARNILLEAN_LADDER_UP, new WorldPoint(2544, 9694, 0),
			"Leave the kitchen.");
		talkToCerilAfterPoison = new NpcStep(this, NpcID.SIR_CERIL_CARNILLEAN_VIS, new WorldPoint(2569, 3275, 0),
			"Talk to Ceril Carnillean to confirm the results of the poison.");

		valveStepsHazeel = new HazeelValves(this);

		enterCaveAfterPoison = new ObjectStep(this, ObjectID.HAZEELCULTCAVE, new WorldPoint(2587, 3235, 0),
			"Return to Clivet in the cave south of East Ardougne.");
		talkToClivetAfterPoison = new NpcStep(this, NpcID.CLIVET_HAZEEL_CULTIST_VIS, new WorldPoint(2569, 9682, 0),
			"Return to Clivet.");
		talkToClivetAfterPoison.addSubSteps(enterCaveAfterPoison);

		boardRaftAfterPoison = new ObjectStep(this, ObjectID.HAZEELSEWERRAFT, new WorldPoint(2568, 9679, 0),
			"Board the raft.");
		talkToAlomone = new NpcStep(this, NpcID.ALOMONE_HAZEEL_CULTIST_1OP, new WorldPoint(2607, 9673, 0),
			"Talk to Alomone.");
		returnOnRaftAfterAlomone = new ObjectStep(this, ObjectID.HAZEELSEWERRAFT, new WorldPoint(2607, 9693, 0),
			"Search a crate in the Carnillean's kitchen for a key.");
		leaveCaveAfterAlomone = new ObjectStep(this, ObjectID.HAZEELCULTSTAIRS, new WorldPoint(2571, 9684, 0),
			"Search a crate in the Carnillean's kitchen for a key.");
		enterKitchenAfterButler = new ObjectStep(this, ObjectID.CARNILLEAN_LADDER_DOWN, new WorldPoint(2570, 3267, 0),
			"Search a crate in the Carnillean's basement kitchen for a key.");
		searchCrateForKey = new ObjectStep(this, ObjectID.CARNILLEANCRATE, new WorldPoint(2545, 9696, 0),
			"Search a crate in the Carnillean's basement kitchen for a key.");
		searchCrateForKey.addSubSteps(returnOnRaftAfterAlomone, leaveCaveAfterAlomone, enterKitchenAfterButler);

		leaveKitchenWithKey = new ObjectStep(this, ObjectID.CARNILLEAN_LADDER_UP, new WorldPoint(2544, 9694, 0),
			"Go back upstairs from the kitchen.", key);
		goToF1WithKey = new ObjectStep(this, ObjectID.CARNILLEAN_STAIRS, new WorldPoint(2569, 3269, 0),
			"Go upstairs in the house.", key);

		knockOnWall = new ObjectStep(this, ObjectID.CARNILLEANBOOKCASE_KNOCK, new WorldPoint(2572, 3270, 1), "Knock the wall to enter the hidden room.");
		knockOnWall.addDialogStep("Yes.");

		climbLadderWithKey = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2573, 3271, 1),
			"Knock the wall to enter the hidden room, then climb up the ladder.", key);
		climbLadderWithKey.addDialogStep("Yes.");
		climbLadderWithKey.addSubSteps(knockOnWall);

		searchChestForScroll = new ObjectStep(this, ObjectID.CARNILLEANSHUTCHEST, new WorldPoint(2571, 3269, 2),
			"Open the chest.", key.highlighted());
		searchChestForScroll.addIcon(ItemID.CARNILLEANCHESTKEY);
		goF2ToF1WithScroll = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2573, 3271, 2),
			"Return to Alomone with the scroll.", hazeelScroll);

		knockOnWallToExit = new ObjectStep(this, ObjectID.CARNILLEANBOOKCASE_KNOCK, new WorldPoint(2572, 3270, 1), "Return to Alomone with the scroll.", hazeelScroll);

		goF1ToF0WithScroll = new ObjectStep(this, ObjectID.CARNILLEAN_STAIRSTOP, new WorldPoint(2569, 3269, 1),
			"Return to Alomone with the scroll.", hazeelScroll);
		enterCaveWithScroll = new ObjectStep(this, ObjectID.HAZEELCULTCAVE, new WorldPoint(2587, 3235, 0),
			"Return to Alomone with the scroll.", hazeelScroll);
		boardRaftWithScroll = new ObjectStep(this, ObjectID.HAZEELSEWERRAFT, new WorldPoint(2568, 9679, 0),
			"Return to Alomone with the scroll. If the raft doesn't go to Alomone, repeat the prior valve steps first.",
			hazeelScroll);

		giveAlomoneScroll = new NpcStep(this, NpcID.ALOMONE_HAZEEL_CULTIST_1OP, new WorldPoint(2607, 9673, 0),
			"Return to Alomone with the scroll.", hazeelScroll);
		giveAlomoneScroll.addSubSteps(goF2ToF1WithScroll, knockOnWallToExit, goF1ToF0WithScroll, enterCaveWithScroll, boardRaftWithScroll);

		// Ceril side
		enterCaveAfterValvesForCeril = new ObjectStep(this, ObjectID.HAZEELCULTCAVE, new WorldPoint(2587, 3235, 0),
			"Re-enter the cave.");
		boardRaftToKill = new ObjectStep(this, ObjectID.HAZEELSEWERRAFT, new WorldPoint(2568, 9679, 0),
			"Board the raft.");
		talkToAlomoneToKill = new MultiNpcStep(this, NpcID.ALOMONE_HAZEEL_CULTIST_1OP, new WorldPoint(2607, 9673, 0),
			"Talk to Alomone. Be ready to fight him (level 13).", 14770, 11976);
		killAlomone = new MultiNpcStep(this, NpcID.ALOMONE_HAZEEL_CULTIST_2OP, new WorldPoint(2607, 9673, 0),
			"Kill Alomone.", 14770, 11976);
		// Hit Alomone once, 13092 = 1895
		// 13095 = 1730
		retrieveArmourFromChest = new ObjectStep(this, ObjectID.HAZEEL_CHEST_CLOSED, new WorldPoint(2611, 9674, 0),
			"Retrieve armour from the chest. If you'd like to keep a set of the armour for yourself, drop it and search the chest for more.", carnilleanArmour);
		returnOnRaftAfterKilling = new ObjectStep(this, ObjectID.HAZEELSEWERRAFT, new WorldPoint(2607, 9693, 0),
			"Return to Ceril Carnillean.", carnilleanArmour);
		leaveCaveAfterKilling = new ObjectStep(this, ObjectID.HAZEELCULTSTAIRS, new WorldPoint(2571, 9684, 0),
			"Return to Ceril Carnillean.", carnilleanArmour);
		talkToJonesAfterKilling = new NpcStep(this, NpcID.BUTLER_JONES_HAZEEL_CULTIST_OP, new WorldPoint(2569, 3271, 0),
			"Talk to Butler Jones.");
		talkToCerilAfterKilling = new NpcStep(this, NpcID.SIR_CERIL_CARNILLEAN_VIS, new WorldPoint(2569, 3275, 0),
			"Bring the armour to Ceril in house in the south west of East Ardougne.", carnilleanArmour);
		talkToCerilAfterKilling.addSubSteps(returnOnRaftAfterKilling, leaveCaveAfterKilling);
		// Started up
		// 12164 0->1
		// 933 0->1
		// 13989 0->1

		// Given armour, 14772 0->1

		goUpToCupboard = new ObjectStep(this, ObjectID.STAIRS, new WorldPoint(2569, 3269, 0),
			"Go upstairs in the house.");
		searchCupboardForEvidence = new ObjectStep(this, ObjectID.HAZEELCBSHUT, new WorldPoint(2574, 3267, 1),
			"Search the cupboard in the east room.");
		searchCupboardForEvidence.addAlternateObjects(ObjectID.HAZEELCBOPEN);
		searchCupboardForEvidence.addSubSteps(goUpToCupboard);

		talkToCerilToFinish = new NpcStep(this, NpcID.SIR_CERIL_CARNILLEAN_VIS, new WorldPoint(2569, 3275, 0),
			"Talk to Ceril to finish the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		// TODO: Should the valves section implement the PuzzleWrapper?
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToCeril);

		// Got poison first time, 14782  0->1
		// Have poison currently (10670 0->1, back to 0 if you talk to Clivet without poison)
		// Is also for recieved the mark?

		var goTalkToClivet = new ConditionalStep(this, enterCave);
		goTalkToClivet.addStep(inCultEntrance, talkToClivet);
		steps.put(2, goTalkToClivet);
		steps.put(3, goTalkToClivet);

		// Help Hazeel
		var hazeel4GoPoisonSteps = new ConditionalStep(this, enterKitchen);
		// 14779 0->1 when used poison on range
		hazeel4GoPoisonSteps.addStep(inManorBasement, usePoisonOnRange);
		hazeel4GoPoisonSteps.addStep(inCultEntrance, leaveCaveWithPoison);

		// TODO: Verify if this is needed
		var goTalkAfterPoison = new ConditionalStep(this, talkToCerilAfterPoison);
		goTalkAfterPoison.addStep(inManorBasement, leaveKitchen);

		var goTalkToClivetAfterPoison = new ConditionalStep(this, goTalkAfterPoison);
		// Probably don't actually need the mark but nice to ensure the player gets it
		goTalkToClivetAfterPoison.addStep(and(inCultRoom, hazeelMark.alsoCheckBank(questBank)), talkToAlomone);
		goTalkToClivetAfterPoison.addStep(and(inCultEntrance, valveStepsHazeel.solved, hazeelMark.alsoCheckBank(questBank)), boardRaftAfterPoison);
		goTalkToClivetAfterPoison.addStep(and(inCultEntrance, valveStepsHazeel.solved), talkToClivetAfterPoison);
		goTalkToClivetAfterPoison.addStep(valveStepsHazeel.solved, enterCaveAfterPoison);
		goTalkToClivetAfterPoison.addStep(talkedToCerilAfterPoison, valveStepsHazeel);

		var hadScroll = or(hazeelScroll.alsoCheckBank(questBank), givenAlomoneScroll);
		var hazeelSteps = new ConditionalStep(this, enterKitchenAfterButler);
		hazeelSteps.addStep(and(inCultRoom, hadScroll), giveAlomoneScroll);
		hazeelSteps.addStep(and(inCultEntrance, hadScroll), boardRaftWithScroll);
		hazeelSteps.addStep(and(inManorF1, hadScroll, inManorF2LadderRoom), knockOnWallToExit);
		hazeelSteps.addStep(and(inManorF1, hadScroll), goF1ToF0WithScroll);
		hazeelSteps.addStep(and(inManorF2, hadScroll), goF2ToF1WithScroll);
		hazeelSteps.addStep(hadScroll, enterCaveWithScroll);
		hazeelSteps.addStep(and(inManorF2, key), searchChestForScroll);
		hazeelSteps.addStep(and(inManorF1, key, inManorF2LadderRoom), climbLadderWithKey);
		hazeelSteps.addStep(and(inManorF1, key), knockOnWall);
		hazeelSteps.addStep(and(inManorBasement, key), leaveKitchenWithKey);
		hazeelSteps.addStep(key, goToF1WithKey);
		hazeelSteps.addStep(inManorBasement, searchCrateForKey);
		hazeelSteps.addStep(inCultEntrance, leaveCaveAfterAlomone);
		hazeelSteps.addStep(inCultRoom, returnOnRaftAfterAlomone);

		// Ceril side
		var cerilSteps = new ConditionalStep(this, valveStepsCeril);
		cerilSteps.addStep(and(onStep7, butlerArrested), talkToCerilToFinish);
		cerilSteps.addStep(and(onStep7, inManorF1), searchCupboardForEvidence);
		cerilSteps.addStep(onStep7, goUpToCupboard);
		cerilSteps.addStep(and(inCultEntrance, hadArmour), leaveCaveAfterKilling);
		cerilSteps.addStep(and(inCultRoom, hadArmour), returnOnRaftAfterKilling);
		cerilSteps.addStep(hadArmour, talkToCerilAfterKilling);
		cerilSteps.addStep(and(inCultRoom, canSearchChest), retrieveArmourFromChest);
		cerilSteps.addStep(and(inCultRoom, alomoneAttackable), killAlomone);
		cerilSteps.addStep(and(inCultRoom), talkToAlomoneToKill);
		cerilSteps.addStep(and(inCultEntrance, valveStepsCeril.solved), boardRaftToKill);
		cerilSteps.addStep(valveStepsCeril.solved, enterCaveAfterValvesForCeril);
		cerilSteps.addStep(inCultEntrance, leaveCaveForValves);

		// TODO: 14782 0->1 may occur to represent Hazeel

		// Told to make poison, 223 3->4
		// Sided with Ceril:
		// 10670 1->0 occurs,
		// 14769 0->1 and 223 3->4
		var step4 = new ConditionalStep(this, enterCave);
		step4.addStep(sidedWithCeril, cerilSteps);
		step4.addStep(poison, hazeel4GoPoisonSteps);
		step4.addStep(inCultEntrance, getPoison);
		steps.put(4, step4);

		// Assuming this can only be reached in Hazeel side, but may be wrong
		steps.put(5, goTalkToClivetAfterPoison);

		var step6 = new ConditionalStep(this, hazeelSteps);
		step6.addStep(sidedWithCeril, cerilSteps);
		steps.put(6, step6);

		var step7 = new ConditionalStep(this, hazeelSteps);
		step7.addStep(sidedWithCeril, cerilSteps);
		steps.put(7, step7);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			ardougneCloak
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Alomone (level 13) if taking Ceril's side"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 1500)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("(2,005 if siding with Ceril) Coins", ItemID.COINS, 2000),
			new ItemReward("Hazeel's mark (if you sided with Hazeel)", ItemID.MARK_OF_HAZEEL),
			new ItemReward("Carnillean armour (if you sided with Ceril)", ItemID.CARNILLEAN_ARMOUR)
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"If you sided with Hazeel and are being guided to help Ceril, just click the box in the Ceril sidebar header to switch to Hazeel"
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToCeril,
			enterCave,
			talkToClivet
		)));

		var cerilStepsSidebar = new ArrayList<QuestStep>(Collections.singletonList(leaveCaveForValves));
		cerilStepsSidebar.addAll(valveStepsCeril.getDisplaySteps());
		cerilStepsSidebar.addAll(Arrays.asList(enterCaveAfterValvesForCeril, boardRaftToKill, talkToAlomoneToKill, killAlomone,
			retrieveArmourFromChest, talkToCerilAfterKilling, talkToJonesAfterKilling, searchCupboardForEvidence, talkToCerilToFinish));
		PanelDetails cerilPanel = new PanelDetails("Siding with Ceril", cerilStepsSidebar);
		// TODO: add locking step to make it lockable?
		sections.add(cerilPanel);

		var hazeelSteps = new ArrayList<QuestStep>(Arrays.asList(getPoison, leaveCaveWithPoison, enterKitchen,
			usePoisonOnRange, leaveKitchen, talkToCerilAfterPoison));
		hazeelSteps.addAll(valveStepsHazeel.getDisplaySteps());
		hazeelSteps.addAll(Arrays.asList(talkToClivetAfterPoison, boardRaftAfterPoison, talkToAlomone,
			searchCrateForKey, leaveKitchenWithKey, goToF1WithKey, climbLadderWithKey, searchChestForScroll,
			giveAlomoneScroll));
		// TODO: add locking step to make it lockable?
		sections.add(new PanelDetails("Siding with Hazeel", hazeelSteps));

		return sections;
	}
}
