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

import com.questhelper.ItemCollections;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.helpers.quests.secretsofthenorth.SolveChestCode;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class SucellusSteps extends ConditionalStep
{
	DetailedQuestStep moveToWeissCave, enterWeissCave, enterPrison, getKnockedOut, inspectWall,
		searchSkeleton, readPrisonersLetter, searchBucket, giveKnifeToSoldier, searchBed,
		giveSoldierLockpick, talkToAssassin, lockpickGate, searchAltar, readWarningLetter, unlockChest;
	QuestStep unlockChestStep;

	ItemRequirement meleeCombatGear, food, prayerPotions, icyBasalt, staminaPotions;
	ItemRequirement prisonersLetter, lockpick, chisel, knife, warningLetter;

	Requirement inWeissCave, inMahjarratCave, inPrisonF0, inPrisonF1, inPrisonF2, inPrisonCell,
		haveReadPrisonersLetter, givenKnifeToSoldier, talkedToSoldier, foundLockpick, escapedCell,
		assassinGone, pickedDoor, haveReadWarningLetter, inChestInterface;

	Zone weissCave, mahjarratCave, prisonF0, prisonF1, prisonF2, prisonCell;

	public SucellusSteps(QuestHelper questHelper, QuestStep defaultStep)
	{
		super(questHelper, defaultStep);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

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
		icyBasalt = new ItemRequirement("Icy basalt", ItemID.ICY_BASALT);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		// Quest items
		prisonersLetter = new ItemRequirement("Prisoner's letter", ItemID.PRISONERS_LETTER);
		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK_28415);
		lockpick.setTooltip("You can obtain another from the bed in the cell you started in");
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL_28414);
		knife = new ItemRequirement("Knife", ItemID.KNIFE_28413);

		warningLetter = new ItemRequirement("Warning letter", ItemID.WARNING_LETTER);
	}

	protected void setupZones()
	{
		weissCave = new Zone(new WorldPoint(2825, 10355,  0), new WorldPoint(2860, 10323, 0));
		mahjarratCave = new Zone(new WorldPoint(2890, 10290, 0), new WorldPoint(2936, 10360, 0));
		prisonF0 = new Zone(new WorldPoint(2872, 6334, 0), new WorldPoint(3009, 6463, 0));
		prisonF1 = new Zone(new WorldPoint(2872, 6334, 1), new WorldPoint(3009, 6463, 1));
		prisonF2 = new Zone(new WorldPoint(2872, 6334, 2), new WorldPoint(3009, 6463, 2));
		prisonCell = new Zone(new WorldPoint(3035, 6364, 0), new WorldPoint(3044, 6373, 0));
	}

	protected void setupConditions()
	{
		inWeissCave = new ZoneRequirement(weissCave);
		inMahjarratCave = new ZoneRequirement(mahjarratCave);
		inPrisonF0 = new ZoneRequirement(prisonF0);
		inPrisonF1 = new ZoneRequirement(prisonF1);
		inPrisonF2 = new ZoneRequirement(prisonF2);
		inPrisonCell = new ZoneRequirement(prisonCell);

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

		talkedToSoldier = new VarbitRequirement(15127, 10, Operation.GREATER_EQUAL);
		haveReadPrisonersLetter = new VarbitRequirement(15127, 12, Operation.GREATER_EQUAL);
		givenKnifeToSoldier = new VarbitRequirement(15127, 14, Operation.GREATER_EQUAL);
		// Recieved chisel, 14->16
		foundLockpick = new VarbitRequirement(15127, 18, Operation.GREATER_EQUAL);

		escapedCell = new VarbitRequirement(15127, 20, Operation.GREATER_EQUAL);

		assassinGone = new VarbitRequirement(15127, 22, Operation.GREATER_EQUAL);

		// Re-enter prison:
		// 14283 0->4
		pickedDoor = new VarbitRequirement(15127, 24, Operation.GREATER_EQUAL);
//		haveReadWarningLetter = new VarbitRequirement(15127, 26, Operation.GREATER_EQUAL);
		inChestInterface = new WidgetTextRequirement(809, 5, 9, "Confirm");
	}

	protected void setupSteps()
	{
		moveToWeissCave = new ObjectStep(getQuestHelper(), ObjectID.STAIRS_33234, new WorldPoint(2867, 3940, 0),
			"Enter the basalt cave in Weiss.", meleeCombatGear, food, prayerPotions);
		moveToWeissCave.addRecommended(staminaPotions);
		moveToWeissCave.addTeleport(icyBasalt);
		enterWeissCave = new ObjectStep(getQuestHelper(), NullObjectID.NULL_46905, new WorldPoint(2846, 10332, 0),
			"Enter the cave to the south.");
		enterWeissCave.addDialogStep("Yes.");

		enterPrison = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49513, new WorldPoint(2927, 10354, 0),
			"Enter the heavy door in the north-eastern room.");

		getKnockedOut = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2912, 6348, 2), "Move deeper into the prison.");
		inspectWall = new ObjectStep(getQuestHelper(), ObjectID.WALL_49099, new WorldPoint(3040, 6370, 0),
			"Inspect the wall to the north in the prison cell.");
		searchSkeleton = new ObjectStep(getQuestHelper(), ObjectID.SKELETON_49094, new WorldPoint(3042, 6369, 0),
			"Search the skeleton in the cell.");

		readPrisonersLetter = new DetailedQuestStep(getQuestHelper(), "Read the prisoner's letter.", prisonersLetter.highlighted());
		searchBucket = new ObjectStep(getQuestHelper(), ObjectID.BUCKET_49095, new WorldPoint(3039, 6367, 0),
			"Search the bucket for a knife.");
		giveKnifeToSoldier = new ObjectStep(getQuestHelper(), ObjectID.WALL_49099, new WorldPoint(3040, 6370, 0),
			"Give the knife to the soldier.");
		searchBed = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49514, new WorldPoint(3041, 6367, 0),
			"Search the bed.");
		giveSoldierLockpick = new ObjectStep(getQuestHelper(), ObjectID.WALL_49099, new WorldPoint(3040, 6370, 0),
		"Give the lockpick to the soldier..");

		talkToAssassin = new NpcStep(getQuestHelper(), NpcID.ASSASSIN_12348, new WorldPoint(2920, 6375, 2),
			"Talk to the Assassin outside the cell.");

		lockpickGate = new ObjectStep(getQuestHelper(), ObjectID.GATE_49120, new WorldPoint(2945, 6389, 2),
			"Head north from the assassin to the junction, and then east. Lockpick the gate you end up at.", lockpick);
		lockpickGate.setLinePoints(Arrays.asList(
			new WorldPoint(2919, 6362, 2),
			new WorldPoint(2919, 6388, 2),
			new WorldPoint(2913, 6388, 2),
			new WorldPoint(2913, 6395, 2),
			new WorldPoint(2932, 6395, 2),
			new WorldPoint(2932, 6389, 2),
			new WorldPoint(2945, 6389, 2)
		));

		searchAltar = new ObjectStep(getQuestHelper(), ObjectID.ALTAR_49150, new WorldPoint(2975, 6347, 2),
			"Head south from the gate, and all the way around until you enter the large area. Search the altar in the south room.");
		searchAltar.setLinePoints(Arrays.asList(
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
		unlockChest = new ObjectStep(getQuestHelper(), ObjectID.CHEST_49111, new WorldPoint(2980, 6346, 2),
			"Head south from the gate, and all the way around until you enter the large area. Unlock the chest east of the altar in the south room. The code is '214013'.");
		unlockChest.setLinePoints(Arrays.asList(
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
		unlockChestStep = new ChestCodeStep(getQuestHelper());
		unlockChest.addSubSteps(unlockChestStep);
	}


	protected List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(moveToWeissCave, enterWeissCave, enterPrison, getKnockedOut, inspectWall,
			searchSkeleton, readPrisonersLetter, searchBucket, giveKnifeToSoldier, searchBed, giveSoldierLockpick,
			talkToAssassin, lockpickGate, unlockChest);
	}
}
