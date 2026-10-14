package com.questhelper.helpers.mischelpers.farmruns.treeruns;

import com.questhelper.helpers.mischelpers.farmruns.CropState;
import com.questhelper.helpers.mischelpers.farmruns.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmruns.FarmingPatch;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils;
import com.questhelper.helpers.mischelpers.farmruns.FarmingWorld;
import com.questhelper.helpers.mischelpers.farmruns.PatchImplementation;
import com.questhelper.helpers.mischelpers.farmruns.PatchStates;
import com.questhelper.helpers.mischelpers.farmruns.PaymentTracker;
import com.questhelper.helpers.mischelpers.farmruns.TreeRun;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.game.ItemManager;
import java.util.List;
import java.util.Set;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;

public abstract class TreeFactory<T extends FarmingUtils.PlantableItem>
{
	public enum MultiLevelPanel {
		FARMING_GUILD,
		GNOME_STRONGHOLD,
		KARAMJA,
		KASTORI
	}

	protected final TreeRun treeRun;
	protected final TreeRunTeleports teleports;
	protected final TreeRunConfig config;
	protected final TreeRunItems items;
	protected final ItemManager itemManager;

	protected static final String TREE_PROTECTION_DIALOG = "Would you look after my crops for me?";

	protected TreeFactory(TreeRun treeRun, TreeRunTeleports teleports, TreeRunConfig config, TreeRunItems items, ItemManager itemManager)
	{
		this.treeRun = treeRun;
		this.teleports = teleports;
		this.config = config;
		this.items = items;
		this.itemManager = itemManager;

		setupRequirements();
		setupSteps();
		setupConditionalSteps();
	}

	protected abstract void setupRequirements();
	protected abstract void setupSteps();
	protected abstract void setupConditionalSteps();

	public abstract Conditions isAllGrowing();
	public abstract void onGameTick(GameTick event, FarmingWorld farmingWorld, FarmingHandler handler, Client client, PaymentTracker paymentTracker);
	public abstract void updateSapling(T sapling);

	public abstract HelperConfig getConfig();

	protected void handleTreePatches(PaymentTracker paymentTracker, Client client, FarmingHandler farmingHandler, PatchImplementation implementation, List<PatchStates> regions, Set<FarmingPatch> patches, ItemRequirement allSaplings, ItemRequirement allPayment)
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
			boolean needsProtection = !isProtected && config.getPayingForProtection().check(client);

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
		items.getCoins().setQuantity(items.getCoins().getQuantity() + (200 * numberOfSaplings));
		allPayment.setQuantity(allPayment.getQuantity() * numberOfSaplings);
	}

	protected ConditionalStep createTreeConditionalStep(
		PatchStates states,
		DetailedQuestStep checkHealth,
		DetailedQuestStep cutDown,
		DetailedQuestStep clear,
		DetailedQuestStep dig,
		DetailedQuestStep plant,
		DetailedQuestStep protect,
		boolean needsCompost,
		int id
	)
	{
		return createTreeConditionalStep(states, checkHealth, cutDown, clear, dig, plant, protect, null, needsCompost, id);
	}

	protected ConditionalStep createTreeConditionalStep(
		PatchStates states,
		DetailedQuestStep checkHealth,
		DetailedQuestStep cutDown,
		DetailedQuestStep clear,
		DetailedQuestStep dig,
		DetailedQuestStep plant,
		DetailedQuestStep protect,
		Requirement locationAccessRequirement,
		boolean needsCompost,
		int id)
	{
		ConditionalStep step = (ConditionalStep) new ConditionalStep(
			treeRun,
			checkHealth
		).withId(id);

		Requirement unchecked = states.getIsUnchecked();
		Requirement harvestable = states.getIsHarvestable();
		Requirement stump = states.getIsStump();
		Requirement empty = states.getIsEmpty();

		if (locationAccessRequirement != null)
		{
			unchecked = and(locationAccessRequirement, unchecked);
			harvestable = and(locationAccessRequirement, harvestable);
			stump = and(locationAccessRequirement, stump);
			empty = and(locationAccessRequirement, empty);
		}

		step.addStep(unchecked, checkHealth);
		step.addStep(and(harvestable, not(config.getPayingForRemoval())), cutDown);
		step.addStep(harvestable, clear);
		step.addStep(stump, dig);
		step.addStep(empty, plant);

		if (needsCompost)
		{
			Requirement protectRequirement = nor(config.getUsingCompostOrNothing(), states.getIsProtected());

			if (locationAccessRequirement != null)
			{
				protectRequirement = and(locationAccessRequirement, protectRequirement);
			}

			step.addStep(protectRequirement, protect);
		}

		return step;
	}
}
