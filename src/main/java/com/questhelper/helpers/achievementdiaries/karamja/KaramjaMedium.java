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
package com.questhelper.helpers.achievementdiaries.karamja;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.KARAMJA_MEDIUM
)
public class KaramjaMedium extends BasicQuestHelper
{
	// Items required
	ItemRequirement pickaxe, coins, spiderCarcass, skewerTicksOrArrowShaft, goutTuber, rake, fruitTreeSapling,
		teasingStick, knife, logs, axe, tradingSticks, opal, smallFishingNet, karambwanVessel, rawKarambwanji,
		rawKarambwanjiOrSmallFishingNet;

	ItemRequirement spade, machete;

	ItemRequirement spiderOnAStick;

	// Items recommended
	ItemRequirement food, antipoison;

	Requirement notClaimedTicket, notEnteredWall, notUsedCart, notEarned100, notCookedSpider, notTraveledToKhazard,
		notCutTeak, notCutMahog, notCaughtKarambwan, notExchangedGems, notUsedGlider, notGrownFruitTree,
		notEnteredCrandor, notTrappedGraahk, notCutVine, notCrossedLava, notClimbedStairs, notCharteredFromShipyard,
		notMinedRedRopaz;

	Requirement agility12, cooking16, farming27, fishing65, hunter41, mining40, woodcutting50;

	Requirement grandTree, taiBwoWannaiTrio, dragonSlayerI, shiloVillage, junglePotion;

	QuestStep enterAgilityArena, tag2Pillars, enterVolcano, returnThroughWall, useCart, doCleanup, makeSpiderStick, cookSpider,
		climbUpToBoat, travelToKhazard, cutTeak, cutMahogany, catchKarambwanji, catchKarambwan, getMachete, flyToKaramja, growFruitTree,
		trapGraahk, chopVines, crossLava, climbBrimhavenStaircase, charterFromShipyard, mineRedTopaz, enterCrandor,
		enterBrimDungeonVine, enterBrimDungeonLava, enterBrimDungeonStairs, claimReward;

	Zone cave, agilityArena, brimhavenDungeon;

	ZoneRequirement inCave, inAgilityArena, inBrimhavenDungeon;

	ConditionalStep claimedTicketTask, enteredWallTask, usedCartTask, earned100Task, cookedSpiderTask,
		traveledToKhazardTask, cutTeakTask, cutMahogTask, caughtKarambwanTask, exchangedGemsTask, usedGliderTask,
		grownFruitTreeTask, enteredCrandorTask, trappedGraahkTask, cutVineTask, crossedLavaTask, climbedStairsTask,
		charteredFromShipyardTask, minedRedRopazTask;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);

		grownFruitTreeTask = new ConditionalStep(this, growFruitTree);
		doMedium.addStep(notGrownFruitTree, grownFruitTreeTask);

		enteredWallTask = new ConditionalStep(this, enterVolcano);
		enteredWallTask.addStep(inCave, returnThroughWall);
		doMedium.addStep(notEnteredWall, enteredWallTask);

		enteredCrandorTask = new ConditionalStep(this, enterVolcano);
		enteredCrandorTask.addStep(inCave, enterCrandor);
		doMedium.addStep(notEnteredCrandor, enteredCrandorTask);

		claimedTicketTask = new ConditionalStep(this, enterAgilityArena);
		claimedTicketTask.addStep(inAgilityArena, tag2Pillars);
		doMedium.addStep(notClaimedTicket, claimedTicketTask);

		usedCartTask = new ConditionalStep(this, useCart);
		doMedium.addStep(notUsedCart, usedCartTask);

		minedRedRopazTask = new ConditionalStep(this, mineRedTopaz);
		doMedium.addStep(notMinedRedRopaz, minedRedRopazTask);

		traveledToKhazardTask = new ConditionalStep(this, travelToKhazard);
		doMedium.addStep(notTraveledToKhazard, traveledToKhazardTask);

		usedGliderTask = new ConditionalStep(this, flyToKaramja);
		doMedium.addStep(notUsedGlider, usedGliderTask);

		caughtKarambwanTask = new ConditionalStep(this, catchKarambwan);
		caughtKarambwanTask.addStep(rawKarambwanji.alsoCheckBank(questBank), catchKarambwanji);
		doMedium.addStep(notCaughtKarambwan, caughtKarambwanTask);

		charteredFromShipyardTask = new ConditionalStep(this, charterFromShipyard);
		doMedium.addStep(notCharteredFromShipyard, charteredFromShipyardTask);

		trappedGraahkTask = new ConditionalStep(this, trapGraahk);
		doMedium.addStep(notTrappedGraahk, trappedGraahkTask);

		earned100Task = new ConditionalStep(this, doCleanup);
		doMedium.addStep(notEarned100, earned100Task);

		cookedSpiderTask = new ConditionalStep(this, makeSpiderStick);
		cookedSpiderTask.addStep(spiderOnAStick, cookSpider);
		doMedium.addStep(notCookedSpider, cookedSpiderTask);

		cutTeakTask = new ConditionalStep(this, cutTeak);
		doMedium.addStep(notCutTeak, cutTeakTask);

		cutMahogTask = new ConditionalStep(this, cutMahogany);
		doMedium.addStep(notCutMahog, cutMahogTask);

		exchangedGemsTask = new ConditionalStep(this, getMachete);
		doMedium.addStep(notExchangedGems, exchangedGemsTask);

		cutVineTask = new ConditionalStep(this, enterBrimDungeonVine);
		cutVineTask.addStep(inBrimhavenDungeon, chopVines);
		doMedium.addStep(notCutVine, cutVineTask);

		crossedLavaTask = new ConditionalStep(this, enterBrimDungeonLava);
		crossedLavaTask.addStep(inBrimhavenDungeon, crossLava);
		doMedium.addStep(notCrossedLava, crossedLavaTask);

		climbedStairsTask = new ConditionalStep(this, enterBrimDungeonStairs);
		climbedStairsTask.addStep(inBrimhavenDungeon, climbBrimhavenStaircase);
		doMedium.addStep(notClimbedStairs, climbedStairsTask);

		steps.put(0, doMedium);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		notClaimedTicket = new VarbitRequirement(3579, 0);
		notEnteredWall = new VarbitRequirement(3580, 0);
		notEnteredCrandor = new VarbitRequirement(3581, 0);
		notUsedCart = new VarbitRequirement(3582, 0);
		notEarned100 = new VarbitRequirement(3583, 0);
		notCookedSpider = new VarbitRequirement(3584, 0);
		notMinedRedRopaz = new VarbitRequirement(3585, 0);
		notCutTeak = new VarbitRequirement(3586, 0);
		notCutMahog = new VarbitRequirement(3587, 0);
		notCaughtKarambwan = new VarbitRequirement(3588, 0);
		notExchangedGems = new VarbitRequirement(3589, 0);
		notUsedGlider = new VarbitRequirement(3590, 0);
		notGrownFruitTree = new VarbitRequirement(3591, 0);
		notTrappedGraahk = new VarbitRequirement(3592, 0);
		notCutVine = new VarbitRequirement(3593, 0);
		notCrossedLava = new VarbitRequirement(3594, 0);
		notClimbedStairs = new VarbitRequirement(3595, 0);
		notTraveledToKhazard = new VarbitRequirement(3596, 0);
		notCharteredFromShipyard = new VarbitRequirement(3597, 0);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES)
			.showConditioned(new Conditions(LogicType.OR, notMinedRedRopaz, notEarned100)).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS)
			.showConditioned(new Conditions(LogicType.OR, notClaimedTicket, notUsedCart, notTraveledToKhazard, notCharteredFromShipyard));
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).showConditioned(notCaughtKarambwan).isNotConsumed();

		spiderCarcass = new ItemRequirement("Spider carcass", ItemID.SPIDER_CARCASS).showConditioned(notCookedSpider);
		spiderCarcass.setTooltip("You can get one killing a spider during Tai Bwo Wannai Cleanup");
		skewerTicksOrArrowShaft = new ItemRequirement("Arrow shaft or skewer stick", ItemID.ARROW_SHAFT).showConditioned(notCookedSpider);
		skewerTicksOrArrowShaft.addAlternates(ItemID.SKEWER_STICK);
		goutTuber = new ItemRequirement("Gout tuber", ItemID.GOUT_TUBER).showConditioned(notExchangedGems);
		goutTuber.setTooltip("This can be obtained rarely during Tai Bwo Wannai Cleanup. Have a spade to dig it up");
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notGrownFruitTree).isNotConsumed();
		fruitTreeSapling = new ItemRequirement("A fruit tree sapling you can plant, preferably Palm Tree for the " +
			"Elite diary", ItemID.PALM_SAPLING).showConditioned(notGrownFruitTree);
		fruitTreeSapling.addAlternates(ItemID.APPLE_SAPLING, ItemID.BANANA_SAPLING, ItemID.ORANGE_SAPLING, ItemID.CURRY_SAPLING,
			ItemID.PINEAPPLE_SAPLING, ItemID.PAPAYA_SAPLING, ItemID.DRAGONFRUIT_SAPLING);
		teasingStick = new ItemRequirement("Teasing stick", ItemID.TEASING_STICK).showConditioned(notTrappedGraahk).isNotConsumed();
		teasingStick.setTooltip("You can buy one from the hunter shop in Yanille");
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notTrappedGraahk).isNotConsumed();
		logs = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notTrappedGraahk);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(new Conditions(LogicType.OR,
			notTrappedGraahk, notCrossedLava, notClimbedStairs, notCutVine, notCutTeak, notCutMahog)).isNotConsumed();
		tradingSticks = new ItemRequirement("Trading sticks", ItemID.TRADING_STICKS).showConditioned(notExchangedGems);
		tradingSticks.setTooltip("You can get these from villagers when doing Tai Bwo Wannai Cleanup");
		opal = new ItemRequirement("Opal", ItemID.OPAL).showConditioned(notExchangedGems);
		opal.setTooltip("You can bring a jade or red topaz instead for a machete if you also bring more trading sticks");
		karambwanVessel = new ItemRequirement("Karambwan vessel", ItemID.KARAMBWAN_VESSEL).showConditioned(notCaughtKarambwan).isNotConsumed();
		karambwanVessel.addAlternates(ItemID.KARAMBWAN_VESSEL_3159);
		rawKarambwanji = new ItemRequirement("Raw karambwanji",
		ItemID.RAW_KARAMBWANJI).showConditioned(notCaughtKarambwan);
		rawKarambwanjiOrSmallFishingNet = new ItemRequirement("Raw karambwanji, or a small fishing net to catch some",
			ItemID.RAW_KARAMBWANJI).showConditioned(notCaughtKarambwan);
		rawKarambwanjiOrSmallFishingNet.addAlternates(ItemID.SMALL_FISHING_NET);

		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(new Conditions(LogicType.OR,
			notEarned100, notGrownFruitTree)).isNotConsumed();
		machete = new ItemRequirement("Machete", ItemID.RED_TOPAZ_MACHETE).showConditioned((notEarned100)).isNotConsumed();
		machete.addAlternates(ItemID.JADE_MACHETE, ItemID.OPAL_MACHETE, ItemID.MACHETE);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS, -1);

		spiderOnAStick = new ItemRequirement("Spider on stick", ItemID.SPIDER_ON_STICK);
		spiderOnAStick.setTooltip("You can get one by using a spider carcass on an arrow shaft");

		agility12 = new SkillRequirement(Skill.AGILITY, 12);
		cooking16 = new SkillRequirement(Skill.COOKING, 16);
		farming27 = new SkillRequirement(Skill.FARMING, 27);
		fishing65 = new SkillRequirement(Skill.FISHING, 65, true);
		hunter41 = new SkillRequirement(Skill.HUNTER, 41);
		mining40 = new SkillRequirement(Skill.MINING, 40);
		woodcutting50 = new SkillRequirement(Skill.WOODCUTTING, 50);

		grandTree = new QuestRequirement(QuestHelperQuest.THE_GRAND_TREE, QuestState.FINISHED);
		taiBwoWannaiTrio = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED, "Partial " +
			"completion of Tai Bwo Wannai Trio to fish karambwan");
		dragonSlayerI = new QuestRequirement(QuestHelperQuest.DRAGON_SLAYER_I, QuestState.FINISHED, "Partial " +
			"completion of Dragon Slayer I for access to Crandor");
		shiloVillage = new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED);
		junglePotion = new QuestRequirement(QuestHelperQuest.JUNGLE_POTION, QuestState.FINISHED);

		inCave = new ZoneRequirement(cave);
		inAgilityArena = new ZoneRequirement(agilityArena);
		inBrimhavenDungeon = new ZoneRequirement(brimhavenDungeon);
	}

	public void loadZones()
	{
		cave = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		agilityArena = new Zone(new WorldPoint(2747, 9531, 0), new WorldPoint(2813, 9601, 3));
		brimhavenDungeon = new Zone(new WorldPoint(2560, 9411, 0), new WorldPoint(2752, 9599, 2));
	}

	public void setupSteps()
	{
		enterAgilityArena = new ObjectStep(this, ObjectID.LADDER_3617, new WorldPoint(2809, 3194, 0), "Pay Cap'n Izzy" +
			" No Beard 200 coins and enter the Agility Arena in Brimhaven.",
			coins.quantity(200));
		tag2Pillars = new DetailedQuestStep(this, "Tag 2 marked pillars in a row.");
		enterVolcano = new ObjectStep(this, ObjectID.ROCKS_11441, new WorldPoint(2857, 3169, 0),
			"Enter the Karamja Volcano.");
		returnThroughWall = new ObjectStep(this, ObjectID.WALL_2606, new WorldPoint(2836, 9600, 0), "Return back through the shortcut.");
		useCart = new ObjectStep(this, ObjectID.TRAVEL_CART, new WorldPoint(2778, 3212, 0),
			"Travel on the cart north of Brimhaven.", coins.quantity(200));
		doCleanup = new DetailedQuestStep(this, "Talk to Murcaily in the east of Tai Bwo Wannai to start up the " +
			"cleanup, then chop the light jungle and repair the rotten fences until 100% favour. Make sure you dig up" +
			" any gout tuber and mine any gem rocks that may appear.", machete, spade, pickaxe, antipoison);
		makeSpiderStick = new DetailedQuestStep(this, "Make a spider on stick by using a spider carcass on an arrow " +
			"shaft.", spiderCarcass.highlighted(), skewerTicksOrArrowShaft.highlighted());
		cookSpider = new DetailedQuestStep(this, "Cook a spider on a stick on a fire/range.", spiderOnAStick);
		climbUpToBoat = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2763, 2952, 0),
			"Charter the Lady of the Waves from south of Cairn Isle to Port Khazard.", coins.quantity(50));
		travelToKhazard = new NpcStep(this, NpcID.CAPTAIN_SHANKS, new WorldPoint(2761, 2959, 1),
			"Charter the Lady of the Waves from south of Cairn Isle to Port Khazard.", coins.quantity(50));
		travelToKhazard.addDialogSteps("Yes, I'll buy a ticket for the ship.", "Khazard Port please.");
		travelToKhazard.addSubSteps(climbUpToBoat);
		cutTeak = new ObjectStep(this, ObjectID.TEAK_TREE, new WorldPoint(2822, 3078, 0), "Chop a teak tree down either in" +
			" the Hardwood Grove in Tai Bwo Wannai or in the Kharazi Jungle (requires Legends' Quest started).", axe, tradingSticks.quantity(100));
		cutMahogany = new ObjectStep(this, ObjectID.MAHOGANY, new WorldPoint(2820, 3080, 0), "Chop a mahogany tree " +
			"down either in the Hardwood Grove in Tai Bwo Wannai or in the Kharazi Jungle (requires Legends' Quest started).", axe,
			tradingSticks.quantity(100));
		catchKarambwanji = new NpcStep(this, NpcID.FISHING_SPOT_4710, new WorldPoint(2791,3019,0),
			"Using your small fishing net, catch some raw karambwanji just south of Tai Bwo Wannai, or buy some from the GE.", smallFishingNet);
		catchKarambwan = new NpcStep(this, NpcID.FISHING_SPOT_4712, new WorldPoint(2899, 3119, 0),
			"Fish a karambwan from the north east coast of Karamja.", karambwanVessel, rawKarambwanji);
		getMachete = new NpcStep(this, NpcID.SAFTA_DOC_6423, new WorldPoint(2790, 3100, 0),
			"Get a gem machete from Safta Doc. If you want to make a red topaz one, you'll need 1200 trading sticks.",
			goutTuber, opal.quantity(3), tradingSticks.quantity(300));
		getMachete.addDialogSteps("What do you do here?", "Yes, I'd like to get a machete.");
		flyToKaramja = new NpcStep(this, NpcID.CAPTAIN_DALBUR, new WorldPoint(3284, 3213, 0),
			"Fly on a Gnome Glider to Karamja.");
		growFruitTree = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Grow a fruit tree in the Brimhaven patch. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			fruitTreeSapling, rake, spade);
		trapGraahk = new NpcStep(this, NpcID.HORNED_GRAAHK, new WorldPoint(2770, 3003, 0),
			"Place logs over a pit north of Cairn Isle, and poke a graahk with a teasing stick. Jump over the pits " +
				"until the graahk falls in and loot it.", teasingStick, knife, logs);
		enterBrimDungeonVine = new ObjectStep(this, ObjectID.DUNGEON_ENTRANCE_20876, new WorldPoint(2745, 3155, 0),
			"Enter Brimhaven Dungeon.", axe, coins.quantity(875));
		enterBrimDungeonLava = new ObjectStep(this, ObjectID.DUNGEON_ENTRANCE_20876, new WorldPoint(2745, 3155, 0),
			"Enter Brimhaven Dungeon.", coins.quantity(875));
		enterBrimDungeonStairs = new ObjectStep(this, ObjectID.DUNGEON_ENTRANCE_20876, new WorldPoint(2745, 3155, 0),
			"Enter Brimhaven Dungeon.", coins.quantity(875));
		chopVines = new ObjectStep(this, ObjectID.VINES_21731, new WorldPoint(2690, 9564, 0),
			"Chop the vines nearby.", axe);
		crossLava = new ObjectStep(this, ObjectID.STEPPING_STONE_21738, new WorldPoint(2649, 9561, 0),
			"Cross the lava to the west of the entrance.");
		climbBrimhavenStaircase = new ObjectStep(this, ObjectID.STAIRS_21725, new WorldPoint(2637, 9515, 0),
			"Climb up to the top floor of the Brimhaven Dungeon.");
		charterFromShipyard = new NpcStep(this, NpcID.TRADER_CREWMEMBER_9349, new WorldPoint(3001, 3032, 0),
			"Travel with the charter ship in the Shipyard. Musa Point is the cheapest destination.", coins.quantity(200));
		charterFromShipyard.addDialogSteps("Glough sent me.", "Ka.", "Lu.", "Min.");
		mineRedTopaz = new ObjectStep(this, ObjectID.GEM_ROCKS, new WorldPoint(2823, 2999, 0),
			"Mine gem rocks until you get a red topaz.", pickaxe);
		((ObjectStep) mineRedTopaz).addAlternateObjects(ObjectID.GEM_ROCKS_11381);
		enterCrandor = new ObjectStep(this, ObjectID.CLIMBING_ROPE_25213, new WorldPoint(2833, 9657, 0),
			"Climb the rope to Crandor Isle.");

		claimReward = new NpcStep(this, NpcID.PIRATE_JACKIE_THE_FRUIT, new WorldPoint(2810, 3192, 0),
			"Talk to Pirate Jackie the Fruit in Brimhaven to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(1615), spiderCarcass, skewerTicksOrArrowShaft, goutTuber, spade, rake,
			fruitTreeSapling, teasingStick, knife, logs, axe, pickaxe, opal.quantity(3), tradingSticks.quantity(500),
			karambwanVessel, rawKarambwanjiOrSmallFishingNet, machete);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, antipoison);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();

		reqs.add(new SkillRequirement(Skill.AGILITY, 12));
		reqs.add(new SkillRequirement(Skill.COOKING, 16));
		reqs.add(new SkillRequirement(Skill.FARMING, 27));
		reqs.add(new SkillRequirement(Skill.FISHING, 65, true));
		reqs.add(new SkillRequirement(Skill.HUNTER, 41));
		reqs.add(new SkillRequirement(Skill.MINING, 40));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 50));

		reqs.add(new QuestRequirement(QuestHelperQuest.THE_GRAND_TREE, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED, "Partial " +
			"completion of Tai Bwo Wannai Trio to fish karambwan"));
		reqs.add(new QuestRequirement(QuestHelperQuest.DRAGON_SLAYER_I, QuestState.FINISHED, "Partial " +
			"completion of Dragon Slayer I for access to Crandor"));
		reqs.add(new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED));

		return reqs;
	}

	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Karamja Gloves (2)", ItemID.KARAMJA_GLOVES_2, 1),
			new ItemReward("5,000 Exp. Lamp (Any skill above level 30)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Increased Agility Experience when redeeming Agility tickets"),
			new UnlockReward("10% increased Agility experience earned from Brimhaven Agility Arena"),
			new UnlockReward("Access to the stepping stone shortcut across the river of Shilo Village"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails growFruitTreeSteps = new PanelDetails("Grow a Healthy Fruit Tree",
			Collections.singletonList(growFruitTree), farming27, fruitTreeSapling, rake, spade);
		growFruitTreeSteps.setDisplayCondition(notGrownFruitTree);
		growFruitTreeSteps.setLockingStep(grownFruitTreeTask);
		allSteps.add(growFruitTreeSteps);

		PanelDetails enteredWallSteps = new PanelDetails("Discover Hidden Wall in Volcano",
			Arrays.asList(enterVolcano, returnThroughWall), dragonSlayerI);
		enteredWallSteps.setDisplayCondition(notEnteredWall);
		enteredWallSteps.setLockingStep(enteredWallTask);
		allSteps.add(enteredWallSteps);

		PanelDetails enteredCrandorSteps = new PanelDetails("Enter Crandor",
			Arrays.asList(enterVolcano, enterCrandor), dragonSlayerI, food);
		enteredCrandorSteps.setDisplayCondition(notEnteredCrandor);
		enteredCrandorSteps.setLockingStep(enteredCrandorTask);
		allSteps.add(enteredCrandorSteps);

		PanelDetails enterAgiSteps = new PanelDetails("Claim a ticket in The Agility Arena",
			Arrays.asList(enterAgilityArena, tag2Pillars), coins.quantity(200));
		enterAgiSteps.setDisplayCondition(notClaimedTicket);
		enterAgiSteps.setLockingStep(claimedTicketTask);
		allSteps.add(enterAgiSteps);

		PanelDetails usedCartSteps = new PanelDetails("Use Vigroy & Hajedy's Cart Service",
			Collections.singletonList(useCart), shiloVillage, coins.quantity(200));
		usedCartSteps.setDisplayCondition(notUsedCart);
		usedCartSteps.setLockingStep(usedCartTask);
		allSteps.add(usedCartSteps);

		PanelDetails mineRedTopazSteps = new PanelDetails("Mine Red Topaz", Collections.singletonList(mineRedTopaz),
			shiloVillage, mining40, pickaxe);
		mineRedTopazSteps.setDisplayCondition(notMinedRedRopaz);
		mineRedTopazSteps.setLockingStep(minedRedRopazTask);
		allSteps.add(mineRedTopazSteps);

		PanelDetails travelToKhazardSteps = new PanelDetails("Travel to Port Khazard",
			Collections.singletonList(travelToKhazard), shiloVillage, coins.quantity(50));
		travelToKhazardSteps.setDisplayCondition(notTraveledToKhazard);
		travelToKhazardSteps.setLockingStep(traveledToKhazardTask);
		allSteps.add(travelToKhazardSteps);

		PanelDetails useGnomeGliderSteps = new PanelDetails("Use Gnome Glider",
			Collections.singletonList(flyToKaramja), grandTree);
		useGnomeGliderSteps.setDisplayCondition(notUsedGlider);
		useGnomeGliderSteps.setLockingStep(usedGliderTask);
		allSteps.add(useGnomeGliderSteps);

		PanelDetails catchAKarambwanSteps = new PanelDetails("Catch a Karambwan",
			Collections.singletonList(catchKarambwan), fishing65, taiBwoWannaiTrio, karambwanVessel, rawKarambwanjiOrSmallFishingNet);
		catchAKarambwanSteps.setDisplayCondition(notCaughtKarambwan);
		catchAKarambwanSteps.setLockingStep(caughtKarambwanTask);
		allSteps.add(catchAKarambwanSteps);

		PanelDetails charteredShipyardSteps = new PanelDetails("Charter a Ship in The Shipyard",
			Collections.singletonList(charterFromShipyard), grandTree, coins.quantity(200));
		charteredShipyardSteps.setDisplayCondition(notCharteredFromShipyard);
		charteredShipyardSteps.setLockingStep(charteredFromShipyardTask);
		allSteps.add(charteredShipyardSteps);

		PanelDetails trapAHornedGraahkSteps = new PanelDetails("Trap a Horned Graahk",
			Collections.singletonList(trapGraahk), hunter41, teasingStick, knife, logs);
		trapAHornedGraahkSteps.setDisplayCondition(notTrappedGraahk);
		trapAHornedGraahkSteps.setLockingStep(trappedGraahkTask);
		allSteps.add(trapAHornedGraahkSteps);

		PanelDetails earn100Steps = new PanelDetails("Earn 100% Favour in Tai Bwo Wannai Cleanup",
			Collections.singletonList(doCleanup), junglePotion, machete, spade, pickaxe, antipoison);
		earn100Steps.setDisplayCondition(notEarned100);
		earn100Steps.setLockingStep(earned100Task);
		allSteps.add(earn100Steps);

		PanelDetails spiderOnStickSteps = new PanelDetails("Cook a Spider on Stick", Arrays.asList(makeSpiderStick,
			cookSpider), cooking16, spiderCarcass, skewerTicksOrArrowShaft);
		spiderOnStickSteps.setDisplayCondition(notCookedSpider);
		spiderOnStickSteps.setLockingStep(cookedSpiderTask);
		allSteps.add(spiderOnStickSteps);

		PanelDetails cutATeakTreeSteps = new PanelDetails("Cut a Teak Tree", Collections.singletonList(cutTeak),
			new SkillRequirement(Skill.WOODCUTTING, 35),
			junglePotion, axe, tradingSticks.quantity(100));
		cutATeakTreeSteps.setDisplayCondition(notCutTeak);
		cutATeakTreeSteps.setLockingStep(cutTeakTask);
		allSteps.add(cutATeakTreeSteps);

		PanelDetails cutAMahoganyTreeSteps = new PanelDetails("Cut a Mahogany Tree",
			Collections.singletonList(cutMahogany), junglePotion, woodcutting50, axe, tradingSticks.quantity(100));
		cutAMahoganyTreeSteps.setDisplayCondition(notCutMahog);
		cutAMahoganyTreeSteps.setLockingStep(cutMahogTask);
		allSteps.add(cutAMahoganyTreeSteps);

		PanelDetails exchangeGemsWithSaftaDocSteps = new PanelDetails("Exchange Gems with Safta Doc", Collections.singletonList(getMachete),
			junglePotion, goutTuber, opal.quantity(3), tradingSticks.quantity(300));
		exchangeGemsWithSaftaDocSteps.setDisplayCondition(notExchangedGems);
		exchangeGemsWithSaftaDocSteps.setLockingStep(exchangedGemsTask);
		allSteps.add(exchangeGemsWithSaftaDocSteps);

		PanelDetails chopVinesSteps = new PanelDetails("Chop Vines in Brimhaven Dungeon",
			Arrays.asList(enterBrimDungeonVine, chopVines), new SkillRequirement(Skill.WOODCUTTING, 10), axe,
			coins.quantity(875));
		chopVinesSteps.setDisplayCondition(notCutVine);
		chopVinesSteps.setLockingStep(cutVineTask);
		allSteps.add(chopVinesSteps);

		PanelDetails crossLavaSteps = new PanelDetails("Cross The Lava in Brimhaven Dungeon",
			Arrays.asList(enterBrimDungeonLava, crossLava), agility12, axe, coins.quantity(875));
		crossLavaSteps.setDisplayCondition(notCrossedLava);
		crossLavaSteps.setLockingStep(crossedLavaTask);
		allSteps.add(crossLavaSteps);

		PanelDetails climbStairsSteps = new PanelDetails("Climb The Stairs in Brimhaven Dungeon",
			Arrays.asList(enterBrimDungeonStairs, climbBrimhavenStaircase), new SkillRequirement(Skill.WOODCUTTING, 10),
			axe, coins.quantity(875));
		climbStairsSteps.setDisplayCondition(notClimbedStairs);
		climbStairsSteps.setLockingStep(climbedStairsTask);
		allSteps.add(climbStairsSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}

