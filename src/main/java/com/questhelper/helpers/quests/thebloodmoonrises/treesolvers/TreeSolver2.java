// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import com.questhelper.questhelpers.QuestHelper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

@Slf4j
public class TreeSolver2 extends TreeSolver
{
	public TreeSolver2(QuestHelper theCurseOfArrav)
	{
		super(theCurseOfArrav, "2");
	}

	@Override
	protected void setupRubbleSteps()
	{
		this.addTreeStep(2967, 7899, TreeType.Untouched, Direction.SOUTH);
		this.addTreeStep(2967, 7900, TreeType.PartiallyChopped, Direction.EAST);
		this.addTreeStep(2967, 7900, TreeType.Stump, Direction.EAST);
	}
}
