// SPDX-FileCopyrightText: 2026 pajlada <rasmus.karlsson@pajlada.com>
//
// SPDX-License-Identifier: BSD-2-Clause

package com.questhelper.steps;

import com.questhelper.questhelpers.QuestHelper;

public class UnreachableStep extends DetailedQuestStep
{
	public UnreachableStep(QuestHelper questHelper)
	{
		super(questHelper, "This step should be unreachable. Please report this in the Quest Helper discord or GitHub issues page with a screenshot of your full game client and a brief explanation of what you were doing at the time. In the meantime, you can use the excellent OSRS wiki quest guide linked in the sidebar until the quest helper gets back on track.");
	}
}
