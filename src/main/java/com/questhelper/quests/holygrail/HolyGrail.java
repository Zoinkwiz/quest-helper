/*
 * Copyright (c) 2020, Kijjuy
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
package com.questhelper.quests.holygrail;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.ZoneCondition;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.HOLY_GRAIL
)
public class HolyGrail extends BasicQuestHelper
{
	ItemRequirement excalibur, holyTableNapkin, twoMagicWhistles, highlightMagicWhistle1, threeCamelotTele, ardyTele, faladorTele, sixtyCoins,
		antipoison, combatGear, food, goldFeather, grailBell, highlightGrailBell, emptyInvSpot, oneMagicWhistle, highlightMagicWhistle2, grail;

	ConditionForStep inCamelot, inCamelotUpstairs, inMerlinRoom, merlinNearby, onEntrana, inGalahadHouse, hasNapkin, inDraynorFrontManor, inDraynorManorBottomFloor, inDraynorManorSecondFloor,
		inDraynorManorTopFloor, inMagicWhistleRoom, hasTwoWhistles, inTeleportLocation, hasExcalibur, inFisherKingRealmEntrance, titanNearby, inFisherKingRealmAfterTitan, talkedToFisherman,
		hasGrailBell, inGrailBellRingLocation, inFisherKingCastle1BottomFloor, inFisherKingCastle1SecondFloor, hasFeather, inFisherKingRealm, inFisherKingCastle2BottomFloor, inFisherKingCastle2SecondFloor, inFisherKingCastle2ThirdFloor,
		hasGrail;

	QuestStep talkToKingArthur1, talkToMerlin, goUpStairsCamelot, openMerlinDoor, goToEntrana, talkToHighPriest, goToGalahad, talkToGalahad, goToDraynorManor, enterDraynorManor, goUpStairsDraynor1,
		goUpStairsDraynor2, openWhistleDoor, takeWhistles, goGetExcalibur, goToTeleportLocation1, blowWhistle1, attackTitan, talkToFisherman, pickupBell, ringBell, goUpStairsBrokenCastle, talkToFisherKing, goToCamelot,
		talkToKingArthur2, openSack, goToTeleportLocation2, blowWhistle2, openFisherKingCastleDoor, goUpNewCastleStairs, goUpNewCastleLadder, takeGrail, talkToKingArthur3;

	ConditionalStep findFisherKing;

	Zone camelotGround, camelotUpstairsZone1, camelotUpstairsZone2, merlinRoom, entranaBoat, entranaIsland, galahadHouse, draynorManorFront, draynorManorBottomFloor, draynorManorSecondFloor,
		draynorManorTopFloor, magicWhistleRoom, teleportLocation, fisherKingRealmEntrance, fisherKingRealmAfterTitan1, fisherKingRealmAfterTitan2, fisherKingRealmAfterTitan3, grailBellRingLocation,
		fisherKingRealmCastle1BottomFloor, fisherKingRealmCastle1SecondFloor, fisherKingRealm, fisherKingRealmCastle2BottomFloor, fisherKingRealmCastle2SecondFloor, fisherKingRealmCastle2ThirdFloor;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToKingArthur1);

		ConditionalStep findingMerlin = new ConditionalStep(this, goUpStairsCamelot);
		findingMerlin.addStep(new Conditions(inMerlinRoom, merlinNearby), talkToMerlin);
		findingMerlin.addStep(inCamelotUpstairs, openMerlinDoor);

		steps.put(2, findingMerlin);

		ConditionalStep findHighPriest = new ConditionalStep(this, goToEntrana);
		findHighPriest.addStep(onEntrana, talkToHighPriest);

		steps.put(3, findHighPriest);

		findFisherKing = new ConditionalStep(this, goToGalahad);
		findFisherKing.addStep(inFisherKingCastle1SecondFloor, talkToFisherKing);
		findFisherKing.addStep(inFisherKingCastle1BottomFloor, goUpStairsBrokenCastle);
		findFisherKing.addStep(new Conditions(hasGrailBell, inFisherKingRealmAfterTitan), ringBell);
		findFisherKing.addStep(talkedToFisherman, pickupBell);
		findFisherKing.addStep(inFisherKingRealmAfterTitan, talkToFisherman);
		findFisherKing.addStep(new Conditions(hasExcalibur, titanNearby), attackTitan);
		findFisherKing.addStep(new Conditions(hasTwoWhistles, inTeleportLocation, hasExcalibur), blowWhistle1);
		findFisherKing.addStep(new Conditions(hasTwoWhistles, hasExcalibur), goToTeleportLocation1);
		findFisherKing.addStep(hasTwoWhistles, goGetExcalibur);
		findFisherKing.addStep(inMagicWhistleRoom, takeWhistles);
		findFisherKing.addStep(inDraynorManorTopFloor, openWhistleDoor);
		findFisherKing.addStep(inDraynorManorSecondFloor, goUpStairsDraynor2);
		findFisherKing.addStep(inDraynorManorBottomFloor, goUpStairsDraynor1);
		findFisherKing.addStep(inDraynorFrontManor, enterDraynorManor);
		findFisherKing.addStep(hasNapkin, goToDraynorManor);
		findFisherKing.addStep(inGalahadHouse, talkToGalahad);

		findFisherKing.setLockingCondition(hasTwoWhistles);

		steps.put(4, findFisherKing);

		ConditionalStep findPercival = new ConditionalStep(this, talkToKingArthur2);
		findPercival.addStep(hasFeather, openSack);

		steps.put(7, findPercival);
		steps.put(8, findPercival);

		ConditionalStep finishQuest = new ConditionalStep(this, goToTeleportLocation2);
		finishQuest.addStep(hasGrail, talkToKingArthur3);
		finishQuest.addStep(inFisherKingCastle2ThirdFloor, takeGrail);
		finishQuest.addStep(inFisherKingCastle2SecondFloor, goUpNewCastleLadder);
		finishQuest.addStep(inFisherKingCastle2BottomFloor, goUpNewCastleStairs);
		finishQuest.addStep(inFisherKingRealm, openFisherKingCastleDoor);
		finishQuest.addStep(inTeleportLocation, blowWhistle2);

		steps.put(9, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		excalibur = new ItemRequirement("Excalibur", ItemID.EXCALIBUR);
		holyTableNapkin = new ItemRequirement("Holy Table Napkin", ItemID.HOLY_TABLE_NAPKIN);
		twoMagicWhistles = new ItemRequirement("Magic Whistles", ItemID.MAGIC_WHISTLE, 2);
		threeCamelotTele = new ItemRequirement("Camelot Teleports", ItemID.CAMELOT_TELEPORT, 3);
		ardyTele = new ItemRequirement("Ardougne Teleport", ItemID.ARDOUGNE_TELEPORT);
		faladorTele = new ItemRequirement("Falador Teleport", ItemID.FALADOR_TELEPORT);
		sixtyCoins = new ItemRequirement("Coins", ItemID.COINS_995, 60);
		antipoison = new ItemRequirement("Antipoison", ItemID.ANTIPOISON4);
		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
		combatGear = new ItemRequirement("A weapon and armour", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		emptyInvSpot = new ItemRequirement("Empty Inventory Spot", -1, 1);
		goldFeather = new ItemRequirement("Magic gold feather", ItemID.MAGIC_GOLD_FEATHER);
		grailBell = new ItemRequirement("Grail Bell", ItemID.GRAIL_BELL);
		oneMagicWhistle = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE);
		grail = new ItemRequirement("Holy Grail", ItemID.HOLY_GRAIL);

		highlightMagicWhistle1 = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE, 2);
		highlightMagicWhistle1.setHighlightInInventory(true);

		highlightMagicWhistle2 = new ItemRequirement("Magic Whistle", ItemID.MAGIC_WHISTLE);
		highlightMagicWhistle2.setHighlightInInventory(true);

		highlightGrailBell = new ItemRequirement("Grail Bell", ItemID.GRAIL_BELL);
		highlightGrailBell.setHighlightInInventory(true);
	}

	public void loadZones()
	{
		camelotGround = new Zone(new WorldPoint(2744, 3517, 0), new WorldPoint(2733, 3483, 0));
		camelotUpstairsZone1 = new Zone(new WorldPoint(2768, 3517, 1), new WorldPoint(2757, 3506, 1));
		camelotUpstairsZone2 = new Zone(new WorldPoint(2764, 3517, 1), new WorldPoint(2748, 3496, 1));
		merlinRoom = new Zone(new WorldPoint(2768, 3505, 1), new WorldPoint(2765, 3496, 1));
		entranaBoat = new Zone(new WorldPoint(2841, 3332, 0), new WorldPoint(2823, 3328, 2));
		entranaIsland = new Zone(new WorldPoint(2871, 3393, 0), new WorldPoint(2800, 3329, 2));
		galahadHouse = new Zone(new WorldPoint(2616, 3480, 0), new WorldPoint(2609, 3473, 0));
		draynorManorFront = new Zone(new WorldPoint(3116, 3353, 0), new WorldPoint(3100, 3347, 0));
		draynorManorBottomFloor = new Zone(new WorldPoint(3119, 3373, 0), new WorldPoint(3097, 3354, 0));
		draynorManorSecondFloor = new Zone(new WorldPoint(3118, 3373, 1), new WorldPoint(3098, 3354, 1));
		draynorManorTopFloor = new Zone(new WorldPoint(3112, 3370, 2), new WorldPoint(3104, 3362, 2));
		magicWhistleRoom = new Zone(new WorldPoint(3112, 3361, 2), new WorldPoint(3104, 3357, 2));
		teleportLocation = new Zone(new WorldPoint(2743, 3237, 0), new WorldPoint(2740, 3234, 0));
		fisherKingRealmAfterTitan1 = new Zone(new WorldPoint(2791, 4734, 0), new WorldPoint(2752, 4671, 0));
		fisherKingRealmAfterTitan2 = new Zone(new WorldPoint(2808, 4707, 0), new WorldPoint(2791, 4688, 0));
		fisherKingRealmAfterTitan3 = new Zone(new WorldPoint(2798, 4710, 0), new WorldPoint(2781, 4707, 0));
		grailBellRingLocation = new Zone(new WorldPoint(2762, 4694, 0), new WorldPoint(2761, 4694, 0));
		fisherKingRealmCastle1BottomFloor = new Zone(new WorldPoint(2780, 4692, 0), new WorldPoint(2756, 4675, 0));
		fisherKingRealmCastle1SecondFloor = new Zone(new WorldPoint(2771, 4692, 1), new WorldPoint(2756, 4675, 1));
		fisherKingRealm = new Zone(new WorldPoint(2683, 4733, 0), new WorldPoint(2625, 4672, 0));
		fisherKingRealmCastle2BottomFloor = new Zone(new WorldPoint(2652, 4692, 0), new WorldPoint(2628, 4675, 0));
		fisherKingRealmCastle2SecondFloor = new Zone(new WorldPoint(2652, 4687, 1), new WorldPoint(2646, 4681, 1));
		fisherKingRealmCastle2ThirdFloor = new Zone(new WorldPoint(2651, 4686, 2), new WorldPoint(2647, 4682, 2));
	}

	public void setupConditions()
	{
		inCamelot = new ZoneCondition(camelotGround);
		inCamelotUpstairs = new Conditions(LogicType.OR,
			new ZoneCondition(camelotUpstairsZone1),
			new ZoneCondition(camelotUpstairsZone2));
		inMerlinRoom = new ZoneCondition(merlinRoom);
		merlinNearby = new NpcCondition(NpcID.MERLIN_4059);
		onEntrana = new Conditions(LogicType.OR,
			new ZoneCondition(entranaBoat),
			new ZoneCondition(entranaIsland));
		inGalahadHouse = new ZoneCondition(galahadHouse);
		hasNapkin = new ItemRequirementCondition(holyTableNapkin);
		inDraynorFrontManor = new ZoneCondition(draynorManorFront);
		inDraynorManorBottomFloor = new ZoneCondition(draynorManorBottomFloor);
		inDraynorManorSecondFloor = new ZoneCondition(draynorManorSecondFloor);
		inDraynorManorTopFloor = new ZoneCondition(draynorManorTopFloor);
		inMagicWhistleRoom = new ZoneCondition(magicWhistleRoom);
		hasTwoWhistles = new ItemRequirementCondition(twoMagicWhistles);
		inTeleportLocation = new ZoneCondition(teleportLocation);
		hasExcalibur = new ItemRequirementCondition(excalibur);
		inFisherKingRealmEntrance = new ZoneCondition(fisherKingRealmEntrance);
		titanNearby = new NpcCondition(NpcID.BLACK_KNIGHT_TITAN);
		inFisherKingRealmAfterTitan = new Conditions(LogicType.OR,
			new ZoneCondition(fisherKingRealmAfterTitan1),
			new ZoneCondition(fisherKingRealmAfterTitan2),
			new ZoneCondition(fisherKingRealmAfterTitan3));
		talkedToFisherman = new Conditions(true, new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, "You must be blind then. There's ALWAYS bells there<br>when I go to the castle."));
		hasGrailBell = new ItemRequirementCondition(grailBell);
		inGrailBellRingLocation = new ZoneCondition(grailBellRingLocation);
		inFisherKingCastle1BottomFloor = new ZoneCondition(fisherKingRealmCastle1BottomFloor);
		inFisherKingCastle1SecondFloor = new ZoneCondition(fisherKingRealmCastle1SecondFloor);
		hasFeather = new ItemRequirementCondition(goldFeather);
		inFisherKingRealm = new ZoneCondition(fisherKingRealm);
		inFisherKingCastle2BottomFloor = new ZoneCondition(fisherKingRealmCastle2BottomFloor);
		inFisherKingCastle2SecondFloor = new ZoneCondition(fisherKingRealmCastle2SecondFloor);
		inFisherKingCastle2ThirdFloor = new ZoneCondition(fisherKingRealmCastle2ThirdFloor);
		hasGrail = new ItemRequirementCondition(grail);
	}

	public void setupSteps()
	{
		WorldPoint kingArthurWorldPoint = new WorldPoint(2763, 3513, 0);
		talkToKingArthur1 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Talk to King Arthur in Camelot Castle to start.");
		talkToKingArthur1.addDialogStep("Tell me of this quest.");
		talkToKingArthur1.addDialogStep("I'd enjoy trying that.");
		goUpStairsCamelot = new ObjectStep(this, ObjectID.STAIRCASE_26106, new WorldPoint(2751, 3511, 0), "Go upstairs to talk to Merlin.");
		openMerlinDoor = new ObjectStep(this, ObjectID.DOOR_24, "Open the door to go to Merlin's room.");
		talkToMerlin = new NpcStep(this, NpcID.MERLIN_4059, new WorldPoint(2763, 3513, 1), "Talk to Merlin");
		talkToMerlin.addDialogStep("Where can I find Sir Galahad?");

		goToEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA_1167, new WorldPoint(3048, 3235, 0), "Talk to a monk of Entrana. Bank all combat gear.");
		talkToHighPriest = new NpcStep(this, NpcID.HIGH_PRIEST, new WorldPoint(2851, 3348, 0), "Talk to the High Priest.");
		talkToHighPriest.addDialogSteps("Ask about the Holy Grail Quest", "Ok, I will go searching.");

		goToGalahad = new DetailedQuestStep(this, new WorldPoint(2612, 3475, 0), "Travel to Galahad's House. His house is west of McGrubor's Woods.");
		talkToGalahad = new NpcStep(this, NpcID.GALAHAD, new WorldPoint(2612, 3475, 0), "Talk to Galahad.");
		talkToGalahad.addDialogStep("I seek an item from the realm of the Fisher King.");

		goToDraynorManor = new DetailedQuestStep(this, new WorldPoint(3108, 3350, 0), "Travel to Draynor Manor.", holyTableNapkin);
		enterDraynorManor = new ObjectStep(this, ObjectID.LARGE_DOOR_135, "Enter Draynor Manor.", holyTableNapkin);
		goUpStairsDraynor1 = new ObjectStep(this, ObjectID.STAIRCASE_11498, new WorldPoint(3109, 3364, 0), "Go up the stairs in Draynor Manor.", holyTableNapkin);
		goUpStairsDraynor2 = new ObjectStep(this, ObjectID.STAIRCASE_11511, new WorldPoint(3105, 3363, 1), "Go up the second set of stairs in Draynor Manor.", holyTableNapkin);
		openWhistleDoor = new ObjectStep(this, ObjectID.DOOR_22, "Open the door to the Magic Whistles.", holyTableNapkin);
		takeWhistles = new DetailedQuestStep(this, new WorldPoint(3107, 3359, 2), "Pickup 2 Magic Whistles.", holyTableNapkin);

		goGetExcalibur = new ItemStep(this, "Go retrieve Excalibur from your bank. If you do not own Excalibur, you can retrieve it from the Lady of the Lake in Taverly for 500 coins.", twoMagicWhistles, excalibur);
		WorldPoint teleportLocationPoint = new WorldPoint(2742, 3236, 0);
		goToTeleportLocation1 = new DetailedQuestStep(this, teleportLocationPoint, "Go to the tower on Karamja near gold mine west of Brimhaven.", twoMagicWhistles, excalibur);
		blowWhistle1 = new ItemStep(this, "Blow the whistle once you are underneath of the tower.", highlightMagicWhistle1, excalibur);

		attackTitan = new NpcStep(this, NpcID.BLACK_KNIGHT_TITAN, "Kill the Black Knight Titan with Excalibur. (You only need to deal the killing blow with excalibur!)", twoMagicWhistles, excalibur);
		talkToFisherman = new NpcStep(this, NpcID.FISHERMAN_4065, new WorldPoint(2798, 4706, 0), "Talk to the fisherman by the river. After talking to him walk West to the castle.");
		talkToFisherman.addDialogStep("Any idea how to get into the castle?");
		pickupBell = new DetailedQuestStep(this, new WorldPoint(2762, 4694, 0), "Pickup the bell outside of the castle.");
		ringBell = new DetailedQuestStep(this, new WorldPoint(2762, 4694, 0), "Ring the grail bell directly north of the broken castle wall (Where you picked up the bell)", highlightGrailBell);
		ringBell.addIcon(ItemID.GRAIL_BELL);
		goUpStairsBrokenCastle = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2762, 4681, 0), "Go up the stairs inside of the castle.");
		talkToFisherKing = new NpcStep(this, NpcID.THE_FISHER_KING, "Talk to The Fisher King.");
		talkToFisherKing.addDialogStep("You don't look too well.");

		goToCamelot = new DetailedQuestStep(this, new WorldPoint(2758, 3486, 0), "Go back to Camelot.");
		talkToKingArthur2 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Return to Camelot and talk to King Arthur.", emptyInvSpot);

		openSack = new ObjectStep(this, ObjectID.SACKS, new WorldPoint(2962, 3506, 0), "Travel to the Goblin Village North of Falador. Right click and open the sacks.", twoMagicWhistles);
		openSack.addDialogStep("Come with me, I shall make you a king.");

		goToTeleportLocation2 = new DetailedQuestStep(this, teleportLocationPoint, "Go to the tower on Karamja near gold mine west of Brimhaven.", oneMagicWhistle, goldFeather);
		blowWhistle2 = new ItemStep(this, "Blow the whistle once you are underneath of the tower.", highlightMagicWhistle2, goldFeather);

		openFisherKingCastleDoor = new ObjectStep(this, ObjectID.LARGE_DOOR_1524, "Open the door to the castle and enter.", goldFeather);
		goUpNewCastleStairs = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2649, 4684, 0), "Go up the stairs to the east.", goldFeather);
		goUpNewCastleLadder = new ObjectStep(this, ObjectID.LADDER_16683, "Climb the ladder on the second floor.", goldFeather);
		takeGrail = new DetailedQuestStep(this, new WorldPoint(2649, 4684, 2), "Pickup the Grail.", goldFeather);

		talkToKingArthur3 = new NpcStep(this, NpcID.KING_ARTHUR, kingArthurWorldPoint, "Return to Camelot and talk to King Arthur", grail);
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Black Knight Titan (level 120)");
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(excalibur);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(threeCamelotTele);
		reqs.add(ardyTele);
		reqs.add(faladorTele);
		reqs.add(sixtyCoins);
		reqs.add(antipoison);
		reqs.add(food);
		reqs.add(combatGear);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting Off", new ArrayList<>(Arrays.asList(talkToKingArthur1, goUpStairsCamelot, openMerlinDoor, talkToMerlin))));
		allSteps.add(new PanelDetails("Getting the Napkin", new ArrayList<>(Arrays.asList(goToEntrana, talkToHighPriest, goToGalahad, talkToGalahad))));
		allSteps.add(new PanelDetails("Getting the Magic Whistles", new ArrayList<>(Arrays.asList(goToDraynorManor, enterDraynorManor, goUpStairsDraynor1, goUpStairsDraynor2, openWhistleDoor, takeWhistles)), holyTableNapkin));
		allSteps.add(new PanelDetails("Fisher King Realm Pt.1", new ArrayList<>(Arrays.asList(goToTeleportLocation1, blowWhistle1, attackTitan, talkToFisherman, pickupBell, ringBell, goUpStairsBrokenCastle, talkToFisherKing)), twoMagicWhistles, excalibur));
		allSteps.add(new PanelDetails("Finding Percival", new ArrayList<>(Arrays.asList(talkToKingArthur2, openSack)), emptyInvSpot, twoMagicWhistles));
		allSteps.add(new PanelDetails("Fisher King Realm Pt.2", new ArrayList<>(Arrays.asList(goToTeleportLocation2, blowWhistle2, openFisherKingCastleDoor, goUpNewCastleStairs, goUpNewCastleLadder, takeGrail)), oneMagicWhistle, goldFeather));
		allSteps.add(new PanelDetails("Finishing Up", new ArrayList<>(Collections.singletonList(talkToKingArthur3)), grail));

		return allSteps;
	}
}
