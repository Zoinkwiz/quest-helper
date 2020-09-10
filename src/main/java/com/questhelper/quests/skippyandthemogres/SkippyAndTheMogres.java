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
package com.questhelper.quests.skippyandthemogres;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.SKIPPY_AND_THE_MOGRES
)
public class SkippyAndTheMogres extends BasicQuestHelper
{
	ItemRequirement bucketOfWater, nettleTea, chocolateDust, bucketOfMilk, snapeGrass, chocolateMilk, hangoverCure;

	ConditionForStep hasChocolateMilk, hasHangoverCure;

	QuestStep soberSkippy, useTeaOnSkippy, useChocolateDustOnMilk, useSnapeGrassOnMilk, useHangoverCure;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, soberSkippy);
		steps.put(1, useTeaOnSkippy);

		ConditionalStep makeAndUseCure = new ConditionalStep(this, useChocolateDustOnMilk);
		makeAndUseCure.addStep(hasHangoverCure, useHangoverCure);
		makeAndUseCure.addStep(hasChocolateMilk, useSnapeGrassOnMilk);

		steps.put(2, makeAndUseCure);

		return steps;
	}

	public void setupItemRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		bucketOfMilk.setHighlightInInventory(true);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.setHighlightInInventory(true);
		hangoverCure = new ItemRequirement("Hangover cure", ItemID.HANGOVER_CURE);
		hangoverCure.setHighlightInInventory(true);
		chocolateDust = new ItemRequirement("Chocolate dust", ItemID.CHOCOLATE_DUST);
		chocolateDust.setHighlightInInventory(true);
		nettleTea = new ItemRequirement("Nettle tea", ItemID.NETTLE_TEA);
		nettleTea.setTip("You can make this by using nettles on a bowl of water, then cooking it");
		snapeGrass = new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS);
		snapeGrass.setHighlightInInventory(true);
		chocolateMilk = new ItemRequirement("Chocolatey milk", ItemID.CHOCOLATEY_MILK);
		chocolateMilk.setHighlightInInventory(true);
	}

	public void setupConditions()
	{
		hasHangoverCure = new ItemRequirementCondition(hangoverCure);
		hasChocolateMilk = new ItemRequirementCondition(chocolateMilk);
	}

	public void setupSteps()
	{
		soberSkippy = new NpcStep(this, NpcID.SKIPPY, new WorldPoint(2982, 3194, 0), "Right-click 'sober-up' on Skippy south west of Port Sarim.", bucketOfWater);
		soberSkippy.addIcon(ItemID.BUCKET_OF_WATER);
		soberSkippy.addDialogStep("Throw the water!");
		useTeaOnSkippy = new NpcStep(this, NpcID.SKIPPY_2588, new WorldPoint(2982, 3194, 0), "Talk to Skippy.", nettleTea);
		useChocolateDustOnMilk = new DetailedQuestStep(this, "Use some chocolate dust on a bucket of milk.", chocolateDust, bucketOfMilk);
		useSnapeGrassOnMilk = new DetailedQuestStep(this, "Use some snape grass on the chocolatey milk.", snapeGrass, chocolateMilk);
		useHangoverCure = new NpcStep(this, NpcID.SKIPPY_2589, new WorldPoint(2982, 3194, 0), "Use the hangover cure on Skippy.", hangoverCure);
		useHangoverCure.addIcon(ItemID.HANGOVER_CURE);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bucketOfWater);
		reqs.add(nettleTea);
		reqs.add(bucketOfMilk);
		reqs.add(chocolateDust);
		reqs.add(snapeGrass);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Help Skippy", new ArrayList<>(Arrays.asList(soberSkippy, useTeaOnSkippy, useChocolateDustOnMilk, useSnapeGrassOnMilk, useHangoverCure)), bucketOfWater, nettleTea, bucketOfMilk, chocolateDust, snapeGrass));
		return allSteps;
	}
}
