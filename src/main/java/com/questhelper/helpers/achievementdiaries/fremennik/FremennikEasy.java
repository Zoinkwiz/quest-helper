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
package com.questhelper.helpers.achievementdiaries.fremennik;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	QuestStep catchCerulean, changeBoots, browseStonemason, collectSnapeGrass, stealStall, enterTrollStronghold,
		goneToWaterbirth, claimReward, burnOak, goneToKeldagrimStone, goneToCaveStone, goneToRiverStone,
		goneToVarrockStone, goneToKeldagrimStall, goneToCaveStall, goneToRiverStall, goneToVarrockStall;

	Zone waterbirth, keldagrim, hunterArea, caveArea, riverArea, varrockArea, mine;

	ZoneRequirement inWaterbirth, inKeldagrim, inHunterArea, inCaveArea, inRiverArea, inVarrockArea, inMine;

	ConditionalStep catchCeruleanTask, changeBootsTask, killedCrabsTask, craftTiaraTask, browseStonemasonTask,
		collectSnapeGrassTask, stealStallTask, fillBucketTask, enterTrollStrongholdTask, chopAndBurnOakTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		catchCeruleanTask = new ConditionalStep(this, catchCerulean);
		catchCeruleanTask.addStep(inHunterArea, catchCerulean);
		doEasy.addStep(notCatchCerulean, catchCeruleanTask);

		killedCrabsTask = new ConditionalStep(this, killedCrabs);
		doEasy.addStep(notKilledCrabs, killedCrabsTask);

		chopAndBurnOakTask = new ConditionalStep(this, chopOak);
		chopAndBurnOakTask.addStep(new Conditions(oakLogs, choppedLogs), burnOak);
		doEasy.addStep(notChopAndBurnOak, chopAndBurnOakTask);

		fillBucketTask = new ConditionalStep(this, fillBucket);
		doEasy.addStep(notFillBucket, fillBucketTask);

		changeBootsTask = new ConditionalStep(this, changeBoots);
		doEasy.addStep(notChangeBoots, changeBootsTask);

		craftTiaraTask = new ConditionalStep(this, mineSilver);
		craftTiaraTask.addStep(new Conditions(silverOre, minedSilver), smeltSilver);
		craftTiaraTask.addStep(silverBar, craftTiara);
		doEasy.addStep(notCraftTiara, craftTiaraTask);

		collectSnapeGrassTask = new ConditionalStep(this, goneToWaterbirth);
		collectSnapeGrassTask.addStep(inWaterbirth, collectSnapeGrass);
		doEasy.addStep(notCollectSnapeGrass, collectSnapeGrassTask);

		enterTrollStrongholdTask = new ConditionalStep(this, enterTrollStronghold);
		doEasy.addStep(notEnterTrollStronghold, enterTrollStrongholdTask);

		browseStonemasonTask = new ConditionalStep(this, goneToKeldagrimStone);
		browseStonemasonTask.addStep(inVarrockArea, goneToVarrockStone);
		browseStonemasonTask.addStep(inRiverArea, goneToRiverStone);
		browseStonemasonTask.addStep(inCaveArea, goneToCaveStone);
		browseStonemasonTask.addStep(inKeldagrim, browseStonemason);
		doEasy.addStep(notBrowseStonemason, browseStonemasonTask);

		stealStallTask = new ConditionalStep(this, goneToKeldagrimStall);
		stealStallTask.addStep(inVarrockArea, goneToVarrockStall);
		stealStallTask.addStep(inCaveArea, goneToCaveStall);
		stealStallTask.addStep(inRiverArea, goneToRiverStall);
		stealStallTask.addStep(inKeldagrim, stealStall);
		doEasy.addStep(notStealStall, stealStallTask);

		return doEasy;
	}

	@Override
	protected void setupRequirements()
	{
		notCatchCerulean = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 1);
		notChangeBoots = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 2);
		notKilledCrabs = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 3);
		notCraftTiara = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 4);
		notBrowseStonemason = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 5);
		notCollectSnapeGrass = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 6);
		notStealStall = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 7);
		notFillBucket = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 8);
		notEnterTrollStronghold = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 9);
		notChopAndBurnOak = new VarplayerRequirement(VarPlayerID.FREMENNIK_ACHIEVEMENT_DIARY, false, 10);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notCraftTiara).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notChangeBoots);
		birdSnare = new ItemRequirement("Bird snare", ItemID.HUNTING_OJIBWAY_BIRD_SNARE).showConditioned(notCatchCerulean).isNotConsumed();
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notChopAndBurnOak).isNotConsumed();
		tiaraMould = new ItemRequirement("Tiara mould", ItemID.TIARA_MOULD).showConditioned(notCraftTiara).isNotConsumed();
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY).showConditioned(notFillBucket).isNotConsumed();
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.BUCKET_EMPTY).showConditioned(notEnterTrollStronghold).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notChopAndBurnOak).isNotConsumed();
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		silverOre = new ItemRequirement("Silver ore", ItemID.SILVER_ORE);
		snapeGrass = new ItemRequirement("Snape grass", ItemID.SNAPE_GRASS);
		oakLogs = new ItemRequirement("Oak logs", ItemID.OAK_LOGS);

		combatGear = new ItemRequirement("Combat gear", -1, -1).showConditioned(notKilledCrabs).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		fremennikTrials = new QuestRequirement(QuestHelperQuest.THE_FREMENNIK_TRIALS, QuestState.FINISHED);
		giantDwarf = new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.IN_PROGRESS);
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
	}

	@Override
	protected void setupZones()
	{
		keldagrim = new Zone(new WorldPoint(2816, 10238, 0), new WorldPoint(2943, 10158, 0));
		mine = new Zone(new WorldPoint(2675, 3712, 0), new WorldPoint(2690, 3697, 0));
		waterbirth = new Zone(new WorldPoint(2499, 3770, 0), new WorldPoint(2557, 3713, 0));
		hunterArea = new Zone(new WorldPoint(2690, 3838, 0), new WorldPoint(2748, 3767, 0));
		caveArea = new Zone(new WorldPoint(2767, 10165, 0), new WorldPoint(2802, 10127, 0));
		riverArea = new Zone(new WorldPoint(2816, 10148, 0), new WorldPoint(2893, 10114, 0));
		varrockArea = new Zone(new WorldPoint(3076, 3617, 0), new WorldPoint(3290, 3374, 0));

		inMine = new ZoneRequirement(mine);
		inKeldagrim = new ZoneRequirement(keldagrim);
		inWaterbirth = new ZoneRequirement(waterbirth);
		inHunterArea = new ZoneRequirement(hunterArea);
		inCaveArea = new ZoneRequirement(caveArea);
		inRiverArea = new ZoneRequirement(riverArea);
		inVarrockArea = new ZoneRequirement(varrockArea);
	}

	public void setupSteps()
	{
		catchCerulean = new ObjectStep(this, ObjectID.HUNTING_OJIBWAY_TRAP_FULL_POLAR, new WorldPoint(2724, 3773, 0),
			"Catch a Cerulean Twitch in the Rellekka Hunter area.", birdSnare.highlighted());
		killedCrabs = new NpcStep(this, NpcID.HORROR_ROCKCRAB, new WorldPoint(2707, 3723, 0),
			"Kill 5 Rock crabs.", true, combatGear);
		killedCrabs.addAlternateNpcs(NpcID.HORROR_ROCKCRAB_SMALL);

		chopOak = new ObjectStep(this, ObjectID.OAKTREE, new WorldPoint(2714, 3664, 0),
			"Chop some oak logs in Rellekka.", axe, tinderbox);
		chopOak.addIcon(6739);
		burnOak = new ItemStep(this, "Burn the oak logs you've chopped.", tinderbox.highlighted(),
			oakLogs.highlighted());

		fillBucket = new ObjectStep(this, ObjectID.RELLEKKA_WELL, new WorldPoint(2669, 3661, 0),
			"Fill a bucket at the Rellekka well.", bucket);
		fillBucket.addIcon(ItemID.BUCKET_EMPTY);

		mineSilver = new ObjectStep(this, ObjectID.SILVERROCK1, new WorldPoint(2685, 3702, 0),
			"Mine a silver ore in Rellekka.", pickaxe, fremennikTrials);
		mineSilver.addIcon(ItemID.RUNE_PICKAXE);
		smeltSilver = new ObjectStep(this, ObjectID.VIKING_FURNACE, new WorldPoint(2617, 3667, 0),
			"Smelt a silver bar in Rellekka.", silverOre.highlighted(), fremennikTrials);
		smeltSilver.addIcon(ItemID.SILVER_ORE);
		craftTiara = new ObjectStep(this, ObjectID.VIKING_FURNACE, new WorldPoint(2617, 3667, 0),
			"Craft a tiara in Rellekka.", silverBar.highlighted(), fremennikTrials);
		craftTiara.addIcon(ItemID.SILVER_BAR);
		changeBoots = new NpcStep(this, NpcID.VIKING_CLOTHING_SHOPKEEPER, new WorldPoint(2625, 3674, 0),
			"Change your boots at Yrsa's Shoe Store.", coins.quantity(500));
		goneToWaterbirth = new NpcStep(this, new int[]{NpcID.VIKING_DAGGANOTH_CAVE_FERRYMAN_ISLAND, NpcID.VIKING_DAGGANOTH_CAVE_FERRYMAN_1OP}, new WorldPoint(2620, 3686, 0),
			"Speak with Jarvald to travel to Waterbirth Island.");
		goneToWaterbirth.addDialogStep("What Jarvald is doing.");
		goneToWaterbirth.addDialogStep("Can I come?");
		goneToWaterbirth.addDialogStep("YES");
		collectSnapeGrass = new NpcStep(this, ItemID.SNAPE_GRASS, new WorldPoint(2551, 3754, 0),
			"Collect 5 snape grass on Waterbirth Island. Speak with Jarvald to return to Rellekka when complete.", snapeGrass.highlighted().quantity(5));
		enterTrollStronghold = new ObjectStep(this, ObjectID.TROLL_STRONGHOLD_ENTRANCE, new WorldPoint(2828, 3647, 0),
			"Enter the Troll Stronghold.");
		goneToKeldagrimStone = new ObjectStep(this, ObjectID.TROLLROMANCE_STRONGHOLD_EXIT_TUNNEL, new WorldPoint(2732, 3713, 0),
			"Enter the tunnel that leads to Keldagrim. Alternatively Teleport to Varrock and take a minecart near the Grand Exchange.");
		goneToCaveStone = new ObjectStep(this, ObjectID.DWARF_CAVEWALL_TUNNEL, new WorldPoint(2781, 10161, 0),
			"Go through the cave entrance.");
		goneToRiverStone = new NpcStep(this, NpcID.DWARF_CITY_BOATMAN_MINES_POSTQUEST, new WorldPoint(2842, 10129, 0),
			"Speak with the Dwarven Boatman to go to Keldagrim.");
		goneToRiverStone.addDialogStep("Yes, please take me.");
		goneToVarrockStone = new ObjectStep(this, ObjectID.GE_KELDAGRIM_TRAPDOOR, new WorldPoint(3140, 3504, 0),
			"Enter the trapdoor near the Grand Exchange.");
		goneToKeldagrimStall = new ObjectStep(this, ObjectID.TROLLROMANCE_STRONGHOLD_EXIT_TUNNEL, new WorldPoint(2732, 3713, 0),
			"Enter the tunnel that leads to Keldagrim. Alternatively Teleport to Varrock and take a minecart near the Grand Exchange.");
		goneToCaveStall = new ObjectStep(this, ObjectID.DWARF_CAVEWALL_TUNNEL, new WorldPoint(2781, 10161, 0),
			"Go through the cave entrance.");
		goneToRiverStall = new NpcStep(this, NpcID.DWARF_CITY_BOATMAN_MINES_POSTQUEST, new WorldPoint(2842, 10129, 0),
			"Speak with the Dwarven Boatman to go to Keldagrim.");
		goneToRiverStall.addDialogStep("Yes, please take me.");
		goneToVarrockStall = new ObjectStep(this, ObjectID.GE_KELDAGRIM_TRAPDOOR, new WorldPoint(3140, 3504, 0),
			"Enter the trapdoor near the Grand Exchange.");
		stealStall = new ObjectStep(this, ObjectID.DWARF_MARKET_BAKERY, new WorldPoint(2892, 10211, 0),
			"Steal from the bakery stall.");
		browseStonemason = new NpcStep(this, NpcID.POH_STONEMASON, new WorldPoint(2848, 10185, 0),
			"Browse the Stonemason's Shop.");

		claimReward = new NpcStep(this, NpcID.VIKING_FREM_DIARY, new WorldPoint(2658, 3627, 0),
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
			new ItemReward("Fremennik Sea Boots (1)", ItemID.FREMENNIK_BOOTS_EASY, 1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.THOSF_REWARD_LAMP, 1));
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
		ceruleanTwitchSteps.setLockingStep(catchCeruleanTask);
		allSteps.add(ceruleanTwitchSteps);

		PanelDetails rockCrabSteps = new PanelDetails("Kill 5 Rock Crabs", Collections.singletonList(killedCrabs),
			combatGear, food);
		rockCrabSteps.setDisplayCondition(notKilledCrabs);
		rockCrabSteps.setLockingStep(killedCrabsTask);
		allSteps.add(rockCrabSteps);

		PanelDetails oakSteps = new PanelDetails("Chop and burn", Arrays.asList(chopOak, burnOak),
			new SkillRequirement(Skill.FIREMAKING, 15), new SkillRequirement(Skill.WOODCUTTING, 15),
			axe, tinderbox);
		oakSteps.setDisplayCondition(notChopAndBurnOak);
		oakSteps.setLockingStep(chopAndBurnOakTask);
		allSteps.add(oakSteps);

		PanelDetails bucketSteps = new PanelDetails("Fill bucket", Collections.singletonList(fillBucket), bucket);
		bucketSteps.setDisplayCondition(notFillBucket);
		bucketSteps.setLockingStep(fillBucketTask);
		allSteps.add(bucketSteps);

		PanelDetails changeBootsSteps = new PanelDetails("Change boots", Collections.singletonList(changeBoots),
			fremennikTrials, coins.quantity(500));
		changeBootsSteps.setDisplayCondition(notChangeBoots);
		changeBootsSteps.setLockingStep(changeBootsTask);
		allSteps.add(changeBootsSteps);

		PanelDetails tiaraSteps = new PanelDetails("Craft Tiara", Arrays.asList(mineSilver, smeltSilver, craftTiara),
			new SkillRequirement(Skill.CRAFTING, 23), new SkillRequirement(Skill.MINING, 20),
			new SkillRequirement(Skill.SMITHING, 20), fremennikTrials, pickaxe, tiaraMould);
		tiaraSteps.setDisplayCondition(notCraftTiara);
		tiaraSteps.setLockingStep(craftTiaraTask);
		allSteps.add(tiaraSteps);

		PanelDetails snapeGrassSteps = new PanelDetails("Collect snape grass", Arrays.asList(goneToWaterbirth,
			collectSnapeGrass), fremennikTrials);
		snapeGrassSteps.setDisplayCondition(notCollectSnapeGrass);
		snapeGrassSteps.setLockingStep(collectSnapeGrassTask);
		allSteps.add(snapeGrassSteps);

		PanelDetails trollStrongholdSteps = new PanelDetails("Enter troll stronghold",
			Collections.singletonList(enterTrollStronghold), trollStronghold, deathPlateau, climbingBoots);
		trollStrongholdSteps.setDisplayCondition(notEnterTrollStronghold);
		trollStrongholdSteps.setLockingStep(enterTrollStrongholdTask);
		allSteps.add(trollStrongholdSteps);

		PanelDetails browseStonemasonSteps = new PanelDetails("Browse Stonemason's store",
			Arrays.asList(goneToKeldagrimStone, goneToCaveStone, goneToRiverStone, browseStonemason), giantDwarf);
		browseStonemasonSteps.setDisplayCondition(notBrowseStonemason);
		browseStonemasonSteps.setLockingStep(browseStonemasonTask);
		allSteps.add(browseStonemasonSteps);

		PanelDetails bakersStallSteps = new PanelDetails("Steal from baker's stall", Arrays.asList(goneToKeldagrimStall,
			goneToCaveStall, goneToRiverStall, stealStall), giantDwarf);
		bakersStallSteps.setDisplayCondition(notStealStall);
		bakersStallSteps.setLockingStep(stealStallTask);
		allSteps.add(bakersStallSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
