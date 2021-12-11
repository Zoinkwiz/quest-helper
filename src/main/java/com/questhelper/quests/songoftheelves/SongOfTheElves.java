/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.songoftheelves;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.EmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.emote.QuestEmote;
import java.util.ArrayList;
import java.util.Arrays;
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
	quest = QuestHelperQuest.SONG_OF_THE_ELVES
)
public class SongOfTheElves extends BasicQuestHelper
{
	//Items Required
	ItemRequirement mournersOutfit, gasMask, mournerTop, mournerTrousers, mournerBoots, mournerGloves, mournerCloak, ardyFullHelm,
		ardyPlatebody, ardyPlatelegs, ardyTabard, ardyPlatelegsEquipped, ardyFullHelmEquipped, ardyPlatebodyEquipped, ropeHighlighted,
		tinderboxHighlighted, steelPlatelegsEquipped, clearLiquid, combatGear, baxKey, odeToEternityHighlighted, crystalSeed, pickaxeHighlighted;

	ItemRequirement steelFullHelm, steelPlatebody, steelPlatelegs, redDye, purpleDye, silk, runiteBar, limestoneBricks8, tinderbox, rope,
		natureRune, iritLeafOrFlowers, adamantChainbody, wineOfZamorakOrZamorakBrew, cabbage, blackKnifeOrBlackDagger, cadantineSeed, seedDibber,
		vialOfWater, pestleAndMortar, hammer, saw, pickaxe, axe, spade, hammerHighlighted, elderCadantine, elderCadantineHighlighted, crystal,
		pestleAndMortarHighlighted, vialOfWaterHighlighted, crystalDust, elderCadantineVial, inversionPotion, explosivePotion, magicCombatGear,
		saradominBrews, superRestorePotions, teleCrystal, iorwerthCampTeleport, gamesNecklace;

	NoItemRequirement nothingEquipped;

	Requirement inArdougneCastleF1, inPassF0, inPassF1, inWellEntrance, inIbanRoom, inArdyPrison, inHideout, inWestArdyInstance, inMournerBaseHQInstance,
		inEastArdyInstance, inArdougneCastleF1Instance, inLletyaF1, onHudonIsland, onDeadTreeIsland, onLedge, inFalls, inBaxThroneRoom, inIorwerthCave,
		inLletyaF0Battle, inLletyaF1Battle, inLletyaF1Damaged, inBossArea, askedAboutCrwys, askedAboutHefin, learnedHowToMakeStatue, dugNearTyras,
		dugNearStash, dugNearPrif, spunOutsideUndergroundPass;

	QuestStep talkToEdmond, goUpToLathas, talkToLathas, goDownFromLathas, talkToEdmondAgain, useRedDyeOnSteelFullHelm, talkToAlrena, talkToAlrenaNoItems,
		useTabardOnPlatebody, talkToEdmondWithOutfit, goDownstairsCastle, talkToElenaInCell, goUpFromCastleBasement, searchElenaCabinet, goDownstairsCastleAgain,
		talkToElenaInCellAgain, goUpFromCastleBasementAgain, talkToEdmondAfterFreeingElena;

	QuestStep searchBed, talkToElenaInHideout, leaveHideout, useTinderboxOnGrain, talkToPriest, useTinderboxOnChurchGrain, talkToSarah, useTinderboxOnSWGrain,
		talkToChadwell, talkToSilverMerchant, talkToBaker1, talkToBaker2, talkToGemMerchant, talkToFurTrader, talkToSpiceSeller, talkToSilkMerchant,
		talkToTownCrier, talkToZenesha, talkToEstateAgent, talkToProbita, talkToAemad, talkToPriest2, talkToOrbon;

	Requirement burnedGrain1, burnedGrain2, burnedGrain3, talkedToPriest1, talkedToSarah, talkedToChadwell, talkedToWestArdougne, talkedToSilverMerchant,
		talkedToBaker1, talkedToBaker2, talkedToGemMerchant, talkedToFurTrader, talkedToSpiceSeller, talkedToSilkMerchant, talkedToMarketStalls, talkedToTownCrier,
		talkedToZenesha, talkedToEstateAgent, talkedToProbita, talkedToAemad, talkedToPriest2, talkedToOrbon, askedAboutAmlodd, askedAboutTrahaearn,
		gottenTeleportCrystal, inValley, clearedTraBlockage, inTraRoom, repairedExo, inLightPuzzle, inLibraryF0, inLibraryF1, inLibraryF2,
		foundHefin, revealedCrwys, tracked1, tracked2, tracked3, tracked4, tracked5, foundEoin, askedAboutIthell, askedAboutMeilyr, checkedSymbol1,
		checkedSymbol2, checkedSymbol3, checkedSymbol4, checkedSymbol5, builtStatue, finishedIthell, filledHole1, filledHole2, filledHole3,
		filledHole4, filledHole5, filledHole6, filledHole7;

	DetailedQuestStep searchBedAgain, talkToElenaForFight, defeat10Mourners, mournerBattleGoDownTrapdoor, defeat11Mourners, talkToElenaForEastArdyFight, defeat15Knights,
		defeat15KnightsF1, talkToLathasAfterFight, watchArdyFightCutscene;

	QuestStep talkToArianwyn, goUpToYsgawyn, talkToYsgawyn, goDownToElena, talkToElenaInLletya, talkToArianwynAfterElena, talkToArianwynAfterMeeting;

	QuestStep boardRaft, useRopeOnRock, useRopeOnTree, enterFalls, searchCrateForKey, enterBaxThroneRoom, talkToArianwynInBax, readOdeToEternity,
		pillarPuzzle, talkToBax;

	DetailedQuestStep talkToArianwynAfterBax, talkToElenaAfterBax, plantCadantine, talkToBaxInLletya, talkToElunedWithSeed, rubCrystal, talkToFigure, talkToBaxAfterValley,
		enterCaveToWell, usePickaxeOnWall, enterCrevice, useHammerOnExoskeleton, talkToTrahaearn, talkToElenaAfterTra, talkToArianwynAfterTra,
		usePestleOnCrystal, useCadantineOnVial, useDustOnVial, talkToArianwynAfterPotion, talkToArianwynAfterGivingPotion;

	QuestStep talkToAmlodd, goF0ToF1, goF1ToF0, goF2ToF1, goF1ToF2, goF1ToF2NW, goF1ToF2SW, goF1ToF2E, touchSeal, openSealOfIthell, touchIthellSeal, touchCadarnSeal,
		touchCrwysSeal, touchAmloddSeal, touchMeilyrSeal, touchHefinSeal, touchTrahaearnSeal, touchIorwerthSeal;

	ConditionalStep goToF0Steps, goToF1Steps, goToF2Steps, goToF2NWSteps, goToF2SWSteps, goToF2ESteps, goTouchFirstSeal, openIthellSeal, goTouchIthellSeal,
		goTouchCadarnSeal, goTouchCrwysSeal, goTouchAmloddSeal, goTouchMeilyrSeal, goTouchHefinSeal, goTouchTrahaearnSeal, goTouchIorwerthSeal;

	CadarnLightPuzzle cadarnPuzzle;
	CrwysLightPuzzle crwysLightPuzzle;
	AmloddLightPuzzle amloddLightPuzzle;
	MeilyrLightPuzzle meilyrLightPuzzle;
	HefinLightPuzzle hefinLightPuzzle;
	TrahaearnLightPuzzle trahaearnLightPuzzle;
	IorwerthLightPuzzle iorwerthLightPuzzle;

	QuestStep leaveLibrary, talkToArianwynAfterLightPuzzle, askBaxtorianHowToFindCrwys, askBaxtorianHowToFindHefin, chopTrees, talkToCrwys, crossSteppingStones,
		talkToBaxAfterHefinAndCrwys;

	QuestStep enterIorwerthCave, kill2IorwerthCaves, searchChestForOrb, talkToBaxWithOrb;

	QuestStep talkToElenaAfterOrb, inspectMushrooms, track1, track2, track3, track4, track5, track6, talkToYsgawynInClearing, enterLletyaForFighting;

	QuestStep searchTableForEoin, goUpLletyaLadder, searchCookingPots, fightArianwyn;

	DetailedQuestStep talkToBaxAfterLletyaFightForIthellClue, talkToBaxAfterLletyaFightForMeilyrClue, checkSymbol1, checkSymbol2, checkSymbol3,
		checkSymbol4, checkSymbol5, goToSecondFloorSymbols, buildStatue, talkToIthell, digNearSwamp, digNearSTASH, digOutsidePrif, spinOutsidePass,
		talkToMeilyr;

	QuestStep enterWellCaveForFight, enterWellOfVoyage, leaveIbanRoom, goDownToDwarves, talkToBaxAtDwarves, talkToElenaAtDwarves, fillHole1, fillHole2,
		fillHole3, fillHole4, fillHole5, fillHole6, fillHole7, talkToBaxAfterFillingHoles, defendDwarfCamp, defeatEssyllt;

	QuestStep enterWellCaveForFragmentFight, defeatFragmentOfSeren, defeatFragmentSidebar, talkToBaxToFinish;

	ConditionalStep finalBattle;

	//Zones
	Zone ardougneCastleF1, passF1, passF0, wellEntrance, ibanRoom, ardyPrison, hideout, westArdyInstance, mournerBasement, eastArdy, llyetaF1,
		hudonIsland, deadTreeIsland, ledge, falls, baxThroneRoom, valley, traRoom, lightPuzzleRoom, libraryF0, libraryF1, libraryF2, iorwerthCave,
		llyetaF0Battle, llyetaF1Battle, llyetaF1Damaged, bossArea;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToEdmond);

		ConditionalStep goTalkToKing = new ConditionalStep(this, goUpToLathas);

		goTalkToKing.addStep(inArdougneCastleF1, talkToLathas);
		steps.put(2, goTalkToKing);

		ConditionalStep goReturnToEdmond = new ConditionalStep(this, talkToEdmondAgain);
		goReturnToEdmond.addStep(inArdougneCastleF1, goDownFromLathas);
		steps.put(4, goReturnToEdmond);
		steps.put(6, goReturnToEdmond);
		steps.put(8, goReturnToEdmond);

		steps.put(10, talkToAlrena);
		steps.put(12, talkToAlrena);
		steps.put(14, talkToAlrenaNoItems);

		ConditionalStep infiltrateCastle = new ConditionalStep(this, talkToAlrenaNoItems);
		infiltrateCastle.addStep(new Conditions(ardyFullHelm, ardyPlatebody), talkToEdmondWithOutfit);
		infiltrateCastle.addStep(new Conditions(ardyPlatebody), useRedDyeOnSteelFullHelm);
		infiltrateCastle.addStep(new Conditions(ardyTabard), useTabardOnPlatebody);
		steps.put(16, infiltrateCastle);

		steps.put(18, goDownstairsCastle);

		ConditionalStep goTalkToElenaInCell = new ConditionalStep(this, goDownstairsCastle);
		goTalkToElenaInCell.addStep(inArdyPrison, talkToElenaInCell);
		steps.put(20, goTalkToElenaInCell);

		ConditionalStep goGetAcid = new ConditionalStep(this, searchElenaCabinet);
		goGetAcid.addStep(inArdyPrison, goUpFromCastleBasement);
		steps.put(22, goGetAcid);

		ConditionalStep bringAcidToElena = new ConditionalStep(this, goDownstairsCastleAgain);
		bringAcidToElena.addStep(inArdyPrison, talkToElenaInCellAgain);
		steps.put(24, bringAcidToElena);

		ConditionalStep goBackToEdmondAfterFreeingElena = new ConditionalStep(this, talkToEdmondAfterFreeingElena);
		goBackToEdmondAfterFreeingElena.addStep(inArdyPrison, goUpFromCastleBasementAgain);
		steps.put(26, goBackToEdmondAfterFreeingElena);

		steps.put(28, searchBed);
		steps.put(30, searchBed);

		ConditionalStep goTalkToElenaInHideout = new ConditionalStep(this, searchBed);
		goTalkToElenaInHideout.addStep(inHideout, talkToElenaInHideout);
		steps.put(32, goTalkToElenaInHideout);
		steps.put(34, goTalkToElenaInHideout);
		steps.put(36, goTalkToElenaInHideout);
		steps.put(38, goTalkToElenaInHideout);

		ConditionalStep riseUp = new ConditionalStep(this, useTinderboxOnGrain);
		riseUp.addStep(inHideout, leaveHideout);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls, talkedToTownCrier, talkedToZenesha, talkedToEstateAgent,
			talkedToProbita, talkedToAemad, talkedToPriest2), talkToOrbon);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls, talkedToTownCrier, talkedToZenesha, talkedToEstateAgent,
			talkedToProbita, talkedToAemad), talkToPriest2);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls, talkedToTownCrier, talkedToZenesha, talkedToEstateAgent, talkedToProbita), talkToAemad);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls, talkedToTownCrier, talkedToZenesha, talkedToEstateAgent), talkToProbita);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls, talkedToTownCrier, talkedToZenesha), talkToEstateAgent);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls, talkedToTownCrier), talkToZenesha);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToMarketStalls), talkToTownCrier);

		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToSilverMerchant, talkedToBaker2, talkedToBaker1, talkedToGemMerchant,
			talkedToFurTrader, talkedToSpiceSeller), talkToSilkMerchant);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToSilverMerchant, talkedToBaker2, talkedToBaker1, talkedToGemMerchant,
			talkedToFurTrader), talkToSpiceSeller);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToSilverMerchant, talkedToBaker2, talkedToBaker1, talkedToGemMerchant), talkToFurTrader);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToSilverMerchant, talkedToBaker2, talkedToBaker1), talkToGemMerchant);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToSilverMerchant, talkedToBaker2), talkToBaker1);
		riseUp.addStep(new Conditions(talkedToWestArdougne, talkedToSilverMerchant), talkToBaker2);
		riseUp.addStep(new Conditions(talkedToWestArdougne), talkToSilverMerchant);

		riseUp.addStep(new Conditions(burnedGrain1, talkedToPriest1, burnedGrain2, talkedToSarah, burnedGrain3), talkToChadwell);
		riseUp.addStep(new Conditions(burnedGrain1, talkedToPriest1, burnedGrain2, talkedToSarah), useTinderboxOnSWGrain);
		riseUp.addStep(new Conditions(burnedGrain1, talkedToPriest1, burnedGrain2), talkToSarah);
		riseUp.addStep(new Conditions(burnedGrain1, talkedToPriest1), useTinderboxOnChurchGrain);
		riseUp.addStep(burnedGrain1, talkToPriest);

		steps.put(40, riseUp);

		ConditionalStep goFightForArdougne = new ConditionalStep(this, searchBedAgain);
		goFightForArdougne.addStep(inWestArdyInstance, defeat10Mourners);
		goFightForArdougne.addStep(inHideout, talkToElenaForFight);
		steps.put(42, goFightForArdougne);
		steps.put(44, goFightForArdougne);

		ConditionalStep goFightInBasement = new ConditionalStep(this, searchBedAgain);
		goFightInBasement.addStep(inMournerBaseHQInstance, defeat11Mourners);
		goFightInBasement.addStep(inWestArdyInstance, mournerBattleGoDownTrapdoor);
		goFightInBasement.addStep(inHideout, talkToElenaForFight);
		steps.put(45, goFightInBasement);

		ConditionalStep goFightInEastArdougne = new ConditionalStep(this, searchBedAgain);
		goFightInEastArdougne.addStep(inArdougneCastleF1Instance, defeat15KnightsF1);
		goFightInEastArdougne.addStep(inEastArdyInstance, defeat15Knights);
		goFightInEastArdougne.addStep(inHideout, talkToElenaForEastArdyFight);
		steps.put(46, goFightInEastArdougne);
		steps.put(48, goFightInEastArdougne);

		ConditionalStep goConfrontKing = new ConditionalStep(this, searchBedAgain);
		goConfrontKing.addStep(new InInstanceRequirement(), watchArdyFightCutscene);
		goConfrontKing.addStep(inHideout, talkToLathasAfterFight);
		steps.put(50, goConfrontKing);
		steps.put(52, goConfrontKing);
		steps.put(54, goConfrontKing);

		steps.put(56, talkToArianwyn);

		ConditionalStep goTalkToYs = new ConditionalStep(this, goUpToYsgawyn);
		goTalkToYs.addStep(inLletyaF1, talkToYsgawyn);
		steps.put(58, goTalkToYs);

		ConditionalStep goTalkToElenaInLletya = new ConditionalStep(this, talkToElenaInLletya);
		goTalkToElenaInLletya.addStep(inLletyaF1, goDownToElena);
		steps.put(59, goTalkToElenaInLletya);

		steps.put(60, goTalkToElenaInLletya);

		steps.put(62, talkToArianwynAfterElena);
		steps.put(64, talkToArianwynAfterMeeting);

		ConditionalStep goEnterFalls = new ConditionalStep(this, boardRaft);
		goEnterFalls.addStep(new Conditions(inFalls, baxKey), enterBaxThroneRoom);
		goEnterFalls.addStep(inFalls, searchCrateForKey);
		goEnterFalls.addStep(onLedge, enterFalls);
		goEnterFalls.addStep(onDeadTreeIsland, useRopeOnTree);
		goEnterFalls.addStep(onHudonIsland, useRopeOnRock);

		ConditionalStep goTalkToArianwynInBax = new ConditionalStep(this, goEnterFalls);
		goTalkToArianwynInBax.addStep(inBaxThroneRoom, talkToArianwynInBax);
		steps.put(66, goTalkToArianwynInBax);

		ConditionalStep solveBaxPuzzle = new ConditionalStep(this, goEnterFalls);
		solveBaxPuzzle.addStep(inBaxThroneRoom, pillarPuzzle);
		steps.put(68, solveBaxPuzzle);

		ConditionalStep goTalkToBaxInFalls = new ConditionalStep(this, goEnterFalls);
		goTalkToBaxInFalls.addStep(inBaxThroneRoom, talkToBax);
		steps.put(70, goTalkToBaxInFalls);

		steps.put(72, talkToArianwynAfterBax);

		steps.put(74, talkToElenaAfterBax);
		steps.put(75, talkToElenaAfterBax);
		steps.put(76, plantCadantine);

		steps.put(78, talkToBaxInLletya);

		ConditionalStep findAmlodd = new ConditionalStep(this, talkToBaxInLletya);
		findAmlodd.addStep(inValley, talkToFigure);
		findAmlodd.addStep(gottenTeleportCrystal, rubCrystal);
		findAmlodd.addStep(askedAboutAmlodd, talkToElunedWithSeed);
		steps.put(80, findAmlodd); // Dialog options available

		ConditionalStep findTra = new ConditionalStep(this, talkToBaxAfterValley);
		findTra.addStep(new Conditions(inTraRoom, repairedExo), talkToTrahaearn);
		findTra.addStep(inTraRoom, useHammerOnExoskeleton);
		findTra.addStep(new Conditions(inWellEntrance, clearedTraBlockage), enterCrevice);
		findTra.addStep(inWellEntrance, usePickaxeOnWall);
		findTra.addStep(askedAboutTrahaearn, enterCaveToWell);
		steps.put(82, findTra);

		steps.put(84, talkToElenaAfterTra);
		steps.put(85, talkToElenaAfterTra);

		ConditionalStep makePotion = new ConditionalStep(this, talkToElenaAfterTra);
		makePotion.addStep(new Conditions(inversionPotion), talkToArianwynAfterPotion);
		makePotion.addStep(new Conditions(crystalDust, elderCadantineVial), useDustOnVial);
		makePotion.addStep(new Conditions(crystalDust, elderCadantine), useCadantineOnVial);
		makePotion.addStep(new Conditions(crystal, elderCadantine), usePestleOnCrystal);
		makePotion.addStep(elderCadantine, talkToArianwynAfterTra);
		steps.put(86, makePotion);

		steps.put(88, talkToArianwynAfterGivingPotion);

		steps.put(90, talkToAmlodd);

		goTouchFirstSeal = new ConditionalStep(this, goToF1Steps, "Touch the Seal of the Forgotten.");
		goTouchFirstSeal.addStep(inLibraryF1, touchSeal);
		steps.put(92, goTouchFirstSeal);
		steps.put(93, goTouchFirstSeal);

		openIthellSeal = new ConditionalStep(this, goToF1Steps,
			"Rotate the mirrors in the pillars in the south east room along the marked path until the light reaches the Seal of Ithell.");
		openIthellSeal.addStep(inLibraryF1, openSealOfIthell);
		steps.put(94, openIthellSeal);

		goTouchIthellSeal = new ConditionalStep(this, goToF1Steps, "Touch the Seal of Ithell in the south east room.");
		goTouchIthellSeal.addStep(inLibraryF1, touchIthellSeal);
		steps.put(95, goTouchIthellSeal);

		steps.put(96, cadarnPuzzle);

		goTouchCadarnSeal = new ConditionalStep(this, goToF0Steps, "Touch the Seal of Cadarn in the south of the ground floor.");
		goTouchCadarnSeal.addStep(inLibraryF0, touchCadarnSeal);
		steps.put(97, goTouchCadarnSeal);

		steps.put(98, crwysLightPuzzle);

		goTouchCrwysSeal = new ConditionalStep(this, goToF1Steps, "Touch the Seal of Crwys in the north west of the first floor.");
		goTouchCrwysSeal.addStep(inLibraryF1, touchCrwysSeal);
		steps.put(99, goTouchCrwysSeal);

		steps.put(100, amloddLightPuzzle);

		goTouchAmloddSeal = new ConditionalStep(this, goToF0Steps, "Touch the Seal of Amlodd in the middle of the bottom floor.");
		goTouchAmloddSeal.addStep(inLibraryF0, touchAmloddSeal);
		steps.put(101, goTouchAmloddSeal);

		steps.put(102, meilyrLightPuzzle);

		goTouchMeilyrSeal = new ConditionalStep(this, goToF2NWSteps, "Touch the Seal of Meilyr in the middle of the top floor.");
		goTouchMeilyrSeal.addStep(inLibraryF2, touchMeilyrSeal);
		steps.put(103, goTouchMeilyrSeal);

		steps.put(104, hefinLightPuzzle);

		goTouchHefinSeal = new ConditionalStep(this, goToF2SWSteps, "Touch the Seal of Hefin in the south west of the top floor.");
		goTouchHefinSeal.addStep(inLibraryF2, touchHefinSeal);
		steps.put(105, goTouchHefinSeal);

		steps.put(106, trahaearnLightPuzzle);

		goTouchTrahaearnSeal = new ConditionalStep(this, goToF2ESteps, "Touch the Seal of Trahaearn in the east of the top floor.");
		goTouchTrahaearnSeal.addStep(inLibraryF2, touchTrahaearnSeal);
		steps.put(107, goTouchTrahaearnSeal);

		steps.put(108, iorwerthLightPuzzle);

		goTouchIorwerthSeal = new ConditionalStep(this, goToF1Steps, "Touch the Seal of Iorwerth in the north east of the middle floor.");
		goTouchIorwerthSeal.addStep(inLibraryF1, touchIorwerthSeal);
		steps.put(109, goTouchIorwerthSeal);


		ConditionalStep goTalkToArianwynAfterLibrary = new ConditionalStep(this, talkToArianwynAfterLightPuzzle);
		goTalkToArianwynAfterLibrary.addStep(new Conditions(LogicType.OR, inLibraryF0, inLibraryF1, inLibraryF2), leaveLibrary);
		steps.put(110, goTalkToArianwynAfterLibrary);
		steps.put(112, talkToArianwynAfterLightPuzzle);

		steps.put(114, talkToBaxInLletya);

		ConditionalStep askAboutHefinAndCrwys = new ConditionalStep(this, askBaxtorianHowToFindCrwys);
		askAboutHefinAndCrwys.addStep(new Conditions(foundHefin, revealedCrwys), talkToCrwys);
		askAboutHefinAndCrwys.addStep(new Conditions(foundHefin, askedAboutCrwys), chopTrees);
		askAboutHefinAndCrwys.addStep(new Conditions(askedAboutHefin, askedAboutCrwys), crossSteppingStones);
		askAboutHefinAndCrwys.addStep(askedAboutCrwys, askBaxtorianHowToFindHefin);
		steps.put(116, askAboutHefinAndCrwys);
		steps.put(118, askAboutHefinAndCrwys);

		steps.put(120, talkToBaxAfterHefinAndCrwys);
		steps.put(122, talkToBaxAfterHefinAndCrwys);


		ConditionalStep goKillOrbGuards = new ConditionalStep(this, enterIorwerthCave);
		goKillOrbGuards.addStep(inIorwerthCave, kill2IorwerthCaves);
		steps.put(124, goKillOrbGuards);
		steps.put(126, goKillOrbGuards);

		ConditionalStep retrieveOrbFromChest = new ConditionalStep(this, enterIorwerthCave);
		retrieveOrbFromChest.addStep(inIorwerthCave, searchChestForOrb);
		steps.put(128, retrieveOrbFromChest);

		steps.put(130, talkToBaxWithOrb);
		steps.put(132, talkToBaxWithOrb);

		steps.put(134, talkToElenaAfterOrb);

		steps.put(136, inspectMushrooms);

		ConditionalStep tracking = new ConditionalStep(this, track1);
		tracking.addStep(tracked5, track6);
		tracking.addStep(tracked4, track5);
		tracking.addStep(tracked3, track4);
		tracking.addStep(tracked2, track3);
		tracking.addStep(tracked1, track2);
		steps.put(138, tracking);

		steps.put(140, talkToYsgawynInClearing);

		ConditionalStep findChildren = new ConditionalStep(this, enterLletyaForFighting);
		findChildren.addStep(new Conditions(inLletyaF1Battle, foundEoin), searchCookingPots);
		findChildren.addStep(new Conditions(inLletyaF0Battle, foundEoin), goUpLletyaLadder);
		findChildren.addStep(new Conditions(LogicType.OR, inLletyaF0Battle, inLletyaF1Battle), searchTableForEoin);
		steps.put(142, findChildren);
		steps.put(144, findChildren);

		steps.put(146, enterLletyaForFighting);
		steps.put(148, enterLletyaForFighting);
		steps.put(150, enterLletyaForFighting);

		ConditionalStep goDefeatArianwyn = new ConditionalStep(this, enterLletyaForFighting);
		goDefeatArianwyn.addStep(inLletyaF0Battle, fightArianwyn);
		steps.put(152, goDefeatArianwyn);

		steps.put(154, enterLletyaForFighting);

		steps.put(156, talkToBaxAfterLletyaFightForIthellClue);
		steps.put(158, talkToBaxAfterLletyaFightForIthellClue);

		ConditionalStep findingIthellAndMeilyr = new ConditionalStep(this, talkToBaxAfterLletyaFightForIthellClue);
		findingIthellAndMeilyr.addStep(new Conditions(finishedIthell, spunOutsideUndergroundPass), talkToMeilyr);
		findingIthellAndMeilyr.addStep(new Conditions(finishedIthell, dugNearPrif), spinOutsidePass);
		findingIthellAndMeilyr.addStep(new Conditions(finishedIthell, dugNearStash), digOutsidePrif);
		findingIthellAndMeilyr.addStep(new Conditions(finishedIthell, dugNearTyras), digNearSTASH);
		findingIthellAndMeilyr.addStep(new Conditions(finishedIthell, askedAboutMeilyr), digNearSwamp);
		findingIthellAndMeilyr.addStep(new Conditions(builtStatue, askedAboutMeilyr), talkToIthell);
		findingIthellAndMeilyr.addStep(new Conditions(learnedHowToMakeStatue, askedAboutMeilyr), buildStatue);
		findingIthellAndMeilyr.addStep(new Conditions(askedAboutIthell, askedAboutMeilyr, checkedSymbol1, checkedSymbol2,
				checkedSymbol3, checkedSymbol4, inLletyaF1Damaged), checkSymbol5);
		findingIthellAndMeilyr.addStep(new Conditions(askedAboutIthell, askedAboutMeilyr, checkedSymbol1, checkedSymbol2,
				checkedSymbol3, inLletyaF1Damaged), checkSymbol4);
		findingIthellAndMeilyr.addStep(new Conditions(askedAboutIthell, askedAboutMeilyr, checkedSymbol1, checkedSymbol2,
				checkedSymbol3), goToSecondFloorSymbols);
		findingIthellAndMeilyr.addStep(new Conditions(askedAboutIthell, askedAboutMeilyr, checkedSymbol1, checkedSymbol2), checkSymbol3);
		findingIthellAndMeilyr.addStep(new Conditions(askedAboutIthell, askedAboutMeilyr, checkedSymbol1), checkSymbol2);
		findingIthellAndMeilyr.addStep(new Conditions(askedAboutIthell, askedAboutMeilyr), checkSymbol1);
		findingIthellAndMeilyr.addStep(askedAboutIthell, talkToBaxAfterLletyaFightForMeilyrClue);
		steps.put(160, findingIthellAndMeilyr);
		steps.put(162, findingIthellAndMeilyr);

		ConditionalStep goToDwarves = new ConditionalStep(this, enterWellCaveForFight);
		goToDwarves.addStep(inPassF1, goDownToDwarves);
		goToDwarves.addStep(inIbanRoom, leaveIbanRoom);
		goToDwarves.addStep(inWellEntrance, enterWellOfVoyage);

		ConditionalStep goTalkToBaxAtDwarves = new ConditionalStep(this, goToDwarves);
		goTalkToBaxAtDwarves.addStep(inPassF0, talkToBaxAtDwarves);
		steps.put(164, goTalkToBaxAtDwarves);

		ConditionalStep goTalkToElenaAtDwarves = new ConditionalStep(this, goToDwarves);
		goTalkToElenaAtDwarves.addStep(inPassF0, talkToElenaAtDwarves);
		steps.put(166, goTalkToElenaAtDwarves);

		ConditionalStep goFillHoles = new ConditionalStep(this, goToDwarves);
		goFillHoles.addStep(new Conditions(inPassF0, filledHole1, filledHole2, filledHole3, filledHole4, filledHole5, filledHole6), fillHole7);
		goFillHoles.addStep(new Conditions(inPassF0, filledHole1, filledHole2, filledHole3, filledHole4, filledHole5), fillHole6);
		goFillHoles.addStep(new Conditions(inPassF0, filledHole1, filledHole2, filledHole3, filledHole4), fillHole5);
		goFillHoles.addStep(new Conditions(inPassF0, filledHole1, filledHole2, filledHole3), fillHole4);
		goFillHoles.addStep(new Conditions(inPassF0, filledHole1, filledHole2), fillHole3);
		goFillHoles.addStep(new Conditions(inPassF0, filledHole1), fillHole2);
		goFillHoles.addStep(inPassF0, fillHole1);
		steps.put(167, goFillHoles);

		ConditionalStep goTalkToBaxAtDwarvesAgain = new ConditionalStep(this, goToDwarves);
		goTalkToBaxAtDwarvesAgain.addStep(inPassF0, talkToBaxAfterFillingHoles);
		steps.put(168, goTalkToBaxAtDwarvesAgain);
		steps.put(170, goTalkToBaxAtDwarvesAgain);

		ConditionalStep defendPass = new ConditionalStep(this, goToDwarves);
		defendPass.addStep(inPassF0, defendDwarfCamp);
		steps.put(172, defendPass);
		steps.put(174, defendPass);

		ConditionalStep goDefeatEssyllt = new ConditionalStep(this, goToDwarves);
		goDefeatEssyllt.addStep(inPassF0, defeatEssyllt);
		steps.put(176, goDefeatEssyllt);
		steps.put(178, goDefeatEssyllt);

		steps.put(180, goToDwarves);
		steps.put(182, goToDwarves);
		steps.put(184, goToDwarves);

		finalBattle = new ConditionalStep(this, enterWellCaveForFragmentFight,
			"Defeat the Fragment of Seren with magic. Protect from Ranged, and keep your distance.", magicCombatGear);
		finalBattle.addStep(inBossArea, defeatFragmentOfSeren);
		finalBattle.addStep(inPassF1, goDownToDwarves);
		finalBattle.addStep(inIbanRoom, leaveIbanRoom);
		finalBattle.addStep(inWellEntrance, enterWellOfVoyage);
		steps.put(186, finalBattle);

		steps.put(188, talkToBaxToFinish);
		steps.put(190, talkToBaxToFinish);
		steps.put(192, talkToBaxToFinish);

		return steps;
	}

	public void setupItemRequirements()
	{
		mournerBoots = new ItemRequirement("Mourner boots", ItemID.MOURNER_BOOTS, 1, true);
		gasMask = new ItemRequirement("Gas mask", ItemID.GAS_MASK, 1, true);
		mournerGloves = new ItemRequirement("Mourner gloves", ItemID.MOURNER_GLOVES, 1, true);
		mournerCloak = new ItemRequirement("Mourner cloak", ItemID.MOURNER_CLOAK, 1, true);
		mournerTop = new ItemRequirement("Mourner top", ItemID.MOURNER_TOP, 1, true);
		mournerTrousers = new ItemRequirement("Mourner trousers", ItemID.MOURNER_TROUSERS, 1, true);
		mournersOutfit = new ItemRequirements("Full mourners' outfit", gasMask, mournerTop, mournerTrousers, mournerCloak, mournerBoots, mournerGloves);

		combatGear = new ItemRequirement("Combat gear, food and potions", -1, -1);

		steelFullHelm = new ItemRequirement("Steel full helm", ItemID.STEEL_FULL_HELM);
		steelFullHelm.setHighlightInInventory(true);
		steelPlatebody = new ItemRequirement("Steel platebody", ItemID.STEEL_PLATEBODY);
		steelPlatebody.setHighlightInInventory(true);
		steelPlatelegs = new ItemRequirement("Steel platelegs", ItemID.STEEL_PLATELEGS);
		steelPlatelegs.addAlternates(ItemID.ARDOUGNE_KNIGHT_PLATELEGS);
		redDye = new ItemRequirement("Red dye", ItemID.RED_DYE);
		redDye.setHighlightInInventory(true);
		purpleDye = new ItemRequirement("Purple dye", ItemID.PURPLE_DYE);
		silk = new ItemRequirement("Silk", ItemID.SILK);
		runiteBar = new ItemRequirement("Runite bar", ItemID.RUNITE_BAR);
		limestoneBricks8 = new ItemRequirement("Limestone brick", ItemID.LIMESTONE_BRICK, 8);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		tinderboxHighlighted = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlighted.setHighlightInInventory(true);
		ropeHighlighted = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted.setHighlightInInventory(true);
		natureRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE);
		iritLeafOrFlowers = new ItemRequirement("Irit leaf or a flower", ItemID.IRIT_LEAF);
		// TODO: Add flower collection with bank tags
		iritLeafOrFlowers.addAlternates(ItemID.RED_FLOWERS, ItemID.YELLOW_FLOWERS, ItemID.PURPLE_FLOWERS, ItemID.ORANGE_FLOWERS,
			ItemID.MIXED_FLOWERS, ItemID.ASSORTED_FLOWERS, ItemID.BLACK_FLOWERS, ItemID.WHITE_FLOWERS);
		adamantChainbody = new ItemRequirement("Adamant chainbody", ItemID.ADAMANT_CHAINBODY);
		wineOfZamorakOrZamorakBrew = new ItemRequirement("Wine of Zamorak or Zamorak brew", ItemID.WINE_OF_ZAMORAK);
		wineOfZamorakOrZamorakBrew.addAlternates(ItemID.ZAMORAK_BREW1, ItemID.ZAMORAK_BREW2, ItemID.ZAMORAK_BREW3, ItemID.ZAMORAK_BREW4);
		cabbage = new ItemRequirement("Cabbage", ItemID.CABBAGE);
		blackKnifeOrBlackDagger = new ItemRequirement("Black knife or black dagger", ItemID.BLACK_KNIFE);
		blackKnifeOrBlackDagger.addAlternates(ItemID.BLACK_KNIFEP, ItemID.BLACK_KNIFEP_5658, ItemID.BLACK_KNIFEP_5665, ItemID.BLACK_DAGGER,
			ItemID.BLACK_DAGGERP, ItemID.BLACK_DAGGERP_5682, ItemID.BLACK_DAGGERP_5700);
		cadantineSeed = new ItemRequirement("Cadantine seed", ItemID.CADANTINE_SEED);
		cadantineSeed.setHighlightInInventory(true);
		seedDibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER);
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted.setHighlightInInventory(true);
		hammer = new ItemRequirement("Hammer", ItemCollections.getHammer());
		saw = new ItemRequirement("Saw", ItemCollections.getSaw());
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		pickaxeHighlighted = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		pickaxeHighlighted.setHighlightInInventory(true);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		spade = new ItemRequirement("Spade", ItemID.SPADE);

		ardyFullHelm = new ItemRequirement("Ardougne knight helm", ItemID.ARDOUGNE_KNIGHT_HELM);
		ardyPlatebody = new ItemRequirement("Ardougne knight platebody", ItemID.ARDOUGNE_KNIGHT_PLATEBODY);
		ardyPlatelegs = new ItemRequirement("Ardougne knight platelegs", ItemID.ARDOUGNE_KNIGHT_PLATELEGS);

		ardyTabard = new ItemRequirement("Ardougne knight tabard", ItemID.ARDOUGNE_KNIGHT_TABARD);
		ardyTabard.setHighlightInInventory(true);
		ardyFullHelmEquipped = new ItemRequirement("Ardougne knight helm", ItemID.ARDOUGNE_KNIGHT_HELM, 1, true);
		ardyPlatebodyEquipped = new ItemRequirement("Ardougne knight platebody", ItemID.ARDOUGNE_KNIGHT_PLATEBODY, 1, true);
		ardyPlatelegsEquipped = new ItemRequirement("Ardougne knight platelegs", ItemID.ARDOUGNE_KNIGHT_PLATELEGS, 1, true);
		steelPlatelegsEquipped = new ItemRequirement("Steel platelegs", ItemID.STEEL_PLATELEGS, 1, true);
		steelPlatelegsEquipped.addAlternates(ItemID.ARDOUGNE_KNIGHT_PLATELEGS);

		clearLiquid = new ItemRequirement("Clear liquid", ItemID.CLEAR_LIQUID);
		baxKey = new ItemRequirement("Key", ItemID.KEY_298);

		odeToEternityHighlighted = new ItemRequirement("Ode to Eternity", ItemID.ODE_TO_ETERNITY);
		odeToEternityHighlighted.setHighlightInInventory(true);

		crystalSeed = new ItemRequirement("Crystal seed", ItemID.CRYSTAL_SEED_23810);
		crystalSeed.setHighlightInInventory(true);

		hammerHighlighted = new ItemRequirement("Hammer", ItemCollections.getHammer());
		hammerHighlighted.setHighlightInInventory(true);

		elderCadantine = new ItemRequirement("Elder cadantine", ItemID.ELDER_CADANTINE);

		elderCadantineHighlighted = new ItemRequirement("Elder cadantine", ItemID.ELDER_CADANTINE);
		elderCadantineHighlighted.setHighlightInInventory(true);

		crystal = new ItemRequirement("Crystal", ItemID.CRYSTAL_23802);
		crystal.setHighlightInInventory(true);

		vialOfWaterHighlighted = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER);
		vialOfWaterHighlighted.setHighlightInInventory(true);
		crystalDust = new ItemRequirement("Crystal dust", ItemID.CRYSTAL_DUST);
		crystalDust.setHighlightInInventory(true);
		elderCadantineVial = new ItemRequirement("Elder cadantine potion (unf)", ItemID.ELDER_CADANTINE_POTION_UNF);
		elderCadantineVial.setHighlightInInventory(true);

		inversionPotion = new ItemRequirement("Inversion potion", ItemID.INVERSION_POTION);

		nothingEquipped = new NoItemRequirement("Nothing equipped", ItemSlots.ANY_EQUIPPED);

		explosivePotion = new ItemRequirement("Explosive potion", ItemID.EXPLOSIVE_POTION_23818);
		explosivePotion.setTooltip("You can get more from Elena");

		magicCombatGear = new ItemRequirement("Magic combat gear, food and potions", -1, -1);
		saradominBrews = new ItemRequirement("Saradomin brews", ItemID.SARADOMIN_BREW4);
		superRestorePotions = new ItemRequirement("Super restores", ItemID.SUPER_RESTORE4);

		teleCrystal =  new ItemRequirement("Teleport crystal", ItemID.TELEPORT_CRYSTAL_1);
		teleCrystal.addAlternates(ItemID.TELEPORT_CRYSTAL_2, ItemID.TELEPORT_CRYSTAL_3, ItemID.TELEPORT_CRYSTAL_4, ItemID.TELEPORT_CRYSTAL_5);
		iorwerthCampTeleport = new ItemRequirement("Iorwerth camp teleport", ItemID.IORWERTH_CAMP_TELEPORT);
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.getGamesNecklaces());
	}

	public void setupConditions()
	{
		inArdougneCastleF1 = new ZoneRequirement(ardougneCastleF1);
		inArdyPrison = new ZoneRequirement(ardyPrison);
		inHideout = new ZoneRequirement(hideout);
		inWestArdyInstance = new Conditions(new InInstanceRequirement(), new ZoneRequirement(westArdyInstance));
		inMournerBaseHQInstance = new Conditions(new InInstanceRequirement(), new ZoneRequirement(mournerBasement));
		inEastArdyInstance = new Conditions(new InInstanceRequirement(), new ZoneRequirement(eastArdy));
		inArdougneCastleF1Instance = new Conditions(new InInstanceRequirement(), new ZoneRequirement(ardougneCastleF1));
		inLletyaF1 = new ZoneRequirement(llyetaF1);
		inValley = new ZoneRequirement(valley);
		inTraRoom = new ZoneRequirement(traRoom);
		inLightPuzzle = new ZoneRequirement(lightPuzzleRoom);
		inLibraryF0 = new ZoneRequirement(libraryF0);
		inLibraryF1 = new ZoneRequirement(libraryF1);
		inLibraryF2 = new ZoneRequirement(libraryF2);

		onDeadTreeIsland = new ZoneRequirement(deadTreeIsland);
		onHudonIsland = new ZoneRequirement(hudonIsland);
		onLedge = new ZoneRequirement(ledge);
		inFalls = new ZoneRequirement(falls);
		inBaxThroneRoom = new ZoneRequirement(baxThroneRoom);

		inIorwerthCave = new ZoneRequirement(iorwerthCave);
		inLletyaF0Battle = new Conditions(new ZoneRequirement(llyetaF0Battle));
		inLletyaF1Battle = new Conditions(new ZoneRequirement(llyetaF1Battle));
		inLletyaF1Damaged = new ZoneRequirement(llyetaF1Damaged);

		inPassF0 = new ZoneRequirement(passF0);
		inPassF1 = new ZoneRequirement(passF1);
		inWellEntrance = new ZoneRequirement(wellEntrance);
		inIbanRoom = new ZoneRequirement(ibanRoom);
		inBossArea = new ZoneRequirement(bossArea);

		burnedGrain1 = new VarbitRequirement(9058, 1);
		burnedGrain2 = new VarbitRequirement(9059, 1);
		burnedGrain3 = new VarbitRequirement(9060, 1);
		talkedToPriest1 = new VarbitRequirement(9079, 1);
		talkedToSarah = new VarbitRequirement(9063, 1);
		talkedToChadwell = new VarbitRequirement(9065, 1);
		talkedToWestArdougne = new Conditions(burnedGrain1, burnedGrain2, burnedGrain3, talkedToPriest1, talkedToSarah, talkedToChadwell);
		talkedToSilverMerchant = new VarbitRequirement(9074, 1);
		talkedToBaker1 = new VarbitRequirement(9081, 1); // East baker
		talkedToBaker2 = new VarbitRequirement(9073, 1); // West baker
		talkedToGemMerchant = new VarbitRequirement(9072, 1);
		talkedToFurTrader = new VarbitRequirement(9071, 1);
		talkedToSpiceSeller = new VarbitRequirement(9070, 1);
		talkedToSilkMerchant = new VarbitRequirement(9069, 1);
		talkedToMarketStalls = new Conditions(talkedToSilverMerchant, talkedToBaker1, talkedToBaker2, talkedToGemMerchant, talkedToFurTrader,
			talkedToSpiceSeller, talkedToSilkMerchant);
		talkedToTownCrier = new VarbitRequirement(9075, 1);
		talkedToZenesha = new VarbitRequirement(9068, 1);
		talkedToEstateAgent = new VarbitRequirement(9080, 1);
		talkedToProbita = new VarbitRequirement(9067, 1);
		talkedToAemad = new VarbitRequirement(9066, 1);
		talkedToPriest2 = new VarbitRequirement(9078, 1);
		talkedToOrbon = new VarbitRequirement(9064, 1);

		// 9017 0->1 talked to guard in prison


		// Start west ardy fight:
		// 9683 0->1
		// 9657 0->30

		// Defeated mourners, 9154 = 1

		// Finished battle cutscenes, 9026 = 1

		askedAboutAmlodd = new VarbitRequirement(9020, 1, Operation.GREATER_EQUAL);
		gottenTeleportCrystal = new VarbitRequirement(9020, 2, Operation.GREATER_EQUAL);

		// Amlodd has come:
		// 9016 80->82, 9020 3->4, 9021 0->1

		askedAboutTrahaearn = new VarbitRequirement(9018, 1, Operation.GREATER_EQUAL);
		clearedTraBlockage = new VarbitRequirement(9018, 3, Operation.GREATER_EQUAL);
		repairedExo = new VarbitRequirement(9018, 5, Operation.GREATER_EQUAL);

		// Tra has come:
		// 9016: 82 ->84, 9018, 5->6, 9019 0->1

		askedAboutCrwys = new VarbitRequirement(9022, 1, Operation.GREATER_EQUAL);
		revealedCrwys = new VarbitRequirement(9022, 2, Operation.GREATER_EQUAL);
		askedAboutHefin = new VarbitRequirement(9024, 1, Operation.GREATER_EQUAL);
		foundHefin = new VarbitRequirement(9024, 3, Operation.GREATER_EQUAL);

		tracked1 = new VarbitRequirement(9028, 1);
		tracked2 = new VarbitRequirement(9029, 1);
		tracked3 = new VarbitRequirement(9030, 1);
		tracked4 = new VarbitRequirement(9031, 1);
		tracked5 = new VarbitRequirement(9032, 1);

		foundEoin = new VarbitRequirement(9033, 1);
		// foundIona, 9034 = 1

		askedAboutIthell = new VarbitRequirement(9035, 1, Operation.GREATER_EQUAL);
		askedAboutMeilyr = new VarbitRequirement(9036, 1, Operation.GREATER_EQUAL);

		checkedSymbol1 = new VarbitRequirement(9041, 2);
		checkedSymbol2 = new VarbitRequirement(9039, 2);
		checkedSymbol3 = new VarbitRequirement(9040, 2);
		checkedSymbol4 = new VarbitRequirement(9038, 2);
		checkedSymbol5 = new VarbitRequirement(9037, 2);

		learnedHowToMakeStatue = new VarbitRequirement(9035, 2, Operation.GREATER_EQUAL);
		builtStatue = new VarbitRequirement(9035, 3, Operation.GREATER_EQUAL);
		finishedIthell = new VarbitRequirement(9035, 4, Operation.GREATER_EQUAL);

		dugNearTyras = new VarbitRequirement(9036, 2, Operation.GREATER_EQUAL);
		dugNearStash = new VarbitRequirement(9036, 3, Operation.GREATER_EQUAL);
		dugNearPrif = new VarbitRequirement(9036, 4, Operation.GREATER_EQUAL);
		spunOutsideUndergroundPass = new VarbitRequirement(9036, 5, Operation.GREATER_EQUAL);
		// 9037->9041 marks

		// 9042->49 0->1 for holes
		filledHole1 = new VarbitRequirement(9042, 2);
		filledHole2 = new VarbitRequirement(9043, 2);
		filledHole3 = new VarbitRequirement(9044, 2);
		filledHole4 = new VarbitRequirement(9045, 2);
		filledHole5 = new VarbitRequirement(9047, 2);
		filledHole6 = new VarbitRequirement(9048, 2);
		filledHole7 = new VarbitRequirement(9049, 2);

	}

	public void loadZones()
	{
		ardougneCastleF1 = new Zone(new WorldPoint(2570, 3283, 1), new WorldPoint(2590, 3310, 1));
		ardyPrison = new Zone(new WorldPoint(2564, 9703, 0), new WorldPoint(2576, 9715, 0));
		hideout = new Zone(new WorldPoint(2535, 9740, 0), new WorldPoint(2546, 9747, 0));
		westArdyInstance = new Zone(new WorldPoint(2509, 3263, 0), new WorldPoint(2558, 3334, 0));
		eastArdy = new Zone(new WorldPoint(2559, 3256, 0), new WorldPoint(2696, 3343, 0));
		mournerBasement = new Zone(new WorldPoint(2034, 4628, 0), new WorldPoint(2045, 4651, 0));
		llyetaF0Battle = new Zone(new WorldPoint(2882, 6157, 0), new WorldPoint(2940, 6194, 0));
		llyetaF1Battle = new Zone(new WorldPoint(2882, 6157, 1), new WorldPoint(2940, 6194, 1));
		llyetaF1Damaged = new Zone(new WorldPoint(2750, 6070, 1), new WorldPoint(2810, 6140, 1));
		llyetaF1 = new Zone(new WorldPoint(2331, 3154, 1), new WorldPoint(2358, 3189, 1));

		hudonIsland = new Zone(new WorldPoint(2510, 3476, 0), new WorldPoint(2515, 3482, 0));
		deadTreeIsland = new Zone(new WorldPoint(2512, 3465, 0), new WorldPoint(2513, 3475, 0));
		ledge = new Zone(new WorldPoint(2510, 3462, 0), new WorldPoint(2513, 3464, 0));
		falls = new Zone(new WorldPoint(2556, 9861, 0), new WorldPoint(2595, 9920, 0));
		baxThroneRoom = new Zone(new WorldPoint(2595, 9895, 0), new WorldPoint(2615, 9925, 0));

		passF0 = new Zone(new WorldPoint(2429, 6077, 0), new WorldPoint(2499, 6210, 0));
		passF1 = new Zone(new WorldPoint(2105, 4540, 1), new WorldPoint(2187, 4750, 1));
		wellEntrance = new Zone(new WorldPoint(2311, 9608, 0), new WorldPoint(2354, 9637, 0));
		ibanRoom = new Zone(new WorldPoint(1999, 4704, 1), new WorldPoint(2015, 4717, 1));

		valley = new Zone(new WorldPoint(3046, 4481, 0), new WorldPoint(3055, 4486, 0));
		traRoom = new Zone(new WorldPoint(2330, 9572, 0), new WorldPoint(2337, 9580, 0));

		lightPuzzleRoom = new Zone(new WorldPoint(2565, 6080, 0), new WorldPoint(2740, 6179, 2));
		libraryF0 = new Zone(new WorldPoint(2565, 6080, 0), new WorldPoint(2740, 6204, 0));
		libraryF1 = new Zone(new WorldPoint(2565, 6080, 1), new WorldPoint(2740, 6204, 1));
		libraryF2 = new Zone(new WorldPoint(2565, 6080, 2), new WorldPoint(2740, 6204, 2));

		iorwerthCave = new Zone(new WorldPoint(2190, 9668, 0), new WorldPoint(2210, 9688, 0));
		//3283, y=5912
		bossArea = new Zone(new WorldPoint(3272, 5900, 0), new WorldPoint(3330, 5955, 3));
	}

	public void setupSteps()
	{
		talkToEdmond = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2566, 3337, 0), "Talk to Edmond in East Ardougne.");
		talkToEdmond.addDialogStep("Yes.");
		goUpToLathas = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0),
			"Talk to King Lathas in East Ardougne castle.");
		talkToLathas = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Talk to King Lathas in East Ardougne castle.");
		talkToLathas.addSubSteps(goUpToLathas);
		goDownFromLathas = new ObjectStep(this, ObjectID.STAIRCASE_15648, new WorldPoint(2572, 3296, 1), "Return to Edmond.");
		talkToEdmondAgain = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2566, 3337, 0), "Return to Edmond in East Ardougne.");
		talkToEdmondAgain.addDialogStep("How do I get this disguise?");
		talkToEdmondAgain.addSubSteps(goDownFromLathas);
		useRedDyeOnSteelFullHelm = new DetailedQuestStep(this, "Use red dye on a steel full helm.", redDye, steelFullHelm);
		talkToAlrena = new NpcStep(this, NpcID.ALRENA, new WorldPoint(2574, 3334, 0), "Talk to Alrena in East Ardougne.", purpleDye, silk);
		talkToAlrena.addDialogStep("Yes.");

		talkToAlrenaNoItems = new NpcStep(this, NpcID.ALRENA, new WorldPoint(2574, 3334, 0), "Talk to Alrena in East Ardougne.");
		talkToAlrenaNoItems.addDialogStep("I need another tabard.");
		talkToAlrena.addSubSteps(talkToAlrenaNoItems);

		useTabardOnPlatebody = new DetailedQuestStep(this, "Add the tabard to a steel platebody.", ardyTabard, steelPlatebody);

		talkToEdmondWithOutfit = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2566, 3337, 0),
			"Return to Edmond in East Ardougne.", ardyFullHelm, ardyPlatebody);

		goDownstairsCastle = new ObjectStep(this, ObjectID.STAIRCASE_35791, new WorldPoint(2570, 3296, 0),
			"Enter the basement of East Ardougne castle.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToElenaInCell = new NpcStep(this, NpcID.ELENA_8792, new WorldPoint(2574, 9714, 0), "Talk to Elena in the cell.");
		goUpFromCastleBasement = new ObjectStep(this, ObjectID.STAIRCASE_35792, new WorldPoint(2565, 9711, 0), "Leave the prison.");
		searchElenaCabinet = new ObjectStep(this, ObjectID.CABINET_35796, new WorldPoint(2590, 3338, 0), "Search the cabinet in Elena's house.");
		searchElenaCabinet.addDialogStep("The thick clear liquid.");

		goDownstairsCastleAgain = new ObjectStep(this, ObjectID.STAIRCASE_35791, new WorldPoint(2570, 3296, 0),
			"Return to Elena with the clear liquid.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped, clearLiquid);
		talkToElenaInCellAgain = new NpcStep(this, NpcID.ELENA_8792, new WorldPoint(2574, 9714, 0),
			"Return to Elena with the clear liquid.", clearLiquid);
		talkToElenaInCellAgain.addDialogStep("Yes.");
		talkToElenaInCellAgain.addSubSteps(goDownstairsCastleAgain);

		goUpFromCastleBasementAgain = new ObjectStep(this, ObjectID.STAIRCASE_35792, new WorldPoint(2565, 9711, 0),
			"Return to Edmond in East Ardougne.");
		talkToEdmondAfterFreeingElena = new NpcStep(this, NpcID.EDMOND_4256, new WorldPoint(2566, 3337, 0),
				"Return to Edmond in East Ardougne.");
		talkToEdmondAfterFreeingElena.addSubSteps(goUpFromCastleBasementAgain);

		searchBed = new ObjectStep(this, NullObjectID.NULL_37265, new WorldPoint(2533, 3333, 0),
			"Search the bed in the house west of the Mourner base.");
		searchBed.addDialogStep("Climb through it.");
		talkToElenaInHideout = new NpcStep(this, NpcID.ELENA_8798, new WorldPoint(2545, 9746, 0), "Talk to Elena in the hideout.");
		talkToElenaInHideout.addDialogSteps("It's not good, but it might be our only option.","It's all in a day's work.", "I am.");
		leaveHideout = new ObjectStep(this, ObjectID.LADDER_35799, new WorldPoint(2546, 9744, 0), "Leave the hideout.");

		useTinderboxOnGrain = new ObjectStep(this, NullObjectID.NULL_37330, new WorldPoint(2517, 3315, 0),
			"Use a tinderbox on the grain in the West Ardougne council building.", tinderboxHighlighted, gasMask, mournerTop,
				mournerTrousers, mournerBoots, mournerGloves, mournerCloak);
		useTinderboxOnGrain.addIcon(ItemID.TINDERBOX);
		talkToPriest = new NpcStep(this, NpcID.PRIEST, new WorldPoint(2527, 3287, 0),
			"Talk to the priest in West Ardougne.", gasMask, mournerTop, mournerTrousers, mournerBoots, mournerGloves, mournerCloak);
		useTinderboxOnChurchGrain = new ObjectStep(this, NullObjectID.NULL_37331, new WorldPoint(2524, 3285, 0),
				"Use a tinderbox on the grain in the West Ardougne church.",
				tinderboxHighlighted, gasMask, mournerTop, mournerTrousers, mournerBoots, mournerGloves, mournerCloak);
		useTinderboxOnChurchGrain.addIcon(ItemID.TINDERBOX);
		talkToSarah = new NpcStep(this, NpcID.NURSE_SARAH, new WorldPoint(2517, 3274, 0),
			"Talk to Nurse Sarah in West Ardougne.", gasMask, mournerTop, mournerTrousers, mournerBoots, mournerGloves, mournerCloak);
		useTinderboxOnSWGrain = new ObjectStep(this, NullObjectID.NULL_37332, new WorldPoint(2469, 3287, 0),
				"Use a tinderbox on the grain outside the West Ardougne general store.",
				tinderboxHighlighted, gasMask, mournerTop, mournerTrousers, mournerBoots, mournerGloves, mournerCloak);
		useTinderboxOnSWGrain.addIcon(ItemID.TINDERBOX);
		talkToChadwell = new NpcStep(this, NpcID.CHADWELL, new WorldPoint(2466, 3286, 0),
			"Talk to Chadwell in the West Ardougne general store.", gasMask, mournerTop, mournerTrousers, mournerBoots, mournerGloves, mournerCloak);
		talkToChadwell.addDialogStep("I'm here to tell you about some new taxes.");

		talkToSilverMerchant = new NpcStep(this, NpcID.SILVER_MERCHANT_8722, new WorldPoint(2658, 3316, 0),
			"Talk to the silver merchant in the East Ardougne Market", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToSilverMerchant.addDialogStep("I'm here to tell you about some new taxes.");
		talkToBaker1 = new NpcStep(this, NpcID.BAKER_8725, new WorldPoint(2669, 3310, 0),
			"Talk to the bakers in the East Ardougne Market", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToBaker1.addDialogStep("I'm here to tell you about some new taxes.");
		talkToBaker2 = new NpcStep(this, NpcID.BAKER_8724, new WorldPoint(2654, 3311, 0),
			"Talk to the bakers in the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToBaker2.addDialogStep("I'm here to tell you about some new taxes.");
		talkToBaker1.addSubSteps(talkToBaker2);
		talkToGemMerchant = new NpcStep(this, NpcID.GEM_MERCHANT_8723, new WorldPoint(2669, 3303, 0),
			"Talk to the gem merchant in the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToGemMerchant.addDialogStep("I'm here to tell you about some new taxes.");
		talkToFurTrader = new NpcStep(this, NpcID.FUR_TRADER_8727, new WorldPoint(2666, 3295, 0),
			"Talk to the fur trader in the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToFurTrader.addDialogStep("I'm here to tell you about some new taxes.");
		talkToSpiceSeller = new NpcStep(this, NpcID.SPICE_SELLER_8726, new WorldPoint(2658, 3296, 0),
			"Talk to the spice seller in the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToSpiceSeller.addDialogStep("I'm here to tell you about some new taxes.");
		talkToSilkMerchant = new NpcStep(this, NpcID.SILK_MERCHANT_8728, new WorldPoint(2656, 3301, 0),
			"Talk to the silk merchant in the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);

		talkToTownCrier = new NpcStep(this, NpcID.TOWN_CRIER_279, new WorldPoint(2666, 3312, 0),
			"Talk to the town crier in the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToTownCrier.addDialogStep("Could you help me tell people about some new taxes?");
		talkToZenesha = new NpcStep(this, NpcID.ZENESHA_8681, new WorldPoint(2652, 3295, 0),
			"Talk to Zenesha south of the East Ardougne Market.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToZenesha.addDialogStep("I'm here to tell you about some new taxes.");
		talkToEstateAgent = new NpcStep(this, NpcID.ESTATE_AGENT, new WorldPoint(2638, 3293, 0),
			"Talk to the Estate Agent in East Ardougne.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToEstateAgent.addDialogStep("I'm here to tell you about some new taxes.");
		talkToProbita = new NpcStep(this, NpcID.PROBITA, new WorldPoint(2621, 3293, 0),
			"Talk to Probita in East Ardougne.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToProbita.addDialogStep("I'm here to tell you about some new taxes.");
		talkToAemad = new NpcStep(this, NpcID.AEMAD, new WorldPoint(2613, 3292, 0),
			"Talk to Aemad in the East Ardougne General Store.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToAemad.addDialogStep("I'm here to tell you about some new taxes.");
		talkToPriest2 = new NpcStep(this, NpcID.PRIEST_5417, new WorldPoint(2616, 3308, 0),
			"Talk to the East Ardougne Priest.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToOrbon = new NpcStep(this, NpcID.DOCTOR_ORBON, new WorldPoint(2616, 3308, 0),
			"Talk to Doctor Orbon in the East Ardougne church.", ardyFullHelmEquipped, ardyPlatebodyEquipped, steelPlatelegsEquipped);
		talkToOrbon.addDialogStep("I'm here to tell you about some new taxes.");

		searchBedAgain = new ObjectStep(this, NullObjectID.NULL_37265, new WorldPoint(2533, 3333, 0),
			"Search the bed in the house west of the Mourner base.", combatGear);
		talkToElenaForFight = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2545, 9746, 0),
			"Talk to Elena in the hideout, ready for a fight.", combatGear);
		talkToElenaForFight.addDialogSteps("Are you sure you're okay with this Elena?", "I'm ready.");
		talkToElenaForFight.addSubSteps(searchBedAgain);
		defeat10Mourners = new NpcStep(this, NpcID.MOURNER_8844, new WorldPoint(2549, 3314, 0), "Kill the 10 mourners.", true);
		defeat10Mourners.setHideWorldArrow(true);
		((NpcStep) defeat10Mourners).addAlternateNpcs(NpcID.MOURNER_8845, NpcID.MOURNER_8846);
		mournerBattleGoDownTrapdoor = new ObjectStep(this, ObjectID.TRAPDOOR_35815, new WorldPoint(2542, 3327, 0),
			"Enter the Mourner HQ's basement.");
		defeat11Mourners = new NpcStep(this, NpcID.MOURNER_8844, new WorldPoint(2040, 4639, 0), "Kill the 11 mourners.", true);
		defeat11Mourners.setHideWorldArrow(true);
		((NpcStep) defeat11Mourners).addAlternateNpcs(NpcID.MOURNER_8845, NpcID.MOURNER_8846);
		talkToElenaForEastArdyFight = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2545, 9746, 0),
			"Talk to Elena in the hideout, ready for a fight.", combatGear);
		talkToElenaForEastArdyFight.addDialogSteps("I'm ready.");
		defeat15Knights = new NpcStep(this, NpcID.PALADIN_8849, new WorldPoint(2585, 3297, 0),
			"Defeat 15 knights in the courtyard and upstairs.", true);
		((NpcStep) defeat15Knights).addAlternateNpcs(NpcID.PALADIN_8850, NpcID.KNIGHT_OF_ARDOUGNE_8851, NpcID.KNIGHT_OF_ARDOUGNE_8852);
		defeat15KnightsF1 = new NpcStep(this, NpcID.PALADIN_8849, new WorldPoint(2585, 3297, 0),
			"Defeat 15 knights in the courtyard and upstairs.", true);
		((NpcStep) defeat15KnightsF1).addAlternateNpcs(NpcID.PALADIN_8850, NpcID.KNIGHT_OF_ARDOUGNE_8851, NpcID.KNIGHT_OF_ARDOUGNE_8852);
		defeat15Knights.addSubSteps(defeat15KnightsF1);
		talkToLathasAfterFight = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2545, 9746, 0),
			"Talk to Elena and then go through the cutscene with Lathas.", combatGear);
		talkToLathasAfterFight.addDialogStep("We're done here, take him away.");
		watchArdyFightCutscene = new DetailedQuestStep(this, "Watch the cutscenes.");

		talkToArianwyn = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn in Lletya.");
		goUpToYsgawyn = new ObjectStep(this, ObjectID.LADDER_8744, new WorldPoint(2352, 3180, 0), "Go up to Ysgawyn in Lletya.");
		talkToYsgawyn = new NpcStep(this, NpcID.YSGAWYN, new WorldPoint(2335, 3167, 1), "Talk to Ysgawyn.");
		talkToYsgawyn.addSubSteps(goUpToYsgawyn);
		goDownToElena = new ObjectStep(this, ObjectID.LADDER_8746, new WorldPoint(2352, 3180, 1), "Talk to Elena in the south west of Lletya.");
		talkToElenaInLletya = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2324, 3152, 0), "Talk to Elena in the south west of Lletya.");
		talkToElenaInLletya.addDialogSteps("It is. I'm here for you if you need me though.", "Go ahead.");
		talkToElenaInLletya.addSubSteps(goDownToElena);
		talkToArianwynAfterElena = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn again.");
		talkToArianwynAfterMeeting = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn after the meeting.");

		boardRaft = new ObjectStep(this, ObjectID.LOG_RAFT, new WorldPoint(2509, 3494, 0),
			"Board the log raft west of Almera.", rope, iritLeafOrFlowers, cabbage, wineOfZamorakOrZamorakBrew,
			natureRune, adamantChainbody, blackKnifeOrBlackDagger);
		useRopeOnRock = new ObjectStep(this, ObjectID.ROCK, new WorldPoint(2512, 3468, 0),
			"Use a rope on the rock to the south.", ropeHighlighted);
		useRopeOnRock.addIcon(ItemID.ROPE);
		useRopeOnTree = new ObjectStep(this, ObjectID.DEAD_TREE_2020, new WorldPoint(2512, 3465, 0),
			"Use a rope on the dead tree.", ropeHighlighted);
		useRopeOnTree.addIcon(ItemID.ROPE);
		enterFalls = new ObjectStep(this, ObjectID.DOOR_2010, new WorldPoint(2511, 3464, 0), "Enter the falls.");

		searchCrateForKey = new ObjectStep(this, ObjectID.CRATE_1999, new WorldPoint(2589, 9888, 0),
			"Search the crate in the east room for a key.");

		enterBaxThroneRoom = new ObjectStep(this, ObjectID.DOOR_2002, new WorldPoint(2566, 9901, 0),
			"Go through the doors from the west room.", baxKey);
		talkToArianwynInBax = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2603, 9905, 0), "Talk to Arianwyn.");
		readOdeToEternity = new DetailedQuestStep(this, "Read the Ode to Eternity.", odeToEternityHighlighted);

		pillarPuzzle = new BaxtorianPuzzle(this);

		talkToBax = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2605, 9915, 0), "Talk to Baxtorian.");

		talkToArianwynAfterBax = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn in Lletya.");
		talkToElenaAfterBax = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2324, 3152, 0),
			"Talk to Elena in the south west of Lletya.", seedDibber, cadantineSeed);

		plantCadantine = new ObjectStep(this, NullObjectID.NULL_37270, new WorldPoint(2322, 3152, 0),
			"Plant a cadantine seed in the Lletya farm patch.", seedDibber, cadantineSeed);
		plantCadantine.addIcon(ItemID.CADANTINE_SEED);

		talkToBaxInLletya = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2352, 3170, 0), "Talk to Baxtorian in " +
			"Lletya.");
		talkToBaxInLletya.addDialogStep("How do I find Lord Amlodd?");
		talkToElunedWithSeed = new NpcStep(this, NpcID.ELUNED_8767, new WorldPoint(2322, 3160, 0), "Talk to Eluned in" +
			" Lletya.");
		talkToElunedWithSeed.addDialogStep("I have a seed from Baxtorian that I think needs enchanting.");
		rubCrystal = new DetailedQuestStep(this, "Rub the teleport crystal to teleport.", crystalSeed);
		talkToFigure = new NpcStep(this, NpcID.MYSTERIOUS_FIGURE, new WorldPoint(3051, 4486, 0), 
				"Talk to the Mysterious Figure.");
		talkToBaxAfterValley = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2352, 3170, 0),
				"Talk to Baxtorian in Lletya about Trahaearn.");
		talkToBaxAfterValley.addDialogStep("How do I find Lady Trahaearn?");

		enterCaveToWell = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_4006, new WorldPoint(2314, 3217, 0),
			"Enter the cave to the Underground Pass from the Elven Lands entrance.", pickaxe, hammer, runiteBar);
		enterCaveToWell.setLinePoints(Arrays.asList(
			new WorldPoint(2322, 3171, 0),
			new WorldPoint(2310, 3171, 0),
			new WorldPoint(2306, 3195, 0),
			new WorldPoint(2292, 3188, 0),
			new WorldPoint(2275, 3188, 0),
			new WorldPoint(2273, 3191, 0),
			new WorldPoint(2263, 3192, 0),
			new WorldPoint(2271, 3210, 0),
			new WorldPoint(2285, 3213, 0),
			new WorldPoint(2289, 3207, 0),
			new WorldPoint(2297, 3207, 0),
			new WorldPoint(2312, 3216, 0)
		));

		usePickaxeOnWall = new ObjectStep(this, NullObjectID.NULL_37271, new WorldPoint(2341, 9616, 0),
			"Use a pickaxe on the wall south of the Well of Voyage.", pickaxeHighlighted);
		usePickaxeOnWall.addIcon(ItemID.RUNE_PICKAXE);
		enterCrevice = new ObjectStep(this, NullObjectID.NULL_37271, new WorldPoint(2341, 9616, 0), "Enter the hole in the wall.");

		useHammerOnExoskeleton = new NpcStep(this, NpcID.ELDERLY_ELF, new WorldPoint(2334, 9574, 0),
			"Use a hammer on the elderly elf.", hammerHighlighted, runiteBar);
		useHammerOnExoskeleton.addIcon(ItemID.HAMMER);
		talkToTrahaearn = new NpcStep(this, NpcID.LADY_TANGWEN_TRAHAEARN, new WorldPoint(2334, 9574, 0), "Talk to Lady Tangwen Trahararn.");

		talkToElenaAfterTra = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2324, 3152, 0),
			"Return to Elena in the south west of Lletya.");
		talkToArianwynAfterTra = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0),
			"Talk to Arianwyn in Lletya.", elderCadantine);
		usePestleOnCrystal = new DetailedQuestStep(this, "Use a pestle and mortar on the crystal.", pestleAndMortarHighlighted, crystal);
		useCadantineOnVial = new DetailedQuestStep(this, "Add the cadantine to a vial of water.", elderCadantineHighlighted, vialOfWaterHighlighted);
		useDustOnVial = new DetailedQuestStep(this, "Add the crystal dust to a cadantine potion.", crystalDust, elderCadantineVial);
		talkToArianwynAfterPotion = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0),
			"Bring the potion to Arianwyn.", inversionPotion);
		talkToArianwynAfterGivingPotion = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn.");
		talkToArianwynAfterPotion.addSubSteps(talkToArianwynAfterGivingPotion);

		talkToAmlodd = new NpcStep(this, NpcID.LORD_IEUAN_AMLODD, new WorldPoint(2352, 3182, 0), "Talk to Lord Amlodd" +
			" in Lletya.");
		((NpcStep) talkToAmlodd).setMaxRoamRange(9);
		talkToAmlodd.addDialogStep("Yes.");
		touchSeal = new ObjectStep(this, ObjectID.SEAL_OF_THE_FORGOTTEN, new WorldPoint(2623, 6135, 1), "");

		List<WorldPoint> pathToIthell = Arrays.asList(
			new WorldPoint(2651, 6102, 1),
			new WorldPoint(2651, 6098, 1),
			new WorldPoint(2655, 6098, 1),
			new WorldPoint(2655, 6106, 1),
			new WorldPoint(2659, 6106, 1),
			new WorldPoint(2659, 6110, 1),
			new WorldPoint(2663, 6110, 1),
			new WorldPoint(2663, 6098, 1),
			new WorldPoint(2659, 6098, 1),
			new WorldPoint(2659, 6094, 1),
			new WorldPoint(2667, 6094, 1),
			new WorldPoint(2667, 6098, 1),
			new WorldPoint(2671, 6098, 1),
			new WorldPoint(2671, 6102, 1),
			new WorldPoint(2675, 6102, 1)
		);

		int LIBRARY_F1_SHIFT = 190;
		ArrayList<WorldPoint> mapPathToIthell = new ArrayList<>();
		for (WorldPoint worldPoint : pathToIthell)
		{
			mapPathToIthell.add(new WorldPoint(worldPoint.getX() + LIBRARY_F1_SHIFT, worldPoint.getY(),
				worldPoint.getPlane()));
		}

		goF0ToF1 = new ObjectStep(this, ObjectID.STAIRS_35387, new WorldPoint(2626, 6153, 0), "");

		goF1ToF0 = new ObjectStep(this, ObjectID.STAIRS_35388, new WorldPoint(2626, 6153, 1), "");
		((DetailedQuestStep) goF1ToF0).setWorldMapPoint(new WorldPoint(2818, 6154, 1));

		goF2ToF1 = new ObjectStep(this, ObjectID.STAIRS_35388, new WorldPoint(2634, 6166, 2), "");
		((DetailedQuestStep) goF2ToF1).setWorldMapPoint(new WorldPoint(3017, 6154, 2));
		goF1ToF2 = new ObjectStep(this, ObjectID.STAIRS_35387, new WorldPoint(2634, 6166, 1), "");
		((DetailedQuestStep) goF1ToF2).setWorldMapPoint(new WorldPoint(2826, 6154, 1));
		goF1ToF2NW = new ObjectStep(this, ObjectID.STAIRS_35389, new WorldPoint(2581, 6203, 1), "");
		goF1ToF2NW.addDialogStep("Climb up.");
		((DetailedQuestStep) goF1ToF2NW).setWorldMapPoint(new WorldPoint(2773, 6203, 1));

		goF1ToF2SW = new ObjectStep(this, ObjectID.STAIRS_35389, new WorldPoint(2584, 6137, 1), "");
		goF1ToF2SW.addDialogStep("Climb up.");
		((DetailedQuestStep) goF1ToF2SW).setWorldMapPoint(new WorldPoint(2776, 6137, 1));

		goF1ToF2E = new ObjectStep(this, ObjectID.STAIRS_35387, new WorldPoint(2682, 6144, 1), "");
		((DetailedQuestStep) goF1ToF2E).setWorldMapPoint(new WorldPoint(2874, 6144, 1));

		openSealOfIthell = new ObjectStep(this, NullObjectID.NULL_36730, new WorldPoint(2676, 6102, 1), "");
		((DetailedQuestStep) openSealOfIthell).setWorldMapPoint(new WorldPoint(2866, 6102, 1));
		((DetailedQuestStep) openSealOfIthell).setLinePoints(pathToIthell);
		((DetailedQuestStep) openSealOfIthell).setWorldLinePoints(mapPathToIthell);

		touchIthellSeal = new ObjectStep(this, NullObjectID.NULL_36730, new WorldPoint(2676, 6102, 1), "");
		((DetailedQuestStep) openSealOfIthell).setWorldMapPoint(new WorldPoint(2866, 6102, 1));

		touchCadarnSeal = new ObjectStep(this, NullObjectID.NULL_36725, new WorldPoint(2623, 6097, 0), "");

		touchCrwysSeal = new ObjectStep(this, NullObjectID.NULL_36726, new WorldPoint(2576, 6172, 1), "");
		((DetailedQuestStep) touchCrwysSeal).setWorldMapPoint(new WorldPoint(2768, 6172, 1));

		touchAmloddSeal = new ObjectStep(this, NullObjectID.NULL_36727, new WorldPoint(2614, 6158, 0), "");

		touchMeilyrSeal = new ObjectStep(this, NullObjectID.NULL_36731, new WorldPoint(2581, 6163, 2), "");
		((DetailedQuestStep) touchMeilyrSeal).setWorldMapPoint(new WorldPoint(2965, 6163, 2));

		touchHefinSeal = new ObjectStep(this, NullObjectID.NULL_36728, new WorldPoint(2562, 6130, 2), "");
		((DetailedQuestStep) touchHefinSeal).setWorldMapPoint(new WorldPoint(2946, 6130, 2));

		touchTrahaearnSeal = new ObjectStep(this, NullObjectID.NULL_36732, new WorldPoint(2646, 6144, 2), "");
		((DetailedQuestStep) touchTrahaearnSeal).setWorldMapPoint(new WorldPoint(3030, 2646, 2));

		touchIorwerthSeal = new ObjectStep(this, NullObjectID.NULL_36729, new WorldPoint(2651, 6167, 1), "");
		((DetailedQuestStep) touchIorwerthSeal).setWorldMapPoint(new WorldPoint(3843, 6167, 1));


		leaveLibrary = new ObjectStep(this, ObjectID.PORTAL_35075, new WorldPoint(2623, 6088, 1),
			"Leave the library. A quick way to do this is hopping worlds.");
		((DetailedQuestStep)(leaveLibrary)).setWorldMapPoint(new WorldPoint(2815, 6088, 1));

		talkToArianwynAfterLightPuzzle = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn in Lletya.");
		askBaxtorianHowToFindCrwys = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2352, 3170, 0),
			"Ask Baxtorian about how to find Crwys and Lady Hefin.");
		askBaxtorianHowToFindCrwys.addDialogStep("How do I find Lord Crwys?");
		askBaxtorianHowToFindHefin = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2352, 3170, 0),
			"Ask Baxtorian about how to find Crwys and Lady Hefin.");
		askBaxtorianHowToFindHefin.addDialogStep("How do I find Lady Hefin?");
		askBaxtorianHowToFindCrwys.addSubSteps(askBaxtorianHowToFindHefin);
		crossSteppingStones = new NpcStep(this, NpcID.ELF_HERMIT, new WorldPoint(2276, 3094, 0),
			"Hop across the stepping stones in the swamp south of the magic trees near Lletya. Talk to the Elf Hermit" +
				" on the island you reach.");
		((NpcStep)crossSteppingStones).addAlternateNpcs(NpcID.LADY_CARYS_HEFIN);
		((DetailedQuestStep) crossSteppingStones).setLinePoints(Arrays.asList(
			new WorldPoint(2299, 3114, 0),
			new WorldPoint(2299, 3110, 0),
			new WorldPoint(2302, 3110, 0),
			new WorldPoint(2302, 3104, 0),
			new WorldPoint(2299, 3104, 0),
			new WorldPoint(2299, 3098, 0),
			new WorldPoint(2302, 3098, 0),
			new WorldPoint(2302, 3089, 0),
			new WorldPoint(2293, 3089, 0),
			new WorldPoint(2293, 3092, 0),
			new WorldPoint(2290, 3092, 0),
			new WorldPoint(2290, 3095, 0),
			new WorldPoint(2284, 3095, 0),
			new WorldPoint(2284, 3092, 0),
			new WorldPoint(2280, 3092, 0)
		));

		chopTrees = new NpcStep(this, NpcID.TREE_8902, new WorldPoint(2176, 3211, 0),
			"Make your way to the north west of Isafdar, and chop a tree there. Talk to Crwys when he appears.", axe);
		((DetailedQuestStep) chopTrees).setLinePoints(Arrays.asList(
			new WorldPoint(2191, 3222, 0),
			new WorldPoint(2181, 3211, 0),
			new WorldPoint(2181, 3208, 0),
			new WorldPoint(2176, 3210, 0)
		));
		talkToCrwys = new NpcStep(this, NpcID.LORD_PIQUAN_CRWYS, new WorldPoint(2177, 3210, 0), "Talk to Lord Crwys.");

		talkToBaxAfterHefinAndCrwys = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2352, 3170, 0), "Return to Baxtorian in Lletya.");


		enterIorwerthCave = new ObjectStep(this, ObjectID.CAVE_35846, new WorldPoint(2202, 3263, 0),
			"Enter the cave in the Iorwerth camp.", combatGear);
		kill2IorwerthCaves = new NpcStep(this, NpcID.IORWERTH_ARCHER_8923, new WorldPoint(2200, 9678, 0),
			"Kill the guards.", true);
		((NpcStep) kill2IorwerthCaves).addAlternateNpcs(NpcID.IORWERTH_WARRIOR_8922);
		searchChestForOrb = new ObjectStep(this, ObjectID.CHEST_35848, new WorldPoint(2200, 9681, 0), "Search the chest for the orb.");
		((ObjectStep) searchChestForOrb).addAlternateObjects(ObjectID.CHEST_35849);
		talkToBaxWithOrb = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2352, 3170, 0), "Return to Baxtorian in Lletya.");

		talkToElenaAfterOrb = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2324, 3152, 0),
				"Talk to Elena in the south west of Lletya.");
		talkToElenaAfterOrb.addDialogSteps("That's fair. Anyway, I wanted to ask you about Ysgawyn.");
		inspectMushrooms = new ObjectStep(this, NullObjectID.NULL_37292, new WorldPoint(2302, 3195, 0),
			"Inspect the mushrooms just outside Lletya.");

		track1 = new ObjectStep(this, NullObjectID.NULL_37297, new WorldPoint(2291, 3187, 0), "Inspect the rocks to the west.");
		track2 = new ObjectStep(this, NullObjectID.NULL_37296, new WorldPoint(2277, 3184, 0),
				"Inspect the rocks to the west over the tripwire.");
		track3 = new ObjectStep(this, NullObjectID.NULL_37301, new WorldPoint(2269, 3164, 0),
			"Inspect the elven lamp to the south over the leaf trap.");
		track4 = new ObjectStep(this, NullObjectID.NULL_37299, new WorldPoint(2259, 3158, 0), "Inspect the mushrooms to the west.");
		track5 = new ObjectStep(this, NullObjectID.NULL_37297, new WorldPoint(2244, 3170, 0), "Inspect the rocks to the north west.");
		track6 = new DetailedQuestStep(this, new WorldPoint(2254, 3168, 0), "Climb over the tripwire into the clearing.");

		talkToYsgawynInClearing = new NpcStep(this, NpcID.YSGAWYN, new WorldPoint(2256, 3168, 0), "Talk to Ysgawyn.");
		talkToYsgawynInClearing.addDialogStep("So what have you been discussing?");

		enterLletyaForFighting = new ObjectStep(this, ObjectID.TREE_8742, new WorldPoint(2305, 3193, 0), "Enter Lletya.", combatGear);
		enterLletyaForFighting.addDialogStep("Yes.");

		searchTableForEoin = new ObjectStep(this, ObjectID.TABLE_35873, new WorldPoint(2916, 6166, 0),
			"Search the table to the south for Eoin.");
		goUpLletyaLadder = new ObjectStep(this, ObjectID.LADDER_8744, new WorldPoint(2911, 6168, 0), "Go upstairs.");
		searchCookingPots = new ObjectStep(this, ObjectID.COOKING_POTS_35875, new WorldPoint(2915, 6164, 1),
				"Search the nearby cooking pots.");
		fightArianwyn = new NpcStep(this, NpcID.ARIANWYN_8865, new WorldPoint(2906, 6179, 0),
			"Defeat Arianwyn. Protect from Ranged. Move when he shoots orange arrows.");

		talkToBaxAfterLletyaFightForIthellClue = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2801, 6116, 0),
			"Talk to Baxtorian in Lletya about Ithell and Meilyr.");
		talkToBaxAfterLletyaFightForIthellClue.addDialogStep("What's Lady Ithell's clue?");
		talkToBaxAfterLletyaFightForIthellClue.setWorldMapPoint(new WorldPoint(2354, 3171, 0));

		talkToBaxAfterLletyaFightForMeilyrClue = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2801, 6116, 0),
			"Talk to Baxtorian in Lletya about Ithell and Meilyr.");
		talkToBaxAfterLletyaFightForMeilyrClue.addDialogStep("What's Lady Meilyr's clue?");
		talkToBaxAfterLletyaFightForMeilyrClue.setWorldMapPoint(new WorldPoint(2354, 3171, 0));

		talkToBaxAfterLletyaFightForIthellClue.addSubSteps(talkToBaxAfterLletyaFightForMeilyrClue);

		checkSymbol1 = new ObjectStep(this, NullObjectID.NULL_37307, new WorldPoint(2798, 6107, 0), "Touch the symbol on the bank's wall.");
		checkSymbol1.setWorldMapPoint(new WorldPoint(2350, 3163, 0));
		checkSymbol2 = new ObjectStep(this, NullObjectID.NULL_37305, new WorldPoint(2771, 6109, 0),
			"Touch the symbol in the building north of Elena.");
		checkSymbol2.setWorldMapPoint(new WorldPoint(2323, 3165, 0));
		checkSymbol3 = new ObjectStep(this, NullObjectID.NULL_37306, new WorldPoint(2789, 6127, 0), "Touch the symbol in the General Store.");
		checkSymbol3.setWorldMapPoint(new WorldPoint(2341, 3183, 0));

		goToSecondFloorSymbols = new ObjectStep(this, ObjectID.LADDER_8744, new WorldPoint(2783, 6127, 0), "Go up to the top floor of Lletya.");
		goToSecondFloorSymbols.setWorldMapPoint(new WorldPoint(2335, 3183, 0));
		checkSymbol4 = new ObjectStep(this, NullObjectID.NULL_37304, new WorldPoint(2783, 6129, 1), "Touch the symbol above the General Store.");
		checkSymbol4.setWorldMapPoint(new WorldPoint(2335, 3185, 1));
		checkSymbol5 = new ObjectStep(this, NullObjectID.NULL_37303, new WorldPoint(2800, 6118, 1), "Touch the symbol at the altar.");
		checkSymbol5.setWorldMapPoint(new WorldPoint(2352, 3174, 1));

		buildStatue = new ObjectStep(this, NullObjectID.NULL_37309, new WorldPoint(2786, 6116, 0),
			"Make a statue in the middle of Lletya.", saw, hammer, limestoneBricks8);
		buildStatue.setWorldMapPoint(new WorldPoint(2338, 3172, 0));

		talkToIthell = new NpcStep(this, NpcID.LADY_KELYN_ITHELL, new WorldPoint(2783, 6115, 0), "Talk to Lady Kelyn Ithell in Lletya.");
		talkToIthell.setWorldMapPoint(new WorldPoint(2334, 3172, 0));

		digNearSwamp = new DigStep(this, new WorldPoint(2225, 3133, 0), "Dig at the marked spot near Port Tyras.");
		digNearSTASH = new DigStep(this, new WorldPoint(2203, 3061, 0), "Dig at the marked spot in Zul-Andra.");
		digOutsidePrif = new DigStep(this, new WorldPoint(2240, 3269, 0), "Dig outside Prifiddinas's south gate.");
		spinOutsidePass = new EmoteStep(this, QuestEmote.SPIN, new WorldPoint(2309, 3215, 0),
			"Spin outside of the Underground Pass's cave entrance with nothing equipped.", nothingEquipped);

		talkToMeilyr = new NpcStep(this, NpcID.LADY_FFION_MEILYR, new WorldPoint(2311, 3215, 0), "Talk to Lady Meilyr.");

		enterWellCaveForFight = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_4006, new WorldPoint(2314, 3217, 0),
			"Enter the Underground Pass from the Elven Lands entrance, and make your way to the dwarven camp. Make sure you're prepared for battle.", combatGear);
		enterWellOfVoyage = new ObjectStep(this, ObjectID.WELL_4005, new WorldPoint(2342, 9623, 0), "Enter the Well of Voyage.");
		leaveIbanRoom = new ObjectStep(this, ObjectID.DOOR_3333, new WorldPoint(2016, 4712, 1),
			"Leave the well area, and head to the dwarven camp below to the south.");
		goDownToDwarves = new ObjectStep(this, ObjectID.CAVE_3222, new WorldPoint(2150, 4545, 1),
			"Enter the south cave to go to the lower level of the pass.");
		goDownToDwarves.addDialogStep("Yes.");
		enterWellCaveForFight.addSubSteps(enterWellOfVoyage, leaveIbanRoom, goDownToDwarves);

		talkToBaxAtDwarves = new NpcStep(this, NpcID.BAXTORIAN_8809, new WorldPoint(2444, 6092, 0), "Talk to Baxtorian in the dwarven camp.");
		talkToElenaAtDwarves = new NpcStep(this, NpcID.ELENA_8791, new WorldPoint(2452, 6091, 0), "Talk to Elena in the dwarven camp.");
		talkToElenaAtDwarves.addDialogStep("Baxtorian said you had an idea to help our defence.");
		fillHole1 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2457, 6109, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole2 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2459, 6114, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole3 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2456, 6119, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole4 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2452, 6122, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole5 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2450, 6127, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole6 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2444, 6129, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole7 = new ObjectStep(this, ObjectID.HOLE_35888, new WorldPoint(2439, 6126, 0),
			"Fill the holes around the camp's entrance.", explosivePotion);
		fillHole1.addSubSteps(fillHole2, fillHole3, fillHole4, fillHole5, fillHole6, fillHole7);

		talkToBaxAfterFillingHoles = new NpcStep(this, NpcID.BAXTORIAN_8809, new WorldPoint(2444, 6092, 0),
				"Talk to Baxtorian in the dwarven camp.");
		talkToBaxAfterFillingHoles.addDialogStep("I'm ready.");
		defendDwarfCamp = new NpcStep(this, NpcID.IORWERTH_ARCHER_8953, "Defend the camp.", true);
		((NpcStep) defendDwarfCamp).addAlternateNpcs(NpcID.IORWERTH_ARCHER_8954, NpcID.IORWERTH_WARRIOR_8955, NpcID.IORWERTH_WARRIOR_8956);
		defeatEssyllt = new NpcStep(this, NpcID.ESSYLLT_8950, new WorldPoint(2442, 6094, 0),
				"Defeat Essyllt. Protect from melee. He can drain your stats, so drink super restores when needed.");
		defeatEssyllt.addText("When he shoves you, flick to Protect from Ranged then back to Protect from Melee.");
		enterWellCaveForFragmentFight = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_4006, new WorldPoint(2314, 3217, 0),
			"Enter the Underground Pass from the Elven Lands entrance, and make your way to the dwarven camp. Make sure you're prepared for battle with magic gear.");
		defeatFragmentOfSeren = new NpcStep(this, NpcID.FRAGMENT_OF_SEREN, "");
		defeatFragmentOfSeren.addSubSteps(enterWellCaveForFragmentFight);

		defeatFragmentSidebar = new DetailedQuestStep(this, "Defeat the Fragment of Seren. Protect from Ranged, and keep your distance.");
		defeatFragmentSidebar.addText("She is only susceptible to magic damage, so bring your best magic gear.");
		defeatFragmentSidebar.addText("If she moves you close to her and locks you in place, she is about to hit 99% of your max health. Eat up.");
		defeatFragmentSidebar.addText("If she spawns a healing spell, attack it to destroy it.");
		defeatFragmentSidebar.addText("If she splits into multiple fragments, attack the light coloured one.");

		talkToBaxToFinish = new NpcStep(this, NpcID.BAXTORIAN, new WorldPoint(2241, 3266, 0),
			"Talk to Baxtorian outside of Prifddinas to finish the quest, and unlock Prifddinas!");
	}

	public void setupConditionalSteps()
	{
		goToF0Steps = new ConditionalStep(this, talkToAmlodd, "Go to the bottom floor of the library.");
		goToF0Steps.addStep(inLibraryF1, goF1ToF0);
		goToF0Steps.addStep(inLibraryF2, goF2ToF1);

		goToF1Steps = new ConditionalStep(this, talkToAmlodd, "Go to the middle floor of the library.");
		goToF1Steps.addStep(inLibraryF2, goF2ToF1);
		goToF1Steps.addStep(inLibraryF0, goF0ToF1);

		goToF2Steps = new ConditionalStep(this, talkToAmlodd, "Go to the top floor of the library.");
		goToF2Steps.addStep(inLibraryF1, goF1ToF2);
		goToF2Steps.addStep(inLibraryF0, goF0ToF1);

		goToF2NWSteps = new ConditionalStep(this, talkToAmlodd, "Go to the north west top floor of the library.");
		goToF2NWSteps.addStep(inLibraryF1, goF1ToF2NW);
		goToF2NWSteps.addStep(inLibraryF0, goF0ToF1);

		goToF2SWSteps = new ConditionalStep(this, talkToAmlodd, "Go to the south west top floor of the library.");
		goToF2SWSteps.addStep(inLibraryF1, goF1ToF2SW);
		goToF2SWSteps.addStep(inLibraryF0, goF0ToF1);

		goToF2ESteps = new ConditionalStep(this, talkToAmlodd, "Go to the top floor by the east stairs.");
		goToF2ESteps.addStep(inLibraryF1, goF1ToF2E);
		goToF2ESteps.addStep(inLibraryF0, goF0ToF1);

		cadarnPuzzle = new CadarnLightPuzzle(this, goToF0Steps, goToF1Steps);
		cadarnPuzzle.addDialogStep("Yes please.");

		crwysLightPuzzle = new CrwysLightPuzzle(this, goToF1Steps.copy());
		crwysLightPuzzle.addDialogStep("Yes please.");

		amloddLightPuzzle = new AmloddLightPuzzle(this, goToF1Steps.copy(), goToF0Steps.copy());
		amloddLightPuzzle.addDialogStep("Yes please.");

		meilyrLightPuzzle = new MeilyrLightPuzzle(this, goToF1Steps.copy(), goToF0Steps.copy());
		meilyrLightPuzzle.addDialogStep("Yes please.");

		hefinLightPuzzle = new HefinLightPuzzle(this, goToF1Steps.copy(), goToF0Steps.copy());
		hefinLightPuzzle.addDialogStep("Yes please.");

		trahaearnLightPuzzle = new TrahaearnLightPuzzle(this, goToF1Steps.copy());
		trahaearnLightPuzzle.addDialogStep("Yes please.");

		iorwerthLightPuzzle = new IorwerthLightPuzzle(this, goToF1Steps.copy(), goToF0Steps.copy());
		iorwerthLightPuzzle.addDialogStep("Yes please.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(steelFullHelm, steelPlatebody, steelPlatelegs, redDye,
			purpleDye, silk, mournersOutfit, runiteBar, limestoneBricks8, tinderbox, rope,
			natureRune, iritLeafOrFlowers, adamantChainbody, wineOfZamorakOrZamorakBrew,
			cabbage, blackKnifeOrBlackDagger, cadantineSeed, seedDibber,
			vialOfWater, pestleAndMortar, hammer, saw, pickaxe, axe, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(teleCrystal, iorwerthCampTeleport, gamesNecklace, combatGear, magicCombatGear, saradominBrews, superRestorePotions);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Many Mourners, Paladins and Knights of Ardougne", "Arianwyn" +
			" (level 182)", "Essyllt (level 236)", "Fragment of Seren (level 494)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.MOURNINGS_END_PART_II, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.MAKING_HISTORY, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 70));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 70));
		req.add(new SkillRequirement(Skill.FARMING, 70));
		req.add(new SkillRequirement(Skill.HERBLORE, 70));
		req.add(new SkillRequirement(Skill.HUNTER, 70));
		req.add(new SkillRequirement(Skill.MINING, 70));
		req.add(new SkillRequirement(Skill.SMITHING, 70));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 70));
		return req;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new ItemRequirement("Recommended: 75 Magic", -1, -1));
		req.add(new ItemRequirement("Recommended: 40+ Prayer", -1, -1));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.AGILITY, 20000),
				new ExperienceReward(Skill.CONSTRUCTION, 20000),
				new ExperienceReward(Skill.CONSTRUCTION, 20000),
				new ExperienceReward(Skill.FARMING, 20000),
				new ExperienceReward(Skill.HERBLORE, 20000),
				new ExperienceReward(Skill.MINING, 20000),
				new ExperienceReward(Skill.SMITHING, 20000),
				new ExperienceReward(Skill.WOODCUTTING, 20000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Prifddinas"),
				new UnlockReward("Ability to fight Zalcano"),
				new UnlockReward("Access to The Gauntlet"),
				new UnlockReward("Access to Trahaearn Mine"),
				new UnlockReward("Access to Iorwerth Dungeon"),
				new UnlockReward("Ability to respawn at Prifddinas"),
				new UnlockReward("Ability to move POH to Prifddinas"),
				new UnlockReward("Prifddinas Teleport added to Crystal Teleport Seed"),
				new UnlockReward("Ability to make Divine Potions"),
				new UnlockReward("Ability to make Crystal Tools, Weapons and Armor")
		);
	}

	@Override
	public List<PanelDetails> getPanels() {
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToEdmond, talkToLathas, talkToEdmondAgain, talkToAlrena,
				useRedDyeOnSteelFullHelm, useTabardOnPlatebody, talkToEdmondWithOutfit, goDownstairsCastle, talkToElenaInCell, goUpFromCastleBasement,
				searchElenaCabinet, talkToElenaInCellAgain, talkToEdmondAfterFreeingElena), steelFullHelm, steelPlatebody, steelPlatelegs, redDye, purpleDye,
				silk, mournersOutfit, tinderbox));

		allSteps.add(new PanelDetails("Inciting the citizens", Arrays.asList(searchBed, talkToElenaInHideout, leaveHideout, useTinderboxOnGrain,
				talkToPriest, useTinderboxOnChurchGrain, talkToSarah, useTinderboxOnSWGrain, talkToChadwell, talkToSilverMerchant, talkToBaker1, talkToGemMerchant,
				talkToFurTrader, talkToSpiceSeller, talkToSilkMerchant, talkToTownCrier, talkToZenesha, talkToEstateAgent, talkToProbita, talkToAemad, talkToPriest2,
				talkToOrbon), tinderbox, mournersOutfit, ardyFullHelm, ardyPlatebody, steelPlatelegs));

		allSteps.add(new PanelDetails("Overthrowing the King", Arrays.asList(talkToElenaForFight, defeat10Mourners, mournerBattleGoDownTrapdoor,
				defeat11Mourners, talkToElenaForEastArdyFight, defeat15Knights, talkToLathasAfterFight, watchArdyFightCutscene), combatGear));

		allSteps.add(new PanelDetails("Helping Arianwyn", Arrays.asList(talkToArianwyn, talkToYsgawyn, talkToElenaInLletya, talkToArianwynAfterElena,
				talkToArianwynAfterMeeting)));

		allSteps.add(new PanelDetails("Freeing Baxtorian", Arrays.asList(boardRaft, useRopeOnRock, useRopeOnTree, enterFalls, searchCrateForKey,
				enterBaxThroneRoom, talkToArianwynInBax, pillarPuzzle, talkToBax),
				rope, iritLeafOrFlowers, cabbage, wineOfZamorakOrZamorakBrew, natureRune, adamantChainbody, blackKnifeOrBlackDagger));

		allSteps.add(new PanelDetails("Finding Amlodd", Arrays.asList(talkToArianwynAfterBax, talkToElenaAfterBax, plantCadantine, talkToBaxInLletya,
				talkToElunedWithSeed, rubCrystal, talkToFigure), seedDibber, cadantineSeed));

		allSteps.add(new PanelDetails("Finding Trahaearn", Arrays.asList(talkToBaxAfterValley, enterCaveToWell, usePickaxeOnWall, enterCrevice,
				useHammerOnExoskeleton, talkToTrahaearn), pickaxe, hammer, runiteBar));

		allSteps.add(new PanelDetails("Making the potion", Arrays.asList(talkToElenaAfterTra, talkToArianwynAfterTra, usePestleOnCrystal,
				useCadantineOnVial, useDustOnVial, talkToArianwynAfterPotion), pestleAndMortar, vialOfWater));

		allSteps.add(new PanelDetails("Entering the Crystal", Arrays.asList(talkToAmlodd, goTouchFirstSeal)));

		allSteps.add(new PanelDetails("Ithell Seal", Arrays.asList(openIthellSeal, goTouchIthellSeal)));

		List<QuestStep> cadarnSteps = cadarnPuzzle.getDisplaySteps();
		cadarnSteps.add(goTouchCadarnSeal);
		allSteps.add(new PanelDetails("Cadarn Seal", cadarnSteps));

		List<QuestStep> crwysSteps = crwysLightPuzzle.getDisplaySteps();
		crwysSteps.add(goTouchCrwysSeal);
		allSteps.add(new PanelDetails("Crwys Seal", crwysSteps));

		List<QuestStep> amloddSteps = amloddLightPuzzle.getDisplaySteps();
		amloddSteps.add(goTouchAmloddSeal);
		allSteps.add(new PanelDetails("Amlodd Seal", amloddSteps));

		List<QuestStep> meilyrSteps = meilyrLightPuzzle.getDisplaySteps();
		meilyrSteps.add(goTouchMeilyrSeal);
		allSteps.add(new PanelDetails("Meilyr Seal", meilyrSteps));

		List<QuestStep> hefinSteps = hefinLightPuzzle.getDisplaySteps();
		hefinSteps.add(goTouchHefinSeal);
		allSteps.add(new PanelDetails("Hefin Seal", hefinSteps));

		List<QuestStep> trahaearnSteps = trahaearnLightPuzzle.getDisplaySteps();
		trahaearnSteps.add(goTouchTrahaearnSeal);
		allSteps.add(new PanelDetails("Trahaearn Seal", trahaearnSteps));

		List<QuestStep> iorwerthSteps = iorwerthLightPuzzle.getDisplaySteps();
		iorwerthSteps.add(goTouchIorwerthSeal);
		allSteps.add(new PanelDetails("Iorwerth Seal", iorwerthSteps));

		allSteps.add(new PanelDetails("Finding Crwys and Hefin", Arrays.asList(leaveLibrary, talkToArianwynAfterLightPuzzle,
				askBaxtorianHowToFindCrwys,
				crossSteppingStones, chopTrees, talkToCrwys), axe));

		allSteps.add(new PanelDetails("Retrieving the orb", Arrays.asList(talkToBaxAfterHefinAndCrwys, enterIorwerthCave, kill2IorwerthCaves,
				searchChestForOrb,
				talkToBaxWithOrb), combatGear));

		allSteps.add(new PanelDetails("Locating Ysgawyn", Arrays.asList(talkToElenaAfterOrb, inspectMushrooms, track1, track2, track3, track4,
				track5, track6, talkToYsgawynInClearing), combatGear));

		allSteps.add(new PanelDetails("Defending Lletya", Arrays.asList(enterLletyaForFighting, searchTableForEoin, goUpLletyaLadder,
				searchCookingPots, fightArianwyn), combatGear));

		allSteps.add(new PanelDetails("Finding Ithell and Meilyr", Arrays.asList(talkToBaxAfterLletyaFightForIthellClue, checkSymbol1,
				checkSymbol2, checkSymbol3, goToSecondFloorSymbols, checkSymbol4, checkSymbol5, buildStatue, talkToIthell, digNearSwamp, digNearSTASH,
				digOutsidePrif, spinOutsidePass, talkToMeilyr), limestoneBricks8, saw, hammer, spade, combatGear));

		allSteps.add(new PanelDetails("Defending the Temple", Arrays.asList(enterWellCaveForFight, talkToBaxAtDwarves, talkToElenaAtDwarves,
				fillHole1, talkToBaxAfterFillingHoles, defendDwarfCamp, defeatEssyllt), combatGear));

		allSteps.add(new PanelDetails("Defeat the Fragment", Arrays.asList(defeatFragmentSidebar, talkToBaxToFinish),
				magicCombatGear, saradominBrews, superRestorePotions));

		return allSteps;
	}
}
