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
package com.questhelper.quests.witchspotion;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.WITCHS_POTION
)
public class WitchsPotion extends BasicQuestHelper
{
	ItemRequirement ratTail, onion, burntMeat, eyeOfNewt;

	ConditionForStep hasRatTail;

	QuestStep talkToWitch, killRat, returnToWitch, drinkPotion;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToWitch);

		ConditionalStep getIngredients = new ConditionalStep(this, killRat);
		getIngredients.addStep(hasRatTail, returnToWitch);

		steps.put(1, getIngredients);

		steps.put(2, drinkPotion);

		return steps;
	}

	public void setupItemRequirements()
	{
		ratTail = new ItemRequirement("Rat's tail", ItemID.RATS_TAIL);
		onion = new ItemRequirement("Onion", ItemID.ONION);
		onion.setTip("You can pick one from the field north of Rimmington");
		burntMeat = new ItemRequirement("Burnt meat", ItemID.BURNT_MEAT);
		burntMeat.setTip("You can use cooked meat on a fire/range to burn it");
		eyeOfNewt = new ItemRequirement("Eye of newt", ItemID.EYE_OF_NEWT);
		eyeOfNewt.setTip("You can buy one from Betty in Port Sarim for 3gp");
	}

	public void setupConditions()
	{
		hasRatTail = new ItemRequirementCondition(ratTail);
	}

	public void setupSteps()
	{
		talkToWitch = new NpcStep(this, NpcID.HETTY, new WorldPoint(2968, 3205, 0),
			"Talk to Hetty in Rimmington.", onion, eyeOfNewt, burntMeat);
		talkToWitch.addDialogStep("I am in search of a quest.");
		talkToWitch.addDialogStep("Yes, help me become one with my darker side.");

		killRat = new NpcStep(this, NpcID.RAT_2855, new WorldPoint(2956, 3203, 0), "Kill a rat in the house to the west for a rat tail.", ratTail);
		returnToWitch = new NpcStep(this, NpcID.HETTY, new WorldPoint(2968, 3205, 0),
			"Bring the ingredients to Hetty.", onion, eyeOfNewt, burntMeat, ratTail);

		drinkPotion = new ObjectStep(this, ObjectID.CAULDRON_2024, new WorldPoint(2967, 3205, 0), "Drink from the cauldron to finish off the quest.");

	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(onion);
		reqs.add(burntMeat);
		reqs.add(eyeOfNewt);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToWitch))));
		allSteps.add(new PanelDetails("Make the potion", new ArrayList<>(Arrays.asList(killRat, returnToWitch))));
		return allSteps;
	}
}
