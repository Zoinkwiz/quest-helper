/*
 * Copyright (c) 2020, Snoodelz
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
package com.questhelper.quests.clocktower;

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

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
        quest = QuestHelperQuest.CLOCK_TOWER
)
public class ClockTower extends BasicQuestHelper
{
    ItemRequirement bucketOfWater, noteAboutWater, staminaPotions, ardougneCloak, redCog, blueCog, blackCog, whiteCog, ratPoison;
    Zone groundFloor, basement, firstFloor, secondFloor;
    ConditionForStep inBasement, inGroundFloor, inFirstFloor, inSecondFloor, hasRedCog, hasBlueCog, hasBlackCog, hasWhiteCog, hasRatPoison, firstLeverDown, pulledFirstLeverUp, secondLeverUp, pulledSecondLeverDown;
    QuestStep talkToKojo, enterClockTower, goToBasement, southEasternDoor, pickUpRedCog, goToGroundFloor, redCogOnRedSpindle, goToLadderCedric, pushWall, climbBlueLadder,
            pickUpBlueCog, goToFirstFloor, blueCogOnBlueSpindle, goToGroundFloorFromFirstFloor, northEasternDoor, pickupBlackCog, blackCogOnBlackSpindle, northWesternDoor,
            pickUpRatPoison, pullFirstLever, pullSecondLever, ratPoisonFood, westernGate, pickUpWhiteCog, climbWhiteLadder, goToSecondFloor, whiteCogOnWhiteSpindle, gotoFirstFloorFromSecondFloor, kojoReward;
    ConditionalStep startingOut, getRedCog, getBlueCog, getBlackCog, getWhiteCog, finishingOff, getAllCogs;


    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        setupItemRequirements();
        setupZones();
        setupConditions();
        setupSteps();

        Map<Integer, QuestStep> steps = new HashMap<>();

        steps.put(0, talkToKojo);
        startingOut = new ConditionalStep(this, talkToKojo);
        startingOut.addStep(inGroundFloor, talkToKojo);
        startingOut.addStep(inGroundFloor, goToBasement);
        getRedCog = new ConditionalStep(this, southEasternDoor);
        getRedCog.addStep(inBasement, pickUpRedCog);
        getRedCog.addStep(new Conditions(hasRedCog, inGroundFloor), redCogOnRedSpindle);
        getBlueCog = new ConditionalStep(this, goToLadderCedric);
        getBlueCog.addStep(inBasement, pushWall);
        getBlueCog.addStep(inBasement, pickUpBlueCog);
        getBlueCog.addStep(inBasement, climbBlueLadder);
        getBlueCog.addStep(inGroundFloor, goToFirstFloor);
        getBlueCog.addStep(new Conditions(inFirstFloor, hasBlueCog), blueCogOnBlueSpindle);
        getBlueCog.addStep(inFirstFloor, goToGroundFloorFromFirstFloor);
        getBlackCog = new ConditionalStep(this, goToBasement);
        getBlackCog.addStep(inBasement, northEasternDoor);
        getBlackCog.addStep(inBasement, pickupBlackCog);
        getBlackCog.addStep(inBasement, blackCogOnBlackSpindle);
        getWhiteCog = new ConditionalStep(this, northWesternDoor);
        getWhiteCog.addStep(inBasement, pickUpRatPoison);
        getWhiteCog.addStep(new Conditions(inBasement, firstLeverDown), pullFirstLever);
        getWhiteCog.addStep(new Conditions(inBasement, secondLeverUp), pullSecondLever);
        getWhiteCog.addStep(new Conditions(inBasement, hasRatPoison), ratPoisonFood);
        getWhiteCog.addStep(inBasement, westernGate);
        getWhiteCog.addStep(inBasement, pickUpWhiteCog);
        getWhiteCog.addStep(inBasement, climbWhiteLadder);
        getWhiteCog.addStep(inGroundFloor, goToFirstFloor);
        getWhiteCog.addStep(inFirstFloor, goToSecondFloor);
        getWhiteCog.addStep(new Conditions(inSecondFloor, hasWhiteCog), whiteCogOnWhiteSpindle);
        getWhiteCog.addStep(inSecondFloor, gotoFirstFloorFromSecondFloor);
        getWhiteCog.addStep(inFirstFloor, goToGroundFloorFromFirstFloor);
        finishingOff = new ConditionalStep(this, kojoReward);

        return steps;
    }

    public void setupItemRequirements()
    {
        bucketOfWater = new ItemRequirement("Bucket of Water or a pair of ice gloves", ItemID.BUCKET_OF_WATER);
        noteAboutWater = new ItemRequirement("There's a bucket and a well and just next to brother cedric for the black cog", -1, -1);
        staminaPotions = new ItemRequirement("Stamina Potions", ItemID.STAMINA_POTION4);
        ardougneCloak = new ItemRequirement("Ardougne Cloak", ItemID.ARDOUGNE_CLOAK);
        redCog = new ItemRequirement("Red Cog", ItemID.RED_COG);
        redCog.setHighlightInInventory(true);
        blueCog = new ItemRequirement("Blue Cog", ItemID.BLUE_COG);
        blueCog.setHighlightInInventory(true);
        blackCog = new ItemRequirement("Black Cog", ItemID.BLACK_COG);
        blackCog.setHighlightInInventory(true);
        whiteCog = new ItemRequirement("White Cog", ItemID.WHITE_COG);
        whiteCog.setHighlightInInventory(true);
        ratPoison = new ItemRequirement("Rat Poison", ItemID.RAT_POISON);
        ratPoison.setHighlightInInventory(true);

    }

    public void setupZones()
    {
        secondFloor = new Zone(new WorldPoint(2563, 3239, 2), new WorldPoint(2573, 3245, 2));
        firstFloor = new Zone(new WorldPoint(2563, 3239, 1), new WorldPoint(2573, 3245, 1));
        groundFloor = new Zone(new WorldPoint(2574, 3255, 0), new WorldPoint(2563, 3233, 0));
        basement = new Zone(new WorldPoint(2000, 9573, 0), new WorldPoint(3000, 9700, 0));
    }

    public void setupConditions()
    {
        inSecondFloor = new ZoneCondition(secondFloor);
        inFirstFloor = new ZoneCondition(firstFloor);
        inGroundFloor = new ZoneCondition(groundFloor);
        inBasement = new ZoneCondition(basement);
        hasRedCog = new ItemRequirementCondition(redCog);
        hasBlueCog = new ItemRequirementCondition(blueCog);
        hasBlackCog = new ItemRequirementCondition(blackCog);
        hasWhiteCog = new ItemRequirementCondition(whiteCog);
        hasRatPoison = new ItemRequirementCondition(ratPoison);
        firstLeverDown = new ObjectCondition(ObjectID.LEVER, new WorldPoint(2591, 9661, 0));
        pulledFirstLeverUp = new ObjectCondition(ObjectID.LEVER_35, new WorldPoint(2591, 9661, 0));
        secondLeverUp = new ObjectCondition(ObjectID.LEVER_34, new WorldPoint(2593, 9661, 0));
        pulledSecondLeverDown = new ObjectCondition(ObjectID.LEVER_36, new WorldPoint(2593, 9661, 0));
    }

    public void setupSteps()
    {
        enterClockTower= new ObjectStep(this, ObjectID.DOOR_1535, new WorldPoint(2574, 3250, 0), "Enter the clock tower.");
        talkToKojo = new NpcStep(this, NpcID.BROTHER_KOJO, new WorldPoint(2570, 3245, 0), "Talk to Brother Kojo at the clock tower.");
        talkToKojo.addDialogStep("OK old monk, what can I do?");
        goToBasement = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2566, 3242, 0), "Go down the ladder in the middle room.");
        southEasternDoor = new ObjectStep(this, ObjectID.DOOR_1535, new WorldPoint(2582, 9648, 0), "Head through the south-eastern door.");
        pickUpRedCog = new DetailedQuestStep(this, new WorldPoint(2583, 9613, 0), "Run through the path and pick up the red cog.", redCog);
        goToGroundFloor = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2566, 9642, 0), "Go back where you came from and up the ladder.");
        redCogOnRedSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_29, new WorldPoint(2568, 3243, 0), "Use the red cog on the red spindle.", redCog);

        goToLadderCedric = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2621, 3261, 0), "Go down the ladder east of Brother Cedric.");
        pushWall = new ObjectStep(this, ObjectID.WALL_1597, new WorldPoint(2575, 9631, 0), "Follow the tunnel and push the wall");
        pickUpBlueCog = new DetailedQuestStep(this, new WorldPoint(2574, 9633, 0), "Pick up the blue cog.", blueCog);
        climbBlueLadder = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2572, 9631, 0), "Climb up the ladder.");
        goToFirstFloor = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2572, 3241, 0), "Go inside the clock tower and go upstairs.");
        blueCogOnBlueSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_32, new WorldPoint(2569, 3240, 1), "Use the blue cog on the blue spindle.", blueCog);

        goToGroundFloorFromFirstFloor = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(2572, 3240, 1), "go back to the ground floor.");
        northEasternDoor = new ObjectStep(this, ObjectID.DOOR_1535, new WorldPoint(2582, 9651, 0), "Go through the north-eastern door.");
        pickupBlackCog = new DetailedQuestStep(this, new WorldPoint(2613, 9639, 0), "Use the bucket of water on the cog or wear ice gloves to pick up the the cog.", blackCog);
        blackCogOnBlackSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_30, new WorldPoint(2570, 9642, 0), "Go back to the entrance of the basement and use the black cog on the black spindle.", blackCog);

        northWesternDoor = new ObjectStep(this, ObjectID.DOOR_1535, new WorldPoint(2575, 9651, 0), "Go through the north-western door.");
        pickUpRatPoison = new DetailedQuestStep(this, new WorldPoint(2564, 9662, 0), "Pick up the rat poison, take an extra if you haven't completeted Ratcatchers", ratPoison);
        pullFirstLever = new ObjectStep(this, ObjectID.LEVER, new WorldPoint(2591, 9661, 0), "Pull the first lever up once.");
        pullSecondLever = new ObjectStep(this, ObjectID.LEVER_34, new WorldPoint(2593, 9661, 0), "Pull the second lever down once.");
        ratPoisonFood = new ObjectStep(this, ObjectID.FOOD_TROUGH, new WorldPoint(2586, 9654, 0), "Use the rat poison on the food through. Wait till the rats have died.");
        westernGate = new ObjectStep(this, ObjectID.GATE_39, new WorldPoint(2579, 9656, 0), "Go through gate");
        pickUpWhiteCog = new DetailedQuestStep(this, new WorldPoint(2577, 9655, 0), "Pick up the white cog.", whiteCog);
        climbWhiteLadder = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2575, 9655, 0), "Head up the ladder.");
        goToSecondFloor = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(2572, 3240, 1), "go up the stairs to the second floor.");
        whiteCogOnWhiteSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_31, new WorldPoint(2567, 3241, 2), "Use the white cog on the white spindle.", whiteCog);
        gotoFirstFloorFromSecondFloor = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(2572, 3241, 2), "Go down the staircase.");
        kojoReward = new NpcStep(this, NpcID.BROTHER_KOJO, new WorldPoint(2570, 3245, 0), "Talk to Brother Kojo for your reward.");
    }

    @Override
    public ArrayList<ItemRequirement> getItemRequirements()
    {
        ArrayList<ItemRequirement> reqs = new ArrayList<>();
        reqs.add(bucketOfWater);
        return reqs;
    }

//    @Override
//    public ArrayList<ItemRequirement> getItemRecommended()
//    {
//        ArrayList<ItemRequirement> recs = new ArrayList<>();
//        recs.add(staminaPotions);
//        recs.add(ardougneCloak);
//        return recs;
//    }


    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();

        PanelDetails gettingStarted = new PanelDetails("Getting Started", new ArrayList<>(Arrays.asList(enterClockTower, talkToKojo, goToBasement)));
        allSteps.add(gettingStarted);

        PanelDetails redCog = new PanelDetails("Obtaining the red cog", new ArrayList<>(Arrays.asList(southEasternDoor, pickUpRedCog, goToGroundFloor, redCogOnRedSpindle)));
        allSteps.add(redCog);
        PanelDetails blueCog = new PanelDetails("Obtaining the blue cog",
                new ArrayList<>(Arrays.asList(goToLadderCedric, pushWall, pickUpBlueCog, climbBlueLadder, goToFirstFloor, blueCogOnBlueSpindle)), noteAboutWater);
        allSteps.add(blueCog);
        PanelDetails blackCog = new PanelDetails("Obtaining the black cog",
                new ArrayList<>(Arrays.asList(goToGroundFloorFromFirstFloor, northEasternDoor, pickupBlackCog, blackCogOnBlackSpindle)), bucketOfWater);
        allSteps.add(blackCog);
        PanelDetails whiteCog = new PanelDetails("Obtaining the white cog",
                new ArrayList<>(Arrays.asList(northWesternDoor, pickUpRatPoison, pullFirstLever, pullSecondLever, ratPoisonFood, westernGate, pickUpWhiteCog, climbWhiteLadder, goToSecondFloor, whiteCogOnWhiteSpindle, gotoFirstFloorFromSecondFloor, goToGroundFloorFromFirstFloor)));
        allSteps.add(whiteCog);
        PanelDetails finishingOff = new PanelDetails("Finishing off",
                new ArrayList<>(Arrays.asList(kojoReward)));
        allSteps.add(finishingOff);

        return allSteps;
    }
}