/*
 * Copyright (c) 2023, jLereback <https://github.com/jLereback, JesperBodin <https://github.com/JesperBodin>
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
package com.questhelper.helpers.skills.agility;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemID;
import static net.runelite.api.Skill.AGILITY;

public class Agility extends ComplexStateQuestHelper
{
	// Items recommended
	ItemRequirement bootsOfLightness, gracefulOutfit, gracefulHood, gracefulTop,
		gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape;

	SkillRequirement ag10, ag20, ag30, ag40, ag50, ag60, ag70, ag80, ag90;

	SkillRequirement ag45;

	AgilityCourse draynorVillage, alKharid, varrock, canifis,
		falador, seersVillage, pollnivneach, rellekka, ardougne;
	ConditionalStep draynorStep, alKharidStep, varrockStep, canifisStep,
		faladorStep, seersStep, pollnivneachStep, rellekkaStep, ardougneStep;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();

		draynorStep = draynorVillage.loadStep();
		alKharidStep = alKharid.loadStep();
		varrockStep = varrock.loadStep();
		canifisStep = canifis.loadStep();
		faladorStep = falador.loadStep();
		seersStep = seersVillage.loadStep();
		pollnivneachStep = pollnivneach.loadStep();
		rellekkaStep = rellekka.loadStep();
		ardougneStep = ardougne.loadStep();


		ConditionalStep superStep = new ConditionalStep(this, draynorStep);
		superStep.addStep(ag90, ardougneStep);
		superStep.addStep(ag80, rellekkaStep);
		superStep.addStep(ag70, pollnivneachStep);
		superStep.addStep(ag60, seersStep);
		superStep.addStep(ag40, canifisStep);
		superStep.addStep(ag50, faladorStep);
		superStep.addStep(ag30, varrockStep);
		superStep.addStep(ag20, alKharidStep);

		return superStep;
	}

	@Override
	protected void setupRequirements()
	{
		//Setup courses
		draynorVillage = new DraynorVillage(this);
		alKharid = new AlKharid(this);
		varrock = new Varrock(this);
		canifis = new Canifis(this);
		falador = new Falador(this);
		seersVillage = new SeersVillage(this);
		pollnivneach = new Pollnivneach(this);
		rellekka = new Rellekka(this);
		ardougne = new Ardougne(this);

		//Setup skill requirements
		ag10 = new SkillRequirement(AGILITY, 10);
		ag20 = new SkillRequirement(AGILITY, 20);
		ag30 = new SkillRequirement(AGILITY, 30);
		ag40 = new SkillRequirement(AGILITY, 40);
		ag45 = new SkillRequirement(AGILITY, 45);
		ag50 = new SkillRequirement(AGILITY, 50);
		ag60 = new SkillRequirement(AGILITY, 60);
		ag70 = new SkillRequirement(AGILITY, 70);
		ag80 = new SkillRequirement(AGILITY, 80);
		ag90 = new SkillRequirement(AGILITY, 90);

		//Setup item requirements
		bootsOfLightness = new ItemRequirement(
			"Boots of Lightness", ItemID.BOOTS_OF_LIGHTNESS).showConditioned(
			new Conditions(LogicType.NOR, ag45)
		).isNotConsumed();

		gracefulHood = new ItemRequirement(
			"Graceful hood", ItemCollections.GRACEFUL_HOOD, 1, true).showConditioned(
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


		draynorVillage.setRecommended(bootsOfLightness);
		alKharid.setRecommended(bootsOfLightness);
		varrock.setRecommended(bootsOfLightness);
		canifis.setRecommended(bootsOfLightness, gracefulOutfit);
		falador.setRecommended(gracefulOutfit);
		seersVillage.setRecommended(gracefulOutfit);
		pollnivneach.setRecommended(gracefulOutfit);
		rellekka.setRecommended(gracefulOutfit);
		ardougne.setRecommended(gracefulOutfit);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to purchase the Agility Cape for 99k"),
			new UnlockReward("Ability to traverse various shortcuts throughout Gielinor"),
			new UnlockReward("Increased run energy restoration")
		);
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return Arrays.asList(
			bootsOfLightness, gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape
		);
	}

	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(draynorVillage.getPanelDetails());
		allSteps.add(alKharid.getPanelDetails());
		allSteps.add(varrock.getPanelDetails());
		allSteps.add(canifis.getPanelDetails());
		allSteps.add(falador.getPanelDetails());
		allSteps.add(seersVillage.getPanelDetails());
		allSteps.add(pollnivneach.getPanelDetails());
		allSteps.add(rellekka.getPanelDetails());
		allSteps.add(ardougne.getPanelDetails());


		return allSteps;
	}

	@Override
	public List<String> getNotes()
	{
		return Arrays.asList("40-60 Agility: Stay on Canifis Rooftop Course for best spawn of Mark of Grace" +
				" until 60 Agility, then go directly to Seer's Village\n\n",
			"70-90 Agility: If you haven't done the Fremennik Hard Diary, the Pollnivneach course is better exp/hour than the Rellekka one.");
	}
}
