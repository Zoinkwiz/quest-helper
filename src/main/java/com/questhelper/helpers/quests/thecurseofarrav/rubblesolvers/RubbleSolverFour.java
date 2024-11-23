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
