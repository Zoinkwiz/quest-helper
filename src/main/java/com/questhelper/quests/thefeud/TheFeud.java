/*
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.quests.thefeud;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_FEUD
)
public class TheFeud extends BasicQuestHelper
{
	private ItemRequirement coins, unspecifiedCoins, gloves, headPiece, fakeBeard, desertDisguise,
		shantyPass, beer, oakBlackjack, glovesEquipped, disguiseEquipped, doorKeys,
		highlightedCoins, snakeCharmHighlighted, snakeBasket, snakeBasketFull,
		redHotSauce, dung, poisonHighlighted, combatGear;
	private Conditions hasDisguise, hasDisguiseComponents, doesNotHaveDisguise, doesNotHaveDisguiseComponents,
		notThroughShantyGate;
	private QuestStep startQuest, buyDisguiseGear, createDisguise, goToPollniveachStep;
	private ObjectStep hideBehindCactus, openTheDoor, goUpStairs, crackTheSafe,
		giveCoinToSnakeCharmer, getDung, poisonTheDrink;
	private NpcStep drunkenAli, talkToThug, talkToBandit, talkToCamelman,
		talkToBanditReturnedCamel, talkToMenaphiteReturnedCamel, talkToAliTheOperator,
		pickpocketVillager, pickPocketVillagerWithUrchin, getBlackjackFromAli, blackJackVillager,
		talkToAliToGetSecondJob, giveTheJewelsToAli, talkMenaphiteToFindTraitor, tellAliYouFoundTraitor,
		talkToAliTheBarman, talkToAliTheHag, catchSnake, givePoisonToAliTheHag,
		talkToAliTheKebabSalesman, givenDungToHag, tellAliOperatorPoisoned,
		talkToMenaphiteLeader, talkToAVillager, talkToBanditLeader, talkToAVillagerToSpawnMayor,
		talkToMayor, finishQuest;
	private ItemRequirementCondition desertDisguiseCondition, hasShantyPass, hasOakBlackjack, oakBlackjackEquipped,
		snakeCharm, hasSnakeBasket, hasSnakeBasketFull, hasRedHotSauce, hasBucket, doesNotHaveBucket,
		hasDungInInventory;
	private ConditionForStep thoughShantyGate;
	private ZoneCondition shantyPassZoneCondition, pollniveachZoneCondition, secondFloorMansion;
	private DetailedQuestStep getBucket;
	private VarbitCondition talkedToThug, talkedToBandit, talkedToBanditReturn, doorOpen, traitorFound,
		talkedToBarman, talkedToAliTheHag, givenPoisonToHag, menaphiteThugAlive, talkedToVillagerAboutMenaphite,
		banditChampionSpawned, mayorSpawned;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupVarbits();
		setupItemRequirements();
		setupZones();
		setupConditions();

		return getSteps();
	}

	private Map<Integer, QuestStep> getSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, startQuest = getStartQuest());
		steps.put(1, getPollnivneachStep());
		steps.put(2, findBeef());
		steps.put(3, buyCamels());
		steps.put(4, talkToGangsAgain());
		steps.put(5, getFirstJob());
		steps.put(6, pickPocketVillager());
		QuestStep pickPocketWithUrchin = pickPocketVillagerWithUrchin();
		steps.put(7, pickPocketWithUrchin);
		steps.put(8, pickPocketWithUrchin);
		QuestStep blackjack = blackjackVillager();
		steps.put(9, blackjack);
		steps.put(10, blackjack);
		steps.put(11, blackjack);
		steps.put(12, getSecondJob());
		steps.put(13, secondJob());
		steps.put(14, openTheDoor());
		steps.put(15, returnTheJewels());
		steps.put(16, findTraitor());
		steps.put(17, talkToBarman());
		steps.put(18, getDung());
		steps.put(22, poisonTheDrink());
		steps.put(23, tellOperatorPoisoned());
		steps.put(24, killToughGuy());
		steps.put(25, talkToVillager());
		steps.put(26, talkToVillagerToSpawnMayor());
		steps.put(27, finishQuest());
		return steps;
	}

	private void setupVarbits()
	{
		//318 drunk ali beer count

		// 315 -> 2 Talked to thug -> 3 Talked to bandit
		talkedToThug = new VarbitCondition(315, 2);
		talkedToBandit = new VarbitCondition(315, 3);

		talkedToBanditReturn = new VarbitCondition(316, true,0); // Might have missed?
		// 340 -> 1 when pickpocket villager
		doorOpen = new VarbitCondition(320, 1);

		//Varbit 325 keeps track of correctly entered safe values

		// Varbit 342 when found Traitor
		traitorFound = new VarbitCondition(342, 1);

		// Varbit 321 when talked to bar man
		talkedToBarman = new VarbitCondition(321, 1);

		// 345 and 328 when talking to hag
		talkedToAliTheHag = new VarbitCondition(328, 1);
		givenPoisonToHag = new VarbitCondition(328, 2);
		// 335 = POisoned drink

		//322 Menaphite
		menaphiteThugAlive = new VarbitCondition(322, 1);

		// Talked to villager about Menaphite 343, 338
		talkedToVillagerAboutMenaphite = new VarbitCondition(343, 1);
		banditChampionSpawned = new VarbitCondition(323, 1);

		// 343 -> Mayor spawned
		mayorSpawned = new VarbitCondition(343, 2);
	}

	private void setupZones()
	{
		Zone shantyPassZone = new Zone(new WorldPoint(3297, 3116, 0), new WorldPoint(3313, 3132, 0));
		Zone pollniveachZone = new Zone(new WorldPoint(3320, 2926, 0), new WorldPoint(3381, 3006, 0));
		Zone secondFloor = new Zone(new WorldPoint(3366, 2965, 1), new WorldPoint(3375, 2979, 1));

		shantyPassZoneCondition = new ZoneCondition(shantyPassZone);
		pollniveachZoneCondition = new ZoneCondition(pollniveachZone);
		secondFloorMansion = new ZoneCondition(secondFloor);
	}

	private QuestStep finishQuest()
	{
		finishQuest = talkToAliStep("Talk to Ali Morrisane to finish the quest.");
		return finishQuest;
	}

	private QuestStep talkToVillagerToSpawnMayor()
	{
		talkToAVillagerToSpawnMayor = new NpcStep(this, NpcID.VILLAGER, "Talk to a villager and they will be angry still.", true);
		talkToAVillagerToSpawnMayor.addAlternateNpcs(NpcID.VILLAGER_3554, NpcID.VILLAGER_3555, NpcID.VILLAGER_3557, NpcID.VILLAGER_3558, NpcID.VILLAGER_3560);
		talkToMayor = new NpcStep(this, NpcID.ALI_THE_MAYOR, new WorldPoint(3360, 2972, 0), "Talk to the mayor and get your due congratulations.");

		ConditionalStep conditionalStep = new ConditionalStep(this, talkToAVillagerToSpawnMayor);
		conditionalStep.addStep(mayorSpawned, talkToMayor);
		return conditionalStep;
	}

	private QuestStep talkToVillager()
	{
		talkToAVillager = new NpcStep(this, NpcID.VILLAGER, "Talk to a villager and they will be angry you broke the power of balance.", true);
		talkToAVillager.addAlternateNpcs(NpcID.VILLAGER_3554, NpcID.VILLAGER_3555, NpcID.VILLAGER_3557, NpcID.VILLAGER_3558, NpcID.VILLAGER_3560);

		talkToBanditLeader = new NpcStep(this, NpcID.BANDIT_LEADER, new WorldPoint(3353, 3002, 0),
			"Talk to the bandit leader and be prepared to the fight a bandit champion. You can safe spot him with the stool.");

		DetailedQuestStep killBanditChampion = new DetailedQuestStep(this, "Kill the bandit champion spawned, you can safe spot him with the stool. \n If he's not spawned then talk to the Bandit Leader again.");
		ConditionalStep conditionalStep = new ConditionalStep(this, talkToAVillager);
		conditionalStep.addStep(banditChampionSpawned, killBanditChampion);
		conditionalStep.addStep(talkedToVillagerAboutMenaphite, talkToBanditLeader);
		talkToBanditLeader.addSubSteps(killBanditChampion);

		return conditionalStep;
	}

	private QuestStep killToughGuy()
	{
		talkToMenaphiteLeader = new NpcStep(this, NpcID.MENAPHITE_LEADER, "Talk to the Menaphite leader and prepare for a fight against a tough guy. You can safespot him inside the tent by using a chair.");
		DetailedQuestStep killMenaphiteThug = new DetailedQuestStep(this, "Kill the Menaphite thug.  You can safespot him inside the tent by using a chair, if he's not spawned then talk to the Menaphite leader again.");
		ConditionalStep conditionalStep = new ConditionalStep(this, talkToMenaphiteLeader);
		conditionalStep.addStep(menaphiteThugAlive, killMenaphiteThug);
		talkToMenaphiteLeader.addSubSteps(killMenaphiteThug);

		return conditionalStep;
	}

	private QuestStep tellOperatorPoisoned()
	{
		tellAliOperatorPoisoned = talkToAliTheOperatorStep("Talk to Ali the Operator and tell him he is dead.");
		return tellAliOperatorPoisoned;
	}

	private QuestStep poisonTheDrink()
	{
		poisonTheDrink = new ObjectStep(this, ObjectID.TABLE_6246, new WorldPoint(3356, 2957, 0), "Use the poison on the beer glass to kill Traiterous Ali.", poisonHighlighted);
		poisonTheDrink.addIcon(ItemID.HAGS_POISON);
		return poisonTheDrink;
	}

	private QuestStep getDung()
	{
		talkToAliTheKebabSalesman = new NpcStep(this, NpcID.ALI_THE_KEBAB_SELLER, new WorldPoint(3352, 2975, 0),
			"Talk to Ali the Kebab Salesman to get a special sauce to use.");
		talkToAliTheKebabSalesman.addDialogStep("Would you sell me that bottle of special kebab sauce?");
		talkToAliTheKebabSalesman.addDialogStep("No thanks, I'm good.");

		getBucket = new DetailedQuestStep(this, new WorldPoint(3346, 2966, 0), "Pick up a bucket to grab the dung with.");
		getDung = new ObjectStep(this, ObjectID.TROUGH_6256, new WorldPoint(3343, 2960, 0), "Use the Red Hot Sauce on the Trough and wait for a Camel to poop out the dung.\n Only pickup brown/Ugthanki dung, if you plan to do \"My Arm's Big Adventure\" or \"Forgettable Tale of a Drunken Dwarf\" then you may want to grab four more Ugthanki dung.",
			redHotSauce);
		getDung.addSubSteps(getBucket);
		getDung.addIcon(ItemID.RED_HOT_SAUCE);
		givenDungToHag = talkToAliTheHagStep("Talk to Ali the Hag and give her your dung.", dung);

		ConditionalStep conditionalStep = new ConditionalStep(this, talkToAliTheKebabSalesman);

		conditionalStep.addStep(new Conditions(givenPoisonToHag, hasDungInInventory), givenDungToHag);
		conditionalStep.addStep(new Conditions(givenPoisonToHag, hasRedHotSauce, hasBucket), getDung);
		conditionalStep.addStep(new Conditions(givenPoisonToHag, hasRedHotSauce, doesNotHaveBucket), getBucket);
		return conditionalStep;
	}

	private QuestStep talkToBarman()
	{
		talkToAliTheBarman = new NpcStep(this, NpcID.ALI_THE_BARMAN, new WorldPoint(3361, 2956, 0),
			"Talk to Ali the barman to find out where Traiterous Ali is.");
		talkToAliTheBarman.addDialogStep("I'm looking for Traitorous Ali.");
		talkToAliTheBarman.addDialogStep("No thanks I'm ok.");
		talkToAliTheHag = talkToAliTheHagStep("Talk to Ali the Hag to ask her to make some poison for you.");
		giveCoinToSnakeCharmer = new ObjectStep(this, ObjectID.MONEY_POT, new WorldPoint(3355, 2953, 0),
			"Use money on the money pot to attract the snake charmers attention.", highlightedCoins);
		giveCoinToSnakeCharmer.addIcon(ItemID.COINS);
		catchSnake = new NpcStep(this, NpcID.DESERT_SNAKE, new WorldPoint(3332, 2958, 0),
			"Use the Snake Charm on a snake to capture it.", true,
			snakeCharmHighlighted, snakeBasket);
		catchSnake.addIcon(ItemID.SNAKE_CHARM);

		givePoisonToAliTheHag = talkToAliTheHagStep("Give the snake to Ali the Hag.", snakeBasketFull);
		ConditionalStep conditionalStep = new ConditionalStep(this, talkToAliTheBarman);

		conditionalStep.addStep(givenPoisonToHag, talkToAliTheKebabSalesman);
		conditionalStep.addStep(hasSnakeBasketFull, givePoisonToAliTheHag);
		conditionalStep.addStep(new Conditions(talkedToAliTheHag, snakeCharm, hasSnakeBasket), catchSnake);
		conditionalStep.addStep(talkedToAliTheHag, giveCoinToSnakeCharmer);
		conditionalStep.addStep(talkedToBarman, talkToAliTheHag);
		return conditionalStep;
	}

	private QuestStep findTraitor()
	{
		talkMenaphiteToFindTraitor = talkToMenaphiteStep("Talk to a Menaphite member to find the traitor.");
		tellAliYouFoundTraitor = talkToAliTheOperatorStep("Talk to Ali and tell him you have found the traitor.");
		ConditionalStep conditionalStep = new ConditionalStep(this, talkMenaphiteToFindTraitor);
		conditionalStep.addStep(traitorFound, tellAliYouFoundTraitor);
		return conditionalStep;
	}

	private QuestStep returnTheJewels()
	{
		ObjectStep goDownStairs = new ObjectStep(this, ObjectID.STAIRCASE_6245, "Go down the stairs.");
		giveTheJewelsToAli = talkToAliTheOperatorStep("Give Ali the Operator the jewels to get your final task from him.");
		giveTheJewelsToAli.addSubSteps(goDownStairs);
		ConditionalStep conditionalStep = new ConditionalStep(this, giveTheJewelsToAli);
		conditionalStep.addStep(secondFloorMansion, goDownStairs);
		return conditionalStep;
	}

	private QuestStep openTheDoor()
	{

		openTheDoor = new ObjectStep(this, 6238, "Open the door.", doorKeys);
		openTheDoor.addAlternateObjects(6240);
		openTheDoor.addIcon(ItemID.KEYS);

		goUpStairs = new ObjectStep(this, ObjectID.STAIRCASE_6244, "Go up the stairs.");
		crackTheSafe = new ObjectStep(this, 6276, "Search the painting to reveal the safe. Enter the code 1, 1, 2, 3, 5, 8.");

		crackTheSafe.addSubSteps(goUpStairs);
		ConditionalStep stealStep = new ConditionalStep(this, openTheDoor);
		stealStep.addStep(secondFloorMansion, crackTheSafe);
		stealStep.addStep(doorOpen, goUpStairs);
		return stealStep;
	}

	private QuestStep getSecondJob()
	{
		talkToAliToGetSecondJob = talkToAliTheOperatorStep("Talk to Ali the Operator to get the second job.");
		return talkToAliToGetSecondJob;
	}

	private QuestStep secondJob()
	{
		hideBehindCactus = new ObjectStep(this, ObjectID.CACTUS_6277, new WorldPoint(3364, 2968, 0)
			, "Hide behind the cactus", glovesEquipped, disguiseEquipped);
		ConditionalStep secondJob = new ConditionalStep(this, hideBehindCactus);
		return secondJob;
	}

	private QuestStep blackjackVillager()
	{
		getBlackjackFromAli = talkToAliTheOperatorStep("Talk to Ali the Operator to get a blackjack.");
		getBlackjackFromAli.addDialogStep("Yeah, I could do with a bit of advice.");

		blackJackVillager = new NpcStep(this, NpcID.VILLAGER, "Lure a villager to secluded place, knock them out and then pickpocket them.", true);
		blackJackVillager.addAlternateNpcs(NpcID.VILLAGER_3555, NpcID.VILLAGER_3557, NpcID.VILLAGER_3558, NpcID.VILLAGER_3560);

		DetailedQuestStep equipBlackjack = new DetailedQuestStep(this, "Equip your oak blackjack in your inventory.", oakBlackjack);

		ConditionalStep blackjackVillagerStep = new ConditionalStep(this, getBlackjackFromAli);
		blackjackVillagerStep.addStep(oakBlackjackEquipped, blackJackVillager);
		blackjackVillagerStep.addStep(hasOakBlackjack, equipBlackjack);
		blackJackVillager.addSubSteps(equipBlackjack);

		return blackjackVillagerStep;
	}

	private QuestStep pickPocketVillagerWithUrchin()
	{
		pickPocketVillagerWithUrchin = new NpcStep(this, NpcID.STREET_URCHIN, "Talk to a street urchin and get them to distract a villager, once he has them distracted pickpocket them from behind.", true);
		return pickPocketVillagerWithUrchin;
	}

	private QuestStep pickPocketVillager()
	{
		pickpocketVillager = new NpcStep(this, NpcID.VILLAGER, new WorldPoint(3356, 2962, 0), "Pickpocket a villager.", true);
		pickpocketVillager.setHideWorldArrow(true);
		pickpocketVillager.addAlternateNpcs(NpcID.VILLAGER_3555, NpcID.VILLAGER_3558);

		return pickpocketVillager;
	}

	private QuestStep getFirstJob()
	{
		talkToAliTheOperator = talkToAliTheOperatorStep("Talk to Ali the Operator to get a job from him.");
		talkToAliTheOperator.addDialogStep("Yes, of course, those bandits should be taught a lesson.");
		return talkToAliTheOperator;
	}

	private QuestStep talkToGangsAgain()
	{
		talkToBanditReturnedCamel = talkToBanditStep("Tell the bandits that the Menaphites have agreed to return the camel.");
		talkToMenaphiteReturnedCamel = talkToMenaphiteStep("Tell the Menaphites the bandits have agreed to return the camel.");
		ConditionalStep firstJob = new ConditionalStep(this, talkToBanditReturnedCamel);
		firstJob.addStep(talkedToBanditReturn, talkToMenaphiteReturnedCamel);
		return firstJob;
	}

	private void setupConditions()
	{
		ItemRequirementCondition fakeBeardCondition = new ItemRequirementCondition(fakeBeard);
		ItemRequirementCondition headPieceCondition = new ItemRequirementCondition(headPiece);
		desertDisguiseCondition = new ItemRequirementCondition(desertDisguise);
		doesNotHaveDisguise = new Conditions(LogicType.NAND, desertDisguiseCondition);
		hasSnakeBasketFull = new ItemRequirementCondition(snakeBasketFull);
		hasSnakeBasket = new ItemRequirementCondition(snakeBasket);
		snakeCharm = new ItemRequirementCondition(snakeCharmHighlighted);
		hasShantyPass = new ItemRequirementCondition(shantyPass);
		hasOakBlackjack = new ItemRequirementCondition(oakBlackjack);
		oakBlackjackEquipped = new ItemRequirementCondition(new ItemRequirement("Oak Blackjack", ItemID.OAK_BLACKJACK, 1, true));
		hasRedHotSauce = new ItemRequirementCondition(redHotSauce);
		hasDisguiseComponents = new Conditions(fakeBeardCondition, headPieceCondition);
		doesNotHaveDisguiseComponents = new Conditions(LogicType.NAND, fakeBeardCondition,
			headPieceCondition);
		hasDisguise = new Conditions(desertDisguiseCondition);
		hasBucket = new ItemRequirementCondition(new ItemRequirement("Bucket", ItemID.BUCKET));
		doesNotHaveBucket = new ItemRequirementCondition(Operation.LESS_EQUAL, 0, new ItemRequirement("Bucket", ItemID.BUCKET));
		thoughShantyGate = new ConditionForStep()
		{
			@Override
			public boolean checkCondition(Client client)
			{
				return client.getLocalPlayer().getWorldLocation().getY() < 3116;
			}
		};
		notThroughShantyGate = new Conditions(LogicType.NAND, thoughShantyGate);
		hasDungInInventory = new ItemRequirementCondition(dung);
		combatGear = new ItemRequirement("Combat gear to fight NPCs", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	private void setupItemRequirements()
	{
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 800);
		unspecifiedCoins = new ItemRequirement("Coins", ItemID.COINS_995, -1);
		gloves = new ItemRequirement("Gloves", ItemID.LEATHER_GLOVES);
		headPiece = new ItemRequirement("Kharidian Headpiece", ItemID.KHARIDIAN_HEADPIECE);
		headPiece.setHighlightInInventory(true);
		fakeBeard = new ItemRequirement("Fake Beard", ItemID.FAKE_BEARD);
		fakeBeard.setHighlightInInventory(true);
		shantyPass = new ItemRequirement("Shantay Pass", ItemID.SHANTAY_PASS);
		desertDisguise = new ItemRequirement("Desert Disguise", ItemID.DESERT_DISGUISE);
		beer = new ItemRequirement("Beer", ItemID.BEER);
		beer.setHighlightInInventory(true);
		doorKeys = new ItemRequirement("Keys", ItemID.KEYS);
		doorKeys.setHighlightInInventory(true);
		highlightedCoins = new ItemRequirement("Coins", ItemID.COINS_995);
		highlightedCoins.setHighlightInInventory(true);
		snakeCharmHighlighted = new ItemRequirement("Snake Charm", ItemID.SNAKE_CHARM);
		snakeCharmHighlighted.setHighlightInInventory(true);
		glovesEquipped = new ItemRequirement("Gloves", ItemID.LEATHER_GLOVES, 1, true);
		disguiseEquipped = new ItemRequirement("Disguise", ItemID.DESERT_DISGUISE, 1, true);
		oakBlackjack = new ItemRequirement("Oak Blackjack", ItemID.OAK_BLACKJACK);
		oakBlackjack.setHighlightInInventory(true);
		snakeBasket = new ItemRequirement("Snake Basket", ItemID.SNAKE_BASKET);
		snakeBasketFull = new ItemRequirement("Snake Basket(Full)", ItemID.SNAKE_BASKET_FULL);
		redHotSauce = new ItemRequirement("Red Hot Sauce", ItemID.RED_HOT_SAUCE);
		redHotSauce.setHighlightInInventory(true);
		dung = new ItemRequirement("Dung", ItemID.UGTHANKI_DUNG);
		poisonHighlighted = new ItemRequirement("Hag's Poison", ItemID.HAGS_POISON);
		poisonHighlighted.setHighlightInInventory(true);
	}

	private QuestStep buyCamels()
	{
		talkToCamelman = new NpcStep(this, NpcID.ALI_THE_CAMEL_MAN, new WorldPoint(3350, 2966, 0),
			"Talk to ali the camel man to try and get two camels to solve the dispute");
		talkToCamelman.addDialogStep("Are those camels around the side for sale?");
		talkToCamelman.addDialogStep("What price do you want for both of them?");
		talkToCamelman.addDialogStep("Would 500 gold coins for the pair of them do?");
		return talkToCamelman;
	}

	private QuestStep findBeef()
	{
		talkToThug = talkToMenaphiteStep("Talk to a Menaphite Thug to figure out how their dispute started with the bandits.");
		talkToBandit = talkToBanditStep("Talk to a bandit to figure out their issues with the Menaphites.");

		ConditionalStep firstJobStep = new ConditionalStep(this, talkToThug);
		firstJobStep.addStep(talkedToBandit, talkToCamelman);
		firstJobStep.addStep(talkedToThug, talkToBandit);

		return firstJobStep;
	}

	private QuestStep getPollnivneachStep()
	{
		QuestStep goToShanty = new ObjectStep(this, ObjectID.SHANTAY_PASS, new WorldPoint(3304, 3116, 0), "Go through Shanty Pass.", shantyPass);
		buyDisguiseGear = talkToAliStep("Buy a Kharidian Headpiece and a Fake Beard to create a disguise.", unspecifiedCoins);
		NpcStep buyShantypass = new NpcStep(this, NpcID.SHANTAY, new WorldPoint(3303, 3122, 0),
			"Buy a shanty pass from Shantay.", unspecifiedCoins);
		buyDisguiseGear.addDialogStep("Okay.");
		createDisguise = new DetailedQuestStep(this, "Create a disguise by using the Kharidian Headpiece on the Fake Beard. This is used later in the quest.",
			headPiece, fakeBeard);

		goToPollniveachStep = new DetailedQuestStep(this, "Go through shanty pass and travel to Pollnivneach.");
		NpcStep talkToRugMerchant = new NpcStep(this, NpcID.RUG_MERCHANT, new WorldPoint(3311, 3109, 0),
			"Talk to the rug merchant and travel to Pollnivneach via magic carpet.", unspecifiedCoins);
		talkToRugMerchant.addDialogStep("Pollnivneach");

		drunkenAli = new NpcStep(this, NpcID.DRUNKEN_ALI, new WorldPoint(3360, 2957, 0),
			"Buy 3 beers from the bartender and use them on Drunken Ali to get him to explain where his son is.",
			beer);
		drunkenAli.addIcon(ItemID.BEER);
		goToPollniveachStep.addSubSteps(buyShantypass);
		goToPollniveachStep.addSubSteps(goToShanty);
		goToPollniveachStep.addSubSteps(talkToRugMerchant);

		ConditionalStep conditionalStep = new ConditionalStep(this, buyShantypass);
		conditionalStep.addStep(pollniveachZoneCondition, drunkenAli);
		conditionalStep.addStep(thoughShantyGate, talkToRugMerchant);
		conditionalStep.addStep(new Conditions(notThroughShantyGate, hasShantyPass), goToShanty);
		conditionalStep.addStep(new Conditions(notThroughShantyGate, doesNotHaveDisguise, doesNotHaveDisguiseComponents), buyDisguiseGear);
		conditionalStep.addStep(new Conditions(notThroughShantyGate, doesNotHaveDisguise, hasDisguiseComponents), createDisguise);

		return conditionalStep;
	}

	private QuestStep getStartQuest()
	{
		NpcStep talkToAli = talkToAliStep("Talk to Ali Morrisane to start the quest.");
		talkToAli.addDialogStep("If you are, then why are you still selling goods from a stall?");
		talkToAli.addDialogStep("I'd like to help you but.....");
		talkToAli.addDialogStep("I'll find you your help.");
		return talkToAli;
	}

	private NpcStep talkToAliTheHagStep(String text, ItemRequirement... itemRequirements)
	{
		return new NpcStep(this, NpcID.ALI_THE_HAG, new WorldPoint(3345, 2986, 0), text, itemRequirements);
	}

	private NpcStep talkToAliTheOperatorStep(String text)
	{
		return new NpcStep(this, NpcID.ALI_THE_OPERATOR, new WorldPoint(3332, 2948, 0), text);
	}

	private NpcStep talkToMenaphiteStep(String text)
	{
		NpcStep step = new NpcStep(this, NpcID.MENAPHITE_THUG, new WorldPoint(3347, 2955, 0), text, true);
		step.addAlternateNpcs(NpcID.MENAPHITE_THUG_3550);
		return step;
	}

	private NpcStep talkToBanditStep(String text)
	{
		return new NpcStep(this, NpcID.BANDIT_734, new WorldPoint(3362, 2993, 0), text, true);
	}

	private NpcStep talkToAliStep(String text, ItemRequirement... requirements)
	{
		return new NpcStep(this, NpcID.ALI_MORRISANE, new WorldPoint(3304, 3211, 0), text, requirements);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(coins, gloves));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Bandit champion (level 70) - Safespottable", "Tough Guy (level 75) - Safespottable"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting out",
			new ArrayList<>(Arrays.asList(startQuest)),
			unspecifiedCoins);
		steps.add(startingPanel);

		PanelDetails pollniveachPanel = new PanelDetails("Pollnivneach",
			new ArrayList<>(Arrays.asList(buyDisguiseGear, createDisguise, goToPollniveachStep, drunkenAli)),
			unspecifiedCoins);
		steps.add(pollniveachPanel);

		PanelDetails findTheBeefJobPanel = new PanelDetails("Find the beef",
			new ArrayList<>(Arrays.asList(talkToThug, talkToBandit, talkToCamelman, talkToBanditReturnedCamel, talkToMenaphiteReturnedCamel)),
			unspecifiedCoins);
		steps.add(findTheBeefJobPanel);

		PanelDetails firstJobPanel = new PanelDetails("First job",
			new ArrayList<>(Arrays.asList(talkToAliTheOperator, pickpocketVillager, pickPocketVillagerWithUrchin,
				getBlackjackFromAli, blackJackVillager)),
			unspecifiedCoins);
		steps.add(firstJobPanel);

		PanelDetails secondJobPanel = new PanelDetails("Second job",
			new ArrayList<>(Arrays.asList(talkToAliToGetSecondJob, hideBehindCactus, openTheDoor, goUpStairs,
				crackTheSafe, giveTheJewelsToAli)),
			desertDisguise, gloves);
		steps.add(secondJobPanel);

		PanelDetails thirdJobPanel = new PanelDetails("Third job",
			new ArrayList<>(Arrays.asList(talkMenaphiteToFindTraitor, tellAliYouFoundTraitor, talkToAliTheBarman,
				talkToAliTheHag, giveCoinToSnakeCharmer, catchSnake, givePoisonToAliTheHag, talkToAliTheKebabSalesman, getDung,
				givenDungToHag, tellAliOperatorPoisoned)), unspecifiedCoins);
		steps.add(thirdJobPanel);

		PanelDetails finishingUpPanel = new PanelDetails("Finishing up",
			new ArrayList<>(Arrays.asList(talkToMenaphiteLeader, talkToAVillager, talkToBanditLeader,
				talkToAVillagerToSpawnMayor, talkToMayor, finishQuest)), combatGear
		);
		steps.add(finishingUpPanel);

		return steps;
	}
}
