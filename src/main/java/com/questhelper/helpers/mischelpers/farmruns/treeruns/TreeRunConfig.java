package com.questhelper.helpers.mischelpers.farmruns.treeruns;

import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import lombok.Getter;
import net.runelite.client.config.ConfigManager;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;

@Getter
public class TreeRunConfig
{
	final static String TREE_SAPLING = "treeSaplings";
	final static String FRUIT_TREE_SAPLING = "fruitTreeSaplings";
	final static String HARDWOOD_TREE_SAPLING = "hardwoodTreeSaplings";
	final static String CALQUAT_TREE_SAPLING = "calquatTreeSaplings";

	private final ConfigManager configManager;

	private Requirement treesEnabled;
	private Requirement fruitTreesEnabled;
	private Requirement hardwoodEnabled;
	private Requirement calquatEnabled;

	private Requirement payingForRemoval;
	private Requirement payingForProtection;
	private Requirement usingCompostOrNothing;

	private FarmingUtils.FruitTreeSapling fruitTreeSapling;

	private final String PAY_OR_CUT = "payOrCutTree";
	private final String PAY_OR_COMPOST = "payOrCompostTree";

	public TreeRunConfig(ConfigManager configManager)
	{
		this.configManager = configManager;
		refreshConfig();
	}

	public void refreshConfig() {
		treesEnabled = not(
			new Conditions(
				new RuneliteRequirement(
					configManager,
					TREE_SAPLING,
					FarmingUtils.TreeSapling.NONE.name()
				)
			)
		);

		fruitTreesEnabled = not(
			new Conditions(
				new RuneliteRequirement(
					configManager,
					FRUIT_TREE_SAPLING,
					FarmingUtils.FruitTreeSapling.NONE.name()
				)
			)
		);

		fruitTreeSapling = (FarmingUtils.FruitTreeSapling) FarmingUtils.getEnumFromConfig(
				configManager,
				FarmingUtils.FruitTreeSapling.APPLE
			);

		hardwoodEnabled = not(
			new Conditions(
				new RuneliteRequirement(
					configManager,
					HARDWOOD_TREE_SAPLING,
					FarmingUtils.HardwoodTreeSapling.NONE.name()
				)
			)
		);

		calquatEnabled = not(
			new Conditions(
				new RuneliteRequirement(
					configManager,
					CALQUAT_TREE_SAPLING,
					FarmingUtils.CalquatTreeSapling.NONE.name()
				)
			)
		);

		payingForRemoval = new RuneliteRequirement(
			configManager,
			PAY_OR_CUT,
			FarmingUtils.PayOrCut.PAY.name()
		);

		payingForProtection = new RuneliteRequirement(
			configManager,
			PAY_OR_COMPOST,
			FarmingUtils.PayOrCompost.PAY.name()
		);

		usingCompostOrNothing = or(
			new RuneliteRequirement(
				configManager,
				PAY_OR_COMPOST,
				FarmingUtils.PayOrCompost.COMPOST.name()
			),
			new RuneliteRequirement(
				configManager,
				PAY_OR_COMPOST,
				FarmingUtils.PayOrCompost.NEITHER.name()
			)
		);
	}
}
