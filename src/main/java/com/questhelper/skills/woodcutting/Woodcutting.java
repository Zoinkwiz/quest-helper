/*
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.skills.woodcutting;

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
	quest = QuestHelperQuest.WOODCUTTING
)
public class Woodcutting extends ComplexStateQuestHelper
{
	//Items Required
	ItemRequirement ironAxe, steelAxe, blackAxe, mithrilAxe, adamantAxe, runeAxe;

	// Levels for axes
	SkillRequirement wc6, wc11, wc21, wc31, wc41;
	SkillRequirement at5, at10, at20, at30, at40;

	SkillRequirement wc15, wc30;

	QuestStep chopNormalTree, chopOakTrees, chopWillowTrees;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, chopNormalTree);
		fullTraining.addStep(wc30, chopWillowTrees);
		fullTraining.addStep(wc15, chopOakTrees);

		return fullTraining;
	}

	@Override
	public void setupRequirements()
	{
		at5 = new SkillRequirement(Skill.ATTACK, 5);
		at10 = new SkillRequirement(Skill.ATTACK, 10);
		at20 = new SkillRequirement(Skill.ATTACK, 20);
		at30 = new SkillRequirement(Skill.ATTACK, 30);
		at40 = new SkillRequirement(Skill.ATTACK, 40);

		wc6 = new SkillRequirement(Skill.WOODCUTTING, 6);
		wc11 = new SkillRequirement(Skill.WOODCUTTING, 11);
		wc21 = new SkillRequirement(Skill.WOODCUTTING, 21);
		wc31 = new SkillRequirement(Skill.WOODCUTTING, 31);
		wc41 = new SkillRequirement(Skill.WOODCUTTING, 41);


		wc15 = new SkillRequirement(Skill.WOODCUTTING, 15);
		wc30 = new SkillRequirement(Skill.WOODCUTTING, 30);

		ironAxe = new ItemRequirement("Iron axe", ItemID.IRON_AXE);
		ironAxe = ironAxe.showConditioned(new Conditions(LogicType.NOR, at5, wc6)
		).isNotConsumed();
		steelAxe = new ItemRequirement("Steel axe", ItemID.STEEL_AXE);
		steelAxe = steelAxe.showConditioned(
			new Conditions(at5, wc6, new Conditions(LogicType.NOR, at10, wc11))
		).isNotConsumed();
		blackAxe = new ItemRequirement("Black axe", ItemID.BLACK_AXE);
		blackAxe = blackAxe.showConditioned(
			new Conditions(at10, wc11, new Conditions(LogicType.NOR, at20, wc21))
		).isNotConsumed();
		mithrilAxe = new ItemRequirement("Mithril axe", ItemID.MITHRIL_AXE);
		mithrilAxe = mithrilAxe.showConditioned(
			new Conditions(at20, wc21, new Conditions(LogicType.NOR, at30, wc31))
		).isNotConsumed();
		adamantAxe = new ItemRequirement("Adamant axe", ItemID.ADAMANT_AXE);
		adamantAxe = adamantAxe.showConditioned(
			new Conditions(at30, wc31, new Conditions(LogicType.NOR, at40, wc41))
		).isNotConsumed();
		runeAxe = new ItemRequirement("Rune axe", ItemID.RUNE_AXE);
		runeAxe = runeAxe.showConditioned(new Conditions(at40, wc41)
		).isNotConsumed();
	}

	private void setupSteps()
	{
		chopNormalTree = new ObjectStep(this, ObjectID.TREE, new WorldPoint(3192, 3223, 0),
			"Chop normal trees around Lumbridge until 15 Woodcutting. You can choose to burn the logs as you go, drop" +
				" them, or bank them.", true, ironAxe, steelAxe, blackAxe
		);

		chopOakTrees = new ObjectStep(this, ObjectID.OAK_10820, new WorldPoint(3190, 3247, 0),
			"Chop oak trees around Lumbridge until 30 Woodcutting. You can choose to burn the logs as you go, drop" +
				" them, or bank them.", true, steelAxe, blackAxe, mithrilAxe, adamantAxe
		);

		chopWillowTrees = new ObjectStep(this, ObjectID.WILLOW, new WorldPoint(3059, 3253, 0),
			"Chop willow trees east of the Rusty Anchor Inn in Port Sarim until 99 Woodcutting. You can deposit them" +
				" at the bank deposit box just south on the docks next to the monks." +
				" If choose to burn the logs as you go or drop them, oak trees gives faster XP until 60 Woodcutting",
			true, steelAxe, blackAxe, mithrilAxe, adamantAxe, runeAxe
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("Ability to purchase Woodcutting Cape for 99k")
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(ironAxe, steelAxe, blackAxe, mithrilAxe, adamantAxe, runeAxe);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("1 - 15: Cut normal trees", Collections.singletonList(chopNormalTree),
			ironAxe, steelAxe, blackAxe));
		allSteps.add(new PanelDetails("15 - 30: Cut oak trees", Collections.singletonList(chopOakTrees),
			steelAxe, blackAxe, mithrilAxe, adamantAxe));
		allSteps.add(new PanelDetails("30 - 99: Cut willow trees", Collections.singletonList(chopWillowTrees),
			steelAxe, blackAxe, mithrilAxe, adamantAxe, runeAxe));
		return allSteps;
	}
}
