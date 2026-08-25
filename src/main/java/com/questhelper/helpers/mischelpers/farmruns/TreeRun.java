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
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.CalquatTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.PayOrCut;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.PayOrCompost;
import com.questhelper.helpers.mischelpers.farmruns.treeruns.FruitTreeFactory;
import com.questhelper.helpers.mischelpers.farmruns.treeruns.TreeRunConfig;
import com.questhelper.helpers.mischelpers.farmruns.treeruns.TreeRunItems;
import com.questhelper.helpers.mischelpers.farmruns.treeruns.TreeRunTeleports;
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
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.ReorderableConditionalStep;
import com.questhelper.steps.widget.NormalSpells;
import java.util.Set;

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
	private static final String TREE_PROTECTION_DIALOG = "Would you look after my crops for me?";

	@Inject
	private FarmingWorld farmingWorld;

	@Inject
	private PaymentTracker paymentTracker;

	@Inject
	ItemManager itemManager;

	private FarmingHandler farmingHandler;

	DetailedQuestStep waitForTree;

	FruitTreeFactory fruitTreeFactory;

	// Trees
	DetailedQuestStep farmingGuildTreePatchCheckHealth, lumbridgeTreePatchCheckHealth, faladorTreePatchCheckHealth,
		taverleyTreePatchCheckHealth, varrockTreePatchCheckHealth, gnomeStrongholdTreePatchCheckHealth,
		auburnvaleTreePatchCheckHealth;
	DetailedQuestStep farmingGuildTreePatchCutDown, lumbridgeTreePatchCutDown, faladorTreePatchCutDown,
		taverleyTreePatchCutDown, varrockTreePatchCutDown, gnomeStrongholdTreePatchCutDown,
		auburnvaleTreePatchCutDown;
	DetailedQuestStep farmingGuildTreePatchPlant, lumbridgeTreePatchPlant, faladorTreePatchPlant,
		taverleyTreePatchPlant, varrockTreePatchPlant, gnomeStrongholdTreePatchPlant,
		auburnvaleTreePatchPlant;

	DetailedQuestStep lumbridgeTreePatchClear, faladorTreePatchClear, taverleyTreePatchClear, varrockTreePatchClear,
		gnomeStrongholdTreePatchClear, farmingGuildTreePatchClear, auburnvaleTreePatchClear;
	DetailedQuestStep lumbridgeTreePatchDig, faladorTreePatchDig, taverleyTreePatchDig, varrockTreePatchDig,
		gnomeStrongholdTreePatchDig, farmingGuildTreePatchDig, auburnvaleTreePatchDig;

	DetailedQuestStep farmingGuildTreePayForProtection, lumbridgeTreeProtect,
		faladorTreeProtect, taverleyTreeProtect, varrockTreeProtect, strongholdTreeProtect,
		auburnvaleTreeProtect;

	// Calquat Trees
	DetailedQuestStep
		taiBwoWannaiCalquatPatchCheckHealth, kastoriCalquatPatchCheckHealth, greatConchCalquatPatchCheckHealth;
	DetailedQuestStep
		taiBwoWannaiCalquatPatchPlant, kastoriCalquatPatchPlant, greatConchCalquatPatchPlant;
	DetailedQuestStep
		taiBwoWannaiCalquatPatchClear, kastoriCalquatPatchClear, greatConchCalquatPatchClear;
	DetailedQuestStep
		taiBwoWannaiCalquatPatchDig, kastoriCalquatPatchDig, greatConchCalquatPatchDig;
	DetailedQuestStep
		taiBwoWannaiCalquatPatchRemove, kastoriCalquatPatchRemove, greatConchCalquatPatchRemove;
	DetailedQuestStep
		taiBwoWannaiCalquatProtect, kastoriCalquatProtect, greatConchCalquatProtect;

	// Hardwood Trees
	DetailedQuestStep eastHardwoodTreePatchCheckHealth, westHardwoodTreePatchCheckHealth,
		middleHardwoodTreePatchCheckHealth, savannahCheckHealth, anglersCheckHealth;
	DetailedQuestStep eastHardwoodTreePatchPlant, westHardwoodTreePatchPlant, middleHardwoodTreePatchPlant,
		savannahPlant, anglersPlant;
	DetailedQuestStep eastHardwoodTreePatchDig, westHardwoodTreePatchDig, middleHardwoodTreePatchDig, savannahDig,
		anglersDig;

	DetailedQuestStep eastHardwoodTreePatchClear, westHardwoodTreePatchClear, middleHardwoodTreePatchClear,
		savannahClear, anglersClear;
	DetailedQuestStep eastHardwoodTreePatchCutDown, westHardwoodTreePatchCutDown, middleHardwoodTreePatchCutDown,
		savannahCutDown, anglersCutDown;

	DetailedQuestStep eastHardwoodProtect, westHardwoodProtect, middleHardwoodProtect, savannahProtect,
		anglersProtect;

	// Farming Items
	ItemRequirement coins, spade, rake, allTreeSaplings, treeSapling,
		allHardwoodSaplings, hardwoodSapling, calquatSapling, allCalquatSaplings,
		compost, axe,	protectionItemTree, allProtectionItemTree, protectionItemCalquat, allProtectionItemCalquat,
		protectionItemHardwood, allProtectionItemHardwood;

	// Teleport Items
	// TODO: Add these...
	ItemRequirement farmingGuildTeleport, crystalTeleport, catherbyTeleport, varrockTeleport, lumbridgeTeleport,
		faladorTeleport, fossilIslandTeleport, auburnvaleTeleport, kastoriTeleport;

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape,
		gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Toggles
	Requirement treesEnabled, hardwoodEnabled, calquatEnabled;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToCalquatFarming, accessToFossilIsland,
		accessToSavannah, accessToVarlamore, accessToAnglersRetreat, accessToGreatConch;

	Requirement payingForRemoval, payingForProtection, usingCompostorNothing;

	PatchStates faladorStates, lumbridgeStates, farmingGuildTreeStates, taverleyStates, varrockStates,
		gnomeStrongholdTreeStates, auburnvaleStates;

	PatchStates taiBwoWannaiStates, kastoriCalquatStates, greatConchStates;

	PatchStates eastHardwoodStates, middleHardwoodStates, westHardwoodStates, savannahStates, anglersRetreatStates;

	Requirement allGrowing;

	ReorderableConditionalStep farmingGuildStep, strongholdStep, karamjaStep, fossilIslandStep, kastoriStep;
	ConditionalStep farmingGuildTreeStep, lumbridgeStep, varrockStep, faladorStep, taverleyStep,
		strongholdTreeStep, taiBwoWannaiStep, fossilIslandEastStep, fossilIslandMiddleStep, fossilIslandWestStep,
		savannahStep, auburnvaleStep, kastoriCalquatStep, anglersRetreatStep, greatConchStep;

	private final String PAY_OR_CUT = "payOrCutTree";
	private final String PAY_OR_COMPOST = "payOrCompostTree";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	public QuestStep loadStep()
	{
		TreeRunConfig treeRunConfig = new TreeRunConfig(configManager);
		TreeRunTeleports teleports = new TreeRunTeleports();
		TreeRunItems items = new TreeRunItems(treeRunConfig);

		fruitTreeFactory = FruitTreeFactory.getInstance(this, teleports, treeRunConfig, items, itemManager);

		initializeRequirements();
		setupSteps();
		farmingHandler = new FarmingHandler(client, configManager);

		ReorderableConditionalStep steps = new ReorderableConditionalStep(this, waitForTree, spade, coins,
			rake, compost, farmersOutfit, gracefulOutfit);

		// Farming Guild Tree -> Farming Guild Fruit Tree -> Lumbridge -> Falador -> Taverley
		// Varrock -> Gnome Stronghold Fruit -> Gnome Stronghold Tree -> Gnome Village -> catherby
		// -> Brimhaven -> lletya -> east hardwood -> middle hardwood -> west hardwood.
		farmingGuildTreeStep = (ConditionalStep) new ConditionalStep(this, farmingGuildTreePatchCheckHealth).withId(-1);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsUnchecked(), farmingGuildTreePatchCheckHealth);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsStump(), farmingGuildTreePatchDig);
		farmingGuildTreeStep.addStep(and(farmingGuildTreeStates.getIsHarvestable(), not(payingForRemoval)), farmingGuildTreePatchCutDown);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsHarvestable(), farmingGuildTreePatchClear);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsEmpty(), farmingGuildTreePatchPlant);
		farmingGuildTreeStep.addStep(nor(farmingGuildTreeStates.getIsProtected(), usingCompostorNothing), farmingGuildTreePayForProtection);

		// TODO: Ideally we should allow for null steps to be rejected and propagate up conditionalstep chains
		farmingGuildStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		farmingGuildStep.addStep(not(farmingGuildTreeStates.getIsGrowing()), farmingGuildTreeStep);
		farmingGuildStep.addStep(fruitTreeFactory.isFarmingGuildActionable(), fruitTreeFactory.getFarmingGuildStep());
		steps.addStep(or(and(accessToFarmingGuildTreePatch, not(farmingGuildTreeStates.getIsGrowing())), fruitTreeFactory.isFarmingGuildActionable()), farmingGuildStep.withId(0));

		lumbridgeStep = new ConditionalStep(this, lumbridgeTreePatchCheckHealth);
		lumbridgeStep.addStep(lumbridgeStates.getIsUnchecked(), lumbridgeTreePatchCheckHealth);
		lumbridgeStep.addStep(and(lumbridgeStates.getIsHarvestable(), not(payingForRemoval)), lumbridgeTreePatchCutDown);
		lumbridgeStep.addStep(lumbridgeStates.getIsEmpty(), lumbridgeTreePatchPlant);
		lumbridgeStep.addStep(lumbridgeStates.getIsHarvestable(), lumbridgeTreePatchClear);
		lumbridgeStep.addStep(lumbridgeStates.getIsStump(), lumbridgeTreePatchDig);
		lumbridgeStep.addStep(nor(usingCompostorNothing, lumbridgeStates.getIsProtected()), lumbridgeTreeProtect);
		steps.addStep(not(lumbridgeStates.getIsGrowing()), lumbridgeStep.withId(1));

		faladorStep = new ConditionalStep(this, faladorTreePatchCheckHealth);
		faladorStep.addStep(faladorStates.getIsUnchecked(), faladorTreePatchCheckHealth);
		faladorStep.addStep(and(faladorStates.getIsHarvestable(), not(payingForRemoval)), faladorTreePatchCutDown);
		faladorStep.addStep(faladorStates.getIsEmpty(), faladorTreePatchPlant);
		faladorStep.addStep(faladorStates.getIsHarvestable(), faladorTreePatchClear);
		faladorStep.addStep(faladorStates.getIsStump(), faladorTreePatchDig);
		faladorStep.addStep(nor(usingCompostorNothing, faladorStates.getIsProtected()), faladorTreeProtect);
		steps.addStep(not(faladorStates.getIsGrowing()), faladorStep.withId(2));

		taverleyStep = new ConditionalStep(this, taverleyTreePatchCheckHealth);
		taverleyStep.addStep(taverleyStates.getIsUnchecked(), taverleyTreePatchCheckHealth);
		taverleyStep.addStep(and(taverleyStates.getIsHarvestable(), not(payingForRemoval)), taverleyTreePatchCutDown);
		taverleyStep.addStep(taverleyStates.getIsEmpty(), taverleyTreePatchPlant);
		taverleyStep.addStep(taverleyStates.getIsHarvestable(), taverleyTreePatchClear);
		taverleyStep.addStep(taverleyStates.getIsStump(), taverleyTreePatchDig);
		taverleyStep.addStep(nor(usingCompostorNothing, taverleyStates.getIsProtected()), taverleyTreeProtect);
		steps.addStep(not(taverleyStates.getIsGrowing()), taverleyStep.withId(3));

		varrockStep = new ConditionalStep(this, varrockTreePatchCheckHealth);
		varrockStep.addStep(varrockStates.getIsUnchecked(), varrockTreePatchCheckHealth);
		varrockStep.addStep(and(varrockStates.getIsHarvestable(), not(payingForRemoval)), varrockTreePatchCutDown);
		varrockStep.addStep(varrockStates.getIsEmpty(), varrockTreePatchPlant);
		varrockStep.addStep(varrockStates.getIsHarvestable(), varrockTreePatchClear);
		varrockStep.addStep(varrockStates.getIsStump(), varrockTreePatchDig);
		varrockStep.addStep(nor(usingCompostorNothing, varrockStates.getIsProtected()), varrockTreeProtect);
		steps.addStep(not(varrockStates.getIsGrowing()), varrockStep.withId(4));

		strongholdStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		strongholdStep.addStep(fruitTreeFactory.isGnomeStrongholdActionable(), fruitTreeFactory.getGnomeStrongholdStep());

		strongholdTreeStep = (ConditionalStep) new ConditionalStep(this, gnomeStrongholdTreePatchCheckHealth).withId(52);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsUnchecked(), gnomeStrongholdTreePatchCheckHealth);
		strongholdTreeStep.addStep(and(gnomeStrongholdTreeStates.getIsHarvestable(), not(payingForRemoval)), gnomeStrongholdTreePatchCutDown);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsEmpty(), gnomeStrongholdTreePatchPlant);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsHarvestable(), gnomeStrongholdTreePatchClear);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsStump(), gnomeStrongholdTreePatchDig);
		strongholdTreeStep.addStep(nor(usingCompostorNothing, gnomeStrongholdTreeStates.getIsProtected()), strongholdTreeProtect);
		strongholdStep.addStep(not(gnomeStrongholdTreeStates.getIsGrowing()), strongholdTreeStep);

		steps.addStep(or(fruitTreeFactory.isGnomeStrongholdActionable(), not(gnomeStrongholdTreeStates.getIsGrowing())), strongholdStep.withId(5));
		steps.addStep(fruitTreeFactory.isGnomeVillageActionable(), fruitTreeFactory.getGnomeVillageStep());
		steps.addStep(fruitTreeFactory.isCatherbyActionable(), fruitTreeFactory.getCatherbyStep());

		karamjaStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		karamjaStep.addStep(fruitTreeFactory.isBrimhavenActionable(), fruitTreeFactory.getBrimhavenStep());

		taiBwoWannaiStep = (ConditionalStep) new ConditionalStep(this, taiBwoWannaiCalquatPatchCheckHealth, accessToCalquatFarming).withId(82);
		taiBwoWannaiStep.addStep(and(accessToCalquatFarming, taiBwoWannaiStates.getIsUnchecked()), taiBwoWannaiCalquatPatchCheckHealth);
		taiBwoWannaiStep.addStep(and(accessToCalquatFarming, taiBwoWannaiStates.getIsHarvestable(), not(payingForRemoval)), taiBwoWannaiCalquatPatchRemove);
		taiBwoWannaiStep.addStep(and(accessToCalquatFarming, taiBwoWannaiStates.getIsEmpty()), taiBwoWannaiCalquatPatchPlant);
		taiBwoWannaiStep.addStep(and(accessToCalquatFarming, taiBwoWannaiStates.getIsHarvestable()), taiBwoWannaiCalquatPatchClear);
		taiBwoWannaiStep.addStep(and(accessToCalquatFarming, taiBwoWannaiStates.getIsStump()), taiBwoWannaiCalquatPatchDig);
		taiBwoWannaiStep.addStep(and(accessToCalquatFarming, nor(usingCompostorNothing, taiBwoWannaiStates.getIsProtected())),
			taiBwoWannaiCalquatProtect);
		karamjaStep.addStep(and(accessToCalquatFarming, not(taiBwoWannaiStates.getIsGrowing())), taiBwoWannaiStep);
		steps.addStep(or(fruitTreeFactory.isBrimhavenActionable(), not(taiBwoWannaiStates.getIsGrowing())), karamjaStep.withId(8));

		steps.addStep(fruitTreeFactory.isLletyaActionable(), fruitTreeFactory.getLletyaStep());

		fossilIslandEastStep = (ConditionalStep) new ConditionalStep(this, eastHardwoodTreePatchCheckHealth).withId(101);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsUnchecked(), eastHardwoodTreePatchCheckHealth);
		fossilIslandEastStep.addStep(and(eastHardwoodStates.getIsHarvestable(), not(payingForRemoval)), eastHardwoodTreePatchCutDown);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsEmpty(), eastHardwoodTreePatchPlant);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsHarvestable(), eastHardwoodTreePatchClear);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsStump(), eastHardwoodTreePatchDig);
		fossilIslandEastStep.addStep(nor(usingCompostorNothing,  eastHardwoodStates.getIsProtected()), eastHardwoodProtect);
		fossilIslandStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		fossilIslandStep.addStep(not(eastHardwoodStates.getIsGrowing()), fossilIslandEastStep);
		fossilIslandMiddleStep = (ConditionalStep) new ConditionalStep(this, middleHardwoodTreePatchCheckHealth).withId(102);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsUnchecked(), middleHardwoodTreePatchCheckHealth);
		fossilIslandMiddleStep.addStep(and(middleHardwoodStates.getIsHarvestable(), not(payingForRemoval)), middleHardwoodTreePatchCutDown);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsEmpty(), middleHardwoodTreePatchPlant);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsHarvestable(), middleHardwoodTreePatchClear);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsStump(), middleHardwoodTreePatchDig);
		fossilIslandMiddleStep.addStep(nor(usingCompostorNothing, middleHardwoodStates.getIsProtected()), middleHardwoodProtect);
		fossilIslandStep.addStep(not(middleHardwoodStates.getIsGrowing()), fossilIslandMiddleStep);

		fossilIslandWestStep = (ConditionalStep) new ConditionalStep(this, westHardwoodTreePatchCheckHealth).withId(103);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsUnchecked(), westHardwoodTreePatchCheckHealth);
		fossilIslandWestStep.addStep(and(westHardwoodStates.getIsHarvestable(), not(payingForRemoval)), westHardwoodTreePatchCutDown);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsEmpty(), westHardwoodTreePatchPlant);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsHarvestable(), westHardwoodTreePatchClear);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsStump(), westHardwoodTreePatchDig);
		fossilIslandWestStep.addStep(nor(usingCompostorNothing, westHardwoodStates.getIsProtected()), westHardwoodProtect);
		fossilIslandStep.addStep(not(westHardwoodStates.getIsGrowing()), fossilIslandWestStep);
		steps.addStep(and(accessToFossilIsland, or(not(eastHardwoodStates.getIsGrowing()), not(middleHardwoodStates.getIsGrowing()), not(westHardwoodStates.getIsGrowing()))), fossilIslandStep.withId(10));

		savannahStep = new ConditionalStep(this, savannahCheckHealth);
		savannahStep.addStep(savannahStates.getIsUnchecked(), savannahCheckHealth);
		savannahStep.addStep(and(savannahStates.getIsHarvestable(), not(payingForRemoval)), savannahCutDown);
		savannahStep.addStep(savannahStates.getIsEmpty(), savannahPlant);
		savannahStep.addStep(savannahStates.getIsHarvestable(), savannahClear);
		savannahStep.addStep(savannahStates.getIsStump(), savannahDig);
		savannahStep.addStep(nor(usingCompostorNothing, savannahStates.getIsProtected()), savannahProtect);
		steps.addStep(and(accessToSavannah, not(savannahStates.getIsGrowing())), savannahStep.withId(11));

		auburnvaleStep = new ConditionalStep(this, auburnvaleTreePatchCheckHealth);
		auburnvaleStep.addStep(auburnvaleStates.getIsUnchecked(), auburnvaleTreePatchCheckHealth);
		auburnvaleStep.addStep(and(auburnvaleStates.getIsHarvestable(), not(payingForRemoval)), auburnvaleTreePatchCutDown);
		auburnvaleStep.addStep(auburnvaleStates.getIsEmpty(), auburnvaleTreePatchPlant);
		auburnvaleStep.addStep(auburnvaleStates.getIsHarvestable(), auburnvaleTreePatchClear);
		auburnvaleStep.addStep(auburnvaleStates.getIsStump(), auburnvaleTreePatchDig);
		auburnvaleStep.addStep(nor(usingCompostorNothing, auburnvaleStates.getIsProtected()), auburnvaleTreeProtect);
		steps.addStep(and(accessToVarlamore, not(auburnvaleStates.getIsGrowing())), auburnvaleStep.withId(12));

		kastoriCalquatStep = (ConditionalStep) new ConditionalStep(this, kastoriCalquatPatchCheckHealth, accessToCalquatFarming).withId(132);
		kastoriCalquatStep.addStep(and(accessToCalquatFarming, kastoriCalquatStates.getIsUnchecked()), kastoriCalquatPatchCheckHealth);
		kastoriCalquatStep.addStep(and(accessToCalquatFarming, kastoriCalquatStates.getIsHarvestable(), not(payingForRemoval)), kastoriCalquatPatchRemove);
		kastoriCalquatStep.addStep(and(accessToCalquatFarming, kastoriCalquatStates.getIsEmpty()), kastoriCalquatPatchPlant);
		kastoriCalquatStep.addStep(and(accessToCalquatFarming, kastoriCalquatStates.getIsHarvestable()), kastoriCalquatPatchClear);
		kastoriCalquatStep.addStep(and(accessToCalquatFarming, kastoriCalquatStates.getIsStump()), kastoriCalquatPatchDig);
		kastoriCalquatStep.addStep(and(accessToCalquatFarming, nor(usingCompostorNothing, kastoriCalquatStates.getIsProtected())), kastoriCalquatProtect);

		kastoriStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		kastoriStep.addStep(fruitTreeFactory.isKastoriActionable(), fruitTreeFactory.getKastoriStep());
		kastoriStep.addStep(and(accessToCalquatFarming, not(kastoriCalquatStates.getIsGrowing())), kastoriCalquatStep);
		steps.addStep(or(fruitTreeFactory.isKastoriActionable(), and(accessToVarlamore, not(kastoriCalquatStates.getIsGrowing()))), kastoriStep.withId(13));

		anglersRetreatStep = new ConditionalStep(this, anglersCheckHealth);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsUnchecked(), anglersCheckHealth);
		anglersRetreatStep.addStep(and(anglersRetreatStates.getIsHarvestable(), not(payingForRemoval)), anglersCutDown);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsEmpty(), anglersPlant);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsHarvestable(), anglersClear);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsStump(), anglersDig);
		anglersRetreatStep.addStep(nor(usingCompostorNothing, anglersRetreatStates.getIsProtected()), anglersProtect);
		steps.addStep(and(accessToAnglersRetreat, not(anglersRetreatStates.getIsGrowing())), anglersRetreatStep.withId(14));

		greatConchStep = new ConditionalStep(this, greatConchCalquatPatchCheckHealth, accessToCalquatFarming);
		greatConchStep.addStep(and(accessToCalquatFarming, greatConchStates.getIsUnchecked()), greatConchCalquatPatchCheckHealth);
		greatConchStep.addStep(and(accessToCalquatFarming, greatConchStates.getIsHarvestable(), not(payingForRemoval)), greatConchCalquatPatchRemove);
		greatConchStep.addStep(and(accessToCalquatFarming, greatConchStates.getIsEmpty()), greatConchCalquatPatchPlant);
		greatConchStep.addStep(and(accessToCalquatFarming, greatConchStates.getIsHarvestable()), greatConchCalquatPatchClear);
		greatConchStep.addStep(and(accessToCalquatFarming, greatConchStates.getIsStump()), greatConchCalquatPatchDig);
		greatConchStep.addStep(and(accessToCalquatFarming, nor(usingCompostorNothing, greatConchStates.getIsProtected())), greatConchCalquatProtect);
		steps.addStep(and(accessToCalquatFarming, accessToGreatConch, not(greatConchStates.getIsGrowing())), greatConchStep.withId(15));

		return steps;
	}

	private void setupConditions()
	{
		// Tree Patch Ready Requirements

		// Toggle Requirements
		treesEnabled = not(new Conditions(new RuneliteRequirement(configManager, TREE_SAPLING, TreeSapling.NONE.name())));
		hardwoodEnabled = not(new Conditions(new RuneliteRequirement(configManager, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.NONE.name())));
		calquatEnabled = not(new Conditions(new RuneliteRequirement(configManager, CALQUAT_TREE_SAPLING, CalquatTreeSapling.NONE.name())));

		// Access Requirements
		// ME1 partial completion required only, however much easier to access when finished.
		accessToFossilIsland = new QuestRequirement(QuestHelperQuest.BONE_VOYAGE, QuestState.FINISHED);
		accessToFarmingGuildTreePatch = new Conditions(
			new SkillRequirement(Skill.FARMING, 65)
		);
		accessToCalquatFarming = new Conditions(
			new SkillRequirement(Skill.FARMING, 72, false)
		);
		accessToSavannah = new QuestRequirement(QuestHelperQuest.THE_RIBBITING_TALE_OF_A_LILY_PAD_LABOUR_DISPUTE, QuestState.FINISHED);
		accessToVarlamore = new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);

		accessToAnglersRetreat = new Conditions(
			new SkillRequirement(Skill.SAILING, 51)
		);

		accessToGreatConch = new QuestRequirement(QuestHelperQuest.TROUBLED_TORTUGANS, QuestState.FINISHED);

		// Trees
		lumbridgeStates = new PatchStates("Lumbridge", treesEnabled);
		faladorStates = new PatchStates("Falador", treesEnabled);
		taverleyStates = new PatchStates("Taverley", treesEnabled);
		varrockStates = new PatchStates("Varrock", treesEnabled);
		gnomeStrongholdTreeStates = new PatchStates("Gnome Stronghold", treesEnabled);
		farmingGuildTreeStates = new PatchStates("Farming Guild", and(accessToFarmingGuildTreePatch, treesEnabled));
		auburnvaleStates = new PatchStates("Auburnvale", and(accessToVarlamore, treesEnabled));

		// Calquat trees
		taiBwoWannaiStates = new PatchStates("Tai Bwo Wannai", and(accessToCalquatFarming));
		kastoriCalquatStates = new PatchStates("Kastori", and(accessToVarlamore, accessToCalquatFarming));
		greatConchStates = new PatchStates("Great Conch", and(accessToGreatConch, accessToCalquatFarming));

		westHardwoodStates = new PatchStates("Fossil Island", "West");
		middleHardwoodStates = new PatchStates("Fossil Island", "Middle");
		eastHardwoodStates = new PatchStates("Fossil Island", "East");
		savannahStates = new PatchStates("Avium Savannah", and(accessToSavannah, hardwoodEnabled));
		anglersRetreatStates = new PatchStates("Anglers' Retreat", and(accessToAnglersRetreat, hardwoodEnabled));

		allGrowing = and(
			// Tree patches
			or(not(treesEnabled),
				and(lumbridgeStates.getIsGrowing(),
					faladorStates.getIsGrowing(),
					taverleyStates.getIsGrowing(),
					varrockStates.getIsGrowing(),
					gnomeStrongholdTreeStates.getIsGrowing(),
					or(not(accessToFarmingGuildTreePatch), farmingGuildTreeStates.getIsGrowing()),
					or(not(accessToVarlamore), auburnvaleStates.getIsGrowing()))),

			// Fruit tree patches
			fruitTreeFactory.isAllGrowing(),

			// Hard wood patches
			or(not(hardwoodEnabled),
				and(or(not(accessToFossilIsland),
					and(westHardwoodStates.getIsGrowing(),
						middleHardwoodStates.getIsGrowing(),
						eastHardwoodStates.getIsGrowing())),
					or(not(accessToSavannah), savannahStates.getIsGrowing()),
					or(not(accessToAnglersRetreat), anglersRetreatStates.getIsGrowing()))),

			// Calquat patches
			or(not(calquatEnabled),
				not(accessToCalquatFarming),
				and(taiBwoWannaiStates.getIsGrowing(),
					or(not(accessToVarlamore), kastoriCalquatStates.getIsGrowing()),
					or(not(accessToGreatConch), greatConchStates.getIsGrowing())))
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
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(VarbitID.FARMING_BLOCKWEEDS, 2));
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS)
			.showConditioned(payingForRemoval);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed().showConditioned(not(payingForRemoval));

		treesEnabled = not(new RuneliteRequirement(configManager, TREE_SAPLING, TreeSapling.NONE.name()));
		hardwoodEnabled = not(new RuneliteRequirement(configManager, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.NONE.name()));
		calquatEnabled = not(new RuneliteRequirement(configManager, CALQUAT_TREE_SAPLING, CalquatTreeSapling.NONE.name()));

		TreeSapling treeSaplingEnum = (TreeSapling) FarmingUtils.getEnumFromConfig(configManager, TreeSapling.MAGIC);
		treeSapling = treeSaplingEnum.getPlantableItemRequirement(itemManager).showConditioned(treesEnabled);
		treeSapling.setHighlightInInventory(true);
		allTreeSaplings = treeSapling.copy();

		protectionItemTree = treeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(
			and(payingForProtection, treesEnabled));
		protectionItemTree.addAlternates(protectionItemTree.getId() + 1);
		allProtectionItemTree = protectionItemTree.copy();

		HardwoodTreeSapling hardwoodTreeSaplingEnum = (HardwoodTreeSapling) FarmingUtils.getEnumFromConfig(configManager, HardwoodTreeSapling.TEAK);
		hardwoodSapling = hardwoodTreeSaplingEnum.getPlantableItemRequirement(itemManager).showConditioned(hardwoodEnabled);
		hardwoodSapling.setHighlightInInventory(true);
		allHardwoodSaplings = hardwoodSapling.copy();

		protectionItemHardwood = hardwoodTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(
			and(payingForProtection, hardwoodEnabled));
		protectionItemHardwood.addAlternates(protectionItemHardwood.getId() + 1);
		allProtectionItemHardwood = protectionItemHardwood.copy();

		CalquatTreeSapling calquatTreeSaplingEnum = (CalquatTreeSapling) FarmingUtils.getEnumFromConfig(configManager,
			CalquatTreeSapling.CALQUAT);
		calquatSapling = calquatTreeSaplingEnum.getPlantableItemRequirement(itemManager).showConditioned(calquatEnabled);
		calquatSapling.setHighlightInInventory(true);
		allCalquatSaplings = calquatSapling.copy();

		protectionItemCalquat = calquatTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(
			and(payingForProtection, calquatEnabled));
		protectionItemCalquat.addAlternates(protectionItemCalquat.getId() + 1);
		allProtectionItemCalquat = protectionItemCalquat.copy();

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
		auburnvaleTeleport = new ItemRequirement("Auburnvale Teleport", ItemID.PENDANT_OF_ATES);
		auburnvaleTeleport.addAlternates(ItemCollections.FAIRY_STAFF);
		kastoriTeleport = new ItemRequirement("Kastori Teleport", ItemID.PENDANT_OF_ATES);
		kastoriTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

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
			"Farmer's strawhat", ItemID.TITHE_REWARD_HAT_MALE, 1 ,true).isNotConsumed();
		farmingHat.addAlternates(ItemID.TITHE_REWARD_HAT_FEMALE);

		farmingTop = new ItemRequirement(
			"Farmer's top", ItemID.TITHE_REWARD_TORSO_MALE, 1, true).isNotConsumed();
		farmingTop.addAlternates(ItemID.TITHE_REWARD_TORSO_FEMALE);

		farmingLegs = new ItemRequirement(
			"Farmer's boro trousers", ItemID.TITHE_REWARD_LEGS_MALE, 1, true).isNotConsumed();
		farmingLegs.addAlternates(ItemID.TITHE_REWARD_LEGS_FEMALE);

		farmingBoots = new ItemRequirement(
			"Graceful cape", ItemID.TITHE_REWARD_FEET_MALE, 1, true).isNotConsumed();
		farmingBoots.addAlternates(ItemID.TITHE_REWARD_FEET_FEMALE);

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
		lumbridgeTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		lumbridgeTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		faladorTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_2, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to clear the patch.");
		faladorTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		faladorTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		taverleyTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to clear the patch.");
		taverleyTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		taverleyTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		varrockTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_3_02, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to clear the patch.");
		varrockTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		varrockTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		gnomeStrongholdTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_GNOME, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to clear the patch.");
		gnomeStrongholdTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		gnomeStrongholdTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		farmingGuildTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T2, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to clear the patch.");
		farmingGuildTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		farmingGuildTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		auburnvaleTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to clear the patch.");
		auburnvaleTreePatchClear.conditionToHideInSidebar(or(not(treesEnabled), not(payingForRemoval)));
		auburnvaleTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		lumbridgeTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_4, new WorldPoint(3193, 3231, 0),
			"Speak to Fayeth to protect the patch.");
		lumbridgeTreeProtect.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		lumbridgeTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		faladorTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_2, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to protect the patch.");
		faladorTreeProtect.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		faladorTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		taverleyTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to protect the patch.");
		taverleyTreeProtect.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		taverleyTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		varrockTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_3_02, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to protect the patch.");
		varrockTreeProtect.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		varrockTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		strongholdTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_GNOME, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to protect the patch.");
		strongholdTreeProtect.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		strongholdTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		farmingGuildTreePayForProtection = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T2, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to protect the patch.");
		farmingGuildTreePayForProtection.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		farmingGuildTreePayForProtection.addDialogSteps(TREE_PROTECTION_DIALOG);

		auburnvaleTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to protect the patch.");
		auburnvaleTreeProtect.conditionToHideInSidebar(or(not(treesEnabled), not(payingForProtection)));
		auburnvaleTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		// Tree Patch Steps
		lumbridgeTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Check the health of the tree planted in Lumbridge.");
		lumbridgeTreePatchCheckHealth.addTeleport(lumbridgeTeleport);
		lumbridgeTreePatchCheckHealth.addSpellHighlight(NormalSpells.LUMBRIDGE_TELEPORT);
		lumbridgeTreePatchCheckHealth.addSpellHighlight(NormalSpells.LUMBRIDGE_HOME_TELEPORT);
		lumbridgeTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		faladorTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Check the health of the tree planted in Falador.");
		faladorTreePatchCheckHealth.addTeleport(faladorTeleport);
		faladorTreePatchCheckHealth.addSpellHighlight(NormalSpells.FALADOR_TELEPORT);
		faladorTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		taverleyTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Check the health of the tree planted in Taverley.");
		taverleyTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		varrockTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Check the health of the tree planted in Varrock.");
		varrockTreePatchCheckHealth.addTeleport(varrockTeleport);
		varrockTreePatchCheckHealth.addSpellHighlight(NormalSpells.VARROCK_TELEPORT);
		varrockTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		gnomeStrongholdTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Check the health of the tree planted in the Tree Gnome Stronghold.");
		gnomeStrongholdTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		farmingGuildTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Check the health of the tree planted in the Farming Guild.");
		farmingGuildTreePatchCheckHealth.conditionToHideInSidebar(not(accessToFarmingGuildTreePatch));
		farmingGuildTreePatchCheckHealth.addTeleport(farmingGuildTeleport);
		farmingGuildTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		auburnvaleTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Check the health of the tree planted at Auburnvale");
		auburnvaleTreePatchCheckHealth.conditionToHideInSidebar(or(not(accessToVarlamore), not(treesEnabled)));
		auburnvaleTreePatchCheckHealth.addTeleport(auburnvaleTeleport);

		// Tree Cut Down Steps
		farmingGuildTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Cut down the tree planted in the Farming Guild.", axe);
		farmingGuildTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), not(accessToFarmingGuildTreePatch), payingForRemoval));
		farmingGuildTreePatchCutDown.addTeleport(farmingGuildTeleport);

		lumbridgeTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Cut down the tree planted in Lumbridge.", axe);
		lumbridgeTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		lumbridgeTreePatchCutDown.addTeleport(lumbridgeTeleport);
		lumbridgeTreePatchCutDown.addSpellHighlight(NormalSpells.LUMBRIDGE_TELEPORT);
		lumbridgeTreePatchCutDown.addSpellHighlight(NormalSpells.LUMBRIDGE_HOME_TELEPORT);

		faladorTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Cut down the tree planted in Falador.", axe);
		faladorTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		faladorTreePatchCutDown.addTeleport(faladorTeleport);
		faladorTreePatchCutDown.addSpellHighlight(NormalSpells.FALADOR_TELEPORT);

		taverleyTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Cut down the tree planted in Taverley.", axe);
		taverleyTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));

		varrockTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Cut down the tree planted in Varrock.", axe);
		varrockTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		varrockTreePatchCutDown.addTeleport(varrockTeleport);
		varrockTreePatchCutDown.addSpellHighlight(NormalSpells.VARROCK_TELEPORT);

		gnomeStrongholdTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Cut down the tree planted in the Tree Gnome Stronghold.", axe);
		gnomeStrongholdTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));

		auburnvaleTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Cut down the tree planted at Auburnvale.", axe);
		auburnvaleTreePatchCutDown.conditionToHideInSidebar(or(not(treesEnabled), not(accessToVarlamore), payingForRemoval));
		auburnvaleTreePatchCutDown.addTeleport(auburnvaleTeleport);

		// Tree Plant Steps
		lumbridgeTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Plant your sapling in the Lumbridge patch.", treeSapling);
		lumbridgeTreePatchPlant.addIcon(treeSapling.getId());
		lumbridgeTreePatchCheckHealth.addSubSteps(lumbridgeTreePatchPlant);
		lumbridgeTreePatchPlant.conditionToHideInSidebar(not(treesEnabled));

		faladorTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Plant your sapling in the Falador patch.", treeSapling);
		faladorTreePatchPlant.addIcon(treeSapling.getId());
		faladorTreePatchCheckHealth.addSubSteps(faladorTreePatchPlant);
		faladorTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		taverleyTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Plant your sapling in the Taverley patch.", treeSapling);
		taverleyTreePatchPlant.addIcon(treeSapling.getId());
		taverleyTreePatchCheckHealth.addSubSteps(taverleyTreePatchPlant);
		taverleyTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		varrockTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Plant your sapling in the Varrock patch.", treeSapling);
		varrockTreePatchPlant.addIcon(treeSapling.getId());
		varrockTreePatchCheckHealth.addSubSteps(varrockTreePatchPlant);
		varrockTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		gnomeStrongholdTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Plant your sapling in the Gnome Stronghold patch.", treeSapling);
		gnomeStrongholdTreePatchPlant.addIcon(treeSapling.getId());
		gnomeStrongholdTreePatchCheckHealth.addSubSteps(gnomeStrongholdTreePatchPlant);
		gnomeStrongholdTreePatchCheckHealth.conditionToHideInSidebar(not(treesEnabled));

		farmingGuildTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Plant your sapling in the Farming Guild tree patch.", treeSapling);
		farmingGuildTreePatchPlant.conditionToHideInSidebar(or(not(treesEnabled), not(accessToFarmingGuildTreePatch)));
		farmingGuildTreePatchPlant.addIcon(treeSapling.getId());
		farmingGuildTreePatchCheckHealth.addSubSteps(farmingGuildTreePatchPlant);

		auburnvaleTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Plant your sapling in the Auburnvale tree patch.", treeSapling);
		auburnvaleTreePatchPlant.conditionToHideInSidebar(or(not(treesEnabled), not(accessToVarlamore)));
		auburnvaleTreePatchPlant.addIcon(treeSapling.getId());
		auburnvaleTreePatchCheckHealth.addSubSteps(auburnvaleTreePatchPlant);

		// Dig
		lumbridgeTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Dig up the tree stump in Lumbridge.");
		lumbridgeTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		faladorTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Dig up the tree stump in Falador.");
		faladorTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		taverleyTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Dig up the tree stump in Taverley.");
		taverleyTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		varrockTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Dig up the tree stump in Varrock.");
		varrockTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		gnomeStrongholdTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Dig up the tree stump in the Tree Gnome Stronghold.");
		gnomeStrongholdTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval));
		farmingGuildTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Dig up the tree stump in the Farming Guild tree patch.");
		farmingGuildTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval, not(accessToFarmingGuildTreePatch)));
		auburnvaleTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Dig up the tree stump in the Auburnvale tree patch.");
		auburnvaleTreePatchDig.conditionToHideInSidebar(or(not(treesEnabled), payingForRemoval, not(accessToVarlamore)));

		faladorTreePatchClear.addSubSteps(faladorTreePatchDig);
		taverleyTreePatchClear.addSubSteps(taverleyTreePatchDig);
		varrockTreePatchClear.addSubSteps(varrockTreePatchDig);
		gnomeStrongholdTreePatchClear.addSubSteps(gnomeStrongholdTreePatchDig);
		lumbridgeTreePatchClear.addSubSteps(lumbridgeTreePatchDig);
		farmingGuildTreePatchClear.addSubSteps(farmingGuildTreePatchDig);
		auburnvaleTreePatchClear.addSubSteps(auburnvaleTreePatchDig);

		// Calquat Tree Steps

		// Calquat Tree Plant Steps
		taiBwoWannaiCalquatPatchPlant = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Plant your sapling in the Tai Bwo Wannai patch.", calquatSapling);
		taiBwoWannaiCalquatPatchPlant.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToCalquatFarming)));
		taiBwoWannaiCalquatPatchPlant.addIcon(calquatSapling.getId());

		kastoriCalquatPatchPlant = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Plant your sapling in the Kastori Calquat patch.", calquatSapling);
		kastoriCalquatPatchPlant.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToVarlamore), not(accessToCalquatFarming)));
		kastoriCalquatPatchPlant.addIcon(calquatSapling.getId());

		greatConchCalquatPatchPlant = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Plant your sapling in the Great Conch patch.", calquatSapling);
		greatConchCalquatPatchPlant.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToGreatConch), not(accessToCalquatFarming)));
		greatConchCalquatPatchPlant.addIcon(calquatSapling.getId());

		// Calquat Tree Check Health Steps
		taiBwoWannaiCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Check the health of the calquat tree planted in Tai Bwo Wannai.", calquatSapling);
		taiBwoWannaiCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Tai Bwo Wannai", true);
		taiBwoWannaiCalquatPatchCheckHealth.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToCalquatFarming)));
		taiBwoWannaiCalquatPatchCheckHealth.addSubSteps(taiBwoWannaiCalquatPatchPlant);

		kastoriCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Check the health of the calquat tree planted in Kastori.");
		kastoriCalquatPatchCheckHealth.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToVarlamore), not(accessToCalquatFarming)));
		kastoriCalquatPatchCheckHealth.addTeleport(kastoriTeleport);
		kastoriCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);
		kastoriCalquatPatchCheckHealth.addSubSteps(kastoriCalquatPatchPlant);

		greatConchCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Check the health of the calquat tree planted in Great Conch.", calquatSapling);
		greatConchCalquatPatchCheckHealth.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToGreatConch), not(accessToCalquatFarming)));
		greatConchCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Great Conch", true);
		greatConchCalquatPatchCheckHealth.addSubSteps(greatConchCalquatPatchPlant);

		// Calquat Tree Cut Down Steps
		taiBwoWannaiCalquatPatchRemove = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Pick the fruit off the calquat and clear the patch in Tai Bwo Wannai.");
		taiBwoWannaiCalquatPatchRemove.conditionToHideInSidebar(or(not(calquatEnabled), payingForRemoval, not(accessToCalquatFarming)));
		taiBwoWannaiCalquatPatchRemove.addWidgetHighlightWithTextRequirement(187, 3, "Tai Bwo Wannai", true);

		kastoriCalquatPatchRemove = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Pick the fruit off the calquat and clear the patch in Kastori.");
		kastoriCalquatPatchRemove.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToVarlamore), not(accessToCalquatFarming), payingForRemoval));
		kastoriCalquatPatchRemove.addTeleport(kastoriTeleport);
		kastoriCalquatPatchRemove.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);

		greatConchCalquatPatchRemove = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Pick the fruit off the calquat and clear the patch in Great Conch.");
		greatConchCalquatPatchRemove.conditionToHideInSidebar(or(not(calquatEnabled), not(accessToGreatConch), not(accessToCalquatFarming), payingForRemoval));
		greatConchCalquatPatchRemove.addWidgetHighlightWithTextRequirement(187, 3, "Great Conch", true);

		// Clear
		taiBwoWannaiCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT, new WorldPoint(2795, 3102, 0),
			"Pay Imiago 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		taiBwoWannaiCalquatPatchClear.conditionToHideInSidebar(or(not(calquatEnabled), not(payingForRemoval), not(accessToCalquatFarming)));
		taiBwoWannaiCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		kastoriCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_2, new WorldPoint(1366, 3033, 0),
			"Pay Tziuhtla 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		kastoriCalquatPatchClear.conditionToHideInSidebar(or(not(calquatEnabled), not(payingForRemoval), not(accessToCalquatFarming)));
		kastoriCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		greatConchCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_3, new WorldPoint(3129, 2406, 0),
			"Pay Guppa 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		greatConchCalquatPatchClear.conditionToHideInSidebar(or(not(calquatEnabled), not(payingForRemoval), not(accessToCalquatFarming)));
		greatConchCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		taiBwoWannaiCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT, new WorldPoint(2795, 3102, 0),
			"Pay Imiago to protect the patch.");
		taiBwoWannaiCalquatProtect.conditionToHideInSidebar(or(not(calquatEnabled), not(payingForProtection), not(accessToCalquatFarming)));
		taiBwoWannaiCalquatProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		kastoriCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_2, new WorldPoint(1366, 3033, 0),
			"Pay Tziuhtla to protect the patch.");
		kastoriCalquatProtect.conditionToHideInSidebar(or(not(calquatEnabled), not(payingForProtection), not(accessToCalquatFarming)));
		kastoriCalquatProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		greatConchCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_3, new WorldPoint(3129, 2406, 0),
			"Pay Guppa to protect the patch.");
		greatConchCalquatProtect.conditionToHideInSidebar(or(not(calquatEnabled), not(payingForProtection), not(accessToCalquatFarming)));
		greatConchCalquatProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		// Dig Calquat Tree Steps
		taiBwoWannaiCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Dig up the calquat tree's stump in Tai Bwo Wannai.");
		taiBwoWannaiCalquatPatchDig.conditionToHideInSidebar(or(not(calquatEnabled), payingForRemoval, not(accessToCalquatFarming)));
		kastoriCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Dig up the calquat tree's stump in Kastori.");
		kastoriCalquatPatchDig.conditionToHideInSidebar(or(not(calquatEnabled), payingForRemoval, not(accessToVarlamore), not(accessToCalquatFarming)));
		greatConchCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Dig up the calquat tree's stump in Great Conch.");
		greatConchCalquatPatchDig.conditionToHideInSidebar(or(not(calquatEnabled), payingForRemoval, not(accessToGreatConch), not(accessToCalquatFarming)));

		taiBwoWannaiCalquatPatchClear.addSubSteps(taiBwoWannaiCalquatPatchDig);
		kastoriCalquatPatchClear.addSubSteps(kastoriCalquatPatchDig);
		greatConchCalquatPatchClear.addSubSteps(greatConchCalquatPatchDig);

		// Hardwood Tree Steps
		westHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Check the health of the western hardwood tree on Fossil Island.");
		westHardwoodTreePatchCheckHealth.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToFossilIsland)));
		middleHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Check the health of the centre hardwood tree on Fossil Island.");
		middleHardwoodTreePatchCheckHealth.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToFossilIsland)));
		eastHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Check the health of the eastern hardwood tree on Fossil Island.");
		eastHardwoodTreePatchCheckHealth.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToFossilIsland)));
		savannahCheckHealth  = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Check the health of the hardwood tree in the Avium Savannah.");
		savannahCheckHealth.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToSavannah)));
		anglersCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470, 2704, 0),
			"Check the health of the hardwood tree in the Anglers' Retreat.");
		anglersCheckHealth.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToAnglersRetreat)));

		// Hardwood Tree Plant Steps
		westHardwoodTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Plant your sapling on the western hardwood tree patch on Fossil Island.", hardwoodSapling);
		westHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		westHardwoodTreePatchPlant.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToFossilIsland)));
		westHardwoodTreePatchCheckHealth.addSubSteps(westHardwoodTreePatchPlant);

		middleHardwoodTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Plant your sapling on the centre hardwood tree patch on Fossil Island.", hardwoodSapling);
		middleHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		middleHardwoodTreePatchPlant.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToFossilIsland)));
		middleHardwoodTreePatchCheckHealth.addSubSteps(middleHardwoodTreePatchPlant);

		eastHardwoodTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Plant your sapling on the eastern hardwood tree patch on Fossil Island.", hardwoodSapling);
		eastHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		eastHardwoodTreePatchPlant.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToFossilIsland)));
		eastHardwoodTreePatchCheckHealth.addSubSteps(eastHardwoodTreePatchPlant);

		savannahPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Plant your sapling on the hardwood tree patch in the Avium Savannah.", hardwoodSapling);
		savannahPlant.addIcon(hardwoodSapling.getId());
		savannahPlant.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToSavannah)));
		savannahCheckHealth.addSubSteps(savannahPlant);

		anglersPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470, 2704, 0),
			"Plant your sapling on the hardwood tree patch on Anglers' Retreat.", hardwoodSapling);
		anglersPlant.addIcon(hardwoodSapling.getId());
		anglersPlant.conditionToHideInSidebar(or(not(hardwoodEnabled), not(accessToAnglersRetreat)));
		anglersCheckHealth.addSubSteps(anglersPlant);

		// Hardwood Tree Cut Down Steps
		eastHardwoodTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Cut down the hardwood tree planted on the eastern hardwood tree patch on Fossil Island.", axe);
		eastHardwoodTreePatchCutDown.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToFossilIsland)));

		middleHardwoodTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Cut down the hardwood tree planted on the centre hardwood tree patch on Fossil Island.", axe);
		middleHardwoodTreePatchCutDown.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToFossilIsland)));

		westHardwoodTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Cut down the hardwood tree planted on the western hardwood tree patch on Fossil Island.", axe);
		westHardwoodTreePatchCutDown.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToFossilIsland)));

		savannahCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Cut down the hardwood tree planted in the Avium Savannah.", axe);
		savannahCutDown.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToSavannah)));

		anglersCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470, 2704, 0),
			"Cut down the hardwood tree planted in the Anglers' Retreat.", axe);
		anglersCutDown.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToAnglersRetreat)));

		westHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to remove the west tree.");
		westHardwoodTreePatchClear.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForRemoval), not(accessToFossilIsland)));
		westHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");
		middleHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to remove the middle tree.");
		middleHardwoodTreePatchClear.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForRemoval), not(accessToFossilIsland)));
		middleHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");
		eastHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to remove the east tree.");
		eastHardwoodTreePatchClear.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForRemoval), not(accessToFossilIsland)));
		eastHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");

		savannahClear = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to clear the tree.");
		savannahClear.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForRemoval), not(accessToSavannah)));
		savannahClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
			"Coins - chop my tree down please.", "Yes.");

		anglersClear = new NpcStep(this, NpcID.FARMING_GARDENER_HARDWOOD_TREE_5, new WorldPoint(2470, 2704, 0),
			"Pay Argo to clear the tree.");
		anglersClear.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForRemoval), not(accessToAnglersRetreat)));
		anglersClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
			"Coins - chop my tree down please.", "Yes.");

		westHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Dig up the western hardwood tree's stump on Fossil Island.");
		westHardwoodTreePatchDig.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToFossilIsland)));
		middleHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Dig up the centre hardwood tree's stump on Fossil Island.");
		middleHardwoodTreePatchDig.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToFossilIsland)));
		eastHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Dig up the eastern hardwood tree's stump on Fossil Island.");
		eastHardwoodTreePatchDig.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToFossilIsland)));
		savannahDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Dig up the Savannah hardwood tree's stump.");
		savannahDig.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToSavannah)));
		anglersDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470,2704, 0),
			"Dig up the Anglers' Retreat hardwood tree's stump.");
		anglersDig.conditionToHideInSidebar(or(not(hardwoodEnabled), payingForRemoval, not(accessToAnglersRetreat)));

		westHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to protect the west tree.");
		westHardwoodProtect.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForProtection), not(accessToFossilIsland)));
		westHardwoodProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		middleHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to protect the middle tree.");
		middleHardwoodProtect.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForProtection), not(accessToFossilIsland)));
		middleHardwoodProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		eastHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to protect the east tree.");
		eastHardwoodProtect.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForProtection), not(accessToFossilIsland)));
		eastHardwoodProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		savannahProtect = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to protect the hardwood tree.");
		savannahProtect.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForProtection), not(accessToSavannah)));
		savannahProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		anglersProtect = new NpcStep(this, NpcID.FARMING_GARDENER_HARDWOOD_TREE_5, new WorldPoint(2470, 2704, 0),
			"Pay Argo to protect the hardwood tree.");
		anglersProtect.conditionToHideInSidebar(or(not(hardwoodEnabled), not(payingForProtection), not(accessToAnglersRetreat)));
		anglersProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		westHardwoodTreePatchClear.addSubSteps(westHardwoodTreePatchDig);
		middleHardwoodTreePatchClear.addSubSteps(middleHardwoodTreePatchDig);
		eastHardwoodTreePatchClear.addSubSteps(eastHardwoodTreePatchDig);
		savannahClear.addSubSteps(savannahDig);
		anglersClear.addSubSteps(anglersDig);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		coins.setQuantity(0);

		fruitTreeFactory.onGameTick(event, farmingWorld, farmingHandler, client, paymentTracker);

		treesEnabled = not(new RuneliteRequirement(configManager, TREE_SAPLING, TreeSapling.NONE.name()));
		hardwoodEnabled = not(new RuneliteRequirement(configManager, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.NONE.name()));
		calquatEnabled = not(new RuneliteRequirement(configManager, CALQUAT_TREE_SAPLING, CalquatTreeSapling.NONE.name()));

		allProtectionItemTree.setQuantity(protectionItemTree.getQuantity());
		allProtectionItemHardwood.setQuantity(protectionItemHardwood.getQuantity());
		allProtectionItemCalquat.setQuantity(protectionItemCalquat.getQuantity());
		handleTreePatches(PatchImplementation.TREE,
			List.of(farmingGuildTreeStates, varrockStates, faladorStates, taverleyStates, lumbridgeStates, gnomeStrongholdTreeStates, auburnvaleStates),
			farmingWorld.getTabs().get(Tab.TREE), allTreeSaplings, allProtectionItemTree);
		handleTreePatches(PatchImplementation.CALQUAT,
			List.of(taiBwoWannaiStates, kastoriCalquatStates, greatConchStates),
			farmingWorld.getTabs().get(Tab.FRUIT_TREE), allCalquatSaplings, allProtectionItemCalquat);
		handleTreePatches(PatchImplementation.HARDWOOD_TREE, List.of(westHardwoodStates, middleHardwoodStates,
				eastHardwoodStates, savannahStates, anglersRetreatStates),
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
			boolean isGrowing = state == CropState.GROWING;
			boolean isProtected = paymentTracker.getProtectedState(patch);
			boolean needsProtection = !isProtected && payingForProtection.check(client);

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
				region.getIsGrowing().setShouldPass(isGrowing && !needsProtection);
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
				fruitTreeFactory::updateSapling, FruitTreeSapling.APPLE, configManager, questHelperPlugin);
			FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.class,
				this::updateHardwoodTreeSapling, HardwoodTreeSapling.TEAK, configManager, questHelperPlugin);
			FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, CALQUAT_TREE_SAPLING, CalquatTreeSapling.class,
				this::updateCalquatTreeSapling, CalquatTreeSapling.CALQUAT, configManager, questHelperPlugin);

			if (event.getKey().equals(GRACEFUL_OR_FARMING) || event.getKey().equals(PAY_OR_CUT) || event.getKey().equals(PAY_OR_COMPOST))
			{
				questHelperPlugin.refreshBank();
			}
		});
	}
	private final String TREE_SAPLING = "treeSaplings";
	private final String FRUIT_TREE_SAPLING = "fruitTreeSaplings";
	private final String HARDWOOD_TREE_SAPLING = "hardwoodTreeSaplings";
	private final String CALQUAT_TREE_SAPLING = "calquatTreeSaplings";

	@Override
	public List<HelperConfig> getConfigs()
	{
		HelperConfig treesConfig = new HelperConfig("Trees", TREE_SAPLING, TreeSapling.values());
		HelperConfig hardwoodTreesConfig = new HelperConfig("Hardwood Trees", HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.values());
		HelperConfig calquatTreesConfig = new HelperConfig("Calquat Trees", CALQUAT_TREE_SAPLING, CalquatTreeSapling.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		HelperConfig payOrCutConfig = new HelperConfig("Pay or cut tree removal", PAY_OR_CUT, PayOrCut.values());
		HelperConfig payOrCompostConfig = new HelperConfig("Pay farmer or compost", PAY_OR_COMPOST, PayOrCompost.values());
		return Arrays.asList(treesConfig, fruitTreeFactory.getConfig(), hardwoodTreesConfig, calquatTreesConfig, outfitConfig, payOrCutConfig, payOrCompostConfig);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, rake, compost, coins, axe, allTreeSaplings, fruitTreeFactory.getAllSaplings(), allHardwoodSaplings, allCalquatSaplings, allProtectionItemTree, fruitTreeFactory.getAllProtectionItems(), allProtectionItemHardwood, allProtectionItemCalquat);
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
		
		allSteps.add(new PanelDetails("Wait for Trees", waitForTree).withHideCondition(nor(allGrowing)));

		PanelDetails farmingGuildTreePanel = new PanelDetails("Tree Patch",
			Arrays.asList(farmingGuildTreePatchCheckHealth, farmingGuildTreePatchCutDown, farmingGuildTreePatchDig, farmingGuildTreePatchClear, farmingGuildTreePatchPlant, farmingGuildTreePayForProtection)).withId(-1);
		farmingGuildTreePanel.setLockingStep(farmingGuildTreeStep);
		farmingGuildTreePanel.setHideCondition(or(not(treesEnabled), not(accessToFarmingGuildTreePatch)));
		var farmingGuildPanel = new TopLevelPanelDetails("Farming Guild",
			farmingGuildTreePanel, fruitTreeFactory.getFarmingGuildPanel()).withId(0);
		farmingGuildPanel.setLockingStep(farmingGuildStep);
		farmingGuildPanel.setHideCondition(and(
			or(not(treesEnabled), not(accessToFarmingGuildTreePatch)),
			fruitTreeFactory.shouldHideTopLevelFarmingGuildPanel()
		));

		PanelDetails lumbridgePanel = new PanelDetails("Lumbridge", Arrays.asList(lumbridgeTreePatchCheckHealth, lumbridgeTreePatchCutDown, lumbridgeTreePatchDig, lumbridgeTreePatchClear, lumbridgeTreePatchPlant, lumbridgeTreeProtect)).withId(1);
		lumbridgePanel.setLockingStep(lumbridgeStep);
		lumbridgePanel.setHideCondition(not(treesEnabled));

		PanelDetails faladorPanel = new PanelDetails("Falador", Arrays.asList(faladorTreePatchCheckHealth, faladorTreePatchCutDown, faladorTreePatchDig, faladorTreePatchClear, faladorTreePatchPlant, faladorTreeProtect)).withId(2);
		faladorPanel.setLockingStep(faladorStep);
		faladorPanel.setHideCondition(not(treesEnabled));

		PanelDetails taverleyPanel = new PanelDetails("Taverley", Arrays.asList(taverleyTreePatchCheckHealth, taverleyTreePatchCutDown, taverleyTreePatchDig, taverleyTreePatchClear, taverleyTreePatchPlant, taverleyTreeProtect)).withId(3);
		taverleyPanel.setLockingStep(taverleyStep);
		taverleyPanel.setHideCondition(not(treesEnabled));

		PanelDetails varrockPanel = new PanelDetails("Varrock", Arrays.asList(varrockTreePatchCheckHealth, varrockTreePatchCutDown, varrockTreePatchDig, varrockTreePatchClear, varrockTreePatchPlant, varrockTreeProtect)).withId(4);
		varrockPanel.setLockingStep(varrockStep);
		varrockPanel.setHideCondition(not(treesEnabled));

		PanelDetails gnomeStrongholdTreePanel = new PanelDetails("Tree Patch",
			Arrays.asList(gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchCutDown, gnomeStrongholdTreePatchDig, gnomeStrongholdTreePatchClear, gnomeStrongholdTreePatchPlant, strongholdTreeProtect)).withId(52);
		gnomeStrongholdTreePanel.setLockingStep(strongholdTreeStep);
		gnomeStrongholdTreePanel.setHideCondition(not(treesEnabled));

		var gnomeStrongholdPanel = new TopLevelPanelDetails("Gnome Stronghold",
			fruitTreeFactory.getGnomeStrongholdPanel(),
			gnomeStrongholdTreePanel).withId(5);
		gnomeStrongholdPanel.setLockingStep(strongholdStep);
		gnomeStrongholdPanel.setHideCondition(and(not(treesEnabled), fruitTreeFactory.shouldHideTopLevelGnomeStrongholdPanel()));

		PanelDetails taiBwoWannaiPanel = new PanelDetails("Tai Bwo Wannai", Arrays.asList(taiBwoWannaiCalquatPatchCheckHealth, taiBwoWannaiCalquatPatchRemove, taiBwoWannaiCalquatPatchDig, taiBwoWannaiCalquatPatchClear, taiBwoWannaiCalquatPatchPlant, taiBwoWannaiCalquatProtect)).withId(82);
		taiBwoWannaiPanel.setLockingStep(taiBwoWannaiStep);
		taiBwoWannaiPanel.setHideCondition(or(not(accessToCalquatFarming), not(calquatEnabled)));

		var karamjaPanel = new TopLevelPanelDetails("Karamja", fruitTreeFactory.getBrimhavenPanel(), taiBwoWannaiPanel).withId(8);
		karamjaPanel.setLockingStep(karamjaStep);
		karamjaPanel.setHideCondition(and(fruitTreeFactory.shouldHideTopLevelBrimhavenPanel(), not(calquatEnabled)));

		PanelDetails fossilIslandEastPanel = new PanelDetails("East Hardwood Patch",
			Arrays.asList(eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchCutDown, eastHardwoodTreePatchDig, eastHardwoodTreePatchClear, eastHardwoodTreePatchPlant, eastHardwoodProtect)
		).withId(101);
		fossilIslandEastPanel.setLockingStep(fossilIslandEastStep);
		fossilIslandEastPanel.setHideCondition(or(not(accessToFossilIsland), not(hardwoodEnabled)));
		PanelDetails fossilIslandMiddlePanel = new PanelDetails("Middle Hardwood Patch",
			Arrays.asList(middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchCutDown, middleHardwoodTreePatchDig, middleHardwoodTreePatchClear, middleHardwoodTreePatchPlant, middleHardwoodProtect)
		).withId(102);
		fossilIslandMiddlePanel.setLockingStep(fossilIslandMiddleStep);
		fossilIslandMiddlePanel.setHideCondition(or(not(accessToFossilIsland), not(hardwoodEnabled)));
		PanelDetails fossilIslandWestPanel = new PanelDetails("West Hardwood Patch",
			Arrays.asList(westHardwoodTreePatchCheckHealth, westHardwoodTreePatchCutDown, westHardwoodTreePatchDig, westHardwoodTreePatchClear, westHardwoodTreePatchPlant, westHardwoodProtect)
		).withId(103);
		fossilIslandWestPanel.setLockingStep(fossilIslandWestStep);
		fossilIslandWestPanel.setHideCondition(or(not(accessToFossilIsland), not(hardwoodEnabled)));
		var fossilIslandPanel = new TopLevelPanelDetails("Fossil Island",
			fossilIslandEastPanel, fossilIslandMiddlePanel, fossilIslandWestPanel).withId(10);
		fossilIslandPanel.setLockingStep(fossilIslandStep);
		fossilIslandPanel.setHideCondition(or(not(accessToFossilIsland), not(hardwoodEnabled)));

		PanelDetails savannahPanel = new PanelDetails("Avium Savannah", Arrays.asList(savannahCheckHealth, savannahCutDown, savannahDig, savannahClear, savannahPlant, savannahProtect)).withId(11);
		savannahPanel.setLockingStep(savannahStep);
		savannahPanel.setHideCondition(or(not(accessToSavannah), not(hardwoodEnabled)));

		PanelDetails auburnvalePanel = new PanelDetails("Auburnvale", Arrays.asList(auburnvaleTreePatchCheckHealth, auburnvaleTreePatchCutDown, auburnvaleTreePatchDig, auburnvaleTreePatchClear, auburnvaleTreePatchPlant, auburnvaleTreeProtect)).withId(12);
		auburnvalePanel.setLockingStep(auburnvaleStep);
		auburnvalePanel.setHideCondition(or(not(accessToVarlamore), not(hardwoodEnabled)));

		PanelDetails kastoriCalquatPanel = new PanelDetails("Calquat Patch", Arrays.asList(kastoriCalquatPatchCheckHealth, kastoriCalquatPatchRemove, kastoriCalquatPatchDig, kastoriCalquatPatchClear, kastoriCalquatPatchPlant, kastoriCalquatProtect)).withId(132);
		kastoriCalquatPanel.setLockingStep(kastoriCalquatStep);
		kastoriCalquatPanel.setHideCondition(or(not(accessToVarlamore), not(accessToCalquatFarming), not(calquatEnabled)));

		var kastoriPanel = new TopLevelPanelDetails("Kastori", fruitTreeFactory.getKastoriPanel(), kastoriCalquatPanel).withId(13);
		kastoriPanel.setLockingStep(kastoriStep);
		kastoriPanel.setHideCondition(and(fruitTreeFactory.shouldHideTopLevelKastoriPanel(), or(not(accessToVarlamore), not(calquatEnabled))));

		PanelDetails anglersPanel = new PanelDetails("Anglers' Retreat", Arrays.asList(anglersCheckHealth,
			anglersCutDown, anglersDig, anglersClear, anglersPlant, anglersProtect)).withId(14);
		anglersPanel.setLockingStep(anglersRetreatStep);
		anglersPanel.setHideCondition(or(not(accessToAnglersRetreat), not(hardwoodEnabled)));

		PanelDetails greatConchPanel = new PanelDetails("Great Conch",
			Arrays.asList(greatConchCalquatPatchCheckHealth, greatConchCalquatPatchRemove, greatConchCalquatPatchDig, greatConchCalquatPatchClear,
				greatConchCalquatPatchPlant, greatConchCalquatProtect)).withId(15);
		greatConchPanel.setLockingStep(greatConchStep);
		greatConchPanel.setHideCondition(or(not(accessToCalquatFarming), not(accessToGreatConch), not(calquatEnabled)));

		var farmRunSidebar = new TopLevelPanelDetails("Tree Run", farmingGuildPanel, lumbridgePanel, faladorPanel, taverleyPanel,
			varrockPanel, gnomeStrongholdPanel, fruitTreeFactory.getGnomeVillagePanel(), fruitTreeFactory.getCatherbyPanel(), karamjaPanel, fruitTreeFactory.getLletyaPanel(), fossilIslandPanel, savannahPanel, auburnvalePanel,
			kastoriPanel, anglersPanel, greatConchPanel);
		farmRunSidebar.setHideCondition(and(not(treesEnabled), /*not(fruitTreesEnabled), */not(hardwoodEnabled), not(calquatEnabled)));
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

	private void updateHardwoodTreeSapling(HardwoodTreeSapling selectedHardwoodTreeSapling)
	{
		hardwoodSapling.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		hardwoodSapling.setName(itemManager.getItemComposition(selectedHardwoodTreeSapling.getPlantableItemId()).getName());

		allHardwoodSaplings.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		allHardwoodSaplings.setName(itemManager.getItemComposition(selectedHardwoodTreeSapling.getPlantableItemId()).getName());
		updateHardwoodTreePaymentItem(selectedHardwoodTreeSapling);
	}

	private void updateCalquatTreeSapling(CalquatTreeSapling selectedCalquatTreeSapling)
	{
		calquatSapling.setId(selectedCalquatTreeSapling.calquatTreeSaplingId);
		calquatSapling.setName(itemManager.getItemComposition(selectedCalquatTreeSapling.getPlantableItemId()).getName());

		allCalquatSaplings.setId(selectedCalquatTreeSapling.calquatTreeSaplingId);
		allCalquatSaplings.setName(itemManager.getItemComposition(selectedCalquatTreeSapling.getPlantableItemId()).getName());
		updateCalquatPaymentItem(selectedCalquatTreeSapling);
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

	private void updateHardwoodTreePaymentItem(HardwoodTreeSapling treeSapling)
	{
		protectionItemHardwood.setId(treeSapling.protectionItemId);
		protectionItemHardwood.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		protectionItemHardwood.setQuantity(treeSapling.protectionItemQuantity);

		allProtectionItemHardwood.setId(treeSapling.protectionItemId);
		allProtectionItemHardwood.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		allProtectionItemHardwood.setQuantity(treeSapling.protectionItemQuantity);
	}

	private void updateCalquatPaymentItem(CalquatTreeSapling treeSapling)
	{
		protectionItemCalquat.setId(treeSapling.protectionItemId);
		protectionItemCalquat.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		protectionItemCalquat.setQuantity(treeSapling.protectionItemQuantity);

		allProtectionItemCalquat.setId(treeSapling.protectionItemId);
		allProtectionItemCalquat.setName(itemManager.getItemComposition(treeSapling.protectionItemId).getName());
		allProtectionItemCalquat.setQuantity(treeSapling.protectionItemQuantity);
	}
}
