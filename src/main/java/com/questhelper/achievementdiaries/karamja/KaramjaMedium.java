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
package com.questhelper.achievementdiaries.karamja;

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
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
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
		teasingStick, knife, logs, axe, tradingSticks, opal, smallFishingNet, karambwanVessel, rawKarambwanji;

	ItemRequirement spade, machete;

	ItemRequirement spiderOnAStick;

	// Items recommended
	ItemRequirement food, antipoison;

	Requirement notClaimedTicket, notEnteredWall, notUsedCart, notEarned100, notCookedSpider, notTraveledToKhazard,
		notCutTeak, notCutMahog, notCaughtKarambwan, notExchangedGems, notUsedGlider, notGrownFruitTree,
		notEnteredCrandor, notTrappedGraahk, notCutVine, notCrossedLava, notClimbedStairs, notCharteredFromShipyard,
		notMinedRedRopaz;

	Requirement agility12, cooking16, farming27, fishing65, hunter41, mining40, woodcutting50;

	Requirement grandTree, taiBwoWannaiTrio, dragonSlayerI, shiloVillage;

	QuestStep enterAgilityArena, tag2Pillars, enterVolcano, returnThroughWall, useCart, doCleanup,
		makeSpiderStick, cookSpider, climbUpToBoat, travelToKhazard, cutTeak, cutMahogany, catchKarambwan, getMachete,
		flyToKaramja, growFruitTree, trapGraahk, enterBrimhavenDungeon, chopVines, crossLava, climbBrimhavenStaircase,
		charterFromShipyard, mineRedTopaz, claimReward;

	Zone cave, agilityArena, brimhavenDungeon;

	ZoneRequirement inCave, inAgilityArena, inBrimhavenDungeon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		doMedium.addStep(new Conditions(notEnteredWall, inCave), returnThroughWall);
		doMedium.addStep(notEnteredWall, enterVolcano);
		doMedium.addStep(new Conditions(notClaimedTicket, inAgilityArena), tag2Pillars);
		doMedium.addStep(notClaimedTicket, enterAgilityArena);
		doMedium.addStep(notUsedCart, useCart);
		doMedium.addStep(notMinedRedRopaz, mineRedTopaz);
		doMedium.addStep(notTraveledToKhazard, travelToKhazard);
		doMedium.addStep(notUsedGlider, flyToKaramja);
		doMedium.addStep(notCaughtKarambwan, catchKarambwan);
		doMedium.addStep(notCharteredFromShipyard, charterFromShipyard);
		doMedium.addStep(notTrappedGraahk, trapGraahk);
		doMedium.addStep(notEarned100, doCleanup);
		doMedium.addStep(new Conditions(notCookedSpider, spiderOnAStick), cookSpider);
		doMedium.addStep(notCookedSpider, makeSpiderStick);
		doMedium.addStep(notCutTeak, cutTeak);
		doMedium.addStep(notCutMahog, cutMahogany);
		doMedium.addStep(notExchangedGems, getMachete);
		doMedium.addStep(new Conditions(notCutVine, inBrimhavenDungeon), chopVines);
		doMedium.addStep(new Conditions(notCrossedLava, inBrimhavenDungeon), crossLava);
		doMedium.addStep(new Conditions(notClimbedStairs, inBrimhavenDungeon), climbBrimhavenStaircase);
		doMedium.addStep(new Conditions(LogicType.OR, notCutVine, notCrossedLava, notClimbedStairs), enterBrimhavenDungeon);
		doMedium.addStep(notGrownFruitTree, growFruitTree);

		steps.put(0, doMedium);

		return steps;
	}

	public void setupRequirements()
	{
		notClaimedTicket = new Conditions(LogicType.NOR, new VarbitRequirement(3579, 1));
		notEnteredWall = new Conditions(LogicType.NOR, new VarbitRequirement(3580, 1));
		notEnteredCrandor = new Conditions(LogicType.NOR, new VarbitRequirement(3581, 1));
		notUsedCart = new Conditions(LogicType.NOR, new VarbitRequirement(3582, 1));
		notEarned100 = new Conditions(LogicType.NOR, new VarbitRequirement(3583, 1));
		notCookedSpider = new Conditions(LogicType.NOR, new VarbitRequirement(3584, 1));
		notMinedRedRopaz = new Conditions(LogicType.NOR, new VarbitRequirement(3585, 1));
		notCutTeak = new Conditions(LogicType.NOR, new VarbitRequirement(3586, 1));
		notCutMahog = new Conditions(LogicType.NOR, new VarbitRequirement(3587, 1));
		notCaughtKarambwan = new Conditions(LogicType.NOR, new VarbitRequirement(3588, 1));
		notExchangedGems = new Conditions(LogicType.NOR, new VarbitRequirement(3589, 1));
		notUsedGlider = new Conditions(LogicType.NOR, new VarbitRequirement(3590, 1));
		notGrownFruitTree = new Conditions(LogicType.NOR, new VarbitRequirement(3591, 1));
		notTrappedGraahk = new Conditions(LogicType.NOR, new VarbitRequirement(3592, 1));
		notCutVine = new Conditions(LogicType.NOR, new VarbitRequirement(3593, 1));
		notCrossedLava = new Conditions(LogicType.NOR, new VarbitRequirement(3594, 1));
		notClimbedStairs = new Conditions(LogicType.NOR, new VarbitRequirement(3595, 1));
		notTraveledToKhazard = new Conditions(LogicType.NOR, new VarbitRequirement(3596, 1));
		notCharteredFromShipyard = new Conditions(LogicType.NOR, new VarbitRequirement(3597, 1));

		pickaxe =
			new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(new Conditions(LogicType.OR,
				notMinedRedRopaz, notEarned100));
		coins = new ItemRequirement("Coins", ItemID.COINS_995).showConditioned(new Conditions(LogicType.OR,
			notClaimedTicket, notUsedCart, notTraveledToKhazard, notCharteredFromShipyard));
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).showConditioned(notCaughtKarambwan);

		spiderCarcass = new ItemRequirement("Spider carcass", ItemID.SPIDER_CARCASS).showConditioned(notCookedSpider);
		spiderCarcass.setTooltip("You can get one killing a spider during Tai Bwo Wannai Cleanup");
		skewerTicksOrArrowShaft =
			new ItemRequirement("Arrow shaft or skewer stick", ItemID.ARROW_SHAFT).showConditioned(notCookedSpider);
		skewerTicksOrArrowShaft.addAlternates(ItemID.SKEWER_STICK);
		goutTuber = new ItemRequirement("Gout tuber", ItemID.GOUT_TUBER).showConditioned(notExchangedGems);
		goutTuber.setTooltip("This can be obtained rarely during Tai Bwo Wannai Cleanup. Have a spade to dig it up");
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notGrownFruitTree);
		fruitTreeSapling = new ItemRequirement("A fruit tree sapling you can plant, preferably Palm Tree for the " +
			"Elite diary", ItemID.PALM_SAPLING).showConditioned(notGrownFruitTree);
		fruitTreeSapling.addAlternates(ItemID.APPLE_SAPLING, ItemID.BANANA_SAPLING, ItemID.ORANGE_SAPLING, ItemID.CURRY_SAPLING,
			ItemID.PINEAPPLE_SAPLING, ItemID.PAPAYA_SAPLING, ItemID.DRAGONFRUIT_SAPLING);
		teasingStick = new ItemRequirement("Teasing stick", ItemID.TEASING_STICK).showConditioned(notTrappedGraahk);
		teasingStick.setTooltip("You can buy one from the hunter shop in Yanille");
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notTrappedGraahk);
		logs = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notTrappedGraahk);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(new Conditions(LogicType.OR,
				notTrappedGraahk, notCrossedLava, notClimbedStairs, notCutVine));
		tradingSticks = new ItemRequirement("Trading sticks", ItemID.TRADING_STICKS).showConditioned(notExchangedGems);
		tradingSticks.setTooltip("You can get these from villagers when doing Tai Bwo Wannai Cleanup");
		opal = new ItemRequirement("Opal", ItemID.OPAL).showConditioned(notExchangedGems);
		opal.setTooltip("You can bring a jade or red topaz instead for a machete if you also bring more trading " +
			"sticks");
		karambwanVessel = new ItemRequirement("Karambwan vessel", ItemID.KARAMBWAN_VESSEL).showConditioned(notCaughtKarambwan);
		karambwanVessel.addAlternates(ItemID.KARAMBWAN_VESSEL_3159);
		rawKarambwanji = new ItemRequirement("Raw karambwanji, or a small fishing net to catch some",
			ItemID.RAW_KARAMBWANJI).showConditioned(notCaughtKarambwan);

		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(new Conditions(LogicType.OR,
			notEarned100, notGrownFruitTree));
		machete = new ItemRequirement("Machete", ItemID.RED_TOPAZ_MACHETE).showConditioned((notEarned100));
		machete.addAlternates(ItemID.JADE_MACHETE, ItemID.OPAL_MACHETE, ItemID.MACHETE);

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.getAntipoisons(), -1);

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
		shiloVillage = new QuestRequirement(QuestHelperQuest.THE_GRAND_TREE, QuestState.FINISHED);


		// 3578 = 2, completed final task
		// varplayer 2943 0->1>2>3 when done final task

		// Used skewer on spider, 2943 3->4 (VARPLAYER)

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
		cutTeak = new ObjectStep(this, ObjectID.TEAK, new WorldPoint(2822, 3078, 0), "Chop a teak tree down either in" +
			" the Hardwood Grove in Tai Bwo Wannai or in the Khazari Jungle (requires Legends' Quest started).", axe, tradingSticks.quantity(100));
		cutMahogany = new ObjectStep(this, ObjectID.MAHOGANY, new WorldPoint(2820, 3080, 0), "Chop a mahogany tree " +
			"down either in the Hardwood Grove in Tai Bwo Wannai or in the Khazari Jungle (requires Legends' Quest started).", axe,
		tradingSticks.quantity(100));
		catchKarambwan = new NpcStep(this, NpcID.FISHING_SPOT_4712, new WorldPoint(2899, 3119, 0),
			"Fish a karambwan from the north east coast of Karamja.", karambwanVessel, rawKarambwanji);
		getMachete = new NpcStep(this, NpcID.SAFTA_DOC_6423, new WorldPoint(2790, 3100, 0),
			"Get a gem machete from Safta Doc. If you want to make a red topaz one, you'll need 1200 trading sticks.",
			opal.quantity(3), tradingSticks.quantity(300));
		getMachete.addDialogSteps("What do you do here?", "Yes, I'd like to get a machete.");
		flyToKaramja = new NpcStep(this, NpcID.CAPTAIN_DALBUR, new WorldPoint(3284, 3213, 0),
			"Fly on a Gnome Glider to Karamja.");
		growFruitTree = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Grow a fruit tree in the Brimhaven patch.", fruitTreeSapling, rake, spade);
		trapGraahk = new NpcStep(this, NpcID.HORNED_GRAAHK, new WorldPoint(2770, 3003, 0),
			"Place logs over a pit north of Cairn Isle, and poke a graahk with a teasing stick. Jump over the pits " +
				"until the graahk falls in and loot it.", teasingStick, knife, logs);
		enterBrimhavenDungeon = new ObjectStep(this, ObjectID.DUNGEON_ENTRANCE_20876, new WorldPoint(2745, 3155, 0),
			"Enter Brimhaven Dungeon.", axe, coins.quantity(875));
		chopVines = new ObjectStep(this, ObjectID.VINES_21731, new WorldPoint(2690, 9564, 0),
			"Chop the vines nearby.", axe);
		crossLava = new ObjectStep(this, ObjectID.STEPPING_STONE_21738, new WorldPoint(2649, 9561, 0),
			"Cross the lava to the west of the entrance.");
		climbBrimhavenStaircase = new ObjectStep(this, ObjectID.STAIRS_21725, new WorldPoint(2637, 9515, 0),
			"Climb up to the top floor of the Brimhaven Dungeon.");
		charterFromShipyard = new NpcStep(this, NpcID.TRADER_CREWMEMBER_9349, new WorldPoint(3001, 3032, 0),
			"Travel with the charter ship in the Shipyard. Musa Point is the cheapest destination.", coins.quantity(200));
		charterFromShipyard.addDialogSteps("Glough sent me.", "Ka.", "Lu.", "Min.");
		mineRedTopaz = new ObjectStep(this, ObjectID.ROCKS_11380, new WorldPoint(2823, 2999, 0),
			"Mine gem rocks until you get a red topaz.", pickaxe);
		((ObjectStep) mineRedTopaz).addAlternateObjects(ObjectID.ROCKS_11381);

		claimReward = new NpcStep(this, NpcID.PIRATE_JACKIE_THE_FRUIT, new WorldPoint(2810, 3192, 0),
			"Talk to Pirate Jackie the Fruit in Brimhaven to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(1615), spiderCarcass, skewerTicksOrArrowShaft, goutTuber, spade, rake,
			fruitTreeSapling, teasingStick, knife, logs, axe, pickaxe, opal.quantity(3), tradingSticks.quantity(500),
			karambwanVessel, rawKarambwanji, machete);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, antipoison);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Arrays.asList(agility12, cooking16, farming27, fishing65, hunter41, mining40, woodcutting50,
		grandTree, taiBwoWannaiTrio, dragonSlayerI, shiloVillage);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Easy Diary", Arrays.asList(enterVolcano, returnThroughWall, enterAgilityArena,
			tag2Pillars, useCart, mineRedTopaz, travelToKhazard, flyToKaramja, catchKarambwan, charterFromShipyard,
			trapGraahk, doCleanup, makeSpiderStick, cookSpider, cutTeak, cutMahogany, getMachete,
			enterBrimhavenDungeon, chopVines, crossLava, climbBrimhavenStaircase, growFruitTree, claimReward),
			coins.quantity(1615), spiderCarcass, skewerTicksOrArrowShaft, goutTuber, spade, rake,
			fruitTreeSapling, teasingStick, knife, logs, axe, pickaxe, opal.quantity(3), tradingSticks.quantity(500),
			karambwanVessel, rawKarambwanji, machete));

		return allSteps;
	}
}

