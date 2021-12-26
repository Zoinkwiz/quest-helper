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
package com.questhelper.quests.eadgarsruse;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.WidgetTextRequirement;
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
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.EADGARS_RUSE
)
public class EadgarsRuse extends BasicQuestHelper
{
	//Items Required
	ItemRequirement climbingBoots, climbingBootsOr12Coins, vodka, vodkaHighlight, pineappleChunks, pineappleChunksHighlight, logs2, grain10, rawChicken5, tinderbox, pestleAndMortar, ranarrPotionUnf,
		coins12, cellKey2, alcoChunks, parrot, parrotHighlighted, robe, logs1, thistle, logHighlight, tinderboxHighlight, driedThistle, pestleAndMortarHighlight, groundThistle, ranarrUnfHighlight, trollPotion, trainedParrot,
		fakeMan, storeroomKey, goutweed, climbingBootsEquipped;

	//Items Recommended
	ItemRequirement ardougneTeleport;

	Requirement inSanfewRoom, inTenzingHut, hasClimbingBoots, hasCoins, onMountainPath, inTrollArea1, inPrison, freedEadgar, hasCellKey2, inStrongholdFloor1, inStrongholdFloor2,
		inEadgarsCave, inTrollheimArea, askedAboutAlcohol, askedAboutPineapple, fireNearby, foundOutAboutKey, inStoreroom;

	DetailedQuestStep goUpToSanfew, talkToSanfew, buyClimbingBoots, travelToTenzing, getCoinsOrBoots, climbOverStile, climbOverRocks, enterSecretEntrance, freeEadgar, goUpStairsPrison,
		getBerryKey, goUpToTopFloorStronghold, exitStronghold, enterEadgarsCave, talkToEadgar, leaveEadgarsCave, enterStronghold, goDownSouthStairs, talkToCook, goUpToTopFloorStrongholdFromCook,
		exitStrongholdFromCook, enterEadgarsCaveFromCook, talkToEadgarFromCook, talkToPete, talkToPeteAboutAlcohol, talkToPeteAboutPineapple, useVodkaOnChunks, useChunksOnParrot, enterEadgarsCaveWithParrot,
		talkToEadgarWithParrot, enterPrisonWithParrot, leaveEadgarsCaveWithParrot, enterStrongholdWithParrot, goDownNorthStairsWithParrot, goDownToPrisonWithParrot, parrotOnRack, talkToTegid, enterEadgarsCaveWithItems,
		talkToEadgarWithItems, leaveEadgarsCaveForThistle, pickThistle, lightFire, useThistleOnFire, useThistleOnTrollFire, grindThistle, useGroundThistleOnRanarr, enterEadgarsCaveWithTrollPotion, giveTrollPotionToEadgar,
		enterPrisonForParrot, enterStrongholdForParrot, goDownNorthStairsForParrot, goDownToPrisonForParrot, getParrotFromRack, leaveEadgarsCaveForParrot, leavePrisonWithParrot, goUpToTopFloorWithParrot, leaveStrongholdWithParrot,
		enterEadgarCaveWithTrainedParrot, talkToEadgarWithTrainedParrot, leaveEadgarsCaveWithScarecrow, enterStrongholdWithScarecrow, goDownSouthStairsWithScarecrow, talkToCookWithScarecrow, talkToBurntmeat, goDownToStoreroom,
		enterStoreroomDoor, getGoutweed, returnUpToSanfew, returnToSanfew;

	ObjectStep searchDrawers;

	PanelDetails travelToEadgarPanel;

	//Zones
	Zone sanfewRoom, tenzingHut, mountainPath1, mountainPath2, mountainPath3, mountainPath4, mountainPath5, trollArea1, prison, strongholdFloor1, strongholdFloor2, eadgarsCave,
		trollheimArea, storeroom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		if (freedEadgar.check(client))
		{
			travelToEadgarPanel = new PanelDetails("Travel to Eadgar",
				Arrays.asList(travelToTenzing, climbOverStile, climbOverRocks, enterSecretEntrance, goUpStairsPrison,
					goUpToTopFloorStronghold, enterEadgarsCave, talkToEadgar), climbingBoots);
		}
		else
		{
			travelToEadgarPanel = new PanelDetails("Travel to Eadgar", Arrays.asList(travelToTenzing, climbOverStile,
				climbOverRocks, enterSecretEntrance, getBerryKey, freeEadgar, goUpStairsPrison, goUpToTopFloorStronghold,
				enterEadgarsCave, talkToEadgar), climbingBoots);
		}

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, goUpToSanfew);
		startQuest.addStep(inSanfewRoom, talkToSanfew);

		steps.put(0, startQuest);

		ConditionalStep enterTheStronghold = new ConditionalStep(this, getCoinsOrBoots);
		enterTheStronghold.addStep(new Conditions(inEadgarsCave, freedEadgar), talkToEadgar);
		enterTheStronghold.addStep(new Conditions(inTrollheimArea, freedEadgar), enterEadgarsCave);
		enterTheStronghold.addStep(new Conditions(inStrongholdFloor2, freedEadgar), exitStronghold);
		enterTheStronghold.addStep(new Conditions(inStrongholdFloor1, freedEadgar), goUpToTopFloorStronghold);
		enterTheStronghold.addStep(new Conditions(inPrison, freedEadgar), goUpStairsPrison);
		enterTheStronghold.addStep(new Conditions(inPrison, hasCellKey2), freeEadgar);
		enterTheStronghold.addStep(inPrison, freeEadgar);
		enterTheStronghold.addStep(inTrollArea1, enterSecretEntrance);
		enterTheStronghold.addStep(new Conditions(hasClimbingBoots, onMountainPath), climbOverRocks);
		enterTheStronghold.addStep(new Conditions(hasClimbingBoots, inTenzingHut), climbOverStile);
		enterTheStronghold.addStep(hasClimbingBoots, travelToTenzing);
		enterTheStronghold.addStep(hasCoins, buyClimbingBoots);

		steps.put(10, enterTheStronghold);

		ConditionalStep talkToCooksAboutGoutweed = new ConditionalStep(this, getCoinsOrBoots);
		talkToCooksAboutGoutweed.addStep(inEadgarsCave, leaveEadgarsCave);
		talkToCooksAboutGoutweed.addStep(inTrollheimArea, enterStronghold);
		talkToCooksAboutGoutweed.addStep(inStrongholdFloor2, goDownSouthStairs);
		talkToCooksAboutGoutweed.addStep(inStrongholdFloor1, talkToCook);
		talkToCooksAboutGoutweed.addStep(inPrison, goUpStairsPrison);
		talkToCooksAboutGoutweed.addStep(inTrollArea1, enterSecretEntrance);
		talkToCooksAboutGoutweed.addStep(new Conditions(hasClimbingBoots, onMountainPath), climbOverRocks);
		talkToCooksAboutGoutweed.addStep(new Conditions(hasClimbingBoots, inTenzingHut), climbOverStile);
		talkToCooksAboutGoutweed.addStep(hasClimbingBoots, travelToTenzing);
		talkToCooksAboutGoutweed.addStep(hasCoins, buyClimbingBoots);

		steps.put(15, talkToCooksAboutGoutweed);

		ConditionalStep returnToEadgar = new ConditionalStep(this, getCoinsOrBoots);
		returnToEadgar.addStep(inEadgarsCave, talkToEadgarFromCook);
		returnToEadgar.addStep(inTrollheimArea, enterEadgarsCaveFromCook);
		returnToEadgar.addStep(inStrongholdFloor2, exitStrongholdFromCook);
		returnToEadgar.addStep(inStrongholdFloor1, goUpToTopFloorStrongholdFromCook);
		returnToEadgar.addStep(inPrison, goUpStairsPrison);
		returnToEadgar.addStep(inTrollArea1, enterSecretEntrance);
		returnToEadgar.addStep(new Conditions(hasClimbingBoots, onMountainPath), climbOverRocks);
		returnToEadgar.addStep(new Conditions(hasClimbingBoots, inTenzingHut), climbOverStile);
		returnToEadgar.addStep(hasClimbingBoots, travelToTenzing);


		steps.put(25, returnToEadgar);

		ConditionalStep poisonTheParrot = new ConditionalStep(this, talkToPete);
		poisonTheParrot.addStep(new Conditions(parrot, inEadgarsCave), talkToEadgarWithParrot);
		poisonTheParrot.addStep(parrot, enterEadgarsCaveWithParrot);
		poisonTheParrot.addStep(alcoChunks, useChunksOnParrot);
		poisonTheParrot.addStep(new Conditions(askedAboutAlcohol, askedAboutPineapple), useVodkaOnChunks);
		poisonTheParrot.addStep(askedAboutPineapple, talkToPeteAboutAlcohol);
		poisonTheParrot.addStep(askedAboutAlcohol, talkToPeteAboutPineapple);

		steps.put(30, poisonTheParrot);

		ConditionalStep useParrotOnRack = new ConditionalStep(this, enterPrisonWithParrot);
		useParrotOnRack.addStep(inTrollheimArea, enterStrongholdWithParrot);
		useParrotOnRack.addStep(inStrongholdFloor2, goDownNorthStairsWithParrot);
		useParrotOnRack.addStep(inStrongholdFloor1, goDownToPrisonWithParrot);
		useParrotOnRack.addStep(inPrison, parrotOnRack);
		useParrotOnRack.addStep(inEadgarsCave, leaveEadgarsCaveWithParrot);

		steps.put(50, useParrotOnRack);

		ConditionalStep bringItemsToEadgar = new ConditionalStep(this, talkToTegid);
		bringItemsToEadgar.addStep(new Conditions(robe, inEadgarsCave), talkToEadgarWithItems);
		bringItemsToEadgar.addStep(robe, enterEadgarsCaveWithItems);

		steps.put(60, bringItemsToEadgar);
		steps.put(70, bringItemsToEadgar);

		ConditionalStep makePotion = new ConditionalStep(this, pickThistle);
		makePotion.addStep(new Conditions(trollPotion, inEadgarsCave), giveTrollPotionToEadgar);
		makePotion.addStep(trollPotion, enterEadgarsCaveWithTrollPotion);
		makePotion.addStep(groundThistle, useGroundThistleOnRanarr);
		makePotion.addStep(driedThistle, grindThistle);
		makePotion.addStep(new Conditions(thistle, fireNearby), useThistleOnFire);
		makePotion.addStep(new Conditions(logs1, tinderbox, thistle), lightFire);
		makePotion.addStep(thistle, useThistleOnTrollFire);
		makePotion.addStep(inEadgarsCave, leaveEadgarsCaveForThistle);

		steps.put(80, makePotion);

		ConditionalStep fetchParrot = new ConditionalStep(this, enterPrisonForParrot);
		fetchParrot.addStep(inTrollheimArea, enterStrongholdForParrot);
		fetchParrot.addStep(inStrongholdFloor2, goDownNorthStairsForParrot);
		fetchParrot.addStep(inStrongholdFloor1, goDownToPrisonForParrot);
		fetchParrot.addStep(inPrison, getParrotFromRack);
		fetchParrot.addStep(inEadgarsCave, leaveEadgarsCaveForParrot);
		steps.put(85, fetchParrot);

		ConditionalStep returnParrotToEadgar = new ConditionalStep(this, enterEadgarCaveWithTrainedParrot);
		returnParrotToEadgar.addStep(inEadgarsCave, talkToEadgarWithTrainedParrot);
		returnParrotToEadgar.addStep(inTrollheimArea, enterEadgarCaveWithTrainedParrot);
		returnParrotToEadgar.addStep(inStrongholdFloor2, leaveStrongholdWithParrot);
		returnParrotToEadgar.addStep(inStrongholdFloor1, goUpToTopFloorWithParrot);
		returnParrotToEadgar.addStep(inPrison, leavePrisonWithParrot);

		steps.put(86, returnParrotToEadgar);

		ConditionalStep bringManToBurntmeat = new ConditionalStep(this, enterStrongholdWithScarecrow);
		bringManToBurntmeat.addStep(inEadgarsCave, leaveEadgarsCaveWithScarecrow);
		bringManToBurntmeat.addStep(inStrongholdFloor2, goDownSouthStairsWithScarecrow);
		bringManToBurntmeat.addStep(inStrongholdFloor1, talkToCookWithScarecrow);
		bringManToBurntmeat.addStep(inPrison, goUpStairsPrison);

		steps.put(87, bringManToBurntmeat);

		ConditionalStep getTheGoutweed = new ConditionalStep(this, talkToBurntmeat);
		getTheGoutweed.addStep(new Conditions(inSanfewRoom, goutweed), returnToSanfew);
		getTheGoutweed.addStep(goutweed, returnUpToSanfew);
		getTheGoutweed.addStep(new Conditions(inStoreroom, storeroomKey), getGoutweed);
		getTheGoutweed.addStep(storeroomKey, goDownToStoreroom);
		getTheGoutweed.addStep(foundOutAboutKey, searchDrawers);

		steps.put(90, getTheGoutweed);

		ConditionalStep returnGoutWeed = new ConditionalStep(this, goDownToStoreroom);
		returnGoutWeed.addStep(new Conditions(inSanfewRoom, goutweed), returnToSanfew);
		returnGoutWeed.addStep(goutweed, returnUpToSanfew);
		returnGoutWeed.addStep(inStoreroom, getGoutweed);

		steps.put(100, returnGoutWeed);

		return steps;
	}

	public void setupItemRequirements()
	{
		climbingBoots = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS);
		climbingBootsEquipped = new ItemRequirement("Climbing boots", ItemID.CLIMBING_BOOTS, 1, true);
		climbingBootsOr12Coins = new ItemRequirement("Climbing boots or 12 coins", ItemID.CLIMBING_BOOTS);
		vodka = new ItemRequirement("Vodka", ItemID.VODKA);
		pineappleChunks = new ItemRequirement("Pineapple chunks", ItemID.PINEAPPLE_CHUNKS);
		pineappleChunks.setTooltip("You can make these by using a knife on a pineapple");
		logs2 = new ItemRequirement("Logs", ItemID.LOGS, 2);
		logs1 = new ItemRequirement("Logs", ItemCollections.getLogsForFire());
		grain10 = new ItemRequirement("Grain", ItemID.GRAIN, 10);
		rawChicken5 = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN, 5);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		pestleAndMortar = new ItemRequirement("Pestle and Mortar", ItemID.PESTLE_AND_MORTAR);
		ranarrPotionUnf = new ItemRequirement("Ranarr potion (unf)", ItemID.RANARR_POTION_UNF);
		ardougneTeleport = new ItemRequirement("Ardougne teleport", ItemID.ARDOUGNE_TELEPORT);
		coins12 = new ItemRequirement("Coins", ItemID.COINS_995, 12);
		cellKey2 = new ItemRequirement("Cell key 2", ItemID.CELL_KEY_2);
		vodkaHighlight = new ItemRequirement("Vodka", ItemID.VODKA);
		vodkaHighlight.setTooltip("You can buy some from the Gnome Stronghold drinks shop");
		vodkaHighlight.setHighlightInInventory(true);

		pineappleChunksHighlight = new ItemRequirement("Pineapple chunks", ItemID.PINEAPPLE_CHUNKS);
		pineappleChunksHighlight.setTooltip("You can cut a pineapple into chunks with a knife");
		pineappleChunksHighlight.setHighlightInInventory(true);

		alcoChunks = new ItemRequirement("Alco-chunks", ItemID.ALCOCHUNKS);
		alcoChunks.setHighlightInInventory(true);

		parrot = new ItemRequirement("Drunk parrot", ItemID.DRUNK_PARROT);
		parrot.setTooltip("You can get another by using alco-chunks on the aviary hatch of the parrot cage in Ardougne Zoo");

		parrotHighlighted = new ItemRequirement("Parrot", ItemID.DRUNK_PARROT);
		parrotHighlighted.setHighlightInInventory(true);
		parrotHighlighted.setTooltip("You can get another by using alco-chunks on the aviary hatch of the parrot cage in Ardougne Zoo");

		robe = new ItemRequirement("Robe", ItemID.DIRTY_ROBE);

		thistle = new ItemRequirement("Troll thistle", ItemID.TROLL_THISTLE);
		thistle.setHighlightInInventory(true);

		logHighlight = new ItemRequirement("Logs", ItemCollections.getLogsForFire());
		logHighlight.setHighlightInInventory(true);

		tinderboxHighlight = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderboxHighlight.setHighlightInInventory(true);

		driedThistle = new ItemRequirement("Dried thistle", ItemID.DRIED_THISTLE);
		driedThistle.setHighlightInInventory(true);

		pestleAndMortarHighlight = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlight.setHighlightInInventory(true);

		groundThistle = new ItemRequirement("Ground thistle", ItemID.GROUND_THISTLE);
		groundThistle.setHighlightInInventory(true);

		ranarrUnfHighlight = new ItemRequirement("Ranarr potion (unf)", ItemID.RANARR_POTION_UNF);
		ranarrUnfHighlight.setHighlightInInventory(true);

		trollPotion = new ItemRequirement("Troll potion", ItemID.TROLL_POTION);

		trainedParrot = new ItemRequirement("Drunk parrot", ItemID.DRUNK_PARROT);
		trainedParrot.setTooltip("If you lost the parrot Eadgar will have it");

		fakeMan = new ItemRequirement("Fake man", ItemID.FAKE_MAN);
		fakeMan.setTooltip("You can get another from Eadgar if you lose it");

		storeroomKey = new ItemRequirement("Storeroom key", ItemID.STOREROOM_KEY);

		goutweed = new ItemRequirement("Goutweed", ItemID.GOUTWEED);
	}

	public void loadZones()
	{
		sanfewRoom = new Zone(new WorldPoint(2893, 3423, 1), new WorldPoint(2903, 3433, 1));
		tenzingHut = new Zone(new WorldPoint(2814, 3553, 0), new WorldPoint(2822, 3562, 0));
		mountainPath1 = new Zone(new WorldPoint(2814, 3563, 0), new WorldPoint(2823, 3593, 0));
		mountainPath2 = new Zone(new WorldPoint(2824, 3589, 0), new WorldPoint(2831, 3599, 0));
		mountainPath3 = new Zone(new WorldPoint(2832, 3595, 0), new WorldPoint(2836, 3603, 0));
		mountainPath4 = new Zone(new WorldPoint(2837, 3601, 0), new WorldPoint(2843, 3607, 0));
		mountainPath5 = new Zone(new WorldPoint(2844, 3607, 0), new WorldPoint(2876, 3611, 0));
		trollArea1 = new Zone(new WorldPoint(2822, 3613, 0), new WorldPoint(2896, 3646, 0));
		prison = new Zone(new WorldPoint(2822, 10049, 0), new WorldPoint(2859, 10110, 0));
		strongholdFloor1 = new Zone(new WorldPoint(2820, 10048, 1), new WorldPoint(2862, 10110, 1));
		strongholdFloor2 = new Zone(new WorldPoint(2820, 10048, 2), new WorldPoint(2862, 10110, 2));
		eadgarsCave = new Zone(new WorldPoint(2884, 10074, 2), new WorldPoint(2901, 10091, 2));
		trollheimArea = new Zone(new WorldPoint(2836, 3651, 0), new WorldPoint(2934, 3773, 0));
		storeroom = new Zone(new WorldPoint(2850, 10063, 0), new WorldPoint(2870, 10093, 0));
	}

	public void setupConditions()
	{
		inSanfewRoom = new ZoneRequirement(sanfewRoom);
		hasClimbingBoots = climbingBoots;
		hasCoins = coins12;
		inTenzingHut = new ZoneRequirement(tenzingHut);
		onMountainPath = new ZoneRequirement(mountainPath1, mountainPath2, mountainPath3, mountainPath4, mountainPath5);
		inTrollArea1 = new ZoneRequirement(trollArea1);
		inPrison = new ZoneRequirement(prison);
		freedEadgar = new VarbitRequirement(0, 1);
		hasCellKey2 = cellKey2;
		inStrongholdFloor1 = new ZoneRequirement(strongholdFloor1);
		inStrongholdFloor2 = new ZoneRequirement(strongholdFloor2);
		inEadgarsCave = new ZoneRequirement(eadgarsCave);
		inTrollheimArea = new ZoneRequirement(trollheimArea);

		askedAboutAlcohol = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Just recently."));
		askedAboutPineapple = new Conditions(true, new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "fruit and grain mostly"));

		fireNearby = new ObjectCondition(ObjectID.FIRE_26185);

		foundOutAboutKey = new Conditions(true, new WidgetTextRequirement(217, 4, "That's some well-guarded secret alright"));
		inStoreroom = new ZoneRequirement(storeroom);
	}

	public void setupSteps()
	{
		goUpToSanfew = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2899, 3429, 0), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "Talk to Sanfew upstairs in the Taverley herblore store.");
		talkToSanfew.addDialogStep("Ask general questions.");
		talkToSanfew.addDialogStep("Have you any more work for me, to help reclaim the circle?");
		talkToSanfew.addDialogStep("I'll do it.");
		talkToSanfew.addSubSteps(goUpToSanfew);

		getCoinsOrBoots = new DetailedQuestStep(this, "Get some climbing boots or 12 coins.", climbingBootsOr12Coins);

		travelToTenzing = new DetailedQuestStep(this, new WorldPoint(2820, 3555, 0), "Follow the path west of Burthorpe, then go along the path going south.");
		buyClimbingBoots = new NpcStep(this, NpcID.TENZING, new WorldPoint(2820, 3555, 0), "Follow the path west of Burthorpe, then go along the path going south. Buy some climbing boots from Tenzing in his hut here.", coins12);
		buyClimbingBoots.addDialogStep("Can I buy some Climbing boots?");
		buyClimbingBoots.addDialogStep("OK, sounds good.");
		travelToTenzing.addSubSteps(getCoinsOrBoots, buyClimbingBoots);

		climbOverStile = new ObjectStep(this, ObjectID.STILE_3730, new WorldPoint(2817, 3563, 0), "Climb over the stile north of Tenzing.");
		climbOverRocks = new ObjectStep(this, ObjectID.ROCKS_3748, new WorldPoint(2856, 3612, 0), "Follow the path until you reach some rocks. Climb over them.", climbingBoots);

		enterSecretEntrance = new ObjectStep(this, ObjectID.SECRET_DOOR, new WorldPoint(2828, 3647, 0), "Enter the Secret Door to the Troll Stronghold.");

		if (client.getRealSkillLevel(Skill.THIEVING) >= 30)
		{
			getBerryKey = new NpcStep(this, NpcID.BERRY_4134, new WorldPoint(2833, 10083, 0), "Pickpocket or kill Berry for a cell key.");
		}
		else
		{
			getBerryKey = new NpcStep(this, NpcID.BERRY_4134, new WorldPoint(2833, 10083, 0), "Kill Berry for a cell key.");
		}

		freeEadgar = new ObjectStep(this, ObjectID.CELL_DOOR_3765, new WorldPoint(2832, 10082, 0), "Unlock Eadgar's cell.");

		goUpStairsPrison = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Go up the stairs from the prison.");
		goUpStairsPrison.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		goUpToTopFloorStronghold = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10109, 1), "Go up to the next floor of the Stronghold.");
		goUpToTopFloorStronghold.setWorldMapPoint(new WorldPoint(2906, 10140, 1));

		exitStronghold = new ObjectStep(this, ObjectID.EXIT_3772, new WorldPoint(2838, 10091, 2), "Leave the Stronghold.");
		exitStronghold.setWorldMapPoint(new WorldPoint(2965, 10155, 1));

		enterEadgarsCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3759, new WorldPoint(2893, 3673, 0), "Climb to the top of Trollheim and enter Eadgar's cave.");

		talkToEadgar = new NpcStep(this, NpcID.EADGAR, new WorldPoint(2891, 10086, 2), "Talk to Eadgar.");
		talkToEadgar.addDialogStep("I need to find some goutweed.");

		leaveEadgarsCave = new ObjectStep(this, ObjectID.CAVE_EXIT_3760, new WorldPoint(2893, 10073, 2), "Leave Eadgar's cave.");
		enterStronghold = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Enter the Troll Stronghold.");
		goDownSouthStairs = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Go down the south staircase.");
		goDownSouthStairs.setWorldMapPoint(new WorldPoint(2971, 10115, 1));
		talkToCook = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1), "Talk to Burntmeat.");
		talkToCook.setWorldMapPoint(new WorldPoint(2911, 10087, 1));

		goUpToTopFloorStrongholdFromCook = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10052, 1), "Go up to the next floor of the Stronghold.");
		goUpToTopFloorStrongholdFromCook.setWorldMapPoint(new WorldPoint(2907, 10083, 1));

		exitStrongholdFromCook = new ObjectStep(this, ObjectID.EXIT_3772, new WorldPoint(2838, 10091, 2), "Leave the Stronghold.");
		exitStrongholdFromCook.setWorldMapPoint(new WorldPoint(2965, 10155, 1));

		enterEadgarsCaveFromCook = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3759, new WorldPoint(2893, 3673, 0), "Climb to the top of Trollheim and enter Eadgar's cave.");

		talkToEadgarFromCook = new NpcStep(this, NpcID.EADGAR, new WorldPoint(2891, 10086, 2), "Talk to Eadgar in his cave on top of Trollheim.");
		talkToEadgarFromCook.addSubSteps(goUpToTopFloorStrongholdFromCook, exitStrongholdFromCook, enterEadgarsCaveFromCook);

		talkToPete = new NpcStep(this, NpcID.PARROTY_PETE, new WorldPoint(2612, 3285, 0), "Travel to Ardougne Zoo and talk to Parroty Pete. Ask him both question 2 and 3, then use the vodka on the pineapple chunks.", pineappleChunks, vodka);
		talkToPete.addDialogStep("No thanks, Eadgar.");
		talkToPete.addDialogStep("What do you feed them?");
		talkToPete.addDialogStep("When did you add it?");

		talkToPeteAboutAlcohol = new NpcStep(this, NpcID.PARROTY_PETE, new WorldPoint(2612, 3285, 0), "Travel to Ardougne Zoo and talk to Parroty Pete. Ask him question 3 then use the vodka on the pineapple chunks.", pineappleChunks, vodka);
		talkToPeteAboutAlcohol.addDialogStep("When did you add it?");

		talkToPeteAboutPineapple = new NpcStep(this, NpcID.PARROTY_PETE, new WorldPoint(2612, 3285, 0), "Travel to Ardougne Zoo and talk to Parroty Pete. Ask question 2 then use the vodka on the pineapple chunks.", pineappleChunks, vodka);
		talkToPeteAboutPineapple.addDialogStep("What do you feed them?");

		useVodkaOnChunks = new DetailedQuestStep(this, "Use vodka on your pineapple chunks", pineappleChunksHighlight, vodkaHighlight);

		talkToPete.addSubSteps(talkToPeteAboutAlcohol, talkToPeteAboutPineapple, useVodkaOnChunks);

		useChunksOnParrot = new ObjectStep(this, ObjectID.AVIARY_HATCH, new WorldPoint(2611, 3287, 0), "Use the alco-chunks on the aviary hatch of the parrot cage.", alcoChunks);
		useChunksOnParrot.addIcon(ItemID.ALCOCHUNKS);

		enterEadgarsCaveWithParrot = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3759, new WorldPoint(2893, 3673, 0), "Return the parrot to Eadgar on top of Trollheim.", parrot, climbingBoots);

		talkToEadgarWithParrot = new NpcStep(this, NpcID.EADGAR, new WorldPoint(2891, 10086, 2), "Return the parrot to Eadgar on top of Trollheim.");
		talkToEadgarWithParrot.addSubSteps(enterEadgarsCaveWithParrot);

		leaveEadgarsCaveWithParrot = new ObjectStep(this, ObjectID.CAVE_EXIT_3760, new WorldPoint(2893, 10073, 2), "Take the parrot to the Troll Stronghold prison.", parrot);
		leaveEadgarsCaveWithParrot.addDialogStep("No thanks, Eadgar.");

		enterStrongholdWithParrot = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Take the parrot to the Troll Stronghold prison, and hide it in the rack there.", parrot);
		goDownNorthStairsWithParrot = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Take the parrot to the Troll Stronghold prison, and hide it in the rack there.", parrot);
		goDownNorthStairsWithParrot.setWorldMapPoint(new WorldPoint(2971, 10172, 1));
		goDownToPrisonWithParrot = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2853, 10108, 1), "Take the parrot to the Troll Stronghold prison, and hide it in the rack there.", parrot);
		goDownToPrisonWithParrot.setWorldMapPoint(new WorldPoint(2917, 10140, 1));

		enterPrisonWithParrot = new ObjectStep(this, ObjectID.SECRET_DOOR, new WorldPoint(2828, 3647, 0), "Take the parrot to the Troll Stronghold prison, and hide it in the rack there.", parrot, climbingBoots);

		parrotOnRack = new ObjectStep(this, ObjectID.RACK_3821, new WorldPoint(2829, 10097, 0), "Use the parrot on a rack in the prison.", parrotHighlighted);
		parrotOnRack.setWorldMapPoint(new WorldPoint(2830, 10098, 1));
		parrotOnRack.addIcon(ItemID.DRUNK_PARROT);

		enterStrongholdWithParrot.addSubSteps(leaveEadgarsCaveWithParrot, goDownNorthStairsWithParrot, goDownToPrisonWithParrot, parrotOnRack, enterPrisonWithParrot);

		talkToTegid = new NpcStep(this, NpcID.TEGID, new WorldPoint(2910, 3417, 0), "Talk to Tegid in the south of Taverley for a dirty robe. Once you have it, return to Eadgar with all the items he needs.", robe, grain10, rawChicken5, logs2, climbingBoots, ranarrPotionUnf, tinderbox, pestleAndMortar);
		talkToTegid.addDialogStep("Sanfew won't be happy...");
		enterEadgarsCaveWithItems = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3759, new WorldPoint(2893, 3673, 0), "Bring Eadgar all the listed items. Check the quest log if you're not sure what items you've already handed in.", robe, grain10, rawChicken5, logs2, climbingBoots, ranarrPotionUnf, tinderbox, pestleAndMortar);
		talkToEadgarWithItems = new NpcStep(this, NpcID.EADGAR, new WorldPoint(2891, 10086, 2), "Bring Eadgar all the listed items. Check the quest log if you're not sure what items you've already handed in.", robe, grain10, rawChicken5, logs2, climbingBoots, ranarrPotionUnf, tinderbox, pestleAndMortar);

		talkToEadgarWithItems.addSubSteps(enterEadgarsCaveWithItems);

		leaveEadgarsCaveForThistle = new ObjectStep(this, ObjectID.CAVE_EXIT_3760, new WorldPoint(2893, 10073, 2), "Go pick a troll thistle from outside Eadgar's cave.", ranarrPotionUnf);
		leaveEadgarsCaveForThistle.addDialogStep("No thanks, Eadgar.");

		pickThistle = new NpcStep(this, NpcID.THISTLE, new WorldPoint(2887, 3675, 0), "Go pick a troll thistle from outside Eadgar's cave.", ranarrPotionUnf, logs1, tinderbox);
		pickThistle.addSubSteps(leaveEadgarsCaveForThistle);

		lightFire = new DetailedQuestStep(this, "Light a fire then use the troll thistle on it.", logHighlight, tinderboxHighlight);

		useThistleOnFire = new ObjectStep(this, ObjectID.FIRE_26185, "Use the troll thistle on the fire.", thistle);
		useThistleOnFire.addIcon(ItemID.THISTLE);

		useThistleOnTrollFire = new ObjectStep(this, ObjectID.FIRE, new WorldPoint(2849, 3678, 0), "Use the troll thistle on one of the fire outside the Troll Stronghold.", thistle);
		useThistleOnTrollFire.addIcon(ItemID.THISTLE);

		useThistleOnFire.addSubSteps(useThistleOnTrollFire);

		grindThistle = new DetailedQuestStep(this, "Use the pestle and mortar on the dried thistle", pestleAndMortar, driedThistle);

		useGroundThistleOnRanarr = new DetailedQuestStep(this, "Use the ground thistle on a ranarr potion (unf)", groundThistle, ranarrUnfHighlight);

		enterEadgarsCaveWithTrollPotion = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3759, new WorldPoint(2893, 3673, 0), "Bring Eadgar the troll potion.", trollPotion);
		giveTrollPotionToEadgar = new NpcStep(this, NpcID.EADGAR, new WorldPoint(2891, 10086, 2), "Bring Eadgar the troll potion.", trollPotion);
		giveTrollPotionToEadgar.addSubSteps(enterEadgarsCaveWithTrollPotion);

		leaveEadgarsCaveForParrot = new ObjectStep(this, ObjectID.CAVE_EXIT_3760, new WorldPoint(2893, 10073, 2), "Get the parrot from the Troll Stronghold prison.");
		enterStrongholdForParrot = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Get the parrot from the Troll Stronghold prison.");

		goDownNorthStairsForParrot = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10109, 2), "Get the parrot from the Troll Stronghold prison.");
		goDownNorthStairsForParrot.setWorldMapPoint(new WorldPoint(2971, 10172, 1));

		goDownToPrisonForParrot = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2853, 10108, 1), "Get the parrot from the Troll Stronghold prison.");
		goDownToPrisonForParrot.setWorldMapPoint(new WorldPoint(2917, 10140, 1));

		enterPrisonForParrot = new ObjectStep(this, ObjectID.SECRET_DOOR, new WorldPoint(2828, 3647, 0), "Get the parrot from the Troll Stronghold prison.", climbingBoots);

		getParrotFromRack = new ObjectStep(this, ObjectID.RACK_3821, new WorldPoint(2829, 10097, 0), "Get the parrot from the rack in the prison.");
		getParrotFromRack.setWorldMapPoint(new WorldPoint(2830, 10098, 1));

		enterStrongholdForParrot.addSubSteps(leaveEadgarsCaveForParrot, goDownNorthStairsForParrot, goDownToPrisonForParrot, enterPrisonForParrot, getParrotFromRack);

		leavePrisonWithParrot = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2853, 10107, 0), "Return to Eadgar with the parrot.", trainedParrot);
		leavePrisonWithParrot.setWorldMapPoint(new WorldPoint(2853, 10106, 1));

		goUpToTopFloorWithParrot = new ObjectStep(this, ObjectID.STONE_STAIRCASE, new WorldPoint(2843, 10109, 1), "Return to Eadgar with the parrot.", trainedParrot);
		goUpToTopFloorWithParrot.setWorldMapPoint(new WorldPoint(2906, 10140, 1));

		leaveStrongholdWithParrot = new ObjectStep(this, ObjectID.EXIT_3772, new WorldPoint(2838, 10091, 2), "Return to Eadgar with the parrot.", trainedParrot);
		leaveStrongholdWithParrot.setWorldMapPoint(new WorldPoint(2965, 10155, 1));

		enterEadgarCaveWithTrainedParrot = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3759, new WorldPoint(2893, 3673, 0), "Return to Eadgar with the parrot.", trainedParrot);

		talkToEadgarWithTrainedParrot = new NpcStep(this, NpcID.EADGAR, new WorldPoint(2891, 10086, 2), "Return to Eadgar with the parrot.", trainedParrot);
		leaveStrongholdWithParrot.addSubSteps(leavePrisonWithParrot, goUpToTopFloorWithParrot, leaveStrongholdWithParrot, enterEadgarCaveWithTrainedParrot, talkToEadgarWithTrainedParrot);

		leaveEadgarsCaveWithScarecrow = new ObjectStep(this, ObjectID.CAVE_EXIT_3760, new WorldPoint(2893, 10073, 2), "Take the fake man to Burntmeat.", fakeMan);
		leaveEadgarsCaveWithScarecrow.addDialogStep("No thanks, Eadgar.");
		enterStrongholdWithScarecrow = new ObjectStep(this, ObjectID.STRONGHOLD, new WorldPoint(2839, 3690, 0), "Take the fake man to Burntmeat.", fakeMan);
		goDownSouthStairsWithScarecrow = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2844, 10052, 2), "Take the fake man to Burntmeat.", fakeMan);
		goDownSouthStairsWithScarecrow.setWorldMapPoint(new WorldPoint(2971, 10115, 1));
		talkToCookWithScarecrow = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1), "Take the fake man to Burntmeat.", fakeMan);
		talkToCookWithScarecrow.setWorldMapPoint(new WorldPoint(2911, 10087, 1));

		talkToBurntmeat = new NpcStep(this, NpcID.BURNTMEAT, new WorldPoint(2845, 10057, 1), "Talk to Burntmeat in the Troll Stronghold kitchen.");
		talkToBurntmeat.setWorldMapPoint(new WorldPoint(2911, 10087, 1));
		talkToBurntmeat.addDialogStep("So, where can I get some goutweed?");

		enterStrongholdWithScarecrow.addSubSteps(leaveEadgarsCaveWithScarecrow, goDownSouthStairsWithScarecrow, talkToCookWithScarecrow, talkToBurntmeat);

		searchDrawers = new ObjectStep(this, ObjectID.KITCHEN_DRAWERS, new WorldPoint(2853, 10050, 1), "Search the kitchen drawers south east of Burntmeat.");
		searchDrawers.addAlternateObjects(ObjectID.KITCHEN_DRAWERS_3817);

		goDownToStoreroom = new ObjectStep(this, ObjectID.STONE_STAIRCASE_3789, new WorldPoint(2853, 10061, 1), "Go down to the storeroom from the Troll Stronghold kitchen.");

		enterStoreroomDoor = new ObjectStep(this, ObjectID.STOREROOM_DOOR, new WorldPoint(2869, 10085, 0), "Enter the storeroom.", storeroomKey);

		getGoutweed = new ObjectStep(this, ObjectID.GOUTWEED_CRATE, new WorldPoint(2857, 10074, 0), "Search the goutweed crates for goutweed. You'll need to avoid the troll guards or you'll be kicked out and take damage.");

		returnUpToSanfew = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2899, 3429, 0), "If you wish to do Dream Mentor or Dragon Slayer II, grab two more goutweed. Afterwards, return to Sanfew upstairs in the Taverley herblore store.", goutweed);
		returnToSanfew = new NpcStep(this, NpcID.SANFEW, new WorldPoint(2899, 3429, 1), "If you wish to do Dream Mentor or Dragon Slayer II, grab two more goutweed. Afterwards, return to Sanfew upstairs in the Taverley herblore store.", goutweed);
		returnToSanfew.addDialogStep("Ask general questions.");
		returnToSanfew.addSubSteps(returnUpToSanfew);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(climbingBootsOr12Coins);
		reqs.add(vodka);
		reqs.add(pineappleChunks);
		reqs.add(logs2);
		reqs.add(grain10);
		reqs.add(rawChicken5);
		reqs.add(tinderbox);
		reqs.add(pestleAndMortar);
		reqs.add(ranarrPotionUnf);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ardougneTeleport);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.HERBLORE, 11000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to use the Trollheim teleport"),
				new UnlockReward("Ability to use Scrolls of Redirection to Trollheim."),
				new UnlockReward("Ability to trade Goutweed to Sanfew for herbs."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest", Collections.singletonList(talkToSanfew)));

		allSteps.add(travelToEadgarPanel);

		allSteps.add(new PanelDetails("Talk to Burntmeat", Arrays.asList(leaveEadgarsCave, enterStronghold, goDownSouthStairs,
			talkToCook, talkToEadgarFromCook), climbingBoots));

		allSteps.add(new PanelDetails("Get a parrot", Arrays.asList(talkToPete, useChunksOnParrot, talkToEadgarWithParrot,
			enterStrongholdWithParrot), vodka, pineappleChunks, climbingBoots));

		allSteps.add(new PanelDetails("Making a fake man", Arrays.asList(talkToTegid, talkToEadgarWithItems, pickThistle,
			lightFire, useThistleOnFire, grindThistle, useGroundThistleOnRanarr, giveTrollPotionToEadgar, enterStrongholdForParrot,
			leaveStrongholdWithParrot), climbingBoots, logs2, tinderbox, pestleAndMortar, grain10, rawChicken5, ranarrPotionUnf));

		allSteps.add(new PanelDetails("Get the Goutweed", Arrays.asList(enterStrongholdWithScarecrow, searchDrawers,
			goDownToStoreroom, enterStoreroomDoor, getGoutweed, returnToSanfew)));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TROLL_STRONGHOLD, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.HERBLORE, 31, true));
		return req;
	}
}
