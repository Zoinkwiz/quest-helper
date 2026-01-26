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

	// Fruit Trees
	DetailedQuestStep farmingGuildFruitTreePatchCheckHealth, gnomeStrongholdFruitTreePatchCheckHealth,
		gnomeVillageFruitTreePatchCheckHealth, brimhavenFruitTreePatchCheckHealth, lletyaFruitTreePatchCheckHealth,
		catherbyFruitTreePatchCheckHealth, taiBwoWannaiCalquatPatchCheckHealth, kastoriFruitTreePatchCheckHealth,
		kastoriCalquatPatchCheckHealth, greatConchCalquatPatchCheckHealth;
	DetailedQuestStep farmingGuildFruitTreePatchPlant, gnomeStrongholdFruitTreePatchPlant,
		gnomeVillageFruitTreePatchPlant, brimhavenFruitTreePatchPlant, lletyaFruitTreePatchPlant,
		catherbyFruitTreePatchPlant, taiBwoWannaiCalquatPatchPlant, kastoriFruitTreePatchPlant,
		kastoriCalquatPatchPlant, greatConchCalquatPatchPlant;

	DetailedQuestStep farmingGuildFruitTreePatchClear, gnomeStrongholdFruitTreePatchClear,
		gnomeVillageFruitTreePatchClear, brimhavenFruitTreePatchClear, lletyaFruitTreePatchClear,
		catherbyFruitTreePatchClear, taiBwoWannaiCalquatPatchClear, kastoriFruitTreePatchClear,
		kastoriCalquatPatchClear, greatConchCalquatPatchClear;
	DetailedQuestStep farmingGuildFruitTreePatchDig, gnomeStrongholdFruitTreePatchDig, gnomeVillageFruitTreePatchDig,
		brimhavenFruitTreePatchDig, lletyaFruitTreePatchDig, catherbyFruitTreePatchDig,
		taiBwoWannaiCalquatPatchDig, kastoriFruitTreePatchDig, kastoriCalquatPatchDig, greatConchCalquatPatchDig;

	DetailedQuestStep guildFruitProtect, strongholdFruitProtect, villageFruitProtect, brimhavenFruitProtect,
		lletyaFruitProtect, catherbyFruitProtect, taiBwoWannaiCalquatProtect, kastoriFruitProtect,
		kastoriCalquatProtect, greatConchCalquatProtect;

	// Hardwood Trees
	DetailedQuestStep eastHardwoodTreePatchCheckHealth, westHardwoodTreePatchCheckHealth,
		middleHardwoodTreePatchCheckHealth, savannahCheckHealth, anglersCheckHealth;
	DetailedQuestStep eastHardwoodTreePatchPlant, westHardwoodTreePatchPlant, middleHardwoodTreePatchPlant,
		savannahPlant, anglersPlant;
	DetailedQuestStep eastHardwoodTreePatchDig, westHardwoodTreePatchDig, middleHardwoodTreePatchDig, savannahDig,
		anglersDig;

	DetailedQuestStep eastHardwoodTreePatchClear, westHardwoodTreePatchClear, middleHardwoodTreePatchClear,
		savannahClear, anglersClear;

	DetailedQuestStep eastHardwoodProtect, westHardwoodProtect, middleHardwoodProtect, savannahProtect,
		anglersProtect;

	// Farming Items
	ItemRequirement coins, spade, rake, allTreeSaplings, treeSapling, allFruitSaplings, fruitTreeSapling,
		allHardwoodSaplings, hardwoodSapling, calquatSapling, allCalquatSaplings,
		compost, axe,	protectionItemTree, allProtectionItemTree, protectionItemCalquat, allProtectionItemCalquat,
		protectionItemFruitTree, allProtectionItemFruitTree, protectionItemHardwood, allProtectionItemHardwood;

	// Teleport Items
	// TODO: Add these...
	ItemRequirement farmingGuildTeleport, crystalTeleport, catherbyTeleport, varrockTeleport, lumbridgeTeleport,
		faladorTeleport, fossilIslandTeleport, auburnvaleTeleport, kastoriTeleport;

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape,
		gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland,
		accessToSavannah, accessToVarlamore, accessToAnglersRetreat, accessToGreatConch;

	Requirement payingForRemoval, payingForProtection, usingCompostorNothing;

	PatchStates faladorStates, lumbridgeStates, farmingGuildTreeStates, taverleyStates, varrockStates,
		gnomeStrongholdTreeStates, auburnvaleStates;

	PatchStates gnomeStrongholdFruitStates, gnomeVillageStates, brimhavenStates, catherbyStates, lletyaStates,
		farmingGuildFruitStates, taiBwoWannaiStates, kastoriFruitStates, kastoriCalquatStates, greatConchStates;

	PatchStates eastHardwoodStates, middleHardwoodStates, westHardwoodStates, savannahStates, anglersRetreatStates;

	Requirement allGrowing;

	ReorderableConditionalStep farmingGuildStep, strongholdStep, karamjaStep, fossilIslandStep, kastoriStep;
	ConditionalStep farmingGuildTreeStep, farmingGuildFruitStep, lumbridgeStep, varrockStep, faladorStep, taverleyStep,
		strongholdTreeStep,	strongholdFruitStep, villageStep, lletyaStep, catherbyStep, brimhavenStep,
		taiBwoWannaiStep, fossilIslandEastStep, fossilIslandMiddleStep, fossilIslandWestStep, savannahStep,
		auburnvaleStep,	kastoriFruitStep, kastoriCalquatStep, anglersRetreatStep, greatConchStep;

	private final String PAY_OR_CUT = "payOrCutTree";
	private final String PAY_OR_COMPOST = "payOrCompostTree";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	public QuestStep loadStep()
	{
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
		farmingGuildTreeStep.addStep(and(farmingGuildTreeStates.getIsHarvestable(), not(payingForRemoval)), farmingGuildTreePatchCutDown);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsHarvestable(), farmingGuildTreePatchClear);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsStump(), farmingGuildTreePatchDig);
		farmingGuildTreeStep.addStep(farmingGuildTreeStates.getIsEmpty(), farmingGuildTreePatchPlant);
		farmingGuildTreeStep.addStep(nor(farmingGuildTreeStates.getIsProtected(), usingCompostorNothing), farmingGuildTreePayForProtection);
		farmingGuildStep = new ReorderableConditionalStep(this, farmingGuildTreeStep);

		farmingGuildFruitStep = (ConditionalStep) new ConditionalStep(this, farmingGuildFruitTreePatchCheckHealth).withId(-2);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsUnchecked()), farmingGuildFruitTreePatchCheckHealth);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsHarvestable()), farmingGuildFruitTreePatchClear);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsStump()), farmingGuildFruitTreePatchDig);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsEmpty()), farmingGuildFruitTreePatchPlant);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, nor(farmingGuildFruitStates.getIsProtected(), usingCompostorNothing)), guildFruitProtect);
		farmingGuildStep.addStep(and(accessToFarmingGuildFruitTreePatch, not(farmingGuildFruitStates.getIsGrowing())), farmingGuildFruitStep);
		steps.addStep(or(and(accessToFarmingGuildTreePatch, not(farmingGuildTreeStates.getIsGrowing())), and(accessToFarmingGuildFruitTreePatch, not(farmingGuildFruitStates.getIsGrowing()))), farmingGuildStep.withId(0));

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

		strongholdFruitStep = (ConditionalStep) new ConditionalStep(this, gnomeStrongholdFruitTreePatchCheckHealth).withId(51);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsUnchecked(), gnomeStrongholdFruitTreePatchCheckHealth);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsEmpty(), gnomeStrongholdFruitTreePatchPlant);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsHarvestable(), gnomeStrongholdFruitTreePatchClear);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsStump(), gnomeStrongholdFruitTreePatchDig);
		strongholdFruitStep.addStep(nor(usingCompostorNothing, gnomeStrongholdFruitStates.getIsProtected()), strongholdFruitProtect);
		strongholdStep = new ReorderableConditionalStep(this, strongholdFruitStep, not(gnomeStrongholdFruitStates.getIsGrowing()));

		strongholdTreeStep = (ConditionalStep) new ConditionalStep(this, gnomeStrongholdTreePatchCheckHealth).withId(52);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsUnchecked(), gnomeStrongholdTreePatchCheckHealth);
		strongholdTreeStep.addStep(and(gnomeStrongholdTreeStates.getIsHarvestable(), not(payingForRemoval)), gnomeStrongholdTreePatchCutDown);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsEmpty(), gnomeStrongholdTreePatchPlant);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsHarvestable(), gnomeStrongholdTreePatchClear);
		strongholdTreeStep.addStep(gnomeStrongholdTreeStates.getIsStump(), gnomeStrongholdTreePatchDig);
		strongholdTreeStep.addStep(nor(usingCompostorNothing, gnomeStrongholdTreeStates.getIsProtected()), strongholdTreeProtect);
		strongholdStep.addStep(not(gnomeStrongholdTreeStates.getIsGrowing()), strongholdTreeStep);
		steps.addStep(nand(gnomeStrongholdFruitStates.getIsGrowing(), gnomeStrongholdTreeStates.getIsGrowing()), strongholdStep.withId(5));

		villageStep = new ConditionalStep(this, gnomeVillageFruitTreePatchCheckHealth);
		villageStep.addStep(gnomeVillageStates.getIsUnchecked(), gnomeVillageFruitTreePatchCheckHealth);
		villageStep.addStep(gnomeVillageStates.getIsEmpty(), gnomeVillageFruitTreePatchPlant);
		villageStep.addStep(gnomeVillageStates.getIsHarvestable(), gnomeVillageFruitTreePatchClear);
		villageStep.addStep(gnomeVillageStates.getIsStump(), gnomeVillageFruitTreePatchDig);
		villageStep.addStep(nor(usingCompostorNothing, gnomeVillageStates.getIsProtected()), villageFruitProtect);
		steps.addStep(not(gnomeVillageStates.getIsGrowing()), villageStep.withId(6));

		catherbyStep = new ConditionalStep(this, catherbyFruitTreePatchCheckHealth);
		catherbyStep.addStep(catherbyStates.getIsUnchecked(), catherbyFruitTreePatchCheckHealth);
		catherbyStep.addStep(catherbyStates.getIsEmpty(), catherbyFruitTreePatchPlant);
		catherbyStep.addStep(catherbyStates.getIsHarvestable(), catherbyFruitTreePatchClear);
		catherbyStep.addStep(catherbyStates.getIsStump(), catherbyFruitTreePatchDig);
		catherbyStep.addStep(nor(usingCompostorNothing, catherbyStates.getIsProtected()), catherbyFruitProtect);
		steps.addStep(not(catherbyStates.getIsGrowing()), catherbyStep.withId(7));

		brimhavenStep = (ConditionalStep) new ConditionalStep(this, brimhavenFruitTreePatchCheckHealth).withId(81);
		brimhavenStep.addStep(brimhavenStates.getIsUnchecked(), brimhavenFruitTreePatchCheckHealth);
		brimhavenStep.addStep(brimhavenStates.getIsEmpty(), brimhavenFruitTreePatchPlant);
		brimhavenStep.addStep(brimhavenStates.getIsHarvestable(), brimhavenFruitTreePatchClear);
		brimhavenStep.addStep(brimhavenStates.getIsStump(), brimhavenFruitTreePatchDig);
		brimhavenStep.addStep(nor(usingCompostorNothing, brimhavenStates.getIsProtected()), brimhavenFruitProtect);
		karamjaStep = new ReorderableConditionalStep(this, brimhavenStep, not(brimhavenStates.getIsGrowing()));

		taiBwoWannaiStep = (ConditionalStep) new ConditionalStep(this, taiBwoWannaiCalquatPatchCheckHealth).withId(82);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsUnchecked(), taiBwoWannaiCalquatPatchCheckHealth);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsEmpty(), taiBwoWannaiCalquatPatchPlant);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsHarvestable(), taiBwoWannaiCalquatPatchClear);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsStump(), taiBwoWannaiCalquatPatchDig);
		taiBwoWannaiStep.addStep(nor(usingCompostorNothing, taiBwoWannaiStates.getIsProtected()),
			taiBwoWannaiCalquatProtect);
		karamjaStep.addStep(not(taiBwoWannaiStates.getIsGrowing()), taiBwoWannaiStep);
		steps.addStep(nand(brimhavenStates.getIsGrowing(), taiBwoWannaiStates.getIsGrowing()), karamjaStep.withId(8));

		lletyaStep = new ConditionalStep(this, lletyaFruitTreePatchCheckHealth);
		lletyaStep.addStep(lletyaStates.getIsUnchecked(), lletyaFruitTreePatchCheckHealth);
		lletyaStep.addStep(lletyaStates.getIsEmpty(), lletyaFruitTreePatchPlant);
		lletyaStep.addStep(lletyaStates.getIsHarvestable(), lletyaFruitTreePatchClear);
		lletyaStep.addStep(lletyaStates.getIsStump(), lletyaFruitTreePatchDig);
		lletyaStep.addStep(nor(usingCompostorNothing, lletyaStates.getIsProtected()), lletyaFruitProtect);
		steps.addStep(and(accessToLletya, not(lletyaStates.getIsGrowing())), lletyaStep.withId(9));

		fossilIslandEastStep = (ConditionalStep) new ConditionalStep(this, eastHardwoodTreePatchCheckHealth).withId(101);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsUnchecked(), eastHardwoodTreePatchCheckHealth);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsEmpty(), eastHardwoodTreePatchPlant);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsHarvestable(), eastHardwoodTreePatchClear);
		fossilIslandEastStep.addStep(eastHardwoodStates.getIsStump(), eastHardwoodTreePatchDig);
		fossilIslandEastStep.addStep(nor(usingCompostorNothing,  eastHardwoodStates.getIsProtected()), eastHardwoodProtect);
		fossilIslandStep = new ReorderableConditionalStep(this, fossilIslandEastStep, and(accessToFossilIsland, not(eastHardwoodStates.getIsGrowing())));

		fossilIslandMiddleStep = (ConditionalStep) new ConditionalStep(this, middleHardwoodTreePatchCheckHealth).withId(102);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsUnchecked(), middleHardwoodTreePatchCheckHealth);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsEmpty(), middleHardwoodTreePatchPlant);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsHarvestable(), middleHardwoodTreePatchClear);
		fossilIslandMiddleStep.addStep(middleHardwoodStates.getIsStump(), middleHardwoodTreePatchDig);
		fossilIslandMiddleStep.addStep(nor(usingCompostorNothing, middleHardwoodStates.getIsProtected()), middleHardwoodProtect);
		fossilIslandStep.addStep(and(accessToFossilIsland, not(middleHardwoodStates.getIsGrowing())), fossilIslandMiddleStep);

		fossilIslandWestStep = (ConditionalStep) new ConditionalStep(this, westHardwoodTreePatchCheckHealth).withId(103);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsUnchecked(), westHardwoodTreePatchCheckHealth);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsEmpty(), westHardwoodTreePatchPlant);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsHarvestable(), westHardwoodTreePatchClear);
		fossilIslandWestStep.addStep(westHardwoodStates.getIsStump(), westHardwoodTreePatchDig);
		fossilIslandWestStep.addStep(nor(usingCompostorNothing, westHardwoodStates.getIsProtected()), westHardwoodProtect);
		fossilIslandStep.addStep(and(accessToFossilIsland, not(westHardwoodStates.getIsGrowing())), fossilIslandWestStep);
		steps.addStep(nand(eastHardwoodStates.getIsGrowing(), middleHardwoodStates.getIsGrowing(), westHardwoodStates.getIsGrowing()), fossilIslandStep.withId(10));

		savannahStep = new ConditionalStep(this, savannahCheckHealth);
		savannahStep.addStep(savannahStates.getIsUnchecked(), savannahCheckHealth);
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

		kastoriFruitStep = (ConditionalStep) new ConditionalStep(this, kastoriFruitTreePatchCheckHealth).withId(131);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsUnchecked(), kastoriFruitTreePatchCheckHealth);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsEmpty(), kastoriFruitTreePatchPlant);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsHarvestable(), kastoriFruitTreePatchClear);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsStump(), kastoriFruitTreePatchDig);
		kastoriFruitStep.addStep(nor(usingCompostorNothing, kastoriFruitStates.getIsProtected()), kastoriFruitProtect);
		kastoriStep = new ReorderableConditionalStep(this, kastoriFruitStep, and(accessToVarlamore, not(kastoriFruitStates.getIsGrowing())));

		kastoriCalquatStep = (ConditionalStep) new ConditionalStep(this, kastoriCalquatPatchCheckHealth).withId(132);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsUnchecked(), kastoriCalquatPatchCheckHealth);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsEmpty(), kastoriCalquatPatchPlant);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsHarvestable(), kastoriCalquatPatchClear);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsStump(), kastoriCalquatPatchDig);
		kastoriCalquatStep.addStep(nor(usingCompostorNothing, kastoriCalquatStates.getIsProtected()), kastoriCalquatProtect);
		kastoriStep.addStep(and(accessToVarlamore, not(kastoriCalquatStates.getIsGrowing())), kastoriCalquatStep);
		steps.addStep(and(accessToVarlamore, nand(kastoriFruitStates.getIsGrowing(), kastoriCalquatStates.getIsGrowing())), kastoriStep.withId(13));

		anglersRetreatStep = new ConditionalStep(this, anglersCheckHealth);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsUnchecked(), anglersCheckHealth);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsEmpty(), anglersPlant);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsHarvestable(), anglersClear);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsStump(), anglersDig);
		anglersRetreatStep.addStep(nor(usingCompostorNothing, anglersRetreatStates.getIsProtected()), anglersProtect);
		steps.addStep(and(accessToAnglersRetreat, not(anglersRetreatStates.getIsGrowing())), anglersRetreatStep.withId(14));

		greatConchStep = new ConditionalStep(this, greatConchCalquatPatchCheckHealth);
		greatConchStep.addStep(greatConchStates.getIsUnchecked(), greatConchCalquatPatchCheckHealth);
		greatConchStep.addStep(greatConchStates.getIsEmpty(), greatConchCalquatPatchPlant);
		greatConchStep.addStep(greatConchStates.getIsHarvestable(), greatConchCalquatPatchClear);
		greatConchStep.addStep(greatConchStates.getIsStump(), greatConchCalquatPatchDig);
		greatConchStep.addStep(nor(usingCompostorNothing, greatConchStates.getIsProtected()), greatConchCalquatProtect);
		steps.addStep(and(accessToGreatConch, not(greatConchStates.getIsGrowing())), greatConchStep.withId(15));

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

		accessToAnglersRetreat = new Conditions(
			new SkillRequirement(Skill.SAILING, 51)
		);

		accessToGreatConch = new QuestRequirement(QuestHelperQuest.TROUBLED_TORTUGANS, QuestState.FINISHED);

		// Trees
		lumbridgeStates = new PatchStates("Lumbridge");
		faladorStates = new PatchStates("Falador");
		taverleyStates = new PatchStates("Taverley");
		varrockStates = new PatchStates("Varrock");
		gnomeStrongholdTreeStates = new PatchStates("Gnome Stronghold");
		farmingGuildTreeStates = new PatchStates("Farming Guild", accessToFarmingGuildTreePatch);
		auburnvaleStates = new PatchStates("Auburnvale", accessToVarlamore);

		// Fruit trees
		catherbyStates = new PatchStates("Catherby");
		brimhavenStates = new PatchStates("Brimhaven");
		taiBwoWannaiStates = new PatchStates("Tai Bwo Wannai");
		gnomeVillageStates = new PatchStates("Tree Gnome Village");
		gnomeStrongholdFruitStates = new PatchStates("Gnome Stronghold");
		lletyaStates = new PatchStates("Lletya", accessToLletya);
		farmingGuildFruitStates = new PatchStates("Farming Guild", accessToFarmingGuildFruitTreePatch);
		kastoriFruitStates = new PatchStates("Kastori", accessToVarlamore);
		kastoriCalquatStates = new PatchStates("Kastori", accessToVarlamore);
		greatConchStates = new PatchStates("Great Conch", accessToGreatConch);

		westHardwoodStates = new PatchStates("Fossil Island", "West");
		middleHardwoodStates = new PatchStates("Fossil Island", "Middle");
		eastHardwoodStates = new PatchStates("Fossil Island", "East");
		savannahStates = new PatchStates("Avium Savannah", accessToSavannah);
		anglersRetreatStates = new PatchStates("Anglers' Retreat", accessToAnglersRetreat);

		allGrowing = and(lumbridgeStates.getIsGrowing(), faladorStates.getIsGrowing(), taverleyStates.getIsGrowing(),
			varrockStates.getIsGrowing(), gnomeStrongholdTreeStates.getIsGrowing(), catherbyStates.getIsGrowing(),
			brimhavenStates.getIsGrowing(), taiBwoWannaiStates.getIsGrowing(), gnomeVillageStates.getIsGrowing(),
			gnomeStrongholdFruitStates.getIsGrowing(),
			or(not(accessToLletya), lletyaStates.getIsGrowing()),
			or(not(accessToVarlamore), auburnvaleStates.getIsGrowing()),
			or(not(accessToVarlamore), kastoriFruitStates.getIsGrowing()),
			or(not(accessToVarlamore), kastoriCalquatStates.getIsGrowing()),
			or(not(accessToFarmingGuildTreePatch), farmingGuildTreeStates.getIsGrowing()),
			or(not(accessToFarmingGuildFruitTreePatch), farmingGuildFruitStates.getIsGrowing()),
			or(not(accessToFossilIsland), and(westHardwoodStates.getIsGrowing(), middleHardwoodStates.getIsGrowing(), eastHardwoodStates.getIsGrowing())),
			or(not(accessToSavannah), savannahStates.getIsGrowing()),
			or(not(accessToAnglersRetreat), anglersRetreatStates.getIsGrowing()),
			or(not(accessToGreatConch), greatConchStates.getIsGrowing())
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

		CalquatTreeSapling calquatTreeSaplingEnum = (CalquatTreeSapling) FarmingUtils.getEnumFromConfig(configManager,
			CalquatTreeSapling.CALQUAT);
		calquatSapling = calquatTreeSaplingEnum.getPlantableItemRequirement(itemManager);
		calquatSapling.setHighlightInInventory(true);
		allCalquatSaplings = calquatSapling.copy();

		protectionItemCalquat = calquatTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
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
		farmingBoots.addAlternates(nItemID.TITHE_REWARD_FEET_FEMALE);

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

		auburnvaleTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to clear the patch.");
		auburnvaleTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

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

		auburnvaleTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to protect the patch.");
		auburnvaleTreeProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

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

		auburnvaleTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Check the health of the tree planted at Auburnvale");
		auburnvaleTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		auburnvaleTreePatchCheckHealth.addTeleport(auburnvaleTeleport);

		// Tree Cut Down Steps
		farmingGuildTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Cut down the tree planted in the Farming Guild.", axe);
		farmingGuildTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));
		farmingGuildTreePatchCutDown.addTeleport(farmingGuildTeleport);

		lumbridgeTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Cut down the tree planted in Lumbridge.", axe);
		lumbridgeTreePatchCutDown.addTeleport(lumbridgeTeleport);
		lumbridgeTreePatchCutDown.addSpellHighlight(NormalSpells.LUMBRIDGE_TELEPORT);
		lumbridgeTreePatchCutDown.addSpellHighlight(NormalSpells.LUMBRIDGE_HOME_TELEPORT);

		faladorTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Cut down the tree planted in Falador.", axe);
		faladorTreePatchCutDown.addTeleport(faladorTeleport);
		faladorTreePatchCutDown.addSpellHighlight(NormalSpells.FALADOR_TELEPORT);

		taverleyTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Cut down the tree planted in Taverley.", axe);

		varrockTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Cut down the tree planted in Varrock.", axe);
		varrockTreePatchCutDown.addTeleport(varrockTeleport);
		varrockTreePatchCutDown.addSpellHighlight(NormalSpells.VARROCK_TELEPORT);

		gnomeStrongholdTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Cut down the tree planted in the Tree Gnome Stronghold.", axe);

		auburnvaleTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Cut down the tree planted at Auburnvale.", axe);
		auburnvaleTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		auburnvaleTreePatchCutDown.addTeleport(auburnvaleTeleport);

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

		auburnvaleTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Plant your sapling in the Auburnvale tree patch.", treeSapling);
		auburnvaleTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		auburnvaleTreePatchPlant.addIcon(treeSapling.getId());
		auburnvaleTreePatchCheckHealth.addSubSteps(auburnvaleTreePatchPlant);

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
		auburnvaleTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Dig up the tree stump in the Auburnvale tree patch.");

		faladorTreePatchClear.addSubSteps(faladorTreePatchDig, faladorTreeProtect);
		taverleyTreePatchClear.addSubSteps(taverleyTreePatchDig, taverleyTreeProtect);
		varrockTreePatchClear.addSubSteps(varrockTreePatchDig, varrockTreeProtect);
		gnomeStrongholdTreePatchClear.addSubSteps(gnomeStrongholdTreePatchDig, strongholdTreeProtect);
		lumbridgeTreePatchClear.addSubSteps(lumbridgeTreePatchDig, lumbridgeTreeProtect);
		farmingGuildTreePatchClear.addSubSteps(farmingGuildTreePatchDig, farmingGuildTreePayForProtection);
		auburnvaleTreePatchClear.addSubSteps(auburnvaleTreePatchDig, auburnvaleTreeProtect);

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

		kastoriFruitTreePatchPlant = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Plant your sapling in the Kastori patch.", fruitTreeSapling);
		kastoriFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		kastoriFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());

		taiBwoWannaiCalquatPatchPlant = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Plant your sapling in the Tai Bwo Wannai patch.", calquatSapling);
		taiBwoWannaiCalquatPatchPlant.addIcon(calquatSapling.getId());

		kastoriCalquatPatchPlant = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Plant your sapling in the Kastori Calquat patch.", calquatSapling);
		kastoriCalquatPatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		kastoriCalquatPatchPlant.addIcon(calquatSapling.getId());

		greatConchCalquatPatchPlant = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Plant your sapling in the Great Conch patch.", calquatSapling);
		greatConchCalquatPatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToGreatConch));
		greatConchCalquatPatchPlant.addIcon(calquatSapling.getId());

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

		kastoriFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Check the health of the fruit tree planted in Kastori.");
		kastoriFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		kastoriFruitTreePatchCheckHealth.addTeleport(kastoriTeleport);
		kastoriFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);
		kastoriFruitTreePatchCheckHealth.addSubSteps(kastoriFruitTreePatchPlant);

		taiBwoWannaiCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Check the health of the calquat tree planted in Tai Bwo Wannai.", calquatSapling);
		taiBwoWannaiCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Tai Bwo Wannai", true);
		taiBwoWannaiCalquatPatchCheckHealth.addSubSteps(taiBwoWannaiCalquatPatchPlant);

		kastoriCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Check the health of the calquat tree planted in Kastori.");
		kastoriCalquatPatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		kastoriCalquatPatchCheckHealth.addTeleport(kastoriTeleport);
		kastoriCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);
		kastoriCalquatPatchCheckHealth.addSubSteps(kastoriCalquatPatchPlant);

		greatConchCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Check the health of the calquat tree planted in Great Conch.", calquatSapling);
		greatConchCalquatPatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToGreatConch));
		greatConchCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Great Conch", true);
		greatConchCalquatPatchCheckHealth.addSubSteps(greatConchCalquatPatchPlant);

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
		kastoriFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_7, new WorldPoint(1350, 3057, 0),
			"Pay Ehecatl 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		kastoriFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		taiBwoWannaiCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT, new WorldPoint(2795, 3102, 0),
			"Pay Imiago 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		taiBwoWannaiCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		kastoriCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_2, new WorldPoint(1366, 3033, 0),
			"Pay Tziuhtla 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		kastoriCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		greatConchCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_3, new WorldPoint(3129, 2406, 0),
			"Pay Guppa 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		greatConchCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

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
		kastoriFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_7, new WorldPoint(1350, 3057, 0),
			"Pay Ehecatl to protect the patch.");
		kastoriFruitProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		taiBwoWannaiCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT, new WorldPoint(2795, 3102, 0),
			"Pay Imiago to protect the patch.");
		taiBwoWannaiCalquatProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		kastoriCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_2, new WorldPoint(1366, 3033, 0),
			"Pay Tziuhtla to protect the patch.");
		kastoriCalquatProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		greatConchCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_3, new WorldPoint(3129, 2406, 0),
			"Pay Guppa to protect the patch.");
		greatConchCalquatProtect.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

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
		kastoriFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Dig up the fruit tree's stump in Kastori.");
		taiBwoWannaiCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Dig up the calquat tree's stump in Tai Bwo Wannai.");
		kastoriCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Dig up the calquat tree's stump in Kastori.");
		greatConchCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Dig up the calquat tree's stump in Great Conch.");

		gnomeStrongholdFruitTreePatchClear.addSubSteps(gnomeStrongholdFruitTreePatchDig, strongholdFruitProtect);
		gnomeVillageFruitTreePatchClear.addSubSteps(gnomeVillageFruitTreePatchDig, villageFruitProtect);
		brimhavenFruitTreePatchClear.addSubSteps(brimhavenFruitTreePatchDig, brimhavenFruitProtect);
		catherbyFruitTreePatchClear.addSubSteps(catherbyFruitTreePatchDig, catherbyFruitProtect);
		lletyaFruitTreePatchClear.addSubSteps(lletyaFruitTreePatchDig, lletyaFruitProtect);
		farmingGuildFruitTreePatchClear.addSubSteps(farmingGuildFruitTreePatchDig, guildFruitProtect);
		kastoriFruitTreePatchClear.addSubSteps(kastoriFruitTreePatchDig, kastoriFruitProtect);
		taiBwoWannaiCalquatPatchClear.addSubSteps(taiBwoWannaiCalquatPatchDig, taiBwoWannaiCalquatProtect);
		kastoriCalquatPatchClear.addSubSteps(kastoriCalquatPatchDig, kastoriCalquatProtect);
		greatConchCalquatPatchClear.addSubSteps(greatConchCalquatPatchDig, greatConchCalquatProtect);

		// Hardwood Tree Steps
		westHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Check the health of the western hardwood tree on Fossil Island.");
		middleHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Check the health of the centre hardwood tree on Fossil Island.");
		eastHardwoodTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Check the health of the eastern hardwood tree on Fossil Island.");
		savannahCheckHealth  = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Check the health of the hardwood tree in the Avium Savannah.");
		anglersCheckHealth = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470, 2704, 0),
			"Check the health of the hardwood tree in the Anglers' Retreat.");

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
			"Plant your sapling on the hardwood tree patch in the Avium Savannah.", hardwoodSapling);

		anglersPlant = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470, 2704, 0),
			"Plant your sapling on the hardwood tree patch on Anglers' Retreat.", hardwoodSapling);

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

		anglersClear = new NpcStep(this, NpcID.FARMING_GARDENER_HARDWOOD_TREE_5, new WorldPoint(2470, 2704, 0),
			"Pay Argo to clear the tree.");
		anglersClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
			"Coins - chop my tree down please.", "Yes.");

		westHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Dig up the western hardwood tree's stump on Fossil Island.");
		middleHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Dig up the centre hardwood tree's stump on Fossil Island.");
		eastHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Dig up the eastern hardwood tree's stump on Fossil Island.");
		savannahDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Dig up the Savannah hardwood tree's stump.");
		anglersDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470,2704, 0),
			"Dig up the Anglers' Retreat hardwood tree's stump.");

		westHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to protect the west tree.");
		middleHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to protect the middle tree.");
		eastHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to protect the east tree.");
		savannahProtect = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to protect the hardwood tree.");
		anglersProtect = new NpcStep(this, NpcID.FARMING_GARDENER_HARDWOOD_TREE_5, new WorldPoint(2470, 2704, 0),
			"Pay Argo to protect the hardwood tree.");

		westHardwoodTreePatchClear.addSubSteps(westHardwoodTreePatchDig, westHardwoodProtect);
		middleHardwoodTreePatchClear.addSubSteps(middleHardwoodTreePatchDig, middleHardwoodProtect);
		eastHardwoodTreePatchClear.addSubSteps(eastHardwoodTreePatchDig, eastHardwoodProtect);
		savannahClear.addSubSteps(savannahDig, savannahProtect);
		anglersClear.addSubSteps(anglersDig, anglersProtect);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		coins.setQuantity(0);
		allProtectionItemTree.setQuantity(protectionItemTree.getQuantity());
		allProtectionItemFruitTree.setQuantity(protectionItemFruitTree.getQuantity());
		allProtectionItemHardwood.setQuantity(protectionItemHardwood.getQuantity());
		allProtectionItemCalquat.setQuantity(protectionItemCalquat.getQuantity());
		handleTreePatches(PatchImplementation.TREE,
			List.of(farmingGuildTreeStates, varrockStates, faladorStates, taverleyStates, lumbridgeStates, gnomeStrongholdTreeStates, auburnvaleStates),
			farmingWorld.getTabs().get(Tab.TREE), allTreeSaplings, allProtectionItemTree);
		handleTreePatches(PatchImplementation.FRUIT_TREE,
			List.of(farmingGuildFruitStates, brimhavenStates, catherbyStates, gnomeStrongholdFruitStates, gnomeVillageStates, lletyaStates,
				kastoriFruitStates),
			farmingWorld.getTabs().get(Tab.FRUIT_TREE), allFruitSaplings, allProtectionItemFruitTree);
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
		HelperConfig fruitTreesConfig = new HelperConfig("Fruit Trees", FRUIT_TREE_SAPLING, FruitTreeSapling.values());
		HelperConfig hardwoodTreesConfig = new HelperConfig("Hardwood Trees", HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.values());
		HelperConfig calquatTreesConfig = new HelperConfig("Calquat Trees", CALQUAT_TREE_SAPLING, CalquatTreeSapling.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		HelperConfig payOrCutConfig = new HelperConfig("Pay or cut tree removal", PAY_OR_CUT, PayOrCut.values());
		HelperConfig payOrCompostConfig = new HelperConfig("Pay farmer or compost", PAY_OR_COMPOST, PayOrCompost.values());
		return Arrays.asList(treesConfig, fruitTreesConfig, hardwoodTreesConfig, calquatTreesConfig, outfitConfig, payOrCutConfig, payOrCompostConfig);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, rake, compost, coins, axe, allTreeSaplings, allFruitSaplings, allHardwoodSaplings, allCalquatSaplings, allProtectionItemTree, allProtectionItemFruitTree, allProtectionItemHardwood, allProtectionItemCalquat);
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
			Arrays.asList(farmingGuildTreePatchCheckHealth, farmingGuildTreePatchClear, farmingGuildTreePatchPlant)).withId(-1);
		farmingGuildTreePanel.setLockingStep(farmingGuildTreeStep);
		PanelDetails farmingGuildFruitPanel = new PanelDetails("Fruit Tree Patch",
			Arrays.asList(farmingGuildFruitTreePatchCheckHealth, farmingGuildFruitTreePatchClear, farmingGuildFruitTreePatchPlant)).withId(-2);
		farmingGuildFruitPanel.setLockingStep((farmingGuildFruitStep));
		var farmingGuildPanel = new TopLevelPanelDetails("Farming Guild",
			farmingGuildTreePanel, farmingGuildFruitPanel).withId(0);
		farmingGuildPanel.setLockingStep(farmingGuildStep);

		PanelDetails lumbridgePanel = new PanelDetails("Lumbridge", Arrays.asList(lumbridgeTreePatchCheckHealth, lumbridgeTreePatchClear, lumbridgeTreePatchPlant)).withId(1);
		lumbridgePanel.setLockingStep(lumbridgeStep);

		PanelDetails faladorPanel = new PanelDetails("Falador", Arrays.asList(faladorTreePatchCheckHealth, faladorTreePatchClear, faladorTreePatchPlant)).withId(2);
		faladorPanel.setLockingStep(faladorStep);

		PanelDetails taverleyPanel = new PanelDetails("Taverley", Arrays.asList(taverleyTreePatchCheckHealth, taverleyTreePatchClear, taverleyTreePatchPlant)).withId(3);
		taverleyPanel.setLockingStep(taverleyStep);

		PanelDetails varrockPanel = new PanelDetails("Varrock", Arrays.asList(varrockTreePatchCheckHealth, varrockTreePatchClear, varrockTreePatchPlant)).withId(4);
		varrockPanel.setLockingStep(varrockStep);

		PanelDetails gnomeStrongholdFruitPanel = new PanelDetails("Fruit Tree Patch",
			Arrays.asList(gnomeStrongholdFruitTreePatchCheckHealth, gnomeStrongholdFruitTreePatchClear, gnomeStrongholdFruitTreePatchPlant)).withId(51);
		gnomeStrongholdFruitPanel.setLockingStep(strongholdFruitStep);
		PanelDetails gnomeStrongholdTreePanel = new PanelDetails("Tree Patch",
			Arrays.asList(gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchClear, gnomeStrongholdTreePatchPlant)).withId(52);
		gnomeStrongholdTreePanel.setLockingStep(strongholdTreeStep);
		var gnomeStrongholdPanel = new TopLevelPanelDetails("Gnome Stronghold",
			gnomeStrongholdFruitPanel, gnomeStrongholdTreePanel).withId(5);
		gnomeStrongholdPanel.setLockingStep(strongholdStep);

		PanelDetails villagePanel = new PanelDetails("Tree Gnome Village", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth,
				gnomeVillageFruitTreePatchClear, gnomeVillageFruitTreePatchPlant)).withId(6);
		villagePanel.setLockingStep(villageStep);

		PanelDetails catherbyPanel = new PanelDetails("Catherby", Arrays.asList(catherbyFruitTreePatchCheckHealth, catherbyFruitTreePatchClear, catherbyFruitTreePatchPlant)).withId(7);
		catherbyPanel.setLockingStep(catherbyStep);

		PanelDetails brimhavenPanel = new PanelDetails("Brimhaven", Arrays.asList(brimhavenFruitTreePatchCheckHealth, brimhavenFruitTreePatchClear, brimhavenFruitTreePatchPlant)).withId(81);
		brimhavenPanel.setLockingStep(brimhavenStep);
		PanelDetails taiBwoWannaiPanel = new PanelDetails("Tai Bwo Wannai", Arrays.asList(taiBwoWannaiCalquatPatchCheckHealth, taiBwoWannaiCalquatPatchClear, taiBwoWannaiCalquatPatchPlant)).withId(82);
		taiBwoWannaiPanel.setLockingStep(taiBwoWannaiStep);
		var karamjaPanel = new TopLevelPanelDetails("Karamja", brimhavenPanel, taiBwoWannaiPanel).withId(8);
		karamjaPanel.setLockingStep(karamjaStep);

		PanelDetails lletyaPanel = new PanelDetails("Lletya", Arrays.asList(lletyaFruitTreePatchCheckHealth, lletyaFruitTreePatchClear, lletyaFruitTreePatchPlant)).withId(9);
		lletyaPanel.setLockingStep(lletyaStep);

		PanelDetails fossilIslandEastPanel = new PanelDetails("East Hardwood Patch",
			Arrays.asList(eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchClear, eastHardwoodTreePatchPlant)
		).withId(101);
		fossilIslandEastPanel.setLockingStep(fossilIslandEastStep);
		PanelDetails fossilIslandMiddlePanel = new PanelDetails("Middle Hardwood Patch",
			Arrays.asList(middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchClear, middleHardwoodTreePatchPlant)
		).withId(102);
		fossilIslandMiddlePanel.setLockingStep(fossilIslandMiddleStep);
		PanelDetails fossilIslandWestPanel = new PanelDetails("West Hardwood Patch",
			Arrays.asList(westHardwoodTreePatchCheckHealth, westHardwoodTreePatchClear, westHardwoodTreePatchPlant)
		).withId(103);
		fossilIslandWestPanel.setLockingStep(fossilIslandWestStep);
		var fossilIslandPanel = new TopLevelPanelDetails("Fossil Island",
			fossilIslandEastPanel, fossilIslandMiddlePanel, fossilIslandWestPanel).withId(10);
		fossilIslandPanel.setLockingStep(fossilIslandStep);

		PanelDetails savannahPanel = new PanelDetails("Avium Savannah", Arrays.asList(savannahCheckHealth, savannahClear, savannahPlant)).withId(11);
		savannahPanel.setLockingStep(savannahStep);

		PanelDetails auburnvalePanel = new PanelDetails("Auburnvale", Arrays.asList(auburnvaleTreePatchCheckHealth, auburnvaleTreePatchClear, auburnvaleTreePatchPlant)).withId(12);
		auburnvalePanel.setLockingStep(auburnvaleStep);

		PanelDetails kastoriFruitPanel = new PanelDetails("Fruit Tree Patch", Arrays.asList(kastoriFruitTreePatchCheckHealth, kastoriFruitTreePatchClear, kastoriFruitTreePatchPlant)).withId(131);
		kastoriFruitPanel.setLockingStep(kastoriFruitStep);
		PanelDetails kastoriCalquatPanel = new PanelDetails("Calquat Patch", Arrays.asList(kastoriCalquatPatchCheckHealth, kastoriCalquatPatchClear, kastoriCalquatPatchPlant)).withId(132);
		kastoriCalquatPanel.setLockingStep(kastoriCalquatStep);
		var kastoriPanel = new TopLevelPanelDetails("Kastori", kastoriFruitPanel, kastoriCalquatPanel).withId(13);
		kastoriPanel.setLockingStep(kastoriStep);

		PanelDetails anglersPanel = new PanelDetails("Anglers' Retreat", Arrays.asList(anglersCheckHealth,
			anglersClear, anglersPlant)).withId(14);
		anglersPanel.setLockingStep(anglersRetreatStep);

		PanelDetails greatConchPanel = new PanelDetails("Great Conch",
			Arrays.asList(greatConchCalquatPatchCheckHealth, greatConchCalquatPatchClear,
				greatConchCalquatPatchPlant)).withId(15);
		greatConchPanel.setLockingStep(greatConchStep);

		var farmRunSidebar = new TopLevelPanelDetails("Tree Run", farmingGuildPanel, lumbridgePanel, faladorPanel, taverleyPanel,
			varrockPanel, gnomeStrongholdPanel, villagePanel, catherbyPanel, karamjaPanel, lletyaPanel, fossilIslandPanel, savannahPanel, auburnvalePanel,
			kastoriPanel, anglersPanel, greatConchPanel);
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
