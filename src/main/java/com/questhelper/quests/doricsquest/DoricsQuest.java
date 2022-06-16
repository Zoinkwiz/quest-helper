/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.doricsquest;

import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.NpcStep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.DORICS_QUEST
)
public class DoricsQuest extends BasicQuestHelper
{
	//Items Required
	ItemRequirement clay, copper, iron;

	//NPC Steps
	QuestStep talkToDoric;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToDoric);
		steps.put(10, talkToDoric);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		clay = new ItemRequirement("Clay (UNNOTED)", ItemID.CLAY, 6);
		copper = new ItemRequirement("Copper ore (UNNOTED)", ItemID.COPPER_ORE, 4);
		iron = new ItemRequirement("Iron ore (UNNOTED)", ItemID.IRON_ORE, 2);
	}

	public void setupSteps()
	{
		talkToDoric = new NpcStep(this, NpcID.DORIC, new WorldPoint(2951, 3451, 0), "Bring Doric north of Falador all the required items. You can mine them all in the Dwarven Mines, or buy them from the Grand Exchange.", clay, copper, iron);
		talkToDoric.addDialogStep("I wanted to use your anvils.");
		talkToDoric.addDialogStep("Yes, I will get you the materials.");
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.MINING, 15, true, "15 Mining to get ores yourself"));
		return req;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(clay);
		reqs.add(copper);
		reqs.add(iron);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.MINING, 1300));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("180 Coins", ItemID.COINS_995, 180));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Use of Doric's Anvil"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Help Doric", Collections.singletonList(talkToDoric), clay, copper, iron));
		return allSteps;
	}
}
