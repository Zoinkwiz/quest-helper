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
package com.questhelper.helpers.miniquests.enchantedkey;

import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.KeyringCollection;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.KeyringRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.gameval.ItemID;

import java.util.*;

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
		initializeRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		for (int i = 0; i < 2047; i++)
		{
			steps.put(i, solvePuzzle);
		}

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		key = new KeyringRequirement("Enchanted key", configManager, KeyringCollection.ENCHANTED_KEY);
		varrockTeleports = new ItemRequirement("Varrock teleports", ItemID.POH_TABLET_VARROCKTELEPORT);
		ardougneTeleports = new ItemRequirement("Ardougne teleports", ItemID.POH_TABLET_ARDOUGNETELEPORT);
		rellekkaTeleports = new ItemRequirement("Rellekka teleport", ItemID.NZONE_TELETAB_RELLEKKA);
		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		passage = new ItemRequirement("Necklace of passage", ItemCollections.NECKLACE_OF_PASSAGES);
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
				new ItemReward("Saradomin Mjolnir", ItemID.MAKINGHISTORY_SARADOMIN_POLESTAFF, 1),
				new ItemReward("Guthix Mjolnir", ItemID.MAKINGHISTORY_GUTHIX_POLESTAFF, 1),
				new ItemReward("Zamorak Mjolnir", ItemID.MAKINGHISTORY_ZAMORAK_POLESTAFF, 1));
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
