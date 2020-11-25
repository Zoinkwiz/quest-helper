package com.questhelper.quests.dwarfcannon;

import com.questhelper.*;
import com.questhelper.requirements.FollowerRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.Requirements;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;

import java.util.*;

import com.questhelper.requirements.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.DWARF_CANNON
)

public class DwarfCannon extends BasicQuestHelper{

    QuestStep talkToCaptainLawgof, talkToCaptainLawgof2, gotoTower, takeRemains, talkToCaptainLawgof3, gotoCave;

    Zone cave;

    @Override
    public Map<Integer, QuestStep> loadSteps() {
        setupItemRequirements();
        setupConditions();
        setupSteps();
        Map<Integer, QuestStep> steps = new HashMap<>();

        steps.put(0, talkToCaptainLawgof);
        return steps;
    }

    public void setupItemRequirements() {
        //None
    }

    public void setupConditions() {
    }

    public void setupZones()
    {
        cave = new Zone(new WorldPoint(1,1,1), new WorldPoint(1,1,1));
    }

    public void setupSteps() {
        talkToCaptainLawgof = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(1, 1, 0), "Talk to Captain Lawgof near the Coal Truck Mining Site (north of Fishing Guild, West of McGrubor's Wood).");
        talkToCaptainLawgof.addDialogStep("Sure, I'd be honoured to join.");
        //Fix the 6 bent railings
        talkToCaptainLawgof2 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(1, 1, 0), "Talk to Captain Lawgof again.");
        gotoTower = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(1,1,0),"Go to the tower south of Captain Lawgof.");
        //Up ladders
        //Take remains
        //Down ladders?
        talkToCaptainLawgof3 = new NpcStep(this, NpcID.CAPTAIN_LAWGOF, new WorldPoint(1, 1, 0), "Go back and talk to Captain Lawgof again.");
        gotoCave = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(1,1,0),"Go to the cave east of the Fishing Guild door.");
    }

    @Override
    public ArrayList<ItemRequirement> getItemRequirements()
    {
        //None
        return new ArrayList<>(Arrays.asList());
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();
        allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToCaptainLawgof))));
        allSteps.add(new PanelDetails("Repair and Retrieval", new ArrayList<>(Arrays.asList(talkToCaptainLawgof2))));
        return allSteps;
    }
}