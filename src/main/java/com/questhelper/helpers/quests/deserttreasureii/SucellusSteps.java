/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.secretsofthenorth.ArrowChestPuzzleStep;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.Arrays;
import java.util.List;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;

public class SucellusSteps extends ConditionalStep
{
	QuestStep moveToWeissCave, enterWeissCave, enterPrison, getKnockedOut, inspectWall,
		searchSkeleton, readPrisonersLetter, searchBucket, giveKnifeToSoldier, searchBed,
		giveSoldierLockpick, talkToAssassin, lockpickGate, searchAltar, readWarningLetter, unlockChest,
		goToAdminRoom, enterCrevice, openArrowChest, enterCreviceToStart, openDiamondChest, getGear,
		talkToAssassinWithGear, goLightFirecrackers, goLightFirecrackersThroughCrevice, survive3Mins,
		talkToAssassinAfterJhallanFight, enterDukeArena, enterDukeBossArea, defeatDuke, defeatDukeSidebar,
		pickUpOddKey, enterRoomWestOfDuke, retrieveKeyFromDoor, talkToAssassinAfterDuke, openDukeChest,
		returnToDesertWithSucellusMedallion, useSucellusMedallionOnStatue;
	QuestStep unlockChestStep, unlockChest2, openArrowChestStep, openDiamondChestStep;

	ItemRequirement meleeCombatGear, food, prayerPotions, icyBasalt, staminaPotions, nardahTeleport;
	ItemRequirement prisonersLetter, lockpick, chisel, knife, warningLetter, sapphireKey, emeraldKey, rubyKey, diamondKey,
		oddKey, sucellusMedallion;

	Requirement inWeissCave, inMahjarratCave, inPrisonF2, inPrisonCell,
		haveReadPrisonersLetter, givenKnifeToSoldier, talkedToSoldier, foundLockpick, escapedCell,
		assassinGone, pickedDoor, inChestInterface, atLiesChest, inPrisonNW, inArrowPuzzle,
		atDiamondChest, inPrisonNE, gotGear, unlockedSECrevice, talkedToAssassinWithGear, inJhallanFight,
		defeatedJhallan, dukeArenaUnlocked, inDukeEntrance, inDukeBossArena, killedDuke, oddKeyNearby,
		oddKeyDoorUnlocked, talkedToAssassinAfterDuke, inVault;

	Zone weissCave, mahjarratCave, prisonF0, prisonF1, prisonF2, prisonCell, liesChest, prisonNW,
		prisonNW2, diamondChest, prisonNE, dukeBossArena, dukeEntrance, vault;

	public SucellusSteps(QuestHelper questHelper, QuestStep defaultStep)
	{
		super(questHelper, defaultStep);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		addStep(and(inVault, sucellusMedallion), useSucellusMedallionOnStatue);
		addStep(sucellusMedallion, returnToDesertWithSucellusMedallion);
		addStep(and(talkedToAssassinAfterDuke, or(inDukeBossArena, inDukeEntrance)), openDukeChest);
		addStep(and(oddKeyDoorUnlocked, or(inDukeBossArena, inDukeEntrance)), talkToAssassinAfterDuke);
		addStep(and(oddKey, or(inDukeBossArena, inDukeEntrance)), enterRoomWestOfDuke);
		addStep(and(inDukeEntrance, killedDuke), retrieveKeyFromDoor);
		addStep(and(oddKeyNearby), pickUpOddKey);
		addStep(and(inDukeBossArena), defeatDuke);
		addStep(and(inDukeEntrance), enterDukeBossArea);
		addStep(and(inPrisonF2, dukeArenaUnlocked), enterDukeArena);
		addStep(and(inPrisonF2, defeatedJhallan), talkToAssassinAfterJhallanFight);
		addStep(and(inPrisonF2, inJhallanFight), survive3Mins);
		addStep(and(inPrisonF2, talkedToAssassinWithGear, unlockedSECrevice), goLightFirecrackersThroughCrevice);
		addStep(and(inPrisonF2, talkedToAssassinWithGear, unlockedSECrevice), goLightFirecrackersThroughCrevice);
		addStep(and(inPrisonF2, talkedToAssassinWithGear), goLightFirecrackers);
		addStep(and(inPrisonF2, gotGear), talkToAssassinWithGear);
		addStep(and(inPrisonF2, diamondKey), getGear);
		addStep(and(inPrisonNE, rubyKey), enterCreviceToStart);
		addStep(and(atDiamondChest, rubyKey, inChestInterface), openDiamondChestStep);
		addStep(and(inPrisonF2, rubyKey), openDiamondChest);
		addStep(and(inPrisonF2, inArrowPuzzle), openArrowChestStep);
		addStep(and(inPrisonNW, sapphireKey, emeraldKey), enterCrevice);
		addStep(and(inPrisonF2, sapphireKey, emeraldKey), openArrowChest);
		addStep(and(inPrisonF2, atLiesChest, inChestInterface), unlockChest2);
		addStep(and(inPrisonF2, sapphireKey), goToAdminRoom);
		addStep(and(inPrisonF2, pickedDoor, inChestInterface), unlockChestStep);
		addStep(and(inPrisonF2, pickedDoor), unlockChest);
//		addStep(and(inPrisonF2, pickedDoor), readWarningLetter);
//		addStep(and(inPrisonF2, pickedDoor), searchAltar);
		addStep(and(inPrisonF2, assassinGone), lockpickGate);
		addStep(and(inPrisonF2, escapedCell), talkToAssassin);
		addStep(and(inPrisonCell, lockpick), giveSoldierLockpick);
		addStep(and(inPrisonCell, or(chisel, foundLockpick)), searchBed);
		addStep(and(inPrisonCell, or(knife, givenKnifeToSoldier)), giveKnifeToSoldier);
		addStep(and(inPrisonCell, haveReadPrisonersLetter), searchBucket);
		addStep(and(inPrisonCell, prisonersLetter), readPrisonersLetter);
		addStep(and(inPrisonCell, talkedToSoldier), searchSkeleton);
		addStep(inPrisonCell, inspectWall);
		addStep(inPrisonF2, getKnockedOut);
		addStep(inMahjarratCave, enterPrison);
		addStep(inWeissCave, enterWeissCave);
		addStep(null, moveToWeissCave);
	}

	protected void setupItemRequirements()
	{
		meleeCombatGear = new ItemRequirement("Melee combat gear", -1, -1);
		meleeCombatGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		food = new ItemRequirement("Food, as much as you can bring", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);
		icyBasalt = new ItemRequirement("Icy basalt", ItemID.WEISS_TELEPORT_BASALT);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(VarbitID.BOOKOFSCROLLS_NARDAH, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_ELITE);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_HARD, ItemID.TELEPORTSCROLL_NARDAH, ItemID.DESERT_AMULET_MEDIUM);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		// Quest items
		prisonersLetter = new ItemRequirement("Prisoner's letter", ItemID.DT2_GHORROCK_CELL_NOTE);
		lockpick = new ItemRequirement("Lockpick", ItemID.DT2_GHORROCK_LOCKPICK);
		lockpick.setTooltip("You can obtain another from the bed in the cell you started in");
		chisel = new ItemRequirement("Chisel", ItemID.DT2_GHORROCK_CELL_CHISEL);
		knife = new ItemRequirement("Knife", ItemID.DT2_GHORROCK_CELL_KNIFE);

		warningLetter = new ItemRequirement("Warning letter", ItemID.DT2_GHORROCK_HINT_NOTE);

		sapphireKey = new ItemRequirement("Sapphire key", ItemID.DT2_GHORROCK_SAPPHIRE_KEY);
		emeraldKey = new ItemRequirement("Emerald key", ItemID.DT2_GHORROCK_EMERALD_KEY);
		rubyKey = new ItemRequirement("Ruby key", ItemID.DT2_GHORROCK_RUBY_KEY);
		diamondKey = new ItemRequirement("Diamond key", ItemID.DT2_GHORROCK_DIAMOND_KEY);
		oddKey = new ItemRequirement("Odd key", ItemID.DT2_DUKE_MEDALLION_KEY);
		sucellusMedallion = new ItemRequirement("Sucellus medallion", ItemID.DT2_MEDALLION_SUCELLUS);
	}

	protected void setupZones()
	{
		weissCave = new Zone(new WorldPoint(2825, 10355,  0), new WorldPoint(2860, 10323, 0));
		mahjarratCave = new Zone(new WorldPoint(2890, 10290, 0), new WorldPoint(2936, 10360, 0));
		prisonF0 = new Zone(new WorldPoint(2872, 6334, 0), new WorldPoint(3009, 6463, 0));
		prisonF1 = new Zone(new WorldPoint(2872, 6334, 1), new WorldPoint(3009, 6463, 1));
		prisonF2 = new Zone(new WorldPoint(2872, 6334, 2), new WorldPoint(3009, 6463, 2));
		prisonCell = new Zone(new WorldPoint(3035, 6364, 0), new WorldPoint(3044, 6373, 0));
		liesChest = new Zone(new WorldPoint(2903, 6445, 2));
		prisonNW = new Zone(new WorldPoint(2889, 6398, 2), new WorldPoint(2948, 6457, 2));
		prisonNW2 = new Zone(new WorldPoint(2949, 6420, 2), new WorldPoint(2961, 6450, 2));
		diamondChest = new Zone(new WorldPoint(2889, 6375, 2));
		prisonNE = new Zone(new WorldPoint(2947, 6345, 2), new WorldPoint(3005, 6446, 2));
		dukeBossArena = new Zone(new WorldPoint(3025, 6433, 0), new WorldPoint(3053, 6461, 0));
		dukeEntrance = new Zone(new WorldPoint(3010, 6414, 0), new WorldPoint(3069, 6432, 0));
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
	}

	protected void setupConditions()
	{
		inWeissCave = new ZoneRequirement(weissCave);
		inMahjarratCave = new ZoneRequirement(mahjarratCave);
		inDukeEntrance = new ZoneRequirement(dukeEntrance);
		inDukeBossArena = new ZoneRequirement(dukeBossArena);
		inPrisonF2 = new ZoneRequirement(prisonF2);
		inPrisonCell = new ZoneRequirement(prisonCell);
		inPrisonNW = new ZoneRequirement(prisonNW, prisonNW2);
		inPrisonNE = new ZoneRequirement(prisonNE);
		inVault = new ZoneRequirement(vault);

		// Can enter prison:
		// 15127 2->4

		// Entered prison: 15127 4->6
		// 14862 66->68
		// 3575 36413184 -> 36429568:
		// 	10001010111001111100000000
		// 	10001010111101111100000000

		// Teled to prison:
		// 15127 6->8

		// Removing items bits:
		// Varplayer 46 2->1
		// 14283 0->4
		// varplayer 456 0->-1 (disease value)

		// Tick 5 logged in:
		// 12164 0->1
		// 933 0->1
		// 13989 0->1

		talkedToSoldier = new VarbitRequirement(VarbitID.DT2_GHORROCK, 10, Operation.GREATER_EQUAL);
		haveReadPrisonersLetter = new VarbitRequirement(VarbitID.DT2_GHORROCK, 12, Operation.GREATER_EQUAL);
		givenKnifeToSoldier = new VarbitRequirement(VarbitID.DT2_GHORROCK, 14, Operation.GREATER_EQUAL);
		// Recieved chisel, 14->16
		foundLockpick = new VarbitRequirement(VarbitID.DT2_GHORROCK, 18, Operation.GREATER_EQUAL);

		escapedCell = new VarbitRequirement(VarbitID.DT2_GHORROCK, 20, Operation.GREATER_EQUAL);

		assassinGone = new VarbitRequirement(VarbitID.DT2_GHORROCK, 22, Operation.GREATER_EQUAL);

		// Re-enter prison:
		// 14283 0->4
		pickedDoor = new VarbitRequirement(VarbitID.DT2_GHORROCK, 24, Operation.GREATER_EQUAL);
		inChestInterface = new WidgetTextRequirement(809, 5, 9, "Confirm");


		// Opened chest
		// 15127 24->26

		// Cleared crevice near altar
		// 15177 0->1

		// Gone through north door
		// 26->28

		atLiesChest = new ZoneRequirement(liesChest);
		// Unlocked NW chest
		// 28->30

		// Surprise Jhallan attack:
		// 15127 30->32

		// Cleared crevice north-side
		// 15178 0->1

		// Entered beds room
		// 15127 32->34
		inArrowPuzzle = new WidgetTextRequirement(810, 15, 9, "Confirm");

		// Opened ruby key chest
		// 15127 34->36

		// Entered ruby room, 36->38??? (Maybe wrong)
		atDiamondChest = new ZoneRequirement(diamondChest);

		// Obtained diamond key
		// 15127 38->40

		// Talked to assassin, 40->46
		// NOTE: Implies missed steps

		// Entered gear chest room (I think?)
		// 46->48
		// Got gear
		// 48->50

		gotGear = new VarbitRequirement(VarbitID.DT2_GHORROCK, 50, Operation.GREATER_EQUAL);
		talkedToAssassinWithGear = new VarbitRequirement(VarbitID.DT2_GHORROCK, 52, Operation.GREATER_EQUAL);

		unlockedSECrevice = new VarbitRequirement(VarbitID.DT2_GHORROCK_SHORTCUT_1, 1);

		inJhallanFight = new VarbitRequirement(VarbitID.DT2_GHORROCK, 54, Operation.GREATER_EQUAL);
		defeatedJhallan = new VarbitRequirement(VarbitID.DT2_GHORROCK, 56, Operation.GREATER_EQUAL);
		dukeArenaUnlocked = new VarbitRequirement(VarbitID.DT2_GHORROCK, 58, Operation.GREATER_EQUAL);
		// Entered succulus area
		// 15127 58->60

		// In boss arena:
		// 15127 60->62
		// 14862 68->70

		killedDuke = new VarbitRequirement(VarbitID.DT2_GHORROCK, 64, Operation.GREATER_EQUAL);
		// Global state 70->72
		oddKeyNearby = new ItemOnTileRequirement(oddKey);

		oddKeyDoorUnlocked = new VarbitRequirement(VarbitID.DT2_GHORROCK_ASYLUM_GATE, 1);

		talkedToAssassinAfterDuke = new VarbitRequirement(VarbitID.DT2_GHORROCK, 66, Operation.GREATER_EQUAL);

		// Told guards no luck finding assassin, 15129 0->1

		// Attacked by assassin:
		// 15176 0->1
		// 14862 74->76
		// 15176 1->2
		// 14192 0->7

		// Re-enter house to assassin:
		// 15161 1->0
		// 14192 7->0->7

		// Placed medallion
		// 15127 68->70
		// 14862 76->78
		// 15134 0->3
		// 15135 0->2
	}

	protected void setupSteps()
	{
		moveToWeissCave = new ObjectStep(getQuestHelper(), ObjectID.MY2ARM_THRONEROOM_STAIRSDOWN, new WorldPoint(2867, 3940, 0),
			"Enter the basalt cave in Weiss.", meleeCombatGear, food, prayerPotions);
		((ObjectStep) moveToWeissCave).addRecommended(staminaPotions);
		((ObjectStep) moveToWeissCave).addTeleport(icyBasalt);
		enterWeissCave = new ObjectStep(getQuestHelper(), ObjectID.MY2ARM_MINE_WALL_CAVE_EXIT_02, new WorldPoint(2846, 10332, 0),
			"Enter the cave to the south.");
		enterWeissCave.addDialogStep("Yes.");

		enterPrison = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_ENTRY, new WorldPoint(2927, 10354, 0),
			"Enter the heavy door in the north-eastern room.");

		getKnockedOut = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2912, 6348, 2), "Move deeper into the prison.");
		inspectWall = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_CELL_HOLE, new WorldPoint(3040, 6370, 0),
			"Inspect the wall to the north in the prison cell."),
			"Work out how to escape the cell.");
		searchSkeleton = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_CELL_SKELETON, new WorldPoint(3042, 6369, 0),
			"Search the skeleton in the cell."),
			"Work out how to escape the cell.").withNoHelpHiddenInSidebar(true);

		readPrisonersLetter = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Read the prisoner's letter.", prisonersLetter.highlighted()),
				"Work out how to escape the cell.").withNoHelpHiddenInSidebar(true);
		searchBucket = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_CELL_BUCKET, new WorldPoint(3039, 6367, 0),
			"Search the bucket for a knife."),
			"Work out how to escape the cell.").withNoHelpHiddenInSidebar(true);
		giveKnifeToSoldier = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_CELL_HOLE, new WorldPoint(3040, 6370, 0),
			"Give the knife to the soldier."),
			"Work out how to escape the cell.").withNoHelpHiddenInSidebar(true);
		searchBed = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_CELL_BED, new WorldPoint(3041, 6367, 0),
			"Search the bed."),
			"Work out how to escape the cell.").withNoHelpHiddenInSidebar(true);
		giveSoldierLockpick = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_CELL_HOLE, new WorldPoint(3040, 6370, 0),
		"Give the lockpick to the soldier."),
			"Work out how to escape the cell.").withNoHelpHiddenInSidebar(true);

		talkToAssassin = new NpcStep(getQuestHelper(), NpcID.DT2_ASSASSIN_GHORROCK_INSTANCE, new WorldPoint(2920, 6375, 2),
			"Talk to the Assassin outside the cell.");
		lockpickGate = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_PADLOCK_GATE_CLOSED, new WorldPoint(2945, 6389, 2),
			"Head north from the assassin to the junction, and then east. Lockpick the gate you end up at.", lockpick);
		((ObjectStep) lockpickGate).setLinePoints(Arrays.asList(
			new WorldPoint(2919, 6362, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2913, 6388, 2),
			new WorldPoint(2913, 6395, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2945, 6389, 2)
		));

		searchAltar = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_ALTAR, new WorldPoint(2975, 6347, 2),
			"Head south from the gate, and all the way around until you enter the large area. Search the altar in the south room.");
		((ObjectStep) searchAltar).setLinePoints(Arrays.asList(
			new WorldPoint(2919, 6362, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2913, 6388, 2),
			new WorldPoint(2913, 6395, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2945, 6389, 2),

			new WorldPoint(2950, 6389, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(3001, 6382, 2),
			new WorldPoint(3001, 6356, 2),
			new WorldPoint(2993, 6356, 2),
			new WorldPoint(2993, 6365, 2),
			new WorldPoint(2979, 6365, 2),
			new WorldPoint(2979, 6360, 2),
			new WorldPoint(2975, 6360, 2),
			new WorldPoint(2975, 6348, 2)
		));

		readWarningLetter = new DetailedQuestStep(getQuestHelper(), "Read the warning letter.", warningLetter.highlighted());
		ObjectStep unlockChestRealStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_SAPPHIRE_CHEST, new WorldPoint(2980, 6346, 2),
			"Head south from the gate, and all the way around until you enter the large area. Unlock the chest east of the altar in the south room. The code is '214013'.");
		unlockChestRealStep.setLinePoints(Arrays.asList(
			new WorldPoint(2919, 6362, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2913, 6388, 2),
			new WorldPoint(2913, 6395, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2945, 6389, 2),

			new WorldPoint(2950, 6389, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(3001, 6382, 2),
			new WorldPoint(3001, 6356, 2),
			new WorldPoint(2993, 6356, 2),
			new WorldPoint(2993, 6365, 2),
			new WorldPoint(2979, 6365, 2),
			new WorldPoint(2979, 6360, 2),
			new WorldPoint(2975, 6360, 2),
			new WorldPoint(2975, 6348, 2)
		));
		ObjectStep unlockChestFakeStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_SAPPHIRE_CHEST, new WorldPoint(2980, 6346, 2),
			"Head south from the gate, and all the way around until you enter the large area. Work out how to unlock the chest east of the altar in the south room.");
		unlockChestFakeStep.setLinePoints(Arrays.asList(
			new WorldPoint(2919, 6362, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2913, 6388, 2),
			new WorldPoint(2913, 6395, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2945, 6389, 2),

			new WorldPoint(2950, 6389, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(3001, 6382, 2),
			new WorldPoint(3001, 6356, 2),
			new WorldPoint(2993, 6356, 2),
			new WorldPoint(2993, 6365, 2),
			new WorldPoint(2979, 6365, 2),
			new WorldPoint(2979, 6360, 2),
			new WorldPoint(2975, 6360, 2),
			new WorldPoint(2975, 6348, 2)
		));
		unlockChestStep = new PuzzleWrapperStep(getQuestHelper(), new ChestCodeStep(getQuestHelper(), "214013", 5, 2, 1, 4, 0, 1, 3),
			"Enter the correct code for the chest.");

		unlockChest = new PuzzleWrapperStep(getQuestHelper(), unlockChestRealStep, unlockChestFakeStep).withNoHelpHiddenInSidebar(true);
		unlockChest.addSubSteps(unlockChestStep);

		ObjectStep goToAdminRoomRealStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_EMERALD_CHEST, new WorldPoint(2902, 6445, 2),
			"Go to the most north-western room. Open the chest closest to the room's west door. The code for it is 'LIES'.");
		goToAdminRoomRealStep.addDialogStep("Attempt to open the chest.");
		goToAdminRoomRealStep.setLinePoints(Arrays.asList(
			new WorldPoint(2975, 6361, 2),
			new WorldPoint(2971, 6361, 2),
			new WorldPoint(2971, 6369, 2),
			new WorldPoint(2975, 6369, 2),
			new WorldPoint(2975, 6382, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(2950, 6389, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2912, 6395, 2),
			new WorldPoint(2912, 6423, 2),
			new WorldPoint(2923, 6423, 2),
			new WorldPoint(2923, 6454, 2),
			new WorldPoint(2916, 6454, 2),
			new WorldPoint(2916, 6444, 2),
			new WorldPoint(2907, 6444, 2),

			// From main area
			new WorldPoint(0, 0, 0),
			new WorldPoint(2914, 6348, 2),
			new WorldPoint(2919, 6348, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2912, 6388, 2),
			new WorldPoint(2912, 6395, 2)
		));
		DetailedQuestStep goToAdminRoomHiddenStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_EMERALD_CHEST, new WorldPoint(2910, 6445, 2),
			"Go to the most north-western room. Solve the chest puzzle in there. " +
				"If you want help with this, enable 'Show Puzzle Solutions' in the Quest Helper configuration settings.", true);
		goToAdminRoomHiddenStep.setLinePoints(Arrays.asList(
			new WorldPoint(2975, 6531, 2),
			new WorldPoint(2975, 6361, 2),
			new WorldPoint(2971, 6361, 2),
			new WorldPoint(2971, 6369, 2),
			new WorldPoint(2975, 6369, 2),
			new WorldPoint(2975, 6382, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(2950, 6389, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2912, 6395, 2),
			new WorldPoint(2912, 6423, 2),
			new WorldPoint(2923, 6423, 2),
			new WorldPoint(2923, 6454, 2),
			new WorldPoint(2916, 6454, 2),
			new WorldPoint(2916, 6444, 2),
			new WorldPoint(2907, 6444, 2),

			// From main area
			new WorldPoint(0, 0, 0),
			new WorldPoint(2914, 6348, 2),
			new WorldPoint(2919, 6348, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2912, 6388, 2),
			new WorldPoint(2912, 6395, 2)
		));
		goToAdminRoom = new PuzzleWrapperStep(getQuestHelper(), goToAdminRoomRealStep, goToAdminRoomHiddenStep);
		unlockChest2 = new PuzzleWrapperStep(getQuestHelper(), new ChestCodeStep(getQuestHelper(), "LIES", 10, 0, 4, 1, 5),
			"Enter the correct code for the chest.").withNoHelpHiddenInSidebar(true);
		goToAdminRoom.addSubSteps(unlockChest2);

		enterCrevice = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_SHORTCUT_2_ENTRY, new WorldPoint(2960, 6428, 2),
			"When you're not in a room, Jhallan will now chase you down. Use Protect from Magic to reduce his damage.");
		enterCrevice.addText("Run east into the room with a crevice. Clear it, and go through it.");
		((ObjectStep) enterCrevice).setLinePoints(Arrays.asList(
			new WorldPoint(2907, 6444, 2),
			new WorldPoint(2916, 6444, 2),
			new WorldPoint(2916, 6454, 2),
			new WorldPoint(2923, 6454, 2),
			new WorldPoint(2923, 6442, 2),
			new WorldPoint(2951, 6442, 2),
			new WorldPoint(2951, 6428, 2),
			new WorldPoint(2959, 6428, 2)
		));
		enterCrevice.addDialogStep("Yes.");

		ArrowChestPuzzleStep arrowChestPuzzleStep = new ArrowChestPuzzleStep(getQuestHelper());
		arrowChestPuzzleStep.setSolution(0, 1, 3, 2, 1, 0);
		openArrowChestStep = new PuzzleWrapperStep(getQuestHelper(), arrowChestPuzzleStep, "Work out how to open the nearby locked chest.").withNoHelpHiddenInSidebar(true);
		openArrowChest = new PuzzleWrapperStep(getQuestHelper(), new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_RUBY_CHEST, new WorldPoint(2968, 6414, 2),
			"Enter the room with beds in it, south of the crevice. " +
				"Open the south-eastern chest, next to the crates. The code is 'UP RIGHT LEFT DOWN RIGHT UP'."),
			"Enter the room with beds in it, south of the crevice. Work out how to open the south-eastern chest, next to the crates.");
		openArrowChest.addSubSteps(openArrowChestStep);

		enterCreviceToStart = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_SHORTCUT_3_EXIT, new WorldPoint(2984, 6399, 2),
			"Enter the crevice to the south to shortcut back to the main room.");
		((ObjectStep) enterCreviceToStart).setLinePoints(Arrays.asList(
			new WorldPoint(2974, 6418, 2),
			new WorldPoint(2984, 6418, 2),
			new WorldPoint(2984, 6400, 2)
		));
		ObjectStep openDiamondChestRealStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_DIAMOND_CHEST, new WorldPoint(2890, 6375, 2),
			"Open the chest in the cell to the west of the main room, through the Ruby Gate. The code for it is 'WRATH'.");
		openDiamondChestRealStep.setLinePoints(Arrays.asList(
			new WorldPoint(2901, 6361, 2),
			new WorldPoint(2905, 6361, 2),
			new WorldPoint(2905, 6354, 2),
			new WorldPoint(2891, 6354, 2),
			new WorldPoint(2891, 6366, 2),
			new WorldPoint(2884, 6366, 2),
			new WorldPoint(2884, 6374, 2),
			new WorldPoint(2889, 6375, 2)
		));
		ObjectStep openDiamondChestFakeStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_DIAMOND_CHEST, new WorldPoint(2890, 6375, 2),
			"Work out how to open the chest in the cell to the west of the main room, through the Ruby Gate.");
		openDiamondChestFakeStep.setLinePoints(Arrays.asList(
			new WorldPoint(2901, 6361, 2),
			new WorldPoint(2905, 6361, 2),
			new WorldPoint(2905, 6354, 2),
			new WorldPoint(2891, 6354, 2),
			new WorldPoint(2891, 6366, 2),
			new WorldPoint(2884, 6366, 2),
			new WorldPoint(2884, 6374, 2),
			new WorldPoint(2889, 6375, 2)
		));
		openDiamondChestStep = new PuzzleWrapperStep(getQuestHelper(), new ChestCodeStep(getQuestHelper(), "WRATH", 10, 2, 3, 5, 9, 5),
			"Enter the correct code for the chest.").withNoHelpHiddenInSidebar(true);

		openDiamondChest = new PuzzleWrapperStep(getQuestHelper(), openDiamondChestRealStep, openDiamondChestFakeStep);
		openDiamondChest.addSubSteps(openDiamondChestStep);

		// Door to gear: ObjectID.DT2_GHORROCK_DIAMOND_GATE_CLOSED, 2903, 6375, 2
		getGear = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_EQUIPMENT_CHEST, new WorldPoint(2899, 6376, 2),
			"Get your equipment back from the chest in the cell next to the Assassin. You'll need to go through some dialog with him to enter.");
		talkToAssassinWithGear = new NpcStep(getQuestHelper(), NpcID.DT2_ASSASSIN_GHORROCK_INSTANCE, new WorldPoint(2903, 6383, 2),
			"Talk to the assassin.");
		goLightFirecrackers = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_FIRECRACKER_PACK, new WorldPoint(2972, 6367, 2),
			"Get a full inventory of food, and return to the refugee camp in the south-eastern room. " +
				"Light the firecrackers there. Once you do, you'll need to survive Jhallan attacking you for 2 minutes.");
		((ObjectStep) goLightFirecrackers).setLinePoints(Arrays.asList(
			new WorldPoint(2905, 6375, 2),
			new WorldPoint(2905, 6348, 2),
			new WorldPoint(2919, 6348, 2),
			new WorldPoint(2919, 6354, 2),
			new WorldPoint(2933, 6367, 2),
			new WorldPoint(2938, 6367, 2),
			new WorldPoint(2938, 6389, 2),
			new WorldPoint(2950, 6389, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(2975, 6382, 2),
			new WorldPoint(2975, 6369, 2),
			new WorldPoint(2972, 6368, 2)
		));
		goLightFirecrackers.addDialogStep("Yes.");

		goLightFirecrackersThroughCrevice = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_FIRECRACKER_PACK, new WorldPoint(2972, 6367, 2),
			"Get a full inventory of food, and return to the refugee camp in the south-eastern room. " +
				"Light the firecrackers there. Once you do, you'll need to survive Jhallan attacking you for 2 minutes.");
		((ObjectStep) goLightFirecrackersThroughCrevice).setLinePoints(Arrays.asList(
			new WorldPoint(2905, 6375, 2),
			new WorldPoint(2905, 6348, 2),
			new WorldPoint(2919, 6348, 2),
			new WorldPoint(2919, 6354, 2),
			new WorldPoint(2954, 6354, 2),
			new WorldPoint(2954, 6365, 2),
			new WorldPoint(2971, 6365, 2),
			new WorldPoint(2971, 6367, 2)
		));
		goLightFirecrackersThroughCrevice.addDialogStep("Yes.");

		goLightFirecrackers.addSubSteps(goLightFirecrackersThroughCrevice);
		survive3Mins = new NpcStep(getQuestHelper(), NpcID.DT2_JHALLAN_CUTSCENE, new WorldPoint(2975, 6362, 2),
			"Survive Jhallan's attacks for 2 minutes. Protect from Mage, keep your health high, and avoid the shadow attacks.");
		((NpcStep) survive3Mins).addAlternateNpcs(NpcID.SOTN_JHALLAN_GHORROCK, NpcID.SOTN_JHALLAN_CUTSCENE, NpcID.DT2_JHALLAN_COMBAT, NpcID.DT2_JHALLAN_CHASE);

		talkToAssassinAfterJhallanFight = new NpcStep(getQuestHelper(), NpcID.DT2_ASSASSIN_GHORROCK_VIS, new WorldPoint(2978, 6371, 2),
			"Talk to the assassin after the fight.");

		enterDukeArena = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_ASYLUM_ENTRY, new WorldPoint(2974, 6440, 2),
			"Go down the stairs in the far north-east of the prison. Be prepared for fighting the boss of the area.",
			meleeCombatGear, food, prayerPotions);
		((ObjectStep) enterDukeArena).setLinePoints(Arrays.asList(
			new WorldPoint(2975, 6372, 2),
			new WorldPoint(2975, 6382, 2),
			new WorldPoint(2950, 6382, 2),
			new WorldPoint(2950, 6406, 2),
			new WorldPoint(2974, 6406, 2),
			new WorldPoint(2974, 6439, 2),
			null,
			new WorldPoint(2912, 6344, 2),
			new WorldPoint(2912, 6348, 2),
			new WorldPoint(2905, 6348, 2),
			new WorldPoint(2905, 6361, 2),
			new WorldPoint(2899, 6361, 2),
			null,
			new WorldPoint(2984, 6400, 2),
			new WorldPoint(2984, 6406, 2),
			new WorldPoint(2974, 6406, 2)
		));

		enterDukeBossArea = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_GATE_BOSS, new WorldPoint(3039, 6433, 0),
			"Enter the boss arena.");

		defeatDuke = new NpcStep(getQuestHelper(), NpcID.DUKE_SUCELLUS_ASLEEP_QUEST, new WorldPoint(3039, 6454, 0),
			"Defeat the Duke. Check the sidebar for more details.");
		((NpcStep) defeatDuke).addAlternateNpcs(NpcID.DUKE_SUCELLUS_ASLEEP, NpcID.DUKE_SUCELLUS_AWAKE, NpcID.DUKE_SUCELLUS_DEAD,
			NpcID.DUKE_SUCELLUS_INACTIVE_QUEST, NpcID.DUKE_SUCELLUS_ASLEEP_QUEST, NpcID.DUKE_SUCELLUS_AWAKE_QUEST, NpcID.DUKE_SUCELLUS_DEAD_QUEST);

		defeatDukeSidebar = new DetailedQuestStep(getQuestHelper(), "Defeat the Duke. You must first wake him with two 'Arder-musca poison'.");
		defeatDukeSidebar.addText("Make sure to avoid any of the enviromental effects, such as gas, lightning, and the Extremitys' vision.");
		defeatDukeSidebar.addText("To make the poison, get 3 Arder mushrooms from the north-eastern corner, and 3 Musca from the north-western corner.");
		defeatDukeSidebar.addText("Grab the pestle and mortar from the shelves west of the entrance, and a pickaxe from the east of it.");
		defeatDukeSidebar.addText("Use the pestle and mortar on the mushrooms, and then mine 12 salt from the salt deposits in the middle of the room.");
		defeatDukeSidebar.addText("With the ground mushrooms and salts, add them to the fermentation vats near the entrance. Empty them for 2 poisons.");
		defeatDukeSidebar.addText("Use both poisons on the boss to start the fight. Stay close to him where possible, and Protect from Melee. He will use the following attacks:");
		defeatDukeSidebar.addText("Blue orb: Magic attack.");
		defeatDukeSidebar.addText("Spikes: the Duke hits the floor, causing spikes to appear. Avoid them.");
		defeatDukeSidebar.addText("Gas vent: the Duke activates one of the gas vents. Keep away from it.");
		defeatDukeSidebar.addText("Duke's gaze: the duke opens his eye to deal massive damage. Hide behind a pillar to avoid this.");
		defeatDukeSidebar.addSubSteps(defeatDuke);

		pickUpOddKey = new ItemStep(getQuestHelper(), "Pick up the odd key.", oddKey);

		enterRoomWestOfDuke = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_MEDALLION_GATE, new WorldPoint(3023, 6425, 0),
			"Enter the room south-west of the Duke.");

		retrieveKeyFromDoor = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_GATE_BOSS, new WorldPoint(3039, 6433, 0),
			"Attempt to enter the Duke's room to retrieve the odd key.");
		pickUpOddKey.addSubSteps(retrieveKeyFromDoor);

		talkToAssassinAfterDuke = new NpcStep(getQuestHelper(), NpcID.DT2_ASSASSIN_GHORROCK_VIS, new WorldPoint(3017, 6427, 0),
			"Talk to the assassin in the room.");
		openDukeChest = new ObjectStep(getQuestHelper(), ObjectID.DT2_GHORROCK_MEDALLION_CHEST, new WorldPoint(3021, 6420, 0),
			"Open the chest in the south-east of the room for sucellus' medallion.");

		returnToDesertWithSucellusMedallion = new ObjectStep(getQuestHelper(), ObjectID.DT2_DESERT_VAULT_DOOR,
			new WorldPoint(3511, 2971, 0),
			"Return to the Vault door north-east of Nardah. Be wary of an assassin coming to kill you! They can run, freeze, and teleblock you.",
			sucellusMedallion);
		((ObjectStep) returnToDesertWithSucellusMedallion).addTeleport(nardahTeleport);

		useSucellusMedallionOnStatue = new ObjectStep(getQuestHelper(), ObjectID.DT2_VAULT_SUCELLUS_STATUE, new WorldPoint(3932, 9626, 1),
			"Use the medallion on the south-west statue.", sucellusMedallion.highlighted());
		useSucellusMedallionOnStatue.addIcon(ItemID.DT2_MEDALLION_SUCELLUS);
		useSucellusMedallionOnStatue.addDialogStep("Yes.");
	}

	protected List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(moveToWeissCave, enterWeissCave, enterPrison, getKnockedOut, inspectWall,
			searchSkeleton, readPrisonersLetter, searchBucket, giveKnifeToSoldier, searchBed, giveSoldierLockpick,
			talkToAssassin, lockpickGate, unlockChest, goToAdminRoom, enterCrevice, openArrowChest, enterCreviceToStart,
			openDiamondChest, getGear, talkToAssassinWithGear, goLightFirecrackers, survive3Mins,
			talkToAssassinAfterJhallanFight, enterDukeArena, enterDukeBossArea, defeatDukeSidebar, pickUpOddKey, enterRoomWestOfDuke,
			talkToAssassinAfterDuke, openDukeChest, returnToDesertWithSucellusMedallion, useSucellusMedallionOnStatue);
	}
}
