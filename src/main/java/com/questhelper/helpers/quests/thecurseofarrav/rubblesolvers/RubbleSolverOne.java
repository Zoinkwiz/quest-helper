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
 * This class describes the rubble mining steps required for Roadblock 1 (when quest state varbit is 22)
 */
@Slf4j
public class RubbleSolverOne extends RubbleSolver
{
	public RubbleSolverOne(TheCurseOfArrav theCurseOfArrav) {
		super(theCurseOfArrav, "1");
	}

	@Override
	protected void setupRubbleSteps() {
		this.addMineRubbleStep(2764, 10266, RubbleType.Two, Direction.SOUTH); // 1
		this.addMineRubbleStep(2775, 10258, RubbleType.One, Direction.SOUTH); // 2
		this.addMineRubbleStep(2764, 10266, RubbleType.One, Direction.EAST); // 3
		this.addMineRubbleStep(2764, 10267, RubbleType.One, Direction.SOUTH); // 4
	}
}
