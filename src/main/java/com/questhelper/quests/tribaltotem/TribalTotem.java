/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2020, Twinkle <https://github.com/twinkle-is-dum>
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
package com.questhelper.quests.tribaltotem;

import com.google.common.collect.ImmutableMap;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.TRIBAL_TOTEM
)
public class TribalTotem extends BasicQuestHelper
{
    //Items Required
    ItemRequirement addressLabel, totem;

    //Items Recommended
    ItemRequirement coins, amuletOfGlory, ardougneTeleports;

    QuestStep talkToKangaiMau, investigateCrate, useLabel, talkToEmployee, talkToCromperty, enterPassword, solvePassword, climbStairs, searchChest, leaveHouse, talkToKangaiMauAgain;

    Requirement inEntrance, inMiddleRoom, openedLockWidget, inStairway, investigatedStairs, isUpstairs, chestOpened;

    //Zones
    Zone houseGroundFloorEntrance, houseGroundFloorMiddleRoom, houseGroundFloor, houseFirstFloor;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        loadZones();
        setupRequirements();
        setupConditions();
        setupSteps();

        ConditionalStep useLabelOnCrate = new ConditionalStep(this, investigateCrate);
        useLabelOnCrate.addStep(addressLabel, useLabel);

        ConditionalStep navigateMansion = new ConditionalStep(this, talkToCromperty);
        navigateMansion.addStep(totem.alsoCheckBank(questBank), talkToKangaiMauAgain);
        navigateMansion.addStep(new Conditions(openedLockWidget, inMiddleRoom), solvePassword);
        navigateMansion.addStep(inStairway, climbStairs);
        navigateMansion.addStep(isUpstairs, searchChest);
        navigateMansion.addStep(inEntrance, enterPassword);
        navigateMansion.addStep(inMiddleRoom, enterPassword);

        return new ImmutableMap.Builder<Integer, QuestStep>()
                .put(0, talkToKangaiMau)
                .put(1, useLabelOnCrate)
                .put(2, talkToEmployee)
                .put(3, talkToCromperty)
                .put(4, navigateMansion)
                .build();
    }

 @Override
 public void setupRequirements()
    {
        coins = new ItemRequirement("Coins or more for boat trips", ItemCollections.COINS, 90);
        amuletOfGlory = new ItemRequirement("Amulet of glory", ItemCollections.AMULET_OF_GLORIES);
        ardougneTeleports = new ItemRequirement("Ardougne teleports", ItemID.ARDOUGNE_TELEPORT);
        addressLabel = new ItemRequirement("Address label", ItemID.ADDRESS_LABEL);
        addressLabel.setHighlightInInventory(true);
        totem = new ItemRequirement("Totem", ItemID.TOTEM);
    }

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        ArrayList<ItemRequirement> reqs = new ArrayList<>();
        reqs.add(coins);
        reqs.add(amuletOfGlory);
        reqs.add(ardougneTeleports);
        return reqs;
    }

    public void loadZones()
    {
        houseGroundFloorEntrance = new Zone(new WorldPoint(2637, 3320, 0), new WorldPoint(2639, 3325, 0));
        houseGroundFloorMiddleRoom = new Zone(new WorldPoint(2634, 3322, 0), new WorldPoint(2636, 3324, 0));
        houseGroundFloor = new Zone(new WorldPoint(2629, 3321, 0), new WorldPoint(2633, 3325, 0));
        houseFirstFloor = new Zone(new WorldPoint(2630, 3318, 1), new WorldPoint(2639, 3323, 1));
    }

    public void setupConditions()
    {
        inEntrance = new ZoneRequirement(houseGroundFloorEntrance);
        inMiddleRoom = new ZoneRequirement(houseGroundFloorMiddleRoom);
        openedLockWidget = new WidgetTextRequirement(369, 54,"Combination Lock Door");
        inStairway = new ZoneRequirement(houseGroundFloor);
        investigatedStairs = new WidgetTextRequirement(229, 1, "Your trained senses as a thief enable you to see that there is a trap<br>in these stairs. You make a note of its location for future reference<br>when using these stairs.");
        isUpstairs = new ZoneRequirement(houseFirstFloor);
        chestOpened = new ObjectCondition(ObjectID.CHEST_2710);
    }

    public void setupSteps()
    {
        talkToKangaiMau = new NpcStep(this, NpcID.KANGAI_MAU, new WorldPoint(2794, 3182, 0), "Talk to Kangai Mau in the Brimhaven food store.");
        talkToKangaiMau.addDialogSteps("I'm in search of adventure!", "Ok, I will get it back.");

        investigateCrate = new ObjectStep(this, ObjectID.CRATE_2707, new WorldPoint(2650, 3273, 0), "Travel to the GPDT depot in Ardougne and investigate the most northeastern crate for a label.");
        useLabel = new ObjectStep(this, ObjectID.CRATE_2708, new WorldPoint(2650, 3271, 0), "Use the label on the highlighted crate.", addressLabel);
        useLabel.addIcon(ItemID.ADDRESS_LABEL);
        talkToEmployee = new NpcStep(this, NpcID.GPDT_EMPLOYEE, new WorldPoint(2647, 3272, 0), "Talk to a nearby GPDT employee.", true);
        talkToEmployee.addDialogStep("So, when are you going to deliver this crate?");

        talkToCromperty = new NpcStep(this, NpcID.WIZARD_CROMPERTY, new WorldPoint(2683, 3326, 0), "Talk to Wizard Cromperty in north east Ardougne.");
        talkToCromperty.addDialogSteps("Chat.", "So what have you invented?", "Can I be teleported please?", "Yes, that sounds good. Teleport me!");

        enterPassword = new ObjectStep(this, ObjectID.DOOR_2705, new WorldPoint(2634, 3323, 0), "Try and open the door to the west.");
        solvePassword = new PuzzleStep(this);
        climbStairs = new ObjectStep(this, ObjectID.STAIRS_2711, new WorldPoint(2632, 3323, 0), "FIRST Right-click 'Investigate' the stairs to disable a trap, then climb the stairs.");

        searchChest = new ObjectStep(this, ObjectID.CHEST_2709, new WorldPoint(2638, 3324, 1), "Open the chest on the top floor and search it for the totem.");
        ((ObjectStep)(searchChest)).addAlternateObjects(ObjectID.CHEST_2710);

        leaveHouse = new DetailedQuestStep(this, "Travel back to Brimhaven.");
        talkToKangaiMauAgain = new NpcStep(this, NpcID.KANGAI_MAU, new WorldPoint(2794, 3182, 0), "Return to Kangai Mau in Brimhaven.", totem);
    }

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.THIEVING, 21, true));
	}

    @Override
    public QuestPointReward getQuestPointReward()
    {
        return new QuestPointReward(1);
    }

    @Override
    public List<ExperienceReward> getExperienceRewards()
    {
        return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 1775));
    }

    @Override
    public List<ItemReward> getItemRewards()
    {
        return Collections.singletonList(new ItemReward("Swordfish", ItemID.SWORDFISH, 5));
    }

    @Override
    public List<PanelDetails> getPanels()
    {
        List<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Retrieving the totem",
                Arrays.asList(talkToKangaiMau, investigateCrate, useLabel, talkToEmployee, talkToCromperty, enterPassword, solvePassword, climbStairs, searchChest, talkToKangaiMauAgain)));
        return allSteps;
    }
}