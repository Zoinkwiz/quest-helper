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
package com.questhelper.helpers.quests.sheepshearer;

import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questinfo.QuestDescriptor;
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
	ItemRequirement ballOfWool, shears, coins, wool;

	QuestStep startStep, getSheers, climbStairsUp, climbStairsDown, spinBalls, turnInBalls;

	NpcStep shearSheep;

	Zone castleSecond;

	Requirement inCastleSecond;

	int woolNeeded;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupConditions();
		setupRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep craftingBalls = new ConditionalStep(this, getSheers);
		craftingBalls.addStep(new Conditions(ballOfWool.quantity(woolNeeded), inCastleSecond), climbStairsDown);
		craftingBalls.addStep(ballOfWool.quantity(woolNeeded), turnInBalls);
		craftingBalls.addStep(new Conditions(wool.quantity(woolNeeded), inCastleSecond), spinBalls);
		craftingBalls.addStep(wool.quantity(woolNeeded), climbStairsUp);
		craftingBalls.addStep(shears, shearSheep);

		steps.put(client.getVarpValue(179), craftingBalls);// catches if closed mid-quest or partially complete
		steps.put(0, startStep);
		steps.put(1, craftingBalls);
		steps.put(20, craftingBalls);// prevents helper from crashing

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		ballOfWool = new ItemRequirement("Balls of wool", ItemID.BALL_OF_WOOL);
		shears = new ItemRequirement("Shears", ItemID.SHEARS).isNotConsumed();
		shears.setTooltip("if you plan on collecting wool yourself");
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 20);
		wool = new ItemRequirement("Wool", ItemID.WOOL);
		wool.addAlternates(ItemID.BALL_OF_WOOL);

		woolNeeded = client.getVarpValue(179) != 1 ? 21 - client.getVarpValue(179) : 20;
	}

	public void loadZones()
	{
		castleSecond = new Zone(new WorldPoint(3200, 3232, 1), new WorldPoint(3220, 3205, 1));
	}

	public void setupConditions()
	{
		inCastleSecond = new ZoneRequirement(castleSecond);
	}

	public void setupSteps()
	{
		startStep = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0),
			"Talk Fred the Farmer north of Lumbridge to start the quest. Bring 20 balls of wool to autocomplete the quest.");
		startStep.addDialogSteps("I'm looking for a quest.", "Yes, okay. I can do that.", "Yes.");
		getSheers = new ItemStep(this, new WorldPoint(3190, 3273, 0),
			"Pickup the shears in Fred's house.", shears);
		shearSheep = new NpcStep(this, NpcID.SHEEP_2786, new WorldPoint(3201, 3268, 0),
			"Shear " + woolNeeded + " sheep in the nearby field.", true, shears);
		shearSheep.addAlternateNpcs(NpcID.SHEEP_2699, NpcID.SHEEP_2787, NpcID.SHEEP_2693, NpcID.SHEEP_2694, NpcID.SHEEP_2699, NpcID.SHEEP_2695);
		climbStairsUp = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(3204, 3207, 0),
			"Climb the staircase in the Lumbridge Castle.", wool.quantity(woolNeeded));
		spinBalls = new ObjectStep(this, ObjectID.SPINNING_WHEEL_14889, new WorldPoint(3209, 3212, 1),
			"Spin your wool into balls.", wool.quantity(woolNeeded));
		climbStairsDown = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(3204, 3207, 1),
			"Climb down the staircase.", ballOfWool.quantity(woolNeeded));
		turnInBalls = new NpcStep(this, NpcID.FRED_THE_FARMER, new WorldPoint(3190, 3273, 0),
			"Bring Fred the Farmer north of Lumbridge " + woolNeeded + " balls of wool (UNNOTED) to finish the quest.",
			ballOfWool.quantity(woolNeeded));
		turnInBalls.addDialogSteps("I need to talk to you about shearing these sheep!");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ballOfWool.quantity(20));
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
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS_995, 60));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Bring Fred Some Wool", Arrays.asList(startStep, getSheers, shearSheep,
			climbStairsUp, spinBalls, climbStairsDown, turnInBalls), ballOfWool.quantity(20)));
		return allSteps;
	}
}
