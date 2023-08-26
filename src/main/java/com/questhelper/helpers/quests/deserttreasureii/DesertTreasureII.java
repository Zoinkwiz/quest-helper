/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.Icon;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import static com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan.createLeviathan;
import static com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan.showBlueHitsplatUntilTick;
import static com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan.showRedHitsplatUntilTick;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_TREASURE_II
)

public class DesertTreasureII extends BasicQuestHelper
{
	DetailedQuestStep attemptToEnterVaultDoor, attemptToEnterVaultDoor2, talkToAsgarnia,
		inspectStatueSE, inspectStatueSW, inspectStatueNE, inspectStatueNW, inspectPlaque,
		talkToAsgarniaAgain, talkToBalando, operateWinch, talkToBanikan, getPickaxe, mineRocks,
		enterDigsiteHole, killAncientGuardian, enterDigsiteHoleAgain, talkToBanikanInGolemRoom,
		inspectGolem, inspectAltar, castOnBloodStatue, castOnIceStatue, castOnShadowStatue,
		castOnSmokeStatue, searchCrateForCharges, imbueAtAltar, chargeGolem, operateGolem, searchVardorvis,
		searchPerseriya, searchSucellus, searchWhisperer, askAboutVardorvis, askAboutPerseriya, askAboutSucellus,
		askAboutWhisperer, talkToBanikanAfterGolem, operateGolemFrostenhorn;


	GolemPuzzleStep solveGolemPuzzle;

	SpellbookRequirement ancientMagicksActive;

	ItemRequirement waterSource, senntistenTeleport, pickaxe, combatGear, bloodBurstRunes, iceBurstRunes,
		shadowBurstRunes, smokeBurstRunes, allBursts, uncharedCells, chargedCells, xericTalisman,
		facemask, staminaPotions, eyeTeleport, rangedCombatGear, food, prayerPotions, nardahTeleport;

	Zone vault, digsiteHole, golemRoom;
	Requirement inVault, inDigsiteHole, inGolemRoom;

	Requirement inspectedStatueSE, inspectedStatueSW, inspectedStatueNE, inspectedStatueNW, inspectedPlaque,
	inspectedGolem, inspectedAltar, bloodBeenCast, iceBeenCast, shadowBeenCast, smokeBeenCast, castAllSpells, inPuzzle;

	Requirement searchedVardorvis, searchedPerseriya, searchedSucellus, searchedWhisperer, askedAboutVardorvis,
		askedAboutPerseriya, askedAboutSucellus, askedAboutWhisperer;

	/* Vardorvis */
	DetailedQuestStep talkToElissa, talkToBarus, searchDesk, readPotionNote, drinkPotion, boardBoat, runIntoStanglewood,
		talkToKasonde, enterEntry, defendKasonde, defendKasondeSidebar, leaveTowerDefenseRoom, talkToKasondeAfterTowerDefense,
		getBerry, getHerb, getHerbSidebar, goDownToKasonde, defendKasondeHerb, talkToKasondeWithHerbAndBerry, addHerb, addBerry,
		drinkStranglewoodPotion, goToRitualSite, fightVardorvis, fightVardorvisSidebar, pickUpTempleKey, getTempleKeyFromRocks,
		returnToDesertWithVardorvisMedallion, useVardorvisMedallionOnStatue;

	ConditionalStep returnToKasondeWithTempleKey, defeatKasonde, goTalkToKasondeAfterFight, goGetVardorvisMedallion;
	Requirement talkedToElissa, talkedToBarus, haveReadPotionNote, haveDrunkPotion, inStrangewood, finishedStranglewoodCutscene,
		talkedToKasonde, inTowerDefenseRoom, defendedKasonde, toldAboutHerbAndBerry, herbTaken, berryTaken,
		inStranglewoodPyramidRoom, defendedKasondeWithHerb, receivedSerum, addedHerb, addedBerry, drankPotion,
		inAnyStranglewood, inVardorvisArea, defeatedVardorvis, templeKeyNearby, kasondeAggressive, givenKasondeKey, defeatedKasonde,
		kasondeRevealedMedallion, gotVardorvisMedallion, finishedVardorvis;
	ItemRequirement potionNote, strangePotion, freezes, berry, herb, unfinishedSerum, serumWithHerb, stranglerSerum, templeKey,
		vardorvisMedallion;
	Zone stranglewood, towerDefenseRoom, stranglewoodPyramidRoom, vardorvisArea;

	/* Perseriya */
	DetailedQuestStep enterWizardBasement, enterPortalToTempleOfTheEye, killDemons, hopOverSteppingStone, talkToPersten, enterPassage1,
		enterPathfinderRoom;

	ConditionalStep goTalkToCatalyticGuardian, goKillDemons, goBoardBoat, goTalkToPersten, goDoPassage1;

	Requirement inWizardBasement, inTempleOfTheEye, inDemonArea, inTentArea, defeatedDemons, attemptedToBoardBoat, talkedToPersten,
		inAbyssRoom1, inAbyssRoom2, inAbyssRoom3, destroyedTether;

	Zone wizardBasement, templeOfTheEye, templeOfTheEye2, demonArea, tentArea, abyssRoom1, abyssRoom2, abyssRoom3, growthRoom;

	Zone path1, path1P2, path2, path3, path4, path5, path6, nearCatalystRoom1, nearCatalystRoom2, catalystRoom, nearGrowth1,
		nearGrowth2, boatRoom1;
	Requirement onPath1, onPath2, onPath3, onPath4, onPath5, onPath6, isNearCatalystRoom, inCatalystRoom, completedCatalystRoom,
		isNearGrowthRoom, growthRepairedSE, growthRepairedSW, growthRepairedNE, growthRepairedNW, inGrowthRoom, repairedGrowths,
		solvedGrowthRoom, inBoatRoom1, haveReadOldTablet, completedRoom1, talkedToPerstenAfterRoom1;
	DetailedQuestStep doPath1, doPath2, doPath3, doPath4, doPath5, doPath6, enterGreenTeleporter1, enterCatalystRoom, solveCatalystRoom,
		enterBlueTeleporter1, enterGrowthRoom, repairGrowths, growthPuzzle, returnThroughBlueNeuralTeleporter, enterBoatRoom1, getTinderbox,
		burnBoat1, searchSkeletonForKey, searchSkeletonForGunpowder, getOldTablet, readOldTablet, talkToPerstenAfterRoom1;

	// Room 2
	DetailedQuestStep enterSouthEastPassage, enterAxonRoom, hitCosmicAxon, hitFireAxon, hitNatureAxon, hitWaterAxon,
		enterBlueTeleporter2, enterNerveRoom, getEarthNerve, getWaterNerve, getFireNerve, getAirNerve, makeDustNerve, makeLavaNerve,
		makeSmokeNerve, makeSteamNerve, repairLavaNerve, repairSmokeNerve, repairDustNerve, repairSteamNerve, makeMatchingNerves, returnThroughBlueNeuralTeleporter2,
		enterGreenTeleporter2, enterSummoningRoom, killImps, killLesserDemons, enterBoatRoom2, getTinderboxRoom2, getGunpowderRoom2, getSlimyKey, getDampTablet,
		readDampTablet, burnBoat2, talkToPerstenAfterRoom2, enterMiddlePassage;
	ItemRequirement oldTablet, slimyKey, gunpowder, tinderbox, fireNerve, airNerve, waterNerve, earthNerve, dustNerve, lavaNerve,
		smokeNerve, steamNerve, dampTablet, illuminatingLure;
	Requirement inAxonRoom, cosmicAxonPresent, waterAxonPresent, natureAxonPresent, fireAxonPresent, completedAxonRoom, nothingInHands,
		inNorthOfAbyssRoom2, inNerveRoom, inSummoningRoom, inBoatRoom2, steamNerveBroken, lavaNerveBroken, dustNerveBroken,
		smokeNerveBroken, completedNerveRoom, inNervePassage, impsNearby, completedSummoningRoom, haveReadDampTablet, completedRoom2,
		talkedToPerstenAfterRoom2;
	Zone axonRoom1, axonRoom2, axonRoom3, northAbyssRoom2P1, northAbyssRoom2P2, northAbyssRoom2P3, northAbyssRoom2P4, northAbyssRoom2P5,
		nerveRoom1, nerveRoom2, nerveRoom3, summoningRoom1, summoningRoom2, summoningRoom3, boatRoom2, nervePassage;

	// Room 3
	MemoryPuzzle memoryPuzzleSteps;
	DetailedQuestStep enterMemoryPuzzle, enterTreeRoom, killTreeMonsters, enterLightLeechRoom, repairGrowthsRoom3,
		enterGreenTeleporter3, repairCrimsonVeins, repairRadiantVeins, returnThroughGreenPortal,
		enterBoatRoom3, getTinderBoxRoom3, getGunpowderRoom3, getSlimyKeyRoom3, getDampTablet2,
		readDampTablet2, burnBoat3, getLure, killCrimson, killRadiant;

	ItemRequirement dampTablet2, radiantFibre, crimsonFibre, perseriyasMedallion;

	Requirement inMemoryPuzzle, inTreeRoom, inLeechRoom, solvedMemoryRoom, solvedTreeRoom,
		solvedLeechRoom, inSwRoom3, inBoatRoom3, haveReadDampTablet2, repairedGrowthRoom3, repairedCrimsonVeins,
		protectFromMagic, completedRoom3;

	Zone memoryPuzzle, treeRoom, leechRoom, swRoom3P1, swRoom3P2, swRoom3P3, swRoom3P4, swRoom3P5, boatRoom3;

	DetailedQuestStep talkToPerstenAfterRooms, boardBoatToLeviathan, killLeviathan,
		killLeviathanSidebar, climbDownFromLeviathan, hopAcrossFromLeviathan, talkToPerstenAtShip,
		searchDebris, returnToDesertWithPerseriyasMedallion, usePerseriyasMedallionOnStatue;

	ConditionalStep goKillLeviathan, goToShip;

	Zone leviathanArea, neLeviathanArea;

	Requirement inLeviathanArea, readyToFightLeviathan, inNELeviathanArea, defeatedLeviathan, perstenAtShip,
		perstenLeft, foundPerseriyasMedallion, finishedPerseriya;


	DetailedQuestStep moreComingSoon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, attemptToEnterVaultDoor);
		steps.put(2, attemptToEnterVaultDoor);

		ConditionalStep goTalkToAsgarnia = new ConditionalStep(this, attemptToEnterVaultDoor2);
		goTalkToAsgarnia.addStep(inVault, talkToAsgarnia);
		steps.put(4, goTalkToAsgarnia);

		ConditionalStep inspectTheVault = new ConditionalStep(this, attemptToEnterVaultDoor2);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque, inspectedStatueNE,
			inspectedStatueNW, inspectedStatueSW), inspectStatueSE);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque, inspectedStatueNE,
			inspectedStatueNW), inspectStatueSW);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque, inspectedStatueNE), inspectStatueNW);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque), inspectStatueNE);
		inspectTheVault.addStep(inVault, inspectPlaque);
		steps.put(6, inspectTheVault);

		ConditionalStep goTalkToAsgarniaAgain = new ConditionalStep(this, attemptToEnterVaultDoor2);
		goTalkToAsgarniaAgain.addStep(inVault, talkToAsgarniaAgain);
		steps.put(8, goTalkToAsgarniaAgain);

		steps.put(10, talkToBalando);

		ConditionalStep goTalkToBanikan = new ConditionalStep(this, operateWinch);
		goTalkToBanikan.addStep(inDigsiteHole, talkToBanikan);

		steps.put(12, goTalkToBanikan);
		steps.put(14, goTalkToBanikan);
		steps.put(16, goTalkToBanikan);

		ConditionalStep goMineRocks = new ConditionalStep(this, operateWinch);
		goMineRocks.addStep(new Conditions(inDigsiteHole, pickaxe), mineRocks);
		goMineRocks.addStep(inDigsiteHole, getPickaxe);
		steps.put(18, goMineRocks);

		ConditionalStep goKillGolem = new ConditionalStep(this, operateWinch);
		goKillGolem.addStep(inGolemRoom, killAncientGuardian);
		goKillGolem.addStep(inDigsiteHole, enterDigsiteHole);
		steps.put(20, goKillGolem);
		steps.put(22, goKillGolem);

		ConditionalStep goTalkToBanikanAfterGolem = new ConditionalStep(this, operateWinch);
		goTalkToBanikanAfterGolem.addStep(inGolemRoom, talkToBanikanInGolemRoom);
		goTalkToBanikanAfterGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(24, goTalkToBanikanAfterGolem);
		steps.put(26, goTalkToBanikanAfterGolem);

		ConditionalStep goInspectAltarAndGolem = new ConditionalStep(this, operateWinch);
		// Go cast magic on pillars
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, chargedCells), chargeGolem);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, castAllSpells, uncharedCells),
			imbueAtAltar);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, castAllSpells), searchCrateForCharges);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, bloodBeenCast, shadowBeenCast,
				smokeBeenCast), castOnIceStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, bloodBeenCast, shadowBeenCast),
			castOnSmokeStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, bloodBeenCast),
			castOnShadowStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, inspectedAltar),
			castOnBloodStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem), inspectAltar);
		goInspectAltarAndGolem.addStep(inGolemRoom, inspectGolem);
		goInspectAltarAndGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(28, goInspectAltarAndGolem);
		steps.put(30, goInspectAltarAndGolem);

		ConditionalStep goDoGolemPuzzle = new ConditionalStep(this, operateWinch);
		goDoGolemPuzzle.addStep(new Conditions(inGolemRoom, inPuzzle), solveGolemPuzzle);
		goDoGolemPuzzle.addStep(inGolemRoom, chargeGolem);
		goDoGolemPuzzle.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(32, goDoGolemPuzzle);
		// Post-golem, 15110 0->1

		ConditionalStep goOperateGolem = new ConditionalStep(this, operateWinch);
		goOperateGolem.addStep(inGolemRoom, operateGolem);
		goOperateGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(34, goOperateGolem);

		ConditionalStep goSearchGolem = new ConditionalStep(this, operateWinch);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, askedAboutSucellus,
			askedAboutWhisperer), talkToBanikanAfterGolem);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, askedAboutSucellus,
			searchedWhisperer), askAboutWhisperer);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, askedAboutSucellus), searchWhisperer);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, searchedSucellus), askAboutSucellus);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya), searchSucellus);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, searchedPerseriya), askAboutPerseriya);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis), searchPerseriya);
		goSearchGolem.addStep(new Conditions(inGolemRoom, searchedVardorvis), askAboutVardorvis);
		goSearchGolem.addStep(inGolemRoom, searchVardorvis);
		goSearchGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(36, goSearchGolem);

		ConditionalStep goOperateGolemLastTime = new ConditionalStep(this, operateWinch);
		goOperateGolemLastTime.addStep(inGolemRoom, operateGolemFrostenhorn);
		goOperateGolemLastTime.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(38, goOperateGolemLastTime);
		steps.put(40, goOperateGolemLastTime);

		ConditionalStep findingVardorvis = new ConditionalStep(this, talkToElissa);
		findingVardorvis.addStep(new Conditions(gotVardorvisMedallion, inVault), useVardorvisMedallionOnStatue);
		findingVardorvis.addStep(gotVardorvisMedallion, returnToDesertWithVardorvisMedallion);
		findingVardorvis.addStep(kasondeRevealedMedallion, goGetVardorvisMedallion);
		findingVardorvis.addStep(defeatedKasonde, goTalkToKasondeAfterFight);
		findingVardorvis.addStep(kasondeAggressive, defeatKasonde);
		findingVardorvis.addStep(defeatedVardorvis, returnToKasondeWithTempleKey);
		findingVardorvis.addStep(new Conditions(inVardorvisArea, defeatedVardorvis), pickUpTempleKey);
		findingVardorvis.addStep(new Conditions(inVardorvisArea, drankPotion), fightVardorvis);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, drankPotion), goToRitualSite);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, addedBerry), drinkStranglewoodPotion);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, addedHerb), addBerry);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, receivedSerum), addHerb);
		findingVardorvis.addStep(new Conditions(inStranglewoodPyramidRoom, defendedKasondeWithHerb, herbTaken, berryTaken),
			talkToKasondeWithHerbAndBerry);
		findingVardorvis.addStep(new Conditions(inStranglewoodPyramidRoom, herbTaken, berryTaken), defendKasondeHerb);
		findingVardorvis.addStep(new Conditions(inStrangewood, herbTaken, berryTaken), goDownToKasonde);
		findingVardorvis.addStep(new Conditions(inStrangewood, herbTaken), getBerry);
		findingVardorvis.addStep(new Conditions(inStrangewood, toldAboutHerbAndBerry), getHerb);
		findingVardorvis.addStep(new Conditions(inTowerDefenseRoom, defendedKasonde), leaveTowerDefenseRoom);
		findingVardorvis.addStep(new Conditions(inStrangewood, defendedKasonde), talkToKasondeAfterTowerDefense);
		findingVardorvis.addStep(new Conditions(inTowerDefenseRoom), defendKasonde);
		findingVardorvis.addStep(new Conditions(inStrangewood, talkedToKasonde), enterEntry);
		findingVardorvis.addStep(new Conditions(inStrangewood, finishedStranglewoodCutscene), talkToKasonde);
		findingVardorvis.addStep(new Conditions(inStrangewood), runIntoStanglewood);
		findingVardorvis.addStep(new Conditions(haveDrunkPotion), boardBoat);
		findingVardorvis.addStep(new Conditions(haveReadPotionNote, strangePotion.alsoCheckBank(questBank)), drinkPotion);
		findingVardorvis.addStep(new Conditions(nor(haveReadPotionNote), potionNote.alsoCheckBank(questBank)), readPotionNote);
		findingVardorvis.addStep(talkedToBarus, searchDesk);
		findingVardorvis.addStep(talkedToElissa, talkToBarus);

		ConditionalStep doPathfinderRoom = new ConditionalStep(this, enterPathfinderRoom);
		doPathfinderRoom.addStep(onPath6, doPath6);
		doPathfinderRoom.addStep(onPath5, doPath5);
		doPathfinderRoom.addStep(onPath4, doPath4);
		doPathfinderRoom.addStep(onPath3, doPath3);
		doPathfinderRoom.addStep(onPath2, doPath2);
		doPathfinderRoom.addStep(onPath1, doPath1);

		Conditions doingPathfinderRoom = new Conditions(LogicType.OR, onPath1, onPath2, onPath3, onPath4, onPath5, onPath6);

		ConditionalStep solveAbyssRoom1 = new ConditionalStep(this, enterPathfinderRoom);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom,
			haveReadOldTablet, gunpowder, tinderbox), burnBoat1);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom,
			haveReadOldTablet, gunpowder), getTinderbox);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, haveReadOldTablet),
			searchSkeletonForGunpowder);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, slimyKey),
			getOldTablet);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom),
			searchSkeletonForKey);
		solveAbyssRoom1.addStep(new Conditions(new Conditions(LogicType.OR, inGrowthRoom, isNearGrowthRoom),
			completedCatalystRoom, destroyedTether, solvedGrowthRoom), returnThroughBlueNeuralTeleporter);
		solveAbyssRoom1.addStep(new Conditions(completedCatalystRoom, destroyedTether), enterBoatRoom1);
		solveAbyssRoom1.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether, repairedGrowths), growthPuzzle);
		solveAbyssRoom1.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether), repairGrowths);
		solveAbyssRoom1.addStep(new Conditions(isNearGrowthRoom, completedCatalystRoom, destroyedTether), enterGrowthRoom);
		solveAbyssRoom1.addStep(new Conditions(completedCatalystRoom, destroyedTether), enterBlueTeleporter1);
		solveAbyssRoom1.addStep(new Conditions(inCatalystRoom, destroyedTether), solveCatalystRoom);
		solveAbyssRoom1.addStep(new Conditions(isNearCatalystRoom, destroyedTether), enterCatalystRoom);
		solveAbyssRoom1.addStep(destroyedTether, enterGreenTeleporter1);
		solveAbyssRoom1.addStep(doingPathfinderRoom, doPathfinderRoom);

		ConditionalStep repairNerve = new ConditionalStep(this, getWaterNerve);
		repairNerve.addStep(and(lavaNerveBroken, lavaNerve), repairLavaNerve);
		repairNerve.addStep(and(lavaNerveBroken, fireNerve, earthNerve), makeLavaNerve);
		repairNerve.addStep(and(lavaNerveBroken, fireNerve), getEarthNerve);
		repairNerve.addStep(and(lavaNerveBroken), getFireNerve);

		repairNerve.addStep(and(dustNerveBroken, dustNerve), repairDustNerve);
		repairNerve.addStep(and(dustNerveBroken, airNerve, earthNerve), makeDustNerve);
		repairNerve.addStep(and(dustNerveBroken, airNerve), getEarthNerve);
		repairNerve.addStep(and(dustNerveBroken), getAirNerve);

		repairNerve.addStep(and(smokeNerveBroken, smokeNerve), repairSmokeNerve);
		repairNerve.addStep(and(smokeNerveBroken, fireNerve, airNerve), makeSmokeNerve);
		repairNerve.addStep(and(smokeNerveBroken, fireNerve), getAirNerve);
		repairNerve.addStep(and(smokeNerveBroken), getFireNerve);

		repairNerve.addStep(and(steamNerve), repairSteamNerve);
		repairNerve.addStep(and(waterNerve, fireNerve), makeSteamNerve);
		repairNerve.addStep(and(waterNerve), getFireNerve);

		ConditionalStep solveAbyssRoom2 = new ConditionalStep(this, enterSouthEastPassage);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, haveReadDampTablet, tinderbox, gunpowder), burnBoat2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, haveReadDampTablet, tinderbox), getGunpowderRoom2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, haveReadDampTablet), getTinderboxRoom2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, slimyKey), getDampTablet);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2), getSlimyKey);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inNorthOfAbyssRoom2), enterBoatRoom2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, inSummoningRoom, impsNearby), killImps);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, inSummoningRoom), killLesserDemons);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, inNervePassage), returnThroughBlueNeuralTeleporter2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, inNorthOfAbyssRoom2), enterSummoningRoom);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom), enterGreenTeleporter2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, inNerveRoom), repairNerve);
		solveAbyssRoom2.addStep(and(completedAxonRoom, inNorthOfAbyssRoom2), enterNerveRoom);
		solveAbyssRoom2.addStep(completedAxonRoom, enterBlueTeleporter2);
		solveAbyssRoom2.addStep(and(inAxonRoom, cosmicAxonPresent), hitCosmicAxon);
		solveAbyssRoom2.addStep(and(inAxonRoom, fireAxonPresent), hitFireAxon);
		solveAbyssRoom2.addStep(and(inAxonRoom, natureAxonPresent), hitNatureAxon);
		solveAbyssRoom2.addStep(and(inAxonRoom, waterAxonPresent), hitWaterAxon);
		solveAbyssRoom2.addStep(inAbyssRoom2, enterAxonRoom);

		ConditionalStep solveAbyssRoom3 = new ConditionalStep(this, enterMemoryPuzzle);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, haveReadDampTablet2, tinderbox, gunpowder), burnBoat3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, haveReadDampTablet2, tinderbox), getGunpowderRoom3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, haveReadDampTablet2), getTinderBoxRoom3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, slimyKey), getDampTablet2);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3), getSlimyKeyRoom3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, solvedLeechRoom, inSwRoom3), returnThroughGreenPortal);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, solvedLeechRoom), enterBoatRoom3);

		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inLeechRoom, repairedGrowthRoom3, repairedCrimsonVeins, radiantFibre), repairRadiantVeins);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inLeechRoom, repairedGrowthRoom3, repairedCrimsonVeins), killRadiant);

		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inLeechRoom, repairedGrowthRoom3, crimsonFibre), repairCrimsonVeins);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inLeechRoom, repairedGrowthRoom3), killCrimson);

		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inLeechRoom, illuminatingLure), repairGrowthsRoom3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inLeechRoom), getLure);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom, inSwRoom3), enterLightLeechRoom);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, solvedTreeRoom), enterGreenTeleporter3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inTreeRoom), killTreeMonsters);
		solveAbyssRoom3.addStep(solvedMemoryRoom, enterTreeRoom);
		solveAbyssRoom3.addStep(inMemoryPuzzle, memoryPuzzleSteps);

		ConditionalStep findingPerseriya = new ConditionalStep(this, goTalkToCatalyticGuardian);
		findingPerseriya.addStep(and(inVault, foundPerseriyasMedallion), usePerseriyasMedallionOnStatue);
		findingPerseriya.addStep(foundPerseriyasMedallion, returnToDesertWithPerseriyasMedallion);
		findingPerseriya.addStep(and(perstenLeft, inNELeviathanArea), searchDebris);
		findingPerseriya.addStep(defeatedLeviathan, goToShip);
		findingPerseriya.addStep(readyToFightLeviathan, goKillLeviathan);
		findingPerseriya.addStep(new Conditions(inTentArea, completedRoom3), talkToPerstenAfterRooms);
		findingPerseriya.addStep(and(nor(haveReadDampTablet2), dampTablet2), readDampTablet2);
		findingPerseriya.addStep(inAbyssRoom3, solveAbyssRoom3);
		findingPerseriya.addStep(new Conditions(inTentArea, talkedToPerstenAfterRoom2), enterMiddlePassage);
		findingPerseriya.addStep(new Conditions(inTentArea, completedRoom2), talkToPerstenAfterRoom2);
		findingPerseriya.addStep(and(nor(haveReadDampTablet), dampTablet), readDampTablet);
		findingPerseriya.addStep(inAbyssRoom2, solveAbyssRoom2);
		findingPerseriya.addStep(new Conditions(inTentArea, talkedToPerstenAfterRoom1), enterSouthEastPassage);
		findingPerseriya.addStep(new Conditions(inTentArea, completedRoom1), talkToPerstenAfterRoom1);
		findingPerseriya.addStep(and(nor(haveReadOldTablet), oldTablet), readOldTablet);
		findingPerseriya.addStep(inAbyssRoom1, solveAbyssRoom1);
		findingPerseriya.addStep(talkedToPersten, goDoPassage1);
		findingPerseriya.addStep(attemptedToBoardBoat, goTalkToPersten);
		findingPerseriya.addStep(defeatedDemons, goBoardBoat);
		findingPerseriya.addStep(inDemonArea, goKillDemons);

		ConditionalStep findingTheFour = new ConditionalStep(this, findingVardorvis);
		findingTheFour.addStep(and(finishedVardorvis, finishedPerseriya), moreComingSoon);
		findingTheFour.addStep(finishedVardorvis, findingPerseriya);
		steps.put(42, findingTheFour);
		/* Entered stranglewood */
		steps.put(44, findingTheFour);
		/* Entered vardorvis arena */
		steps.put(46, findingTheFour);
		/* Defeated vardorvis */
		steps.put(48, findingTheFour);
		/* Obtained the vardorvis medallion */
		steps.put(50, findingTheFour);
		/* After talk with stranger */
		steps.put(52, findingTheFour);
		/* Placed vardorvis medallion */
		steps.put(54, findingTheFour);
		steps.put(56, findingTheFour);
		steps.put(58, findingTheFour);
		steps.put(60, findingTheFour);
		steps.put(62, findingTheFour);
		steps.put(64, findingTheFour);
		steps.put(66, findingTheFour);
		steps.put(68, findingTheFour);
		steps.put(70, findingTheFour);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		waterSource = new ItemRequirement("Full waterskin, or any protection from the desert", ItemID.CIRCLET_OF_WATER);
		waterSource.addAlternates(ItemID.DESERT_AMULET_4, ItemID.WATERSKIN4, ItemID.WATERSKIN3, ItemID.WATERSKIN2, ItemID.WATERSKIN1);
		ancientMagicksActive = new SpellbookRequirement(Spellbook.ANCIENT);

		// TODO: Add check where IF you have nardah teleport in scroll box, show scroll box as requirement
		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(5672, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_4);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_3, ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_2);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		ItemRequirement digsitePendant = new ItemRequirement("Digsite pendant", ItemID.DIGSITE_PENDANT_5);
		digsitePendant.addAlternates(ItemID.DIGSITE_PENDANT_4, ItemID.DIGSITE_PENDANT_3, ItemID.DIGSITE_PENDANT_2,
			ItemID.DIGSITE_PENDANT_1);

		// TODO: Make a spell SpellRequirement, which will change the highlight to be for the spellbook widget not runes
		// Check if have requirement + runes for spell
		senntistenTeleport = new ItemRequirements(LogicType.OR, "Senntisten teleport",
			new ItemRequirement("Senntisten teleport", ItemID.SENNTISTEN_TELEPORT),
			new ItemRequirements(
				new ItemRequirement("Law rune", ItemID.LAW_RUNE, 2),
				new ItemRequirement("Soul rune", ItemID.SOUL_RUNE)
			),
			digsitePendant
		);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		bloodBurstRunes = new ItemRequirements("Blood burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2));
		iceBurstRunes = new ItemRequirements("Ice burst runes",
		new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
		new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
		new ItemRequirement("Water runes", ItemID.WATER_RUNE, 4));
		shadowBurstRunes = new ItemRequirements("Shadow burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 1));
		smokeBurstRunes = new ItemRequirements("Smoke burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 2));
		allBursts = new ItemRequirements("Runes for shadow, smoke, blood, and ice burst",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 8),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 16),
			new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2),
			new ItemRequirement("Water runes", ItemID.WATER_RUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 3),
			new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 2));

		rangedCombatGear = new ItemRequirement("Ranged combat gear", -1);
		rangedCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		xericTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERICS_TALISMAN);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS, 20);
		facemask = new ItemRequirement("Facemask", ItemCollections.SLAYER_HELMETS);
		facemask.addAlternates(ItemID.FACEMASK, ItemID.GAS_MASK);

		eyeTeleport = new ItemRequirement("Teleport to Temple of the Eye via minigame teleport or Amulet of the Eye", ItemID.AMULET_OF_THE_EYE_26990);
		eyeTeleport.addAlternates(ItemID.AMULET_OF_THE_EYE, ItemID.AMULET_OF_THE_EYE_26992, ItemID.AMULET_OF_THE_EYE_26994);

		/* Quest Items */
		uncharedCells = new ItemRequirement("Uncharged cells", ItemID.UNCHARGED_CELL_28402);
		chargedCells = new ItemRequirement("Charged cells", ItemID.CHARGED_CELL);

		potionNote = new ItemRequirement("Potion note", ItemID.POTION_NOTE);
		strangePotion = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION_28383);
		freezes = new ItemRequirement("Freezing spells STRONGLY recommended + reasonable mage accuracy", -1);
		berry = new ItemRequirement("Argian berries", ItemID.ARGIAN_BERRIES);
		berry.setTooltip("You can get another from the south-west corner of The Stranglewood");
		herb = new ItemRequirement("Korbal herb", ItemID.KORBAL_HERB);
		herb.setTooltip("You can get another from the north-west corner of The Stranglewood");
		unfinishedSerum = new ItemRequirement("Unfinished serum", ItemID.UNFINISHED_SERUM);
		serumWithHerb = new ItemRequirement("Unfinished serum (herb added)", ItemID.UNFINISHED_SERUM_28387);
		stranglerSerum = new ItemRequirement("Strangler serum", ItemID.STRANGLER_SERUM);
		templeKey = new ItemRequirement("Temple key", ItemID.TEMPLE_KEY_28389);
		vardorvisMedallion = new ItemRequirement("Vardorvis' medallion", ItemID.VARDORVIS_MEDALLION);
		vardorvisMedallion.setTooltip("You can get another from Kasonde's hideout");

		oldTablet = new ItemRequirement("Old tablet", ItemID.OLD_TABLET);
		slimyKey = new ItemRequirement("Slimy key", ItemID.SLIMY_KEY);
		gunpowder = new ItemRequirement("Gunpowder", ItemID.GUNPOWDER_28442);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);

		airNerve = new ItemRequirement("Air nerve", ItemID.AIR_NERVE);
		fireNerve = new ItemRequirement("Fire nerve", ItemID.FIRE_NERVE);
		waterNerve = new ItemRequirement("Water nerve", ItemID.WATER_NERVE);
		earthNerve = new ItemRequirement("Earth nerve", ItemID.EARTH_NERVE);
		dustNerve = new ItemRequirement("Dust nerve", ItemID.DUST_NERVE);
		lavaNerve = new ItemRequirement("Lava nerve", ItemID.LAVA_NERVE);
		smokeNerve = new ItemRequirement("Smoke nerve", ItemID.SMOKE_NERVE);
		steamNerve = new ItemRequirement("Steam nerve", ItemID.STEAM_NERVE);

		dampTablet = new ItemRequirement("Damp tablet", ItemID.DAMP_TABLET);

		dampTablet2 = new ItemRequirement("Damp tablet", ItemID.DAMP_TABLET_28440);

		illuminatingLure = new ItemRequirement("Illuminating lure", ItemID.ILLUMINATING_LURE);
		crimsonFibre = new ItemRequirement("Crimson fibre", ItemID.CRIMSON_FIBRE);
		radiantFibre = new ItemRequirement("Radiant fibre", ItemID.RADIANT_FIBRE);

		perseriyasMedallion = new ItemRequirement("Perseriya's medallion", ItemID.PERSERIYAS_MEDALLION);
		perseriyasMedallion.setTooltip("You can get another from the debris at the ship in the Leviathan area");
	}

	public void loadZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		digsiteHole = new Zone(new WorldPoint(3400, 9800, 0), new WorldPoint(3419, 9824, 0));
		golemRoom = new Zone(new WorldPoint(2767, 6425, 0), new WorldPoint(2807, 6459, 0));
		stranglewood = new Zone(new WorldPoint(1087, 3264, 0), new WorldPoint(1261, 3458, 0));
		towerDefenseRoom = new Zone(new WorldPoint(1160, 9740, 0), new WorldPoint(1210, 9780, 0));
		stranglewoodPyramidRoom = new Zone(new WorldPoint(1177, 9810, 0), new WorldPoint(1190, 9846, 0));
		vardorvisArea = new Zone(new WorldPoint(1119, 3405, 0), new WorldPoint(1140, 3430, 0));

		/* Perseriya */
		wizardBasement = new Zone(new WorldPoint(3094, 9553, 0), new WorldPoint(3125, 9582, 0));
		templeOfTheEye = new Zone(new WorldPoint(2370, 5627, 0), new WorldPoint(2425, 5682, 0));
		templeOfTheEye2 = new Zone(new WorldPoint(2433, 5698, 0), new WorldPoint(3648, 9523, 0));
		demonArea = new Zone(new WorldPoint(2006, 6424, 0), new WorldPoint(2030, 6442, 0));
		tentArea = new Zone(new WorldPoint(2031, 6424, 0), new WorldPoint(2065, 6448, 0));

		abyssRoom1 = new Zone(new WorldPoint(2175, 6400, 0), new WorldPoint(2244, 6467, 0));
		abyssRoom2 = new Zone(new WorldPoint(1725, 6400, 0), new WorldPoint(1794, 6467, 0));
		abyssRoom3 = new Zone(new WorldPoint(1856, 6400, 0), new WorldPoint(1926, 6467, 0));

		path1 = new Zone(new WorldPoint(2194, 6443, 0), new WorldPoint(2196, 6453, 0));
		path1P2 = new Zone(new WorldPoint(2197, 6452, 0), new WorldPoint(2197, 6453, 0));
		path2 = new Zone(new WorldPoint(2197, 6443, 0), new WorldPoint(2206, 6445, 0));
		path3 = new Zone(new WorldPoint(2201, 6437, 0), new WorldPoint(2203, 6442, 0));
		path4 = new Zone(new WorldPoint(2193, 6437, 0), new WorldPoint(2200, 6439, 0));
		path5 = new Zone(new WorldPoint(2195, 6434, 0), new WorldPoint(2205, 6437, 0));
		path6 = new Zone(new WorldPoint(2206, 6433, 0), new WorldPoint(2210, 6438, 0));

		nearCatalystRoom1 = new Zone(new WorldPoint(2177, 6401, 0), new WorldPoint(2237, 6431, 0));
		nearCatalystRoom2 = new Zone(new WorldPoint(2177, 6431, 0), new WorldPoint(2192, 6463, 0));

		catalystRoom = new Zone(new WorldPoint(2192, 6405, 0), new WorldPoint(2207, 6420, 0));

		nearGrowth1 = new Zone(new WorldPoint(2225, 6417, 0), new WorldPoint(2237, 6439, 0));
		nearGrowth2 = new Zone(new WorldPoint(2231, 6438, 0), new WorldPoint(2238, 6450, 0));

		growthRoom = new Zone(new WorldPoint(2218, 6422, 0), new WorldPoint(2233, 6437, 0));
		boatRoom1 = new Zone(new WorldPoint(2220, 6402, 0), new WorldPoint(2238, 6415, 0));
		axonRoom1 = new Zone(new WorldPoint(1734, 6409, 0), new WorldPoint(1752, 6429, 0));
		axonRoom2 = new Zone(new WorldPoint(1731, 6420, 0), new WorldPoint(1735, 6429, 0));
		axonRoom3 = new Zone(new WorldPoint(1752, 6420, 0), new WorldPoint(1755, 6421, 0));
		northAbyssRoom2P1 = new Zone(new WorldPoint(1772, 6415, 0), new WorldPoint(1792, 6463, 0));
		northAbyssRoom2P2 = new Zone(new WorldPoint(1769, 6430, 0), new WorldPoint(1771, 6463, 0));
		northAbyssRoom2P3 = new Zone(new WorldPoint(1732, 6444, 0), new WorldPoint(1768, 6463, 0));
		northAbyssRoom2P4 = new Zone(new WorldPoint(1735, 6439, 0), new WorldPoint(1755, 6444, 0));
		northAbyssRoom2P5 = new Zone(new WorldPoint(1729, 6448, 0), new WorldPoint(1740, 6463, 0));
		nerveRoom1 = new Zone(new WorldPoint(1772, 6419, 0), new WorldPoint(1781, 6442,0));
		nerveRoom2 = new Zone(new WorldPoint(1782, 6420, 0), new WorldPoint(1790, 6442,0));
		nerveRoom3 = new Zone(new WorldPoint(1770, 6424, 0), new WorldPoint(1772, 6442,0));
		nervePassage = new Zone(new WorldPoint(1775, 6415, 0), new WorldPoint(1790, 6421, 0));
		summoningRoom1 = new Zone(new WorldPoint(1735, 6439, 0), new WorldPoint(1753, 6453, 0));
		summoningRoom2 = new Zone(new WorldPoint(1739, 6453, 0), new WorldPoint(1749, 6456, 0));
		summoningRoom3 = new Zone(new WorldPoint(1753, 6445, 0), new WorldPoint(1755, 6450, 0));
		boatRoom2 = new Zone(new WorldPoint(1773, 6448, 0), new WorldPoint(1790, 6461, 0));
		memoryPuzzle = new Zone(new WorldPoint(1899, 6429, 0), new WorldPoint(1916, 6446, 0));
		leechRoom = new Zone(new WorldPoint(1862, 6422, 0), new WorldPoint(1876, 6440, 0));
		treeRoom = new Zone(new WorldPoint(1888, 6402, 0), new WorldPoint(1906, 6411, 0));
		swRoom3P1 = new Zone(new WorldPoint(1859, 6401, 0), new WorldPoint(1877, 6442, 0));
		swRoom3P2 = new Zone(new WorldPoint(1878, 6401, 0), new WorldPoint(1882, 6420, 0));
		swRoom3P3 = new Zone(new WorldPoint(1883, 6401, 0), new WorldPoint(1887, 6416, 0));
		swRoom3P4 = new Zone(new WorldPoint(1858, 6442, 0), new WorldPoint(1868, 6451, 0));
		swRoom3P5 = new Zone(new WorldPoint(1868, 6442, 0), new WorldPoint(1872, 6446, 0));
		boatRoom3 = new Zone(new WorldPoint(1901, 6444, 0), new WorldPoint(1917, 6461, 0));

		leviathanArea = new Zone(new WorldPoint(2060, 6356, 0), new WorldPoint(2094, 6387, 0));
		neLeviathanArea = new Zone(new WorldPoint(2092, 6374, 0), new WorldPoint(2110, 6388, 0));
	}

	public void setupConditions()
	{
		inVault = new ZoneRequirement(vault);
		inDigsiteHole = new ZoneRequirement(digsiteHole);
		inGolemRoom = new ZoneRequirement(golemRoom);

		inspectedPlaque = new VarbitRequirement(15105, 1);
		inspectedStatueNE = new VarbitRequirement(15106, 1);
		inspectedStatueSE = new VarbitRequirement(15107, 1);
		inspectedStatueSW = new VarbitRequirement(15108, 1);
		inspectedStatueNW = new VarbitRequirement(15109, 1);
		// 12139 0->1 (cutscene specific ID)
		// VarPlayer 3575 3840 -> 7936

		inspectedGolem = new VarbitRequirement(QuestVarbits.QUEST_DESERT_TREASURE_II.getId(), 30,
			Operation.GREATER_EQUAL);
		// TODO: FIX CHECK FOR INSPECTED ALTAR, THOUGHT IT WAS 15111 BUT IT WASN'T
		inspectedAltar = new VarbitRequirement(15109, 1);

		// CAST BLOOD BARRAGE
		// 15116 0->4
		// 15119 0->1
		smokeBeenCast = new VarbitRequirement(15117, 1);
		shadowBeenCast = new VarbitRequirement(15118, 1);
		bloodBeenCast = new VarbitRequirement(15119, 1);
		iceBeenCast = new VarbitRequirement(15120, 1);

		castAllSpells = new VarbitRequirement(15116, 15);

		inPuzzle = new WidgetTextRequirement(838, 10, "One cell per row!");

		searchedVardorvis = new VarbitRequirement(15111, 1, Operation.GREATER_EQUAL);
		searchedPerseriya = new VarbitRequirement(15112, 1, Operation.GREATER_EQUAL);
		searchedSucellus = new VarbitRequirement(15113, 1, Operation.GREATER_EQUAL);
		searchedWhisperer = new VarbitRequirement(15114, 1, Operation.GREATER_EQUAL);

		askedAboutVardorvis = new VarbitRequirement(15111, 2, Operation.GREATER_EQUAL);
		askedAboutPerseriya = new VarbitRequirement(15112, 2, Operation.GREATER_EQUAL);
		askedAboutSucellus = new VarbitRequirement(15113, 2, Operation.GREATER_EQUAL);
		askedAboutWhisperer = new VarbitRequirement(15114, 2, Operation.GREATER_EQUAL);

		// After frostenhorn:
		// 40->42

		// 15125 0->2
		// 15126 0->2
		// 15127 0->2
		// 15128 0->2

		// 15130 0->1

		/* Vardorvis */
		talkedToElissa = new VarbitRequirement(15125, 4, Operation.GREATER_EQUAL);
		talkedToBarus = new VarbitRequirement(15125, 6, Operation.GREATER_EQUAL);
		haveReadPotionNote = new VarbitRequirement(15125, 8, Operation.GREATER_EQUAL);
		haveDrunkPotion = new VarbitRequirement(15125, 10, Operation.GREATER_EQUAL);
		inStrangewood = new ZoneRequirement(stranglewood);
//		seenStranglewoodCutscene = new VarbitRequirement(15125, 14, Operation.GREATER_EQUAL);
		finishedStranglewoodCutscene = new VarbitRequirement(15125, 16, Operation.GREATER_EQUAL);
		// Entered Stranglewood
		// 15125 10->12
		// 14862 42->44
		// 15160 0->3, happens whenever you enter the Stranglewood
		// 15099 = how infected you are

		// Cutscene upon entering area
		// 15160 3->2
		// 12139 0->1

		// After Cutscene, 15099 44->39
		// 12427 0->1
		// 12428 0->2
		talkedToKasonde = new VarbitRequirement(15125, 18, Operation.GREATER_EQUAL);

		// 15100, 0->400 (is a timer for tower defense)
		// 15125 18->20, gone into Entry
		// 15101 0->85 (Kasonde's health)

		// A, SW CHest
		// VarClientInt 1121 0->5
		// VarClientInt 1122 0->5
		// VarClientInt 1123 0->5
		inTowerDefenseRoom = new ZoneRequirement(towerDefenseRoom);

		defendedKasonde = new VarbitRequirement(15125, 22, Operation.GREATER_EQUAL);
		toldAboutHerbAndBerry = new VarbitRequirement(15125, 24, Operation.GREATER_EQUAL);
		// 15136 0->2 taken herb
		// 15125 24->26, herb taken
		herbTaken = new VarbitRequirement(15136, 2);
		// 15125 26->28, picked berry
		// 15137, 0->1 berry taken
		berryTaken = new VarbitRequirement(15137, 1);

		// 15125 28->30->32 when entering pyramid
		inStranglewoodPyramidRoom = new ZoneRequirement(stranglewoodPyramidRoom);
		defendedKasondeWithHerb = new VarbitRequirement(15125, 34, Operation.GREATER_EQUAL);
		receivedSerum = new VarbitRequirement(15125, 36, Operation.GREATER_EQUAL);
		addedHerb = serumWithHerb.alsoCheckBank(questBank);
		addedBerry = stranglerSerum.alsoCheckBank(questBank);
		drankPotion = new VarbitRequirement(15125, 38, Operation.GREATER_EQUAL);
		inAnyStranglewood = new Conditions(LogicType.OR, inStranglewoodPyramidRoom, inStrangewood);

		// Vardorvis arena entered
		// 15125 38->40
		// 14862 44->46

		defeatedVardorvis = new VarbitRequirement(15125, 42, Operation.GREATER_EQUAL);
		inVardorvisArea = new ZoneRequirement(vardorvisArea);
		templeKeyNearby = new ItemOnTileRequirement(templeKey);
		givenKasondeKey = new VarbitRequirement(15125, 46, Operation.GREATER_EQUAL);
		kasondeAggressive = new VarbitRequirement(15125, 48, Operation.GREATER_EQUAL);
		defeatedKasonde = new VarbitRequirement(15125, 50, Operation.GREATER_EQUAL);
		kasondeRevealedMedallion = new VarbitRequirement(15125, 52, Operation.GREATER_EQUAL);
		gotVardorvisMedallion = new VarbitRequirement(15125, 54, Operation.GREATER_EQUAL);

		// Probably 15132 0->1 for placed medallion works too
		finishedVardorvis = new VarbitRequirement(15125, 56, Operation.GREATER_EQUAL);
		// 15122 0->1, talked to Asgarnia with medallion first time?

		/* Perseriya */
		inWizardBasement = new ZoneRequirement(wizardBasement);
		inTempleOfTheEye = new ZoneRequirement(templeOfTheEye, templeOfTheEye2);
		inDemonArea = new ZoneRequirement(demonArea);
		inTentArea = new ZoneRequirement(tentArea);
		inAbyssRoom1 = new ZoneRequirement(abyssRoom1);
		inAbyssRoom2 = new ZoneRequirement(abyssRoom2);
		inAbyssRoom3 = new ZoneRequirement(abyssRoom3);
		// 15128 2->4, talked to guardian

		// Entered rift, 15128 4->6, 14862 54->56
		// 13092 0->100
		// 13095 0->100
		// 5934 0->1->2->3->4???

		final int PERSERIYA_VARBIT = 15128;

		defeatedDemons = new VarbitRequirement(PERSERIYA_VARBIT, 8, Operation.GREATER_EQUAL);

		attemptedToBoardBoat = new VarbitRequirement(PERSERIYA_VARBIT, 10, Operation.GREATER_EQUAL);
		// 12139 1->0 after boat attempt
		talkedToPersten = new VarbitRequirement(PERSERIYA_VARBIT, 14, Operation.GREATER_EQUAL);

		// In room 1
		// 15128 14->16
		// 15193 0->1
		// 15192 0->2
		// 15194 0->1

		onPath1 = new ZoneRequirement(path1, path1P2);
		onPath2 = new ZoneRequirement(path2);
		onPath3 = new ZoneRequirement(path3);
		onPath4 = new ZoneRequirement(path4);
		onPath5 = new ZoneRequirement(path5);
		onPath6 = new ZoneRequirement(path6);

		destroyedTether = new VarbitRequirement(15258, 1);

		isNearCatalystRoom = new ZoneRequirement(nearCatalystRoom1, nearCatalystRoom2);
		inCatalystRoom = new ZoneRequirement(catalystRoom);

		completedCatalystRoom = new VarbitRequirement(15259, 1);
		isNearGrowthRoom = new ZoneRequirement(nearGrowth1, nearGrowth2);
		// On login to main area, 12164 0->1
		// 13989 0->1


		// In growth room:
		// 15203 0->2
		// 15204 0->1
		// 15205 0->3
		// 15206 0->6
		// 15207 0->4
		// 15208 0->5

		// 15210 0->1 repaired SW corner
		growthRepairedNE = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2232, 6435, 0));
		growthRepairedNW = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2219, 6435, 0));
		growthRepairedSE = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2232, 6435, 0));
		growthRepairedSW = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2231, 6423, 0));

		// All repaired:
		// 15197 0->1
		// 15198 0->1
		// 15199 0->1
		// 200
		// 201
		// 202
		inGrowthRoom = new ZoneRequirement(growthRoom);

		repairedGrowths = new VarbitRequirement(15210, 4);
		solvedGrowthRoom = new VarbitRequirement(15260, 1);

		// Entered boat room, 15261 0->1
		inBoatRoom1 = new ZoneRequirement(boatRoom1);
		// TODO: Verify if order is random for this stuff, and thus variable needs to consider some shift based on area?
		haveReadOldTablet = new VarbitRequirement(PERSERIYA_VARBIT, 18, Operation.GREATER_EQUAL);

		// 18->20, burned ship
		// TODO: Verify if this is a required progression
		// 15128 20->22, talked to Persten

		// Attempted to enter room 2, 22->24
		completedRoom1 = new VarbitRequirement(PERSERIYA_VARBIT, 20, Operation.GREATER_EQUAL);
		talkedToPerstenAfterRoom1 = new VarbitRequirement(PERSERIYA_VARBIT, 22, Operation.GREATER_EQUAL);

		// Room 2
		inAxonRoom = new ZoneRequirement(axonRoom1, axonRoom2, axonRoom3);

		// Counts how many done
		// Each axon done, 15220 += 1
		cosmicAxonPresent = new NpcRequirement(NpcID.ABYSSAL_AXON, "Abyssal Axon (Cosmic)");
		waterAxonPresent = new NpcRequirement(NpcID.ABYSSAL_AXON, "Abyssal Axon (Water)");
		fireAxonPresent = new NpcRequirement(NpcID.ABYSSAL_AXON, "Abyssal Axon (Fire)");
		natureAxonPresent = new NpcRequirement(NpcID.ABYSSAL_AXON, "Abyssal Axon (Nature)");
		completedAxonRoom = new VarbitRequirement(15258, 1);

		nothingInHands = and(new NoItemRequirement("Weapon", ItemSlots.WEAPON),
			new NoItemRequirement("Shield", ItemSlots.SHIELD));
		((Conditions) nothingInHands).setText("Nothing equipped in your hands");

		inNorthOfAbyssRoom2 = new ZoneRequirement(northAbyssRoom2P1, northAbyssRoom2P2, northAbyssRoom2P3, northAbyssRoom2P4, northAbyssRoom2P5);
		inNerveRoom = new ZoneRequirement(nerveRoom1, nerveRoom2, nerveRoom3);
		inSummoningRoom = new ZoneRequirement(summoningRoom1, summoningRoom2, summoningRoom3);
		inBoatRoom2 = new ZoneRequirement(boatRoom2);

		lavaNerveBroken = new ObjectCondition(ObjectID.LAVA_NERVE_ENDING_BROKEN, new WorldPoint(1779, 6435, 0));
		smokeNerveBroken = new ObjectCondition(ObjectID.SMOKE_NERVE_ENDING_BROKEN, new WorldPoint(1778, 6431, 0));
		dustNerveBroken = new ObjectCondition(ObjectID.DUST_NERVE_ENDING_BROKEN, new WorldPoint(1784, 6433, 0));
		steamNerveBroken = new ObjectCondition(ObjectID.STEAM_NERVE_ENDING_BROKEN, new WorldPoint(1783, 6430, 0));

		completedNerveRoom = new VarbitRequirement(15259, 1);
		inNervePassage = new ZoneRequirement(nervePassage);
		impsNearby = new NpcRequirement("Scarred imp", NpcID.SCARRED_IMP);
		completedSummoningRoom = new VarbitRequirement(15260, 1);
		// Entered boat room, 15261 0->1. Seems to indicate 'teleport to boat room if they leave'
		haveReadDampTablet = new VarbitRequirement(PERSERIYA_VARBIT, 26, Operation.GREATER_EQUAL);

		// 15128 26->28 burnt second boat
		// 15260 1->0
		// 15259 1->0
		// 15258 0->1
		// 15261 0->1

		// PERSTEN 2 = 28
		completedRoom2 = new VarbitRequirement(PERSERIYA_VARBIT, 28, Operation.GREATER_EQUAL);
		talkedToPerstenAfterRoom2 = new VarbitRequirement(PERSERIYA_VARBIT, 30, Operation.GREATER_EQUAL);

		// ENTER ROOM 3
		// 15212 0->1
		// 15214 0->2
		// 15215 0->3
		//

		// Second time:
		// 15212 1->3
		// 15213 0->1
		// 15214 0->0
		// 15215 3->2

		// 0 = SW
		// 1 = NW
		// 2 = NE
		// 3 = SE

		// Room 3
		inMemoryPuzzle = new ZoneRequirement(memoryPuzzle);
		inTreeRoom = new ZoneRequirement(treeRoom);
		inLeechRoom = new ZoneRequirement(leechRoom);
		inBoatRoom3 = new ZoneRequirement(boatRoom3);

		solvedMemoryRoom = new VarbitRequirement(15258, 1);
		solvedTreeRoom = new VarbitRequirement(15260, 1);
		solvedLeechRoom = new VarbitRequirement(15259, 1);
		protectFromMagic = new PrayerRequirement("Protect from Magic", Prayer.PROTECT_FROM_MAGIC);
		inSwRoom3 = new ZoneRequirement(swRoom3P1, swRoom3P2, swRoom3P3, swRoom3P4, swRoom3P5);

		repairedGrowthRoom3 = new VarbitRequirement(15210, 4);
		repairedCrimsonVeins = new VarbitRequirement(15219, 3);

		haveReadDampTablet2 = new VarbitRequirement(PERSERIYA_VARBIT, 34, Operation.GREATER_EQUAL);

		completedRoom3 = new VarbitRequirement(PERSERIYA_VARBIT, 36, Operation.GREATER_EQUAL);

		readyToFightLeviathan = new VarbitRequirement(PERSERIYA_VARBIT, 38, Operation.GREATER_EQUAL);
		inLeviathanArea = new ZoneRequirement(leviathanArea);

		// Killed leviathan
		// 3968 0->1
		// 15128 40->42
		// 14862 58->60
		// 1683 12215->-1
		// 12401 1->0 (healthbar?)

		defeatedLeviathan = new VarbitRequirement(PERSERIYA_VARBIT, 42, Operation.GREATER_EQUAL);
		inNELeviathanArea = new ZoneRequirement(neLeviathanArea);
		perstenAtShip = new VarbitRequirement(PERSERIYA_VARBIT, 44, Operation.GREATER_EQUAL);
		perstenLeft = new VarbitRequirement(PERSERIYA_VARBIT, 46, Operation.GREATER_EQUAL);

		// Searched debris
		// 15128 46->48
		// 14862 60->62

		// Afterwards
		// 15161 0->1->2->3...
		// 0->1 happens 110 ticks after first obtaining medallion
		// Up once every 110 ticks (66 seconds?)
		// Obtain medallion again, resets
		// Seemed to cap at 10

		foundPerseriyasMedallion = new VarbitRequirement(PERSERIYA_VARBIT, 48, Operation.GREATER_EQUAL);

		// Entered the Eye area:
		// 3575 36347648 -> 36413184
		// 14862 62->64
		// 15161 10->0
		// 14192 0->7

		// Enter Desert through Shanty Pass: 13137 0->50
		// With clothes on, 50->160
		// No top, 140
		// Updates upon damage taken

		// On login, 12164 0->1
		// 933 0->1
		// 13989 0->1

		// Spawned at 3326, 2896, 0:
		// 14192 0->7
		// Teled out of dessert, 13140 17->0?

		// At house:...
		//

		// Outside vault
		// 14192 0->7
		// Only got invaded once the counter hit 10

		// Placed perseriya's medallion
		// 15128 48->50
		// 14862 64->66
		// 15133 0->4

		finishedPerseriya = new VarbitRequirement(PERSERIYA_VARBIT, 50, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		attemptToEnterVaultDoor = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north-east of Nardah.", ancientMagicksActive);
		attemptToEnterVaultDoor.addTeleport(nardahTeleport);
		attemptToEnterVaultDoor.addDialogStep("Yes.");

		attemptToEnterVaultDoor2 = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north-east of Nardah.");
		attemptToEnterVaultDoor.addSubSteps(attemptToEnterVaultDoor2);

		talkToAsgarnia = new NpcStep(this, NpcID.ASGARNIA_SMITH_12291,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		inspectPlaque = new ObjectStep(this, ObjectID.PLAQUE_48798,
			new WorldPoint(3944, 9631, 1), "Inspect the plaque.");

		inspectStatueNE = new ObjectStep(this, NullObjectID.NULL_49499, new WorldPoint(3942, 9636, 1),
			"Inspect the north-east statue.");
		inspectStatueNW = new ObjectStep(this, NullObjectID.NULL_49501, new WorldPoint(3932, 9636, 1),
			"Inspect the north-west statue.");
		inspectStatueSW = new ObjectStep(this, NullObjectID.NULL_49503, new WorldPoint(3932, 9626, 1),
			"Inspect the south-west statue.");
		inspectStatueSE = new ObjectStep(this, NullObjectID.NULL_49505, new WorldPoint(3942, 9626, 1),
			"Inspect the south-east statue.");

		talkToAsgarniaAgain = new NpcStep(this, NpcID.ASGARNIA_SMITH_12291,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		talkToBalando = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3359, 3334, 0),
			"Talk to the Terry Balando in the Exam Centre found south-east of Varrock, " +
				"directly south of the Digsite.");
		talkToBalando.addTeleport(senntistenTeleport);

		operateWinch = new ObjectStep(this, ObjectID.WINCH_48918, new WorldPoint(3384, 3418, 0),
			"Enter the winch on the east side of the Digsite.", combatGear);
		talkToBanikan = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(3409, 9815, 0),
			"Talk to Dr Banikan.");

		getPickaxe = new ObjectStep(this, ObjectID.CRATE_48923, new WorldPoint(3414, 9819, 0),
			"Get a pickaxe from the crate in the north-east of the cavern.");

		mineRocks = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Mine the rocks in the south-east of the cavern.", pickaxe);

		enterDigsiteHole = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south-east of the cavern, ready for a fight.", combatGear);

		enterDigsiteHoleAgain = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south-east of the cavern.");

		killAncientGuardian = new NpcStep(this, NpcID.ANCIENT_GUARDIAN_12337, new WorldPoint(2783, 6431, 0)
		, "Kill the Ancient Guardian. The shield needs to be broken to hurt it, " +
			"and it will regenerate the shield unless you are using melee attacks with a pickaxe in the inventory.", combatGear);
		((NpcStep) killAncientGuardian).addAlternateNpcs(NpcID.ANCIENT_GUARDIAN_12336);

		talkToBanikanInGolemRoom = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6431, 0),
			"Talk to Dr Banikan in the room you defeated the Ancient Guardian.");
		talkToBanikanInGolemRoom.addSubSteps(enterDigsiteHoleAgain);

		inspectGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Inspect the golem to the north.");
		inspectAltar = new ObjectStep(this, NullObjectID.NULL_49512, new WorldPoint(2773, 6442, 0),
			"Inspect the altar to the north-west.");

		castOnBloodStatue = new NpcStep(this, NpcID.BLOOD_TOTEM, new WorldPoint(2775, 6445, 0),
			"Cast blood burst or higher on the blood totem.", bloodBurstRunes);
		castOnBloodStatue.addIcon(ItemID.BLOOD_DIAMOND);
		castOnBloodStatue.addWidgetHighlight(218, 82);

		castOnShadowStatue = new NpcStep(this, NpcID.SHADOW_TOTEM, new WorldPoint(2771, 6445, 0),
			"Cast shadow burst or higher on the shadow totem.", shadowBurstRunes);
		castOnShadowStatue.addIcon(ItemID.SHADOW_DIAMOND);
		castOnShadowStatue.addWidgetHighlight(218, 90);

		castOnSmokeStatue = new NpcStep(this, NpcID.SMOKE_TOTEM, new WorldPoint(2771, 6439, 0),
			"Cast smoke burst or higher on the smoke totem.", smokeBurstRunes);
		castOnSmokeStatue.addIcon(ItemID.SMOKE_DIAMOND);
		castOnSmokeStatue.addWidgetHighlight(218, 86);

		castOnIceStatue = new NpcStep(this, NpcID.ICE_TOTEM, new WorldPoint(2775, 6439, 0),
			"Cast ice burst or higher on the smoke totem.", iceBurstRunes);
		castOnIceStatue.addIcon(ItemID.ICE_DIAMOND);
		castOnIceStatue.addWidgetHighlight(218, 78);

		searchCrateForCharges = new ObjectStep(this, ObjectID.CRATE_48931,
			new WorldPoint(2780, 6440, 0), "Search the crate in the room with the golem.");
		imbueAtAltar = new ObjectStep(this, NullObjectID.NULL_49512, new WorldPoint(2773, 6442, 0),
			"Imbue the cells at the altar.", uncharedCells);

		chargeGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
		"Inspect the golem again.");

		solveGolemPuzzle = new GolemPuzzleStep(this);

		operateGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Vardorvis\", \"Perseriya\", \"Sucellus\", and \"Whisperer\". " +
				"Make sure to talk to Banikan about each one!");
		operateGolem.addDialogStep("Yes.");

		searchVardorvis = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the term : \"Vardorvis\".");
		searchVardorvis.addDialogStep("Yes.");
		searchPerseriya = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Perseriya\".");
		searchPerseriya.addDialogStep("Yes.");
		searchSucellus = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Sucellus\".");
		searchSucellus.addDialogStep("Yes.");
		searchWhisperer = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Whisperer\".");
		searchWhisperer.addDialogStep("Yes.");

		askAboutVardorvis = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Vardorvis.");
		askAboutVardorvis.addDialogStep("Any thoughts on Vardorvis?");
		askAboutPerseriya = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Perseriya.");
		askAboutPerseriya.addDialogStep("Any thoughts on Perseriya?");
		askAboutSucellus = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Sucellus.");
		askAboutSucellus.addDialogStep("Any thoughts on Vardorvis?");
		askAboutWhisperer = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Whisperer.");
		askAboutWhisperer.addDialogStep("Any thoughts on Whisperer?");

		operateGolem.addSubSteps(searchVardorvis, searchPerseriya, searchSucellus, searchWhisperer, askAboutVardorvis,
			askAboutPerseriya, askAboutSucellus, askAboutWhisperer);

		talkToBanikanAfterGolem = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Talk to Banikan about what to do next.");

		operateGolemFrostenhorn = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Try operating the golem again until Banikan leaves, and the golem lets you know the last search term.");


		/* Vardorvis */
		talkToElissa = new NpcStep(this, NpcID.ELISSA, new WorldPoint(3378, 3428, 0), "Talk to Elissa in the north-east of the Digsite on the surface.");
		talkToElissa.addDialogStep("I hear you visited Lovakengj recently.");
		talkToElissa.addTeleport(senntistenTeleport);
		talkToBarus = new NpcStep(this, NpcID.BARUS, new WorldPoint(1459, 3782, 0), "Talk to Barus near the burning man in south-west Lovakengj.");
		// TODO: Highlight the widget
		talkToBarus.addTeleport(xericTalisman.named("Xeric's talisman ([3] Xeric's Inferno)"));

		searchDesk = new ObjectStep(this, NullObjectID.NULL_49490, new WorldPoint(1781, 3619, 0),
			"Search the desk in the house south of the Hosidius Estate Agent.");
		searchDesk.addTeleport(xericTalisman.named("Xeric's talisman ([2] Xeric's Glade)"));

		readPotionNote = new ItemStep(this, "Read the potion note.", potionNote.highlighted());
		drinkPotion = new ItemStep(this, "Drink the strange potion.", strangePotion.highlighted());
		boardBoat = new ObjectStep(this, NullObjectID.NULL_49491, new WorldPoint(1227, 3470, 0),
			"Board the boat south of Quidamortem into The Stranglewood. You can use the Fairy Ring BLS to get nearby, or travel with the Mountain Guide.");
		runIntoStanglewood = new DetailedQuestStep(this, new WorldPoint(1194, 3394, 0), "Run deeper into Stranglewood. " +
			"Be careful of the Strangled, as they'll bind you and deal damage.");
		talkToKasonde = new NpcStep(this, NpcID.KASONDE, new WorldPoint(1191, 3404, 0), "Talk to Kasonde.");
		enterEntry = new ObjectStep(this, ObjectID.ENTRY_48722, new WorldPoint(1191, 3411, 0), "Hide away in the Entry in the north-west of the room.");
		defendKasondeSidebar = new NpcStep(this, NpcID.STRANGLED_12275, "Defend Kasonde! There are barricades in the stone chests you can set up to block routes. " +
			"There are also satchels you can place on the floor, and detonate using the Detonator. This will kill all of the Strangled in a 7x7 area, as well as damaging you or " +
			"Kasonde if either of you are in the blast radius.", true);
		defendKasondeSidebar.addText("Closed chests require you to guess the correct code to open, with correct numbers in the correct place being marked in green, " +
			"and correct numbers in the wrong places being marked with blue.");
		defendKasondeSidebar.addText("It's recommended to also use freezes if you have ancient magicks with you to keep them off of Kasonde. If you have freezes, you can largely ignore the barricading and explosives.");
		((NpcStep) defendKasondeSidebar).addAlternateNpcs(NpcID.STRANGLED_12276, NpcID.STRANGLED_12277, NpcID.STRANGLED_12278);

		defendKasonde = new DetailedQuestStep(this, "Defend Kasonde! Read the sidebar for more details.");
		defendKasonde.addRecommended(freezes);
		defendKasondeSidebar.addSubSteps(defendKasonde);

		// TODO: Get actual coordinate and ladder ID!
		leaveTowerDefenseRoom = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(1175, 9755, 0),
			"Leave the dungeon up the ladder.");
		talkToKasondeAfterTowerDefense = new NpcStep(this, NpcID.KASONDE, new WorldPoint(1191, 3404, 0),
			"Talk to Kasonde on the surface.");
		getHerb = new ObjectStep(this, ObjectID.KORBAL_HERBS, new WorldPoint(1155, 3447, 0),
			"Go get the herb from the north-west corner of the area. " +
				"The stangled will attack you, so bring food and freezes to trap them. More info in the sidebar.");
		getHerb.setLinePoints(Arrays.asList(
			new WorldPoint(1193, 3403, 0),
			new WorldPoint(1193, 3395, 0),
			new WorldPoint(1186, 3395, 0),
			new WorldPoint(1186, 3416, 0),
			new WorldPoint(1165, 3415, 0),
			new WorldPoint(1161, 3415, 0),
			new WorldPoint(1161, 3426, 0),
			new WorldPoint(1159, 3426, 0),
			new WorldPoint(1159, 3428, 0),
			new WorldPoint(1161, 3441, 0)
		));

		getHerbSidebar = new ObjectStep(this, ObjectID.KORBAL_HERBS, new WorldPoint(1111, 3434, 0),
			"Go get the herb. The Strangled will attack you, so have food. If your infected bar reaches full, " +
				"you'll be teleported to the starting room again. ");
		getHerbSidebar.addText("You can get some stink bombs from the chest in the south-east corner of Kasonde's room, " +
				"which when used attract the Strangled to the location. This can be useful for avoiding them.");
		getHerbSidebar.addText("Freezes and blood spells are useful for trapping them and healing up.");
		getHerbSidebar.addSubSteps(getHerb);

		getBerry = new ObjectStep(this, ObjectID.ARGIAN_BUSH, new WorldPoint(1126, 3323, 0),
			"Get the berry from the south-west part of the area. Beware the Strangled still.");
		getBerry.setLinePoints(Arrays.asList(
			new WorldPoint(1161, 3441, 0),
			new WorldPoint(1159, 3428, 0),
			new WorldPoint(1159, 3426, 0),
			new WorldPoint(1161, 3426, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1144, 3342, 0),
			new WorldPoint(1126, 3323, 0)
		));

		goDownToKasonde = new ObjectStep(this, ObjectID.ENTRY_48723, new WorldPoint(1174, 3428, 0),
			"Go to Kasonde, who is inside the main pyramid of the area to the north. Be ready to fight a few Strangled.", combatGear, berry, herb);
		goDownToKasonde.setLinePoints(Arrays.asList(
			new WorldPoint(1126, 3323, 0),
			new WorldPoint(1144, 3342, 0),
			new WorldPoint(1144, 3357, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1174, 3416, 0),
			new WorldPoint(1174, 3427, 0)
		));
		defendKasondeHerb = new NpcStep(this, NpcID.STRANGLED_12280, new WorldPoint(1183, 9824, 0),
			"Defeat the Strangled attacking Kasonde.", true);
		((NpcStep) defendKasondeHerb).addAlternateNpcs(NpcID.STRANGLED_12279);

		talkToKasondeWithHerbAndBerry = new NpcStep(this, NpcID.KASONDE_12258, new WorldPoint(1183, 9824, 0),
			"Talk to Kasonde.", berry, herb);
		addHerb = new DetailedQuestStep(this, "Add the herb to unfinished serum.", herb.highlighted(), unfinishedSerum.highlighted());
		addBerry = new DetailedQuestStep(this, "Add the berries to the serum.", serumWithHerb.highlighted(), berry.highlighted());
		drinkStranglewoodPotion = new DetailedQuestStep(this, "Drink the strangler serum.", stranglerSerum.highlighted());

		goToRitualSite = new ObjectStep(this, NullObjectID.NULL_49495, new WorldPoint(1118, 3428, 0), "Go to the ritual site to the west, ready to fight the boss of the area.", combatGear);
		goToRitualSite.setLinePoints(Arrays.asList(
			new WorldPoint(1174, 3427, 0),
			new WorldPoint(1174, 3416, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1144, 3357, 0),
			new WorldPoint(1144, 3342, 0),
			/* Bridge to west */
			new WorldPoint(1126, 3344, 0),
			new WorldPoint(1116, 3344, 0),
			new WorldPoint(1115, 3355, 0),
			new WorldPoint(1109, 3356, 0),
			/* Bridge to north */
			new WorldPoint(1109, 3383, 0),
			new WorldPoint(1111, 3400, 0),
			new WorldPoint(1106, 3410, 0),
			new WorldPoint(1106, 3434, 0),
			new WorldPoint(1112, 3435, 0),
			new WorldPoint(1124, 3423, 0),
			new WorldPoint(0, 0, 0),
			/* From boat */
			new WorldPoint(1196, 3450, 0),
			new WorldPoint(1197, 3428, 0),
			new WorldPoint(1216, 3412, 0),
			new WorldPoint(1215, 3395, 0),
			new WorldPoint(1188, 3395, 0),
			new WorldPoint(1163, 3395, 0)
		));

		fightVardorvis = new NpcStep(this, NpcID.VARDORVIS, new WorldPoint(1129, 3419, 0), "Defeat Vardorvis. Look at the sidebar for more details. Protect from Melee when he's not using a special attack.");
		((NpcStep) fightVardorvis).addAlternateNpcs(NpcID.VARDORVIS_12224, NpcID.VARDORVIS_12228, NpcID.VARDORVIS_12425, NpcID.VARDORVIS_12426);
		fightVardorvisSidebar = new DetailedQuestStep(this, "Defeat Vardorvis. It's recommended to watch a video to get an understanding of his abilities.");
		fightVardorvisSidebar.addText("Swinging axes: He will spawn axes around the arena, which will go to the opposite corner to which they appear. Avoid them.");
		fightVardorvisSidebar.addText("Homing spikes: Vardorvis hits the ground, causing a spike to appear under you. Move off the tile to avoid.");
		fightVardorvisSidebar.addText("Head projectile: A tentacle will appear with a head on it, firing a green projectile. Protect from Missiles should be flicked to.");
		fightVardorvisSidebar.addText("Virus cells: Red splotches appear on the screen. Click them all within the time limit to avoid damage.");
		fightVardorvisSidebar.addSubSteps(fightVardorvis);

		pickUpTempleKey = new ItemStep(this,  "Pick up the Temple Key in the Vardorvis arena.", templeKey);
		getTempleKeyFromRocks = new ObjectStep(this, NullObjectID.NULL_49495, new WorldPoint(1118, 3428, 0),
			"Go to the ritual site to the west, and search the rocks to get another Temple Key.");

		DetailedQuestStep enterKasondeWithKey = new ObjectStep(this, ObjectID.ENTRY_48723, new WorldPoint(1174, 3428, 0),
		"");
		DetailedQuestStep giveKasondeKey =  new NpcStep(this, NpcID.KASONDE_12258, new WorldPoint(1183, 9824, 0),
			"");
		returnToKasondeWithTempleKey = new ConditionalStep(this, boardBoat,
			"Return to Kasonde with the temple key, who is inside the main pyramid of the area to the north. " +
			"Be ready for another fight.", combatGear, templeKey);
		returnToKasondeWithTempleKey.addStep(new Conditions(inStranglewoodPyramidRoom, templeKey.alsoCheckBank(questBank)
			.hideConditioned(givenKasondeKey)), giveKasondeKey);
		returnToKasondeWithTempleKey.addStep(new Conditions(inAnyStranglewood, templeKey.alsoCheckBank(questBank)), enterKasondeWithKey);
		returnToKasondeWithTempleKey.addStep(templeKeyNearby, pickUpTempleKey);
		returnToKasondeWithTempleKey.addStep(inAnyStranglewood, getTempleKeyFromRocks);

		DetailedQuestStep kasondeFight = new NpcStep(this, NpcID.KASONDE_12263, new WorldPoint(1183, 9824, 0),
			"");
		defeatKasonde = new ConditionalStep(this, boardBoat,
			"Defeat Kasonde in the pyramid. Avoid the potions he throws. If he throws his hands up, hide behind a pillar.", combatGear);
		defeatKasonde.addStep(new Conditions(inStranglewoodPyramidRoom), kasondeFight);
		defeatKasonde.addStep(new Conditions(inAnyStranglewood), enterKasondeWithKey);

		/* Post-fight Kasonde */
		NpcStep talkToKasondeAfterFight = new NpcStep(this, NpcID.KASONDE_12264, new WorldPoint(1183, 9824, 0), "");

		goTalkToKasondeAfterFight = new ConditionalStep(this, boardBoat,
			"Talk to Kasonde again in the pyramid.");
		goTalkToKasondeAfterFight.addStep(new Conditions(inStranglewoodPyramidRoom), talkToKasondeAfterFight);
		goTalkToKasondeAfterFight.addStep(new Conditions(inAnyStranglewood), enterKasondeWithKey);

		ObjectStep searchChestForVardorvisMedallion = new ObjectStep(this, NullObjectID.NULL_49496, new WorldPoint(1196, 3411, 0), "");
		/* Getting the medallion */
		ObjectStep leavePyramid = new ObjectStep(this, ObjectID.STAIRS_48730, new WorldPoint(1183, 9809, 0), "");
		goGetVardorvisMedallion = new ConditionalStep(this, boardBoat,
			"Search the north-east chest in Kadsone's initial hideout for a medallion.");
		goGetVardorvisMedallion.addStep(inStranglewoodPyramidRoom, leavePyramid);
		goGetVardorvisMedallion.addStep(new Conditions(inAnyStranglewood), searchChestForVardorvisMedallion);

		returnToDesertWithVardorvisMedallion = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0), "Return to the Vault door north-east of Nardah.", vardorvisMedallion);
		returnToDesertWithVardorvisMedallion.addTeleport(nardahTeleport);

		useVardorvisMedallionOnStatue = new ObjectStep(this, NullObjectID.NULL_49499, new WorldPoint(3942, 9636, 1),
			"Use the medallion on the north-east statue.", vardorvisMedallion.highlighted());
		useVardorvisMedallionOnStatue.addIcon(ItemID.VARDORVIS_MEDALLION);


		/* Perseriya */
		enterWizardBasement = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Go to the Temple of the Eye.");
		enterWizardBasement.addTeleport(eyeTeleport);
		enterPortalToTempleOfTheEye = new ObjectStep(this, ObjectID.PORTAL_43765, new WorldPoint(3104, 9574, 0),
			"Enter the portal to the Temple of the Eye.");
		NpcStep talkToCatalyticGuardian = new NpcStep(this, NpcID.CATALYTIC_GUARDIAN, new WorldPoint(3611, 9473, 0),
			"Talk to the Catalytic Guardian to travel into the Rift.");
		talkToCatalyticGuardian.addAlternateNpcs(NpcID.CATALYTIC_GUARDIAN_12384);
		talkToCatalyticGuardian.addDialogStep("Yes.");

		killDemons = new NpcStep(this, NpcID.SCARRED_IMP, new WorldPoint(2021, 6433, 0),
			"", true);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.ABYSSAL_WALKER_12392, NpcID.ABYSSAL_WALKER_12394);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.ABYSSAL_LEECH_12391, NpcID.ABYSSAL_LEECH_12393);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.GREATER_DEMON_12387, NpcID.LESSER_DEMON_12389);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_BLACK_DEMON, NpcID.BLACK_DEMON_12385);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_GREATER_DEMON_12388, NpcID.SCARRED_LESSER_DEMON_12390);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_BLACK_DEMON, NpcID.SCARRED_LESSER_DEMON_12378);

		ConditionalStep goToAbyss = new ConditionalStep(this, enterWizardBasement);
		goToAbyss.addStep(inTempleOfTheEye, talkToCatalyticGuardian);
		goToAbyss.addStep(inWizardBasement, enterPortalToTempleOfTheEye);

		goTalkToCatalyticGuardian = new ConditionalStep(this, goToAbyss,
			"Talk to the Catalytic Guardian by the portal in the Temple of the Eye.", combatGear, facemask);

		goKillDemons = new ConditionalStep(this, goToAbyss, "Kill all the demons and abyssal entities in the rift.",
			combatGear, facemask);
		goKillDemons.addStep(inDemonArea, killDemons);

		hopOverSteppingStone = new ObjectStep(this, ObjectID.STEPPING_STONE_49209, new WorldPoint(2031, 6430, 0),
			"Hop across the stepping stone.");

		ObjectStep boardAbyssBoat = new ObjectStep(this, ObjectID.ROWBOAT_49212, new WorldPoint(2065, 6436, 0),
			"");

		goBoardBoat = new ConditionalStep(this, goToAbyss, "Attempt to board the boat to the east.", facemask);
		goBoardBoat.addStep(inTentArea, boardAbyssBoat);
		goBoardBoat.addStep(inDemonArea, hopOverSteppingStone);

		talkToPersten = new NpcStep(this, NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "");
		goTalkToPersten = new ConditionalStep(this, goToAbyss, "Talk to Wizard Persten in the Rift.", facemask);
		goTalkToPersten.addStep(inTentArea, talkToPersten);
		goTalkToPersten.addStep(inDemonArea, hopOverSteppingStone);

		enterPassage1 = new ObjectStep(this, NullObjectID.NULL_49526, new WorldPoint(2043, 6441, 0),
			"Enter the north-west passage.");

		goDoPassage1 = new ConditionalStep(this, goToAbyss, facemask);
		goDoPassage1.addStep(inTentArea, enterPassage1);
		goDoPassage1.addStep(inDemonArea, hopOverSteppingStone);

		enterPathfinderRoom = new ObjectStep(this, ObjectID.PASSAGE_49410, new WorldPoint(2196, 6454, 0),
			"Enter the pathfinder room to the east of the entrance.");
		enterPathfinderRoom.setLinePoints(Arrays.asList(
			new WorldPoint(2178, 6459, 0),
			new WorldPoint(2192, 6461, 0),
			new WorldPoint(2215, 6458, 0),
			new WorldPoint(2215, 6452, 0),
			new WorldPoint(2210, 6452, 0),
			new WorldPoint(2206, 6456, 0),
			new WorldPoint(2197, 6456, 0)
		));

		doPath1 = new DetailedQuestStep(this, new WorldPoint(2197, 6444, 0),
			"Move the nearest pathfinder from the north, and follow it within a 3x3 area until the next pathfinder.");
		doPath1.addTileMarker(new WorldPoint(2195, 6451, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath1.setLinePoints(Arrays.asList(
			new WorldPoint(2195, 6450, 0),
			new WorldPoint(2195, 6444, 0),
			new WorldPoint(2197, 6444, 0)
		));

		doPath2 = new DetailedQuestStep(this, new WorldPoint(2202, 6442, 0),
			"Move the next pathfinder from the west, and step off when safe to the south pathfinder.");
		doPath2.addTileMarker(new WorldPoint(2197, 6444, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath2.setLinePoints(Arrays.asList(
			new WorldPoint(2199, 6444, 0),
			new WorldPoint(2202, 6444, 0),
			new WorldPoint(2202, 6442, 0)
		));

		doPath3 = new DetailedQuestStep(this, new WorldPoint(2198, 6438, 0),
			"Move the next pathbreaker from the north, and step off when safe to the west pathbreaker.");
		doPath3.addTileMarker(new WorldPoint(2202, 6442, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath3.setLinePoints(Arrays.asList(
			new WorldPoint(2202, 6440, 0),
			new WorldPoint(2201, 6439, 0),
			new WorldPoint(2198, 6438, 0)
		));

		doPath4 = new DetailedQuestStep(this, new WorldPoint(2196, 6435, 0),
			"Move the next pathbreaker from the west, and step off when safe to the south-west pathbreaker.");
		doPath4.addTileMarker(new WorldPoint(2198, 6438, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath4.setLinePoints(Arrays.asList(
			new WorldPoint(2197, 6438, 0),
			new WorldPoint(2196, 6435, 0)
		));

		doPath5 = new DetailedQuestStep(this, new WorldPoint(2207, 6436, 0),
			"Move the next pathbreaker from the east, and step off when safe to the east pathbreaker.");
		doPath5.addTileMarker(new WorldPoint(2196, 6435, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath5.setLinePoints(Arrays.asList(
			new WorldPoint(2196, 6435, 0),
			new WorldPoint(2207, 6436, 0)
		));

		doPath6 = new ObjectStep(this, ObjectID.ABYSSAL_TETHER, new WorldPoint(2210, 6433, 0),
			"Move the next pathbreaker from the south, and step off to destroy the abyssal tether.");
		doPath6.addTileMarker(new WorldPoint(2207, 6436, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath6.setLinePoints(Arrays.asList(
			new WorldPoint(2207, 6436, 0),
			new WorldPoint(2207, 6433, 0),
			new WorldPoint(2209, 6433, 0)
		));

		enterGreenTeleporter1 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49415, new WorldPoint(2234, 6461, 0),
			"Enter the neural teleporter in the north-east corner.");

		enterCatalystRoom = new ObjectStep(this, ObjectID.PASSAGE_49411, new WorldPoint(2190, 6412, 0),
			"Enter the catalyst room in the south-west corner of the area.");

		solveCatalystRoom = new DetailedQuestStep(this, "You need to use the rune nerves on the catalyst which match the current symbol until the walkers die.");
		solveCatalystRoom.addText("You can make additional rune nerves by combining nerves:");
		solveCatalystRoom.addText("Mind + mind = soul.");
		solveCatalystRoom.addText("Water + earth = nature.");
		solveCatalystRoom.addText("Soul + nature = cosmic.");
		solveCatalystRoom.addText("Cosmic + earth = astral.");
		solveCatalystRoom.addText("Fire + air = smoke.");
		solveCatalystRoom.addText("Mind + water = blood.");
		solveCatalystRoom.addText("Smoke + blood = wrath.");

		enterBlueTeleporter1 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49414, new WorldPoint(2181, 6425, 0),
			"Enter the blue teleporter to the west of the area.");

		enterGrowthRoom = new ObjectStep(this, ObjectID.PASSAGE_49412, new WorldPoint(2225, 6421, 0),
			"Enter the damage growth room to the south.");

		repairGrowths = new ObjectStep(this, ObjectID.DAMAGED_GROWTH, "Take lures from the light leeches to repair the growths. Protect from Melee to not take damage from them.", true);
		growthPuzzle = new GrowthPuzzleStep(this);

		returnThroughBlueNeuralTeleporter = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER,
			new WorldPoint(2237, 6444, 0), "Head to the south-east room via the blue neural transmitter.");
		enterBoatRoom1 = new ObjectStep(this, ObjectID.PASSAGE_49418, new WorldPoint(2220, 6402, 0),
			"Enter the south-east room via the southern corridor.");

		getTinderbox = new ObjectStep(this, ObjectID.CRATE_49425, new WorldPoint(2236, 6402, 0), "Search the south-eastern crate for a tinderbox.");
		burnBoat1 = new ObjectStep(this, ObjectID.SHIPWRECK, new WorldPoint(2235, 6409, 0), "Burn the boat.");
		searchSkeletonForKey = new ObjectStep(this, ObjectID.SKELETON_49426, new WorldPoint(2221, 6414, 0),
			"Search the north-west skeleton for a key.");
		searchSkeletonForGunpowder = new ObjectStep(this, ObjectID.SKELETON_49427, new WorldPoint(2223, 6402, 0),
			"Search the south-west skeleton for gunpowder.");
		getOldTablet = new ObjectStep(this, ObjectID.CHEST_49420, new WorldPoint(2231, 6413, 0),
			"Open the chest in the north of the room for a tablet.", slimyKey);
		((ObjectStep) getOldTablet).addAlternateObjects(ObjectID.CHEST_49422);
		readOldTablet = new DetailedQuestStep(this, "Read the old tablet.", oldTablet.highlighted());

		enterSouthEastPassage = new ObjectStep(this, NullObjectID.NULL_49527, new WorldPoint(2047, 6427, 0),
			"Enter the passage to the south on the east wall.", facemask.equipped());

		enterAxonRoom = new ObjectStep(this, ObjectID.PASSAGE_49410, new WorldPoint(1756, 6420, 0), "Enter the Abyssal Axon room to the west.");
		// [9816, 9672, 9777]
		hitCosmicAxon = new NpcStep(this, NpcID.ABYSSAL_AXON, "Abyssal Axon (Cosmic)", new WorldPoint(1743, 6421, 0),
			"Hit the Cosmic Axon towards the Cosmic terminal. Avoid the lightning strikes.");
		hitCosmicAxon.addTileMarker(new WorldPoint(1746, 6414, 0), SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS);
		hitCosmicAxon.setLinePoints(Arrays.asList(
			// Cosmic Axon
			new WorldPoint(1749, 6419, 0),
			new WorldPoint(1747, 6419, 0),
			new WorldPoint(1747, 6414, 0)
		));

		// [553, 573, 542]
		hitFireAxon = new NpcStep(this, NpcID.ABYSSAL_AXON, "Abyssal Axon (Fire)", new WorldPoint(1743, 6421, 0),
		"Hit the Fire Axon towards the Fire terminal. Avoid the lightning strikes.");
		hitFireAxon.addTileMarker(new WorldPoint(1748, 6426, 0), SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS);
		hitFireAxon.setLinePoints(Arrays.asList(
			new WorldPoint(1744, 6413, 0),
			new WorldPoint(1744, 6421, 0),
			new WorldPoint(1742, 6421, 0),
			new WorldPoint(1742, 6424, 0),
			new WorldPoint(1740, 6424, 0),
			new WorldPoint(1740, 6427, 0),
			new WorldPoint(1747, 6427, 0)
		));
		// [20013, 19904, 20126]
		hitNatureAxon = new NpcStep(this, NpcID.ABYSSAL_AXON, "Abyssal Axon (Nature)", new WorldPoint(1743, 6421, 0),
			"Hit the Nature Axon towards the Nature terminal. Avoid the lightning strikes.");
		hitNatureAxon.addTileMarker(new WorldPoint(1733, 6426, 0), SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS);
		hitNatureAxon.setLinePoints(Arrays.asList(
			new WorldPoint(1743, 6424, 0),
			new WorldPoint(1740, 6424, 0),
			new WorldPoint(1740, 6422, 0),
			new WorldPoint(1734, 6422, 0),
			new WorldPoint(1734, 6425, 0)
		));

		// [-25047, -25024, -25058]
		hitWaterAxon = new NpcStep(this, NpcID.ABYSSAL_AXON, "Abyssal Axon (Water)", new WorldPoint(1743, 6421, 0),
			"Hit the Water Axon towards the Water terminal. Avoid the lightning strikes.");
		hitWaterAxon.addTileMarker(new WorldPoint(1736, 6414, 0), SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS);
		hitWaterAxon.setLinePoints(Arrays.asList(
			new WorldPoint(1735, 6422, 0),
			new WorldPoint(1743, 6422, 0),
			new WorldPoint(1743, 6417, 0),
			new WorldPoint(1736, 6417, 0),
			new WorldPoint(1736, 6414, 0)
		));

		enterBlueTeleporter2 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49414,
			new WorldPoint(1730, 6435, 0), "Enter the blue neural teleporter to the west of the area to reach the nerve endings room.");

		enterNerveRoom = new ObjectStep(this, ObjectID.PASSAGE_49411, new WorldPoint(1782, 6420, 0),
			"Enter the nerve endings room.", nothingInHands);

		// Repaired any, 15221, 0->1->2->3->4
		// 15211 represent air, 100->0

		getEarthNerve = new ObjectStep(this, ObjectID.NERVE_ENDING_EARTH, new WorldPoint(1784, 6423, 0), "Break off an earth nerve. Enter the air bubbles when your air's low.");
		getWaterNerve = new ObjectStep(this, ObjectID.NERVE_ENDING_WATER, new WorldPoint(1775, 6426, 0), "Break off a water nerve. Enter the air bubbles when your air's low.");
		getFireNerve = new ObjectStep(this, ObjectID.NERVE_ENDING_FIRE, new WorldPoint(1777, 6422, 0), "Break off a fire nerve. Enter the air bubbles when your air's low.");
		getAirNerve = new ObjectStep(this, ObjectID.NERVE_ENDING_AIR, new WorldPoint(1789, 6429, 0), "Break off an air nerve. Enter the air bubbles when your air's low.");

		makeDustNerve = new DetailedQuestStep(this, "Make a dust nerve by combining an air and earth nerve.", airNerve.highlighted(), earthNerve.highlighted());
		makeLavaNerve = new DetailedQuestStep(this, "Make a lava nerve by combining an earth and fire nerve.", earthNerve.highlighted(), fireNerve.highlighted());
		makeSmokeNerve = new DetailedQuestStep(this, "Make a smoke nerve by combining an air and fire nerve.", airNerve.highlighted(), fireNerve.highlighted());
		makeSteamNerve = new DetailedQuestStep(this, "Make a steam nerve by combining an water and fire nerve.", waterNerve.highlighted(), fireNerve.highlighted());

		repairLavaNerve = new ObjectStep(this, ObjectID.LAVA_NERVE_ENDING_BROKEN, new WorldPoint(1779, 6435, 0), "Repair the lava nerve ending.", lavaNerve);
		repairSmokeNerve = new ObjectStep(this, ObjectID.SMOKE_NERVE_ENDING_BROKEN, new WorldPoint(1778, 6431, 0), "Repair the smoke nerve ending.", smokeNerve);
		repairDustNerve = new ObjectStep(this, ObjectID.DUST_NERVE_ENDING_BROKEN, new WorldPoint(1784, 6433, 0), "Repair the dust nerve ending.", dustNerve);
		repairSteamNerve = new ObjectStep(this, ObjectID.STEAM_NERVE_ENDING_BROKEN, new WorldPoint(1783, 6430, 0), "Repair the steam nerve ending.", steamNerve);

		makeMatchingNerves = new DetailedQuestStep(this, "Collect nerves and combine them together to make nerves which match the broken nerve endings. " +
			"Use these to repair their matching endings. Enter air bubbles whenever low on air.");
		makeMatchingNerves.addSubSteps(getEarthNerve, getWaterNerve, getFireNerve, getAirNerve, makeDustNerve, makeLavaNerve, makeSmokeNerve, makeSteamNerve, repairLavaNerve, repairSmokeNerve, repairDustNerve, repairSteamNerve);

		returnThroughBlueNeuralTeleporter2 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER, new WorldPoint(1789, 6418, 0), "Return through the blue teleporter.");
		enterGreenTeleporter2 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49415, new WorldPoint(1741, 6403, 0), "Enter the green teleporter in the south-west.");
		enterSummoningRoom = new ObjectStep(this, ObjectID.PASSAGE_49412, new WorldPoint(1744, 6457, 0), "Enter the room with a summoning circle in it.");

		killImps = new NpcStep(this, NpcID.SCARRED_IMP, new WorldPoint(1744, 6448, 0),
			"Kill the scarred imps to remove the lessers' prayers. Ignore the other scarred monsters which appear.", true);
		killLesserDemons = new NpcStep(this, NpcID.LESSER_DEMON, new WorldPoint(1744, 6448, 0), "Kill the lesser demons.", true);

		enterBoatRoom2 = new ObjectStep(this, ObjectID.PASSAGE_49418, new WorldPoint(1771, 6459, 0), "Enter the boat room to the north-east.");
		getTinderboxRoom2 = new ObjectStep(this, ObjectID.CRATE_49425, new WorldPoint(1774, 6448, 0), "Search the crate in the south-west of the room for a tinderbox.");
		getGunpowderRoom2 = new ObjectStep(this, ObjectID.SKELETON_49427, new WorldPoint(1779, 6448, 0), "Search the skeleton near the crate for some gunpowder.");
		getSlimyKey = new ObjectStep(this, ObjectID.SKELETON_49426, new WorldPoint(1789, 6449, 0), "Search the skeleton in the south-east for a slimy key.");
		getDampTablet = new ObjectStep(this, ObjectID.CHEST_49420, new WorldPoint(1775, 6460, 0), "Search the chest for a tablet.");
		((ObjectStep) getDampTablet).addAlternateObjects(ObjectID.CHEST_49422);
		readDampTablet = new DetailedQuestStep(this, "Read the old tablet.", dampTablet.highlighted());
		burnBoat2 = new ObjectStep(this, ObjectID.SHIPWRECK, new WorldPoint(1787, 6455, 0), "Burn the shipwreck.");

		enterMiddlePassage = new ObjectStep(this, NullObjectID.NULL_49528, new WorldPoint(2051, 6434, 0), "Enter the passage south of Persten.");
		enterMiddlePassage.addDialogStep("I'll be alright.");
		talkToPerstenAfterRoom1 = new NpcStep(this, NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "Talk to Wizard Persten.");
		talkToPerstenAfterRoom2 = new NpcStep(this, NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "Talk to Wizard Persten.");

		enterMemoryPuzzle = new ObjectStep(this, ObjectID.PASSAGE_49410, new WorldPoint(1897, 6436, 0), "Enter the memory puzzle room to the east.", nothingInHands);
		memoryPuzzleSteps = new MemoryPuzzle(this);

		repairGrowthsRoom3 = new ObjectStep(this, ObjectID.DAMAGED_GROWTH,
			"Take lures from the light leeches to repair the growths. " +
				"Protect from Melee to not take damage from them.", true);

		returnThroughGreenPortal = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49415,
			new WorldPoint(1862, 6410, 0), "Return back through the green portal to the south.");

		enterBoatRoom3 = new ObjectStep(this, ObjectID.PASSAGE_49418, new WorldPoint(1899, 6456, 0), "Enter the boat room in the north-east.");
		getTinderBoxRoom3 = new ObjectStep(this, ObjectID.CRATE_49425, new WorldPoint(1915, 6460, 0),
			"Search the north-east crate for a tinderbox.");
		getGunpowderRoom3 = new ObjectStep(this, ObjectID.SKELETON_49427, new WorldPoint(1912, 6446, 0),
			"Search the skeleton in the south-east corner for gunpowder.");
		getSlimyKeyRoom3 = new ObjectStep(this, ObjectID.SKELETON_49426, new WorldPoint(1902, 6459, 0),
			"Search the skeleton in the north-west for a slimy key.");
		getDampTablet2 = new ObjectStep(this, ObjectID.CHEST_49420, new WorldPoint(1909, 6449, 0),
			"Search the chest to the south for a tablet.");
		((ObjectStep) getDampTablet2).addAlternateObjects(ObjectID.CHEST_49422);
		readDampTablet2 = new DetailedQuestStep(this, "Read the damp tablet.", dampTablet2.highlighted());
		burnBoat3 = new ObjectStep(this, ObjectID.SHIPWRECK, new WorldPoint(1914, 6455, 0), "Burn the shipwreck.");

		enterTreeRoom = new ObjectStep(this, ObjectID.PASSAGE_49412, new WorldPoint(1902, 6411, 0),
			"Enter the room with a tree in the south of the area.");

		// 12401 0->1, inside the spawn room
		killTreeMonsters = new NpcStep(this, NpcID.SCARRED_HELLHOUND, new WorldPoint(1897, 6407, 0),
			"Kill all creatures which come through the portals.", true);
		((NpcStep) killTreeMonsters).addAlternateNpcs(NpcID.SCARRED_GREATER_DEMON);

		enterGreenTeleporter3 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49416, new WorldPoint(1892, 6457, 0),
			"Enter the green teleporter in the north of the area.");
		enterLightLeechRoom = new ObjectStep(this, ObjectID.PASSAGE_49411, new WorldPoint(1862, 6421, 0),
			"Enter the light leech room.");
		// Repaired all, 15210 = 4
		repairCrimsonVeins = new ObjectStep(this, NullObjectID.NULL_49537,
			"Kill crimson sanguisphera for crimson fibre to repair all the crimson veins.", true);
		repairRadiantVeins = new ObjectStep(this, NullObjectID.NULL_49536,
			"Kill radiant sanguisphera for radiant fibre to repair all the radiant veins.", true);

		getLure = new NpcStep(this, NpcID.LIGHT_LEECH, "Light leech", new WorldPoint(1868, 6430, 0),
			"Get a lure from a light lure.", true);
		repairGrowthsRoom3.addSubSteps(getLure);

		killRadiant = new NpcStep(this, NpcID.RADIANT_SANGUISPHERA, new WorldPoint(1868, 6430, 0),
			"Kill radiant sanguisphera with Protect from Magic on for a radiant fibre.", true, protectFromMagic, radiantFibre);
		killCrimson = new NpcStep(this, NpcID.CRIMSON_SANGUISPHERA, new WorldPoint(1868, 6430, 0),
			"Kill crimson sanguisphera with Protect from Magic on for a radiant fibre.", true, protectFromMagic, crimsonFibre);

		repairCrimsonVeins.addSubSteps(killCrimson);
		repairRadiantVeins.addSubSteps(killRadiant);

		talkToPerstenAfterRooms = new NpcStep(this, NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "Talk to Wizard Persten.");

		boardBoatToLeviathan = new ObjectStep(this, ObjectID.ROWBOAT_49212, new WorldPoint(2065, 6436, 0), "Board the boat to the Leviathan area.");
		killLeviathan = new NpcStep(this, NpcID.THE_LEVIATHAN, "");
		((NpcStep) killLeviathan).addAlternateNpcs(NpcID.THE_LEVIATHAN_12215, NpcID.THE_LEVIATHAN_12219, NpcID.THE_LEVIATHAN_12221);
		goKillLeviathan = new ConditionalStep(this, goToAbyss,
			"Consider re-gearing, then board the boat to fight the Leviathan. Read the sidebar for more details.",
			rangedCombatGear, ancientMagicksActive, shadowBurstRunes, food, prayerPotions);
		goKillLeviathan.addStep(inLeviathanArea, killLeviathan);
		goKillLeviathan.addStep(inTentArea, boardBoatToLeviathan);
		goKillLeviathan.addStep(inDemonArea, hopOverSteppingStone);

		killLeviathanSidebar = new DetailedQuestStep(this, "Consider re-gearing, then board the boat to fight the Leviathan.");
		killLeviathanSidebar.addText("Use ranged combat. Its attacks register as 'hitting' you upon actual contact, so make sure to pray when projectiles are hitting you rather than being created.");
		killLeviathanSidebar.addText("Shadow spells will stun it.");
		killLeviathanSidebar.addText("Blue projectiles are magic attacks, green ranged, and orange melee. Avoid dropping rocks.");
		killLeviathanSidebar.addText("Run away from electricity attacks. When boulders start falling, walk them in a line to create a wall which you can hide behind from the Leviathan, as after 10 rocks you will need to use them to avoid a blast attack.");
		killLeviathanSidebar.addText("At 20% health, the leviathan will become enraged, constantly dropping rocks. You need to locate an abyssal pathfinder and stay near it until you kill the Leviathan.");
		killLeviathanSidebar.addSubSteps(goKillLeviathan);

		createLeviathan(client, this, runeliteObjectManager);

		climbDownFromLeviathan = new ObjectStep(this, ObjectID.HANDHOLDS_47594, new WorldPoint(2091, 6380, 0),
			"Climb down the handholds in the north-eastern corner of the Leviathan area.");
		hopAcrossFromLeviathan = new ObjectStep(this, NullObjectID.NULL_49529,
			new WorldPoint(2096, 6382, 0), "Cross the stepping stone.");

		talkToPerstenAtShip = new NpcStep(this, NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2098, 6374, 0),
			"");

		goToShip = new ConditionalStep(this, goToAbyss, "Go to the ship north-east of the Leviathan boss area, and talk to Persten there..");
		goToShip.addStep(and(inNELeviathanArea, perstenAtShip), talkToPerstenAtShip);
		goToShip.addStep(inNELeviathanArea, hopAcrossFromLeviathan);
		goToShip.addStep(inLeviathanArea, climbDownFromLeviathan);
		goToShip.addStep(inTentArea, boardBoatToLeviathan);
		goToShip.addStep(inDemonArea, hopOverSteppingStone);

		searchDebris = new ObjectStep(this, NullObjectID.NULL_49218, new WorldPoint(2099, 6374, 0),
			"Search the debris next to the ship.");

		returnToDesertWithPerseriyasMedallion = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Return to the Vault door north-east of Nardah. Be wary of an assassin coming to kill you! They can run, freeze, and teleblock you.",
			perseriyasMedallion);
		returnToDesertWithPerseriyasMedallion.addTeleport(nardahTeleport);

		usePerseriyasMedallionOnStatue = new ObjectStep(this, NullObjectID.NULL_49505, new WorldPoint(3942, 9626, 1),
			"Use the medallion on the south-east statue.", perseriyasMedallion.highlighted());
		usePerseriyasMedallionOnStatue.addIcon(ItemID.PERSERIYAS_MEDALLION);

		moreComingSoon = new DetailedQuestStep(this, "The rest of the Quest Helper will come out once it's ready.");
	}

	final BufferedImage missIcon = Icon.BLUE_HITSPLAT.getImage();
	final BufferedImage hitIcon = Icon.MAX_HITSPLAT.getImage();

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		Point imageLocation = client.getLocalPlayer().getCanvasImageLocation(missIcon, client.getLocalPlayer().getLogicalHeight() / 2);
		if (imageLocation != null)
		{
			if (showBlueHitsplatUntilTick >= client.getTickCount())
			{
				OverlayUtil.renderImageLocation(graphics, imageLocation, missIcon);
			}
			if (showRedHitsplatUntilTick >= client.getTickCount())
			{
				OverlayUtil.renderImageLocation(graphics, imageLocation, hitIcon);
			}
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, allBursts, facemask);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(nardahTeleport, waterSource, senntistenTeleport, staminaPotions, freezes, xericTalisman);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Ancient Guardian (level 153)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(5);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ring of Shadows", ItemID.RING_OF_SHADOWS),
			new ItemReward("Ancient Lamp (100k exp in a combat skill)", ItemID.ANCIENT_LAMP, 3)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Ability to use Ancient Rings."),
			new UnlockReward("Access to four new bosses"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("The Vault",
			Arrays.asList(attemptToEnterVaultDoor, talkToAsgarnia, inspectPlaque, inspectStatueNE,
				inspectStatueNW, inspectStatueSW, inspectStatueSE, talkToAsgarniaAgain),
			Arrays.asList(ancientMagicksActive),
			Arrays.asList(nardahTeleport, waterSource, senntistenTeleport)));

		allSteps.add(new PanelDetails("Learning of the Ancients",
			Arrays.asList(talkToBalando, operateWinch, talkToBanikan, getPickaxe, mineRocks,
				enterDigsiteHole, killAncientGuardian, talkToBanikanInGolemRoom, inspectGolem,
				inspectAltar, castOnBloodStatue, castOnShadowStatue, castOnSmokeStatue, castOnIceStatue,
				searchCrateForCharges, imbueAtAltar, chargeGolem, solveGolemPuzzle, operateGolem, talkToBanikanAfterGolem,
				operateGolemFrostenhorn),
			Arrays.asList(combatGear, allBursts),
			Arrays.asList(senntistenTeleport)));
		allSteps.add(new PanelDetails("Vardorvis",
			Arrays.asList(talkToElissa, talkToBarus, searchDesk, readPotionNote, drinkPotion, boardBoat,
				runIntoStanglewood, talkToKasonde, enterEntry, defendKasondeSidebar, leaveTowerDefenseRoom,
				talkToKasondeAfterTowerDefense, getHerbSidebar, getBerry, goDownToKasonde, defendKasondeHerb,
				talkToKasondeWithHerbAndBerry, addHerb, addBerry, drinkStranglewoodPotion, goToRitualSite, fightVardorvisSidebar,
				pickUpTempleKey, returnToKasondeWithTempleKey, defeatKasonde, goTalkToKasondeAfterFight,
				goGetVardorvisMedallion, returnToDesertWithVardorvisMedallion, useVardorvisMedallionOnStatue),
			Arrays.asList(combatGear),
			Arrays.asList(xericTalisman, freezes)));
		allSteps.add(new PanelDetails("Perseriya",
			Arrays.asList(goTalkToCatalyticGuardian, goKillDemons, hopOverSteppingStone, goBoardBoat, goTalkToPersten),
			Arrays.asList(facemask),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - Room 1",
			Arrays.asList(enterPassage1,
				enterPathfinderRoom, doPath1, doPath2, doPath3, doPath4, doPath5, doPath6, enterGreenTeleporter1, enterCatalystRoom,
				solveCatalystRoom, enterBlueTeleporter1, enterGrowthRoom, repairGrowths, growthPuzzle, returnThroughBlueNeuralTeleporter,
				enterBoatRoom1, searchSkeletonForKey, getOldTablet, readOldTablet, searchSkeletonForGunpowder, getTinderbox, burnBoat1),
			Arrays.asList(facemask),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - Room 2",
			Arrays.asList(talkToPerstenAfterRoom1, enterSouthEastPassage, enterAxonRoom, hitCosmicAxon, hitFireAxon, hitNatureAxon, hitWaterAxon, enterBlueTeleporter2, enterNerveRoom,
				makeMatchingNerves, returnThroughBlueNeuralTeleporter2, enterGreenTeleporter2, enterSummoningRoom, killImps, killLesserDemons, enterBoatRoom2,
				getSlimyKey, getDampTablet, readDampTablet, getTinderboxRoom2, getGunpowderRoom2, burnBoat2),
			Arrays.asList(facemask),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - Room 3",
			Arrays.asList(talkToPerstenAfterRoom2, enterMiddlePassage, enterMemoryPuzzle, memoryPuzzleSteps, enterTreeRoom,
				killTreeMonsters, enterGreenTeleporter3, enterLightLeechRoom, repairGrowthsRoom3, repairCrimsonVeins, repairRadiantVeins, returnThroughGreenPortal,
				enterBoatRoom3, getSlimyKeyRoom3, getDampTablet2, readDampTablet2, getTinderBoxRoom3, getGunpowderRoom3, burnBoat3),
			Arrays.asList(facemask),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - The battle",
			Arrays.asList(talkToPerstenAfterRooms, killLeviathanSidebar, goToShip, searchDebris,
				returnToDesertWithPerseriyasMedallion, usePerseriyasMedallionOnStatue),
			Arrays.asList(rangedCombatGear, shadowBurstRunes),
			Arrays.asList(eyeTeleport, staminaPotions, food, prayerPotions)));
		allSteps.add(new PanelDetails("More to come...", Arrays.asList(moreComingSoon)));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SECRETS_OF_THE_NORTH, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ENAKHRAS_LAMENT, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TEMPLE_OF_THE_EYE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_GARDEN_OF_DEATH, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.BELOW_ICE_MOUNTAIN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HIS_FAITHFUL_SERVANTS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MAGIC, 75));
		req.add(new SkillRequirement(Skill.FIREMAKING, 75));
		req.add(new SkillRequirement(Skill.THIEVING, 70));
		req.add(new SkillRequirement(Skill.HERBLORE, 62));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 60));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 60));
		req.add(ancientMagicksActive);

		return req;
	}
}
