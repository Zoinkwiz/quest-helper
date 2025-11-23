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
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;

@Getter
public class ChartingWeatherStep extends ConditionalStep implements ChartingTaskInterface
{
	private Requirement incompleteRequirement;
	private Requirement canDoRequirement;

	// Steps
	private ChartingTaskNpcStep talkToNpcStep;
	private DetailedQuestStep measureWeatherStep;
	private ChartingTaskNpcStep returnToNpcStep;

	// Requirements
	private ItemRequirement weatherStationEmpty;
	private ItemRequirement weatherStationFull;

	public ChartingWeatherStep(QuestHelper questHelper, ChartingTaskDefinition definition, Requirement... requirements)
	{
		super(questHelper, new DetailedQuestStep(questHelper, "Loading weather charting task..."));

		setupRequirements();
		setupSteps(questHelper, definition, requirements);
		addConditionalSteps();
		setupSidebarRequirements(definition);
		addDialogStep("Can I help you with collecting weather data?");

		setText("[" + definition.getType().getDisplayName() + "] " + definition.getDescription());
	}

	private void setupRequirements()
	{
		weatherStationEmpty = new ItemRequirement("Portable weather station", ItemID.SAILING_CHARTING_WEATHER_STATION_EMPTY);
		weatherStationFull = new ItemRequirement("Data filled portable weather station", ItemID.SAILING_CHARTING_WEATHER_STATION_FULL);
	}

	private void setupSteps(QuestHelper questHelper, ChartingTaskDefinition definition, Requirement... requirements)
	{
		talkToNpcStep = new ChartingTaskNpcStep(questHelper, NpcID.SAILING_CHARTING_WEATHER_TROLL, definition, requirements);
		talkToNpcStep.setText("Talk to the Meaty Aura Logist to get a weather station.");

		measureWeatherStep = new DetailedQuestStep(questHelper, "Sail to the weather location and measure the winds.", weatherStationEmpty);
		measureWeatherStep.setWorldPoint(definition.getSecondaryWorldPoint());

		returnToNpcStep = new ChartingTaskNpcStep(questHelper, NpcID.SAILING_CHARTING_WEATHER_TROLL, definition, requirements);
		returnToNpcStep.setText("Return to the Meaty Aura Logist with the full weather station.");
		returnToNpcStep.addRequirement(weatherStationFull);
	}

	private void addConditionalSteps()
	{
		addStep(weatherStationFull, returnToNpcStep);
		addStep(weatherStationEmpty, measureWeatherStep);
		addStep(null, talkToNpcStep);
	}

	private void setupSidebarRequirements(ChartingTaskDefinition definition)
	{
		var sailingRequirement = new SkillRequirement(Skill.SAILING, Math.max(1, definition.getLevel()));
		var completedRequirement = new VarbitRequirement(definition.getVarbitId(), 1);
		var levelNotMet = nor(sailingRequirement);
		levelNotMet.setText("You need to meet level " + sailingRequirement.getRequiredLevel() + " Sailing.");

		conditionToHideInSidebar(completedRequirement);
		conditionToFadeInSidebar(levelNotMet);

		canDoRequirement = and(new VarbitRequirement(definition.getVarbitId(), 0), sailingRequirement);
		incompleteRequirement = new VarbitRequirement(definition.getVarbitId(), 0);
	}
}
