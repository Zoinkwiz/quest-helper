/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.varrock;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.VARROCK_MEDIUM
)
public class VarrockMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement coins, limpRoot, redSpiderEgg, ringOfCharos, digsitePend, lawRune, airRune, fireRune, mahoLog,
		willowLog, log, willowLog1, willowLog11;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement gardenOfTranq, digSite, gertCat, treeGnomeVillage, soulsBane, enlightenedJourney, qp;

	Requirement notApothStr, notChamps, notCatColour, notGESpirit, notStrongholdEmote, notTolna, notTPDigsite,
		notTPVarrock, notVannaka, notMaho20, notWhiteFruit, notBalloon, notVarrAgi, notFlap, notSlap, notIdea,
		notStamp, notVarrBalloon, notVarrBalloon2;

	Requirement normalBook;

	QuestStep claimReward, apothStr, tolna, tpVarrock, champs, colourCat, geSpirit, emoteFlap, emoteSlap, emoteIdea,
		emoteStamp, moveToStronghold4, moveToStronghold3, moveToStronghold2, moveToStronghold, peace, grain, health,
		cradle, emote, moveToEdge, vannaka, tpDigsite, maho20, whiteFruit, balloon, talkToAug, varrAgi;

	NpcStep moveToEntrana;

	Zone stronghold1, stronghold2, stronghold3, stronghold4, edge, entrana;

	ZoneRequirement inStronghold1, inStronghold4, inStronghold3, inStronghold2, inEdge, inEntrana;

	ConditionalStep apothStrTask, champsTask, colourCatTask, geSpiritTask, stongholdEmoteTask, vannakaTask,
		tpDigsiteTask, tolnaTask, maho20Task, balloonTask, tpVarrockTask, varrAgiTask, whiteFruitTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		apothStrTask = new ConditionalStep(this, apothStr);
		doMedium.addStep(notApothStr, apothStrTask);

		champsTask = new ConditionalStep(this, champs);
		doMedium.addStep(notChamps, champsTask);

		colourCatTask = new ConditionalStep(this, colourCat);
		doMedium.addStep(notCatColour, colourCatTask);

		geSpiritTask = new ConditionalStep(this, geSpirit);
		doMedium.addStep(notGESpirit, geSpiritTask);

		stongholdEmoteTask = new ConditionalStep(this, moveToStronghold);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, inStronghold1), peace);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, notFlap, inStronghold1), moveToStronghold2);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, inStronghold2), grain);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, notSlap, notFlap, inStronghold2), moveToStronghold3);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, inStronghold3), health);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, notIdea, notSlap, notFlap, inStronghold3), moveToStronghold4);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, inStronghold4), cradle);
		stongholdEmoteTask.addStep(new Conditions(notStrongholdEmote, notIdea, notSlap, notFlap, notStamp), emote);
		doMedium.addStep(notStrongholdEmote, stongholdEmoteTask);

		vannakaTask = new ConditionalStep(this, moveToEdge);
		vannakaTask.addStep(new Conditions(notVannaka, inEdge), vannaka);
		doMedium.addStep(notVannaka, vannakaTask);

		tpDigsiteTask = new ConditionalStep(this, tpDigsite);
		doMedium.addStep(notTPDigsite, tpDigsiteTask);

		tolnaTask = new ConditionalStep(this, tolna);
		doMedium.addStep(notTolna, tolnaTask);

		maho20Task = new ConditionalStep(this, maho20);
		maho20Task.addStep(new Conditions(notBalloon, notVarrBalloon2), balloon);
		doMedium.addStep(notMaho20, maho20Task);

		balloonTask = new ConditionalStep(this, moveToEntrana);
		balloonTask.addStep(new Conditions(notBalloon, inEntrana), talkToAug);
		balloonTask.addStep(new Conditions(notBalloon, notVarrBalloon), balloon);
		doMedium.addStep(notBalloon, balloonTask);

		tpVarrockTask = new ConditionalStep(this, tpVarrock);
		doMedium.addStep(notTPVarrock, tpVarrockTask);

		whiteFruitTask = new ConditionalStep(this, whiteFruit);
		doMedium.addStep(notWhiteFruit, whiteFruitTask);

		varrAgiTask = new ConditionalStep(this, varrAgi);
		doMedium.addStep(notVarrAgi, varrAgiTask);

		return doMedium;
	}

	@Override
	public void setupRequirements()
	{
		notApothStr = new VarplayerRequirement(1176, false, 15);
		notChamps = new VarplayerRequirement(1176, false, 16);
		notCatColour = new VarplayerRequirement(1176, false, 18);
		notGESpirit = new VarplayerRequirement(1176, false, 19);
		notStrongholdEmote = new VarplayerRequirement(1176, false, 20);
		notTolna = new VarplayerRequirement(1176, false, 21);
		notTPDigsite = new VarplayerRequirement(1176, false, 22);
		notTPVarrock = new VarplayerRequirement(1176, false, 23);
		notVannaka = new VarplayerRequirement(1176, false, 24);
		notMaho20 = new VarplayerRequirement(1176, false, 25);
		notWhiteFruit = new VarplayerRequirement(1176, false, 26);
		notBalloon = new VarplayerRequirement(1176, false, 27);
		notVarrAgi = new VarplayerRequirement(1176, false, 28);

		notFlap = new VarbitRequirement(2309, 1);
		notSlap = new VarbitRequirement(2310, 1);
		notIdea = new VarbitRequirement(2311, 1);
		notStamp = new VarbitRequirement(2312, 1);

		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		notVarrBalloon = new VarbitRequirement(2872, 0);
		notVarrBalloon2 = new VarbitRequirement(2872, 1);

		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(new Conditions(LogicType.OR, notApothStr, notCatColour, notMaho20));
		limpRoot = new ItemRequirement("Limpwurt root", ItemID.LIMPWURT_ROOT).showConditioned(notApothStr);
		redSpiderEgg = new ItemRequirement("Red spiders' egg", ItemID.RED_SPIDERS_EGGS).showConditioned(notApothStr);
		ringOfCharos = new ItemRequirement("Ring of charos (A)", ItemID.RING_OF_CHAROSA).showConditioned(notCatColour);
		digsitePend = new ItemRequirement("Digsite pendant", ItemCollections.DIGSITE_PENDANTS).showConditioned(notTPDigsite);
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notTPVarrock);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE).showConditioned(notTPVarrock);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE).showConditioned(notTPVarrock);
		mahoLog = new ItemRequirement("Mahogany logs", ItemID.MAHOGANY_LOGS).showConditioned(notMaho20);
		willowLog1 = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS, 1)
			.showConditioned(new Conditions(notBalloon, notVarrBalloon2));
		willowLog11 = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS, 11)
			.showConditioned(new Conditions(notBalloon, notVarrBalloon));
		willowLog = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS).showConditioned(notBalloon);
		log = new ItemRequirement("Logs", ItemID.LOGS).showConditioned(notBalloon);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inStronghold1 = new ZoneRequirement(stronghold1);
		inStronghold2 = new ZoneRequirement(stronghold2);
		inStronghold3 = new ZoneRequirement(stronghold3);
		inStronghold4 = new ZoneRequirement(stronghold4);
		inEdge = new ZoneRequirement(edge);
		inEntrana = new ZoneRequirement(entrana);

		qp = new QuestPointRequirement(32);
		gardenOfTranq = new QuestRequirement(QuestHelperQuest.GARDEN_OF_TRANQUILLITY, QuestState.FINISHED);
		digSite = new QuestRequirement(QuestHelperQuest.THE_DIG_SITE, QuestState.FINISHED);
		gertCat = new QuestRequirement(QuestHelperQuest.GERTRUDES_CAT, QuestState.FINISHED);
		treeGnomeVillage = new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED);
		soulsBane = new QuestRequirement(QuestHelperQuest.A_SOULS_BANE, QuestState.FINISHED);
		enlightenedJourney = new QuestRequirement(QuestHelperQuest.ENLIGHTENED_JOURNEY, QuestState.FINISHED);
	}

	public void loadZones()
	{
		stronghold1 = new Zone(new WorldPoint(1854, 5248, 0), new WorldPoint(1918, 5183, 0));
		stronghold2 = new Zone(new WorldPoint(1983, 5248, 0), new WorldPoint(2049, 5183, 0));
		stronghold3 = new Zone(new WorldPoint(2113, 5313, 0), new WorldPoint(2177, 5246, 0));
		stronghold4 = new Zone(new WorldPoint(2302, 5250, 0), new WorldPoint(2369, 5183, 0));
		edge = new Zone(new WorldPoint(3067, 10000, 0), new WorldPoint(3288, 9821, 0));
		entrana = new Zone(new WorldPoint(2800, 3394, 0), new WorldPoint(2878, 3325, 0));
	}

	public void setupSteps()
	{
		apothStr = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3404, 0),
			"Speak with the apothecary to create a strength potion.", limpRoot, redSpiderEgg, coins.quantity(5));
		apothStr.addDialogSteps("Can you make potions for me?");
		champs = new ObjectStep(this, ObjectID.DOOR_1805, new WorldPoint(3191, 3363, 0),
			"Enter the Champions' Guild.", qp);
		colourCat = new NpcStep(this, NpcID.GERTRUDE_7723, new WorldPoint(3151, 3415, 0),
			"Check your bank if you own a cat/kitten and shoo them. Then speak with Gertrude for a new kitten.",
			coins.quantity(100), ringOfCharos.equipped());
		colourCat.addDialogSteps("Do you have any more kittens?", "[Charm] I'm quite fussy over cats - can I pick my own?");
		geSpirit = new ObjectStep(this, 1295, new WorldPoint(3185, 3510, 0),
			"Use the spirit tree in the Grand Exchange.");

		moveToStronghold = new ObjectStep(this, ObjectID.ENTRANCE_20790, new WorldPoint(3081, 3420, 0),
			"Enter the Security Stronghold.");
		moveToStronghold2 = new ObjectStep(this, ObjectID.LADDER_20785, new WorldPoint(1902, 5222, 0),
			"Go to the 2nd floor of the stronghold.");
		moveToStronghold3 = new ObjectStep(this, ObjectID.LADDER_19004, new WorldPoint(2026, 5218, 0),
			"Go to the 3rd floor of the stronghold.");
		moveToStronghold4 = new ObjectStep(this, ObjectID.DRIPPING_VINE_23706, new WorldPoint(2148, 5284, 0),
			"Go to the 4th floor of the stronghold.");
		peace = new ObjectStep(this, ObjectID.GIFT_OF_PEACE, new WorldPoint(1907, 5222, 0),
			"Collect the gift of peace.");
		grain = new ObjectStep(this, ObjectID.GRAIN_OF_PLENTY, new WorldPoint(2022, 5216, 0),
			"Collect the grain of plenty.");
		health = new ObjectStep(this, ObjectID.BOX_OF_HEALTH, new WorldPoint(2144, 5280, 0),
			"Collect the box of health.");
		cradle = new ObjectStep(this, ObjectID.CRADLE_OF_LIFE, new WorldPoint(2344, 5214, 0),
			"Collect the cradle of life.");
		emote = new DetailedQuestStep(this, "Use the flap, slap head, idea, and stamp emotes.");
		moveToEdge = new ObjectStep(this, ObjectID.TRAPDOOR_1581, new WorldPoint(3097, 3468, 0),
			"Move to the Edgeville dungeon.");

		//TODO find a better way to check for slayer task
		vannaka = new NpcStep(this, NpcID.VANNAKA, new WorldPoint(3146, 9913, 0),
			"Get a task from Vannaka.");
		tolna = new ObjectStep(this, 13968, new WorldPoint(3310, 3452, 0),
			"Enter the Tolna dungeon.");

		tpDigsite = new DetailedQuestStep(this, "Rub the digsite pendant and select the 'Digsite' teleport.",
			digsitePend.highlighted());

		maho20 = new NpcStep(this, NpcID.SAWMILL_OPERATOR, new WorldPoint(3302, 3492, 0),
			"Make 20 mahogany planks at the sawmill in ONE run.", mahoLog.quantity(20), coins.quantity(30000));

		balloon = new ObjectStep(this, 19143, new WorldPoint(3297, 3482, 0),
			"Use the basket east of Varrock to fly to any available destination.", willowLog1);
		moveToEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA_1167, new WorldPoint(3048, 3236, 0),
			"Speak with a monk to travel to Entrana.", true, willowLog11);
		moveToEntrana.addAlternateNpcs(NpcID.MONK_OF_ENTRANA_1166, NpcID.MONK_OF_ENTRANA);
		talkToAug = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2810, 3356, 0),
			"Speak with Augustine and travel to Varrock.", willowLog11);

		whiteFruit = new ObjectStep(this, 9209, new WorldPoint(3230, 3475, 0),
			"Pick a white tree fruit at Varrock Castle.");
		varrAgi = new ObjectStep(this, ObjectID.ROUGH_WALL_14412, new WorldPoint(3221, 3414, 0),
			"Complete a lap of the Varrock rooftop course.");
		tpVarrock = new DetailedQuestStep(this, "Cast Teleport to Varrock", airRune.quantity(3), fireRune.quantity(1), lawRune.quantity(1));

		claimReward = new NpcStep(this, NpcID.TOBY, new WorldPoint(3225, 3415, 0),
			"Talk to Toby in Varrock to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(30105), limpRoot, redSpiderEgg, ringOfCharos, digsitePend, lawRune.quantity(1),
			airRune.quantity(3), fireRune.quantity(1), mahoLog.quantity(20), willowLog1, willowLog11, log);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(40));
		reqs.add(qp);
		reqs.add(new SkillRequirement(Skill.AGILITY, 30));
		reqs.add(new SkillRequirement(Skill.FARMING, 30));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 40));
		reqs.add(new SkillRequirement(Skill.MAGIC, 25));
		reqs.add(normalBook);

		reqs.add(gardenOfTranq);
		reqs.add(digSite);
		reqs.add(treeGnomeVillage);
		reqs.add(gertCat);
		reqs.add(soulsBane);
		reqs.add(enlightenedJourney);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Varrock Armor (2)", ItemID.VARROCK_ARMOUR_2, 1),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("10% Chance to mine 2 ores at once up to mithril ore"),
			new UnlockReward("10% Chance of smelting 2 bars at once up to mithril when using the Edgeville furnace"),
			new UnlockReward("Zaff will sell 30 Battlestaves per day for 7,000 Coins each"),
			new UnlockReward("The Skull sceptre will now hold 18 charges"),
			new UnlockReward("Ability to toggle the Varrock Teleport to go to The Grand Exchange"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails apothSteps = new PanelDetails("Apothecary Strength Potion", Collections.singletonList(apothStr),
			limpRoot, redSpiderEgg, coins.quantity(5));
		apothSteps.setDisplayCondition(notApothStr);
		apothSteps.setLockingStep(apothStrTask);
		allSteps.add(apothSteps);

		PanelDetails champsSteps = new PanelDetails("Enter Champions' Guild", Collections.singletonList(champs), qp);
		champsSteps.setDisplayCondition(notChamps);
		champsSteps.setLockingStep(champsTask);
		allSteps.add(champsSteps);

		PanelDetails catColourSteps = new PanelDetails("Select Cat's Colour", Collections.singletonList(colourCat),
			gardenOfTranq, gertCat, coins.quantity(100), ringOfCharos);
		catColourSteps.setDisplayCondition(notCatColour);
		catColourSteps.setLockingStep(colourCatTask);
		allSteps.add(catColourSteps);

		PanelDetails geSpiritSteps = new PanelDetails("Use The Spirit Tree", Collections.singletonList(geSpirit),
			treeGnomeVillage);
		geSpiritSteps.setDisplayCondition(notGESpirit);
		geSpiritSteps.setLockingStep(geSpiritTask);
		allSteps.add(geSpiritSteps);

		PanelDetails strongEmotesSteps = new PanelDetails("Use 4 Stronghold Emotes", Arrays.asList(moveToStronghold,
			peace, moveToStronghold2, grain, moveToStronghold3, health, moveToStronghold4, cradle, emote), food);
		strongEmotesSteps.setDisplayCondition(notStrongholdEmote);
		strongEmotesSteps.setLockingStep(stongholdEmoteTask);
		allSteps.add(strongEmotesSteps);

		PanelDetails vannakaSteps = new PanelDetails("Slayer Task From Vannaka", Arrays.asList(moveToEdge, vannaka));
		vannakaSteps.setDisplayCondition(notVannaka);
		vannakaSteps.setLockingStep(vannakaTask);
		allSteps.add(vannakaSteps);

		PanelDetails tpDigsiteSteps = new PanelDetails("Teleport to The Digsite", Collections.singletonList(tpDigsite),
			digSite, digsitePend);
		tpDigsiteSteps.setDisplayCondition(notTPDigsite);
		tpDigsiteSteps.setLockingStep(tpDigsiteTask);
		allSteps.add(tpDigsiteSteps);

		PanelDetails tolnaSteps = new PanelDetails("Enter The Tolna Dungeon", Collections.singletonList(tolna),
			soulsBane);
		tolnaSteps.setDisplayCondition(notTolna);
		tolnaSteps.setLockingStep(tolnaTask);
		allSteps.add(tolnaSteps);

		PanelDetails maho20Steps = new PanelDetails("Make 20 Mahogany Planks", Collections.singletonList(maho20),
			mahoLog.quantity(20), coins.quantity(30000));
		maho20Steps.setDisplayCondition(notMaho20);
		maho20Steps.setLockingStep(maho20Task);
		allSteps.add(maho20Steps);

		PanelDetails balloonSteps = new PanelDetails("Leave Varrock in a Balloon", Arrays.asList(moveToEntrana, talkToAug,
			balloon), new SkillRequirement(Skill.FIREMAKING, 40), enlightenedJourney, willowLog1, log);
		balloonSteps.setDisplayCondition(new Conditions(notBalloon, notVarrBalloon2));
		balloonSteps.setLockingStep(balloonTask);
		allSteps.add(balloonSteps);

		PanelDetails balloon2Steps = new PanelDetails("Leave Varrock in a Balloon", Arrays.asList(moveToEntrana, talkToAug,
			balloon), new SkillRequirement(Skill.FIREMAKING, 40), enlightenedJourney, willowLog11, log);
		balloon2Steps.setDisplayCondition(new Conditions(notBalloon, notVarrBalloon));
		balloon2Steps.setLockingStep(balloonTask);
		allSteps.add(balloon2Steps);

		PanelDetails tpVarrSteps = new PanelDetails("Teleport to Varrock", Collections.singletonList(tpVarrock),
			new SkillRequirement(Skill.MAGIC, 25), airRune.quantity(3), lawRune.quantity(1), fireRune.quantity(1),
			normalBook);
		tpVarrSteps.setDisplayCondition(notTPVarrock);
		tpVarrSteps.setLockingStep(tpVarrockTask);
		allSteps.add(tpVarrSteps);

		PanelDetails whiteFruitSteps = new PanelDetails("Pick a White Fruit", Collections.singletonList(whiteFruit),
			new SkillRequirement(Skill.FARMING, 25), gardenOfTranq);
		whiteFruitSteps.setDisplayCondition(notWhiteFruit);
		whiteFruitSteps.setLockingStep(whiteFruitTask);
		allSteps.add(whiteFruitSteps);

		PanelDetails varrAgiSteps = new PanelDetails("Rooftop Course Lap", Collections.singletonList(varrAgi),
			new SkillRequirement(Skill.AGILITY, 30));
		varrAgiSteps.setDisplayCondition(notVarrAgi);
		varrAgiSteps.setLockingStep(varrAgiTask);
		allSteps.add(varrAgiSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
