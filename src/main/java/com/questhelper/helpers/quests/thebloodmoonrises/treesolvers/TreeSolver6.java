// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import com.questhelper.questhelpers.QuestHelper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

@Slf4j
public class TreeSolver6 extends TreeSolver
{
	public TreeSolver6(QuestHelper theCurseOfArrav)
	{
		super(theCurseOfArrav, "6");
	}

	@Override
	protected void setupRubbleSteps()
	{
		this.addTreeStep(2981, 7913, TreeType.Untouched, Direction.SOUTH);
		this.addTreeStep(2980, 7914, TreeType.PartiallyChopped, Direction.WEST);
		this.addTreeStep(2980, 7914, TreeType.Stump, Direction.WEST);
	}
}
