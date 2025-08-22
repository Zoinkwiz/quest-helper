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

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class TheFeud extends BasicQuestHelper
{

	//Items Requirements
	ItemRequirement coins, unspecifiedCoins, gloves, headPiece, fakeBeard, desertDisguise,
			shantayPass, beer, oakBlackjack, disguiseEquipped, doorKeys,
			highlightedCoins, snakeCharmHighlighted, snakeBasket, snakeBasketFull,
			redHotSauce, bucket, dung, poisonHighlighted, oakBlackjackEquipped;

	//Items Recommended
	ItemRequirement combatGear, ringOfDueling, pollnivneachTeleport;

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
	ZoneRequirement inPollnivneach, secondFloorMansion, inShantayDesertSide;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		setupVarBits();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest);

		ConditionalStep goToPollnivneach = new ConditionalStep(this, buyShantayPass);
		goToPollnivneach.addStep(new Conditions(hasDisguise, inPollnivneach), drunkenAli);
		goToPollnivneach.addStep(new Conditions(hasDisguise, inShantayDesertSide), talkToRugMerchant);
		goToPollnivneach.addStep(new Conditions(notThroughShantayGate, doesNotHaveDisguise, doesNotHaveDisguiseComponents), buyDisguiseGear);
		goToPollnivneach.addStep(new Conditions(notThroughShantayGate, doesNotHaveDisguise, hasDisguiseComponents), createDisguise);
		goToPollnivneach.addStep(new Conditions(notThroughShantayGate, shantayPass), goToShantay);
		steps.put(1, goToPollnivneach);

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

		talkedToBanditReturn = new VarbitRequirement(VarbitID.FEUD_VAR_COMP_GANGS, true, 0); // Might have missed?
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
	protected void setupRequirements()
	{
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 800);
		unspecifiedCoins = new ItemRequirement("Coins", ItemCollections.COINS, -1);
		highlightedCoins = new ItemRequirement("Coins", ItemCollections.COINS);
		highlightedCoins.setHighlightInInventory(true);
		gloves = new ItemRequirement("Leather or Graceful Gloves", ItemID.LEATHER_GLOVES).isNotConsumed();
		gloves.addAlternates(ItemCollections.GRACEFUL_GLOVES);

		headPiece = new ItemRequirement("Kharidian Headpiece", ItemID.FEUD_KARIDIAN_TURBAN).isNotConsumed();
		headPiece.setHighlightInInventory(true);
		fakeBeard = new ItemRequirement("Fake Beard", ItemID.FEUD_KARIDIAN_FAKEBEARD).isNotConsumed();
		fakeBeard.setHighlightInInventory(true);
		desertDisguise = new ItemRequirement("Desert Disguise", ItemID.FEUD_DESERT_DISGUISE).isNotConsumed();
		disguiseEquipped = new ItemRequirement("Desert Disguise", ItemID.FEUD_DESERT_DISGUISE, 1, true).isNotConsumed();
		shantayPass = new ItemRequirement("Shantay Pass", ItemID.SHANTAY_PASS);
		beer = new ItemRequirement("Beer", ItemID.BEER, 3);
		beer.setHighlightInInventory(true);
		oakBlackjack = new ItemRequirement("Oak Blackjack", ItemID.BLACKJACK_OAK).isNotConsumed();
		oakBlackjack.setHighlightInInventory(true);
		oakBlackjackEquipped = oakBlackjack.equipped();
		doorKeys = new ItemRequirement("Keys", ItemID.FEUD_MAYORS_HOUSE_KEYS);
		doorKeys.setHighlightInInventory(true);
		snakeCharmHighlighted = new ItemRequirement("Snake Charm", ItemID.SNAKE_FLUTE);
		snakeCharmHighlighted.setHighlightInInventory(true);
		snakeBasket = new ItemRequirement("Snake Basket", ItemID.BASKET_FOR_SNAKE);
		snakeBasketFull = new ItemRequirement("Snake Basket (Full)", ItemID.BASKET_WITH_SNAKE);
		redHotSauce = new ItemRequirement("Red Hot Sauce", ItemID.SUPERHOT_KEBAB_SAUCE);
		redHotSauce.setHighlightInInventory(true);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		dung = new ItemRequirement("Dung", ItemID.FEUD_CAMEL_POOH_BUCKET);
		poisonHighlighted = new ItemRequirement("Hag's Poison", ItemID.POISON_FROM_HAG);
		poisonHighlighted.setHighlightInInventory(true);

		//Combat Gear
		combatGear = new ItemRequirement("Combat Gear bring Range or Mage Gear if safe spotting.", -1, -1 );
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		//Recommended Teleports
		ringOfDueling = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS);
		pollnivneachTeleport = new ItemRequirement("Pollnivneach teleport", ItemID.NZONE_TELETAB_POLLNIVNEACH);
	}

	@Override
	protected void setupZones()
	{
		Zone pollnivneachZone = new Zone(new WorldPoint(3320, 2926, 0), new WorldPoint(3381, 3006, 0));
		Zone secondFloor = new Zone(new WorldPoint(3366, 2965, 1), new WorldPoint(3375, 2979, 1));
		Zone shantayDesertSide = new Zone(new WorldPoint(3325, 3116, 0), new WorldPoint(3278, 3086, 0));

		inPollnivneach = new ZoneRequirement(pollnivneachZone);
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
		oakBlackjackEquipped = new ItemRequirements(new ItemRequirement("Oak Blackjack", ItemID.BLACKJACK_OAK, 1, true));

		//Dung
		doesNotHaveBucket = new ComplexRequirement(LogicType.NOR, "", new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY));
		dungNearby = new ObjectCondition(ObjectID.FEUD_UGTHANKI_POOH);
	}

	public void setupSteps()
	{
		//Step 0-1
		//Start Quest & Purchase Disguise
		startQuest = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Talk to Ali Morrisane in Al Kharid to start the quest.");
		startQuest.addDialogStep("Are you really the greatest salesman in the world?");
		startQuest.addDialogStep("I'd like to help you, but...");
		startQuest.addDialogStep("Yes.");

		buyDisguiseGear = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Buy a Kharidian Headpiece and a Fake Beard to create a disguise.", unspecifiedCoins);
		buyDisguiseGear.addDialogStep("Okay.");

		createDisguise = new DetailedQuestStep(this, "Create a disguise by using the Kharidian Headpiece on the Fake Beard.", headPiece, fakeBeard);

		//To Pollnivneach
		goToShantay = new ObjectStep(this, ObjectID.SHANTAY_PASS_HENGE_DOORWAY, new WorldPoint(3304, 3116, 0), "Go through Shantay Pass.", shantayPass);
		buyShantayPass = new NpcStep(this, NpcID.SHANTAY, new WorldPoint(3303, 3122, 0), "Buy a shantay pass from Shantay.", unspecifiedCoins);

		talkToRugMerchant = new NpcStep(this, NpcID.MAGIC_CARPET_SELLER1, new WorldPoint(3311, 3109, 0),"Talk to the rug merchant and travel to Pollnivneach via magic carpet.", unspecifiedCoins);
		talkToRugMerchant.addDialogStep("Pollnivneach");


		//Drunken Ali
		drunkenAli = new NpcStep(this, NpcID.FEUD_DRUNKEN_ALI, new WorldPoint(3360, 2957, 0), "Buy 3 beers from the bartender and use them on Drunken Ali to get him to explain where his son is. Talk with him between beers", beer);
		drunkenAli.addIcon(ItemID.BEER);

		//Step 2
		//Find Beef
		talkToThug = new NpcStep(this, NpcID.FEUD_EGYPTIAN_DOORMAN_1, new WorldPoint(3347, 2955, 0), "Talk to a Menaphite Thug to figure out how their dispute started with the bandits.", true);
		talkToBandit = new NpcStep(this, NpcID.FEUD_ARABIAN_GUARD1_1, new WorldPoint(3362, 2993, 0),"Talk to a bandit to figure out their issues with the Menaphites.", true);

		//Step 3
		//Buy Camels
		talkToCamelman = new NpcStep(this, NpcID.FEUD_ALI_THE_DISCOUNT_CAMEL_SELLER, new WorldPoint(3350, 2966, 0), "Talk to Ali the Camel Man to try and get two camels to solve the dispute");
		talkToCamelman.addDialogStep("Are those camels around the side for sale?");
		talkToCamelman.addDialogStep("What price do you want for both of them?");
		talkToCamelman.addDialogStep("Would 500 gold coins for the pair of them do?");


		//Step 4
		//Return Camels
		talkToBanditReturnedCamel = new NpcStep(this, NpcID.FEUD_ARABIAN_GUARD1_1, new WorldPoint(3362, 2993, 0),"Tell the bandits that the Menaphites have agreed to return the camel.", true);
		talkToMenaphiteReturnedCamel = new NpcStep(this, NpcID.FEUD_EGYPTIAN_DOORMAN_1, new WorldPoint(3347, 2955, 0), "Tell the Menaphites that the bandits have agreed to return the camel.", true);

		//Step 5
		//Get First Job
		talkToAliTheOperator = new NpcStep(this, NpcID.FEUD_EGYPTIAN_MINDER, new WorldPoint(3332, 2948, 0),"Talk to Ali the Operator to get a job from him.");
		talkToAliTheOperator.addDialogStep("Yes, of course, those bandits should be taught a lesson.");

		//Step 6
		//Pickpocket Villager
		pickpocketVillager = new NpcStep(this, NpcID.FEUD_VILLAGER_1_1, new WorldPoint(3356, 2962, 0), "Pickpocket a villager.", true);
		pickpocketVillager.setHideWorldArrow(true);
		pickpocketVillager.addAlternateNpcs(NpcID.FEUD_VILLAGER_2_1, NpcID.FEUD_VILLAGER_3_1);

		//Step 7 -> 8
		//Pickpocket with Urchin
		pickPocketVillagerWithUrchin = new NpcStep(this, NpcID.FEUD_STREET_URCHIN, "Lure a villager then talk to a street urchin and get them to distract a villager, once he has them distracted pickpocket them from behind.", true);

		//Step 9 -> 11
		//Blackjack
		getBlackjackFromAli = new NpcStep(this, NpcID.FEUD_EGYPTIAN_MINDER, new WorldPoint(3332, 2948, 0), ("Talk to Ali the Operator to get a blackjack."));
		getBlackjackFromAli.addDialogStep("Yeah, I could do with a bit of advice.");

		equipBlackjack = new DetailedQuestStep(this, "Equip your oak blackjack in your inventory.", oakBlackjack);

		blackjackVillager = new NpcStep(this, NpcID.FEUD_VILLAGER_1_1, "Lure a villager to secluded place or an empty building, knock them out and then pickpocket them.", true);
		blackjackVillager.addAlternateNpcs(NpcID.FEUD_VILLAGER_2_1, NpcID.FEUD_VILLAGER_2_3, NpcID.FEUD_VILLAGER_3_1, NpcID.FEUD_VILLAGER_3_3);


		//Step 12
		//Get Second Job
		talkToAliToGetSecondJob = new NpcStep(this, NpcID.FEUD_EGYPTIAN_MINDER, new WorldPoint(3332, 2948, 0), "Talk to Ali the Operator to get the second job.");

		//Step 13
		//Hide Behind Cactus - Second Job
		hideBehindCactus = new ObjectStep(this, ObjectID.FEUD_CACTUS_ROW, new WorldPoint(3364, 2968, 0), "Hide behind the cactus", gloves.equipped(), disguiseEquipped);

		//Step 14
		//Open the Door
		openTheDoor = new ObjectStep(this, ObjectID.FEUD_CLOSED_DOOR_LEFT, "Open the door.", doorKeys);
		openTheDoor.addAlternateObjects(6240);
		openTheDoor.addIcon(ItemID.FEUD_MAYORS_HOUSE_KEYS);

		goUpStairs = new ObjectStep(this, ObjectID.FEUD_INSIDESTAIRS_BASE, "Go up the stairs.");

		crackTheSafe = new ObjectStep(this, ObjectID.FEUD_MAYORS_PICTURE, "Search the painting to reveal the safe. Enter the code 1, 1, 2, 3, 5, 8.");

		//Step 15
		//Return the Jewels
		goDownStairs = new ObjectStep(this, ObjectID.FEUD_INSIDESTAIRS_TOP, "Go down the stairs.");

		giveTheJewelsToAli = new NpcStep(this, NpcID.FEUD_EGYPTIAN_MINDER, new WorldPoint(3332, 2948, 0), "Give Ali the Operator the jewels to get your final task from him.");

		//Step 16
		//Find Traitor
		talkMenaphiteToFindTraitor =  new NpcStep(this, NpcID.FEUD_EGYPTIAN_DOORMAN_1, new WorldPoint(3347, 2955, 0),"Talk to a Menaphite member to find the traitor.", true);
		tellAliYouFoundTraitor = new NpcStep(this, NpcID.FEUD_EGYPTIAN_MINDER, new WorldPoint(3332, 2948, 0),"Talk to Ali and tell him you have found the traitor.");

		//Step 17
		//Get Snake & Talk to Barman
		talkToAliTheBarman = new NpcStep(this, NpcID.FEUD_ALI_THE_BARMAN, new WorldPoint(3361, 2956, 0), "Talk to Ali the Barman to find out where Traitorous Ali is.");
		talkToAliTheBarman.addDialogStep(("I'm looking for Traitorous Ali."));
		talkToAliTheBarman.addDialogStep("No thanks I'm ok.");

		talkToAliTheHag = new NpcStep(this, NpcID.FEUD_HAG, new WorldPoint(3345, 2986, 0), "Talk to Ali the Hag to ask her to make some poison for you.");

		giveCoinToSnakeCharmer = new ObjectStep(this, ObjectID.FEUD_MONEY_BOWL, new WorldPoint(3355, 2953, 0), "Use coins on the snake charmer's money pot to get a snake charm and a snake basket.", highlightedCoins);
		giveCoinToSnakeCharmer.addIcon(ItemID.COINS);

		catchSnake = new NpcStep(this, NpcID.FEUD_DESERT_SNAKE, new WorldPoint(3332, 2958, 0), "Use the Snake Charm on a snake to capture it.", true, snakeCharmHighlighted, snakeBasket);
		catchSnake.addIcon(ItemID.SNAKE_FLUTE);

		giveSnakeToHag = new NpcStep(this, NpcID.FEUD_HAG, new WorldPoint(3345, 2986, 0), "Give the snake to Ali the Hag.", snakeBasketFull);

		//Step 18
		//Camel Dung - Get Dung
		talkToAliTheKebabSalesman = new NpcStep(this, NpcID.FEUD_KEBABMAN, new WorldPoint(3352, 2975, 0), "Talk to Ali the Kebab Salesman to get a special sauce to use.");
		talkToAliTheKebabSalesman.addDialogStep("Would you sell me that bottle of special kebab sauce?");
		talkToAliTheKebabSalesman.addDialogStep("No thanks, I'm good.");

		getBucket = new DetailedQuestStep(this, new WorldPoint(3346, 2966, 0), "Pick up a bucket to grab the dung");

		pickupDung = new ObjectStep(this, ObjectID.FEUD_UGTHANKI_POOH, "Use the bucket on the dung.", bucket.highlighted());
		pickupDung.addIcon(ItemID.BUCKET_EMPTY);

		getDung = new ObjectStep(this, ObjectID.FEUD_FOODTROUGH2, new WorldPoint(3343, 2960, 0), "Use the Red Hot Sauce on the Trough and wait for a Camel to poop out the dung.\n Only pickup brown/Ugthanki dung, if you plan to do \"My Arm's Big Adventure\" or \"Forgettable Tale of a Drunken Dwarf\" then you may want to grab four more Ugthanki dung.", redHotSauce);
		getDung.addIcon(ItemID.SUPERHOT_KEBAB_SAUCE);

		givenDungToHag = new NpcStep(this, NpcID.FEUD_HAG, new WorldPoint(3345, 2986, 0), "Talk to Ali the Hag and give her the dung.", dung);

		//Step 22
		//Poison The Drink
		poisonTheDrink = new ObjectStep(this, ObjectID.FEUD_POISON_BEER_TABLE, new WorldPoint(3356, 2957, 0), "Use the poison on the beer glass to kill Traitorous Ali.", poisonHighlighted);
		poisonTheDrink.addIcon(ItemID.POISON_FROM_HAG);

		//Step 23
		//Tell Ali The Operator Poisoned
		tellAliOperatorPoisoned = new NpcStep(this, NpcID.FEUD_EGYPTIAN_MINDER, new WorldPoint(3332, 2948, 0), "Talk to Ali the Operator and tell him he is dead.");

		killMenaphiteThug = new DetailedQuestStep(this, "Kill the Menaphite Thug. You can safespot him inside the tent by using a chair, if he's not spawned then talk to the Menaphite Leader again.");

		//Step 24
		//Kill Thug
		talkToMenaphiteLeader = new NpcStep(this, NpcID.FEUD_MENAP_BOSS_VIS, "Talk to the Menaphite Leader and prepare for a fight against a tough guy. You can safespot him inside the tent by using a chair.");
		talkToMenaphiteLeader.addSubSteps(killMenaphiteThug);

		//Step 25
		//Kill Champion - Talk to Villager
		talkToAVillager = new NpcStep(this, NpcID.FEUD_VILLAGER_1_1, "Talk to a villager and they will be angry you broke the balance of power.", true);
		talkToAVillager.addAlternateNpcs(NpcID.FEUD_VILLAGER_1_3, NpcID.FEUD_VILLAGER_2_1, NpcID.FEUD_VILLAGER_2_3, NpcID.FEUD_VILLAGER_3_1, NpcID.FEUD_VILLAGER_3_3);

		talkToBanditLeader = new NpcStep(this, NpcID.FEUD_BANDIT_BOSS_VIS, new WorldPoint(3353, 3002, 0), "Talk to the Bandit Leader and be prepared to fight a Bandit Champion. You can safe spot him with the stool.");
		talkToBanditLeader.addSubSteps(killBanditChampion);

		killBanditChampion = new DetailedQuestStep(this, "Kill the Bandit Champion, you can safe spot him with the stool. \n If he's not spawned then talk to the Bandit Leader again.");

		//Step 26
		//Spawn Mayor
		talkToAVillagerToSpawnMayor  = new NpcStep(this, NpcID.FEUD_VILLAGER_1_1, "Talk to a villager and they will be angry still.", true);
		talkToAVillagerToSpawnMayor.addAlternateNpcs(NpcID.FEUD_VILLAGER_1_3, NpcID.FEUD_VILLAGER_2_1, NpcID.FEUD_VILLAGER_2_3, NpcID.FEUD_VILLAGER_3_1, NpcID.FEUD_VILLAGER_3_3);

		talkToMayor = new NpcStep(this, NpcID.FEUD_MAYOR_GEOM, new WorldPoint(3360, 2972, 0), "Talk to Ali the Mayor and get your due congratulations.");

		//Step 27
		//Finish Quest
		finishQuest = new NpcStep(this, NpcID.FEUD_ALI_M, new WorldPoint(3304, 3211, 0), "Talk to  Ali Morrisane in Al Kharid to finish the quest.");
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
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ringOfDueling, pollnivneachTeleport);
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
				new ItemReward("Coins", ItemID.COINS, 500),
				new ItemReward("Oak Blackjack", ItemID.BLACKJACK_OAK, 1),
				new ItemReward("Desert Disguise", ItemID.FEUD_DESERT_DISGUISE, 1),
				new ItemReward("Willow Blackjack", ItemID.BLACKJACK_WILLOW, 1),
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
