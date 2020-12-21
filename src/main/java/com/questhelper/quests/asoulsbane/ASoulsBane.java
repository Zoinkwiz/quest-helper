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
package com.questhelper.quests.asoulsbane;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
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
	quest = QuestHelperQuest.A_SOULS_BANE
)
public class ASoulsBane extends BasicQuestHelper
{
	ItemRequirement rope, combatGear, angerSword, angerSpear, angerMace, angerBattleaxe;

	ConditionForStep ropeUsed, inAngerRoom, hasWeapon, hasSword, hasSpear, hasMace, hasBattleaxe, watchedTolnaLeavingCutscene, inHole0, inHole1,
		inHole2, inHole3, inHole4, inHole5, inFearRoom, reaperNearby, inConfusionRoom, inHopelessRoom, inHopeRoom, inTolnaRoom;

	DetailedQuestStep talkToLauna, useRopeOnRift, enterRift, takeWeapon, killAnimals, killBears, killRats, killUnicorn, killGoblins, leaveAngerRoom,
		lookInsideHole0, lookInsideHole1, lookInsideHole2, lookInsideHole3, lookInsideHole4, lookInsideHole5, killReaper, leaveFearRoom, killRealConfusionBeast,
		leaveConfusionRoom, leaveHopelessRoom, talkToTolna, talkToTolnaAgain;

	NpcStep killHopelessCreatures, killHeads;

	Zone rageRoom, fearRoom, confusionRoom, hopelessRoom, hopeRoom, tolnaRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToLauna);

		ConditionalStep firstRoomSteps = new ConditionalStep(this, useRopeOnRift);
		firstRoomSteps.addStep(hasSpear, killBears);
		firstRoomSteps.addStep(hasBattleaxe, killGoblins);
		firstRoomSteps.addStep(hasSword, killUnicorn);
		firstRoomSteps.addStep(hasMace, killRats);
		firstRoomSteps.addStep(hasWeapon, killAnimals);
		firstRoomSteps.addStep(inAngerRoom, takeWeapon);
		firstRoomSteps.addStep(ropeUsed, enterRift);

		ConditionalStep secondRoomSteps = new ConditionalStep(this, enterRift);
		secondRoomSteps.addStep(new Conditions(inFearRoom, reaperNearby), killReaper);
		secondRoomSteps.addStep(new Conditions(inFearRoom, inHole0), lookInsideHole0);
		secondRoomSteps.addStep(new Conditions(inFearRoom, inHole1), lookInsideHole1);
		secondRoomSteps.addStep(new Conditions(inFearRoom, inHole2), lookInsideHole2);
		secondRoomSteps.addStep(new Conditions(inFearRoom, inHole3), lookInsideHole3);
		secondRoomSteps.addStep(new Conditions(inFearRoom, inHole4), lookInsideHole4);
		secondRoomSteps.addStep(new Conditions(inFearRoom, inHole5), lookInsideHole5);
		secondRoomSteps.addStep(inAngerRoom, leaveAngerRoom);

		ConditionalStep thirdRoomSteps = new ConditionalStep(this, enterRift);
		thirdRoomSteps.addStep(inConfusionRoom, killRealConfusionBeast);
		thirdRoomSteps.addStep(inFearRoom, leaveFearRoom);

		ConditionalStep fourthRoomSteps = new ConditionalStep(this, enterRift);
		fourthRoomSteps.addStep(inHopelessRoom, killHopelessCreatures);
		fourthRoomSteps.addStep(inConfusionRoom, leaveConfusionRoom);

		ConditionalStep tolnaRoomSteps = new ConditionalStep(this, enterRift);
		tolnaRoomSteps.addStep(inTolnaRoom, killHeads);
		tolnaRoomSteps.addStep(inHopeRoom, leaveHopelessRoom);

		ConditionalStep tolnaSaveSteps = new ConditionalStep(this, enterRift);
		tolnaSaveSteps.addStep(inTolnaRoom, talkToTolna);

		steps.put(1, firstRoomSteps);
		steps.put(2, firstRoomSteps);
		steps.put(3, secondRoomSteps);
		steps.put(4, secondRoomSteps);
		steps.put(5, thirdRoomSteps);
		steps.put(6, thirdRoomSteps);
		steps.put(7, fourthRoomSteps);
		steps.put(8, fourthRoomSteps);
		steps.put(9, tolnaRoomSteps);
		steps.put(10, tolnaRoomSteps);
		steps.put(11, tolnaSaveSteps);
		steps.put(12, talkToTolnaAgain);

		return steps;
	}

	public void setupItemRequirements()
	{
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		rope.setHighlightInInventory(true);
		combatGear = new ItemRequirement("Combat gear + food", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		angerBattleaxe = new ItemRequirement("Anger battleaxe", ItemID.ANGER_BATTLEAXE);
		angerMace = new ItemRequirement("Anger mace", ItemID.ANGER_MACE);
		angerSpear = new ItemRequirement("Anger spear", ItemID.ANGER_SPEAR);
		angerSword = new ItemRequirement("Anger sword", ItemID.ANGER_SWORD);
	}

	public void setupConditions()
	{
		ropeUsed = new VarbitCondition(2032, 1);
		hasWeapon = new ItemRequirementCondition(LogicType.OR, angerBattleaxe, angerMace, angerSpear, angerSword);
		hasSword = new VarbitCondition(2029, 1);
		hasSpear = new VarbitCondition(2029, 2);
		hasMace = new VarbitCondition(2029, 3);
		hasBattleaxe = new VarbitCondition(2029, 4);

		inAngerRoom = new ZoneCondition(rageRoom);
		inFearRoom = new ZoneCondition(fearRoom);
		inConfusionRoom = new ZoneCondition(confusionRoom);
		inHopelessRoom = new ZoneCondition(hopelessRoom);
		inHopeRoom = new ZoneCondition(hopeRoom);
		inTolnaRoom = new ZoneCondition(tolnaRoom);

		watchedTolnaLeavingCutscene = new VarbitCondition(2560, 1);

		inHole0 = new VarbitCondition(2012, 0);
		inHole1 = new VarbitCondition(2012, 1);
		inHole2 = new VarbitCondition(2012, 2);
		inHole3 = new VarbitCondition(2012, 3);
		inHole4 = new VarbitCondition(2012, 4);
		inHole5 = new VarbitCondition(2012, 5);

		reaperNearby = new VarbitCondition(2035, 1);
	}

	public void loadZones()
	{
		rageRoom = new Zone(new WorldPoint(3010, 5217, 0), new WorldPoint(3038, 5246, 0));
		fearRoom = new Zone(new WorldPoint(3044, 5218, 0), new WorldPoint(3071, 5247, 0));
		confusionRoom = new Zone(new WorldPoint(3043, 5185, 0), new WorldPoint(3071, 5213, 0));
		hopelessRoom = new Zone(new WorldPoint(3074, 5186, 0), new WorldPoint(3102, 5214, 0));
		hopeRoom = new Zone(new WorldPoint(3010, 5185, 0), new WorldPoint(3038, 5214, 0));
		tolnaRoom = new Zone(new WorldPoint(2967, 5201, 1), new WorldPoint(2993, 5225, 1));
	}

	public void setupSteps()
	{
		talkToLauna = new NpcStep(this, NpcID.LAUNA, new WorldPoint(3307, 3454, 0), "Talk to Launa east of Varrock.");
		talkToLauna.addDialogStep("Would you like me to go down to look for your husband and son?");

		useRopeOnRift = new ObjectStep(this, NullObjectID.NULL_13968, new WorldPoint(3310, 3452, 0), "Use a rope on the rift.", rope);
		useRopeOnRift.addIcon(ItemID.ROPE);

		enterRift = new ObjectStep(this, NullObjectID.NULL_13968, new WorldPoint(3310, 3452, 0), "Enter the rift.", combatGear);

		takeWeapon = new ObjectStep(this, NullObjectID.NULL_13993, new WorldPoint(3012, 5244, 0), "Take a weapon from the weapon rack. Kill the angry monsters with the appropriate weapon:");
		takeWeapon.setText(new ArrayList<>(Arrays.asList("Take a weapon from the weapon rack. Kill the angry monsters with the appropriate weapon:",
			"Sword - Unicorn",
			"Spear - Bear",
			"Mace - Rat",
			"Battleaxe - Goblin")));


		ArrayList<String> killText = new ArrayList<>(Arrays.asList("Kill animals with appropriate weapons until the Rage Metre fills.",
			"Sword - Unicorn",
			"Spear - Bear",
			"Mace - Rat",
			"Battleaxe - Goblin"));

		killAnimals = new DetailedQuestStep(this, "");
		killAnimals.setText(killText);

		killBears = new NpcStep(this, NpcID.ANGRY_BEAR, new WorldPoint(3027, 5232, 0),  "", true);
		killBears.setText(killText);

		killGoblins = new NpcStep(this, NpcID.ANGRY_GOBLIN, new WorldPoint(3027, 5232, 0),  "", true);
		killGoblins.setText(killText);

		killRats = new NpcStep(this, NpcID.ANGRY_GIANT_RAT, new WorldPoint(3027, 5232, 0),  "", true);
		killRats.setText(killText);

		killUnicorn = new NpcStep(this, NpcID.ANGRY_UNICORN, new WorldPoint(3027, 5232, 0),  "", true);
		killUnicorn.setText(killText);

		killAnimals.addSubSteps(killBears, killGoblins, killRats, killUnicorn);

		leaveAngerRoom = new ObjectStep(this, ObjectID.EXIT_13882, new WorldPoint(3038, 5229, 0), "Go to the next room.");

		lookInsideHole0 = new ObjectStep(this, ObjectID.DARK_HOLE_13891, new WorldPoint(3066, 5245, 0), "Look inside the Dark Holes to cause fear reapers to appear. Kill 5-6 of them.");
		lookInsideHole1 = new ObjectStep(this, ObjectID.DARK_HOLE_13892, new WorldPoint(3069, 5227, 0), "Look inside the Dark Holes to cause fear reapers to appear. Kill 5-6 of them.");
		lookInsideHole2 = new ObjectStep(this, ObjectID.DARK_HOLE_13893, new WorldPoint(3064, 5219, 0), "Look inside the Dark Holes to cause fear reapers to appear. Kill 5-6 of them.");
		lookInsideHole3 = new ObjectStep(this, ObjectID.DARK_HOLE_13894, new WorldPoint(3053, 5219, 0), "Look inside the Dark Holes to cause fear reapers to appear. Kill 5-6 of them.");
		lookInsideHole4 = new ObjectStep(this, ObjectID.DARK_HOLE_13895, new WorldPoint(3046, 5230, 0), "Look inside the Dark Holes to cause fear reapers to appear. Kill 5-6 of them.");
		lookInsideHole5 = new ObjectStep(this, ObjectID.DARK_HOLE_13896, new WorldPoint(3046, 5240, 0), "Look inside the Dark Holes to cause fear reapers to appear. Kill 5-6 of them.");

		killReaper = new NpcStep(this, NpcID.FEAR_REAPER, new WorldPoint(3058, 5230, 0), "Kill the Fear Reaper.");
		lookInsideHole0.addSubSteps(lookInsideHole1, lookInsideHole2, lookInsideHole3, lookInsideHole4, lookInsideHole5, killReaper);

		leaveFearRoom = new ObjectStep(this, NullObjectID.NULL_13898, new WorldPoint(3046, 5236, 0), "Continue to the next room.");

		killRealConfusionBeast = new NpcStep(this, NpcID.CONFUSION_BEAST, new WorldPoint(3055, 5199, 0),
			"Kill the marked confusion beasts. The others won't take damage.", true);

		leaveConfusionRoom = new ObjectStep(this, NullObjectID.NULL_13912, new WorldPoint(3051, 5200, 0), "Leave the room through the confusing door.");

		killHopelessCreatures = new NpcStep(this, NpcID.HOPELESS_CREATURE, new WorldPoint(3087, 5198, 0), "Kill each hopeless creature 3 times.", true);
		killHopelessCreatures.addAlternateNpcs(NpcID.HOPELESS_CREATURE_1073, NpcID.HOPELESS_CREATURE_1074);

		leaveHopelessRoom = new ObjectStep(this, ObjectID.EXIT_13933, new WorldPoint(3021, 5188, 0), "Continue through the exit of the room.");

		killHeads = new NpcStep(this, NpcID.TOLNA_1075, new WorldPoint(2984, 5212, 1), "Kill all three of Tolna's heads.", true);
		killHeads.addAlternateNpcs(NpcID.TOLNA_1076, NpcID.TOLNA_1077);

		talkToTolna = new NpcStep(this, NpcID.TOLNA, new WorldPoint(2984, 5212, 1), "Talk to Tolna.");

		talkToTolnaAgain = new NpcStep(this, NpcID.TOLNA_1058, new WorldPoint(3307, 3454, 0), "Talk to Tolna outside the rift.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(rope, combatGear));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToLauna, useRopeOnRift, enterRift)), rope, combatGear));

		allSteps.add(new PanelDetails("Anger room",
			new ArrayList<>(Arrays.asList(takeWeapon, killAnimals, leaveAngerRoom)), combatGear));

		allSteps.add(new PanelDetails("Fear room",
			new ArrayList<>(Arrays.asList(lookInsideHole0, leaveFearRoom)), combatGear));

		allSteps.add(new PanelDetails("Confusion room",
			new ArrayList<>(Arrays.asList(killRealConfusionBeast, leaveConfusionRoom)), combatGear));

		allSteps.add(new PanelDetails("Hopelessness room",
			new ArrayList<>(Arrays.asList(killHopelessCreatures, leaveHopelessRoom)), combatGear));

		allSteps.add(new PanelDetails("Save Tolna",
			new ArrayList<>(Arrays.asList(killHeads, talkToTolna, talkToTolnaAgain)), combatGear));

		return allSteps;
	}
}
