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
package com.questhelper.quests.sheepshearer;

import com.questhelper.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.NpcStep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;

import java.util.HashMap;
import java.util.Map;

@QuestDescriptor(
	quest = QuestHelperQuest.SHEEP_SHEARER
)
public class SheepShearer extends BasicQuestHelper
{
	//Items Required
	ItemRequirement twentyBallsOfWool, shears;

	QuestStep startStep;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		WorldPoint farmerFredPoint = new WorldPoint(3190, 3273, 0);
		String dialoguePrompt = "Bring Fred the Farmer north of Lumbridge 20 balls of wool (UNNOTED) to finish the " +
			"quest. You don't have to bring them all at once.";

		setupRequirements();

		startStep = new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint, dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 20));

		steps.put(0, startStep);

		steps.put(1, steps.get(0));
		steps.put(2, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 19)));
		steps.put(3, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 18)));
		steps.put(4, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 17)));
		steps.put(5, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 16)));
		steps.put(6, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 15)));
		steps.put(7, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 14)));
		steps.put(8, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 13)));
		steps.put(9, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 12)));
		steps.put(10, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 11)));
		steps.put(11, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 10)));
		steps.put(12, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 9)));
		steps.put(13, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 8)));
		steps.put(14, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 7)));
		steps.put(15, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 6)));
		steps.put(16, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 5)));
		steps.put(17, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 4)));
		steps.put(18, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 3)));
		steps.put(19, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 2)));
		steps.put(20, new NpcStep(this, NpcID.FRED_THE_FARMER, farmerFredPoint,
			dialoguePrompt,
			new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL, 1)));


		steps.get(0).addDialogStep("I'm looking for a quest.");
		steps.get(0).addDialogStep("Yes, okay. I can do that.");
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		twentyBallsOfWool = new ItemRequirement("Balls of wool", ItemID.BALL_OF_WOOL, 20);
		shears = new ItemRequirement("Shears if you plan on collecting wool yourself", ItemID.SHEARS).isNotConsumed();
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(twentyBallsOfWool);
		reqs.add(shears);
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
		return Collections.singletonList(new ExperienceReward(Skill.CRAFTING, 150));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("60 Coins", ItemID.COINS_995, 60));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Bring Fred some wool", Collections.singletonList(startStep), twentyBallsOfWool));
		return allSteps;
	}
}
