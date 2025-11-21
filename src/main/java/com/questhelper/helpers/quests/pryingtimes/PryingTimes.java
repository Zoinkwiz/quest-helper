/*
 * Copyright (c) 2025, TTvanWillegen
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
package com.questhelper.helpers.quests.pryingtimes;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeSailingTaskSlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

public class PryingTimes extends BasicQuestHelper
{
	ItemRequirement hammerRequirement, steelBarRequirement, redberryPieRequirement, captainsLogRequirement;
	SkillRequirement sailingSkillRequirement, smithingSkillRequirement;
	QuestRequirement pandemoniumQuestRequirement, knightsSwordQuestRequirement;
	FreeSailingTaskSlotRequirement freeTaskSlotRequirement;

	NpcStep startQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0,  startQuest);

		return steps;
	}

	public void setupConditions()
	{

	}

	@Override
	protected void setupZones()
	{

	}

	@Override
	protected void setupRequirements()
	{
		//Quest Requirements
		hammerRequirement = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		hammerRequirement.setTooltip("You can pick this up at the shipyard.");
		steelBarRequirement = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 1);
		redberryPieRequirement = new ItemRequirement("Redberry pie", ItemID.REDBERRY_PIE, 1);
		captainsLogRequirement = new ItemRequirement("Captain's log", ItemID.SAILING_LOG, 1);
		sailingSkillRequirement = new SkillRequirement(Skill.SAILING, 12);
		smithingSkillRequirement = new SkillRequirement(Skill.SMITHING, 30);
		pandemoniumQuestRequirement = new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED);
		knightsSwordQuestRequirement = new QuestRequirement(QuestHelperQuest.THE_KNIGHTS_SWORD, QuestState.FINISHED);
		freeTaskSlotRequirement = new FreeSailingTaskSlotRequirement(1);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Talk to Steve to start the quest.", true);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(hammerRequirement, steelBarRequirement, redberryPieRequirement, captainsLogRequirement);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(sailingSkillRequirement, smithingSkillRequirement, pandemoniumQuestRequirement, knightsSwordQuestRequirement, freeTaskSlotRequirement);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(new ExperienceReward(Skill.SAILING, 800), new ExperienceReward(Skill.SMITHING, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Sawmill Coupon (oak plank)", ItemID.SAWMILL_COUPON, 25)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited Crowbars from the crate of crowbars"),
			new UnlockReward("Ability to chart forgotten drinks"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("How's Old Grog?", Arrays.asList(startQuest)));
		return allSteps;
	}
}
