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
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.or;

public class HazeelCult extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement ardougneCloak;

	ItemRequirement hazeelScroll, poison, carnilleanArmour, key, hazeelMark;

	Requirement inCultEntrance, inCultRoom, inManorBasement, inManorF1, inManorF2, canSearchChest,
		talkedToCerilAfterPoison, armourNearby, sidedWithCeril, receivedMark, onStep7, givenAlomoneScroll,
		givenArmour, hadArmour, butlerArrested;

	QuestStep talkToCeril, enterCave, talkToClivet, leaveCaveForValves;

	// Hazeel side
	QuestStep getPoison, leaveCaveWithPoison, enterKitchen, usePoisonOnRange, leaveKitchen, talkToCerilAfterPoison,
		enterCaveAfterPoison, talkToClivetAfterPoison, boardRaftAfterPoison, talkToAlomone, returnOnRaftAfterAlomone, leaveCaveAfterAlomone,
		enterKitchenAfterButler, searchCrateForKey, leaveKitchenWithKey, goToF1WithKey,
		climbLadderWithKey, searchChestForScroll, goF2ToF1WithScroll, goF1ToF0WithScroll, enterCaveWithScroll,
		boardRaftWithScroll, giveAlomoneScroll;

	// Ceril side
	QuestStep enterCaveAfterValvesForCeril, boardRaftToKill, talkToAlomoneToKill, killAlomone, retrieveArmourFromChest, returnOnRaftAfterKilling,
		leaveCaveAfterKilling, talkToJonesAfterKilling, talkToCerilAfterKilling, goUpToCupboard,
		searchCupboardForEvidence, talkToCerilToFinish;

	HazeelValves valveStepsCeril, valveStepsHazeel;

	//Zones
	Zone cultEntrance, cultRoom, manorBasement, manorF1, manorF2;

	ConditionalStep cerilSteps, hazeel4GoPoisonSteps;

	VarbitRequirement alomoneAttackable;

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

		ConditionalStep goTalkToClivet = new ConditionalStep(this, enterCave);
		goTalkToClivet.addStep(inCultEntrance, talkToClivet);
		steps.put(2, goTalkToClivet);
		steps.put(3, goTalkToClivet);

		// Help Hazeel
		hazeel4GoPoisonSteps = new ConditionalStep(this, enterKitchen);
		// 14779 0->1 when used poison on range
		hazeel4GoPoisonSteps.addStep(inManorBasement, usePoisonOnRange);
		hazeel4GoPoisonSteps.addStep(inCultEntrance, leaveCaveWithPoison);

		// TODO: Verify if this is needed
		ConditionalStep goTalkAfterPoison = new ConditionalStep(this, talkToCerilAfterPoison);
		goTalkAfterPoison.addStep(inManorBasement, leaveKitchen);

		ConditionalStep goTalkToClivetAfterPoison = new ConditionalStep(this, goTalkAfterPoison);
		// Probably don't actually need the mark but nice to ensure the player gets it
		goTalkToClivetAfterPoison.addStep(new Conditions(inCultRoom, hazeelMark.alsoCheckBank(questBank)), talkToAlomone);
		goTalkToClivetAfterPoison.addStep(new Conditions(inCultEntrance, valveStepsHazeel.solved,
			hazeelMark.alsoCheckBank(questBank)), boardRaftAfterPoison);
		goTalkToClivetAfterPoison.addStep(new Conditions(inCultEntrance, valveStepsHazeel.solved), talkToClivetAfterPoison);
		goTalkToClivetAfterPoison.addStep(valveStepsHazeel.solved, enterCaveAfterPoison);
		goTalkToClivetAfterPoison.addStep(talkedToCerilAfterPoison, valveStepsHazeel);

		Conditions hadScroll = new Conditions(LogicType.OR, hazeelScroll.alsoCheckBank(questBank), givenAlomoneScroll);
		ConditionalStep goGetScroll = new ConditionalStep(this, enterKitchenAfterButler);
		goGetScroll.addStep(new Conditions(inCultRoom, hadScroll), giveAlomoneScroll);
		goGetScroll.addStep(new Conditions(inCultEntrance, hadScroll), boardRaftWithScroll);
		goGetScroll.addStep(new Conditions(inManorF1, hadScroll), goF1ToF0WithScroll);
		goGetScroll.addStep(new Conditions(inManorF2, hadScroll), goF2ToF1WithScroll);
		goGetScroll.addStep(new Conditions(hadScroll), enterCaveWithScroll);
		goGetScroll.addStep(new Conditions(inManorF2, key), searchChestForScroll);
		goGetScroll.addStep(new Conditions(inManorF1, key), climbLadderWithKey);
		goGetScroll.addStep(new Conditions(inManorBasement, key), leaveKitchenWithKey);
		goGetScroll.addStep(new Conditions(key), goToF1WithKey);
		goGetScroll.addStep(inManorBasement, searchCrateForKey);
		goGetScroll.addStep(inCultEntrance, leaveCaveAfterAlomone);
		goGetScroll.addStep(inCultRoom, returnOnRaftAfterAlomone);

		// Ceril side
		cerilSteps = new ConditionalStep(this, valveStepsCeril);
		cerilSteps.addStep(new Conditions(onStep7, butlerArrested), talkToCerilToFinish);
		cerilSteps.addStep(new Conditions(onStep7, inManorF1), searchCupboardForEvidence);
		cerilSteps.addStep(onStep7, goUpToCupboard);
		cerilSteps.addStep(new Conditions(inCultEntrance, hadArmour), leaveCaveAfterKilling);
		cerilSteps.addStep(new Conditions(inCultRoom, hadArmour), returnOnRaftAfterKilling);
		cerilSteps.addStep(hadArmour, talkToCerilAfterKilling);
		cerilSteps.addStep(new Conditions(inCultRoom, canSearchChest), retrieveArmourFromChest);
		cerilSteps.addStep(new Conditions(inCultRoom, alomoneAttackable), killAlomone);
		cerilSteps.addStep(new Conditions(inCultRoom), talkToAlomoneToKill);
		cerilSteps.addStep(new Conditions(inCultEntrance, valveStepsCeril.solved), boardRaftToKill);
		cerilSteps.addStep(valveStepsCeril.solved, enterCaveAfterValvesForCeril);
		cerilSteps.addStep(inCultEntrance, leaveCaveForValves);

		// TODO: 14782 0->1 may occur to represent Hazeel

		// Told to make poison, 223 3->4
		// Sided with Ceril:
		// 10670 1->0 occurs,
		// 14769 0->1 and 223 3->4
		ConditionalStep step4 = new ConditionalStep(this, enterCave);
		step4.addStep(sidedWithCeril, cerilSteps);
		step4.addStep(poison, hazeel4GoPoisonSteps);
		step4.addStep(inCultEntrance, getPoison);
		steps.put(4, step4);

		// Assuming this can only be reached in Hazeel side, but may be wrong
		steps.put(5, goTalkToClivetAfterPoison);

		ConditionalStep step6And7 = new ConditionalStep(this, goGetScroll);
		step6And7.addStep(sidedWithCeril, cerilSteps);
		steps.put(6, step6And7);
		steps.put(7, step6And7);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		givenAlomoneScroll = new VarbitRequirement(14780, 1);
		givenArmour = new VarbitRequirement(14772, 1);

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

		alomoneAttackable = new VarbitRequirement(14770, 1);
		canSearchChest = new VarbitRequirement(14770, 2);

		// Got armour, 14778 0->1
		hadArmour = or(givenArmour, carnilleanArmour.alsoCheckBank(questBank));

		talkedToCerilAfterPoison = new VarbitRequirement(14775, 1);
		// Talking to Ceril
		// 3679 -1 -> 71
		armourNearby = new ItemOnTileRequirement(carnilleanArmour);

		sidedWithCeril = new VarbitRequirement(14769, 1);
		// Mark also could be 14776
		receivedMark = new VarbitRequirement(14777, 1);
		// 14779 1->2, asked to go talk to the butler
		onStep7 = new VarplayerRequirement(QuestVarPlayer.QUEST_HAZEEL_CULT.getId(), 7);

		// Butler found out
		// 14773 1
		// 14774 1
		butlerArrested = new VarbitRequirement(14773, 1);

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

	@Override
	protected void setupZones()
	{
		cultEntrance = new Zone(new WorldPoint(2565, 9679, 0), new WorldPoint(2571, 9685, 0));
		cultRoom = new Zone(new WorldPoint(2600, 9666, 0), new WorldPoint(2615, 9693, 0));
		manorF1 = new Zone(new WorldPoint(2564, 3267, 1), new WorldPoint(2576, 3275, 1));
		manorF2 = new Zone(new WorldPoint(2564, 3267, 2), new WorldPoint(2576, 3275, 2));
		manorBasement = new Zone(new WorldPoint(2535, 9692, 0), new WorldPoint(2550, 9703, 0));
	}

	public void setupSteps()
	{
		talkToCeril = new NpcStep(this, NpcID.SIR_CERIL_CARNILLEAN_VIS, new WorldPoint(2569, 3275, 0),
			"Talk to Ceril Carnillean in the south west of East Ardougne.");
		talkToCeril.addDialogSteps("What's wrong?", "Yes.");
		enterCave = new ObjectStep(this, ObjectID.HAZEELCULTCAVE, new WorldPoint(2587, 3235, 0),
			"Enter the cave south east of the Clock Tower.");
		talkToClivet = new NpcStep(this, NpcID.CLIVET_HAZEEL_CULTIST_VIS, new WorldPoint(2569, 9682, 0),
			"Talk to Clivet. You can choose to either side with him or with the Carnilleans.");
		talkToClivet.addDialogSteps("Alright, I've made my decision.", "I have no more questions.", "What do you mean?");
		talkToClivet.addDialogChange("I won't help you.", "I won't help you. (side with Ceril)");
		talkToClivet.addDialogChange("Alright, how do I do it?", "Alright, how do I do it? (side with Hazeel)");
		talkToClivet.addDialogChange("I'll help you.", "I'll help you. (side with Hazeel)");
		leaveCaveForValves = new ObjectStep(this, ObjectID.HAZEELCULTSTAIRS, new WorldPoint(2571, 9684, 0),
			"Go back to the surface.");

		valveStepsHazeel = new HazeelValves(this);

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
		climbLadderWithKey = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2573, 3271, 1),
			"Knock the wall to enter the hidden room, then climb up the ladder.", key);
		climbLadderWithKey.addDialogStep("Yes.");
		searchChestForScroll = new ObjectStep(this, ObjectID.CARNILLEANSHUTCHEST, new WorldPoint(2571, 3269, 2),
			"Open the chest.", key.highlighted());
		searchChestForScroll.addIcon(ItemID.CARNILLEANCHESTKEY);
		goF2ToF1WithScroll = new ObjectStep(this, ObjectID.LADDERTOP, new WorldPoint(2573, 3271, 2),
			"Return to Alomone with the scroll.", hazeelScroll);
		goF1ToF0WithScroll = new ObjectStep(this, ObjectID.CARNILLEAN_STAIRSTOP, new WorldPoint(2569, 3269, 1),
			"Return to Alomone with the scroll.", hazeelScroll);
		enterCaveWithScroll = new ObjectStep(this, ObjectID.HAZEELCULTCAVE, new WorldPoint(2587, 3235, 0),
			"Return to Alomone with the scroll.", hazeelScroll);
		boardRaftWithScroll = new ObjectStep(this, ObjectID.HAZEELSEWERRAFT, new WorldPoint(2568, 9679, 0),
			"Return to Alomone with the scroll. If the raft doesn't go to Alomone, repeat the prior valve steps first.",
			hazeelScroll);
		giveAlomoneScroll = new NpcStep(this, NpcID.ALOMONE_HAZEEL_CULTIST_1OP, new WorldPoint(2607, 9673, 0),
			"Return to Alomone with the scroll.", hazeelScroll);
		giveAlomoneScroll.addSubSteps(goF2ToF1WithScroll, goF1ToF0WithScroll, enterCaveWithScroll, boardRaftWithScroll);

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
		((ObjectStep) searchCupboardForEvidence).addAlternateObjects(ObjectID.HAZEELCBOPEN);
		searchCupboardForEvidence.addSubSteps(goUpToCupboard);

		talkToCerilToFinish = new NpcStep(this, NpcID.SIR_CERIL_CARNILLEAN_VIS, new WorldPoint(2569, 3275, 0),
			"Talk to Ceril to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(ardougneCloak);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Alomone (level 13) if taking Ceril's side");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 1500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("(2,005 if siding with Ceril) Coins", ItemID.COINS, 2000),
			new ItemReward("Hazeel's mark (if you sided with Hazeel)", ItemID.MARK_OF_HAZEEL),
			new ItemReward("Carnillean armour (if you sided with Ceril)", ItemID.CARNILLEAN_ARMOUR)
		);
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("If you sided with Hazeel and are being guided to help Ceril, just click the" +
			" box in the Ceril sidebar header to switch to Hazeel");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToCeril, enterCave, talkToClivet)));

		List<QuestStep> cerilStepsSidebar = new ArrayList<>(Collections.singletonList(leaveCaveForValves));
		cerilStepsSidebar.addAll(valveStepsCeril.getDisplaySteps());
		cerilStepsSidebar.addAll(Arrays.asList(enterCaveAfterValvesForCeril, boardRaftToKill, talkToAlomoneToKill, killAlomone,
			retrieveArmourFromChest, talkToCerilAfterKilling, talkToJonesAfterKilling, searchCupboardForEvidence, talkToCerilToFinish));
		PanelDetails cerilPanel = new PanelDetails("Siding with Ceril", cerilStepsSidebar);
		allSteps.add(cerilPanel);

		List<QuestStep> hazeelSteps = new ArrayList<>(Arrays.asList(getPoison, leaveCaveWithPoison, enterKitchen,
			usePoisonOnRange, leaveKitchen, talkToCerilAfterPoison));
		hazeelSteps.addAll(valveStepsHazeel.getDisplaySteps());
		hazeelSteps.addAll(Arrays.asList(talkToClivetAfterPoison, boardRaftAfterPoison, talkToAlomone,
			searchCrateForKey, leaveKitchenWithKey, goToF1WithKey, climbLadderWithKey, searchChestForScroll,
			giveAlomoneScroll));
		allSteps.add(new PanelDetails("Siding with Hazeel", hazeelSteps));

		return allSteps;
	}
}
