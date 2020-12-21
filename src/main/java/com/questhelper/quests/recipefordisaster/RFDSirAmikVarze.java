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

import com.questhelper.BankSlotIcons;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.QuestState;
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

	ConditionForStep inDiningRoom, talkedToWom, inEvilChickenLair, inZanaris, hasEgg, hasEggAndToken, tokenNearby, eggNearby, hasToken,
		hasMilkyMixture, hasCornflourMixture, hasBruleeWithEgg, hasBaseBrulee, hasUncookedBrulee, hasCompleteBrulee, hasCinnamon;

	QuestStep enterDiningRoom, inspectAmik, enterKitchen, talkToCook, enterDiningRoomAgain, useBruleeOnVarze, talkToWom, useMilkOnCream,
		useCornflourOnMilky, addPodToCornflourMixture, enterZanaris, useChickenOnShrine, killEvilChicken, pickUpEgg, useEggOnBrulee,
		killBlackDragon, pickUpToken, grindBranch, useCinnamonOnBrulee, rubToken;

	ConditionalStep tokenAndEggSteps;

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
		tokenAndEggSteps.addStep(new Conditions(hasEgg, inEvilChickenLair), killBlackDragon);
		tokenAndEggSteps.addStep(eggNearby, pickUpEgg);
		tokenAndEggSteps.addStep(inEvilChickenLair, killEvilChicken);
		tokenAndEggSteps.addStep(inZanaris, useChickenOnShrine);
		tokenAndEggSteps.setLockingCondition(hasEggAndToken);
		tokenAndEggSteps.setBlocker(true);

		ConditionalStep makeBrulee = new ConditionalStep(this, useMilkOnCream);
		makeBrulee.addStep(new Conditions(hasUncookedBrulee), rubToken);
		makeBrulee.addStep(new Conditions(hasBruleeWithEgg, hasCinnamon), useCinnamonOnBrulee);
		makeBrulee.addStep(hasBruleeWithEgg, grindBranch);
		makeBrulee.addStep(hasBaseBrulee, useEggOnBrulee);
		makeBrulee.addStep(hasCornflourMixture, addPodToCornflourMixture);
		makeBrulee.addStep(hasMilkyMixture, useCornflourOnMilky);

		ConditionalStep saveAmik = new ConditionalStep(this, talkToWom);
		saveAmik.addStep(new Conditions(inDiningRoom, hasCompleteBrulee), useBruleeOnVarze);
		saveAmik.addStep(hasCompleteBrulee, enterDiningRoomAgain);
		saveAmik.addStep(hasEggAndToken, makeBrulee);
		saveAmik.addStep(talkedToWom, tokenAndEggSteps);
		steps.put(10, saveAmik);

		return steps;
	}

	public void setupRequirements()
	{
		bucketOfMilk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		bucketOfMilk.setHighlightInInventory(true);
		potOfCream = new ItemRequirement("Pot of cream", ItemID.POT_OF_CREAM);
		potOfCream.setHighlightInInventory(true);
		cornflour = new ItemRequirement("Pot of cornflour", ItemID.POT_OF_CORNFLOUR);
		cornflour.setTip("You can make this by grinding a sweetcorn in a windmill and collecting it with a pot");
		cornflour.setHighlightInInventory(true);

		pot = new ItemRequirement("Pot", ItemID.POT);
		sweetcorn = new ItemRequirement("Sweetcorn", ItemID.SWEETCORN);

		axe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		machete = new ItemRequirement("Any machete", ItemID.MACHETE);
		machete.addAlternates(ItemID.RED_TOPAZ_MACHETE, ItemID.OPAL_MACHETE, ItemID.JADE_MACHETE);
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
			macheteAndRadimus.addAlternates(ItemID.RED_TOPAZ_MACHETE, ItemID.OPAL_MACHETE, ItemID.JADE_MACHETE);
		}
		vanillaPod = new ItemRequirement("Vanilla pod", ItemID.VANILLA_POD);
		vanillaPod.setTip("You can get a pod from the Khazari Jungle. Bring an axe and machete to get in");
		vanillaPod.setHighlightInInventory(true);

		dramenStaffOrLunar = new ItemRequirement("Dramen/lunar staff", ItemID.DRAMEN_STAFF, 1, true);
		dramenStaffOrLunar.addAlternates(ItemID.LUNAR_STAFF);

		dramenBranch = new ItemRequirement("Dramen branch", ItemID.DRAMEN_BRANCH);
		dramenBranch.setHighlightInInventory(true);
		dramenBranch.setTip("You can get one from the dramen tree under Entrana");
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted.setHighlightInInventory(true);

		iceGloves = new ItemRequirement("Ice gloves", ItemID.ICE_GLOVES, 1, true);
		iceGloves.setTip("Although optional, you'll take a lot of damage if you're not wearing them");
		rawChicken = new ItemRequirement("Raw chicken", ItemID.RAW_CHICKEN);
		rawChicken.setHighlightInInventory(true);
		antidragonShield = new ItemRequirement("Anti-dragon shield", ItemID.ANTIDRAGON_SHIELD);
		antifirePotion = new ItemRequirement("Antifire potion", ItemID.ANTIFIRE_POTION4);
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
		inDiningRoom = new ZoneCondition(diningRoom);
		// 1911 0->1->2->3->4->5->6 status of brulee
		// 1912 0->1 picked up token
		// 1913 0->1 evil chicken killed
		talkedToWom = new VarbitCondition(1919, 1, Operation.GREATER_EQUAL);
		// 1919 = 2 when entered black dragon lair once

		inEvilChickenLair = new ZoneCondition(evilChickenLair);
		inZanaris = new ZoneCondition(zanaris);
		hasEgg = new ItemRequirementCondition(evilEgg);
		hasToken = new ItemRequirementCondition(token);
		hasEggAndToken = new Conditions(hasEgg, hasToken);
		hasCornflourMixture = new ItemRequirementCondition(cornflourMixture);
		hasMilkyMixture = new ItemRequirementCondition(milkyMixture);
		tokenNearby = new ItemCondition(token);
		eggNearby = new ItemCondition(evilEgg);
		hasBruleeWithEgg = new ItemRequirementCondition(bruleeWithEgg);
		hasBaseBrulee = new ItemRequirementCondition(baseBrulee);
		hasUncookedBrulee = new ItemRequirementCondition(uncookedBrulee);
		hasCompleteBrulee = new ItemRequirementCondition(finishedBrulee);
		hasCinnamon = new ItemRequirementCondition(cinnamon);
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
		killEvilChicken = new NpcStep(this, NpcID.EVIL_CHICKEN, new WorldPoint(2455, 4399, 0), "Kill the Evil Chicken in the north of the range.");
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
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(axe, macheteAndRadimus, dramenStaffOrLunar, rawChicken, bucketOfMilk, potOfCream, cornflour, pestleAndMortar, iceGloves));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(combatGear, antidragonShield, antifirePotion));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Evil Chicken (level " + evilChickenLevel + ")", "Black dragon (level 227)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(inspectAmik, talkToCook, talkToWom))));
		PanelDetails tokenAndEggPanel = new PanelDetails("Get token and egg", new ArrayList<>(Arrays.asList(enterZanaris, useChickenOnShrine, killEvilChicken, pickUpEgg, killBlackDragon, pickUpToken)),
			dramenStaffOrLunar, rawChicken, combatGear, antidragonShield, antifirePotion);
		tokenAndEggPanel.setLockingStep(tokenAndEggSteps);
		allSteps.add(tokenAndEggPanel);
		allSteps.add(new PanelDetails("Making the brulee", new ArrayList<>(Arrays.asList(useMilkOnCream, useCornflourOnMilky, addPodToCornflourMixture, useEggOnBrulee, grindBranch, useCinnamonOnBrulee, rubToken, useBruleeOnVarze)), bucketOfMilk, potOfCream, cornflourMixture, pestleAndMortar, dramenBranch, vanillaPod, evilEgg, token));

		return allSteps;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_SIR_AMIK_VARZE.getId()) >= 20 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
