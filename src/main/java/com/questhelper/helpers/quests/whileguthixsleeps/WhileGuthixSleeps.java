/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.quests.whileguthixsleeps;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class WhileGuthixSleeps extends BasicQuestHelper
{
	//Items Required
	ItemRequirement sapphireLantern, litSapphireLantern, airRunes, earthRunes, fireRunes, waterRunes, mindRunes, lawRunes,
		deathRunes, log, charcoal, papyrus, lanternLens, mortMyreFungus, unpoweredOrb, ringOfCharosA, coins, bronzeMedHelm,
		ironChainbody, chargeOrbSpell, meleeGear, rangedGear;

	QuestStep talkToIvy, questPlaceholder;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, questPlaceholder);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		sapphireLantern = new ItemRequirement("Emerald lantern", ItemID.SAPPHIRE_LANTERN_4701).isNotConsumed();
		litSapphireLantern = new ItemRequirement("Sapphire lantern", ItemID.SAPPHIRE_LANTERN_4702).isNotConsumed();
		litSapphireLantern.setTooltip("You can make this by using a cut sapphire on a bullseye lantern");

		airRunes = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		earthRunes = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		fireRunes = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE);
		waterRunes = new ItemRequirement("Water rune", ItemID.WATER_RUNE);
		mindRunes = new ItemRequirement("Mind rune", ItemID.MIND_RUNE);
		lawRunes = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		deathRunes = new ItemRequirement("Death rune", ItemID.DEATH_RUNE);
		log = new ItemRequirement("Logs", ItemID.LOGS);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		lanternLens = new ItemRequirement("Lantern lens", ItemID.LANTERN_LENS);
		mortMyreFungus = new ItemRequirement("Mort myre fungus", ItemID.MORT_MYRE_FUNGUS);
		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB);
		ringOfCharosA = new ItemRequirement("Ring of charos (a)", ItemID.RING_OF_CHAROSA);
		coins = new ItemRequirement("", ItemCollections.COINS);
		bronzeMedHelm = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM);
		ironChainbody = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY);

		ItemRequirement cosmic3 = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE, 3);
		ItemRequirement fire30 = new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 63));
		ItemRequirement air30 = new ItemRequirement("Air runes", ItemID.AIR_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 66));
		ItemRequirement water30 = new ItemRequirement("Water runes", ItemID.WATER_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 56));
		ItemRequirement earth30 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 60));
		ItemRequirements elemental30 = new ItemRequirements(LogicType.OR, "Elemental runes", air30, water30, earth30, fire30);
		elemental30.addAlternates(ItemID.FIRE_RUNE, ItemID.EARTH_RUNE, ItemID.AIR_RUNE);
		elemental30.setExclusiveToOneItemType(true);

		chargeOrbSpell = new ItemRequirements(LogicType.AND, "Runes for any charge orb spell you have the level to cast", cosmic3, elemental30);
		meleeGear = new ItemRequirement("Melee weapon", -1, -1).isNotConsumed();
		meleeGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		rangedGear = new ItemRequirement("Ranged weapon", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
	}

	public void setupSteps()
	{
		questPlaceholder = new DetailedQuestStep(this, "This is a very large quest, so it will be a while before a Quest Helper is made for it. Enjoy the quest if you do it yourself, it's one of the best ever made!");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Mystery", questPlaceholder));
		return allSteps;
	}
}
