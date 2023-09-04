package com.questhelper.helpers.mischelpers.farmruns;

import com.google.inject.Inject;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperConfig;
import com.questhelper.QuestHelperQuest;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.Favour;
import com.questhelper.requirements.player.FavourRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.client.util.Text;
import sun.reflect.generics.tree.Tree;

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
	DetailedQuestStep farmingGuildTreePatchPlant, lumbridgeTreePatchPlant, faladorTreePatchPlant, taverlyTreePatchPlant,
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

	// Graceful Set
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;

	// Farming Set
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	// Access Requirements
	Requirement accessToFarmingGuildTreePatch, accessToFarmingGuildFruitTreePatch, accessToLletya, accessToFossilIsland;

	// Tree Requirements
	ManualRequirement  lumbridgeTreePatchEmpty, faladorTreePatchEmpty, taverlyTreePatchEmpty,
		varrockTreePatchEmpty, gnomeStrongholdTreePatchEmpty, farmingGuildTreePatchEmpty;
	ManualRequirement  lumbridgeTreePatchReady, faladorTreePatchReady, taverlyTreePatchReady,
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
		taverlyTreePatchReady = new ManualRequirement();
		varrockTreePatchReady = new ManualRequirement();
		gnomeStrongholdTreePatchReady = new ManualRequirement();
		farmingGuildTreePatchReady = new ManualRequirement();

		// Tree Patch Empty Requirements
		lumbridgeTreePatchEmpty = new ManualRequirement();
		faladorTreePatchEmpty = new ManualRequirement();
		taverlyTreePatchEmpty = new ManualRequirement();
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


	}

	private void setupSteps()
	{
	}


}
