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
package com.questhelper.quests.zogreflesheaters;

import com.questhelper.BankSlotIcons;
import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
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
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.ZOGRE_FLESH_EATERS
)
public class ZogreFleshEaters extends BasicQuestHelper
{
	ItemRequirement knife, backpack, tankard, tornPage, blackPrism, necroBook, hamBook, portrait, goodPort, strangePotionHighlighted,
		badPort, charcoal, papyrus, signedPort, cupOfTea, strangePotion, grishKey, ogreRelic, combatGear, knifeHighlighted, tankardHighlighted;

	ConditionForStep askedAboutSickies, inSurface, inTombF2, killedZombie, hasBackpack, hasTankard, hasTornPage, hasBlackPrism, searchedCoffin, usedKnife, openedCoffin,
		talkedToZavistic, hasBackpackOrTankard, atSith, askedSithToLookAround, hasOrShownNecroBook, hasOrShownHamBook, hasPortait, hasNecroBook, hasHamBook,
		hasGoodPortrait, hasBadPortrait, hasPapyrus, shownNecroBook, shownHamBook, shownTankard, usedTankardOnBartender, usedPortraitOnBartender, hasOrShownSignedPortrait,
		shownSignedPortrait, sithTransformed, askedAboutDisease, askedAboutGettingRidOfUndead, askedAboutBow, inTombF0, inTombF2ToBoss, ogreRelicNearby;

	QuestStep talkToGrish, talkToGuard, climbBarricade, goDownStairs, searchSkeleton, killZombie, searchLectern, searchCoffin, useKnifeOnCoffin, openCoffin,
		openBackpack, searchCoffinProperly, useTankardOnBartender, talkToZavistic, goUpToSith, talkToSith, searchDrawers, searchCupboard, searchWardrobe, dropPortraitAndSearchDrawers,
		usePapyrusOnSith, usePortraitOnBartender, bringSignedPortraitToZavistic, goUpToSithAgain, usePotionOnTea, goDownstairsFromSith, goUpToOgreSith, talkToSithForAnswers,
		askAboutRemovingUndead, askAboutRemovingDisease, talkToGrishForKey, talkToGrishForBow, climbBarricadeForBoss, goDownStairsForBoss, enterDoors,
		goDownToBoss, searchStand, pickUpOgreArtefact, returnArtefactToGrish;

	Zone surface, tombF2, sith, tombF0, tombF2ToBoss;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGrish);
		steps.put(2, talkToGuard);

		ConditionalStep explore = new ConditionalStep(this, climbBarricade);
		explore.addStep(new Conditions(hasOrShownHamBook, hasOrShownNecroBook, hasOrShownSignedPortrait), bringSignedPortraitToZavistic);
		explore.addStep(new Conditions(hasOrShownHamBook, hasOrShownNecroBook, hasGoodPortrait, usedTankardOnBartender), usePortraitOnBartender);
		explore.addStep(new Conditions(hasTankard, hasOrShownHamBook, hasOrShownNecroBook, hasGoodPortrait), useTankardOnBartender);
		explore.addStep(new Conditions(hasTankard, atSith, hasOrShownHamBook, hasOrShownNecroBook, hasPapyrus), usePapyrusOnSith);
		explore.addStep(new Conditions(hasTankard, atSith, hasOrShownHamBook, hasOrShownNecroBook, hasBadPortrait), dropPortraitAndSearchDrawers);
		explore.addStep(new Conditions(hasTankard, atSith, hasOrShownHamBook, hasOrShownNecroBook), searchDrawers);
		explore.addStep(new Conditions(hasTankard, atSith, hasOrShownHamBook), searchCupboard);
		explore.addStep(new Conditions(hasTankard, atSith, askedSithToLookAround), searchWardrobe);
		explore.addStep(new Conditions(hasTankard, talkedToZavistic, atSith), talkToSith);
		explore.addStep(new Conditions(hasTankard, talkedToZavistic), goUpToSith);

		explore.addStep(new Conditions(hasTankard, hasTornPage, hasBlackPrism), talkToZavistic);
		explore.addStep(new Conditions(inTombF2, hasTankard, hasTornPage, openedCoffin), searchCoffinProperly);
		explore.addStep(new Conditions(inTombF2, hasTankard, hasTornPage, usedKnife), openCoffin);
		explore.addStep(new Conditions(inTombF2, hasTankard, hasTornPage, searchedCoffin), useKnifeOnCoffin);
		explore.addStep(new Conditions(inTombF2, hasTankard, hasTornPage), searchCoffin);
		explore.addStep(new Conditions(inTombF2, hasTankard), searchLectern);
		explore.addStep(new Conditions(inTombF2, hasBackpack), openBackpack);
		explore.addStep(inTombF2, searchSkeleton);
		explore.addStep(inSurface, goDownStairs);
		steps.put(3, explore);

		ConditionalStep poisonSith = new ConditionalStep(this, goUpToSithAgain);
		poisonSith.addStep(atSith, usePotionOnTea);

		steps.put(4, poisonSith);
		steps.put(5, poisonSith);

		ConditionalStep askSithQuestions = new ConditionalStep(this, goDownstairsFromSith);
		askSithQuestions.addStep(new Conditions(sithTransformed, atSith), talkToSithForAnswers);
		askSithQuestions.addStep(sithTransformed, goUpToOgreSith);
		steps.put(6, askSithQuestions);

		ConditionalStep askAboutDiseaseAndOgres = new ConditionalStep(this, goUpToOgreSith);
		askAboutDiseaseAndOgres.addStep(new Conditions(askedAboutGettingRidOfUndead, askedAboutDisease), talkToGrishForKey);
		askAboutDiseaseAndOgres.addStep(new Conditions(askedAboutGettingRidOfUndead, atSith), askAboutRemovingDisease);
		askAboutDiseaseAndOgres.addStep(atSith, askAboutRemovingUndead);
		steps.put(8, askAboutDiseaseAndOgres);

		ConditionalStep goKillBash = new ConditionalStep(this, talkToGrishForBow);
		goKillBash.addStep(inTombF0, searchStand);
		goKillBash.addStep(new Conditions(inTombF2ToBoss, askedAboutBow), goDownToBoss);
		goKillBash.addStep(new Conditions(inTombF2, askedAboutBow), enterDoors);
		goKillBash.addStep(new Conditions(inSurface, askedAboutBow), goDownStairsForBoss);
		goKillBash.addStep(askedAboutBow, climbBarricadeForBoss);

		steps.put(10, goKillBash);

		ConditionalStep returnRelic = new ConditionalStep(this, returnArtefactToGrish);
		returnRelic.addStep(ogreRelicNearby, pickUpOgreArtefact);
		steps.put(12, returnRelic);

		return steps;
	}

	public void setupItemRequirements()
	{
		backpack = new ItemRequirement("Ruined backpack", ItemID.RUINED_BACKPACK);
		backpack.setHighlightInInventory(true);
		tankard = new ItemRequirement("Dragon inn tankard", ItemID.DRAGON_INN_TANKARD);
		tankardHighlighted = new ItemRequirement("Dragon inn tankard", ItemID.DRAGON_INN_TANKARD);
		tankardHighlighted.setHighlightInInventory(true);
		tornPage = new ItemRequirement("Torn page", ItemID.TORN_PAGE);
		blackPrism = new ItemRequirement("Black prism", ItemID.BLACK_PRISM);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		knifeHighlighted = new ItemRequirement("Knife", ItemID.KNIFE);
		knifeHighlighted.setHighlightInInventory(true);
		necroBook = new ItemRequirement("Necromancy book", ItemID.NECROMANCY_BOOK);
		hamBook = new ItemRequirement("Book of 'h.a.m'", ItemID.BOOK_OF_HAM);
		portrait = new ItemRequirement("Book of portraiture", ItemID.BOOK_OF_PORTRAITURE);
		goodPort = new ItemRequirement("Sithik portrait", ItemID.SITHIK_PORTRAIT);
		goodPort.setHighlightInInventory(true);
		badPort = new ItemRequirement("Sithik portrait", ItemID.SITHIK_PORTRAIT_4815);
		signedPort = new ItemRequirement("Signed portrait", ItemID.SIGNED_PORTRAIT);
		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		papyrus.setHighlightInInventory(true);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		cupOfTea = new ItemRequirement("Cup of tea", ItemID.CUP_OF_TEA_4838);
		strangePotion = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION);
		strangePotion.setTip("You can get another from Zavistic Rarve");

		strangePotionHighlighted = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION);
		strangePotionHighlighted.setTip("You can get another from Zavistic Rarve");
		strangePotionHighlighted.setHighlightInInventory(true);

		grishKey = new ItemRequirement("Ogre gate key", ItemID.OGRE_GATE_KEY);
		ogreRelic = new ItemRequirement("Ogre artefact", ItemID.OGRE_ARTEFACT);
		ogreRelic.setTip("You can get another by searching the stand where you fought Slash Bash");

		ItemRequirement brutalArrows = new ItemRequirement("Brutal arrows", ItemID.RUNE_BRUTAL);
		brutalArrows.addAlternates(ItemID.ADAMANT_BRUTAL, ItemID.MITHRIL_BRUTAL, ItemID.BLACK_BRUTAL, ItemID.STEEL_BRUTAL,
			ItemID.IRON_BRUTAL, ItemID.BRONZE_BRUTAL);

		ItemRequirement ogreCompBow = new ItemRequirement("Comp ogre bow", ItemID.COMP_OGRE_BOW);

		ItemRequirements crumbleUndead = new ItemRequirements("Crumble undead runes",
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE),
			new ItemRequirement("Earth runes", ItemID.EARTH_RUNE),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE));

		combatGear = new ItemRequirements(LogicType.OR, "Either brutal arrows or Crumble Undead for fighting Slash Bash",
			ogreCompBow, crumbleUndead);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void loadZones()
	{
		surface = new Zone(new WorldPoint(2456, 3037, 0), new WorldPoint(2491, 3058, 0));
		tombF2 = new Zone(new WorldPoint(2434, 9400, 2), new WorldPoint(2494, 9474, 2));
		tombF0 = new Zone(new WorldPoint(2432, 9409, 0), new WorldPoint(2492, 9465, 0));
		sith = new Zone(new WorldPoint(2590, 3102, 1), new WorldPoint(2598, 3108, 1));
		tombF2ToBoss = new Zone(new WorldPoint(2433, 9408, 2), new WorldPoint(2449, 9432, 2));
	}

	public void setupConditions()
	{
		askedAboutSickies = new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, "Da sickies is when yous creature goes like orange");
		inSurface = new ZoneCondition(surface);
		inTombF2 = new ZoneCondition(tombF2);
		inTombF0 = new ZoneCondition(tombF0);
		inTombF2ToBoss = new ZoneCondition(tombF2ToBoss);
		killedZombie = new VarbitCondition(503, 2);
		hasBackpack = new ItemRequirementCondition(backpack);
		hasTankard = new ItemRequirementCondition(tankard);
		hasBackpackOrTankard = new Conditions(LogicType.OR, hasBackpack, hasTankard);
		hasBlackPrism = new ItemRequirementCondition(blackPrism);
		hasTornPage = new ItemRequirementCondition(tornPage);
		searchedCoffin = new VarbitCondition(488, 1);
		usedKnife = new VarbitCondition(488, 2);
		openedCoffin = new VarbitCondition(488, 3);
		talkedToZavistic = new VarbitCondition(488, 4, Operation.GREATER_EQUAL);
		askedSithToLookAround = new VarbitCondition(488, 5);

		atSith = new ZoneCondition(sith);

		hasPortait = new ItemRequirementCondition(portrait);
		hasNecroBook = new ItemRequirementCondition(necroBook);
		hasHamBook = new ItemRequirementCondition(hamBook);
		usedTankardOnBartender = new VarbitCondition(489, 1);
		usedPortraitOnBartender = new VarbitCondition(490, 1);
		shownNecroBook = new VarbitCondition(491, 1);
		shownHamBook = new VarbitCondition(492, 1);
		shownTankard = new VarbitCondition(493, 1);
		shownSignedPortrait = new VarbitCondition(494, 1);

		hasOrShownHamBook = new Conditions(LogicType.OR, shownHamBook, hasHamBook);
		hasOrShownNecroBook = new Conditions(LogicType.OR, shownNecroBook, hasNecroBook);
		hasOrShownSignedPortrait = new Conditions(LogicType.OR, shownSignedPortrait, new ItemRequirementCondition(signedPort));

		hasGoodPortrait = new ItemRequirementCondition(goodPort);
		hasBadPortrait = new ItemRequirementCondition(badPort);
		hasPapyrus = new ItemRequirementCondition(papyrus);

		sithTransformed = new VarbitCondition(495, 1);

		askedAboutDisease = new VarbitCondition(498, 1);
		askedAboutGettingRidOfUndead = new VarbitCondition(499, 1);
		askedAboutBow = new VarbitCondition(500, 1);

		ogreRelicNearby = new ItemCondition(ogreRelic);
		// 507 1 when are ya sure?
	}

	public void setupSteps()
	{
		talkToGrish = new NpcStep(this, NpcID.GRISH, new WorldPoint(2447, 3049, 0), "Talk to Grish south of Castle Wars.");
		talkToGrish.addDialogStepWithExclusion("What do you mean sickies?", "Can I help in any way?");
		talkToGrish.addDialogSteps("Can I help in any way?", "Ok, I'll check things out then and report back.", "Yes, I'm really sure!", "Yes, I want to help you out and find out about the zogres.");

		talkToGuard = new NpcStep(this, NpcID.OGRE_GUARD, new WorldPoint(2454, 3048, 0), "Talk to the guard east of Grish.");
		climbBarricade = new ObjectStep(this, NullObjectID.NULL_6878, new WorldPoint(2456, 3048, 0), "Climb over the barricade east of Grish.");

		goDownStairs = new ObjectStep(this, ObjectID.STAIRS_6841, new WorldPoint(2486, 3043, 0), "Climb down the stairs to the east.");
		searchSkeleton = new ObjectStep(this, ObjectID.SKELETON_6893, new WorldPoint(2442, 9459, 2), "Search the skeleton in the north west corner of this area.");
		killZombie = new NpcStep(this, NpcID.ZOMBIE_880, new WorldPoint(2442, 9459, 2), "Kill the zombie which appeared, and pick up the ruined backpack that appeared.");
		openBackpack = new DetailedQuestStep(this, "Open the backpack for a knife and a dragon inn tankard.", backpack);

		searchLectern = new ObjectStep(this, ObjectID.BROKEN_LECTURN, new WorldPoint(2443, 9459, 2), "Search the broken lecturn in the north west corner of the tombs.");
		searchCoffin = new ObjectStep(this, NullObjectID.NULL_6843, new WorldPoint(2439, 9459, 2), "Search the ogre coffin next to the skeleton.");
		useKnifeOnCoffin = new ObjectStep(this, NullObjectID.NULL_6843, new WorldPoint(2439, 9459, 2), "Use a knife on the ogre coffin next to the skeleton.", knifeHighlighted);
		useKnifeOnCoffin.addIcon(ItemID.KNIFE);
		openCoffin = new ObjectStep(this, NullObjectID.NULL_6843, new WorldPoint(2439, 9459, 2), "Search the coffin next to the skeleton again. It may take a few attempts to open.");
		searchCoffinProperly = new ObjectStep(this, NullObjectID.NULL_6843, new WorldPoint(2439, 9459, 2), "Search the coffin for a black prism.");

		talkToZavistic = new NpcStep(this, NpcID.ZAVISTIC_RARVE, new WorldPoint(2598, 3087, 0), "Talk to Zavistic Rarve at the Yanille Wizards' Guild. If you don't have 66 Magic, ring the bell outside the guild.");
		talkToZavistic.addDialogStep("I'm here about the sicks...err Zogres");

		goUpToSith = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2597, 3107, 0), "Go upstairs to Sithik upstairs in north Yanille.");
		talkToSith = new ObjectStep(this, NullObjectID.NULL_6887, new WorldPoint(2591, 3104, 1), "Talk to Sithik Ints in the bed to the west.");
		talkToSith.addDialogStep("Do you mind if I look around?");
		goUpToSith.addSubSteps(talkToSith);

		searchWardrobe = new ObjectStep(this, ObjectID.WARDROBE_6877, new WorldPoint(2590, 3103, 1), "Search Sithik's wardrobe.");
		searchCupboard = new ObjectStep(this, ObjectID.CUPBOARD_6876, new WorldPoint(2593, 3105, 1), "Search Sithik's cupboard.");

		goUpToOgreSith = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2597, 3107, 0), "Go talk to Sithik upstairs in north Yanille.");

		searchDrawers = new ObjectStep(this, ObjectID.DRAWERS_6875, new WorldPoint(2593, 3103, 1), "Search Sithik's drawers for more papyrus.");
		dropPortraitAndSearchDrawers = new ObjectStep(this, ObjectID.DRAWERS_6875, new WorldPoint(2593, 3103, 1), "The portrait drawn is wrong. Drop it and try drawing him again.");
		searchDrawers.addSubSteps(dropPortraitAndSearchDrawers);

		usePapyrusOnSith = new ObjectStep(this, NullObjectID.NULL_6887, new WorldPoint(2591, 3104, 1), "Use the papyrus on Sithik Ints.", papyrus, charcoal);
		usePapyrusOnSith.addIcon(ItemID.PAPYRUS);

		useTankardOnBartender = new NpcStep(this, NpcID.BARTENDER_1320, new WorldPoint(2555, 3080, 0), "Travel to the Dragon Inn in Yanille and use the dragon inn tankard on the bartender.", tankardHighlighted);
		useTankardOnBartender.addIcon(ItemID.DRAGON_INN_TANKARD);

		usePortraitOnBartender = new NpcStep(this, NpcID.BARTENDER_1320, new WorldPoint(2555, 3080, 0), "Use the portrait on the bartender.", goodPort);
		usePortraitOnBartender.addIcon(ItemID.SITHIK_PORTRAIT);

		bringSignedPortraitToZavistic = new NpcStep(this, NpcID.ZAVISTIC_RARVE, new WorldPoint(2598, 3087, 0),
			"Bring the books and signed portrait to Zavistic Rarve at the Yanille Wizards' Guild. If you don't have 66 Magic, ring the bell outside the guild.", signedPort, hamBook, necroBook);
		bringSignedPortraitToZavistic.addDialogStep("I'm here about the sicks...err Zogres");
		bringSignedPortraitToZavistic.addDialogStep("I have some items that I'd like you to look at.");

		usePotionOnTea = new DetailedQuestStep(this, new WorldPoint(2593, 3103, 1), "Use the strange potion on the cup of tea next to Sithik.", strangePotionHighlighted);
		usePotionOnTea.addIcon(ItemID.STRANGE_POTION);
		goUpToSithAgain = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2597, 3107, 0), "Go upstairs to Sithik upstairs in north Yanille.", strangePotion);

		goDownstairsFromSith = new ObjectStep(this, ObjectID.LADDER_16681, new WorldPoint(2597, 3107, 1), "Leave Sithik Int's house.");
		goUpToOgreSith = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2597, 3107, 0), "Go upstairs to Sithik again.");

		talkToSithForAnswers = new ObjectStep(this, NullObjectID.NULL_6887, new WorldPoint(2591, 3104, 1), "Talk to the transformed Sithik Ints for answers.");
		talkToSithForAnswers.addDialogSteps("How do I remove the effects of the spell from the area?");

		askAboutRemovingUndead = new ObjectStep(this, NullObjectID.NULL_6887, new WorldPoint(2591, 3104, 1), "Talk to the transformed Sithik Ints for answers.");
		askAboutRemovingUndead.addDialogStep("How do I get rid of the undead ogres?");

		askAboutRemovingDisease = new ObjectStep(this, NullObjectID.NULL_6887, new WorldPoint(2591, 3104, 1), "Talk to the transformed Sithik Ints for answers.");
		askAboutRemovingDisease.addDialogStep("How do I get rid of the disease?");
		talkToSithForAnswers.addSubSteps(askAboutRemovingDisease, askAboutRemovingUndead);

		talkToGrishForKey = new NpcStep(this, NpcID.GRISH, new WorldPoint(2447, 3049, 0), "Talk to Grish south of Castle Wars. Be prepared to fight Slash Bash. You'll need to use either brutal arrows or the Crumble Undead spell.");
		talkToGrishForKey.addDialogSteps("I found who's responsible for the Zogres being here.");
		talkToGrishForBow = new NpcStep(this, NpcID.GRISH, new WorldPoint(2447, 3049, 0), "Talk to Grish south of Castle Wars again to learn how to make composite ogre bows.");
		talkToGrishForBow.addDialogStep("There must be an easier way to kill these zogres!");

		climbBarricadeForBoss = new ObjectStep(this, NullObjectID.NULL_6878, new WorldPoint(2456, 3048, 0), "Climb over the barricade east of Grish, prepared to fight Slash Bash.", grishKey);
		climbBarricadeForBoss.addDialogStep("Sorry, I have to go.");
		goDownStairsForBoss = new ObjectStep(this, ObjectID.STAIRS_6841, new WorldPoint(2486, 3043, 0), "Climb down the stairs to the east.");

		enterDoors = new ObjectStep(this, ObjectID.OGRE_STONE_DOOR_6872, new WorldPoint(2442, 9433, 2), "Enter the doors at the west side of the area.", grishKey);
		goDownToBoss = new ObjectStep(this, ObjectID.STAIRS_6841, new WorldPoint(2444, 9418, 2), "Go down the stairs to the boss.");

		searchStand = new ObjectStep(this, ObjectID.STAND, new WorldPoint(2483, 9445, 0), "Search the stand in the north east corner, and kill Slash Bash when he spawns.");

		pickUpOgreArtefact = new ItemStep(this, "Pick up the ogre artefact.", ogreRelic);

		returnArtefactToGrish = new NpcStep(this, NpcID.GRISH, new WorldPoint(2447, 3049, 0), "Return to Grish with the ogre artefact.", ogreRelic);
		returnArtefactToGrish.addDialogStep("Yeah, I have them here!");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Collections.singletonList(combatGear));
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Slash Bash (level 111)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToGrish, talkToGuard, goDownStairs, searchSkeleton, killZombie, openBackpack, searchLectern, searchCoffin, useKnifeOnCoffin, openCoffin, searchCoffinProperly)), combatGear));
		allSteps.add(new PanelDetails("Investigating", new ArrayList<>(Arrays.asList(talkToZavistic, goUpToSith, searchWardrobe, searchCupboard, searchDrawers, usePapyrusOnSith, useTankardOnBartender, usePortraitOnBartender, bringSignedPortraitToZavistic))));
		allSteps.add(new PanelDetails("Discover the truth", new ArrayList<>(Arrays.asList(goUpToSith, usePotionOnTea, goDownstairsFromSith, goUpToOgreSith, talkToSithForAnswers))));
		allSteps.add(new PanelDetails("Help the ogres", new ArrayList<>(Arrays.asList(talkToGrishForKey, talkToGrishForBow, climbBarricadeForBoss, goDownStairsForBoss, enterDoors, goDownToBoss, searchStand, pickUpOgreArtefact, returnArtefactToGrish)), combatGear));

		return allSteps;
	}
}
