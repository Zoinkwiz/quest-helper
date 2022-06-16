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
package com.questhelper.quests.dragonslayer;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
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

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.DRAGON_SLAYER_I
)
public class DragonSlayer extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement chronicle, edgevilleTeleport, rimmingtonTeleport, antidragonShieldEquipped, antifirePotion, ringsOfRecoil;

	//Items Required
	ItemRequirement unfiredBowl, mindBomb, lobsterPot, silk, telegrabOrTenK, hammer, antidragonShield, planks3, planks2, planks1,
		nails90, nails60, nails30, twoThousandCoins, mapPart1, mapPart2, mapPart3, fullMap, melzarsKey, ratKey, ghostKey,
		skeletonKey, zombieKey, melzarKey, demonKey, combatGear, food;

	Requirement askedAboutShip, askedAboutShield, askedAboutMelzar, askedAboutThalzar, askedAboutLozar, askedAllQuestions, askedOracleAboutMap,
		inDwarvenMines, silkUsed, lobsterPotUsed, mindBombUsed, unfiredBowlUsed, thalzarDoorOpened, thalzarChest2Nearby, hasMapPart1, hasMapPart2,
		hasMapPart3, inMelzarsMaze, inRatRoom, inPostRatRoom, inGhostRoom, inPostGhostRoom, inSkeletonRoom, inPostSkeletonRoom, inLadderRoom,
		inRoomToBasement, inZombieRoom, inMelzarRoom, inDemonRoom, inLastMelzarRoom, hasShield, inShipHull, onShipDeck, hasBoughtBoat,
		hasRepairedHullOnce, hasRepairedHullTwice, fullyRepairedHull, onCrandorSurface, inCrandorUnderground, inElvargArea, inKaramjaVolcano, unlockedShortcut;

	ConditionalStep getLozarPiece, getThalzarPiece, getMelzarPiece, getShieldSteps;

	QuestStep startQuest, talkToOziach, returnToGuildmaster, askAboutShield, askAboutMelzar, askAboutThalzar, askAboutLozar, talkToOracle,
		goIntoDwarvenMine, useSilkOnDoor, usePotOnDoor, useUnfiredBowlOnDoor, useMindBombOnDoor, searchThalzarChest, searchThalzarChest2,
		optionsForLozarPiece, enterMelzarsMaze, killRat, openRedDoor, goUpRatLadder, killGhost, openOrangeDoor, goUpGhostLadder,
		killSkeleton, openYellowDoor, goDownSkeletonLadder, goDownLadderRoomLadder, goDownBasementEntryLadder, openBlueDoor, killZombie,
		killMelzar, openMagntaDoor, killLesserDemon, openGreenDoor, openMelzarChest, getShield, talkToKlarense, boardShip1, boardShip2, boardShip3,
		goDownShipLadder, repairShip, repairShip2, repairShip3, repairMap, talkToNed, boardShipToGo, talkToNedOnShip, enterCrandorHole, unlockShortcut,
		returnThroughShortcut, enterElvargArea, goDownIntoKaramjaVolcano, repairShipAgainAndSail, killElvarg, finishQuest;

	//Zones
	Zone dwarvenMines, melzarsMaze, melzarsBasement, ratRoom1, ratRoom2, ratRoom3, postRatRoom1, postRatRoom2, ghostRoom1, ghostRoom2,
		postGhostRoom1, postGhostRoom2, skeletonRoom1, skeletonRoom2, postSkeletonRoom1, postSkeletonRoom2, postSkeletonRoom3, ladderRoom,
		roomToBasement1, roomToBasement2, zombieRoom, melzarRoom1, melzarRoom2, demonRoom1, demonRoom2, lastMelzarRoom1, lastMelzarRoom2,
		shipHull, shipDeck, crandorSurface, crandorUnderground, elvargArea, karamjaVolcano;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);

		steps.put(1, talkToOziach);

		ConditionalStep askQuestions = new ConditionalStep(this, returnToGuildmaster);
		askQuestions.addStep(new Conditions(askedAboutShip, askedAboutShield, askedAboutMelzar, askedAboutThalzar), askAboutLozar);
		askQuestions.addStep(new Conditions(askedAboutShip, askedAboutShield, askedAboutMelzar), askAboutThalzar);
		askQuestions.addStep(new Conditions(askedAboutShip, askedAboutShield), askAboutMelzar);
		askQuestions.addStep(askedAboutShip, askAboutShield);

		getThalzarPiece = new ConditionalStep(this, talkToOracle);
		getThalzarPiece.addStep(new Conditions(thalzarDoorOpened, inDwarvenMines, thalzarChest2Nearby), searchThalzarChest2);
		getThalzarPiece.addStep(new Conditions(thalzarDoorOpened, inDwarvenMines), searchThalzarChest);
		getThalzarPiece.addStep(new Conditions(inDwarvenMines, silkUsed, lobsterPotUsed, unfiredBowlUsed), useMindBombOnDoor);
		getThalzarPiece.addStep(new Conditions(inDwarvenMines, silkUsed, lobsterPotUsed), useUnfiredBowlOnDoor);
		getThalzarPiece.addStep(new Conditions(inDwarvenMines, silkUsed), usePotOnDoor);
		getThalzarPiece.addStep(new Conditions(askedOracleAboutMap, inDwarvenMines), useSilkOnDoor);
		getThalzarPiece.addStep(askedOracleAboutMap, goIntoDwarvenMine);
		getThalzarPiece.setLockingCondition(hasMapPart1);
		getThalzarPiece.setBlocker(true);

		getLozarPiece = new ConditionalStep(this, optionsForLozarPiece);
		getLozarPiece.setLockingCondition(hasMapPart2);

		getMelzarPiece = new ConditionalStep(this, enterMelzarsMaze);
		getMelzarPiece.addStep(inLastMelzarRoom, openMelzarChest);
		getMelzarPiece.addStep(new Conditions(inDemonRoom, demonKey), openGreenDoor);
		getMelzarPiece.addStep(inDemonRoom, killLesserDemon);
		getMelzarPiece.addStep(new Conditions(inMelzarRoom, melzarKey), openMagntaDoor);
		getMelzarPiece.addStep(inMelzarRoom, killMelzar);
		getMelzarPiece.addStep(new Conditions(inZombieRoom, zombieKey), openBlueDoor);
		getMelzarPiece.addStep(inZombieRoom, killZombie);
		getMelzarPiece.addStep(inRoomToBasement, goDownBasementEntryLadder);
		getMelzarPiece.addStep(inLadderRoom, goDownLadderRoomLadder);
		getMelzarPiece.addStep(inPostSkeletonRoom, goDownSkeletonLadder);
		getMelzarPiece.addStep(new Conditions(inSkeletonRoom, skeletonKey), openYellowDoor);
		getMelzarPiece.addStep(inSkeletonRoom, killSkeleton);
		getMelzarPiece.addStep(inPostGhostRoom, goUpGhostLadder);
		getMelzarPiece.addStep(new Conditions(inGhostRoom, ghostKey), openOrangeDoor);
		getMelzarPiece.addStep(inGhostRoom, killGhost);
		getMelzarPiece.addStep(inPostRatRoom, goUpRatLadder);
		getMelzarPiece.addStep(new Conditions(inRatRoom, ratKey), openRedDoor);
		getMelzarPiece.addStep(inRatRoom, killRat);
		getMelzarPiece.setLockingCondition(hasMapPart3);

		getShieldSteps = new ConditionalStep(this, getShield);
		getShieldSteps.setLockingCondition(hasShield);

		ConditionalStep getBoat = new ConditionalStep(this, talkToKlarense);
		getBoat.addStep(new Conditions(hasBoughtBoat, inShipHull, hasRepairedHullTwice), repairShip3);
		getBoat.addStep(new Conditions(hasBoughtBoat, inShipHull, hasRepairedHullOnce), repairShip2);
		getBoat.addStep(new Conditions(hasBoughtBoat, inShipHull), repairShip);
		getBoat.addStep(new Conditions(hasBoughtBoat, onShipDeck), goDownShipLadder);
		getBoat.addStep(new Conditions(hasBoughtBoat, hasRepairedHullTwice), boardShip3);
		getBoat.addStep(new Conditions(hasBoughtBoat, hasRepairedHullOnce), boardShip2);
		getBoat.addStep(new Conditions(hasBoughtBoat), boardShip1);
		getBoat.setLockingCondition(fullyRepairedHull);

		ConditionalStep getCaptain = new ConditionalStep(this, repairMap);
		getCaptain.addStep(fullMap, talkToNed);

		ConditionalStep getMapAndBoat = new ConditionalStep(this, askQuestions);
		getMapAndBoat.addStep(new Conditions(hasMapPart1, hasMapPart2, hasMapPart3, askedAllQuestions, hasShield, fullyRepairedHull), getCaptain);
		getMapAndBoat.addStep(new Conditions(hasMapPart1, hasMapPart2, hasMapPart3, askedAllQuestions, hasShield), getBoat);
		getMapAndBoat.addStep(new Conditions(hasMapPart1, hasMapPart2, hasMapPart3, askedAllQuestions), getShieldSteps);
		getMapAndBoat.addStep(new Conditions(LogicType.OR, inMelzarsMaze, new Conditions(hasMapPart1, hasMapPart2, askedAllQuestions)), getMelzarPiece);
		getMapAndBoat.addStep(new Conditions(hasMapPart1, askedAllQuestions), getLozarPiece);
		getMapAndBoat.addStep(askedAllQuestions, getThalzarPiece);

		steps.put(2, getMapAndBoat);
		steps.put(3, getMapAndBoat);
		steps.put(6, getMapAndBoat);

		ConditionalStep goToCrandor = new ConditionalStep(this, boardShipToGo);
		goToCrandor.addStep(onShipDeck, talkToNedOnShip);
		steps.put(7, goToCrandor);

		ConditionalStep killingElvarg = new ConditionalStep(this, repairShipAgainAndSail);
		killingElvarg.addStep(inElvargArea, killElvarg);
		killingElvarg.addStep(new Conditions(inCrandorUnderground, unlockedShortcut), enterElvargArea);
		killingElvarg.addStep(new Conditions(inKaramjaVolcano, unlockedShortcut), returnThroughShortcut);
		killingElvarg.addStep(inCrandorUnderground, unlockShortcut);
		killingElvarg.addStep(onCrandorSurface, enterCrandorHole);
		killingElvarg.addStep(unlockedShortcut, goDownIntoKaramjaVolcano);

		steps.put(8, killingElvarg);

		steps.put(9, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		unfiredBowl = new ItemRequirement("Unfired bowl", ItemID.UNFIRED_BOWL);
		unfiredBowl.setTooltip("You can make one with soft clay at a Potter's Wheel with 8 Crafting.");
		mindBomb = new ItemRequirement("Wizard's mind bomb", ItemID.WIZARDS_MIND_BOMB);
		mindBomb.setTooltip("You can buy one from the Rising Sun Inn in Falador.");
		lobsterPot = new ItemRequirement("Lobster pot", ItemID.LOBSTER_POT);
		silk = new ItemRequirement("Silk", ItemID.SILK);
		ItemRequirement telegrab = new ItemRequirement("Telekinetic grab", ItemID.TELEKINETIC_GRAB, 1);
		telegrabOrTenK = new ItemRequirements(LogicType.OR, "Either 33 Magic for Telegrab and a ranged/mage weapon, or 10,000 coins",
			new ItemRequirement("Coins", ItemCollections.COINS, 10000), telegrab);
		ringsOfRecoil = new ItemRequirement("Rings of Recoil for Elvarg", ItemID.RING_OF_RECOIL, -1);
		chronicle = new ItemRequirement("The Chronicle for teleports to Champions' Guild", ItemID.CHRONICLE).isNotConsumed();
		antifirePotion = new ItemRequirement("Antifire potion for Elvarg", ItemCollections.ANTIFIRE_POTIONS, -1);
		edgevilleTeleport = new ItemRequirement("Teleports to Edgeville for getting to Oziach", ItemID.AMULET_OF_GLORY_T6, -1);
		rimmingtonTeleport = new ItemRequirement("Teleports to Port Sarim/Rimmington/Draynor Village area", ItemID.AMULET_OF_GLORY_T6, -1);

		mapPart1 = new ItemRequirement("Map part", ItemID.MAP_PART_1537);
		mapPart2 = new ItemRequirement("Map part", ItemID.MAP_PART_1536);
		mapPart3 = new ItemRequirement("Map part", ItemID.MAP_PART);
		melzarsKey = new ItemRequirement("Maze key", ItemID.MAZE_KEY);
		melzarsKey.setTooltip("You can get another maze key from the Guildmaster in the Champions' Guild.");
		ratKey = new ItemRequirement("Key", ItemID.KEY_1543);
		ghostKey = new ItemRequirement("Key", ItemID.KEY_1544);
		skeletonKey = new ItemRequirement("Key", ItemID.KEY_1545);
		zombieKey = new ItemRequirement("Key", ItemID.KEY_1546);
		melzarKey = new ItemRequirement("Key", ItemID.KEY_1547);
		demonKey = 	new ItemRequirement("Key", ItemID.KEY_1548);
		combatGear = new ItemRequirement("Combat equipment", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		antidragonShield = new ItemRequirement("Anti-dragon shield", ItemCollections.ANTIFIRE_SHIELDS);
		antidragonShieldEquipped = new ItemRequirement("Anti-dragon shield", ItemCollections.ANTIFIRE_SHIELDS, 1, true);
		planks3 = new ItemRequirement("Planks", ItemID.PLANK, 3);
		planks2 = new ItemRequirement("Planks", ItemID.PLANK, 2);
		planks1 = new ItemRequirement("Plank", ItemID.PLANK);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		nails90 = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS, 90);
		nails60 = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS, 60);
		nails30 = new ItemRequirement("Steel nails", ItemID.STEEL_NAILS, 30);
		twoThousandCoins = new ItemRequirement("Coins", ItemCollections.COINS, 2000);
		fullMap = new ItemRequirement("Crandor map", ItemID.CRANDOR_MAP);
	}

	public void loadZones()
	{
		dwarvenMines = new Zone(new WorldPoint(2960, 9696, 0), new WorldPoint(3062, 9854, 0));
		ratRoom1 = new Zone(new WorldPoint(2926, 3243, 0), new WorldPoint(2937, 3254, 0));
		ratRoom2 = new Zone(new WorldPoint(2930, 3255, 0), new WorldPoint(2937, 3257, 0));
		ratRoom3 = new Zone(new WorldPoint(2938, 3246, 0), new WorldPoint(2940, 3250, 0));
		postRatRoom1 = new Zone(new WorldPoint(2922, 3251, 0), new WorldPoint(2925, 3255, 0));
		postRatRoom2 = new Zone(new WorldPoint(2923, 3255, 0), new WorldPoint(2929, 3257, 0));
		ghostRoom1 = new Zone(new WorldPoint(2926, 3247, 1), new WorldPoint(2930, 3257, 1));
		ghostRoom2 = new Zone(new WorldPoint(2921, 3250, 1), new WorldPoint(2925, 3257, 1));
		postGhostRoom1 = new Zone(new WorldPoint(2931, 3252, 1), new WorldPoint(2936, 3254, 1));
		postGhostRoom2 = new Zone(new WorldPoint(2933, 3255, 1), new WorldPoint(2936, 3256, 1));
		skeletonRoom1 = new Zone(new WorldPoint(2922, 3250, 2), new WorldPoint(2932, 3252, 2));
		skeletonRoom2 = new Zone(new WorldPoint(2921, 3253, 2), new WorldPoint(2935, 3257, 2));

		postSkeletonRoom1 = new Zone(new WorldPoint(2922, 3239, 2), new WorldPoint(2925, 3249, 2));
		postSkeletonRoom2 = new Zone(new WorldPoint(2926, 3237, 2), new WorldPoint(2940, 3241, 2));
		postSkeletonRoom3 = new Zone(new WorldPoint(2936, 3242, 2), new WorldPoint(2940, 3243, 2));

		ladderRoom = new Zone(new WorldPoint(2937, 3237, 1), new WorldPoint(2940, 3241, 1));
		roomToBasement1 = new Zone(new WorldPoint(2937, 3237, 0), new WorldPoint(2940, 3245, 0));
		roomToBasement2 = new Zone(new WorldPoint(2932, 3240, 0), new WorldPoint(2936, 3242, 0));

		zombieRoom = new Zone(new WorldPoint(2931, 9639, 0), new WorldPoint( 2933, 9644, 0));
		melzarRoom1 = new Zone(new WorldPoint(2927, 9643, 0), new WorldPoint( 2930, 9651, 0));
		melzarRoom2 = new Zone(new WorldPoint(2931, 9646, 0), new WorldPoint( 2931, 9651, 0));

		demonRoom1 = new Zone(new WorldPoint(2924, 9652, 0), new WorldPoint( 2933, 9655, 0));
		demonRoom2 = new Zone(new WorldPoint(2934, 9647, 0), new WorldPoint( 2933, 9658, 0));
		melzarsMaze = new Zone(new WorldPoint(2922, 3237, 0), new WorldPoint(2942, 9658, 0));
		melzarsBasement = new Zone(new WorldPoint(2920, 9639, 0), new WorldPoint(1,2,0));

		lastMelzarRoom1 = new Zone(new WorldPoint(2924, 9656, 0), new WorldPoint(2942, 9656, 0));
		lastMelzarRoom2 = new Zone(new WorldPoint(2926, 9657, 0), new WorldPoint(2942, 9657, 0));

		shipDeck = new Zone(new WorldPoint(3041, 3207, 1), new WorldPoint(3050, 3209, 2));
		shipHull = new Zone(new WorldPoint(3041, 9639, 1), new WorldPoint(3050, 9641, 1));

		crandorSurface = new Zone(new WorldPoint(2810, 3228, 0), new WorldPoint(2867, 3312, 0));
		crandorUnderground = new Zone(new WorldPoint(2821, 9600, 0), new WorldPoint(2872, 9663, 0));

		elvargArea = new Zone(new WorldPoint(2846, 9625, 0), new WorldPoint(2867, 9651, 0));
		karamjaVolcano = new Zone(new WorldPoint(2827, 9547, 0), new WorldPoint(2867, 9599, 0));
	}

	public void setupConditions()
	{
		askedAboutMelzar = new VarplayerRequirement(177, false, 11);
		askedAboutThalzar = new VarplayerRequirement(177, false, 12);
		askedAboutLozar = new VarplayerRequirement(177, false, 13);
		askedAboutShip = new VarplayerRequirement(177, false, 14);
		askedAboutShield = new VarplayerRequirement(177, false, 15);
		askedAllQuestions = new Conditions(askedAboutShip, askedAboutShield, askedAboutMelzar, askedAboutThalzar, askedAboutLozar);
		askedOracleAboutMap = new VarbitRequirement(1832, 1);
		inDwarvenMines = new ZoneRequirement(dwarvenMines);
		silkUsed = new VarplayerRequirement(177, true, 17);
		unfiredBowlUsed = new VarplayerRequirement(177, true, 18);
		lobsterPotUsed = new VarplayerRequirement(177, true, 19);
		mindBombUsed = new VarplayerRequirement(177, true, 20);
		thalzarDoorOpened = new Conditions(silkUsed, unfiredBowlUsed, lobsterPotUsed, mindBombUsed);
		thalzarChest2Nearby = new ObjectCondition(ObjectID.CHEST_2588);

		hasMapPart1 = new Conditions(LogicType.OR, fullMap, mapPart1);
		hasMapPart2 = new Conditions(LogicType.OR, fullMap, mapPart2);
		hasMapPart3 = new Conditions(LogicType.OR, fullMap, mapPart3);

		inMelzarsMaze = new ZoneRequirement(melzarsMaze, melzarsBasement);
		inRatRoom = new ZoneRequirement(ratRoom1, ratRoom2, ratRoom3);

		inPostRatRoom = new ZoneRequirement(postRatRoom1, postRatRoom2);
		inGhostRoom = new ZoneRequirement(ghostRoom1, ghostRoom2);
		inPostGhostRoom = new ZoneRequirement(postGhostRoom1, postGhostRoom2);
		inSkeletonRoom = new ZoneRequirement(skeletonRoom1, skeletonRoom2);
		inPostSkeletonRoom = new ZoneRequirement(postSkeletonRoom1, postSkeletonRoom2, postSkeletonRoom3);
		inLadderRoom = new ZoneRequirement(ladderRoom);
		inRoomToBasement = new ZoneRequirement(roomToBasement1, roomToBasement2);
		inZombieRoom = new ZoneRequirement(zombieRoom);
		inMelzarRoom = new ZoneRequirement(melzarRoom1, melzarRoom2);
		inDemonRoom = new ZoneRequirement(demonRoom1, demonRoom2);
		inLastMelzarRoom = new ZoneRequirement(lastMelzarRoom1, lastMelzarRoom2);

		hasShield = antidragonShield;

		onShipDeck = new ZoneRequirement(shipDeck);
		inShipHull = new ZoneRequirement(shipHull);
		hasBoughtBoat = new VarplayerRequirement(176, 3);

		hasRepairedHullOnce = new VarbitRequirement(1835, 1);
		hasRepairedHullTwice = new VarbitRequirement(1836, 1);
		fullyRepairedHull = new VarbitRequirement(1837, 1);

		onCrandorSurface = new ZoneRequirement(crandorSurface);
		inCrandorUnderground = new ZoneRequirement(crandorUnderground);
		inElvargArea = new ZoneRequirement(elvargArea);
		inKaramjaVolcano = new ZoneRequirement(karamjaVolcano);

		unlockedShortcut = new VarplayerRequirement(177, true, 6);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.GUILDMASTER, new WorldPoint(3190, 3360, 0), "Talk to the Guildmaster in the Champions' Guild, south of Varrock.");
		startQuest.addDialogStep("Can I have a quest?");

		talkToOziach = new NpcStep(this, NpcID.OZIACH, new WorldPoint(3068, 3517, 0), "Talk to Oziach in his house in north western Edgeville.");
		talkToOziach.addDialogStep("Can you sell me a rune platebody?");
		talkToOziach.addDialogStep("The Guildmaster of the Champions' Guild told me.");
		talkToOziach.addDialogStep("I thought you were going to give me a quest.");
		talkToOziach.addDialogStep("A dragon, that sounds like fun.");

		returnToGuildmaster = new NpcStep(this, NpcID.GUILDMASTER, new WorldPoint(3190, 3360, 0), "Return to the Guildmaster and ask him all the available questions.");
		returnToGuildmaster.addDialogSteps("About my quest to kill the dragon...", "I talked to Oziach...", "Where can I find the right ship?");

		askAboutShield = new NpcStep(this, NpcID.GUILDMASTER, new WorldPoint(3190, 3360, 0), "Return to the Guildmaster and ask him all the available questions.");
		askAboutShield.addDialogSteps("About my quest to kill the dragon...", "I talked to Oziach...", "How can I protect myself from the dragon's breath?");

		askAboutMelzar = new NpcStep(this, NpcID.GUILDMASTER, new WorldPoint(3190, 3360, 0), "Return to the Guildmaster and ask him all the available questions.");
		askAboutMelzar.addDialogSteps("About my quest to kill the dragon...", "I talked to Oziach...", "How can I find the route to Crandor?", "Where is Melzar's map piece?");

		askAboutThalzar = new NpcStep(this, NpcID.GUILDMASTER, new WorldPoint(3190, 3360, 0), "Return to the Guildmaster and ask him all the available questions.");
		askAboutThalzar.addDialogSteps("About my quest to kill the dragon...", "I talked to Oziach...", "How can I find the route to Crandor?", "Where is Thalzar's map piece?");

		askAboutLozar = new NpcStep(this, NpcID.GUILDMASTER, new WorldPoint(3190, 3360, 0), "Return to the Guildmaster and ask him all the available questions.");
		askAboutLozar.addDialogSteps("About my quest to kill the dragon...", "I talked to Oziach...", "How can I find the route to Crandor?", "Where is Lozar's map piece?");

		returnToGuildmaster.addSubSteps(askAboutShield, askAboutMelzar, askAboutLozar, askAboutThalzar);

		talkToOracle = new NpcStep(this, NpcID.ORACLE, new WorldPoint(3014, 3501, 0), "Talk to the Oracle on top of Ice Mountain.", silk, lobsterPot, mindBomb, unfiredBowl);
		talkToOracle.addDialogStep("I seek a piece of the map to the island of Crandor.");

		goIntoDwarvenMine = new ObjectStep(this, ObjectID.TRAPDOOR_11867, new WorldPoint(3019, 3450,0), "Go down the ladder in the dwarven camp to the south into the Dwarven Mines.", silk, lobsterPot, mindBomb, unfiredBowl);

		useSilkOnDoor = new ObjectStep(this, ObjectID.MAGIC_DOOR_25115, new WorldPoint(3050, 9840, 0), "Go to the north east of the Dwarven Mines and use the silk on the magic door.", silk, lobsterPot, mindBomb, unfiredBowl);
		useSilkOnDoor.addIcon(ItemID.SILK);
		usePotOnDoor = new ObjectStep(this, ObjectID.MAGIC_DOOR_25115, new WorldPoint(3050, 9840, 0), "Go to the north east of the Dwarven Mines and use the lobster pot on the magic door.", lobsterPot, mindBomb, unfiredBowl);
		usePotOnDoor.addIcon(ItemID.LOBSTER_POT);
		useUnfiredBowlOnDoor = new ObjectStep(this, ObjectID.MAGIC_DOOR_25115, new WorldPoint(3050, 9840, 0), "Go to the north east of the Dwarven Mines and use the unfired bowl on the magic door.",  mindBomb, unfiredBowl);
		useUnfiredBowlOnDoor.addIcon(ItemID.UNFIRED_BOWL);
		useMindBombOnDoor = new ObjectStep(this, ObjectID.MAGIC_DOOR_25115, new WorldPoint(3050, 9840, 0), "Go to the north east of the Dwarven Mines and use the wizard's mind bomb on the magic door (BE CAREFUL NOT TO DRINK IT).", lobsterPot, mindBomb, unfiredBowl);
		useMindBombOnDoor.addIcon(ItemID.WIZARDS_MIND_BOMB);

		searchThalzarChest = new ObjectStep(this, ObjectID.CHEST_2587, new WorldPoint(3057, 9841, 0), "Search the chest for Thalzar's map piece.");
		searchThalzarChest2 = new ObjectStep(this, ObjectID.CHEST_2588, new WorldPoint(3057, 9841, 0), "Search the chest for Thalzar's map piece.");
		searchThalzarChest.addSubSteps(searchThalzarChest2);

		optionsForLozarPiece = new NpcStep(this, NpcID.WORMBRAIN, new WorldPoint(3015, 3195, 0), "Go to Port Sarim Jail. You can either pay Wormbrain there 10,000 coins for Lozar's map piece, or kill him with ranged/mage and telegrab it with level 33 magic.", telegrabOrTenK);
		optionsForLozarPiece.addDialogStep("I believe you've got a piece of a map that I need.");
		optionsForLozarPiece.addDialogStep("I suppose I could pay you for the map piece...");
		optionsForLozarPiece.addDialogStep("Alright then, 10,000 it is.");

		enterMelzarsMaze = new ObjectStep(this, ObjectID.DOOR_2595, new WorldPoint(2941, 3248, 0), "Enter Melzar's Maze north of Rimmington. Be prepared to fight multiple monsters up to a level 82 lesser demon.", melzarsKey);
		enterMelzarsMaze.addDialogSteps("About my quest to kill the dragon...", "I talked to Oziach...", "How can I find the route to Crandor?", "Where is Melzar's map piece?");

		killRat = new NpcStep(this, NpcID.ZOMBIE_RAT, new WorldPoint(2933, 3250, 0), "Kill the marked zombie rat for a key.");
		openRedDoor = new ObjectStep(this, ObjectID.RED_DOOR, new WorldPoint(2926, 3253, 0), "Go through the north west red door", ratKey);
		goUpRatLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2928, 3256, 0), "Climb up the ladder.");

		killGhost = new NpcStep(this, NpcID.GHOST_3975, new WorldPoint(2927, 3253, 1), "Kill the marked ghost for a key.", ghostKey);

		openOrangeDoor = new ObjectStep(this, ObjectID.ORANGE_DOOR, new WorldPoint(2931, 3253, 1), "Go through second yellow door from the north.", ghostKey);
		goUpGhostLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2934, 3254, 1), "Climb up the ladder.");

		killSkeleton = new NpcStep(this, NpcID.SKELETON_3972, new WorldPoint(2927, 3253, 2), "Kill the marked skeleton for a key", skeletonKey);
		openYellowDoor = new ObjectStep(this, ObjectID.YELLOW_DOOR, new WorldPoint(2924, 3249, 2), "Go through south west yellow door.", skeletonKey);
		goDownSkeletonLadder = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(2940, 3240, 2), "Climb down the ladder.");

		goDownLadderRoomLadder = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(2937, 3240, 1), "Climb down again.");
		goDownBasementEntryLadder = new ObjectStep(this, ObjectID.LADDER_2605, new WorldPoint(2932, 3240, 0), "Climb down the ladder into the basement.");
		killZombie = new NpcStep(this, NpcID.ZOMBIE_3980, new WorldPoint(2932, 9643, 0), "Kill the marked zombie for a key", zombieKey);
		openBlueDoor = new ObjectStep(this, ObjectID.BLUE_DOOR, new WorldPoint(2931, 9644, 0), "Go through the blue door in the north west corner.");

		killMelzar = new NpcStep(this, NpcID.MELZAR_THE_MAD, new WorldPoint(2929, 9649, 0), "Kill Melzar the Mad for a magenta key.", melzarKey);
		openMagntaDoor = new ObjectStep(this, ObjectID.MAGENTA_DOOR, new WorldPoint(2929, 9652, 0), "Go through the magenta door.");

		killLesserDemon = new NpcStep(this, NpcID.LESSER_DEMON_3982, new WorldPoint(2936, 9652, 0), "Kill the lesser demon. You can safe spot it from the spot east of the magenta door.", demonKey);
		openGreenDoor = new ObjectStep(this, ObjectID.GREEN_DOOR, new WorldPoint(2936, 9655, 0), "Go through the green door.", demonKey);

		openMelzarChest = new ObjectStep(this, ObjectID.CHEST_2603, new WorldPoint(2935, 9657, 0), "Open the chest and get Melzar's map part.");

		getShield = new NpcStep(this, NpcID.DUKE_HORACIO, new WorldPoint(3210, 3220, 1), "Talk to Duke Horacio on the first floor of Lumbridge castle for the anti-dragon shield if you don't have one.");
		getShield.addDialogStep("I seek a shield that will protect me from dragonbreath.");
		getShield.addDialogStep("Elvarg, the dragon of Crandor island!");
		getShield.addDialogStep("Yes");
		getShield.addDialogStep("So, are you going to give me the shield or not?");

		talkToKlarense = new NpcStep(this, NpcID.KLARENSE, new WorldPoint(3044, 3203, 0), "Talk to Klarense on the south Port Sarim docks and buy his boat.", planks3, nails90, hammer, twoThousandCoins);
		talkToKlarense.addDialogStep("I'd like to buy her.");
		talkToKlarense.addDialogStep("Yep, sounds good.");

		boardShip1 = new ObjectStep(this, ObjectID.GANGPLANK_2593, new WorldPoint(3047, 3205, 0), "Board your new ship.", nails90, planks3, hammer);
		boardShip2 = new ObjectStep(this, ObjectID.GANGPLANK_2593, new WorldPoint(3047, 3205, 0), "Board your new ship.", nails60, planks2, hammer);
		boardShip3 = new ObjectStep(this, ObjectID.GANGPLANK_2593, new WorldPoint(3047, 3205, 0), "Board your new ship.", nails30, planks1, hammer);
		boardShip1.addSubSteps(boardShip2, boardShip3);

		goDownShipLadder = new ObjectStep(this, ObjectID.LADDER_2590, new WorldPoint(3049, 3208, 1), "Go down into the ship's hull.");

		repairShip = new ObjectStep(this, NullObjectID.NULL_25036, new WorldPoint(3047, 9639, 1), "Repair the hole in the hull 3 times.", nails90, planks3, hammer);
		repairShip2 = new ObjectStep(this, NullObjectID.NULL_25036, new WorldPoint(3047, 9639, 1), "Repair the hole in the hull 2 more times.", nails60, planks2, hammer);
		repairShip3 = new ObjectStep(this, NullObjectID.NULL_25036, new WorldPoint(3047, 9639, 1), "Repair the hole in the hull 1 more time.", nails30, planks1, hammer);
		repairShip.addSubSteps(repairShip2, repairShip3);

		repairMap = new DetailedQuestStep(this, "Use the three map parts together to repair the map.", mapPart1, mapPart2, mapPart3);
		talkToNed = new NpcStep(this, NpcID.NED, new WorldPoint(3098, 3257, 0), "Bring Ned your map and ask him to be your captain.", fullMap);
		talkToNed.addDialogStep("You're a sailor? Could you take me to Crandor?");

		boardShipToGo = new ObjectStep(this, ObjectID.GANGPLANK_2593, new WorldPoint(3047, 3205, 0),
			"Prepare to fight Elvarg (level 83). When you're ready, go board your boat in Port Sarim and talk to Ned.", antidragonShield, combatGear);
		talkToNedOnShip = new NpcStep(this, NpcID.CAPTAIN_NED_5864, new WorldPoint(3048, 3208, 1), "Talk to Ned to go to Crandor.");
		talkToNedOnShip.addDialogStep("Yes, let's go!");
		boardShipToGo.addSubSteps(talkToNedOnShip);

		enterCrandorHole = new ObjectStep(this, ObjectID.HOLE_25154, new WorldPoint(2834, 3256, 0), "Go to the center of the island and go down the hole.");

		unlockShortcut = new ObjectStep(this, ObjectID.WALL_2606, new WorldPoint(2836, 9600, 0), "Go all the way south and through the wall to unlock the shortcut to return here if needed.");

		returnThroughShortcut = new ObjectStep(this, ObjectID.WALL_2606, new WorldPoint(2836, 9600, 0), "Return back through the shortcut.");

		enterElvargArea = new ObjectStep(this, ObjectID.WALL_25161, new WorldPoint(2846, 9635, 0), "Enter Elvarg's area and kill her.", antidragonShieldEquipped);
		goDownIntoKaramjaVolcano = new ObjectStep(this, ObjectID.ROCKS_11441, new WorldPoint(2857, 3169, 0), "Prepare to fight Elvarg again, and go to the Karamja volcano and enter it.", antidragonShieldEquipped, combatGear);
		repairShipAgainAndSail = new DetailedQuestStep(this, "As you did not unlock the shortcut, you will need to repair your ship again and sail to Crandor.", planks3, nails90, hammer);
		enterElvargArea.addSubSteps(goDownIntoKaramjaVolcano, repairShipAgainAndSail);

		killElvarg = new NpcStep(this, NpcID.ELVARG_8033, new WorldPoint(2855, 9637, 0), "Kill Elvarg.");

		finishQuest = new NpcStep(this, NpcID.OZIACH, new WorldPoint(3068, 3517, 0), "Talk to Oziach in his house in north western Edgeville to finish the quest.");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Zombie rats (level 3)");
		reqs.add("Ghosts (level 19)");
		reqs.add("Skeletons (level 22)");
		reqs.add("Zombies (level 24)");
		reqs.add("Melzar the Mad (level 43)");
		reqs.add("Lesser demon (level 82) (safespottable)");
		reqs.add("Elvarg (level 83)");

		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(unfiredBowl);
		reqs.add(mindBomb);
		reqs.add(lobsterPot);
		reqs.add(silk);
		reqs.add(telegrabOrTenK);
		reqs.add(hammer);
		reqs.add(planks3);
		reqs.add(nails90);
		reqs.add(twoThousandCoins);
		reqs.add(combatGear);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();

		reqs.add(chronicle);
		reqs.add(rimmingtonTeleport);
		reqs.add(edgevilleTeleport);
		reqs.add(ringsOfRecoil);
		reqs.add(antifirePotion);

		return reqs;
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
				new ExperienceReward(Skill.STRENGTH, 18650),
				new ExperienceReward(Skill.DEFENCE, 18650));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("The abiltiy to equip a Green D'hide Body, Rune Platebody & Dragon Platebody"),
				new UnlockReward("Access to Crandor"),
				new UnlockReward("Access to the Corsair Cove Resource Area."),
				new UnlockReward("Ability to receive dragons as a slayer task."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(startQuest, talkToOziach, returnToGuildmaster)));

		PanelDetails thalzarPanel = new PanelDetails("Thalzar's map piece",
			Arrays.asList(talkToOracle, goIntoDwarvenMine, useSilkOnDoor, usePotOnDoor, useUnfiredBowlOnDoor, useMindBombOnDoor, searchThalzarChest), silk, lobsterPot, unfiredBowl, mindBomb);
		thalzarPanel.setLockingStep(getThalzarPiece);
		allSteps.add(thalzarPanel);

		PanelDetails lozarPanel = new PanelDetails("Lozar's map piece",
			Collections.singletonList(optionsForLozarPiece), telegrabOrTenK);
		lozarPanel.setLockingStep(getLozarPiece);

		allSteps.add(lozarPanel);

		PanelDetails melzarPanel = new PanelDetails("Melzar's map piece",
			Arrays.asList(enterMelzarsMaze, killRat, openRedDoor, goUpRatLadder, killGhost, openOrangeDoor, goUpGhostLadder,
				killSkeleton, openYellowDoor, goDownSkeletonLadder, goDownLadderRoomLadder, goDownBasementEntryLadder, killZombie,
				openBlueDoor, killMelzar, openMagntaDoor, killLesserDemon, openGreenDoor, openMelzarChest),
			melzarsKey, combatGear, food);
		melzarPanel.setLockingStep(getMelzarPiece);

		allSteps.add(melzarPanel);

		PanelDetails antiDragonPanel = new PanelDetails("Get an anti-dragon shield", Collections.singletonList(getShield));
		antiDragonPanel.setLockingStep(getShieldSteps);

		allSteps.add(antiDragonPanel);

		PanelDetails boatPanel = new PanelDetails("Get a boat",
			Arrays.asList(talkToKlarense, boardShip1, goDownShipLadder, repairShip), hammer, planks3, nails90, twoThousandCoins);

		allSteps.add(boatPanel);

		PanelDetails captainPanel = new PanelDetails("Get a captain ready", Arrays.asList(repairMap, talkToNed));

		allSteps.add(captainPanel);

		allSteps.add(new PanelDetails("Slaying Elvarg", Arrays.asList(boardShipToGo, enterCrandorHole, unlockShortcut,
			returnThroughShortcut, enterElvargArea, killElvarg), combatGear, antidragonShield, food));

		allSteps.add(new PanelDetails("Finish the quest", Collections.singletonList(finishQuest)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new QuestPointRequirement(32));
	}
}
