package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.steps.*;

import java.util.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

/**
 * This class describes the rubble mining steps required for Roadblock 1 (when quest state varbit is 22)
 */
@Slf4j
public class RubbleSolverOne extends RubbleSolver
{
	public RubbleSolverOne(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav);
	}

	@Override
	protected void setupRubbleSteps() {
		this.addMineRubbleStep(2764, 10266, RubbleType.Two, Direction.SOUTH); // 1
		this.addMineRubbleStep(2775, 10258, RubbleType.One, Direction.SOUTH); // 2
		this.addMineRubbleStep(2764, 10266, RubbleType.One, Direction.EAST); // 3
		this.addMineRubbleStep(2764, 10267, RubbleType.One, Direction.SOUTH); // 4
	}
}
