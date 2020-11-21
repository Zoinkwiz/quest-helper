package com.questhelper.quests.treegnomevillage;


import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.*;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

@QuestDescriptor(
        quest = QuestHelperQuest.TREE_GNOME_VILLAGE
)
public class TreeGnomeVillage extends BasicQuestHelper {

    QuestStep talkToKingBolren, talkToCommanderMontai, bringWoodToCommanderMontai, talkToCommanderMontaiAgain,
        firstTracker, secondTracker, thirdTracker, fireBallista, climbTheLadder, getOrbFromChest, talkToKingBolrenFirstOrb,
            talkToTheWarlord, fightTheWarlord, returnOrbs;

    ConditionForStep completeFirstTracker, completeSecondTracker, completeThirdTracker,
            notCompleteFirstTracker, notCompleteSecondTracker, notCompleteThirdTracker;

    private Conditions talkToFirstTracker, talkToSecondTracker, talkToThirdTracker, shouldFireBallista;

    Zone upstairsTower;
    ZoneCondition isUpstairsTower;
    ItemRequirement logRequirement;
    ItemCondition hasOrb;

    private final int TRACKER_1_VARBITID = 599;
    private final int TRACKER_2_VARBITID = 600;
    private final int TRACKER_3_VARBITID = 601;

    @Override
    public Map<Integer, QuestStep> loadSteps() {
        setupSteps();
        setupZones();
        setupConditions();
        setupItemRequirements();

       return CreateSteps();
    }

    private Map<Integer, QuestStep> CreateSteps() {
        Map<Integer, QuestStep> steps = new HashMap<>();
        steps.put(0, talkToKingBolren);
        steps.put(1, talkToCommanderMontai);
        steps.put(2, bringWoodToCommanderMontai);
        steps.put(3, talkToCommanderMontaiAgain);
        steps.put(4, talkToTrackersStep());
        steps.put(5, retrieveOrbStep());
        steps.put(6, talkToKingBolrenFirstOrb);
        steps.put(7, defeatWarlordStep());
        steps.put(8, returnOrbs);
        return steps;
    }

    private QuestStep talkToTrackersStep() {
        ConditionalStep talkToTrackers = new ConditionalStep(this,talkToFirstTracker, firstTracker);
        talkToTrackers.addStep(talkToSecondTracker, secondTracker);
        talkToTrackers.addStep(talkToThirdTracker, thirdTracker);
        talkToTrackers.addStep(shouldFireBallista, fireBallista);
        return talkToTrackers;
    }

    private QuestStep retrieveOrbStep() {
        ConditionalStep retrieveOrb = new ConditionalStep(this, climbTheLadder);
        getOrbFromChest = new ObjectStep(this, 2183, new WorldPoint(2506,3259,1), "Retrieve the first orb from chest.");
        retrieveOrb.addStep(isUpstairsTower, getOrbFromChest);
        return retrieveOrbStep();
    }

    private QuestStep defeatWarlordStep() {
        NpcCondition fightingWarlord = new NpcCondition(NpcID.KHAZARD_WARLORD_7622);

        fightTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_7622, new WorldPoint(2456,3301,0), "Defeat the warlord and retrieve orbs");
        talkToTheWarlord = new NpcStep(this, NpcID.KHAZARD_WARLORD_7621, new WorldPoint(2456,3301,0), "Retrieve the orbs from the warlord.");

        ConditionalStep defeatTheWarlord = new ConditionalStep(this, talkToTheWarlord);
        defeatTheWarlord.addStep(fightingWarlord, fightTheWarlord);
        return defeatTheWarlord;
    }

    private void setupItemRequirements() {
        logRequirement = new ItemRequirement("Logs", ItemID.LOGS, 6);
    }

    private void setupZones() {
        upstairsTower = new Zone(new WorldPoint(2500,3251,1), new WorldPoint(2506,3259,1));
    }

    public void setupConditions() {
        notCompleteFirstTracker = new VarbitCondition(TRACKER_1_VARBITID, 0);
        notCompleteSecondTracker = new VarbitCondition(TRACKER_2_VARBITID, 0);
        notCompleteThirdTracker = new VarbitCondition(TRACKER_3_VARBITID, 0);

        completeFirstTracker = new VarbitCondition(TRACKER_1_VARBITID, 1);
        completeSecondTracker = new VarbitCondition(TRACKER_2_VARBITID, 1);
        completeThirdTracker = new VarbitCondition(TRACKER_3_VARBITID, 1);

        isUpstairsTower = new ZoneCondition(upstairsTower);

        talkToFirstTracker = new Conditions(notCompleteFirstTracker);
        talkToSecondTracker = new Conditions(LogicType.AND, completeFirstTracker, notCompleteSecondTracker);
        talkToThirdTracker = new Conditions(LogicType.AND, completeFirstTracker, notCompleteThirdTracker);
        shouldFireBallista = new Conditions(LogicType.AND, completeFirstTracker, completeSecondTracker, completeThirdTracker);
    }

    private void setupSteps() {
        talkToKingBolren = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");
        talkToKingBolren.addDialogStep("Can I help at all?");
        talkToKingBolren.addDialogStep("I would be glad to help.");

        talkToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523,3208,0), "Speak with Commander Montai.");
        talkToCommanderMontai.addDialogStep("Ok, I'll gather some wood.");

        bringWoodToCommanderMontai = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523,3208,0), "Speak with Commander Montai again to give him the wood.");

        talkToCommanderMontaiAgain = new NpcStep(this, NpcID.COMMANDER_MONTAI, new WorldPoint(2523,3208,0), "Speak with Commander Montai.");
        talkToCommanderMontaiAgain.addDialogStep("I'll try my best.");

        firstTracker = new NpcStep(this, NpcID.TRACKER_GNOME_1, new WorldPoint(2501,3261, 0), "Talk to the first tracker gnome to the northwest.");
        secondTracker = new NpcStep(this, NpcID.TRACKER_GNOME_2, new WorldPoint(2524, 3257, 0), "Talk to the second tracker gnome inside the jail.");
        thirdTracker = new NpcStep(this, NpcID.TRACKER_GNOME_3, new WorldPoint(2497, 3234, 0), "Talk to the third tracker gnome to the southwest.");

        fireBallista = new ObjectStep(this, ObjectID.BALLISTA, new WorldPoint(2509,3211,0), "Fire the ballista.");

        climbTheLadder = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2503, 3252,0), "Climb the ladder");

        talkToKingBolrenFirstOrb = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");
        talkToKingBolrenFirstOrb.addDialogStep("I will find the warlord and bring back the orbs.");

        returnOrbs = new NpcStep(this, NpcID.KING_BOLREN, new WorldPoint(2541, 3170, 0), "Speak to King Bolren in the centre of the Tree Gnome Maze.");
    }

    @Override
    public ArrayList<PanelDetails> getPanels()
    {
        ArrayList<PanelDetails> steps = new ArrayList<>();
        steps.add(new PanelDetails("Getting started", new ArrayList<>(Arrays.asList(talkToKingBolren))));
        steps.add(new PanelDetails("The three trackers", new ArrayList<>(Arrays.asList(
                talkToCommanderMontai, bringWoodToCommanderMontai, talkToCommanderMontaiAgain,
                firstTracker, secondTracker, thirdTracker, fireBallista)), logRequirement));
        steps.add(new PanelDetails("Retrieving the orbs", new ArrayList<>(Arrays.asList(getOrbFromChest, talkToKingBolrenFirstOrb,
                talkToTheWarlord, fightTheWarlord, returnOrbs))));
        return steps;
    }
}
