// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause
package com.questhelper.helpers.quests.thebloodmoonrises.treesolvers;

import com.questhelper.questhelpers.QuestHelper;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.Direction;

@Slf4j
public class TreeSolver3 extends TreeSolver
{
	public TreeSolver3(QuestHelper theCurseOfArrav)
	{
		super(theCurseOfArrav, "3");
	}

	@Override
	protected void setupRubbleSteps()
	{
		this.addTreeStep(2968, 7903, TreeType.Untouched, Direction.SOUTH);
		this.addTreeStep(2969, 7903, TreeType.PartiallyChopped, Direction.EAST);
		this.addTreeStep(2968, 7903, TreeType.Stump, Direction.SOUTH);
	}
}
