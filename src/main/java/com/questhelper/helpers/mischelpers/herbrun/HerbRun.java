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
package com.questhelper.helpers.mischelpers.herbrun;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
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
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.CropState;

@QuestDescriptor(
	quest = QuestHelperQuest.HERB_RUN
)
public class HerbRun extends ComplexStateQuestHelper
{
	@Inject
	private FarmingWorld farmingWorld;

	private FarmingHandler farmingHandler;

	DetailedQuestStep waitForHerbs, ardougnePatch, catherbyPatch, faladorPatch, farmingGuildPatch, harmonyPatch, morytaniaPatch, trollStrongholdPatch, weissPatch;

	DetailedQuestStep ardougnePlant, catherbyPlant, faladorPlant, farmingGuildPlant, harmonyPlant, morytaniaPlant, trollStrongholdPlant, weissPlant;
	ItemRequirement spade, dibber, rake, seed, compost;
	ItemRequirement ectophial, magicSec, explorerRing2, ardyCloak2, xericsTalisman, catherbyTeleport, trollheimTeleport, icyBasalt, stonyBasalt, farmingGuildTeleport;
	ItemRequirement gracefulHood, gracefulTop, gracefulLegs, gracefulGloves, gracefulBoots, gracefulCape, gracefulOutfit;

	Requirement accessToHarmony, accessToWeiss, accessToTrollStronghold, accessToFarmingGuildPatch;

	ManualRequirement ardougneEmpty, catherbyEmpty, faladorEmpty, farmingGuildEmpty, harmonyEmpty, morytaniaEmpty, trollStrongholdEmpty, weissEmpty;
	ManualRequirement ardougneReady, catherbyReady, faladorReady, farmingGuildReady, harmonyReady, morytaniaReady, trollStrongholdReady, weissReady;

	@Override
	public QuestStep loadStep()
	{
		farmingHandler = new FarmingHandler(client, configManager);
		setupRequirements();
		setupConditions();
		setupSteps();

		ConditionalStep steps = new ConditionalStep(this, waitForHerbs);
		steps.addStep(faladorReady, faladorPatch);
		steps.addStep(faladorEmpty, faladorPlant);

		steps.addStep(ardougneReady, ardougnePatch);
		steps.addStep(ardougneEmpty, ardougnePlant);

		steps.addStep(catherbyReady, catherbyPatch);
		steps.addStep(catherbyEmpty, catherbyPlant);

		steps.addStep(morytaniaReady, morytaniaPatch);
		steps.addStep(morytaniaEmpty, morytaniaPlant);

		steps.addStep(new Conditions(accessToTrollStronghold, trollStrongholdReady), trollStrongholdPatch);
		steps.addStep(new Conditions(accessToTrollStronghold, trollStrongholdEmpty), trollStrongholdPlant);

		steps.addStep(new Conditions(accessToWeiss, weissReady), weissPatch);
		steps.addStep(new Conditions(accessToWeiss, weissEmpty), weissPlant);

		steps.addStep(new Conditions(accessToHarmony, harmonyReady), harmonyPatch);
		steps.addStep(new Conditions(accessToHarmony, harmonyEmpty), harmonyPlant);

		steps.addStep(new Conditions(accessToFarmingGuildPatch, farmingGuildReady), farmingGuildPatch);
		steps.addStep(new Conditions(accessToFarmingGuildPatch, farmingGuildEmpty), farmingGuildPlant);

		return steps;
	}

	public void setupConditions()
	{
		ardougneReady = new ManualRequirement();
		catherbyReady = new ManualRequirement();
		faladorReady = new ManualRequirement();
		farmingGuildReady = new ManualRequirement();
		harmonyReady = new ManualRequirement();
		morytaniaReady = new ManualRequirement();
		trollStrongholdReady = new ManualRequirement();
		weissReady = new ManualRequirement();

		ardougneEmpty = new ManualRequirement();
		catherbyEmpty = new ManualRequirement();
		faladorEmpty = new ManualRequirement();
		farmingGuildEmpty = new ManualRequirement();
		harmonyEmpty = new ManualRequirement();
		morytaniaEmpty = new ManualRequirement();
		trollStrongholdEmpty = new ManualRequirement();
		weissEmpty = new ManualRequirement();

		accessToHarmony = new QuestRequirement(QuestHelperQuest.THE_GREAT_BRAIN_ROBBERY, QuestState.FINISHED);
		accessToWeiss = new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED);
		accessToTrollStronghold = new QuestRequirement(QuestHelperQuest.MY_ARMS_BIG_ADVENTURE, QuestState.FINISHED);
		accessToFarmingGuildPatch = new Conditions(new FavourRequirement(Favour.HOSIDIUS, 60),
			new SkillRequirement(Skill.FARMING, 65));
	}

	@Override
	public void setupRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		dibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);
		rake = new ItemRequirement("Rake", ItemID.RAKE).hideConditioned(new VarbitRequirement(Varbits.AUTOWEED, 2));
		seed = new ItemRequirement("Seeds of your choice", ItemCollections.SEEDS);
		compost = new ItemRequirement("Compost", ItemCollections.COMPOST);

		ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL).showConditioned(new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.FINISHED));
		ectophial.addAlternates(ItemID.ECTOPHIAL_4252);
		magicSec = new ItemRequirement("Magic secateurs", ItemID.MAGIC_SECATEURS).showConditioned(new QuestRequirement(QuestHelperQuest.FAIRYTALE_I__GROWING_PAINS, QuestState.FINISHED));
		explorerRing2 = new ItemRequirement("Explorers' ring 2+", ItemID.EXPLORERS_RING_2).showConditioned(new QuestRequirement(QuestHelperQuest.LUMBRIDGE_MEDIUM, QuestState.FINISHED));
		explorerRing2.addAlternates(ItemID.EXPLORERS_RING_3, ItemID.EXPLORERS_RING_4);
		ardyCloak2 = new ItemRequirement("Ardougne cloak 2+", ItemID.ARDOUGNE_CLOAK_2).showConditioned(new QuestRequirement(QuestHelperQuest.ARDOUGNE_MEDIUM, QuestState.FINISHED));
		ardyCloak2.addAlternates(ItemID.ARDOUGNE_CLOAK_3, ItemID.ARDOUGNE_CLOAK_4);
		xericsTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERICS_TALISMAN);

		ItemRequirement catherbyRunes = new ItemRequirements("Catherby teleport runes", new ItemRequirement("Law rune",
			ItemID.LAW_RUNE), new ItemRequirement("Air rune", ItemID.AIR_RUNE, 5));
		ItemRequirement catherbyTablet = new ItemRequirement("Catherby tablet", ItemID.CATHERBY_TELEPORT);

		catherbyTeleport = new ItemRequirements(LogicType.OR, "Catherby teleport", catherbyRunes, catherbyTablet);

		ItemRequirement trollheimRunes = new ItemRequirements("Trollheim teleport runes", new ItemRequirement("Law rune",
			ItemID.LAW_RUNE, 2), new ItemRequirement("Fire rune", ItemID.FIRE_RUNE, 2));
		ItemRequirement trollheimTablet = new ItemRequirement("Trollheim tablet", ItemID.TROLLHEIM_TELEPORT);
		trollheimTeleport = new ItemRequirements(LogicType.OR, "Trollheim teleport", trollheimRunes, trollheimTablet)
			.hideConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED));

		icyBasalt = new ItemRequirement("Icy basalt", ItemID.ICY_BASALT).showConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED));
		stonyBasalt = new ItemRequirement("Stony basalt", ItemID.STONY_BASALT).showConditioned(new QuestRequirement(QuestHelperQuest.MAKING_FRIENDS_WITH_MY_ARM, QuestState.FINISHED));

		farmingGuildTeleport = new ItemRequirement("Farming guild teleport (Skills' Necklace or CIR fairy ring)", ItemID.FARMING_CAPET).showConditioned(accessToFarmingGuildPatch);
		farmingGuildTeleport.addAlternates(ItemID.FARMING_CAPE);
		farmingGuildTeleport.addAlternates(ItemCollections.SKILLS_NECKLACES);
		farmingGuildTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

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
		).isNotConsumed();
	}

	public void setupSteps()
	{
		waitForHerbs = new DetailedQuestStep(this, "Wait for your herbs to grow.");
		ardougnePatch = new ObjectStep(this, NullObjectID.NULL_8152, new WorldPoint(2670, 3374, 0), "Harvest your herbs from the Ardougne patch.", spade, dibber, rake, seed, ardyCloak2);
		catherbyPatch = new ObjectStep(this, NullObjectID.NULL_8151, new WorldPoint(2813, 3463, 0), "Harvest your herbs from the Catherby patch.", spade, dibber, rake, seed, catherbyTeleport);
		faladorPatch = new ObjectStep(this, NullObjectID.NULL_8150, new WorldPoint(3058, 3311, 0), "Harvest your herbs from the Falador patch.", spade, dibber, rake, seed, explorerRing2);

		farmingGuildPatch = new ObjectStep(this, NullObjectID.NULL_38979, new WorldPoint(1238, 3726, 0), "Harvest your herbs from the Farming Guild patch.", spade, dibber, rake, seed);
		farmingGuildPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildPatch));

		harmonyPatch = new ObjectStep(this, NullObjectID.NULL_9372, new WorldPoint(3789, 2837, 0), "Harvest your herbs from the Harmony patch.", spade, dibber, rake, seed, ectophial);
		harmonyPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToHarmony));

		morytaniaPatch = new ObjectStep(this, NullObjectID.NULL_8153, new WorldPoint(3605, 3529, 0), "Harvest your herbs from the Morytania patch.", spade, dibber, rake, seed, ectophial);

		trollStrongholdPatch = new ObjectStep(this, NullObjectID.NULL_18816, new WorldPoint(2826, 3694, 0), "Harvest your herbs from the Troll Stronghold patch.",
			spade, dibber, rake, seed, trollheimTeleport, stonyBasalt);
		trollStrongholdPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToTrollStronghold));
		weissPatch = new ObjectStep(this, NullObjectID.NULL_33176, new WorldPoint(2848, 3934, 0), "Harvest your herbs from the Weiss patch.", spade, dibber, rake, seed, icyBasalt);
		weissPatch.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToWeiss));

		ardougnePlant = new ObjectStep(this, NullObjectID.NULL_8152, new WorldPoint(2670, 3374, 0), "Plant your seeds into the Ardougne patch.", spade, dibber, rake, seed, ardyCloak2);
		ardougnePlant.addIcon(ItemID.RANARR_SEED);
		ardougnePatch.addSubSteps(ardougnePlant);

		catherbyPlant = new ObjectStep(this, NullObjectID.NULL_8151, new WorldPoint(2813, 3463, 0), "Plant your seeds into the Catherby patch.", spade, dibber, rake, seed, catherbyTeleport);
		catherbyPlant.addIcon(ItemID.RANARR_SEED);
		catherbyPatch.addSubSteps(catherbyPlant);

		faladorPlant = new ObjectStep(this, NullObjectID.NULL_8150, new WorldPoint(3058, 3311, 0), "Plant your seeds into the Falador patch.", spade, dibber, rake, seed, explorerRing2);
		faladorPlant.addIcon(ItemID.RANARR_SEED);
		faladorPatch.addSubSteps(faladorPlant);

		farmingGuildPlant = new ObjectStep(this, NullObjectID.NULL_38979, new WorldPoint(1238, 3726, 0), "Plant your seeds into the Farming Guild patch.", spade, dibber, rake, seed);
		farmingGuildPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToFarmingGuildPatch));
		farmingGuildPlant.addIcon(ItemID.RANARR_SEED);
		farmingGuildPatch.addSubSteps(farmingGuildPlant);

		harmonyPlant = new ObjectStep(this, NullObjectID.NULL_9372, new WorldPoint(3789, 2837, 0), "Plant your seeds into the Harmony patch.", spade, dibber, rake, seed, ectophial);
		harmonyPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToHarmony));
		harmonyPlant.addIcon(ItemID.RANARR_SEED);
		harmonyPatch.addSubSteps(harmonyPlant);

		morytaniaPlant = new ObjectStep(this, NullObjectID.NULL_8153, new WorldPoint(3605, 3529, 0), "Plant your seeds into the Morytania patch.", spade, dibber, rake, seed, ectophial);
		morytaniaPlant.addIcon(ItemID.RANARR_SEED);
		morytaniaPatch.addSubSteps(morytaniaPlant);

		trollStrongholdPlant = new ObjectStep(this, NullObjectID.NULL_18816, new WorldPoint(2826, 3694, 0), "Plant your seeds into the Troll Stronghold patch.",
			spade, dibber, rake, seed, trollheimTeleport, stonyBasalt);
		trollStrongholdPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToTrollStronghold));
		trollStrongholdPlant.addIcon(ItemID.RANARR_SEED);
		trollStrongholdPatch.addSubSteps(trollStrongholdPlant);

		weissPlant = new ObjectStep(this, NullObjectID.NULL_33176, new WorldPoint(2848, 3934, 0), "Plant your seeds into the Weiss patch.", spade, dibber, rake, seed, icyBasalt);
		weissPlant.conditionToHideInSidebar(new Conditions(LogicType.NOR, accessToWeiss));
		weissPlant.addIcon(ItemID.RANARR_SEED);
		weissPatch.addSubSteps(weissPlant);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for (FarmingPatch patch : farmingWorld.getTabs().get(Tab.HERB))
		{
			CropState state = farmingHandler.predictPatch(patch);
			boolean isHarvestable = state == CropState.HARVESTABLE;
			boolean isPlantable = state == CropState.EMPTY || state == CropState.DEAD;
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
					break;
				case "Harmony":
					harmonyReady.setShouldPass(isHarvestable);
					harmonyEmpty.setShouldPass(isPlantable);
					break;
				case "Morytania":
					morytaniaReady.setShouldPass(isHarvestable);
					morytaniaEmpty.setShouldPass(isPlantable);
					break;
				case "Troll Stronghold":
					trollStrongholdReady.setShouldPass(isHarvestable);
					trollStrongholdEmpty.setShouldPass(isPlantable);
					break;
				case "Weiss":
					weissReady.setShouldPass(isHarvestable);
					weissEmpty.setShouldPass(isPlantable);
					break;
			}
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(spade, dibber, rake, seed);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ectophial, magicSec, explorerRing2, ardyCloak2, xericsTalisman, catherbyTeleport, trollheimTeleport, icyBasalt, stonyBasalt, farmingGuildTeleport, gracefulOutfit);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Farm run", Arrays.asList(faladorPatch, ardougnePatch, catherbyPatch, morytaniaPatch,
			trollStrongholdPatch, weissPatch, harmonyPatch, farmingGuildPatch), Arrays.asList(spade, dibber, rake, seed, magicSec),
			Arrays.asList(ectophial, magicSec, explorerRing2, ardyCloak2, xericsTalisman, catherbyTeleport, trollheimTeleport, icyBasalt, stonyBasalt, farmingGuildTeleport, gracefulOutfit)));

		return allSteps;
	}
}
