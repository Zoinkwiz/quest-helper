/*
 * Copyright (c) 2022, claudiodekker <https://github.com/claudiodekker>
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.sleepinggiants;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetSpriteRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import com.questhelper.steps.WidgetDetails;
import com.questhelper.steps.WidgetStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(quest = QuestHelperQuest.SLEEPING_GIANTS)
@Slf4j
public class SleepingGiants extends BasicQuestHelper
{
	// Items Required
	ItemRequirement oakLogs, wool, nails, hammer, chisel, iceGloves;

	// Optional Items
	ItemRequirement alKharidTeleport, bucket, bucketOfWater;
	FreeInventorySlotRequirement freeInventorySpace;

	// Quest (Step-related) Items
	ItemRequirement oneOakLog, twoOakLogs, fiveNails, preform;

	// Primary Quest Line Steps
	QuestStep strikeHillGiant, speakToKovac, fixPolishingStone, fixGrindstone, fixHammer, speakToKovacAfterRepairs, searchCrate, fillCrucible,
		speakToKovacAboutMould, pourMetal, coolDownSword, speakToKovacContinue, interactWithMould, selectForteTab, selectForte, selectBladesTab,
		selectBlade, selectTipsTab, selectTip, setMould, talkToKovakAfterMould;

	QuestStep getPreform, dunkPreform, hitPreformWhileRed, coolPreformToGrindstone, dunkPreformToGrindstone, grindstonePreform, coolPreformToPolish,
		heatPreformToPolish, polishPreform, handInPreform;

	// Optional & Supportive Quest Steps
	QuestStep goToDesertPlateau, takeHammer, enterFoundry, enterFoundryToMakeWeapon, takeBucket, fillBucketWaterfall;

	ZoneRequirement onDesertPlateau, inGiantsFoundry;

	VarbitRequirement grindstoneFixed, polishingStoneFixed, hammerFixed, commissionReceived, crateSearched, crucibleFilled, talkedToKovacAboutMould,
		shouldSetMould, mouldSet, talkedToKovacAboutPouringMetal, metalPoured, preformObtained, swordMade, preformHandedIn;

	VarbitRequirement storedPreform, metalHeated, metalHammered, metalTooCoolToGrindstone, metalTooHotToGrindstone, metalGrinded, metalTooHotForPolishing,
		metalTooCoolForPolishing;

	Requirement selectingMould, noForteSelected, noBladeSelected, noTipSelected, forteTabOpen, bladeTabOpen, tipTabOpen;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, strikeHillGiant);
		startQuest.addStep(new Conditions(LogicType.NOR, onDesertPlateau), goToDesertPlateau);
		startQuest.addStep(new Conditions(LogicType.NOR, hammer), takeHammer);
		steps.put(0, startQuest);

		ConditionalStep speakToKovacOnDesertPlateau = new ConditionalStep(this, speakToKovac);
		speakToKovacOnDesertPlateau.addStep(new Conditions(LogicType.NOR, onDesertPlateau), goToDesertPlateau);
		startQuest.addStep(new Conditions(LogicType.NOR, hammer), takeHammer);
		steps.put(5, speakToKovacOnDesertPlateau);

		ConditionalStep fixMachinery = new ConditionalStep(this, speakToKovacAfterRepairs);
		fixMachinery.addStep(new Conditions(LogicType.NOR, inGiantsFoundry), enterFoundry);
		fixMachinery.addStep(new Conditions(LogicType.NOR, polishingStoneFixed), fixPolishingStone);
		fixMachinery.addStep(new Conditions(LogicType.NOR, grindstoneFixed), fixGrindstone);
		fixMachinery.addStep(new Conditions(LogicType.NOR, hammerFixed), fixHammer);
		steps.put(10, fixMachinery);
		steps.put(15, fixMachinery);

		ConditionalStep speakToKovacContinueAfterRepairs = new ConditionalStep(this, speakToKovacAfterRepairs);
		speakToKovacContinueAfterRepairs.addStep(new Conditions(LogicType.NOR, inGiantsFoundry), enterFoundry);
		steps.put(20, speakToKovacContinueAfterRepairs);

		// TODO: This needs validation, as it's a very complex varbit-base step. It is very possible that the varbits are not checked properly.
		ConditionalStep speakToKovacForCommission = new ConditionalStep(this, handInPreform);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, inGiantsFoundry), enterFoundryToMakeWeapon);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, commissionReceived), speakToKovacContinue);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, crateSearched), searchCrate);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, crucibleFilled), fillCrucible);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, talkedToKovacAboutMould), speakToKovacAboutMould);

		// Mould setup
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noForteSelected, forteTabOpen), selectForte);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noForteSelected), selectForteTab);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noBladeSelected, bladeTabOpen), selectBlade);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noBladeSelected), selectBladesTab);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noTipSelected, tipTabOpen), selectTip);
		speakToKovacForCommission.addStep(new Conditions(selectingMould, noTipSelected), selectTipsTab);
		speakToKovacForCommission.addStep(new Conditions(selectingMould), setMould);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, mouldSet), interactWithMould);

		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, talkedToKovacAboutPouringMetal), talkToKovakAfterMould);
		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, metalPoured), pourMetal);

		speakToKovacForCommission.addStep(new Conditions(new Conditions(LogicType.NOR, preformObtained),
			new Conditions(LogicType.OR, iceGloves, bucketOfWater)), coolDownSword);
		speakToKovacForCommission.addStep(new Conditions(new Conditions(LogicType.NOR, preformObtained), bucket), fillBucketWaterfall);
		speakToKovacForCommission.addStep(new Conditions(new Conditions(LogicType.NOR, preformObtained), new Conditions(LogicType.NOR, bucket)), takeBucket);


		ConditionalStep swordCreation = new ConditionalStep(this, dunkPreform);
		swordCreation.addStep(storedPreform, getPreform);
		swordCreation.addStep(swordMade, handInPreform);
		swordCreation.addStep(new Conditions(metalGrinded, metalTooCoolForPolishing), heatPreformToPolish);
		swordCreation.addStep(new Conditions(metalGrinded, metalTooHotForPolishing), coolPreformToPolish);
		swordCreation.addStep(metalGrinded, polishPreform);

		swordCreation.addStep(new Conditions(metalHammered, metalTooCoolToGrindstone), dunkPreformToGrindstone);
		swordCreation.addStep(new Conditions(metalHammered, metalTooHotToGrindstone), coolPreformToGrindstone);
		swordCreation.addStep(metalHammered, grindstonePreform);

		swordCreation.addStep(metalHeated, hitPreformWhileRed);

		speakToKovacForCommission.addStep(new Conditions(LogicType.NOR, preformHandedIn), swordCreation);

		steps.put(25, speakToKovacForCommission);

		return steps;
	}

	public void setupItemRequirements()
	{
		oakLogs = new ItemRequirement("Oak Logs", ItemID.OAK_LOGS);
		oakLogs.setQuantity(3);

		wool = new ItemRequirement("Wool", ItemID.WOOL);

		nails = new ItemRequirement("Nails", ItemID.BRONZE_NAILS);
		nails.addAlternates(ItemID.IRON_NAILS, ItemID.STEEL_NAILS, ItemID.BLACK_NAILS, ItemID.MITHRIL_NAILS, ItemID.ADAMANTITE_NAILS, ItemID.RUNE_NAILS);
		nails.setQuantity(10);

		hammer = new ItemRequirement("Hammer", ItemID.HAMMER);
		hammer.canBeObtainedDuringQuest();

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);

		iceGloves = new ItemRequirement("Ice Gloves", ItemID.ICE_GLOVES);
		iceGloves.setTooltip("Allows you to skip filling and using a bucket of water.");

		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucket.canBeObtainedDuringQuest();
		bucket.setHighlightInInventory(true);

		bucketOfWater = new ItemRequirement("Bucket of Water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.canBeObtainedDuringQuest();

		freeInventorySpace = new FreeInventorySlotRequirement(InventoryID.INVENTORY, 20);

		alKharidTeleport = new ItemRequirement("Al Kharid Teleport", ItemCollections.RING_OF_DUELINGS);
		alKharidTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		alKharidTeleport.setTooltip("Ring of Dueling or Amulet of Glory; Can be used to quickly reach the Giants' Foundry.");

		twoOakLogs = new ItemRequirement("Oak Logs", ItemID.OAK_LOGS);
		twoOakLogs.setQuantity(2);

		fiveNails = new ItemRequirement("Nails", ItemID.BRONZE_NAILS);
		fiveNails.addAlternates(ItemID.IRON_NAILS, ItemID.STEEL_NAILS, ItemID.BLACK_NAILS, ItemID.MITHRIL_NAILS, ItemID.ADAMANTITE_NAILS, ItemID.RUNE_NAILS);
		fiveNails.setQuantity(5);

		oneOakLog = new ItemRequirement("Oak Logs", ItemID.OAK_LOGS);

		// TODO: This might not be the right Item ID.
		preform = new ItemRequirement("Preform", ItemID.PREFORM).equipped();
	}

	public void setupZones()
	{
		Zone desertPlateau = new Zone(new WorldPoint(3375, 3169, 0), new WorldPoint(3349, 3143, 0));
		onDesertPlateau = new ZoneRequirement(desertPlateau);

		Zone giantsFoundry = new Zone(new WorldPoint(3357, 11480, 0), new WorldPoint(3380, 11510, 0));
		inGiantsFoundry = new ZoneRequirement(giantsFoundry);
	}

	public void setupConditions()
	{
		grindstoneFixed = new VarbitRequirement(13905, 2, Operation.EQUAL);
		polishingStoneFixed = new VarbitRequirement(13906, 2, Operation.EQUAL);
		hammerFixed = new VarbitRequirement(13904, 2, Operation.EQUAL);

		commissionReceived = new VarbitRequirement(13903, 10, Operation.GREATER_EQUAL);
		crateSearched = new VarbitRequirement(13903, 15, Operation.GREATER_EQUAL);
		crucibleFilled = new VarbitRequirement(13903, 25, Operation.GREATER_EQUAL);
		talkedToKovacAboutMould = new VarbitRequirement(13903, 30, Operation.GREATER_EQUAL);
		// REMOVE
		shouldSetMould = new VarbitRequirement(13903, 30, Operation.GREATER_EQUAL);
		//REMOVE?
		mouldSet = new VarbitRequirement(13903, 35, Operation.GREATER_EQUAL);
		talkedToKovacAboutPouringMetal = new VarbitRequirement(13903, 40, Operation.GREATER_EQUAL);
		metalPoured = new VarbitRequirement(13903, 45, Operation.GREATER_EQUAL);
		preformObtained = new VarbitRequirement(13903, 50, Operation.GREATER_EQUAL);

		selectingMould = new WidgetPresenceRequirement(718, 2);
		noForteSelected = new VarbitRequirement(13910, 0);
		noBladeSelected = new VarbitRequirement(13911, 0);
		noTipSelected = new VarbitRequirement(13912, 0);
		forteTabOpen = new WidgetSpriteRequirement(718, 12, 1, 297);
		bladeTabOpen = new WidgetSpriteRequirement(718, 12, 10, 297);
		tipTabOpen = new WidgetSpriteRequirement(718, 12, 19, 297);

		storedPreform = new VarbitRequirement(13947, 1, Operation.GREATER_EQUAL);
		metalHeated = new VarbitRequirement(13948, 720, Operation.GREATER_EQUAL);

		metalHammered = new VarbitRequirement(13949, 333, Operation.GREATER_EQUAL);

		metalTooCoolToGrindstone = new VarbitRequirement(13948, 340, Operation.LESS_EQUAL);
		metalTooHotToGrindstone = new VarbitRequirement(13948, 620, Operation.GREATER_EQUAL);

		metalGrinded = new VarbitRequirement(13949, 666, Operation.GREATER_EQUAL);
		metalTooHotForPolishing = new VarbitRequirement(13948, 333, Operation.GREATER_EQUAL);
		metalTooCoolForPolishing = new VarbitRequirement(13948, 30, Operation.LESS_EQUAL);

		swordMade = new VarbitRequirement(13949, 1000);
		preformHandedIn = new VarbitRequirement(13903, 55, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		goToDesertPlateau = new DetailedQuestStep(this, new WorldPoint(3362, 3148, 0),
			"Go to the Desert Plateau east of Al Kharid to start the quest.", oakLogs, wool, nails, chisel, iceGloves);
		((DetailedQuestStep) goToDesertPlateau).setLinePoints(Arrays.asList(new WorldPoint(3293, 3180, 0),
			new WorldPoint(3306, 3180, 0),
			new WorldPoint(3313, 3171, 0),
			new WorldPoint(3320, 3171, 0),
			new WorldPoint(3333, 3175, 0),
			new WorldPoint(3339, 3175, 0),
			new WorldPoint(3343, 3173, 0),
			new WorldPoint(3353, 3173, 0),
			new WorldPoint(3364, 3184, 0),
			new WorldPoint(3376, 3184, 0),
			new WorldPoint(3381, 3178, 0),
			new WorldPoint(3381, 3175, 0),
			new WorldPoint(3375, 3170, 0))
		);

		takeHammer = new DetailedQuestStep(this, new WorldPoint(3350, 3162, 0),
			"Take a hammer from the crate north of the Giants' Foundry.");
		takeHammer.addIcon(ItemID.HAMMER);

		strikeHillGiant = new NpcStep(this, NpcID.HILL_GIANT_11467, new WorldPoint(3361, 3147, 0),
			"Attempt to Strike the Hill Giant at the entrance of the cave to start the quest.");
		strikeHillGiant.addDialogStep("Yes.");
		speakToKovac = new NpcStep(this, NpcID.KOVAC, new WorldPoint(3361, 3147, 0),
			"Speak to Kovac.");

		enterFoundry = new ObjectStep(this, ObjectID.CAVE_44635 /* Cave */, new WorldPoint(3361, 3150, 0),
			"Enter the Giants' Foundry.");

		enterFoundryToMakeWeapon = new ObjectStep(this, ObjectID.CAVE_44635 /* Cave */, new WorldPoint(3361, 3150, 0),
			"Enter the Giants' Foundry again.");

		fixPolishingStone = new ObjectStep(this, NullObjectID.NULL_44775 /* Broken polishing wheel */, new WorldPoint(3363, 11485, 0),
			"Fix the Broken polishing wheel.", twoOakLogs, fiveNails, hammer);
		fixPolishingStone.addDialogStep("Yes.");

		fixGrindstone = new ObjectStep(this, NullObjectID.NULL_44774 /* Broken grindstone */, new WorldPoint(3362, 11492, 0),
			"Fix the Broken grindstone.", chisel);
		fixGrindstone.addDialogStep("Yes.");
		fixHammer = new ObjectStep(this, NullObjectID.NULL_44773 /* Broken trip hammer */, new WorldPoint(3364, 11497, 0),
			"Fix the Broken trip hammer.", oneOakLog, fiveNails, hammer);
		fixHammer.addDialogStep("Yes.");

		speakToKovacAfterRepairs = new NpcStep(this, NpcID.KOVAC, "Speak to Kovac to continue.");
		speakToKovacContinue = new NpcStep(this, NpcID.KOVAC_11470, "Speak to Kovac for a commission.");

		searchCrate = new ObjectStep(this, NullObjectID.NULL_44779 /* Crate */, new WorldPoint(3370, 11483, 0),
			"Take some bars & weapons from the Crate.", freeInventorySpace);
		searchCrate.addDialogStep("Yes.");

		fillCrucible = new ObjectStep(this, NullObjectID.NULL_44776 /* Crucible (empty */,
			"Fill the Crucible with items & bars until it is full.");
		fillCrucible.addSubSteps(enterFoundryToMakeWeapon);

		speakToKovacAboutMould = new NpcStep(this, NpcID.KOVAC_11470, "Speak to Kovac about the mould.");

		interactWithMould = new ObjectStep(this, NullObjectID.NULL_44777 /* Mould jig (Empty) */,
			"Select the mould for the forte, blade and tip.");

		selectForteTab = new WidgetStep(this, "Select the Forte tab.", new WidgetDetails(718, 12, 1));
		selectForte = new WidgetStep(this, "Select the Forte that matches your commission best.", 718, 9);
		selectBladesTab = new WidgetStep(this, "Select the Blades tab.", new WidgetDetails(718, 12, 9));
		selectBlade = new WidgetStep(this, "Select the Blade that matches your commission best.", 718, 9);
		selectTipsTab = new WidgetStep(this, "Select the Tips tab.", new WidgetDetails(718, 12, 18));
		selectTip = new WidgetStep(this, "Select the Tip that matches your commission best.", 718, 9);
		setMould = new WidgetStep(this, "Set the Mould.", 718, 6);
		interactWithMould.addSubSteps(selectForte, selectForteTab, selectBladesTab, selectBlade, selectTipsTab, selectTip);

		talkToKovakAfterMould = new NpcStep(this, NpcID.KOVAC_11470, "Speak to Kovac about pouring the metal.");

		pourMetal = new ObjectStep(this, NullObjectID.NULL_44776 /* Crucible (empty */, "Pour the full Crucible into the mould.");

		coolDownSword = new ObjectStep(this, NullObjectID.NULL_44777, "Pick up the sword from the mould with a bucket of water or ice gloves equipped.",
			iceGloves.equipped().showConditioned(iceGloves), bucketOfWater.showConditioned(bucketOfWater));
		fillBucketWaterfall = new ObjectStep(this, ObjectID.WATERFALL_44632,
			"Fill the bucket with water by using it on the Waterfall.", bucketOfWater);

		takeBucket = new ObjectStep(this, ObjectID.PILE_OF_BUCKETS /* Pile of Buckets */, "Take a Bucket from the Pile of Buckets");

		// Trip hammer
		dunkPreform = new ObjectStep(this, ObjectID.LAVA_POOL_44631, new WorldPoint(3372, 11497, 0), "Dunk the preform in the lava pool until it's at max heat.");
		hitPreformWhileRed = new ObjectStep(this, ObjectID.TRIP_HAMMER /*  trip hammer */, new WorldPoint(3364, 11497, 0),
			"Use the trip hammer while the heat bar is in the red, and re-heat it in the lava whenever it's close to yellow.");
		hitPreformWhileRed.addSubSteps(dunkPreform);

		// Grindstone
		coolPreformToGrindstone = new ObjectStep(this, ObjectID.WATERFALL_44632,
			"Cool the preform to the lower range of the yellow heat section on the waterfall.");
		dunkPreformToGrindstone = new ObjectStep(this, ObjectID.LAVA_POOL_44631, new WorldPoint(3372, 11497, 0),
			"Dunk the preform in the lava pool until it's at the bottom of the yellow heat section.");
		grindstonePreform = new ObjectStep(this, ObjectID.GRINDSTONE, new WorldPoint(3362, 11492, 0),
			"Grindstone the preform whilst in the yellow heat section.");
		grindstonePreform.addSubSteps(coolPreformToGrindstone, dunkPreformToGrindstone);
		// Polishing
		coolPreformToPolish = new ObjectStep(this, ObjectID.WATERFALL_44632,
			"Cool the preform to the top of the range of the green heat section on the waterfall.");
		heatPreformToPolish = new ObjectStep(this, ObjectID.LAVA_POOL_44631, new WorldPoint(3372, 11497, 0),
			"Dunk the preform in the lava pool until it's at the top of the green heat section.");
		polishPreform = new ObjectStep(this, ObjectID.POLISHING_WHEEL /* Broken polishing wheel */, new WorldPoint(3363, 11485, 0),
			"Use the polishing wheel whilst in the green heat range, heating it in the lava when needed.");
		polishPreform.addSubSteps(coolPreformToPolish, heatPreformToPolish);

		getPreform = new ObjectStep(this, NullObjectID.NULL_44778, new WorldPoint(3369, 11501, 0),
			"Get your preform from the preform storage in the north of the room.");

		handInPreform = new NpcStep(this, NpcID.KOVAC_11470, "Give the finished sword to Kovac.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(oakLogs, wool, nails, hammer, chisel);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(iceGloves, alKharidTeleport);
	}

	public List<Requirement> getGeneralRecommended()
	{
		return Arrays.asList(freeInventorySpace);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.SMITHING, 6000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the Giants' Foundry"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", Arrays.asList(goToDesertPlateau, strikeHillGiant, speakToKovac, enterFoundry,
			fixPolishingStone, fixGrindstone, fixHammer, speakToKovacAfterRepairs),
			Arrays.asList(oakLogs, wool, nails, hammer, chisel), Collections.singletonList(freeInventorySpace)));
		allSteps.add(new PanelDetails("Creating the sword", Arrays.asList(speakToKovacContinue, searchCrate, fillCrucible,
			speakToKovacAboutMould, setMould, talkToKovakAfterMould, pourMetal, coolDownSword, hitPreformWhileRed, grindstonePreform, polishPreform,
			handInPreform), Arrays.asList(iceGloves, bucket)));

		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.SMITHING, 15));
	}
}
