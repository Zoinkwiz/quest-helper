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
package com.questhelper.achievementdiaries.varrock;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
        quest = QuestHelperQuest.VARROCK_HARD
)
public class VarrockHard extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement combatGear;

    ItemRequirement botSceptre, topSceptre, rightSkull, leftSkull, strangeSkull, runedSceptre, combinedSkullSceptre;

    // Items recommended
    ItemRequirement food;

    // Quests required
    Requirement desertTreasure;

    Requirement notSpottyCape, not153Kudos, notWakkaEdge, notPaddewwaTP, notSkullSceptre, notYewChurch, notFancyStone, notYewRoots, notSmiteAltar;

    QuestStep claimReward, moveToStronghold, moveToStronghold2, moveToStronghold3, moveToStronghold4, makeSceptre, makeSkull, skullSceptre, makeSkullSceptre;

    NpcStep killMino, killFlesh, killCatablepon, killAnkou;

    Zone stronghold1, stronghold2, stronghold3, stronghold4;

    ZoneRequirement inStronghold1, inStronghold2, inStronghold3, inStronghold4;

    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doHard = new ConditionalStep(this, claimReward);
        doHard.addStep(new Conditions(notSkullSceptre, combinedSkullSceptre), skullSceptre);
        doHard.addStep(new Conditions(notSkullSceptre, runedSceptre, strangeSkull), makeSkullSceptre);
        doHard.addStep(new Conditions(notSkullSceptre, botSceptre, topSceptre), makeSceptre);
        doHard.addStep(new Conditions(notSkullSceptre, leftSkull, rightSkull), makeSkull);
        doHard.addStep(new Conditions(notSkullSceptre, inStronghold4), killAnkou);
        doHard.addStep(new Conditions(notSkullSceptre, rightSkull, inStronghold3), moveToStronghold4);
        doHard.addStep(new Conditions(notSkullSceptre, inStronghold3), killCatablepon);
        doHard.addStep(new Conditions(notSkullSceptre, botSceptre, inStronghold2), moveToStronghold3);
        doHard.addStep(new Conditions(notSkullSceptre, inStronghold2), killFlesh);
        doHard.addStep(new Conditions(notSkullSceptre, rightSkull, inStronghold1), moveToStronghold2);
        doHard.addStep(new Conditions(notSkullSceptre, inStronghold1), killMino);
        doHard.addStep(notSkullSceptre, moveToStronghold);

        return doHard;
    }

    public void setupRequirements()
    {
        notSpottyCape = new VarplayerRequirement(1176, false, 29);
        not153Kudos = new VarplayerRequirement(1176, false, 30);
        notWakkaEdge = new VarplayerRequirement(1176, false, 31);
        notPaddewwaTP = new VarplayerRequirement(1177, false, 0);
        notSkullSceptre = new VarplayerRequirement(1177, false, 1);
        notYewChurch = new VarplayerRequirement(1177, false, 2);
        notFancyStone = new VarplayerRequirement(1177, false, 3);
        notYewRoots = new VarplayerRequirement(1177, false, 4);
        notSmiteAltar = new VarplayerRequirement(1177, false, 5);

        botSceptre = new ItemRequirement("Bottom of sceptre", ItemID.BOTTOM_OF_SCEPTRE).showConditioned(notSkullSceptre);
        topSceptre = new ItemRequirement("Top of sceptre", ItemID.TOP_OF_SCEPTRE).showConditioned(notSkullSceptre);
        leftSkull = new ItemRequirement("Left skull half", ItemID.LEFT_SKULL_HALF).showConditioned(notSkullSceptre);
        rightSkull = new ItemRequirement("Right skull half", ItemID.RIGHT_SKULL_HALF).showConditioned(notSkullSceptre);
        strangeSkull = new ItemRequirement("Strange skull", ItemID.STRANGE_SKULL).showConditioned(notSkullSceptre);
        runedSceptre = new ItemRequirement("Runed sceptre", ItemID.RUNED_SCEPTRE).showConditioned(notSkullSceptre);
        combinedSkullSceptre = new ItemRequirement("Skull sceptre", ItemID.SKULL_SCEPTRE).showConditioned(notSkullSceptre);

        combatGear = new ItemRequirement("", -1, -1);
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

        inStronghold1 = new ZoneRequirement(stronghold1);
        inStronghold2 = new ZoneRequirement(stronghold2);
        inStronghold3 = new ZoneRequirement(stronghold3);
        inStronghold4 = new ZoneRequirement(stronghold4);

        desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
    }

    public void loadZones()
    {
        stronghold1 = new Zone(new WorldPoint(1854, 5248, 0), new WorldPoint(1918, 5183, 0));
        stronghold2 = new Zone(new WorldPoint(1983, 5248, 0), new WorldPoint(2049, 5183, 0));
        stronghold3 = new Zone(new WorldPoint(2113, 5313, 0), new WorldPoint(2177, 5246, 0));
        stronghold4 = new Zone(new WorldPoint(2302, 5250, 0), new WorldPoint(2369, 5183, 0));
    }

    public void setupSteps()
    {
        moveToStronghold = new ObjectStep(this, ObjectID.ENTRANCE_20790, new WorldPoint(3081, 3420, 0),
                "Enter the stronghold.");
        killMino = new NpcStep(this, NpcID.MINOTAUR, new WorldPoint(1888, 5220, 0),
                "Kill Minotaurs until you recieve a right skull half.", rightSkull);
        killMino.addAlternateNpcs(NpcID.MINOTAUR_2482);
        killMino.addAlternateNpcs(NpcID.MINOTAUR_2483);
        moveToStronghold2 = new ObjectStep(this, ObjectID.LADDER_20785, new WorldPoint(1902, 5222, 0),
                "Go to the 2nd floor of the stronghold.");
        killFlesh = new NpcStep(this, NpcID.FLESH_CRAWLER, new WorldPoint(2019, 5215, 0),
                "Kill Flesh crawlers until you recieve a bottom of sceptre.", botSceptre);
        killFlesh.addAlternateNpcs(NpcID.FLESH_CRAWLER_2499);
        killFlesh.addAlternateNpcs(NpcID.FLESH_CRAWLER_2500);
        moveToStronghold3 = new ObjectStep(this, ObjectID.LADDER_19004, new WorldPoint(2026, 5218, 0),
                "Go to the 3rd floor of the stronghold.");
        killCatablepon = new NpcStep(this, NpcID.CATABLEPON, new WorldPoint(2144, 5281, 0),
                "Kill Catablepons until you recieve a top of sceptre.", topSceptre);
        killCatablepon.addAlternateNpcs(NpcID.CATABLEPON_2475);
        killCatablepon.addAlternateNpcs(NpcID.CATABLEPON_2476);
        moveToStronghold4 = new ObjectStep(this, ObjectID.DRIPPING_VINE_23706, new WorldPoint(2148, 5284, 0),
                "Go to the 4th floor of the stronghold.");
        killAnkou = new NpcStep(this, NpcID.ANKOU, new WorldPoint(2344, 5213, 0),
                "Kill Ankous until you recieve a left skull half.", leftSkull);
        killAnkou.addAlternateNpcs(NpcID.ANKOU_2515);
        killAnkou.addAlternateNpcs(NpcID.ANKOU_2516);
        makeSkull = new ItemStep(this, "Use the skulls halfs together.", leftSkull.highlighted(), rightSkull.highlighted());
        makeSceptre = new ItemStep(this, "Use the sceptre pieces together.", botSceptre.highlighted(), topSceptre.highlighted());
        makeSkullSceptre  = new ItemStep(this, "Use the sceptre pieces together.", runedSceptre.highlighted(), strangeSkull.highlighted());
        skullSceptre = new DetailedQuestStep(this, "Use the sceptre to teleport to the stronghold.", combinedSkullSceptre.highlighted());

        claimReward = new NpcStep(this, NpcID.TOBY, new WorldPoint(3225, 3415, 0),
                "Talk to Toby in Varrock to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(combatGear);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(food);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        List<Requirement> reqs = new ArrayList<>();
        reqs.add(new SkillRequirement(Skill.AGILITY, 51));
        reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 50));
        reqs.add(new SkillRequirement(Skill.FARMING, 68));
        reqs.add(new SkillRequirement(Skill.FIREMAKING, 60));
        reqs.add(new SkillRequirement(Skill.HUNTER, 66));
        reqs.add(new SkillRequirement(Skill.MAGIC, 54));
        reqs.add(new SkillRequirement(Skill.PRAYER, 52));
        reqs.add(new SkillRequirement(Skill.RANGED, 40));
        reqs.add(new SkillRequirement(Skill.THIEVING, 53));
        reqs.add(new SkillRequirement(Skill.WOODCUTTING, 60));

        reqs.add(desertTreasure);

        return reqs;
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("Kill various monsters in the stronghold of security (levels 13-86)");
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Teleport with Skull Sceptre", Arrays.asList(moveToStronghold, killMino, moveToStronghold2, killFlesh, moveToStronghold3, killCatablepon, moveToStronghold4, killAnkou, makeSkull, makeSceptre, makeSkullSceptre, skullSceptre), combatGear, food));
        allSteps.add(new PanelDetails("Finishing off", Arrays.asList(claimReward)));

        return allSteps;
    }
}
