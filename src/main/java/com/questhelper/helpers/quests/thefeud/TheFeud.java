/*
 * Copyright (c) 2021, Kerpackie
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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

package com.questhelper.helpers.quests.thefeud;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
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

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
		quest = QuestHelperQuest.THE_FEUD
)
public class TheFeud extends BasicQuestHelper
{

	//Items Requirements
	ItemRequirement coins, unspecifiedCoins, gloves, headPiece, fakeBeard, desertDisguise,
			shantayPass, beer, oakBlackjack, disguiseEquipped, doorKeys,
			highlightedCoins, snakeCharmHighlighted, snakeBasket, snakeBasketFull,
			redHotSauce, bucket, dung, poisonHighlighted, oakBlackjackEquipped;

	//Items Recommended
	ItemRequirement combatGear;

	Requirement doesNotHaveBucket;

	Conditions hasDisguiseComponents, doesNotHaveDisguise, doesNotHaveDisguiseComponents, notThroughShantayGate, hasDisguise;

	ObjectCondition dungNearby;

	QuestStep startQuest, buyDisguiseGear, createDisguise, goToShantay;

	DetailedQuestStep getBucket, equipBlackjack, killMenaphiteThug, killBanditChampion;

	NpcStep buyShantayPass, talkToRugMerchant, drunkenAli, talkToThug, talkToBandit, talkToCamelman, talkToBanditReturnedCamel, talkToMenaphiteReturnedCamel,
			talkToAliTheOperator, pickpocketVillager, pickPocketVillagerWithUrchin, getBlackjackFromAli, blackjackVillager,
			talkToAliToGetSecondJob, giveTheJewelsToAli, talkMenaphiteToFindTraitor, tellAliYouFoundTraitor, talkToAliTheBarman,
			talkToAliTheHag, catchSnake, giveSnakeToHag, talkToAliTheKebabSalesman, givenDungToHag, tellAliOperatorPoisoned,
			talkToMenaphiteLeader, talkToAVillager, talkToBanditLeader, talkToAVillagerToSpawnMayor, talkToMayor, finishQuest;

	ObjectStep hideBehindCactus, pickupDung, openTheDoor, goUpStairs, crackTheSafe, goDownStairs, giveCoinToSnakeCharmer, getDung, poisonTheDrink;

	VarbitRequirement talkedToThug, talkedToBandit, talkedToBanditReturn, doorOpen, traitorFound, talkedToBarman, talkedToAliTheHag,
			givenPoisonToHag, menaphiteThugAlive, talkedToVillagerAboutMenaphite, banditChampionSpawned, mayorSpawned;

	//Zones
	ZoneRequirement inPollniveach, secondFloorMansion, inShantayDesertSide;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		setupVarBits();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);

		ConditionalStep goToPollniveach = new ConditionalStep(this, buyShantayPass);
		goToPollniveach.addStep(new Conditions(hasDisguise, inPollniveach), drunkenAli);
		goToPollniveach.addStep(new Conditions(hasDisguise, inShantayDesertSide), talkToRugMerchant);
		goToPollniveach.addStep(new Conditions(notThroughShantayGate, doesNotHaveDisguise, doesNotHaveDisguiseComponents), buyDisguiseGear);
		goToPollniveach.addStep(new Conditions(notThroughShantayGate, doesNotHaveDisguise, hasDisguiseComponents), createDisguise);
		goToPollniveach.addStep(new Conditions(notThroughShantayGate, shantayPass), goToShantay);
		steps.put(1, goToPollniveach);

		ConditionalStep findBeef = new ConditionalStep(this, talkToThug);
		findBeef.addStep(talkedToBandit, talkToCamelman);
		findBeef.addStep(talkedToThug, talkToBandit);
		steps.put(2, findBeef);

		steps.put(3, talkToCamelman);

		ConditionalStep returnCamels = new ConditionalStep(this, talkToBanditReturnedCamel);
		returnCamels.addStep(talkedToBanditReturn, talkToMenaphiteReturnedCamel);
		steps.put(4, returnCamels);

		steps.put(5, talkToAliTheOperator);
		steps.put(6, pickpocketVillager);

		steps.put(7, pickPocketVillagerWithUrchin);
		steps.put(8, pickPocketVillagerWithUrchin);

		ConditionalStep blackjackVillagerStep = new ConditionalStep(this, getBlackjackFromAli);
		blackjackVillagerStep.addStep(oakBlackjackEquipped, blackjackVillager);
		blackjackVillagerStep.addStep(oakBlackjack, equipBlackjack);
		steps.put(9, blackjackVillagerStep);
		steps.put(10, blackjackVillagerStep);
		steps.put(11, blackjackVillagerStep);

		steps.put(12, talkToAliToGetSecondJob);
		steps.put(13, hideBehindCactus);

		ConditionalStep heist = new ConditionalStep(this, openTheDoor);
		heist.addStep(secondFloorMansion, crackTheSafe);
		heist.addStep(doorOpen, goUpStairs);
		steps.put(14, heist);

		ConditionalStep returnTheJewels = new ConditionalStep(this, giveTheJewelsToAli);
		returnTheJewels.addStep(secondFloorMansion, goDownStairs);
		steps.put(15, returnTheJewels);

		ConditionalStep findTraitor = new ConditionalStep(this, talkMenaphiteToFindTraitor);
		findTraitor.addStep(traitorFound, tellAliYouFoundTraitor);
		steps.put(16, findTraitor);

		ConditionalStep getSnake = new ConditionalStep(this, talkToAliTheBarman);
		getSnake.addStep(givenPoisonToHag, talkToAliTheKebabSalesman);
		getSnake.addStep(snakeBasketFull, giveSnakeToHag);
		getSnake.addStep(new Conditions(talkedToAliTheHag, snakeCharmHighlighted, snakeBasket), catchSnake);
		getSnake.addStep(talkedToAliTheHag, giveCoinToSnakeCharmer);
		getSnake.addStep(talkedToBarman, talkToAliTheHag);
		steps.put(17, getSnake);

		ConditionalStep camelDung = new ConditionalStep(this, talkToAliTheKebabSalesman);
		camelDung.addStep(new Conditions(givenPoisonToHag, dung), givenDungToHag);
		camelDung.addStep(new Conditions(givenPoisonToHag, bucket, dungNearby), pickupDung);
		camelDung.addStep(new Conditions(givenPoisonToHag, redHotSauce, bucket), getDung);
		camelDung.addStep(new Conditions(givenPoisonToHag, redHotSauce, doesNotHaveBucket), getBucket);
		steps.put(18, camelDung);

		steps.put(22, poisonTheDrink);
		steps.put(23, tellAliOperatorPoisoned);

		ConditionalStep killThug = new ConditionalStep(this, talkToMenaphiteLeader);
		killThug.addStep(menaphiteThugAlive, killMenaphiteThug);
		steps.put(24, killThug);

		ConditionalStep killChampion = new ConditionalStep(this, talkToAVillager);
		killChampion.addStep(banditChampionSpawned, killBanditChampion);
		killChampion.addStep(talkedToVillagerAboutMenaphite, talkToBanditLeader);
		steps.put(25, killChampion);

		ConditionalStep spawnMayor = new ConditionalStep(this, talkToAVillagerToSpawnMayor);
		spawnMayor.addStep(mayorSpawned, talkToMayor);
		steps.put(26, spawnMayor);

		steps.put(27, finishQuest);

		return steps;
	}

	public void setupVarBits()
	{
		//318 Drunk Ali beer count 0->1->2->3

		// 315 -> 2 Talked to thug -> 3 Talked to bandit
		talkedToThug = new VarbitRequirement(315, 2);
		talkedToBandit = new VarbitRequirement(315, 3);

		talkedToBanditReturn = new VarbitRequirement(316, true, 0); // Might have missed?
		// 340 -> 1 when pickpocket villager
		doorOpen = new VarbitRequirement(320, 1);

		//Varbit 325 keeps track of correctly entered safe values
		//UPDATE: 325 keeps track of amount of numbers input. Even if they are incorrect
		//this VarBit updates.
		//TODO: Overlay for safe cracking.

		// Varbit 342 when found Traitor
		traitorFound = new VarbitRequirement(342, 1);

		// Varbit 321 when talked to bar man
		talkedToBarman = new VarbitRequirement(321, 1);

		// 345 and 328 when talking to hag
		talkedToAliTheHag = new VarbitRequirement(328, 1);
		givenPoisonToHag = new VarbitRequirement(328, 2);

		// 335 = Poisoned drink

		//322 Menaphite
		menaphiteThugAlive = new VarbitRequirement(322, 1);

		// Talked to villager about Menaphite 343, 338
		talkedToVillagerAboutMenaphite = new VarbitRequirement(343, 1);
		banditChampionSpawned = new VarbitRequirement(323, 1);

		// 343 -> Mayor spawned
		mayorSpawned = new VarbitRequirement(343, 2);
	}

	@Override
	public void setupRequirements()
	{
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 800);
		unspecifiedCoins = new ItemRequirement("Coins", ItemCollections.COINS, -1);
		highlightedCoins = new ItemRequirement("Coins", ItemCollections.COINS);
		highlightedCoins.setHighlightInInventory(true);
		gloves = new ItemRequirement("Leather or Graceful Gloves", ItemID.LEATHER_GLOVES).isNotConsumed();
		gloves.addAlternates(ItemCollections.GRACEFUL_GLOVES);

		headPiece = new ItemRequirement("Kharidian Headpiece", ItemID.KHARIDIAN_HEADPIECE).isNotConsumed();
		headPiece.setHighlightInInventory(true);
		fakeBeard = new ItemRequirement("Fake Beard", ItemID.FAKE_BEARD).isNotConsumed();
		fakeBeard.setHighlightInInventory(true);
		desertDisguise = new ItemRequirement("Desert Disguise", ItemID.DESERT_DISGUISE).isNotConsumed();
		disguiseEquipped = new ItemRequirement("Desert Disguise", ItemID.DESERT_DISGUISE, 1, true).isNotConsumed();
		shantayPass = new ItemRequirement("Shantay Pass", ItemID.SHANTAY_PASS);
		beer = new ItemRequirement("Beer", ItemID.BEER, 3);
		beer.setHighlightInInventory(true);
		oakBlackjack = new ItemRequirement("Oak Blackjack", ItemID.OAK_BLACKJACK).isNotConsumed();
		oakBlackjack.setHighlightInInventory(true);
		oakBlackjackEquipped = oakBlackjack.equipped();
		doorKeys = new ItemRequirement("Keys", ItemID.KEYS);
		doorKeys.setHighlightInInventory(true);
		snakeCharmHighlighted = new ItemRequirement("Snake Charm", ItemID.SNAKE_CHARM);
		snakeCharmHighlighted.setHighlightInInventory(true);
		snakeBasket = new ItemRequirement("Snake Basket", ItemID.SNAKE_BASKET);
		snakeBasketFull = new ItemRequirement("Snake Basket (Full)", ItemID.SNAKE_BASKET_FULL);
		redHotSauce = new ItemRequirement("Red Hot Sauce", ItemID.RED_HOT_SAUCE);
		redHotSauce.setHighlightInInventory(true);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		dung = new ItemRequirement("Dung", ItemID.UGTHANKI_DUNG);
		poisonHighlighted = new ItemRequirement("Hag's Poison", ItemID.HAGS_POISON);
		poisonHighlighted.setHighlightInInventory(true);

		//Combat Gear
		combatGear = new ItemRequirement("Combat Gear bring Range or Mage Gear if safe spotting.", -1, -1 );
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void loadZones()
	{
		Zone pollniveachZone = new Zone(new WorldPoint(3320, 2926, 0), new WorldPoint(3381, 3006, 0));
		Zone secondFloor = new Zone(new WorldPoint(3366, 2965, 1), new WorldPoint(3375, 2979, 1));
		Zone shantayDesertSide = new Zone(new WorldPoint(3325, 3116, 0), new WorldPoint(3278, 3086, 0));

		inPollniveach = new ZoneRequirement(pollniveachZone);
		secondFloorMansion = new ZoneRequirement(secondFloor);
		inShantayDesertSide = new ZoneRequirement(shantayDesertSide);
	}

	public void setupConditions()
	{
		//Disguise
		hasDisguiseComponents = new Conditions(fakeBeard, headPiece);
		doesNotHaveDisguise = new Conditions(LogicType.NAND, desertDisguise);
		doesNotHaveDisguiseComponents = new Conditions(LogicType.NAND, fakeBeard, headPiece);
		hasDisguise = new Conditions(desertDisguise.alsoCheckBank(questBank));

		//a
		notThroughShantayGate = new Conditions(LogicType.NAND, inShantayDesertSide);

		//Blackjack
		oakBlackjackEquipped = new ItemRequirements(new ItemRequirement("Oak Blackjack", ItemID.OAK_BLACKJACK, 1, true));

		//Dung
		doesNotHaveBucket = new ComplexRequirement(LogicType.NOR, "", new ItemRequirement("Bucket", ItemID.BUCKET));
		dungNearby = new ObjectCondition(ObjectID.DUNG);
	}

	public void setupSteps()
	{
		//Step 0-1
		//Start Quest & Purchase Disguise
		startQuest = new NpcStep(this, NpcID.ALI_MORRISANE, new WorldPoint(3304, 3211, 0), "Talk to  Ali Morrisane in Al Kharid to start the quest.");
		startQuest.addDialogStep("If you are, then why are you still selling goods from a stall?");
		startQuest.addDialogStep("I'd like to help you but.....");
		startQuest.addDialogStep("I'll find you your help.");
		startQuest.addDialogStep("Yes.");

		buyDisguiseGear = new NpcStep(this, NpcID.ALI_MORRISANE, new WorldPoint(3304, 3211, 0), "Buy a Kharidian Headpiece and a Fake Beard to create a disguise.", unspecifiedCoins);
		buyDisguiseGear.addDialogStep("Okay.");

		createDisguise = new DetailedQuestStep(this, "Create a disguise by using the Kharidian Headpiece on the Fake Beard.", headPiece, fakeBeard);

		//To Pollnivneach
		goToShantay = new ObjectStep(this, ObjectID.SHANTAY_PASS, new WorldPoint(3304, 3116, 0), "Go through Shantay Pass.", shantayPass);
		buyShantayPass = new NpcStep(this, NpcID.SHANTAY, new WorldPoint(3303, 3122, 0), "Buy a shantay pass from Shantay.", unspecifiedCoins);

		talkToRugMerchant = new NpcStep(this, NpcID.RUG_MERCHANT, new WorldPoint(3311, 3109, 0),"Talk to the rug merchant and travel to Pollnivneach via magic carpet.", unspecifiedCoins);
		talkToRugMerchant.addDialogStep("Pollnivneach");


		//Drunken Ali
		drunkenAli = new NpcStep(this, NpcID.DRUNKEN_ALI, new WorldPoint(3360, 2957, 0), "Buy 3 beers from the bartender and use them on Drunken Ali to get him to explain where his son is. Talk with him between beers", beer);
		drunkenAli.addIcon(ItemID.BEER);

		//Step 2
		//Find Beef
		talkToThug = new NpcStep(this, NpcID.MENAPHITE_THUG, new WorldPoint(3347, 2955, 0), "Talk to a Menaphite Thug to figure out how their dispute started with the bandits.", true);
		talkToBandit = new NpcStep(this, NpcID.BANDIT_734, new WorldPoint(3362, 2993, 0),"Talk to a bandit to figure out their issues with the Menaphites.", true);

		//Step 3
		//Buy Camels
		talkToCamelman = new NpcStep(this, NpcID.ALI_THE_CAMEL_MAN, new WorldPoint(3350, 2966, 0), "Talk to Ali the Camel Man to try and get two camels to solve the dispute");
		talkToCamelman.addDialogStep("Are those camels around the side for sale?");
		talkToCamelman.addDialogStep("What price do you want for both of them?");
		talkToCamelman.addDialogStep("Would 500 gold coins for the pair of them do?");


		//Step 4
		//Return Camels
		talkToBanditReturnedCamel = new NpcStep(this, NpcID.BANDIT_734, new WorldPoint(3362, 2993, 0),"Tell the bandits that the Menaphites have agreed to return the camel.", true);
		talkToMenaphiteReturnedCamel = new NpcStep(this, NpcID.MENAPHITE_THUG, new WorldPoint(3347, 2955, 0), "Tell the Menaphites that the bandits have agreed to return the camel.", true);

		//Step 5
		//Get First Job
		talkToAliTheOperator = new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0),"Talk to Ali the Operator to get a job from him.");
		talkToAliTheOperator.addDialogStep("Yes, of course, those bandits should be taught a lesson.");

		//Step 6
		//Pickpocket Villager
		pickpocketVillager = new NpcStep(this, NpcID.VILLAGER, new WorldPoint(3356, 2962, 0), "Pickpocket a villager.", true);
		pickpocketVillager.setHideWorldArrow(true);
		pickpocketVillager.addAlternateNpcs(NpcID.VILLAGER_3555, NpcID.VILLAGER_3558);

		//Step 7 -> 8
		//Pickpocket with Urchin
		pickPocketVillagerWithUrchin = new NpcStep(this, NpcID.STREET_URCHIN, "Lure a villager then talk to a street urchin and get them to distract a villager, once he has them distracted pickpocket them from behind.", true);

		//Step 9 -> 11
		//Blackjack
		getBlackjackFromAli = new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0), ("Talk to Ali the Operator to get a blackjack."));
		getBlackjackFromAli.addDialogStep("Yeah, I could do with a bit of advice.");

		equipBlackjack = new DetailedQuestStep(this, "Equip your oak blackjack in your inventory.", oakBlackjack);

		blackjackVillager = new NpcStep(this, NpcID.VILLAGER, "Lure a villager to secluded place or an empty building, knock them out and then pickpocket them.", true);
		blackjackVillager.addAlternateNpcs(NpcID.VILLAGER_3555, NpcID.VILLAGER_3557, NpcID.VILLAGER_3558, NpcID.VILLAGER_3560);


		//Step 12
		//Get Second Job
		talkToAliToGetSecondJob = new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0), "Talk to Ali the Operator to get the second job.");

		//Step 13
		//Hide Behind Cactus - Second Job
		hideBehindCactus = new ObjectStep(this, ObjectID.CACTUS_6277, new WorldPoint(3364, 2968, 0), "Hide behind the cactus", gloves.equipped(), disguiseEquipped);

		//Step 14
		//Open the Door
		openTheDoor = new ObjectStep(this, 6238, "Open the door.", doorKeys);
		openTheDoor.addAlternateObjects(6240);
		openTheDoor.addIcon(ItemID.KEYS);

		goUpStairs = new ObjectStep(this, ObjectID.STAIRCASE_6244, "Go up the stairs.");

		crackTheSafe = new ObjectStep(this, 6276, "Search the painting to reveal the safe. Enter the code 1, 1, 2, 3, 5, 8.");

		//Step 15
		//Return the Jewels
		goDownStairs = new ObjectStep(this, ObjectID.STAIRCASE_6245, "Go down the stairs.");

		giveTheJewelsToAli = new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0), "Give Ali the Operator the jewels to get your final task from him.");

		//Step 16
		//Find Traitor
		talkMenaphiteToFindTraitor =  new NpcStep(this, NpcID.MENAPHITE_THUG, new WorldPoint(3347, 2955, 0),"Talk to a Menaphite member to find the traitor.", true);
		tellAliYouFoundTraitor = new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0),"Talk to Ali and tell him you have found the traitor.");

		//Step 17
		//Get Snake & Talk to Barman
		talkToAliTheBarman = new NpcStep(this, NpcID.ALI_THE_BARMAN, new WorldPoint(3361, 2956, 0), "Talk to Ali the Barman to find out where Traitorous Ali is.");
		talkToAliTheBarman.addDialogStep(("I'm looking for Traitorous Ali."));
		talkToAliTheBarman.addDialogStep("No thanks I'm ok.");

		talkToAliTheHag = new NpcStep(this, NpcID.ALI_THE_HAG, new WorldPoint(3345, 2986, 0), "Talk to Ali the Hag to ask her to make some poison for you.");

		giveCoinToSnakeCharmer = new ObjectStep(this, ObjectID.MONEY_POT, new WorldPoint(3355, 2953, 0), "Use coins on the snake charmer's money pot to get a snake charm and a snake basket.", highlightedCoins);
		giveCoinToSnakeCharmer.addIcon(ItemID.COINS);

		catchSnake = new NpcStep(this, NpcID.SNAKE_3544, new WorldPoint(3332, 2958, 0), "Use the Snake Charm on a snake to capture it.", true, snakeCharmHighlighted, snakeBasket);
		catchSnake.addIcon(ItemID.SNAKE_CHARM);

		giveSnakeToHag = new NpcStep(this, NpcID.ALI_THE_HAG, new WorldPoint(3345, 2986, 0), "Give the snake to Ali the Hag.", snakeBasketFull);

		//Step 18
		//Camel Dung - Get Dung
		talkToAliTheKebabSalesman = new NpcStep(this, NpcID.ALI_THE_KEBAB_SELLER, new WorldPoint(3352, 2975, 0), "Talk to Ali the Kebab Salesman to get a special sauce to use.");
		talkToAliTheKebabSalesman.addDialogStep("Would you sell me that bottle of special kebab sauce?");
		talkToAliTheKebabSalesman.addDialogStep("No thanks, I'm good.");

		getBucket = new DetailedQuestStep(this, new WorldPoint(3346, 2966, 0), "Pick up a bucket to grab the dung");

		pickupDung = new ObjectStep(this, ObjectID.DUNG, "Use the bucket on the dung.", bucket.highlighted());
		pickupDung.addIcon(ItemID.BUCKET);

		getDung = new ObjectStep(this, ObjectID.TROUGH_6256, new WorldPoint(3343, 2960, 0), "Use the Red Hot Sauce on the Trough and wait for a Camel to poop out the dung.\n Only pickup brown/Ugthanki dung, if you plan to do \"My Arm's Big Adventure\" or \"Forgettable Tale of a Drunken Dwarf\" then you may want to grab four more Ugthanki dung.", redHotSauce);
		getDung.addIcon(ItemID.RED_HOT_SAUCE);

		givenDungToHag = new NpcStep(this, NpcID.ALI_THE_HAG, new WorldPoint(3345, 2986, 0), "Talk to Ali the Hag and give her the dung.", dung);

		//Step 22
		//Poison The Drink
		poisonTheDrink = new ObjectStep(this, ObjectID.TABLE_6246, new WorldPoint(3356, 2957, 0), "Use the poison on the beer glass to kill Traitorous Ali.", poisonHighlighted);
		poisonTheDrink.addIcon(ItemID.HAGS_POISON);

		//Step 23
		//Tell Ali The Operator Poisoned
		tellAliOperatorPoisoned = new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0), "Talk to Ali the Operator and tell him he is dead.");

		//Step 24
		//Kill Thug
		talkToMenaphiteLeader = new NpcStep(this, NpcID.MENAPHITE_LEADER, "Talk to the Menaphite Leader and prepare for a fight against a tough guy. You can safespot him inside the tent by using a chair.");
		talkToMenaphiteLeader.addSubSteps(killMenaphiteThug);

		killMenaphiteThug = new DetailedQuestStep(this, "Kill the Menaphite Thug. You can safespot him inside the tent by using a chair, if he's not spawned then talk to the Menaphite Leader again.");

		//Step 25
		//Kill Champion - Talk to Villager
		talkToAVillager = new NpcStep(this, NpcID.VILLAGER, "Talk to a villager and they will be angry you broke the balance of power.", true);
		talkToAVillager.addAlternateNpcs(NpcID.VILLAGER_3554, NpcID.VILLAGER_3555, NpcID.VILLAGER_3557, NpcID.VILLAGER_3558, NpcID.VILLAGER_3560);

		talkToBanditLeader = new NpcStep(this, NpcID.BANDIT_LEADER, new WorldPoint(3353, 3002, 0), "Talk to the Bandit Leader and be prepared to fight a Bandit Champion. You can safe spot him with the stool.");
		talkToBanditLeader.addSubSteps(killBanditChampion);

		killBanditChampion = new DetailedQuestStep(this, "Kill the Bandit Champion, you can safe spot him with the stool. \n If he's not spawned then talk to the Bandit Leader again.");

		//Step 26
		//Spawn Mayor
		talkToAVillagerToSpawnMayor  = new NpcStep(this, NpcID.VILLAGER, "Talk to a villager and they will be angry still.", true);
		talkToAVillagerToSpawnMayor.addAlternateNpcs(NpcID.VILLAGER_3554, NpcID.VILLAGER_3555, NpcID.VILLAGER_3557, NpcID.VILLAGER_3558, NpcID.VILLAGER_3560);

		talkToMayor = new NpcStep(this, NpcID.ALI_THE_MAYOR, new WorldPoint(3360, 2972, 0), "Talk to Ali the Mayor and get your due congratulations.");

		//Step 27
		//Finish Quest
		finishQuest = new NpcStep(this, NpcID.ALI_MORRISANE, new WorldPoint(3304, 3211, 0), "Talk to  Ali Morrisane in Al Kharid to finish the quest.");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.THIEVING, 30));
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins, gloves, combatGear);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Bandit Champion (level 70) - Safespottable", "Tough Guy (level 75) - Safespottable");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.THIEVING, 15000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("500 Coins", ItemID.COINS_995, 500),
				new ItemReward("Oak Blackjack", ItemID.OAK_BLACKJACK, 1),
				new ItemReward("Desert Disguise", ItemID.DESERT_DISGUISE, 1),
				new ItemReward("Willow Blackjack", ItemID.WILLOW_BLACKJACK, 1),
				new ItemReward("An Adamant Scimitar", ItemID.ADAMANT_SCIMITAR, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to blackjack."),
				new UnlockReward("Access to the Rogue Trader minigame"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out",
				Collections.singletonList(startQuest), unspecifiedCoins));
		allSteps.add(new PanelDetails("Pollnivneach",
				Arrays.asList(buyDisguiseGear, createDisguise, goToShantay, talkToRugMerchant, drunkenAli), unspecifiedCoins));
		allSteps.add(new PanelDetails("Find the beef between the two factions",
				Arrays.asList(talkToThug, talkToBandit, talkToCamelman, talkToBanditReturnedCamel, talkToMenaphiteReturnedCamel), unspecifiedCoins));
		allSteps.add(new PanelDetails("First job",
			Arrays.asList(talkToAliTheOperator, pickpocketVillager, pickPocketVillagerWithUrchin,
				getBlackjackFromAli, blackjackVillager), unspecifiedCoins));
		allSteps.add(new PanelDetails("Second job",
				Arrays.asList(talkToAliToGetSecondJob, hideBehindCactus, openTheDoor, goUpStairs, crackTheSafe, giveTheJewelsToAli), desertDisguise, gloves));
		allSteps.add(new PanelDetails("Rising up",
				Arrays.asList(talkMenaphiteToFindTraitor, tellAliYouFoundTraitor, talkToAliTheBarman, talkToAliTheHag, giveCoinToSnakeCharmer, catchSnake, giveSnakeToHag, talkToAliTheKebabSalesman, getDung, givenDungToHag, poisonTheDrink, tellAliOperatorPoisoned), unspecifiedCoins));
		allSteps.add(new PanelDetails("Finishing off",
				Arrays.asList(talkToMenaphiteLeader, talkToAVillager, talkToBanditLeader, talkToAVillagerToSpawnMayor, talkToMayor, finishQuest), combatGear));

		return allSteps;
	}
}
