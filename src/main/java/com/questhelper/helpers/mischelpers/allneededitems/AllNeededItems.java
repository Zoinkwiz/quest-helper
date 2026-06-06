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
package com.questhelper.helpers.mischelpers.allneededitems;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questhelpers.QuestDetails;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.gameval.SpriteID;

import java.util.*;
import static com.questhelper.requirements.item.ItemRequirement.UNDEFINED_QUANTITY;

public class AllNeededItems extends ComplexStateQuestHelper
{
	DetailedQuestStep step1;

	@Override
	public QuestStep loadStep()
	{
		Map<Integer, ItemRequirement> reqs = new LinkedHashMap<>();;
		questHelperPlugin.getItemRequirements().forEach((qhQuest, questReqs) -> refinedList(qhQuest.getName(), reqs, questReqs));
		questHelperPlugin.getItemRecommended().forEach((qhQuest, questRecommended) -> refinedList(qhQuest.getName(), reqs, questRecommended));

		step1 = new DetailedQuestStep(this, "Get all items you need. You can have items being highlighted that you" +
			" need without running this helper if you activate it in the Quest Helper settings.", new ArrayList<>(reqs.values()));
		step1.hideRequirements = true;
		step1.considerBankForItemHighlight = true;
		step1.iconToUseForNeededItems = SpriteID.SideiconsInterface.QUESTS;
		step1.setBackgroundWorldTooltipText("Highlighted due to the config setting 'Highlight missing items' in Quest Helper.");

		return step1;
	}

	private void refinedList(String questName, Map<Integer, ItemRequirement> compressedReqs, List<ItemRequirement> reqs)
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

			if (req.getQuantity() < 0) newReq = req.quantity(1);

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
	}

	@Override
	protected void setupRequirements()
	{

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		Map<Integer, ItemRequirement> reqs = new LinkedHashMap<>();
		questHelperPlugin.getItemRequirements().forEach((qhQuest, questReqs) -> refinedList(qhQuest.getName(), reqs, questReqs));
		return new ArrayList<>(reqs.values());
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		Map<Integer, ItemRequirement> reqs = new LinkedHashMap<>();
		questHelperPlugin.getItemRecommended().forEach((qhQuest, questRecs) -> refinedList(qhQuest.getName(), reqs, questRecs));
		return new ArrayList<>(reqs.values());
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(step1)));
		Map<Integer, ItemRequirement> questsReq = new LinkedHashMap<>();
		Map<Integer, ItemRequirement> miniquestsReq = new LinkedHashMap<>();
		Map<Integer, ItemRequirement> diariesReq = new LinkedHashMap<>();
		Map<Integer, ItemRequirement> questsRec = new LinkedHashMap<>();
		Map<Integer, ItemRequirement> miniquestsRec = new LinkedHashMap<>();
		Map<Integer, ItemRequirement> diariesRec = new LinkedHashMap<>();

		questHelperPlugin.getItemRequirements().forEach((qhQuest, reqs) -> {
			QuestDetails.Type type = qhQuest.getQuestType();
			if (type == QuestDetails.Type.P2P || type == QuestDetails.Type.F2P)
			{
				refinedList(qhQuest.getName(), questsReq, reqs);
			}
			else if (type == QuestDetails.Type.MINIQUEST)
			{
				refinedList(qhQuest.getName(), miniquestsReq, reqs);
			}
			else if (type == QuestDetails.Type.ACHIEVEMENT_DIARY)
			{
				refinedList(qhQuest.getName(), diariesReq, reqs);
			}
		});
		questHelperPlugin.getItemRecommended().forEach((qhQuest, reqs) -> {
			QuestDetails.Type type = qhQuest.getQuestType();
			if (type == QuestDetails.Type.P2P || type == QuestDetails.Type.F2P)
			{
				refinedList(qhQuest.getName(), questsRec, reqs);
			}
			else if (type == QuestDetails.Type.MINIQUEST)
			{
				refinedList(qhQuest.getName(), miniquestsRec, reqs);
			}
			else if (type == QuestDetails.Type.ACHIEVEMENT_DIARY)
			{
				refinedList(qhQuest.getName(), diariesRec, reqs);
			}
		});
		if (questsReq.size() > 0)
		{
			allSteps.add(new PanelDetails("Quests", Collections.emptyList(), new ArrayList<>(questsReq.values()), new ArrayList<>(questsRec.values())));
		}

		if (miniquestsReq.size() > 0)
		{
			allSteps.add(new PanelDetails("Miniquests", Collections.emptyList(), new ArrayList<>(miniquestsReq.values()), new ArrayList<>(miniquestsRec.values())));
		}

		if (diariesReq.size() > 0)
		{
			allSteps.add(new PanelDetails("Achievement Diaries", Collections.emptyList(), new ArrayList<>(diariesReq.values()), new ArrayList<>(diariesRec.values())));
		}

		return allSteps;
	}
}
