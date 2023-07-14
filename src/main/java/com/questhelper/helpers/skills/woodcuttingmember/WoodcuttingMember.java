/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.skills.woodcuttingmember;

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
	quest = QuestHelperQuest.WOODCUTTING_MEMBER
)
public class WoodcuttingMember extends ComplexStateQuestHelper
{
	//Items Required
	ItemRequirement ironAxe, steelAxe, blackAxe, mithrilAxe, adamantAxe, runeAxe, dragonAxe;

	// Items recommended
	ItemRequirement lumberjackHat, lumberjackBody, lumberjackLegs, lumberjackBoots;

	SkillRequirement wc6, wc11, wc21, wc31, wc41, wc61;

	SkillRequirement wc15, wc35;

	QuestStep chopNormalTree, chopOakTrees, chopTeakTrees;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, chopNormalTree);
		fullTraining.addStep(wc35, chopTeakTrees);
		fullTraining.addStep(wc15, chopOakTrees);

		return fullTraining;
	}

	@Override
	public void setupRequirements()
	{
		wc6 = new SkillRequirement(Skill.WOODCUTTING, 6);
		wc11 = new SkillRequirement(Skill.WOODCUTTING, 11);
		wc21 = new SkillRequirement(Skill.WOODCUTTING, 21);
		wc31 = new SkillRequirement(Skill.WOODCUTTING, 31);
		wc41 = new SkillRequirement(Skill.WOODCUTTING, 41);
		wc61 = new SkillRequirement(Skill.WOODCUTTING, 61);

		wc15 = new SkillRequirement(Skill.WOODCUTTING, 15);
		wc35 = new SkillRequirement(Skill.WOODCUTTING, 35);

		ironAxe = new ItemRequirement("Iron axe", ItemID.IRON_AXE).showConditioned(
			new Conditions(LogicType.NOR, wc6)
		).isNotConsumed();
		steelAxe = new ItemRequirement("Steel axe", ItemID.STEEL_AXE).showConditioned(
			new Conditions(wc6, new Conditions(LogicType.NOR, wc11))
		).isNotConsumed();
		blackAxe = new ItemRequirement("Black axe", ItemID.BLACK_AXE).showConditioned(
			new Conditions(wc11, new Conditions(LogicType.NOR, wc21))
		).isNotConsumed();
		mithrilAxe = new ItemRequirement("Mithril axe", ItemID.MITHRIL_AXE).showConditioned(
			new Conditions(wc21, new Conditions(LogicType.NOR, wc31))
		).isNotConsumed();
		adamantAxe = new ItemRequirement("Adamant axe", ItemID.ADAMANT_AXE).showConditioned(
			new Conditions(wc31, new Conditions(LogicType.NOR, wc41))
		).isNotConsumed();
		runeAxe = new ItemRequirement("Rune axe", ItemID.RUNE_AXE).showConditioned(
			new Conditions(wc41, new Conditions(LogicType.NOR, wc61))
		).isNotConsumed();
		dragonAxe = new ItemRequirement("Dragon axe", ItemID.DRAGON_AXE).showConditioned(
			new Conditions(wc61)
		).isNotConsumed();


		lumberjackBody = new ItemRequirement("Lumberjack top", ItemID.LUMBERJACK_TOP);
		lumberjackBody = lumberjackBody.showConditioned(lumberjackBody.alsoCheckBank(questBank));

		lumberjackHat = new ItemRequirement("Lumberjack hat", ItemID.LUMBERJACK_HAT);
		lumberjackHat = lumberjackHat.showConditioned(lumberjackHat.alsoCheckBank(questBank));

		lumberjackLegs = new ItemRequirement("Lumberjack legs", ItemID.LUMBERJACK_LEGS);
		lumberjackLegs = lumberjackLegs.showConditioned(lumberjackLegs.alsoCheckBank(questBank));

		lumberjackBoots = new ItemRequirement("Lumberjack boots", ItemID.LUMBERJACK_BOOTS);
		lumberjackBoots = lumberjackBoots.showConditioned(lumberjackBoots.alsoCheckBank(questBank));
	}

	private void setupSteps()
	{
		chopNormalTree = new ObjectStep(this, ObjectID.TREE, new WorldPoint(3192, 3223, 0),
			"Chop normal trees around Lumbridge until 15 Woodcutting. You can choose to burn the logs as you go, drop" +
				" them, or bank them.", true, ironAxe, steelAxe, blackAxe, lumberjackHat, lumberjackBody, lumberjackLegs,
			lumberjackBoots);

		chopOakTrees = new ObjectStep(this, ObjectID.OAK_TREE_10820, new WorldPoint(3190, 3247, 0),
			"Chop oak trees around Lumbridge until 35 Woodcutting. You can choose to burn the logs as you go, drop" +
				" them, or bank them.", true, blackAxe, mithrilAxe, adamantAxe,
			lumberjackHat, lumberjackBody, lumberjackLegs, lumberjackBoots);

		chopTeakTrees = new ObjectStep(this, ObjectID.TEAK_TREE, new WorldPoint(2335, 3048, 0),
			"Chop teak trees south of Castle Wars until 99 Woodcutting. You can choose to burn the logs as you go, " +
				"drop them, or bank them.", true, adamantAxe, runeAxe, dragonAxe, lumberjackHat, lumberjackBody, lumberjackLegs,
			lumberjackBoots);
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
		return Arrays.asList(ironAxe, steelAxe, blackAxe, mithrilAxe, adamantAxe, runeAxe, dragonAxe);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(lumberjackHat, lumberjackBody, lumberjackLegs, lumberjackBoots);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("1 - 15: Cut normal trees", Collections.singletonList(chopNormalTree), Arrays.asList(ironAxe,
			steelAxe, blackAxe), Arrays.asList(lumberjackHat, lumberjackBody, lumberjackLegs, lumberjackBoots)));
		allSteps.add(new PanelDetails("15 - 35: Cut oak trees", Collections.singletonList(chopOakTrees), Arrays.asList(blackAxe,
			mithrilAxe, adamantAxe), Arrays.asList(lumberjackHat, lumberjackBody, lumberjackLegs, lumberjackBoots)));
		allSteps.add(new PanelDetails("35 - 99: Cut teak trees", Collections.singletonList(chopTeakTrees), Arrays.asList(adamantAxe,
			runeAxe, dragonAxe), Arrays.asList(lumberjackHat, lumberjackBody, lumberjackLegs, lumberjackBoots)));
		return allSteps;
	}
}
