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
package com.questhelper.helpers.quests.demonslayer;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class DemonSlayer extends BasicQuestHelper
{
	// Required items
	ItemRequirement bucketOfWaterOptional;
	ItemRequirement bones;
	ItemRequirement coin;
	ItemRequirement combatGear;
	ItemRequirement food;

	// Recommended items
	ItemRequirement varrockTeleport;
	ItemRequirement wizardsTowerTeleport;

	// Mid-quest item requirements
	ItemRequirement bucketOfWater;
	ItemRequirement bucket;
	ItemRequirement key1;
	ItemRequirement key2;
	ItemRequirement key3;
	ItemRequirement silverlight;
	ItemRequirement silverlightEquipped;

	// Zones
	Zone varrockSewer;
	Zone castleNWFloor1;
	Zone castleNWFloor2;
	Zone castleNEFloor1;
	Zone towerFloor1;

	// Miscellaneous requirements
	ZoneRequirement inVarrockSewer;
	ZoneRequirement inCastleNWFloor1;
	ZoneRequirement inCastleNWFloor2;
	ZoneRequirement inCastleNEFloor1;
	VarbitRequirement hasPouredWaterIntoDrain;
	ZoneRequirement inTowerFloor1;
	VarbitRequirement obtainedSilverlight;
	NpcCondition delrithNearby;
	NpcCondition delrithWeakenedNearby;
	VarbitRequirement inInstance;

	// Steps
	// -- Starting off
	NpcStep talkToAris;
	NpcStep talkToPrysin;

	// -- Get Rovin's key
	ConditionalStep getFirstKey;
	ObjectStep goUpToRovin;
	ObjectStep goUpToRovin2;
	NpcStep talkToRovin;

	// -- Get Prysin's key
	ConditionalStep getSecondKey;
	ObjectStep goDownstairsFromRovin;
	ObjectStep goDownstairsFromRovin2;

	ObjectStep goUpToBucket;
	ItemStep pickupBucket;
	ObjectStep goDownFromBucket;
	ObjectStep fillBucket;

	ObjectStep useFilledBucketOnDrain;
	ObjectStep goDownManhole;
	ObjectStep pickupSecondKey;

	// -- Get Traiborn's key
	ConditionalStep getThirdKey;
	ObjectStep goUpManhole;
	ObjectStep goUpstairsWizard;

	NpcStep talkToTraiborn;

	// -- Kill Delrith
	NpcStep returnToPrysin;
	NpcStep getSilverlightBack;
	IncantationStep killDelrith;
	NpcStep killDelrithStep;

	@Override
	protected void setupZones()
	{
		varrockSewer = new Zone(new WorldPoint(3151, 9855, 0), new WorldPoint(3290, 9919, 0));
		castleNWFloor1 = new Zone(new WorldPoint(3200, 3490, 1), new WorldPoint(3206, 3500, 1));
		castleNWFloor2 = new Zone(new WorldPoint(3200, 3494, 2), new WorldPoint(3206, 3500, 2));
		castleNEFloor1 = new Zone(new WorldPoint(3207, 3487, 1), new WorldPoint(3225, 3497, 1));
		towerFloor1 = new Zone(new WorldPoint(3102, 3154, 1), new WorldPoint(3114, 3165, 1));
	}

	@Override
	protected void setupRequirements()
	{
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY).isNotConsumed();
		bucket.setHighlightInInventory(true);
		bucketOfWater = new ItemRequirement("Bucket of water", ItemID.BUCKET_WATER);
		bucketOfWater.setHighlightInInventory(true);
		key1 = new ItemRequirement("Silverlight key", ItemID.SILVERLIGHT_KEY_2);
		key2 = new ItemRequirement("Silverlight key", ItemID.SILVERLIGHT_KEY_3);
		key3 = new ItemRequirement("Silverlight key", ItemID.SILVERLIGHT_KEY_1);
		bones = new ItemRequirement("Bones (UNNOTED)", ItemID.BONES, 25);
		silverlight = new ItemRequirement("Silverlight", ItemID.SILVERLIGHT).isNotConsumed();
		silverlightEquipped = new ItemRequirement("Silverlight", ItemID.SILVERLIGHT, 1, true).isNotConsumed();
		combatGear = new ItemRequirement("Armour", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getArmour());

		bucketOfWaterOptional = new ItemRequirement("Bucket of water", ItemID.BUCKET_WATER);
		bucketOfWaterOptional.canBeObtainedDuringQuest();

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		wizardsTowerTeleport = new ItemRequirement("Teleport to the Wizards' Tower", ItemID.NECKLACE_OF_PASSAGE_5);
		coin = new ItemRequirement("Coin", ItemCollections.COINS);
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inCastleNEFloor1 = new ZoneRequirement(castleNEFloor1);
		inCastleNWFloor1 = new ZoneRequirement(castleNWFloor1);
		inCastleNWFloor2 = new ZoneRequirement(castleNWFloor2);
		inVarrockSewer = new ZoneRequirement(varrockSewer);
		inTowerFloor1 = new ZoneRequirement(towerFloor1);
		// 2568 going to 2 means you've taken the key, thus the key won't be there to be picked up should the key be deleted
		hasPouredWaterIntoDrain = new VarbitRequirement(VarbitID.DELRITH_DRAIN_KEY, 1);
		obtainedSilverlight = new VarbitRequirement(VarbitID.DELRITH_SILVERLIGHT_CASE, 1);
		delrithNearby = new NpcCondition(NpcID.DELRITH);
		delrithWeakenedNearby = new NpcCondition(NpcID.DELRITH_WEAKENED);
		inInstance = new VarbitRequirement(VarbitID.DELRITH_SEEN_SUMMONING_CUTSCENE, 1);
	}

	public void setupSteps()
	{
		// -- Starting off
		talkToAris = new NpcStep(this, NpcID.ARIS, new WorldPoint(3204, 3424, 0), "Talk to Aris in her tent in Varrock Square.", coin);
		talkToAris.addDialogStep("The Demon Slayer Quest");
		talkToAris.addDialogStep("Yes.");
		talkToAris.addDialogStep("Ok, here you go.");
		talkToAris.addDialogStep("Okay, where is he? I'll kill him for you!");
		talkToAris.addDialogStep("So how did Wally kill Delrith?");

		talkToPrysin = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Talk to Sir Prysin in the south west corner of Varrock Castle.");
		talkToPrysin.addDialogStep("Aris said I should come and talk to you.");
		talkToPrysin.addDialogStep("I need to find Silverlight.");
		talkToPrysin.addDialogStep("He's back and unfortunately I've got to deal with him.");
		talkToPrysin.addDialogStep("So give me the keys!");
		talkToPrysin.addDialogStep("Can you give me your key?");

		// -- Get Rovin's key
		goUpToRovin = new ObjectStep(this, ObjectID.VARROCK_SPIRALSTAIRS_TALLER, new WorldPoint(3203, 3498, 0), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goUpToRovin2 = new ObjectStep(this, ObjectID.VARROCK_SPIRALSTAIRS_MIDDLE_TALLER, new WorldPoint(3203, 3498, 1), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		talkToRovin = new NpcStep(this, NpcID.CAPTAIN_ROVIN, new WorldPoint(3205, 3498, 2), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		talkToRovin.addDialogStep("Yes I know, but this is important.");
		talkToRovin.addDialogStep("There's a demon who wants to invade this city.");
		talkToRovin.addDialogStep("Yes, very.");
		talkToRovin.addDialogStep("It's not them who are going to fight the demon, it's me.");
		talkToRovin.addDialogStep("Sir Prysin said you would give me the key.");
		talkToRovin.addDialogStep("Why did he give you one of the keys then?");
		talkToRovin.addSubSteps(goUpToRovin, goUpToRovin2);

		// -- Get Prysin's key
		goDownstairsFromRovin = new ObjectStep(this, ObjectID.VARROCK_SPIRALSTAIRSTOP, new WorldPoint(3203, 3498, 2), "Go to the Varrock Castle kitchen.");
		goDownstairsFromRovin2 = new ObjectStep(this, ObjectID.VARROCK_SPIRALSTAIRS_MIDDLE_TALLER, new WorldPoint(3203, 3498, 1), "Go to the Varrock Castle kitchen.");

		goUpToBucket = new ObjectStep(this, ObjectID.VARROCK_SPIRALSTAIRS, new WorldPoint(3219, 3497, 0), "Get a bucket from above the Varrock Castle kitchen.");
		pickupBucket = new ItemStep(this, new WorldPoint(3221, 3497, 1), "Pick up the bucket nearby.", bucket);
		goDownFromBucket = new ObjectStep(this, ObjectID.VARROCK_SPIRALSTAIRSTOP, new WorldPoint(3219, 3497, 1), "Go back down to the kitchen.");
		fillBucket = new ObjectStep(this, ObjectID.FAI_VARROCK_POSH_SINK, new WorldPoint(3224, 3495, 0), "Use the bucket on the sink.", bucket);
		fillBucket.addIcon(ItemID.BUCKET_EMPTY);

		useFilledBucketOnDrain = new ObjectStep(this, ObjectID.QIP_DS_QUESTDRAIN_KEY, new WorldPoint(3225, 3496, 0), "Use the bucket of water on the drain outside the kitchen.", bucketOfWater);
		useFilledBucketOnDrain.addAlternateObjects(ObjectID.QIP_DS_QUESTDRAIN_NOKEY);
		useFilledBucketOnDrain.addIcon(ItemID.BUCKET_WATER);
		useFilledBucketOnDrain.addSubSteps(goDownstairsFromRovin, goDownstairsFromRovin2, goUpToBucket, pickupBucket, goDownFromBucket, fillBucket);
		goDownManhole = new ObjectStep(this, ObjectID.MANHOLEOPEN, new WorldPoint(3237, 3458, 0), "Go down into Varrock Sewer via the Manhole south east of Varrock Castle.");
		goDownManhole.addAlternateObjects(ObjectID.MANHOLECLOSED);
		pickupSecondKey = new ObjectStep(this, ObjectID.QIP_DS_SEWER_KEY, new WorldPoint(3225, 9897, 0), "Pick up the Rusty Key north of the Sewer entrance.");

		// -- Get Traiborn's key
		goUpManhole = new ObjectStep(this, ObjectID.FAI_VARROCK_MANHOLE_LADDER, new WorldPoint(3237, 9858, 0), "Bring Wizard Traiborn 25 bones in the Wizards' Tower.", bones);
		goUpstairsWizard = new ObjectStep(this, ObjectID.FAI_WIZTOWER_SPIRALSTAIRS, new WorldPoint(3104, 3160, 0), "Bring Wizard Traiborn 25 bones in the Wizards' Tower.", bones);

		talkToTraiborn = new NpcStep(this, NpcID.TRAIBORN, new WorldPoint(3114, 3163, 1), "Bring Wizard Traiborn 25 bones in the Wizards' Tower. You don't need to bring them all at once.", bones);
		talkToTraiborn.addDialogStep("Talk about Demon Slayer.");
		talkToTraiborn.addDialogStep("I need to get a key given to you by Sir Prysin.");
		talkToTraiborn.addDialogStep("Well, have you got any keys knocking around?");
		talkToTraiborn.addDialogStep("I'll get the bones for you.");
		talkToTraiborn.addSubSteps(goUpManhole, goUpstairsWizard);

		// -- Kill Delrith
		returnToPrysin = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Return to Sir Prysin in the south west of Varrock Castle.", key1, key2, key3);
		getSilverlightBack = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0), "Get Silverlight back from Sir Prysin in the south west of Varrock Castle.");
		returnToPrysin.addSubSteps(getSilverlightBack);

		killDelrithStep = new NpcStep(this, NpcID.DELRITH, new WorldPoint(3227, 3370, 0), "Kill Delrith (level 27) using Silverlight at the dark wizards south of Varrock. Once defeated, you'll need to say the magic words to banish him.", silverlightEquipped, combatGear);
		killDelrith = new IncantationStep(this, killDelrithStep);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToAris);
		steps.put(1, talkToPrysin);

		getFirstKey = new ConditionalStep(this, goUpToRovin);
		getFirstKey.addStep(inCastleNWFloor2, talkToRovin);
		getFirstKey.addStep(inCastleNWFloor1, goUpToRovin2);
		getFirstKey.setLockingCondition(or(obtainedSilverlight, key1.alsoCheckBank()));

		getSecondKey = new ConditionalStep(this, goUpToBucket);
		getSecondKey.addStep(and(hasPouredWaterIntoDrain, inVarrockSewer), pickupSecondKey);
		getSecondKey.addStep(hasPouredWaterIntoDrain, goDownManhole);
		getSecondKey.addStep(inCastleNWFloor1, goDownstairsFromRovin2);
		getSecondKey.addStep(inCastleNWFloor2, goDownstairsFromRovin);
		getSecondKey.addStep(bucketOfWater, useFilledBucketOnDrain);
		getSecondKey.addStep(and(inCastleNEFloor1, bucketOfWater), goDownFromBucket);
		getSecondKey.addStep(bucket, fillBucket);
		getSecondKey.addStep(inCastleNEFloor1, pickupBucket);
		getSecondKey.setLockingCondition(or(obtainedSilverlight, key2.alsoCheckBank()));

		getThirdKey = new ConditionalStep(this, goUpstairsWizard);
		getThirdKey.addStep(inTowerFloor1, talkToTraiborn);
		getThirdKey.addStep(inVarrockSewer, goUpManhole);
		getThirdKey.setLockingCondition(or(obtainedSilverlight, key3.alsoCheckBank()));

		var goAndKillDelrith = new ConditionalStep(this, getSilverlightBack);
		goAndKillDelrith.addStep(silverlight.alsoCheckBank(), killDelrith);

		var getKeys = new ConditionalStep(this, getFirstKey);
		getKeys.addStep(obtainedSilverlight, goAndKillDelrith);
		getKeys.addStep(and(key1, key2, key3), returnToPrysin);
		getKeys.addStep(and(key1, key2), getThirdKey);
		getKeys.addStep(key1, getSecondKey);

		steps.put(2, getKeys);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			coin,
			bones,
			bucketOfWaterOptional,
			combatGear,
			food
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			varrockTeleport,
			wizardsTowerTeleport
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Delrith (level 27)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Silverlight", ItemID.SILVERLIGHT, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToAris,
			talkToPrysin
		), List.of(
			coin,
			bucketOfWaterOptional
		)));

		var rovinPanel = new PanelDetails("Get Rovin's key", List.of(
			talkToRovin
		), List.of(
			bucketOfWaterOptional
		));
		rovinPanel.setLockingStep(getFirstKey);
		sections.add(rovinPanel);

		var prysinPanel = new PanelDetails("Get Prysin's key", List.of(
			useFilledBucketOnDrain,
			goDownManhole,
			pickupSecondKey
		));
		prysinPanel.setLockingStep(getSecondKey);
		sections.add(prysinPanel);

		var traibornPanel = new PanelDetails("Get Traiborn's key", List.of(
			talkToTraiborn
		), List.of(
			bones
		));
		traibornPanel.setLockingStep(getThirdKey);
		sections.add(traibornPanel);

		sections.add(new PanelDetails("Kill Delrith", List.of(
			returnToPrysin,
			killDelrithStep
		), List.of(
			silverlight,
			combatGear,
			food
		)));

		return sections;
	}
}
