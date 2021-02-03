/*
 * Copyright (c) 2020, Lesteenman <https://github.com/lesteenman>
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
package com.questhelper.quests.thegrandtree;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.FreeInventorySlotRequirement;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.SkillRequirement;
import com.questhelper.requirements.conditional.ConditionForStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ItemRequirementCondition;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ZoneCondition;
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
import java.util.concurrent.locks.Condition;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_GRAND_TREE
)
public class TheGrandTree extends BasicQuestHelper
{
	ItemRequirement accessToFairyRings, energyOrStaminaPotions, transportToGrandTree, oneThousandCoins, combatGear,
		food, prayerPotions, translationBook, barkSample, lumberOrder, gloughsKey, highlightedGloughsKey, invasionPlans,
		twigsT, twigsU, twigsZ, twigsO, highlightedTwigsT, highlightedTwigsU, highlightedTwigsZ, highlightedTwigsO,
		hazelmereScroll, daconiaStone;

	FreeInventorySlotRequirement fourFreeInventorySlots;

	Zone topOfGrandTree, hazelmereHouseFirstFloor, gloughHouse, shipyard, anitaHouse, watchtower, grandTreeTunnels;

	ConditionForStep isInGrandTreeTop, isNearHazelmere, isInGloughsHouse, isInShipyard, isInAnitasHouse, hasGloughsKey,
		isInWatchtower, hasTwigsT, hasTwigsU, hasTwigsZ, hasTwigsO, blackDemonVisible, isInGrandTreeTunnels, hasDaconiaStone;

	QuestStep talkToKingNarnode, climbUpToHazelmere, talkToHazelmere, bringScrollToKingNarnode, climbUpToGlough,
		talkToGlough, talkToKingNarnodeAfterGlough, climbUpToCharlie, talkToCharlie, returnToGlough, findGloughJournal,
		talkToGloughAgain, talkToCharlieAgain, talkToKingNarnodeBeforeEscape, escapeByGlider, enterTheShipyard,
		talkToForeman, climbUpToCharlieAgain, talkToCharlieThirdTime, climbUpToAnita, talkToAnita, climbUpToGloughAgain,
		findInvasionPlans, takeInvasionPlansToKing, climbUpToGloughForWatchtower, climbUpToWatchtower, placeTwigsT,
		placeTwigsU, placeTwigsZ, placeTwigsO, placeTwigs, climbDownTrapDoor, talkToGloughBeforeFight, killBlackDemon,
		climbDownTrapDoorAfterFight, talkToKingAfterFight, findDaconiaStone, giveDaconiaStoneToKingNarnode, returnToGrandTreeTop;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		// Getting started
		steps.put(0, talkToKingNarnode);

		ConditionalStep goTalkToHazelmere = new ConditionalStep(this, climbUpToHazelmere);
		goTalkToHazelmere.addStep(isNearHazelmere, talkToHazelmere);
		steps.put(10, goTalkToHazelmere);

		steps.put(20, bringScrollToKingNarnode);

		ConditionalStep goTalkToGlough = new ConditionalStep(this, climbUpToGlough);
		goTalkToGlough.addStep(isInGloughsHouse, talkToGlough);
		steps.put(30, goTalkToGlough);

		// Investigation
		steps.put(40, talkToKingNarnodeAfterGlough);

		ConditionalStep goTalkToCharlie = new ConditionalStep(this, climbUpToCharlie);
		goTalkToCharlie.addStep(isInGrandTreeTop, talkToCharlie);
		steps.put(50, goTalkToCharlie);

		ConditionalStep goFindGloughJournal = new ConditionalStep(this, returnToGlough);
		goFindGloughJournal.addStep(isInGloughsHouse, findGloughJournal);
		steps.put(60, goFindGloughJournal);

		ConditionalStep goTalkToGloughAgain = new ConditionalStep(this, returnToGlough);
		goTalkToGloughAgain.addStep(isInGloughsHouse, talkToGloughAgain);
		steps.put(70, goTalkToGloughAgain);

		// Karamja
		ConditionalStep goTalkToForeman = new ConditionalStep(this, enterTheShipyard);
		goTalkToForeman.addStep(isInGrandTreeTop, escapeByGlider);
		goTalkToForeman.addStep(isInShipyard, talkToForeman);
		steps.put(80, goTalkToForeman);

		// Tuzo
		ConditionalStep goTalkToCharlieAfterShipyard = new ConditionalStep(this, climbUpToCharlieAgain);
		goTalkToCharlieAfterShipyard.addStep(isInGrandTreeTop, talkToCharlieThirdTime);
		steps.put(90, goTalkToCharlieAfterShipyard);

		ConditionalStep goFindInvasionPlans = new ConditionalStep(this, climbUpToAnita);
		goFindInvasionPlans.addStep(new Conditions(hasGloughsKey, isInGloughsHouse), findInvasionPlans);
		goFindInvasionPlans.addStep(hasGloughsKey, climbUpToGloughAgain);
		goFindInvasionPlans.addStep(isInAnitasHouse, talkToAnita);
		steps.put(100, goFindInvasionPlans);

		steps.put(110, takeInvasionPlansToKing);

		ConditionalStep goUpToWatchtower = new ConditionalStep(this, climbUpToGloughForWatchtower);
		goUpToWatchtower.addStep(isInGloughsHouse, climbUpToWatchtower);

		ConditionalStep goPlaceTuzoTwigs = new ConditionalStep(this, goUpToWatchtower);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, hasTwigsT), placeTwigsT);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, hasTwigsU), placeTwigsU);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, hasTwigsZ), placeTwigsZ);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, hasTwigsO), placeTwigsO);
		goPlaceTuzoTwigs.addStep(isInWatchtower, placeTwigs);
		steps.put(120, goPlaceTuzoTwigs);

		// Encountering the black Demon
		ConditionalStep fightBlackDemonStep = new ConditionalStep(this, goUpToWatchtower);
		fightBlackDemonStep.addStep(blackDemonVisible, killBlackDemon);
		fightBlackDemonStep.addStep(isInWatchtower, climbDownTrapDoor);
		steps.put(130, fightBlackDemonStep);

		ConditionalStep returnToGrandTreeTunnels = new ConditionalStep(this, goUpToWatchtower);
		returnToGrandTreeTunnels.addStep(isInWatchtower, climbDownTrapDoorAfterFight);

		ConditionalStep goTalkToKingAfterFight = new ConditionalStep(this, returnToGrandTreeTunnels);
		goTalkToKingAfterFight.addStep(isInGrandTreeTunnels, talkToKingAfterFight);
		steps.put(140, goTalkToKingAfterFight);

		ConditionalStep bringDaconiaStoneToKing = new ConditionalStep(this, returnToGrandTreeTunnels);
		bringDaconiaStoneToKing.addStep(hasDaconiaStone, giveDaconiaStoneToKingNarnode);
		bringDaconiaStoneToKing.addStep(isInGrandTreeTunnels, findDaconiaStone);
		steps.put(150, bringDaconiaStoneToKing);

		return steps;
	}

	public void setupItemRequirements()
	{
		oneThousandCoins = new ItemRequirement("Coins", ItemID.COINS_995, 1000);

		accessToFairyRings = new ItemRequirement("Access to Fairy Rings", ItemID.DRAMEN_STAFF);
		accessToFairyRings.addAlternates(ItemID.LUNAR_STAFF);

		energyOrStaminaPotions = new ItemRequirement("Energy restoration", ItemCollections.getEnergyRestoration());
		combatGear = new ItemRequirement("Combat gear. Safespotting is possible.", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.getPrayerPotions());
		transportToGrandTree = new ItemRequirement("Transport to the Grand Tree", -1);

		translationBook = new ItemRequirement("Translation Book", ItemID.TRANSLATION_BOOK);
		barkSample = new ItemRequirement("Bark Sample", ItemID.BARK_SAMPLE);
		hazelmereScroll = new ItemRequirement("Hazelmere's Scroll", ItemID.HAZELMERES_SCROLL);
		lumberOrder = new ItemRequirement("Lumber order", ItemID.LUMBER_ORDER);
		gloughsKey = new ItemRequirement("Glough's key", ItemID.GLOUGHS_KEY);
		highlightedGloughsKey = new ItemRequirement("Glough's key", ItemID.GLOUGHS_KEY);
		highlightedGloughsKey.setHighlightInInventory(true);
		invasionPlans = new ItemRequirement("Invasion plans", ItemID.INVASION_PLANS);
		fourFreeInventorySlots = new FreeInventorySlotRequirement(InventoryID.INVENTORY, 4);
		daconiaStone = new ItemRequirement("Daconia stone", ItemID.DACONIA_ROCK);

		twigsT = new ItemRequirement("Twigs (T)", ItemID.TWIGS);
		twigsU = new ItemRequirement("Twigs (U)", ItemID.TWIGS_790);
		twigsZ = new ItemRequirement("Twigs (Z)", ItemID.TWIGS_791);
		twigsO = new ItemRequirement("Twigs (O)", ItemID.TWIGS_792);

		highlightedTwigsT = new ItemRequirement("Twigs (T)", ItemID.TWIGS);
		highlightedTwigsT.setHighlightInInventory(true);
		highlightedTwigsU = new ItemRequirement("Twigs (U)", ItemID.TWIGS_790);
		highlightedTwigsU.setHighlightInInventory(true);
		highlightedTwigsZ = new ItemRequirement("Twigs (Z)", ItemID.TWIGS_791);
		highlightedTwigsZ.setHighlightInInventory(true);
		highlightedTwigsO = new ItemRequirement("Twigs (O)", ItemID.TWIGS_792);
		highlightedTwigsO.setHighlightInInventory(true);
	}

	public void setupZones()
	{
		topOfGrandTree = new Zone(new WorldPoint(2438, 3511, 3), new WorldPoint(2493, 3478, 3));
		hazelmereHouseFirstFloor = new Zone(new WorldPoint(2674, 3089, 1), new WorldPoint(2680, 3085, 1));
		gloughHouse = new Zone(new WorldPoint(2474, 3466, 1), new WorldPoint(2488, 3461, 1));
		shipyard = new Zone(new WorldPoint(2945, 3070, 0), new WorldPoint(3007, 3015, 0));
		anitaHouse = new Zone(new WorldPoint(2386, 3517, 1), new WorldPoint(2392, 3512, 1));
		watchtower = new Zone(new WorldPoint(2482, 3469, 2), new WorldPoint(2491, 3459, 2));
		grandTreeTunnels = new Zone(new WorldPoint(2431, 9920, 0), new WorldPoint(2500, 9856, 0));
	}

	public void setupConditions()
	{
		isInGrandTreeTop = new ZoneCondition(topOfGrandTree);
		isNearHazelmere = new ZoneCondition(hazelmereHouseFirstFloor);
		isInGloughsHouse = new ZoneCondition(gloughHouse);
		isInShipyard = new ZoneCondition(shipyard);
		isInAnitasHouse = new ZoneCondition(anitaHouse);
		isInWatchtower = new ZoneCondition(watchtower);
		isInGrandTreeTunnels = new ZoneCondition(grandTreeTunnels);

		hasGloughsKey = new ItemRequirementCondition(gloughsKey);
		hasTwigsT = new ItemRequirementCondition(twigsT);
		hasTwigsU = new ItemRequirementCondition(twigsU);
		hasTwigsZ = new ItemRequirementCondition(twigsZ);
		hasTwigsO = new ItemRequirementCondition(twigsO);
		hasDaconiaStone = new ItemRequirementCondition(daconiaStone);

		blackDemonVisible = new NpcCondition(NpcID.BLACK_DEMON_1432);
	}

	public void setupSteps()
	{
		WorldPoint locationBottomOfGrandTree = new WorldPoint(2466, 3495, 0);
		WorldPoint locationTopOfGrandTree = new WorldPoint(2466, 3495, 3);

		// Getting Started
		talkToKingNarnode = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, locationBottomOfGrandTree, "Talk to King Narnode Shareen in the Grand Tree.");
		talkToKingNarnode.addDialogSteps("You seem worried, what's up?", "I'd be happy to help!");

		// Hazelmere
		climbUpToHazelmere = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2677, 3087, 0), "Go up to Hazelmere, on the island west of Yanille. Fairy ring CLS or minigame teleport near Yanille.");
		talkToHazelmere = new NpcStep(this, NpcID.HAZELMERE, "Talk to Hazelmere.", translationBook, barkSample);

		bringScrollToKingNarnode = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, locationBottomOfGrandTree, "Return to King Narnode in the Grand Tree.", hazelmereScroll);
		bringScrollToKingNarnode.addDialogStep("I think so!");
		bringScrollToKingNarnode.addDialogStepWithExclusions("None of the above.", "A man came to me with the King's seal.", "I gave the man Daconia rocks.");
		bringScrollToKingNarnode.addDialogStep("A man came to me with the King's seal.");
		bringScrollToKingNarnode.addDialogStep("I gave the man Daconia rocks.");
		bringScrollToKingNarnode.addDialogStep("And Daconia rocks will kill the tree!");

		// Investigation
		climbUpToGlough = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2476, 3463, 0), "Go up to Glough in the Tree Gnome Stronghold.");
		talkToGlough = new NpcStep(this, NpcID.GLOUGH_2061, "Talk to Glough.");
		talkToGlough.addSubSteps(climbUpToGlough);

		talkToKingNarnodeAfterGlough = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, locationBottomOfGrandTree, "Talk to King Narnode again in the Grand Tree.");

		climbUpToCharlie = new DetailedQuestStep(this, locationTopOfGrandTree, "Climb to the top of the Grand Tree.");
		talkToCharlie = new NpcStep(this, NpcID.CHARLIE, "Talk to the prisoner Charlie.");
		talkToCharlie.addSubSteps(climbUpToCharlie);

		returnToGlough = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2476, 3463, 0), "Return to Glough.");
		findGloughJournal = new ObjectStep(this, ObjectID.CUPBOARD_2434, "Search the cupboard for Glough's Journal.");
		findGloughJournal.addSubSteps(returnToGlough);
		talkToGloughAgain = new NpcStep(this, NpcID.GLOUGH_2061, "Talk to Glough again.");
		returnToGrandTreeTop = new DetailedQuestStep(this, locationTopOfGrandTree, "Return to the top of the Grand Tree.");
		talkToCharlieAgain = new NpcStep(this, NpcID.CHARLIE, "Talk to Charlie again.");
		talkToCharlieAgain.addSubSteps(returnToGrandTreeTop);
		talkToKingNarnodeBeforeEscape = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN_8020, "Talk to King Narnode.");
		escapeByGlider = new NpcStep(this, NpcID.CAPTAIN_ERRDO_10467, locationTopOfGrandTree, "Travel with the glider to escape.");
		escapeByGlider.addDialogStep("Take me to Karamja please!");

		// Karamja
		enterTheShipyard = new ObjectStep(this, ObjectID.GATE_2438, new WorldPoint(2945, 3041, 0), "Enter the shipyard on Karamja.");
		enterTheShipyard.addDialogSteps("Glough sent me.", "Ka.", "Lu.", "Min.");
		talkToForeman = new NpcStep(this, NpcID.FOREMAN, new WorldPoint(3000, 3044, 0), "Get the Lumber order from the Foreman on the southern docks. You can either kill him or talk to him.");
		talkToForeman.addDialogSteps("Sadly his wife is no longer with us!", "He loves worm holes.", "Anita.");

		// Tuzo
		climbUpToCharlieAgain = new DetailedQuestStep(this, locationTopOfGrandTree, "Climb to the top of the Grand Tree again.");
		talkToCharlieThirdTime = new NpcStep(this, NpcID.CHARLIE, "Speak to Charlie at the top of the grand tree.", lumberOrder);
		talkToCharlieThirdTime.addSubSteps(climbUpToCharlieAgain);

		climbUpToAnita = new ObjectStep(this, ObjectID.STAIRCASE_16675, new WorldPoint(2390, 3513, 0), "Go up to Anita.");
		talkToAnita = new NpcStep(this, NpcID.ANITA, "Speak to Anita.");
		talkToAnita.addSubSteps(climbUpToAnita);
		talkToAnita.addDialogStep("I suppose so.");

		climbUpToGloughAgain = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2476, 3463, 0), "Go up to Glough's house again.");
		findInvasionPlans = new ObjectStep(this, ObjectID.CLOSED_CHEST_2436, "Search the chest in Glough's house.", highlightedGloughsKey);
		findInvasionPlans.addSubSteps(climbUpToGloughAgain);
		findInvasionPlans.addIcon(ItemID.GLOUGHS_KEY);

		takeInvasionPlansToKing = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, "Take the invasion plans to King Narnode in the Grand Tree.", invasionPlans, fourFreeInventorySlots);

		climbUpToGloughForWatchtower = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2476, 3463, 0), "Go up to Glough's house again.");
		climbUpToWatchtower = new ObjectStep(this, ObjectID.TREE_2447, "Climb up the tree to the watchtower in Glough's house.");

		placeTwigsT = new ObjectStep(this, ObjectID.PILLAR, "Place the twigs (T). Return to King Narnode if you lost them.", highlightedTwigsT);
		placeTwigsT.addIcon(ItemID.TWIGS);

		placeTwigsU = new ObjectStep(this, ObjectID.PILLAR_2441, "Place the twigs (U). Return to King Narnode if you lost them.", highlightedTwigsU);
		placeTwigsU.addIcon(ItemID.TWIGS_790);

		placeTwigsZ = new ObjectStep(this, ObjectID.PILLAR_2442, "Place the twigs (Z). Return to King Narnode if you lost them.", highlightedTwigsZ);
		placeTwigsZ.addIcon(ItemID.TWIGS_791);

		placeTwigsO = new ObjectStep(this, ObjectID.PILLAR_2443, "Place the twigs (O). Return to King Narnode if you lost them.", highlightedTwigsO);
		placeTwigsO.addIcon(ItemID.TWIGS_792);

		placeTwigs = new DetailedQuestStep(this, "Place the twigs to spell 'TUZO' in the watchtower in Glough's house. Return to King Narnode if you lost any.",
			twigsT, twigsU, twigsZ, twigsO);
		placeTwigs.addSubSteps(climbUpToGloughForWatchtower, climbUpToWatchtower, placeTwigsT, placeTwigsU, placeTwigsZ, placeTwigsO);

		// The black demon
		climbDownTrapDoor = new ObjectStep(this, ObjectID.TRAPDOOR_26243, "Go down the trap door. Be prepared for the fight against a Black Demon (level 172).");
		talkToGloughBeforeFight = new NpcStep(this, NpcID.GLOUGH, "Talk to Glough. You can safespot the Demon from where he stands.");
		killBlackDemon = new NpcStep(this, NpcID.BLACK_DEMON_1432, "Kill the black Demon. You can safespot from where Glough stands.");

		climbDownTrapDoorAfterFight = new ObjectStep(this, ObjectID.TRAPDOOR_26243, "Go down the trap door again.");
		talkToKingAfterFight = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 9895, 0), "Talk to King Narnode deeper in the cave.");
		talkToKingAfterFight.addSubSteps(climbDownTrapDoorAfterFight);
		giveDaconiaStoneToKingNarnode = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 9895, 0), "Give the Daconia stone to King Narnode under the Grand Tree.", daconiaStone);

		findDaconiaStone = new ObjectStep(this, ObjectID.ROOT, "Search the roots under the Grand Tree until you find the Daconia stone. If you lose the rock, it will be in the same root again.");
		((ObjectStep) findDaconiaStone).addAlternateObjects(ObjectID.ROOT_1986);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(oneThousandCoins);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food, prayerPotions, energyOrStaminaPotions, accessToFairyRings, transportToGrandTree);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.AGILITY, 25, true));
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Black demon (level 172) (can be safespotted)");
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Getting started", Collections.singletonList(talkToKingNarnode)));

		allSteps.add(new PanelDetails("Hazelmere", Arrays.asList(climbUpToHazelmere, talkToHazelmere), barkSample, translationBook));

		allSteps.add(new PanelDetails("Investigation",
			Arrays.asList(bringScrollToKingNarnode, climbUpToGlough, talkToGlough, talkToKingNarnodeAfterGlough, talkToCharlie,
				findGloughJournal, talkToGloughAgain, talkToCharlieAgain, talkToKingNarnodeBeforeEscape, escapeByGlider)));

		allSteps.add(new PanelDetails("Karamja", Arrays.asList(enterTheShipyard, talkToForeman), oneThousandCoins));

		allSteps.add(new PanelDetails("Tuzo", Arrays.asList(talkToCharlieThirdTime, talkToAnita, findInvasionPlans,
			takeInvasionPlansToKing, placeTwigs), lumberOrder));

		allSteps.add(new PanelDetails("Encountering the Black Demon", Arrays.asList(climbDownTrapDoor, killBlackDemon,
			talkToKingAfterFight, findDaconiaStone, giveDaconiaStoneToKingNarnode), combatGear, food, prayerPotions));

		return allSteps;
	}
}
