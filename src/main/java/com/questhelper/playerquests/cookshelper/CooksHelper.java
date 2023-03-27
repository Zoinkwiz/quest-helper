/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.playerquests.cookshelper;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.runelite.PlayerQuestStateRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.playermadesteps.RuneliteConfigSetter;
import com.questhelper.steps.playermadesteps.RuneliteNpcDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteNpcStep;
import com.questhelper.steps.playermadesteps.RunelitePlayerDialogStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;


@QuestDescriptor(
	quest = QuestHelperQuest.COOKS_HELPER
)
public class CooksHelper extends ComplexStateQuestHelper
{
	private RuneliteNpcStep talkToCook;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupConditions();
		setupSteps();

		PlayerQuestStateRequirement req = new PlayerQuestStateRequirement(configManager, "cookshelper", 1);

		DetailedQuestStep lol = new DetailedQuestStep(this, "WOW IT WORKED!!!!");
		ConditionalStep questSteps = new ConditionalStep(this, talkToCook);
//		questSteps.addStep(req.getNewState(1), lol);
//		questSteps.addStep(req, lol);

		// We want something which can be added to a requirement, which

		// Don't save to config until helper closes/client closing?

		return questSteps;
	}

	@Override
	public void setupRequirements()
	{
		// NPCs should persist through quest steps unless actively removed? Dialog should be conditional on step (sometimes)
		// Hide/show NPCs/the runelite character when NPCs go on it/you go over it
		// Handle cancelling dialog boxes (even just moving a tile away should remove for example)
		// Properly handle removing NPC from screen when changing floors and such
		// Work out how to do proper priority on the npcs being clicked
		// Wandering NPCs?
		// Objects + items (basically same as NPCs)

	}

	public void setupConditions()
	{

	}

	public void setupSteps()
	{
		talkToCook = new RuneliteNpcStep(this, client.getNpcDefinition(NpcID.COOK_4626).getModels(), new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook.");
		talkToCook.setFace(4626);
		talkToCook.setName("Cook's Cousin");
		RuneliteNpcDialogStep dialog = talkToCook.createDialogStepForNpc(
			"You were seriously great when you defeated the Culinaromancer! I can't believe I nearly caused all of those people to be killed! So how is the adventuring going now?");
		RuneliteNpcDialogStep dialog2 = talkToCook.createDialogStepForNpc("Are you ready for the next bit of action?");
		RunelitePlayerDialogStep dialog3 = new RunelitePlayerDialogStep(client, "I sure am!");
		dialog3.setStateProgression(new RuneliteConfigSetter(configManager, "cookshelper", "1"));
		dialog.setContinueDialog(dialog2);
		dialog2.setContinueDialog(dialog3);
		talkToCook.setDialogTree(dialog);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		List<ItemRequirement> reqs = new ArrayList<>();

		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList();
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new QuestPointRequirement(16));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS_995, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails chopMagicsSteps = new PanelDetails("Helping the Cook", Collections.singletonList(talkToCook));
		allSteps.add(chopMagicsSteps);

		return allSteps;
	}
}
