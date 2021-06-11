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

import jdk.internal.net.http.common.Log;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
        quest = QuestHelperQuest.KANDARIN_ELITE
)

public class KandarinElite extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement dwarfSeed, seedDib, spade, rake, compost, harpoon, cookingGaunt, stamPot, caviar, runiteBar, magicLogs, hammer, chewedBone, tinderbox, axe, lawRune, astralRune, waterRune, combatGear;

    // unlisted item reqs
    ItemRequirement rawShark;

    Requirement notbarb5, notPickDwarf, not5Shark, notStamMix, notRuneHasta, notPyre, notTPCath, notHeal, notAtk, notDef, notCol, notDwarfGrowing, dwarfReady, dwarfGrowing;

    //Quest steps
    Requirement barbSmith, barbFire, familyCrest, LunarDip;

    QuestStep claimReward, tpCath, plantDwarf, waitDwarf, pickDwarf, cook5Sharks, moveToSeersRooftop, stamMix, runeHasta, pyre, barb5, barb52, barb5Heal, barb5Atk, barb5Def, barb5Col;

    NpcStep catch5Sharks;

    Zone bankRoof, barbUnder;

    ZoneRequirement inBankRoof, inBarbUnder;

    Requirement lunarBook;

    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doElite = new ConditionalStep(this, claimReward);
        doElite.addStep(notTPCath, tpCath);
        doElite.addStep(new Conditions(notPickDwarf, notDwarfGrowing), plantDwarf);
        doElite.addStep(new Conditions(not5Shark, rawShark.quantity(5)), cook5Sharks);
        doElite.addStep(not5Shark, catch5Sharks);
        doElite.addStep(new Conditions(notPickDwarf, dwarfReady), pickDwarf);
        doElite.addStep(new Conditions(notPickDwarf, dwarfGrowing), waitDwarf);
        doElite.addStep(new Conditions(notStamMix, inBankRoof), stamMix);
        doElite.addStep(notStamMix, moveToSeersRooftop);
        doElite.addStep(notRuneHasta, runeHasta);
        doElite.addStep(notPyre, pyre);
        doElite.addStep(new Conditions(notbarb5, notHeal), barb5Heal);
        doElite.addStep(new Conditions(notbarb5, notAtk), barb5Atk);
        doElite.addStep(new Conditions(notbarb5, notDef), barb5Def);
        doElite.addStep(new Conditions(notbarb5, notCol), barb5Col);
        doElite.addStep(new Conditions(notbarb5, inBarbUnder), barb52);
        doElite.addStep(notbarb5, barb5);


        return doElite;
    }

    public void setupRequirements()
    {
        notbarb5 = new VarplayerRequirement(1179, false, 5);
        notPickDwarf = new VarplayerRequirement(1179, false, 6);
        not5Shark = new VarplayerRequirement(1179, false, 7);
        notStamMix = new VarplayerRequirement(1179, false, 8);
        notRuneHasta = new VarplayerRequirement(1179, false, 9);
        notPyre = new VarplayerRequirement(1179, false, 10);
        notTPCath = new VarplayerRequirement(1179, false, 11);

        // dwarf weed
        dwarfReady = new VarbitRequirement(4774, 100);
        dwarfGrowing = new VarbitRequirement(4774, 96, Operation.GREATER_EQUAL);
        notDwarfGrowing = new VarbitRequirement(4774, 6, Operation.LESS_EQUAL);

        // BA levels
        notHeal = new VarbitRequirement(3255, 4, Operation.LESS_EQUAL);
        notAtk = new VarbitRequirement(3251, 4, Operation.LESS_EQUAL);
        notDef = new VarbitRequirement(3252, 4, Operation.LESS_EQUAL);
        notCol = new VarbitRequirement(3254, 4, Operation.LESS_EQUAL);

        dwarfSeed = new ItemRequirement("Dwarf weed seed", ItemID.DWARF_WEED_SEED).showConditioned(notPickDwarf);
        seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notPickDwarf);
        spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPickDwarf);
        rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPickDwarf);
        compost = new ItemRequirement("Any compost", ItemCollections.getCompost()).showConditioned(notPickDwarf);
        harpoon = new ItemRequirement("Harpoon", ItemID.HARPOON).showConditioned(not5Shark);
        cookingGaunt = new ItemRequirement("Cooking gauntlets", ItemID.COOKING_GAUNTLETS).showConditioned(not5Shark);
        stamPot = new ItemRequirement("Stamina potion (2)", ItemID.STAMINA_POTION2).showConditioned(notStamMix);
        caviar = new ItemRequirement("Caviar", ItemID.CAVIAR).showConditioned(notStamMix);
        runiteBar = new ItemRequirement("Runite bar", ItemID.RUNITE_BAR).showConditioned(notRuneHasta);
        magicLogs = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS).showConditioned(new Conditions(LogicType.OR, notRuneHasta, notPyre));
        hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notRuneHasta);
        chewedBone = new ItemRequirement("Chewed bones", ItemID.CHEWED_BONES).showConditioned(notPyre);
        tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notPyre);
        axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notPyre);
        lawRune = new ItemRequirement("Law runes", ItemID.LAW_RUNE).showConditioned(notTPCath);
        astralRune = new ItemRequirement("Astral runes", ItemID.ASTRAL_RUNE).showConditioned(notTPCath);
        waterRune = new ItemRequirement("Water runes", ItemID.WATER_RUNE).showConditioned(notTPCath);
        rawShark = new ItemRequirement("Raw shark", ItemID.RAW_SHARK).showConditioned(not5Shark);

        combatGear = new ItemRequirement("Combat gear", -1, -1).showConditioned(notbarb5);
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

        lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

        // TODO find a way to track barb training
        // barbSmith = new QuestRequirement(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL, QuestState.FINISHED);
        // barbFire = new QuestRequirement(QuestHelperQuest.ALFRED_GRIMHANDS_BARCRAWL, QuestState.FINISHED);
        familyCrest = new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED);
        LunarDip = new QuestRequirement(QuestHelperQuest.LUNAR_DIPLOMACY, QuestState.FINISHED);

        inBankRoof = new ZoneRequirement(bankRoof);
        inBarbUnder = new ZoneRequirement(barbUnder);
    }

    public void loadZones()
    {
        bankRoof = new Zone(new WorldPoint(2721, 3495, 3), new WorldPoint(2730, 3490, 3));
        barbUnder = new Zone(new WorldPoint(2572, 53202, 0), new WorldPoint(2614, 5258, 0));
    }

    public void setupSteps()
    {
        tpCath = new DetailedQuestStep(this, "Teleport to Catherby.", lunarBook, waterRune.quantity(10), astralRune.quantity(3), lawRune.quantity(3));
        plantDwarf = new ObjectStep(this, 8151, new WorldPoint(2814, 3464, 0),
                "Rake the patch and plant the Dwarf weed seed, don't forget to compost!");
        plantDwarf.addIcon(ItemID.DWARF_WEED_SEED);
        waitDwarf = new DetailedQuestStep(this, "Wait for the Dwarf weed to grow.");
        pickDwarf = new ObjectStep(this, 39801, new WorldPoint(2813, 3463, 0),
                "Pick the Dwarf weed.");
        catch5Sharks = new NpcStep(this, NpcID.FISHING_SPOT_1519, new WorldPoint(2837, 3431, 0),
                "Catch 5 sharks.", harpoon);
        catch5Sharks.addAlternateNpcs(NpcID.FISHING_SPOT_1520);
        cook5Sharks = new ObjectStep(this, ObjectID.RANGE_26181, new WorldPoint(2818, 3444, 0),
                "Cook 5 sharks.", cookingGaunt.equipped(), rawShark.quantity(5));
        moveToSeersRooftop = new ObjectStep(this, 14927, new WorldPoint(2729, 3489, 0),
                "Climb on-top of Seers' Bank.");
        stamMix = new ItemStep(this, "Create a stamina mix.", stamPot.highlighted(), caviar.highlighted());
        runeHasta = new ObjectStep(this, ObjectID.BARBARIAN_ANVIL, new WorldPoint(2502, 3485, 0),
                "Smith an adamant spear.", runiteBar, magicLogs, hammer);
        runeHasta.addIcon(ItemID.RUNITE_BAR);
        pyre = new ObjectStep(this, 25286, new WorldPoint(2519, 3519, 0),
                "Construct a pyre ship from magic logs.", magicLogs, chewedBone, tinderbox, axe);
        barb5Heal = new DetailedQuestStep(this, "Level up your healer.");
        barb5Atk = new DetailedQuestStep(this, "Level up your attacker.");
        barb5Def = new DetailedQuestStep(this, "Level up your defender.");
        barb5Col = new DetailedQuestStep(this, "Level up your collector.");
        barb5 = new ObjectStep(this, ObjectID.BLACKBOARD_20134, new WorldPoint(2535, 3569, 0),
                "Click the blackboard!");
        barb52 = new ObjectStep(this, ObjectID.BLACKBOARD_20134, new WorldPoint(2587, 5264, 0),
                "Click the blackboard!");
        claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
                "Talk to the 'Wedge' infront of camelot castle to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(dwarfSeed, seedDib, spade, rake, compost, harpoon, cookingGaunt, stamPot, caviar, runiteBar, magicLogs.quantity(2), hammer, chewedBone, tinderbox, axe, lawRune.quantity(3), astralRune.quantity(3), waterRune.quantity(10), combatGear);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skill.AGILITY, 60, true));
        req.add(new SkillRequirement(Skill.COOKING, 80));
        req.add(new SkillRequirement(Skill.CRAFTING, 85));
        req.add(new SkillRequirement(Skill.FARMING, 79));
        req.add(new SkillRequirement(Skill.FIREMAKING, 85));
        req.add(new SkillRequirement(Skill.FISHING, 76));
        req.add(new SkillRequirement(Skill.HERBLORE, 86));
        req.add(new SkillRequirement(Skill.MAGIC, 87));
        req.add(new SkillRequirement(Skill.SMITHING, 90));

        return req;
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("Mithril Dragons (level 304) for chewed bones");
    }

    @Override
    public List<PanelDetails> getPanels() {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Teleport Catherby", Arrays.asList(tpCath), waterRune.quantity(10), lawRune.quantity(3), astralRune.quantity(3)));
        allSteps.add(new PanelDetails("Dwarf Weed", Arrays.asList(plantDwarf, waitDwarf, pickDwarf), dwarfSeed, seedDib, rake, spade, compost));
        allSteps.add(new PanelDetails("5 Sharks Caught and Cooked", Arrays.asList(catch5Sharks, cook5Sharks), harpoon, cookingGaunt));
        allSteps.add(new PanelDetails("Stamina Mix", Arrays.asList(moveToSeersRooftop, stamMix), stamPot, caviar));
        allSteps.add(new PanelDetails("Smith Rune Hasta", Arrays.asList(runeHasta), magicLogs.quantity(1), runiteBar, hammer));
        allSteps.add(new PanelDetails("Magic Pyre Ship", Arrays.asList(pyre), axe, tinderbox, magicLogs.quantity(1), chewedBone));
        allSteps.add(new PanelDetails("Level 5 each Role", Arrays.asList(barb5Heal, barb5Atk, barb5Def, barb5Col, barb5)));
        allSteps.add(new PanelDetails("Finishing off", Arrays.asList(claimReward)));

        return allSteps;
    }
}
