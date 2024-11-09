package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.*;

import java.util.*;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public abstract class RubbleSolver extends DetailedOwnerStep {
    @Inject
    protected Client client;

    private List<ObjectStep> mineSteps;
    private List<Requirement> conditions;
    private List<Requirement> inverseConditions;
    private ConditionalStep conditionalStep;

    private int stepCounter;

    public RubbleSolver(TheCurseOfArrav theCurseOfArrav) {
        super(theCurseOfArrav, "Make your way through the Trollweiss cave, mining rubble with your pickaxe.");
    }

    protected void addMineRubbleStep(int x, int y, RubbleType rubbleType, Direction direction) {
        var validObjectIDs = rubbleType.getObjectIDs();
        assert !validObjectIDs.isEmpty();

        var validIDSet = new HashSet<>(validObjectIDs);

        var wp = new WorldPoint(x, y, 0);
        var stepCounter = this.stepCounter++;
        var text = String.format("[%d] Mine the rubble from the %s side", stepCounter, direction.toString().toLowerCase());
        log.info("setting up mine rubble step: {}: {}", stepCounter, text);
        var mainObjectID = validObjectIDs.get(0);
        var step = new ObjectStep(getQuestHelper(), mainObjectID, wp, text);
        var offsetX = x;
        var offsetY = y;
        switch (direction) {
            case NORTH:
                offsetY += 1;
                break;
            case SOUTH:
                offsetY -= 1;
                break;
            case WEST:
                offsetX -= 1;
                break;
            case EAST:
                offsetX += 1;
                break;
        }
        var posWp = new WorldPoint(offsetX, offsetY, 0);
        step.addTileMarker(posWp, SpriteID.SKILL_MINING);
        for (var alternateIDs : validObjectIDs) {
            // todo this adds the first object again xd
            step.addAlternateObjects(alternateIDs);
        }

        var conditionText = String.format("[%d] Rubble mined from the %s side", stepCounter, direction.toString().toLowerCase());
        var inverseConditionText = String.format("[%d] Rubble needs to be mined from the %s side", stepCounter, direction.toString().toLowerCase());
        var conditionThatRubbleIsStillThere = new ObjectCondition(validIDSet, wp);
        var conditionThatRubbleHasBeenMined = new Conditions(true, LogicType.NAND, conditionThatRubbleIsStillThere);
        conditionThatRubbleIsStillThere.setText(inverseConditionText);
        conditionThatRubbleHasBeenMined.setText(conditionText);

        this.mineSteps.add(step);
        this.conditions.add(conditionThatRubbleHasBeenMined);
        this.inverseConditions.add(conditionThatRubbleIsStillThere);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        updateSteps();
    }

    @Override
    public void startUp() {
        updateSteps();
    }

	protected abstract void setupRubbleSteps();

    @Override
    protected void setupSteps() {
        this.stepCounter = 1;
        this.mineSteps = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.inverseConditions = new ArrayList<>();

        var todo = new DetailedQuestStep(getQuestHelper(), "todo");

		this.setupRubbleSteps();

        // Roadblock 1 (when quest state varbit is 22)
        this.addMineRubbleStep(2764, 10266, RubbleType.Two, Direction.SOUTH); // 1
        this.addMineRubbleStep(2775, 10258, RubbleType.One, Direction.SOUTH); // 2
        this.addMineRubbleStep(2764, 10266, RubbleType.One, Direction.EAST); // 3
        this.addMineRubbleStep(2764, 10267, RubbleType.One, Direction.SOUTH); // 4

        // Roadblock 2 (when quest state varbit is 24)
        this.addMineRubbleStep(2766, 10279, RubbleType.Three, Direction.WEST); // 5
        this.addMineRubbleStep(2766, 10280, RubbleType.One, Direction.WEST); // 6
        this.addMineRubbleStep(2767, 10281, RubbleType.Two, Direction.WEST); // 7
        this.addMineRubbleStep(2766, 10279, RubbleType.Two, Direction.NORTH); // 8
        this.addMineRubbleStep(2766, 10278, RubbleType.Two, Direction.WEST); // 9
        this.addMineRubbleStep(2766, 10278, RubbleType.One, Direction.SOUTH); // 10
        this.addMineRubbleStep(2766, 10279, RubbleType.One, Direction.SOUTH); // 11
        this.addMineRubbleStep(2767, 10278, RubbleType.One, Direction.WEST); // 12
        this.addMineRubbleStep(2767, 10279, RubbleType.Two, Direction.WEST); // 13
        this.addMineRubbleStep(2768, 10279, RubbleType.One, Direction.WEST); // 14
        this.addMineRubbleStep(2767, 10279, RubbleType.One, Direction.SOUTH); // 15
        this.addMineRubbleStep(2768, 10280, RubbleType.Three, Direction.SOUTH); // 16: THIS TRIGGERS A STONE FALL OR SOMETHING :)
        this.addMineRubbleStep(2768, 10281, RubbleType.One, Direction.SOUTH); // 17
        this.addMineRubbleStep(2769, 10281, RubbleType.Two, Direction.WEST); // 18
        this.addMineRubbleStep(2767, 10281, RubbleType.One, Direction.EAST); // 19
        this.addMineRubbleStep(2767, 10282, RubbleType.One, Direction.SOUTH); // 20
        this.addMineRubbleStep(2769, 10281, RubbleType.One, Direction.NORTH); // 21
        this.addMineRubbleStep(2770, 10281, RubbleType.One, Direction.WEST); // 22

        // Roadblock 3
        this.addMineRubbleStep(2787, 10267, RubbleType.Three, Direction.WEST); // 23
        this.addMineRubbleStep(2787, 10266, RubbleType.Three, Direction.WEST); // 24
        this.addMineRubbleStep(2787, 10267, RubbleType.Two, Direction.SOUTH); // 25

        this.addMineRubbleStep(2789, 10286, RubbleType.One, Direction.WEST);
        this.addMineRubbleStep(2789, 10285, RubbleType.Three, Direction.NORTH);
        this.addMineRubbleStep(2789, 10285, RubbleType.Three, Direction.WEST);

        this.addMineRubbleStep(2789, 10283, RubbleType.Two, Direction.WEST);
        this.addMineRubbleStep(2789, 10284, RubbleType.Three, Direction.WEST);
        this.addMineRubbleStep(2789, 10285, RubbleType.Three, Direction.SOUTH);
        this.addMineRubbleStep(2790, 10285, RubbleType.One, Direction.WEST);
        this.addMineRubbleStep(2791, 10285, RubbleType.Two, Direction.WEST);
        this.addMineRubbleStep(2789, 10283, RubbleType.One, Direction.NORTH);
        this.addMineRubbleStep(2790, 10283, RubbleType.One, Direction.WEST);
        this.addMineRubbleStep(2791, 10283, RubbleType.Two, Direction.WEST);
        this.addMineRubbleStep(2790, 10282, RubbleType.One, Direction.NORTH);
        this.addMineRubbleStep(2791, 10282, RubbleType.Three, Direction.WEST);
        this.addMineRubbleStep(2791, 10283, RubbleType.One, Direction.SOUTH);
        this.addMineRubbleStep(2791, 10285, RubbleType.One, Direction.SOUTH);
        this.addMineRubbleStep(2792, 10285, RubbleType.Two, Direction.SOUTH);
        this.addMineRubbleStep(2792, 10285, RubbleType.Two, Direction.WEST);
        this.addMineRubbleStep(2793, 10285, RubbleType.Two, Direction.WEST);

        this.addMineRubbleStep(2787, 10267, RubbleType.One, Direction.NORTH); // 26 (or when??)

        // after reversing
        // mineStep 0: mine C
        // mineStep 1: mine B
        // mineStep 2: mine A

        // condition 0: A is mined
        // condition 1: B is mined
        // condition 2: C is mined

        // i = 0: Mine C, if B and A are mined, and C is not mined
        // i = 1: Mine B, if A is mined
        // i = 2: Mine A, with no condition

        conditionalStep = new ConditionalStep(getQuestHelper(), todo);
        Collections.reverse(this.mineSteps);
        Collections.reverse(this.inverseConditions);

        assert this.mineSteps.size() == this.conditions.size();
        assert this.mineSteps.size() == this.inverseConditions.size();

        // {
        //     var allDone = new DetailedQuestStep(getQuestHelper(), "you are all done lol");
        //     var conditionList = new ArrayList<Requirement>();
        //     for (var condition : this.conditions) {
        //         allDone.addRequirement(condition);
        //     }
        //     var xd = new Conditions(LogicType.AND, conditionList);
        //     conditionalStep.addStep(xd, allDone);
        // }
        for (var i = 0; i < mineSteps.size(); i++) {
            var mineStep = mineSteps.get(i);

            var conditionList = new ArrayList<Requirement>();

            mineStep.addRequirement(this.inverseConditions.get(i));
            conditionList.add(this.inverseConditions.get(i));

            StringBuilder text = new StringBuilder();
            for (var j = 0; j < this.conditions.size() - i - 1; j++) {
                var condition = this.conditions.get(j);
                conditionList.add(condition);
                text.append(this.conditions.get(j).getDisplayText());
                mineStep.addRequirement(condition);
            }

            var xd = new Conditions(true, LogicType.AND, conditionList);
            xd.setText(text.toString());

            conditionalStep.addStep(xd, mineStep);
        }
    }

    protected void updateSteps() {
        startUpStep(this.conditionalStep);
    }

    @Override
    public List<QuestStep> getSteps() {
        var steps = new ArrayList<QuestStep>();

        steps.add(this.conditionalStep);
        for (var step : this.mineSteps) {
            steps.add(step);
        }

        return steps;
    }

}
