/*
 * Copyright (c) 2025, Zoinkwiz <https://www.github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.guides;

import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor.LumbridgeEasy;
import com.questhelper.helpers.quests.therestlessghost.TheRestlessGhost;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ActiveStepFromHelperStep;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;

public class BoatyGuideHelper extends ComplexStateQuestHelper
{
	// Required items
	ItemRequirement egg;

	// Zones
	Zone millSecond;


	// Miscellaneous requirements

	// Bank 0
	DialogRequirement hasTalkedToAereck;



	// Steps

	// Bank 0
	NpcStep getRunesFromMagicTutor;
	NpcStep sellItemsToGeneralStore;
	NpcStep startXMarksTheSpot;
	DigStep xMarksTheSpotDigSpot1;
	NpcStep checkPlaytimeHans;
	ConditionalStep goTalkToDukeHoracio;
	ConditionalStep goCollect4Logs;
	ConditionalStep goDepositEverythingInLumbridgeBank;
	ConditionalStep goSetBankPin;


	// Loaded Helpers
	TheRestlessGhost theRestlessGhost = (TheRestlessGhost) QuestHelperQuest.THE_RESTLESS_GHOST.getQuestHelper();
	LumbridgeEasy lumbridgeEasy = (LumbridgeEasy) QuestHelperQuest.LUMBRIDGE_EASY.getQuestHelper();


	@Override
	protected void setupZones()
	{

	}

	@Override
	protected void setupRequirements()
	{

	}

	public void setupSteps()
	{
//		getRunesFromMagicTutor;
//		sellItemsToGeneralStore;
//		startXMarksTheSpot;
//		xMarksTheSpotDigSpot1;
//		checkPlaytimeHans;
//		goTalkToDukeHoracio;
//		goCollect4Logs;
//		goDepositEverythingInLumbridgeBank;
//		goSetBankPin;
	}

	public void setupHelpers()
	{
		theRestlessGhost.startUp(config);
		lumbridgeEasy.startUp(config);
	}

	@Override
	public void shutDown()
	{
		super.shutDown();
		theRestlessGhost.shutDown();
		lumbridgeEasy.shutDown();
	}


	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();
		setupHelpers();

		var completedFirstStepRestlessGhost = new QuestRequirement(theRestlessGhost, 1);
		var notPickpocketedManOrWoman = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY, false, 6);

		var fullHelper = new ConditionalStep(this, new DetailedQuestStep(this, "You've completed everything, or something has gone wrong!"));
		fullHelper.addStep(nor(completedFirstStepRestlessGhost), new ActiveStepFromHelperStep(this, theRestlessGhost));
		fullHelper.addStep(notPickpocketedManOrWoman, lumbridgeEasy.getPickpocket());

		return fullHelper;
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Song of the Elves completed.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Starting off", List.of(
			theRestlessGhost.getTalkToAereck(),
			lumbridgeEasy.getPickpocket()
		), List.of(
		)));

		return steps;
	}
}
