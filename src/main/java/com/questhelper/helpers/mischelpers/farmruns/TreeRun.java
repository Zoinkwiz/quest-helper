/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz/>
 * Copyright (c) 2024, Kerpackie <https://github.com/Kerpackie/>
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
package com.questhelper.helpers.mischelpers.farmruns;

import com.google.inject.Inject;
import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.FruitTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.GracefulOrFarming;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.HardwoodTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.TreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.PayOrCut;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.PayOrCompost;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.LunarSpells;
import com.questhelper.steps.widget.NormalSpells;
import java.util.Set;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.Tab;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
*
* TODO LIST:
*  Add better direction for using spirit trees and such
* */
public class TreeRun extends ComplexStateQuestHelper
{
	@Inject
	private FarmingWorld farmingWorld;

	@Inject
	private PaymentTracker paymentTracker;

	@Inject
	ItemManager itemManager;

	private FarmingHandler farmingHandler;

	DetailedQuestStep waitForTree;

	// Trees
	DetailedQuestStep farmingGuildTreePatchCheckHealth, lumbridgeTreePatchCheckHealth, faladorTreePatchCheckHealth, taverleyTreePatchCheckHealth, varrockTreePatchCheckHealth,
		gnomeStrongholdTreePatchCheckHealth;
	DetailedQuestStep farmingGuildTreePatchPlant, lumbridgeTreePatchPlant, faladorTreePatchPlant, taverleyTreePatchPlant,
		varrockTreePatchPlant, gnomeStrongholdTreePatchPlant;

	DetailedQuestStep lumbridgeTreePatchClear, faladorTreePatchClear, taverleyTreePatchClear, varrockTreePatchClear,
		gnomeStrongholdTreePatchClear, farmingGuildTreePatchClear;
	DetailedQuestStep lumbridgeTreePatchDig, faladorTreePatchDig, taverleyTreePatchDig, varrockTreePatchDig,
		gnomeStrongholdTreePatchDig, farmingGuildTreePatchDig;

	DetailedQuestStep farmingGuildTreePayForProtection, lumbridgeTreeProtect, faladorTreeProtect, taverleyTreeProtect, varrockTreeProtect, strongholdTreeProtect;

	// Fruit Trees
	DetailedQuestStep farmingGuildFruitTreePatchCheckHealth, gnomeStrongholdFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchCheckHealth,
		brimhavenFruitTreePatchCheckHealth, lletyaFruitTreePatchCheckHealth, catherbyFruitTreePatchCheckHealth;
	DetailedQuestStep farmingGuildFruitTreePatchPlant, gnomeStrongholdFruitTreePatchPlant, gnomeVillageFruitTreePatchPlant,
		brimhavenFruitTreePatchPlant, lletyaFruitTreePatchPlant, catherbyFruitTreePatchPlant;

	DetailedQuestStep farmingGuildFruitTreePatchClear, gnomeStrongholdFruitTreePatchClear, gnomeVillageFruitTreePatchClear,
		brimhavenFruitTreePatchClear, lletyaFruitTreePatchClear, catherbyFruitTreePatchClear;
	DetailedQuestStep farmingGuildFruitTreePatchDig, gnomeStrongholdFruitTreePatchDig, gnomeVillageFruitTreePatchDig,
		brimhavenFruitTreePatchDig, lletyaFruitTreePatchDig, catherbyFruitTreePatchDig;

	DetailedQuestStep guildFruitProtect, strongholdFruitProtect, villageFruitProtect, brimhavenFruitProtect,
		lletyaFruitProtect, catherbyFruitProtect;

	// Hardwood Trees
	DetailedQuestStep eastHardwoodTreePatchCheckHealth, westHardwoodTreePatchCheckHealth, middleHardwoodTreePatchCheckHealth, savannahCheckHealth;
	DetailedQuestStep eastHardwoodTreePatchPlant, westHardwoodTreePatchPlant, middleHardwoodTreePatchPlant, savannahPlant;
	DetailedQuestStep eastHardwoodTreePatchDig, westHardwoodTreePatchDig, middleHardwoodTreePatchDig, savannahDig;

	DetailedQuestStep eastHardwoodTreePatchClear, westHardwoodTreePatchClear, middleHardwoodTreePatchClear, savannahClear;

	DetailedQuestStep eastHardwoodProtect, westHardwoodProtect, middleHardwoodProtect, savannahProtect;

	// Farming Items
	ItemRequirement coins, spade, rake, allTreeSaplings, treeSapling, allFruitSaplings, fruitTreeSapling, allHardwoodSaplings, hardwoodSapling, compost, axe,
		protectionItemTree, allProtectionItemTree, protectionItemFruitTree, allProtectionItemFruitTree, protectionItemHardwood, allProtectionItemHardwood;

	// Teleport Items
	// TODO: Add these...
	ItemRequirement farmingGuildTeleport, crystalTeleport, catherbyTeleport, varrockTeleport, lumbridgeTeleport, faladorTeleport, fossilIslandTeleport;

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland, accessToSavannah;

	Requirement payingForRemoval, payingForProtection, usingCompostorNothing;

	PatchStates faladorStates, lumbridgeStates, farmingGuildTreeStates, taverleyStates, varrockStates, gnomeStrongholdTreeStates;

	PatchStates gnomeStrongholdFruitStates, gnomeVillageStates, brimhavenStates, catherbyStates, lletyaStates, farmingGuildFruitStates;

	PatchStates eastHardwoodStates, middleHardwoodStates, westHardwoodStates, savannahStates;

	private final String PAY_OR_CUT = "payOrCutTree";
	private final String PAY_OR_COMPOST = "payOrCompostTree";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	public QuestStep loadStep()
	{
		setupSteps();
		farmingHandler = new FarmingHandler(client, configManager);

		ConditionalStep steps = new ConditionalStep(this, waitForTree, spade, coins, rake, compost
			, farmersOutfit, gracefulOutfit);

		// Farming Guild Tree -> Farming Guild Fruit Tree -> Lumbridge -> Falador -> Taverley
		// Varrock -> Gnome Stronghold Fruit -> Gnome Stronghold Tree -> Gnome Village -> Brimhaven
		// -> catherby -> lletya -> east hardwood -> middle hardwood -> west hardwood.


		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreeStates.getIsUnchecked()), farmingGuildTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreeStates.getIsHarvestable()), farmingGuildTreePatchClear);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreeStates.getIsStump()), farmingGuildTreePatchDig);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreeStates.getIsEmpty()), farmingGuildTreePatchPlant);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, nor(farmingGuildTreeStates.getIsProtected(), usingCompostorNothing)), farmingGuildTreePayForProtection);

		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsUnchecked()), farmingGuildFruitTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsHarvestable()), farmingGuildFruitTreePatchClear);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsStump()), farmingGuildTreePatchDig);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsEmpty()), farmingGuildTreePatchPlant);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, nor(farmingGuildFruitStates.getIsProtected(), usingCompostorNothing)), guildFruitProtect);

		steps.addStep(lumbridgeStates.getIsUnchecked(), lumbridgeTreePatchCheckHealth);
		steps.addStep(lumbridgeStates.getIsEmpty(), lumbridgeTreePatchPlant);
		steps.addStep(lumbridgeStates.getIsHarvestable(), lumbridgeTreePatchClear);
		steps.addStep(lumbridgeStates.getIsStump(), lumbridgeTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, lumbridgeStates.getIsProtected()), lumbridgeTreeProtect);

		steps.addStep(faladorStates.getIsUnchecked(), faladorTreePatchCheckHealth);
		steps.addStep(faladorStates.getIsEmpty(), faladorTreePatchPlant);
		steps.addStep(faladorStates.getIsHarvestable(), faladorTreePatchClear);
		steps.addStep(faladorStates.getIsStump(), faladorTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, faladorStates.getIsProtected()), faladorTreeProtect);

		steps.addStep(taverleyStates.getIsUnchecked(), taverleyTreePatchCheckHealth);
		steps.addStep(taverleyStates.getIsEmpty(), taverleyTreePatchPlant);
		steps.addStep(taverleyStates.getIsHarvestable(), taverleyTreePatchClear);
		steps.addStep(taverleyStates.getIsStump(), taverleyTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, taverleyStates.getIsProtected()), taverleyTreeProtect);

		steps.addStep(varrockStates.getIsUnchecked(), varrockTreePatchCheckHealth);
		steps.addStep(varrockStates.getIsEmpty(), varrockTreePatchPlant);
		steps.addStep(varrockStates.getIsHarvestable(), varrockTreePatchClear);
		steps.addStep(varrockStates.getIsStump(), varrockTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, varrockStates.getIsProtected()), varrockTreeProtect);

		steps.addStep(gnomeStrongholdFruitStates.getIsUnchecked(), gnomeStrongholdFruitTreePatchCheckHealth);
		steps.addStep(gnomeStrongholdFruitStates.getIsEmpty(), gnomeStrongholdFruitTreePatchPlant);
		steps.addStep(gnomeStrongholdFruitStates.getIsHarvestable(), gnomeStrongholdFruitTreePatchClear);
		steps.addStep(gnomeStrongholdFruitStates.getIsStump(), gnomeStrongholdFruitTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, gnomeStrongholdFruitStates.getIsProtected()), strongholdFruitProtect);

		steps.addStep(gnomeStrongholdTreeStates.getIsUnchecked(), gnomeStrongholdTreePatchCheckHealth);
		steps.addStep(gnomeStrongholdTreeStates.getIsEmpty(), gnomeStrongholdTreePatchPlant);
		steps.addStep(gnomeStrongholdTreeStates.getIsHarvestable(), gnomeStrongholdTreePatchClear);
		steps.addStep(gnomeStrongholdTreeStates.getIsStump(), gnomeStrongholdTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, gnomeStrongholdTreeStates.getIsProtected()), strongholdTreeProtect);

		steps.addStep(gnomeVillageStates.getIsUnchecked(), gnomeVillageFruitTreePatchCheckHealth);
		steps.addStep(gnomeVillageStates.getIsEmpty(), gnomeVillageFruitTreePatchPlant);
		steps.addStep(gnomeVillageStates.getIsHarvestable(), gnomeVillageFruitTreePatchClear);
		steps.addStep(gnomeVillageStates.getIsStump(), gnomeVillageFruitTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, gnomeVillageStates.getIsProtected()), villageFruitProtect);

		steps.addStep(brimhavenStates.getIsUnchecked(), brimhavenFruitTreePatchCheckHealth);
		steps.addStep(brimhavenStates.getIsEmpty(), brimhavenFruitTreePatchPlant);
		steps.addStep(brimhavenStates.getIsHarvestable(), brimhavenFruitTreePatchClear);
		steps.addStep(brimhavenStates.getIsStump(), brimhavenFruitTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, brimhavenStates.getIsProtected()), brimhavenFruitProtect);

		steps.addStep(catherbyStates.getIsUnchecked(), catherbyFruitTreePatchCheckHealth);
		steps.addStep(catherbyStates.getIsEmpty(), catherbyFruitTreePatchPlant);
		steps.addStep(catherbyStates.getIsHarvestable(), catherbyFruitTreePatchClear);
		steps.addStep(catherbyStates.getIsStump(), catherbyFruitTreePatchDig);
		steps.addStep(nor(usingCompostorNothing, catherbyStates.getIsProtected()), catherbyFruitProtect);

		steps.addStep(new Conditions(accessToLletya, lletyaStates.getIsUnchecked()), lletyaFruitTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToLletya, lletyaStates.getIsEmpty()), lletyaFruitTreePatchPlant);
		steps.addStep(new Conditions(accessToLletya, lletyaStates.getIsHarvestable()), lletyaFruitTreePatchClear);
		steps.addStep(new Conditions(accessToLletya, lletyaStates.getIsStump()), lletyaFruitTreePatchDig);
		steps.addStep(new Conditions(accessToLletya, nor(usingCompostorNothing, lletyaStates.getIsProtected())), lletyaFruitProtect);

		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodStates.getIsUnchecked()), eastHardwoodTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodStates.getIsEmpty()), eastHardwoodTreePatchPlant);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodStates.getIsHarvestable()), eastHardwoodTreePatchClear);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodStates.getIsStump()), eastHardwoodTreePatchDig);
		steps.addStep(new Conditions(accessToFossilIsland,nor(usingCompostorNothing,  eastHardwoodStates.getIsProtected())), eastHardwoodProtect);

		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodStates.getIsUnchecked()), middleHardwoodTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodStates.getIsEmpty()), middleHardwoodTreePatchPlant);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodStates.getIsHarvestable()), middleHardwoodTreePatchClear);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodStates.getIsStump()), middleHardwoodTreePatchDig);
		steps.addStep(new Conditions(accessToFossilIsland, nor(usingCompostorNothing, middleHardwoodStates.getIsProtected())), middleHardwoodProtect);

		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodStates.getIsUnchecked()), westHardwoodTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodStates.getIsEmpty()), westHardwoodTreePatchPlant);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodStates.getIsHarvestable()), westHardwoodTreePatchClear);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodStates.getIsStump()), westHardwoodTreePatchDig);
		steps.addStep(new Conditions(accessToFossilIsland, nor(usingCompostorNothing, westHardwoodStates.getIsProtected())), westHardwoodProtect);

		steps.addStep(new Conditions(accessToSavannah, savannahStates.getIsUnchecked()), savannahCheckHealth);
		steps.addStep(new Conditions(accessToSavannah, savannahStates.getIsEmpty()), savannahPlant);
		steps.addStep(new Conditions(accessToSavannah, savannahStates.getIsHarvestable()), savannahClear);
		steps.addStep(new Conditions(accessToSavannah, savannahStates.getIsStump()), savannahDig);
		steps.addStep(new Conditions(accessToSavannah, nor(usingCompostorNothing, savannahStates.getIsProtected())), savannahProtect);

		return steps;
	}

	private void setupConditions()
	{
		// Tree Patch Ready Requirements

		// Access Requirements
		// ME1 partial completion required only, however much easier to access when finished.
		accessToLletya = new QuestRequirement(QuestHelperQuest.MOURNINGS_END_PART_I, QuestState.FINISHED);
		accessToFossilIsland = new QuestRequirement(QuestHelperQuest.BONE_VOYAGE, QuestState.FINISHED);
		accessToFarmingGuildTreePatch = new Conditions(
			new SkillRequirement(Skill.FARMING, 65)
		);
		accessToFarmingGuildFruitTreePatch = new Conditions(
			new SkillRequirement(Skill.FARMING, 85)
		);
		accessToSavannah = new QuestRequirement(QuestHelperQuest.THE_RIBBITING_TALE_OF_A_LILY_PAD_LABOUR_DISPUTE, QuestState.FINISHED);

		// Trees
		lumbridgeStates = new PatchStates("Lumbridge");
		faladorStates = new PatchStates("Falador");
		taverleyStates = new PatchStates("Taverley");
		varrockStates = new PatchStates("Varrock");
		gnomeStrongholdTreeStates = new PatchStates("Gnome Stronghold");
		farmingGuildTreeStates = new PatchStates("Farming Guild", accessToFarmingGuildFruitTreePatch);

		// Fruit trees
		catherbyStates = new PatchStates("Catherby");
		brimhavenStates = new PatchStates("Brimhaven");
		gnomeVillageStates = new PatchStates("Tree Gnome Village");
		gnomeStrongholdFruitStates = new PatchStates("Gnome Stronghold");
		lletyaStates = new PatchStates("Lletya", accessToLletya);
		farmingGuildFruitStates = new PatchStates("Farming Guild", accessToFarmingGuildFruitTreePatch);

		westHardwoodStates = new PatchStates("Fossil Island", "West");
		middleHardwoodStates = new PatchStates("Fossil Island", "Middle");
		eastHardwoodStates = new PatchStates("Fossil Island", "East");
		savannahStates = new PatchStates("Avium Savannah", accessToSavannah);

		payingForRemoval = new RuneliteRequirement(configManager, PAY_OR_CUT, PayOrCut.PAY.name());
		payingForProtection = new RuneliteRequirement(configManager, PAY_OR_COMPOST, PayOrCompost.PAY.name());
		usingCompostorNothing = or(new RuneliteRequirement(configManager, PAY_OR_COMPOST, PayOrCompost.COMPOST.name()),
			new RuneliteRequirement(configManager, PAY_OR_COMPOST, PayOrCompost.NEITHER.name()));
	}

	@Override
	public void setupRequirements()
	{
		setupConditions();
		// Farming Item Requirements
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2));
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS_995)
			.showConditioned(payingForRemoval);
		axe = new ItemRequirement("Any axe you can use", ItemCollections.AXES);

		TreeSapling treeSaplingEnum = (TreeSapling) FarmingUtils.getEnumFromConfig(configManager, TreeSapling.MAGIC);
		treeSapling = treeSaplingEnum.getPlantableItemRequirement(itemManager);
		treeSapling.setHighlightInInventory(true);
		allTreeSaplings = treeSapling.copy();

		protectionItemTree = treeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemTree.addAlternates(protectionItemTree.getId() + 1);
		allProtectionItemTree = protectionItemTree.copy();

		FruitTreeSapling fruitTreeSaplingEnum = (FruitTreeSapling) FarmingUtils.getEnumFromConfig(configManager, FruitTreeSapling.APPLE);
		fruitTreeSapling = fruitTreeSaplingEnum.getPlantableItemRequirement(itemManager);
		fruitTreeSapling.setHighlightInInventory(true);
		allFruitSaplings = fruitTreeSapling.copy();

		protectionItemFruitTree = fruitTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemFruitTree.addAlternates(protectionItemFruitTree.getId() + 1);
		allProtectionItemFruitTree = protectionItemFruitTree.copy();

		HardwoodTreeSapling hardwoodTreeSaplingEnum = (HardwoodTreeSapling) FarmingUtils.getEnumFromConfig(configManager, HardwoodTreeSapling.TEAK);
		hardwoodSapling = hardwoodTreeSaplingEnum.getPlantableItemRequirement(itemManager);
		hardwoodSapling.setHighlightInInventory(true);
		allHardwoodSaplings = hardwoodSapling.copy();

		protectionItemHardwood = hardwoodTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemHardwood.addAlternates(protectionItemHardwood.getId() + 1);
		allProtectionItemHardwood = protectionItemHardwood.copy();

		compost	= new ItemRequirement("Compost", ItemCollections.COMPOST).showConditioned(usingCompostorNothing);
		compost.setDisplayMatchedItemName(true);

		// Teleport Items
		farmingGuildTeleport = new ItemRequirement("Farming Guild Teleport", ItemCollections.SKILLS_NECKLACES);
		crystalTeleport = new ItemRequirement("Crystal teleport", ItemCollections.TELEPORT_CRYSTAL);
		catherbyTeleport = new ItemRequirement("Catherby teleport", ItemID.CATHERBY_TELEPORT);
		catherbyTeleport.addAlternates(ItemID.CAMELOT_TELEPORT);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.LUMBRIDGE_TELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemCollections.RING_OF_WEALTHS);
		faladorTeleport.addAlternates(ItemID.FALADOR_TELEPORT);
		fossilIslandTeleport = new ItemRequirement("Teleport to Fossil Island", ItemCollections.DIGSITE_PENDANTS);


		// Graceful and Farming Outfit
		gracefulHood = new ItemRequirement(
			"Graceful hood", ItemCollections.GRACEFUL_HOOD, 1 ,true).isNotConsumed();

		gracefulTop = new ItemRequirement(
			"Graceful top", ItemCollections.GRACEFUL_TOP, 1, true).isNotConsumed();

		gracefulLegs = new ItemRequirement(
			"Graceful legs", ItemCollections.GRACEFUL_LEGS, 1, true).isNotConsumed();

		gracefulCape = new ItemRequirement(
			"Graceful cape", ItemCollections.GRACEFUL_CAPE, 1, true).isNotConsumed();

		gracefulGloves = new ItemRequirement(
			"Graceful gloves", ItemCollections.GRACEFUL_GLOVES, 1, true).isNotConsumed();

		gracefulBoots = new ItemRequirement(
			"Graceful boots", ItemCollections.GRACEFUL_BOOTS, 1, true).isNotConsumed();
		gracefulBoots.addAlternates(ItemID.BOOTS_OF_LIGHTNESS);

		gracefulOutfit = new ItemRequirements(
			"Graceful outfit (equipped)",
			gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape
		).isNotConsumed().showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.GRACEFUL.name()));


		farmingHat = new ItemRequirement(
			"Farmer's strawhat", ItemID.FARMERS_STRAWHAT, 1 ,true).isNotConsumed();
		farmingHat.addAlternates(ItemID.FARMERS_STRAWHAT_13647, ItemID.FARMERS_STRAWHAT_21253, ItemID.FARMERS_STRAWHAT_21254);

		farmingTop = new ItemRequirement(
			"Farmer's top", ItemID.FARMERS_JACKET, 1, true).isNotConsumed();
		farmingTop.addAlternates(ItemID.FARMERS_SHIRT);

		farmingLegs = new ItemRequirement(
			"Farmer's boro trousers", ItemID.FARMERS_BORO_TROUSERS, 1, true).isNotConsumed();
		farmingLegs.addAlternates(ItemID.FARMERS_BORO_TROUSERS_13641);

		farmingBoots = new ItemRequirement(
			"Graceful cape", ItemID.FARMERS_BOOTS, 1, true).isNotConsumed();
		farmingBoots.addAlternates(ItemID.FARMERS_BOOTS_13645);

		farmersOutfit = new ItemRequirements(
			"Farmer's outfit (equipped)",
			farmingHat, farmingTop, farmingLegs, farmingBoots).isNotConsumed()
			.showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.FARMING.name()));
	}

	private void setupSteps()
	{
		waitForTree = new DetailedQuestStep(this, "Wait for your trees to grow! This may take a while..!");

		// Tree Patch Clear Steps

		lumbridgeTreePatchClear = new NpcStep(this, NpcID.FAYETH, new WorldPoint(3193, 3231, 0),
			"Speak to Fayeth to clear the patch.");
		lumbridgeTreePatchClear.addDialogStep("Yes.");

		faladorTreePatchClear = new NpcStep(this, NpcID.HESKEL, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to clear the patch.");
		faladorTreePatchClear.addDialogStep("Yes.");

		taverleyTreePatchClear = new NpcStep(this, NpcID.ALAIN, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to clear the patch.");
		taverleyTreePatchClear.addDialogStep("Yes.");

		varrockTreePatchClear = new NpcStep(this, NpcID.TREZNOR_11957, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to clear the patch.");
		varrockTreePatchClear.addDialogStep("Yes.");

		gnomeStrongholdTreePatchClear = new NpcStep(this, NpcID.PRISSY_SCILLA, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to clear the patch.");
		gnomeStrongholdTreePatchClear.addDialogStep("Yes.");

		farmingGuildTreePatchClear = new NpcStep(this, NpcID.ROSIE, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to clear the patch.");
		farmingGuildTreePatchClear.addDialogStep("Yes.");

		lumbridgeTreeProtect = new NpcStep(this, NpcID.FAYETH, new WorldPoint(3193, 3231, 0),
			"Speak to Fayeth to protect the patch.");
		lumbridgeTreeProtect.addDialogStep("Yes.");

		faladorTreeProtect = new NpcStep(this, NpcID.HESKEL, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to protect the patch.");
		faladorTreeProtect.addDialogStep("Yes.");

		taverleyTreeProtect = new NpcStep(this, NpcID.ALAIN, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to protect the patch.");
		taverleyTreeProtect.addDialogStep("Yes.");

		varrockTreeProtect = new NpcStep(this, NpcID.TREZNOR_11957, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to protect the patch.");
		varrockTreeProtect.addDialogStep("Yes.");

		strongholdTreeProtect = new NpcStep(this, NpcID.PRISSY_SCILLA, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to protect the patch.");
		strongholdTreeProtect.addDialogStep("Yes.");

		farmingGuildTreePayForProtection = new NpcStep(this, NpcID.ROSIE, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to protect the patch.");
		farmingGuildTreePayForProtection.addDialogStep("Yes.");

		// Tree Patch Steps
		lumbridgeTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_8391, new WorldPoint(3193, 3231, 0),
			"Check the health of the tree planted in Lumbridge.");
		lumbridgeTreePatchCheckHealth.addTeleport(lumbridgeTeleport);
		lumbridgeTreePatchCheckHealth.addSpellHighlight(NormalSpells.LUMBRIDGE_TELEPORT);
		lumbridgeTreePatchCheckHealth.addSpellHighlight(NormalSpells.LUMBRIDGE_HOME_TELEPORT);
		faladorTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Check the health of the tree planted in Falador.");
		faladorTreePatchCheckHealth.addTeleport(faladorTeleport);
		faladorTreePatchCheckHealth.addSpellHighlight(NormalSpells.FALADOR_TELEPORT);
		taverleyTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2936, 3438, 0),
			"Check the health of the tree planted in Taverley.");
		varrockTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3229, 3459, 0),
			"Check the health of the tree planted in Varrock.");
		varrockTreePatchCheckHealth.addTeleport(varrockTeleport);
		varrockTreePatchCheckHealth.addSpellHighlight(NormalSpells.VARROCK_TELEPORT);
		gnomeStrongholdTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2436, 3415, 0),
			"Check the health of the tree planted in the Tree Gnome Stronghold.");
		farmingGuildTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1232, 3736, 0),
			"Check the health of the tree planted in the Farming Guild.");
		farmingGuildTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));
		farmingGuildTreePatchCheckHealth.addTeleport(farmingGuildTeleport);

		// Tree Plant Steps
		lumbridgeTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8391, new WorldPoint(3193, 3231, 0),
			"Plant your sapling in the Lumbridge patch.", treeSapling);
		lumbridgeTreePatchPlant.addIcon(treeSapling.getId());
		lumbridgeTreePatchCheckHealth.addSubSteps(lumbridgeTreePatchPlant);

		faladorTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Plant your sapling in the Falador patch.", treeSapling);
		faladorTreePatchPlant.addIcon(treeSapling.getId());
		faladorTreePatchCheckHealth.addSubSteps(faladorTreePatchPlant);

		taverleyTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2936, 3438, 0),
			"Plant your sapling in the Taverley patch.", treeSapling);
		taverleyTreePatchPlant.addIcon(treeSapling.getId());
		taverleyTreePatchCheckHealth.addSubSteps(taverleyTreePatchPlant);

		varrockTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3229, 3459, 0),
			"Plant your sapling in the Varrock patch.", treeSapling);
		varrockTreePatchPlant.addIcon(treeSapling.getId());
		varrockTreePatchCheckHealth.addSubSteps(varrockTreePatchPlant);

		gnomeStrongholdTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2436, 3415, 0),
			"Plant your sapling in the Gnome Stronghold patch.", treeSapling);
		gnomeStrongholdTreePatchPlant.addIcon(treeSapling.getId());
		gnomeStrongholdTreePatchCheckHealth.addSubSteps(gnomeStrongholdTreePatchPlant);

		farmingGuildTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1232, 3736, 0),
			"Plant your sapling in the Farming Guild tree patch.", treeSapling);
		farmingGuildTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));
		farmingGuildTreePatchPlant.addIcon(treeSapling.getId());
		farmingGuildTreePatchCheckHealth.addSubSteps(farmingGuildTreePatchPlant);

		// Dig
		lumbridgeTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8391, new WorldPoint(3193, 3231, 0),
			"Dig up the tree stump in Lumbridge.");
		faladorTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Dig up the tree stump in Falador.");
		taverleyTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2936, 3438, 0),
			"Dig up the tree stump in Taverley.");
		varrockTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3229, 3459, 0),
			"Dig up the tree stump in Varrock.");
		gnomeStrongholdTreePatchDig = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2436, 3415, 0),
			"Dig up the tree stump in the Tree Gnome Stronghold.");
		farmingGuildTreePatchDig = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1232, 3736, 0),
			"Dig up the tree stump in the Farming Guild tree patch.");

		faladorTreePatchClear.addSubSteps(faladorTreePatchDig, faladorTreeProtect);
		taverleyTreePatchClear.addSubSteps(taverleyTreePatchDig, taverleyTreeProtect);
		varrockTreePatchClear.addSubSteps(varrockTreePatchDig, varrockTreeProtect);
		gnomeStrongholdTreePatchClear.addSubSteps(gnomeStrongholdTreePatchDig, strongholdTreeProtect);
		lumbridgeTreePatchClear.addSubSteps(lumbridgeTreePatchDig, lumbridgeTreeProtect);
		farmingGuildTreePatchClear.addSubSteps(farmingGuildTreePatchDig, farmingGuildTreePatchDig);

		// Fruit Tree Steps
		gnomeStrongholdFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Check the health of the fruit tree planted in the Tree Gnome Stronghold.");
		gnomeStrongholdFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Gnome Stronghold", true);
		gnomeVillageFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Check the health of the fruit tree planted outside the Tree Gnome Village. Follow Elkoy to get out quickly.");
		gnomeVillageFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Tree Gnome Village", true);
		brimhavenFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Check the health of the fruit tree planted in Brimhaven.");
		brimhavenFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Brimhaven", true);
		catherbyFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby.");
		catherbyFruitTreePatchCheckHealth.addTeleport(catherbyTeleport);
		catherbyFruitTreePatchCheckHealth.addSpellHighlight(NormalSpells.CAMELOT_TELEPORT);
		catherbyFruitTreePatchCheckHealth.addSpellHighlight(LunarSpells.CATHERBY_TELEPORT);

		lletyaFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchCheckHealth.addTeleport(crystalTeleport);

		farmingGuildFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1242, 3758, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchCheckHealth.addTeleport(farmingGuildTeleport);
		farmingGuildFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Farming Guild", true);

		// Fruit Tree Plant Steps
		gnomeStrongholdFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Plant your sapling in the Tree Gnome Stronghold patch.", fruitTreeSapling);
		gnomeStrongholdFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		gnomeStrongholdTreePatchCheckHealth.addSubSteps(gnomeStrongholdTreePatchPlant);

		gnomeVillageFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Plant your sapling in the Tree Gnome Village patch. Follow Elkoy to get out quickly.", fruitTreeSapling);
		gnomeVillageFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		gnomeVillageFruitTreePatchCheckHealth.addSubSteps(gnomeVillageFruitTreePatchPlant);

		brimhavenFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Plant your sapling in the Brimhaven patch.", fruitTreeSapling);
		brimhavenFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		brimhavenFruitTreePatchCheckHealth.addSubSteps(brimhavenFruitTreePatchPlant);

		// Plant
		catherbyFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2860, 3433, 0),
			"Plant your sapling in the Catherby patch.", fruitTreeSapling);
		catherbyFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		catherbyFruitTreePatchCheckHealth.addSubSteps(catherbyFruitTreePatchPlant);

		lletyaFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Plant your sapling in the Lletya patch.", fruitTreeSapling);
		lletyaFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		lletyaFruitTreePatchCheckHealth.addSubSteps(lletyaFruitTreePatchPlant);

		farmingGuildFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1242, 3758, 0),
			"Plant your sapling in the Farming Guild patch.", fruitTreeSapling);
		farmingGuildFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		farmingGuildFruitTreePatchCheckHealth.addSubSteps(farmingGuildFruitTreePatchPlant);

		// Clear
		gnomeStrongholdFruitTreePatchClear = new NpcStep(this, NpcID.BOLONGO, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeStrongholdFruitTreePatchClear.addDialogStep("Yes.");
		gnomeVillageFruitTreePatchClear = new NpcStep(this, NpcID.GILETH, new WorldPoint(2490, 3180, 0),
			"Pay Gileth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeVillageFruitTreePatchClear.addDialogStep("Yes.");
		brimhavenFruitTreePatchClear = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		brimhavenFruitTreePatchClear.addDialogStep("Yes.");
		catherbyFruitTreePatchClear = new NpcStep(this, NpcID.ELLENA, new WorldPoint(2860, 3433, 0),
			"Pay Ellena 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		catherbyFruitTreePatchClear.addDialogStep("Yes.");
		lletyaFruitTreePatchClear = new NpcStep(this, NpcID.LILIWEN, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		lletyaFruitTreePatchClear.addDialogStep("Yes.");
		farmingGuildFruitTreePatchClear = new NpcStep(this, NpcID.NIKKIE, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		farmingGuildFruitTreePatchClear.addDialogStep("Yes.");

		strongholdFruitProtect = new NpcStep(this, NpcID.BOLONGO, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo to protect the patch.");
		strongholdFruitProtect.addDialogStep("Yes.");
		villageFruitProtect = new NpcStep(this, NpcID.GILETH, new WorldPoint(2490, 3180, 0),
			"Pay Gileth to protect the patch.");
		villageFruitProtect.addDialogStep("Yes.");
		brimhavenFruitProtect = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth to protect the patch.");
		brimhavenFruitProtect.addDialogStep("Yes.");
		catherbyFruitProtect = new NpcStep(this, NpcID.ELLENA, new WorldPoint(2860, 3433, 0),
			"Pay Ellena to protect the patch.");
		catherbyFruitProtect.addDialogStep("Yes.");
		lletyaFruitProtect = new NpcStep(this, NpcID.LILIWEN, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen to protect the patch.");
		lletyaFruitProtect.addDialogStep("Yes.");
		guildFruitProtect = new NpcStep(this, NpcID.NIKKIE, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie to protect the patch.");
		guildFruitProtect.addDialogStep("Yes.");

			// Dig Fruit Tree Steps
		gnomeStrongholdFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Dig up the fruit tree's stump in the Tree Gnome Stronghold.");
		gnomeVillageFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Dig up the fruit tree's stump outside the Tree Gnome Village.");
		brimhavenFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Dig up the fruit tree's stump in Brimhaven.");
		catherbyFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby");
		lletyaFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Dig up the fruit tree's stump in Lletya.");
		farmingGuildFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1242, 3758, 0),
			"Dig up the fruit tree's stump in the Farming Guild.");

		gnomeStrongholdFruitTreePatchClear.addSubSteps(gnomeStrongholdFruitTreePatchDig, strongholdFruitProtect);
		gnomeVillageFruitTreePatchClear.addSubSteps(gnomeVillageFruitTreePatchDig, villageFruitProtect);
		brimhavenFruitTreePatchClear.addSubSteps(brimhavenFruitTreePatchDig, brimhavenFruitProtect);
		catherbyFruitTreePatchClear.addSubSteps(catherbyFruitTreePatchDig, catherbyFruitProtect);
		lletyaFruitTreePatchClear.addSubSteps(lletyaFruitTreePatchDig, lletyaFruitProtect);
		farmingGuildFruitTreePatchClear.addSubSteps(farmingGuildFruitTreePatchDig, guildFruitProtect);

		// Hardwood Tree Steps
		westHardwoodTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Check the health of the western hardwood tree on Fossil Island.");
		middleHardwoodTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Check the health of the centre hardwood tree on Fossil Island.");
		eastHardwoodTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Check the health of the eastern hardwood tree on Fossil Island.");
		savannahCheckHealth  = new ObjectStep(this, NullObjectID.NULL_50692, new WorldPoint(1687, 2972, 0),
			"Check the health of the hardwood tree in the Avium Savannah.");

		// Hardwood Tree Plant Steps
		westHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Plant your sapling on the western hardwood tree patch on Fossil Island.", hardwoodSapling);
		westHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		westHardwoodTreePatchCheckHealth.addSubSteps(westHardwoodTreePatchPlant);

		middleHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Plant your sapling on the centre hardwood tree patch on Fossil Island.", hardwoodSapling);
		middleHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		middleHardwoodTreePatchCheckHealth.addSubSteps(middleHardwoodTreePatchPlant);

		eastHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Plant your sapling on the eastern hardwood tree patch on Fossil Island.", hardwoodSapling);
		eastHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		eastHardwoodTreePatchCheckHealth.addSubSteps(eastHardwoodTreePatchPlant);

		savannahPlant = new ObjectStep(this, NullObjectID.NULL_50692, new WorldPoint(1687, 2972, 0),
			"Plant your sapling into the hardwood tree in the hardwood tree patch in the Avium Savannah.", hardwoodSapling);

		westHardwoodTreePatchClear = new NpcStep(this, NpcID.SQUIRREL_7756, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to remove the west tree.");
		middleHardwoodTreePatchClear = new NpcStep(this, NpcID.SQUIRREL_7755, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to remove the middle tree.");
		eastHardwoodTreePatchClear = new NpcStep(this, NpcID.SQUIRREL_7754, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to remove the east tree.");

		savannahClear = new NpcStep(this, NpcID.MARCELLUS_12936, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to clear the tree.");

		westHardwoodTreePatchDig = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Dig up the western hardwood tree's stump on Fossil Island.");
		middleHardwoodTreePatchDig = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Dig up the centre hardwood tree's stump on Fossil Island.");
		eastHardwoodTreePatchDig = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Dig up the eastern hardwood tree's stump on Fossil Island.");
		savannahDig = new ObjectStep(this, NullObjectID.NULL_50692, new WorldPoint(1687, 2972, 0),
			"Dig up the Savannah hardwood tree's stump.");

		westHardwoodProtect = new NpcStep(this, NpcID.SQUIRREL_7756, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to protect the west tree.");
		middleHardwoodProtect = new NpcStep(this, NpcID.SQUIRREL_7755, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to protect the middle tree.");
		eastHardwoodProtect = new NpcStep(this, NpcID.SQUIRREL_7754, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to protect the east tree.");
		savannahProtect = new NpcStep(this, NpcID.MARCELLUS_12936, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to protect the hardwood tree.");

		westHardwoodTreePatchClear.addSubSteps(westHardwoodTreePatchDig, westHardwoodProtect);
		middleHardwoodTreePatchClear.addSubSteps(middleHardwoodTreePatchDig, middleHardwoodProtect);
		eastHardwoodTreePatchClear.addSubSteps(eastHardwoodTreePatchDig, eastHardwoodProtect);
		savannahClear.addSubSteps(savannahDig, savannahProtect);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		coins.setQuantity(0);
		allProtectionItemTree.setQuantity(protectionItemTree.getQuantity());
		allProtectionItemFruitTree.setQuantity(protectionItemFruitTree.getQuantity());
		allProtectionItemHardwood.setQuantity(protectionItemHardwood.getQuantity());
		handleTreePatches(PatchImplementation.TREE,
			List.of(farmingGuildTreeStates, varrockStates, faladorStates, taverleyStates, lumbridgeStates, gnomeStrongholdTreeStates),
			farmingWorld.getTabs().get(Tab.TREE), allTreeSaplings, allProtectionItemTree);
		handleTreePatches(PatchImplementation.FRUIT_TREE,
			List.of(farmingGuildFruitStates, brimhavenStates, catherbyStates, gnomeStrongholdFruitStates, gnomeVillageStates, lletyaStates),
			farmingWorld.getTabs().get(Tab.FRUIT_TREE), allFruitSaplings, allProtectionItemFruitTree);
		handleTreePatches(PatchImplementation.HARDWOOD_TREE, List.of(westHardwoodStates, middleHardwoodStates, eastHardwoodStates, savannahStates),
			farmingWorld.getTabs().get(Tab.TREE), allHardwoodSaplings, allProtectionItemHardwood);
	}

	public void handleTreePatches(PatchImplementation implementation, List<PatchStates> regions, Set<FarmingPatch> patches, ItemRequirement allSaplings, ItemRequirement allPayment)
	{
		int numberOfSaplings = 0;
		for (FarmingPatch patch : patches)
		{
			if (patch.getImplementation() != implementation)
			{
				continue;
			}

			CropState state = farmingHandler.predictPatch(patch);
			boolean isPlantable = state == CropState.EMPTY || state == CropState.DEAD;
			boolean isUnchecked = state == CropState.UNCHECKED; // 'Check health'
			boolean isHarvestable = state == CropState.HARVESTABLE; // 'Chop'
			boolean isStump = state == CropState.STUMP; // 'Clear'
			boolean isProtected = paymentTracker.getProtectedState(patch);

			if (state != CropState.GROWING)
			{
				numberOfSaplings++;
			}

			PatchStates region = regions.stream()
				.filter(r -> r.getRegionName().equals(patch.getRegion().getName()))
				.filter(r -> r.getPatchName() == null || r.getPatchName().equals(patch.getName()))
				.findFirst()
				.orElse(null);

			if (region != null)
			{
				region.getIsHarvestable().setShouldPass(isHarvestable);
				region.getIsEmpty().setShouldPass(isPlantable);
				region.getIsUnchecked().setShouldPass(isUnchecked);
				region.getIsStump().setShouldPass(isStump);
				region.getIsProtected().setShouldPass(isProtected);
				if (!region.canAccess(client))
				{
					numberOfSaplings--;
				}
			}
		}
		allSaplings.setQuantity(numberOfSaplings);
		coins.setQuantity(coins.getQuantity() + (200 * numberOfSaplings));
		allPayment.setQuantity(allPayment.getQuantity() * numberOfSaplings);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			return;
		}
		questHelperPlugin.getClientThread().invokeLater(() ->
		{
			FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, TREE_SAPLING, TreeSapling.class,
				this::updateTreeSapling, TreeSapling.OAK, configManager, questHelperPlugin);
			FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, FRUIT_TREE_SAPLING, FruitTreeSapling.class,
				this::updateFruitTreeSapling, FruitTreeSapling.APPLE, configManager, questHelperPlugin);
			FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.class,
				this::updateHardwoodTreeSapling, HardwoodTreeSapling.TEAK, configManager, questHelperPlugin);

			if (event.getKey().equals(GRACEFUL_OR_FARMING) || event.getKey().equals(PAY_OR_CUT) || event.getKey().equals(PAY_OR_COMPOST))
			{
				questHelperPlugin.refreshBank();
			}
		});
	}
	private final String TREE_SAPLING = "treeSaplings";
	private final String FRUIT_TREE_SAPLING = "fruitTreeSaplings";
	private final String HARDWOOD_TREE_SAPLING = "hardwoodTreeSaplings";

	@Override
	public List<HelperConfig> getConfigs()
	{
		HelperConfig treesConfig = new HelperConfig("Trees", TREE_SAPLING, TreeSapling.values());
		HelperConfig fruitTreesConfig = new HelperConfig("Fruit Trees", FRUIT_TREE_SAPLING, FruitTreeSapling.values());
		HelperConfig hardwoodTreesConfig = new HelperConfig("Hardwood Trees", HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		HelperConfig payOrCutConfig = new HelperConfig("Pay or cut tree removal", PAY_OR_CUT, PayOrCut.values());
		HelperConfig payOrCompostConfig = new HelperConfig("Pay farmer or compost", PAY_OR_COMPOST, PayOrCompost.values());
		return Arrays.asList(treesConfig, fruitTreesConfig, hardwoodTreesConfig, outfitConfig, payOrCutConfig, payOrCompostConfig);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, rake, compost, coins, allTreeSaplings, allFruitSaplings, allHardwoodSaplings, allProtectionItemTree, allProtectionItemFruitTree, allProtectionItemHardwood);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gracefulOutfit, farmersOutfit, farmingGuildTeleport, lumbridgeTeleport, faladorTeleport, varrockTeleport, catherbyTeleport, crystalTeleport, fossilIslandTeleport);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Farming Guild", Arrays.asList(farmingGuildTreePatchCheckHealth, farmingGuildTreePatchClear, farmingGuildFruitTreePatchCheckHealth, farmingGuildFruitTreePatchClear)));
		allSteps.add(new PanelDetails("Lumbridge", Arrays.asList(lumbridgeTreePatchCheckHealth, lumbridgeTreePatchClear)));
		allSteps.add(new PanelDetails("Falador", Arrays.asList(faladorTreePatchCheckHealth, faladorTreePatchClear)));
		allSteps.add(new PanelDetails("Taverley", Arrays.asList(taverleyTreePatchCheckHealth, taverleyTreePatchClear)));
		allSteps.add(new PanelDetails("Varrock", Arrays.asList(varrockTreePatchCheckHealth, varrockTreePatchClear)));
		allSteps.add(new PanelDetails("Gnome Stronghold", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchClear, gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchClear)));
		allSteps.add(new PanelDetails("Tree Gnome Village", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchClear)));
		allSteps.add(new PanelDetails("Catherby", Arrays.asList(catherbyFruitTreePatchCheckHealth, catherbyFruitTreePatchClear)));
		allSteps.add(new PanelDetails("Brimhaven", Arrays.asList(brimhavenFruitTreePatchCheckHealth, brimhavenFruitTreePatchClear)));
		allSteps.add(new PanelDetails("Llyeta", Arrays.asList(lletyaFruitTreePatchCheckHealth, lletyaFruitTreePatchClear)));
		allSteps.add(new PanelDetails("Fossil Island", Arrays.asList(eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchClear,
			middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchClear,
			westHardwoodTreePatchCheckHealth, westHardwoodTreePatchClear)));
		allSteps.add(new PanelDetails("Avium Savannah", Arrays.asList(savannahCheckHealth, savannahClear)));
		return allSteps;
	}

	private void updateTreeSapling(TreeSapling selectedTreeSapling)
	{
		treeSapling.setId(selectedTreeSapling.treeSaplingID);
		treeSapling.setName(itemManager.getItemComposition(selectedTreeSapling.getPlantableItemId()).getName());

		allTreeSaplings.setId(selectedTreeSapling.treeSaplingID);
		allTreeSaplings.setName(itemManager.getItemComposition(selectedTreeSapling.getPlantableItemId()).getName());
		updateTreePaymentItem(selectedTreeSapling);
	}

	private void updateFruitTreeSapling(FruitTreeSapling selectedFruitTreeSapling)
	{
		fruitTreeSapling.setId(selectedFruitTreeSapling.fruitTreeSaplingId);
		fruitTreeSapling.setName(itemManager.getItemComposition(selectedFruitTreeSapling.getPlantableItemId()).getName());

		allFruitSaplings.setId(selectedFruitTreeSapling.fruitTreeSaplingId);
		allFruitSaplings.setName(itemManager.getItemComposition(selectedFruitTreeSapling.getPlantableItemId()).getName());
		updateFruitTreePaymentItem(selectedFruitTreeSapling);
	}

	private void updateHardwoodTreeSapling(HardwoodTreeSapling selectedHardwoodTreeSapling)
	{
		hardwoodSapling.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		hardwoodSapling.setName(itemManager.getItemComposition(selectedHardwoodTreeSapling.getPlantableItemId()).getName());

		allHardwoodSaplings.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		allHardwoodSaplings.setName(itemManager.getItemComposition(selectedHardwoodTreeSapling.getPlantableItemId()).getName());
		updateHardwoodTreePaymentItem(selectedHardwoodTreeSapling);
	}

	private void updateTreePaymentItem(TreeSapling treeSapling)
	{
		protectionItemTree.setId(treeSapling.protectionItemId);
		protectionItemTree.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		protectionItemTree.setQuantity(treeSapling.protectionItemQuantity);

		allProtectionItemTree.setId(treeSapling.protectionItemId);
		allProtectionItemTree.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		allProtectionItemTree.setQuantity(treeSapling.protectionItemQuantity);
	}

	private void updateFruitTreePaymentItem(FruitTreeSapling treeSapling)
	{
		protectionItemFruitTree.setId(treeSapling.protectionItemId);
		protectionItemFruitTree.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		protectionItemFruitTree.setQuantity(treeSapling.protectionItemQuantity);

		allProtectionItemFruitTree.setId(treeSapling.protectionItemId);
		allProtectionItemFruitTree.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		allProtectionItemFruitTree.setQuantity(treeSapling.protectionItemQuantity);
	}

	private void updateHardwoodTreePaymentItem(HardwoodTreeSapling treeSapling)
	{
		protectionItemHardwood.setId(treeSapling.protectionItemId);
		protectionItemHardwood.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		protectionItemHardwood.setQuantity(treeSapling.protectionItemQuantity);

		allProtectionItemHardwood.setId(treeSapling.protectionItemId);
		allProtectionItemHardwood.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		allProtectionItemHardwood.setQuantity(treeSapling.protectionItemQuantity);
	}
}
