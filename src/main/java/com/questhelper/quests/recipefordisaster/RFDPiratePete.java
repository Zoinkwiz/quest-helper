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
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.WeightRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_PIRATE_PETE
)
public class RFDPiratePete extends BasicQuestHelper
{
	ItemRequirement combatGear, pestleHighlighted, rawCodHighlighted, knifeHighlighted, breadHighlighted, divingAparatus, divingHelmet, fishBowl, bronzeWire3, needle, fishCake,
		fishCakeHighlighted, rocks5, mudskipperHide5, crabMeat, kelp, groundKelpHighlighted, groundCrabMeatHighlighted, kelpHighlighted, crabMeatHighlighted,
		rawFishCake, groundCod, breadcrumbs;

	WeightRequirement canSwim;

	ConditionForStep inDiningRoom, askedCookOptions, inUnderWater, hasEnoughRocks, has5Hide, hasCrabMeat, hasKelp, hasGroundKelp, hasGroundCrabMeat,
		walkingUnderwater, hasCake, hasRawCake, hasGroundCod, hasBreadcrumbs;

	QuestStep enterDiningRoom, inspectPete, enterKitchen, usePestleOnCod, useKnifeOnBread, talkToMurphy, talkToMurphyAgain, goDiving,
		pickKelp, talkToNung, pickUpRocks, enterCave, killMudksippers5, returnToNung, talkToNungAgain, giveNungWire, killCrab, grindKelp, grindCrab, climbAnchor,
		talkToCookAgain, useCrabOnKelp, cookCake, enterDiningRoomAgain, useCakeOnPete, goDivingAgain, pickUpRocksAgain;

	AskAboutFishCake talkToCook;

	Zone diningRoom, underwater;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectPete = new ConditionalStep(this, enterDiningRoom);
		goInspectPete.addStep(inDiningRoom, inspectPete);
		steps.put(0, goInspectPete);

		ConditionalStep goTalkToCook = new ConditionalStep(this, talkToCook);
		goTalkToCook.addStep(askedCookOptions, talkToMurphy);
		steps.put(10, goTalkToCook);
		steps.put(20, goTalkToCook);

		steps.put(30, talkToMurphyAgain);

		ConditionalStep goTalkToNung = new ConditionalStep(this, goDiving);
		goTalkToNung.addStep(new Conditions(hasKelp, inUnderWater), talkToNung);
		goTalkToNung.addStep(inUnderWater, pickKelp);
		steps.put(40, goTalkToNung);

		ConditionalStep goGetHides = new ConditionalStep(this, goDiving);
		goGetHides.addStep(new Conditions(hasKelp, inUnderWater, has5Hide), returnToNung);
		goGetHides.addStep(new Conditions(hasKelp, inUnderWater, walkingUnderwater), killMudksippers5);
		goGetHides.addStep(new Conditions(hasKelp, inUnderWater, hasEnoughRocks), enterCave);
		goGetHides.addStep(new Conditions(hasKelp, inUnderWater), pickUpRocks);
		goGetHides.addStep(inUnderWater, pickKelp);
		steps.put(50, goGetHides);

		ConditionalStep goTalkToNungAgain = new ConditionalStep(this, goDiving);
		goTalkToNungAgain.addStep(new Conditions(hasKelp, inUnderWater), talkToNungAgain);
		goTalkToNungAgain.addStep(inUnderWater, pickKelp);
		steps.put(60, goTalkToNungAgain);

		ConditionalStep goBringNungWire = new ConditionalStep(this, goDiving);
		goBringNungWire.addStep(new Conditions(hasKelp, inUnderWater), giveNungWire);
		goBringNungWire.addStep(inUnderWater, pickKelp);
		steps.put(70, goBringNungWire);

		ConditionalStep goLearnHowToMakeFishCake = new ConditionalStep(this, goDivingAgain);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasCake, inDiningRoom), useCakeOnPete);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasCake), enterDiningRoomAgain);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasRawCake), cookCake);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasKelp, hasCrabMeat, inUnderWater), climbAnchor);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasKelp, inUnderWater, hasEnoughRocks), killCrab);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasKelp, inUnderWater), pickUpRocksAgain);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasGroundKelp, hasGroundCrabMeat, hasGroundCod, hasBreadcrumbs), talkToCookAgain);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasGroundKelp, hasGroundCrabMeat, hasGroundCod), useKnifeOnBread);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasGroundKelp, hasGroundCrabMeat), usePestleOnCod);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasKelp, hasGroundCrabMeat), grindKelp);
		goLearnHowToMakeFishCake.addStep(new Conditions(hasKelp, hasCrabMeat), grindCrab);
		goLearnHowToMakeFishCake.addStep(inUnderWater, pickKelp);
		steps.put(80, goLearnHowToMakeFishCake);
		steps.put(90, goLearnHowToMakeFishCake);

		ConditionalStep savePete = new ConditionalStep(this, useCrabOnKelp);
		savePete.addStep(new Conditions(hasCake, inDiningRoom), useCakeOnPete);
		savePete.addStep(new Conditions(hasCake), enterDiningRoomAgain);
		savePete.addStep(new Conditions(hasRawCake), cookCake);
		steps.put(100, savePete);

		return steps;
	}

	public void setupRequirements()
	{
		canSwim = new WeightRequirement("Weight less than 27kg", 26, Operation.LESS_EQUAL);

		pestleHighlighted = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleHighlighted.setHighlightInInventory(true);
		rawCodHighlighted = new ItemRequirement("Raw cod", ItemID.RAW_COD);
		rawCodHighlighted.setHighlightInInventory(true);
		knifeHighlighted = new ItemRequirement("Knife", ItemID.KNIFE);
		knifeHighlighted.setHighlightInInventory(true);
		breadHighlighted = new ItemRequirement("Bread", ItemID.BREAD);
		breadHighlighted.setTip("You can make this by using a knife on bread");
		breadHighlighted.setHighlightInInventory(true);
		divingAparatus = new ItemRequirement("Diving apparatus", ItemID.DIVING_APPARATUS, 1, true);
		divingHelmet = new ItemRequirement("Fishbowl helmet", ItemID.FISHBOWL_HELMET, 1, true);
		fishBowl = new ItemRequirement("Fish bowl", ItemID.EMPTY_FISHBOWL);
		bronzeWire3 = new ItemRequirement("Bronze wire", ItemID.BRONZE_WIRE, 3);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE);
		fishCake = new ItemRequirement("Cooked fishcake", ItemID.COOKED_FISHCAKE);
		fishCakeHighlighted = new ItemRequirement("Cooked fishcake", ItemID.COOKED_FISHCAKE);
		fishCakeHighlighted.setHighlightInInventory(true);
		rocks5 = new ItemRequirement("Rock", ItemID.ROCK_7533, 5);
		mudskipperHide5 = new ItemRequirement("Mudskipper hide", ItemID.MUDSKIPPER_HIDE, 5);
		kelp = new ItemRequirement("Kelp", ItemID.KELP);
		crabMeat = new ItemRequirement("Crab meat", ItemID.CRAB_MEAT);
		crabMeat.addAlternates(ItemID.CRAB_MEAT_7519);
		kelpHighlighted = new ItemRequirement("Kelp", ItemID.KELP);
		kelpHighlighted.setHighlightInInventory(true);
		crabMeatHighlighted = new ItemRequirement("Crab meat", ItemID.CRAB_MEAT);
		crabMeatHighlighted.addAlternates(ItemID.CRAB_MEAT_7519);
		crabMeatHighlighted.setHighlightInInventory(true);
		groundCrabMeatHighlighted = new ItemRequirement("Ground kelp", ItemID.GROUND_CRAB_MEAT);
		groundCrabMeatHighlighted.setTip("You will need to kill another underwater crab and grind its meat");
		groundCrabMeatHighlighted.setHighlightInInventory(true);
		groundKelpHighlighted = new ItemRequirement("Ground kelp", ItemID.GROUND_KELP);
		groundKelpHighlighted.setTip("You will need to go underwater with Murphy and pick more kelp to grind");
		groundKelpHighlighted.setHighlightInInventory(true);
		rawFishCake = new ItemRequirement("Raw fishcake", ItemID.RAW_FISHCAKE);
		groundCod = new ItemRequirement("Ground cod", ItemID.GROUND_COD);
		groundCod.setTip("You can make this by use a pestle and mortar on a raw cod");
		breadcrumbs = new ItemRequirement("Breadcrumbs", ItemID.BREADCRUMBS);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		underwater = new Zone(new WorldPoint(2944, 9472, 1), new WorldPoint(3007, 9534, 1));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneCondition(diningRoom);
		walkingUnderwater = new VarbitCondition(1871, 1);

		askedCookOptions = new Conditions(
			new VarbitCondition(1873, 1),
			new VarbitCondition(1876, 1),
			new VarbitCondition(1877, 1));

		inUnderWater = new ZoneCondition(underwater);

		hasEnoughRocks = new VarbitCondition(1869, 5);

		has5Hide = new ItemRequirementCondition(mudskipperHide5);
		hasGroundCrabMeat = new ItemRequirementCondition(groundCrabMeatHighlighted);
		hasCrabMeat = new Conditions(LogicType.OR, new ItemRequirementCondition(crabMeat), hasGroundCrabMeat);
		hasGroundKelp = new ItemRequirementCondition(groundKelpHighlighted);
		hasKelp = new Conditions(LogicType.OR, new ItemRequirementCondition(kelp), hasGroundKelp);
		hasCake = new ItemRequirementCondition(fishCake);
		hasRawCake = new ItemRequirementCondition(rawFishCake);
		hasGroundCod = new ItemRequirementCondition(groundCod);
		hasBreadcrumbs = new ItemRequirementCondition(breadcrumbs);

		// 1852 = number of people saved
		// Talked to cook through base dialog: 1854 0->1

		// 1860 = 1->0 is Pirate Pete gone from dining room
		// 1872 = 0->1 when first going underwater
		// 1875 0->5, Given hides
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0), "Go inspect Pirate Pete.");
		inspectPete = new ObjectStep(this, ObjectID.PIRATE_PETE_12337, new WorldPoint(1862, 5323, 0), "Inspect Pirate Pete.");
		inspectPete.addSubSteps(enterDiningRoom);

		enterKitchen = new ObjectStep(this, ObjectID.BARRIER_12351, new WorldPoint(1861, 5316, 0), "Talk to the Lumbridge Cook.");
		talkToCook = new AskAboutFishCake(this);
		talkToCook.addSubSteps(enterKitchen);
		usePestleOnCod = new DetailedQuestStep(this, "Use a pestle and mortar on a raw cod.", pestleHighlighted, rawCodHighlighted);
		useKnifeOnBread = new DetailedQuestStep(this, "Use a knife on some bread.", knifeHighlighted, breadHighlighted);
		talkToMurphy = new NpcStep(this, NpcID.MURPHY, new WorldPoint(2664, 3160, 0), "Talk to Murphy in Port Khazard.", fishBowl);
		talkToMurphy.addDialogStep("Talk about Recipe for Disaster.");
		talkToMurphyAgain = new NpcStep(this, NpcID.MURPHY, new WorldPoint(2664, 3160, 0), "Talk to Murphy again.", fishBowl);
		talkToMurphyAgain.addDialogStep("Talk about Recipe for Disaster.");
		goDiving = new NpcStep(this, NpcID.MURPHY, new WorldPoint(2664, 3160, 0), "Talk to Murphy again to go diving.", divingAparatus, divingHelmet, bronzeWire3, needle, canSwim);
		goDiving.addDialogSteps("Talk about Recipe for Disaster.", "Yes, Let's go diving.");
		pickKelp = new ObjectStep(this, ObjectID.KELP_12478, new WorldPoint(2966, 9490, 1), "Pick some kelp. Get 3 or so in case you burn a fishcake or two.");
		talkToNung = new NpcStep(this, NpcID.NUNG, new WorldPoint(2971, 9513, 1), "Talk to Nung in the north of the area.");
		pickUpRocks = new DetailedQuestStep(this, new WorldPoint(2950, 9511, 1), "Pick up 5 rocks in the west of the area.", rocks5);
		enterCave = new ObjectStep(this, ObjectID.UNDERWATER_CAVERN_ENTRANCE_12461, new WorldPoint(2950, 9516, 1), "Enter the underwater cave entrance.");
		killMudksippers5 = new NpcStep(this, NpcID.MUDSKIPPER, new WorldPoint(2951, 9526, 1), "Kill mudskippers for 5 hides.", true, mudskipperHide5);
		((NpcStep)(killMudksippers5)).addAlternateNpcs(NpcID.MUDSKIPPER_4821);
		returnToNung = new NpcStep(this, NpcID.NUNG, new WorldPoint(2971, 9513, 1), "Bring the hides to Nung.", mudskipperHide5);
		talkToNungAgain = new NpcStep(this, NpcID.NUNG, new WorldPoint(2971, 9513, 1), "Talk to Nung again.");
		returnToNung.addSubSteps(talkToNungAgain);

		giveNungWire = new NpcStep(this, NpcID.NUNG, new WorldPoint(2971, 9513, 1), "Bring bronze wire and a needle to Nung.", bronzeWire3, needle);
		goDivingAgain = new NpcStep(this, NpcID.MURPHY, new WorldPoint(2664, 3160, 0), "Talk to Murphy again to go diving.", divingAparatus, divingHelmet, canSwim);
		goDiving.addDialogSteps("Talk about Recipe for Disaster.", "Yes, Let's go diving.");
		pickUpRocksAgain = new DetailedQuestStep(this, new WorldPoint(2950, 9511, 1), "Pick up 5 rocks so you can kill the crabs.", rocks5);
		killCrab = new NpcStep(this, NpcID.CRAB_4819, new WorldPoint(2977, 9518, 1), "Kill the crabs for some crab meat. Get 2-3 to be safe.", true, crabMeat);
		killCrab.addSubSteps(pickUpRocksAgain);
		grindKelp = new DetailedQuestStep(this, "Use a pestle and mortar on the kelp.", pestleHighlighted, kelpHighlighted);
		grindCrab = new DetailedQuestStep(this, "Use a pestle and mortar on the crab.", pestleHighlighted, crabMeatHighlighted);
		climbAnchor = new ObjectStep(this, ObjectID.ANCHOR_12475, new WorldPoint(2963, 9477, 1), "Climb the anchor to the south to return to the surface.");
		talkToCookAgain = new NpcStep(this, NpcID.COOK_4626, new WorldPoint(3209, 3215, 0),
			"Talk to the Lumbridge Cook about Pirate Pete again.", groundCod, groundCrabMeatHighlighted, groundKelpHighlighted, breadcrumbs);
		talkToCookAgain.addDialogStep("Protecting the Pirate");
		useCrabOnKelp = new DetailedQuestStep(this, "Use the ingredients together to make the cake.", groundCrabMeatHighlighted, groundKelpHighlighted, groundCod, breadcrumbs);
		cookCake = new DetailedQuestStep(this, "Cook the fishcake. It's possible to burn it.", rawFishCake);
		enterDiningRoomAgain = new ObjectStep(this, ObjectID.DOOR_12348, new WorldPoint(3207, 3217, 0), "Go give the fish cake to Pirate Pete to finish the quest.", fishCake);
		useCakeOnPete = new ObjectStep(this, ObjectID.PIRATE_PETE_12337, new WorldPoint(1862, 5323, 0), "Give the fish cake to Pirate Pete to finish the quest. BE CAREFUL NOT TO EAT IT.", fishCakeHighlighted);
		useCakeOnPete.addIcon(ItemID.COOKED_FISHCAKE);
		useCakeOnPete.addSubSteps(enterDiningRoomAgain);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(fishBowl, needle, bronzeWire3, pestleHighlighted, rawCodHighlighted, breadHighlighted, knifeHighlighted));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Collections.singletonList(combatGear));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("5 Mudskippers (level 30/31)", "Crab (level 21/23)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(inspectPete, talkToCook, talkToMurphy, talkToMurphyAgain)), fishBowl));
		allSteps.add(new PanelDetails("Get Crab and Kelp", new ArrayList<>(Arrays.asList(goDiving, pickKelp, talkToNung, pickUpRocks, enterCave, killMudksippers5, returnToNung, giveNungWire, killCrab, climbAnchor)), divingHelmet, divingAparatus, bronzeWire3, needle));
		allSteps.add(new PanelDetails("Saving Pete", new ArrayList<>(Arrays.asList(grindCrab, grindKelp, usePestleOnCod, useKnifeOnBread, talkToCookAgain, useCrabOnKelp, cookCake, useCakeOnPete)), pestleHighlighted, knifeHighlighted, rawCodHighlighted, breadHighlighted, crabMeat, kelp));
		return allSteps;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(1895) >= 110 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
