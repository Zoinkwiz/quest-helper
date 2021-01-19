/*
 * Copyright (c) 2021, Zoinkwiz
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
import com.questhelper.requirements.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE
)
public class RFDGoblins extends BasicQuestHelper
{
	ItemRequirement bread, orange, knife, blueGreenPurpledye, spice, fishingBait, bucketOfWater,
		breadHighlighted, orangeHighlighted, knifeHighlighted, blueGreenPurpledyeHighlighted,
		spiceHighlighted, fishingBaitHighlighted, bucketOfWaterHighlighted, charcoal, dyedOrange,
		spicedBait, wetBread, teleportLumbridge, teleportFalador, slop, slopHighlighted,
		orangeSliceHighlighted;

	ConditionForStep inDiningRoom, inCookRoom, hasSlop, hasSpicedBait, hasWetBread, hasDyedOrange,
		hasOrangeSlices;

	QuestStep enterDiningRoom, inspectGoblin, goDownToKitchen, talkToCook, talkToCookAfterChar,
		sliceOrange, dyeOrange, spiceBait, useWaterOnBread, enterDiningRoomAgain,
		useSlopOnGoblin;

	ConditionalStep goTalkCook1, goTalkCook2, goTalkCook3, goTalkCook4;

	Zone diningRoom, cookRoom, cookRoomDestroyed;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectGoblin = new ConditionalStep(this, enterDiningRoom);
		goInspectGoblin.addStep(inDiningRoom, inspectGoblin);
		steps.put(0, goInspectGoblin);

		steps.put(5, goTalkCook1);
		steps.put(10, goTalkCook2);
		steps.put(15, goTalkCook3);

		ConditionalStep goGetIngredients = new ConditionalStep(this, useWaterOnBread);
		goGetIngredients.addStep(new Conditions(hasWetBread, hasDyedOrange, hasSpicedBait),
			goTalkCook4);
		goGetIngredients.addStep(new Conditions(hasWetBread, hasDyedOrange), spiceBait);
		goGetIngredients.addStep(new Conditions(hasWetBread, hasOrangeSlices), dyeOrange);
		goGetIngredients.addStep(hasWetBread, sliceOrange);
		steps.put(30, goGetIngredients);

		ConditionalStep goGiveSlop = new ConditionalStep(this, enterDiningRoomAgain);
		goGiveSlop.addStep(inDiningRoom, useSlopOnGoblin);
		steps.put(35, goGiveSlop);

		return steps;
	}

	public void setupRequirements()
	{
		bread = new ItemRequirement("Bread", ItemID.BREAD);
		orange = new ItemRequirement("Orange", ItemID.ORANGE);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		blueGreenPurpledye = new ItemRequirement("A blue, green, or purple dye", ItemID.BLUE_DYE);
		blueGreenPurpledye.addAlternates(ItemID.GREEN_DYE, ItemID.PURPLE_DYE);
		spice = new ItemRequirement("Spice or gnome spice", ItemID.SPICE);
		spice.setTip("You can get some from the Culinaromancer's chest, or a Gnome Spice from the Tree Gnome " +
			"Stronghold");
		spice.addAlternates(ItemID.GNOME_SPICE);
		fishingBait = new ItemRequirement("Fishing bait", ItemID.FISHING_BAIT);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);

		breadHighlighted = new ItemRequirement("Bread", ItemID.BREAD);
		breadHighlighted.setHighlightInInventory(true);
		orangeHighlighted = new ItemRequirement("Orange", ItemID.ORANGE);
		orangeHighlighted.setHighlightInInventory(true);
		knifeHighlighted = new ItemRequirement("Knife", ItemID.KNIFE);
		knifeHighlighted.setHighlightInInventory(true);
		blueGreenPurpledyeHighlighted = new ItemRequirement("A blue, green, or purple dye", ItemID.BLUE_DYE);
		blueGreenPurpledyeHighlighted.setHighlightInInventory(true);
		blueGreenPurpledyeHighlighted.addAlternates(ItemID.GREEN_DYE, ItemID.PURPLE_DYE);
		spiceHighlighted = new ItemRequirement("Spice or gnome spice", ItemID.SPICE);
		spiceHighlighted.setHighlightInInventory(true);
		spiceHighlighted.setTip("You can get some from the Culinaromancer's chest, or a Gnome Spice from the Tree Gnome " +
			"Stronghold");
		spiceHighlighted.addAlternates(ItemID.GNOME_SPICE);
		fishingBaitHighlighted = new ItemRequirement("Fishing bait", ItemID.FISHING_BAIT);
		fishingBaitHighlighted.setHighlightInInventory(true);
		bucketOfWaterHighlighted = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWaterHighlighted.setHighlightInInventory(true);

		orangeSliceHighlighted = new ItemRequirement("Orange slices", ItemID.ORANGE_SLICES);
		orangeSliceHighlighted.setHighlightInInventory(true);

		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		charcoal.setTip("You can buy one from the general store north of Shilo Village");
		charcoal.addAlternates(ItemID.GROUND_CHARCOAL);

		dyedOrange = new ItemRequirement("Dyed orange", ItemID.DYED_ORANGE);
		spicedBait = new ItemRequirement("Spicy maggots", ItemID.SPICY_MAGGOTS);
		wetBread = new ItemRequirement("Soggy bread", ItemID.SOGGY_BREAD);
		slop = new ItemRequirement("Slop of compromise", ItemID.SLOP_OF_COMPROMISE);
		slopHighlighted = new ItemRequirement("Slop of compromise", ItemID.SLOP_OF_COMPROMISE);
		slopHighlighted.setHighlightInInventory(true);

		teleportFalador = new ItemRequirement("Teleport to Falador", ItemID.FALADOR_TELEPORT);
		teleportLumbridge = new ItemRequirement("Teleport to Lumbridge", ItemID.LUMBRIDGE_TELEPORT);
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		cookRoom = new Zone(new WorldPoint(2974, 9906, 0), new WorldPoint(2990, 9917, 0));
		cookRoomDestroyed = new Zone(new WorldPoint(2974, 9866, 0), new WorldPoint(2990, 9877, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneCondition(diningRoom);
		inCookRoom = new ZoneCondition(cookRoom, cookRoomDestroyed);

		hasDyedOrange = new ItemRequirementCondition(dyedOrange);
		hasOrangeSlices = new ItemRequirementCondition(orangeSliceHighlighted);
		hasSpicedBait = new ItemRequirementCondition(spicedBait);
		hasWetBread = new ItemRequirementCondition(wetBread);
		hasSlop = new ItemRequirementCondition(slop);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0),
			"Go inspect Wartface or Bentnoze.");
		inspectGoblin = new ObjectStep(this, ObjectID.GENERAL_BENTNOZE_12332, new WorldPoint(1862, 5325, 0),
			"Inspect Wartface or Bentnoze.");
		inspectGoblin.addSubSteps(enterDiningRoom);

		goDownToKitchen = new ObjectStep(this, ObjectID.LADDER_12389, new WorldPoint(2960, 3507, 0),
			"");

		talkToCook = new NpcStep(this, NpcID.GOBLIN_COOK, new WorldPoint(2981, 9909, 0), "");
		((NpcStep)talkToCook).addAlternateNpcs(NpcID.GOBLIN_COOK_4852);
		talkToCookAfterChar = new NpcStep(this, NpcID.GOBLIN_COOK_4851, new WorldPoint(2981, 9909, 0),
			"");

		useWaterOnBread = new DetailedQuestStep(this, "Use a bucket of water on some bread.", bucketOfWaterHighlighted,
			breadHighlighted);

		sliceOrange = new DetailedQuestStep(this, "Use a knife on an orange and SLICE it.", knifeHighlighted,
			orangeHighlighted);
		dyeOrange = new DetailedQuestStep(this, "Use a dye on the sliced orange.", blueGreenPurpledyeHighlighted,
			orangeSliceHighlighted);
		spiceBait = new DetailedQuestStep(this, "Use spices on the fishing bait.", spiceHighlighted, fishingBaitHighlighted);

		enterDiningRoomAgain = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0),
			"Go use the slop on Wartface or Bentnoze in the Lumbridge Banquet room.", slop);
		useSlopOnGoblin = new ObjectStep(this, ObjectID.GENERAL_BENTNOZE_12332, new WorldPoint(1862, 5325, 0),
			"Use the slop on Wartface or Bentnoze.", slopHighlighted);
		useSlopOnGoblin.addSubSteps(enterDiningRoomAgain);
		useSlopOnGoblin.addIcon(ItemID.SLOP_OF_COMPROMISE);

		goTalkCook1 = new ConditionalStep(this, goDownToKitchen, "Go talk to the Goblin Cook down the ladder in the Goblin " +
			"Village.", bread, orange, knife, blueGreenPurpledye, spice, fishingBait,
			bucketOfWater, charcoal);
		goTalkCook1.addStep(inCookRoom, talkToCook);
		goTalkCook1.addDialogSteps("I need your help...", "What do you need? Maybe I can get it for you.");

		goTalkCook2 = new ConditionalStep(this, goDownToKitchen, "Give the Goblin Cook some charcoal.", charcoal);
		goTalkCook2.addStep(inCookRoom, talkToCook);
		goTalkCook2.addDialogSteps("I've got the charcoal you were after.");

		goTalkCook3 = new ConditionalStep(this, goDownToKitchen, "Talk to the Goblin Cook again.",
			bread, orange, knife, blueGreenPurpledye, spice, fishingBait, bucketOfWater);
		goTalkCook3.addStep(inCookRoom, talkToCookAfterChar);
		goTalkCook3.addDialogSteps("I need your help...");

		goTalkCook4 = new ConditionalStep(this, goDownToKitchen, "Talk to the Goblin Cook again with the ingredients.",
			wetBread, dyedOrange, spicedBait);
		goTalkCook4.addStep(inCookRoom, talkToCookAfterChar);
		goTalkCook4.addDialogStep("I've got the ingredients we need...");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(bread, orange, knife, blueGreenPurpledye, spice, fishingBait, bucketOfWater, charcoal));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(teleportFalador, teleportLumbridge));
	}

	@Override
	public ArrayList<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new VarbitRequirement(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId(), Operation.GREATER_EQUAL, 3,
			"Started Recipe for Disaster"));
		reqs.add(new QuestRequirement(Quest.GOBLIN_DIPLOMACY, QuestState.FINISHED));

		return reqs;

	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Saving the Generals",
			new ArrayList<>(Arrays.asList(inspectGoblin, goTalkCook1, goTalkCook2, goTalkCook3,
				useWaterOnBread, sliceOrange, dyeOrange, spiceBait, goTalkCook4,
				useSlopOnGoblin)),
			bread, orange, knife, blueGreenPurpledye, spice, fishingBait, bucketOfWater, charcoal));
		return allSteps;
	}

	@Override
	public QuestState getState(Client client)
	{
		int questState = client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_WARTFACE_AND_BENTNOZE.getId());
		if (questState == 0)
		{
			return QuestState.NOT_STARTED;
		}

		if (questState < 40)
		{
			return QuestState.IN_PROGRESS;
		}

		return QuestState.FINISHED;
	}

	@Override
	public boolean isCompleted()
	{
		return (getQuest().getVar(client) >= 40 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
