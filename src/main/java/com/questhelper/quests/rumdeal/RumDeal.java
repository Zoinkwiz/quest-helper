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
package com.questhelper.quests.rumdeal;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RUM_DEAL
)
public class RumDeal extends BasicQuestHelper
{
	//Items Required
	ItemRequirement combatGear, dibber, rake, slayerGloves, blindweedSeed, rakeHighlight, blindweedSeedHighlight, blindweed, blindweedHighlight, bucket, bucketHighlight,
		stagnantWater, stagnantWaterHighlight, netBowl, sluglings5, holyWrench, wrench, spiderCarcass, spiderCarcassHighlight, swill;

	Requirement prayerPoints47;

	Requirement onIsland, onIslandF1, onIslandF2, onIslandF0, rakedPatch, plantedPatch, grownPatch, onNorthIsland, added5Sluglings,
		inSpiderRoom, evilSpiritNearby,carcassNearby;

	DetailedQuestStep talkToPete, talkToBraindeath, goDownstairs, rakePatch, plantSeed, waitForGrowth, pickPlant, goUpStairsWithPlant, talkToBraindeathWithPlant, talkToPeteWithPlant,
		climbUpToDropPlant, dropPlant, goDownFromDropPlant, talkToBraindeathAfterPlant, goDownForWater, openGate, useBucketOnWater, goUpWithWater, goUpToDropWater, dropWater,
		goDownFromTopAfterDropWater, talkToBraindeathAfterWater, goDownFromTop, goUpFromBottom, goDownAfterSlugs, talkToBraindeathAfterSlugs, talkToDavey, useWrenchOnControl,
		killSpirit, goUpFromSpiders, talkToBraindeathAfterSpirit, goDownToSpiders, killSpider, goUpFromSpidersWithCorpse, goUpToDropSpider, dropSpider, goDownAfterSpider,
		talkToBraindeathAfterSpider, useBucketOnTap, goDownToDonnie, talkToDonnie, goUpToBraindeathToFinish, talkToBraindeathToFinish, pickUpCarcass;

	SlugSteps getSlugs;

	//Zones
	Zone island, islandF0, islandF1, islandF2, northIsland, spiderRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToPete);
		steps.put(1, talkToPete);

		ConditionalStep startOff = new ConditionalStep(this, talkToPete);
		startOff.addStep(onIsland, talkToBraindeath);

		steps.put(2, startOff);

		ConditionalStep growBlindweed = new ConditionalStep(this, talkToPete);
		growBlindweed.addStep(grownPatch, pickPlant);
		growBlindweed.addStep(plantedPatch, waitForGrowth);
		growBlindweed.addStep(inSpiderRoom, goUpFromSpiders);
		growBlindweed.addStep(new Conditions(onIslandF0, rakedPatch), plantSeed);
		growBlindweed.addStep(onIslandF2, goDownFromTop);
		growBlindweed.addStep(onIslandF0, rakePatch);
		growBlindweed.addStep(onIslandF1, goDownstairs);

		steps.put(3, growBlindweed);
		steps.put(4, growBlindweed);

		ConditionalStep bringPlant = new ConditionalStep(this, talkToPeteWithPlant);
		bringPlant.addStep(inSpiderRoom, goUpFromSpiders);
		bringPlant.addStep(new Conditions(onIslandF1), talkToBraindeathWithPlant);
		bringPlant.addStep(new Conditions(onIslandF0), goUpStairsWithPlant);
		bringPlant.addStep(onIslandF2, goDownFromTop);

		steps.put(5, bringPlant);

		ConditionalStep addPlant = new ConditionalStep(this, talkToPeteWithPlant);
		addPlant.addStep(inSpiderRoom, goUpFromSpiders);
		addPlant.addStep(onIslandF1, climbUpToDropPlant);
		addPlant.addStep(onIslandF0, goUpFromBottom);
		addPlant.addStep(onIslandF2, dropPlant);

		steps.put(6, addPlant);

		ConditionalStep talkAfterPlant = new ConditionalStep(this, talkToPete);
		talkAfterPlant.addStep(inSpiderRoom, goUpFromSpiders);
		talkAfterPlant.addStep(onIslandF1, talkToBraindeathAfterPlant);
		talkAfterPlant.addStep(onIslandF0, goUpFromBottom);
		talkAfterPlant.addStep(onIslandF2, goDownFromDropPlant);

		steps.put(7, talkAfterPlant);

		ConditionalStep getWater = new ConditionalStep(this, talkToPete);
		getWater.addStep(inSpiderRoom, goUpFromSpiders);
		getWater.addStep(new Conditions(onIslandF2, stagnantWater), dropWater);
		getWater.addStep(new Conditions(onIslandF1, stagnantWater), goUpToDropWater);
		getWater.addStep(new Conditions(onIslandF0, stagnantWater), goUpWithWater);
		getWater.addStep(onNorthIsland, useBucketOnWater);
		getWater.addStep(onIslandF0, openGate);
		getWater.addStep(onIslandF1, goDownForWater);
		getWater.addStep(onIslandF2, goDownFromTop);

		steps.put(8, getWater);

		ConditionalStep putWater = new ConditionalStep(this, talkToPete);
		putWater.addStep(inSpiderRoom, goUpFromSpiders);
		putWater.addStep(onIslandF2, dropWater);
		putWater.addStep(onIslandF1, goUpToDropWater);
		putWater.addStep(onIslandF0, goUpWithWater);

		steps.put(9, putWater);

		ConditionalStep startSlug = new ConditionalStep(this, talkToPete);
		startSlug.addStep(inSpiderRoom, goUpFromSpiders);
		startSlug.addStep(onIslandF2, goDownFromTopAfterDropWater);
		startSlug.addStep(onIslandF1, talkToBraindeathAfterWater);
		startSlug.addStep(onIslandF0, goUpFromBottom);

		steps.put(10, startSlug);

		ConditionalStep getSlugsSteps = new ConditionalStep(this, getSlugs);
		getSlugsSteps.addStep(inSpiderRoom, goUpFromSpiders);

		steps.put(11, getSlugsSteps);

		ConditionalStep startSpirit = new ConditionalStep(this, talkToPete);
		startSpirit.addStep(inSpiderRoom, goUpFromSpiders);
		startSpirit.addStep(onIslandF1, talkToBraindeathAfterSlugs);
		startSpirit.addStep(onIslandF2, goDownAfterSlugs);
		startSpirit.addStep(onIslandF0, goUpFromBottom);

		// 1355 0->1
		steps.put(12, startSpirit);

		ConditionalStep killSpiritSteps = new ConditionalStep(this, talkToPete);
		killSpiritSteps.addStep(inSpiderRoom, goUpFromSpiders);
		killSpiritSteps.addStep(new Conditions(onIslandF1, holyWrench, evilSpiritNearby), killSpirit);
		killSpiritSteps.addStep(new Conditions(onIslandF1, holyWrench), useWrenchOnControl);
		killSpiritSteps.addStep(onIslandF1, talkToDavey);
		killSpiritSteps.addStep(onIslandF2, goDownFromTop);
		killSpiritSteps.addStep(onIslandF0, goUpFromBottom);

		steps.put(13, killSpiritSteps);

		ConditionalStep spiderStepsStart = new ConditionalStep(this, talkToPete);
		spiderStepsStart.addStep(inSpiderRoom, goUpFromSpiders);
		spiderStepsStart.addStep(onIslandF1, talkToBraindeathAfterSpirit);
		spiderStepsStart.addStep(onIslandF2, goDownFromTop);
		spiderStepsStart.addStep(onIslandF0, goUpFromBottom);

		steps.put(14, spiderStepsStart);

		ConditionalStep spiderSteps = new ConditionalStep(this, talkToPete);
		spiderSteps.addStep(new Conditions(onIslandF2, spiderCarcass), dropSpider);
		spiderSteps.addStep(new Conditions(onIslandF1, spiderCarcass), goUpToDropSpider);
		spiderSteps.addStep(new Conditions(inSpiderRoom, spiderCarcass), goUpFromSpidersWithCorpse);
		spiderSteps.addStep(carcassNearby, pickUpCarcass);
		spiderSteps.addStep(inSpiderRoom, killSpider);
		spiderSteps.addStep(onIslandF1, goDownToSpiders);
		spiderSteps.addStep(onIslandF2, goDownFromTop);
		spiderSteps.addStep(onIslandF0, goUpFromBottom);

		steps.put(15, spiderSteps);

		ConditionalStep makeBrewForDonnieStart = new ConditionalStep(this, talkToPete);
		makeBrewForDonnieStart.addStep(inSpiderRoom, goUpFromSpiders);
		makeBrewForDonnieStart.addStep(onIslandF1, talkToBraindeathAfterSpider);
		makeBrewForDonnieStart.addStep(onIslandF2, goDownFromTop);
		makeBrewForDonnieStart.addStep(onIslandF0, goUpFromBottom);

		steps.put(16, makeBrewForDonnieStart);

		ConditionalStep giveBrewToDonnie = new ConditionalStep(this, talkToPete);
		giveBrewToDonnie.addStep(new Conditions(onIslandF0, swill), talkToDonnie);
		giveBrewToDonnie.addStep(new Conditions(onIslandF1, swill), goDownToDonnie);
		giveBrewToDonnie.addStep(inSpiderRoom, goUpFromSpiders);
		giveBrewToDonnie.addStep(onIslandF1, useBucketOnTap);
		giveBrewToDonnie.addStep(onIslandF2, goDownAfterSpider);
		giveBrewToDonnie.addStep(onIslandF0, goUpFromBottom);

		steps.put(17, giveBrewToDonnie);

		ConditionalStep finishQuest = new ConditionalStep(this, talkToPete);
		finishQuest.addStep(inSpiderRoom, goUpFromSpiders);
		finishQuest.addStep(onIslandF1, talkToBraindeathToFinish);
		finishQuest.addStep(onIslandF2, goDownFromTop);
		finishQuest.addStep(onIslandF0, goUpToBraindeathToFinish);

		steps.put(18, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		slayerGloves = new ItemRequirement("Slayer gloves", ItemID.SLAYER_GLOVES);
		slayerGloves.addAlternates(ItemID.SLAYER_GLOVES_6720);
		blindweedSeed = new ItemRequirement("Blindweed seed", ItemID.BLINDWEED_SEED);
		blindweedSeedHighlight = new ItemRequirement("Blindweed seed", ItemID.BLINDWEED_SEED);
		blindweedSeedHighlight.setHighlightInInventory(true);
		rake = new ItemRequirement("Rake", ItemID.RAKE);
		rakeHighlight = new ItemRequirement("Rake", ItemID.RAKE);
		rakeHighlight.setHighlightInInventory(true);
		dibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);
		blindweed = new ItemRequirement("Blindweed", ItemID.BLINDWEED);
		blindweed.setTooltip("You can get another from Captain Braindeath");

		blindweedHighlight = new ItemRequirement("Blindweed", ItemID.BLINDWEED);
		blindweedHighlight.setTooltip("You can get another from Captain Braindeath");

		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);

		bucketHighlight = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketHighlight.setHighlightInInventory(true);

		stagnantWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER_6712);
		stagnantWater.setTooltip("You can get more from Captain Braindeath");

		stagnantWaterHighlight = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER_6712);
		stagnantWaterHighlight.setTooltip("You can get more from Captain Braindeath");
		stagnantWaterHighlight.setHighlightInInventory(true);

		netBowl = new ItemRequirement("Fishbowl and net", ItemID.FISHBOWL_AND_NET);
		netBowl.setTooltip("You can get another from Captain Braindeath, or make it with a fishbowl and large net");

		sluglings5 = new ItemRequirement("Sluglings", ItemID.SLUGLINGS, 5);

		holyWrench = new ItemRequirement("Holy wrench", ItemID.HOLY_WRENCH);
		holyWrench.setHighlightInInventory(true);

		wrench = new ItemRequirement("Wrench", ItemID.WRENCH);
		wrench.setTooltip("You can get another from Captain Braindeath");

		spiderCarcass = new ItemRequirement("Fever spider body", ItemID.FEVER_SPIDER_BODY);

		spiderCarcassHighlight = new ItemRequirement("Fever spider body", ItemID.FEVER_SPIDER_BODY);
		spiderCarcassHighlight.setHighlightInInventory(true);

		swill = new ItemRequirement("Unsanitary swill", ItemID.UNSANITARY_SWILL);

		prayerPoints47 = new ItemRequirement("47 prayer points", -1, -1);
	}

	public void loadZones()
	{
		island = new Zone(new WorldPoint(2110, 5054, 0), new WorldPoint(2178, 5185, 2));
		islandF0 = new Zone(new WorldPoint(2110, 5054, 0), new WorldPoint(2178, 5185, 0));
		islandF1 = new Zone(new WorldPoint(2110, 5054, 1), new WorldPoint(2178, 5185, 1));
		islandF2 = new Zone(new WorldPoint(2110, 5054, 2), new WorldPoint(2178, 5185, 2));
		northIsland = new Zone(new WorldPoint(2110, 5099, 2), new WorldPoint(2178, 5185, 0));
		spiderRoom = new Zone(new WorldPoint(2138, 5091, 0), new WorldPoint(2164, 5106, 0));
	}

	public void setupConditions()
	{
		onIsland = new ZoneRequirement(island);
		onIslandF0 = new ZoneRequirement(islandF0);
		onIslandF1 = new ZoneRequirement(islandF1);
		onIslandF2 = new ZoneRequirement(islandF2);
		onNorthIsland = new ZoneRequirement(northIsland);
		inSpiderRoom = new ZoneRequirement(spiderRoom);

		rakedPatch = new VarbitRequirement(1366, 3);
		plantedPatch = new VarbitRequirement(1366, 4);
		grownPatch = new VarbitRequirement(1366, 5);

		added5Sluglings = new VarbitRequirement(1354, 5);

		evilSpiritNearby = new NpcCondition(NpcID.EVIL_SPIRIT);

		carcassNearby = new ItemOnTileRequirement(spiderCarcass);
		// 1359-64 0->1 given swill
	}

	public void setupSteps()
	{
		talkToPete = new NpcStep(this, NpcID.PIRATE_PETE, new WorldPoint(3680, 3537, 0), "Talk to Pirate Pete north east of the Ectofuntus.");
		talkToPete.addDialogSteps("Yes!", "Of course, I fear no demon!", "Nonsense! Keep the money!", "I've decided to help you for free.", "Okay!");
		talkToBraindeath = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.");

		goDownFromTop = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Go down the ladder.");
		goUpFromBottom = new ObjectStep(this, ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0), "Go up to the first floor.");

		goDownstairs = new ObjectStep(this, ObjectID.WOODEN_STAIR_10137, new WorldPoint(2150, 5088, 1), "Go down to the island's farming patch to plant the blindweed seed.", blindweedSeed, rake, dibber);
		rakePatch = new ObjectStep(this, NullObjectID.NULL_10096, new WorldPoint(2163, 5070, 0), "Rake the blindweed patch.", rakeHighlight);
		rakePatch.addIcon(ItemID.RAKE);

		plantSeed = new ObjectStep(this, NullObjectID.NULL_10096, new WorldPoint(2163, 5070, 0), "Plant the seed in the blindweed patch.", blindweedSeedHighlight, dibber);
		plantSeed.addIcon(ItemID.BLINDWEED_SEED);

		waitForGrowth = new DetailedQuestStep(this, "Wait 5 minutes for the blindweed to grow.");

		pickPlant = new ObjectStep(this, NullObjectID.NULL_10096, new WorldPoint(2163, 5070, 0), "Pick the blindweed on Braindeath Island.");

		goUpStairsWithPlant = new ObjectStep(this, ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0), "Take the blindweed back to Captain Braindeath.", blindweed);

		talkToBraindeathWithPlant = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.", blindweed);
		talkToPeteWithPlant = new NpcStep(this, NpcID.PIRATE_PETE, new WorldPoint(3680, 3537, 0), "Talk to Pirate Pete north east of the Ectofuntus.", blindweed);
		goUpStairsWithPlant.addSubSteps(talkToPeteWithPlant, talkToBraindeathWithPlant);

		climbUpToDropPlant = new ObjectStep(this, ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1),
			"Go to the top floor and put the blindweed into the hopper.", blindweed);

		dropPlant = new ObjectStep(this, ObjectID.HOPPER_10170, new WorldPoint(2142, 5102, 2),
			"Go to the top floor and put the blindweed into the hopper.", blindweedHighlight);
		dropPlant.addSubSteps(climbUpToDropPlant);
		dropPlant.addIcon(ItemID.BLINDWEED);

		goDownFromDropPlant = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Return to Captain Braindeath.");

		talkToBraindeathAfterPlant = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.");
		talkToBraindeathAfterPlant.addSubSteps(goDownFromDropPlant);

		goDownForWater = new ObjectStep(this, ObjectID.WOODEN_STAIR_10137, new WorldPoint(2138, 5088, 1), "Go to the north part of the island and get some stagnant water.", bucket);
		openGate = new ObjectStep(this, ObjectID.GATE_10172, new WorldPoint(2120, 5098, 0), "Go to the north part of the island and get some stagnant water.", bucket);

		useBucketOnWater = new ObjectStep(this, ObjectID.STAGNANT_LAKE, new WorldPoint(2135, 5161, 0), "Go to the north part of the island and get some stagnant water.", bucketHighlight);
		useBucketOnWater.addSubSteps(goDownForWater, openGate);
		useBucketOnWater.addIcon(ItemID.BUCKET);

		goUpWithWater = new ObjectStep(this, ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0), "Take the water back to the hopper on the top floor.", stagnantWater);

		goUpToDropWater = new ObjectStep(this, ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1),
			"Take the water back to the hopper on the top floor.", stagnantWater);
		goUpToDropWater.addDialogStep("What exactly do you want me to do?");

		dropWater = new ObjectStep(this, ObjectID.HOPPER_10170, new WorldPoint(2142, 5102, 2),
			"Take the water back to the hopper on the top floor.", stagnantWaterHighlight);
		dropWater.addSubSteps(goUpWithWater, goUpToDropWater);
		dropWater.addIcon(ItemID.BUCKET_OF_WATER_6712);

		goDownFromTopAfterDropWater = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Return to Captain Braindeath.");

		talkToBraindeathAfterWater = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.");
		talkToBraindeathAfterWater.addSubSteps(goDownFromTopAfterDropWater);

		getSlugs = new SlugSteps(this);

		goDownAfterSlugs = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Return to Captain Braindeath.");
		talkToBraindeathAfterSlugs = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.");
		talkToBraindeathAfterSlugs.addSubSteps(goDownAfterSlugs);
		talkToDavey = new NpcStep(this, NpcID.DAVEY, new WorldPoint(2132, 5100, 1), "Talk to Davey south west of Captain Braindeath.", wrench, prayerPoints47);
		useWrenchOnControl = new ObjectStep(this, NullObjectID.NULL_10104, new WorldPoint(2144, 5101, 1), "Use the holy wrench on the brewing control. Be prepared to fight an evil spirit.", holyWrench);
		useWrenchOnControl.addIcon(ItemID.HOLY_WRENCH);
		killSpirit = new NpcStep(this, NpcID.EVIL_SPIRIT, "Kill the Evil Spirit.", prayerPoints47);

		goUpFromSpiders = new ObjectStep(this, ObjectID.LADDER_10167, new WorldPoint(2139, 5105, 0), "Go up the ladder.");

		talkToBraindeathAfterSpirit = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.");
		goDownToSpiders = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2139, 5105, 1), "Go into the brewery's basement and kill a fever spider. If you're not wearing slayer gloves they'll afflict you with disease.", slayerGloves);

		killSpider = new NpcStep(this, NpcID.FEVER_SPIDER, "Go into the brewery's basement and kill a fever spider. If you're not wearing slayer gloves they'll afflict you with disease.", slayerGloves.equipped());
		pickUpCarcass = new ItemStep(this, "Pick up the fever spider body.", spiderCarcass);
		goUpFromSpidersWithCorpse = new ObjectStep(this, ObjectID.LADDER_10167, new WorldPoint(2139, 5105, 0), "Add the spider body to the hopper on the top floor.", spiderCarcass);
		goUpToDropSpider = new ObjectStep(this, ObjectID.LADDER_10167, new WorldPoint(2163, 5092, 1), "Add the spider body to the hopper on the top floor.", spiderCarcass);
		dropSpider = new ObjectStep(this, ObjectID.HOPPER_10170, new WorldPoint(2142, 5102, 2), "Add the spider body to the hopper on the top floor.", spiderCarcassHighlight);
		dropSpider.addIcon(ItemID.FEVER_SPIDER_BODY);
		dropSpider.addSubSteps(goUpFromSpidersWithCorpse, goUpToDropSpider);

		goDownAfterSpider = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2163, 5092, 2), "Return to Captain Braindeath.");
		talkToBraindeathAfterSpider = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath.");
		talkToBraindeathAfterSpider.addSubSteps(goDownAfterSpider);

		useBucketOnTap = new ObjectStep(this, ObjectID.OUTPUT_TAP, new WorldPoint(2142, 5093, 1), "Fill a bucket from the output tap in the south west of the brewery.", bucket);

		goDownToDonnie = new ObjectStep(this, ObjectID.WOODEN_STAIR_10137, new WorldPoint(2150, 5088, 1), "Bring the unsanitary swill to Captain Donnie south of the Brewery.", swill);
		talkToDonnie = new NpcStep(this, NpcID.CAPTAIN_DONNIE, new WorldPoint(2152, 5078, 0), "Bring the unsanitary swill to Captain Donnie south of the Brewery.", swill);
		talkToDonnie.addSubSteps(goDownToDonnie);

		goUpToBraindeathToFinish = new ObjectStep(this, ObjectID.WOODEN_STAIR, new WorldPoint(2150, 5088, 0), "Return to Captain Braindeath to finish.");
		talkToBraindeathToFinish = new NpcStep(this, NpcID.CAPTAIN_BRAINDEATH, new WorldPoint(2145, 5108, 1), "Talk to Captain Braindeath to finish.");
		talkToBraindeathToFinish.addSubSteps(goUpToBraindeathToFinish);

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, dibber, rake, slayerGloves);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Evil spirit (level 150)", "Fever spider (level 49)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.FISHING, 7000),
				new ExperienceReward(Skill.PRAYER, 7000),
				new ExperienceReward(Skill.FARMING, 7000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("A Holy Wrench", ItemID.HOLY_WRENCH, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToPete, talkToBraindeath), rake, dibber, slayerGloves, combatGear));
		allSteps.add(new PanelDetails("Get blindweed", Arrays.asList(goDownstairs, rakePatch, plantSeed, waitForGrowth, pickPlant, goUpStairsWithPlant, dropPlant), rake, dibber));
		allSteps.add(new PanelDetails("Get stagnant water", Arrays.asList(talkToBraindeathAfterPlant, useBucketOnWater, dropWater)));

		List<QuestStep> sluglingSteps = QuestUtil.toArrayList(talkToBraindeathAfterWater);
		sluglingSteps.addAll(getSlugs.getDisplaySteps());
		allSteps.add(new PanelDetails("Get sluglings", sluglingSteps));

		allSteps.add(new PanelDetails("Kill evil spirit", Arrays.asList(talkToBraindeathAfterSlugs, talkToDavey, useWrenchOnControl, killSpirit), combatGear));
		allSteps.add(new PanelDetails("Get spider carcass", Arrays.asList(talkToBraindeathAfterSpirit, killSpider, pickUpCarcass, dropSpider), combatGear, slayerGloves));
		allSteps.add(new PanelDetails("Giving swill to Donnie", Arrays.asList(talkToBraindeathAfterSpider, useBucketOnTap, talkToDonnie, talkToBraindeathToFinish)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new FreeInventorySlotRequirement(InventoryID.INVENTORY, 6));
		return req;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.ZOGRE_FLESH_EATERS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 42, true));
		req.add(new SkillRequirement(Skill.FISHING, 50, true));
		req.add(new SkillRequirement(Skill.FARMING, 40, true));
		req.add(new SkillRequirement(Skill.PRAYER, 47, true));
		req.add(new SkillRequirement(Skill.SLAYER, 42));
		return req;
	}
}
