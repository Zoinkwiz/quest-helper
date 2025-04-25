/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.helpers.achievementdiaries.fremennik;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpecialAttackRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.SpecialAttack;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FremennikMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement coins, spade, rope, pickaxe, staff, butterFlyJar, butterFlyNet, petRock, goldHelm, oakPlanks,
		saw, hammer, thrownaxe, combatGear, coinsForFerry;

	// Recommended
	ItemRequirement food, prayerPot, stamPot;

	Requirement notSlayBrineRat, notSnowyHunter, notMineCoal, notStealFish, notTravelMisc, notSnowyKnight,
		notPetRockPOH, notLighthouse, notMineGold;

	// Quest requirements
	Requirement horrorFromTheDeep, eaglesPeak, fairyTaleI, lostCity, natureSpirit, fairyTaleII, olafsQuest,
		fremennikTrials, betweenARock, dwarfCannon, fishingContest;

	// Steps
	QuestStep slayBrineRat, enterEaglesPeak, snowyHunter, exitIceCave, mineCoal, stealFish,
		travelMisc, snowyKnight0, snowyKnight1, petRockPOH, moveToCannon, moveToCave, moveToRiver, moveToArzinian,
		mineGold, lighthouse, moveToWaterbirth, moveToDagCave, moveToAxeSpot, throwAxe, moveToDagCave1,
		moveToDagCave2, moveToDagCave3, moveToDagCave4, moveToDagCave5, moveToDagCave6, moveToDagCave7,
		moveToDagCave8, moveToDagCave9, moveToDagCave10, moveToDagCave11, moveToDagCave12, moveToDagCave13,
		moveToDagCave14, moveToDagCave15, claimReward, activateSpecial;

	ObjectStep dropPetRock;

	DigStep enterBrineCave;

	Zone eagleArea, iceCave, hunterArea0, hunterArea1, riverArea, riverArea2, caveArea, arzinianMine,
		waterbirthIsland, brineRatCave, dagCave, dagCave1, dagCave_2, dagCave_3, dagCave_4, dagCave2, dagCave3,
		dagCave4, dagCave5, dagCave6, dagCave7, dagCave8, dagCave9, dagCave10, dagCave11, dagCave12, dagCave13,
		dagCave14, dagCave15;

	ZoneRequirement inEagleArea, inIceCave, inHunterArea0, inHunterArea1, inRiverArea, inCannonArea, inCaveArea,
		inArzinianMine, inWaterbirthIsland, inDagCave, inBrineRatCave, inDagCave1, inDagCave_2, inDagCave_3,
		inDagCave_4, inDagCave2, inDagCave3, inDagCave4, inDagCave5, inDagCave6, inDagCave7, inDagCave8, inDagCave9,
		inDagCave10, inDagCave11, inDagCave12, inDagCave13, inDagCave14, inDagCave15;

	Requirement protectMelee, protectMissiles, protectMagic, specialAttackEnabled;

	ConditionalStep slayBrineRatTask, snowyHunterTask, mineCoalTask, stealFishTask, travelMiscTask, snowyKnightTask,
		petRockPOHTask, lighthouseTask, mineGoldTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		slayBrineRatTask = new ConditionalStep(this, enterBrineCave);
		slayBrineRatTask.addStep(inBrineRatCave, slayBrineRat);
		doMedium.addStep(notSlayBrineRat, slayBrineRatTask);

		travelMiscTask = new ConditionalStep(this, travelMisc);
		doMedium.addStep(notTravelMisc, travelMiscTask);

		snowyHunterTask = new ConditionalStep(this, enterEaglesPeak);
		snowyHunterTask.addStep(inEagleArea, snowyHunter);
		snowyHunterTask.addStep(inIceCave, exitIceCave);
		doMedium.addStep(notSnowyHunter, snowyHunterTask);

		snowyKnightTask = new ConditionalStep(this, snowyKnight0);
		snowyKnightTask.addStep(inHunterArea1, snowyKnight1);
		snowyKnightTask.addStep(inHunterArea0, snowyKnight0);
		doMedium.addStep(notSnowyKnight, snowyKnightTask);

		mineGoldTask = new ConditionalStep(this, moveToCave);
		mineGoldTask.addStep(inCaveArea, moveToRiver);
		mineGoldTask.addStep(inRiverArea, moveToCannon);
		mineGoldTask.addStep(inCannonArea, moveToArzinian);
		mineGoldTask.addStep(inArzinianMine, mineGold);
		doMedium.addStep(notMineGold, mineGoldTask);

		mineCoalTask = new ConditionalStep(this, mineCoal);
		doMedium.addStep(notMineCoal, mineCoalTask);

		stealFishTask = new ConditionalStep(this, stealFish);
		doMedium.addStep(notStealFish, stealFishTask);

		lighthouseTask = new ConditionalStep(this, moveToWaterbirth);
		lighthouseTask.addStep(inWaterbirthIsland, moveToDagCave);
		lighthouseTask.addStep(inDagCave, dropPetRock);
		lighthouseTask.addStep(inDagCave_2, moveToAxeSpot);
		lighthouseTask.addStep(new Conditions(inDagCave_3, specialAttackEnabled), throwAxe);
		lighthouseTask.addStep(inDagCave_3, activateSpecial);
		lighthouseTask.addStep(inDagCave_4, moveToDagCave1);
		lighthouseTask.addStep(inDagCave1, moveToDagCave2);
		lighthouseTask.addStep(inDagCave2, moveToDagCave3);
		lighthouseTask.addStep(inDagCave3, moveToDagCave4);
		lighthouseTask.addStep(inDagCave4, moveToDagCave5);
		lighthouseTask.addStep(inDagCave5, moveToDagCave6);
		lighthouseTask.addStep(inDagCave6, moveToDagCave7);
		lighthouseTask.addStep(inDagCave7, moveToDagCave8);
		lighthouseTask.addStep(inDagCave8, moveToDagCave9);
		lighthouseTask.addStep(inDagCave9, moveToDagCave10);
		lighthouseTask.addStep(inDagCave10, moveToDagCave11);
		lighthouseTask.addStep(inDagCave11, moveToDagCave12);
		lighthouseTask.addStep(inDagCave12, moveToDagCave13);
		lighthouseTask.addStep(inDagCave13, moveToDagCave14);
		lighthouseTask.addStep(inDagCave14, moveToDagCave15);
		lighthouseTask.addStep(inDagCave15, lighthouse);
		doMedium.addStep(notLighthouse, lighthouseTask);

		petRockPOHTask = new ConditionalStep(this, petRockPOH);
		doMedium.addStep(notPetRockPOH, petRockPOHTask);

		return doMedium;
	}

	@Override
	protected void setupRequirements()
	{
		notSlayBrineRat = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 11);
		notSnowyHunter = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 12);
		notMineCoal = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 13);
		notStealFish = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 14);
		notTravelMisc = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 15);
		notSnowyKnight = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 17);
		notPetRockPOH = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 18);
		notLighthouse = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 19);
		notMineGold = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 20);

		specialAttackEnabled = new SpecialAttackRequirement(SpecialAttack.ON);

		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notPetRockPOH);
		coinsForFerry = new ItemRequirement("Coins", ItemCollections.COINS);
		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notSnowyHunter);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notSlayBrineRat).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(
			new Conditions(LogicType.OR, notMineGold, notMineCoal)).isNotConsumed();
		staff = new TeleportItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF).showConditioned(notTravelMisc).isNotConsumed();
		butterFlyJar = new ItemRequirement("Butterfly Jar", ItemID.BUTTERFLY_JAR).showConditioned(notSnowyKnight).isNotConsumed();
		butterFlyNet = new ItemRequirement("Butterfly Net", ItemID.HUNTING_BUTTERFLY_NET).showConditioned(notSnowyKnight).isNotConsumed();
		butterFlyNet.addAlternates(ItemID.II_MAGIC_BUTTERFLY_NET);
		butterFlyNet.setTooltip("Alternatively, use Magic Butterfly Net");
		petRock = new ItemRequirement("Pet rock", ItemID.VT_USELESS_ROCK).showConditioned(new Conditions(LogicType.OR,
			notPetRockPOH, notLighthouse)).isNotConsumed();
		petRock.setTooltip("Obtained from Askeladden in Rellekka");
		goldHelm = new ItemRequirement("Gold helmet", ItemID.DWARF_GOLDROCK_HELMET).showConditioned(notMineGold).isNotConsumed();
		oakPlanks = new ItemRequirement("Oak planks", ItemID.PLANK_OAK).showConditioned(notPetRockPOH);
		saw = new ItemRequirement("Saw", ItemID.POH_SAW).showConditioned(notPetRockPOH).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notPetRockPOH).isNotConsumed();
		thrownaxe = new ItemRequirement("Rune thrownaxe", ItemID.RUNE_THROWNAXE).showConditioned(notLighthouse);

		combatGear = new ItemRequirement("Combat gear", -1, -1).showConditioned(new Conditions(LogicType.OR,
			notSlayBrineRat, notLighthouse)).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		prayerPot = new ItemRequirement("Prayer Potions", ItemCollections.PRAYER_POTIONS, -1);
		stamPot = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS, -1);

		protectMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
		protectMissiles = new PrayerRequirement("Protect from Missiles", Prayer.PROTECT_FROM_MISSILES);
		protectMagic = new PrayerRequirement("Protect from Magic", Prayer.PROTECT_FROM_MAGIC);


		eaglesPeak = new QuestRequirement(QuestHelperQuest.EAGLES_PEAK, QuestState.FINISHED);
		fairyTaleI = new QuestRequirement(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS, QuestState.FINISHED);
		lostCity = new QuestRequirement(QuestHelperQuest.LOST_CITY, QuestState.FINISHED);
		natureSpirit = new QuestRequirement(QuestHelperQuest.NATURE_SPIRIT, QuestState.FINISHED);
		fairyTaleII = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(),
			Operation.GREATER_EQUAL, 40, "Partial completion of Fairytale II for access to fairy rings");
		olafsQuest = new QuestRequirement(QuestHelperQuest.OLAFS_QUEST, QuestState.FINISHED,
			"Partial completion of Olaf's Quest to access the Brine Rat Cavern");
		fremennikTrials = new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED);
		betweenARock = new QuestRequirement(QuestHelperQuest.BETWEEN_A_ROCK, QuestState.FINISHED,
			"Mostly completed Between a Rock for access to the Arzinian Mine");
		dwarfCannon = new QuestRequirement(QuestHelperQuest.DWARF_CANNON, QuestState.FINISHED);
		fishingContest = new QuestRequirement(QuestHelperQuest.FISHING_CONTEST, QuestState.FINISHED);
		horrorFromTheDeep = new QuestRequirement(QuestHelperQuest.HORROR_FROM_THE_DEEP, QuestState.FINISHED);

		inEagleArea = new ZoneRequirement(eagleArea);
		inIceCave = new ZoneRequirement(iceCave);
		inHunterArea1 = new ZoneRequirement(hunterArea1);
		inHunterArea0 = new ZoneRequirement(hunterArea0);
		inRiverArea = new ZoneRequirement(riverArea);
		inCannonArea = new ZoneRequirement(riverArea2);
		inCaveArea = new ZoneRequirement(caveArea);
		inArzinianMine = new ZoneRequirement(arzinianMine);
		inWaterbirthIsland = new ZoneRequirement(waterbirthIsland);
		inBrineRatCave = new ZoneRequirement(brineRatCave);
		inDagCave = new ZoneRequirement(dagCave);
		inDagCave_2 = new ZoneRequirement(dagCave_2);
		inDagCave_3 = new ZoneRequirement(dagCave_3);
		inDagCave_4 = new ZoneRequirement(dagCave_4);
		inDagCave1 = new ZoneRequirement(dagCave1);
		inDagCave2 = new ZoneRequirement(dagCave2);
		inDagCave3 = new ZoneRequirement(dagCave3);
		inDagCave4 = new ZoneRequirement(dagCave4);
		inDagCave5 = new ZoneRequirement(dagCave5);
		inDagCave6 = new ZoneRequirement(dagCave6);
		inDagCave7 = new ZoneRequirement(dagCave7);
		inDagCave8 = new ZoneRequirement(dagCave8);
		inDagCave9 = new ZoneRequirement(dagCave9);
		inDagCave10 = new ZoneRequirement(dagCave10);
		inDagCave11 = new ZoneRequirement(dagCave11);
		inDagCave12 = new ZoneRequirement(dagCave12);
		inDagCave13 = new ZoneRequirement(dagCave13);
		inDagCave14 = new ZoneRequirement(dagCave14);
		inDagCave15 = new ZoneRequirement(dagCave15);
	}

	@Override
	protected void setupZones()
	{
		eagleArea = new Zone(new WorldPoint(1986, 4985, 3), new WorldPoint(2030, 4944, 3));
		iceCave = new Zone(new WorldPoint(2706, 10228, 0), new WorldPoint(2741, 10193, 0));
		hunterArea1 = new Zone(new WorldPoint(2690, 3838, 1), new WorldPoint(2748, 3767, 1));
		hunterArea0 = new Zone(new WorldPoint(2690, 3838, 0), new WorldPoint(2748, 3767, 0));
		riverArea = new Zone(new WorldPoint(2815, 10137, 0), new WorldPoint(2880, 10113, 0));
		riverArea2 = new Zone(new WorldPoint(2816, 10180, 0), new WorldPoint(2876, 10139, 0));
		caveArea = new Zone(new WorldPoint(2767, 10165, 0), new WorldPoint(2802, 10127, 0));
		arzinianMine = new Zone(new WorldPoint(2497, 4989, 0), new WorldPoint(2691, 4928, 0));
		waterbirthIsland = new Zone(new WorldPoint(2499, 3770, 0), new WorldPoint(2557, 3713, 0));
		brineRatCave = new Zone(new WorldPoint(2688, 10175, 0), new WorldPoint(2751, 10116, 0));
		dagCave = new Zone(new WorldPoint(2434, 10174, 0), new WorldPoint(2491, 10118, 0));
		dagCave_2 = new Zone(new WorldPoint(2492, 10174, 0), new WorldPoint(2558, 10149, 0));
		dagCave_3 = new Zone(new WorldPoint(2544, 10148, 0), new WorldPoint(2546, 10146, 0));
		dagCave_4 = new Zone(new WorldPoint(2542, 10145, 0), new WorldPoint(2547, 10141, 0));
		dagCave1 = new Zone(new WorldPoint(1792, 4414, 3), new WorldPoint(1809, 4397, 3));
		dagCave2 = new Zone(new WorldPoint(1808, 4411, 2), new WorldPoint(1824, 4400, 2));
		dagCave3 = new Zone(new WorldPoint(1824, 4412, 3), new WorldPoint(1853, 4389, 3));
		dagCave4 = new Zone(new WorldPoint(1807, 4397, 2), new WorldPoint(1835, 4380, 2));
		dagCave5 = new Zone(new WorldPoint(1794, 4398, 1), new WorldPoint(1815, 4387, 1));
		dagCave6 = new Zone(new WorldPoint(1793, 4387, 2), new WorldPoint(1805, 4378, 2));
		dagCave7 = new Zone(new WorldPoint(1793, 4385, 1), new WorldPoint(1807, 4365, 1));
		dagCave8 = new Zone(new WorldPoint(1796, 4374, 2), new WorldPoint(1877, 4354, 2));
		dagCave9 = new Zone(new WorldPoint(1824, 4374, 1), new WorldPoint(1872, 4353, 1));
		dagCave10 = new Zone(new WorldPoint(1856, 4389, 2), new WorldPoint(1871, 4371, 2));
		dagCave11 = new Zone(new WorldPoint(1858, 4415, 1), new WorldPoint(1896, 4387, 1));
		dagCave12 = new Zone(new WorldPoint(1874, 4415, 0), new WorldPoint(1968, 4350, 0));
		dagCave13 = new Zone(new WorldPoint(1927, 4383, 1), new WorldPoint(1967, 4371, 1));
		dagCave14 = new Zone(new WorldPoint(1924, 4399, 2), new WorldPoint(1968, 4374, 2));
		dagCave15 = new Zone(new WorldPoint(1956, 4414, 3), new WorldPoint(1981, 4386, 3));
	}

	public void setupSteps()
	{
		enterBrineCave = new DigStep(this, new WorldPoint(2749, 3732, 0),
			"Use a spade to enter the Brine Rat Cavern.", spade, combatGear, olafsQuest);
		enterBrineCave.addIcon(ItemID.SPADE);
		slayBrineRat = new NpcStep(this, NpcID.OLAF2_BRINE_RATS, new WorldPoint(2706, 10133, 0),
			"Kill a brine rat then roll the boulder and exit the cave..", true);
		travelMisc = new ObjectStep(this, 29495, new WorldPoint(2744, 3719, 0),
			"Use a fairy ring and travel to (CIP).", fairyTaleII);
		enterEaglesPeak = new ObjectStep(this, 19790, new WorldPoint(2329, 3495, 0),
			"Enter the cave at the top of Eagles' Peak. Use fairy ring and travel to (AKQ), then head south.", rope);
		snowyHunter = new NpcStep(this, NpcID.EAGLEPEAK_EAGLE_TOPOLAR, new WorldPoint(2027, 4964, 3),
			"Use rope on the Polar Eagle to travel to the Snowy Hunter area.", rope.highlighted());
		snowyHunter.addIcon(ItemID.ROPE);
		exitIceCave = new ObjectStep(this, 19764, new WorldPoint(2706, 10205, 0), "Exit the cave.");
		snowyKnight0 = new NpcStep(this, NpcID.BUTTERFLY_SNOWY, new WorldPoint(2725, 3770, 0),
			"Catch a Snowy Knight at the Fremennik Hunter Area.", butterFlyNet.equipped());
		snowyKnight1 = new NpcStep(this, NpcID.BUTTERFLY_SNOWY, new WorldPoint(2712, 3822, 1),
			"Catch a Snowy Knight at the Fremennik Hunter Area.", butterFlyNet.equipped());
		stealFish = new ObjectStep(this, ObjectID.VIKING_FISH_MARKET, new WorldPoint(2648, 3677, 0),
			"Steal from the Rellekka fish stall.");
		mineCoal = new ObjectStep(this, ObjectID.COALROCK2, new WorldPoint(2683, 3702, 0),
			"Mine some coal.", pickaxe);
		mineCoal.addIcon(ItemID.RUNE_PICKAXE);
		moveToCave = new ObjectStep(this, ObjectID.TROLLROMANCE_STRONGHOLD_EXIT_TUNNEL, new WorldPoint(2732, 3713, 0),
			"Enter the tunnel that leads to the Arzinian Mine.", pickaxe, goldHelm, coinsForFerry.quantity(2));
		moveToRiver = new ObjectStep(this, ObjectID.DWARF_CAVEWALL_TUNNEL, new WorldPoint(2781, 10161, 0),
			"Go through the cave entrance.", pickaxe, goldHelm, coinsForFerry.quantity(2));
		moveToCannon = new NpcStep(this, NpcID.DWARFROCK_FERRYMAN1, new WorldPoint(2842, 10129, 0),
			"Speak with the Dwarven Ferryman to go to cross the river.", pickaxe, goldHelm, coinsForFerry.quantity(2));
		moveToCannon.addDialogStep("Yes please.");
		moveToArzinian = new NpcStep(this, NpcID.DWARFROCK_DONDAKAN_NOAXE, new WorldPoint(2824, 10168, 0),
			"Speak with Dondakan to enter the mine.", goldHelm.equipped(), pickaxe);
		moveToArzinian.addDialogStep("Can you shoot me into the rock again?");
		mineGold = new ObjectStep(this, ObjectID.GOLDROCK1, new WorldPoint(2614, 4968, 0),
			"Mine the nearby gold. Remove helmet to escape area.", pickaxe);
		mineGold.addIcon(ItemID.RUNE_PICKAXE);
		moveToWaterbirth = new NpcStep(this, NpcID.VIKING_DAGGANOTH_CAVE_FERRYMAN_ISLAND, new WorldPoint(2620, 3686, 0),
			"Speak with Jarvald to travel to Waterbirth Island.", petRock, thrownaxe);
		moveToWaterbirth.addDialogSteps("What Jarvald is doing.", "Can I come?", "YES");
		moveToDagCave = new ObjectStep(this, 8929, new WorldPoint(2521, 3740, 0),
			"Enter the cave and pray melee. Make sure you are full stamina and prayer before entering.", protectMelee,
			thrownaxe, petRock, food, stamPot, prayerPot);
		dropPetRock = new ObjectStep(this, 8965, new WorldPoint(2490, 10162, 0),
			"Drop your pet rock on one pressure pad then stand on the other pad to open the gate.", petRock);// item on tile req?
		dropPetRock.addIcon(ItemID.VT_USELESS_ROCK);
		dropPetRock.addTileMarker(new WorldPoint(2490, 10164, 0), SpriteID.SKILL_AGILITY);
		moveToAxeSpot = new ObjectStep(this, 8945, new WorldPoint(2545, 10146, 0),
			"Continue onwards until you reach the barrier.", thrownaxe);
		activateSpecial = new DetailedQuestStep(this, "Activate special attack with the rune thrownaxes equipped.",
			thrownaxe.equipped(), specialAttackEnabled);
		throwAxe = new NpcStep(this, 2253, new WorldPoint(2543, 10143, 0),
			"Attack the Door-Support with a rune thrownaxe special attack. If done correctly the axe should ricochet" +
				" and lower all 3 barriers.", thrownaxe.equipped(), specialAttackEnabled);
		moveToDagCave1 = new ObjectStep(this, 10177, new WorldPoint(2546, 10143, 0),
			"Enable magic protection then climb down the ladder.", protectMagic);
		moveToDagCave1.addDialogSteps("Climb Down.");
		moveToDagCave2 = new ObjectStep(this, ObjectID.DAGEXP_LADDER1, new WorldPoint(1808, 4405, 3),
			"Enable melee protection then continue through the cave.", protectMelee);
		moveToDagCave3 = new ObjectStep(this, ObjectID.DAGEXP_LADDER4, new WorldPoint(1823, 4404, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave4 = new ObjectStep(this, ObjectID.DAGEXP_LADDER5, new WorldPoint(1834, 4389, 3),
			"Enable missile protection then continue through the cave.", protectMissiles);
		moveToDagCave5 = new ObjectStep(this, ObjectID.DAGEXP_LADDER7, new WorldPoint(1811, 4394, 2),
			"Enable magic protection and continue through the cave.", protectMagic);
		moveToDagCave6 = new ObjectStep(this, ObjectID.DAGEXP_LADDER9, new WorldPoint(1799, 4388, 1),
			"Keep current protection and continue through the cave.", protectMagic);
		moveToDagCave7 = new ObjectStep(this, ObjectID.DAGEXP_LADDER11, new WorldPoint(1797, 4382, 2),
			"Keep current protection and continue through the cave.", protectMagic);
		moveToDagCave8 = new ObjectStep(this, ObjectID.DAGEXP_LADDER13, new WorldPoint(1802, 4369, 1),
			"Enable melee protection and continue through the cave.", protectMelee);
		moveToDagCave9 = new ObjectStep(this, ObjectID.DAGEXP_LADDER15, new WorldPoint(1826, 4362, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave10 = new ObjectStep(this, ObjectID.DAGEXP_LADDER17, new WorldPoint(1863, 4371, 1),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave11 = new ObjectStep(this, ObjectID.DAGEXP_LADDER19, new WorldPoint(1864, 4388, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave12 = new ObjectStep(this, ObjectID.DAGEXP_LADDER21, new WorldPoint(1890, 4407, 1),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave13 = new ObjectStep(this, ObjectID.DAGEXP_LADDER23, new WorldPoint(1957, 4371, 0),
			"Keep current protection and continue through the cave. BE PREPARED TO EAT FOOD, YOU WILL GET HIT BY MISSILE / MAGIC.", protectMelee);
		moveToDagCave14 = new ObjectStep(this, ObjectID.DAGEXP_LADDER32, new WorldPoint(1932, 4378, 1),
			"Keep current protection and continue through the cave.", protectMelee);
		moveToDagCave15 = new ObjectStep(this, ObjectID.DAGEXP_LADDER34, new WorldPoint(1961, 4391, 2),
			"Keep current protection and continue through the cave.", protectMelee);
		lighthouse = new ObjectStep(this, ObjectID.DAGEXP_EXIT_LADDER, new WorldPoint(1975, 4408, 3),
			"Keep current protection and continue through the cave.", protectMelee);
		petRockPOH = new DetailedQuestStep(this,
			"Use a pet rock on your pet house in your menagerie in your player owned house and then pick it up off the GROUND." +
				" You may need to re-enter your house after placing your pet rock in the pet house.",
			petRock, oakPlanks.quantity(4), saw, hammer);

		claimReward = new NpcStep(this, NpcID.VIKING_FREM_DIARY, new WorldPoint(2658, 3627, 0),
			"Talk to Thorodin south of Rellekka to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pickaxe, coins.quantity(30002), spade, staff, rope, butterFlyJar,
			butterFlyNet, petRock, goldHelm, oakPlanks.quantity(4), saw, hammer, thrownaxe,
			combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, prayerPot, stamPot);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 37, true));
		req.add(new SkillRequirement(Skill.HUNTER, 35, true));
		req.add(new SkillRequirement(Skill.MINING, 40));
		req.add(new SkillRequirement(Skill.SLAYER, 47, true));
		req.add(new SkillRequirement(Skill.THIEVING, 42, true));
		req.add(new SkillRequirement(Skill.PRAYER, 43, false,
			"43 Prayer for protection prayers"));
		req.add(olafsQuest);
		req.add(eaglesPeak);
		req.add(betweenARock);
		req.add(horrorFromTheDeep);
		return req;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Brine rat (level 70) and tank many hits in the Waterbirth Island Dungeon");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Fremennik Sea Boots (2)", ItemID.FREMENNIK_BOOTS_MEDIUM, 1),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.THOSF_REWARD_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(new UnlockReward("Improved rate of gaining approval on Miscellania."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails brineRatSteps = new PanelDetails("Brine Rat Slayer", Arrays.asList(enterBrineCave, slayBrineRat),
			olafsQuest, new SkillRequirement(Skill.SLAYER, 47, true), spade, combatGear);
		brineRatSteps.setDisplayCondition(notSlayBrineRat);
		brineRatSteps.setLockingStep(slayBrineRatTask);
		allSteps.add(brineRatSteps);

		PanelDetails miscellaniaSteps = new PanelDetails("Travel to Miscellania", Collections.singletonList(travelMisc),
			fairyTaleII, fremennikTrials, staff);
		miscellaniaSteps.setDisplayCondition(notTravelMisc);
		miscellaniaSteps.setLockingStep(travelMiscTask);
		allSteps.add(miscellaniaSteps);

		PanelDetails snowyHunterSteps = new PanelDetails("Travel by Eagle", Arrays.asList(enterEaglesPeak, snowyHunter),
			eaglesPeak, rope);
		snowyHunterSteps.setDisplayCondition(notSnowyHunter);
		snowyHunterSteps.setLockingStep(snowyHunterTask);
		allSteps.add(snowyHunterSteps);

		PanelDetails snowyKnightSteps = new PanelDetails("Catch Snowy Knight", Collections.singletonList(snowyKnight0),
			new SkillRequirement(Skill.HUNTER, 35, true), butterFlyNet, butterFlyJar);
		snowyKnightSteps.setDisplayCondition(notSnowyKnight);
		snowyKnightSteps.setLockingStep(snowyKnightTask);
		allSteps.add(snowyKnightSteps);

		PanelDetails mineGoldSteps = new PanelDetails("Mine Gold", Arrays.asList(moveToCave, moveToRiver, moveToCannon,
			moveToArzinian, mineGold), betweenARock, new SkillRequirement(Skill.MINING, 40), pickaxe, goldHelm,
			coins.quantity(2));
		mineGoldSteps.setDisplayCondition(notMineGold);
		mineGoldSteps.setLockingStep(mineGoldTask);
		allSteps.add(mineGoldSteps);

		PanelDetails mineCoalSteps = new PanelDetails("Mine Coal", Collections.singletonList(mineCoal),
			fremennikTrials, new SkillRequirement(Skill.MINING, 30), pickaxe);
		mineCoalSteps.setDisplayCondition(notMineCoal);
		mineCoalSteps.setLockingStep(mineCoalTask);
		allSteps.add(mineCoalSteps);

		PanelDetails stealFishSteps = new PanelDetails("Steal Fish", Collections.singletonList(stealFish),
			fremennikTrials, new SkillRequirement(Skill.THIEVING, 42, true));
		stealFishSteps.setDisplayCondition(notStealFish);
		stealFishSteps.setLockingStep(stealFishTask);
		allSteps.add(stealFishSteps);

		PanelDetails lighthouseSteps = new PanelDetails("Waterbirth to Lighthouse", Arrays.asList(moveToWaterbirth,
			moveToDagCave, dropPetRock, moveToAxeSpot, throwAxe, moveToDagCave1, moveToDagCave2, moveToDagCave3,
			moveToDagCave4, moveToDagCave5, moveToDagCave6, moveToDagCave7, moveToDagCave8, moveToDagCave9,
			moveToDagCave10, moveToDagCave11, moveToDagCave12, moveToDagCave13, moveToDagCave14, moveToDagCave15,
			lighthouse), horrorFromTheDeep, fremennikTrials, petRock, combatGear, thrownaxe, food, stamPot, prayerPot);
		lighthouseSteps.setDisplayCondition(notLighthouse);
		lighthouseSteps.setLockingStep(lighthouseTask);
		allSteps.add(lighthouseSteps);

		PanelDetails petRockSteps = new PanelDetails("Pet Rock", Collections.singletonList(petRockPOH),
			fremennikTrials, new SkillRequirement(Skill.CONSTRUCTION, 37, true), coins.quantity(30000),
			petRock, saw, hammer, oakPlanks.quantity(4));
		petRockSteps.setDisplayCondition(notPetRockPOH);
		petRockSteps.setLockingStep(petRockPOHTask);
		allSteps.add(petRockSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
