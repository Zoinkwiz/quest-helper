/*
 * Copyright (c) 2023, Zoinkwiz
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

import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.config.ConfigKeys;
import com.questhelper.panel.PanelDetails;
import com.questhelper.panel.TopLevelPanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.questhelper.requirements.util.LogicHelper.nor;

public class HerbRun extends ComplexStateQuestHelper
{
	@Inject
	private FarmingWorld farmingWorld;

	private FarmingHandler farmingHandler;

	// Required items
	ItemRequirement spade;
	ItemRequirement dibber;
	ItemRequirement rake;
	ItemRequirement seed;

	// Recommended items
	ItemRequirement compost;
	ItemRequirement ectophial;
	ItemRequirement magicSec;
	ItemRequirement explorerRing2;
	ItemRequirement ardyCloak2;
	ItemRequirement xericsTalisman;
	ItemRequirement catherbyTeleport;
	ItemRequirement trollheimTeleport;
	ItemRequirement icyBasalt;
	ItemRequirement stonyBasalt;
	ItemRequirement farmingGuildTeleport;
	ItemRequirement hosidiusHouseTeleport;
	ItemRequirement hunterWhistle;
	ItemRequirement harmonyTeleport;
	ItemRequirement gracefulHood;
	ItemRequirement gracefulTop;
	ItemRequirement gracefulLegs;
	ItemRequirement gracefulGloves;
	ItemRequirement gracefulBoots;
	ItemRequirement gracefulCape;
	ItemRequirement gracefulOutfit;
	ItemRequirement farmingHat;
	ItemRequirement farmingTop;
	ItemRequirement farmingLegs;
	ItemRequirement farmingBoots;
	ItemRequirement farmersOutfit;

	// Miscellaneous requirements
	QuestRequirement accessToHarmony;
	QuestRequirement accessToWeiss;
	QuestRequirement accessToTrollStronghold;
	SkillRequirement accessToFarmingGuildPatch;
	QuestRequirement accessToVarlamore;
	RuneliteRequirement unlockedBarbarianPlanting;

	ManualRequirement ardougneEmpty;
	ManualRequirement catherbyEmpty;
	ManualRequirement faladorEmpty;
	ManualRequirement farmingGuildEmpty;
	ManualRequirement harmonyEmpty;
	ManualRequirement morytaniaEmpty;
	ManualRequirement trollStrongholdEmpty;
	ManualRequirement weissEmpty;
	ManualRequirement hosidiusEmpty;
	ManualRequirement varlamoreEmpty;
	ManualRequirement ardougneReady;
	ManualRequirement catherbyReady;
	ManualRequirement faladorReady;
	ManualRequirement farmingGuildReady;
	ManualRequirement harmonyReady;
	ManualRequirement morytaniaReady;
	ManualRequirement trollStrongholdReady;
	ManualRequirement weissReady;
	ManualRequirement hosidiusReady;
	ManualRequirement varlamoreReady;
	Conditions allGrowing;

	// Steps
	ReorderableConditionalStep steps;
	DetailedQuestStep waitForHerbs;
	ObjectStep ardougnePatch;
	ObjectStep catherbyPatch;
	ObjectStep faladorPatch;
	ObjectStep farmingGuildPatch;
	ObjectStep harmonyPatch;
	ObjectStep morytaniaPatch;
	ObjectStep trollStrongholdPatch;
	ObjectStep weissPatch;
	ObjectStep hosidiusPatch;
	ObjectStep varlamorePatch;
	ObjectStep ardougnePlant;
	ObjectStep catherbyPlant;
	ObjectStep faladorPlant;
	ObjectStep farmingGuildPlant;
	ObjectStep harmonyPlant;
	ObjectStep morytaniaPlant;
	ObjectStep trollStrongholdPlant;
	ObjectStep weissPlant;
	ObjectStep hosidiusPlant;
	ObjectStep varlamorePlant;

	// Sidebar panels
	List<PanelDetails> allSteps;

	private enum Seed
	{
		GUAM(ItemID.GUAM_SEED), MARRENTILL(ItemID.MARRENTILL_SEED), TARROMIN(ItemID.TARROMIN_SEED), HARRALANDER(ItemID.HARRALANDER_SEED),
		RANARR(ItemID.RANARR_SEED), TOADFLAX(ItemID.TOADFLAX_SEED), IRIT(ItemID.IRIT_SEED), AVANTOE(ItemID.AVANTOE_SEED), KWUARM(ItemID.KWUARM_SEED),
		SNAPDRAGON(ItemID.SNAPDRAGON_SEED), HUASCA(ItemID.HUASCA_SEED), CADANTINE(ItemID.CADANTINE_SEED), LANTADYME(ItemID.LANTADYME_SEED),
		DWARF_WEED(ItemID.DWARF_WEED_SEED), TORSTOL(ItemID.TORSTOL_SEED);

		final int seedID;

		Seed(int seedID)
		{
			this.seedID = seedID;
		}
	}

	private enum GracefulOrFarming
	{
		NONE(),
		GRACEFUL(),
		FARMING()
	}

	private final String HERB_SEEDS = "herbSeeds";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	protected void setupRequirements()
	{
		ardougneReady = new ManualRequirement();
		catherbyReady = new ManualRequirement();
		faladorReady = new ManualRequirement();
		farmingGuildReady = new ManualRequirement();
		harmonyReady = new ManualRequirement();
		morytaniaReady = new ManualRequirement();
		trollStrongholdReady = new ManualRequirement();
		weissReady = new ManualRequirement();
		hosidiusReady = new ManualRequirement();
		varlamoreReady = new ManualRequirement();

		ardougneEmpty = new ManualRequirement();
		catherbyEmpty = new ManualRequirement();
		faladorEmpty = new ManualRequirement();
		farmingGuildEmpty = new ManualRequirement();
		harmonyEmpty = new ManualRequirement();
		morytaniaEmpty = new ManualRequirement();
		trollStrongholdEmpty = new ManualRequirement();
		weissEmpty = new ManualRequirement();
		hosidiusEmpty = new ManualRequirement();
		varlamoreEmpty = new ManualRequirement();

		allGrowing = nor(ardougneReady, catherbyReady, faladorReady, farmingGuildReady, harmonyReady, morytaniaReady, trollStrongholdReady, weissReady,
				hosidiusReady, varlamoreReady, ardougneEmpty, catherbyEmpty, faladorEmpty, farmingGuildEmpty, harmonyEmpty, morytaniaEmpty,
				trollStrongholdEmpty, weissEmpty, hosidiusEmpty, varlamoreEmpty);

		accessToFarmingGuildPatch = new SkillRequirement(Skill.FARMING, 65);

		accessToHarmony = new QuestRequirement(QuestHelperQuest.MORYTANIA_ELITE, QuestState.FINISHED);
		accessToWeiss = new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED);
		accessToTrollStronghold = new QuestRequirement(QuestHelperQuest.MY_ARMS_BIG_ADVENTURE, QuestState.FINISHED);
		accessToVarlamore = new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);

		unlockedBarbarianPlanting = new RuneliteRequirement(configManager, ConfigKeys.BARBARIAN_TRAINING_FINISHED_SEED_PLANTING.getKey());

		spade = new ItemRequirement("Spade", ItemID.SPADE);
		dibber = new ItemRequirement("Seed dibber", ItemID.DIBBER).hideConditioned(unlockedBarbarianPlanting);
		rake = new ItemRequirement("Rake", ItemID.RAKE).hideConditioned(new VarbitRequirement(VarbitID.FARMING_BLOCKWEEDS, 2));

		seed = new ItemRequirement("Seeds of your choice", ItemID.GUAM_SEED);

		var seedName = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS);

		if (seedName != null)
		{
			try
			{
				seed.setId(Seed.valueOf(seedName).seedID);
			} catch (IllegalArgumentException err)
			{
				configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS, Seed.GUAM);
			}
			seed.setName(Text.titleCase(Seed.valueOf(seedName)) + " seed");
		} else
		{
			configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS, Seed.GUAM);
		}
		compost = new ItemRequirement("Compost", ItemCollections.COMPOST);
		compost.setDisplayMatchedItemName(true);
		ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL).showConditioned(new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.FINISHED));
		ectophial.addAlternates(ItemID.ECTOPHIAL_EMPTY);
		magicSec = new ItemRequirement("Magic secateurs", ItemID.FAIRY_ENCHANTED_SECATEURS).showConditioned(new QuestRequirement(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS, QuestState.FINISHED));
		explorerRing2 = new ItemRequirement("Explorers' ring 2+", ItemID.LUMBRIDGE_RING_MEDIUM).showConditioned(new QuestRequirement(QuestHelperQuest.LUMBRIDGE_MEDIUM, QuestState.FINISHED));
		explorerRing2.addAlternates(ItemID.LUMBRIDGE_RING_HARD, ItemID.LUMBRIDGE_RING_ELITE);
		ardyCloak2 = new ItemRequirement("Ardougne cloak 2+", ItemID.ARDY_CAPE_MEDIUM).showConditioned(new QuestRequirement(QuestHelperQuest.ARDOUGNE_MEDIUM, QuestState.FINISHED));
		ardyCloak2.addAlternates(ItemID.ARDY_CAPE_HARD, ItemID.ARDY_CAPE_ELITE);
		xericsTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERIC_TALISMAN);

		hosidiusHouseTeleport = new ItemRequirement("Teleport to Hosidius House", ItemID.NZONE_TELETAB_KOUREND);
		xericsTalisman.addAlternates(ItemID.NZONE_TELETAB_KOUREND);

		var catherbyRunes = new ItemRequirements("Catherby teleport runes", new ItemRequirement("Law rune",
				ItemID.LAWRUNE), new ItemRequirement("Air rune", ItemID.AIRRUNE, 5));
		var catherbyTablet = new ItemRequirement("Catherby tablet", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);

		catherbyTeleport = new ItemRequirements(LogicType.OR, "Catherby teleport", catherbyTablet, catherbyRunes);

		var trollheimRunes = new ItemRequirements("Trollheim teleport runes", new ItemRequirement("Law rune",
				ItemID.LAWRUNE, 2), new ItemRequirement("Fire rune", ItemID.FIRERUNE, 2));
		var trollheimTablet = new ItemRequirement("Trollheim tablet", ItemID.NZONE_TELETAB_TROLLHEIM);
		trollheimTeleport = new ItemRequirements(LogicType.OR, "Trollheim teleport", trollheimTablet, trollheimRunes)
				.showConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED));

		icyBasalt = new ItemRequirement("Icy basalt", ItemID.WEISS_TELEPORT_BASALT).showConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED));
		stonyBasalt = new ItemRequirement("Stony basalt", ItemID.STRONGHOLD_TELEPORT_BASALT).showConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED));

		farmingGuildTeleport = new ItemRequirement("Farming guild teleport (Skills' Necklace or CIR fairy ring)", ItemID.SKILLCAPE_FARMING_TRIMMED).showConditioned(accessToFarmingGuildPatch);
		farmingGuildTeleport.addAlternates(ItemID.SKILLCAPE_FARMING);
		farmingGuildTeleport.addAlternates(ItemCollections.SKILLS_NECKLACES);
		farmingGuildTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		harmonyTeleport = new ItemRequirement("Harmony Teleport", ItemID.TELETAB_HARMONY).showConditioned(accessToHarmony);

		hunterWhistle = new ItemRequirement("Quetzal whistle", ItemID.HG_QUETZALWHISTLE_PERFECTED).showConditioned(accessToVarlamore);
		hunterWhistle.addAlternates(ItemID.HG_QUETZALWHISTLE_BASIC);
		hunterWhistle.addAlternates(ItemID.HG_QUETZALWHISTLE_ENHANCED);

		gracefulHood = new ItemRequirement(
				"Graceful hood", ItemCollections.GRACEFUL_HOOD, 1, true).isNotConsumed();

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
				"Farmer's strawhat", ItemID.TITHE_REWARD_HAT_MALE, 1, true).isNotConsumed();
		farmingHat.addAlternates(ItemID.TITHE_REWARD_HAT_FEMALE, ItemID.TITHE_REWARD_HAT_MALE_DUMMY, ItemID.TITHE_REWARD_HAT_FEMALE_DUMMY);

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
				farmingHat, farmingTop, farmingLegs, farmingBoots
		).isNotConsumed().showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.FARMING.name()));
	}

	public void setupSteps()
	{
		waitForHerbs = new DetailedQuestStep(this, "Wait for your herbs to grow.");
		ardougnePatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_3, new WorldPoint(2670, 3374, 0), "Harvest your herbs from the Ardougne patch.", ardyCloak2);
		catherbyPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_2, new WorldPoint(2813, 3463, 0), "Harvest your herbs from the Catherby patch.", catherbyTeleport);
		faladorPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_1, new WorldPoint(3058, 3311, 0), "Harvest your herbs from the Falador patch.", explorerRing2);
		hosidiusPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_6, new WorldPoint(1738, 3550, 0), "Harvest your herbs from the Hosidius patch.", xericsTalisman);

		farmingGuildPatch = new ObjectStep(this, ObjectID.HS_NPT2_WALL_CUTS_03, new WorldPoint(1238, 3726, 0), "Harvest your herbs from the Farming Guild patch.", farmingGuildTeleport);
		farmingGuildPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildPatch));

		harmonyPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_5, new WorldPoint(3789, 2837, 0), "Harvest your herbs from the Harmony patch.", ectophial);
		harmonyPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToHarmony));

		morytaniaPatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_4, new WorldPoint(3605, 3529, 0), "Harvest your herbs from the Morytania patch.", ectophial);

		trollStrongholdPatch = new ObjectStep(this, ObjectID.MYARM_HERBPATCH, new WorldPoint(2826, 3694, 0), "Harvest your herbs from the Troll Stronghold patch.",
				trollheimTeleport, stonyBasalt);
		trollStrongholdPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToTrollStronghold));
		weissPatch = new ObjectStep(this, ObjectID.MY2ARM_HERBPATCH, new WorldPoint(2848, 3934, 0), "Harvest your herbs from the Weiss patch.", icyBasalt);
		weissPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToWeiss));

		varlamorePatch = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_8, new WorldPoint(1582, 3094, 0), "Harvest your herbs from the Varlamore patch.", hunterWhistle);
		varlamorePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));

		ardougnePlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_3, new WorldPoint(2670, 3374, 0), "Plant your seeds into the Ardougne patch.", ardyCloak2);
		ardougnePlant.addIcon(ItemID.RANARR_SEED);
		ardougnePatch.addSubSteps(ardougnePlant);

		catherbyPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_2, new WorldPoint(2813, 3463, 0), "Plant your seeds into the Catherby patch.", catherbyTeleport);
		catherbyPlant.addIcon(ItemID.RANARR_SEED);
		catherbyPatch.addSubSteps(catherbyPlant);

		faladorPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_1, new WorldPoint(3058, 3311, 0), "Plant your seeds into the Falador patch.", explorerRing2);
		faladorPlant.addIcon(ItemID.RANARR_SEED);
		faladorPatch.addSubSteps(faladorPlant);

		hosidiusPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_6, new WorldPoint(1738, 3550, 0), "Plant your seeds into the Hosidius patch.", hosidiusHouseTeleport);
		hosidiusPlant.addIcon(ItemID.RANARR_SEED);
		hosidiusPatch.addSubSteps(hosidiusPlant);

		farmingGuildPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_7, new WorldPoint(1238, 3726, 0), "Plant your seeds into the Farming Guild patch.", farmingGuildTeleport);
		farmingGuildPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildPatch));
		farmingGuildPlant.addIcon(ItemID.RANARR_SEED);
		farmingGuildPatch.addSubSteps(farmingGuildPlant);

		harmonyPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_5, new WorldPoint(3789, 2837, 0), "Plant your seeds into the Harmony patch.", ectophial);
		harmonyPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToHarmony));
		harmonyPlant.addIcon(ItemID.RANARR_SEED);
		harmonyPatch.addSubSteps(harmonyPlant);

		morytaniaPlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_4, new WorldPoint(3605, 3529, 0), "Plant your seeds into the Morytania patch.", ectophial);
		morytaniaPlant.addIcon(ItemID.RANARR_SEED);
		morytaniaPatch.addSubSteps(morytaniaPlant);

		trollStrongholdPlant = new ObjectStep(this, ObjectID.MYARM_HERBPATCH, new WorldPoint(2826, 3694, 0), "Plant your seeds into the Troll Stronghold patch.",
				trollheimTeleport, stonyBasalt);
		trollStrongholdPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToTrollStronghold));
		trollStrongholdPlant.addIcon(ItemID.RANARR_SEED);
		trollStrongholdPatch.addSubSteps(trollStrongholdPlant);

		weissPlant = new ObjectStep(this, ObjectID.MY2ARM_HERBPATCH, new WorldPoint(2848, 3934, 0), "Plant your seeds into the Weiss patch.", icyBasalt);
		weissPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToWeiss));
		weissPlant.addIcon(ItemID.RANARR_SEED);
		weissPatch.addSubSteps(weissPlant);

		varlamorePlant = new ObjectStep(this, ObjectID.FARMING_HERB_PATCH_8, new WorldPoint(1582, 3094, 0), "Plant your seeds into the Varlamore patch.", hunterWhistle);
		varlamorePlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		varlamorePlant.addIcon(ItemID.RANARR_SEED);
		varlamorePatch.addSubSteps(varlamorePlant);
	}

	@Override
	public QuestStep loadStep()
	{
		farmingHandler = new FarmingHandler(client, configManager);
		initializeRequirements();
		setupSteps();
		prepopulateSidebarPanels();

		steps = new ReorderableConditionalStep(this, waitForHerbs, spade, dibber, rake, seed, magicSec, farmersOutfit, gracefulOutfit);
		steps.addStep(faladorReady, faladorPatch);
		steps.addStep(faladorEmpty, faladorPlant);

		steps.addStep(ardougneReady, ardougnePatch);
		steps.addStep(ardougneEmpty, ardougnePlant);

		steps.addStep(catherbyReady, catherbyPatch);
		steps.addStep(catherbyEmpty, catherbyPlant);

		steps.addStep(morytaniaReady, morytaniaPatch);
		steps.addStep(morytaniaEmpty, morytaniaPlant);

		steps.addStep(hosidiusReady, hosidiusPatch);
		steps.addStep(hosidiusEmpty, hosidiusPlant);

		steps.addStep(new Conditions(accessToTrollStronghold, trollStrongholdReady), trollStrongholdPatch);
		steps.addStep(new Conditions(accessToTrollStronghold, trollStrongholdEmpty), trollStrongholdPlant);

		steps.addStep(new Conditions(accessToWeiss, weissReady), weissPatch);
		steps.addStep(new Conditions(accessToWeiss, weissEmpty), weissPlant);

		steps.addStep(new Conditions(accessToFarmingGuildPatch, farmingGuildReady), farmingGuildPatch);
		steps.addStep(new Conditions(accessToFarmingGuildPatch, farmingGuildEmpty), farmingGuildPlant);

		steps.addStep(new Conditions(accessToHarmony, harmonyReady), harmonyPatch);
		steps.addStep(new Conditions(accessToHarmony, harmonyEmpty), harmonyPlant);

		steps.addStep(new Conditions(accessToVarlamore, varlamoreReady), varlamorePatch);
		steps.addStep(new Conditions(accessToVarlamore, varlamoreEmpty), varlamorePlant);

		return steps;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			return;
		}

		if (event.getKey().equals(HERB_SEEDS))
		{
			try
			{
				Seed selectedSeed = Seed.valueOf(event.getNewValue());
				seed.setId(selectedSeed.seedID);
				seed.setName(Text.titleCase(selectedSeed) + " seed");
				questHelperPlugin.refreshBank();
			} catch (IllegalArgumentException err)
			{
				questHelperPlugin.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HERB_SEEDS, Seed.GUAM);
			}
		}
		if (event.getKey().equals(GRACEFUL_OR_FARMING))
		{
			questHelperPlugin.refreshBank();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		int seedsNeeded = 0;
		for (FarmingPatch patch : farmingWorld.getTabs().get(Tab.HERB))
		{
			CropState state = farmingHandler.predictPatch(patch);
			boolean isHarvestable = state == CropState.HARVESTABLE || state == CropState.UNCHECKED;
			boolean isPlantable = state == CropState.EMPTY || state == CropState.DEAD || state == null;

			if (isHarvestable || isPlantable)
			{
				seedsNeeded++;
			}

			switch (patch.getRegion().getName())
			{
				case "Ardougne":
					ardougneReady.setShouldPass(isHarvestable);
					ardougneEmpty.setShouldPass(isPlantable);
					break;
				case "Catherby":
					catherbyReady.setShouldPass(isHarvestable);
					catherbyEmpty.setShouldPass(isPlantable);
					break;
				case "Falador":
					faladorReady.setShouldPass(isHarvestable);
					faladorEmpty.setShouldPass(isPlantable);
					break;
				case "Farming Guild":
					farmingGuildReady.setShouldPass(isHarvestable);
					farmingGuildEmpty.setShouldPass(isPlantable);
					if (!accessToFarmingGuildPatch.check(client))
					{
						seedsNeeded--;
					}
					break;
				case "Harmony":
					harmonyReady.setShouldPass(isHarvestable);
					harmonyEmpty.setShouldPass(isPlantable);
					if (!accessToHarmony.check(client))
					{
						seedsNeeded--;
					}
					break;
				case "Morytania":
					morytaniaReady.setShouldPass(isHarvestable);
					morytaniaEmpty.setShouldPass(isPlantable);
					break;
				case "Kourend":
					hosidiusReady.setShouldPass(isHarvestable);
					hosidiusEmpty.setShouldPass(isPlantable);
					break;
				case "Troll Stronghold":
					trollStrongholdReady.setShouldPass(isHarvestable);
					trollStrongholdEmpty.setShouldPass(isPlantable);
					if (!accessToTrollStronghold.check(client))
					{
						seedsNeeded--;
					}
					break;
				case "Weiss":
					weissReady.setShouldPass(isHarvestable);
					weissEmpty.setShouldPass(isPlantable);
					if (!accessToWeiss.check(client))
					{
						seedsNeeded--;
					}
					break;
				case "Civitas illa Fortis":
					varlamoreReady.setShouldPass(isHarvestable);
					varlamoreEmpty.setShouldPass(isPlantable);
					if (!accessToVarlamore.check(client))
					{
						seedsNeeded--;
					}
					break;
			}
		}
		seed.setQuantity(seedsNeeded);
		compost.setQuantity(seedsNeeded);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, dibber, rake, seed);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(compost, ectophial, magicSec, explorerRing2, ardyCloak2, xericsTalisman, catherbyTeleport,
				trollheimTeleport, icyBasalt, stonyBasalt, farmingGuildTeleport, hunterWhistle, harmonyTeleport,  gracefulOutfit, farmersOutfit);
	}

	@Override
	public List<HelperConfig> getConfigs()
	{
		HelperConfig seedsConfig = new HelperConfig("Seeds", HERB_SEEDS, Seed.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		return Arrays.asList(seedsConfig, outfitConfig);
	}

	private void prepopulateSidebarPanels()
	{
		allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Wait for Herbs", waitForHerbs).withHideCondition(nor(allGrowing)));
		TopLevelPanelDetails farmRunSidebar = new TopLevelPanelDetails("Farm Run",
				new PanelDetails("Farming Guild", Collections.singletonList(farmingGuildPatch)).withId(0),
				new PanelDetails("Falador", Collections.singletonList(faladorPatch)).withId(1),
				new PanelDetails("Ardougne", Collections.singletonList(ardougnePatch)).withId(2),
				new PanelDetails("Catherby", Collections.singletonList(catherbyPatch)).withId(3),
				new PanelDetails("Morytania", Collections.singletonList(morytaniaPatch)).withId(4),
				new PanelDetails("Hosidius", Collections.singletonList(hosidiusPatch)).withId(5),
				new PanelDetails("Varlamore", Collections.singletonList(varlamorePatch)).withId(6).withHideCondition(nor(accessToVarlamore)),
				new PanelDetails("Troll Stronghold", Collections.singletonList(trollStrongholdPatch)).withId(7).withHideCondition(nor(accessToTrollStronghold)),
				new PanelDetails("Weiss", Collections.singletonList(weissPatch)).withId(8).withHideCondition(nor(accessToWeiss)),
				new PanelDetails("Harmony Island", Collections.singletonList(harmonyPatch)).withId(9).withHideCondition(nor(accessToHarmony))
		);
		allSteps.add(farmRunSidebar);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		return allSteps;
	}
}
