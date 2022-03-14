/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2020, llamositopia <https://github.com/llamositopia>
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
package com.questhelper.quests.undergroundpass;

import com.google.common.collect.ImmutableMap;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.UNDERGROUND_PASS
)
public class UndergroundPass extends BasicQuestHelper
{
	//Items Required
	ItemRequirement rope1, rope2, ropeHighlight, bow, arrows, arrowsHighlight, spade, spadeHighlight, plank,
		plankHighlight, bucket, tinderbox, tinderboxHighlight, combatEquipment, robeTopEquipped, robeBottomEquipped,
		agilityPotions, oilyCloth, oilyClothHighlight, fireArrow, litArrow, litArrowEquipped, dollOfIbanHighlighted,
		orb1, orb2, orb3, orb4, railing, railingHighlight, unicornHorn, badgeJerro, badgeCarl, badgeHarry,
		badgeJerroHighlight, badgeCarlHighlight, badgeHarryHighlight, unicornHornHighlight, klanksGauntlets,
		witchsCat, amuletHolthion, amuletHolthionHighlight, amuletDoomion, amuletDoomionHighlight, amuletOthanian,
		amuletOthanianHighlight, dollOfIban, bucketHighlight, brew, klanksGauntletsEquipped, robeTop, robeBottom,
		ibansAshes, ibansDove, ibansShadow;

	//Items Recommended
	ItemRequirement food, staminaPotions, coins, telegrabRunes;

	Requirement inCastleFloor2, inWestArdougne, isBeforeRockslide1, isBeforeRockslide2, isBeforeRockslide3,
		isBeforeBridge, isNorthEastOfBridge, isBeforeThePit, isAfterThePit, isBeforeTheGrid, isAtTheGrid, isAfterTheGrid, isBeforeTrap1,
		isBeforeTrap2, isBeforeTrap3, isBeforeTrap4, isBeforeTrap5, isInWellArea, isBeforePlank2, isBeforePlank3,
		isAtOrb1, haveOrb1, haveOrb2, haveOrb3, haveOrb4, isInsideCell, isBeforeLedge, isAfterMaze,
		isInUnicornArea, isInUnicornArea2, haveUnicornHorn, isInKnightsArea, haveBadgeJerro,
		haveBadgeCarl, haveBadgeHarry, isBeforeIbansDoor, isInDwarfCavern, haveKlanksGauntlets, isInFinalArea,
		dollImbued, pouredBrew, dollAshed, kalragKilled, doveSmeared, clothInBag, isInFallArea, isInUndergroundSection2,
		usedOrb1, usedOrb2, usedOrb3, usedOrb4, destroyedAllOrbs, isInUndergroundSection3, isInMaze, usedHorn, usedBadgeJerro,
		usedBadgeCarl, usedBadgeHarry, givenWitchCat, isInTemple, isInPostIbanArea;

	DetailedQuestStep goToArdougneCastleFloor2, talkToKingLathas, goDownCastleStairs, enterWestArdougne, talkToKoftik,
		enterTheDungeon, climbOverRockslide1, climbOverRockslide2, climbOverRockslide3, talkToKoftikAtBridge,
		useClothOnArrow, lightArrow, walkNorthEastOfBridge, shootBridgeRope, collectPlank, searchBagForCloth,
		crossThePit, climbOverRockslide4, climbOverRockslide5, crossTheGrid, pullLeverAfterGrid, passTrap1,
		passTrap2, passTrap3, passTrap4, passTrap5, plankRock1, plankRock2, plankRock3, collectOrb1, collectOrb2,
		collectOrb3, collectOrb4, orbsToFurnace, climbDownWell, pickCellLock, digMud, crossLedge, navigateMaze,
		goThroughPipe, searchUnicornCage, useRailingOnBoulder, searchUnicornCageAgain, leaveUnicornArea,
		walkToKnights, killJerro, killCarl, killHarry, useBadgeJerroOnWell, useBadgeHarryOnWell, useBadgeCarlOnWell,
		useUnicornHornOnWell, openIbansDoor, descendCave, talkToNiloof, talkToKlank, goBackUpToIbansCavern,
		pickUpWitchsCat, useCatOnDoor, searchWitchsChest, killHolthion, killDoomion, killOthanian,
		searchDoomionsChest, returnToDwarfs, useBucketOnBrew, useBrewOnTomb, useTinderboxOnTomb, killKalrag,
		ascendToHalfSouless, searchCage, killDisciple, enterTemple, useDollOnWell, talkToKoftikAfterTemple,
		talkToKingLathasAfterTemple, leaveFallArea, useAshOnDoll, useShadowOnDoll, useDoveOnDoll, goUpToLathasToFinish;

	//Zones
	Zone castleFloor2, westArdougne, beforeRockslide1, beforeRockslide2, beforeRockslide3, beforeBridge,
		northEastOfBridge, westOfBridge, beforeThePit, afterThePit, beforeTheGrid, atTheGrid, afterTheGrid,
		beforeTrap1, beforeTrap2, beforeTrap3, beforeTrap4, beforeTrap5, wellArea, beforePlank2, beforePlank3,
		atOrb1, insideCell, beforeLedge, afterMaze, afterMazeShortcut, inUnicornArea, inUnicornArea2, inKnightsArea1,
		inKnightsArea2, inKnightsArea3, beforeIbansDoor, inDwarfCavern, inFinalArea, inTemple, inFallArea,
		inUndergroundSection2P1, inUndergroundSection2P2, inUndergroundSection2P3, inUndergroundSection2P4,
		inUndergroundSection3, inMaze1, inMaze2, inPostIbanArea;

	private void setupItemReqs()
	{
		rope1 = new ItemRequirement("Rope", ItemID.ROPE);
		rope2 = new ItemRequirement("Rope, multiple in case you fail an agility check", ItemID.ROPE, -1);
		ropeHighlight = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlight.setHighlightInInventory(true);
		bow = new ItemRequirement("Bow (not crossbow)", ItemCollections.getBows(), 1, true);
		arrows = new ItemRequirement("Arrows (metal, unpoisoned)", ItemCollections.getMetalArrows());
		arrowsHighlight = new ItemRequirement("Arrows (metal, unpoisoned)", ItemCollections.getMetalArrows());
		arrowsHighlight.setHighlightInInventory(true);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		spadeHighlight = new ItemRequirement("Spade", ItemID.SPADE);
		spadeHighlight.setHighlightInInventory(true);
		plank = new ItemRequirement("Plank", ItemID.PLANK);
		plankHighlight = new ItemRequirement("Plank", ItemID.PLANK);
		plankHighlight.setHighlightInInventory(true);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight.setHighlightInInventory(true);
		bucketHighlight.setTooltip("You can grab a bucket from the southwest corner of the large dwarf encampment building.");
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlight = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlight.setHighlightInInventory(true);
		combatEquipment = new ItemRequirement("Combat Equipment", -1, -1);
		combatEquipment.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(ItemID.SHARK);
		staminaPotions = new ItemRequirement("Stamina Potions", ItemCollections.getStaminaPotions());
		coins = new ItemRequirement("Coins (to buy food, 75 ea)", ItemCollections.getCoins(), 750);
		agilityPotions = new ItemRequirement("Agility Potions", ItemCollections.getAgilityPotions());
		telegrabRunes = new ItemRequirements("Telegrab Runes", new ItemRequirement("Air rune", ItemID.AIR_RUNE),
			new ItemRequirement("Law rune", ItemID.LAW_RUNE));
		oilyCloth = new ItemRequirement("Oily Cloth", ItemID.OILY_CLOTH);
		oilyCloth.setTooltip("You can get another by searching the equipment by the fireplace beside Koftik.");
		oilyClothHighlight = new ItemRequirement("Oily Cloth", ItemID.OILY_CLOTH);
		oilyClothHighlight.setHighlightInInventory(true);
		fireArrow = new ItemRequirement("Fire Arrow", ItemID.BRONZE_FIRE_ARROW);
		fireArrow.setHighlightInInventory(true);
		fireArrow.addAlternates(ItemID.IRON_FIRE_ARROW, ItemID.STEEL_FIRE_ARROW, ItemID.MITHRIL_FIRE_ARROW, ItemID.ADAMANT_FIRE_ARROW, ItemID.RUNE_FIRE_ARROW);
		litArrow = new ItemRequirement("Lit Arrow", ItemID.BRONZE_FIRE_ARROW_LIT);
		litArrow.setHighlightInInventory(true);
		litArrow.addAlternates(ItemID.IRON_FIRE_ARROW_LIT, ItemID.STEEL_FIRE_ARROW_LIT, ItemID.MITHRIL_FIRE_ARROW_LIT, ItemID.ADAMANT_FIRE_ARROW_LIT, ItemID.RUNE_FIRE_ARROW_LIT);
		litArrowEquipped = new ItemRequirement("Lit Arrow", ItemID.BRONZE_FIRE_ARROW_LIT, 1, true);
		litArrowEquipped.addAlternates(ItemID.IRON_FIRE_ARROW_LIT, ItemID.STEEL_FIRE_ARROW_LIT, ItemID.MITHRIL_FIRE_ARROW_LIT, ItemID.ADAMANT_FIRE_ARROW_LIT, ItemID.RUNE_FIRE_ARROW_LIT);
		orb1 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT_1484);
		orb2 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT_1483);
		orb3 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT_1482);
		orb4 = new ItemRequirement("Orb of light", ItemID.ORB_OF_LIGHT);
		railing = new ItemRequirement("Railing", ItemID.PIECE_OF_RAILING);
		railingHighlight = new ItemRequirement("Railing", ItemID.PIECE_OF_RAILING);
		railingHighlight.setHighlightInInventory(true);
		unicornHorn = new ItemRequirement("Unicorn Horn", ItemID.UNICORN_HORN_1487);
		unicornHorn.setTooltip("You can get this from the cage in the previous room");
		unicornHornHighlight = new ItemRequirement("Unicorn Horn", ItemID.UNICORN_HORN_1487);
		unicornHornHighlight.setTooltip("You can get this from the cage in the previous room");
		unicornHornHighlight.setHighlightInInventory(true);
		badgeJerro = new ItemRequirement("Badge (Sir Jerro)", ItemID.PALADINS_BADGE);
		badgeJerroHighlight = new ItemRequirement("Badge (Sir Jerro)", ItemID.PALADINS_BADGE);
		badgeJerroHighlight.setHighlightInInventory(true);
		badgeCarl = new ItemRequirement("Badge (Sir Carl)", ItemID.PALADINS_BADGE_1489);
		badgeCarlHighlight = new ItemRequirement("Badge (Sir Carl)", ItemID.PALADINS_BADGE_1489);
		badgeCarlHighlight.setHighlightInInventory(true);
		badgeHarry = new ItemRequirement("Badge (Sir Harry)", ItemID.PALADINS_BADGE_1490);
		badgeHarryHighlight = new ItemRequirement("Badge (Sir Harry)", ItemID.PALADINS_BADGE_1490);
		badgeHarryHighlight.setHighlightInInventory(true);
		klanksGauntlets = new ItemRequirement("Klank's gauntlets", ItemID.KLANKS_GAUNTLETS);
		klanksGauntletsEquipped = new ItemRequirement("Klank's gauntlets", ItemID.KLANKS_GAUNTLETS, 1, true);
		klanksGauntletsEquipped.setTooltip("You can get a pair from Klank in the Dwarf camp");
		witchsCat = new ItemRequirement("Witch's Cat", ItemID.WITCHS_CAT);
		witchsCat.setHighlightInInventory(true);
		amuletHolthion = new ItemRequirement("Holthion's Amulet", ItemID.AMULET_OF_HOLTHION);
		amuletHolthionHighlight = new ItemRequirement("Holthion's Amulet", ItemID.AMULET_OF_HOLTHION);
		amuletHolthionHighlight.setHighlightInInventory(true);
		amuletDoomion = new ItemRequirement("Doomion's Amulet", ItemID.AMULET_OF_DOOMION);
		amuletDoomionHighlight = new ItemRequirement("Doomion's Amulet", ItemID.AMULET_OF_DOOMION);
		amuletDoomionHighlight.setHighlightInInventory(true);
		amuletOthanian = new ItemRequirement("Othanian's Amulet", ItemID.AMULET_OF_OTHANIAN);
		amuletOthanianHighlight = new ItemRequirement("Othanian's Amulet", ItemID.AMULET_OF_OTHANIAN);
		amuletOthanianHighlight.setHighlightInInventory(true);
		dollOfIban = new ItemRequirement("Doll of Iban", ItemID.DOLL_OF_IBAN);
		dollOfIbanHighlighted = new ItemRequirement("Doll of Iban", ItemID.DOLL_OF_IBAN);
		dollOfIbanHighlighted.setHighlightInInventory(true);
		brew = new ItemRequirement("Dwarf brew", ItemID.DWARF_BREW);
		brew.setHighlightInInventory(true);
		robeTop = new ItemRequirement("Zamorak monk top", ItemID.ZAMORAK_MONK_TOP);
		robeBottom = new ItemRequirement("Zamorak monk bottom", ItemID.ZAMORAK_MONK_BOTTOM);
		robeTopEquipped = new ItemRequirement("Zamorak monk top", ItemID.ZAMORAK_MONK_TOP, 1, true);
		robeBottomEquipped = new ItemRequirement("Zamorak monk bottom", ItemID.ZAMORAK_MONK_BOTTOM, 1, true);
		ibansAshes = new ItemRequirement("Iban's ashes", ItemID.IBANS_ASHES);
		ibansAshes.setHighlightInInventory(true);
		ibansDove = new ItemRequirement("Iban's dove", ItemID.IBANS_DOVE);
		ibansDove.setHighlightInInventory(true);
		ibansShadow = new ItemRequirement("Iban's shadow", ItemID.IBANS_SHADOW);
		ibansShadow.setHighlightInInventory(true);
	}

	private void setupZones()
	{
		castleFloor2 = new Zone(new WorldPoint(2568, 3283, 1), new WorldPoint(2591, 3310, 1));
		westArdougne = new Zone(new WorldPoint(2433, 3264, 0), new WorldPoint(2557, 3337, 2));

		// Section 1
		beforeRockslide1 = new Zone(new WorldPoint(2473, 9713, 0), new WorldPoint(2500, 9720, 0));
		beforeRockslide2 = new Zone(new WorldPoint(2471, 9705, 0), new WorldPoint(2481, 9712, 0));
		beforeRockslide3 = new Zone(new WorldPoint(2457, 9704, 0), new WorldPoint(2470, 9711, 0));
		beforeBridge = new Zone(new WorldPoint(2445, 9713, 0), new WorldPoint(2467, 9719, 0));
		northEastOfBridge = new Zone(new WorldPoint(2445, 9719, 0), new WorldPoint(2456, 9727, 0));
		inFallArea = new Zone(new WorldPoint(2440, 9628, 0), new WorldPoint(2486, 9657, 0));

		// Section 2
		inUndergroundSection2P1 = new Zone(new WorldPoint(2431, 9660, 0), new WorldPoint(2443, 9728, 0));
		inUndergroundSection2P2 = new Zone(new WorldPoint(2444, 9660, 0), new WorldPoint(2494, 9703, 0));
		inUndergroundSection2P3 = new Zone(new WorldPoint(2485, 9704, 0), new WorldPoint(2494, 9707, 0));
		inUndergroundSection2P4 = new Zone(new WorldPoint(2380, 9663, 0), new WorldPoint(2430, 9698, 0));
		inUndergroundSection3 = new Zone(new WorldPoint(2365, 9590, 0), new WorldPoint(2430, 9661, 0));
		westOfBridge = new Zone(new WorldPoint(2431, 9684, 0), new WorldPoint(2443, 9728, 0));
		beforeThePit = new Zone(new WorldPoint(2439, 9691, 0), new WorldPoint(2463, 9703, 0));
		afterThePit = new Zone(new WorldPoint(2463, 9691, 0), new WorldPoint(2493, 9709, 0));
		beforeTheGrid = new Zone(new WorldPoint(2482, 9668, 0), new WorldPoint(2494, 9690, 0));
		atTheGrid = new Zone(new WorldPoint(2467, 9672, 0), new WorldPoint(2482, 9684, 0));
		afterTheGrid = new Zone(new WorldPoint(2466, 9672, 0), new WorldPoint(2466, 9683, 0));
		beforeTrap1 = new Zone(new WorldPoint(2444, 9668, 0), new WorldPoint(2465, 9690, 0));
		beforeTrap2 = new Zone(new WorldPoint(2441, 9676, 0), new WorldPoint(2442, 9678, 0));
		beforeTrap3 = new Zone(new WorldPoint(2436, 9675, 0), new WorldPoint(2439, 9683, 0));
		beforeTrap4 = new Zone(new WorldPoint(2433, 9675, 0), new WorldPoint(2434, 9677, 0));
		beforeTrap5 = new Zone(new WorldPoint(2431, 9675, 0), new WorldPoint(2431, 9677, 0));
		wellArea = new Zone(new WorldPoint(2409, 9663, 0), new WorldPoint(2430, 9680, 0));
		beforePlank2 = new Zone(new WorldPoint(2416, 9682, 0), new WorldPoint(2420, 9684, 0));
		beforePlank3 = new Zone(new WorldPoint(2414, 9686, 0), new WorldPoint(2420, 9688, 0));
		atOrb1 = new Zone(new WorldPoint(2412, 9690, 0), new WorldPoint(2422, 9698, 0));

		insideCell = new Zone(new WorldPoint(2392, 9650, 0), new WorldPoint(2394, 9654, 0));
		beforeLedge = new Zone(new WorldPoint(2374, 9639, 0), new WorldPoint(2395, 9647, 0));
		inMaze1 = new Zone(new WorldPoint(2369, 9616, 0), new WorldPoint(2415, 9638, 0));
		inMaze2 = new Zone(new WorldPoint(2398, 9639, 0), new WorldPoint(2404, 9646, 0));
		afterMaze = new Zone(new WorldPoint(2416, 9597, 0), new WorldPoint(2432, 9645, 0));
		afterMazeShortcut = new Zone(new WorldPoint(2404, 9615, 0), new WorldPoint(2420, 9624, 0));
		inUnicornArea = new Zone(new WorldPoint(2391, 9586, 0), new WorldPoint(2415, 9612, 0));
		inUnicornArea2 = new Zone(new WorldPoint(2366, 9586, 0), new WorldPoint(2390, 9612, 0));
		inKnightsArea1 = new Zone(new WorldPoint(2369, 9666, 0), new WorldPoint(2379, 9728, 0));
		inKnightsArea2 = new Zone(new WorldPoint(2379, 9693, 0), new WorldPoint(2413, 9728, 0));
		inKnightsArea3 = new Zone(new WorldPoint(2414, 9705, 0), new WorldPoint(2429, 9728, 0));
		beforeIbansDoor = new Zone(new WorldPoint(2367, 9709, 0), new WorldPoint(2386, 9727, 0));

		inFinalArea = new Zone(new WorldPoint(2110, 4544, 1), new WorldPoint(2177, 4739, 1));
		inDwarfCavern = new Zone(new WorldPoint(2304, 9789, 0), new WorldPoint(2365, 9921, 0));

		inTemple = new Zone(new WorldPoint(2132, 4641, 1), new WorldPoint(2143, 4654, 1));
		inPostIbanArea = new Zone(new WorldPoint(2439, 9603, 0), new WorldPoint(2491, 9615, 0));
	}

	private void setupConditions()
	{
		clothInBag = new VarbitRequirement(9138, 1);

		inCastleFloor2 = new ZoneRequirement(castleFloor2);
		inWestArdougne = new ZoneRequirement(westArdougne);
		isBeforeRockslide1 = new ZoneRequirement(beforeRockslide1);
		isBeforeRockslide2 = new ZoneRequirement(beforeRockslide2);
		isBeforeRockslide3 = new ZoneRequirement(beforeRockslide3);
		isInFallArea = new ZoneRequirement(inFallArea);
		isBeforeBridge = new ZoneRequirement(beforeBridge);
		isNorthEastOfBridge = new ZoneRequirement(northEastOfBridge);
		isBeforeThePit = new ZoneRequirement(westOfBridge, beforeThePit);
		isAfterThePit = new ZoneRequirement(afterThePit);
		isBeforeTheGrid = new ZoneRequirement(beforeTheGrid);
		isAtTheGrid = new ZoneRequirement(atTheGrid);
		isAfterTheGrid = new ZoneRequirement(afterTheGrid);
		isBeforeTrap1 = new ZoneRequirement(beforeTrap1);
		isBeforeTrap2 = new ZoneRequirement(beforeTrap2);
		isBeforeTrap3 = new ZoneRequirement(beforeTrap3);
		isBeforeTrap4 = new ZoneRequirement(beforeTrap4);
		isBeforeTrap5 = new ZoneRequirement(beforeTrap5);
		usedOrb1 = new VarbitRequirement(9122, 1);
		usedOrb2 = new VarbitRequirement(9121, 1);
		usedOrb3 = new VarbitRequirement(9120, 1);
		usedOrb4 = new VarbitRequirement(9119, 1);
		destroyedAllOrbs = new Conditions(usedOrb1, usedOrb2, usedOrb3, usedOrb4);

		haveOrb1 = new Conditions(LogicType.OR, usedOrb1, orb1);
		haveOrb2 = new Conditions(LogicType.OR, usedOrb2, orb2);
		haveOrb3 = new Conditions(LogicType.OR, usedOrb3, orb3);
		haveOrb4 = new Conditions(LogicType.OR, usedOrb4, orb4);
		isInWellArea = new ZoneRequirement(wellArea);
		isInUndergroundSection2 = new ZoneRequirement(inUndergroundSection2P1, inUndergroundSection2P2, inUndergroundSection2P3, inUndergroundSection2P4);
		isInUndergroundSection3 = new ZoneRequirement(inUndergroundSection3);
		isBeforePlank2 = new ZoneRequirement(beforePlank2);
		isBeforePlank3 = new ZoneRequirement(beforePlank3);
		isAtOrb1 = new ZoneRequirement(atOrb1);
		isInsideCell = new ZoneRequirement(insideCell);
		isBeforeLedge = new ZoneRequirement(beforeLedge);
		isInMaze = new ZoneRequirement(inMaze1, inMaze2);
		isAfterMaze = new ZoneRequirement(afterMaze, afterMazeShortcut);
		isInUnicornArea = new ZoneRequirement(inUnicornArea);
		isInUnicornArea2 = new ZoneRequirement(inUnicornArea2);
		usedHorn = new VarbitRequirement(9136, 1);
		haveUnicornHorn = new Conditions(LogicType.OR, unicornHorn, usedHorn);
		isInKnightsArea = new ZoneRequirement(inKnightsArea1, inKnightsArea2, inKnightsArea3);

		usedBadgeJerro = new VarbitRequirement(9128, 1);
		usedBadgeCarl = new VarbitRequirement(9129, 1);
		usedBadgeHarry = new VarbitRequirement(9130, 1);

		haveBadgeCarl = new Conditions(LogicType.OR, badgeCarl, usedBadgeCarl);
		haveBadgeHarry = new Conditions(LogicType.OR, badgeHarry, usedBadgeHarry);
		haveBadgeJerro = new Conditions(LogicType.OR, badgeJerro, usedBadgeJerro);
		isBeforeIbansDoor = new ZoneRequirement(inKnightsArea3, beforeIbansDoor);
		isInFinalArea = new ZoneRequirement(inFinalArea);
		isInDwarfCavern = new ZoneRequirement(inDwarfCavern);
		haveKlanksGauntlets = klanksGauntlets;
		givenWitchCat = new VarbitRequirement(9123, 1);
		dollImbued = new VarbitRequirement(9118, 1);
		pouredBrew = new VarbitRequirement(9134, 1);
		dollAshed = new VarbitRequirement(9117, 1);
		kalragKilled = new VarbitRequirement(9115, 1);
		doveSmeared = new VarbitRequirement(9116, 1);
		isInTemple = new ZoneRequirement(inTemple);
		isInPostIbanArea = new ZoneRequirement(inPostIbanArea);
	}

	public void setupSteps()
	{
		goToArdougneCastleFloor2 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0), "Go to the second floor of Ardougne Castle.");
		talkToKingLathas = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Talk to King Lathas.");
		talkToKingLathas.addDialogSteps("I am. What's our plan?", "I'm ready.", "Yes.");
		talkToKingLathas.addSubSteps(goToArdougneCastleFloor2);

		goDownCastleStairs = new ObjectStep(this, ObjectID.STAIRCASE_15648, new WorldPoint(2572, 3296, 1), "Talk to Koftik in West Ardougne.");
		enterWestArdougne = new ObjectStep(this, ObjectID.ARDOUGNE_WALL_DOOR_8739, new WorldPoint(2558, 3300, 0), "Talk to Koftik in West Ardougne.");
		talkToKoftik = new NpcStep(this, NpcID.KOFTIK_8976, new WorldPoint(2435, 3314, 0), "Talk to Koftik outside the West Ardougne cave.");
		talkToKoftik.addDialogStep("I'll take my chances.");
		talkToKoftik.addSubSteps(goDownCastleStairs, enterWestArdougne);

		enterTheDungeon = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3213, new WorldPoint(2434, 3315, 0), "Enter the dungeon.", rope1, bow, arrows, spade, plank, bucket, tinderbox);
		climbOverRockslide1 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2480, 9713, 0), "Climb-over rockslide.");
		climbOverRockslide2 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2471, 9706, 0), "Climb-over rockslide.");
		climbOverRockslide3 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2458, 9712, 0), "Climb-over rockslide.");
		talkToKoftikAtBridge = new NpcStep(this, NpcID.KOFTIK_8976, "Talk to Koftik inside the cave.");
		talkToKoftikAtBridge.addSubSteps(enterTheDungeon, climbOverRockslide1, climbOverRockslide2, climbOverRockslide3);

		searchBagForCloth = new ObjectStep(this, ObjectID.ABANDONED_EQUIPMENT, new WorldPoint(2452, 9715, 0), "Search the abandoned equipment for an oily cloth.");
		useClothOnArrow = new DetailedQuestStep(this, "Use the oily cloth on an arrow.", oilyClothHighlight, arrowsHighlight);
		lightArrow = new DetailedQuestStep(this, "Use the tinderbox on the fire arrow. If you don't have a tinderbox, use the arrow on the nearby fire instead.", fireArrow, tinderboxHighlight);
		walkNorthEastOfBridge = new DetailedQuestStep(this, new WorldPoint(2447, 9722, 0), "Walk to the room north of Koftik.",
			litArrowEquipped);
		shootBridgeRope = new ObjectStep(this, ObjectID.GUIDE_ROPE, "Wield a lit arrow and shoot the guide-rope.", bow, litArrowEquipped);
		shootBridgeRope.addSubSteps(searchBagForCloth, useClothOnArrow, lightArrow, walkNorthEastOfBridge);

		leaveFallArea = new ObjectStep(this, ObjectID.PILE_OF_ROCKS, new WorldPoint(2443, 9652, 0), "Follow the path west to leave.");
		leaveFallArea.setLinePoints(Arrays.asList(
			new WorldPoint(2485, 9648, 0),
			new WorldPoint(2485, 9645, 0),
			new WorldPoint(2483, 9642, 0),
			new WorldPoint(2483, 9635, 0),
			new WorldPoint(2481, 9629, 0),
			new WorldPoint(2476, 9629, 0),
			new WorldPoint(2476, 9636, 0),
			new WorldPoint(2474, 9637, 0),
			new WorldPoint(2470, 9635, 0),
			new WorldPoint(2467, 9637, 0),
			new WorldPoint(2467, 9639, 0),
			new WorldPoint(2471, 9642, 0),
			new WorldPoint(2471, 9646, 0),
			new WorldPoint(2465, 9646, 0),
			new WorldPoint(2460, 9640, 0),
			new WorldPoint(2460, 9633, 0),
			new WorldPoint(2455, 9633, 0),
			new WorldPoint(2452, 9637, 0),
			new WorldPoint(2452, 9640, 0),
			new WorldPoint(2459, 9645, 0),
			new WorldPoint(2459, 9647, 0),
			new WorldPoint(2454, 9647, 0),
			new WorldPoint(2449, 9650, 0),
			new WorldPoint(2444, 9651, 0)
		));

		collectPlank = new DetailedQuestStep(this, new WorldPoint(2435, 9726, 0), "Pick up the plank in the north room.", plank);
		crossThePit = new ObjectStep(this, ObjectID.ROCK_23125, new WorldPoint(2463, 9699, 0), "Swing across the pit " +
			"with a rope.", ropeHighlight);
		crossThePit.addIcon(ItemID.ROPE);
		crossThePit.addSubSteps(collectPlank);
		climbOverRockslide4 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2491, 9691, 0), "Climb-over rockslide");
		climbOverRockslide5 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2482, 9679, 0), "Climb-over rockslide");
		crossTheGrid = new DetailedQuestStep(this, "Cross the grid by trial and error. Keep health above 15, and remember the path for Regicide.");
		pullLeverAfterGrid = new ObjectStep(this, ObjectID.LEVER_3337, "Pull the lever to raise the gate.");
		crossTheGrid.addSubSteps(climbOverRockslide4, climbOverRockslide5, pullLeverAfterGrid);

		passTrap1 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2443, 9678, 0), "Either disable or walk past the traps.");
		passTrap2 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2440, 9678, 0), "Either disable or walk past the traps.");
		passTrap3 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2435, 9676, 0), "Either disable or walk past the traps.");
		passTrap4 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2432, 9676, 0), "Either disable or walk past the traps.");
		passTrap5 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2430, 9676, 0), "Either disable or walk past the traps.");
		passTrap1.addSubSteps(passTrap2, passTrap3, passTrap4, passTrap5);

		final int OBJECTID_ORB_OF_LIGHT1 = NullObjectID.NULL_37326;
		final int OBJECTID_ORB_OF_LIGHT2 = NullObjectID.NULL_37325;
		final int OBJECTID_ORB_OF_LIGHT3 = NullObjectID.NULL_37324;

		collectOrb1 = new ObjectStep(this, OBJECTID_ORB_OF_LIGHT1, "Take the first orb of light.");
		plankRock1 = new ObjectStep(this, ObjectID.FLAT_ROCK, new WorldPoint(2418, 9681, 0), "Use a plank on the flat rock to cross it.", plankHighlight);
		plankRock1.addIcon(ItemID.PLANK);
		plankRock2 = new ObjectStep(this, ObjectID.FLAT_ROCK, new WorldPoint(2418, 9685, 0), "Use a plank on the flat rock to cross it.", plankHighlight);
		plankRock2.addIcon(ItemID.PLANK);
		plankRock3 = new ObjectStep(this, ObjectID.FLAT_ROCK, new WorldPoint(2416, 9689, 0), "Use a plank on the flat rock to cross it.", plankHighlight);
		plankRock3.addIcon(ItemID.PLANK);
		collectOrb1.addSubSteps(plankRock1, plankRock2, plankRock3);

		collectOrb2 = new ObjectStep(this, OBJECTID_ORB_OF_LIGHT2, new WorldPoint(2385, 9685, 0), "Take the second orb of light north west of the well.");
		collectOrb3 = new ObjectStep(this, OBJECTID_ORB_OF_LIGHT3, new WorldPoint(2386, 9677, 0), "Take the third orb of light west of the well. Remember to use a plank on the flat rocks to cross them.");
		collectOrb4 = new ObjectStep(this, ObjectID.FLAT_ROCK_3339, new WorldPoint(2382, 9668, 0), "Search the flat rock to remove the trap and take the fourth orb to the south west of the well.");
		collectOrb4.addDialogStep("Yes, I'll give it a go.");
		orbsToFurnace = new ObjectStep(this, ObjectID.FURNACE_3294, new WorldPoint(2455, 9683, 0), "Return to the furnace to the east, and use the orbs on it.");
		climbDownWell = new ObjectStep(this, ObjectID.WELL_3264, new WorldPoint(2417, 9675, 0), "Climb down the well.");

		navigateMaze = new DetailedQuestStep(this, "Navigate the maze, or use the shortcut to the south with 50 Thieving.");
		pickCellLock = new ObjectStep(this, ObjectID.GATE_3266, new WorldPoint(2393, 9655, 0), "Pick the lock to enter the cell.");
		digMud = new ObjectStep(this, ObjectID.LOOSE_MUD, new WorldPoint(2393, 9650, 0), "Dig the loose mud.", spadeHighlight);
		digMud.addIcon(ItemID.SPADE);
		crossLedge = new ObjectStep(this, ObjectID.LEDGE_3238, new WorldPoint(2374, 9644, 0), "Cross the ledge.");
		goThroughPipe = new ObjectStep(this, ObjectID.PIPE_3237, new WorldPoint(2418, 9605, 0), "Enter the pipe to the next section.");
		navigateMaze.addSubSteps(pickCellLock, digMud, crossLedge, goThroughPipe);

		searchUnicornCage = new ObjectStep(this, ObjectID.CAGE, new WorldPoint(2398, 9605, 0), "Search the unicorn cage.");
		useRailingOnBoulder = new NpcStep(this, NpcID.BOULDER_4543, new WorldPoint(2397, 9596, 0), "Use the railing on the boulder.", railingHighlight);
		useRailingOnBoulder.addIcon(ItemID.PIECE_OF_RAILING);

		searchUnicornCageAgain = new ObjectStep(this, ObjectID.SMASHED_CAGE, new WorldPoint(2372, 9605, 0), "Search the cage again.");
		leaveUnicornArea = new ObjectStep(this, ObjectID.TUNNEL_3219, new WorldPoint(2375, 9611, 0), "Enter the tunnel.");
		walkToKnights = new TileStep(this, new WorldPoint(2424, 9715, 0), "Travel through the tunnel to find some knights.");
		walkToKnights.addSubSteps(leaveUnicornArea);

		killJerro = new NpcStep(this, NpcID.SIR_JERRO, new WorldPoint(2425, 9720, 0), "Kill Sir Jerro and take his badge. Optionally: talk to one of the knights first to receive food and potions.", badgeJerro);
		killCarl = new NpcStep(this, NpcID.SIR_CARL, new WorldPoint(2425, 9720, 0), "Kill Sir Carl and take his badge.", badgeCarl);
		killHarry = new NpcStep(this, NpcID.SIR_HARRY, new WorldPoint(2425, 9720, 0), "Kill Sir Harry and take his badge.", badgeHarry);

		useBadgeJerroOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2374, 9719, 0), "Use the badges and unicorn horn on the well.", badgeJerroHighlight);
		useBadgeJerroOnWell.addIcon(ItemID.PALADINS_BADGE);
		useBadgeHarryOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2374, 9719, 0), "Use the badges and unicorn horn on the well.", badgeHarryHighlight);
		useBadgeHarryOnWell.addIcon(ItemID.PALADINS_BADGE_1489);
		useBadgeCarlOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2374, 9719, 0), "Use the badges and unicorn horn on the well.", badgeCarlHighlight);
		useBadgeCarlOnWell.addIcon(ItemID.PALADINS_BADGE_1490);
		useUnicornHornOnWell = new ObjectStep(this, ObjectID.WELL_3305, new WorldPoint(2374, 9719, 0), "Use the badges and unicorn horn on the well.", unicornHornHighlight);
		useUnicornHornOnWell.addIcon(ItemID.UNICORN_HORN_1487);
		useBadgeJerroOnWell.addSubSteps(useBadgeHarryOnWell, useBadgeCarlOnWell, useUnicornHornOnWell);
		openIbansDoor = new ObjectStep(this, ObjectID.DOOR_3221, new WorldPoint(2369, 9720, 0), "Open the door at the end of the path.");

		descendCave = new ObjectStep(this, ObjectID.CAVE_3222, new WorldPoint(2150, 4545, 1), "Follow the edge of the cavern down to enter a cave on the south wall.");
		talkToNiloof = new NpcStep(this, NpcID.NILOOF, new WorldPoint(2313, 9806, 0), "Talk to Niloof.");
		talkToNiloof.addSubSteps(descendCave);

		talkToKlank = new NpcStep(this, NpcID.KLANK, "Talk to Klank.");
		talkToKlank.addDialogStep("What happened to them?");

		goBackUpToIbansCavern = new ObjectStep(this, ObjectID.CAVE_3223, new WorldPoint(2336, 9793, 0), "Return up to Iban's Cavern.");

		pickUpWitchsCat = new NpcStep(this, NpcID.WITCHS_CAT, new WorldPoint(2131, 4602, 1), "Start from the south east corner of the bridges, and go northwest along them and retrieve the witch's cat.");
		pickUpWitchsCat.addSubSteps(goBackUpToIbansCavern);
		pickUpWitchsCat.setLinePoints(Arrays.asList(
			new WorldPoint(2149, 4547, 1),
			new WorldPoint(2172, 4547, 1),
			new WorldPoint(2172, 4582, 1),
			new WorldPoint(2152, 4582, 1),
			new WorldPoint(2152, 4583, 1),
			new WorldPoint(2144, 4583, 1),
			new WorldPoint(2143, 4585, 1),
			new WorldPoint(2143, 4593, 1),
			new WorldPoint(2142, 4595, 1),
			new WorldPoint(2136, 4595, 1),
			new WorldPoint(2136, 4592, 1),
			new WorldPoint(2132, 4592, 1),
			new WorldPoint(2132, 4600, 1)
		));

		useCatOnDoor = new ObjectStep(this, ObjectID.DOOR_3270, new WorldPoint(2158, 4566, 1), "Use the cat on the witch's door in the south east corner of the area to distract her.", witchsCat);
		useCatOnDoor.addIcon(ItemID.WITCHS_CAT);
		searchWitchsChest = new ObjectStep(this, ObjectID.CHEST_3272, new WorldPoint(2157, 4564, 1), "Search the chest in the witch's house. You'll need 4 empty inventory slots.");

		killHolthion = new NpcStep(this, NpcID.HOLTHION, new WorldPoint(2133, 4555, 1), "From the south east corner of the area, head west then south along the pathways. Kill Holthion and pick up his amulet.", amuletHolthion);
		killHolthion.setLinePoints(Arrays.asList(
			new WorldPoint(2169, 4582, 1),
			new WorldPoint(2152, 4582, 1),
			new WorldPoint(2151, 4583, 1),
			new WorldPoint(2142, 4582, 1),
			new WorldPoint(2142, 4556, 1),
			new WorldPoint(2137, 4556, 1)
		));

		killDoomion = new NpcStep(this, NpcID.DOOMION, new WorldPoint(2135, 4566, 1), "Kill Doomion and pick up his amulet.", amuletDoomion);
		killOthanian = new NpcStep(this, NpcID.OTHAINIAN, new WorldPoint(2123, 4563, 1), "Kill Othanian and pick up his amulet.", amuletOthanian);
		useShadowOnDoll = new DetailedQuestStep(this, "Use the shadow on the doll.", ibansShadow, dollOfIbanHighlighted);
		searchDoomionsChest = new ObjectStep(this, ObjectID.CHEST_3274, new WorldPoint(2136, 4578, 1), "Search the chest north of Doomion.");
		searchDoomionsChest.addSubSteps(useShadowOnDoll);
		useAshOnDoll = new DetailedQuestStep(this, "Use the ashes on the doll.", ibansAshes, dollOfIbanHighlighted);
		useDoveOnDoll = new DetailedQuestStep(this, "Use the dove on the doll.", ibansDove, dollOfIbanHighlighted);
		returnToDwarfs = new ObjectStep(this, ObjectID.CAVE_3222, new WorldPoint(2150, 4545, 1), "Return to the dwarf encampment on the lower level.");
		useBucketOnBrew = new ObjectStep(this, ObjectID.BREW_BARREL, new WorldPoint(2327, 9799, 0), "Use a bucket on the brew barrel in the dwarf encampment on the south west.", bucketHighlight);
		useBucketOnBrew.addIcon(ItemID.BUCKET);
		useBrewOnTomb = new ObjectStep(this, ObjectID.TOMB, new WorldPoint(2357, 9802, 0), "Use the bucket of brew on the tomb in the south east corner of the area.", brew);
		useBrewOnTomb.addIcon(ItemID.DWARF_BREW);
		useTinderboxOnTomb = new ObjectStep(this, ObjectID.TOMB, new WorldPoint(2357, 9802, 0), "Light the tomb with a tinderbox.", tinderboxHighlight);
		useTinderboxOnTomb.addSubSteps(useAshOnDoll);
		useTinderboxOnTomb.addIcon(ItemID.TINDERBOX);
		killKalrag = new NpcStep(this, NpcID.KALRAG, new WorldPoint(2356, 9913, 0), "Kill Kalrag the spider. Protect From Melee can keep you safe in this fight.", dollOfIban);
		ascendToHalfSouless = new ObjectStep(this, ObjectID.CAVE_3223, new WorldPoint(2304, 9915, 0), "Ascend to the upper level of the cave again via the north west exit.");
		searchCage = new ObjectStep(this, ObjectID.CAGE_3351, new WorldPoint(2135, 4703, 1), "Search the marked cage in the north west of the area while wearing Klank's gauntlets.", klanksGauntletsEquipped);
		searchCage.setLinePoints(Arrays.asList(
			new WorldPoint(2116, 4729, 1),
			new WorldPoint(2116, 4686, 1),
			new WorldPoint(2128, 4686, 1),
			new WorldPoint(2128, 4691, 1),
			new WorldPoint(2129, 4693, 1),
			new WorldPoint(2129, 4696, 1),
			new WorldPoint(2132, 4698, 1),
			new WorldPoint(2138, 4698, 1),
			new WorldPoint(2140, 4700, 1),
			new WorldPoint(2140, 4702, 1),
			new WorldPoint(2136, 4702, 1)
		));
		searchCage.addSubSteps(ascendToHalfSouless, useDoveOnDoll);

		killDisciple = new NpcStep(this, NpcID.DISCIPLE_OF_IBAN, new WorldPoint(2163, 4648, 1), "Travel along the pathways from the north west corner of the area to the middle. Kill a disciple of Iban and take their robes.", true, dollOfIban);
		killDisciple.setLinePoints(Arrays.asList(
			new WorldPoint(2117, 4686, 1),
			new WorldPoint(2128, 4686, 1),
			new WorldPoint(2130, 4685, 1),
			new WorldPoint(2134, 4687, 1),
			new WorldPoint(2135, 4689, 1),
			new WorldPoint(2140, 4689, 1),
			new WorldPoint(2140, 4685, 1),
			new WorldPoint(2152, 4685, 1),
			new WorldPoint(2153, 4682, 1),
			new WorldPoint(2153, 4678, 1),
			new WorldPoint(2154, 4676, 1),
			new WorldPoint(2160, 4676, 1),
			new WorldPoint(2160, 4670, 1),
			new WorldPoint(2165, 4670, 1),
			new WorldPoint(2165, 4667, 1),
			new WorldPoint(2162, 4667, 1),
			new WorldPoint(2162, 4660, 1),
			new WorldPoint(2161, 4659, 1),
			new WorldPoint(2161, 4654, 1)
		));
		enterTemple = new ObjectStep(this, ObjectID.DOOR_3333, new WorldPoint(2144, 4648, 1), "Enter the Temple of Iban wearing monk robes. Prepare to use Iban's doll on the well, as Iban can deal lots of damage quickly.", robeTopEquipped, robeBottomEquipped);
		enterTemple.setLinePoints(Arrays.asList(
			new WorldPoint(2117, 4686, 1),
			new WorldPoint(2128, 4686, 1),
			new WorldPoint(2130, 4685, 1),
			new WorldPoint(2134, 4687, 1),
			new WorldPoint(2135, 4689, 1),
			new WorldPoint(2140, 4689, 1),
			new WorldPoint(2140, 4685, 1),
			new WorldPoint(2152, 4685, 1),
			new WorldPoint(2153, 4682, 1),
			new WorldPoint(2153, 4678, 1),
			new WorldPoint(2154, 4676, 1),
			new WorldPoint(2160, 4676, 1),
			new WorldPoint(2160, 4670, 1),
			new WorldPoint(2165, 4670, 1),
			new WorldPoint(2165, 4667, 1),
			new WorldPoint(2162, 4667, 1),
			new WorldPoint(2162, 4660, 1),
			new WorldPoint(2161, 4659, 1),
			new WorldPoint(2161, 4654, 1)
		));
		useDollOnWell = new ObjectStep(this, ObjectID.WELL_3359, new WorldPoint(2137, 4648, 1), "Right-click USE the doll on the well. Standing north of the well can prevent Iban from hitting.", dollOfIbanHighlighted);
		useDollOnWell.addIcon(ItemID.DOLL_OF_IBAN);

		talkToKoftikAfterTemple = new NpcStep(this, NpcID.KOFTIK_8976, new WorldPoint(2444, 9607, 0), "Talk to Koftik.");

		goUpToLathasToFinish = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0), "Return to King Lathas to finish the quest.");
		talkToKingLathasAfterTemple = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Return to King Lathas to finish the quest.");
		talkToKingLathasAfterTemple.addSubSteps(goUpToLathasToFinish);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemReqs();
		setupZones();
		setupConditions();
		setupSteps();

		ConditionalStep beginWithLathas = new ConditionalStep(this, goToArdougneCastleFloor2);
		beginWithLathas.addStep(inCastleFloor2, talkToKingLathas);

		ConditionalStep startDungeon = new ConditionalStep(this, enterWestArdougne);
		startDungeon.addStep(inCastleFloor2, goDownCastleStairs);
		startDungeon.addStep(inWestArdougne, talkToKoftik);

		ConditionalStep crossTheBridge = new ConditionalStep(this, enterTheDungeon);
		crossTheBridge.addStep(isInFallArea, leaveFallArea);
		crossTheBridge.addStep(isBeforeRockslide1, climbOverRockslide1);
		crossTheBridge.addStep(isBeforeRockslide2, climbOverRockslide2);
		crossTheBridge.addStep(isBeforeRockslide3, climbOverRockslide3);
		crossTheBridge.addStep(new Conditions(isNorthEastOfBridge, litArrowEquipped), shootBridgeRope);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), litArrow), walkNorthEastOfBridge);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), fireArrow), lightArrow);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), oilyCloth), useClothOnArrow);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), clothInBag), searchBagForCloth);
		crossTheBridge.addStep(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), talkToKoftikAtBridge);

		ConditionalStep theUndergroundPass = new ConditionalStep(this, climbDownWell);
		theUndergroundPass.addStep(new Conditions(LogicType.NOR, plank), collectPlank);
		theUndergroundPass.addStep(isBeforeThePit, crossThePit);
		theUndergroundPass.addStep(isAfterThePit, climbOverRockslide4);
		theUndergroundPass.addStep(isBeforeTheGrid, climbOverRockslide5);
		theUndergroundPass.addStep(isAtTheGrid, crossTheGrid);
		theUndergroundPass.addStep(isAfterTheGrid, pullLeverAfterGrid);
		theUndergroundPass.addStep(destroyedAllOrbs, climbDownWell);
		theUndergroundPass.addStep(new Conditions(haveOrb1, haveOrb2, haveOrb3, haveOrb4), orbsToFurnace);
		theUndergroundPass.addStep(new Conditions(haveOrb1, haveOrb2, haveOrb3), collectOrb4);
		theUndergroundPass.addStep(new Conditions(haveOrb1, haveOrb2), collectOrb3);
		theUndergroundPass.addStep(new Conditions(haveOrb1), collectOrb2);
		theUndergroundPass.addStep(isInWellArea, plankRock1);
		theUndergroundPass.addStep(isBeforePlank2, plankRock2);
		theUndergroundPass.addStep(isBeforePlank3, plankRock3);
		theUndergroundPass.addStep(isAtOrb1, collectOrb1);

		theUndergroundPass.addStep(isBeforeTrap1, passTrap1);
		theUndergroundPass.addStep(isBeforeTrap2, passTrap2);
		theUndergroundPass.addStep(isBeforeTrap3, passTrap3);
		theUndergroundPass.addStep(isBeforeTrap4, passTrap4);
		theUndergroundPass.addStep(isBeforeTrap5, passTrap5);

		ConditionalStep travelThroughPassSection2 = new ConditionalStep(this, crossTheBridge);
		travelThroughPassSection2.addStep(isInUndergroundSection2, theUndergroundPass);

		ConditionalStep descendingDeeper = new ConditionalStep(this, pickCellLock);
		descendingDeeper.addStep(isAfterMaze, goThroughPipe);
		descendingDeeper.addStep(isInMaze, navigateMaze);
		descendingDeeper.addStep(isBeforeLedge, crossLedge);
		descendingDeeper.addStep(isInsideCell, digMud);
		descendingDeeper.addStep(new Conditions(isInUnicornArea, railing), useRailingOnBoulder);
		descendingDeeper.addStep(isInUnicornArea, searchUnicornCage);

		ConditionalStep travelThroughPassSection3 = new ConditionalStep(this, crossTheBridge);
		travelThroughPassSection3.addStep(isInUndergroundSection3, descendingDeeper);
		travelThroughPassSection3.addStep(isInUndergroundSection2, theUndergroundPass);

		ConditionalStep killedAUnicorn = new ConditionalStep(this, travelThroughPassSection3);
		killedAUnicorn.addStep(new Conditions(isInKnightsArea, usedHorn, usedBadgeCarl, usedBadgeHarry, usedBadgeJerro), openIbansDoor);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, usedHorn, usedBadgeCarl, usedBadgeHarry, haveBadgeJerro), useBadgeJerroOnWell);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, usedHorn, usedBadgeCarl, haveBadgeHarry, haveBadgeJerro), useBadgeHarryOnWell);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, usedHorn, haveBadgeCarl, haveBadgeHarry, haveBadgeJerro), useBadgeCarlOnWell);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, haveBadgeCarl, haveBadgeHarry, haveBadgeJerro), useUnicornHornOnWell);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, new Conditions(LogicType.NOR, haveBadgeJerro)), killJerro);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, new Conditions(LogicType.NOR, haveBadgeCarl)), killCarl);
		killedAUnicorn.addStep(new Conditions(isBeforeIbansDoor, new Conditions(LogicType.NOR, haveBadgeHarry)), killHarry);
		killedAUnicorn.addStep(isInKnightsArea, walkToKnights);
		killedAUnicorn.addStep(new Conditions(haveUnicornHorn, isInUnicornArea2), leaveUnicornArea);
		killedAUnicorn.addStep(isInUnicornArea2, searchUnicornCageAgain);

		ConditionalStep gettingToFinalSection = new ConditionalStep(this, travelThroughPassSection3);
		gettingToFinalSection.addStep(isInKnightsArea, openIbansDoor);
		gettingToFinalSection.addStep(isInUnicornArea2, leaveUnicornArea);

		ConditionalStep talkingToNiloof = new ConditionalStep(this, gettingToFinalSection);
		talkingToNiloof.addStep(isInDwarfCavern, talkToNiloof);
		talkingToNiloof.addStep(isInFinalArea, descendCave);

		ConditionalStep theWitch = new ConditionalStep(this, gettingToFinalSection);
		theWitch.addStep(new Conditions(isInFinalArea, givenWitchCat), searchWitchsChest);
		theWitch.addStep(new Conditions(isInFinalArea, witchsCat), useCatOnDoor);
		theWitch.addStep(isInFinalArea, pickUpWitchsCat);
		theWitch.addStep(new Conditions(isInDwarfCavern, haveKlanksGauntlets), goBackUpToIbansCavern);
		theWitch.addStep(new Conditions(isInDwarfCavern), talkToKlank);

		ConditionalStep imbuingTheDoll = new ConditionalStep(this, gettingToFinalSection);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, dollImbued, dollAshed, kalragKilled, doveSmeared, robeBottom, robeTop), enterTemple);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, dollImbued, dollAshed, kalragKilled, doveSmeared), killDisciple);
		imbuingTheDoll.addStep(new Conditions(dollImbued, dollAshed, kalragKilled, ibansDove), useDoveOnDoll);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, dollImbued, dollAshed, kalragKilled), searchCage);
		imbuingTheDoll.addStep(new Conditions(isInDwarfCavern, dollImbued, dollAshed, kalragKilled), ascendToHalfSouless);
		imbuingTheDoll.addStep(new Conditions(isInDwarfCavern, dollImbued, dollAshed), killKalrag);
		imbuingTheDoll.addStep(new Conditions(dollImbued, pouredBrew, ibansAshes), useAshOnDoll);
		imbuingTheDoll.addStep(new Conditions(isInDwarfCavern, dollImbued, pouredBrew), useTinderboxOnTomb);
		imbuingTheDoll.addStep(new Conditions(isInDwarfCavern, dollImbued, brew), useBrewOnTomb);
		imbuingTheDoll.addStep(new Conditions(isInDwarfCavern, dollImbued), useBucketOnBrew);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, dollImbued), returnToDwarfs);
		imbuingTheDoll.addStep(new Conditions(ibansShadow), useShadowOnDoll);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, amuletHolthion, amuletDoomion, amuletOthanian), searchDoomionsChest);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, amuletHolthion, amuletDoomion), killOthanian);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea, amuletHolthion), killDoomion);
		imbuingTheDoll.addStep(new Conditions(isInFinalArea), killHolthion);
		imbuingTheDoll.addStep(new Conditions(isInDwarfCavern), goBackUpToIbansCavern);

		ConditionalStep goDestroyDoll = new ConditionalStep(this, gettingToFinalSection);
		goDestroyDoll.addStep(new Conditions(isInTemple), useDollOnWell);
		goDestroyDoll.addStep(new Conditions(isInFinalArea, robeBottom, robeTop), enterTemple);
		goDestroyDoll.addStep(isInFinalArea, killDisciple);
		goDestroyDoll.addStep(isInDwarfCavern, ascendToHalfSouless);

		ConditionalStep wrappingUp = new ConditionalStep(this, talkToKingLathasAfterTemple);
		wrappingUp.addStep(isInPostIbanArea, talkToKoftikAfterTemple);

		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(0, beginWithLathas)
			.put(1, startDungeon)
			.put(2, crossTheBridge)
			.put(3, travelThroughPassSection2)
			.put(4, travelThroughPassSection3)
			.put(5, killedAUnicorn)
			.put(6, talkingToNiloof)
			.put(7, theWitch)
			.put(8, imbuingTheDoll)
			.put(9, goDestroyDoll)
			.put(10, wrappingUp)
			.build();
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(rope2);
		reqs.add(bow);
		reqs.add(arrows);
		reqs.add(spade);
		reqs.add(plank);
		reqs.add(bucket);
		reqs.add(tinderbox);
		reqs.add(combatEquipment);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(food);
		reqs.add(staminaPotions);
		reqs.add(coins);
		reqs.add(agilityPotions);
		reqs.add(telegrabRunes);
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("3 Demons (level 91, safespottable)");
		reqs.add("3 Paladins (level 62, safespottable)");
		reqs.add("Kalrag (level 89)");
		reqs.add("Disciple of Iban (level 13)");
		return reqs;
	}

	@Override
	public List<String> getNotes()
	{
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Kalrag attacks with melee only, so Protect From Melee can keep you safe in that fight.");
		notes.add("The 3 Demons can be easily safespotted by staying on the connecting bridges.");
		notes.add("Using the bow provided for combat can save inventory space.");
		notes.add("Bring all items with you to start the quest, as leaving the dungeon will require you to restart from the beginning.");
		return notes;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.BIOHAZARD, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.RANGED, 25));
		return req;
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
				new ExperienceReward(Skill.AGILITY, 3000),
				new ExperienceReward(Skill.ATTACK, 3000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Iban's Staff", ItemID.IBANS_STAFF, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", Arrays.asList(talkToKingLathas, talkToKoftik)));
		allSteps.add(new PanelDetails("The Underground Pass", Arrays.asList(talkToKoftikAtBridge, shootBridgeRope, crossThePit,
			crossTheGrid, passTrap1, collectOrb1, collectOrb2, collectOrb3, collectOrb4, orbsToFurnace,
			climbDownWell), rope2, bow, arrows, spade, plank, bucket, tinderbox, combatEquipment));
		allSteps.add(new PanelDetails("Descending Deeper", Arrays.asList(navigateMaze, searchUnicornCage, useRailingOnBoulder)));
		allSteps.add(new PanelDetails("Cold-blooded Killing", Arrays.asList(searchUnicornCageAgain, walkToKnights, killJerro, killHarry, killCarl, useBadgeJerroOnWell, openIbansDoor)));
		allSteps.add(new PanelDetails("The Witch Kardia", Arrays.asList(talkToNiloof, pickUpWitchsCat, useCatOnDoor, searchWitchsChest)));
		allSteps.add(new PanelDetails("Imbuing the Doll", Arrays.asList(killHolthion, killDoomion, killOthanian, searchDoomionsChest, returnToDwarfs, useBucketOnBrew, useBrewOnTomb, useTinderboxOnTomb, killKalrag, searchCage)));
		allSteps.add(new PanelDetails("Entering the Temple", Arrays.asList(killDisciple, enterTemple, useDollOnWell)));
		allSteps.add(new PanelDetails("Foggy Memories", Arrays.asList(talkToKoftikAfterTemple, talkToKingLathasAfterTemple)));
		return allSteps;
	}
}
