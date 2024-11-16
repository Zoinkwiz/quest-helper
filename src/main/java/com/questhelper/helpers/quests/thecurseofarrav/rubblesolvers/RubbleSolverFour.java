package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

/**
 * This class describes the rubble mining steps required for Roadblock 4 (when quest state varbit is 28)
 */
@Slf4j
public class RubbleSolverFour extends RubbleSolver
{
	public RubbleSolverFour(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav, "4");
	}

	@Override
	protected void setupRubbleSteps() {
		this.addMineRubbleStep(2787, 10267, RubbleType.Three, Direction.WEST);
		this.addMineRubbleStep(2787, 10266, RubbleType.Three, Direction.WEST);
		this.addMineRubbleStep(2787, 10267, RubbleType.Two, Direction.SOUTH);
		this.addMineRubbleStep(2788, 10267, RubbleType.Two, Direction.NORTH);
		this.addMineRubbleStep(2787, 10267, RubbleType.One, Direction.NORTH);
		this.addMineRubbleStep(2788, 10267, RubbleType.One, Direction.WEST);

		// Last part from south
		this.addMineRubbleStep(2803, 10264, RubbleType.Three, Direction.SOUTH);
		this.addMineRubbleStep(2803, 10265, RubbleType.Two, Direction.SOUTH);

		// Last part from north
		this.addMineRubbleStep(2803, 10267, RubbleType.One, Direction.NORTH);
		this.addMineRubbleStep(2803, 10266, RubbleType.Three, Direction.NORTH);
		this.addMineRubbleStep(2804, 10266, RubbleType.Two, Direction.NORTH);

		// Last part from west
		this.addMineRubbleStep(2802, 10266, RubbleType.Two, Direction.WEST);
		this.addMineRubbleStep(2801, 10265, RubbleType.One, Direction.NORTH);
		this.addMineRubbleStep(2802, 10265, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2803, 10265, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2802, 10266, RubbleType.One, Direction.SOUTH);
		this.addMineRubbleStep(2804, 10265, RubbleType.Two, Direction.WEST);
		this.addMineRubbleStep(2803, 10266, RubbleType.Two, Direction.SOUTH);
		this.addMineRubbleStep(2803, 10266, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2804, 10266, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2804, 10265, RubbleType.One, Direction.NORTH);
		this.addMineRubbleStep(2805, 10265, RubbleType.One, Direction.WEST);
		this.addMineRubbleStep(2806, 10265, RubbleType.One, Direction.WEST);
	}
}
