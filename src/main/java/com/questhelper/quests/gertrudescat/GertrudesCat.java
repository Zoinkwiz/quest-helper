/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
package com.questhelper.quests.gertrudescat;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.IconID;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.GERTRUDES_CAT
)
public class GertrudesCat extends BasicQuestHelper
{
	private ItemRequirement bucketOfMilk, coins, seasonedSardine;

	private QuestStep talkToGertrude, talkToChildren,
		gertrudesCat, gertrudesCat2, searchNearbyCrates, giveKittenToFluffy,
		finishQuest;

	private ConditionForStep isUpstairsLumberyard, hasFluffsKitten;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();

		return getSteps();
	}

	private Map<Integer, QuestStep> getSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGertrude = getTalkToGertrude());
		steps.put(1, talkToChildren = getTalkToChildren());
		steps.put(2, getLumberyard());
		steps.put(3, getFeedCat());
		steps.put(4, findFluffsKitten());
		steps.put(5, finishQuest = returnToGertrude());
		return steps;
	}

	private NpcStep returnToGertrude()
	{
		return new NpcStep(this, NpcID.GERTRUDE,
			new WorldPoint(3148, 3413, 0), "Talk to Gertrude.");
	}

	private QuestStep findFluffsKitten()
	{

		//Need to find to ways to hide arrow
		searchNearbyCrates = new NpcStep(this, NpcID.CRATE,
			"Search for a kitten.", true);
		ObjectStep climbDownLadderStep = goDownLadderStep();
		ObjectStep climbUpLadderStep = getClimbLadder();
		ArrayList<ItemRequirement> fluffsKittenRequirement = new ArrayList();
		fluffsKittenRequirement.add(new ItemRequirement("Fluffs' Kitten", ItemID.FLUFFS_KITTEN));

		climbUpLadderStep.addItemRequirements(fluffsKittenRequirement);
		Conditions hasFluffsKittenUpstairs = new Conditions(hasFluffsKitten, isUpstairsLumberyard);

		giveKittenToFluffy = getGertrudesCat(new ItemRequirement("Fluffs' Kitten", ItemID.FLUFFS_KITTEN));
		giveKittenToFluffy.setText("Return the kitten to Gertrude's Cat");
		giveKittenToFluffy.addIcon(ItemID.FLUFFS_KITTEN);

		ConditionalStep conditionalKitten = new ConditionalStep(this, searchNearbyCrates);
		conditionalKitten.addStep(hasFluffsKittenUpstairs, giveKittenToFluffy);
		conditionalKitten.addStep(hasFluffsKitten, climbUpLadderStep);
		conditionalKitten.addStep(isUpstairsLumberyard, climbDownLadderStep);

		searchNearbyCrates.addSubSteps(climbDownLadderStep);
		giveKittenToFluffy.addSubSteps(climbUpLadderStep);

		return conditionalKitten;
	}

	private ObjectStep goDownLadderStep()
	{
		return new ObjectStep(this, ObjectID.LADDER_11795, new WorldPoint(3310, 3509, 1)
			, "Climb down ladder in the Lumberyard.");
	}

	private QuestStep getFeedCat()
	{
		gertrudesCat2 = getGertrudesCat(null);
		gertrudesCat2.addIcon(ItemID.SEASONED_SARDINE);

		ObjectStep climbLadder = new ObjectStep(this, ObjectID.LADDER_11794,
			new WorldPoint(3310, 3509, 0), "Climb ladder");

		ConditionalStep lumberyard = new ConditionalStep(this, climbLadder, "Use a bucket of milk on Gertrude's cat upstairs in the Lumberyard north east of Varrock.", seasonedSardine);
		lumberyard.addStep(isUpstairsLumberyard, gertrudesCat2);
		gertrudesCat2.addSubSteps(climbLadder);

		return lumberyard;
	}

	private QuestStep getLumberyard()
	{
		gertrudesCat = getGertrudesCat(new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK));
		gertrudesCat.addIcon(ItemID.BUCKET_OF_MILK);

		ObjectStep climbLadder = getClimbLadder();

		ConditionalStep lumberyard = new ConditionalStep(this, climbLadder, "Use a bucket of milk on Gertrude's cat upstairs in the Lumberyard north east of Varrock.", bucketOfMilk, seasonedSardine);
		lumberyard.addStep(isUpstairsLumberyard, gertrudesCat);
		gertrudesCat.addSubSteps(climbLadder);

		return lumberyard;
	}

	private NpcStep getGertrudesCat(ItemRequirement... requirement)
	{
		return new NpcStep(this, NpcID.GERTRUDES_CAT_3497,
			new WorldPoint(3308, 3511, 1), "", requirement);
	}

	private QuestStep getTalkToChildren()
	{
		NpcStep talkToChildren = new NpcStep(this, NpcID.SHILOP,
			new WorldPoint(3222, 3435, 0), "Talk to Shilop or Wilough.", true, coins);
		talkToChildren.addAlternateNpcs(NpcID.WILOUGH);
		talkToChildren.addDialogStep("What will make you tell me?");
		talkToChildren.addDialogStep("Okay then, I'll pay.");
		return talkToChildren;
	}

	private QuestStep getTalkToGertrude()
	{
		NpcStep talkToGertrude = new NpcStep(this, NpcID.GERTRUDE,
			new WorldPoint(3148, 3413, 0), "Talk to Gertrude.");
		talkToGertrude.addDialogStep("Yes.");
		return talkToGertrude;
	}

	public void setupItemRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK, 1);
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 100);

		seasonedSardine = new ItemRequirement("Seasoned Sardine", ItemID.SEASONED_SARDINE, 1);
		seasonedSardine.setTip("Can be created by using a sardine on Doogle leaves(South of Gertrudes House)");
	}

	private void setupZones()
	{
		Zone zone = new Zone(new WorldPoint(3306, 3507, 12), new WorldPoint(3312, 3513, 1));

		isUpstairsLumberyard = new ZoneCondition(zone);
	}

	private void setupConditions()
	{
		hasFluffsKitten = new ItemRequirementCondition(new ItemRequirement("Fluffs' kitten", ItemID.FLUFFS_KITTEN));
	}

	private ObjectStep getClimbLadder()
	{
		return new ObjectStep(this, ObjectID.LADDER_11794,
			new WorldPoint(3310, 3509, 0), "Climb up the ladder in the Lumberyard.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(bucketOfMilk, coins, seasonedSardine));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting out",
			new ArrayList<>(Arrays.asList(talkToGertrude, talkToChildren)),
			coins);
		steps.add(startingPanel);

		PanelDetails lumberYardPanel = new PanelDetails("The secret playground (Lumber Yard)",
			new ArrayList<>(Arrays.asList(gertrudesCat, gertrudesCat2, searchNearbyCrates, giveKittenToFluffy)),
			seasonedSardine, bucketOfMilk);
		steps.add(lumberYardPanel);

		PanelDetails finishQuestPanel = new PanelDetails("Finish the quest",
			new ArrayList<>(Arrays.asList(finishQuest)));
		steps.add(finishQuestPanel);
		return steps;
	}
}
