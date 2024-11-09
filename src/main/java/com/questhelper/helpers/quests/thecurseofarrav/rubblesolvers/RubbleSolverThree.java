package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

/**
 * This class describes the rubble mining steps required for Roadblock 3 (when quest state varbit is 26)
 */
@Slf4j
public class RubbleSolverThree extends RubbleSolver
{
	public RubbleSolverThree(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav);
	}

	@Override
	protected void setupRubbleSteps() {
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
	}
}
