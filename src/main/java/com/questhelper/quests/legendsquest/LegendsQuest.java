/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.legendsquest;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.LEGENDS_QUEST
)
public class LegendsQuest extends BasicQuestHelper
{
	//Items Required
	ItemRequirement axe, machete, radimusNotes, papyrus3, charcoal3, papyrus, charcoal, radimusNotesHighlight, completeNotesHighlighted, bullRoarer,  lockpick,
		soulRune, mindRune, earthRune, lawRune2, opal, jade, sapphire, ruby, diamond, pickaxe, bullRoarerHighlight, sketch, lawRuneHighlight,
		soulRuneHighlight, mindRuneHighlight, earthRuneHighlight, opalHighlighted, jadeHighlighted, topazHighlighted, sapphireHighlighted,
		emeraldHighlighted, rubyHighlighted, diamondHighlighted, topaz, emerald, bindingBook, bindingBookHighlighted, goldBar2, hammer, goldBowl,
		goldBowlHighlighted, combatGear, goldBowlBlessed, goldBowlFull, goldBowlFullHighlighted, reed, macheteHighlighted, yommiSeeds, germinatedSeeds,
		germinatedSeedsHighlighted, runeOrDragonAxe, ardrigal, snakeWeed, vialOfWater, unpoweredOrb, ardrigalMixture, braveryPotion, braveryPotionHighlighted,
		snakeMixture, rope, elemental30, cosmic3, ropeHighlighted, lumpCrystal, chunkCrystal, hunkCrystal, heartCrystal, heartCrystal2, darkDagger, glowingDagger,
		force, forceHighlighted, yommiTotem, yommiTotemHighlighted, gildedTotem, completeNotes, anyNotes;

	ItemRequirements chargeOrbRunes;

	SpellbookRequirement normalSpellbook;

	Requirement inGuild, inKhazari, completeEast, completeMiddle, completeWest, completeTextAppeared, inWest, inMiddle,
		inEast, finishedMap, gujuoNearby, inCaveRoom1, inCaves, talkedToUngadulu, hadSketch, inCaveRoom2, inCaveRoom3, inCaveRoom4,
		addedSoulRune, addedMindRune, addedEarthRune, addedLawRune, addedLawRune2, searchedMarkedWall, inCaveRoom5, sapphirePlaced,
		opalPlaced, jadePlaced, topazPlaced, emeraldPlaced, rubyPlaced, diamondPlaced, bookAppearing, bookNearby, inFire, nezNearby,
		inCaveRoom6, addedRope, inChallengeCave, echnedNearby, viyeldiNearby, sacredWaterNearby, saplingNearby, adultNearby, felledNearby,
		trimmedNearby, totemNearby, ranalphNearby, irvigNearby, sanNearby;

	QuestStep talkToGuard, talkToRadimus, enterJungle, sketchWest, sketchMiddle, sketchEast, enterJungleWithRoarer, spinBull,
		talkToGujuo, enterMossyRock, investigateFireWall, leaveCave, spinBullAgain, talkToGujuoAgain, enterMossyRockAgain,
		enterBookcase, enterGate1, enterGate2, searchMarkedWall, useSoul, useMind, useEarth, useLaw, useLaw2, useSapphire,
		useOpal, useTopaz, useJade, useEmerald, useRuby, useDiamond, waitForBook, pickUpBook, makeBowl, enterJungleWithBowl,
		spinBullToBless, talkToGujuoWithBowl, useMacheteOnReeds, useReedOnPool, enterMossyRockWithBowl, useBowlOnFireWall,
		fightNezikchenedInFire, enterMossyRockAfterFight, enterFireAfterFight, talkToUngadulu, useBowlOnSeeds, plantSeed,
		leaveCaveWithSeed, useMacheteOnReedsAgain, spinBullAfterSeeds, talkToGujuoAfterSeeds, enterJungleAfterSeeds, addArdrigal,
		addSnake, addArdrigalToSnake, enterJungleToGoToSource, enterMossyRockToSource, enterBookcaseToSource, enterGate1ToSource,
		enterGate2ToSource, searchMarkedWallToSource, useSpellOnDoor, useRopeOnWinch, enterMossyRockForViyeldi, useCrystalsOnFurnace,
		useHeartOnRock, useHeartOnRecess, pushBoulder, talkToEchned, pickUpHat, killViyeldi, pushBoulderAgain, giveDaggerToEchned,
		fightNezikchenedAtSource, castForce, pushBoulderWithForce, pushBoulderAfterFight, useBowlOnSacredWater, returnToSurface,
		useWaterOnTree, useMacheteOnReedsEnd, useReedOnPoolEnd, useAxe, useAxeAgain, enterJungleToPlant, craftTree, pickUpTotem,
		killRanalph, killIrvig, killSan, defeatDemon;

	ConditionalStep runePuzzle, gemPuzzle, blessBowl;

	NpcStep useNotes, useBindingBookOnUngadulu, returnToRadimus, talkToRadimusInGuild, talkToRadimusInGuildAgain;

	ObjectStep useReedOnPoolAgain, climbDownWinch, drinkBraveryPotionAndClimbDown, useTotemOnTotem, useTotemOnTotemAgain;

	//Zones
	Zone guild1, guild2, guild3, khazari1, khazari2, khazari3, khazari4, eastKhazari, westKhazari, middleKhazari, caveRoom1P1, caveRoom1P2,
			caves, caveRoom2P1, caveRoom2P2, caveRoom3, caveRoom4P1, caveRoom4P2, caveRoom4P3, caveRoom5P1, caveRoom5P2, caveRoom5P3, fire1,
			fire2, fire3, caveRoom6P1, caveRoom6P2, challengeCave;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep startQuest = new ConditionalStep(this, talkToGuard);
		startQuest.addStep(inGuild, talkToRadimus);

		steps.put(0, startQuest);

		ConditionalStep sketchJungle = new ConditionalStep(this, enterJungle);
		sketchJungle.addStep(new Conditions(inKhazari, completeWest, completeMiddle), sketchEast);
		sketchJungle.addStep(new Conditions(inKhazari, completeWest), sketchMiddle);
		sketchJungle.addStep(inKhazari, sketchWest);

		steps.put(1, sketchJungle);

		steps.put(2, useNotes);

		ConditionalStep talkWithGujuo = new ConditionalStep(this, enterJungleWithRoarer);
		talkWithGujuo.addStep(gujuoNearby, talkToGujuo);
		talkWithGujuo.addStep(inKhazari, spinBull);

		steps.put(3, talkWithGujuo);
		steps.put(4, talkWithGujuo);

		ConditionalStep investigatingTheCave = new ConditionalStep(this, enterJungleWithRoarer);
		investigatingTheCave.addStep(new Conditions(inKhazari, talkedToUngadulu, gujuoNearby), talkToGujuoAgain);
		investigatingTheCave.addStep(new Conditions(inKhazari, talkedToUngadulu), spinBullAgain);
		investigatingTheCave.addStep(new Conditions(inCaves, talkedToUngadulu), leaveCave);
		investigatingTheCave.addStep(inCaves, investigateFireWall);
		investigatingTheCave.addStep(inKhazari, enterMossyRock);

		steps.put(5, investigatingTheCave);
		steps.put(6, investigatingTheCave);
		steps.put(7, investigatingTheCave);

		runePuzzle = new ConditionalStep(this, enterMossyRockAgain);
		runePuzzle.addStep(new Conditions(inCaveRoom4, addedLawRune), useLaw2);
		runePuzzle.addStep(new Conditions(inCaveRoom4, addedEarthRune), useLaw);
		runePuzzle.addStep(new Conditions(inCaveRoom4, addedMindRune), useEarth);
		runePuzzle.addStep(new Conditions(inCaveRoom4, addedSoulRune), useMind);
		runePuzzle.addStep(new Conditions(inCaveRoom4, searchedMarkedWall), useSoul);
		runePuzzle.addStep(new Conditions(inCaveRoom4), searchMarkedWall);
		runePuzzle.addStep(new Conditions(inCaveRoom3), enterGate2);
		runePuzzle.addStep(new Conditions(inCaveRoom2), enterGate1);
		runePuzzle.addStep(new Conditions(inCaveRoom1), enterBookcase);
		runePuzzle.setLockingCondition(inCaveRoom5);
		runePuzzle.setBlocker(true);

		gemPuzzle = new ConditionalStep(this, useSapphire);
		gemPuzzle.addStep(new Conditions(bookNearby), pickUpBook);
		gemPuzzle.addStep(new Conditions(bookAppearing), waitForBook);
		gemPuzzle.addStep(new Conditions(sapphirePlaced, diamondPlaced, rubyPlaced, topazPlaced, jadePlaced, emeraldPlaced), useOpal);
		gemPuzzle.addStep(new Conditions(sapphirePlaced, diamondPlaced, rubyPlaced, topazPlaced, jadePlaced), useEmerald);
		gemPuzzle.addStep(new Conditions(sapphirePlaced, diamondPlaced, rubyPlaced, topazPlaced), useJade);
		gemPuzzle.addStep(new Conditions(sapphirePlaced, diamondPlaced, rubyPlaced), useTopaz);
		gemPuzzle.addStep(new Conditions(sapphirePlaced, diamondPlaced), useRuby);
		gemPuzzle.addStep(new Conditions(sapphirePlaced), useDiamond);
		gemPuzzle.setLockingCondition(bindingBook.alsoCheckBank(questBank));
		gemPuzzle.setBlocker(true);

		blessBowl = new ConditionalStep(this, makeBowl);
		blessBowl.addStep(new Conditions(inKhazari, goldBowlBlessed.alsoCheckBank(questBank), reed), useReedOnPool);
		blessBowl.addStep(new Conditions(inKhazari, goldBowlBlessed.alsoCheckBank(questBank)), useMacheteOnReeds);
		blessBowl.addStep(new Conditions(inKhazari, goldBowl.alsoCheckBank(questBank), gujuoNearby), talkToGujuoWithBowl);
		blessBowl.addStep(new Conditions(inKhazari, goldBowl.alsoCheckBank(questBank)), spinBullToBless);
		blessBowl.addStep(goldBowl.alsoCheckBank(questBank), enterJungleWithBowl);

		ConditionalStep solvingCaves = new ConditionalStep(this, enterJungleWithRoarer);
		solvingCaves.addStep(new Conditions(inFire, nezNearby), fightNezikchenedInFire);
		solvingCaves.addStep(new Conditions(inFire, bindingBook.alsoCheckBank(questBank)), useBindingBookOnUngadulu);
		solvingCaves.addStep(new Conditions(inCaves, bindingBook.alsoCheckBank(questBank), goldBowlFull), useBowlOnFireWall);
		solvingCaves.addStep(new Conditions(bindingBook.alsoCheckBank(questBank), goldBowlFull), enterMossyRockWithBowl);
		solvingCaves.addStep(bindingBook.alsoCheckBank(questBank), blessBowl);
		solvingCaves.addStep(inCaveRoom5, gemPuzzle);
		solvingCaves.addStep(hadSketch, runePuzzle);
		solvingCaves.addStep(new Conditions(inKhazari, gujuoNearby), talkToGujuoAgain);
		solvingCaves.addStep(new Conditions(inKhazari), spinBullAgain);
		solvingCaves.addStep(inCaves, leaveCave);

		steps.put(8, solvingCaves);
		// No step 9 encountered, but including as may occur for some players
		steps.put(9, solvingCaves);
		steps.put(10, solvingCaves);
		steps.put(11, solvingCaves);

		ConditionalStep talkingToUngadulu = new ConditionalStep(this, enterMossyRockAfterFight);
		talkingToUngadulu.addStep(germinatedSeeds.alsoCheckBank(questBank), useBowlOnSeeds);
		talkingToUngadulu.addStep(inFire, talkToUngadulu);
		talkingToUngadulu.addStep(inCaves, enterFireAfterFight);

		steps.put(12, talkingToUngadulu);

		ConditionalStep plantSeedAttempt = new ConditionalStep(this, useMacheteOnReedsAgain);
		plantSeedAttempt.addStep(reed, useReedOnPool);
		plantSeedAttempt.addStep(inCaves, leaveCaveWithSeed);

		steps.put(13, plantSeedAttempt);

		ConditionalStep learnAboutThePool = new ConditionalStep(this, enterJungleAfterSeeds);
		learnAboutThePool.addStep(gujuoNearby, talkToGujuoAfterSeeds);
		learnAboutThePool.addStep(inKhazari, spinBullAfterSeeds);

		steps.put(14, learnAboutThePool);

		ConditionalStep reachingTheDeeperCaves = new ConditionalStep(this, addArdrigal);
		reachingTheDeeperCaves.addStep(new Conditions(inCaveRoom6, addedRope), drinkBraveryPotionAndClimbDown);
		reachingTheDeeperCaves.addStep(inCaveRoom6, useRopeOnWinch);
		reachingTheDeeperCaves.addStep(inCaveRoom5, useSpellOnDoor);
		reachingTheDeeperCaves.addStep(inCaveRoom4, searchMarkedWallToSource);
		reachingTheDeeperCaves.addStep(inCaveRoom3, enterGate2ToSource);
		reachingTheDeeperCaves.addStep(inCaveRoom2, enterGate1ToSource);
		reachingTheDeeperCaves.addStep(new Conditions(inCaveRoom1, braveryPotion), enterBookcaseToSource);
		reachingTheDeeperCaves.addStep(new Conditions(inKhazari, braveryPotion), enterMossyRockToSource);
		reachingTheDeeperCaves.addStep(braveryPotion.alsoCheckBank(questBank), enterJungleToGoToSource);
		reachingTheDeeperCaves.addStep(snakeMixture, addArdrigalToSnake);
		reachingTheDeeperCaves.addStep(ardrigalMixture, addSnake);

		steps.put(15, reachingTheDeeperCaves);

		ConditionalStep solvingViyeldiCaves = new ConditionalStep(this, enterMossyRockForViyeldi);
		solvingViyeldiCaves.addStep(inChallengeCave, useCrystalsOnFurnace);
		solvingViyeldiCaves.addStep(inCaveRoom6, climbDownWinch);
		solvingViyeldiCaves.addStep(inCaveRoom5, useSpellOnDoor);
		solvingViyeldiCaves.addStep(inCaveRoom4, searchMarkedWallToSource);
		solvingViyeldiCaves.addStep(inCaveRoom3, enterGate2ToSource);
		solvingViyeldiCaves.addStep(inCaveRoom2, enterGate1ToSource);
		solvingViyeldiCaves.addStep(inCaveRoom1, enterBookcaseToSource);

		steps.put(16, solvingViyeldiCaves);

		ConditionalStep useHeart = new ConditionalStep(this, enterMossyRockForViyeldi);
		useHeart.addStep(new Conditions(inChallengeCave, heartCrystal2), useHeartOnRecess);
		useHeart.addStep(inChallengeCave, useHeartOnRock);
		useHeart.addStep(inCaveRoom6, climbDownWinch);
		useHeart.addStep(inCaveRoom5, useSpellOnDoor);
		useHeart.addStep(inCaveRoom4, searchMarkedWallToSource);
		useHeart.addStep(inCaveRoom3, enterGate2ToSource);
		useHeart.addStep(inCaveRoom2, enterGate1ToSource);
		useHeart.addStep(inCaveRoom1, enterBookcaseToSource);

		steps.put(17, useHeart);

		ConditionalStep attemptToPushBoulder = new ConditionalStep(this, enterMossyRockForViyeldi);
		attemptToPushBoulder.addStep(echnedNearby, talkToEchned);
		attemptToPushBoulder.addStep(inChallengeCave, pushBoulder);
		attemptToPushBoulder.addStep(inCaveRoom6, climbDownWinch);
		attemptToPushBoulder.addStep(inCaveRoom5, useSpellOnDoor);
		attemptToPushBoulder.addStep(inCaveRoom4, searchMarkedWallToSource);
		attemptToPushBoulder.addStep(inCaveRoom3, enterGate2ToSource);
		attemptToPushBoulder.addStep(inCaveRoom2, enterGate1ToSource);
		attemptToPushBoulder.addStep(inCaveRoom1, enterBookcaseToSource);

		steps.put(18, attemptToPushBoulder);
		steps.put(19, attemptToPushBoulder);

		ConditionalStep killViy = new ConditionalStep(this, enterMossyRockForViyeldi);
		killViy.addStep(new Conditions(nezNearby, inChallengeCave), fightNezikchenedAtSource);
		killViy.addStep(new Conditions(echnedNearby, glowingDagger), giveDaggerToEchned);
		killViy.addStep(new Conditions(echnedNearby, force), castForce);
		killViy.addStep(new Conditions(inChallengeCave, force), pushBoulderWithForce);
		killViy.addStep(new Conditions(inChallengeCave, glowingDagger), pushBoulderAgain);
		killViy.addStep(viyeldiNearby, killViyeldi);
		killViy.addStep(inChallengeCave, pickUpHat);
		killViy.addStep(inCaveRoom6, climbDownWinch);
		killViy.addStep(inCaveRoom5, useSpellOnDoor);
		killViy.addStep(inCaveRoom4, searchMarkedWallToSource);
		killViy.addStep(inCaveRoom3, enterGate2ToSource);
		killViy.addStep(inCaveRoom2, enterGate1ToSource);
		killViy.addStep(inCaveRoom1, enterBookcaseToSource);

		steps.put(20, killViy);
		// didn't see step 21

		ConditionalStep saveTheSource = new ConditionalStep(this, enterMossyRockForViyeldi);
		saveTheSource.addStep(sacredWaterNearby, useBowlOnSacredWater);
		saveTheSource.addStep(inChallengeCave, pushBoulderAfterFight);
		saveTheSource.addStep(inCaveRoom6, climbDownWinch);
		saveTheSource.addStep(inCaveRoom5, useSpellOnDoor);
		saveTheSource.addStep(inCaveRoom4, searchMarkedWallToSource);
		saveTheSource.addStep(inCaveRoom3, enterGate2ToSource);
		saveTheSource.addStep(inCaveRoom2, enterGate1ToSource);
		saveTheSource.addStep(inCaveRoom1, enterBookcaseToSource);

		steps.put(22, saveTheSource);
		// didn't see steps 23/24

		ConditionalStep growTree = new ConditionalStep(this, enterJungleToPlant);
		growTree.addStep(new Conditions(inKhazari, totemNearby), pickUpTotem);
		growTree.addStep(new Conditions(inKhazari, trimmedNearby), craftTree);
		growTree.addStep(new Conditions(inKhazari, felledNearby), useAxeAgain);
		growTree.addStep(new Conditions(inKhazari, adultNearby), useAxe);
		growTree.addStep(new Conditions(inKhazari, saplingNearby), useWaterOnTree);
		growTree.addStep(new Conditions(inKhazari, goldBowlFull), plantSeed);
		growTree.addStep(new Conditions(inKhazari, reed), useReedOnPoolEnd);
		growTree.addStep(inKhazari, useReedOnPoolEnd);
		growTree.addStep(inCaves, returnToSurface);

		steps.put(25, growTree);

		ConditionalStep placingTheTotem = new ConditionalStep(this, useTotemOnTotem);
		placingTheTotem.addStep(new Conditions(inKhazari, ranalphNearby), killRanalph);
		placingTheTotem.addStep(new Conditions(inKhazari, irvigNearby), killIrvig);
		placingTheTotem.addStep(new Conditions(inKhazari, sanNearby), killSan);
		placingTheTotem.addStep(new Conditions(inKhazari, nezNearby), defeatDemon);

		steps.put(30, placingTheTotem);
		steps.put(31, placingTheTotem);
		steps.put(32, placingTheTotem);
		steps.put(33, placingTheTotem);
		steps.put(34, placingTheTotem);

		steps.put(35, useTotemOnTotemAgain);

		steps.put(40, returnToRadimus);
		steps.put(45, returnToRadimus);

		steps.put(50, talkToRadimusInGuild);
		steps.put(55, talkToRadimusInGuild);
		steps.put(60, talkToRadimusInGuild);
		steps.put(65, talkToRadimusInGuild);

		steps.put(70, talkToRadimusInGuildAgain);

		return steps;
	}

	private void setupZones()
	{
		guild1 = new Zone(new WorldPoint(2726, 3350, 0), new WorldPoint(2731, 3382, 2));
		guild2 = new Zone(new WorldPoint(2721, 3363, 0), new WorldPoint(2725, 3382, 2));
		guild3 = new Zone(new WorldPoint(2731, 3363, 0), new WorldPoint(2736, 3382, 2));
		khazari1 = new Zone(new WorldPoint(2941, 2875, 0), new WorldPoint(2985, 2948, 0));
		khazari2 = new Zone(new WorldPoint(2753, 2873, 0), new WorldPoint(2940, 2938, 0));
		khazari3 = new Zone(new WorldPoint(2801, 2939, 0), new WorldPoint(2814, 2939, 0));
		khazari4 = new Zone(new WorldPoint(2757, 2939, 0), new WorldPoint(2784, 2939, 0));

		eastKhazari = new Zone(new WorldPoint(2880, 2880, 0), new WorldPoint(2985, 2940, 0));
		westKhazari = new Zone(new WorldPoint(2753, 2880, 0), new WorldPoint(2815, 2940, 0));
		middleKhazari = new Zone(new WorldPoint(2816, 2880, 0), new WorldPoint(2879, 2940, 0));
		caveRoom1P1 = new Zone(new WorldPoint(2780, 9317, 0), new WorldPoint(2803, 9335, 0));
		caveRoom1P2 = new Zone(new WorldPoint(2770, 9336, 0), new WorldPoint(2797, 9343, 0));
		caveRoom2P1 = new Zone(new WorldPoint(2804, 9332, 0), new WorldPoint(2812, 9342, 0));
		caveRoom2P2 = new Zone(new WorldPoint(2799, 9336, 0), new WorldPoint(2803, 9341, 0));
		caveRoom3 = new Zone(new WorldPoint(2807, 9314, 0), new WorldPoint(2812, 9331, 0));
		caveRoom4P1 = new Zone(new WorldPoint(2785, 9276, 0), new WorldPoint(2823, 9313, 0));
		caveRoom4P2 = new Zone(new WorldPoint(2779, 9300, 0), new WorldPoint(2784, 9313, 0));
		caveRoom4P3 = new Zone(new WorldPoint(2774, 9307, 0), new WorldPoint(2778, 9313, 0));
		caveRoom5P1 = new Zone(new WorldPoint(2753, 9281, 0), new WorldPoint(2785, 9299, 0));
		caveRoom5P2 = new Zone(new WorldPoint(2754, 9300, 0), new WorldPoint(2778, 9307, 0));
		caveRoom5P3 = new Zone(new WorldPoint(2754, 9308, 0), new WorldPoint(2771, 9313, 0));
		caveRoom6P1 = new Zone(new WorldPoint(2754, 9316, 0), new WorldPoint(2771, 9340, 0));
		caveRoom6P2 = new Zone(new WorldPoint(2772, 9316, 0), new WorldPoint(2779, 9330, 0));
		caves = new Zone(new WorldPoint(2749, 9275, 0), new WorldPoint(2817, 9345, 0));
		fire1 = new Zone(new WorldPoint(2789, 9325, 0), new WorldPoint(2796, 9332, 0));
		fire2 = new Zone(new WorldPoint(2788, 9328, 0), new WorldPoint(2797, 9329, 0));
		fire3 = new Zone(new WorldPoint(2792, 9324, 0), new WorldPoint(2793, 9333, 0));
		challengeCave = new Zone(new WorldPoint(2369, 4672, 0), new WorldPoint(2430, 4736, 0));
	}

	private void setupItemRequirements()
	{
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		machete = new ItemRequirement("A machete", ItemCollections.getMachete());

		macheteHighlighted = new ItemRequirement("A machete", ItemCollections.getMachete());
		macheteHighlighted.setHighlightInInventory(true);

		radimusNotes = new ItemRequirement("Radimus notes", ItemID.RADIMUS_NOTES);
		radimusNotes.setTooltip("You can get another from Radimus in the Legends' Guild for 30 gp");
		papyrus3 = new ItemRequirement("3-5 papyrus", ItemID.PAPYRUS, 3);
		charcoal3 = new ItemRequirement("1-3 charcoal", ItemID.CHARCOAL);
		radimusNotesHighlight = new ItemRequirement("Radimus notes", ItemID.RADIMUS_NOTES);
		radimusNotesHighlight.setTooltip("You can get another from Radimus in the Legends' Guild for 30 gp");
		radimusNotesHighlight.setHighlightInInventory(true);

		completeNotes = new ItemRequirement("Radimus notes", ItemID.RADIMUS_NOTES_715);
		completeNotes.setTooltip("You can get another from Radimus in the Legends' Guild for 30 gp, and you'll need to re-sketch the jungle");

		completeNotesHighlighted = new ItemRequirement("Radimus notes", ItemID.RADIMUS_NOTES_715);
		completeNotesHighlighted.setTooltip("You can get another from Radimus in the Legends' Guild for 30 gp, and you'll need to re-sketch the jungle");
		completeNotesHighlighted.setHighlightInInventory(true);

		anyNotes = new ItemRequirement("Radimus notes", ItemID.RADIMUS_NOTES);
		anyNotes.addAlternates(ItemID.RADIMUS_NOTES_715);

		sketch = new ItemRequirement("Sketch", ItemID.SKETCH);
		sketch.setTooltip("You can get another by summoning Gujuo with the bull roarer again");

		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);

		bullRoarer = new ItemRequirement("Bull roarer", ItemID.BULL_ROARER);
		bullRoarer.setTooltip("You can get another by using a complete Radimus notes on a Jungle Forester");
		bullRoarerHighlight = new ItemRequirement("Bull roarer", ItemID.BULL_ROARER);
		bullRoarerHighlight.setTooltip("You can get another by using a complete Radimus notes on a Jungle Forester");
		bullRoarerHighlight.setHighlightInInventory(true);

		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK);
		soulRune = new ItemRequirement("Soul rune", ItemID.SOUL_RUNE);
		soulRuneHighlight = new ItemRequirement("Soul rune", ItemID.SOUL_RUNE);
		soulRuneHighlight.setHighlightInInventory(true);
		mindRune = new ItemRequirement("Mind rune", ItemID.MIND_RUNE);
		mindRuneHighlight = new ItemRequirement("Mind rune", ItemID.MIND_RUNE);
		mindRuneHighlight.setHighlightInInventory(true);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		earthRuneHighlight = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		earthRuneHighlight.setHighlightInInventory(true);
		lawRune2 = new ItemRequirement("Law rune", ItemID.LAW_RUNE, 2);
		lawRuneHighlight = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		lawRuneHighlight.setHighlightInInventory(true);
		opal = new ItemRequirement("Opal", ItemID.OPAL);
		jade = new ItemRequirement("Jade", ItemID.JADE);
		topaz = new ItemRequirement("Red topaz", ItemID.RED_TOPAZ);
		sapphire = new ItemRequirement("Sapphire", ItemID.SAPPHIRE);
		emerald = new ItemRequirement("Emerald", ItemID.EMERALD);
		ruby = new ItemRequirement("Ruby", ItemID.RUBY);
		diamond = new ItemRequirement("Diamond", ItemID.DIAMOND);

		opalHighlighted = new ItemRequirement("Opal", ItemID.OPAL);
		opalHighlighted.setHighlightInInventory(true);
		jadeHighlighted = new ItemRequirement("Jade", ItemID.JADE);
		jadeHighlighted.setHighlightInInventory(true);
		topazHighlighted = new ItemRequirement("Red topaz", ItemID.RED_TOPAZ);
		topazHighlighted.setHighlightInInventory(true);
		sapphireHighlighted = new ItemRequirement("Sapphire", ItemID.SAPPHIRE);
		sapphireHighlighted.setHighlightInInventory(true);
		emeraldHighlighted = new ItemRequirement("Emerald", ItemID.EMERALD);
		emeraldHighlighted.setHighlightInInventory(true);
		rubyHighlighted = new ItemRequirement("Ruby", ItemID.RUBY);
		rubyHighlighted.setHighlightInInventory(true);
		diamondHighlighted = new ItemRequirement("Diamond", ItemID.DIAMOND);
		diamondHighlighted.setHighlightInInventory(true);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());

		bindingBook = new ItemRequirement("Binding book", ItemID.BINDING_BOOK);
		bindingBookHighlighted = new ItemRequirement("Binding book", ItemID.BINDING_BOOK);
		bindingBookHighlighted.setHighlightInInventory(true);

		goldBar2 = new ItemRequirement("Gold bar", ItemID.GOLD_BAR, 2);

		hammer = new ItemRequirement("Hammer", ItemCollections.getHammer());

		goldBowl = new ItemRequirement("Gold bowl", ItemID.GOLD_BOWL);
		goldBowl.addAlternates(ItemID.GOLDEN_BOWL, ItemID.GOLDEN_BOWL_724, ItemID.GOLDEN_BOWL_725, ItemID.GOLDEN_BOWL_726, ItemID.BLESSED_GOLD_BOWL);
		goldBowlHighlighted = new ItemRequirement("Gold bowl", ItemID.GOLD_BOWL);
		goldBowlHighlighted.setHighlightInInventory(true);

		combatGear = new ItemRequirement("Combat gear, food and potions", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		goldBowlBlessed = new ItemRequirement("Blessed gold bowl", ItemID.BLESSED_GOLD_BOWL);

		goldBowlFull = new ItemRequirement("Golden bowl", ItemID.GOLDEN_BOWL_726);
		goldBowlFullHighlighted = new ItemRequirement("Golden bowl", ItemID.GOLDEN_BOWL_726);
		goldBowlFullHighlighted.setHighlightInInventory(true);
		goldBowlFullHighlighted.setTooltip("You can fill another gold bowl from the water pool using a reed");

		reed = new ItemRequirement("Hollow reed", ItemID.HOLLOW_REED);
		reed.setHighlightInInventory(true);

		yommiSeeds = new ItemRequirement("Yommi tree seeds", ItemID.YOMMI_TREE_SEEDS);
		yommiSeeds.setHighlightInInventory(true);

		germinatedSeeds = new ItemRequirement("Yommi tree seeds", ItemID.YOMMI_TREE_SEEDS_736);
		germinatedSeeds.setTooltip("You can get more seeds from Ungadulu, and use sacred water on them");
		germinatedSeedsHighlighted = new ItemRequirement("Yommi tree seeds", ItemID.YOMMI_TREE_SEEDS_736);
		germinatedSeedsHighlighted.setTooltip("You can get more seeds from Ungadulu, and use sacred water on them");
		germinatedSeedsHighlighted.setHighlightInInventory(true);

		runeOrDragonAxe = new ItemRequirement("Rune or Dragon axe", ItemID.RUNE_AXE);
		runeOrDragonAxe.addAlternates(ItemID.DRAGON_AXE);
		ardrigal = new ItemRequirement("Ardrigal", ItemID.ARDRIGAL);
		ardrigal.setTooltip("You can find some in the palm trees north east of Tai Bwo Wannai");
		ardrigal.setHighlightInInventory(true);
		snakeWeed = new ItemRequirement("Snake weed", ItemID.SNAKE_WEED);
		snakeWeed.setTooltip("You can find some in the marshy jungle vines south west of Tai Bwo Wannai");
		snakeWeed.setHighlightInInventory(true);
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER);
		vialOfWater.setHighlightInInventory(true);

		cosmic3 = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE, 3);
		elemental30 = new ItemRequirement("Elemental runes", ItemID.WATER_RUNE, 30);
		elemental30.addAlternates(ItemID.FIRE_RUNE, ItemID.EARTH_RUNE, ItemID.AIR_RUNE);
		elemental30.setExclusiveToOneItemType(true);

		chargeOrbRunes = new ItemRequirements("Runes for charge orb spell", cosmic3, elemental30);

		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB);

		ardrigalMixture = new ItemRequirement("Ardrigal mixture", ItemID.ARDRIGAL_MIXTURE);
		ardrigalMixture.setHighlightInInventory(true);

		braveryPotion = new ItemRequirement("Bravery potion", ItemID.BRAVERY_POTION);
		braveryPotionHighlighted = new ItemRequirement("Bravery potion", ItemID.BRAVERY_POTION);
		braveryPotionHighlighted.setHighlightInInventory(true);

		snakeMixture = new ItemRequirement("Snakeweed mixture", ItemID.SNAKEWEED_MIXTURE);
		snakeMixture.setHighlightInInventory(true);

		rope = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted.setHighlightInInventory(true);

		lumpCrystal = new ItemRequirement("Lump of crystal", ItemID.LUMP_OF_CRYSTAL);
		lumpCrystal.setHighlightInInventory(true);
		chunkCrystal = new ItemRequirement("Chunk of crystal", ItemID.CHUNK_OF_CRYSTAL);
		chunkCrystal.setHighlightInInventory(true);
		hunkCrystal = new ItemRequirement("Hunk of crystal", ItemID.HUNK_OF_CRYSTAL);
		hunkCrystal.setHighlightInInventory(true);

		heartCrystal = new ItemRequirement("Heart crystal", ItemID.HEART_CRYSTAL);
		heartCrystal.setTooltip("You'll have to kill the 3 skeletons for the pieces and forge them in the furnace");
		heartCrystal.setHighlightInInventory(true);

		heartCrystal2 = new ItemRequirement("Heart crystal", ItemID.HEART_CRYSTAL_745);
		heartCrystal2.setHighlightInInventory(true);

		normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);

		darkDagger = new ItemRequirement("Dark dagger", ItemID.DARK_DAGGER);
		darkDagger.setHighlightInInventory(true);
		darkDagger.setTooltip("You can get another from Echned at the source");

		glowingDagger = new ItemRequirement("Glowing dagger", ItemID.GLOWING_DAGGER);
		glowingDagger.setHighlightInInventory(true);

		force = new ItemRequirement("Holy force", ItemID.HOLY_FORCE);

		forceHighlighted = new ItemRequirement("Holy force", ItemID.HOLY_FORCE);
		forceHighlighted.setHighlightInInventory(true);

		yommiTotem = new ItemRequirement("Yommi totem", ItemID.YOMMI_TOTEM);
		yommiTotem.setTooltip("You'll need to grow another if you've lost it");

		yommiTotemHighlighted = new ItemRequirement("Yommi totem", ItemID.YOMMI_TOTEM);
		yommiTotemHighlighted.setTooltip("You'll need to grow another if you've lost it");
		yommiTotemHighlighted.setHighlightInInventory(true);

		gildedTotem = new ItemRequirement("Gilded totem", ItemID.GILDED_TOTEM);
		gildedTotem.setTooltip("You can get another from Gujuo");
	}

	private void setupConditions()
	{
		inGuild = new ZoneRequirement(guild1, guild2, guild3);
		inKhazari = new ZoneRequirement(khazari1, khazari2, khazari3, khazari4);
		inWest = new ZoneRequirement(westKhazari);
		inMiddle = new ZoneRequirement(middleKhazari);
		inEast = new ZoneRequirement(eastKhazari);
		inCaveRoom1 = new ZoneRequirement(caveRoom1P1, caveRoom1P2);
		inCaves = new ZoneRequirement(caves, challengeCave);
		inCaveRoom2 = new ZoneRequirement(caveRoom2P1, caveRoom2P2);
		inCaveRoom3 = new ZoneRequirement(caveRoom3);
		inCaveRoom4 = new ZoneRequirement(caveRoom4P1, caveRoom4P2, caveRoom4P3);
		inCaveRoom5 = new ZoneRequirement(caveRoom5P1, caveRoom5P2, caveRoom5P3);
		inCaveRoom6 = new ZoneRequirement(caveRoom6P1, caveRoom6P2);
		inFire = new ZoneRequirement(fire1, fire2, fire3);
		inChallengeCave = new ZoneRequirement(challengeCave);

		completeTextAppeared = new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You have already completed this part of the map.");

		completeEast = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "Eastern Kharazi Jungle- *** Completed"),
			new Conditions(inEast, completeTextAppeared));
		completeMiddle = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT,  "Middle Kharazi Jungle- *** Completed"),
			new Conditions(inMiddle, completeTextAppeared));
		completeWest = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT,  "Western Kharazi Jungle- *** Completed"),
			new Conditions(inWest, completeTextAppeared));

		finishedMap = new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You have completed mapping the Khazari");

		gujuoNearby = new NpcCondition(NpcID.GUJUO);

		talkedToUngadulu = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(229, 1, "The Shaman throws himself down on the floor and starts convulsing."),
			new WidgetTextRequirement(119, 3, true, "is acting weird and talking a lot of nonsense"));

		hadSketch = new Conditions(true, LogicType.OR, sketch);

		searchedMarkedWall = new WidgetTextRequirement(229, 1, "You can see a message on the wall");

		addedSoulRune = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(229, 1,  "You slide the Soul Rune into the first"),
			new WidgetTextRequirement(229, 1, "You find the letter 'S'"));
		addedMindRune = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(229, 1, "You slide the Mind Rune into the second"),
			new WidgetTextRequirement(229, 1, "You find the letters 'SM'"));
		addedEarthRune = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(229, 1, "You slide the Earth Rune into the third"),
			new WidgetTextRequirement(229, 1, "You find the letters 'SME'"));

		addedLawRune = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(229, 1, "You slide the Law Rune into the fourth"),
			new WidgetTextRequirement(229, 1, "You find the letters 'SMEL'"));

		addedLawRune2 = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(229, 1, "You slide the Law Rune into the fifth"),
			new WidgetTextRequirement(229, 1, "You find the letters 'SMELL'"));

		sapphirePlaced = new Conditions(true, new ItemOnTileRequirement(sapphire, new WorldPoint(2781, 9291, 0)));
		emeraldPlaced = new Conditions(true, new ItemOnTileRequirement(emerald, new WorldPoint(2757, 9297, 0)));
		rubyPlaced = new Conditions(true, new ItemOnTileRequirement(ruby, new WorldPoint(2767, 9289, 0)));
		diamondPlaced = new Conditions(true, new ItemOnTileRequirement(diamond, new WorldPoint(2774, 9287, 0)));
		opalPlaced = new Conditions(true, new ItemOnTileRequirement(opal, new WorldPoint(2764, 9309, 0)));
		jadePlaced = new Conditions(true, new ItemOnTileRequirement(jade, new WorldPoint(2771, 9303, 0)));
		topazPlaced = new Conditions(true, new ItemOnTileRequirement(topaz, new WorldPoint(2772, 9295, 0)));

		bookAppearing = new ChatMessageRequirement("You feel a powerful force picking you up....");
		bookNearby = new ItemOnTileRequirement(ItemID.BINDING_BOOK);

		nezNearby = new NpcInteractingRequirement(NpcID.NEZIKCHENED);

		addedRope = new Conditions(true, new ObjectCondition(ObjectID.WINCH_2935, new WorldPoint(2761, 9331, 0)));
		echnedNearby = new NpcCondition(NpcID.ECHNED_ZEKIN);
		viyeldiNearby = new NpcCondition(NpcID.VIYELDI);

		sacredWaterNearby = new ObjectCondition(ObjectID.SACRED_WATER);

		saplingNearby = new ObjectCondition(ObjectID.YOMMI_TREE_SAPLING);

		adultNearby = new ObjectCondition(ObjectID.ADULT_YOMMI_TREE);
		felledNearby = new ObjectCondition(ObjectID.FELLED_YOMMI_TREE);
		trimmedNearby = new ObjectCondition(ObjectID.TRIMMED_YOMMI);
		totemNearby = new ObjectCondition(ObjectID.TOTEM_POLE_2954);

		ranalphNearby = new NpcCondition(NpcID.RANALPH_DEVERE);
		irvigNearby = new NpcCondition(NpcID.IRVIG_SENAY);
		sanNearby = new NpcCondition(NpcID.SAN_TOJALON);
	}

	private void setupSteps()
	{
		talkToGuard = new NpcStep(this, NpcID.LEGENDS_GUARD, new WorldPoint(2729, 3348, 0), "Talk to one of the guards outside the Legends' Guild.");
		talkToGuard.addDialogSteps("Can I speak to someone in charge?", "Can I go on the quest?", "Yes, I'd like to talk to Grand Vizier Erkle.");
		talkToRadimus = new NpcStep(this, NpcID.RADIMUS_ERKLE, new WorldPoint(2725, 3368, 0), "Talk to Radimus Erkle inside the Legends' Guild's grounds.");
		talkToRadimus.addDialogSteps("Yes actually, what's involved?", "Yes, it sounds great!");
		enterJungle = new DetailedQuestStep(this, "Travel to the Khazari Jungle in south Karamja. You'll need to cut through some trees and bushes to enter.", radimusNotes, axe, machete, papyrus3, charcoal3);
		sketchEast = new DetailedQuestStep(this, new WorldPoint(2944, 2916, 0), "Stand in the east of the Khazari Jungle and right-click complete the Radimus note.", radimusNotesHighlight, papyrus, charcoal);
		sketchEast.addDialogStep("Start Mapping Khazari Jungle.");
		sketchMiddle = new DetailedQuestStep(this, new WorldPoint(2852, 2915, 0), "Stand in the middle of the Khazari Jungle and right-click complete the Radimus note.", radimusNotesHighlight, papyrus, charcoal);
		sketchMiddle.addDialogStep("Start Mapping Khazari Jungle.");
		sketchWest = new DetailedQuestStep(this, new WorldPoint(2791, 2917, 0), "Stand in the west of the Khazari Jungle and right-click complete the Radimus note.", radimusNotesHighlight, papyrus, charcoal);
		sketchWest.addDialogStep("Start Mapping Khazari Jungle.");
		useNotes = new NpcStep(this, NpcID.JUNGLE_FORESTER, new WorldPoint(2867, 2942, 0), "Use the Radimus notes on a Jungle Forester outside the Khazari Jungle.", true, completeNotesHighlighted);
		useNotes.addAlternateNpcs(NpcID.JUNGLE_FORESTER_3955);
		useNotes.addDialogStep("Yes, go ahead make a copy!");
		enterJungleWithRoarer = new DetailedQuestStep(this, "Re-enter the Khazari Jungle. You'll need to cut through some trees and bushes to enter.", completeNotes, bullRoarer, axe, machete, lockpick, pickaxe, soulRune, mindRune, earthRune, lawRune2, opal, jade, topaz, sapphire, emerald, ruby, diamond);
		spinBull = new DetailedQuestStep(this, "Spin the bull roarer until Gujuo appears.", bullRoarerHighlight);
		talkToGujuo = new NpcStep(this, NpcID.GUJUO, "Talk to Gujuo.");
		talkToGujuo.addDialogSteps("I was hoping to attract the attention of a native.", "I want to develop friendly relations with your people.", "Can you get your people together?", "What can we do instead then?", "How do we make the totem pole?", "I will release Ungadulu...");

		enterMossyRock = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the Mossy Rocks in the north west of the Khazari.");
		enterMossyRock.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		investigateFireWall = new ObjectStep(this, ObjectID.FIRE_WALL, new WorldPoint(2790, 9333, 0), "Right-click investigate the fire wall.");
		investigateFireWall.addDialogSteps("How can I extinguish the flames?", "Where do I get pure water from?");

		leaveCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2903, new WorldPoint(2773, 9342, 0), "Leave the cave back to the surface.");
		spinBullAgain = new DetailedQuestStep(this, "Spin the bull roarer until Gujuo appears.", bullRoarerHighlight);
		talkToGujuoAgain = new NpcStep(this, NpcID.GUJUO, "Talk to Gujuo about pure water and the vessel needed for it.");
		talkToGujuoAgain.addDialogSteps("I need some pure water to douse some magic flames.", "What kind of a vessel?");

		enterMossyRockAgain = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the Mossy Rocks in the north west of the Khazari.");
		enterMossyRockAgain.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		enterBookcase = new ObjectStep(this, ObjectID.BOOKCASE_2911, new WorldPoint(2796, 9339, 0), "Right-click search the bookcase and slide past it.");
		enterBookcase.addDialogStep("Yes please!");

		enterGate1 = new ObjectStep(this, ObjectID.ANCIENT_GATE, new WorldPoint(2810, 9332, 0), "Right-click search the gate.", lockpick);
		enterGate2 = new ObjectStep(this, ObjectID.ANCIENT_GATE_2922, new WorldPoint(2810, 9314, 0), "Smash through the boulders and enter the gate at the end of the corridor.", pickaxe);
		enterGate2.addDialogStep("Yes, I'm very strong, I'll force them open.");

		searchMarkedWall = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Follow the cave around until you reach a marked wall. Right-click search it. Kill a Deathwing for the Karamja Achievement Diary whilst you're here.");
		searchMarkedWall.addDialogSteps("Investigate the outline of the door.", "Yes, I'll go through!");
		useSoul = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Use a Soul Rune on the marked wall.", soulRuneHighlight);
		useSoul.addIcon(ItemID.SOUL_RUNE);
		useMind = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Use a Mind Rune on the marked wall.", mindRuneHighlight);
		useMind.addIcon(ItemID.MIND_RUNE);
		useEarth = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Use a Earth Rune on the marked wall.", earthRuneHighlight);
		useEarth.addIcon(ItemID.EARTH_RUNE);
		useLaw = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Use a Law Rune on the marked wall.", lawRuneHighlight);
		useLaw.addIcon(ItemID.LAW_RUNE);
		useLaw2 = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Use another Law Rune on the marked wall.", lawRuneHighlight);
		useLaw2.addIcon(ItemID.LAW_RUNE);

		useSapphire = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2781, 9291, 0), "Use a sapphire on the south-east carved rock. If you have already, search it instead.", sapphireHighlighted);
		useSapphire.addIcon(ItemID.SAPPHIRE);
		useEmerald = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2757, 9297, 0), "Use a emerald on the west carved rock. If you have already, search it instead.", emeraldHighlighted);
		useEmerald.addIcon(ItemID.EMERALD);
		useRuby = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2767, 9289, 0), "Use a ruby on the south-west carved rock. If you have already, search it instead.", rubyHighlighted);
		useRuby.addIcon(ItemID.RUBY);
		useDiamond = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2774, 9287, 0), "Use a diamond on the south carved rock. If you have already, search it instead.", diamondHighlighted);
		useDiamond.addIcon(ItemID.DIAMOND);
		useOpal = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2764, 9309, 0), "Use an opal on the far west carved rock. If you have already, search it instead.", opalHighlighted);
		useOpal.addIcon(ItemID.OPAL);
		useJade = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2771, 9303, 0), "Use a jade on the north-east carved rock. If you have already, search it instead.", jadeHighlighted);
		useJade.addIcon(ItemID.JADE);
		useTopaz = new ObjectStep(this, ObjectID.CARVED_ROCK, new WorldPoint(2772, 9295, 0), "Use a topaz on the east carved rock. If you have already, search it instead.", topazHighlighted);
		useTopaz.addIcon(ItemID.RED_TOPAZ);

		waitForBook = new DetailedQuestStep(this, "Wait for the binding book to appear.");
		pickUpBook = new ItemStep(this, "Pick up the binding book", bindingBook);
		pickUpBook.addSubSteps(waitForBook);

		makeBowl = new DetailedQuestStep(this, "Travel to an anvil and make a gold bowl.", goldBar2, sketch, hammer);

		enterJungleWithBowl = new DetailedQuestStep(this, "Return to the Khazari Jungle with your gold bowl, and be prepared for a fight.", bullRoarer, goldBowl, bindingBook, axe, machete, combatGear);

		spinBullToBless = new DetailedQuestStep(this, "Spin the bull roarer until Gujuo appears.", bullRoarerHighlight, goldBowl);

		talkToGujuoWithBowl = new NpcStep(this, NpcID.GUJUO, "Talk to Gujuo to bless the gold bowl.");
		talkToGujuoWithBowl.addDialogSteps("Yes, I'd like you to bless my gold bowl.");

		useMacheteOnReeds = new ObjectStep(this, ObjectID.TALL_REEDS, new WorldPoint(2836, 2916, 0), "Use a machete on the tall reeds next the Khazari's water pool.", macheteHighlighted);
		useMacheteOnReeds.addIcon(ItemID.MACHETE);
		useReedOnPool = new ObjectStep(this, ObjectID.WATER_POOL, new WorldPoint(2838, 2916, 0), "Use the reed on the water pool.", reed, goldBowlBlessed);
		useReedOnPool.addIcon(ItemID.HOLLOW_REED);

		enterMossyRockWithBowl = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the Mossy Rocks in the north west of the Khazari.", goldBowlFull);
		enterMossyRockWithBowl.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		useBowlOnFireWall = new ObjectStep(this, ObjectID.FIRE_WALL, new WorldPoint(2790, 9333, 0), "Use the golden bowl on the wall of fire.", goldBowlFullHighlighted);
		useBowlOnFireWall.addIcon(ItemID.GOLDEN_BOWL_726);

		useBindingBookOnUngadulu = new NpcStep(this, NpcID.UNGADULU, new WorldPoint(2792, 9328, 0), "Use the binding book on Ungadulu.", bindingBookHighlighted);
		useBindingBookOnUngadulu.addAlternateNpcs(NpcID.UNGADULU_3958);
		useBindingBookOnUngadulu.addIcon(ItemID.BINDING_BOOK);

		fightNezikchenedInFire = new NpcStep(this, NpcID.NEZIKCHENED, new WorldPoint(2793, 9329, 0), "Fight Nezikchened.");

		enterMossyRockAfterFight = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the Mossy Rocks in the north west of the Khazari.", goldBowlFull);
		enterMossyRockAfterFight.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		enterFireAfterFight = new ObjectStep(this, ObjectID.FIRE_WALL, new WorldPoint(2790, 9333, 0), "Touch the wall of fire to pass it.");

		talkToUngadulu = new NpcStep(this, NpcID.UNGADULU, new WorldPoint(2792, 9328, 0), "Right-click talk to Ungadulu.");
		talkToUngadulu.addSubSteps(enterMossyRockAfterFight, enterFireAfterFight);
		talkToUngadulu.addDialogSteps("I need to collect some Yommi tree seeds for Gujuo.");

		useBowlOnSeeds = new DetailedQuestStep(this, "Use the blessed bowl of water on the yommi seeds.", yommiSeeds, goldBowlFullHighlighted);
		leaveCaveWithSeed = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2903, new WorldPoint(2773, 9342, 0), "Leave the cave back to the surface.");
		plantSeed = new ObjectStep(this, ObjectID.FERTILE_SOIL, new WorldPoint(2779, 2917, 0), "Plant the seeds in fertile soil.", germinatedSeedsHighlighted);
		plantSeed.addIcon(ItemID.YOMMI_TREE_SEEDS_736);

		useMacheteOnReedsAgain = new ObjectStep(this, ObjectID.TALL_REEDS, new WorldPoint(2836, 2916, 0), "Use a machete on the tall reeds next the Khazari's water pool.", macheteHighlighted);
		useMacheteOnReedsAgain.addIcon(ItemID.MACHETE);
		useReedOnPoolAgain = new ObjectStep(this, ObjectID.WATER_POOL, new WorldPoint(2838, 2916, 0), "Use the reed on the water pool.", reed, goldBowlBlessed);
		useReedOnPoolAgain.addIcon(ItemID.HOLLOW_REED);
		useReedOnPoolAgain.addAlternateObjects(ObjectID.POLLUTED_WATER);

		spinBullAfterSeeds = new DetailedQuestStep(this, "Spin the bull roarer until Gujuo appears.", bullRoarerHighlight);
		talkToGujuoAfterSeeds = new NpcStep(this, NpcID.GUJUO, "Talk to Gujuo about what's happened to the water pool.");
		talkToGujuoAfterSeeds.addDialogSteps("The sacred water pool has dried up and I need more water.", "Where is the source of the spring of pure water?");

		enterJungleAfterSeeds = new DetailedQuestStep(this, "Return to the Khazari Jungle with your bull roarer, and be prepared for some fights.",
			bullRoarer, runeOrDragonAxe, machete, pickaxe, lockpick, vialOfWater, snakeWeed, ardrigal, chargeOrbRunes, unpoweredOrb, rope, goldBowlBlessed, combatGear, normalSpellbook);

		useMacheteOnReedsAgain.addSubSteps(enterJungleAfterSeeds);

		enterJungleToGoToSource = new DetailedQuestStep(this, "Return to the Khazari Jungle and be prepared for some fights.",
			runeOrDragonAxe, machete, pickaxe, lockpick, braveryPotion, chargeOrbRunes, unpoweredOrb, rope, goldBowlBlessed,
			combatGear, normalSpellbook);

		addArdrigal = new DetailedQuestStep(this, "Add ardrigal and snake weed to a vial of water to make a bravery potion.", ardrigal, vialOfWater);
		addSnake = new DetailedQuestStep(this, "Add snake weed to the ardigal mixture.", ardrigalMixture, snakeWeed);
		addArdrigalToSnake = new DetailedQuestStep(this, "Add ardrigal to the snakeweed mixture.", snakeMixture, ardrigal);
		addArdrigal.addSubSteps(addSnake, addArdrigalToSnake);

		enterMossyRockToSource = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the Mossy Rocks in the north west of the Khazari.");
		enterMossyRockToSource.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		enterBookcaseToSource = new ObjectStep(this, ObjectID.BOOKCASE_2911, new WorldPoint(2796, 9339, 0), "Right-click search the bookcase and slide past it.");
		enterBookcaseToSource.addDialogStep("Yes please!");

		enterGate1ToSource = new ObjectStep(this, ObjectID.ANCIENT_GATE, new WorldPoint(2810, 9332, 0), "Right-click search the gate.", lockpick);
		enterGate2ToSource = new ObjectStep(this, ObjectID.ANCIENT_GATE_2922, new WorldPoint(2810, 9314, 0), "Smash through the boulders and enter the gate at the end of the corridor.", pickaxe);
		enterGate2ToSource.addDialogStep("Yes, I'm very strong, I'll force them open.");

		searchMarkedWallToSource = new ObjectStep(this, ObjectID.MARKED_WALL, new WorldPoint(2779, 9305, 0), "Follow the cave around until you reach a marked wall. Use it.");
		searchMarkedWallToSource.addDialogSteps("Investigate the outline of the door.", "Yes, I'll go through!");

		useSpellOnDoor = new ObjectStep(this, ObjectID.ANCIENT_GATE_2930, new WorldPoint(2763, 9314, 0), "Cast a charge orb spell on the ancient gate.", chargeOrbRunes, unpoweredOrb, normalSpellbook);

		useRopeOnWinch = new ObjectStep(this, ObjectID.WINCH_2934, new WorldPoint(2761, 9331, 0), "Use a rope on the winch. If you've already done so, search it instead.", ropeHighlighted);
		useRopeOnWinch.addIcon(ItemID.ROPE);

		drinkBraveryPotionAndClimbDown = new ObjectStep(this, ObjectID.WINCH_2934, new WorldPoint(2761, 9331, 0), "Drink the bravery potion, then climb down the winch.", braveryPotionHighlighted);
		drinkBraveryPotionAndClimbDown.addAlternateObjects(ObjectID.WINCH_2935);
		drinkBraveryPotionAndClimbDown.addDialogSteps("Yes, I'll bravely drink the bravery potion.", "Yes, I'll shimmy down the rope into possible doom.");

		climbDownWinch = new ObjectStep(this, ObjectID.WINCH_2934, new WorldPoint(2761, 9331, 0), "Climb down the winch.");
		climbDownWinch.addAlternateObjects(ObjectID.WINCH_2935);
		climbDownWinch.addDialogStep("Yes, I'll shimmy down the rope into possible doom.");

		enterMossyRockForViyeldi = new ObjectStep(this, ObjectID.MOSSY_ROCK, new WorldPoint(2782, 2937, 0), "Search and then enter the Mossy Rocks in the north west of the Khazari.",
			runeOrDragonAxe, machete, pickaxe, lockpick, chargeOrbRunes, unpoweredOrb, goldBowlBlessed, combatGear, normalSpellbook);
		enterMossyRockForViyeldi.addDialogStep("Yes, I'll crawl through, I'm very athletic.");

		useCrystalsOnFurnace = new ObjectStep(this, ObjectID.FURNACE_2966, new WorldPoint(2427, 4727, 0),
			"Follow the path down, and kill each of the 3 skeletons for crystal pieces. Use them on the furnace in the north east of the area.",
			lumpCrystal, chunkCrystal, hunkCrystal);

		useHeartOnRock = new ObjectStep(this, ObjectID.MOSSY_ROCK_2965, new WorldPoint(2411, 4716, 0),
			"Use the crystal heart on the mossy rock in the centre of the area.", heartCrystal);
		useHeartOnRock.addIcon(ItemID.HEART_CRYSTAL);

		useHeartOnRecess = new ObjectStep(this, ObjectID.RECESS, new WorldPoint(2422, 4691, 0),
			"Use the heart on the recess next to the barrier to the south.", heartCrystal2);
		useHeartOnRecess.addIcon(ItemID.HEART_CRYSTAL_745);

	    pushBoulder = new NpcStep(this, NpcID.BOULDER_3967, new WorldPoint(2393, 4679, 0),
			"Enter the south room and attempt to push one of the boulders to the west.", true);

	    talkToEchned = new NpcStep(this, NpcID.ECHNED_ZEKIN, new WorldPoint(2385, 4681, 0), "Talk to Echned Zekin.");
	    talkToEchned.addDialogSteps("Who's asking?", "Yes, I need it for my quest.", "What can I do about that?",
			"I'll do what I must to get the water.", "Ok, I'll do it.");

	    pickUpHat = new DetailedQuestStep(this, new WorldPoint(2379, 4712, 0),
			"Attempt to pick up the wizard hat near the rope down and kill Viyeldi with the dark dagger. If he doesn't appear," +
				" you'll need to talk to Ungadulu for a holy force spell.", darkDagger);
	    killViyeldi = new NpcStep(this, NpcID.VIYELDI, new WorldPoint(2379, 4712, 0), "Kill Viyeldi.", darkDagger);
	    killViyeldi.addIcon(ItemID.DARK_DAGGER);
	    pickUpHat.addSubSteps(killViyeldi);

		pushBoulderAgain =  new NpcStep(this, NpcID.BOULDER_3967, new WorldPoint(2393, 4679, 0),
			"Return to Echned in the south room with the dagger. Be prepared for a fight with Nezikchened again.", true, glowingDagger);
		pushBoulderWithForce = new NpcStep(this, NpcID.BOULDER_3967, new WorldPoint(2393, 4679, 0),
			"Return to Echned in the south room with holy force. Be prepared for a fight with Nezikchened again.", true, force);
		pushBoulderAgain.addSubSteps(pushBoulderWithForce);

		giveDaggerToEchned = new NpcStep(this, NpcID.ECHNED_ZEKIN, new WorldPoint(2385, 4681, 0),
			"Use the glowing dagger on Echned Zekin.");
		giveDaggerToEchned.addIcon(ItemID.GLOWING_DAGGER);

		castForce = new DetailedQuestStep(this, "Cast holy force.", forceHighlighted);

		giveDaggerToEchned.addSubSteps(castForce);

		fightNezikchenedAtSource = new NpcStep(this, NpcID.NEZIKCHENED, new WorldPoint(2385, 4681, 0), "Defeat Nezikchened.");

		pushBoulderAfterFight = new NpcStep(this, NpcID.BOULDER_3967, new WorldPoint(2393, 4679, 0), "Push one of the boulders in the south room.", true);

		useBowlOnSacredWater = new ObjectStep(this, ObjectID.SACRED_WATER, "Use the blessed bowl on the sacred water.", goldBowlBlessed);

		returnToSurface = new DetailedQuestStep(this, "Return to the surface.");

		useWaterOnTree = new ObjectStep(this, ObjectID.YOMMI_TREE_SAPLING, "Use the golden bowl on the sapling.", goldBowlFullHighlighted);

		useMacheteOnReedsEnd = new ObjectStep(this, ObjectID.TALL_REEDS, new WorldPoint(2836, 2916, 0), "Use a machete on the tall reeds next the Khazari's water pool.", macheteHighlighted, goldBowlBlessed);
		useMacheteOnReedsEnd.addIcon(ItemID.MACHETE);
		useReedOnPoolEnd = new ObjectStep(this, ObjectID.WATER_POOL, new WorldPoint(2838, 2916, 0), "Use the reed on the water pool.", reed, goldBowlBlessed);
		useReedOnPoolEnd.addIcon(ItemID.HOLLOW_REED);

		enterJungleToPlant = new DetailedQuestStep(this, "Return to the Khazari Jungle and be prepared for some fights.",
			runeOrDragonAxe, machete, goldBowlBlessed, germinatedSeeds, combatGear);

		useAxe = new ObjectStep(this, ObjectID.ADULT_YOMMI_TREE, "Use your axe on the adult yommi tree.", runeOrDragonAxe);
		useAxe.addIcon(ItemID.RUNE_AXE);
		useAxeAgain = new ObjectStep(this, ObjectID.FELLED_YOMMI_TREE, "Use your axe on the felled yommi tree.", runeOrDragonAxe);
		useAxeAgain.addIcon(ItemID.RUNE_AXE);

		craftTree = new ObjectStep(this, ObjectID.TRIMMED_YOMMI, "Use your axe once more on the tree.", runeOrDragonAxe);
		craftTree.addIcon(ItemID.RUNE_AXE);

		pickUpTotem = new ObjectStep(this, ObjectID.TOTEM_POLE_2954, "Pick up the totem pole.");

		useTotemOnTotem = new ObjectStep(this, ObjectID.TOTEM_POLE_2938, new WorldPoint(2852, 2917, 0), "Put Protect from Melee on, and use the new totem on one of the corrupted totems.", yommiTotemHighlighted, combatGear);
		useTotemOnTotem.addAlternateObjects(ObjectID.TOTEM_POLE_2936);
		useTotemOnTotem.addIcon(ItemID.YOMMI_TOTEM);
		killRanalph = new NpcStep(this, NpcID.RANALPH_DEVERE, "Kill Ranalph.");
		killIrvig = new NpcStep(this, NpcID.IRVIG_SENAY, "Kill Irvig.");
		killSan = new NpcStep(this, NpcID.SAN_TOJALON, "Kill San.");
		defeatDemon = new NpcStep(this, NpcID.NEZIKCHENED, "Defeat Nezikchened.");

		useTotemOnTotemAgain = new ObjectStep(this, ObjectID.TOTEM_POLE_2938, new WorldPoint(2852, 2917, 0), "Use the new totem on one of the corrupted totems.", yommiTotemHighlighted);
		useTotemOnTotemAgain.addAlternateObjects(ObjectID.TOTEM_POLE_2936);
		useTotemOnTotemAgain.addIcon(ItemID.YOMMI_TOTEM);

		returnToRadimus = new NpcStep(this, NpcID.RADIMUS_ERKLE, new WorldPoint(2725, 3368, 0), "Return to Radimus Erkle inside the Legends' Guild's grounds.", gildedTotem, completeNotes);
		returnToRadimus.setMaxRoamRange(5);
		talkToRadimusInGuild = new NpcStep(this, NpcID.RADIMUS_ERKLE, new WorldPoint(2729, 3382, 0), "Talk to Radimus Erkle inside the Legends' Guild");
		talkToRadimusInGuild.setMaxRoamRange(13);
		talkToRadimusInGuild.addDialogStep("Yes, I'll train now.");
		talkToRadimusInGuildAgain = new NpcStep(this, NpcID.RADIMUS_ERKLE, new WorldPoint(2729, 3382, 0), "Talk to Radimus Erkle inside the Legends' Guild once more.");
		talkToRadimusInGuildAgain.setMaxRoamRange(13);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(charcoal3, papyrus3, machete, runeOrDragonAxe, goldBar2, hammer, rope, lockpick, pickaxe, vialOfWater, ardrigal, snakeWeed, sapphire, emerald, ruby, diamond, opal, jade, topaz, soulRune, mindRune, earthRune, lawRune2, unpoweredOrb, chargeOrbRunes, combatGear);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Nezikchened (level 187) 3 times", "Ranalph Devere (level 92)", "Irvig Senay (level 100)", "San Tojalon (level 106)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(4);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("7,650 Experience Lamps (Choice of Attack, Defence, Strength, Hitpoints, Prayer, Magic, Woodcutting, Crafting, Smithing, Herblore, Agility or Thieving", ItemID.ANTIQUE_LAMP, 4)); //4447 Is placeholder for filtering.
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Kharazi Jungle"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToGuard, talkToRadimus)));
		allSteps.add(new PanelDetails("Mapping Khazari", Arrays.asList(enterJungle, sketchWest, sketchMiddle, sketchEast, useNotes), axe, machete, papyrus3, charcoal3, anyNotes));
		allSteps.add(new PanelDetails("Contacting the locals", Arrays.asList(enterJungleWithRoarer, spinBull, talkToGujuo, enterMossyRock, investigateFireWall, leaveCave, spinBullAgain,
			talkToGujuoAgain),
			anyNotes, bullRoarer, axe, machete, lockpick, pickaxe, soulRune, mindRune, earthRune, lawRune2, opal, jade, topaz, sapphire, emerald, ruby, diamond));
		PanelDetails runePuzzlePanel = new PanelDetails("Unlocking the caves", Arrays.asList(enterMossyRockAgain, enterBookcase, enterGate1, enterGate2, searchMarkedWall, useSoul, useMind, useEarth, useLaw, useLaw2),
			completeNotes, bullRoarer, axe, machete, lockpick, pickaxe, soulRune, mindRune, earthRune, lawRune2, opal, jade,
			topaz,	sapphire, emerald, ruby, diamond);
		runePuzzlePanel.setLockingStep(runePuzzle);
		allSteps.add(runePuzzlePanel);

		PanelDetails gemPuzzlePanel = new PanelDetails("Getting a binding book", 
			Arrays.asList(useSapphire, useDiamond, useRuby, useTopaz, useJade, useEmerald, useOpal, pickUpBook),
			completeNotes, bullRoarer, axe, machete, lockpick, pickaxe, opal, jade, topaz, sapphire, emerald, ruby, diamond);
		gemPuzzlePanel.setLockingStep(gemPuzzle);
		allSteps.add(gemPuzzlePanel);

		allSteps.add(new PanelDetails("Freeing Ungadulu", 
			Arrays.asList(makeBowl, enterJungleWithBowl, spinBullToBless, talkToGujuoWithBowl, useMacheteOnReeds, useReedOnPool, enterMossyRockWithBowl, useBowlOnFireWall, useBindingBookOnUngadulu,
				fightNezikchenedInFire, talkToUngadulu), completeNotes, bullRoarer, goldBar2, hammer, axe, machete, combatGear));

		allSteps.add(new PanelDetails("Attempted planting", Arrays.asList(
		useBowlOnSeeds, leaveCaveWithSeed, useMacheteOnReedsAgain, useReedOnPoolAgain, spinBullAfterSeeds,
			talkToGujuoAfterSeeds), completeNotes, machete, axe, goldBowlFull, yommiSeeds, bullRoarer));

		allSteps.add(new PanelDetails("To the source", Arrays.asList(
			addArdrigal, enterMossyRockToSource, enterBookcaseToSource, enterGate1ToSource, enterGate2ToSource, searchMarkedWallToSource, useSpellOnDoor, useRopeOnWinch, climbDownWinch),
				completeNotes, ardrigal, snakeWeed, vialOfWater, machete, runeOrDragonAxe, lockpick, pickaxe, chargeOrbRunes, unpoweredOrb, rope, goldBowlBlessed, germinatedSeeds, combatGear));

		allSteps.add(new PanelDetails("Unlocking the source", Arrays.asList(
			useCrystalsOnFurnace, useHeartOnRock, useHeartOnRecess, pushBoulder, pickUpHat, giveDaggerToEchned, fightNezikchenedAtSource, pushBoulderAfterFight, useBowlOnSacredWater, returnToSurface),
				completeNotes, machete, runeOrDragonAxe, lockpick, pickaxe, chargeOrbRunes, unpoweredOrb, goldBowlBlessed, germinatedSeeds, combatGear));

		allSteps.add(new PanelDetails("Making a totem", Arrays.asList(
			plantSeed, useWaterOnTree, useAxe, useAxeAgain, craftTree, pickUpTotem),
				completeNotes, machete, runeOrDragonAxe, goldBowlBlessed, germinatedSeeds, combatGear));

		allSteps.add(new PanelDetails("Placing the totem", Arrays.asList(
			useTotemOnTotem, killSan, killIrvig, killRanalph, defeatDemon, useTotemOnTotemAgain),
				completeNotes, machete, runeOrDragonAxe, yommiTotem, combatGear));

		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(
			returnToRadimus, talkToRadimusInGuild, talkToRadimusInGuildAgain),
			gildedTotem, completeNotesHighlighted));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(107));
		req.add(new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HEROES_QUEST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.UNDERGROUND_PASS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 50, true));
		req.add(new SkillRequirement(Skill.CRAFTING, 50, true));
		req.add(new SkillRequirement(Skill.HERBLORE, 45, true));
		req.add(new SkillRequirement(Skill.MAGIC, 56, true));
		req.add(new SkillRequirement(Skill.MINING, 52, true));
		req.add(new SkillRequirement(Skill.PRAYER, 42, true));
		req.add(new SkillRequirement(Skill.SMITHING, 50, true));
		req.add(new SkillRequirement(Skill.STRENGTH, 50, true));
		req.add(new SkillRequirement(Skill.THIEVING, 50, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 50, true));
		return req;
	}
}
