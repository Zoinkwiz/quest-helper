/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.quests.cooksassistant;

import com.questhelper.QuestHelperQuest;
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
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.COOKS_ASSISTANT
)
public class CooksAssistant extends BasicQuestHelper
{
	ItemRequirement egg, milk, flour;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, new NpcStep(this, NpcID.COOK_4626, new WorldPoint(3206, 3214, 0),
			"Give the Cook in Lumbridge Castle's kitchen the required items to finish the quest.",
			egg, milk, flour));
		steps.get(0).addDialogStep("I'll get right on it.");

		steps.put(1, steps.get(0));

		return steps;
	}

	public void setupItemRequirements() {
		egg = new ItemRequirement("Egg", ItemID.EGG);
		egg.setTip("You can find an egg in the farm north of Lumbridge.");
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milk.setTip("You can get a bucket from the Lumbridge General Store, then milk a Dairy Cow north-east of Lumbridge.");
		flour = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
		flour.setTip("You can buy a pot from the Lumbridge General Store, collect some wheat from a field north of Lumbridge, then grind it in the Lumbridge Mill north of Lumbridge");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(egg);
		reqs.add(flour);
		reqs.add(milk);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Bring the cook cake ingredients", new ArrayList<>(Arrays.asList(new DetailedQuestStep(this, "Bring the cook the ingredients he needs."))), egg, flour, milk));
		return allSteps;
	}
}