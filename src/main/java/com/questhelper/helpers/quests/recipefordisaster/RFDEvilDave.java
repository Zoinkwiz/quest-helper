/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.helpers.quests.recipefordisaster;

import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.NpcCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.FollowerItemRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class RFDEvilDave extends BasicQuestHelper
{
	ItemRequirement cat, stews, stew, teleportLumbridge, teleportEdgeville, evilStew, evilStewHighlighted;

	Requirement inDiningRoom, inEvilDaveRoom;

	QuestStep enterDiningRoom, inspectEvilDave, talkToDoris, enterBasement, talkToEvilDave, goUpToDoris,
		enterBasementAgain, enterDiningRoomAgain, useStewOnEvilDave, makeStew;

	//Zones
	Zone diningRoom, evilDaveRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectEvilDave = new ConditionalStep(this, enterDiningRoom);
		goInspectEvilDave.addStep(inDiningRoom, inspectEvilDave);
		steps.put(0, goInspectEvilDave);

		ConditionalStep goTalkToEvilDave = new ConditionalStep(this, enterBasement);
		goTalkToEvilDave.addStep(inEvilDaveRoom, talkToEvilDave);
		steps.put(1, goTalkToEvilDave);

		ConditionalStep goTalkToDoris = new ConditionalStep(this, talkToDoris);
		goTalkToDoris.addStep(inEvilDaveRoom, goUpToDoris);
		steps.put(2, goTalkToDoris);

		steps.put(3, makeStew);

		ConditionalStep goGiveStewToEvilDave = new ConditionalStep(this, enterDiningRoomAgain);
		goGiveStewToEvilDave.addStep(inDiningRoom, useStewOnEvilDave);
		steps.put(4, goGiveStewToEvilDave);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		teleportLumbridge = new ItemRequirement("Teleport to Lumbridge", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		teleportEdgeville = new ItemRequirement("Teleport to Edgeville", ItemCollections.AMULET_OF_GLORIES);

		stew = new ItemRequirement("Stew", ItemID.STEW);
		stews = new ItemRequirement("Many stews", ItemID.STEW, -1);
		cat = new FollowerItemRequirement("A non-overgrown cat for catching rats", ItemCollections.HUNTING_CATS,
			NpcCollections.getHuntingCats());

		evilStew = new ItemRequirement("Spicy stew", ItemID.HUNDRED_DAVE_STEW);
		evilStewHighlighted = new ItemRequirement("Spicy stew", ItemID.HUNDRED_DAVE_STEW);
		evilStewHighlighted.setHighlightInInventory(true);
	}

	@Override
	protected void setupZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		evilDaveRoom = new Zone(new WorldPoint(3068, 9874, 0), new WorldPoint(3086, 9904, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneRequirement(diningRoom);
		inEvilDaveRoom = new ZoneRequirement(evilDaveRoom);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.HUNDRED_LUMBRIDGE_DOUBLEDOORL, new WorldPoint(3213, 3221, 0),
			"Go inspect Evil Dave in the Lumbridge Banquet room.");
		inspectEvilDave = new ObjectStep(this, ObjectID.HUNDRED_DAVE_BASE, new WorldPoint(1865, 5323, 0),
			"Inspect Evil Dave.");
		inspectEvilDave.addSubSteps(enterDiningRoom);

		talkToDoris = new NpcStep(this, NpcID.HUNDRED_DAVE_MUM, new WorldPoint(3079, 3494, 0), "Talk to Doris.");

		enterBasement = new ObjectStep(this, ObjectID._100_DAVE_CELLER_TRAPDOOR_CLOSED, new WorldPoint(3077, 3493, 0),
			"Enter Doris's basement in Edgeville.");
		((ObjectStep) enterBasement).addAlternateObjects(ObjectID._100_DAVE_CELLER_TRAPDOOR_OPEN);

		talkToEvilDave = new NpcStep(this, NpcID.HUNDRED_EVIL_DAVE, new WorldPoint(3080, 9889, 0), "Talk to Evil Dave.");
		talkToEvilDave.addDialogSteps("What did you eat at the secret council meeting?",
			"You've got to tell me because the magic requires it!");

		goUpToDoris = new ObjectStep(this, ObjectID._100_DAVE_CELLER_STAIRS, new WorldPoint(3076, 9893, 0),
			"Go up to Doris.");

		enterBasementAgain = new ObjectStep(this, ObjectID._100_DAVE_CELLER_TRAPDOOR_CLOSED, new WorldPoint(3077, 3493, 0),
			"Go back down to Evil Dave.");
		((ObjectStep) enterBasementAgain).addAlternateObjects(ObjectID._100_DAVE_CELLER_TRAPDOOR_OPEN);

		makeStew = new MakeEvilStew(this);

		enterDiningRoomAgain = new ObjectStep(this, ObjectID.HUNDRED_LUMBRIDGE_DOUBLEDOORL, new WorldPoint(3213, 3221, 0),
			"Go use the stew on Evil Dave in the Lumbridge Banquet room.", evilStew);
		useStewOnEvilDave = new ObjectStep(this, ObjectID.HUNDRED_DAVE_BASE, new WorldPoint(1865, 5323, 0),
			"Use the stew on Evil Dave.", evilStewHighlighted);
		useStewOnEvilDave.addSubSteps(enterDiningRoomAgain);
		useStewOnEvilDave.addIcon(ItemID.HUNDRED_DAVE_STEW);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(cat, stews);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(teleportEdgeville, teleportLumbridge);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new VarbitRequirement(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId(), Operation.GREATER_EQUAL, 3,
			"Started Recipe for Disaster"));
		reqs.add(new QuestRequirement(QuestHelperQuest.GERTRUDES_CAT, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.SHADOW_OF_THE_STORM, QuestState.FINISHED));

		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.COOKING, 7000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to catch Hell Rats"),
				new UnlockReward("Ability to make spicy stews"),
				new UnlockReward("Ability to own a hell-cat"),
				new UnlockReward("Increased access to the Culinaromancer's Chest"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		List<QuestStep> steps = QuestUtil.toArrayList(inspectEvilDave, enterBasement,
			talkToEvilDave, goUpToDoris, talkToDoris, enterBasementAgain);
		steps.addAll(makeStew.getSubsteps());
		steps.add(useStewOnEvilDave);

		allSteps.add(new PanelDetails("Saving Evil Dave", steps, cat, stews));
		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = getQuest().getVar(client);
		if (questState == 0)
		{
			return QuestState.NOT_STARTED;
		}

		if (questState < 5)
		{
			return QuestState.IN_PROGRESS;
		}

		return QuestState.FINISHED;
	}

	@Override
	public boolean isCompleted()
	{
		return (getQuest().getVar(client) >= 5 || client.getVarbitValue(VarbitID.HUNDRED_MAIN_QUEST_VAR) < 3);
	}
}
