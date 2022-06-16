/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_SIR_AMIK_VARZE
)
public class RFDSirAmikVarze extends BasicQuestHelper
{
	ItemRequirement combatGear, bucketOfMilk, potOfCream, cornflour, pot, sweetcorn, axe, macheteAndRadimus, machete,
		vanillaPod, dramenStaffOrLunar, dramenBranch, pestleAndMortar, iceGloves, rawChicken, antidragonShield,
		antifirePotion, radimusNotes, bruleeWithEgg, baseBrulee, uncookedBrulee, finishedBrulee, finishedBruleeHighlighted,
		milkyMixture, cornflourMixture, evilEgg, token, cinnamon, pestleAndMortarHighlighted, tokenHighlighted;

	Requirement inDiningRoom, talkedToWom, inEvilChickenLair, inZanaris, hasEggAndToken, tokenNearby, eggNearby;

	QuestStep enterDiningRoom, inspectAmik, enterKitchen, talkToCook, enterDiningRoomAgain, useBruleeOnVarze, talkToWom, useMilkOnCream,
		useCornflourOnMilky, addPodToCornflourMixture, enterZanaris, useChickenOnShrine, killEvilChicken, pickUpEgg, useEggOnBrulee,
		killBlackDragon, pickUpToken, grindBranch, useCinnamonOnBrulee, rubToken;

	ConditionalStep tokenAndEggSteps;

	//Zones
	Zone diningRoom, zanaris, evilChickenLair;

	int evilChickenLevel = 19;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		calculateEvilChickenLevel();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectPete = new ConditionalStep(this, enterDiningRoom);
		goInspectPete.addStep(inDiningRoom, inspectAmik);
		steps.put(0, goInspectPete);

		ConditionalStep goAskCook = new ConditionalStep(this, talkToCook);
		goAskCook.addStep(inDiningRoom, enterKitchen);
		steps.put(5, goAskCook);

		tokenAndEggSteps = new ConditionalStep(this, enterZanaris);
		tokenAndEggSteps.addStep(tokenNearby, pickUpToken);
		tokenAndEggSteps.addStep(new Conditions(evilEgg.alsoCheckBank(questBank), inEvilChickenLair), killBlackDragon);
		tokenAndEggSteps.addStep(eggNearby, pickUpEgg);
		tokenAndEggSteps.addStep(inEvilChickenLair, killEvilChicken);
		tokenAndEggSteps.addStep(inZanaris, useChickenOnShrine);
		tokenAndEggSteps.setLockingCondition(hasEggAndToken);
		tokenAndEggSteps.setBlocker(true);

		ConditionalStep makeBrulee = new ConditionalStep(this, useMilkOnCream);
		makeBrulee.addStep(new Conditions(uncookedBrulee), rubToken);
		makeBrulee.addStep(new Conditions(bruleeWithEgg, cinnamon), useCinnamonOnBrulee);
		makeBrulee.addStep(bruleeWithEgg, grindBranch);
		makeBrulee.addStep(baseBrulee, useEggOnBrulee);
		makeBrulee.addStep(cornflourMixture, addPodToCornflourMixture);
		makeBrulee.addStep(milkyMixture, useCornflourOnMilky);

		ConditionalStep saveAmik = new ConditionalStep(this, talkToWom);
		saveAmik.addStep(new Conditions(inDiningRoom, finishedBrulee), useBruleeOnVarze);
		saveAmik.addStep(finishedBrulee.alsoCheckBank(questBank), enterDiningRoomAgain);
		saveAmik.addStep(hasEggAndToken, makeBrulee);
		saveAmik.addStep(talkedToWom, tokenAndEggSteps);
		steps.put(10, saveAmik);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		bucketOfMilk.setHighlightInInventory(true);
		potOfCream = new ItemRequirement("Pot of cream", ItemID.POT_OF_CREAM);
		potOfCream.setHighlightInInventory(true);
		cornflour = new ItemRequirement("Pot of cornflour", ItemID.POT_OF_CORNFLOUR);
		cornflour.setTooltip("You can make this by grinding a sweetcorn in a windmill and collecting it with a pot");
		cornflour.setHighlightInInventory(true);

		pot = new ItemRequirement("Pot", ItemID.POT);
		sweetcorn = new ItemRequirement("Sweetcorn", ItemID.SWEETCORN);

		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		machete = new ItemRequirement("Any machete", ItemCollections.MACHETE);
		radimusNotes = new ItemRequirement("Radimus notes", ItemID.RADIMUS_NOTES);
		radimusNotes.addAlternates(ItemID.RADIMUS_NOTES_715);
		if (QuestHelperQuest.LEGENDS_QUEST.getState(client) == QuestState.FINISHED)
		{
			macheteAndRadimus = machete;
		}
		else
		{
			macheteAndRadimus = new ItemRequirements("Machete and Radimus notes",
				machete, radimusNotes);
		}
		vanillaPod = new ItemRequirement("Vanilla pod", ItemID.VANILLA_POD);
		vanillaPod.setTooltip("You can get a pod from the Kharazi Jungle. Bring an axe and machete to get in");
		vanillaPod.setHighlightInInventory(true);

		dramenStaffOrLunar = new ItemRequirement("Dramen/lunar staff", ItemID.DRAMEN_STAFF, 1, true);
		dramenStaffOrLunar.addAlternates(ItemID.LUNAR_STAFF);
		dramenStaffOrLunar.setDisplayMatchedItemName(true);

		dramenBranch = new ItemRequirement("Dramen branch", ItemID.DRAMEN_BRANCH);
		dramenBranch.setHighlightInInventory(true);
		dramenBranch.setTooltip("You can get one from the dramen tree under Entrana");
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted.setHighlightInInventory(true);

		iceGloves = new ItemRequirement("Ice gloves or smiths gloves(i)", ItemID.ICE_GLOVES, 1, true);
		iceGloves.addAlternates(ItemID.SMITHS_GLOVES_I);
		iceGloves.setTooltip("Although optional, you'll take a lot of damage if you're not wearing them");
		rawChicken = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN);
		rawChicken.setHighlightInInventory(true);
		antidragonShield = new ItemRequirement("Anti-dragon shield", ItemCollections.ANTIFIRE_SHIELDS);
		antifirePotion = new ItemRequirement("Antifire potion", ItemCollections.ANTIFIRE);
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		milkyMixture = new ItemRequirement("Milky mixture", ItemID.MILKY_MIXTURE);
		milkyMixture.setHighlightInInventory(true);
		cornflourMixture = new ItemRequirement("Cornflour mixture", ItemID.CORNFLOUR_MIXTURE);
		cornflourMixture.setHighlightInInventory(true);
		bruleeWithEgg = new ItemRequirement("Brulee", ItemID.BRULEE);
		bruleeWithEgg.setHighlightInInventory(true);
		baseBrulee = new ItemRequirement("Brulee", ItemID.BRULEE_7474);
		baseBrulee.setHighlightInInventory(true);
		uncookedBrulee = new ItemRequirement("Brulee", ItemID.BRULEE_7475);
		finishedBrulee = new ItemRequirement("Brulee supreme", ItemID.BRULEE_SUPREME);
		finishedBruleeHighlighted = new ItemRequirement("Brulee supreme", ItemID.BRULEE_SUPREME);
		finishedBruleeHighlighted.setHighlightInInventory(true);

		cinnamon = new ItemRequirement("Cinnamon", ItemID.CINNAMON);
		cinnamon.setHighlightInInventory(true);

		evilEgg = new ItemRequirement("Evil chickens egg", ItemID.EVIL_CHICKENS_EGG);
		evilEgg.addAlternates(ItemID.BRULEE, ItemID.BRULEE_7475);
		evilEgg.setHighlightInInventory(true);

		token = new ItemRequirement("Dragon token", ItemID.DRAGON_TOKEN);
		token.addAlternates(ItemID.BRULEE_SUPREME);

		tokenHighlighted = new ItemRequirement("Dragon token", ItemID.DRAGON_TOKEN);
		tokenHighlighted.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		zanaris = new Zone(new WorldPoint(2368, 4353, 0), new WorldPoint(2495, 4479, 0));
		evilChickenLair = new Zone(new WorldPoint(2430, 4355, 0), new WorldPoint(2492, 4407, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneRequirement(diningRoom);
		// 1911 0->1->2->3->4->5->6 status of brulee
		// 1912 0->1 picked up token
		// 1913 0->1 evil chicken killed
		talkedToWom = new VarbitRequirement(1919, 1, Operation.GREATER_EQUAL);
		// 1919 = 2 when entered black dragon lair once

		inEvilChickenLair = new ZoneRequirement(evilChickenLair);
		inZanaris = new ZoneRequirement(zanaris);
		hasEggAndToken = new Conditions(evilEgg, token);
		tokenNearby = new ItemOnTileRequirement(token);
		eggNearby = new ItemOnTileRequirement(evilEgg);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0), "Go inspect Sir Amik Varze in Lumbridge Castle.");
		inspectAmik = new ObjectStep(this, ObjectID.SIR_AMIK_VARZE_12345, new WorldPoint(1865, 5321, 0), "Inspect Sir Amik Varze.");
		inspectAmik.addSubSteps(enterDiningRoom);

		enterKitchen = new ObjectStep(this, ObjectID.BARRIER_12351, new WorldPoint(1861, 5316, 0), "Talk to the Lumbridge Cook.");
		talkToCook = new NpcStep(this, NpcID.COOK_4626, new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook and ask him all the options about Sir Amik Varze.");
		talkToCook.addDialogSteps("More...", "Protecting Sir Amik Varze");
		talkToCook.addSubSteps(enterKitchen);

		talkToWom = new NpcStep(this, NpcID.WISE_OLD_MAN, new WorldPoint(3088, 3255, 0), "Talk to the Wise Old Man in Draynor Village about strange beasts and the Evil Chicken.");
		talkToWom.addDialogSteps("I'd just like to ask you something.", "Strange beasts", "The Evil Chicken");

		useMilkOnCream = new DetailedQuestStep(this, "Use a bucket of milk on a pot of cream.", bucketOfMilk, potOfCream);
		useCornflourOnMilky = new DetailedQuestStep(this, "Use cornflour on the milky mixture.", cornflour, milkyMixture);
		addPodToCornflourMixture = new DetailedQuestStep(this, "Add a vanilla pod to the cornflour mixture.", vanillaPod, cornflourMixture);
		enterZanaris = new DetailedQuestStep(this, "Travel to Zanaris.", dramenStaffOrLunar);
		useChickenOnShrine = new ObjectStep(this, ObjectID.CHICKEN_SHRINE, new WorldPoint(2453, 4477, 0),
			"Use a raw chicken on the Chicken Shrine in the north east of Zanaris.", rawChicken, combatGear);
		useChickenOnShrine.addIcon(ItemID.RAW_CHICKEN);
		killEvilChicken = new NpcStep(this, NpcID.EVIL_CHICKEN, new WorldPoint(2455, 4399, 0), "Kill the Evil Chicken in the north of the range. Pray protect from magic against it.");
		pickUpEgg = new ItemStep(this, "Pick up the evil chicken egg.", evilEgg);
		useEggOnBrulee = new DetailedQuestStep(this, "Use the evil chickens egg on the brulee.", evilEgg, baseBrulee, pestleAndMortar);
		killBlackDragon = new NpcStep(this, NpcID.BLACK_DRAGON, new WorldPoint(2461, 4367, 0), "Kill a black dragon.", combatGear, antidragonShield);
		pickUpToken = new ItemStep(this, "Pick up the dragon token.", token);
		grindBranch = new DetailedQuestStep(this, "Use a pestle and mortar on the dramen branch.", pestleAndMortarHighlighted, dramenBranch);
		useCinnamonOnBrulee = new DetailedQuestStep(this, "Use the cinnamon on the brulee.", cinnamon, bruleeWithEgg);
		rubToken = new DetailedQuestStep(this, "Equip your Ice Gloves and rub the dragon token in Lumbridge or Zanaris. Ask the fairy dragon to cook your brulee.", tokenHighlighted, uncookedBrulee, iceGloves);
		rubToken.addDialogStep("Please flambe this creme brulee for me.");

		enterDiningRoomAgain = new ObjectStep(this, ObjectID.DOOR_12348, new WorldPoint(3207, 3217, 0), "Go give the Brulee to Sir Amik Varze to finish the quest.", finishedBrulee);
		useBruleeOnVarze = new ObjectStep(this, ObjectID.SIR_AMIK_VARZE_12345, new WorldPoint(1865, 5321, 0), "Give the Brulee to Sir Amik Varze to finish the quest.", finishedBruleeHighlighted);
		useBruleeOnVarze.addIcon(ItemID.BRULEE_SUPREME);
		useBruleeOnVarze.addSubSteps(enterDiningRoomAgain);
	}

	private void calculateEvilChickenLevel()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		int combatLevel = player.getCombatLevel();
		if (combatLevel <= 10)
		{
			evilChickenLevel = 19;
		}
		else if (combatLevel <= 20)
		{
			evilChickenLevel = 38;
		}
		else if (combatLevel <= 40)
		{
			evilChickenLevel = 69;
		}
		else if (combatLevel <= 60)
		{
			evilChickenLevel = 81;
		}
		else if (combatLevel <= 90)
		{
			evilChickenLevel = 121;
		}
		else
		{
			evilChickenLevel = 159;
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(axe, macheteAndRadimus, dramenStaffOrLunar, rawChicken, bucketOfMilk,
			potOfCream, cornflour, dramenBranch, vanillaPod, pestleAndMortar, iceGloves);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, antidragonShield, antifirePotion);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Evil Chicken (level " + evilChickenLevel + ")", "Black dragon (level 227) (Can be safespotted)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestPointRequirement(107));
		req.add(new VarplayerRequirement(QuestVarPlayer.QUEST_LEGENDS_QUEST.getId(), 1, Operation.GREATER_EQUAL,
			"Started Legends' Quest to access the Kharazi Jungle"));
		req.add(new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HEROES_QUEST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SHILO_VILLAGE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.UNDERGROUND_PASS, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WATERFALL_QUEST, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.COOKING, 4000),
				new ExperienceReward(Skill.HITPOINTS, 4000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Evil Chickens Lair"),
				new UnlockReward("Further access to the Culinaromancer's Chest"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(inspectAmik, talkToCook, talkToWom)));
		PanelDetails tokenAndEggPanel = new PanelDetails("Get token and egg", Arrays.asList(enterZanaris, useChickenOnShrine,
			killEvilChicken, pickUpEgg, killBlackDragon, pickUpToken),
			dramenStaffOrLunar, rawChicken, combatGear, antidragonShield, antifirePotion);
		tokenAndEggPanel.setLockingStep(tokenAndEggSteps);
		allSteps.add(tokenAndEggPanel);
		allSteps.add(new PanelDetails("Making the brulee", Arrays.asList(useMilkOnCream, useCornflourOnMilky, addPodToCornflourMixture,
			useEggOnBrulee, grindBranch, useCinnamonOnBrulee, rubToken, useBruleeOnVarze),
			bucketOfMilk, potOfCream, cornflourMixture, pestleAndMortar, dramenBranch, vanillaPod, evilEgg, token, iceGloves));

		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE.getId());
		if (questState == 0)
		{
			return QuestState.NOT_STARTED;
		}

		if (questState < 20)
		{
			return QuestState.IN_PROGRESS;
		}

		return QuestState.FINISHED;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE.getId()) >= 20 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
