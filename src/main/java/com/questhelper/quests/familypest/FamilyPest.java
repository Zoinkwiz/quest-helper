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

package com.questhelper.quests.familypest;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.FAMILY_PEST
)
public class FamilyPest extends BasicQuestHelper
{
	//Item Requirements
	ItemRequirement coins;

	//Item Recommended
	ItemRequirement dueling, camelotTele, varrockTele, lumberTele;

	//Quest Steps
	QuestStep talkToDimintheis, talkToAvan, talkToCaleb, talkToJohnathon, talkToDimintheis2, goUpstairs;

	//Zone
	Zone upstairsJollyBoar;

	//Conditions
	Requirement upJollyBoar, talkedToAvan, talkedToCaleb, talkedToJohnathon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToDimintheis);

		ConditionalStep talkToBrothers = new ConditionalStep(this, talkToAvan);
		talkToBrothers.addStep(new Conditions(talkedToCaleb, talkedToAvan, talkedToJohnathon), talkToDimintheis2);
		talkToBrothers.addStep(new Conditions(upJollyBoar, talkedToCaleb), talkToJohnathon);
		talkToBrothers.addStep(talkedToCaleb, goUpstairs);
		talkToBrothers.addStep(talkedToAvan, talkToCaleb);
		steps.put(1, talkToBrothers);
		steps.put(2, talkToBrothers);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		//Recommended
		dueling = new ItemRequirement("Ring of Dueling", -1, -1);
		camelotTele = new ItemRequirement("Camelot Teleports", -1, -1);
		varrockTele = new ItemRequirement("Varrock Teleports", -1, -1);
		lumberTele = new ItemRequirement("Lumberyard Teleport", -1, -1);

		//Required
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 500000);
	}

	public void setupSteps()
	{
		talkToDimintheis = new NpcStep(this, NpcID.DIMINTHEIS, new WorldPoint(3280, 3404, 0), "Talk to Dimintheis (Southeast of Varrock.");
		talkToDimintheis.addDialogSteps("Have you got any quests for me?", "Oh come on, however menial, I want to help!");

		talkToAvan = new NpcStep(this, NpcID.AVAN_387, new WorldPoint(3294, 3282, 0), "Talk to Avan (Entrance to the Al Kharid Mine).");
		talkToAvan.addDialogStep("Family Pest");

		talkToCaleb = new NpcStep(this, NpcID.CALEB_5433, new WorldPoint(2819, 3453, 0), "Talk to Caleb (Northeast of Catherby bank).");
		talkToCaleb.addDialogStep("Family Pest");

		talkToJohnathon = new NpcStep(this, NpcID.JOHNATHON_5445, new WorldPoint(3281, 3505, 1), "Talk to Johnathon.");
		talkToJohnathon.addDialogStep("Family Pest");
		goUpstairs = new ObjectStep(this, ObjectID.STAIRCASE_11797, new WorldPoint(3286, 3494, 0), "Go upstairs in the Jolly Boar Inn (Northeast of Varrock) and talk to Johnathon.");
		goUpstairs.addSubSteps(talkToJohnathon);

		talkToDimintheis2 = new NpcStep(this, NpcID.DIMINTHEIS, new WorldPoint(3280, 3404, 0), "Return to Dimintheis and pay him 500k.");
		talkToDimintheis2.addDialogSteps("Family Pest", "Yes");
	}

	public void setupConditions()
	{
		upJollyBoar = new ZoneRequirement(upstairsJollyBoar);

		talkedToCaleb = new VarbitRequirement(5348, 1);
		talkedToAvan = new VarbitRequirement(5349, 1);
		talkedToJohnathon = new VarbitRequirement(5350, 1);
	}

	public void loadZones()
	{
		upstairsJollyBoar = new Zone(new WorldPoint(3273, 3485, 1), new WorldPoint(3287, 3509, 1));
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(coins);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(dueling, camelotTele, varrockTele, lumberTele);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to own all three Steel Gauntlets simultaneously"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Talk to the Brothers", Arrays.asList(talkToDimintheis,
			talkToAvan, talkToCaleb, goUpstairs, talkToDimintheis2), coins));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED));
		return req;
	}
}
