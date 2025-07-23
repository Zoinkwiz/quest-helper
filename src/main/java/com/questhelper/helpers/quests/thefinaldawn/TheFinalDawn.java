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
package com.questhelper.helpers.quests.thefinaldawn;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

/**
 * The quest guide for the "The Final Dawn" OSRS quest
 */
public class TheFinalDawn extends BasicQuestHelper
{
	ItemRequirement emissaryRobesEquipped, emissaryRobes, bone, rangedGear;

	ItemRequirement combatGear, food, prayerPotions;

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
		var emissaryHood = new ItemRequirement("Emissary hood", ItemID.VMQ3_CULTIST_HOOD);
		var emissaryTop = new ItemRequirement("Emissary top", ItemID.VMQ3_CULTIST_ROBE_TOP);
		var emissaryBottom = new ItemRequirement("Emissary bottom", ItemID.VMQ3_CULTIST_ROBE_BOTTOM);
		var emissaryBoots = new ItemRequirement("Emissary sandals", ItemID.VMQ3_CULTIST_SANDALS);
		emissaryRobesEquipped = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom,
				emissaryBoots).equipped();
		emissaryRobes = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom, emissaryBoots);

		// TODO: Add remaining bones
		bone = new ItemRequirement("Any type of bone", ItemID.BONES);
		bone.addAlternates(ItemID.BIG_BONES, ItemID.BONES_BURNT, ItemID.WOLF_BONES, ItemID.BAT_BONES, ItemID.DAGANNOTH_KING_BONES);

		rangedGear = new ItemRequirement("Ranged/Magic Combat gear", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());


		// Item Recommended
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		food.setUrlSuffix("Food");

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

	}

	public void setupSteps()
	{
		// TODO: Implement
		startQuest = new NpcStep(this, NpcID.ELIAS_WHITE_VIS, new WorldPoint(3505, 3037, 0), "Talk to Elias south of Ruins of Uzer to start the quest.");
		startQuest.addDialogStep("Yes.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			emissaryRobes, bone, combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			rangedGear, food, prayerPotions
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
				new QuestRequirement(QuestHelperQuest.THE_HEART_OF_DARKNESS, QuestState.FINISHED),
				new QuestRequirement(QuestHelperQuest.PERILOUS_MOON, QuestState.FINISHED),
				new SkillRequirement(Skill.THIEVING, 66),
				new SkillRequirement(Skill.FLETCHING, 52),
				new SkillRequirement(Skill.RUNECRAFT, 52)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		// TODO: Add in rest
		return List.of(
			"Emissary Enforcer (lvl-196)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 55000),
			new ExperienceReward(Skill.FLETCHING, 25000),
			new ExperienceReward(Skill.RUNECRAFT, 25000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The Arkan Blade"),
			new UnlockReward("Access to Mokhaiotl"),
			new UnlockReward("Access to Crypt of Tonali")
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("55,000 Experience Lamps (Combat Skills)", ItemID.THOSF_REWARD_LAMP, 1)
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
