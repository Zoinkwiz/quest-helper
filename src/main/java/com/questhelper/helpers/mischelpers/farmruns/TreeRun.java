package com.questhelper.helpers.mischelpers.farmruns;

import com.google.inject.Inject;
import com.questhelper.HelperConfig;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperQuest;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.FruitTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.GracefulOrFarming;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.HardwoodTreeSapling;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils.TreeSapling;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
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
import com.sun.source.doctree.ValueTree;
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
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.util.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.TREE_RUN
)

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
	DetailedQuestStep farmingGuildTreePatch, lumbridgeTreePatch, faladorTreePatch, taverleyTreePatch, varrockTreePatch,
		gnomeStrongholdTreePatch;
	DetailedQuestStep farmingGuildTreePatchPlant, lumbridgeTreePatchPlant, faladorTreePatchPlant, taverleyTreePatchPlant,
		varrockTreePatchPlant, gnomeStrongholdTreePatchPlant;

	DetailedQuestStep lumbridgeTreePatchClear, faladorTreePatchClear, taverleyTreePatchClear, varrockTreePatchClear,
		gnomeStrongholdTreePatchClear, farmingGuildTreePatchClear;

	// Fruit Trees
	DetailedQuestStep farmingGuildFruitTreePatch, gnomeStrongholdFruitTreePatch, gnomeVillageFruitTreePatch,
		brimhavenFruitTreePatch, lletyaFruitTreePatch, catherbyFruitTreePatch;
	DetailedQuestStep farmingGuildFruitTreePatchPlant, gnomeStrongholdFruitTreePatchPlant, gnomeVillageFruitTreePatchPlant,
		brimhavenFruitTreePatchPlant, lletyaFruitTreePatchPlant, catherbyFruitTreePatchPlant;

	DetailedQuestStep farmingGuildFruitTreePatchClear, gnomeStrongholdFruitTreePatchClear, gnomeVillageFruitTreePatchClear,
		brimhavenFruitTreePatchClear, lletyaFruitTreePatchClear, catherbyFruitTreePatchClear;

	// Hardwood Trees
	DetailedQuestStep eastHardwoodTreePatch, westHardwoodTreePatch, middleHardwoodTreePatch;
	DetailedQuestStep eastHardwoodTreePatchPlant, westHardwoodTreePatchPlant, middleHardwoodTreePatchPlant;

	DetailedQuestStep eastHardwoodTreePatchClear, westHardwoodTreePatchClear, middleHardwoodTreePatchClear;

	// Farming Items
	ItemRequirement coins, spade, rake, treeSapling, fruitTreeSapling, hardwoodSapling, compost, axe, protectionItem;

	// Teleport Items
	// TODO: Add these...
	ItemRequirement farmingGuildTeleport;

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland;

	// Tree Requirements
	ManualRequirement  lumbridgeTreePatchEmpty, faladorTreePatchEmpty, taverleyTreePatchEmpty,
		varrockTreePatchEmpty, gnomeStrongholdTreePatchEmpty, farmingGuildTreePatchEmpty;
	ManualRequirement  lumbridgeTreePatchReady, faladorTreePatchReady, taverleyTreePatchReady,
		varrockTreePatchReady, gnomeStrongholdTreePatchReady, farmingGuildTreePatchReady;
	ManualRequirement faladorTreePatchNotChecked, faladorTreePatchChecked, faladorTreePatchIsStump,
		lumbridgeTreePatchNotChecked, lumbridgeTreePatchChecked, lumbridgeTreePatchIsStump,
		farmingGuildTreePatchNotChecked, farmingGuildTreePatchChecked, farmingGuildTreePatchIsStump,
		taverleyTreePatchNotChecked, taverleyTreePatchChecked, taverleyTreePatchIsStump,
		varrockTreePatchNotChecked, varrockTreePatchChecked, varrockTreePatchIsStump,
		gnomeStrongholdTreePatchNotChecked, gnomeStrongholdTreePatchChecked, gnomeStrongholdTreePatchIsStump;

	// Fruit Tree Requirements
	ManualRequirement gnomeStrongholdFruitTreePatchEmpty, gnomeVillageFruitTreePatchEmpty,
		brimhavenFruitTreePatchEmpty, lletyaFruitTreePatchEmpty, catherbyFruitTreePatchEmpty, farmingGuildFruitTreePatchEmpty;
	ManualRequirement gnomeStrongholdFruitTreePatchReady, gnomeVillageFruitTreePatchReady,
		brimhavenFruitTreePatchReady, lletyaFruitTreePatchReady, catherbyFruitTreePatchReady, farmingGuildFruitTreePatchReady;

	ManualRequirement gnomeStrongholdFruitTreePatchNotChecked, gnomeStrongholdFruitTreePatchChecked, gnomeStrongholdFruitTreePatchIsStump,
		gnomeVillageFruitTreePatchNotChecked, gnomeVillageFruitTreePatchChecked, gnomeVillageFruitTreePatchIsStump,
		brimhavenFruitTreePatchNotChecked, brimhavenFruitTreePatchChecked, brimhavenFruitTreePatchIsStump,
		lletyaFruitTreePatchNotChecked, lletyaFruitTreePatchChecked, lletyaFruitTreePatchIsStump,
		catherbyFruitTreePatchNotChecked, catherbyFruitTreePatchChecked, catherbyFruitTreePatchIsStump,
		farmingGuildFruitTreePatchNotChecked, farmingGuildFruitTreePatchChecked, farmingGuildFruitTreePatchIsStump;

	// Hardwood Tree Requirements
	ManualRequirement eastHardwoodTreePatchEmpty, westHardwoodTreePatchEmpty, middleHardwoodTreePatchEmpty;
	ManualRequirement eastHardwoodTreePatchReady, westHardwoodTreePatchReady, middleHardwoodTreePatchReady;

	ManualRequirement eastHardwoodTreePatchNotChecked, eastHardwoodTreePatchChecked, eastHardwoodTreePatchIsStump,
		westHardwoodTreePatchNotChecked, westHardwoodTreePatchChecked, westHardwoodTreePatchIsStump,
		middleHardwoodTreePatchNotChecked, middleHardwoodTreePatchChecked, middleHardwoodTreePatchIsStump;

	private final String TREE_SAPLING = "treeSaplings";
	private final String FRUIT_TREE_SAPLING = "fruitTreeSaplings";
	private final String HARDWOOD_TREE_SAPLING = "hardwoodTreeSaplings";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	private final int[] TREE_UNCHECKED = { 12, 21, 32, 45, 60 };
	// 192 - 197 for Willow tree final state? Are all trees like this?
	private final int[] TREE_CHECKED = {13, 22, 33, 46, 61, 192, 193, 194, 195, 196, 197};
	private final int[] TREE_STUMP = {14, 23, 34, 47, 62};

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

		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreePatchReady), farmingGuildTreePatch);
		steps.addStep(new Conditions(accessToFarmingGuildTreePatch, farmingGuildTreePatchEmpty), farmingGuildTreePatchPlant);

		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitTreePatchReady), farmingGuildFruitTreePatch);
		steps.addStep(new Conditions(accessToFarmingGuildFruitTreePatch, farmingGuildFruitTreePatchEmpty), farmingGuildFruitTreePatchPlant);

		steps.addStep(lumbridgeTreePatchReady, lumbridgeTreePatch);
		steps.addStep(lumbridgeTreePatchEmpty, lumbridgeTreePatchPlant);

		steps.addStep(new Conditions(faladorTreePatchReady, faladorTreePatchNotChecked), faladorTreePatch);
		steps.addStep(new Conditions(faladorTreePatchReady, faladorTreePatchChecked), faladorTreePatchClear);
		steps.addStep(faladorTreePatchEmpty, faladorTreePatchPlant);

		steps.addStep(taverleyTreePatchReady, taverleyTreePatch);
		steps.addStep(taverleyTreePatchEmpty, taverleyTreePatchPlant);

		steps.addStep(varrockTreePatchReady, varrockTreePatch);
		steps.addStep(varrockTreePatchEmpty, varrockTreePatchPlant);

		steps.addStep(gnomeStrongholdFruitTreePatchReady, gnomeStrongholdFruitTreePatch);
		steps.addStep(gnomeStrongholdFruitTreePatchEmpty, gnomeStrongholdFruitTreePatchPlant);

		steps.addStep(gnomeStrongholdTreePatchReady, gnomeStrongholdTreePatch);
		steps.addStep(gnomeStrongholdTreePatchEmpty, gnomeStrongholdFruitTreePatchPlant);

		steps.addStep(gnomeVillageFruitTreePatchReady, gnomeVillageFruitTreePatch);
		steps.addStep(gnomeVillageFruitTreePatchEmpty, gnomeVillageFruitTreePatchPlant);

		steps.addStep(brimhavenFruitTreePatchReady, brimhavenFruitTreePatch);
		steps.addStep(brimhavenFruitTreePatchEmpty, brimhavenFruitTreePatchPlant);

		steps.addStep(catherbyFruitTreePatchReady, catherbyFruitTreePatch);
		steps.addStep(catherbyFruitTreePatchEmpty, catherbyFruitTreePatchPlant);

		steps.addStep(new Conditions(accessToLletya, lletyaFruitTreePatchReady), lletyaFruitTreePatch);
		steps.addStep(new Conditions(accessToLletya, lletyaFruitTreePatchEmpty), lletyaFruitTreePatchPlant);

		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodTreePatchReady), eastHardwoodTreePatch);
		steps.addStep(new Conditions(accessToFossilIsland, eastHardwoodTreePatchEmpty), eastHardwoodTreePatchPlant);

		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodTreePatchReady), middleHardwoodTreePatch);
		steps.addStep(new Conditions(accessToFossilIsland, middleHardwoodTreePatchEmpty), middleHardwoodTreePatchPlant);

		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodTreePatchReady), westHardwoodTreePatch);
		steps.addStep(new Conditions(accessToFossilIsland, westHardwoodTreePatchEmpty), westHardwoodTreePatchPlant);

		return steps;
	}

	private void setupConditions()
	{
		// Tree Patch Ready Requirements
		lumbridgeTreePatchReady = new ManualRequirement();
		faladorTreePatchReady = new ManualRequirement();
		taverleyTreePatchReady = new ManualRequirement();
		varrockTreePatchReady = new ManualRequirement();
		gnomeStrongholdTreePatchReady = new ManualRequirement();
		farmingGuildTreePatchReady = new ManualRequirement();

		// Tree Patch Empty Requirements
		lumbridgeTreePatchEmpty = new ManualRequirement();
		faladorTreePatchEmpty = new ManualRequirement();
		taverleyTreePatchEmpty = new ManualRequirement();
		varrockTreePatchEmpty = new ManualRequirement();
		gnomeStrongholdTreePatchEmpty = new ManualRequirement();
		farmingGuildTreePatchEmpty = new ManualRequirement();

		// Fruit Patch Ready Requirements
		gnomeStrongholdFruitTreePatchReady = new ManualRequirement();
		gnomeVillageFruitTreePatchReady = new ManualRequirement();
		brimhavenFruitTreePatchReady = new ManualRequirement();
		lletyaFruitTreePatchReady = new ManualRequirement();
		catherbyFruitTreePatchReady = new ManualRequirement();
		farmingGuildFruitTreePatchReady = new ManualRequirement();

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
			new FavourRequirement(Favour.HOSIDIUS, 60),
			new SkillRequirement(Skill.FARMING, 65)
		);
		accessToFarmingGuildFruitTreePatch = new Conditions(
			new FavourRequirement(Favour.HOSIDIUS, 60),
			new SkillRequirement(Skill.FARMING, 85)
		);

		// Tree Not Checked
		lumbridgeTreePatchNotChecked = new ManualRequirement();
		lumbridgeTreePatchChecked = new ManualRequirement();
		lumbridgeTreePatchIsStump = new ManualRequirement();

		faladorTreePatchNotChecked = new ManualRequirement();
		faladorTreePatchChecked = new ManualRequirement();
		faladorTreePatchIsStump = new ManualRequirement();

		taverleyTreePatchNotChecked = new ManualRequirement();
		taverleyTreePatchChecked = new ManualRequirement();
		taverleyTreePatchIsStump = new ManualRequirement();

		varrockTreePatchNotChecked = new ManualRequirement();
		varrockTreePatchChecked = new ManualRequirement();
		varrockTreePatchIsStump = new ManualRequirement();

		gnomeStrongholdTreePatchNotChecked = new ManualRequirement();
		gnomeStrongholdTreePatchChecked = new ManualRequirement();
		gnomeStrongholdTreePatchIsStump = new ManualRequirement();

		farmingGuildTreePatchNotChecked = new ManualRequirement();
		farmingGuildTreePatchChecked = new ManualRequirement();
		farmingGuildTreePatchIsStump = new ManualRequirement();

		// Fruit Tree Not Checked
		gnomeStrongholdFruitTreePatchNotChecked = new ManualRequirement();
		gnomeStrongholdFruitTreePatchChecked = new ManualRequirement();
		gnomeStrongholdFruitTreePatchIsStump = new ManualRequirement();

		gnomeVillageFruitTreePatchNotChecked = new ManualRequirement();
		gnomeVillageFruitTreePatchChecked = new ManualRequirement();
		gnomeVillageFruitTreePatchIsStump = new ManualRequirement();

		brimhavenFruitTreePatchNotChecked = new ManualRequirement();
		brimhavenFruitTreePatchChecked = new ManualRequirement();
		brimhavenFruitTreePatchIsStump = new ManualRequirement();

		lletyaFruitTreePatchNotChecked = new ManualRequirement();
		lletyaFruitTreePatchChecked = new ManualRequirement();
		lletyaFruitTreePatchIsStump = new ManualRequirement();

		catherbyFruitTreePatchNotChecked = new ManualRequirement();
		catherbyFruitTreePatchChecked = new ManualRequirement();
		catherbyFruitTreePatchIsStump = new ManualRequirement();

		farmingGuildFruitTreePatchNotChecked = new ManualRequirement();
		farmingGuildFruitTreePatchChecked = new ManualRequirement();
		farmingGuildFruitTreePatchIsStump = new ManualRequirement();

		// Hardwood Tree Not Checked
		eastHardwoodTreePatchNotChecked = new ManualRequirement();
		eastHardwoodTreePatchChecked = new ManualRequirement();
		eastHardwoodTreePatchIsStump = new ManualRequirement();

		westHardwoodTreePatchNotChecked = new ManualRequirement();
		westHardwoodTreePatchChecked = new ManualRequirement();
		westHardwoodTreePatchIsStump = new ManualRequirement();

		middleHardwoodTreePatchNotChecked = new ManualRequirement();
		middleHardwoodTreePatchChecked = new ManualRequirement();
		middleHardwoodTreePatchIsStump = new ManualRequirement();

	}

	@Override
	public void setupRequirements()
	{
		// Farming Item Requirements
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2));
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS_995, -1);

		treeSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			TREE_SAPLING,
			TreeSapling.OAK,
			ItemID.OAK_SAPLING
		);
		treeSapling.setHighlightInInventory(true);

		fruitTreeSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			FRUIT_TREE_SAPLING,
			FruitTreeSapling.APPLE,
			ItemID.APPLE_SAPLING
		);
		fruitTreeSapling.setHighlightInInventory(true);

		hardwoodSapling = FarmingUtils.createSeedRequirement (
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

		lumbridgeTreePatchClear = new NpcStep(this, NpcID.FARMER, new WorldPoint(3226, 3219, 0),
			"Speak to the farmer to clear the patch.");
		lumbridgeTreePatchClear.addDialogStep("Yes.");

		faladorTreePatchClear = new NpcStep(this, NpcID.FAYETH, "Speak to the farmer to clear the patch.");
		faladorTreePatchClear.addDialogStep("Yes.");

		taverleyTreePatchClear = new NpcStep(this, NpcID.ALAIN, "Speak to the farmer to clear the patch.");
		taverleyTreePatchClear.addDialogStep("Yes.");

		varrockTreePatchClear = new NpcStep(this, NpcID.TREZNOR_11957, new WorldPoint(3226, 3219, 0),
			"Speak to the farmer to clear the patch.");
		varrockTreePatchClear.addDialogStep("Yes.");

		gnomeStrongholdTreePatchClear = new NpcStep(this, NpcID.PRISSY_SCILLA, new WorldPoint(3226, 3219, 0),
			"Speak to the farmer to clear the patch.");
		gnomeStrongholdTreePatchClear.addDialogStep("Yes.");

		farmingGuildTreePatchClear = new NpcStep(this, NpcID.ROSIE, new WorldPoint(3226, 3219, 0),
			"Speak to the farmer to clear the patch.");
		farmingGuildTreePatchClear.addDialogStep("Yes.");

		// Tree Patch Steps
		lumbridgeTreePatch = new ObjectStep(this, NullObjectID.NULL_8391, new WorldPoint(3193, 3231, 0),
			"Check the health of the tree planted in Lumbridge.");
		faladorTreePatch = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Check the health of the tree planted in Falador.");
		taverleyTreePatch = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2936, 3438, 0),
			"Check the health of the tree planted in Taverley.");
		varrockTreePatch = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3229, 3459, 0),
			"Check the health of the tree planted in Varrock.");
		gnomeStrongholdTreePatch = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2436, 3415, 0),
			"Check the health of the tree planted in the Tree Gnome Stronghold.");

		farmingGuildTreePatch = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1232, 3736, 0),
			"Check the health of the tree planted in the Farming Guild.");
		farmingGuildTreePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));

		// Tree Plant Steps
		lumbridgeTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8391, new WorldPoint(3193, 3231, 0),
			"Plant your sapling in the Lumbridge patch.", treeSapling);
		treeSapling.highlighted();
		lumbridgeTreePatchPlant.addIcon(treeSapling.getId());
		lumbridgeTreePatch.addSubSteps(lumbridgeTreePatchPlant);

		faladorTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3004, 3373, 0),
			"Plant your sapling in the Falador patch.", treeSapling);
		faladorTreePatchPlant.addIcon(treeSapling.getId());
		faladorTreePatch.addSubSteps(faladorTreePatchPlant);

		taverleyTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2936, 3438, 0),
			"Plant your sapling in the Taverley patch.", treeSapling);
		taverleyTreePatchPlant.addIcon(treeSapling.getId());
		taverleyTreePatch.addSubSteps(taverleyTreePatchPlant);

		varrockTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3229, 3459, 0),
			"Plant your sapling in the Varrock patch.", treeSapling);
		varrockTreePatchPlant.addIcon(treeSapling.getId());
		varrockTreePatch.addSubSteps(varrockTreePatchPlant);

		gnomeStrongholdTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2436, 3415, 0),
			"Plant your sapling in the Gnome Stronghold patch.", treeSapling);
		gnomeStrongholdTreePatchPlant.addIcon(treeSapling.getId());
		gnomeStrongholdTreePatch.addSubSteps(gnomeStrongholdTreePatchPlant);

		farmingGuildTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1232, 3736, 0),
			"Plant your sapling in the Farming Guild tree patch.", treeSapling);
		farmingGuildTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));
		farmingGuildTreePatchPlant.addIcon(treeSapling.getId());
		farmingGuildTreePatch.addSubSteps(farmingGuildTreePatchPlant);

		// Fruit Tree Steps
		gnomeStrongholdFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Check the health of the fruit tree planted in the Tree Gnome Stronghold.");
		gnomeVillageFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Check the health of the fruit tree planted outside the Tree Gnome Village. Follow Elkoy to get out quickly.");
		brimhavenFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Check the health of the fruit tree planted in Brimhaven.");
		catherbyFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2681, 3434, 0),
			"Check the health of the fruit tree planted in Catherby");

		lletyaFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaFruitTreePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));

		farmingGuildFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1243, 3760, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildFruitTreePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));

		// Fruit Tree Plant Steps
		gnomeStrongholdFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2476, 3446, 0),
			"Plant your sapling in the Tree Gnome Stronghold patch.", fruitTreeSapling);
		gnomeStrongholdFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		gnomeStrongholdTreePatch.addSubSteps(gnomeStrongholdTreePatchPlant);

		gnomeVillageFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2490, 3180, 0),
			"Plant your sapling in the Tree Gnome Village patch. Follow Elkoy to get out quickly.", fruitTreeSapling);
		gnomeVillageFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		gnomeVillageFruitTreePatch.addSubSteps(gnomeVillageFruitTreePatchPlant);

		brimhavenFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Plant your sapling in the Brimhaven patch.", fruitTreeSapling);
		brimhavenFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		brimhavenFruitTreePatch.addSubSteps(brimhavenFruitTreePatchPlant);

		catherbyFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2681, 3434, 0),
			"Plant your sapling in the Catherby patch.", fruitTreeSapling);
		catherbyFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		catherbyFruitTreePatch.addSubSteps(catherbyFruitTreePatchPlant);

		lletyaFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2347, 3162, 0),
			"Plant your sapling in the Lletya patch.", fruitTreeSapling);
		lletyaFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));
		lletyaFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		lletyaFruitTreePatch.addSubSteps(lletyaFruitTreePatchPlant);

		farmingGuildFruitTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1243, 3760, 0),
			"Plant your sapling in the Farming Guild patch.", fruitTreeSapling);
		farmingGuildFruitTreePatchPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));
		farmingGuildFruitTreePatchPlant.addIcon(fruitTreeSapling.getId());
		farmingGuildFruitTreePatch.addSubSteps(farmingGuildFruitTreePatchPlant);

		// Hardwood Tree Steps
		westHardwoodTreePatch = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Check the health of the western hardwood tree on Fossil Island.");
		middleHardwoodTreePatch = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Check the health of the centre hardwood tree on Fossil Island.");
		eastHardwoodTreePatch = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Check the health of the eastern hardwood tree on Fossil Island.");

		// Hardwood Tree Plant Steps
		westHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30481, new WorldPoint(3702, 3837, 0),
			"Plant your sapling on the western hardwood tree on Fossil Island.", hardwoodSapling);
		westHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		westHardwoodTreePatch.addSubSteps(westHardwoodTreePatchPlant);

		middleHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30480, new WorldPoint(3708, 3833, 0),
			"Plant your sapling on the centre hardwood tree on Fossil Island.", hardwoodSapling);
		middleHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		middleHardwoodTreePatch.addSubSteps(middleHardwoodTreePatchPlant);

		eastHardwoodTreePatchPlant = new ObjectStep(this, NullObjectID.NULL_30482, new WorldPoint(3715, 3835, 0),
			"Plant your sapling on the eastern hardwood tree on Fossil Island.", hardwoodSapling);
		eastHardwoodTreePatchPlant.addIcon(hardwoodSapling.getId());
		eastHardwoodTreePatch.addSubSteps(eastHardwoodTreePatchPlant);
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{

		// Tree Patch Checks
		// Since these all use the same values, it is probably redundant as it will always return the same thing..
		// Should instead use a single Checked/NotChecked/Stump conditional for 4771 and another for 7905?
		// Need to see radius that causes varb to update, as sometimes update doesn't trigger....
		checkTreePatch(lumbridgeTreePatchNotChecked, lumbridgeTreePatchChecked, lumbridgeTreePatchIsStump, Varbits.FARMING_4771);
		checkTreePatch(faladorTreePatchNotChecked, faladorTreePatchChecked, faladorTreePatchIsStump, Varbits.FARMING_4771);
		checkTreePatch(taverleyTreePatchNotChecked, taverleyTreePatchChecked, taverleyTreePatchIsStump, Varbits.FARMING_4771);
		checkTreePatch(varrockTreePatchNotChecked, varrockTreePatchChecked, varrockTreePatchIsStump, Varbits.FARMING_4771);
		checkTreePatch(gnomeStrongholdTreePatchNotChecked, gnomeStrongholdTreePatchChecked, gnomeStrongholdTreePatchIsStump, Varbits.FARMING_4771);
		checkTreePatch(farmingGuildTreePatchNotChecked, farmingGuildTreePatchChecked, farmingGuildTreePatchIsStump, Varbits.FARMING_7905);

		for (FarmingPatch treePatch : farmingWorld.getTabs().get(Tab.TREE))
		{
			CropState treeState = farmingHandler.predictPatch(treePatch);
			boolean isTreePatchHarvestable = treeState == CropState.HARVESTABLE;
			boolean isTreePatchPlantable = treeState == CropState.EMPTY || treeState == CropState.DEAD;

			switch (treePatch.getRegion().getName())
			{
				case "Lumbridge":
					lumbridgeTreePatchReady.setShouldPass(isTreePatchHarvestable);
					lumbridgeTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					break;
				case "Varrock":
					varrockTreePatchReady.setShouldPass(isTreePatchHarvestable);
					varrockTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					break;
				case "Falador":
					faladorTreePatchReady.setShouldPass(isTreePatchHarvestable);
					faladorTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					break;
				case "Gnome Stronghold":
					gnomeStrongholdTreePatchReady.setShouldPass(isTreePatchHarvestable);
					gnomeStrongholdTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					break;
				case "Taverley":
					taverleyTreePatchReady.setShouldPass(isTreePatchHarvestable);
					taverleyTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					break;
				case "Farming Guild":
					farmingGuildTreePatchReady.setShouldPass(isTreePatchHarvestable);
					farmingGuildTreePatchEmpty.setShouldPass(isTreePatchPlantable);
					break;
			}
		}

		for (FarmingPatch fruitTreePatch : farmingWorld.getTabs().get(Tab.FRUIT_TREE))
		{
			CropState fruitTreeState = farmingHandler.predictPatch(fruitTreePatch);
			boolean isFruitTreePatchHarvestable = fruitTreeState == CropState.HARVESTABLE;
			boolean isFruitTreePatchPlantable = fruitTreeState == CropState.EMPTY || fruitTreeState == CropState.DEAD;

			switch (fruitTreePatch.getRegion().getName())
			{
				case "Catherby":
					catherbyFruitTreePatchReady.setShouldPass(isFruitTreePatchHarvestable);
					catherbyFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					break;
				case "Brimhaven":
					brimhavenFruitTreePatchReady.setShouldPass(isFruitTreePatchHarvestable);
					brimhavenFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					break;
				case "Tree Gnome Village":
					gnomeVillageFruitTreePatchReady.setShouldPass(isFruitTreePatchHarvestable);
					gnomeVillageFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					break;
				case "Gnome Stronghold":
					gnomeStrongholdFruitTreePatchReady.setShouldPass(isFruitTreePatchHarvestable);
					gnomeStrongholdFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					break;
				case "Lletya":
					lletyaFruitTreePatchReady.setShouldPass(isFruitTreePatchHarvestable);
					lletyaFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					break;
				case "Farming Guild":
					farmingGuildFruitTreePatchReady.setShouldPass(isFruitTreePatchHarvestable);
					farmingGuildFruitTreePatchEmpty.setShouldPass(isFruitTreePatchPlantable);
					break;
			}
		}
	}

	private void checkTreePatch(ManualRequirement treePatchUnchecked, ManualRequirement treePatchChecked,
									   ManualRequirement treePatchIsStump, int farmingVarbit)
	{
		if (FarmingUtils.getPatchState(client, TREE_UNCHECKED, farmingVarbit))
		{
			treePatchUnchecked.setShouldPass(true);
		}

		if (FarmingUtils.getPatchState(client, TREE_CHECKED, farmingVarbit))
		{
			treePatchChecked.setShouldPass(true);
		}

		if (FarmingUtils.getPatchState(client, TREE_STUMP, farmingVarbit))
		{
			treePatchIsStump.setShouldPass(true);
		}
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
		return Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gracefulOutfit, farmersOutfit);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Farm run", Arrays.asList(farmingGuildTreePatch, farmingGuildFruitTreePatch,
			lumbridgeTreePatch, faladorTreePatch, taverleyTreePatch, varrockTreePatch, gnomeStrongholdFruitTreePatch,
			gnomeStrongholdTreePatch, gnomeVillageFruitTreePatch, brimhavenFruitTreePatch, catherbyFruitTreePatch,
			lletyaFruitTreePatch, eastHardwoodTreePatch, middleHardwoodTreePatch, westHardwoodTreePatch),
			Arrays.asList(spade, rake, coins, treeSapling, fruitTreeSapling, hardwoodSapling),
			Arrays.asList(gracefulOutfit, farmersOutfit)));

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
