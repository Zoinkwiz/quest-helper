/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
public abstract class RubbleSolver extends DetailedOwnerStep {
    @Inject
    protected Client client;

    private List<ObjectStep> mineSteps;
    private List<Requirement> conditions;
    private List<Requirement> inverseConditions;
    private ConditionalStep conditionalStep;

	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private int stepCounter;

    public RubbleSolver(TheCurseOfArrav theCurseOfArrav, @SuppressWarnings("unused") String number) {
        super(theCurseOfArrav, "Make your way through the Trollweiss cave, mining rubble with your pickaxe from the direction indicated. Rubble can only be mined from the same direction once.");
    }

    protected void addMineRubbleStep(int x, int y, RubbleType rubbleType, Direction direction) {
        var validObjectIDs = rubbleType.getObjectIDs();
        assert !validObjectIDs.isEmpty();

        var validIDSet = new HashSet<>(validObjectIDs);

        var wp = new WorldPoint(x, y, 0);
		// Useful for debugging
        // var stepCounter = this.stepCounter++;
        var text = String.format("Mine the rubble from the %s side", direction.toString().toLowerCase());
        var mainObjectID = validObjectIDs.get(0);
        var step = new ObjectStep(getQuestHelper(), mainObjectID, wp, text, ((TheCurseOfArrav) getQuestHelper()).anyPickaxe);
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

        var conditionText = String.format("Rubble mined from the %s side", direction.toString().toLowerCase());
        var inverseConditionText = String.format("Rubble needs to be mined from the %s side", direction.toString().toLowerCase());
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

        conditionalStep = new ConditionalStep(getQuestHelper(), todo);

        assert this.mineSteps.size() == this.conditions.size();
        assert this.mineSteps.size() == this.inverseConditions.size();

        for (var i = 0; i < mineSteps.size(); i++) {
            var mineStep = mineSteps.get(i);
			var inverseCondition = this.inverseConditions.get(i);

			// Useful for debugging
            // mineStep.addRequirement(inverseCondition);

            conditionalStep.addStep(inverseCondition, mineStep);
        }
    }

    protected void updateSteps() {
        startUpStep(this.conditionalStep);
    }

    @Override
    public List<QuestStep> getSteps() {
        var steps = new ArrayList<QuestStep>();

        steps.add(this.conditionalStep);
		steps.addAll(this.mineSteps);

        return steps;
    }

}
