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
package com.questhelper.helpers.miniquests.lairoftarnrazorlor;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class LairOfTarnRazorlor extends BasicQuestHelper
{
	//Requirements
	ItemRequirement combatGear, diary;

	Requirement inBossRoom, killedTarn, inFinalRoom;

	DetailedQuestStep enterFinalRoom, pickUpDiary;

	NpcStep killTarn;

	TarnRoute tarnRoute;

	//Zones
	Zone bossRoom, finalRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep fullQuest = new ConditionalStep(this, tarnRoute);
		fullQuest.addStep(inFinalRoom, pickUpDiary);
		fullQuest.addStep(new Conditions(inBossRoom, killedTarn), enterFinalRoom);
		fullQuest.addStep(inBossRoom, killTarn);

		steps.put(0, fullQuest);
		steps.put(1, fullQuest);
		steps.put(2, fullQuest);
		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		diary = new ItemRequirement("Tarn's diary", ItemID.LOTR_DIARY);
		diary.setHighlightInInventory(true);
	}

	@Override
	protected void setupZones()
	{
		bossRoom = new Zone(new WorldPoint(3176, 4611, 0), new WorldPoint(3196, 4626, 0));
		finalRoom = new Zone(new WorldPoint(3181, 4632, 0), new WorldPoint(3191, 4637, 0));
	}

	public void setupConditions()
	{
		inBossRoom = new ZoneRequirement(bossRoom);
		inFinalRoom = new ZoneRequirement(finalRoom);

		killedTarn = new VarbitRequirement(VarbitID.LOTR_INSTANCE_ENTERED, 2, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		tarnRoute = new TarnRoute(this);
		killTarn = new NpcStep(this, NpcID.LOTR_TRAN_RAZORLOR_MUTANT, new WorldPoint(3186, 4619, 0), "Kill Mutant and Ghost Tarn.");
		killTarn.addAlternateNpcs(NpcID.LOTR_TRAN_RAZORLOR, NpcID.LOTR_TRAN_RAZORLOR_GHOST);

		enterFinalRoom = new ObjectStep(this, ObjectID.LOTR_RUINS_TRAN_DOOR_OPEN, new WorldPoint(3186, 4627, 0), "Go into the north passageway. If you would like to complete a task for the Morytania Diary, you should kill a Terror Dog now.");
		pickUpDiary = new ItemStep(this, "Pick up Tarn's diary. Quest complete!", diary);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(combatGear);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Tarn (level 69) twice");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.HAUNTED_MINE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.SLAYER, 40));
		return req;
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.SLAYER, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Tarn's Diary", ItemID.LOTR_DIARY, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("The ability to enchant Salve Amulets."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails fullQuestPanel = new PanelDetails("Traversing the dungeon", tarnRoute.getDisplaySteps(), combatGear);
		fullQuestPanel.addSteps(killTarn, enterFinalRoom, pickUpDiary);
		allSteps.add(fullQuestPanel);
		return allSteps;
	}
}
