/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
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
package com.questhelper.helpers.quests.cooksassistant;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.WidgetHighlight;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class CooksAssistant extends BasicQuestHelper
{
	// Required items
	ItemRequirement egg;
	ItemRequirement milk;
	ItemRequirement flour;
	ItemRequirement bucket;
	ItemRequirement pot;
	ItemRequirement coins;
	ItemRequirement grain;

	// Zones
	Zone millSecond;
	Zone millThird;

	// Miscellaneous requirements
	VarbitRequirement controlsUsed;

	ZoneRequirement inMillSecond;
	ZoneRequirement inMillThird;

	// Steps
	NpcStep getBucket;
	NpcStep getPot;
	ObjectStep milkCow;
	ItemStep getEgg;
	ObjectStep getWheat;
	ObjectStep climbLadderOne;
	ObjectStep fillHopper;
	ObjectStep operateControls;
	ObjectStep climbLadderThree;
	ObjectStep collectFlour;
	ObjectStep climbLadderTwoUp;
	ObjectStep climbLadderTwoDown;
	NpcStep finishQuest;

	@Override
	protected void setupZones()
	{
		millSecond = new Zone(new WorldPoint(3162, 3311, 1), new WorldPoint(3171, 3302, 1));
		millThird = new Zone(new WorldPoint(3162, 3311, 2), new WorldPoint(3171, 3302, 2));
	}

	@Override
	protected void setupRequirements()
	{
		egg = new ItemRequirement("Egg", ItemID.EGG);
		egg.canBeObtainedDuringQuest();
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK);
		milk.canBeObtainedDuringQuest();
		flour = new ItemRequirement("Pot of flour", ItemID.POT_FLOUR);
		flour.canBeObtainedDuringQuest();
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		pot = new ItemRequirement("Pot", ItemID.POT_EMPTY);
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		coins.setTooltip("Necessary if you do not have a pot / bucket");
		grain = new ItemRequirement("Grain", ItemID.GRAIN);

		controlsUsed = new VarbitRequirement(VarbitID.MILL_FLOUR, 1);

		inMillSecond = new ZoneRequirement(millSecond);
		inMillThird = new ZoneRequirement(millThird);
	}

	public void setupSteps()
	{
		getEgg = new ItemStep(this, new WorldPoint(3177, 3296, 0),
			"Grab an egg from the farm north of Lumbridge.", egg);
		getBucket = new NpcStep(this, NpcID.GENERALSHOPKEEPER1, new WorldPoint(3212, 3246, 0),
			"Purchase a bucket from the Lumbridge General Store.", coins.quantity(3));
		getBucket.addWidgetHighlight(WidgetHighlight.createShopItemHighlight(ItemID.BUCKET_EMPTY));
		getBucket.addAlternateNpcs(NpcID.GENERALASSISTANT1);
		getPot = new NpcStep(this, NpcID.GENERALSHOPKEEPER1, new WorldPoint(3212, 3246, 0),
			"Purchase a pot from the Lumbridge General Store.", coins.quantity(3));
		// TODO: highlight a pot to purchase
		getPot.addAlternateNpcs(NpcID.GENERALASSISTANT1);
		milkCow = new ObjectStep(this, ObjectID.FAT_COW, new WorldPoint(3254, 3272, 0),
			"Milk the cow north-east of Lumbridge.", bucket);
		getWheat = new ObjectStep(this, ObjectID.FAI_VARROCK_WHEAT_CORNER, new WorldPoint(3161, 3292, 0),
			"Pick some wheat north of Lumbridge.");
		climbLadderOne = new ObjectStep(this, ObjectID.QIP_COOK_LADDER, new WorldPoint(3164, 3307, 0),
			"Climb up the ladder in the Mill north of Lumbridge to the top floor.", pot, grain);
		climbLadderTwoUp = new ObjectStep(this, ObjectID.QIP_COOK_LADDER_MIDDLE, new WorldPoint(3164, 3307, 1),
			"Climb up the ladder in the Mill north of Lumbridge to the top floor.", pot, grain);
		climbLadderTwoUp.addDialogStep("Climb Up.");
		climbLadderOne.addSubSteps(climbLadderTwoUp);
		fillHopper = new ObjectStep(this, ObjectID.HOPPER1, new WorldPoint(3166, 3307, 2),
			"Fill the hopper with your grain.", pot, grain.highlighted());
		fillHopper.addIcon(ItemID.GRAIN);
		operateControls = new ObjectStep(this, ObjectID.HOPPERLEVERS1, new WorldPoint(3166, 3305, 2),
			"Operate the hopper controls.", pot);
		climbLadderThree = new ObjectStep(this, ObjectID.QIP_COOK_LADDER_TOP, new WorldPoint(3164, 3307, 2),
			"Climb down the ladder in the Mill to the ground floor.", pot);
		climbLadderTwoDown = new ObjectStep(this, ObjectID.QIP_COOK_LADDER_MIDDLE, new WorldPoint(3164, 3307, 1),
			"Climb down the ladder in the Mill to the ground floor.", pot);
		climbLadderTwoDown.addDialogStep("Climb Down.");
		climbLadderThree.addSubSteps(climbLadderTwoDown);
		collectFlour = new ObjectStep(this, ObjectID.MILLBASE_FLOUR, new WorldPoint(3166, 3306, 0),
			"Collect the flour in the bin.", pot.highlighted());
		collectFlour.addIcon(ItemID.POT_EMPTY);
		finishQuest = new NpcStep(this, NpcID.POH_SERVANT_COOK_WOMAN, new WorldPoint(3206, 3214, 0),
			"Give the Cook in Lumbridge Castle's kitchen the required items to finish the quest.",
			egg, milk, flour);
		finishQuest.addDialogSteps("What's wrong?", "Can I help?", "Yes.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var getFlour = new ConditionalStep(this, getPot);
		getFlour.addStep(and(pot, controlsUsed, inMillThird), climbLadderThree);
		getFlour.addStep(and(pot, controlsUsed, inMillSecond), climbLadderTwoDown);
		getFlour.addStep(and(pot, controlsUsed), collectFlour);
		getFlour.addStep(and(pot, grain, inMillThird), fillHopper);
		getFlour.addStep(and(pot, inMillThird), operateControls);
		getFlour.addStep(and(pot, grain, inMillSecond), climbLadderTwoUp);
		getFlour.addStep(and(pot, grain), climbLadderOne);
		getFlour.addStep(and(pot), getWheat);

		var doQuest = new ConditionalStep(this, finishQuest);
		doQuest.addStep(and(not(milk), not(bucket)), getBucket);
		doQuest.addStep(and(not(flour), not(pot)), getPot);
		doQuest.addStep(not(egg), getEgg);
		doQuest.addStep(not(flour), getFlour);
		doQuest.addStep(not(milk), milkCow);

		steps.put(0, doQuest);
		steps.put(1, doQuest);

		return steps;
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			egg,
			flour,
			milk
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			coins
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
			new ExperienceReward(Skill.COOKING, 300)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Permission to use The Cook's range.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var steps = new ArrayList<PanelDetails>();

		steps.add(new PanelDetails("Starting off", List.of(
			getBucket,
			getPot
		), List.of(
			coins.quantity(3)
		)));

		steps.add(new PanelDetails("Getting the Egg", List.of(
			getEgg
		)));

		steps.add(new PanelDetails("Getting the Flour", List.of(
			getWheat,
			climbLadderOne,
			fillHopper,
			operateControls,
			climbLadderThree,
			collectFlour
		), List.of(
			pot
		)));

		steps.add(new PanelDetails("Getting the Milk", List.of(
			milkCow
		), List.of(
			bucket
		)));

		steps.add(new PanelDetails("Finishing up", List.of(
			finishQuest
		), List.of(
			egg,
			flour,
			milk
		)));

		return steps;
	}
}
