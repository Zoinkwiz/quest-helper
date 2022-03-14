/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
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
    ItemRequirement combatGear, bigFishingNet, coins, juteSeed, seedDibber, rake, batteredKey,
		emptyFishbowl, fishBowl, seaweed, fishBowlSeaweed, tinyNet, genericFishbowl;

    // Items recommended
    ItemRequirement food;

    Requirement notCatchMackerel, notBuyCandle, notCollectFlax, notPlayOrgan, notPlantJute, notCupTea,
		notKillEle, notPetFish, notBuyStew, notTalkSherlock, notLogShortcut;

    Requirement eleWorkI;

    // quest steps
    QuestStep buyCandle, collectFlax, playOrgan, plantJute, cupTea, petFish, fillFishbowl,
		buyStew, talkSherlock, logShortcut, claimReward, moveToWorkshop, petFishMix, petFishFish;

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
        doEasy.addStep(new Conditions(notPetFish, fishBowl), petFishMix);
		doEasy.addStep(new Conditions(notPetFish), fillFishbowl);
        doEasy.addStep(notBuyCandle, buyCandle);
        doEasy.addStep(notCollectFlax, collectFlax);
        doEasy.addStep(notTalkSherlock, talkSherlock);
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

        coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(new Conditions(LogicType.OR, notPetFish, notBuyCandle, notBuyStew));
        bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).showConditioned(notCatchMackerel);

        emptyFishbowl = new ItemRequirement("Empty fishbowl", ItemID.EMPTY_FISHBOWL).showConditioned(notPetFish);
        fishBowl = new ItemRequirement("Filled fishbowl", ItemID.FISHBOWL).showConditioned(notPetFish);
		fishBowlSeaweed = new ItemRequirement("Fishbowl with seaweed", ItemID.FISHBOWL_6669).showConditioned(notPetFish);
		tinyNet = new ItemRequirement("Tiny fish net", ItemID.TINY_NET).showConditioned(notPetFish);
		genericFishbowl = new ItemRequirements(LogicType.OR, "Fishbowl", emptyFishbowl, fishBowl, fishBowlSeaweed).showConditioned(notPetFish);

        seaweed = new ItemRequirement("Seaweed", ItemID.SEAWEED).showConditioned(notPetFish);
        juteSeed = new ItemRequirement("Jute seeds", ItemID.JUTE_SEED).showConditioned(notPlantJute);
        rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPlantJute);
        seedDibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notPlantJute);
        batteredKey = new ItemRequirement("Battered key", ItemID.BATTERED_KEY).showConditioned(notKillEle);
        batteredKey.setTooltip("You can get another by searching the bookcase in the house south of the Elemental " +
			"Workshop, then reading the book you get from it");

        combatGear = new ItemRequirement("Combat gear to defeat all types of elementals (level 35)", -1, -1).showConditioned(notKillEle);
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

        inWorkshop = new ZoneRequirement(workshop);

        setupGenericRequirements();
    }

    public void setupGenericRequirements()
	{
		eleWorkI = new Conditions(LogicType.OR,
			new VarplayerRequirement(299, true, 15),
			new QuestRequirement(QuestHelperQuest.ELEMENTAL_WORKSHOP_I, QuestState.FINISHED)
		);
		((Conditions) eleWorkI).setText("Partial completion of Elemental Workshop I");
	}

    public void loadZones()
    {
        workshop = new Zone(new WorldPoint(2682, 9862, 0), new WorldPoint(2747, 9927, 0));
    }

    public void setupSteps()
    {
        catchMackerel = new NpcStep(this, NpcID.FISHING_SPOT_1518, new WorldPoint(2841, 3432, 0),
                "Fish on Catherby beach at the Big Net fishing spots for a mackerel.", true, bigFishingNet);
        catchMackerel.addAlternateNpcs(NpcID.FISHING_SPOT_1520);
        buyCandle = new NpcStep(this, NpcID.CANDLE_MAKER, new WorldPoint(2799, 3439, 0),
                "Buy a candle from the candle maker in Catherby.", coins.quantity(3));

		fillFishbowl = new ObjectStep(this, ObjectID.SINK_874, new WorldPoint(2830, 3441, 0),
			"Fill an empty fish bowl at a sink.", emptyFishbowl.highlighted(), seaweed, coins.quantity(10));
		fillFishbowl.addIcon(ItemID.EMPTY_FISHBOWL);
        petFish = new NpcStep(this, NpcID.HARRY, new WorldPoint(2833, 3443, 0),
                "Speak with Harry in the Catherby Fishing Shop to get a tiny net.", fishBowlSeaweed, coins.quantity(10));
        petFish.addDialogSteps("Can I get a fish for this bowl?", "I'll take it!");
        petFishMix = new ItemStep(this, "Put seaweed into the fishbowl.", fishBowl.highlighted(), seaweed.highlighted());
        petFishFish = new ObjectStep(this, ObjectID.AQUARIUM, new WorldPoint(2831, 3445, 0),
                "Fish in the aquarium near Harry.", tinyNet);
        collectFlax = new ObjectStep(this, ObjectID.FLAX, new WorldPoint(2742, 3446, 0),
                "Pick 5 flax at the flax field west of Catherby.");
        talkSherlock = new NpcStep(this, NpcID.SHERLOCK, new WorldPoint(2735, 3413, 0),
                "Speak with Sherlock west of Catherby.");
        moveToWorkshop = new ObjectStep(this, ObjectID.STAIRCASE_3415, new WorldPoint(2711, 3498, 0),
                "Enter the Elemental Workshop in Seers' Village.", batteredKey, combatGear, food);
        killEle = new NpcStep(this, NpcID.FIRE_ELEMENTAL, new WorldPoint(2719, 9889, 0),
                "Kill one of each of the 4 elementals.", combatGear, food);
        killEle.addAlternateNpcs(NpcID.WATER_ELEMENTAL, NpcID.AIR_ELEMENTAL, NpcID.EARTH_ELEMENTAL);
        buyStew = new NpcStep(this, NpcID.BARTENDER_1318, new WorldPoint(2691, 3494, 0),
                "Talk with the bartender in Seers' Village and buy a stew.", coins.quantity(20));
        buyStew.addDialogSteps("What do you have?", "Could I have some stew please?");
        playOrgan = new ObjectStep(this, ObjectID.CHURCH_ORGAN_25818, new WorldPoint(2692, 3463, 0),
                "Play the organ in Seers' Village Church.");
        plantJute = new ObjectStep(this, NullObjectID.NULL_8176, new WorldPoint(2669, 3523, 0),
                "Plant 3 jute seeds in the hops patch north west of Seers' Village.", juteSeed.quantity(3),
			seedDibber, rake);
        plantJute.addIcon(ItemID.JUTE_SEED);
        cupTea = new NpcStep(this, NpcID.GALAHAD, new WorldPoint(2612, 3474, 0),
                "Talk with Galahad west of McGrubor's Wood until he gives you some tea.");
        cupTea.addDialogStep("Do you get lonely here on your own?");
        logShortcut = new ObjectStep(this, ObjectID.LOG_BALANCE_23274, new WorldPoint(2602, 3477, 0),
                "Cross the log shortcut near to Galahad.");

        claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
                "Talk to the 'Wedge' in front of Camelot Castle to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(coins.quantity(33), bigFishingNet, juteSeed.quantity(3),
			seedDibber, rake, batteredKey, genericFishbowl, seaweed, combatGear);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Collections.singletonList(food);
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("4 level 35 elementals");
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
		setupGenericRequirements();
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skill.AGILITY, 20, true));
        req.add(new SkillRequirement(Skill.FARMING, 13, true));
        req.add(new SkillRequirement(Skill.FISHING, 16, true));

		req.add(eleWorkI);

        return req;
    }

    @Override
    public List<ItemReward> getItemRewards()
    {
        return Arrays.asList(
                new ItemReward("Kandarin headgear (1)", ItemID.KANDARIN_HEADGEAR_1, 1),
                new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP, 1));
    }

    @Override
    public List<UnlockReward> getUnlockRewards()
    {
        return Arrays.asList(
                new UnlockReward("Coal trucks can hold up to 140 coal."),
                new UnlockReward("The Flax keeper will exchange 30 noted flax for 30 noted bow strings daily"),
                new UnlockReward("5% more marks of grace on Seers' Village Rooftop Course"));
    }

    @Override
    public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails catchMackSteps = new PanelDetails("Catch a Mackerel", Collections.singletonList(catchMackerel),
			new SkillRequirement(Skill.FISHING, 16, true), bigFishingNet);
		catchMackSteps.setDisplayCondition(notCatchMackerel);
		allSteps.add(catchMackSteps);

		PanelDetails getPetFishSteps = new PanelDetails("Get a Pet Fish", Arrays.asList(fillFishbowl, petFishMix,
			petFish, petFishFish), coins.quantity(10), genericFishbowl, seaweed);
		getPetFishSteps.setDisplayCondition(notPetFish);
		allSteps.add(getPetFishSteps);

		PanelDetails buyCandleSteps = new PanelDetails("Buy a Candle", Collections.singletonList(buyCandle),
			coins.quantity(3));
		buyCandleSteps.setDisplayCondition(notBuyCandle);
		allSteps.add(buyCandleSteps);

		PanelDetails collectFlaxSteps = new PanelDetails("Collect 5 Flax", Collections.singletonList(collectFlax));
		collectFlaxSteps.setDisplayCondition(notCollectFlax);
		allSteps.add(collectFlaxSteps);

		PanelDetails talkSherlockSteps = new PanelDetails("Talk to Sherlock", Collections.singletonList(talkSherlock));
		talkSherlockSteps.setDisplayCondition(notTalkSherlock);
		allSteps.add(talkSherlockSteps);

		PanelDetails killElesSteps = new PanelDetails("Defeat Elementals", Arrays.asList(moveToWorkshop, killEle),
			eleWorkI, batteredKey, combatGear, food);
		killElesSteps.setDisplayCondition(notKillEle);
		allSteps.add(killElesSteps);

		PanelDetails playOrganSteps = new PanelDetails("Play the Church Organ", Collections.singletonList(playOrgan));
		playOrganSteps.setDisplayCondition(notPlayOrgan);
		allSteps.add(playOrganSteps);

		PanelDetails buyStewSteps = new PanelDetails("Buy Stew", Collections.singletonList(buyStew),
			coins.quantity(20));
		buyStewSteps.setDisplayCondition(notBuyStew);
		allSteps.add(buyStewSteps);

		PanelDetails getTeaSteps = new PanelDetails("Cup of Tea with Galahad", Collections.singletonList(cupTea));
		getTeaSteps.setDisplayCondition(notCupTea);
		allSteps.add(getTeaSteps);

		PanelDetails takeShortcutSteps = new PanelDetails("Log Shortcut", Collections.singletonList(logShortcut),
			new SkillRequirement(Skill.AGILITY, 20, true));
		takeShortcutSteps.setDisplayCondition(notLogShortcut);
		allSteps.add(takeShortcutSteps);

		PanelDetails plantJuteSteps = new PanelDetails("Plant Jute", Collections.singletonList(plantJute),
			new SkillRequirement(Skill.FARMING, 13, true), juteSeed.quantity(3), seedDibber, rake);
		plantJuteSteps.setDisplayCondition(notPlantJute);
		allSteps.add(plantJuteSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
