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
package com.questhelper.achievementdiaries.morytania;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
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
import net.runelite.api.NullObjectID;
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
	quest = QuestHelperQuest.MORYTANIA_HARD
)
public class MorytaniaHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, crystalMineKey, pickaxe, coins, limestoneBrick, hammer, saw, teakPlank, lawRune,
		bloodRune, noseProtection, watermelonSeeds, seedDibber, rake, axe, tinderbox, witchwoodIcon, lightSource,
		mushroomSpore, spade;

	// Items recommended
	ItemRequirement food, fairyAccess, slayerRing;

	// Quests required
	Requirement hauntedMine, natureSpirit, kingsRansom, knightWaves, cabinFever, inAidOfMyreque,
		theGreatBrainRobbery, desertTreasure, piety;

	Requirement notKharyrll, notAdvancedSpikes, notHarvestWatermelon, notBurnMaho, notHardTempleTrekk, notCaveHorror,
		notBittercapMush, notPietyAltar, notBridgeSalve, notMithOre;

	QuestStep claimReward, moveToMine, moveToLevelTwo, mithOre, kharyrll, advancedSpikes, harvestWatermelon,
		burnMaho, bittercapMush, pietyAltar, bridgeSalve, moveToGrotto, moveToMos, moveToCave,
		moveToHarmony, moveToUpstairs, moveToCapt, moveToIsland;

	NpcStep hardTempleTrekk, caveHorror;

	Zone hauntedMine1, hauntedMine2, grotto, slayerTower2, mos, boat, island, cave, harmony;

	ZoneRequirement inHauntedMine1, inHauntedMine2, inGrotto, inSlayerTower2, inMos, inBoat, inIsland, inCave, inHarmony;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);
		doHard.addStep(new Conditions(notMithOre, inHauntedMine2), mithOre);
		doHard.addStep(new Conditions(notMithOre, inHauntedMine1), moveToLevelTwo);
		doHard.addStep(notMithOre, moveToMine);
		doHard.addStep(new Conditions(notPietyAltar, inGrotto), moveToGrotto);
		doHard.addStep(notPietyAltar, moveToGrotto);
		doHard.addStep(notHardTempleTrekk, hardTempleTrekk);
		doHard.addStep(notBridgeSalve, bridgeSalve);
		doHard.addStep(new Conditions(notAdvancedSpikes, inSlayerTower2), advancedSpikes);
		doHard.addStep(notAdvancedSpikes, moveToUpstairs);
		doHard.addStep(new Conditions(notCaveHorror, inCave), caveHorror);
		doHard.addStep(new Conditions(notCaveHorror, inMos), moveToCave);
		doHard.addStep(new Conditions(notCaveHorror, inBoat), moveToMos);
		doHard.addStep(notCaveHorror, moveToCapt);
		doHard.addStep(new Conditions(notBurnMaho, inIsland), burnMaho);
		doHard.addStep(new Conditions(notBurnMaho, inCave), moveToIsland);
		doHard.addStep(new Conditions(notBurnMaho, inMos), moveToCave);
		doHard.addStep(new Conditions(notBurnMaho, inBoat), moveToMos);
		doHard.addStep(notBurnMaho, moveToCapt);
		doHard.addStep(new Conditions(notHarvestWatermelon, inHarmony), harvestWatermelon);
		doHard.addStep(new Conditions(notHarvestWatermelon, inMos), moveToHarmony);
		doHard.addStep(new Conditions(notHarvestWatermelon, inBoat), moveToMos);
		doHard.addStep(notHarvestWatermelon, moveToCapt);
		doHard.addStep(notKharyrll, kharyrll);
		doHard.addStep(notBittercapMush, bittercapMush);

		return doHard;
	}

	public void setupRequirements()
	{
		notKharyrll = new VarplayerRequirement(1180, false, 23);
		notAdvancedSpikes = new VarplayerRequirement(1180, false, 24);
		notHarvestWatermelon = new VarplayerRequirement(1180, false, 25);
		notBurnMaho = new VarplayerRequirement(1180, false, 26);
		notHardTempleTrekk = new VarplayerRequirement(1180, false, 27);
		notCaveHorror = new VarplayerRequirement(1180, false, 28);
		notBittercapMush = new VarplayerRequirement(1180, false, 29);
		notPietyAltar = new VarplayerRequirement(1180, false, 30);
		notBridgeSalve = new VarplayerRequirement(1181, false, 1);
		notMithOre = new VarplayerRequirement(1181, false, 2);

		piety = new PrayerRequirement("Piety", Prayer.PIETY);

		crystalMineKey = new ItemRequirement("Crystal mine key", ItemID.CRYSTALMINE_KEY).showConditioned(notMithOre);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes()).showConditioned(notMithOre);
		coins = new ItemRequirement("Coins", ItemCollections.getCoins()).showConditioned(notKharyrll);
		limestoneBrick = new ItemRequirement("Limestone brick", ItemID.LIMESTONE_BRICK).showConditioned(notKharyrll);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notKharyrll);
		saw = new ItemRequirement("Saw", ItemID.SAW).showConditioned(notKharyrll);
		teakPlank = new ItemRequirement("Teak plank", ItemID.TEAK_PLANK).showConditioned(notKharyrll);
		lawRune = new ItemRequirement("Law runes", ItemID.LAW_RUNE).showConditioned(notKharyrll);
		bloodRune = new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE).showConditioned(notKharyrll);
		noseProtection = new ItemRequirement("Nose protection", ItemCollections.getNoseProtection())
			.showConditioned(notAdvancedSpikes);
		watermelonSeeds = new ItemRequirement("Watermelon seeds", ItemID.WATERMELON_SEED)
			.showConditioned(notHarvestWatermelon);
		seedDibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER)
			.showConditioned(new Conditions(LogicType.OR, notBittercapMush, notHarvestWatermelon));
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.showConditioned(new Conditions(LogicType.OR, notBittercapMush, notHarvestWatermelon));
		axe = new ItemRequirement("Any axe", ItemCollections.getAxes()).showConditioned(notBurnMaho);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notBurnMaho);
		witchwoodIcon = new ItemRequirement("Witchwood icon", ItemID.WITCHWOOD_ICON)
			.showConditioned(new Conditions(LogicType.OR, notCaveHorror, notBurnMaho));
		lightSource = new ItemRequirement("Light source", ItemCollections.getLightSources())
			.showConditioned(new Conditions(LogicType.OR, notCaveHorror, notBurnMaho));
		mushroomSpore = new ItemRequirement("Mushroom spores", ItemID.MUSHROOM_SPORE).showConditioned(notBittercapMush);
		spade = new ItemRequirement("SPADE", ItemID.SPADE)
			.showConditioned(new Conditions(LogicType.OR, notBittercapMush, notHarvestWatermelon));

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
		fairyAccess = new ItemRequirement("Access to the fairy ring system", ItemCollections.getFairyStaff())
			.showConditioned(new Conditions(LogicType.OR, notBridgeSalve, notPietyAltar));
		slayerRing = new ItemRequirement("Slayer ring", ItemCollections.getSlayerRings());

		inHauntedMine1 = new ZoneRequirement(hauntedMine1);
		inHauntedMine2 = new ZoneRequirement(hauntedMine2);
		inGrotto = new ZoneRequirement(grotto);
		inSlayerTower2 = new ZoneRequirement(slayerTower2);
		inBoat = new ZoneRequirement(boat);
		inMos = new ZoneRequirement(mos);
		inIsland = new ZoneRequirement(island);
		inCave = new ZoneRequirement(cave);
		inHarmony = new ZoneRequirement(harmony);

		hauntedMine = new QuestRequirement(QuestHelperQuest.HAUNTED_MINE, QuestState.FINISHED);
		natureSpirit = new QuestRequirement(QuestHelperQuest.NATURE_SPIRIT, QuestState.FINISHED);
		kingsRansom = new QuestRequirement(QuestHelperQuest.KINGS_RANSOM, QuestState.FINISHED);
		knightWaves = new QuestRequirement(QuestHelperQuest.KNIGHT_WAVES_TRAINING_GROUNDS, QuestState.FINISHED);
		cabinFever = new QuestRequirement(QuestHelperQuest.CABIN_FEVER, QuestState.FINISHED);
		inAidOfMyreque = new QuestRequirement(QuestHelperQuest.IN_AID_OF_THE_MYREQUE, QuestState.FINISHED);
		theGreatBrainRobbery = new QuestRequirement(QuestHelperQuest.THE_GREAT_BRAIN_ROBBERY, QuestState.FINISHED);
		desertTreasure = new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED);
	}

	public void loadZones()
	{
		hauntedMine1 = new Zone(new WorldPoint(3400, 9662, 0), new WorldPoint(3439, 9614, 0));
		hauntedMine2 = new Zone(new WorldPoint(2767, 4605, 0), new WorldPoint(2817, 4556, 0));
		grotto = new Zone(new WorldPoint(2821, 9545, 0), new WorldPoint(2879, 9663, 0));
		slayerTower2 = new Zone(new WorldPoint(3401, 3581, 1), new WorldPoint(3456, 3529, 1));
		boat = new Zone(new WorldPoint(3709, 3508, 1), new WorldPoint(3721, 3489, 1));
		mos = new Zone(new WorldPoint(3642, 3077, 0), new WorldPoint(3855, 2924, 1));
		island = new Zone(new WorldPoint(3819, 3072, 0), new WorldPoint(3841, 3049, 0));
		cave = new Zone(new WorldPoint(3711, 9474, 0), new WorldPoint(3840, 9340, 0));
		harmony = new Zone(new WorldPoint(3774, 2878, 0), new WorldPoint(3841, 2813, 0));
	}

	public void setupSteps()
	{
		moveToMine = new ObjectStep(this, ObjectID.CART_TUNNEL, new WorldPoint(3440, 3232, 0),
			"Enter the haunted mine.");
		mithOre = new ObjectStep(this, ObjectID.ORE_VEIN_20419, new WorldPoint(2786, 4599, 0),
			"Mine the mithril ore. You may need to kill or juke the possessed pickaxes in the area.", pickaxe,
			crystalMineKey);
		moveToLevelTwo = new ObjectStep(this, ObjectID.LADDER_4965, new WorldPoint(3413, 9633, 0),
			"Climb down the ladder that leads to the lower level.");

		moveToGrotto = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3338, 0),
			"Enter the grotto tree in Mort Myre Swamp.");
		pietyAltar = new ObjectStep(this, ObjectID.ALTAR_OF_NATURE, new WorldPoint(3442, 9741, 1),
			"Pray at the altar.", piety);

		hardTempleTrekk = new NpcStep(this, NpcID.ROLAYNE_TWICKIT_HARD, new WorldPoint(3477, 3236, 0),
			"Complete a Hard Temple Trek. Alternatively complete a Hard Burgh de Rott Ramble. You can use Route 1 to" +
				" evade combat events.");

		bridgeSalve = new ObjectStep(this, ObjectID.ORNATE_RAILING, new WorldPoint(3424, 3476, 0),
			"Use the shortcut to get to the bridge. This achievement only works one-way, so you must go from " +
				"top-down.");

		bittercapMush = new ObjectStep(this, NullObjectID.NULL_8337, new WorldPoint(3452, 3473, 0),
			"Plant and harvest the bittercap mushrooms in Canifis. It takes 4 hours to fully grow.", rake, seedDibber,
			mushroomSpore);

		moveToUpstairs = new ObjectStep(this, ObjectID.SPIKEY_CHAIN, new WorldPoint(3422, 3550, 0),
			"Climb up the chain to get to the second floor of the slayer tower.");
		advancedSpikes = new ObjectStep(this, 16537, new WorldPoint(3447, 3576, 1),
			"Climb the advanced spike chain.");

		moveToCapt = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0),
			"Cross the gangplank to Bill Teach's ship.");
		moveToMos = new NpcStep(this, NpcID.BILL_TEACH_4016, new WorldPoint(3714, 3497, 1),
			"Talk to Bill Teach to travel to Mos Le'Harmless.");
		moveToCave = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3650, new WorldPoint(3748, 2973, 0),
			"Enter the Mos Le'Harmless Cave.", witchwoodIcon.equipped(), lightSource);
		caveHorror = new NpcStep(this, NpcID.CAVE_HORROR, new WorldPoint(3740, 9373, 0),
			"Kill a Cave horror.", witchwoodIcon.equipped());
		caveHorror.addAlternateNpcs(NpcID.CAVE_HORROR_1048, NpcID.CAVE_HORROR_1049, NpcID.CAVE_HORROR_1050,
			NpcID.CAVE_HORROR_1051);
		moveToIsland = new ObjectStep(this, ObjectID.STAIRCASE_5269, new WorldPoint(3830, 9463, 0),
			"Climb the staircase in the north east of the Cave Horror dungeon.");
		burnMaho = new ObjectStep(this, 9034, new WorldPoint(3826, 3056, 0),
			"Chop and burn mahogany logs on the island.", axe, tinderbox);
		moveToHarmony = new NpcStep(this, NpcID.BROTHER_TRANQUILITY_551, new WorldPoint(3680, 2961, 0),
			"Talk to Brother Tranquility to travel to Harmony Island.");
		moveToHarmony.addDialogStep("Yes, please.");
		harvestWatermelon = new ObjectStep(this, NullObjectID.NULL_21950, new WorldPoint(3794, 2836, 0),
			"Plant and harvest watermelon on Harmony Island. It takes 80 minutes to fully grow.",
			watermelonSeeds.quantity(3), seedDibber, spade, rake);

		kharyrll = new DetailedQuestStep(this, "Enter the Kharyll portal in your POH. Through a Portal Chamber or " +
			"Portal Nexus. Either make or construct a Portal Chamber / Kharyll Portal.", coins.quantity(100000),
			limestoneBrick.quantity(2), hammer, saw, teakPlank.quantity(3), lawRune.quantity(200), bloodRune.quantity(100));

		claimReward = new NpcStep(this, NpcID.LESABR, new WorldPoint(3464, 3480, 0),
			"Talk to Le-Sabre near Canifis to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, coins.quantity(100000), limestoneBrick.quantity(2), hammer, saw,
			teakPlank.quantity(3), lawRune.quantity(200), bloodRune.quantity(100), seedDibber, spade, rake, axe,
			tinderbox, witchwoodIcon, lightSource, mushroomSpore, pickaxe, crystalMineKey);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, fairyAccess, slayerRing);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 71, true));
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 50));
		reqs.add(new SkillRequirement(Skill.DEFENCE, 70, false));
		reqs.add(new SkillRequirement(Skill.FARMING, 53, true));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 50));
		reqs.add(new SkillRequirement(Skill.MAGIC, 66, true));
		reqs.add(new SkillRequirement(Skill.MINING, 55, true));
		reqs.add(new SkillRequirement(Skill.PRAYER, 70, false));
		reqs.add(new SkillRequirement(Skill.SLAYER, 58, true));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 50));

		reqs.add(cabinFever);
		reqs.add(desertTreasure);
		reqs.add(hauntedMine);
		reqs.add(inAidOfMyreque);
		reqs.add(kingsRansom);
		reqs.add(knightWaves);
		reqs.add(natureSpirit);
		reqs.add(theGreatBrainRobbery);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("At least 1 Gorak (level 145)");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails Steps = new PanelDetails("Haunted Mithril Ore", Arrays.asList(moveToMine, moveToLevelTwo,
			mithOre), new SkillRequirement(Skill.MINING, 55, true), hauntedMine, pickaxe, crystalMineKey);
		Steps.setDisplayCondition(notMithOre);
		allSteps.add(Steps);

		PanelDetails pietySteps = new PanelDetails("Nature Grotto Piety", Arrays.asList(moveToGrotto, pietyAltar),
			natureSpirit, kingsRansom, knightWaves, new SkillRequirement(Skill.PRAYER, 70),
			new SkillRequirement(Skill.DEFENCE, 70));
		pietySteps.setDisplayCondition(notPietyAltar);
		allSteps.add(pietySteps);

		PanelDetails hardSteps = new PanelDetails("Hard Temple Trekk", Collections.singletonList(hardTempleTrekk));
		hardSteps.setDisplayCondition(notHardTempleTrekk);
		allSteps.add(hardSteps);

		PanelDetails spikeSteps = new PanelDetails("Advanced Spike Chain", Arrays.asList(moveToUpstairs,
			advancedSpikes), new SkillRequirement(Skill.AGILITY, 71, true), noseProtection, food);
		spikeSteps.setDisplayCondition(notAdvancedSpikes);
		allSteps.add(spikeSteps);

		PanelDetails salveSteps = new PanelDetails("Shortcut Over the Salve", Collections.singletonList(bridgeSalve),
			new SkillRequirement(Skill.AGILITY, 65));
		salveSteps.setDisplayCondition(notBridgeSalve);
		allSteps.add(salveSteps);

		PanelDetails caveHorrorSteps = new PanelDetails("Kill Cave Horror", Arrays.asList(moveToMos, moveToCave,
			caveHorror), new SkillRequirement(Skill.SLAYER, 58, true), cabinFever, combatGear,
			witchwoodIcon.equipped(), lightSource, food);
		caveHorrorSteps.setDisplayCondition(notCaveHorror);
		allSteps.add(caveHorrorSteps);

		PanelDetails mahoSteps = new PanelDetails("Chop and Burn Mahogany", Arrays.asList(moveToMos, burnMaho),
			new SkillRequirement(Skill.FIREMAKING, 50), new SkillRequirement(Skill.WOODCUTTING, 50),
			axe, tinderbox, witchwoodIcon.equipped(), lightSource);
		mahoSteps.setDisplayCondition(notBurnMaho);
		allSteps.add(mahoSteps);

		PanelDetails watermelonsSteps = new PanelDetails("Harmony Watermelons", Arrays.asList(moveToMos, moveToHarmony,
			harvestWatermelon), new SkillRequirement(Skill.FARMING, 53, true), theGreatBrainRobbery,
			watermelonSeeds.quantity(3), rake, seedDibber);
		watermelonsSteps.setDisplayCondition(notHarvestWatermelon);
		allSteps.add(watermelonsSteps);

		PanelDetails kharyllSteps = new PanelDetails("Kharyll Portal", Collections.singletonList(kharyrll),
			new SkillRequirement(Skill.MAGIC, 66), new SkillRequirement(Skill.CONSTRUCTION, 50), desertTreasure,
			coins.quantity(100000), limestoneBrick.quantity(2), hammer, saw, teakPlank.quantity(3),
			lawRune.quantity(200), bloodRune.quantity(100));
		kharyllSteps.setDisplayCondition(notKharyrll);
		allSteps.add(kharyllSteps);

		PanelDetails bitterSteps = new PanelDetails("Bittercap Mushrooms", Collections.singletonList(bittercapMush),
			new SkillRequirement(Skill.FARMING, 53, true), mushroomSpore, rake, seedDibber, spade);
		bitterSteps.setDisplayCondition(notBittercapMush);
		allSteps.add(bitterSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}