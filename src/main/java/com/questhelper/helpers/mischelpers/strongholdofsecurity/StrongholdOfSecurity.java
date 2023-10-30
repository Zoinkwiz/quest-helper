/*
 * Copyright (c) 2023, Okke234
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
package com.questhelper.helpers.mischelpers.strongholdofsecurity;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestDescriptor;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.STRONGHOLD_OF_SECURITY
)
public class StrongholdOfSecurity extends BasicQuestHelper
{
	Requirement inFloorWar, inFloorFamine, inFloorPestilence, inFloorDeath,
		inStartRoomWar, inStartRoomFamine, inStartRoomPestilence,
		notFlap, notSlap, notIdea, notStamp, hasFlap, hasSlap, hasIdea, hasStamp;

	ItemRequirement food;
	Zone floorWar, floorFamine, floorPestilence, floorDeath, startRoomWar, startRoomFamine, startRoomPestilence;

	QuestStep enterStronghold, enterFloorFamine, enterFloorPestilence, enterFloorDeath,
		usePortalWar, usePortalFamine, usePortalPestilence;

	DetailedQuestStep openChestWar, openChestFamine, openChestPestilence, openChestDeath;

	String[] answers = {
		"No.",
		"Me.",
		"Nobody.",
		"Talk to any banker.",
		"Nothing, it's a fake.",
		"Delete it - it's a fake!",
		"Don't give them my password.",
		"Report the player for phishing.",
		"Use the Account Recovery System.",
		"No way! I'm reporting you to Jagex!",
		"No, you should never buy an account.",
		"Secure my device and reset my password.",
		"Decline the offer and report that player.",
		"The birthday of a famous person or event.",
		"Only on the Old School RuneScape website.",
		"Read the text and follow the advice given.",
		"Virus scan my device then change my password.",
		"Report the incident and do not click any links.",
		"Use the account management section on the website.",
		"Don't share your information and report the player.",
		"Set up 2 step authentication with my email provider.",
		"No, you should never allow anyone to level your account.",
		"Authenticator and two-step login on my registered email.",
		"No way! You'll just take my gold for your own! Reported!",
		"Don't type in my password backwards and report the player.",
		"Don't give them the information and send an 'Abuse report'.",
		"Politely tell them no and then use the 'Report Abuse' button.",
		"Don't give out your password to anyone. Not even close friends.",
		"Do not visit the website and report the player who messaged you.",
		"Report the stream as a scam. Real Jagex streams have a 'verified' mark."
	};

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goEnterStronghold = new ConditionalStep(this, enterStronghold);
		goEnterStronghold.addStep(new Conditions(notFlap, inFloorWar), openChestWar);
		goEnterStronghold.addStep(new Conditions(hasFlap, inFloorWar, inStartRoomWar), usePortalWar);
		goEnterStronghold.addStep(new Conditions(notStamp, inFloorWar), enterFloorFamine);

		goEnterStronghold.addStep(new Conditions(notSlap, inFloorFamine), openChestFamine);
		goEnterStronghold.addStep(new Conditions(hasSlap, inFloorFamine, inStartRoomFamine), usePortalFamine);
		goEnterStronghold.addStep(new Conditions(notStamp, inFloorFamine), enterFloorPestilence);

		goEnterStronghold.addStep(new Conditions(notIdea, inFloorPestilence), openChestPestilence);
		goEnterStronghold.addStep(new Conditions(hasIdea, inFloorPestilence, inStartRoomPestilence), usePortalPestilence);
		goEnterStronghold.addStep(new Conditions(notStamp, inFloorPestilence), enterFloorDeath);

		goEnterStronghold.addStep(new Conditions(notStamp, inFloorDeath), openChestDeath);

		//TODO: Highlight confirmation when climbing down -> varb 3854, N579.17 (id: 37945361, parentid: 10551312)
		//TODO: Auto start when entering or when teleporting with Count Check?

		steps.put(0, goEnterStronghold);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		notFlap = new VarbitRequirement(2309, 0);
		notSlap = new VarbitRequirement(2310, 0);
		notIdea = new VarbitRequirement(2311, 0);
		notStamp = new VarbitRequirement(2312, 0);

		hasFlap = new VarbitRequirement(2309, 1);
		hasSlap = new VarbitRequirement(2310, 1);
		hasIdea = new VarbitRequirement(2311, 1);
		hasStamp = new VarbitRequirement(2312, 1);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
	}

	public void setupConditions()
	{
		inFloorWar = new ZoneRequirement(floorWar);
		inFloorFamine = new ZoneRequirement(floorFamine);
		inFloorPestilence = new ZoneRequirement(floorPestilence);
		inFloorDeath = new ZoneRequirement(floorDeath);

		inStartRoomWar = new ZoneRequirement(startRoomWar);
		inStartRoomFamine = new ZoneRequirement(startRoomFamine);
		inStartRoomPestilence = new ZoneRequirement(startRoomPestilence);
	}

	public void setupZones()
	{
		floorWar = new Zone(new WorldPoint(1855, 5248, 0), new WorldPoint(1920, 5184, 0));
		floorFamine = new Zone(new WorldPoint(1983, 5248, 0), new WorldPoint(2048, 5184, 0));
		floorPestilence = new Zone(new WorldPoint(2111, 5310, 0), new WorldPoint(2176, 5248, 0));
		floorDeath = new Zone(new WorldPoint(2304, 5248, 0), new WorldPoint(2367, 5184, 0));

		startRoomWar = new Zone(new WorldPoint(1855, 5246, 0), new WorldPoint(1866, 5239, 0));
		startRoomFamine = new Zone(new WorldPoint(2040, 5245, 0), new WorldPoint(2046, 5240, 0));
		startRoomPestilence = new Zone(new WorldPoint(2117, 5258, 0), new WorldPoint(2133, 5251, 0));
	}

	public void setupSteps()
	{
		enterStronghold = new ObjectStep(this, ObjectID.ENTRANCE_20790, new WorldPoint(3081, 3420, 0),
			"Climb down the entrance to the Stronghold of Security");
		enterFloorFamine = new ObjectStep(this, ObjectID.LADDER_20785, new WorldPoint(1902, 5222, 0),
			"Go to the 2nd floor of the stronghold.");
		enterFloorPestilence = new ObjectStep(this, ObjectID.LADDER_19004, new WorldPoint(2026, 5218, 0),
			"Go to the 3rd floor of the stronghold.");
		enterFloorDeath = new ObjectStep(this, ObjectID.DRIPPING_VINE_23706, new WorldPoint(2148, 5284, 0),
			"Go to the 4th floor of the stronghold.");

		usePortalWar = new ObjectStep(this, ObjectID.PORTAL_20786,
			new WorldPoint(1863, 5238, 0), "Enter the portal.");
		usePortalFamine = new ObjectStep(this, ObjectID.PORTAL_19005,
			new WorldPoint(2039, 5240, 0), "Enter the portal.");
		usePortalPestilence = new ObjectStep(this, ObjectID.PORTAL_23707,
			new WorldPoint(2120, 5258, 0), "Enter the portal.");

		List<WorldPoint> pathFromStartToChest1 = Arrays.asList(
			new WorldPoint(1859, 5243, 0),
			new WorldPoint(1859, 5232, 0),
			new WorldPoint(1864, 5227, 0),
			new WorldPoint(1870, 5227, 0),
			new WorldPoint(1875, 5239, 0),
			new WorldPoint(1880, 5240, 0),
			new WorldPoint(1883, 5243, 0),
			new WorldPoint(1912, 5242, 0),
			new WorldPoint(1912, 5237, 0),
			new WorldPoint(1905, 5234, 0),
			new WorldPoint(1905, 5228, 0),
			new WorldPoint(1907, 5223, 0)
		);

		List<WorldPoint> pathFromStartToChest2 = Arrays.asList(
			new WorldPoint(2042, 5245, 0),
			new WorldPoint(2034, 5244, 0),
			new WorldPoint(2033, 5239, 0),
			new WorldPoint(2028, 5236, 0),
			new WorldPoint(2025, 5245, 0),
			new WorldPoint(2020, 5244, 0),
			new WorldPoint(2019, 5237, 0),
			new WorldPoint(2014, 5237, 0),
			new WorldPoint(2013, 5244, 0),
			new WorldPoint(2005, 5244, 0),
			new WorldPoint(2005, 5230, 0),
			new WorldPoint(2007, 5228, 0),
			new WorldPoint(2011, 5228, 0),
			new WorldPoint(2011, 5231, 0),
			new WorldPoint(2014, 5231, 0),
			new WorldPoint(2014, 5228, 0),
			new WorldPoint(2020, 5227, 0),
			new WorldPoint(2022, 5217, 0)
		);

		List<WorldPoint> pathFromStartToChest3 = Arrays.asList(
			new WorldPoint(2123, 5252, 0),
			new WorldPoint(2132, 5256, 0),
			new WorldPoint(2132, 5261, 0),
			new WorldPoint(2135, 5263, 0),
			new WorldPoint(2159, 5263, 0),
			new WorldPoint(2164, 5271, 0),
			new WorldPoint(2164, 5279, 0),
			new WorldPoint(2156, 5284, 0),
			new WorldPoint(2155, 5291, 0),
			new WorldPoint(2147, 5291, 0),
			new WorldPoint(2144, 5281, 0)
		);

		List<WorldPoint> pathFromStartToChest4 = Arrays.asList(
			new WorldPoint(2358, 5215, 0),
			new WorldPoint(2356, 5216, 0),
			new WorldPoint(2355, 5224, 0),
			new WorldPoint(2348, 5231, 0),
			new WorldPoint(2341, 5232, 0),
			new WorldPoint(2341, 5237, 0),
			new WorldPoint(2331, 5238, 0),
			new WorldPoint(2330, 5245, 0),
			new WorldPoint(2324, 5245, 0),
			new WorldPoint(2323, 5231, 0),
			new WorldPoint(2332, 5227, 0),
			new WorldPoint(2340, 5226, 0),
			new WorldPoint(2341, 5219, 0),
			new WorldPoint(2344, 5215, 0)
		);

		openChestWar = new ObjectStep(this, ObjectID.GIFT_OF_PEACE,
			new WorldPoint(1907, 5222, 0), "Claim 2k coins and the Flap emote.");
		openChestWar.setLinePoints(pathFromStartToChest1);
		openChestWar.setHideMinimapLines(true);
		openChestWar.setHideWorldArrow(true);
		openChestWar.addDialogSteps(answers);

		openChestFamine = new ObjectStep(this, ObjectID.GRAIN_OF_PLENTY,
			new WorldPoint(2021, 5216, 0), "Claim 3k coins and the Slap Head emote.");
		openChestFamine.setLinePoints(pathFromStartToChest2);
		openChestFamine.setHideMinimapLines(true);
		openChestFamine.setHideWorldArrow(true);
		openChestFamine.addDialogSteps(answers);

		openChestPestilence = new ObjectStep(this, ObjectID.BOX_OF_HEALTH,
			new WorldPoint(2144, 5280, 0), "Claim 5k coins and the Idea emote.");
		openChestPestilence.setLinePoints(pathFromStartToChest3);
		openChestPestilence.setHideMinimapLines(true);
		openChestPestilence.setHideWorldArrow(true);
		openChestPestilence.addDialogSteps(answers);

		openChestDeath = new ObjectStep(this, ObjectID.CRADLE_OF_LIFE,
			new WorldPoint(2344, 5214, 0), "Claim Fancy boots or Fighting boots, and the Stamp emote.");
		openChestDeath.setLinePoints(pathFromStartToChest4);
		openChestDeath.setHideMinimapLines(true);
		openChestDeath.setHideWorldArrow(true);
		openChestDeath.addDialogSteps(answers);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Flap, Slap Head, Idea and Stamp emotes."));
	}

	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Coins", ItemID.COINS_995, 10000),
			new ItemReward("Fancy or Fighting boots", ItemID.FANCY_BOOTS, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Entering the stronghold", Collections.singletonList(enterStronghold)));
		allSteps.add(new PanelDetails("Claiming coins", Arrays.asList(openChestWar, openChestFamine, openChestPestilence)));
		allSteps.add(new PanelDetails("Claiming boots", Collections.singletonList(openChestDeath)));
		return allSteps;
	}
}
