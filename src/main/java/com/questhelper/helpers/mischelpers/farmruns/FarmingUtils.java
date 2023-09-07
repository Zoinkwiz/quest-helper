package com.questhelper.helpers.mischelpers.farmruns;

import com.questhelper.requirements.item.ItemRequirement;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
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
				System.out.println("Found patch state: " + client.getVarbitValue(varbit) + " " + stateVarb);
				return true;
			}
		}

		return false;
	}

	public enum TreeSapling
	{
		OAK(ItemID.OAK_SAPLING), WILLOW(ItemID.WILLOW_SAPLING), MAPLE(ItemID.MAPLE_SAPLING), YEW(ItemID.YEW_SAPLING),
		MAGIC(ItemID.MAGIC_SAPLING);

		final int treeSaplingID;

		TreeSapling(int treeSaplingID)
		{
			this.treeSaplingID = treeSaplingID;
		}
	}

	public enum FruitTreeSapling
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

	public enum HardwoodTreeSapling
	{
		TEAK(ItemID.TEAK_SAPLING),
		MAHOGANY(ItemID.MAHOGANY_SAPLING);

		final int hardwoodTreeSaplingId;

		HardwoodTreeSapling(int hardwoodTreeSaplingId)
		{
			this.hardwoodTreeSaplingId = hardwoodTreeSaplingId;
		}
	}

	public enum GracefulOrFarming
	{
		NONE(),
		GRACEFUL(),
		FARMING();
	}
}

