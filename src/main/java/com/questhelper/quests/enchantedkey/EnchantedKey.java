/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.enchantedkey;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.questhelper.rewards.ItemReward;
import net.runelite.api.ItemID;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.QuestDescriptor;
import net.runelite.api.QuestState;

@QuestDescriptor(
	quest = QuestHelperQuest.ENCHANTED_KEY
)
public class EnchantedKey extends BasicQuestHelper
{
	//Items Required
	ItemRequirement spade, key;

	//Items Recommended
	ItemRequirement rellekkaTeleports, varrockTeleports, ardougneTeleports, lumbridgeTeleports, passage;

	QuestStep solvePuzzle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		for (int i = 0; i < 2047; i++)
		{
			steps.put(i, solvePuzzle);
		}

		return steps;
	}

	private void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		key = new ItemRequirement("Enchanted key", ItemID.ENCHANTED_KEY);
		varrockTeleports = new ItemRequirement("Varrock teleports", ItemID.VARROCK_TELEPORT);
		ardougneTeleports = new ItemRequirement("Ardougne teleports", ItemID.ARDOUGNE_TELEPORT);
		rellekkaTeleports = new ItemRequirement("Rellekka teleport", ItemID.RELLEKKA_TELEPORT);
		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.LUMBRIDGE_TELEPORT);
		passage = new ItemRequirement("Necklace of passage", ItemCollections.getNecklaceOfPassages());
	}

	private void setupSteps()
	{
		solvePuzzle = new EnchantedKeyDigStep(this, key, spade);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(key, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(rellekkaTeleports, varrockTeleports, ardougneTeleports, lumbridgeTeleports, passage);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Saradomin Mjolnir", ItemID.SARADOMIN_MJOLNIR, 1),
				new ItemReward("Guthix Mjolnir", ItemID.GUTHIX_MJOLNIR, 1),
				new ItemReward("Zamorak Mjolnir", ItemID.ZAMORAK_MJOLNIR, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Dig for treasure", Collections.singletonList(solvePuzzle), key, spade));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.MAKING_HISTORY, QuestState.FINISHED));
		return req;
	}
}
