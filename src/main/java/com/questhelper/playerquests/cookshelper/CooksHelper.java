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
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.PlayerQuestStateRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.playermadesteps.RuneliteConfigSetter;
import com.questhelper.steps.playermadesteps.RuneliteDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectStep;
import com.questhelper.steps.playermadesteps.RunelitePlayerDialogStep;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeItem;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeNpc;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.ReplacedNpc;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.ReplacedObject;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.RuneliteObjectManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.AnimationID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.Player;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.COOKS_HELPER
)
public class CooksHelper extends ComplexStateQuestHelper
{
	private RuneliteObjectStep talkToCook, talkToHopleez, grabCabbage, returnToHopleez;
	private ReplacedNpc replacedHopleez;

	private FakeNpc cooksCousin, hopleez;

	private FakeItem cabbage;

	private PlayerQuestStateRequirement talkedToCooksCousin, talkedToHopleez, pickedCabbage, returnedToHopleez;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		createRuneliteObjects();
		setupSteps();

		PlayerQuestStateRequirement req = new PlayerQuestStateRequirement(configManager, "cookshelper", 1);

		ConditionalStep questSteps = new ConditionalStep(this, talkToCook);
		questSteps.addStep(req.getNewState(2), returnToHopleez);
		questSteps.addStep(req.getNewState(1), grabCabbage);
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
		talkedToCooksCousin = new PlayerQuestStateRequirement(configManager, "cookshelper", 1, Operation.GREATER_EQUAL);
		talkedToHopleez = new PlayerQuestStateRequirement(configManager, "cookshelper", 2, Operation.GREATER_EQUAL);
		pickedCabbage = new PlayerQuestStateRequirement(configManager, "cookshelper", 3, Operation.GREATER_EQUAL);
		returnedToHopleez = new PlayerQuestStateRequirement(configManager, "cookshelper", 4, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		// TODO: Need a way to define the groupID of a runelite object to be a quest step without it being stuck
		// Add each step's groupID as a sub-group
		talkToCook = new RuneliteObjectStep(this, cooksCousin, "Talk to the Lumbridge Cook.");
//		talkToCook.addNpcToDelete(this, cooksCousin);

		talkToHopleez = new RuneliteObjectStep(this, hopleez, "Talk to Hopleez east of Lumbridge Castle.");

		grabCabbage = new RuneliteObjectStep(this, cabbage, "Get the cabbage to the north of Hopleez, outside the Sheared Ram.");

		returnToHopleez = new RuneliteObjectStep(this, hopleez, "Return to Hopleez east of Lumbridge Castle.");
	}

	private void setupCooksCousin()
	{
		// Cook's Cousin
		cooksCousin = runeliteObjectManager.createFakeNpc(this.toString(), client.getNpcDefinition(NpcID.COOK_4626).getModels(), new WorldPoint(3209, 3215, 0), 808);
		cooksCousin.setName("Cook's Cousin");
		cooksCousin.setFace(4626);
		cooksCousin.setExamine("The Cook's cousin, Vinny.");
		cooksCousin.addTalkAction(runeliteObjectManager);
		cooksCousin.addExamineAction(runeliteObjectManager);

		QuestRequirement hasDoneCooksAssistant = new QuestRequirement(QuestHelperQuest.COOKS_ASSISTANT, QuestState.FINISHED);
		String cookHelpedReplyText = "";
		if (hasDoneCooksAssistant.check(client))
		{
			cookHelpedReplyText = "Yes, why?";
		}
		else
		{
			cookHelpedReplyText = "No, never even seen them before.";
		}
		RuneliteObjectDialogStep dialog = cooksCousin.createDialogStepForNpc(
			"Hey, you there! You helped out my cousin before right?");
		RuneliteObjectDialogStep dialog2 = cooksCousin.createDialogStepForNpc("Well can you do me a favour? There's a terribly dressed person outside the castle's courtyard.");
		RunelitePlayerDialogStep dialog3 = new RunelitePlayerDialogStep(client, "Sure thing!");
		dialog3.setStateProgression(talkedToCooksCousin.getSetter());
		dialog.addContinueDialog(new RunelitePlayerDialogStep(client, cookHelpedReplyText))
			.addContinueDialog(dialog2)
			.addContinueDialog(cooksCousin.createDialogStepForNpc("Can you please get them to move along please?"))
			.addContinueDialog(dialog3);
		cooksCousin.addDialogTree(null, dialog);

		RuneliteObjectDialogStep dialogV2 = cooksCousin.createDialogStepForNpc("That terribly dressed person is still outside the castle, go talk to them!");
		cooksCousin.addDialogTree(talkedToCooksCousin, dialogV2);
	}

	private void setupHopleez()
	{
		replacedHopleez = runeliteObjectManager.createReplacedNpc(this.toString(), client.getNpcDefinition(NpcID.HOPLEEZ).getModels(), new WorldPoint(3235, 3215, 0), NpcID.HATIUS_COSAINTUS);
		replacedHopleez.setName("Hopleez");
		replacedHopleez.setFace(7481);
		replacedHopleez.setExamine("He was here first.");
		replacedHopleez.addExamineAction(runeliteObjectManager);

		// Hopleez
		hopleez = runeliteObjectManager.createFakeNpc(this.toString(), client.getNpcDefinition(NpcID.HOPLEEZ).getModels(), new WorldPoint(3235, 3215, 0), 808);
		hopleez.setName("Hopleez");
		hopleez.setFace(7481);
		hopleez.setExamine("He was here first.");
		hopleez.addTalkAction(runeliteObjectManager);
		hopleez.addExamineAction(runeliteObjectManager);

		// Dialog
		RuneliteDialogStep hopleezDialogPreQuest = hopleez.createDialogStepForNpc("Hop noob.");
		hopleezDialogPreQuest.addContinueDialog(new RunelitePlayerDialogStep(client, "What? Also, what are you wearing?"))
			.addContinueDialog(hopleez.createDialogStepForNpc("Hop NOOB."));
		hopleez.addDialogTree(null, hopleezDialogPreQuest);

		RuneliteDialogStep hopleezDialog1 = hopleez.createDialogStepForNpc("Hop noob.");
		hopleezDialog1.addContinueDialog(new RunelitePlayerDialogStep(client, "What? The Cook's Cousin sent me to see what you were doing here."))
			.addContinueDialog(hopleez.createDialogStepForNpc("I was here first but this noob is crashing me!"))
			.addContinueDialog(hopleez.createDialogStepForNpc("Help me teach him a lesson, get me that old cabbage from outside the The Sheared Ram."))
			.addContinueDialog(new RunelitePlayerDialogStep(client, "Ummm okay....", talkedToHopleez.getSetter()));
		hopleez.addDialogTree(talkedToCooksCousin, hopleezDialog1);


		RuneliteDialogStep hopleezWaitingForCabbageDialog = hopleez.createDialogStepForNpc("Get me that cabbage!");
		hopleez.addDialogTree(talkedToHopleez, hopleezWaitingForCabbageDialog);

		RuneliteConfigSetter restartQuest = new RuneliteConfigSetter(configManager, "cookshelper", "0");
		RuneliteDialogStep hopleezGiveCabbageDialog = hopleez.createDialogStepForNpc("Have you got the cabbage?");
		hopleezGiveCabbageDialog.addContinueDialog(new RunelitePlayerDialogStep(client, "I have!"))
				.addContinueDialog(hopleez.createDialogStepForNpc("Nice! Now let's sort out this crasher...", restartQuest));
		hopleez.addDialogTree(pickedCabbage, hopleezGiveCabbageDialog);
	}

	private void setupCabbage()
	{
		// Old cabbage
		cabbage = runeliteObjectManager.createFakeItem(this.toString(), new int[]{ 8196 }, new WorldPoint(3231, 3235, 0), -1);
		cabbage.setName("Old cabbage");
		cabbage.setExamine("A mouldy looking cabbage.");
		cabbage.addExamineAction(runeliteObjectManager);
		cabbage.setDisplayRequirement(new Conditions(LogicType.NOR, pickedCabbage));
		cabbage.setReplaceWalkAction((menuEntry) -> {
			// Bend down and pick up the item
			cabbage.setPendingAction(() -> {
				// Kinda needs to be a 'last interacted object'
				Player player = client.getLocalPlayer();
				// TODO: Won't work in instances?
				if (player.getWorldLocation().distanceTo(cabbage.getWorldPoint()) <= 1)
				{
					runeliteObjectManager.createChatboxMessage("You pick up the old cabbage.");
					player.setAnimation(AnimationID.BURYING_BONES);
					player.setAnimationFrame(0);

					// Set variable
					pickedCabbage.getSetter().setConfigValue();
					cabbage.activate();

					return true;
				}
				return false;
			});
		});
		cabbage.setReplaceWalkActionText("Pick");
		cabbage.setObjectToRemove(new ReplacedObject(NullObjectID.NULL_37348, new WorldPoint(3231, 3235, 0)));
	}

	private void createRuneliteObjects()
	{
		setupCooksCousin();
		setupHopleez();
		setupCabbage();
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
		PanelDetails helpingTheCousinSteps = new PanelDetails("Helping the Cook's Cousin", Arrays.asList(talkToCook, talkToHopleez, grabCabbage, returnToHopleez));
		allSteps.add(helpingTheCousinSteps);

		return allSteps;
	}
}
