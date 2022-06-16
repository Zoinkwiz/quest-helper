/*
 * Copyright (c) 2022, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.westernprovinces;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
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
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WESTERN_HARD
)
public class WesternHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, crystalBow, smallFishingNet, coins, ninjaGreegree, axe, tinderbox, pickaxe, palmSapling,
		ogreBellows, ogreBow, ogreArrows, mahoganyPlank, painting, saw, hammer, lawRunes2, fireRunes2,
		waterRunes2, banana, rake, spade, rawMonkfish, birdReady, mahoganyLogs;

	// Items recommended
	ItemRequirement food, papayas, zulrahTP, tpCrystal;

	// Quests required
	Requirement monkeyMadnessI, rovingElves, swanSong, regicide, mourningsEndPartI, bigChompy, awowogeiRFD,
		treeGnomeVillage, normalBook, choppedLogs, caughtMonkfish;

	Requirement notElfCystalBow, notMonkfishPisc, notVetPest, notDashingKebbit, notApeAtollAgi, notMahoganyBurned,
		notMineAddyOre, notLletyaPalm, notChompyHat, notIsafdarPainting, notKillZulrah, notTPApe, notPickpocketGnome;

	QuestStep claimReward, elfCrystalBow, monkfishPisc, vetPest, dashingKebbit, apeAtollAgi, mahoganyBurned,
		mahoganyChopped, lletyaPalm, chompyHat, isafdarPainting, killZulrah, tpApe, pickpocketGnome,
		fishMonkfish, moveToApeMahogany, moveToApeAgi, moveToPest;

	NpcStep getBird;

	ObjectStep mineAddyOre;

	Zone pest, apeAtoll, pisc;

	ZoneRequirement inPest, inApeAtoll, inPisc;

	ConditionalStep elfCystalBowTask, monkfishPiscTask, vetPestTask, dashingKebbitTask, apeAtollAgiTask, mahoganyBurnedTask,
		mineAddyOreTask, lletyaPalmTask, chompyHatTask, isafdarPaintingTask, killZulrahTask, tpApeTask, pickpocketGnomeTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		lletyaPalmTask = new ConditionalStep(this, lletyaPalm);
		doHard.addStep(notLletyaPalm, lletyaPalmTask);

		monkfishPiscTask = new ConditionalStep(this, fishMonkfish);
		monkfishPiscTask.addStep(new Conditions(rawMonkfish, caughtMonkfish), monkfishPisc);
		doHard.addStep(notMonkfishPisc, monkfishPiscTask);

		dashingKebbitTask = new ConditionalStep(this, getBird);
		dashingKebbitTask.addStep(birdReady, dashingKebbit);
		doHard.addStep(notDashingKebbit, dashingKebbitTask);

		pickpocketGnomeTask = new ConditionalStep(this, pickpocketGnome);
		doHard.addStep(notPickpocketGnome, pickpocketGnomeTask);

		tpApeTask = new ConditionalStep(this, tpApe);
		doHard.addStep(notTPApe, tpApeTask);

		mahoganyBurnedTask = new ConditionalStep(this, moveToApeMahogany);
		mahoganyBurnedTask.addStep(inApeAtoll, mahoganyChopped);
		mahoganyBurnedTask.addStep(new Conditions(inApeAtoll, mahoganyLogs, choppedLogs), mahoganyBurned);
		doHard.addStep(notMahoganyBurned, mahoganyBurnedTask);

		apeAtollAgiTask = new ConditionalStep(this, moveToApeAgi);
		apeAtollAgiTask.addStep(inApeAtoll, apeAtollAgi);
		doHard.addStep(notApeAtollAgi, apeAtollAgiTask);

		killZulrahTask = new ConditionalStep(this, killZulrah);
		doHard.addStep(notKillZulrah, killZulrahTask);

		mineAddyOreTask = new ConditionalStep(this, mineAddyOre);
		doHard.addStep(notMineAddyOre, mineAddyOreTask);

		elfCystalBowTask = new ConditionalStep(this, elfCrystalBow);
		doHard.addStep(notElfCystalBow, elfCystalBowTask);

		vetPestTask = new ConditionalStep(this, moveToPest);
		vetPestTask.addStep(inPest, vetPest);
		doHard.addStep(notVetPest, vetPestTask);

		isafdarPaintingTask = new ConditionalStep(this, isafdarPainting);
		doHard.addStep(notIsafdarPainting, isafdarPaintingTask);

		chompyHatTask = new ConditionalStep(this, chompyHat);
		doHard.addStep(notChompyHat, chompyHatTask);

		return doHard;
	}

	@Override
	public void setupRequirements()
	{
		notElfCystalBow = new VarplayerRequirement(1182, false, 25);
		notMonkfishPisc = new VarplayerRequirement(1182, false, 26);
		notVetPest = new VarplayerRequirement(1182, false, 27);
		notDashingKebbit = new VarplayerRequirement(1182, false, 28);
		notApeAtollAgi = new VarplayerRequirement(1182, false, 29);
		notMahoganyBurned = new VarplayerRequirement(1182, false, 30);
		notMineAddyOre = new VarplayerRequirement(1182, false, 31);
		notLletyaPalm = new VarplayerRequirement(1183, false, 0);
		notChompyHat = new VarplayerRequirement(1183, false, 1);
		notIsafdarPainting = new VarplayerRequirement(1183, false, 2);
		notKillZulrah = new VarplayerRequirement(1183, false, 3);
		notTPApe = new VarplayerRequirement(1183, false, 4);
		notPickpocketGnome = new VarplayerRequirement(1183, false, 5);


		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		crystalBow = new ItemRequirement("Crystal bow", ItemCollections.CRYSTAL_BOW).showConditioned(notElfCystalBow);
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET).showConditioned(notMonkfishPisc);
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 500).showConditioned(notDashingKebbit);
		ninjaGreegree = new ItemRequirement("Ninja greegree", ItemCollections.NINJA_GREEGREE).showConditioned(notApeAtollAgi);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notMahoganyBurned);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(notMahoganyBurned);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineAddyOre);
		mahoganyPlank = new ItemRequirement("Mahogany planks", ItemID.MAHOGANY_PLANK, 3)
			.showConditioned(notIsafdarPainting);
		painting = new ItemRequirement("Isafdar (painting)", ItemID.ISAFDAR_PAINTING).showConditioned(notIsafdarPainting);
		painting.setTooltip("Can be bought from Sir Renitee in White Knights' Castle for 2,000 gold ");
		saw = new ItemRequirement("Saw", ItemCollections.SAW).showConditioned(notIsafdarPainting);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notIsafdarPainting);
		lawRunes2 = new ItemRequirement("Law runes", ItemID.LAW_RUNE, 2).showConditioned(notTPApe);
		fireRunes2 = new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 2).showConditioned(notTPApe);
		waterRunes2 = new ItemRequirement("Water runes", ItemID.WATER_RUNE, 2).showConditioned(notTPApe);
		banana = new ItemRequirement("Banana", ItemID.BANANA).showConditioned(notTPApe);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notLletyaPalm);
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notLletyaPalm);
		palmSapling = new ItemRequirement("Palm sapling", ItemID.PALM_SAPLING).showConditioned(notLletyaPalm);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineAddyOre);
		ogreBellows = new ItemRequirement("Ogre bellows", ItemCollections.OGRE_BELLOWS).showConditioned(notChompyHat);
		ogreBow = new ItemRequirement("Ogre bow", ItemCollections.OGRE_BOW).showConditioned(notChompyHat);
		ogreArrows = new ItemRequirement("Ogre / brutal arrows", ItemCollections.OGRE_BRUTAL_ARROWS).showConditioned(notChompyHat);
		mahoganyLogs = new ItemRequirement("Mahogany logs", ItemID.MAHOGANY_LOGS);
		rawMonkfish = new ItemRequirement("Raw monkfish", ItemID.RAW_MONKFISH);
		birdReady = new ItemRequirement("Falconer's glove", ItemID.FALCONERS_GLOVE);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		papayas = new ItemRequirement("Papayas", ItemID.PAPAYA_FRUIT, 15);
		zulrahTP = new ItemRequirement("Zul-andra teleport", ItemID.ZULANDRA_TELEPORT);
		tpCrystal = new ItemRequirement("Teleport Crystal", ItemCollections.TELEPORT_CRYSTAL);

		inApeAtoll = new ZoneRequirement(apeAtoll);
		inPest = new ZoneRequirement(pest);
		inPisc = new ZoneRequirement(pisc);

		caughtMonkfish = new ChatMessageRequirement(
			inPisc,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) caughtMonkfish).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inPisc),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		choppedLogs = new ChatMessageRequirement(
			inApeAtoll,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) choppedLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inApeAtoll),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		monkeyMadnessI = new QuestRequirement(QuestHelperQuest.MONKEY_MADNESS_I, QuestState.IN_PROGRESS);
		rovingElves = new QuestRequirement(QuestHelperQuest.ROVING_ELVES, QuestState.FINISHED);
		swanSong = new QuestRequirement(QuestHelperQuest.SWAN_SONG, QuestState.FINISHED);
		regicide = new QuestRequirement(QuestHelperQuest.REGICIDE, QuestState.IN_PROGRESS);
		mourningsEndPartI = new QuestRequirement(QuestHelperQuest.MOURNINGS_END_PART_I, QuestState.IN_PROGRESS);
		bigChompy = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED);
		awowogeiRFD = new QuestRequirement(QuestHelperQuest.RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR, QuestState.FINISHED);
		treeGnomeVillage = new QuestRequirement(QuestHelperQuest.TREE_GNOME_VILLAGE, QuestState.FINISHED);
	}

	public void loadZones()
	{
		pest = new Zone(new WorldPoint(2631, 2681, 0), new WorldPoint(2683, 2626, 0));
		apeAtoll = new Zone(new WorldPoint(2687, 2814, 0), new WorldPoint(2817, 2686, 0));
		pisc = new Zone(new WorldPoint(2306, 3707, 0), new WorldPoint(2364, 3661, 0));
	}

	public void setupSteps()
	{
		fishMonkfish = new NpcStep(this, NpcID.FISHING_SPOT_4316, new WorldPoint(2340, 3702, 0),
			"Catch a monkfish in the Piscatoris Fishing Colony.", smallFishingNet);
		monkfishPisc = new ObjectStep(this, ObjectID.RANGE_12611, new WorldPoint(2316, 3668, 0),
			"Cook your monkfish in the Piscatoris Fishing Colony.", rawMonkfish);

		getBird = new NpcStep(this, NpcID.MATTHIAS, new WorldPoint(2375, 3607, 0),
			"Talk to Matthias to get a bird. Make sure to unequip any gloves and weapons.", coins);
		getBird.addAlternateNpcs(NpcID.MATTHIAS_1341);
		getBird.addDialogSteps("Could I have a go with your bird?", "Ok, that seems reasonable.");
		dashingKebbit = new NpcStep(this, NpcID.DASHING_KEBBIT, new WorldPoint(2372, 3581, 0),
			"Catch a dashing kebbit with your falcon!");

		pickpocketGnome = new NpcStep(this, NpcID.GNOME_6094, new WorldPoint(2466, 3460, 0), "Pickpocket a gnome.",
			true);
		((NpcStep) pickpocketGnome).setMaxRoamRange(2000);
		((NpcStep) pickpocketGnome).addAlternateNpcs(NpcID.GNOME_6095, NpcID.GNOME_6096, NpcID.GNOME_WOMAN,
			NpcID.GNOME_WOMAN_6087);

		tpApe = new DetailedQuestStep(this, "Cast teleport to Ape Atoll.", normalBook, lawRunes2, fireRunes2,
			waterRunes2, banana);

		moveToApeMahogany = new DetailedQuestStep(this, "Travel to Ape Atoll.", axe, tinderbox);
		mahoganyChopped = new ObjectStep(this, ObjectID.MAHOGANY, new WorldPoint(2718, 2710, 0),
			"Chop some mahogany logs on Ape Atoll.", axe, tinderbox);
		mahoganyBurned = new ItemStep(this, "Burn some mahogany logs on Ape Atoll.",
			mahoganyLogs.highlighted(), tinderbox.highlighted());

		moveToApeAgi = new DetailedQuestStep(this, "Travel to Ape Atoll.", ninjaGreegree);
		apeAtollAgi = new ObjectStep(this, ObjectID.STEPPING_STONE_15412, new WorldPoint(2754, 2742, 0),
			" Complete a lap of the Ape Atoll Agility Course.", ninjaGreegree.equipped());

		killZulrah = new ObjectStep(this, ObjectID.SACRIFICIAL_BOAT, new WorldPoint(2215, 3057, 0),
			"Kill Zulrah.", combatGear, food);

		mineAddyOre = new ObjectStep(this, ObjectID.ROCKS_11374, new WorldPoint(2277, 3160, 0),
			"Mine some adamantite ore in Tirannwn.", true, pickaxe);
		mineAddyOre.addAlternateObjects(ObjectID.ROCKS_11375);

		elfCrystalBow = new NpcStep(this, NpcID.ELF_ARCHER, new WorldPoint(2333, 3171, 0), "Kill an elf with a crystal" +
			" bow.", true, crystalBow.equipped());
		((NpcStep) elfCrystalBow).addAlternateNpcs(NpcID.ELF_ARCHER_5296, NpcID.ELF_WARRIOR, NpcID.ELF_WARRIOR_5294);
		((NpcStep) elfCrystalBow).setMaxRoamRange(2000);

		isafdarPainting = new DetailedQuestStep(this, "Build an Isafdar painting in your POH Quest Hall.",
			mahoganyPlank, painting, saw, hammer);

		moveToPest = new NpcStep(this, NpcID.SQUIRE_1770, new WorldPoint(3041, 3202, 0),
			"Talk to the squire to travel to the Void Knights' Outpost. Alternatively, use the pest control minigame teleport.");
		moveToPest.addDialogStep("I'd like to go to your outpost.");
		vetPest = new ObjectStep(this, ObjectID.GANGPLANK_25632, new WorldPoint(2637, 2653, 0),
			"Complete a veteran game of Pest Control.");

		chompyHat = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2628, 2979, 0),
			"Claim any Chompy bird hat from Rantz. Kill chompy birds until you have 300 kills. \n \nYou can check " +
				"your kill count by right clicking selecting 'Check Kills' on an ogre bow.",
			ogreBow, ogreArrows, ogreBellows);
		chompyHat.addDialogStep("Can I have a hat please?");

		lletyaPalm = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2346, 3161, 0),
			"Check the health of your palm tree in Lletya. It will take about 16 hours to grow fully. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.", rake, spade,
			palmSapling);

		claimReward = new NpcStep(this, NpcID.ELDER_GNOME_CHILD, new WorldPoint(2466, 3460, 0),
			"Talk to the Elder gnome child in Gnome Stronghold to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, crystalBow, smallFishingNet, coins, ninjaGreegree, axe, tinderbox, pickaxe, palmSapling,
			ogreBellows, ogreBow, ogreArrows, mahoganyPlank, painting, saw, hammer, lawRunes2, fireRunes2,
			waterRunes2, banana, rake, spade);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, papayas, zulrahTP, tpCrystal);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(100));
		reqs.add(new SkillRequirement(Skill.AGILITY, 48));
		reqs.add(new SkillRequirement(Skill.CONSTRUCTION, 65));
		reqs.add(new SkillRequirement(Skill.COOKING, 62));
		reqs.add(new SkillRequirement(Skill.FARMING, 68));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 50));
		reqs.add(new SkillRequirement(Skill.FISHING, 62));
		reqs.add(new SkillRequirement(Skill.HUNTER, 69));
		reqs.add(new SkillRequirement(Skill.RANGED, 70));
		reqs.add(new SkillRequirement(Skill.MAGIC, 64));
		reqs.add(new SkillRequirement(Skill.MINING, 70));
		reqs.add(new SkillRequirement(Skill.THIEVING, 75));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 50));

		reqs.add(mourningsEndPartI);
		reqs.add(awowogeiRFD);
		reqs.add(swanSong);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Kill Zulrah (lvl 725) and an elf (lvl 108)");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Western banner 3", ItemID.WESTERN_BANNER_3),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("One free daily teleport to the Piscatoris Fishing Colony from Western banner 3"),
			new UnlockReward("Ability to upgrade void knight armour into elite void knight armour"),
			new UnlockReward("Access to the monkey skull room on Ape Atoll"),
			new UnlockReward("Access to the Hunter master's private red chinchompa hunting ground"),
			new UnlockReward("100 free ogre arrows every day from Rantz"),
			new UnlockReward("Teleport crystals can hold up to 5 charges."),
			new UnlockReward("Ability to purchase a crystal halberd from Islwyn for 750,000 coins.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails palmSteps = new PanelDetails("Lletya Palm tree", Collections.singletonList(lletyaPalm),
			new SkillRequirement(Skill.FARMING, 68), mourningsEndPartI, palmSapling, rake, spade);
		palmSteps.setDisplayCondition(notLletyaPalm);
		palmSteps.setLockingStep(lletyaPalmTask);
		allSteps.add(palmSteps);

		PanelDetails fishSteps = new PanelDetails("Catch And Cook Monkfish", Arrays.asList(fishMonkfish, monkfishPisc),
			new SkillRequirement(Skill.COOKING, 62), new SkillRequirement(Skill.FISHING, 62),
			swanSong, smallFishingNet);
		fishSteps.setDisplayCondition(notMonkfishPisc);
		fishSteps.setLockingStep(monkfishPiscTask);
		allSteps.add(fishSteps);

		PanelDetails kebbitSteps = new PanelDetails("Dashing Kebbit", Arrays.asList(getBird, dashingKebbit),
			new SkillRequirement(Skill.HUNTER, 69), coins);
		kebbitSteps.setDisplayCondition(notDashingKebbit);
		kebbitSteps.setLockingStep(dashingKebbitTask);
		allSteps.add(kebbitSteps);

		PanelDetails gnomeSteps = new PanelDetails("Pickpocket A Gnome", Collections.singletonList(pickpocketGnome),
			new SkillRequirement(Skill.THIEVING, 75), treeGnomeVillage);
		gnomeSteps.setDisplayCondition(notPickpocketGnome);
		gnomeSteps.setLockingStep(pickpocketGnomeTask);
		allSteps.add(gnomeSteps);

		PanelDetails tpSteps = new PanelDetails("Teleport To Ape Atoll", Collections.singletonList(tpApe),
			new SkillRequirement(Skill.MAGIC, 64), awowogeiRFD, normalBook, lawRunes2, fireRunes2, waterRunes2, banana);
		tpSteps.setDisplayCondition(notTPApe);
		tpSteps.setLockingStep(tpApeTask);
		allSteps.add(tpSteps);

		PanelDetails mahoSteps = new PanelDetails("Mahogany On Ape Atoll", Arrays.asList(moveToApeMahogany,
			mahoganyChopped, mahoganyBurned), new SkillRequirement(Skill.FIREMAKING, 50),
			new SkillRequirement(Skill.WOODCUTTING, 50), monkeyMadnessI, axe, tinderbox);
		mahoSteps.setDisplayCondition(notMahoganyBurned);
		mahoSteps.setLockingStep(mahoganyBurnedTask);
		allSteps.add(mahoSteps);

		PanelDetails agiSteps = new PanelDetails("Ape Atoll Agility Course", Arrays.asList(moveToApeAgi, apeAtollAgi),
			new SkillRequirement(Skill.AGILITY, 48), monkeyMadnessI, ninjaGreegree);
		agiSteps.setDisplayCondition(notApeAtollAgi);
		agiSteps.setLockingStep(apeAtollAgiTask);
		allSteps.add(agiSteps);

		PanelDetails zulSteps = new PanelDetails("Kill Zulrah", Collections.singletonList(killZulrah), regicide,
			combatGear);
		zulSteps.setDisplayCondition(notKillZulrah);
		zulSteps.setLockingStep(killZulrahTask);
		allSteps.add(zulSteps);

		PanelDetails addySteps = new PanelDetails("Mine Adamantite in Tirannwn", Collections.singletonList(mineAddyOre),
			new SkillRequirement(Skill.MINING, 70), regicide, pickaxe);
		addySteps.setDisplayCondition(notMineAddyOre);
		addySteps.setLockingStep(mineAddyOreTask);
		allSteps.add(addySteps);

		PanelDetails elfSteps = new PanelDetails("Kill Elf With Crystal Bow", Collections.singletonList(elfCrystalBow));
		elfSteps.setDisplayCondition(notElfCystalBow);
		elfSteps.setLockingStep(elfCystalBowTask);
		allSteps.add(elfSteps);

		PanelDetails pestSteps = new PanelDetails("Veteran Pest Control", Arrays.asList(moveToPest, vetPest),
			new CombatLevelRequirement(100), combatGear);
		pestSteps.setDisplayCondition(notVetPest);
		pestSteps.setLockingStep(vetPestTask);
		allSteps.add(pestSteps);

		PanelDetails isaSteps = new PanelDetails("Isafdar Painting In POH", Collections.singletonList(isafdarPainting),
			new SkillRequirement(Skill.CONSTRUCTION, 65), rovingElves, mahoganyPlank, painting, saw, hammer);
		isaSteps.setDisplayCondition(notIsafdarPainting);
		isaSteps.setLockingStep(isafdarPaintingTask);
		allSteps.add(isaSteps);

		PanelDetails hatSteps = new PanelDetails("Chompy Bird Hat", Collections.singletonList(chompyHat),
			new SkillRequirement(Skill.RANGED, 30), bigChompy, ogreBellows, ogreBow, ogreArrows);
		hatSteps.setDisplayCondition(notChompyHat);
		hatSteps.setLockingStep(chompyHatTask);
		allSteps.add(hatSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
