// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import com.questhelper.questhelpers.QuestHelper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

@Slf4j
public class TreeSolver1 extends TreeSolver
{
	public TreeSolver1(QuestHelper theCurseOfArrav)
	{
		super(theCurseOfArrav, "1");
	}

	@Override
	protected void setupRubbleSteps()
	{
		this.addTreeStep(2966, 7896, TreeType.Untouched, Direction.SOUTH);
		this.addTreeStep(2966, 7896, TreeType.PartiallyChopped, Direction.EAST);
		this.addTreeStep(2966, 7896, TreeType.Stump, Direction.SOUTH);
	}
}
