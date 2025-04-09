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
package com.questhelper.helpers.quests.thegrandtree;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.util.QHObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TheGrandTree extends BasicQuestHelper
{
	ItemRequirement accessToFairyRings, energyOrStaminaPotions, transportToGrandTree, oneThousandCoins, combatGear,
		food, prayerPotions, translationBook, barkSample, lumberOrder, gloughsKey, highlightedGloughsKey, invasionPlans,
		twigsT, twigsU, twigsZ, twigsO, highlightedTwigsT, highlightedTwigsU, highlightedTwigsZ, highlightedTwigsO,
		daconiaStone;

	FreeInventorySlotRequirement fourFreeInventorySlots;

	Zone grandTreeF1, grandTreeF2, topOfGrandTree, hazelmereHouseFirstFloor, gloughHouse, shipyard, anitaHouse,
		watchtower, grandTreeTunnels, cell;

	Requirement isInGrandTreeF1, isInGrandTreeF2, isInGrandTreeTop, isInGrandTree, isNearHazelmere,
		isInCell, isInGloughsHouse, isInShipyard, isInAnitasHouse, isInWatchtower, blackDemonVisible,
		isInGrandTreeTunnels, narnodeNearby;

	QuestStep talkToKingNarnode, talkToKingNarnodeCaves, climbUpToHazelmere, talkToHazelmere, bringScrollToKingNarnode, climbUpToGlough,
		talkToGlough, talkToKingNarnodeAfterGlough, talkToCharlie, returnToGlough, findGloughJournal, talkToGloughAgain, goToStronghold,
		talkToCharlieFromCell, talkToKingNarnodeBeforeEscape, escapeByGlider, enterTheShipyard, talkToForeman, climbUpToAnita,
		talkToAnita, climbUpToGloughAgain, findInvasionPlans, takeInvasionPlansToKing, climbUpToGloughForWatchtower, climbUpToWatchtower,
		placeTwigsT, placeTwigsU, placeTwigsZ, placeTwigsO, placeTwigs, climbDownTrapDoor, talkToGloughBeforeFight, killBlackDemon,
		climbDownTrapDoorAfterFight, talkToKingAfterFight, findDaconiaStone, giveDaconiaStoneToKingNarnode;

	QuestStep climbGrandTreeF0ToF1, climbGrandTreeF1ToF2, climbGrandTreeF2ToF3, climbGrandTreeF3ToF2,
		climbGrandTreeF2ToF1, climbGrandTreeF1ToF0;

	ConditionalStep climbToTopOfGrandTree, climbToBottomOfGrandTree, goTalkToCharlie, goFindGloughJournal,
		goConfrontGlough, goTalkToCharlie3, goGetAnitaKey;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		// Getting started
		ConditionalStep startQuest = new ConditionalStep(this, talkToKingNarnode);
		startQuest.addStep(isInGrandTreeTunnels, talkToKingNarnodeCaves);
		steps.put(0, startQuest);

		ConditionalStep goTalkToHazelmere = new ConditionalStep(this, climbUpToHazelmere);
		goTalkToHazelmere.addStep(isNearHazelmere, talkToHazelmere);
		steps.put(10, goTalkToHazelmere);

		steps.put(20, bringScrollToKingNarnode);

		ConditionalStep goTalkToGlough = new ConditionalStep(this, climbUpToGlough);
		goTalkToGlough.addStep(isInGloughsHouse, talkToGlough);
		steps.put(30, goTalkToGlough);

		// Investigation
		steps.put(40, talkToKingNarnodeAfterGlough);

		goTalkToCharlie = new ConditionalStep(this, climbToTopOfGrandTree,
			"Talk to Charlie on the top floor of the Grand Tree.");
		goTalkToCharlie.addStep(isInGrandTreeTop, talkToCharlie);
		steps.put(50, goTalkToCharlie);

		goFindGloughJournal = new ConditionalStep(this, returnToGlough, "Return to Glough's house and search the " +
			"cupboard for Glough's Journal.");
		goFindGloughJournal.addStep(isInGrandTree, climbToBottomOfGrandTree);
		goFindGloughJournal.addStep(isInGloughsHouse, findGloughJournal);
		steps.put(60, goFindGloughJournal);

		goConfrontGlough = new ConditionalStep(this, returnToGlough, "Confront Glough about his journal.");
		goConfrontGlough.addStep(isInGrandTree, climbToBottomOfGrandTree);
		goConfrontGlough.addStep(isInGloughsHouse, talkToGloughAgain);

		// In order for Narnode to appear, you need to finish talking to Charlie from within the cell.
		// Talking from outside won't cause Narnode to spawn. Narnode can only then be interacted with from
		// Outside the cell. Should verify if talking to Narnode downstairs at this stage works for progression.
		ConditionalStep goLearnOfDock = new ConditionalStep(this, goConfrontGlough);
		goLearnOfDock.addStep(isInCell, talkToCharlieFromCell);
		goLearnOfDock.addStep(new Conditions(isInGrandTreeTop, narnodeNearby), talkToKingNarnodeBeforeEscape);
		steps.put(70, goLearnOfDock);

		// Karamja
		ConditionalStep goTalkToForeman = new ConditionalStep(this, enterTheShipyard);
		goTalkToForeman.addStep(isInGrandTreeTop, escapeByGlider);
		goTalkToForeman.addStep(isInShipyard, talkToForeman);
		steps.put(80, goTalkToForeman);

		// Tuzo
		goTalkToCharlie3 = goTalkToCharlie.copy();
		if (QuestHelperQuest.TREE_GNOME_VILLAGE.getState(client, configManager) == QuestState.FINISHED)
		{
			goTalkToCharlie3.setText("Return to Charlie. You won't be able to enter through the main entrance, so " +
				"speak to Femi outside the main gate to be snuck in, or make use of a Spirit Tree to enter the Tree Gnome Stronghold." +
				"The easiest Spirit Tree to use is the one in the Grand Exchange.");
		}
		else
		{
			goTalkToCharlie3.setText("Return to Charlie. If entering the Stronghold through the south entrance you'll " +
				"need to talk to Femi there. If you didn't help them previously you'll need to pay them 1k.");
		}
		goTalkToCharlie3.addRequirement(lumberOrder);
		goTalkToCharlie3.addRequirement(oneThousandCoins);
		steps.put(90, goTalkToCharlie3);

		goGetAnitaKey = new ConditionalStep(this, climbUpToAnita, "Talk to Anita in her house west of the Grand Tree.");
		goGetAnitaKey.addStep(isInAnitasHouse, talkToAnita);
		goGetAnitaKey.addStep(isInGrandTree, climbToBottomOfGrandTree);

		ConditionalStep goFindInvasionPlans = new ConditionalStep(this, goGetAnitaKey);
		goFindInvasionPlans.addStep(new Conditions(gloughsKey, isInGloughsHouse), findInvasionPlans);
		goFindInvasionPlans.addStep(gloughsKey, climbUpToGloughAgain);
		steps.put(100, goFindInvasionPlans);

		steps.put(110, takeInvasionPlansToKing);

		ConditionalStep goUpToWatchtower = new ConditionalStep(this, climbUpToGloughForWatchtower);
		goUpToWatchtower.addStep(isInGloughsHouse, climbUpToWatchtower);

		ConditionalStep goPlaceTuzoTwigs = new ConditionalStep(this, goUpToWatchtower);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, twigsT), placeTwigsT);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, twigsU), placeTwigsU);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, twigsZ), placeTwigsZ);
		goPlaceTuzoTwigs.addStep(new Conditions(isInWatchtower, twigsO), placeTwigsO);
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
		bringDaconiaStoneToKing.addStep(daconiaStone, giveDaconiaStoneToKingNarnode);
		bringDaconiaStoneToKing.addStep(isInGrandTreeTunnels, findDaconiaStone);
		steps.put(150, bringDaconiaStoneToKing);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		oneThousandCoins = new ItemRequirement("Coins to enter the Stronghold if you didn't help Femi previously", ItemCollections.COINS, 1000)
			.hideConditioned(new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED));

		accessToFairyRings = new ItemRequirement("Access to Fairy Rings", ItemCollections.FAIRY_STAFF).isNotConsumed();

		energyOrStaminaPotions = new ItemRequirement("Energy restoration", ItemCollections.RUN_RESTORE_ITEMS, -1);
		combatGear = new ItemRequirement("Combat gear. Safespotting is possible.", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		transportToGrandTree = new ItemRequirement("Transport to the Grand Tree", ItemCollections.NECKLACE_OF_PASSAGES);

		translationBook = new ItemRequirement("Translation Book", ItemID.GRANDTREE_TRANSLATIONBOOK);
		translationBook.setTooltip("You can get another from Narnode");
		barkSample = new ItemRequirement("Bark Sample", ItemID.GRANDTREE_BARKSAMPLE);
		barkSample.setTooltip("You can get another from Narnode");
		lumberOrder = new ItemRequirement("Lumber order", ItemID.GRANDTREE_ORDER);
		gloughsKey = new ItemRequirement("Glough's key", ItemID.GRANDTREE_GLOUGHSKEY);
		highlightedGloughsKey = new ItemRequirement("Glough's key", ItemID.GRANDTREE_GLOUGHSKEY);
		highlightedGloughsKey.setHighlightInInventory(true);
		invasionPlans = new ItemRequirement("Invasion plans", ItemID.GRANDTREE_INVASIONPLANS);
		invasionPlans.setTooltip("You can get another from Glough's house in a chest");
		fourFreeInventorySlots = new FreeInventorySlotRequirement(4);
		daconiaStone = new ItemRequirement("Daconia stone", ItemID.GRANDTREE_DACONIAROCK);

		twigsT = new ItemRequirement("Twigs (T)", ItemID.GRANDTREE_TWIGT);
		twigsU = new ItemRequirement("Twigs (U)", ItemID.GRANDTREE_TWIGU);
		twigsZ = new ItemRequirement("Twigs (Z)", ItemID.GRANDTREE_TWIGZ);
		twigsO = new ItemRequirement("Twigs (O)", ItemID.GRANDTREE_TWIGO);

		highlightedTwigsT = new ItemRequirement("Twigs (T)", ItemID.GRANDTREE_TWIGT);
		highlightedTwigsT.setHighlightInInventory(true);
		highlightedTwigsU = new ItemRequirement("Twigs (U)", ItemID.GRANDTREE_TWIGU);
		highlightedTwigsU.setHighlightInInventory(true);
		highlightedTwigsZ = new ItemRequirement("Twigs (Z)", ItemID.GRANDTREE_TWIGZ);
		highlightedTwigsZ.setHighlightInInventory(true);
		highlightedTwigsO = new ItemRequirement("Twigs (O)", ItemID.GRANDTREE_TWIGO);
		highlightedTwigsO.setHighlightInInventory(true);
	}

	@Override
	protected void setupZones()
	{
		grandTreeF1 = new Zone(new WorldPoint(2437, 3474, 1), new WorldPoint(2493, 3511, 1));
		grandTreeF2 = new Zone(new WorldPoint(2437, 3474, 2), new WorldPoint(2493, 3511, 2));
		topOfGrandTree = new Zone(new WorldPoint(2438, 3511, 3), new WorldPoint(2493, 3478, 3));
		cell = new Zone(new WorldPoint(2464, 3496, 3));
		hazelmereHouseFirstFloor = new Zone(new WorldPoint(2674, 3089, 1), new WorldPoint(2680, 3085, 1));
		gloughHouse = new Zone(new WorldPoint(2474, 3466, 1), new WorldPoint(2488, 3461, 1));
		shipyard = new Zone(new WorldPoint(2945, 3070, 0), new WorldPoint(3007, 3015, 0));
		anitaHouse = new Zone(new WorldPoint(2386, 3517, 1), new WorldPoint(2392, 3512, 1));
		watchtower = new Zone(new WorldPoint(2482, 3469, 2), new WorldPoint(2491, 3459, 2));
		grandTreeTunnels = new Zone(new WorldPoint(2431, 9920, 0), new WorldPoint(2500, 9856, 0));
	}

	public void setupConditions()
	{
		isInGrandTreeF1 = new ZoneRequirement(grandTreeF1);
		isInGrandTreeF2 = new ZoneRequirement(grandTreeF2);
		isInGrandTreeTop = new ZoneRequirement(topOfGrandTree);
		isInCell = new ZoneRequirement(cell);
		isInGrandTree = new ZoneRequirement(grandTreeF1, grandTreeF2, topOfGrandTree);
		isNearHazelmere = new ZoneRequirement(hazelmereHouseFirstFloor);
		isInGloughsHouse = new ZoneRequirement(gloughHouse);
		isInShipyard = new ZoneRequirement(shipyard);
		isInAnitasHouse = new ZoneRequirement(anitaHouse);
		isInWatchtower = new ZoneRequirement(watchtower);
		isInGrandTreeTunnels = new ZoneRequirement(grandTreeTunnels);

		narnodeNearby = new NpcCondition(NpcID.GRANDTREE_NARNODE_1OP);
		blackDemonVisible = new NpcInteractingRequirement(NpcID.GRANDTREE_BLACKDEMON);
	}

	public void setupSteps()
	{
		WorldPoint locationBottomOfGrandTree = new WorldPoint(2466, 3495, 0);
		WorldPoint locationTopOfGrandTree = new WorldPoint(2466, 3495, 3);

		goToStronghold = new DetailedQuestStep(this, locationBottomOfGrandTree, "Travel to the Tree Gnome Stronghold.");

		// Getting Started
		talkToKingNarnode = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, locationBottomOfGrandTree, "Talk to King Narnode Shareen in the Grand Tree" +
			"(Make sure to have two empty inventory slots to start the quest).");
		talkToKingNarnode.addDialogSteps("You seem worried, what's up?", "I'd be happy to help!");
		talkToKingNarnodeCaves = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, new WorldPoint(2465, 9895, 0),
			"Talk to King Narnode.");
		talkToKingNarnodeCaves.addDialogStep("I'd be happy to help!");
		talkToKingNarnode.addSubSteps(talkToKingNarnodeCaves);

		// Hazelmere
		climbUpToHazelmere = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2677, 3087, 0),
			"Go up to Hazelmere, on the island east of Yanille. Fairy ring CLS or minigame teleport near Yanille.", translationBook, barkSample);
		talkToHazelmere = new NpcStep(this, NpcID.GRANDTREE_HAZELMERE, "Talk to Hazelmere.", translationBook, barkSample);

		bringScrollToKingNarnode = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, locationBottomOfGrandTree, "Return to King Narnode in the Grand Tree.");
		bringScrollToKingNarnode.addDialogStep("I think so!");
		bringScrollToKingNarnode.addDialogStepWithExclusions("None of the above.", "A man came to me with the King's seal.", "I gave the man Daconia rocks.");
		bringScrollToKingNarnode.addDialogStep("A man came to me with the King's seal.");
		bringScrollToKingNarnode.addDialogStep("I gave the man Daconia rocks.");
		bringScrollToKingNarnode.addDialogStep("And Daconia rocks will kill the tree!");

		// Investigation
		climbUpToGlough = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2476, 3463, 0),
			"Go up to Glough in the Tree Gnome Stronghold.");
		talkToGlough = new NpcStep(this, NpcID.GRANDTREE_GLOUGH_VISIBLE, "Talk to Glough.");
		talkToGlough.addSubSteps(climbUpToGlough);

		talkToKingNarnodeAfterGlough = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, locationBottomOfGrandTree,
			"Talk to King Narnode again in the Grand Tree.");
		talkToCharlie = new NpcStep(this, NpcID.GRANDTREE_CHARLIE, new WorldPoint(2464, 3495, 3), "");

		returnToGlough = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2476, 3463, 0), "");
		findGloughJournal = new ObjectStep(this, ObjectID.GRANDTREE_CUPBOARDCLOSED, "");
		((ObjectStep) findGloughJournal).addAlternateObjects(ObjectID.GRANDTREE_CUPBOARDOPEN);

		talkToGloughAgain = new NpcStep(this, NpcID.GRANDTREE_GLOUGH_VISIBLE, "");

		climbGrandTreeF0ToF1 = new ObjectStep(this, QHObjectID.GRAND_TREE_F0_LADDER, new WorldPoint(2466, 3495, 0), "");
		climbGrandTreeF1ToF2 = new ObjectStep(this, QHObjectID.GRAND_TREE_F1_LADDER, new WorldPoint(2466, 3495, 1), "");
		climbGrandTreeF1ToF2.addDialogStep("Climb Up.");
		climbGrandTreeF2ToF3 = new ObjectStep(this, QHObjectID.GRAND_TREE_F2_LADDER, new WorldPoint(2466, 3495, 2), "");
		climbGrandTreeF2ToF3.addDialogStep("Climb Up.");

		climbToTopOfGrandTree = new ConditionalStep(this, climbGrandTreeF0ToF1);
		climbToTopOfGrandTree.addStep(isInGrandTreeF2, climbGrandTreeF2ToF3);
		climbToTopOfGrandTree.addStep(isInGrandTreeF1, climbGrandTreeF1ToF2);

		climbGrandTreeF3ToF2 = new ObjectStep(this, QHObjectID.GRAND_TREE_F3_LADDER, new WorldPoint(2466, 3495, 3), "");
		climbGrandTreeF2ToF1 = new ObjectStep(this, QHObjectID.GRAND_TREE_F2_LADDER, new WorldPoint(2466, 3495, 2), "");
		climbGrandTreeF2ToF1.addDialogStep("Climb Down.");
		climbGrandTreeF1ToF0 = new ObjectStep(this, QHObjectID.GRAND_TREE_F1_LADDER, new WorldPoint(2466, 3495, 1), "");
		climbGrandTreeF1ToF0.addDialogStep("Climb Down.");

		climbToBottomOfGrandTree = new ConditionalStep(this, goToStronghold);
		climbToBottomOfGrandTree.addStep(isInGrandTreeTop, climbGrandTreeF3ToF2);
		climbToBottomOfGrandTree.addStep(isInGrandTreeF2, climbGrandTreeF2ToF1);
		climbToBottomOfGrandTree.addStep(isInGrandTreeF1, climbGrandTreeF1ToF0);

		talkToCharlieFromCell = new NpcStep(this, NpcID.GRANDTREE_CHARLIE, new WorldPoint(2464, 3495, 3), "Talk to Charlie.");
		talkToKingNarnodeBeforeEscape = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP,
			"Talk to King Narnode outside Charlie's cell.");
		escapeByGlider = new NpcStep(this, NpcID.PILOT_GRAND_TREE_BASE, locationTopOfGrandTree, "Travel with the glider to escape.");
		escapeByGlider.addDialogStep("Take me to Karamja please!");

		// Karamja
		enterTheShipyard = new ObjectStep(this, ObjectID.GRANDTREE_FENCEGATE_L, new WorldPoint(2945, 3041, 0), "Enter the shipyard on Karamja.");
		enterTheShipyard.addDialogSteps("Glough sent me.", "Ka.", "Lu.", "Min.");
		talkToForeman = new NpcStep(this, NpcID.GRANDTREE_FOREMAN, new WorldPoint(3000, 3044, 0),
			"Get the Lumber order from the Foreman on the southern docks. You can either kill him or talk to him.");
		talkToForeman.addDialogSteps("Sadly his wife is no longer with us!", "He loves worm holes.", "Anita.");

		climbUpToAnita = new ObjectStep(this, ObjectID.SPIRALSTAIRS_WOODEN, new WorldPoint(2390, 3513, 0), "");
		talkToAnita = new NpcStep(this, NpcID.GT_ANITA, "");
		talkToAnita.addDialogStep("I suppose so.");

		climbUpToGloughAgain = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2476, 3463, 0), "Go up to Glough's house again.");
		findInvasionPlans = new ObjectStep(this, ObjectID.GRANDTREE_CHESTCLOSED, "Search the chest in Glough's " +
			"house.",
			highlightedGloughsKey);
		findInvasionPlans.addSubSteps(climbUpToGloughAgain);
		findInvasionPlans.addIcon(ItemID.GRANDTREE_GLOUGHSKEY);

		takeInvasionPlansToKing = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, new WorldPoint(2465, 3496, 0),
			"Take the invasion plans to King Narnode in the Grand Tree.",
			invasionPlans, fourFreeInventorySlots);

		climbUpToGloughForWatchtower = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(2476, 3463, 0), "Go up to Glough's house again.");
		climbUpToWatchtower = new ObjectStep(this, ObjectID.GRANDTREE_CLIMBTREE, "Climb up the tree to the watchtower in Glough's house.");

		placeTwigsT = new ObjectStep(this, ObjectID.GRANDTREE_PILLART, "Place the twigs (T). Return to King Narnode if you lost them.", highlightedTwigsT);
		placeTwigsT.addIcon(ItemID.GRANDTREE_TWIGT);

		placeTwigsU = new ObjectStep(this, ObjectID.GRANDTREE_PILLARU, "Place the twigs (U). Return to King Narnode if you lost them.", highlightedTwigsU);
		placeTwigsU.addIcon(ItemID.GRANDTREE_TWIGU);

		placeTwigsZ = new ObjectStep(this, ObjectID.GRANDTREE_PILLARZ, "Place the twigs (Z). Return to King Narnode if you lost them.", highlightedTwigsZ);
		placeTwigsZ.addIcon(ItemID.GRANDTREE_TWIGZ);

		placeTwigsO = new ObjectStep(this, ObjectID.GRANDTREE_PILLARO, "Place the twigs (O). Return to King Narnode if you lost them.", highlightedTwigsO);
		placeTwigsO.addIcon(ItemID.GRANDTREE_TWIGO);

		placeTwigs = new DetailedQuestStep(this, "Place the twigs to spell 'TUZO' in the watchtower in Glough's house. Return to King Narnode if you lost any.",
			twigsT, twigsU, twigsZ, twigsO);
		placeTwigs.addSubSteps(climbUpToGloughForWatchtower, climbUpToWatchtower, placeTwigsT, placeTwigsU, placeTwigsZ, placeTwigsO);

		// The black demon
		climbDownTrapDoor = new ObjectStep(this, ObjectID.GRANDTREE_TRAPDOORTOWEROPEN, "Go down the trap door. Be prepared for the fight against a Black Demon (level 172).");
		talkToGloughBeforeFight = new NpcStep(this, NpcID.GRANDTREE_GLOUGH_BATTLE, "Talk to Glough. You can safespot the Demon from where he stands.");
		killBlackDemon = new NpcStep(this, NpcID.GRANDTREE_BLACKDEMON, "Kill the black Demon. You can safespot from where Glough stands.");
		((NpcStep) killBlackDemon).addSafeSpots(new WorldPoint(2492, 9865, 0));
		climbDownTrapDoorAfterFight = new ObjectStep(this, ObjectID.GRANDTREE_TRAPDOORTOWEROPEN, "Go down the trap door again.");
		talkToKingAfterFight = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, new WorldPoint(2465, 9895, 0), "Talk to King Narnode deeper in the cave.");
		talkToKingAfterFight.addSubSteps(climbDownTrapDoorAfterFight);
		giveDaconiaStoneToKingNarnode = new NpcStep(this, NpcID.GRANDTREE_NARNODE_1OP, new WorldPoint(2465, 9895, 0),
			"Give the Daconia stone to King Narnode under the Grand Tree.", daconiaStone);

		findDaconiaStone = new ObjectStep(this, ObjectID.LARGEROOT_GNOME,
			"Search the roots under the Grand Tree until you find the Daconia stone. If you lose the rock, it will be in the same root again.");
		((ObjectStep) findDaconiaStone).addAlternateObjects(ObjectID.LARGEROOT2_GNOME);
		((ObjectStep) findDaconiaStone).setHideWorldArrow(true);
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
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(5);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.ATTACK, 18400),
				new ExperienceReward(Skill.AGILITY, 7900),
				new ExperienceReward(Skill.MAGIC, 2150));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to use the Spirit Tree in the Tree Gnome Stronghold."));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Getting started", Collections.singletonList(talkToKingNarnode)));

		allSteps.add(new PanelDetails("Hazelmere", Arrays.asList(climbUpToHazelmere, talkToHazelmere), barkSample, translationBook));

		allSteps.add(new PanelDetails("Investigation",
			Arrays.asList(bringScrollToKingNarnode, climbUpToGlough, talkToGlough, talkToKingNarnodeAfterGlough, goTalkToCharlie,
				goFindGloughJournal, goConfrontGlough, talkToCharlieFromCell, talkToKingNarnodeBeforeEscape,
				escapeByGlider)));

		allSteps.add(new PanelDetails("Karamja", Arrays.asList(enterTheShipyard, talkToForeman), oneThousandCoins));

		allSteps.add(new PanelDetails("Tuzo", Arrays.asList(goTalkToCharlie3, goGetAnitaKey, findInvasionPlans,
			takeInvasionPlansToKing, placeTwigs)));

		allSteps.add(new PanelDetails("Defeat the Black Demon", Arrays.asList(climbDownTrapDoor, killBlackDemon,
			talkToKingAfterFight, findDaconiaStone, giveDaconiaStoneToKingNarnode), combatGear, food, prayerPotions));

		return allSteps;
	}
}
