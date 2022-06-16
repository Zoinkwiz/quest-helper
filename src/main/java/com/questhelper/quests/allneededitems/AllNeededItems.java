/*
 * Copyright (c) 2022, Zoinkwiz
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
package com.questhelper.quests.allneededitems;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuestDescriptor(
	quest = QuestHelperQuest.CHECK_ITEMS
)
public class AllNeededItems extends ComplexStateQuestHelper
{
	DetailedQuestStep step1;

	@Override
	public QuestStep loadStep()
	{
		List<ItemRequirement> reqs = new ArrayList<>();
		questHelperPlugin.itemRequirements.forEach((name, questReqs) -> {
			reqs.addAll(questReqs);
		});
		Map<Integer, ItemRequirement> compressedReqs = new HashMap<>();

		for (ItemRequirement req : reqs)
		{
			if (!compressedReqs.containsKey(req.getId()))
			{
				compressedReqs.put(1, req);
			}
		}


		step1 = new DetailedQuestStep(this, "Get all items you need. You can have items being highlighted that you" +
			" need without running this helper if you activate it in the Quest Helper settings.", reqs);
		step1.hideRequirements = true;
		step1.considerBankForItemHighlight = true;
		setupRequirements();

		return step1;
	}

	@Override
	public void setupRequirements()
	{

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		List<ItemRequirement> reqs = new ArrayList<>();
		questHelperPlugin.itemRequirements.forEach((name, questReqs) -> {
			reqs.addAll(questReqs);
		});
		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(step1)));
		questHelperPlugin.itemRequirements.forEach((name, reqs) -> {
			allSteps.add(new PanelDetails(name, Arrays.asList(), (List<Requirement>)(List<?>) reqs));
		});
		return allSteps;
	}
}

