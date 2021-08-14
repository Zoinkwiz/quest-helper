/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.lumbridgeanddraynor;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.LUMBRIDGE_MEDIUM
)
public class lumbMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement animalMagnetism, fairyTaleII;

	Requirement not;

	QuestStep claimReward;

	Zone cave;

	ZoneRequirement inCave;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		// doHard.addStep(notUsedShortcut, useShortcut);

		return doMedium;
	}

	public void setupRequirements()
	{
		not = new VarplayerRequirement(3600, false, 1);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inCave = new ZoneRequirement(cave);

		fairyTaleII = new VarbitRequirement( QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(), Operation.GREATER_EQUAL, 40, "Partial completion of Fairytale II for access to fairy rings");
		animalMagnetism = new QuestRequirement(QuestHelperQuest.ANIMAL_MAGNETISM, QuestState.FINISHED);
	}

	public void loadZones()
	{
		cave = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
	}

	public void setupSteps()
	{

		claimReward = new NpcStep(this, NpcID.HATIUS_COSAINTUS, new WorldPoint(3235, 3213, 0),
			"Talk to Hatius Cosaintus in Lumbridge to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(70));
		reqs.add(new SkillRequirement(Skill.AGILITY, 20));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 38));
		reqs.add(new SkillRequirement(Skill.FISHING, 30));
		reqs.add(new SkillRequirement(Skill.HUNTER, 42));
		reqs.add(new SkillRequirement(Skill.MAGIC, 31));
		reqs.add(new SkillRequirement(Skill.RANGED, 50));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 23));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 19));
		reqs.add(new SkillRequirement(Skill.THIEVING, 38));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 36));

		reqs.add(fairyTaleII);
		reqs.add(animalMagnetism);

		return reqs;
	}


	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
