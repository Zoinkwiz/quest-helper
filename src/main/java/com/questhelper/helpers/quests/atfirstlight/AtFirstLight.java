/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.atfirstlight;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestDescriptor;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuestDescriptor(
	quest = QuestHelperQuest.AT_FIRST_LIGHT
)
public class AtFirstLight extends BasicQuestHelper
{
	//Items Required
	ItemRequirement needle, boxTrap, hammer, jerboaTail;

	// Items Recommended
	ItemRequirement staminaPotion;

	// Quest Items
	Requirement toyMouse, toyMouseWound, smoothLeaf, stickyLeaf, makeshiftPoultice, furSample, trimmedFur, foxsReport;

	QuestStep talkToApatura, goDownTree, talkToVerity, talkToWolf, petKiko, windUpToy, useToyOnKiko,
		checkBed, returnToWolf, goUpTree, talkToFox, takeLeaf, takeSecondLeaf, catchJerboa, useTailOnLeaves,
		returnToFox, talkToAtza, makeEquipmentPile, talkToAtzaForTrim, returnToFoxAfterTrim, goDownTreeEnd, talkToVerityEnd,
		useJerboaTailOnBed, talkToApaturaToFinishQuest;

	//Zones
	Zone z;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupRequirements();
		setupConditions();
		setupSteps();

//		steps.put(1, step);

		return steps;

		// NpcID.REGULUS_CENTO_12883
		// Told about going to place, 9652 1->2
		// "Let's do it!"

		// Gone to place:

		// 9650 0->1
		// 9652 2->3
		// Varp 4066 238 -> 494

		// Talked about twins to west
		// 9652 3->4
	}

	private void setupZones()
	{

	}

	@Override
	public void setupRequirements()
	{
		// Required
		needle = new ItemRequirement("Needle", ItemID.NEEDLE).isNotConsumed();
		needle.canBeObtainedDuringQuest();
		boxTrap = new ItemRequirement("Box trap", ItemID.BOX_TRAP).isNotConsumed();
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		hammer.canBeObtainedDuringQuest();
		jerboaTail = new ItemRequirement("Jerboa tails", ItemID.JERBOA_TAIL);
		jerboaTail.canBeObtainedDuringQuest();

		// Recommended
		staminaPotion = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		// Quest Items
		toyMouse = new ItemRequirement("Toy mouse", ItemID.TOY_MOUSE);
		toyMouseWound = new ItemRequirement("Toy mouse (wound)", ItemID.TOY_MOUSE_WOUND);
		smoothLeaf = new ItemRequirement("Smooth leaf", ItemID.SMOOTH_LEAF);
		stickyLeaf = new ItemRequirement("Sticky leaf", ItemID.STICKY_LEAF);
		makeshiftPoultice = new ItemRequirement("Makeshift poultice", ItemID.MAKESHIFT_POULTICE);
		furSample = new ItemRequirement("Fur sample", ItemID.FUR_SAMPLE);
		trimmedFur = new ItemRequirement("Trimmed fur", ItemID.TRIMMED_FUR);
		foxsReport = new ItemRequirement("Fox's report", ItemID.FOXS_REPORT);
	}

	private void setupConditions()
	{

	}

	private void setupSteps()
	{


	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(needle, boxTrap, hammer, jerboaTail.quantity(2));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(staminaPotion);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new SkillRequirement(Skill.HUNTER, 46),
			new SkillRequirement(Skill.HERBLORE, 30),
			new SkillRequirement(Skill.CONSTRUCTION, 27),
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED)
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
			new ExperienceReward(Skill.HUNTER, 4500),
			new ExperienceReward(Skill.CONSTRUCTION, 800),
			new ExperienceReward(Skill.HERBLORE, 500)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to Master Tier Hunters' Rumours"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

//		allSteps.add(new PanelDetails("", Arrays.asList());

		return allSteps;
	}
}
