/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.thefinaldawn;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;

import java.util.*;

import com.questhelper.steps.tools.QuestPerspective;
import com.questhelper.steps.widget.WidgetHighlight;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.*;
import net.runelite.client.eventbus.Subscribe;

import static com.questhelper.requirements.util.LogicHelper.*;

/**
 * The quest guide for the "The Final Dawn" OSRS quest
 */
public class TheFinalDawn extends BasicQuestHelper
{
	ItemRequirement emissaryRobesEquipped, emissaryRobes, bone, rangedGear;

	ItemRequirement combatGear, combatWeapon, food, prayerPotions, whistle, pendant, pendantToTwilight, civitasTeleport;
	FreeInventorySlotRequirement freeInvSlots4, freeInvSlot1;

	ItemRequirement drawerKey, canvasPiece, emissaryScroll, potatoes, knife, coinPurse, coinPurseFullOrEmpty, branch, coinPurseWithSand, coinPurseEmpty,
			emptySack, makeshiftBlackjack;
	ItemRequirement steamforgedBrew, dwarvenStout, beer, emptyGlass, wizardsMindBomb, keystoneFragment, essence, roots, kindling, knifeBlade, stoneTablet;

	QuestStep startQuest, goToTempleWithAtes, goToTempleFromSalvager, goToTempleFromGorge, searchChestForEmissaryRobes, enterTwilightTemple, goDownStairsTemple, enterBackroom,
			searchBed,	openDrawers, openDrawers2;
	DetailedQuestStep useCanvasPieceOnPicture, enterPassage, pickBlueChest, fightEnforcer, pickUpEmissaryScroll, readEmissaryScroll, talkToQueen,
	climbStairsF0ToF1Palace, climbStairsF1ToF2Palace;
	QuestStep openDoorWithGusCode;
	QuestStep talkToCaptainVibia, inspectWindow, giveBonesOrMeatToDog, enterDoorCode, takePotato, removePotatoesFromSack, takeKnife, takeCoinPurse,
			emptyCoinPurse, goToF1Hideout, goDownFromF2Hideout, goToF0Hideout, goToF0HideoutEnd, goF2ToF1HideoutEnd;
	QuestStep goF1ToF2Hideout, useKnifeOnPottedFan, fillCoinPurse, useBranchOnCoinPurse, showSackToVibia, searchBodyForKey, enterTrapdoor, talkToQueenToGoCamTorum;
	DetailedQuestStep enterCamTorum, talkToAttala, talkToServiusInCamTorum, goUpstairsPub, takeBeer, goDownstairsPub, useBeerOnGalna, enterCamTorumHouseBasement;
	QuestStep takeBeerCabinet, drinkBeer, takeSteamforgeBrew, takeDwarvenStout, takeWizardsMindBomb, placeSteamforgedBrew, placeDwarvenStout, placeBeer,
			takeBeerFromBarrel, placeEmptyGlass, placeMindBomb,	inspectFireplace, useHole, watchCutsceneCamTorum, returnThroughHole, returnToServius;
	DetailedQuestStep climbUpFromTeumoBasement, enterNeypotzli, talkToEyatalli, locateKeystone;
	QuestStep enterStreamboundCavern, locateInStreambound, enterEarthboundCavernFromStreambound, enterEarthboundCavern, locateInEarthbound,
			enterAncientPrison, enterAncientPrisonFromEarthbound, locateInAncientPrison, touchGlowingSymbol, defeatCultists, talkToAttalaAfterCultistFight;

	DetailedQuestStep talkToServiusAtTalTeklan, enterTonaliCavern, defeatFinalCultists, fightEnnius, tonaliGoDownStairsF2ToF1, tonaliGoDownStairsF1ToF0,
			useRedTeleporter, useBlueTeleporter, crossLog, useBlueTeleporter2;
	DetailedQuestStep useRedTeleporter2, useBlueTeleporterLizards, useRedTeleporter3, climbRope;

	QuestStep activateStrangePlatform, enterTonaliWithLift, descendIntoSunPuzzle, inspectSunStatue, getEssenceFromUrns, solveSunPuzzle, solveSunPuzzle2Step1MoveItzla,
			solveSunPuzzle2Step1Craft, solveSunPuzzle2Step2MoveItzla, solveSunPuzzle2Step2Craft, solveSunPuzzle2Step3MoveItzla, solveSunPuzzle2Step3Craft;
	QuestStep solveSunPuzzle1Step1MoveItzla, solveSunPuzzle1Step1Craft, solveSunPuzzle1Step2MoveItzla, solveSunPuzzle1Step2Craft,
			solveSunPuzzle1Step3MoveItzla, solveSunPuzzle1Step3Craft;

	QuestStep goUpFromSunPuzzle, enterMoonPuzzle, moveItzlaNorth, moveItzlaSouth, pullTreeRoots, getKnifeBlade, placeRoots, fletchRoots,
			repeatMoonPuzzleThreeTimes,	leaveMoonPuzzleRoom;

	QuestStep enterFinalBossArea, approachMetzli, defeatFinalBoss, defeatFinalBossSidebar, watchFinalBossAfterCutscene, goToNorthOfFinalArea,
	goToNorthOfFinalAreaAgilityShortcut, inspectRanulPillar, inspectRalosPillar, inspectDoor, inspectSkeleton, readStoneTablet, finishQuest;

	Zone templeArea, templeBasement, eastTempleBasement, hiddenRoom, palaceF1, palaceF2, hideoutGroundFloor, hideoutMiddleFloor, hideoutTopFloor,
			hideoutBasement, camTorum, camTorumF2, camTorumBasement, hiddenTunnel, hiddenTunnel2;

	Zone antechamber, prison, streambound, earthbound, ancientShrine, neypotzliFightRoom, tonaliCavernF2, tonaliCavernF0P2South, tonaliCavernF0P2North,
	tonaliCavernF1Stairs, tonaliCavernF0Start, tonaliCavernF1Rockslugs, tonaliCavernF0P3V1, tonaliCavernF1GrimyLizards, tonaliCavernF1Nagua,
			tonaliCavernF2North, tonaliCavernF0P3V2, sunPuzzleRoom, moonPuzzleRoom, finalBossArea;

	Requirement inTempleArea, inTempleBasement, inEastTempleBasement, inHiddenRoom, inPalaceF1, inPalaceF2, inHideout, inHideoutF1, inHideoutF2,
			inHideoutBasement, inCamTorum, inCamTorumF2, inCamTorumBasement, inCamTorumHiddenTunnel;

	Requirement inAntechamber, inPrison, inStreambound, inEarthbound, inAncientShrine, inNeypotzli, inNeypotzliFightRoom;
	Requirement inTonaliCavern, inTonaliCavernF0P2South, inTonaliCavernF0P2North, inTonaliCavernF0Start, inTonaliCavernF0P3, inTonaliCavernF1Stairs,
			inTonaliCavernF1Rockslugs, inTonaliCavernF1GrimyLizards, inTonaliCavernF1Nagua, inTonaliCavernF2North, inSunPuzzleRoom, inMoonPuzzleRoom,
			inFinalBossArea;

	Requirement quetzalMadeSalvagerOverlook, hasAtesAndActivatedTeleport;

	Requirement isSouthDrawer, hasDrawerKeyOrOpened, usedSigilOnCanvas, emissaryScrollNearby, inChestInterface;
	Requirement hasSackOfGivenSack, isGalnaDrunk, notPlacedMindBomb, notPlacedBeer, notPlacedSteamforgeBrew, notPlacedDwarvenStout, beerTakenFromBarrel;
	Requirement locatedKeystone1, locatedKeystone2, liftActivated, inspectedSunStatue, itzlaInPosSunPuzzle2Step1, completedSunPuzzleP1;
	Requirement  itzlaInPosSunPuzzle1Step1, itzlaInPosSunPuzzle1Step2, itzlaInPosSunPuzzle1Step3;
	Requirement  itzlaInPosSunPuzzle2Step2, completedSunPuzzleP2, itzlaInPosSunPuzzle2Step3, completedSunPuzzleP3;
	Requirement inMoonPuzzleP1, inMoonPuzzleP2, inMoonPuzzleP3, completedMoonPuzzle;
	ManualRequirement northPlatformSolutionKnown, southPlatformSolutionKnown;
	Requirement isPuzzleOrder1, isPuzzleOrder2;

	Requirement is72Agility, notInspectedRalosPillar, notInspectedRanulPillar, notInspectedSkeleton, notInspectedDoor;

	int lastKnownStateStep = 0;
	int lastKnownStateDarkFlame, lastKnownStateLightFlame = -1;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupZones();
		setupRequirements();
		setupSteps();

		lastKnownStateStep = -1;
		lastKnownStateDarkFlame = -1;
		lastKnownStateLightFlame = -1;

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(1, startQuest);

		ConditionalStep goEnterTemple = new ConditionalStep(this, goToTempleFromGorge);
		goEnterTemple.addStep(and(inTempleArea, emissaryRobes), enterTwilightTemple);
		goEnterTemple.addStep(inTempleArea, searchChestForEmissaryRobes);
		goEnterTemple.addStep(hasAtesAndActivatedTeleport, goToTempleWithAtes);
		goEnterTemple.addStep(quetzalMadeSalvagerOverlook, goToTempleFromSalvager);
		steps.put(3, goEnterTemple);

		ConditionalStep goEnterTempleBasement = new ConditionalStep(this, goEnterTemple);
		goEnterTempleBasement.addStep(inHiddenRoom, pickBlueChest);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, usedSigilOnCanvas), enterPassage);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, canvasPiece), useCanvasPieceOnPicture);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, hasDrawerKeyOrOpened, isSouthDrawer), openDrawers2);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, hasDrawerKeyOrOpened), openDrawers);
		goEnterTempleBasement.addStep(inEastTempleBasement, searchBed);
		goEnterTempleBasement.addStep(inTempleBasement, enterBackroom);
		goEnterTempleBasement.addStep(and(inTempleArea, emissaryRobes), goDownStairsTemple);

		steps.put(4, goEnterTempleBasement);
		steps.put(5, goEnterTempleBasement);
		steps.put(6, goEnterTempleBasement);
		steps.put(7, goEnterTempleBasement);

		ConditionalStep goFightInBasement = new ConditionalStep(this, goEnterTemple);
		goFightInBasement.addStep(inEastTempleBasement, fightEnforcer);
		goFightInBasement.addStep(inTempleBasement, enterBackroom);
		goFightInBasement.addStep(and(inTempleArea, emissaryRobes), goDownStairsTemple);
		steps.put(8, goFightInBasement);

		ConditionalStep goReadScroll = new ConditionalStep(this, goEnterTemple);
		goReadScroll.addStep(emissaryScroll, readEmissaryScroll);
		goReadScroll.addStep(and(or(inEastTempleBasement, inHiddenRoom), emissaryScrollNearby), pickUpEmissaryScroll);
		goReadScroll.addStep(inEastTempleBasement, enterPassage);
		goReadScroll.addStep(inHiddenRoom, pickBlueChest);
		goReadScroll.addStep(inTempleBasement, enterBackroom);
		goReadScroll.addStep(and(inTempleArea, emissaryRobes), goDownStairsTemple);
		steps.put(9, goReadScroll);

		ConditionalStep goTalkToQueen = new ConditionalStep(this, climbStairsF0ToF1Palace);
		goTalkToQueen.addStep(inPalaceF2, talkToQueen);
		goTalkToQueen.addStep(inPalaceF1, climbStairsF1ToF2Palace);
		steps.put(10, goTalkToQueen);

		steps.put(11, talkToCaptainVibia);
		ConditionalStep goIntoHouse = new ConditionalStep(this, inspectWindow);
		goIntoHouse.addStep(inHideout, giveBonesOrMeatToDog);
		steps.put(12, goIntoHouse);
		steps.put(13, goIntoHouse);
		steps.put(14, goIntoHouse);
		steps.put(15, goIntoHouse);
		steps.put(16, goIntoHouse);

		ConditionalStep goPetDog = new ConditionalStep(this, inspectWindow);
		goPetDog.addStep(inChestInterface, openDoorWithGusCode);
		goPetDog.addStep(inHideout, enterDoorCode);
		steps.put(17, goPetDog);

		ConditionalStep goDoHideoutStuff = new ConditionalStep(this, inspectWindow);
		goDoHideoutStuff.addStep(and(inHideoutF1, hasSackOfGivenSack, makeshiftBlackjack), goToF0HideoutEnd);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, makeshiftBlackjack), goF2ToF1HideoutEnd);
		goDoHideoutStuff.addStep(and(inHideout, hasSackOfGivenSack, makeshiftBlackjack), showSackToVibia);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, coinPurseWithSand, branch), useBranchOnCoinPurse);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, knife, coinPurseWithSand), useKnifeOnPottedFan);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, knife, coinPurseEmpty), fillCoinPurse);
		goDoHideoutStuff.addStep(inHideoutF2, goDownFromF2Hideout);
		goDoHideoutStuff.addStep(and(inHideoutF1, hasSackOfGivenSack, knife, coinPurseEmpty), goF1ToF2Hideout);
		goDoHideoutStuff.addStep(and(coinPurse), emptyCoinPurse);
		goDoHideoutStuff.addStep(and(inHideoutF1, hasSackOfGivenSack, knife), takeCoinPurse);
		goDoHideoutStuff.addStep(and(inHideoutF1), goToF0Hideout);
		goDoHideoutStuff.addStep(and(inHideout, hasSackOfGivenSack, knife), goToF1Hideout);
		goDoHideoutStuff.addStep(and(inHideout, hasSackOfGivenSack), takeKnife);
		goDoHideoutStuff.addStep(and(inHideout, potatoes), removePotatoesFromSack);
		goDoHideoutStuff.addStep(inHideout, takePotato);
		steps.put(18, goDoHideoutStuff);
		// 19 was took coin purse, 20 was emptied it first time
		steps.put(19, goDoHideoutStuff);
		steps.put(20, goDoHideoutStuff);
		steps.put(21, goDoHideoutStuff);
		steps.put(22, goDoHideoutStuff);

		ConditionalStep goSearchJanus = new ConditionalStep(this, inspectWindow);
		goSearchJanus.addStep(inHideout, searchBodyForKey);
		steps.put(23, goSearchJanus);

		ConditionalStep goEnterTrapdoor = new ConditionalStep(this, inspectWindow);
		goEnterTrapdoor.addStep(inHideoutBasement, talkToQueenToGoCamTorum);
		goEnterTrapdoor.addStep(inHideout, enterTrapdoor);
		steps.put(24, goEnterTrapdoor);
		steps.put(25, goEnterTrapdoor);
		steps.put(26, goEnterTrapdoor);

		ConditionalStep goTalkToAttala = new ConditionalStep(this, enterCamTorum);
		goTalkToAttala.addStep(inCamTorum, talkToAttala);
		// TODO: See if cut cutscene at 27, if you restart it by entering Cam Torum again or not
		steps.put(27, goTalkToAttala);
		steps.put(28, goTalkToAttala);

		ConditionalStep goTalkToServiusCamTorum = new ConditionalStep(this, enterCamTorum);
		goTalkToServiusCamTorum.addStep(inCamTorum, talkToServiusInCamTorum);
		steps.put(29, goTalkToServiusCamTorum);

		ConditionalStep doBasementPuzzle = new ConditionalStep(this, enterCamTorumHouseBasement);
		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedMindBomb, wizardsMindBomb), placeMindBomb);
		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedMindBomb), takeWizardsMindBomb);

		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedBeer, beer), placeBeer);
		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedBeer), takeBeerCabinet);

		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedSteamforgeBrew, steamforgedBrew), placeSteamforgedBrew);
		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedSteamforgeBrew), takeSteamforgeBrew);

		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedDwarvenStout, dwarvenStout), placeDwarvenStout);
		doBasementPuzzle.addStep(and(inCamTorumBasement, notPlacedDwarvenStout), takeDwarvenStout);

		doBasementPuzzle.addStep(and(inCamTorumBasement, beerTakenFromBarrel, emptyGlass), placeEmptyGlass);
		doBasementPuzzle.addStep(and(inCamTorumBasement, beer), drinkBeer);
		doBasementPuzzle.addStep(inCamTorumBasement, takeBeerFromBarrel);

		ConditionalStep goGetGalnaDrunk = new ConditionalStep(this, enterCamTorum);
		goGetGalnaDrunk.addStep(and(inCamTorumF2, beer), goDownstairsPub);
		goGetGalnaDrunk.addStep(and(inCamTorum, beer), useBeerOnGalna);
		goGetGalnaDrunk.addStep(inCamTorumF2, takeBeer);
		goGetGalnaDrunk.addStep(inCamTorum, goUpstairsPub);

		ConditionalStep goDoCamTorum = new ConditionalStep(this, enterCamTorum);
		goDoCamTorum.addStep(and(inCamTorum, isGalnaDrunk), doBasementPuzzle);
		goDoCamTorum.addStep(inCamTorum, goGetGalnaDrunk);
		steps.put(30, goDoCamTorum);
		steps.put(31, goDoCamTorum);

		ConditionalStep goEnterFireplace = new ConditionalStep(this, enterCamTorum);
		goEnterFireplace.addStep(inCamTorumBasement, inspectFireplace);
		goEnterFireplace.addStep(inCamTorum, enterCamTorumHouseBasement);
		steps.put(32, goEnterFireplace);

		ConditionalStep goEnterHole = new ConditionalStep(this, enterCamTorum);
		goEnterHole.addStep(inCamTorumHiddenTunnel, watchCutsceneCamTorum);
		goEnterHole.addStep(inCamTorumBasement, useHole);
		goEnterHole.addStep(inCamTorum, enterCamTorumHouseBasement);
		steps.put(33, goEnterHole);
		steps.put(34, goEnterHole);

		ConditionalStep goTalkToServiusBasement = new ConditionalStep(this, enterCamTorum);
		goTalkToServiusBasement.addStep(inCamTorumHiddenTunnel, returnThroughHole);
		goTalkToServiusBasement.addStep(inCamTorumBasement, returnToServius);
		goTalkToServiusBasement.addStep(inCamTorum, enterCamTorumHouseBasement);
		steps.put(35, goTalkToServiusBasement);

		ConditionalStep goTalkToEyat = new ConditionalStep(this, enterCamTorum);
		goTalkToEyat.addStep(inCamTorumBasement, climbUpFromTeumoBasement);
		goTalkToEyat.addStep(inNeypotzli, talkToEyatalli);
		goTalkToEyat.addStep(inCamTorum, enterNeypotzli);
		steps.put(36, goTalkToEyat);
		steps.put(37, goTalkToEyat);
		steps.put(38, goTalkToEyat);

		// Received keystone fragment, varbit VarbitID.VMQ4_MONOLITH_FRAGMENT_ATTEMPTED_GIVE went 0->1
		ConditionalStep goLocateKeystone = new ConditionalStep(this, enterCamTorum);
		goLocateKeystone.addStep(and(inPrison, locatedKeystone2), locateInAncientPrison);
		goLocateKeystone.addStep(and(inEarthbound, locatedKeystone2), enterAncientPrisonFromEarthbound);
		goLocateKeystone.addStep(and(inNeypotzli, locatedKeystone2), enterAncientPrison);

		goLocateKeystone.addStep(and(inEarthbound, locatedKeystone1), locateInEarthbound);
		goLocateKeystone.addStep(and(inStreambound, locatedKeystone1), enterEarthboundCavernFromStreambound);
		goLocateKeystone.addStep(and(inNeypotzli, locatedKeystone1), enterEarthboundCavern);

		goLocateKeystone.addStep(inStreambound, locateInStreambound);
		goLocateKeystone.addStep(inNeypotzli, enterStreamboundCavern);
		goLocateKeystone.addStep(inCamTorum, enterNeypotzli);
		steps.put(39, goLocateKeystone);
		steps.put(40, goLocateKeystone);

		ConditionalStep goTouchSymbol = new ConditionalStep(this, enterCamTorum);
		goTouchSymbol.addStep(and(inNeypotzliFightRoom), defeatCultists);
		goTouchSymbol.addStep(and(inPrison), touchGlowingSymbol);
		goTouchSymbol.addStep(and(inEarthbound), enterAncientPrisonFromEarthbound);
		goTouchSymbol.addStep(and(inNeypotzli), enterAncientPrison);
		goTouchSymbol.addStep(inCamTorum, enterNeypotzli);
		steps.put(41, goTouchSymbol);
		// Skipped 42?
		steps.put(43, goTouchSymbol);

		ConditionalStep goTalkToAttalaAfterFight = new ConditionalStep(this, enterCamTorum);
		goTalkToAttalaAfterFight.addStep(and(inNeypotzliFightRoom), talkToAttalaAfterCultistFight);
		goTalkToAttalaAfterFight.addStep(and(inPrison), touchGlowingSymbol);
		goTalkToAttalaAfterFight.addStep(and(inEarthbound), enterAncientPrisonFromEarthbound);
		goTalkToAttalaAfterFight.addStep(and(inNeypotzli), enterAncientPrison);
		goTalkToAttalaAfterFight.addStep(inCamTorum, enterNeypotzli);
		steps.put(44, goTalkToAttalaAfterFight);

		steps.put(45, talkToServiusAtTalTeklan);
		steps.put(46, enterTonaliCavern);

		ConditionalStep goDefeatFinalCultists = new ConditionalStep(this, enterTonaliCavern);
		goDefeatFinalCultists.addStep(inTonaliCavern, defeatFinalCultists);
		steps.put(47, goDefeatFinalCultists);

		ConditionalStep goDefeatEnnius = new ConditionalStep(this, enterTonaliCavern);
		goDefeatEnnius.addStep(inTonaliCavern, fightEnnius);
		steps.put(48, goDefeatEnnius);
		steps.put(49, goDefeatEnnius);
		steps.put(50, goDefeatEnnius);
		steps.put(51, goDefeatEnnius);

		ConditionalStep goDoSunPuzzle = new ConditionalStep(this, getEssenceFromUrns);
		goDoSunPuzzle.addStep(not(inspectedSunStatue), inspectSunStatue);
		goDoSunPuzzle.addStep(completedSunPuzzleP3, goUpFromSunPuzzle);
		goDoSunPuzzle.addStep(and(completedSunPuzzleP2, essence, isPuzzleOrder2, itzlaInPosSunPuzzle2Step3), solveSunPuzzle2Step3Craft);
		goDoSunPuzzle.addStep(and(completedSunPuzzleP2, essence, isPuzzleOrder2), solveSunPuzzle2Step3MoveItzla);

		goDoSunPuzzle.addStep(and(completedSunPuzzleP1, essence, isPuzzleOrder2, itzlaInPosSunPuzzle2Step2), solveSunPuzzle2Step2Craft);
		goDoSunPuzzle.addStep(and(completedSunPuzzleP1, essence, isPuzzleOrder2), solveSunPuzzle2Step2MoveItzla);

		goDoSunPuzzle.addStep(and(essence, isPuzzleOrder2, itzlaInPosSunPuzzle2Step1), solveSunPuzzle2Step1Craft);
		goDoSunPuzzle.addStep(and(essence, isPuzzleOrder2), solveSunPuzzle2Step1MoveItzla);

		goDoSunPuzzle.addStep(and(completedSunPuzzleP2, essence, isPuzzleOrder1, itzlaInPosSunPuzzle1Step3), solveSunPuzzle1Step3Craft);
		goDoSunPuzzle.addStep(and(completedSunPuzzleP2, essence, isPuzzleOrder1), solveSunPuzzle1Step3MoveItzla);

		goDoSunPuzzle.addStep(and(completedSunPuzzleP1, essence, isPuzzleOrder1, itzlaInPosSunPuzzle1Step2), solveSunPuzzle1Step2Craft);
		goDoSunPuzzle.addStep(and(completedSunPuzzleP1, essence, isPuzzleOrder1), solveSunPuzzle1Step2MoveItzla);

		goDoSunPuzzle.addStep(and(essence, isPuzzleOrder1, itzlaInPosSunPuzzle1Step1), solveSunPuzzle1Step1Craft);
		goDoSunPuzzle.addStep(and(essence, isPuzzleOrder1), solveSunPuzzle1Step1MoveItzla);

		goDoSunPuzzle.addStep(and(essence), solveSunPuzzle);

		ConditionalStep goDoMoonPuzzle = new ConditionalStep(this, getKnifeBlade);
		goDoMoonPuzzle.addStep(completedMoonPuzzle, leaveMoonPuzzleRoom);
		goDoMoonPuzzle.addStep(not(southPlatformSolutionKnown), moveItzlaSouth);
		goDoMoonPuzzle.addStep(not(northPlatformSolutionKnown), moveItzlaNorth);
		goDoMoonPuzzle.addStep(and(kindling), placeRoots);
		goDoMoonPuzzle.addStep(and(roots, knifeBlade), fletchRoots);
		goDoMoonPuzzle.addStep(knifeBlade, pullTreeRoots);

		ConditionalStep goDeeperIntoTonali = new ConditionalStep(this, enterTonaliCavern);
		goDeeperIntoTonali.addStep(and(inMoonPuzzleRoom), goDoMoonPuzzle);
		goDeeperIntoTonali.addStep(and(inSunPuzzleRoom), goDoSunPuzzle);
		goDeeperIntoTonali.addStep(and(inTonaliCavernF2North, liftActivated, completedSunPuzzleP3), enterMoonPuzzle);
		goDeeperIntoTonali.addStep(and(inTonaliCavernF2North, liftActivated), descendIntoSunPuzzle);
		goDeeperIntoTonali.addStep(inTonaliCavernF2North, activateStrangePlatform);
		goDeeperIntoTonali.addStep(inTonaliCavernF1Nagua, climbRope);
		goDeeperIntoTonali.addStep(inTonaliCavernF0P3, useRedTeleporter3);
		goDeeperIntoTonali.addStep(inTonaliCavernF0P2North, useRedTeleporter3);
		goDeeperIntoTonali.addStep(inTonaliCavernF1GrimyLizards, useBlueTeleporterLizards);
		goDeeperIntoTonali.addStep(inTonaliCavernF0P2South, useRedTeleporter2);
		goDeeperIntoTonali.addStep(inTonaliCavernF1Rockslugs, useBlueTeleporter);
		goDeeperIntoTonali.addStep(inTonaliCavernF0Start, useRedTeleporter);
		goDeeperIntoTonali.addStep(inTonaliCavernF1Stairs, tonaliGoDownStairsF1ToF0);
		goDeeperIntoTonali.addStep(inTonaliCavern, tonaliGoDownStairsF2ToF1);
		goDeeperIntoTonali.addStep(liftActivated, enterTonaliWithLift);
		steps.put(52, goDeeperIntoTonali);
		steps.put(53, goDeeperIntoTonali);
		steps.put(54, goDeeperIntoTonali);

		steps.put(55, goDeeperIntoTonali);

		ConditionalStep goFinalFight = new ConditionalStep(this, goDeeperIntoTonali);
		goFinalFight.addStep(and(inFinalBossArea), approachMetzli);
		goFinalFight.addStep(and(inTonaliCavernF2North, liftActivated), enterFinalBossArea);
		steps.put(60, goFinalFight);
		steps.put(61, goFinalFight);

		ConditionalStep fightFinalBoss = new ConditionalStep(this, goDeeperIntoTonali);
		fightFinalBoss.addStep(and(inFinalBossArea), defeatFinalBoss);
		fightFinalBoss.addStep(and(inTonaliCavernF2North, liftActivated), enterFinalBossArea);
		steps.put(62, fightFinalBoss);
		// Defeated boss

		ConditionalStep doFinalBossPostCutscene = new ConditionalStep(this, goDeeperIntoTonali);
		doFinalBossPostCutscene.addStep(and(inFinalBossArea), watchFinalBossAfterCutscene);
		doFinalBossPostCutscene.addStep(and(inTonaliCavernF2North, liftActivated), enterFinalBossArea);
		steps.put(63, doFinalBossPostCutscene);

		ConditionalStep goNorthAfterFinalBoss = new ConditionalStep(this, goDeeperIntoTonali);
		goNorthAfterFinalBoss.addStep(and(inFinalBossArea, is72Agility), goToNorthOfFinalAreaAgilityShortcut);
		goNorthAfterFinalBoss.addStep(inFinalBossArea, goToNorthOfFinalArea);
		steps.put(64, goNorthAfterFinalBoss);

		// Inspected

		// Look at tablet, statues
		ConditionalStep goInspectFinalChamberItems = new ConditionalStep(this, goDeeperIntoTonali);
		goInspectFinalChamberItems.addStep(and(inFinalBossArea, notInspectedRalosPillar), inspectRalosPillar);
		goInspectFinalChamberItems.addStep(and(inFinalBossArea, notInspectedRanulPillar), inspectRanulPillar);
		goInspectFinalChamberItems.addStep(and(inFinalBossArea, notInspectedDoor), inspectDoor);
		goInspectFinalChamberItems.addStep(and(inFinalBossArea, notInspectedSkeleton, stoneTablet), readStoneTablet);
		goInspectFinalChamberItems.addStep(and(inFinalBossArea, notInspectedSkeleton), inspectSkeleton);
		goInspectFinalChamberItems.addStep(and(inTonaliCavernF2North, liftActivated), enterFinalBossArea);
		steps.put(65, goInspectFinalChamberItems);

		ConditionalStep goFinishQuest = new ConditionalStep(this, enterTonaliCavern);
		goFinishQuest.addStep(inTonaliCavern, finishQuest);
		steps.put(66, goFinishQuest);
		steps.put(67, goFinishQuest);
		return steps;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		Requirement darkBlueFlameMissing = not(new ObjectCondition(ObjectID.VMQ4_MOON_PUZZLE_FIRE_1, new WorldPoint(1296, 9455, 1)));
		Requirement lightBlueFlameMissing = not(new ObjectCondition(ObjectID.VMQ4_MOON_PUZZLE_FIRE_2, new WorldPoint(1292, 9454, 1)));
		if (inMoonPuzzleRoom == null || !inMoonPuzzleRoom.check(client)) return;

		int currentStep = client.getVarbitValue(VarbitID.VMQ4_MOON_PUZZLE_PROGRESS);
		if (currentStep != lastKnownStateStep)
		{
			lastKnownStateDarkFlame = -1;
			lastKnownStateLightFlame = -1;
			northPlatformSolutionKnown.setShouldPass(false);
			southPlatformSolutionKnown.setShouldPass(false);
			lastKnownStateStep = currentStep;
			placeRoots.setText("Have Itzla move between both the north and south " +
					"platforms to see how many braziers are lit around the room total. Put that many into the statue.");
			kindling.setQuantity(-1);
		}

		if (lastKnownStateDarkFlame != -1 && lastKnownStateLightFlame != -1) return;

		if (darkBlueFlameMissing.check(client))
		{
			lastKnownStateDarkFlame = getSumOfLitBraziers();
			northPlatformSolutionKnown.setShouldPass(true);
		}
		else if (lightBlueFlameMissing.check(client))
		{
			lastKnownStateLightFlame = getSumOfLitBraziers();
			southPlatformSolutionKnown.setShouldPass(true);
		}

		if (lastKnownStateDarkFlame >= 0 && lastKnownStateLightFlame >= 0)
		{
			int total = lastKnownStateLightFlame + lastKnownStateDarkFlame;
			placeRoots.setText("Place " + total + " roots in the moon statue.");
			kindling.setQuantity(total);
		}
	}

	private int getSumOfLitBraziers()
	{
		Tile[][] tiles;
		if (client.getTopLevelWorldView().getScene() == null) return -1;

		List<WorldPoint> wps = List.of(
				new WorldPoint(1296, 9457, 1),
				new WorldPoint(1289, 9457, 1),
				new WorldPoint(1286, 9454, 1),
				new WorldPoint(1282, 9449, 1),
				new WorldPoint(1282, 9443, 1),
				new WorldPoint(1286, 9438, 1),
				new WorldPoint(1289, 9435, 1),
				new WorldPoint(1296, 9435, 1)

		);

		int total = 0;

		for (WorldPoint wp : wps)
		{
			List<LocalPoint> localPoints = QuestPerspective.getLocalPointsFromWorldPointInInstance(client.getTopLevelWorldView(), wp);

			if (localPoints.isEmpty()) return -1;

			tiles = client.getTopLevelWorldView().getScene().getTiles()[client.getTopLevelWorldView().getPlane()];

			for (LocalPoint localPoint : localPoints)
			{
				Tile b1Tile = tiles[localPoint.getSceneX()][localPoint.getSceneY()];
				boolean isMatch = Arrays.stream(b1Tile.getGameObjects()).anyMatch((obj) -> obj != null && obj.getId() == ObjectID.VMQ4_MOON_PUZZLE_BRAZIER_LIT);
				if (isMatch) total++;
			}
		}

		return total;
	}

	@Override
	protected void setupZones()
	{
		templeArea = new Zone(new WorldPoint(1613, 3205, 0), new WorldPoint(1729, 3293, 0));
		templeBasement = new Zone(new WorldPoint(1660, 9680, 0), new WorldPoint(1725, 9720, 0));
		eastTempleBasement = new Zone(new WorldPoint(1707, 9696, 0), new WorldPoint(1718, 9715, 0));
		hiddenRoom = new Zone(new WorldPoint(1721, 9702, 0), new WorldPoint(1725, 9709, 0));
		palaceF1 = new Zone(new WorldPoint(1669, 3150, 1), new WorldPoint(1692, 3175, 1));
		palaceF2 = new Zone(new WorldPoint(1669, 3150, 2), new WorldPoint(1692, 3175, 2));
		hideoutGroundFloor = new Zone(new WorldPoint(1643, 3091, 0), new WorldPoint(1652, 3096, 0));
		hideoutMiddleFloor = new Zone(new WorldPoint(1643, 3091, 1), new WorldPoint(1652, 3096, 1));
		hideoutTopFloor = new Zone(new WorldPoint(1643, 3091, 2), new WorldPoint(1652, 3102, 2));
		hideoutBasement = new Zone(new WorldPoint(1643, 9486, 0), new WorldPoint(1657, 9500, 0));
		camTorum = new Zone(new WorldPoint(1378, 9502, 0), new WorldPoint(1524, 9600, 3));
		camTorumF2 = new Zone(new WorldPoint(1465, 9567, 2), new WorldPoint(1470, 9572, 2));
		camTorumBasement = new Zone(new WorldPoint(1464, 9564, 0), new WorldPoint(1472, 9574, 0));
		hiddenTunnel = new Zone(new WorldPoint(1468, 9561, 0), new WorldPoint(1476, 9563, 0));
		hiddenTunnel2 = new Zone(new WorldPoint(1477, 9536, 0), new WorldPoint(1500, 9570, 0));

		antechamber = new Zone(5782, 1);
		prison = new Zone(5525, 0);
		earthbound = new Zone(5527, 0);
		streambound = new Zone(6039, 0);
		ancientShrine = new Zone(6037, 0);

		neypotzliFightRoom = new Zone(new WorldPoint(1352, 9501, 0), new WorldPoint(1372, 9524, 0));
		tonaliCavernF1Stairs = new Zone(new WorldPoint(1329, 9360, 1), new WorldPoint(1343, 9369, 1));
		tonaliCavernF1Rockslugs = new Zone(new WorldPoint(1319, 9374, 1), new WorldPoint(1329, 9386, 1));
		tonaliCavernF0Start = new Zone(new WorldPoint(1317, 9366, 0), new WorldPoint(1341, 9385, 0));
		tonaliCavernF0P2South = new Zone(new WorldPoint(1282, 9365, 0), new WorldPoint(1315, 9391, 0));
		tonaliCavernF0P2North = new Zone(new WorldPoint(1282, 9392, 0), new WorldPoint(1305, 9403, 0));
		tonaliCavernF2 = new Zone(new WorldPoint(1298, 9344, 2), new WorldPoint(1343, 9380, 2));

		tonaliCavernF0P3V1 = new Zone(new WorldPoint(1306, 9392, 0), new WorldPoint(1340, 9420, 0));
		tonaliCavernF0P3V2 = new Zone(new WorldPoint(1320, 9387, 0), new WorldPoint(1340, 9391, 0));

		tonaliCavernF1GrimyLizards = new Zone(new WorldPoint(1291, 9381, 1), new WorldPoint(1298, 9395, 1));
		tonaliCavernF1Nagua = new Zone(new WorldPoint(1283, 9399, 1), new WorldPoint(1310, 9425, 1));
		tonaliCavernF2North = new Zone(new WorldPoint(1303, 9399, 2), new WorldPoint(1317, 9468, 2));
		sunPuzzleRoom = new Zone(new WorldPoint(1318, 9433, 1), new WorldPoint(1345, 9458, 1));
		moonPuzzleRoom = new Zone(new WorldPoint(1279, 9433, 1), new WorldPoint(1305, 9460, 1));

		finalBossArea = new Zone(new WorldPoint(1275, 9470, 0), new WorldPoint(1350, 9550, 1));
	}

	@Override
	protected void setupRequirements()
	{
		var emissaryHood = new ItemRequirement("Emissary hood", ItemID.VMQ3_CULTIST_HOOD);
		var emissaryTop = new ItemRequirement("Emissary top", ItemID.VMQ3_CULTIST_ROBE_TOP);
		var emissaryBottom = new ItemRequirement("Emissary bottom", ItemID.VMQ3_CULTIST_ROBE_BOTTOM);
		var emissaryBoots = new ItemRequirement("Emissary sandals", ItemID.VMQ3_CULTIST_SANDALS);
		emissaryRobesEquipped = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom,
				emissaryBoots).equipped().highlighted();
		emissaryRobes = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom, emissaryBoots);

		var givenBoneToDog = new VarbitRequirement(VarbitID.VMQ4, 17, Operation.GREATER_EQUAL);
		bone = new ItemRequirement("Any type of bone or raw meat", ItemID.BONES);
		bone.setConditionToHide(givenBoneToDog);
		bone.addAlternates(ItemID.BIG_BONES, ItemID.BONES_BURNT, ItemID.WOLF_BONES, ItemID.BAT_BONES, ItemID.DAGANNOTH_KING_BONES, ItemID.TBWT_BEAST_BONES,
				ItemID.WYRM_BONES, ItemID.BABYWYRM_BONES, ItemID.BABYDRAGON_BONES, ItemID.WYVERN_BONES, ItemID.DRAGON_BONES, ItemID.DRAKE_BONES,
				ItemID.HYDRA_BONES, ItemID.LAVA_DRAGON_BONES, ItemID.DRAGON_BONES_SUPERIOR, ItemID.MM_NORMAL_MONKEY_BONES,
				ItemID.MM_BEARDED_GORILLA_MONKEY_BONES, ItemID.MM_NORMAL_GORILLA_MONKEY_BONES, ItemID.MM_LARGE_ZOMBIE_MONKEY_BONES,
				ItemID.MM_SMALL_ZOMBIE_MONKEY_BONES, ItemID.MM_SMALL_NINJA_MONKEY_BONES, ItemID.MM_MEDIUM_NINJA_MONKEY_BONES, ItemID.TBWT_JOGRE_BONES,
				ItemID.TBWT_BURNT_JOGRE_BONES, ItemID.ZOGRE_BONES, ItemID.ZOGRE_ANCESTRAL_BONES_FAYG, ItemID.ZOGRE_ANCESTRAL_BONES_RAURG,
				ItemID.ZOGRE_ANCESTRAL_BONES_OURG, ItemID.ALAN_BONES, ItemID.RAW_BEAR_MEAT, ItemID.RAW_BOAR_MEAT, ItemID.RAW_RAT_MEAT,
				ItemID.RAW_UGTHANKI_MEAT, ItemID.YAK_MEAT_RAW);

		rangedGear = new ItemRequirement("Ranged/Magic Combat gear", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());

		// Item Recommended
		combatWeapon = new ItemRequirement("Combat weapon", -1, -1).isNotConsumed();
		combatWeapon.setDisplayItemId(BankSlotIcons.getCombatGear());

		combatGear = new ItemRequirement("Melee Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		food.setUrlSuffix("Food");

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, 3);

		whistle = new ItemRequirement("Quetzal whistle", ItemID.HG_QUETZALWHISTLE_BASIC);
		whistle.addAlternates(ItemID.HG_QUETZALWHISTLE_ENHANCED, ItemID.HG_QUETZALWHISTLE_PERFECTED);
		pendant = new ItemRequirement("Pendant of ates", ItemID.PENDANT_OF_ATES);
		pendantToTwilight = new ItemRequirement("Pendant of ates ([2] Twilight Temple)", ItemID.PENDANT_OF_ATES);
		civitasTeleport = new ItemRequirement("Civitas illa fortis teleport", ItemID.POH_TABLET_FORTISTELEPORT);
		// Quest items
		drawerKey = new ItemRequirement("Key", ItemID.VMQ4_DRAWER_KEY);
		canvasPiece = new ItemRequirement("Canvas piece", ItemID.VMQ4_PAINTING_SIGIL);
		emissaryScroll = new ItemRequirement("Emissary scroll", ItemID.VMQ4_CULT_MANIFEST);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		potatoes = new ItemRequirement("Potatoes (?)", ItemID.SACK_POTATO_3);
		potatoes.addAlternates(ItemID.SACK_POTATO_2, ItemID.SACK_POTATO_1);

		var givenSack = new VarbitRequirement(VarbitID.VMQ4_JANUS_SACK_GIVEN, 1, Operation.GREATER_EQUAL);
		emptySack = new ItemRequirement("Empty sack", ItemID.SACK_EMPTY);
		emptySack.setConditionToHide(givenSack);
		coinPurse = new ItemRequirement("Coin purse", ItemID.VMQ4_JANUS_PURSE);

		coinPurseFullOrEmpty = new ItemRequirement("Coin purse", ItemID.VMQ4_JANUS_PURSE);
		coinPurseFullOrEmpty.addAlternates(ItemID.VMQ4_JANUS_PURSE_EMPTY);
		coinPurseEmpty = new ItemRequirement("Empty coin purse", ItemID.VMQ4_JANUS_PURSE_EMPTY);
		coinPurseWithSand = new ItemRequirement("Sandy coin purse", ItemID.VMQ4_JANUS_PURSE_SAND);
		branch = new ItemRequirement("Branch", ItemID.VMQ4_JANUS_REED);
		makeshiftBlackjack = new ItemRequirement("Makeshift blackjack", ItemID.VMQ4_JANUS_SLAP);

		beer = new ItemRequirement("Beer", ItemID.BEER);
		steamforgedBrew = new ItemRequirement("Steamforge brew", ItemID.STEAMFORGE_BREW);
		dwarvenStout = new ItemRequirement("Dwarven stout", ItemID.DWARVEN_STOUT);
		emptyGlass = new ItemRequirement("Empty glass", ItemID.BEER_GLASS);
		wizardsMindBomb = new ItemRequirement("Wizard's mind bomb", ItemID.WIZARDS_MIND_BOMB);

		keystoneFragment = new ItemRequirement("Keystone fragment", ItemID.VMQ4_MONOLITH_FRAGMENT);
		essence = new ItemRequirement("Kuhu essence", ItemID.VMQ4_ESSENCE, 2);
		roots = new ItemRequirement("Ancient roots", ItemID.VMQ4_ROOTS);
		kindling = new ItemRequirement("Root kindling", ItemID.VMQ4_ROOT_KINDLING);
		knifeBlade = new ItemRequirement("Knife blade", ItemID.VMQ4_KNIFE);

		stoneTablet = new ItemRequirement("Stone tablet", ItemID.VMQ4_MOKI_TABLET);

		// Quest requirements
		inTempleArea = new ZoneRequirement(templeArea);
		inTempleBasement = new ZoneRequirement(templeBasement);
		inEastTempleBasement = new ZoneRequirement(eastTempleBasement);
		inHiddenRoom = new ZoneRequirement(hiddenRoom);
		inPalaceF1 = new ZoneRequirement(palaceF1);
		inPalaceF2 = new ZoneRequirement(palaceF2);
		inHideout = new ZoneRequirement(hideoutGroundFloor);
		inHideoutF1 = new ZoneRequirement(hideoutMiddleFloor);
		inHideoutF2 = new ZoneRequirement(hideoutTopFloor);
		inHideoutBasement = new ZoneRequirement(hideoutBasement);
		inCamTorum = new ZoneRequirement(camTorum);
		inCamTorumF2 = new ZoneRequirement(camTorumF2);
		inCamTorumBasement = new ZoneRequirement(camTorumBasement);
		inCamTorumHiddenTunnel = new ZoneRequirement(hiddenTunnel, hiddenTunnel2);

		inAntechamber = new ZoneRequirement(antechamber);
		inPrison = new ZoneRequirement(prison);
		inStreambound = new ZoneRequirement(streambound);
		inEarthbound = new ZoneRequirement(earthbound);
		inAncientShrine = new ZoneRequirement(ancientShrine);
		inNeypotzli = new ZoneRequirement(antechamber, prison, streambound, earthbound, ancientShrine);
		inNeypotzliFightRoom = new ZoneRequirement(neypotzliFightRoom);

		inTonaliCavern = new ZoneRequirement(tonaliCavernF2);
		inTonaliCavernF1Stairs = new ZoneRequirement(tonaliCavernF1Stairs);
		inTonaliCavernF1Rockslugs = new ZoneRequirement(tonaliCavernF1Rockslugs);
		inTonaliCavernF0Start = new ZoneRequirement(tonaliCavernF0Start);
		inTonaliCavernF0P2South = new ZoneRequirement(tonaliCavernF0P2South);
		inTonaliCavernF0P2North = new ZoneRequirement(tonaliCavernF0P2North);
		inTonaliCavernF0P3 = new ZoneRequirement(tonaliCavernF0P3V1, tonaliCavernF0P3V2);
		inTonaliCavernF1GrimyLizards = new ZoneRequirement(tonaliCavernF1GrimyLizards);
		inTonaliCavernF1Nagua = new ZoneRequirement(tonaliCavernF1Nagua);
		inTonaliCavernF2North = new ZoneRequirement(tonaliCavernF2North);
		inSunPuzzleRoom = new ZoneRequirement(sunPuzzleRoom);
		inMoonPuzzleRoom = new ZoneRequirement(moonPuzzleRoom);
		inFinalBossArea = new ZoneRequirement(finalBossArea);

		isSouthDrawer = new VarbitRequirement(VarbitID.VMQ4_CANVAS_DRAWER, 2);
		hasDrawerKeyOrOpened = or(drawerKey, new VarbitRequirement(VarbitID.VMQ4_TEMPLE_DRAW_UNLOCKED, 1, Operation.GREATER_EQUAL));
		usedSigilOnCanvas = new VarbitRequirement(VarbitID.VMQ4, 7, Operation.GREATER_EQUAL);
		emissaryScrollNearby = new ItemOnTileRequirement(emissaryScroll);
		inChestInterface = new WidgetTextRequirement(809, 5, 9, "Confirm");

		hasSackOfGivenSack = or(emptySack, givenSack);
		isGalnaDrunk = new VarbitRequirement(VarbitID.VMQ4_TEUMO_WIFE_BEER_GIVEN, 1);

		notPlacedMindBomb = not(new ItemOnTileRequirement(ItemID.WIZARDS_MIND_BOMB, new WorldPoint(1471, 9567, 0)));
		notPlacedBeer = not(new ItemOnTileRequirement(ItemID.BEER, new WorldPoint(1466, 9573, 0)));
		notPlacedSteamforgeBrew = not(new ItemOnTileRequirement(ItemID.STEAMFORGE_BREW, new WorldPoint(1464, 9568, 0)));
		notPlacedDwarvenStout = not(new ItemOnTileRequirement(ItemID.DWARVEN_STOUT, new WorldPoint(1466, 9568, 0)));
		beerTakenFromBarrel = not(new ItemOnTileRequirement(ItemID.BEER, new WorldPoint(1469, 9571, 0)));

		locatedKeystone1 = new VarbitRequirement(VarbitID.VMQ4_NEYPOTZLI_CHECKPOINT, 1, Operation.GREATER_EQUAL);
		locatedKeystone2 = new VarbitRequirement(VarbitID.VMQ4_NEYPOTZLI_CHECKPOINT, 2, Operation.GREATER_EQUAL);

		liftActivated = new VarbitRequirement(VarbitID.VMQ4_LIFT_ACTIVATED, 1);

		// Sun puzzle
		inspectedSunStatue = or(new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_PROGRESS, 1, Operation.GREATER_EQUAL), new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_ITZLA_STATE, 1, Operation.GREATER_EQUAL));
		itzlaInPosSunPuzzle1Step1 = new NpcRequirement("Itzla at east altar", NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1338, 9446, 1));
		itzlaInPosSunPuzzle1Step2 = new NpcRequirement("Itzla at north altar", NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1330, 9454, 1));
		itzlaInPosSunPuzzle1Step3 = new NpcRequirement("Itzla at north-west altar", NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1324, 9452, 1));

		itzlaInPosSunPuzzle2Step1 = new NpcRequirement("Itzla at north altar", NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1330, 9454, 1));
		itzlaInPosSunPuzzle2Step2 = new NpcRequirement("Itzla at north altar", NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1338, 9446, 1));
		itzlaInPosSunPuzzle2Step3 = new NpcRequirement("Itzla at north altar", NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1324, 9452, 1));
		completedSunPuzzleP1 = new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_PROGRESS, 1, Operation.GREATER_EQUAL);
		completedSunPuzzleP2 = new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_PROGRESS, 2, Operation.GREATER_EQUAL);
		completedSunPuzzleP3 = new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_PROGRESS, 3, Operation.GREATER_EQUAL);
		isPuzzleOrder1 = new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_ORDER, 1);
		isPuzzleOrder2 = new VarbitRequirement(VarbitID.VMQ4_SUN_PUZZLE_ORDER, 2);

		inMoonPuzzleP1 = new VarbitRequirement(VarbitID.VMQ4_MOON_PUZZLE_PROGRESS, 0);
		inMoonPuzzleP2 = new VarbitRequirement(VarbitID.VMQ4_MOON_PUZZLE_PROGRESS, 1);
		inMoonPuzzleP3 = new VarbitRequirement(VarbitID.VMQ4_MOON_PUZZLE_PROGRESS, 2);
		completedMoonPuzzle = new VarbitRequirement(VarbitID.VMQ4_MOON_PUZZLE_PROGRESS, 3);
		northPlatformSolutionKnown = new ManualRequirement();
		southPlatformSolutionKnown = new ManualRequirement();

		is72Agility = new SkillRequirement(Skill.AGILITY, 72, true);

		notInspectedRalosPillar = not(new VarbitRequirement(VarbitID.VMQ4_FINAL_CHAMBER_RALOS_INSPECT, 1));
		notInspectedRanulPillar = not(new VarbitRequirement(VarbitID.VMQ4_FINAL_CHAMBER_RANUL_INSPECT, 1));
		notInspectedSkeleton = not(new VarbitRequirement(VarbitID.VMQ4_FINAL_CHAMBER_TABLET_INSPECT, 1));
		notInspectedDoor = not(new VarbitRequirement(VarbitID.VMQ4_FINAL_CHAMBER_DOOR_INSPECT, 1));
		quetzalMadeSalvagerOverlook = new VarbitRequirement(VarbitID.QUETZAL_SALVAGEROVERLOOK, 1);
		hasAtesAndActivatedTeleport = and(pendant.alsoCheckBank(), new VarbitRequirement(VarbitID.PENDANT_OF_ATES_TWILIGHT_FOUND, 1, Operation.GREATER_EQUAL));
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.VMQ3_SERVIUS_PALACE, new WorldPoint(1681, 3168, 0), "Talk to Servius in the Sunrise Palace in Civitas illa " +
				"Fortis to start the quest.");
		startQuest.addDialogStep("Yes.");

		goToTempleWithAtes = new DetailedQuestStep(this, new WorldPoint(1657, 3231, 0), "Use the pendant of ates  to the Twilight Temple, or go there via" +
				" Quetzal.", pendantToTwilight.highlighted());
		goToTempleWithAtes.addWidgetHighlight(new WidgetHighlight(InterfaceID.PendantOfAtes.TELEPORT_TWILIGHT, true).withModelRequirement(54541));

		goToTempleFromSalvager = new DetailedQuestStep(this, new WorldPoint(1657, 3231, 0), "Take a quetzal to the Salvager Overlook and run south to the " +
				"Twilight Temple.");
		goToTempleFromSalvager.addWidgetHighlight(new WidgetHighlight(InterfaceID.QuetzalMenu.ICONS, true).withModelRequirement(54546));
		((DetailedQuestStep) goToTempleFromSalvager).setLinePoints(List.of(
				new WorldPoint(1613, 3299, 0),
				new WorldPoint(1630, 3293, 0),
				new WorldPoint(1644, 3279, 0),
				new WorldPoint(1644, 3263, 0),
				new WorldPoint(1639, 3254, 0),
				new WorldPoint(1638, 3244, 0),
				new WorldPoint(1644, 3240, 0),
				new WorldPoint(1649, 3240, 0),
				new WorldPoint(1657, 3231, 0)
		));
		goToTempleFromGorge = new DetailedQuestStep(this, new WorldPoint(1657, 3231, 0), "Take a quetzal to the Salvager Overlook and run south to the " +
				"Twilight Temple.");
		goToTempleFromGorge.addWidgetHighlight(new WidgetHighlight(InterfaceID.QuetzalMenu.ICONS, true).withModelRequirement(54539));
		((DetailedQuestStep) goToTempleFromGorge).setLinePoints(List.of(
				new WorldPoint(1510, 3226, 0),
				new WorldPoint(1522, 3252, 0),
				new WorldPoint(1529, 3254, 0),
				new WorldPoint(1538, 3267, 0),
				new WorldPoint(1542, 3277, 0),
				new WorldPoint(1551, 3274, 0),
				new WorldPoint(1561, 3279, 0),
				new WorldPoint(1581, 3279, 0),
				new WorldPoint(1593, 3269, 0),
				new WorldPoint(1600, 3268, 0),
				new WorldPoint(1610, 3260, 0),
				new WorldPoint(1618, 3254, 0),
				new WorldPoint(1621, 3254, 0),
				new WorldPoint(1644, 3240, 0),
				new WorldPoint(1649, 3240, 0),
				new WorldPoint(1657, 3231, 0)
		));
		freeInvSlots4 = new FreeInventorySlotRequirement(4);
		freeInvSlot1 = new FreeInventorySlotRequirement(1);
		searchChestForEmissaryRobes = new ObjectStep(this, ObjectID.VMQ3_CULTIST_OUTFIT_CHEST, new WorldPoint(1638, 3217, 0), "Search the chest in the south " +
				"of the Tower of Ascension south of Salvager Overlook for some emissary robes.", freeInvSlots4);
		searchChestForEmissaryRobes.addSubSteps(goToTempleWithAtes, goToTempleFromGorge, goToTempleFromSalvager);
		searchChestForEmissaryRobes.addWidgetHighlight(new WidgetHighlight(InterfaceID.QuetzalMenu.ICONS, true).withModelRequirement(54546));
		((ObjectStep) searchChestForEmissaryRobes).addTeleport(pendant);
		enterTwilightTemple = new DetailedQuestStep(this, new WorldPoint(1687, 3247, 0), "Enter the temple south-east of Salvager Overlook.",
				emissaryRobesEquipped);

		var goDownStairsTempleBaseStep = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_STAIRS, new WorldPoint(1677, 3248, 0), "Go down the " +
				"stairs in the temple. The passphrase is 'Final' and 'Dawn'.", List.of(emissaryRobesEquipped), List.of(combatWeapon, food));
		goDownStairsTempleBaseStep.addDialogSteps("Final.", "Dawn.");
		goDownStairsTemple = new PuzzleWrapperStep(this, goDownStairsTempleBaseStep,
				new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_STAIRS, new WorldPoint(1677, 3248, 0), "Go down the " +
				"stairs in the temple. You'll need to guess the password.", List.of(emissaryRobesEquipped), List.of(combatWeapon, food)));

		enterBackroom = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_CHAMBER_ENTRY, new WorldPoint(1706, 9706, 0), "Enter the far eastern room. Avoid" +
				" the patrolling guard.");

		searchBed = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_BED_WITH_KEY, new WorldPoint(1713, 9698, 0), "Search the bed in the south room.");
		openDrawers = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_1_CLOSED, new WorldPoint(1713, 9714, 0), "Open the drawers in the north room.");
		((ObjectStep) openDrawers).addAlternateObjects(ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_1_OPEN);
		openDrawers.conditionToHideInSidebar(isSouthDrawer);

		openDrawers2 = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_2_CLOSED, new WorldPoint(1709, 9700, 0), "Open the drawers in the same room.");
		((ObjectStep) openDrawers2).addAlternateObjects(ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_2_OPEN);
		openDrawers2.conditionToHideInSidebar(not(isSouthDrawer));
		useCanvasPieceOnPicture = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_PAINTING, new WorldPoint(1719, 9706, 0), "Use canvas piece on the " +
				"painting in the east of the middle room of the eastern rooms.", canvasPiece.highlighted());
		useCanvasPieceOnPicture.addIcon(ItemID.VMQ4_PAINTING_SIGIL);
		enterPassage = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_PAINTING, new WorldPoint(1719, 9706, 0), "Enter the passage behind the painting.");
		enterPassage.addDialogSteps("Enter the passage.");
		pickBlueChest = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_CHAMBER_CHEST_CLOSED, new WorldPoint(1723, 9709, 0), "Picklock the chest in the " +
				"hidden room. Be ready for a fight afterwards.");
		fightEnforcer = new NpcStep(this, NpcID.VMQ4_TEMPLE_GUARD_BOSS_FIGHT, new WorldPoint(1712, 9706, 0), "Defeat the enforcer. You cannot use prayers" +
				". Step away each time he goes to attack, and step behind him or sideways if he says 'Traitor!' or 'Thief!'.");
		pickUpEmissaryScroll = new ItemStep(this, "Pick up the emissary scroll.", emissaryScroll);
		readEmissaryScroll = new DetailedQuestStep(this, "Read the emissary scroll.", emissaryScroll.highlighted());

		// Part 2
		climbStairsF0ToF1Palace = new ObjectStep(this, ObjectID.CIVITAS_PALACE_STAIRS_UP, new WorldPoint(1672, 3164, 0), "Go back to Civitas illa Fortis." + 
		"Climb up the stairs to the top of the Sunrise Palace to talk to the queen.");
		climbStairsF0ToF1Palace.addTeleport(civitasTeleport);
		climbStairsF1ToF2Palace = new ObjectStep(this, ObjectID.CIVITAS_PALACE_STAIRS_UP, new WorldPoint(1671, 3169, 1), "Climb up the stairs to the top of " +
				"the Sunrise Palace to talk to the queen.");
		talkToQueen = new NpcStep(this, NpcID.VMQ4_QUEEN_PALACE, new WorldPoint(1673, 3156, 2), "Talk to the queen on the top floor of the Sunrise Palace.");
		talkToQueen.addSubSteps(climbStairsF0ToF1Palace, climbStairsF1ToF2Palace);
		talkToCaptainVibia = new NpcStep(this, NpcID.VMQ4_CAPTAIN_VIBIA_OUTSIDE_HOUSE, new WorldPoint(1652, 3088, 0), "Talk to Captain Vibia south of Civitas" +
				" illa Fortis' west bank.");
		inspectWindow = new ObjectStep(this, ObjectID.VMQ4_CIVITAS_JANUS_WINDOW, new WorldPoint(1652, 3093, 0), "Inspect the window on the east side of the " +
				"house north of Captain Vibia, and then enter it.", bone);
		inspectWindow.addDialogSteps("Force open the window.");
		var giveBonesOrMeatToDogNoPuzzleWrap = new NpcStep(this, NpcID.VMQ4_JANUS_DOG, new WorldPoint(1650, 3094, 0),  "Use bones or some raw meat on the dog to calm it down" +
				".", bone.highlighted());
		giveBonesOrMeatToDogNoPuzzleWrap.addIcon(ItemID.BONES);
		giveBonesOrMeatToDog = giveBonesOrMeatToDogNoPuzzleWrap.puzzleWrapStep("Work out how to calm down the dog.");
		enterDoorCode = new ObjectStep(this, ObjectID.VMQ4_JANUS_HOUSE_PUZZLE_DOOR, new WorldPoint(1649, 3093, 0), "Pet the dog to see the code for the door." +
				" Open the door using the code 'GUS'.").puzzleWrapStep("Work out the door code.");
		openDoorWithGusCode = new ChestCodeStep(this, "GUS", 10, 0, 4, 0).puzzleWrapStep(true);
		enterDoorCode.addSubSteps(openDoorWithGusCode);
		takePotato = new ItemStep(this, "Pick up the sack of potatoes (3).", potatoes).puzzleWrapStep("Work out how to make a way to trap Janus and how to " +
				"knock him out.");
		removePotatoesFromSack = new DetailedQuestStep(this, "Empty the sack of potatoes.", potatoes.highlighted()).puzzleWrapStep(true);
		var takeKnifeNoPuzzleWrapped = new ItemStep(this, "Pick up the knife.", knife);
		takeKnife = takeKnifeNoPuzzleWrapped.puzzleWrapStep(true);
		takeCoinPurse = new ItemStep(this, "Pick up the coin purse.", coinPurseFullOrEmpty).puzzleWrapStep(true);

		goToF1Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_BOTTOM, new WorldPoint(1647, 3091, 0), "Go upstairs.").puzzleWrapStep(true);
		goDownFromF2Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_TOP, new WorldPoint(1647, 3091, 2), "Go downstairs.").puzzleWrapStep(true);
		goF1ToF2Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_MIDDLE, new WorldPoint(1647, 3091, 1), "Go to the top floor.").puzzleWrapStep(true);
		goF1ToF2Hideout.addDialogStep("Climb up.");
		useKnifeOnPottedFan = new ObjectStep(this, ObjectID.VMQ4_JANUS_HOUSE_PLANT, new WorldPoint(1650, 3095, 2), "Use the knife on the inspectable potted " +
				"fan.", knife.highlighted(), freeInvSlot1).puzzleWrapStep(true);
		useKnifeOnPottedFan.addIcon(ItemID.KNIFE);
		fillCoinPurse = new ObjectStep(this, ObjectID.VMQ4_JANUS_HOUSE_EMPTY_POT, new WorldPoint(1645, 3098, 2), "Use the empty coin purse on the plant pot " +
				"in the north-west of the roof with sand in it.", coinPurseEmpty.highlighted()).puzzleWrapStep(true);
		fillCoinPurse.addIcon(ItemID.VMQ4_JANUS_PURSE_EMPTY);
		emptyCoinPurse = new DetailedQuestStep(this, "Empty the coin purse.", coinPurse.highlighted()).puzzleWrapStep(true);
		useBranchOnCoinPurse = new DetailedQuestStep(this, "Use the branch on the coin purse.", branch.highlighted(), coinPurseWithSand.highlighted()).puzzleWrapStep(true);

		goToF0Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_MIDDLE, new WorldPoint(1647, 3091, 1), "Go to the ground floor.").puzzleWrapStep(true);
		goToF0Hideout.addDialogStep("Climb down.");
		takeKnifeNoPuzzleWrapped.addSubSteps(goToF0Hideout);
		goToF0HideoutEnd = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_MIDDLE, new WorldPoint(1647, 3091, 1), "Go to the ground floor.").puzzleWrapStep(true);
		goToF0HideoutEnd.addDialogStep("Climb down.");

		goF2ToF1HideoutEnd = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_TOP, new WorldPoint(1647, 3091, 2), "Go downstairs back to Vibia.").puzzleWrapStep(true);
		var showSackToVibiaNotPuzzleWrapped = new NpcStep(this, NpcID.VMQ4_CAPTAIN_VIBIA_INSIDE_HOUSE, new WorldPoint(1651, 3094, 0), "Show Captain Vibia the empty sack and " +
				"makeshift blackjack.", emptySack, makeshiftBlackjack);
		showSackToVibiaNotPuzzleWrapped.addDialogStep("I'll get searching.");
		showSackToVibia = showSackToVibiaNotPuzzleWrapped.puzzleWrapStep(true);
		showSackToVibiaNotPuzzleWrapped.addSubSteps(goF2ToF1HideoutEnd, goToF0HideoutEnd);

		takePotato.addSubSteps(removePotatoesFromSack, takeKnife, takeCoinPurse, goToF1Hideout, goDownFromF2Hideout, goF1ToF2Hideout, useKnifeOnPottedFan,
				fillCoinPurse, emptyCoinPurse, useBranchOnCoinPurse, goToF0Hideout, goToF0HideoutEnd, goF2ToF1HideoutEnd, showSackToVibia);

		searchBodyForKey = new NpcStep(this, NpcID.VMQ4_JANUS_HOUSE_JANUS_UNCONSCIOUS, new WorldPoint(1647, 3093, 0), "Search Janus.");
		enterTrapdoor = new ObjectStep(this, ObjectID.VMQ4_JANUS_BASEMENT_ENTRY, new WorldPoint(1643, 3092, 0), "Enter the basement in the hideout.");

		talkToQueenToGoCamTorum = new NpcStep(this, NpcID.VMQ4_QUEEN_BASEMENT, new WorldPoint(1653, 9493, 0), "Talk to the queen in the hideout basement.");
		talkToQueenToGoCamTorum.addDialogStep("Yes, let's go.");
		enterCamTorum = new ObjectStep(this, ObjectID.PMOON_TELEBOX, new WorldPoint(1436, 3129, 0), "Enter Cam Torum.");

		talkToAttala = new NpcStep(this, NpcID.CAM_TORUM_ATTALA, new WorldPoint(1442, 9550, 1), "Talk to Attala in Cam Torum's marketplace.");
		talkToServiusInCamTorum = new NpcStep(this, NpcID.VMQ4_SERVIUS_TEUMO_HOUSE, new WorldPoint(1466, 9570, 1), "Talk to Servius in the house east of the " +
				"bank in Cam Torum.");
		goUpstairsPub = new ObjectStep(this, ObjectID.STAIRS_IMCANDO01_LOWER01, new WorldPoint(1467, 9572, 1), "Go upstairs in the house.");
		takeBeer = new ItemStep(this, "Take the beer. Hop worlds if it's missing.", beer);
		goDownstairsPub = new ObjectStep(this, ObjectID.STAIRS_IMCANDO01_UPPER01, new WorldPoint(1468, 9572, 2), "Go downstairs in the house.");
		useBeerOnGalna = new NpcStep(this, NpcID.VMQ4_TEUMO_WIFE, new WorldPoint(1466, 9570, 1), "Use the beer on Galna.", beer.highlighted());
		useBeerOnGalna.addIcon(ItemID.BEER);
		enterCamTorumHouseBasement = new ObjectStep(this, ObjectID.VMQ4_TEUMO_HOUSE_STAIRS_DOWN, new WorldPoint(1470, 9571, 1), "Enter the house's basement.");
		takeBeerCabinet = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_SHELF, new WorldPoint(1464, 9569, 0), "Search the south-west cabinet for a beer.")
				.puzzleWrapStep("Solve the puzzle in the basement.");
		takeBeerCabinet.addDialogStep("Beer.");
		drinkBeer = new DetailedQuestStep(this, "Drink a beer.", beer.highlighted()).puzzleWrapStep(true);
		takeSteamforgeBrew = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_SHELF, new WorldPoint(1464, 9569, 0), "Search the south-west cabinet for " +
				"steamforge brew.").puzzleWrapStep(true);
		takeSteamforgeBrew.addDialogSteps("More options...", "Steamforge brew.");
		takeDwarvenStout = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_SHELF, new WorldPoint(1464, 9569, 0), "Search the south-west cabinet for dwarven" +
				" stout.").puzzleWrapStep(true);
		takeDwarvenStout.addDialogSteps("Previous options...", "Dwarven stout.");
		takeWizardsMindBomb = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_SHELF, new WorldPoint(1464, 9569, 0), "Search the south-west cabinet for " +
				"wizard's mind bomb.").puzzleWrapStep(true);
		takeWizardsMindBomb.addDialogSteps("More options...", "Wizard's mind bomb.");
		placeSteamforgedBrew = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_BARREL_3, new WorldPoint(1464, 9568, 0), "Place steamforge brew on the " +
				"south-west barrel.", steamforgedBrew.highlighted()).puzzleWrapStep(true);
		placeSteamforgedBrew.addIcon(ItemID.STEAMFORGE_BREW);
		placeDwarvenStout = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_BARREL_4, new WorldPoint(1466, 9568, 0), "Place dwarven stout on the " +
				"south barrel.", dwarvenStout.highlighted()).puzzleWrapStep(true);
		placeDwarvenStout.addIcon(ItemID.DWARVEN_STOUT);

		var nonPuzzleTakeBeerFromBarrel = new ItemStep(this, new WorldPoint(1469, 9571, 0), "Take the beer from on top of the barrel near the staircase.",
				beer);
		nonPuzzleTakeBeerFromBarrel.setOnlyHighlightItemsOnTile(true);
		takeBeerFromBarrel = nonPuzzleTakeBeerFromBarrel.puzzleWrapStep(true);
		placeBeer = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_BARREL_2, new WorldPoint(1466, 9573, 0), "Place beer on the " +
				"north barrel.", beer.highlighted()).puzzleWrapStep(true);
		placeBeer.addIcon(ItemID.BEER);

		placeEmptyGlass = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_BARREL_1, new WorldPoint(1469, 9571, 0), "Place empty glass on the " +
				"east barrel.", emptyGlass.highlighted()).puzzleWrapStep(true);
		placeEmptyGlass.addIcon(ItemID.BEER_GLASS);
		placeMindBomb = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_BARREL_5, new WorldPoint(1471, 9567, 0), "Place wizard's mind bomb on the " +
				"south-east barrel.", wizardsMindBomb.highlighted()).puzzleWrapStep(true);
		placeMindBomb.addIcon(ItemID.WIZARDS_MIND_BOMB);

		takeBeerCabinet.addSubSteps(drinkBeer, takeSteamforgeBrew, takeDwarvenStout, takeWizardsMindBomb, placeSteamforgedBrew, placeDwarvenStout,
				takeBeerFromBarrel, placeBeer, placeEmptyGlass, placeMindBomb);

		inspectFireplace = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_FIRE_OUT, new WorldPoint(1463, 9571, 0), "Inspect the fireplace.");
		inspectFireplace.addDialogStep("Pull it.");
		useHole = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_SECRET_PASSAGE_ENTRY, new WorldPoint(1470, 9565, 0), "Enter the hole in the south-east " +
				"corner of the room.");
		watchCutsceneCamTorum = new DetailedQuestStep(this, "Watch the cutscene with Ennius.");
		returnThroughHole = new ObjectStep(this, ObjectID.VMQ4_TEUMO_BASEMENT_SECRET_PASSAGE_EXIT, new WorldPoint(1470, 9563, 0), "Go back through the hole " +
				"into the Teumo's basement.");
		returnToServius = new NpcStep(this, NpcID.VMQ4_SERVIUS_TEUMO_HOUSE_DOWNSTAIRS, new WorldPoint(1468, 9569, 0), "Talk to Servius in the house basement.");

		// Neypotzli section
		climbUpFromTeumoBasement = new ObjectStep(this, ObjectID.VMQ4_TEUMO_HOUSE_STAIRS_UP, new WorldPoint(1468, 9573, 0), "Go to Neypotzli.");
		enterNeypotzli = new ObjectStep(this, ObjectID.PMOON_TELEBOX, new WorldPoint(1439, 9600, 1),
			"Enter the Neypotzli entrance in the far north of the cavern.");
		enterNeypotzli.addSubSteps(climbUpFromTeumoBasement);
		talkToEyatalli = new NpcStep(this, NpcID.VMQ4_EYATLALLI, new WorldPoint(1440, 9628, 1),
				"Talk to Eyatlalli.");

		locateKeystone = new DetailedQuestStep(this, "Use the keystone fragment to locate the keystone.", keystoneFragment.highlighted());

		enterStreamboundCavern = new ObjectStep(this, ObjectID.PMOON_TELEBOX_DIAGONAL, new WorldPoint(1458, 9650, 1),
				"Enter the north-east entrance to the streambound cavern.").puzzleWrapStep(locateKeystone);
		locateInStreambound = new TileStep(this, new WorldPoint(1511, 9702, 0), "Locate with the keystone fragment on the marked tile north of the " +
				"cooking stove.", keystoneFragment.highlighted()).puzzleWrapStep(locateKeystone, true);
		enterEarthboundCavernFromStreambound = new ObjectStep(this, ObjectID.PMOON_TELEBOX_CAVE, new WorldPoint(1522, 9720, 0), "Enter the earthbound cavern " +
				"via the north cave entrance.", keystoneFragment).puzzleWrapStep(locateKeystone, true);
		enterEarthboundCavern = new ObjectStep(this, ObjectID.PMOON_TELEBOX_DIAGONAL, new WorldPoint(1421, 9650, 1),
				"Enter the north-west entrance.").puzzleWrapStep(locateKeystone, true);
		enterEarthboundCavernFromStreambound.addSubSteps(enterEarthboundCavern);
		locateInEarthbound = new DetailedQuestStep(this, new WorldPoint(1375, 9684, 0), "Locate with the keystone fragment on the marked tile near where you " +
				"trap lizards.",
				keystoneFragment.highlighted()).puzzleWrapStep(locateKeystone, true);
		enterAncientPrison = new ObjectStep(this, ObjectID.PMOON_TELEBOX_DIAGONAL, new WorldPoint(1421, 9613, 1),
				"Enter the south-west entrance.", keystoneFragment).puzzleWrapStep(locateKeystone, true);
		enterAncientPrisonFromEarthbound = new ObjectStep(this, ObjectID.PMOON_TELEBOX_3X3, new WorldPoint(1374, 9665, 0), "Enter the ancient prison via the " +
				"large entrance in the south-west of the area.", keystoneFragment).puzzleWrapStep(locateKeystone, true);
		enterAncientPrisonFromEarthbound.addSubSteps(enterAncientPrison);
		locateInAncientPrison = new DetailedQuestStep(this, new WorldPoint(1373, 9539, 0), "Locate with the keystone fragment on the marked tile in the " +
				"south-east room with a statue.", keystoneFragment.highlighted()).puzzleWrapStep(locateKeystone, true);
		touchGlowingSymbol = new ObjectStep(this, ObjectID.VMQ4_KEYSTONE_CHAMBER_ENTRANCE, new WorldPoint(1375, 9537, 0), "Touch the glowing symbol that " +
				"appeared. Be ready for a fight.", combatGear);
		defeatCultists = new NpcStep(this, NpcID.VMQ4_KEYSTONE_CHAMBER_BOSS_MAGIC, new WorldPoint(1364, 9514, 0), "Defeat the cultists. Step into golden " +
				"circle to avoid the mage's special. Avoid the arrow barrage from the ranger. When you defeat one of the cultists, the other will become " +
				"enraged.",	true);
		((NpcStep) defeatCultists).addAlternateNpcs(NpcID.VMQ4_KEYSTONE_CHAMBER_BOSS_MAGIC_CS, NpcID.VMQ4_KEYSTONE_CHAMBER_BOSS_RANGED,
				NpcID.VMQ4_KEYSTONE_CHAMBER_BOSS_RANGED_CS);
		talkToAttalaAfterCultistFight = new NpcStep(this, NpcID.VMQ4_ATTALA_KEYSTONE_CHAMBER, new WorldPoint(1363, 9516, 0), "Talk to Attala.");

		talkToServiusAtTalTeklan = new NpcStep(this, NpcID.VMQ4_SERVIUS_VIS, new WorldPoint(1236, 3105, 0), "Talk to Servius in Tal Teklan in the " +
				"Tlati Rainforest, in the north-west of Varlamore. The easiest way here is via quetzal.", List.of(combatGear, food, prayerPotions), List.of(rangedGear));
		talkToServiusAtTalTeklan.addWidgetHighlight(new WidgetHighlight(InterfaceID.QuetzalMenu.ICONS, true).withModelRequirement(56665));

		enterTonaliCavern = new ObjectStep(this, ObjectID.VMQ4_CRYPT_OF_TONALI_ENTRY, new WorldPoint(1305, 3034, 0), "Enter the passageway in the" +
				" tree south-east of Tal Teklan, into the Crypt of Tonali.");
		defeatFinalCultists = new NpcStep(this, NpcID.VMQ4_CRYPT_ATTACKER_MELEE_VARIANT_1A, new WorldPoint(1312, 9355, 2), "Defeat the attacking cultists.",
				true);
		((NpcStep) defeatFinalCultists).addAlternateNpcs(NpcID.VMQ4_CRYPT_ATTACKER_MELEE_VARIANT_1B, NpcID.VMQ4_CRYPT_ATTACKER_MELEE_VARIANT_2A,
				NpcID.VMQ4_CRYPT_ATTACKER_MELEE_VARIANT_2B, NpcID.VMQ4_CRYPT_ATTACKER_MAGIC_VARIANT_1, NpcID.VMQ4_CRYPT_ATTACKER_MAGIC_VARIANT_2,
				NpcID.VMQ4_CRYPT_ATTACKER_RANGED_VARIANT_1, NpcID.VMQ4_CRYPT_ATTACKER_RANGED_VARIANT_2);
		fightEnnius = new NpcStep(this, NpcID.VMQ4_CRYPT_ENNIUS_BOSS, new WorldPoint(1336, 9355, 2), "Defeat Ennius. Protect from Melee. Stand on the " +
				"circles to avoid damage when they appear. Avoid the lines of yellow star icons on the floor when they appear. When they reach 0 health, " +
				"they'll gain some back and do more damage.");
		tonaliGoDownStairsF2ToF1 = new ObjectStep(this, ObjectID.VMQ4_CRYPT_STAIRS_TOP_ENNIUS, new WorldPoint(1335, 9360, 2), "Climb down the stairs.");
		tonaliGoDownStairsF1ToF0 = new ObjectStep(this, ObjectID.VMQ4_CRYPT_STAIRS_TOP_ENNIUS, new WorldPoint(1332, 9367, 1), "Climb down the stairs.");
		tonaliGoDownStairsF2ToF1.addSubSteps(tonaliGoDownStairsF1ToF0);
		useRedTeleporter = new ObjectStep(this, ObjectID.VMQ4_SUN_TELEPORT, new WorldPoint(1332, 9382, 0), "Step onto the red teleporter to the north-east.");
		useBlueTeleporter = new ObjectStep(this, ObjectID.VMQ4_MOON_TELEPORT, new WorldPoint(1319, 9384, 1), "Step onto the blue teleporter to the west.");

		useRedTeleporter2 = new ObjectStep(this, ObjectID.VMQ4_SUN_TELEPORT, new WorldPoint(1303, 9389, 0), "Step on the red teleporter to the west.");
		useBlueTeleporterLizards = new ObjectStep(this, ObjectID.VMQ4_MOON_TELEPORT, new WorldPoint(1293, 9394, 1), "Step onto the blue teleporter to the " +
				"north-west.");
		useRedTeleporter3 = new ObjectStep(this, ObjectID.VMQ4_SUN_TELEPORT, new WorldPoint(1296, 9402, 0), "Step on the red teleporter to the north.");


		// Unused for now
		crossLog = new ObjectStep(this, ObjectID.VMQ4_CRYPT_LOG_BALANCE_1, new WorldPoint(1302, 9398, 0), "Walk across the log balance to the north.");
		useBlueTeleporter2 = new ObjectStep(this, ObjectID.VMQ4_MOON_TELEPORT, new WorldPoint(1315, 9409, 0), "Step onto the blue teleporter to the north.");
		climbRope = new ObjectStep(this, ObjectID.VMQ4_CRYPT_SHORTCUT_2_BOTTOM, new WorldPoint(1308, 9420, 1), "Climb the rope up to the north-east.");
		activateStrangePlatform = new ObjectStep(this, ObjectID.VMQ4_CRYPT_LIFT, new WorldPoint(1311, 9428, 2), "Inspect the strange platform nearby to " +
				"activate a shortcut lift from the surface.");
		enterTonaliWithLift = new ObjectStep(this, ObjectID.VMQ4_CRYPT_LIFT_SURFACE, new WorldPoint(1310, 3103, 0), "Go down the lift north of the Crypt of " +
				"Tonali in the Tlati rainforest.");
		enterTonaliCavern.addSubSteps(enterTonaliWithLift);
		descendIntoSunPuzzle = new ObjectStep(this, ObjectID.VMQ4_SUN_TELEPORT, new WorldPoint(1316, 9446, 2), "Step on the red teleporter to the north-east " +
				"of the lift.");
		getEssenceFromUrns = new ObjectStep(this, ObjectID.VMQ4_SUN_PUZZLE_URN, new WorldPoint(1323, 9449, 1), "Search the urns in the area for some essence" +
				".", true).puzzleWrapStep(true);
		inspectSunStatue = new ObjectStep(this, ObjectID.VMQ4_SUN_PUZZLE_STATUE, new WorldPoint(1330, 9446, 1), "Inspect the statue in the middle of the room.").puzzleWrapStep(true);
		var solveSunPuzzleNoPuzzleWrap = new DetailedQuestStep(this, "Look at the pillar in the middle of the room. The first word " +
				"indicates where to tell Itzla to stand, " +
				"and the second word where you craft the essence." +
				"1 is the north-east altar, and incrementing numbers rotate clockwise. The words mean the following numbers: \n    Oma = 2\n" +
				"    Naui = 4\n" +
				"    Kuli = 5\n" +
				"    Chaki = 6\n" +
				"    Koma = 7\n" +
				"    Ueai = 8\n" +
				"    Makti = 10");
		solveSunPuzzle = solveSunPuzzleNoPuzzleWrap.puzzleWrapStep("Solve the sun puzzle.");

		// Can get the text from statue with `Messagebox.TEXT`
		solveSunPuzzle1Step1MoveItzla = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1327, 9446, 1), "Tell Itzla to stand in the east" +
				" of the room.").puzzleWrapStep(true);
		solveSunPuzzle1Step1MoveItzla.addDialogSteps("Can you go to an altar for me?", "East.", "Previous options...");
		solveSunPuzzle1Step1Craft = new ObjectStep(this, ObjectID.VMQ4_SUN_ALTAR, new WorldPoint(1320, 9446, 1), "Imbue the essence on the west altar.",
			essence).puzzleWrapStep(true);

		solveSunPuzzle1Step2MoveItzla = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1327, 9446, 1), "Tell Itzla to stand in the north" +
				" of the room.").puzzleWrapStep(true);
		solveSunPuzzle1Step2MoveItzla.addDialogSteps("Can you go to an altar for me?", "North.", "Previous options...");
		solveSunPuzzle1Step2Craft = new ObjectStep(this, ObjectID.VMQ4_SUN_ALTAR, new WorldPoint(1330, 9436, 1), "Imbue the essence on the south altar.",
				essence).puzzleWrapStep(true);

		solveSunPuzzle1Step3MoveItzla = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1327, 9446, 1), "Tell Itzla to stand in the " +
				"north-west of the room.").puzzleWrapStep(true);
		solveSunPuzzle1Step3MoveItzla.addDialogSteps("Can you go to an altar for me?", "North west.", "More options...");
		solveSunPuzzle1Step3Craft = new ObjectStep(this, ObjectID.VMQ4_SUN_ALTAR, new WorldPoint(1322, 9438, 1), "Imbue the essence on the south-west altar.",
				essence).puzzleWrapStep(true);

		solveSunPuzzle2Step1MoveItzla = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1327, 9446, 1), "Tell Itzla to stand in the north" +
				" of the room.").puzzleWrapStep(true);
		solveSunPuzzle2Step1MoveItzla.addDialogSteps("Can you go to an altar for me?", "North.", "Previous options...");
		solveSunPuzzle2Step1Craft = new ObjectStep(this, ObjectID.VMQ4_SUN_ALTAR, new WorldPoint(1330, 9436, 1), "Imbue the essence on the south altar.",
				essence).puzzleWrapStep(true);

		solveSunPuzzle2Step2MoveItzla = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1327, 9446, 1), "Tell Itzla to stand in the east" +
				" of the room.").puzzleWrapStep(true);
		solveSunPuzzle2Step2MoveItzla.addDialogSteps("Can you go to an altar for me?", "East.", "Previous options...");
		solveSunPuzzle2Step2Craft = new ObjectStep(this, ObjectID.VMQ4_SUN_ALTAR, new WorldPoint(1320, 9446, 1), "Imbue the essence on the west altar.",
				essence).puzzleWrapStep(true);

		solveSunPuzzle2Step3MoveItzla = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_PUZZLE_SUN, new WorldPoint(1327, 9446, 1), "Tell Itzla to stand in the " +
				"north-west of the room.").puzzleWrapStep(true);
		solveSunPuzzle2Step3MoveItzla.addDialogSteps("Can you go to an altar for me?", "North west.", "More options...");
		solveSunPuzzle2Step3Craft = new ObjectStep(this, ObjectID.VMQ4_SUN_ALTAR, new WorldPoint(1322, 9438, 1), "Imbue the essence on the south-west altar.",
				essence).puzzleWrapStep(true);
		solveSunPuzzleNoPuzzleWrap.addSubSteps(inspectSunStatue, solveSunPuzzle1Step1MoveItzla, solveSunPuzzle1Step1Craft, solveSunPuzzle1Step2MoveItzla, solveSunPuzzle1Step2Craft,
				solveSunPuzzle1Step3MoveItzla, solveSunPuzzle1Step3Craft, solveSunPuzzle2Step1Craft, solveSunPuzzle2Step1MoveItzla,
				solveSunPuzzle2Step2MoveItzla, solveSunPuzzle2Step2Craft, solveSunPuzzle2Step3MoveItzla, solveSunPuzzle2Step3Craft);
		solveSunPuzzle.addSubSteps(inspectSunStatue, solveSunPuzzle1Step1MoveItzla, solveSunPuzzle1Step1Craft, solveSunPuzzle1Step2MoveItzla, solveSunPuzzle1Step2Craft,
				solveSunPuzzle1Step3MoveItzla, solveSunPuzzle1Step3Craft, solveSunPuzzle2Step1Craft, solveSunPuzzle2Step1MoveItzla,
				solveSunPuzzle2Step2MoveItzla, solveSunPuzzle2Step2Craft, solveSunPuzzle2Step3MoveItzla, solveSunPuzzle2Step3Craft);

		goUpFromSunPuzzle = new ObjectStep(this, ObjectID.VMQ4_SUN_TELEPORT, new WorldPoint(1323, 9443, 1), "Go back up to the main area.");
		enterMoonPuzzle = new ObjectStep(this, ObjectID.VMQ4_MOON_TELEPORT, new WorldPoint(1304, 9446, 2), "Go through the blue teleport to the west.");

		moveItzlaNorth = new ObjectStep(this, ObjectID.VMQ4_MOON_PUZZLE_PLATFORM_1, new WorldPoint(1298, 9448, 1), "Move-Itzla to the platform north of him " +
				"by clicking on it to see which torches around the room light up.");
		moveItzlaSouth = new ObjectStep(this, ObjectID.VMQ4_MOON_PUZZLE_PLATFORM_2, new WorldPoint(1298, 9444, 1), "Move-Itzla to the platform south of him " +
				"by clicking on it to see which torches around the room light up.");
		pullTreeRoots = new ObjectStep(this, ObjectID.VMQ4_MOON_PUZZLE_ROOT, new WorldPoint(1285, 9452, 1), "Pull some tree roots.",	true)
				.puzzleWrapStep("Solve the moon puzzle.");
		getKnifeBlade = new ObjectStep(this, ObjectID.VMQ4_MOON_PUZZLE_OLD_TOOLS, new WorldPoint(1292, 9457, 1),
				"Search the old tools for a knife blade. Avoid the lines of fire by having Itzla go on the north platform to remove dark flames, and the " +
						"south one for light flames.").puzzleWrapStep(true);
		fletchRoots = new DetailedQuestStep(this, "Fletch the roots into kindling.", roots.highlighted()).puzzleWrapStep(true);
		placeRoots = new ObjectStep(this, ObjectID.VMQ4_MOON_PUZZLE_STATUE, new WorldPoint(1283, 9446, 1), "Have Itzla move between both the north and south " +
				"platforms to see how many braziers are lit around the room total. Put that many into the statue.",	kindling).puzzleWrapStep(true);
		repeatMoonPuzzleThreeTimes = new DetailedQuestStep(this, "Repeat the kindling burning matching the total braziers lit two more times.").puzzleWrapStep(true);
		pullTreeRoots.addSubSteps(moveItzlaNorth, moveItzlaSouth, pullTreeRoots, getKnifeBlade, fletchRoots, placeRoots, repeatMoonPuzzleThreeTimes);
		leaveMoonPuzzleRoom = new ObjectStep(this, ObjectID.VMQ4_MOON_TELEPORT, new WorldPoint(1299, 9455, 1), "Leave the moon puzzle room.");

		enterFinalBossArea = new ObjectStep(this, ObjectID.VMQ4_CRYPT_DOOR_TO_MOKI, new WorldPoint(1311, 9468, 2), "Try to open the door to the " +
				"north. Be ready for the final boss!", rangedGear);
		approachMetzli = new NpcStep(this, NpcID.VMQ4_CRYPT_METZLI_NOOPS, new WorldPoint(1311, 9497, 1), "Approach Augur Metzli, ready for a fight.");
		defeatFinalBoss = new NpcStep(this, NpcID.VMQ4_METZLI_BOSS, new WorldPoint(1311, 9497, 1), "Defeat Metzli. Read the sidebar for more details.");
		defeatFinalBossSidebar = new NpcStep(this, NpcID.VMQ4_METZLI_BOSS, new WorldPoint(1311, 9497, 1), "Defeat Metzli." +
				"\n\nStart with Protect from Missiles." +
				"\n\nUse the gaps in the wave attacks to dodge the walls as they approach. " +
				"\n\nIf circles appear, stand where they appeared." +
				"\n\nEvery time a teleporter appears to jump over a wave, the boss will switch attack styles alternating between mage and ranged. " +
				"\n\nThe boss will enter an enrage phase, it is much easier to range but melee is still possible. " +
				"\n\nAttack the boss and then immediately click on the next teleporter to avoid taking damage.");
		defeatFinalBossSidebar.addSubSteps(defeatFinalBoss);

		watchFinalBossAfterCutscene = new NpcStep(this, NpcID.VMQ4_MOKI_METZLI_FIGHT_DEFEATED_NOOPS, new WorldPoint(1311, 9497, 1), "Watch Metzli's cutscene.");

		goToNorthOfFinalAreaAgilityShortcut = new ObjectStep(this, ObjectID.MOKI_ENTRANCE_TO_DOM_BOSS, new WorldPoint(1311, 9533, 1), "Enter the entrance in the north of " +
			"the area.");
		((ObjectStep) goToNorthOfFinalAreaAgilityShortcut).setLinePoints(List.of(
				new WorldPoint(1310, 9497, 1),
				new WorldPoint(1310, 9510, 1),
				new WorldPoint(1310, 9520, 1),
				new WorldPoint(1311, 9531, 1)
		));

		goToNorthOfFinalArea = new ObjectStep(this, ObjectID.MOKI_ENTRANCE_TO_DOM_BOSS, new WorldPoint(1311, 9533, 1), "Enter the entrance in the north of " +
				"the area.");
		((ObjectStep) goToNorthOfFinalArea).setLinePoints(List.of(
				new WorldPoint(1310, 9497, 1),
				new WorldPoint(1304, 9497, 1),
				new WorldPoint(1300, 9497, 0),
				new WorldPoint(1287, 9497, 0),
				new WorldPoint(1283, 9497, 1),
				new WorldPoint(1283, 9513, 1),
				new WorldPoint(1311, 9513, 1),
				new WorldPoint(1311, 9531, 1)
		));
		goToNorthOfFinalArea.addSubSteps(goToNorthOfFinalAreaAgilityShortcut);

		inspectRanulPillar = new ObjectStep(this, ObjectID.VMQ4_MOKI_MEMORIAL_RANUL, new WorldPoint(1317, 9527, 1), "Inspect the ranul pillar south-east " +
				"of the north door.");
		inspectRalosPillar = new ObjectStep(this, ObjectID.VMQ4_MOKI_MEMORIAL_RALOS, new WorldPoint(1304, 9527, 1), "Inspect the ralos pillar " +
				"south-west of the north door.");
		inspectDoor = new ObjectStep(this, ObjectID.MOKI_ENTRANCE_TO_DOM_BOSS, new WorldPoint(1311, 9533, 1), "Inspect the entrance in the north of " +
				"the area again.");
		inspectSkeleton = new ObjectStep(this, ObjectID.VMQ4_MOKI_SKELETON_TABLET, new WorldPoint(1307, 9532, 1), "Inspect the skeleton west of the north " +
				"door.");
		readStoneTablet = new DetailedQuestStep(this, "Read the stone tablet.", stoneTablet.highlighted());

		finishQuest = new NpcStep(this, NpcID.VMQ4_ITZLA_CRYPT_DONE, new WorldPoint(1315, 9355, 2), "Talk to Prince Itzla Arkan to complete the quest!");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			bone, combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			rangedGear, food, prayerPotions, pendant, whistle, civitasTeleport
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
				new QuestRequirement(QuestHelperQuest.THE_HEART_OF_DARKNESS, QuestState.FINISHED),
				new QuestRequirement(QuestHelperQuest.PERILOUS_MOON, QuestState.FINISHED),
				new SkillRequirement(Skill.THIEVING, 66),
				new SkillRequirement(Skill.FLETCHING, 52),
				new SkillRequirement(Skill.RUNECRAFT, 52)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Emissary Enforcer (lvl-196)",
			"Chimalli (lvl-160) and Lucius (lvl-160)",
			"Multiple waves of Twilight Emissaries (lvl-70 to lvl-90)",
			"Ennius Tullus (lvl-306)",
			"Augur Metzli (lvl-396)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 55000),
			new ExperienceReward(Skill.FLETCHING, 25000),
			new ExperienceReward(Skill.RUNECRAFT, 25000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The Arkan Blade"),
			new UnlockReward("Access to Mokhaiotl"),
			new UnlockReward("Access to Crypt of Tonali")
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("55,000 Experience Lamps (Combat Skills)", ItemID.VMQ4_REWARD_LAMP, 1),
			new ItemReward("Arkan blade", ItemID.ARKAN_BLADE)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Starting off", List.of(
			startQuest, searchChestForEmissaryRobes, enterTwilightTemple, goDownStairsTemple, enterBackroom, searchBed, openDrawers, openDrawers2,
				useCanvasPieceOnPicture, enterPassage, pickBlueChest, fightEnforcer, pickUpEmissaryScroll, readEmissaryScroll
		), List.of(
			combatWeapon, food
		), List.of(
			civitasTeleport
		)));
		panels.add(new PanelDetails("The hideout", List.of(talkToQueen, talkToCaptainVibia, inspectWindow, giveBonesOrMeatToDog, enterDoorCode, takePotato,
				takeKnife, goToF1Hideout, takeCoinPurse, goF1ToF2Hideout, useKnifeOnPottedFan, fillCoinPurse, useBranchOnCoinPurse, showSackToVibia,
				searchBodyForKey, enterTrapdoor, talkToQueenToGoCamTorum),
				List.of(bone),
				List.of()));
		panels.add(new PanelDetails("The dwarves", List.of(enterCamTorum, talkToAttala, talkToServiusInCamTorum, goUpstairsPub, takeBeer, goDownstairsPub,
				useBeerOnGalna, enterCamTorumHouseBasement, takeWizardsMindBomb, placeMindBomb, takeBeerCabinet,
				placeBeer, takeSteamforgeBrew, placeSteamforgedBrew, takeDwarvenStout, placeDwarvenStout,takeBeerFromBarrel, drinkBeer, placeEmptyGlass,
				inspectFireplace, useHole, watchCutsceneCamTorum, returnThroughHole, returnToServius),
				List.of(),
				List.of()));
		panels.add(new PanelDetails("Ancient keys", List.of(enterNeypotzli, talkToEyatalli, enterStreamboundCavern, locateInStreambound,
				enterEarthboundCavernFromStreambound, locateInEarthbound, enterAncientPrisonFromEarthbound, locateInAncientPrison, touchGlowingSymbol,
				defeatCultists, talkToAttalaAfterCultistFight),
				List.of(combatGear, food, prayerPotions)));

		panels.add(new PanelDetails("Crypt of Tonali", List.of(talkToServiusAtTalTeklan, enterTonaliCavern, defeatFinalCultists, fightEnnius,
				tonaliGoDownStairsF2ToF1, useRedTeleporter, useBlueTeleporter, useRedTeleporter2, useBlueTeleporterLizards, useRedTeleporter3, climbRope,
				activateStrangePlatform, descendIntoSunPuzzle, inspectSunStatue, getEssenceFromUrns, solveSunPuzzle, goUpFromSunPuzzle, enterMoonPuzzle,
				moveItzlaNorth, moveItzlaSouth, pullTreeRoots, getKnifeBlade, fletchRoots, placeRoots, repeatMoonPuzzleThreeTimes, leaveMoonPuzzleRoom),
				List.of(combatGear, food, prayerPotions)));

		panels.add(new PanelDetails("Doom", List.of(enterFinalBossArea, approachMetzli, defeatFinalBossSidebar, watchFinalBossAfterCutscene,
				goToNorthOfFinalArea, inspectRalosPillar, inspectRanulPillar, inspectDoor, inspectSkeleton, readStoneTablet, finishQuest),
				List.of(rangedGear, food, prayerPotions)));
		return panels;
	}
}
