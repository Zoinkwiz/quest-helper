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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.quests.gertrudescat;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class GertrudesCat extends BasicQuestHelper
{
	// Required items
	ItemRequirement bucketOfMilk;
	ItemRequirement coins;
	ItemRequirement sardineHighlighted;

	// Miscellaneous requirements
	ItemRequirement seasonedSardine;
	ItemRequirement doogleLeaves;
	ItemRequirement doogleLeavesHighlighted;
	ItemRequirement bucketOfMilkHighlighted;
	ItemRequirement seasonedSardineHighlighted;
	ItemRequirement kitten;
	ItemRequirement kittenHighlighted;

	ZoneRequirement isUpstairsLumberyard;

	QuestRequirement hasGivenFluffsMilkAndSardine;

	ItemRequirement lumberyardTeleport;
	ItemRequirement varrockTeleport;

	// Steps
	NpcStep talkToGertrude;

	DetailedQuestStep pickupDoogle;
	DetailedQuestStep makeSeasonedSardine;
	NpcStep talkToChildren;

	ObjectStep climbLadder;
	QuestStep gertrudesCat;
	ConditionalStep cGiveMilkToCat;

	ObjectStep climbLadder2;
	NpcStep gertrudesCat2;
	ConditionalStep cGiveSardineToCat;

	ObjectStep climbDownLadderStep;
	ObjectStep climbUpLadderStep;
	NpcStep searchNearbyCrates;
	NpcStep giveKittenToFluffy;

	NpcStep finishQuest;

	@Override
	protected void setupZones()
	{
		Zone zone = new Zone(new WorldPoint(3306, 3507, 12), new WorldPoint(3312, 3513, 1));

		isUpstairsLumberyard = new ZoneRequirement(zone);
	}

	@Override
	protected void setupRequirements()
	{
		hasGivenFluffsMilkAndSardine = new QuestRequirement(QuestHelperQuest.GERTRUDES_CAT, 4);

		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK).hideConditioned(hasGivenFluffsMilkAndSardine);
		bucketOfMilkHighlighted = bucketOfMilk.highlighted();

		coins = new ItemRequirement("Coins", ItemCollections.COINS, 100);

		sardineHighlighted = new ItemRequirement("Raw Sardine", ItemID.RAW_SARDINE).highlighted().hideConditioned(hasGivenFluffsMilkAndSardine);

		seasonedSardine = new ItemRequirement("Seasoned Sardine", ItemID.SEASONED_SARDINE).hideConditioned(hasGivenFluffsMilkAndSardine);
		seasonedSardine.setTooltip("Can be created by using a sardine on Doogle leaves(South of Gertrudes House)");
		seasonedSardineHighlighted = seasonedSardine.highlighted();

		doogleLeaves = new ItemRequirement("Doogle Leaves", ItemID.DOOGLELEAVES);
		doogleLeavesHighlighted = doogleLeaves.highlighted();

		kitten = new ItemRequirement("Fluffs' Kitten", ItemID.GERTRUDEKITTENS);
		kittenHighlighted = kitten.highlighted();

		// Teleport recommendations
		lumberyardTeleport = new ItemRequirement("Lumberyard teleport", ItemID.TELEPORTSCROLL_LUMBERYARD);
		varrockTeleport = new ItemRequirement("Varrock teleports", ItemID.POH_TABLET_VARROCKTELEPORT, 2);
	}

	public void setupSteps()
	{
		talkToGertrude = new NpcStep(this, NpcID.GERTRUDE_QUEST, new WorldPoint(3148, 3413, 0), "Talk to Gertrude west of Varrock to start the quest.");
		talkToGertrude.addDialogStep("Yes.");

		pickupDoogle = new DetailedQuestStep(this, "Pickup some Doogle Leaves south of Gertrude's house.", doogleLeaves, sardineHighlighted);
		makeSeasonedSardine = new DetailedQuestStep(this, "Use your Doogle Leaves on  the Sardine.", doogleLeavesHighlighted, sardineHighlighted);

		talkToChildren = new NpcStep(this, NpcID.SHILOP, new WorldPoint(3222, 3435, 0), "Talk to Shilop or Wilough in the Varrock Square.", true, seasonedSardine, coins);
		talkToChildren.addAlternateNpcs(NpcID.WILOUGH);
		talkToChildren.addDialogSteps("What will make you tell me?", "Okay then, I'll pay.");

		gertrudesCat = new NpcStep(this, NpcID.GERTRUDESCAT, new WorldPoint(3308, 3511, 1), "", bucketOfMilkHighlighted);
		gertrudesCat.addIcon(ItemID.BUCKET_MILK);

		climbLadder = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER, new WorldPoint(3310, 3509, 0), "Climb up the ladder in the Lumberyard.", bucketOfMilk);

		cGiveMilkToCat = new ConditionalStep(this, climbLadder, "Use a bucket of milk on Gertrude's cat upstairs in the Lumberyard north east of Varrock.", seasonedSardine);
		cGiveMilkToCat.addStep(isUpstairsLumberyard, gertrudesCat);

		gertrudesCat2 = new NpcStep(this, NpcID.GERTRUDESCAT, new WorldPoint(3308, 3511, 1), "", seasonedSardineHighlighted);
		gertrudesCat2.addIcon(ItemID.SEASONED_SARDINE);

		climbLadder2 = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER,
			new WorldPoint(3310, 3509, 0), "Climb up the ladder in the Lumberyard.", seasonedSardine);

		cGiveSardineToCat = new ConditionalStep(this, climbLadder2, "Use a seasoned sardine on Gertrude's cat upstairs in the Lumberyard north east of Varrock.");
		cGiveSardineToCat.addStep(isUpstairsLumberyard, gertrudesCat2);

		//Need to find to ways to hide arrow
		searchNearbyCrates = new NpcStep(this, NpcID.KITTENS_MEW, new WorldPoint(3306, 3505, 0),
			"Search different crates downstairs in the Lumberyard until you find the kitten.", true, bucketOfMilk);
		searchNearbyCrates.setHideWorldArrow(true);
		climbDownLadderStep = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDERTOP, new WorldPoint(3310, 3509, 1), "Climb down ladder in the Lumberyard.");
		climbUpLadderStep = new ObjectStep(this, ObjectID.FAI_VARROCK_LADDER, new WorldPoint(3310, 3509, 0), "Climb up the ladder in the Lumberyard.", kitten);


		giveKittenToFluffy = new NpcStep(this, NpcID.GERTRUDESCAT,
			new WorldPoint(3308, 3511, 1), "", kittenHighlighted);
		giveKittenToFluffy.setText("Return the kitten to Gertrude's cat.");
		giveKittenToFluffy.addIcon(ItemID.GERTRUDEKITTENS);


		searchNearbyCrates.addSubSteps(climbDownLadderStep);
		giveKittenToFluffy.addSubSteps(climbUpLadderStep);

		finishQuest = new NpcStep(this, NpcID.GERTRUDE_QUEST,
			new WorldPoint(3148, 3413, 0), "Return to Gertrude east of Varrock to complete the quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToGertrude);

		var cTalkToChildren = new ConditionalStep(this, pickupDoogle);
		cTalkToChildren.addStep(and(sardineHighlighted, doogleLeaves), makeSeasonedSardine);
		cTalkToChildren.addStep(seasonedSardine, talkToChildren);
		steps.put(1, cTalkToChildren);

		steps.put(2, cGiveMilkToCat);

		steps.put(3, cGiveSardineToCat);

		var cFindFluffsKitten = new ConditionalStep(this, searchNearbyCrates);
		cFindFluffsKitten.addStep(and(kitten, isUpstairsLumberyard), giveKittenToFluffy);
		cFindFluffsKitten.addStep(kitten, climbUpLadderStep);
		cFindFluffsKitten.addStep(isUpstairsLumberyard, climbDownLadderStep);
		steps.put(4, cFindFluffsKitten);

		steps.put(5, finishQuest);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			bucketOfMilk,
			coins,
			sardineHighlighted
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			lumberyardTeleport
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.COOKING, 1525)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("A pet Kitten", ItemID.KITTENOBJECT, 1),
			new ItemReward("Chocolate Cake", ItemID.CHOCOLATE_CAKE, 1),
			new ItemReward("Stew", ItemID.STEW, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to raise kittens.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting out", List.of(
			talkToGertrude,
			pickupDoogle,
			makeSeasonedSardine,
			talkToChildren
		), List.of(
			sardineHighlighted,
			coins
		)));

		sections.add(new PanelDetails("The secret playground (Lumber Yard)", List.of(
			cGiveMilkToCat,
			cGiveSardineToCat,
			searchNearbyCrates,
			giveKittenToFluffy
		), List.of(
			seasonedSardine,
			bucketOfMilk
		)));

		sections.add(new PanelDetails("Finish the quest", List.of(
			finishQuest
		)));

		return sections;
	}
}
