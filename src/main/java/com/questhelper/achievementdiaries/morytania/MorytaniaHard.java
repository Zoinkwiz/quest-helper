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
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
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
		mushroomSpore, spade, mahoLogs;

	// Items recommended
	ItemRequirement food, fairyAccess, slayerRing;

	// Quests required
	Requirement hauntedMine, natureSpirit, kingsRansom, knightWaves, cabinFever, inAidOfMyreque,
		theGreatBrainRobbery, desertTreasure, piety;

	Requirement notKharyrll, notAdvancedSpikes, notHarvestWatermelon, notBurnMaho, notHardTempleTrekk, notCaveHorror,
		notBittercapMush, notPietyAltar, notBridgeSalve, notMithOre, choppedLogs;

	QuestStep claimReward, moveToMine, moveToLevelTwo, mithOre, kharyrll, advancedSpikes, harvestWatermelon, burnMaho,
		chopMaho, bittercapMush, pietyAltar, bridgeSalve, moveToGrotto, moveToMosHorror, moveToCaveHorror, moveToHarmony,
		moveToUpstairs, moveToCaptHorror, moveToIsland, moveToMosMaho, moveToCaveMaho, moveToCaptMaho, moveToCaptMelon,
		moveToMosMelon;

	NpcStep hardTempleTrekk, caveHorror;

	Zone hauntedMine1, hauntedMine2, grotto, slayerTower2, mos, boat, island, cave, harmony;

	ZoneRequirement inHauntedMine1, inHauntedMine2, inGrotto, inSlayerTower2, inMos, inBoat, inIsland, inCave, inHarmony;

	ConditionalStep kharyrllTask, advancedSpikesTask, harvestWatermelonTask, burnMahoTask, hardTempleTrekkTask,
		caveHorrorTask, bittercapMushTask, pietyAltarTask, bridgeSalveTask, mithOreTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		bittercapMushTask = new ConditionalStep(this, bittercapMush);
		doHard.addStep(notBittercapMush, bittercapMushTask);

		harvestWatermelonTask = new ConditionalStep(this, moveToCaptMelon);
		harvestWatermelonTask.addStep(inBoat, moveToMosMelon);
		harvestWatermelonTask.addStep(inMos, moveToHarmony);
		harvestWatermelonTask.addStep(inHarmony, harvestWatermelon);
		doHard.addStep(notHarvestWatermelon, harvestWatermelonTask);

		mithOreTask = new ConditionalStep(this, moveToMine);
		mithOreTask.addStep(inHauntedMine1, moveToLevelTwo);
		mithOreTask.addStep(inHauntedMine2, mithOre);
		doHard.addStep(notMithOre, mithOreTask);

		pietyAltarTask = new ConditionalStep(this, moveToGrotto);
		pietyAltarTask.addStep(inGrotto, pietyAltar);
		doHard.addStep(notPietyAltar, pietyAltarTask);

		hardTempleTrekkTask = new ConditionalStep(this, hardTempleTrekk);
		doHard.addStep(notHardTempleTrekk, hardTempleTrekkTask);

		bridgeSalveTask = new ConditionalStep(this, bridgeSalve);
		doHard.addStep(notBridgeSalve, bridgeSalveTask);

		advancedSpikesTask = new ConditionalStep(this, moveToUpstairs);
		advancedSpikesTask.addStep(inSlayerTower2, advancedSpikes);
		doHard.addStep(notAdvancedSpikes, advancedSpikesTask);

		caveHorrorTask = new ConditionalStep(this, moveToCaptHorror);
		caveHorrorTask.addStep(inBoat, moveToMosHorror);
		caveHorrorTask.addStep(inMos, moveToCaveHorror);
		caveHorrorTask.addStep(inCave, caveHorror);
		doHard.addStep(notCaveHorror, caveHorrorTask);

		burnMahoTask = new ConditionalStep(this, moveToCaptMaho);
		burnMahoTask.addStep(inBoat, moveToMosMaho);
		burnMahoTask.addStep(inMos, moveToCaveMaho);
		burnMahoTask.addStep(inCave, moveToIsland);
		burnMahoTask.addStep(inIsland, chopMaho);
		burnMahoTask.addStep(new Conditions(inIsland, choppedLogs, mahoLogs), burnMaho);
		doHard.addStep(notBurnMaho, burnMahoTask);

		kharyrllTask = new ConditionalStep(this, kharyrll);
		doHard.addStep(notKharyrll, kharyrllTask);

		return doHard;
	}

	@Override
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

		piety = new PrayerRequirement("Piety activated", Prayer.PIETY);

		crystalMineKey = new ItemRequirement("Crystal mine key", ItemID.CRYSTALMINE_KEY).showConditioned(notMithOre);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMithOre);
		coins = new ItemRequirement("Coins", ItemCollections.COINS).showConditioned(notKharyrll);
		limestoneBrick = new ItemRequirement("Limestone brick", ItemID.LIMESTONE_BRICK).showConditioned(notKharyrll);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notKharyrll);
		saw = new ItemRequirement("Saw", ItemID.SAW).showConditioned(notKharyrll);
		teakPlank = new ItemRequirement("Teak plank", ItemID.TEAK_PLANK).showConditioned(notKharyrll);
		lawRune = new ItemRequirement("Law runes", ItemID.LAW_RUNE).showConditioned(notKharyrll);
		bloodRune = new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE).showConditioned(notKharyrll);
		noseProtection = new ItemRequirement("Nose protection", ItemCollections.NOSE_PROTECTION)
			.showConditioned(notAdvancedSpikes);
		watermelonSeeds = new ItemRequirement("Watermelon seeds", ItemID.WATERMELON_SEED)
			.showConditioned(notHarvestWatermelon);
		seedDibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER)
			.showConditioned(new Conditions(LogicType.OR, notBittercapMush, notHarvestWatermelon));
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.showConditioned(new Conditions(LogicType.OR, notBittercapMush, notHarvestWatermelon));
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notBurnMaho);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notBurnMaho);
		witchwoodIcon = new ItemRequirement("Witchwood icon", ItemID.WITCHWOOD_ICON)
			.showConditioned(new Conditions(LogicType.OR, notCaveHorror, notBurnMaho));
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES)
			.showConditioned(new Conditions(LogicType.OR, notCaveHorror, notBurnMaho));
		mushroomSpore = new ItemRequirement("Mushroom spores", ItemID.MUSHROOM_SPORE).showConditioned(notBittercapMush);
		spade = new ItemRequirement("Spade", ItemID.SPADE)
			.showConditioned(new Conditions(LogicType.OR, notBittercapMush, notHarvestWatermelon));
		mahoLogs = new ItemRequirement("Mahogany logs", ItemID.MAHOGANY_LOGS);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		fairyAccess = new ItemRequirement("Access to the fairy ring system", ItemCollections.FAIRY_STAFF)
			.showConditioned(new Conditions(LogicType.OR, notBridgeSalve, notPietyAltar));
		slayerRing = new ItemRequirement("Slayer ring", ItemCollections.SLAYER_RINGS);

		inHauntedMine1 = new ZoneRequirement(hauntedMine1);
		inHauntedMine2 = new ZoneRequirement(hauntedMine2);
		inGrotto = new ZoneRequirement(grotto);
		inSlayerTower2 = new ZoneRequirement(slayerTower2);
		inBoat = new ZoneRequirement(boat);
		inMos = new ZoneRequirement(mos);
		inIsland = new ZoneRequirement(island);
		inCave = new ZoneRequirement(cave);
		inHarmony = new ZoneRequirement(harmony);

		choppedLogs = new ChatMessageRequirement(
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) choppedLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inIsland),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

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
		grotto = new Zone(new WorldPoint(3430, 9750, 1), new WorldPoint(3453, 9728, 1));
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

		moveToGrotto = new ObjectStep(this, ObjectID.GROTTO, new WorldPoint(3440, 3337, 0),
			"Enter the grotto tree in Mort Myre Swamp.");
		pietyAltar = new ObjectStep(this, ObjectID.ALTAR_OF_NATURE, new WorldPoint(3442, 9741, 1),
			"Pray at the altar with Piety activated.", piety);

		hardTempleTrekk = new NpcStep(this, NpcID.ROLAYNE_TWICKIT_HARD, new WorldPoint(3477, 3236, 0),
			"Complete a Hard Temple Trek. Alternatively complete a Hard Burgh de Rott Ramble. You can use Route 1 to" +
				" evade combat events.");

		bridgeSalve = new ObjectStep(this, ObjectID.ORNATE_RAILING, new WorldPoint(3424, 3476, 0),
			"Use the shortcut to get to the bridge. This achievement only works one-way, so you must go from " +
				"top-down.");

		bittercapMush = new ObjectStep(this, NullObjectID.NULL_8337, new WorldPoint(3452, 3473, 0),
			"Plant and harvest the bittercap mushrooms in Canifis. It takes 4 hours to fully grow." +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.", rake, seedDibber,
			mushroomSpore);

		moveToUpstairs = new ObjectStep(this, ObjectID.SPIKEY_CHAIN, new WorldPoint(3422, 3550, 0),
			"Climb up the chain to get to the second floor of the slayer tower.");
		advancedSpikes = new ObjectStep(this, 16537, new WorldPoint(3447, 3576, 1),
			"Climb the advanced spike chain. Go down and back up if you rip your hands as you climb.");

		moveToCaptHorror = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0),
			"Cross the gangplank to Bill Teach's ship.", witchwoodIcon.equipped(), lightSource, combatGear, food);
		moveToMosHorror = new NpcStep(this, NpcID.BILL_TEACH_4016, new WorldPoint(3714, 3497, 1),
			"Talk to Bill Teach to travel to Mos Le'Harmless.", witchwoodIcon.equipped(), lightSource, combatGear, food);
		moveToCaveHorror = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3650, new WorldPoint(3748, 2973, 0),
			"Enter the Mos Le'Harmless Cave.", witchwoodIcon.equipped(), lightSource, combatGear, food);
		caveHorror = new NpcStep(this, NpcID.CAVE_HORROR, new WorldPoint(3740, 9373, 0),
			"Kill a Cave horror.", witchwoodIcon.equipped());
		caveHorror.addAlternateNpcs(NpcID.CAVE_HORROR_1048, NpcID.CAVE_HORROR_1049, NpcID.CAVE_HORROR_1050,
			NpcID.CAVE_HORROR_1051);
		moveToCaptMaho = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0),
			"Cross the gangplank to Bill Teach's ship.", witchwoodIcon.equipped(), lightSource, axe, tinderbox);
		moveToMosMaho = new NpcStep(this, NpcID.BILL_TEACH_4016, new WorldPoint(3714, 3497, 1),
			"Talk to Bill Teach to travel to Mos Le'Harmless.", witchwoodIcon.equipped(), lightSource, axe, tinderbox);
		moveToCaveMaho = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3650, new WorldPoint(3748, 2973, 0),
			"Enter the Mos Le'Harmless Cave.", witchwoodIcon.equipped(), lightSource, axe, tinderbox);
		moveToIsland = new ObjectStep(this, ObjectID.STAIRCASE_5269, new WorldPoint(3830, 9463, 0),
			"Climb the staircase in the north east of the Cave Horror dungeon.");
		burnMaho = new ItemStep(this, "Burn mahogany logs on the island.", tinderbox.highlighted(), mahoLogs.highlighted());
		chopMaho = new ObjectStep(this, 9034, new WorldPoint(3826, 3056, 0),
			"Chop mahogany logs on the island.", axe, tinderbox);

		moveToCaptMelon = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0),
			"Cross the gangplank to Bill Teach's ship.", watermelonSeeds.quantity(3), seedDibber, spade, rake);
		moveToMosMelon = new NpcStep(this, NpcID.BILL_TEACH_4016, new WorldPoint(3714, 3497, 1),
			"Talk to Bill Teach to travel to Mos Le'Harmless.", watermelonSeeds.quantity(3), seedDibber, spade, rake);
		moveToHarmony = new NpcStep(this, NpcID.BROTHER_TRANQUILITY_551, new WorldPoint(3680, 2961, 0),
			"Talk to Brother Tranquility to travel to Harmony Island.", watermelonSeeds.quantity(3), seedDibber, spade, rake);
		moveToHarmony.addDialogStep("Yes, please.");
		harvestWatermelon = new ObjectStep(this, NullObjectID.NULL_21950, new WorldPoint(3794, 2836, 0),
			"Plant and harvest watermelon on Harmony Island. It takes 80 minutes to fully grow. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
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
			teakPlank.quantity(3), lawRune.quantity(200), bloodRune.quantity(100), watermelonSeeds.quantity(3), seedDibber, spade, rake, axe,
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
		return Collections.singletonList("Kill a cave horror (lvl 80)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Morytania legs 3", ItemID.MORYTANIA_LEGS_3),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Unlimited teleports to Burgh de Rott from Morytania legs"),
			new UnlockReward("Bonecrusher - an item that automatically buries bones of monsters killed when carried"),
			new UnlockReward("Robin offers 26 free buckets of slime and 26 pots of bonemeal in exchange for bones each day"),
			new UnlockReward("Double Mort myre fungi when casting Bloom"),
			new UnlockReward("50% more Prayer experience from burning shade remains"),
			new UnlockReward("Access to a shortcut across the estuary on Mos Le'Harmless"),
			new UnlockReward("50% more runes from the Barrows chest"),
			new UnlockReward("7.5% more Slayer experience in the Slayer Tower while on a Slayer task")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails bitterSteps = new PanelDetails("Bittercap Mushrooms", Collections.singletonList(bittercapMush),
			new SkillRequirement(Skill.FARMING, 53, true), mushroomSpore, rake, seedDibber, spade);
		bitterSteps.setDisplayCondition(notBittercapMush);
		bitterSteps.setLockingStep(bittercapMushTask);
		allSteps.add(bitterSteps);

		PanelDetails watermelonsSteps = new PanelDetails("Harmony Watermelons", Arrays.asList(moveToCaptMelon,
			moveToMosMelon, moveToHarmony, harvestWatermelon), new SkillRequirement(Skill.FARMING, 47, true),
			theGreatBrainRobbery, watermelonSeeds.quantity(3), rake, seedDibber);
		watermelonsSteps.setDisplayCondition(notHarvestWatermelon);
		watermelonsSteps.setLockingStep(harvestWatermelonTask);
		allSteps.add(watermelonsSteps);

		PanelDetails mithSteps = new PanelDetails("Haunted Mithril Ore", Arrays.asList(moveToMine, moveToLevelTwo,
			mithOre), new SkillRequirement(Skill.MINING, 55, true), hauntedMine, pickaxe, crystalMineKey);
		mithSteps.setDisplayCondition(notMithOre);
		mithSteps.setLockingStep(mithOreTask);
		allSteps.add(mithSteps);

		PanelDetails pietySteps = new PanelDetails("Nature Grotto Piety", Arrays.asList(moveToGrotto, pietyAltar),
			natureSpirit, kingsRansom, knightWaves, new SkillRequirement(Skill.PRAYER, 70),
			new SkillRequirement(Skill.DEFENCE, 70));
		pietySteps.setDisplayCondition(notPietyAltar);
		pietySteps.setLockingStep(pietyAltarTask);
		allSteps.add(pietySteps);

		PanelDetails hardSteps = new PanelDetails("Hard Temple Trekk", Collections.singletonList(hardTempleTrekk));
		hardSteps.setDisplayCondition(notHardTempleTrekk);
		hardSteps.setLockingStep(hardTempleTrekkTask);
		allSteps.add(hardSteps);

		PanelDetails salveSteps = new PanelDetails("Shortcut Over the Salve", Collections.singletonList(bridgeSalve),
			new SkillRequirement(Skill.AGILITY, 65));
		salveSteps.setDisplayCondition(notBridgeSalve);
		salveSteps.setLockingStep(bridgeSalveTask);
		allSteps.add(salveSteps);

		PanelDetails spikeSteps = new PanelDetails("Advanced Spike Chain", Arrays.asList(moveToUpstairs,
			advancedSpikes), new SkillRequirement(Skill.AGILITY, 71, true), noseProtection, food);
		spikeSteps.setDisplayCondition(notAdvancedSpikes);
		spikeSteps.setLockingStep(advancedSpikesTask);
		allSteps.add(spikeSteps);

		PanelDetails caveHorrorSteps = new PanelDetails("Kill Cave Horror", Arrays.asList(moveToCaptHorror,
			moveToMosHorror, moveToCaveHorror, caveHorror), new SkillRequirement(Skill.SLAYER, 58, true),
			cabinFever, combatGear, witchwoodIcon.equipped(), lightSource, food);
		caveHorrorSteps.setDisplayCondition(notCaveHorror);
		caveHorrorSteps.setLockingStep(caveHorrorTask);
		allSteps.add(caveHorrorSteps);

		PanelDetails mahoSteps = new PanelDetails("Chop and Burn Mahogany", Arrays.asList(moveToCaptMaho, moveToMosMaho,
			moveToCaveMaho, moveToIsland, chopMaho, burnMaho), new SkillRequirement(Skill.FIREMAKING, 50),
			new SkillRequirement(Skill.WOODCUTTING, 50),
			axe, tinderbox, witchwoodIcon.equipped(), lightSource);
		mahoSteps.setDisplayCondition(notBurnMaho);
		mahoSteps.setLockingStep(burnMahoTask);
		allSteps.add(mahoSteps);

		PanelDetails kharyllSteps = new PanelDetails("Kharyll Portal", Collections.singletonList(kharyrll),
			new SkillRequirement(Skill.MAGIC, 66), new SkillRequirement(Skill.CONSTRUCTION, 50),
			desertTreasure, coins.quantity(100000), limestoneBrick.quantity(2), hammer, saw, teakPlank.quantity(3),
			lawRune.quantity(200), bloodRune.quantity(100));
		kharyllSteps.setDisplayCondition(notKharyrll);
		kharyllSteps.setLockingStep(kharyrllTask);
		allSteps.add(kharyllSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
