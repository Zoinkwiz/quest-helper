/*
 * Copyright (c) 2021, Kerpackie <https://github.com/Kerpackie/>
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

package com.questhelper.achievementdiaries.falador;

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

import java.util.*;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
        quest = QuestHelperQuest.FALADOR_EASY
)
public class FaladorEasy extends ComplexStateQuestHelper
{

    //Items Required
    ItemRequirement coins2000, bucket, tiara, mindTalisman, hammer, pickaxe, combatGear;

    //Items Recommended
    ItemRequirement teleportFalador, teleportMindAltar, explorersRing;

    ItemRequirement bluriteBar, bluriteOre;

    Requirement notFilledWater, notKilledDuck, notClimbedWall, notGotHaircut, notMotherloadMine,
            notEntrana, notMindTiara, notBluriteLimbs, notGotSecurityBook, notSarahFarmingShop,
            notFamilyCrest, hasBluriteOre, hasBluriteBar;

    QuestStep claimReward, fillWater, climbWall, getHaircut, enterDwarvenMines, enterCaveToMotherlodeMine,
            fixMotherloadMine, goEntrana, enterMindAltar, getMindTiara, getPickaxe, enterDungeon,
            mineBlurite, smeltBlurite, smithBluriteLimbs, climbLadderPortSarimJail, getSecurityBook,
            browseSarahFarmingShop, climbLadderWhiteKnightCastle, discoverFamilyCrest;

    NpcStep killDuck;

    Zone mindAltar, bluriteDungeon, faladorCastle1, portSarimJail1, motherlodeMine, dwarvenMine;

    ZoneRequirement inMindAltar, inBluriteDungeon, inFaladorCastle1, inPortSarimJail1,
            inMotherlodeMine, inDwarvenMine;


    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doEasy = new ConditionalStep(this, claimReward);
        doEasy.addStep(notFilledWater, fillWater);
        doEasy.addStep(notGotHaircut, getHaircut);
        doEasy.addStep(notClimbedWall, climbWall);
        doEasy.addStep(notKilledDuck, killDuck);
        doEasy.addStep(new Conditions(notMotherloadMine, inMotherlodeMine), fixMotherloadMine);
        doEasy.addStep(new Conditions(notMotherloadMine, inDwarvenMine), enterCaveToMotherlodeMine);
        doEasy.addStep(notMotherloadMine, enterDwarvenMines);
        doEasy.addStep(notSarahFarmingShop, browseSarahFarmingShop);
        doEasy.addStep(notEntrana, goEntrana);
        doEasy.addStep(new Conditions(notGotSecurityBook, inPortSarimJail1), getSecurityBook);
        doEasy.addStep(notGotSecurityBook, climbLadderPortSarimJail);
        doEasy.addStep(new Conditions(notBluriteLimbs, hasBluriteBar), smithBluriteLimbs);
        doEasy.addStep(new Conditions(notBluriteLimbs, hasBluriteOre), smeltBlurite);
        doEasy.addStep(new Conditions(notBluriteLimbs, pickaxe, inBluriteDungeon), mineBlurite);
        doEasy.addStep(new Conditions(notBluriteLimbs, pickaxe), enterDungeon);
        doEasy.addStep(notBluriteLimbs, getPickaxe);
        doEasy.addStep(new Conditions(notMindTiara, inMindAltar), getMindTiara);
        doEasy.addStep(notMindTiara, enterMindAltar);
        doEasy.addStep(new Conditions(notFamilyCrest, inFaladorCastle1), discoverFamilyCrest);
        doEasy.addStep(notFamilyCrest, climbLadderWhiteKnightCastle);

        return doEasy;
    }

    public void setupRequirements()
    {
        //Required
        coins2000 = new ItemRequirement("Coins", ItemID.COINS_995, 2000).showConditioned(notGotHaircut);
        bucket = new ItemRequirement("A Bucket", ItemID.BUCKET).showConditioned(notFilledWater);
        tiara = new ItemRequirement("A Silver Tiara", ItemID.TIARA).showConditioned(notMindTiara);
        mindTalisman = new ItemRequirement("A Mind Talisman", ItemID.MIND_TALISMAN).showConditioned(notMindTiara);
        hammer = new ItemRequirement("A Hammer", ItemID.HAMMER).showConditioned(new Conditions(LogicType.OR, notMotherloadMine, notBluriteLimbs));
        pickaxe = new ItemRequirement("Any Pickaxe", ItemCollections.getPickaxes()).showConditioned(new Conditions(LogicType.OR, notMotherloadMine, notBluriteLimbs));
        combatGear = new ItemRequirement("A range or mage attack to kill a Duck (Level 1)", -1, -1).showConditioned(notKilledDuck);
        combatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());

        //Recommended
        teleportFalador = new ItemRequirement("Multiple teleports to Falador", ItemID.FALADOR_TELEPORT, -1);
        teleportMindAltar = new ItemRequirement("A Teleport to the Mind Altar", ItemID.MIND_ALTAR_TELEPORT);
        explorersRing = new ItemRequirement("Explorers Ring (2) or above.", ItemID.EXPLORERS_RING_2);
        explorersRing.addAlternates(ItemID.EXPLORERS_RING_3, ItemID.EXPLORERS_RING_4);

        bluriteOre = new ItemRequirement("Blurite Ore", ItemID.BLURITE_ORE);
        bluriteBar = new ItemRequirement("Blurite Bar", ItemID.BLURITE_BAR);

        hasBluriteOre = bluriteOre.alsoCheckBank(questBank);
        hasBluriteBar = bluriteBar.alsoCheckBank(questBank);
        
        notFamilyCrest = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 0));
        notClimbedWall = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true,1));
        notSarahFarmingShop = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 2));
        notGotHaircut = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 3));
        notFilledWater = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 4));
        notKilledDuck = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 5));
        notMindTiara = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 6));
        notEntrana = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 7));
        notMotherloadMine = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 8));
        notGotSecurityBook = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 9));
        notBluriteLimbs = new Conditions(LogicType.NOR, new VarplayerRequirement(1186, true, 10));

        inMindAltar = new ZoneRequirement(mindAltar);
        inBluriteDungeon = new ZoneRequirement(bluriteDungeon);
        inFaladorCastle1 = new ZoneRequirement(faladorCastle1);
        inPortSarimJail1 = new ZoneRequirement(portSarimJail1);
        inDwarvenMine = new ZoneRequirement(dwarvenMine);
        inMotherlodeMine = new ZoneRequirement(motherlodeMine);

    }
    public void loadZones()
    {
        mindAltar = new Zone(new WorldPoint(2805,4819,0), new WorldPoint(2760,4855,0));
        bluriteDungeon = new Zone(new WorldPoint(2979, 9538, 0), new WorldPoint(3069, 9602, 0));
        faladorCastle1 = new Zone(new WorldPoint(2954, 3328, 1), new WorldPoint(2997, 3353, 1));
        dwarvenMine = new Zone(new WorldPoint(3011, 9856, 0), new WorldPoint(3069, 9698,0));
        motherlodeMine = new Zone(new WorldPoint(3712, 5695, 0), new WorldPoint(3780, 5636, 0));
        portSarimJail1 = new Zone(new WorldPoint(3009, 3197, 1), new WorldPoint(3021, 3178, 1));
    }

    public void setupSteps()
    {
        //Fill a bucket of water from the pump north of Falador Park
        fillWater = new ObjectStep(this, ObjectID.WATERPUMP_24004, new WorldPoint(2947, 3382, 0), "Use the bucket on the Waterpump outside the Hairdressers in west Falador.", bucket.highlighted());
        fillWater.addIcon(ItemID.BUCKET);

        //Get a Haircut from the Falador hairdresser
        getHaircut = new NpcStep(this, NpcID.HAIRDRESSER, new WorldPoint(2945, 3380, 0), "Visit the hairdresser in west Falador for a well deserved shave.", coins2000);
        getHaircut.addDialogStep("Go Ahead.");

        //Climb over the Western Falador Wall
        climbWall = new ObjectStep(this, ObjectID.CRUMBLING_WALL_24222, new WorldPoint(2935, 3355, 0), "Climb over the Crumbled Wall west of the West Falador Bank.");

        //Kill a duck in Falador Park
        killDuck = new NpcStep(this, NpcID.DUCK_1839, new WorldPoint(2989, 3379, 0), "Kill a Duck (Level 1) in the Falador Park", true, combatGear);
        killDuck.addAlternateNpcs(NpcID.DUCK);

        //Repair a broken strut in the Motherlode Mine
        enterDwarvenMines = new ObjectStep(this, ObjectID.STAIRCASE_16664, new WorldPoint(3058, 3376, 0), "Go to the Motherlode Mine.", pickaxe);
        enterCaveToMotherlodeMine = new ObjectStep(this, ObjectID.CAVE_26654, new WorldPoint(3059, 9764, 0), "Go to the Motherlode Mine.", pickaxe);
        fixMotherloadMine = new ObjectStep(this, ObjectID.BROKEN_STRUT, new WorldPoint(3743,5662,0), "Repair a broken strut on the Waterwheel in the Motherlode mine. It may take a few minnutes for it to break.", hammer.highlighted());
        fixMotherloadMine.addSubSteps(enterDwarvenMines, enterCaveToMotherlodeMine);
        fixMotherloadMine.addIcon(ItemID.HAMMER);

        //Find out what your family crest is from Sir Renitee
        climbLadderWhiteKnightCastle = new ObjectStep(this, ObjectID.LADDER_24070, new WorldPoint(2994, 3341, 0), "Climb the ladder in The White Knights Castle!");
        discoverFamilyCrest = new NpcStep(this, NpcID.SIR_RENITEE, new WorldPoint(2982, 3341, 1), "Speak to Sir Renitee to find out your family crest.");
        discoverFamilyCrest.addDialogStep("I don't know, what can you do for me?");
        discoverFamilyCrest.addDialogStep("Can you see if I have a family crest?");
        discoverFamilyCrest.addDialogStep("Thanks!");

        //Browse Sarah's Farm Shop
        browseSarahFarmingShop = new NpcStep(this, NpcID.SARAH, new WorldPoint(3039, 3292, 0), "Trade Sarah in the Farming Shop south of Falador and browse her goods.");

        //Take the boat to Entrana
        goEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA, new WorldPoint(3046, 3235, 0), "Deposit all your combat gear and talk to the Monk of Entrana in Port Sarim to take the boat.");
        goEntrana.addDialogStep("Yes, okay, I'm ready to go.");

        //Claim a security book from the Security Guard at Port Sarim Jaiil.
        climbLadderPortSarimJail = new ObjectStep(this, ObjectID.LADDER_9558, new WorldPoint(3010, 3184, 0), "Climb in the Port Sarim Jail to speak to the Security Guard.");
        getSecurityBook = new NpcStep(this, NpcID.SECURITY_GUARD, new WorldPoint(3013, 3192, 1), "Speak to the Security Guard in the Port Sarim Jail to get a Security Book.");
        getSecurityBook.addSubSteps(climbLadderPortSarimJail);

        //Smith some Blurite Limbs on Doric's Anvil
        getPickaxe = new DetailedQuestStep(this, new WorldPoint(2963, 3216, 0), "Get a pickaxe  in Rimmington if you do not have one.");
        enterDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_1738, new WorldPoint(3008, 3150, 0), "Go down the ladder south of Port Sarim. Be prepared for ice giants and ice warriors to attack you.", pickaxe, hammer);
        mineBlurite = new ObjectStep(this, ObjectID.ROCKS_11378, new WorldPoint(3049, 9566, 0), "Mine a blurite ore in the eastern cavern.", pickaxe, hammer);
        smeltBlurite = new ObjectStep(this, ObjectID.FURNACE_24009, new WorldPoint(2976, 3368, 0), "Smelt the blurite ore into a blurite bar.", hammer, bluriteOre);
        smithBluriteLimbs = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(2950, 3451, 0), "Smith the blurite bar into blurite limbs on Dorics Anvil, north of Falador.", hammer, bluriteBar);
        smithBluriteLimbs.addSubSteps(enterDungeon, mineBlurite, smeltBlurite);

        //Make a Mind Tiara
        enterMindAltar = new ObjectStep(this, ObjectID.MYSTERIOUS_RUINS_29094, new WorldPoint(2981, 3513, 0), "Use the mind talisman on the Mysterious Ruins to access the Mind Altar.", mindTalisman, tiara);
        getMindTiara = new ObjectStep(this, ObjectID.ALTAR_34761, new WorldPoint(2785, 4840, 0), "Use the mind talisman on the Altar", mindTalisman, tiara);
        enterMindAltar.addSubSteps(getMindTiara);

        //Claim Reward
        claimReward = new NpcStep(this, NpcID.SIR_REBRAL, new WorldPoint(2977, 3346, 0), "Congratulations! Talk to Sir Rebral in the courtyard of The White Knight Castle to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(coins2000, pickaxe, hammer, bucket, tiara, mindTalisman, combatGear);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(teleportFalador, teleportMindAltar, explorersRing);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skill.CONSTRUCTION, 16));
        req.add(new SkillRequirement(Skill.AGILITY, 5, true));
        req.add(new SkillRequirement(Skill.MINING, 10, true));
        req.add(new SkillRequirement(Skill.SMITHING, 13, true));

        req.add(new QuestRequirement(QuestHelperQuest.DORICS_QUEST, QuestState.FINISHED));
        req.add(new QuestRequirement(QuestHelperQuest.THE_KNIGHTS_SWORD, QuestState.FINISHED));
        req.add(new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED));

        return req;
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Fill Water Bucket:", Collections.singletonList(fillWater), bucket));
        allSteps.add(new PanelDetails("Get A Haircut", Collections.singletonList(getHaircut), coins2000));
        allSteps.add(new PanelDetails("Climb West Wall", Collections.singletonList(climbWall)));
        allSteps.add(new PanelDetails("Kill The Duck", Collections.singletonList(killDuck), combatGear));
        allSteps.add(new PanelDetails("Motherlode Repairs", Arrays.asList(enterCaveToMotherlodeMine, fixMotherloadMine), pickaxe, hammer));
        allSteps.add(new PanelDetails("Sarah's Farming Shop", Collections.singletonList(browseSarahFarmingShop)));
        allSteps.add(new PanelDetails("To the Holy Land!", Collections.singletonList(goEntrana)));
        allSteps.add(new PanelDetails("Get A Security Book", Arrays.asList(climbLadderPortSarimJail, getSecurityBook)));
        allSteps.add(new PanelDetails("Blurite Limbs", Arrays.asList(getPickaxe, enterDungeon, mineBlurite, smeltBlurite, smithBluriteLimbs)));
        allSteps.add(new PanelDetails("Mind Tiara", Arrays.asList(enterMindAltar, getMindTiara)));
        allSteps.add(new PanelDetails("Family Crest", Arrays.asList(climbLadderWhiteKnightCastle, discoverFamilyCrest)));
        allSteps.add(new PanelDetails("Congratulations!", Collections.singletonList(claimReward)));

        return allSteps;

    }

}
