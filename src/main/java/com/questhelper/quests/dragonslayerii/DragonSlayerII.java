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
package com.questhelper.quests.dragonslayerii;

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
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
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
	quest = QuestHelperQuest.DRAGON_SLAYER_II
)
public class DragonSlayerII extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement ardougneTeleport, varrockTeleport, faladorTeleport, morytaniaTeleport, karamjaTeleport, rellekkaTeleport;

	//Items Required
	ItemRequirement pickaxe, axe, oakPlank8, swampPaste10, nails12OrMore, hammer, machete, saw, catspeakAmulet, ghostspeakOrMory2,
		goutweed, dragonstone, moltenGlass2, glassblowingPipe, chisel, spade, astralRune, sealOfPassage, tinderbox,
		pestleAndMortarHighlighted, runesForFireWaveOrSurge3, fireRune7, fireRune21, bloodRune1, bloodRune3,
		airRune5, airRune15, wrathRune1, wrathRune3, fireRune10, fireRune30, airRune7, airRune21, fireWaveRunes,
		fireWave3Runes, fireSurgeRunes, fireSurge3Runes, runesForFireWaveOrSurge, combatGear, pickaxeHighlighted,
		aivasDiary, dreamVial, dreamVialWater, dreamVialWithGoutweed, dreamPotion, astralRuneHighlighted, astralRuneShards,
		groundAstralRune, hammerHighlighted, dreamPotionHighlighted, food, lightSource, rangedCombatGear, dragonfireProtection,
		venomProtection, salveE, kourendKeyPiece, varrocKeyPiece, karamjaKeyPiece, fremennikKeyPiece, varrockCensusRecords,
		inertLocator, glassblowingPipeHighlighted, dragonstoneHighlighted, locator, ancientKey, dragonKey, antifireShield;

	ItemRequirement map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11, map12, map13, map14, map15, map16, map17,
		map18, map19, map20, map21, map22, map23, map24;

	Requirement inCrandorUnderground, inElvargArea, inKaramjaVolcano, inMuralRoom, inHouseGroundFloor, inHouseFirstFloor, inLithkrenGroundFloor, inLithkrenFirstFloor, inLithkrenUnderground,
		inLithkrenGroundFloorRoom, inDream, litBrazier, hasTheKourendKeyPiece, hasTheVarrockKeyPiece, hasTheFremennikKeyPiece,
		hasTheKaramjaKeyPiece, inDeepLithkren, recruitedBrundt, recruitedLathas, recruitedAmik, inArdougneCastle, inFaladorF1, inFaladorF2, onBoat, inBattle;

	Requirement hadChest1MapPieces, hadChest2MapPieces, hadFungiMapPieces, hadBriarMapPieces, hadMushtreeMapPieces, hadMap1, hadMap2, hadMap3, hadMap4, hadMap5, hadMap6, hadMap7, hadMap8, hadMap9, hadMap10, hadMap11, hadMap12, hadMap13, hadMap14,
		hadMap15, hadMap16, hadMap17, hadMap18, hadMap19, hadMap20, hadMap21, hadMap22, hadMap23, hadMap24, inMapPuzzle, onUngael, inUngaelUnderground, inUngaelKeyRoom,
		inKharaziMaze, openedMithrilDoor, inMithDragonEntranceArea, inMithDragonGroundArea, inMithDragonUpperArea, inMithDragonOrbRoom, inMithDragonFurnaceArea, litFurnace;

	Zone crandorUnderground, elvargArea, karamjaVolcano, muralRoom, houseGroundFloor, houseFirstFloor, lithkrenGroundFloor, lithkrenFirstFloor, lithkrenUnderground, lithkrenGroundFloorRoom,
		dream, cryptF2, cryptF1, cryptF0, ungael, ungaelUnderground, ungaelKeyRoom, kharaziMaze, mithDragonEntranceArea, mithDragonGroundArea, mithDragonUpperArea,
		mithDragonOrbRoom, mithDragonFurnaceArea, deepLithkren, ardougneCastle, faladorF1, faladorF2, boat, battle;

	DetailedQuestStep talkToAlec, talkToDallas, enterVolcano, enterCrandorWall, talkToDallasOnCrandor, usePickaxeOnBlockage, enterBlockage, investigateMural, killSpawn,
		investigateMuralAgain, talkToDallasAfterMural;

	QuestStep enterHouseOnTheHill, enterHouseBasement, talkToDallasInHouse, searchNorthChest, goUpstairsForMap, searchStoneChestNorth, leaveHouseForMap, searchFungi, searchBriar,
		searchMushtree, enterHouseWithMapPieces, goDownWithMapPieces, talkToDallasWithMapPieces, startMapPuzzle, solveMap, talkToDallasAfterSolvingMap, talkToJardricInMuseumCamp,
		buildRowBoat;

	DetailedQuestStep talkToDallasAfterBoatRepair, boardBoat, climbCourtyardStairs, climbDownLithkrenTrapdoor, climbDownLithkrenNorthStairs, talkToDallasInLithkren,
		searchSkeleton, readDiary, talkToDallasAfterDiary;

	DetailedQuestStep talkToBob, talkToSphinx, talkToOneiromancer, crushAstralRune, grindAstralShards, fillDreamVial, addGoutweed, addGroundAstral, lightBrazier, usePotionOnFlame,
		talkToBobInDream, killRobertTheStrong, talkToBobAfterRobertFight, talkToBobToEnterDreamAgain;

	ConditionalStep gettingTheKaramjaKey, gettingTheKourendKey, gettingTheVarrockKey, gettingTheFremennikKey;

	DetailedQuestStep enterKharaziMaze, getToCentreOfMaze;

	DetailedQuestStep talkToReldo, searchBookcase, talkToReldoAgain, talkToReldoAgainNoBook, talkToSarah, talkToAva, usePipeOnDragonstone, talkToAvaAgain,
		talkToAvaAgainNoOrb, useLocatorInSwamp;
	Requirement talkedToReldo, foundCensus, talkedToReldoAgain, givenReldoBook, talkedToSarah, talkedToAva, givenAvaOrb, talkedToAvaAgain;

	DetailedQuestStep talkToBrundt, talkToTorfinn, killVorkath, killVorkathSidebar, enterVorkathCave,
		pullLeverInVorkathCave, enterEastVorkathRoom, searchStoneChestForVorkathKey;
	Requirement talkedToBrundt, defeatedVorkath, pulledLever;

	QuestStep talkToAmelia, enterCrypt, goDownInCryptF2ToF1, goDownInCryptF1ToF0, searchTombInCrypt, solveCryptPuzzle, searchTombForCryptKey;
	Requirement talkedToAmelia, inCryptF0, inCryptF1, inCryptF2, inspectedTomb, openedTomb;

	DetailedQuestStep enterAncientCavern, goDownToBrutalGreenDragons, goUpToMithrilDragons, openMithrilDoor, castFireOnHead, leaveMithrilRoom,
		goDownFromMithDragons, climbUpSouthWestStairsAncientCavern, forgeDragonKey, useKeyOnGrandioseDoors, talkToDallasAfterDoors, talkToBobAfterRelease;

	DetailedQuestStep talkToRoald, goUpToAmik1, goUpToAmik2, talkToAmik, goUpToLathasOrThoros, talkToLathasOrThoros, talkToBrundtAboutThreat, enterVarrockDiningRoom, talkToBobAfterDiningRoom;

	DetailedQuestStep takeBoatToUngael, keepShipAfloat, getToMainShip, kill2Blue2Green, killBlackSteelBrutalRedDragon
	, killMithAddyAndRuneDragons, killGalvekSidebar, killGalvek, talkToAlecToFinish;

	ConditionalStep goEnterMithDoorFirstTime, goEnterMithDoorSecondTime, goSmithKey, goOpenDoorWithKey, openDoorWithoutKey, goTalkToBobAfterRelease;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToAlec);
		steps.put(5, talkToDallas);

		ConditionalStep goTalkToDallasCrandor = new ConditionalStep(this, enterVolcano);
		goTalkToDallasCrandor.addStep(inCrandorUnderground, talkToDallasOnCrandor);
		goTalkToDallasCrandor.addStep(inKaramjaVolcano, enterCrandorWall);
		steps.put(10, goTalkToDallasCrandor);

		ConditionalStep goMineWall = new ConditionalStep(this, enterVolcano);
		goMineWall.addStep(inCrandorUnderground, usePickaxeOnBlockage);
		goMineWall.addStep(inKaramjaVolcano, enterCrandorWall);
		steps.put(15, goMineWall);
		steps.put(16, goMineWall);

		ConditionalStep goInspectMural = new ConditionalStep(this, enterVolcano);
		goInspectMural.addStep(inMuralRoom, investigateMural);
		goInspectMural.addStep(inCrandorUnderground, enterBlockage);
		goInspectMural.addStep(inKaramjaVolcano, enterCrandorWall);
		steps.put(17, goInspectMural);
		steps.put(18, goInspectMural);
		steps.put(19, goInspectMural);
		steps.put(20, goInspectMural);
		steps.put(21, goInspectMural);

		ConditionalStep goKillSpawn = new ConditionalStep(this, enterVolcano);
		goKillSpawn.addStep(inMuralRoom, killSpawn);
		goKillSpawn.addStep(inCrandorUnderground, enterBlockage);
		goKillSpawn.addStep(inKaramjaVolcano, enterCrandorWall);
		steps.put(22, goKillSpawn);
		steps.put(23, goKillSpawn);
		steps.put(24, goKillSpawn);

		ConditionalStep goInvestigateMuralAgain = new ConditionalStep(this, enterVolcano);
		goInvestigateMuralAgain.addStep(inMuralRoom, investigateMuralAgain);
		goInvestigateMuralAgain.addStep(inCrandorUnderground, enterBlockage);
		goInvestigateMuralAgain.addStep(inKaramjaVolcano, enterCrandorWall);
		steps.put(25, goInvestigateMuralAgain);

		ConditionalStep goTalkToDallasAfterMural = new ConditionalStep(this, enterVolcano);
		goTalkToDallasAfterMural.addStep(inMuralRoom, talkToDallasAfterMural);
		goTalkToDallasAfterMural.addStep(inCrandorUnderground, enterBlockage);
		goTalkToDallasAfterMural.addStep(inKaramjaVolcano, enterCrandorWall);
		steps.put(30, goTalkToDallasAfterMural);

		ConditionalStep goTalkToDallasOnFossilIsland = new ConditionalStep(this, enterHouseOnTheHill);
		goTalkToDallasOnFossilIsland.addStep(inHouseGroundFloor, talkToDallasInHouse);
		goTalkToDallasOnFossilIsland.addStep(inHouseFirstFloor, enterHouseBasement);
		steps.put(35, goTalkToDallasOnFossilIsland);

		ConditionalStep getMapPieces = new ConditionalStep(this, enterHouseOnTheHill);
		getMapPieces.addStep(new Conditions(inHouseGroundFloor, hadChest1MapPieces, hadChest2MapPieces, hadFungiMapPieces, hadBriarMapPieces, hadMushtreeMapPieces), talkToDallasWithMapPieces);
		getMapPieces.addStep(new Conditions(inHouseFirstFloor, hadChest1MapPieces, hadChest2MapPieces, hadFungiMapPieces, hadBriarMapPieces, hadMushtreeMapPieces), goDownWithMapPieces);
		getMapPieces.addStep(new Conditions(hadChest1MapPieces, hadChest2MapPieces, hadFungiMapPieces, hadBriarMapPieces, hadMushtreeMapPieces), enterHouseWithMapPieces);

		getMapPieces.addStep(new Conditions(inHouseFirstFloor, hadChest1MapPieces, hadChest2MapPieces), leaveHouseForMap);
		getMapPieces.addStep(new Conditions(inHouseGroundFloor, hadChest1MapPieces, hadChest2MapPieces), goUpstairsForMap);

		getMapPieces.addStep(new Conditions(hadChest1MapPieces, hadChest2MapPieces, hadFungiMapPieces, hadBriarMapPieces), searchMushtree);
		getMapPieces.addStep(new Conditions(hadChest1MapPieces, hadChest2MapPieces, hadFungiMapPieces), searchBriar);
		getMapPieces.addStep(new Conditions(hadChest1MapPieces, hadChest2MapPieces), searchFungi);
		getMapPieces.addStep(new Conditions(inHouseFirstFloor, hadChest1MapPieces), searchStoneChestNorth);
		getMapPieces.addStep(new Conditions(inHouseGroundFloor, hadChest1MapPieces), goUpstairsForMap);
		getMapPieces.addStep(inHouseGroundFloor, searchNorthChest);
		getMapPieces.addStep(inHouseFirstFloor, enterHouseBasement);
		steps.put(40, getMapPieces);

		ConditionalStep talkToDallasAfterMap = new ConditionalStep(this, enterHouseOnTheHill);
		talkToDallasAfterMap.addStep(inHouseGroundFloor, talkToDallasInHouse);
		talkToDallasAfterMap.addStep(inHouseFirstFloor, enterHouseBasement);
		steps.put(45, talkToDallasAfterMap);

		ConditionalStep goSolveMapPuzzle = new ConditionalStep(this, enterHouseOnTheHill);
		goSolveMapPuzzle.addStep(inMapPuzzle, solveMap);
		goSolveMapPuzzle.addStep(inHouseGroundFloor, startMapPuzzle);
		goSolveMapPuzzle.addStep(inHouseFirstFloor, enterHouseBasement);
		steps.put(45, goSolveMapPuzzle);

		ConditionalStep goTalkToDallasAfterSolvingMap = new ConditionalStep(this, enterHouseOnTheHill);
		goTalkToDallasAfterSolvingMap.addStep(inHouseGroundFloor, talkToDallasAfterSolvingMap);
		goTalkToDallasAfterSolvingMap.addStep(inHouseFirstFloor, enterHouseBasement);
		steps.put(50, goTalkToDallasAfterSolvingMap);

		steps.put(55, talkToJardricInMuseumCamp);

		steps.put(60, buildRowBoat);
		steps.put(61, buildRowBoat);
		steps.put(62, buildRowBoat);
		steps.put(63, buildRowBoat);
		steps.put(64, buildRowBoat);

		steps.put(65, talkToDallasAfterBoatRepair);

		ConditionalStep goDownToLithkrenBasement = new ConditionalStep(this, boardBoat);
		goDownToLithkrenBasement.addStep(inLithkrenUnderground, talkToDallasInLithkren);
		goDownToLithkrenBasement.addStep(inLithkrenGroundFloorRoom, climbDownLithkrenNorthStairs);
		goDownToLithkrenBasement.addStep(inLithkrenFirstFloor, climbDownLithkrenTrapdoor);
		goDownToLithkrenBasement.addStep(inLithkrenGroundFloor, climbCourtyardStairs);

		steps.put(70, goDownToLithkrenBasement);
		steps.put(71, goDownToLithkrenBasement);
		steps.put(72, goDownToLithkrenBasement);
		steps.put(73, goDownToLithkrenBasement);
		steps.put(74, goDownToLithkrenBasement);
		steps.put(75, goDownToLithkrenBasement);

		ConditionalStep goReadDiary = new ConditionalStep(this, boardBoat);
		goReadDiary.addStep(aivasDiary, readDiary);
		goReadDiary.addStep(inLithkrenUnderground, searchSkeleton);
		goReadDiary.addStep(inLithkrenGroundFloorRoom, climbDownLithkrenNorthStairs);
		goReadDiary.addStep(inLithkrenFirstFloor, climbDownLithkrenTrapdoor);
		goReadDiary.addStep(inLithkrenGroundFloor, climbCourtyardStairs);
		steps.put(80, goReadDiary);
		steps.put(81, goReadDiary);
		steps.put(82, goReadDiary);
		steps.put(83, goReadDiary);
		steps.put(84, goReadDiary);

		ConditionalStep goTalkToDallasAfterDiary = new ConditionalStep(this, boardBoat);
		goTalkToDallasAfterDiary.addStep(inLithkrenUnderground, talkToDallasAfterDiary);
		goTalkToDallasAfterDiary.addStep(inLithkrenGroundFloorRoom, climbDownLithkrenNorthStairs);
		goTalkToDallasAfterDiary.addStep(inLithkrenFirstFloor, climbDownLithkrenTrapdoor);
		goTalkToDallasAfterDiary.addStep(inLithkrenGroundFloor, climbCourtyardStairs);
		steps.put(85, goTalkToDallasAfterDiary);

		steps.put(90, talkToBob);

		steps.put(95, talkToSphinx);
		steps.put(96, talkToSphinx);

		steps.put(100, talkToOneiromancer);

		ConditionalStep goEnterDream = new ConditionalStep(this, fillDreamVial);
		goEnterDream.addStep(new Conditions(dreamPotion, litBrazier), usePotionOnFlame);
		goEnterDream.addStep(dreamPotion, lightBrazier);
		goEnterDream.addStep(new Conditions(dreamVialWithGoutweed, groundAstralRune), addGroundAstral);
		goEnterDream.addStep(new Conditions(dreamVialWithGoutweed, astralRuneShards), grindAstralShards);
		goEnterDream.addStep(dreamVialWithGoutweed, crushAstralRune);
		goEnterDream.addStep(dreamVialWater, addGoutweed);
		steps.put(105, goEnterDream);

		ConditionalStep goTalkToBobInDream = new ConditionalStep(this, talkToBobToEnterDreamAgain);
		goTalkToBobInDream.addStep(inDream, talkToBobInDream);
		steps.put(110, goTalkToBobInDream);

		ConditionalStep goDefeatRobert = new ConditionalStep(this, talkToBobToEnterDreamAgain);
		goDefeatRobert.addStep(inDream, killRobertTheStrong);
		steps.put(111, goDefeatRobert);

		steps.put(115, talkToBobAfterRobertFight);
		steps.put(120, talkToBobAfterRobertFight);

		gettingTheFremennikKey = new ConditionalStep(this, talkToBrundt);
		gettingTheFremennikKey.addStep(new Conditions(inUngaelKeyRoom), searchStoneChestForVorkathKey);
		gettingTheFremennikKey.addStep(new Conditions(inUngaelUnderground, pulledLever), enterEastVorkathRoom);
		gettingTheFremennikKey.addStep(new Conditions(inUngaelUnderground), pullLeverInVorkathCave);
		gettingTheFremennikKey.addStep(new Conditions(onUngael, defeatedVorkath), enterVorkathCave);
		gettingTheFremennikKey.addStep(onUngael, killVorkath);
		gettingTheFremennikKey.addStep(talkedToBrundt, talkToTorfinn);
		gettingTheFremennikKey.setLockingCondition(hasTheFremennikKeyPiece);

		gettingTheKaramjaKey = new ConditionalStep(this, enterKharaziMaze);
		gettingTheKaramjaKey.addStep(inKharaziMaze, getToCentreOfMaze);
		gettingTheKaramjaKey.setLockingCondition(hasTheKaramjaKeyPiece);

		gettingTheKourendKey = new ConditionalStep(this, talkToAmelia);
		gettingTheKourendKey.addStep(new Conditions(openedTomb, inCryptF0), searchTombForCryptKey);
		gettingTheKourendKey.addStep(new Conditions(inspectedTomb, inCryptF0), solveCryptPuzzle);
		gettingTheKourendKey.addStep(new Conditions(talkedToAmelia, inCryptF0), searchTombInCrypt);
		gettingTheKourendKey.addStep(new Conditions(talkedToAmelia, inCryptF1), goDownInCryptF1ToF0);
		gettingTheKourendKey.addStep(new Conditions(talkedToAmelia, inCryptF2), goDownInCryptF2ToF1);
		gettingTheKourendKey.addStep(new Conditions(talkedToAmelia), enterCrypt);
		gettingTheKourendKey.setLockingCondition(hasTheKourendKeyPiece);

		gettingTheVarrockKey = new ConditionalStep(this, talkToReldo);
		gettingTheVarrockKey.addStep(new Conditions(talkedToAvaAgain), useLocatorInSwamp);
		gettingTheVarrockKey.addStep(new Conditions(givenAvaOrb), talkToAvaAgainNoOrb);
		gettingTheVarrockKey.addStep(new Conditions(talkedToAva, inertLocator), talkToAvaAgain);
		gettingTheVarrockKey.addStep(talkedToAva, usePipeOnDragonstone);
		gettingTheVarrockKey.addStep(talkedToSarah, talkToAva);
		gettingTheVarrockKey.addStep(talkedToReldoAgain, talkToSarah);
		gettingTheVarrockKey.addStep(givenReldoBook, talkToReldoAgainNoBook);
		gettingTheVarrockKey.addStep(foundCensus, talkToReldoAgain);
		gettingTheVarrockKey.addStep(talkedToReldo, searchBookcase);
		gettingTheVarrockKey.setLockingCondition(hasTheVarrockKeyPiece);

		ConditionalStep goAndForgeTheKey = new ConditionalStep(this, gettingTheKourendKey);
		goAndForgeTheKey.addStep(new Conditions(hasTheKourendKeyPiece, hasTheVarrockKeyPiece, hasTheFremennikKeyPiece), gettingTheKaramjaKey);
		goAndForgeTheKey.addStep(new Conditions(hasTheKourendKeyPiece, hasTheVarrockKeyPiece), gettingTheFremennikKey);
		goAndForgeTheKey.addStep(hasTheKourendKeyPiece, gettingTheVarrockKey);

		steps.put(125, goAndForgeTheKey);
		steps.put(126, goAndForgeTheKey);
		steps.put(127, goAndForgeTheKey);
		steps.put(128, goAndForgeTheKey);
		steps.put(129, goAndForgeTheKey);

		ConditionalStep goForgeKey = new ConditionalStep(this, goEnterMithDoorFirstTime);
		goForgeKey.addStep(litFurnace, goSmithKey);
		goForgeKey.addStep(openedMithrilDoor, goEnterMithDoorSecondTime);
		steps.put(130, goForgeKey);

		steps.put(135, goOpenDoorWithKey);

		steps.put(140, openDoorWithoutKey);
		steps.put(141, openDoorWithoutKey);
		steps.put(142, openDoorWithoutKey);
		steps.put(143, openDoorWithoutKey);
		steps.put(144, openDoorWithoutKey);
		steps.put(145, openDoorWithoutKey);

		steps.put(150, goTalkToBobAfterRelease);

		steps.put(155, talkToRoald);

		ConditionalStep recruitTheTeam = new ConditionalStep(this, talkToBrundtAboutThreat);
		recruitTheTeam.addStep(new Conditions(recruitedBrundt, recruitedAmik, inArdougneCastle), talkToLathasOrThoros);
		recruitTheTeam.addStep(new Conditions(recruitedBrundt, recruitedAmik), goUpToLathasOrThoros);
		recruitTheTeam.addStep(new Conditions(recruitedBrundt, inFaladorF2), talkToAmik);
		recruitTheTeam.addStep(new Conditions(recruitedBrundt, inFaladorF1), goUpToAmik2);
		recruitTheTeam.addStep(recruitedBrundt, goUpToAmik1);
		steps.put(160, recruitTheTeam);

		steps.put(161, enterVarrockDiningRoom);
		steps.put(165, talkToBobAfterDiningRoom);
		steps.put(170, takeBoatToUngael);

		ConditionalStep goRepairBoat = new ConditionalStep(this, takeBoatToUngael);
		goRepairBoat.addStep(onBoat, keepShipAfloat);
		steps.put(175, goRepairBoat);

		ConditionalStep getToMainShipSteps = new ConditionalStep(this, takeBoatToUngael);
		getToMainShipSteps.addStep(inBattle, getToMainShip);
		steps.put(180, getToMainShipSteps);
		steps.put(181, getToMainShipSteps);
		steps.put(182, getToMainShipSteps);
		steps.put(183, getToMainShipSteps);
		steps.put(184, getToMainShipSteps);

		ConditionalStep goFightBlueAndGreenDragons = new ConditionalStep(this, takeBoatToUngael);
		goFightBlueAndGreenDragons.addStep(inBattle, kill2Blue2Green);
		steps.put(185, goFightBlueAndGreenDragons);
		steps.put(186, goFightBlueAndGreenDragons);

		ConditionalStep goFightBlackSteelBrutalRedDragons = new ConditionalStep(this, takeBoatToUngael);
		goFightBlackSteelBrutalRedDragons.addStep(inBattle, killBlackSteelBrutalRedDragon);
		steps.put(190, goFightBlackSteelBrutalRedDragons);
		steps.put(191, goFightBlackSteelBrutalRedDragons);

		ConditionalStep goFightMetalDragons = new ConditionalStep(this, takeBoatToUngael);
		goFightMetalDragons.addStep(inBattle, killMithAddyAndRuneDragons);
		steps.put(195, goFightMetalDragons);
		steps.put(196, goFightMetalDragons);

		ConditionalStep goFightGalvek = new ConditionalStep(this, takeBoatToUngael);
		goFightGalvek.addStep(inBattle, killGalvek);
		steps.put(200, goFightGalvek);
		steps.put(201, goFightGalvek);

		steps.put(205, talkToAlecToFinish);
		steps.put(210, talkToAlecToFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		pickaxeHighlighted = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		pickaxeHighlighted.setHighlightInInventory(true);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		oakPlank8 = new ItemRequirement("Oak planks", ItemID.OAK_PLANK, 8);
		swampPaste10 = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE, 10);
		nails12OrMore = new ItemRequirement("Nails, bring more in case some break", ItemCollections.NAILS, 12);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		hammerHighlighted = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		hammerHighlighted.setHighlightInInventory(true);
		machete = new ItemRequirement("Any machete", ItemCollections.MACHETE);
		saw = new ItemRequirement("Saw", ItemCollections.SAW);
		catspeakAmulet = new ItemRequirement("Catspeak amulet (e)", ItemID.CATSPEAK_AMULETE, 1, true);
		catspeakAmulet.setTooltip("You can get another basic amulet from the Sphinx (bring a cat), and then get it enchanted by Hild in Burthorpe for 5 death runes");
		ghostspeakOrMory2 = new ItemRequirement("Ghostspeak amulet", ItemCollections.GHOSTSPEAK, 1, true);
		ghostspeakOrMory2.setTooltip("Morytania Legs 2 and above are also valid.");
		goutweed = new ItemRequirement("Goutweed", ItemID.GOUTWEED);
		goutweed.setHighlightInInventory(true);
		goutweed.setTooltip("You can get this from the Troll Stronghold Kitchen Storeroom");
		dragonstone = new ItemRequirement("Dragonstone", ItemID.DRAGONSTONE);
		moltenGlass2 = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS, 2);
		glassblowingPipe = new ItemRequirement("Glassblowing pipe", ItemID.GLASSBLOWING_PIPE);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE);
		sealOfPassage = new ItemRequirement("Seal of passage", ItemID.SEAL_OF_PASSAGE);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		pestleAndMortarHighlighted = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted.setHighlightInInventory(true);
		bloodRune1 = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE);
		bloodRune3 = new ItemRequirement("Blood rune", ItemID.BLOOD_RUNE, 3);
		fireRune7 = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE, 7);
		fireRune21 = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE, 21);
		airRune5 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 5);
		airRune15 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 15);
		fireWaveRunes = new ItemRequirements("Fire wave runes", bloodRune1, fireRune7, airRune5);
		fireWave3Runes = new ItemRequirements("3 casts of Fire Wave", bloodRune3, fireRune21, airRune15);

		wrathRune1 = new ItemRequirement("Wrath rune", ItemID.WRATH_RUNE);
		wrathRune3 = new ItemRequirement("Wrath rune", ItemID.WRATH_RUNE, 3);
		fireRune10 = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE, 10);
		fireRune30 = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE, 30);
		airRune7 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 7);
		airRune21 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 21);
		fireSurgeRunes = new ItemRequirements("Fire Surge runes", wrathRune1, fireRune10, airRune7);
		fireSurge3Runes = new ItemRequirements("3 casts of Fire Surge", wrathRune3, fireRune30, airRune21);

		runesForFireWaveOrSurge = new ItemRequirements(LogicType.OR, "Fire Wave or Fire Surge runes", fireWaveRunes, fireSurgeRunes);
		runesForFireWaveOrSurge3 = new ItemRequirements(LogicType.OR, "3 casts of Fire Wave or Fire Surge", fireWave3Runes, fireSurge3Runes);

		combatGear = new ItemRequirement("Combat gear, food and potions", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		lightSource = new ItemRequirement("A light source", ItemCollections.LIGHT_SOURCES);

		rangedCombatGear = new ItemRequirement("Ranged combat gear", -1, -1);
		rangedCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());

		dragonfireProtection = new ItemRequirement("Protection from dragonfire", ItemCollections.ANTIFIRE_SHIELDS);
		venomProtection = new ItemRequirement("Anti venom", ItemCollections.ANTIVENOMS);
		salveE = new ItemRequirement("Salve amulet", ItemCollections.SALVE_AMULET);

		// Chest 1 map pieces
		map1 = new ItemRequirement("Map piece", ItemID.MAP_PIECE);
		map2 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22010);
		map3 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22011);
		map4 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22012);
		map5 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22013);

		// Chest 2 map pieces
		map6 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22014);
		map7 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22015);
		map8 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22016);

		// Fungi map pieces
		map9 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22017);
		map10 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22018);
		map11 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22019);
		map12 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22020);

		// Briar map pieces
		map13 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22021);
		map14 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22022);
		map15 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22023);
		map16 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22024);
		map17 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22025);
		map18 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22026);
		map19 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22027);

		// Mushtree map pieces
		map20 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22028);
		map21 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22029);
		map22 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22030);
		map23 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22031);
		map24 = new ItemRequirement("Map piece", ItemID.MAP_PIECE_22032);

		aivasDiary = new ItemRequirement("Aivas' diary", ItemID.AIVAS_DIARY);
		aivasDiary.setHighlightInInventory(true);

		dreamVial = new ItemRequirement("Dream vial (empty)", ItemID.DREAM_VIAL_EMPTY);
		dreamVial.setHighlightInInventory(true);
		astralRuneHighlighted = new ItemRequirement("Astral rune", ItemID.ASTRAL_RUNE);
		astralRuneHighlighted.setHighlightInInventory(true);
		astralRuneShards = new ItemRequirement("Astral rune shards", ItemID.ASTRAL_RUNE_SHARDS);
		astralRuneShards.setHighlightInInventory(true);
		groundAstralRune = new ItemRequirement("Ground astral rune", ItemID.GROUND_ASTRAL_RUNE);
		groundAstralRune.setHighlightInInventory(true);
		dreamVialWater = new ItemRequirement("Dream vial (water)", ItemID.DREAM_VIAL_WATER);
		dreamVialWater.setHighlightInInventory(true);
		dreamVialWithGoutweed = new ItemRequirement("Dream vial (herb)", ItemID.DREAM_VIAL_HERB);
		dreamVialWithGoutweed.setHighlightInInventory(true);
		dreamPotion = new ItemRequirement("Dream potion", ItemID.DREAM_POTION);

		dreamPotionHighlighted = new ItemRequirement("Dream potion", ItemID.DREAM_POTION);
		dreamPotionHighlighted.setHighlightInInventory(true);

		varrockCensusRecords = new ItemRequirement("Varrock census records", ItemID.VARROCK_CENSUS_RECORDS);

		kourendKeyPiece = new ItemRequirement("Dragon key piece (Kourend)", ItemID.DRAGON_KEY_PIECE_22089);
		kourendKeyPiece.setTooltip("You can get another from the tomb in the Shayzien Crypt");
		varrocKeyPiece = new ItemRequirement("Dragon key piece (Varrock)", ItemID.DRAGON_KEY_PIECE_22090);
		varrocKeyPiece.setTooltip("You can get another by digging in Mort Myre");
		fremennikKeyPiece = new ItemRequirement("Dragon key piece (Fremmennik)", ItemID.DRAGON_KEY_PIECE);
		fremennikKeyPiece.setTooltip("You can get another from Ungael");
		karamjaKeyPiece = new ItemRequirement("Dragon key piece (Kharazi)", ItemID.DRAGON_KEY_PIECE_22091);
		karamjaKeyPiece.setTooltip("You can get another from the middle of the Kharazi dungeon");

		inertLocator = new ItemRequirement("Inert locator orb", ItemID.INERT_LOCATOR_ORB);
		glassblowingPipeHighlighted = new ItemRequirement("Glassblowing pipe", ItemID.GLASSBLOWING_PIPE);
		glassblowingPipeHighlighted.setHighlightInInventory(true);
		dragonstoneHighlighted = new ItemRequirement("Dragonstone", ItemID.DRAGONSTONE);
		dragonstoneHighlighted.setHighlightInInventory(true);

		locator = new ItemRequirement("Locator orb", ItemID.LOCATOR_ORB);
		locator.setTooltip("You can get another from Ava");

		ancientKey = new ItemRequirement("Ancient key", ItemID.ANCIENT_KEY);
		ancientKey.setTooltip("You can get another from under Ungael");

		dragonKey = new ItemRequirement("Dragon key", ItemID.DRAGON_KEY);
		dragonKey.setTooltip("If you've lost it you'll need to recover the 4 pieces and reforge it");

		antifireShield = new ItemRequirement("Antifire shield", ItemCollections.ANTIFIRE_SHIELDS, 1, true);

		ardougneTeleport = new ItemRequirement("Ardougne teleport", ItemID.ARDOUGNE_TELEPORT);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.FALADOR_TELEPORT);
		morytaniaTeleport = new ItemRequirement("Port Phasmatys teleport", ItemID.ECTOPHIAL);
		karamjaTeleport = new ItemRequirement("Karamja teleport", ItemID.TAI_BWO_WANNAI_TELEPORT);
		rellekkaTeleport = new ItemRequirement("Rellekka teleport", ItemID.RELLEKKA_TELEPORT);
	}

	public void setupZones()
	{
		crandorUnderground = new Zone(new WorldPoint(2821, 9600, 0), new WorldPoint(2872, 9663, 0));
		elvargArea = new Zone(new WorldPoint(2846, 9625, 0), new WorldPoint(2867, 9651, 0));
		karamjaVolcano = new Zone(new WorldPoint(2827, 9547, 0), new WorldPoint(2867, 9599, 0));
		muralRoom = new Zone(new WorldPoint(2862, 9673, 0), new WorldPoint(2872, 9688, 0));
		houseGroundFloor = new Zone(new WorldPoint(3757, 3863, 0), new WorldPoint(3770, 3873, 0));
		houseFirstFloor = new Zone(new WorldPoint(3757, 3863, 1), new WorldPoint(3774, 3883, 1));
		lithkrenGroundFloor = new Zone(new WorldPoint(3520, 3968, 0), new WorldPoint(3601, 4031, 0));
		lithkrenFirstFloor = new Zone(new WorldPoint(3520, 3968, 1), new WorldPoint(3601, 4031, 1));
		lithkrenUnderground = new Zone(new WorldPoint(3536, 10376, 0), new WorldPoint(3562, 10490, 0));
		lithkrenGroundFloorRoom = new Zone(new WorldPoint(3549, 3990, 0), new WorldPoint(3556, 4002, 0));
		dream = new Zone(new WorldPoint(1815, 5196, 2), new WorldPoint(1833, 5232, 2));
		cryptF0 = new Zone(new WorldPoint(1470, 9910, 1), new WorldPoint(1540, 9990, 1));
		cryptF1 = new Zone(new WorldPoint(1470, 9910, 2), new WorldPoint(1540, 9990, 2));
		cryptF2 = new Zone(new WorldPoint(1470, 9910, 3), new WorldPoint(1540, 9990, 3));
		ungael = new Zone(new WorldPoint(2240, 4031, 0), new WorldPoint(2303, 4095, 0));
		ungaelUnderground = new Zone(new WorldPoint(2254, 10454, 0), new WorldPoint(2288, 10487, 0));
		ungaelKeyRoom = new Zone(new WorldPoint(2288, 10463, 0), new WorldPoint(2294, 10467, 0));
		kharaziMaze = new Zone(new WorldPoint(2819, 9200, 0), new WorldPoint(2880, 9294, 0));
		mithDragonOrbRoom = new Zone(new WorldPoint(1545, 4873, 0), new WorldPoint(1567, 4892, 0));
		mithDragonGroundArea = new Zone(new WorldPoint(1731, 5314, 0), new WorldPoint(1794, 5385, 0));
		mithDragonEntranceArea = new Zone(new WorldPoint(1762, 5364, 1), new WorldPoint(1769, 5368, 1));
		mithDragonUpperArea = new Zone(new WorldPoint(1751, 5323, 1), new WorldPoint(1791, 5360, 1));
		mithDragonFurnaceArea = new Zone(new WorldPoint(1735, 5270, 1), new WorldPoint(1761, 5321, 1));
		deepLithkren = new Zone(new WorldPoint(1539, 5059, 0), new WorldPoint(1596, 5107, 0));

		ardougneCastle = new Zone(new WorldPoint(2570, 3283, 1), new WorldPoint(2590, 3310, 1));
		faladorF1 = new Zone(new WorldPoint(2952, 3326, 1), new WorldPoint(3000, 3356, 1));
		faladorF2 = new Zone(new WorldPoint(2952, 3326, 2), new WorldPoint(3000, 3356, 2));
		boat = new Zone(new WorldPoint(1694, 5528, 1), new WorldPoint(1710, 5558, 1));
		battle = new Zone(new WorldPoint(1600, 5600, 0), new WorldPoint(1726, 5758, 3));
	}

	public void setupConditions()
	{
		inCrandorUnderground = new ZoneRequirement(crandorUnderground);
		inElvargArea = new ZoneRequirement(elvargArea);
		inKaramjaVolcano = new ZoneRequirement(karamjaVolcano);
		inMuralRoom = new ZoneRequirement(muralRoom);
		inHouseGroundFloor = new ZoneRequirement(houseGroundFloor);
		inHouseFirstFloor = new ZoneRequirement(houseFirstFloor);
		inLithkrenGroundFloor = new ZoneRequirement(lithkrenGroundFloor);
		inLithkrenFirstFloor = new ZoneRequirement(lithkrenFirstFloor);
		inLithkrenGroundFloorRoom = new ZoneRequirement(lithkrenGroundFloorRoom);
		inLithkrenUnderground = new ZoneRequirement(lithkrenUnderground);
		inDream = new ZoneRequirement(dream);
		inCryptF0 = new ZoneRequirement(cryptF0);
		inCryptF1 = new ZoneRequirement(cryptF1);
		inCryptF2 = new ZoneRequirement(cryptF2);
		onUngael = new ZoneRequirement(ungael);
		inUngaelUnderground = new ZoneRequirement(ungaelUnderground);
		inUngaelKeyRoom = new ZoneRequirement(ungaelKeyRoom);
		inKharaziMaze = new ZoneRequirement(kharaziMaze);
		inMithDragonEntranceArea = new ZoneRequirement(mithDragonEntranceArea);
		inMithDragonGroundArea = new ZoneRequirement(mithDragonGroundArea);
		inMithDragonOrbRoom = new ZoneRequirement(mithDragonOrbRoom);
		inMithDragonUpperArea = new ZoneRequirement(mithDragonUpperArea);
		inMithDragonFurnaceArea = new ZoneRequirement(mithDragonFurnaceArea);
		inDeepLithkren = new ZoneRequirement(deepLithkren);
		inArdougneCastle = new ZoneRequirement(ardougneCastle);
		inFaladorF1 = new ZoneRequirement(faladorF1);
		inFaladorF2 = new ZoneRequirement(faladorF2);
		onBoat = new ZoneRequirement(boat);
		inBattle = new ZoneRequirement(battle);

		hadMap1 = new Conditions(LogicType.OR, map1, new VarbitRequirement(6116, 1));
		hadMap2 = new Conditions(LogicType.OR, map2, new VarbitRequirement(6117, 1));
		hadMap3 = new Conditions(LogicType.OR, map3, new VarbitRequirement(6118, 1));
		hadMap4 = new Conditions(LogicType.OR, map4, new VarbitRequirement(6119, 1));
		hadMap5 = new Conditions(LogicType.OR, map5, new VarbitRequirement(6120, 1));
		hadChest1MapPieces = new Conditions(hadMap1, hadMap2, hadMap3, hadMap4, hadMap5);

		hadMap6 = new Conditions(LogicType.OR, map6, new VarbitRequirement(6121, 1));
		hadMap7 = new Conditions(LogicType.OR, map7, new VarbitRequirement(6122, 1));
		hadMap8 = new Conditions(LogicType.OR, map8, new VarbitRequirement(6123, 1));
		hadChest2MapPieces = new Conditions(hadMap6, hadMap7, hadMap8);

		hadMap9 = new Conditions(LogicType.OR, map9, new VarbitRequirement(6124, 1));
		hadMap10 = new Conditions(LogicType.OR, map10, new VarbitRequirement(6125, 1));
		hadMap11 = new Conditions(LogicType.OR, map11, new VarbitRequirement(6126, 1));
		hadMap12 = new Conditions(LogicType.OR, map12, new VarbitRequirement(6127, 1));
		hadFungiMapPieces = new Conditions(hadMap9, hadMap10, hadMap11, hadMap12);

		hadMap13 = new Conditions(LogicType.OR, map13, new VarbitRequirement(6128, 1));
		hadMap14 = new Conditions(LogicType.OR, map14, new VarbitRequirement(6129, 1));
		hadMap15 = new Conditions(LogicType.OR, map15, new VarbitRequirement(6130, 1));
		hadMap16 = new Conditions(LogicType.OR, map16, new VarbitRequirement(6131, 1));
		hadMap17 = new Conditions(LogicType.OR, map17, new VarbitRequirement(6132, 1));
		hadMap18 = new Conditions(LogicType.OR, map18, new VarbitRequirement(6133, 1));
		hadMap19 = new Conditions(LogicType.OR, map19, new VarbitRequirement(6134, 1));
		hadBriarMapPieces = new Conditions(hadMap13, hadMap14, hadMap15, hadMap16, hadMap17, hadMap18, hadMap19);

		hadMap20 = new Conditions(LogicType.OR, map20, new VarbitRequirement(6135, 1));
		hadMap21 = new Conditions(LogicType.OR, map21, new VarbitRequirement(6136, 1));
		hadMap22 = new Conditions(LogicType.OR, map22, new VarbitRequirement(6137, 1));
		hadMap23 = new Conditions(LogicType.OR, map23, new VarbitRequirement(6138, 1));
		hadMap24 = new Conditions(LogicType.OR, map23, new VarbitRequirement(6138, 1));
		hadMushtreeMapPieces = new Conditions(hadMap20, hadMap21, hadMap22, hadMap23, hadMap24);

		inMapPuzzle = new WidgetModelRequirement(305, 1, 35060);

		litBrazier = new VarbitRequirement(2430, 1);

		hasTheKourendKeyPiece = new VarbitRequirement(6105, 35, Operation.GREATER_EQUAL);
		hasTheKaramjaKeyPiece = new VarbitRequirement(6106, 20, Operation.GREATER_EQUAL);
		hasTheVarrockKeyPiece = new VarbitRequirement(6107, 55, Operation.GREATER_EQUAL);
		hasTheFremennikKeyPiece = new VarbitRequirement(6108, 35, Operation.GREATER_EQUAL);

		// Kourend key piece
		talkedToAmelia = new VarbitRequirement(6105, 20, Operation.GREATER_EQUAL);
		inspectedTomb = new VarbitRequirement(6105, 25, Operation.GREATER_EQUAL);
		openedTomb = new VarbitRequirement(6105, 30, Operation.GREATER_EQUAL);
		// 6105 35->36 placed key on door

		// Varrock key piece
		talkedToReldo = new VarbitRequirement(6107, 10, Operation.GREATER_EQUAL);
		foundCensus = new VarbitRequirement(6107, 15, Operation.GREATER_EQUAL);
		givenReldoBook = new VarbitRequirement(6107, 20, Operation.GREATER_EQUAL);
		talkedToReldoAgain = new VarbitRequirement(6107, 25, Operation.GREATER_EQUAL);
		talkedToSarah = new VarbitRequirement(6107, 35, Operation.GREATER_EQUAL);
		talkedToAva = new VarbitRequirement(6107, 40, Operation.GREATER_EQUAL);
		givenAvaOrb = new VarbitRequirement(6107, 46, Operation.GREATER_EQUAL);
		talkedToAvaAgain = new VarbitRequirement(6107, 50, Operation.GREATER_EQUAL);

		// 6141, presumbly represents location of treasure
		// 11: 3442, 3421, 0

		// Fremennik key piece
		talkedToBrundt = new VarbitRequirement(6108, 10, Operation.GREATER_EQUAL);
		defeatedVorkath = new VarbitRequirement(6108, 30, Operation.GREATER_EQUAL);
		pulledLever = new ObjectCondition(ObjectID.LEVER_32055, new WorldPoint(2260, 10464, 0));

		openedMithrilDoor = new VarbitRequirement(6108, 40, Operation.GREATER_EQUAL);

		// West dragon lit, 6109 = 1
		// North dragon lit, 6110 = 1
		// East dragon lit, 6111 = 1
		litFurnace = new Conditions(new VarbitRequirement(6109, 1), new VarbitRequirement(6110, 1), new VarbitRequirement(6111, 1));

		recruitedBrundt = new VarbitRequirement(6114, 1);
		recruitedAmik = new VarbitRequirement(6115, 1);
		recruitedLathas = new VarbitRequirement(6113, 1);
	}

	public void setupSteps()
	{
		talkToAlec = new NpcStep(this, NpcID.ALEC_KINCADE, new WorldPoint(2458, 2869, 0), "Talk to Alec Kincade outside the Myths' Guild.");
		talkToAlec.addDialogSteps("How can I gain access to the Guild?", "Yes.");
		talkToDallas = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(2924, 3146, 0), "Talk to Dallas Jones in the Pub on Musa Point.", pickaxe, combatGear);
		enterVolcano = new ObjectStep(this, ObjectID.ROCKS_11441, new WorldPoint(2857, 3169, 0), "Talk to Dallas in Elvarg's Lair.", pickaxe, combatGear);
		enterCrandorWall = new ObjectStep(this, ObjectID.WALL_2606, new WorldPoint(2836, 9600, 0), "Talk to Dallas in Elvarg's Lair.");
		talkToDallasOnCrandor = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(2856, 9645, 0), "Talk to Dallas in Elvarg's Lair.");
		talkToDallasOnCrandor.addSubSteps(enterVolcano, enterCrandorWall);
		usePickaxeOnBlockage = new ObjectStep(this, NullObjectID.NULL_29873, new WorldPoint(2860, 9648, 0), "Use a pickaxe on the blockage in the north east of Elvarg's Lair.", pickaxeHighlighted);
		usePickaxeOnBlockage.addIcon(ItemID.BRONZE_PICKAXE);

		enterBlockage = new ObjectStep(this, NullObjectID.NULL_29873, new WorldPoint(2860, 9648, 0), "Enter the passage in the north east of Elvarg's Lair.", combatGear);
		investigateMural = new ObjectStep(this, ObjectID.ANCIENT_MURAL, new WorldPoint(2867, 9680, 0), "Inspect the mural.");
		killSpawn = new NpcStep(this, NpcID.SPAWN_8056, new WorldPoint(2867, 9676, 0), "Kill the spawn.", combatGear);
		investigateMuralAgain = new ObjectStep(this, ObjectID.ANCIENT_MURAL, new WorldPoint(2867, 9680, 0), "Investigate the mural again.");
		talkToDallasAfterMural = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(2867, 9680, 0), "Talk to Dallas about the mural.");

		// Section 2
		enterHouseOnTheHill = new ObjectStep(this, ObjectID.STAIRS_30681, new WorldPoint(3755, 3869, 0), "Travel to the House on the Hill on Fossil Island. The fastest way here is with the digsite pendant.");
		enterHouseBasement = new ObjectStep(this, ObjectID.TRAPDOOR_30725, new WorldPoint(3768, 3867, 1), "Go down the trapdoor to the basement.");
		talkToDallasInHouse = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(3762, 3868, 0), "Talk to Dallas.");
		talkToDallasInHouse.addDialogStep("Ask about the island.");
		talkToDallasInHouse.addSubSteps(enterHouseBasement);
		searchNorthChest = new ObjectStep(this, NullObjectID.NULL_29911, new WorldPoint(3765, 3873, 0), "Search the stone chest in the north of the room. Whilst finding map pieces you can talk to Dallas to give him your current pieces.");
		searchNorthChest.addDialogStep("Let's talk about the investigation.");
		goUpstairsForMap = new ObjectStep(this, ObjectID.LADDER_30727, new WorldPoint(3768, 3867, 0), "Climb the ladder to go upstairs in the house. Talk to Dallas if you want to give him your current map pieces.");
		goUpstairsForMap.addDialogStep("Let's talk about the investigation.");
		searchStoneChestNorth = new ObjectStep(this, NullObjectID.NULL_29912, new WorldPoint(3767, 3874, 1), "Search the stone chest in the north of the house's top floor.");
		leaveHouseForMap = new ObjectStep(this, ObjectID.STAIRS_30682, new WorldPoint(3755, 3869, 1), "Leave the house down the stairs to the west.");
		searchFungi = new ObjectStep(this, NullObjectID.NULL_29913, new WorldPoint(3755, 3873, 0), "Search the fungi north of the stairs.");
		searchBriar = new ObjectStep(this, NullObjectID.NULL_29914, new WorldPoint(3765, 3862, 0), "Search the hook briar south of the house.");
		searchMushtree = new ObjectStep(this, NullObjectID.NULL_29915, new WorldPoint(3791, 3877, 0), "Search the mushtree east of the house.");
		enterHouseWithMapPieces = new ObjectStep(this, ObjectID.STAIRS_30681, new WorldPoint(3755, 3869, 0), "Return to Dallas with the map pieces.");
		goDownWithMapPieces = new ObjectStep(this, ObjectID.TRAPDOOR_30725, new WorldPoint(3768, 3867, 1), "Go down the trapdoor to the basement.");
		talkToDallasWithMapPieces = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(3762, 3868, 0), "Talk to Dallas with the map pieces.");
		talkToDallasWithMapPieces.addDialogSteps("Let's talk about the investigation.", "Let's do it!");
		enterHouseWithMapPieces.addSubSteps(goDownWithMapPieces, talkToDallasWithMapPieces);

		startMapPuzzle = new ObjectStep(this, NullObjectID.NULL_29910, new WorldPoint(3764, 3869, 0), "Click on the incomplete map for a puzzle to solve.");
		solveMap = new MapPuzzle(this);
		startMapPuzzle.addSubSteps(solveMap);
		talkToDallasAfterSolvingMap = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(3762, 3868, 0), "Talk to Dallas after solving the map.");

		// Phase 3
		talkToJardricInMuseumCamp = new NpcStep(this, NpcID.JARDRIC_8105, new WorldPoint(3719, 3812, 0), "Talk to Jardric in the museum camp.");
		buildRowBoat = new ObjectStep(this, NullObjectID.NULL_29916, new WorldPoint(3659, 3849, 0), "Build a rowing boat on the west coast of Fossil Island.", hammer, saw, oakPlank8, swampPaste10, nails12OrMore);
		talkToDallasAfterBoatRepair = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(3663, 3849, 0), "Talk to Dallas near the boat.");
		talkToDallasAfterBoatRepair.addDialogStep("I'm ready.");
		boardBoat = new ObjectStep(this, NullObjectID.NULL_29916, new WorldPoint(3659, 3849, 0), "Board the boat.");
		talkToDallasAfterBoatRepair.addSubSteps(boardBoat);
		climbCourtyardStairs = new ObjectStep(this, ObjectID.STAIRS_32081, new WorldPoint(3559, 4004, 0), "Climb up the stairs to the west.");
		climbDownLithkrenTrapdoor = new ObjectStep(this, ObjectID.TRAPDOOR_32084, new WorldPoint(3555, 3999, 1), "Climb down the trapdoor to the south.");
		climbDownLithkrenNorthStairs = new ObjectStep(this, ObjectID.STAIRCASE_32080, new WorldPoint(3555, 4004, 0), "Climb down the staircase to the north.");

		talkToDallasInLithkren = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(3549, 10419, 0), "Talk to Dallas in the far north of the room.");
		searchSkeleton = new ObjectStep(this, NullObjectID.NULL_29871, new WorldPoint(3555, 10419, 0), "Search the skeleton east of Dallas.");
		readDiary = new DetailedQuestStep(this, "Read Aivas' Diary.", aivasDiary);
		talkToDallasAfterDiary = new NpcStep(this, NpcID.DALLAS_JONES, new WorldPoint(3549, 10419, 0), "Talk to Dallas again.");

		talkToBob = new NpcStep(this, NpcID.BOB_8034, "Use a catspeak amulet (e) to locate Bob the Cat and talk to him. He's often in Catherby Archery Shop or at the Varrock Anvil.", catspeakAmulet);
		talkToSphinx = new NpcStep(this, NpcID.SPHINX_4209, new WorldPoint(3301, 2785, 0), "Talk to the Sphinx in Sophanem.");
		talkToSphinx.addDialogStep("Ask the Sphinx for help with Bob's memories.");
		// Sphinx allows you to always talk to cats: 6144 0->1, 6145 1->0

		talkToOneiromancer = new NpcStep(this, NpcID.ONEIROMANCER, new WorldPoint(2151, 3867, 0), "Talk to the Oneiromancer in the south east of Lunar Isle.", sealOfPassage);
		talkToOneiromancer.addDialogSteps("Bob's memories", "No thanks.");
		fillDreamVial = new ObjectStep(this, ObjectID.SINK_16705, new WorldPoint(2091, 3922, 0), "Fill the vial with water.", dreamVial);
		fillDreamVial.addIcon(ItemID.DREAM_VIAL_EMPTY);

		addGoutweed = new DetailedQuestStep(this, "Add a goutweed to the dream vial.", dreamVialWater, goutweed);
		crushAstralRune = new DetailedQuestStep(this, "Use a hammer on an astral rune.", hammerHighlighted, astralRuneHighlighted);
		grindAstralShards = new DetailedQuestStep(this, "Use a pestle and mortar on the astral rune shards.", pestleAndMortarHighlighted, astralRuneShards);
		addGroundAstral = new DetailedQuestStep(this, "Add the ground astral rune to the dream vial.", groundAstralRune, dreamVialWithGoutweed);

		lightBrazier = new ObjectStep(this, NullObjectID.NULL_17025, new WorldPoint(2073, 3912, 0),
			"Equip your combat equipment, food, and light the Brazier in the west of Lunar Isle's town. Be prepared for a fight. You will need melee gear, preferably a crush weapon, to fight.",
			sealOfPassage, tinderbox, dreamPotion, combatGear);
		lightBrazier.addIcon(ItemID.TINDERBOX);

		usePotionOnFlame = new ObjectStep(this, NullObjectID.NULL_17025, new WorldPoint(2073, 3912, 0),
			"Use the dream potion on the brazier.",
			sealOfPassage, dreamPotionHighlighted, combatGear);
		usePotionOnFlame.addDialogStep("I'm ready.");
		usePotionOnFlame.addIcon(ItemID.DREAM_POTION);

		talkToBobToEnterDreamAgain = new NpcStep(this, NpcID.BOB_8111, new WorldPoint(2074, 3912, 0), "Talk to Bob next to the brazier to enter the dream again.", combatGear);
		talkToBobToEnterDreamAgain.addDialogStep("I'm ready.");

		talkToBobInDream = new NpcStep(this, NpcID.BOB_8112, new WorldPoint(1824, 5209, 2), "Talk to Bob in the dream.");
		talkToBobInDream.addSubSteps(talkToBobToEnterDreamAgain);
		killRobertTheStrong = new NpcStep(this, NpcID.ROBERT_THE_STRONG_8057, new WorldPoint(1824, 5224, 2), "When you're ready, fight Robert.");
		killRobertTheStrong.addText("Use Protect from Missiles.");
		killRobertTheStrong.addText("When he shouts 'See if you can hide from this!', hide behind a pillar.");

		talkToBobAfterRobertFight = new NpcStep(this, NpcID.BOB_8111, new WorldPoint(2074, 3912, 0), "Talk to Bob next to the brazier.");

		// Kourend key piece
		talkToAmelia = new NpcStep(this, NpcID.AMELIA, new WorldPoint(1540, 3545, 0), "Talk to Amelia south west of the Shayzien Archery Store.");
		enterCrypt = new ObjectStep(this, ObjectID.CRYPT_ENTRANCE, new WorldPoint(1483, 3548, 0), "Enter the Crypt west of Amelia.");
		goDownInCryptF2ToF1 = new ObjectStep(this, ObjectID.LADDER_32397, new WorldPoint(1524, 9967, 3), "Climb down to the bottom of the crypts.");
		goDownInCryptF1ToF0 = new ObjectStep(this, ObjectID.STAIRCASE_32394, new WorldPoint(1511, 9979, 2), "Climb down to the bottom of the crypts.");
		goDownInCryptF2ToF1.addSubSteps(goDownInCryptF1ToF0);
		searchTombInCrypt = new ObjectStep(this, NullObjectID.NULL_29901, new WorldPoint(1504, 9939, 1), "Inspect the tomb in the south room.");
		solveCryptPuzzle = new CryptPuzzle(this);
		searchTombForCryptKey = new ObjectStep(this, NullObjectID.NULL_29901, new WorldPoint(1504, 9939, 1), "Inspect the tomb for the key.");

		// Varrock key piece
		talkToReldo = new NpcStep(this, NpcID.RELDO_4243, new WorldPoint(3209, 3492, 0), "Talk to Reldo in the Varrock Castle Library.");
		talkToReldo.addDialogStep("Ask about Tristan.");
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_32462, new WorldPoint(3211, 3495, 0), "Search the bookcases for the census");
		talkToReldoAgain = new NpcStep(this, NpcID.RELDO_4243, new WorldPoint(3209, 3492, 0), "Talk to Reldo again.", varrockCensusRecords);
		talkToReldoAgain.addDialogStep("Ask about Tristan.");
		talkToReldoAgainNoBook = new NpcStep(this, NpcID.RELDO_4243, new WorldPoint(3209, 3492, 0), "Talk to Reldo again.");
		talkToReldoAgainNoBook.addDialogStep("Ask about Tristan.");
		talkToReldoAgain.addSubSteps(talkToReldoAgainNoBook);
		talkToSarah = new NpcStep(this, NpcID.SARAH_8134, new WorldPoint(3674, 3469, 0), "Talk to Sarah in Port Phasmatys.", ghostspeakOrMory2);
		talkToAva = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0), "Talk to Ava in Draynor Manor.", dragonstone, moltenGlass2, glassblowingPipe);
		talkToAva.addDialogStep("I need your help with a key piece.");
		usePipeOnDragonstone = new DetailedQuestStep(this, "Use the glassblowing pipe on a dragonstone.", glassblowingPipeHighlighted, dragonstoneHighlighted, moltenGlass2);
		talkToAvaAgain = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0), "Talk to Ava again.", inertLocator);
		talkToAvaAgain.addDialogStep("I need your help with a key piece.");
		talkToAvaAgainNoOrb = new NpcStep(this, NpcID.AVA, new WorldPoint(3093, 3357, 0), "Talk to Ava again.");
		talkToAvaAgainNoOrb.addDialogStep("I need your help with a key piece.");
		talkToAvaAgain.addSubSteps(talkToAvaAgainNoOrb);
		useLocatorInSwamp = new DetailedQuestStep(this, "Use the locator orb in Mort Myre to locate the dig spot. Once you find the spot, dig there.", locator, spade);

		// Fremennik key piece
		talkToBrundt = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3666, 0), "Talk to Brundt in Rellekka.");
		talkToBrundt.addDialogStep("Ask about the dragonkin fortress.");
		talkToTorfinn = new NpcStep(this, NpcID.TORFINN, new WorldPoint(2640, 3696, 0), "Talk to Torfinn on the Rellekka docks to go to Ungael.");
		((NpcStep) (talkToTorfinn)).addAlternateNpcs(NpcID.TORFINN_10403);
		talkToTorfinn.addDialogSteps("I'm ready.", "Yes please.");
		killVorkath = new NpcStep(this, NpcID.VORKATH, new WorldPoint(2273, 4065, 0), "Defeat Vorkath. " +
			"This is a hard fight, so if you're unfamiliar with it it's recommended you watch a video and read the " +
			"sidebar first.", rangedCombatGear);
		((NpcStep) (killVorkath)).addAlternateNpcs(NpcID.VORKATH_8058, NpcID.VORKATH_8059, NpcID.VORKATH_8060, NpcID.VORKATH_8061);

		killVorkathSidebar = new NpcStep(this, NpcID.VORKATH, new WorldPoint(2273, 4065, 0), "Defeat Vorkath. " +
			"This is a hard fight, so if you're unfamiliar with it it's recommended you watch a video on it first.", rangedCombatGear);
		killVorkathSidebar.addSubSteps(killVorkath);

			killVorkathSidebar.addText("Protect from Magic, and drink an antifire and antivenom potion.");
		killVorkathSidebar.addText("When Vorkath fires a pink fireball it'll turn your prayer off.");
		killVorkathSidebar.addText("When Vorkath shoots an orange fireball into the air, move away from the spot you're on.");
		killVorkathSidebar.addText("When Vorkath spits acid everywhere, WALK around to avoid the fireballs he will shoot.");
		killVorkathSidebar.addText("When Vorkath freezes you, kill the zombified spawn that appears before it reaches you.");


		enterVorkathCave = new ObjectStep(this, ObjectID.CAVE_31999, new WorldPoint(2249, 4078, 0), "Enter the cave on the north west of Ungael.");
		enterVorkathCave.addDialogStep("I'm sure.");

		pullLeverInVorkathCave = new ObjectStep(this, ObjectID.LEVER_32056, new WorldPoint(2260, 10464, 0), "Pull the lever in the south west corner. Make sure you have run energy before doing so, as you need to run to the east room afterwards.");
		enterEastVorkathRoom = new DetailedQuestStep(this, new WorldPoint(2290, 10465, 0), "RUN to the east room.");
		searchStoneChestForVorkathKey = new ObjectStep(this, ObjectID.STONE_CHEST_32053, new WorldPoint(2293, 10467, 0), "Search the stone chest in the room for the key piece.");

		// Karamja key piece
		enterKharaziMaze = new ObjectStep(this, ObjectID.CAVE_32479, new WorldPoint(2944, 2895, 0), "Go down the staircase in the south east of the Kharazi Jungle. MAKE SURE TO HAVE AUTO-RETALIATE OFF BEFORE ENTERING.");
		getToCentreOfMaze = new ObjectStep(this, NullObjectID.NULL_30730, new WorldPoint(2848, 9248, 0), "Navigate to the middle of the maze, disarming any traps in the way.");
		getToCentreOfMaze.setLinePoints(Arrays.asList(
			new WorldPoint(2847, 9284, 0),
			new WorldPoint(2847, 9276, 0),
			new WorldPoint(2843, 9274, 0),
			new WorldPoint(2843, 9272, 0),
			new WorldPoint(2836, 9272, 0),
			new WorldPoint(2836, 9275, 0),
			new WorldPoint(2828, 9275, 0),
			new WorldPoint(2828, 9264, 0),
			new WorldPoint(2832, 9264, 0),
			new WorldPoint(2832, 9256, 0),
			new WorldPoint(2836, 9256, 0),
			new WorldPoint(2836, 9248, 0),
			new WorldPoint(2840, 9248, 0),
			new WorldPoint(2840, 9236, 0),
			new WorldPoint(2851, 9236, 0),
			new WorldPoint(2851, 9232, 0),
			new WorldPoint(2856, 9232, 0),
			new WorldPoint(2856, 9228, 0),
			new WorldPoint(2860, 9228, 0),
			new WorldPoint(2860, 9224, 0),

			new WorldPoint(2867, 9224, 0),
			new WorldPoint(2867, 9220, 0),
			new WorldPoint(2871, 9220, 0),
			new WorldPoint(2871, 9240, 0),
			new WorldPoint(2875, 9240, 0),
			new WorldPoint(2875, 9251, 0),
			new WorldPoint(2875, 9251, 0),
			new WorldPoint(2865, 9251, 0),
			new WorldPoint(2865, 9256, 0),
			new WorldPoint(2864, 9256, 0),
			new WorldPoint(2864, 9267, 0),
			new WorldPoint(2867, 9267, 0),
			new WorldPoint(2867, 9275, 0),
			new WorldPoint(2864, 9275, 0),
			new WorldPoint(2864, 9272, 0),
			new WorldPoint(2860, 9272, 0),
			new WorldPoint(2860, 9264, 0),
			new WorldPoint(2856, 9264, 0),
			new WorldPoint(2856, 9256, 0),
			new WorldPoint(2848, 9256, 0),
			new WorldPoint(2848, 9249, 0)
		));

		boardBoat = new ObjectStep(this, NullObjectID.NULL_29916, new WorldPoint(3659, 3849, 0), "Board the boat.");
		talkToDallasAfterBoatRepair.addSubSteps(boardBoat);
		climbCourtyardStairs = new ObjectStep(this, ObjectID.STAIRS_32081, new WorldPoint(3559, 4004, 0), "Climb up the stairs to the west.");
		climbDownLithkrenTrapdoor = new ObjectStep(this, ObjectID.TRAPDOOR_32084, new WorldPoint(3555, 3999, 1), "Climb down the trapdoor to the south.");
		climbDownLithkrenNorthStairs = new ObjectStep(this, ObjectID.STAIRCASE_32080, new WorldPoint(3555, 4004, 0), "Climb down the staircase to the north.");

		enterAncientCavern = new ObjectStep(this, ObjectID.WHIRLPOOL_25274, new WorldPoint(2512, 3508, 0), "Enter the Ancient Caverns through the whirlpool near the Barbarian Outpost.");
		goDownToBrutalGreenDragons = new ObjectStep(this, ObjectID.STAIRS_25338, new WorldPoint(1770, 5366, 1), "Go down the stairs.");
		goUpToMithrilDragons = new ObjectStep(this, ObjectID.STAIRS_25339, new WorldPoint(1778, 5345, 0), "Go up the stairs to the Mithil Dragons.");
		openMithrilDoor = new ObjectStep(this, ObjectID.MITHRIL_DOOR, new WorldPoint(1759, 5343, 1), "Enter the Mithril Door to the west of the area.");
		goDownToBrutalGreenDragons.addSubSteps(goUpToMithrilDragons, openMithrilDoor);
		castFireOnHead = new NpcStep(this, NpcID.DRAGON_HEAD, "Cast fire wave or surge on the 3 dragon heads.", true);

		leaveMithrilRoom = new ObjectStep(this, ObjectID.MITHRIL_DOOR_32208, new WorldPoint(1555, 4875, 0), "Leave the orb room.");
		goDownFromMithDragons = new ObjectStep(this, ObjectID.STAIRS_25340, new WorldPoint(1778, 5345, 1), "Go back to the ground floor.");
		climbUpSouthWestStairsAncientCavern = new ObjectStep(this, ObjectID.ROUGH_HEWN_STEPS_32212, new WorldPoint(1745, 5324, 0), "Climb up the rough steps in the south west corner.");
		forgeDragonKey = new ObjectStep(this, ObjectID.ANVIL_32215, new WorldPoint(1748, 5288, 1), "Forge the key on one of the anvils to the south.");
		useKeyOnGrandioseDoors = new ObjectStep(this, NullObjectID.NULL_29872, new WorldPoint(3550, 10425, 0), "Unlock the grandiose door.");

		talkToDallasAfterDoors = new NpcStep(this, NpcID.DALLAS_JONES_8102, new WorldPoint(1566, 5099, 0), "Talk to Dallas Jones to the north.");

		talkToBobAfterRelease = new NpcStep(this, NpcID.BOB_8111, new WorldPoint(3548, 10480, 0), "");

		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_5215, new WorldPoint(3222, 3473, 0), "Talk to King Roald in Varrock Castle.");
		talkToRoald.addDialogStep("Talk about the dragon threat.");

		talkToBrundtAboutThreat = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3666, 0), "Talk to Brundt in Rellekka.");
		talkToBrundtAboutThreat.addDialogStep("Ask about the dragon threat.");

		goUpToAmik1 = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2955, 3339, 0), "Go talk to Sir Amik Varze upstairs in Falador Castle.");
		goUpToAmik2 = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2961, 3339, 1), "Go talk to Sir Amik Varze upstairs in Falador Castle.");
		talkToAmik = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, new WorldPoint(2962, 3338, 2), "Talk to Sir Amik Varze upstairs in Falador Castle.");
		talkToAmik.addDialogStep("Talk about the dragon threat.");
		talkToAmik.addSubSteps(goUpToAmik1, goUpToAmik2);

		goUpToLathasOrThoros = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0), "Talk to King Lathas/Thoros in East Ardougne castle.");
		talkToLathasOrThoros = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Talk to King Lathas/Thoros in East Ardougne castle.");
		((NpcStep) (talkToLathasOrThoros)).addAlternateNpcs(NpcID.KING_THOROS, NpcID.KING_THOROS_9011);
		talkToLathasOrThoros.addDialogStep("Talk about the dragon threat.");
		talkToLathasOrThoros.addSubSteps(goUpToLathasOrThoros);

		enterVarrockDiningRoom = new ObjectStep(this, ObjectID.DOOR_32464, new WorldPoint(3222, 3479, 0), "Enter the Varrock Castle dining room.");
		talkToBobAfterDiningRoom = new NpcStep(this, NpcID.BOB_8111, new WorldPoint(3222, 3476, 0), "Talk to Bob in Varrock Castle.");

		// Final battle
		takeBoatToUngael = new NpcStep(this, NpcID.TORFINN_10405, new WorldPoint(2640, 3696, 0), "Talk to Torfinn on the Rellekka docks to go to Ungael. Be prepared for a lot of fighting.", combatGear, antifireShield);
		takeBoatToUngael.addDialogSteps("Yes please.", "Yes.");
		keepShipAfloat = new ObjectStep(this, ObjectID.FIRE_32297, "Keep the boat afloat by filling leaks, putting out fires, healing warriors and repairing the masts.");
		((ObjectStep) (keepShipAfloat)).addAlternateObjects(ObjectID.LEAK, ObjectID.FREMENNIK_WARRIOR_32302, ObjectID.FREMENNIK_WARRIOR_32300, ObjectID.DAMAGED_MAST);

		getToMainShip = new NpcStep(this, NpcID.RED_DRAGON_8079, "Travel to the main ship, killing dragons along the way.", true);
		((NpcStep) (getToMainShip)).addAlternateNpcs(NpcID.IRON_DRAGON_8080, NpcID.BRUTAL_GREEN_DRAGON_8081);
		getToMainShip.setLinePoints(Arrays.asList(
			new WorldPoint(1695, 5665, 2),
			new WorldPoint(1688, 5665, 1),
			new WorldPoint(1679, 5665, 1),
			new WorldPoint(1670, 5667, 1),
			new WorldPoint(1667, 5667, 1),
			new WorldPoint(1662, 5667, 1),
			new WorldPoint(1662, 5661, 1),
			new WorldPoint(1649, 5662, 1),
			new WorldPoint(1647, 5660, 1),
			new WorldPoint(1642, 5660, 1),
			new WorldPoint(1641, 5660, 2),
			new WorldPoint(1635, 5666, 2),

			new WorldPoint(1635, 5666, 0),
			new WorldPoint(1621, 5666, 0),

			new WorldPoint(1621, 5666, 2),
			new WorldPoint(1621, 5668, 2),

			new WorldPoint(1621, 5670, 1),
			new WorldPoint(1619, 5672, 1),
			new WorldPoint(1619, 5674, 0),
			new WorldPoint(1619, 5681, 0),
			new WorldPoint(1627, 5681, 0),
			new WorldPoint(1628, 5681, 1),
			new WorldPoint(1632, 5681, 1),
			new WorldPoint(1640, 5681, 0),
			new WorldPoint(1645, 5681, 0),
			new WorldPoint(1647, 5681, 1),
			new WorldPoint(1655, 5681, 1),
			new WorldPoint(1655, 5683, 1),
			new WorldPoint(1655, 5684, 0),
			new WorldPoint(1655, 5688, 0),
			new WorldPoint(1655, 5688, 2),
			new WorldPoint(1658, 5688, 2),
			new WorldPoint(1660, 5688, 1),
			new WorldPoint(1662, 5689, 1),
			new WorldPoint(1665, 5689, 0),
			new WorldPoint(1668, 5689, 0),
			new WorldPoint(1668, 5698, 0),
			new WorldPoint(1668, 5698, 1),
			new WorldPoint(1668, 5705, 1),
			new WorldPoint(1668, 5707, 0),
			new WorldPoint(1668, 5710, 0),
			new WorldPoint(1672, 5710, 0),

			new WorldPoint(1673, 5710, 2),
			new WorldPoint(1679, 5708, 2),
			new WorldPoint(1690, 5708, 1),
			new WorldPoint(1698, 5710, 1),
			new WorldPoint(1698, 5712, 0),
			new WorldPoint(1698, 5715, 0),
			new WorldPoint(1702, 5719, 0),
			new WorldPoint(1702, 5723, 0),
			new WorldPoint(1702, 5724, 1),
			new WorldPoint(1703, 5727, 1),
			new WorldPoint(1708, 5729, 0),
			new WorldPoint(1710, 5731, 0),
			new WorldPoint(1710, 5734, 0),
			new WorldPoint(1705, 5739, 0),
			new WorldPoint(1704, 5739, 2),
			new WorldPoint(1698, 5737, 2),
			new WorldPoint(1696, 5737, 0),
			new WorldPoint(1688, 5737, 0),
			new WorldPoint(1687, 5738, 0),
			new WorldPoint(1670, 5738, 0),
			new WorldPoint(1666, 5734, 0),
			new WorldPoint(1660, 5734, 0),
			new WorldPoint(1657, 5733, 1),
			new WorldPoint(1657, 5726, 1),
			new WorldPoint(1657, 5724, 0),
			new WorldPoint(1657, 5714, 0),
			new WorldPoint(1657, 5713, 1),
			new WorldPoint(1657, 5704, 1),
			new WorldPoint(1656, 5704, 1),
			new WorldPoint(1654, 5704, 0),
			new WorldPoint(1651, 5701, 0),
			new WorldPoint(1645, 5701, 0),
			new WorldPoint(1640, 5706, 0),
			new WorldPoint(1637, 5706, 0)
		));

		kill2Blue2Green = new NpcStep(this, NpcID.BLUE_DRAGON_8083, "Kill the blue and green dragons.", true);
		((NpcStep) (kill2Blue2Green)).addAlternateNpcs(NpcID.GREEN_DRAGON_8082);

		killBlackSteelBrutalRedDragon = new NpcStep(this, NpcID.BLACK_DRAGON_8084, "Kill the black, steel, and brutal" +
			" red dragon. Occasionally Galvek will shoot a fireball in the air, move to avoid it.", true);
		((NpcStep) (killBlackSteelBrutalRedDragon)).addAlternateNpcs(NpcID.BLACK_DRAGON_8085, NpcID.BRUTAL_RED_DRAGON_8087, NpcID.STEEL_DRAGON_8086);

		killMithAddyAndRuneDragons = new NpcStep(this, NpcID.MITHRIL_DRAGON_8088, "Kill the metal dragons.", true);
		killMithAddyAndRuneDragons.addText("Occasionally Galvek will shoot a fireball in the air, move to avoid it.");
		killMithAddyAndRuneDragons.addText("");
		((NpcStep) (killMithAddyAndRuneDragons)).addAlternateNpcs(NpcID.MITHRIL_DRAGON_8089, NpcID.ADAMANT_DRAGON, NpcID.ADAMANT_DRAGON_8090, NpcID.RUNE_DRAGON, NpcID.RUNE_DRAGON_8091);
		killGalvek = new NpcStep(this, NpcID.GALVEK_8095, new WorldPoint(1631, 5735, 2), "Kill Galvek. Read the " +
			"sidebar for more details.",true);
		((NpcStep) (killGalvek)).addAlternateNpcs(NpcID.GALVEK_8096, NpcID.GALVEK_8097, NpcID.GALVEK_8098);

		killGalvekSidebar = new NpcStep(this, NpcID.GALVEK_8095, new WorldPoint(1631, 5735, 2),
			"Kill Galvek. This is a hard fight, so it's recommended you check a video to see what you'll have to do.");
		killGalvekSidebar.addText("Avoid the ground-targeted fireballs.");
		killGalvekSidebar.addText("The pink attack turns off prayer.");
		killGalvekSidebar.addText("Use Protect from Magic in phase 1, use Protect from Missiles otherwise.");
		killGalvekSidebar.addText("Phase 1 - Fire traps appear - if you go near them you die.");
		killGalvekSidebar.addText("Phase 2 - Fires transparent projectile which drains stats.");
		killGalvekSidebar.addText("Phase 3 - Waves of fire appear. Go through the gap in them to avoid.");
		killGalvekSidebar.addText("Phase 4 - Fire binding projective. Avoid it by moving.");
		killGalvekSidebar.addSubSteps(killGalvek);

		talkToAlecToFinish = new NpcStep(this, NpcID.ALEC_KINCADE, new WorldPoint(2458, 2869, 0), "Talk to Alec Kincade to become a Myth!");
		talkToAlecToFinish.addDialogStep("Let's talk about my quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pickaxe, axe, oakPlank8, swampPaste10, nails12OrMore, hammer, machete, saw, catspeakAmulet, lightSource, goutweed,
			astralRune, sealOfPassage, tinderbox, pestleAndMortarHighlighted, dragonstone, moltenGlass2, glassblowingPipe, ghostspeakOrMory2, chisel, spade,
			runesForFireWaveOrSurge3, antifireShield);
	}

	public void setupConditionalSteps()
	{
		goEnterMithDoorFirstTime = new ConditionalStep(this, enterAncientCavern, "Open the Mithril Door in the Ancient Caverns.", ancientKey, runesForFireWaveOrSurge3, fremennikKeyPiece, karamjaKeyPiece, kourendKeyPiece, varrocKeyPiece, hammer);
		goEnterMithDoorFirstTime.addStep(inMithDragonOrbRoom, castFireOnHead);
		goEnterMithDoorFirstTime.addStep(inMithDragonUpperArea, openMithrilDoor);
		goEnterMithDoorFirstTime.addStep(inMithDragonGroundArea, goUpToMithrilDragons);
		goEnterMithDoorFirstTime.addStep(inMithDragonEntranceArea, goDownToBrutalGreenDragons);

		goEnterMithDoorSecondTime = new ConditionalStep(this, enterAncientCavern, "Open the Mithril Door in the Ancient Caverns.", runesForFireWaveOrSurge3, fremennikKeyPiece, karamjaKeyPiece, kourendKeyPiece, varrocKeyPiece, hammer);
		goEnterMithDoorSecondTime.addStep(inMithDragonOrbRoom, castFireOnHead);
		goEnterMithDoorSecondTime.addStep(inMithDragonUpperArea, openMithrilDoor);
		goEnterMithDoorSecondTime.addStep(inMithDragonGroundArea, goUpToMithrilDragons);
		goEnterMithDoorSecondTime.addStep(inMithDragonEntranceArea, goDownToBrutalGreenDragons);
		goEnterMithDoorFirstTime.addSubSteps(goEnterMithDoorSecondTime);

		goSmithKey = new ConditionalStep(this, enterAncientCavern, "Forge the dragon key on the anvils in the south of the Ancient Caverns.", fremennikKeyPiece, karamjaKeyPiece, kourendKeyPiece, varrocKeyPiece, hammer);
		goSmithKey.addStep(inMithDragonFurnaceArea, forgeDragonKey);
		goSmithKey.addStep(inMithDragonGroundArea, climbUpSouthWestStairsAncientCavern);
		goSmithKey.addStep(inMithDragonUpperArea, goDownFromMithDragons);
		goSmithKey.addStep(inMithDragonOrbRoom, leaveMithrilRoom);
		goSmithKey.addStep(inMithDragonEntranceArea, goDownToBrutalGreenDragons);

		goOpenDoorWithKey = new ConditionalStep(this, boardBoat, "Open the door on Lithkren.", dragonKey);
		goOpenDoorWithKey.addStep(inLithkrenUnderground, useKeyOnGrandioseDoors);
		goOpenDoorWithKey.addStep(inLithkrenGroundFloorRoom, climbDownLithkrenNorthStairs);
		goOpenDoorWithKey.addStep(inLithkrenFirstFloor, climbDownLithkrenTrapdoor);
		goOpenDoorWithKey.addStep(inLithkrenGroundFloor, climbCourtyardStairs);

		openDoorWithoutKey = new ConditionalStep(this, boardBoat, "Enter the grandiose doors and talk to Dallas in the new area.");
		openDoorWithoutKey.addStep(inDeepLithkren, talkToDallasAfterDoors);
		openDoorWithoutKey.addStep(inLithkrenUnderground, useKeyOnGrandioseDoors);
		openDoorWithoutKey.addStep(inLithkrenGroundFloorRoom, climbDownLithkrenNorthStairs);
		openDoorWithoutKey.addStep(inLithkrenFirstFloor, climbDownLithkrenTrapdoor);
		openDoorWithoutKey.addStep(inLithkrenGroundFloor, climbCourtyardStairs);

		goTalkToBobAfterRelease = new ConditionalStep(this, boardBoat, "Talk to Bob underground in Lithkren.");
		goTalkToBobAfterRelease.addStep(inLithkrenUnderground, talkToBobAfterRelease);
		goTalkToBobAfterRelease.addStep(inLithkrenGroundFloorRoom, climbDownLithkrenNorthStairs);
		goTalkToBobAfterRelease.addStep(inLithkrenFirstFloor, climbDownLithkrenTrapdoor);
		goTalkToBobAfterRelease.addStep(inLithkrenGroundFloor, climbCourtyardStairs);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ardougneTeleport, varrockTeleport, faladorTeleport, morytaniaTeleport,
			karamjaTeleport, rellekkaTeleport);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Spawn (level 100)", "Robert the Strong (level 194)", "Vorkath (level 392)", "Numerous chromatic and metal dragons", "Galvek (level 608)");
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
				new ExperienceReward(Skill.SMITHING, 25000),
				new ExperienceReward(Skill.MINING, 18000),
				new ExperienceReward(Skill.AGILITY, 15000),
				new ExperienceReward(Skill.THIEVING, 15000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("25,000 Experience Tome (Any Combat Skill).", ItemID.ANTIQUE_LAMP, 4), //4447 is placeholder
				new ItemReward("A Locator Orb", ItemID.LOCATOR_ORB, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Myths Guild."),
				new UnlockReward("Ability to make Super Antifire Potions"),
				new UnlockReward("Access to the Fountain of Uhld."),
				new UnlockReward("Access to the Wrath Altar."),
				new UnlockReward("Ability to purchase the Mythical Cape"),
				new UnlockReward("Access to Adamant and Rune Dragons"),
				new UnlockReward("Access to Vorkath"),
				new UnlockReward("Ability to re-forge the dragon platebody and kiteshield."),
				new UnlockReward("Ability to craft Ferocious Gloves"),
				new UnlockReward("Ability to further upgrade your Ava's device."),
				new UnlockReward("Ability to teleport to Lithkren with the Digsite Pendant."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToAlec, talkToDallas, talkToDallasOnCrandor, usePickaxeOnBlockage, enterBlockage, investigateMural, killSpawn,
			investigateMuralAgain, talkToDallasAfterMural), pickaxe, combatGear));
		allSteps.add(new PanelDetails("Investigating Fossil Island", Arrays.asList(enterHouseOnTheHill, talkToDallasInHouse, searchNorthChest,
			goUpstairsForMap, searchStoneChestNorth, leaveHouseForMap, searchFungi, searchBriar, searchMushtree, enterHouseWithMapPieces, startMapPuzzle,
			talkToDallasAfterSolvingMap)));
		allSteps.add(new PanelDetails("Investigating Lithkren", Arrays.asList(talkToJardricInMuseumCamp, buildRowBoat, talkToDallasAfterBoatRepair,
			climbCourtyardStairs, climbDownLithkrenTrapdoor, climbDownLithkrenNorthStairs, talkToDallasInLithkren, searchSkeleton, readDiary, talkToDallasAfterDiary), oakPlank8, swampPaste10, nails12OrMore, hammer, saw));

		allSteps.add(new PanelDetails("Contacting Bob", Arrays.asList(talkToBob, talkToSphinx), catspeakAmulet));

		allSteps.add(new PanelDetails("Entering a dream", Arrays.asList(talkToOneiromancer, fillDreamVial, addGoutweed, crushAstralRune, grindAstralShards, addGroundAstral,
			lightBrazier, usePotionOnFlame, talkToBobInDream, killRobertTheStrong, talkToBobAfterRobertFight), sealOfPassage, goutweed, astralRune, hammer, pestleAndMortarHighlighted, tinderbox, combatGear));

		PanelDetails karamjaKeyPanel = new PanelDetails("Karamja key piece", Arrays.asList(enterKharaziMaze, getToCentreOfMaze), machete, axe, food);
		karamjaKeyPanel.setLockingStep(gettingTheKaramjaKey);

		PanelDetails varrockKeyPanel = new PanelDetails("Varrock key piece", Arrays.asList(talkToReldo, searchBookcase, talkToReldoAgain, talkToSarah,
			talkToAva, usePipeOnDragonstone, talkToAvaAgain, useLocatorInSwamp), ghostspeakOrMory2, dragonstone, moltenGlass2, glassblowingPipe, spade);
		varrockKeyPanel.setLockingStep(gettingTheVarrockKey);

		PanelDetails kourendKeyPanel = new PanelDetails("Kourend key piece", Arrays.asList(talkToAmelia, enterCrypt, goDownInCryptF2ToF1, searchTombInCrypt, solveCryptPuzzle, searchTombForCryptKey), combatGear, lightSource);
		kourendKeyPanel.setLockingStep(gettingTheKourendKey);

		PanelDetails fremennikKeyPanel = new PanelDetails("Fremennik key piece", Arrays.asList(talkToBrundt, talkToTorfinn, killVorkathSidebar, enterVorkathCave, pullLeverInVorkathCave, enterEastVorkathRoom, searchStoneChestForVorkathKey), combatGear);
		fremennikKeyPanel.setLockingStep(gettingTheFremennikKey);

		allSteps.add(kourendKeyPanel);
		allSteps.add(varrockKeyPanel);
		allSteps.add(fremennikKeyPanel);
		allSteps.add(karamjaKeyPanel);

		allSteps.add(new PanelDetails("Unlocking the door", Arrays.asList(goEnterMithDoorFirstTime, castFireOnHead, goSmithKey,
			goOpenDoorWithKey, openDoorWithoutKey, goTalkToBobAfterRelease), ancientKey, runesForFireWaveOrSurge3, fremennikKeyPiece,
			karamjaKeyPiece, varrocKeyPiece, kourendKeyPiece, hammer, antifireShield));

		allSteps.add(new PanelDetails("Creating an army", Arrays.asList(talkToRoald, talkToBrundtAboutThreat, talkToAmik, talkToLathasOrThoros, enterVarrockDiningRoom, talkToBobAfterDiningRoom)));

		allSteps.add(new PanelDetails("Final showdown", Arrays.asList(takeBoatToUngael, keepShipAfloat, getToMainShip, kill2Blue2Green, killBlackSteelBrutalRedDragon,
			killMithAddyAndRuneDragons, killGalvekSidebar, talkToAlecToFinish), combatGear, antifireShield));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(200));
		req.add(new QuestRequirement(QuestHelperQuest.LEGENDS_QUEST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.DREAM_MENTOR, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.A_TAIL_OF_TWO_CATS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ANIMAL_MAGNETISM, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.BONE_VOYAGE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.CLIENT_OF_KOUREND, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MAGIC, 75));
		req.add(new SkillRequirement(Skill.SMITHING, 70));
		req.add(new SkillRequirement(Skill.MINING, 68));
		req.add(new SkillRequirement(Skill.CRAFTING, 62));
		req.add(new SkillRequirement(Skill.AGILITY, 60, false, "60 Agility (higher recommended)"));
		req.add(new SkillRequirement(Skill.THIEVING, 60));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 50));
		req.add(new SkillRequirement(Skill.HITPOINTS, 50));
		req.add(new ItemRequirement("Unlocked the Ancient Caverns during Barbarian Training", -1, -1));
		return req;
	}
}
