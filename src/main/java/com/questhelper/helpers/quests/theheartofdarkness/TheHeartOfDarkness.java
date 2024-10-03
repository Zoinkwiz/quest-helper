/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.theheartofdarkness;

import com.google.common.collect.Lists;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.steps.widget.WidgetHighlight;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.questhelper.requirements.util.LogicHelper.*;

public class TheHeartOfDarkness extends BasicQuestHelper
{
    ManualRequirement knowPoemSolution, hasReadCompletedNote;

    ManualRequirement inspectedAirMarkings, inspectedEarthMarkings, inspectedWaterMarkings, inspectedFireMarkings;
    Requirement takenOrUsedAirIcon, takenOrUsedEarthIcon,
            takenOrUsedFireIcon, takenOrUsedWaterIcon, repairedWaterStatue, repairedFireStatue, repairedEarthStatue, repairedAirStatue;

    VarbitRequirement activatedFirstStatue, activatedSecondStatue, activatedThirdStatue, activatedFourthStatue;

    ObjectStep[] statueActivateSteps = new ObjectStep[4];

    VarbitRequirement[] activateStatueRequirements = new VarbitRequirement[4];

    // Required
    ItemRequirement combatGear, food, coins;

    // Recommended
    ItemRequirement quetzalFeed, limestoneBrick, softClay, pickaxe, civitasIllaFortisTeleport, prayerPotions, staminaPotions;

    // Quest items
    ItemRequirement towerKey, book, poem, scrapOfPaper1, scrapOfPaper2, scrapOfPaper3, completedNote, emissaryHood, emissaryTop, emissaryBottom,
            emissaryBoots, emissaryRobesEquipped, emissaryRobes, airIcon, waterIcon, earthIcon, fireIcon;

    Requirement atTeomat, builtLandingInOverlook, talkedToSergius, talkedToCaritta, talkedToFelius, princeIsFollowing, inFirstTrialRoom,
            inSecondTrialRoom, southEastGateUnlocked, southWestChestOpened, hasReadPoem, knowAboutDirections, inArrowPuzzle, combatStarted,
            startedInvestigation, freeInvSlots4;

    Requirement tenochGuilty, hasTalkedToTenoch, siliaGuilty, hasTalkedToSilia, hasTalkedToAdrius, eleuiaGuilty, hasTalkedToEleuia, inTempleCutscene,
            inFirstIceRoom, inSecondIceAreaFirstRoom, inSecondIceAreaSecondRoom, inThirdIceArea, pulledFirstLever, pulledSecondLever, pulledThirdLever,
            unlockedShortcut, inBossRoom;

    ObjectStep activateSecondStatue, activateFirstStatue, activateThirdStatue, activateFourthStatue;

    Zone teomat, firstTrialRoom, secondTrialRoom, firstIceRoom, secondIceAreaFirstRoom, secondIceAreaSecondRoom, thirdIceArea, bossRoom;

    DetailedQuestStep talkToItzlaAtTeomat,travelToGorge, talkToBartender, restOnBed, talkToPrinceAfterRest, talkToShopkeeper, talkToPrinceInPubAgain,
            talkToPrinceAtTower, buildSalvagerOverlookLandingSite, talkToPrinceAtTowerAfterLanding, talkToNova, talkToSergius, talkToFelius, talkToCaritta, talkToPrinceAfterRecruits,
            talkToJanus, climbUpToFirstTrial;
    PuzzleWrapperStep pickpocketAscended, useKeyOnSouthEastGate, searchChestForBookAndPaper, readPoem, talkToPrinceAfterPoem,
            openKeywordChestNorthWest, combineScraps, readCompletedNote;

    LockedChestPuzzle openKeywordChestSouthWest;

    ArrowChestPuzzleStep inputArrows;

    PuzzleWrapperStep openKeywordChestSouthWestPuzzleWrapped, inputArrowsPuzzleWrapped, tellJanusPasscode;

    DetailedQuestStep startCombatTrial;
    NpcStep completeCombatTrial;
    DetailedQuestStep talkToJanusAfterTrial;

    DetailedQuestStep talkToPrinceToStartThirdTrial;
    PuzzleWrapperStep talkToTenoch, talkToSilia, talkToAdrius, talkToEleuia, accuseTenoch, accuseSilia, accuseEleuia;
    DetailedQuestStep accuseGuiltyIndividual;

    PuzzleWrapperStep accuseGuiltyIndividualPuzzleWrapped;

    DetailedQuestStep goUpToFinalTrial, fightPrince, fightPrinceSidebar, talkToJanusAfterPrinceFight, talkToJanusAfterAllTrials, searchChestForEmissaryRobes, talkToItzlaToFollow,
            enterTemple, talkToItzlaAfterSermon, talkToFides, enterRuins;

    // Ice dungeon
    DetailedQuestStep takePickaxe;
    DetailedQuestStep mineRocks;
    ObjectStep pullFirstLever;
    DetailedQuestStep climbDownLedge;
    DetailedQuestStep climbUpLedgeToFirstLever;
    DetailedQuestStep slideAlongIceLedge;
    DetailedQuestStep slideAlongIceLedgeBackToSecondLever;
    DetailedQuestStep pullSecondLever;
    DetailedQuestStep jumpOverFrozenPlatforms;
    DetailedQuestStep climbUpLedgeToSecondLever;
    DetailedQuestStep pullThirdLever;
    DetailedQuestStep pullFourthLever;
    DetailedQuestStep pullChain;
    ObjectStep climbDownIceShortcut;

    DetailedQuestStep searchAirUrn, searchEarthUrn, searchWaterUrn, searchFireUrn, fixAirStatue, fixWaterStatue, fixEarthStatue, fixFireStatue,
            inspectAirMarkings, inspectWaterMarkings, inspectEarthMarkings, inspectFireMarkings;

    DetailedQuestStep enterFinalBossRoom;

    DetailedQuestStep defeatAmoxliatl, defeatAmoxliatlSidebar, talkToServius;

    private static final Pattern WHITE_TEXT = Pattern.compile("<col=faf9f6>([A-Za-z]+)");
    private static final Pattern CARVED_WALL_TEXT = Pattern.compile("<col=0000b2>([A-Za-z]+) ");

    Map<String, Integer> directionToInt = new HashMap<>();
    Map<String, Integer> wordToNumber = new HashMap<>();

    {
        directionToInt.put("Makt", 0);
        directionToInt.put("Takam", 1);
        directionToInt.put("Uitt", 2);
        directionToInt.put("Silam", 3);

        wordToNumber.put("Zema", 1);
        wordToNumber.put("Oma", 2);
        wordToNumber.put("Yeya", 3);
        wordToNumber.put("Naui", 4);
    }

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        Map<Integer, QuestStep> steps = new HashMap<>();
        initializeRequirements();
        setupConditions();
        setupSteps();

        steps.put(0, talkToItzlaAtTeomat);
        steps.put(2, talkToItzlaAtTeomat);
        steps.put(4, talkToItzlaAtTeomat);
        steps.put(6, talkToItzlaAtTeomat);

        ConditionalStep goToPub = new ConditionalStep(this, talkToBartender);
        goToPub.addStep(atTeomat, travelToGorge);
        steps.put(8, goToPub);
        steps.put(10, goToPub);

        steps.put(12, restOnBed);
        steps.put(14, talkToPrinceAfterRest);
        steps.put(16, talkToPrinceAfterRest);

        steps.put(18, talkToShopkeeper);
        steps.put(20, talkToPrinceInPubAgain);

        ConditionalStep goTalkToPrinceAtTower = new ConditionalStep(this, talkToPrinceAtTower);
        goTalkToPrinceAtTower.addStep(builtLandingInOverlook, talkToPrinceAtTowerAfterLanding);
        goTalkToPrinceAtTower.addStep(and(quetzalFeed.quantity(10),
                limestoneBrick.quantity(3),
                softClay.quantity(4)), buildSalvagerOverlookLandingSite);
        steps.put(22, goTalkToPrinceAtTower);

        ConditionalStep goTalkToRecruits = new ConditionalStep(this, talkToNova);
        goTalkToRecruits.addStep(not(talkedToFelius), talkToFelius);
        goTalkToRecruits.addStep(not(talkedToCaritta), talkToCaritta);
        goTalkToRecruits.addStep(not(talkedToSergius), talkToSergius);
        steps.put(24, goTalkToRecruits);

        steps.put(26, talkToPrinceAfterRecruits);

        ConditionalStep goEnterTower = new ConditionalStep(this, talkToPrinceAfterRecruits);
        goEnterTower.addStep(princeIsFollowing, talkToJanus);
        steps.put(28, goEnterTower);

        ConditionalStep goUpToFirstChallenge = new ConditionalStep(this, talkToPrinceAfterRecruits);
        goUpToFirstChallenge.addStep(princeIsFollowing, climbUpToFirstTrial);
        steps.put(30, goUpToFirstChallenge);

        ConditionalStep firstTrialPuzzle = new ConditionalStep(this, pickpocketAscended);
        firstTrialPuzzle.addStep(and(completedNote, hasReadCompletedNote), tellJanusPasscode);
        firstTrialPuzzle.addStep(and(completedNote), readCompletedNote);
        firstTrialPuzzle.addStep(and(scrapOfPaper1, scrapOfPaper2, scrapOfPaper3), combineScraps);
        firstTrialPuzzle.addStep(and(southWestChestOpened, scrapOfPaper1, scrapOfPaper2, knowAboutDirections, knowPoemSolution, inArrowPuzzle), inputArrows);
        firstTrialPuzzle.addStep(and(southWestChestOpened, scrapOfPaper1, scrapOfPaper2, knowAboutDirections, knowPoemSolution), openKeywordChestNorthWest);
        firstTrialPuzzle.addStep(and(southWestChestOpened, scrapOfPaper1, scrapOfPaper2, knowAboutDirections), readPoem);
        firstTrialPuzzle.addStep(and(southWestChestOpened, scrapOfPaper1, scrapOfPaper2, hasReadPoem), talkToPrinceAfterPoem);
        firstTrialPuzzle.addStep(and(southWestChestOpened, scrapOfPaper1, scrapOfPaper2, poem), readPoem);
        firstTrialPuzzle.addStep(and(scrapOfPaper1, book), openKeywordChestSouthWest);
        firstTrialPuzzle.addStep(and(southEastGateUnlocked), searchChestForBookAndPaper);
        firstTrialPuzzle.addStep(and(towerKey), useKeyOnSouthEastGate);

        ConditionalStep goDoFirstChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goDoFirstChallenge.addStep(inFirstTrialRoom, firstTrialPuzzle);
        steps.put(32, goDoFirstChallenge);

        ConditionalStep goDoSecondChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goDoSecondChallenge.addStep(and(inSecondTrialRoom, combatStarted), completeCombatTrial);
        goDoSecondChallenge.addStep(inSecondTrialRoom, startCombatTrial);
        goDoSecondChallenge.addStep(inFirstTrialRoom, tellJanusPasscode);
        steps.put(34, goDoSecondChallenge);
        steps.put(36, goDoSecondChallenge);

        ConditionalStep goStartThirdChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goStartThirdChallenge.addStep(inSecondTrialRoom, talkToJanusAfterTrial);
        steps.put(38, goStartThirdChallenge);

        ConditionalStep goDoThirdChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, not(hasTalkedToTenoch)), talkToTenoch);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, not(hasTalkedToSilia)), talkToSilia);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, not(hasTalkedToAdrius)), talkToAdrius);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, not(hasTalkedToEleuia)), talkToEleuia);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, tenochGuilty), accuseTenoch);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, siliaGuilty), accuseSilia);
//        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, adriusGuilty), accuseAdrius);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation, eleuiaGuilty), accuseEleuia);
        goDoThirdChallenge.addStep(and(inSecondTrialRoom, startedInvestigation), talkToTenoch);
        goDoThirdChallenge.addStep(inSecondTrialRoom, talkToPrinceToStartThirdTrial);
        steps.put(40, goDoThirdChallenge);

        ConditionalStep goToFourthChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goToFourthChallenge.addStep(inSecondTrialRoom, goUpToFinalTrial);
        steps.put(42, goToFourthChallenge);

        ConditionalStep goDoFourthChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goDoFourthChallenge.addStep(inSecondTrialRoom, fightPrince);
        steps.put(44, goDoFourthChallenge);

        ConditionalStep wrapUpFourthChallenge = new ConditionalStep(this, talkToJanusAfterAllTrials);
        wrapUpFourthChallenge.addStep(inSecondTrialRoom, talkToJanusAfterPrinceFight);
        steps.put(46, wrapUpFourthChallenge);

        steps.put(48, searchChestForEmissaryRobes);

        ConditionalStep goToTemple = new ConditionalStep(this, talkToItzlaToFollow);
        goToTemple.addStep(or(inTempleCutscene, and(princeIsFollowing, emissaryRobes)), enterTemple);
        goToTemple.addStep(princeIsFollowing, searchChestForEmissaryRobes);
        steps.put(50, goToTemple);

        ConditionalStep goTalkAfterSermon = new ConditionalStep(this, searchChestForEmissaryRobes);
        goTalkAfterSermon.addStep(emissaryRobes, talkToItzlaAfterSermon);
        steps.put(52, goTalkAfterSermon);

        ConditionalStep goTalkToFides = new ConditionalStep(this, searchChestForEmissaryRobes);
        goTalkToFides.addStep(and(emissaryRobes, princeIsFollowing), talkToFides);
        goTalkToFides.addStep(emissaryRobes, talkToItzlaAfterSermon);
        steps.put(54, goTalkToFides);

        ConditionalStep goEnterRuins = new ConditionalStep(this, searchChestForEmissaryRobes);
        goEnterRuins.addStep(princeIsFollowing, enterRuins);
        goEnterRuins.addStep(emissaryRobes, talkToItzlaAfterSermon);
        steps.put(56, goEnterRuins);

        ConditionalStep goMineRuins = new ConditionalStep(this, enterRuins);
        goMineRuins.addStep(and(inFirstIceRoom, pickaxe), mineRocks);
        goMineRuins.addStep(inFirstIceRoom, takePickaxe);
        steps.put(58, goMineRuins);

        steps.put(60, enterRuins);

        ConditionalStep goPullLevers = new ConditionalStep(this, enterRuins);
        goPullLevers.addStep(and(inThirdIceArea, pulledFirstLever, pulledSecondLever, pulledThirdLever), pullFourthLever);
        goPullLevers.addStep(and(inThirdIceArea, pulledFirstLever, pulledSecondLever), pullThirdLever);
        goPullLevers.addStep(and(inThirdIceArea), climbUpLedgeToSecondLever);
        goPullLevers.addStep(and(inSecondIceAreaSecondRoom, pulledFirstLever, pulledSecondLever), jumpOverFrozenPlatforms);
        goPullLevers.addStep(and(inSecondIceAreaSecondRoom, pulledFirstLever), pullSecondLever);
        goPullLevers.addStep(and(inSecondIceAreaSecondRoom), slideAlongIceLedgeBackToSecondLever);
        goPullLevers.addStep(and(inSecondIceAreaFirstRoom, pulledFirstLever), slideAlongIceLedge);
        goPullLevers.addStep(and(inSecondIceAreaFirstRoom), climbUpLedgeToFirstLever);
        goPullLevers.addStep(and(inFirstIceRoom, pulledFirstLever), climbDownLedge);
        goPullLevers.addStep(inFirstIceRoom, pullFirstLever);
        steps.put(62, goPullLevers);

        ConditionalStep puzzleStep = new ConditionalStep(this, inspectAirMarkings);
        puzzleStep.addStep(not(inspectedAirMarkings), inspectAirMarkings);
        puzzleStep.addStep(not(inspectedEarthMarkings), inspectEarthMarkings);

        puzzleStep.addStep(not(takenOrUsedAirIcon), searchAirUrn);
        puzzleStep.addStep(not(takenOrUsedEarthIcon), searchEarthUrn);

        puzzleStep.addStep(not(inspectedWaterMarkings), inspectWaterMarkings);
        puzzleStep.addStep(not(inspectedFireMarkings), inspectFireMarkings);

        puzzleStep.addStep(not(takenOrUsedFireIcon), searchFireUrn);
        puzzleStep.addStep(not(takenOrUsedWaterIcon), searchWaterUrn);

        puzzleStep.addStep(not(repairedWaterStatue), fixWaterStatue);
        puzzleStep.addStep(not(repairedFireStatue), fixFireStatue);
        puzzleStep.addStep(not(repairedEarthStatue), fixEarthStatue);
        puzzleStep.addStep(not(repairedAirStatue), fixAirStatue);

        puzzleStep.addStep(not(activatedFirstStatue), activateFirstStatue);
        puzzleStep.addStep(not(activatedSecondStatue), activateSecondStatue);
        puzzleStep.addStep(not(activatedThirdStatue), activateThirdStatue);
        puzzleStep.addStep(not(activatedFourthStatue), activateFourthStatue);

        ConditionalStep goToIcePuzzle = new ConditionalStep(this, enterRuins);
        goToIcePuzzle.addStep(and(inThirdIceArea, unlockedShortcut), puzzleStep);
        goToIcePuzzle.addStep(inThirdIceArea, pullChain);
        goToIcePuzzle.addStep(inSecondIceAreaSecondRoom, jumpOverFrozenPlatforms);
        goToIcePuzzle.addStep(inSecondIceAreaFirstRoom, climbUpLedgeToFirstLever);
        goToIcePuzzle.addStep(and(inFirstIceRoom, unlockedShortcut), climbDownIceShortcut);
        goToIcePuzzle.addStep(inFirstIceRoom, climbDownLedge);
        steps.put(64, goToIcePuzzle);
        // Skipped over step, but could be talk to prince or something
        steps.put(66, goToIcePuzzle);

        ConditionalStep goDoBoss = new ConditionalStep(this, enterRuins);
        goDoBoss.addStep(inBossRoom, defeatAmoxliatl);
        goDoBoss.addStep(and(inThirdIceArea, unlockedShortcut), enterFinalBossRoom);
        goDoBoss.addStep(inThirdIceArea, pullChain);
        goDoBoss.addStep(inSecondIceAreaSecondRoom, jumpOverFrozenPlatforms);
        goDoBoss.addStep(inSecondIceAreaFirstRoom, climbUpLedgeToFirstLever);
        goDoBoss.addStep(and(inFirstIceRoom, unlockedShortcut), climbDownIceShortcut);
        goDoBoss.addStep(inFirstIceRoom, climbDownLedge);
        steps.put(68, goDoBoss);
        steps.put(70, goDoBoss);
        steps.put(72, goDoBoss);

        steps.put(74, talkToServius);

        return steps;
    }

    @Override
    protected void setupRequirements()
    {
        // Required items
        combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
        food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, 10);
        coins = new ItemRequirement("Coins", ItemCollections.COINS);

        // Recommended items
        quetzalFeed = new ItemRequirement("Quetzal feed", ItemID.QUETZAL_FEED_29307);
        quetzalFeed.setTooltip("Optional for the Salvager Overlook landing site");
        limestoneBrick = new ItemRequirement("Limestone brick", ItemID.LIMESTONE_BRICK);
        limestoneBrick.setTooltip("Optional for the Salvager Overlook landing site");
        softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);
        softClay.setTooltip("Optional for the Salvager Overlook landing site");
        pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
        civitasIllaFortisTeleport = new ItemRequirement("Civitas illa fortis teleport", ItemID.CIVITAS_ILLA_FORTIS_TELEPORT);
        civitasIllaFortisTeleport.addAlternates(ItemID.PERFECTED_QUETZAL_WHISTLE, ItemID.ENHANCED_QUETZAL_WHISTLE, ItemID.BASIC_QUETZAL_WHISTLE);
        prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);
        staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

        // Quest items
        towerKey = new ItemRequirement("Tower key", ItemID.TOWER_KEY_29877);
        book = new ItemRequirement("Book", ItemID.BOOK_29878);
        poem = new ItemRequirement("Poem", ItemID.POEM);
        scrapOfPaper1 = new ItemRequirement("Scrap of paper", ItemID.SCRAP_OF_PAPER);
        scrapOfPaper2 = new ItemRequirement("Scrap of paper", ItemID.SCRAP_OF_PAPER_29881);
        scrapOfPaper3 = new ItemRequirement("Scrap of paper", ItemID.SCRAP_OF_PAPER_29882);
        completedNote = new ItemRequirement("Completed note", ItemID.COMPLETED_NOTE);
        emissaryHood = new ItemRequirement("Emissary hood", ItemID.EMISSARY_HOOD);
        emissaryTop = new ItemRequirement("Emissary top", ItemID.EMISSARY_ROBE_TOP);
        emissaryBottom = new ItemRequirement("Emissary bottom", ItemID.EMISSARY_ROBE_BOTTOM);
        emissaryBoots = new ItemRequirement("Emissary sandals", ItemID.EMISSARY_SANDALS);
        emissaryRobesEquipped = new ItemRequirements("Emissary robes (equipped)", emissaryHood, emissaryTop, emissaryBottom,
                emissaryBoots).equipped();
        emissaryRobes = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom, emissaryBoots);
        airIcon = new ItemRequirement("Air icon", ItemID.ICON_29887);
        waterIcon = new ItemRequirement("Water icon", ItemID.ICON_29888);
        earthIcon = new ItemRequirement("Earth icon", ItemID.ICON_29886);
        fireIcon = new ItemRequirement("Fire icon", ItemID.ICON);


    }

    private void setupConditions()
    {
        teomat = new Zone(new WorldPoint(1377, 2951, 0), new WorldPoint(1479, 3222, 3));
        atTeomat = new ZoneRequirement(teomat);
        firstTrialRoom = new Zone(new WorldPoint(1635, 3216, 1), new WorldPoint(1650, 3230, 1));
        inFirstTrialRoom = new ZoneRequirement(firstTrialRoom);
        secondTrialRoom = new Zone(new WorldPoint(1635, 3216, 2), new WorldPoint(1650, 3230, 2));
        inSecondTrialRoom = new ZoneRequirement(secondTrialRoom);
        builtLandingInOverlook = new VarbitRequirement(11379, 4);
        talkedToFelius = new VarbitRequirement(11118, 1);
        talkedToCaritta = new VarbitRequirement(11119, 1);
        talkedToSergius = new VarbitRequirement(11120, 1);
//        talkedToNova = new VarbitRequirement(11121, 1);
        princeIsFollowing = new VarplayerRequirement(447, 14053, 16);
        // Overlook landing could also be varp 4182 480 -> 2528
        southEastGateUnlocked = new VarbitRequirement(11165, 1);
        southWestChestOpened = new VarbitRequirement(11166, 1);
        hasReadPoem = new VarbitRequirement(11170, 1);
        knowAboutDirections = new VarbitRequirement(11171, 1);
        knowPoemSolution = new ManualRequirement();
        inArrowPuzzle = new WidgetTextRequirement(810, 15, 9, "Confirm");
        hasReadCompletedNote = new ManualRequirement();
//        northWestChestOpened = new VarbitRequirement(11167, 1);

        NpcCondition emissaryIsPassive = new NpcCondition(NpcID.EMISSARY_BRAWLER, new WorldPoint(1641, 3227, 2));
        emissaryIsPassive.setAnimationIDRequired(-1);
        combatStarted = not(emissaryIsPassive);
        startedInvestigation = new VarbitRequirement(11134, 1);
        // Accused Tenoch incorrectly
        // 11135 0->1
        // 11140 0->1

        DialogRequirement tenochInnocent = new DialogRequirement("Tenoch", false,
                "To do my duty, of course. Ralos and Ranul wish us to bring about the end of this world. Only then will we see the birth of a new one.",
                "The Final Dawn is our destiny. Ralos and Ranul will unite us all and end this world, but in doing so, we will all play a part in forming a new one.");
        tenochGuilty = new DialogRequirement("Tenoch", false,
                "To do my duty, of course. Ralos and Ranul wish us to bring about the end of this world. Only then will we will all unite in Mictl.",
                "The Final Dawn is our destiny. Ralos and Ranul will unite us all and end this world, but after doing so, they will grant us eternal life in Mictl.");
        hasTalkedToTenoch = or(tenochGuilty, tenochInnocent);

        DialogRequirement siliaInnocent = new DialogRequirement("Silia", false,
                "I always knew people were wrong about me. I knew I would serve a higher purpose. I never cared what it was. I only cared that in doing so, I would be serving the will of Ralos.",
                "The Final Dawn is an end, but also a beginning. It is when Ralos will use his holy fire to destroy this world so that he and Ranul can build a new one.");
        siliaGuilty = new DialogRequirement("Silia", false,
                "I always knew people were wrong about me. I knew I would serve a higher purpose. I never cared what it was. I only cared that in doing so, I would be granted a place in Mictl.",
                "The Final Dawn is an end, but also a beginning. It is when Ralos will use his holy fire to destroy this world. Then he and Ranul will guide us to Mictl.");
        hasTalkedToSilia = or(siliaInnocent, siliaGuilty);

        DialogRequirement adriusInnocent = new DialogRequirement("Adrius", false,
                "To be honest, I wasn't too sure about joining at first. My friend convinced me though. Here we're part of something bigger than ourselves.",
                "I've never been one for proper worship, but I believe in the teachings of Ralos and Ranul. I hope those teachings guide me through whatever is to come.",
                "They say the Final Dawn is inevitable. It's the end that we can't escape from. It's a scary thought, but the Twilight Emissaries have embraced it, so I have as well.");
        hasTalkedToAdrius = or(adriusInnocent);

        DialogRequirement eleuiaInnocent = new DialogRequirement("Eleuia", false,
                "I wished to see my god in mortal form and serve his will. In return for my service, the Emissaries promised me they could grant that wish.",
                "The Final Dawn is the will of Ranul. He will show us all that death is nothing to be feared as he and Ralos guide us into forming a new world.");
        eleuiaGuilty = new DialogRequirement("Eleuia", false,
                "I wished to see my god in mortal form and be granted eternity in Mictl. In return for my service, the Emissaries promised me they could grant that wish.",
                "The Final Dawn is the will of Ranul. He will show us all that death is nothing to be feared. He and Ralos will lead us into forming a new world before then guiding us to Mictl.");
        hasTalkedToEleuia = or(eleuiaInnocent, eleuiaGuilty);

        freeInvSlots4 = new FreeInventorySlotRequirement(4);

        ZoneRequirement inTemple = new ZoneRequirement(new Zone(new WorldPoint(1677, 3244, 0), new WorldPoint(1691, 3251, 0)));
        inTempleCutscene = and(inTemple, new InInstanceRequirement());

        firstIceRoom = new Zone(new WorldPoint(1660, 9595, 2), new WorldPoint(1724, 9659, 2));
        inFirstIceRoom = new ZoneRequirement(firstIceRoom);

        secondIceAreaFirstRoom = new Zone(new WorldPoint(1699, 9602, 1), new WorldPoint(1726, 9639, 1));
        inSecondIceAreaFirstRoom = new ZoneRequirement(secondIceAreaFirstRoom);

        secondIceAreaSecondRoom = new Zone(new WorldPoint(1686, 9640, 1), new WorldPoint(1716, 9663, 1));
        inSecondIceAreaSecondRoom = new ZoneRequirement(secondIceAreaSecondRoom);

        thirdIceArea = new Zone(new WorldPoint(1580, 9600, 0), new WorldPoint(1689, 9663, 0));
        inThirdIceArea = new ZoneRequirement(thirdIceArea);

        bossRoom = new Zone(new WorldPoint(1359, 4505, 0), new WorldPoint(1385, 4520, 0));
        inBossRoom = new ZoneRequirement(bossRoom);

        pulledFirstLever = new VarbitRequirement(11138, 1);
        pulledSecondLever = new VarbitRequirement(11139, 1);
        // on first frozen platform:
        // 11181 0->1
        pulledThirdLever = new VarbitRequirement(11137, 1);
//        pulledFourthLever = new VarbitRequirement(11136, 1);

        unlockedShortcut = new VarbitRequirement(11174, 1);

        inspectedAirMarkings = new ManualRequirement();
        inspectedEarthMarkings = new ManualRequirement();
        inspectedWaterMarkings = new ManualRequirement();
        inspectedFireMarkings = new ManualRequirement();

        repairedWaterStatue = new VarbitRequirement(11127, 1, Operation.GREATER_EQUAL);
        repairedFireStatue = new VarbitRequirement(11124, 1, Operation.GREATER_EQUAL);
        repairedEarthStatue = new VarbitRequirement(11126, 1, Operation.GREATER_EQUAL);
        repairedAirStatue = new VarbitRequirement(11125, 1, Operation.GREATER_EQUAL);

        takenOrUsedAirIcon = or(repairedAirStatue, airIcon);
        takenOrUsedEarthIcon = or(repairedEarthStatue, earthIcon);
        takenOrUsedFireIcon = or(repairedFireStatue, fireIcon);
        takenOrUsedWaterIcon = or(repairedWaterStatue, waterIcon);

        activatedFirstStatue = new VarbitRequirement(11161, -1);
        activatedSecondStatue = new VarbitRequirement(11162, -1);
        activatedThirdStatue = new VarbitRequirement(11163, -1);
        activatedFourthStatue = new VarbitRequirement(11164, -1);

        activateStatueRequirements[0] = activatedFirstStatue;
        activateStatueRequirements[1] = activatedSecondStatue;
        activateStatueRequirements[2] = activatedThirdStatue;
        activateStatueRequirements[3] = activatedFourthStatue;

        // 1651, 9641, 0
        elementalStatueDetails = new ArrayList<>();
        elementalStatueDetails.add(new StatueDetails(new Zone(new WorldPoint(1648, 9620, 0), new WorldPoint(1653, 9624, 0)), inspectedEarthMarkings,
                new WorldPoint(1605, 9635, 0),"earth"));
        elementalStatueDetails.add(new StatueDetails(new Zone(new WorldPoint(1610, 9639, 0), new WorldPoint(1615, 9643, 0)), inspectedFireMarkings,
                new WorldPoint(1605, 9627, 0), "fire"));
        elementalStatueDetails.add(new StatueDetails(new Zone(new WorldPoint(1610, 9619, 0), new WorldPoint(1616, 9624, 0)), inspectedWaterMarkings,
                new WorldPoint(1608, 9624, 0), "water"));
        elementalStatueDetails.add(new StatueDetails(new Zone(new WorldPoint(1648, 9639, 0), new WorldPoint(1653, 9642, 0)), inspectedAirMarkings,
                new WorldPoint(1608, 9638, 0), "air"));
        // Failed puzzle
        // 11132 0->1
    }

    private void setupSteps()
    {
        talkToItzlaAtTeomat = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_12896, new WorldPoint(1454, 3173, 0), "Talk to Prince Itzla Arkan at the Teomat. You" +
                " can travel here using Renu the quetzal.");
        WidgetHighlight teomatWidget = new WidgetHighlight(874, 15, true);
        teomatWidget.setModelIdRequirement(51205);
        talkToItzlaAtTeomat.addWidgetHighlight(teomatWidget);
        talkToItzlaAtTeomat.addDialogStep("Yes.");
        travelToGorge = new NpcStep(this, NpcID.RENU, new WorldPoint(1437, 3169, 0), "Travel on Renu to the Quetzacalli Gorge.");
        ((NpcStep) travelToGorge).addAlternateNpcs(NpcID.RENU_13349, NpcID.RENU_13350, NpcID.RENU_13351, NpcID.RENU_13352, NpcID.RENU_13353, NpcID.RENU_13354);
        WidgetHighlight gorgeWidget = new WidgetHighlight(874, 15, true);
        gorgeWidget.setModelIdRequirement(54539);
        travelToGorge.addWidgetHighlight(gorgeWidget);
        talkToBartender = new NpcStep(this, NpcID.BARTENDER_14020, new WorldPoint(1499, 3224, 0), "Talk to the Bartender in the pub in the Gorge.",
                coins.quantity(30));
        // Told about ground room, 11123 1->0
        talkToBartender.addDialogSteps("I'd like to rent the basement room.", "I'll take it.");
        restOnBed = new ObjectStep(this, NullObjectID.NULL_55374, new WorldPoint(1505, 3225, 0), "Rest in the bed in the south-eastern room of the pub.");
        // 11123 0->3, prince in room
        talkToPrinceAfterRest = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13690, new WorldPoint(1505, 3223, 0), "Talk to Prinze Itzla Arkan in the bedroom.");
        talkToShopkeeper = new NpcStep(this, NpcID.SHOPKEEPER_14021, new WorldPoint(1517, 3223, 0), "Talk to the shopkeeper east of the pub.");
        talkToShopkeeper.addDialogStep("I hear you can offer help to those in need.");
        talkToPrinceInPubAgain = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13690, new WorldPoint(1505, 3223, 0), "Return to Prinze Itzla Arkan in the " +
                "bedroom.");
        // Gone through gate, varp 4411 60->124

        List<WorldPoint> pathFromPubToOverlook = List.of(
                new WorldPoint(1499, 3229, 0),
                new WorldPoint(1522, 3252, 0),
                new WorldPoint(1529, 3254, 0),
                new WorldPoint(1538, 3267, 0),
                new WorldPoint(1542, 3277, 0),
                new WorldPoint(1546, 3298, 0),
                new WorldPoint(1576, 3298, 0),
                new WorldPoint(1593, 3290, 0),
                new WorldPoint(1615, 3290, 0),
                new WorldPoint(1615, 3298, 0)
        );

        List<WorldPoint> pathFromPubToChasm = List.of(
                new WorldPoint(1499, 3229, 0),
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
                new WorldPoint(1621, 3254, 0)
        );

        List<WorldPoint> pathFromChasmToOverlook = List.of(
                new WorldPoint(1638, 3244, 0),
                new WorldPoint(1639, 3254, 0),
                new WorldPoint(1644, 3263, 0),
                new WorldPoint(1644, 3279, 0),
                new WorldPoint(1630, 3293, 0),
                new WorldPoint(1613, 3299, 0)
        );

        List<WorldPoint> pathFromChasmToTower = List.of(
                new WorldPoint(1644, 3240, 0),
                new WorldPoint(1649, 3240, 0),
                new WorldPoint(1660, 3228, 0),
                new WorldPoint(1656, 3220, 0)
        );

        List<WorldPoint> pathFromPubToTower = new ArrayList<>();
        pathFromPubToTower.addAll(pathFromPubToChasm);
        pathFromPubToTower.addAll(pathFromChasmToTower);

        List<WorldPoint> pathFromOverlookToTower = new ArrayList<>();
        pathFromOverlookToTower.addAll(Lists.reverse(pathFromChasmToOverlook));
        pathFromOverlookToTower.addAll(pathFromChasmToTower);

        talkToPrinceAtTower = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13690, new WorldPoint(1656, 3219, 0), "Travel past the proudspire to the east to the" +
                " Tower of Ascension. Talk to the prince there.");
        talkToPrinceAtTower.setLinePoints(pathFromPubToTower);

        buildSalvagerOverlookLandingSite = new ObjectStep(this, ObjectID.UNBUILT_LANDING_SITE, new WorldPoint(1613, 3302, 0), "Before going to the tower, " +
                "build the landing site in the north of the salvager overlook.", quetzalFeed.quantity(10), limestoneBrick.quantity(3), softClay.quantity(4));
        buildSalvagerOverlookLandingSite.setLinePoints(pathFromPubToOverlook);

        talkToPrinceAtTowerAfterLanding = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13690, new WorldPoint(1656, 3219, 0), "Talk to the prince at the Tower " +
                "of Ascension to the south-east of the salvager overlook.");
        talkToPrinceAtTowerAfterLanding.setLinePoints(pathFromOverlookToTower);
        talkToPrinceAtTower.addSubSteps(talkToPrinceAtTowerAfterLanding);

        talkToNova = new NpcStep(this, NpcID.NOVA, new WorldPoint(1659, 3224, 0), "Talk to Nova outside the tower.");
        talkToSergius = new NpcStep(this, NpcID.SERGIUS, new WorldPoint(1659, 3224, 0), "Talk to Sergius outside the tower.");
        talkToFelius = new NpcStep(this, NpcID.FELIUS, new WorldPoint(1659, 3224, 0), "Talk to Felius outside the tower.");
        talkToCaritta = new NpcStep(this, NpcID.CARITTA, new WorldPoint(1659, 3224, 0), "Talk to Caritta outside the tower.");
        talkToPrinceAfterRecruits = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13690, new WorldPoint(1656, 3219, 0), "Talk to the prince at the Tower " +
                "of Ascension to the south-east of the salvager overlook again.");
        talkToPrinceAfterRecruits.addDialogStep("Could you remind me what Ximoua is?");
        talkToJanus = new NpcStep(this, NpcID.FOREBEARER_JANUS, new WorldPoint(1638, 3224, 0), "Talk to Forebearer Janus inside the tower.");

        final String PUZZLE_1_TEXT = "Work out the passcode to tell Janus by solving the room's puzzles.";

        // First trial section
        climbUpToFirstTrial = new ObjectStep(this, ObjectID.STAIRCASE_54369, new WorldPoint(1635, 3221, 0), "Climb up the staircase in the tower to the first" +
                " challenge.");

        pickpocketAscended = new NpcStep(this, NpcID.EMISSARY_ASCENDED_13768, new WorldPoint(1641, 3225, 1), "Pickpocket one of the emissary ascended.", true)
                .addAlternateNpcs(NpcID.EMISSARY_ASCENDED_13767)
                .puzzleWrapStep(PUZZLE_1_TEXT);

        useKeyOnSouthEastGate = new ObjectStep(this, NullObjectID.NULL_55354, new WorldPoint(1644, 3220, 1), "Use the key to open the gate in the south-east " +
                "corner of the room.", towerKey)
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        searchChestForBookAndPaper = new ObjectStep(this, ObjectID.CHEST_54372, new WorldPoint(1644, 3217, 1), "Search the south-east chest for a book and some paper.")
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        openKeywordChestSouthWest = new LockedChestPuzzle(this);
        openKeywordChestSouthWestPuzzleWrapped = openKeywordChestSouthWest
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        readPoem = new DetailedQuestStep(this, "Read the poem.", poem.highlighted())
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        talkToPrinceAfterPoem = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13770, new WorldPoint(1638, 3218, 1), "Talk to the prince in the room.")
                .addDialogSteps("Makt.", "Takam.", "Silam.", "Thanks! Could you translate another?")
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        openKeywordChestNorthWest = new ObjectStep(this, ObjectID.CHEST_54374, new WorldPoint(1636, 3225, 1), "Open the north-west chest.")
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        inputArrows = new ArrowChestPuzzleStep(this);
        inputArrowsPuzzleWrapped = inputArrows
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        combineScraps = new DetailedQuestStep(this,  "Inspect one of the scraps to combine them.", scrapOfPaper1.highlighted(), scrapOfPaper2.highlighted(), scrapOfPaper3.highlighted())
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        readCompletedNote = new DetailedQuestStep(this, "Read the completed note.", completedNote.highlighted())
                .puzzleWrapStep(PUZZLE_1_TEXT)
                .withNoHelpHiddenInSidebar(true);

        tellJanusPasscode = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1644, 3226, 1), "Talk to Janus and tell him the passcode.")
                        .addDialogSteps("About that passphrase...", "Yes.")
                        .puzzleWrapStep(PUZZLE_1_TEXT)
                        .withNoHelpHiddenInSidebar(true);

        pickpocketAscended.addSubSteps(useKeyOnSouthEastGate, searchChestForBookAndPaper, openKeywordChestSouthWest, openKeywordChestSouthWestPuzzleWrapped,
                readPoem, talkToPrinceAfterPoem, openKeywordChestNorthWest, inputArrows, inputArrowsPuzzleWrapped, combineScraps, readCompletedNote, tellJanusPasscode);

        // Second trial section
        startCombatTrial = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1644, 3225, 2), "Talk to Forebearer Janus, ready to fight.");
        startCombatTrial.addDialogStep("Yes! I'm ready.");
        completeCombatTrial = new NpcStep(this, NpcID.EMISSARY_BRAWLER, new WorldPoint(1644, 3225, 2), "Defeat the waves of enemies. Use Protect from " +
                "Melee. You can heal yourself and the prince using bandages from the chests in the area.", true);
        completeCombatTrial.setMustBeFocusedOnPlayer(true);
        completeCombatTrial.setMustBeFocusedOnNpcs(NpcID.PRINCE_ITZLA_ARKAN_13771);
        completeCombatTrial.addAlternateNpcs(NpcID.EMISSARY_BRAWLER_13774, NpcID.EMISSARY_BRAWLER_13775, NpcID.EMISSARY_BRAWLER_13776,
                NpcID.EMISSARY_CONJURER, NpcID.EMISSARY_CONJURER_13778);
        talkToJanusAfterTrial = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1644, 3225, 2),
                "Talk to Forebearer Janus again to progress to the third trial.");
        talkToJanusAfterTrial.addDialogSteps("Yes.", "Proceed to the next trial.");

        // Third trial section
        talkToPrinceToStartThirdTrial = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13770, new WorldPoint(1638, 3218, 2), "Talk to the prince in the room.");

        talkToTenoch = new NpcStep(this, NpcID.TENOCH, new WorldPoint(1643, 3225, 2), "Talk to Tenoch.")
                .addDialogSteps("Interrogate Tenoch.", "Tell me about the Final Dawn.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        talkToSilia = new NpcStep(this, NpcID.SILIA, new WorldPoint(1645, 3223, 2), "Talk to Silia.")
                .addDialogSteps("Interrogate Silia.", "Tell me about the Final Dawn.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        talkToAdrius = new NpcStep(this, NpcID.ADRIUS, new WorldPoint(1645, 3220, 2), "Talk to Adrius.")
                .addDialogSteps("Interrogate Adrius.", "Tell me about the Final Dawn.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        talkToEleuia = new NpcStep(this, NpcID.ELEUIA, new WorldPoint(1643, 3218, 2), "Talk to Eleuia.")
                .addDialogSteps("Interrogate Eleuia.", "Tell me about the Final Dawn.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        accuseTenoch = new NpcStep(this, NpcID.TENOCH, new WorldPoint(1643, 3225, 2), "Accuse Tenoch.")
                .addDialogSteps("Yes.", "Choose Tenoch.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        accuseSilia = new NpcStep(this, NpcID.SILIA, new WorldPoint(1645, 3223, 2), "Accuse Silia.")
                .addDialogSteps("Yes.", "Choose Silia.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        accuseEleuia = new NpcStep(this, NpcID.ELEUIA, new WorldPoint(1643, 3218, 2), "Accuse Eleuia.")
                .addDialogSteps("Yes.", "Choose Eleuia.")
                .puzzleWrapStep("Work out who the guilty emissary is.")
                .withNoHelpHiddenInSidebar(true);

        accuseGuiltyIndividual = new DetailedQuestStep(this, "Accuse the guilty individual.");
        accuseGuiltyIndividual.addSubSteps(accuseTenoch, accuseSilia, accuseEleuia);
        accuseGuiltyIndividualPuzzleWrapped = accuseGuiltyIndividual
                .puzzleWrapStep("Work out who the guilty emissary is.");
        accuseGuiltyIndividualPuzzleWrapped.addSubSteps(talkToTenoch, talkToSilia, talkToAdrius, talkToEleuia, accuseTenoch, accuseSilia, accuseEleuia);

        goUpToFinalTrial = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1640, 3226, 2), "Talk to Janus to go to the final trial, ready for " +
                "a fight.");
        goUpToFinalTrial.addDialogStep("Yes.");

        // Final trial section
        fightPrince = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13784, new WorldPoint(1644, 3225, 2), "Defeat the prince. Use protect from melee. See " +
                "sidebar for more details.");
        fightPrinceSidebar = new DetailedQuestStep(this, "Defeat the prince. Use Protect from Melee.");
        fightPrinceSidebar.addText("He will protect from any special attacks you do.");
        fightPrinceSidebar.addText("He can do a sword swipe to turn off your prayers. Simply re-activate them straight away.");
        fightPrinceSidebar.addText("If he says he's doing a slam attack, step on the tile behind him to dodge it.");
        fightPrinceSidebar.addSubSteps(fightPrince);

        // Cult section
        talkToJanusAfterPrinceFight = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1640, 3226, 2), "Talk to Janus to wrap up the final " +
                "trial.");
        talkToJanusAfterAllTrials = new NpcStep(this, NpcID.FOREBEARER_JANUS, new WorldPoint(1640, 3226, 0), "Talk to Janus at the bottom of the tower.");
        searchChestForEmissaryRobes = new ObjectStep(this, ObjectID.CHEST_54515, new WorldPoint(1638, 3217, 0), "Search the chest in the south of the tower " +
                "for some emissary robes.", freeInvSlots4);
        talkToItzlaToFollow = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13691, new WorldPoint(1638, 3222, 0), "Talk to the prince to have him follow you.");
        enterTemple = new DetailedQuestStep(this, new WorldPoint(1687, 3247, 0), "Enter the temple to the east of the tower for a cutscene.",
                emissaryRobesEquipped);
        talkToItzlaAfterSermon = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13691, new WorldPoint(1688, 3247, 0), "Talk to the prince in the temple.",
                emissaryRobesEquipped);
        talkToFides = new NpcStep(this, NpcID.FOREBEARER_FIDES, new WorldPoint(1685, 3247, 0), "Talk to Forebearer Fides in the temple.", emissaryRobesEquipped);
        enterRuins = new ObjectStep(this, ObjectID.STAIRS_54525, new WorldPoint(1693, 3231, 0), "Go down the stairs south-east of the temple. Be equipped for" +
                " a fight.");

        // Ice dungeon section
        takePickaxe = new ObjectStep(this, ObjectID.BARREL_54517, new WorldPoint(1696, 9633, 2), "Take a pickaxe from the nearby barrel.");
        mineRocks = new ObjectStep(this, ObjectID.ROCKS_54501, new WorldPoint(1690, 9634, 2), "Mine the nearby rocks.", pickaxe);
        int LEVER_ID = 55367; // Decorative object
        pullFirstLever = new ObjectStep(this, LEVER_ID, new WorldPoint(1695, 9604, 2), "Pull the lever in the south-east of the area. Make sure to avoid the " +
                "wall spikes.");
        pullFirstLever.addTileMarkers(SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING,
                new WorldPoint(1682, 9603, 2), new WorldPoint(1682, 9604, 2), new WorldPoint(1682, 9605, 2),
                new WorldPoint(1682, 9606, 2), new WorldPoint(1684, 9603, 2), new WorldPoint(1684, 9604, 2),
                new WorldPoint(1684, 9605, 2), new WorldPoint(1684, 9606, 2), new WorldPoint(1686, 9603, 2),
                new WorldPoint(1686, 9604, 2), new WorldPoint(1686, 9605, 2), new WorldPoint(1686, 9606, 2),
                new WorldPoint(1688, 9603, 2), new WorldPoint(1688, 9604, 2), new WorldPoint(1688, 9605, 2),
                new WorldPoint(1688, 9606, 2), new WorldPoint(1690, 9603, 2), new WorldPoint(1690, 9604, 2),
                new WorldPoint(1690, 9605, 2), new WorldPoint(1690, 9606, 2));
        climbDownLedge = new ObjectStep(this, ObjectID.LEDGE_54531, new WorldPoint(1701, 9607, 2), "Climb down the ledge east of the lever.");
        climbUpLedgeToFirstLever = new ObjectStep(this, ObjectID.ICE_54532, new WorldPoint(1702, 9606, 1), "Climb back up the ice to the south-west to pull " +
                "the first lever.");
        slideAlongIceLedge = new ObjectStep(this, ObjectID.ICY_LEDGE, new WorldPoint(1714, 9632, 1), "Climb across the ice ledge to the north. Avoid the " +
                "floor spike traps on the way.");
        slideAlongIceLedge.setLinePoints(List.of(
                new WorldPoint(1707, 9607, 1),
                new WorldPoint(1719, 9607, 1),
                new WorldPoint(1715, 9615, 1),
                new WorldPoint(1716, 9619, 1),
                new WorldPoint(1716, 9629, 1)
        ));

        slideAlongIceLedgeBackToSecondLever = new ObjectStep(this, ObjectID.ICY_LEDGE_54534, new WorldPoint(1714, 9641, 1), "Climb back across the icy ledge.");
        climbUpLedgeToSecondLever = new ObjectStep(this, ObjectID.ICE_54542, new WorldPoint(1684, 9659, 0), "Climb back up to pull a lever you missed.");
        pullFirstLever.addSubSteps(slideAlongIceLedgeBackToSecondLever, climbUpLedgeToFirstLever, climbUpLedgeToSecondLever);
        pullSecondLever = new ObjectStep(this, NullObjectID.NULL_55368, new WorldPoint(1711, 9660, 1), "Pull the lever in the north-east room. Avoid the " +
                "floor traps.");
        pullSecondLever.setLinePoints(List.of(
                new WorldPoint(1710, 9648, 1),
                new WorldPoint(1710, 9653, 1),
                new WorldPoint(1713, 9655, 1),
                new WorldPoint(1713, 9658, 1),
                new WorldPoint(1711, 9660, 1)
        ));
        jumpOverFrozenPlatforms = new ObjectStep(this, ObjectID.LEDGE_54541, new WorldPoint(1686, 9659, 1), "Jump over the frozen platforms to the " +
                "north-west, and climb down the ledge there.");
        jumpOverFrozenPlatforms.setLinePoints(List.of(
                new WorldPoint(1711, 9660, 1),
                new WorldPoint(1713, 9658, 1),
                new WorldPoint(1713, 9655, 1),
                new WorldPoint(1710, 9653, 1),
                new WorldPoint(1710, 9648, 1),
                new WorldPoint(1703, 9653, 1),
                new WorldPoint(1698, 9655, 1),
                new WorldPoint(1694, 9655, 1),
                new WorldPoint(1694, 9659, 1),
                new WorldPoint(1691, 9659, 1),
                new WorldPoint(1686, 9659, 1)
        ));

        pullThirdLever = new ObjectStep(this, NullObjectID.NULL_55366, new WorldPoint(1671, 9661, 0), "Pull the third lever in the north-west room.");
        pullThirdLever.setLinePoints(List.of(
                new WorldPoint(1681, 9656, 0),
                new WorldPoint(1677, 9653, 0),
                new WorldPoint(1668, 9653, 0),
                new WorldPoint(1668, 9656, 0),
                new WorldPoint(1671, 9659, 0),
                new WorldPoint(1671, 9661, 0)
        ));
        pullFourthLever = new ObjectStep(this, NullObjectID.NULL_55365, new WorldPoint(1662, 9636, 0), "Pull the final lever in the south-west, near the gate.");
        pullFourthLever.setLinePoints(List.of(
                new WorldPoint(1671, 9659, 0),
                new WorldPoint(1671, 9658, 0),
                new WorldPoint(1668, 9656, 0),
                new WorldPoint(1668, 9653, 0),
                new WorldPoint(1675, 9653, 0),
                new WorldPoint(1675, 9644, 0),
                new WorldPoint(1674, 9644, 0),
                new WorldPoint(1674, 9642, 0),
                new WorldPoint(1667, 9636, 0),
                new WorldPoint(1663, 9636, 0)
        ));
        pullChain = new ObjectStep(this, NullObjectID.NULL_55375, new WorldPoint(1670, 9631, 0), "Pull the chain to unlock a shortcut.");

        climbDownIceShortcut = new ObjectStep(this, ObjectID.CHAIN_54527, new WorldPoint(1670, 9631, 2), "Climb down the shortcut chain to the west of the " +
                "entrance.");

        searchAirUrn = new ObjectStep(this, NullObjectID.NULL_55358, new WorldPoint(1647, 9622, 0), "Search the urn with an air symbol on it near to the " +
                "earth markings in the south-east of the area.");
        searchEarthUrn = new ObjectStep(this, NullObjectID.NULL_55359, new WorldPoint(1652, 9622, 0), "Search the urn with an earth symbol on it near to the " +
                "earth markings in the south-east of the area.");
        searchWaterUrn = new ObjectStep(this, NullObjectID.NULL_55357, new WorldPoint(1610, 9622, 0), "Search the urn with an water symbol on it near to the " +
                "water markings in the south-west of the area.");
        searchFireUrn = new ObjectStep(this, NullObjectID.NULL_55356, new WorldPoint(1615, 9622, 0), "Search the urn with an fire symbol on it near to the " +
                "water markings in the south-west of the area.");
        fixAirStatue = new ObjectStep(this, NullObjectID.NULL_54471, new WorldPoint(1608, 9638, 0), "Fix the broken air statue in the west of the area.",
                airIcon.highlighted());
        fixAirStatue.addDialogStep("Yes.");
        fixAirStatue.addIcon(ItemID.ICON_29887);
        fixWaterStatue = new ObjectStep(this, NullObjectID.NULL_54465, new WorldPoint(1608, 9624, 0), "Fix the broken water statue in the west of the area.",
                waterIcon.highlighted());
        fixWaterStatue.addDialogStep("Yes.");
        fixWaterStatue.addIcon(ItemID.ICON_29888);
        fixEarthStatue = new ObjectStep(this, NullObjectID.NULL_54477, new WorldPoint(1605, 9635, 0), "Fix the broken earth statue in the west of the area.",
                earthIcon.highlighted());
        fixEarthStatue.addDialogStep("Yes.");
        fixEarthStatue.addIcon(ItemID.ICON_29886);
        fixFireStatue = new ObjectStep(this, NullObjectID.NULL_54459, new WorldPoint(1605, 9627, 0), "Fix the broken fire statue in the west of the area.",
                fireIcon.highlighted());
        fixFireStatue.addDialogStep("Yes.");
        fixFireStatue.addIcon(ItemID.ICON);

        inspectAirMarkings = new ObjectStep(this, NullObjectID.NULL_55363, new WorldPoint(1650, 9642, 0), "Inspect the air markings in the north-east of " +
                "the area.");
        inspectWaterMarkings = new ObjectStep(this, NullObjectID.NULL_55362, new WorldPoint(1613, 9621, 0), "Inspect the water markings in the south-west of " +
                "the area.");
        inspectEarthMarkings = new ObjectStep(this, NullObjectID.NULL_55364, new WorldPoint(1650, 9621, 0), "Inspect the earth markings in the south-east of " +
                "the area.");
        inspectFireMarkings = new ObjectStep(this, NullObjectID.NULL_55361, new WorldPoint(1613, 9642, 0), "Inspect the fire markings in the north-west of " +
                "the area.");

        activateFirstStatue = new ObjectStep(this, NullObjectID.NULL_54459, new WorldPoint(1650, 9642, 0), "Activate the first statue in the order.");
        activateFirstStatue.addAlternateObjects(NullObjectID.NULL_54465, NullObjectID.NULL_54471, NullObjectID.NULL_54477);
        activateSecondStatue = new ObjectStep(this, NullObjectID.NULL_54459, new WorldPoint(1650, 9642, 0), "Activate the second statue in the order.");
        activateSecondStatue.addAlternateObjects(NullObjectID.NULL_54465, NullObjectID.NULL_54471, NullObjectID.NULL_54477);
        activateThirdStatue = new ObjectStep(this, NullObjectID.NULL_54459, new WorldPoint(1650, 9642, 0), "Activate the third statue in the order.");
        activateThirdStatue.addAlternateObjects(NullObjectID.NULL_54465, NullObjectID.NULL_54471, NullObjectID.NULL_54477);
        activateFourthStatue = new ObjectStep(this, NullObjectID.NULL_54459, new WorldPoint(1650, 9642, 0), "Activate the fourth statue in the order.");
        activateFourthStatue.addAlternateObjects(NullObjectID.NULL_54465, NullObjectID.NULL_54471, NullObjectID.NULL_54477);
        statueActivateSteps[0] = activateFirstStatue;
        statueActivateSteps[1] = activateSecondStatue;
        statueActivateSteps[2] = activateThirdStatue;
        statueActivateSteps[3] = activateFourthStatue;

        enterFinalBossRoom = new ObjectStep(this, NullObjectID.NULL_55355, new WorldPoint(1601, 9631, 0), "Enter the door to the west, ready for the boss.");

        defeatAmoxliatl = new NpcStep(this, NpcID.AMOXLIATL, new WorldPoint(1365, 4510,  0), "Defeat Amoxliatl. See the sidebar for more details.");
        ((NpcStep) defeatAmoxliatl).addAlternateNpcs(NpcID.AMOXLIATL_13686, NpcID.AMOXLIATL_13687, NpcID.AMOXLIATL_13689);

        defeatAmoxliatlSidebar = new DetailedQuestStep(this, "Defeat Amoxliatl. Use Protect from Magic.");
        defeatAmoxliatlSidebar.addText("Ice spikes will appear on the floor, move off the tile to avoid them.");
        defeatAmoxliatlSidebar.addText("She may disable your prayers and shoot an icicle at you. Simply turn back on Protect from Magic.");
        defeatAmoxliatlSidebar.addText("Ice blocks may appear. Use melee to destroy them all before they explode and heal the boss.");
        defeatAmoxliatlSidebar.addSubSteps(defeatAmoxliatl);

        talkToServius = new NpcStep(this, NpcID.SERVIUS_TEOKAN_OF_RALOS_13694, new WorldPoint(1681, 3168, 0), "Talk to Servius, Teokan of Ralos in the palace" +
                " in Civitas illa Fortis to complete the quest.");
        talkToServius.addTeleport(civitasIllaFortisTeleport);
    }

    List<StatueDetails> elementalStatueDetails = new ArrayList<>();

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        LocalPoint playerLocalLocation = client.getLocalPlayer().getLocalLocation();
        WorldPoint playerWorldLocation = WorldPoint.fromLocalInstance(client, playerLocalLocation);
        if (chatMessage.getType() == ChatMessageType.MESBOX)
        {
            Matcher matcher = CARVED_WALL_TEXT.matcher(chatMessage.getMessage());
            if (matcher.find())
            {
                String word = matcher.group(1);
                Integer pos = wordToNumber.get(word);
                if (pos == null) return;

                for (int i = 0; i < elementalStatueDetails.size(); i++)
                {
                    StatueDetails statueDetails = elementalStatueDetails.get(i);
                    if (statueDetails.getElementZone().contains(playerWorldLocation))
                    {
                        statueDetails.setPosInOrder(pos);
                        statueDetails.getHasBeenInspected().setShouldPass(true);
                        ObjectStep stepToChange = statueActivateSteps[pos - 1];
                        stepToChange.setText("Activate the "  + statueDetails.getElementName() + " statue in the west of the room.");
                        stepToChange.setWorldPoint(statueDetails.getStatuePos());
                        activateStatueRequirements[pos - 1].setRequiredValue(i + 1);
                        // set the step of the pos to be this element's info
                    }
                }
            }
        }

        // If all 4 not -1, then

        // Inspected markings 55363 (north-east)
        // 11128 0->1

        // Earth
        // 11130 0->1
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded)
    {
        arrowPuzzleSolutionCheck(widgetLoaded);
        passphraseCheck(widgetLoaded);
    }

    private void passphraseCheck(WidgetLoaded widgetLoaded)
    {
        if (hasReadCompletedNote.check(client)) return;

        int GROUP_ID = 808;
        Pattern ANSWERS = Pattern.compile("([A-Za-z]+) - ([A-Za-z]+) - ([A-Za-z]+)");

        if (widgetLoaded.getGroupId() == GROUP_ID)
        {
            Widget line = client.getWidget(GROUP_ID, 11);
            if (line == null) return;

            Matcher matcher = ANSWERS.matcher(line.getText());
            if (matcher.find()) {
                tellJanusPasscode.addDialogSteps(matcher.group(1) + ".", matcher.group(2) + ".", matcher.group(3) + ".");
                hasReadCompletedNote.setShouldPass(true);
            }
        }
    }

    private void arrowPuzzleSolutionCheck(WidgetLoaded widgetLoaded)
    {
        if (knowPoemSolution.check(client)) return;

        int GROUP_ID = 808;
        List<String> tmpChars = new ArrayList<>();
        int firstLineChildId = 8;
        int maxChildId = 18;
        if (widgetLoaded.getGroupId() == GROUP_ID)
        {
            Widget line;
            for (int i = 0; i <= maxChildId - firstLineChildId; i++) {
                line = client.getWidget(GROUP_ID, firstLineChildId + i);
                if (line == null) break;
                Matcher matcher = WHITE_TEXT.matcher(line.getText());
                if (matcher.find())
                {
                    tmpChars.add(matcher.group(1));
                }
            }
        }

        if (tmpChars.size() == 4)
        {
            inputArrows.setSolution(
                    directionToInt.get(tmpChars.get(0)),
                    directionToInt.get(tmpChars.get(1)),
                    directionToInt.get(tmpChars.get(2)),
                    directionToInt.get(tmpChars.get(3))
            );

            knowPoemSolution.setShouldPass(true);
        }
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return List.of(coins.quantity(30), combatGear, food, prayerPotions.quantity(2));
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return List.of(quetzalFeed.quantity(10), limestoneBrick.quantity(3), softClay.quantity(4), staminaPotions, civitasIllaFortisTeleport);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        return List.of(
                new SkillRequirement(Skill.MINING, 55),
                new SkillRequirement(Skill.THIEVING, 48),
                new SkillRequirement(Skill.SLAYER, 48),
                new SkillRequirement(Skill.AGILITY, 46),
                new QuestRequirement(QuestHelperQuest.TWILIGHTS_PROMISE, QuestState.FINISHED)
        );
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return List.of(
                "Several Emissary Brawlers (levels 74 and 86)",
                "Several Emissary Conjurers (level 71)",
                "Prince Itzla Arkan (level 167)",
                "Amoxliatl (level 263)"
        );
    }

    @Override
    public QuestPointReward getQuestPointReward()
    {
        return new QuestPointReward(2);
    }

    @Override
    public List<ExperienceReward> getExperienceRewards()
    {
        return List.of(
                new ExperienceReward(Skill.MINING, 8000),
                new ExperienceReward(Skill.THIEVING, 8000),
                new ExperienceReward(Skill.SLAYER, 8000),
                new ExperienceReward(Skill.AGILITY, 8000)
        );
    }

    @Override
    public List<UnlockReward> getUnlockRewards()
    {
        return Collections.singletonList(new UnlockReward("Access to Tapoyauik"));
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();

        allSteps.add(new PanelDetails("Starting off", List.of(talkToItzlaAtTeomat, travelToGorge, talkToBartender, restOnBed, talkToPrinceAfterRest,
                talkToShopkeeper, talkToPrinceInPubAgain), List.of(coins.quantity(30))));
        allSteps.add(new PanelDetails("The Tower", List.of(talkToPrinceAtTower, buildSalvagerOverlookLandingSite, talkToPrinceAtTowerAfterLanding,
                talkToFelius, talkToCaritta, talkToSergius, talkToNova, talkToPrinceAfterRecruits, talkToJanus, climbUpToFirstTrial), List.of(combatGear, food),
                List.of(staminaPotions, quetzalFeed.quantity(10), limestoneBrick.quantity(3), softClay.quantity(4))));

        Collection<QuestStep> chestSteps = openKeywordChestSouthWest.getSteps();
        List<QuestStep> firstTrialSteps = new ArrayList<>(List.of(pickpocketAscended, useKeyOnSouthEastGate, searchChestForBookAndPaper));
        firstTrialSteps.addAll(chestSteps);
        firstTrialSteps.addAll(List.of(readPoem, talkToPrinceAfterPoem, openKeywordChestNorthWest, inputArrowsPuzzleWrapped, combineScraps, readCompletedNote,
                tellJanusPasscode));
        PanelDetails firstTrial = new PanelDetails("First Trial", firstTrialSteps);
        allSteps.add(firstTrial);

        allSteps.add(new PanelDetails("Second Trial", List.of(startCombatTrial, completeCombatTrial, talkToJanusAfterTrial), List.of(combatGear)));
        allSteps.add(new PanelDetails("Third Trial", List.of(talkToPrinceToStartThirdTrial, talkToTenoch, talkToSilia, talkToAdrius, talkToEleuia,
                accuseGuiltyIndividualPuzzleWrapped, goUpToFinalTrial)));
        allSteps.add(new PanelDetails("Final Trial", List.of(fightPrinceSidebar, talkToJanusAfterPrinceFight)));
        allSteps.add(new PanelDetails("Cult", List.of(talkToJanusAfterAllTrials, searchChestForEmissaryRobes, talkToItzlaToFollow, enterTemple, talkToItzlaAfterSermon, talkToFides)));
        allSteps.add(new PanelDetails("The Old Ones", List.of(enterRuins, takePickaxe, mineRocks, pullFirstLever, climbDownLedge, slideAlongIceLedge, pullSecondLever, jumpOverFrozenPlatforms, pullThirdLever,
                pullFourthLever, pullChain, inspectAirMarkings, inspectEarthMarkings, searchAirUrn, searchEarthUrn, inspectWaterMarkings, inspectFireMarkings,
                searchFireUrn, searchWaterUrn, fixWaterStatue, fixFireStatue, fixEarthStatue, fixAirStatue, activateFirstStatue, activateSecondStatue,
                activateThirdStatue, activateFourthStatue, enterFinalBossRoom, defeatAmoxliatlSidebar, talkToServius), List.of(combatGear, freeInvSlots4),
                List.of(food,
                prayerPotions, civitasIllaFortisTeleport)));

        return allSteps;
    }
}
