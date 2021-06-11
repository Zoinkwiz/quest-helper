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
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.KANDARIN_HARD
)

public class KandarinHard extends ComplexStateQuestHelper
{
    // Items required
    ItemRequirement barbRod, feather, axe, bowString, knife, cosmicRune, waterRune, unpoweredOrb, dustyKey, mapleLogs, bow, ringOfVis, coins, addyBar, hammer, yewLogs, combatGear;

    // Items recommended
    ItemRequirement food;

    // unlisted item reqs
    ItemRequirement unstrungYewLong;

    Requirement piety;

    Requirement notCatchStur, notSeersRooftop, notYewLong, notPietyCourt, notWaterOrb, notBurnMaple, notShadowHound, notMithrilDrag, notBuyGranite, notFancyStone, notAddySpear;

    //Quest steps
    Requirement barbFishing, barbFiremaking, barbSmithing, taiBwoWannai, knightWaves, desertTreasure;

    QuestStep claimReward, moveToTavDungeon, moveToOb, waterOrb, seersRooftop, yewLong, cutLongbow, stringBow, pietyCourt, burnMaple, moveToSeers, fancyStone, moveToShadow, shadowHound, catchStur, addySpear, moveToWhirl, moveToAncient2, moveToAncient3, mithrilDrag, buyGranite;

    Zone tavDungeon, obIsland, seers, shadow, ancient1, ancient2, ancient3;

    ZoneRequirement inTavDungeon, inObIsland, inSeers, inShadow, inAncient1, inAncient2, inAncient3;

    @Override
    public QuestStep loadStep()
    {
        loadZones();
        setupRequirements();
        setupSteps();

        ConditionalStep doHard = new ConditionalStep(this, claimReward);
        doHard.addStep(new Conditions(notWaterOrb, inObIsland), waterOrb);
        doHard.addStep(new Conditions(notWaterOrb, inTavDungeon), moveToOb);
        doHard.addStep(notWaterOrb, moveToTavDungeon);
        doHard.addStep(notSeersRooftop, seersRooftop);
        doHard.addStep(new Conditions(notYewLong, unstrungYewLong), stringBow);
        doHard.addStep(new Conditions(notYewLong, yewLogs), cutLongbow);
        doHard.addStep(notYewLong, yewLong);
        doHard.addStep(notPietyCourt, pietyCourt);
        doHard.addStep(new Conditions(notBurnMaple, inSeers), burnMaple);
        doHard.addStep(notBurnMaple, moveToSeers);
        doHard.addStep(notFancyStone, fancyStone);
        doHard.addStep(new Conditions(notShadowHound, inShadow), shadowHound);
        doHard.addStep(notShadowHound, moveToShadow);
        doHard.addStep(notCatchStur, catchStur);
        doHard.addStep(notAddySpear, addySpear);
        doHard.addStep(new Conditions(notMithrilDrag, inShadow), shadowHound);
        doHard.addStep(new Conditions(notMithrilDrag, inAncient3), mithrilDrag);
        doHard.addStep(new Conditions(notMithrilDrag, inAncient2), moveToAncient3);
        doHard.addStep(new Conditions(notMithrilDrag, inAncient1), moveToAncient2);
        doHard.addStep(notMithrilDrag, moveToWhirl);

        return doHard;
    }

    public void setupRequirements()
    {
        notCatchStur = new VarplayerRequirement(1178, false, 26);
        notSeersRooftop = new VarplayerRequirement(1178, false, 27);
        notYewLong = new VarplayerRequirement(1178, false, 28);
        notPietyCourt = new VarplayerRequirement(1178, false, 29);
        notWaterOrb = new VarplayerRequirement(1178, false, 30);
        notBurnMaple = new VarplayerRequirement(1178, false, 31);
        notShadowHound = new VarplayerRequirement(1179, false, 0);
        notMithrilDrag = new VarplayerRequirement(1179, false, 1);
        notBuyGranite = new VarplayerRequirement(1179, false, 2);
        notFancyStone = new VarplayerRequirement(1179, false, 3);
        notAddySpear = new VarplayerRequirement(1179, false, 4);

        piety = new PrayerRequirement("Piety", Prayer.PIETY);

        barbRod = new ItemRequirement("Barbarian fishing rod", ItemID.BARBARIAN_ROD).showConditioned(notCatchStur);
        feather = new ItemRequirement("Feathers", ItemID.FEATHER).showConditioned(notCatchStur);
        axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notYewLong);
        bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notYewLong);
        knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notYewLong);
        waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notWaterOrb);
        cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE).showConditioned(notWaterOrb);
        unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB).showConditioned(notWaterOrb);
        dustyKey = new ItemRequirement("Dusty key", ItemID.DUSTY_KEY).showConditioned(notWaterOrb);
        mapleLogs = new ItemRequirement("Maple logs", ItemID.MAPLE_LOGS).showConditioned(notBurnMaple);
        bow = new ItemRequirement("Dusty key", ItemCollections.getBows()).showConditioned(notBurnMaple);
        ringOfVis = new ItemRequirement("Ring of Visibility", ItemID.RING_OF_VISIBILITY).showConditioned(notShadowHound);
        coins = new ItemRequirement("Coins", ItemID.COINS).showConditioned(new Conditions(LogicType.OR, notFancyStone, notBuyGranite));
        addyBar = new ItemRequirement("Adamantite bar", ItemID.ADAMANTITE_BAR).showConditioned(notAddySpear);
        hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notAddySpear);
        yewLogs = new ItemRequirement("Yew logs", ItemID.YEW_LOGS).showConditioned(notAddySpear);
        unstrungYewLong = new ItemRequirement("Unstrung yew longbow", ItemID.YEW_LONGBOW_U);

        combatGear = new ItemRequirement("Combat gear", -1, -1).showConditioned(new Conditions(LogicType.OR, notShadowHound, notMithrilDrag));
        combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

        food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

        // TODO find a way to track barb training / knight waves
        barbFishing = new ComplexRequirement("Barbarian fishing");
        barbFiremaking = new ComplexRequirement("Barbarian firemaking");
        barbSmithing = new ComplexRequirement("Barbarian smithing");
        taiBwoWannai = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED);
        knightWaves = new ComplexRequirement("Knight waves training grounds");
        // knightWaves = new QuestRequirement(QuestHelperQuest., QuestState.FINISHED);
        desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);

        inTavDungeon = new ZoneRequirement(tavDungeon);
        inObIsland = new ZoneRequirement(obIsland);
        inSeers = new ZoneRequirement(seers);
        inShadow = new ZoneRequirement(shadow);
        inAncient1 = new ZoneRequirement(ancient1);
        inAncient2 = new ZoneRequirement(ancient2);
        inAncient3 = new ZoneRequirement(ancient3);
    }

    public void loadZones()
    {
        tavDungeon = new Zone(new WorldPoint(2813, 9857, 0), new WorldPoint(2972, 9669, 0));
        obIsland = new Zone(new WorldPoint(2833, 3427, 0), new WorldPoint(2849, 3415, 0));
        seers = new Zone(new WorldPoint(2682, 3510, 0), new WorldPoint(2742, 3455, 0));
        shadow = new Zone(new WorldPoint(2621, 5121, 0), new WorldPoint(2754, 5054, 0));
        ancient1 = new Zone(new WorldPoint(1761, 5369, 1), new WorldPoint(1770, 5363, 1));
        ancient2 = new Zone(new WorldPoint(1733, 5373, 0), new WorldPoint(1792, 5315, 0));
        ancient3 = new Zone(new WorldPoint(1734, 5362, 1), new WorldPoint(1792, 5279, 1));
    }

    public void setupSteps()
    {
        moveToTavDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0),
                "Enter the Taverly Dungeon.");
        moveToOb = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2842, 9824,0),
                "Climb the ladder.");
        waterOrb = new ObjectStep(this, 2151, new WorldPoint(2844, 3422, 0),
                "Use charge water orb on the obelisk.");
        waterOrb.addIcon(ItemID.WATER_ORB);
        seersRooftop = new ObjectStep(this, 14927, new WorldPoint(2729, 3489, 0),
                "Complete a lap of the Seers' village Rooftop course.");
        yewLong = new ObjectStep(this, 10822, new WorldPoint(2715, 3460, 0),
                "Cut some yew logs. Make sure to use the knife on the ones you cut.", axe);
        cutLongbow = new ItemStep(this, "Use knife on yew logs to make a yew longbow (u)", yewLogs.highlighted(), knife.highlighted());
        stringBow= new ItemStep(this, "String the bow.", bowString.highlighted(), unstrungYewLong.highlighted());
        pietyCourt = new ObjectStep(this, 123, new WorldPoint(2735, 3469, 0),
                "Activate piety then enter the courthouse.", piety);
        moveToSeers = new ObjectStep(this, 123, new WorldPoint(2714, 3484, 0),
                "Move to Seers' Village.");
        burnMaple = new ItemStep(this, "Burn some maple logs with a bow.", bow.highlighted(), mapleLogs.highlighted());
        fancyStone = new NpcStep(this, NpcID.ESTATE_AGENT, new WorldPoint(2735, 3500, 0),
                "TALK to the estate agent to redecorate your house. Must be done through dialog.", coins.quantity(25000));
        fancyStone.addDialogStep("Can you redecorate my house please?");
        moveToShadow = new ObjectStep(this, 6560, new WorldPoint(2547, 3421, 0),
                "Go down the ladder.", ringOfVis.equipped());
        shadowHound = new NpcStep(this, NpcID.SHADOW_HOUND, new WorldPoint(2699, 5095, 0),
                "Kill a shadow hound.", true);
        catchStur = new NpcStep(this, NpcID.FISHING_SPOT_1542, new WorldPoint(2501, 3504, 0),
                "Catch a leaping Sturgeon.", true);
        addySpear = new ObjectStep(this, ObjectID.BARBARIAN_ANVIL, new WorldPoint(2502, 3485, 0),
                "Smith an adamant spear.", addyBar, yewLogs);
        addySpear.addIcon(ItemID.ADAMANTITE_BAR);
        moveToWhirl = new ObjectStep(this, 25274, new WorldPoint(2512, 3508, 0),
                "Jump in the whirl pool.");
        moveToAncient2 = new ObjectStep(this, ObjectID.STAIRS_25338, new WorldPoint(1770, 5366, 1),
                "Go down the stairs.");
        moveToAncient3 = new ObjectStep(this, ObjectID.STAIRS_25339, new WorldPoint(1778, 5345, 0),
                "Go up the stairs.");
        mithrilDrag = new NpcStep(this, NpcID.MITHRIL_DRAGON, new WorldPoint(1779, 5344, 1),
                "Kill a mithril dragon.", true);
        buyGranite = new NpcStep(this, NpcID.COMMANDER_CONNAD, new WorldPoint(2535, 3576, 0),
                "Buy the granite body. (Requires at least 1 Penance Queen kill)", coins.quantity(95000));

        claimReward = new NpcStep(this, NpcID.THE_WEDGE, new WorldPoint(2760, 3476, 0),
                "Talk to the 'Wedge' infront of camelot castle to claim your reward!");
        claimReward.addDialogStep("I have a question about my Achievement Diary.");
    }

    @Override
    public List<ItemRequirement> getItemRequirements()
    {
        //setup
        return Arrays.asList(barbRod, feather, axe, bowString, knife, cosmicRune.quantity(3), waterRune.quantity(30), unpoweredOrb, dustyKey, mapleLogs, bow, ringOfVis, coins, addyBar, hammer, yewLogs, combatGear);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        //setup
        return Arrays.asList(food);
    }

    @Override
    public List<Requirement> getGeneralRequirements()
    {
        ArrayList<Requirement> req = new ArrayList<>();
        req.add(new SkillRequirement(Skill.AGILITY, 60, true));
        req.add(new SkillRequirement(Skill.CONSTRUCTION, 50));
        req.add(new SkillRequirement(Skill.DEFENCE, 70));
        req.add(new SkillRequirement(Skill.FIREMAKING, 65));
        req.add(new SkillRequirement(Skill.FISHING, 70, true));
        req.add(new SkillRequirement(Skill.FLETCHING, 70, true));
        req.add(new SkillRequirement(Skill.PRAYER, 70));
        req.add(new SkillRequirement(Skill.MAGIC, 56));
        req.add(new SkillRequirement(Skill.SMITHING, 75, true));
        req.add(new SkillRequirement(Skill.STRENGTH, 50));
        req.add(new SkillRequirement(Skill.THIEVING, 53));
        req.add(new SkillRequirement(Skill.WOODCUTTING, 60, true));

        return req;
    }

    @Override
    public List<String> getCombatRequirements()
    {
        return Collections.singletonList("Shadow Hound (Level 63), Mithril Dragon (level 304)");
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Charge Water Orb", Arrays.asList(moveToTavDungeon, moveToOb, waterOrb), waterRune.quantity(30), cosmicRune.quantity(3), unpoweredOrb, dustyKey));
        allSteps.add(new PanelDetails("Seers' Village Rooftop", Arrays.asList(seersRooftop)));
        allSteps.add(new PanelDetails("Yew Longbow", Arrays.asList(yewLong, cutLongbow, stringBow), axe, bowString, knife));
        allSteps.add(new PanelDetails("Piety", Arrays.asList(pietyCourt), knightWaves));
        allSteps.add(new PanelDetails("Burn Maple", Arrays.asList(moveToSeers, burnMaple), barbFiremaking, mapleLogs, bow));
        allSteps.add(new PanelDetails("Fancy Stone", Arrays.asList(fancyStone), coins.quantity(25000)));
        allSteps.add(new PanelDetails("Shadow Hound", Arrays.asList(moveToShadow, shadowHound), desertTreasure, ringOfVis, combatGear, food));
        allSteps.add(new PanelDetails("Fish Leaping Sturgeon", Arrays.asList(catchStur), barbFishing, barbRod, feather));
        allSteps.add(new PanelDetails("Smith Adamant Spear", Arrays.asList(addySpear), barbSmithing, taiBwoWannai, yewLogs, addyBar, hammer));
        allSteps.add(new PanelDetails("Mithril Dragon", Arrays.asList(moveToWhirl, moveToAncient2, moveToAncient3, mithrilDrag), barbFiremaking, combatGear, food));
        allSteps.add(new PanelDetails("Granite Body", Arrays.asList(buyGranite), coins.quantity(95000), combatGear));
        allSteps.add(new PanelDetails("Finishing off", Arrays.asList(claimReward)));

        return allSteps;
    }
}
