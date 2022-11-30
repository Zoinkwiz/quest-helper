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
package com.questhelper.quests.agility;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
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
	quest = QuestHelperQuest.AGILITY
)
public class Agility extends ComplexStateQuestHelper
{

	// Items recommended
	ItemRequirement bootsOfLightness, gracefulOutfit, gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape;

	SkillRequirement ag10, ag20, ag30, ag40, ag50, ag60, ag70, ag80, ag90;

	SkillRequirement ag45;

	QuestStep gnomeStronghold, draynorVillage, alKharid, varrock, canifis, falador, seersVillage, pollnivneach, rellekka, ardougne;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep fullTraining = new ConditionalStep(this, gnomeStronghold);
		fullTraining.addStep(ag90, ardougne, true);
		fullTraining.addStep(ag80, rellekka, true);
		fullTraining.addStep(ag70, pollnivneach, true);
		fullTraining.addStep(ag60, seersVillage, true);
		fullTraining.addStep(ag50, falador, true);
		fullTraining.addStep(ag40, canifis, true);
		fullTraining.addStep(ag30, varrock, true);
		fullTraining.addStep(ag20, alKharid, true);
		fullTraining.addStep(ag10, draynorVillage, true);

		return fullTraining;
	}

	@Override
	public void setupRequirements()
	{
		ag10 = new SkillRequirement(Skill.AGILITY, 10);
		ag20 = new SkillRequirement(Skill.AGILITY, 20);
		ag30 = new SkillRequirement(Skill.AGILITY, 30);
		ag40 = new SkillRequirement(Skill.AGILITY, 40);
		ag50 = new SkillRequirement(Skill.AGILITY, 50);
		ag60 = new SkillRequirement(Skill.AGILITY, 60);
		ag70 = new SkillRequirement(Skill.AGILITY, 70);
		ag80 = new SkillRequirement(Skill.AGILITY, 80);
		ag90 = new SkillRequirement(Skill.AGILITY, 90);

		ag45 = new SkillRequirement(Skill.AGILITY, 45);


		bootsOfLightness = new ItemRequirement(
			"Boots of Lightness", ItemID.BOOTS_OF_LIGHTNESS).showConditioned(
			new Conditions(LogicType.NOR, ag45)
		).isNotConsumed();

		gracefulHood = new ItemRequirement(
			"Graceful hood", ItemCollections.GRACEFUL_HOOD, 1 ,true).showConditioned(
			new Conditions(ag45)
		).isNotConsumed();

		gracefulTop = new ItemRequirement(
			"Graceful top", ItemCollections.GRACEFUL_TOP, 1, true).showConditioned(
			new Conditions(ag45)
		).isNotConsumed();

		gracefulLegs = new ItemRequirement(
			"Graceful legs", ItemCollections.GRACEFUL_LEGS, 1, true).showConditioned(
			new Conditions(ag45)
		).isNotConsumed();

		gracefulCape = new ItemRequirement(
			"Graceful cape", ItemCollections.GRACEFUL_CAPE, 1, true).showConditioned(
			new Conditions(ag45)
		).isNotConsumed();

		gracefulGloves = new ItemRequirement(
			"Graceful gloves", ItemCollections.GRACEFUL_GLOVES, 1, true).showConditioned(
			new Conditions(ag45)
		).isNotConsumed();

		gracefulBoots = new ItemRequirement(
			"Graceful boots", ItemCollections.GRACEFUL_BOOTS, 1, true).showConditioned(
			new Conditions(ag45)
		).isNotConsumed();
		gracefulBoots.addAlternates(ItemID.BOOTS_OF_LIGHTNESS);

		gracefulOutfit = new ItemRequirements(
			"Graceful outfit (equipped)",
			gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape
		).isNotConsumed();
	}

	private void setupSteps()
	{
		gnomeStronghold = new ObjectStep(this, ObjectID.LOG_BALANCE_23145, new WorldPoint(2474, 3435, 0),
			"Train agility at the Gnome Stronghold Agility Course.",  Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		draynorVillage = new ObjectStep(this, ObjectID.ROUGH_WALL, new WorldPoint(3103, 3279, 0),
			"Train agility at the Draynor Village Rooftop Course.",  Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		alKharid = new ObjectStep(this, ObjectID.ROUGH_WALL_11633, new WorldPoint(3273, 3195, 0),
			"Train agility at the Al Kharid Rooftop Course.",  Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		varrock = new ObjectStep(this, ObjectID.ROUGH_WALL_14412, new WorldPoint(3221, 3414, 0),
			"Train agility at the Varrock Rooftop Course.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		canifis = new ObjectStep(this, ObjectID.TALL_TREE_14843, new WorldPoint(3508, 3488, 0),
			"Train agility at the Canifis Rooftop Course. For best spawn of Mark of Grace, " +
				"stay on this course until 60 Agility.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		falador = new ObjectStep(this, ObjectID.ROUGH_WALL_14898, new WorldPoint(3036, 3341, 0),
			"Train agility at the Falador Rooftop Course. For best spawn of Mark of Grace," +
				" stay on Canifis Rooftop Course until 60 Agility, then go directly to Seer's Village.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		seersVillage = new ObjectStep(this, ObjectID.WALL_14927, new WorldPoint(2729, 3489, 0),
			"Train agility at Seer's Village Rooftop Course.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		pollnivneach = new ObjectStep(this, ObjectID.BASKET_14935, new WorldPoint(3351, 2962, 0),
			"Train agility at the Pollnivneach Rooftop Course.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		rellekka = new ObjectStep(this, ObjectID.ROUGH_WALL_14946, new WorldPoint(2625, 2677, 0),
			"Train agility at the Rellekka Rooftop Course.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));

		ardougne = new ObjectStep(this, ObjectID.WOODEN_BEAMS, new WorldPoint(2673, 3298, 0),
			"Train agility at the Ardougne Rooftop Course.", Collections.EMPTY_LIST, Collections.singletonList(gracefulOutfit));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("Ability to purchase AGILITY Cape for 99k")
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(
			bootsOfLightness, gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(
			new PanelDetails("1 - 10: Gnome Stronghold", Collections.singletonList(gnomeStronghold))
		);
		allSteps.add(
			new PanelDetails("10 - 20: Draynor Village", Collections.singletonList(draynorVillage))
		);
		allSteps.add(
			new PanelDetails("20 - 30: Al Kharid", Collections.singletonList(alKharid))
		);
		allSteps.add(
			new PanelDetails("30 - 40: Varrock", Collections.singletonList(varrock))
		);
		allSteps.add(
			new PanelDetails("40 - 50/60: Canifis", Collections.singletonList(canifis))
		);
		allSteps.add(
			new PanelDetails("50 - 60: Falador", Collections.singletonList(falador))
		);
		allSteps.add(
			new PanelDetails("60 - 70: Seer's Village", Collections.singletonList(seersVillage))
		);
		allSteps.add(
			new PanelDetails("70 - 80: Pollnivneach", Collections.singletonList(pollnivneach))
		);
		allSteps.add(
			new PanelDetails("80 - 90: Rellekka", Collections.singletonList(rellekka))
		);
		allSteps.add(
			new PanelDetails("90 - 99: Ardougne", Collections.singletonList(ardougne))
		);
		return allSteps;
	}
}
