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
package com.questhelper.achievementdiaries.falador;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
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
	ItemRequirement combatGear, bullseyeLantern, tinderbox, lawRune2, airRune4, waterRune1,
		crystalKey, bronzeSpear, watermelon, emptySack, fishingExplosive, mithGrapple,
		anyCrossbow, initiateHelm, initiateChest, initiateLegs, pickaxe, axe, brownApron,
		willowBranch6, rake;

	//Items Recommended
	ItemRequirement faladorTeleport, explorersRing, combatBracelet;

	ItemRequirement willowLog, telegrab, haySack, scarecrow, bullseyeLanternHighLight,
		tinderboxHighlight, willowLogHighlight, scarecrowStep2, scarecrowStep2Highlight,
		watermelonHighlight, emptySackHighlight, haySackHighlight, bronzeSpearHighlight,
		fishingExplosiveHighlight, mithGrappleHighlight;

	ItemRequirements initiateSet;

	Requirement notLitLantern, notTelegrabbedWine, notUnlockedCrystalChest, notPlacedScarecrow,
		notKilledMogre, notVisitRatPits, notGrappleNorthWall, notPickpocketGuard, notPrayAtAltar,
		notMineGold, notDwarfShortcut, notChopBurnWillowTav, notBasketFalLoom, notTeleportFalador;

	QuestStep claimReward, goToChemist, lightLantern, goToChaosTemple, telegrabWine, unlockCrystalChest,
		getHaysack, useSackOnSpear, useWatermelonOnSack, placeScarecrow, killMogre, visitRatPits,
		grappleNorthWallStart, grappleNorthWallEnd, prayAtAltar, getInitiateSet, goToCraftingGuild,
		mineGold, enterDwarvenMines, dwarfShortcut, goToTav, chopWillowLog, burnWillowLog,
		makeBasketFalLoom, teleportToFalador;

	NpcStep pickpocketGuard;

	ObjectStep spawnMogre;

	Zone chemist, chaosTemple, craftingGuild, dwarvenMine, tav, falNorthWall;

	ZoneRequirement inChemist, inChaosTemple, inCraftingGuild, inDwarvenMine, inTav, inFalNorthWall;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMed = new ConditionalStep(this, claimReward);
		doMed.addStep(new Conditions(notPrayAtAltar, initiateSet), prayAtAltar);
		doMed.addStep(notPrayAtAltar, getInitiateSet);
		doMed.addStep(notUnlockedCrystalChest, unlockCrystalChest);
		doMed.addStep(new Conditions(notChopBurnWillowTav, inTav, willowLog), burnWillowLog);
		doMed.addStep(new Conditions(notChopBurnWillowTav, inTav), chopWillowLog);
		doMed.addStep(notChopBurnWillowTav, goToTav);
		doMed.addStep(new Conditions(notMineGold, inCraftingGuild), mineGold);
		doMed.addStep(notMineGold, goToCraftingGuild);
		doMed.addStep(new Conditions(notLitLantern, inChemist), lightLantern);
		doMed.addStep(notLitLantern, goToChemist);
		doMed.addStep(notKilledMogre, spawnMogre);
		doMed.addStep(notVisitRatPits, visitRatPits);
		doMed.addStep(notBasketFalLoom, makeBasketFalLoom);
		doMed.addStep(new Conditions(notPlacedScarecrow, scarecrow), placeScarecrow);
		doMed.addStep(new Conditions(notPlacedScarecrow, scarecrowStep2), useWatermelonOnSack);
		doMed.addStep(new Conditions(notPlacedScarecrow, haySack), useSackOnSpear);
		doMed.addStep(notPlacedScarecrow, getHaysack);
		doMed.addStep(new Conditions(notGrappleNorthWall, inFalNorthWall), grappleNorthWallEnd);
		doMed.addStep(notGrappleNorthWall, grappleNorthWallStart);
		doMed.addStep(new Conditions(notDwarfShortcut, inDwarvenMine), dwarfShortcut);
		doMed.addStep(notDwarfShortcut, enterDwarvenMines);
		doMed.addStep(notTeleportFalador, teleportToFalador);
		doMed.addStep(notPickpocketGuard, pickpocketGuard);
		doMed.addStep(new Conditions(notTelegrabbedWine, inChaosTemple), telegrabWine);
		doMed.addStep(notTelegrabbedWine, goToChaosTemple);

		return doMed;
	}

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
		// 19 = ???
		notPrayAtAltar = new VarplayerRequirement(1186, false, 20);
		notMineGold = new VarplayerRequirement(1186, false, 21);
		notDwarfShortcut = new VarplayerRequirement(1186, false, 22);
		notChopBurnWillowTav = new VarplayerRequirement(1186, false, 23);
		notBasketFalLoom = new VarplayerRequirement(1186, false, 24);
		notTeleportFalador = new VarplayerRequirement(1186, false, 25);

		bullseyeLantern = new ItemRequirement("Bullseye Lantern", ItemID.BULLSEYE_LANTERN).showConditioned(notLitLantern);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).showConditioned(new Conditions(LogicType.OR, notLitLantern, notChopBurnWillowTav));
		ItemRequirement airRunes = new ItemRequirement("Air rune", ItemCollections.getAirRune(), 4);
		ItemRequirement airStaff = new ItemRequirement("Air staff", ItemCollections.getAirStaff(), 1, true);
		airRune4 = new ItemRequirements(LogicType.OR, "2 air runes", airRunes, airStaff).showConditioned(new Conditions(LogicType.OR,
			notTelegrabbedWine, notTeleportFalador));
		lawRune2 = new ItemRequirement("Law Runes", ItemID.LAW_RUNE, 2);
		waterRune1 = new ItemRequirement("Water Runes", ItemID.WATER_RUNE, 1);
		crystalKey = new ItemRequirement("Crystal Key", ItemID.CRYSTAL_KEY).showConditioned(notUnlockedCrystalChest);
		scarecrow = new ItemRequirement("Scarecrow", ItemID.SCARECROW).showConditioned(notPlacedScarecrow);
		haySack = new ItemRequirement("Hay Sack", ItemID.HAY_SACK).showConditioned(notPlacedScarecrow);
		bronzeSpear = new ItemRequirement("Bronze Spear", ItemID.BRONZE_SPEAR).showConditioned(notPlacedScarecrow);
		watermelon = new ItemRequirement("Watermelon", ItemID.WATERMELON).showConditioned(notPlacedScarecrow);
		emptySack = new ItemRequirement("Empty Sack", ItemID.EMPTY_SACK).showConditioned(notPlacedScarecrow);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(notPlacedScarecrow);
		fishingExplosive = new ItemRequirement("Fishing Explosive", ItemID.FISHING_EXPLOSIVE).showConditioned(notKilledMogre);
		combatGear = new ItemRequirement("Combat Gear", -1, -1).showConditioned(notKilledMogre);
		mithGrapple = new ItemRequirement("Mith grapple", ItemID.MITH_GRAPPLE_9419).showConditioned(notGrappleNorthWall);
		anyCrossbow = new ItemRequirement("Any usable crossbow", ItemCollections.getCrossbows()).showConditioned(notGrappleNorthWall);
		initiateHelm = new ItemRequirement("Initiate Helm", ItemID.INITIATE_SALLET).showConditioned(notPrayAtAltar);
		initiateChest = new ItemRequirement("Initiate Chest", ItemID.INITIATE_HAUBERK).showConditioned(notPrayAtAltar);
		initiateLegs = new ItemRequirement("Initiate Legs", ItemID.INITIATE_CUISSE).showConditioned(notPrayAtAltar);
		pickaxe = new ItemRequirement("A Pickaxe", ItemCollections.getPickaxes(), -1).showConditioned(notMineGold);
		axe = new ItemRequirement("An Axe", ItemCollections.getAxes(), -1).showConditioned(notChopBurnWillowTav);
		brownApron = new ItemRequirement("Brown Apron", ItemID.BROWN_APRON).showConditioned(notMineGold);
		willowBranch6 = new ItemRequirement("Willow Branches", ItemID.WILLOW_BRANCH, 6).showConditioned(notBasketFalLoom);

		willowLog = new ItemRequirement("Willow Log", ItemID.WILLOW_LOGS);
		telegrab = new ItemRequirement("Telekinetic Grab", ItemID.TELEKINETIC_GRAB, 1);
		scarecrowStep2 = new ItemRequirement("Hay Sack", ItemID.HAY_SACK_6058);

		initiateSet = new ItemRequirements(initiateChest, initiateLegs, initiateHelm);

		bullseyeLanternHighLight = new ItemRequirement(true, "Bullseye Lantern", ItemID.BULLSEYE_LANTERN);
		tinderboxHighlight = new ItemRequirement(true, "Tinderbox", ItemID.TINDERBOX);
		willowLogHighlight = new ItemRequirement(true, "Willow Log", ItemID.WILLOW_LOGS);
		watermelonHighlight = new ItemRequirement(true, "Watermelon", ItemID.WATERMELON);
		emptySackHighlight = new ItemRequirement(true, "Empty Sack", ItemID.EMPTY_SACK);
		haySackHighlight = new ItemRequirement(true, "Hay Sack", ItemID.HAY_SACK);
		scarecrowStep2Highlight = new ItemRequirement(true, "Hay Sack", ItemID.HAY_SACK_6058);
		bronzeSpearHighlight = new ItemRequirement(true, "Bronze Spear", ItemID.BRONZE_SPEAR);
		fishingExplosiveHighlight = new ItemRequirement(true, "Fishing Explosive", ItemID.FISHING_EXPLOSIVE);
		mithGrappleHighlight = new ItemRequirement(true, "Mith grapple", ItemID.MITH_GRAPPLE_9419);

		faladorTeleport = new ItemRequirement("Falador Teleports", ItemID.FALADOR_TELEPORT);
		explorersRing = new ItemRequirement("Explorer's Ring (2)", ItemID.EXPLORERS_RING_2);
		explorersRing.addAlternates(ItemID.EXPLORERS_RING_4, ItemID.EXPLORERS_RING_3);
		combatBracelet = new ItemRequirement("Combat Bracelet", ItemCollections.getCombatBracelets());
		combatBracelet.addAlternates(ItemCollections.getGamesNecklaces());

		inChemist = new ZoneRequirement(chemist);
		inChaosTemple = new ZoneRequirement(chaosTemple);
		inCraftingGuild = new ZoneRequirement(craftingGuild);
		inDwarvenMine = new ZoneRequirement(dwarvenMine);
		inTav = new ZoneRequirement(tav);
		inFalNorthWall = new ZoneRequirement(falNorthWall);

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
			"Use the tinderbox on the bullseye lantern.", bullseyeLanternHighLight, tinderboxHighlight);

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
		getHaysack = new ObjectStep(this, ObjectID.HAY_BALE_8713, new WorldPoint(3019, 3297, 0),
			"Use the empty sack on the hay bale to fill it, you can buy an empty sack from Sarah for 5gp.");
		getHaysack.addIcon(ItemID.EMPTY_SACK);
		useSackOnSpear = new DetailedQuestStep(this,
			"Use the Hay sack on the Bronze Spear.", haySackHighlight, bronzeSpearHighlight);
		useWatermelonOnSack = new DetailedQuestStep(this,
			"Use the watermelon on the Hay Sack to make the Scarecrow.", scarecrowStep2Highlight, watermelonHighlight);

		placeScarecrow = new ObjectStep(this, ObjectID.FLOWER_PATCH, new WorldPoint(3054, 3307, 0),
			"Rake any weeds in the flower patch, then plant your scarecrow.", rake);

		//Mogre
		spawnMogre = new ObjectStep(this, ObjectID.OMINOUS_FISHING_SPOT,
			"Go to Mudskipper Point south of Port Sarim and use your fishing explosive to spawn a Mogre.", fishingExplosiveHighlight);
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
			"Equip your crossbow and grapple then climb the agility shortcut near the Falador Party Room.", anyCrossbow.highlighted(), mithGrappleHighlight);
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
		getInitiateSet.addDialogStep("Can I buy some armor?");
		prayAtAltar = new ObjectStep(this, ObjectID.ALTAR_OF_GUTHIX, new WorldPoint(2925, 3483, 0),
			"Equip your Initiate armor and pray at the Altar of Guthix in Taverley", initiateHelm.equipped(),
			initiateChest.equipped(), initiateLegs.equipped());

		//Mine Gold in Crafting Guild
		goToCraftingGuild = new ObjectStep(this, ObjectID.GUILD_DOOR_14910, new WorldPoint(2933, 3289, 0),
			"Go to the Crafting Guild west of Falador. You will need to equip a brown apron to enter.", brownApron, pickaxe);
		mineGold = new ObjectStep(this, ObjectID.ROCKS_11370, new WorldPoint(2938, 3280, 0),
			"Mine a gold ore.", pickaxe);

		//Dwarven Mines Shortcut
		enterDwarvenMines = new ObjectStep(this, ObjectID.STAIRCASE_16664, new WorldPoint(3058, 3376, 0),
			"Go to the Dwarven Mines.");
		dwarfShortcut = new ObjectStep(this, ObjectID.CREVICE_16543, new WorldPoint(3403, 9806, 0),
			"Squeeze through the crevice in the Dwarven Mines");

		//Chop and burn Willow in Tav
		goToTav = new DetailedQuestStep(this,
			"Go to Taverly, north west of Falador.", axe, tinderbox);
		chopWillowLog = new ObjectStep(this, ObjectID.WILLOW, new WorldPoint(2925, 3412, 0),
			"Chop a Willow Tree while within Taverley.", axe, tinderbox);
		burnWillowLog = new DetailedQuestStep(this,
			"Use your tinderbox on the Willow Logs.", willowLogHighlight, tinderboxHighlight);

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
		return Arrays.asList(bullseyeLantern, tinderbox, airRune4, lawRune2, waterRune1, crystalKey, bronzeSpear, watermelon, rake, emptySack,
			fishingExplosive, mithGrapple, anyCrossbow, initiateHelm, initiateChest, initiateLegs, pickaxe, axe, brownApron, willowBranch6);
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

		req.add(new QuestRequirement(QuestHelperQuest.RATCATCHERS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.RECRUITMENT_DRIVE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SKIPPY_AND_THE_MOGRES, QuestState.FINISHED));

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
			new QuestRequirement(QuestHelperQuest.RECRUITMENT_DRIVE, QuestState.FINISHED), initiateHelm, initiateChest,
			initiateLegs);
		initiateSteps.setDisplayCondition(notPrayAtAltar);
		allSteps.add(initiateSteps);

		PanelDetails crystalChestSteps = new PanelDetails("Lost Elven Treasure",
			Collections.singletonList(unlockCrystalChest), crystalKey);
		crystalChestSteps.setDisplayCondition(notUnlockedCrystalChest);
		allSteps.add(crystalChestSteps);

		PanelDetails willowTavSteps = new PanelDetails("Snoop Loggy-log", Arrays.asList(goToTav, chopWillowLog,
			burnWillowLog), new SkillRequirement(Skill.WOODCUTTING, 30, true),
			new SkillRequirement(Skill.FIREMAKING, 30, true), axe, tinderbox);
		willowTavSteps.setDisplayCondition(notChopBurnWillowTav);
		allSteps.add(willowTavSteps);

		PanelDetails mineGoldSteps = new PanelDetails("Au! I'm minin' here!", Arrays.asList(goToCraftingGuild,
			mineGold), new SkillRequirement(Skill.MINING, 40, true),
			new SkillRequirement(Skill.CRAFTING, 40, true), pickaxe, brownApron);
		mineGoldSteps.setDisplayCondition(notMineGold);
		allSteps.add(mineGoldSteps);

		PanelDetails lanternSteps = new PanelDetails("Bullseye!", Arrays.asList(goToChemist, lightLantern),
			new SkillRequirement(Skill.FIREMAKING, 49, true), tinderbox, bullseyeLantern);
		lanternSteps.setDisplayCondition(notLitLantern);
		allSteps.add(lanternSteps);

		PanelDetails mogreSteps = new PanelDetails("Mogres have layers", Arrays.asList(spawnMogre, killMogre),
			new QuestRequirement(QuestHelperQuest.SKIPPY_AND_THE_MOGRES, QuestState.FINISHED),
			new SkillRequirement(Skill.SLAYER, 32), combatGear, fishingExplosive);
		mogreSteps.setDisplayCondition(notKilledMogre);
		allSteps.add(mogreSteps);

		PanelDetails visitRatsSteps = new PanelDetails("Ahh rats..", Collections.singletonList(visitRatPits));
		visitRatsSteps.setDisplayCondition(notVisitRatPits);
		allSteps.add(visitRatsSteps);

		PanelDetails basketSteps = new PanelDetails("I never knew this existed",
			Collections.singletonList(makeBasketFalLoom), new SkillRequirement(Skill.CRAFTING, 36, true),
			willowBranch6);
		basketSteps.setDisplayCondition(notBasketFalLoom);
		allSteps.add(basketSteps);

		PanelDetails scarecrowSteps = new PanelDetails("Brain not included", Arrays.asList(getHaysack, useSackOnSpear,
			useWatermelonOnSack, placeScarecrow), new SkillRequirement(Skill.FARMING, 23, true),
			emptySack, bronzeSpear, watermelon, scarecrow, rake);
		scarecrowSteps.setDisplayCondition(notPlacedScarecrow);
		allSteps.add(scarecrowSteps);

		PanelDetails grappleSteps = new PanelDetails("To the window.. To the wall!",
			Arrays.asList(grappleNorthWallStart, grappleNorthWallEnd), new SkillRequirement(Skill.AGILITY, 11),
			new SkillRequirement(Skill.STRENGTH, 37), new SkillRequirement(Skill.RANGED, 19),
			mithGrapple, anyCrossbow);
		grappleSteps.setDisplayCondition(notGrappleNorthWall);
		allSteps.add(grappleSteps);

		PanelDetails dwarfShortcutSteps = new PanelDetails("To Middle Earth", Arrays.asList(enterDwarvenMines,
			dwarfShortcut),
			new SkillRequirement(Skill.AGILITY, 42));
		dwarfShortcutSteps.setDisplayCondition(notDwarfShortcut);
		allSteps.add(dwarfShortcutSteps);

		PanelDetails teleFallySteps = new PanelDetails("Beam me up Scotty!",
			Collections.singletonList(teleportToFalador), new SkillRequirement(Skill.MAGIC, 37, true));
		teleFallySteps.setDisplayCondition(notTeleportFalador);
		allSteps.add(teleFallySteps);

		PanelDetails pickGuardSteps = new PanelDetails("Cor blimey mate!", Collections.singletonList(pickpocketGuard),
			new SkillRequirement(Skill.THIEVING, 40));
		pickGuardSteps.setDisplayCondition(notPickpocketGuard);
		allSteps.add(pickGuardSteps);

		PanelDetails teleGrabSteps = new PanelDetails("Yoink!", Arrays.asList(goToChaosTemple, telegrabWine),
			new SkillRequirement(Skill.MAGIC, 33, true), telegrab);
		teleGrabSteps.setDisplayCondition(notTelegrabbedWine);
		allSteps.add(teleGrabSteps);

		PanelDetails finishOffSteps = new PanelDetails("Finishing off", Collections.singletonList(claimReward));
		allSteps.add(finishOffSteps);

		return allSteps;
	}
}
