/*
 * Copyright (c) 2022, jLereback <https://github.com/jLereback>
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
package com.questhelper.skills.mining;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.MINING
)
public class Mining extends ComplexStateQuestHelper
{
	//Items Required
	ItemRequirement ironPickaxe, steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe;

	// Levels for pickaxes
	SkillRequirement mi6, mi11, mi21, mi31, mi41;
	SkillRequirement at5, at10, at20, at30, at40;

	SkillRequirement mi15;

	ObjectStep mineCopperOrTin, mineIron;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, mineCopperOrTin);
		fullTraining.addStep(mi15, mineIron);

		return fullTraining;
	}

	@Override
	public void setupRequirements()
	{
		mi6 = new SkillRequirement(Skill.MINING, 6);
		mi11 = new SkillRequirement(Skill.MINING, 11);
		mi21 = new SkillRequirement(Skill.MINING, 21);
		mi31 = new SkillRequirement(Skill.MINING, 31);
		mi41 = new SkillRequirement(Skill.MINING, 41);

		mi15 = new SkillRequirement(Skill.MINING, 15);

		ironPickaxe = new ItemRequirement(
			"Iron pickaxe", ItemID.IRON_PICKAXE).showConditioned(
			new Conditions(LogicType.NOR, mi6)).showConditioned(
			new Conditions(LogicType.NOR, at5)
		).isNotConsumed();
		steelPickaxe = new ItemRequirement(
			"Steel pickaxe", ItemID.STEEL_PICKAXE).showConditioned(
			new Conditions(mi6, new Conditions(LogicType.NOR, mi11))).showConditioned(
			new Conditions(at5, new Conditions(LogicType.NOR, at10))
		).isNotConsumed();
		blackPickaxe = new ItemRequirement(
			"Black pickaxe", ItemID.BLACK_PICKAXE).showConditioned(
			new Conditions(mi11, new Conditions(LogicType.NOR, mi21))).showConditioned(
			new Conditions(at10, new Conditions(LogicType.NOR, at20))
		).isNotConsumed();
		mithrilPickaxe = new ItemRequirement(
			"Mithril pickaxe", ItemID.MITHRIL_PICKAXE).showConditioned(
			new Conditions(mi21, new Conditions(LogicType.NOR, mi31))).showConditioned(
			new Conditions(at20, new Conditions(LogicType.NOR, at30))
		).isNotConsumed();
		adamantPickaxe = new ItemRequirement(
			"Adamant pickaxe", ItemID.ADAMANT_PICKAXE).showConditioned(
			new Conditions(mi31, new Conditions(LogicType.NOR, mi41))).showConditioned(
			new Conditions(at30, new Conditions(LogicType.NOR, at40))
		).isNotConsumed();
		runePickaxe = new ItemRequirement(
			"Rune pickaxe", ItemID.RUNE_PICKAXE).showConditioned(
			new Conditions(mi41)).showConditioned(
			new Conditions(at40)
		).isNotConsumed();
	}

	private void setupSteps()
	{
		mineCopperOrTin = new ObjectStep(this, ObjectID.ROCKS_11360, new WorldPoint(3287, 3366, 0),
			"Mine Copper ore and Tin ore at South-east Varrock mine until 15 Mining. You can choose to drop " +
				"the ores as you go or bank them in the eastern Varrock bank.", true,
			ironPickaxe, steelPickaxe, blackPickaxe
		);
		mineCopperOrTin.addAlternateObjects(ObjectID.ROCKS_11360, ObjectID.ROCKS_11161, ObjectID.ROCKS_10943);

		mineIron = new ObjectStep(this, ObjectID.ROCKS_11364, new WorldPoint(3294, 3310, 0),
			"Mine Iron ore at Al Kharid Mine until 99 Mining. You can choose to drop the ores as you go" +
				" or bank them at the Al Kharid bank.", true,
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe
		);
		mineIron.addAlternateObjects(ObjectID.ROCKS_11365);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("Ability to purchase Mining Cape for 99k")
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ironPickaxe, steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("1 - 15: Mine Copper/Tin", Collections.singletonList(mineCopperOrTin),
			ironPickaxe, steelPickaxe, blackPickaxe));
		allSteps.add(new PanelDetails("15 - 99: Mine Iron ore", Collections.singletonList(mineIron),
			steelPickaxe, blackPickaxe, mithrilPickaxe, adamantPickaxe, runePickaxe));
		return allSteps;
	}
}
