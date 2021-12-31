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

package com.questhelper.achievementdiaries.kourend;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@QuestDescriptor(
    quest = QuestHelperQuest.KOUREND_MEDIUM
)

public class KourendMedium extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement dramenStaff, kharedstsMemoirs, pickaxe, faceMask, hammer, nails, planks, kingWorm, axe, tinderbox,
            boxTrap, intelligence;

    // Items recommended
    ItemRequirement knife, warmClothing, combatGear, food, antipoison, radasBlessing1;

    // Quests required
    Requirement fairytaleII, depthsOfDespair, queenOfThieves, taleOfTheRighteous, forsakenTower, ascentOfArceuus,
            eaglesPeak, arceuusFavour, hosidiusFavour, shayzienFavour;

    // Requirements
    Requirement notFairyRing, notKillLizardman, notTravelWithMemoirs, notMineSulphur, notEnterFarmingGuild,
            notSwitchSpellbooks, notRepairCrane, notDeliverIntelligence, notCatchBluegill, notUseBoulderShortcut,
            notSubdueWintertodt, notCatchChinchompa, notChopMahoganyTree, hasIntelligence, hasBird;

    QuestStep travelFairyRing, travelWithMemoirs, enterFarmingGuild, travelToMolchIsland,
            repairCrane, catchBluegill, useBoulderShortcut, subdueWintertodt, catchChinchompa,
            chopMahoganyTree, claimReward, pickupWorms;

    ObjectStep mineSulphur;

    NpcStep killLizardman, switchSpellbooks, killGangBoss, deliverIntelligence, talkToAlry;

    ZoneRequirement inMolchIsland;
    Zone molchIsland;

    @Override
    public QuestStep loadStep()
    {
        ConditionalStep doMedium = new ConditionalStep(this, claimReward);

        doMedium.addStep(notFairyRing, travelFairyRing);
        doMedium.addStep(notKillLizardman, killLizardman);
        doMedium.addStep(notTravelWithMemoirs, travelWithMemoirs);
        doMedium.addStep(notMineSulphur, mineSulphur);
        doMedium.addStep(notEnterFarmingGuild, enterFarmingGuild);
        doMedium.addStep(notSwitchSpellbooks, switchSpellbooks);
        doMedium.addStep(notRepairCrane, repairCrane);
        doMedium.addStep(new Conditions(notDeliverIntelligence, hasIntelligence), deliverIntelligence);
        doMedium.addStep(notDeliverIntelligence, killGangBoss);
        doMedium.addStep(new Conditions(notCatchBluegill, hasBird), catchBluegill);
        doMedium.addStep(new Conditions(notCatchBluegill, kingWorm), talkToAlry);
        doMedium.addStep(new Conditions(notCatchBluegill, inMolchIsland), pickupWorms);
        doMedium.addStep(notCatchBluegill, travelToMolchIsland);
        doMedium.addStep(notUseBoulderShortcut, useBoulderShortcut);
        doMedium.addStep(notSubdueWintertodt, subdueWintertodt);
        doMedium.addStep(notCatchChinchompa, catchChinchompa);
        doMedium.addStep(notChopMahoganyTree, chopMahoganyTree);

        return doMedium;
    }

    public void setupRequirements()
    {
        // TODO: Find varplayerid
        notFairyRing = new VarplayerRequirement(0000, false, 0);
        notKillLizardman = new VarplayerRequirement(0000, false, 0);
        notTravelWithMemoirs = new VarplayerRequirement(0000, false, 0);
        notMineSulphur = new VarplayerRequirement(0000, false, 0);
        notEnterFarmingGuild = new VarplayerRequirement(0000, false, 0);
        notSwitchSpellbooks = new VarplayerRequirement(0000, false, 0);
        notRepairCrane = new VarplayerRequirement(0000, false, 0);
        notDeliverIntelligence = new VarplayerRequirement(0000, false, 0);
        notCatchBluegill = new VarplayerRequirement(0000, false, 0);
        notUseBoulderShortcut = new VarplayerRequirement(0000, false, 0);
        notSubdueWintertodt = new VarplayerRequirement(0000, false, 0);
        notCatchChinchompa = new VarplayerRequirement(0000, false, 0);
        notChopMahoganyTree = new VarplayerRequirement(0000, false, 0);

        // Required items
        dramenStaff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.getFairyStaff())
                .showConditioned(notFairyRing);
        kharedstsMemoirs = new ItemRequirement("Kharedst's Memoirs or Book of the Dead",
                Arrays.asList(ItemID.BOOK_OF_THE_DEAD, ItemID.KHAREDSTS_MEMOIRS)).showConditioned(notTravelWithMemoirs);
        pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notMineSulphur);
        faceMask = new ItemRequirement("Facemask or slayer helmet", ItemCollections.getSlayerHelmets())
                .showConditioned(notMineSulphur);
        faceMask.addAlternates(ItemID.FACEMASK, ItemID.GAS_MASK);
        hammer = new ItemRequirement("A hammer", ItemCollections.getHammer()).showConditioned(notRepairCrane);
        nails = new ItemRequirement("50+ Nails", ItemCollections.getNails()).showConditioned(notRepairCrane);
        planks = new ItemRequirement("3 Planks", ItemID.PLANK).showConditioned(notRepairCrane);
        kingWorm = new ItemRequirement("King worm or fish chunks", ItemID.KING_WORM).showConditioned(notCatchBluegill);
        kingWorm.addAlternates(ItemID.FISH_CHUNKS);
        axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notSubdueWintertodt);
        tinderbox = new ItemRequirement("Tinderbox", Arrays.asList(ItemID.BRUMA_TORCH, ItemID.TINDERBOX))
                .showConditioned(notSubdueWintertodt);
        boxTrap = new ItemRequirement("Box trap", ItemID.BOX_TRAP).showConditioned(notCatchChinchompa);
        intelligence = new ItemRequirement("Intelligence", ItemID.INTELLIGENCE).showConditioned(notDeliverIntelligence);

        // Recommended items
        knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notSubdueWintertodt);
        warmClothing = new ItemRequirement("Warm clothing", ItemCollections.getWarmClothing())
                .showConditioned(notSubdueWintertodt);
        combatGear = new ItemRequirement("Combat gear", -1, -1);
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood()).showConditioned(notKillLizardman);
        antipoison = new ItemRequirement("Anti-poison", ItemCollections.getAntipoisons())
                .showConditioned(notKillLizardman);
        radasBlessing1 = new ItemRequirement("Rada's Blessing (1)", ItemID.RADAS_BLESSING_1)
                .showConditioned(notCatchChinchompa);

        // Required quests
        fairytaleII = new QuestRequirement(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.IN_PROGRESS);
        depthsOfDespair = new QuestRequirement(QuestHelperQuest.THE_DEPTHS_OF_DESPAIR, QuestState.FINISHED);
        queenOfThieves = new QuestRequirement(QuestHelperQuest.THE_QUEEN_OF_THIEVES, QuestState.FINISHED);
        taleOfTheRighteous = new QuestRequirement(QuestHelperQuest.TALE_OF_THE_RIGHTEOUS, QuestState.FINISHED);
        forsakenTower = new QuestRequirement(QuestHelperQuest.THE_FORSAKEN_TOWER, QuestState.FINISHED);
        ascentOfArceuus = new QuestRequirement(QuestHelperQuest.THE_ASCENT_OF_ARCEUUS, QuestState.FINISHED);
        eaglesPeak = new QuestRequirement(QuestHelperQuest.EAGLES_PEAK, QuestState.FINISHED);

        arceuusFavour = new VarbitRequirement(Varbits.KOUREND_FAVOR_ARCEUUS.getId(), 600);
        hosidiusFavour = new VarbitRequirement(Varbits.KOUREND_FAVOR_HOSIDIUS.getId(), 600);
        shayzienFavour = new VarbitRequirement(Varbits.KOUREND_FAVOR_SHAYZIEN.getId(), 400);

        // Zone requirements
        inMolchIsland = new ZoneRequirement(molchIsland);

        hasIntelligence = intelligence.alsoCheckBank(questBank);
    }

    public void loadZones()
    {
        molchIsland = new Zone(new WorldPoint(1360, 3640, 0), new WorldPoint(1376, 3625, 0));
    }

    public void setupSteps()
    {
        // Travel to Fairy Ring
        travelFairyRing = new ObjectStep(this, ObjectID.FAIRY_RING, new WorldPoint(2658, 3230, 0),
                "Travel from any fairy ring to (CIR).", dramenStaff.highlighted());

        // Kill a lizardman
        killLizardman = new NpcStep(this, NpcID.LIZARDMAN, new WorldPoint(1507, 3683, 0),
                "Kill a lizardman.", true, combatGear, antipoison, food);
        killLizardman.addAlternateNpcs(NpcID.LIZARDMAN_6915, NpcID.LIZARDMAN_6916, NpcID.LIZARDMAN_6917,
                NpcID.LIZARDMAN_BRUTE, NpcID.LIZARDMAN_BRUTE_6919);

        // Travel with Kharedst's Memoirs
        // TODO: This is going to need someone that hasn't done the steps to check it. I assume there's a varbit
        // that is checking which places the user has teleported to with the memoirs.
        travelWithMemoirs = new ItemStep(this, "Teleport to each of the five cities via the memoirs.",
                kharedstsMemoirs.highlighted());
        travelWithMemoirs.addDialogStep("'Lunch by the Lancalliums' - Hosidius"); // TODO: check for varbit
        travelWithMemoirs.addDialogStep("'The Fisher's Flute' - Piscarillius"); // TODO: check for varbit
        travelWithMemoirs.addDialogStep("'History and Hearsay' - shayzien"); // TODO: check for varbit
        travelWithMemoirs.addDialogStep("'Jewellery of Jubilation' - Lovakengj"); // TODO: check for varbit
        travelWithMemoirs.addDialogStep("'A Dark Disposition' - Arceuus"); // TODO: check for varbit

        // Mine some volcanic sulphur
        mineSulphur = new ObjectStep(this, ObjectID.VOLCANIC_SULPHUR, new WorldPoint(1444, 3860, 0),
                "Mine some volcanic sulfur", true, pickaxe, faceMask.highlighted(),
                new SkillRequirement(Skill.MINING, 42));
        mineSulphur.addAlternateObjects(ObjectID.VOLCANIC_SULPHUR_28497, ObjectID.VOLCANIC_SULPHUR_28498);

        // Enter the farming guild
        enterFarmingGuild = new TileStep(this, new WorldPoint(1249, 3725, 0), "Enter the Farming Guild.",
                new SkillRequirement(Skill.FARMING, 45), hosidiusFavour);

        // Switch to the Arceuus spellbook via Tyss
        switchSpellbooks = new NpcStep(this, NpcID.TYSS, new WorldPoint(1712, 3882, 0),
                "Switch to the Arceuus spellbook via Tyss", arceuusFavour);
        switchSpellbooks.addDialogStep("Can I try the magicks myself?");

        // Repair a crane
        repairCrane = new ObjectStep(this, ObjectID.FISHING_CRANE, new WorldPoint(1830, 3735, 0),
                "Repair a crane withing Port Piscarilius.", true, new SkillRequirement(Skill.CRAFTING, 30),
                hammer, nails, planks);

        // Deliver some intelligence
        killGangBoss = new NpcStep(this, NpcID.GANG_BOSS, new WorldPoint(1778, 3682, 0),
                "Kill a gang boss or gang members for intelligence.", combatGear, food, shayzienFavour);
        killGangBoss.addAlternateNpcs(NpcID.GANG_BOSS_6901, NpcID.GANG_BOSS_6902, NpcID.GANG_BOSS_6903);
        deliverIntelligence = new NpcStep(this, NpcID.CAPTAIN_GINEA, new WorldPoint(1504, 3632, 0),
                "Turn in the intelligence to Captain Ginea.", intelligence);
        deliverIntelligence.addAlternateNpcs(NpcID.CAPTAIN_GINEA_10931);
        deliverIntelligence.addIcon(ItemID.INTELLIGENCE);

        // Catch a bluegill on Lake Molch
        travelToMolchIsland = new ObjectStep(this, ObjectID.BOATY, new WorldPoint(1408, 3612, 0),
                "Board the boaty to Molch Island.");
        travelToMolchIsland.addDialogStep("Molch Island");
        pickupWorms = new ItemStep(this, new WorldPoint(1371, 3633, 0), "Collect some King Worms");
        talkToAlry = new NpcStep(this, NpcID.ALRY_THE_ANGLER, new WorldPoint(1366, 3631, 0),
                "Talk to Alry the Angler to receive a bird.", new SkillRequirement(Skill.FISHING, 43),
                new SkillRequirement(Skill.HUNTER, 35));
        talkToAlry.addDialogStep(2, "Could I have a go with your bird?");
        catchBluegill = new NpcStep(this, NpcID.FISHING_SPOT_8523, new WorldPoint(1379, 3627, 0),
                "Catch a bluegill", kingWorm);
        catchBluegill.addSubSteps(pickupWorms);

        // Use the boulder leap shortcut
        useBoulderShortcut = new ObjectStep(this, ObjectID.BOULDER_27990, new WorldPoint(1776, 3883, 0),
                "Use the boulder leap shortcut from the North side", new SkillRequirement(Skill.AGILITY, 49));

        // Subdue the Wintertodt
        subdueWintertodt = new ObjectStep(this, ObjectID.DOORS_OF_DINH, new WorldPoint(1631, 3962, 0),
                "Enter the Wintertodt", new SkillRequirement(Skill.FIREMAKING, 50), axe, tinderbox, food,
                warmClothing, knife, hammer);
        subdueWintertodt.addIcon(ItemID.FIRE);

        // Catch a chinchompa
        catchChinchompa = new TileStep(this, new WorldPoint(1485, 3507, 0),
                "Catch a chinchompa in the Kourend Woodland", boxTrap.highlighted(), radasBlessing1.highlighted());
        catchChinchompa.addIcon(ItemID.BOX_TRAP);

        // Chop some mahogany logs
        chopMahoganyTree = new ObjectStep(this, ObjectID.MAHOGANY, new WorldPoint(1238, 3771, 0),
                "Chop some logs from a mahogany tree North of the Farming Guild.", true, axe,
                new SkillRequirement(Skill.WOODCUTTING, 50));

        // Claim reward
        claimReward = new NpcStep(this, NpcID.ELISE, new WorldPoint(1647, 3665, 0),
                "Talk to Elise in the Kourend castle courtyard to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary");
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("Kill a Lizardman (level 53)");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(dramenStaff, kharedstsMemoirs, pickaxe, faceMask, hammer, nails.quantity(50),
                planks.quantity(3), kingWorm, axe, tinderbox, boxTrap);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(knife, warmClothing, combatGear, food, antipoison, radasBlessing1);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();

        req.add(new SkillRequirement(Skill.AGILITY, 49, true));
        req.add(new SkillRequirement(Skill.CRAFTING, 30));
        req.add(new SkillRequirement(Skill.FARMING, 45));
        req.add(new SkillRequirement(Skill.FIREMAKING, 50));
        req.add(new SkillRequirement(Skill.FISHING, 43));
        req.add(new SkillRequirement(Skill.HUNTER, 53));
        req.add(new SkillRequirement(Skill.MINING, 42));
        req.add(new SkillRequirement(Skill.WOODCUTTING, 50, true));

        req.add(arceuusFavour);
        req.add(hosidiusFavour);
        req.add(shayzienFavour);
        req.add(fairytaleII);
        req.add(depthsOfDespair);
        req.add(queenOfThieves);
        req.add(taleOfTheRighteous);
        req.add(forsakenTower);
        req.add(ascentOfArceuus);
        req.add(eaglesPeak);

        return req;
    }

    @Override
    public List<ItemReward> getItemRewards()
    {
        return Arrays.asList(
                new ItemReward("Rada's Blessing (2)", ItemID.RADAS_BLESSING_2, 1),
                new ItemReward("7,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP, 1));
    }

    @Override
    public List<UnlockReward> getUnlockRewards()
    {
        return Arrays.asList(
                new UnlockReward("Free access cost for Crabclaw Isle"),
                new UnlockReward("5% chance to mine two Dense essence blocks at once"),
                new UnlockReward("20 free Dynamite per day from Thirus"),
                new UnlockReward("Reduced tanning prices at Eodan in Forthos Dungeon to 60%."));
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();

        PanelDetails fairyRingStep = new PanelDetails("Travel to Mount Karuulm",
                Collections.singletonList(travelFairyRing), dramenStaff, fairytaleII);
        fairyRingStep.setDisplayCondition(notFairyRing);
        allSteps.add(fairyRingStep);

        PanelDetails killLizardmanStep = new PanelDetails("Kill a lizardman", Collections.singletonList(killLizardman),
                food, combatGear, antipoison);
        killLizardmanStep.setDisplayCondition(notKillLizardman);
        allSteps.add(killLizardmanStep);

        PanelDetails travelMemoirsStep = new PanelDetails("Travel with Kharedst's Memoirs",
                Collections.singletonList(travelWithMemoirs), kharedstsMemoirs, depthsOfDespair, queenOfThieves,
                taleOfTheRighteous, forsakenTower, ascentOfArceuus);
        travelMemoirsStep.setDisplayCondition(notTravelWithMemoirs);
        allSteps.add(travelMemoirsStep);

        PanelDetails mineSulphurStep = new PanelDetails("Mine volcanic sulphur", Collections.singletonList(mineSulphur),
                pickaxe, faceMask, new SkillRequirement(Skill.MINING, 42));
        mineSulphurStep.setDisplayCondition(notMineSulphur);
        allSteps.add(mineSulphurStep);

        PanelDetails enterFarmingGuildStep = new PanelDetails("Enter the Farming Guild",
                Collections.singletonList(enterFarmingGuild), new SkillRequirement(Skill.FARMING, 45), hosidiusFavour);
        enterFarmingGuildStep.setDisplayCondition(notEnterFarmingGuild);
        allSteps.add(enterFarmingGuildStep);

        PanelDetails switchSpellbookStep = new PanelDetails("Switch spellbook", Collections.singletonList(switchSpellbooks),
                arceuusFavour);
        switchSpellbookStep.setDisplayCondition(notSwitchSpellbooks);
        allSteps.add(switchSpellbookStep);

        PanelDetails repairCraneStep = new PanelDetails("Repair a crane", Collections.singletonList(repairCrane),
                hammer, nails, planks, new SkillRequirement(Skill.CRAFTING, 30));
        repairCraneStep.setDisplayCondition(notRepairCrane);
        allSteps.add(repairCraneStep);

        PanelDetails deliverIntelligenceStep = new PanelDetails("Deliver intelligence", Arrays.asList(killGangBoss,
                deliverIntelligence), combatGear, food, shayzienFavour);
        deliverIntelligenceStep.setDisplayCondition(notDeliverIntelligence);
        allSteps.add(deliverIntelligenceStep);

        PanelDetails catchBluegillStep = new PanelDetails("Catch a bluegill", Arrays.asList(travelToMolchIsland,
                pickupWorms, talkToAlry, catchBluegill), kingWorm, new SkillRequirement(Skill.FISHING, 43),
                new SkillRequirement(Skill.HUNTER, 35));
        catchBluegillStep.setDisplayCondition(notCatchBluegill);
        allSteps.add(catchBluegillStep);

        PanelDetails leapBoulderStep = new PanelDetails("Leap the boulder", Collections.singletonList(useBoulderShortcut),
                new SkillRequirement(Skill.AGILITY, 49));
        leapBoulderStep.setDisplayCondition(notUseBoulderShortcut);
        allSteps.add(leapBoulderStep);

        PanelDetails subdueWintertodtStep = new PanelDetails("Subdue the Wintertodt",
                Collections.singletonList(subdueWintertodt), new SkillRequirement(Skill.FIREMAKING, 50), axe,
                tinderbox, food, warmClothing, knife, hammer);
        subdueWintertodtStep.setDisplayCondition(notSubdueWintertodt);
        allSteps.add(subdueWintertodtStep);

        PanelDetails catchChinStep = new PanelDetails("Catch a chinchompa", Collections.singletonList(catchChinchompa),
                new SkillRequirement(Skill.HUNTER, 53), boxTrap, eaglesPeak);
        catchChinStep.setDisplayCondition(notCatchChinchompa);
        allSteps.add(catchChinStep);

        PanelDetails chopMahoganyStep = new PanelDetails("Chop mahogany tree", Collections.singletonList(chopMahoganyTree),
                new SkillRequirement(Skill.WOODCUTTING, 50), axe);
        chopMahoganyStep.setDisplayCondition(notChopMahoganyTree);
        allSteps.add(chopMahoganyStep);

        return allSteps;
    }
}
