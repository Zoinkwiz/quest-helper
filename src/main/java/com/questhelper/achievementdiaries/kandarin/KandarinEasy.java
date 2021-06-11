/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.achievementdiaries.kandarin;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
        quest = QuestHelperQuest.KANDARIN_EASY
)

public class KandarinEasy extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement combatGear, bigFishingNet, coins, juteSeed, seedDibber, rake, batteredKey, fishBowl, seaweed, fishBowlSeaweed, tinyNet;

    // Items recommended
    ItemRequirement food;

    Requirement notCatchMackerel, notBuyCandle, notCollectFlax, notPlayOrgan, notPlantJute, notCupTea, notKillEle, notPetFish, notBuyStew, notTalkSherlock, notLogShortcut;

    Requirement eleWorkI;

    // quest steps
    QuestStep buyCandle, collectFlax, playOrgan, plantJute, cupTea, petFish, buyStew, talkSherlock, logShortcut, claimReward, moveToWorkshop, petFishMix, petFishFish;

    NpcStep catchMackerel, killEle;

    Zone workshop;

    ZoneRequirement inWorkshop;

    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doEasy = new ConditionalStep(this, claimReward);
        doEasy.addStep(notCatchMackerel, catchMackerel);
        doEasy.addStep(new Conditions(notPetFish, fishBowlSeaweed, tinyNet), petFishFish);
        doEasy.addStep(new Conditions(notPetFish, fishBowlSeaweed), petFish);
        doEasy.addStep(new Conditions(notPetFish, fishBowl, seaweed), petFishMix);
        doEasy.addStep(notBuyCandle, buyCandle);
        doEasy.addStep(notCollectFlax, collectFlax);
        doEasy.addStep(notTalkSherlock, talkSherlock);
        doEasy.addStep(notBuyCandle, buyCandle);
        doEasy.addStep(new Conditions(notKillEle, inWorkshop), killEle);
        doEasy.addStep(notKillEle, moveToWorkshop);
        doEasy.addStep(notPlayOrgan, playOrgan);
        doEasy.addStep(notPlantJute, plantJute);
        doEasy.addStep(notBuyStew, buyStew);
        doEasy.addStep(notCupTea, cupTea);
        doEasy.addStep(notLogShortcut, logShortcut);

        return doEasy;
    }

    public void setupRequirements()
    {
        notCatchMackerel = new VarplayerRequirement(1178, false, 1);
        notBuyCandle = new VarplayerRequirement(1178, false, 2);
        notCollectFlax = new VarplayerRequirement(1178, false, 3);
        notPlayOrgan = new VarplayerRequirement(1178, false, 4);
        notPlantJute = new VarplayerRequirement(1178, false, 5);
        notCupTea = new VarplayerRequirement(1178, false, 6);
        notKillEle = new VarplayerRequirement(1178, false, 7);
        notPetFish = new VarplayerRequirement(1178, false, 8);
        notBuyStew = new VarplayerRequirement(1178, false, 9);
        notTalkSherlock = new VarplayerRequirement(1178, false, 10);
        notLogShortcut = new VarplayerRequirement(1178, false, 11);

        coins = new ItemRequirement("Coins", ItemID.COINS_995).showConditioned(new Conditions(LogicType.OR, notPetFish, notBuyCandle, notBuyStew));
        bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).showConditioned(notCatchMackerel);
        fishBowl = new ItemRequirement("Filled fishbowl", ItemID.FISHBOWL).showConditioned(notPetFish);// check fishbowl id
        seaweed = new ItemRequirement("Seaweed", ItemID.SEAWEED).showConditioned(notPetFish);
        juteSeed = new ItemRequirement("Jute seeds", ItemID.JUTE_SEED).showConditioned(notPlantJute);
        rake = new ItemRequirement("Small fishing net", ItemID.RAKE).showConditioned(notPlantJute);
        seedDibber = new ItemRequirement("Small fishing net", ItemID.SEED_DIBBER).showConditioned(notPlantJute);
        batteredKey = new ItemRequirement("Battered key", ItemID.BATTERED_KEY).showConditioned(notKillEle);
        fishBowlSeaweed = new ItemRequirement("Fishbowl with seaweed", ItemID.FISHBOWL_6669).showConditioned(notPetFish);
        tinyNet = new ItemRequirement("Tiny fish net", ItemID.TINY_NET).showConditioned(notPetFish);

        combatGear = new ItemRequirement("Combat gear to defeat all types of elementals (level 35)", -1, -1).showConditioned(notKillEle);
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

        eleWorkI = new QuestRequirement(QuestHelperQuest.ELEMENTAL_WORKSHOP_I, QuestState.FINISHED);

        inWorkshop = new ZoneRequirement(workshop);
    }

    public void loadZones()
    {
        workshop = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
    }

    public void setupSteps()
    {
        catchMackerel = new NpcStep(this, NpcID.FISHING_SPOT_1518, new WorldPoint(2708, 3209, 0),
                "Fish until you get a mackerel.", bigFishingNet);
        catchMackerel.addAlternateNpcs(NpcID.FISHING_SPOT_1520);
        buyCandle = new NpcStep(this, NpcID.CANDLE_MAKER, new WorldPoint(2799, 3439, 0),
                "Buy a candle from the candle maker.", coins.quantity(3));
        petFish = new NpcStep(this, NpcID.HARRY, new WorldPoint(2833, 3443, 0),
                "Speak with harry to get a pet fish.", fishBowlSeaweed, coins.quantity(10));
        petFish.addDialogStep("Can I get a fish for this bowl?");
        petFish.addDialogStep("I'll take it!");
        petFishMix = new ItemStep(this, "Put seaweed into the fishbowl.", fishBowl.highlighted(), seaweed.highlighted());
        petFishFish = new ObjectStep(this, 10091, new WorldPoint(2831, 3445, 0),
                "Fish in the aquarium");
        collectFlax = new ObjectStep(this, ObjectID.FLAX, new WorldPoint(2742, 3446, 0),
                "Pick 5 flax.");
        talkSherlock = new NpcStep(this, NpcID.SHERLOCK, new WorldPoint(2735, 3413, 0),
                "Speak with Sherlock.");
        moveToWorkshop = new ObjectStep(this, ObjectID.STAIRCASE_3415, new WorldPoint(2711, 3498, 0),
                "With battered key in inventory, go through the wall then go down the staircase.", batteredKey);
        killEle = new NpcStep(this, NpcID.FIRE_ELEMENTAL, new WorldPoint(2719, 9889, 0),
                "Kill one of each elemental.", combatGear, food);
        killEle.addAlternateNpcs(NpcID.WATER_ELEMENTAL);
        killEle.addAlternateNpcs(NpcID.EARTH_ELEMENTAL);
        killEle.addAlternateNpcs(NpcID.AIR_ELEMENTAL);
        buyStew = new NpcStep(this, NpcID.BARTENDER_1318, new WorldPoint(2691, 3494, 0),
                "Speak with the bartender and buy a stew.", coins.quantity(20));
        buyStew.addDialogStep("What do you have?");
        buyStew.addDialogStep("Could I have some stew please?");
        playOrgan = new ObjectStep(this, ObjectID.CHURCH_ORGAN_25818, new WorldPoint(2692, 3463, 0),
                "Play the organ.");
        plantJute = new ObjectStep(this, 8176, new WorldPoint(2669, 3523, 0),
                "Plant jute seed.", juteSeed.quantity(3), seedDibber, rake);
        plantJute.addIcon(ItemID.JUTE_SEED);
        cupTea = new NpcStep(this, NpcID.GALAHAD, new WorldPoint(2612, 3474, 0),
                "Speak with Galahad.");
        logShortcut = new ObjectStep(this, ObjectID.LOG_BALANCE_23274, new WorldPoint(2602, 3477, 0),
                "Cross the log shortcut.");

        claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
                "Talk to the 'Wedge' infront of camelot castle to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(coins.quantity(33), bigFishingNet, juteSeed.quantity(3), seedDibber, rake, batteredKey, fishBowl, seaweed, combatGear);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(food);
    }


    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("4 level 35 elementals");
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skill.AGILITY, 20));
        req.add(new SkillRequirement(Skill.FARMING, 13));
        req.add(new SkillRequirement(Skill.FISHING, 16));

        return req;
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Catch Makerel", Arrays.asList(catchMackerel)));
        allSteps.add(new PanelDetails("Buy Candle", Arrays.asList(buyCandle)));
        allSteps.add(new PanelDetails("Pet Fish", Arrays.asList(petFishMix, petFish, petFishFish), coins.quantity(10), fishBowl, seaweed));
        allSteps.add(new PanelDetails("Collect Flax", Arrays.asList(collectFlax)));
        allSteps.add(new PanelDetails("Sherlock", Arrays.asList(talkSherlock)));
        allSteps.add(new PanelDetails("Defeat Elementals", Arrays.asList(moveToWorkshop, killEle), combatGear, food));
        allSteps.add(new PanelDetails("Play Church Organ", Arrays.asList(playOrgan)));
        allSteps.add(new PanelDetails("Plant Jute", Arrays.asList(plantJute), juteSeed.quantity(3), seedDibber, rake));
        allSteps.add(new PanelDetails("Buy Stew", Arrays.asList(buyStew), coins.quantity(20)));
        allSteps.add(new PanelDetails("Cut of Tea", Arrays.asList(cupTea)));
        allSteps.add(new PanelDetails("Log Shortcut", Arrays.asList(logShortcut)));
        allSteps.add(new PanelDetails("Finishing off", Arrays.asList(claimReward)));

        return allSteps;
    }
}
