/*
 * Copyright (c) 2022, rileyyy <https://github.com/rileyyy/> and Obasill <https://github.com/obasill/>
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
package com.questhelper.achievementdiaries.kourend;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.KOUREND_EASY
)

public class KourendEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement pickaxe, spade, coins, medpack, tarrominPotU, limpwurtRoot,
		flyFishingRod, feathers, libraryBook;

	// Items recommended
	ItemRequirement combatGear, food;

	// Quests required
	Requirement druidicRitual, hosidiusFavour;

	// Requirements
	Requirement notMineIron, notSandCrab, notArceuusBook, notStealFruit, notWarrensStore, notBoatLandsEnd, notPrayCastle,
		notDigSaltpetre, houseInKourend, notEnterPoh, hasMedpack, notHealSoldier, notStrengthPotion, notFishTrout;

	QuestStep sandCrab, stealFruit, warrensStore, enterCastleF1, enterCastleF2, prayCastle, digSaltpetre, enterPoh,
		collectMedpack, healSoldier, enterPub, strengthPotion, fishTrout, claimReward;

	ObjectStep enterWarrens, mineIron;

	NpcStep arceuusBook, boatLandsEnd, relocateHouse;

	ZoneRequirement inPub, inWarrens, inCastleF1, inCastleF2;

	Zone deeperLodePub, warrens, castleF1, castleF2;

	ConditionalStep mineIronTask, sandCrabTask, arceuusBookTask, stealFruitTask, warrensStoreTask, boatLandsEndTask,
		prayCastleTask, digSaltpetreTask, enterPohTask, healSoldierTask, strengthPotionTask, fishTroutTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		mineIronTask = new ConditionalStep(this, mineIron);
		doEasy.addStep(notMineIron, mineIronTask);

		fishTroutTask = new ConditionalStep(this, fishTrout);
		doEasy.addStep(notFishTrout, fishTroutTask);

		strengthPotionTask = new ConditionalStep(this, enterPub);
		strengthPotionTask.addStep(inPub, strengthPotion);
		doEasy.addStep(notStrengthPotion, strengthPotionTask);

		arceuusBookTask = new ConditionalStep(this, arceuusBook);
		doEasy.addStep(notArceuusBook, arceuusBookTask);

		warrensStoreTask = new ConditionalStep(this, enterWarrens);
		warrensStoreTask.addStep(inWarrens, warrensStore);
		doEasy.addStep(notWarrensStore, warrensStoreTask);

		boatLandsEndTask = new ConditionalStep(this, boatLandsEnd);
		doEasy.addStep(notBoatLandsEnd, boatLandsEndTask);

		stealFruitTask = new ConditionalStep(this, stealFruit);
		doEasy.addStep(notStealFruit, stealFruitTask);

		digSaltpetreTask = new ConditionalStep(this, digSaltpetre);
		doEasy.addStep(notDigSaltpetre, digSaltpetreTask);

		sandCrabTask = new ConditionalStep(this, sandCrab);
		doEasy.addStep(notSandCrab, sandCrabTask);

		enterPohTask = new ConditionalStep(this, relocateHouse);
		enterPohTask.addStep(houseInKourend, enterPoh);
		doEasy.addStep(notEnterPoh, enterPohTask);

		prayCastleTask = new ConditionalStep(this, enterCastleF1);
		prayCastleTask.addStep(inCastleF1, enterCastleF2);
		prayCastleTask.addStep(inCastleF2, prayCastle);
		doEasy.addStep(notPrayCastle, prayCastleTask);

		healSoldierTask = new ConditionalStep(this, collectMedpack);
		healSoldierTask.addStep(hasMedpack, healSoldier);
		doEasy.addStep(notHealSoldier, healSoldierTask);


		return doEasy;
	}

	@Override
	public void setupRequirements()
	{
		notMineIron = new VarplayerRequirement(2085, false, 1);
		notSandCrab = new VarplayerRequirement(2085, false, 2);
		notArceuusBook = new VarplayerRequirement(2085, false, 3);
		notStealFruit = new VarplayerRequirement(2085, false, 4);
		notWarrensStore = new VarplayerRequirement(2085, false, 5);
		notBoatLandsEnd = new VarplayerRequirement(2085, false, 6);
		notPrayCastle = new VarplayerRequirement(2085, false, 7);
		notDigSaltpetre = new VarplayerRequirement(2085, false, 8);
		notEnterPoh = new VarplayerRequirement(2085, false, 9);
		notHealSoldier = new VarplayerRequirement(2085, false, 10);
		notStrengthPotion = new VarplayerRequirement(2085, false, 11);
		notFishTrout = new VarplayerRequirement(2085, false, 12);

		// Required items
		pickaxe = new ItemRequirement("Pickaxe", ItemCollections.PICKAXES).showConditioned(notMineIron);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notDigSaltpetre);
		libraryBook = new ItemRequirement("Arceuus library book", ItemCollections.ARCEUUS_BOOKS).showConditioned(notArceuusBook);

		coins = new ItemRequirement("Coins", ItemCollections.COINS, 8075).showConditioned(notEnterPoh);
		medpack = new ItemRequirement("Medpacks", ItemID.SHAYZIEN_MEDPACK).showConditioned(notHealSoldier);
		tarrominPotU = new ItemRequirement("Tarromin potion (unf)", ItemID.TARROMIN_POTION_UNF).showConditioned(notStrengthPotion);
		limpwurtRoot = new ItemRequirement("Limpwurt root", ItemID.LIMPWURT_ROOT).showConditioned(notStrengthPotion);
		flyFishingRod = new ItemRequirement("Fly fishing rod", Arrays.asList(ItemID.FLY_FISHING_ROD, ItemID.PEARL_FLY_FISHING_ROD))
			.showConditioned(notFishTrout);
		feathers = new ItemRequirement("Feathers", ItemID.FEATHER, 10).showConditioned(notFishTrout);

		// Recommended items
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		inPub = new ZoneRequirement(deeperLodePub);
		inWarrens = new ZoneRequirement(warrens);
		inCastleF1 = new ZoneRequirement(castleF1);
		inCastleF2 = new ZoneRequirement(castleF2);

		// Required quests
		druidicRitual = new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED);

		// Zone requirements
		inPub = new ZoneRequirement(deeperLodePub);
		inWarrens = new ZoneRequirement(warrens);
		inCastleF1 = new ZoneRequirement(castleF1);
		inCastleF2 = new ZoneRequirement(castleF2);

		hasMedpack = medpack.alsoCheckBank(questBank);

		houseInKourend = new VarbitRequirement(2187, 8);
		hosidiusFavour = new FavourRequirement(Favour.HOSIDIUS, 15);
	}

	public void loadZones()
	{
		deeperLodePub = new Zone(new WorldPoint(1562, 3765, 0), new WorldPoint(1569, 3752, 0));
		warrens = new Zone(new WorldPoint(1728, 10176, 0), new WorldPoint(1816, 10109, 0));
		castleF1 = new Zone(new WorldPoint(1592, 3691, 1), new WorldPoint(1627, 3655, 1));
		castleF2 = new Zone(new WorldPoint(1592, 3691, 2), new WorldPoint(1627, 3655, 2));
	}

	public void setupSteps()
	{
		// Mine some iron
		mineIron = new ObjectStep(this, ObjectID.ROCKS_11364, new WorldPoint(1275, 3817, 0),
			"Mine some iron ore at the Mount Karuulm mine.", true, pickaxe);
		mineIron.addAlternateObjects(ObjectID.ROCKS_11365);

		// Kill a sand crab
		sandCrab = new NpcStep(this, NpcID.SAND_CRAB, new WorldPoint(1739, 3468, 0),
			"Kill a sand crab.", true, combatGear, food);

		// Hand in a book in the Arceuus library
		arceuusBook = new NpcStep(this, NpcID.PROFESSOR_GRACKLEBONE, new WorldPoint(1625, 3801, 0),
			"Collect a book for a patron in the Arceuus Library.", libraryBook);
		arceuusBook.addAlternateNpcs(NpcID.SAM_7049);
		arceuusBook.addAlternateNpcs(NpcID.VILLIA);

		// Steal from a Hosidius fruit stall
		stealFruit = new ObjectStep(this, ObjectID.FRUIT_STALL_28823, new WorldPoint(1767, 3597, 0),
			"Steal from a Hosidius fruit stall.");

		// Browse the Warrens general store
		enterWarrens = new ObjectStep(this, ObjectID.MANHOLE_31707, new WorldPoint(1813, 3745, 0),
			"Enter the Warrens.");
		enterWarrens.addAlternateObjects(ObjectID.MANHOLE_31706);
		warrensStore = new NpcStep(this, NpcID.SHOP_KEEPER_7913, new WorldPoint(1775, 10148, 0),
			"Browse the Warrens General Store.");

		// Take a boat from Land's End
		boatLandsEnd = new NpcStep(this, NpcID.CABIN_BOY_HERBERT, new WorldPoint(1826, 3691, 0),
			"Take a boat to Land's End", true);
		boatLandsEnd.addAlternateNpcs(NpcID.VEOS_10727);
		boatLandsEnd.addDialogStep("Can you take me somewhere?");
		boatLandsEnd.addDialogStep("Travel to Land's End.");

		// Pray at the Kourend castle altar
		enterCastleF1 = new ObjectStep(this, ObjectID.STAIRCASE_11807, new WorldPoint(1618, 3681, 0),
			"Climb the stairs to the second floor of the Kourend Castle.");
		enterCastleF2 = new ObjectStep(this, ObjectID.STAIRCASE_12536, new WorldPoint(1616, 3686, 1),
			"Climb the stairs to the third floor of the Kourend Castle.");
		prayCastle = new ObjectStep(this, ObjectID.ALTAR_18258, new WorldPoint(1616, 3672, 2),
			"Pray at the Kourend Castle altar.");
		prayCastle.addSubSteps(enterCastleF1, enterCastleF2);

		// Dig some Saltpetre
		digSaltpetre = new ObjectStep(this, ObjectID.SALTPETRE_27436, new WorldPoint(1703, 3526, 0),
			"Dig up some Saltpetre in Hosidius.", spade);

		// Enter your POH from Kourend
		relocateHouse = new NpcStep(this, NpcID.ESTATE_AGENT, new WorldPoint(1779, 3625, 0),
			"Relocate your player-owned house to Hosidius.", true, coins.quantity(8750));
		relocateHouse.addDialogStep(1, "Can you move my house please?");
		relocateHouse.addDialogStep(4, "Hosidius (8,750)");
		enterPoh = new ObjectStep(this, ObjectID.PORTAL_28822, new WorldPoint(1742, 3517, 0),
			"Enter your player-owned house from Hosidius.", houseInKourend);
		enterPoh.addSubSteps(relocateHouse);

		// Heal a wounded shayzien soldier
		collectMedpack = new ObjectStep(this, ObjectID.MEDPACK_BOX, new WorldPoint(1522, 3615, 0),
			"Collect a medpack in Shayzien.", true);
		healSoldier = new NpcStep(this, NpcID.WOUNDED_SOLDIER, new WorldPoint(1516, 3621, 0),
			"Heal a wounded shayzien soldier.", true, medpack);
		healSoldier.addSubSteps(collectMedpack);

		// Create a strength potion in the Lovakenji pub
		enterPub = new DetailedQuestStep(this, new WorldPoint(1564, 3759, 0), "Enter The Deeper Lode pub in Lovakengj.");
		strengthPotion = new ItemStep(this, "Create a strength Potion in The Deeper Lode pub.",
			tarrominPotU.highlighted(), limpwurtRoot.highlighted());

		// Fish trout from the Rover Molch
		fishTrout = new NpcStep(this, NpcID.ROD_FISHING_SPOT_8524, new WorldPoint(1267, 3706, 0),
			"Fish a trout from the River Molch.", flyFishingRod, feathers);

		// Claim rewards
		claimReward = new NpcStep(this, NpcID.ELISE, new WorldPoint(1647, 3665, 0),
			"Talk to Elise in the Kourend castle courtyard to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill a Sand Crab (level 15)");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(8750), pickaxe, spade, tarrominPotU, limpwurtRoot, flyFishingRod, feathers);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new SkillRequirement(Skill.CONSTRUCTION, 25));
		req.add(new SkillRequirement(Skill.FISHING, 20));
		req.add(new SkillRequirement(Skill.HERBLORE, 12));
		req.add(new SkillRequirement(Skill.MINING, 15));
		req.add(new SkillRequirement(Skill.THIEVING, 25));

		req.add(hosidiusFavour);
		req.add(druidicRitual);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Rada's Blessing (1)", ItemID.RADAS_BLESSING_1, 1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Halved access cost for Crabclaw Isle."),
			new UnlockReward("Doubled drop rate of Xeric's talisman, excluding stone chests."),
			new UnlockReward("Reduced tanning prices at Eodan in Forthos Dungeon to 80%."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails mineIronStep = new PanelDetails("Mine Iron Ore", Collections.singletonList(mineIron),
			pickaxe, new SkillRequirement(Skill.MINING, 15, true));
		mineIronStep.setDisplayCondition(notMineIron);
		mineIronStep.setLockingStep(mineIronTask);
		allSteps.add(mineIronStep);

		PanelDetails fishTroutStep = new PanelDetails("Fish A Trout", Collections.singletonList(fishTrout),
			new SkillRequirement(Skill.FISHING, 20), flyFishingRod, feathers);
		fishTroutStep.setDisplayCondition(notFishTrout);
		fishTroutStep.setLockingStep(fishTroutTask);
		allSteps.add(fishTroutStep);

		PanelDetails makePotionStep = new PanelDetails("Make A Strength Potion", Arrays.asList(enterPub,
			strengthPotion), new SkillRequirement(Skill.HERBLORE, 12), druidicRitual, tarrominPotU, limpwurtRoot);
		makePotionStep.setDisplayCondition(notStrengthPotion);
		makePotionStep.setLockingStep(strengthPotionTask);
		allSteps.add(makePotionStep);

		PanelDetails bookStep = new PanelDetails("Hand In A Book", Collections.singletonList(arceuusBook),
			libraryBook);
		bookStep.setDisplayCondition(notArceuusBook);
		bookStep.setLockingStep(arceuusBookTask);
		allSteps.add(bookStep);

		PanelDetails warrensStep = new PanelDetails("Browse The Warrens Store", Arrays.asList(enterWarrens,
			warrensStore));
		warrensStep.setDisplayCondition(notWarrensStore);
		warrensStep.setLockingStep(warrensStoreTask);
		allSteps.add(warrensStep);

		PanelDetails takeBoatStep = new PanelDetails("Boat To Land's End", Collections.singletonList(boatLandsEnd));
		takeBoatStep.setDisplayCondition(notBoatLandsEnd);
		takeBoatStep.setLockingStep(boatLandsEndTask);
		allSteps.add(takeBoatStep);

		PanelDetails stealStallStep = new PanelDetails("Steal Some Fruit", Collections.singletonList(stealFruit),
			new SkillRequirement(Skill.THIEVING, 25, true), hosidiusFavour);
		stealStallStep.setDisplayCondition(notStealFruit);
		stealStallStep.setLockingStep(stealFruitTask);
		allSteps.add(stealStallStep);

		PanelDetails digSaltpetreStep = new PanelDetails("Dig Up Some Saltpetre",
			Collections.singletonList(digSaltpetre), spade);
		digSaltpetreStep.setDisplayCondition(notDigSaltpetre);
		digSaltpetreStep.setLockingStep(digSaltpetreTask);
		allSteps.add(digSaltpetreStep);

		PanelDetails killCrabStep = new PanelDetails("Kill Sand Crab", Collections.singletonList(sandCrab),
			combatGear);
		killCrabStep.setDisplayCondition(notSandCrab);
		killCrabStep.setLockingStep(sandCrabTask);
		allSteps.add(killCrabStep);

		PanelDetails enterPohStep = new PanelDetails("Hosidius House", Arrays.asList(relocateHouse,
			enterPoh), new SkillRequirement(Skill.CONSTRUCTION, 25), coins.quantity(8750));
		enterPohStep.setDisplayCondition(notEnterPoh);
		enterPohStep.setLockingStep(enterPohTask);
		allSteps.add(enterPohStep);

		PanelDetails prayStep = new PanelDetails("Pray At Kourend Castle", Arrays.asList(enterCastleF1, enterCastleF2,
			prayCastle));
		prayStep.setDisplayCondition(notPrayCastle);
		prayStep.setLockingStep(prayCastleTask);
		allSteps.add(prayStep);

		PanelDetails healSoldierStep = new PanelDetails("Heal A Soldier", Arrays.asList(collectMedpack, healSoldier),
			medpack);
		healSoldierStep.setDisplayCondition(notHealSoldier);
		healSoldierStep.setLockingStep(healSoldierTask);
		allSteps.add(healSoldierStep);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
