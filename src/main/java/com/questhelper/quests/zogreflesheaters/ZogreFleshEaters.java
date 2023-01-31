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

import com.questhelper.QuestHelperQuest;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.ZOGRE_FLESH_EATERS
)
public class ZogreFleshEaters extends BasicQuestHelper
{
	//Items Required
	ItemRequirement knife, backpack, tankard, tornPage, blackPrism, necroBook, hamBook, portrait, goodPort, strangePotionHighlighted, sanfew,
		badPort, charcoal, papyrus, signedPort, cupOfTea, strangePotion, grishKey, ogreRelic, combatGear, knifeHighlighted, tankardHighlighted;

	Requirement askedAboutSickies, inSurface, inTombF2, killedZombie, searchedCoffin, usedKnife, openedCoffin,
		talkedToZavistic, hasBackpackOrTankard, atSith, askedSithToLookAround, hasOrShownNecroBook, hasOrShownHamBook, 
		shownNecroBook, shownHamBook, shownTankard, usedTankardOnBartender, usedPortraitOnBartender, hasOrShownSignedPortrait,
		shownSignedPortrait, sithTransformed, askedAboutDisease, askedAboutGettingRidOfUndead, askedAboutBow, inTombF0, inTombF2ToBoss, ogreRelicNearby;

	QuestStep talkToGrish, talkToGuard, climbBarricade, goDownStairs, searchSkeleton, killZombie, searchLectern, searchCoffin, useKnifeOnCoffin, openCoffin,
		openBackpack, searchCoffinProperly, useTankardOnBartender, talkToZavistic, goUpToSith, talkToSith, searchDrawers, searchCupboard, searchWardrobe, dropPortraitAndSearchDrawers,
		usePapyrusOnSith, usePortraitOnBartender, bringSignedPortraitToZavistic, goUpToSithAgain, usePotionOnTea, goDownstairsFromSith, goUpToOgreSith, talkToSithForAnswers,
		askAboutRemovingUndead, askAboutRemovingDisease, talkToGrishForKey, talkToGrishForBow, climbBarricadeForBoss, goDownStairsForBoss, enterDoors,
		goDownToBoss, searchStand, pickUpOgreArtefact, returnArtefactToGrish;

	//Zones
	Zone surface, tombF2, sith, tombF0, tombF2ToBoss;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGrish);
		steps.put(2, talkToGuard);

		ConditionalStep explore = new ConditionalStep(this, climbBarricade);
		explore.addStep(new Conditions(hasOrShownHamBook, hasOrShownNecroBook, hasOrShownSignedPortrait), bringSignedPortraitToZavistic);
		explore.addStep(new Conditions(hasOrShownHamBook, hasOrShownNecroBook, goodPort, usedTankardOnBartender), usePortraitOnBartender);
		explore.addStep(new Conditions(tankard, hasOrShownHamBook, hasOrShownNecroBook, goodPort), useTankardOnBartender);
		explore.addStep(new Conditions(tankard, atSith, hasOrShownHamBook, hasOrShownNecroBook, papyrus), usePapyrusOnSith);
		explore.addStep(new Conditions(tankard, atSith, hasOrShownHamBook, hasOrShownNecroBook, badPort), dropPortraitAndSearchDrawers);
		explore.addStep(new Conditions(tankard, atSith, hasOrShownHamBook, hasOrShownNecroBook), searchDrawers);
		explore.addStep(new Conditions(tankard, atSith, hasOrShownHamBook), searchCupboard);
		explore.addStep(new Conditions(tankard, atSith, askedSithToLookAround), searchWardrobe);
		explore.addStep(new Conditions(tankard, talkedToZavistic, atSith), talkToSith);
		explore.addStep(new Conditions(tankard, talkedToZavistic), goUpToSith);

		explore.addStep(new Conditions(tankard, tornPage, blackPrism), talkToZavistic);
		explore.addStep(new Conditions(inTombF2, tankard, tornPage, openedCoffin), searchCoffinProperly);
		explore.addStep(new Conditions(inTombF2, tankard, tornPage, usedKnife), openCoffin);
		explore.addStep(new Conditions(inTombF2, tankard, tornPage, searchedCoffin), useKnifeOnCoffin);
		explore.addStep(new Conditions(inTombF2, tankard, tornPage), searchCoffin);
		explore.addStep(new Conditions(inTombF2, tankard), searchLectern);
		explore.addStep(new Conditions(inTombF2, backpack), openBackpack);
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

	@Override
	public void setupRequirements()
	{
		backpack = new ItemRequirement("Ruined backpack", ItemID.RUINED_BACKPACK);
		backpack.setHighlightInInventory(true);
		tankard = new ItemRequirement("Dragon inn tankard", ItemID.DRAGON_INN_TANKARD);
		tankardHighlighted = new ItemRequirement("Dragon inn tankard", ItemID.DRAGON_INN_TANKARD);
		tankardHighlighted.setHighlightInInventory(true);
		tornPage = new ItemRequirement("Torn page", ItemID.TORN_PAGE);
		blackPrism = new ItemRequirement("Black prism", ItemID.BLACK_PRISM);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).isNotConsumed();
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
		sanfew = new ItemRequirement("Sanfew serum or Relicym's balm", ItemID.SANFEW_SERUM1);
		sanfew.addAlternates(ItemID.SANFEW_SERUM2, ItemID.SANFEW_SERUM3, ItemID.SANFEW_SERUM4, ItemID.RELICYMS_BALM1,
		ItemID.RELICYMS_BALM2, ItemID.RELICYMS_BALM3, ItemID.RELICYMS_BALM4);
		sanfew.setTooltip("To help prevent disease and restore stats.");
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		cupOfTea = new ItemRequirement("Cup of tea", ItemID.CUP_OF_TEA_4838);
		strangePotion = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION);
		strangePotion.setTooltip("You can get another from Zavistic Rarve");

		strangePotionHighlighted = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION);
		strangePotionHighlighted.setTooltip("You can get another from Zavistic Rarve");
		strangePotionHighlighted.setHighlightInInventory(true);

		grishKey = new ItemRequirement("Ogre gate key", ItemID.OGRE_GATE_KEY);
		ogreRelic = new ItemRequirement("Ogre artefact", ItemID.OGRE_ARTEFACT);
		ogreRelic.setTooltip("You can get another by searching the stand where you fought Slash Bash");

		combatGear = new ItemRequirement("Either brutal arrows or Crumble Undead for fighting Slash Bash", -1, -1).isNotConsumed();
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
		askedAboutSickies = new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "Da sickies is when yous creature goes like orange");
		inSurface = new ZoneRequirement(surface);
		inTombF2 = new ZoneRequirement(tombF2);
		inTombF0 = new ZoneRequirement(tombF0);
		inTombF2ToBoss = new ZoneRequirement(tombF2ToBoss);
		killedZombie = new VarbitRequirement(503, 2);
		hasBackpackOrTankard = new Conditions(LogicType.OR, backpack, tankard);
		searchedCoffin = new VarbitRequirement(488, 1);
		usedKnife = new VarbitRequirement(488, 2);
		openedCoffin = new VarbitRequirement(488, 3);
		talkedToZavistic = new VarbitRequirement(488, 4, Operation.GREATER_EQUAL);
		askedSithToLookAround = new VarbitRequirement(488, 5);

		atSith = new ZoneRequirement(sith);

		usedTankardOnBartender = new VarbitRequirement(489, 1);
		usedPortraitOnBartender = new VarbitRequirement(490, 1);
		shownNecroBook = new VarbitRequirement(491, 1);
		shownHamBook = new VarbitRequirement(492, 1);
		shownTankard = new VarbitRequirement(493, 1);
		shownSignedPortrait = new VarbitRequirement(494, 1);

		hasOrShownHamBook = new Conditions(LogicType.OR, shownHamBook, hamBook);
		hasOrShownNecroBook = new Conditions(LogicType.OR, shownNecroBook, necroBook);
		hasOrShownSignedPortrait = new Conditions(LogicType.OR, shownSignedPortrait, signedPort);

		sithTransformed = new VarbitRequirement(495, 1);

		askedAboutDisease = new VarbitRequirement(498, 1);
		askedAboutGettingRidOfUndead = new VarbitRequirement(499, 1);
		askedAboutBow = new VarbitRequirement(500, 1);

		ogreRelicNearby = new ItemOnTileRequirement(ogreRelic);
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

		goUpToSithAgain = new ObjectStep(this, ObjectID.LADDER_16683, new WorldPoint(2597, 3107, 0), "Go upstairs to Sithik upstairs in north Yanille.", strangePotion);
		usePotionOnTea = new DetailedQuestStep(this, new WorldPoint(2593, 3103, 1), "Use the strange potion on the cup of tea next to Sithik.", strangePotionHighlighted);
		usePotionOnTea.addIcon(ItemID.STRANGE_POTION);

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
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(combatGear);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Slash Bash (level 111)");
		reqs.add("Zombie (level 39)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.BIG_CHOMPY_BIRD_HUNTING, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.JUNGLE_POTION, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.SMITHING, 4));
		req.add(new SkillRequirement(Skill.HERBLORE, 8));
		req.add(new SkillRequirement(Skill.RANGED, 30));
		return req;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new FreeInventorySlotRequirement(InventoryID.INVENTORY, 5));
		req.add(sanfew);
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.FLETCHING, 2000),
				new ExperienceReward(Skill.RANGED, 2000),
				new ExperienceReward(Skill.HERBLORE, 2000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Ourg Bones", ItemID.OURG_BONES, 3),
				new ItemReward("Zogre Bones", ItemID.ZOGRE_BONE, 2));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to make Relicym's balm"),
				new UnlockReward("Ability to fletch Comp Ogre Bows and Brutal Arrows"),
				new UnlockReward("Ability to wear Inoculation Bracelets"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToGrish, talkToGuard, goDownStairs, searchSkeleton, killZombie, openBackpack, searchLectern, searchCoffin, useKnifeOnCoffin, openCoffin, searchCoffinProperly)));
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(talkToZavistic, goUpToSith, searchWardrobe, searchCupboard, searchDrawers, usePapyrusOnSith, useTankardOnBartender, usePortraitOnBartender, bringSignedPortraitToZavistic)));
		allSteps.add(new PanelDetails("Discover the truth", Arrays.asList(goUpToSithAgain, usePotionOnTea, goDownstairsFromSith, goUpToOgreSith, talkToSithForAnswers)));
		allSteps.add(new PanelDetails("Help the ogres", Arrays.asList(talkToGrishForKey, talkToGrishForBow, climbBarricadeForBoss, goDownStairsForBoss, enterDoors, goDownToBoss, searchStand, pickUpOgreArtefact, returnArtefactToGrish), combatGear));

		return allSteps;
	}
}
