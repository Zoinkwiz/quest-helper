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
import com.questhelper.helpers.activities.charting.ChartingTaskInterface;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ObjectStep;
import lombok.Getter;
import net.runelite.api.Skill;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;

@Getter
public class ChartingTaskObjectStep extends ObjectStep implements ChartingTaskInterface
{
	private Requirement incompleteRequirement;
	protected Requirement canDoRequirement;

	ChartingTaskObjectStep(QuestHelper questHelper, int objectID, ChartingTaskDefinition definition, Requirement... requirements)
	{
		super(questHelper, objectID, "[" + definition.getType().getDisplayName() + "] " + definition.getDescription(), requirements);
		setupChartingDetails(definition);
	}

	public void setupChartingDetails(ChartingTaskDefinition definition)
	{
		var point = definition.getWorldPoint();
		if (point != null)
		{
			setWorldPoint(point);
		}
		else
		{
			setHideWorldArrow(true);
			setHideMinimapLines(true);
		}

		var sailingRequirement = new SkillRequirement(Skill.SAILING, Math.max(1, definition.getLevel()));
		addRequirement(sailingRequirement);

		// Additional reqs and recc
		setupRequiredAndRecommended(definition);

		var completedRequirement = new VarbitRequirement(definition.getVarbitId(), 1);
		var levelNotMet = nor(sailingRequirement);
		levelNotMet.setText("You need to meet level " + sailingRequirement.getRequiredLevel() + " Sailing.");
		conditionToHideInSidebar(completedRequirement);
		conditionToFadeInSidebar(levelNotMet);

		canDoRequirement = and(new VarbitRequirement(definition.getVarbitId(), 0), sailingRequirement);
		incompleteRequirement = new VarbitRequirement(definition.getVarbitId(), 0);
	}

	@Override
	public void setupRequiredAndRecommended(ChartingTaskDefinition definition)
	{
		// Additional reqs and recc
		if (!definition.getAdditionalRequirements().isEmpty())
		{
			addRequirement(definition.getAdditionalRequirements().toArray(new Requirement[0]));
		}
		if (!definition.getAdditionalRecommended().isEmpty())
		{
			addRecommended(definition.getAdditionalRecommended());
		}
	}
}
