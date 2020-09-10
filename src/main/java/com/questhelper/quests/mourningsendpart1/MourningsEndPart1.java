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
package com.questhelper.quests.mourningsendpart1;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
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
	quest = QuestHelperQuest.MOURNINGS_END_PART_I
)
public class MourningsEndPart1 extends BasicQuestHelper
{
	ItemRequirement bearFur, silk2, redDye, yellowDye, greenDye, blueDye, waterBucket, feather, rottenApple, toadCrunchies, magicLogs, leather, ogreBellows, coal20,
		coal20OrNaphtha, blueBellow, redBellow, yellowBellow, greenBellow, mournerMask, bloodyMournerBody, mournerLegsBroken, mournerBoots, mournerGloves, mournerCloak,
		mournerLetter, tegidsSoap, mournerBody, mournerLegs, sieve, tarnishedKey, fullMourners, equippedMournerMask, equippedMournerBody, equippedMournerLegs, equippedMournerCloak,
		equippedMournerGloves, equippedMournerBoots, brokenDevice, featherHighlight, fixedDevice, redToad, yellowToad, greenToad, blueToad, fixedDeviceEquipped, emptyBarrel, barrelOfRottenApples,
		appleBarrel, naphtha, naphthaAppleMix, toxicNaphtha, toxicPowder;

	ConditionForStep hasAllMournerItems, mournerItemsNearby, hasSoap, cleanedTop, repairedTrousers, inMournerHQ, inMournerBasement, knowWeaknesses, torturedGnome, talkedWithItem, releasedGnome, repairedDevice,
		learntAboutToads, hasAllToads, blueToadLoaded, redToadLoaded, yellowToadLoaded, greenToadLoaded, redToadGot, yellowToadGot, greenToadGot, blueToadGot, greenDyed, yellowDyed, redDyed, blueDyed, hasRottenApple,
		givenRottenApple, receivedSieve, hasBarrel, hasRottenApples, hasAppleBarrel, hasNaphtha, hasNaphthaAppleMix, hasToxicNaphtha, hadToxicPowder, poisoned1, poisoned2, poisoned3, twoPoisoned;

	QuestStep talkToIslwyn, talkToArianwyn, killMourner, pickUpLoot, searchLaundry, useSoapOnTop, talkToOronwen, enterMournerBase, enterMournerBaseNoPass, enterBasement, talkToEssyllt, talkToGnome,
		enterMournerBaseForGnome, enterBasementForGnome, useFeatherOnGnome, enterMournerBaseAfterTorture, enterBasementAfterTorture, talkToGnomeWithItems, releaseGnome, giveGnomeItems, askAboutToads,
		getToads, loadBlueToad, shootBlueToad, loadRedToad, shootRedToad, loadGreenToad, shootGreenToad, loadYellowToad, shootYellowToad, dyeSheep, enterBaseAfterSheep, enterBasementAfterSheep,
		talkToEssylltAfterSheep, pickUpRottenApple, talkToElena, talkToElenaNoApple, pickUpBarrel, useBarrelOnPile, useApplesOnPress, getNaphtha, useNaphthaOnBarrel, useSieveOnBarrel, cookNaphtha,
		usePowderOnFood1, usePowderOnFood2, enterMournerBaseAfterPoison, enterMournerBasementAfterPoison, talkToEssylltAfterPoison, returnToArianwyn;

	ConditionalStep getItems, cleanTopSteps, repairTrousersSteps;

	Zone mournerHQ, mournerHQ2, mournerBasement;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToIslwyn);
		steps.put(1, talkToIslwyn);

		steps.put(2, talkToArianwyn);

		getItems = new ConditionalStep(this, killMourner);
		getItems.addStep(mournerItemsNearby, pickUpLoot);
		getItems.setLockingCondition(hasAllMournerItems);

		cleanTopSteps = new ConditionalStep(this, searchLaundry);
		cleanTopSteps.addStep(hasSoap, useSoapOnTop);
		cleanTopSteps.setLockingCondition(cleanedTop);

		repairTrousersSteps = new ConditionalStep(this, talkToOronwen);
		repairTrousersSteps.setLockingCondition(repairedTrousers);

		ConditionalStep enterMournerHQ = new ConditionalStep(this, enterMournerBase);
		enterMournerHQ.addStep(inMournerBasement, talkToEssyllt);
		enterMournerHQ.addStep(inMournerHQ, enterBasement);

		ConditionalStep prepareItems = new ConditionalStep(this, getItems);
		prepareItems.addStep(new Conditions(hasAllMournerItems, cleanedTop, repairedTrousers), enterMournerHQ);
		prepareItems.addStep(new Conditions(hasAllMournerItems, cleanedTop), repairTrousersSteps);
		prepareItems.addStep(new Conditions(hasAllMournerItems), cleanTopSteps);

		steps.put(3, prepareItems);

		ConditionalStep getAssignment = new ConditionalStep(this, enterMournerBaseNoPass);
		getAssignment.addStep(inMournerBasement, talkToEssyllt);
		getAssignment.addStep(inMournerHQ, enterBasement);

		steps.put(4, getAssignment);

		ConditionalStep tortureGnome = new ConditionalStep(this, enterMournerBaseForGnome);
		tortureGnome.addStep(new Conditions(greenDyed, redDyed, yellowDyed, blueDyed, inMournerBasement), talkToEssylltAfterSheep);
		tortureGnome.addStep(new Conditions(greenDyed, redDyed, yellowDyed, blueDyed, inMournerHQ), enterBasementAfterSheep);
		tortureGnome.addStep(new Conditions(greenDyed, redDyed, yellowDyed, blueDyed), enterBaseAfterSheep);
		tortureGnome.addStep(new Conditions(hasAllToads, greenDyed, redDyed, yellowDyed, blueToadLoaded), shootBlueToad);
		tortureGnome.addStep(new Conditions(hasAllToads, greenDyed, redDyed, yellowDyed), loadBlueToad);
		tortureGnome.addStep(new Conditions(hasAllToads, greenDyed, redDyed, yellowToadLoaded), shootYellowToad);
		tortureGnome.addStep(new Conditions(hasAllToads, greenDyed, redDyed), loadYellowToad);
		tortureGnome.addStep(new Conditions(hasAllToads, greenDyed, redToadLoaded), shootRedToad);
		tortureGnome.addStep(new Conditions(hasAllToads, greenDyed), loadRedToad);
		tortureGnome.addStep(new Conditions(hasAllToads, greenToadLoaded), shootGreenToad);
		tortureGnome.addStep(new Conditions(hasAllToads), loadGreenToad);
		tortureGnome.addStep(new Conditions(learntAboutToads), getToads);
		tortureGnome.addStep(new Conditions(inMournerBasement, repairedDevice), askAboutToads);
		tortureGnome.addStep(new Conditions(inMournerBasement, releasedGnome), giveGnomeItems);
		tortureGnome.addStep(new Conditions(inMournerBasement, talkedWithItem), releaseGnome);
		tortureGnome.addStep(new Conditions(inMournerBasement, torturedGnome), talkToGnomeWithItems);
		tortureGnome.addStep(new Conditions(inMournerHQ, torturedGnome), enterBasementAfterTorture);
		tortureGnome.addStep(new Conditions(torturedGnome), enterMournerBaseAfterTorture);
		tortureGnome.addStep(new Conditions(inMournerBasement, knowWeaknesses), useFeatherOnGnome);
		tortureGnome.addStep(inMournerBasement, talkToGnome);
		tortureGnome.addStep(inMournerHQ, enterBasementForGnome);

		steps.put(5, tortureGnome);

		ConditionalStep takeAppleToElena = new ConditionalStep(this, pickUpRottenApple);
		takeAppleToElena.addStep(new Conditions(twoPoisoned, inMournerBasement), talkToEssylltAfterPoison);
		takeAppleToElena.addStep(new Conditions(twoPoisoned, inMournerHQ), enterMournerBasementAfterPoison);
		takeAppleToElena.addStep(twoPoisoned, enterMournerBaseAfterPoison);
		takeAppleToElena.addStep(new Conditions(receivedSieve, poisoned1), usePowderOnFood2);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hadToxicPowder), usePowderOnFood1);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hasToxicNaphtha), cookNaphtha);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hasNaphthaAppleMix), useSieveOnBarrel);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hasAppleBarrel, hasNaphtha), useNaphthaOnBarrel);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hasAppleBarrel), getNaphtha);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hasRottenApples), useApplesOnPress);
		takeAppleToElena.addStep(new Conditions(receivedSieve, hasBarrel), useBarrelOnPile);
		takeAppleToElena.addStep(receivedSieve, pickUpBarrel);
		takeAppleToElena.addStep(givenRottenApple, talkToElenaNoApple);
		takeAppleToElena.addStep(hasRottenApple, talkToElena);

		steps.put(6, takeAppleToElena);

		ConditionalStep learnTheSecret = new ConditionalStep(this, enterMournerBaseAfterPoison);
		learnTheSecret.addStep(inMournerBasement, talkToEssylltAfterPoison);
		learnTheSecret.addStep(inMournerHQ, enterMournerBasementAfterPoison);

		steps.put(7, learnTheSecret);

		steps.put(8, returnToArianwyn);

		return steps;
	}

	public void setupItemRequirements()
	{
		bearFur = new ItemRequirement("Bear fur", ItemID.BEAR_FUR);
		silk2 = new ItemRequirement("Silk", ItemID.SILK, 2);
		redDye = new ItemRequirement("Red dye", ItemID.RED_DYE);
		yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE);
		greenDye = new ItemRequirement("Green dye", ItemID.GREEN_DYE);
		blueDye = new ItemRequirement("Blue dye", ItemID.BLUE_DYE);
		waterBucket = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		rottenApple = new ItemRequirement("Rotten apple", ItemID.ROTTEN_APPLE);
		toadCrunchies = new ItemRequirement("Toad crunchies (can be premade)", ItemID.TOAD_CRUNCHIES);
		toadCrunchies.addAlternates(ItemID.TOAD_CRUNCHIES_9538, ItemID.PREMADE_TD_CRUNCH);
		magicLogs = new ItemRequirement("Magic logs", ItemID.MAGIC_LOGS);
		leather = new ItemRequirement("Leather", ItemID.LEATHER);
		ogreBellows = new ItemRequirement("Ogre bellows", ItemID.OGRE_BELLOWS);
		ogreBellows.addAlternates(ItemID.OGRE_BELLOWS_1, ItemID.OGRE_BELLOWS_2, ItemID.OGRE_BELLOWS_3);
		coal20 = new ItemRequirement("10-20 coal", ItemID.COAL, 10);
		coal20OrNaphtha = new ItemRequirement("10-20 coal, or a barrel of naphtha", ItemID.BARREL_OF_NAPHTHA, -1);
		coal20OrNaphtha.addAlternates(ItemID.COAL);
		feather = new ItemRequirement("Feather", ItemID.FEATHER);
		greenBellow = new ItemRequirement("Green dye bellows", ItemID.GREEN_DYE_BELLOWS);
		yellowBellow = new ItemRequirement("Yellow dye bellows", ItemID.YELLOW_DYE_BELLOWS);
		blueBellow = new ItemRequirement("Blue dye bellows", ItemID.BLUE_DYE_BELLOWS);
		redBellow = new ItemRequirement("Red dye bellows", ItemID.RED_DYE_BELLOWS);
		mournerMask = new ItemRequirement("Gas mask", ItemID.GAS_MASK);
		bloodyMournerBody = new ItemRequirement("Bloody mourner top", ItemID.BLOODY_MOURNER_TOP);
		mournerLegsBroken = new ItemRequirement("Ripped mourner trousers", ItemID.RIPPED_MOURNER_TROUSERS);
		mournerBoots = new ItemRequirement("Mourner boots", ItemID.MOURNER_BOOTS);
		mournerGloves = new ItemRequirement("Mourner gloves", ItemID.MOURNER_GLOVES);
		mournerCloak = new ItemRequirement("Mourner cloak", ItemID.MOURNER_CLOAK);
		mournerLetter = new ItemRequirement("Mourner letter", ItemID.MOURNER_LETTER);
		tegidsSoap = new ItemRequirement("Tegid's soap", ItemID.TEGIDS_SOAP);
		tegidsSoap.setHighlightInInventory(true);
		mournerBody = new ItemRequirement("Mourner top", ItemID.MOURNER_TOP);
		mournerLegs = new ItemRequirement("Mourner trousers", ItemID.MOURNER_TROUSERS);
		sieve = new ItemRequirement("Sieve", ItemID.SIEVE);
		sieve.setHighlightInInventory(true);
		sieve.setTip("You can get another from Elena");
		tarnishedKey = new ItemRequirement("Tarnished key", ItemID.TARNISHED_KEY);
		fullMourners = new ItemRequirement("Full mourners outfit", -1, -1);

		equippedMournerBoots = new ItemRequirement("Mourner boots", ItemID.MOURNER_BOOTS, 1, true);
		equippedMournerGloves = new ItemRequirement("Mourner gloves", ItemID.MOURNER_GLOVES, 1, true);
		equippedMournerCloak = new ItemRequirement("Mourner cloak", ItemID.MOURNER_CLOAK, 1, true);
		equippedMournerBody = new ItemRequirement("Mourner top", ItemID.MOURNER_TOP, 1, true);
		equippedMournerLegs = new ItemRequirement("Mourner trousers", ItemID.MOURNER_TROUSERS, 1, true);
		equippedMournerMask = new ItemRequirement("Gas mask", ItemID.GAS_MASK, 1, true);

		brokenDevice = new ItemRequirement("Broken device", ItemID.BROKEN_DEVICE);
		fixedDevice = new ItemRequirement("Fixed device", ItemID.FIXED_DEVICE);
		fixedDeviceEquipped = new ItemRequirement("Fixed device", ItemID.FIXED_DEVICE, 1, true);
		featherHighlight = new ItemRequirement("Feather", ItemID.FEATHER);
		featherHighlight.setHighlightInInventory(true);

		redToad = new ItemRequirement("Red toad", ItemID.RED_TOAD);
		yellowToad = new ItemRequirement("Yellow toad", ItemID.YELLOW_TOAD);
		greenToad = new ItemRequirement("Green toad", ItemID.GREEN_TOAD);
		blueToad = new ItemRequirement("Blue toad", ItemID.BLUE_TOAD);

		emptyBarrel = new ItemRequirement("Barrel", ItemID.BARREL_3216);
		emptyBarrel.setHighlightInInventory(true);
		barrelOfRottenApples = new ItemRequirement("Rotten apples", ItemID.ROTTEN_APPLES);
		barrelOfRottenApples.setHighlightInInventory(true);

		appleBarrel = new ItemRequirement("Apple barrel", ItemID.APPLE_BARREL);
		appleBarrel.setHighlightInInventory(true);

		naphtha = new ItemRequirement("Barrel of naphtha", ItemID.BARREL_OF_NAPHTHA);
		naphtha.setHighlightInInventory(true);

		naphthaAppleMix = new ItemRequirement("Naphtha apple mix", ItemID.NAPHTHA_APPLE_MIX);
		naphthaAppleMix.setHighlightInInventory(true);

		toxicNaphtha = new ItemRequirement("Toxic naphtha", ItemID.TOXIC_NAPHTHA);
		toxicNaphtha.setHighlightInInventory(true);

		toxicPowder = new ItemRequirement("Toxic powder", ItemID.TOXIC_POWDER);
		toxicPowder.setTip("You'll have to make more if you've lost it");
		toxicPowder.setHighlightInInventory(true);
	}

	public void setupConditions()
	{
		mournerItemsNearby = new Conditions(LogicType.OR, new ItemCondition(bloodyMournerBody), new ItemCondition(mournerBoots), new ItemCondition(mournerGloves), new ItemCondition(mournerCloak),
			new ItemCondition(mournerLegsBroken), new ItemCondition(mournerMask), new ItemCondition(mournerLetter));
		hasAllMournerItems = new Conditions(LogicType.AND, new ItemRequirementCondition(mournerMask, mournerLetter, mournerMask, mournerGloves, mournerCloak, mournerBoots),
			new ItemRequirementCondition(LogicType.OR, mournerBody, bloodyMournerBody), new ItemRequirementCondition(LogicType.OR, mournerLegsBroken, mournerLegs));

		hasSoap = new ItemRequirementCondition(tegidsSoap);

		cleanedTop = new Conditions(new ItemRequirementCondition(mournerBody));
		repairedTrousers = new Conditions(new ItemRequirementCondition(mournerLegs));
		inMournerHQ = new ZoneCondition(mournerHQ, mournerHQ2);

		inMournerBasement = new ZoneCondition(mournerBasement);
		knowWeaknesses = new VarbitCondition(799, 3, Operation.GREATER_EQUAL);
		torturedGnome = new VarbitCondition(799, 5, Operation.GREATER_EQUAL);
		talkedWithItem = new VarbitCondition(799, 6, Operation.GREATER_EQUAL);
		releasedGnome = new VarbitCondition(799, 7, Operation.GREATER_EQUAL);
		repairedDevice = new VarbitCondition(799, 9, Operation.GREATER_EQUAL);

		learntAboutToads = new VarbitCondition(9155, 1);
		redToadLoaded = new VarbitCondition(804, 1);
		greenToadLoaded = new VarbitCondition(804, 2);
		blueToadLoaded = new VarbitCondition(804, 3);
		yellowToadLoaded = new VarbitCondition(804, 4);

		greenDyed = new VarbitCondition(803, 1);
		redDyed = new VarbitCondition(801, 1);
		yellowDyed = new VarbitCondition(802, 1);
		blueDyed = new VarbitCondition(800, 1);

		greenToadGot = new Conditions(LogicType.OR, greenToadLoaded, new ItemRequirementCondition(greenToad), greenDyed);
		redToadGot = new Conditions(LogicType.OR, redToadLoaded, new ItemRequirementCondition(redToad), redDyed);
		yellowToadGot = new Conditions(LogicType.OR, yellowToadLoaded, new ItemRequirementCondition(yellowToad), yellowDyed);
		blueToadGot = new Conditions(LogicType.OR, blueToadLoaded, new ItemRequirementCondition(blueToad), blueDyed);

		hasAllToads = new Conditions(true, LogicType.AND, greenToadGot, yellowToadGot, redToadGot, blueToadGot);

		hasRottenApple = new ItemRequirementCondition(rottenApple);

		givenRottenApple = new VarbitCondition(805, 2, Operation.GREATER_EQUAL);
		receivedSieve = new VarbitCondition(805, 4, Operation.GREATER_EQUAL);

		hasRottenApples = new ItemRequirementCondition(barrelOfRottenApples);
		hasBarrel = new ItemRequirementCondition(emptyBarrel);
		hasAppleBarrel = new Conditions(true, LogicType.OR, new ItemRequirementCondition(appleBarrel));
		hasNaphtha = new ItemRequirementCondition(naphtha);
		hasNaphthaAppleMix = new ItemRequirementCondition(naphthaAppleMix);
		hasToxicNaphtha = new ItemRequirementCondition(toxicNaphtha);

		hadToxicPowder = new Conditions(true, LogicType.OR, new ItemRequirementCondition(toxicPowder));
		poisoned1 = new VarbitCondition(806, 1);
		poisoned2 = new VarbitCondition(807, 1);
		poisoned3 = new VarbitCondition(808, 1);

		twoPoisoned = new Conditions(LogicType.OR, new Conditions(poisoned1, poisoned2), new Conditions(poisoned1, poisoned3), new Conditions(poisoned2, poisoned3));
	}

	public void loadZones()
	{
		mournerHQ = new Zone(new WorldPoint(2547, 3321,0), new WorldPoint(2555, 3327, 0));
		mournerHQ2 = new Zone(new WorldPoint(2542, 3324,0), new WorldPoint(2546, 3327, 0));
		mournerBasement = new Zone(new WorldPoint(2034, 4628, 0), new WorldPoint(2045, 4651, 0));
	}

	public void setupSteps()
	{
		talkToIslwyn = new NpcStep(this, NpcID.ISLWYN_8675, new WorldPoint(2207, 3159, 0), "Talk to Islwyn in Isfadar. If he's not at the marked location, try hopping worlds to find him here.");
		talkToIslwyn.addDialogStep("I'm ready now.");
		talkToIslwyn.addDialogStep("I'm ready.");
		talkToIslwyn.addDialogStep("Yes.");

		talkToArianwyn = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Talk to Arianwyn in Lletya.");
		talkToArianwyn.addDialogStep("Okay, let's begin.");
		killMourner = new NpcStep(this, NpcID.MOURNER_9013, new WorldPoint(2385, 3326, 0), "Kill a mourner travelling through the Arandar pass. This is more easily accessed from the north entrance.", true);
		pickUpLoot = new DetailedQuestStep(this, "Pick up everything the mourner dropped.", mournerBoots, mournerCloak, mournerGloves, mournerLegsBroken, mournerMask, mournerLetter, bloodyMournerBody);

		searchLaundry = new ObjectStep(this, ObjectID.LAUNDRY_BASKET, new WorldPoint(2912, 3418, 0), "Search Tegid's laundry basket in south Taverley for some soap.");
		searchLaundry.addDialogStep("Steal the soap.");
		useSoapOnTop = new DetailedQuestStep(this, "Use the soap on the bloody mourner top", tegidsSoap, waterBucket, bloodyMournerBody);

		talkToOronwen = new NpcStep(this, NpcID.ORONWEN, new WorldPoint(2327, 3176, 0),
			"Teleport to Lletya using a crystal teleport seed and talk to Oronwen to have them repair your trousers. Buy dyes here if you still need them.", mournerLegsBroken, bearFur, silk2);
		talkToOronwen.addDialogStep("Do you mend clothes?");
		talkToOronwen.addDialogStep("I have all I need to mend my trousers.");

		enterMournerBase = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Equip the full mourners outfit and enter the Mourners' Headquaters in West Ardougne.", toadCrunchies, feather, magicLogs, leather, equippedMournerMask, equippedMournerBody, equippedMournerLegs, equippedMournerBoots,
			equippedMournerGloves, equippedMournerCloak, mournerLetter);

		enterMournerBaseNoPass = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Equip the full mourners outfit and enter the Mourners' Headquaters in West Ardougne.", toadCrunchies, feather, magicLogs, leather, equippedMournerMask, equippedMournerBody, equippedMournerLegs, equippedMournerBoots,
			equippedMournerGloves, equippedMournerCloak);

		enterBasement = new ObjectStep(this, ObjectID.TRAPDOOR_8783, new WorldPoint(2542, 3327, 0), "Go down the trapdoor in the north west corner of the HQ.");

		talkToEssyllt = new NpcStep(this, NpcID.ESSYLLT_9016, new WorldPoint(2043, 4631, 0), "Talk to Essyllt in the south room.");

		talkToGnome = new ObjectStep(this, NullObjectID.NULL_8794, new WorldPoint(2035, 4630, 0), "Talk to the gnome on a rack.", tarnishedKey, feather, toadCrunchies);
		talkToGnome.addDialogStep("You talked about toad crunchies and being tickled.");

		enterMournerBaseForGnome = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Equip the full mourners outfit and enter the Mourners' Headquaters in West Ardougne.", toadCrunchies, feather, magicLogs, leather, equippedMournerMask, equippedMournerBody,
			equippedMournerLegs, equippedMournerBoots, equippedMournerGloves, equippedMournerCloak);
		enterBasementForGnome = new ObjectStep(this, ObjectID.TRAPDOOR_8783, new WorldPoint(2542, 3327, 0), "Go down the trapdoor in the north west corner of the HQ.", feather, toadCrunchies, magicLogs, leather);
		talkToGnome.addSubSteps(enterMournerBaseForGnome, enterBasementForGnome);

		useFeatherOnGnome = new ObjectStep(this, NullObjectID.NULL_8794, new WorldPoint(2035, 4630, 0), "Use a feather on the gnome with toad crunchies in your inventory.", tarnishedKey, featherHighlight, toadCrunchies);
		useFeatherOnGnome.addIcon(ItemID.FEATHER);

		enterMournerBaseAfterTorture = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Equip the full mourners outfit and enter the Mourners' Headquaters in West Ardougne.", toadCrunchies, feather, magicLogs, leather, equippedMournerMask, equippedMournerBody, equippedMournerLegs, equippedMournerBoots,
			equippedMournerGloves, equippedMournerCloak);
		enterBasementAfterTorture = new ObjectStep(this, ObjectID.TRAPDOOR_8783, new WorldPoint(2542, 3327, 0), "Go down the trapdoor in the north west corner of the HQ.", toadCrunchies, magicLogs, leather);

		talkToGnomeWithItems = new ObjectStep(this, NullObjectID.NULL_8794, new WorldPoint(2035, 4630, 0), "Talk to the gnome again with the required items.", toadCrunchies, magicLogs, leather);

		releaseGnome = new ObjectStep(this, NullObjectID.NULL_8794, new WorldPoint(2035, 4630, 0), "Right-click release the gnome with the items.", tarnishedKey, magicLogs, leather, toadCrunchies, brokenDevice);

		giveGnomeItems = new NpcStep(this, NpcID.GNOME_5309, new WorldPoint(2035, 4630, 0), "Give the gnome a magic log, some soft leather and some toad crunchies.", tarnishedKey, magicLogs, leather, toadCrunchies, brokenDevice);

		askAboutToads = new NpcStep(this, NpcID.GNOME_5309, new WorldPoint(2035, 4630, 0), "Ask the gnome about ammo.");

		getToads = new DetailedQuestStep(this, "You need to make some dyed toads. Go to Feldip Hills, use a dye on your empty bellows, then use the bellows to inflate a toad. Get at least one toad of each colour.", redToad, yellowToad, greenToad, blueToad);

		loadGreenToad = new DetailedQuestStep(this, "Add a green toad to the fixed device.", greenToad, fixedDevice);
		shootGreenToad = new NpcStep(this, NpcID.GREEN_SHEEP, new WorldPoint(2621, 3368, 0), "Wield the fixed device and select Aim and Fire from your combat options to fire at a green sheep north of Ardougne.", true, fixedDeviceEquipped);

		loadRedToad = new DetailedQuestStep(this, "Add a red toad to the fixed device.", redToad, fixedDevice);
		shootRedToad = new NpcStep(this, NpcID.RED_SHEEP, new WorldPoint(2611, 3344, 0), "Wield the fixed device and select Aim and Fire from your combat options to fire at a shoot the red toad at a red sheep north of Ardougne.", true, fixedDeviceEquipped);

		loadYellowToad = new DetailedQuestStep(this, "Add a yellow toad to the fixed device.", yellowToad, fixedDevice);
		shootYellowToad = new NpcStep(this, NpcID.YELLOW_SHEEP, new WorldPoint(2610, 3391, 0), "Wield the fixed device and select Aim and Fire from your combat options to fire at a yellow sheep north of Ardougne.", true, fixedDeviceEquipped);

		loadBlueToad = new DetailedQuestStep(this, "Add a blue toad to the fixed device.", blueToad, fixedDevice);
		shootBlueToad = new NpcStep(this, NpcID.BLUE_SHEEP, new WorldPoint(2562, 3390, 0), "Wield the fixed device and select Aim and Fire from your combat options to fire at a blue sheep north of Ardougne.", true, fixedDeviceEquipped);

		dyeSheep = new DetailedQuestStep(this, "Dye each colour of sheep north of Ardougne using the dyed toads and the fixed device.");
		dyeSheep.addSubSteps(loadGreenToad, loadYellowToad, loadBlueToad, loadRedToad, shootBlueToad, shootGreenToad, shootRedToad, shootYellowToad);

		enterBaseAfterSheep = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Equip the full mourners outfit and enter the Mourners' Headquaters in West Ardougne.", equippedMournerMask, equippedMournerBody, equippedMournerLegs, equippedMournerBoots, equippedMournerGloves, equippedMournerCloak);

		enterBasementAfterSheep = new ObjectStep(this, ObjectID.TRAPDOOR_8783, new WorldPoint(2542, 3327, 0), "Go down the trapdoor in the north west corner of the HQ.");

		talkToEssylltAfterSheep = new NpcStep(this, NpcID.ESSYLLT_9016, new WorldPoint(2043, 4631, 0), "Talk to Essyllt in the south room.");

		pickUpRottenApple = new DetailedQuestStep(this, new WorldPoint(2535, 3333, 0), "Pick up a rotten apple from west of the Mourner HQ.", rottenApple);

		talkToElena = new NpcStep(this, NpcID.ELENA, new WorldPoint(2592, 3335, 0), "Talk to Elena in north-west of East Ardougne.", rottenApple);
		talkToElenaNoApple = new NpcStep(this, NpcID.ELENA, new WorldPoint(2592, 3335, 0), "Talk to Elena in north-west of East Ardougne.");
		talkToElena.addSubSteps(talkToElenaNoApple);

		pickUpBarrel = new DetailedQuestStep(this, new WorldPoint(2487, 3371, 0), "Pick up a barrel from the Orchard north of Ardougne.", emptyBarrel);
		useBarrelOnPile = new ObjectStep(this, ObjectID.ROTTEN_APPLE_PILE, new WorldPoint(2487, 3374, 0), "Use the barrel on a rotten apple pile.", emptyBarrel);
		useBarrelOnPile.addIcon(ItemID.BARREL_3216);

		useApplesOnPress = new ObjectStep(this, ObjectID.APPLE_PRESS, new WorldPoint(2484, 3374, 0), "Use the rotten apples on the apple press.", barrelOfRottenApples);
		useApplesOnPress.addIcon(ItemID.ROTTEN_APPLES);

		getNaphtha = new DetailedQuestStep(this, "Make some Naphtha. Grab another barrel, fill it on the swamp south of the elven lands, then refine it at the Chemist in Rimmington with 10-20 coal.", coal20OrNaphtha);

		useNaphthaOnBarrel = new DetailedQuestStep(this, "Use a barrel of naptha on the apple barrel.", naphtha, appleBarrel);

		useSieveOnBarrel = new DetailedQuestStep(this, "Use the sieve on the naphtha apple mix", sieve, naphthaAppleMix);

		cookNaphtha = new DetailedQuestStep(this, "Cook the toxic naphtha on a range. DO NOT USE IT ON A FIRE.", toxicNaphtha);

		usePowderOnFood1 = new ObjectStep(this, NullObjectID.NULL_37330, new WorldPoint(2517, 3315, 0), "Use the toxic powder on the food store in the room north west of West Ardougne's town centre.", toxicPowder);
		usePowderOnFood1.addIcon(ItemID.TOXIC_POWDER);
		usePowderOnFood2 = new ObjectStep(this, NullObjectID.NULL_37331, new WorldPoint(2525, 3288, 0), "Use the toxic powder on the food store in the church south of West Ardougne's town centre.", toxicPowder);
		usePowderOnFood2.addIcon(ItemID.TOXIC_POWDER);

		enterMournerBaseAfterPoison = new ObjectStep(this, ObjectID.DOOR_2036, new WorldPoint(2551, 3320, 0),
			"Return to Essyllt in the Mourner HQ basement.", equippedMournerMask, equippedMournerBody, equippedMournerLegs, equippedMournerBoots, equippedMournerGloves, equippedMournerCloak);

		enterMournerBasementAfterPoison = new ObjectStep(this, ObjectID.TRAPDOOR_8783, new WorldPoint(2542, 3327, 0), "Return to Essyllt in the Mourner HQ basement.");

		talkToEssylltAfterPoison = new NpcStep(this, NpcID.ESSYLLT_9016, new WorldPoint(2043, 4631, 0), "Return to Essyllt in the Mourner HQ basement.");
		talkToEssylltAfterPoison.addSubSteps(enterMournerBasementAfterPoison, enterMournerBaseAfterPoison);

		returnToArianwyn = new NpcStep(this, NpcID.ARIANWYN_9014, new WorldPoint(2354, 3170, 0), "Return to Arianwyn in Lletya.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(bearFur, silk2, redDye, yellowDye, blueDye, greenDye, waterBucket, feather, rottenApple, toadCrunchies, magicLogs, leather, ogreBellows, coal20OrNaphtha));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Mourning (level 11)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToIslwyn, talkToArianwyn))));

		PanelDetails pickItemsPanel = new PanelDetails("Get Mourner's outfit",
			new ArrayList<>(Arrays.asList(killMourner, pickUpLoot)));
		pickItemsPanel.setLockingStep(getItems);
		allSteps.add(pickItemsPanel);

		PanelDetails cleanPanel = new PanelDetails("Clean Mourner top",
			new ArrayList<>(Arrays.asList(searchLaundry, useSoapOnTop)), waterBucket);
		cleanPanel.setLockingStep(cleanTopSteps);

		PanelDetails repairPanel = new PanelDetails("Repair Mourner trousers",
			new ArrayList<>(Collections.singletonList(talkToOronwen)), bearFur, silk2);
		repairPanel.setLockingStep(repairTrousersSteps);

		allSteps.add(cleanPanel);
		allSteps.add(repairPanel);

		PanelDetails enterWestArdougnePanel = new PanelDetails("Infiltrate the Mourners", new ArrayList<>(Arrays.asList(enterMournerBase, enterBasement, talkToEssyllt, talkToGnome, useFeatherOnGnome, talkToGnomeWithItems, releaseGnome, giveGnomeItems)),
			fullMourners, mournerLetter, feather, toadCrunchies, magicLogs, leather);

		allSteps.add(enterWestArdougnePanel);

		allSteps.add(new PanelDetails("Dye the sheep", new ArrayList<>(Arrays.asList(getToads, dyeSheep, enterBaseAfterSheep, enterBasementAfterSheep, talkToEssylltAfterSheep)), fixedDevice, ogreBellows, redDye, yellowDye, greenDye, blueDye));
 
		 
		allSteps.add(new PanelDetails("Poison the citizens",
			new ArrayList<>(Arrays.asList(pickUpRottenApple, talkToElena, pickUpBarrel, useBarrelOnPile, useApplesOnPress, getNaphtha, useNaphthaOnBarrel, useSieveOnBarrel, cookNaphtha, usePowderOnFood1, usePowderOnFood2, talkToEssylltAfterPoison)), coal20OrNaphtha));

		allSteps.add(new PanelDetails("Report back to Arianwyn",
			new ArrayList<>(Collections.singletonList(returnToArianwyn))));


		return allSteps;
	}
}
