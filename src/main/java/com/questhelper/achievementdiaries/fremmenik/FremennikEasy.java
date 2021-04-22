/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.achievementdiaries.fremmenik;

import com.questhelper.*;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

@QuestDescriptor(
	quest = QuestHelperQuest.FREMENNIK_EASY
)
public class FremennikEasy extends BasicQuestHelper
{
	// Items required
	ItemRequirement birdSnare, coins, tiaraMould, pickaxe, bucket, climbingBoots, axe, tinderbox, combatGear;

	// Items recommended
	ItemRequirement food;

	Requirement notChangedBoots, notCaughtCeruleanTwitch, notKilledRockCrabs, notGoneToRellekka, notCraftedTiara,
			notCollectedSnapeGrass, notStolenFromStall, notFilledBucketWithWater,
			notEnteredTrollStronghold, notChoppedAndBurnedLogs, notInWaterBirth, notTradedStonemason, notMinedSilver, notInKeldagrim;

	QuestStep catchCeruleanTwitch, changeBoots,  killRockCrabs, goTrollStronghold, pickupSnapeGrass,
		enterFightCave, enterPothole, claimReward, chopAndBurnLogs, fillBucketWithWater, craftTiara, stealFromStall, tradeStonemason, mineSilver,
			goRellekka, travelToWaterbirth, goKeldagrim;

	Zone Rellekka, Waterbirth, Keldagrim;

	ZoneRequirement inRellekka, atWaterbirth, inKeldagrim;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);
		doEasy.addStep(notGoneToRellekka, goRellekka);
		doEasy.addStep(notChoppedAndBurnedLogs, chopAndBurnLogs);
		doEasy.addStep(notFilledBucketWithWater, fillBucketWithWater);
		doEasy.addStep(new Conditions(notCraftedTiara, notMinedSilver), mineSilver);
		doEasy.addStep(notCraftedTiara, craftTiara);
		doEasy.addStep(new Conditions(notInKeldagrim, notTradedStonemason), goKeldagrim);
		doEasy.addStep(notTradedStonemason, tradeStonemason);
		doEasy.addStep(new Conditions(notInKeldagrim, notStolenFromStall), goKeldagrim);
		doEasy.addStep(notStolenFromStall, stealFromStall);
		doEasy.addStep(notChangedBoots, changeBoots);
		doEasy.addStep(new Conditions(notInWaterBirth), travelToWaterbirth);
		doEasy.addStep(new Conditions(atWaterbirth, notCollectedSnapeGrass), pickupSnapeGrass);
		doEasy.addStep(notKilledRockCrabs, killRockCrabs);
		doEasy.addStep(notCaughtCeruleanTwitch, catchCeruleanTwitch);

		steps.put(0, doEasy);
		return steps;
	}

	public void setupRequirements()
	{
		birdSnare = new ItemRequirement("Bird Snare", ItemID.BIRD_SNARE);
		coins = new ItemRequirement("Coins", ItemID.COINS_995);
		tiaraMould = new ItemRequirement("Tiara mould", ItemID.TIARA_MOULD);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		bucket = new ItemRequirement("Empty bucket", ItemID.EMPTY_BUCKET);
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		combatGear = new ItemRequirement("Combat gear to defeat 5 Rock Crabs", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		notCaughtCeruleanTwitch = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,0));
		notChangedBoots = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,1));
		notKilledRockCrabs = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,2));
		notGoneToRellekka = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,3));
		notCraftedTiara = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,4));
		notTradedStonemason = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,5));
		notCollectedSnapeGrass = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,6));
		notStolenFromStall = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,7));
		notFilledBucketWithWater = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,8));
		notEnteredTrollStronghold = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,9));
		notChoppedAndBurnedLogs = new Conditions(LogicType.NOR, new VarplayerRequirement(1184,true,10));


		inRellekka = new ZoneRequirement(Rellekka);
		inKeldagrim = new ZoneRequirement(Keldagrim);
		atWaterbirth = new ZoneRequirement(Waterbirth);
	}

	public void loadZones()
	{
		Rellekka = new Zone(new WorldPoint(2590, 3626, 0), new WorldPoint(2750, 3790, 0));
		Keldagrim = new Zone(new WorldPoint(2816, 10177, 0), new WorldPoint(2943, 10239, 0));
		Waterbirth = new Zone(new WorldPoint(2496, 3712, 0), new WorldPoint(2559, 3774, 0));

	}

	public void setupSteps()
	{

		catchCeruleanTwitch = new NpcStep(this, NpcID.CERULEAN_TWITCH, new WorldPoint(2730, 3763, 0), "Catch a Cerulean Twitch.");
		changeBoots = new NpcStep(this, NpcID.YRSA, new WorldPoint(2625, 3676, 0), "Change your boots "
				+ "at Yrsa's Shoe Store. Right click Yrsa to access the store.");
		killRockCrabs = new NpcStep(this, NpcID.ROCK_CRAB, new WorldPoint(2681, 3722, 0),
				"Kill 5 Rock crabs.");
		mineSilver = new ObjectStep(this, 11369, new WorldPoint(2686,3703,0), "Mine silver. Craft a tiara from scratch in Rellekka (1/2)");
		craftTiara = new ObjectStep(this, ObjectID.FURNACE_4304, new WorldPoint(2616,3666,0), "Craft a Tiara. Craft a tiara from scratch in Rellekka (2/2)");
		tradeStonemason = new NpcStep(this, NpcID.STONEMASON, new WorldPoint(2848, 10185, 0), "Browse the Stonemason's shop. Must use right-click option Trade.");
		travelToWaterbirth = new NpcStep(this, NpcID.JARVALD, new WorldPoint(2620, 3685, 0), "Travel to Waterbirth " +
				"Island.");
		((NpcStep) travelToWaterbirth).addAlternateNpcs(NpcID.JARVALD_7205, NpcID.JARVALD_10407);
		pickupSnapeGrass = new DetailedQuestStep(this, new WorldPoint(2553, 3754, 0),
				"Pick up 5 snape grass on Waterbirth Island.");
		stealFromStall = new ObjectStep(this, ObjectID.BAKERY_STALL_6163, new WorldPoint(2891,10210,0), "Steal from the Keldagrim crafting or baker's stall.");
		fillBucketWithWater = new ObjectStep(this, ObjectID.WELL_8927, new WorldPoint(2668, 3660, 0), "Fill a bucket "
				+ "with water at the Rellekka well.");
		chopAndBurnLogs = new ObjectStep(this, ObjectID.OAK_10820, new WorldPoint(2681,3626,0), "Chop and burn some oak logs in the Fremennik Province");

		claimReward = new NpcStep(this, NpcID.THORODIN, new WorldPoint(2658, 3627, 0),
			"Talk to Thorodin south of Rellekka to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(axe, pickaxe, coins.quantity(500), birdSnare, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.CRAFTING, 23, true));
		req.add(new SkillRequirement(Skill.FIREMAKING, 15, true));
		req.add(new SkillRequirement(Skill.HUNTER, 11, true));
		req.add(new SkillRequirement(Skill.MINING, 20, true));
		req.add(new SkillRequirement(Skill.SMITHING, 20, true));
		req.add(new SkillRequirement(Skill.THIEVING, 5, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 15, true));
		req.add(new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TROLL_STRONGHOLD, QuestState.FINISHED));
		return req;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Easy Diary", Arrays.asList(catchCeruleanTwitch, changeBoots,  killRockCrabs, goTrollStronghold, pickupSnapeGrass,
				enterFightCave, enterPothole, claimReward, chopAndBurnLogs, fillBucketWithWater, craftTiara, stealFromStall, tradeStonemason, mineSilver,
				goRellekka, travelToWaterbirth, goKeldagrim)));

		return allSteps;
	}
}
