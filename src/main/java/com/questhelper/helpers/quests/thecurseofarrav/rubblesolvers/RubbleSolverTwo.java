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
		super(theCurseOfArrav);
	}

	@Override
	protected void setupRubbleSteps() {
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
	}
}
