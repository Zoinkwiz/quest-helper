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
package com.questhelper.helpers.achievementdiaries.lumbridgeanddraynor;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.TileStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.LUMBRIDGE_HARD
)
public class LumbridgeHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement bones, earthRune4, earthRune10, earthRune14, waterRune, natureRune, fairyAccess, axe, goldBar,
		cutDiamond, amuletMould, ballOfWool, cosmicRune, diamondAmuletU, diamondAmulet, miningHelm, tinderbox, coins,
		essence, cosmicAccessOrAbyss, bellaSeed, seedDib, spade, rake, gloves, earthRune;

	// Items recommended
	ItemRequirement ringOfDueling, gamesNeck, dorgSphere, lightsource;

	// Quests required
	Requirement tearOfGuth, anotherSliceOfHAM, recipeForDisaster, lostCity, madeAmuletU, bonesToPeaches;

	Requirement notBonesToPeachesPalace, notJuttingWall, notCosmics, notWakaToEdge, notHundredTears, notTrainToKeld,
		notBarrowsGloves, notBelladonna, notLightMiningHelm, notSmiteAltar, notPowerAmmy, smiteActive, bothEarth;

	QuestStep claimReward, moveToBasementForHelm, moveToBasementForTears, moveToBasementForTrain, moveToZanarisForWall,
		moveToZanarisForCosmics, moveToPalace, moveToLumby, moveToDorg, bonesToPeachesPalace, moveToBasementForGloves,
		juttingWall, cosmics, wakaToEdge, hundredTears, trainToKeld, barrowsGloves, belladonna, lightMiningHelm,
		smiteAltar, powerAmmy, smeltAmmy, stringAmmy, dorgStairs, stationDoor, moveToCosmic, unlockBonesToPeaches;

	Zone zanaris, basement, palace, lumby, underground, dorg1, dorg2, cosmicAltar, station;

	ZoneRequirement inZanaris, inBasement, inPalace, inLumby, inUnderground, inDorg1, inDorg2, inCosmicAltar, inStation;

	ConditionalStep bonesToPeachesPalaceTask, juttingWallTask, cosmicsTask, wakaToEdgeTask, hundredTearsTask, trainToKeldTask,
		barrowsGlovesTask, belladonnaTask, lightMiningHelmTask, smiteAltarTask, powerAmmyTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		belladonnaTask = new ConditionalStep(this, belladonna);
		doHard.addStep(notBelladonna, belladonnaTask);

		smiteAltarTask = new ConditionalStep(this, smiteAltar);
		doHard.addStep(notSmiteAltar, smiteAltarTask);

		bonesToPeachesPalaceTask = new ConditionalStep(this, unlockBonesToPeaches);
		bonesToPeachesPalaceTask.addStep(bonesToPeaches, moveToPalace);
		bonesToPeachesPalaceTask.addStep(new Conditions(bonesToPeaches, inPalace), bonesToPeachesPalace);
		doHard.addStep(notBonesToPeachesPalace, bonesToPeachesPalaceTask);

		wakaToEdgeTask = new ConditionalStep(this, wakaToEdge);
		doHard.addStep(notWakaToEdge, wakaToEdgeTask);

		powerAmmyTask = new ConditionalStep(this, moveToLumby);
		powerAmmyTask.addStep(inLumby, smeltAmmy);
		powerAmmyTask.addStep(new Conditions(inLumby, madeAmuletU, diamondAmuletU), stringAmmy);
		powerAmmyTask.addStep(new Conditions(inLumby, madeAmuletU, diamondAmulet), powerAmmy);
		doHard.addStep(notPowerAmmy, powerAmmyTask);

		lightMiningHelmTask = new ConditionalStep(this, moveToBasementForHelm);
		lightMiningHelmTask.addStep(inBasement, lightMiningHelm);
		doHard.addStep(notLightMiningHelm, lightMiningHelmTask);

		barrowsGlovesTask = new ConditionalStep(this, moveToBasementForGloves);
		barrowsGlovesTask.addStep(inBasement, barrowsGloves);
		doHard.addStep(notBarrowsGloves, barrowsGlovesTask);

		hundredTearsTask = new ConditionalStep(this, moveToBasementForTears);
		hundredTearsTask.addStep(inUnderground, hundredTears);
		doHard.addStep(notHundredTears, hundredTearsTask);

		trainToKeldTask = new ConditionalStep(this, moveToBasementForTrain);
		trainToKeldTask.addStep(inUnderground, moveToDorg);
		trainToKeldTask.addStep(inDorg1, dorgStairs);
		trainToKeldTask.addStep(inDorg2, stationDoor);
		trainToKeldTask.addStep(inStation, trainToKeld);
		doHard.addStep(notTrainToKeld, trainToKeldTask);

		juttingWallTask = new ConditionalStep(this, moveToZanarisForWall);
		juttingWallTask.addStep(inZanaris, juttingWall);
		doHard.addStep(notJuttingWall, juttingWallTask);

		cosmicsTask = new ConditionalStep(this, moveToZanarisForCosmics);
		cosmicsTask.addStep(inZanaris, moveToCosmic);
		cosmicsTask.addStep(inCosmicAltar, cosmics);
		doHard.addStep(notCosmics, cosmicsTask);

		return doHard;
	}

	@Override
	public void setupRequirements()
	{
		notBonesToPeachesPalace = new VarplayerRequirement(1194, false, 25);
		notJuttingWall = new VarplayerRequirement(1194, false, 26);
		notCosmics = new VarplayerRequirement(1194, false, 27);
		notWakaToEdge = new VarplayerRequirement(1194, false, 28);
		notHundredTears = new VarplayerRequirement(1194, false, 29);
		notTrainToKeld = new VarplayerRequirement(1194, false, 30);
		notBarrowsGloves = new VarplayerRequirement(1194, false, 31);
		notBelladonna = new VarplayerRequirement(1195, false, 0);
		notLightMiningHelm = new VarplayerRequirement(1195, false, 1);
		notSmiteAltar = new VarplayerRequirement(1195, false, 2);
		notPowerAmmy = new VarplayerRequirement(1195, false, 3);
		bothEarth = new ComplexRequirement(LogicType.AND, "Earth runes", notBonesToPeachesPalace, notPowerAmmy);

		smiteActive = new PrayerRequirement("Smite prayer active", Prayer.SMITE);
		bonesToPeaches = new VarbitRequirement(1505, Operation.EQUAL, 1, "Bones to peaches unlocked");
		ringOfDueling = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS)
			.showConditioned(new Conditions(LogicType.OR, notSmiteAltar, notBonesToPeachesPalace));
		bones = new ItemRequirement("Bones", ItemID.BONES).showConditioned(notBonesToPeachesPalace);
		earthRune4 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 4)
			.showConditioned(new Conditions(notBonesToPeachesPalace, new Conditions(LogicType.NOR, bothEarth)));
		earthRune10 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 10)
			.showConditioned(new Conditions(notPowerAmmy, new Conditions(LogicType.NOR, bothEarth)));
		earthRune14 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 14)
			.showConditioned(bothEarth);
		earthRune = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE, 4).showConditioned(notBonesToPeachesPalace);
		natureRune = new ItemRequirement("Nature rune", ItemID.NATURE_RUNE, 2).showConditioned(notBonesToPeachesPalace);
		fairyAccess = new ItemRequirement("Lunar or Dramen staff", ItemCollections.FAIRY_STAFF)
			.showConditioned(new Conditions(LogicType.OR, notCosmics, notJuttingWall)).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notWakaToEdge).isNotConsumed();
		goldBar = new ItemRequirement("Gold bar", ItemID.GOLD_BAR).showConditioned(notPowerAmmy);
		cutDiamond = new ItemRequirement("Diamond", ItemID.DIAMOND).showConditioned(notPowerAmmy);
		amuletMould = new ItemRequirement("Amulet mould", ItemID.AMULET_MOULD).showConditioned(notPowerAmmy).isNotConsumed();
		ballOfWool = new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL).showConditioned(notPowerAmmy);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE).showConditioned(notPowerAmmy);
		diamondAmuletU = new ItemRequirement("Diamond amulet (u)", ItemID.DIAMOND_AMULET_U).showConditioned(notPowerAmmy);
		diamondAmulet = new ItemRequirement("Diamond amulet", ItemID.DIAMOND_AMULET).showConditioned(notPowerAmmy);
		miningHelm = new ItemRequirement("Mining helmet", ItemCollections.MINING_HELM).showConditioned(notLightMiningHelm).isNotConsumed();
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notLightMiningHelm).isNotConsumed();
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 130000).showConditioned(notBarrowsGloves);
		gamesNeck = new ItemRequirement("Games Necklace", ItemCollections.GAMES_NECKLACES).showConditioned(notHundredTears);
		dorgSphere = new ItemRequirement("Dorgesh-kaan Sphere", ItemID.DORGESHKAAN_SPHERE).showConditioned(notTrainToKeld);
		essence = new ItemRequirement("Pure or Daeyalt essence", ItemCollections.ESSENCE_HIGH, 28).showConditioned(notCosmics);
		cosmicAccessOrAbyss = new ItemRequirement("Access to cosmic altar, or travel through abyss. Tiara recommended unless using essence pouches",
			ItemCollections.COSMIC_ALTAR).showConditioned(notCosmics).isNotConsumed();
		cosmicAccessOrAbyss.setTooltip("Cosmic talisman or tiara");
		bellaSeed = new ItemRequirement("Belladonna seed", ItemID.BELLADONNA_SEED).showConditioned(notBelladonna);
		seedDib = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER).showConditioned(notBelladonna).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notBelladonna).isNotConsumed();
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notBelladonna).isNotConsumed();
		gloves = new ItemRequirement("Gloves", ItemCollections.BELLADONNA_GLOVES).showConditioned(notBelladonna).isNotConsumed();
		gloves.addAlternates(ItemCollections.GRACEFUL_GLOVES);
		gloves.setUrlSuffix("Belladonna#Gloves");
		gloves.setTooltip("'Go to wiki..' to see valid options for handling belladonna");
		lightsource = new ItemRequirement("A lightsource", ItemCollections.LIGHT_SOURCES)
			.showConditioned(new Conditions(LogicType.OR, notHundredTears, notTrainToKeld)).isNotConsumed();

		inZanaris = new ZoneRequirement(zanaris);
		inBasement = new ZoneRequirement(basement);
		inPalace = new ZoneRequirement(palace);
		inLumby = new ZoneRequirement(lumby);
		inUnderground = new ZoneRequirement(underground);
		inDorg1 = new ZoneRequirement(dorg1);
		inDorg2 = new ZoneRequirement(dorg2);
		inCosmicAltar = new ZoneRequirement(cosmicAltar);
		inStation = new ZoneRequirement(station);


		madeAmuletU = new ChatMessageRequirement(
			inLumby,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) madeAmuletU).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inLumby),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		recipeForDisaster = new QuestRequirement(QuestHelperQuest.RECIPE_FOR_DISASTER, QuestState.FINISHED);
		anotherSliceOfHAM = new QuestRequirement(QuestHelperQuest.ANOTHER_SLICE_OF_HAM, QuestState.FINISHED);
		tearOfGuth = new QuestRequirement(QuestHelperQuest.TEARS_OF_GUTHIX, QuestState.FINISHED);
		lostCity = new QuestRequirement(QuestHelperQuest.LOST_CITY, QuestState.FINISHED);
	}

	public void loadZones()
	{
		zanaris = new Zone(new WorldPoint(2369, 4481, 0), new WorldPoint(2497, 4352, 0));
		basement = new Zone(new WorldPoint(3206, 9626, 0), new WorldPoint(3221, 9613, 0));
		palace = new Zone(new WorldPoint(3282, 3166, 0), new WorldPoint(3303, 3159, 0));
		lumby = new Zone(new WorldPoint(3216, 3261, 0), new WorldPoint(3234, 3247, 0));
		underground = new Zone(new WorldPoint(3137, 9706, 0), new WorldPoint(3332, 9465, 2));
		dorg1 = new Zone(new WorldPoint(2688, 5377, 0), new WorldPoint(2751, 5251, 0));
		dorg2 = new Zone(new WorldPoint(2688, 5377, 1), new WorldPoint(2751, 5251, 1));
		cosmicAltar = new Zone(new WorldPoint(2111, 4863, 0), new WorldPoint(2174, 4804, 0));
		station = new Zone(new WorldPoint(2478, 5558, 0), new WorldPoint(2491, 5513, 0));
	}

	public void setupSteps()
	{
		smiteAltar = new ObjectStep(this, ObjectID.ALTAR, new WorldPoint(3377, 3285, 0),
			"Pray at the altar at the Emir's Arena with smite active.", smiteActive);

		moveToPalace = new DetailedQuestStep(this, new WorldPoint(3293, 3164, 0),
			"Enter the Al Kharid palace.");
		bonesToPeachesPalace = new DetailedQuestStep(this, "Cast bones to peaches.", bones, earthRune4,
			waterRune, natureRune);
		unlockBonesToPeaches = new DetailedQuestStep(this, "Unlock bones to peaches from the Mage Training Arena.");

		wakaToEdge = new ObjectStep(this, 12163, new WorldPoint(3242, 3237, 0),
			"Use the canoe station in lumbridge to make a Waka and travel to Edgeville.", axe);

		smeltAmmy = new ObjectStep(this, ObjectID.FURNACE_24009, new WorldPoint(3227, 3257, 0),
			"Smelt a Diamond Amulet (u), at the furnace in Lumbridge.", goldBar.highlighted(), cutDiamond);
		smeltAmmy.addIcon(ItemID.GOLD_BAR);
		stringAmmy = new ItemStep(this, "String the diamond amulet.", ballOfWool.highlighted(), diamondAmuletU.highlighted());
		powerAmmy = new ItemStep(this, "Enchant the strung diamond amulet.", diamondAmulet, cosmicRune.quantity(1),
			earthRune10);
		moveToLumby = new TileStep(this, new WorldPoint(3228, 3238, 0),
			"Move to the Lumbridge smithy.", ballOfWool, cutDiamond, goldBar);

		moveToBasementForHelm = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.", miningHelm, tinderbox);
		lightMiningHelm = new ItemStep(this, "Light your mining helmet.", miningHelm.highlighted(), tinderbox.highlighted());

		moveToBasementForGloves = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.", coins);
		barrowsGloves = new ObjectStep(this, 12308, new WorldPoint(3219, 9623, 0),
			"Purchase the barrows gloves from the bank chest. Right click and select 'Buy-items'.",
			coins);
		// 12308 is mine and I only have FULL access, and the ELITE version wouldn't make sense here

		moveToBasementForTears = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.");
		hundredTears = new ObjectStep(this, ObjectID.JUNA_28929, new WorldPoint(3252, 9517, 2),
			"Talk to Juna to start collecting tears. You MUST collect 100 or more and only have one try a week.");
		hundredTears.addDialogStep("Okay...");

		moveToBasementForTrain = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.");
		moveToDorg = new ObjectStep(this, ObjectID.DOOR_6919, new WorldPoint(3317, 9601, 0),
			"Go through the doors to Dorgesh-Kaan.");
		dorgStairs = new ObjectStep(this, ObjectID.STAIRS_22937, new WorldPoint(2714, 5283, 0),
			"Climb the stairs to the second level of Dorgesh-Kaan.");
		stationDoor = new ObjectStep(this, ObjectID.DOORWAY_23052, new WorldPoint(2695, 5277, 1),
			"Enter the Dorgesh-Kaan train station.");
		trainToKeld = new TileStep(this, new WorldPoint(2480, 5538, 0),
			"Board the train and wait for it to leave.");

		moveToZanarisForWall = new ObjectStep(this, ObjectID.DOOR_2406, new WorldPoint(3202, 3169, 0),
			"Go to Zanaris through the shed in Lumbridge swamp " +
				"or any fairy ring in the world if you've partially completed Fairytale II.", fairyAccess.equipped());
		juttingWall = new ObjectStep(this, ObjectID.JUTTING_WALL_17002, new WorldPoint(2400, 4403, 0),
			"Squeeze-through the jutting wall.");

		moveToZanarisForCosmics = new ObjectStep(this, ObjectID.DOOR_2406, new WorldPoint(3202, 3169, 0),
			"Go to Zanaris through the shed in Lumbridge swamp " +
				"or any fairy ring in the world if you've partially completed Fairytale II.", fairyAccess.equipped());
		moveToCosmic = new ObjectStep(this, ObjectID.MYSTERIOUS_RUINS_31607, new WorldPoint(2408, 4377, 0),
			"Enter the cosmic altar.", cosmicAccessOrAbyss.highlighted());
		moveToCosmic.addIcon(ItemID.COSMIC_TALISMAN);
		cosmics = new ObjectStep(this, ObjectID.ALTAR_34766, new WorldPoint(2142, 4833, 0),
			"Craft 56 cosmic runes.", essence);
		belladonna = new ObjectStep(this, 7572, new WorldPoint(3087, 3355, 0),
			"Grow and pick some belladonna from the Draynor Manor farming patch. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			bellaSeed, rake, spade, gloves, seedDib);

		claimReward = new NpcStep(this, NpcID.HATIUS_COSAINTUS, new WorldPoint(3235, 3213, 0),
			"Talk to Hatius Cosaintus in Lumbridge to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins, bones, earthRune4, earthRune10, earthRune14, waterRune, natureRune, essence,
			fairyAccess, axe, goldBar, cutDiamond, amuletMould, ballOfWool, cosmicRune, miningHelm, tinderbox,
			bellaSeed, seedDib, spade, rake, gloves);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ringOfDueling, gamesNeck, dorgSphere, lightsource);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 46));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 70, true));
		reqs.add(new SkillRequirement(Skill.FARMING, 63));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 65));
		reqs.add(new SkillRequirement(Skill.MAGIC, 60));
		reqs.add(new SkillRequirement(Skill.PRAYER, 52));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 59, true));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 57));

		reqs.add(tearOfGuth);
		reqs.add(anotherSliceOfHAM);
		reqs.add(recipeForDisaster);
		reqs.add(lostCity);
		reqs.add(bonesToPeaches);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Explorer's ring 3", ItemID.EXPLORERS_RING_3),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("50% run energy replenish 4 times a day from Explorer's ring"),
			new UnlockReward("Unlimited teleports to cabbage patch near Falador farm for Explorer's ring"),
			new UnlockReward("Access to a shortcut from Lumbridge Swamp to the desert"),
			new UnlockReward("10% increased experience from Tears of Guthix")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails belladonnaSteps = new PanelDetails("Belladonna", Collections.singletonList(belladonna),
			bellaSeed, seedDib, spade, rake, gloves);
		belladonnaSteps.setDisplayCondition(notBelladonna);
		belladonnaSteps.setLockingStep(belladonnaTask);
		allSteps.add(belladonnaSteps);

		PanelDetails smiteAltarSteps = new PanelDetails("Smite Altar", Collections.singletonList(smiteAltar),
			new SkillRequirement(Skill.PRAYER, 52));
		smiteAltarSteps.setDisplayCondition(notSmiteAltar);
		smiteAltarSteps.setLockingStep(smiteAltarTask);
		allSteps.add(smiteAltarSteps);

		PanelDetails peachesPalaceSteps = new PanelDetails("Bones to Peaches Palace",
			Arrays.asList(unlockBonesToPeaches, moveToPalace, bonesToPeachesPalace),
			new SkillRequirement(Skill.MAGIC, 60), bonesToPeaches, bones, earthRune.quantity(4),
			waterRune, natureRune);
		peachesPalaceSteps.setDisplayCondition(notBonesToPeachesPalace);
		peachesPalaceSteps.setLockingStep(bonesToPeachesPalaceTask);
		allSteps.add(peachesPalaceSteps);

		PanelDetails wakaToEdgevilleSteps = new PanelDetails("Waka to Edgeville",
			Collections.singletonList(wakaToEdge), new SkillRequirement(Skill.WOODCUTTING, 57), axe);
		wakaToEdgevilleSteps.setDisplayCondition(notWakaToEdge);
		wakaToEdgevilleSteps.setLockingStep(wakaToEdgeTask);
		allSteps.add(wakaToEdgevilleSteps);

		PanelDetails powerAmuletSteps = new PanelDetails("Power Amulet", Arrays.asList(moveToLumby, smeltAmmy,
			stringAmmy, powerAmmy), new SkillRequirement(Skill.CRAFTING, 70, true),
			new SkillRequirement(Skill.MAGIC, 57), goldBar, cutDiamond, amuletMould, ballOfWool,
			cosmicRune.quantity(1), earthRune.quantity(10));
		powerAmuletSteps.setDisplayCondition(notPowerAmmy);
		powerAmuletSteps.setLockingStep(powerAmmyTask);
		allSteps.add(powerAmuletSteps);

		PanelDetails miningHelmetSteps = new PanelDetails("Mining Helmet", Arrays.asList(moveToBasementForHelm,
			lightMiningHelm), new SkillRequirement(Skill.FIREMAKING, 65), miningHelm, tinderbox);
		miningHelmetSteps.setDisplayCondition(notLightMiningHelm);
		miningHelmetSteps.setLockingStep(lightMiningHelmTask);
		allSteps.add(miningHelmetSteps);

		PanelDetails barrowsGlovesSteps = new PanelDetails("Barrows Gloves", Arrays.asList(moveToBasementForGloves,
			barrowsGloves), recipeForDisaster, coins);
		barrowsGlovesSteps.setDisplayCondition(notBarrowsGloves);
		barrowsGlovesSteps.setLockingStep(barrowsGlovesTask);
		allSteps.add(barrowsGlovesSteps);

		PanelDetails hundredTearsSteps = new PanelDetails("100 Tears", Arrays.asList(moveToBasementForTears, hundredTears),
			tearOfGuth);
		hundredTearsSteps.setDisplayCondition(notHundredTears);
		hundredTearsSteps.setLockingStep(hundredTearsTask);
		allSteps.add(hundredTearsSteps);

		PanelDetails dorgeshTrainSteps = new PanelDetails("Dorgesh Train", Arrays.asList(moveToBasementForTrain,
			moveToDorg, dorgStairs, stationDoor, trainToKeld), anotherSliceOfHAM);
		dorgeshTrainSteps.setDisplayCondition(notTrainToKeld);
		dorgeshTrainSteps.setLockingStep(trainToKeldTask);
		allSteps.add(dorgeshTrainSteps);

		PanelDetails juttingWallSteps = new PanelDetails("Jutting Wall", Arrays.asList(moveToZanarisForWall, juttingWall),
			new SkillRequirement(Skill.AGILITY, 46), lostCity, fairyAccess);
		juttingWallSteps.setDisplayCondition(notJuttingWall);
		juttingWallSteps.setLockingStep(juttingWallTask);
		allSteps.add(juttingWallSteps);

		PanelDetails cosmicsSteps = new PanelDetails("56 Cosmic Runes", Arrays.asList(moveToZanarisForCosmics, moveToCosmic,
			cosmics), new SkillRequirement(Skill.RUNECRAFT, 59, true), lostCity, essence,
			cosmicAccessOrAbyss, fairyAccess);
		cosmicsSteps.setDisplayCondition(notCosmics);
		cosmicsSteps.setLockingStep(cosmicsTask);
		allSteps.add(cosmicsSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
