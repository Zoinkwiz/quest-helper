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
package com.questhelper.helpers.mischelpers.hardwoodrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.questhelper.QuestHelperConfig;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.questinfo.HelperConfig;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
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
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.util.Text;

public class HardwoodRun extends ComplexStateQuestHelper
{
	@Inject
	private FarmingWorld farmingWorld;

	private FarmingHandler farmingHandler;

	DetailedQuestStep waitForTrees, easternPatch, southernPatch, westernPatch, varlamorePatch;

	DetailedQuestStep easternPlant, southernPlant, westernPlant, varlamorePlant;
	ItemRequirement spade, rake, sapling, compost;
	ItemRequirement  digsitependant, dramenstaff;
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;
	ItemRequirement farmingHat, farmingTop, farmingLegs, farmingBoots, farmersOutfit;

	@SuppressWarnings("unused")
                Requirement accessToFossilIsland;
        Requirement accessToVarlamore;

	ManualRequirement easternEmpty, southernEmpty, westernEmpty, varlamoreEmpty;
	ManualRequirement easternReady, southernReady, westernReady, varlamoreReady;

	private enum Sapling {
		MAHOGANY(ItemID.MAHOGANY_SAPLING), TEAK(ItemID.TEAK_SAPLING);

		final int saplingID;

		Sapling(int saplingID)
		{
			this.saplingID = saplingID;
		}
	}

	private enum GracefulOrFarming {
		NONE(),
		GRACEFUL(),
		FARMING();
	}

	private final String HARDWOOD_SAPLINGS = "hardwoodSaplings";
	private final String GRACEFUL_OR_FARMING = "gracefulOrFarming";

	@Override
	public QuestStep loadStep()
	{
		farmingHandler = new FarmingHandler(client, configManager);
		initializeRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep steps = new ConditionalStep(this, waitForTrees, spade, rake, sapling, farmersOutfit, gracefulOutfit);
		steps.addStep(easternReady, easternPatch);
		steps.addStep(southernReady, southernPatch);
		steps.addStep(westernReady, westernPatch);
		steps.addStep(easternEmpty, easternPlant);
		steps.addStep(southernEmpty, southernPlant);
		steps.addStep(westernEmpty, westernPlant);

		steps.addStep(new Conditions(accessToVarlamore, varlamoreReady), varlamorePatch);
		steps.addStep(new Conditions(accessToVarlamore, varlamoreEmpty), varlamorePlant);

		return steps;
	}

	public void setupConditions()
	{
		easternReady = new ManualRequirement();
		southernReady = new ManualRequirement();
		westernReady = new ManualRequirement();
		varlamoreReady = new ManualRequirement();

		easternEmpty = new ManualRequirement();
		southernEmpty = new ManualRequirement();
		westernEmpty = new ManualRequirement();
		varlamoreEmpty = new ManualRequirement();
	}

	@Override
	protected void setupRequirements()
	{
		
		accessToFossilIsland = new QuestRequirement(QuestHelperQuest.BONE_VOYAGE, QuestState.FINISHED);
		accessToVarlamore = new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED);

		spade = new ItemRequirement("Spade", ItemID.SPADE);
		rake = new ItemRequirement("Rake", ItemID.RAKE).hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2));

		sapling = new ItemRequirement("Sapling of your choice", ItemID.MAHOGANY_SAPLING, ItemID.TEAK_SAPLING);

		String saplingName = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_SAPLINGS);

		if (saplingName != null)
		{
			try
			{
				sapling.setId(Sapling.valueOf(saplingName).saplingID);
			} catch (IllegalArgumentException err)
			{
				configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_SAPLINGS, Sapling.MAHOGANY);
			}
			sapling.setName(Text.titleCase(Sapling.valueOf(saplingName)) + " sapling");
		} else
		{
			configManager.setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_SAPLINGS, Sapling.MAHOGANY);
		}
		compost = new ItemRequirement("Compost", ItemCollections.COMPOST);
		compost.setDisplayMatchedItemName(true);
		digsitependant = new ItemRequirement("Digsite pendant", ItemID.DIGSITE_PENDANT_5);

		digsitependant.addAlternates(ItemID.DIGSITE_PENDANT_1);
		digsitependant.addAlternates(ItemID.DIGSITE_PENDANT_2);
		digsitependant.addAlternates(ItemID.DIGSITE_PENDANT_3);
		digsitependant.addAlternates(ItemID.DIGSITE_PENDANT_4);

		dramenstaff = new ItemRequirement("dramen staff", ItemID.DRAMEN_STAFF).showConditioned(accessToVarlamore);
		

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
			farmingHat, farmingTop, farmingLegs, farmingBoots
		).isNotConsumed().showConditioned(new RuneliteRequirement(configManager, GRACEFUL_OR_FARMING, GracefulOrFarming.FARMING.name()));
	}

	public void setupSteps()
	{
		waitForTrees = new DetailedQuestStep(this, "Wait for your trees to grow.");
		easternPatch = new ObjectStep(this, NullObjectID.NULL_8152, new WorldPoint(3715, 3835, 0), "Harvest your trees from the eastern patch.", digsitependant);
		southernPatch = new ObjectStep(this, NullObjectID.NULL_8151, new WorldPoint(3708, 3833, 0), "Harvest your trees from the southern patch.", digsitependant);
		westernPatch = new ObjectStep(this, NullObjectID.NULL_8150, new WorldPoint(3702, 3837, 0), "Harvest your trees from the western patch.", digsitependant);

		varlamorePatch = new ObjectStep(this, NullObjectID.NULL_50697, new WorldPoint(1687, 2972, 0), "Harvest your trees from the Varlamore patch.", dramenstaff);
		varlamorePatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));

		easternPlant = new ObjectStep(this, NullObjectID.NULL_8152, new WorldPoint(3715, 3835, 0), "Plant your sapling into the eastern patch.", digsitependant);
		easternPlant.addIcon(ItemID.MAHOGANY_SAPLING);
		easternPatch.addSubSteps(easternPlant);

		southernPlant = new ObjectStep(this, NullObjectID.NULL_8151, new WorldPoint(3708, 3833, 0), "Plant your spaling into the southern patch.", digsitependant);
		southernPlant.addIcon(ItemID.MAHOGANY_SAPLING);
		southernPatch.addSubSteps(southernPlant);

		westernPlant = new ObjectStep(this, NullObjectID.NULL_8150, new WorldPoint(3702, 3837, 0), "Plant your sapling into the western patch.", digsitependant);
		westernPlant.addIcon(ItemID.MAHOGANY_SAPLING);
		westernPatch.addSubSteps(westernPlant);

		varlamorePlant = new ObjectStep(this, NullObjectID.NULL_50697, new WorldPoint(1687, 2972, 0), "Plant your sapling into the Varlamore patch.", dramenstaff);
		varlamorePlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToVarlamore));
		varlamorePlant.addIcon(ItemID.MAHOGANY_SAPLING);
		varlamorePatch.addSubSteps(varlamorePlant);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(QuestHelperConfig.QUEST_BACKGROUND_GROUP))
		{
			return;
		}

		if (event.getKey().equals(HARDWOOD_SAPLINGS))
		{
			try
			{
				Sapling selectedSapling = Sapling.valueOf(event.getNewValue());
				sapling.setId(selectedSapling.saplingID);
				sapling.setName(Text.titleCase(selectedSapling) + " sapling");
				questHelperPlugin.refreshBank();
			}
			catch (IllegalArgumentException err)
			{
				questHelperPlugin.getConfigManager().setConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, HARDWOOD_SAPLINGS, Sapling.MAHOGANY);
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
		int saplingsNeeded = 0;
		for (FarmingPatch patch : farmingWorld.getTabs().get(Tab.TREE))
		{
			CropState state = farmingHandler.predictPatch(patch);
			boolean isHarvestable = state == CropState.HARVESTABLE;
			boolean isPlantable = state == CropState.EMPTY || state == CropState.DEAD || state == null;

			if (isHarvestable || isPlantable)
			{
				saplingsNeeded++;
			}

			switch (patch.getRegion().getName())
			{
				case "Fossil Island":
					easternReady.setShouldPass(isHarvestable);
					easternEmpty.setShouldPass(isPlantable);
					southernReady.setShouldPass(isHarvestable);
					southernEmpty.setShouldPass(isPlantable);
					westernReady.setShouldPass(isHarvestable);
					westernEmpty.setShouldPass(isPlantable);
					break;
				case "Civitas illa Fortis":
					varlamoreReady.setShouldPass(isHarvestable);
					varlamoreEmpty.setShouldPass(isPlantable);
					if(!accessToVarlamore.check(client))
					{
						saplingsNeeded--;
					}
					break;
			}
		}
		sapling.setQuantity(saplingsNeeded);
		compost.quantity(saplingsNeeded);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, rake, sapling);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(compost, digsitependant, dramenstaff, gracefulOutfit, farmersOutfit);
	}

	@Override
	public List<HelperConfig> getConfigs()
	{
		HelperConfig saplingsConfig = new HelperConfig("Saplings", HARDWOOD_SAPLINGS, Sapling.values());
		HelperConfig outfitConfig = new HelperConfig("Outfit", GRACEFUL_OR_FARMING, GracefulOrFarming.values());
		return Arrays.asList(saplingsConfig, outfitConfig);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Hardwood run", Arrays.asList(easternPatch, southernPatch, westernPatch,
			varlamorePatch), Arrays.asList(spade, rake, sapling),
			Arrays.asList(compost, dramenstaff, digsitependant, gracefulOutfit, farmersOutfit)));

		return allSteps;
	}
}
