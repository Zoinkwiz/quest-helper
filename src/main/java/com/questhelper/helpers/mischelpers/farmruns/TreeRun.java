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
import com.questhelper.panel.TopLevelPanelDetails;
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
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.*;
import com.questhelper.steps.widget.LunarSpells;
import com.questhelper.steps.widget.NormalSpells;
import java.util.Set;

import com.questhelper.steps.widget.WidgetHighlight;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.Tab;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.questhelper.requirements.util.LogicHelper.*;

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
		gnomeStrongholdTreePatchCheckHealth, nemusRetreatTreePatchCheckHealth;
	DetailedQuestStep farmingGuildTreePatchPlant, lumbridgeTreePatchPlant, faladorTreePatchPlant, taverleyTreePatchPlant,
		varrockTreePatchPlant, gnomeStrongholdTreePatchPlant, nemusRetreatTreePatchPlant;

	DetailedQuestStep lumbridgeTreePatchClear, faladorTreePatchClear, taverleyTreePatchClear, varrockTreePatchClear,
		gnomeStrongholdTreePatchClear, farmingGuildTreePatchClear, nemusRetreatTreePatchClear;
	DetailedQuestStep lumbridgeTreePatchDig, faladorTreePatchDig, taverleyTreePatchDig, varrockTreePatchDig,
		gnomeStrongholdTreePatchDig, farmingGuildTreePatchDig, nemusRetreatTreePatchDig;

	DetailedQuestStep farmingGuildTreePayForProtection, lumbridgeTreeProtect, faladorTreeProtect, taverleyTreeProtect,
		varrockTreeProtect, strongholdTreeProtect, nemusRetreatTreeProtect;

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
	ItemRequirement farmingGuildTeleport, crystalTeleport, catherbyTeleport, varrockTeleport, lumbridgeTeleport, faladorTeleport, fossilIslandTeleport, nemusRetreatTeleport;

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland, accessToSavannah, accessToVarlamore;

	Requirement payingForRemoval, payingForProtection, usingCompostorNothing;

	PatchStates faladorStates, lumbridgeStates, farmingGuildTreeStates, taverleyStates, varrockStates, gnomeStrongholdTreeStates, nemusRetreatStates;

	PatchStates gnomeStrongholdFruitStates, gnomeVillageStates, brimhavenStates, catherbyStates, lletyaStates, farmingGuildFruitStates;

	PatchStates eastHardwoodStates, middleHardwoodStates, westHardwoodStates, savannahStates;

	Requirement allGrowing;

	ConditionalStep farmingGuildStep, lumbridgeStep, varrockStep, faladorStep, taverleyStep, strongholdStep, villageStep, lletyaStep,
		catherbyStep, brimhavenStep, fossilIslandStep, savannahStep, nemusRetreatStep;

	private final String PAY_OR_CUT = "payOrCutTree";
	private final String PAY_OR_COMPOST = "payOrCompostTree";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();
		farmingHandler = new FarmingHandler(client, configManager);

		ReorderableConditionalStep steps = new ReorderableConditionalStep(this, waitForTree, spade, coins, rake, compost
			, farmersOutfit, gracefulOutfit);

		// Farming Guild Tree -> Farming Guild Fruit Tree -> Lumbridge -> Falador -> Taverley
		// Varrock -> Gnome Stronghold Fruit -> Gnome Stronghold Tree -> Gnome Village -> catherby
		// -> Brimhaven -> lletya -> east hardwood -> middle hardwood -> west hardwood.

		farmingGuildStep = new ConditionalStep(this, farmingGuildTreePatchCheckHealth);
		farmingGuildStep.addStep(farmingGuildTreeStates.getIsUnchecked(), farmingGuildTreePatchCheckHealth);
		farmingGuildStep.addStep(farmingGuildTreeStates.getIsHarvestable(), farmingGuildTreePatchClear);
		farmingGuildStep.addStep(farmingGuildTreeStates.getIsStump(), farmingGuildTreePatchDig);
		farmingGuildStep.addStep(farmingGuildTreeStates.getIsEmpty(), farmingGuildTreePatchPlant);
		farmingGuildStep.addStep(nor(farmingGuildTreeStates.getIsProtected(), usingCompostorNothing), farmingGuildTreePayForProtection);

		farmingGuildStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsUnchecked()), farmingGuildFruitTreePatchCheckHealth);
		farmingGuildStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsHarvestable()), farmingGuildFruitTreePatchClear);
		farmingGuildStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsStump()), farmingGuildFruitTreePatchDig);
		farmingGuildStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsEmpty()), farmingGuildFruitTreePatchPlant);
		farmingGuildStep.addStep(and(accessToFarmingGuildFruitTreePatch, nor(farmingGuildFruitStates.getIsProtected(), usingCompostorNothing)), guildFruitProtect);

		steps.addStep(and(accessToFarmingGuildTreePatch, nand(farmingGuildTreeStates.getIsGrowing(),
			farmingGuildFruitStates.getIsGrowing())), farmingGuildStep.withId(0));

		lumbridgeStep = new ConditionalStep(this, lumbridgeTreePatchCheckHealth);
		lumbridgeStep.addStep(lumbridgeStates.getIsUnchecked(), lumbridgeTreePatchCheckHealth);
		lumbridgeStep.addStep(lumbridgeStates.getIsEmpty(), lumbridgeTreePatchPlant);
		lumbridgeStep.addStep(lumbridgeStates.getIsHarvestable(), lumbridgeTreePatchClear);
		lumbridgeStep.addStep(lumbridgeStates.getIsStump(), lumbridgeTreePatchDig);
		lumbridgeStep.addStep(nor(usingCompostorNothing, lumbridgeStates.getIsProtected()), lumbridgeTreeProtect);

		steps.addStep(nor(lumbridgeStates.getIsGrowing()), lumbridgeStep.withId(1));

		faladorStep = new ConditionalStep(this, faladorTreePatchCheckHealth);
		faladorStep.addStep(faladorStates.getIsUnchecked(), faladorTreePatchCheckHealth);
		faladorStep.addStep(faladorStates.getIsEmpty(), faladorTreePatchPlant);
		faladorStep.addStep(faladorStates.getIsHarvestable(), faladorTreePatchClear);
		faladorStep.addStep(faladorStates.getIsStump(), faladorTreePatchDig);
		faladorStep.addStep(nor(usingCompostorNothing, faladorStates.getIsProtected()), faladorTreeProtect);

		steps.addStep(nor(faladorStates.getIsGrowing()), faladorStep.withId(2));

		taverleyStep = new ConditionalStep(this, taverleyTreePatchCheckHealth);
		taverleyStep.addStep(taverleyStates.getIsUnchecked(), taverleyTreePatchCheckHealth);
		taverleyStep.addStep(taverleyStates.getIsEmpty(), taverleyTreePatchPlant);
		taverleyStep.addStep(taverleyStates.getIsHarvestable(), taverleyTreePatchClear);
		taverleyStep.addStep(taverleyStates.getIsStump(), taverleyTreePatchDig);
		taverleyStep.addStep(nor(usingCompostorNothing, taverleyStates.getIsProtected()), taverleyTreeProtect);

		steps.addStep(nor(taverleyStates.getIsGrowing()), taverleyStep.withId(3));

		varrockStep = new ConditionalStep(this, varrockTreePatchCheckHealth);
		varrockStep.addStep(varrockStates.getIsUnchecked(), varrockTreePatchCheckHealth);
		varrockStep.addStep(varrockStates.getIsEmpty(), varrockTreePatchPlant);
		varrockStep.addStep(varrockStates.getIsHarvestable(), varrockTreePatchClear);
		varrockStep.addStep(varrockStates.getIsStump(), varrockTreePatchDig);
		varrockStep.addStep(nor(usingCompostorNothing, varrockStates.getIsProtected()), varrockTreeProtect);

		steps.addStep(nor(varrockStates.getIsGrowing()), varrockStep.withId(4));

		strongholdStep = new ConditionalStep(this, gnomeStrongholdFruitTreePatchCheckHealth);
		strongholdStep.addStep(gnomeStrongholdFruitStates.getIsUnchecked(), gnomeStrongholdFruitTreePatchCheckHealth);
		strongholdStep.addStep(gnomeStrongholdFruitStates.getIsEmpty(), gnomeStrongholdFruitTreePatchPlant);
		strongholdStep.addStep(gnomeStrongholdFruitStates.getIsHarvestable(), gnomeStrongholdFruitTreePatchClear);
		strongholdStep.addStep(gnomeStrongholdFruitStates.getIsStump(), gnomeStrongholdFruitTreePatchDig);
		strongholdStep.addStep(nor(usingCompostorNothing, gnomeStrongholdFruitStates.getIsProtected()), strongholdFruitProtect);

		strongholdStep.addStep(gnomeStrongholdTreeStates.getIsUnchecked(), gnomeStrongholdTreePatchCheckHealth);
		strongholdStep.addStep(gnomeStrongholdTreeStates.getIsEmpty(), gnomeStrongholdTreePatchPlant);
		strongholdStep.addStep(gnomeStrongholdTreeStates.getIsHarvestable(), gnomeStrongholdTreePatchClear);
		strongholdStep.addStep(gnomeStrongholdTreeStates.getIsStump(), gnomeStrongholdTreePatchDig);
		strongholdStep.addStep(nor(usingCompostorNothing, gnomeStrongholdTreeStates.getIsProtected()), strongholdTreeProtect);

		steps.addStep(nand(gnomeStrongholdTreeStates.getIsGrowing(),
			gnomeStrongholdFruitStates.getIsGrowing()), strongholdStep.withId(5));

		villageStep = new ConditionalStep(this, gnomeVillageFruitTreePatchCheckHealth);
		villageStep.addStep(gnomeVillageStates.getIsUnchecked(), gnomeVillageFruitTreePatchCheckHealth);
		villageStep.addStep(gnomeVillageStates.getIsEmpty(), gnomeVillageFruitTreePatchPlant);
		villageStep.addStep(gnomeVillageStates.getIsHarvestable(), gnomeVillageFruitTreePatchClear);
		villageStep.addStep(gnomeVillageStates.getIsStump(), gnomeVillageFruitTreePatchDig);
		villageStep.addStep(nor(usingCompostorNothing, gnomeVillageStates.getIsProtected()), villageFruitProtect);

		steps.addStep(nor(gnomeVillageStates.getIsGrowing()), villageStep.withId(6));

		catherbyStep = new ConditionalStep(this, catherbyFruitTreePatchCheckHealth);
		catherbyStep.addStep(catherbyStates.getIsUnchecked(), catherbyFruitTreePatchCheckHealth);
		catherbyStep.addStep(catherbyStates.getIsEmpty(), catherbyFruitTreePatchPlant);
		catherbyStep.addStep(catherbyStates.getIsHarvestable(), catherbyFruitTreePatchClear);
		catherbyStep.addStep(catherbyStates.getIsStump(), catherbyFruitTreePatchDig);
		catherbyStep.addStep(nor(usingCompostorNothing, catherbyStates.getIsProtected()), catherbyFruitProtect);

		steps.addStep(nor(catherbyStates.getIsGrowing()), catherbyStep.withId(7));

		brimhavenStep = new ConditionalStep(this, brimhavenFruitTreePatchCheckHealth);
		brimhavenStep.addStep(brimhavenStates.getIsUnchecked(), brimhavenFruitTreePatchCheckHealth);
		brimhavenStep.addStep(brimhavenStates.getIsEmpty(), brimhavenFruitTreePatchPlant);
		brimhavenStep.addStep(brimhavenStates.getIsHarvestable(), brimhavenFruitTreePatchClear);
		brimhavenStep.addStep(brimhavenStates.getIsStump(), brimhavenFruitTreePatchDig);
		brimhavenStep.addStep(nor(usingCompostorNothing, brimhavenStates.getIsProtected()), brimhavenFruitProtect);

		steps.addStep(nor(brimhavenStates.getIsGrowing()), brimhavenStep.withId(8));

		lletyaStep = new ConditionalStep(this, lletyaFruitTreePatchCheckHealth);
		lletyaStep.addStep(lletyaStates.getIsUnchecked(), lletyaFruitTreePatchCheckHealth);
		lletyaStep.addStep(lletyaStates.getIsEmpty(), lletyaFruitTreePatchPlant);
		lletyaStep.addStep(lletyaStates.getIsHarvestable(), lletyaFruitTreePatchClear);
		lletyaStep.addStep(lletyaStates.getIsStump(), lletyaFruitTreePatchDig);
		lletyaStep.addStep(nor(usingCompostorNothing, lletyaStates.getIsProtected()), lletyaFruitProtect);

		steps.addStep(and(accessToLletya, nor(lletyaStates.getIsGrowing())), lletyaStep.withId(9));

		fossilIslandStep = new ConditionalStep(this, eastHardwoodTreePatchCheckHealth);
		fossilIslandStep.addStep(eastHardwoodStates.getIsUnchecked(), eastHardwoodTreePatchCheckHealth);
		fossilIslandStep.addStep(eastHardwoodStates.getIsEmpty(), eastHardwoodTreePatchPlant);
		fossilIslandStep.addStep(eastHardwoodStates.getIsHarvestable(), eastHardwoodTreePatchClear);
		fossilIslandStep.addStep(eastHardwoodStates.getIsStump(), eastHardwoodTreePatchDig);
		fossilIslandStep.addStep(nor(usingCompostorNothing,  eastHardwoodStates.getIsProtected()), eastHardwoodProtect);

		fossilIslandStep.addStep(middleHardwoodStates.getIsUnchecked(), middleHardwoodTreePatchCheckHealth);
		fossilIslandStep.addStep(middleHardwoodStates.getIsEmpty(), middleHardwoodTreePatchPlant);
		fossilIslandStep.addStep(middleHardwoodStates.getIsHarvestable(), middleHardwoodTreePatchClear);
		fossilIslandStep.addStep(middleHardwoodStates.getIsStump(), middleHardwoodTreePatchDig);
		fossilIslandStep.addStep(nor(usingCompostorNothing, middleHardwoodStates.getIsProtected()), middleHardwoodProtect);

		fossilIslandStep.addStep(westHardwoodStates.getIsUnchecked(), westHardwoodTreePatchCheckHealth);
		fossilIslandStep.addStep(westHardwoodStates.getIsEmpty(), westHardwoodTreePatchPlant);
		fossilIslandStep.addStep(westHardwoodStates.getIsHarvestable(), westHardwoodTreePatchClear);
		fossilIslandStep.addStep(westHardwoodStates.getIsStump(), westHardwoodTreePatchDig);
		fossilIslandStep.addStep(nor(usingCompostorNothing, westHardwoodStates.getIsProtected()), westHardwoodProtect);

		steps.addStep(and(accessToFossilIsland,
			nand(eastHardwoodStates.getIsGrowing(), westHardwoodStates.getIsGrowing(), middleHardwoodStates.getIsGrowing())),
			fossilIslandStep.withId(10));

		savannahStep = new ConditionalStep(this, savannahCheckHealth);
		savannahStep.addStep(savannahStates.getIsUnchecked(), savannahCheckHealth);
		savannahStep.addStep(savannahStates.getIsEmpty(), savannahPlant);
		savannahStep.addStep(savannahStates.getIsHarvestable(), savannahClear);
		savannahStep.addStep(savannahStates.getIsStump(), savannahDig);
		savannahStep.addStep(nor(usingCompostorNothing, savannahStates.getIsProtected()), savannahProtect);

		steps.addStep(and(accessToSavannah, nor(savannahStates.getIsGrowing())), savannahStep.withId(11));

		nemusRetreatStep = new ConditionalStep(this, nemusRetreatTreePatchCheckHealth);
		nemusRetreatStep.addStep(nemusRetreatStates.getIsUnchecked(), nemusRetreatTreePatchCheckHealth);
		nemusRetreatStep.addStep(nemusRetreatStates.getIsEmpty(), nemusRetreatTreePatchPlant);
		nemusRetreatStep.addStep(nemusRetreatStates.getIsHarvestable(), nemusRetreatTreePatchClear);
		nemusRetreatStep.addStep(nemusRetreatStates.getIsStump(), nemusRetreatTreePatchDig);
		nemusRetreatStep.addStep(nor(usingCompostorNothing, nemusRetreatStates.getIsProtected()), nemusRetreatTreeProtect);

		steps.addStep(and(accessToVarlamore, nor(nemusRetreatStates.getIsGrowing())), nemusRetreatStep.withId(12));

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
		accessToVarlamore = new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);

		// Trees
		lumbridgeStates = new PatchStates("Lumbridge");
		faladorStates = new PatchStates("Falador");
		taverleyStates = new PatchStates("Taverley");
		varrockStates = new PatchStates("Varrock");
		gnomeStrongholdTreeStates = new PatchStates("Gnome Stronghold");
		farmingGuildTreeStates = new PatchStates("Farming Guild", accessToFarmingGuildTreePatch);
		nemusRetreatStates = new PatchStates("Nemus Retreat", accessToVarlamore);

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

		allGrowing = and(lumbridgeStates.getIsGrowing(), faladorStates.getIsGrowing(), taverleyStates.getIsGrowing(), varrockStates.getIsGrowing(),
			gnomeStrongholdTreeStates.getIsGrowing(), catherbyStates.getIsGrowing(), brimhavenStates.getIsGrowing(), gnomeVillageStates.getIsGrowing(),
			gnomeStrongholdFruitStates.getIsGrowing(),
			or(not(accessToLletya), lletyaStates.getIsGrowing()),
			or(not(accessToVarlamore), nemusRetreatStates.getIsGrowing()),
			or(not(accessToFarmingGuildTreePatch), farmingGuildTreeStates.getIsGrowing()),
			or(not(accessToFarmingGuildFruitTreePatch), farmingGuildFruitStates.getIsGrowing()),
			or(not(accessToFossilIsland), and(westHardwoodStates.getIsGrowing(), middleHardwoodStates.getIsGrowing(), eastHardwoodStates.getIsGrowing())),
			or(not(accessToSavannah), savannahStates.getIsGrowing())
		);

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
		spade = new ItemRequirement("Spade", net.runelite.api.gameval.ItemID.SPADE);
		rake = new ItemRequirement("Rake", net.runelite.api.gameval.ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(VarbitID.FARMING_BLOCKWEEDS, 2));
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS)
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
		catherbyTeleport = new ItemRequirement("Catherby teleport", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
		catherbyTeleport.addAlternates(ItemID.POH_TABLET_CAMELOTTELEPORT);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemCollections.RING_OF_WEALTHS);
		faladorTeleport.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT);
		fossilIslandTeleport = new ItemRequirement("Teleport to Fossil Island", ItemCollections.DIGSITE_PENDANTS);
		nemusRetreatTeleport = new ItemRequirement("Nemus Retreat Teleport", ItemID.PENDANT_OF_ATES);
		nemusRetreatTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

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
		gracefulBoots.addAlternates(ItemID.IKOV_BOOTSOFLIGHTNESS);

		gracefulOutfit = new ItemRequirements(
			"Graceful outfit (equipped)",
			gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape
		).isNotConsumed().showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.GRACEFUL.name()));


		farmingHat = new ItemRequirement(
			"Farmer's strawhat", net.runelite.api.gameval.ItemID.TITHE_REWARD_HAT_MALE, 1 ,true).isNotConsumed();
		farmingHat.addAlternates(ItemID.TITHE_REWARD_HAT_FEMALE);

		farmingTop = new ItemRequirement(
			"Farmer's top", ItemID.TITHE_REWARD_TORSO_MALE, 1, true).isNotConsumed();
		farmingTop.addAlternates(ItemID.TITHE_REWARD_TORSO_FEMALE);

		farmingLegs = new ItemRequirement(
			"Farmer's boro trousers", ItemID.TITHE_REWARD_LEGS_MALE, 1, true).isNotConsumed();
		farmingLegs.addAlternates(net.runelite.api.gameval.ItemID.TITHE_REWARD_LEGS_FEMALE);

		farmingBoots = new ItemRequirement(
			"Graceful cape", ItemID.TITHE_REWARD_FEET_MALE, 1, true).isNotConsumed();
		farmingBoots.addAlternates(net.runelite.api.gameval.ItemID.TITHE_REWARD_FEET_FEMALE);

		farmersOutfit = new ItemRequirements(
			"Farmer's outfit (equipped)",
			farmingHat, farmingTop, farmingLegs, farmingBoots).isNotConsumed()
			.showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.FARMING.name()));
	}

	private void setupSteps()
	{
		waitForTree = new DetailedQuestStep(this, "Wait for your trees to grow! This may take a while..!");

		// Tree Patch Clear Steps

		lumbridgeTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_4, new WorldPoint(3193, 3231, 0),
			"Speak to Fayeth to clear the patch.");
		lumbridgeTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		faladorTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_2, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to clear the patch.");
		faladorTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		taverleyTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to clear the patch.");
		taverleyTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		varrockTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_3_02, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to clear the patch.");
		varrockTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		gnomeStrongholdTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_GNOME, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to clear the patch.");
		gnomeStrongholdTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		farmingGuildTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T2, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to clear the patch.");
		farmingGuildTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		nemusRetreatTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to clear the patch.");
		nemusRetreatTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		lumbridgeTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_4, new WorldPoint(3193, 3231, 0),
			"Speak to Fayeth to protect the patch.");
		lumbridgeTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		faladorTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_2, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to protect the patch.");
		faladorTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		taverleyTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to protect the patch.");
		taverleyTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		varrockTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_3_02, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to protect the patch.");
		varrockTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		strongholdTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_GNOME, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to protect the patch.");
		strongholdTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		farmingGuildTreePayForProtection = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T2, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to protect the patch.");
		farmingGuildTreePayForProtection.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		nemusRetreatTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to protect the patch.");
		nemusRetreatTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		// Tree Patch Steps
		lumbridgeTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Check the health of the tree planted in Lumbridge.");
		lumbridgeTreePatchCheckHealth.addTeleport(lumbridgeTeleport);
		lumbridgeTreePatchCheckHealth.addSpellHighlight(NormalSpells.LUMBRIDGE_TELEPORT);
		lumbridgeTreePatchCheckHealth.addSpellHighlight(NormalSpells.LUMBRIDGE_HOME_TELEPORT);
		faladorTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Check the health of the tree planted in Falador.");
		faladorTreePatchCheckHealth.addTeleport(faladorTeleport);
		faladorTreePatchCheckHealth.addSpellHighlight(NormalSpells.FALADOR_TELEPORT);
		taverleyTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Check the health of the tree planted in Taverley.");
		varrockTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Check the health of the tree planted in Varrock.");
		varrockTreePatchCheckHealth.addTeleport(varrockTeleport);
		varrockTreePatchCheckHealth.addSpellHighlight(NormalSpells.VARROCK_TELEPORT);
		gnomeStrongholdTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Check the health of the tree planted in the Tree Gnome Stronghold.");
		farmingGuildTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Check the health of the tree planted in the Farming Guild.");
		farmingGuildTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));
		farmingGuildTreePatchCheckHealth.addTeleport(farmingGuildTeleport);

		nemusRetreatTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Check the health of the tree planted at the Nemus Retreat");
		nemusRetreatTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		nemusRetreatTreePatchCheckHealth.addTeleport(nemusRetreatTeleport);

		// Tree Plant Steps
		lumbridgeTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Plant your sapling in the Lumbridge patch.", treeSapling);
		lumbridgeTreePatchPlant.addIcon(treeSapling.getId());
		lumbridgeTreePatchCheckHealth.addSubSteps(lumbridgeTreePatchPlant);

		faladorTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Plant your sapling in the Falador patch.", treeSapling);
		faladorTreePatchPlant.addIcon(treeSapling.getId());
		faladorTreePatchCheckHealth.addSubSteps(faladorTreePatchPlant);

		taverleyTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Plant your sapling in the Taverley patch.", treeSapling);
		taverleyTreePatchPlant.addIcon(treeSapling.getId());
		taverleyTreePatchCheckHealth.addSubSteps(taverleyTreePatchPlant);

		varrockTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Plant your sapling in the Varrock patch.", treeSapling);
		varrockTreePatchPlant.addIcon(treeSapling.getId());
		varrockTreePatchCheckHealth.addSubSteps(varrockTreePatchPlant);

		gnomeStrongholdTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Plant your sapling in the Gnome Stronghold patch.", treeSapling);
		gnomeStrongholdTreePatchPlant.addIcon(treeSapling.getId());
		gnomeStrongholdTreePatchCheckHealth.addSubSteps(gnomeStrongholdTreePatchPlant);

		farmingGuildTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Plant your sapling in the Farming Guild tree patch.", treeSapling);
		farmingGuildTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));
		farmingGuildTreePatchPlant.addIcon(treeSapling.getId());
		farmingGuildTreePatchCheckHealth.addSubSteps(farmingGuildTreePatchPlant);

		nemusRetreatTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Plant your sapling in the Nemus Retreat tree patch.", treeSapling);
		nemusRetreatTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		nemusRetreatTreePatchPlant.addIcon(treeSapling.getId());
		nemusRetreatTreePatchCheckHealth.addSubSteps(nemusRetreatTreePatchPlant);

		// Dig
		lumbridgeTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Dig up the tree stump in Lumbridge.");
		faladorTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Dig up the tree stump in Falador.");
		taverleyTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Dig up the tree stump in Taverley.");
		varrockTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Dig up the tree stump in Varrock.");
		gnomeStrongholdTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Dig up the tree stump in the Tree Gnome Stronghold.");
		farmingGuildTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Dig up the tree stump in the Farming Guild tree patch.");
		nemusRetreatTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Dig up the tree stump in the Nemus Retreat tree patch.");

		faladorTreePatchClear.addSubSteps(faladorTreePatchDig, faladorTreeProtect);
		taverleyTreePatchClear.addSubSteps(taverleyTreePatchDig, taverleyTreeProtect);
		varrockTreePatchClear.addSubSteps(varrockTreePatchDig, varrockTreeProtect);
		gnomeStrongholdTreePatchClear.addSubSteps(gnomeStrongholdTreePatchDig, strongholdTreeProtect);
		lumbridgeTreePatchClear.addSubSteps(lumbridgeTreePatchDig, lumbridgeTreeProtect);
		farmingGuildTreePatchClear.addSubSteps(farmingGuildTreePatchDig, farmingGuildTreePayForProtection);
		nemusRetreatTreePatchClear.addSubSteps(nemusRetreatTreePatchDig, nemusRetreatTreeProtect);

		// Fruit Tree Steps

		// Fruit Tree Plant Steps
		gnomeStrongholdFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Plant your sapling in the Tree Gnome Stronghold patch.", fruitTreeSapling);
		gnomeStrongholdFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		gnomeVillageFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Plant your sapling in the Tree Gnome Village patch. Follow Elkoy to get out quickly.", fruitTreeSapling);
		gnomeVillageFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		brimhavenFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Plant your sapling in the Brimhaven patch.", fruitTreeSapling);
		brimhavenFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		catherbyFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Plant your sapling in the Catherby patch.", fruitTreeSapling);
		catherbyFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		lletyaFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Plant your sapling in the Lletya patch.", fruitTreeSapling);
		lletyaFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		farmingGuildFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Plant your sapling in the Farming Guild patch.", fruitTreeSapling);
		farmingGuildFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		// Fruit Tree Check Health Steps
		gnomeStrongholdFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Check the health of the fruit tree planted in the Tree Gnome Stronghold.");
		gnomeStrongholdFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Gnome Stronghold", true);
		gnomeStrongholdFruitTreePatchCheckHealth.addSubSteps(gnomeStrongholdFruitTreePatchPlant);

		gnomeVillageFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Check the health of the fruit tree planted outside the Tree Gnome Village. Follow Elkoy to get out quickly.");
		gnomeVillageFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Tree Gnome Village", true);
		gnomeVillageFruitTreePatchCheckHealth.addSubSteps(gnomeVillageFruitTreePatchPlant);

		brimhavenFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Check the health of the fruit tree planted in Brimhaven.");
		brimhavenFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchCheckHealth.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));
		brimhavenFruitTreePatchCheckHealth.addSubSteps(brimhavenFruitTreePatchPlant);

		catherbyFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby.");
		catherbyFruitTreePatchCheckHealth.addTeleport(catherbyTeleport);
		catherbyFruitTreePatchCheckHealth.addSpellHighlight(NormalSpells.CAMELOT_TELEPORT);
		catherbyFruitTreePatchCheckHealth.addSpellHighlight(LunarSpells.CATHERBY_TELEPORT);
		catherbyFruitTreePatchCheckHealth.addSubSteps(catherbyFruitTreePatchPlant);

		lletyaFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchCheckHealth.addTeleport(crystalTeleport);
		lletyaFruitTreePatchCheckHealth.addSubSteps(lletyaFruitTreePatchPlant);

		farmingGuildFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchCheckHealth.addTeleport(farmingGuildTeleport);
		farmingGuildFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Farming Guild", true);
		farmingGuildFruitTreePatchCheckHealth.addSubSteps(farmingGuildFruitTreePatchPlant);

		// Clear
		gnomeStrongholdFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_1, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeStrongholdFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		gnomeVillageFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_2, new WorldPoint(2490, 3180, 0),
			"Pay Gileth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeVillageFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		brimhavenFruitTreePatchClear = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		brimhavenFruitTreePatchClear.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchClear.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchClear.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));
		brimhavenFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		catherbyFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_4, new WorldPoint(2860, 3433, 0),
			"Pay Ellena 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		catherbyFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		lletyaFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_TREE_5, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		lletyaFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		farmingGuildFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T3, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		farmingGuildFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		strongholdFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_1, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo to protect the patch.");
		strongholdFruitProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		villageFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_2, new WorldPoint(2490, 3180, 0),
			"Pay Gileth to protect the patch.");
		villageFruitProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		brimhavenFruitProtect = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth to protect the patch.");
		brimhavenFruitProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		catherbyFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_4, new WorldPoint(2860, 3433, 0),
			"Pay Ellena to protect the patch.");
		catherbyFruitProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		lletyaFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_TREE_5, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen to protect the patch.");
		lletyaFruitProtect.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - " +
				"chop my tree down please.", "Yes.");
		guildFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T3, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie to protect the patch.");
		guildFruitProtect.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - " +
				"chop my tree down please.", "Yes.");

		// Dig Fruit Tree Steps
		gnomeStrongholdFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Dig up the fruit tree's stump in the Tree Gnome Stronghold.");
		gnomeVillageFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Dig up the fruit tree's stump outside the Tree Gnome Village.");
		brimhavenFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Dig up the fruit tree's stump in Brimhaven.");
		catherbyFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby");
		lletyaFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Dig up the fruit tree's stump in Lletya.");
		farmingGuildFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Dig up the fruit tree's stump in the Farming Guild.");

		gnomeStrongholdFruitTreePatchClear.addSubSteps(gnomeStrongholdFruitTreePatchDig, strongholdFruitProtect);
		gnomeVillageFruitTreePatchClear.addSubSteps(gnomeVillageFruitTreePatchDig, villageFruitProtect);
		brimhavenFruitTreePatchClear.addSubSteps(brimhavenFruitTreePatchDig, brimhavenFruitProtect);
		catherbyFruitTreePatchClear.addSubSteps(catherbyFruitTreePatchDig, catherbyFruitProtect);
		lletyaFruitTreePatchClear.addSubSteps(lletyaFruitTreePatchDig, lletyaFruitProtect);
		farmingGuildFruitTreePatchClear.addSubSteps(farmingGuildFruitTreePatchDig, guildFruitProtect);

		// Hardwood Tree Steps
		westHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Check the health of the western hardwood tree on Fossil Island.");
		middleHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Check the health of the centre hardwood tree on Fossil Island.");
		eastHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Check the health of the eastern hardwood tree on Fossil Island.");
		savannahCheckHealth  = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Check the health of the hardwood tree in the Avium Savannah.");

		// Hardwood Tree Plant Steps
		westHardwoodTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Plant your sapling on the western hardwood tree patch on Fossil Island.", hardwoodSapling);
		westHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		westHardwoodTreePatchCheckHealth.addSubSteps(westHardwoodTreePatchPlant);

		middleHardwoodTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Plant your sapling on the centre hardwood tree patch on Fossil Island.", hardwoodSapling);
		middleHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		middleHardwoodTreePatchCheckHealth.addSubSteps(middleHardwoodTreePatchPlant);

		eastHardwoodTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Plant your sapling on the eastern hardwood tree patch on Fossil Island.", hardwoodSapling);
		eastHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		eastHardwoodTreePatchCheckHealth.addSubSteps(eastHardwoodTreePatchPlant);

		savannahPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Plant your sapling into the hardwood tree in the hardwood tree patch in the Avium Savannah.", hardwoodSapling);

		westHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to remove the west tree.");
		westHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");
		middleHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to remove the middle tree.");
		middleHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");
		eastHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to remove the east tree.");
		eastHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");

		savannahClear = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to clear the tree.");
		savannahClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
			"Coins - chop my tree down please.", "Yes.");

		westHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Dig up the western hardwood tree's stump on Fossil Island.");
		middleHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Dig up the centre hardwood tree's stump on Fossil Island.");
		eastHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Dig up the eastern hardwood tree's stump on Fossil Island.");
		savannahDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Dig up the Savannah hardwood tree's stump.");

		westHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to protect the west tree.");
		middleHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to protect the middle tree.");
		eastHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to protect the east tree.");
		savannahProtect = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
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
			List.of(farmingGuildTreeStates, varrockStates, faladorStates, taverleyStates, lumbridgeStates, gnomeStrongholdTreeStates, nemusRetreatStates),
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
			boolean isGrowing = state == CropState.GROWING;

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
				region.getIsGrowing().setShouldPass(isGrowing);
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
		// IDEA: Can add ID to each step. onLoad and onConfigChanged it checks id ordering.
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Wait for Herbs", waitForTree).withHideCondition(nor(allGrowing)));

		PanelDetails farmingGuildPanel = new PanelDetails("Farming Guild", Arrays.asList(farmingGuildTreePatchCheckHealth, farmingGuildTreePatchClear,
				farmingGuildFruitTreePatchCheckHealth, farmingGuildFruitTreePatchClear)).withId(0);
		farmingGuildPanel.setLockingStep(farmingGuildStep);

		PanelDetails lumbridgePanel = new PanelDetails("Lumbridge", Arrays.asList(lumbridgeTreePatchCheckHealth, lumbridgeTreePatchClear)).withId(1);
		lumbridgePanel.setLockingStep(lumbridgeStep);

		PanelDetails faladorPanel = new PanelDetails("Falador", Arrays.asList(faladorTreePatchCheckHealth, faladorTreePatchClear)).withId(2);
		faladorPanel.setLockingStep(faladorStep);

		PanelDetails taverleyPanel = new PanelDetails("Taverley", Arrays.asList(taverleyTreePatchCheckHealth, taverleyTreePatchClear)).withId(3);
		taverleyPanel.setLockingStep(taverleyStep);

		PanelDetails varrockPanel = new PanelDetails("Varrock", Arrays.asList(varrockTreePatchCheckHealth, varrockTreePatchClear)).withId(4);
		varrockPanel.setLockingStep(varrockStep);

		PanelDetails gnomeStrongholdPanel = new PanelDetails("Gnome Stronghold", Arrays.asList(gnomeStrongholdFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchClear,
			gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchClear)).withId(5);
		gnomeStrongholdPanel.setLockingStep(strongholdStep);

		PanelDetails villagePanel = new PanelDetails("Tree Gnome Village", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth,
				gnomeVillageFruitTreePatchClear)).withId(6);
		villagePanel.setLockingStep(villageStep);

		PanelDetails catherbyPanel = new PanelDetails("Catherby", Arrays.asList(catherbyFruitTreePatchCheckHealth, catherbyFruitTreePatchClear)).withId(7);
		catherbyPanel.setLockingStep(catherbyStep);

		PanelDetails brimhavenPanel = new PanelDetails("Brimhaven", Arrays.asList(brimhavenFruitTreePatchCheckHealth, brimhavenFruitTreePatchClear)).withId(8);
		brimhavenPanel.setLockingStep(brimhavenStep);

		PanelDetails lletyaPanel = new PanelDetails("Lletya", Arrays.asList(lletyaFruitTreePatchCheckHealth, lletyaFruitTreePatchClear)).withId(9);
		lletyaPanel.setLockingStep(lletyaStep);

		PanelDetails fossilIslandPanel = new PanelDetails("Fossil Island", Arrays.asList(eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchClear,
			middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchClear,
			westHardwoodTreePatchCheckHealth, westHardwoodTreePatchClear)).withId(10);
		fossilIslandPanel.setLockingStep(fossilIslandStep);

		PanelDetails savannahPanel = new PanelDetails("Avium Savannah", Arrays.asList(savannahCheckHealth, savannahClear, savannahPlant)).withId(11);
		savannahPanel.setLockingStep(savannahStep);

		PanelDetails nemusRetreatPanel = new PanelDetails("Nemus Retreat", Arrays.asList(nemusRetreatTreePatchCheckHealth, nemusRetreatTreePatchClear, nemusRetreatTreePatchPlant)).withId(12);
		nemusRetreatPanel.setLockingStep(nemusRetreatStep);

		TopLevelPanelDetails farmRunSidebar = new TopLevelPanelDetails("Tree Run", farmingGuildPanel, lumbridgePanel, faladorPanel, taverleyPanel,
			varrockPanel, gnomeStrongholdPanel, villagePanel, catherbyPanel, brimhavenPanel, lletyaPanel, fossilIslandPanel, savannahPanel, nemusRetreatPanel);
		allSteps.add(farmRunSidebar);

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
