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
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.Category;
import com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.Destination;
import com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.CompostSource;
import com.questhelper.helpers.mischelpers.farmruns.TreeRunOptions.CompostType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.plugins.timetracking.farming.CompostState;

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

/** Combined tree runs with player-selected transport and supplies. */
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
	private TreeRunOptions options;
	private TreeRunCompostTracker compostTracker;
	private final Map<PatchStates, PatchChoice> patchChoices = new LinkedHashMap<>();
	private final Map<DetailedQuestStep, List<String>> originalStepText = new LinkedHashMap<>();
	private final Map<FarmingPatch, CropState> previousStates = new HashMap<>();
	private final Set<FarmingPatch> replantedThisRun = new HashSet<>();
	private final ManualRequirement showCompost = new ManualRequirement();
	private int compostNeeded;
	private String lastSupplySignature = "";
	private List<ItemRequirement> selectedTravelItems = new ArrayList<>();
	private final Map<Integer, ItemRequirement> travelCatalog = new LinkedHashMap<>();
	private final Map<Integer, ManualRequirement> travelVisibility = new HashMap<>();
	private final Map<ItemRequirement, ManualRequirement> quantityVisibility = new HashMap<>();
	private final ManualRequirement showCoins = new ManualRequirement();
	private final ManualRequirement showRake = new ManualRequirement();
	private final Map<Destination, String> nexusKeys = new LinkedHashMap<>();
	private final Set<PatchChoice> nearbyChoices = new HashSet<>();
	private boolean rocksAvailable;
	private String lastRouteSignature = "";

	private static final class PatchChoice
	{
		final Category category;
		final Destination destination;
		final ConditionalStep route;
		final ConditionalStep parent;
		final DetailedQuestStep compostStep;
		final ManualRequirement needsCompost = new ManualRequirement();
		final List<DetailedQuestStep> instructions;

		PatchChoice(Category category, Destination destination, ConditionalStep route, ConditionalStep parent,
			DetailedQuestStep compostStep, List<DetailedQuestStep> instructions)
		{
			this.category = category;
			this.destination = destination;
			this.route = route;
			this.parent = parent;
			this.compostStep = compostStep;
			this.instructions = instructions;
		}
	}

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
	DetailedQuestStep farmingGuildFruitTreePatchCutDown, gnomeStrongholdFruitTreePatchCutDown, gnomeVillageFruitTreePatchCutDown,
		brimhavenFruitTreePatchCutDown, lletyaFruitTreePatchCutDown, catherbyFruitTreePatchCutDown,
		kastoriFruitTreePatchCutDown;
	DetailedQuestStep taiBwoWannaiCalquatPatchRemove, kastoriCalquatPatchRemove, greatConchCalquatPatchRemove;

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
	DetailedQuestStep eastHardwoodTreePatchCutDown, westHardwoodTreePatchCutDown, middleHardwoodTreePatchCutDown,
		savannahCutDown, anglersCutDown;

	DetailedQuestStep eastHardwoodProtect, westHardwoodProtect, middleHardwoodProtect, savannahProtect,
		anglersProtect;

	// Farming Items
	ItemRequirement coins, spade, rake, allTreeSaplings, treeSapling, allFruitSaplings, fruitTreeSapling,
		allHardwoodSaplings, hardwoodSapling, calquatSapling, allCalquatSaplings,
		compost, axe,	protectionItemTree, allProtectionItemTree, protectionItemCalquat, allProtectionItemCalquat,
		protectionItemFruitTree, allProtectionItemFruitTree, protectionItemHardwood, allProtectionItemHardwood;


	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape,
		gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland,
		accessToSavannah, accessToVarlamore, accessToAnglersRetreat, accessToGreatConch, accessToCalquatFarming;

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
		options = new TreeRunOptions(configManager);
		compostTracker = new TreeRunCompostTracker(client, farmingWorld, configManager);
		if (travelCatalog.isEmpty())
		{
			for (ItemRequirement item : options.itemCatalog())
			{
				ManualRequirement visible = new ManualRequirement();
				item = item.showConditioned(visible);
				travelCatalog.put(item.getId(), item);
				travelVisibility.put(item.getId(), visible);
			}
		}
		patchChoices.clear();
		originalStepText.clear();
		previousStates.clear();
		replantedThisRun.clear();
		nearbyChoices.clear();
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

		farmingGuildFruitStep = (ConditionalStep) new ConditionalStep(this, farmingGuildFruitTreePatchCheckHealth).withId(-2);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsUnchecked()), farmingGuildFruitTreePatchCheckHealth);
		farmingGuildFruitStep.addStep(and(accessToFarmingGuildFruitTreePatch, farmingGuildFruitStates.getIsHarvestable(), not(payingForRemoval)), farmingGuildFruitTreePatchCutDown);
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
		strongholdFruitStep.addStep(and(gnomeStrongholdFruitStates.getIsHarvestable(), not(payingForRemoval)), gnomeStrongholdFruitTreePatchCutDown);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsEmpty(), gnomeStrongholdFruitTreePatchPlant);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsHarvestable(), gnomeStrongholdFruitTreePatchClear);
		strongholdFruitStep.addStep(gnomeStrongholdFruitStates.getIsStump(), gnomeStrongholdFruitTreePatchDig);
		strongholdFruitStep.addStep(nor(usingCompostorNothing, gnomeStrongholdFruitStates.getIsProtected()), strongholdFruitProtect);
		strongholdStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		strongholdStep.addStep(not(gnomeStrongholdFruitStates.getIsGrowing()), strongholdFruitStep);
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
		villageStep.addStep(and(gnomeVillageStates.getIsHarvestable(), not(payingForRemoval)), gnomeVillageFruitTreePatchCutDown);
		villageStep.addStep(gnomeVillageStates.getIsEmpty(), gnomeVillageFruitTreePatchPlant);
		villageStep.addStep(gnomeVillageStates.getIsHarvestable(), gnomeVillageFruitTreePatchClear);
		villageStep.addStep(gnomeVillageStates.getIsStump(), gnomeVillageFruitTreePatchDig);
		villageStep.addStep(nor(usingCompostorNothing, gnomeVillageStates.getIsProtected()), villageFruitProtect);
		steps.addStep(not(gnomeVillageStates.getIsGrowing()), villageStep.withId(6));

		catherbyStep = new ConditionalStep(this, catherbyFruitTreePatchCheckHealth);
		catherbyStep.addStep(catherbyStates.getIsUnchecked(), catherbyFruitTreePatchCheckHealth);
		catherbyStep.addStep(and(catherbyStates.getIsHarvestable(), not(payingForRemoval)), catherbyFruitTreePatchCutDown);
		catherbyStep.addStep(catherbyStates.getIsEmpty(), catherbyFruitTreePatchPlant);
		catherbyStep.addStep(catherbyStates.getIsHarvestable(), catherbyFruitTreePatchClear);
		catherbyStep.addStep(catherbyStates.getIsStump(), catherbyFruitTreePatchDig);
		catherbyStep.addStep(nor(usingCompostorNothing, catherbyStates.getIsProtected()), catherbyFruitProtect);
		steps.addStep(not(catherbyStates.getIsGrowing()), catherbyStep.withId(7));

		brimhavenStep = (ConditionalStep) new ConditionalStep(this, brimhavenFruitTreePatchCheckHealth).withId(81);
		brimhavenStep.addStep(brimhavenStates.getIsUnchecked(), brimhavenFruitTreePatchCheckHealth);
		brimhavenStep.addStep(and(brimhavenStates.getIsHarvestable(), not(payingForRemoval)), brimhavenFruitTreePatchCutDown);
		brimhavenStep.addStep(brimhavenStates.getIsEmpty(), brimhavenFruitTreePatchPlant);
		brimhavenStep.addStep(brimhavenStates.getIsHarvestable(), brimhavenFruitTreePatchClear);
		brimhavenStep.addStep(brimhavenStates.getIsStump(), brimhavenFruitTreePatchDig);
		brimhavenStep.addStep(nor(usingCompostorNothing, brimhavenStates.getIsProtected()), brimhavenFruitProtect);
		karamjaStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		karamjaStep.addStep(not(brimhavenStates.getIsGrowing()), brimhavenStep);

		taiBwoWannaiStep = (ConditionalStep) new ConditionalStep(this, taiBwoWannaiCalquatPatchCheckHealth).withId(82);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsUnchecked(), taiBwoWannaiCalquatPatchCheckHealth);
		taiBwoWannaiStep.addStep(and(taiBwoWannaiStates.getIsHarvestable(), not(payingForRemoval)), taiBwoWannaiCalquatPatchRemove);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsEmpty(), taiBwoWannaiCalquatPatchPlant);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsHarvestable(), taiBwoWannaiCalquatPatchClear);
		taiBwoWannaiStep.addStep(taiBwoWannaiStates.getIsStump(), taiBwoWannaiCalquatPatchDig);
		taiBwoWannaiStep.addStep(nor(usingCompostorNothing, taiBwoWannaiStates.getIsProtected()),
			taiBwoWannaiCalquatProtect);
		karamjaStep.addStep(not(taiBwoWannaiStates.getIsGrowing()), taiBwoWannaiStep);
		steps.addStep(nand(brimhavenStates.getIsGrowing(), taiBwoWannaiStates.getIsGrowing()), karamjaStep.withId(8));

		lletyaStep = new ConditionalStep(this, lletyaFruitTreePatchCheckHealth);
		lletyaStep.addStep(lletyaStates.getIsUnchecked(), lletyaFruitTreePatchCheckHealth);
		lletyaStep.addStep(and(lletyaStates.getIsHarvestable(), not(payingForRemoval)), lletyaFruitTreePatchCutDown);
		lletyaStep.addStep(lletyaStates.getIsEmpty(), lletyaFruitTreePatchPlant);
		lletyaStep.addStep(lletyaStates.getIsHarvestable(), lletyaFruitTreePatchClear);
		lletyaStep.addStep(lletyaStates.getIsStump(), lletyaFruitTreePatchDig);
		lletyaStep.addStep(nor(usingCompostorNothing, lletyaStates.getIsProtected()), lletyaFruitProtect);
		steps.addStep(and(accessToLletya, not(lletyaStates.getIsGrowing())), lletyaStep.withId(9));

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
		steps.addStep(and(accessToFossilIsland, nand(eastHardwoodStates.getIsGrowing(), middleHardwoodStates.getIsGrowing(), westHardwoodStates.getIsGrowing())), fossilIslandStep.withId(10));

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

		kastoriFruitStep = (ConditionalStep) new ConditionalStep(this, kastoriFruitTreePatchCheckHealth).withId(131);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsUnchecked(), kastoriFruitTreePatchCheckHealth);
		kastoriFruitStep.addStep(and(kastoriFruitStates.getIsHarvestable(), not(payingForRemoval)), kastoriFruitTreePatchCutDown);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsEmpty(), kastoriFruitTreePatchPlant);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsHarvestable(), kastoriFruitTreePatchClear);
		kastoriFruitStep.addStep(kastoriFruitStates.getIsStump(), kastoriFruitTreePatchDig);
		kastoriFruitStep.addStep(nor(usingCompostorNothing, kastoriFruitStates.getIsProtected()), kastoriFruitProtect);

		kastoriCalquatStep = (ConditionalStep) new ConditionalStep(this, kastoriCalquatPatchCheckHealth).withId(132);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsUnchecked(), kastoriCalquatPatchCheckHealth);
		kastoriCalquatStep.addStep(and(kastoriCalquatStates.getIsHarvestable(), not(payingForRemoval)), kastoriCalquatPatchRemove);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsEmpty(), kastoriCalquatPatchPlant);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsHarvestable(), kastoriCalquatPatchClear);
		kastoriCalquatStep.addStep(kastoriCalquatStates.getIsStump(), kastoriCalquatPatchDig);
		kastoriCalquatStep.addStep(nor(usingCompostorNothing, kastoriCalquatStates.getIsProtected()), kastoriCalquatProtect);


		kastoriStep = new ReorderableConditionalStep(this, new DetailedQuestStep(this, "Unreachable."));
		kastoriStep.addStep(not(kastoriFruitStates.getIsGrowing()), kastoriFruitStep);
		kastoriStep.addStep(not(kastoriCalquatStates.getIsGrowing()), kastoriCalquatStep);
		steps.addStep(and(accessToVarlamore, nand(kastoriFruitStates.getIsGrowing(), kastoriCalquatStates.getIsGrowing())), kastoriStep.withId(13));

		anglersRetreatStep = new ConditionalStep(this, anglersCheckHealth);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsUnchecked(), anglersCheckHealth);
		anglersRetreatStep.addStep(and(anglersRetreatStates.getIsHarvestable(), not(payingForRemoval)), anglersCutDown);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsEmpty(), anglersPlant);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsHarvestable(), anglersClear);
		anglersRetreatStep.addStep(anglersRetreatStates.getIsStump(), anglersDig);
		anglersRetreatStep.addStep(nor(usingCompostorNothing, anglersRetreatStates.getIsProtected()), anglersProtect);
		steps.addStep(and(accessToAnglersRetreat, not(anglersRetreatStates.getIsGrowing())), anglersRetreatStep.withId(14));

		greatConchStep = new ConditionalStep(this, greatConchCalquatPatchCheckHealth);
		greatConchStep.addStep(greatConchStates.getIsUnchecked(), greatConchCalquatPatchCheckHealth);
		greatConchStep.addStep(and(greatConchStates.getIsHarvestable(), not(payingForRemoval)), greatConchCalquatPatchRemove);
		greatConchStep.addStep(greatConchStates.getIsEmpty(), greatConchCalquatPatchPlant);
		greatConchStep.addStep(greatConchStates.getIsHarvestable(), greatConchCalquatPatchClear);
		greatConchStep.addStep(greatConchStates.getIsStump(), greatConchCalquatPatchDig);
		greatConchStep.addStep(nor(usingCompostorNothing, greatConchStates.getIsProtected()), greatConchCalquatProtect);
		steps.addStep(and(accessToGreatConch, not(greatConchStates.getIsGrowing())), greatConchStep.withId(15));


		registerPatch(farmingGuildTreeStates, Category.REGULAR, Destination.GUILD, farmingGuildTreeStep, farmingGuildStep, farmingGuildTreePatchPlant, farmingGuildTreePatchCheckHealth, farmingGuildTreePatchClear, farmingGuildTreePatchCutDown, farmingGuildTreePatchDig, farmingGuildTreePayForProtection);
		registerPatch(farmingGuildFruitStates, Category.FRUIT, Destination.GUILD, farmingGuildFruitStep, farmingGuildStep, farmingGuildFruitTreePatchPlant, farmingGuildFruitTreePatchCheckHealth, farmingGuildFruitTreePatchClear, farmingGuildFruitTreePatchCutDown, farmingGuildFruitTreePatchDig, guildFruitProtect);
		registerPatch(lumbridgeStates, Category.REGULAR, Destination.LUMBRIDGE, lumbridgeStep, null, lumbridgeTreePatchPlant, lumbridgeTreePatchCheckHealth, lumbridgeTreePatchClear, lumbridgeTreePatchCutDown, lumbridgeTreePatchDig, lumbridgeTreeProtect);
		registerPatch(faladorStates, Category.REGULAR, Destination.FALADOR, faladorStep, null, faladorTreePatchPlant, faladorTreePatchCheckHealth, faladorTreePatchClear, faladorTreePatchCutDown, faladorTreePatchDig, faladorTreeProtect);
		registerPatch(taverleyStates, Category.REGULAR, Destination.TAVERLEY, taverleyStep, null, taverleyTreePatchPlant, taverleyTreePatchCheckHealth, taverleyTreePatchClear, taverleyTreePatchCutDown, taverleyTreePatchDig, taverleyTreeProtect);
		registerPatch(varrockStates, Category.REGULAR, Destination.VARROCK, varrockStep, null, varrockTreePatchPlant, varrockTreePatchCheckHealth, varrockTreePatchClear, varrockTreePatchCutDown, varrockTreePatchDig, varrockTreeProtect);
		registerPatch(gnomeStrongholdTreeStates, Category.REGULAR, Destination.STRONGHOLD, strongholdTreeStep, strongholdStep, gnomeStrongholdTreePatchPlant, gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchClear, gnomeStrongholdTreePatchCutDown, gnomeStrongholdTreePatchDig, strongholdTreeProtect);
		registerPatch(gnomeStrongholdFruitStates, Category.FRUIT, Destination.STRONGHOLD, strongholdFruitStep, strongholdStep, gnomeStrongholdFruitTreePatchPlant, gnomeStrongholdFruitTreePatchCheckHealth, gnomeStrongholdFruitTreePatchClear, gnomeStrongholdFruitTreePatchCutDown, gnomeStrongholdFruitTreePatchDig, strongholdFruitProtect);
		registerPatch(gnomeVillageStates, Category.FRUIT, Destination.VILLAGE, villageStep, null, gnomeVillageFruitTreePatchPlant, gnomeVillageFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchClear, gnomeVillageFruitTreePatchCutDown, gnomeVillageFruitTreePatchDig, villageFruitProtect);
		registerPatch(catherbyStates, Category.FRUIT, Destination.CATHERBY, catherbyStep, null, catherbyFruitTreePatchPlant, catherbyFruitTreePatchCheckHealth, catherbyFruitTreePatchClear, catherbyFruitTreePatchCutDown, catherbyFruitTreePatchDig, catherbyFruitProtect);
		registerPatch(brimhavenStates, Category.FRUIT, Destination.BRIMHAVEN, brimhavenStep, karamjaStep, brimhavenFruitTreePatchPlant, brimhavenFruitTreePatchCheckHealth, brimhavenFruitTreePatchClear, brimhavenFruitTreePatchCutDown, brimhavenFruitTreePatchDig, brimhavenFruitProtect);
		registerPatch(taiBwoWannaiStates, Category.CALQUAT, Destination.TAI_BWO_WANNAI, taiBwoWannaiStep, karamjaStep, taiBwoWannaiCalquatPatchPlant, taiBwoWannaiCalquatPatchCheckHealth, taiBwoWannaiCalquatPatchClear, taiBwoWannaiCalquatPatchRemove, taiBwoWannaiCalquatPatchDig, taiBwoWannaiCalquatProtect);
		registerPatch(lletyaStates, Category.FRUIT, Destination.LLETYA, lletyaStep, null, lletyaFruitTreePatchPlant, lletyaFruitTreePatchCheckHealth, lletyaFruitTreePatchClear, lletyaFruitTreePatchCutDown, lletyaFruitTreePatchDig, lletyaFruitProtect);
		registerPatch(eastHardwoodStates, Category.HARDWOOD, Destination.FOSSIL_ISLAND, fossilIslandEastStep, fossilIslandStep, eastHardwoodTreePatchPlant, eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchClear, eastHardwoodTreePatchCutDown, eastHardwoodTreePatchDig, eastHardwoodProtect);
		registerPatch(middleHardwoodStates, Category.HARDWOOD, Destination.FOSSIL_ISLAND, fossilIslandMiddleStep, fossilIslandStep, middleHardwoodTreePatchPlant, middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchClear, middleHardwoodTreePatchCutDown, middleHardwoodTreePatchDig, middleHardwoodProtect);
		registerPatch(westHardwoodStates, Category.HARDWOOD, Destination.FOSSIL_ISLAND, fossilIslandWestStep, fossilIslandStep, westHardwoodTreePatchPlant, westHardwoodTreePatchCheckHealth, westHardwoodTreePatchClear, westHardwoodTreePatchCutDown, westHardwoodTreePatchDig, westHardwoodProtect);
		registerPatch(savannahStates, Category.HARDWOOD, Destination.SAVANNAH, savannahStep, null, savannahPlant, savannahCheckHealth, savannahClear, savannahCutDown, savannahDig, savannahProtect);
		registerPatch(auburnvaleStates, Category.REGULAR, Destination.NEMUS, auburnvaleStep, null, auburnvaleTreePatchPlant, auburnvaleTreePatchCheckHealth, auburnvaleTreePatchClear, auburnvaleTreePatchCutDown, auburnvaleTreePatchDig, auburnvaleTreeProtect);
		registerPatch(kastoriFruitStates, Category.FRUIT, Destination.KASTORI, kastoriFruitStep, kastoriStep, kastoriFruitTreePatchPlant, kastoriFruitTreePatchCheckHealth, kastoriFruitTreePatchClear, kastoriFruitTreePatchCutDown, kastoriFruitTreePatchDig, kastoriFruitProtect);
		registerPatch(kastoriCalquatStates, Category.CALQUAT, Destination.KASTORI, kastoriCalquatStep, kastoriStep, kastoriCalquatPatchPlant, kastoriCalquatPatchCheckHealth, kastoriCalquatPatchClear, kastoriCalquatPatchRemove, kastoriCalquatPatchDig, kastoriCalquatProtect);
		registerPatch(anglersRetreatStates, Category.HARDWOOD, Destination.ANGLERS, anglersRetreatStep, null, anglersPlant, anglersCheckHealth, anglersClear, anglersCutDown, anglersDig, anglersProtect);
		registerPatch(greatConchStates, Category.CALQUAT, Destination.GREAT_CONCH, greatConchStep, null, greatConchCalquatPatchPlant, greatConchCalquatPatchCheckHealth, greatConchCalquatPatchClear, greatConchCalquatPatchRemove, greatConchCalquatPatchDig, greatConchCalquatProtect);
		refreshRunOptions();
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
		// Calquat planting requires Farming 72.
		accessToCalquatFarming = new SkillRequirement(Skill.FARMING, 72, false);

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
		taiBwoWannaiStates = new PatchStates("Tai Bwo Wannai", accessToCalquatFarming);
		gnomeVillageStates = new PatchStates("Tree Gnome Village");
		gnomeStrongholdFruitStates = new PatchStates("Gnome Stronghold");
		lletyaStates = new PatchStates("Lletya", accessToLletya);
		farmingGuildFruitStates = new PatchStates("Farming Guild", accessToFarmingGuildFruitTreePatch);
		kastoriFruitStates = new PatchStates("Kastori", accessToVarlamore);
		kastoriCalquatStates = new PatchStates("Kastori", and(accessToVarlamore, accessToCalquatFarming));
		greatConchStates = new PatchStates("Great Conch", and(accessToGreatConch, accessToCalquatFarming));

		westHardwoodStates = new PatchStates("Fossil Island", "West", accessToFossilIsland);
		middleHardwoodStates = new PatchStates("Fossil Island", "Middle", accessToFossilIsland);
		eastHardwoodStates = new PatchStates("Fossil Island", "East", accessToFossilIsland);
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

		// Enum dropdowns display their first choice for an unset/invalid value. Match it,
		// rather than RuneliteRequirement's boolean "false" default (which hid Pay).
		normalizeEnumSetting(PAY_OR_CUT, PayOrCut.class, PayOrCut.PAY);
		normalizeEnumSetting(PAY_OR_COMPOST, PayOrCompost.class, PayOrCompost.PAY);
		payingForRemoval = enumRequirement(PAY_OR_CUT, PayOrCut.PAY.name());
		payingForProtection = enumRequirement(PAY_OR_COMPOST, PayOrCompost.PAY.name());
		usingCompostorNothing = or(new RuneliteRequirement(configManager, PAY_OR_COMPOST, PayOrCompost.COMPOST.name()),
			new RuneliteRequirement(configManager, PAY_OR_COMPOST, PayOrCompost.NEITHER.name()));
	}

	private <T extends Enum<T>> void normalizeEnumSetting(String key, Class<T> type, T fallback)
	{
		String saved = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key);
		try { Enum.valueOf(type, saved == null ? "" : saved); }
		catch (IllegalArgumentException ignored)
		{
			if (configManager.getRSProfileKey() != null)
				configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key, fallback.name());
		}
	}

	private RuneliteRequirement enumRequirement(String key, String expected)
	{
		return new RuneliteRequirement(configManager, key, "PAY", expected, expected, Collections.emptyMap());
	}

	@Override
	public void setupRequirements()
	{
		setupConditions();
		// Farming Item Requirements
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.showConditioned(and(showRake, not(new VarbitRequirement(VarbitID.FARMING_BLOCKWEEDS, 2))));
		coins = new ItemRequirement("Coins for tree removal and travel", ItemID.COINS)
			.showConditioned(showCoins);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed().showConditioned(not(payingForRemoval));

		TreeSapling treeSaplingEnum = (TreeSapling) FarmingUtils.getEnumFromConfig(configManager, TreeSapling.MAGIC);
		if (treeSaplingEnum == TreeSapling.NONE) treeSaplingEnum = TreeSapling.OAK;
		treeSapling = treeSaplingEnum.getPlantableItemRequirement(itemManager);
		treeSapling.setHighlightInInventory(true);
		allTreeSaplings = treeSapling.copy();

		protectionItemTree = treeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemTree.addAlternates(protectionItemTree.getId() + 1);
		allProtectionItemTree = protectionItemTree.copy();

		FruitTreeSapling fruitTreeSaplingEnum = (FruitTreeSapling) FarmingUtils.getEnumFromConfig(configManager, FruitTreeSapling.APPLE);
		if (fruitTreeSaplingEnum == FruitTreeSapling.NONE) fruitTreeSaplingEnum = FruitTreeSapling.APPLE;
		fruitTreeSapling = fruitTreeSaplingEnum.getPlantableItemRequirement(itemManager);
		fruitTreeSapling.setHighlightInInventory(true);
		allFruitSaplings = fruitTreeSapling.copy();

		protectionItemFruitTree = fruitTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemFruitTree.addAlternates(protectionItemFruitTree.getId() + 1);
		allProtectionItemFruitTree = protectionItemFruitTree.copy();

		HardwoodTreeSapling hardwoodTreeSaplingEnum = (HardwoodTreeSapling) FarmingUtils.getEnumFromConfig(configManager, HardwoodTreeSapling.TEAK);
		if (hardwoodTreeSaplingEnum == HardwoodTreeSapling.NONE) hardwoodTreeSaplingEnum = HardwoodTreeSapling.TEAK;
		hardwoodSapling = hardwoodTreeSaplingEnum.getPlantableItemRequirement(itemManager);
		hardwoodSapling.setHighlightInInventory(true);
		allHardwoodSaplings = hardwoodSapling.copy();

		protectionItemHardwood = hardwoodTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemHardwood.addAlternates(protectionItemHardwood.getId() + 1);
		allProtectionItemHardwood = protectionItemHardwood.copy();

		CalquatTreeSapling calquatTreeSaplingEnum = (CalquatTreeSapling) FarmingUtils.getEnumFromConfig(configManager,
			CalquatTreeSapling.CALQUAT);
		if (calquatTreeSaplingEnum == CalquatTreeSapling.NONE) calquatTreeSaplingEnum = CalquatTreeSapling.CALQUAT;
		calquatSapling = calquatTreeSaplingEnum.getPlantableItemRequirement(itemManager);
		calquatSapling.setHighlightInInventory(true);
		allCalquatSaplings = calquatSapling.copy();

		protectionItemCalquat = calquatTreeSaplingEnum.getProtectionItemRequirement(itemManager).showConditioned(payingForProtection);
		protectionItemCalquat.addAlternates(protectionItemCalquat.getId() + 1);
		allProtectionItemCalquat = protectionItemCalquat.copy();

		allTreeSaplings = visibleWhenNeeded(allTreeSaplings, false);
		allFruitSaplings = visibleWhenNeeded(allFruitSaplings, false);
		allHardwoodSaplings = visibleWhenNeeded(allHardwoodSaplings, false);
		allCalquatSaplings = visibleWhenNeeded(allCalquatSaplings, false);
		allProtectionItemTree = visibleWhenNeeded(allProtectionItemTree, true);
		allProtectionItemFruitTree = visibleWhenNeeded(allProtectionItemFruitTree, true);
		allProtectionItemHardwood = visibleWhenNeeded(allProtectionItemHardwood, true);
		allProtectionItemCalquat = visibleWhenNeeded(allProtectionItemCalquat, true);

		compost = new ItemRequirement("Ultracompost", ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED).showConditioned(showCompost);
		compost.setDisplayMatchedItemName(true);

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
		lumbridgeTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		lumbridgeTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		faladorTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_2, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to clear the patch.");
		faladorTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		faladorTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		taverleyTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to clear the patch.");
		taverleyTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		taverleyTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		varrockTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_3_02, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to clear the patch.");
		varrockTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		varrockTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		gnomeStrongholdTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_GNOME, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to clear the patch.");
		gnomeStrongholdTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		gnomeStrongholdTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		farmingGuildTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T2, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to clear the patch.");
		farmingGuildTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		farmingGuildTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		auburnvaleTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to clear the patch.");
		auburnvaleTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		auburnvaleTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		lumbridgeTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_4, new WorldPoint(3193, 3231, 0),
			"Speak to Fayeth to protect the patch.");
		lumbridgeTreeProtect.conditionToHideInSidebar(not(payingForProtection));
		lumbridgeTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		faladorTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_2, new WorldPoint(3004, 3373, 0),
			"Speak to Heskel to protect the patch.");
		faladorTreeProtect.conditionToHideInSidebar(not(payingForProtection));
		faladorTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		taverleyTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_1, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to protect the patch.");
		taverleyTreeProtect.conditionToHideInSidebar(not(payingForProtection));
		taverleyTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		varrockTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_3_02, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to protect the patch.");
		varrockTreeProtect.conditionToHideInSidebar(not(payingForProtection));
		varrockTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		strongholdTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_GNOME, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to protect the patch.");
		strongholdTreeProtect.conditionToHideInSidebar(not(payingForProtection));
		strongholdTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		farmingGuildTreePayForProtection = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T2, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to protect the patch.");
		farmingGuildTreePayForProtection.conditionToHideInSidebar(not(payingForProtection));
		farmingGuildTreePayForProtection.addDialogSteps(TREE_PROTECTION_DIALOG);

		auburnvaleTreeProtect = new NpcStep(this, NpcID.FARMING_GARDENER_TREE_7, new WorldPoint(1367, 3322, 0),
			"Speak to Aub to protect the patch.");
		auburnvaleTreeProtect.conditionToHideInSidebar(not(payingForProtection));
		auburnvaleTreeProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		// Tree Patch Steps
		lumbridgeTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Check the health of the tree planted in Lumbridge.");


		faladorTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Check the health of the tree planted in Falador.");

		taverleyTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Check the health of the tree planted in Taverley.");
		varrockTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Check the health of the tree planted in Varrock.");

		gnomeStrongholdTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Check the health of the tree planted in the Tree Gnome Stronghold.");
		farmingGuildTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Check the health of the tree planted in the Farming Guild.");
		farmingGuildTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));


		auburnvaleTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Check the health of the tree planted at Auburnvale");
		auburnvaleTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));


		// Tree Cut Down Steps
		farmingGuildTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Cut down the tree planted in the Farming Guild.", axe);
		farmingGuildTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch), payingForRemoval));


		lumbridgeTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_4, new WorldPoint(3193, 3231, 0),
			"Cut down the tree planted in Lumbridge.", axe);
		lumbridgeTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);



		faladorTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Cut down the tree planted in Falador.", axe);
		faladorTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);


		taverleyTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Cut down the tree planted in Taverley.", axe);
		taverleyTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);

		varrockTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Cut down the tree planted in Varrock.", axe);
		varrockTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);


		gnomeStrongholdTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Cut down the tree planted in the Tree Gnome Stronghold.", axe);
		gnomeStrongholdTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);

		auburnvaleTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Cut down the tree planted at Auburnvale.", axe);
		auburnvaleTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToVarlamore), payingForRemoval));


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
		lumbridgeTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		faladorTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_2, new WorldPoint(3004, 3373, 0),
			"Dig up the tree stump in Falador.");
		faladorTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		taverleyTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_1, new WorldPoint(2936, 3438, 0),
			"Dig up the tree stump in Taverley.");
		taverleyTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		varrockTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_3, new WorldPoint(3229, 3459, 0),
			"Dig up the tree stump in Varrock.");
		varrockTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		gnomeStrongholdTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_5, new WorldPoint(2436, 3415, 0),
			"Dig up the tree stump in the Tree Gnome Stronghold.");
		gnomeStrongholdTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		farmingGuildTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_6, new WorldPoint(1232, 3736, 0),
			"Dig up the tree stump in the Farming Guild tree patch.");
		farmingGuildTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		auburnvaleTreePatchDig = new ObjectStep(this, ObjectID.FARMING_TREE_PATCH_7, new WorldPoint(1367, 3322, 0),
			"Dig up the tree stump in the Auburnvale tree patch.");
		auburnvaleTreePatchDig.conditionToHideInSidebar(payingForRemoval);

		faladorTreePatchClear.addSubSteps(faladorTreePatchDig);
		taverleyTreePatchClear.addSubSteps(taverleyTreePatchDig);
		varrockTreePatchClear.addSubSteps(varrockTreePatchDig);
		gnomeStrongholdTreePatchClear.addSubSteps(gnomeStrongholdTreePatchDig);
		lumbridgeTreePatchClear.addSubSteps(lumbridgeTreePatchDig);
		farmingGuildTreePatchClear.addSubSteps(farmingGuildTreePatchDig);
		auburnvaleTreePatchClear.addSubSteps(auburnvaleTreePatchDig);

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


		catherbyFruitTreePatchCheckHealth.addSubSteps(catherbyFruitTreePatchPlant);

		lletyaFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));

		lletyaFruitTreePatchCheckHealth.addSubSteps(lletyaFruitTreePatchPlant);

		farmingGuildFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));

		farmingGuildFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Farming Guild", true);
		farmingGuildFruitTreePatchCheckHealth.addSubSteps(farmingGuildFruitTreePatchPlant);

		kastoriFruitTreePatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Check the health of the fruit tree planted in Kastori.");
		kastoriFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));

		kastoriFruitTreePatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);
		kastoriFruitTreePatchCheckHealth.addSubSteps(kastoriFruitTreePatchPlant);

		taiBwoWannaiCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Check the health of the calquat tree planted in Tai Bwo Wannai.", calquatSapling);
		taiBwoWannaiCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Tai Bwo Wannai", true);
		taiBwoWannaiCalquatPatchCheckHealth.addSubSteps(taiBwoWannaiCalquatPatchPlant);

		kastoriCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Check the health of the calquat tree planted in Kastori.");
		kastoriCalquatPatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));

		kastoriCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);
		kastoriCalquatPatchCheckHealth.addSubSteps(kastoriCalquatPatchPlant);

		greatConchCalquatPatchCheckHealth = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Check the health of the calquat tree planted in Great Conch.", calquatSapling);
		greatConchCalquatPatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToGreatConch));
		greatConchCalquatPatchCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Great Conch", true);
		greatConchCalquatPatchCheckHealth.addSubSteps(greatConchCalquatPatchPlant);

		// Fruit Tree Cut Down Steps
		gnomeStrongholdFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Cut down the fruit tree planted in the Tree Gnome Stronghold.", axe);
		gnomeStrongholdFruitTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);
		gnomeStrongholdFruitTreePatchCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Gnome Stronghold", true);

		gnomeVillageFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Cut down the fruit tree planted outside the Tree Gnome Village.", axe);
		gnomeVillageFruitTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);
		gnomeVillageFruitTreePatchCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Tree Gnome Village", true);

		brimhavenFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Cut down the fruit tree planted in Brimhaven.", axe);
		brimhavenFruitTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);
		brimhavenFruitTreePatchCutDown.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchCutDown.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchCutDown.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));

		catherbyFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Cut down the fruit tree planted in Catherby.", axe);
		catherbyFruitTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);



		lletyaFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Cut down the fruit tree planted in Lletya.", axe);
		lletyaFruitTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToLletya), payingForRemoval));


		farmingGuildFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Cut down the fruit tree planted in the Farming Guild.", axe);
		farmingGuildFruitTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch), payingForRemoval));

		farmingGuildFruitTreePatchCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Farming Guild", true);

		kastoriFruitTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Cut down the fruit tree planted in Kastori.", axe);
		kastoriFruitTreePatchCutDown.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToVarlamore), payingForRemoval));

		kastoriFruitTreePatchCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);

		taiBwoWannaiCalquatPatchRemove = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Pick the fruit off the calquat and clear the patch in Tai Bwo Wannai.");
		taiBwoWannaiCalquatPatchRemove.conditionToHideInSidebar(payingForRemoval);
		taiBwoWannaiCalquatPatchRemove.addWidgetHighlightWithTextRequirement(187, 3, "Tai Bwo Wannai", true);

		kastoriCalquatPatchRemove = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Pick the fruit off the calquat and clear the patch in Kastori.");
		kastoriCalquatPatchRemove.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToVarlamore), payingForRemoval));

		kastoriCalquatPatchRemove.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);

		greatConchCalquatPatchRemove = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Pick the fruit off the calquat and clear the patch in Great Conch.");
		greatConchCalquatPatchRemove.conditionToHideInSidebar(new Conditions(LogicType.OR, new Conditions(LogicType.NOR, accessToGreatConch), payingForRemoval));
		greatConchCalquatPatchRemove.addWidgetHighlightWithTextRequirement(187, 3, "Great Conch", true);

		// Clear
		gnomeStrongholdFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_1, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeStrongholdFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		gnomeStrongholdFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		gnomeVillageFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_2, new WorldPoint(2490, 3180, 0),
			"Pay Gileth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeVillageFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		gnomeVillageFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		brimhavenFruitTreePatchClear = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		brimhavenFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		brimhavenFruitTreePatchClear.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchClear.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenFruitTreePatchClear.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));
		brimhavenFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		catherbyFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_4, new WorldPoint(2860, 3433, 0),
			"Pay Ellena 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		catherbyFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		catherbyFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		lletyaFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_TREE_5, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		lletyaFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		lletyaFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		farmingGuildFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T3, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		farmingGuildFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		farmingGuildFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		kastoriFruitTreePatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_7, new WorldPoint(1350, 3057, 0),
			"Pay Ehecatl 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		kastoriFruitTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		kastoriFruitTreePatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		taiBwoWannaiCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT, new WorldPoint(2795, 3102, 0),
			"Pay Imiago 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		taiBwoWannaiCalquatPatchClear.conditionToHideInSidebar(not(payingForRemoval));
		taiBwoWannaiCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		kastoriCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_2, new WorldPoint(1366, 3033, 0),
			"Pay Tziuhtla 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		kastoriCalquatPatchClear.conditionToHideInSidebar(not(payingForRemoval));
		kastoriCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		greatConchCalquatPatchClear = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_3, new WorldPoint(3129, 2406, 0),
			"Pay Guppa 200 coins to clear the calquat tree, or pick all the fruit and cut it down.");
		greatConchCalquatPatchClear.conditionToHideInSidebar(not(payingForRemoval));
		greatConchCalquatPatchClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");

		strongholdFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_1, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo to protect the patch.");
		strongholdFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		strongholdFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		villageFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_2, new WorldPoint(2490, 3180, 0),
			"Pay Gileth to protect the patch.");
		villageFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		villageFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		brimhavenFruitProtect = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth to protect the patch.");
		brimhavenFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		brimhavenFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		catherbyFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_4, new WorldPoint(2860, 3433, 0),
			"Pay Ellena to protect the patch.");
		catherbyFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		catherbyFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		lletyaFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_TREE_5, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen to protect the patch.");
		lletyaFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		lletyaFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		guildFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FARMGUILD_T3, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie to protect the patch.");
		guildFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		guildFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		kastoriFruitProtect = new NpcStep(this, NpcID.FARMING_GARDENER_FRUIT_7, new WorldPoint(1350, 3057, 0),
			"Pay Ehecatl to protect the patch.");
		kastoriFruitProtect.conditionToHideInSidebar(not(payingForProtection));
		kastoriFruitProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		taiBwoWannaiCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT, new WorldPoint(2795, 3102, 0),
			"Pay Imiago to protect the patch.");
		taiBwoWannaiCalquatProtect.conditionToHideInSidebar(not(payingForProtection));
		taiBwoWannaiCalquatProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		kastoriCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_2, new WorldPoint(1366, 3033, 0),
			"Pay Tziuhtla to protect the patch.");
		kastoriCalquatProtect.conditionToHideInSidebar(not(payingForProtection));
		kastoriCalquatProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		greatConchCalquatProtect = new NpcStep(this, NpcID.FARMING_GARDENER_CALQUAT_3, new WorldPoint(3129, 2406, 0),
			"Pay Guppa to protect the patch.");
		greatConchCalquatProtect.conditionToHideInSidebar(not(payingForProtection));
		greatConchCalquatProtect.addDialogSteps(TREE_PROTECTION_DIALOG);

		// Dig Fruit Tree Steps
		gnomeStrongholdFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Dig up the fruit tree's stump in the Tree Gnome Stronghold.");
		gnomeStrongholdFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		gnomeVillageFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Dig up the fruit tree's stump outside the Tree Gnome Village.");
		gnomeVillageFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		brimhavenFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Dig up the fruit tree's stump in Brimhaven.");
		brimhavenFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		catherbyFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby");
		catherbyFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		lletyaFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Dig up the fruit tree's stump in Lletya.");
		lletyaFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		farmingGuildFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Dig up the fruit tree's stump in the Farming Guild.");
		farmingGuildFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		kastoriFruitTreePatchDig = new ObjectStep(this, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Dig up the fruit tree's stump in Kastori.");
		kastoriFruitTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		taiBwoWannaiCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH, new WorldPoint(2795, 3102, 0),
			"Dig up the calquat tree's stump in Tai Bwo Wannai.");
		taiBwoWannaiCalquatPatchDig.conditionToHideInSidebar(payingForRemoval);
		kastoriCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_2, new WorldPoint(1366, 3033, 0),
			"Dig up the calquat tree's stump in Kastori.");
		kastoriCalquatPatchDig.conditionToHideInSidebar(payingForRemoval);
		greatConchCalquatPatchDig = new ObjectStep(this, ObjectID.FARMING_CALQUAT_TREE_PATCH_3, new WorldPoint(3129, 2406, 0),
			"Dig up the calquat tree's stump in Great Conch.");
		greatConchCalquatPatchDig.conditionToHideInSidebar(payingForRemoval);

		gnomeStrongholdFruitTreePatchClear.addSubSteps(gnomeStrongholdFruitTreePatchDig);
		gnomeVillageFruitTreePatchClear.addSubSteps(gnomeVillageFruitTreePatchDig);
		brimhavenFruitTreePatchClear.addSubSteps(brimhavenFruitTreePatchDig);
		catherbyFruitTreePatchClear.addSubSteps(catherbyFruitTreePatchDig);
		lletyaFruitTreePatchClear.addSubSteps(lletyaFruitTreePatchDig);
		farmingGuildFruitTreePatchClear.addSubSteps(farmingGuildFruitTreePatchDig);
		kastoriFruitTreePatchClear.addSubSteps(kastoriFruitTreePatchDig);
		taiBwoWannaiCalquatPatchClear.addSubSteps(taiBwoWannaiCalquatPatchDig);
		kastoriCalquatPatchClear.addSubSteps(kastoriCalquatPatchDig);
		greatConchCalquatPatchClear.addSubSteps(greatConchCalquatPatchDig);

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

		// Hardwood Tree Cut Down Steps
		eastHardwoodTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Cut down the hardwood tree planted on the eastern hardwood tree patch on Fossil Island.", axe);
		eastHardwoodTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);

		middleHardwoodTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Cut down the hardwood tree planted on the centre hardwood tree patch on Fossil Island.", axe);
		middleHardwoodTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);

		westHardwoodTreePatchCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Cut down the hardwood tree planted on the western hardwood tree patch on Fossil Island.", axe);
		westHardwoodTreePatchCutDown.conditionToHideInSidebar(payingForRemoval);

		savannahCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Cut down the hardwood tree planted in the Avium Savannah.", axe);
		savannahCutDown.conditionToHideInSidebar(payingForRemoval);

		anglersCutDown = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470, 2704, 0),
			"Cut down the hardwood tree planted in the Anglers' Retreat.", axe);
		anglersCutDown.conditionToHideInSidebar(payingForRemoval);

		westHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to remove the west tree.");
		westHardwoodTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		westHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");
		middleHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to remove the middle tree.");
		middleHardwoodTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		middleHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");
		eastHardwoodTreePatchClear = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to remove the east tree.");
		eastHardwoodTreePatchClear.conditionToHideInSidebar(not(payingForRemoval));
		eastHardwoodTreePatchClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
				"Coins - chop my tree down please.", "Yes.");

		savannahClear = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to clear the tree.");
		savannahClear.conditionToHideInSidebar(not(payingForRemoval));
		savannahClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
			"Coins - chop my tree down please.", "Yes.");

		anglersClear = new NpcStep(this, NpcID.FARMING_GARDENER_HARDWOOD_TREE_5, new WorldPoint(2470, 2704, 0),
			"Pay Argo to clear the tree.");
		anglersClear.conditionToHideInSidebar(not(payingForRemoval));
		anglersClear.addDialogSteps("Would you chop my tree down for me?", "I can't be bothered - I'd rather pay you to do it.", "Here's 200 " +
			"Coins - chop my tree down please.", "Yes.");

		westHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_3, new WorldPoint(3702, 3837, 0),
			"Dig up the western hardwood tree's stump on Fossil Island.");
		westHardwoodTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		middleHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_2, new WorldPoint(3708, 3833, 0),
			"Dig up the centre hardwood tree's stump on Fossil Island.");
		middleHardwoodTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		eastHardwoodTreePatchDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_1, new WorldPoint(3715, 3835, 0),
			"Dig up the eastern hardwood tree's stump on Fossil Island.");
		eastHardwoodTreePatchDig.conditionToHideInSidebar(payingForRemoval);
		savannahDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_4, new WorldPoint(1687, 2972, 0),
			"Dig up the Savannah hardwood tree's stump.");
		savannahDig.conditionToHideInSidebar(payingForRemoval);
		anglersDig = new ObjectStep(this, ObjectID.FARMING_HARDWOOD_TREE_PATCH_5, new WorldPoint(2470,2704, 0),
			"Dig up the Anglers' Retreat hardwood tree's stump.");
		anglersDig.conditionToHideInSidebar(payingForRemoval);

		westHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER3, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to protect the west tree.");
		westHardwoodProtect.conditionToHideInSidebar(not(payingForProtection));
		westHardwoodProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		middleHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER2, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to protect the middle tree.");
		middleHardwoodProtect.conditionToHideInSidebar(not(payingForProtection));
		middleHardwoodProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		eastHardwoodProtect = new NpcStep(this, NpcID.FOSSIL_SQUIRREL_GARDENER1, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to protect the east tree.");
		eastHardwoodProtect.conditionToHideInSidebar(not(payingForProtection));
		eastHardwoodProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		savannahProtect = new NpcStep(this, NpcID.FROG_QUEST_MARCELLUS_FARMER, new WorldPoint(1687, 2972, 0),
			"Pay Marcellus to protect the hardwood tree.");
		savannahProtect.conditionToHideInSidebar(not(payingForProtection));
		savannahProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
		anglersProtect = new NpcStep(this, NpcID.FARMING_GARDENER_HARDWOOD_TREE_5, new WorldPoint(2470, 2704, 0),
			"Pay Argo to protect the hardwood tree.");
		anglersProtect.conditionToHideInSidebar(not(payingForProtection));
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
		if (farmingHandler == null) return;
		refreshSupplies();
		Map<Destination, String> displayedKeys = TreeRunNexusMenu.rememberedHotkeys(client, configManager);
		Set<PatchChoice> nearby = new HashSet<>();
		for (PatchChoice choice : patchChoices.values()) if (isNearDestination(choice.destination)) nearby.add(choice);
		boolean canUseRocks = canUseRocks();
		String routeSignature = options.routeSignature();
		if (!routeSignature.equals(lastRouteSignature) || !displayedKeys.equals(nexusKeys) || !nearby.equals(nearbyChoices) || canUseRocks != rocksAvailable)
		{
			lastRouteSignature = routeSignature;
			rocksAvailable = canUseRocks;
			nexusKeys.clear();
			nexusKeys.putAll(displayedKeys);
			nearbyChoices.clear();
			nearbyChoices.addAll(nearby);
			refreshRunOptions();
		}
	}

	private void refreshSupplies()
	{
		coins.setQuantity(0);
		compostNeeded = 0;
		Set<Destination> neededDestinations = new LinkedHashSet<>();
		handleTreePatches(PatchImplementation.TREE, farmingWorld.getTabs().get(Tab.TREE), allTreeSaplings, allProtectionItemTree, protectionItemTree, neededDestinations);
		handleTreePatches(PatchImplementation.FRUIT_TREE, farmingWorld.getTabs().get(Tab.FRUIT_TREE), allFruitSaplings, allProtectionItemFruitTree, protectionItemFruitTree, neededDestinations);
		handleTreePatches(PatchImplementation.CALQUAT, farmingWorld.getTabs().get(Tab.FRUIT_TREE), allCalquatSaplings, allProtectionItemCalquat, protectionItemCalquat, neededDestinations);
		handleTreePatches(PatchImplementation.HARDWOOD_TREE, farmingWorld.getTabs().get(Tab.TREE), allHardwoodSaplings, allProtectionItemHardwood, protectionItemHardwood, neededDestinations);
		Set<Destination> arrived = new HashSet<>();
		for (Destination destination : Destination.values()) if (isNearDestination(destination)) arrived.add(destination);
		neededDestinations.removeAll(arrived);
		selectedTravelItems = options.travelItems(neededDestinations, arrived);
		int fares = selectedTravelItems.stream().filter(i -> i.getId() == ItemID.COINS).mapToInt(ItemRequirement::getQuantity).sum();
		selectedTravelItems.removeIf(i -> i.getId() == ItemID.COINS);
		coins.setQuantity(coins.getQuantity() + fares);
		showCoins.setShouldPass(coins.getQuantity() > 0);
		showRake.setShouldPass(options.bringRake());
		for (Map.Entry<ItemRequirement, ManualRequirement> entry : quantityVisibility.entrySet())
			entry.getValue().setShouldPass(entry.getKey().getQuantity() > 0);
		travelVisibility.values().forEach(visible -> visible.setShouldPass(false));
		for (ItemRequirement selected : selectedTravelItems)
		{
			ItemRequirement item = travelCatalog.get(selected.getId());
			if (item != null)
			{
				item.setName(selected.getName());
				item.setQuantity(selected.getQuantity());
				travelVisibility.get(item.getId()).setShouldPass(true);
			}
		}
		updateCompostItem();
		StringBuilder signature = new StringBuilder();
		List<ItemRequirement> plannedItems = new ArrayList<>(getItemRequirements());
		plannedItems.addAll(getItemRecommended());
		for (ItemRequirement item : plannedItems)
			if (item.shouldDisplayText(client)) signature.append(item.getId()).append(':').append(item.getQuantity()).append(';');
		String currentSignature = signature.toString();
		if (!currentSignature.equals(lastSupplySignature))
		{
			lastSupplySignature = currentSignature;
			questHelperPlugin.refreshBank();
		}
	}

	private void handleTreePatches(PatchImplementation implementation, Set<FarmingPatch> patches,
		ItemRequirement allSaplings, ItemRequirement allPayment, ItemRequirement singlePayment, Set<Destination> neededDestinations)
	{
		int saplings = 0;
		int payments = 0;
		for (FarmingPatch patch : patches)
		{
			if (patch.getImplementation() != implementation) continue;
			PatchStates region = patchChoices.keySet().stream()
				.filter(r -> r.getRegionName().equals(patch.getRegion().getName()))
				.filter(r -> r.getPatchName() == null || r.getPatchName().equals(patch.getName()))
				.filter(r -> patchChoices.get(r).category == category(implementation))
				.findFirst().orElse(null);
			if (region == null) continue;
			PatchChoice choice = patchChoices.get(region);
			CropState state = farmingHandler.predictPatch(patch);
			CropState previous = previousStates.put(patch, state);
			if (state == CropState.EMPTY && previous != null && previous != CropState.EMPTY)
			{
				compostTracker.setCompostState(patch, null);
				replantedThisRun.remove(patch);
			}
			if (previous == CropState.EMPTY && state == CropState.GROWING) replantedThisRun.add(patch);
			boolean growing = state == CropState.GROWING;
			boolean protectedTree = paymentTracker.getProtectedState(patch);
			boolean enabled = categoryEnabled(choice.category) && region.canAccess(client)
				&& !choice.route.isLocked() && (choice.parent == null || !choice.parent.isLocked());
			boolean needsProtection = enabled && growing && !protectedTree && payingForProtection.check(client);
			boolean needsCompost = enabled && growing && isComposting() && replantedThisRun.contains(patch)
				&& !hasSelectedCompost(patch);
			region.getIsHarvestable().setShouldPass(state == CropState.HARVESTABLE);
			region.getIsEmpty().setShouldPass(state == CropState.EMPTY || state == CropState.DEAD);
			region.getIsUnchecked().setShouldPass(state == CropState.UNCHECKED);
			region.getIsStump().setShouldPass(state == CropState.STUMP);
			region.getIsProtected().setShouldPass(protectedTree);
			region.getIsGrowing().setShouldPass(!enabled || (growing && !needsProtection && !needsCompost));
			choice.needsCompost.setShouldPass(needsCompost);
			if (!enabled) continue;
			if (!growing)
			{
				saplings++;
				// Compost on the old mature tree does not cover its replacement.
				if (isComposting()) compostNeeded++;
				if (payingForRemoval.check(client) && (state == CropState.UNCHECKED || state == CropState.HARVESTABLE || state == null))
					coins.setQuantity(coins.getQuantity() + 200);
			}
			else if (needsCompost) compostNeeded++;
			if ((!growing || needsProtection) && payingForProtection.check(client)) payments++;
			if (!growing || needsProtection || needsCompost) neededDestinations.add(choice.destination);
		}
		allSaplings.setQuantity(saplings);
		allPayment.setQuantity(singlePayment.getQuantity() * payments);
	}

	// Respect both the existing category skip and the tree-type NONE option from #2817.
	private String saplingConfigKey(Category category)
	{
		switch (category)
		{
			case REGULAR: return TREE_SAPLING;
			case FRUIT: return FRUIT_TREE_SAPLING;
			case HARDWOOD: return HARDWOOD_TREE_SAPLING;
			case CALQUAT: return CALQUAT_TREE_SAPLING;
			default: throw new IllegalArgumentException("Unknown tree category: " + category);
		}
	}

	private boolean categoryEnabled(Category category)
	{
		return options.includes(category) && !"NONE".equals(
			configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, saplingConfigKey(category)));
	}

	private Requirement categoryHidden(Category category)
	{
		return or(new RuneliteRequirement(configManager, category.key(), TreeRunOptions.Inclusion.SKIP.name()),
			new RuneliteRequirement(configManager, saplingConfigKey(category), "NONE"));
	}

	private Category category(PatchImplementation implementation)
	{
		switch (implementation)
		{
			case TREE: return Category.REGULAR;
			case FRUIT_TREE: return Category.FRUIT;
			case HARDWOOD_TREE: return Category.HARDWOOD;
			case CALQUAT: return Category.CALQUAT;
			default: throw new IllegalArgumentException("Not a tree patch: " + implementation);
		}
	}

	private boolean isComposting()
	{
		return options.compostType() != CompostType.NONE &&
			PayOrCompost.COMPOST.name().equals(configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, PAY_OR_COMPOST));
	}

	private boolean hasSelectedCompost(FarmingPatch patch)
	{
		CompostState state = compostTracker.getCompostState(patch);
		CompostState requested = options.compostType().state;
		return state != null && requested != null && state.ordinal() >= requested.ordinal();
	}

	private ItemRequirement visibleWhenNeeded(ItemRequirement item, boolean protection)
	{
		ManualRequirement visible = new ManualRequirement();
		ItemRequirement conditioned = item.showConditioned(protection ? and(payingForProtection, visible) : visible);
		quantityVisibility.put(conditioned, visible);
		return conditioned;
	}

	private void updateCompostItem()
	{
		CompostType type = options.compostType();
		CompostSource source = options.compostSource();
		if (type == CompostType.NONE)
		{
			showCompost.setShouldPass(false);
			return;
		}
		showCompost.setShouldPass(isComposting() && compostNeeded > 0 && source != CompostSource.LEPRECHAUN_STORAGE);
		compost.setId(source == CompostSource.BOTTOMLESS_BUCKET ? ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED : type.itemId);
		compost.setName(source == CompostSource.BOTTOMLESS_BUCKET ? "Bottomless bucket filled with " + type.name().toLowerCase() : type.name().toLowerCase());
		compost.setQuantity(source == CompostSource.INDIVIDUAL_BUCKETS ? compostNeeded : 1);
	}

	private void registerPatch(PatchStates states, Category category, Destination destination,
		ConditionalStep route, ConditionalStep parent, DetailedQuestStep plant, DetailedQuestStep... otherSteps)
	{
		DetailedQuestStep compostStep = new DetailedQuestStep(this, plant.getDefinedPoint(), "Apply compost to the newly planted tree.", compost);
		List<DetailedQuestStep> instructions = new ArrayList<>(Arrays.asList(otherSteps));
		instructions.add(plant);
		PatchChoice choice = new PatchChoice(category, destination, route, parent, compostStep, instructions);
		route.addStep(choice.needsCompost, compostStep);
		plant.addSubSteps(compostStep);
		for (DetailedQuestStep instruction : instructions)
			originalStepText.put(instruction, new ArrayList<>(instruction.getText()));
		patchChoices.put(states, choice);
	}

	private boolean isNearDestination(Destination destination)
	{
		if (client.getLocalPlayer() == null) return false;
		WorldPoint player = client.getLocalPlayer().getWorldLocation();
		if (player == null) return false;
		return patchChoices.values().stream().filter(choice -> choice.destination == destination)
			.anyMatch(choice -> {
				WorldPoint patch = choice.compostStep.getDefinedPoint().getWorldPoint();
				if (patch.distanceTo(player) <= 64) return true;
				// Include the spirit-tree arrival and the walk between both Stronghold patches.
				if (destination == Destination.STRONGHOLD && player.getPlane() == 0
					&& player.getX() >= 2420 && player.getX() <= 2500
					&& player.getY() >= 3400 && player.getY() <= 3470) return true;
				// CKR is farther than 64 tiles from the calquat. Include the walk between
				// the ring and patch so arriving at CKR does not suggest teleporting again.
				return destination == Destination.TAI_BWO_WANNAI
					&& options.travel(destination) == TreeRunOptions.Travel.POH_FAIRY_RING
					&& player.getPlane() == 0 && player.getX() >= Math.min(2801, patch.getX()) - 12
					&& player.getX() <= Math.max(2801, patch.getX()) + 12
					&& player.getY() >= Math.min(3003, patch.getY()) - 12
					&& player.getY() <= Math.max(3003, patch.getY()) + 12;
			});
	}

	private boolean canUseRocks()
	{
		return Math.max(client.getRealSkillLevel(Skill.AGILITY), client.getBoostedSkillLevel(Skill.AGILITY)) >= 68;
	}

	private Map<String, Object> walkingPathConfig()
	{
		Map<String, Object> config = new HashMap<>();
		config.put("useTeleportationItems", "None");
		for (String key : Arrays.asList("useTeleportationSpells", "useTeleportationSpellsHome",
			"useTeleportationMinigames", "useTeleportationPortals", "useTeleportationLevers",
			"usePoh", "useSpiritTrees", "useFairyRings", "useWildernessObelisks")) config.put(key, false);
		config.put("useAgilityShortcuts", true);
		return config;
	}

	private void refreshRunOptions()
	{
		for (PatchChoice choice : patchChoices.values())
		{
			boolean local = isNearDestination(choice.destination);
			boolean rocks = options.travel(choice.destination) == TreeRunOptions.Travel.WALK_FROM_TAVERLEY_ROCKS;
			boolean canUseRocks = canUseRocks();
			boolean walkingFromTaverley = rocks && canUseRocks && isNearDestination(Destination.TAVERLEY);
			boolean guideFromNemus = options.travel(choice.destination) == TreeRunOptions.Travel.MOUNTAIN_GUIDE_QUETZAL
				&& isNearDestination(Destination.NEMUS);
			List<ItemRequirement> travelItems = local || walkingFromTaverley || guideFromNemus ? Collections.emptyList()
				: options.travelItems(Collections.singleton(choice.destination));
			for (DetailedQuestStep instruction : choice.instructions)
			{
				List<String> text = new ArrayList<>(originalStepText.get(instruction));
				if (local) text.add("Already in " + choice.destination.label + ": walk to this patch; no teleport needed.");
				else if (guideFromNemus) text.add("Follow the mountain guide, then use the nearby quetzal to Kastori and walk to the farming patches. Unlock the quetzal destination first; no house teleport needed.");
				else if (walkingFromTaverley) text.add("Run south-west to the White Wolf Mountain rocks; climb down west to the Catherby fruit tree (68 Agility). No teleport needed.");
				else if (rocks && !canUseRocks) text.add("This rocks route requires 68 Agility. Choose Direct teleport or POH nexus instead.");
				else text.add(options.instruction(choice.destination));
				if (!local && options.travel(choice.destination) == TreeRunOptions.Travel.POH_NEXUS && nexusKeys.containsKey(choice.destination))
					text.add("Portal nexus: press " + nexusKeys.get(choice.destination) + " for " + choice.destination.label + " (last seen; reopen the menu after reordering).");
				if (isComposting()) text.add("After planting, apply " + options.compostType().name().toLowerCase() + ".");
				instruction.setText(text);
				instruction.getTeleport().clear();
				instruction.getTeleport().addAll(travelItems);
				// Selected arrival transport is described above. Let Shortest Path draw only
				// the local walk, so its independent POH choices cannot contradict that route.
				instruction.configureShortestPath(local || walkingFromTaverley, walkingPathConfig());
			}
			choice.compostStep.configureShortestPath(local, walkingPathConfig());
			choice.compostStep.setText((options.compostSource() == CompostSource.LEPRECHAUN_STORAGE
				? "Withdraw " + options.compostType().name().toLowerCase() + " from the nearby tool leprechaun, then apply it to this tree."
				: "Apply " + options.compostType().name().toLowerCase() + " to this newly planted tree.")
				+ " Inspect the patch if it was already treated.");
		}
		refreshSupplies();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (compostTracker != null) compostTracker.onMenuOptionClicked(event);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (compostTracker != null) compostTracker.onChatMessage(event);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (compostTracker != null) compostTracker.onGameStateChanged(event);
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

			if (event.getKey().startsWith(TreeRunOptions.PREFIX) || event.getKey().equals(PAY_OR_COMPOST)
				|| event.getKey().equals(TREE_SAPLING) || event.getKey().equals(FRUIT_TREE_SAPLING)
				|| event.getKey().equals(HARDWOOD_TREE_SAPLING) || event.getKey().equals(CALQUAT_TREE_SAPLING))
			{
				refreshRunOptions();
				questHelperPlugin.refreshBank();
			}
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
		List<HelperConfig> configs = new ArrayList<>(Arrays.asList(treesConfig, fruitTreesConfig, hardwoodTreesConfig, calquatTreesConfig, outfitConfig, payOrCutConfig, payOrCompostConfig));
		configs.addAll(new TreeRunOptions(configManager).configs());
		return configs;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return visibleItems(Arrays.asList(spade, rake, coins, axe, allTreeSaplings, allFruitSaplings, allHardwoodSaplings,
			allCalquatSaplings, allProtectionItemTree, allProtectionItemFruitTree, allProtectionItemHardwood, allProtectionItemCalquat));
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		List<ItemRequirement> recommended = new ArrayList<>(Arrays.asList(compost, gracefulOutfit, farmersOutfit));
		recommended.addAll(travelCatalog.values());
		return visibleItems(recommended);
	}

	private List<ItemRequirement> visibleItems(List<ItemRequirement> items)
	{
		return items.stream().filter(item -> item.shouldDisplayText(client))
			.collect(java.util.stream.Collectors.toList());
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
		farmingGuildTreePanel.setHideCondition(or(categoryHidden(Category.REGULAR), not(accessToFarmingGuildTreePatch)));
		PanelDetails farmingGuildFruitPanel = new PanelDetails("Fruit Tree Patch",
			Arrays.asList(farmingGuildFruitTreePatchCheckHealth, farmingGuildFruitTreePatchCutDown, farmingGuildFruitTreePatchDig, farmingGuildFruitTreePatchClear, farmingGuildFruitTreePatchPlant, guildFruitProtect)).withId(-2);
		farmingGuildFruitPanel.setLockingStep((farmingGuildFruitStep));
		farmingGuildFruitPanel.setHideCondition(or(categoryHidden(Category.FRUIT), not(accessToFarmingGuildFruitTreePatch)));
		var farmingGuildPanel = new TopLevelPanelDetails("Farming Guild",
			farmingGuildTreePanel, farmingGuildFruitPanel).withId(0);
		farmingGuildPanel.setLockingStep(farmingGuildStep);
		farmingGuildPanel.setHideCondition(and(or(categoryHidden(Category.REGULAR), not(accessToFarmingGuildTreePatch)), or(categoryHidden(Category.FRUIT), not(accessToFarmingGuildFruitTreePatch))));

		PanelDetails lumbridgePanel = new PanelDetails("Lumbridge", Arrays.asList(lumbridgeTreePatchCheckHealth, lumbridgeTreePatchCutDown, lumbridgeTreePatchDig, lumbridgeTreePatchClear, lumbridgeTreePatchPlant, lumbridgeTreeProtect)).withId(1);
		lumbridgePanel.setLockingStep(lumbridgeStep);
		lumbridgePanel.setHideCondition(categoryHidden(Category.REGULAR));

		PanelDetails faladorPanel = new PanelDetails("Falador", Arrays.asList(faladorTreePatchCheckHealth, faladorTreePatchCutDown, faladorTreePatchDig, faladorTreePatchClear, faladorTreePatchPlant, faladorTreeProtect)).withId(2);
		faladorPanel.setLockingStep(faladorStep);
		faladorPanel.setHideCondition(categoryHidden(Category.REGULAR));

		PanelDetails taverleyPanel = new PanelDetails("Taverley", Arrays.asList(taverleyTreePatchCheckHealth, taverleyTreePatchCutDown, taverleyTreePatchDig, taverleyTreePatchClear, taverleyTreePatchPlant, taverleyTreeProtect)).withId(3);
		taverleyPanel.setLockingStep(taverleyStep);
		taverleyPanel.setHideCondition(categoryHidden(Category.REGULAR));

		PanelDetails varrockPanel = new PanelDetails("Varrock", Arrays.asList(varrockTreePatchCheckHealth, varrockTreePatchCutDown, varrockTreePatchDig, varrockTreePatchClear, varrockTreePatchPlant, varrockTreeProtect)).withId(4);
		varrockPanel.setLockingStep(varrockStep);
		varrockPanel.setHideCondition(categoryHidden(Category.REGULAR));

		PanelDetails gnomeStrongholdFruitPanel = new PanelDetails("Fruit Tree Patch",
			Arrays.asList(gnomeStrongholdFruitTreePatchCheckHealth, gnomeStrongholdFruitTreePatchCutDown, gnomeStrongholdFruitTreePatchDig, gnomeStrongholdFruitTreePatchClear, gnomeStrongholdFruitTreePatchPlant, strongholdFruitProtect)).withId(51);
		gnomeStrongholdFruitPanel.setLockingStep(strongholdFruitStep);
		gnomeStrongholdFruitPanel.setHideCondition(categoryHidden(Category.FRUIT));
		PanelDetails gnomeStrongholdTreePanel = new PanelDetails("Tree Patch",
			Arrays.asList(gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchCutDown, gnomeStrongholdTreePatchDig, gnomeStrongholdTreePatchClear, gnomeStrongholdTreePatchPlant, strongholdTreeProtect)).withId(52);
		gnomeStrongholdTreePanel.setLockingStep(strongholdTreeStep);
		gnomeStrongholdTreePanel.setHideCondition(categoryHidden(Category.REGULAR));
		var gnomeStrongholdPanel = new TopLevelPanelDetails("Gnome Stronghold",
			gnomeStrongholdFruitPanel, gnomeStrongholdTreePanel).withId(5);
		gnomeStrongholdPanel.setLockingStep(strongholdStep);
		gnomeStrongholdPanel.setHideCondition(and(categoryHidden(Category.FRUIT), categoryHidden(Category.REGULAR)));

		PanelDetails villagePanel = new PanelDetails("Tree Gnome Village", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth,
				gnomeVillageFruitTreePatchCutDown, gnomeVillageFruitTreePatchDig, gnomeVillageFruitTreePatchClear, gnomeVillageFruitTreePatchPlant, villageFruitProtect)).withId(6);
		villagePanel.setLockingStep(villageStep);
		villagePanel.setHideCondition(categoryHidden(Category.FRUIT));

		PanelDetails catherbyPanel = new PanelDetails("Catherby", Arrays.asList(catherbyFruitTreePatchCheckHealth, catherbyFruitTreePatchCutDown, catherbyFruitTreePatchDig, catherbyFruitTreePatchClear, catherbyFruitTreePatchPlant, catherbyFruitProtect)).withId(7);
		catherbyPanel.setLockingStep(catherbyStep);
		catherbyPanel.setHideCondition(categoryHidden(Category.FRUIT));

		PanelDetails brimhavenPanel = new PanelDetails("Brimhaven", Arrays.asList(brimhavenFruitTreePatchCheckHealth, brimhavenFruitTreePatchCutDown, brimhavenFruitTreePatchDig, brimhavenFruitTreePatchClear, brimhavenFruitTreePatchPlant, brimhavenFruitProtect)).withId(81);
		brimhavenPanel.setLockingStep(brimhavenStep);
		brimhavenPanel.setHideCondition(categoryHidden(Category.FRUIT));
		PanelDetails taiBwoWannaiPanel = new PanelDetails("Tai Bwo Wannai", Arrays.asList(taiBwoWannaiCalquatPatchCheckHealth, taiBwoWannaiCalquatPatchRemove, taiBwoWannaiCalquatPatchDig, taiBwoWannaiCalquatPatchClear, taiBwoWannaiCalquatPatchPlant, taiBwoWannaiCalquatProtect)).withId(82);
		taiBwoWannaiPanel.setLockingStep(taiBwoWannaiStep);
		taiBwoWannaiPanel.setHideCondition(or(categoryHidden(Category.CALQUAT), not(accessToCalquatFarming)));
		var karamjaPanel = new TopLevelPanelDetails("Karamja", brimhavenPanel, taiBwoWannaiPanel).withId(8);
		karamjaPanel.setLockingStep(karamjaStep);
		karamjaPanel.setHideCondition(and(categoryHidden(Category.FRUIT), or(categoryHidden(Category.CALQUAT), not(accessToCalquatFarming))));

		PanelDetails lletyaPanel = new PanelDetails("Lletya", Arrays.asList(lletyaFruitTreePatchCheckHealth, lletyaFruitTreePatchCutDown, lletyaFruitTreePatchDig, lletyaFruitTreePatchClear, lletyaFruitTreePatchPlant, lletyaFruitProtect)).withId(9);
		lletyaPanel.setLockingStep(lletyaStep);
		lletyaPanel.setHideCondition(or(categoryHidden(Category.FRUIT), not(accessToLletya)));

		PanelDetails fossilIslandEastPanel = new PanelDetails("East Hardwood Patch",
			Arrays.asList(eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchCutDown, eastHardwoodTreePatchDig, eastHardwoodTreePatchClear, eastHardwoodTreePatchPlant, eastHardwoodProtect)
		).withId(101);
		fossilIslandEastPanel.setLockingStep(fossilIslandEastStep);
		PanelDetails fossilIslandMiddlePanel = new PanelDetails("Middle Hardwood Patch",
			Arrays.asList(middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchCutDown, middleHardwoodTreePatchDig, middleHardwoodTreePatchClear, middleHardwoodTreePatchPlant, middleHardwoodProtect)
		).withId(102);
		fossilIslandMiddlePanel.setLockingStep(fossilIslandMiddleStep);
		PanelDetails fossilIslandWestPanel = new PanelDetails("West Hardwood Patch",
			Arrays.asList(westHardwoodTreePatchCheckHealth, westHardwoodTreePatchCutDown, westHardwoodTreePatchDig, westHardwoodTreePatchClear, westHardwoodTreePatchPlant, westHardwoodProtect)
		).withId(103);
		fossilIslandWestPanel.setLockingStep(fossilIslandWestStep);
		var fossilIslandPanel = new TopLevelPanelDetails("Fossil Island",
			fossilIslandEastPanel, fossilIslandMiddlePanel, fossilIslandWestPanel).withId(10);
		fossilIslandPanel.setLockingStep(fossilIslandStep);
		fossilIslandPanel.setHideCondition(or(categoryHidden(Category.HARDWOOD), not(accessToFossilIsland)));

		PanelDetails savannahPanel = new PanelDetails("Avium Savannah", Arrays.asList(savannahCheckHealth, savannahCutDown, savannahDig, savannahClear, savannahPlant, savannahProtect)).withId(11);
		savannahPanel.setLockingStep(savannahStep);
		savannahPanel.setHideCondition(or(categoryHidden(Category.HARDWOOD), not(accessToSavannah)));

		PanelDetails auburnvalePanel = new PanelDetails("Auburnvale", Arrays.asList(auburnvaleTreePatchCheckHealth, auburnvaleTreePatchCutDown, auburnvaleTreePatchDig, auburnvaleTreePatchClear, auburnvaleTreePatchPlant, auburnvaleTreeProtect)).withId(12);
		auburnvalePanel.setLockingStep(auburnvaleStep);
		auburnvalePanel.setHideCondition(or(categoryHidden(Category.REGULAR), not(accessToVarlamore)));

		PanelDetails kastoriFruitPanel = new PanelDetails("Fruit Tree Patch", Arrays.asList(kastoriFruitTreePatchCheckHealth, kastoriFruitTreePatchCutDown, kastoriFruitTreePatchDig, kastoriFruitTreePatchClear, kastoriFruitTreePatchPlant, kastoriFruitProtect)).withId(131);
		kastoriFruitPanel.setLockingStep(kastoriFruitStep);
		kastoriFruitPanel.setHideCondition(or(categoryHidden(Category.FRUIT), not(accessToVarlamore)));
		PanelDetails kastoriCalquatPanel = new PanelDetails("Calquat Patch", Arrays.asList(kastoriCalquatPatchCheckHealth, kastoriCalquatPatchRemove, kastoriCalquatPatchDig, kastoriCalquatPatchClear, kastoriCalquatPatchPlant, kastoriCalquatProtect)).withId(132);
		kastoriCalquatPanel.setLockingStep(kastoriCalquatStep);
		kastoriCalquatPanel.setHideCondition(or(categoryHidden(Category.CALQUAT), not(accessToCalquatFarming), not(accessToVarlamore)));
		var kastoriPanel = new TopLevelPanelDetails("Kastori", kastoriFruitPanel, kastoriCalquatPanel).withId(13);
		kastoriPanel.setLockingStep(kastoriStep);
		kastoriPanel.setHideCondition(or(not(accessToVarlamore), and(categoryHidden(Category.FRUIT), or(categoryHidden(Category.CALQUAT), not(accessToCalquatFarming)))));

		PanelDetails anglersPanel = new PanelDetails("Anglers' Retreat", Arrays.asList(anglersCheckHealth,
			anglersCutDown, anglersDig, anglersClear, anglersPlant, anglersProtect)).withId(14);
		anglersPanel.setLockingStep(anglersRetreatStep);
		anglersPanel.setHideCondition(or(categoryHidden(Category.HARDWOOD), not(accessToAnglersRetreat)));

		PanelDetails greatConchPanel = new PanelDetails("Great Conch",
			Arrays.asList(greatConchCalquatPatchCheckHealth, greatConchCalquatPatchRemove, greatConchCalquatPatchDig, greatConchCalquatPatchClear,
				greatConchCalquatPatchPlant, greatConchCalquatProtect)).withId(15);
		greatConchPanel.setLockingStep(greatConchStep);
		greatConchPanel.setHideCondition(or(categoryHidden(Category.CALQUAT), not(accessToCalquatFarming), not(accessToGreatConch)));

		var farmRunSidebar = new TopLevelPanelDetails("Tree Run", farmingGuildPanel, lumbridgePanel, faladorPanel, taverleyPanel,
			varrockPanel, gnomeStrongholdPanel, villagePanel, catherbyPanel, karamjaPanel, lletyaPanel, fossilIslandPanel, savannahPanel, auburnvalePanel,
			kastoriPanel, anglersPanel, greatConchPanel);
		farmRunSidebar.setHideCondition(and(categoryHidden(Category.REGULAR), categoryHidden(Category.FRUIT), categoryHidden(Category.HARDWOOD), categoryHidden(Category.CALQUAT)));
		allSteps.add(farmRunSidebar);

		return allSteps;
	}

	private void updateTreeSapling(TreeSapling selectedTreeSapling)
	{
		// NONE disables the category; never look up an invalid item ID 0.
		if (selectedTreeSapling == TreeSapling.NONE) selectedTreeSapling = TreeSapling.OAK;
		treeSapling.setId(selectedTreeSapling.treeSaplingID);
		treeSapling.setName(itemManager.getItemComposition(selectedTreeSapling.getPlantableItemId()).getName());

		allTreeSaplings.setId(selectedTreeSapling.treeSaplingID);
		allTreeSaplings.setName(itemManager.getItemComposition(selectedTreeSapling.getPlantableItemId()).getName());
		updateTreePaymentItem(selectedTreeSapling);
	}

	private void updateFruitTreeSapling(FruitTreeSapling selectedFruitTreeSapling)
	{
		// NONE disables the category; never look up an invalid item ID 0.
		if (selectedFruitTreeSapling == FruitTreeSapling.NONE) selectedFruitTreeSapling = FruitTreeSapling.APPLE;
		fruitTreeSapling.setId(selectedFruitTreeSapling.fruitTreeSaplingId);
		fruitTreeSapling.setName(itemManager.getItemComposition(selectedFruitTreeSapling.getPlantableItemId()).getName());

		allFruitSaplings.setId(selectedFruitTreeSapling.fruitTreeSaplingId);
		allFruitSaplings.setName(itemManager.getItemComposition(selectedFruitTreeSapling.getPlantableItemId()).getName());
		updateFruitTreePaymentItem(selectedFruitTreeSapling);
	}

	private void updateHardwoodTreeSapling(HardwoodTreeSapling selectedHardwoodTreeSapling)
	{
		// NONE disables the category; never look up an invalid item ID 0.
		if (selectedHardwoodTreeSapling == HardwoodTreeSapling.NONE) selectedHardwoodTreeSapling = HardwoodTreeSapling.TEAK;
		hardwoodSapling.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		hardwoodSapling.setName(itemManager.getItemComposition(selectedHardwoodTreeSapling.getPlantableItemId()).getName());

		allHardwoodSaplings.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		allHardwoodSaplings.setName(itemManager.getItemComposition(selectedHardwoodTreeSapling.getPlantableItemId()).getName());
		updateHardwoodTreePaymentItem(selectedHardwoodTreeSapling);
	}

	private void updateCalquatTreeSapling(CalquatTreeSapling selectedCalquatTreeSapling)
	{
		// NONE disables the category; never look up an invalid item ID 0.
		if (selectedCalquatTreeSapling == CalquatTreeSapling.NONE) selectedCalquatTreeSapling = CalquatTreeSapling.CALQUAT;
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
