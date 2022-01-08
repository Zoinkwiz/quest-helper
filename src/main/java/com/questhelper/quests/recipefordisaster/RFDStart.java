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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_START
)
public class RFDStart extends BasicQuestHelper
{
	//Items Required
	ItemRequirement eyeOfNewt, greenmansAle, rottenTomato, fruitBlast, ashes, ashesHighlighted, fruitBlastHighlighted, dirtyBlast;

	QuestStep talkToCook, useAshesOnFruitBlast, talkToCookAgain, enterDiningRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToCook);

		ConditionalStep goGiveCookItems = new ConditionalStep(this, useAshesOnFruitBlast);
		goGiveCookItems.addStep(dirtyBlast.alsoCheckBank(questBank), talkToCookAgain);
		steps.put(1, goGiveCookItems);

		steps.put(2, enterDiningRoom);

		return steps;
	}

	public void setupRequirements()
	{
		eyeOfNewt = new ItemRequirement("Eye of newt", ItemID.EYE_OF_NEWT);
		greenmansAle = new ItemRequirement("Greenman's ale", ItemID.GREENMANS_ALE);
		rottenTomato = new ItemRequirement("Rotten tomato", ItemID.ROTTEN_TOMATO);
		fruitBlast = new ItemRequirement("Fruit blast", ItemID.FRUIT_BLAST);
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		ashesHighlighted = new ItemRequirement("Ashes", ItemID.ASHES);
		ashesHighlighted.setHighlightInInventory(true);
		fruitBlastHighlighted = new ItemRequirement("Fruit blast", ItemID.FRUIT_BLAST);
		fruitBlastHighlighted.setHighlightInInventory(true);
		dirtyBlast = new ItemRequirement("Dirty blast", ItemID.DIRTY_BLAST);
	}

	public void setupConditions()
	{
		// 4606 0->1

		// 1850 = 2->3
		// 1858-66 0->1
	}

	public void setupSteps()
	{
		talkToCook = new NpcStep(this, NpcID.COOK_4626, new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook.", eyeOfNewt, greenmansAle, rottenTomato, ashes, fruitBlast);
		talkToCook.addDialogStep("Do you have any other quests for me?");
		talkToCook.addDialogSteps("Angry! It makes me angry!", "I don't really care to be honest.");
		talkToCook.addDialogStep("What seems to be the problem?");
		talkToCook.addDialogStep("YES");

		useAshesOnFruitBlast = new DetailedQuestStep(this, "Use ashes on the fruit blast.", ashesHighlighted, fruitBlastHighlighted);

		talkToCookAgain = new NpcStep(this, NpcID.COOK_4626, new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook with the required items.", eyeOfNewt, greenmansAle, rottenTomato, dirtyBlast);
		talkToCookAgain.addDialogStep("About those ingredients you wanted for the banquet...");

		enterDiningRoom = new ObjectStep(this, ObjectID.DOOR_12348, new WorldPoint(3207, 3217, 0), "Enter the Lumbridge Castle dining room.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(eyeOfNewt, greenmansAle, rottenTomato, ashes, fruitBlast);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.COOKS_ASSISTANT, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.COOKING, 10));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Culinaromancer's Chest"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Help the Cook", Arrays.asList(talkToCook, useAshesOnFruitBlast, talkToCookAgain, enterDiningRoom), eyeOfNewt, greenmansAle, rottenTomato, ashes, fruitBlast));
		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId());
		if (questState >= 3)
		{
			return QuestState.FINISHED;
		}
		return getQuest().getState(client);
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) >= 3);
	}
}
