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
package com.questhelper.achievementdiaries.ardougne;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;
import net.runelite.api.vars.AccountType;
import net.runelite.client.game.FishingSpot;

@QuestDescriptor(
	quest = QuestHelperQuest.ARDOUGNE_MEDIUM
)
public class ArdougneMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, fairyAccess, mithGrap, crossbow, strawSeeds, rake, seedDib, basketOrCompost,
		waterRune, lawRune, yewLog1, smallFishingNet, lightSource, rawChick, rawSword, ibanStaff, skavMap, nightshade,
		bucket, ibanStaffU, coins, basket, compost, spade, yewLog11;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement fairyTaleII, enlightenedJourney, handInSand, watchtower, seaSlug, towerOfLife, undergroundPass,
		plagueCity, grapUp, notCWBallon, notCWBallon2, normalBook;

	Requirement notUniPen, notGrapYan, notArdyStraw, notTPArdy, notBalloonCW, notClaimSand, notFishOnPlatform,
		notPickMasterFarmer, notCaveNightshade, notKillSwordchick, notIbanUpgrade, notNecroTower;

	QuestStep claimReward, uniPen, grapYan, ardyStraw, tPArdy, balloonCW, claimSand, fishOnPlatform,
		pickMasterFarmer, caveNightshade, killSwordchick, ibanUpgrade, necroTower, moveToSkavid, moveToPlatform,
		moveToBasement, grapYan2, talkToAug, equipIban;

	NpcStep moveToEntrana;

	Zone skavidCaves, platform, basement, entrana, wall;

	ZoneRequirement inSkavidCaves, inPlatform, inBasement, inEntrana, inWall;

	ConditionalStep uniPenTask, grapYanTask, ardyStrawTask, tpArdyTask, balloonCWTask, claimSandTask, fishOnPlatformTask,
		pickMasterFarmerTask, caveNightshadeTask, killSwordchickTask, ibanUpgradeTask, necroTowerTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		ardyStrawTask = new ConditionalStep(this, ardyStraw);
		doMedium.addStep(notArdyStraw, ardyStrawTask);

		balloonCWTask = new ConditionalStep(this, moveToEntrana);
		balloonCWTask.addStep(inEntrana, talkToAug);
		balloonCWTask.addStep(notCWBallon, balloonCW);
		doMedium.addStep(notBalloonCW, balloonCWTask);

		caveNightshadeTask = new ConditionalStep(this, moveToSkavid);
		caveNightshadeTask.addStep(inSkavidCaves, caveNightshade);
		doMedium.addStep(notCaveNightshade, caveNightshadeTask);

		grapYanTask = new ConditionalStep(this, grapYan);
		grapYanTask.addStep(new Conditions(grapUp, inWall), grapYan2);
		doMedium.addStep(notGrapYan, grapYanTask);

		claimSandTask = new ConditionalStep(this, claimSand);
		doMedium.addStep(notClaimSand, claimSandTask);

		tpArdyTask = new ConditionalStep(this, tPArdy);
		doMedium.addStep(notTPArdy, tpArdyTask);

		killSwordchickTask = new ConditionalStep(this, moveToBasement);
		killSwordchickTask.addStep(inBasement, killSwordchick);
		doMedium.addStep(notKillSwordchick, killSwordchickTask);

		fishOnPlatformTask = new ConditionalStep(this, moveToPlatform);
		fishOnPlatformTask.addStep(inPlatform, fishOnPlatform);
		doMedium.addStep(notFishOnPlatform, fishOnPlatformTask);

		pickMasterFarmerTask = new ConditionalStep(this, pickMasterFarmer);
		doMedium.addStep(notPickMasterFarmer, pickMasterFarmerTask);

		ibanUpgradeTask = new ConditionalStep(this, ibanUpgrade);
		ibanUpgradeTask.addStep(ibanStaffU.alsoCheckBank(questBank), equipIban);
		doMedium.addStep(notIbanUpgrade, ibanUpgradeTask);

		uniPenTask = new ConditionalStep(this, uniPen);
		doMedium.addStep(notUniPen, uniPenTask);

		necroTowerTask = new ConditionalStep(this, necroTower);
		doMedium.addStep(notNecroTower, necroTowerTask);

		return doMedium;
	}

	@Override
	public void setupRequirements()
	{
		notUniPen = new VarplayerRequirement(1196, false, 13);
		notGrapYan = new VarplayerRequirement(1196, false, 14);
		notArdyStraw = new VarplayerRequirement(1196, false, 15);
		notTPArdy = new VarplayerRequirement(1196, false, 16);
		notBalloonCW = new VarplayerRequirement(1196, false, 17);
		notClaimSand = new VarplayerRequirement(1196, false, 18);
		notFishOnPlatform = new VarplayerRequirement(1196, false, 19);
		notPickMasterFarmer = new VarplayerRequirement(1196, false, 20);
		notCaveNightshade = new VarplayerRequirement(1196, false, 21);
		notKillSwordchick = new VarplayerRequirement(1196, false, 23);
		notIbanUpgrade = new VarplayerRequirement(1196, false, 24);
		notNecroTower = new VarplayerRequirement(1196, false, 25);

		notCWBallon = new VarbitRequirement(2869, 1);
		notCWBallon2 = new VarbitRequirement(2869, 0);
		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		fairyAccess = new ItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF)
			.showConditioned(new Conditions(LogicType.OR, notNecroTower, notUniPen));
		skavMap = new ItemRequirement("Skavid map", ItemID.SKAVID_MAP).showConditioned(notCaveNightshade);
		lightSource = new ItemRequirement("Any light source", ItemCollections.LIGHT_SOURCES)
			.showConditioned(notCaveNightshade);
		nightshade = new ItemRequirement("Cave nightshade", ItemID.CAVE_NIGHTSHADE);
		mithGrap = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notGrapYan);
		crossbow = new ItemRequirement("Any crossbow", ItemCollections.CROSSBOWS).showConditioned(notGrapYan);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET)
			.showConditioned(new Conditions(notClaimSand));
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPArdy);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE).showConditioned(notTPArdy);
		rawChick = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN).showConditioned(notKillSwordchick);
		rawSword = new ItemRequirement("Raw swordfish", ItemID.RAW_SWORDFISH).showConditioned(notKillSwordchick);
		ibanStaff = new ItemRequirement("Iban staff", ItemID.IBANS_STAFF).showConditioned(notIbanUpgrade);
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notIbanUpgrade);
		ibanStaffU = new ItemRequirement("Iban staff Upgraded", ItemID.IBANS_STAFF_U).showConditioned(notIbanUpgrade);
		seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notArdyStraw);
		strawSeeds = new ItemRequirement("Strawberry seeds", ItemID.STRAWBERRY_SEED).showConditioned(notArdyStraw);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notArdyStraw);
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET)
			.showConditioned(notFishOnPlatform);
		yewLog1 = new ItemRequirement("Yew logs", ItemID.YEW_LOGS, 1).showConditioned(new Conditions(notBalloonCW,
			notCWBallon));
		yewLog11 = new ItemRequirement("Yew logs", ItemID.YEW_LOGS, 11).showConditioned(new Conditions(notBalloonCW,
			notCWBallon2));
		basket = new ItemRequirement("Basket of apples", ItemID.APPLES5).showConditioned(notArdyStraw);
		compost = new ItemRequirement("Compost", ItemCollections.COMPOST).showConditioned(notArdyStraw);
		basketOrCompost = new ItemRequirements(LogicType.OR, "Basket of apples or compost", compost, basket).showConditioned(notArdyStraw);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notArdyStraw);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inSkavidCaves = new ZoneRequirement(skavidCaves);
		inBasement = new ZoneRequirement(basement);
		inPlatform = new ZoneRequirement(platform);
		inEntrana = new ZoneRequirement(entrana);
		inWall = new ZoneRequirement(wall);

		grapUp = new ChatMessageRequirement(
			inWall,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) grapUp).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inWall),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		fairyTaleII = new VarbitRequirement(QuestVarbits.QUEST_FAIRYTALE_II_CURE_A_QUEEN.getId(),
			Operation.GREATER_EQUAL, 40, "Partial completion of Fairytale II for access to fairy rings");
		enlightenedJourney = new QuestRequirement(QuestHelperQuest.ENLIGHTENED_JOURNEY, QuestState.FINISHED);
		handInSand = new QuestRequirement(QuestHelperQuest.THE_HAND_IN_THE_SAND, QuestState.FINISHED);
		watchtower = new QuestRequirement(QuestHelperQuest.WATCHTOWER, QuestState.IN_PROGRESS);
		seaSlug = new QuestRequirement(QuestHelperQuest.SEA_SLUG, QuestState.IN_PROGRESS);
		towerOfLife = new QuestRequirement(QuestHelperQuest.TOWER_OF_LIFE, QuestState.FINISHED);
		undergroundPass = new QuestRequirement(QuestHelperQuest.UNDERGROUND_PASS, QuestState.FINISHED);
		plagueCity = new QuestRequirement(QuestHelperQuest.PLAGUE_CITY, QuestState.FINISHED);
	}

	public void loadZones()
	{
		skavidCaves = new Zone(new WorldPoint(2527, 9470, 0), new WorldPoint(2536, 9460, 0));
		platform = new Zone(new WorldPoint(2760, 3293, 0), new WorldPoint(2795, 3271, 0));
		basement = new Zone(new WorldPoint(3008, 4415, 0), new WorldPoint(3070, 4352, 0));
		entrana = new Zone(new WorldPoint(2800, 3394, 0), new WorldPoint(2878, 3325, 0));
		wall = new Zone(new WorldPoint(2539, 3077, 1), new WorldPoint(2581, 3070, 1));
	}

	public void setupSteps()
	{
		uniPen = new DetailedQuestStep(this, "Enter the unicorn pen in Ardougne Zoo using Fairy rings (BIS).", fairyAccess);
		necroTower = new DetailedQuestStep(this, "Visit the island east of the Necromancer Tower (AIR).", fairyAccess);

		grapYan = new ObjectStep(this, ObjectID.WALL_17047, new WorldPoint(2556, 3072, 0),
			"Grapple up Yanille's south wall.", crossbow.equipped(), mithGrap);
		grapYan2 = new ObjectStep(this, ObjectID.WALL_17048, new WorldPoint(2556, 3075, 1),
			"Jump off the opposite side!");

		if (client.getAccountType() == AccountType.ULTIMATE_IRONMAN)// will need testing to confirm this works
		{
			claimSand = new ObjectStep(this, ObjectID.SANDPIT, new WorldPoint(2543, 3104, 0),
				"Fill a bucket with sand using Bert's sand pit.", bucket);
		}
		else
		{
			claimSand = new NpcStep(this, NpcID.BERT_5819, new WorldPoint(2551, 3100, 0),
				"Claim buckets of sand from Bert in Yanille.");
		}

		balloonCW = new DetailedQuestStep(this, "Use a nearby Hot Air Ballon to travel to Castle Wars.",
			yewLog1);
		moveToEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA_1167, new WorldPoint(3048, 3236, 0),
			"Speak with a monk to travel to Entrana.", true, yewLog11);
		moveToEntrana.addAlternateNpcs(NpcID.MONK_OF_ENTRANA_1166, NpcID.MONK_OF_ENTRANA);
		talkToAug = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2810, 3356, 0),
			"Speak with Augustine and travel to Castle Wars.", yewLog11);

		tPArdy = new DetailedQuestStep(this, "Cast Teleport to Ardougne spell.", lawRune.quantity(2),
			waterRune.quantity(2));

		moveToBasement = new ObjectStep(this, ObjectID.TRAPDOOR_21922, new WorldPoint(2648, 3212, 0),
			"Enter the basement of the Tower of Life.", combatGear, food, rawChick, rawSword);
		killSwordchick = new ObjectStep(this, ObjectID.SYMBOL_OF_LIFE, new WorldPoint(3034, 4362, 0),
			"Activate the Symbol of Life and kill the Swordchick.", combatGear, food, rawChick, rawSword);

		moveToSkavid = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_2806, new WorldPoint(2524, 3069, 0),
			"Enter the Cave.", lightSource, skavMap);
		caveNightshade = new ItemStep(this, "Pickup the Cave nightshade.", nightshade);

		moveToPlatform = new NpcStep(this, NpcID.JEB, new WorldPoint(2719, 3305, 0),
			"Talk to Jeb or Holgart to travel to the Fishing Platform.", smallFishingNet);
		((NpcStep) (moveToPlatform)).addAlternateNpcs(NpcID.HOLGART_7789);
		fishOnPlatform = new NpcStep(this, FishingSpot.SHRIMP.getIds(), new WorldPoint(2790, 3276, 0),
			"Catch any fish on the Fishing Platform.", smallFishingNet);

		pickMasterFarmer = new NpcStep(this, NpcID.MASTER_FARMER, new WorldPoint(2637, 3362, 0),
			"Pickpocket the master farmer north of East Ardougne.");

		ibanUpgrade = new NpcStep(this, NpcID.DARK_MAGE_7753, new WorldPoint(2455, 3312, 0),
			"Talk to the Dark mage in West Ardougne to upgrade your staff.", ibanStaff, coins.quantity(200000));
		ibanUpgrade.addDialogSteps("Can you upgrade my Staff of Iban?",
			"Here's 200,000 coins. Please upgrade the staff.");
		equipIban = new ItemStep(this, "Equip Iban's staff (u).", ibanStaffU);

		ardyStraw = new ObjectStep(this, NullObjectID.NULL_8555, new WorldPoint(2667, 3371, 0),
			"Plant and harvest the strawberries from the north Ardougne allotment. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			true, rake, spade, seedDib, strawSeeds.quantity(3));

		claimReward = new NpcStep(this, NpcID.TWOPINTS, new WorldPoint(2574, 3323, 0),
			"Talk to Two-pints in the Flying Horse Inn at East Ardougne to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, crossbow, mithGrap, rake, strawSeeds.quantity(3), seedDib, ibanStaff,
			coins.quantity(200000), skavMap, lightSource, smallFishingNet, rawChick, rawSword, yewLog1, yewLog11,
			fairyAccess, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, basketOrCompost);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 39));
		reqs.add(new SkillRequirement(Skill.FARMING, 31));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 50));
		reqs.add(new SkillRequirement(Skill.MAGIC, 51));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 38));
		reqs.add(new SkillRequirement(Skill.RANGED, 21));
		reqs.add(new SkillRequirement(Skill.THIEVING, 38));

		reqs.add(fairyTaleII);
		reqs.add(enlightenedJourney);
		reqs.add(handInSand);
		reqs.add(watchtower);
		reqs.add(seaSlug);
		reqs.add(towerOfLife);
		reqs.add(undergroundPass);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Swordchick (lvl 46)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ardougne Cloak 2", ItemID.ARDOUGNE_CLOAK_2),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Three daily teleports to Ardougne farm patch with the Ardougne cloak 2"),
			new UnlockReward("100 free noted pure essence every day from Wizard Cromperty"),
			new UnlockReward("Unicow, Newtroost, and Spidine drops will be noted at the Tower of Life"),
			new UnlockReward("10% increased chance to pickpocket in Ardougne (even if the cloak is not equipped or in inventory)"),
			new UnlockReward("Ability to toggle the Ring of life teleport to Ardougne"),
			new UnlockReward("Receive additional runes when crafting essence at the Ourania Altar")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails strawSteps = new PanelDetails("Ardougne Strawberries", Collections.singletonList(ardyStraw),
			new SkillRequirement(Skill.FARMING, 31), rake, strawSeeds.quantity(3), seedDib, spade);
		strawSteps.setDisplayCondition(notArdyStraw);
		strawSteps.setLockingStep(ardyStrawTask);
		allSteps.add(strawSteps);

		PanelDetails cwSteps = new PanelDetails("Castle Wars Balloon", Arrays.asList(moveToEntrana, talkToAug,
			balloonCW), new SkillRequirement(Skill.FIREMAKING, 50), enlightenedJourney, yewLog11);
		cwSteps.setDisplayCondition(new Conditions(notBalloonCW, notCWBallon2));
		cwSteps.setLockingStep(balloonCWTask);
		allSteps.add(cwSteps);

		PanelDetails cw2Steps = new PanelDetails("Castle Wars Balloon", Collections.singletonList(balloonCW),
			new SkillRequirement(Skill.FIREMAKING, 50), enlightenedJourney, yewLog1);
		cw2Steps.setDisplayCondition(new Conditions(notBalloonCW, notCWBallon));
		cw2Steps.setLockingStep(balloonCWTask);
		allSteps.add(cw2Steps);

		PanelDetails nightSteps = new PanelDetails("Cave Nightshade", Arrays.asList(moveToSkavid, caveNightshade),
			watchtower, lightSource, skavMap);
		nightSteps.setDisplayCondition(notCaveNightshade);
		nightSteps.setLockingStep(caveNightshadeTask);
		allSteps.add(nightSteps);

		PanelDetails grapSteps = new PanelDetails("Yanille Wall Grapple", Arrays.asList(grapYan, grapYan2),
			new SkillRequirement(Skill.AGILITY, 39), new SkillRequirement(Skill.STRENGTH, 38),
			new SkillRequirement(Skill.RANGED, 21), mithGrap, crossbow);
		grapSteps.setDisplayCondition(notGrapYan);
		grapSteps.setLockingStep(grapYanTask);
		allSteps.add(grapSteps);

		PanelDetails sandSteps = new PanelDetails("Claim Sand", Collections.singletonList(claimSand), handInSand);
		sandSteps.setDisplayCondition(notClaimSand);
		sandSteps.setLockingStep(claimSandTask);
		allSteps.add(sandSteps);

		PanelDetails tpSteps = new PanelDetails("Teleport to Ardougne", Collections.singletonList(tPArdy),
			new SkillRequirement(Skill.MAGIC, 51), plagueCity, normalBook, lawRune.quantity(2), waterRune.quantity(2));
		tpSteps.setDisplayCondition(notTPArdy);
		tpSteps.setLockingStep(tpArdyTask);
		allSteps.add(tpSteps);

		PanelDetails chickSteps = new PanelDetails("Kill Swordchick", Arrays.asList(moveToBasement, killSwordchick),
			towerOfLife, rawSword, rawChick, combatGear, food);
		chickSteps.setDisplayCondition(notKillSwordchick);
		chickSteps.setLockingStep(killSwordchickTask);
		allSteps.add(chickSteps);

		PanelDetails fishSteps = new PanelDetails("Fishing Platform", Arrays.asList(moveToPlatform, fishOnPlatform),
			seaSlug, smallFishingNet);
		fishSteps.setDisplayCondition(notFishOnPlatform);
		fishSteps.setLockingStep(fishOnPlatformTask);
		allSteps.add(fishSteps);

		PanelDetails farmerSteps = new PanelDetails("Pickpocket Master Farmer",
			Collections.singletonList(pickMasterFarmer), new SkillRequirement(Skill.THIEVING, 38));
		farmerSteps.setDisplayCondition(notPickMasterFarmer);
		farmerSteps.setLockingStep(pickMasterFarmerTask);
		allSteps.add(farmerSteps);

		PanelDetails ibanSteps = new PanelDetails("Iban Upgrade", Collections.singletonList(ibanUpgrade),
			undergroundPass, ibanStaff, coins.quantity(200000));
		ibanSteps.setDisplayCondition(notIbanUpgrade);
		ibanSteps.setLockingStep(ibanUpgradeTask);
		allSteps.add(ibanSteps);

		PanelDetails uniSteps = new PanelDetails("Fairy Ring to Unicorn Pen", Collections.singletonList(uniPen),
			fairyTaleII, fairyAccess);
		uniSteps.setDisplayCondition(notUniPen);
		uniSteps.setLockingStep(uniPenTask);
		allSteps.add(uniSteps);

		PanelDetails necroSteps = new PanelDetails("Fairy Ring to Necromancer Tower",
			Collections.singletonList(necroTower), fairyTaleII, fairyAccess);
		necroSteps.setDisplayCondition(notNecroTower);
		necroSteps.setLockingStep(necroTowerTask);
		allSteps.add(necroSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
