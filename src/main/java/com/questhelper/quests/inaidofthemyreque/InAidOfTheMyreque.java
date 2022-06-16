/*
 * Copyright (c) 2020, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.inaidofthemyreque;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
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

@QuestDescriptor(
	quest = QuestHelperQuest.IN_AID_OF_THE_MYREQUE
)
public class InAidOfTheMyreque extends BasicQuestHelper
{
	//Items Required
	ItemRequirement food, spade, bucketTo5, pickaxe, hammer, planks11, nails44, swampPaste, rawMackerelOrSnail10, bronzeAxes10, tinderboxes4, steelBars2,
		coal, softClay, rope, silverBar, mithrilBar, sapphire, cosmicRune, waterRune, efaritaysAidOrSilverWeapon, bucketOrSemiFilledBucket, tinderbox,
		steelBars2Highlighted, coalHiglighted, tinderboxHighlighted, planks3, planks2, planks6, nails12, nails8, crate, tinderbox3, snails10, mackerel10,
		nails24, planks5, nails20, templeLibraryKey, sleepingSeven, hammerHighlighted, mould, silvRod, softClayHighlighted, enchantedRod, rodOfIvandis,
		enchantedRodHighlighted, foodForChest;

	//Items Recommended
	ItemRequirement steelmed, steelChain, steelLegs, silverSickle, morttonTeleport, canifisTeleport;

	Requirement normalSpellbook;

	Requirement onEntranceIsland, inCaves, inMyrequeCave, inBoatArea, inNewBase, onRoof, filledCrate, addedCoal, litFurnace, talkedToGadderanks, talkedToJuvinates,
		talkedToWiskit, inGadderanksFight, defeatedGadderanks, veliafReturnedToBase, inTempleTrekArea, inTempleTrekArea2, inTemple, libraryOpen, inTempleLibrary,
		inCoffinRoom, boardsRemoved;

	QuestStep climbDownCanifis, enterMyrequeCave, talkToVeliaf;

	QuestStep leaveMyrequeCave, leaveCavesIntoSwamp, goOverBridge, takeBoatToMortton, talkToFlorin, putFoodInChest, talkToRazvan, clearTrapdoorRubble,
		enterBurghPubBasement, clearBasementRubble, leavePubBasement;

	QuestStep talkToAurel, climbShopLadder, fixRoof, climbDownShopLadder, fixShopWall, talkToAurelForCrate, fillCrate, talkToAurelWithCrate;

	QuestStep repairBooth, repairBankWall, talkToCornelius;

	QuestStep talkToRazvanAfterRepairs, repairFurnace, addCoalToFurnace, lightFurnace, talkToGadderanks, talkToJuvinate, talkToWiskit, killGadderanksAndJuvinates, talkToGadderanksAgain,
		talkToVeliafAfterFight;

	QuestStep talkToVeliafInHideoutAgain, talkToPolmafi, talkToIvanForTrek, killJuvinates, killJuvinates2, goDownToDrezel, talkToDrezel, useKeyOnHole, enterLibrary, searchBookcase,
		readBook;

	QuestStep useHammerOnBoards, enterCoffinRoom, useClayOnCoffin, makeRod, enchantRod, useRodOnWell, talkToVeliafToFinish;

	ConditionalStep startQuest, travelToBurgh, returnToHideout, goTalkToPolmafi, travelWithIvan, goIntoCavesAgain, goBlessRod, finishQuest;

	//Zones
	Zone entranceIsland, caves, myrequeCave, boatArea, newBase, roof, gadderanksFightArea, templeTrekArea, templeTrekArea2, temple, templeLibrary, coffinRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);
		steps.put(20, travelToBurgh);
		steps.put(30, putFoodInChest);
		steps.put(40, talkToRazvan); // Can skip talking to Razvan
		steps.put(50, talkToRazvan);
		steps.put(60, clearTrapdoorRubble);
		steps.put(70, clearTrapdoorRubble);

		steps.put(80, enterBurghPubBasement);
		steps.put(90, enterBurghPubBasement); // Opened trapdoor

		ConditionalStep clearBasement = new ConditionalStep(this, enterBurghPubBasement);
		clearBasement.addStep(inNewBase, clearBasementRubble);
		steps.put(100, clearBasement);

		ConditionalStep goTalkToRazAfterClearing = new ConditionalStep(this, talkToAurel);
		goTalkToRazAfterClearing.addStep(inNewBase, leavePubBasement);
		steps.put(110, goTalkToRazAfterClearing);

		ConditionalStep goRepairRoof = new ConditionalStep(this, climbShopLadder);
		goRepairRoof.addStep(onRoof, fixRoof);
		steps.put(120, goRepairRoof);
		steps.put(130, goRepairRoof);
		steps.put(140, goRepairRoof);

		ConditionalStep goRepairShopWall = new ConditionalStep(this, fixShopWall);
		goRepairShopWall.addStep(onRoof, climbDownShopLadder);
		steps.put(150, goRepairShopWall);

		steps.put(160, talkToAurelForCrate);

		ConditionalStep goFillCrate = new ConditionalStep(this, fillCrate);
		goFillCrate.addStep(filledCrate, talkToAurelWithCrate);
		steps.put(165, goFillCrate);

		steps.put(170, repairBooth);
		steps.put(180, repairBankWall);
		steps.put(190, talkToCornelius);

		steps.put(200, talkToRazvanAfterRepairs);

		steps.put(205, repairFurnace);
		ConditionalStep goStartFurnace = new ConditionalStep(this, addCoalToFurnace);
		goStartFurnace.addStep(addedCoal, lightFurnace);
		steps.put(210, goStartFurnace);

		steps.put(220, lightFurnace);

		ConditionalStep investigateShop = new ConditionalStep(this, talkToGadderanks);
		investigateShop.addStep(new Conditions(talkedToGadderanks, talkedToJuvinates), talkToWiskit);
		investigateShop.addStep(talkedToGadderanks, talkToJuvinate);
		steps.put(230, investigateShop);

		ConditionalStep defeatGadderanks = new ConditionalStep(this, talkToGadderanks);
		defeatGadderanks.addStep(new Conditions(inGadderanksFight, defeatedGadderanks), talkToGadderanks);
		defeatGadderanks.addStep(inGadderanksFight, killGadderanksAndJuvinates);
		steps.put(240, defeatGadderanks);
		steps.put(250, defeatGadderanks); // Veliaf appeared to help
		steps.put(260, defeatGadderanks); // Defeated him once. If you leave still need to fight again

		ConditionalStep goBackToHollow = new ConditionalStep(this, talkToVeliafAfterFight);
		goBackToHollow.addStep(veliafReturnedToBase, returnToHideout);
		steps.put(280, goBackToHollow);

		steps.put(290, goTalkToPolmafi);

		steps.put(300, travelWithIvan);
		steps.put(310, travelWithIvan);

		ConditionalStep goTalkToDrezel = new ConditionalStep(this, goDownToDrezel);
		goTalkToDrezel.addStep(inTemple, talkToDrezel);
		steps.put(315, goTalkToDrezel);
		steps.put(320, goTalkToDrezel);
		steps.put(330, goTalkToDrezel);
		steps.put(340, goTalkToDrezel);

		ConditionalStep unlockLibrary = new ConditionalStep(this, goDownToDrezel);
		unlockLibrary.addStep(inTemple, useKeyOnHole);
		steps.put(350, unlockLibrary);

		ConditionalStep goReadBook = new ConditionalStep(this, goDownToDrezel);
		goReadBook.addStep(sleepingSeven, readBook);
		goReadBook.addStep(inTempleLibrary, searchBookcase);
		goReadBook.addStep(inTemple, enterLibrary);
		steps.put(360, goReadBook);
		steps.put(370, goReadBook);

		ConditionalStep goMakeRod = new ConditionalStep(this, goIntoCavesAgain);
		goMakeRod.addStep(enchantedRod, goBlessRod);
		goMakeRod.addStep(silvRod, enchantRod);
		goMakeRod.addStep(mould, makeRod);
		goMakeRod.addStep(inCoffinRoom, useClayOnCoffin);
		goMakeRod.addStep(new Conditions(inCaves, boardsRemoved), enterCoffinRoom);
		goMakeRod.addStep(inCaves, useHammerOnBoards);
		steps.put(375, goMakeRod);
		steps.put(380, goMakeRod);
		steps.put(390, goMakeRod);
		steps.put(400, goMakeRod);
		steps.put(410, goMakeRod);

		steps.put(420, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		food = new ItemRequirement("Any food", ItemCollections.GOOD_EATING_FOOD);
		foodForChest = new ItemRequirement("Food to put in a chest, multiple pieces in case a Ghast eats some",
			ItemCollections.GOOD_EATING_FOOD);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		bucketTo5 = new ItemRequirement("buckets (Can use 1 but is much slower)", ItemID.BUCKET, 5);
		bucketOrSemiFilledBucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucketOrSemiFilledBucket.addAlternates(ItemID.BUCKET_OF_RUBBLE, ItemID.BUCKET_OF_RUBBLE_7624);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		planks11 = new ItemRequirement("Plank", ItemID.PLANK, 11);
		nails44 = new ItemRequirement("Any nails", ItemCollections.NAILS, 44);
		swampPaste = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE);
		snails10 = new ItemRequirement("Raw snail", ItemID.THIN_SNAIL_MEAT);
		snails10.addAlternates(ItemID.FAT_SNAIL_MEAT, ItemID.LEAN_SNAIL_MEAT);
		mackerel10 = new ItemRequirement("Raw mackerel", ItemID.RAW_MACKEREL, 10);
		rawMackerelOrSnail10 = new ItemRequirements("10 Raw mackerel or raw snail meat (random for each player)",
			mackerel10,
			snails10);

		bronzeAxes10 = new ItemRequirement("Bronze axe", ItemID.BRONZE_AXE, 10);
		tinderboxes4 = new ItemRequirement("Tinderbox", ItemID.TINDERBOX, 4);
		tinderbox3 = new ItemRequirement("Tinderbox", ItemID.TINDERBOX, 3);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlighted = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlighted.setHighlightInInventory(true);

		steelBars2 = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 2);
		steelBars2Highlighted = new ItemRequirement("Steel bar", ItemID.STEEL_BAR, 2);
		steelBars2Highlighted.setHighlightInInventory(true);
		coal = new ItemRequirement("Coal", ItemID.COAL);
		coalHiglighted = new ItemRequirement("Coal", ItemID.COAL);
		coalHiglighted.setHighlightInInventory(true);

		efaritaysAidOrSilverWeapon = new ItemRequirement("Silver weapon (including Silverlight + varieties), blessed " +
			"axe or Efaritay's Aid to damage vampyres", ItemID.ARCLIGHT);
		efaritaysAidOrSilverWeapon.addAlternates(ItemID.DARKLIGHT, ItemID.SILVERLIGHT, ItemID.SILVERLIGHT_6745,
			ItemID.SILVERLIGHT_8279, ItemID.SILVER_SICKLE_B, ItemID.SILVER_SICKLE, ItemID.EFARITAYS_AID, ItemID.WOLFBANE);

		softClay = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		silverBar = new ItemRequirement("Silver bar", ItemID.SILVER_BAR);
		mithrilBar = new ItemRequirement("Mithril bar", ItemID.MITHRIL_BAR);
		sapphire = new ItemRequirement("Sapphire", ItemID.SAPPHIRE);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE);

		steelmed = new ItemRequirement("Steel med helm", ItemID.STEEL_MED_HELM);
		steelmed.setTooltip("You can give this to Ivan just before accompanying him through the swamp to make him " +
			"stronger");
		steelChain = new ItemRequirement("Steel chainbody", ItemID.STEEL_CHAINBODY);
		steelChain.setTooltip("You can give this to Ivan just before accompanying him through the swamp to make him " +
			"stronger");
		steelLegs = new ItemRequirement("Steel Platelegs", ItemID.STEEL_PLATELEGS);
		steelLegs.setTooltip("You can give this to Ivan just before accompanying him through the swamp to make him " +
			"stronger");
		silverSickle = new ItemRequirement("Silver sickle", ItemID.SILVER_SICKLE);
		silverSickle.setTooltip("You can give this to Ivan just before accompanying him through the swamp to make him " +
			"stronger");

		morttonTeleport = new ItemRequirement("Teleports to Mort'ton (minigame tele, teleport scroll)", ItemID.MORTTON_TELEPORT);
		canifisTeleport = new ItemRequirement("Canifis teleports (ancients spell, nearby fairy ring bip)", ItemID.KHARYRLL_TELEPORT);
		canifisTeleport.addAlternates(ItemID.DRAMEN_STAFF, ItemID.LUNAR_STAFF, ItemID.FENKENSTRAINS_CASTLE_TELEPORT);

		planks2 = new ItemRequirement("Plank", ItemID.PLANK, 2);
		planks3 = new ItemRequirement("Plank", ItemID.PLANK, 3);
		planks5 = new ItemRequirement("Plank", ItemID.PLANK, 5);
		planks6 = new ItemRequirement("Plank", ItemID.PLANK, 6);
		nails8 = new ItemRequirement("Nails", ItemCollections.NAILS, 8);
		nails12 = new ItemRequirement("Nails", ItemCollections.NAILS, 12);
		nails20 = new ItemRequirement("Nails", ItemCollections.NAILS, 20);
		nails24 = new ItemRequirement("Nails", ItemCollections.NAILS, 24);

		crate = new ItemRequirement("Crate", ItemID.CRATE);
		crate.setTooltip("You can get another by asking Aurel what you should do now");

		templeLibraryKey = new ItemRequirement("Temple library key", ItemID.TEMPLE_LIBRARY_KEY);
		templeLibraryKey.setHighlightInInventory(true);
		templeLibraryKey.setTooltip("You can get another from Drezel");

		sleepingSeven = new ItemRequirement("The sleeping seven", ItemID.THE_SLEEPING_SEVEN);
		sleepingSeven.setHighlightInInventory(true);

		hammerHighlighted = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		hammerHighlighted.setHighlightInInventory(true);

		mould = new ItemRequirement("Rod mould", ItemID.ROD_MOULD);
		silvRod = new ItemRequirement("Silvthrill rod", ItemID.SILVTHRILL_ROD);
		silvRod.setHighlightInInventory(true);
		softClayHighlighted = new ItemRequirement("Soft clay", ItemID.SOFT_CLAY);
		softClayHighlighted.setHighlightInInventory(true);

		enchantedRod = new ItemRequirement("Silvthrill rod", ItemID.SILVTHRILL_ROD_7638);
		enchantedRodHighlighted = new ItemRequirement("Silvthrill rod", ItemID.SILVTHRILL_ROD_7638);
		enchantedRodHighlighted.setHighlightInInventory(true);

		rodOfIvandis = new ItemRequirement("Rod of Ivandis", ItemCollections.ROD_OF_IVANDIS);

		normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);
	}

	public void loadZones()
	{
		entranceIsland = new Zone(new WorldPoint(3426, 3430, 0), new WorldPoint(3513, 3464, 0));
		boatArea = new Zone(new WorldPoint(3480, 3373, 0), new WorldPoint(3530, 3429, 0));
		caves = new Zone(new WorldPoint(3450, 9792, 0), new WorldPoint(3504, 9847, 2));
		myrequeCave = new Zone(new WorldPoint(3502, 9829, 0), new WorldPoint(3515, 9846, 0));
		newBase = new Zone(new WorldPoint(3489, 9622, 0), new WorldPoint(3500, 9632, 1));
		roof = new Zone(new WorldPoint(3512, 3239, 2), new WorldPoint(3518, 3243, 2));
		gadderanksFightArea = new Zone(new WorldPoint(3512, 3239, 0), new WorldPoint(3518, 3243, 0));
		templeTrekArea = new Zone(new WorldPoint(2845, 4550, 0), new WorldPoint(2872, 4575, 0));
		templeTrekArea2 = new Zone(new WorldPoint(2824, 4576, 0), new WorldPoint(2847, 4599, 0));
		temple = new Zone(new WorldPoint(3402, 9880, 0), new WorldPoint(3443, 9907, 0));
		templeLibrary = new Zone(new WorldPoint(3351, 9899, 0), new WorldPoint(3362, 9905, 0));
		coffinRoom = new Zone(new WorldPoint(3490, 9858, 0), new WorldPoint(3517, 9882, 0));
	}

	public void setupConditions()
	{
		onEntranceIsland = new ZoneRequirement(entranceIsland);
		inCaves = new ZoneRequirement(caves);
		inMyrequeCave = new ZoneRequirement(myrequeCave);
		inBoatArea = new ZoneRequirement(boatArea);
		inNewBase = new ZoneRequirement(newBase);
		onRoof = new ZoneRequirement(roof);
		inGadderanksFight = new Conditions(new ZoneRequirement(gadderanksFightArea), new InInstanceRequirement());
		inTempleTrekArea = new ZoneRequirement(templeTrekArea);
		inTempleTrekArea2 = new ZoneRequirement(templeTrekArea2);
		inTemple = new ZoneRequirement(temple);
		inTempleLibrary = new ZoneRequirement(templeLibrary);
		inCoffinRoom = new ZoneRequirement(coffinRoom);

		// 1969 0>1>2>3 Florin throwing food

		// Rubble removed
		// Quest 70->80
		// 1970 0->1

		// 1971 = 1 if trapdoor is open

		// 2006 = 1 message
		// 1972 increments as rubble removed

		// 1974 = 1 repaired roof
		// 1975 = 1, repaired shop wall

		// Shop available, 1984 = 1
		// 1977 = 1, bank booth fixed
		// 1978, bank wall fixed

		// 1979, Cornelius is banker

		filledCrate = new VarbitRequirement(1994, 938);
		addedCoal = new VarbitRequirement(1980, 2);
		litFurnace = new VarbitRequirement(1980, 3);

		talkedToGadderanks = new VarbitRequirement(1995, 1);
		talkedToJuvinates = new VarbitRequirement(1997, 1);
		talkedToWiskit = new VarbitRequirement(1996, 1);

		defeatedGadderanks = new VarbitRequirement(1999, 3);
		veliafReturnedToBase = new VarbitRequirement(1981, 3, Operation.GREATER_EQUAL);

		// 2001 = 1, travelling with ivan
		// 2003 = 1, Ivan has silver sickle
		// 2005 0-10 for food

		libraryOpen = new VarbitRequirement(1982, 1);

		boardsRemoved = new VarbitRequirement(1983, 1);

		// 1981 1->2 when talked to Gadderanks

		// 2004 = 1, given gadderanks hammer
	}

	public void setupSteps()
	{
		talkToVeliaf = new NpcStep(this, NpcID.VELIAF_HURTZ_5048, new WorldPoint(3506, 9837, 0), "Talk to Veliaf Hurtz.");
		talkToVeliaf.addDialogStep("Yes.");
		climbDownCanifis = new ObjectStep(this, ObjectID.TRAPDOOR_5055, new WorldPoint(3495, 3464, 0), "Enter the trapdoor behind the Canifis Pub.");
		enterMyrequeCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5046, new WorldPoint(3492, 9823, 0), "Enter the cave on the east side.");
		leaveMyrequeCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5046, new WorldPoint(3505, 9831, 0), "Leave the cave.");
		leaveCavesIntoSwamp = new ObjectStep(this, ObjectID.WOODEN_DOORS_5057, new WorldPoint(3501, 9813, 0), "Leave through the doors to the south.");
		goOverBridge = new ObjectStep(this, NullObjectID.NULL_5003, new WorldPoint(3502, 3431, 0), "Cross the bridge to the south.");
		takeBoatToMortton = new ObjectStep(this, ObjectID.SWAMP_BOATY_6970, new WorldPoint(3499, 3378, 0), "Board the boat to the south to Mort'ton.");
		talkToFlorin = new NpcStep(this, NpcID.FLORIN, new WorldPoint(3484, 3242, 0), "");

		putFoodInChest = new ObjectStep(this, ObjectID.OPEN_CHEST_12736, new WorldPoint(3483, 3246, 0), "Place some food in the nearby chest.", food);

		talkToRazvan = new NpcStep(this, NpcID.RAZVAN, new WorldPoint(3493, 3235, 0), "Talk to Razvan in Burgh de Rott.");
		talkToRazvan.addDialogStep("Are there any 'out of the way' places here?");
		clearTrapdoorRubble = new ObjectStep(this, NullObjectID.NULL_12738, new WorldPoint(3490, 3232, 0), "Use a pickaxe on the rubble in the building south of Razvan.", pickaxe);
		clearTrapdoorRubble.addIcon(ItemID.RUNE_PICKAXE);
		clearTrapdoorRubble.addDialogStep("Yes.");

		enterBurghPubBasement = new ObjectStep(this, NullObjectID.NULL_12743, new WorldPoint(3490, 3232, 0), "Enter the trapdoor.");

		clearBasementRubble = new ObjectStep(this, ObjectID.RUBBLE_12812, "Mine all the rubble in the room, and then clean it up with a spade. You'll need an empty bucket when using the spade. You can empty the buckets outside on the rubble pile.",
			pickaxe, spade, bucketOrSemiFilledBucket);
		((ObjectStep) (clearBasementRubble)).addAlternateObjects(ObjectID.RUBBLE_12813, ObjectID.RUBBLE_12814, ObjectID.RUBBLE_12815);

		leavePubBasement = new ObjectStep(this, ObjectID.LADDER_12779, new WorldPoint(3490, 9632, 1), "Leave the basement.");

		/* Shop repairs */
		talkToAurel = new NpcStep(this, NpcID.AUREL, new WorldPoint(3515, 3241, 0), "Talk to Aurel in north east Burgh de Rott.");
		talkToAurel.addSubSteps(leavePubBasement);
		talkToAurel.addDialogStep("I'd like to help fix up the town.");
		climbShopLadder = new ObjectStep(this, ObjectID.LADDER_12780, new WorldPoint(3513, 3238, 0), "Go to the roof of Aurel's shop.", hammer, planks3, nails12);
		fixRoof = new ObjectStep(this, NullObjectID.NULL_12751, new WorldPoint(3515, 3240, 2), "Repair the broken roof.", hammer, planks3, nails12);
		fixRoof.addDialogStep("Yes.");
		climbDownShopLadder = new ObjectStep(this, ObjectID.LADDER_12781, new WorldPoint(3513, 3238, 2), "Climb back down the ladder.");
		fixShopWall = new ObjectStep(this, NullObjectID.NULL_12859, new WorldPoint(3517, 3238, 0), "Repair the shop's wall.", hammer, planks3, nails12);
		fixShopWall.addDialogStep("Yes.");
		talkToAurelForCrate = new NpcStep(this, NpcID.AUREL, new WorldPoint(3515, 3241, 0), "Talk to Aurel again.");
		talkToAurelForCrate.addDialogStep("What should I do now?");
		fillCrate = new FillBurghCrate(this);
		talkToAurelWithCrate = new NpcStep(this, NpcID.AUREL, new WorldPoint(3515, 3241, 0), "Return to Aurel with the filled crate.", crate);

		/* Bank repairs */
		repairBooth = new ObjectStep(this, NullObjectID.NULL_12759, new WorldPoint(3494, 3211, 0), "Repair the bank booth.", hammer, planks2, swampPaste, nails8);
		repairBooth.addDialogStep("Yes.");
		repairBankWall = new ObjectStep(this, NullObjectID.NULL_12859, new WorldPoint(3491, 3211, 0), "Repair the bank's outside wall.", hammer, planks3, nails12);
		repairBankWall.addDialogStep("Yes.");
		talkToCornelius = new NpcStep(this, NpcID.CORNELIUS, new WorldPoint(3496, 3211, 0), "Talk to Cornelius and ask him to be the banker.");
		talkToCornelius.addDialogSteps("What should I do now?");

		/* Furnace + fighting */
		talkToRazvanAfterRepairs = new NpcStep(this, NpcID.RAZVAN, new WorldPoint(3493, 3235, 0), "Talk to Razvan.");
		talkToRazvanAfterRepairs.addDialogSteps("What should I do now?");
		repairFurnace = new ObjectStep(this, NullObjectID.NULL_12805, new WorldPoint(3528, 3210, 0), "Repair the furnace in the south east of Burgh de Rott. You can use the bank you just made to get items out.", hammer, steelBars2Highlighted, coal, tinderbox);
		repairFurnace.addDialogStep("Yes.");
		addCoalToFurnace = new ObjectStep(this, NullObjectID.NULL_12805, new WorldPoint(3528, 3210, 0), "Add coal to the furnace.", coalHiglighted, tinderbox);
		addCoalToFurnace.addDialogStep("Yes.");
		lightFurnace = new ObjectStep(this, NullObjectID.NULL_12805, new WorldPoint(3528, 3210, 0), "Light the furnace.", tinderboxHighlighted);
		lightFurnace.addDialogStep("Yes.");
		talkToGadderanks = new NpcStep(this, NpcID.GADDERANKS, new WorldPoint(3515, 3241, 0), "Talk to Gadderanks in the shop.");
		talkToJuvinate = new NpcStep(this, NpcID.VAMPYRE_JUVINATE_4482, new WorldPoint(3515, 3241, 0), "Talk to a juvinate in the shop.");
		talkToWiskit = new NpcStep(this, NpcID.WISKIT, new WorldPoint(3515, 3241, 0), "Talk to Wiskit in the shop. Be prepared to fight the vampyres and Gadderanks.", efaritaysAidOrSilverWeapon);
		killGadderanksAndJuvinates = new NpcStep(this, NpcID.GADDERANKS_4484, new WorldPoint(3515, 3241, 0), "Defeat Gadderanks and the juvinates.", true, efaritaysAidOrSilverWeapon);
		((NpcStep) (killGadderanksAndJuvinates)).addAlternateNpcs(NpcID.VAMPYRE_JUVINATE_4486, NpcID.VAMPYRE_JUVINATE_4487);
		talkToGadderanksAgain = new NpcStep(this, NpcID.GADDERANKS_4485, new WorldPoint(3515, 3241, 0), "Talk to Gadderanks.");
		((NpcStep) (talkToGadderanksAgain)).addAlternateNpcs(NpcID.GADDERANKS_4484);
		talkToVeliafAfterFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9633, new WorldPoint(3515, 3241, 0), "Talk to Veliaf in Burgh de Rott.");

		talkToVeliafInHideoutAgain = new NpcStep(this, NpcID.VELIAF_HURTZ_5048, new WorldPoint(3506, 9837, 0), "Talk to Veliaf Hurtz.");
		talkToPolmafi = new NpcStep(this, NpcID.POLMAFI_FERDYGRIS, new WorldPoint(3506, 9837, 0), "");
		talkToIvanForTrek = new NpcStep(this, NpcID.IVAN_STROM_5053, new WorldPoint(3506, 9837, 0), "", efaritaysAidOrSilverWeapon);
		killJuvinates = new NpcStep(this, NpcID.VAMPYRE_JUVINATE_4443, new WorldPoint(2859, 4564, 0), "Kill the juvenates.", true, efaritaysAidOrSilverWeapon);
		killJuvinates2 = new NpcStep(this, NpcID.VAMPYRE_JUVINATE_4442, new WorldPoint(2839, 4589, 0), "Kill the juvenates.", true, efaritaysAidOrSilverWeapon);
		goDownToDrezel = new ObjectStep(this, ObjectID.TRAPDOOR_3432, new WorldPoint(3422, 3485, 0), "Talk to Drezel under the Paterdomus Temple.");
		((ObjectStep) (goDownToDrezel)).addAlternateObjects(ObjectID.TRAPDOOR_3433);
		talkToDrezel = new NpcStep(this, NpcID.DREZEL, new WorldPoint(3439, 9896, 0), "Talk to Drezel under the Paterdomus Temple.");
		talkToDrezel.addDialogSteps("Veliaf told me about Ivandis.", "Is there somewhere that I might get more information on Ivandis?",
			"The lives of those pitiful few left in Morytania could rest on this!");
		useKeyOnHole = new ObjectStep(this, ObjectID.KEYHOLE, new WorldPoint(3443, 9898, 0), "Use the key on the keyhole near Drezel.", templeLibraryKey);
		useKeyOnHole.addIcon(ItemID.TEMPLE_LIBRARY_KEY);
		enterLibrary = new ObjectStep(this, NullObjectID.NULL_12762, new WorldPoint(3441, 9899, 0), "Enter the library.");
		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_12767, new WorldPoint(3354, 9902, 0), "Search the bookcases for 'The Sleeping Seven' book.");
		readBook = new DetailedQuestStep(this, "Read 'The Sleeping Seven'.", sleepingSeven);

		useHammerOnBoards = new ObjectStep(this, NullObjectID.NULL_12772, new WorldPoint(3483, 9832, 0), "Use a hammer on the boarded up cave.", hammerHighlighted);
		useHammerOnBoards.addDialogStep("Yes.");
		useHammerOnBoards.addIcon(ItemID.HAMMER);
		enterCoffinRoom = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_12770, new WorldPoint(3484, 9832, 0), "Enter the cave.");

		useClayOnCoffin = new ObjectStep(this, ObjectID.COFFIN_12802, new WorldPoint(3511, 9864, 0), "Use a piece of soft clay on the coffin.", softClayHighlighted);
		useClayOnCoffin.addIcon(ItemID.SOFT_CLAY);
		makeRod = new DetailedQuestStep(this, "Make a silvthrill rod at any furnace.", mould, mithrilBar, silverBar, sapphire);
		enchantRod = new DetailedQuestStep(this, "Cast Lvl-1 enchant on the rod.", silvRod, waterRune, cosmicRune, normalSpellbook);
		useRodOnWell = new ObjectStep(this, ObjectID.WELL_3485, new WorldPoint(3423, 9890, 0), "", enchantedRodHighlighted, rope);
		useRodOnWell.addIcon(ItemID.SILVTHRILL_ROD_7638);
		talkToVeliafToFinish = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3494, 9628, 0), "");

	}

	public void setupConditionalSteps()
	{
		ConditionalStep goToHollowBase = new ConditionalStep(this, climbDownCanifis);
		goToHollowBase.addStep(inCaves, enterMyrequeCave);

		startQuest = new ConditionalStep(this, goToHollowBase, "Enter the Myreque Hideout under the Canifis Pub and talk to Veliaf to start.");
		startQuest.addStep(inMyrequeCave, talkToVeliaf);

		travelToBurgh = new ConditionalStep(this, talkToFlorin, "Travel to Burgh de Rott, south of Mort'ton, and talk to Florin at the entrance.");
		travelToBurgh.addStep(inBoatArea, takeBoatToMortton);
		travelToBurgh.addStep(onEntranceIsland, goOverBridge);
		travelToBurgh.addStep(inMyrequeCave, leaveMyrequeCave);
		travelToBurgh.addStep(inCaves, leaveCavesIntoSwamp);

		returnToHideout = new ConditionalStep(this, goToHollowBase, "Return the Myreque Hideout under the Canifis Pub and talk to Veliaf there. Bank any followers before going.");
		returnToHideout.addStep(inMyrequeCave, talkToVeliafInHideoutAgain);

		goTalkToPolmafi = new ConditionalStep(this, goToHollowBase, "Talk to Polmafi in the Myreque Hideout.");
		goTalkToPolmafi.addStep(inMyrequeCave, talkToPolmafi);

		travelWithIvan = new ConditionalStep(this, goToHollowBase, "Talk to Ivan in the Myreque Hideout to temple trek with him. " +
			"The long route has 4 level 50 Juvinates and the short route has 2 level 75 Juvinates. " +
			"You can give him a steel med helm / chainbody / platelegs, and salmon to help him survive.");
		travelWithIvan.addStep(inTempleTrekArea2, killJuvinates2);
		travelWithIvan.addStep(inTempleTrekArea, killJuvinates);
		travelWithIvan.addStep(inMyrequeCave, talkToIvanForTrek);

		goIntoCavesAgain = new ConditionalStep(this, climbDownCanifis, "Enter the trapdoor behind the Canifis pub.", hammer, softClay);

		ConditionalStep enterPaterToBless = new ConditionalStep(this, goDownToDrezel, enchantedRod, rope);

		goBlessRod = new ConditionalStep(this, enterPaterToBless, "Go use the Silvthrill rod on the well in Paterdomus.");
		goBlessRod.addStep(inTemple, useRodOnWell);

		finishQuest = new ConditionalStep(this, enterBurghPubBasement, "Bring the rod to Veliaf in the Burgh de Rott pub's basement to finish the quest.", rodOfIvandis);
		finishQuest.addStep(inNewBase, talkToVeliafToFinish);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(foodForChest, spade, bucketTo5, pickaxe, hammer, planks11, nails44, swampPaste,
			rawMackerelOrSnail10, bronzeAxes10, tinderboxes4, steelBars2, coal, efaritaysAidOrSilverWeapon,
			softClay, rope, silverBar, mithrilBar, sapphire, cosmicRune, waterRune);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(normalSpellbook);
		return req;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.IN_SEARCH_OF_THE_MYREQUE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 25));
		req.add(new SkillRequirement(Skill.MINING, 15));
		req.add(new SkillRequirement(Skill.MAGIC, 7));
		return req;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(steelmed, steelChain, steelLegs, silverSickle, morttonTeleport, canifisTeleport);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Gadderanks (level 35)", "Vampyre Juvenites (levels 50-75)");
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
				new ExperienceReward(Skill.ATTACK, 2000),
				new ExperienceReward(Skill.STRENGTH, 2000),
				new ExperienceReward(Skill.CRAFTING, 2000),
				new ExperienceReward(Skill.DEFENCE, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards() {
		return Arrays.asList(
				new UnlockReward("Access to Temple Trekking Minigame."),
				new UnlockReward("Ability to make the Rod of Ivandis"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Finding a new base", Arrays.asList(startQuest, travelToBurgh, putFoodInChest, talkToRazvan, clearTrapdoorRubble,
			enterBurghPubBasement, clearBasementRubble), foodForChest, pickaxe, bucketTo5, spade));

		allSteps.add(new PanelDetails("Repairing the shop", Arrays.asList(talkToAurel, climbShopLadder, fixRoof, climbDownShopLadder, fixShopWall, talkToAurelForCrate), hammer, planks6, nails24));
		allSteps.add(new PanelDetails("Stocking the shop", Collections.singletonList(fillCrate), tinderbox3,
			bronzeAxes10, rawMackerelOrSnail10, hammer, planks5, nails20, swampPaste));

		allSteps.add(new PanelDetails("Repairing the bank", Arrays.asList(talkToAurelWithCrate, repairBooth, repairBankWall, talkToCornelius), hammer, planks5, nails20, swampPaste));
		allSteps.add(new PanelDetails("Repairing the furnace", Arrays.asList(talkToRazvanAfterRepairs, repairFurnace, addCoalToFurnace, lightFurnace), hammer, steelBars2, coal, tinderbox));
		allSteps.add(new PanelDetails("Defending Burgh de Rott", Arrays.asList(talkToGadderanks, talkToJuvinate, talkToWiskit, killGadderanksAndJuvinates, talkToGadderanksAgain, talkToVeliafAfterFight), efaritaysAidOrSilverWeapon));

		allSteps.add(new PanelDetails("Relocating", Arrays.asList(returnToHideout, goTalkToPolmafi, travelWithIvan),
			Collections.singletonList(efaritaysAidOrSilverWeapon), Arrays.asList(steelmed, steelChain, steelLegs,
			silverSickle)));

		allSteps.add(new PanelDetails("Ivandis' legacy", Arrays.asList(talkToDrezel, useKeyOnHole, enterLibrary, searchBookcase, readBook, goIntoCavesAgain, useHammerOnBoards, enterCoffinRoom,
			useClayOnCoffin, makeRod, enchantRod, goBlessRod, finishQuest), hammer, softClay, mithrilBar, silverBar, cosmicRune, waterRune, rope, sapphire));

		return allSteps;
	}
}
