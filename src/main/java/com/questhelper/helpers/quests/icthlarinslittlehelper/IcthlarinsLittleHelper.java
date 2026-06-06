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
package com.questhelper.helpers.quests.icthlarinslittlehelper;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.collections.NpcCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.FollowerItemRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.FollowerRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

public class IcthlarinsLittleHelper extends BasicQuestHelper
{
	//Items Required
	ItemRequirement cat, tinderbox, coins600, bagOfSaltOrBucket, willowLog, bucketOfSap, waterskin4, food, sphinxsToken, jar,
		coinsOrLinen, coins30, linen, holySymbol, unholySymbol, combatGear, prayerPotions, antipoison, bucketOfSaltwater, salt, bucket;

	Requirement catFollower;

	Requirement inSoph, inPyramid, inNorthPyramid, puzzleOpen, givenToken, hasScarabasJar, hasCrondisJar, hasHetJar, hasApmekenJar,
		killedGuardian, talkedToEmbalmer, givenLinen, givenSalt, givenSap, givenEmbalmerAllItems, talkedToCarpenter,
		givenCarpenterLogs, inEastRoom, possessedPriestNearby;

	QuestStep talkToWanderer, talkToWandererAgain, enterRock, touchPyramidDoor, jumpPit, openWestDoor, solveDoorPuzzle, talkToSphinx, talkToHighPriest,
		talkToHighPriestWithoutToken, openPyramidDoor, jumpPitAgain, pickUpScarabasJar, pickUpCrondisJar, pickUpHetJar, pickUpApmekenJar,
		pickUpScarabasJarAgain, pickUpCrondisJarAgain, pickUpHetJarAgain, pickUpApmekenJarAgain, returnOverPit, jumpOverPitAgain, dropJar,
		dropCrondisJar, dropScarabasJar, dropHetJar, dropApmekenJar, solvePuzzleAgain, leavePyramid, returnToHighPriest, talkToEmbalmer,
		talkToEmbalmerAgain, talkToCarpenter, talkToCarpenterAgain, talkToCarpenterOnceMore, buyLinen, enterRockWithItems,
		openPyramidDoorWithSymbol, jumpPitWithSymbol, enterEastRoom, useSymbolOnSarcopagus, leaveEastRoom, jumpPitWithSymbolAgain, enterEastRoomAgain, killPriest,
		talkToHighPriestInPyramid, enterPyramidAtEnd, leavePyramidToFinish, talkToHighPriestToFinish;

	QuestStep fillBucketWithWater, makeSalt;

	ObjectStep pickUpAnyJar, pickUpAnyJarAgain;

	//Zones
	Zone soph, pyramid, northPyramid, northPyramid2, eastRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, fillBucketWithWater);
		steps.put(1, talkToWanderer);
		steps.put(2, talkToWandererAgain);
		steps.put(3, enterRock);

		ConditionalStep firstMemory = new ConditionalStep(this, enterRock);
		firstMemory.addStep(puzzleOpen, solveDoorPuzzle);
		firstMemory.addStep(inNorthPyramid, openWestDoor);
		firstMemory.addStep(inPyramid, jumpPit);
		firstMemory.addStep(inSoph, touchPyramidDoor);

		steps.put(4, firstMemory);
		steps.put(5, firstMemory);

		ConditionalStep talkToSphinxSteps = new ConditionalStep(this, enterRock);
		talkToSphinxSteps.addStep(inSoph, talkToSphinx);

		steps.put(6, talkToSphinxSteps);

		ConditionalStep talkToHighPriestSteps = new ConditionalStep(this, enterRock);
		talkToHighPriestSteps.addStep(new Conditions(inSoph, givenToken), talkToHighPriestWithoutToken);
		talkToHighPriestSteps.addStep(inSoph, talkToHighPriest);

		steps.put(7, talkToHighPriestSteps);

		ConditionalStep takeTheJar = new ConditionalStep(this, enterRock);
		takeTheJar.addStep(new Conditions(inNorthPyramid, jar), returnOverPit);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasHetJar, killedGuardian), pickUpHetJarAgain);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasCrondisJar, killedGuardian), pickUpCrondisJarAgain);
		takeTheJar.addStep(new Conditions(inNorthPyramid, killedGuardian), pickUpAnyJarAgain);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasHetJar), pickUpHetJar);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasCrondisJar), pickUpCrondisJar);
		takeTheJar.addStep(inNorthPyramid, pickUpAnyJar);
		takeTheJar.addStep(inPyramid, jumpPitAgain);
		takeTheJar.addStep(inSoph, openPyramidDoor);

		steps.put(8, takeTheJar);
		steps.put(9, takeTheJar);
		steps.put(10, takeTheJar);
		steps.put(11, takeTheJar);
		steps.put(12, takeTheJar);

		ConditionalStep returnTheJar = new ConditionalStep(this, enterRock);
		returnTheJar.addStep(puzzleOpen, solvePuzzleAgain);
		returnTheJar.addStep(new Conditions(inNorthPyramid, hasHetJar), dropHetJar);
		returnTheJar.addStep(new Conditions(inNorthPyramid, hasCrondisJar), dropCrondisJar);
		returnTheJar.addStep(inNorthPyramid, dropJar);
		returnTheJar.addStep(inPyramid, jumpOverPitAgain);
		returnTheJar.addStep(inSoph, openPyramidDoor);

		steps.put(13, returnTheJar);
		steps.put(14, returnTheJar);

		ConditionalStep afterPlacingJarSteps = new ConditionalStep(this, enterRock);
		afterPlacingJarSteps.addStep(inSoph, returnToHighPriest);
		afterPlacingJarSteps.addStep(inPyramid, leavePyramid);

		steps.put(15, afterPlacingJarSteps);

		ConditionalStep prepareItems = new ConditionalStep(this, enterRockWithItems);
		prepareItems.addStep(new Conditions(inSoph, givenEmbalmerAllItems, givenCarpenterLogs), talkToCarpenterOnceMore);
		prepareItems.addStep(new Conditions(inSoph, givenEmbalmerAllItems, talkedToCarpenter), talkToCarpenterAgain);
		prepareItems.addStep(new Conditions(inSoph, givenEmbalmerAllItems), talkToCarpenter);
		Requirement givenOrHaveLinen = new Conditions(LogicType.OR, linen, givenLinen);
		Requirement givenOrHaveSalt = new Conditions(LogicType.OR, salt, givenSalt);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, givenOrHaveLinen, givenOrHaveSalt), talkToEmbalmerAgain);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, givenOrHaveLinen), fillBucketWithWater);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, givenOrHaveLinen, bucketOfSaltwater), makeSalt);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer), buyLinen);
		prepareItems.addStep(new Conditions(inSoph), talkToEmbalmer);

		steps.put(16, prepareItems);

		ConditionalStep goToRitual = new ConditionalStep(this, enterRock);
		goToRitual.addStep(new Conditions(inEastRoom, possessedPriestNearby), killPriest);
		goToRitual.addStep(inEastRoom, talkToHighPriestInPyramid);
		goToRitual.addStep(inNorthPyramid, enterEastRoomAgain);
		goToRitual.addStep(inPyramid, jumpPitWithSymbol);
		goToRitual.addStep(inSoph, openPyramidDoorWithSymbol);

		steps.put(17, goToRitual);

		ConditionalStep placeSymbol = new ConditionalStep(this, enterRock);
		placeSymbol.addStep(inEastRoom, useSymbolOnSarcopagus);
		placeSymbol.addStep(inPyramid, enterEastRoom);
		placeSymbol.addStep(inSoph, openPyramidDoorWithSymbol);

		steps.put(18, placeSymbol);

		steps.put(19, leaveEastRoom);

		steps.put(20, goToRitual);
		steps.put(21, goToRitual);
		steps.put(22, goToRitual);

		steps.put(23, goToRitual);
		steps.put(24, goToRitual);

		ConditionalStep meetIcthlarin = new ConditionalStep(this, enterPyramidAtEnd);
		meetIcthlarin.addStep(inPyramid, leavePyramidToFinish);
		steps.put(25, meetIcthlarin);

		ConditionalStep finishTheQuest = new ConditionalStep(this, enterRock);
		finishTheQuest.addStep(inPyramid, leavePyramidToFinish);
		finishTheQuest.addStep(inSoph, talkToHighPriestToFinish);
		steps.put(26, finishTheQuest);
		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		setupConditions();
		cat = new FollowerItemRequirement("Any cat or kitten",
			ItemCollections.CATS,
			NpcCollections.getCats()).isNotConsumed();

		catFollower = new FollowerRequirement("Any cat following you", NpcCollections.getCats());
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		waterskin4 = new ItemRequirement("Waterskin(4), bring a few to avoid drinking it", ItemID.WATER_SKIN4);
		coins600 = new ItemRequirement("Coins or more for various payments", ItemCollections.COINS, 600);
		bagOfSaltOrBucket = new ItemRequirement("Bag of Salt from a Slayer Master, or an empty bucket to get some", ItemID.SLAYER_BAG_OF_SALT).hideConditioned(givenSalt);
		bagOfSaltOrBucket.addAlternates(ItemID.ICS_LITTLE_PILEOFSALT, ItemID.BUCKET_EMPTY);
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		salt = new ItemRequirement("Salt", ItemID.SLAYER_BAG_OF_SALT).hideConditioned(givenSalt);
		salt.addAlternates(ItemID.ICS_LITTLE_PILEOFSALT);


		coins30 = new ItemRequirement("Coins", ItemCollections.COINS, 30).hideConditioned(givenLinen);
		linen = new ItemRequirement("Linen", ItemID.ICS_LITTLE_LINEN).hideConditioned(givenLinen);

		coinsOrLinen = new ItemRequirements(LogicType.OR, "1 x Linen or 30 coins to buy some", coins30, linen).hideConditioned(givenLinen);

		willowLog = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS).hideConditioned(givenCarpenterLogs);
		bucketOfSap = new ItemRequirement("Bucket of sap", ItemID.ICS_LITTLE_SAP_BUCKET).hideConditioned(givenSap);
		bucketOfSap.setTooltip("You can get this by using a knife on an evergreen tree with a bucket in your " +
			"inventory");

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS, -1);
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS, -1);
		combatGear = new ItemRequirement("Combat equipment", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		sphinxsToken = new ItemRequirement("Sphinx's token", ItemID.ICS_LITTLE_SPHINXSTATUE);
		sphinxsToken.setTooltip("You can get another from the Sphinx");
		jar = new ItemRequirement("Canopic jar", ItemID.ICS_LITTLE_CANOPIC_JAR_LIVER);
		jar.addAlternates(ItemID.ICS_LITTLE_CANOPIC_JAR_INTESTINES, ItemID.ICS_LITTLE_CANOPIC_JAR_STOMACH, ItemID.ICS_LITTLE_CANOPIC_JAR_LUNGS);
		jar.setHighlightInInventory(true);

		bucketOfSaltwater = new ItemRequirement("Bucket of saltwater", ItemID.ICS_LITTLE_SALTWATERBUCKET);

		holySymbol = new ItemRequirement("Holy symbol", ItemID.ICS_LITTLE_HOLY_SYMBOL);
		holySymbol.setTooltip("You can get another from the Carpenter in Sophanem");

		unholySymbol = new ItemRequirement("Unholy symbol", ItemID.ICS_LITTLE_UNHOLY_SYMBOL);
		unholySymbol.setHighlightInInventory(true);
	}

	public void setupConditions()
	{
		puzzleOpen = new WidgetModelRequirement(147, 3, 6474);
		givenToken = new VarbitRequirement(VarbitID.ICS_GIVENSPHINXSTATUE, 1);

		hasHetJar = new VarbitRequirement(VarbitID.ICS_LITTLE_JAR_MULTI, 1);
		hasCrondisJar = new VarbitRequirement(VarbitID.ICS_LITTLE_JAR_MULTI, 4);

		// TODO: Verify varbit values for apmeken/scarabas
		hasApmekenJar = new VarbitRequirement(VarbitID.ICS_LITTLE_JAR_MULTI, 3);
		hasScarabasJar = new VarbitRequirement(VarbitID.ICS_LITTLE_JAR_MULTI, 2);
		killedGuardian = new VarbitRequirement(VarbitID.ICS_LITTLE_VAR, 11, Operation.GREATER_EQUAL);

		// picked up het, 404 = 1
		// picked up apmeken, 405 = 1
		talkedToEmbalmer = new VarbitRequirement(VarbitID.ICS_METEMBALMER, 1);

		givenSalt = new VarbitRequirement(VarbitID.ICS_GOTSALT, 1);
		givenSap = new VarbitRequirement(VarbitID.ICS_GOTSAP, 1);
		givenLinen = new VarbitRequirement(VarbitID.ICS_GOTLINEN, 1);
		givenEmbalmerAllItems = new VarbitRequirement(VarbitID.ICS_LITTLE_EMBALMER_MULTI, 7);

		talkedToCarpenter = new VarbitRequirement(VarbitID.ICS_METCARPENTER, 1);
		givenCarpenterLogs = new VarbitRequirement(VarbitID.ICS_LITTLE_CARPENTER_MULTI, 1);

		possessedPriestNearby = new NpcCondition(NpcID.ICS_LITTLE_POSSESSEDPRIEST);
	}

	@Override
	protected void setupZones()
	{
		soph = new Zone(new WorldPoint(3262, 2751, 0), new WorldPoint(3322, 2809, 0));
		pyramid = new Zone(new WorldPoint(3273, 9170, 0), new WorldPoint(3311, 9204, 0));
		northPyramid = new Zone(new WorldPoint(3276, 9194, 0), new WorldPoint(3311, 9204, 0));
		northPyramid2 = new Zone(new WorldPoint(3276, 9192, 0), new WorldPoint(3287, 9193, 0));
		eastRoom = new Zone(new WorldPoint(3300, 9192, 0), new WorldPoint(3311, 9199, 0));

		inSoph = new ZoneRequirement(soph);
		inPyramid = new ZoneRequirement(pyramid);
		inNorthPyramid = new ZoneRequirement(northPyramid, northPyramid2, eastRoom);
		inEastRoom = new ZoneRequirement(eastRoom);
	}

	public void setupSteps()
	{
		talkToWanderer = new NpcStep(this, NpcID.ICS_LITTLE_REDHEADLADY, new WorldPoint(3316, 2849, 0), "Talk to the Wanderer west of the Agility Pyramid.", catFollower, waterskin4, tinderbox);
		talkToWanderer.addDialogStep("Yes.");
		talkToWanderer.addDialogStep("Why? What's your problem with it?");
		talkToWanderer.addDialogStep("Ok I'll get your supplies.");

		talkToWandererAgain = new NpcStep(this, NpcID.ICS_LITTLE_REDHEADLADY, new WorldPoint(3316, 2849, 0), "Talk to the Wanderer again with the required items.", waterskin4, tinderbox);
		talkToWandererAgain.addDialogStep("Yes. I have them all here.");

		enterRock = new ObjectStep(this, ObjectID.ICS_LITTLE_ENTRANCE_MULTI, new WorldPoint(3324, 2858, 0), "Enter the rock west of the Agility Pyramid to re-enter Sophanem.");

		touchPyramidDoor = new ObjectStep(this, ObjectID.ICTHALARINS_TEMPLE_DOOR, new WorldPoint(3295, 2779, 0), "Enter the pyramid in the south of Sophanem.");

		jumpPit = new ObjectStep(this, ObjectID.ICS_LITTLE_PIT_TO, new WorldPoint(3292, 9194, 0), "Follow the path until you reach a pit, and jump it. Move using the minimap to avoid all the traps.");

		openWestDoor = new ObjectStep(this, ObjectID.ICTHALARINS_ANCIENT_TEMPLE_DOOR_1, new WorldPoint(3280, 9199, 0), "Attempt to open the western door.");

		solveDoorPuzzle = new PuzzleWrapperStep(this, new DoorPuzzleStep(this), "Solve the door puzzle.");

		talkToSphinx = new NpcStep(this, NpcID.ICS_LITTLE_SPHINX, new WorldPoint(3301, 2785, 0), "Talk to the Sphinx in Sophanem with your cat, and answer its riddle with '9.'.", catFollower);
		talkToSphinx.addDialogStep("I need help.");
		talkToSphinx.addDialogStep("Okay, that sounds fair.");
		talkToSphinx.addDialogStep("9.");
		talkToSphinx.addDialogStep("Totally positive.");

		talkToHighPriest = new NpcStep(this, NpcID.ICS_LITTLE_HIPRIEST_VIS, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in the south west of Sophanem.", sphinxsToken);
		talkToHighPriestWithoutToken = new NpcStep(this, NpcID.ICS_LITTLE_HIPRIEST_VIS, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in the south west of Sophanem.");
		talkToHighPriest.addSubSteps(talkToHighPriestWithoutToken);

		openPyramidDoor = new ObjectStep(this, ObjectID.ICTHALARINS_TEMPLE_DOOR, new WorldPoint(3295, 2779, 0), "Enter the pyramid in the south of Sophanem.", catFollower);

		jumpPitAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_PIT_TO, new WorldPoint(3292, 9194, 0), "Follow the path again until you reach a pit, and jump it. Move using the minimap to avoid all the traps.");

		pickUpCrondisJar = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_LUNGS_MULTI, new WorldPoint(3286, 9195, 0), "Attempt to pick up the Crondis Canopic Jar, and kill Apparition (level 75) when they appear. They will attack with magic.");
		pickUpScarabasJar = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_STOMACH_MULTI, new WorldPoint(3286, 9196, 0), "Attempt to pick up the Scarabas Canopic Jar, and kill Apparition (level 75) when they appear. They will attack with melee.");
		pickUpApmekenJar = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_INTESTINES_MULTI, new WorldPoint(3286, 9193, 0), "Attempt to pick up the Apmeken Canopic Jar, and kill Apparition (level 75) when they appear. They will attack with magic.");
		pickUpHetJar = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_LIVER_MULTI, new WorldPoint(3286, 9194, 0), "Attempt to pick up the Het Canopic Jar, and kill Apparition (level 81) when they appear. They will attack with melee.");

		pickUpAnyJar = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_LIVER_MULTI, "Try picking up the canopic jars in the north " +
			"west room until a level 75-81 enemy spawns. Kill them. You can safespot them on the central table.");
		pickUpAnyJar.addAlternateObjects(ObjectID.ICS_LITTLE_POT_LUNGS_MULTI, ObjectID.ICS_LITTLE_POT_STOMACH_MULTI, ObjectID.ICS_LITTLE_POT_INTESTINES_MULTI);
		pickUpAnyJar.addSubSteps(pickUpCrondisJar, pickUpScarabasJar, pickUpApmekenJar, pickUpHetJar);

		pickUpCrondisJarAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_LUNGS_MULTI, new WorldPoint(3286, 9195, 0), "Pick up the Crondis Canopic Jar.");
		pickUpScarabasJarAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_STOMACH_MULTI, new WorldPoint(3286, 9196, 0), "Pick up the Scarabas Canopic Jar.");
		pickUpApmekenJarAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_INTESTINES_MULTI, new WorldPoint(3286, 9193, 0), "Pick up the Apmeken Canopic Jar.");
		pickUpHetJarAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_LIVER_MULTI, new WorldPoint(3286, 9194, 0), "Pick up the Het Canopic Jar.");

		pickUpAnyJarAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_POT_LIVER_MULTI, "Try picking up the canopic jars in the north west room again.");
		pickUpAnyJarAgain.addAlternateObjects(ObjectID.ICS_LITTLE_POT_LUNGS_MULTI, ObjectID.ICS_LITTLE_POT_STOMACH_MULTI, ObjectID.ICS_LITTLE_POT_INTESTINES_MULTI);
		pickUpAnyJarAgain.addSubSteps(pickUpCrondisJarAgain, pickUpScarabasJarAgain, pickUpApmekenJarAgain, pickUpHetJarAgain);

		returnOverPit = new ObjectStep(this, ObjectID.ICS_LITTLE_PIT_FROM, new WorldPoint(3292, 9196, 0), "Jump back over the pit with the jar.");
		jumpOverPitAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_PIT_TO, new WorldPoint(3292, 9194, 0), "Jump back over to the north side of the pit. " +
			"Move using the minimap to avoid all the traps inside the pyramid.");

		dropCrondisJar = new DetailedQuestStep(this, new WorldPoint(3286, 9195, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropApmekenJar = new DetailedQuestStep(this, new WorldPoint(3286, 9193, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropHetJar = new DetailedQuestStep(this, new WorldPoint(3286, 9194, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropScarabasJar = new DetailedQuestStep(this, new WorldPoint(3286, 9196, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropJar = new DetailedQuestStep(this, "Drop the canopic jar in the spot you took it from.", jar);
		dropJar.addSubSteps(dropCrondisJar, dropApmekenJar, dropHetJar, dropScarabasJar);

		solvePuzzleAgain = new PuzzleWrapperStep(this, new DoorPuzzleStep(this), "Solve the door puzzle again.");

		leavePyramid = new ObjectStep(this, ObjectID.ICS_LADDER, new WorldPoint(3277, 9172, 0), "Leave the pyramid and return to the High Priest.");
		returnToHighPriest = new NpcStep(this, NpcID.ICS_LITTLE_HIPRIEST_VIS, new WorldPoint(3281, 2772, 0), "Return to the High Priest in the south west of Sophanem.");
		returnToHighPriest.addDialogStep("Sure, no problem.");
		leavePyramid.addSubSteps(returnToHighPriest);

		talkToEmbalmer = new NpcStep(this, NpcID.ICS_LITTLE_EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer just south of the High Priest.",
			bagOfSaltOrBucket, bucketOfSap);
		talkToEmbalmerAgain = new NpcStep(this, NpcID.ICS_LITTLE_EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", bagOfSaltOrBucket, linen, bucketOfSap);

		fillBucketWithWater = new ObjectStep(this, ObjectID.ICTHALARINS_WATERS_EDGE, new WorldPoint(3286, 2840, 0), "Fill up a bucket with salt water from the lake north of Sophanem, or buy a bag of salt from a Slayer Master.", bucket);
		makeSalt = new ObjectStep(this, ObjectID.ICTHALARINS_SUNTRAP_CENTRE, new WorldPoint(3305, 2756, 0), "Use the bucket of saltwater on the suntrap in the south east of Sophenam.", bucketOfSaltwater.highlighted());
		makeSalt.addIcon(ItemID.ICS_LITTLE_SALTWATERBUCKET);
		talkToCarpenter = new NpcStep(this, NpcID.ICS_LITTLE_CARPENTER, new WorldPoint(3313, 2770, 0), "Talk to the Carpenter in the east of Sophanem until he gives you the holy symbol.", willowLog);
		talkToCarpenter.addDialogStep("Alright, I'll get the wood for you.");
		talkToCarpenterAgain = new NpcStep(this, NpcID.ICS_LITTLE_CARPENTER, new WorldPoint(3313, 2770, 0), "Talk to the Carpenter again in the east of Sophanem.");
		talkToCarpenterOnceMore = new NpcStep(this, NpcID.ICS_LITTLE_CARPENTER, new WorldPoint(3313, 2770, 0), "Talk to the Carpenter again in the east of Sophanem once more.");
		talkToCarpenter.addSubSteps(talkToCarpenterAgain, talkToCarpenterOnceMore);
		buyLinen = new NpcStep(this, NpcID.ICS_LITTLE_LINEN1_1OP, new WorldPoint(3311, 2787, 0), "Get some linen. You can buy some from Raetul in east Sophanem for 30 coins.", coins30);

		enterRockWithItems = new ObjectStep(this, ObjectID.ICS_LITTLE_ENTRANCE_MULTI, new WorldPoint(3324, 2858, 0),
			"Enter the rock west of the Agility Pyramid to re-enter Sophanem. Make sure to bring the items you need.", bucketOfSap, bagOfSaltOrBucket, coinsOrLinen, willowLog, catFollower);

		openPyramidDoorWithSymbol = new ObjectStep(this, ObjectID.ICTHALARINS_TEMPLE_DOOR, new WorldPoint(3295, 2779, 0), "Enter the pyramid in the south of Sophanem.", catFollower, holySymbol);

		jumpPitWithSymbol = new ObjectStep(this, ObjectID.ICS_LITTLE_PIT_TO, new WorldPoint(3292, 9194, 0), "Follow the path again until you reach a pit, and jump it. Move using the minimap to avoid all the traps.", holySymbol);

		enterEastRoom = new ObjectStep(this, ObjectID.ICTHALARINS_ANCIENT_TEMPLE_DOOR_2, new WorldPoint(3306, 9199, 0), "Enter the east room.");
		useSymbolOnSarcopagus = new ObjectStep(this, ObjectID.DESERTTREASURE_SARCOPHIGI_WALL, new WorldPoint(3312, 9197, 0), "Use the unholy symbol on a sarcophagus.", unholySymbol);
		useSymbolOnSarcopagus.addIcon(ItemID.ICS_LITTLE_UNHOLY_SYMBOL);

		leaveEastRoom = new ObjectStep(this, ObjectID.ICTHALARINS_ANCIENT_TEMPLE_DOOR_2, new WorldPoint(3306, 9199, 0), "Leave the east room.");

		jumpPitWithSymbolAgain = new ObjectStep(this, ObjectID.ICS_LITTLE_PIT_TO, new WorldPoint(3292, 9194, 0), "Jump over the pit.", holySymbol);

		enterEastRoomAgain = new ObjectStep(this, ObjectID.ICTHALARINS_ANCIENT_TEMPLE_DOOR_2, new WorldPoint(3306, 9199, 0), "Enter the east room again.");

		killPriest = new NpcStep(this, NpcID.ICS_LITTLE_POSSESSEDPRIEST, new WorldPoint(3306, 9196, 0), "Kill the possessed priest. Pray protect from magic against the priest.");

		talkToHighPriestInPyramid = new NpcStep(this, NpcID.ICS_LITTLE_HIPRIEST_CEREMONY_OP, new WorldPoint(3306, 9196, 0),
			"Talk to the High Priest in the north east room of the pyramid.");

		enterPyramidAtEnd = new ObjectStep(this, ObjectID.ICTHALARINS_TEMPLE_DOOR, new WorldPoint(3295, 2779, 0),
			"Open the door of the pyramid in the south of Sophanem to watch a cutscene.");

		leavePyramidToFinish = new ObjectStep(this, ObjectID.ICS_LADDER, new WorldPoint(3277, 9172, 0), "Leave the pyramid and witness a cutscene at the exit.");
		talkToHighPriestToFinish = new NpcStep(this, NpcID.ICS_LITTLE_HIPRIEST_VIS, new WorldPoint(3281, 2772, 0),
			"Return to the High Priest in the south west of Sophanem to finish the quest.");
		leavePyramidToFinish.addSubSteps(enterPyramidAtEnd);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(cat, tinderbox, coins600, bagOfSaltOrBucket, willowLog, bucketOfSap, waterskin4, coinsOrLinen);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Level 75 or 81 guardian", "Possessed priest (level 91)");
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, food, prayerPotions, antipoison);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new QuestRequirement(QuestHelperQuest.GERTRUDES_CAT, QuestState.FINISHED));
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
			new ExperienceReward(Skill.THIEVING, 4500),
			new ExperienceReward(Skill.AGILITY, 4000),
			new ExperienceReward(Skill.WOODCUTTING, 4000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Catspeak Amulet", ItemID.ICS_LITTLE_AMULET_OF_CATSPEAK, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Access to the city of Sophanem."),
			new UnlockReward("Ability to take carpet rides from Pollnivneach to Sophanem and Menaphos."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(fillBucketWithWater, talkToWanderer, talkToWandererAgain), cat, waterskin4, tinderbox,
			coins600, bagOfSaltOrBucket, willowLog, bucketOfSap));
		allSteps.add(new PanelDetails("Remembering",
			Arrays.asList(touchPyramidDoor, jumpPit, openWestDoor)));

		allSteps.add(new PanelDetails("Returning the jar",
			Arrays.asList(talkToSphinx, talkToHighPriest, openPyramidDoor, jumpPitAgain, pickUpAnyJar,
				pickUpAnyJarAgain, returnOverPit, jumpOverPitAgain, solvePuzzleAgain, dropJar, leavePyramid),
			combatGear, food, cat));

		allSteps.add(new PanelDetails("Prepare the ritual",
			Arrays.asList(talkToEmbalmer, buyLinen, talkToEmbalmerAgain, talkToCarpenter), bucketOfSap, bagOfSaltOrBucket, coinsOrLinen, willowLog));

		allSteps.add(new PanelDetails("Save the ritual",
			Arrays.asList(openPyramidDoorWithSymbol, jumpPitWithSymbol, enterEastRoom, useSymbolOnSarcopagus,
				leaveEastRoom, jumpPitWithSymbolAgain, enterEastRoomAgain, killPriest, talkToHighPriestInPyramid,
				leavePyramidToFinish, talkToHighPriestToFinish), combatGear, food, cat));

		return allSteps;
	}
}
