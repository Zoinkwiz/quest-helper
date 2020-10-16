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

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_LUMBRIDGE_GUIDE
)
public class RFDLumbridgeGuide extends BasicQuestHelper
{
	ItemRequirement milk, egg, flour, tin, rawGuidanceCake, guidanceCake, guidanceCakeHighlighted, enchantedEgg, enchantedMilk,
		enchantedFlour, tinHighlighted;

	ConditionForStep inDiningRoom, inUpstairsTrailborn, hasCake, hasRawCake;

	QuestStep enterDiningRoom, inspectLumbridgeGuide, goUpToTraiborn, talkToTraiborn, cookCake, enterDiningRoomAgain,
		useCakeOnLumbridgeGuide, mixIngredients;

	Zone diningRoom, upstairsTrailborn, quizSpot;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectGuide = new ConditionalStep(this, enterDiningRoom);
		goInspectGuide.addStep(inDiningRoom, inspectLumbridgeGuide);
		steps.put(0, goInspectGuide);

		ConditionalStep goTalkToTrailborn = new ConditionalStep(this, goUpToTraiborn);
		goTalkToTrailborn.addStep(inUpstairsTrailborn, talkToTraiborn);
		steps.put(1, goTalkToTrailborn);
		steps.put(2, goTalkToTrailborn);

		ConditionalStep saveGuide = new ConditionalStep(this, mixIngredients);
		saveGuide.addStep(new Conditions(hasCake, inDiningRoom), useCakeOnLumbridgeGuide);
		saveGuide.addStep(hasCake, enterDiningRoomAgain);
		saveGuide.addStep(hasRawCake, cookCake);
		steps.put(3, saveGuide);
		steps.put(4, saveGuide);
		return steps;
	}

	public void setupRequirements()
	{
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		flour = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
		egg = new ItemRequirement("Egg", ItemID.EGG);
		tin = new ItemRequirement("Cake tin", ItemID.CAKE_TIN);
		tinHighlighted = new ItemRequirement("Cake tin", ItemID.CAKE_TIN);
		tinHighlighted.setHighlightInInventory(true);
		enchantedEgg = new ItemRequirement("Enchanted egg", ItemID.ENCHANTED_EGG);
		enchantedEgg.setTip("You can get another from Traiborn if you've lost it");

		enchantedFlour = new ItemRequirement("Enchanted flour", ItemID.ENCHANTED_FLOUR);
		enchantedFlour.setTip("You can get another from Traiborn if you've lost it");

		enchantedMilk = new ItemRequirement("Enchanted milk", ItemID.ENCHANTED_MILK);
		enchantedMilk.setTip("You can get another from Traiborn if you've lost it");
		enchantedMilk.setHighlightInInventory(true);

		rawGuidanceCake = new ItemRequirement("Raw guide cake", ItemID.RAW_GUIDE_CAKE);
		guidanceCake = new ItemRequirement("Guidance cake", ItemID.CAKE_OF_GUIDANCE);
		guidanceCakeHighlighted = new ItemRequirement("Guidance cake", ItemID.CAKE_OF_GUIDANCE);
		guidanceCakeHighlighted.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		upstairsTrailborn = new Zone(new WorldPoint(3100, 3152, 1), new WorldPoint(3117, 3168, 1));
		quizSpot = new Zone(new WorldPoint(2579, 4625, 0), new WorldPoint(2579, 4625, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneCondition(diningRoom);
		inUpstairsTrailborn = new ZoneCondition(upstairsTrailborn, quizSpot);

		hasCake = new ItemRequirementCondition(guidanceCake);
		hasRawCake = new ItemRequirementCondition(rawGuidanceCake);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0), "Go inspect the Lumbridge Guide in the Lumbridge Castle dining room.");
		inspectLumbridgeGuide = new ObjectStep(this, ObjectID.LUMBRIDGE_GUIDE_12339, new WorldPoint(1865, 5325, 0), "Inspect the Lumbridge Guide in the Lumbridge Castle dining room.");
		inspectLumbridgeGuide.addDialogStep("Yes, I'm sure I can make a cake.");
		inspectLumbridgeGuide.addSubSteps(enterDiningRoom);

		goUpToTraiborn = new ObjectStep(this, ObjectID.STAIRCASE_12536, new WorldPoint(3104, 3160, 0), "Go talk to Traiborn in the Wizards' Tower.", egg, flour, milk, tin);
		talkToTraiborn = new QuizSteps(this);

		cookCake = new DetailedQuestStep(this, "Cook the Guidance Cake.", rawGuidanceCake);
		enterDiningRoomAgain = new ObjectStep(this, ObjectID.DOOR_12348, new WorldPoint(3207, 3217, 0), "Use the Guidance Cake on the Lumbridge Guide to finish the quest.", guidanceCake);
		useCakeOnLumbridgeGuide = new ObjectStep(this, ObjectID.LUMBRIDGE_GUIDE_12339, new WorldPoint(1865, 5325, 0), "Use the Guidance Cake on the Lumbridge Guide to finish the quest.", guidanceCakeHighlighted);
		useCakeOnLumbridgeGuide.addIcon(ItemID.CAKE_OF_GUIDANCE);
		useCakeOnLumbridgeGuide.addSubSteps(enterDiningRoomAgain);

		mixIngredients = new DetailedQuestStep(this, "Talk to Traiborn for the enchanted ingredients, then mix them in a tin.", enchantedEgg, enchantedFlour, enchantedMilk, tin);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(milk, egg, flour, tin));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Saving the Guide", new ArrayList<>(Arrays.asList(inspectLumbridgeGuide, goUpToTraiborn, talkToTraiborn, mixIngredients, cookCake, useCakeOnLumbridgeGuide)), milk, egg, flour, tin));
		return allSteps;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(1896) >= 5 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
