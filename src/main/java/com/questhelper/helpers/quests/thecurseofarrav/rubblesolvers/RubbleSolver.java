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

    public RubbleSolver(TheCurseOfArrav theCurseOfArrav, String number) {
        super(theCurseOfArrav, "Make your way through the Trollweiss cave, mining rubble with your pickaxe. " + number);
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
        // Collections.reverse(this.mineSteps);
        // Collections.reverse(this.inverseConditions);

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
			var inverseCondition = this.inverseConditions.get(i);

            // var conditionList = new ArrayList<Requirement>();

            mineStep.addRequirement(inverseCondition);
            // conditionList.add(this.inverseConditions.get(i));

            // StringBuilder text = new StringBuilder();
            // for (var j = 0; j < this.conditions.size() - i - 1; j++) {
            //     var condition = this.conditions.get(j);
            //     conditionList.add(condition);
            //     text.append(this.conditions.get(j).getDisplayText());
            //     mineStep.addRequirement(condition);
            // }

            var xd = new Conditions(LogicType.AND, inverseCondition);
            xd.setText(inverseCondition.getDisplayText());

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
