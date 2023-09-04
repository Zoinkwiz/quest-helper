package com.questhelper.helpers.mischelpers.farmruns;

import com.google.inject.Inject;
import com.questhelper.HelperConfig;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperQuest;
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
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.util.Text;
import java.util.Arrays;
import java.util.List;

@QuestDescriptor(
	quest = QuestHelperQuest.TREE_RUN
)

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

	// Fruit Trees
	DetailedQuestStep farmingGuildFruitTreePatch, gnomeStrongholdFruitTreePatch, gnomeVillageFruitTreePatch,
		brimhavenFruitTreePatch, lletyaFruitTreePatch, catherbyFruitTreePatch;
	DetailedQuestStep farmingGuildFruitTreePatchPlant, gnomeStrongholdFruitTreePatchPlant, gnomeVillageFruitTreePatchPlant,
		brimhavenFruitTreePatchPlant, lletyaFruitTreePatchPlant, catherbyFruitTreePatchPlant;

	// Hardwood Trees
	DetailedQuestStep eastHardwoodTreePatch, westHardwoodTreePatch, middleHardwoodTreePatch;
	DetailedQuestStep eastHardwoodTreePatchPlant, westHardwoodTreePatchPlant, middleHardwoodTreePatchPlant;

	// Farming Items
	ItemRequirement coins, spade, rake, treeSapling, fruitTreeSapling, hardwoodSapling, compost;

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

	// Fruit Tree Requirements
	ManualRequirement gnomeStrongholdFruitTreePatchEmpty, gnomeVillageFruitTreePatchEmpty,
		brimhavenFruitTreePatchEmpty, lletyaFruitTreePatchEmpty, catherbyFruitTreePatchEmpty, farmingGuildFruitTreePatchEmpty;
	ManualRequirement gnomeStrongholdFruitTreePatchReady, gnomeVillageFruitTreePatchReady,
		brimhavenFruitTreePatchReady, lletyaFruitTreePatchReady, catherbyFruitTreePatchReady, farmingGuildFruitTreePatchReady;

	// Hardwood Tree Requirements
	ManualRequirement eastHardwoodTreePatchEmpty, westHardwoodTreePatchEmpty, middleHardwoodTreePatchEmpty;
	ManualRequirement eastHardwoodTreePatchReady, westHardwoodTreePatchReady, middleHardwoodTreePatchReady;

	private enum TreeSapling
	{
		OAK(ItemID.OAK_SAPLING), WILLOW(ItemID.WILLOW_SAPLING), MAPLE(ItemID.MAPLE_SAPLING), YEW(ItemID.YEW_SAPLING),
		MAGIC(ItemID.MAGIC_SAPLING);

		final int treeSaplingID;

		TreeSapling(int treeSaplingID)
		{
			this.treeSaplingID = treeSaplingID;
		}
	}

	private enum FruitTreeSapling
	{
		APPLE(ItemID.APPLE_SAPLING), BANANA(ItemID.BANANA_SAPLING), ORANGE(ItemID.ORANGE_SAPLING),
		CURRY(ItemID.CURRY_SAPLING), PINEAPPLE(ItemID.PINEAPPLE_SAPLING), PAPAYA(ItemID.PAPAYA_SAPLING),
		PALM(ItemID.PALM_SAPLING), DRAGONFRUIT(ItemID.DRAGONFRUIT_SAPLING);

		final int fruitTreeSaplingId;

		FruitTreeSapling(int fruitTreeSaplingId)
		{
			this.fruitTreeSaplingId = fruitTreeSaplingId;
		}
	}

	private enum HardwoodTreeSapling
	{
		TEAK(ItemID.TEAK_SAPLING),
		MAHOGANY(ItemID.MAHOGANY_SAPLING);

		final int hardwoodTreeSaplingId;

		HardwoodTreeSapling(int hardwoodTreeSaplingId)
		{
			this.hardwoodTreeSaplingId = hardwoodTreeSaplingId;
		}
	}

	private enum GracefulOrFarming {
		NONE(),
		GRACEFUL(),
		FARMING();
	}

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

		ConditionalStep steps = new ConditionalStep(this, waitForTree, spade, coins, rake, treeSapling,
			fruitTreeSapling, hardwoodSapling, farmersOutfit, gracefulOutfit);

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

	}

	@Override
	public void setupRequirements()
	{
		// Farming Item Requirements
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE)
			.hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2));
		coins = new ItemRequirement("Coins to quickly remove trees.", ItemID.COINS_995, -1);

		// TODO: Make a util function that can ingest the ItemRequirement and other required objects to make below 3 single function calls instead of the current repetition, should work for all farming helpers.
		// Saplings
		// Tree
		/*treeSapling = new ItemRequirement("Tree saplings of your choice", ItemID.OAK_SAPLING);

		String treeSaplingName = configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_BACKGROUND_GROUP, TREE_SAPLING
		);

		if (treeSaplingName != null)
		{
			try
			{
				treeSapling.setId(TreeSapling.valueOf(treeSaplingName).treeSaplingID);
			}
			catch (IllegalArgumentException e)
			{
				questHelperPlugin.getConfigManager().setRSProfileConfiguration(
					QuestHelperConfig.QUEST_BACKGROUND_GROUP, TREE_SAPLING, TreeSapling.OAK
				);
			}

			treeSapling.setName(Text.titleCase(TreeSapling.valueOf(treeSaplingName)) + " sapling");
		}
		else
		{
			questHelperPlugin.getConfigManager().setConfiguration(
				QuestHelperConfig.QUEST_BACKGROUND_GROUP, TREE_SAPLING, TreeSapling.OAK
			);
		}

		// Fruit Tree
		fruitTreeSapling = new ItemRequirement("Fruit tree saplings of your choice", ItemID.APPLE_SAPLING);

		String fruitTreeSaplingName = configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_BACKGROUND_GROUP, FRUIT_TREE_SAPLING
		);

		if (fruitTreeSaplingName != null)
		{
			try
			{
				fruitTreeSapling.setId(FruitTreeSapling.valueOf(fruitTreeSaplingName).fruitTreeSaplingId);
			}
			catch (IllegalArgumentException e)
			{
				questHelperPlugin.getConfigManager().setRSProfileConfiguration(
					QuestHelperConfig.QUEST_BACKGROUND_GROUP, FRUIT_TREE_SAPLING, FruitTreeSapling.APPLE
				);
			}

			fruitTreeSapling.setName(Text.titleCase(FruitTreeSapling.valueOf(fruitTreeSaplingName)) + "sapling");
		}
		else
		{
			questHelperPlugin.getConfigManager().setConfiguration(
				QuestHelperConfig.QUEST_BACKGROUND_GROUP, FRUIT_TREE_SAPLING, FruitTreeSapling.APPLE
			);
		}

		// Hardwood Tree
		hardwoodSapling = new ItemRequirement("Hardwood tree saplings of your choice", ItemID.TEAK_SAPLING);

		String hardwoodTreeSaplingName = configManager.getRSProfileConfiguration(
			QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_TREE_SAPLING
		);

		if (hardwoodTreeSaplingName != null)
		{
			try
			{
				hardwoodSapling.setId(HardwoodTreeSapling.valueOf(hardwoodTreeSaplingName).hardwoodTreeSaplingId);
			}
			catch (IllegalArgumentException e)
			{
				questHelperPlugin.getConfigManager().setRSProfileConfiguration(
					QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.TEAK
				);
			}

			hardwoodSapling.setName(Text.titleCase(HardwoodTreeSapling.valueOf(hardwoodTreeSaplingName)) + " sapling");
		}
		else
		{
			questHelperPlugin.getConfigManager().setConfiguration(
				QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.TEAK
			);
		}*/

		// Replace the existing code for treeSapling, fruitTreeSapling, and hardwoodSapling setup with the following:

		treeSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			TREE_SAPLING,
			TreeSapling.OAK,
			ItemID.OAK_SAPLING
		);

		fruitTreeSapling = FarmingUtils.createSeedRequirement(
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			FRUIT_TREE_SAPLING,
			FruitTreeSapling.APPLE,
			ItemID.APPLE_SAPLING
		);

		hardwoodSapling = FarmingUtils.createSeedRequirement (
			configManager,
			QuestHelperConfig.QUEST_BACKGROUND_GROUP,
			HARDWOOD_TREE_SAPLING,
			HardwoodTreeSapling.TEAK,
			ItemID.TEAK_SAPLING
		);

		compost	= new ItemRequirement("Compost", ItemCollections.COMPOST);

		// Teleport Items
		farmingGuildTeleport = new ItemRequirement("Farming Guild Teleport", ItemCollections.SKILLS_NECKLACES);

		// Graceful and Farming Outfit
		// TODO: Extract to FarmingUtils

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
		).isNotConsumed().showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, TreeRun.GracefulOrFarming.GRACEFUL.name()));

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
			farmingHat, farmingTop, farmingLegs, farmingBoots
		).isNotConsumed().showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, TreeRun.GracefulOrFarming.FARMING.name()));

	}

	private void setupSteps()
	{
		waitForTree = new DetailedQuestStep(this, "Wait for your trees to grow! This may take a while..!");

		// Tree Steps
		lumbridgeTreePatch = new ObjectStep(this, NullObjectID.NULL_8391, new WorldPoint(3192, 3230, 0),
			"Check the health of the tree planted in Lumbridge.");
		faladorTreePatch = new ObjectStep(this, NullObjectID.NULL_8389, new WorldPoint(3003, 3372, 0),
			"Check the health of the tree planted in Falador.");
		taverleyTreePatch = new ObjectStep(this, NullObjectID.NULL_8388, new WorldPoint(2935, 3437, 0),
			"Check the health of the tree planted in Taverley.");
		varrockTreePatch = new ObjectStep(this, NullObjectID.NULL_8390, new WorldPoint(3228, 3458, 0),
			"Check the health of the tree planted in Varrock.");
		gnomeStrongholdTreePatch = new ObjectStep(this, NullObjectID.NULL_19147, new WorldPoint(2435, 3414, 0),
			"Check the health of the tree planted in the Tree Gnome Stronghold.");

		farmingGuildTreePatch = new ObjectStep(this, NullObjectID.NULL_33732, new WorldPoint(1231, 3735, 0),
			"Check the health of the tree planted in the Farming Guild.");
		farmingGuildTreePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildTreePatch));



		// Fruit Tree Steps
		gnomeStrongholdFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7962, new WorldPoint(2475, 3445, 0),
			"Check the health of the fruit tree planted in the Tree Gnome Stronghold.");
		gnomeVillageFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7963, new WorldPoint(2489, 3179, 0),
			"Check the health of the fruit tree planted outside the Tree Gnome Village. Follow Elkoy to get out quickly.");
		brimhavenFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2764, 3212, 0),
			"Check the health of the fruit tree planted in Brimhaven.");
		catherbyFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_7965, new WorldPoint(2680, 3433, 0),
			"Check the health of the fruit tree planted in Catherby");

		lletyaFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_26579, new WorldPoint(2346, 3161, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaFruitTreePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToLletya));

		farmingGuildFruitTreePatch = new ObjectStep(this, NullObjectID.NULL_34007, new WorldPoint(1242, 3759, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildFruitTreePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildFruitTreePatch));


	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP)) {
			return;
		}

		FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, TREE_SAPLING, TreeSapling.class, this::updateTreeSapling, TreeSapling.OAK, configManager, questHelperPlugin);
		FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, FRUIT_TREE_SAPLING, FruitTreeSapling.class, this::updateFruitTreeSapling, FruitTreeSapling.APPLE, configManager, questHelperPlugin);
		FarmingConfigChangeHandler.handleFarmingEnumConfigChange(event, HARDWOOD_TREE_SAPLING, HardwoodTreeSapling.class, this::updateHardwoodTreeSapling, HardwoodTreeSapling.TEAK, configManager, questHelperPlugin);

		if (event.getKey().equals(GRACEFUL_OR_FARMING)) {
			questHelperPlugin.refreshBank();
		}
	}

	private void updateTreeSapling(TreeSapling selectedTreeSapling) {
		treeSapling.setId(selectedTreeSapling.treeSaplingID);
		treeSapling.setName(Text.titleCase(selectedTreeSapling) + " sapling");
	}

	private void updateFruitTreeSapling(FruitTreeSapling selectedFruitTreeSapling) {
		fruitTreeSapling.setId(selectedFruitTreeSapling.fruitTreeSaplingId);
		fruitTreeSapling.setName(Text.titleCase(selectedFruitTreeSapling) + " sapling");
	}

	private void updateHardwoodTreeSapling(HardwoodTreeSapling selectedHardwoodTreeSapling) {
		hardwoodSapling.setId(selectedHardwoodTreeSapling.hardwoodTreeSaplingId);
		hardwoodSapling.setName(Text.titleCase(selectedHardwoodTreeSapling) + " sapling");
	}

	@Override
	public List<HelperConfig> getConfigs()
	{
		HelperConfig treesConfig = new HelperConfig("Trees", TREE_SAPLING, TreeRun.TreeSapling.values());
		HelperConfig fruitTreesConfig = new HelperConfig("Fruit Trees", FRUIT_TREE_SAPLING, TreeRun.FruitTreeSapling.values());
		HelperConfig hardwoodTreesConfig = new HelperConfig("Hardwood Trees", HARDWOOD_TREE_SAPLING, TreeRun.HardwoodTreeSapling.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, TreeRun.GracefulOrFarming.values());
		return Arrays.asList(treesConfig, fruitTreesConfig, hardwoodTreesConfig, outfitConfig);
	}

}
