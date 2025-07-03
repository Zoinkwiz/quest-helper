/*
 * Copyright (c) 2022, rileyyy <https://github.com/rileyyy/> and Obasill <https://github.com/obasill/>
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
package com.questhelper.helpers.achievementdiaries.kourend;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KourendHard extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement adamantiteOre, coal, bootsOfStone, pickaxe, lightSource, xericsTalisman, lockpick, seedDibber,
		astralRune, cosmicRune, mindRune, wateringCan, spade, artifact, logavanoSeeds;

	// Items recommended
	ItemRequirement combatGear, food, antipoison, kharedstsMemoirs, dramenStaff, skillsNecklace, radasBlessing,
		shayzienHelmet, shayzienBody, shayzienGreaves, shayzienBoots, shayzienGloves;

	// Quests required
	Requirement dreamMentor, theForsakenTower, lunarBook;

	// Requirements
	Requirement notWoodcuttingGuild, notSmeltAddyBar, notKillLizardmanShaman, notMineLovakite, notPlantLogavano,
		notKillZombie, notTeleportHeart, notDeliverArtifact, notKillWyrm, notExamineMonster;

	QuestStep enterForsakenTower, smeltAddyBar, enterLizardmanTemple, killLizardmanShaman,
		mineLovakiteOre, searchSeedTable, enterTitheFarm, plantLogavanoSeed, entershayzienCrypt, killZombie,
		teleportToHeart, enterMountKaruulmDungeon,
		enterWyrmArea, killWyrm, claimReward;

	ObjectStep enterWoodcuttingGuild;

	NpcStep castMonsterExamine, talkToCaptainKhaled, deliverArtifact;

	ZoneRequirement inForsakenTower, inLizardmanTemple, inTitheFarm, inshayzienCrypt, inMountKaruulmDungeon, inWyrmArea;

	Zone forsakenTower, lizardmanTemple, titheFarm, shayzienCrypt, mountKaruulmDungeon, wyrmArea;

	ConditionalStep woodcuttingGuildTask, smeltAddyBarTask, killLizardmanShamanTask, mineLovakiteTask, plantLogavanoTask,
		killZombieTask, teleportHeartTask, deliverArtifactTask, killWyrmTask, examineMonsterTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doHard = new ConditionalStep(this, claimReward);

		deliverArtifactTask = new ConditionalStep(this, talkToCaptainKhaled);
		deliverArtifactTask.addStep(new Conditions(artifact), deliverArtifact);
		doHard.addStep(notDeliverArtifact, deliverArtifactTask);

		plantLogavanoTask = new ConditionalStep(this, searchSeedTable);
		plantLogavanoTask.addStep(logavanoSeeds, enterTitheFarm);
		plantLogavanoTask.addStep(inTitheFarm, plantLogavanoSeed);
		doHard.addStep(notPlantLogavano, plantLogavanoTask);

		woodcuttingGuildTask = new ConditionalStep(this, enterWoodcuttingGuild);
		doHard.addStep(notWoodcuttingGuild, woodcuttingGuildTask);

		killZombieTask = new ConditionalStep(this, entershayzienCrypt);
		killZombieTask.addStep(inshayzienCrypt, killZombie);
		doHard.addStep(notKillZombie, killZombieTask);

		mineLovakiteTask = new ConditionalStep(this, mineLovakiteOre);
		doHard.addStep(notMineLovakite, mineLovakiteTask);

		smeltAddyBarTask = new ConditionalStep(this, enterForsakenTower);
		smeltAddyBarTask.addStep(inForsakenTower, smeltAddyBar);
		doHard.addStep(notSmeltAddyBar, smeltAddyBarTask);

		killWyrmTask = new ConditionalStep(this, enterMountKaruulmDungeon);
		killWyrmTask.addStep(inMountKaruulmDungeon, enterWyrmArea);
		killWyrmTask.addStep(inWyrmArea, killWyrm);
		doHard.addStep(notKillWyrm, killWyrmTask);

		killLizardmanShamanTask = new ConditionalStep(this, enterLizardmanTemple);
		killLizardmanShamanTask.addStep(inLizardmanTemple, killLizardmanShaman);
		doHard.addStep(notKillLizardmanShaman, killLizardmanShamanTask);

		examineMonsterTask = new ConditionalStep(this, castMonsterExamine);
		doHard.addStep(notExamineMonster, examineMonsterTask);

		teleportHeartTask = new ConditionalStep(this, teleportToHeart);
		doHard.addStep(notTeleportHeart, teleportHeartTask);

		return doHard;
	}

	@Override
	protected void setupRequirements()
	{
		notWoodcuttingGuild = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY, false, 26);
		notSmeltAddyBar = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY, false, 27);
		notKillLizardmanShaman = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY, false, 28);
		notMineLovakite = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY, false, 29);
		notPlantLogavano = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY, false, 31);
		notKillZombie = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY, false, 30);
		notTeleportHeart = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY2, false, 0);
		notDeliverArtifact = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY2, false, 1);
		notKillWyrm = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY2, false, 2);
		notExamineMonster = new VarplayerRequirement(VarPlayerID.KOUREND_ACHIEVEMENT_DIARY2, false, 3);

		lunarBook = new SpellbookRequirement(Spellbook.LUNAR);

		// Items required
		adamantiteOre = new ItemRequirement("Adamantite ore", ItemID.ADAMANTITE_ORE).showConditioned(notSmeltAddyBar);
		coal = new ItemRequirement("Coal", ItemID.COAL).showConditioned(notSmeltAddyBar);
		bootsOfStone = new ItemRequirement("Boots of stone", ItemCollections.STONE_BOOTS).showConditioned(notKillWyrm).isNotConsumed();
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineLovakite).isNotConsumed();
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES)
			.showConditioned(notKillZombie).isNotConsumed();
		xericsTalisman = new ItemRequirement("Xeric's Talisman", Arrays.asList(ItemID.XERIC_TALISMAN,
			ItemID.POH_AMULET_XERIC)).showConditioned(notTeleportHeart).isNotConsumed();
		xericsTalisman.setTooltip("Obtained from Lizardmen as a rare drop and charged with a Lizardman fang");
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notDeliverArtifact).isNotConsumed();
		seedDibber = new ItemRequirement("Seed dibber", ItemID.DIBBER).showConditioned(notPlantLogavano).isNotConsumed();
		astralRune = new ItemRequirement("Astral rune", ItemID.ASTRALRUNE).showConditioned(notExamineMonster);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMICRUNE).showConditioned(notExamineMonster);
		mindRune = new ItemRequirement("Mind rune", ItemID.MINDRUNE).showConditioned(notExamineMonster);
		wateringCan = new ItemRequirement("Watering can", ItemCollections.WATERING_CANS)
			.showConditioned(notPlantLogavano).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notPlantLogavano).isNotConsumed();
		artifact = new ItemRequirement("Artifact", ItemID.SLICE_ARTIFACT_1_DIRTY).showConditioned(notDeliverArtifact);
		logavanoSeeds = new ItemRequirement("Logavano seeds", ItemID.HOSIDIUS_TITHE_SEED_C).showConditioned(notPlantLogavano);

		// Items recommended
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		antipoison = new ItemRequirement("Anti-poison", ItemCollections.ANTIPOISONS, -1)
			.showConditioned(notKillLizardmanShaman);
		dramenStaff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF, -1).isNotConsumed();
		kharedstsMemoirs = new ItemRequirement("Kharedst's Memoirs or Book of the Dead",
			Arrays.asList(ItemID.BOOK_OF_THE_DEAD, ItemID.VEOS_KHAREDSTS_MEMOIRS), -1).isNotConsumed();
		radasBlessing = new ItemRequirement("Rada's Blessing", Arrays.asList(ItemID.ZEAH_BLESSING_EASY,
			ItemID.ZEAH_BLESSING_MEDIUM), -1).showConditioned(notWoodcuttingGuild).isNotConsumed();
		skillsNecklace = new ItemRequirement("Skills necklace", ItemCollections.SKILLS_NECKLACES, -1).isNotConsumed();
		shayzienHelmet = new ItemRequirement("Shayzien Helmet (5)", ItemID.SHAYZIEN_HELM_5)
			.showConditioned(notKillLizardmanShaman).isNotConsumed();
		shayzienBody = new ItemRequirement("Shayzien Body (5)", ItemID.SHAYZIEN_BODY_5)
			.showConditioned(notKillLizardmanShaman).isNotConsumed();
		shayzienGreaves = new ItemRequirement("Shayzien Greaves (5)", ItemID.SHAYZIEN_LEGS_5)
			.showConditioned(notKillLizardmanShaman).isNotConsumed();
		shayzienBoots = new ItemRequirement("Shayzien Boots (5)", ItemID.SHAYZIEN_BOOTS_5)
			.showConditioned(notKillLizardmanShaman).isNotConsumed();
		shayzienGloves = new ItemRequirement("Shayzien Gloves (5)", ItemID.SHAYZIEN_GLOVES_5)
			.showConditioned(notKillLizardmanShaman).isNotConsumed();

		// Quests required
		dreamMentor = new QuestRequirement(QuestHelperQuest.DREAM_MENTOR, QuestState.FINISHED);
		theForsakenTower = new QuestRequirement(QuestHelperQuest.THE_FORSAKEN_TOWER, QuestState.FINISHED);


		// Zone requirements
		inForsakenTower = new ZoneRequirement(forsakenTower);
		inLizardmanTemple = new ZoneRequirement(lizardmanTemple);
		inTitheFarm = new ZoneRequirement(titheFarm);
		inshayzienCrypt = new ZoneRequirement(shayzienCrypt);
		inMountKaruulmDungeon = new ZoneRequirement(mountKaruulmDungeon);
		inWyrmArea = new ZoneRequirement(wyrmArea);
	}

	@Override
	protected void setupZones()
	{
		forsakenTower = new Zone(new WorldPoint(1377, 3829, 0), new WorldPoint(1388, 3817, 0));
		lizardmanTemple = new Zone(new WorldPoint(1289, 10100, 0), new WorldPoint(1333, 10067, 0));
		titheFarm = new Zone(new WorldPoint(13596, 4724, 0), new WorldPoint(13632, 4688, 0));
		shayzienCrypt = new Zone(new WorldPoint(1473, 9974, 3), new WorldPoint(1516, 9928, 3));
		mountKaruulmDungeon = new Zone(new WorldPoint(1303, 10210, 0), new WorldPoint(1320, 10187, 0));
		wyrmArea = new Zone(new WorldPoint(1255, 10207, 0), new WorldPoint(1302, 10174, 0));
	}

	public void setupSteps()
	{
		// Enter the woodcutting guild
		enterWoodcuttingGuild = new ObjectStep(this, ObjectID.WCGUILD_GATEL, new WorldPoint(1657, 3505, 0),
			"Enter the Woodcutting Guild.", true);
		enterWoodcuttingGuild.addAlternateObjects(ObjectID.WCGUILD_GATER);

		// Smelt an adamantite bar
		enterForsakenTower = new DetailedQuestStep(this, new WorldPoint(1382, 3818, 0),
			"Enter the Forsaken Tower.", adamantiteOre, coal.quantity(6));
		smeltAddyBar = new ObjectStep(this, 34591, "Smelt an adamantite bar.", adamantiteOre,
			coal.quantity(6));
		smeltAddyBar.addSubSteps(enterForsakenTower);

		// Kill a lizardman shaman
		enterLizardmanTemple = new ObjectStep(this, ObjectID.MOLCH_TEMPLE_LIZARD_DWELLING4, new WorldPoint(1312, 3686, 0),
			"Enter the lizardman temple.", shayzienHelmet.equipped(), shayzienBody.equipped(), shayzienGreaves.equipped(),
			shayzienBoots.equipped(), shayzienGloves.equipped(), antipoison, combatGear);
		killLizardmanShaman = new NpcStep(this, NpcID.MOLCH_LIZARDSHAMAN_1, new WorldPoint(1292, 10096, 0),
			"Kill a Lizardman Shaman.", shayzienHelmet.equipped(), shayzienBody.equipped(),
			shayzienGreaves.equipped(), shayzienBoots.equipped(), shayzienGloves.equipped(), antipoison, combatGear);

		// Mine some lovakite ore
		mineLovakiteOre = new ObjectStep(this, ObjectID.LOVAKITE_ROCK2, new WorldPoint(1438, 3816, 0),
			"Mine some lovakite ore.", pickaxe);

		// Plant some logavano seeds
		searchSeedTable = new ObjectStep(this, ObjectID.TITHE_PLANT_SEED_TABLE, new WorldPoint(1802, 3503, 0),
			"Take some logavano seeds from the table in the Tithe Farm entrance.");
		searchSeedTable.addDialogStep("Logavano seed (level 74)");
		enterTitheFarm = new DetailedQuestStep(this, new WorldPoint(1806, 3501, 0),
			"Enter the Tithe Farm.");
		plantLogavanoSeed = new ObjectStep(this, ObjectID.HOSIDIUS_TITHE_EMPTY, new WorldPoint(6692, 867, 0),
			"Plant the logavano seeds in any available farming patch.", true, seedDibber, spade, wateringCan,
			logavanoSeeds);
		plantLogavanoSeed.addIcon(ItemID.HOSIDIUS_TITHE_SEED_C);

		// Kill a zombie
		entershayzienCrypt = new ObjectStep(this, ObjectID.DS2_TOMB_ENTRY, new WorldPoint(1483, 3548, 0),
			"Enter the shayzien crypt.", lightSource);
		killZombie = new NpcStep(this, NpcID.DS2_ZOMBIE_RANGED, new WorldPoint(1491, 9949, 3),
			"Kill a zombie in the shayzien crypt.", combatGear, food);
		killZombie.addSubSteps(entershayzienCrypt);

		// Teleport to Xeric's heart
		teleportToHeart = new ItemStep(this, "Teleport to Xeric's Heart using the Xeric's Talisman",
			xericsTalisman.highlighted());

		// Deliver an artifact
		talkToCaptainKhaled = new NpcStep(this, NpcID.PISCARILIUS_CAPTAIN_KHALED_DEFAULT, new WorldPoint(1845, 3753, 0),
			"Talk to Captain Khaled to receive a job.");
		talkToCaptainKhaled.addAlternateNpcs(NpcID.PISCARILIUS_CAPTAIN_KHALED_TASK);
		talkToCaptainKhaled.addDialogStep("Looking for any help?");
		talkToCaptainKhaled.addDialogStep("I have what it takes.");
		deliverArtifact = new NpcStep(this, NpcID.PISCARILIUS_CAPTAIN_KHALED_DEFAULT, new WorldPoint(1845, 3753, 0),
			"Deliver the stolen artifact to Captain Khaled.", lockpick, artifact);

		// Kill a wyrm
		enterMountKaruulmDungeon = new ObjectStep(this, ObjectID.BRIMSTONE_ELEVATOR_CENTRAL_TILE, new WorldPoint(1311, 3807, 0),
			"Enter the Mount Karuulm Slayer Dungeon.", bootsOfStone.equipped(), combatGear, food);
		enterWyrmArea = new ObjectStep(this, ObjectID.KARUULM_SAFE_STEPOVER, new WorldPoint(1302, 10205, 0),
			"Enter the wyrm area.", bootsOfStone.equipped());
		killWyrm = new NpcStep(this, NpcID.WYRM_DARK, new WorldPoint(1282, 10189, 0),
			"Kill a wyrm.", combatGear, food);
		killWyrm.addSubSteps(enterMountKaruulmDungeon, enterWyrmArea);

		// Cast monster examine
		castMonsterExamine = new NpcStep(this, NpcID.DEATH_TROLL_MELEE1, new WorldPoint(1240, 3515, 0),
			"Cast Monster Examine on a Mountain Troll.", lunarBook, astralRune, cosmicRune, mindRune);
		castMonsterExamine.addAlternateNpcs(NpcID.DEATH_TROLL_MELEE2, NpcID.DEATH_TROLL_MELEE3,
			NpcID.DEATH_TROLL_MELEE4, NpcID.DEATH_TROLL_MELEE6);

		// Claim reward
		claimReward = new NpcStep(this, NpcID.ELISE_KOUREND_KEBOS_DIARY, new WorldPoint(1647, 3665, 0),
			"Talk to Elise in the Kourend castle courtyard to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList(
			"Kill a Lizardman Shaman (level 150)",
			"Kill a Zombie (level 132)",
			"Kill a Wyrm (level 99)");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(adamantiteOre, coal.quantity(6), bootsOfStone, pickaxe, lightSource, xericsTalisman,
			lockpick, seedDibber, astralRune.quantity(1), cosmicRune.quantity(1), mindRune.quantity(1), wateringCan,
			spade, shayzienHelmet, shayzienBody, shayzienGreaves, shayzienBoots, shayzienGloves);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food, antipoison, kharedstsMemoirs, dramenStaff, skillsNecklace, radasBlessing);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new CombatLevelRequirement(85));
		req.add(new SkillRequirement(Skill.FARMING, 74, true));
		req.add(new SkillRequirement(Skill.MAGIC, 66, true));
		req.add(new SkillRequirement(Skill.MINING, 65, true));
		req.add(new SkillRequirement(Skill.SLAYER, 62, true));
		req.add(new SkillRequirement(Skill.SMITHING, 70, true));
		req.add(new SkillRequirement(Skill.THIEVING, 49, false));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 60, true));

		req.add(dreamMentor);
		req.add(theForsakenTower);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Rada's Blessing (3)", ItemID.ZEAH_BLESSING_HARD, 1),
			new ItemReward("Ash sanctifier", ItemID.ASH_SANCTIFIER, 1),
			new ItemReward("15,000 Exp. Lamp (Any skill over 50)", ItemID.THOSF_REWARD_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Slayer helmet will work as a shayzien helm (5) after talking to Captain Cleive."),
			new UnlockReward("5% increased chance to save a harvest life at the Hosidius and Farming Guild herb patches."),
			new UnlockReward("40 free Dynamite per day from Thirus"),
			new UnlockReward("Reduced tanning prices at Eodan in Forthos Dungeon to 40%."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails deliverArtifactStep = new PanelDetails("Deliver an artifact", Arrays.asList(talkToCaptainKhaled,
			deliverArtifact), new SkillRequirement(Skill.THIEVING, 49, false), lockpick);
		deliverArtifactStep.setDisplayCondition(notDeliverArtifact);
		deliverArtifactStep.setLockingStep(deliverArtifactTask);
		allSteps.add(deliverArtifactStep);

		PanelDetails plantLogavanoStep = new PanelDetails("Plant some logavano seeds", Arrays.asList(searchSeedTable,
			enterTitheFarm, plantLogavanoSeed), new SkillRequirement(Skill.FARMING, 74, true), seedDibber, spade,
			wateringCan, logavanoSeeds);
		plantLogavanoStep.setDisplayCondition(notPlantLogavano);
		plantLogavanoStep.setLockingStep(plantLogavanoTask);
		allSteps.add(plantLogavanoStep);

		PanelDetails woodcuttingGuildStep = new PanelDetails("Enter the woodcutting guild",
			Collections.singletonList(enterWoodcuttingGuild), new SkillRequirement(Skill.WOODCUTTING, 60, true));
		woodcuttingGuildStep.setDisplayCondition(notWoodcuttingGuild);
		woodcuttingGuildStep.setLockingStep(woodcuttingGuildTask);
		allSteps.add(woodcuttingGuildStep);

		PanelDetails killZombieStep = new PanelDetails("Kill a zombie in the crypt", Arrays.asList(entershayzienCrypt,
			killZombie), lightSource, combatGear, food);
		killZombieStep.setDisplayCondition(notKillZombie);
		killZombieStep.setLockingStep(killZombieTask);
		allSteps.add(killZombieStep);

		PanelDetails mineLovakiteStep = new PanelDetails("Mine some lovakite", Collections.singletonList(mineLovakiteOre),
			pickaxe);
		mineLovakiteStep.setDisplayCondition(notMineLovakite);
		mineLovakiteStep.setLockingStep(mineLovakiteTask);
		allSteps.add(mineLovakiteStep);

		PanelDetails smeltBarStep = new PanelDetails("Smelt some adamantite", Arrays.asList(enterForsakenTower,
			smeltAddyBar), new SkillRequirement(Skill.SMITHING, 70, true), theForsakenTower, adamantiteOre, coal.quantity(6));
		smeltBarStep.setDisplayCondition(notSmeltAddyBar);
		smeltBarStep.setLockingStep(smeltAddyBarTask);
		allSteps.add(smeltBarStep);

		PanelDetails killWyrmStep = new PanelDetails("Slay a wyrm", Arrays.asList(enterMountKaruulmDungeon,
			enterWyrmArea, killWyrm), new SkillRequirement(Skill.SLAYER, 62, true), combatGear, food, bootsOfStone);
		killWyrmStep.setDisplayCondition(notKillWyrm);
		killWyrmStep.setLockingStep(killWyrmTask);
		allSteps.add(killWyrmStep);

		PanelDetails killShamanStep = new PanelDetails("Kill a lizardman shaman", Arrays.asList(enterLizardmanTemple,
			killLizardmanShaman), shayzienHelmet, shayzienBody, shayzienGreaves, shayzienBoots, shayzienGloves,
			antipoison, combatGear);
		killShamanStep.setDisplayCondition(notKillLizardmanShaman);
		killShamanStep.setLockingStep(killLizardmanShamanTask);
		allSteps.add(killShamanStep);

		PanelDetails examineMonsterStep = new PanelDetails("Cast Monster Examine",
			Collections.singletonList(castMonsterExamine), new SkillRequirement(Skill.MAGIC, 66, true), dreamMentor,
			lunarBook, mindRune, astralRune, cosmicRune);
		examineMonsterStep.setDisplayCondition(notExamineMonster);
		examineMonsterStep.setLockingStep(examineMonsterTask);
		allSteps.add(examineMonsterStep);

		PanelDetails teleportHeartStep = new PanelDetails("Teleport to Xeric's Heart", Collections.singletonList(
			teleportToHeart), xericsTalisman);
		teleportHeartStep.setDisplayCondition(notTeleportHeart);
		teleportHeartStep.setLockingStep(teleportHeartTask);
		allSteps.add(teleportHeartStep);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
