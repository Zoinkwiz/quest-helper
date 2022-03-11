/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.fremennik;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;

@QuestDescriptor(
	quest = QuestHelperQuest.FREMENNIK_EASY
)

public class FremennikEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement birdSnare, coins, tiaraMould, pickaxe, bucket, climbingBoots, axe, tinderbox, combatGear, oakLogs;

	// Items recommended
	ItemRequirement food;

	ItemRequirement silverOre, silverBar, snapeGrass;

	Requirement notCatchCerulean, notChangeBoots, notKilledCrabs, notCraftTiara, notBrowseStonemason,
		notCollectSnapeGrass, notStealStall, notFillBucket, notEnterTrollStronghold, notChopAndBurnOak;

	Requirement fremennikTrials, giantDwarf, trollStronghold, deathPlateau, choppedLogs, minedSilver;

	NpcStep killedCrabs;

	ObjectStep fillBucket, chopOak, mineSilver, smeltSilver, craftTiara;

	QuestStep catchCerulean, changeBoots, browseStonemason,
		collectSnapeGrass, stealStall, enterTrollStronghold, goneToWaterbirth, goneToKeldagrim, goneToCave,
		goneToRiver, goneToVarrock, claimReward, burnOak;

	Zone waterbirth, keldagrim, hunterArea, caveArea, riverArea, varrockArea, mine;

	ZoneRequirement inWaterbirth, inKeldagrim, inHunterArea, inCaveArea, inRiverArea, inVarrockArea, inMine;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);
		doEasy.addStep(new Conditions(notCatchCerulean, inHunterArea), catchCerulean);
		doEasy.addStep(notCatchCerulean, catchCerulean);
		doEasy.addStep(notKilledCrabs, killedCrabs);
		doEasy.addStep(new Conditions(notChopAndBurnOak, oakLogs, choppedLogs), burnOak);
		doEasy.addStep(notChopAndBurnOak, chopOak);
		doEasy.addStep(notFillBucket, fillBucket);
		doEasy.addStep(notChangeBoots, changeBoots);
		doEasy.addStep(new Conditions(notCraftTiara, silverBar), craftTiara);
		doEasy.addStep(new Conditions(notCraftTiara, silverOre, minedSilver), smeltSilver);
		doEasy.addStep(new Conditions(notCraftTiara, pickaxe), mineSilver);
		doEasy.addStep(new Conditions(notCollectSnapeGrass, inWaterbirth), collectSnapeGrass);
		doEasy.addStep(notCollectSnapeGrass, goneToWaterbirth);
		doEasy.addStep(notEnterTrollStronghold, enterTrollStronghold);
		doEasy.addStep(new Conditions(notStealStall, inKeldagrim), stealStall);
		doEasy.addStep(new Conditions(notStealStall, inRiverArea), goneToRiver);
		doEasy.addStep(new Conditions(notStealStall, inCaveArea), goneToCave);
		doEasy.addStep(new Conditions(notStealStall, inVarrockArea), goneToVarrock);
		doEasy.addStep(notStealStall, goneToKeldagrim);
		doEasy.addStep(new Conditions(notBrowseStonemason, inKeldagrim), browseStonemason);
		doEasy.addStep(new Conditions(notBrowseStonemason, inCaveArea), goneToCave);
		doEasy.addStep(new Conditions(notBrowseStonemason, inRiverArea), goneToRiver);
		doEasy.addStep(new Conditions(notBrowseStonemason, inVarrockArea), goneToVarrock);
		doEasy.addStep(notBrowseStonemason, goneToKeldagrim);

		return doEasy;
	}

	public void setupRequirements()
	{
		notCatchCerulean = new VarplayerRequirement(1184, false, 1);
		notChangeBoots = new VarplayerRequirement(1184, false, 2);
		notKilledCrabs = new VarplayerRequirement(1184, false, 3);
		notCraftTiara = new VarplayerRequirement(1184, false, 4);
		notBrowseStonemason = new VarplayerRequirement(1184, false, 5);
		notCollectSnapeGrass = new VarplayerRequirement(1184, false, 6);
		notStealStall = new VarplayerRequirement(1184, false, 7);
		notFillBucket = new VarplayerRequirement(1184, false, 8);
		notEnterTrollStronghold = new VarplayerRequirement(1184, false, 9);
		notChopAndBurnOak = new VarplayerRequirement(1184, false, 10);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notCraftTiara);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(notChangeBoots);
		birdSnare = new ItemRequirement("Bird snare", ItemID.BIRD_SNARE).showConditioned(notCatchCerulean);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notChopAndBurnOak);
		tiaraMould = new ItemRequirement("Tiara mould", ItemID.TIARA_MOULD).showConditioned(notCraftTiara);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET).showConditioned(notFillBucket);
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.BUCKET).showConditioned(notEnterTrollStronghold);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notChopAndBurnOak);
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		silverOre = new ItemRequirement("Silver ore", ItemID.SILVER_ORE);
		snapeGrass = new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS);
		oakLogs = new ItemRequirement("Oak logs", ItemID.OAK_LOGS);

		combatGear = new ItemRequirement("Combat gear", -1, -1).showConditioned(notKilledCrabs);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		fremennikTrials = new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED);
		giantDwarf = new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.FINISHED);
		trollStronghold = new QuestRequirement(QuestHelperQuest.TROLL_STRONGHOLD, QuestState.FINISHED);
		deathPlateau = new QuestRequirement(QuestHelperQuest.DEATH_PLATEAU, QuestState.FINISHED);

		choppedLogs = new ChatMessageRequirement(
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);

		minedSilver = new ChatMessageRequirement(
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) minedSilver).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inMine),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		inMine = new ZoneRequirement(mine);
		inKeldagrim = new ZoneRequirement(keldagrim);
		inWaterbirth = new ZoneRequirement(waterbirth);
		inHunterArea = new ZoneRequirement(hunterArea);
		inCaveArea = new ZoneRequirement(caveArea);
		inRiverArea = new ZoneRequirement(riverArea);
		inVarrockArea = new ZoneRequirement(varrockArea);
	}

	public void loadZones()
	{
		keldagrim = new Zone(new WorldPoint(2816, 10238, 0), new WorldPoint(2943, 10158, 0));
		mine = new Zone(new WorldPoint(2675, 3712, 0), new WorldPoint(2690, 3697, 0));
		waterbirth = new Zone(new WorldPoint(2499, 3770, 0), new WorldPoint(2557, 3713, 0));
		hunterArea = new Zone(new WorldPoint(2690, 3838, 0), new WorldPoint(2748, 3767, 0));
		caveArea = new Zone(new WorldPoint(2767, 10165, 0), new WorldPoint(2802, 10127, 0));
		riverArea = new Zone(new WorldPoint(2816, 10148, 0), new WorldPoint(2893, 10114, 0));
		varrockArea = new Zone(new WorldPoint(3076, 3617, 0), new WorldPoint(3290, 3374, 0));
	}

	public void setupSteps()
	{
		catchCerulean = new ObjectStep(this, ObjectID.BIRD_SNARE_9375, new WorldPoint(2724, 3773, 0),
			"Catch a Cerulean Twitch in the Rellekka Hunter area.", birdSnare.highlighted());
		killedCrabs = new NpcStep(this, NpcID.ROCK_CRAB, new WorldPoint(2707, 3723, 0),
			"Kill 5 Rock crabs.", true, combatGear);
		killedCrabs.addAlternateNpcs(NpcID.ROCK_CRAB_102);

		chopOak = new ObjectStep(this, ObjectID.OAK_10820, new WorldPoint(2714, 3664, 0),
			"Chop some oak logs in Rellekka.", axe, tinderbox);
		chopOak.addIcon(6739);
		burnOak = new ItemStep(this, "Burn the oak logs you've chopped.", tinderbox.highlighted(),
			oakLogs.highlighted());

		fillBucket = new ObjectStep(this, ObjectID.WELL_8927, new WorldPoint(2669, 3661, 0),
			"Fill a bucket at the Rellekka well.", bucket);
		fillBucket.addIcon(ItemID.BUCKET);

		mineSilver = new ObjectStep(this, ObjectID.ROCKS_11368, new WorldPoint(2685, 3702, 0),
			"Mine a silver ore in Rellekka.", pickaxe, fremennikTrials);
		mineSilver.addIcon(ItemID.RUNE_PICKAXE);
		smeltSilver = new ObjectStep(this, ObjectID.FURNACE_4304, new WorldPoint(2617, 3667, 0),
			"Smelt a silver bar in Rellekka.", silverOre.highlighted(), fremennikTrials);
		smeltSilver.addIcon(ItemID.SILVER_ORE);
		craftTiara = new ObjectStep(this, ObjectID.FURNACE_4304, new WorldPoint(2617, 3667, 0),
			"Craft a tiara in Rellekka.", silverBar.highlighted(), fremennikTrials);
		craftTiara.addIcon(ItemID.SILVER_BAR);
		changeBoots = new NpcStep(this, NpcID.YRSA_3933, new WorldPoint(2625, 3674, 0),
			"Change your boots at Yrsa's Shoe Store.", coins.quantity(500));
		goneToWaterbirth = new NpcStep(this, NpcID.JARVALD, new WorldPoint(2620, 3686, 0),
			"Speak with Jarvald to travel to Waterbirth Island.");
		goneToWaterbirth.addDialogStep("What Jarvald is doing.");
		goneToWaterbirth.addDialogStep("Can I come?");
		goneToWaterbirth.addDialogStep("YES");
		collectSnapeGrass = new NpcStep(this, ItemID.SNAPE_GRASS, new WorldPoint(2551, 3754, 0),
			"Collect 5 snape grass on Waterbirth Island. Speak with Jarvald to return to Rellekka when complete.", snapeGrass.highlighted().quantity(5));
		enterTrollStronghold = new ObjectStep(this, ObjectID.SECRET_DOOR, new WorldPoint(2828, 3647, 0),
			"Enter the Troll Stronghold.");
		goneToKeldagrim = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0),
			"Enter the tunnel that leads to Keldagrim. Alternatively Teleport to Varrock and take a minecart near the Grand Exchange.");
		goneToCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0),
			"Go through the cave entrance.");
		goneToRiver = new NpcStep(this, NpcID.DWARVEN_BOATMAN_7726, new WorldPoint(2842, 10129, 0),
			"Speak with the Dwarven Boatman to go to Keldagrim.");
		goneToRiver.addDialogStep("Yes, please take me.");
		stealStall = new ObjectStep(this, ObjectID.BAKERY_STALL_6163, new WorldPoint(2892, 10211, 0),
			"Steal from the bakery stall.");
		browseStonemason = new NpcStep(this, NpcID.STONEMASON, new WorldPoint(2848, 10185, 0),
			"Browse the Stonemason's Shop.");
		goneToVarrock = new ObjectStep(this, ObjectID.TRAPDOOR_16168, new WorldPoint(3140, 3504, 0),
			"Enter the trapdoor near the Grand Exchange.");

		claimReward = new NpcStep(this, NpcID.THORODIN_5526, new WorldPoint(2658, 3627, 0),
			"Talk to Thorodin south of Rellekka to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pickaxe, coins.quantity(500), birdSnare, tiaraMould, axe, bucket, climbingBoots, tinderbox, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("5 Rock Crabs (Level 13)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.CRAFTING, 23));
		req.add(new SkillRequirement(Skill.FIREMAKING, 15));
		req.add(new SkillRequirement(Skill.HUNTER, 11));
		req.add(new SkillRequirement(Skill.MINING, 20));
		req.add(new SkillRequirement(Skill.SMITHING, 20));
		req.add(new SkillRequirement(Skill.THIEVING, 5));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 15));

		req.add(fremennikTrials);
		req.add(giantDwarf);
		req.add(trollStronghold);
		req.add(deathPlateau);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Fremennik Sea Boots (1)", ItemID.FREMENNIK_SEA_BOOTS_1, 1),
				new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Peer the Seer will act as a bank deposit box."),
				new UnlockReward("Fossegrimen will give your enchanted lyre an extra charge when making a sacrifice."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails ceruleanTwitchSteps = new PanelDetails("Catch a Cerulean Twitch", Collections.singletonList(catchCerulean),
			birdSnare);
		ceruleanTwitchSteps.setDisplayCondition(notCatchCerulean);
		allSteps.add(ceruleanTwitchSteps);

		PanelDetails rockCrabSteps = new PanelDetails("Kill 5 Rock Crabs", Collections.singletonList(killedCrabs),
			combatGear, food);
		rockCrabSteps.setDisplayCondition(notKilledCrabs);
		allSteps.add(rockCrabSteps);

		PanelDetails oakSteps = new PanelDetails("Chop and burn", Arrays.asList(chopOak, burnOak),
			new SkillRequirement(Skill.FIREMAKING, 15), new SkillRequirement(Skill.WOODCUTTING, 15),
			axe, tinderbox);
		oakSteps.setDisplayCondition(notChopAndBurnOak);
		allSteps.add(oakSteps);

		PanelDetails bucketSteps = new PanelDetails("Fill bucket", Collections.singletonList(fillBucket), bucket);
		bucketSteps.setDisplayCondition(notFillBucket);
		allSteps.add(bucketSteps);

		PanelDetails changeBootsSteps = new PanelDetails("Change boots", Collections.singletonList(changeBoots),
			fremennikTrials, coins.quantity(500));
		changeBootsSteps.setDisplayCondition(notChangeBoots);
		allSteps.add(changeBootsSteps);

		PanelDetails tiaraSteps = new PanelDetails("Craft Tiara", Arrays.asList(mineSilver, smeltSilver, craftTiara),
			new SkillRequirement(Skill.CRAFTING, 23), new SkillRequirement(Skill.MINING, 20),
			new SkillRequirement(Skill.SMITHING, 20), fremennikTrials, pickaxe, tiaraMould);
		tiaraSteps.setDisplayCondition(notCraftTiara);
		allSteps.add(tiaraSteps);

		PanelDetails snapeGrassSteps = new PanelDetails("Collect snape grass", Arrays.asList(goneToWaterbirth,
			collectSnapeGrass), fremennikTrials);
		snapeGrassSteps.setDisplayCondition(notCollectSnapeGrass);
		allSteps.add(snapeGrassSteps);

		PanelDetails trollStrongholdSteps = new PanelDetails("Enter troll stronghold",
			Collections.singletonList(enterTrollStronghold), trollStronghold, deathPlateau, climbingBoots);
		trollStrongholdSteps.setDisplayCondition(notEnterTrollStronghold);
		allSteps.add(trollStrongholdSteps);

		PanelDetails browseStonemasonSteps = new PanelDetails("Browse Stonemason's store",
			Arrays.asList(goneToKeldagrim, goneToCave, goneToRiver, browseStonemason), giantDwarf);
		browseStonemasonSteps.setDisplayCondition(notBrowseStonemason);
		allSteps.add(browseStonemasonSteps);

		PanelDetails bakersStallSteps = new PanelDetails("Steal from baker's stall", Arrays.asList(goneToKeldagrim,
			goneToCave, goneToRiver, stealStall), giantDwarf);
		bakersStallSteps.setDisplayCondition(notStealStall);
		allSteps.add(bakersStallSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
