/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.enlightenedjourney;

import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;

public class GrandTreeBalloonFlight extends ComplexStateQuestHelper
{
	BalloonFlightStep fly;

	ItemRequirement magicLogs;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();

		HashMap<Integer, List<Integer>> sections = new HashMap<>();
		List<Integer> section1 = Arrays.asList(8, 8, 8,  8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7);
		List<Integer> section2 = Arrays.asList(7, 7, 8, 7, 8, 7, 8, 8, 8, 9, 8, 8, 8, 10, 10, 10, 8, 8, 7, 7);
		List<Integer> section3 = Arrays.asList(7, 7, 8, 9, 10, 10, 10, 9, 10, 10, 10, 10, 10, 10, 8, 8, 6, 5, 5, 5);
		sections.put(7, section1);
		sections.put(8, section2);
		sections.put(9, section3);

		fly = new BalloonFlightStep(this, "Navigate the balloon on Entrana to the Grand Tree.", sections, magicLogs);
		return fly;
	}

	@Override
	protected void setupRequirements()
	{
		magicLogs = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS, 3);
	}

	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return Collections.singletonList(magicLogs);
	}


	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.ENLIGHTENED_JOURNEY, QuestState.FINISHED));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 60));
		return reqs;
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.FIREMAKING, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("Ability to fly via balloon to the Grand Tree with 1 magic log")
		);
	}


	@Override
    protected ArrayList<PanelDetails> setupPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Flying to the Grand Tree", Collections.singletonList(fly), magicLogs));

		return allSteps;
	}
}
