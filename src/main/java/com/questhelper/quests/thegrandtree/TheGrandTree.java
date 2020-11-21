package com.questhelper.quests.thegrandtree;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ZoneCondition;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

@QuestDescriptor(
        quest = QuestHelperQuest.THE_GRAND_TREE
)
public class TheGrandTree extends BasicQuestHelper {

    QuestStep talkToKing, talkToKingUnderground, talkToHazelmere, enterGroundFloorDoor;
    Zone groundFloorGrandTree, undergroundGrandTree;
    ConditionForStep inGrandTreeFirstFloor, isUndergroundGrandTree;

    @Override
    public Map<Integer, QuestStep> loadSteps()
    {
        setupSteps();
        setupZones();
        setupConditions();

        Map<Integer, QuestStep> steps = new HashMap<>();


        ConditionalStep startQuest = new ConditionalStep(this, talkToKing);
        startQuest.addStep(inGrandTreeFirstFloor, talkToKing);
        startQuest.addStep(isUndergroundGrandTree, talkToKingUnderground);
        steps.put(0, startQuest);
        steps.put(10, talkToHazelmere);

        return steps;
    }

    private void setupZones() {
        groundFloorGrandTree = new Zone(new WorldPoint(2462,3492,0), new WorldPoint(2469, 3499, 0));
        undergroundGrandTree = new Zone(new WorldPoint(2458,9887,0), new WorldPoint(2482, 9902, 0));
    }

    public void setupConditions() {
        inGrandTreeFirstFloor = new ZoneCondition(groundFloorGrandTree);
        isUndergroundGrandTree = new ZoneCondition(undergroundGrandTree);
    }

    private void setupSteps() {
        talkToKing = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 3496, 0), "Speak to King Narnode Shareen on the 1st floor of the Grand Tree.");
        talkToKing.addDialogSteps("You seem worried, what's up?");

        talkToKingUnderground = new NpcStep(this, NpcID.KING_NARNODE_SHAREEN, new WorldPoint(2465, 9897,0), "Speak to King Narnode Shareen underneath the Grand Tree.");
        talkToKingUnderground.addDialogSteps("I'd be happy to help!");

        talkToHazelmere = new NpcStep(this, NpcID.HAZELMERE, new WorldPoint(0,0,0), "Talk to Hazelmere.");

        ObjectStep doorStep = new ObjectStep(this, 1967, new WorldPoint(2465, 3492, 0), "Enter gnome stronghold ground floor.");
        doorStep.addAlternateObjects(1968);
        enterGroundFloorDoor = doorStep;
    }

    @Override
    public ArrayList<String> getCombatRequirements()
    {
        ArrayList<String> reqs = new ArrayList<>();
        reqs.add("The ability to defeat a level 172 Black demon (safespottable for Ranged and Magic or melee using a halberd).");
        return reqs;
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> steps = new ArrayList<>();
        steps.add(new PanelDetails("Getting started", new ArrayList<>(Arrays.asList(talkToKing,talkToKingUnderground))));
        steps.add(new PanelDetails("Hazelmere", new ArrayList<>(Collections.singletonList(talkToKing))));


        return steps;
    }
}
