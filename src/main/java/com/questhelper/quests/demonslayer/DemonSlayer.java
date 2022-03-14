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
package com.questhelper.quests.demonslayer;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.conditional.NpcCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.DEMON_SLAYER
)
public class DemonSlayer extends BasicQuestHelper
{
	//Items Required
	ItemRequirement bucket, bucketOfWater, key1, key2, key3, bones, silverlight, combatGear, silverlightEquipped, coin, food, bucketOfWaterOptional;

	//Items Recommended
	ItemRequirement varrockTeleport, wizardsTowerTeleport;

	Requirement inVarrockSewer, inCastleNWFloor1, inCastleNWFloor2, inCastleNEFloor1,
		hasPouredWaterIntoDrain, inTowerFloor1, obtainedSilverlight, delrithNearby, delrithWeakenedNearby, inInstance;

	QuestStep talkToAris, talkToPrysin, goUpToRovin, goUpToRovin2, talkToRovin, goDownstairsFromRovin, goDownstairsFromRovin2, goUpToBucket, pickupBucket,
		goDownFromBucket, fillBucket, useFilledBucketOnDrain, goDownManhole, pickupSecondKey, goUpManhole, goUpstairsWizard, talkToTraiborn, returnToPrysin,
		getSilverlightBack, killDelrith, killDelrithStep;

	ConditionalStep getFirstKey, getSecondKey, getThirdKey, goAndKillDelrith;

	//Zones
	Zone varrockSewer, castleNWFloor1, castleNWFloor2, castleNEFloor1, towerFloor1;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToAris);
		steps.put(1, talkToPrysin);

		getFirstKey = new ConditionalStep(this, goUpToRovin);
		getFirstKey.addStep(inCastleNWFloor2, talkToRovin);
		getFirstKey.addStep(inCastleNWFloor1, goUpToRovin2);
		getFirstKey.setLockingCondition(new Conditions(LogicType.OR, obtainedSilverlight, key1.alsoCheckBank(questBank)));

		getSecondKey = new ConditionalStep(this, goUpToBucket);
		getSecondKey.addStep(inVarrockSewer, pickupSecondKey);
		getSecondKey.addStep(hasPouredWaterIntoDrain, goDownManhole);
		getSecondKey.addStep(inCastleNWFloor1, goDownstairsFromRovin2);
		getSecondKey.addStep(inCastleNWFloor2, goDownstairsFromRovin);
		getSecondKey.addStep(bucketOfWater, useFilledBucketOnDrain);
		getSecondKey.addStep(new Conditions(inCastleNEFloor1, bucketOfWater), goDownFromBucket);
		getSecondKey.addStep(bucket, fillBucket);
		getSecondKey.addStep(inCastleNEFloor1, pickupBucket);
		getSecondKey.setLockingCondition(new Conditions(LogicType.OR, obtainedSilverlight, key2.alsoCheckBank(questBank)));

		getThirdKey = new ConditionalStep(this, goUpstairsWizard);
		getThirdKey.addStep(inTowerFloor1, talkToTraiborn);
		getThirdKey.addStep(inVarrockSewer, goUpManhole);
		getThirdKey.setLockingCondition(new Conditions(LogicType.OR, obtainedSilverlight, key3.alsoCheckBank(questBank)));

		goAndKillDelrith = new ConditionalStep(this, getSilverlightBack);
		goAndKillDelrith.addStep(silverlight.alsoCheckBank(questBank), killDelrith);

		ConditionalStep getKeys = new ConditionalStep(this, getFirstKey);
		getKeys.addStep(obtainedSilverlight, goAndKillDelrith);
		getKeys.addStep(new Conditions(key1, key2, key3), returnToPrysin);
		getKeys.addStep(new Conditions(key1, key2), getThirdKey);
		getKeys.addStep(key1, getSecondKey);

		steps.put(2, getKeys);

		return steps;
	}

	public void setupItemRequirements()
	{
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET);
		bucket.setHighlightInInventory(true);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER);
		bucketOfWater.setHighlightInInventory(true);
		key1 = new ItemRequirement("Silverlight key", ItemID.SILVERLIGHT_KEY_2400);
		key2 = new ItemRequirement("Silverlight key", ItemID.SILVERLIGHT_KEY_2401);
		key3 = new ItemRequirement("Silverlight key", ItemID.SILVERLIGHT_KEY);
		bones = new ItemRequirement("Bones (UNNOTED)", ItemID.BONES, 25);
		silverlight = new ItemRequirement("Silverlight", ItemID.SILVERLIGHT);
		silverlightEquipped = new ItemRequirement("Silverlight", ItemID.SILVERLIGHT, 1, true);
		combatGear = new ItemRequirement("Armour", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getArmour());

		bucketOfWaterOptional = new ItemRequirement("Bucket of water (obtainable during quest)", ItemID.BUCKET_OF_WATER);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		wizardsTowerTeleport = new ItemRequirement("Teleport to the Wizards' Tower", ItemID.NECKLACE_OF_PASSAGE5);
		coin = new ItemRequirement("Coin", ItemCollections.getCoins());
		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);
	}

	public void setupConditions()
	{
		inCastleNEFloor1 = new ZoneRequirement(castleNEFloor1);
		inCastleNWFloor1 = new ZoneRequirement(castleNWFloor1);
		inCastleNWFloor2 = new ZoneRequirement(castleNWFloor2);
		inVarrockSewer = new ZoneRequirement(varrockSewer);
		inTowerFloor1 = new ZoneRequirement(towerFloor1);
		hasPouredWaterIntoDrain = new VarbitRequirement(2568, 1);
		obtainedSilverlight = new VarbitRequirement(2567, 1);
		delrithNearby = new NpcCondition(NpcID.DELRITH);
		delrithWeakenedNearby = new NpcCondition(NpcID.WEAKENED_DELRITH);
		inInstance = new VarbitRequirement(2569, 1);
	}

	public void setupZones()
	{
		varrockSewer = new Zone(new WorldPoint(3151, 9855, 0), new WorldPoint(3290, 9919, 0));
		castleNWFloor1 = new Zone(new WorldPoint(3200, 3490, 1), new WorldPoint(3206, 3500, 1));
		castleNWFloor2 = new Zone(new WorldPoint(3200, 3494, 2), new WorldPoint(3206, 3500, 2));
		castleNEFloor1 = new Zone(new WorldPoint(3207, 3487, 1), new WorldPoint(3225, 3497, 1));
		towerFloor1 = new Zone(new WorldPoint(3102, 3154, 1), new WorldPoint(3114, 3165, 1));
	}

	public void setupSteps()
	{
		talkToAris = new NpcStep(this, NpcID.GYPSY_ARIS, new WorldPoint(3204, 3424, 0), "Talk to Gypsy Aris in her tent in Varrock Square.", coin);
		talkToAris.addDialogStep("Ok, here you go.");
		talkToAris.addDialogStep("Okay, where is he? I'll kill him for you!");
		talkToAris.addDialogStep("So how did Wally kill Delrith?");
		talkToPrysin = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Talk to Sir Prysin in the south west corner of Varrock Castle.");
		talkToPrysin.addDialogStep("Gypsy Aris said I should come and talk to you.");
		talkToPrysin.addDialogStep("I need to find Silverlight.");
		talkToPrysin.addDialogStep("He's back and unfortunately I've got to deal with him.");
		talkToPrysin.addDialogStep("So give me the keys!");
		talkToPrysin.addDialogStep("Can you give me your key?");
		goUpToRovin = new ObjectStep(this, ObjectID.STAIRCASE_11790, new WorldPoint(3203, 3498, 0), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goUpToRovin2 = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3203, 3498, 1), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		talkToRovin = new NpcStep(this, NpcID.CAPTAIN_ROVIN, new WorldPoint(3205, 3498, 2), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		talkToRovin.addDialogStep("Yes I know, but this is important.");
		talkToRovin.addDialogStep("There's a demon who wants to invade this city.");
		talkToRovin.addDialogStep("Yes, very.");
		talkToRovin.addDialogStep("It's not them who are going to fight the demon, it's me.");
		talkToRovin.addDialogStep("Sir Prysin said you would give me the key.");
		talkToRovin.addDialogStep("Why did he give you one of the keys then?");
		talkToRovin.addSubSteps(goUpToRovin, goUpToRovin2);

		goDownstairsFromRovin = new ObjectStep(this, ObjectID.STAIRCASE_11793, new WorldPoint(3203, 3498, 2), "Go to the Varrock Castle kitchen.");
		goDownstairsFromRovin2 = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3203, 3498, 1), "Go to the Varrock Castle kitchen.");
		goUpToBucket = new ObjectStep(this, ObjectID.STAIRCASE_11789, new WorldPoint(3219, 3497, 0), "Get a bucket from above the Varrock Castle kitchen.");
		pickupBucket = new DetailedQuestStep(this, new WorldPoint(3221, 3497, 1), "Pick up the bucket nearby.", bucket);
		goDownFromBucket = new ObjectStep(this, ObjectID.STAIRCASE_11793, new WorldPoint(3219, 3497, 1), "Go back down to the kitchen.");
		fillBucket = new ObjectStep(this, ObjectID.SINK_7422, new WorldPoint(3224, 3495, 0), "Use the bucket on the sink.", bucket);
		fillBucket.addIcon(ItemID.BUCKET);
		useFilledBucketOnDrain = new ObjectStep(this, ObjectID.DRAIN_17424, new WorldPoint(3225, 3496, 0), "Use the bucket of water on the drain outside the kitchen.", bucketOfWater);
		useFilledBucketOnDrain.addIcon(ItemID.BUCKET_OF_WATER);
		useFilledBucketOnDrain.addSubSteps(goDownstairsFromRovin, goDownstairsFromRovin2, goUpToBucket, pickupBucket, goDownFromBucket, fillBucket);
		goDownManhole = new ObjectStep(this, ObjectID.MANHOLE_882, new WorldPoint(3237, 3458, 0), "Go down into Varrock Sewer via the Manhole south east of Varrock Castle.");
		((ObjectStep) goDownManhole).addAlternateObjects(ObjectID.MANHOLE);
		pickupSecondKey = new ObjectStep(this, NullObjectID.NULL_17431, new WorldPoint(3225, 9897, 0), "Pick up the Rusty Key north of the Sewer entrance.");

		goUpManhole = new ObjectStep(this, ObjectID.LADDER_11806, new WorldPoint(3237, 9858, 0), "Bring Wizard Traiborn 25 bones in the Wizards' Tower.", bones);
		goUpstairsWizard = new ObjectStep(this, ObjectID.STAIRCASE_12536, new WorldPoint(3104, 3160, 0), "Bring Wizard Traiborn 25 bones in the Wizards' Tower.", bones);
		talkToTraiborn = new NpcStep(this, NpcID.TRAIBORN, new WorldPoint(3114, 3163, 1), "Bring Wizard Traiborn 25 bones in the Wizards' Tower. You don't need to bring them all at once.", bones);
		talkToTraiborn.addDialogStep("I need to get a key given to you by Sir Prysin.");
		talkToTraiborn.addDialogStep("Well, have you got any keys knocking around?");
		talkToTraiborn.addDialogStep("I'll get the bones for you.");
		talkToTraiborn.addSubSteps(goUpManhole, goUpstairsWizard);

		returnToPrysin = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Return to Sir Prysin in the south west of Varrock Castle.", key1, key2, key3);
		getSilverlightBack = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Get Silverlight back from Sir Prysin in the south west of Varrock Castle.");
		returnToPrysin.addSubSteps(getSilverlightBack);

		killDelrithStep = new NpcStep(this, NpcID.DELRITH, new WorldPoint(3227, 3370, 0), "Kill Delrith (level 27) using Silverlight at the dark wizards south of Varrock. Once defeated, you'll need to say the magic words to banish him.", silverlightEquipped, combatGear);

		killDelrith = new IncantationStep(this, killDelrithStep);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coin, bones, bucketOfWaterOptional, combatGear, food);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(varrockTeleport, wizardsTowerTeleport);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Delirth (level 27)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Silverlight", ItemID.SILVERLIGHT, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToAris, talkToPrysin), coin, bucketOfWaterOptional));
		PanelDetails rovinPanel = new PanelDetails("Get Rovin's key", Collections.singletonList(talkToRovin), bucketOfWaterOptional);
		rovinPanel.setLockingStep(getFirstKey);
		allSteps.add(rovinPanel);

		PanelDetails prysinPanel = new PanelDetails("Get Prysin's key", Arrays.asList(useFilledBucketOnDrain, goDownManhole, pickupSecondKey));
		prysinPanel.setLockingStep(getSecondKey);
		allSteps.add(prysinPanel);

		PanelDetails traibornPanel = new PanelDetails("Get Traiborn's key", Collections.singletonList(talkToTraiborn), bones);
		traibornPanel.setLockingStep(getThirdKey);
		allSteps.add(traibornPanel);

		PanelDetails killDelrithPanel = new PanelDetails("Kill Delrith", Arrays.asList(returnToPrysin, killDelrithStep), silverlight, combatGear, food);
		allSteps.add(killDelrithPanel);
		return allSteps;
	}
}
