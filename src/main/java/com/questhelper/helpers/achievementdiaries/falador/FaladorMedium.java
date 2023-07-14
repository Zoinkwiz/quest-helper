/*
 * Copyright (c) 2021, Kerpackie <https://github.com/Kerpackie/>
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
package com.questhelper.helpers.achievementdiaries.falador;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.ComplexRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
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
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.FALADOR_MEDIUM
)

public class FaladorMedium extends ComplexStateQuestHelper
{
	//Items Required
	ItemRequirement combatGear, bullseyeLantern, tinderbox, lawRune1, lawRune2, airRune1, airRune3, airRune4,
		waterRune1, crystalKey, bronzeSpear, watermelon, emptySack, fishingExplosive, mithGrapple, anyCrossbow,
		initiateHelm, initiateChest, initiateLegs, pickaxe, axe, brownApron, willowBranch6, rake, lawRune, airRune;

	//Items Recommended
	ItemRequirement faladorTeleport, explorersRing, combatBracelet;

	ItemRequirement willowLog, haySack, scarecrow, scarecrowStep2, sack;

	Requirement ratCatchers, skippyAndMogres, recDrive, normalBook, bothRunes;

	ItemRequirements initiateSet, scarecrowItems;

	Requirement notLitLantern, notTelegrabbedWine, notUnlockedCrystalChest, notPlacedScarecrow,
		notKilledMogre, notVisitRatPits, notGrappleNorthWall, notPickpocketGuard, notPrayAtAltar,
		notMineGold, notDwarfShortcut, notChopBurnWillowTav, notBasketFalLoom, notTeleportFalador, choppedLogs;

	QuestStep claimReward, goToChemist, lightLantern, goToChaosTemple, telegrabWine, unlockCrystalChest,
		fillSack, useSackOnSpear, useWatermelonOnSack, placeScarecrow, killMogre, visitRatPits,
		grappleNorthWallStart, grappleNorthWallEnd, prayAtAltar, getInitiateSet, goToCraftingGuild,
		mineGold, enterDwarvenMines, dwarfShortcut, goToTav, chopWillowLog, burnWillowLog,
		makeBasketFalLoom, teleportToFalador;

	NpcStep pickpocketGuard;

	ObjectStep spawnMogre;

	Zone chemist, chaosTemple, craftingGuild, dwarvenMine, tav, falNorthWall;

	ZoneRequirement inChemist, inChaosTemple, inCraftingGuild, inDwarvenMine, inTav, inFalNorthWall;

	ConditionalStep litLanternTask, telegrabbedWineTask, unlockedCrystalChestTask, placedScarecrowTask, killedMogreTask,
		visitRatPitsTask, grappleNorthWallTask, pickpocketGuardTask, prayAtAltarTask, mineGoldTask, dwarfShortcutTask,
		chopBurnWillowTavTask, basketFalLoomTask, teleportFaladorTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMed = new ConditionalStep(this, claimReward);

		prayAtAltarTask = new ConditionalStep(this, getInitiateSet);
		prayAtAltarTask.addStep(initiateSet, prayAtAltar);
		doMed.addStep(notPrayAtAltar, prayAtAltarTask);

		unlockedCrystalChestTask = new ConditionalStep(this, unlockCrystalChest);
		doMed.addStep(notUnlockedCrystalChest, unlockedCrystalChestTask);

		chopBurnWillowTavTask = new ConditionalStep(this, goToTav);
		chopBurnWillowTavTask.addStep(inTav, chopWillowLog);
		chopBurnWillowTavTask.addStep(new Conditions(inTav, willowLog, choppedLogs), burnWillowLog);
		doMed.addStep(notChopBurnWillowTav, chopBurnWillowTavTask);

		mineGoldTask = new ConditionalStep(this, goToCraftingGuild);
		mineGoldTask.addStep(inCraftingGuild, mineGold);
		doMed.addStep(notMineGold, mineGoldTask);

		litLanternTask = new ConditionalStep(this, goToChemist);
		litLanternTask.addStep(inChemist, lightLantern);
		doMed.addStep(notLitLantern, litLanternTask);

		killedMogreTask = new ConditionalStep(this, spawnMogre);
		doMed.addStep(notKilledMogre, killedMogreTask);

		visitRatPitsTask = new ConditionalStep(this, visitRatPits);
		doMed.addStep(notVisitRatPits, visitRatPitsTask);

		basketFalLoomTask = new ConditionalStep(this, makeBasketFalLoom);
		doMed.addStep(notBasketFalLoom, basketFalLoomTask);

		placedScarecrowTask = new ConditionalStep(this, fillSack);
		placedScarecrowTask.addStep(haySack, useSackOnSpear);
		placedScarecrowTask.addStep(scarecrowStep2, placeScarecrow);
		placedScarecrowTask.addStep(scarecrow, useWatermelonOnSack);
		doMed.addStep(notPlacedScarecrow, placedScarecrowTask);

		grappleNorthWallTask = new ConditionalStep(this, grappleNorthWallStart);
		grappleNorthWallTask.addStep(inFalNorthWall, grappleNorthWallEnd);
		doMed.addStep(notGrappleNorthWall, grappleNorthWallTask);

		dwarfShortcutTask = new ConditionalStep(this, enterDwarvenMines);
		dwarfShortcutTask.addStep(inDwarvenMine, dwarfShortcut);
		doMed.addStep(notDwarfShortcut, dwarfShortcutTask);

		teleportFaladorTask = new ConditionalStep(this, teleportToFalador);
		doMed.addStep(notTeleportFalador, teleportFaladorTask);

		pickpocketGuardTask = new ConditionalStep(this, pickpocketGuard);
		doMed.addStep(notPickpocketGuard, pickpocketGuardTask);

		telegrabbedWineTask = new ConditionalStep(this, goToChaosTemple);
		telegrabbedWineTask.addStep(inChaosTemple, telegrabWine);
		doMed.addStep(notTelegrabbedWine, telegrabbedWineTask);

		return doMed;
	}

	@Override
	public void setupRequirements()
	{
		notLitLantern = new VarplayerRequirement(1186, false, 11);
		notTelegrabbedWine = new VarplayerRequirement(1186, false, 12);
		notUnlockedCrystalChest = new VarplayerRequirement(1186, false, 13);
		notPlacedScarecrow = new VarplayerRequirement(1186, false, 14);
		notKilledMogre = new VarplayerRequirement(1186, false, 15);
		notVisitRatPits = new VarplayerRequirement(1186, false, 16);
		notGrappleNorthWall = new VarplayerRequirement(1186, false, 17);
		notPickpocketGuard = new VarplayerRequirement(1186, false, 18);
		notPrayAtAltar = new VarplayerRequirement(1186, false, 20);
		notMineGold = new VarplayerRequirement(1186, false, 21);
		notDwarfShortcut = new VarplayerRequirement(1186, false, 22);
		notChopBurnWillowTav = new VarplayerRequirement(1186, false, 23);
		notBasketFalLoom = new VarplayerRequirement(1186, false, 24);
		notTeleportFalador = new VarplayerRequirement(1186, false, 25);
		bothRunes = new ComplexRequirement(LogicType.AND, "Earth runes", notTeleportFalador, notTelegrabbedWine);

		normalBook = new SpellbookRequirement(Spellbook.NORMAL);

		bullseyeLantern = new ItemRequirement("Bullseye Lantern", ItemID.BULLSEYE_LANTERN).showConditioned(notLitLantern);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX)
			.showConditioned(new Conditions(LogicType.OR, notLitLantern, notChopBurnWillowTav)).isNotConsumed();
		airRune1 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 1)
			.showConditioned(new Conditions(notTelegrabbedWine, new Conditions(LogicType.NOR, bothRunes)));
		airRune3 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 3)
			.showConditioned(new Conditions(notTeleportFalador, new Conditions(LogicType.NOR, bothRunes)));
		airRune4 = new ItemRequirement("Air rune", ItemID.AIR_RUNE, 4).showConditioned(bothRunes);
		lawRune2 = new ItemRequirement("Law rune", ItemID.LAW_RUNE, 2).showConditioned(bothRunes);
		lawRune1 = new ItemRequirement("Law rune", ItemID.LAW_RUNE, 1)
			.showConditioned(new Conditions(new Conditions(LogicType.OR, notTelegrabbedWine, notTeleportFalador),
				new Conditions(LogicType.NOR, bothRunes)));
		lawRune = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		waterRune1 = new ItemRequirement("Water rune", ItemID.WATER_RUNE, 1).showConditioned(notTeleportFalador);
		crystalKey = new ItemRequirement("Crystal Key", ItemID.CRYSTAL_KEY).showConditioned(notUnlockedCrystalChest);
		haySack = new ItemRequirement("Hay Sack", ItemID.HAY_SACK);
		bronzeSpear = new ItemRequirement("Bronze Spear", ItemID.BRONZE_SPEAR);
		watermelon = new ItemRequirement("Watermelon", ItemID.WATERMELON);
		emptySack = new ItemRequirement("Empty Sack", ItemID.EMPTY_SACK);
		sack = new ItemRequirements(LogicType.OR, emptySack, haySack);
		scarecrow = new ItemRequirement("Scarecrow", ItemID.SCARECROW).showConditioned(notPlacedScarecrow);
		scarecrowItems = new ItemRequirements(LogicType.OR, "1 x Scarecrow", scarecrow, new ItemRequirements(sack,
			watermelon, bronzeSpear));
		scarecrowItems.setTooltip("Created by combining a bronze spear, watermelon, and hay sack " +
			"(empty sack filled at a hay bale, nearest is North-West of Lumbridge)");
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPlacedScarecrow).isNotConsumed();
		fishingExplosive = new ItemRequirement("Fishing explosive", ItemID.FISHING_EXPLOSIVE).showConditioned(notKilledMogre);
		fishingExplosive.addAlternates(ItemID.FISHING_EXPLOSIVE_6664);
		combatGear = new ItemRequirement("Combat Gear", -1, -1).showConditioned(notKilledMogre).isNotConsumed();
		mithGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notGrappleNorthWall).isNotConsumed();
		anyCrossbow = new ItemRequirement("Any usable crossbow", ItemCollections.CROSSBOWS).showConditioned(notGrappleNorthWall).isNotConsumed();
		initiateHelm = new ItemRequirement("Initiate Helm", ItemID.INITIATE_SALLET).showConditioned(notPrayAtAltar).isNotConsumed();
		initiateChest = new ItemRequirement("Initiate Chest", ItemID.INITIATE_HAUBERK).showConditioned(notPrayAtAltar).isNotConsumed();
		initiateLegs = new ItemRequirement("Initiate Legs", ItemID.INITIATE_CUISSE).showConditioned(notPrayAtAltar).isNotConsumed();
		pickaxe = new ItemRequirement("Any Pickaxe", ItemCollections.PICKAXES).showConditioned(notMineGold).isNotConsumed();
		axe = new ItemRequirement("Any Axe", ItemCollections.AXES).showConditioned(notChopBurnWillowTav).isNotConsumed();
		brownApron = new ItemRequirement("Brown Apron", ItemID.BROWN_APRON).showConditioned(notMineGold).isNotConsumed();
		willowBranch6 = new ItemRequirement("Willow Branches", ItemID.WILLOW_BRANCH, 6).showConditioned(notBasketFalLoom);

		willowLog = new ItemRequirement("Willow Log", ItemID.WILLOW_LOGS);
		scarecrowStep2 = new ItemRequirement("Hay Sack", ItemID.HAY_SACK_6058);

		initiateSet = new ItemRequirements(initiateChest, initiateLegs, initiateHelm);

		faladorTeleport = new ItemRequirement("Falador Teleports", ItemID.FALADOR_TELEPORT);
		explorersRing = new ItemRequirement("Explorer's Ring (2)", ItemID.EXPLORERS_RING_2).isNotConsumed();
		explorersRing.addAlternates(ItemID.EXPLORERS_RING_4, ItemID.EXPLORERS_RING_3);
		combatBracelet = new ItemRequirement("Combat Bracelet", ItemCollections.COMBAT_BRACELETS).isNotConsumed();
		combatBracelet.addAlternates(ItemCollections.GAMES_NECKLACES);

		inChemist = new ZoneRequirement(chemist);
		inChaosTemple = new ZoneRequirement(chaosTemple);
		inCraftingGuild = new ZoneRequirement(craftingGuild);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inTav = new ZoneRequirement(tav);
		inFalNorthWall = new ZoneRequirement(falNorthWall);

		choppedLogs = new ChatMessageRequirement(
			"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
		);
		((ChatMessageRequirement) choppedLogs).setInvalidateRequirement(
			new ChatMessageRequirement(
				new Conditions(LogicType.NOR, inTav),
				"<col=0040ff>Achievement Diary Stage Task - Current stage: 1.</col>"
			)
		);

		ratCatchers = new QuestRequirement(QuestHelperQuest.RATCATCHERS, QuestState.IN_PROGRESS);
		skippyAndMogres = new QuestRequirement(QuestHelperQuest.SKIPPY_AND_THE_MOGRES, QuestState.FINISHED);
		recDrive = new QuestRequirement(QuestHelperQuest.RECRUITMENT_DRIVE, QuestState.FINISHED);

	}

	public void loadZones()
	{
		chemist = new Zone(new WorldPoint(2929, 3213, 0), new WorldPoint(2936, 3207, 0));
		chaosTemple = new Zone(new WorldPoint(2935, 3513, 0), new WorldPoint(2929, 3518, 0));
		craftingGuild = new Zone(new WorldPoint(2929, 3288, 0), new WorldPoint(2943, 3276, 0));
		dwarvenMine = new Zone(new WorldPoint(2979, 9855, 0), new WorldPoint(3069, 9698, 0));
		tav = new Zone(new WorldPoint(2939, 3398, 0), new WorldPoint(2878, 3489, 0));
		falNorthWall = new Zone(new WorldPoint(3022, 3089, 0), new WorldPoint(3039, 3089, 1));
	}

	public void setupSteps()
	{
		//Bullseye Lantern - Rim Chemist
		goToChemist = new DetailedQuestStep(this, new WorldPoint(2932, 3213, 0),
			"Go to the Chemist's in Rimmington.", bullseyeLantern, tinderbox);
		lightLantern = new DetailedQuestStep(this,
			"Use the tinderbox on the bullseye lantern.", bullseyeLantern.highlighted(), tinderbox.highlighted());

		//Telegrab - Chaos Temple
		goToChaosTemple = new DetailedQuestStep(this, new WorldPoint(2934, 3516, 0),
			"Go to the Chaos Temple near the Goblin Village north of Falador.");
		telegrabWine = new DetailedQuestStep(this,
			"Use the Telekinetic Grab Spell on the Wine of Zamorak.");
		telegrabWine.addIcon(ItemID.TELEKINETIC_GRAB);

		//Crystal Chest
		unlockCrystalChest = new ObjectStep(this, ObjectID.CLOSED_CHEST_172, new WorldPoint(2914, 3452, 0),
			"Use the crystal key to unlock the chest in Taverley");
		unlockCrystalChest.addIcon(ItemID.CRYSTAL_KEY);

		//Scarecrow
		fillSack = new ObjectStep(this, ObjectID.HAY_BALE_8713, new WorldPoint(3019, 3297, 0),
			"Use the empty sack on the hay bale to fill it, you can buy an empty sack from Sarah for 5gp.");
		fillSack.addIcon(ItemID.EMPTY_SACK);
		useSackOnSpear = new DetailedQuestStep(this,
			"Use the Hay sack on the Bronze Spear.", haySack.highlighted(), bronzeSpear.highlighted());
		useWatermelonOnSack = new DetailedQuestStep(this,
			"Use the watermelon on the Hay Sack to make the Scarecrow.", scarecrowStep2.highlighted(), watermelon.highlighted());
		placeScarecrow = new ObjectStep(this, ObjectID.FLOWER_PATCH, new WorldPoint(3054, 3307, 0),
			"Rake any weeds in the flower patch, then plant your scarecrow.", rake, scarecrow.highlighted());
		placeScarecrow.addIcon(ItemID.SCARECROW);

		//Mogre
		spawnMogre = new ObjectStep(this, ObjectID.OMINOUS_FISHING_SPOT,
			"Go to Mudskipper Point south of Port Sarim and use your fishing explosive to spawn a Mogre.", fishingExplosive.highlighted());
		spawnMogre.addAlternateObjects(ObjectID.OMINOUS_FISHING_SPOT_10088, ObjectID.OMINOUS_FISHING_SPOT_10089);
		spawnMogre.addIcon(ItemID.FISHING_EXPLOSIVE);
		killMogre = new NpcStep(this, NpcID.MOGRE,
			"Kill the Mogre", combatGear);
		spawnMogre.addSubSteps(killMogre);

		//Ratpits
		visitRatPits = new ObjectStep(this, ObjectID.MANHOLE_10321, new WorldPoint(3018, 3232, 0),
			"Climb down the manhole in Port Sarim to visit the Rat Pits.");

		//Grapple wall
		grappleNorthWallStart = new ObjectStep(this, ObjectID.WALL_17050, new WorldPoint(3032, 3389, 0),
			"Equip your crossbow and grapple then climb the agility shortcut near the Falador Party Room.",
			anyCrossbow.highlighted(), mithGrapple.highlighted());
		grappleNorthWallEnd = new ObjectStep(this, ObjectID.WALL_17051, new WorldPoint(3033, 3390, 0),
			"Climb down the wall to finish the task.");
		grappleNorthWallEnd.addSubSteps(grappleNorthWallStart);

		//PickPocket
		pickpocketGuard = new NpcStep(this, NpcID.GUARD_3269, new WorldPoint(2961, 3381, 0),
			"Pickpocket a guard.", true);
		pickpocketGuard.setHideWorldArrow(true);
		pickpocketGuard.addAlternateNpcs(NpcID.GUARD_3271, NpcID.GUARD_3272);

		//Pray with Initiate Set
		getInitiateSet = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, new WorldPoint(2997, 3373, 0),
			"Speak to Sir Tiffy Cashien to purchase a set of Initiate Armor for 20,000 Coins for a full set.");
		getInitiateSet.addDialogStep("Can I buy some armour?");
		prayAtAltar = new ObjectStep(this, ObjectID.ALTAR_OF_GUTHIX, new WorldPoint(2925, 3483, 0),
			"Equip your Initiate armor and pray at the Altar of Guthix in Taverley", initiateHelm.equipped(),
			initiateChest.equipped(), initiateLegs.equipped());

		//Mine Gold in Crafting Guild
		goToCraftingGuild = new ObjectStep(this, ObjectID.GUILD_DOOR_14910, new WorldPoint(2933, 3289, 0),
			"Go to the Crafting Guild west of Falador. You will need to equip a brown apron to enter.", brownApron, pickaxe);
		mineGold = new ObjectStep(this, ObjectID.GOLD_ROCKS, new WorldPoint(2938, 3280, 0),
			"Mine a gold ore.", pickaxe);

		//Dwarven Mines Shortcut
		enterDwarvenMines = new ObjectStep(this, ObjectID.STAIRCASE_16664, new WorldPoint(3059, 3376, 0),
			"Go to the Dwarven Mines.");
		dwarfShortcut = new ObjectStep(this, ObjectID.CREVICE_16543, new WorldPoint(3034, 9806, 0),
			"Squeeze through the crevice in the Dwarven Mines");

		//Chop and burn Willow in Tav
		goToTav = new DetailedQuestStep(this, new WorldPoint(2921, 3431, 0),
			"Go to Taverley, north west of Falador.", axe, tinderbox);
		chopWillowLog = new ObjectStep(this, ObjectID.WILLOW_TREE_10819, new WorldPoint(2925, 3412, 0),
			"Chop a Willow Tree while within Taverley.", axe, tinderbox);
		burnWillowLog = new DetailedQuestStep(this,
			"Use your tinderbox on the Willow Logs.", willowLog.highlighted(), tinderbox.highlighted());

		//Make Basket on Loom
		makeBasketFalLoom = new ObjectStep(this, ObjectID.LOOM_8717, new WorldPoint(3039, 3287, 0),
			"Use Sarah's Loom in the Falador Farm to make a basket.", willowBranch6);
		makeBasketFalLoom.addIcon(ItemID.WILLOW_BRANCH);

		//Teleport to Falador
		teleportToFalador = new DetailedQuestStep(this,
			"Use the Teleport to Falador spell to teleport to Falador");

		//Claim Reward
		claimReward = new NpcStep(this, NpcID.SIR_REBRAL, new WorldPoint(2977, 3346, 0),
			"Congratulations! Talk to Sir Rebral in the courtyard of The White Knight Castle to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(bullseyeLantern, tinderbox, airRune4, airRune3, airRune1, lawRune2, lawRune1, waterRune1,
			crystalKey, scarecrowItems, rake, fishingExplosive, mithGrapple, anyCrossbow, initiateHelm, initiateChest,
			initiateLegs, pickaxe, axe, brownApron, willowBranch6);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(faladorTeleport, explorersRing, combatBracelet);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		req.add(new SkillRequirement(Skill.AGILITY, 42, true));
		req.add(new SkillRequirement(Skill.CRAFTING, 40, true));
		req.add(new SkillRequirement(Skill.DEFENCE, 20));
		req.add(new SkillRequirement(Skill.FARMING, 23, true));
		req.add(new SkillRequirement(Skill.FIREMAKING, 49, true));
		req.add(new SkillRequirement(Skill.MAGIC, 37, true));
		req.add(new SkillRequirement(Skill.MINING, 40, true));
		req.add(new SkillRequirement(Skill.PRAYER, 10));
		req.add(new SkillRequirement(Skill.RANGED, 19));
		req.add(new SkillRequirement(Skill.SLAYER, 32));
		req.add(new SkillRequirement(Skill.STRENGTH, 37));
		req.add(new SkillRequirement(Skill.THIEVING, 40, true));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 30, true));

		req.add(ratCatchers);
		req.add(recDrive);
		req.add(skippyAndMogres);

		return req;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Falador Shield (2)", ItemID.FALADOR_SHIELD_2, 1),
			new ItemReward("7,500 Exp. Lamp (Any skill over 40)", ItemID.ANTIQUE_LAMP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("10% more experience from the Falador Farming Patch"),
			new UnlockReward("Access to a shortcut in the Motherlode Mine"),
			new UnlockReward("Increased chance to receiving a clue scroll from a guard in Falador"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails initiateSteps = new PanelDetails("Initialising...", Arrays.asList(getInitiateSet, prayAtAltar),
			recDrive, initiateHelm, initiateChest, initiateLegs);
		initiateSteps.setDisplayCondition(notPrayAtAltar);
		initiateSteps.setLockingStep(prayAtAltarTask);
		allSteps.add(initiateSteps);

		PanelDetails crystalChestSteps = new PanelDetails("Lost Elven Treasure",
			Collections.singletonList(unlockCrystalChest), crystalKey);
		crystalChestSteps.setDisplayCondition(notUnlockedCrystalChest);
		crystalChestSteps.setLockingStep(unlockedCrystalChestTask);
		allSteps.add(crystalChestSteps);

		PanelDetails willowTavSteps = new PanelDetails("Snoop Loggy-log", Arrays.asList(goToTav, chopWillowLog,
			burnWillowLog), new SkillRequirement(Skill.WOODCUTTING, 30, true),
			new SkillRequirement(Skill.FIREMAKING, 30, true), axe, tinderbox);
		willowTavSteps.setDisplayCondition(notChopBurnWillowTav);
		willowTavSteps.setLockingStep(chopBurnWillowTavTask);
		allSteps.add(willowTavSteps);

		PanelDetails mineGoldSteps = new PanelDetails("Au! I'm minin' here!", Arrays.asList(goToCraftingGuild,
			mineGold), new SkillRequirement(Skill.MINING, 40, true),
			new SkillRequirement(Skill.CRAFTING, 40, true), pickaxe, brownApron);
		mineGoldSteps.setDisplayCondition(notMineGold);
		mineGoldSteps.setLockingStep(mineGoldTask);
		allSteps.add(mineGoldSteps);

		PanelDetails lanternSteps = new PanelDetails("Bullseye!", Arrays.asList(goToChemist, lightLantern),
			new SkillRequirement(Skill.FIREMAKING, 49, true), tinderbox, bullseyeLantern);
		lanternSteps.setDisplayCondition(notLitLantern);
		lanternSteps.setLockingStep(litLanternTask);
		allSteps.add(lanternSteps);

		PanelDetails mogreSteps = new PanelDetails("Mogres have layers", Arrays.asList(spawnMogre, killMogre),
			skippyAndMogres, new SkillRequirement(Skill.SLAYER, 32), combatGear, fishingExplosive);
		mogreSteps.setDisplayCondition(notKilledMogre);
		mogreSteps.setLockingStep(killedMogreTask);
		allSteps.add(mogreSteps);

		PanelDetails visitRatsSteps = new PanelDetails("Ahh rats..", Collections.singletonList(visitRatPits),
			ratCatchers);
		visitRatsSteps.setDisplayCondition(notVisitRatPits);
		visitRatsSteps.setLockingStep(visitRatPitsTask);
		allSteps.add(visitRatsSteps);

		PanelDetails basketSteps = new PanelDetails("I never knew this existed",
			Collections.singletonList(makeBasketFalLoom), new SkillRequirement(Skill.CRAFTING, 36, true),
			willowBranch6);
		basketSteps.setDisplayCondition(notBasketFalLoom);
		basketSteps.setLockingStep(basketFalLoomTask);
		allSteps.add(basketSteps);

		PanelDetails scarecrowSteps = new PanelDetails("Brain not included", Arrays.asList(fillSack, useSackOnSpear,
			useWatermelonOnSack, placeScarecrow), new SkillRequirement(Skill.FARMING, 23, true),
			scarecrowItems, rake);
		scarecrowSteps.setDisplayCondition(notPlacedScarecrow);
		scarecrowSteps.setLockingStep(placedScarecrowTask);
		allSteps.add(scarecrowSteps);

		PanelDetails grappleSteps = new PanelDetails("To the window.. To the wall!",
			Arrays.asList(grappleNorthWallStart, grappleNorthWallEnd), new SkillRequirement(Skill.AGILITY, 11),
			new SkillRequirement(Skill.STRENGTH, 37), new SkillRequirement(Skill.RANGED, 19),
			mithGrapple, anyCrossbow);
		grappleSteps.setDisplayCondition(notGrappleNorthWall);
		grappleSteps.setLockingStep(grappleNorthWallTask);
		allSteps.add(grappleSteps);

		PanelDetails dwarfShortcutSteps = new PanelDetails("To Middle Earth", Arrays.asList(enterDwarvenMines,
			dwarfShortcut), new SkillRequirement(Skill.AGILITY, 42));
		dwarfShortcutSteps.setDisplayCondition(notDwarfShortcut);
		dwarfShortcutSteps.setLockingStep(dwarfShortcutTask);
		allSteps.add(dwarfShortcutSteps);

		PanelDetails teleFallySteps = new PanelDetails("Beam me up Scotty!",
			Collections.singletonList(teleportToFalador), new SkillRequirement(Skill.MAGIC, 37, true),
			lawRune.quantity(1), airRune.quantity(3), waterRune1);
		teleFallySteps.setDisplayCondition(notTeleportFalador);
		teleFallySteps.setLockingStep(teleportFaladorTask);
		allSteps.add(teleFallySteps);

		PanelDetails pickGuardSteps = new PanelDetails("Cor blimey mate!", Collections.singletonList(pickpocketGuard),
			new SkillRequirement(Skill.THIEVING, 40));
		pickGuardSteps.setDisplayCondition(notPickpocketGuard);
		pickGuardSteps.setLockingStep(pickpocketGuardTask);
		allSteps.add(pickGuardSteps);

		PanelDetails teleGrabSteps = new PanelDetails("Yoink!", Arrays.asList(goToChaosTemple, telegrabWine),
			new SkillRequirement(Skill.MAGIC, 33, true), normalBook, lawRune.quantity(1), airRune.quantity(1));
		teleGrabSteps.setDisplayCondition(notTelegrabbedWine);
		teleGrabSteps.setLockingStep(telegrabbedWineTask);
		allSteps.add(teleGrabSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
