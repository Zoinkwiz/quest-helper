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

package com.questhelper.quests.gettingahead;

import com.questhelper.BankSlotIcons;
import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.runelite.api.*;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
        quest = QuestHelperQuest.GETTING_AHEAD
)
public class GettingAhead extends BasicQuestHelper
{
	//Items Recommended
	ItemRequirement food, staminaPotions, combatGear, skillsNeck, clayHead, clayHeadHighlighted, furHead, furHeadHighlighted, bloodyHead;

	//Items Required
	ItemRequirement bearFur, softClay, hammer, saw, planks, nails, knife, redDye, potOfFlour, needle, thread;

	//Other items used
	ItemRequirement clay, pickaxe, bucket, bucketOfWater, itemsTip;

	//NPC Steps
	QuestStep talkToGordon1, talkToMary, talkToGordon2, talkToMary2, talkToGordonGen, talkToGordonGen2, talkToGordonGen3, talkToGordonFinal;

	//Object/items Steps
	QuestStep usePotOfFlour, goToMine, leaveCave, killBeast, useKnifeOnClay, useFurOnHead, useDyeOnHead, buildBearHead;

	//Getting items steps
	QuestStep goUpstairsHouse, takePot, goDownstairsHouse, getNeedle, getThread, takePickaxe, mineClay, takeKnife, fillBucket, wetClay, takeBucket, takeDye,
		takeNails, takeSaw, takeHammer, takePlanks;

	ConditionalStep goUseFlourOnGate, returnToGordon, makeClayHead, addFurToHead, dyeHead, putUpHead;

	//Conditions
	ConditionForStep inMine, inUpstairsHouse, hasClayHead, hasFurHead, hasBloodyHead, hasPotOfFlour, hasClay, hasPickaxe, hasSoftClay, hasKnife, hasBucket,
		hasBucketOfWater, hasNeedle, hasThread, hasRedDye, hasPlanks2, hasNails6, hasHammer, hasSaw;

	//Zones
	Zone kebosMine, upstairsHouse;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGordon1);
		steps.put(2, talkToMary);

		goUseFlourOnGate = new ConditionalStep(this, goUpstairsHouse, "Use the pot of flour on the cow pen gate.");
		goUseFlourOnGate.addStep(new Conditions(inUpstairsHouse, hasPotOfFlour), goDownstairsHouse);
		goUseFlourOnGate.addStep(new Conditions(hasPotOfFlour), usePotOfFlour);
		goUseFlourOnGate.addStep(inUpstairsHouse, takePot);
		steps.put(4, goUseFlourOnGate);

		ConditionalStep killBeastStep = new ConditionalStep(this, goToMine);
		killBeastStep.addStep(inMine, killBeast);
		steps.put(6, killBeastStep);
		steps.put(8, killBeastStep);
		steps.put(10, killBeastStep);
		steps.put(12, killBeastStep);

		returnToGordon = new ConditionalStep(this, takePickaxe, "Return to Gordon and talk to him.");
		returnToGordon.addStep(inMine, leaveCave);
		returnToGordon.addStep(new Conditions(new Conditions(LogicType.OR, hasClay, hasSoftClay), hasPlanks2), talkToGordon2);
		returnToGordon.addStep(new Conditions(LogicType.OR, hasClay, hasSoftClay), takePlanks);
		returnToGordon.addStep(hasPickaxe, mineClay);

		steps.put(14, returnToGordon);
		steps.put(16, talkToMary2);

		makeClayHead = new ConditionalStep(this, takePickaxe, "Use the knife on the soft clay then talk to Gordon.");
		makeClayHead.addStep(new Conditions(hasSoftClay, hasKnife), useKnifeOnClay);
		makeClayHead.addStep(hasSoftClay, takeKnife);
		makeClayHead.addStep(new Conditions(hasBucketOfWater, hasClay), wetClay);
		makeClayHead.addStep(new Conditions(hasBucket, hasClay), fillBucket);
		makeClayHead.addStep(hasClay, takeBucket);
		makeClayHead.addStep(hasPickaxe, mineClay);
		steps.put(18, makeClayHead);

		steps.put(20, talkToGordonGen);
		addFurToHead = new ConditionalStep(this, goUpstairsHouse, "Use the bear fur on the clay head then talk to Gordon.");
		addFurToHead.addStep(new Conditions(hasThread, hasNeedle), useFurOnHead);
		addFurToHead.addStep(new Conditions(inUpstairsHouse, hasNeedle), getThread);
		addFurToHead.addStep(inUpstairsHouse, getNeedle);
		steps.put(22, addFurToHead);
		steps.put(24, talkToGordonGen2);

		dyeHead = new ConditionalStep(this, takeDye, "Use the red dye on the fur head then talk to Gordon.");
		dyeHead.addStep(hasRedDye, useDyeOnHead);
		steps.put(26, dyeHead);
		steps.put(28, talkToGordonGen3);

		putUpHead = new ConditionalStep(this, takePlanks, "Build the Mounted Head Space inside the farmhouse.");
		putUpHead.addStep(new Conditions(hasPlanks2, hasHammer, hasSaw, hasNails6), buildBearHead);
		putUpHead.addStep(new Conditions(hasPlanks2, hasHammer, hasSaw), takeNails);
		putUpHead.addStep(new Conditions(hasPlanks2, hasHammer), takeSaw);
		putUpHead.addStep(hasPlanks2, takeHammer);
		steps.put(30, putUpHead);
		steps.put(32, talkToGordonFinal);

		return steps;
	}

	public void setupItemRequirements()
	{
		itemsTip = new ItemRequirement("You can get all the required items during the quest.", -1, -1);

		//Recommended
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
		staminaPotions = new ItemRequirement("Stamina Potion", ItemCollections.getStaminaPotions());
		skillsNeck = new ItemRequirement("Skills Necklace", ItemCollections.getSkillsNecklaces());

		//Required
		bearFur = new ItemRequirement("Bear Fur", ItemID.BEAR_FUR);
		bearFur.setTip("You can kill a bear west of the farm for some fur");
		bearFur.setHighlightInInventory(true);
		softClay = new ItemRequirement("Soft Clay", ItemID.SOFT_CLAY, 1);
		softClay.setHighlightInInventory(true);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER, 1);
		saw = new ItemRequirement("Saw", ItemID.SAW, 1);
		planks = new ItemRequirement("Planks", ItemID.PLANK, 2);
		nails = new ItemRequirement("Nails", ItemCollections.getNails(), 6);
		knife = new ItemRequirement("Knife", ItemID.KNIFE, 1);
		knife.setHighlightInInventory(true);
		redDye = new ItemRequirement("Red Dye", ItemID.RED_DYE, 1);
		redDye.setHighlightInInventory(true);
		potOfFlour = new ItemRequirement("Pot of Flour", ItemID.POT_OF_FLOUR, 1);
		potOfFlour.setHighlightInInventory(true);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE, 1);
		thread = new ItemRequirement("Thread", ItemID.THREAD, 1);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		clay = new ItemRequirement("Clay", ItemID.CLAY);
		clay.setHighlightInInventory(true);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucket.setHighlightInInventory(true);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.setHighlightInInventory(true);

		//Making the fake head
		clayHead = new ItemRequirement("Clay Head", ItemID.CLAY_HEAD);
		clayHead.setTip("You can make another by using a knife on some soft clay");

		clayHeadHighlighted = new ItemRequirement("Clay Head", ItemID.CLAY_HEAD);
		clayHeadHighlighted.setTip("You can make another by using a knife on some soft clay");
		clayHeadHighlighted.setHighlightInInventory(true);
		furHead = new ItemRequirement("Fur Head", ItemID.FUR_HEAD);
		furHead.setTip("You can make another by using a knife on soft clay, then adding bear fur");
		furHeadHighlighted = new ItemRequirement("Fur Head", ItemID.FUR_HEAD);
		furHeadHighlighted.setTip("You can make another by using a knife on soft clay, then adding bear fur");
		furHeadHighlighted.setHighlightInInventory(true);
		bloodyHead = new ItemRequirement("Bloody Head", ItemID.BLOODY_HEAD);
	}

	public void setupConditions()
	{
		inMine = new ZoneCondition(kebosMine);
		inUpstairsHouse = new ZoneCondition(upstairsHouse);

		hasPotOfFlour = new ItemRequirementCondition(potOfFlour);
		hasPickaxe = new ItemRequirementCondition(pickaxe);
		hasClay = new ItemRequirementCondition(clay);
		hasSoftClay = new ItemRequirementCondition(softClay);
		hasKnife = new ItemRequirementCondition(knife);
		hasBucket = new ItemRequirementCondition(bucket);
		hasBucketOfWater = new ItemRequirementCondition(bucketOfWater);
		hasNeedle = new ItemRequirementCondition(needle);
		hasThread = new ItemRequirementCondition(thread);
		hasPlanks2 = new ItemRequirementCondition(planks);
		hasSaw = new ItemRequirementCondition(saw);
		hasHammer = new ItemRequirementCondition(hammer);
		hasNails6 = new ItemRequirementCondition(nails);
		hasRedDye = new ItemRequirementCondition(redDye);

		hasClayHead = new ItemRequirementCondition(clayHeadHighlighted);
		hasFurHead = new ItemRequirementCondition(furHeadHighlighted);
		hasBloodyHead = new ItemRequirementCondition(bloodyHead);
	}

	public void loadZones()
	{
		kebosMine = new Zone(new WorldPoint(1174, 10000, 0), new WorldPoint(1215, 10035, 0));
		upstairsHouse = new Zone(new WorldPoint(1238, 3677, 1), new WorldPoint(1244, 3687, 1));
	}

	public void setupSteps()
	{
		//Starting Off
		talkToGordon1 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk to Gordon south of the Farming Guild.");
		talkToGordon1.addDialogStep("Need a hand with anything?");
		talkToGordon1.addDialogStep("Yes.");

		talkToMary = new NpcStep(this, NpcID.MARY_10502, new WorldPoint(1237, 3678, 0), "Talk to Mary inside the main building, west of Gordon.");

		//Killing the beast
		goUpstairsHouse = new ObjectStep(this, ObjectID.STAIRCASE_34502, new WorldPoint(1240, 3686, 0), "Climb up the nearby house's staircase.");
		takePot = new ItemStep(this, "Take the nearby pot of flour, or fill a pot with the barrel of flour in the south west room.", potOfFlour);
		goDownstairsHouse = new ObjectStep(this, ObjectID.STAIRCASE_34503, new WorldPoint(1240, 3686, 1), "Go back downstairs.");

		usePotOfFlour = new ObjectStep(this, NullObjectID.NULL_40427, new WorldPoint(1257, 3686, 0), "", potOfFlour);
		usePotOfFlour.addIcon(ItemID.POT_OF_FLOUR);

		goToMine = new ObjectStep(this, ObjectID.CAVE_20852, new WorldPoint(1212, 3647, 0), "Enter the Kebos Lowlands mine just west of the bridge and kill the Headless Beast (level 82).");
		goToMine.addDialogStep("Yes.");
		killBeast = new NpcStep(this, NpcID.HEADLESS_BEAST_10506, new WorldPoint(1191, 10021, 0), "Kill the headless beast.");
		goToMine.addSubSteps(killBeast);

		talkToGordon2 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "");
		leaveCave = new ObjectStep(this, ObjectID.CAVE_20853, new WorldPoint(1190, 10029, 0), "Leave the cave");
		talkToGordon2.addSubSteps(leaveCave);

		//Making the fake head
		talkToGordonGen = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk with Gordon.", clayHead);
		talkToGordonGen2 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk with Gordon.", furHead);
		talkToGordonGen3 = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Talk with Gordon.", bloodyHead);

		talkToMary2 = new NpcStep(this, NpcID.MARY_10502, new WorldPoint(1237, 3678, 0), "Talk to Mary inside the house.");
		takePickaxe = new ObjectStep(this, ObjectID.ROCKS_40365, new WorldPoint(1222, 3653, 0), "Take the pickaxe in the mine.");
		mineClay = new ObjectStep(this, ObjectID.ROCKS_11362, new WorldPoint(1217, 3657, 0), "Mine some clay.", pickaxe);
		takeKnife = new DetailedQuestStep(this, new WorldPoint(1241, 3679, 0), "Take the knife near Mary.", knife);
		takeBucket = new DetailedQuestStep(this, new WorldPoint(1244, 3682, 0), "Take the bucket in the house.", bucket);
		fillBucket = new ObjectStep(this, ObjectID.SINK_1763, new WorldPoint(1240, 3677, 0), "Fill the bucket on the sink.", bucket);
		fillBucket.addIcon(ItemID.BUCKET);
		wetClay = new DetailedQuestStep(this, "Use the bucket of water on the clay.", bucketOfWater, clay);
		useKnifeOnClay = new DetailedQuestStep(this, "", knife, softClay);
		useKnifeOnClay.addDialogStep("Yes.");

		getNeedle = new DetailedQuestStep(this, new WorldPoint(1244, 3685, 1), "Take the needle.", needle);
		getThread = new DetailedQuestStep(this, new WorldPoint(1244, 3684, 1), "Take the thread.", thread);
		useFurOnHead = new DetailedQuestStep(this, "Use the bear fur on the clay head then talk to Gordon.", bearFur, clayHeadHighlighted, thread, needle);
		useFurOnHead.addDialogStep("Yes.");

		takeDye = new ObjectStep(this, ObjectID.SHELVES_40362, new WorldPoint(1240, 3688, 0), "Get some red dye from the shelves in the farm house.");
		takeDye.addDialogStep("Take some red dye.");
		useDyeOnHead = new DetailedQuestStep(this, "", redDye, furHeadHighlighted);
		useDyeOnHead.addDialogStep("Yes.");
		useDyeOnHead.addSubSteps(talkToGordonGen3);

		takePlanks = new ItemStep(this, new WorldPoint(1202, 3649, 0), "Take the planks outside the monster's cave.", planks);
		takeSaw = new DetailedQuestStep(this, new WorldPoint(1239, 3696, 0), "Take the saw in the building north of the farmhouse.", saw);
		takeHammer = new DetailedQuestStep(this, new WorldPoint(1259, 3686, 0), "Take the hammer east of the farmhouse.", hammer);
		takeNails = new ObjectStep(this, ObjectID.WORKBENCH_40364, new WorldPoint(1239, 3699, 0), "Take some nails from the farm's workbench.");
		buildBearHead = new ObjectStep(this, NullObjectID.NULL_20858, new WorldPoint(1240, 3683, 0), "", bloodyHead, nails, planks, saw, hammer);
		buildBearHead.addDialogStep("Yes.");
		talkToGordonFinal = new NpcStep(this, NpcID.GORDON, new WorldPoint(1248, 3686, 0), "Return to Gordon to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(itemsTip, bearFur, softClay, hammer, saw, planks, nails, knife, redDye, potOfFlour, needle, thread));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(food, staminaPotions, combatGear, skillsNeck));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Headless Beast (level 82, safespottable)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToGordon1, talkToMary))));
		allSteps.add(new PanelDetails("Killing the Beast", new ArrayList<>(Arrays.asList(goUseFlourOnGate, goToMine, returnToGordon))));
		allSteps.add(new PanelDetails("Making the fake head", new ArrayList<>(Arrays.asList(talkToMary2, makeClayHead, addFurToHead, useDyeOnHead, putUpHead, talkToGordonFinal))));
		return allSteps;
	}
}
