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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;
import net.runelite.api.SpriteID;

@QuestDescriptor(
	quest = QuestHelperQuest.CHECK_ITEMS
)
public class AllNeededItems extends ComplexStateQuestHelper
{
	DetailedQuestStep step1;

	@Override
	public QuestStep loadStep()
	{
		Map<Integer, ItemRequirement> reqs = new LinkedHashMap<>();
		questHelperPlugin.itemRequirements.forEach((name, questReqs) -> refinedList(name, reqs, questReqs));
		questHelperPlugin.itemRecommended.forEach((name, questRecommended) -> refinedList(name, reqs, questRecommended));

		step1 = new DetailedQuestStep(this, "Get all items you need. You can have items being highlighted that you" +
			" need without running this helper if you activate it in the Quest Helper settings.", new ArrayList<>(reqs.values()));
		step1.hideRequirements = true;
		step1.considerBankForItemHighlight = true;
		step1.iconToUseForNeededItems = SpriteID.TAB_QUESTS;

		return step1;
	}

	private List<ItemRequirement> refinedList(String questName, Map<Integer, ItemRequirement> compressedReqs, List<ItemRequirement> reqs)
	{
		// TODO: Rather than an ItemRequirement, shift to an ItemRequirements with each itemreq as an ItemRequirement in it
		// This would allow for better mixed IDs between items
		for (ItemRequirement req : reqs)
		{
			ItemRequirement newReq = req;
			if (req.getId() == -1)
			{
				continue;
			}

			if (req.getQuantity() == -1) newReq = req.quantity(1);

			if (!compressedReqs.containsKey(newReq.getId()))
			{
				ItemRequirement freshReq = new ItemRequirement(req.getName(), newReq.getId(), newReq.getQuantity());
				String tip = "Needed for " + questName;
				freshReq.setTooltip(tip);
				compressedReqs.put(newReq.getId(), freshReq);
			}
			else
			{
				ItemRequirement currentReq = compressedReqs.get(newReq.getId());
				currentReq.appendToTooltip(questName);
				if (newReq.isConsumedItem())
				{
					currentReq.setQuantity(currentReq.getQuantity() + newReq.getQuantity());
				}
			}
		}

		return new ArrayList<>(compressedReqs.values());
	}

	@Override
	public void setupRequirements()
	{

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		Map<Integer, ItemRequirement> reqs = new LinkedHashMap<>();
		questHelperPlugin.itemRequirements.forEach((name, questReqs) -> refinedList(name, reqs, questReqs));
		return new ArrayList<>(reqs.values());
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		Map<Integer, ItemRequirement> reqs = new LinkedHashMap<>();
		questHelperPlugin.itemRecommended.forEach((name, questRecs) -> refinedList(name, reqs, questRecs));
		return new ArrayList<>(reqs.values());
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(step1)));
		questHelperPlugin.itemRequirements.forEach((name, reqs) -> allSteps.add(new PanelDetails(name, Collections.emptyList(),
			(List<Requirement>)(List<?>) reqs,
			(List<Requirement>)(List<?>) questHelperPlugin.itemRecommended.get(name))));
		return allSteps;
	}
}
