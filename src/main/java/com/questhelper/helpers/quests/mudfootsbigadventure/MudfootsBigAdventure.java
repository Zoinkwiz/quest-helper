/*
 * Copyright (c) 2026, INSERT_YOUR_NAME_HERE
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
package com.questhelper.helpers.quests.mudfootsbigadventure;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class MudfootsBigAdventure extends BasicQuestHelper
{
	// Required items
	ItemRequirement sampleRequirement;

	// Recommended items
	ItemRequirement energyRestorePotion;

	// Mid-quest item requirements
	ItemRequirement itemAcquiredDuringQuest;

	// Zones
	Zone sampleZone;

	// Miscellaneous requirements
	VarbitRequirement hasFinishedCertainTask;

	// Steps
	NpcStep talkToNpc;
	ObjectStep climbUpLadder;

	@Override
	protected void setupZones()
	{
		sampleZone = new Zone(new WorldPoint(10, 10, 0), new WorldPoint(20, 20, 0));
	}

	@Override
	protected void setupRequirements()
	{
		sampleRequirement = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);

		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		itemAcquiredDuringQuest = new ItemRequirement("Book of Lore", ItemID.BOOK_OF_ASTROLOGY);

		hasFinishedCertainTask = new VarbitRequirement(VarbitID.PORT_TASK_SLOT_2_ID, 2);
	}

	void setupSteps()
	{
		talkToNpc = new NpcStep(this, NpcID.HANS, "Talk to Hans in Lumbridge", sampleRequirement);
		climbUpLadder = new ObjectStep(this, ObjectID.LADDER, "Climb up the ladder");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToNpc);
		steps.put(2, climbUpLadder);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 45, false)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			//
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			sampleRequirement
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			energyRestorePotion
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Big scary monster (lvl 200)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"This is a note to appear in the sidebar"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SAILING, 10000),
			new ExperienceReward(Skill.SLAYER, 8000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to something grand")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("The section's header!", List.of(
			talkToNpc,
			climbUpLadder
		), List.of(
			sampleRequirement
		)));

		return sections;
	}
}
