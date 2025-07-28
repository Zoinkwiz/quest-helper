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
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class StrongholdOfSecurity extends BasicQuestHelper
{
	static final String[] CORRECT_ANSWERS = {
		"No.",
		"Me.",
		"Nobody.",
		"Talk to any banker.",
		"Nothing, it's a fake.",
		"Delete it - it's a fake!",
		"Don't give them my password.",
		"Report the player for phishing.",
		"Use the Account Recovery system.",
		"No way! I'm reporting you to Jagex!",
		"No, you should never buy an account.",
		"Secure my device and reset my password.",
		"Decline the offer and report that player.",
		"The birthday of a famous person or event.",
		"Only on the Old School RuneScape website.",
		"Read the text and follow the advice given.",
		"Virus scan my device then change my password.",
		"Report the incident and do not click any links.",
		"Don't share your information and report the player.",
		"Set up two-factor authentication with my email provider.",
		"No, you should never allow anyone to level your account.",
		"Authenticator and two-step login on my registered email.",
		"No way! You'll just take my gold for your own! Reported!",
		"Don't type in my password backwards and report the player.",
		"Don't give them the information and send an 'Abuse report'.",
		"Don't tell them anything and click the 'Report Abuse' button.",
		"Politely tell them no and then use the 'Report Abuse' button.",
		"Don't give out your password to anyone. Not even close friends.",
		"Do not visit the website and report the player who messaged you.",
		"Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
		"Two-factor authentication on yuor account and your registered email.",
		"Nope, you're tricking me into going somewhere dangerous.",
	};

	static final int CB_LEVEL_SKIP_WAR = 26;
	static final int CB_LEVEL_SKIP_FAMINE = 51;
	static final int CB_LEVEL_SKIP_PESTILENCE = 76;

	// Recommended items
	ItemRequirement food;

	// Miscellaneous requirements
	Requirement canSkipWar;
	Requirement canSkipFamine;
	Requirement canSkipPestilence;
	Requirement notUsedCountCheck;
	Requirement nearCountCheck;
	Requirement inFloorWar;
	Requirement inFloorFamine;
	Requirement inFloorPestilence;
	Requirement inFloorDeath;
	Requirement inStartRoomWar;
	Requirement inStartRoomFamine;
	Requirement inStartRoomPestilence;
	Requirement notFlap;
	Requirement notSlap;
	Requirement notIdea;
	Requirement notStamp;
	Requirement hasFlap;
	Requirement hasSlap;
	Requirement hasIdea;
	Requirement hasStamp;

	// Zones
	Zone countCheck;
	Zone floorWar;
	Zone floorFamine;
	Zone floorPestilence;
	Zone floorDeath;
	Zone startRoomWar;
	Zone startRoomFamine;
	Zone startRoomPestilence;

	// Steps
	QuestStep talkToCountCheck;
	QuestStep enterStronghold;
	QuestStep usePortalWar;
	QuestStep usePortalFamine;
	QuestStep usePortalPestilence;
	DetailedQuestStep openChestWar;
	DetailedQuestStep openChestFamine;
	DetailedQuestStep openChestPestilence;
	DetailedQuestStep openChestDeath;
	DetailedQuestStep enterFloorFamine;
	DetailedQuestStep enterFloorPestilence;
	DetailedQuestStep enterFloorDeath;

	@Override
	protected void setupZones()
	{
		countCheck = new Zone(new WorldPoint(3120, 3275, 0), new WorldPoint(3267, 3135, 0));
		floorWar = new Zone(new WorldPoint(1855, 5248, 0), new WorldPoint(1920, 5184, 0));
		floorFamine = new Zone(new WorldPoint(1983, 5248, 0), new WorldPoint(2048, 5184, 0));
		floorPestilence = new Zone(new WorldPoint(2111, 5310, 0), new WorldPoint(2176, 5248, 0));
		floorDeath = new Zone(new WorldPoint(2304, 5248, 0), new WorldPoint(2367, 5184, 0));
		startRoomWar = new Zone(new WorldPoint(1855, 5246, 0), new WorldPoint(1866, 5239, 0));
		startRoomFamine = new Zone(new WorldPoint(2040, 5245, 0), new WorldPoint(2046, 5240, 0));
		startRoomPestilence = new Zone(new WorldPoint(2117, 5258, 0), new WorldPoint(2133, 5251, 0));

	}

	@Override
	protected void setupRequirements()
	{
		canSkipWar = new CombatLevelRequirement(CB_LEVEL_SKIP_WAR);
		canSkipFamine = new CombatLevelRequirement(CB_LEVEL_SKIP_FAMINE);
		canSkipPestilence = new CombatLevelRequirement(CB_LEVEL_SKIP_PESTILENCE);

		nearCountCheck = new ZoneRequirement(countCheck);
		inFloorWar = new ZoneRequirement(floorWar);
		inFloorFamine = new ZoneRequirement(floorFamine);
		inFloorPestilence = new ZoneRequirement(floorPestilence);
		inFloorDeath = new ZoneRequirement(floorDeath);
		inStartRoomWar = new ZoneRequirement(startRoomWar);
		inStartRoomFamine = new ZoneRequirement(startRoomFamine);
		inStartRoomPestilence = new ZoneRequirement(startRoomPestilence);

		notUsedCountCheck = new VarbitRequirement(VarbitID.SOS_TELEPORTED_BY_COUNT, 0);
		notFlap = new VarbitRequirement(VarbitID.SOS_EMOTE_FLAP, 0);
		notSlap = new VarbitRequirement(VarbitID.SOS_EMOTE_DOH, 0);
		notIdea = new VarbitRequirement(VarbitID.SOS_EMOTE_IDEA, 0);
		notStamp = new VarbitRequirement(VarbitID.SOS_EMOTE_STAMP, 0);
		hasFlap = new VarbitRequirement(VarbitID.SOS_EMOTE_FLAP, 1);
		hasSlap = new VarbitRequirement(VarbitID.SOS_EMOTE_DOH, 1);
		hasIdea = new VarbitRequirement(VarbitID.SOS_EMOTE_IDEA, 1);
		hasStamp = new VarbitRequirement(VarbitID.SOS_EMOTE_STAMP, 1);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
	}

	public void setupSteps()
	{
		var pathFromStartToChest1 = List.of(
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
			new WorldPoint(1905, 5228, 0)
		);

		var pathFromStartToChest2 = List.of(
			new WorldPoint(2042, 5245, 0),
			new WorldPoint(2034, 5244, 0),
			new WorldPoint(2033, 5239, 0),
			new WorldPoint(2030, 5236, 0),
			new WorldPoint(2027, 5236, 0),
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
			new WorldPoint(2020, 5227, 0)
		);

		var pathFromStartToChest3 = List.of(
			new WorldPoint(2123, 5252, 0),
			new WorldPoint(2132, 5256, 0),
			new WorldPoint(2132, 5261, 0),
			new WorldPoint(2135, 5263, 0),
			new WorldPoint(2159, 5263, 0),
			new WorldPoint(2163, 5267, 0),
			new WorldPoint(2164, 5271, 0),
			new WorldPoint(2164, 5279, 0),
			new WorldPoint(2156, 5284, 0),
			new WorldPoint(2155, 5291, 0),
			new WorldPoint(2147, 5291, 0)
		);

		var pathFromStartToChest4 = List.of(
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
			new WorldPoint(2341, 5219, 0)
		);

		talkToCountCheck = new NpcStep(this, NpcID.COUNT_CHECK, new WorldPoint(3238, 3199, 0),
			"Have Count Check teleport you to the Stronghold. (one-time only)");
		talkToCountCheck.addDialogStep("Where can I learn more about security?");
		talkToCountCheck.addDialogStep("Yes");

		enterStronghold = new ObjectStep(this, ObjectID.SOS_DUNG_ENT_OPEN, new WorldPoint(3081, 3420, 0),
			"Climb down the entrance to the Stronghold of Security.");
		enterFloorFamine = new ObjectStep(this, ObjectID.SOS_WAR_LADD_DOWN, new WorldPoint(1902, 5222, 0),
			"Go to the 2nd floor of the stronghold.");
		enterFloorFamine.setLinePoints(pathFromStartToChest1);
		enterFloorFamine.setHideMinimapLines(true);
		enterFloorFamine.addDialogSteps(CORRECT_ANSWERS);
		enterFloorPestilence = new ObjectStep(this, ObjectID.SOS_FAM_LADD_DOWN, new WorldPoint(2026, 5218, 0),
			"Go to the 3rd floor of the stronghold.");
		enterFloorPestilence.setLinePoints(pathFromStartToChest2);
		enterFloorPestilence.setHideMinimapLines(true);
		enterFloorPestilence.addDialogSteps(CORRECT_ANSWERS);
		enterFloorDeath = new ObjectStep(this, ObjectID.SOS_PEST_LADD_DOWN, new WorldPoint(2148, 5284, 0),
			"Go to the 4th floor of the stronghold.");
		enterFloorDeath.setLinePoints(pathFromStartToChest3);
		enterFloorDeath.setHideMinimapLines(true);
		enterFloorDeath.addDialogSteps(CORRECT_ANSWERS);

		usePortalWar = new ObjectStep(this, ObjectID.SOS_WAR_PORTAL,
			new WorldPoint(1863, 5238, 0), "Enter the portal.");
		usePortalFamine = new ObjectStep(this, ObjectID.SOS_FAM_PORTAL,
			new WorldPoint(2039, 5240, 0), "Enter the portal.");
		usePortalPestilence = new ObjectStep(this, ObjectID.SOS_PEST_PORTAL,
			new WorldPoint(2120, 5258, 0), "Enter the portal.");

		openChestWar = new ObjectStep(this, ObjectID.SOS_WAR_CHEST,
			new WorldPoint(1907, 5222, 0), "Claim 2k coins and the Flap emote.");
		openChestWar.setLinePoints(pathFromStartToChest1);
		openChestWar.setHideMinimapLines(true);
		openChestWar.addDialogSteps(CORRECT_ANSWERS);

		openChestFamine = new ObjectStep(this, ObjectID.SOS_FAM_SACK,
			new WorldPoint(2021, 5216, 0), "Claim 3k coins and the Slap Head emote.");
		openChestFamine.setLinePoints(pathFromStartToChest2);
		openChestFamine.setHideMinimapLines(true);
		openChestFamine.addDialogSteps(CORRECT_ANSWERS);

		openChestPestilence = new ObjectStep(this, ObjectID.SOS_PEST_CHEST,
			new WorldPoint(2144, 5280, 0), "Claim 5k coins and the Idea emote.");
		openChestPestilence.setLinePoints(pathFromStartToChest3);
		openChestPestilence.setHideMinimapLines(true);
		openChestPestilence.addDialogSteps(CORRECT_ANSWERS);

		openChestDeath = new ObjectStep(this, ObjectID.SOS_DEATH_PRAM,
			new WorldPoint(2344, 5214, 0), "Claim Fancy boots or Fighting boots, and the Stamp emote.");
		openChestDeath.setLinePoints(pathFromStartToChest4);
		openChestDeath.setHideMinimapLines(true);
		openChestDeath.addDialogSteps(CORRECT_ANSWERS);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var goEnterStronghold = new ConditionalStep(this, enterStronghold);
		goEnterStronghold.addStep(and(nearCountCheck, notUsedCountCheck), talkToCountCheck);
		goEnterStronghold.addStep(and(or(canSkipWar, hasFlap),
			inFloorWar, inStartRoomWar), usePortalWar);
		goEnterStronghold.addStep(and(notFlap, inFloorWar), openChestWar);
		goEnterStronghold.addStep(and(notStamp, inFloorWar), enterFloorFamine);

		goEnterStronghold.addStep(and(or(canSkipFamine, hasSlap),
			inFloorFamine, inStartRoomFamine), usePortalFamine);
		goEnterStronghold.addStep(and(notSlap, inFloorFamine), openChestFamine);
		goEnterStronghold.addStep(and(notStamp, inFloorFamine), enterFloorPestilence);

		goEnterStronghold.addStep(and(or(canSkipPestilence, hasIdea),
			inFloorPestilence, inStartRoomPestilence), usePortalPestilence);
		goEnterStronghold.addStep(and(notIdea, inFloorPestilence), openChestPestilence);
		goEnterStronghold.addStep(and(notStamp, inFloorPestilence), enterFloorDeath);

		goEnterStronghold.addStep(and(notStamp, inFloorDeath), openChestDeath);

		//TODO: Highlight warning confirmation when climbing down ladder
		//TODO: Auto start when entering or when teleporting with Count Check?

		steps.put(0, goEnterStronghold);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			food
		);
	}

	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 10000),
			new ItemReward("Fancy or Fighting boots", ItemID.SOS_BOOTS, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Flap, Slap Head, Idea and Stamp emotes.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Entering the stronghold", List.of(
			talkToCountCheck,
			enterStronghold
		)));
		steps.add(new PanelDetails("Claiming coins", List.of(
			openChestWar,
			openChestFamine,
			openChestPestilence
		)));
		steps.add(new PanelDetails("Claiming boots", List.of(
			openChestDeath
		)));

		return steps;
	}
}
