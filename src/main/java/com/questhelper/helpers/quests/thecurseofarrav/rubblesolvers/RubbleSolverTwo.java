package com.questhelper.helpers.quests.thecurseofarrav.rubblesolvers;

import com.questhelper.helpers.quests.thecurseofarrav.TheCurseOfArrav;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

/**
 * This class describes the rubble mining steps required for Roadblock 1 (when quest state varbit is 22)
 */
@Slf4j
public class RubbleSolverTwo extends RubbleSolver
{
	public RubbleSolverTwo(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav);
	}

	@Override
	protected void setupRubbleSteps() {
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
	}
}
