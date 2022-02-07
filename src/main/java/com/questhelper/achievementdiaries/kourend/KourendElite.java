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
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
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
    quest = QuestHelperQuest.KOUREND_ELITE
)

public class KourendElite extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement pickaxe, chisel, axe, darkTotem, totemBase, totemMiddle, totemTop, denseEssenceBlock,
            bloodRune, lawRune, soulRune, fishingRod, sandworm, celastrusSapling, combatGear, food, prayerPotion,
            celastrusBark, darkEssenceBlock, darkEssenceFragment, rawAnglerfish, knife, bootsOfStone, spade;

    // Items recommended
    ItemRequirement arclight, kharedstsMemoirs, dramenStaff, skillsNecklace, radasBlessing, xericsTalisman,
            potatoCactus, ultraCompost;

    // Quests required
    Requirement hosidiusFavour60, hosidiusFavour75, arceuusFavour, piscariliusFavour;

    // Requirements
    Requirement notCraftBloodRune, notChopRedwood, notDefeatSkotizo, notCatchAngler, notKillHydra, notCreateTeleport,
            notCompleteRaid, notFletchBattlestave, hasDarkTotem, hasCelastrusBark, onArceuusSpellbook,
            celastrusTreeGrown;

    QuestStep craftBloodRune, enterCatacombs, enterSkotizoLair, defeatSkotizo, catchAngler, killHydra,
            createTeleportTab, completeRaid, plantCelastrusTree, harvestCelastrus, fletchBattlestave, enterHydraArea,
            enterMountKaruulmDungeon, enterWoodcuttingGuild, climbRedwoodTree, mineDenseEssence, chiselEssenceBlock,
            venerateEssenceBlock, cookAnglerfish, combineDarkTotem, claimReward;

    ObjectStep chopRedwood;
    NpcStep switchSpellbook;

    ZoneRequirement inRedwoodTree, inCatacombs, inSkotizoLair, inWoodcuttingGuild, inMountKaruulmDungeon, inHydraArea;
    Zone redwoodTree, catacombs, skotizoLair, woodcuttingGuild, mountKaruulmDungeon, hydraArea;

    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doElite = new ConditionalStep(this, claimReward);
        doElite.addStep(new Conditions(notCraftBloodRune, darkEssenceFragment), craftBloodRune);
        doElite.addStep(new Conditions(notCraftBloodRune, darkEssenceBlock), chiselEssenceBlock);
        doElite.addStep(new Conditions(notCraftBloodRune, denseEssenceBlock), venerateEssenceBlock);
        doElite.addStep(notCraftBloodRune, mineDenseEssence);
        doElite.addStep(new Conditions(notChopRedwood, inRedwoodTree), chopRedwood);
        doElite.addStep(new Conditions(notChopRedwood, inWoodcuttingGuild), climbRedwoodTree);
        doElite.addStep(notChopRedwood, enterWoodcuttingGuild);
        doElite.addStep(new Conditions(notDefeatSkotizo, inSkotizoLair), defeatSkotizo);
        doElite.addStep(new Conditions(notDefeatSkotizo, inCatacombs), enterSkotizoLair);
        doElite.addStep(new Conditions(notDefeatSkotizo, hasDarkTotem), enterCatacombs);
        doElite.addStep(notDefeatSkotizo, combineDarkTotem);
        doElite.addStep(new Conditions(notCatchAngler, rawAnglerfish), cookAnglerfish);
        doElite.addStep(notCatchAngler, catchAngler);
        doElite.addStep(new Conditions(notKillHydra, inHydraArea), killHydra);
        doElite.addStep(new Conditions(notKillHydra, inMountKaruulmDungeon), enterHydraArea);
        doElite.addStep(notKillHydra, enterMountKaruulmDungeon);
        doElite.addStep(new Conditions(notCraftBloodRune, onArceuusSpellbook), createTeleportTab);
        doElite.addStep(new Conditions(notCraftBloodRune, darkEssenceBlock), switchSpellbook);
        doElite.addStep(new Conditions(notCreateTeleport, denseEssenceBlock), venerateEssenceBlock);
        doElite.addStep(notCreateTeleport, mineDenseEssence);
        doElite.addStep(notCompleteRaid, completeRaid);
        doElite.addStep(new Conditions(notFletchBattlestave, hasCelastrusBark), fletchBattlestave);
        doElite.addStep(new Conditions(notFletchBattlestave, celastrusTreeGrown), harvestCelastrus);
        doElite.addStep(notFletchBattlestave, plantCelastrusTree);

        return doElite;
    }

    public void setupRequirements()
    {
        // TODO: Find varplayerid
        notCraftBloodRune = new VarplayerRequirement(0000, false, 0);
        notChopRedwood = new VarplayerRequirement(0000, false, 0);
        notDefeatSkotizo = new VarplayerRequirement(0000, false, 0);
        notCatchAngler = new VarplayerRequirement(0000, false, 0);
        notKillHydra = new VarplayerRequirement(0000, false, 0);
        notCreateTeleport = new VarplayerRequirement(0000, false, 0);
        notCompleteRaid = new VarplayerRequirement(0000, false, 0);
        notFletchBattlestave = new VarplayerRequirement(0000, false, 0);

        // Items required
        pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes())
                .showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport));
        chisel = new ItemRequirement("Chisel", ItemID.CHISEL)
                .showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport));
        chisel.setTooltip("One can be found in the Arceuus essence mine.");
        axe = new ItemRequirement("Any axe", ItemCollections.getAxes())
                .showConditioned(notChopRedwood);
        darkTotem = new ItemRequirement("Dark Totem", ItemID.DARK_TOTEM)
                .showConditioned(notDefeatSkotizo);
        totemBase = new ItemRequirement("Dark totem base", ItemID.DARK_TOTEM_BASE)
                .hideConditioned(hasDarkTotem);
        totemMiddle = new ItemRequirement("Dark totem middle", ItemID.DARK_TOTEM_MIDDLE)
                .hideConditioned(hasDarkTotem);
        totemTop = new ItemRequirement("Dark totem top", ItemID.DARK_TOTEM_TOP)
                .hideConditioned(hasDarkTotem);
        denseEssenceBlock = new ItemRequirement("Dense essence block", ItemID.DENSE_ESSENCE_BLOCK)
                .showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport));
        denseEssenceBlock.canBeObtainedDuringQuest();
        bloodRune = new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2)
                .showConditioned(notCreateTeleport);
        lawRune = new ItemRequirement("Law runes", ItemID.LAW_RUNE, 2)
                .showConditioned(notCreateTeleport);
        soulRune = new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2)
                .showConditioned(notCreateTeleport);
        fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD)
                .showConditioned(notCatchAngler);
        sandworm = new ItemRequirement("Sandworms", ItemID.SANDWORMS)
                .showConditioned(notCatchAngler);
        celastrusSapling = new ItemRequirement("Celastrus sapling", ItemID.CELASTRUS_SAPLING)
                .hideConditioned(celastrusTreeGrown);
        knife = new ItemRequirement("Knife", ItemID.KNIFE)
                .showConditioned(notFletchBattlestave);
        combatGear = new ItemRequirement("Combat gear", -1, -1)
                .showConditioned(new Conditions(LogicType.OR, notDefeatSkotizo, notKillHydra));
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood())
                .showConditioned(new Conditions(LogicType.OR, notDefeatSkotizo, notKillHydra));
        prayerPotion = new ItemRequirement("Prayer potions", ItemCollections.getPrayerPotions())
                .showConditioned(new Conditions(LogicType.OR, notDefeatSkotizo, notKillHydra));
        celastrusBark = new ItemRequirement("Celastrus bark", ItemID.CELASTRUS_BARK)
                .showConditioned(notFletchBattlestave);
        celastrusBark.canBeObtainedDuringQuest();
        darkEssenceBlock = new ItemRequirement("Dark essence block", ItemID.DARK_ESSENCE_BLOCK)
                .showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport));
        darkEssenceBlock.canBeObtainedDuringQuest();
        darkEssenceFragment = new ItemRequirement("Dark essence fragments", ItemID.DARK_ESSENCE_FRAGMENTS)
                .showConditioned(notCraftBloodRune);
        darkEssenceFragment.canBeObtainedDuringQuest();
        rawAnglerfish = new ItemRequirement("Raw anglerfish", ItemID.RAW_ANGLERFISH, 1)
                .showConditioned(notCatchAngler);
        rawAnglerfish.canBeObtainedDuringQuest();
        bootsOfStone = new ItemRequirement("Boots of stone", ItemCollections.getStoneBoots(), 1, true)
                .showConditioned(notKillHydra);
        spade = new ItemRequirement("Spade", ItemID.SPADE)
                .showConditioned(new Conditions(LogicType.XOR, notFletchBattlestave, celastrusTreeGrown));
        spade.canBeObtainedDuringQuest();

        // Items recommended
        arclight = new ItemRequirement("Arclight", ItemID.ARCLIGHT, -1, true);
        kharedstsMemoirs = new ItemRequirement("Kharedst's Memoirs or Book of the Dead",
                Arrays.asList(ItemID.BOOK_OF_THE_DEAD, ItemID.KHAREDSTS_MEMOIRS), -1);
        xericsTalisman = new ItemRequirement("Xeric's Talisman", Arrays.asList(ItemID.XERICS_TALISMAN,
                ItemID.MOUNTED_XERICS_TALISMAN), -1);
        dramenStaff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.getFairyStaff(), -1);
        radasBlessing = new ItemRequirement("Rada's Blessing", Arrays.asList(ItemID.RADAS_BLESSING_1,
                ItemID.RADAS_BLESSING_2, ItemID.RADAS_BLESSING_3), -1);
        skillsNecklace = new ItemRequirement("Skills neckalce", ItemCollections.getSkillsNecklaces(), -1);
        potatoCactus = new ItemRequirement("Potato cactus", ItemID.POTATO_CACTUS, -8);
        ultraCompost = new ItemRequirement("Ultra compost", Arrays.asList(ItemID.ULTRACOMPOST,
                ItemID.BOTTOMLESS_COMPOST_BUCKET), -1);

        arceuusFavour = new VarbitRequirement(Varbits.KOUREND_FAVOR_ARCEUUS.getId(), Operation.GREATER_EQUAL, 1000,
                "");
        hosidiusFavour60 = new VarbitRequirement(Varbits.KOUREND_FAVOR_HOSIDIUS.getId(), Operation.GREATER_EQUAL, 600,
                "");
        hosidiusFavour75 = new VarbitRequirement(Varbits.KOUREND_FAVOR_HOSIDIUS.getId(), Operation.GREATER_EQUAL, 750,
                "");
        piscariliusFavour = new VarbitRequirement(Varbits.KOUREND_FAVOR_PISCARILIUS.getId(), Operation.GREATER_EQUAL, 1000,
                "");

        // Zone requirements
        inRedwoodTree = new ZoneRequirement(redwoodTree);
        inCatacombs = new ZoneRequirement(catacombs);
        inSkotizoLair = new ZoneRequirement(skotizoLair);
        inWoodcuttingGuild = new ZoneRequirement(woodcuttingGuild);
        inMountKaruulmDungeon = new ZoneRequirement(mountKaruulmDungeon);
        inHydraArea = new ZoneRequirement(hydraArea);

        hasCelastrusBark = celastrusBark.alsoCheckBank(questBank);
        hasDarkTotem = darkTotem.alsoCheckBank(questBank);
    }

    public void loadZones()
    {
        redwoodTree = new Zone(new WorldPoint(1567, 3496, 1), new WorldPoint(1574, 3479, 1));
        catacombs = new Zone(new WorldPoint(1659, 10052, 0), new WorldPoint(1668, 10043, 0));
        skotizoLair = new Zone(new WorldPoint(1479, 2415, 0), new WorldPoint(6513, 2384, 0));
        woodcuttingGuild = new Zone(new WorldPoint(1563, 3948, 0), new WorldPoint(1581, 3477, 0));
        mountKaruulmDungeon = new Zone(new WorldPoint(1303, 10210, 0), new WorldPoint(1320, 10187, 0));
        hydraArea = new Zone(new WorldPoint(1310, 10250, 0), new WorldPoint(1330, 10215, 0));
    }

    public void setupSteps()
    {
        // Craft blood runes
        mineDenseEssence = new ObjectStep(this, ObjectID.DENSE_RUNESTONE, new WorldPoint(1764, 3858, 0),
                "Mine a dense essence block", pickaxe, chisel, new SkillRequirement(Skill.MINING, 38));
        venerateEssenceBlock = new ObjectStep(this, ObjectID.DARK_ALTAR, new WorldPoint(1716, 3883, 0),
                "Venerate the essence block.", denseEssenceBlock);
        chiselEssenceBlock = new ItemStep(this, "Chisel the dark essence block.", chisel.highlighted(),
                darkEssenceBlock.highlighted(), new SkillRequirement(Skill.CRAFTING, 38));
        craftBloodRune = new ObjectStep(this, ObjectID.BLOOD_ALTAR, new WorldPoint(1718, 3828, 0),
                "Craft some blood runes.", darkEssenceFragment, new SkillRequirement(Skill.RUNECRAFT, 77, true));

        // Chop redwood logs
        enterWoodcuttingGuild = new TileStep(this, new WorldPoint(1570, 3487, 0),
                "Enter the Woodcutting Guild.", hosidiusFavour75, skillsNecklace, radasBlessing,
                new SkillRequirement(Skill.WOODCUTTING, 60));
        climbRedwoodTree = new ObjectStep(this, ObjectID.ROPE_LADDER_28857, "Climb the redwood tree.");
        chopRedwood = new ObjectStep(this, ObjectID.REDWOOD, "Chop the redwood tree.",
                axe, new SkillRequirement(Skill.WOODCUTTING, 90, true));
        chopRedwood.addAlternateObjects(ObjectID.REDWOOD_29670);

        // Defeat Skotizo
        combineDarkTotem = new ItemStep(this, "Combine the dark totem pieces.", totemTop.highlighted(),
                totemMiddle.highlighted(), totemBase.highlighted());
        enterCatacombs = new ObjectStep(this, ObjectID.STATUE_27785, "Enter the Catacombs of Kourend.",
                xericsTalisman);
        enterSkotizoLair = new ObjectStep(this, ObjectID.ALTAR_28900, "Enter Skotizo's Lair.",
                darkTotem.highlighted());
        enterSkotizoLair.addIcon(ItemID.DARK_TOTEM);
        defeatSkotizo = new NpcStep(this, NpcID.SKOTIZO, "Defeat Skotizo", combatGear, food, prayerPotion);

        // Catch and cook and an anglerfish
        catchAngler = new NpcStep(this, NpcID.ROD_FISHING_SPOT_6825, "Catch a raw anglerfish.",
                fishingRod, sandworm, piscariliusFavour, kharedstsMemoirs,
                new SkillRequirement(Skill.FISHING, 82, true));
        cookAnglerfish = new ObjectStep(this, ObjectID.RANGE_27724, "Cook a raw anglerfish in Kourend.",
                rawAnglerfish, new SkillRequirement(Skill.COOKING, 84, true));

        // Kill a hydra
        enterMountKaruulmDungeon = new ObjectStep(this, ObjectID.ELEVATOR, new WorldPoint(1311, 3807, 0),
                "Enter the Mount Karuulm Slayer Dungeon.", radasBlessing, dramenStaff);
        enterHydraArea = new ObjectStep(this, ObjectID.ROCKS_34544, new WorldPoint(1312, 10215, 0),
                "Enter the hydra area", bootsOfStone);
        killHydra = new NpcStep(this, NpcID.HYDRA, new WorldPoint(1312, 10232, 0),
                "Kill a hydra.", combatGear, food, prayerPotion, new SkillRequirement(Skill.SLAYER, 95, true));
        killHydra.addSubSteps(enterMountKaruulmDungeon, enterHydraArea);

        // Create an Ape Atol teleport tab
        mineDenseEssence = new ObjectStep(this, ObjectID.DENSE_RUNESTONE, new WorldPoint(1764, 3858, 0),
                "Mine a dense essence block", pickaxe, chisel, dramenStaff,
                new SkillRequirement(Skill.MINING, 38));
        venerateEssenceBlock = new ObjectStep(this, ObjectID.DARK_ALTAR, new WorldPoint(1716, 3883, 0),
                "Venerate the essence block.", denseEssenceBlock);
        switchSpellbook = new NpcStep(this, NpcID.TYSS, new WorldPoint(1712, 3882, 0),
                "Switch to the Arceuus spellbook via Tyss", arceuusFavour);
        switchSpellbook.addDialogStep("Can I try the magicks myself?");
        createTeleportTab = new ObjectStep(this, ObjectID.LECTERN_28802, new WorldPoint(1679, 3765, 0),
                "Create an Ape Atoll teleport tablet.", darkEssenceBlock, bloodRune.quantity(2), lawRune.quantity(2),
                soulRune.quantity(2), arceuusFavour, new SkillRequirement(Skill.MAGIC, 90, true));

        // Complete a raid
        completeRaid = new ObjectStep(this, ObjectID.CHAMBERS_OF_XERIC, "Complete a Chambers of Xeric raid.",
                kharedstsMemoirs);

        // Fletch a battlestaff from scratch
        // TODO: Check the ObjectIDs for the patch/tree, they don't seem to match what I see in game
        plantCelastrusTree = new ObjectStep(this, ObjectID.CELASTRUS_PATCH, "Plant a celastrus sapling", spade,
                celastrusSapling, hosidiusFavour60, skillsNecklace, dramenStaff,
                new SkillRequirement(Skill.FARMING, 85));
        harvestCelastrus = new ObjectStep(this, ObjectID.CELASTRUS_TREE, "harvest some celastrus bark.",
                axe, new SkillRequirement(Skill.FARMING, 85));
        fletchBattlestave = new ItemStep(this, "Fletch a battlestaff", knife.highlighted(),
                celastrusBark.highlighted(), new SkillRequirement(Skill.FLETCHING, 40));

        // Claim reward
        claimReward = new NpcStep(this, NpcID.ELISE, new WorldPoint(1647, 3665, 0),
                "Talk to Elise in the Kourend castle courtyard to claim your reward!", xericsTalisman);
        claimReward.addDialogStep("I have a question about my Achievement Diary");
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Arrays.asList(
                "Kill Skotizo (level 321)",
                "Kill a Hydra (level 194)");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        return Arrays.asList(pickaxe, chisel, axe, darkTotem, totemBase, totemMiddle, totemTop, denseEssenceBlock,
                bloodRune.quantity(2), lawRune.quantity(2), soulRune.quantity(2), fishingRod, sandworm,
                celastrusSapling, combatGear, food, prayerPotion, knife, bootsOfStone);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(arclight, kharedstsMemoirs, dramenStaff, skillsNecklace, radasBlessing, xericsTalisman,
                potatoCactus, ultraCompost);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();

        req.add(new SkillRequirement(Skill.COOKING, 84, true));
        req.add(new SkillRequirement(Skill.CRAFTING, 38));
        req.add(new SkillRequirement(Skill.FARMING, 85));
        req.add(new SkillRequirement(Skill.FISHING, 82, true));
        req.add(new SkillRequirement(Skill.FLETCHING, 40));
        req.add(new SkillRequirement(Skill.MAGIC, 90, true));
        req.add(new SkillRequirement(Skill.MINING, 38));
        req.add(new SkillRequirement(Skill.RUNECRAFT, 77, true));
        req.add(new SkillRequirement(Skill.SLAYER, 95, true));
        req.add(new SkillRequirement(Skill.WOODCUTTING, 90, true));

        // Overall required favours
        req.add(new VarbitRequirement(Varbits.KOUREND_FAVOR_ARCEUUS.getId(), 1000));
        req.add(new VarbitRequirement(Varbits.KOUREND_FAVOR_HOSIDIUS.getId(), 750));
        req.add(new VarbitRequirement(Varbits.KOUREND_FAVOR_PISCARILIUS.getId(), 1000));

        return req;
    }

    @Override
    public List<ItemReward> getItemRewards()
    {
        return Arrays.asList(
                new ItemReward("Rada's Blessing (4)", ItemID.RADAS_BLESSING_4, 1),
                new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP, 1));
    }

    @Override
    public List<UnlockReward> getUnlockRewards()
    {
        return Arrays.asList(
                new UnlockReward("Demonic ashes from the ash sanctifier grant full Prayer experience"),
                new UnlockReward("Completing a slayer task from Konar quo Maten gives 20 Slayer points (up from 18)"),
                new UnlockReward("10% reduced burn chance at the city kitchens (up from 5%)"),
                new UnlockReward("Protection from the burn effect in Karuulm Slayer Dungeon without boots of stone"),
                new UnlockReward("5% increased chance ot save a harvest life at the Hosidius and Farming Guild herb patches."),
                new UnlockReward("80 free Dynamite per day from Thirus"),
                new UnlockReward("10% additional blood runes from blood runecrafting (no additional xp)"),
                new UnlockReward("Reduced tanning prices at Eodan in Forthos Dungeon to 20%."));
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();

        PanelDetails craftBloodRuneStep = new PanelDetails("Craft some blood runes", Arrays.asList(mineDenseEssence,
                venerateEssenceBlock, chiselEssenceBlock, craftBloodRune), arceuusFavour, pickaxe, chisel,
                new SkillRequirement(Skill.RUNECRAFT, 77, true), new SkillRequirement(Skill.MINING, 38),
                new SkillRequirement(Skill.CRAFTING, 38));
        craftBloodRuneStep.setDisplayCondition(notCraftBloodRune);
        allSteps.add(craftBloodRuneStep);

        PanelDetails chopRedwoodStep = new PanelDetails("Chop some redwood logs", Arrays.asList(enterWoodcuttingGuild,
                climbRedwoodTree, chopRedwood), axe, radasBlessing, skillsNecklace, hosidiusFavour75,
                new SkillRequirement(Skill.WOODCUTTING, 90, true));
        chopRedwoodStep.setDisplayCondition(notChopRedwood);
        allSteps.add(chopRedwoodStep);

        PanelDetails defeatSkotizoStep = new PanelDetails("Defeat Skotizo", Arrays.asList(combineDarkTotem,
                enterCatacombs, enterSkotizoLair, defeatSkotizo), darkTotem, combatGear, food, prayerPotion);
        defeatSkotizoStep.setDisplayCondition(notDefeatSkotizo);
        allSteps.add(defeatSkotizoStep);

        PanelDetails catchAnglerStep = new PanelDetails("Catch and cook an Anglerfish", Arrays.asList(catchAngler,
                cookAnglerfish), fishingRod, sandworm, kharedstsMemoirs, piscariliusFavour,
                new SkillRequirement(Skill.COOKING, 84, true),
                new SkillRequirement(Skill.FISHING, 82, true));
        catchAnglerStep.setDisplayCondition(notCatchAngler);
        allSteps.add(catchAnglerStep);

        PanelDetails killHydraStep = new PanelDetails("Kill a hydra", Arrays.asList(enterMountKaruulmDungeon,
                enterHydraArea, killHydra), combatGear, food, prayerPotion, bootsOfStone,
                new SkillRequirement(Skill.SLAYER, 95, true));
        killHydraStep.setDisplayCondition(notKillHydra);
        allSteps.add(killHydraStep);

        PanelDetails createTabStep = new PanelDetails("Create an Ape Atoll teleport tablet", Arrays.asList(
                mineDenseEssence, venerateEssenceBlock, switchSpellbook, createTeleportTab), chisel, pickaxe,
                soulRune.quantity(2), lawRune.quantity(2), bloodRune.quantity(2), kharedstsMemoirs, dramenStaff,
                darkEssenceBlock, arceuusFavour, new SkillRequirement(Skill.MAGIC, 90, true));
        createTabStep.setDisplayCondition(notCreateTeleport);
        allSteps.add(createTabStep);

        PanelDetails raidStep = new PanelDetails("Complete a Chambers of Xerric raid", Collections.singletonList(
                completeRaid), kharedstsMemoirs);
        raidStep.setDisplayCondition(notCompleteRaid);
        allSteps.add(raidStep);

        PanelDetails battlestaffStep = new PanelDetails("Create a battlestaff from scratch in the Farming Guild",
                Arrays.asList(plantCelastrusTree, harvestCelastrus, fletchBattlestave), spade, celastrusSapling, knife,
                axe, hosidiusFavour60, ultraCompost, potatoCactus, dramenStaff, skillsNecklace,
                new SkillRequirement(Skill.FARMING, 85), new SkillRequirement(Skill.FLETCHING, 40));
        battlestaffStep.setDisplayCondition(notFletchBattlestave);
        allSteps.add(battlestaffStep);

        return allSteps;
    }
}
