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
package com.questhelper.quests.icthlarinslittlehelper;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetModelCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.ICTHLARINS_LITTLE_HELPER
)
public class IcthlarinsLittleHelper extends BasicQuestHelper
{
	ItemRequirement cat, tinderbox, coins600, bagOfSaltOrBucket, willowLog, bucketOfSap, waterskin4, food, sphinxsToken, jar, coinsOrLinen, coins30, linen, holySymbol,
		unholySymbol;

	ConditionForStep inSoph, inPyramid, inNorthPyramid, puzzleOpen, givenToken, hasScarabasJar, hasCrondisJar, hasHetJar, hasApmekenJar, killedGuardian,
		hasJar, talkedToEmbalmer, hasLinen, givenLinen, givenSalt, givenSap, givenEmbalmerAllItems, talkedToCarpenter, givenCarpenterLogs, inEastRoom,
		posessedPriestNearby;

	QuestStep talkToWanderer, talkToWandererAgain, enterRock, touchPyramidDoor, jumpPit, openWestDoor, solveDoorPuzzle, talkToSphinx, talkToHighPriest,
		talkToHighPriestWithoutToken, openPyramidDoor, jumpPitAgain, pickUpScarabasJar, pickUpCrondisJar, pickUpHetJar, pickUpApmekenJar,
		pickUpScarabasJarAgain, pickUpCrondisJarAgain, pickUpHetJarAgain, pickUpApmekenJarAgain, returnOverPit, jumpOverPitAgain, dropJar,
		dropCrondisJar, dropScarabasJar, dropHetJar, dropApmekenJar, solvePuzzleAgain, leavePyramid, returnToHighPriest, talkToEmbalmer,
		talkToEmbalmerAgain, talkToCarpenter, talkToCarpenterAgain, talkToCarpenterOnceMore, buyLinen, enterRockWithItems, talkToEmbalmerAgainNoLinen,
		talkToEmbalmerAgainNoSalt, talkToEmbalmerAgainNoSap, talkToEmbalmerAgainNoLinenNoSalt, talkToEmbalmerAgainNoLinenNoSap, talkToEmbalmerAgainNoSaltNoSap,
		openPyramidDoorWithSymbol, jumpPitWithSymbol, enterEastRoom, useSymbolOnSarcopagus, leaveEastRoom, jumpPitWithSymbolAgain, enterEastRoomAgain, killPriest,
		talkToHighPriestInPyramid, leavePyramidToFinish, talkToHighPriestToFinish;

	ObjectStep pickUpAnyJar, pickUpAnyJarAgain;

	Zone soph, pyramid, northPyramid, northPyramid2, eastRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToWanderer);
		steps.put(1, talkToWandererAgain);
		steps.put(2, enterRock);

		ConditionalStep firstMemory = new ConditionalStep(this, enterRock);
		firstMemory.addStep(puzzleOpen, solveDoorPuzzle);
		firstMemory.addStep(inNorthPyramid, openWestDoor);
		firstMemory.addStep(inPyramid, jumpPit);
		firstMemory.addStep(inSoph, touchPyramidDoor);

		steps.put(3, firstMemory);
		steps.put(4, firstMemory);

		ConditionalStep talkToSphinxSteps = new ConditionalStep(this, enterRock);
		talkToSphinxSteps.addStep(inSoph, talkToSphinx);

		steps.put(5, talkToSphinxSteps);

		ConditionalStep talkToHighPriestSteps = new ConditionalStep(this, enterRock);
		talkToHighPriestSteps.addStep(new Conditions(inSoph, givenToken), talkToHighPriestWithoutToken);
		talkToHighPriestSteps.addStep(inSoph, talkToHighPriest);

		steps.put(6, talkToHighPriestSteps);

		ConditionalStep takeTheJar = new ConditionalStep(this, enterRock);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasJar), returnOverPit);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasCrondisJar, killedGuardian), pickUpCrondisJarAgain);
		takeTheJar.addStep(new Conditions(inNorthPyramid, killedGuardian), pickUpAnyJarAgain);
		takeTheJar.addStep(new Conditions(inNorthPyramid, hasCrondisJar), pickUpCrondisJar);
		takeTheJar.addStep(inNorthPyramid, pickUpAnyJar);
		takeTheJar.addStep(inPyramid, jumpPitAgain);
		takeTheJar.addStep(inSoph, openPyramidDoor);

		steps.put(7, takeTheJar);
		steps.put(8, takeTheJar);
		steps.put(9, takeTheJar);
		steps.put(10, takeTheJar);
		steps.put(11, takeTheJar);

		ConditionalStep returnTheJar = new ConditionalStep(this, enterRock);
		returnTheJar.addStep(puzzleOpen, solvePuzzleAgain);
		returnTheJar.addStep(new Conditions(inNorthPyramid, hasCrondisJar), dropCrondisJar);
		returnTheJar.addStep(inNorthPyramid, dropJar);
		returnTheJar.addStep(inPyramid, jumpOverPitAgain);
		returnTheJar.addStep(inSoph, openPyramidDoor);

		steps.put(12, returnTheJar);
		steps.put(13, returnTheJar);

		ConditionalStep afterPlacingJarSteps = new ConditionalStep(this, enterRock);
		afterPlacingJarSteps.addStep(inSoph, returnToHighPriest);
		afterPlacingJarSteps.addStep(inPyramid, leavePyramid);

		steps.put(14, afterPlacingJarSteps);

		ConditionalStep prepareItems = new ConditionalStep(this, enterRockWithItems);
		prepareItems.addStep(new Conditions(inSoph, givenEmbalmerAllItems, givenCarpenterLogs), talkToCarpenterOnceMore);
		prepareItems.addStep(new Conditions(inSoph, givenEmbalmerAllItems, talkedToCarpenter), talkToCarpenterAgain);
		prepareItems.addStep(new Conditions(inSoph, givenEmbalmerAllItems), talkToCarpenter);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, hasLinen, givenSap, givenSalt), talkToEmbalmerAgainNoSaltNoSap);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, givenLinen, givenSap), talkToEmbalmerAgainNoLinenNoSap);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, givenLinen, givenSalt), talkToEmbalmerAgainNoLinenNoSalt);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, hasLinen, givenSalt), talkToEmbalmerAgainNoSalt);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, hasLinen, givenSap), talkToEmbalmerAgainNoSap);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, givenLinen), talkToEmbalmerAgainNoLinen);
		prepareItems.addStep(new Conditions(inSoph, talkedToEmbalmer, hasLinen), talkToEmbalmerAgain);
		prepareItems.addStep(new Conditions(inSoph, hasLinen), talkToEmbalmer);
		prepareItems.addStep(inSoph, buyLinen);

		steps.put(15, prepareItems);

		ConditionalStep goToRitual = new ConditionalStep(this, enterRock);
		goToRitual.addStep(new Conditions(inEastRoom, posessedPriestNearby), killPriest);
		goToRitual.addStep(inEastRoom, talkToHighPriestInPyramid);
		goToRitual.addStep(inNorthPyramid, enterEastRoomAgain);
		goToRitual.addStep(inPyramid, jumpPitWithSymbol);
		goToRitual.addStep(inSoph, openPyramidDoorWithSymbol);

		steps.put(16, goToRitual);

		ConditionalStep placeSymbol = new ConditionalStep(this, enterRock);
		placeSymbol.addStep(inEastRoom, useSymbolOnSarcopagus);
		placeSymbol.addStep(inPyramid, enterEastRoom);

		steps.put(17, placeSymbol);

		steps.put(18, leaveEastRoom);

		steps.put(19, goToRitual);
		steps.put(20, goToRitual);
		steps.put(21, goToRitual);

		steps.put(22, goToRitual);
		steps.put(23, goToRitual);
		steps.put(24, goToRitual);

		ConditionalStep finishTheQuest = new ConditionalStep(this, enterRock);
		finishTheQuest.addStep(inPyramid, leavePyramidToFinish);
		finishTheQuest.addStep(inSoph, talkToHighPriestToFinish);

		steps.put(25, finishTheQuest);
		return steps;
	}

	public void setupItemRequirements()
	{
		cat = new ItemRequirement("Any cat", ItemID.PET_CAT);
		cat.addAlternates(ItemCollections.getCats());
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		waterskin4 = new ItemRequirement("Waterskin(4), bring a few to avoid drinking it", ItemID.WATERSKIN4);
		coins600 = new ItemRequirement("600 coins or more for various payments", ItemID.COINS_995, 600);
		bagOfSaltOrBucket = new ItemRequirement("Bag of Salt from a Slayer Master, or an empty bucket to get some", ItemID.BAG_OF_SALT);
		bagOfSaltOrBucket.addAlternates(ItemID.PILE_OF_SALT);

		coinsOrLinen = new ItemRequirement("Linen or 30 coins to buy some", ItemID.LINEN);

		coins30 = new ItemRequirement("30 coins", ItemID.COINS_995, 30);

		willowLog = new ItemRequirement("Willow logs", ItemID.WILLOW_LOGS);
		bucketOfSap = new ItemRequirement("Bucket of sap", ItemID.BUCKET_OF_SAP);

		food = new ItemRequirement("Combat gear, food + prayer potions", -1, -1);

		sphinxsToken = new ItemRequirement("Sphinx's token", ItemID.SPHINXS_TOKEN);
		sphinxsToken.setTip("You can get another from the Sphinx");
		jar = new ItemRequirement("Canopic jar", ItemID.CANOPIC_JAR);
		jar.addAlternates(ItemID.CANOPIC_JAR_4679, ItemID.CANOPIC_JAR_4680, ItemID.CANOPIC_JAR_4681);
		jar.setHighlightInInventory(true);

		linen = new ItemRequirement("Linen", ItemID.LINEN);

		holySymbol = new ItemRequirement("Holy symbol", ItemID.HOLY_SYMBOL_4682);
		holySymbol.setTip("You can get another from the Carpenter in Sophenham");

		unholySymbol = new ItemRequirement("Unholy symbol", ItemID.UNHOLY_SYMBOL_4683);
		unholySymbol.setHighlightInInventory(true);
	}

	public void setupConditions()
	{
		inSoph = new ZoneCondition(soph);
		inPyramid = new ZoneCondition(pyramid);
		inNorthPyramid = new ZoneCondition(northPyramid, northPyramid2, eastRoom);
		inEastRoom = new ZoneCondition(eastRoom);

		puzzleOpen = new WidgetModelCondition(147, 3, 6474);
		givenToken = new VarbitCondition(450, 1);


		hasCrondisJar = new VarbitCondition(397, 4);

		// TODO: Verify varbit values for apmeken/het/scarabas
		hasApmekenJar = new VarbitCondition(397, 3);
		hasHetJar = new VarbitCondition(397, 2);
		hasScarabasJar = new VarbitCondition(397, 1);

		killedGuardian = new VarbitCondition(418, 11, Operation.GREATER_EQUAL);

		hasJar = new VarbitCondition(405, 1);
		talkedToEmbalmer = new VarbitCondition(399, 1);

		hasLinen = new ItemRequirementCondition(linen);

		givenSalt = new VarbitCondition(401, 1);
		givenSap = new VarbitCondition(402, 1);
		givenLinen = new VarbitCondition(403, 1);
		givenEmbalmerAllItems = new VarbitCondition(400, 7);

		talkedToCarpenter = new VarbitCondition(412, 1);
		givenCarpenterLogs = new VarbitCondition(398, 1);

		posessedPriestNearby = new NpcCondition(NpcID.POSSESSED_PRIEST);
	}

	public void loadZones()
	{
		soph = new Zone(new WorldPoint(3262, 2751, 0), new WorldPoint(3322, 2809, 0));
		pyramid = new Zone(new WorldPoint(3273, 9170, 0), new WorldPoint(3311, 9204, 0));
		northPyramid = new Zone(new WorldPoint(3276, 9194, 0), new WorldPoint(3311, 9204, 0));
		northPyramid2 = new Zone(new WorldPoint(3276, 9192, 0), new WorldPoint(3287, 9193, 0));
		eastRoom = new Zone(new WorldPoint(3300, 9192, 0), new WorldPoint(3311, 9199, 0));
	}

	public void setupSteps()
	{
		talkToWanderer = new NpcStep(this, NpcID.WANDERER_4194, new WorldPoint(3316, 2849, 0), "Talk to the Wanderer west of the Agility Pyramid.", cat, waterskin4, tinderbox);
		talkToWanderer.addDialogStep("Why? What's your problem with it?");
		talkToWanderer.addDialogStep("Ok I'll get your supplies.");

		talkToWandererAgain = new NpcStep(this, NpcID.WANDERER_4194, new WorldPoint(3316, 2849, 0), "Talk to the Wanderer again with the required items.", waterskin4, tinderbox);
		talkToWandererAgain.addDialogStep("Yes. I have them all here.");

		enterRock = new ObjectStep(this, NullObjectID.NULL_6621, new WorldPoint(3324, 2858, 0), "Enter the rock west of the Agility Pyramid to re-enter Sophanhem.");

		touchPyramidDoor = new ObjectStep(this, ObjectID.DOOR_6614, new WorldPoint(3295, 2779, 0), "Touch the south pyramid door in Sophanem.");

		jumpPit = new ObjectStep(this, ObjectID.PIT, new WorldPoint(3292, 9194, 0), "Follow the path until you reach a pit, and jump it. Move using the minimap to avoid all the traps.");

		openWestDoor = new ObjectStep(this, ObjectID.DOORWAY_6643, new WorldPoint(3280, 9199, 0), "Attempt to open the west door.");

		solveDoorPuzzle = new DoorPuzzleStep(this);

		talkToSphinx = new NpcStep(this, NpcID.SPHINX_4209, new WorldPoint(3301, 2785, 0), "Talk to the Sphinx in Sophenham with your cat, and answer its riddle with '9.'.", cat);
		talkToSphinx.addDialogStep("I need help.");
		talkToSphinx.addDialogStep("Okay, that sounds fair.");
		talkToSphinx.addDialogStep("9.");
		talkToSphinx.addDialogStep("Totally positive.");

		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in the south west of Sophanem.", sphinxsToken);
		talkToHighPriestWithoutToken = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Talk to the High Priest in the south west of Sophanem.");
		talkToHighPriest.addSubSteps(talkToHighPriestWithoutToken);

		openPyramidDoor = new ObjectStep(this, ObjectID.DOOR_6614, new WorldPoint(3295, 2779, 0), "Right-click open the south pyramid's door in Sophanem.", cat);

		jumpPitAgain = new ObjectStep(this, ObjectID.PIT, new WorldPoint(3292, 9194, 0), "Follow the path again until you reach a pit, and jump it. Move using the minimap to avoid all the traps.");

		pickUpAnyJar = new ObjectStep(this, NullObjectID.NULL_6634, "Try picking up the canopic jars in the north west room until a level 75-81 enemy spawns. Kill them.");
		pickUpAnyJar.addAlternateObjects(NullObjectID.NULL_6636, NullObjectID.NULL_6638, NullObjectID.NULL_6640);

		pickUpCrondisJar = new ObjectStep(this, NullObjectID.NULL_6636, new WorldPoint(3286, 9195, 0), "Attempt to pick up the Crondis Canopic Jar, and kill Crondis (level 75) when they appear.");
		pickUpScarabasJar = new ObjectStep(this, NullObjectID.NULL_6638, new WorldPoint(3286, 9196, 0), "Attempt to pick up the Scarabas Canopic Jar, and kill Scarabas (level 75) when they appear.");
		pickUpApmekenJar = new ObjectStep(this, NullObjectID.NULL_6640, new WorldPoint(3286, 9193, 0), "Attempt to pick up the Apmeken Canopic Jar, and kill Apmeken (level 75) when they appear.");
		pickUpHetJar = new ObjectStep(this, NullObjectID.NULL_6634, new WorldPoint(3286, 9194, 0), "Attempt to pick up the Het Canopic Jar, and kill Het (level 75) when they appear.");

		pickUpAnyJarAgain = new ObjectStep(this, NullObjectID.NULL_6634, "Try picking up the canopic jars in the north west room again.");
		pickUpAnyJarAgain.addAlternateObjects(NullObjectID.NULL_6636, NullObjectID.NULL_6638, NullObjectID.NULL_6640);

		pickUpCrondisJarAgain = new ObjectStep(this, NullObjectID.NULL_6636, new WorldPoint(3286, 9195, 0), "Pick up the Crondis Canopic Jar.");
		pickUpScarabasJarAgain = new ObjectStep(this, NullObjectID.NULL_6638, new WorldPoint(3286, 9196, 0), "Pick up the Scarabas Canopic Jar.");
		pickUpApmekenJarAgain = new ObjectStep(this, NullObjectID.NULL_6640, new WorldPoint(3286, 9193, 0), "Pick up the Apmeken Canopic Jar.");
		pickUpHetJarAgain = new ObjectStep(this, NullObjectID.NULL_6634, new WorldPoint(3286, 9194, 0), "Pick up the Het Canopic Jar.");

		returnOverPit = new ObjectStep(this, ObjectID.PIT, new WorldPoint(3292, 9196, 0), "Jump back over the pit with the jar.");
		jumpOverPitAgain = new ObjectStep(this, ObjectID.PIT, new WorldPoint(3292, 9194, 0), "Follow the path again until you reach a pit, and jump it. Move using the minimap to avoid all the traps.");

		dropJar = new DetailedQuestStep(this, "Drop the canopic jar in the spot you took it from.", jar);
		dropCrondisJar = new DetailedQuestStep(this, new WorldPoint(3286, 9195, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropApmekenJar = new DetailedQuestStep(this, new WorldPoint(3286, 9193, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropHetJar = new DetailedQuestStep(this, new WorldPoint(3286, 9194, 0), "Drop the canopic jar in the spot you took it from.", jar);
		dropScarabasJar = new DetailedQuestStep(this, new WorldPoint(3286, 9196, 0), "Drop the canopic jar in the spot you took it from.", jar);

		solvePuzzleAgain = new DoorPuzzleStep(this);

		leavePyramid = new ObjectStep(this, ObjectID.LADDER_6645, new WorldPoint(3277, 9172, 0), "Leave the pyramid and return to the High Priest.");
		returnToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Return to the High Priest in the south west of Sophanem.");
		returnToHighPriest.addDialogStep("Sure, no problem.");
		leavePyramid.addSubSteps(returnToHighPriest);

		talkToEmbalmer = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer just south of the High Priest.", bagOfSaltOrBucket, linen, bucketOfSap);
		talkToEmbalmerAgain = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", bagOfSaltOrBucket, linen, bucketOfSap);
		talkToEmbalmerAgainNoLinen = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0),
			"Talk to the Embalmer again just south of the High Priest.", bagOfSaltOrBucket, bucketOfSap);
		talkToEmbalmerAgainNoSalt = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", linen, bucketOfSap);
		talkToEmbalmerAgainNoSap = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", bagOfSaltOrBucket, linen);
		talkToEmbalmerAgainNoLinenNoSalt = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", bucketOfSap);
		talkToEmbalmerAgainNoLinenNoSap = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", bagOfSaltOrBucket);
		talkToEmbalmerAgainNoSaltNoSap = new NpcStep(this, NpcID.EMBALMER, new WorldPoint(3287, 2755, 0), "Talk to the Embalmer again just south of the High Priest.", linen);
		talkToEmbalmerAgain.addSubSteps(talkToEmbalmerAgainNoLinen, talkToEmbalmerAgainNoLinenNoSalt, talkToEmbalmerAgainNoLinenNoSap, talkToEmbalmerAgainNoSalt, talkToEmbalmerAgainNoLinenNoSap, talkToEmbalmerAgainNoSaltNoSap);

		talkToCarpenter = new NpcStep(this, NpcID.CARPENTER, new WorldPoint(3313, 2770, 0), "Talk to the Carpenter in the east of Sophanem.", willowLog);
		talkToCarpenter.addDialogStep("Alright, I'll get the wood for you.");
		talkToCarpenterAgain = new NpcStep(this, NpcID.CARPENTER, new WorldPoint(3313, 2770, 0), "Talk to the Carpenter again in the east of Sophanem.");
		talkToCarpenterOnceMore = new NpcStep(this, NpcID.CARPENTER, new WorldPoint(3313, 2770, 0), "Talk to the Carpenter again in the east of Sophanem once more.");

		buyLinen = new NpcStep(this, NpcID.RAETUL, new WorldPoint(3311, 2787, 0), "Get some linen. You can buy some from Raetul in east Sophanem for 30 coins.", coinsOrLinen);

		enterRockWithItems = new ObjectStep(this, NullObjectID.NULL_6621, new WorldPoint(3324, 2858, 0),
			"Enter the rock west of the Agility Pyramid to re-enter Sophanhem. Make sure to bring the items you need.", bucketOfSap, bagOfSaltOrBucket, coinsOrLinen, willowLog, cat);

		openPyramidDoorWithSymbol = new ObjectStep(this, ObjectID.DOOR_6614, new WorldPoint(3295, 2779, 0), "Right-click open the south pyramid's door in Sophanem.", cat, holySymbol);

		jumpPitWithSymbol = new ObjectStep(this, ObjectID.PIT, new WorldPoint(3292, 9194, 0), "Follow the path again until you reach a pit, and jump it. Move using the minimap to avoid all the traps.", cat, holySymbol);

		enterEastRoom = new ObjectStep(this, ObjectID.DOORWAY_6643, new WorldPoint(3306, 9199, 0), "Enter the east room.");
		useSymbolOnSarcopagus = new ObjectStep(this, ObjectID.SARCOPHAGUS_6630, new WorldPoint(3312, 9197, 0), "Use the unholy symbol on a sarcophagus.", unholySymbol);

		leaveEastRoom = new ObjectStep(this, ObjectID.DOORWAY_6643, new WorldPoint(3306, 9199, 0), "Leave the east room.");

		jumpPitWithSymbolAgain = new ObjectStep(this, ObjectID.PIT, new WorldPoint(3292, 9194, 0), "Jump over the pit.", cat, holySymbol);

		enterEastRoomAgain = new ObjectStep(this, ObjectID.DOORWAY_6643, new WorldPoint(3306, 9199, 0), "Enter the east room again.");

		killPriest = new NpcStep(this, NpcID.POSSESSED_PRIEST, new WorldPoint(3306, 9196, 0), "Kill the posessed priest.");

		talkToHighPriestInPyramid = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3306, 9196, 0), "Talk to the High Priest in the north east room of the pyramid.");

		leavePyramidToFinish = new ObjectStep(this, ObjectID.LADDER_6645, new WorldPoint(3277, 9172, 0), "Leave the pyramid and return to the High Priest.");
		talkToHighPriestToFinish = new NpcStep(this, NpcID.HIGH_PRIEST_4206, new WorldPoint(3281, 2772, 0), "Return to the High Priest in the south west of Sophanem to finish the quest.");
		leavePyramidToFinish.addSubSteps(talkToHighPriestToFinish);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(cat, tinderbox, coins600, bagOfSaltOrBucket, willowLog, bucketOfSap, waterskin4));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Level 75 or 81 guardian", "Possessed priest (level 91)"));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Collections.singletonList(food));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToWanderer, talkToWandererAgain)), cat, waterskin4, tinderbox, coins600, bagOfSaltOrBucket, willowLog, bucketOfSap));
		allSteps.add(new PanelDetails("Remembering",
			new ArrayList<>(Arrays.asList(touchPyramidDoor, jumpPit, openWestDoor))));

		allSteps.add(new PanelDetails("Returning the jar",
			new ArrayList<>(Arrays.asList(talkToSphinx, talkToHighPriest, openPyramidDoor, jumpPitAgain, pickUpAnyJar, pickUpAnyJarAgain, returnOverPit, jumpOverPitAgain, solvePuzzleAgain, dropJar, leavePyramid)), cat));

		allSteps.add(new PanelDetails("Prepare the ritual",
			new ArrayList<>(Arrays.asList(buyLinen, talkToEmbalmer, talkToEmbalmerAgain, talkToCarpenter, talkToCarpenterAgain, talkToCarpenterOnceMore)), bucketOfSap, bagOfSaltOrBucket, coinsOrLinen, willowLog));

		allSteps.add(new PanelDetails("Save the ritual",
			new ArrayList<>(Arrays.asList(openPyramidDoorWithSymbol, jumpPitWithSymbol, enterEastRoom, useSymbolOnSarcopagus, leaveEastRoom, jumpPitWithSymbolAgain, enterEastRoomAgain, killPriest, talkToHighPriestInPyramid, leavePyramidToFinish)), cat));

		return allSteps;
	}
}
