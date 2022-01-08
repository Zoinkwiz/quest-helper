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
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;

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

@QuestDescriptor(
	quest = QuestHelperQuest.VARROCK_HARD
)
public class VarrockHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear;

	ItemRequirement botSceptre, topSceptre, rightSkull, leftSkull, strangeSkull, runedSceptre, combinedSkullSceptre,
		dashingKeb, coins, cape, axe, airRune, lawRune, fireRune, yewLog, tinderBox, yewSap, rake, spade;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement desertTreasure;

	Requirement ancientBook;

	Requirement notSpottyCape, not153Kudos, notWakkaEdge, notPaddewwaTP, notSkullSceptre, notYewChurch,
		notFancyStone, notYewRoots, notSmiteAltar, notPipe, notMoreKudos, yewNotChecked, yewChecked, yewStump, smiteActive;

	Requirement cutYewTree, boughtSpotterCape;

	QuestStep claimReward, moveToStronghold, moveToStronghold2, moveToStronghold3, moveToStronghold4, makeSceptre,
		makeSkull, skullSceptre, makeSkullSceptre, getCape, spottyCape, getKudos, wakkaEdge, moveToBasement, kudos,
		paddewwaTP, cutYew, goUp1, goUp2, goUp3, burnLogs, fancyStone, growYew, chopYew, digUpYewRoots,
		moveToUpstairs, activateSmite, prayAtAltar, obsPipe, moveToEdge;

	NpcStep killMino, killFlesh, killCatablepon, killAnkou;

	Zone stronghold1, stronghold2, stronghold3, stronghold4, basement, church2, church3, church4, upstairs, sewer;

	ZoneRequirement inStronghold1, inStronghold2, inStronghold3, inStronghold4, inBasement, inChurch2, inChurch3,
		inChurch4, inUpstairs, inSewer, inAsyffShop, inYewZone;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);
		doHard.addStep(new Conditions(notSpottyCape, boughtSpotterCape), spottyCape);
		doHard.addStep(notSpottyCape, getCape);
		doHard.addStep(new Conditions(not153Kudos, inBasement, notMoreKudos), kudos);
		doHard.addStep(new Conditions(not153Kudos, notMoreKudos), moveToBasement);
		doHard.addStep(not153Kudos, getKudos);
		doHard.addStep(new Conditions(notYewChurch, inChurch4, cutYewTree), burnLogs);
		doHard.addStep(new Conditions(notYewChurch, inChurch3, cutYewTree), goUp3);
		doHard.addStep(new Conditions(notYewChurch, inChurch2, cutYewTree), goUp2);
		doHard.addStep(new Conditions(notYewChurch, cutYewTree), goUp1);
		doHard.addStep(notYewChurch, cutYew);
		doHard.addStep(notFancyStone, fancyStone);
		doHard.addStep(new Conditions(notYewRoots, yewStump), digUpYewRoots);
		doHard.addStep(new Conditions(notYewRoots, yewChecked), chopYew);
		doHard.addStep(new Conditions(notYewRoots, yewNotChecked), chopYew);
		doHard.addStep(notYewRoots, growYew);
		doHard.addStep(new Conditions(notSmiteAltar, inUpstairs, smiteActive), prayAtAltar);
		doHard.addStep(new Conditions(notSmiteAltar, inUpstairs), activateSmite);
		doHard.addStep(notSmiteAltar, moveToUpstairs);
		doHard.addStep(notWakkaEdge, wakkaEdge);
		doHard.addStep(notPaddewwaTP, paddewwaTP);
		doHard.addStep(new Conditions(notPipe, inSewer), obsPipe);
		doHard.addStep(notPipe, moveToEdge);
		doHard.addStep(new Conditions(notSkullSceptre, combinedSkullSceptre.alsoCheckBank(questBank)), skullSceptre);
		doHard.addStep(new Conditions(notSkullSceptre, runedSceptre.alsoCheckBank(questBank), strangeSkull.alsoCheckBank(questBank)), makeSkullSceptre);
		doHard.addStep(new Conditions(notSkullSceptre, botSceptre.alsoCheckBank(questBank), topSceptre), makeSceptre);
		doHard.addStep(new Conditions(notSkullSceptre, leftSkull.alsoCheckBank(questBank), rightSkull.alsoCheckBank(questBank)), makeSkull);
		doHard.addStep(new Conditions(notSkullSceptre, inStronghold4), killAnkou);
		doHard.addStep(new Conditions(notSkullSceptre, rightSkull.alsoCheckBank(questBank), inStronghold3), moveToStronghold4);
		doHard.addStep(new Conditions(notSkullSceptre, inStronghold3), killCatablepon);
		doHard.addStep(new Conditions(notSkullSceptre, botSceptre.alsoCheckBank(questBank), inStronghold2), moveToStronghold3);
		doHard.addStep(new Conditions(notSkullSceptre, inStronghold2), killFlesh);
		doHard.addStep(new Conditions(notSkullSceptre, rightSkull.alsoCheckBank(questBank), inStronghold1), moveToStronghold2);
		doHard.addStep(new Conditions(notSkullSceptre, inStronghold1), killMino);
		doHard.addStep(notSkullSceptre, moveToStronghold);

		return doHard;
	}

	public void setupRequirements()
	{
		notSpottyCape = new VarplayerRequirement(1176, false, 29);
		not153Kudos = new VarplayerRequirement(1176, false, 30);
		notWakkaEdge = new VarplayerRequirement(1176, false, 31);
		notPaddewwaTP = new VarplayerRequirement(1177, false, 0);
		notSkullSceptre = new VarplayerRequirement(1177, false, 1);
		notYewChurch = new VarplayerRequirement(1177, false, 2);
		notFancyStone = new VarplayerRequirement(1177, false, 3);
		notYewRoots = new VarplayerRequirement(1177, false, 4);
		notSmiteAltar = new VarplayerRequirement(1177, false, 5);
		notPipe = new VarplayerRequirement(1177, false, 6);

		notMoreKudos = new VarbitRequirement(3637, Operation.GREATER_EQUAL, 153, "153+ Kudos");

		smiteActive = new VarbitRequirement(4121, Operation.EQUAL, 1, "Smite Active");

		ancientBook = new SpellbookRequirement(Spellbook.ANCIENT);

		yewNotChecked = new VarbitRequirement(4771, 45);
		yewChecked = new VarbitRequirement(4771, 46);
		yewStump = new VarbitRequirement(4771, 47);

		// We consider the combined sceptre bits for logic checks (if have runed sceptre, don't need bottom for example)
		botSceptre = new ItemRequirement("Bottom of sceptre", ItemID.BOTTOM_OF_SCEPTRE).showConditioned(notSkullSceptre);
		botSceptre.addAlternates(ItemID.RUNED_SCEPTRE);
		topSceptre = new ItemRequirement("Top of sceptre", ItemID.TOP_OF_SCEPTRE).showConditioned(notSkullSceptre);
		topSceptre.addAlternates(ItemID.RUNED_SCEPTRE);
		leftSkull = new ItemRequirement("Left skull half", ItemID.LEFT_SKULL_HALF).showConditioned(notSkullSceptre);
		leftSkull.addAlternates(ItemID.STRANGE_SKULL);
		rightSkull = new ItemRequirement("Right skull half", ItemID.RIGHT_SKULL_HALF).showConditioned(notSkullSceptre);
		rightSkull.addAlternates(ItemID.STRANGE_SKULL);

		strangeSkull = new ItemRequirement("Strange skull", ItemID.STRANGE_SKULL).showConditioned(notSkullSceptre);
		runedSceptre = new ItemRequirement("Runed sceptre", ItemID.RUNED_SCEPTRE).showConditioned(notSkullSceptre);
		combinedSkullSceptre = new ItemRequirement("Skull sceptre", ItemID.SKULL_SCEPTRE).showConditioned(notSkullSceptre);
		dashingKeb = new ItemRequirement("Dashing kebbit fur", ItemID.DASHING_KEBBIT_FUR).showConditioned(notSpottyCape);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(new Conditions(LogicType.OR, notSpottyCape, notFancyStone));
		cape = new ItemRequirement("Spottier cape", ItemID.SPOTTIER_CAPE).showConditioned(notSpottyCape);
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(new Conditions(LogicType.OR,
			notWakkaEdge, notYewChurch, notYewRoots));
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE).showConditioned(notPaddewwaTP);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE).showConditioned(notPaddewwaTP);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE).showConditioned(notPaddewwaTP);
		yewLog = new ItemRequirement("Yew log", ItemID.YEW_LOGS);
		tinderBox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notYewChurch);
		yewSap = new ItemRequirement("Yew sapling", ItemID.YEW_SAPLING).showConditioned(notYewRoots);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notYewRoots);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notYewRoots);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inStronghold1 = new ZoneRequirement(stronghold1);
		inStronghold2 = new ZoneRequirement(stronghold2);
		inStronghold3 = new ZoneRequirement(stronghold3);
		inStronghold4 = new ZoneRequirement(stronghold4);
		inBasement = new ZoneRequirement(basement);
		inChurch2 = new ZoneRequirement(church2);
		inChurch3 = new ZoneRequirement(church3);
		inChurch4 = new ZoneRequirement(church4);
		inUpstairs = new ZoneRequirement(upstairs);
		inSewer = new ZoneRequirement(sewer);

		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);

		inAsyffShop = new ZoneRequirement(new Zone(
			new WorldPoint(3278, 3394, 0),
			new WorldPoint(3283, 3400, 0))
		);
		boughtSpotterCape = new ChatMessageRequirement(
			inAsyffShop,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) boughtSpotterCape).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inAsyffShop),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		inYewZone = new ZoneRequirement(new Zone(new WorldPoint(3225, 3456, 0), new WorldPoint(3254, 3478, 0)));
		cutYewTree = new ChatMessageRequirement(
			inYewZone,
			"You get some yew logs."
		);
		((ChatMessageRequirement) cutYewTree).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inYewZone),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);
	}

	public void loadZones()
	{
		stronghold1 = new Zone(new WorldPoint(1854, 5248, 0), new WorldPoint(1918, 5183, 0));
		stronghold2 = new Zone(new WorldPoint(1983, 5248, 0), new WorldPoint(2049, 5183, 0));
		stronghold3 = new Zone(new WorldPoint(2113, 5313, 0), new WorldPoint(2177, 5246, 0));
		stronghold4 = new Zone(new WorldPoint(2302, 5250, 0), new WorldPoint(2369, 5183, 0));
		basement = new Zone(new WorldPoint(1728, 4989, 0), new WorldPoint(1792, 4931, 0));
		church2 = new Zone(new WorldPoint(3249, 3489, 1), new WorldPoint(3262, 3469, 1));
		church3 = new Zone(new WorldPoint(3249, 3489, 2), new WorldPoint(3262, 3469, 2));
		church4 = new Zone(new WorldPoint(3249, 3489, 3), new WorldPoint(3262, 3469, 3));
		upstairs = new Zone(new WorldPoint(3199, 3501, 1), new WorldPoint(3227, 3466, 1));
		sewer = new Zone(new WorldPoint(3066, 10001, 0), new WorldPoint(3289, 9820, 0));
	}

	public void setupSteps()
	{
		moveToStronghold = new ObjectStep(this, ObjectID.ENTRANCE_20790, new WorldPoint(3081, 3420, 0),
			"Enter the Security Stronghold.");
		killMino = new NpcStep(this, NpcID.MINOTAUR, new WorldPoint(1888, 5220, 0),
			"Kill Minotaurs until you recieve a right skull half.", rightSkull);
		killMino.addAlternateNpcs(NpcID.MINOTAUR_2482);
		killMino.addAlternateNpcs(NpcID.MINOTAUR_2483);
		moveToStronghold2 = new ObjectStep(this, ObjectID.LADDER_20785, new WorldPoint(1902, 5222, 0),
			"Go to the 2nd floor of the stronghold.");
		killFlesh = new NpcStep(this, NpcID.FLESH_CRAWLER, new WorldPoint(2019, 5215, 0),
			"Kill Flesh crawlers until you receive a bottom of sceptre.", botSceptre);
		killFlesh.addAlternateNpcs(NpcID.FLESH_CRAWLER_2499);
		killFlesh.addAlternateNpcs(NpcID.FLESH_CRAWLER_2500);
		moveToStronghold3 = new ObjectStep(this, ObjectID.LADDER_19004, new WorldPoint(2026, 5218, 0),
			"Go to the 3rd floor of the stronghold.");
		killCatablepon = new NpcStep(this, NpcID.CATABLEPON, new WorldPoint(2144, 5281, 0),
			"Kill Catablepons until you receive a top of sceptre.", topSceptre);
		killCatablepon.addAlternateNpcs(NpcID.CATABLEPON_2475);
		killCatablepon.addAlternateNpcs(NpcID.CATABLEPON_2476);
		moveToStronghold4 = new ObjectStep(this, ObjectID.DRIPPING_VINE_23706, new WorldPoint(2148, 5284, 0),
			"Go to the 4th floor of the stronghold.");
		killAnkou = new NpcStep(this, NpcID.ANKOU, new WorldPoint(2344, 5213, 0),
			"Kill Ankous until you receive a left skull half.", leftSkull);
		killAnkou.addAlternateNpcs(NpcID.ANKOU_2515);
		killAnkou.addAlternateNpcs(NpcID.ANKOU_2516);
		makeSkull = new ItemStep(this, "Use both skull halves together.", leftSkull.highlighted(),
			rightSkull.highlighted());
		makeSceptre = new ItemStep(this, "Use the sceptre pieces together.", botSceptre.highlighted(), topSceptre.highlighted());
		makeSkullSceptre = new ItemStep(this, "Use the runed sceptre and strange skull together.",
			runedSceptre.highlighted(), strangeSkull.highlighted());
		skullSceptre = new DetailedQuestStep(this, "Use the sceptre to teleport to the stronghold.", combinedSkullSceptre.highlighted());
		getCape = new NpcStep(this, NpcID.ASYFF, new WorldPoint(3281, 3398, 0),
			"Have Asyff make a spotty cape.", dashingKeb.quantity(2), coins.quantity(800));
		getCape.addDialogStep("Could you make anything out of this fur that I got from hunting?");
		spottyCape = new ItemStep(this, "Equip the spottier cape.", cape.highlighted());
		moveToBasement = new ObjectStep(this, ObjectID.STAIRS_24428, new WorldPoint(3256, 3452, 0),
			"Climb down the Varrock Museum stairs.");
		kudos = new NpcStep(this, NpcID.ORLANDO_SMITH, new WorldPoint(1759, 4955, 0),
			"Speak with Orlando.");
		getKudos = new DetailedQuestStep(this, "Complete more quests and tasks for kudos. " +
			"Check out the kudos wiki page for more details.");
		wakkaEdge = new ObjectStep(this, 12166, new WorldPoint(3131, 3510, 0),
			"Make a Wakka at the canoe station in Edgeville.", axe);
		paddewwaTP = new DetailedQuestStep(this, "Cast teleport to Paddewwa.", ancientBook, lawRune.quantity(2), airRune.quantity(1), fireRune.quantity(1));
		cutYew = new ObjectStep(this, 10823, new WorldPoint(3249, 3473, 0),
			"Cut a yew tree until you get a log.", axe);
		goUp1 = new ObjectStep(this, ObjectID.STAIRCASE_11790, new WorldPoint(3259, 3488, 0),
			"Climb to the top of the Varrock Church.", yewLog, tinderBox);
		goUp2 = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3259, 3488, 1),
			"Climb the stairs.", yewLog, tinderBox);
		goUp3 = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3259, 3488, 2),
			"Climb the stairs.", yewLog, tinderBox);
		goUp1.addSubSteps(goUp2, goUp3);
		burnLogs = new ItemStep(this, "Burn the yew logs on top of the church.", tinderBox.highlighted(),
			yewLog.highlighted());

		fancyStone = new NpcStep(this, NpcID.ESTATE_AGENT, new WorldPoint(3240, 3475, 0),
			"TALK to the estate agent to redecorate your house to fancy stone. Must be done through dialog, NOT " +
				"right-click.", coins.quantity(25000));
		fancyStone.addDialogStep("Can you redecorate my house please?");
		growYew = new ObjectStep(this, 8513, new WorldPoint(3229, 3459, 0),
			"Grow and check the health of a yew tree in front of Varrock palace. " +
				"Afterwards, dig up the stump to get the yew roots.", yewSap, rake, spade);
		chopYew = new ObjectStep(this, 8513, new WorldPoint(3229, 3459, 0),
			"Chop the yew tree that you grew in front of Varrock palace. Afterwards, dig up the stump to get the Yew " +
				"roots.", axe, spade);
		digUpYewRoots = new ObjectStep(this, 8514, new WorldPoint(3229, 3459, 0),
			"Dig up the stump to get the Yew roots.", spade);
		moveToUpstairs = new ObjectStep(this, ObjectID.STAIRCASE_11789, new WorldPoint(3219, 3497, 0),
			"Climb the stairs in the back of the Varrock palace.");
		activateSmite = new DetailedQuestStep(this, "Activate smite.");
		prayAtAltar = new ObjectStep(this, ObjectID.ALTAR, new WorldPoint(3208, 3495, 1),
			"Pray at altar.", smiteActive);
		moveToEdge = new ObjectStep(this, ObjectID.TRAPDOOR_1581, new WorldPoint(3097, 3468, 0),
			"Enter the Edgeville dungeon.");
		obsPipe = new ObjectStep(this, 16511, new WorldPoint(3150, 9906, 0),
			"Climb through the pipe shortcut near Vannakka.");

		claimReward = new NpcStep(this, NpcID.TOBY, new WorldPoint(3225, 3415, 0),
			"Talk to Toby in Varrock to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(25800), dashingKeb.quantity(2), axe, lawRune.quantity(2),
			fireRune, airRune, tinderBox, yewSap, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 51));
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 50));
		reqs.add(new SkillRequirement(Skill.FARMING, 68, true));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 60));
		reqs.add(new SkillRequirement(Skill.HUNTER, 66));
		reqs.add(new SkillRequirement(Skill.MAGIC, 54));
		reqs.add(new SkillRequirement(Skill.PRAYER, 52));
		reqs.add(new SkillRequirement(Skill.RANGED, 40));
		reqs.add(new SkillRequirement(Skill.THIEVING, 53));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 60));
		reqs.add(ancientBook);
		reqs.add(desertTreasure);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill various monsters in the stronghold of security (levels 13-86)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Varrock Armor (3)", ItemID.VARROCK_ARMOUR_3, 1),
				new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("10% Chance to mine 2 ores at once up to adamantite ore"),
				new UnlockReward("10% Chance of smelting 2 bars at once up to adamantite when using the Edgeville furnace"),
				new UnlockReward("Zaff will sell 60 Battlestaves per day for 7,000 Coins each"),
				new UnlockReward("The Skull sceptre will now hold 22 charges"),
				new UnlockReward("Access to the Cooks' Guild bank"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails spottierCapeSteps = new PanelDetails("Trade for A Spottier Cape", Arrays.asList(getCape, spottyCape),
			new SkillRequirement(Skill.HUNTER, 66), dashingKeb.quantity(2), coins.quantity(800));
		spottierCapeSteps.setDisplayCondition(notSpottyCape);
		allSteps.add(spottierCapeSteps);

		PanelDetails kudosSteps = new PanelDetails("Speak with Orlando With 153+ Kudos", Arrays.asList(getKudos,
			moveToBasement, kudos), notMoreKudos);
		kudosSteps.setDisplayCondition(not153Kudos);
		allSteps.add(kudosSteps);

		PanelDetails yewChurchSteps = new PanelDetails("Cut and Burn Yew Logs Atop the Church", Arrays.asList(cutYew,
			goUp1, burnLogs), new SkillRequirement(Skill.FIREMAKING, 60),
			new SkillRequirement(Skill.WOODCUTTING, 60), axe, tinderBox);
		yewChurchSteps.setDisplayCondition(notYewChurch);
		allSteps.add(yewChurchSteps);

		PanelDetails fancyStoneSteps = new PanelDetails("Fancy Stone", Collections.singletonList(fancyStone),
			new SkillRequirement(Skill.CONSTRUCTION, 50), coins.quantity(25000));
		fancyStoneSteps.setDisplayCondition(notFancyStone);
		allSteps.add(fancyStoneSteps);

		PanelDetails yewRootsSteps = new PanelDetails("Yew Roots", Arrays.asList(growYew, chopYew, digUpYewRoots),
			new SkillRequirement(Skill.FARMING, 68, true), new SkillRequirement(Skill.WOODCUTTING, 60), axe, spade,
			rake, yewSap);
		yewRootsSteps.setDisplayCondition(notYewRoots);
		allSteps.add(yewRootsSteps);

		PanelDetails smitedSteps = new PanelDetails("Altar Smited", Arrays.asList(moveToUpstairs, activateSmite,
			prayAtAltar), new SkillRequirement(Skill.PRAYER, 52));
		smitedSteps.setDisplayCondition(notSmiteAltar);
		allSteps.add(smitedSteps);

		PanelDetails edgevilleWakkaSteps = new PanelDetails("Edgeville Wakka", Collections.singletonList(wakkaEdge),
			new SkillRequirement(Skill.WOODCUTTING, 57), axe);
		edgevilleWakkaSteps.setDisplayCondition(notWakkaEdge);
		allSteps.add(edgevilleWakkaSteps);

		PanelDetails paddewwaSteps = new PanelDetails("Teleport to Paddewwa", Collections.singletonList(paddewwaTP),
			new SkillRequirement(Skill.MAGIC, 54), desertTreasure, ancientBook, lawRune.quantity(2),
			airRune.quantity(1), fireRune.quantity(1));
		paddewwaSteps.setDisplayCondition(notPaddewwaTP);
		allSteps.add(paddewwaSteps);

		PanelDetails obstaclePipeSteps = new PanelDetails("Obstacle Pipe", Arrays.asList(moveToEdge, obsPipe),
			new SkillRequirement(Skill.AGILITY, 51));
		obstaclePipeSteps.setDisplayCondition(notPipe);
		allSteps.add(obstaclePipeSteps);

		PanelDetails skullSceptreSteps = new PanelDetails("Teleport with Skull Sceptre", Arrays.asList(moveToStronghold,
			killMino, moveToStronghold2, killFlesh, moveToStronghold3, killCatablepon, moveToStronghold4, killAnkou,
			makeSkull, makeSceptre, makeSkullSceptre, skullSceptre), combatGear, food);
		skullSceptreSteps.setDisplayCondition(notSkullSceptre);
		allSteps.add(skullSceptreSteps);


		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
