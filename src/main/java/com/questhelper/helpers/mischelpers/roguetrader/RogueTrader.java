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
package com.questhelper.helpers.mischelpers.roguetrader;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;

public class RogueTrader extends ComplexStateQuestHelper
{
    //Items Required
    ItemRequirement dyes3, hides3, coins, dye3OrHide3;

    Requirement canDoClothes, canDoBlackjack, canDoRunes, talkedToAubury, inRunedoku, solvedSmallRunedoku, solvedBothRundoku,
            talkedToBlackjackGuyOnce, newBlackjacksAvailable, reportedOnBlackjacks, givenSiamunItems;

    QuestStep talkToAli, talkToAubury, talkToAliAfterAubury, solveRunedoku, talkToAliForLargeRunedoku;

    QuestStep talkToBlackjackSellerOffensive, talkToAliAfterBeingHit;
    QuestStep talkToBlackjackSellerDefensive, talkToStreetUrchin, talkToAliAfterUrchin;

    QuestStep talkToSiamun, talkToAliAfterSiamun;

    ConditionalStep runeSteps, blackjackStepsOffensive, blackjackDefensiveSteps;

    @Override
    public QuestStep loadStep()
    {
        initializeRequirements();
        setupSteps();

        runeSteps = new ConditionalStep(this, talkToAubury);
        runeSteps.addStep(inRunedoku, solveRunedoku);
        runeSteps.addStep(solvedSmallRunedoku, talkToAliForLargeRunedoku);
        runeSteps.addStep(talkedToAubury, talkToAliAfterAubury);
        runeSteps.setLockingCondition(solvedBothRundoku);

        blackjackDefensiveSteps = new ConditionalStep(this, talkToBlackjackSellerDefensive);
        blackjackDefensiveSteps.addStep(newBlackjacksAvailable, talkToAliAfterUrchin);
        blackjackDefensiveSteps.addStep(talkedToBlackjackGuyOnce, talkToStreetUrchin);
        blackjackDefensiveSteps.setLockingCondition(reportedOnBlackjacks);

        blackjackStepsOffensive = new ConditionalStep(this, talkToBlackjackSellerOffensive);
        blackjackStepsOffensive.addStep(newBlackjacksAvailable, talkToAliAfterBeingHit);
        blackjackStepsOffensive.setLockingCondition(reportedOnBlackjacks);

        ConditionalStep clothingSteps = new ConditionalStep(this, talkToSiamun);
        clothingSteps.addStep(givenSiamunItems, talkToAliAfterSiamun);

        ConditionalStep steps = new ConditionalStep(this, talkToAli);
        steps.addStep(and(solvedBothRundoku, reportedOnBlackjacks), clothingSteps);
        steps.addStep(solvedBothRundoku, blackjackStepsOffensive);
        // This will only be reached if the offensive route is manually locked
        steps.addStep(or(solvedBothRundoku), blackjackDefensiveSteps);
        steps.addStep(canDoRunes, runeSteps);

        return steps;
    }

    @Override
    protected void setupRequirements()
    {
        ItemRequirement blueDye = new ItemRequirement("Blue dye", ItemID.BLUEDYE, 3);
        ItemRequirement redDye = new ItemRequirement("Red dye", ItemID.REDDYE, 3);
        ItemRequirement yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOWDYE, 3);
        ItemRequirement purpleDye = new ItemRequirement("Purple dye", ItemID.PURPLEDYE, 3);
        ItemRequirement orangeDye = new ItemRequirement("Orange dye", ItemID.ORANGEDYE, 3);
        ItemRequirement greenDye = new ItemRequirement("Green dye", ItemID.GREENDYE, 3);
        dyes3 = new ItemRequirements(LogicType.OR,"3 of any dye", blueDye, redDye, yellowDye, purpleDye, orangeDye, greenDye);

        ItemRequirement cowhide = new ItemRequirement("Cow hide", ItemID.COW_HIDE, 3);
        ItemRequirement greyWolfFur = new ItemRequirement("Grey wolf fur", ItemID.GREY_WOLF_FUR, 3);
        ItemRequirement snakeskin = new ItemRequirement("Snake skin", ItemID.VILLAGE_SNAKE_SKIN, 3);
        ItemRequirement bearFur = new ItemRequirement("Bear fur", ItemID.FUR, 3);
        ItemRequirement wool = new ItemRequirement("Wool", ItemID.WOOL, 3);
        hides3 = new ItemRequirements(LogicType.OR, "3 of any fur", cowhide, greyWolfFur, snakeskin, bearFur, wool);

        dye3OrHide3 = new ItemRequirements(LogicType.OR, "3 of any fur or dye", dyes3, hides3);

        coins = new ItemRequirement("Coins", ItemCollections.COINS);

        canDoBlackjack = new VarbitRequirement(VarbitID.ROGUETRADER_BLACKJACKS, 1, Operation.GREATER_EQUAL);
        canDoClothes = new VarbitRequirement(VarbitID.ROGUETRADER_CLOTHES, 1, Operation.GREATER_EQUAL);
        canDoRunes = new VarbitRequirement(VarbitID.ROGUETRADER_RUNES, 1, Operation.GREATER_EQUAL);
        talkedToAubury = new VarbitRequirement(VarbitID.ROGUETRADER_RUNES, 2, Operation.GREATER_EQUAL);

        solvedSmallRunedoku = or(new VarbitRequirement(VarbitID.ROGUETRADER_RUNES, 3), new VarbitRequirement(VarbitID.ROGUETRADER_RUNES, 5));
        solvedBothRundoku = new VarbitRequirement(VarbitID.ROGUETRADER_RUNES, 5, Operation.GREATER_EQUAL);
        inRunedoku = new WidgetPresenceRequirement(InterfaceID.RoguetraderSudoku.GAME_RUNES);

        talkedToBlackjackGuyOnce = new VarbitRequirement(VarbitID.ROGUETRADER_BLACKJACKS, 2, Operation.GREATER_EQUAL);
        newBlackjacksAvailable = new VarbitRequirement(VarbitID.ROGUETRADER_BLACKJACKS, 3, Operation.GREATER_EQUAL);

        reportedOnBlackjacks = new VarbitRequirement(VarbitID.ROGUETRADER_BJTOLD_ALI, 1);

        var givenSiamunDye = new VarbitRequirement(VarbitID.ROGUETRADER_CLOTHES, 2);
        var givenSiamunFur = new VarbitRequirement(VarbitID.ROGUETRADER_CLOTHES, 3);
        givenSiamunItems = or(givenSiamunDye, givenSiamunFur);
    }

    public void setupSteps()
    {
        talkToAli = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Talk to Ali Morrisane in Al Kharid.");
        talkToAli.addDialogStep("What's in this for me?");

        talkToAubury = new NpcStep(this, NpcID.AUBURY, new WorldPoint(3253, 3401, 0), "Talk to Aubury in the Varrock rune shop.");
        talkToAubury.addDialogStep("Actually, I'm here with a business proposal from Ali Morrisane.");
        ((NpcStep) talkToAubury).addAlternateNpcs(NpcID.AUBURY_2OP, NpcID.AUBURY_3OP);

        talkToAliAfterAubury = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Return to Ali Morrisane in Al Kharid.");
        talkToAliAfterAubury.addDialogSteps("View runes.", "I would like to have a look at your selection of runes.", "Try opening a small casket of runes",
                "Try to open a small casket of runes", "Examine lock.");
        talkToAliForLargeRunedoku = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Talk to Ali Morrisane in Al Kharid to open the large " +
                "rune casket.");
        talkToAliForLargeRunedoku.addDialogSteps("View runes.", "I would like to have a look at your selection of runes.", "Try opening a large casket of " +
                "runes", "Try to open a large casket of runes", "Examine lock.");
        // Clicked `What's in this for me?`, ends up with ROGUETRADER_CHEAPFLIGHTS going to 1

        // Near mage training arena, SANITY 0->100
        // SLUG2_REGIONUID 29090823 -> 29091600

        // Hang on a second. -> ROGUETRADER_STALLVAR 0->1

        // TEMP1 top 2 rows

        solveRunedoku = new RunedokuSolver(this).puzzleWrapStep();

        talkToBlackjackSellerOffensive = new NpcStep(this, NpcID.FEUD_BJSELLER, new WorldPoint(3351, 2971, 0), "Talk to the blackjack seller in " +
                "Pollnivneach. Let him hit you twice with a blackjack to unlock offensive blackjacks. If you want defensive ones instead, mark this section " +
                "in the sidebar to follow the guide for unlocking those instead.");
        talkToBlackjackSellerOffensive.addDialogSteps("I bet you couldn't knock me out with your blackjack.", "I'm sure. No pain no gain. Grrr!", "Go again.");
        talkToAliAfterBeingHit = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Return to Ali to report on the blackjacks.");
        talkToAliAfterUrchin = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Return to Ali to report on the blackjacks.");

        talkToBlackjackSellerDefensive = new NpcStep(this, NpcID.FEUD_BJSELLER, new WorldPoint(3351, 2971, 0), "Talk to the blackjack seller in " +
                "Pollnivneach.");
        talkToStreetUrchin = new NpcStep(this, NpcID.FEUD_STREET_URCHIN, new WorldPoint(3351, 2971, 0), "Talk to the street urchin near" +
                " the blackjack seller.", coins.quantity(100));
        talkToStreetUrchin.addDialogStep("Fair enough, here you go.");

        talkToSiamun = new NpcStep(this, NpcID.ICS_LITTLE_LINEN2, new WorldPoint(3314, 2785, 0), "Talk to Siamun in Sophanem.", dye3OrHide3);
        talkToSiamun.addDialogStep("Ok, I'll get you the materials.");

        talkToAliAfterSiamun = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Return to Ali to report on the clothing.");
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new QuestRequirement(QuestHelperQuest.THE_FEUD, QuestState.FINISHED));
        req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));
        req.add(new QuestRequirement(QuestHelperQuest.ICTHLARINS_LITTLE_HELPER, QuestState.FINISHED));
        return req;
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(dye3OrHide3, coins.quantity(100));
    }


    @Override
    public List<UnlockReward> getUnlockRewards()
    {
        return Arrays.asList(
                new UnlockReward("Access to Ali Morrisane's Rune Shop"),
                new UnlockReward("Access to Ali Morrisane's Clothing Shop"),
                new UnlockReward("Access to Ali Morrisane's Blackjack Shop"));
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Starting off", List.of(talkToAli)));
        PanelDetails runePanel = new PanelDetails("Rune shop", List.of(talkToAubury, talkToAliAfterAubury, solveRunedoku, talkToAliForLargeRunedoku));
        runePanel.setLockingStep(runeSteps);
        allSteps.add(runePanel);

        PanelDetails blackjackOffensivePanel = new PanelDetails("Blackjack shop (offensive)", List.of(talkToBlackjackSellerOffensive, talkToAliAfterBeingHit));
        blackjackOffensivePanel.setLockingStep(blackjackStepsOffensive);
        allSteps.add(blackjackOffensivePanel);

        PanelDetails blackjackDefensivePanel = new PanelDetails("Blackjack shop (defensive)", List.of(talkToBlackjackSellerDefensive, talkToStreetUrchin,
                talkToAliAfterUrchin), coins.quantity(100));
        blackjackDefensivePanel.setLockingStep(blackjackDefensiveSteps);
        allSteps.add(blackjackDefensivePanel);

        allSteps.add(new PanelDetails("Clothes shop", List.of(talkToSiamun, talkToAliAfterSiamun), dye3OrHide3));

        return allSteps;
    }

    @Override
    public QuestState getState(Client client)
    {
        int questCompleted = client.getVarbitValue(VarbitID.DESERT_ALKHARID_ALIS_VISIBLE);
        if (questCompleted == 1)
        {
            return QuestState.FINISHED;
        }
        int questStarted = client.getVarbitValue(VarbitID.ROGUETRADER_RUNES);
        if (questStarted >= 1)
        {
            return QuestState.IN_PROGRESS;
        }

        return QuestState.NOT_STARTED;
    }

    @Override
    public boolean isCompleted()
    {
        int questCompleted = client.getVarbitValue(VarbitID.DESERT_ALKHARID_ALIS_VISIBLE);
        return questCompleted == 1;
    }
}
