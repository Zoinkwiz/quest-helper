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
package com.questhelper.quests.spiritsoftheelid;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.*;
import com.questhelper.steps.conditional.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;

@QuestDescriptor(
        quest = QuestHelperQuest.SPIRITS_OF_THE_ELID
)
public class SpiritsOfTheElid extends BasicQuestHelper
{
    //Items Requirements
    ItemRequirement airRune, lawRune, needle, thread, crushWep, stabWep, slashWep, lightSource,knife,rope,pickaxe,bow,arrows,tornRobeTop,tornRobeBottom,robeOfElidinisTop,robeOfElidinisBottom,ancestralKey,shoes,soles,statuette;

    //Items recommended
    ItemRequirement combatGear, waterskins,shantaypass,coins,spear,food,necklaceOfPassage;

    //Quest Steps -- Broken down for part
    QuestStep speakToAwusah,speakToGhaslor,openCuboard,useNeedleTornRobes,useNeedleTornRobesTop,telegrabKey;
    QuestStep enterCave,useAncestralKey,openStabDoor,openStabDoorAfterGolem,clearChannel,openSlashDoor,openSlashDoorAfterGolem,clearChannel2,openCrushDoor,openCrushDoorAfterGolem,clearChannel3,openFarNorthDoor,speakToSpirits;
    QuestStep speakToAwusah2,takeShoes,cutShoes,enterCrevice,talkToGenie,talkToGenieAgain,useStatuette;

    //Conditions
    ConditionForStep hasAncenstralKey,hasTornRobeTop,hasTornRobeBottom,hasRobeOfElidinisTop,hasRobeOfElidinisBottom,whiteGolem,greyGolem,blackGolem,stabChannel,slashChannel,crushChannel,hasShoes,hasSole;
    ConditionForStep inCaveEntrance,inWhiteGolemRoom,inGreyGolemRoom,inBlackGolemRoom,notAwusahHouse,insideCrevice;

    //Zones
    Zone riverElidCaveEntrance,whiteGolemRoom,greyGolemRoom,blackGolemRoom,outsideAwusahHouse,creviceOutsideNardah;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        setupItemRequirements();
        setupConditions();
        setupSteps();
        setupZones();
        Map<Integer, QuestStep> steps = new HashMap<>();

        steps.put(0,speakToAwusah);
        steps.put(10,speakToGhaslor);
        ConditionalStep getRobesAndKey = new ConditionalStep(this,openCuboard);
        getRobesAndKey.addStep(new Conditions(hasRobeOfElidinisTop,hasRobeOfElidinisBottom,hasAncenstralKey),enterCave);
        getRobesAndKey.addStep(new Conditions(hasRobeOfElidinisTop,hasRobeOfElidinisBottom),telegrabKey);
        getRobesAndKey.addStep(new Conditions(hasTornRobeTop,hasRobeOfElidinisBottom),useNeedleTornRobesTop);
        getRobesAndKey.addStep(new Conditions(hasTornRobeBottom,hasTornRobeTop),useNeedleTornRobes);
        steps.put(20,getRobesAndKey);

        steps.put(25,useAncestralKey);

        ConditionalStep clearChannels = new ConditionalStep(this,openStabDoor);
        clearChannels.addStep(crushChannel,openFarNorthDoor);
        clearChannels.addStep(inBlackGolemRoom,clearChannel3);
        clearChannels.addStep(blackGolem,openCrushDoorAfterGolem);
        clearChannels.addStep(slashChannel,openCrushDoor);
        clearChannels.addStep(inGreyGolemRoom,clearChannel2);
        clearChannels.addStep(greyGolem,openSlashDoorAfterGolem);
        clearChannels.addStep(stabChannel,openSlashDoor);
        clearChannels.addStep(inWhiteGolemRoom,clearChannel);
        clearChannels.addStep(whiteGolem,openStabDoorAfterGolem);
        steps.put(27,clearChannels);
        steps.put(30,speakToSpirits);
        steps.put(35,speakToAwusah2);

        ConditionalStep creviceSteps = new ConditionalStep(this,takeShoes);
        creviceSteps.addStep(insideCrevice,talkToGenie);
        creviceSteps.addStep(hasSole,enterCrevice);
        creviceSteps.addStep(new Conditions(hasShoes,notAwusahHouse),cutShoes);
        steps.put(40,creviceSteps);

        steps.put(50,talkToGenieAgain);
        steps.put(55,useStatuette);
        return steps;
    }

    public void setupItemRequirements()
    {
        //Requirements
        airRune = new ItemRequirement("Air Rune",ItemID.AIR_RUNE,1);
        lawRune = new ItemRequirement("Law Rune",ItemID.LAW_RUNE,1);
        needle = new ItemRequirement("Needle",ItemID.NEEDLE,1);
        needle.setHighlightInInventory(true);
        thread = new ItemRequirement("Thread", ItemID.THREAD,2);
        crushWep = new ItemRequirement("Crush Weapon Style",-1,1);
        stabWep = new ItemRequirement("Stab Weapon Style",-1,1);
        slashWep = new ItemRequirement("Slash Weapon Style",-1,1);
        lightSource = new ItemRequirement("Lightsource",ItemCollections.getLightSources(),1);
        knife = new ItemRequirement("Knife",ItemID.KNIFE,1);
        knife.setHighlightInInventory(true);
        rope = new ItemRequirement("Rope",ItemID.ROPE,1);
        rope.setHighlightInInventory(true);
        pickaxe = new ItemRequirement("Any Pickaxe", ItemCollections.getPickaxes(),1);
        bow = new ItemRequirement("Any bow", ItemCollections.getBows(),1,true);
        arrows = new ItemRequirement("Arrows for bow",ItemCollections.getMetalArrows(),1,true);

        //Quest Items
        tornRobeTop = new ItemRequirement("Torn Robe (top)",ItemID.TORN_ROBE,1);
        tornRobeTop.setHighlightInInventory(true);
        tornRobeBottom = new ItemRequirement("Torn Robe (bottom)",ItemID.TORN_ROBE_6789,1);
        tornRobeBottom.setHighlightInInventory(true);
        robeOfElidinisTop = new ItemRequirement("Robe of Elidinis (top)",ItemID.ROBE_OF_ELIDINIS,1,true);
        robeOfElidinisBottom = new ItemRequirement("Robe of Elidinis (bottom)",ItemID.ROBE_OF_ELIDINIS_6787,1, true);
        ancestralKey = new ItemRequirement("Ancestral Key",ItemID.ANCESTRAL_KEY,1);
        ancestralKey.setHighlightInInventory(true);
        shoes = new ItemRequirement("Shoes",ItemID.SHOES,1);
        shoes.setHighlightInInventory(true);
        soles = new ItemRequirement("Sole",ItemID.SOLE,1);
        statuette = new ItemRequirement("Statuette",ItemID.STATUETTE_6785,1);
        statuette.setHighlightInInventory(true);

        //Recommended
        combatGear = new ItemRequirement("Combat Gear",-1,1);
        waterskins = new ItemRequirement("Waterskins",-1,1);
        shantaypass = new ItemRequirement("Shantay Passes",-1,1);
        coins = new ItemRequirement("Coins for magic carpet",-1,1);
        spear = new ItemRequirement("Spear or Hasta for the 3 weapon styles",-1,1);
        food = new ItemRequirement("Food",-1,1);
        necklaceOfPassage = new ItemRequirement("Necklace of Passage",-1,1);
    }

    public void setupConditions()
    {
        hasTornRobeTop = new ItemRequirementCondition(tornRobeTop);
        hasTornRobeBottom = new ItemRequirementCondition(tornRobeBottom);
        hasRobeOfElidinisTop = new ItemRequirementCondition(robeOfElidinisTop);
        hasRobeOfElidinisBottom = new ItemRequirementCondition(robeOfElidinisBottom);
        hasAncenstralKey = new ItemRequirementCondition(ancestralKey);
        hasShoes = new ItemRequirementCondition(shoes);
        hasSole = new ItemRequirementCondition(soles);

        inCaveEntrance = new ZoneCondition(riverElidCaveEntrance);
        inWhiteGolemRoom = new ZoneCondition(whiteGolemRoom);
        inGreyGolemRoom = new ZoneCondition(greyGolemRoom);
        inBlackGolemRoom = new ZoneCondition(blackGolemRoom);
        notAwusahHouse = new ZoneCondition(outsideAwusahHouse);
        insideCrevice = new ZoneCondition(creviceOutsideNardah);

        whiteGolem = new VarbitCondition(1447,1);
        greyGolem = new VarbitCondition(1448,1);
        blackGolem = new VarbitCondition(1446,1);
        stabChannel = new VarbitCondition(1450,1);
        slashChannel = new VarbitCondition(1449,1);
        crushChannel = new VarbitCondition(1451,1);
    }

    public void setupSteps()
    {
        //Starting Off
        speakToAwusah = new NpcStep(this,NpcID.AWUSAH_THE_MAYOR,new WorldPoint(3443,2915,0),"Speak to Awusah the Mayor in Nardah.");
        speakToAwusah.addDialogSteps("I am an adventurer in search of quests.","Any idea how you got this curse?","Yes.","Ok I will have a look around and see what I can do.");
        speakToGhaslor = new NpcStep(this, NpcID.GHASLOR_THE_ELDER,new WorldPoint(3441,2932,0),"Speak to Ghaslor the Elder, just north of Awusah.");
        speakToGhaslor.addDialogSteps("I am trying to find out the cause of this town's curse.","River spirits, what are they?");
        openCuboard = new ObjectStep(this, ObjectID.CUPBOARD_10384,new WorldPoint(3420,2930,0),"Open and search the cupboard in the north-west corner of the house that is north of the fountain.");
        ((ObjectStep) (openCuboard)).addAlternateObjects(ObjectID.CUPBOARD_10385);
        useNeedleTornRobes = new DetailedQuestStep(this,"Use the needle and thread on the torn robes (both top and bottom).",needle,tornRobeBottom);
        useNeedleTornRobesTop = new DetailedQuestStep(this,"Use the needle and thread on the torn robes (both top and bottom).",needle,tornRobeTop);
        useNeedleTornRobes.addSubSteps(useNeedleTornRobesTop);
        telegrabKey = new ObjectStep(this,ObjectID.TABLE_10490,new WorldPoint(3432,2929,0),"Cast telegrab on the ancestral key on the table.",airRune,lawRune);

        //The Golems
        enterCave = new ObjectStep(this,ObjectID.ROOT_6382,new WorldPoint(3370,3132,0),"Enter the cave north-west of Nardah where the river turns into a waterfall.",rope);
        enterCave.addIcon(ItemID.ROPE);
        useAncestralKey = new ObjectStep(this,ObjectID.DOOR_10427,new WorldPoint(3353,9544,0),"Equip the Robes of Elidinis and use the ancestral key on the door.",ancestralKey,robeOfElidinisTop,robeOfElidinisBottom);
        ((ObjectStep) (useAncestralKey)).addAlternateObjects(ObjectID.DOOR_10429);
        useAncestralKey.addIcon(ItemID.ANCESTRAL_KEY);

        openStabDoor = new ObjectStep(this,ObjectID.DOOR_10420,new WorldPoint(3365,9542,0),"Open the door to the south and fight the White Golem (level-75).  You can only damage the Golem with the stab attack style.");
        openStabDoorAfterGolem = new ObjectStep(this,ObjectID.DOOR_10420,new WorldPoint(3365,9542,0),"Open the southern door again.");
        openStabDoor.addSubSteps(openStabDoorAfterGolem);
        clearChannel = new ObjectStep(this,ObjectID.WATER_CHANNEL_10407,new WorldPoint(3365,9538,0),"Clear the Water Channel then leave the room.");

        openSlashDoor = new ObjectStep(this,ObjectID.DOOR_10421,new WorldPoint(3374,9547,0),"Open the door to the east and fight the Grey Golem (level-75).  You can only damage the Golem with the slash attack style.");
        openSlashDoorAfterGolem = new ObjectStep(this,ObjectID.DOOR_10421,new WorldPoint(3374,9547,0),"Open the eastern door again.");
        openSlashDoor.addSubSteps(openSlashDoorAfterGolem);
        clearChannel2 = new ObjectStep(this,ObjectID.WATER_CHANNEL_10405,new WorldPoint(3378,9547,0),"Clear the Water Channel then leave the room.",pickaxe);

        openCrushDoor = new ObjectStep(this,ObjectID.DOOR_10419,new WorldPoint(3372,9556,0),"Open the door to the north-eastern and fight the Black Golem (level-75).  You can only damge the Golem with the crush attack style.");
        openCrushDoorAfterGolem = new ObjectStep(this,ObjectID.DOOR_10419,new WorldPoint(3372,9556,0),"Open the north-eastern door again.");
        openCrushDoor.addSubSteps(openCrushDoorAfterGolem);
        clearChannel3 = new NpcStep(this, NpcID.TARGET, new WorldPoint(3376,9557,0), "Clear the Water Channel by shooting the target then leave the room.",bow,arrows);

        openFarNorthDoor = new ObjectStep(this,ObjectID.DOOR_10423,new WorldPoint(3354,9558,0),"Open the door to the north near the lake.");
        speakToSpirits = new NpcStep(this,NpcID.NIRRIE,new WorldPoint(3364,9589,0), "Talk to the Water Spirits on the north side of the lake.",true);
        ((NpcStep) (speakToSpirits)).addAlternateNpcs(NpcID.TIRRIE);
        ((NpcStep) (speakToSpirits)).addAlternateNpcs(NpcID.HALLAK);

        speakToSpirits.addDialogSteps("I come as an emissary from the people of Nardah.","Is there anything they can do to get their fountain working again?");

        //The Genie
        speakToAwusah2 = new NpcStep(this,NpcID.AWUSAH_THE_MAYOR,new WorldPoint(3443,2915,0),"Return to Nardah and speak to Awusah the Mayor.");
        takeShoes = new ObjectStep(this, ItemID.SHOES, new WorldPoint(3439,2913,0),"Take the shoes near the doorway then leave Awusah's house.",shoes);
        cutShoes = new DetailedQuestStep(this,"Use a knife on the shoes to cut out the sole.",knife,shoes);
        enterCrevice = new ObjectStep(this,ObjectID.CREVICE_10416,new WorldPoint(3373,2905,0),"Enter the crevice to the west of Nardah.",rope,lightSource);
        enterCrevice.addIcon(ItemID.ROPE);
        talkToGenie = new NpcStep(this, NpcID.GENIE_4738,new WorldPoint(3371,9320,0),"Talk to the Genie in the Crevice.");
        talkToGenie.addDialogSteps("I'm after a statue that was thrown down here.","Maybe I can make a deal for it?","Ok I agree to the deal.");
        talkToGenieAgain = new NpcStep(this, NpcID.GENIE_4738,new WorldPoint(3371,9320,0),"Talk to the Genie again with the sole.",soles);
        useStatuette = new ObjectStep(this, NullObjectID.NULL_10389,new WorldPoint(3427,2930,0), "Return to Nardah and use the statuette on the statuette plinth in the northern house.",statuette);
        useStatuette.addIcon(ItemID.STATUETTE_6785);
    }

    public void setupZones()
    {
        riverElidCaveEntrance = new Zone(new WorldPoint(3343,9532,0),new WorldPoint(3356,9544,0));
        whiteGolemRoom = new Zone(new WorldPoint(3360,9535,0),new WorldPoint(3369,9542,0));
        greyGolemRoom = new Zone(new WorldPoint(3374,9543,0),new WorldPoint(3381,9550,0));
        blackGolemRoom = new Zone(new WorldPoint(3372,9553,0),new WorldPoint(3380,9561,0));
        outsideAwusahHouse = new Zone(new WorldPoint(3435,2885,0), new WorldPoint(3387,2949,0));
        creviceOutsideNardah = new Zone(new WorldPoint(3362,9298,0), new WorldPoint(3380,9329,0));
    }

    @Override
    public ArrayList<ItemRequirement> getItemRequirements()
    {
        return new ArrayList<>(Arrays.asList(airRune, lawRune,needle,thread,crushWep,stabWep,slashWep,lightSource,knife,rope,pickaxe,bow,arrows));
    }

    @Override
    public ArrayList<ItemRequirement> getItemRecommended()
    {
        return new ArrayList<>(Arrays.asList(combatGear, waterskins));
    }

    @Override
    public ArrayList<String> getCombatRequirements()
    {
        ArrayList<String> reqs = new ArrayList<>();
        reqs.add("Black Golem, Grey Golem, White Golem (Level 75)");
        return reqs;
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Starting Off", new ArrayList<>(Arrays.asList(speakToAwusah, speakToGhaslor,openCuboard,useNeedleTornRobes,telegrabKey)),airRune,lawRune,needle,thread));
        allSteps.add(new PanelDetails("The Golems", new ArrayList<>(Arrays.asList(enterCave,useAncestralKey,openStabDoor,clearChannel,openSlashDoor,clearChannel2,openCrushDoor,clearChannel3,openFarNorthDoor,speakToSpirits)),ancestralKey,robeOfElidinisTop,robeOfElidinisBottom,rope,pickaxe,bow,arrows,crushWep,stabWep,slashWep));
        allSteps.add(new PanelDetails("The Genie", new ArrayList<>(Arrays.asList(speakToAwusah2,takeShoes,cutShoes,enterCrevice,talkToGenie,talkToGenieAgain,useStatuette)),knife,rope,lightSource));
        return allSteps;
    }
}