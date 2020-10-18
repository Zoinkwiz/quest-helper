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

import com.questhelper.QuestHelperQuest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.QuestDescriptor;

@QuestDescriptor(
	quest = QuestHelperQuest.ENCHANTED_KEY
)
public class EnchantedKey extends BasicQuestHelper
{
	ItemRequirement spade, key, varrockTeleports, ardougneTeleports, rellekkaTeleports, lumbridgeTeleports, passage;

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
		passage = new ItemRequirement("Necklace of passage", ItemID.NECKLACE_OF_PASSAGE5);
		passage.addAlternates(ItemID.NECKLACE_OF_PASSAGE1, ItemID.NECKLACE_OF_PASSAGE2, ItemID.NECKLACE_OF_PASSAGE3, ItemID.NECKLACE_OF_PASSAGE4);
	}

	private void setupSteps()
	{
		solvePuzzle = new EnchantedKeyDigStep(this, key, spade);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(key, spade));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(rellekkaTeleports, varrockTeleports, ardougneTeleports, lumbridgeTeleports, passage));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Dig for treasure", new ArrayList<>(Arrays.asList(solvePuzzle)), key, spade));
		return allSteps;
	}
}
