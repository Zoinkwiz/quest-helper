/*
 * Copyright (c) 2025, <insert quest helper creator here>
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
package com.questhelper.helpers.quests.impendingchaos;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;

/**
 * The quest guide for the "Impending Chaos" OSRS quest
 */
public class ImpendingChaos extends BasicQuestHelper
{
	QuestStep startQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		// TODO
	}

	@Override
	protected void setupRequirements()
	{
		// TODO
	}

	public void setupSteps()
	{
		var unreachableState = new DetailedQuestStep(this, "This state should not be reachable, please make a report with a screenshot in the Quest Helper discord.");

		// TODO: Implement
		startQuest = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Talk to Elias south of Ruins of Uzer to start the quest.");
		startQuest.addDialogStep("Yes.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		// TODO: Verify
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			// TODO
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("TODO", List.of(
			startQuest
		), List.of(
			// Requirements
		), List.of(
			// Recommended
		)));

		return panels;
	}
}
