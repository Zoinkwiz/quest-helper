/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
        quest = QuestHelperQuest.TRIBAL_TOTEM
)
public class TribalTotem extends BasicQuestHelper
{
    ItemRequirement coins, amuletOfGlory, ardougneTeleports, addressLabel, totem;

    QuestStep talkToKangaiMau, investigateCrate, useLabel, talkToEmployee, talkToCromperty, enterPassword, solvePassword, climbStairs, searchChest, leaveHouse, talkToKangaiMauAgain;

    ConditionForStep hasLabel, openedLockWidget, inStairway, investigatedStairs, isUpstairs, chestOpened, hasTotem;

    Zone houseGroundFloor, houseFirstFloor;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        loadZones();
        setupItemRequirements();
        setupConditions();
        setupSteps();
        Map<Integer, QuestStep> steps = new HashMap<>();

        steps.put(0, talkToKangaiMau);

        ConditionalStep useLabelOnCrate = new ConditionalStep(this, investigateCrate);
        useLabelOnCrate.addStep(hasLabel, useLabel);
        steps.put(1, useLabelOnCrate);

        steps.put(2, talkToEmployee);
        steps.put(3, talkToCromperty);

        ConditionalStep solveCombinationLock = new ConditionalStep(this, enterPassword);
        solveCombinationLock.addStep(hasTotem, talkToKangaiMauAgain);
        solveCombinationLock.addStep(openedLockWidget, solvePassword);
        solveCombinationLock.addStep(inStairway, climbStairs);
        solveCombinationLock.addStep(isUpstairs, searchChest);
        steps.put(4, solveCombinationLock);

        return steps;
    }

    public void setupItemRequirements()
    {
        coins = new ItemRequirement("Coins", ItemID.COINS, 90);
        amuletOfGlory = new ItemRequirement("Amulet of glory", ItemCollections.getAmuletOfGlories());
        ardougneTeleports = new ItemRequirement("Teleports to Ardougne", ItemID.ARDOUGNE_TELEPORT);
        addressLabel = new ItemRequirement("Address label", ItemID.ADDRESS_LABEL);
        totem = new ItemRequirement("Totem", ItemID.TOTEM);
    }

    @Override
    public ArrayList<ItemRequirement> getItemRecommended()
    {
        ArrayList<ItemRequirement> reqs = new ArrayList<>();
        reqs.add(coins);
        reqs.add(amuletOfGlory);
        reqs.add(ardougneTeleports);
        return reqs;
    }

    public void loadZones()
    {
        houseGroundFloor = new Zone(new WorldPoint(2629, 3321, 0), new WorldPoint(2633, 3325, 0));
        houseFirstFloor = new Zone(new WorldPoint(2630, 3319, 1), new WorldPoint(2638, 3323, 1));
    }

    public void setupConditions()
    {
        hasLabel = new ItemRequirementCondition(addressLabel);
        openedLockWidget = new WidgetTextCondition(369, 54,"Combination Lock Door");
        inStairway = new ZoneCondition(houseGroundFloor);
        investigatedStairs = new WidgetTextCondition(229, 1, "Your trained senses as a thief enable you to see that there is a trap<br>in these stairs. You make a note of its location for future reference<br>when using these stairs.");
        isUpstairs = new ZoneCondition(houseFirstFloor);
        chestOpened = new ObjectCondition(ObjectID.CHEST_2710);
        hasTotem = new ItemRequirementCondition(totem);
    }

    public void setupSteps()
    {
        talkToKangaiMau = new NpcStep(this, NpcID.KANGAI_MAU, new WorldPoint(2794, 3182, 0), "Talk to Kangai Mau in the Brimhaven food store.");
        talkToKangaiMau.addDialogSteps("I'm in search of adventure!", "Ok, I will get it back.");

        investigateCrate = new ObjectStep(this, ObjectID.CRATE_2707, new WorldPoint(2650, 3273, 0), "Travel to the GPDT depot in Ardougne and investigate the most northeastern crate for a label.");
        useLabel = new ObjectStep(this, ObjectID.CRATE_2708, new WorldPoint(2650, 3271, 0), "Use the label on the highlighted crate.", addressLabel);
        useLabel.addIcon(ItemID.ADDRESS_LABEL);
        talkToEmployee = new NpcStep(this, NpcID.GPDT_EMPLOYEE, new WorldPoint(2647, 3272, 0), "Talk to a GPDT employee.");
        talkToEmployee.addDialogStep("So, when are you going to deliver this crate?");

        talkToCromperty = new NpcStep(this, NpcID.WIZARD_CROMPERTY, new WorldPoint(2683, 3326, 0), "Talk to Wizard Cromperty.");
        talkToCromperty.addDialogSteps("Chat.", "So what have you invented?", "Can I be teleported please?", "Yes, that sounds good. Teleport me!");

        enterPassword = new ObjectStep(this, ObjectID.DOOR_2705, new WorldPoint(2634, 3323, 0), "Go west 2 doors and enter the password KURT.");
        solvePassword = new CombinationLockStep(this);
        climbStairs = new ObjectStep(this, ObjectID.STAIRS_2711, new WorldPoint(2632, 3323, 0), "Right-click 'Investigate' the stairs, then climb the stairs.");

        searchChest = new ObjectStep(this, ObjectID.CHEST_2709, new WorldPoint(2638, 3324, 1), "Open the chest on the top floor and search it for the totem.");
        ((ObjectStep)(searchChest)).addAlternateObjects(ObjectID.CHEST_2710);

        leaveHouse = new DetailedQuestStep(this, "Travel back to Brimhaven.");
        talkToKangaiMauAgain = new NpcStep(this, NpcID.KANGAI_MAU, new WorldPoint(2794, 3182, 0), "Talk to Kangai Mau.", totem);
    }

    @Override
    public ArrayList<ItemRequirement> getItemRequirements()
    {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Retrieving the totem",
                new ArrayList<>(Arrays.asList(talkToKangaiMau, investigateCrate, useLabel, talkToEmployee, talkToCromperty, enterPassword, solvePassword, climbStairs, searchChest, talkToKangaiMauAgain))));

        return allSteps;
    }
}