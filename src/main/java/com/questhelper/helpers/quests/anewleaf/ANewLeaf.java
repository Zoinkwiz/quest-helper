/*
 * Copyright (c) 2026, eindiran <https://github.com/eindiran>
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
package com.questhelper.helpers.quests.anewleaf;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.SimpleRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.var.VarbitRequirement;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

/**
 * VarbitID.ANL does not change while the stew is being made and delivered. That task is tracked by
 * VarbitID.ANL_STEW_STATE (5 asked, 10 cooked, 15 reported to Dennis) and can be done at any stage from 15 to 35.
 */
public class ANewLeaf extends BasicQuestHelper
{
	// RuneLite has no ObjectID constants for the cooking pot or the Farming Guild patches yet
	private static final int COOKING_POT = 63342;
	private static final int GUILD_SOUTH_ALLOTMENT = 33693;
	private static final int GUILD_NORTH_ALLOTMENT = 33694;
	private static final int GUILD_FLOWER_PATCH = 33649;

	private static final String DISCUSS_TASKS = "Can we discuss the tasks you have given me?";
	private static final String TOPIC_POTION = "Can we talk about communicating with Brassica Prime?";
	private static final String TOPIC_STEW = "Can we talk about making and distributing stew?";
	private static final String TOPIC_SPIES = "Can we talk about sprout spies?";

	// Required items
	ItemRequirement spade;
	ItemRequirement vialOfWater;
	ItemRequirement bowls;
	ItemRequirement cabbage;
	ItemRequirement compost;

	// Recommended items
	ItemRequirement rake;
	ItemRequirement seedDibber;
	ItemRequirement combatGear;
	ItemRequirement food;

	// Mid-quest item requirements
	ItemRequirement communicationCabbage;
	ItemRequirement cabbageSeed;
	ItemRequirement potatoSeed;
	ItemRequirement rosemarySeed;
	ItemRequirement prayerToBrassica;
	ItemRequirement perfectCabbage;
	ItemRequirement perfectPotato;
	ItemRequirement perfectRosemary;
	ItemRequirement perfectStew;
	ItemRequirement cabbageWater;
	ItemRequirement blessedCompost;
	ItemRequirement sentiencePotion;
	ItemRequirement sproutSpy;

	// Miscellaneous requirements
	VarbitRequirement stewNotAsked;
	VarbitRequirement stewAsked;
	VarbitRequirement stewCooked;
	VarbitRequirement stewDone;
	VarbitRequirement toldWhoToDeliverTo;
	VarbitRequirement stewGivenToJane;
	VarbitRequirement stewGivenToSergeant;
	VarbitRequirement stewGivenToMary;
	VarbitRequirement stewGivenToKonar;
	Requirement needsMoreStew;
	VarbitRequirement toldToGetBook;
	VarbitRequirement faladorSpyPlaced;
	VarbitRequirement hosidiusSpyPlaced;
	VarbitRequirement gnomeSpyPlaced;
	VarbitRequirement phasmatysSpyPlaced;
	VarbitRequirement alKharidSpyPlaced;
	NpcRequirement inFight;

	// Steps
	ObjectStep pickCabbage;
	DetailedQuestStep eatCabbage;
	NpcStep talkToDennis;

	NpcStep askAboutStew;
	ObjectStep growCrops;
	ObjectStep cookStew;
	NpcStep askWhoToDeliverTo;
	ObjectStep fillBowls;
	NpcStep giveStewToJane;
	NpcStep giveStewToSergeant;
	NpcStep giveStewToMary;
	NpcStep giveStewToKonar;
	NpcStep reportStew;
	ConditionalStep stewTask;

	NpcStep askAboutPotion;
	NpcStep bringDennisCabbage;
	NpcStep getBookFromAllanna;
	DetailedQuestStep makeCabbageWater;
	NpcStep blessCompost;
	DetailedQuestStep addCompostToPotion;
	NpcStep givePotionToDennis;
	NpcStep continueWithDennis;

	ObjectStep placeFaladorSpy;
	ObjectStep placeHosidiusSpy;
	ObjectStep placeGnomeSpy;
	ObjectStep placePhasmatysSpy;
	ObjectStep placeAlKharidSpy;
	NpcStep reportSpies;

	NpcStep startFight;
	NpcStep killFarmers;
	NpcStep finishQuest;

	@Override
	protected void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_WATER);
		bowls = new ItemRequirement("Bowl", ItemID.BOWL_EMPTY, 4).canBeObtainedDuringQuest();
		cabbage = new ItemRequirement("Cabbage (not from Draynor Manor)", ItemID.CABBAGE, 2).canBeObtainedDuringQuest();
		compost = new ItemRequirement("Compost (not supercompost or ultracompost)", ItemID.BUCKET_COMPOST).canBeObtainedDuringQuest();

		rake = new ItemRequirement("Rake", ItemID.RAKE).isNotConsumed();
		seedDibber = new ItemRequirement("Seed dibber", ItemID.DIBBER).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		communicationCabbage = new ItemRequirement("Communication cabbage", ItemID.ANL_COMMUNICATION_CABBAGE);
		cabbageSeed = new ItemRequirement("Cabbage seed", ItemID.CABBAGE_SEED, 3);
		potatoSeed = new ItemRequirement("Potato seed", ItemID.POTATO_SEED, 3);
		rosemarySeed = new ItemRequirement("Rosemary seed", ItemID.ROSEMARY_SEED);
		prayerToBrassica = new ItemRequirement("Prayer to Brassica", ItemID.ANL_PRAYER);
		perfectCabbage = new ItemRequirement("Perfect cabbage", ItemID.ANL_PERFECT_CABBAGE);
		perfectPotato = new ItemRequirement("Perfect potato", ItemID.ANL_PERFECT_POTATO);
		perfectRosemary = new ItemRequirement("Perfect rosemary", ItemID.ANL_PERFECT_ROSEMARY);
		perfectStew = new ItemRequirement("Perfect cabbage stew", ItemID.ANL_PERFECT_STEW);
		cabbageWater = new ItemRequirement("Cabbage water", ItemID.ANL_CABBAGE_WATER);
		blessedCompost = new ItemRequirement("Blessed compost", ItemID.ANL_BLESSED_COMPOST);
		sentiencePotion = new ItemRequirement("Brassican sentience potion", ItemID.ANL_CABBAGE_POTION);
		sproutSpy = new ItemRequirement("Sprout spy", ItemID.ANL_SPROUT_SPY);

		stewNotAsked = new VarbitRequirement(VarbitID.ANL_STEW_STATE, 0);
		stewAsked = new VarbitRequirement(VarbitID.ANL_STEW_STATE, 5);
		stewCooked = new VarbitRequirement(VarbitID.ANL_STEW_STATE, 10);
		stewDone = new VarbitRequirement(VarbitID.ANL_STEW_STATE, 15);
		toldWhoToDeliverTo = new VarbitRequirement(VarbitID.ANL_STEW_TOLD_WHO_TO_DELIVER_TO, 1);
		stewGivenToJane = new VarbitRequirement(VarbitID.ANL_STEW_GIVEN_JANE, 1);
		stewGivenToSergeant = new VarbitRequirement(VarbitID.ANL_STEW_GIVEN_SERGEANT, 1);
		stewGivenToMary = new VarbitRequirement(VarbitID.ANL_STEW_GIVEN_MARY, 1);
		stewGivenToKonar = new VarbitRequirement(VarbitID.ANL_STEW_GIVEN_KONAR, 1);
		needsMoreStew = new SimpleRequirement()
		{
			@Override
			public boolean check(Client client)
			{
				var stillToFeed = 4
					- client.getVarbitValue(VarbitID.ANL_STEW_GIVEN_JANE)
					- client.getVarbitValue(VarbitID.ANL_STEW_GIVEN_SERGEANT)
					- client.getVarbitValue(VarbitID.ANL_STEW_GIVEN_MARY)
					- client.getVarbitValue(VarbitID.ANL_STEW_GIVEN_KONAR);
				var inventory = client.getItemContainer(InventoryID.INV);
				var stewHeld = inventory == null ? 0 : inventory.count(ItemID.ANL_PERFECT_STEW);
				return stewHeld < stillToFeed;
			}
		};
		toldToGetBook = new VarbitRequirement(VarbitID.ANL_TOLD_TO_GET_BOOK, 1);

		faladorSpyPlaced = new VarbitRequirement(VarbitID.ANL_SPY_1_PLACED, 1);
		hosidiusSpyPlaced = new VarbitRequirement(VarbitID.ANL_SPY_2_PLACED, 1);
		gnomeSpyPlaced = new VarbitRequirement(VarbitID.ANL_SPY_3_PLACED, 1);
		phasmatysSpyPlaced = new VarbitRequirement(VarbitID.ANL_SPY_4_PLACED, 1);
		alKharidSpyPlaced = new VarbitRequirement(VarbitID.ANL_SPY_5_PLACED, 1);

		inFight = new NpcRequirement(NpcID.ANL_GARDENER_OF_ARMADYL);
	}

	private NpcStep dennisStep(String text, Requirement... requirements)
	{
		var step = new NpcStep(this, NpcID.ANL_DISHOP_DENNIS, new WorldPoint(1296, 3722, 0), text, requirements);
		step.addAlternateNpcs(NpcID.ANL_DISHOP_DENNIS_MULTI);
		return step;
	}

	public void setupSteps()
	{
		pickCabbage = new ObjectStep(this, ObjectID.CABBAGE, new WorldPoint(1747, 3555, 0), "Pick cabbages in the field south of Hosidius until you get a communication cabbage. Keep two normal cabbages for later.");
		eatCabbage = new DetailedQuestStep(this, "Eat the communication cabbage.", communicationCabbage.highlighted());
		eatCabbage.addDialogStep("Yes.");
		talkToDennis = dennisStep("Talk to Dishop Dennis in the farmhouse south-east of the Farming Guild.");
		talkToDennis.addDialogStep("How can I help the Church of Brassica?");

		askAboutStew = dennisStep("Ask Dishop Dennis about making and distributing stew.");
		askAboutStew.addDialogSteps(DISCUSS_TASKS, TOPIC_STEW);
		growCrops = new ObjectStep(this, GUILD_SOUTH_ALLOTMENT, new WorldPoint(1267, 3727, 0), "Plant a cabbage seed and a potato seed in the Farming Guild allotments and a rosemary seed in the flower patch, with compost. Read the Prayer to Brassica to grow them instantly, then harvest the perfect cabbage, perfect potato and perfect rosemary. Keep two normal cabbages.", true, spade, cabbageSeed, potatoSeed, rosemarySeed, prayerToBrassica);
		growCrops.addAlternateObjects(GUILD_NORTH_ALLOTMENT, GUILD_FLOWER_PATCH);
		cookStew = new ObjectStep(this, COOKING_POT, new WorldPoint(1293, 3722, 0), "Use the perfect cabbage on the cooking pot in Dennis's farmhouse.", perfectCabbage.highlighted(), perfectPotato, perfectRosemary);
		cookStew.addIcon(ItemID.ANL_PERFECT_CABBAGE);
		askWhoToDeliverTo = dennisStep("Ask Dishop Dennis who to deliver the stew to.");
		askWhoToDeliverTo.addDialogSteps(DISCUSS_TASKS, TOPIC_STEW);
		fillBowls = new ObjectStep(this, COOKING_POT, new WorldPoint(1293, 3722, 0), "Use a bowl on the cooking pot for each person who still needs stew. Bowls are in the crates in the farmhouse.", bowls.highlighted());
		fillBowls.addIcon(ItemID.BOWL_EMPTY);
		giveStewToJane = new NpcStep(this, NpcID.FARMING_GUILD_MASTER_2OP, new WorldPoint(1247, 3727, 0), "Give stew to Guildmaster Jane in the Farming Guild.", perfectStew);
		giveStewToJane.addAlternateNpcs(NpcID.FARMING_GUILD_MASTER_1OP);
		giveStewToSergeant = new NpcStep(this, NpcID.FARMING_GUILD_GUARD_2_ALT, new WorldPoint(1250, 3716, 0), "Give stew to a Sergeant outside the entrance of the Farming Guild.", true, perfectStew);
		giveStewToMary = new NpcStep(this, NpcID.GA_MARY_1OP, new WorldPoint(1240, 3678, 0), "Give stew to Mary in the farmhouse south of the Farming Guild.", perfectStew);
		giveStewToKonar = new NpcStep(this, NpcID.SLAYER_MASTER_8, new WorldPoint(1308, 3786, 0), "Give stew to Konar quo Maten on top of Mount Karuulm.", perfectStew);
		reportStew = dennisStep("Tell Dishop Dennis you have delivered the stew.");
		reportStew.addDialogSteps(DISCUSS_TASKS, TOPIC_STEW);

		var deliverStew = new ConditionalStep(this, reportStew);
		deliverStew.addStep(not(toldWhoToDeliverTo), askWhoToDeliverTo);
		deliverStew.addStep(and(needsMoreStew, bowls.quantity(1)), fillBowls);
		deliverStew.addStep(and(not(stewGivenToJane), perfectStew), giveStewToJane);
		deliverStew.addStep(and(not(stewGivenToSergeant), perfectStew), giveStewToSergeant);
		deliverStew.addStep(and(not(stewGivenToMary), perfectStew), giveStewToMary);
		deliverStew.addStep(and(not(stewGivenToKonar), perfectStew), giveStewToKonar);
		deliverStew.addStep(not(and(stewGivenToJane, stewGivenToSergeant, stewGivenToMary, stewGivenToKonar)), fillBowls);

		stewTask = new ConditionalStep(this, growCrops);
		stewTask.addStep(stewNotAsked, askAboutStew);
		stewTask.addStep(stewCooked, deliverStew);
		stewTask.addStep(and(stewAsked, perfectCabbage, perfectPotato, perfectRosemary), cookStew);

		askAboutPotion = dennisStep("Ask Dishop Dennis about communicating with Brassica Prime.", cabbage.quantity(1));
		askAboutPotion.addDialogSteps(DISCUSS_TASKS, TOPIC_POTION);
		bringDennisCabbage = dennisStep("Bring Dishop Dennis a cabbage.", cabbage.quantity(1));
		bringDennisCabbage.addDialogSteps(DISCUSS_TASKS, TOPIC_POTION);
		getBookFromAllanna = new NpcStep(this, NpcID.KEBOS_FARMING_STORE, new WorldPoint(1250, 3733, 0), "Talk to Allanna in the Farming Guild to get her book. She only gives it to you if you have completed a farming contract.");
		getBookFromAllanna.addDialogStep("I'm told you might have a book on brewing potions from cabbages?");
		makeCabbageWater = new DetailedQuestStep(this, "Use a cabbage on a vial of water.", cabbage.quantity(1).highlighted(), vialOfWater.highlighted());
		blessCompost = dennisStep("Ask Dishop Dennis to bless a bucket of compost.", compost);
		blessCompost.addDialogSteps(DISCUSS_TASKS, TOPIC_POTION);
		addCompostToPotion = new DetailedQuestStep(this, "Use the blessed compost on the cabbage water.", blessedCompost.highlighted(), cabbageWater.highlighted());
		givePotionToDennis = dennisStep("Give the Brassican sentience potion to Dishop Dennis.", sentiencePotion);
		givePotionToDennis.addDialogSteps(DISCUSS_TASKS, TOPIC_POTION);
		continueWithDennis = dennisStep("Talk to Dishop Dennis.");
		continueWithDennis.addDialogSteps(DISCUSS_TASKS, TOPIC_POTION);

		placeFaladorSpy = new ObjectStep(this, ObjectID.RANGE, new WorldPoint(2967, 3331, 0), "Use a sprout spy on the range in the White Knights' Castle in Falador.", sproutSpy.highlighted());
		placeFaladorSpy.addIcon(ItemID.ANL_SPROUT_SPY);
		placeHosidiusSpy = new ObjectStep(this, ObjectID.IZNOT_CLAY_RANGE, new WorldPoint(1648, 3627, 0), "Use a sprout spy on the clay oven in the Hosidius mess hall.", sproutSpy.highlighted());
		placeHosidiusSpy.addIcon(ItemID.ANL_SPROUT_SPY);
		placeGnomeSpy = new ObjectStep(this, ObjectID.ALUFT_GNOME_COOKER, new WorldPoint(2447, 3510, 1), "Use a sprout spy on the gnome cooker upstairs in the Gnome Restaurant area of the Tree Gnome Stronghold.", sproutSpy.highlighted());
		placeGnomeSpy.addIcon(ItemID.ANL_SPROUT_SPY);
		placePhasmatysSpy = new ObjectStep(this, ObjectID.AHOY_RANGE, new WorldPoint(3676, 3467, 0), "Use a sprout spy on the cooking range in Sarah's house in Port Phasmatys.", sproutSpy.highlighted());
		placePhasmatysSpy.addIcon(ItemID.ANL_SPROUT_SPY);
		placeAlKharidSpy = new ObjectStep(this, ObjectID.RANGE, new WorldPoint(3271, 3180, 0), "Use a sprout spy on the range in the kebab shop north of Al Kharid bank.", sproutSpy.highlighted());
		placeAlKharidSpy.addIcon(ItemID.ANL_SPROUT_SPY);
		reportSpies = dennisStep("Tell Dishop Dennis you have placed all the sprout spies. The seed of growth teleports you to the Farming Guild.");
		reportSpies.addDialogSteps(DISCUSS_TASKS, TOPIC_SPIES);

		startFight = dennisStep("Talk to Dishop Dennis to be teleported into the fight. Protect from Melee blocks all damage.", combatGear);
		startFight.addDialogSteps("Is there anything else you need help with?", "I can do that. Let's get to it.");
		killFarmers = new NpcStep(this, NpcID.ANL_GARDENER_OF_ARMADYL, "Kill the five chefs and farmers. Leaving the instance resets the fight.", true, combatGear);
		killFarmers.addAlternateNpcs(NpcID.ANL_BANDIT_CACTUS_CULTIVATOR, NpcID.ANL_FERMENTER_OF_CHAOS, NpcID.ANL_SIR_LOIN, NpcID.ANL_GOBLIN_MEAT_TENDERISER);
		finishQuest = dennisStep("Tell Dishop Dennis you have defeated the chefs and farmers.");
		finishQuest.addDialogStep("I've defeated the chefs and farmers.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var cStart = new ConditionalStep(this, pickCabbage);
		cStart.addStep(communicationCabbage, eatCabbage);
		steps.put(0, cStart);

		steps.put(5, talkToDennis);
		steps.put(10, talkToDennis);

		var cAskAboutPotion = new ConditionalStep(this, askAboutPotion);
		cAskAboutPotion.addStep(not(stewDone), stewTask);
		steps.put(15, cAskAboutPotion);

		var cGetBook = new ConditionalStep(this, bringDennisCabbage);
		cGetBook.addStep(not(stewDone), stewTask);
		cGetBook.addStep(toldToGetBook, getBookFromAllanna);
		steps.put(20, cGetBook);

		var cMakePotion = new ConditionalStep(this, makeCabbageWater);
		cMakePotion.addStep(not(stewDone), stewTask);
		cMakePotion.addStep(sentiencePotion, givePotionToDennis);
		cMakePotion.addStep(and(cabbageWater, blessedCompost), addCompostToPotion);
		cMakePotion.addStep(cabbageWater, blessCompost);
		steps.put(25, cMakePotion);

		steps.put(30, continueWithDennis);

		var cSpies = new ConditionalStep(this, reportSpies);
		cSpies.addStep(not(stewDone), stewTask);
		cSpies.addStep(not(faladorSpyPlaced), placeFaladorSpy);
		cSpies.addStep(not(hosidiusSpyPlaced), placeHosidiusSpy);
		cSpies.addStep(not(gnomeSpyPlaced), placeGnomeSpy);
		cSpies.addStep(not(phasmatysSpyPlaced), placePhasmatysSpy);
		cSpies.addStep(not(alKharidSpyPlaced), placeAlKharidSpy);
		steps.put(35, cSpies);

		steps.put(40, startFight);
		steps.put(45, startFight);

		var cFight = new ConditionalStep(this, startFight);
		cFight.addStep(inFight, killFarmers);
		steps.put(50, cFight);

		steps.put(55, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED),
			new SkillRequirement(Skill.FARMING, 45),
			new SkillRequirement(Skill.COOKING, 35),
			new SkillRequirement(Skill.HERBLORE, 32)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new CombatLevelRequirement(40)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			spade,
			vialOfWater,
			bowls,
			cabbage,
			compost
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			rake,
			seedDibber,
			combatGear,
			food
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Gardener of Armadyl (level 64)",
			"Bandit cactus cultivator (level 57)",
			"Fermenter of chaos (level 53)",
			"Sir Loin (level 52)",
			"Goblin meat tenderiser (level 50)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"You must have completed at least one farming contract.",
			"The Prayer to Brassica grows every allotment patch, so consider harvesting other crops first."
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.FARMING, 10000),
			new ExperienceReward(Skill.COOKING, 2000),
			new ExperienceReward(Skill.HERBLORE, 2000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Antique lamp (2,500 XP in a combat skill)", ItemID.ANL_COMBAT_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Farming Guild allotment patches are permanently protected from disease"),
			new UnlockReward("Seed packs have a 1 in 100 chance of an extra seed of growth")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			pickCabbage,
			eatCabbage,
			talkToDennis
		)));

		sections.add(new PanelDetails("The stew", List.of(
			askAboutStew,
			growCrops,
			cookStew,
			askWhoToDeliverTo,
			fillBowls,
			giveStewToJane,
			giveStewToSergeant,
			giveStewToMary,
			giveStewToKonar,
			reportStew
		), List.of(
			spade,
			bowls
		), List.of(
			rake,
			seedDibber
		)));

		sections.add(new PanelDetails("The potion", List.of(
			askAboutPotion,
			bringDennisCabbage,
			getBookFromAllanna,
			makeCabbageWater,
			blessCompost,
			addCompostToPotion,
			givePotionToDennis,
			continueWithDennis
		), List.of(
			cabbage,
			vialOfWater,
			compost
		)));

		sections.add(new PanelDetails("Sprout spies", List.of(
			placeFaladorSpy,
			placeHosidiusSpy,
			placeGnomeSpy,
			placePhasmatysSpy,
			placeAlKharidSpy,
			reportSpies
		)));

		sections.add(new PanelDetails("The fight", List.of(
			startFight,
			killFarmers,
			finishQuest
		), List.of(
			combatGear
		), List.of(
			food
		)));

		return sections;
	}
}
