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

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.KOUREND_ELITE
)

public class KourendElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement pickaxe, chisel, axe, darkTotem, totemBase, totemMiddle, totemTop, denseEssenceBlock,
		bloodRune, lawRune, soulRune, fishingRod, sandworm, celastrusSapling, combatGear, food, prayerPotion,
		celastrusBark, darkEssenceBlock, darkEssenceFragment, rawAnglerfish, knife, bootsOfStone, spade;

	// Items recommended
	ItemRequirement arclight, kharedstsMemoirs, dramenStaff, skillsNecklace, radasBlessing, xericsTalisman,
		potatoCactus, ultraCompost;

	// Quests required
	Requirement hosidiusFavour60, hosidiusFavour75, arceuusFavour, piscariliusFavour;

	// Requirements
	Requirement notCraftBloodRune, notChopRedwood, notDefeatSkotizo, notCatchAngler, notKillHydra, notCreateTeleport,
		notCompleteRaid, notFletchBattlestaff, onArceuusSpellbook, anglerCaught, barkHarvested;

	QuestStep craftBloodRune, enterCatacombs, enterSkotizoLair, defeatSkotizo, catchAngler, killHydra, createTeleportTab,
		completeRaid, plantCelastrusTree, fletchBattlestaff, enterHydraArea, enterMountKaruulmDungeon,
		climbRedwoodTree, cookAnglerfish, combineDarkTotem, claimReward, bloodMineDenseEssence, bloodVenerateEssenceBlock,
		chiselEssenceBlock, apeMineDenseEssence, apeVenerateEssenceBlock;

	ObjectStep enterWoodcuttingGuild, chopRedwood;

	NpcStep switchSpellbook;

	ZoneRequirement inRedwoodTree, inCatacombs, inSkotizoLair, inWoodcuttingGuild, inMountKaruulmDungeon, inHydraArea,
		inFish, inFarming;

	Zone redwoodTree, catacombs, skotizoLair, woodcuttingGuild, mountKaruulmDungeon, hydraArea, fish, farming;

	ConditionalStep craftBloodRuneTask, chopRedwoodTask, defeatSkotizoTask, catchAnglerTask, killHydraTask,
		createTeleportTask, completeRaidTask, fletchBattlestaffTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		fletchBattlestaffTask = new ConditionalStep(this, plantCelastrusTree);
		fletchBattlestaffTask.addStep(barkHarvested, fletchBattlestaff);
		doElite.addStep(notFletchBattlestaff, fletchBattlestaffTask);

		craftBloodRuneTask = new ConditionalStep(this, bloodMineDenseEssence);
		craftBloodRuneTask.addStep(denseEssenceBlock, bloodVenerateEssenceBlock);
		craftBloodRuneTask.addStep(darkEssenceBlock, chiselEssenceBlock);
		craftBloodRuneTask.addStep(darkEssenceFragment, craftBloodRune);
		doElite.addStep(notCraftBloodRune, craftBloodRuneTask);

		createTeleportTask = new ConditionalStep(this, apeMineDenseEssence);
		createTeleportTask.addStep(denseEssenceBlock, apeVenerateEssenceBlock);
		createTeleportTask.addStep(darkEssenceBlock, switchSpellbook);
		createTeleportTask.addStep(onArceuusSpellbook, createTeleportTab);
		doElite.addStep(notCreateTeleport, createTeleportTask);

		killHydraTask = new ConditionalStep(this, enterMountKaruulmDungeon);
		killHydraTask.addStep(inMountKaruulmDungeon, enterHydraArea);
		killHydraTask.addStep(inHydraArea, killHydra);
		doElite.addStep(notKillHydra, killHydraTask);

		chopRedwoodTask = new ConditionalStep(this, enterWoodcuttingGuild);
		chopRedwoodTask.addStep(inWoodcuttingGuild, climbRedwoodTree);
		chopRedwoodTask.addStep(inRedwoodTree, chopRedwood);
		doElite.addStep(notChopRedwood, chopRedwoodTask);

		catchAnglerTask = new ConditionalStep(this, catchAngler);
		catchAnglerTask.addStep(new Conditions(rawAnglerfish, anglerCaught), cookAnglerfish);
		doElite.addStep(notCatchAngler, catchAnglerTask);

		completeRaidTask = new ConditionalStep(this, completeRaid);
		doElite.addStep(notCompleteRaid, completeRaidTask);

		defeatSkotizoTask = new ConditionalStep(this, combineDarkTotem);
		defeatSkotizoTask.addStep(darkTotem.alsoCheckBank(questBank), enterCatacombs);
		defeatSkotizoTask.addStep(new Conditions(darkTotem.alsoCheckBank(questBank), inCatacombs), enterSkotizoLair);
		defeatSkotizoTask.addStep(inSkotizoLair, defeatSkotizo);
		doElite.addStep(notDefeatSkotizo, defeatSkotizoTask);

		return doElite;
	}

	@Override
	public void setupRequirements()
	{
		notCraftBloodRune = new VarplayerRequirement(2086, false, 4);
		notChopRedwood = new VarplayerRequirement(2086, false, 5);
		notDefeatSkotizo = new VarplayerRequirement(2086, false, 6);
		notCatchAngler = new VarplayerRequirement(2086, false, 7);
		notKillHydra = new VarplayerRequirement(2086, false, 8);
		notCreateTeleport = new VarplayerRequirement(2086, false, 9);
		notCompleteRaid = new VarplayerRequirement(2086, false, 10);
		notFletchBattlestaff = new VarplayerRequirement(2086, false, 11);

		onArceuusSpellbook = new SpellbookRequirement(Spellbook.ARCEUUS);

		// Items required
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES)
			.showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport)).isNotConsumed();
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL)
			.showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport)).isNotConsumed();
		chisel.setTooltip("One can be found in the Arceuus essence mine.");
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notChopRedwood).isNotConsumed();
		darkTotem = new ItemRequirement("Dark Totem", ItemID.DARK_TOTEM).showConditioned(notDefeatSkotizo);
		totemBase = new ItemRequirement("Dark totem base", ItemID.DARK_TOTEM_BASE);
		totemMiddle = new ItemRequirement("Dark totem middle", ItemID.DARK_TOTEM_MIDDLE);
		totemTop = new ItemRequirement("Dark totem top", ItemID.DARK_TOTEM_TOP);
		denseEssenceBlock = new ItemRequirement("Dense essence block", ItemID.DENSE_ESSENCE_BLOCK)
			.showConditioned(new Conditions(LogicType.OR, notCraftBloodRune, notCreateTeleport));
		bloodRune = new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2).showConditioned(notCreateTeleport);
		lawRune = new ItemRequirement("Law runes", ItemID.LAW_RUNE, 2).showConditioned(notCreateTeleport);
		soulRune = new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2).showConditioned(notCreateTeleport);
		fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD).showConditioned(notCatchAngler).isNotConsumed();
		sandworm = new ItemRequirement("Sandworms", ItemID.SANDWORMS).showConditioned(notCatchAngler);
		celastrusSapling = new ItemRequirement("Celastrus sapling", ItemID.CELASTRUS_SAPLING).showConditioned(notFletchBattlestaff);
		knife = new ItemRequirement("Knife", ItemID.KNIFE).showConditioned(notFletchBattlestaff).isNotConsumed();
		combatGear = new ItemRequirement("Combat gear", -1, -1)
			.showConditioned(new Conditions(LogicType.OR, notDefeatSkotizo, notKillHydra, notCompleteRaid)).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD)
			.showConditioned(new Conditions(LogicType.OR, notDefeatSkotizo, notKillHydra, notCompleteRaid));
		prayerPotion = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS)
			.showConditioned(new Conditions(LogicType.OR, notDefeatSkotizo, notKillHydra, notCompleteRaid));
		celastrusBark = new ItemRequirement("Celastrus bark", ItemID.CELASTRUS_BARK);
		darkEssenceBlock = new ItemRequirement("Dark essence block", ItemID.DARK_ESSENCE_BLOCK);
		darkEssenceFragment = new ItemRequirement("Dark essence fragments", ItemID.DARK_ESSENCE_FRAGMENTS);
		rawAnglerfish = new ItemRequirement("Raw anglerfish", ItemID.RAW_ANGLERFISH);
		bootsOfStone = new ItemRequirement("Boots of stone", ItemCollections.STONE_BOOTS).showConditioned(notKillHydra).isNotConsumed();
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(notFletchBattlestaff).isNotConsumed();

		// Items recommended
		arclight = new ItemRequirement("Arclight", ItemID.ARCLIGHT).showConditioned(notDefeatSkotizo).isNotConsumed();
		kharedstsMemoirs = new ItemRequirement("Kharedst's Memoirs or Book of the Dead",
			Arrays.asList(ItemID.BOOK_OF_THE_DEAD, ItemID.KHAREDSTS_MEMOIRS)).isNotConsumed();
		xericsTalisman = new ItemRequirement("Xeric's Talisman", ItemID.XERICS_TALISMAN).isNotConsumed();
		dramenStaff = new ItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF).isNotConsumed();
		radasBlessing = new ItemRequirement("Rada's Blessing", Arrays.asList(ItemID.RADAS_BLESSING_1,
			ItemID.RADAS_BLESSING_2, ItemID.RADAS_BLESSING_3)).isNotConsumed();
		skillsNecklace = new ItemRequirement("Skills necklace", ItemCollections.SKILLS_NECKLACES).isNotConsumed();
		potatoCactus = new ItemRequirement("Potato cactus", ItemID.POTATO_CACTUS, 8);
		ultraCompost = new ItemRequirement("Compost", ItemCollections.COMPOST);

		arceuusFavour = new FavourRequirement(Favour.ARCEUUS, 100);
		hosidiusFavour60 = new FavourRequirement(Favour.HOSIDIUS, 60);
		hosidiusFavour75 = new FavourRequirement(Favour.HOSIDIUS, 75);
		piscariliusFavour = new FavourRequirement(Favour.PISCARILIUS, 100);

		// Zone requirements
		inRedwoodTree = new ZoneRequirement(redwoodTree);
		inCatacombs = new ZoneRequirement(catacombs);
		inSkotizoLair = new ZoneRequirement(skotizoLair);
		inWoodcuttingGuild = new ZoneRequirement(woodcuttingGuild);
		inMountKaruulmDungeon = new ZoneRequirement(mountKaruulmDungeon);
		inHydraArea = new ZoneRequirement(hydraArea);
		inFish = new ZoneRequirement(fish);
		inFarming = new ZoneRequirement(farming);

		anglerCaught = new ChatMessageRequirement(
			inFish,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) anglerCaught).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inFish),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		barkHarvested = new ChatMessageRequirement(
			inFarming,
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 3.</col>"
		);
		((ChatMessageRequirement) barkHarvested).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inFarming),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 3.</col>"
			)
		);

	}

	public void loadZones()
	{
		redwoodTree = new Zone(new WorldPoint(1567, 3496, 1), new WorldPoint(1574, 3479, 1));
		catacombs = new Zone(new WorldPoint(1659, 10052, 0), new WorldPoint(1668, 10043, 0));
		skotizoLair = new Zone(new WorldPoint(1479, 2415, 0), new WorldPoint(6513, 2384, 0));
		woodcuttingGuild = new Zone(new WorldPoint(1563, 3948, 0), new WorldPoint(1581, 3477, 0));
		mountKaruulmDungeon = new Zone(new WorldPoint(1303, 10210, 0), new WorldPoint(1320, 10187, 0));
		hydraArea = new Zone(new WorldPoint(1310, 10250, 0), new WorldPoint(1330, 10215, 0));
		fish = new Zone(new WorldPoint(1810, 3787, 0), new WorldPoint(1852, 3758, 0));
		farming = new Zone(new WorldPoint(1220, 3764, 0), new WorldPoint(1274, 3720, 0));
	}

	public void setupSteps()
	{
		// Craft blood runes
		bloodMineDenseEssence = new ObjectStep(this, ObjectID.DENSE_RUNESTONE, new WorldPoint(1764, 3858, 0),
			"Mine a dense essence block.", pickaxe, chisel);
		bloodVenerateEssenceBlock = new ObjectStep(this, ObjectID.DARK_ALTAR, new WorldPoint(1716, 3883, 0),
			"Venerate the essence block on the Dark Altar.", denseEssenceBlock);
		chiselEssenceBlock = new ItemStep(this, "Chisel the dark essence block.", chisel.highlighted(),
			darkEssenceBlock.highlighted());
		craftBloodRune = new ObjectStep(this, ObjectID.BLOOD_ALTAR, new WorldPoint(1718, 3828, 0),
			"Craft some blood runes.", darkEssenceFragment);

		// Chop redwood logs
		enterWoodcuttingGuild = new ObjectStep(this, ObjectID.GATE_28851, new WorldPoint(1657, 3505, 0),
			"Enter the Woodcutting Guild.", true);
		enterWoodcuttingGuild.addAlternateObjects(ObjectID.GATE_28852);
		climbRedwoodTree = new ObjectStep(this, ObjectID.ROPE_LADDER_28857, "Climb the redwood tree.", axe);
		chopRedwood = new ObjectStep(this, ObjectID.REDWOOD_TREE, "Chop the redwood tree.", axe);
		chopRedwood.addAlternateObjects(ObjectID.REDWOOD_TREE_29669, ObjectID.REDWOOD_TREE_29670, ObjectID.REDWOOD_TREE_29671);

		// Defeat Skotizo
		combineDarkTotem = new ItemStep(this, "Collect and combine dark totem pieces.", totemTop.highlighted(),
			totemMiddle.highlighted(), totemBase.highlighted());
		enterCatacombs = new ObjectStep(this, ObjectID.STATUE_27785, "Enter the Catacombs of Kourend.",
			xericsTalisman);
		enterSkotizoLair = new ObjectStep(this, ObjectID.ALTAR_28900, "Enter Skotizo's Lair.",
			darkTotem.highlighted());
		enterSkotizoLair.addIcon(ItemID.DARK_TOTEM);
		defeatSkotizo = new NpcStep(this, NpcID.SKOTIZO, "Defeat Skotizo.", combatGear, food, prayerPotion);

		// Catch and cook and an anglerfish
		catchAngler = new NpcStep(this, NpcID.ROD_FISHING_SPOT_6825, "Catch a raw anglerfish.",
			fishingRod, sandworm);
		cookAnglerfish = new ObjectStep(this, ObjectID.RANGE_27724, new WorldPoint(1795, 3754, 0),
			"Cook a raw anglerfish in Kourend.", rawAnglerfish);

		// Kill a hydra
		enterMountKaruulmDungeon = new ObjectStep(this, ObjectID.ELEVATOR, new WorldPoint(1311, 3807, 0),
			"Enter the Mount Karuulm Slayer Dungeon.");
		enterHydraArea = new ObjectStep(this, ObjectID.ROCKS_34544, new WorldPoint(1312, 10215, 0),
			"Enter the hydra area.", bootsOfStone.equipped());
		killHydra = new NpcStep(this, NpcID.HYDRA, new WorldPoint(1312, 10232, 0),
			"Kill a hydra.", combatGear, food);
		killHydra.addSubSteps(enterMountKaruulmDungeon, enterHydraArea);

		// Create an Ape Atoll teleport tab
		apeMineDenseEssence = new ObjectStep(this, ObjectID.DENSE_RUNESTONE, new WorldPoint(1764, 3858, 0),
			"Mine a dense essence block.", pickaxe, chisel);
		apeVenerateEssenceBlock = new ObjectStep(this, ObjectID.DARK_ALTAR, new WorldPoint(1716, 3883, 0),
			"Venerate the essence block.", denseEssenceBlock);
		switchSpellbook = new NpcStep(this, NpcID.TYSS, new WorldPoint(1712, 3882, 0),
			"Switch to the Arceuus spellbook via Tyss.");
		switchSpellbook.addDialogStep("Can I try the magicks myself?");
		createTeleportTab = new ObjectStep(this, ObjectID.LECTERN_28802, new WorldPoint(1679, 3765, 0),
			"Create an Ape Atoll teleport tablet.", arceuusFavour, bloodRune.quantity(2), lawRune.quantity(2),
			soulRune.quantity(2));

		// Complete a raid
		completeRaid = new ObjectStep(this, ObjectID.CHAMBERS_OF_XERIC, new WorldPoint(1232, 3573, 0),
			"Complete a Chambers of Xeric raid.");

		// Fletch a battlestaff from scratch
		plantCelastrusTree = new ObjectStep(this, NullObjectID.NULL_34629, new WorldPoint(1244, 3750, 0),
			"Plant a celastrus sapling (Fully grown after 13 hours). When it's fully grown harvest its bark. " +
				"If you're waiting for it to grow and want to complete further tasks, use the tick box on panel.",
			celastrusSapling, spade, axe);
		fletchBattlestaff = new ItemStep(this, "Fletch a battlestaff.", knife.highlighted(),
			celastrusBark.highlighted());

		// Claim reward
		claimReward = new NpcStep(this, NpcID.ELISE, new WorldPoint(1647, 3665, 0),
			"Talk to Elise in the Kourend castle courtyard to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary");
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList(
			"Kill Skotizo (level 321)",
			"Kill a Hydra (level 194)",
			"Various enemies in Chambers of Xeric");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(pickaxe, chisel, axe, darkTotem, bloodRune.quantity(2), lawRune.quantity(2),
			soulRune.quantity(2), fishingRod, sandworm, celastrusSapling, combatGear, food, knife, bootsOfStone);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(arclight, kharedstsMemoirs, dramenStaff, skillsNecklace, radasBlessing, xericsTalisman,
			potatoCactus, ultraCompost, prayerPotion);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new SkillRequirement(Skill.COOKING, 84, true));
		req.add(new SkillRequirement(Skill.CRAFTING, 38));
		req.add(new SkillRequirement(Skill.FARMING, 85));
		req.add(new SkillRequirement(Skill.FISHING, 82, true));
		req.add(new SkillRequirement(Skill.FLETCHING, 40));
		req.add(new SkillRequirement(Skill.MAGIC, 90, true));
		req.add(new SkillRequirement(Skill.MINING, 38));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 77, true));
		req.add(new SkillRequirement(Skill.SLAYER, 95, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 90, true));

		req.add(arceuusFavour);
		req.add(hosidiusFavour75);
		req.add(piscariliusFavour);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Rada's Blessing (4)", ItemID.RADAS_BLESSING_4, 1),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Demonic ashes from the ash sanctifier grant full Prayer experience"),
			new UnlockReward("Completing a slayer task from Konar quo Maten gives 20 Slayer points (up from 18)"),
			new UnlockReward("10% reduced burn chance at the city kitchens (up from 5%)"),
			new UnlockReward("Protection from the burn effect in Karuulm Slayer Dungeon without boots of stone"),
			new UnlockReward("5% increased chance to save a harvest life at the Hosidius and Farming Guild herb patches."),
			new UnlockReward("80 free Dynamite per day from Thirus"),
			new UnlockReward("10% additional blood runes from blood runecrafting (no additional xp)"),
			new UnlockReward("Reduced tanning prices at Eodan in Forthos Dungeon to 20%."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails battlestaffStep = new PanelDetails("Battlestaff From Scratch", Arrays.asList(plantCelastrusTree,
			fletchBattlestaff), new SkillRequirement(Skill.FARMING, 85),
			new SkillRequirement(Skill.FLETCHING, 40), hosidiusFavour60, spade, celastrusSapling, knife, axe);
		battlestaffStep.setDisplayCondition(notFletchBattlestaff);
		battlestaffStep.setLockingStep(fletchBattlestaffTask);
		allSteps.add(battlestaffStep);

		PanelDetails craftBloodRuneStep = new PanelDetails("Craft Blood Rune",
			Arrays.asList(bloodMineDenseEssence, bloodVenerateEssenceBlock, chiselEssenceBlock, craftBloodRune),
			new SkillRequirement(Skill.RUNECRAFT, 77, true), new SkillRequirement(Skill.MINING, 38),
			new SkillRequirement(Skill.CRAFTING, 38), arceuusFavour, chisel, pickaxe);
		craftBloodRuneStep.setDisplayCondition(notCraftBloodRune);
		craftBloodRuneStep.setLockingStep(craftBloodRuneTask);
		allSteps.add(craftBloodRuneStep);

		PanelDetails createTabStep = new PanelDetails("Ape Atoll Teleport Tablet", Arrays.asList(
			apeMineDenseEssence, apeVenerateEssenceBlock, switchSpellbook, createTeleportTab),
			new SkillRequirement(Skill.MAGIC, 90, true), arceuusFavour, chisel, pickaxe,
			soulRune.quantity(2), lawRune.quantity(2), bloodRune.quantity(2));
		createTabStep.setDisplayCondition(notCreateTeleport);
		createTabStep.setLockingStep(createTeleportTask);
		allSteps.add(createTabStep);

		PanelDetails killHydraStep = new PanelDetails("Kill Hydra", Arrays.asList(enterMountKaruulmDungeon,
			enterHydraArea, killHydra), new SkillRequirement(Skill.SLAYER, 95, true), combatGear,
			food, prayerPotion, bootsOfStone);
		killHydraStep.setDisplayCondition(notKillHydra);
		killHydraStep.setLockingStep(killHydraTask);
		allSteps.add(killHydraStep);

		PanelDetails chopRedwoodStep = new PanelDetails("Chop Redwood Logs", Arrays.asList(enterWoodcuttingGuild,
			climbRedwoodTree, chopRedwood), new SkillRequirement(Skill.WOODCUTTING, 90, true),
			hosidiusFavour75, axe);
		chopRedwoodStep.setDisplayCondition(notChopRedwood);
		chopRedwoodStep.setLockingStep(chopRedwoodTask);
		allSteps.add(chopRedwoodStep);

		PanelDetails catchAnglerStep = new PanelDetails("Catch And Cook Anglerfish", Arrays.asList(catchAngler,
			cookAnglerfish), new SkillRequirement(Skill.COOKING, 84, true),
			new SkillRequirement(Skill.FISHING, 82, true), piscariliusFavour, fishingRod,
			sandworm);
		catchAnglerStep.setDisplayCondition(notCatchAngler);
		catchAnglerStep.setLockingStep(catchAnglerTask);
		allSteps.add(catchAnglerStep);

		PanelDetails raidStep = new PanelDetails("Complete Chambers Of Xeric Raid", Collections.singletonList(
			completeRaid));
		raidStep.setDisplayCondition(notCompleteRaid);
		raidStep.setLockingStep(completeRaidTask);
		allSteps.add(raidStep);

		PanelDetails defeatSkotizoStep = new PanelDetails("Defeat Skotizo", Arrays.asList(combineDarkTotem,
			enterCatacombs, enterSkotizoLair, defeatSkotizo), darkTotem, combatGear, food);
		defeatSkotizoStep.setDisplayCondition(notDefeatSkotizo);
		defeatSkotizoStep.setLockingStep(defeatSkotizoTask);
		allSteps.add(defeatSkotizoStep);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
