package com.questhelper.quests.dwarfcannon;

import com.questhelper.*;
import com.questhelper.steps.*;
import com.questhelper.steps.conditional.*;

import java.util.*;
import java.util.concurrent.locks.Condition;

import com.questhelper.requirements.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.DWARF_CANNON
)

public class DwarfCannon extends BasicQuestHelper{

    ItemRequirement staminas,teleToAsg, hammer, railing, dwarfRemains,toolkit,cannonballMould;
    ConditionForStep upTower1, upTower2, inCave, bar1,bar2,bar3,bar4,bar5,bar6,hasRailings,hasHammer,hasRemains,foundLollk,hasToolkit,cannonFixed,hasCannonballMould;
    QuestStep talkToCaptainLawgof, talkToCaptainLawgof2, gotoTower, goToTower2, talkToCaptainLawgof3, gotoCave,inspectRailings1,inspectRailings2,inspectRailings3,inspectRailings4,inspectRailings5,inspectRailings6,getRemainsStep, downTower, downTower2, searchCrates,talkToCaptainLawgof4,useToolkit,talkToCaptainLawgof5,talkToNulodion,talkToCaptainLawgof6;
    Zone cave,tower1,tower2;

    @Override
    public Map<Integer, QuestStep> loadSteps() {
        setupItemRequirements();
        setupZones();
        setupConditions();
        setupSteps();
        Map<Integer, QuestStep> steps = new HashMap<>();

        //Start
        steps.put(0, talkToCaptainLawgof);

        //Repair Bars
        ConditionalStep fixedRailings = new ConditionalStep(this,inspectRailings1);
        fixedRailings.addStep(new Conditions(hasHammer,hasRailings,bar6),talkToCaptainLawgof2);
        fixedRailings.addStep(new Conditions(hasHammer,hasRailings,bar5),inspectRailings6);
        fixedRailings.addStep(new Conditions(hasHammer,hasRailings,bar4),inspectRailings5);
        fixedRailings.addStep(new Conditions(hasHammer,hasRailings,bar3),inspectRailings4);
        fixedRailings.addStep(new Conditions(hasHammer,hasRailings,bar2),inspectRailings3);
        fixedRailings.addStep(new Conditions(hasHammer,hasRailings,bar1),inspectRailings2);

        steps.put(1,fixedRailings);

        //Go to tower, get remains, come back
        ConditionalStep getRemains = new ConditionalStep(this,gotoTower);
        getRemains.addStep(upTower2,getRemainsStep);
        getRemains.addStep(upTower1,goToTower2);
        steps.put(2,getRemains);

        ConditionalStep returnRemains = new ConditionalStep(this,downTower2);
        returnRemains.addStep(hasRemains,talkToCaptainLawgof3);
        returnRemains.addStep(new Conditions(hasRemains,upTower1),downTower);
        steps.put(3,returnRemains);

        //Go to the cave, find Lollk, return and fix cannon
        ConditionalStep findLollk = new ConditionalStep(this,gotoCave);
        findLollk.addStep(new Conditions(foundLollk),talkToCaptainLawgof4);
        findLollk.addStep(new Conditions(inCave),searchCrates);
        steps.put(3,findLollk);

        ConditionalStep fixUpCannon = new ConditionalStep(this,useToolkit);
        fixUpCannon.addStep(cannonFixed,talkToCaptainLawgof5);
        steps.put(4,fixUpCannon);

        return steps;
    }
    public void setupItemRequirements() {
        staminas = new ItemRequirement("Stamina Potions",-1);
        teleToAsg = new ItemRequirement("Teleport to Falador or Amulet of Glory",-1);
        hammer = new ItemRequirement("Hammer",ItemID.HAMMER);
        railing = new ItemRequirement("Railing",ItemID.RAILING);
        toolkit = new ItemRequirement("Toolkit",ItemID.TOOLKIT);
        dwarfRemains = new ItemRequirement("Dwarf Remains",ItemID.DWARF_REMAINS);
        cannonballMould = new ItemRequirement("Cannonball Mould",ItemID.AMMO_MOULD);
    }

    public void setupConditions() {
        //Items
        hasRailings = new ItemRequirementCondition(railing);
        hasHammer = new ItemRequirementCondition(hammer);
        hasRemains = new ItemRequirementCondition(dwarfRemains);
        hasToolkit = new ItemRequirementCondition(toolkit);
        hasCannonballMould = new ItemRequirementCondition(cannonballMould);

        //Varbits
        bar1 = new VarbitCondition(2240, 1);
        bar2 = new VarbitCondition(2241, 1);
        bar3 = new VarbitCondition(2242, 1);
        bar4 = new VarbitCondition(2243, 1);
        bar5 = new VarbitCondition(2244, 1);
        bar6 = new VarbitCondition(2245, 1);
        //All Complete varbit 2246

        foundLollk = new VarbitCondition(0000,1);
        cannonFixed = new VarbitCondition(0000,1);

        //Zones
        upTower1 = new ZoneCondition(tower1);
        upTower2 = new ZoneCondition(tower2);
        inCave = new ZoneCondition(cave);
    }

    public void setupZones()
    {
        cave = new Zone(new WorldPoint(2557,9790,0), new WorldPoint(2624,9859,0));
        tower1 = new Zone(new WorldPoint(2568,3439,1), new WorldPoint(2572,3445,1));
        tower2 = new Zone(new WorldPoint(2566,3441,2), new WorldPoint(2572,3445,2));
    }

    public void setupSteps() {
        talkToCaptainLawgof = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof near the Coal Truck Mining Site (north of Fishing Guild, West of McGrubor's Wood).");
        talkToCaptainLawgof.addDialogStep("Sure, I'd be honoured to join.");

        //Fix the 6 bent railings
        inspectRailings1 = new ObjectStep(this, ObjectID.RAILING_15601, new WorldPoint(2555, 3479, 0), "Inspect Railings to fix them.",hammer,railing);
        inspectRailings2 = new ObjectStep(this, ObjectID.RAILING_15601, new WorldPoint(2557, 3468, 0), "Inspect Railings to fix them.",hammer,railing);
        inspectRailings3 = new ObjectStep(this, ObjectID.RAILING_15601, new WorldPoint(2559, 3458, 0), "Inspect Railings to fix them.",hammer,railing);
        inspectRailings4 = new ObjectStep(this, ObjectID.RAILING_15601, new WorldPoint(2563, 3457, 0), "Inspect Railings to fix them.",hammer,railing);
        inspectRailings5 = new ObjectStep(this, ObjectID.RAILING_15601, new WorldPoint(2573, 3457, 0), "Inspect Railings to fix them.",hammer,railing);
        inspectRailings6 = new ObjectStep(this, ObjectID.RAILING_15601, new WorldPoint(2577, 3457, 0), "Inspect Railings to fix them.",hammer,railing);

        //Get dwarf remains
        talkToCaptainLawgof2 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof again.  Make sure to complete the entire dialogue.");
        gotoTower = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2570,3441,0),"Go to the top floor of the tower south of Captain Lawgof.");
        goToTower2 = new ObjectStep(this, ObjectID.LADDER_11, new WorldPoint(2570,3443,1), "Go up the second ladder.");
        //TODO: Highlight on dwarf remains isn't working
        //getRemainsStep = new ObjectStep(this,ObjectID.DWARF_REMAINS,new WorldPoint(2567,3444,2),"Get the dwarf remains at the top of the tower.");
        getRemainsStep = new TileStep(this,new WorldPoint(2567,3444,2),"Get the dwarf remains at the top of the tower.");
        downTower = new ObjectStep(this,ObjectID.LADDER_16679,new WorldPoint(2570,3443,2),"Go down the first ladder");
        downTower2 = new ObjectStep(this, ObjectID.LADDER_16679, new WorldPoint(2570,3441,1),"Go down the second ladder");
        talkToCaptainLawgof3 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Go back down the ladders and return the remains to Captain Lawgof.");

        //Cave
        gotoCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE, new WorldPoint(2622,3391,0),"Go to the cave east of the Fishing Guild door.");
        searchCrates = new ObjectStep(this, ObjectID.CRATE, new WorldPoint(2571,9850,0),"Search the crates to find Lollk");
        talkToCaptainLawgof4 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Return to Captain Lawgof.");
        talkToCaptainLawgof4.addDialogStep("Okay, I'll see what I can do.");

        useToolkit = new ObjectStep(this,NullObjectID.NULL_15597, new WorldPoint(2563,3462,0),"Use the toolkit on the broken multicannon.");
        useToolkit.addIcon(ItemID.TOOLKIT);

        talkToCaptainLawgof5 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Talk to Captain Lawgof.");
        talkToCaptainLawgof5.addDialogStep("Okay then, just for you!");

        //Cannonball mould
        talkToNulodion = new NpcStep(this, NpcID.NULODION,new WorldPoint(0,0,0),"Go talk to Nulodion at the Dwarven Black Guard camp (north-est of Falador, South of ICe Mountain).");
        talkToCaptainLawgof6 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(2567, 3460, 0), "Finally, return to Captain Lawgof with the cannonball mould.");
    }

    @Override
    public ArrayList<ItemRequirement> getItemRecommended()
    {
        ArrayList<ItemRequirement> reqs = new ArrayList<>();
        reqs.add(staminas);
        reqs.add(teleToAsg);
        return reqs;
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToCaptainLawgof))));
        allSteps.add(new PanelDetails("Repair and Retrieval", new ArrayList<>(Arrays.asList(inspectRailings6,talkToCaptainLawgof2,gotoTower,getRemainsStep,talkToCaptainLawgof3))));
        allSteps.add(new PanelDetails("Find Lollk and Fix Cannon",new ArrayList<>(Arrays.asList(gotoCave,searchCrates,talkToCaptainLawgof4,useToolkit))));
        allSteps.add(new PanelDetails("Get Cannonball mould",new ArrayList<>(Arrays.asList(talkToNulodion,talkToCaptainLawgof6))));
        return allSteps;
    }
}