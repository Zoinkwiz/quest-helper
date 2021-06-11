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
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.*;
import com.questhelper.requirements.util.Spellbook;
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
        quest = QuestHelperQuest.KANDARIN_MEDIUM
)

public class KandarinMedium extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement combatGear, mithGrap, crossbow, unicornHorn, mortarPest, vialOfWater, dustyKey, bigFishingNet, lawRune, airRune, mapleUnstrung, bowString, limpSeed,  rake, seedDib, primedMind, hammer, beatenBook, batteredKey, rope, lockpick, staff, pickaxe, iritLeaf;

    // Items recommended
    ItemRequirement food, compost;

    // unlisted item reqs
    ItemRequirement rawBass, unfIrit, hornDust;

    Requirement notBarbAgi, notSuperAnti, notEnterRange, notGrapOb, notCatchCookBass, notTPCam, notStringMaple, notPickLimp, notMindHelm, notFireGiant, notBarbAss, notStealHemen, notTravelMcGrubor, notMineCoal, limpReady, limpGrowing, notLimpGrowing;

    Requirement normalBook;

    // Quest steps
    Requirement alfredBar, eleWorkII, waterfallQuest, fairyTaleI, fairyTaleII;

    QuestStep barbAgi, superAnti, enterRange, grapOb, cookBass, tpCAM, stringMaple, pickLimp, mindHelm, barbAss, stealHemen, travelMcGrubor, mineCoal, claimReward, moveToTavDungeon, moveToBank, moveToSeersCath, moveToOb, moveToWorkshop, moveToWaterfall, mixUnf, crushHorn, plantLimp, waitLimp;

    NpcStep catchBass, fireGiant;

    Zone bank, seersCath, tavDungeon, workshop, obIsland, waterfall;

    ZoneRequirement inBank, inSeersCath, inTavDungeon, inWorkshop, inObIsland, inWaterfall;

    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doMedium = new ConditionalStep(this, claimReward);

        doMedium.addStep(new Conditions(notGrapOb, inObIsland), grapOb);
        doMedium.addStep(new Conditions(notGrapOb, inTavDungeon), moveToOb);
        doMedium.addStep(notGrapOb, moveToTavDungeon);
        doMedium.addStep(new Conditions(notPickLimp, notLimpGrowing), plantLimp);
        doMedium.addStep(new Conditions(notSuperAnti, inSeersCath, unfIrit, hornDust), superAnti);
        doMedium.addStep(new Conditions(notSuperAnti, inSeersCath, unfIrit), crushHorn);
        doMedium.addStep(new Conditions(notSuperAnti, inSeersCath), mixUnf);
        doMedium.addStep(notSuperAnti, moveToSeersCath);
        doMedium.addStep(new Conditions(notCatchCookBass, rawBass), cookBass);
        doMedium.addStep(notCatchCookBass, catchBass);
        doMedium.addStep(new Conditions(notStringMaple, inBank), stringMaple);
        doMedium.addStep(notStringMaple, moveToBank);
        doMedium.addStep(new Conditions(notPickLimp, limpReady), pickLimp);
        doMedium.addStep(new Conditions(notPickLimp, limpGrowing), waitLimp);
        doMedium.addStep(notEnterRange, enterRange);
        doMedium.addStep(notStealHemen, stealHemen);
        doMedium.addStep(notMineCoal, mineCoal);
        doMedium.addStep(new Conditions(notFireGiant, inWaterfall), fireGiant);
        doMedium.addStep(notFireGiant, moveToWaterfall);
        doMedium.addStep(notBarbAgi, barbAgi);
        doMedium.addStep(notBarbAss, barbAss);
        doMedium.addStep(notTravelMcGrubor, travelMcGrubor);
        doMedium.addStep(notTPCam, tpCAM);

        return doMedium;
    }

    public void setupRequirements()
    {
        notBarbAgi = new VarplayerRequirement(1178, false, 12);
        notSuperAnti = new VarplayerRequirement(1178, false, 13);
        notEnterRange = new VarplayerRequirement(1178, false, 14);
        notGrapOb = new VarplayerRequirement(1178, false, 15);
        notCatchCookBass = new VarplayerRequirement(1178, false, 16);
        notTPCam = new VarplayerRequirement(1178, false, 17);
        notStringMaple = new VarplayerRequirement(1178, false, 18);
        notPickLimp = new VarplayerRequirement(1178, true, 19);
        notMindHelm = new VarplayerRequirement(1178, false, 20);
        notFireGiant = new VarplayerRequirement(1178, false, 21);
        notBarbAss = new VarplayerRequirement(1178, false, 22);
        notStealHemen = new VarplayerRequirement(1178, false, 23);
        notTravelMcGrubor = new VarplayerRequirement(1178, false, 24);
        notMineCoal = new VarplayerRequirement(1178, false, 25);

        limpReady = new VarbitRequirement(4957, 0);
        limpGrowing = new VarbitRequirement(4957, 4, Operation.GREATER_EQUAL);
        notLimpGrowing = new VarbitRequirement(4957, 3, Operation.LESS_EQUAL);

        mithGrap = new ItemRequirement("Mithril grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notGrapOb);
        crossbow = new ItemRequirement("Any crossbow", ItemCollections.getCrossbows()).showConditioned(notGrapOb);
        unfIrit = new ItemRequirement("Unfinished Irit potion", ItemID.IRIT_POTION_UNF, 1).showConditioned(notSuperAnti);
        unicornHorn = new ItemRequirement("Unicorn horn", ItemID.UNICORN_HORN, 1).showConditioned(notSuperAnti);
        mortarPest = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).showConditioned(notSuperAnti);
        hornDust = new ItemRequirement("Horn Dust", ItemID.UNICORN_HORN_DUST, 1).showConditioned(notSuperAnti);
        vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER, 1).showConditioned(notSuperAnti);
        iritLeaf = new ItemRequirement("Irit leaf", ItemID.IRIT_LEAF, 1).showConditioned(notSuperAnti);
        dustyKey = new ItemRequirement("Dusty key", ItemID.DUSTY_KEY).showConditioned(notGrapOb);
        bigFishingNet = new ItemRequirement("Big fishing net", ItemID.BIG_FISHING_NET).showConditioned(notCatchCookBass);
        lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE, 1).showConditioned(notTPCam);
        airRune = new ItemRequirement("Air Rune", ItemID.AIR_RUNE, 5).showConditioned(notTPCam);
        mapleUnstrung = new ItemRequirement("Unstring Maple shortbow", ItemID.MAPLE_SHORTBOW_U).showConditioned(notStringMaple);
        bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notStringMaple);
        limpSeed = new ItemRequirement("Limpwurt seed", ItemID.LIMPWURT_SEED).showConditioned(notPickLimp);
        rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickLimp);
        seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notPickLimp);
        primedMind = new ItemRequirement("Mind bar", ItemID.PRIMED_MIND_BAR).showConditioned(notMindHelm);
        hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notMindHelm);
        beatenBook = new ItemRequirement("Beaten Book", ItemID.BEATEN_BOOK).showConditioned(notMindHelm);
        batteredKey = new ItemRequirement("Battered Key", ItemID.BATTERED_KEY).showConditioned(notMindHelm);
        rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notFireGiant);
        lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notStealHemen);
        staff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.getFairyStaff()).showConditioned(notTravelMcGrubor);
        pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notMineCoal);
        rawBass = new ItemRequirement("Raw bass", ItemID.RAW_BASS).showConditioned(notCatchCookBass);
        compost = new ItemRequirement("Compost", ItemCollections.getCompost()).showConditioned(notPickLimp);

        combatGear = new ItemRequirement("Combat gear to kill a fire giant and complete a wave of Barbarian Assault", -1, -1).showConditioned(new Conditions(LogicType.OR, notFireGiant, notBarbAss));
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

        normalBook = new SpellbookRequirement(Spellbook.NORMAL);

        alfredBar = new QuestRequirement(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL, QuestState.FINISHED);
        eleWorkII = new QuestRequirement(QuestHelperQuest.ELEMENTAL_WORKSHOP_II, QuestState.FINISHED);
        waterfallQuest = new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED);
        fairyTaleI = new QuestRequirement(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS, QuestState.FINISHED);
        fairyTaleII = new QuestRequirement(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.FINISHED, "Partial completion up until you can use fairy rings");

        inBank = new ZoneRequirement(bank);
        inSeersCath = new ZoneRequirement(seersCath);
        inTavDungeon = new ZoneRequirement(tavDungeon);
        inWorkshop = new ZoneRequirement(workshop);
        inObIsland = new ZoneRequirement(obIsland);
        inWaterfall = new ZoneRequirement(waterfall);
    }

    public void loadZones()
    {
        bank = new Zone(new WorldPoint(2721, 3495, 0), new WorldPoint(2730, 3490, 0));
        seersCath = new Zone(new WorldPoint(2687, 3510, 0), new WorldPoint(2839, 3430, 0));
        tavDungeon = new Zone(new WorldPoint(2813, 9857, 0), new WorldPoint(2972, 9669, 0));
        obIsland = new Zone(new WorldPoint(2833, 3427, 0), new WorldPoint(2849, 3415, 0));
        waterfall = new Zone(new WorldPoint(2534, 9918, 0), new WorldPoint(2613, 9860, 0));
    }

    public void setupSteps()
    {
        barbAgi = new ObjectStep(this, ObjectID.OBSTACLE_PIPE_20210, new WorldPoint(2552, 3560, 0),
                "Complete a lap of the Barbarian Outpost agility course.");
        barbAss = new ObjectStep(this, ObjectID.LADDER_20226, new WorldPoint(2534, 3572, 0),
                "Complete a wave of Barbarian Assault. If it's your first time here, speak with Captain Cain.");
        enterRange = new ObjectStep(this, ObjectID.GUILD_DOOR, new WorldPoint(2658, 3438, 0),
                "Enter the range guild.");
        stealHemen = new ObjectStep(this, ObjectID.CHEST_11742, new WorldPoint(2639, 3424, 0),
                "Steal from the chest.", lockpick);
        mineCoal = new ObjectStep(this, ObjectID.ROCKS_11366, new WorldPoint(2590, 3476, 0),
                "Mine coal.", pickaxe);
        mineCoal.addIcon(ItemID.RUNE_PICKAXE);
        moveToWaterfall = new ObjectStep(this, ObjectID.DOOR_2010, new WorldPoint(2511, 3464, 0),
                "Enter the waterfall dungeon.", rope);
        fireGiant = new NpcStep(this, NpcID.FIRE_GIANT_2080, new WorldPoint(2566, 9887, 0),
                "Kill a Fire giant.", true);
        fireGiant.addAlternateNpcs(NpcID.FIRE_GIANT_2079);
        fireGiant.addAlternateNpcs(NpcID.FIRE_GIANT_2078);
        moveToWorkshop = new ObjectStep(this, ObjectID.STAIRCASE_3415, new WorldPoint(2711, 3498, 0),
                "With battered key in inventory, go through the wall then go down the staircase.", batteredKey);
        mindHelm = new ObjectStep(this, 123, new WorldPoint(2719, 9889, 0),
                "Make a mind helm.", primedMind);
        moveToBank = new ObjectStep(this, 123, new WorldPoint(2725, 3492, 0),
                "Move into the bank before stringing the bow.");
        stringMaple = new ItemStep(this, "String the maple bow.", mapleUnstrung.highlighted(), bowString.highlighted());
        moveToTavDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
                "Enter the Taverly Dungeon.");
        moveToOb = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2842, 9824,0),
                "Climb the ladder.");
        grapOb = new ObjectStep(this, ObjectID.CROSSBOW_TREE_17062, new WorldPoint(2842, 3435, 0),
                "Grapple across!", mithGrap.equipped(), crossbow.equipped());
        moveToSeersCath = new ObjectStep(this, 123, new WorldPoint(2755, 3475, 0),
                "Move to the Seers' Village / Catherby area.");
        mixUnf = new ItemStep(this,
                "Create an unfinished Irit potion.", iritLeaf.highlighted(), vialOfWater.highlighted());
        crushHorn = new ItemStep(this,
                "Crush the unicorn horn.", unicornHorn.highlighted(), mortarPest.highlighted());
        superAnti = new ItemStep(this,
                "Create superantipoision.", hornDust.highlighted(), unfIrit.highlighted());
        plantLimp = new ObjectStep(this, 7848, new WorldPoint(2810, 3464, 0),
                "Rake the patch and plant the Limpwurt seed, don't forget to compost!");
        plantLimp.addIcon(ItemID.LIMPWURT_SEED);
        waitLimp = new DetailedQuestStep(this, "Wait for the Limpwurt to grow.");
        pickLimp = new ObjectStep(this, ObjectID.LIMPWURT_PLANT_7855, new WorldPoint(2809, 3463, 0),
                "Pick the limpwurt.");
        catchBass = new NpcStep(this, NpcID.FISHING_SPOT_1518, new WorldPoint(2837, 3431, 0),
                "Catch a bass.");
        catchBass.addAlternateNpcs(NpcID.FISHING_SPOT_1520);
        cookBass = new ObjectStep(this, ObjectID.RANGE_26181, new WorldPoint(2818, 3444, 0),
                "Cook the bass.", rawBass);
        travelMcGrubor = new DetailedQuestStep(this, "Take any fairy ring to McGrubor's Woods (ALS)", staff.equipped());
        tpCAM = new DetailedQuestStep(this, "Teleport to Camelot", lawRune.quantity(1), airRune.quantity(5), normalBook);

        claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
                "Talk to the 'Wedge' infront of camelot castle to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(mithGrap, crossbow, dustyKey, bigFishingNet, unicornHorn, mortarPest, vialOfWater, iritLeaf, lockpick, lawRune.quantity(1), airRune.quantity(5), limpSeed, seedDib, compost, rake, primedMind, batteredKey, beatenBook, hammer, staff, combatGear);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(food);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skill.AGILITY, 36));
        req.add(new SkillRequirement(Skill.COOKING, 43));
        req.add(new SkillRequirement(Skill.FARMING, 26));
        req.add(new SkillRequirement(Skill.FISHING, 46));
        req.add(new SkillRequirement(Skill.FLETCHING, 50));
        req.add(new SkillRequirement(Skill.HERBLORE, 48));
        req.add(new SkillRequirement(Skill.MAGIC, 45));
        req.add(new SkillRequirement(Skill.MINING, 30));
        req.add(new SkillRequirement(Skill.RANGED, 40));
        req.add(new SkillRequirement(Skill.STRENGTH, 22));
        req.add(new SkillRequirement(Skill.THIEVING, 47));
        return req;
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("Fire giant (Level 86)");
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();

        allSteps.add(new PanelDetails("Grapple from Water Obelisk", Arrays.asList(moveToTavDungeon, moveToOb, grapOb), mithGrap, crossbow, dustyKey));
        allSteps.add(new PanelDetails("Pick Limpwurt", Arrays.asList(plantLimp, waitLimp, pickLimp), limpSeed, seedDib, compost, rake));
        allSteps.add(new PanelDetails("String Maple Shortbow", Arrays.asList(moveToBank, stringMaple), mapleUnstrung, bowString));
        allSteps.add(new PanelDetails("Catch and Cook Bass", Arrays.asList(catchBass, cookBass), bigFishingNet));
        allSteps.add(new PanelDetails("Mix Superantipoison", Arrays.asList(moveToSeersCath, mixUnf, crushHorn, superAnti), unicornHorn, mortarPest, vialOfWater, iritLeaf));
        allSteps.add(new PanelDetails("Make mind helm", Arrays.asList(mindHelm), eleWorkII, primedMind, batteredKey, beatenBook, hammer));
        allSteps.add(new PanelDetails("Enter Range Guild", Arrays.asList(enterRange)));
        allSteps.add(new PanelDetails("Steal from Chest", Arrays.asList(stealHemen), lockpick));
        allSteps.add(new PanelDetails("Mine Coal", Arrays.asList(mineCoal)));
        allSteps.add(new PanelDetails("Kill Fire Giant", Arrays.asList(moveToWaterfall, fireGiant), waterfallQuest, combatGear, food));
        allSteps.add(new PanelDetails("Barbarian Agility Course", Arrays.asList(barbAgi), alfredBar));
        allSteps.add(new PanelDetails("Barbarian Assault Wave", Arrays.asList(barbAss)));
        allSteps.add(new PanelDetails("Fairy Ring to McGrubor's Woods", Arrays.asList(travelMcGrubor), fairyTaleII, staff));
        allSteps.add(new PanelDetails("Teleport to Camelot", Arrays.asList(tpCAM), lawRune.quantity(1), airRune.quantity(5), normalBook));
        allSteps.add(new PanelDetails("Finishing off", Arrays.asList(claimReward)));

        return allSteps;
    }
}
