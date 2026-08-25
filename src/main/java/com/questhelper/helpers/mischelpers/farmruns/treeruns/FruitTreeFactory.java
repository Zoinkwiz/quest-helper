package com.questhelper.helpers.mischelpers.farmruns.treeruns;

import com.questhelper.helpers.mischelpers.farmruns.FarmingHandler;
import com.questhelper.helpers.mischelpers.farmruns.FarmingUtils;
import com.questhelper.helpers.mischelpers.farmruns.FarmingWorld;
import com.questhelper.helpers.mischelpers.farmruns.PatchImplementation;
import com.questhelper.helpers.mischelpers.farmruns.PatchStates;
import com.questhelper.helpers.mischelpers.farmruns.PaymentTracker;
import com.questhelper.helpers.mischelpers.farmruns.TreeRun;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.widget.LunarSpells;
import com.questhelper.steps.widget.NormalSpells;
import com.questhelper.steps.widget.WidgetHighlight;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.Tab;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import static com.questhelper.helpers.mischelpers.farmruns.treeruns.TreeRunConfig.FRUIT_TREE_SAPLING;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import static com.questhelper.requirements.util.LogicHelper.or;

public class FruitTreeFactory extends TreeFactory<FarmingUtils.FruitTreeSapling>
{
	private ItemRequirement sapling;
	@Getter
	private ItemRequirement allSaplings;
	private ItemRequirement protectionItem;
	@Getter
	private ItemRequirement allProtectionItems;

	// Farming Guild
	private Requirement accessToFarmingGuild;
	private DetailedQuestStep farmingGuildCheckHealth, farmingGuildPlant, farmingGuildClear, farmingGuildDig, farmingGuildCutDown, farmingGuildProtect;
	@Getter
	private ConditionalStep farmingGuildStep;
	private PatchStates farmingGuildStates;

	// Gnome Stronghold
	private DetailedQuestStep gnomeStrongholdCheckHealth, gnomeStrongholdPlant, gnomeStrongholdClear, gnomeStrongholdDig, gnomeStrongholdCutDown, gnomeStrongholdProtect;
	@Getter
	private ConditionalStep gnomeStrongholdStep;
	private PatchStates gnomeStrongholdStates;

	// Tree Gnome Village
	private DetailedQuestStep gnomeVillageCheckHealth, gnomeVillagePlant, gnomeVillageClear, gnomeVillageDig, gnomeVillageCutDown, gnomeVillageProtect;
	@Getter
	private ConditionalStep gnomeVillageStep;
	private PatchStates gnomeVillageStates;

	// Brimhaven
	private DetailedQuestStep brimhavenCheckHealth, brimhavenPlant, brimhavenClear, brimhavenDig, brimhavenCutDown, brimhavenProtect;
	@Getter
	private ConditionalStep brimhavenStep;
	private PatchStates brimhavenStates;

	// Lletya
	private Requirement accessToLletya;
	private DetailedQuestStep lletyaCheckHealth, lletyaPlant, lletyaClear, lletyaDig, lletyaCutDown, lletyaProtect;
	@Getter
	private ConditionalStep lletyaStep;
	private PatchStates lletyaStates;

	// Catherby
	private DetailedQuestStep catherbyCheckHealth, catherbyPlant, catherbyClear, catherbyDig, catherbyCutDown, catherbyProtect;
	@Getter
	private ConditionalStep catherbyStep;
	private PatchStates catherbyStates;

	// Kastori
	private Requirement accessToVarlamore;
	private DetailedQuestStep kastoriCheckHealth, kastoriPlant, kastoriClear, kastoriDig, kastoriCutDown, kastoriProtect;
	@Getter
	private ConditionalStep kastoriStep;
	private PatchStates kastoriStates;

	private static FruitTreeFactory instance;

	private FruitTreeFactory(
		TreeRun treeRun,
		TreeRunTeleports teleports,
		TreeRunConfig config,
		TreeRunItems items,
		ItemManager itemManager)
	{
		super(treeRun, teleports, config, items, itemManager);
	}

	public static FruitTreeFactory getInstance(
		TreeRun treeRun,
		TreeRunTeleports teleports,
		TreeRunConfig config,
		TreeRunItems items,
		ItemManager itemManager)
	{
		if (instance == null)
		{
			instance = new FruitTreeFactory(
				treeRun,
				teleports,
				config,
				items,
				itemManager
			);
		}

		return instance;
	}

	@Override
	protected void setupRequirements()
	{
		// Create fruit tree requirements
		accessToFarmingGuild = new Conditions(new SkillRequirement(Skill.FARMING, 85));
		accessToLletya = new QuestRequirement(QuestHelperQuest.MOURNINGS_END_PART_I, QuestState.FINISHED);
		accessToVarlamore = new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);

		farmingGuildStates = new PatchStates("Farming Guild", and(accessToFarmingGuild, config.getFruitTreesEnabled()));
		gnomeStrongholdStates = new PatchStates("Gnome Stronghold", config.getFruitTreesEnabled());
		gnomeVillageStates = new PatchStates("Tree Gnome Village", config.getFruitTreesEnabled());
		brimhavenStates = new PatchStates("Brimhaven", config.getFruitTreesEnabled());
		lletyaStates = new PatchStates("Lletya", and(accessToLletya, config.getFruitTreesEnabled()));
		catherbyStates = new PatchStates("Catherby", config.getFruitTreesEnabled());
		kastoriStates = new PatchStates("Kastori", and(accessToVarlamore, config.getFruitTreesEnabled()));

		sapling = config.getFruitTreeSapling()
			.getPlantableItemRequirement(itemManager)
			.showConditioned(config.getFruitTreesEnabled());
		sapling.setHighlightInInventory(true);
		allSaplings = sapling.copy();

		protectionItem = config.getFruitTreeSapling()
			.getProtectionItemRequirement(itemManager)
			.showConditioned(and(config.getPayingForProtection(), config.getFruitTreesEnabled()));
		protectionItem.addAlternates(protectionItem.getId() + 1);
		allProtectionItems = protectionItem.copy();
	}

	@Override
	protected void setupSteps()
	{
		// Create individual fruit tree steps
		setupFarmingGuildSteps();
		setupGnomeStrongholdSteps();
		setupGnomeVillageSteps();
		setupBrimhavenSteps();
		setupLletyaSteps();
		setupCatherbySteps();
		setupKastoriSteps();
	}

	private void setupFarmingGuildSteps()
	{
		farmingGuildPlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Plant your sapling in the Farming Guild patch.", sapling);
		farmingGuildPlant.addIcon(sapling.getId());
		farmingGuildPlant.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToFarmingGuild)));

		farmingGuildCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Check the health of the fruit tree planted in the Farming Guild.");
		farmingGuildCheckHealth.addTeleport(teleports.getFarmingGuildTeleport());
		farmingGuildCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Farming Guild", true);
		farmingGuildCheckHealth.addSubSteps(farmingGuildPlant);
		farmingGuildCheckHealth.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToFarmingGuild)));

		farmingGuildCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Cut down the fruit tree planted in the Farming Guild.", items.getAxe());
		farmingGuildCutDown.addTeleport(teleports.getFarmingGuildTeleport());
		farmingGuildCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Farming Guild", true);
		farmingGuildCutDown.conditionToHideInSidebar(
			or(not(config.getFruitTreesEnabled()), not(accessToFarmingGuild), config.getPayingForRemoval()));

		farmingGuildDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_6, new WorldPoint(1242, 3758, 0),
			"Dig up the fruit tree's stump in the Farming Guild.");
		farmingGuildDig.conditionToHideInSidebar(
			or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval(), not(accessToFarmingGuild)));

		farmingGuildClear = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FARMGUILD_T3, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		farmingGuildClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		farmingGuildClear.addDialogSteps(
			"Would you chop my tree down for me?",
			"I can't be bothered - I'd rather pay you to do it.",
			"Here's 200 Coins - chop my tree down please.",
			"Yes.");
		farmingGuildClear.addSubSteps(farmingGuildDig);

		farmingGuildProtect = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FARMGUILD_T3, new WorldPoint(1243, 3760, 0),
			"Pay Nikkie to protect the patch.");
		farmingGuildProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		farmingGuildProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	private void setupGnomeStrongholdSteps()
	{
		gnomeStrongholdPlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Plant your sapling in the Tree Gnome Stronghold patch.", sapling);
		gnomeStrongholdPlant.addIcon(sapling.getId());
		gnomeStrongholdPlant.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		gnomeStrongholdCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Check the health of the fruit tree planted in the Tree Gnome Stronghold.");
		gnomeStrongholdCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Gnome Stronghold", true);
		gnomeStrongholdCheckHealth.addSubSteps(gnomeStrongholdPlant);
		gnomeStrongholdCheckHealth.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		gnomeStrongholdCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Cut down the fruit tree planted in the Tree Gnome Stronghold.", items.getAxe());
		gnomeStrongholdCutDown.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));
		gnomeStrongholdCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Gnome Stronghold", true);

		gnomeStrongholdDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_1, new WorldPoint(2476, 3446, 0),
			"Dig up the fruit tree's stump in the Tree Gnome Stronghold.");
		gnomeStrongholdDig.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));

		gnomeStrongholdClear = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_1, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeStrongholdClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		gnomeStrongholdClear.addDialogSteps("Would you chop my tree down for me?","I can't be bothered - I'd rather pay you to do it.", "Here's 200 Coins - chop my tree down please.", "Yes.");
		gnomeStrongholdClear.addSubSteps(gnomeStrongholdDig);

		gnomeStrongholdProtect = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_1, new WorldPoint(2476, 3446, 0),
			"Pay Bolongo to protect the patch.");
		gnomeStrongholdProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		gnomeStrongholdProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	private void setupGnomeVillageSteps()
	{
		gnomeVillagePlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Plant your sapling in the Tree Gnome Village patch. Follow Elkoy to get out quickly.", sapling);
		gnomeVillagePlant.addIcon(sapling.getId());
		gnomeVillagePlant.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		gnomeVillageCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Check the health of the fruit tree planted outside the Tree Gnome Village. Follow Elkoy to get out quickly.");
		gnomeVillageCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Tree Gnome Village", true);
		gnomeVillageCheckHealth.addSubSteps(gnomeVillagePlant);
		gnomeVillageCheckHealth.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		gnomeVillageCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Cut down the fruit tree planted outside the Tree Gnome Village.", items.getAxe());
		gnomeVillageCutDown.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));
		gnomeVillageCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Tree Gnome Village", true);

		gnomeVillageDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_2, new WorldPoint(2490, 3180, 0),
			"Dig up the fruit tree's stump outside the Tree Gnome Village.");
		gnomeVillageDig.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));

		gnomeVillageClear = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_2, new WorldPoint(2490, 3180, 0),
			"Pay Gileth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		gnomeVillageClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		gnomeVillageClear.addDialogSteps("Would you chop my tree down for me?",
			"I can't be bothered - I'd rather pay you to do it.",
			"Here's 200 Coins - chop my tree down please.", "Yes.");
		gnomeVillageClear.addSubSteps(gnomeVillageDig);

		gnomeVillageProtect = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_2, new WorldPoint(2490, 3180, 0),
			"Pay Gileth to protect the patch.");
		gnomeVillageProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		gnomeVillageProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	private void setupBrimhavenSteps()
	{
		brimhavenPlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Plant your sapling in the Brimhaven patch.", sapling);
		brimhavenPlant.addIcon(sapling.getId());
		brimhavenPlant.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		brimhavenCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Check the health of the fruit tree planted in Brimhaven.");
		brimhavenCheckHealth.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenCheckHealth.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenCheckHealth.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));
		brimhavenCheckHealth.addSubSteps(brimhavenPlant);
		brimhavenCheckHealth.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		brimhavenCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Cut down the fruit tree planted in Brimhaven.", items.getAxe());
		brimhavenCutDown.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));
		brimhavenCutDown.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenCutDown.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenCutDown.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));

		brimhavenDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_3, new WorldPoint(2765, 3213, 0),
			"Dig up the fruit tree's stump in Brimhaven.");
		brimhavenDig.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));

		brimhavenClear = new NpcStep(treeRun, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		brimhavenClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		brimhavenClear.addWidgetHighlightWithTextRequirement(InterfaceID.MENU, InterfaceID.Menu.LJ_LAYER1 & 0xFFFF, "Brimhaven", true);
		brimhavenClear.addWidgetHighlightWithTextRequirement(InterfaceID.CHARTERING_MENU_SIDE, InterfaceID.CharteringMenuSide.LIST_CONTENT & 0xFFFF, "Brimhaven", true);
		brimhavenClear.addWidgetHighlight(new WidgetHighlight(InterfaceID.SAILING_MENU, InterfaceID.SailingMenu.CONTENT & 0xFFFF, 2));
		brimhavenClear.addDialogSteps("Would you chop my tree down for me?",
			"I can't be bothered - I'd rather pay you to do it.",
			"Here's 200 Coins - chop my tree down please.", "Yes.");
		brimhavenClear.addSubSteps(brimhavenDig);

		brimhavenProtect = new NpcStep(treeRun, NpcID.GARTH, new WorldPoint(2765, 3213, 0),
			"Pay Garth to protect the patch.");
		brimhavenProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		brimhavenProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	private void setupLletyaSteps()
	{
		lletyaPlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Plant your sapling in the Lletya patch.", sapling);
		lletyaPlant.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToLletya)));
		lletyaPlant.addIcon(sapling.getId());

		lletyaCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Check the health of the fruit tree planted in Lletya.");
		lletyaCheckHealth.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToLletya)));
		lletyaCheckHealth.addTeleport(teleports.getCrystalTeleport());
		lletyaCheckHealth.addSubSteps(lletyaPlant);

		lletyaCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Cut down the fruit tree planted in Lletya.", items.getAxe());
		lletyaCutDown.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToLletya), config.getPayingForRemoval()));
		lletyaCutDown.addTeleport(teleports.getCrystalTeleport());

		lletyaDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_5, new WorldPoint(2347, 3162, 0),
			"Dig up the fruit tree's stump in Lletya.");
		lletyaDig.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval(), not(accessToLletya)));

		lletyaClear = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_TREE_5, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		lletyaClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		lletyaClear.addDialogSteps("Would you chop my tree down for me?",
			"I can't be bothered - I'd rather pay you to do it.",
			"Here's 200 Coins - chop my tree down please.", "Yes.");
		lletyaClear.addSubSteps(lletyaDig);

		lletyaProtect = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_TREE_5, new WorldPoint(2347, 3162, 0),
			"Pay Liliwen to protect the patch.");
		lletyaProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		lletyaProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	private void setupCatherbySteps()
	{
		catherbyPlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Plant your sapling in the Catherby patch.", sapling);
		catherbyPlant.addIcon(sapling.getId());
		catherbyPlant.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		catherbyCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby.");
		catherbyCheckHealth.addTeleport(teleports.getCatherbyTeleport());
		catherbyCheckHealth.addSpellHighlight(NormalSpells.CAMELOT_TELEPORT);
		catherbyCheckHealth.addSpellHighlight(LunarSpells.CATHERBY_TELEPORT);
		catherbyCheckHealth.addSubSteps(catherbyPlant);
		catherbyCheckHealth.conditionToHideInSidebar(not(config.getFruitTreesEnabled()));

		catherbyCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Cut down the fruit tree planted in Catherby.", items.getAxe());
		catherbyCutDown.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));
		catherbyCutDown.addTeleport(teleports.getCatherbyTeleport());
		catherbyCutDown.addSpellHighlight(NormalSpells.CAMELOT_TELEPORT);
		catherbyCutDown.addSpellHighlight(LunarSpells.CATHERBY_TELEPORT);

		catherbyDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_4, new WorldPoint(2860, 3433, 0),
			"Check the health of the fruit tree planted in Catherby");
		catherbyDig.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval()));

		catherbyClear = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_4, new WorldPoint(2860, 3433, 0),
			"Pay Ellena 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		catherbyClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		catherbyClear.addDialogSteps("Would you chop my tree down for me?",
			"I can't be bothered - I'd rather pay you to do it.",
			"Here's 200 Coins - chop my tree down please.", "Yes.");
		catherbyClear.addSubSteps(catherbyDig);

		catherbyProtect = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_4, new WorldPoint(2860, 3433, 0),
			"Pay Ellena to protect the patch.");
		catherbyProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		catherbyProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	private void setupKastoriSteps()
	{
		kastoriPlant = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Plant your sapling in the Kastori patch.", sapling);
		kastoriPlant.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToVarlamore)));
		kastoriPlant.addIcon(sapling.getId());

		kastoriCheckHealth = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Check the health of the fruit tree planted in Kastori.");
		kastoriCheckHealth.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToVarlamore)));
		kastoriCheckHealth.addTeleport(teleports.getKastoriTeleport());
		kastoriCheckHealth.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);
		kastoriCheckHealth.addSubSteps(kastoriPlant);

		kastoriCutDown = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Cut down the fruit tree planted in Kastori.", items.getAxe());
		kastoriCutDown.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(accessToVarlamore), config.getPayingForRemoval()));
		kastoriCutDown.addTeleport(teleports.getKastoriTeleport());
		kastoriCutDown.addWidgetHighlightWithTextRequirement(187, 3, "Kastori", true);

		kastoriDig = new ObjectStep(treeRun, ObjectID.FARMING_FRUIT_TREE_PATCH_7, new WorldPoint(1350, 3057, 0),
			"Dig up the fruit tree's stump in Kastori.");
		kastoriDig.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), config.getPayingForRemoval(), not(accessToVarlamore)));

		kastoriClear = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_7, new WorldPoint(1350, 3057, 0),
			"Pay Ehecatl 200 coins to clear the fruit tree, or pick all the fruit and cut it down.");
		kastoriClear.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForRemoval())));
		kastoriClear.addDialogSteps("Would you chop my tree down for me?",
			"I can't be bothered - I'd rather pay you to do it.",
			"Here's 200 Coins - chop my tree down please.", "Yes.");
		kastoriClear.addSubSteps(kastoriDig);

		kastoriProtect = new NpcStep(treeRun, NpcID.FARMING_GARDENER_FRUIT_7, new WorldPoint(1350, 3057, 0),
			"Pay Ehecatl to protect the patch.");
		kastoriProtect.conditionToHideInSidebar(or(not(config.getFruitTreesEnabled()), not(config.getPayingForProtection())));
		kastoriProtect.addDialogSteps(TREE_PROTECTION_DIALOG);
	}

	@Override
	protected void setupConditionalSteps()
	{
		// Create conditional fruit tree steps
		farmingGuildStep = createTreeConditionalStep(
			farmingGuildStates, farmingGuildCheckHealth, farmingGuildCutDown, farmingGuildClear, farmingGuildDig, farmingGuildPlant, farmingGuildProtect,
			accessToFarmingGuild,
			false,-2);
		gnomeStrongholdStep = createTreeConditionalStep(
			gnomeStrongholdStates, gnomeStrongholdCheckHealth, gnomeStrongholdCutDown, gnomeStrongholdClear, gnomeStrongholdDig, gnomeStrongholdPlant, gnomeStrongholdProtect,
			true,51);
		gnomeVillageStep = createTreeConditionalStep(
			gnomeVillageStates, gnomeVillageCheckHealth, gnomeVillageCutDown, gnomeVillageClear, gnomeVillageDig, gnomeVillagePlant, gnomeVillageProtect,
			true,
			6);
		brimhavenStep = createTreeConditionalStep(
			brimhavenStates, brimhavenCheckHealth, brimhavenCutDown, brimhavenClear, brimhavenDig, brimhavenPlant, brimhavenProtect,
			true, 81);
		lletyaStep = createTreeConditionalStep(
			lletyaStates, lletyaCheckHealth, lletyaCutDown, lletyaClear, lletyaDig, lletyaPlant, lletyaProtect,
			accessToLletya,
			true, 9);
		catherbyStep = createTreeConditionalStep(
			catherbyStates, catherbyCheckHealth, catherbyCutDown, catherbyClear, catherbyDig, catherbyPlant, catherbyProtect,
			true, 7);
		kastoriStep = createTreeConditionalStep(
			kastoriStates, kastoriCheckHealth, kastoriCutDown, kastoriClear, kastoriDig, kastoriPlant, kastoriProtect,
			accessToVarlamore,
			true, 131);
	}

	public Conditions isFarmingGuildActionable()
	{
		return and(accessToFarmingGuild, not(farmingGuildStates.getIsGrowing()));
	}

	public Conditions isGnomeStrongholdActionable()
	{
		return not(gnomeStrongholdStates.getIsGrowing());
	}

	public Conditions isGnomeVillageActionable()
	{
		return not(gnomeVillageStates.getIsGrowing());
	}

	public Conditions isBrimhavenActionable()
	{
		return not(brimhavenStates.getIsGrowing());
	}

	public Conditions isLletyaActionable()
	{
		return and(accessToLletya, not(lletyaStates.getIsGrowing()));
	}

	public Conditions isCatherbyActionable()
	{
		return not(catherbyStates.getIsGrowing());
	}

	public Conditions isKastoriActionable()
	{
		return and(accessToVarlamore, not(kastoriStates.getIsGrowing()));
	}

	@Override
	public Conditions isAllGrowing() {
		return or(not(config.getFruitTreesEnabled()),
			and(catherbyStates.getIsGrowing(),
				brimhavenStates.getIsGrowing(),
				gnomeVillageStates.getIsGrowing(),
				gnomeStrongholdStates.getIsGrowing(),
				or(not(accessToLletya), lletyaStates.getIsGrowing()),
				or(not(accessToVarlamore), kastoriStates.getIsGrowing()),
				or(not(accessToFarmingGuild), farmingGuildStates.getIsGrowing())));
	}

	@Override
	public void onGameTick(GameTick event, FarmingWorld farmingWorld, FarmingHandler handler, Client client, PaymentTracker paymentTracker)
	{
		config.refreshConfig();
		allProtectionItems.setQuantity(protectionItem.getQuantity());
		handleTreePatches(paymentTracker, client, handler, PatchImplementation.FRUIT_TREE,
			List.of(farmingGuildStates, brimhavenStates, catherbyStates, gnomeStrongholdStates, gnomeVillageStates, lletyaStates,
				kastoriStates),
			farmingWorld.getTabs().get(Tab.FRUIT_TREE), allSaplings, allProtectionItems);
	}

	@Override
	public void updateSapling(FarmingUtils.FruitTreeSapling selectedSapling)
	{
		sapling.setId(selectedSapling.getPlantableItemId());
		sapling.setName(itemManager.getItemComposition(selectedSapling.getPlantableItemId()).getName());

		allSaplings.setId(selectedSapling.getPlantableItemId());
		allSaplings.setName(itemManager.getItemComposition(selectedSapling.getPlantableItemId()).getName());
		updatePaymentItem(selectedSapling);
	}

	private void updatePaymentItem(FarmingUtils.FruitTreeSapling treeSapling)
	{
		protectionItem.setId(treeSapling.getProtectionItemId());
		protectionItem.setName(itemManager.getItemComposition(treeSapling.getProtectionItemId()).getName());
		protectionItem.setQuantity(treeSapling.getProtectionItemQuantity());

		allProtectionItems.setId(treeSapling.getProtectionItemId());
		allProtectionItems.setName(itemManager.getItemComposition(treeSapling.getProtectionItemId()).getName());
		allProtectionItems.setQuantity(treeSapling.getProtectionItemQuantity());
	}

	@Override
	public HelperConfig getConfig()
	{
		return new HelperConfig("Fruit Trees", FRUIT_TREE_SAPLING, FarmingUtils.FruitTreeSapling.values());
	}

	public List<PanelDetails> getPanelDetails()
	{
		return List.of(
			getGnomeVillagePanel(),
			getCatherbyPanel(),
			getLletyaPanel()
		);
	}

	public Map<MultiLevelPanel, Pair<PanelDetails, Supplier<Conditions>>> getSubPanelDetails()
	{
		return Map.of(
			MultiLevelPanel.FARMING_GUILD, Pair.of(getFarmingGuildPanel(), this::shouldHideTopLevelFarmingGuildPanel),
			MultiLevelPanel.GNOME_STRONGHOLD, Pair.of(getGnomeStrongholdPanel(), this::shouldHideTopLevelGnomeStrongholdPanel),
			MultiLevelPanel.KARAMJA, Pair.of(getBrimhavenPanel(), this::shouldHideTopLevelBrimhavenPanel),
			MultiLevelPanel.KASTORI, Pair.of(getKastoriPanel(), this::shouldHideTopLevelKastoriPanel)
		);
	}

	private PanelDetails getFarmingGuildPanel()
	{
		PanelDetails panel = new PanelDetails(
			"Fruit Tree Patch",
			Arrays.asList(
				farmingGuildCheckHealth,
				farmingGuildCutDown,
				farmingGuildDig,
				farmingGuildClear,
				farmingGuildPlant,
				farmingGuildProtect
			)
		).withId(-2);

		panel.setLockingStep(farmingGuildStep);
		panel.setHideCondition(
			or(
				not(config.getFruitTreesEnabled()),
				not(accessToFarmingGuild)
			)
		);

		return panel;
	}

	private PanelDetails getGnomeStrongholdPanel()
	{

		PanelDetails panel = new PanelDetails("Fruit Tree Patch",
			Arrays.asList(
				gnomeStrongholdCheckHealth,
				gnomeStrongholdCutDown,
				gnomeStrongholdDig,
				gnomeStrongholdClear,
				gnomeStrongholdPlant,
				gnomeStrongholdProtect))
			.withId(51);
		panel.setLockingStep(gnomeStrongholdStep);
		panel.setHideCondition(not(config.getFruitTreesEnabled()));

		return panel;
	}

	private PanelDetails getGnomeVillagePanel()
	{
		PanelDetails panel = new PanelDetails(
			"Tree Gnome Village",
			Arrays.asList(
				gnomeVillageCheckHealth,
				gnomeVillageCutDown,
				gnomeVillageDig,
				gnomeVillageClear,
				gnomeVillagePlant,
				gnomeVillageProtect
			)
		).withId(6);

		panel.setLockingStep(gnomeVillageStep);
		panel.setHideCondition(not(config.getFruitTreesEnabled()));

		return panel;
	}

	private PanelDetails getCatherbyPanel()
	{
		PanelDetails panel = new PanelDetails(
			"Catherby",
			Arrays.asList(
				catherbyCheckHealth,
				catherbyCutDown,
				catherbyDig,
				catherbyClear,
				catherbyPlant,
				catherbyProtect
			)
		).withId(7);

		panel.setLockingStep(catherbyStep);
		panel.setHideCondition(not(config.getFruitTreesEnabled()));

		return panel;
	}

	private PanelDetails getBrimhavenPanel()
	{
		PanelDetails panel = new PanelDetails(
			"Brimhaven",
			Arrays.asList(
				brimhavenCheckHealth,
				brimhavenCutDown,
				brimhavenDig,
				brimhavenClear,
				brimhavenPlant,
				brimhavenProtect
			)
		).withId(81);

		panel.setLockingStep(brimhavenStep);
		panel.setHideCondition(not(config.getFruitTreesEnabled()));

		return panel;
	}

	private PanelDetails getLletyaPanel()
	{
		PanelDetails panel = new PanelDetails(
			"Lletya",
			Arrays.asList(
				lletyaCheckHealth,
				lletyaCutDown,
				lletyaDig,
				lletyaClear,
				lletyaPlant,
				lletyaProtect
			)
		).withId(9);

		panel.setLockingStep(lletyaStep);
		panel.setHideCondition(
			or(
				not(config.getFruitTreesEnabled()),
				not(accessToLletya)
			)
		);

		return panel;
	}

	private PanelDetails getKastoriPanel()
	{
		PanelDetails panel = new PanelDetails(
			"Fruit Tree Patch",
			Arrays.asList(
				kastoriCheckHealth,
				kastoriCutDown,
				kastoriDig,
				kastoriClear,
				kastoriPlant,
				kastoriProtect
			)
		).withId(131);

		panel.setLockingStep(kastoriStep);
		panel.setHideCondition(
			or(
				not(config.getFruitTreesEnabled()),
				not(accessToVarlamore)
			)
		);

		return panel;
	}

	private Conditions shouldHideTopLevelFarmingGuildPanel()
	{
		return or(not(config.getFruitTreesEnabled()), not(accessToFarmingGuild));
	}

	private Conditions shouldHideTopLevelGnomeStrongholdPanel()
	{
		return not(config.getFruitTreesEnabled());
	}

	private Conditions shouldHideTopLevelBrimhavenPanel()
	{
		return not(config.getFruitTreesEnabled());
	}

	private Conditions shouldHideTopLevelKastoriPanel()
	{
		return or(not(config.getFruitTreesEnabled()), not(accessToVarlamore));
	}

}
