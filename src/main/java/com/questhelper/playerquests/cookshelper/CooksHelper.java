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
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.playermadesteps.RuneliteConfigSetter;
import com.questhelper.steps.playermadesteps.runelitenpcs.RuneliteNpc;
import com.questhelper.steps.playermadesteps.RuneliteNpcDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteNpcStep;
import com.questhelper.steps.playermadesteps.RunelitePlayerDialogStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.COOKS_HELPER
)
public class CooksHelper extends ComplexStateQuestHelper
{
	private RuneliteNpcStep talkToCook, talkToHopleez;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupConditions();
		setupSteps();

		PlayerQuestStateRequirement req = new PlayerQuestStateRequirement(configManager, "cookshelper", 1);

		DetailedQuestStep lol = new DetailedQuestStep(this, "WOW IT WORKED!!!!");
		ConditionalStep questSteps = new ConditionalStep(this, talkToCook);
		questSteps.addStep(req.getNewState(1), lol);
		questSteps.addStep(req, talkToHopleez);

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
		// TODO: Decide on logic for NPCs being maintained in the world
		// Use-cases:
		// NPC exists whilst a Helper is open
		// NPC exists whilst a Requirement is true
		// NPC exists during a step (align to a Requirement?)
		// NPC exists always (align to a Requirement?)

		// TODO: Add conditional dialog
		// Currently added to the NPC. When click 'Talk', iterate through the Requirements
		// NOTE: Some requirements kinda require an external tracking element, so may need to shove into a ConditionalStep or some weirdness?

		RuneliteNpc cooksCousin = runeliteObjectManager.createRuneliteNpc(this, client.getNpcDefinition(NpcID.COOK_4626).getModels(), new WorldPoint(3209, 3215, 0), 808);
		cooksCousin.setName("Cook's Cousin");
		cooksCousin.setFace(4626);
		cooksCousin.setExamine("The Cook's cousin, Vinny.");
		cooksCousin.addTalkAction(runeliteObjectManager);
		cooksCousin.addExamineAction(runeliteObjectManager);

		// TODO: Need a way to define the groupID of a runelite object to be a quest step without it being stuck
		// Add each step's groupID as a sub-group
		talkToCook = new RuneliteNpcStep(this, cooksCousin, "Talk to the Lumbridge Cook.");
//		talkToCook.addNpcToDelete(this, cooksCousin);

		RuneliteNpcDialogStep dialog = cooksCousin.createDialogStepForNpc(
			"You were seriously great when you defeated the Culinaromancer! I can't believe I nearly caused all of those people to be killed! So how is the adventuring going now?");
		RuneliteNpcDialogStep dialog2 = cooksCousin.createDialogStepForNpc("Are you ready for the next bit of action?");
		RunelitePlayerDialogStep dialog3 = new RunelitePlayerDialogStep(client, "I sure am!");
		dialog3.setStateProgression(new RuneliteConfigSetter(configManager, "cookshelper", "1"));
		dialog.setContinueDialog(dialog2);
		dialog2.setContinueDialog(dialog3);
		cooksCousin.addDialogTree(null, dialog);

		PlayerQuestStateRequirement req = new PlayerQuestStateRequirement(configManager, "cookshelper", 1);
		RuneliteNpcDialogStep dialogV2 = cooksCousin.createDialogStepForNpc(
			"That terribly dressed person is still outside the castle, go talk to them!");
		cooksCousin.addDialogTree(req, dialogV2);

		RuneliteNpc hopleez = runeliteObjectManager.createRuneliteNpc(this, client.getNpcDefinition(NpcID.HOPLEEZ).getModels(), new WorldPoint(3235, 3215, 0), 808);
		hopleez.setName("Hopleez");
		hopleez.setFace(7481);
		hopleez.setExamine("He was here first.");
		hopleez.addTalkAction(runeliteObjectManager);
		hopleez.addExamineAction(runeliteObjectManager);

		talkToHopleez = new RuneliteNpcStep(this, hopleez, "Talk to Hopleez east of Lumbridge Castle.");

		RuneliteNpcDialogStep hopleezDialog1 = hopleez.createDialogStepForNpc("Hop noob.");
		RunelitePlayerDialogStep playerHopleez1 = new RunelitePlayerDialogStep(client, "What? Also, what are you wearing?");
		RuneliteNpcDialogStep hopleezDialog2 = hopleez.createDialogStepForNpc("Hop NOOB.");
		hopleezDialog1.setContinueDialog(playerHopleez1);
		playerHopleez1.setContinueDialog(hopleezDialog2);
		hopleez.addDialogTree(null, hopleezDialog1);

		RuneliteNpcDialogStep hopleezDialog1V2 = hopleez.createDialogStepForNpc("Hop noob.");
		RunelitePlayerDialogStep playerHopleez1V2 = new RunelitePlayerDialogStep(client, "What? The Cook's Cousin sent me to see what you were doing here.");
		RuneliteNpcDialogStep hopleezDialog2V2 = hopleez.createDialogStepForNpc("I was here first but this noob is crashing me!");
		hopleezDialog1V2.setContinueDialog(playerHopleez1V2);
		playerHopleez1V2.setContinueDialog(hopleezDialog2V2);
		hopleez.addDialogTree(req, hopleezDialog1V2);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new QuestPointRequirement(16));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("A sense of pride and accomplishment")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails helpingTheCousinSteps = new PanelDetails("Helping the Cook's Cousin", Arrays.asList(talkToCook, talkToHopleez));
		allSteps.add(helpingTheCousinSteps);

		return allSteps;
	}
}
