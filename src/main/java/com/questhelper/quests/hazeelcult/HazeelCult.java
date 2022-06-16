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
package com.questhelper.quests.hazeelcult;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.HAZEEL_CULT
)
public class HazeelCult extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement ardougneCloak;

	ItemRequirement hazeelScroll, poison, carnilleanArmour, key, hazeelMark;

	Requirement inCultEntrance, inCultRoom, inManorBasement, inManorF1, inManorF2, armourNearby, hasHazeelItem, onStep7;

	QuestStep talkToCeril, enterCave, talkToClivet, leaveCaveForValves;

	// Hazeel side
	QuestStep leaveCaveForPoison, enterKitchen, usePoisonOnRange, leaveKitchen,
		enterCaveAfterPoison,
		talkToClivetAfterPoison, boardRaftAfterPoison, talkToAlomone, returnOnRaftAfterAlmone, leaveCaveAfterAlmone,
		enterKitchenAfterButler, searchCrateForKey, leaveKitchenWithKey, goToF1WithKey,
		climbLadderWithKey, searchChestForScroll, goF2ToF1WithScroll, goF1ToF0WithScroll, enterCaveWithScroll,
		boardRaftWithScroll, giveAlmoneScroll;

	// Ceril side
	QuestStep enterCaveAfterValvesForCeril, boardRaftToKill, killAlomone, pickupArmour, returnOnRaftAfterKilling,
		leaveCaveAfterKilling, talkToJonesAfterKilling, goUpToCeril, talkToCerilAfterKilling, goUpToCupboard,
		searchCupboardForEvidence;

	HazeelValves valveStepsCeril, valveStepsHazeel;

	//Zones
	Zone cultEntrance, cultRoom, manorBasement, manorF1, manorF2;

	ConditionalStep cerilSteps, hazeel4Steps;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToCeril);

		ConditionalStep goTalkToClivet = new ConditionalStep(this, enterCave);
		goTalkToClivet.addStep(inCultEntrance, talkToClivet);
		steps.put(2, goTalkToClivet);
		steps.put(3, goTalkToClivet);

		// Help Hazeel
		hazeel4Steps = new ConditionalStep(this, enterKitchen);
		hazeel4Steps.addStep(inManorBasement, usePoisonOnRange);
		hazeel4Steps.addStep(inCultEntrance, leaveCaveForPoison);

		// TODO: Verify if this is needed
//		ConditionalStep goTalkAfterPoison = new ConditionalStep(this, talkToCerilAfterPoison);
//		goTalkAfterPoison.addStep(inManorBasement, leaveKitchen);

		ConditionalStep goTalkToClivetAfterPoison = new ConditionalStep(this, valveStepsHazeel);
		// Probably don't actually need the mark but nice to ensure the player gets it
		goTalkToClivetAfterPoison.addStep(new Conditions(inCultRoom, hazeelMark.alsoCheckBank(questBank)), talkToAlomone);
		goTalkToClivetAfterPoison.addStep(new Conditions(inCultEntrance, valveStepsHazeel.solved,
				hazeelMark.alsoCheckBank(questBank)),
			boardRaftAfterPoison);
		goTalkToClivetAfterPoison.addStep(new Conditions(inCultEntrance, valveStepsHazeel.solved), talkToClivetAfterPoison);
		goTalkToClivetAfterPoison.addStep(valveStepsHazeel.solved, enterCaveAfterPoison);
		goTalkToClivetAfterPoison.addStep(inManorBasement, leaveKitchen);

		ConditionalStep goGetScroll = new ConditionalStep(this, enterKitchenAfterButler);
		goGetScroll.addStep(new Conditions(inCultRoom, hazeelScroll.alsoCheckBank(questBank)), giveAlmoneScroll);
		goGetScroll.addStep(new Conditions(inCultEntrance, hazeelScroll.alsoCheckBank(questBank)), boardRaftWithScroll);
		goGetScroll.addStep(new Conditions(inManorF1, hazeelScroll.alsoCheckBank(questBank)), goF1ToF0WithScroll);
		goGetScroll.addStep(new Conditions(inManorF2, hazeelScroll.alsoCheckBank(questBank)), goF2ToF1WithScroll);
		goGetScroll.addStep(new Conditions(hazeelScroll.alsoCheckBank(questBank)), enterCaveWithScroll);
		goGetScroll.addStep(new Conditions(inManorF2, key), searchChestForScroll);
		goGetScroll.addStep(new Conditions(inManorF1, key), climbLadderWithKey);
		goGetScroll.addStep(new Conditions(inManorBasement, key), leaveKitchenWithKey);
		goGetScroll.addStep(new Conditions(key), goToF1WithKey);
		goGetScroll.addStep(inManorBasement, searchCrateForKey);
		goGetScroll.addStep(inCultEntrance, leaveCaveAfterAlmone);
		goGetScroll.addStep(inCultRoom, returnOnRaftAfterAlmone);

		// Ceril side
		cerilSteps = new ConditionalStep(this, valveStepsCeril);
		cerilSteps.addStep(new Conditions(onStep7, inManorF1), searchCupboardForEvidence);
		cerilSteps.addStep(onStep7, goUpToCupboard);
		cerilSteps.addStep(new Conditions(inManorF1, carnilleanArmour.alsoCheckBank(questBank)), talkToCerilAfterKilling);
		cerilSteps.addStep(new Conditions(inCultEntrance, carnilleanArmour.alsoCheckBank(questBank)), leaveCaveAfterKilling);
		cerilSteps.addStep(new Conditions(inCultRoom, carnilleanArmour.alsoCheckBank(questBank)), returnOnRaftAfterKilling);
		cerilSteps.addStep(new Conditions(carnilleanArmour.alsoCheckBank(questBank)), goUpToCeril);
		cerilSteps.addStep(new Conditions(armourNearby), pickupArmour);
		cerilSteps.addStep(new Conditions(inCultRoom), killAlomone);
		cerilSteps.addStep(new Conditions(inCultEntrance, valveStepsCeril.solved), boardRaftToKill);
		cerilSteps.addStep(valveStepsCeril.solved, enterCaveAfterValvesForCeril);
		cerilSteps.addStep(inCultEntrance, leaveCaveForValves);
		cerilSteps.setLockingCondition(hasHazeelItem);


		ConditionalStep step4 = new ConditionalStep(this, cerilSteps);
		step4.addStep(hasHazeelItem, hazeel4Steps);
		steps.put(4, step4);

		// Assuming this can only be reached in Hazeel side, but may be wrong
		steps.put(5, goTalkToClivetAfterPoison);

		ConditionalStep step6And7 = new ConditionalStep(this, cerilSteps);
		step6And7.addStep(hasHazeelItem, goGetScroll);
		steps.put(6, step6And7);

		// 7 after opening chest first time. Chest remains locked

		steps.put(7, step6And7);

		// Killed Alomone, 4->6

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		ardougneCloak = new ItemRequirement("Ardougne cloak for Monastery teleport", ItemID.ARDOUGNE_CLOAK_1);
		ardougneCloak.addAlternates(ItemID.ARDOUGNE_CLOAK_2, ItemID.ARDOUGNE_CLOAK_3, ItemID.ARDOUGNE_CLOAK_4);

		poison = new ItemRequirement("Poison", ItemID.POISON);
		poison.setTooltip("You can get another from Clivet");
		hazeelScroll = new ItemRequirement("Hazeel scroll", ItemID.HAZEEL_SCROLL);
		carnilleanArmour = new ItemRequirement("Carnillean armour", ItemID.CARNILLEAN_ARMOUR);
		key = new ItemRequirement("Chest key", ItemID.CHEST_KEY_2404);
		hazeelMark = new ItemRequirement("Hazeel's mark", ItemID.HAZEELS_MARK);

		inCultEntrance = new ZoneRequirement(cultEntrance);
		inCultRoom = new ZoneRequirement(cultRoom);
		inManorBasement = new ZoneRequirement(manorBasement);
		inManorF1 = new ZoneRequirement(manorF1);
		inManorF2 = new ZoneRequirement(manorF2);
		armourNearby = new ItemOnTileRequirement(carnilleanArmour);

		hasHazeelItem = new Conditions(true, LogicType.OR, poison.alsoCheckBank(questBank),
			hazeelScroll.alsoCheckBank(questBank), hazeelMark.alsoCheckBank(questBank), key.alsoCheckBank(questBank));
		onStep7 = new VarplayerRequirement(QuestVarPlayer.QUEST_HAZEEL_CULT.getId(), 7);
	}

	public void loadZones()
	{
		cultEntrance = new Zone(new WorldPoint(2565, 9679, 0), new WorldPoint(2571, 9685, 0));
		cultRoom = new Zone(new WorldPoint(2600, 9666, 0), new WorldPoint(2615, 9693, 0));
		manorF1 = new Zone(new WorldPoint(2564, 3267, 1), new WorldPoint(2576, 3275, 1));
		manorF2 = new Zone(new WorldPoint(2564, 3267, 2), new WorldPoint(2576, 3275, 2));
		manorBasement = new Zone(new WorldPoint(2564, 9667, 0), new WorldPoint(2574, 9672, 0));
	}

	public void setupSteps()
	{
		talkToCeril = new NpcStep(this, NpcID.CERIL_CARNILLEAN, new WorldPoint(2569, 3275, 0),
			"Talk to Ceril Carnillean in the south west of East Ardougne.");
		talkToCeril.addDialogSteps("What's wrong?", "Yes, of course, I'd be happy to help.");
		enterCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2852, new WorldPoint(2587, 3235, 0),
			"Enter the cave south east of the Clocktower.");
		talkToClivet = new NpcStep(this, NpcID.CLIVET, new WorldPoint(2569, 9682, 0),
			"RIGHT-CLICK talk to Clivet. You can choose to either side with him or with the Carnilleans.");
		talkToClivet.addDialogStep("What do you mean?");
		talkToClivet.addDialogChange("You're crazy, I'd never help you.",
			"You're crazy, I'd never help you. (side with Ceril)");
		talkToClivet.addDialogChange("No. I won't do it.", "No. I won't do it. (side with Ceril)");
		talkToClivet.addDialogChange("So what would I have to do?", "So what would I have to do? (side with Hazeel)");
		talkToClivet.addDialogChange("Ok, count me in.", "Ok, count me in. (side with Hazeel)");
		leaveCaveForValves = new ObjectStep(this, ObjectID.STAIRS_2853, new WorldPoint(2571, 9684, 0),
			"Go back to the surface.");

		valveStepsHazeel = new HazeelValves(this);

		valveStepsCeril = new HazeelValves(this);
		valveStepsCeril.addSubSteps(leaveCaveForValves);

		// Hazeel side
		leaveCaveForPoison = new ObjectStep(this, ObjectID.STAIRS_2853, new WorldPoint(2571, 9684, 0),
			"Go back to the surface.", poison);
		enterKitchen = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2570, 3267, 0),
			"Climb down the ladder in the Carnillean house to the kitchen.", poison);
		usePoisonOnRange = new ObjectStep(this, ObjectID.RANGE, new WorldPoint(2565, 9672, 0),
			"Use the poison on the range.", poison.highlighted());
		usePoisonOnRange.addIcon(ItemID.POISON);
		leaveKitchen = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2570, 9667, 0),
			"Leave the kitchen.");

		enterCaveAfterPoison = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2852, new WorldPoint(2587, 3235, 0),
			"Return to Clivet.");
		talkToClivetAfterPoison = new NpcStep(this, NpcID.CLIVET, new WorldPoint(2569, 9682, 0),
			"Return to Clivet.");
		talkToClivetAfterPoison.addSubSteps(enterCaveAfterPoison);

		boardRaftAfterPoison = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2568, 9679, 0),
			"Board the raft.");
		talkToAlomone = new NpcStep(this, NpcID.ALOMONE, new WorldPoint(2607, 9673, 0),
			"Talk to Alomone.");
		returnOnRaftAfterAlmone = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2607, 9693, 0),
			"Search a crate in the Carnillean's kitchen for a key.");
		leaveCaveAfterAlmone = new ObjectStep(this, ObjectID.STAIRS_2853, new WorldPoint(2571, 9684, 0),
			"Search a crate in the Carnillean's kitchen for a key.");
		enterKitchenAfterButler = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2570, 3267, 0),
			"Search a crate in the Carnillean's kitchen for a key.");
		searchCrateForKey = new ObjectStep(this, ObjectID.CRATE_2858, new WorldPoint(2571, 9669, 0),
			"Search a crate in the Carnillean's kitchen for a key.");
		searchCrateForKey.addSubSteps(returnOnRaftAfterAlmone, leaveCaveAfterAlmone, enterKitchenAfterButler);

		leaveKitchenWithKey = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2570, 9667, 0),
			"Go back upstairs from the kitchen.", key);
		goToF1WithKey = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2569, 3269, 0),
			"Go upstairs in the house.", key);
		climbLadderWithKey = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2573, 3271, 1),
			"Knock the wall to enter the hidden room, then climb up the ladder.", key);
		searchChestForScroll = new ObjectStep(this, ObjectID.CHEST_2856, new WorldPoint(2571, 3269, 2),
			"Open the chest.", key.highlighted());
		searchChestForScroll.addIcon(ItemID.CHEST_KEY_2404);
		((ObjectStep) searchChestForScroll).addAlternateObjects(ObjectID.CHEST_2857);
		goF2ToF1WithScroll = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(2573, 3271, 2),
			"Return to Almone with the scroll.", hazeelScroll);
		goF1ToF0WithScroll = new ObjectStep(this, ObjectID.STAIRCASE_15648, new WorldPoint(2569, 3269, 1),
			"Return to Almone with the scroll.", hazeelScroll);
		enterCaveWithScroll = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2852, new WorldPoint(2587, 3235, 0),
			"Return to Almone with the scroll.", hazeelScroll);
		boardRaftWithScroll = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2568, 9679, 0),
			"Return to Almone with the scroll. If the raft doesn't go to Alomone, repeat the prior valve steps first.",
			hazeelScroll);
		giveAlmoneScroll = new NpcStep(this, NpcID.ALOMONE, new WorldPoint(2607, 9673, 0),
			"Return to Almone with the scroll.", hazeelScroll);
		giveAlmoneScroll.addSubSteps(goF2ToF1WithScroll, goF1ToF0WithScroll, enterCaveWithScroll, boardRaftWithScroll);


		// Ceril side
		enterCaveAfterValvesForCeril = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2852, new WorldPoint(2587, 3235, 0),
			"Re-enter the cave.");
		boardRaftToKill = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2568, 9679, 0),
			"Board the raft.");
		killAlomone = new NpcStep(this, NpcID.ALOMONE, new WorldPoint(2607, 9673, 0),
			"Kill Almone and pickup the carnillean armour.");
		pickupArmour = new ItemStep(this, "Pickup the armour.", carnilleanArmour);
		killAlomone.addSubSteps(pickupArmour);
		returnOnRaftAfterKilling = new ObjectStep(this, ObjectID.RAFT, new WorldPoint(2607, 9693, 0),
			"Return to Ceril Carnillean.", carnilleanArmour);
		leaveCaveAfterKilling = new ObjectStep(this, ObjectID.STAIRS_2853, new WorldPoint(2571, 9684, 0),
			"Return to Ceril Carnillean.", carnilleanArmour);

		talkToJonesAfterKilling = new NpcStep(this, NpcID.BUTLER_JONES, new WorldPoint(2569, 3271, 0),
			"Talk to Butler Jones.");
		goUpToCeril = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2569, 3269, 0),
			"Go upstairs in the house.");
		talkToCerilAfterKilling = new NpcStep(this, NpcID.CERIL_CARNILLEAN, new WorldPoint(2572, 3268, 1),
			"Talk to Ceril upstairs in the house.");
		talkToCerilAfterKilling.addSubSteps(returnOnRaftAfterKilling, leaveCaveAfterKilling, goUpToCeril);

		goUpToCupboard = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2569, 3269, 0),
			"Go upstairs in the house.");
		searchCupboardForEvidence = new ObjectStep(this, ObjectID.CUPBOARD_2850, new WorldPoint(2574, 3267, 1),
			"Search the cupboard in the east room.");
		((ObjectStep) searchCupboardForEvidence).addAlternateObjects(ObjectID.CUPBOARD_2851);
		searchCupboardForEvidence.addSubSteps(goUpToCupboard);
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
			new ItemReward("2,000 (2,005 if siding with Ceril) Coins", ItemID.COINS_995, 2000),
			new ItemReward("Hazeel's mark (if you sided with Hazeel)", ItemID.HAZEELS_MARK),
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

		List<QuestStep> cerilSteps = new ArrayList<>(valveStepsCeril.getSteps());
		cerilSteps.addAll(Arrays.asList(enterCaveAfterValvesForCeril, boardRaftToKill, killAlomone,
			talkToCerilAfterKilling, talkToJonesAfterKilling, searchCupboardForEvidence));
		PanelDetails cerilPanel = new PanelDetails("Siding with Ceril", cerilSteps);
		cerilPanel.setLockingStep(this.cerilSteps);
		allSteps.add(cerilPanel);

		List<QuestStep> hazeelSteps = new ArrayList<>(Arrays.asList(leaveCaveForPoison, enterKitchen,
			usePoisonOnRange, leaveKitchen));
		hazeelSteps.addAll(valveStepsHazeel.getSteps());
		hazeelSteps.addAll(Arrays.asList(talkToClivetAfterPoison, boardRaftAfterPoison, talkToAlomone,
			searchCrateForKey, leaveKitchenWithKey, goToF1WithKey, climbLadderWithKey, searchChestForScroll,
			giveAlmoneScroll));
		allSteps.add(new PanelDetails("Siding with Hazeel", hazeelSteps));

		return allSteps;
	}
}
