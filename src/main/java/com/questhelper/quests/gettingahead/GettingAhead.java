/*
 * Copyright (c) 2020, Zoinkwiz
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

package com.questhelper.quests.gettingahead;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.runelite.api.*;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.GETTING_AHEAD
)
public class GettingAhead extends BasicQuestHelper{
    //Items Recommended
    ItemRequirement food, staminaPotions, combatGear, skillsNeck,clayHead,furHead,bloodyHead;

    //Items Required
    ItemRequirement bearFur,softClay,hammer,saw,planks,nails,knife,redDye,potOfFlour,needle,thread;

    //NPC Steps
    QuestStep talkToGordon1,talkToMary,talkToGordon2,talkToMary2,talkToGordonGen,talkToGordonGen2,talkToGordonGen3,talkToGordonFinal;

    //Object/items Steps
    QuestStep usePotOfFlour,goToMine,leaveCave,killBeast,useKnifeOnClay,useFurOnHead,useDyeOnHead,buildBearHead;

    //Conditions
    ConditionForStep inMine,hasClayHead,hasFurHead,hasBloodyHead;

    //Zones
    Zone kebosMine;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        loadZones();
        setupItemRequirements();
        setupConditions();
        setupSteps();
        Map<Integer, QuestStep> steps = new HashMap<>();

        steps.put(0,talkToGordon1);
        steps.put(2,talkToMary);
        steps.put(4,usePotOfFlour);

        ConditionalStep killBeastStep = new ConditionalStep(this, goToMine);
        killBeastStep.addStep(inMine,killBeast);
        steps.put(6,killBeastStep);
        steps.put(8,killBeastStep);
        steps.put(10,killBeastStep);
        steps.put(12,killBeastStep);

        ConditionalStep returnToGordon = new ConditionalStep(this,talkToGordon2);
        returnToGordon.addStep(inMine,leaveCave);
        steps.put(14,returnToGordon);
        steps.put(16,talkToMary2);
        steps.put(18,useKnifeOnClay);
        steps.put(20,talkToGordonGen);
        steps.put(22,useFurOnHead);
        steps.put(24,talkToGordonGen2);
        steps.put(26,useDyeOnHead);
        steps.put(28,talkToGordonGen3);
        steps.put(30,buildBearHead);
        steps.put(32,talkToGordonFinal);

        return steps;
    }

    public void setupItemRequirements()
    {
        //Recommended
        combatGear = new ItemRequirement("Combat gear", -1, -1);
        food = new ItemRequirement("Food", -1, -1);
        staminaPotions = new ItemRequirement("Stamina Potion", -1, -1);
        skillsNeck = new ItemRequirement("Skills Necklace", -1, 1);

        //Required
        bearFur = new ItemRequirement("Bear Fur",ItemID.BEAR_FUR);
        bearFur.setHighlightInInventory(true);
        softClay = new ItemRequirement("Soft Clay",ItemID.SOFT_CLAY,1);
        softClay.setHighlightInInventory(true);
        hammer = new ItemRequirement("Hammer",ItemID.HAMMER,1);
        saw = new ItemRequirement("Saw",ItemID.SAW,1);
        planks = new ItemRequirement("Planks",ItemID.PLANK,2);
        nails = new ItemRequirement("Nails",ItemCollections.getNails(),6);
        knife = new ItemRequirement("Knife",ItemID.KNIFE,1);
        knife.setHighlightInInventory(true);
        redDye = new ItemRequirement("Red Dye", ItemID.RED_DYE,1);
        redDye.setHighlightInInventory(true);
        potOfFlour = new ItemRequirement("Pot of Flour",ItemID.POT_OF_FLOUR,1);
        potOfFlour.setHighlightInInventory(true);
        needle = new ItemRequirement("Needle",ItemID.NEEDLE,1);
        thread = new ItemRequirement("Thread",ItemID.THREAD,1);

        //Making the fake had
        clayHead = new ItemRequirement("Clay Head",ItemID.CLAY_HEAD);
        clayHead.setHighlightInInventory(true);
        furHead = new ItemRequirement("Fur Head",ItemID.FUR_HEAD);
        furHead.setHighlightInInventory(true);
        bloodyHead = new ItemRequirement("Bloody Head",ItemID.BLOODY_HEAD);
    }

    public void setupConditions()
    {
        inMine = new ZoneCondition(kebosMine);

        hasClayHead = new ItemRequirementCondition(clayHead);
        hasFurHead = new ItemRequirementCondition(furHead);
        hasBloodyHead = new ItemRequirementCondition(bloodyHead);
    }

    public void loadZones()
    {
        kebosMine = new Zone(new WorldPoint(1174, 10000, 0), new WorldPoint(1215, 10035, 0));
    }

    public void setupSteps()
    {
        //Starting Off
        talkToGordon1 = new NpcStep(this,NpcID.GORDON,new WorldPoint(1248,3686,0),"Talk to Gordon south of the Farming Guild.");
        talkToGordon1.addDialogStep("Need a hand with anything?");
        talkToGordon1.addDialogStep("Yes.");

        talkToMary = new NpcStep(this,NpcID.MARY_10502,new WorldPoint(1237,3678,0),"Talk to Mary inside the main building, west of Gordon.");

        //Killing the beast
        usePotOfFlour = new ObjectStep(this, NullObjectID.NULL_40427, new WorldPoint(1257, 3686, 0), "Use the pot of flour on the gate.",potOfFlour);
        usePotOfFlour.addIcon(ItemID.POT_OF_FLOUR);

        goToMine = new ObjectStep(this,ObjectID.CAVE_20852,new WorldPoint(1212,3647,0),"Enter the Kebos Lowlands mine just west of the bridge and kill the Headless Beast.");
        goToMine.addDialogStep("Yes.");
        killBeast = new NpcStep(this,NpcID.HEADLESS_BEAST_10506,new WorldPoint(1191,10021,0),"Kill the headless beast.");
        goToMine.addSubSteps(killBeast);

        talkToGordon2 = new NpcStep(this,NpcID.GORDON,new WorldPoint(1248,3686,0),"Return to Gordon and talk to him.");
        leaveCave = new ObjectStep(this,ObjectID.CAVE_20853,new WorldPoint(1190,10029,0),"Leave the cave");
        talkToGordon2.addSubSteps(leaveCave);

        //Making the fake head
        talkToGordonGen = new NpcStep(this,NpcID.GORDON,new WorldPoint(1248,3686,0),"Talk with Gordon.");
        talkToGordonGen2 = new NpcStep(this,NpcID.GORDON,new WorldPoint(1248,3686,0),"Talk with Gordon.");
        talkToGordonGen3 = new NpcStep(this,NpcID.GORDON,new WorldPoint(1248,3686,0),"Talk with Gordon.");

        talkToMary2 = new NpcStep(this,NpcID.MARY_10502,new WorldPoint(1237,3678,0),"Talk to Mary inside the house.");
        useKnifeOnClay = new DetailedQuestStep(this,"Use the knife on the soft clay then talk to Gordon.",knife,softClay);
        useKnifeOnClay.addDialogStep("Yes.");
        useKnifeOnClay.addSubSteps(talkToGordonGen);

        useFurOnHead = new DetailedQuestStep(this,"Use the bear fur on the clay head then talk to Gordon.",bearFur,clayHead,thread,needle);
        useFurOnHead.addDialogStep("Yes.");
        useFurOnHead.addSubSteps(talkToGordonGen2);

        useDyeOnHead = new DetailedQuestStep(this,"Use the red dye on the fur head then talk to Gordon.",redDye,furHead);
        useDyeOnHead.addDialogStep("Yes.");
        useDyeOnHead.addSubSteps(talkToGordonGen3);

        buildBearHead = new ObjectStep(this,NullObjectID.NULL_20858,new WorldPoint(1240,3683,0),"Go inside and build the Mounted Head Space.",bloodyHead,nails,planks,saw,hammer);
        buildBearHead.addDialogStep("Yes.");
        talkToGordonFinal = new NpcStep(this,NpcID.GORDON,new WorldPoint(1248,3686,0),"Return to Gordon. Talk with him to finish the quest.");
    }

    @Override
    public ArrayList<ItemRequirement> getItemRequirements()
    {
        return new ArrayList<>(Arrays.asList(bearFur,softClay,hammer,saw,planks,nails,knife,redDye,potOfFlour,needle,thread));
    }

    @Override
    public ArrayList<ItemRequirement> getItemRecommended()
    {
        return new ArrayList<>(Arrays.asList(food, staminaPotions, combatGear, skillsNeck));
    }

    @Override
    public ArrayList<String> getCombatRequirements()
    {
        return new ArrayList<>(Arrays.asList("Headless Beast (level 82, safespottable)"));
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToGordon1,talkToMary))));
        allSteps.add(new PanelDetails("Killing the Beast",new ArrayList<>(Arrays.asList(usePotOfFlour,goToMine,talkToGordon2))));
        allSteps.add(new PanelDetails("Making the fake head",new ArrayList<>(Arrays.asList(talkToMary2,useKnifeOnClay,useFurOnHead,useDyeOnHead,buildBearHead,talkToGordonFinal))));
        return allSteps;
    }
}
