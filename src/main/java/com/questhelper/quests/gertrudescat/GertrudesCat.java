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
package com.questhelper.quests.gertrudescat;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.GERTRUDES_CAT
)
public class GertrudesCat extends BasicQuestHelper
{
	//Items Required
	ItemRequirement bucketOfMilk, coins, seasonedSardine, sardine, doogleLeaves, milkHighlighted,
		seasonedSardineHighlighted, kittenHighlighted;

	ItemRequirement lumberyardTeleport, varrockTeleport;

	QuestStep talkToGertrude, talkToChildren, gertrudesCat, gertrudesCat2, searchNearbyCrates,
		giveKittenToFluffy, finishQuest;

	QuestStep pickupDoogle, makeSeasonedSardine;

	ConditionalStep giveMilkToCatSteps, giveSardineToCat;

	Requirement isUpstairsLumberyard, hasFluffsKitten;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();

		return getSteps();
	}

	private Map<Integer, QuestStep> getSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGertrude = getTalkToGertrude());

		talkToChildren = getTalkToChildren();

		ConditionalStep conditionalTalkToChildren = new ConditionalStep(this, pickupDoogle);
		conditionalTalkToChildren.addStep(new ItemRequirements(LogicType.AND, "", sardine, doogleLeaves), makeSeasonedSardine);
		conditionalTalkToChildren.addStep(seasonedSardine, talkToChildren);
		steps.put(1, conditionalTalkToChildren);

		steps.put(2, giveMilkToCatSteps = getGiveMilkToCat());
		steps.put(3, giveSardineToCat = getFeedCat());
		steps.put(4, findFluffsKitten());
		steps.put(5, finishQuest = returnToGertrude());
		return steps;
	}

	private NpcStep returnToGertrude()
	{
		return new NpcStep(this, NpcID.GERTRUDE,
			new WorldPoint(3148, 3413, 0), "Return to Gertrude to complete the quest.");
	}

	private QuestStep findFluffsKitten()
	{
		//Need to find to ways to hide arrow
		searchNearbyCrates = new NpcStep(this, NpcID.CRATE, new WorldPoint(3306, 3505, 0),
			"Search for a kitten in the crates in the Lumberyard.", true);
		((NpcStep)(searchNearbyCrates)).setHideWorldArrow(true);
		ObjectStep climbDownLadderStep = goDownLadderStep();
		ObjectStep climbUpLadderStep = getClimbLadder();
		ArrayList<ItemRequirement> fluffsKittenRequirement = new ArrayList<>();
		fluffsKittenRequirement.add(new ItemRequirement("Fluffs' Kitten", ItemID.FLUFFS_KITTEN));
		climbUpLadderStep.addItemRequirements(fluffsKittenRequirement);
		Conditions hasFluffsKittenUpstairs = new Conditions(hasFluffsKitten, isUpstairsLumberyard);

		kittenHighlighted = new ItemRequirement("Fluffs' Kitten", ItemID.FLUFFS_KITTEN);
		kittenHighlighted.setHighlightInInventory(true);

		giveKittenToFluffy = getGertrudesCat(kittenHighlighted);
		giveKittenToFluffy.setText("Return the kitten to Gertrude's cat.");
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
		return new ObjectStep(this, ObjectID.LADDER_11795, new WorldPoint(3310, 3509, 1),
			"Climb down ladder in the Lumberyard.");
	}

	private ConditionalStep getFeedCat()
	{
		gertrudesCat2 = getGertrudesCat(seasonedSardineHighlighted);
		gertrudesCat2.addIcon(ItemID.SEASONED_SARDINE);

		ObjectStep climbLadder = new ObjectStep(this, ObjectID.LADDER_11794,
			new WorldPoint(3310, 3509, 0), "Climb up the ladder in the Lumberyard.", seasonedSardine);

		ConditionalStep lumberyard = new ConditionalStep(this, climbLadder, "Use a seasoned sardine on Gertrude's cat upstairs in the Lumberyard north east of Varrock.");
		lumberyard.addStep(isUpstairsLumberyard, gertrudesCat2);
		gertrudesCat2.addSubSteps(climbLadder);

		return lumberyard;
	}

	private ConditionalStep getGiveMilkToCat()
	{
		gertrudesCat = getGertrudesCat(milkHighlighted);
		gertrudesCat.addIcon(ItemID.BUCKET_OF_MILK);

		ObjectStep climbLadder = getClimbLadder(bucketOfMilk);

		ConditionalStep giveMilkToCat = new ConditionalStep(this, climbLadder, "Use a bucket of milk on Gertrude's cat upstairs in the Lumberyard north east of Varrock.", seasonedSardine);
		giveMilkToCat.addStep(isUpstairsLumberyard, gertrudesCat);

		return giveMilkToCat;
	}

	private NpcStep getGertrudesCat(ItemRequirement... requirement)
	{
		return new NpcStep(this, NpcID.GERTRUDES_CAT_3497,
			new WorldPoint(3308, 3511, 1), "", requirement);
	}

	private QuestStep getTalkToChildren()
	{
		pickupDoogle = new DetailedQuestStep(this, "Pickup some Doogle Leaves south of Gertrude's house.", new ItemRequirement("Doogle Leaves", ItemID.DOOGLE_LEAVES), sardine);
		makeSeasonedSardine = new DetailedQuestStep(this, "Use your Doogle Leaves on  the Sardine.", sardine, doogleLeaves);

		NpcStep talkToChildren = new NpcStep(this, NpcID.SHILOP,
			new WorldPoint(3222, 3435, 0), "Talk to Shilop or Wilough in the Varrock Square.", true,
			seasonedSardine, coins);
		talkToChildren.addAlternateNpcs(NpcID.WILOUGH);
		talkToChildren.addDialogSteps("What will make you tell me?", "Okay then, I'll pay.");

		return talkToChildren;
	}

	private QuestStep getTalkToGertrude()
	{
		NpcStep talkToGertrude = new NpcStep(this, NpcID.GERTRUDE,
			new WorldPoint(3148, 3413, 0), "Talk to Gertrude.");
		talkToGertrude.addDialogStep("Yes.");
		return talkToGertrude;
	}

	@Override
	public void setupRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milkHighlighted = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milkHighlighted.setHighlightInInventory(true);

		coins = new ItemRequirement("Coins", ItemCollections.COINS, 100);

		seasonedSardine = new ItemRequirement("Seasoned Sardine", ItemID.SEASONED_SARDINE);
		seasonedSardine.setTooltip("Can be created by using a sardine on Doogle leaves(South of Gertrudes House)");

		seasonedSardineHighlighted = new ItemRequirement("Seasoned Sardine", ItemID.SEASONED_SARDINE);
		seasonedSardineHighlighted.setTooltip("Can be created by using a sardine on Doogle leaves(South of Gertrudes House)");
		seasonedSardineHighlighted.setHighlightInInventory(true);

		sardine = new ItemRequirement("Raw Sardine", ItemID.RAW_SARDINE);
		sardine.setHighlightInInventory(true);
		doogleLeaves = new ItemRequirement("Doogle Leaves", ItemID.DOOGLE_LEAVES);
		doogleLeaves.setHighlightInInventory(true);

		// Recommended items
		lumberyardTeleport = new ItemRequirement("Lumberyard teleport", ItemID.LUMBERYARD_TELEPORT);
		varrockTeleport = new ItemRequirement("Varrock teleports", ItemID.VARROCK_TELEPORT, 2);
	}

	private void setupZones()
	{
		Zone zone = new Zone(new WorldPoint(3306, 3507, 12), new WorldPoint(3312, 3513, 1));

		isUpstairsLumberyard = new ZoneRequirement(zone);
	}

	private void setupConditions()
	{
		hasFluffsKitten = new ItemRequirements(new ItemRequirement("Fluffs' kitten", ItemID.FLUFFS_KITTEN));
	}

	private ObjectStep getClimbLadder(ItemRequirement... itemRequirements)
	{
		return new ObjectStep(this, ObjectID.LADDER_11794,
			new WorldPoint(3310, 3509, 0), "Climb up the ladder in the Lumberyard.", itemRequirements);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(bucketOfMilk, coins, sardine);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(varrockTeleport, lumberyardTeleport);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.COOKING, 1525));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("A pet Kitten", ItemID.PET_KITTEN, 1),
				new ItemReward("Chocolate Cake", ItemID.CHOCOLATE_CAKE, 1),
				new ItemReward("Stew", ItemID.STEW, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to raise kittens."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting out",
			Arrays.asList(talkToGertrude, pickupDoogle, makeSeasonedSardine, talkToChildren),
			sardine, coins);
		steps.add(startingPanel);

		PanelDetails lumberYardPanel = new PanelDetails("The secret playground (Lumber Yard)",
			Arrays.asList(giveMilkToCatSteps, giveSardineToCat, searchNearbyCrates, giveKittenToFluffy),
			seasonedSardine, bucketOfMilk);
		steps.add(lumberYardPanel);

		PanelDetails finishQuestPanel = new PanelDetails("Finish the quest",
			Collections.singletonList(finishQuest));
		steps.add(finishQuestPanel);
		return steps;
	}
}
