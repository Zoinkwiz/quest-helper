package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

/**
 * This class describes the rubble mining steps required for Roadblock 2 (when quest state varbit is 24)
 */
@Slf4j
public class RubbleSolverTwo extends RubbleSolver
{
	public RubbleSolverTwo(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav, "2");
	}

	@Override
	protected void setupRubbleSteps() {
		this.addMineRubbleStep(2766, 10279, RubbleType.Three, Direction.WEST);
		this.addMineRubbleStep(2766, 10280, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2767, 10281, RubbleType.Two, Direction.WEST);
		this.addMineRubbleStep(2766, 10279, RubbleType.Two, Direction.NORTH);
		this.addMineRubbleStep(2766, 10278, RubbleType.Two, Direction.WEST);
		this.addMineRubbleStep(2766, 10278, RubbleType.One, Direction.SOUTH);
		this.addMineRubbleStep(2766, 10279, RubbleType.One, Direction.SOUTH);
		this.addMineRubbleStep(2767, 10278, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2767, 10279, RubbleType.Two, Direction.SOUTH);
		this.addMineRubbleStep(2767, 10279, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2768, 10279, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2768, 10280, RubbleType.Three, Direction.SOUTH);
		this.addMineRubbleStep(2768, 10281, RubbleType.One, Direction.SOUTH);
		this.addMineRubbleStep(2769, 10281, RubbleType.Two, Direction.WEST);
		this.addMineRubbleStep(2767, 10281, RubbleType.One, Direction.EAST);
		this.addMineRubbleStep(2767, 10282, RubbleType.One, Direction.SOUTH);
		this.addMineRubbleStep(2769, 10281, RubbleType.One, Direction.NORTH);
		this.addMineRubbleStep(2770, 10281, RubbleType.One, Direction.WEST);
	}
}
