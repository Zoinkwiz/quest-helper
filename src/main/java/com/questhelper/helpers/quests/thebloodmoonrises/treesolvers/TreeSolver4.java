// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import com.questhelper.questhelpers.QuestHelper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

@Slf4j
public class TreeSolver4 extends TreeSolver
{
	public TreeSolver4(QuestHelper theCurseOfArrav)
	{
		super(theCurseOfArrav, "4");
	}

	@Override
	protected void setupRubbleSteps()
	{
		this.addTreeStep(2973, 7909, TreeType.Untouched, Direction.WEST);
		this.addTreeStep(2973, 7908, TreeType.PartiallyChopped, Direction.SOUTH);
		this.addTreeStep(2973, 7909, TreeType.Stump, Direction.WEST);
	}
}
