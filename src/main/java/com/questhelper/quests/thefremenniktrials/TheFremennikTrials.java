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
package com.questhelper.quests.thefremenniktrials;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ChatMessageCondition;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.ObjectCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_FREMENNIK_TRIALS
)
public class TheFremennikTrials extends BasicQuestHelper
{
	ItemRequirement coins, coins250, coins5000, beer, rawShark, tinderbox, axe, knife, potato, onion, cabbage, branch, lyre, lyreUnstrung,
		petRock, goldenFleece, goldenWool, enchantedLyre, strangeObject, litStrangeObject, alcoholFreeBeer, kegOfBeer, combatGear,
		huntersTalisman, chargedHuntersTalisman, promissoryNote, legendaryCocktail, championsToken, warriorsContract, weatherForecast,
		seaFishingMap, unusualFish, customBowString, trackingMap, fiscalStatement, sturdyBoots, ballad, exoticFlower, koscheiGear,
		optionalKoscheiGear, redHerring, woodenDisk, redDiskOld, emptyJug, redDiskNew, stickyRedGoop, emptyBucket, vase, fullBucket, fullJug, vaseLid,
		jug23, jug13, bucket45, bucket35, bucket25, bucket15, filledVase, filledVaseWithLid, vaseWithLidWrong, frozenKey, seersKey, frozenVase,
		ringsOfRecoil, camelotTeleport, rellekkaTeleports, dramenBranch;

	ConditionForStep synced, syncedAll, syncedOlaf, syncedManni, syncedSigmund, inQuestJournal, hasStartedOlaf, hasBranch, hasLyreUnstrung,
		talkedToLalli, gottenRock, hasPetRock, hasLyre, finishedOlafTask, hasOnion, hasCabbage, hasPotato, petRockInCauldron,
		cabbageInCauldron, potatoInCauldron, onionInCauldron, cauldronFilledDialog, stewReady, hasGoldenWool, hasGoldenFleece,
		hasEnchantedLyre, finishedOlafMessage, finishedOlafWidget, hasBeer, talkedToManni, hasStrangeObject, hasAlcoholFreeBeer,
		hasLitStrangeObject, isNearPipe, hasPlacedStrangeObject, hasReplacedBeer, finishedManniTask, hasKegOfBeer, talkedToManniWidget,
		talkedToManniChat, hasHuntersTalisman, hasChargedHuntersTalisman, finishedSigliTask, getFlower, talkedToSailor, talkedToOlafForSigmund,
		talkedToYsra, talkedToBrundtForSigmund, talkedToSigliForSigmund, talkedToSkulgrimenForSigmund, talkedToFishermanForSigmund, talkedToSwensenForSigmund,
		talkedToPeerForSigmund, talkedToThorvaldForSigmund, talkedToManniForSigmund, talkedToThoraForSigmund, talkedToAskeladdenForSigmund, hasPromissoryNote,
		hasLegendaryCocktail, hasChampionsToken, hasWarriorsContract, hasWeatherForecast, hasSeaFishingMap, hasUnusualFish, hasCustomBowString, hasTrackingMap, hasFiscalStatement,
		hasSturdyBoots, hasBallad, hasExoticFlower, finishedSigmundTask, inKoscheiRoom, talkedToThorvald, koschei1Near, koschei2Near, koschei3Near, koschei4Near, syncedThorvald,
		finishedThorvaldTask, talkedToSwensen, syncedSwensen, inSwensenRoom1, inSwensenRoom2, inSwensenRoom3, inSwensenRoom4, inSwensenRoom5, inSwensenRoom6, inSwensenRoom7, inSwensenArea,
		inSwensenFinalRoom, finishedSwensenTask, talkedToPeer, finishedPeerTask, isMind, isTree, isLife, isFire, isTime, isWind, inPeerEntrance, inPeerUpstairs, inPeerExit, hasSolvedDoor,
		hasRedHerring, hasWoodenDisk, hasRedDiskOld, hasRedDiskNew, hasStickyRedGoop, trapDoorOpen, hasUsedDisk, muralHasDisks, hasVase, hasVaseLid,
	    hasEmptyBucket, hasFullBucket, hasBucket45, hasBucket35, hasBucket25, hasBucket15, hasAnyBucket, hasFullJug, hasEmptyJug, hasJug23, hasJug13, hasAnyJug, cupboardOpen,
		chestOpen, hasFilledVase, hasFilledVaseWithLid, hasVaseWithLidWrong, hasFrozenKey, hasSeersKey, hasFrozenVase, syncedPeer, noRockAskeladdenNearby;

	QuestStep talkToBrundt, talkToOlaf, pickVeg, chopSwayingTree, fletchLyre, talkToLalli,
		talkToAskeladdenForRock, useCabbage, useOnion, usePotato, useRock, talkToLaliAfterStew, spinWool,
		makeLyre, enchantLyre, performMusic, talkToManni, pickUpBeer, getAlcoholFreeBeer, getStrangeObject,
		prepareToUseStrangeObject, useStrangeObject, useStrangeObjectOnPipe, cheatInBeerDrinking, getKegOfBeer,
		useAlcoholFreeOnKeg, talkToSigli, huntDraugen, returnToSigli, talkToSigmund, talkToSailor, talkToOlafForSigmund,
		talkToYsra, talkToBrundtForSigmund, talkToSigliForSigmund, talkToSkulgrimenForSigmund, talkToFishermanForSigmund, talkToSwenesenForSigmund,
		talkToPeerForSigmund, talkToThorvaldForSigmund, talkToManniForSigmund, talkToThoraForSigmund, talkToAskeladdenForSigmund, talkToAskeladdenForSigmund2, bringNoteToThora,
		bringCocktailToManni, bringChampionsTokenToThorvald, bringWarriorsContractToPeer, bringWeatherForecastToSwensen, bringSeaFishingMapToFisherman,
		bringUnusualFishToSkulgrimen, bringCustomBowStringToSigli, bringTrackingMapToBrundt, bringFiscalStatementToYsra, bringSturdyBootsToOlaf, bringBalladToSailor,
		bringExoticFlowerToSigmund, talkToThorvald, goDownLadderToKoschei, waitForKoschei, killKoschei1, killKoschei2, killKoschei3, killKoschei4, talkToSwensen, goDownLadderSwensen,
		swensen1South, swensen2West, swensen3East, swensen4North, swensen5South, swensen6East, swensen7North, swensenUpLadder, resetSwensen, talkToPeer, enterPeerHouse, inputMind,
		inputTree, inputLife, inputFire, inputTime, inputWind, goUpEntranceLadderPeer, enterCode, searchBookcase, cookHerring, searchUnicorn, searchBull, searchChest1, searchChest2, searchCupboard1,
		searchCupboard2, useGoopOnDisk, openTrapDoorAndGoDown1, goDown1, useDiskAnyOnMural, useDiskOldOnMural, useDiskNewOnMural, useBucketOnTap1, useBucketOnJug1, useJugOnDrain1, useBucketOnJug2, useBucketOnTap2, useBucketOnJug3,
		useBucketOnScale, goBackUpstairs, goUpstairsWithVaseLid, emptyJugAndBucket, fillVase, useVaseOnTable, useLidOnVase, takeLidOff, useFrozenKeyOnRange, goDownstairsWithKey, goDownstairsWithKey2,
		leaveSeersHouse, warmFrozenVase, finishQuest;

	ConditionalStep olafTask, sigliTask, manniTask, sigmundTask, thorvaldTask, swensenTask, peerTask;

	Zone nearPipe, koscheiRoom, swensenRoom1, swensenRoom2, swensenRoom3, swensenRoom4, swensenRoom5, swensenRoom6, swensenRoom7, swensenFinalRoom, swensenArea, peerEntrance, peerUpstairs, peerExit;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToBrundt);

		olafTask = new ConditionalStep(this, talkToOlaf);
		olafTask.addStep(hasEnchantedLyre, performMusic);
		olafTask.addStep(new Conditions(hasStartedOlaf, hasLyre), enchantLyre);
		olafTask.addStep(new Conditions(hasGoldenWool, hasLyreUnstrung), makeLyre);
		olafTask.addStep(new Conditions(hasGoldenFleece, hasLyreUnstrung), spinWool);
		olafTask.addStep(new Conditions(hasGoldenFleece, hasBranch), fletchLyre);
		olafTask.addStep(hasGoldenFleece, chopSwayingTree);
		olafTask.addStep(new Conditions(gottenRock, onionInCauldron, cabbageInCauldron, potatoInCauldron, petRockInCauldron), talkToLaliAfterStew);
		olafTask.addStep(new Conditions(gottenRock, onionInCauldron, cabbageInCauldron, potatoInCauldron), useRock);
		olafTask.addStep(new Conditions(gottenRock, onionInCauldron, cabbageInCauldron), usePotato);
		olafTask.addStep(new Conditions(gottenRock, onionInCauldron), useCabbage);
		olafTask.addStep(gottenRock, useOnion);
		olafTask.addStep(new Conditions(hasStartedOlaf, talkedToLalli), talkToAskeladdenForRock);
		olafTask.addStep(hasStartedOlaf, talkToLalli);
		olafTask.setLockingCondition(finishedOlafTask);

		manniTask = new ConditionalStep(this, talkToManni);
		manniTask.addStep(new Conditions(hasReplacedBeer, hasKegOfBeer), cheatInBeerDrinking);
		manniTask.addStep(new Conditions(hasReplacedBeer), getKegOfBeer);
		manniTask.addStep(new Conditions(hasPlacedStrangeObject, hasAlcoholFreeBeer, hasKegOfBeer), useAlcoholFreeOnKeg);
		manniTask.addStep(new Conditions(hasPlacedStrangeObject, hasAlcoholFreeBeer), getKegOfBeer);
		manniTask.addStep(new Conditions(hasPlacedStrangeObject), getAlcoholFreeBeer);
		manniTask.addStep(new Conditions(talkedToManni, hasLitStrangeObject, hasAlcoholFreeBeer, isNearPipe), useStrangeObjectOnPipe);
		manniTask.addStep(new Conditions(talkedToManni, hasStrangeObject, hasAlcoholFreeBeer, isNearPipe), useStrangeObject);
		manniTask.addStep(new Conditions(talkedToManni, hasStrangeObject, hasAlcoholFreeBeer), prepareToUseStrangeObject);
		manniTask.addStep(new Conditions(talkedToManni, hasStrangeObject), getAlcoholFreeBeer);
		manniTask.addStep(new Conditions(talkedToManni, hasBeer), getStrangeObject);
		manniTask.addStep(talkedToManni, pickUpBeer);
		manniTask.setLockingCondition(finishedManniTask);

		sigliTask = new ConditionalStep(this, talkToSigli);
		sigliTask.addStep(hasChargedHuntersTalisman, returnToSigli);
		sigliTask.addStep(hasHuntersTalisman, huntDraugen);
		sigliTask.setLockingCondition(finishedSigliTask);

		sigmundTask = new ConditionalStep(this, talkToSigmund);
		sigmundTask.addStep(hasExoticFlower, bringExoticFlowerToSigmund);
		sigmundTask.addStep(hasBallad, bringBalladToSailor);
		sigmundTask.addStep(hasSturdyBoots, bringSturdyBootsToOlaf);
		sigmundTask.addStep(hasFiscalStatement, bringFiscalStatementToYsra);
		sigmundTask.addStep(hasTrackingMap, bringTrackingMapToBrundt);
		sigmundTask.addStep(hasCustomBowString, bringCustomBowStringToSigli);
		sigmundTask.addStep(hasUnusualFish, bringUnusualFishToSkulgrimen);
		sigmundTask.addStep(hasSeaFishingMap, bringSeaFishingMapToFisherman);
		sigmundTask.addStep(hasWeatherForecast, bringWeatherForecastToSwensen);
		sigmundTask.addStep(hasWarriorsContract, bringWarriorsContractToPeer);
		sigmundTask.addStep(hasChampionsToken, bringChampionsTokenToThorvald);
		sigmundTask.addStep(hasLegendaryCocktail, bringCocktailToManni);
		sigmundTask.addStep(hasPromissoryNote, bringNoteToThora);

		sigmundTask.addStep(new Conditions(talkedToThoraForSigmund, noRockAskeladdenNearby), talkToAskeladdenForSigmund2);
		sigmundTask.addStep(talkedToThoraForSigmund, talkToAskeladdenForSigmund);
		sigmundTask.addStep(talkedToManniForSigmund, talkToThoraForSigmund);
		sigmundTask.addStep(talkedToThorvaldForSigmund, talkToManniForSigmund);
		sigmundTask.addStep(talkedToPeerForSigmund, talkToThorvaldForSigmund);
		sigmundTask.addStep(talkedToSwensenForSigmund, talkToPeerForSigmund);
		sigmundTask.addStep(talkedToFishermanForSigmund, talkToSwenesenForSigmund);
		sigmundTask.addStep(talkedToSkulgrimenForSigmund, talkToFishermanForSigmund);
		sigmundTask.addStep(talkedToSigliForSigmund, talkToSkulgrimenForSigmund);
		sigmundTask.addStep(talkedToBrundtForSigmund, talkToSigliForSigmund);
		sigmundTask.addStep(talkedToYsra, talkToBrundtForSigmund);
		sigmundTask.addStep(talkedToOlafForSigmund, talkToYsra);
		sigmundTask.addStep(talkedToSailor, talkToOlafForSigmund);
		sigmundTask.addStep(getFlower, talkToSailor);
		sigmundTask.setLockingCondition(finishedSigmundTask);

		thorvaldTask = new ConditionalStep(this, talkToThorvald);
		thorvaldTask.addStep(koschei4Near, killKoschei4);
		thorvaldTask.addStep(koschei3Near, killKoschei3);
		thorvaldTask.addStep(koschei2Near, killKoschei2);
		thorvaldTask.addStep(koschei1Near, killKoschei1);
		thorvaldTask.addStep(inKoscheiRoom, waitForKoschei);
		thorvaldTask.addStep(talkedToThorvald, goDownLadderToKoschei);
		thorvaldTask.setLockingCondition(finishedThorvaldTask);

		swensenTask = new ConditionalStep(this, talkToSwensen);
		swensenTask.addStep(inSwensenFinalRoom, swensenUpLadder);
		swensenTask.addStep(inSwensenRoom7, swensen7North);
		swensenTask.addStep(inSwensenRoom6, swensen6East);
		swensenTask.addStep(inSwensenRoom5, swensen5South);
		swensenTask.addStep(inSwensenRoom4, swensen4North);
		swensenTask.addStep(inSwensenRoom3, swensen3East);
		swensenTask.addStep(inSwensenRoom2, swensen2West);
		swensenTask.addStep(inSwensenRoom1, swensen1South);
		swensenTask.addStep(inSwensenArea, resetSwensen);
		swensenTask.addStep(talkedToSwensen, goDownLadderSwensen);
		swensenTask.setLockingCondition(finishedSwensenTask);

		peerTask = new ConditionalStep(this, talkToPeer);
		peerTask.addStep(new Conditions(inPeerExit, hasSeersKey), leaveSeersHouse);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasSeersKey, trapDoorOpen), goDownstairsWithKey2);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasSeersKey), goDownstairsWithKey);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasFrozenKey), useFrozenKeyOnRange);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasFilledVaseWithLid), useVaseOnTable);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasFilledVase), useLidOnVase);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasVase), fillVase);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseWithLidWrong), takeLidOff);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasBucket45), useBucketOnScale);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasFullBucket, hasJug23), useBucketOnJug3);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasEmptyBucket, hasJug23), useBucketOnTap2);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasBucket25, hasEmptyJug), useBucketOnJug2);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasBucket25, hasFullJug), useJugOnDrain1);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasFullBucket, hasEmptyJug), useBucketOnJug1);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasEmptyBucket, hasEmptyJug), useBucketOnTap1);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasAnyBucket, hasAnyJug), emptyJugAndBucket);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasAnyBucket, chestOpen), searchChest2);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, hasAnyBucket), searchChest1);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid, cupboardOpen), searchCupboard2);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasVaseLid), searchCupboard1);
		peerTask.addStep(new Conditions(inPeerExit, hasVaseLid), goUpstairsWithVaseLid);
		peerTask.addStep(new Conditions(inPeerExit, hasRedDiskNew, hasUsedDisk), useDiskNewOnMural);
		peerTask.addStep(new Conditions(inPeerExit, hasRedDiskOld, hasUsedDisk), useDiskOldOnMural);
		peerTask.addStep(new Conditions(inPeerExit, hasRedDiskOld, hasRedDiskNew), useDiskAnyOnMural);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasRedDiskOld, hasRedDiskNew, trapDoorOpen), goDown1);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasRedDiskOld, hasRedDiskNew), openTrapDoorAndGoDown1);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasStickyRedGoop, hasWoodenDisk, hasRedDiskOld), useGoopOnDisk);
		peerTask.addStep(new Conditions(inPeerUpstairs, hasRedHerring, hasWoodenDisk, hasRedDiskOld), cookHerring);
		peerTask.addStep(new Conditions(inPeerUpstairs, new Conditions(LogicType.OR, hasRedDiskNew, new Conditions(hasRedHerring, hasWoodenDisk))), searchUnicorn);
		peerTask.addStep(new Conditions(inPeerUpstairs, new Conditions(LogicType.OR, hasStickyRedGoop, hasRedHerring)), searchBull);
		peerTask.addStep(inPeerUpstairs, searchBookcase);
		peerTask.addStep(inPeerExit, goBackUpstairs);
		peerTask.addStep(new Conditions(LogicType.OR, inPeerEntrance, hasSolvedDoor), goUpEntranceLadderPeer);
		peerTask.addStep(isWind, inputWind);
		peerTask.addStep(isTime, inputTime);
		peerTask.addStep(isFire, inputFire);
		peerTask.addStep(isLife, inputLife);
		peerTask.addStep(isTree, inputTree);
		peerTask.addStep(isMind, inputMind);
		peerTask.addStep(talkedToPeer, enterPeerHouse);
		peerTask.setLockingCondition(finishedPeerTask);

		DetailedQuestStep resyncStep = new DetailedQuestStep(this, "Please open the quest's Quest Journal to sync your state.");
		ConditionalStep resyncCondition = new ConditionalStep(this, resyncStep);
		resyncCondition.setLockingCondition(syncedAll);

		ConditionalStep trials = new ConditionalStep(this, resyncCondition);
		trials.addStep(new Conditions(syncedPeer, finishedOlafTask, finishedManniTask, finishedSigliTask, finishedSigmundTask, finishedThorvaldTask, finishedSwensenTask), peerTask);
		trials.addStep(new Conditions(syncedSwensen, finishedOlafTask, finishedManniTask, finishedSigliTask, finishedSigmundTask, finishedThorvaldTask), swensenTask);
		trials.addStep(new Conditions(syncedThorvald, finishedOlafTask, finishedManniTask, finishedSigliTask, finishedSigmundTask), thorvaldTask);
		trials.addStep(new Conditions(syncedSigmund, finishedOlafTask, finishedManniTask, finishedSigliTask), sigmundTask);
		trials.addStep(new Conditions(finishedOlafTask, finishedManniTask), sigliTask);
		trials.addStep(new Conditions(syncedManni, finishedOlafTask), manniTask);
		trials.addStep(syncedOlaf, olafTask);
		steps.put(1, trials);
		steps.put(2, trials);
		steps.put(3, trials);
		steps.put(4, trials);
		steps.put(5, trials);
		steps.put(6, trials);
		steps.put(7, trials);
		steps.put(8, finishQuest);
		return steps;
	}

	public void setupItemRequirements()
	{
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 5250);
		coins250 = new ItemRequirement("Coins", ItemID.COINS_995, 250);
		coins5000 = new ItemRequirement("Coins", ItemID.COINS_995, 5000);
		beer = new ItemRequirement(true, "Beer", ItemID.BEER);
		beer.addAlternates(ItemID.BEER_TANKARD);
		rawShark = new ItemRequirement("Raw shark, manta ray or sea turtle", ItemID.RAW_SHARK);
		rawShark.addAlternates(ItemID.RAW_MANTA_RAY, ItemID.RAW_SEA_TURTLE);

		dramenBranch = new ItemRequirement("Dramen branch for Koschei", ItemID.DRAMEN_BRANCH);
		ringsOfRecoil = new ItemRequirement("Rings of recoil for Koschei", ItemID.RING_OF_RECOIL, -1);
		camelotTeleport = new ItemRequirement("Camelot teleport", ItemID.CAMELOT_TELEPORT);
		rellekkaTeleports = new ItemRequirement("Teleports to Rellekka", ItemID.RELLEKKA_TELEPORT);

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		potato = new ItemRequirement("Potato", ItemID.POTATO);
		onion = new ItemRequirement("Onion", ItemID.ONION);
		cabbage = new ItemRequirement("Cabbage", ItemID.CABBAGE);
		branch = new ItemRequirement("Branch", ItemID.BRANCH);
		lyreUnstrung = new ItemRequirement("Unstrung lyre", ItemID.UNSTRUNG_LYRE);
		lyre = new ItemRequirement("Lyre", ItemID.LYRE);
		petRock = new ItemRequirement("Pet rock", ItemID.PET_ROCK);
		petRock.setTip("You can get another from Askeladden");

		goldenWool = new ItemRequirement("Golden wool", ItemID.GOLDEN_WOOL);
		goldenFleece = new ItemRequirement("Golden fleece", ItemID.GOLDEN_FLEECE);

		enchantedLyre = new ItemRequirement("Enchanted lyre", ItemID.ENCHANTED_LYRE);
		strangeObject = new ItemRequirement("Strange object", ItemID.STRANGE_OBJECT);
		litStrangeObject = new ItemRequirement("Lit strange object", ItemID.LIT_STRANGE_OBJECT);
		alcoholFreeBeer = new ItemRequirement("Low alcohol keg", ItemID.LOW_ALCOHOL_KEG);
		kegOfBeer = new ItemRequirement("Keg of beer", ItemID.KEG_OF_BEER);

		combatGear = new ItemRequirement("Combat gear + food", -1, -1);
		huntersTalisman = new ItemRequirement("Hunters' talisman", ItemID.HUNTERS_TALISMAN_3697);
		chargedHuntersTalisman = new ItemRequirement("Hunters' talisman", ItemID.HUNTERS_TALISMAN);
		promissoryNote = new ItemRequirement("Promissory note", ItemID.PROMISSORY_NOTE);
		legendaryCocktail = new ItemRequirement("Legendary cocktail", ItemID.LEGENDARY_COCKTAIL);
		championsToken = new ItemRequirement("Champions token", ItemID.CHAMPIONS_TOKEN);
		warriorsContract = new ItemRequirement("Warriors' contract", ItemID.WARRIORS_CONTRACT);
		weatherForecast = new ItemRequirement("Weather forecast", ItemID.WEATHER_FORECAST);
		seaFishingMap = new ItemRequirement("Sea fishing map", ItemID.SEA_FISHING_MAP);
		unusualFish = new ItemRequirement("Unusual fish", ItemID.UNUSUAL_FISH);
		customBowString = new ItemRequirement("Custom bow string", ItemID.CUSTOM_BOW_STRING);
		trackingMap = new ItemRequirement("Tracking map", ItemID.TRACKING_MAP);
		fiscalStatement = new ItemRequirement("Fiscal statement", ItemID.FISCAL_STATEMENT);
		sturdyBoots = new ItemRequirement("Sturdy boots", ItemID.STURDY_BOOTS);
		ballad = new ItemRequirement("Fremennik ballad", ItemID.FREMENNIK_BALLAD);
		exoticFlower = new ItemRequirement("Exotic flower", ItemID.BALLAD);
		koscheiGear = new ItemRequirement("Nothing except for food, potions, and rings of recoil.", -1, -1);
		optionalKoscheiGear = new ItemRequirement("Optionally a Dramen branch + knife to make a dramen staff once inside Koschei's arena", -1, -1);
		redHerring = new ItemRequirement(true, "Red herring", ItemID.RED_HERRING);
		woodenDisk = new ItemRequirement(true, "Wooden disk", ItemID.WOODEN_DISK);
		redDiskOld = new ItemRequirement(true, "Old red disk", ItemID.OLD_RED_DISK);
		redDiskNew = new ItemRequirement(true, "Red disk", ItemID.RED_DISK_3743);
		stickyRedGoop = new ItemRequirement(true, "Sticky red goop", ItemID.STICKY_RED_GOOP);
		vase = new ItemRequirement(true, "Vase", ItemID.VASE_3734);
		vaseLid = new ItemRequirement(true, "Vase lid", ItemID.VASE_LID);
		emptyJug = new ItemRequirement(true, "Empty jug", ItemID.EMPTY_JUG);
		fullJug = new ItemRequirement(true, "Full jug", ItemID.FULL_JUG);
		jug13 = new ItemRequirement(true, "1/3rds full jug", ItemID._13RDS_FULL_JUG);
		jug23 = new ItemRequirement(true, "2/3rds full jug", ItemID._23RDS_FULL_JUG);
		emptyBucket = new ItemRequirement(true, "Empty bucket", ItemID.EMPTY_BUCKET);
		fullBucket = new ItemRequirement(true, "Full bucket", ItemID.FULL_BUCKET);
		bucket45 = new ItemRequirement(true, "4/5ths full bucket", ItemID._45THS_FULL_BUCKET);
		bucket35 = new ItemRequirement(true, "3/5ths full bucket", ItemID._35THS_FULL_BUCKET);
		bucket25 = new ItemRequirement(true, "2/5ths full bucket", ItemID._25THS_FULL_BUCKET);
		bucket15 = new ItemRequirement(true, "1/5ths full bucket", ItemID._15THS_FULL_BUCKET);
		filledVase = new ItemRequirement(true, "Vase of water", ItemID.VASE_OF_WATER);
		filledVaseWithLid = new ItemRequirement(true, "Sealed vase", ItemID.SEALED_VASE_3740);
		vaseWithLidWrong = new ItemRequirement(true, "Sealed vase", ItemID.SEALED_VASE);
		frozenKey = new ItemRequirement(true, "Frozen key", ItemID.FROZEN_KEY);
		seersKey = new ItemRequirement("Seer's key", ItemID.SEERS_KEY);
		frozenVase = new ItemRequirement(true, "Frozen vase", ItemID.FROZEN_VASE);
	}

	public void loadZones()
	{
		nearPipe = new Zone(new WorldPoint(2663, 3671, 0), new WorldPoint(2667, 3676, 0));
		koscheiRoom = new Zone(new WorldPoint(2641, 10064, 2), new WorldPoint(2672, 10099, 2));

		swensenRoom1 = new Zone(new WorldPoint(2626, 10001, 0), new WorldPoint(2634, 10008, 0));
		swensenRoom2 = new Zone(new WorldPoint(2639, 10012, 0), new WorldPoint(2645, 10018, 0));
		swensenRoom3 = new Zone(new WorldPoint(2650, 10001, 0), new WorldPoint(2656, 10007, 0));
		swensenRoom4 = new Zone(new WorldPoint(2662, 10012, 0), new WorldPoint(2668, 10018, 0));
		swensenRoom5 = new Zone(new WorldPoint(2627, 10023, 0), new WorldPoint(2633, 10029, 0));
		swensenRoom6 = new Zone(new WorldPoint(2650, 10034, 0), new WorldPoint(2656, 10040, 0));
		swensenRoom7 = new Zone(new WorldPoint(2662, 10022, 0), new WorldPoint(2670, 10030, 0));
		swensenFinalRoom = new Zone(new WorldPoint(2661, 10034, 0), new WorldPoint(2670, 10043, 0));
		swensenArea =  new Zone(new WorldPoint(2626, 10001, 0), new WorldPoint(2674, 10044, 0));

		peerEntrance = new Zone(new WorldPoint(2629, 3659, 0), new WorldPoint(2633, 3666, 0));
		peerUpstairs = new Zone(new WorldPoint(2629, 3659, 2), new WorldPoint(2638, 3666, 2));
		peerExit = new Zone(new WorldPoint(2634, 3659, 0), new WorldPoint(2638, 3666, 0));
	}

	public void setupConditions()
	{
		inQuestJournal = new WidgetTextCondition(WidgetInfo.DIARY_QUEST_WIDGET_TITLE, "The Fremennik Trials");

		synced = new Conditions(true, LogicType.OR, inQuestJournal,
			new WidgetTextCondition(217, 4, true, true, "I think I would enjoy the challenge"));

		hasStartedOlaf = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "That is great news outerlander! We always need more music lovers here!"),
			new WidgetTextCondition(119, 3, true,  "Bard<col=000080> will vote for me if"));

		syncedOlaf = new Conditions(true, LogicType.AND, synced, hasStartedOlaf);

		hasBranch = new ItemRequirementCondition(branch);
		hasLyreUnstrung = new ItemRequirementCondition(lyreUnstrung);
		hasLyre = new ItemRequirementCondition(lyre);

		talkedToLalli = new WidgetTextCondition(217, 4, false, true, "I see... okay, well, bye!");
		gottenRock = new VarbitCondition(6486, 1);
		hasPetRock = new ItemRequirementCondition(petRock);
		hasOnion = new ItemRequirementCondition(onion);
		hasCabbage = new ItemRequirementCondition(cabbage);
		hasPotato = new ItemRequirementCondition(potato);

		petRockInCauldron = new ChatMessageCondition("You put your pet rock into the cauldron.");
		cabbageInCauldron = new ChatMessageCondition("You put a cabbage into the cauldron.");
		potatoInCauldron = new ChatMessageCondition("You put a potato into the cauldron.");
		onionInCauldron = new ChatMessageCondition("You put an onion into the cauldron.");
		cauldronFilledDialog = new WidgetTextCondition(217, 4, "Indeed it is. Try it and see.");

		stewReady = new Conditions(new Conditions(petRockInCauldron, cabbageInCauldron, potatoInCauldron, onionInCauldron), cauldronFilledDialog);

		hasGoldenFleece = new ItemRequirementCondition(goldenFleece);
		hasGoldenWool = new ItemRequirementCondition(goldenWool);
		hasEnchantedLyre = new ItemRequirementCondition(enchantedLyre);

		finishedOlafMessage = new ChatMessageCondition("Congratulations! You have completed the Bard's Trial!");
		finishedOlafWidget = new WidgetTextCondition(119, 3, true, true, "I now have the Bard's vote");
		finishedOlafTask = new Conditions(true, LogicType.OR, finishedOlafMessage, finishedOlafWidget);

		hasBeer = new ItemRequirementCondition(beer);

		talkedToManniWidget = new WidgetTextCondition(119, 3, true, true,
			"Reveller<col=000080> will vote for me");
		talkedToManniChat = new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "pick up a keg from that table over there");
		talkedToManni = new Conditions(true, LogicType.OR, talkedToManniWidget, talkedToManniChat);

		syncedManni = new Conditions(true, LogicType.OR, talkedToManni);

		hasStrangeObject = new ItemRequirementCondition(strangeObject);
		hasLitStrangeObject = new ItemRequirementCondition(litStrangeObject);
		hasAlcoholFreeBeer = new ItemRequirementCondition(alcoholFreeBeer);
		isNearPipe = new ZoneCondition(nearPipe);

		hasPlacedStrangeObject = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(217, 4,"That is going to make a really loud bang"));

		hasReplacedBeer = new Conditions(true, LogicType.AND, new ChatMessageCondition("You empty the keg and refill it with low alcohol beer."));
		finishedManniTask = new Conditions(true, LogicType.OR,
			new ChatMessageCondition("Congratulations! You have completed the Reveller's Trial!"),
			new WidgetTextCondition(119, 3, true, true, "I now have the Reveller's vote"));

		hasKegOfBeer = new ItemRequirementCondition(kegOfBeer);

		hasHuntersTalisman = new ItemRequirementCondition(huntersTalisman);

		hasChargedHuntersTalisman = new ItemRequirementCondition(chargedHuntersTalisman);

		finishedSigliTask = new Conditions(true, LogicType.OR,
			new ChatMessageCondition("Congratulations! You have completed the Hunter's Trial!"),
			new WidgetTextCondition(119, 3, true, true, "I now have the Hunter's vote"));

		getFlower = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "We are a very insular clan"),
			new WidgetTextCondition(119, 3, true, true, "has a <col=800000>rare flower<col=000080> that he wants."));

		talkedToSailor = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "That sounds like a fair deal to me, outerlander."),
			new WidgetTextCondition(217, 4, false, true, "find a<br>love ballad, do you?"));

		talkedToOlafForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "composing you a romantic ballad"),
			new WidgetTextCondition(217, 4, false, true, "where I could find<br>some custom sturdy boots, do you?"));

		Conditions ysraAsked = new Conditions(true, LogicType.AND, new WidgetTextCondition(217, 4, true, "Okay, I will see what I can do."),
			new ZoneCondition(new Zone(new WorldPoint(2622, 3672, 0), new WorldPoint(2629, 3679, 0))));

		talkedToYsra = new Conditions(true, LogicType.OR,
			ysraAsked,
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>shopkeeper<col=000080> is looking for a <col=800000>tax reduction<col=000080>..."));

		talkedToBrundtForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "Speak to Sigli then, and you may have my promise to<br>reduce our sales taxes. And best of luck with the rest<br>of your trials."),
			new WidgetTextCondition(119, 3, true, true, "The <col=800000>chieftain<col=000080> wants a <col=800000>map of new hunting grounds<col=000080>..."));

		talkedToSigliForSigmund = new Conditions(true, LogicType.OR,
			// TODO: Fix this check, missing a br
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "who knows where<br>my hunting ground is."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The hunter<col=000080> is looking for a <col=800000>custom bow string<col=000080>..."));

		talkedToSkulgrimenForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(217, 4, "Sounds good to me."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>armourer<col=000080> is looking for a <col=800000>rare inedible fish<col=000080>..."));

		talkedToFishermanForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(217, 4, true, true, "I'll see what I can do."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>fisherman<col=000080> is looking for a <col=800000>map of fishing spots<col=000080>..."));

		talkedToSwensenForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, " take the time to make a forecast<br>somehow."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>navigator<col=000080> is looking for a <col=800000>weather forecast<col=000080>..."));
		talkedToPeerForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, "That is all."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>seer<col=000080> is looking for a <col=800000>warrior to be his bodyguard<col=000080>..."));
		Conditions thorvaldAsked = new Conditions(true, LogicType.AND, new WidgetTextCondition(217, 4,  "Okay, I'll see what I can do."),
			new ZoneCondition(new Zone(new WorldPoint(2661, 3690, 0), new WorldPoint(2669, 3696, 0))));
		talkedToThorvaldForSigmund = new Conditions(true, LogicType.OR,
			thorvaldAsked,
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>warrior<col=000080> is looking for a <col=800000>champions token<col=000080>..."));
		talkedToManniForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, "That's all."),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>The <col=800000>reveller<col=000080> is looking for a <col=800000>legendary cocktail<col=000080>..."));
		talkedToThoraForSigmund = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(217, 4, true, "cash. You should go ask him"),
			new WidgetTextCondition(119, 3, true, true, "<col=000080>All <col=800000>Askeladden<col=000080> wants is <col=800000>some money<col=000080>!"));

		finishedSigmundTask = new Conditions(true, LogicType.OR,
			new ChatMessageCondition("Congratulations! You have completed the Merchant's Trial!"),
			new WidgetTextCondition(119, 3, true, true, "I now have the Merchant's vote"));

		syncedSigmund = new Conditions(LogicType.OR, getFlower, talkedToSailor, talkedToOlafForSigmund, talkedToYsra, talkedToBrundtForSigmund, talkedToSigliForSigmund, talkedToSkulgrimenForSigmund,
			talkedToFishermanForSigmund, talkedToSwensenForSigmund, talkedToPeerForSigmund, talkedToThorvaldForSigmund, talkedToManniForSigmund, talkedToThoraForSigmund);

		talkedToAskeladdenForSigmund = new ItemRequirementCondition(promissoryNote);
		noRockAskeladdenNearby = new NpcCondition(NpcID.ASKELADDEN);

		hasPromissoryNote = new ItemRequirementCondition(promissoryNote);
		hasLegendaryCocktail = new ItemRequirementCondition(legendaryCocktail);
		hasChampionsToken = new ItemRequirementCondition(championsToken);
		hasWarriorsContract = new ItemRequirementCondition(warriorsContract);
		hasWeatherForecast = new ItemRequirementCondition(weatherForecast);
		hasSeaFishingMap = new ItemRequirementCondition(seaFishingMap);
		hasUnusualFish = new ItemRequirementCondition(unusualFish);
		hasCustomBowString = new ItemRequirementCondition(customBowString);
		hasTrackingMap = new ItemRequirementCondition(trackingMap);
		hasFiscalStatement = new ItemRequirementCondition(fiscalStatement);
		hasSturdyBoots = new ItemRequirementCondition(sturdyBoots);
		hasBallad = new ItemRequirementCondition(ballad);
		hasExoticFlower = new ItemRequirementCondition(exoticFlower);

		inKoscheiRoom = new ZoneCondition(koscheiRoom);
		talkedToThorvald = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "Hahahahaha! I'm beginning"),
			new WidgetTextCondition(119, 3, true, true, "Warrior<col=000080> will vote for me if"));

		koschei1Near = new NpcCondition(NpcID.KOSCHEI_THE_DEATHLESS);
		koschei2Near = new NpcCondition(NpcID.KOSCHEI_THE_DEATHLESS_3898);
		koschei3Near = new NpcCondition(NpcID.KOSCHEI_THE_DEATHLESS_3899);
		koschei4Near = new NpcCondition(NpcID.KOSCHEI_THE_DEATHLESS_3900);

		syncedThorvald = new Conditions(talkedToThorvald);

		finishedThorvaldTask = new Conditions(true, LogicType.OR,
			new ChatMessageCondition("Congratulations! You have completed the Warrior's Trial!"),
			new WidgetTextCondition(119, 3, true, true, "I now have the Warrior's vote"));

		talkedToSwensen = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(217, 4, true, "A maze? Is that all?"),
			new WidgetTextCondition(119, 3, true, true, "Navigator<col=000080> will vote for me if"));

		inSwensenRoom1 = new ZoneCondition(swensenRoom1);
		inSwensenRoom2 = new ZoneCondition(swensenRoom2);
		inSwensenRoom3 = new ZoneCondition(swensenRoom3);
		inSwensenRoom4 = new ZoneCondition(swensenRoom4);
		inSwensenRoom5 = new ZoneCondition(swensenRoom5);
		inSwensenRoom6 = new ZoneCondition(swensenRoom6);
		inSwensenRoom7 = new ZoneCondition(swensenRoom7);
		inSwensenFinalRoom = new ZoneCondition(swensenFinalRoom);
		inSwensenArea = new ZoneCondition(swensenArea);

		syncedSwensen = new Conditions(talkedToSwensen);
		finishedSwensenTask = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "Outerlander! You have finished my maze!"),
			new WidgetTextCondition(119, 3, true, true, "I now have the Navigator's vote"));

		/* Peer Task */

		talkedToPeer = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(217, 4, true, "I have one small question"),
			new WidgetTextCondition(119, 3, true, true, "Seer<col=000080> will vote for me if"));

		isMind = new WidgetTextCondition(229, 1, false, true, "My first is in mage");
		isTree = new WidgetTextCondition(229, 1, false, true, "My first is in tar");
		isLife = new WidgetTextCondition(229, 1, false, true, "My first is in well");
		isFire = new WidgetTextCondition(229, 1, false, true, "My first is in fish");
		isTime = new WidgetTextCondition(229, 1, false, true, "My first is in water");
		isWind = new WidgetTextCondition(229, 1, false, true, "My first is in wizard");

		inPeerEntrance = new ZoneCondition(peerEntrance);
		inPeerUpstairs = new ZoneCondition(peerUpstairs);
		inPeerExit = new ZoneCondition(peerExit);
		hasSolvedDoor = new ChatMessageCondition("You have solved the riddle!");

		hasRedHerring = new ItemRequirementCondition(redHerring);
		hasWoodenDisk = new ItemRequirementCondition(woodenDisk);
		hasRedDiskOld = new ItemRequirementCondition(redDiskOld);
		hasRedDiskNew = new ItemRequirementCondition(redDiskNew);
		hasStickyRedGoop = new ItemRequirementCondition(stickyRedGoop);

		cupboardOpen = new ObjectCondition(ObjectID.CUPBOARD_4178);
		chestOpen = new ObjectCondition(ObjectID.CHEST_4168, new WorldPoint(2635, 3660, 2));
		trapDoorOpen = new ObjectCondition(ObjectID.TRAPDOOR_4173, new WorldPoint(2636, 3663, 2));

		/* Currently does not capture the case a user uses a disk, then logs out */
		hasUsedDisk = new ChatMessageCondition("You put the red disk into the empty hole on the mural.", "You've already put the red disk into the empty hole on the mural.");
		muralHasDisks = new ObjectCondition(ObjectID.ABSTRACT_MURAL_4180);
		hasEmptyBucket = new ItemRequirementCondition(emptyBucket);
		hasFullBucket = new ItemRequirementCondition(fullBucket);
		hasBucket45 = new ItemRequirementCondition(bucket45);
		hasBucket35 = new ItemRequirementCondition(bucket35);
		hasBucket25 = new ItemRequirementCondition(bucket25);
		hasBucket15 = new ItemRequirementCondition(bucket15);
		hasAnyBucket = new Conditions(LogicType.OR, hasBucket15, hasBucket25, hasBucket35, hasBucket45, hasFullBucket, hasEmptyBucket);
		hasEmptyJug = new ItemRequirementCondition(emptyJug);
		hasFullJug = new ItemRequirementCondition(fullJug);
		hasJug23 = new ItemRequirementCondition(jug23);
		hasJug13 = new ItemRequirementCondition(jug13);
		hasAnyJug = new Conditions(LogicType.OR, hasJug13, hasJug23, hasEmptyJug, hasFullJug);
		hasVase = new ItemRequirementCondition(vase);
		hasVaseLid = new ItemRequirementCondition(vaseLid);
		hasFilledVase = new ItemRequirementCondition(filledVase);
		hasFilledVaseWithLid = new ItemRequirementCondition(filledVaseWithLid);
		hasVaseWithLidWrong = new ItemRequirementCondition(vaseWithLidWrong);
		hasFrozenVase = new ItemRequirementCondition(frozenVase);

		hasFrozenKey = new ItemRequirementCondition(frozenKey);
		hasSeersKey = new ItemRequirementCondition(seersKey);

		finishedPeerTask = new Conditions(true, LogicType.OR,
			new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, true, "To have solved my puzzle"),
			new WidgetTextCondition(119, 3, true, true, "I now have the Seer's vote"));

		syncedPeer = new Conditions(talkedToPeer);

		syncedAll = new Conditions(true, LogicType.OR, synced, new Conditions(syncedOlaf, syncedManni, syncedSigmund, syncedThorvald));
	}

	public void setupSteps()
	{
		talkToBrundt = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3669, 0), "Talk to Brundt in Rellekka's longhall.");
		talkToBrundt.addDialogStep("Ask about anything else.");
		talkToBrundt.addDialogStep("Do you have any quests?");
		talkToBrundt.addDialogStep("Yes, I am interested.");
		talkToBrundt.addDialogStep("I want to become a Fremennik!");

		/* Olaf Task */
		talkToOlaf = new NpcStep(this, NpcID.OLAF_THE_BARD, new WorldPoint(2673, 3683, 0),
			"Talk to Olaf the Bard north east of the longhall.");
		talkToOlaf.addDialogStep("Ask about becoming a Fremennik");
		talkToOlaf.addDialogStep("Yes");
		chopSwayingTree = new ObjectStep(this, ObjectID.SWAYING_TREE, new WorldPoint(2739, 3639, 0), "Go south east of Rellekka and cut a branch from the swaying tree", axe, knife);
		fletchLyre = new DetailedQuestStep(this, "Use a knife on the branch to make an unstrung lyre", knife, branch);
		talkToLalli = new NpcStep(this, NpcID.LALLI, new WorldPoint(2771, 3621, 0), "Talk to Lalli to the south east of Rellekka.");
		talkToLalli.addDialogStep("Other human?");
		talkToAskeladdenForRock = new NpcStep(this, NpcID.ASKELADDEN, new WorldPoint(2658, 3660, 0), "Return to Rellekka and talk to Askeladden for a pet rock.");
		talkToAskeladdenForRock.addDialogStep("Ask about becoming a Fremennik");
		pickVeg = new DetailedQuestStep(this, new WorldPoint(2677, 3652, 0), "Pick a potato, cabbage and onion from the field in south east Rellekka.", cabbage, onion, potato);

		useOnion = new ObjectStep(this, ObjectID.LALLIS_STEW, new WorldPoint(2773, 3624, 0),
			"Return to Lalli and use a onion, cabbage, potato, and pet rock on their stew. You can find the vegetables growing in the south east of Rellekka.", petRock, cabbage, potato, onion);
		useOnion.addIcon(ItemID.ONION);
		useCabbage = new ObjectStep(this, ObjectID.LALLIS_STEW, new WorldPoint(2773, 3624, 0),
			"Use a cabbage on the stew.", petRock, cabbage, potato);
		useCabbage.addIcon(ItemID.CABBAGE);
		usePotato = new ObjectStep(this, ObjectID.LALLIS_STEW, new WorldPoint(2773, 3624, 0),
			"Use a potato on the stew.", petRock, potato);
		usePotato.addIcon(ItemID.POTATO);
		useRock =  new ObjectStep(this, ObjectID.LALLIS_STEW, new WorldPoint(2773, 3624, 0),
			"Use your pet rock on the stew.", petRock);
		useRock.addIcon(ItemID.PET_ROCK);
		useOnion.addSubSteps(useCabbage, usePotato, useRock);
		talkToLaliAfterStew =  new NpcStep(this, NpcID.LALLI, new WorldPoint(2771, 3621, 0), "Talk to Lalli for golden wool.");
		spinWool = new DetailedQuestStep(this, "Spin the golden fleece into wool using a spinning wheel. The closest wheel is in Seers' Village.", goldenFleece);
		makeLyre = new DetailedQuestStep(this, "Use the golden wool on your unstrung lyre.", lyreUnstrung, goldenWool);
		enchantLyre = new ObjectStep(this, ObjectID.STRANGE_ALTAR, new WorldPoint(2626, 3598, 0), "Bring your lyre to the Strange Altar south west of Rellekka, and use either a raw shark, raw sea turtle or raw manta ray on it.", lyre, rawShark);
		enchantLyre.addIcon(ItemID.RAW_SHARK);
		performMusic = new DetailedQuestStep(this, new WorldPoint(2658, 3683, 0), "Return to Rellekka and enter the longhall's stage to perform. Once on stage, play the enchanted lyre.", enchantedLyre);

		/* Manni Task */
		talkToManni = new NpcStep(this, NpcID.MANNI_THE_REVELLER, new WorldPoint(2658, 3675, 0), "Talk to Manni the Reveller in the Rellekka longhall.");
		talkToManni.addDialogStep("Ask about becoming a Fremennik");
		pickUpBeer = new DetailedQuestStep(this, new WorldPoint(2658, 3676, 0), "Get some beer. There is beer tankard in the Rellekka longhall.", beer);
		getStrangeObject = new NpcStep(this, NpcID.COUNCIL_WORKMAN, new WorldPoint(2654, 3593, 0), "Give the Council workman on the bridge south of Rellekka going to Seer's Village a beer.", beer);
		getStrangeObject.addIcon(ItemID.BEER_TANKARD);
		getAlcoholFreeBeer = new NpcStep(this, NpcID.POISON_SALESMAN, new WorldPoint(2695, 3491, 0), "Buy some alcohol free beer from the Poison Salesman in the Seer's Village pub.", coins250);
		getAlcoholFreeBeer.addDialogStep("Talk about the Fremennik Trials");
		getAlcoholFreeBeer.addDialogStep("Yes");

		prepareToUseStrangeObject = new DetailedQuestStep(this, new WorldPoint(2664, 3674, 0), "Return to Rellekka with the strange object, a tinderbox, and low alcohol beer.", strangeObject, tinderbox, alcoholFreeBeer);
		useStrangeObject = new ObjectStep(this, ObjectID.PIPE_4162, new WorldPoint(2663, 3674, 0), "Use a tinderbox on the strange object then use it on the nearby pipe.", strangeObject, tinderbox);
		useStrangeObjectOnPipe =  new ObjectStep(this, ObjectID.PIPE_4162, new WorldPoint(2663, 3674, 0), "Use the lit strange object on the nearby pipe!", litStrangeObject);
		useStrangeObjectOnPipe.addIcon(ItemID.LIT_STRANGE_OBJECT);
		useStrangeObject.addSubSteps(useStrangeObjectOnPipe);
		getKegOfBeer = new DetailedQuestStep(this, new WorldPoint(2660, 3676, 0), "Pick up a keg of beer in the longhall.", kegOfBeer);
		useAlcoholFreeOnKeg = new DetailedQuestStep(this, new WorldPoint(2658, 3673, 0), "Use the low alcohol beer on the keg of beer whilst in the longhall.", alcoholFreeBeer, kegOfBeer);

		cheatInBeerDrinking = new NpcStep(this, NpcID.MANNI_THE_REVELLER, new WorldPoint(2658, 3675, 0), "Talk to Manni the Reveller to cheat in the competition.", kegOfBeer);
		cheatInBeerDrinking.addDialogStep("Ask about becoming a Fremennik");
		cheatInBeerDrinking.addDialogStep("Yes");

		talkToSigli = new NpcStep(this, NpcID.SIGLI_THE_HUNTSMAN, new WorldPoint(2658, 3650, 0), "Talk to Sigli south of the longhall for a hunters' talisman.");
		talkToSigli.addDialogStep("What's a Draugen?");
		talkToSigli.addDialogStep("Yes");

		huntDraugen = new DetailedQuestStep(this, "Use the hunter's talisman to hunt down the Draugen.");

		returnToSigli = new NpcStep(this, NpcID.SIGLI_THE_HUNTSMAN, new WorldPoint(2658, 3650, 0), "Return the charged hunters' talisman to Sigli.", chargedHuntersTalisman);

		/* Sigmund Task */
		talkToSigmund = new NpcStep(this, NpcID.SIGMUND_THE_MERCHANT, new WorldPoint(2642, 3680, 0), "Talk to Sigmund the Merchant in the Rellekka market.");
		talkToSigmund.addDialogStep("Yes");

		talkToSailor = new NpcStep(this, NpcID.SAILOR_3936, new WorldPoint(2629, 3693, 0), "Talk to the Sailor on the docks.");
		talkToSailor.addDialogStep("Ask about the Merchant's trial");
		talkToOlafForSigmund = new NpcStep(this, NpcID.OLAF_THE_BARD, new WorldPoint(2673, 3683, 0), "Talk to Olaf the Bard east of the longhall.");
		talkToOlafForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToYsra = new NpcStep(this, NpcID.YRSA_3933, new WorldPoint(2624, 3675, 0), "Talk to Ysra in her shop west of the market.");
		talkToYsra.addDialogStep("Ask about the Merchant's trial");
		talkToBrundtForSigmund = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3669, 0), "Talk to Brundt in the longhall.");
		talkToBrundtForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToBrundtForSigmund.addDialogStep("Talk about anything else.");
		talkToSigliForSigmund = new NpcStep(this, NpcID.SIGLI_THE_HUNTSMAN, new WorldPoint(2658, 3650, 0), "Talk to Sigli south of the longhall.");
		talkToSigliForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToSkulgrimenForSigmund = new NpcStep(this, NpcID.SKULGRIMEN, new WorldPoint(2663, 3692, 0), "Talk to Skulgrimen north of the longhall.");
		talkToSkulgrimenForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToFishermanForSigmund = new NpcStep(this, NpcID.FISHERMAN, new WorldPoint(2641, 3699, 0), "Talk to the Fisherman on the pier north of the market.");
		talkToFishermanForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToSwenesenForSigmund = new NpcStep(this, NpcID.SWENSEN_THE_NAVIGATOR, new WorldPoint(2646, 3660, 0), "Talk to Swensen the Navigator in his hut south west of the longhall.");
		talkToSwenesenForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToPeerForSigmund = new NpcStep(this, NpcID.PEER_THE_SEER, new WorldPoint(2633, 3667, 0), "Talk to Peer the Seer south west of the market.");
		talkToPeerForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToThorvaldForSigmund = new NpcStep(this, NpcID.THORVALD_THE_WARRIOR, new WorldPoint(2666, 3693, 0), "Talk to Thorvald north of the longhall.");
		talkToThorvaldForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToManniForSigmund = new NpcStep(this, NpcID.MANNI_THE_REVELLER, new WorldPoint(2658, 3675, 0), "Talk to Manni the Reveller in the longhall.");
		talkToManniForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToThoraForSigmund = new NpcStep(this, NpcID.THORA_THE_BARKEEP, new WorldPoint(2662, 3673, 0), "Talk to Thora the Barkeep in the longhall.");
		talkToThoraForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToAskeladdenForSigmund2 = new NpcStep(this, NpcID.ASKELADDEN, new WorldPoint(2659, 3660, 0), "Talk to Askeladden south of the longhall.");
		talkToAskeladdenForSigmund2.addDialogStep("Ask about the Merchant's trial");
		talkToAskeladdenForSigmund2.addDialogStep("Yes");

		talkToAskeladdenForSigmund = new NpcStep(this, NpcID.ASKELADDEN_8403, new WorldPoint(2659, 3660, 0), "Talk to Askeladden south of the longhall.");
		talkToAskeladdenForSigmund.addDialogStep("Ask about the Merchant's trial");
		talkToAskeladdenForSigmund.addDialogStep("Yes");
		talkToAskeladdenForSigmund.addSubSteps(talkToAskeladdenForSigmund2);

		bringNoteToThora =  new NpcStep(this, NpcID.THORA_THE_BARKEEP, new WorldPoint(2662, 3673, 0), "Bring the promissory note to Thora the Barkeep in the longhall.", promissoryNote);
		bringNoteToThora.addDialogStep("Ask about the Merchant's trial");
		bringCocktailToManni =  new NpcStep(this, NpcID.MANNI_THE_REVELLER, new WorldPoint(2658, 3675, 0), "Bring the cocktail to Manni the Reveller in the longhall.", legendaryCocktail);
		bringCocktailToManni.addDialogStep("Ask about the Merchant's trial");
		bringChampionsTokenToThorvald = new NpcStep(this, NpcID.THORVALD_THE_WARRIOR, new WorldPoint(2666, 3693, 0), "Bring the champions token to Thorvald north of the longhall.", championsToken);
		bringChampionsTokenToThorvald.addDialogStep("Ask about the Merchant's trial");
		bringWarriorsContractToPeer = new NpcStep(this, NpcID.PEER_THE_SEER, new WorldPoint(2633, 3667, 0), "Bring the warriors' contract to Peer the Seer south west of the market.", warriorsContract);
		bringWarriorsContractToPeer.addDialogStep("Ask about the Merchant's trial");
		bringWeatherForecastToSwensen = new NpcStep(this, NpcID.SWENSEN_THE_NAVIGATOR, new WorldPoint(2646, 3660, 0), "Bring the weather forecast to Swensen the Navigator in his hut south west of the longhall.", weatherForecast);
		bringWeatherForecastToSwensen.addDialogStep("Ask about the Merchant's trial");
		bringSeaFishingMapToFisherman = new NpcStep(this, NpcID.FISHERMAN, new WorldPoint(2641, 3699, 0), "Bring the sea fishing map to the Fisherman on the peer north of the market.", seaFishingMap);
		bringSeaFishingMapToFisherman.addDialogStep("Ask about the Merchant's trial");
		bringUnusualFishToSkulgrimen = new NpcStep(this, NpcID.SKULGRIMEN, new WorldPoint(2663, 3692, 0), "Bring the unusual fish to Skulgrimen north of the longhall.", unusualFish);
		bringUnusualFishToSkulgrimen.addDialogStep("Ask about the Merchant's trial");
		bringCustomBowStringToSigli = new NpcStep(this, NpcID.SIGLI_THE_HUNTSMAN, new WorldPoint(2658, 3650, 0), "Bring the custom bow string to Sigli south of the longhall.", customBowString);
		bringCustomBowStringToSigli.addDialogStep("Ask about the Merchant's trial");
		bringTrackingMapToBrundt = new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3669, 0), "Bring the tracking map to Brundt in the longhall.", trackingMap);
		bringTrackingMapToBrundt.addDialogStep("Ask about the Merchant's trial");
		bringTrackingMapToBrundt.addDialogStep("Ask about anything else.");
		bringFiscalStatementToYsra = new NpcStep(this, NpcID.YRSA_3933, new WorldPoint(2624, 3675, 0), "Bring the fiscal statement to Ysra in her shop west of the market.", fiscalStatement);
		bringFiscalStatementToYsra.addDialogStep("Ask about the Merchant's trial");
		bringSturdyBootsToOlaf = new NpcStep(this, NpcID.OLAF_THE_BARD, new WorldPoint(2673, 3683, 0), "Bring the sturdy boots to Olaf the Bard east of the longhall.", sturdyBoots);
		bringSturdyBootsToOlaf.addDialogStep("Ask about the Merchant's trial");
		bringBalladToSailor = new NpcStep(this, NpcID.SAILOR_3936, new WorldPoint(2629, 3693, 0), "Bring the ballad to the Sailor on the docks.", ballad);
		bringBalladToSailor.addDialogStep("Ask about the Merchant's trial");
		bringExoticFlowerToSigmund = new NpcStep(this, NpcID.SIGMUND_THE_MERCHANT, new WorldPoint(2642, 3680, 0), "Finally, bring the exotic flower to Sigmund the Merchant in the Rellekka market.", exoticFlower);

		/* Thorvald Task */
		talkToThorvald = new NpcStep(this, NpcID.THORVALD_THE_WARRIOR, new WorldPoint(2666, 3693, 0), "Talk to Thorvald north of the longhall.");
		talkToThorvald.addDialogStep("Ask about becoming a Fremennik");
		bringChampionsTokenToThorvald.addDialogStep("Yes");
		goDownLadderToKoschei = new ObjectStep(this, ObjectID.LADDER_4187, new WorldPoint(2667, 3694, 0), "When you're ready to fight Koschei, go down the ladder.", koscheiGear, optionalKoscheiGear);
		waitForKoschei = new DetailedQuestStep(this, "Wait for Koschei the Deathless to spawn. If you brought a dramen branch you should now fletch it and equip the staff.");
		killKoschei1 = new NpcStep(this, NpcID.KOSCHEI_THE_DEATHLESS, new WorldPoint(2660, 10080, 2), "Fight Koschei the Deathless and 'kill' him at least 3 times. Dying to his fourth phase is a SAFE death.");
		killKoschei2 = new NpcStep(this, NpcID.KOSCHEI_THE_DEATHLESS, new WorldPoint(2660, 10080, 2), "Fight Koschei the Deathless and 'kill' him at least 3 times. Dying to his fourth phase is a SAFE death.");
		killKoschei3 = new NpcStep(this, NpcID.KOSCHEI_THE_DEATHLESS, new WorldPoint(2660, 10080, 2), "Fight Koschei the Deathless and 'kill' him at least 3 times. Dying to his fourth phase is a SAFE death.");
		killKoschei4 = new NpcStep(this, NpcID.KOSCHEI_THE_DEATHLESS, new WorldPoint(2660, 10080, 2), "You must now either die (SAFE DEATH), or defeat Koschei once more. Do not leave or you'll have to fight him again.");
		killKoschei1.addSubSteps(killKoschei2, killKoschei3, killKoschei4);

		/* Swensen Task */
		talkToSwensen = new NpcStep(this, NpcID.SWENSEN_THE_NAVIGATOR, new WorldPoint(2646, 3660, 0), "Talk to Swensen the Navigator in his hut south west of the longhall.");
		talkToSwensen.addDialogStep("Yes");
		talkToSwensen.addDialogStep("Ask about becoming a Fremennik");
		goDownLadderSwensen = new ObjectStep(this, ObjectID.LADDER_4158, new WorldPoint(2644, 3657, 0), "Climb down Swensen's ladder.");
		swensen1South = new ObjectStep(this, ObjectID.PORTAL_4150, new WorldPoint(2631, 10002, 0), "Go through the south portal.");
		swensen2West = new ObjectStep(this, ObjectID.PORTAL_4151, new WorldPoint(2639, 10015, 0), "Go through the west portal.");
		swensen3East = new ObjectStep(this, ObjectID.PORTAL_4152, new WorldPoint(2656, 10004, 0), "Go through the east portal.");
		swensen4North = new ObjectStep(this, ObjectID.PORTAL_4153, new WorldPoint(2665, 10018, 0), "Go through the north portal.");
		swensen5South = new ObjectStep(this, ObjectID.PORTAL_4154, new WorldPoint(2630, 10023, 0), "Go through the south portal.");
		swensen6East = new ObjectStep(this, ObjectID.PORTAL_4155, new WorldPoint(2656, 10037, 0), "Go through the east portal.");
		swensen7North = new ObjectStep(this, ObjectID.PORTAL_4156, new WorldPoint(2666, 10029, 0), "Go through the north portal.");
		swensenUpLadder = new ObjectStep(this, ObjectID.LADDER_4160, new WorldPoint(2665, 10037, 0), "Climb up the ladder.");
		resetSwensen = new DetailedQuestStep(this, "Climb up a rope/ladder to restart the puzzle.");

		/* Peer Task */
		talkToPeer = new NpcStep(this, NpcID.PEER_THE_SEER, new WorldPoint(2633, 3667, 0), "Talk to Peer the Seer south west of the Rellekka market. You will need to bank all your items. Peer will offer to do this for you.");
		talkToPeer.addDialogStep("Ask about becoming a Fremennik");
		talkToPeer.addDialogStep("Yes");
		// 261 1->0 when banking all
		enterPeerHouse = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Try to enter Peer the Seer's house after banking all your items. Peer the Seer will offer to bank everything for you.");
		enterPeerHouse.addDialogStep("Read the riddle");

		enterCode = new DetailedQuestStep(this, "Enter the solution to Peer's door.");

		inputMind = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Enter 'MIND' into the door's combination lock.");
		inputMind.addDialogStep("Solve the riddle");
		inputTree = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Enter 'TREE' into the door's combination lock.");
		inputTree.addDialogStep("Solve the riddle");
		inputLife = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Enter 'LIFE' into the door's combination lock.");
		inputLife.addDialogStep("Solve the riddle");
		inputFire = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Enter 'FIRE' into the door's combination lock.");
		inputFire.addDialogStep("Solve the riddle");
		inputTime = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Enter 'TIME' into the door's combination lock.");
		inputTime.addDialogStep("Solve the riddle");
		inputWind = new ObjectStep(this, ObjectID.DOOR_4165, new WorldPoint(2631, 3667, 0), "Enter 'WIND' into the door's combination lock.");
		inputWind.addDialogStep("Solve the riddle");
		enterCode.addSubSteps(inputMind, inputTree, inputLife, inputFire, inputTime, inputWind);

		goUpEntranceLadderPeer = new ObjectStep(this, ObjectID.LADDER_4163, new WorldPoint(2631, 3663, 0), "Go up the ladder.");
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_4171, new WorldPoint(2634, 3665, 2), "Search the bookcase for a red herring. If you've already unlocked the mural, go search it instead.");
		cookHerring = new ObjectStep(this, ObjectID.COOKING_RANGE_4172, new WorldPoint(2629, 3664, 2), "Use the red herring on the cooking range.", redHerring);
		cookHerring.addIcon(ItemID.RED_HERRING);
		searchUnicorn = new ObjectStep(this, ObjectID.UNICORNS_HEAD_4181, new WorldPoint(2632, 3660, 2), "Study the unicorn's head.");
		searchBull = new ObjectStep(this, ObjectID.BULLS_HEAD_4182, new WorldPoint(2634, 3660, 2), "Study the bull's head.");
		searchChest1 = new ObjectStep(this, ObjectID.CHEST_4167, new WorldPoint(2635, 3660, 2), "Search the chest in the south of the room.");
		searchChest2 = new ObjectStep(this, ObjectID.CHEST_4168, new WorldPoint(2635, 3660, 2), "Search the chest in the south of the room.");
		searchChest1.addSubSteps(searchChest2);
		searchCupboard1 = new ObjectStep(this, ObjectID.CUPBOARD_4177, new WorldPoint(2630, 3660, 2), "Search the cupboard in the south west of the room.");
		searchCupboard2 = new ObjectStep(this, ObjectID.CUPBOARD_4178, new WorldPoint(2630, 3660, 2), "Search the cupboard in the south west of the room.");
		searchCupboard1.addSubSteps(searchCupboard2);

		useGoopOnDisk = new DetailedQuestStep(this, "Use the sticky red goop on the wooden disk.", stickyRedGoop, woodenDisk);

		openTrapDoorAndGoDown1 = new ObjectStep(this, ObjectID.TRAPDOOR_4174, new WorldPoint(2636, 3663, 2), "Open and go down the trapdoor.");
		goDown1 = new ObjectStep(this, ObjectID.TRAPDOOR_4173, new WorldPoint(2636, 3663, 2), "Go down the trapdoor.");
		openTrapDoorAndGoDown1.addSubSteps(goDown1);

		useDiskAnyOnMural = new ObjectStep(this, ObjectID.ABSTRACT_MURAL, new WorldPoint(2634, 3663, 0), "Use the red disks on the abstract mural.", redDiskOld, redDiskNew);
		useDiskAnyOnMural.addIcon(ItemID.OLD_RED_DISK);
		useDiskOldOnMural = new ObjectStep(this, ObjectID.ABSTRACT_MURAL, new WorldPoint(2634, 3663, 0), "Use the old red disk on the abstract mural.", redDiskOld);
		useDiskOldOnMural.addIcon(ItemID.OLD_RED_DISK);
		useDiskNewOnMural = new ObjectStep(this, ObjectID.ABSTRACT_MURAL, new WorldPoint(2634, 3663, 0), "Use the other red disk on the abstract mural.", redDiskNew);
		useDiskNewOnMural.addIcon(ItemID.RED_DISK_3743);
		useDiskAnyOnMural.addSubSteps(useDiskOldOnMural, useDiskNewOnMural);

		goBackUpstairs = new ObjectStep(this, ObjectID.LADDER_4164, new WorldPoint(2636, 3663, 0), "Go back upstairs.");
		goUpstairsWithVaseLid = new ObjectStep(this, ObjectID.LADDER_4164, new WorldPoint(2636, 3663, 0), "Go back upstairs.");

		useBucketOnTap1 = new ObjectStep(this, ObjectID.TAP, new WorldPoint(2629, 3661, 2), "Use the empty bucket on the tap.", emptyBucket);
		useBucketOnTap1.addIcon(ItemID.EMPTY_BUCKET);
		useBucketOnJug1 = new DetailedQuestStep(this, "Use the full bucket on the empty jug.", fullBucket, emptyJug);
		useJugOnDrain1 = new ObjectStep(this, ObjectID.DRAIN, new WorldPoint(2629, 3662, 2), "Empty the full jug into the drain.", fullJug);
		useJugOnDrain1.addIcon(ItemID.FULL_JUG);
		useBucketOnJug2 = new DetailedQuestStep(this, "Use the 2/5ths full bucket on the empty jug.", bucket25, emptyJug);
		useBucketOnTap2 = new ObjectStep(this, ObjectID.TAP, new WorldPoint(2629, 3661, 2), "Use the empty bucket on the tap.", emptyBucket);
		useBucketOnTap2.addIcon(ItemID.EMPTY_BUCKET);
		useBucketOnJug3 = new DetailedQuestStep(this, "Use the full bucket on the 2/3rds full jug.", fullBucket, jug23);
		useBucketOnScale = new ObjectStep(this, ObjectID.CHEST_4170, new WorldPoint(2632, 3665, 2), "Use the 4/5ths full bucket on the chest with a scale on it.", bucket45);
		useBucketOnScale.addIcon(ItemID._45THS_FULL_BUCKET);

		fillVase = new ObjectStep(this, ObjectID.TAP, new WorldPoint(2629, 3661, 2), "Use the empty vase on the tap.", vase);
		fillVase.addIcon(ItemID.VASE_3734);

		useLidOnVase = new DetailedQuestStep(this, "Use the vase lid on the filled vase", filledVase, vaseLid);

		useVaseOnTable = new ObjectStep(this, ObjectID.FROZEN_TABLE, new WorldPoint(2638, 3665, 2), "Use the filled, sealed vase on the frozen table.", filledVaseWithLid);
		useVaseOnTable.addIcon(ItemID.SEALED_VASE_3739);

		takeLidOff = new DetailedQuestStep(this, "Take the lid off the vase and fill the vase.", vaseWithLidWrong);
		warmFrozenVase = new ObjectStep(this, ObjectID.COOKING_RANGE_4172, new WorldPoint(2629, 3664, 2), "Use the frozen vase on the cooking range.", frozenVase);
		warmFrozenVase.addIcon(ItemID.FROZEN_VASE);
		emptyJugAndBucket = new DetailedQuestStep(this, "Empty jug and bucket to restart.");

		useFrozenKeyOnRange = new ObjectStep(this, ObjectID.COOKING_RANGE_4172, new WorldPoint(2629, 3664, 2), "Use the frozen key on the cooking range.", frozenKey);
		useFrozenKeyOnRange.addIcon(ItemID.FROZEN_KEY);
		goDownstairsWithKey = new ObjectStep(this, ObjectID.TRAPDOOR_4174, new WorldPoint(2636, 3663, 2), "Open and go down the trapdoor.", seersKey);
		goDownstairsWithKey2 = new ObjectStep(this, ObjectID.TRAPDOOR_4173, new WorldPoint(2636, 3663, 2), "Go down the trapdoor.", seersKey);
		goDownstairsWithKey.addSubSteps(goDownstairsWithKey2);
		leaveSeersHouse = new ObjectStep(this, ObjectID.DOOR_4166, new WorldPoint(2636, 3667, 0), "Leave the Seer's house.", seersKey);

		finishQuest =  new NpcStep(this, NpcID.BRUNDT_THE_CHIEFTAIN_9263, new WorldPoint(2658, 3669, 0), "Talk to Brundt in Rellekka's longhall to finish the quest.");
		finishQuest.addDialogStep("Ask about anything else.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(coins);
		reqs.add(rawShark);
		reqs.add(tinderbox);
		reqs.add(knife);
		reqs.add(axe);
		return reqs;
	}

	@Override
	public ArrayList<String> getNotes()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("If the guide at any point appears to de-sync during this quest, please try opening the quest journal to fix it.");
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(combatGear);
		reqs.add(ringsOfRecoil);
		reqs.add(camelotTeleport);
		reqs.add(rellekkaTeleports);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Koschei the deathless");
		reqs.add("Draugen (level 69)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Talk to Brundt", new ArrayList<>(Collections.singletonList(talkToBrundt)), coins, rawShark, tinderbox, knife, axe, combatGear));

		PanelDetails olafPanel = new PanelDetails("Olaf's task",
			new ArrayList<>(Arrays.asList(talkToOlaf, talkToLalli, talkToAskeladdenForRock, useOnion, talkToLaliAfterStew, chopSwayingTree,
				fletchLyre, spinWool, makeLyre, enchantLyre, performMusic)), axe, knife, rawShark);
		olafPanel.setLockingStep(olafTask);
		olafPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails manniPanel = new PanelDetails("Manni's task",
			new ArrayList<>(Arrays.asList(talkToManni, pickUpBeer, getStrangeObject, getAlcoholFreeBeer, prepareToUseStrangeObject,
				useStrangeObject, cheatInBeerDrinking)), tinderbox, coins250);
		manniPanel.setLockingStep(manniTask);
		manniPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails sigliPanel = new PanelDetails("Sigli's task",
			new ArrayList<>(Arrays.asList(talkToSigli, huntDraugen, returnToSigli)), combatGear);
		sigliPanel.setLockingStep(sigliTask);
		sigliPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails sigmundPanel = new PanelDetails("Sigmund's task",
			new ArrayList<>(Arrays.asList(talkToSigmund, talkToSailor, talkToOlafForSigmund, talkToYsra, talkToBrundtForSigmund, talkToSigliForSigmund,
				talkToSkulgrimenForSigmund, talkToFishermanForSigmund, talkToSwenesenForSigmund, talkToPeerForSigmund, talkToThorvaldForSigmund, talkToManniForSigmund,
				talkToThoraForSigmund, talkToAskeladdenForSigmund, bringNoteToThora, bringCocktailToManni, bringChampionsTokenToThorvald, bringWarriorsContractToPeer, bringWeatherForecastToSwensen,
				bringSeaFishingMapToFisherman, bringUnusualFishToSkulgrimen, bringCustomBowStringToSigli, bringTrackingMapToBrundt, bringFiscalStatementToYsra, bringSturdyBootsToOlaf, bringBalladToSailor,
				bringExoticFlowerToSigmund)), coins5000);
		sigmundPanel.setLockingStep(sigmundTask);
		sigmundPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails thorvaldPanel = new PanelDetails("Thorvald's task",
			new ArrayList<>(Arrays.asList(talkToThorvald, goDownLadderToKoschei, killKoschei1)), koscheiGear, optionalKoscheiGear);
		thorvaldPanel.setLockingStep(thorvaldTask);
		thorvaldPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails swensenPanel = new PanelDetails("Swensen's task",
			new ArrayList<>(Arrays.asList(talkToSwensen, goDownLadderSwensen, swensen1South, swensen2West, swensen3East, swensen4North, swensen5South, swensen6East, swensen7North, swensenUpLadder)));
		swensenPanel.setLockingStep(swensenTask);
		swensenPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails peerPanel = new PanelDetails("Peer's task",
			new ArrayList<>(Arrays.asList(talkToPeer, enterPeerHouse, enterCode, goUpEntranceLadderPeer, searchBookcase, searchBull, searchUnicorn, cookHerring, useGoopOnDisk, openTrapDoorAndGoDown1,
				useDiskAnyOnMural, goUpstairsWithVaseLid, searchCupboard1, searchChest1, useBucketOnTap1, useBucketOnJug1, useJugOnDrain1, useBucketOnJug2, useBucketOnTap2, useBucketOnJug3, useBucketOnScale,
				fillVase, useLidOnVase, useVaseOnTable, useFrozenKeyOnRange, goDownstairsWithKey, leaveSeersHouse)));
		peerPanel.setLockingStep(peerTask);
		peerPanel.setVars(1, 2, 3, 4, 5, 6, 7);

		PanelDetails finalPanel = new PanelDetails("Finish off",
			new ArrayList<>(Collections.singletonList(finishQuest)));

		allSteps.add(olafPanel);
		allSteps.add(manniPanel);
		allSteps.add(sigliPanel);
		allSteps.add(sigmundPanel);
		allSteps.add(thorvaldPanel);
		allSteps.add(swensenPanel);
		allSteps.add(peerPanel);
		allSteps.add(finalPanel);
		return allSteps;
	}
}
