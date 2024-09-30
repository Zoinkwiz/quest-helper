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
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.npc.NpcInteractingWithNpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.questhelper.requirements.util.LogicHelper.*;

public class TheHeartOfDarkness extends BasicQuestHelper
{

    DetailedQuestStep tellJanusPasscode;
    ArrowChestPuzzleStep inputArrows;

    ManualRequirement knowPoemSolution, hasReadCompletedNote;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        ItemRequirement combatGear, food, coins;

        ItemRequirement quetzalFeed, limestoneBrick, softClay, pickaxe;

        ItemRequirement towerKey, book, poem, scrapOfPaper1, scrapOfPaper2, scrapOfPaper3, completedNote, emissaryHood, emissaryTop, emissaryBottom,
                emissaryBoots, emissaryRobesEquipped, emissaryRobes;

        Requirement atTeomat, builtLandingInOverlook, talkedToSergius, talkedToCaritta, talkedToFelius, princeIsFollowing, inFirstTrialRoom,
                inSecondTrialRoom, southEastGateUnlocked, southWestChestOpened, hasReadPoem, knowAboutDirections, inArrowPuzzle, combatStarted,
                startedInvestigation;

        Requirement tenochGuilty, hasTalkedToTenoch, siliaGuilty, hasTalkedToSilia, hasTalkedToAdrius, eleuiaGuilty, hasTalkedToEleuia, inFirstIceRoom,
                inSecondIceRoom, pulledFirstLever, pulledSecondLever, pulledThirdLever, pulledFourthLever;

        Zone teomat, firstTrialRoom, secondTrialRoom, firstIceRoom, secondIceRoom;

        DetailedQuestStep talkToItzlaAtTeomat,travelToGorge, talkToBartender, restOnBed, talkToPrinceAfterRest, talkToShopkeeper, talkToPrinceInPubAgain,
                talkToPrinceAtTower, buildSalvagerOverlookLandingSite, talkToPrinceAtTowerAfterLanding, talkToNova, talkToSergius, talkToFelius, talkToCaritta, talkToPrinceAfterRecruits,
                talkToJanus, climbUpToFirstTrial;
        DetailedQuestStep pickpocketAscended, useKeyOnSouthEastGate, searchChestForBookAndPaper, readPoem, talkToPrinceAfterPoem,
                openKeywordChestNorthWest, combineScraps, readCompletedNote;

        LockedChestPuzzle openKeywordChestSouthWest;

        DetailedQuestStep startCombatTrial;
        NpcStep completeCombatTrial;
        DetailedQuestStep talkToJanusAfterTrial;

        DetailedQuestStep talkToPrinceToStartThirdTrial;
        NpcStep talkToTenoch, talkToSilia, talkToAdrius, talkToEleuia, accuseTenoch, accuseSilia, accuseEleuia;

        DetailedQuestStep goUpToFinalTrial, fightPrince, fightPrinceSidebar, talkToJanusAfterPrinceFight, talkToJanusAfterAllTrials, searchChestForEmissaryRobes, talkToItzlaToFollow,
            enterTemple, talkToItzlaAfterSermon, talkToFides, enterRuins;

        // Ice dungeon
        DetailedQuestStep takePickaxe;
        DetailedQuestStep mineRocks;
        ObjectStep pullFirstLever;
        DetailedQuestStep climbDownLedge;
        DetailedQuestStep slideAlongIceLedge;
        DetailedQuestStep pullSecondLever;
        DetailedQuestStep jumpOverFrozenLatforms;
        DetailedQuestStep climbDownLedge2;
        DetailedQuestStep pullThirdLever;
        DetailedQuestStep pullFourthLever;
        DetailedQuestStep pullChain;

        DetailedQuestStep collectUrns, fixStatues, inspectMarkings, doStatuePuzzle, goToFinalBoss;

        DetailedQuestStep defeatAmoxliatl, talkToServius;

        Map<Integer, QuestStep> steps = new HashMap<>();

        initializeRequirements();
        // Required items
        combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
        food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, 10);
        coins = new ItemRequirement("Coins", ItemCollections.COINS);

        // Recommended items
        quetzalFeed = new ItemRequirement("Quetzal feed", ItemID.QUETZAL_FEED_29307);
        limestoneBrick = new ItemRequirement("Limestone brick", ItemID.LIMESTONE_BRICK);
        softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);
        pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);

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

        setupConditions();
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
        NpcInteractingRequirement playerIsAttacked = new NpcInteractingRequirement(
                NpcID.EMISSARY_BRAWLER, NpcID.EMISSARY_BRAWLER_13774, NpcID.EMISSARY_BRAWLER_13775, NpcID.EMISSARY_BRAWLER_13776,
                NpcID.EMISSARY_CONJURER, NpcID.EMISSARY_CONJURER_13778);
        NpcInteractingWithNpcRequirement princeIsAttacked = new NpcInteractingWithNpcRequirement(NpcID.PRINCE_ITZLA_ARKAN_13771, "Emissary Conjurer",
                "Emissary Brawler");
        combatStarted = or(playerIsAttacked, princeIsAttacked);
        startedInvestigation = new VarbitRequirement(11134, 1);
        // Accused Tenoch incorrectly
        // 11135 0->1
        // 11140 0->1

        DialogRequirement tenochInnocent = new DialogRequirement("Tenoch", false,
                "To do my duty, of course. Ralos and Ranul wish us to bring about the end of this world. Only then will we see the birth of a new one.",
                "The Final Dawn is our destiny. Ralos and Ranul will unite us all and end this world, but in doing so, we will all play a part in forming a new one.");
        tenochGuilty = new DialogRequirement("Tenoch", false,
                "To do my duty, of course. Ralos and Ranul wish us to bring about the end of this world. Only then will we will all unite in Mictl.",
                "The Final Dawn is our destiny. Ralos and Ranul will unite us all and end this world, but in doing so, they will grant us eternal life in Mictl.");
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


        firstIceRoom = new Zone(new WorldPoint(1660, 9595, 2), new WorldPoint(1724, 9659, 2));
        inFirstIceRoom = new ZoneRequirement(firstIceRoom);

        secondIceRoom = new Zone(new WorldPoint(1699, 9602, 1), new WorldPoint(1726, 9639, 1));
        inSecondIceRoom = new ZoneRequirement(secondIceRoom);

        pulledFirstLever = new VarbitRequirement(11138, 1);
        pulledSecondLever = new VarbitRequirement(11139, 1);
        pulledThirdLever = new VarbitRequirement(11140, 1);
        pulledFourthLever = new VarbitRequirement(11141, 1);

        // Steps
        setupSteps();
        talkToItzlaAtTeomat = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_12896, new WorldPoint(1454, 3173, 0), "Talk to Prince Itzla Arkan at the Teomat.");
        travelToGorge = new NpcStep(this, NpcID.RENU, new WorldPoint(1437, 3169, 0), "Travel on Renu to the Quetzacalli Gorge.");
        ((NpcStep) travelToGorge).addAlternateNpcs(NpcID.RENU_13349, NpcID.RENU_13350, NpcID.RENU_13351, NpcID.RENU_13352, NpcID.RENU_13353, NpcID.RENU_13354);
        travelToGorge.addWidgetHighlight(874, 15, 9);
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

        List<WorldPoint> pathFromPubToOverlook = new ArrayList<>();
        pathFromPubToOverlook.addAll(pathFromPubToChasm);
        pathFromPubToOverlook.addAll(pathFromChasmToOverlook);

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
        climbUpToFirstTrial = new ObjectStep(this, ObjectID.STAIRCASE_54369, new WorldPoint(1635, 3221, 0), "Climb up the staircase in the tower to the first" +
                " challenge.");
        pickpocketAscended = new NpcStep(this, NpcID.EMISSARY_ASCENDED_13768, new WorldPoint(1641, 3225, 1), "Pickpocket one of the emissary ascended.", true);
        ((NpcStep) pickpocketAscended).addAlternateNpcs(NpcID.EMISSARY_ASCENDED_13767);
        useKeyOnSouthEastGate = new ObjectStep(this, NullObjectID.NULL_55354, new WorldPoint(1644, 3220, 1), "Use the key to open the gate in the south-east " +
                "corner of the room.", towerKey);
        searchChestForBookAndPaper = new ObjectStep(this, ObjectID.CHEST_54372, new WorldPoint(1644, 3217, 1), "Search the south-east chest for a book and " +
                "some paper.");
        openKeywordChestSouthWest = new LockedChestPuzzle(this);
        readPoem = new DetailedQuestStep(this, "Read the poem.", poem.highlighted());
        talkToPrinceAfterPoem = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13770, new WorldPoint(1638, 3218, 1), "Talk to the prince in the room.");
        talkToPrinceAfterPoem.addDialogSteps("Makt.", "Takam.", "Silam.", "Thanks! Could you translate another?");
        // Makt = north, 11171 0->1
        // Takam = east
        // Silam = west
        openKeywordChestNorthWest = new ObjectStep(this, ObjectID.CHEST_54374, new WorldPoint(1636, 3225, 1), "Open the north-west chest.");
        inputArrows = new ArrowChestPuzzleStep(this);
        combineScraps = new DetailedQuestStep(this,  "Inspect one of the scraps to combine them.", scrapOfPaper1.highlighted(), scrapOfPaper2.highlighted(),
                scrapOfPaper3.highlighted());

        readCompletedNote = new DetailedQuestStep(this, "Read the completed note.", completedNote.highlighted());
        tellJanusPasscode = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1644, 3226, 1), "Talk to Janus and tell him the passcode.");
        tellJanusPasscode.addDialogSteps("About that passphrase...", "Yes.");

        startCombatTrial = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1644, 3225, 2), "Talk to Forebearer Janus, ready to fight.");
        startCombatTrial.addDialogStep("Yes! I'm ready.");
        completeCombatTrial = new NpcStep(this, NpcID.EMISSARY_BRAWLER, new WorldPoint(1644, 3225, 2), "Defeat the waves of enemies.", true);
        completeCombatTrial.setMustBeFocusedOnPlayer(true);
        completeCombatTrial.setMustBeFocusedOnNpcs(NpcID.PRINCE_ITZLA_ARKAN_13771);
        completeCombatTrial.addAlternateNpcs(NpcID.EMISSARY_BRAWLER_13774, NpcID.EMISSARY_BRAWLER_13775, NpcID.EMISSARY_BRAWLER_13776,
                NpcID.EMISSARY_CONJURER, NpcID.EMISSARY_CONJURER_13778);
        talkToJanusAfterTrial = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1644, 3225, 2),
                "Talk to Forebearer Janus again to progress to the third trial.");
        talkToJanusAfterTrial.addDialogSteps("Yes.", "Proceed to the next trial.");
        talkToPrinceToStartThirdTrial = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13770, new WorldPoint(1638, 3218, 2), "Talk to the prince in the room.");
        talkToTenoch = new NpcStep(this, NpcID.TENOCH, new WorldPoint(1643, 3225, 2), "Talk to Tenoch.");
        talkToTenoch.addDialogSteps("Interrogate Tenoch.", "Tell me about the Final Dawn.");
        talkToSilia = new NpcStep(this, NpcID.SILIA, new WorldPoint(1645, 3223, 2), "Talk to Silia.");
        talkToSilia.addDialogSteps("Interrogate Silia.", "Tell me about the Final Dawn.");
        talkToAdrius = new NpcStep(this, NpcID.ADRIUS, new WorldPoint(1645, 3220, 2), "Talk to Adrius.");
        talkToAdrius.addDialogSteps("Interrogate Adrius.", "Tell me about the Final Dawn.");
        talkToEleuia = new NpcStep(this, NpcID.ELEUIA, new WorldPoint(1643, 3218, 2), "Talk to Eleuia.");
        talkToEleuia.addDialogSteps("Interrogate Eleuia.", "Tell me about the Final Dawn.");

        accuseTenoch = new NpcStep(this, NpcID.TENOCH, new WorldPoint(1643, 3225, 2), "Accuse Tenoch.");
        accuseTenoch.addDialogSteps("Yes.", "Choose Tenoch.");
        accuseSilia = new NpcStep(this, NpcID.SILIA, new WorldPoint(1645, 3223, 2), "Accuse Silia.");
        accuseSilia.addDialogSteps("Yes.", "Choose Silia.");
        accuseEleuia = new NpcStep(this, NpcID.ELEUIA, new WorldPoint(1643, 3218, 2), "Accuse Eleuia.");
        accuseEleuia.addDialogSteps("Yes.", "Choose Eleuia.");

        goUpToFinalTrial = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1640, 3226, 2), "Talk to Janus to go to the final trial, ready for " +
                "a fight.");
        goUpToFinalTrial.addDialogStep("Yes.");
        fightPrince = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13784, new WorldPoint(1644, 3225, 2), "Defeat the prince. Use protect from melee. See " +
                "sidebar for more details.");
        fightPrinceSidebar = new DetailedQuestStep(this, "Defeat the prince. Use Protect from Melee.");
        fightPrinceSidebar.addText("He will protect from any special attacks you do.");
        fightPrinceSidebar.addText("He can do a sword swipe to turn off your prayers. Simply re-activate them straight away.");
        fightPrinceSidebar.addText("If he says he's doing a slam attack, step on the tile behind him to dodge it.");
        fightPrinceSidebar.addSubSteps(fightPrince);

        talkToJanusAfterPrinceFight = new NpcStep(this, NpcID.FOREBEARER_JANUS_13766, new WorldPoint(1640, 3226, 2), "Talk to Janus to wrap up the final " +
                "trial.");
        talkToJanusAfterAllTrials = new NpcStep(this, NpcID.FOREBEARER_JANUS, new WorldPoint(1640, 3226, 0), "Talk to Janus at the bottom of the tower.");
        searchChestForEmissaryRobes = new ObjectStep(this, ObjectID.CHEST_54515, new WorldPoint(1638, 3217, 0), "Search the chest in the south of the tower " +
                "for some emissary robes.");
        talkToItzlaToFollow = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13691, new WorldPoint(1638, 3222, 0), "Talk to the prince to have him follow you.");
        enterTemple = new DetailedQuestStep(this, new WorldPoint(1687, 3247, 0), "Enter the temple to the east of the tower for a cutscene.",
                emissaryRobesEquipped);
        talkToItzlaAfterSermon = new NpcStep(this, NpcID.PRINCE_ITZLA_ARKAN_13691, new WorldPoint(1688, 3247, 0), "Talk to the prince in the temple.",
                emissaryRobesEquipped);
        talkToFides = new NpcStep(this, NpcID.FOREBEARER_FIDES, new WorldPoint(1685, 3247, 0), "Talk to Forebearer Fides in the temple.", emissaryRobesEquipped);
        enterRuins = new ObjectStep(this, ObjectID.STAIRS_54525, new WorldPoint(1693, 3231, 0), "Go down the stairs south-east of the temple. Be equipped for" +
                " a fight.");
//
//        // Ice dungeon

        takePickaxe = new ObjectStep(this, ObjectID.BARREL_54517, new WorldPoint(1696, 9633, 2), "Take a pickaxe from the nearby barrel.");
        mineRocks = new ObjectStep(this, ObjectID.ROCKS_54501, new WorldPoint(1690, 9634, 2), "Mine the nearby rocks.", pickaxe);
        int LEVER_ID = 55367; // Decorative object
        pullFirstLever = new ObjectStep(this, LEVER_ID, new WorldPoint(1695, 9604, 2), "Pull the lever in the south-east of the area. Make sure to avoid the " +
                "wall spikes.");
        pullFirstLever.addTileMarker(new WorldPoint(1682, 9603, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1682, 9604, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1682, 9605, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1682, 9606, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);

        pullFirstLever.addTileMarker(new WorldPoint(1684, 9603, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1684, 9604, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1684, 9605, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1684, 9606, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);

        pullFirstLever.addTileMarker(new WorldPoint(1686, 9603, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1686, 9604, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1686, 9605, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1686, 9606, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);

        pullFirstLever.addTileMarker(new WorldPoint(1688, 9603, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1688, 9604, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1688, 9605, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1688, 9606, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);

        pullFirstLever.addTileMarker(new WorldPoint(1690, 9603, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1690, 9604, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1690, 9605, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        pullFirstLever.addTileMarker(new WorldPoint(1690, 9606, 2), SpriteID.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING);
        climbDownLedge = new ObjectStep(this, ObjectID.LEDGE_54531, new WorldPoint(1701, 9607, 2), "Climb down the ledge east of the lever.");
//        slideAlongIceLedge = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        pullSecondLever = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        jumpOverFrozenLatforms = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        climbDownLedge2 = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        pullThirdLever = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        pullFourthLever = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        pullChain = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//
//        collectUrns = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        fixStatues = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        inspectMarkings = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        doStatuePuzzle = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        goToFinalBoss = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//
//        defeatAmoxliatl = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");
//        talkToServius = new NpcStep(this, NpcID., new WorldPoint(0, 0, 0), "");

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
        goTalkToRecruits.addStep(not(talkedToCaritta), talkToCaritta);
        goTalkToRecruits.addStep(not(talkedToSergius), talkToSergius);
        goTalkToRecruits.addStep(not(talkedToFelius), talkToFelius);
        steps.put(24, goTalkToRecruits);

        steps.put(26, talkToPrinceAfterRecruits);

        ConditionalStep goEnterTower = new ConditionalStep(this, talkToPrinceAfterRecruits);
        goEnterTower.addStep(princeIsFollowing, talkToJanus);
        steps.put(28, goEnterTower);

        ConditionalStep goUpToFirstChallenge = new ConditionalStep(this, talkToPrinceAfterRecruits);
        goUpToFirstChallenge.addStep(princeIsFollowing, climbUpToFirstTrial);
        steps.put(30, goUpToFirstChallenge);

        ConditionalStep goDoFirstChallenge = new ConditionalStep(this, climbUpToFirstTrial);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, completedNote, hasReadCompletedNote), tellJanusPasscode);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, completedNote), readCompletedNote);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, scrapOfPaper1, scrapOfPaper2, scrapOfPaper3), combineScraps);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, southWestChestOpened, scrapOfPaper1, scrapOfPaper2, knowAboutDirections, knowPoemSolution, inArrowPuzzle), inputArrows);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, southWestChestOpened, scrapOfPaper1, scrapOfPaper2, knowAboutDirections, knowPoemSolution), openKeywordChestNorthWest);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, southWestChestOpened, scrapOfPaper1, scrapOfPaper2, knowAboutDirections), readPoem);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, southWestChestOpened, scrapOfPaper1, scrapOfPaper2, hasReadPoem), talkToPrinceAfterPoem);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, southWestChestOpened, scrapOfPaper1, scrapOfPaper2, poem), readPoem);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, scrapOfPaper1, book), openKeywordChestSouthWest);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, southEastGateUnlocked), searchChestForBookAndPaper);
        goDoFirstChallenge.addStep(and(inFirstTrialRoom, towerKey), useKeyOnSouthEastGate);
        goDoFirstChallenge.addStep(inFirstTrialRoom, pickpocketAscended);
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
        goToTemple.addStep(and(princeIsFollowing, emissaryRobes), enterTemple);
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
        goPullLevers.addStep(and(inFirstIceRoom, pulledFirstLever), climbDownLedge);
        goPullLevers.addStep(inFirstIceRoom, pullFirstLever);
        steps.put(62, goPullLevers);
        return steps;
    }

    @Override
    protected void setupRequirements()
    {

    }

    private void setupConditions()
    {


    }

    private void setupSteps()
    {

    }

    private static final Pattern WHITE_TEXT = Pattern.compile("<col=faf9f6>([A-Za-z]+)");


    Map<String, Integer> directionToInt = new HashMap<>();

    {
        directionToInt.put("Makt", 0);
        directionToInt.put("Takam", 1);
        directionToInt.put("Uitt", 2);
        directionToInt.put("Silam", 3);
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
                System.out.println(matcher.group(1) + ".");
                System.out.println(matcher.group(2) + ".");
                System.out.println(matcher.group(3) + ".");
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
        if (widgetLoaded.getGroupId() == GROUP_ID) {
            Widget line;
            for (int i = 0; i <= maxChildId - firstLineChildId; i++) {
                line = client.getWidget(GROUP_ID, firstLineChildId + i);
                if (line == null) break;
                Matcher matcher = WHITE_TEXT.matcher(line.getText());
                if (matcher.find()) {
                    tmpChars.add(matcher.group(1));
                }
            }
        }

        if (tmpChars.size() == 4) {
            inputArrows.setSolution(
                    directionToInt.get(tmpChars.get(0)),
                    directionToInt.get(tmpChars.get(1)),
                    directionToInt.get(tmpChars.get(2)),
                    directionToInt.get(tmpChars.get(3))
            );

            knowPoemSolution.setShouldPass(true);
        }
    }

//    @Override
//    public List<ItemRequirement> getItemRequirements()
//    {
//        return List.of(combatGear, food, coins.quantity(30));
//    }

//    @Override
//    public List<ItemRequirement> getItemRecommended()
//    {
//        return List.of(quetzalFeed.quantity(10), limestoneBrick.quantity(3), softClay.quantity(4));
//    }

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

//        allSteps.add(new PanelDetails("Starting off", List.of(talkToItzlaAtTeomat, travelToGorge, talkToBartender, restOnBed, talkToPrinceAfterRest)));
//        allSteps.add(new PanelDetails("The Tower", List.of(talkToPrinceAtTower), List.of(combatGear, food), List.of(quetzalFeed.quantity(10), limestoneBrick.quantity(3), softClay.quantity(4))));
        return allSteps;
    }
}
