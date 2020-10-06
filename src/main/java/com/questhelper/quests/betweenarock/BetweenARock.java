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
package com.questhelper.quests.betweenarock;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.BETWEEN_A_ROCK
)
public class BetweenARock extends BasicQuestHelper
{
	ItemRequirement coins5, pickaxe, page1, page2, page3, pages, dwarvenLore, dwarvenLoreHighlight, goldBar, goldBarHighlight, goldCannonball, cannonMould, goldCannonballHighlight, schematic,
		baseSchematic, schematicEngineer, khorvakSchematic, goldHelmet, hammer, goldBars3, schematicHighlight, solvedSchematic, combatGear,
		goldOre6, goldBars4, coins1000, goldHelmetEquipped;

	ConditionForStep inTrollRoom, inDwarfEntrance, inDwarfMine, inKeldagrim, inDwarvenMine, hasPage1, hasPage2, hasPage3, hasPages, hasUsedGoldBar, hasCannonball, shotGoldCannonball, hasBaseSchematic,
		hasSchematicEngineer, inKhorvakRoom, hasGoldHelmet, hasKhorvakSchematic, hasSolvedSchematic, has6Ore, inRealm, avatarNearby;

	QuestStep enterDwarfCave, enterDwarfCave2, talkToFerryman, talkToDondakan, travelBackWithFerryman, talkToBoatman, talkToEngineer, talkToRolad, enterDwarvenMine, killScorpion,
		searchCart, mineRock, goBackUpToRolad, returnToRolad, readEntireBook, travelToKeldagrim, enterDwarfCaveWithBook, enterDwarfCave2WithBook, talkToFerrymanWithBook, talkToDondakanWithBook,
		useGoldBarOnDondakan, makeGoldCannonball, enterDwarfCaveWithCannonball, enterDwarfCave2WithCannonball, talkToFerrymanWithCannonball, useGoldCannonballOnDondakan, talkToDondakanAfterShot,
		readBookAgain, talkToEngineerAgain, travelBackWithFerrymanAgain, travelToKeldagrimAgain, talkToBoatmanAgain, useGoldBarOnAnvil, enterKhorvakRoom, talkToKhorvak, assembleSchematic,  enterDwarfCaveWithHelmet,
		enterDwarfCave2WithHelmet, talkToFerrymanWithHelmet, talkToDondakanWithHelmet, mine6GoldOre, talkToDondakanForEnd, talkToSecondFlame, finishQuest;

	NpcStep killAvatar;

	Zone trollRoom, dwarfEntrance, dwarfMine, keldagrim, keldagrim2, dwarvenMine, khorvakRoom, realm;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goToDondakan = new ConditionalStep(this, enterDwarfCave);
		goToDondakan.addStep(inDwarfMine, talkToDondakan);
		goToDondakan.addStep(inDwarfEntrance, talkToFerryman);
		goToDondakan.addStep(inTrollRoom, enterDwarfCave2);

		steps.put(0, goToDondakan);

		ConditionalStep talkToDwarvenEngineer = new ConditionalStep(this, travelToKeldagrim);
		talkToDwarvenEngineer.addStep(inKeldagrim, talkToEngineer);
		talkToDwarvenEngineer.addStep(inDwarfEntrance, talkToBoatman);
		talkToDwarvenEngineer.addStep(inDwarfMine, travelBackWithFerryman);

		steps.put(10, talkToDwarvenEngineer);

		steps.put(20, talkToRolad);

		ConditionalStep getPages = new ConditionalStep(this, enterDwarvenMine);
		getPages.addStep(new Conditions(inDwarvenMine, hasPages), goBackUpToRolad);
		getPages.addStep(new Conditions(hasPages), returnToRolad);
		getPages.addStep(new Conditions(inDwarvenMine, hasPage2, hasPage1), mineRock);
		getPages.addStep(new Conditions(inDwarvenMine, hasPage2), killScorpion);
		getPages.addStep(inDwarvenMine, searchCart);

		steps.put(30, getPages);

		steps.put(40, readEntireBook);

		ConditionalStep returnToDondakan = new ConditionalStep(this, enterDwarfCaveWithBook);
		returnToDondakan.addStep(inDwarfMine, talkToDondakanWithBook);
		returnToDondakan.addStep(inDwarfEntrance, talkToFerrymanWithBook);
		returnToDondakan.addStep(inTrollRoom, enterDwarfCave2WithBook);

		steps.put(50, returnToDondakan);

		ConditionalStep giveGoldBar = new ConditionalStep(this, enterDwarfCaveWithBook);
		giveGoldBar.addStep(shotGoldCannonball, talkToDondakanAfterShot);
		giveGoldBar.addStep(new Conditions(inDwarfMine, hasCannonball), useGoldCannonballOnDondakan);
		giveGoldBar.addStep(new Conditions(inDwarfEntrance, hasCannonball), talkToFerrymanWithCannonball);
		giveGoldBar.addStep(new Conditions(inTrollRoom, hasCannonball), enterDwarfCave2WithCannonball);
		giveGoldBar.addStep(hasCannonball, enterDwarfCaveWithCannonball);
		giveGoldBar.addStep(hasUsedGoldBar, makeGoldCannonball);
		giveGoldBar.addStep(inDwarfMine, useGoldBarOnDondakan);
		giveGoldBar.addStep(inDwarfEntrance, talkToFerrymanWithBook);
		giveGoldBar.addStep(inTrollRoom, enterDwarfCave2WithBook);

		steps.put(60, giveGoldBar);

		steps.put(70, talkToDondakanAfterShot);

		ConditionalStep sortSchematics = new ConditionalStep(this, readBookAgain);
		sortSchematics.addStep(new Conditions(hasSolvedSchematic, hasGoldHelmet, inDwarfMine), talkToDondakanWithHelmet);
		sortSchematics.addStep(new Conditions(hasSolvedSchematic, hasGoldHelmet, inDwarfEntrance), talkToFerrymanWithHelmet);
		sortSchematics.addStep(new Conditions(hasSolvedSchematic, hasGoldHelmet, inTrollRoom), enterDwarfCave2WithHelmet);
		sortSchematics.addStep(new Conditions(hasSolvedSchematic, hasGoldHelmet), enterDwarfCaveWithHelmet);
		sortSchematics.addStep(new Conditions(hasSolvedSchematic), useGoldBarOnAnvil);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, hasSchematicEngineer, hasKhorvakSchematic), assembleSchematic);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, hasSchematicEngineer, hasKhorvakSchematic), assembleSchematic);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, hasSchematicEngineer, inKhorvakRoom), talkToKhorvak);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, hasSchematicEngineer, hasGoldHelmet), enterKhorvakRoom);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, hasSchematicEngineer), useGoldBarOnAnvil);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, inKeldagrim), talkToEngineerAgain);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, inDwarfEntrance), talkToBoatmanAgain);
		sortSchematics.addStep(new Conditions(hasBaseSchematic, inDwarfMine), travelBackWithFerrymanAgain);
		sortSchematics.addStep(hasBaseSchematic, travelToKeldagrimAgain);

		steps.put(80, sortSchematics);

		ConditionalStep completeQuest = new ConditionalStep(this, talkToDondakanForEnd);
		completeQuest.addStep(avatarNearby, killAvatar);
		completeQuest.addStep(new Conditions(inRealm, has6Ore), talkToSecondFlame);
		completeQuest.addStep(inRealm, mine6GoldOre);

		steps.put(90, completeQuest);

		steps.put(100, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		coins5 = new ItemRequirement("Coins", ItemID.COINS_995, 5);
		page1 = new ItemRequirement("Book page 1", ItemID.BOOK_PAGE_1);
		page2 = new ItemRequirement("Book page 2", ItemID.BOOK_PAGE_2);
		page3 = new ItemRequirement("Book page 3", ItemID.BOOK_PAGE_3);
		pages = new ItemRequirement("Pages", ItemID.PAGES_4573);
		dwarvenLore = new ItemRequirement("Dwarven lore", ItemID.DWARVEN_LORE);
		dwarvenLore.setTip("You can get another from Rolad south of Ice Mountain");

		dwarvenLoreHighlight = new ItemRequirement("Dwarven lore", ItemID.DWARVEN_LORE);
		dwarvenLoreHighlight.setTip("You can get another from Rolad south of Ice Mountain");
		dwarvenLoreHighlight.setHighlightInInventory(true);

		goldBar = new ItemRequirement("Gold bar", ItemID.GOLD_BAR);
		goldBars3 = new ItemRequirement("Gold bar", ItemID.GOLD_BAR, 3);
		goldBarHighlight = new ItemRequirement("Gold bar", ItemID.GOLD_BAR);
		goldBarHighlight.setHighlightInInventory(true);

		goldCannonball = new ItemRequirement("Cannon ball", ItemID.CANNON_BALL);
		goldCannonballHighlight = new ItemRequirement("Cannon ball", ItemID.CANNON_BALL);
		goldCannonballHighlight.setHighlightInInventory(true);

		cannonMould = new ItemRequirement("Ammo mould", ItemID.AMMO_MOULD);

		schematic = new ItemRequirement("Schematic", ItemID.SCHEMATIC);

		schematicHighlight = new ItemRequirement("Schematic", ItemID.SCHEMATIC);
		schematicHighlight.setHighlightInInventory(true);

		baseSchematic = new ItemRequirement("Base schematics", ItemID.BASE_SCHEMATICS);

		schematicEngineer = new ItemRequirement("Schematics", ItemID.SCHEMATICS);

		goldHelmet = new ItemRequirement("Gold helmet", ItemID.GOLD_HELMET);
		goldHelmetEquipped = new ItemRequirement("Gold helmet", ItemID.GOLD_HELMET, 1, true);
		khorvakSchematic = new ItemRequirement("Khorvak schematic", ItemID.SCHEMATICS_4577);

		solvedSchematic = new ItemRequirement("Schematic", ItemID.SCHEMATIC_4578);

		combatGear = new ItemRequirement("Combat gear + food", -1, -1);

		goldOre6 = new ItemRequirement("Gold ore", ItemID.GOLD_ORE, 6);

		goldBars4 = new ItemRequirement("Gold bar", ItemID.GOLD_BAR, 4);
		coins1000 = new ItemRequirement("Coins for travelling", ItemID.COINS_995, 1000);
	}

	public void loadZones()
	{
		trollRoom = new Zone(new WorldPoint(2762, 10123, 0), new WorldPoint(2804, 10164, 0));
		dwarfEntrance = new Zone(new WorldPoint(2814, 10121, 0), new WorldPoint(2884, 10139, 0));
		dwarfMine = new Zone(new WorldPoint(2816, 10139, 0), new WorldPoint(2878, 10176, 0));
		keldagrim = new Zone(new WorldPoint(2816, 10177, 0), new WorldPoint(2943, 10239, 0));
		keldagrim2 = new Zone(new WorldPoint(2901, 10150, 0), new WorldPoint(2943, 10177, 0));
		dwarvenMine = new Zone(new WorldPoint(2960, 9696, 0), new WorldPoint(3062, 9854, 0));
		khorvakRoom = new Zone(new WorldPoint(2815, 9856, 0), new WorldPoint(2880, 9888, 0));
		realm = new Zone(new WorldPoint(2306, 4928, 0), new WorldPoint(2440, 4990, 0));
	}

	public void setupConditions()
	{
		inTrollRoom = new ZoneCondition(trollRoom);
		inDwarfEntrance = new ZoneCondition(dwarfEntrance);
		inDwarfMine = new ZoneCondition(dwarfMine);
		inKeldagrim = new ZoneCondition(keldagrim, keldagrim2);
		inDwarvenMine = new ZoneCondition(dwarvenMine);
		inKhorvakRoom = new ZoneCondition(khorvakRoom);
		hasPage1 = new ItemRequirementCondition(page1);
		hasPage2 = new ItemRequirementCondition(page2);
		hasPage3 = new ItemRequirementCondition(page3);
		hasPages = new ItemRequirementCondition(pages);
		hasKhorvakSchematic = new ItemRequirementCondition(khorvakSchematic);
		hasUsedGoldBar = new VarbitCondition(301, 1);
		shotGoldCannonball = new VarbitCondition(313, 1);
		hasCannonball = new Conditions(LogicType.OR, new ItemRequirementCondition(goldCannonball), shotGoldCannonball);
		hasBaseSchematic = new ItemRequirementCondition(baseSchematic);
		hasGoldHelmet = new Conditions(true, new ItemRequirementCondition(goldHelmet));
		hasSchematicEngineer = new ItemRequirementCondition(schematicEngineer);
		hasSolvedSchematic = new VarbitCondition(305, 1);
		has6Ore = new ItemRequirementCondition(goldOre6);
		inRealm = new ZoneCondition(realm);

		avatarNearby = new Conditions(LogicType.OR, new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_MAGIC), new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_MAGIC_1234),
			new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_MAGIC_1235), new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_RANGING), new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_RANGING_1231),
			new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_RANGING_1232), new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_STRENGTH), new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_STRENGTH_1228),
			new NpcCondition(NpcID.ARZINIAN_AVATAR_OF_STRENGTH_1229));
	}

	public void setupSteps()
	{
		enterDwarfCave = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0), "Travel to the Keldagrim south mines and talk to Dondakan there.", coins5);
		enterDwarfCave2 = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0), "Travel to the Keldagrim south mines and talk to Dondakan there.", coins5);
		talkToFerryman = new NpcStep(this, NpcID.DWARVEN_FERRYMAN, new WorldPoint(2829, 10129, 0), "Pay the Dwarven Ferryman 5 coins to travel across the water.", coins5);
		talkToFerryman.addDialogStep("Yes please.");
		talkToDondakan = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Talk to Dondakan in the north of the mine.");
		talkToDondakan.addDialogStep("Why are you firing a cannon at a wall?");
		talkToDondakan.addDialogStep("So why were you trying to get through the rock again?");
		talkToDondakan.addDialogStep("Sounds interesting!");
		talkToDondakan.addSubSteps(enterDwarfCave, enterDwarfCave2, talkToFerryman);

		travelBackWithFerryman = new NpcStep(this, NpcID.DWARVEN_FERRYMAN_4897, new WorldPoint(2854, 10142, 0), "Talk to the Dwarven Engineer in west Keldagrim.");
		talkToBoatman = new NpcStep(this, NpcID.DWARVEN_BOATMAN_7726, new WorldPoint(2842, 10129, 0), "Talk to the Dwarven Engineer in west Keldagrim.");
		travelToKeldagrim = new DetailedQuestStep(this, "Talk to the Dwarven Engineer in west Keldagrim.");
		talkToEngineer = new NpcStep(this, NpcID.DWARVEN_ENGINEER, new WorldPoint(2870, 10199, 0), "Talk to the Dwarven Engineer in west Keldagrim.");
		talkToEngineer.addSubSteps(travelBackWithFerryman, talkToBoatman, travelToKeldagrim);

		talkToRolad = new NpcStep(this, NpcID.ROLAD, new WorldPoint(3022, 3453, 0),
			"Talk to Rolad at the Ice Mountain entrance to the Dwarven Mine. If you don't have an ammo mould, buy one from Nulodion whilst you're here.", pickaxe);
		talkToRolad.addDialogStep("I'll be back later.");

		enterDwarvenMine = new ObjectStep(this, ObjectID.TRAPDOOR_11867, new WorldPoint(3019, 3450,0), "Enter the Dwarven Mine.", pickaxe);

		searchCart = new DetailedQuestStep(this, "Search the mine carts for a page.");

		killScorpion = new DetailedQuestStep(this, "Kill scorpions for a page.");

		mineRock = new DetailedQuestStep(this, "Mine low level rocks for a page.", pickaxe);

		goBackUpToRolad = new ObjectStep(this, ObjectID.LADDER_17387, new WorldPoint(3019, 9850, 0), "Go back up to Rolad.", page1, page2, page3);

		returnToRolad = new NpcStep(this, NpcID.ROLAD, new WorldPoint(3022, 3453, 0),
			"Talk Rolad again.", page1, page2, page3);
		returnToRolad.addSubSteps(goBackUpToRolad);

		readEntireBook = new DetailedQuestStep(this, "Read the entire dwarven lore book.", dwarvenLoreHighlight);

		enterDwarfCaveWithBook = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0), "Return to Dondakan with the book and a gold bar.", coins5, goldBar, dwarvenLore);
		enterDwarfCave2WithBook = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0), "Return to Dondakan with the book and a gold bar.", coins5, goldBar, dwarvenLore);
		talkToFerrymanWithBook = new NpcStep(this, NpcID.DWARVEN_FERRYMAN, new WorldPoint(2829, 10129, 0), "Return to Dondakan with the book and a gold bar.", coins5, goldBar, dwarvenLore);
		talkToFerrymanWithBook.addDialogStep("Yes please.");
		talkToDondakanWithBook = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Return to Dondakan with the book and a gold bar.", goldBar, dwarvenLore);
		talkToDondakanWithBook.addSubSteps(enterDwarfCaveWithBook, enterDwarfCave2WithBook, talkToFerrymanWithBook);


		useGoldBarOnDondakan = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Use a gold bar on Dondakan.", goldBarHighlight);
		useGoldBarOnDondakan.addIcon(ItemID.GOLD_BAR);

		makeGoldCannonball = new DetailedQuestStep(this, "Use a gold bar on a furnace to make a gold cannonball.", goldBar, cannonMould);

		enterDwarfCaveWithCannonball = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0), "Return to Dondakan with the book and the gold cannonball.", coins5, goldCannonball, dwarvenLore);
		enterDwarfCave2WithCannonball = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0), "Return to Dondakan with the book and the gold cannonball.", coins5, goldCannonball, dwarvenLore);
		talkToFerrymanWithCannonball = new NpcStep(this, NpcID.DWARVEN_FERRYMAN, new WorldPoint(2829, 10129, 0), "Return to Dondakan with the book and the gold cannonball.", coins5, goldCannonball, dwarvenLore);
		talkToFerrymanWithCannonball.addDialogStep("Yes please.");

		useGoldCannonballOnDondakan = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Use a gold cannon ball on Dondakan.", goldCannonballHighlight);
		useGoldCannonballOnDondakan.addIcon(ItemID.CANNON_BALL);
		useGoldCannonballOnDondakan.addDialogStep("Yes, I'm sure this will crack open the rock.");
		useGoldCannonballOnDondakan.addSubSteps(enterDwarfCave2WithCannonball, enterDwarfCave2WithCannonball, talkToFerrymanWithCannonball);

		talkToDondakanAfterShot = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "talk to Dondakan.");
		talkToDondakanAfterShot.addDialogStep("So you want to... fire me into the rock?");
		talkToDondakanAfterShot.addDialogStep("I can't argue with that, shoot me in!");

		readBookAgain = new DetailedQuestStep(this, "Read the last page of the dwarven lore book again for a base schematic.", dwarvenLoreHighlight);
		readBookAgain.addDialogStep("Yes.");

		travelBackWithFerrymanAgain = new NpcStep(this, NpcID.DWARVEN_FERRYMAN_4897, new WorldPoint(2854, 10142, 0), "Talk to the Dwarven Engineer in west Keldagrim.", schematic, baseSchematic);
		talkToBoatmanAgain = new NpcStep(this, NpcID.DWARVEN_BOATMAN_7726, new WorldPoint(2842, 10129, 0), "Talk to the Dwarven Engineer in west Keldagrim.", schematic, baseSchematic);
		travelToKeldagrimAgain = new DetailedQuestStep(this, "Talk to the Dwarven Engineer in west Keldagrim.", schematic, baseSchematic);
		talkToEngineerAgain = new NpcStep(this, NpcID.DWARVEN_ENGINEER, new WorldPoint(2870, 10199, 0), "Talk to the Dwarven Engineer in west Keldagrim.", schematic, baseSchematic);
		talkToEngineerAgain.addSubSteps(travelBackWithFerrymanAgain, talkToBoatmanAgain, travelToKeldagrimAgain);

		useGoldBarOnAnvil = new ObjectStep(this, ObjectID.ANVIL_6150, new WorldPoint(2869, 10202, 0), "Use 3 gold bars on an anvil to make a gold helmet.", goldBars3, hammer);

		enterKhorvakRoom = new ObjectStep(this, ObjectID.STAIRS_55, new WorldPoint(2821, 3485, 0), "Talk to Khorvak under White Wolf Mountain.");

		talkToKhorvak = new NpcStep(this, NpcID.KHORVAK_A_DWARVEN_ENGINEER, new WorldPoint(2867, 9876, 0), "Talk to Khorvak under White Wolf Mountain.");
		talkToKhorvak.addDialogStep("No, I've had enough of buying drinks for people!");
		talkToKhorvak.addSubSteps(enterKhorvakRoom);

		assembleSchematic = new PuzzleStep(this, schematicHighlight);

		enterDwarfCaveWithHelmet = new ObjectStep(this, ObjectID.TUNNEL_5008, new WorldPoint(2732, 3713, 0), "Prepare to for a fight, then return to Dondakan.", coins5, goldHelmetEquipped, solvedSchematic, pickaxe, combatGear);
		enterDwarfCave2WithHelmet = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_5973, new WorldPoint(2781, 10161, 0), "Prepare to for a fight, then return to Dondakan.", coins5, goldHelmetEquipped, solvedSchematic, pickaxe, combatGear);
		talkToFerrymanWithHelmet = new NpcStep(this, NpcID.DWARVEN_FERRYMAN, new WorldPoint(2829, 10129, 0), "Prepare to for a fight, then return to Dondakan.", coins5, goldHelmetEquipped, solvedSchematic, pickaxe, combatGear);
		talkToFerrymanWithHelmet.addDialogStep("Yes please.");
		talkToDondakanWithHelmet = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Prepare to for a fight, then return to Dondakan.", coins5, goldHelmetEquipped, solvedSchematic, pickaxe, combatGear);
		talkToDondakanWithHelmet.addDialogStep("You may fire when ready!");
		talkToDondakanForEnd = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Prepare to for a fight, then return to Dondakan.", coins5, goldHelmetEquipped, pickaxe, combatGear);
		talkToDondakanForEnd.addDialogStep("Ready as I'll ever be.");
		talkToDondakanWithHelmet.addSubSteps(enterDwarfCaveWithHelmet, enterDwarfCave2WithHelmet, talkToFerrymanWithHelmet, talkToDondakanForEnd);

		mine6GoldOre = new DetailedQuestStep(this, "Mine 6 gold ore. If you want the Avatar to be level 75 vs 125, get 15.", pickaxe);

		talkToSecondFlame = new ObjectStep(this, ObjectID.WALL_OF_FLAME_5979, new WorldPoint(2373, 4956, 0), "TALK to the central wall of flame.");
		killAvatar = new NpcStep(this, NpcID.ARZINIAN_AVATAR_OF_MAGIC, new WorldPoint(2375, 4953, 0), "Kill the avatar.");
		killAvatar.addAlternateNpcs(NpcID.ARZINIAN_AVATAR_OF_MAGIC_1234, NpcID.ARZINIAN_AVATAR_OF_MAGIC_1235,
			NpcID.ARZINIAN_AVATAR_OF_RANGING, NpcID.ARZINIAN_AVATAR_OF_RANGING_1231, NpcID.ARZINIAN_AVATAR_OF_RANGING_1232,
			NpcID.ARZINIAN_AVATAR_OF_STRENGTH, NpcID.ARZINIAN_AVATAR_OF_STRENGTH_1228, NpcID.ARZINIAN_AVATAR_OF_STRENGTH_1229);

		finishQuest = new NpcStep(this, NpcID.DONDAKAN_THE_DWARF, new WorldPoint(2822, 10167, 0), "Talk to Dondakan to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(pickaxe);
		reqs.add(goldBars4);
		reqs.add(hammer);
		reqs.add(cannonMould);
		reqs.add(coins1000);
		return reqs;
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Scorpion (level 14)");
		reqs.add("Arzinian Being of Bordanzan (level 75 - 125)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToDondakan))));
		allSteps.add(new PanelDetails("Research", new ArrayList<>(Arrays.asList(talkToEngineer, talkToRolad, enterDwarvenMine, searchCart, killScorpion, mineRock, returnToRolad, readEntireBook)), coins5, pickaxe));
		allSteps.add(new PanelDetails("Experiment", new ArrayList<>(Arrays.asList(talkToDondakanWithBook, useGoldBarOnDondakan, makeGoldCannonball, useGoldCannonballOnDondakan)), cannonMould, goldBar));
		allSteps.add(new PanelDetails("Solving the schematic", new ArrayList<>(Arrays.asList(readBookAgain, talkToEngineerAgain, useGoldBarOnAnvil, talkToKhorvak, assembleSchematic)), goldBars3, hammer));
		allSteps.add(new PanelDetails("Into the hard place", new ArrayList<>(Arrays.asList(talkToDondakanWithHelmet, mine6GoldOre, talkToSecondFlame, killAvatar, finishQuest)), goldHelmet, solvedSchematic, coins5, pickaxe, combatGear));
		return allSteps;
	}
}
