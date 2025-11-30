/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.activities.charting.steps;

import com.questhelper.helpers.activities.charting.ChartingTaskDefinition;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;

public class ChartingCrateStep extends ChartingTaskObjectStep
{
	public ChartingCrateStep(QuestHelper questHelper, ChartingTaskDefinition definition, Requirement... requirements)
	{
		super(questHelper, ObjectID.SAILING_CHARTING_DRINK_CRATE, definition, requirements);
		var crowbar = new ItemRequirement("Crowbar", ItemID.SAILING_CHARTING_CROWBAR);
		addRequirement(crowbar);
		var reqWithQuest = or(getFadeCondition(), new VarbitRequirement(VarbitID.SAILING_CHARTING_DRINK_CRATE_PRYING_TIMES_COMPLETE, 0));
		reqWithQuest.setText(getFadeCondition().getDisplayText() + " You also require completion of the quest 'Prying Times'.");
		conditionToFadeInSidebar(reqWithQuest);
		
		// Task is complete if varbit is 1 (already drank) OR player has the bottle item
		var completedVarbit = new VarbitRequirement(definition.getVarbitId(), 1);
		Requirement completionRequirement = completedVarbit;
		
		if (definition.getBottleItemId() != null)
		{
			var bottleItem = new ItemRequirement("Bottle", definition.getBottleItemId());
			bottleItem.alsoCheckBank();
			completionRequirement = or(completedVarbit, bottleItem);
		}
		
		// Task can be done if: varbit is 0 (not completed via varbit) AND quest requirement met AND task not complete (via bottle)
		var varbitNotComplete = new VarbitRequirement(definition.getVarbitId(), 0);
		var taskNotComplete = not(completionRequirement);
		canDoRequirement = and(varbitNotComplete, not(reqWithQuest), taskNotComplete);
	}
}
