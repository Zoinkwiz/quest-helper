package com.questhelper.helpers.quests.thecurseofarrav;

import com.questhelper.helpers.quests.thepathofglouphrie.DiscInsertionStep;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.steps.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.questhelper.steps.tools.QuestPerspective;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.tuple.Pair;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;

@Slf4j
public class RubbleMiner extends DetailedOwnerStep {
    @Inject
    Client client;

    private List<ObjectStep> mineSteps;
    private List<ObjectCondition> conditions;
    private ConditionalStep conditionalStep;

    private int operation;

    public RubbleMiner(TheCurseOfArrav theCurseOfArrav) {
        super(theCurseOfArrav, "Make your way through the Trollweiss cave, mining rubble with your pickaxe.");
    }


    private void addMineRubbleStep(int x, int y, RubbleLevel rubbleLevel, Direction direction) {
        var validObjectIDs = rubbleLevel.getObjectIDs();
        assert !validObjectIDs.isEmpty();

        var validIDSet = new HashSet<>(validObjectIDs);

        var wp = new WorldPoint(x, y, 0);
        var operation = this.operation++;
        var text = String.format("[%d] Mine the rubble from the %s side", operation, direction.toString().toLowerCase());
        log.info("setting up mine rubble step: {}: {}", operation, text);
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

        var conditionText = String.format("[%d] Rubble mined from the %s side", operation, direction.toString().toLowerCase());
        var conditionThatThisStepHasBeenDone = new ObjectCondition(validIDSet, wp);
        conditionThatThisStepHasBeenDone.setText(text);

        this.mineSteps.add(step);
        this.conditions.add(conditionThatThisStepHasBeenDone);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        updateSteps();
    }

    @Override
    public void startUp() {
        updateSteps();
    }

    @Override
    protected void setupSteps() {
        this.operation = 1;
        this.mineSteps = new ArrayList<>();
        this.conditions = new ArrayList<>();

        var todo = new DetailedQuestStep(getQuestHelper(), "todo");

        this.addMineRubbleStep(2764, 10266, RubbleLevel.Two, Direction.SOUTH);
        this.addMineRubbleStep(2775, 10258, RubbleLevel.One, Direction.SOUTH);
        this.addMineRubbleStep(2764, 10266, RubbleLevel.One, Direction.EAST);
        this.addMineRubbleStep(2764, 10267, RubbleLevel.One, Direction.SOUTH);

        // after reversing
        // mineStep 0: mine C
        // mineStep 1: mine B
        // mineStep 2: mine A

        // condition 0: A is not at the given state
        // condition 1: B is not at the given state
        // condition 2: C is not at the given state

        // i = 0: Mine C, if B and A are mined
        // i = 1: Mine B, if A is mined
        // i = 2: Mine A, with no condition

        conditionalStep = new ConditionalStep(getQuestHelper(), todo);
        Collections.reverse(mineSteps);

        assert this.mineSteps.size() == this.conditions.size();

        {
            var allDone = new DetailedQuestStep(getQuestHelper(), "you are all done lol");
            var conditionList = new ArrayList<Requirement>();
            for (var condition : this.conditions) {
                var xd2 = new Conditions(LogicType.NAND, condition);
                xd2.setText(condition.getDisplayText());
                allDone.addRequirement(xd2);
            }
            var xd = new Conditions(LogicType.NAND, conditionList);
            conditionalStep.addStep(xd, allDone);
        }
        for (var i = 0; i < mineSteps.size(); i++) {
            var mineStep = mineSteps.get(i);
            var conditionList = new ArrayList<Requirement>();
            StringBuilder text = new StringBuilder();
            for (var j = 0; j < this.conditions.size() - i - 1; j++) {
                conditionList.add(this.conditions.get(j));
                text.append(this.conditions.get(j).getDisplayText());
                var xd2 = new Conditions(LogicType.NAND, this.conditions.get(j));
                xd2.setText(this.conditions.get(j).getDisplayText());
                mineStep.addRequirement(xd2);
            }

            var xd = new Conditions(LogicType.NOR, conditionList);
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

    @Getter
    enum RubbleLevel {
        Three(123),
        Two(ObjectID.RUBBLE_50598),
        One(ObjectID.RUBBLE_50587, ObjectID.RUBBLE_50589);

        private final List<Integer> objectIDs;

        RubbleLevel(Integer... possibleObjectIDs) {
            this.objectIDs = new ArrayList<Integer>();
            for (var xd : possibleObjectIDs) {
                this.objectIDs.add(xd);
            }
            // Collections.addAll(this.objectIDs, possibleObjectIDs);
        }
    }
}
