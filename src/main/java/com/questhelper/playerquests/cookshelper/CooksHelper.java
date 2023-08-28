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
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.PlayerMadeQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.PlayerQuestStateRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import com.questhelper.steps.playermadesteps.RuneliteConfigSetter;
import com.questhelper.steps.playermadesteps.RuneliteDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectDialogStep;
import com.questhelper.steps.playermadesteps.RuneliteObjectStep;
import com.questhelper.steps.playermadesteps.RunelitePlayerDialogStep;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FaceAnimationIDs;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeItem;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.FakeNpc;
import com.questhelper.steps.playermadesteps.extendedruneliteobjects.ReplacedObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.COOKS_HELPER
)
public class CooksHelper extends PlayerMadeQuestHelper
{
	private RuneliteObjectStep talkToCook, talkToHopleez, grabCabbage, returnToHopleez;

	private DetailedQuestStep standNextToCook, standNextToHopleez, standNextToHopleez2;

	private Requirement nearCook, nearHopleez;

	private FakeNpc cooksCousin, hopleez;

	private FakeItem cabbage;

	private PlayerQuestStateRequirement talkedToCooksCousin, talkedToHopleez, displayCabbage, pickedCabbage;

	@Override
	public QuestStep loadStep()
	{
		itemWidget = ItemID.TOP_HAT;
		rotationX = 100;
		zoom = 200;

		setupRequirements();
		createRuneliteObjects();
		setupSteps();

		PlayerQuestStateRequirement req = new PlayerQuestStateRequirement(configManager, getQuest().getPlayerQuests(), 0);

		ConditionalStep questSteps = new ConditionalStep(this, standNextToCook);
		questSteps.addStep(req.getNewState(4), new DetailedQuestStep(this, "Quest completed!"));
		questSteps.addStep(new Conditions(req.getNewState(3), nearHopleez), returnToHopleez);
		questSteps.addStep(req.getNewState(3), standNextToHopleez2);
		questSteps.addStep(req.getNewState(2), grabCabbage);
		questSteps.addStep(new Conditions(req.getNewState(1), nearHopleez), talkToHopleez);
		questSteps.addStep(req.getNewState(1), standNextToHopleez);
		questSteps.addStep(nearCook, talkToCook);

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
		talkedToCooksCousin = new PlayerQuestStateRequirement(configManager, getQuest().getPlayerQuests(), 1, Operation.GREATER_EQUAL);
		talkedToHopleez = new PlayerQuestStateRequirement(configManager, getQuest().getPlayerQuests(), 2, Operation.GREATER_EQUAL);
		pickedCabbage = new PlayerQuestStateRequirement(configManager, getQuest().getPlayerQuests(), 3, Operation.GREATER_EQUAL);
		displayCabbage = new PlayerQuestStateRequirement(configManager, getQuest().getPlayerQuests(), 2);
		nearCook = new ZoneRequirement(new Zone(new WorldPoint(3206, 3212, 0), new WorldPoint(3212, 3218, 0)));
		nearHopleez = new ZoneRequirement(new Zone(new WorldPoint(3232, 3212, 0), new WorldPoint(3238, 3218, 0)));
	}

	public void setupSteps()
	{
		// TODO: Need a way to define the groupID of a runelite object to be a quest step without it being stuck
		// Add each step's groupID as a sub-group

		talkToCook = new RuneliteObjectStep(this, cooksCousin, "Talk to the Lumbridge Cook's Cousin.");
		standNextToCook = new TileStep(this, new WorldPoint(3210, 3215, 0), "Talk to the Lumbridge Cook's Cousin.");
		talkToCook.addSubSteps(standNextToCook);

		talkToHopleez = new RuneliteObjectStep(this, hopleez, "Talk to Hopleez east of Lumbridge Castle.");
		standNextToHopleez = new TileStep(this, new WorldPoint(3236, 3215, 0), "Talk to Hopleez east of Lumbridge Castle.");
		talkToHopleez.addSubSteps(standNextToHopleez);

		grabCabbage = new RuneliteObjectStep(this, cabbage, "Get the cabbage to the north of Hopleez, outside the Sheared Ram.");

		returnToHopleez = new RuneliteObjectStep(this, hopleez, "Return to Hopleez east of Lumbridge Castle.");
		standNextToHopleez2 = new DetailedQuestStep(this, new WorldPoint(3235, 3216, 0), "Return to Hopleez east of Lumbridge Castle.");
		returnToHopleez.addSubSteps(standNextToHopleez2);
	}

	private void setupCooksCousin()
	{
		// Cook's Cousin
		cooksCousin = runeliteObjectManager.createFakeNpc(this.toString(), client.getNpcDefinition(NpcID.COOK_4626).getModels(), new WorldPoint(3209, 3215, 0), 808);
		cooksCousin.setName("Cook's Cousin");
		cooksCousin.setFace(4626);
		cooksCousin.setExamine("The Cook's cousin.");
		cooksCousin.addTalkAction(runeliteObjectManager);
		cooksCousin.addExamineAction(runeliteObjectManager);

		QuestRequirement hasDoneCooksAssistant = new QuestRequirement(QuestHelperQuest.COOKS_ASSISTANT, QuestState.FINISHED);

		RuneliteObjectDialogStep dontMeetReqDialog = cooksCousin.createDialogStepForNpc("Come talk to me once you've helped my cousin out.");
		cooksCousin.addDialogTree(null, dontMeetReqDialog);

		RuneliteDialogStep dialog = cooksCousin.createDialogStepForNpc("Hey, you there! You helped out my cousin before right?");
		dialog.addContinueDialog(new RunelitePlayerDialogStep(client, "I have yeah, what's wrong? Does he need some more eggs? Maybe I can just get him a chicken instead?"))
			.addContinueDialog(cooksCousin.createDialogStepForNpc("No no, nothing like that. Have you seen that terribly dressed person outside the courtyard?", FaceAnimationIDs.FRIENDLY_QUESTIONING))
			.addContinueDialog(cooksCousin.createDialogStepForNpc("I don't know who they are, but can you please get them to move along please?", FaceAnimationIDs.FRIENDLY_QUESTIONING))
			.addContinueDialog(cooksCousin.createDialogStepForNpc("They seem to be attracting more troublemakers...."))
			.addContinueDialog(new RunelitePlayerDialogStep(client, "You mean Hatius? If so it'd be my pleasure.").setStateProgression(talkedToCooksCousin.getSetter()));
		cooksCousin.addDialogTree(hasDoneCooksAssistant, dialog);

		RuneliteObjectDialogStep dialogV2 = cooksCousin.createDialogStepForNpc("That terribly dressed person is still outside the castle, go talk to them!");
		cooksCousin.addDialogTree(talkedToCooksCousin, dialogV2);
	}

	private void setupHopleez()
	{
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

		RuneliteDialogStep hopleezDialog1 = hopleez.createDialogStepForNpc("Hop noob.", FaceAnimationIDs.ANNOYED);
		hopleezDialog1.addContinueDialog(new RunelitePlayerDialogStep(client, "What? The Cook's Cousin sent me to see what you were doing here.", FaceAnimationIDs.QUIZZICAL))
			.addContinueDialog(hopleez.createDialogStepForNpc("One moment I was relaxing in Zeah killing some crabs. I closed my eyes for a second, and suddenly I'm here."))
			.addContinueDialog(hopleez.createDialogStepForNpc("People would always try to steal my spot in Zeah, and it seems it's no different here!", FaceAnimationIDs.ANNOYED))
			.addContinueDialog(hopleez.createDialogStepForNpc("Not only is this guy crashing me, but he's trying to outdress me too!", FaceAnimationIDs.ANNOYED_2))
			.addContinueDialog(new RunelitePlayerDialogStep(client, "Hatius? I'm pretty sure he's been here much longer than you....", FaceAnimationIDs.QUESTIONING))
			.addContinueDialog(hopleez.createDialogStepForNpc("I swear he wasn't here when I first arrived, I went away for a second and suddenly he's here!", FaceAnimationIDs.ANNOYED_2))
			.addContinueDialog(hopleez.createDialogStepForNpc("Help me teach him a lesson, get me that old cabbage from outside the The Sheared Ram."))
			.addContinueDialog(new RunelitePlayerDialogStep(client, "Umm, sure....", talkedToHopleez.getSetter()));
		hopleez.addDialogTree(talkedToCooksCousin, hopleezDialog1);


		RuneliteDialogStep hopleezWaitingForCabbageDialog = hopleez.createDialogStepForNpc("Get me that cabbage!");
		hopleez.addDialogTree(talkedToHopleez, hopleezWaitingForCabbageDialog);

		RuneliteConfigSetter endQuest = new RuneliteConfigSetter(configManager, getQuest().getPlayerQuests().getConfigValue(), "4");
		RuneliteDialogStep hopleezGiveCabbageDialog = hopleez.createDialogStepForNpc("Have you got the cabbage?");
		hopleezGiveCabbageDialog
			.addContinueDialog(new RunelitePlayerDialogStep(client, "I have! Here you go, why do you need it?"))
			.addContinueDialog(hopleez.createDialogStepForNpc("Nice! Now let's sort out this crasher..."))
			.addContinueDialog(hopleez.createDialogStepForNpc("Oi noob, take this!"))
			.addContinueDialog(new RuneliteObjectDialogStep("Hatius Cosaintus", "What on earth?", NpcID.HATIUS_COSAINTUS).setStateProgression(endQuest));
		hopleez.addDialogTree(pickedCabbage, hopleezGiveCabbageDialog);
	}

	private void setupCabbage()
	{
		// Old cabbage
		cabbage = runeliteObjectManager.createFakeItem(this.toString(), new int[]{ 8196 }, new WorldPoint(3231, 3235, 0), -1);
		cabbage.setName("Old cabbage");
		cabbage.setExamine("A mouldy looking cabbage.");
		cabbage.addExamineAction(runeliteObjectManager);
		cabbage.setDisplayRequirement(displayCabbage);
		cabbage.addTakeAction(runeliteObjectManager, new RuneliteConfigSetter(configManager, getQuest().getPlayerQuests().getConfigValue(), "3"),
			"You pick up the old cabbage.");

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
		return Collections.singletonList(new QuestRequirement(QuestHelperQuest.COOKS_ASSISTANT, QuestState.FINISHED));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(
			new UnlockReward("A replacement for Hatius Cosaintus")
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
