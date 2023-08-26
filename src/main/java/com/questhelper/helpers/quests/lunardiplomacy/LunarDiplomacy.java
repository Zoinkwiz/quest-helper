/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.helpers.quests.lunardiplomacy;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
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
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.LUNAR_DIPLOMACY
)
public class LunarDiplomacy extends BasicQuestHelper
{
	//Items Required
	ItemRequirement sealOfPassage, bullseyeLantern, bullseyeLanternLit, emeraldLantern, emeraldLanternLit, emeraldLens,
		bullseyeLanternHighlighted, tinderboxHighlighted, emeraldLensHighlighted, emeraldLanternLitHighlighted, suqahTooth,
		groundTooth, marrentilPotion, guamPotion, guamMarrentilPotion, guamMarrentilPotionHighlighted, sleepPotion, specialVial,
		specialVialHighlighted, waterVial, guam, marrentill, pestle, airTalisman, waterTalisman, earthTalisman, fireTalisman,
		dramenStaff, dramenStaffHighlighted, lunarStaffP1, lunarStaffP1Highlighted, lunarStaffP2, lunarStaffP2Highlighted,
		lunarStaffP3, lunarStaffP3Highlighted, lunarStaff, pickaxe, hammer, needle, thread, coins400, spade, lunarOre,
		lunarBar, tiara, helm, amulet, ring, cape, torso, gloves, boots, legs, suqahHide4, kindling, helmEquipped, bodyEquipped,
		legsEquipped, bootsEquipped, glovesEquipped, cloakEquipped, amuletEquipped, ringEquipped, lunarStaffEquipped, soakedKindling,
		sleepPotionHighlighted, soakedKindlingHighlighted, sealOfPassageEquipped;

	//Items Recommended
	ItemRequirement combatRunes, combatGear;

	Requirement atBaseOfStairs, onCoveF1, onBoatF0, onBoatF1, onBoatF2, onBoatF3, revealedCannon, revealedChart, revealedChest,
		revealedPillar, revealedCrate, onBoatLunar, onLunarDock, onLunarIsle, inYagaHouse, toothNearby,
		inAirAltar, inEarthAltar, inWaterAltar, inFireAltar, inLunarMine, talkedToSelene, talkedToMeteora, talkedToRimae, tiaraNearby,
		hadClothes, hadHelm, hadCape, hadAmulet, hadTorso, hadGloves, hadLegs, hadRing, hadBoots,
		litBrazier, inCentreOfDream, inChanceDream, inNumbersDream, inTreeDream, inMemoryDream, inRaceDream,
		inMimicDream, startedNumberChallenge, finishedChance, finishedNumbers, finishedTree, finishedMemory, finishedRace,
		finishedMimic, needToTalkAtMiddle, doingTreeChallenge, startedRaceChallenge, inFightArena;

	DetailedQuestStep talkToLokar, talkToBrundt, talkToLokarAgain, travelWithLokar, climbLadder, boardShip,
		talkToBentley, climbDownSouthStairs, talkToJack, climbUpSouthStairs, talkToBentleyAfterJack,
		goDownToJackAgain, talkToJackAgain, goUpToShultz, talkToShultz, goDownToBurns1, goDownToBurns2,
		goDownToBurns3, talkToBurns, goUpToLee1, goUpToLee2, goUpToLee3, talkToLee, goDownToDavey, talkToDavey,
		goUpToCabinBoy, talkToCabinBoy, extinguishLantern, replaceLens, lightLantern, getLensAndBullseye,
		goUpToCannon1, goUpToCannon2, goUpToCannon3, useLanternOnCannon, goDownToChart, goUpToChart1, goUpToChart2,
		useLanternOnChart, goDownToChest1, goDownToChest2, goDownToChest3, useLanternOnChest, useLanternOnPillar,
		useLanternOnCrate, goUpToSail1, goUpToSail2, goDownToSail, goDownToIsle1, goDownToIsle2, enterTown, talkToOneiromancer,
		enterChickenHouse, talkToYaga, leaveChickenHouse, fillVial, addGuam, addMarrentil, addGuamToMarrentill, grindTooth, pickUpTooth,
		addToothToPotion, bringPotionToOneiromancer, enterFireAltar, enterWaterAltar, enterAirAltar, enterEarthAltar, useOnEarth, useOnFire,
		useOnAir, useOnWater, talkToOneiromancerWithStaff, enterMine, smeltBar, makeHelmet, talkToPauline, talkToMeteora, talkToSelene,
		returnTiaraToMeteora, digForRing, makeClothes, bringItemsToOneiromancer, talkToRimae, pickUpTiara, useVialOnKindling, lightBrazier,
		useKindlingOnBrazier, talkToEthereal, goToChance, goToMimic, goToRace, goToTrees, goToMemory, goToNumbers, doMemoryChallenge,
		startTreeChallenge, doRaceChallenge, startNumber, startRace, doTreeChallenge, talkWithEtherealToFight, leaveLecturn, finishQuest;

	NpcStep talkToBentleyToSail, killSuqahForTooth, killSuqahForTiara, fightMe;

	ObjectStep mineOre;

	DetailedOwnerStep doNumberChallenge, doMimicChallenge, doChanceChallenge;

	ConditionalStep returnToMakePotion, returnToTalkToYaga, enteringTheIsland, boardingTheBoat, setSail, returnToOneWithPotion, returnWithStaff, makingHelm,
		gettingRing, gettingCape, gettingAmulet, gettingClothes;

	//Zones
	Zone baseOfStairs, coveF1, boatF0, boatF1, boatF2, boatF3, boatLunar1, boatLunar2, lunarDock, lunarIsle, yagaHouse, airAltar,
		waterAltar, earthAltar, fireAltar, lunarMine, centreOfDream, chanceDream, numbersDream, treeDream, memoryDream, raceDream,
		mimicDream, fightArena;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToLokar);
		steps.put(10, talkToBrundt);
		steps.put(20, talkToLokarAgain);

		ConditionalStep talkingToBentley = new ConditionalStep(this, boardingTheBoat);
		talkingToBentley.addStep(onBoatF2, talkToBentley);
		steps.put(30, talkingToBentley);

		ConditionalStep talkingToJack = new ConditionalStep(this, boardingTheBoat);
		talkingToJack.addStep(onBoatF1, talkToJack);
		talkingToJack.addStep(onBoatF2, climbDownSouthStairs);
		steps.put(40, talkingToJack);

		ConditionalStep talkingToBentleyAfterJack = new ConditionalStep(this, boardingTheBoat);
		talkingToBentleyAfterJack.addStep(onBoatF1, climbUpSouthStairs);
		talkingToBentleyAfterJack.addStep(onBoatF2, talkToBentleyAfterJack);
		steps.put(45, talkingToBentleyAfterJack);

		ConditionalStep talkingToJackAgain = new ConditionalStep(this, boardingTheBoat);
		talkingToJackAgain.addStep(onBoatF1, talkToJackAgain);
		talkingToJackAgain.addStep(onBoatF2, goDownToJackAgain);
		steps.put(50, talkingToJackAgain);

		ConditionalStep talkingToShultz = new ConditionalStep(this, boardingTheBoat);
		talkingToShultz.addStep(onBoatF2, talkToShultz);
		talkingToShultz.addStep(onBoatF1, goUpToShultz);
		steps.put(60, talkingToShultz);

		ConditionalStep talkingToBurns = new ConditionalStep(this, boardingTheBoat);
		talkingToBurns.addStep(onBoatF3, goDownToBurns1);
		talkingToBurns.addStep(onBoatF2, goDownToBurns2);
		talkingToBurns.addStep(onBoatF1, goDownToBurns3);
		talkingToBurns.addStep(onBoatF0, talkToBurns);
		steps.put(70, talkingToBurns);

		ConditionalStep talkingToLee = new ConditionalStep(this, boardingTheBoat);
		talkingToLee.addStep(onBoatF3, talkToLee);
		talkingToLee.addStep(onBoatF2, goUpToLee3);
		talkingToLee.addStep(onBoatF1, goUpToLee2);
		talkingToLee.addStep(onBoatF0, goUpToLee1);
		steps.put(80, talkingToLee);

		ConditionalStep talkingToDavey = new ConditionalStep(this, boardingTheBoat);
		talkingToDavey.addStep(onBoatF3, goDownToDavey);
		talkingToDavey.addStep(onBoatF2, talkToDavey);
		steps.put(90, talkingToDavey);

		ConditionalStep talkingToCabinBoyAgain = new ConditionalStep(this, boardingTheBoat);
		talkingToCabinBoyAgain.addStep(onBoatF3, talkToCabinBoy);
		talkingToCabinBoyAgain.addStep(onBoatF2, goUpToCabinBoy);
		steps.put(100, talkingToCabinBoyAgain);

		ConditionalStep removingSymbols = new ConditionalStep(this, getLensAndBullseye);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF0, revealedCannon, revealedChart, revealedChest,
			revealedPillar), useLanternOnCrate);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF0, revealedCannon, revealedChart, revealedChest), useLanternOnPillar);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF3, revealedCannon, revealedChart), goDownToChest1);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF2, revealedCannon, revealedChart), goDownToChest2);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF1, revealedCannon, revealedChart), goDownToChest3);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF0, revealedCannon, revealedChart), useLanternOnChest);

		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF3, revealedCannon), goDownToChart);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF2, revealedCannon), useLanternOnChart);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF1, revealedCannon), goUpToChart2);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF0, revealedCannon), goUpToChart1);

		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF3), useLanternOnCannon);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF2), goUpToCannon3);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF1), goUpToCannon2);
		removingSymbols.addStep(new Conditions(emeraldLanternLit, onBoatF0), goUpToCannon1);

		removingSymbols.addStep(emeraldLanternLit, boardingTheBoat);
		removingSymbols.addStep(emeraldLantern, lightLantern);
		removingSymbols.addStep(bullseyeLantern, replaceLens);
		removingSymbols.addStep(bullseyeLanternLit, extinguishLantern);
		steps.put(110, removingSymbols);
		steps.put(112, removingSymbols);
		steps.put(114, removingSymbols);
		steps.put(116, removingSymbols);
		steps.put(118, removingSymbols);

		steps.put(120, setSail);

		ConditionalStep enteringTheTown = new ConditionalStep(this, enteringTheIsland);
		enteringTheTown.addStep(onLunarIsle, enterTown);
		steps.put(125, enteringTheTown);

		ConditionalStep talkingToTheOneirmancer = new ConditionalStep(this, enteringTheIsland);
		talkingToTheOneirmancer.addStep(onLunarIsle, talkToOneiromancer);
		steps.put(130, talkingToTheOneirmancer);

		ConditionalStep talkingToYaga = new ConditionalStep(this, returnToTalkToYaga);
		talkingToYaga.addStep(inYagaHouse, talkToYaga);
		talkingToYaga.addStep(onLunarIsle, enterChickenHouse);
		steps.put(135, talkingToYaga);

		ConditionalStep makingThePotion = new ConditionalStep(this, fillVial);
		makingThePotion.addStep(new Conditions(onLunarIsle, sleepPotion), bringPotionToOneiromancer);
		makingThePotion.addStep(new Conditions(sleepPotion), returnToOneWithPotion);
		makingThePotion.addStep(new Conditions(groundTooth, guamMarrentilPotion), addToothToPotion);
		makingThePotion.addStep(new Conditions(suqahTooth, guamMarrentilPotion), grindTooth);
		makingThePotion.addStep(new Conditions(toothNearby, guamMarrentilPotion), pickUpTooth);
		makingThePotion.addStep(new Conditions(guamMarrentilPotion, onLunarIsle), killSuqahForTooth);
		makingThePotion.addStep(guamMarrentilPotion, returnToMakePotion);
		makingThePotion.addStep(marrentilPotion, addGuamToMarrentill);
		makingThePotion.addStep(guamPotion, addMarrentil);
		makingThePotion.addStep(waterVial, addGuam);
		makingThePotion.addStep(inYagaHouse, leaveChickenHouse);
		steps.put(140, makingThePotion);

		ConditionalStep makingTheLunarStaff = new ConditionalStep(this, enterAirAltar);
		makingTheLunarStaff.addStep(new Conditions(onLunarIsle, lunarStaff), talkToOneiromancerWithStaff);
		makingTheLunarStaff.addStep(lunarStaff, returnWithStaff);
		makingTheLunarStaff.addStep(new Conditions(lunarStaffP3, inEarthAltar), useOnEarth);
		makingTheLunarStaff.addStep(lunarStaffP3, enterEarthAltar);
		makingTheLunarStaff.addStep(new Conditions(lunarStaffP2, inWaterAltar), useOnWater);
		makingTheLunarStaff.addStep(lunarStaffP2, enterWaterAltar);
		makingTheLunarStaff.addStep(new Conditions(lunarStaffP1, inFireAltar), useOnFire);
		makingTheLunarStaff.addStep(lunarStaffP1, enterFireAltar);
		makingTheLunarStaff.addStep(inAirAltar, useOnAir);
		steps.put(145, makingTheLunarStaff);

		ConditionalStep gettingRestOfEquipment = new ConditionalStep(this, makingHelm);
		gettingRestOfEquipment.addStep(new Conditions(hadHelm, hadCape, hadAmulet, hadRing, hadClothes), bringItemsToOneiromancer);
		gettingRestOfEquipment.addStep(new Conditions(hadHelm, hadCape, hadAmulet, hadRing), gettingClothes);
		gettingRestOfEquipment.addStep(new Conditions(hadHelm, hadCape, hadAmulet), gettingRing);
		gettingRestOfEquipment.addStep(new Conditions(hadHelm, hadCape), gettingAmulet);
		gettingRestOfEquipment.addStep(hadHelm, gettingCape);
		steps.put(155, gettingRestOfEquipment);

		ConditionalStep enterDream = new ConditionalStep(this, useVialOnKindling);
		enterDream.addStep(new Conditions(soakedKindling, litBrazier), useKindlingOnBrazier);
		enterDream.addStep(soakedKindling, lightBrazier);
		steps.put(160, enterDream);

		ConditionalStep startingDream = new ConditionalStep(this, enterDream);
		startingDream.addStep(inCentreOfDream, talkToEthereal);
		steps.put(165, startingDream);

		ConditionalStep challenges = new ConditionalStep(this, enterDream);
		challenges.addStep(new Conditions(inCentreOfDream, needToTalkAtMiddle), talkToEthereal);
		challenges.addStep(new Conditions(inNumbersDream, startedNumberChallenge), doNumberChallenge);
		challenges.addStep(inMimicDream, doMimicChallenge);
		challenges.addStep(inNumbersDream, startNumber);
		challenges.addStep(inChanceDream, doChanceChallenge);
		challenges.addStep(inMemoryDream, doMemoryChallenge);
		challenges.addStep(new Conditions(inTreeDream, doingTreeChallenge), doTreeChallenge);
		challenges.addStep(inTreeDream, startTreeChallenge);
		challenges.addStep(new Conditions(inRaceDream, startedRaceChallenge), doRaceChallenge);
		challenges.addStep(inRaceDream, startRace);
		challenges.addStep(new Conditions(inCentreOfDream, finishedRace, finishedNumbers, finishedMimic, finishedChance, finishedMemory, finishedTree), talkWithEtherealToFight);
		challenges.addStep(new Conditions(inCentreOfDream, finishedRace, finishedNumbers, finishedMimic, finishedChance, finishedMemory), goToTrees);
		challenges.addStep(new Conditions(inCentreOfDream, finishedRace, finishedNumbers, finishedMimic, finishedChance), goToMemory);
		challenges.addStep(new Conditions(inCentreOfDream, finishedRace, finishedNumbers, finishedMimic), goToChance);
		challenges.addStep(new Conditions(inCentreOfDream, finishedRace, finishedNumbers), goToMimic);
		challenges.addStep(new Conditions(inCentreOfDream, finishedRace), goToNumbers);
		challenges.addStep(inCentreOfDream, goToRace);
		steps.put(170, challenges);

		ConditionalStep fightingYourself = new ConditionalStep(this, enterDream);
		fightingYourself.addStep(inFightArena, fightMe);
		fightingYourself.addStep(inCentreOfDream, talkWithEtherealToFight);
		steps.put(175, fightingYourself);

		ConditionalStep reportingToOne = new ConditionalStep(this, finishQuest);
		reportingToOne.addStep(inCentreOfDream, leaveLecturn);
		steps.put(180, reportingToOne);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		sealOfPassage = new ItemRequirement("Seal of passage", ItemID.SEAL_OF_PASSAGE).isNotConsumed();
		sealOfPassage.setTooltip("You can get another from Brundt");

		sealOfPassageEquipped = sealOfPassage.equipped();

		bullseyeLantern = new ItemRequirement("Bullseye lantern", ItemID.BULLSEYE_LANTERN).isNotConsumed();
		bullseyeLantern.addAlternates(ItemID.SAPPHIRE_LANTERN_4701);
		bullseyeLanternHighlighted = new ItemRequirement("Bullseye lantern", ItemID.BULLSEYE_LANTERN).isNotConsumed();
		bullseyeLanternHighlighted.addAlternates(ItemID.SAPPHIRE_LANTERN_4701);
		bullseyeLanternHighlighted.setHighlightInInventory(true);

		bullseyeLanternLit = new ItemRequirement("Bullseye lantern", ItemID.BULLSEYE_LANTERN_4550).isNotConsumed();
		bullseyeLanternLit.setHighlightInInventory(true);
		bullseyeLanternLit.addAlternates(ItemID.SAPPHIRE_LANTERN_4702);

		emeraldLantern = new ItemRequirement("Emerald lantern", ItemID.EMERALD_LANTERN).isNotConsumed();
		emeraldLantern.setHighlightInInventory(true);

		emeraldLanternLit = new ItemRequirement("Emerald lantern", ItemID.EMERALD_LANTERN_9065);
		emeraldLanternLitHighlighted = new ItemRequirement("Emerald lantern", ItemID.EMERALD_LANTERN_9065);
		emeraldLanternLitHighlighted.setHighlightInInventory(true);

		emeraldLens = new ItemRequirement("Emerald lens", ItemID.EMERALD_LENS);
		emeraldLens.setTooltip("You can get another from the Cabin boy");

		emeraldLensHighlighted = new ItemRequirement("Emerald lens", ItemID.EMERALD_LENS);
		emeraldLensHighlighted.setTooltip("You can get another from the Cabin boy");
		emeraldLensHighlighted.setHighlightInInventory(true);

		tinderboxHighlighted = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tinderboxHighlighted.setHighlightInInventory(true);

		guam = new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF);
		guam.setHighlightInInventory(true);
		marrentill = new ItemRequirement("Marrentill", ItemID.MARRENTILL);
		marrentill.setHighlightInInventory(true);

		suqahTooth = new ItemRequirement("Suqah tooth", ItemID.SUQAH_TOOTH);
		suqahTooth.setHighlightInInventory(true);
		groundTooth = new ItemRequirement("Ground tooth", ItemID.GROUND_TOOTH);
		groundTooth.setHighlightInInventory(true);

		specialVial = new ItemRequirement("Empty vial", ItemID.EMPTY_VIAL);
		specialVial.setTooltip("You can get another from Baba Yaga");

		specialVialHighlighted = new ItemRequirement("Empty vial", ItemID.EMPTY_VIAL);
		specialVialHighlighted.setHighlightInInventory(true);
		specialVialHighlighted.setTooltip("You can get another from Baba Yaga");

		waterVial = new ItemRequirement("Empty vial", ItemID.VIAL_OF_WATER_9086);
		waterVial.setHighlightInInventory(true);
		waterVial.setTooltip("You can get another from Baba Yaga");

		guamPotion = new ItemRequirement("Guam vial", ItemID.GUAM_VIAL);
		guamPotion.setHighlightInInventory(true);
		marrentilPotion = new ItemRequirement("Marr vial", ItemID.MARR_VIAL);
		marrentilPotion.setHighlightInInventory(true);

		guamMarrentilPotion = new ItemRequirement("Guam-marr vial", ItemID.GUAMMARR_VIAL);
		guamMarrentilPotionHighlighted = new ItemRequirement("Guam-marr vial", ItemID.GUAMMARR_VIAL);
		guamMarrentilPotionHighlighted.setHighlightInInventory(true);

		sleepPotion = new ItemRequirement("Waking sleep vial", ItemID.WAKING_SLEEP_VIAL);
		sleepPotionHighlighted = new ItemRequirement("Waking sleep vial", ItemID.WAKING_SLEEP_VIAL);
		sleepPotionHighlighted.setHighlightInInventory(true);

		pestle = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();
		pestle.setHighlightInInventory(true);

		airTalisman = new ItemRequirement("Air talisman/tiara, or access via the Abyss", ItemID.AIR_TALISMAN).isNotConsumed();
		airTalisman.addAlternates(ItemID.AIR_TIARA, ItemID.ELEMENTAL_TALISMAN);

		fireTalisman = new ItemRequirement("Fire talisman/tiara, or access via the Abyss", ItemID.FIRE_TALISMAN).isNotConsumed();
		fireTalisman.addAlternates(ItemID.FIRE_TIARA, ItemID.ELEMENTAL_TALISMAN);

		earthTalisman = new ItemRequirement("Earth talisman/tiara, or access via the Abyss", ItemID.EARTH_TALISMAN).isNotConsumed();
		earthTalisman.addAlternates(ItemID.EARTH_TIARA, ItemID.ELEMENTAL_TALISMAN);

		waterTalisman = new ItemRequirement("Water talisman/tiara, or access via the Abyss", ItemID.WATER_TALISMAN).isNotConsumed();
		waterTalisman.addAlternates(ItemID.WATER_TIARA, ItemID.ELEMENTAL_TALISMAN);

		dramenStaff = new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF).isNotConsumed();
		dramenStaff.setTooltip("You can get another from under Entrana");
		dramenStaffHighlighted = new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF).isNotConsumed();
		dramenStaffHighlighted.setTooltip("You can get another from under Entrana");
		dramenStaffHighlighted.setHighlightInInventory(true);

		lunarStaffP1 = new ItemRequirement("Lunar staff - pt1", ItemID.LUNAR_STAFF__PT1);
		lunarStaffP1Highlighted = new ItemRequirement("Lunar staff - pt1", ItemID.LUNAR_STAFF__PT1);
		lunarStaffP1Highlighted.setHighlightInInventory(true);

		lunarStaffP2 = new ItemRequirement("Lunar staff - pt2", ItemID.LUNAR_STAFF__PT2);
		lunarStaffP2Highlighted = new ItemRequirement("Lunar staff - pt2", ItemID.LUNAR_STAFF__PT2);
		lunarStaffP2Highlighted.setHighlightInInventory(true);

		lunarStaffP3 = new ItemRequirement("Lunar staff - pt3", ItemID.LUNAR_STAFF__PT3);
		lunarStaffP3Highlighted = new ItemRequirement("Lunar staff - pt3", ItemID.LUNAR_STAFF__PT3);
		lunarStaffP3Highlighted.setHighlightInInventory(true);

		lunarStaff = new ItemRequirement("Lunar staff", ItemID.LUNAR_STAFF);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		needle = new ItemRequirement("Needle", ItemID.NEEDLE).isNotConsumed();
		thread = new ItemRequirement("Thread", ItemID.THREAD).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		coins400 = new ItemRequirement("Coins", ItemCollections.COINS, 400);

		combatRunes = new ItemRequirement("Combat runes", -1, -1);
		combatRunes.setDisplayItemId(ItemID.DEATH_RUNE);

		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();

		lunarOre = new ItemRequirement("Lunar ore", ItemID.LUNAR_ORE);
		lunarBar = new ItemRequirement("Lunar bar", ItemID.LUNAR_BAR);

		tiara = new ItemRequirement("A special tiara", ItemID.A_SPECIAL_TIARA);

		helm = new ItemRequirement("Lunar helm", ItemID.LUNAR_HELM);
		amulet = new ItemRequirement("Lunar amulet", ItemID.LUNAR_AMULET);
		ring = new ItemRequirement("Lunar ring", ItemID.LUNAR_RING);
		cape = new ItemRequirement("Lunar cape", ItemID.LUNAR_CAPE);
		torso = new ItemRequirement("Lunar torso", ItemID.LUNAR_TORSO);
		gloves = new ItemRequirement("Lunar gloves", ItemID.LUNAR_GLOVES);
		boots = new ItemRequirement("Lunar boots", ItemID.LUNAR_BOOTS);
		legs = new ItemRequirement("Lunar legs", ItemID.LUNAR_LEGS);
		kindling = new ItemRequirement("Kindling", ItemID.KINDLING);
		kindling.setTooltip("You can get any of these items from the Oneiromancer");
		kindling.setHighlightInInventory(true);
		soakedKindling = new ItemRequirement("Soaked kindling", ItemID.SOAKED_KINDLING);
		soakedKindlingHighlighted = new ItemRequirement("Soaked kindling", ItemID.SOAKED_KINDLING);
		soakedKindlingHighlighted.setHighlightInInventory(true);
		helmEquipped = new ItemRequirement("Lunar helm", ItemID.LUNAR_HELM, 1, true);
		bodyEquipped = new ItemRequirement("Lunar torso", ItemID.LUNAR_TORSO, 1, true);
		legsEquipped = new ItemRequirement("Lunar legs", ItemID.LUNAR_LEGS, 1, true);
		bootsEquipped = new ItemRequirement("Lunar boots", ItemID.LUNAR_BOOTS, 1, true);
		glovesEquipped = new ItemRequirement("Lunar gloves", ItemID.LUNAR_GLOVES, 1, true);
		cloakEquipped = new ItemRequirement("Lunar cape", ItemID.LUNAR_CAPE, 1, true);
		amuletEquipped = new ItemRequirement("Lunar amulet", ItemID.LUNAR_AMULET, 1, true);
		ringEquipped = new ItemRequirement("Lunar ring", ItemID.LUNAR_RING, 1, true);
		lunarStaffEquipped = new ItemRequirement("Lunar staff", ItemID.LUNAR_STAFF, 1, true);

		suqahHide4 = new ItemRequirement("Suqah hide", ItemID.SUQAH_HIDE, 4);
		suqahHide4.addAlternates(ItemID.SUQAH_LEATHER, ItemID.LUNAR_BOOTS, ItemID.LUNAR_LEGS, ItemID.LUNAR_GLOVES, ItemID.LUNAR_TORSO);
	}

	public void loadZones()
	{
		baseOfStairs = new Zone(new WorldPoint(2212, 3794, 0), new WorldPoint(2214, 3795, 0));
		coveF1 = new Zone(new WorldPoint(2212, 3796, 1), new WorldPoint(2215, 3810, 1));
		boatF0 = new Zone(new WorldPoint(2216, 3784, 0), new WorldPoint(2235, 3820, 0));
		boatF1 = new Zone(new WorldPoint(2216, 3784, 1), new WorldPoint(2235, 3820, 1));
		boatF2 = new Zone(new WorldPoint(2214, 3784, 2), new WorldPoint(2235, 3820, 2));
		boatF3 = new Zone(new WorldPoint(2216, 3784, 3), new WorldPoint(2235, 3820, 3));

		boatLunar1 = new Zone(new WorldPoint(2128, 3890, 2), new WorldPoint(2135, 3903, 2));
		boatLunar2 = new Zone(new WorldPoint(2133, 3888, 0), new WorldPoint(2147, 3919, 3));

		lunarDock = new Zone(new WorldPoint(2116, 3889, 1), new WorldPoint(2131, 3902, 1));
		lunarIsle = new Zone(new WorldPoint(2048, 3841, 0), new WorldPoint(2174, 3968, 0));

		yagaHouse = new Zone(new WorldPoint(2449, 4645, 0), new WorldPoint(2453, 4649, 0));

		airAltar = new Zone(new WorldPoint(2832, 4826, 0), new WorldPoint(2855, 4849, 0));
		earthAltar = new Zone(new WorldPoint(2644, 4820, 0), new WorldPoint(2675, 4856, 0));
		fireAltar = new Zone(new WorldPoint(2570, 4856, 0), new WorldPoint(2615, 4815, 0));
		waterAltar = new Zone(new WorldPoint(2704, 4820, 0), new WorldPoint(2733, 4846, 0));
		lunarMine = new Zone(new WorldPoint(2300, 10313, 2), new WorldPoint(2370, 10354, 2));
		centreOfDream = new Zone(new WorldPoint(1748, 5076, 2), new WorldPoint(1771, 5099, 2));
		memoryDream = new Zone(new WorldPoint(1730, 5078, 2), new WorldPoint(1741, 5113, 2));
		chanceDream = new Zone(new WorldPoint(1730, 5059, 2), new WorldPoint(1741, 5070, 2));
		mimicDream = new Zone(new WorldPoint(1765, 5056, 2), new WorldPoint(1774, 5071, 2));
		raceDream = new Zone(new WorldPoint(1781, 5078, 2), new WorldPoint(1788, 5109, 2));
		treeDream = new Zone(new WorldPoint(1749, 5108, 2), new WorldPoint(1770, 5115, 2));
		numbersDream = new Zone(new WorldPoint(1780, 5060, 2), new WorldPoint(1788, 5068, 2));
		fightArena = new Zone(new WorldPoint(1812, 5076, 2), new WorldPoint(1840, 5099, 2));
	}

	public void setupConditions()
	{
		atBaseOfStairs = new ZoneRequirement(baseOfStairs);
		onCoveF1 = new ZoneRequirement(coveF1);

		onBoatF0 = new ZoneRequirement(boatF0);
		onBoatF1 = new ZoneRequirement(boatF1);
		onBoatF2 = new ZoneRequirement(boatF2);
		onBoatF3 = new ZoneRequirement(boatF3);

		onBoatLunar = new ZoneRequirement(boatLunar1, boatLunar2);
		onLunarDock = new ZoneRequirement(lunarDock);
		onLunarIsle = new ZoneRequirement(lunarIsle);
		inLunarMine = new ZoneRequirement(lunarMine);
		inYagaHouse = new ZoneRequirement(yagaHouse);

		inAirAltar = new ZoneRequirement(airAltar);
		inEarthAltar = new ZoneRequirement(earthAltar);
		inFireAltar = new ZoneRequirement(fireAltar);
		inWaterAltar = new ZoneRequirement(waterAltar);

		inFightArena = new ZoneRequirement(fightArena);

		revealedPillar = new VarbitRequirement(2431, 2);
		revealedCannon = new VarbitRequirement(2432, 2);
		revealedCrate = new VarbitRequirement(2433, 2);
		revealedChart = new VarbitRequirement(2434, 2);
		revealedChest = new VarbitRequirement(2435, 2);

		toothNearby = new ItemOnTileRequirement(suqahTooth);

		talkedToSelene = new VarbitRequirement(2445, 1);
		talkedToMeteora = new VarbitRequirement(2446, 1);
		talkedToRimae = new VarbitRequirement(2447, 1);

		tiaraNearby = new ItemOnTileRequirement(tiara);

		hadHelm = new Conditions(LogicType.OR, helm.alsoCheckBank(questBank), new VarbitRequirement(2436, 1));
		hadCape = new Conditions(LogicType.OR, cape.alsoCheckBank(questBank), new VarbitRequirement(2437, 1));
		hadAmulet = new Conditions(LogicType.OR, amulet.alsoCheckBank(questBank), new VarbitRequirement(2438, 1));
		hadTorso = new Conditions(LogicType.OR, torso.alsoCheckBank(questBank), new VarbitRequirement(2439, 1));
		hadGloves = new Conditions(LogicType.OR, gloves.alsoCheckBank(questBank), new VarbitRequirement(2441, 1));
		hadBoots = new Conditions(LogicType.OR, boots.alsoCheckBank(questBank), new VarbitRequirement(2440, 1));
		hadLegs = new Conditions(LogicType.OR, legs.alsoCheckBank(questBank), new VarbitRequirement(2442, 1));
		hadRing = new Conditions(LogicType.OR, ring.alsoCheckBank(questBank), new VarbitRequirement(2443, 1));
		hadClothes = new Conditions(hadBoots, hadTorso, hadGloves, hadLegs);

		litBrazier = new VarbitRequirement(2430, 1);

		inCentreOfDream = new ZoneRequirement(centreOfDream);
		inChanceDream = new ZoneRequirement(chanceDream);
		inNumbersDream = new ZoneRequirement(numbersDream);
		inTreeDream = new ZoneRequirement(treeDream);
		inMemoryDream = new ZoneRequirement(memoryDream);
		inRaceDream = new ZoneRequirement(raceDream);
		inMimicDream = new ZoneRequirement(mimicDream);

		doingTreeChallenge = new VarbitRequirement(3184, 1);
		startedRaceChallenge = new VarbitRequirement(2424, 1);

		startedNumberChallenge = new VarbitRequirement(2416, 1);

		needToTalkAtMiddle = new VarbitRequirement(2429, 1);

		finishedMimic = new VarbitRequirement(2405, 5, Operation.GREATER_EQUAL);
		finishedNumbers = new VarbitRequirement(2406, 6, Operation.GREATER_EQUAL);
		finishedTree = new VarbitRequirement(2407, 1, Operation.GREATER_EQUAL);
		finishedMemory = new VarbitRequirement(2408, 1, Operation.GREATER_EQUAL);
		finishedRace = new VarbitRequirement(2409, 1, Operation.GREATER_EQUAL);
		finishedChance = new VarbitRequirement(2410, 5, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		talkToLokar = new NpcStep(this, NpcID.LOKAR_SEARUNNER_6648, new WorldPoint(2620, 3693, 0), "Talk to Lokar Searunner on Rellekka's docks.");
		talkToLokar.addDialogSteps("You've been away from these parts a while?", "Why did you leave?", "Why not, I've always wondered what the state of my innards are!");

		talkToBrundt = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3667, 0), "Talk to Brundt in Rellekka's longhall.");
		talkToBrundt.addDialogStep("Ask about a Seal of Passage.");

		talkToLokarAgain = new NpcStep(this, NpcID.LOKAR_SEARUNNER_6648, new WorldPoint(2620, 3693, 0), "Return to Lokar Searunner on Rellekka's docks.", sealOfPassage);
		talkToLokarAgain.addDialogStep("Arrr! Yar! Let's be on our way, yar!");

		travelWithLokar = new NpcStep(this, NpcID.LOKAR_SEARUNNER, new WorldPoint(2620, 3693, 0), "Travel with Lokar in Rellekka.", sealOfPassage);
		travelWithLokar.addDialogStep("Go now.");

		climbLadder = new ObjectStep(this, ObjectID.LADDER_16960, new WorldPoint(2213, 3795, 0), "Climb up to the ship.");
		boardShip = new ObjectStep(this, ObjectID.LADDER_16959, new WorldPoint(2214, 3801, 1), "Climb up to the ship.");
		climbLadder.addSubSteps(travelWithLokar, boardShip);

		talkToBentley = new NpcStep(this, NpcID.CAPTAIN_BENTLEY, new WorldPoint(2222, 3796, 2), "Talk to Captain " +
			"Bentley.", sealOfPassageEquipped);
		talkToBentley.addDialogStep("Can we sail to Lunar Isle now?");

		climbDownSouthStairs = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2222, 3792, 2), "Go down to talk to 'Birds-Eye' Jack.");
		talkToJack = new NpcStep(this, NpcID.BIRDSEYE_JACK_3861, new WorldPoint(2222, 3788, 1), "Talk to 'Birds-Eye' Jack.");
		climbDownSouthStairs.addSubSteps(talkToJack);

		climbUpSouthStairs = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2222, 3792, 1), "Go upstairs to talk to Bentley again.");
		talkToBentleyAfterJack = new NpcStep(this, NpcID.CAPTAIN_BENTLEY, new WorldPoint(2222, 3796, 2), "Talk to Captain Bentley again.");
		talkToBentleyAfterJack.addDialogStep("Perhaps it's the Navigator's fault?");
		climbUpSouthStairs.addSubSteps(talkToBentleyAfterJack);

		goDownToJackAgain = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2222, 3792, 2), "Go down to talk to 'Birds-Eye' Jack again.");
		talkToJackAgain = new NpcStep(this, NpcID.BIRDSEYE_JACK_3861, new WorldPoint(2222, 3788, 1), "Talk to 'Birds-Eye' Jack.");
		goDownToJackAgain.addSubSteps(talkToJackAgain);

		goUpToShultz = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2222, 3792, 1), "Talk to 'Eagle-eye' Shultz on the north end of the ship.");
		talkToShultz = new NpcStep(this, NpcID.EAGLEEYE_SHULTZ, new WorldPoint(2224, 3813, 2), "Talk to 'Eagle-eye' Shultz on the north end of the ship.");
		goUpToShultz.addSubSteps(talkToShultz);

		goDownToBurns1 = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2227, 3794, 3), "Go to the bottom deck to talk to 'Beefy' Burns.");
		goDownToBurns2 = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2222, 3792, 2), "Go to the bottom deck to talk to 'Beefy' Burns.");
		goDownToBurns3 = new ObjectStep(this, ObjectID.STAIRS_16948, new WorldPoint(2225, 3808, 1), "Go to the bottom deck to talk to 'Beefy' Burns.");
		talkToBurns = new NpcStep(this, NpcID.BEEFY_BURNS, new WorldPoint(2221, 3788, 0), "Talk to 'Beefy' Burns in the south of the ship.");
		goDownToBurns1.addSubSteps(goDownToBurns2, goDownToBurns3, talkToBurns);

		goUpToLee1 = new ObjectStep(this, ObjectID.STAIRS_16946, new WorldPoint(2225, 3808, 0), "Go to the top deck to talk to 'Lecherous' Lee.");
		goUpToLee2 = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2222, 3792, 1), "Go to the top deck to talk to 'Lecherous' Lee.");
		goUpToLee3 = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2227, 3794, 2), "Go to the top deck to talk to 'Lecherous' Lee.");

		talkToLee = new NpcStep(this, NpcID.LECHEROUS_LEE, new WorldPoint(2224, 3788, 3), "Talk to 'Lecherous' Lee.");
		goUpToLee1.addSubSteps(goUpToLee2, goUpToLee3, talkToLee);

		goDownToDavey = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2227, 3794, 3), "Go down to the deck to talk to First mate 'Davey-boy'.");
		talkToDavey = new NpcStep(this, NpcID.FIRST_MATE_DAVEYBOY, new WorldPoint(2223, 3791, 2), "Talk to First mate 'Davey-boy'.");
		goDownToDavey.addSubSteps(talkToDavey);

		goUpToCabinBoy = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2227, 3794, 2), "Go up to the Cabin Boy.");
		talkToCabinBoy = new NpcStep(this, NpcID.CABIN_BOY, new WorldPoint(2225, 3789, 3), "Talk to the Cabin Boy.");
		goUpToCabinBoy.addSubSteps(talkToCabinBoy);

		getLensAndBullseye = new DetailedQuestStep(this, "Go get a bullseye lantern to add the emerald lens to.", bullseyeLantern, emeraldLens);
		extinguishLantern = new DetailedQuestStep(this, "Extinguish the bullseye lantern.", bullseyeLanternLit);
		replaceLens = new DetailedQuestStep(this, "Use the emerald lens on the lantern.", emeraldLensHighlighted, bullseyeLanternHighlighted);
		lightLantern = new DetailedQuestStep(this, "Light the emerald lantern.", tinderboxHighlighted, emeraldLantern);

		goUpToCannon1 = new ObjectStep(this, ObjectID.STAIRS_16946, new WorldPoint(2225, 3808, 0), "Go to the top deck to remove the cannon's symbol.");
		goUpToCannon2 = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2222, 3792, 1), "Go to the top deck to remove the cannon's symbol.");
		goUpToCannon3 = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2227, 3794, 2), "Go to the top deck to remove the cannon's symbol.");
		useLanternOnCannon = new ObjectStep(this, NullObjectID.NULL_17015, new WorldPoint(2227, 3786, 3), "Use the emerald lantern on the east cannon.", emeraldLanternLitHighlighted);
		useLanternOnCannon.addIcon(ItemID.EMERALD_LANTERN_9065);
		useLanternOnCannon.addDialogStep("Rub away!");
		goUpToCannon1.addSubSteps(goUpToCannon2, goUpToCannon3, useLanternOnCannon);

		goDownToChart = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2227, 3794, 3), "Go to the main deck to remove a wallchart's symbol.");
		goUpToChart1 = new ObjectStep(this, ObjectID.STAIRS_16946, new WorldPoint(2225, 3808, 0), "Go to the main deck to remove a wallchart's symbol.");
		goUpToChart2 = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2222, 3792, 1), "Go to the main deck to remove a wallchart's symbol.");
		useLanternOnChart = new ObjectStep(this, NullObjectID.NULL_17017, new WorldPoint(2222, 3790, 2), "Use the emerald lantern on the wallchart in the south room.", emeraldLanternLitHighlighted);
		useLanternOnChart.addIcon(ItemID.EMERALD_LANTERN_9065);
		useLanternOnChart.addDialogStep("Rub away!");
		goDownToChart.addSubSteps(goUpToChart1, goUpToChart2, useLanternOnChart);

		goDownToChest1 = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2227, 3794, 3), "Go to the bottom deck to remove more symbols.");
		goDownToChest2 = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2222, 3792, 2), "Go to the bottom deck to remove more symbols.");
		goDownToChest3 = new ObjectStep(this, ObjectID.STAIRS_16948, new WorldPoint(2225, 3808, 1), "Go to the bottom deck to remove more symbols.");
		useLanternOnChest = new ObjectStep(this, NullObjectID.NULL_17018, new WorldPoint(2223, 3811, 0), "Use the emerald lantern on the chest in the north of the bottom deck.", emeraldLanternLitHighlighted);
		useLanternOnChest.addIcon(ItemID.EMERALD_LANTERN_9065);
		useLanternOnChest.addDialogStep("Rub away!");
		useLanternOnChest.addSubSteps(goDownToChest1, goDownToChest2, goDownToChest3);

		useLanternOnPillar = new ObjectStep(this, NullObjectID.NULL_15587, new WorldPoint(2224, 3793, 0), "Use the emerald lantern on the pillar next to the kitchen of the bottom deck.", emeraldLanternLitHighlighted);
		useLanternOnPillar.addIcon(ItemID.EMERALD_LANTERN_9065);
		useLanternOnPillar.addDialogStep("Rub away!");
		useLanternOnCrate = new ObjectStep(this, NullObjectID.NULL_17016, new WorldPoint(2226, 3790, 0), "Use the emerald lantern on the crate in the kitchen of the bottom deck.", emeraldLanternLitHighlighted);
		useLanternOnCrate.addIcon(ItemID.EMERALD_LANTERN_9065);
		useLanternOnCrate.addDialogStep("Rub away!");

		goDownToSail = new ObjectStep(this, ObjectID.STAIRS_16947, new WorldPoint(2227, 3794, 3), "Go to the main deck and talk to Captain Bentley to set sail.");
		goUpToSail1 = new ObjectStep(this, ObjectID.STAIRS_16946, new WorldPoint(2225, 3808, 0), "Go to the main deck and talk to Captain Bentley to set sail.");
		goUpToSail2 = new ObjectStep(this, ObjectID.STAIRS_16945, new WorldPoint(2222, 3792, 1), "Go to the main deck and talk to Captain Bentley to set sail.");
		talkToBentleyToSail = new NpcStep(this, NpcID.CAPTAIN_BENTLEY, new WorldPoint(2222, 3796, 2), "Talk to Captain Bentley to set sail.");
		talkToBentleyToSail.addAlternateNpcs(NpcID.CAPTAIN_BENTLEY_6650);
		talkToBentleyToSail.addSubSteps(goDownToSail, goUpToSail1, goUpToSail2);

		goDownToIsle1 = new ObjectStep(this, ObjectID.LADDER_16961, new WorldPoint(2127, 3893, 2), "Climb down to Lunar Isle.");
		goDownToIsle2 = new ObjectStep(this, ObjectID.LADDER_16962, new WorldPoint(2118, 3894, 1), "Climb down to Lunar Isle.");
		enterTown = new DetailedQuestStep(this, new WorldPoint(2100, 3914, 0), "Enter the town on Lunar Isle.");
		enterTown.addSubSteps(goDownToIsle1, goDownToIsle2);

		talkToOneiromancer = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0), "Talk to the Oneiromancer in the south east of Lunar Isle.", sealOfPassage);

		enterChickenHouse = new NpcStep(this, NpcID.HOUSE, new WorldPoint(2085, 3931, 0), "Talk to Baba Yaga in the chicken-legged house in the north of Lunar Isle's town.", sealOfPassage);
		talkToYaga = new NpcStep(this, NpcID.BABA_YAGA, new WorldPoint(2451, 4646, 0), "Talk to Baba Yaga.", sealOfPassage);
		talkToYaga.addDialogStep("The Oneiromancer told me you may be able to help...");

		leaveChickenHouse = new ObjectStep(this, ObjectID.DOOR_16774, new WorldPoint(2451, 4644, 0), "Leave Baba Yaga's house.", specialVial);

		fillVial = new ObjectStep(this, ObjectID.SINK_16705, new WorldPoint(2091, 3922, 0), "Fill the vial with water.", specialVialHighlighted);
		fillVial.addIcon(ItemID.EMPTY_VIAL);
		fillVial.addSubSteps(leaveChickenHouse);

		addGuam = new DetailedQuestStep(this, "Add guam to the vial of water.", waterVial, guam);
		addGuamToMarrentill = new DetailedQuestStep(this, "Add guam to the marr vial.", marrentilPotion, guam);
		addGuam.addSubSteps(addGuamToMarrentill);

		addMarrentil = new DetailedQuestStep(this, "Add marrentill to the guam vial.", marrentill, guamPotion);
		grindTooth = new DetailedQuestStep(this, "Grind the suqah tooth.", suqahTooth, pestle);

		addToothToPotion = new DetailedQuestStep(this, "Add the ground tooth to the guam-marr potion.", groundTooth, guamMarrentilPotionHighlighted);

		killSuqahForTooth = new NpcStep(this, NpcID.SUQAH, "Kill the Suqah outside the town for a tooth. You'll also need 4 hides for later, so pick them up.", true);
		pickUpTooth = new ItemStep(this, "Pick up the suqah tooth.", suqahTooth);

		bringPotionToOneiromancer = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0), "Return to the Oneiromancer with the waking sleep vial.", sealOfPassage, sleepPotion);

		enterAirAltar = new ObjectStep(this, NullObjectID.NULL_34813, new WorldPoint(2985, 3292, 0),
			"Enter the Air Altar and use a dramen staff on it.", airTalisman, dramenStaff);
		enterAirAltar.addIcon(ItemID.AIR_TALISMAN);
		useOnAir = new ObjectStep(this, ObjectID.ALTAR_34760, new WorldPoint(2844, 4834, 0),
			"Use the staff on the altar.", dramenStaffHighlighted);
		useOnAir.addIcon(ItemID.DRAMEN_STAFF);

		enterFireAltar = new ObjectStep(this, NullObjectID.NULL_34817, new WorldPoint(3313, 3255, 0),
			"Enter the Fire Altar and use a partially made lunar staff on it.", fireTalisman, lunarStaffP1);
		enterFireAltar.addIcon(ItemID.FIRE_TALISMAN);
		useOnFire = new ObjectStep(this, ObjectID.ALTAR_34764, new WorldPoint(2585, 4838, 0),
			"Use the staff on the altar.", lunarStaffP1Highlighted);
		useOnFire.addIcon(ItemID.LUNAR_STAFF__PT1);

		enterWaterAltar = new ObjectStep(this, NullObjectID.NULL_34815, new WorldPoint(3185, 3165, 0),
			"Enter the Water Altar and use the partially made lunar staff on it.", waterTalisman, lunarStaffP2);
		enterWaterAltar.addIcon(ItemID.WATER_TALISMAN);
		useOnWater = new ObjectStep(this, ObjectID.ALTAR_34762, new WorldPoint(2716, 4836, 0), "Use the staff on the altar.", lunarStaffP2Highlighted);
		useOnWater.addIcon(ItemID.LUNAR_STAFF__PT2);

		enterEarthAltar = new ObjectStep(this, NullObjectID.NULL_34816, new WorldPoint(3306, 3474, 0),
			"Enter the Earth Altar and use a partially made lunar staff on it.", earthTalisman, lunarStaffP3);
		enterEarthAltar.addIcon(ItemID.EARTH_TALISMAN);
		useOnEarth = new ObjectStep(this, ObjectID.ALTAR_34763, new WorldPoint(2658, 4841, 0),
			"Use the staff on the altar.", lunarStaffP3Highlighted);
		useOnEarth.addIcon(ItemID.LUNAR_STAFF__PT3);

		talkToOneiromancerWithStaff = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0), "Bring the staff to the Oneiromancer in the south east of Lunar Isle.", sealOfPassage, lunarStaff);

		enterMine = new ObjectStep(this, ObjectID.LADDER_14996, new WorldPoint(2142, 3944, 0), "Enter the mine in the north east of Lunar Isle.", pickaxe);
		mineOre = new ObjectStep(this, ObjectID.STALAGMITE_15251, "Mine a lunar ore from one of the stalagmites in the area. (Tip: Grab 3 extra ores to save time during The Fremennik Exiles quest)", pickaxe);
		mineOre.addAlternateObjects(ObjectID.STALAGMITES_15250);
		smeltBar = new DetailedQuestStep(this, "Smelt the ore at a furnace.", lunarOre);
		makeHelmet = new DetailedQuestStep(this, "Make the lunar helmet on an anvil.", lunarBar, hammer);
		talkToPauline = new NpcStep(this, NpcID.PAULINE_POLARIS, new WorldPoint(2070, 3917, 0), "Talk to Pauline Polaris in the west of Lunar Isle's town.", sealOfPassage);
		talkToPauline.addDialogSteps("Pauline?", "Jane Blud-Hagic-Maid");
		talkToMeteora = new NpcStep(this, NpcID.METEORA, new WorldPoint(2083, 3890, 0), "Talk to Meteora in the south of Lunar Isle's town.", sealOfPassage);
		talkToSelene = new NpcStep(this, NpcID.SELENE, new WorldPoint(2079, 3912, 0), "Talk to Selene in the west of Lunar Isle's town.", sealOfPassage);
		talkToSelene.addDialogStep("I'm looking for a ring.");
		killSuqahForTiara = new NpcStep(this, NpcID.SUQAH, "Kill the Suqah outside the town for a special tiara. You'll also need 4 hides for making clothes, so pick them up.", true);
		pickUpTiara = new ItemStep(this, "Pick up the tiara.", tiara);
		killSuqahForTiara.addSubSteps(pickUpTiara);
		returnTiaraToMeteora = new NpcStep(this, NpcID.METEORA, new WorldPoint(2083, 3890, 0),
			"Return the tiara to Meteora in the south of Lunar Isle's town.", sealOfPassage, tiara);

		digForRing = new DigStep(this, new WorldPoint(2078, 3863, 0), "Dig in the south west of Lunar Isle for the ring.");
		talkToRimae = new NpcStep(this, NpcID.RIMAE_SIRSALIS, new WorldPoint(2104, 3909, 0),
			"Talk to Rimae in the east of Lunar Isle's town.", sealOfPassage, suqahHide4, coins400, needle, thread);
		talkToRimae.addDialogStep("You know the ceremonial clothes?");
		makeClothes = new NpcStep(this, NpcID.RIMAE_SIRSALIS, new WorldPoint(2104, 3909, 0),
			"Have Rimae in the east of Lunar Isle's town tan 4 suqah hides, and craft them into the torso, gloves, boots and legs.", sealOfPassage, suqahHide4, coins400, needle, thread);
		makeClothes.addDialogSteps("You know the ceremonial clothes?", "That seems like a fair deal.");

		bringItemsToOneiromancer = new BringLunarItems(this);

		useVialOnKindling = new DetailedQuestStep(this, "Use the waking sleep potion on the kindling", sleepPotionHighlighted, kindling);

		lightBrazier = new ObjectStep(this, NullObjectID.NULL_17025, new WorldPoint(2073, 3912, 0),
			"Equip your lunar equipment, some combat runes, and light the Brazier in the west of Lunar Isle's town.",
			sealOfPassage, tinderboxHighlighted, soakedKindling, helmEquipped, bodyEquipped, legsEquipped, bootsEquipped, glovesEquipped,
			cloakEquipped, amuletEquipped, ringEquipped, lunarStaffEquipped);
		lightBrazier.addIcon(ItemID.TINDERBOX);

		useKindlingOnBrazier = new ObjectStep(this, NullObjectID.NULL_17025, new WorldPoint(2073, 3912, 0),
			"Add the soaked kindling the Brazier in the west of Lunar Isle's town.",
			sealOfPassage, soakedKindlingHighlighted, helmEquipped, bodyEquipped, legsEquipped, bootsEquipped, glovesEquipped,
			cloakEquipped, amuletEquipped, ringEquipped, lunarStaffEquipped);
		useKindlingOnBrazier.addIcon(ItemID.SOAKED_KINDLING);

		if (client.getLocalPlayer() != null && client.getLocalPlayer().getPlayerComposition() != null && client.getLocalPlayer().getPlayerComposition().isFemale())
		{
			talkToEthereal = new NpcStep(this, NpcID.ETHEREAL_LADY, new WorldPoint(1762, 5088, 2), "Talk to the Ethereal Lady.");
			talkWithEtherealToFight = new NpcStep(this, NpcID.ETHEREAL_LADY, new WorldPoint(1762, 5088, 2), "Talk to the Ethereal Lady. Be prepared to fight.");
		}
		else
		{
			talkToEthereal = new NpcStep(this, NpcID.ETHEREAL_MAN, new WorldPoint(1762, 5088, 2), "Talk to the Ethereal Man.");
			talkWithEtherealToFight = new NpcStep(this, NpcID.ETHEREAL_MAN, new WorldPoint(1762, 5088, 2), "Talk to the Ethereal Man. Be prepared to fight.");
		}
		talkWithEtherealToFight.addDialogStep("Of course. I'm ready.");

		goToNumbers = new ObjectStep(this, ObjectID.PLATFORM_16633, new WorldPoint(1768, 5080, 2), "Go on the platform to the number challenge.");
		goToMimic = new ObjectStep(this, ObjectID.PLATFORM_16632, new WorldPoint(1765, 5079, 2), "Go on the platform to the music challenge.");
		goToRace = new ObjectStep(this, ObjectID.PLATFORM_16634, new WorldPoint(1770, 5088, 2), "Go on the platform to the race challenge.");
		goToMemory = new ObjectStep(this, ObjectID.PLATFORM_16636, new WorldPoint(1751, 5095, 2), "Go on the platform to the memory challenge.");
		goToTrees = new ObjectStep(this, ObjectID.PLATFORM_16635, new WorldPoint(1764, 5098, 2), "Go on the platform to the trees challenge.");
		goToChance = new ObjectStep(this, ObjectID.PLATFORM_16637, new WorldPoint(1751, 5080, 2), "Go on the platform to the chance challenge.");

		doMemoryChallenge = new MemoryChallenge(this);
		startTreeChallenge = new NpcStep(this, NpcID.ETHEREAL_PERCEPTIVE, new WorldPoint(1765, 5112, 2), "Talk to Ethereal perspective to begin. Cut 20 logs and deposit them on the log piles faster than the NPC.");
		startTreeChallenge.addDialogStep("Ok, let's go!");
		doRaceChallenge = new DetailedQuestStep(this, "Race to the end of the course to win!");
		doChanceChallenge = new ChanceChallenge(this);
		doNumberChallenge = new NumberChallenge(this);
		doMimicChallenge = new MimicChallenge(this);

		startNumber = new NpcStep(this, NpcID.ETHEREAL_NUMERATOR, new WorldPoint(1786, 5066, 2),
			"Talk to the Ethereal Numerator to begin the challenge.");

		doTreeChallenge = new DetailedQuestStep(this, "Chop 20 logs and deposit them in the log pile.");

		startRace = new NpcStep(this, NpcID.ETHEREAL_EXPERT, new WorldPoint(1788, 5068, 2), "Talk to the Ethereal Expert. Be prepared to race!");
		startRace.addDialogStep("Ok.");

		fightMe = new NpcStep(this, NpcID.ME, new WorldPoint(1823, 5087, 2), "Fight Me.");
		fightMe.addAlternateNpcs(NpcID.ME_786);

		leaveLecturn = new ObjectStep(this, ObjectID.MY_LIFE, new WorldPoint(1760, 5088, 2), "Read My life to return to Lunar Isle.");
		leaveLecturn.addDialogStep("Yes");
		finishQuest = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0),
			"Talk to the Oneiromancer in the south east of Lunar Isle to finish the quest!", sealOfPassage);
	}

	private void setupConditionalSteps()
	{
		boardingTheBoat = new ConditionalStep(this, travelWithLokar);
		boardingTheBoat.addStep(onCoveF1, boardShip);
		boardingTheBoat.addStep(atBaseOfStairs, climbLadder);

		setSail = new ConditionalStep(this, boardingTheBoat);
		setSail.addStep(onBoatF3, goDownToSail);
		setSail.addStep(onBoatF2, talkToBentleyToSail);
		setSail.addStep(onBoatF1, goUpToSail2);
		setSail.addStep(onBoatF0, goUpToSail1);

		enteringTheIsland = new ConditionalStep(this, setSail);
		enteringTheIsland.addStep(onLunarDock, goDownToIsle2);
		enteringTheIsland.addStep(onBoatLunar, goDownToIsle1);

		returnToTalkToYaga = new ConditionalStep(this, enteringTheIsland, "Talk to Baba Yaga in the chicken-legged house in the north of Lunar Isle's town.");
		returnToTalkToYaga.addSubSteps(talkToYaga, enterChickenHouse);

		returnToMakePotion = new ConditionalStep(this, enteringTheIsland, "Kill the Suqah outside the town on Lunar Isle for a tooth. You'll also need 4 hides for later, so pick them up.");
		returnToMakePotion.addSubSteps(killSuqahForTooth, pickUpTooth);

		returnToOneWithPotion = new ConditionalStep(this, enteringTheIsland, "Return to the Oneiromancer with the waking sleep vial.", sleepPotion);
		returnToOneWithPotion.addSubSteps(bringPotionToOneiromancer);

		returnWithStaff = new ConditionalStep(this, enteringTheIsland, "Return to the Oneiromancer with the Lunar Staff.", lunarStaff);
		returnWithStaff.addSubSteps(talkToOneiromancerWithStaff);

		makingHelm = new ConditionalStep(this, enterMine);
		makingHelm.addStep(lunarBar, makeHelmet);
		makingHelm.addStep(lunarOre, smeltBar);
		makingHelm.addStep(inLunarMine, mineOre);
		makingHelm.setLockingCondition(hadHelm);

		gettingRing = new ConditionalStep(this, talkToSelene);
		gettingRing.addStep(talkedToSelene, digForRing);
		gettingRing.setLockingCondition(hadRing);

		gettingCape = new ConditionalStep(this, talkToPauline);
		gettingCape.setLockingCondition(hadCape);

		gettingAmulet = new ConditionalStep(this, talkToMeteora);
		gettingAmulet.addStep(tiara, returnTiaraToMeteora);
		gettingAmulet.addStep(tiaraNearby, pickUpTiara);
		gettingAmulet.addStep(talkedToMeteora, killSuqahForTiara);
		gettingAmulet.setLockingCondition(hadAmulet);

		gettingClothes = new ConditionalStep(this, talkToRimae);
		gettingClothes.addStep(talkedToRimae, makeClothes);
		gettingClothes.setLockingCondition(hadClothes);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(bullseyeLantern, tinderboxHighlighted, guam, marrentill, dramenStaff, airTalisman, earthTalisman,
			fireTalisman, waterTalisman, pickaxe, pestle, hammer, thread, needle, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, combatRunes);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Multiple Suqah (level 111)", "Me (level 79)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.LOST_CITY, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.HERBLORE, 5));
		req.add(new SkillRequirement(Skill.CRAFTING, 61));
		req.add(new SkillRequirement(Skill.DEFENCE, 40));
		req.add(new SkillRequirement(Skill.FIREMAKING, 49));
		req.add(new SkillRequirement(Skill.MAGIC, 65));
		req.add(new SkillRequirement(Skill.MINING, 60));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 55));
		req.add(new ItemRequirement("Access to the following altars: \nAir, Earth, Fire, Water", -1, -1));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.MAGIC, 5000),
				new ExperienceReward(Skill.RUNECRAFT, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("50 x Astral Runes", ItemID.ASTRAL_RUNE, 50),
				new ItemReward("A Seal of Passage", ItemID.SEAL_OF_PASSAGE, 1),
				new ItemReward("A set of Lunar Equipment", -1, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Lunar Island"),
				new UnlockReward("Access to the Lunar Spellbook"),
				new UnlockReward("Access to the Astral Altar"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(talkToLokar, talkToBrundt, talkToLokarAgain, climbLadder, talkToBentley),
			bullseyeLantern, tinderboxHighlighted));
		allSteps.add(new PanelDetails("The curse", Arrays.asList(climbDownSouthStairs, climbUpSouthStairs, goUpToShultz, goDownToBurns1, goUpToLee1,
			goDownToDavey, goUpToCabinBoy, replaceLens, lightLantern, goUpToCannon1, goDownToChart, useLanternOnChest, useLanternOnPillar, useLanternOnCrate,
			talkToBentleyToSail), bullseyeLantern, tinderboxHighlighted));
		allSteps.add(new PanelDetails("Starting diplomacy", Arrays.asList(
			enterTown, talkToOneiromancer, returnToTalkToYaga, fillVial, addGuam, addMarrentil, returnToMakePotion, grindTooth,
			addToothToPotion, returnToOneWithPotion), guam, marrentill, pestle, sealOfPassage, combatGear));
		allSteps.add(new PanelDetails("Making the staff", Arrays.asList(
			enterAirAltar, enterFireAltar, enterWaterAltar, enterEarthAltar, returnWithStaff),
			dramenStaff, airTalisman, fireTalisman, waterTalisman, earthTalisman));

		PanelDetails makingHelmPanel = new PanelDetails("Making the helmet", Arrays.asList(
			enterMine, mineOre, smeltBar, makeHelmet), pickaxe, hammer);
		makingHelmPanel.setLockingStep(makingHelm);
		allSteps.add(makingHelmPanel);

		PanelDetails gettingCapePanel = new PanelDetails("Getting the cape", Collections.singletonList(talkToPauline));
		gettingCapePanel.setLockingStep(gettingCape);
		allSteps.add(gettingCapePanel);

		PanelDetails gettingAmuletPanel = new PanelDetails("Getting the amulet", Arrays.asList(
			talkToMeteora, killSuqahForTiara, returnTiaraToMeteora), combatGear);
		gettingAmuletPanel.setLockingStep(gettingAmulet);
		allSteps.add(gettingAmuletPanel);

		PanelDetails gettingRingPanel = new PanelDetails("Getting the ring", Arrays.asList(
			talkToSelene, digForRing), spade);
		gettingRingPanel.setLockingStep(gettingRing);
		allSteps.add(gettingRingPanel);

		PanelDetails gettingClothingPanel = new PanelDetails("Making the clothing", Collections.singletonList(
			makeClothes), coins400, needle, thread, suqahHide4);
		gettingClothingPanel.setLockingStep(gettingClothes);
		allSteps.add(gettingClothingPanel);

		allSteps.add(new PanelDetails("Return to Oneiromancer", Collections.singletonList(
			bringItemsToOneiromancer)));

		allSteps.add(new PanelDetails("Entering the Dreamland", Arrays.asList(
			useVialOnKindling, lightBrazier, useKindlingOnBrazier, talkToEthereal)));
		allSteps.add(new PanelDetails("Racing challenge", Arrays.asList(goToRace, startRace, doRaceChallenge)));
		allSteps.add(new PanelDetails("Number challenge", Arrays.asList(goToNumbers, doNumberChallenge)));
		allSteps.add(new PanelDetails("Mimic challenge", Arrays.asList(goToMimic, doMimicChallenge)));
		allSteps.add(new PanelDetails("Chance challenge", Arrays.asList(goToChance, doChanceChallenge)));
		allSteps.add(new PanelDetails("Memory challenge", Arrays.asList(goToMemory, doMemoryChallenge)));
		allSteps.add(new PanelDetails("Tree challenge", Arrays.asList(goToTrees, doTreeChallenge)));
		allSteps.add(new PanelDetails("Final challenge", Arrays.asList(talkWithEtherealToFight, fightMe, leaveLecturn, finishQuest), combatGear));
		return allSteps;
	}
}
