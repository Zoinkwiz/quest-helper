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

import com.questhelper.ItemCollections;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import static com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan.createLeviathan;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.util.ItemSlots;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.RuneliteObjectManager;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;

public class PerseriyaSteps extends ConditionalStep
{
	ItemRequirement eyeTeleport, facemask;

	final int PERSERIYA_VARBIT = 15128;

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
		solvedGrowthRoom, inBoatRoom1, completedRoom1, talkedToPerstenAfterRoom1;
	DetailedQuestStep doPath1, doPath2, doPath3, doPath4, doPath5, doPath6, enterGreenTeleporter1, enterCatalystRoom, solveCatalystRoom,
		enterBlueTeleporter1, enterGrowthRoom, repairGrowths, growthPuzzle, returnThroughBlueNeuralTeleporter, enterBoatRoom1, getTinderbox,
		burnBoat1, searchSkeletonForKey, searchSkeletonForGunpowder, getOldTablet, readOldTablet, talkToPerstenAfterRoom1, getLureBonus;

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
		smokeNerveBroken, completedNerveRoom, inNervePassage, impsNearby, completedSummoningRoom, haveReadTablet, completedRoom2,
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
		solvedLeechRoom, inSwRoom3, inBoatRoom3, repairedGrowthRoom3, repairedCrimsonVeins,
		protectFromMagic, completedRoom3;

	Zone memoryPuzzle, treeRoom, leechRoom, swRoom3P1, swRoom3P2, swRoom3P3, swRoom3P4, swRoom3P5, boatRoom3;

	DetailedQuestStep talkToPerstenAfterRooms, boardBoatToLeviathan, killLeviathan,
		killLeviathanSidebar, climbDownFromLeviathan, hopAcrossFromLeviathan, talkToPerstenAtShip,
		searchDebris, returnToDesertWithPerseriyasMedallion, usePerseriyasMedallionOnStatue;

	ConditionalStep goKillLeviathan, goToShip;

	Zone leviathanArea, neLeviathanArea, vault;

	Requirement inLeviathanArea, readyToFightLeviathan, inNELeviathanArea, defeatedLeviathan, perstenAtShip,
		perstenLeft, foundPerseriyasMedallion;

	Requirement shouldReadTablet1, shouldReadTablet2, shouldReadTablet3, inVault;

	ItemRequirement nardahTeleport, combatGear, rangedCombatGear, shadowBurstRunes, food, prayerPotions;

	SpellbookRequirement ancientMagicksActive;

	RuneliteObjectManager runeliteObjectManager;

	public PerseriyaSteps(QuestHelper questHelper, QuestStep defaultStep, RuneliteObjectManager runeliteObjectManager) // goTalkToCatalyticGuardian
	{
		super(questHelper, defaultStep);
		this.runeliteObjectManager = runeliteObjectManager;
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		ConditionalStep doPathfinderRoom = new ConditionalStep(getQuestHelper(), enterPathfinderRoom);
		doPathfinderRoom.addStep(onPath6, doPath6);
		doPathfinderRoom.addStep(onPath5, doPath5);
		doPathfinderRoom.addStep(onPath4, doPath4);
		doPathfinderRoom.addStep(onPath3, doPath3);
		doPathfinderRoom.addStep(onPath2, doPath2);
		doPathfinderRoom.addStep(onPath1, doPath1);

		Conditions doingPathfinderRoom = new Conditions(LogicType.OR, onPath1, onPath2, onPath3, onPath4, onPath5, onPath6);

		ConditionalStep solveAbyssRoom1 = new ConditionalStep(getQuestHelper(), enterPathfinderRoom);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom,
			haveReadTablet, gunpowder, tinderbox), burnBoat1);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom,
			haveReadTablet, gunpowder), getTinderbox);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, haveReadTablet),
			searchSkeletonForGunpowder);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, slimyKey),
			getOldTablet);
		solveAbyssRoom1.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom),
			searchSkeletonForKey);
		solveAbyssRoom1.addStep(new Conditions(new Conditions(LogicType.OR, inGrowthRoom, isNearGrowthRoom),
			completedCatalystRoom, destroyedTether, solvedGrowthRoom), returnThroughBlueNeuralTeleporter);
		solveAbyssRoom1.addStep(new Conditions(completedCatalystRoom, destroyedTether, solvedGrowthRoom), enterBoatRoom1);
		solveAbyssRoom1.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether, repairedGrowths), growthPuzzle);
		solveAbyssRoom1.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether, illuminatingLure), repairGrowths);
		solveAbyssRoom1.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether), getLureBonus);
		solveAbyssRoom1.addStep(new Conditions(isNearGrowthRoom, completedCatalystRoom, destroyedTether), enterGrowthRoom);
		solveAbyssRoom1.addStep(new Conditions(completedCatalystRoom, destroyedTether), enterBlueTeleporter1);
		solveAbyssRoom1.addStep(new Conditions(inCatalystRoom, destroyedTether), solveCatalystRoom);
		solveAbyssRoom1.addStep(new Conditions(isNearCatalystRoom, destroyedTether), enterCatalystRoom);
		solveAbyssRoom1.addStep(destroyedTether, enterGreenTeleporter1);
		solveAbyssRoom1.addStep(doingPathfinderRoom, doPathfinderRoom);

		ConditionalStep repairNerve = new ConditionalStep(getQuestHelper(), getWaterNerve);
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

		ConditionalStep solveAbyssRoom2 = new ConditionalStep(getQuestHelper(), enterSouthEastPassage);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, haveReadTablet, tinderbox, gunpowder), burnBoat2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, haveReadTablet, tinderbox), getGunpowderRoom2);
		solveAbyssRoom2.addStep(and(completedAxonRoom, completedNerveRoom, completedSummoningRoom, inBoatRoom2, haveReadTablet), getTinderboxRoom2);
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

		ConditionalStep solveAbyssRoom3 = new ConditionalStep(getQuestHelper(), enterMemoryPuzzle);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, haveReadTablet, tinderbox, gunpowder), burnBoat3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, haveReadTablet, tinderbox), getGunpowderRoom3);
		solveAbyssRoom3.addStep(and(solvedMemoryRoom, inBoatRoom3, haveReadTablet), getTinderBoxRoom3);
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

		addStep(null, goTalkToCatalyticGuardian);
		addStep(and(inVault, foundPerseriyasMedallion), usePerseriyasMedallionOnStatue);
		addStep(foundPerseriyasMedallion, returnToDesertWithPerseriyasMedallion);
		addStep(and(perstenLeft, inNELeviathanArea), searchDebris);
		addStep(defeatedLeviathan, goToShip);
		addStep(readyToFightLeviathan, goKillLeviathan);
		addStep(new Conditions(inTentArea, completedRoom3), talkToPerstenAfterRooms);
		addStep(and(shouldReadTablet3, dampTablet2), readDampTablet2);
		addStep(and(shouldReadTablet2, dampTablet), readDampTablet);
		addStep(and(shouldReadTablet1, oldTablet), readOldTablet);
		addStep(inAbyssRoom3, solveAbyssRoom3);
		addStep(new Conditions(inTentArea, talkedToPerstenAfterRoom2), enterMiddlePassage);
		addStep(new Conditions(inTentArea, completedRoom2), talkToPerstenAfterRoom2);
		addStep(inAbyssRoom2, solveAbyssRoom2);
		addStep(new Conditions(inTentArea, talkedToPerstenAfterRoom1), enterSouthEastPassage);
		addStep(new Conditions(inTentArea, completedRoom1), talkToPerstenAfterRoom1);
		addStep(inAbyssRoom1, solveAbyssRoom1);
		addStep(talkedToPersten, goDoPassage1);
		addStep(attemptedToBoardBoat, goTalkToPersten);
		addStep(defeatedDemons, goBoardBoat);
		addStep(inDemonArea, goKillDemons);
	}
	protected void setupZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));

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

	protected void setupItemRequirements()
	{
		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(5672, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_4);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_3, ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_2);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		eyeTeleport = new ItemRequirement("Teleport to Temple of the Eye via minigame teleport or Amulet of the Eye", ItemID.AMULET_OF_THE_EYE_26990);
		eyeTeleport.addAlternates(ItemID.AMULET_OF_THE_EYE, ItemID.AMULET_OF_THE_EYE_26992, ItemID.AMULET_OF_THE_EYE_26994);

		facemask = new ItemRequirement("Facemask", ItemCollections.SLAYER_HELMETS);
		facemask.addAlternates(ItemID.FACEMASK, ItemID.GAS_MASK);

		rangedCombatGear = new ItemRequirement("Ranged combat gear", -1);
		rangedCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);
		ancientMagicksActive = new SpellbookRequirement(Spellbook.ANCIENT);

		shadowBurstRunes = new ItemRequirements("Shadow burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 1));

		/* Quest items */
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

	protected void setupConditions()
	{
		inVault = new ZoneRequirement(vault);

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
		// TODO: Verify if order is random for this stuff, and thus variable needs to consider some shift based on area?h
		haveReadTablet = new Conditions(LogicType.OR,
			new VarbitRequirement(PERSERIYA_VARBIT, 18),
			new VarbitRequirement(PERSERIYA_VARBIT, 26),
			new VarbitRequirement(PERSERIYA_VARBIT, 34)
		);

		// 18->20, burned ship
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
		shouldReadTablet1 = new VarbitRequirement(PERSERIYA_VARBIT, 16);
		shouldReadTablet2 = new VarbitRequirement(PERSERIYA_VARBIT, 24);
		shouldReadTablet3 = new VarbitRequirement(PERSERIYA_VARBIT, 32);
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
	}

	protected void setupSteps()
	{
		enterWizardBasement = new ObjectStep(getQuestHelper(), ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"Go to the Temple of the Eye.");
		enterWizardBasement.addTeleport(eyeTeleport);
		enterPortalToTempleOfTheEye = new ObjectStep(getQuestHelper(), ObjectID.PORTAL_43765, new WorldPoint(3104, 9574, 0),
			"Enter the portal to the Temple of the Eye.");
		NpcStep talkToCatalyticGuardian = new NpcStep(getQuestHelper(), NpcID.CATALYTIC_GUARDIAN, new WorldPoint(3611, 9473, 0),
			"Talk to the Catalytic Guardian to travel into the Rift.");
		talkToCatalyticGuardian.addAlternateNpcs(NpcID.CATALYTIC_GUARDIAN_12384);
		talkToCatalyticGuardian.addDialogStep("Yes.");

		ConditionalStep goToAbyss = new ConditionalStep(getQuestHelper(), enterWizardBasement);
		goToAbyss.addStep(inTempleOfTheEye, talkToCatalyticGuardian);
		goToAbyss.addStep(inWizardBasement, enterPortalToTempleOfTheEye);

		goTalkToCatalyticGuardian = new ConditionalStep(getQuestHelper(), goToAbyss,
			"Talk to the Catalytic Guardian by the portal in the Temple of the Eye.", combatGear, facemask);

		killDemons = new NpcStep(getQuestHelper(), NpcID.SCARRED_IMP, new WorldPoint(2021, 6433, 0),
			"", true);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.ABYSSAL_WALKER_12392, NpcID.ABYSSAL_WALKER_12394);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.ABYSSAL_LEECH_12391, NpcID.ABYSSAL_LEECH_12393);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.GREATER_DEMON_12387, NpcID.LESSER_DEMON_12389);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_BLACK_DEMON, NpcID.BLACK_DEMON_12385);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_GREATER_DEMON_12388, NpcID.SCARRED_LESSER_DEMON_12390);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_BLACK_DEMON, NpcID.SCARRED_LESSER_DEMON_12378);

		goKillDemons = new ConditionalStep(getQuestHelper(), goToAbyss, "Kill all the demons and abyssal entities in the rift.",
			combatGear, facemask);
		goKillDemons.addStep(inDemonArea, killDemons);

		hopOverSteppingStone = new ObjectStep(getQuestHelper(), ObjectID.STEPPING_STONE_49209, new WorldPoint(2031, 6430, 0),
			"Hop across the stepping stone.");

		ObjectStep boardAbyssBoat = new ObjectStep(getQuestHelper(), ObjectID.ROWBOAT_49212, new WorldPoint(2065, 6436, 0),
			"");

		goBoardBoat = new ConditionalStep(getQuestHelper(), goToAbyss, "Attempt to board the boat to the east.", facemask);
		goBoardBoat.addStep(inTentArea, boardAbyssBoat);
		goBoardBoat.addStep(inDemonArea, hopOverSteppingStone);

		talkToPersten = new NpcStep(getQuestHelper(), NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "");
		goTalkToPersten = new ConditionalStep(getQuestHelper(), goToAbyss, "Talk to Wizard Persten in the Rift.", facemask);
		goTalkToPersten.addStep(inTentArea, talkToPersten);
		goTalkToPersten.addStep(inDemonArea, hopOverSteppingStone);

		enterPassage1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49526, new WorldPoint(2043, 6441, 0),
			"Enter the north-west passage.");

		goDoPassage1 = new ConditionalStep(getQuestHelper(), goToAbyss, facemask);
		goDoPassage1.addStep(inTentArea, enterPassage1);
		goDoPassage1.addStep(inDemonArea, hopOverSteppingStone);

		enterPathfinderRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49410, new WorldPoint(2196, 6454, 0),
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

		doPath1 = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2197, 6444, 0),
			"Move the nearest pathfinder from the north, and follow it within a 3x3 area until the next pathfinder.");
		doPath1.addTileMarker(new WorldPoint(2195, 6451, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath1.setLinePoints(Arrays.asList(
			new WorldPoint(2195, 6450, 0),
			new WorldPoint(2195, 6444, 0),
			new WorldPoint(2197, 6444, 0)
		));

		doPath2 = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2202, 6442, 0),
			"Move the next pathfinder from the west, and step off when safe to the south pathfinder.");
		doPath2.addTileMarker(new WorldPoint(2197, 6444, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath2.setLinePoints(Arrays.asList(
			new WorldPoint(2199, 6444, 0),
			new WorldPoint(2202, 6444, 0),
			new WorldPoint(2202, 6442, 0)
		));

		doPath3 = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2198, 6438, 0),
			"Move the next pathbreaker from the north, and step off when safe to the west pathbreaker.");
		doPath3.addTileMarker(new WorldPoint(2202, 6442, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath3.setLinePoints(Arrays.asList(
			new WorldPoint(2202, 6440, 0),
			new WorldPoint(2201, 6439, 0),
			new WorldPoint(2198, 6438, 0)
		));

		doPath4 = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2196, 6435, 0),
			"Move the next pathbreaker from the west, and step off when safe to the south-west pathbreaker.");
		doPath4.addTileMarker(new WorldPoint(2198, 6438, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath4.setLinePoints(Arrays.asList(
			new WorldPoint(2197, 6438, 0),
			new WorldPoint(2196, 6435, 0)
		));

		doPath5 = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2207, 6436, 0),
			"Move the next pathbreaker from the east, and step off when safe to the east pathbreaker.");
		doPath5.addTileMarker(new WorldPoint(2196, 6435, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath5.setLinePoints(Arrays.asList(
			new WorldPoint(2196, 6435, 0),
			new WorldPoint(2207, 6436, 0)
		));

		doPath6 = new ObjectStep(getQuestHelper(), ObjectID.ABYSSAL_TETHER, new WorldPoint(2210, 6433, 0),
			"Move the next pathbreaker from the south, and step off to destroy the abyssal tether.");
		doPath6.addTileMarker(new WorldPoint(2207, 6436, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath6.setLinePoints(Arrays.asList(
			new WorldPoint(2207, 6436, 0),
			new WorldPoint(2207, 6433, 0),
			new WorldPoint(2209, 6433, 0)
		));

		enterGreenTeleporter1 = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER_49415, new WorldPoint(2234, 6461, 0),
			"Enter the neural teleporter in the north-east corner.");

		enterCatalystRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49411, new WorldPoint(2190, 6412, 0),
			"Enter the catalyst room in the south-west corner of the area.");

		// TODO: improve guidance on this section
		solveCatalystRoom = new DetailedQuestStep(getQuestHelper(), "You need to use the rune nerves on the catalyst which match the current symbol until the walkers die.");
		solveCatalystRoom.addText("You can make additional rune nerves by combining nerves:");
		solveCatalystRoom.addText("Mind + mind = soul.");
		solveCatalystRoom.addText("Water + earth = nature.");
		solveCatalystRoom.addText("Soul + nature = cosmic.");
		solveCatalystRoom.addText("Cosmic + earth = astral.");
		solveCatalystRoom.addText("Fire + air = smoke.");
		solveCatalystRoom.addText("Mind + water = blood.");
		solveCatalystRoom.addText("Smoke + blood = wrath.");

		enterBlueTeleporter1 = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER_49414, new WorldPoint(2181, 6425, 0),
			"Enter the blue teleporter to the west of the area.");

		enterGrowthRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49412, new WorldPoint(2225, 6421, 0),
			"Enter the damage growth room to the south.");

		repairGrowths = new ObjectStep(getQuestHelper(), ObjectID.DAMAGED_GROWTH, "Take lures from the light leeches to repair the growths. Protect from Melee to not take damage from them.", true);
		getLureBonus = new NpcStep(getQuestHelper(), NpcID.LIGHT_LEECH,
			"Get a lure from a light lure. Protect from Melee to avoid damage.", true);
		repairGrowths.addSubSteps(getLureBonus);
		growthPuzzle = new GrowthPuzzleStep(getQuestHelper());

		returnThroughBlueNeuralTeleporter = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER,
			new WorldPoint(2237, 6444, 0), "Head to the south-east room via the blue neural transmitter.");
		enterBoatRoom1 = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49418, new WorldPoint(2220, 6402, 0),
			"Enter the south-east room via the southern corridor.");

		getTinderbox = new ObjectStep(getQuestHelper(), ObjectID.CRATE_49425, new WorldPoint(2236, 6402, 0), "Search the south-eastern crate for a tinderbox.");
		burnBoat1 = new ObjectStep(getQuestHelper(), ObjectID.SHIPWRECK, new WorldPoint(2235, 6409, 0), "Burn the boat.");
		searchSkeletonForKey = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49426, new WorldPoint(2221, 6414, 0),
			"Search the north-west skeleton for a key.");
		searchSkeletonForGunpowder = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49427, new WorldPoint(2223, 6402, 0),
			"Search the south-west skeleton for gunpowder.");
		getOldTablet = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49420, new WorldPoint(2231, 6413, 0),
			"Open the chest in the north of the room for a tablet.", slimyKey);
		((ObjectStep) getOldTablet).addAlternateObjects(ObjectID.CHEST_49422);
		readOldTablet = new DetailedQuestStep(getQuestHelper(), "Read the old tablet.", oldTablet.highlighted());

		enterSouthEastPassage = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49527, new WorldPoint(2047, 6427, 0),
			"Enter the passage to the south on the east wall.", facemask.equipped());

		enterAxonRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49410, new WorldPoint(1756, 6420, 0), "Enter the Abyssal Axon room to the west.");
		// [9816, 9672, 9777]
		hitCosmicAxon = new NpcStep(getQuestHelper(), NpcID.ABYSSAL_AXON, "Abyssal Axon (Cosmic)", new WorldPoint(1743, 6421, 0),
			"Hit the Cosmic Axon towards the Cosmic terminal. Avoid the lightning strikes.");
		hitCosmicAxon.addTileMarker(new WorldPoint(1746, 6414, 0), SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS);
		hitCosmicAxon.setLinePoints(Arrays.asList(
			// Cosmic Axon
			new WorldPoint(1749, 6419, 0),
			new WorldPoint(1747, 6419, 0),
			new WorldPoint(1747, 6414, 0)
		));

		// [553, 573, 542]
		hitFireAxon = new NpcStep(getQuestHelper(), NpcID.ABYSSAL_AXON, "Abyssal Axon (Fire)", new WorldPoint(1743, 6421, 0),
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
		hitNatureAxon = new NpcStep(getQuestHelper(), NpcID.ABYSSAL_AXON, "Abyssal Axon (Nature)", new WorldPoint(1743, 6421, 0),
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
		hitWaterAxon = new NpcStep(getQuestHelper(), NpcID.ABYSSAL_AXON, "Abyssal Axon (Water)", new WorldPoint(1743, 6421, 0),
			"Hit the Water Axon towards the Water terminal. Avoid the lightning strikes.");
		hitWaterAxon.addTileMarker(new WorldPoint(1736, 6414, 0), SpriteID.QUESTS_PAGE_ICON_BLUE_QUESTS);
		hitWaterAxon.setLinePoints(Arrays.asList(
			new WorldPoint(1735, 6422, 0),
			new WorldPoint(1743, 6422, 0),
			new WorldPoint(1743, 6417, 0),
			new WorldPoint(1736, 6417, 0),
			new WorldPoint(1736, 6414, 0)
		));

		enterBlueTeleporter2 = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER_49414,
			new WorldPoint(1730, 6435, 0), "Enter the blue neural teleporter to the west of the area to reach the nerve endings room.");

		enterNerveRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49411, new WorldPoint(1782, 6420, 0),
			"Enter the nerve endings room.", nothingInHands);

		// Repaired any, 15221, 0->1->2->3->4
		// 15211 represent air, 100->0

		getEarthNerve = new ObjectStep(getQuestHelper(), ObjectID.NERVE_ENDING_EARTH, new WorldPoint(1784, 6423, 0), "Break off an earth nerve. Enter the air bubbles when your air's low.");
		getWaterNerve = new ObjectStep(getQuestHelper(), ObjectID.NERVE_ENDING_WATER, new WorldPoint(1775, 6426, 0), "Break off a water nerve. Enter the air bubbles when your air's low.");
		getFireNerve = new ObjectStep(getQuestHelper(), ObjectID.NERVE_ENDING_FIRE, new WorldPoint(1777, 6422, 0), "Break off a fire nerve. Enter the air bubbles when your air's low.");
		getAirNerve = new ObjectStep(getQuestHelper(), ObjectID.NERVE_ENDING_AIR, new WorldPoint(1789, 6429, 0), "Break off an air nerve. Enter the air bubbles when your air's low.");

		makeDustNerve = new DetailedQuestStep(getQuestHelper(), "Make a dust nerve by combining an air and earth nerve.", airNerve.highlighted(), earthNerve.highlighted());
		makeLavaNerve = new DetailedQuestStep(getQuestHelper(), "Make a lava nerve by combining an earth and fire nerve.", earthNerve.highlighted(), fireNerve.highlighted());
		makeSmokeNerve = new DetailedQuestStep(getQuestHelper(), "Make a smoke nerve by combining an air and fire nerve.", airNerve.highlighted(), fireNerve.highlighted());
		makeSteamNerve = new DetailedQuestStep(getQuestHelper(), "Make a steam nerve by combining an water and fire nerve.", waterNerve.highlighted(), fireNerve.highlighted());

		repairLavaNerve = new ObjectStep(getQuestHelper(), ObjectID.LAVA_NERVE_ENDING_BROKEN, new WorldPoint(1779, 6435, 0), "Repair the lava nerve ending.", lavaNerve);
		repairSmokeNerve = new ObjectStep(getQuestHelper(), ObjectID.SMOKE_NERVE_ENDING_BROKEN, new WorldPoint(1778, 6431, 0), "Repair the smoke nerve ending.", smokeNerve);
		repairDustNerve = new ObjectStep(getQuestHelper(), ObjectID.DUST_NERVE_ENDING_BROKEN, new WorldPoint(1784, 6433, 0), "Repair the dust nerve ending.", dustNerve);
		repairSteamNerve = new ObjectStep(getQuestHelper(), ObjectID.STEAM_NERVE_ENDING_BROKEN, new WorldPoint(1783, 6430, 0), "Repair the steam nerve ending.", steamNerve);

		makeMatchingNerves = new DetailedQuestStep(getQuestHelper(), "Collect nerves and combine them together to make nerves which match the broken nerve endings. " +
			"Use these to repair their matching endings. Enter air bubbles whenever low on air.");
		makeMatchingNerves.addSubSteps(getEarthNerve, getWaterNerve, getFireNerve, getAirNerve, makeDustNerve, makeLavaNerve, makeSmokeNerve, makeSteamNerve, repairLavaNerve, repairSmokeNerve, repairDustNerve, repairSteamNerve);

		returnThroughBlueNeuralTeleporter2 = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER, new WorldPoint(1789, 6418, 0), "Return through the blue teleporter.");
		enterGreenTeleporter2 = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER_49415, new WorldPoint(1741, 6403, 0), "Enter the green teleporter in the south-west.");
		enterSummoningRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49412, new WorldPoint(1744, 6457, 0), "Enter the room with a summoning circle in it.");

		killImps = new NpcStep(getQuestHelper(), NpcID.SCARRED_IMP, new WorldPoint(1744, 6448, 0),
			"Kill the scarred imps to remove the lessers' prayers. Ignore the other scarred monsters which appear.", true);
		killLesserDemons = new NpcStep(getQuestHelper(), NpcID.LESSER_DEMON, new WorldPoint(1744, 6448, 0), "Kill the lesser demons.", true);

		enterBoatRoom2 = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49418, new WorldPoint(1771, 6459, 0), "Enter the boat room to the north-east.");
		getTinderboxRoom2 = new ObjectStep(getQuestHelper(), ObjectID.CRATE_49425, new WorldPoint(1774, 6448, 0), "Search the crate in the south-west of the room for a tinderbox.");
		getGunpowderRoom2 = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49427, new WorldPoint(1779, 6448, 0), "Search the skeleton near the crate for some gunpowder.");
		getSlimyKey = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49426, new WorldPoint(1789, 6449, 0), "Search the skeleton in the south-east for a slimy key.");
		getDampTablet = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49420, new WorldPoint(1775, 6460, 0), "Search the chest for a tablet.");
		((ObjectStep) getDampTablet).addAlternateObjects(ObjectID.CHEST_49422);
		readDampTablet = new DetailedQuestStep(getQuestHelper(), "Read the old tablet.", dampTablet.highlighted());
		burnBoat2 = new ObjectStep(getQuestHelper(), ObjectID.SHIPWRECK, new WorldPoint(1787, 6455, 0), "Burn the shipwreck.");

		enterMiddlePassage = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49528, new WorldPoint(2051, 6434, 0), "Enter the passage south of Persten.");
		enterMiddlePassage.addDialogStep("I'll be alright.");
		talkToPerstenAfterRoom1 = new NpcStep(getQuestHelper(), NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "Talk to Wizard Persten.");
		talkToPerstenAfterRoom2 = new NpcStep(getQuestHelper(), NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "Talk to Wizard Persten.");

		enterMemoryPuzzle = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49410, new WorldPoint(1897, 6436, 0), "Enter the memory puzzle room to the east.", nothingInHands);
		memoryPuzzleSteps = new MemoryPuzzle(getQuestHelper());

		repairGrowthsRoom3 = new ObjectStep(getQuestHelper(), ObjectID.DAMAGED_GROWTH,
			"Take lures from the light leeches to repair the growths. " +
				"Protect from Melee to not take damage from them.", true);

		returnThroughGreenPortal = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER_49415,
			new WorldPoint(1862, 6410, 0), "Return back through the green portal to the south.");

		enterBoatRoom3 = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49418, new WorldPoint(1899, 6456, 0), "Enter the boat room in the north-east.");
		getTinderBoxRoom3 = new ObjectStep(getQuestHelper(), ObjectID.CRATE_49425, new WorldPoint(1915, 6460, 0),
			"Search the north-east crate for a tinderbox.");
		getGunpowderRoom3 = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49427, new WorldPoint(1912, 6446, 0),
			"Search the skeleton in the south-east corner for gunpowder.");
		getSlimyKeyRoom3 = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49426, new WorldPoint(1902, 6459, 0),
			"Search the skeleton in the north-west for a slimy key.");
		getDampTablet2 = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49420, new WorldPoint(1909, 6449, 0),
			"Search the chest to the south for a tablet.");
		((ObjectStep) getDampTablet2).addAlternateObjects(ObjectID.CHEST_49422);
		readDampTablet2 = new DetailedQuestStep(getQuestHelper(), "Read the damp tablet.", dampTablet2.highlighted());
		burnBoat3 = new ObjectStep(getQuestHelper(), ObjectID.SHIPWRECK, new WorldPoint(1914, 6455, 0), "Burn the shipwreck.");

		enterTreeRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49412, new WorldPoint(1902, 6411, 0),
			"Enter the room with a tree in the south of the area.");

		// 12401 0->1, inside the spawn room
		killTreeMonsters = new NpcStep(getQuestHelper(), NpcID.SCARRED_HELLHOUND, new WorldPoint(1897, 6407, 0),
			"Kill all creatures which come through the portals.", true);
		((NpcStep) killTreeMonsters).addAlternateNpcs(NpcID.SCARRED_GREATER_DEMON);

		enterGreenTeleporter3 = new ObjectStep(getQuestHelper(), ObjectID.NEURAL_TELEPORTER_49416, new WorldPoint(1892, 6457, 0),
			"Enter the green teleporter in the north of the area.");
		enterLightLeechRoom = new ObjectStep(getQuestHelper(), ObjectID.PASSAGE_49411, new WorldPoint(1862, 6421, 0),
			"Enter the light leech room.");
		// Repaired all, 15210 = 4
		repairCrimsonVeins = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49537,
			"Kill crimson sanguisphera for crimson fibre to repair all 3 of the crimson veins.", true);
		repairRadiantVeins = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49536,
			"Kill radiant sanguisphera for radiant fibre to repair all 3 of the radiant veins.", true);

		getLure = new NpcStep(getQuestHelper(), NpcID.LIGHT_LEECH, "Light leech", new WorldPoint(1868, 6430, 0),
			"Get a lure from a light lure.", true);
		repairGrowthsRoom3.addSubSteps(getLure);

		killRadiant = new NpcStep(getQuestHelper(), NpcID.RADIANT_SANGUISPHERA, new WorldPoint(1868, 6430, 0),
			"Kill radiant sanguisphera with Protect from Magic on for a radiant fibre.", true, protectFromMagic, radiantFibre);
		killCrimson = new NpcStep(getQuestHelper(), NpcID.CRIMSON_SANGUISPHERA, new WorldPoint(1868, 6430, 0),
			"Kill crimson sanguisphera with Protect from Magic on for a radiant fibre.", true, protectFromMagic, crimsonFibre);

		repairCrimsonVeins.addSubSteps(killCrimson);
		repairRadiantVeins.addSubSteps(killRadiant);

		talkToPerstenAfterRooms = new NpcStep(getQuestHelper(), NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "Talk to Wizard Persten.");

		boardBoatToLeviathan = new ObjectStep(getQuestHelper(), ObjectID.ROWBOAT_49212, new WorldPoint(2065, 6436, 0), "Board the boat to the Leviathan area.");
		killLeviathan = new NpcStep(getQuestHelper(), NpcID.THE_LEVIATHAN, "");
		((NpcStep) killLeviathan).addAlternateNpcs(NpcID.THE_LEVIATHAN_12215, NpcID.THE_LEVIATHAN_12219, NpcID.THE_LEVIATHAN_12221);
		goKillLeviathan = new ConditionalStep(getQuestHelper(), goToAbyss,
			"Consider re-gearing, then board the boat to fight the Leviathan. Read the sidebar for more details.",
			rangedCombatGear, ancientMagicksActive, shadowBurstRunes, food, prayerPotions);
		goKillLeviathan.addStep(inLeviathanArea, killLeviathan);
		goKillLeviathan.addStep(inTentArea, boardBoatToLeviathan);
		goKillLeviathan.addStep(inDemonArea, hopOverSteppingStone);

		killLeviathanSidebar = new DetailedQuestStep(getQuestHelper(), "Consider re-gearing, then board the boat to fight the Leviathan.");
		killLeviathanSidebar.addText("Use ranged combat. Its attacks register as 'hitting' you upon actual contact, so make sure to pray when projectiles are hitting you rather than being created.");
		killLeviathanSidebar.addText("Shadow spells will stun it.");
		killLeviathanSidebar.addText("Blue projectiles are magic attacks, green ranged, and orange melee. Avoid dropping rocks.");
		killLeviathanSidebar.addText("Run away from electricity attacks. When boulders start falling, walk them in a line to create a wall which you can hide behind from the Leviathan, as after 10 rocks you will need to use them to avoid a blast attack.");
		killLeviathanSidebar.addText("At 20% health, the leviathan will become enraged, constantly dropping rocks. You need to locate an abyssal pathfinder and stay near it until you kill the Leviathan.");
		killLeviathanSidebar.addSubSteps(goKillLeviathan);

		createLeviathan(getQuestHelper().getQuestHelperPlugin().getClient(), getQuestHelper(), runeliteObjectManager);

		climbDownFromLeviathan = new ObjectStep(getQuestHelper(), ObjectID.HANDHOLDS_47594, new WorldPoint(2091, 6380, 0),
			"Climb down the handholds in the north-eastern corner of the Leviathan area.");
		hopAcrossFromLeviathan = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49529,
			new WorldPoint(2096, 6382, 0), "Cross the stepping stone.");

		talkToPerstenAtShip = new NpcStep(getQuestHelper(), NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2098, 6374, 0),
			"");

		goToShip = new ConditionalStep(getQuestHelper(), goToAbyss, "Go to the ship north-east of the Leviathan boss area, and talk to Persten there.");
		goToShip.addStep(and(inNELeviathanArea, perstenAtShip), talkToPerstenAtShip);
		goToShip.addStep(inNELeviathanArea, hopAcrossFromLeviathan);
		goToShip.addStep(inLeviathanArea, climbDownFromLeviathan);
		goToShip.addStep(inTentArea, boardBoatToLeviathan);
		goToShip.addStep(inDemonArea, hopOverSteppingStone);

		searchDebris = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49218, new WorldPoint(2099, 6374, 0),
			"Search the debris next to the ship.");

		returnToDesertWithPerseriyasMedallion = new ObjectStep(getQuestHelper(), ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Return to the Vault door north-east of Nardah. Be wary of an assassin coming to kill you! They can run, freeze, and teleblock you.",
			perseriyasMedallion);
		returnToDesertWithPerseriyasMedallion.addTeleport(nardahTeleport);

		usePerseriyasMedallionOnStatue = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49505, new WorldPoint(3942, 9626, 1),
			"Use the medallion on the south-east statue.", perseriyasMedallion.highlighted());
		usePerseriyasMedallionOnStatue.addIcon(ItemID.PERSERIYAS_MEDALLION);

	}

	public List<QuestStep> getStartSteps()
	{
		return QuestUtil.toArrayList(goTalkToCatalyticGuardian, goKillDemons, hopOverSteppingStone, goBoardBoat, goTalkToPersten);
	}

	public List<QuestStep> getRoom1Steps()
	{
		return QuestUtil.toArrayList(enterPassage1,
			enterPathfinderRoom, doPath1, doPath2, doPath3, doPath4, doPath5, doPath6, enterGreenTeleporter1, enterCatalystRoom,
			solveCatalystRoom, enterBlueTeleporter1, enterGrowthRoom, repairGrowths, growthPuzzle, returnThroughBlueNeuralTeleporter,
			enterBoatRoom1, searchSkeletonForKey, getOldTablet, readOldTablet, searchSkeletonForGunpowder, getTinderbox, burnBoat1);
	}

	public List<QuestStep> getRoom2Steps()
	{
		return QuestUtil.toArrayList(talkToPerstenAfterRoom1, enterSouthEastPassage, enterAxonRoom, hitCosmicAxon, hitFireAxon, hitNatureAxon, hitWaterAxon, enterBlueTeleporter2, enterNerveRoom,
			makeMatchingNerves, returnThroughBlueNeuralTeleporter2, enterGreenTeleporter2, enterSummoningRoom, killImps, killLesserDemons, enterBoatRoom2,
			getSlimyKey, getDampTablet, readDampTablet, getTinderboxRoom2, getGunpowderRoom2, burnBoat2);
	}

	public List<QuestStep> getRoom3Steps()
	{
		return QuestUtil.toArrayList(talkToPerstenAfterRoom2, enterMiddlePassage, enterMemoryPuzzle, memoryPuzzleSteps, enterTreeRoom,
			killTreeMonsters, enterGreenTeleporter3, enterLightLeechRoom, repairGrowthsRoom3, repairCrimsonVeins, repairRadiantVeins, returnThroughGreenPortal,
			enterBoatRoom3, getSlimyKeyRoom3, getDampTablet2, readDampTablet2, getTinderBoxRoom3, getGunpowderRoom3, burnBoat3);
	}

	public List<QuestStep> getBattleSteps()
	{
		return QuestUtil.toArrayList(talkToPerstenAfterRooms, killLeviathanSidebar, goToShip, searchDebris,
			returnToDesertWithPerseriyasMedallion, usePerseriyasMedallionOnStatue);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList();
	}
}

