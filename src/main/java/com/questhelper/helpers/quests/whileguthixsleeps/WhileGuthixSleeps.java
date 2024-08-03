/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.quests.whileguthixsleeps;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.util.ItemSlots;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.PolyZone;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.NpcFollowerStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.LunarSpells;
import com.questhelper.steps.widget.NormalSpells;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class WhileGuthixSleeps extends BasicQuestHelper
{
	//Items Required
	ItemRequirement sapphireLantern, litSapphireLantern, airRune, earthRune, fireRune, waterRune, mindRune, lawRune,
		deathRune, dibber, log, charcoal, papyrus, lanternLens, mortMyreFungus, unpoweredOrb, ringOfCharosA, coins, bronzeMedHelm,
		ironChainbody, chargeOrbSpell, meleeGear, rangedGear, logs, knife, snapdragonSeed, astralRune, cosmicRune,
		bindRunes, weakenRunes, magicGear, squallOutfit, eliteBlackKnightOutfit, telegrabRunes, alchRunes;


	// Items Recommended
	ItemRequirement antipoison, burthorpeTeleport, khazardTeleport, feldipHillsTeleport, faladorTeleport,
		camelotTeleport, lobster, restorePotion, gamesNecklace, spade, hammer, chisel, food, prayerPotions;

	Requirement lunarSpellbook;

	// Quest items
	ItemRequirement dirtyShirt, unconsciousBroav, broav, movariosNotesV1, movariosNotesV2, wastePaperBasket, rubyKey, movariosNotesV1InBank, movariosNotesV2InBank, teleorb, pinkDye,
		roseTintedLens, enrichedSnapdragonSeed, enrichedSnapdragon, truthSerum, superTruthSerum, sketch, eliteHelm, eliteBody, eliteLegs, eliteBlackKnightOrSquallOutfit, cellKey,
		silifTeleorb, strangeTeleorb, darkSquallHood, darkSquallBody, darkSquallLegs, fireOrb, waterOrb, airOrb, earthOrb, airBlock, waterBlock, earthBlock, fireBlock;

	ItemRequirement toadflax, toadsLegs, guamLeaf, eyeOfNewt, iritLeaf, harralander, redSpidersEggs, garlic, silverDust, goatHorn, ranarrWeed, whiteBerries, cadantine, avantoe, moryMyreFungus,
		chocolateDust, snapeGrass, kebbitTeethdust, lantadyme, potatoCactus, dwarfWeed, wineOfZamorak, snapdragon, tarromin, limpwurt, kwuarm, emptyDruidPouch, fullDruidPouch, silverSickleB;

	Requirement doorNeedsEarthRune, doorNeedsMindRune, doorNeedsAirRune, doorNeedsFireRune, doorNeedsWaterRune, isElectricBookcase1, isElectricBookcase2, isElectricBookcase3, isElectricBookcase4,
		isElectricBookcase5, isElectricBookcase6, isElectricBookcase7;

	Requirement hadToadflax, hadToadsLegs, hadGuamLeaf, hadEyeOfNewt, hadIritLeaf, hadHarralander, hadRedSpidersEggs, hadGarlic, hadSilverDust, hadGoatHorn, hadRanarrWeed, hadWhiteBerries, hadCadantine, hadAvantoeForHunterPotion, hadMortMyreFungus,
		hadChocolateDust, hadSnapeGrass, hadKebbitTeethdustForHunterPotion, hadLantadyme, hadPotatoCactus, hadDwarfWeed, hadWineOfZamorak, hadSnapdragon, hadTarromin, hadLimpwurt, hadKwuarm, hadEmptyDruidPouch, hadFullDruidPouch, hadSilverSickleB;
	Requirement isUpstairsNearThaerisk, assassinsNearby, paidLaunderer, talkedToLaunderer, trapSetUp, trapBaited, broavTrapped, broavNearby, isNearTable, claimedRunes,
		hasBroav, inMovarioFirstRoom, inMovarioDoorRoom, inLibrary, isNextToSpiralStaircase, disarmedStaircase, inMovarioBaseF1, inMovarioBaseF2,
		hadRubyKey, searchedBedForTraps, pulledPaintingLever, inWeightRoom, teleportedToDraynor, inPortSarim, inDoorway, purchasedSnapdragon, teleportedToPortSarim, talkedToThaeriskWithSeed,
		inWhiteKnightsCastleF1, inWhiteKnightsCastleF2, inWhiteKnightsCastleF3, onLunarSpellbook, notContactedCyrisus, notContactedTurael, notContactedMazchna, notContactedDuradel,
		notRecruitedHarrallak, notRecruitedSloane, notRecruitedGhommal, onF1WarriorsGuild, inBlackKnightFortress, inHiddenRoom, inBlackKnightFortressBasement, notSearchedTableForTeleorb,
		hasCastChargeOrb;

	Requirement inCatacombSouth, inCatacombNorth, inCatacombF2, openedCatacombShortcut, inCatacombHQ, notSearchedWardrobeForEliteArmour,
		notSearchedWardrobeForSquallOutfit, isSafeInCatacombs, notSearchedTableForRunes, notSearchedTableForLobsterAndRestore, notSearchedKeyRack, openedSilifCell,
		talkedToSilif, usedFoodOnSilif, usedRestoreOnSilif, givenArmourToSilif, silifIsFollowing, seenMap, inSquallFightRoom, defeatedSurok, inTeleportSpot, inLucienCamp,
		onChaosTempleF1, inJunaRoom, inAbyssEntry, notUsedSpadeOnEarthRocks, notUsedChiselOnEarthBrazier, notUsedSpadeOnFireRocks, notUsedChiselOnFireBrazier,
		notUsedChiselOnWaterBrazier, notUsedChiselOnAirBrazier, usedChiselOnAllBraziers, notPlacedAirOrb, notPlacedWaterOrb, notPlacedEarthOrb, notPlacedFireOrb, inAbyssEntryF1,
		inAbyssEntryF2, placedAllOrbs, notPlacedFireBlock, placedAirBlock, placedWaterBlock, placedEarthBlock, placedAllBlocks;

	Requirement noWeaponOrShieldEquipped, inAirCavity, inWaterCavity, inEarthCavity, inGuthixianTemple, inJailCell, talkedToMovario;

	Zone upstairsNearThaeriskZone, nearTable, movarioFirstRoom, movarioDoorRoom, library, nextToSpiralStaircase, movarioBaseF1, movarioBaseF2, weightRoom, portSarim, doorway,
		whiteKnightsCastleF1, whiteKnightsCastleF2, whiteKnightsCastleF3, f1WarriorsGuild, blackKnightFortress1, blackKnightFortress2, blackKnightFortress3, hiddenRoom,
		blackKnightFortressBasement, catacombSouth1, catacombNorth1, catacombNorth2, catacombF2, catacombHQ, squallFightRoom, teleportSpot, lucienCamp, chaosTempleF1,
		junaRoom, abyssEntry, abyssEntryF1, abyssEntryF2, airCavity, waterCavity, earthCavity, guthixianTemple, jailCell;

	DetailedQuestStep talkToIvy, questPlaceholder, goUpLadderNextToIvy, talkToThaerisk, killAssassins, talkToThaeriskAgain,
		talkToLaunderer, talkToHuntingExpert, setupTrap, useFungusOnTrap, waitForBroavToGetTrapped, retrieveBroav,
		returnBroavToHuntingExpert, useDirtyShirtOnBroav, dropBroav, goToBrokenTable, searchBrokenTable;

	QuestStep enterMovarioBase, claimRunes, climbDownMovarioFirstRoom, inspectDoor, useFireRuneOnDoor, useAirRuneOnDoor, useEarthRuneOnDoor, useWaterRuneOnDoor,
		useMindRuneOnDoor, useRuneOnDoor, enterDoorToLibrary, solveElectricityPuzzle, searchBookcase1, searchBookcase2, searchBookcase3, searchBookcase4, searchBookcase5, searchBookcase6, searchBookcase7,
		enterElectricDoor, searchStaircaseInLibrary, climbStaircaseInLibrary, searchDesk, pickupWasteBasket, searchWasteBasket, useKeyOnBookcase, climbUpHiddenStaircase, searchBed, goDownToF1MovarioBase,
		useKeyOnChest, searchChestForTraps, getNotesFromChest, readNotes1, readNotes2, goDownFromHiddenRoom, inspectPainting;

	DetailedQuestStep goUpToThaeriskWithNotes, talkToThaeriskWithNotes, goUpToThaeriskWithoutNotes, talkToThaeriskWithoutNotes;

	DetailedQuestStep killMercenaries, talkToIdria, talkToAkrisae, talkToAkrisaeForTeleport, useOrbOnShadyStranger, talkToAkrisaeAfterOrb, buySnapdragonSeed, getSarimTeleport,
		talkToBetty, talkToBettyForDye, usePinkDyeOnLanternLens, standInDoorway, useLensOnCounter, searchCounterForSeed, talkToThaeriskWithSeed;

	DetailedQuestStep goFromF0ToF1WhiteKnight, goFromF1ToF2WhiteKnight, goFromF2ToF3WhiteKnight, plantSnapdragon, goFromF3ToF2WhiteKnight, goFromF2ToF1WhiteKnight, goFromF1ToF0WhiteKnight,
		talkToIdriaAfterPlanting, activeLunars, contactTurael, contactMazchna, contactCyrisus, contactDuradel, harvestSnapdragon, talkToThaeriskWithSnapdragon, useSnapdragonOnSerum,
		searchDrawersForCharcoalAndPapyrus, enterJailCell, useSerumOnSpy, talkToSpy, giveSketchToIdria, talkToIdriaAfterSketch, talkToGhommal, talkToHarrallak, goToF1WarriorsGuild, talkToSloane;

	DetailedQuestStep goFromF3ToF2WhiteKnightThaerisk, goFromF2ToF1WhiteKnightThaerisk, goFromF1ToF0WhiteKnightThaerisk;

	ConditionalStep goPlantSnapdragon, goHarvestSnapdragon;

	DetailedQuestStep talkToAkrisaeAfterRecruitment, enterBlackKnightFortress, pushHiddenWall, climbDownBlackKnightBasement, inspectCarvedTile, castChargedOrbOnTile, enterCatacombs, jumpBridge,
		climbWallInCatacombs, useWesternSolidDoor, enterCatacombShortcut, enterNorthernSolidDoor, killKnightsForEliteArmour, equipSquallOrEliteArmour, searchWardrobeForEliteArmour, searchWardrobeForSquallRobes,
		searchDeskForTeleorb, searchDeskForLobster, searchDeskForLawAndDeathRune, searchKeyRack, leaveSolidDoor, openSilifsCell, useLobsterOnSilif, useRestoreOnSilif, giveSilifEliteArmour,
		talkToSilifToFollow, enterNorthernSolidDoorAgain, goNearMap, talkToSilifAtMap, climbUpCatacombLadder, defeatSurok, defeatSurokSidebar, plantOrbOnSurok, talkToAkrisaeAfterSurok, enterCellWithRobesOn,
		talkToSilifForRobes, activateStrangeTeleorb, climbIceWall, jumpToLedge, talkToIdriaAfterChapel;

	DetailedQuestStep goDownForOrbAndRunes, takeStrangeTeleorb, takeRunes, goUpToUseTeleorb, getRunes, standAtTeleportSpot;

	DetailedQuestStep teleportToJuna, talkToMovario, useLitSapphireLanternOnLightCreature, searchRemainsForSpade, searchRemainsForHammerAndChisel, useSpadeOnFireRocks,
		useChiselOnFireBrazier, useSpadeOnEarthRocks, useChiselOnEarthBrazier, useChiselOnAirBrazier, useChiselOnWaterBrazier, useOrbOnFireRecessedBlock,
		useOrbOnWaterRecessedBlock, useOrbOnAirRecessedBlock, climbWallNextToSkullF0ToF1, climbWallNextToSkullF1ToF2, useOrbOnEarthRecessedBlock, climbDownFromSkullF2ToF1,
		climbDownFromSkullF1ToF0;

	DetailedQuestStep useFireBlockOnRecess, enterWestCavity, useEarthBlockOnRecess, leaveWaterRecess, enterMiddleCavity, useAirBlockOnRecess, leaveEarthRecess,
		enterEastCavity, useWaterBlockOnRecess, leaveAirRecess, climbUpToCubeF0ToF1, climbUpToCubeF1ToF2, touchCube, enterSkull;

	DetailedQuestStep getPouch, castBloomToFillPouch, usePouchOnDruid, approachStoneOfJas, fightBalanceElemental, touchStone, talkToMovarioAtStone, fightTormentedDemons, teleportWithIdria, finishQuest;


	WeightStep solveWeightPuzzle;

	HerblorePuzzle herblorePuzzle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToIvy);

		ConditionalStep goTalkToThaerisk = new ConditionalStep(this, goUpLadderNextToIvy);
		goTalkToThaerisk.addStep(assassinsNearby, killAssassins);
		goTalkToThaerisk.addStep(isUpstairsNearThaerisk, talkToThaerisk);
		steps.put(2, goTalkToThaerisk);

		ConditionalStep goTalkToThaeriskAgain = new ConditionalStep(this, goUpLadderNextToIvy);
		goTalkToThaeriskAgain.addStep(isUpstairsNearThaerisk, talkToThaeriskAgain);
		steps.put(3, goTalkToThaeriskAgain);

		ConditionalStep goGetBroav = new ConditionalStep(this, talkToLaunderer);
		goGetBroav.addStep(talkedToLaunderer, talkToHuntingExpert);
		steps.put(4, goGetBroav);

		ConditionalStep goTrapBroav = new ConditionalStep(this, setupTrap);
		goTrapBroav.addStep(unconsciousBroav.alsoCheckBank(questBank), returnBroavToHuntingExpert);
		goTrapBroav.addStep(broavTrapped, retrieveBroav);
		goTrapBroav.addStep(trapBaited, waitForBroavToGetTrapped);
		goTrapBroav.addStep(trapSetUp, useFungusOnTrap);
		steps.put(5, goTrapBroav);

		ConditionalStep goTrackMovario = new ConditionalStep(this, goTrapBroav);
		goTrackMovario.addStep(and(broavNearby, dirtyShirt.alsoCheckBank(questBank), isNearTable), useDirtyShirtOnBroav);
		goTrackMovario.addStep(and(hasBroav, dirtyShirt.alsoCheckBank(questBank), isNearTable), dropBroav);
		goTrackMovario.addStep(and(hasBroav, dirtyShirt.alsoCheckBank(questBank)), goToBrokenTable);
		goTrackMovario.addStep(hasBroav, talkToLaunderer);
		steps.put(6, goTrackMovario);
		steps.put(7, goTrackMovario);

		steps.put(8, searchBrokenTable);

		steps.put(9, enterMovarioBase);

		ConditionalStep goDoDoorPuzzle = new ConditionalStep(this, enterMovarioBase);
		goDoDoorPuzzle.addStep(inMovarioDoorRoom, inspectDoor);
		goDoDoorPuzzle.addStep(and(inMovarioFirstRoom, claimedRunes), climbDownMovarioFirstRoom);
		goDoDoorPuzzle.addStep(inMovarioFirstRoom, claimRunes);
		steps.put(10, goDoDoorPuzzle);

		ConditionalStep solveDoor = new ConditionalStep(this, goDoDoorPuzzle);
		solveDoor.addStep(and(inMovarioDoorRoom, doorNeedsAirRune), useAirRuneOnDoor);
		solveDoor.addStep(and(inMovarioDoorRoom, doorNeedsWaterRune), useWaterRuneOnDoor);
		solveDoor.addStep(and(inMovarioDoorRoom, doorNeedsEarthRune), useEarthRuneOnDoor);
		solveDoor.addStep(and(inMovarioDoorRoom, doorNeedsFireRune), useFireRuneOnDoor);
		solveDoor.addStep(and(inMovarioDoorRoom, doorNeedsMindRune), useMindRuneOnDoor);
		solveDoor.addStep(inMovarioDoorRoom, inspectDoor);
		steps.put(11, solveDoor);

		ConditionalStep enterLibrary = new ConditionalStep(this, goDoDoorPuzzle);
		enterLibrary.addStep(inMovarioDoorRoom, enterDoorToLibrary);
		steps.put(12, enterLibrary);

		ConditionalStep doLibraryStuff = new ConditionalStep(this, enterLibrary);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase1), searchBookcase1);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase2), searchBookcase2);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase3), searchBookcase3);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase4), searchBookcase4);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase5), searchBookcase5);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase6), searchBookcase6);
		doLibraryStuff.addStep(and(inLibrary, isElectricBookcase7), searchBookcase7);
		steps.put(13, doLibraryStuff);

		ConditionalStep goUpToF1Movario = new ConditionalStep(this, enterLibrary);
		goUpToF1Movario.addStep(and(isNextToSpiralStaircase, disarmedStaircase), climbStaircaseInLibrary);
		goUpToF1Movario.addStep(isNextToSpiralStaircase, searchStaircaseInLibrary);
		goUpToF1Movario.addStep(inLibrary, enterElectricDoor);

		ConditionalStep goUpFromLibrary = new ConditionalStep(this, goUpToF1Movario);
		goUpFromLibrary.addStep(and(inMovarioBaseF1, movariosNotesV1, hadRubyKey), useKeyOnBookcase);
		goUpFromLibrary.addStep(and(inMovarioBaseF1, movariosNotesV1, wastePaperBasket), searchWasteBasket);
		goUpFromLibrary.addStep(and(inMovarioBaseF1, movariosNotesV1), pickupWasteBasket);
		goUpFromLibrary.addStep(inMovarioBaseF1, searchDesk);

		steps.put(14, goUpFromLibrary);
		steps.put(15, goUpFromLibrary);

		ConditionalStep goUpToF2Movario = new ConditionalStep(this, goUpToF1Movario);
		goUpToF2Movario.addStep(and(inMovarioBaseF1, hadRubyKey), climbUpHiddenStaircase);
		goUpToF2Movario.addStep(and(inMovarioBaseF1, wastePaperBasket), searchWasteBasket);
		goUpToF2Movario.addStep(and(inMovarioBaseF1), pickupWasteBasket);

		ConditionalStep goSearchBed = new ConditionalStep(this, goUpToF2Movario);
		goSearchBed.addStep(and(inMovarioBaseF2, hadRubyKey), searchBed);
		goSearchBed.addStep(and(inMovarioBaseF2), goDownToF1MovarioBase);

		steps.put(16, goSearchBed);
		steps.put(17, goSearchBed);

		ConditionalStep goUseKeyOnBedChest = new ConditionalStep(this, goUpToF2Movario);
		goUseKeyOnBedChest.addStep(and(inMovarioBaseF2, hadRubyKey), useKeyOnChest);
		goUseKeyOnBedChest.addStep(and(inMovarioBaseF2), goDownToF1MovarioBase);
		steps.put(18, goUseKeyOnBedChest);

		ConditionalStep goSearchChestForTraps = new ConditionalStep(this, goUpToF2Movario);
		goSearchChestForTraps.addStep(and(inMovarioBaseF2, searchedBedForTraps), getNotesFromChest);
		goSearchChestForTraps.addStep(and(inMovarioBaseF2), searchChestForTraps);
		goSearchChestForTraps.addStep(and(inMovarioBaseF2), goDownToF1MovarioBase);
		steps.put(19, goSearchChestForTraps);

		ConditionalStep goSearchChestForNotes2 = new ConditionalStep(this, goUpToF2Movario);
		goSearchChestForNotes2.addStep(and(inMovarioBaseF2), getNotesFromChest);
		steps.put(20, goSearchChestForNotes2);

		ConditionalStep goToPainting = new ConditionalStep(this, goUpFromLibrary);
		goToPainting.addStep(and(isUpstairsNearThaerisk, movariosNotesV1InBank, movariosNotesV2InBank), talkToThaeriskWithNotes);
		goToPainting.addStep(and(inMovarioBaseF1, movariosNotesV1InBank, movariosNotesV2InBank, pulledPaintingLever), solveWeightPuzzle);
		goToPainting.addStep(and(inMovarioBaseF2, movariosNotesV2InBank), goDownToF1MovarioBase);
		goToPainting.addStep(and(inMovarioBaseF1, movariosNotesV1InBank, movariosNotesV2InBank), inspectPainting);
		goToPainting.addStep(and(inMovarioBaseF1, movariosNotesV2InBank), searchDesk);
		goToPainting.addStep(and(inMovarioBaseF2, movariosNotesV2InBank), goDownToF1MovarioBase);
		goToPainting.addStep(and(inMovarioBaseF2), getNotesFromChest);
		goToPainting.addStep(and(inMovarioBaseF1), climbUpHiddenStaircase);
		goToPainting.addStep(and(movariosNotesV1InBank, movariosNotesV2InBank), goUpToThaeriskWithNotes);
		steps.put(21, goToPainting);

		ConditionalStep goContinueThaeriskAfterNotes = new ConditionalStep(this, goUpToThaeriskWithoutNotes);
		goContinueThaeriskAfterNotes.addStep(isUpstairsNearThaerisk, talkToThaeriskWithoutNotes);
		steps.put(22, goContinueThaeriskAfterNotes);

		steps.put(23, killMercenaries);
		steps.put(24, killMercenaries);
		steps.put(25, talkToIdria);
		steps.put(26, talkToAkrisae);
		steps.put(27, talkToAkrisae);
		// Talking to Akrisae, quest varb jumps 27->28->30 after being given orb
		steps.put(28, talkToAkrisae);
		steps.put(29, talkToAkrisae);
		ConditionalStep goPlantOrbOnStranger = new ConditionalStep(this, talkToAkrisaeForTeleport);
		goPlantOrbOnStranger.addStep(teleportedToDraynor, useOrbOnShadyStranger);
		steps.put(30, goPlantOrbOnStranger);
		steps.put(31, talkToAkrisaeAfterOrb);
		steps.put(32, talkToAkrisaeAfterOrb);
		steps.put(33, talkToAkrisaeAfterOrb);
		steps.put(34, talkToAkrisaeAfterOrb);
		ConditionalStep goToBetty = new ConditionalStep(this, buySnapdragonSeed);
		goToBetty.addStep(or(inPortSarim, and(purchasedSnapdragon, teleportedToPortSarim)), talkToBetty);
		goToBetty.addStep(purchasedSnapdragon, getSarimTeleport);
		steps.put(35, goToBetty);

		ConditionalStep goDyeLens = new ConditionalStep(this, talkToBettyForDye);
		goDyeLens.addStep(and(roseTintedLens, inDoorway), useLensOnCounter);
		goDyeLens.addStep(roseTintedLens, standInDoorway);
		goDyeLens.addStep(pinkDye, usePinkDyeOnLanternLens);
		steps.put(36, goDyeLens);

		steps.put(37, searchCounterForSeed);

		ConditionalStep goPlantSeed = new ConditionalStep(this, talkToThaeriskWithSeed);
		goPlantSeed.addStep(and(talkedToThaeriskWithSeed, inWhiteKnightsCastleF3), plantSnapdragon);
		goPlantSeed.addStep(talkedToThaeriskWithSeed, goPlantSnapdragon);
		steps.put(38, goPlantSeed);

		ConditionalStep goTalkToIdriaAfterPlanting = new ConditionalStep(this, talkToIdriaAfterPlanting);
		goTalkToIdriaAfterPlanting.addStep(inWhiteKnightsCastleF3, goFromF3ToF2WhiteKnight);
		goTalkToIdriaAfterPlanting.addStep(inWhiteKnightsCastleF2, goFromF2ToF1WhiteKnight);
		goTalkToIdriaAfterPlanting.addStep(inWhiteKnightsCastleF1, goFromF1ToF0WhiteKnight);
		steps.put(39, goTalkToIdriaAfterPlanting);

		ConditionalStep goContactNpcs = new ConditionalStep(this, activeLunars);
		goContactNpcs.addStep(and(onLunarSpellbook, notContactedCyrisus), contactCyrisus);
		goContactNpcs.addStep(and(onLunarSpellbook, notContactedTurael), contactTurael);
		goContactNpcs.addStep(and(onLunarSpellbook, notContactedMazchna), contactMazchna);
		goContactNpcs.addStep(and(onLunarSpellbook, notContactedDuradel), contactDuradel);
		steps.put(40, goContactNpcs);

		steps.put(41, goHarvestSnapdragon);

		ConditionalStep bringSnapdragonToIdria = new ConditionalStep(this, talkToThaeriskWithSnapdragon);
		bringSnapdragonToIdria.addStep(and(inJailCell, superTruthSerum, charcoal, papyrus), useSerumOnSpy);
		bringSnapdragonToIdria.addStep(and(superTruthSerum, charcoal, papyrus), enterJailCell);
		bringSnapdragonToIdria.addStep(superTruthSerum, searchDrawersForCharcoalAndPapyrus);
		bringSnapdragonToIdria.addStep(truthSerum, useSnapdragonOnSerum);
		bringSnapdragonToIdria.addStep(inWhiteKnightsCastleF3, goFromF3ToF2WhiteKnightThaerisk);
		bringSnapdragonToIdria.addStep(inWhiteKnightsCastleF2, goFromF2ToF1WhiteKnightThaerisk);
		bringSnapdragonToIdria.addStep(inWhiteKnightsCastleF1, goFromF1ToF0WhiteKnightThaerisk);
		steps.put(42, bringSnapdragonToIdria);
		steps.put(43, bringSnapdragonToIdria);

		ConditionalStep getSketch = new ConditionalStep(this, searchDrawersForCharcoalAndPapyrus);
		getSketch.addStep(sketch, giveSketchToIdria);
		getSketch.addStep(and(papyrus, charcoal), talkToSpy);
		steps.put(44, getSketch);
		// ????
		steps.put(410, getSketch);
		steps.put(420, getSketch);

		steps.put(430, talkToIdriaAfterSketch);

		ConditionalStep goRecruitWarriorsGuild = new ConditionalStep(this, goToF1WarriorsGuild);
		goRecruitWarriorsGuild.addStep(notRecruitedGhommal, talkToGhommal);
		goRecruitWarriorsGuild.addStep(notRecruitedHarrallak, talkToHarrallak);
		goRecruitWarriorsGuild.addStep(and(notRecruitedSloane, onF1WarriorsGuild), talkToSloane);
		steps.put(440, goRecruitWarriorsGuild);

		steps.put(450, talkToAkrisaeAfterRecruitment);
		steps.put(460, talkToAkrisaeAfterRecruitment);

		ConditionalStep goToBKF = new ConditionalStep(this, enterBlackKnightFortress);
		goToBKF.addStep(inBlackKnightFortressBasement, inspectCarvedTile);
		goToBKF.addStep(inHiddenRoom, climbDownBlackKnightBasement);
		goToBKF.addStep(inBlackKnightFortress, pushHiddenWall);
		steps.put(465, goToBKF);

		ConditionalStep goCastChargeOrb = new ConditionalStep(this, enterBlackKnightFortress);
		goCastChargeOrb.addStep(inBlackKnightFortressBasement, castChargedOrbOnTile);
		goCastChargeOrb.addStep(inHiddenRoom, climbDownBlackKnightBasement);
		goCastChargeOrb.addStep(inBlackKnightFortress, pushHiddenWall);
		steps.put(470, goCastChargeOrb);

		ConditionalStep goEnterCatacombs = new ConditionalStep(this, enterBlackKnightFortress);
		goEnterCatacombs.addStep(and(inSquallFightRoom, defeatedSurok), plantOrbOnSurok);
		goEnterCatacombs.addStep(inSquallFightRoom, defeatSurok);
		// If collected everything, leave room
		goEnterCatacombs.addStep(and(inCatacombHQ, eliteHelm, eliteBody, eliteLegs, notSearchedWardrobeForEliteArmour), searchWardrobeForEliteArmour);
		goEnterCatacombs.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedWardrobeForSquallOutfit), searchWardrobeForSquallRobes);
		goEnterCatacombs.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedTableForTeleorb), searchDeskForTeleorb);
		goEnterCatacombs.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedKeyRack), searchKeyRack);
		goEnterCatacombs.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedTableForRunes), searchDeskForLawAndDeathRune);
		goEnterCatacombs.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedTableForLobsterAndRestore), searchDeskForLobster);
		goEnterCatacombs.addStep(and(inCatacombHQ, eliteBlackKnightOrSquallOutfit.equipped(), silifTeleorb), climbUpCatacombLadder);
		goEnterCatacombs.addStep(and(inCatacombHQ, eliteBlackKnightOrSquallOutfit.equipped(), seenMap), talkToSilifAtMap);
		goEnterCatacombs.addStep(and(inCatacombHQ, eliteBlackKnightOrSquallOutfit.equipped(), silifIsFollowing), goNearMap);
		goEnterCatacombs.addStep(and(inCatacombHQ, eliteBlackKnightOrSquallOutfit.equipped()), leaveSolidDoor);
		goEnterCatacombs.addStep(and(inCatacombHQ, eliteBlackKnightOrSquallOutfit), equipSquallOrEliteArmour);
		goEnterCatacombs.addStep(inCatacombHQ, killKnightsForEliteArmour);
		// in CatacombsF2, AND have key, AND searched everything
		goEnterCatacombs.addStep(and(inCatacombF2, or(silifIsFollowing, seenMap)), enterNorthernSolidDoorAgain);
		goEnterCatacombs.addStep(and(inCatacombF2, givenArmourToSilif), talkToSilifToFollow);
		goEnterCatacombs.addStep(and(inCatacombF2, openedSilifCell, not(notSearchedWardrobeForSquallOutfit), eliteBlackKnightOutfit,
			usedFoodOnSilif, usedRestoreOnSilif), giveSilifEliteArmour);
		goEnterCatacombs.addStep(and(inCatacombF2, openedSilifCell, not(notSearchedWardrobeForSquallOutfit), eliteBlackKnightOutfit, usedFoodOnSilif, not(notSearchedTableForLobsterAndRestore)), useRestoreOnSilif);
		goEnterCatacombs.addStep(and(inCatacombF2, openedSilifCell, not(notSearchedWardrobeForSquallOutfit), eliteBlackKnightOutfit, not(notSearchedTableForLobsterAndRestore)), useLobsterOnSilif);
		goEnterCatacombs.addStep(and(inCatacombF2, cellKey, not(notSearchedWardrobeForSquallOutfit), eliteBlackKnightOutfit), openSilifsCell);
		goEnterCatacombs.addStep(and(inCatacombF2, openedCatacombShortcut), enterNorthernSolidDoor);
		goEnterCatacombs.addStep(and(inCatacombSouth, openedCatacombShortcut), enterCatacombShortcut);
		goEnterCatacombs.addStep(inCatacombF2, useWesternSolidDoor);
		goEnterCatacombs.addStep(inCatacombNorth, climbWallInCatacombs);
		goEnterCatacombs.addStep(inCatacombSouth, jumpBridge);
		goEnterCatacombs.addStep(inBlackKnightFortressBasement, enterCatacombs);
		goEnterCatacombs.addStep(inHiddenRoom, climbDownBlackKnightBasement);
		goEnterCatacombs.addStep(inBlackKnightFortress, pushHiddenWall);
		steps.put(480, goEnterCatacombs);
		steps.put(490, goEnterCatacombs);
		steps.put(500, goEnterCatacombs);
		steps.put(510, goEnterCatacombs);
		steps.put(520, goEnterCatacombs);
		steps.put(530, goEnterCatacombs);
		steps.put(540, goEnterCatacombs);
		steps.put(550, goEnterCatacombs);
		// 560 was a skipped potential state. Went from 550-570 when entering HQ with Silif
		steps.put(560, goEnterCatacombs);
		steps.put(570, goEnterCatacombs);
		steps.put(580, goEnterCatacombs);
		steps.put(590, goEnterCatacombs);
		// Entered Surok fight
		steps.put(595, goEnterCatacombs);
		steps.put(597, goEnterCatacombs);

		steps.put(600, talkToAkrisaeAfterSurok);
		steps.put(610, talkToAkrisaeAfterSurok);

		// Ready for cell
		ConditionalStep goEnterCellWithRobesOn = new ConditionalStep(this, talkToSilifForRobes);
		goEnterCellWithRobesOn.addStep(squallOutfit, enterCellWithRobesOn);

		steps.put(620, goEnterCellWithRobesOn);

		ConditionalStep goWitnessTrueTerror = new ConditionalStep(this, enterBlackKnightFortress);
		goWitnessTrueTerror.addStep(and(onChaosTempleF1), jumpToLedge);
		goWitnessTrueTerror.addStep(and(inLucienCamp), climbIceWall);
		goWitnessTrueTerror.addStep(and(inTeleportSpot, strangeTeleorb, deathRune, lawRune), activateStrangeTeleorb);
		goWitnessTrueTerror.addStep(and(inSquallFightRoom, strangeTeleorb, deathRune, lawRune), standAtTeleportSpot);
		goWitnessTrueTerror.addStep(and(inSquallFightRoom, strangeTeleorb, not(notSearchedTableForRunes)), getRunes);
		goWitnessTrueTerror.addStep(and(inSquallFightRoom), goDownForOrbAndRunes);
		goWitnessTrueTerror.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedTableForTeleorb), takeStrangeTeleorb);
		goWitnessTrueTerror.addStep(and(inCatacombHQ, isSafeInCatacombs, notSearchedTableForRunes), takeRunes);
		goWitnessTrueTerror.addStep(and(inCatacombHQ), goUpToUseTeleorb);
		goWitnessTrueTerror.addStep(and(inCatacombF2, openedCatacombShortcut), enterNorthernSolidDoor);
		goWitnessTrueTerror.addStep(and(inCatacombSouth, openedCatacombShortcut), enterCatacombShortcut);
		goWitnessTrueTerror.addStep(inCatacombF2, useWesternSolidDoor);
		goWitnessTrueTerror.addStep(inCatacombNorth, climbWallInCatacombs);
		goWitnessTrueTerror.addStep(inCatacombSouth, jumpBridge);
		goWitnessTrueTerror.addStep(inBlackKnightFortressBasement, enterCatacombs);
		goWitnessTrueTerror.addStep(inHiddenRoom, climbDownBlackKnightBasement);
		goWitnessTrueTerror.addStep(inBlackKnightFortress, pushHiddenWall);
		steps.put(630, goWitnessTrueTerror);
		steps.put(640, goWitnessTrueTerror);
		steps.put(650, goWitnessTrueTerror);
		steps.put(660, goWitnessTrueTerror);

		steps.put(670, talkToIdriaAfterChapel);
		steps.put(680, talkToIdriaAfterChapel);

		ConditionalStep goTalkToMovario = new ConditionalStep(this, teleportToJuna);
		goTalkToMovario.addStep(inJunaRoom, talkToMovario);
		steps.put(690, goTalkToMovario);
		steps.put(700, goTalkToMovario);

		ConditionalStep goIntoAbyss = new ConditionalStep(this, teleportToJuna);
		goIntoAbyss.addStep(inJunaRoom, useLitSapphireLanternOnLightCreature);
		steps.put(710, goIntoAbyss);

		ConditionalStep goDoSkullPuzzle = new ConditionalStep(this, goIntoAbyss);
		goDoSkullPuzzle.addStep(and(inAbyssEntryF2, placedAllBlocks), touchCube);
		goDoSkullPuzzle.addStep(and(inAbyssEntryF1, placedAllBlocks), climbUpToCubeF1ToF2);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, placedAllBlocks), climbUpToCubeF0ToF1);

		// TODO: Add in check for what leads where by checking WP of last tile, and use that to determine which cavity went where once detected player has entered at reccess zone
		goDoSkullPuzzle.addStep(and(inAbyssEntryF2, placedAllOrbs, notPlacedFireBlock), useFireBlockOnRecess);
		goDoSkullPuzzle.addStep(and(inAbyssEntryF1, placedAllOrbs, notPlacedFireBlock), climbWallNextToSkullF1ToF2);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, placedAllOrbs, notPlacedFireBlock), climbWallNextToSkullF0ToF1);

		goDoSkullPuzzle.addStep(and(inAbyssEntryF2, placedAllOrbs), climbDownFromSkullF2ToF1);
		goDoSkullPuzzle.addStep(and(inAbyssEntryF1, placedAllOrbs), climbDownFromSkullF1ToF0);

		goDoSkullPuzzle.addStep(and(inAirCavity, placedAllOrbs, placedAirBlock), leaveAirRecess);
		goDoSkullPuzzle.addStep(and(inAirCavity, placedAllOrbs), useAirBlockOnRecess);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, placedAllOrbs, not(placedAirBlock)), enterWestCavity);

		goDoSkullPuzzle.addStep(and(inEarthCavity, placedAllOrbs, placedEarthBlock), leaveEarthRecess);
		goDoSkullPuzzle.addStep(and(inEarthCavity, placedAllOrbs), useEarthBlockOnRecess);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, placedAllOrbs, not(placedEarthBlock)), enterMiddleCavity);

		goDoSkullPuzzle.addStep(and(inWaterCavity, placedAllOrbs, placedWaterBlock), leaveWaterRecess);
		goDoSkullPuzzle.addStep(and(inWaterCavity, placedAllOrbs), useWaterBlockOnRecess);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, placedAllOrbs, not(placedWaterBlock)), enterEastCavity);

		goDoSkullPuzzle.addStep(and(inAirCavity), leaveAirRecess);
		goDoSkullPuzzle.addStep(and(inEarthCavity), leaveEarthRecess);
		goDoSkullPuzzle.addStep(and(inWaterCavity), leaveWaterRecess);

		goDoSkullPuzzle.addStep(and(inAbyssEntryF2, usedChiselOnAllBraziers, or(notPlacedAirOrb, notPlacedFireOrb, notPlacedEarthOrb)), climbDownFromSkullF2ToF1);
		goDoSkullPuzzle.addStep(and(inAbyssEntryF1, usedChiselOnAllBraziers, or(notPlacedAirOrb, notPlacedFireOrb, notPlacedEarthOrb)), climbDownFromSkullF1ToF0);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, usedChiselOnAllBraziers, notPlacedAirOrb), useOrbOnAirRecessedBlock);

		goDoSkullPuzzle.addStep(and(inAbyssEntry, usedChiselOnAllBraziers, notPlacedFireOrb), useOrbOnFireRecessedBlock);

		goDoSkullPuzzle.addStep(and(inAbyssEntry, usedChiselOnAllBraziers, notPlacedEarthOrb), useOrbOnEarthRecessedBlock);

		goDoSkullPuzzle.addStep(and(inAbyssEntryF2, usedChiselOnAllBraziers, notPlacedWaterOrb), useOrbOnWaterRecessedBlock);
		goDoSkullPuzzle.addStep(and(inAbyssEntryF1, usedChiselOnAllBraziers, notPlacedWaterOrb), climbWallNextToSkullF1ToF2);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, usedChiselOnAllBraziers, notPlacedWaterOrb), climbWallNextToSkullF0ToF1);

		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade, hammer, chisel, notUsedSpadeOnFireRocks), useSpadeOnFireRocks);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade, hammer, chisel, notUsedChiselOnFireBrazier), useChiselOnFireBrazier);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade, hammer, chisel, notUsedSpadeOnEarthRocks), useSpadeOnEarthRocks);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade, hammer, chisel, notUsedChiselOnEarthBrazier), useChiselOnEarthBrazier);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade, hammer, chisel, notUsedChiselOnAirBrazier), useChiselOnAirBrazier);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade, hammer, chisel, notUsedChiselOnWaterBrazier), useChiselOnWaterBrazier);
		goDoSkullPuzzle.addStep(and(inAbyssEntry, spade), searchRemainsForHammerAndChisel);
		goDoSkullPuzzle.addStep(inAbyssEntry, searchRemainsForSpade);
		steps.put(740, goDoSkullPuzzle);

		ConditionalStep goIntoMainTemple = new ConditionalStep(this, goIntoAbyss);
		goIntoMainTemple.addStep(and(inGuthixianTemple, fullDruidPouch), herblorePuzzle);
		goIntoMainTemple.addStep(and(inGuthixianTemple, emptyDruidPouch), castBloomToFillPouch);
		goIntoMainTemple.addStep(inGuthixianTemple, getPouch);
		goIntoMainTemple.addStep(inAbyssEntry, climbUpToCubeF0ToF1);
		goIntoMainTemple.addStep(inAbyssEntryF1, climbUpToCubeF1ToF2);
		goIntoMainTemple.addStep(inAbyssEntryF2, enterSkull);
		goIntoMainTemple.addStep(inEarthCavity, leaveEarthRecess);
		goIntoMainTemple.addStep(inWaterCavity, leaveWaterRecess);
		goIntoMainTemple.addStep(inAirCavity, leaveAirRecess);
		steps.put(760, goIntoMainTemple);
		steps.put(770, goIntoMainTemple);

		ConditionalStep goConfrontBalanceElemental = new ConditionalStep(this, goIntoAbyss);
		goConfrontBalanceElemental.addStep(inGuthixianTemple, approachStoneOfJas);
		goConfrontBalanceElemental.addStep(inAbyssEntry, climbUpToCubeF0ToF1);
		goConfrontBalanceElemental.addStep(inAbyssEntryF1, climbUpToCubeF1ToF2);
		goConfrontBalanceElemental.addStep(inAbyssEntryF2, enterSkull);
		goConfrontBalanceElemental.addStep(inEarthCavity, leaveEarthRecess);
		goConfrontBalanceElemental.addStep(inWaterCavity, leaveWaterRecess);
		goConfrontBalanceElemental.addStep(inAirCavity, leaveAirRecess);
		steps.put(780, goConfrontBalanceElemental);

		ConditionalStep goFightBalanceElemental = new ConditionalStep(this, goIntoAbyss);
		goFightBalanceElemental.addStep(inGuthixianTemple, fightBalanceElemental);
		goFightBalanceElemental.addStep(inAbyssEntry, climbUpToCubeF0ToF1);
		goFightBalanceElemental.addStep(inAbyssEntryF1, climbUpToCubeF1ToF2);
		goFightBalanceElemental.addStep(inAbyssEntryF2, enterSkull);
		goFightBalanceElemental.addStep(inEarthCavity, leaveEarthRecess);
		goFightBalanceElemental.addStep(inWaterCavity, leaveWaterRecess);
		goFightBalanceElemental.addStep(inAirCavity, leaveAirRecess);
		steps.put(800, goFightBalanceElemental);

		ConditionalStep goTouchStone = new ConditionalStep(this, goIntoAbyss);
		goTouchStone.addStep(inGuthixianTemple, touchStone);
		goTouchStone.addStep(inAbyssEntry, climbUpToCubeF0ToF1);
		goTouchStone.addStep(inAbyssEntryF1, climbUpToCubeF1ToF2);
		goTouchStone.addStep(inAbyssEntryF2, enterSkull);
		goTouchStone.addStep(inEarthCavity, leaveEarthRecess);
		goTouchStone.addStep(inWaterCavity, leaveWaterRecess);
		goTouchStone.addStep(inAirCavity, leaveAirRecess);
		steps.put(840, goTouchStone);

		ConditionalStep goTalkToMovarioAtStone = new ConditionalStep(this, goIntoAbyss);
		goTalkToMovarioAtStone.addStep(inGuthixianTemple, talkToMovarioAtStone);
		goTalkToMovarioAtStone.addStep(inAbyssEntry, climbUpToCubeF0ToF1);
		goTalkToMovarioAtStone.addStep(inAbyssEntryF1, climbUpToCubeF1ToF2);
		goTalkToMovarioAtStone.addStep(inAbyssEntryF2, enterSkull);
		goTalkToMovarioAtStone.addStep(inEarthCavity, leaveEarthRecess);
		goTalkToMovarioAtStone.addStep(inWaterCavity, leaveWaterRecess);
		goTalkToMovarioAtStone.addStep(inAirCavity, leaveAirRecess);
		steps.put(860, goTalkToMovarioAtStone);
		steps.put(870, goTalkToMovarioAtStone);

		ConditionalStep goFightTormentedDemons = new ConditionalStep(this, goIntoAbyss);
		goFightTormentedDemons.addStep(inGuthixianTemple, fightTormentedDemons);
		goFightTormentedDemons.addStep(inAbyssEntry, climbUpToCubeF0ToF1);
		goFightTormentedDemons.addStep(inAbyssEntryF1, climbUpToCubeF1ToF2);
		goFightTormentedDemons.addStep(inAbyssEntryF2, enterSkull);
		goFightTormentedDemons.addStep(inEarthCavity, leaveEarthRecess);
		goFightTormentedDemons.addStep(inWaterCavity, leaveWaterRecess);
		goFightTormentedDemons.addStep(inAirCavity, leaveAirRecess);
		steps.put(880, goFightTormentedDemons);

		ConditionalStep goFinishQuest = new ConditionalStep(this, finishQuest);
		goFinishQuest.addStep(inGuthixianTemple, teleportWithIdria);
		steps.put(890, goFinishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		logs.setTooltip("You can take an axe from the stump west of the Hunting Expert's hut to cut a tree for a log");
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		knife.setTooltip("There is a knife spawn next to the Hunting Expert");

		sapphireLantern = new ItemRequirement("Sapphire lantern", ItemID.SAPPHIRE_LANTERN_4701).isNotConsumed();
		litSapphireLantern = new ItemRequirement("Lit sapphire lantern", ItemID.SAPPHIRE_LANTERN_4702).isNotConsumed();
		litSapphireLantern.setTooltip("You can make this by using a cut sapphire on a bullseye lantern");

		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE);
		mindRune = new ItemRequirement("Mind rune", ItemID.MIND_RUNE);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		deathRune = new ItemRequirement("Death rune", ItemID.DEATH_RUNE);

		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE);

		log = new ItemRequirement("Logs", ItemID.LOGS);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		lanternLens = new ItemRequirement("Lantern lens", ItemID.LANTERN_LENS);
		mortMyreFungus = new ItemRequirement("Mort myre fungus", ItemID.MORT_MYRE_FUNGUS);
		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB);
		ringOfCharosA = new ItemRequirement("Ring of charos (a)", ItemID.RING_OF_CHAROSA);
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		bronzeMedHelm = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM);
		ironChainbody = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY);
		snapdragonSeed = new ItemRequirement("Snapdragon seed", ItemID.SNAPDRAGON_SEED);
		dibber = new ItemRequirement("Seed dibber (only if not done barb training)", ItemID.SEED_DIBBER);
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);

		ItemRequirement cosmic3 = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE, 3);
		ItemRequirement fire30 = new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 63));
		ItemRequirement air30 = new ItemRequirement("Air runes", ItemID.AIR_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 66));
		ItemRequirement water30 = new ItemRequirement("Water runes", ItemID.WATER_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 56));
		ItemRequirement earth30 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 60));
		ItemRequirements elemental30 = new ItemRequirements(LogicType.OR, "Elemental runes", air30, water30, earth30, fire30);
		elemental30.addAlternates(ItemID.FIRE_RUNE, ItemID.EARTH_RUNE, ItemID.AIR_RUNE);
		elemental30.setExclusiveToOneItemType(true);

		chargeOrbSpell = new ItemRequirements(LogicType.AND, "Runes for any charge orb spell you have the level to cast", cosmic3, elemental30);
		meleeGear = new ItemRequirement("Melee weapon", -1, -1).isNotConsumed();
		meleeGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		rangedGear = new ItemRequirement("Ranged weapon", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		magicGear = new ItemRequirement("Magic weapon", -1, -1).isNotConsumed();
		magicGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());

		// Recommended items
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
		burthorpeTeleport = new ItemRequirement("Teleport to Burthorpe", ItemCollections.COMBAT_BRACELETS);
		burthorpeTeleport.addAlternates(ItemCollections.GAMES_NECKLACES);
		khazardTeleport = new ItemRequirement("Khazard teleport", ItemCollections.ARDY_CLOAKS);
		feldipHillsTeleport = new ItemRequirement("Feldip hills teleport", ItemID.FELDIP_HILLS_TELEPORT);
		feldipHillsTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		camelotTeleport = new ItemRequirement("Seers' Village teleport", ItemID.CAMELOT_TELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.FALADOR_TELEPORT);
		lunarSpellbook = new SpellbookRequirement(Spellbook.LUNAR);

		lobster = new ItemRequirement("Lobster, or some other food", ItemID.LOBSTER);
		restorePotion = new ItemRequirement("Restore potion", ItemID.RESTORE_POTION4);
		restorePotion.addAlternates(ItemID.RESTORE_POTION3, ItemID.RESTORE_POTION2, ItemID.RESTORE_POTION1);

		ItemRequirement natureRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE);
		ItemRequirement waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE);
		ItemRequirement fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE);
		ItemRequirement earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		ItemRequirement bodyRune = new ItemRequirement("Body rune", ItemID.BODY_RUNE);
		ItemRequirement lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE);

		bindRunes = new ItemRequirements("Bind spell", natureRune.quantity(2), waterRune.quantity(3), earthRune.quantity(3));
		weakenRunes = new ItemRequirements("Weaken spell", bodyRune, waterRune.quantity(3), earthRune.quantity(2));
		alchRunes = new ItemRequirements("Low level alchemy spell", natureRune, fireRune.quantity(3));
		telegrabRunes = new ItemRequirements("Telekinetic grab", lawRune, airRune);

		spade = new ItemRequirement("Spade", ItemID.SPADE);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);

		emptyDruidPouch = new ItemRequirement("Druid pouch", ItemID.DRUID_POUCH);
		fullDruidPouch = new ItemRequirement("Druid pouch", ItemID.DRUID_POUCH_2958);
		silverSickleB = new ItemRequirement("Silver sickle (b)", ItemID.SILVER_SICKLE_B);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		// Quest items
		dirtyShirt = new ItemRequirement("Dirty shirt", ItemID.DIRTY_SHIRT);
		dirtyShirt.setTooltip("You can get another from the Khazard Launderer west of the Fight Arena");

		unconsciousBroav = new ItemRequirement("Unconscious broav", ItemID.UNCONSCIOUS_BROAV);
		broav = new ItemRequirement("Broav", ItemID.BROAV);
		movariosNotesV1 = new ItemRequirement("Movario's notes (volume 1)", ItemID.MOVARIOS_NOTES_VOLUME_1);
		movariosNotesV2 = new ItemRequirement("Movario's notes (volume 2)", ItemID.MOVARIOS_NOTES_VOLUME_2);
		movariosNotesV1InBank = new ItemRequirement("Movario's notes (volume 1)", ItemID.MOVARIOS_NOTES_VOLUME_1).alsoCheckBank(questBank);
		movariosNotesV2InBank = new ItemRequirement("Movario's notes (volume 2)", ItemID.MOVARIOS_NOTES_VOLUME_2).alsoCheckBank(questBank);
		wastePaperBasket = new ItemRequirement("Waste-paper basket", ItemID.WASTEPAPER_BASKET);
		rubyKey = new ItemRequirement("Ruby key", ItemID.RUBY_KEY_29523);
		teleorb = new ItemRequirement("Teleorb", ItemID.TELEORB);
		teleorb.setTooltip("You can get another one from Akrisae in the White Knights' Castle");
		pinkDye = new ItemRequirement("Pink dye", ItemID.PINK_DYE);
		roseTintedLens = new ItemRequirement("Rose-tinted lens", ItemID.ROSE_TINTED_LENS);
		enrichedSnapdragonSeed = new ItemRequirement("Enriched snapdragon seed", ItemID.ENRICHED_SNAPDRAGON_SEED);
		enrichedSnapdragonSeed.setTooltip("You can get another from Betty in Port Sarim");

		enrichedSnapdragon = new ItemRequirement("Enriched snapdragon", ItemID.ENRICHED_SNAPDRAGON);
		enrichedSnapdragon.setTooltip("You can get another from the druid next to the patch on top of White Knights' Castle.");
		truthSerum = new ItemRequirement("Truth serum", ItemID.TRUTH_SERUM_29532);
		superTruthSerum = new ItemRequirement("Super truth serum", ItemID.SUPER_TRUTH_SERUM);
		sketch = new ItemRequirement("Suspect sketch", ItemID.SUSPECT_SKETCH);

		eliteHelm = new ItemRequirement("Elite black full helm", ItemID.ELITE_BLACK_FULL_HELM);
		eliteBody = new ItemRequirement("Elite black platebody", ItemID.ELITE_BLACK_PLATEBODY);
		eliteLegs = new ItemRequirement("Elite black platelegs", ItemID.ELITE_BLACK_PLATELEGS);

		eliteBlackKnightOutfit = new ItemRequirements("Full elite black knight", eliteHelm, eliteBody, eliteLegs);

		talkedToMovario = new VarbitRequirement(9653, 710, Operation.GREATER_EQUAL);

		darkSquallHood = new ItemRequirement("Dark squall hood", ItemID.DARK_SQUALL_HOOD);
		darkSquallBody = new ItemRequirement("Dark squall robe top", ItemID.DARK_SQUALL_ROBE_TOP);
		darkSquallLegs = new ItemRequirement("Dark squall robe bottom", ItemID.DARK_SQUALL_ROBE_BOTTOM);
		squallOutfit = new ItemRequirements("Full dark squall outfit", darkSquallHood, darkSquallBody, darkSquallLegs).hideConditioned(talkedToMovario);

		eliteBlackKnightOrSquallOutfit = new ItemRequirements(LogicType.OR, "Full elite black knight or dark squall outfit", eliteBlackKnightOutfit, squallOutfit);

		cellKey = new ItemRequirement("Cell key", ItemID.CELL_KEY);

		silifTeleorb = new ItemRequirement("Teleorb", ItemID.TELEORB_29537);

		strangeTeleorb = new ItemRequirement("Strange teleorb", ItemID.STRANGE_TELEORB);

		airOrb = new ItemRequirement("Air orb", ItemID.AIR_ORB);
		waterOrb = new ItemRequirement("Water orb", ItemID.WATER_ORB);
		earthOrb = new ItemRequirement("Earth orb", ItemID.EARTH_ORB);
		fireOrb = new ItemRequirement("Fire orb", ItemID.FIRE_ORB);

		airBlock = new ItemRequirement("Air block", ItemID.AIR_BLOCK);
		airBlock.setTooltip("You can get another by searching the recess near the eastern cavity");
		waterBlock = new ItemRequirement("Water block", ItemID.WATER_BLOCK);
		waterBlock.setTooltip("You can get another by searching the recess near the upstairs middle cavity");
		earthBlock = new ItemRequirement("Earth block", ItemID.EARTH_BLOCK);
		earthBlock.setTooltip("You can get another by searching the recess near the western cavity");
		fireBlock = new ItemRequirement("Fire block", ItemID.FIRE_BLOCK);
		fireBlock.setTooltip("You can get another by searching the recess near the middle cavity");

		toadflax = new ItemRequirement("Toadflax", ItemID.TOADFLAX);
		toadsLegs = new ItemRequirement("Toad's legs", ItemID.TOADS_LEGS);
		guamLeaf = new ItemRequirement("Guam leaf", ItemID.GUAM_LEAF);
		eyeOfNewt = new ItemRequirement("Eye of newt", ItemID.EYE_OF_NEWT);
		iritLeaf = new ItemRequirement("Irit leaf", ItemID.IRIT_LEAF);
		harralander = new ItemRequirement("Harralander", ItemID.HARRALANDER);
		redSpidersEggs = new ItemRequirement("Red spider's eggs", ItemID.RED_SPIDERS_EGGS);
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC);
		silverDust = new ItemRequirement("Silver dust", ItemID.SILVER_DUST);
		goatHorn = new ItemRequirement("Goat horn dust", ItemID.GOAT_HORN_DUST);
		ranarrWeed = new ItemRequirement("Ranarr weed", ItemID.RANARR_WEED);
		whiteBerries = new ItemRequirement("White berries", ItemID.WHITE_BERRIES);
		cadantine = new ItemRequirement("Cadantine", ItemID.CADANTINE);
		avantoe = new ItemRequirement("Avantoe", ItemID.AVANTOE);
		moryMyreFungus = new ItemRequirement("Mory myre fungus", ItemID.MORT_MYRE_FUNGUS);
		chocolateDust = new ItemRequirement("Chocolate dust", ItemID.CHOCOLATE_DUST);
		snapeGrass = new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS);
		kebbitTeethdust = new ItemRequirement("Kebbit teeth dust", ItemID.KEBBIT_TEETH_DUST);
		lantadyme = new ItemRequirement("Lantadyme", ItemID.LANTADYME);
		potatoCactus = new ItemRequirement("Potato cactus", ItemID.POTATO_CACTUS);
		dwarfWeed = new ItemRequirement("Dwarf weed", ItemID.DWARF_WEED);
		wineOfZamorak = new ItemRequirement("Wine of zamorak", ItemID.WINE_OF_ZAMORAK);
		snapdragon = new ItemRequirement("Snapdragon", ItemID.SNAPDRAGON);
		tarromin = new ItemRequirement("Tarromin", ItemID.TARROMIN);
		limpwurt = new ItemRequirement("Limpwurt root", ItemID.LIMPWURT_ROOT);
		kwuarm = new ItemRequirement("Kwuarm", ItemID.KWUARM);

		hadToadflax = or(toadflax, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadToadsLegs = or(toadsLegs, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadGuamLeaf = or(guamLeaf, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadEyeOfNewt = or(eyeOfNewt, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadIritLeaf = or(iritLeaf, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadHarralander = or(harralander, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadRedSpidersEggs = or(redSpidersEggs, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadGarlic = or(garlic, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadSilverDust = or(silverDust, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadGoatHorn = or(goatHorn, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadRanarrWeed = or(ranarrWeed, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadWhiteBerries = or(whiteBerries, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadCadantine = or(cadantine, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadAvantoeForHunterPotion = or(avantoe, new VarbitRequirement(10924, 1), new VarbitRequirement(10924, 3));
		hadMortMyreFungus = or(mortMyreFungus, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadChocolateDust = or(chocolateDust, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadSnapeGrass = or(snapeGrass, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadKebbitTeethdustForHunterPotion = or(kebbitTeethdust, new VarbitRequirement(10924, 2), new VarbitRequirement(10924, 3));
		hadLantadyme = or(lantadyme, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadPotatoCactus = or(potatoCactus, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadDwarfWeed = or(dwarfWeed, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadWineOfZamorak = or(wineOfZamorak, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadSnapdragon = or(snapdragon, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadTarromin = or(tarromin, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadLimpwurt = or(limpwurt, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadKwuarm = or(kwuarm, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadEmptyDruidPouch = or(emptyDruidPouch, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadFullDruidPouch = or(fullDruidPouch, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));
		hadSilverSickleB = or(silverSickleB, new VarbitRequirement(0, 1), new VarbitRequirement(0, 1));

		// Requirements
		upstairsNearThaeriskZone = new Zone(new WorldPoint(2898, 3448, 1), new WorldPoint(2917, 3452, 1));
		isUpstairsNearThaerisk = new ZoneRequirement(upstairsNearThaeriskZone);

		assassinsNearby = or(new NpcRequirement(NpcID.ASSASSIN_13514), new NpcRequirement(NpcID.ASSASSIN_13515));
		paidLaunderer = new VarbitRequirement(10756, 2, Operation.GREATER_EQUAL);
		talkedToLaunderer = new VarbitRequirement(10756, 4, Operation.GREATER_EQUAL);
		trapSetUp = new VarbitRequirement(10929, 1);
		trapBaited = new VarbitRequirement(10929, 2, Operation.GREATER_EQUAL);
		broavTrapped = new VarbitRequirement(10929, 4);
		broavNearby = new VarplayerRequirement(447, List.of(NpcID.BROAV, 13516), 16);
		hasBroav = or(broavNearby, broav.alsoCheckBank(questBank));

		nearTable = new Zone(new WorldPoint(2516, 3246, 0), new WorldPoint(2522, 3252, 0));
		isNearTable = new ZoneRequirement(nearTable);

		movarioFirstRoom = new Zone(new WorldPoint(4097, 4931, 0), new WorldPoint(4156, 4988, 0));
		inMovarioFirstRoom = new ZoneRequirement(movarioFirstRoom);

		claimedRunes = new VarbitRequirement(10961, 1);

		doorNeedsFireRune = new Conditions(true, new WidgetTextRequirement(877, 6, "0"));
		doorNeedsAirRune = new Conditions(true, new WidgetTextRequirement(877, 6, "1"));
		doorNeedsEarthRune = new Conditions(true, new WidgetTextRequirement(877, 6, "2"));
		doorNeedsWaterRune = new Conditions(true, new WidgetTextRequirement(877, 6, "3"));
		doorNeedsMindRune = new Conditions(true, new WidgetTextRequirement(877, 6, "4"));

		movarioDoorRoom = new Zone(new WorldPoint(4207, 4973, 0), new WorldPoint(4214, 4986, 0));
		inMovarioDoorRoom = new ZoneRequirement(movarioDoorRoom);

		library = new Zone(new WorldPoint(4166, 4946, 0), new WorldPoint(4190, 4968, 0));
		inLibrary = new ZoneRequirement(library);

		isElectricBookcase1 = new VarbitRequirement(10763, 1);
		isElectricBookcase2 = new VarbitRequirement(10764, 1);
		isElectricBookcase3 = new VarbitRequirement(10765, 1);
		isElectricBookcase4 = new VarbitRequirement(10766, 1);
		isElectricBookcase5 = new VarbitRequirement(10767, 1);
		isElectricBookcase6 = new VarbitRequirement(10768, 1);
		isElectricBookcase7 = new VarbitRequirement(10769, 1);

		nextToSpiralStaircase = new Zone(new WorldPoint(4180, 4949, 0), new WorldPoint(4183, 4952, 0));
		isNextToSpiralStaircase = new ZoneRequirement(nextToSpiralStaircase);

		disarmedStaircase = new VarbitRequirement(10799, 1);

		movarioBaseF1 = new Zone(new WorldPoint(4169, 4942, 1), new WorldPoint(4189, 4962, 1));
		inMovarioBaseF1 = new ZoneRequirement(movarioBaseF1);

		movarioBaseF2 = new Zone(new WorldPoint(4172, 4953, 2), new WorldPoint(4180, 4958, 2));
		inMovarioBaseF2 = new ZoneRequirement(movarioBaseF2);

		hadRubyKey = or(rubyKey, new VarbitRequirement(9653, 19, Operation.GREATER_EQUAL));
		searchedBedForTraps = new VarbitRequirement(10798, 1);

		pulledPaintingLever = new VarbitRequirement(10758, 1);

		weightRoom = new Zone(new WorldPoint(4177, 4944, 1), new WorldPoint(4181, 4947, 1));
		inWeightRoom = new ZoneRequirement(weightRoom);

		teleportedToDraynor = new VarbitRequirement(10841, 1);

		portSarim = new Zone(new WorldPoint(2911, 3188, 0), new WorldPoint(3137, 3308, 3));
		inPortSarim = new ZoneRequirement(portSarim);

		doorway = new Zone(new WorldPoint(3016, 3259, 0), new WorldPoint(3016, 3259, 0));
		inDoorway = new ZoneRequirement(doorway);

		purchasedSnapdragon = new VarbitRequirement(10853, 1);
		teleportedToPortSarim = new VarbitRequirement(10842, 1);

		talkedToThaeriskWithSeed = new VarbitRequirement(10847, 1);

		whiteKnightsCastleF1 = new Zone(new WorldPoint(2954, 3353, 1), new WorldPoint(2998, 3327, 1));
		whiteKnightsCastleF2 = new Zone(new WorldPoint(2954, 3353, 2), new WorldPoint(2998, 3327, 2));
		whiteKnightsCastleF3 = new Zone(new WorldPoint(2954, 3353, 3), new WorldPoint(2998, 3327, 3));
		inWhiteKnightsCastleF1 = new ZoneRequirement(whiteKnightsCastleF1);
		inWhiteKnightsCastleF2 = new ZoneRequirement(whiteKnightsCastleF2);
		inWhiteKnightsCastleF3 = new ZoneRequirement(whiteKnightsCastleF3);

		onLunarSpellbook = new SpellbookRequirement(Spellbook.LUNAR);

		notContactedTurael = new VarbitRequirement(10785, 0);
		notContactedDuradel = new VarbitRequirement(10786, 0);
		notContactedMazchna = new VarbitRequirement(10787, 0);
		notRecruitedGhommal = new VarbitRequirement(10788, 0);
		notRecruitedHarrallak = new VarbitRequirement(10789, 0);
		notRecruitedSloane = new VarbitRequirement(10790, 0);
		notContactedCyrisus = new VarbitRequirement(10791, 0);

		f1WarriorsGuild = new Zone(new WorldPoint(2835, 3531, 1), new WorldPoint(3878, 3558, 1));
		onF1WarriorsGuild = new ZoneRequirement(f1WarriorsGuild);

		blackKnightFortress1 = new Zone(new WorldPoint(3008, 3513, 0), new WorldPoint(3012, 3518, 0));
		blackKnightFortress2 = new Zone(new WorldPoint(3013, 3515, 0), new WorldPoint(3019, 3516, 0));
		blackKnightFortress3 = new Zone(new WorldPoint(3019, 3513, 0), new WorldPoint(3019, 3518, 0));
		inBlackKnightFortress = new ZoneRequirement(blackKnightFortress1, blackKnightFortress2, blackKnightFortress3);

		hiddenRoom = new Zone(new WorldPoint(3015, 3517, 0), new WorldPoint(3016, 3519, 0));
		inHiddenRoom = new ZoneRequirement(hiddenRoom);

		blackKnightFortressBasement = new Zone(new WorldPoint(1862, 4230, 0), new WorldPoint(1873, 4247, 0));
		inBlackKnightFortressBasement = new ZoneRequirement(blackKnightFortressBasement);

		hasCastChargeOrb = new VarbitRequirement(9653, 480, Operation.GREATER_EQUAL);

		catacombSouth1 = new PolyZone(List.of(new WorldPoint(4100, 4670, 1), new WorldPoint(4101, 4698, 1), new WorldPoint(4114, 4698, 1),
			new WorldPoint(4115, 4705, 1), new WorldPoint(4119, 4711, 1), new WorldPoint(4162, 4715, 1), new WorldPoint(4161, 4663, 1)));
		inCatacombSouth = new ZoneRequirement(catacombSouth1);

		catacombNorth1 = new Zone(new WorldPoint(4090, 4699, 1), new WorldPoint(4119, 4752, 1));
		catacombNorth2 = new Zone(new WorldPoint(4118, 4712, 1), new WorldPoint(4140, 4727, 1));
		inCatacombNorth = new ZoneRequirement(catacombNorth1, catacombNorth2);

		openedCatacombShortcut = new VarbitRequirement(10857, 1);

		catacombF2 = new Zone(new WorldPoint(4090, 4730, 2), new WorldPoint(4160, 4810, 2));
		inCatacombF2 = new ZoneRequirement(catacombF2);

		catacombHQ = new Zone(new WorldPoint(4108, 4839, 1), new WorldPoint(4148, 4859, 1));
		inCatacombHQ = new ZoneRequirement(catacombHQ);

		notSearchedWardrobeForEliteArmour = new VarbitRequirement(10806, 0);
		notSearchedWardrobeForSquallOutfit = new VarbitRequirement(10779, 0);

		notSearchedTableForTeleorb = new VarbitRequirement(10855, 0);
		notSearchedTableForRunes = new VarbitRequirement(10854, 0);
		notSearchedTableForLobsterAndRestore = new VarbitRequirement(10805, 0);
		notSearchedKeyRack = new VarbitRequirement(10804, 0);
		openedSilifCell = new VarbitRequirement(9653, 520, Operation.GREATER_EQUAL);

		isSafeInCatacombs = new VarbitRequirement(10802, 1);
		// TODO: Check if these are needed to actually feed and restore Silif
		talkedToSilif = new VarbitRequirement(10848, 1);
		// 10849 been told Silif needs food and restore potion

		// TODO: Try using restore potion first
		// Fed him, 9653 -> 530
		usedFoodOnSilif = new VarbitRequirement(10850, 1);
		usedRestoreOnSilif = new VarbitRequirement(10851, 1);
		// 10852 0->1 represents ready to recieve armour

		// Equipped armour:
		// 9653 540->550
		// 10780 2->3 represents state of Silif
		givenArmourToSilif = new VarbitRequirement(9653, 550, Operation.GREATER_EQUAL);

		silifIsFollowing = new VarplayerRequirement(447, 13522, 16);
		seenMap = new VarbitRequirement(9653, 580, Operation.GREATER_EQUAL);

		squallFightRoom = new Zone(new WorldPoint(4126, 4840, 2), new WorldPoint(4151, 4861, 2));
		inSquallFightRoom = new ZoneRequirement(squallFightRoom);
		defeatedSurok = new VarbitRequirement(9653, 597, Operation.GREATER_EQUAL);

		teleportSpot = new Zone(new WorldPoint(4135, 4848, 2), new WorldPoint(4138, 4851, 2));
		inTeleportSpot = new ZoneRequirement(teleportSpot);

		lucienCamp = new Zone(new WorldPoint(2893, 3777, 0), new WorldPoint(2943, 3854, 0));
		inLucienCamp = new ZoneRequirement(lucienCamp);

		chaosTempleF1 = new Zone(new WorldPoint(2942, 3815, 1), new WorldPoint(2958, 3829, 1));
		onChaosTempleF1 = new ZoneRequirement(chaosTempleF1);

		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));
		inJunaRoom = new ZoneRequirement(junaRoom);

		abyssEntry = new Zone(new WorldPoint(4040, 4550, 0), new WorldPoint(4078, 4613, 0));
		inAbyssEntry = new ZoneRequirement(abyssEntry);

		notUsedSpadeOnFireRocks = new VarbitRequirement(10811, 0);
		notUsedChiselOnFireBrazier = new VarbitRequirement(10811, 1);

		notUsedSpadeOnEarthRocks = new VarbitRequirement(10812, 0);
		notUsedChiselOnEarthBrazier = new VarbitRequirement(10812, 1);

		notUsedChiselOnAirBrazier = new VarbitRequirement(10813, 0);
		notUsedChiselOnWaterBrazier = new VarbitRequirement(10814, 0);

		usedChiselOnAllBraziers = and(
			new VarbitRequirement(10811, 2),
			new VarbitRequirement(10812, 2),
			new VarbitRequirement(10813, 1),
			new VarbitRequirement(10814, 1)
		);

		abyssEntryF1 = new Zone(new WorldPoint(4058, 4552, 1), new WorldPoint(4070, 4558, 1));
		inAbyssEntryF1 = new ZoneRequirement(abyssEntryF1);

		abyssEntryF2 = new Zone(new WorldPoint(4055, 4546, 2), new WorldPoint(4072, 4556, 2));
		inAbyssEntryF2 = new ZoneRequirement(abyssEntryF2);

		notPlacedAirOrb = new VarbitRequirement(10816, 0);
		notPlacedWaterOrb = new VarbitRequirement(10817, 0);
		notPlacedEarthOrb = new VarbitRequirement(10818, 0);
		notPlacedFireOrb = new VarbitRequirement(10819, 0);

		placedAllOrbs = and(
			new VarbitRequirement(10816, 1),
			new VarbitRequirement(10817, 1),
			new VarbitRequirement(10818, 1),
			new VarbitRequirement(10819, 1)
		);

		// TODO: Make sure this is the right thing to check
		// 10824 0->1
		// 10825 0->8
		// 10820 4->3
		placedAirBlock = new VarbitRequirement(10821, 1);
		placedWaterBlock = new VarbitRequirement(10822, 1);
		placedEarthBlock = new VarbitRequirement(10823, 1);
		notPlacedFireBlock = new VarbitRequirement(10824, 0);

		noWeaponOrShieldEquipped = new ComplexRequirement("No weapon or shield equipped", new NoItemRequirement("", ItemSlots.WEAPON), new NoItemRequirement("", ItemSlots.SHIELD));

		airCavity = new Zone(new WorldPoint(4107, 5095, 0), new WorldPoint(4119, 5116, 0));
		inAirCavity = new ZoneRequirement(airCavity);
		earthCavity = new Zone(new WorldPoint(4142, 5090, 0), new WorldPoint(4152, 5105, 0));
		inEarthCavity = new ZoneRequirement(earthCavity);
		waterCavity = new Zone(new WorldPoint(4119, 5065, 0), new WorldPoint(4145, 5080, 0));
		inWaterCavity = new ZoneRequirement(waterCavity);

		placedAllBlocks = new VarbitRequirement(10825, 15);

		guthixianTemple = new Zone(new WorldPoint(4025, 4350, 0), new WorldPoint(4160, 4480, 0));
		inGuthixianTemple = new ZoneRequirement(guthixianTemple);


		jailCell = new Zone(new WorldPoint(2988, 3344, 0), new WorldPoint(2990, 3346, 0));
		inJailCell = new ZoneRequirement(jailCell);
	}

	public void setupSteps()
	{
		questPlaceholder = new DetailedQuestStep(this, "This is a very large quest, so it will be a while before a Quest Helper is made for it. Enjoy the quest if you do it yourself, it's one of the best ever made!");
		talkToIvy = new NpcStep(this, NpcID.IVY_SOPHISTA, new WorldPoint(2907, 3450, 0), "Talk to Ivy Sophista in Taverley south of the witch's house.");
		talkToIvy.addDialogStep("Yes.");

		goUpLadderNextToIvy = new ObjectStep(this, ObjectID.LADDER_53347, new WorldPoint(2915, 3450, 0), "Go up the ladder next to Ivy.");

		talkToThaerisk = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk.");
		// 15064 0->100

		// 10670 0->1

		// 10754 0->1 when someone walking downstairs
		killAssassins = new NpcStep(this, NpcID.ASSASSIN_13514, new WorldPoint(2909, 3449, 1), "Kill the assassins. " +
			"If one says 'Now!' they will fire a hard-hitting ranged attack if not prayed against. If one says 'Nothing personal, adventurer', " +
			"they will teleport to you and dragon claw spec you. Protect from Melee to be safe.", true);
		((NpcStep) killAssassins).addAlternateNpcs(NpcID.ASSASSIN_13515);

		talkToThaeriskAgain = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk again.");
		// 10755 0->1, thaerisk dialog checkpoint

		// 10809 1->0 after Thaerisk

		talkToLaunderer = new NpcStep(this, NpcID.KHAZARD_LAUNDERER, new WorldPoint(2564, 3172, 0), "Talk to the Khazard Launderer west of the Khazard Fight Arena.",
			coins.quantity(500).hideConditioned(paidLaunderer));
		talkToLaunderer.addTeleport(khazardTeleport);
		talkToLaunderer.addDialogStep("Would 500 coins change your mind?");

		// 10756 0->1 Launderer dialog checkpoint
		// 1->2 for given money
		// 2->3 when received shirt
		// 3->4 told to go to hunter expert

		talkToHuntingExpert = new NpcStep(this, NpcID.HUNTING_EXPERT_1504, new WorldPoint(2525, 2916, 0),
			"Talk to the Hunting Expert in his hut in the middle of the Feldip Hills hunting area.", knife, logs);
		talkToHuntingExpert.addTeleport(feldipHillsTeleport);
		talkToHuntingExpert.addDialogStep("Do you think you could help me with broavs?");

		setupTrap = new ObjectStep(this, ObjectID.PIT_53273, new WorldPoint(2499, 2910, 0), "Set up the trap west of the Hunting Expert.", knife, logs, mortMyreFungus);
		useFungusOnTrap = new ObjectStep(this, ObjectID.PIT_TRAP, new WorldPoint(2499, 2910, 0), "Use the mort myre fungus on the trap west of the Hunting Expert.", mortMyreFungus);
		waitForBroavToGetTrapped = new ObjectStep(this, ObjectID.BAITED_PIT_TRAP, new WorldPoint(2499, 2910, 0), "Wait for the broav to fall into the trap.");
		retrieveBroav = new ObjectStep(this, ObjectID.COLLAPSED_TRAP_53269, new WorldPoint(2499, 2910, 0), "Dismantle the trap to retrieve the broav.");

		returnBroavToHuntingExpert = new NpcStep(this, NpcID.HUNTING_EXPERT_1504, new WorldPoint(2525, 2916, 0),
			"Bring the unconscious broav to the Feldip Hills hunting expert.", unconsciousBroav);
		returnBroavToHuntingExpert.addDialogStep("Do you think you could train this broav for me?");

		goToBrokenTable = new DetailedQuestStep(this, new WorldPoint(2519, 3248, 0), "Go to the broken table in the middle of the khazard side of the gnome/khazard battlefield.", broav);
		goToBrokenTable.addTeleport(khazardTeleport);
		goToBrokenTable.setHighlightZone(nearTable);

		dropBroav = new DetailedQuestStep(this, "Drop your broav.", broav.highlighted());

		useDirtyShirtOnBroav = new NpcFollowerStep(this, NpcID.BROAV, "Use the dirty shirt on your broav.", dirtyShirt.highlighted());
		useDirtyShirtOnBroav.addIcon(ItemID.DIRTY_SHIRT);

		searchBrokenTable = new ObjectStep(this, NullObjectID.NULL_53889, new WorldPoint(2519, 3249, 0), "Search the broken table.");

		enterMovarioBase = new ObjectStep(this, ObjectID.TRAPDOOR_53279, new WorldPoint(2519, 3249, 0), "Enter movario's base under the broken table in the Khazard Battlefield.");

		// TODO: Update hardcoded 54117 to CHEST_54117
		claimRunes = new ObjectStep(this, 54117, new WorldPoint(4124, 4984, 0), "Search the open chest in the far north of the area for some runes.");

		// 4066 122878 -> 385022
		// 15064 0->100

		climbDownMovarioFirstRoom = new ObjectStep(this, ObjectID.STAIRS_53895, new WorldPoint(4117, 4973, 0), "Climb down the stairs to the north-west.");

		inspectDoor = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0), "Inspect the old battered door.");
		// 10757 0->1

		ObjectStep useFireRuneDoorInnerStep = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Use a fire rune on the old battered door.", fireRune.highlighted());
		useFireRuneDoorInnerStep.addIcon(ItemID.FIRE_RUNE);
			useFireRuneOnDoor = new PuzzleWrapperStep(this,
				useFireRuneDoorInnerStep,
			"Solve the electric door puzzle.");

		ObjectStep useAirRuneDoorInnerStep = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Use a air rune on the old battered door.", airRune.highlighted());
		useAirRuneDoorInnerStep.addIcon(ItemID.AIR_RUNE);
		useAirRuneOnDoor = new PuzzleWrapperStep(this, useAirRuneDoorInnerStep, "Solve the electric door puzzle.");

		ObjectStep useEarthRuneDoorInnerStep = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Use a earth rune on the old battered door.", earthRune.highlighted());
		useEarthRuneDoorInnerStep.addIcon(ItemID.EARTH_RUNE);
		useEarthRuneOnDoor = new PuzzleWrapperStep(this, useEarthRuneDoorInnerStep, "Solve the electric door puzzle.");

		ObjectStep useWaterRuneDoorInnerStep = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Use a water rune on the old battered door.", waterRune.highlighted());
		useWaterRuneDoorInnerStep.addIcon(ItemID.WATER_RUNE);
		useWaterRuneOnDoor = new PuzzleWrapperStep(this, useWaterRuneDoorInnerStep, "Solve the electric door puzzle.");

		ObjectStep useMindRuneDoorInnerStep = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Use a mind rune on the old battered door.", mindRune.highlighted());
		useMindRuneDoorInnerStep.addIcon(ItemID.MIND_RUNE);
		useMindRuneOnDoor = new PuzzleWrapperStep(this, useMindRuneDoorInnerStep, "Solve the electric door puzzle.");

		useRuneOnDoor = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Inspect the old battered door, and then use the rune that stylistically appears in the 'Restricted access' warning on the door.");
		useRuneOnDoor.addSubSteps(useFireRuneOnDoor, useAirRuneOnDoor, useEarthRuneOnDoor, useWaterRuneOnDoor, useMindRuneOnDoor);

		// Search
		searchBookcase1 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53914, new WorldPoint(4181, 4959, 0), "Search the east-facing bookcase in the east of the room."),
		"Solve the electric door puzzle.");
		searchBookcase2 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53915, new WorldPoint(4174, 4957, 0), "Search the west-facing bookcase in the west of the room."),
			"Solve the electric door puzzle.");
		searchBookcase3 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53916, new WorldPoint(4172, 4950, 0), "Search the southern bookcase in the west of the room."),
			"Solve the electric door puzzle.");
		searchBookcase4 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53917, new WorldPoint(4169, 4953, 0), "Search the most western bookcase on the north end of the west wall of the room."),
			"Solve the electric door puzzle.");
		searchBookcase5 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53918, new WorldPoint(4169, 4961, 0), "Search the most western bookcase on the south end of the west wall of the room."),
			"Solve the electric door puzzle.");
		searchBookcase6 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53919, new WorldPoint(4175, 4964, 0), "Search the northern bookcase in the room."),
			"Solve the electric door puzzle.");
		searchBookcase7 = new PuzzleWrapperStep(this,
			new ObjectStep(this, ObjectID.BOOKCASE_53920, new WorldPoint(4187, 4962, 0), "Search the eastern bookcase in the room."),
			"Solve the electric door puzzle.");


		enterDoorToLibrary = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0), "Enter the old battered door.");

		solveElectricityPuzzle = new DetailedQuestStep(this, "Solve the electricity puzzle.");
		solveElectricityPuzzle.addSubSteps(searchBookcase1, searchBookcase2, searchBookcase3, searchBookcase4, searchBookcase5, searchBookcase6, searchBookcase7);

		enterElectricDoor = new ObjectStep(this, NullObjectID.NULL_54108, new WorldPoint(4181, 4953, 0), "Enter the gate to the staircase.");
		searchStaircaseInLibrary = new ObjectStep(this, ObjectID.SPIRAL_STAIRCASE, new WorldPoint(4182, 4951, 0), "Right-click SEARCH the spiral staircase.");
		climbStaircaseInLibrary = new ObjectStep(this, ObjectID.SPIRAL_STAIRCASE, new WorldPoint(4182, 4951, 0), "Climb up the spiral staircase.");

		// Gone up staircase
		// 9653 14->15
		// 10793-10796 0->100

		searchDesk = new ObjectStep(this, ObjectID.DESK_53940, new WorldPoint(4178, 4955, 1), "Search the desk north-west of the staircase.");

		pickupWasteBasket = new ObjectStep(this, ObjectID.WASTEPAPER_BASKET, new WorldPoint(4177, 4955, 1), "Pick-up the waste-paper basket west of the desk.");
		searchWasteBasket = new DetailedQuestStep(this, "Search the waste-paper basket.", wastePaperBasket.highlighted());
		useKeyOnBookcase = new ObjectStep(this, ObjectID.BOOKCASE_53939, new WorldPoint(4172, 4954, 1), "Use the ruby key on the most north-west bookcase.", rubyKey.highlighted());
		useKeyOnBookcase.addIcon(ItemID.RUBY_KEY_29523);
		climbUpHiddenStaircase = new ObjectStep(this, ObjectID.STAIRS_53947, new WorldPoint(4173, 4956, 1), "Go up the stairs that've appeared.");
		searchBed = new ObjectStep(this, ObjectID.BED_53949, new WorldPoint(4179, 4954, 2), "Search the bed.");
		goDownToF1MovarioBase = new ObjectStep(this, ObjectID.STAIRS_53948, new WorldPoint(4173, 4956, 2), "Go back downstairs.");
		searchDesk.addSubSteps(goDownToF1MovarioBase);

		// 10771 3/2/1 for chances before failing weight door
		useKeyOnChest = new ObjectStep(this, ObjectID.BED_CHEST, new WorldPoint(4179, 4954, 2), "Use the ruby key on the bed chest.", rubyKey.highlighted());
		useKeyOnChest.addIcon(ItemID.RUBY_KEY_29523);
		searchChestForTraps = new ObjectStep(this, ObjectID.BED_CHEST_53951, new WorldPoint(4179, 4954, 2), "RIGHT-CLICK search the bed chest for traps.");
		getNotesFromChest = new ObjectStep(this, ObjectID.BED_CHEST_53952, new WorldPoint(4179, 4954, 2), "Take the second pair of notes from the bed chest.");
		((ObjectStep) getNotesFromChest).addAlternateObjects(ObjectID.BED_CHEST_53951);
		readNotes1 = new DetailedQuestStep(this, "Read the movario notes 1.", movariosNotesV1.highlighted());
		readNotes2 = new DetailedQuestStep(this, "Read the movario notes 2.", movariosNotesV2.highlighted());
		goDownFromHiddenRoom = new ObjectStep(this, ObjectID.STAIRS_53948, new WorldPoint(4173, 4956, 2), "Go back downstairs.");
		inspectPainting = new ObjectStep(this, ObjectID.PAINTING_53885, new WorldPoint(4179, 4948, 1), "Inspect the painting in the south of the room.");
		inspectPainting.addDialogStep("Pull the lever.");

		// 10670 0->1 when inspected painting

		// Pulled lever in painting
		// 10670 1->0
		// 10758 0->1
		solveWeightPuzzle = new WeightStep(this);

		// Taken fire runes
		// 10794 100->0

		// Death runes
		// 10795 100->0

		// Coal
		// 10793

		// Magic log
		// 107096

		goUpToThaeriskWithNotes = new ObjectStep(this, ObjectID.LADDER_53347, new WorldPoint(2915, 3450, 0), "Return to Thaerisk upstairs in Taverley with the notes.", movariosNotesV1, movariosNotesV2);
		talkToThaeriskWithNotes = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk.", movariosNotesV1, movariosNotesV2);

		goUpToThaeriskWithoutNotes = new ObjectStep(this, ObjectID.LADDER_53347, new WorldPoint(2915, 3450, 0), "Return to Thaerisk upstairs in Taverley");
		talkToThaeriskWithoutNotes = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk.");
		goUpToThaeriskWithNotes.addSubSteps(talkToThaeriskWithNotes, goUpToThaeriskWithoutNotes, talkToThaeriskWithoutNotes);

		// 9653 22->23
		// 10773 0->1
		// 10775 0->1
		// Now go to Idria near Seers' Village

		// Entered merc area
		// 9653 23->24
		// 4066 Varp: 385022 -> 33939454
		killMercenaries = new NpcStep(this, NpcID.MERCENARY_MAGE, new WorldPoint(2657, 3501, 0), "Kill the mercenaries just north of McGrubor's Wood.", true);
		((NpcStep) killMercenaries).addAlternateNpcs(NpcID.MERCENARY_AXEMAN, NpcID.MERCENARY_AXEMAN_13536);
		killMercenaries.addTeleport(camelotTeleport);
		talkToIdria = new NpcStep(this, NpcID.IDRIA_13542, new WorldPoint(2657, 3501, 0), "Talk to Idria just north of McGrubor's Wood.");
		// Killed mercenaries
		// quest 24->25
		// 10839 0->1
		// 10846 0->1
		// 10780 0->1
		// 10775 1->0

		// Idria moved to castle
		// 10773 1->2

		talkToAkrisae = new NpcStep(this, NpcID.AKRISAE, new WorldPoint(2989, 3342, 0), "Talk to Akrisae in the White Knights' Castle, on the ground floor on the east side.");
		talkToAkrisae.addTeleport(faladorTeleport);
		talkToAkrisaeForTeleport = new NpcStep(this, NpcID.AKRISAE, new WorldPoint(2989, 3342, 0), "Talk to Akrisae again to teleport to Draynor Village.");
		talkToAkrisaeForTeleport.addDialogStep("Yes.");
		useOrbOnShadyStranger = new NpcStep(this, NpcID.SHADY_STRANGER, new WorldPoint(3085, 3243, 0), "Use the orb on the shady stranger near Draynor Village bank.", teleorb.highlighted());
		useOrbOnShadyStranger.addIcon(ItemID.TELEORB);
		talkToAkrisaeAfterOrb = new NpcStep(this, NpcID.AKRISAE, new WorldPoint(2989, 3342, 0), "Return to Akrisae in the White Knights' Castle.",
			 lanternLens);
		talkToAkrisaeAfterOrb.addTeleport(faladorTeleport);
		// Stranger teleported
		// 32->33 quest state
		// 10778 0->1
		// 10800 1->0
		buySnapdragonSeed = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2989, 3342, 0), "Get a snapdragon seed from Thaerisk for free.");
		buySnapdragonSeed.addDialogSteps("Could I have that seed?", "Sounds good to me.");
		getSarimTeleport = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2989, 3342, 0), "Talk to Thaerisk to teleport to Port Sarim.", snapdragonSeed, lanternLens, coins.quantity(20));
		getSarimTeleport.addDialogSteps("Could you teleport me to Port Sarim?", "Yes.");
		talkToBetty = new NpcStep(this, NpcID.BETTY_5905, new WorldPoint(3014, 3258, 0), "Talk to Betty in Port Sarim's magic shop with a snapdragon seed and lantern lens.",
			snapdragonSeed, lanternLens, coins.quantity(20));
		talkToBetty.addDialogStep("Could you help me make some enriched snapdragon?");
		// Given snapdragon seed
		// 1537 0->2
		// quest state 35->36
		talkToBettyForDye = new NpcStep(this, NpcID.BETTY_5905, new WorldPoint(3014, 3258, 0), "Talk to Betty in Port Sarim's magic shop for some pink dye.", lanternLens, coins.quantity(20));
		talkToBettyForDye.addDialogSteps("About that enriched snapdragon...", "Sounds good.");
		usePinkDyeOnLanternLens = new DetailedQuestStep(this, "Use the pink dye on a lantern lens.", pinkDye.highlighted(), lanternLens.highlighted());
		standInDoorway = new DetailedQuestStep(this, new WorldPoint(3016, 3259, 0), "Stand in Betty's doorway and use the rose-tinted lens on the counter.");
		useLensOnCounter = new ObjectStep(this, NullObjectID.NULL_10812, new WorldPoint(3013, 3258, 0), "Stand in Betty's doorway and use the rose-tinted lens on the counter.", roseTintedLens.highlighted());
		useLensOnCounter.addIcon(ItemID.ROSE_TINTED_LENS);
		useLensOnCounter.addSubSteps(standInDoorway);
		// 1532 6->4

		// Used lens on desk
		// 1537 2->3, big seed on desk

		searchCounterForSeed = new ObjectStep(this, NullObjectID.NULL_10812, new WorldPoint(3013, 3258, 0), "Take the seed from Betty's counter.");
		talkToThaeriskWithSeed = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2989, 3342, 0), "Return to Thaerisk with the enhanced snapdragon seed.",
			enrichedSnapdragonSeed, dibber);
		talkToThaeriskWithSeed.addTeleport(faladorTeleport);

		goFromF0ToF1WhiteKnight = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2955, 3339, 0),
			"");
		goFromF1ToF2WhiteKnight = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2961, 3339, 1),
			"");
		goFromF2ToF3WhiteKnight = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2957, 3338, 2),
			"");
		plantSnapdragon = new ObjectStep(this, ObjectID.HERB_PATCH_53290, new WorldPoint(2962, 3338, 3),
			"Plant the enriched snapdragon seed in the herb patch on the top floor of the west side of the White Knights' Castle.", enrichedSnapdragonSeed.highlighted(), dibber);
		plantSnapdragon.addIcon(ItemID.ENRICHED_SNAPDRAGON_SEED);

		ConditionalStep goToF3WhiteKnight = new ConditionalStep(this, goFromF0ToF1WhiteKnight);
		goToF3WhiteKnight.addStep(inWhiteKnightsCastleF2, goFromF2ToF3WhiteKnight);
		goToF3WhiteKnight.addStep(inWhiteKnightsCastleF1, goFromF1ToF2WhiteKnight);

		goPlantSnapdragon = new ConditionalStep(this, goToF3WhiteKnight, "Plant the enriched snapdragon seed in the herb patch on the top floor of the west side of the White Knights' Castle.", enrichedSnapdragonSeed, dibber);
		goPlantSnapdragon.addSubSteps(plantSnapdragon);
		// Seed planted
		// quest state 38 -> 39
		// 10781 0->1 Snapdragon state?

		goFromF3ToF2WhiteKnight = new ObjectStep(this, ObjectID.STAIRCASE_24074, new WorldPoint(2958, 3338, 3),
			"Return to Idria downstairs near Thaerisk.");
		goFromF2ToF1WhiteKnight = new ObjectStep(this, ObjectID.STAIRCASE_24074, new WorldPoint(2960, 3339, 2),
			"Return to Idria downstairs near Thaerisk.");
		goFromF1ToF0WhiteKnight = new ObjectStep(this, ObjectID.STAIRCASE_24074, new WorldPoint(2955, 3338, 1),
			"Return to Idria downstairs near Thaerisk.");
		talkToIdriaAfterPlanting = new NpcStep(this, NpcID.IDRIA, new WorldPoint(2989, 3342, 0), "Return to Idria downstairs near Thaerisk.");
		talkToIdriaAfterPlanting.addSubSteps(goFromF3ToF2WhiteKnight, goFromF2ToF1WhiteKnight, goFromF1ToF0WhiteKnight);

		activeLunars = new ObjectStep(this, ObjectID.ALTAR_34771, new WorldPoint(2158, 3864, 0),
			"Activate the lunar spellbook for NPC Contact. Either use the altar on Lunar Isle, your POH altar, or your Magic Cape.", astralRune.quantity(4), cosmicRune.quantity(4), airRune.quantity(8));

		contactCyrisus = new DetailedQuestStep(this, "Use NPC Contact to talk to Cyrisus.", astralRune, cosmicRune, airRune.quantity(2));
		contactCyrisus.addSpellHighlight(LunarSpells.NPC_CONTACT);
		contactCyrisus.addWidgetHighlight(75, 47);
		contactTurael = new DetailedQuestStep(this, "Use NPC Contact to talk to Turael, or go talk to him in Burthorpe.", astralRune, cosmicRune, airRune.quantity(2));
		contactTurael.addSpellHighlight(LunarSpells.NPC_CONTACT);
		contactTurael.addWidgetHighlight(75, 22);
		contactTurael.addDialogStep("I need to talk to you about Lucien.");
		contactMazchna = new DetailedQuestStep(this, "Use NPC Contact to talk to Mazchna, or go talk to him in Canifis.", astralRune, cosmicRune, airRune.quantity(2));
		contactMazchna.addSpellHighlight(LunarSpells.NPC_CONTACT);
		contactMazchna.addWidgetHighlight(75, 25);
		contactMazchna.addDialogStep("I need to talk to you about Lucien.");
		contactDuradel = new DetailedQuestStep(this, "Use NPC Contact to talk to Duradel, or go talk to him in Shilo Village.", astralRune, cosmicRune, airRune.quantity(2));
		contactDuradel.addSpellHighlight(LunarSpells.NPC_CONTACT);
		contactDuradel.addWidgetHighlight(75, 37);
		contactDuradel.addDialogStep("I need to talk to you about Lucien.");
		// 5006 5->13
		// 3622 100->0
		// 10670 0->1

		// Contacted everyone
		// Quest from 40->41
		// 10781 1->2 Snapdragon state?
		harvestSnapdragon = new ObjectStep(this, ObjectID.HERBS_53292, new WorldPoint(2962, 3338, 3),
			"");
		goHarvestSnapdragon = new ConditionalStep(this, goToF3WhiteKnight, "Harvest the enriched snapdragon in the herb patch on the top floor of the west side of the White Knights' Castle.");
		goHarvestSnapdragon.addStep(inWhiteKnightsCastleF3, harvestSnapdragon);

		// quest 41->42
		// 10782 0->1
		// 10783 0->1
		// 10781 2->0 // Platch from snapdragon to empty

		goFromF3ToF2WhiteKnightThaerisk = new ObjectStep(this, ObjectID.STAIRCASE_24074, new WorldPoint(2958, 3338, 3),
			"Return to Thaerisk downstairs.");
		goFromF2ToF1WhiteKnightThaerisk = new ObjectStep(this, ObjectID.STAIRCASE_24074, new WorldPoint(2960, 3339, 2),
			"Return to Thaerisk downstairs.");
		goFromF1ToF0WhiteKnightThaerisk = new ObjectStep(this, ObjectID.STAIRCASE_24074, new WorldPoint(2955, 3338, 1),
			"Return to Thaerisk downstairs.");
		talkToThaeriskWithSnapdragon = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2989, 3342, 0), "Return to Thaerisk with the enriched snapdragon.",
			enrichedSnapdragon);
		talkToThaeriskWithSnapdragon.addSubSteps(goFromF3ToF2WhiteKnightThaerisk, goFromF2ToF1WhiteKnightThaerisk, goFromF1ToF0WhiteKnightThaerisk);
		// Told Thaerisk patch grown and have herb, 10843 0->1
		useSnapdragonOnSerum = new DetailedQuestStep(this, "Use the enriched snapdragon on the serum.", enrichedSnapdragon.highlighted(), truthSerum.highlighted());
		searchDrawersForCharcoalAndPapyrus = new ObjectStep(this, ObjectID.DRAWERS_53307, new WorldPoint(2990, 3335, 0), "Search the drawers in the south-east of the room.");
		((ObjectStep) searchDrawersForCharcoalAndPapyrus).addAlternateObjects(ObjectID.DRAWERS_53306);
		enterJailCell = new ObjectStep(this, ObjectID.CELL_GATE_53281, new WorldPoint(2988, 3343, 0), "Enter the cell just north of Thaerisk.");
		useSerumOnSpy = new NpcStep(this, NpcID.SHADY_STRANGER_13546, new WorldPoint(2989, 3344, 0), "Use the super truth serum on the shady stranger in the cell near Thaerisk.",
			superTruthSerum.highlighted(), charcoal, papyrus);
		talkToSpy = new NpcStep(this, NpcID.SHADY_STRANGER_13546, new WorldPoint(2989, 3344, 0), "Talk to the shady stranger.", charcoal, papyrus);
		giveSketchToIdria = new NpcStep(this, NpcID.IDRIA, new WorldPoint(2989, 3342, 0), "Use the sketch on Idria.", sketch.highlighted());
		talkToIdriaAfterSketch = new NpcStep(this, NpcID.IDRIA, new WorldPoint(2989, 3342, 0), "Use the sketch on Idria.");
		giveSketchToIdria.addSubSteps(talkToIdriaAfterSketch);
		// Given sketch
		// quest 420 -> 430
		// 10774 0->1
		talkToGhommal = new NpcStep(this, NpcID.GHOMMAL_13613, new WorldPoint(2878, 3546, 0), "Talk to Ghommal outside of the entrance to the Warriors' Guild.");
		talkToGhommal.addDialogStep("I need to talk to you about Lucien.");
		talkToHarrallak = new NpcStep(this, NpcID.HARRALLAK_MENAROUS_13615, new WorldPoint(2868, 3546, 0), "Talk to Harrallak Menarous in the entrance to the Warriors' Guild.");
		talkToHarrallak.addDialogStep("I need to talk to you about Lucien.");
		goToF1WarriorsGuild = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2839, 3537, 0), "Talk to Sloane in the middle of the first floor of the Warriors' Guild.");
		talkToSloane = new NpcStep(this, NpcID.SLOANE_13616, new WorldPoint(2856, 3552, 1), "Talk to Sloane in the middle of the first floor of the Warriors' Guild.");
		talkToSloane.addDialogStep("I need to talk to you about Lucien.");
		talkToSloane.addSubSteps(goToF1WarriorsGuild);

		// Recruited all Warriors' Guild:
		// 10790 0->1
		// Quest state 440->450
		// 10780 1->2
		// 10801 0->1
		// 10778 1->0

		talkToAkrisaeAfterRecruitment = new NpcStep(this, NpcID.AKRISAE, new WorldPoint(2989, 3342, 0),
			"Return to Akrisae in the White Knights' Castle, on the ground floor on the east side.");
		// 10838 0->1
		// 10826 0->1
		enterBlackKnightFortress = new ObjectStep(this, ObjectID.STURDY_DOOR, new WorldPoint(3016, 3514, 0), "Enter the Black Knights' Fortress. Akrisae will give you a one-off teleport there.",
			ironChainbody.equipped().hideConditioned(squallOutfit.alsoCheckBank(questBank)), bronzeMedHelm.equipped().hideConditioned(squallOutfit.alsoCheckBank(questBank)),
			squallOutfit.equipped().showConditioned(squallOutfit.alsoCheckBank(questBank)), unpoweredOrb.hideConditioned(hasCastChargeOrb), chargeOrbSpell.hideConditioned(hasCastChargeOrb));
		// 10962 0->1 when teleported
		enterBlackKnightFortress.addDialogStep("Yes.");
		pushHiddenWall = new ObjectStep(this, ObjectID.WALL_2341, new WorldPoint(3016, 3517, 0), "Push the wall to enter a secret room.");
		climbDownBlackKnightBasement = new ObjectStep(this, ObjectID.LADDER_25843, new WorldPoint(3016, 3519, 0), "Go down the ladder.");
		inspectCarvedTile = new ObjectStep(this, NullObjectID.NULL_54076, new WorldPoint(1870, 4237, 0), "Inspect the tile on the floor on the east side of the room.");
		castChargedOrbOnTile = new ObjectStep(this, NullObjectID.NULL_54076, new WorldPoint(1870, 4237, 0), "Cast one of the charge orb spells on the tile.", unpoweredOrb, chargeOrbSpell);
		castChargedOrbOnTile.addSpellHighlight(NormalSpells.CHARGE_WATER_ORB);
		castChargedOrbOnTile.addSpellHighlight(NormalSpells.CHARGE_AIR_ORB);
		castChargedOrbOnTile.addSpellHighlight(NormalSpells.CHARGE_EARTH_ORB);
		castChargedOrbOnTile.addSpellHighlight(NormalSpells.CHARGE_FIRE_ORB);
		// 10803 0->2
		enterCatacombs = new ObjectStep(this, NullObjectID.NULL_54076, new WorldPoint(1870, 4237, 0), "Enter the tile trapdoor.");
		// 15064 0->100
		// Quest progresses to 490
		jumpBridge = new ObjectStep(this, ObjectID.BRIDGE_53512, new WorldPoint(4112, 4697, 1), "Jump over the west bridge.");
		climbWallInCatacombs = new ObjectStep(this, ObjectID.WALL_53513, new WorldPoint(4134, 4725, 1), "Climb up the northern wall in the north-east area.");
		useWesternSolidDoor = new ObjectStep(this, ObjectID.SOLID_DOOR_53478, new WorldPoint(4096, 4791, 2), "Continue through the catacomb until you reach some solid doors in the north-west. " +
			"Enter the west door.");
		enterCatacombShortcut = new ObjectStep(this, ObjectID.SOLID_DOOR_53479, new WorldPoint(4117, 4689, 1), "Enter the solid door shortcut just to the north-east.");
		useWesternSolidDoor.addSubSteps(enterCatacombShortcut);
		enterNorthernSolidDoor = new ObjectStep(this, ObjectID.SOLID_DOOR, new WorldPoint(4104, 4799, 2), "Enter the most northern solid door.");
		searchWardrobeForEliteArmour = new ObjectStep(this, ObjectID.WARDROBE_53361, new WorldPoint(4119, 4857, 1), "Search the wardrobe to the north.",
			eliteHelm.equipped(), eliteBody.equipped(), eliteLegs.equipped());
		((ObjectStep) searchWardrobeForEliteArmour).addAlternateObjects(ObjectID.WARDROBE_53362);
		searchWardrobeForSquallRobes = new ObjectStep(this, ObjectID.WARDROBE_53372, new WorldPoint(4123, 4841, 1), "Search the wardrobe to the south.",
			eliteHelm.equipped(), eliteBody.equipped(), eliteLegs.equipped());
		((ObjectStep) searchWardrobeForSquallRobes).addAlternateObjects(ObjectID.WARDROBE_53374);
		// GUESS: Killed black knight for elite legs, 10932 0->1
		// Killed black knight for elite body, 10931 0->1
		// Killed black knight for elite helm, 10930 0->1
		killKnightsForEliteArmour = new NpcStep(this, NpcID.ELITE_BLACK_KNIGHT, new WorldPoint(4122, 4849, 1), "Kill elite black knights for their full armour set.",
			true, eliteHelm, eliteBody, eliteLegs);
		((NpcStep) killKnightsForEliteArmour).addAlternateNpcs(NpcID.ELITE_BLACK_KNIGHT_13464, NpcID.ELITE_BLACK_KNIGHT_13480, NpcID.ELITE_BLACK_KNIGHT_13481, NpcID.ELITE_BLACK_KNIGHT_13566);
		searchDeskForTeleorb = new ObjectStep(this, NullObjectID.NULL_54077, new WorldPoint(4119, 4844, 1), "Search the south table for a strange teleorb.");
		searchDeskForLobster = new ObjectStep(this, NullObjectID.NULL_54079, new WorldPoint(4124, 4851, 1), "Search the north-east table for some lobster and a restore potion." +
			" DON'T EAT OR DRINK EITHER.");
		searchDeskForLawAndDeathRune = new ObjectStep(this, NullObjectID.NULL_54080, new WorldPoint(4113, 4849, 1), "Search the west desk for a law and death rune.");
		searchKeyRack = new ObjectStep(this, NullObjectID.NULL_53430, new WorldPoint(4123, 4857, 1), "Search the key rack just east of the northern wardrobe.");
		leaveSolidDoor = new ObjectStep(this, ObjectID.SOLID_DOOR_53366, new WorldPoint(4113, 4841, 1), "Leave the room back to the catacombs.");

		equipSquallOrEliteArmour = new DetailedQuestStep(this, "Equip either a full set of elite black knight or the squall outfit.", eliteBlackKnightOrSquallOutfit.equipped());
		leaveSolidDoor = new ObjectStep(this, ObjectID.SOLID_DOOR_53366, new WorldPoint(4113, 4841, 1), "Return back through the solid door to the south.");
		openSilifsCell = new ObjectStep(this, ObjectID.GATE_53443, new WorldPoint(4132, 4791, 2), "Open Silif's cell door in the north-east of the area.", cellKey.highlighted());
		useLobsterOnSilif = new NpcStep(this, NpcID.SILIF_13524, new WorldPoint(4134, 4790, 2), "Use a lobster or other food on Silif.", lobster.highlighted());
		useLobsterOnSilif.addIcon(ItemID.LOBSTER);
		useRestoreOnSilif = new NpcStep(this, NpcID.SILIF_13524, new WorldPoint(4134, 4790, 2), "Use a restore potion on Silif.", restorePotion.highlighted());
		useRestoreOnSilif.addIcon(ItemID.RESTORE_POTION4);
		giveSilifEliteArmour = new NpcStep(this, NpcID.SILIF_13524, new WorldPoint(4134, 4790, 2), "Use the elite black armour on Silif.", eliteBlackKnightOutfit.highlighted());
		giveSilifEliteArmour.addIcon(ItemID.ELITE_BLACK_FULL_HELM);
		talkToSilifToFollow = new NpcStep(this, NpcID.SILIF_13521, new WorldPoint(4134, 4790, 2), "Talk to Silif until he follows you.");
		talkToSilifToFollow.addDialogStep("Let's go.");
		enterNorthernSolidDoorAgain = new ObjectStep(this, ObjectID.SOLID_DOOR, new WorldPoint(4104, 4799, 2), "Enter the most northern solid door again.");
		goNearMap = new DetailedQuestStep(this, new WorldPoint(4120, 4840, 1), "Stand near the map just west of the door until Silif talks about it.");
		climbUpCatacombLadder = new ObjectStep(this, ObjectID.LADDER_53370, new WorldPoint(4142, 4855, 1), "Climb up the ladder to the east, ready to fight Surok.",
			darkSquallHood.equipped(), darkSquallBody.equipped(), darkSquallLegs.equipped());
		talkToSilifAtMap = new NpcStep(this, NpcID.SILIF_13521, new WorldPoint(4142, 4855, 1), "Talk to Silif near the map until he gives you a teleorb.");
		defeatSurok = new NpcStep(this, NpcID.SUROK_MAGIS_13482, new WorldPoint(4145, 4850, 2), "Defeat Surok. Read the sidebar for more details.");

		defeatSurokSidebar = new NpcStep(this, NpcID.SUROK_MAGIS_13482, new WorldPoint(4145, 4850, 2), "Defeat Surok. You must use magic to hurt him. You cannot use powered staves.");
		defeatSurokSidebar.addText("He has three special attacks:");
		defeatSurokSidebar.addText("1. A henchman appears. Cast 'Weaken' on a 'Strong' one, or 'Bind' on a 'Agile' one. If they reach you you will take 40+ damage.");
		defeatSurokSidebar.addText("2. Surok throws an explosive orb. You must telegrab it, and then alch it, or you will take 45+ damage.");
		defeatSurokSidebar.addText("3. Surok casts a surge attack. Case the opposite elemental spell back. Cast Air for Earth, Water for Fire, Earth for Air, Fire for Water.");
		defeatSurokSidebar.addSubSteps(defeatSurok);

		plantOrbOnSurok = new NpcStep(this, NpcID.SUROK_MAGIS_13551, new WorldPoint(4145, 4850, 2), "Use the teleorb on Surok Magis.", silifTeleorb.highlighted());
		plantOrbOnSurok.addIcon(ItemID.TELEORB_29537);

		// Planted orb:
		// 8653 597 -> 600
		// 10780 0->4
		// 10783 1->0
		// 10801 1->0
		// 10826 1->0
		// 10670 1->0

		talkToAkrisaeAfterSurok = new NpcStep(this, NpcID.AKRISAE, new WorldPoint(2989, 3342, 0),
			"Return to Akrisae in the White Knights' Castle, on the ground floor on the east side.", strangeTeleorb);
		// Ready for cell. Will be swapped. Can grab the strange teleorb once teleported in.
		// 610->620
		talkToSilifForRobes = new NpcStep(this, NpcID.SILIF_13520, new WorldPoint(2989, 3338, 0), "Talk to Silif near Akrisae for the dark squall robes.");
		enterCellWithRobesOn = new ObjectStep(this, ObjectID.CELL_GATE_53281, new WorldPoint(2988, 3343, 0), "Enter the cell near Akrisae.", darkSquallHood.equipped(),
			darkSquallBody.equipped(), darkSquallLegs.equipped());
		enterCellWithRobesOn.addDialogStep("I'm ready.");
		enterCellWithRobesOn.addSubSteps(talkToSilifForRobes);
		// Quest state 620->630
		// 10778 0->2
		// 10774 1->0
		goDownForOrbAndRunes = new ObjectStep(this, ObjectID.LADDER_53371, new WorldPoint(4142, 4855, 2),
			"Climb down the ladder to retrieve a strange teleorb and runes.", squallOutfit.equipped());
		takeStrangeTeleorb = new ObjectStep(this, NullObjectID.NULL_54077, new WorldPoint(4119, 4844, 1), "Search the south table for a strange teleorb.", squallOutfit.equipped());
		takeRunes = new ObjectStep(this, NullObjectID.NULL_54080, new WorldPoint(4113, 4849, 1), "Search the west desk for a law and death rune.", squallOutfit.equipped());
		// If not got runes, and no runes on table, then tell player to get runes
		goUpToUseTeleorb = new ObjectStep(this, ObjectID.LADDER_53370, new WorldPoint(4142, 4855, 1), "Climb back up the ladder to the east.",
			strangeTeleorb, lawRune, deathRune, squallOutfit);
		activateStrangeTeleorb = new DetailedQuestStep(this, "Activate the strange teleorb. Make sure your law and death rune are not in a rune pouch.", strangeTeleorb.highlighted());
		getRunes = new DetailedQuestStep(this, "Get a death rune and a law rune.", lawRune, deathRune);
		standAtTeleportSpot = new DetailedQuestStep(this, new WorldPoint(4136, 4849, 2), "Stand in the middle of the room.");
		standAtTeleportSpot.setHighlightZone(teleportSpot);
		climbIceWall = new ObjectStep(this, ObjectID.ICE_WALL, new WorldPoint(2942, 3822, 0), "Climb the ice wall in the north-east, next to the chaos temple.");
		jumpToLedge = new ObjectStep(this, ObjectID.LEDGE_53288, new WorldPoint(2943, 3822, 1), "Jump across the ledge, and watch the following cutscene.");
		talkToIdriaAfterChapel = new NpcStep(this, NpcID.IDRIA, new WorldPoint(2989, 3342, 0), "Return to Idria in Falador Castle.");
		// Talked to Idria
		// 10809 0->2
		// Quest 680->690

		teleportToJuna = new DetailedQuestStep(this, "Teleport to the Tears of Guthix using a games necklace, or travel there through the Lumbridge Swamp.",
			squallOutfit, litSapphireLantern, meleeGear, rangedGear, magicGear);
		teleportToJuna.addTeleport(gamesNecklace);
		teleportToJuna.addDialogStep("Tears of Guthix.");
		talkToMovario = new NpcStep(this, NpcID.MOVARIO_13567, new WorldPoint(3229, 9528, 2),
			"Talk to Movario in the north-west of the area.", darkSquallHood.equipped(), darkSquallBody.equipped(), darkSquallLegs.equipped());
		useLitSapphireLanternOnLightCreature = new NpcStep(this, NpcID.LIGHT_CREATURE_5435, new WorldPoint(3228, 9525, 2),
			"Use a lit sapphire lantern on one of the light creatures to descend into the abyss.", true, litSapphireLantern.highlighted());
		useLitSapphireLanternOnLightCreature.addDialogStep("Travel into the chasm.");
		useLitSapphireLanternOnLightCreature.addIcon(ItemID.SAPPHIRE_LANTERN_4702);
		// Started going down:
		// 10820 0->2
		// Once down:
		// 9653 710->740
		// 10827 0->1
		// 4066 VARP 56090622->64479230

		searchRemainsForSpade = new ObjectStep(this, ObjectID.SKELETAL_REMAINS_53810, new WorldPoint(4070, 4605, 0), "Search the remains to the north-east for a spade.");
		searchRemainsForHammerAndChisel = new ObjectStep(this, ObjectID.SKELETAL_REMAINS, new WorldPoint(4067, 4600, 0), "Search the remains in the middle of the area for a hammer and chisel.");

		useSpadeOnFireRocks = new ObjectStep(this, ObjectID.ROCKS_53727, new WorldPoint(4072, 4604, 0), "Use a spade on the rocks in the north-east of the area.", spade.highlighted());
		useSpadeOnFireRocks.addIcon(ItemID.SPADE);
		useSpadeOnFireRocks.addDialogStep("Yes.");

		useChiselOnFireBrazier = new ObjectStep(this, ObjectID.BRAZIER_53728, new WorldPoint(4072, 4604, 0), "Use a chisel on the brazier in the north-east of the area.", chisel.highlighted());
		useChiselOnFireBrazier.addIcon(ItemID.CHISEL);
		useChiselOnFireBrazier.addDialogStep("Yes.");

		useSpadeOnEarthRocks = new ObjectStep(this, ObjectID.ROCKS_53731, new WorldPoint(4056, 4602, 0), "Use a spade on the rocks in the north-west of the area.", spade.highlighted());
		useSpadeOnEarthRocks.addIcon(ItemID.SPADE);
		useSpadeOnEarthRocks.addDialogStep("Yes.");
		useChiselOnEarthBrazier = new ObjectStep(this, ObjectID.BRAZIER_53732, new WorldPoint(4056, 4602, 0), "Use a chisel on the north-west brazier.", chisel.highlighted(), hammer);
		useChiselOnEarthBrazier.addIcon(ItemID.CHISEL);
		useChiselOnEarthBrazier.addDialogStep("Yes.");
		useChiselOnAirBrazier = new ObjectStep(this, ObjectID.BRAZIER_53819, new WorldPoint(4053, 4589, 0), "Use a chisel on the south-west brazier.", chisel.highlighted(), hammer);
		useChiselOnAirBrazier.addIcon(ItemID.CHISEL);
		useChiselOnAirBrazier.addDialogStep("Yes.");
		useChiselOnWaterBrazier = new ObjectStep(this, ObjectID.BRAZIER_53820, new WorldPoint(4073, 4588, 0), "Use a chisel on the south-east brazier.", chisel.highlighted(), hammer);
		useChiselOnWaterBrazier.addIcon(ItemID.CHISEL);
		useChiselOnWaterBrazier.addDialogStep("Yes.");
		useOrbOnFireRecessedBlock = new ObjectStep(this, ObjectID.RECESSED_BLOCK, new WorldPoint(4062, 4554, 0),
			"Use the fire orb on the recessed block just next to the middle cavity to the far south, with the fire symbol next to it.", fireOrb.highlighted());
		useOrbOnFireRecessedBlock.addIcon(ItemID.FIRE_ORB);

		useOrbOnWaterRecessedBlock = new ObjectStep(this, ObjectID.RECESSED_BLOCK_53618, new WorldPoint(4063, 4548, 2),
			"Use the water orb on the recessed block just to the south, in the skeleton's mouth.", waterOrb.highlighted());
		useOrbOnWaterRecessedBlock.addIcon(ItemID.WATER_ORB);

		useOrbOnAirRecessedBlock = new ObjectStep(this, ObjectID.RECESSED_BLOCK_53617, new WorldPoint(4068, 4557, 0),
			"Use the air orb on the recessed block just next to the east cavity to the far south, with the air symbol next to it.", airOrb.highlighted());
		useOrbOnAirRecessedBlock.addIcon(ItemID.AIR_ORB);

		climbWallNextToSkullF0ToF1 = new ObjectStep(this, ObjectID.WALL_53623, new WorldPoint(4061, 4554, 0), "Climb up the walls to the south.");
		climbWallNextToSkullF1ToF2 = new ObjectStep(this, ObjectID.WALL_53624, new WorldPoint(4060, 4552, 1), "Climb up the walls to the south.");

		climbWallNextToSkullF0ToF1.addSubSteps(climbWallNextToSkullF1ToF2);

		useOrbOnEarthRecessedBlock = new ObjectStep(this, ObjectID.RECESSED_BLOCK_53616, new WorldPoint(4056, 4557, 0),
			"Use the earth orb on the recessed block just next to the west cavity to the far south, with the earth symbol next to it.", earthOrb.highlighted());
		useOrbOnEarthRecessedBlock.addIcon(ItemID.EARTH_ORB);
		climbDownFromSkullF2ToF1 = new ObjectStep(this, ObjectID.WALL_53676, new WorldPoint(4060, 4553, 2), "Climb back down.");
		climbDownFromSkullF1ToF0 = new ObjectStep(this, ObjectID.WALL_53676, new WorldPoint(4061, 4555, 1), "Climb back down.");
		climbDownFromSkullF2ToF1.addSubSteps(climbDownFromSkullF1ToF0);
		// Placed water orb, 10820 2->4
		// 10820 is the top recessed state
		// 0 is can climb through
		// 1 is both water block and fire block
		// 2 is just water block in
		// 3 is just fire block in
		// 4 is neither block in

		useFireBlockOnRecess = new ObjectStep(this, NullObjectID.NULL_54082, new WorldPoint(4063, 4548, 2),
			"Use the fire block on the recessed block just to the south, in the skeleton's mouth.", fireBlock.highlighted());
		useFireBlockOnRecess.addIcon(ItemID.FIRE_BLOCK);
		// Placed fire block in recess
		// 10824 0->1
		// 10825 0->8
		// 10820 4->3

		// null 54082
		// real 53619

		enterWestCavity = new ObjectStep(this, NullObjectID.NULL_53634, new WorldPoint(4057, 4557, 0), "Enter the western cavity.", noWeaponOrShieldEquipped);
		enterMiddleCavity = new ObjectStep(this, NullObjectID.NULL_53634, new WorldPoint(4063, 4554, 0), "Enter the middle cavity.", noWeaponOrShieldEquipped);
		enterEastCavity = new ObjectStep(this, NullObjectID.NULL_53634, new WorldPoint(4069, 4557, 0), "Enter the eastern cavity.", noWeaponOrShieldEquipped);

		useEarthBlockOnRecess = new ObjectStep(this, ObjectID.MECHANISM, new WorldPoint(4147, 5094, 0), "Use the earth block on the recess.", earthBlock.highlighted());
		useEarthBlockOnRecess.addIcon(ItemID.EARTH_BLOCK);

		leaveEarthRecess = new ObjectStep(this, ObjectID.HOLE_53831, new WorldPoint(4143, 5103, 0), "Return back to the main area.");

		useAirBlockOnRecess = new ObjectStep(this, ObjectID.MECHANISM_53629, new WorldPoint(4113, 5098, 0), "Use the air block on the recess.", airBlock.highlighted());
		useAirBlockOnRecess.addIcon(ItemID.AIR_BLOCK);

		leaveAirRecess = new ObjectStep(this, ObjectID.HOLE_53832, new WorldPoint(4109, 5113, 0), "Return back to the main area.");

		useWaterBlockOnRecess = new ObjectStep(this, ObjectID.MECHANISM_53627, new WorldPoint(4137, 5071, 0), "Use the water block on the recess.", waterBlock.highlighted());
		useWaterBlockOnRecess.addIcon(ItemID.WATER_BLOCK);

		leaveWaterRecess = new ObjectStep(this, ObjectID.HOLE_53830, new WorldPoint(4126, 5081, 0), "Return back to the main area.");

		climbUpToCubeF0ToF1 = new ObjectStep(this, ObjectID.WALL_53623, new WorldPoint(4061, 4554, 0), "Climb up the walls to the south.");
		climbUpToCubeF1ToF2 = new ObjectStep(this, ObjectID.WALL_53624, new WorldPoint(4060, 4552, 1), "Climb up the walls to the south.");
		climbUpToCubeF0ToF1.addSubSteps(climbUpToCubeF1ToF2);

		touchCube = new ObjectStep(this, ObjectID.STONE_CUBE_53693, new WorldPoint(4063, 4552, 2), "Inspect the stone cube.");
		enterSkull = new ObjectStep(this, NullObjectID.NULL_54082, new WorldPoint(4063, 4548, 2),
			"Enter the skull's mouth to the south.");

		// Entered herblore space:
		// 10860 0->4 ATTACK, NW
		// 10861 0->2 ENERGY, W
		// 10862 0->6 DEFENCE, SW
		// 10863 0->10 HUNTER, SSW

		// 10864 0->7 COMBAT NE
		// 10865 0->9 PRAYER, E
		// 10866 0->1 AGILITY, SE
		// 10867 0->13 BALANCE, SEE

		// loot order
		// HUNTER, ATTACK, AGILITY, ENERGY

//		AGILITY_DOLMEN = 29539;
//		ENERGY_DOLMEN = 29540;
//		RESTORATION_DOLMEN = 29541;
//		ATTACK_DOLMEN = 29542;
//		STRENGTH_DOLMEN = 29543;
//		DEFENCE_DOLMEN = 29544;
//		COMBAT_DOLMEN = 29545;
//		RANGED_DOLMEN = 29546;
//		PRAYER_DOLMEN = 29547;
//		HUNTER_DOLMEN = 29548;
//		FISHING_DOLMEN = 29549;
//		MAGIC_DOLMEN = 29550;
//		BALANCE_DOLMEN = 29551;

		// 10868 0->1

		// Tick later
		// 10827 1->2

		//     +4,-18
		// 54, 58, 40, 42, 51, 52, 44, 48

		//         1, 3, 12, 13

		getPouch = new ObjectStep(this, NullObjectID.NULL_54088, new WorldPoint(4078, 4441, 0),"Search the skeleton north of the stone table.");
		castBloomToFillPouch = new DetailedQuestStep(this, new WorldPoint(4091, 4439, 0), "Cast bloom near any of the roots for items to fill your druid pouch with.", silverSickleB.highlighted());
		usePouchOnDruid = new NpcStep(this, NpcID.DRUID_SPIRIT, new WorldPoint(4078, 4439, 0), "Use the druid pouch on nearby druid spirits for herblore ingredients.", true);
		((NpcStep) usePouchOnDruid).addAlternateNpcs(NpcID.DRUID_SPIRIT_13576);
		((NpcStep) usePouchOnDruid).setMaxRoamRange(200);
		// 10933 increments with each druid step
		// ATTACK DOLMEN 0->1, kebbit tooth + avantoe (hunter)
		// ENERGY DOLMEN 1->2 guam + eye of newt (attack)
		// 2->3 toadflax/toads legs (agility)
		// 3->4 harralander/choc dust (energy potion)
		// 4->5 balance potion gredients
		// 5->6 harrlander/goat horn (combat potion)
		// 6->7 whiteberries/ranarr (defence potion)
		// 7->8 ranarr/snape grass (prayer potion)

		// 2nd run through
		// 0->1 tarromin/limpwurt (strength)
		// 1->2 harralander/choc dust (Energy potion)
		// 2->3 lant/potato cactus (magic potion)
		// 3->4 guam/newt (att potion)
		// 4->5 balance potion stuff
		// 5->6 ranarr weed/white berries (defence)
		// 6->7 ranarr/snape (prayer)
		// 7->8 avantoe/snape grass (fishing potion)

		// Altars:
		// Energy
		// Attack
		// Prayer
		// Strength
		// Defence
		// Magic
		// Balance

		// attack potion eye of newt used
		// 0->34
		// guam used, 34->35

		/////////////////////////

		// 1st run
		// DOLMEN: ATTACK, ENERGY, DEFENCE, HUNTER, COMBAT,  PRAYER, AGILITY, BALANCE
		// POTION: HUNTER, ATTACK, AGILITY, ENERGY, BALANCE, COMBAT, DEFENCE, PRAYER

		// 2nd run
		// DOLMEN: ENERGY, ATTACK, PRAYER, STRENGTH, DEFENCE, FISHING, MAGIC, BALANCE
		// POTION: STRENGTH, ENERGY, MAGIC, ATTACK, BALANCE, DEFENCE, PRAYER, FISHING

		// 4th, 1st, 7th, 2nd, 8th, 5th, 3rd, 6th


		////////////////////////

		// Hunter altar avantoe on,
		// 10924 0->1
		// used the tooth, 10924 1->3
		// Check variables for each altar
		// Update text for altar + item requirements
		//
		// Placed

		// Energy went 1->3
		// Defence went 1->35
		// Hunter went 1->3
		// Combat went 1->3

		// Balance potion: 10928
		// harralander 0->1
		// Silver dust 1->5
		// Spiders eggs 5->7
		// Garlic 7->15

		// Attack dolmen placed:
		// 10829 0->1
		// 10828 0->1

		// Agility domen placed:
		// 10835 0->1
		// 10828 1->2

		// Balance dolmen placed:
		// 10836 0->1

		// Combat dolmen:
		// 10833 0->1

		// Energy dolmen:
		// 10830 0->1

		// Defence:
		// 10831 0->1

		// Prayer:
		// 10834 0->1

		// Hunter:
		// 10832 0->1

		// TODO: Highlight each specific dolmen needed, and direct player to correct statue if missing a dolmen
		herblorePuzzle = new HerblorePuzzle(this, new DetailedQuestStep(this, "Broken step state."));

		// Placed all dolmens:
		// 10815 1->0 // TODO: Make note if appears in future run-throughs
		// 9653 770->780

		approachStoneOfJas = new ObjectStep(this, ObjectID.STONE_OF_JAS_53824, new WorldPoint(4139, 4383, 0), "Head through the now open entrance to the south and approach the Stone of Jas, ready to fight.");
		fightBalanceElemental = new NpcStep(this, NpcID.BALANCE_ELEMENTAL, new WorldPoint(4139, 4383, 0), "Fight the Balance Elemental. You need all 3 combat styles to defeat it.");
		fightBalanceElemental.addText("If you pray incorrectly against the first new style of attack, your stats will be majorly drained.");
		fightBalanceElemental.addText("As its health gets lower, it starts to attack faster.");
		fightBalanceElemental.addText(" When it has a fire head, it attacks with ranged.");
		fightBalanceElemental.addText("When it has an air head, it attacks with melee.");
		fightBalanceElemental.addText("When it has a water head, it attacks with magic.");
		((NpcStep) fightBalanceElemental).addAlternateNpcs(NpcID.BALANCE_ELEMENTAL_13529, NpcID.BALANCE_ELEMENTAL_13530);

		touchStone = new ObjectStep(this, ObjectID.STONE_OF_JAS, new WorldPoint(4139, 4383, 0), "Touch the Stone of Jas.");
		talkToMovarioAtStone = new NpcStep(this, NpcID.MOVARIO_13568, new WorldPoint(4133, 4384, 0), "Talk to Movario.");
		// 10773 2->0
		fightTormentedDemons = new NpcStep(this, NpcID.TORMENTED_DEMON, new WorldPoint(4139, 4383, 0), "Defeat the Tormented Demons. Your stats are 255 each.", true);
		((NpcStep) fightTormentedDemons).addAlternateNpcs(NpcID.TORMENTED_DEMON_13600, NpcID.TORMENTED_DEMON_13601, NpcID.TORMENTED_DEMON_13602, NpcID.TORMENTED_DEMON_13603,
			NpcID.TORMENTED_DEMON_13604, NpcID.TORMENTED_DEMON_13605, NpcID.TORMENTED_DEMON_13606);

		teleportWithIdria = new NpcStep(this, NpcID.IDRIA_13582, new WorldPoint(4133, 4381, 0), "Talk to Idria to teleport back to Falador Castle.");
		finishQuest = new NpcStep(this, NpcID.AKRISAE, new WorldPoint(2989, 3342, 0),
			"Return to Akrisae in the White Knights' Castle, on the ground floor on the east side, to finish the quest!");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			knife, coins.quantity(500), dibber, lanternLens, unpoweredOrb, bronzeMedHelm, ironChainbody, sapphireLantern,
			astralRune.quantity(4), cosmicRune.quantity(4), airRune.quantity(8), chargeOrbSpell, meleeGear, magicGear,
			rangedGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			khazardTeleport.quantity(2),
			feldipHillsTeleport,
			burthorpeTeleport,
			food
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("2 Assassins (level 167)",
			"2 Mercenary axemen (level 131)",
			"Mercenary mage (level 112)",
			"3 Elite Black Knights (level 138)",
			"Surok Magis (level 265)",
			"Balance Elemental (level 524)",
			"2 Tormented Demons (level 450)");
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
			new ExperienceReward(Skill.THIEVING, 80000),
			new ExperienceReward(Skill.FARMING, 75000),
			new ExperienceReward(Skill.HERBLORE, 75000),
			new ExperienceReward(Skill.HUNTER, 50000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Elite black knight armour", ItemID.ELITE_BLACK_PLATEBODY),
			new ItemReward("Dark squall robes", ItemID.DARK_SQUALL_ROBE_TOP),
			new ItemReward("death runes from Movario's base", ItemID.DEATH_RUNE, 100),
			new ItemReward("dire runes from Movario's base", ItemID.FIRE_RUNE, 100),
			new ItemReward("coal from Movario's base", ItemID.COAL, 100),
			new ItemReward("magic logs from Movario's base", ItemID.MAGIC_LOGS, 100),
			new ItemReward("Duradel's notes, obtainable from Kuradel", ItemID.DURADELS_NOTES)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Tormented Demons in the Ancient Guthixian Temple"),
			new UnlockReward("Access to Black Knight Catacombs")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", List.of(talkToIvy, goUpLadderNextToIvy, talkToThaerisk, killAssassins, talkToThaeriskAgain), List.of(meleeGear), List.of(antipoison, food)));
		allSteps.add(new PanelDetails("Investigating", List.of(talkToLaunderer, talkToHuntingExpert, setupTrap, useFungusOnTrap, waitForBroavToGetTrapped, retrieveBroav, returnBroavToHuntingExpert,
			goToBrokenTable, dropBroav, useDirtyShirtOnBroav, searchBrokenTable),
			List.of(coins.quantity(500).hideConditioned(paidLaunderer), knife, logs),
			List.of(khazardTeleport.quantity(2), feldipHillsTeleport)));
		allSteps.add(new PanelDetails("Movario's Base", List.of(enterMovarioBase, climbDownMovarioFirstRoom, inspectDoor, useRuneOnDoor, enterDoorToLibrary, solveElectricityPuzzle, enterElectricDoor,
			searchStaircaseInLibrary, climbStaircaseInLibrary, searchDesk, pickupWasteBasket, searchWasteBasket, useKeyOnBookcase, climbUpHiddenStaircase, searchBed, useKeyOnChest, searchChestForTraps,
			getNotesFromChest, readNotes1, readNotes2, goDownFromHiddenRoom, inspectPainting),
			airRune, waterRune, earthRune, fireRune, mindRune));
		allSteps.add(new PanelDetails("Weight puzzle", solveWeightPuzzle.getDisplaySteps()));
		allSteps.add(new PanelDetails("United Front", List.of(goUpToThaeriskWithNotes, killMercenaries, talkToIdria, talkToAkrisae, talkToAkrisaeForTeleport,
			useOrbOnShadyStranger, talkToAkrisaeAfterOrb, buySnapdragonSeed, getSarimTeleport, talkToBetty, talkToBettyForDye, usePinkDyeOnLanternLens, useLensOnCounter, searchCounterForSeed,
			talkToThaeriskWithSeed, goPlantSnapdragon, talkToIdriaAfterPlanting, activeLunars, contactCyrisus, contactTurael, contactMazchna, contactDuradel, goHarvestSnapdragon, talkToThaeriskWithSnapdragon,
			useSnapdragonOnSerum, searchDrawersForCharcoalAndPapyrus, enterJailCell, useSerumOnSpy, talkToSpy, giveSketchToIdria, talkToGhommal, talkToHarrallak, talkToSloane),
			List.of(lunarSpellbook, lanternLens, dibber, astralRune.quantity(4), cosmicRune.quantity(4), airRune.quantity(8)), List.of(burthorpeTeleport)));
		allSteps.add(new PanelDetails("Infiltration", List.of(talkToAkrisaeAfterRecruitment, enterBlackKnightFortress, pushHiddenWall, climbDownBlackKnightBasement, inspectCarvedTile,
			castChargedOrbOnTile, enterCatacombs, jumpBridge, climbWallInCatacombs, useWesternSolidDoor, enterNorthernSolidDoor, killKnightsForEliteArmour, searchWardrobeForEliteArmour,
			searchKeyRack, searchWardrobeForSquallRobes, searchDeskForTeleorb, searchDeskForLawAndDeathRune, searchDeskForLobster, leaveSolidDoor, openSilifsCell, useLobsterOnSilif,
			useRestoreOnSilif, giveSilifEliteArmour, talkToSilifToFollow, enterNorthernSolidDoorAgain, goNearMap, talkToSilifAtMap, climbUpCatacombLadder, defeatSurokSidebar, plantOrbOnSurok,
			talkToAkrisaeAfterSurok),
			List.of(bronzeMedHelm, ironChainbody, magicGear, meleeGear, unpoweredOrb, chargeOrbSpell), List.of(bindRunes, weakenRunes, alchRunes, telegrabRunes, food, prayerPotions)));
		allSteps.add(new PanelDetails("Confrontation", List.of(enterCellWithRobesOn, goDownForOrbAndRunes, takeRunes, takeStrangeTeleorb, goUpToUseTeleorb,
			standAtTeleportSpot, activateStrangeTeleorb, climbIceWall, jumpToLedge)));
		allSteps.add(new PanelDetails("???", List.of(talkToIdriaAfterChapel, teleportToJuna, talkToMovario, useLitSapphireLanternOnLightCreature, searchRemainsForSpade,
			searchRemainsForHammerAndChisel, useSpadeOnFireRocks, useChiselOnFireBrazier, useSpadeOnEarthRocks, useChiselOnEarthBrazier, useChiselOnAirBrazier, useChiselOnWaterBrazier,
			useOrbOnAirRecessedBlock, useOrbOnFireRecessedBlock, useOrbOnEarthRecessedBlock, climbWallNextToSkullF0ToF1, useOrbOnWaterRecessedBlock, useFireBlockOnRecess,
			 climbDownFromSkullF2ToF1, enterWestCavity, useAirBlockOnRecess, leaveAirRecess, enterMiddleCavity, useEarthBlockOnRecess, leaveEarthRecess, enterEastCavity, useWaterBlockOnRecess,
				leaveWaterRecess, climbUpToCubeF0ToF1, touchCube, enterSkull),
			List.of(squallOutfit, litSapphireLantern, meleeGear, rangedGear, magicGear),
			List.of(gamesNecklace, food, prayerPotions)));

		List<QuestStep> templeSteps = new ArrayList<>(List.of(getPouch, usePouchOnDruid));
		templeSteps.addAll(List.of(herblorePuzzle.getSidebarSteps()));
		templeSteps.addAll(List.of(approachStoneOfJas, fightBalanceElemental, touchStone, talkToMovarioAtStone, fightTormentedDemons, teleportWithIdria, finishQuest));

		allSteps.add(new PanelDetails("The Temple", templeSteps,
			List.of(litSapphireLantern, meleeGear, rangedGear, magicGear),
			List.of(gamesNecklace)));
		return allSteps;
	}
}
