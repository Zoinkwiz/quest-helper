package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.QuestHelperConfig;
import com.questhelper.requirements.item.ItemRequirement;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.Text;

public class FarmingUtils
{

	/**
	 * Creates an ItemRequirement representing a seed requirement for farming-related tasks.
	 *
	 * This method allows for customization based on configuration settings or uses default values if necessary.
	 *
	 * @param configManager The RuneLite ConfigManager instance used to access configuration settings.
	 * @param configGroupName The name of the configuration group where seed settings are stored.
	 * @param seedConfigName The name of the specific seed configuration setting.
	 * @param defaultValue The default seed enum value.
	 * @param defaultItemId The default item ID associated with the seed.
	 * @return An ItemRequirement object representing the seed requirement.
	 */

	public static ItemRequirement createSeedRequirement(ConfigManager configManager, String configGroupName,
														String seedConfigName, Enum<?> defaultValue, int defaultItemId)
	{

		ItemRequirement seedRequirement = new ItemRequirement(Text.titleCase(defaultValue), defaultItemId);

		String seedName = configManager.getRSProfileConfiguration(configGroupName, seedConfigName);

		if (seedName != null)
		{
			try
			{
				Enum<?> seedEnum = Enum.valueOf(defaultValue.getDeclaringClass(), seedName);
				seedRequirement.setId(getSeedItemIdFromEnum(seedEnum));
				if (seedEnum instanceof HardwoodTreeSapling ||
					seedEnum instanceof FruitTreeSapling ||
					seedEnum instanceof TreeSapling)
				{
					seedRequirement.setName(Text.titleCase(seedEnum) + " sapling");
				}
				else
				{
					seedRequirement.setName(Text.titleCase(seedEnum) + " seed");
				}

			}
			catch (IllegalArgumentException e)
			{
				configManager.setRSProfileConfiguration(configGroupName, seedConfigName, defaultValue.name());
			}
		}
		else
		{
			configManager.setConfiguration(configGroupName, seedConfigName, defaultValue.name());
		}

		return seedRequirement;
	}

	public static int getSeedItemIdFromEnum(Enum<?> enumValue)
	{
		if (enumValue instanceof TreeSapling)
		{
			return ((TreeSapling) enumValue).treeSaplingID;
		}
		else if (enumValue instanceof FruitTreeSapling)
		{
			return ((FruitTreeSapling) enumValue).fruitTreeSaplingId;
		}
		else if (enumValue instanceof HardwoodTreeSapling)
		{
			return ((HardwoodTreeSapling) enumValue).hardwoodTreeSaplingId;
		}
		else
		{
			throw new IllegalArgumentException("Unsupported enum type");
		}
	}

	public static boolean getPatchState(Client client, int[] patchStates, int varbit)
	{
		for (int stateVarb : patchStates)
		{
			if (client.getVarbitValue(varbit) == stateVarb)
			{
				return true;
			}
		}

		return false;
	}

	public static String getEnumFromConfig(ConfigManager configManager, ConfigEnum configEnum)
	{
		try
		{
			return configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configEnum.getConfigKey());
		}
		catch (Exception err)
		{
			configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, configEnum.getDefault().getConfigKey(), configEnum.getDefault());
			return null;
		}
	}

	public interface ConfigEnum
	{
		String getConfigKey();

		ConfigEnum getDefault();
	}

	public interface PlantableItem extends ConfigEnum
	{
		int getPlantableItemId();

		int getProtectionItemId();

		int getProtectionItemQuantity();

		default ItemRequirement getPlantableItemRequirement(ItemManager itemManager)
		{
			return new ItemRequirement(itemManager.getItemComposition(getPlantableItemId()).getName(), getPlantableItemId());
		}

		default ItemRequirement getProtectionItemRequirement(ItemManager itemManager)
		{
			return new ItemRequirement(itemManager.getItemComposition(getProtectionItemId()).getName(), getProtectionItemId(), getProtectionItemQuantity());
		}
	}

	public enum TreeSapling implements PlantableItem
	{
		OAK(ItemID.OAK_SAPLING, ItemID.TOMATOES5, 1), WILLOW(ItemID.WILLOW_SAPLING, ItemID.APPLES5, 1),
		MAPLE(ItemID.MAPLE_SAPLING, ItemID.ORANGES5, 1), YEW(ItemID.YEW_SAPLING, ItemID.CACTUS_SPINE, 10),
		MAGIC(ItemID.MAGIC_SAPLING, ItemID.COCONUT, 25);

		final int treeSaplingID;
		final int protectionItemId;
		final int protectionItemQuantity;

		TreeSapling(int treeSaplingID, int protectionItemId, int protectionItemQuantity)
		{
			this.treeSaplingID = treeSaplingID;
			this.protectionItemId = protectionItemId;
			this.protectionItemQuantity = protectionItemQuantity;
		}

		@Override
		public String getConfigKey()
		{
			return "treeSaplings";
		}

		@Override
		public int getPlantableItemId()
		{
			return treeSaplingID;
		}

		@Override
		public int getProtectionItemId()
		{
			return protectionItemId;
		}

		@Override
		public int getProtectionItemQuantity()
		{
			return protectionItemQuantity;
		}

		@Override
		public ConfigEnum getDefault()
		{
			return TreeSapling.OAK;
		}
	}

	public enum FruitTreeSapling implements PlantableItem
	{
		APPLE(ItemID.APPLE_SAPLING, ItemID.SWEETCORN, 9), BANANA(ItemID.BANANA_SAPLING, ItemID.APPLES5, 4),
		ORANGE(ItemID.ORANGE_SAPLING, ItemID.STRAWBERRIES5, 3), CURRY(ItemID.CURRY_SAPLING, ItemID.BANANAS5, 5),
		PINEAPPLE(ItemID.PINEAPPLE_SAPLING, ItemID.WATERMELON, 10), PAPAYA(ItemID.PAPAYA_SAPLING, ItemID.PINEAPPLE, 10),
		PALM(ItemID.PALM_SAPLING, ItemID.PAPAYA_FRUIT, 15), DRAGONFRUIT(ItemID.DRAGONFRUIT_SAPLING, ItemID.COCONUT, 15);

		final int fruitTreeSaplingId;
		final int protectionItemId;
		final int protectionItemQuantity;

		FruitTreeSapling(int fruitTreeSaplingId, int protectionItemId, int protectionItemQuantity)
		{
			this.fruitTreeSaplingId = fruitTreeSaplingId;
			this.protectionItemId = protectionItemId;
			this.protectionItemQuantity = protectionItemQuantity;
		}

		@Override
		public String getConfigKey()
		{
			return "fruitTreeSaplings";
		}

		@Override
		public int getPlantableItemId()
		{
			return fruitTreeSaplingId;
		}

		@Override
		public int getProtectionItemId()
		{
			return protectionItemId;
		}

		@Override
		public int getProtectionItemQuantity()
		{
			return protectionItemQuantity;
		}

		@Override
		public ConfigEnum getDefault()
		{
			return FruitTreeSapling.APPLE;
		}
	}

	public enum HardwoodTreeSapling implements PlantableItem
	{
		TEAK(ItemID.TEAK_SAPLING, ItemID.LIMPWURT_ROOT, 15),
		MAHOGANY(ItemID.MAHOGANY_SAPLING, ItemID.YANILLIAN_HOPS, 25);

		final int hardwoodTreeSaplingId;
		final int protectionItemId;
		final int protectionItemQuantity;

		HardwoodTreeSapling(int hardwoodTreeSaplingId, int protectionItemId, int protectionItemQuantity)
		{
			this.hardwoodTreeSaplingId = hardwoodTreeSaplingId;
			this.protectionItemId = protectionItemId;
			this.protectionItemQuantity = protectionItemQuantity;
		}

		@Override
		public String getConfigKey()
		{
			return "hardwoodTreeSaplings";
		}

		@Override
		public int getPlantableItemId()
		{
			return hardwoodTreeSaplingId;
		}

		@Override
		public int getProtectionItemId()
		{
			return protectionItemId;
		}

		@Override
		public int getProtectionItemQuantity()
		{
			return protectionItemQuantity;
		}

		@Override
		public ConfigEnum getDefault()
		{
			return HardwoodTreeSapling.TEAK;
		}
	}

	public enum GracefulOrFarming
	{
		NONE(),
		GRACEFUL(),
		FARMING();
	}

	public enum PayOrCut
	{
		PAY(),
		DIG();
	}

	public enum PayOrCompost
	{
		PAY(),
		COMPOST(),
		NEITHER();
	}
}

