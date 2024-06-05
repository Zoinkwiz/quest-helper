package com.questhelper.helpers.mischelpers.farmruns;

import com.google.inject.Inject;
import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.FruitTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.GracefulOrFarming;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.HardwoodTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.TreeSapling;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.widget.LunarSpells;
import com.questhelper.steps.widget.NormalSpells;
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
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.util.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
*
* TODO LIST:
*  Add option for protection of items, have as config option.
*  Add steps to highlight farmer for clearing.
*  Config for chop or clear?
*  PRIORITY: Add checked or notchecked checks, varbits used for patch checked status?
* */
public class TreeRun extends ComplexStateQuestHelper
{
	@Inject
	private FarmingWorld farmingWorld;
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

	// Fruit Trees
	DetailedQuestStep farmingGuildFruitTreePatchCheckHealth, gnomeStrongholdFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchCheckHealth,
		brimhavenFruitTreePatchCheckHealth, lletyaFruitTreePatchCheckHealth, catherbyFruitTreePatchCheckHealth;
	DetailedQuestStep farmingGuildFruitTreePatchPlant, gnomeStrongholdFruitTreePatchPlant, gnomeVillageFruitTreePatchPlant,
		brimhavenFruitTreePatchPlant, lletyaFruitTreePatchPlant, catherbyFruitTreePatchPlant;

	DetailedQuestStep farmingGuildFruitTreePatchClear, gnomeStrongholdFruitTreePatchClear, gnomeVillageFruitTreePatchClear,
		brimhavenFruitTreePatchClear, lletyaFruitTreePatchClear, catherbyFruitTreePatchClear;
	DetailedQuestStep farmingGuildFruitTreePatchDig, gnomeStrongholdFruitTreePatchDig, gnomeVillageFruitTreePatchDig,
		brimhavenFruitTreePatchDig, lletyaFruitTreePatchDig, catherbyFruitTreePatchDig;

	// Hardwood Trees
	DetailedQuestStep eastHardwoodTreePatchCheckHealth, westHardwoodTreePatchCheckHealth, middleHardwoodTreePatchCheckHealth;
	DetailedQuestStep eastHardwoodTreePatchPlant, westHardwoodTreePatchPlant, middleHardwoodTreePatchPlant;
	DetailedQuestStep eastHardwoodTreePatchDig, westHardwoodTreePatchDig, middleHardwoodTreePatchDig;

	DetailedQuestStep eastHardwoodTreePatchClear, westHardwoodTreePatchClear, middleHardwoodTreePatchClear;

	// Farming Items
	ItemRequirement coins, spade, rake, allTreeSaplings, treeSapling, allFruitSaplings, fruitTreeSapling, hardwoodSapling, compost, axe, protectionItem;

	// Teleport Items
	// TODO: Add these...
	ItemRequirement farmingGuildTeleport, crystalTeleport, catherbyTeleport, varrockTeleport, lumbridgeTeleport, faladorTeleport, fossilIslandTeleport;

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland;

	// Tree Requirements
	ManualRequirement  lumbridgeTreePatchEmpty, faladorTreePatchEmpty, taverleyTreePatchEmpty,
		varrockTreePatchEmpty, gnomeStrongholdTreePatchEmpty, farmingGuildTreePatchEmpty;
	ManualRequirement lumbridgeTreePatchHarvestable, faladorTreePatchHarvestable, taverleyTreePatchHarvestable,
		varrockTreePatchHarvestable, gnomeStrongholdTreePatchHarvestable, farmingGuildTreePatchHarvestable;
	ManualRequirement faladorTreePatchNotChecked, faladorTreePatchIsStump,
		lumbridgeTreePatchNotChecked, lumbridgeTreePatchIsStump,
		farmingGuildTreePatchNotChecked, farmingGuildTreePatchIsStump,
		taverleyTreePatchNotChecked, taverleyTreePatchIsStump,
		varrockTreePatchNotChecked, varrockTreePatchIsStump,
		gnomeStrongholdTreePatchNotChecked, gnomeStrongholdTreePatchIsStump;

	// Fruit Tree Requirements
	ManualRequirement gnomeStrongholdFruitTreePatchEmpty, gnomeVillageFruitTreePatchEmpty,
		brimhavenFruitTreePatchEmpty, lletyaFruitTreePatchEmpty, catherbyFruitTreePatchEmpty, farmingGuildFruitTreePatchEmpty;
	ManualRequirement gnomeStrongholdFruitTreePatchHarvestable, gnomeVillageFruitTreePatchHarvestable,
		brimhavenFruitTreePatchHarvestable, lletyaFruitTreePatchHarvestable, catherbyFruitTreePatchHarvestable, farmingGuildFruitTreePatchHarvestable;

	ManualRequirement gnomeStrongholdFruitTreePatchNotChecked, gnomeStrongholdFruitTreePatchIsStump,
		gnomeVillageFruitTreePatchNotChecked, gnomeVillageFruitTreePatchIsStump,
		brimhavenFruitTreePatchNotChecked, brimhavenFruitTreePatchIsStump,
		lletyaFruitTreePatchNotChecked, lletyaFruitTreePatchIsStump,
		catherbyFruitTreePatchNotChecked, catherbyFruitTreePatchIsStump,
		farmingGuildFruitTreePatchNotChecked, farmingGuildFruitTreePatchChecked, farmingGuildFruitTreePatchIsStump;

	// Hardwood Tree Requirements
	ManualRequirement eastHardwoodTreePatchEmpty, westHardwoodTreePatchEmpty, middleHardwoodTreePatchEmpty;
	ManualRequirement eastHardwoodTreePatchReady, westHardwoodTreePatchReady, middleHardwoodTreePatchReady;

	ManualRequirement eastHardwoodTreePatchNotChecked, eastHardwoodTreePatchHarvestable, eastHardwoodTreePatchIsStump,
		westHardwoodTreePatchNotChecked, westHardwoodTreePatchHarvestable, westHardwoodTreePatchIsStump,
		middleHardwoodTreePatchNotChecked, middleHardwoodTreePatchHarvestable, middleHardwoodTreePatchIsStump;

	private final String TREE_SAPLING = "treeSaplings";
	private final String FRUIT_TREE_SAPLING = "fruitTreeSaplings";
	private final String HARDWOOD_TREE_SAPLING = "hardwoodTreeSaplings";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	public QuestStep loadStep()
	{
		farmingHandler = new FarmingHandler(client, configManager);
		setupRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep steps = new ConditionalStep(this, waitForTree, spade, coins, rake, compost, treeSapling,
			fruitTreeSapling, hardwoodSapling, farmersOutfit, gracefulOutfit);

		// Farming Guild Tree -> Farming Guild Fruit Tree -> Lumbridge -> Falador -> Taverley
		// Varrock -> Gnome Stronghold Fruit -> Gnome Stronghold Tree -> Gnome Village -> Brimhaven
		// -> catherby -> lletya -> east hardwood -> middle hardwood -> west hardwood.


		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreePatchNotChecked), farmingGuildTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreePatchHarvestable), farmingGuildTreePatchClear);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreePatchIsStump), farmingGuildTreePatchDig);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreePatchEmpty), farmingGuildTreePatchPlant);

		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitTreePatchNotChecked), farmingGuildFruitTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitTreePatchChecked), farmingGuildTreePatchClear);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitTreePatchEmpty), farmingGuildFruitTreePatchPlant);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitTreePatchIsStump), farmingGuildTreePatchDig);

		steps.addStep(lumbridgeTreePatchNotChecked, lumbridgeTreePatchCheckHealth);
		steps.addStep(lumbridgeTreePatchEmpty, lumbridgeTreePatchPlant);
		steps.addStep(lumbridgeTreePatchHarvestable, lumbridgeTreePatchClear);
		steps.addStep(lumbridgeTreePatchIsStump, lumbridgeTreePatchDig);

		steps.addStep(faladorTreePatchNotChecked, faladorTreePatchCheckHealth);
		steps.addStep(faladorTreePatchEmpty, faladorTreePatchPlant);
		steps.addStep(faladorTreePatchHarvestable, faladorTreePatchClear);
		steps.addStep(faladorTreePatchIsStump, faladorTreePatchDig);

		steps.addStep(taverleyTreePatchNotChecked, taverleyTreePatchCheckHealth);
		steps.addStep(taverleyTreePatchEmpty, taverleyTreePatchPlant);
		steps.addStep(taverleyTreePatchHarvestable, taverleyTreePatchClear);
		steps.addStep(taverleyTreePatchIsStump, taverleyTreePatchDig);

		steps.addStep(varrockTreePatchNotChecked, varrockTreePatchCheckHealth);
		steps.addStep(varrockTreePatchEmpty, varrockTreePatchPlant);
		steps.addStep(varrockTreePatchHarvestable, varrockTreePatchClear);
		steps.addStep(varrockTreePatchIsStump, varrockTreePatchDig);

		steps.addStep(gnomeStrongholdFruitTreePatchNotChecked, gnomeStrongholdFruitTreePatchCheckHealth);
		steps.addStep(gnomeStrongholdFruitTreePatchEmpty, gnomeStrongholdFruitTreePatchPlant);
		steps.addStep(gnomeStrongholdFruitTreePatchHarvestable, gnomeStrongholdFruitTreePatchClear);
		steps.addStep(gnomeStrongholdFruitTreePatchIsStump, gnomeStrongholdFruitTreePatchDig);

		steps.addStep(gnomeStrongholdTreePatchNotChecked, gnomeStrongholdTreePatchCheckHealth);
		steps.addStep(gnomeStrongholdTreePatchEmpty, gnomeStrongholdFruitTreePatchPlant);
		steps.addStep(gnomeStrongholdTreePatchHarvestable, gnomeStrongholdTreePatchClear);
		steps.addStep(gnomeStrongholdTreePatchIsStump, gnomeStrongholdTreePatchDig);

		steps.addStep(gnomeVillageFruitTreePatchNotChecked, gnomeVillageFruitTreePatchCheckHealth);
		steps.addStep(gnomeVillageFruitTreePatchEmpty, gnomeVillageFruitTreePatchPlant);
		steps.addStep(gnomeVillageFruitTreePatchHarvestable, gnomeVillageFruitTreePatchClear);
		steps.addStep(gnomeVillageFruitTreePatchIsStump, gnomeVillageFruitTreePatchDig);

		steps.addStep(brimhavenFruitTreePatchNotChecked, brimhavenFruitTreePatchCheckHealth);
		steps.addStep(brimhavenFruitTreePatchEmpty, brimhavenFruitTreePatchPlant);
		steps.addStep(brimhavenFruitTreePatchHarvestable, brimhavenFruitTreePatchClear);
		steps.addStep(brimhavenFruitTreePatchIsStump, brimhavenFruitTreePatchDig);

		steps.addStep(catherbyFruitTreePatchNotChecked, catherbyFruitTreePatchCheckHealth);
		steps.addStep(catherbyFruitTreePatchEmpty, catherbyFruitTreePatchPlant);
		steps.addStep(catherbyFruitTreePatchHarvestable, catherbyFruitTreePatchClear);
		steps.addStep(catherbyFruitTreePatchIsStump, catherbyFruitTreePatchDig);

		steps.addStep(new Conditions(accessToLletya, lletyaFruitTreePatchNotChecked), lletyaFruitTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToLletya, lletyaFruitTreePatchEmpty), lletyaFruitTreePatchPlant);
		steps.addStep(new Conditions(accessToLletya, lletyaFruitTreePatchHarvestable), lletyaFruitTreePatchClear);
		steps.addStep(new Conditions(accessToLletya, lletyaFruitTreePatchIsStump), lletyaFruitTreePatchDig);

		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodTreePatchNotChecked), eastHardwoodTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodTreePatchEmpty), eastHardwoodTreePatchPlant);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodTreePatchHarvestable), eastHardwoodTreePatchClear);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodTreePatchIsStump), eastHardwoodTreePatchDig);

		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodTreePatchNotChecked), middleHardwoodTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodTreePatchEmpty), middleHardwoodTreePatchPlant);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodTreePatchHarvestable), middleHardwoodTreePatchClear);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodTreePatchIsStump), middleHardwoodTreePatchDig);

		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodTreePatchNotChecked), westHardwoodTreePatchCheckHealth);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodTreePatchEmpty), westHardwoodTreePatchPlant);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodTreePatchHarvestable), westHardwoodTreePatchClear);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodTreePatchIsStump), westHardwoodTreePatchDig);

		return steps;
	}

	private void setupConditions()
	{
		// Tree Patch Ready Requirements
		lumbridgeTreePatchHarvestable = new ManualRequirement();
		faladorTreePatchHarvestable = new ManualRequirement();
		taverleyTreePatchHarvestable = new ManualRequirement();
		varrockTreePatchHarvestable = new ManualRequirement();
		gnomeStrongholdTreePatchHarvestable = new ManualRequirement();
		farmingGuildTreePatchHarvestable = new ManualRequirement();

		// Tree Patch Empty Requirements
		lumbridgeTreePatchEmpty = new ManualRequirement();
		faladorTreePatchEmpty = new ManualRequirement();
		taverleyTreePatchEmpty = new ManualRequirement();
		varrockTreePatchEmpty = new ManualRequirement();
		gnomeStrongholdTreePatchEmpty = new ManualRequirement();
		farmingGuildTreePatchEmpty = new ManualRequirement();

		// Fruit Patch Ready Requirements
		gnomeStrongholdFruitTreePatchHarvestable = new ManualRequirement();
		gnomeVillageFruitTreePatchHarvestable = new ManualRequirement();
		brimhavenFruitTreePatchHarvestable = new ManualRequirement();
		lletyaFruitTreePatchHarvestable = new ManualRequirement();
		catherbyFruitTreePatchHarvestable = new ManualRequirement();
		farmingGuildFruitTreePatchHarvestable = new ManualRequirement();

		// Fruit Patch Empty Requirements
		gnomeStrongholdFruitTreePatchEmpty = new ManualRequirement();
		gnomeVillageFruitTreePatchEmpty = new ManualRequirement();
		brimhavenFruitTreePatchEmpty = new ManualRequirement();
		lletyaFruitTreePatchEmpty = new ManualRequirement();
		catherbyFruitTreePatchEmpty = new ManualRequirement();
		farmingGuildFruitTreePatchEmpty = new ManualRequirement();

		// Hardwood Tree Ready Requirements
		eastHardwoodTreePatchReady = new ManualRequirement();
		westHardwoodTreePatchReady = new ManualRequirement();
		middleHardwoodTreePatchReady = new ManualRequirement();

		// Hardwood Tree Empty Requirements
		eastHardwoodTreePatchEmpty = new ManualRequirement();
		westHardwoodTreePatchEmpty = new ManualRequirement();
		middleHardwoodTreePatchEmpty = new ManualRequirement();

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

		// Tree Not Checked
		lumbridgeTreePatchNotChecked = new ManualRequirement();
		lumbridgeTreePatchHarvestable = new ManualRequirement();
		lumbridgeTreePatchIsStump = new ManualRequirement();

		faladorTreePatchNotChecked = new ManualRequirement();
		faladorTreePatchHarvestable = new ManualRequirement();
		faladorTreePatchIsStump = new ManualRequirement();

		taverleyTreePatchNotChecked = new ManualRequirement();
		taverleyTreePatchHarvestable = new ManualRequirement();
		taverleyTreePatchIsStump = new ManualRequirement();

		varrockTreePatchNotChecked = new ManualRequirement();
		varrockTreePatchHarvestable = new ManualRequirement();
		varrockTreePatchIsStump = new ManualRequirement();

		gnomeStrongholdTreePatchNotChecked = new ManualRequirement();
		gnomeStrongholdTreePatchHarvestable = new ManualRequirement();
		gnomeStrongholdTreePatchIsStump = new ManualRequirement();

		farmingGuildTreePatchNotChecked = new ManualRequirement();
		farmingGuildTreePatchHarvestable = new ManualRequirement();
		farmingGuildTreePatchIsStump = new ManualRequirement();

		// Fruit Tree Not Checked
		gnomeStrongholdFruitTreePatchNotChecked = new ManualRequirement();
		gnomeStrongholdFruitTreePatchHarvestable = new ManualRequirement();
		gnomeStrongholdFruitTreePatchIsStump = new ManualRequirement();

		gnomeVillageFruitTreePatchNotChecked = new ManualRequirement();
		gnomeVillageFruitTreePatchHarvestable = new ManualRequirement();
		gnomeVillageFruitTreePatchIsStump = new ManualRequirement();

		brimhavenFruitTreePatchNotChecked = new ManualRequirement();
		brimhavenFruitTreePatchHarvestable = new ManualRequirement();
		brimhavenFruitTreePatchIsStump = new ManualRequirement();

		lletyaFruitTreePatchNotChecked = new ManualRequirement();
		lletyaFruitTreePatchHarvestable = new ManualRequirement();
		lletyaFruitTreePatchIsStump = new ManualRequirement();

		catherbyFruitTreePatchNotChecked = new ManualRequirement();
		catherbyFruitTreePatchHarvestable = new ManualRequirement();
		catherbyFruitTreePatchIsStump = new ManualRequirement();

		farmingGuildFruitTreePatchNotChecked = new ManualRequirement();
		farmingGuildFruitTreePatchHarvestable = new ManualRequirement();
		farmingGuildFruitTreePatchIsStump = new ManualRequirement();

		// Hardwood Tree Not Checked
		eastHardwoodTreePatchNotChecked = new ManualRequirement();
		eastHardwoodTreePatchHarvestable = new ManualRequirement();
		eastHardwoodTreePatchIsStump = new ManualRequirement();

		westHardwoodTreePatchNotChecked = new ManualRequirement();
		westHardwoodTreePatchHarvestable = new ManualRequirement();
		westHardwoodTreePatchIsStump = new ManualRequirement();

		middleHardwoodTreePatchNotChecked = new ManualRequirement();
		middleHardwoodTreePatchHarvestable = new ManualRequirement();
		middleHardwoodTreePatchIsStump = new ManualRequirement();
	}

	@Override
	public void setupRequirements()
	{
		// Farming Item Requirements
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2));
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS_995);
		axe = new ItemRequirement("Any axe you can use", ItemCollections.AXES);

		treeSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			TREE_SAPLING,
			TreeSapling.OAK,
			ItemID.OAK_SAPLING
		);
		treeSapling.setHighlightInInventory(true);
		allTreeSaplings = treeSapling.copy();

		fruitTreeSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			FRUIT_TREE_SAPLING,
			FruitTreeSapling.APPLE,
			ItemID.APPLE_SAPLING
		);
		fruitTreeSapling.setHighlightInInventory(true);
		allFruitSaplings = fruitTreeSapling.copy();

		hardwoodSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			HARDWOOD_TREE_SAPLING,
			HardwoodTreeSapling.TEAK,
			ItemID.TEAK_SAPLING
		);
		hardwoodSapling.setHighlightInInventory(true);

		compost	= new ItemRequirement("Compost", ItemCollections.COMPOST);

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

		lumbridgeTreePatchClear = new NpcStep(this, NpcID.FARMER, new WorldPoint(3193, 3231, 0),
			"Speak to the farmer to clear the patch.", coins.quantity(200));
		lumbridgeTreePatchClear.addDialogStep("Yes.");

		faladorTreePatchClear = new NpcStep(this, NpcID.FAYETH, new WorldPoint(3004, 3373, 0),
			"Speak to Fayeth to clear the patch.", coins.quantity(200));
		faladorTreePatchClear.addDialogStep("Yes.");

		taverleyTreePatchClear = new NpcStep(this, NpcID.ALAIN, new WorldPoint(2936, 3438, 0),
			"Speak to Alain to clear the patch.", coins.quantity(200));
		taverleyTreePatchClear.addDialogStep("Yes.");

		varrockTreePatchClear = new NpcStep(this, NpcID.TREZNOR_11957, new WorldPoint(3229, 3459, 0),
			"Speak to Treznor to clear the patch.", coins.quantity(200));
		varrockTreePatchClear.addDialogStep("Yes.");

		gnomeStrongholdTreePatchClear = new NpcStep(this, NpcID.PRISSY_SCILLA, new WorldPoint(2436, 3415, 0),
			"Speak to Prissy Scilla to clear the patch.", coins.quantity(200));
		gnomeStrongholdTreePatchClear.addDialogStep("Yes.");

		farmingGuildTreePatchClear = new NpcStep(this, NpcID.ROSIE, new WorldPoint(1232, 3736, 0),
			"Speak to Rosie to clear the patch.", coins.quantity(200));
		farmingGuildTreePatchClear.addDialogStep("Yes.");

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
			"Dig up the tree stump in Lumbridge.", spade);
		faladorTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Dig up the tree stump in Falador.", spade);
		taverleyTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2936, 3438, 0),
			"Dig up the tree stump in Taverley.", spade);
		varrockTreePatchDig = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3229, 3459, 0),
			"Dig up the tree stump in Varrock.", spade);
		gnomeStrongholdTreePatchDig = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2436, 3415, 0),
			"Dig up the tree stump in the Tree Gnome Stronghold.", spade);
		farmingGuildTreePatchDig = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1232, 3736, 0),
			"Dig up the tree stump in the Farming Guild tree patch.", spade);

		faladorTreePatchClear.addSubSteps(faladorTreePatchDig);
		taverleyTreePatchClear.addSubSteps(taverleyTreePatchDig);
		varrockTreePatchClear.addSubSteps(varrockTreePatchDig);
		gnomeStrongholdTreePatchClear.addSubSteps(gnomeStrongholdTreePatchDig);
		lumbridgeTreePatchClear.addSubSteps(lumbridgeTreePatchDig);
		farmingGuildTreePatchClear.addSubSteps(farmingGuildTreePatchDig);

		// Fruit Tree Steps
		gnomeStrongholdFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Check the health of the fruit tree planted in the Tree Gnome Stronghold.");
		gnomeVillageFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Check the health of the fruit tree planted outside the Tree Gnome Village. Follow Elkoy to get out quickly.");
		brimhavenFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Check the health of the fruit tree planted in Brimhaven.");
		catherbyFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2681, 3434, 0),
			"Check the health of the fruit tree planted in Catherby");
		catherbyFruitTreePatchCheckHealth.addTeleport(catherbyTeleport);
		catherbyFruitTreePatchCheckHealth.addSpellHighlight(NormalSpells.CAMELOT_TELEPORT);
		catherbyFruitTreePatchCheckHealth.addSpellHighlight(LunarSpells.CATHERBY_TELEPORT);

		lletyaFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchCheckHealth.addTeleport(crystalTeleport);

		farmingGuildFruitTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1243, 3760, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildFruitTreePatchCheckHealth.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchCheckHealth.addTeleport(farmingGuildTeleport);

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
		catherbyFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2681, 3434, 0),
			"Plant your sapling in the Catherby patch.", fruitTreeSapling);
		catherbyFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		catherbyFruitTreePatchCheckHealth.addSubSteps(catherbyFruitTreePatchPlant);

		lletyaFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Plant your sapling in the Lletya patch.", fruitTreeSapling);
		lletyaFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		lletyaFruitTreePatchCheckHealth.addSubSteps(lletyaFruitTreePatchPlant);

		farmingGuildFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1243, 3760, 0),
			"Plant your sapling in the Farming Guild patch.", fruitTreeSapling);
		farmingGuildFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		farmingGuildFruitTreePatchCheckHealth.addSubSteps(farmingGuildFruitTreePatchPlant);

		// Clear
		gnomeStrongholdFruitTreePatchClear = new NpcStep(this, NpcID.BOLONGO, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo 200 coins to clear the fruit tree, or pick all the fruit and cut it down.", coins.quantity(200));
		gnomeStrongholdFruitTreePatchClear.addDialogStep("Yes.");
		gnomeVillageFruitTreePatchClear = new NpcStep(this, NpcID.GILETH, new WorldPoint(2490, 3180, 0),
			"Pay Gileth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.", coins.quantity(200));
		gnomeVillageFruitTreePatchClear.addDialogStep("Yes.");
		brimhavenFruitTreePatchClear = new NpcStep(this, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.", coins.quantity(200));
		brimhavenFruitTreePatchClear.addDialogStep("Yes.");
		catherbyFruitTreePatchClear = new NpcStep(this, NpcID.ELLENA, new WorldPoint(2681, 3434, 0),
			"Pay Ellena 200 coins to clear the fruit tree, or pick all the fruit and cut it down.", coins.quantity(200));
		catherbyFruitTreePatchClear.addDialogStep("Yes.");
		lletyaFruitTreePatchClear = new NpcStep(this, NpcID.LILIWEN, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen 200 coins to clear the fruit tree, or pick all the fruit and cut it down.", coins.quantity(200));
		lletyaFruitTreePatchClear.addDialogStep("Yes.");
		farmingGuildFruitTreePatchClear = new NpcStep(this, NpcID.NIKKIE, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie 200 coins to clear the fruit tree, or pick all the fruit and cut it down.", coins.quantity(200));
		farmingGuildFruitTreePatchClear.addDialogStep("Yes.");

		// Dig Fruit Tree Steps
		gnomeStrongholdFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Dig up the fruit tree's stump in the Tree Gnome Stronghold.");
		gnomeVillageFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Dig up the fruit tree's stump outside the Tree Gnome Village.");
		brimhavenFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Dig up the fruit tree's stump in Brimhaven.");
		catherbyFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2681, 3434, 0),
			"Check the health of the fruit tree planted in Catherby");
		lletyaFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Dig up the fruit tree's stump in Lletya.");
		farmingGuildFruitTreePatchDig = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1243, 3760, 0),
			"Dig up the fruit tree's stump in the Farming Guild.");

		gnomeStrongholdFruitTreePatchClear.addSubSteps(gnomeStrongholdFruitTreePatchDig);
		gnomeVillageFruitTreePatchClear.addSubSteps(gnomeVillageFruitTreePatchDig);
		brimhavenFruitTreePatchClear.addSubSteps(brimhavenFruitTreePatchDig);
		catherbyFruitTreePatchClear.addSubSteps(catherbyFruitTreePatchDig);
		lletyaFruitTreePatchClear.addSubSteps(lletyaFruitTreePatchDig);
		farmingGuildFruitTreePatchClear.addSubSteps(farmingGuildFruitTreePatchDig);

		// Hardwood Tree Steps
		westHardwoodTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Check the health of the western hardwood tree on Fossil Island.");
		middleHardwoodTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Check the health of the centre hardwood tree on Fossil Island.");
		eastHardwoodTreePatchCheckHealth = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Check the health of the eastern hardwood tree on Fossil Island.");

		// Hardwood Tree Plant Steps
		westHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Plant your sapling on the western hardwood tree on Fossil Island.", hardwoodSapling);
		westHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		westHardwoodTreePatchCheckHealth.addSubSteps(westHardwoodTreePatchPlant);

		middleHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Plant your sapling on the centre hardwood tree on Fossil Island.", hardwoodSapling);
		middleHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		middleHardwoodTreePatchCheckHealth.addSubSteps(middleHardwoodTreePatchPlant);

		eastHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Plant your sapling on the eastern hardwood tree on Fossil Island.", hardwoodSapling);
		eastHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		eastHardwoodTreePatchCheckHealth.addSubSteps(eastHardwoodTreePatchPlant);

		westHardwoodTreePatchClear = new NpcStep(this, NpcID.SQUIRREL_7756, new WorldPoint(3702, 3837, 0),
			"Pay the brown squirrel to remove the west tree.", coins.quantity(200));
		middleHardwoodTreePatchClear = new NpcStep(this, NpcID.SQUIRREL_7755, new WorldPoint(3702, 3837, 0),
			"Pay the black squirrel to remove the middle tree.", coins.quantity(200));
		eastHardwoodTreePatchClear = new NpcStep(this, NpcID.SQUIRREL_7754, new WorldPoint(3702, 3837, 0),
			"Pay the grey squirrel to remove the east tree.", coins.quantity(200));

		westHardwoodTreePatchDig = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Dig up the western hardwood tree's stump on Fossil Island.");
		middleHardwoodTreePatchDig = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Dig up the centre hardwood tree's stump on Fossil Island.");
		eastHardwoodTreePatchDig = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Dig up the eastern hardwood tree's stump on Fossil Island.");
		westHardwoodTreePatchClear.addSubSteps(westHardwoodTreePatchDig);
		middleHardwoodTreePatchClear.addSubSteps(middleHardwoodTreePatchDig);
		eastHardwoodTreePatchClear.addSubSteps(eastHardwoodTreePatchDig);
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{
		int numberOfTreeSaplings = 0;
		for (FarmingPatch treePatch : farmingWorld.getTabs().get(Tab.TREE))
		{
			CropState treeState = farmingHandler.predictPatch(treePatch);
			boolean isTreePatchPlantable = treeState == CropState.EMPTY || treeState == CropState.DEAD;

			boolean isTreeUnchecked = treeState == CropState.UNCHECKED; // 'Check health'
			boolean isTreePatchHarvestable = treeState == CropState.HARVESTABLE; // 'Chop'
			boolean isTreeStump = treeState == CropState.STUMP; // 'Clear'

			if (treeState != CropState.GROWING)
			{
				numberOfTreeSaplings++;
			}

			switch (treePatch.getRegion().getName())
			{
				case "Lumbridge":
					lumbridgeTreePatchHarvestable.setShouldPass(isTreePatchHarvestable);
					lumbridgeTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					lumbridgeTreePatchNotChecked.setShouldPass(isTreeUnchecked);
					lumbridgeTreePatchIsStump.setShouldPass(isTreeStump);
					break;
				case "Varrock":
					varrockTreePatchHarvestable.setShouldPass(isTreePatchHarvestable);
					varrockTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					varrockTreePatchNotChecked.setShouldPass(isTreeUnchecked);
					varrockTreePatchIsStump.setShouldPass(isTreeStump);
					break;
				case "Falador":
					faladorTreePatchHarvestable.setShouldPass(isTreePatchHarvestable);
					faladorTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					faladorTreePatchNotChecked.setShouldPass(isTreeUnchecked);
					faladorTreePatchIsStump.setShouldPass(isTreeStump);
					break;
				case "Gnome Stronghold":
					gnomeStrongholdTreePatchHarvestable.setShouldPass(isTreePatchHarvestable);
					gnomeStrongholdTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					gnomeStrongholdTreePatchNotChecked.setShouldPass(isTreeUnchecked);
					gnomeStrongholdTreePatchIsStump.setShouldPass(isTreeStump);
					break;
				case "Taverley":
					taverleyTreePatchHarvestable.setShouldPass(isTreePatchHarvestable);
					taverleyTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					taverleyTreePatchNotChecked.setShouldPass(isTreeUnchecked);
					taverleyTreePatchIsStump.setShouldPass(isTreeStump);
					break;
				case "Farming Guild":
					farmingGuildTreePatchHarvestable.setShouldPass(isTreePatchHarvestable);
					farmingGuildTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					farmingGuildTreePatchNotChecked.setShouldPass(isTreeUnchecked);
					farmingGuildTreePatchIsStump.setShouldPass(isTreeStump);
					if (!accessToFarmingGuildTreePatch.check(client))
					{
						numberOfTreeSaplings--;
					}
					break;
			}
		}
		allTreeSaplings.quantity(numberOfTreeSaplings);

		int numberOfFruitTreeSaplings = 0;
		for (FarmingPatch fruitTreePatch : farmingWorld.getTabs().get(Tab.FRUIT_TREE))
		{
			CropState fruitTreeState = farmingHandler.predictPatch(fruitTreePatch);
			boolean isFruitTreePatchPlantable = fruitTreeState == CropState.EMPTY || fruitTreeState == CropState.DEAD;
			boolean isFruitTreeUnchecked = fruitTreeState == CropState.UNCHECKED; // 'Check health'
			boolean isFruitTreePatchHarvestable = fruitTreeState == CropState.HARVESTABLE; // 'Chop'
			boolean isFruitTreeStump = fruitTreeState == CropState.STUMP; // 'Clear'

			if (fruitTreeState != CropState.GROWING)
			{
				numberOfFruitTreeSaplings++;
			}

			switch (fruitTreePatch.getRegion().getName())
			{
				case "Catherby":
					catherbyFruitTreePatchHarvestable.setShouldPass(isFruitTreePatchHarvestable);
					catherbyFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					catherbyFruitTreePatchNotChecked.setShouldPass(isFruitTreeUnchecked);
					catherbyFruitTreePatchIsStump.setShouldPass(isFruitTreeStump);
					break;
				case "Brimhaven":
					brimhavenFruitTreePatchHarvestable.setShouldPass(isFruitTreePatchHarvestable);
					brimhavenFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					brimhavenFruitTreePatchNotChecked.setShouldPass(isFruitTreeUnchecked);
					brimhavenFruitTreePatchIsStump.setShouldPass(isFruitTreeStump);
					break;
				case "Tree Gnome Village":
					gnomeVillageFruitTreePatchHarvestable.setShouldPass(isFruitTreePatchHarvestable);
					gnomeVillageFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					gnomeVillageFruitTreePatchNotChecked.setShouldPass(isFruitTreeUnchecked);
					gnomeVillageFruitTreePatchIsStump.setShouldPass(isFruitTreeStump);
					break;
				case "Gnome Stronghold":
					gnomeStrongholdFruitTreePatchHarvestable.setShouldPass(isFruitTreePatchHarvestable);
					gnomeStrongholdFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					gnomeStrongholdFruitTreePatchNotChecked.setShouldPass(isFruitTreeUnchecked);
					gnomeStrongholdFruitTreePatchIsStump.setShouldPass(isFruitTreeStump);
					break;
				case "Lletya":
					lletyaFruitTreePatchHarvestable.setShouldPass(isFruitTreePatchHarvestable);
					lletyaFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					lletyaFruitTreePatchNotChecked.setShouldPass(isFruitTreeUnchecked);
					lletyaFruitTreePatchIsStump.setShouldPass(isFruitTreeStump);
					if (!accessToLletya.check(client))
					{
						numberOfFruitTreeSaplings--;
					}
					break;
				case "Farming Guild":
					farmingGuildFruitTreePatchHarvestable.setShouldPass(isFruitTreePatchHarvestable);
					farmingGuildFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					farmingGuildFruitTreePatchNotChecked.setShouldPass(isFruitTreeUnchecked);
					farmingGuildFruitTreePatchIsStump.setShouldPass(isFruitTreeStump);
					if (!accessToFarmingGuildFruitTreePatch.check(client))
					{
						numberOfFruitTreeSaplings--;
					}
					break;
			}
		}
		allFruitSaplings.quantity(numberOfFruitTreeSaplings);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			return;
		}

		FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, TREE_SAPLING, TreeSapling.class,
			this::updateTreeSapling, TreeSapling.OAK, configManager, questHelperPlugin);
		FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, FRUIT_TREE_SAPLING, FruitTreeSapling.class,
			this::updateFruitTreeSapling, FruitTreeSapling.APPLE, configManager, questHelperPlugin);
		FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.class,
			this::updateHardwoodTreeSapling, HardwoodTreeSapling.TEAK, configManager, questHelperPlugin);

		if (event.getKey().equals(GRACEFUL_OR_FARMING))
		{
			questHelperPlugin.refreshBank();
		}
	}

	@Override
	public List<HelperConfig> getConfigs()
	{
		HelperConfig treesConfig = new HelperConfig("Trees", TREE_SAPLING, TreeSapling.values());
		HelperConfig fruitTreesConfig = new HelperConfig("Fruit Trees", FRUIT_TREE_SAPLING, FruitTreeSapling.values());
		HelperConfig hardwoodTreesConfig = new HelperConfig("Hardwood Trees", HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		return Arrays.asList(treesConfig, fruitTreesConfig, hardwoodTreesConfig, outfitConfig);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, rake, coins, allTreeSaplings, allFruitSaplings, hardwoodSapling);
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
		allSteps.add(new PanelDetails("Farming Guild", Arrays.asList(farmingGuildTreePatchCheckHealth, farmingGuildTreePatchClear, farmingGuildFruitTreePatchCheckHealth, farmingGuildFruitTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit, farmingGuildTeleport)));
		allSteps.add(new PanelDetails("Lumbridge", Arrays.asList(lumbridgeTreePatchCheckHealth, lumbridgeTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit, lumbridgeTeleport)));
		allSteps.add(new PanelDetails("Falador", Arrays.asList(faladorTreePatchCheckHealth, faladorTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit, faladorTeleport)));
		allSteps.add(new PanelDetails("Taverley", Arrays.asList(taverleyTreePatchCheckHealth, taverleyTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit)));
		allSteps.add(new PanelDetails("Varrock", Arrays.asList(varrockTreePatchCheckHealth, varrockTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit, varrockTeleport)));
		allSteps.add(new PanelDetails("Gnome Stronghold", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchClear, gnomeStrongholdTreePatchCheckHealth, gnomeStrongholdTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit)));
		allSteps.add(new PanelDetails("Tree Gnome Village", Arrays.asList(gnomeVillageFruitTreePatchCheckHealth, gnomeVillageFruitTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit)));
		allSteps.add(new PanelDetails("Catherby", Arrays.asList(catherbyFruitTreePatchCheckHealth, catherbyFruitTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit, catherbyTeleport)));
		allSteps.add(new PanelDetails("Brimhaven", Arrays.asList(brimhavenFruitTreePatchCheckHealth, brimhavenFruitTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit)));
		allSteps.add(new PanelDetails("Llyeta", Arrays.asList(lletyaFruitTreePatchCheckHealth, lletyaFruitTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit)));
		allSteps.add(new PanelDetails("Fossil Island", Arrays.asList(eastHardwoodTreePatchCheckHealth, eastHardwoodTreePatchClear,
			middleHardwoodTreePatchCheckHealth, middleHardwoodTreePatchClear,
			westHardwoodTreePatchCheckHealth, westHardwoodTreePatchClear),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit, fossilIslandTeleport)));

		return allSteps;
	}

	private void updateTreeSapling(TreeSapling selectedTreeSapling)
	{
		treeSapling.setId(selectedTreeSapling.treeSaplingID);
		treeSapling.setName(Text.titleCase(selectedTreeSapling) + " sapling");
	}

	private void updateFruitTreeSapling(FruitTreeSapling selectedFruitTreeSapling)
	{
		fruitTreeSapling.setId(selectedFruitTreeSapling.fruitTreeSaplingId);
		fruitTreeSapling.setName(Text.titleCase(selectedFruitTreeSapling) + " sapling");
	}

	private void updateHardwoodTreeSapling(HardwoodTreeSapling selectedHardwoodTreeSapling)
	{
		hardwoodSapling.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		hardwoodSapling.setName(Text.titleCase(selectedHardwoodTreeSapling) + " sapling");
	}
}
