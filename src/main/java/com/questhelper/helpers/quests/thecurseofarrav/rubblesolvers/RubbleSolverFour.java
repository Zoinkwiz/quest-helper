package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

/**
 * This class describes the rubble mining steps required for Roadblock 3 (when quest state varbit is 26)
 */
@Slf4j
public class RubbleSolverFour extends RubbleSolver
{
	public RubbleSolverFour(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav, "4");
	}

	@Override
	protected void setupRubbleSteps() {
		this.addMineRubbleStep(2787, 10267, RubbleType.Three, Direction.WEST); // 1
		this.addMineRubbleStep(2787, 10266, RubbleType.Three, Direction.WEST); // 2
		this.addMineRubbleStep(2787, 10267, RubbleType.Two, Direction.SOUTH); // 3
		this.addMineRubbleStep(2788, 10267, RubbleType.Two, Direction.NORTH); // 4
		this.addMineRubbleStep(2787, 10267, RubbleType.One, Direction.NORTH); // 5
		this.addMineRubbleStep(2788, 10267, RubbleType.One, Direction.WEST); // 6

		// Last part from south
		this.addMineRubbleStep(2803, 10264, RubbleType.Three, Direction.SOUTH); // 7
		this.addMineRubbleStep(2803, 10265, RubbleType.Two, Direction.SOUTH); // 8

		// Last part from north
		this.addMineRubbleStep(2803, 10267, RubbleType.One, Direction.NORTH); // 9
		this.addMineRubbleStep(2803, 10266, RubbleType.Three, Direction.NORTH); // 10
		this.addMineRubbleStep(2804, 10266, RubbleType.Two, Direction.NORTH); // 11

		// Last part from west
		this.addMineRubbleStep(2802, 10266, RubbleType.Two, Direction.WEST); // 12
		this.addMineRubbleStep(2801, 10265, RubbleType.One, Direction.NORTH); // 13
		this.addMineRubbleStep(2802, 10265, RubbleType.One, Direction.WEST); // 14
		this.addMineRubbleStep(2803, 10265, RubbleType.One, Direction.WEST); // 15
		this.addMineRubbleStep(2802, 10266, RubbleType.One, Direction.SOUTH); // 16
		this.addMineRubbleStep(2803, 10266, RubbleType.Two, Direction.WEST); // 17
		this.addMineRubbleStep(2804, 10265, RubbleType.Two, Direction.WEST); // 18
		this.addMineRubbleStep(2803, 10266, RubbleType.One, Direction.SOUTH); // 19
		this.addMineRubbleStep(2804, 10266, RubbleType.One, Direction.WEST); // 20
		this.addMineRubbleStep(2804, 10265, RubbleType.One, Direction.NORTH); // 21
		this.addMineRubbleStep(2805, 10265, RubbleType.One, Direction.WEST); // 22
		this.addMineRubbleStep(2806, 10265, RubbleType.One, Direction.WEST); // 23
	}
}
