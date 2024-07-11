/*
 * Copyright (c) 2024, Zoinkwiz
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
package com.questhelper.helpers.quests.whileguthixsleeps;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.NpcFollowerStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class WhileGuthixSleeps extends BasicQuestHelper
{
	//Items Required
	ItemRequirement sapphireLantern, litSapphireLantern, airRunes, earthRunes, fireRunes, waterRunes, mindRunes, lawRunes,
		deathRunes, log, charcoal, papyrus, lanternLens, mortMyreFungus, unpoweredOrb, ringOfCharosA, coins, bronzeMedHelm,
		ironChainbody, chargeOrbSpell, meleeGear, rangedGear, logs, knife;


	// Items Recommended
	ItemRequirement antipoison;

	// Quest items
	ItemRequirement dirtyShirt, unconsciousBroav, broav, movariosNotesV1, movariosNotesV2, wastePaperBasket, rubyKey, movariosNotesV1InBank, movariosNotesV2InBank;

	Requirement isUpstairsNearThaerisk, assassinsNearby, paidLaunderer, talkedToLaunderer, trapSetUp, trapBaited, broavTrapped, broavNearby, isNearTable,
		hasBroav, inMovarioFirstRoom, inMovarioDoorRoom, inLibrary, isNextToSpiralStaircase, disarmedStaircase, inMovarioBaseF1, inMovarioBaseF2,
		hadRubyKey, searchedBedForTraps, pulledPaintingLever, inWeightRoom;

	Zone upstairsNearThaeriskZone, nearTable, movarioFirstRoom, movarioDoorRoom, library, nextToSpiralStaircase, movarioBaseF1, movarioBaseF2, weightRoom;

	DetailedQuestStep talkToIvy, questPlaceholder, goUpLadderNextToIvy, talkToThaerisk, killAssassins, talkToThaeriskAgain,
		talkToLaunderer, talkToHuntingExpert, setupTrap, useFungusOnTrap, waitForBroavToGetTrapped, retrieveBroav,
		returnBroavToHuntingExpert, useDirtyShirtOnBroav, dropBroav, goToBrokenTable, searchBrokenTable;

	DetailedQuestStep enterMovarioBase, climbDownMovarioFirstRoom, inspectDoor, useRuneOnDoor, enterDoorToLibrary, solveElectricityPuzzle, enterElectricDoor,
		searchStaircaseInLibrary, climbStaircaseInLibrary, searchDesk, pickupWasteBasket, searchWasteBasket, useKeyOnBookcase, climbUpHiddenStaircase, searchBed, goDownToF1MovarioBase,
		useKeyOnChest,
		searchChestForTraps, getNotesFromChest, readNotes1, readNotes2, goDownFromHiddenRoom, inspectPainting, crossOverBrokenWall;

	DetailedQuestStep goUpToThaeriskWithNotes, talkToThaeriskWithNotes, goUpToThaeriskWithoutNotes, talkToThaeriskWithoutNotes;

	WeightStep solveWeightPuzzle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToIvy);

		ConditionalStep goTalkToThaerisk = new ConditionalStep(this, goUpLadderNextToIvy);
		goTalkToThaerisk.addStep(assassinsNearby, killAssassins);
		goTalkToThaerisk.addStep(isUpstairsNearThaerisk, talkToThaerisk);
		steps.put(2, goTalkToThaerisk);

		ConditionalStep goTalkToThaeriskAgain = new ConditionalStep(this, goUpLadderNextToIvy);
		goTalkToThaeriskAgain.addStep(isUpstairsNearThaerisk, talkToThaeriskAgain);
		steps.put(3, goTalkToThaeriskAgain);

		ConditionalStep goGetBroav = new ConditionalStep(this, talkToLaunderer);
		goGetBroav.addStep(talkedToLaunderer, talkToHuntingExpert);
		steps.put(4, goGetBroav);

		ConditionalStep goTrapBroav = new ConditionalStep(this, setupTrap);
		goTrapBroav.addStep(unconsciousBroav.alsoCheckBank(questBank), returnBroavToHuntingExpert);
		goTrapBroav.addStep(broavTrapped, retrieveBroav);
		goTrapBroav.addStep(trapBaited, waitForBroavToGetTrapped);
		goTrapBroav.addStep(trapSetUp, useFungusOnTrap);
		steps.put(5, goTrapBroav);

		ConditionalStep goTrackMovario = new ConditionalStep(this, goTrapBroav);
		goTrackMovario.addStep(and(broavNearby, dirtyShirt.alsoCheckBank(questBank), isNearTable), useDirtyShirtOnBroav);
		goTrackMovario.addStep(and(hasBroav, dirtyShirt.alsoCheckBank(questBank), isNearTable), dropBroav);
		goTrackMovario.addStep(and(hasBroav, dirtyShirt.alsoCheckBank(questBank)), goToBrokenTable);
		goTrackMovario.addStep(hasBroav, talkToLaunderer);
		steps.put(6, goTrackMovario);
		steps.put(7, goTrackMovario);

		steps.put(8, searchBrokenTable);

		steps.put(9, enterMovarioBase);

		ConditionalStep goDoDoorPuzzle = new ConditionalStep(this, enterMovarioBase);
		goDoDoorPuzzle.addStep(inMovarioDoorRoom, inspectDoor);
		goDoDoorPuzzle.addStep(inMovarioFirstRoom, climbDownMovarioFirstRoom);
		steps.put(10, goDoDoorPuzzle);

		ConditionalStep solveDoor = new ConditionalStep(this, goDoDoorPuzzle);
		solveDoor.addStep(inMovarioDoorRoom, useRuneOnDoor);
		steps.put(11, solveDoor);

		ConditionalStep enterLibrary = new ConditionalStep(this, goDoDoorPuzzle);
		enterLibrary.addStep(inMovarioDoorRoom, enterDoorToLibrary);
		steps.put(12, enterLibrary);

		ConditionalStep doLibraryStuff = new ConditionalStep(this, enterLibrary);
		doLibraryStuff.addStep(inLibrary, solveElectricityPuzzle);
		steps.put(13, doLibraryStuff);

		ConditionalStep goUpToF1Movario = new ConditionalStep(this, enterLibrary);
		goUpToF1Movario.addStep(and(isNextToSpiralStaircase, disarmedStaircase), climbStaircaseInLibrary);
		goUpToF1Movario.addStep(isNextToSpiralStaircase, searchStaircaseInLibrary);
		goUpToF1Movario.addStep(inLibrary, enterElectricDoor);

		ConditionalStep goUpFromLibrary = new ConditionalStep(this, goUpToF1Movario);
		goUpFromLibrary.addStep(and(inMovarioBaseF1, movariosNotesV1, hadRubyKey), useKeyOnBookcase);
		goUpFromLibrary.addStep(and(inMovarioBaseF1, movariosNotesV1, wastePaperBasket), searchWasteBasket);
		goUpFromLibrary.addStep(and(inMovarioBaseF1, movariosNotesV1), pickupWasteBasket);
		goUpFromLibrary.addStep(inMovarioBaseF1, searchDesk);

		steps.put(14, goUpFromLibrary);
		steps.put(15, goUpFromLibrary);

		ConditionalStep goUpToF2Movario = new ConditionalStep(this, goUpToF1Movario);
		goUpToF2Movario.addStep(and(inMovarioBaseF1, hadRubyKey), climbUpHiddenStaircase);
		goUpToF2Movario.addStep(and(inMovarioBaseF1, wastePaperBasket), searchWasteBasket);
		goUpToF2Movario.addStep(and(inMovarioBaseF1), pickupWasteBasket);

		ConditionalStep goSearchBed = new ConditionalStep(this, goUpToF2Movario);
		goSearchBed.addStep(and(inMovarioBaseF2, hadRubyKey), searchBed);
		goSearchBed.addStep(and(inMovarioBaseF2), goDownToF1MovarioBase);

		steps.put(16, goSearchBed);
		steps.put(17, goSearchBed);

		ConditionalStep goUseKeyOnBedChest = new ConditionalStep(this, goUpToF2Movario);
		goUseKeyOnBedChest.addStep(and(inMovarioBaseF2, hadRubyKey), useKeyOnChest);
		goUseKeyOnBedChest.addStep(and(inMovarioBaseF2), goDownToF1MovarioBase);
		steps.put(18, goUseKeyOnBedChest);

		ConditionalStep goSearchChestForTraps = new ConditionalStep(this, goUpToF2Movario);
		goSearchChestForTraps.addStep(and(inMovarioBaseF2, searchedBedForTraps), getNotesFromChest);
		goSearchChestForTraps.addStep(and(inMovarioBaseF2), searchChestForTraps);
		goSearchChestForTraps.addStep(and(inMovarioBaseF2), goDownToF1MovarioBase);
		steps.put(19, goSearchChestForTraps);

		ConditionalStep goSearchChestForNotes2 = new ConditionalStep(this, goUpToF2Movario);
		goSearchChestForNotes2.addStep(and(inMovarioBaseF2), getNotesFromChest);
		steps.put(20, goSearchChestForNotes2);

		ConditionalStep goToPainting = new ConditionalStep(this, goUpFromLibrary);
		goToPainting.addStep(and(isUpstairsNearThaerisk, movariosNotesV1InBank, movariosNotesV2InBank), talkToThaeriskWithNotes);
		goToPainting.addStep(and(inMovarioBaseF1, movariosNotesV1InBank, movariosNotesV2InBank), solveWeightPuzzle);
		goToPainting.addStep(and(movariosNotesV1InBank, movariosNotesV2InBank), goUpToThaeriskWithNotes);
		goToPainting.addStep(and(inMovarioBaseF1, movariosNotesV1InBank, movariosNotesV2InBank), inspectPainting);
		goToPainting.addStep(and(inMovarioBaseF1, movariosNotesV2InBank), searchDesk);
		goToPainting.addStep(and(inMovarioBaseF2, movariosNotesV2InBank), goDownToF1MovarioBase);
		goToPainting.addStep(and(inMovarioBaseF2), getNotesFromChest);
		goToPainting.addStep(and(inMovarioBaseF1), climbUpHiddenStaircase);
		steps.put(21, goToPainting);

		ConditionalStep goContinueThaeriskAfterNotes = new ConditionalStep(this, goUpToThaeriskWithoutNotes);
		goContinueThaeriskAfterNotes.addStep(isUpstairsNearThaerisk, talkToThaeriskWithoutNotes);
		steps.put(22, goContinueThaeriskAfterNotes);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		logs.setTooltip("You can take an axe from the stump west of the Hunting Expert's hut to cut a tree for a log");
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		knife.setTooltip("There is a knife spawn next to the Hunting Expert");

		sapphireLantern = new ItemRequirement("Emerald lantern", ItemID.SAPPHIRE_LANTERN_4701).isNotConsumed();
		litSapphireLantern = new ItemRequirement("Sapphire lantern", ItemID.SAPPHIRE_LANTERN_4702).isNotConsumed();
		litSapphireLantern.setTooltip("You can make this by using a cut sapphire on a bullseye lantern");

		airRunes = new ItemRequirement("Air rune", ItemID.AIR_RUNE);
		earthRunes = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE);
		fireRunes = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE);
		waterRunes = new ItemRequirement("Water rune", ItemID.WATER_RUNE);
		mindRunes = new ItemRequirement("Mind rune", ItemID.MIND_RUNE);
		lawRunes = new ItemRequirement("Law rune", ItemID.LAW_RUNE);
		deathRunes = new ItemRequirement("Death rune", ItemID.DEATH_RUNE);
		log = new ItemRequirement("Logs", ItemID.LOGS);
		charcoal = new ItemRequirement("Charcoal", ItemID.CHARCOAL);
		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		lanternLens = new ItemRequirement("Lantern lens", ItemID.LANTERN_LENS);
		mortMyreFungus = new ItemRequirement("Mort myre fungus", ItemID.MORT_MYRE_FUNGUS);
		unpoweredOrb = new ItemRequirement("Unpowered orb", ItemID.UNPOWERED_ORB);
		ringOfCharosA = new ItemRequirement("Ring of charos (a)", ItemID.RING_OF_CHAROSA);
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		bronzeMedHelm = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM);
		ironChainbody = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY);

		ItemRequirement cosmic3 = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE, 3);
		ItemRequirement fire30 = new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 63));
		ItemRequirement air30 = new ItemRequirement("Air runes", ItemID.AIR_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 66));
		ItemRequirement water30 = new ItemRequirement("Water runes", ItemID.WATER_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 56));
		ItemRequirement earth30 = new ItemRequirement("Earth runes", ItemID.EARTH_RUNE, 30)
			.showConditioned(new SkillRequirement(Skill.MAGIC, 60));
		ItemRequirements elemental30 = new ItemRequirements(LogicType.OR, "Elemental runes", air30, water30, earth30, fire30);
		elemental30.addAlternates(ItemID.FIRE_RUNE, ItemID.EARTH_RUNE, ItemID.AIR_RUNE);
		elemental30.setExclusiveToOneItemType(true);

		chargeOrbSpell = new ItemRequirements(LogicType.AND, "Runes for any charge orb spell you have the level to cast", cosmic3, elemental30);
		meleeGear = new ItemRequirement("Melee weapon", -1, -1).isNotConsumed();
		meleeGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		rangedGear = new ItemRequirement("Ranged weapon", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());

		// Recommended items
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);

		// Quest items
		dirtyShirt = new ItemRequirement("Dirty shirt", ItemID.DIRTY_SHIRT);
		dirtyShirt.setTooltip("You can get another from the Khazard Launderer west of the Fight Arena");

		unconsciousBroav = new ItemRequirement("Unconscious broav", ItemID.UNCONSCIOUS_BROAV);
		broav = new ItemRequirement("Broav", ItemID.BROAV);
		movariosNotesV1 = new ItemRequirement("Movario's notes (volume 1)", ItemID.MOVARIOS_NOTES_VOLUME_1);
		movariosNotesV2 = new ItemRequirement("Movario's notes (volume 2)", ItemID.MOVARIOS_NOTES_VOLUME_2);
		movariosNotesV1InBank = new ItemRequirement("Movario's notes (volume 1)", ItemID.MOVARIOS_NOTES_VOLUME_1).alsoCheckBank(questBank);
		movariosNotesV2InBank = new ItemRequirement("Movario's notes (volume 2)", ItemID.MOVARIOS_NOTES_VOLUME_2).alsoCheckBank(questBank);
		wastePaperBasket = new ItemRequirement("Waste-paper basket", ItemID.WASTEPAPER_BASKET);
		rubyKey = new ItemRequirement("Ruby key", ItemID.RUBY_KEY_29523);

		// Requirements
		upstairsNearThaeriskZone = new Zone(new WorldPoint(2898, 3448, 1), new WorldPoint(2917, 3452, 1));
		isUpstairsNearThaerisk = new ZoneRequirement(upstairsNearThaeriskZone);

		assassinsNearby = or(new NpcRequirement(NpcID.ASSASSIN_13514), new NpcRequirement(NpcID.ASSASSIN_13515));
		paidLaunderer = new VarbitRequirement(10756, 2, Operation.GREATER_EQUAL);
		talkedToLaunderer = new VarbitRequirement(10756, 4, Operation.GREATER_EQUAL);
		trapSetUp = new VarbitRequirement(10929, 1);
		trapBaited = new VarbitRequirement(10929, 2, Operation.GREATER_EQUAL);
		broavTrapped = new VarbitRequirement(10929, 4);
		broavNearby = new VarplayerRequirement(447, List.of(NpcID.BROAV, 13516), 16);
		hasBroav = or(broavNearby, broav.alsoCheckBank(questBank));

		nearTable = new Zone(new WorldPoint(2516, 3246, 0), new WorldPoint(2522, 3252, 0));
		isNearTable = new ZoneRequirement(nearTable);

		movarioFirstRoom = new Zone(new WorldPoint(4097, 4931, 0), new WorldPoint(4156, 4988, 0));
		inMovarioFirstRoom = new ZoneRequirement(movarioFirstRoom);

		movarioDoorRoom = new Zone(new WorldPoint(4207, 4973, 0), new WorldPoint(4214, 4986, 0));
		inMovarioDoorRoom = new ZoneRequirement(movarioDoorRoom);

		library = new Zone(new WorldPoint(4166, 4946, 0), new WorldPoint(4190, 4968, 0));
		inLibrary = new ZoneRequirement(library);

		nextToSpiralStaircase = new Zone(new WorldPoint(4180, 4949, 0), new WorldPoint(4183, 4952, 0));
		isNextToSpiralStaircase = new ZoneRequirement(nextToSpiralStaircase);

		disarmedStaircase = new VarbitRequirement(10799, 1);

		movarioBaseF1 = new Zone(new WorldPoint(4169, 4942, 1), new WorldPoint(4189, 4962, 1));
		inMovarioBaseF1 = new ZoneRequirement(movarioBaseF1);

		movarioBaseF2 = new Zone(new WorldPoint(4172, 4953, 2), new WorldPoint(4180, 4958, 2));
		inMovarioBaseF2 = new ZoneRequirement(movarioBaseF2);

		hadRubyKey = or(rubyKey, new VarbitRequirement(9653, 19, Operation.GREATER_EQUAL));
		searchedBedForTraps = new VarbitRequirement(10798, 1);

		pulledPaintingLever = new VarbitRequirement(10758, 1);

		weightRoom = new Zone(new WorldPoint(4177, 4944, 1), new WorldPoint(4181, 4947, 1));
		inWeightRoom = new ZoneRequirement(weightRoom);
	}

	public void setupSteps()
	{
		questPlaceholder = new DetailedQuestStep(this, "This is a very large quest, so it will be a while before a Quest Helper is made for it. Enjoy the quest if you do it yourself, it's one of the best ever made!");
		talkToIvy = new NpcStep(this, NpcID.IVY_SOPHISTA, new WorldPoint(2907, 3450, 0), "Talk to Ivy Sophista in Taverley south of the witch's house.");
		talkToIvy.addDialogStep("Yes.");

		goUpLadderNextToIvy = new ObjectStep(this, ObjectID.LADDER_53347, new WorldPoint(2915, 3450, 0), "Go up the ladder next to Ivy.");

		talkToThaerisk = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk.");
		// 15064 0->100

		// 10670 0->1

		// 10754 0->1 when someone walking downstairs
		killAssassins = new NpcStep(this, NpcID.ASSASSIN_13514, new WorldPoint(2909, 3449, 1), "Kill the assassins.", true);
		((NpcStep) killAssassins).addAlternateNpcs(NpcID.ASSASSIN_13515);

		talkToThaeriskAgain = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk again.");
		// 10755 0->1, thaerisk dialog checkpoint

		// 10809 1->0 after Thaerisk

		talkToLaunderer = new NpcStep(this, NpcID.KHAZARD_LAUNDERER, new WorldPoint(2564, 3172, 0), "Talk to the Khazard Launderer west of the Khazard Fight Arena.",
			coins.quantity(500).hideConditioned(paidLaunderer));
		talkToLaunderer.addDialogStep("Would 500 coins change your mind?");

		// 10756 0->1 Launderer dialog checkpoint
		// 1->2 for given money
		// 2->3 when received shirt
		// 3->4 told to go to hunter expert

		talkToHuntingExpert = new NpcStep(this, NpcID.HUNTING_EXPERT_1504, new WorldPoint(2525, 2916, 0),
			"Talk to the Hunting Expert in his hut in the middle of the Feldip Hills hunting area.", knife, logs);
		talkToHuntingExpert.addDialogStep("Do you think you could help me with broavs?");

		setupTrap = new ObjectStep(this, ObjectID.PIT_53273, new WorldPoint(2499, 2910, 0), "Set up the trap west of the Hunting Expert.", knife, logs, mortMyreFungus);
		useFungusOnTrap = new ObjectStep(this, ObjectID.PIT_TRAP, new WorldPoint(2499, 2910, 0), "Use the mort myre fungus on the trap west of the Hunting Expert.", mortMyreFungus);
		waitForBroavToGetTrapped = new ObjectStep(this, ObjectID.BAITED_PIT_TRAP, new WorldPoint(2499, 2910, 0), "Wait for the broav to fall into the trap.");
		retrieveBroav = new ObjectStep(this, ObjectID.COLLAPSED_TRAP_53269, new WorldPoint(2499, 2910, 0), "Dismantle the trap to retrieve the broav.");

		returnBroavToHuntingExpert = new NpcStep(this, NpcID.HUNTING_EXPERT_1504, new WorldPoint(2525, 2916, 0),
		"Bring the unconscious broav to the Feldip Hills hunting expert.", unconsciousBroav);
		returnBroavToHuntingExpert.addDialogStep("Do you think you could train this broav for me?");

		goToBrokenTable = new DetailedQuestStep(this, new WorldPoint(2519, 3248, 0), "Go to the broken table in the middle of the khazard side of the gnome/khazard battlefield.", broav);
		goToBrokenTable.setHighlightZone(nearTable);

		dropBroav = new DetailedQuestStep(this, "Drop your broav.", broav.highlighted());

		useDirtyShirtOnBroav = new NpcFollowerStep(this, NpcID.BROAV, "Use the dirty shirt on your broav.", dirtyShirt.highlighted());
		useDirtyShirtOnBroav.addIcon(ItemID.DIRTY_SHIRT);

		searchBrokenTable = new ObjectStep(this, NullObjectID.NULL_53889, new WorldPoint(2519, 3249, 0), "Search the broken table.");

		enterMovarioBase = new ObjectStep(this, ObjectID.TRAPDOOR_53279, new WorldPoint(2519, 3249, 0), "Enter movario's base under the broken table in the Khazard Battlefield.");

		// 4066 122878 -> 385022
		// 15064 0->100

		climbDownMovarioFirstRoom = new ObjectStep(this, ObjectID.STAIRS_53895, new WorldPoint(4117, 4973, 0), "Climb down the stairs to the north-west.");

		inspectDoor = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0), "Inspect the old battered door.");
		// 10757 0->1

		// TODO: Solve what rune to use
		useRuneOnDoor = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0),
			"Use the rune (either mind, earth, air, fire, or water) which is stylistically used in the 'Restricted access' warning on the door.");
		useRuneOnDoor.addIcon(ItemID.PURE_ESSENCE);

		// When used mind rune:
		// 10761 0->2
		// 10856 0->4
		// 9653 11->12
		// 10670 1->0

		enterDoorToLibrary = new ObjectStep(this, NullObjectID.NULL_54089, new WorldPoint(4210, 4974, 0), "Enter the old battered door.");
		// Entered library:
		// 12->13

		// ---
		// Flipping on entering
		// 10743 1->0->1
		// 10764 0->1->0
		// ---

		// 10936 0->23 weight on entering room
		// 10771 0->3

		// Pulled 4181, 4959, 0
		// 10762 0->1
		// 10763 1->0
		// 10769 0->1

		// Pulled 4187, 4962, 0
		// 10762 1->2
		// 10769 1->0
		// 10763 0->1

		// Pulled 4181, 4959, 0
		// 10762 2->3
		// 10763 1->0
		// 10768 0->1

		// Pulled 4175, 4964, 0
		// 10762 3->4
		// 10769 1->0
		// 10763 0->1

		// Pulled 4187, 4962, 0
		// 10762 5->6
		// 10769 1->0
		// 10797 0->1
		// 9653 13->14

		solveElectricityPuzzle = new DetailedQuestStep(this, "Solve the electricity puzzle.");

		enterElectricDoor = new ObjectStep(this, NullObjectID.NULL_54108, new WorldPoint(4181, 4953, 0), "Enter the gate to the staircase.");
		searchStaircaseInLibrary = new ObjectStep(this, ObjectID.SPIRAL_STAIRCASE, new WorldPoint(4182, 4951, 0), "Right-click SEARCH the spiral staircase.");
		climbStaircaseInLibrary = new ObjectStep(this, ObjectID.SPIRAL_STAIRCASE, new WorldPoint(4182, 4951, 0), "Climb up the spiral staircase.");

		// Gone up staircase
		// 9653 14->15
		// 10793-10796 0->100

		searchDesk = new ObjectStep(this, ObjectID.DESK_53940, new WorldPoint(4178, 4955, 1), "Search the desk north-west of the staircase.");

		pickupWasteBasket = new ObjectStep(this, ObjectID.WASTEPAPER_BASKET, new WorldPoint(4177, 4955, 1), "Pick-up the waste-paper basket west of the desk.");
		searchWasteBasket = new DetailedQuestStep(this, "Search the waste-paper basket.", wastePaperBasket.highlighted());
		useKeyOnBookcase = new ObjectStep(this, ObjectID.BOOKCASE_53939, new WorldPoint(4172, 4954, 1), "Use the ruby key on the most north-west bookcase.", rubyKey.highlighted());
		useKeyOnBookcase.addIcon(ItemID.RUBY_KEY_29523);
		climbUpHiddenStaircase = new ObjectStep(this, ObjectID.STAIRS_53947, new WorldPoint(4173, 4956, 1), "Go up the stairs that've appeared.");
		searchBed = new ObjectStep(this, ObjectID.BED_53949, new WorldPoint(4179, 4954, 2), "Search the bed.");
		goDownToF1MovarioBase = new ObjectStep(this, ObjectID.STAIRS_53948, new WorldPoint(4173, 4956, 2), "Go back downstairs.");
		searchDesk.addSubSteps(goDownToF1MovarioBase);

		// 10771 3/2/1 for chances before failing weight door
		useKeyOnChest = new ObjectStep(this, ObjectID.BED_CHEST, new WorldPoint(4179, 4954, 2), "Use the ruby key on the bed chest.", rubyKey.highlighted());
		useKeyOnChest.addIcon(ItemID.RUBY_KEY_29523);
		searchChestForTraps = new ObjectStep(this, ObjectID.BED_CHEST_53951, new WorldPoint(4179, 4954, 2), "RIGHT-CLICK search the bed chest for traps.");
		getNotesFromChest = new ObjectStep(this, ObjectID.BED_CHEST_53952, new WorldPoint(4179, 4954, 2), "Take the second pair of notes from the bed chest.");
		readNotes1 = new DetailedQuestStep(this, "Read the movario notes 1.", movariosNotesV1.highlighted());
		readNotes2 = new DetailedQuestStep(this, "Read the movario notes 2.", movariosNotesV2.highlighted());
		goDownFromHiddenRoom = new ObjectStep(this, ObjectID.STAIRS_53948, new WorldPoint(4173, 4956, 2), "Go back downstairs.");
		inspectPainting = new ObjectStep(this, ObjectID.PAINTING_53885, new WorldPoint(4179, 4948, 1), "Inspect the painting in the south of the room.");
		inspectPainting.addDialogStep("Pull the lever.");

		// 10670 0->1 when inspected painting

		// Pulled lever in painting
		// 10670 1->0
		// 10758 0->1
		crossOverBrokenWall = new ObjectStep(this, ObjectID.BROKEN_WALL_53884, new WorldPoint(4179, 4947, 1),
			"Cross over the broken wall into the south room.");
		solveWeightPuzzle = new WeightStep(this);

		// Taken fire runes
		// 10794 100->0

		// Death runes
		// 10795 100->0

		// Coal
		// 10793

		// Magic log
		// 107096

		goUpToThaeriskWithNotes = new ObjectStep(this, ObjectID.LADDER_53347, new WorldPoint(2915, 3450, 0), "Return to Thaerisk upstairs in Taverley with the notes.", movariosNotesV1, movariosNotesV2);
		talkToThaeriskWithNotes = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk.", movariosNotesV1, movariosNotesV2);

		goUpToThaeriskWithoutNotes = new ObjectStep(this, ObjectID.LADDER_53347, new WorldPoint(2915, 3450, 0), "Return to Thaerisk upstairs in Taverley");
		talkToThaeriskWithoutNotes = new NpcStep(this, NpcID.THAERISK, new WorldPoint(2909, 3449, 1), "Talk to Thaerisk.");
		goUpToThaeriskWithNotes.addSubSteps(talkToThaeriskWithNotes, goUpToThaeriskWithoutNotes, talkToThaeriskWithoutNotes);

		// 9653 22->23
		// 10773 0->1
		// 10775 0->1
		// Now go to Idria near Seers' Village
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", List.of(talkToIvy, goUpLadderNextToIvy, talkToThaerisk, killAssassins, talkToThaeriskAgain), List.of(meleeGear), List.of(antipoison)));
		allSteps.add(new PanelDetails("Investigating", List.of(talkToLaunderer, talkToHuntingExpert, setupTrap, useFungusOnTrap, waitForBroavToGetTrapped, retrieveBroav, returnBroavToHuntingExpert,
			goToBrokenTable, dropBroav, useDirtyShirtOnBroav, searchBrokenTable),
			List.of(coins.quantity(500).hideConditioned(paidLaunderer), knife, logs),
			List.of()));
		allSteps.add(new PanelDetails("Movario's Base", List.of(enterMovarioBase, climbDownMovarioFirstRoom, inspectDoor, useRuneOnDoor, enterDoorToLibrary, solveElectricityPuzzle, enterElectricDoor,
			searchStaircaseInLibrary, climbStaircaseInLibrary, searchDesk, pickupWasteBasket, searchWasteBasket, useKeyOnBookcase, climbUpHiddenStaircase, searchBed, useKeyOnChest, searchChestForTraps,
			getNotesFromChest, readNotes1, readNotes2, goDownFromHiddenRoom, inspectPainting, crossOverBrokenWall),
			airRunes, waterRunes, earthRunes, fireRunes, mindRunes));
		allSteps.add(new PanelDetails("Weight puzzle", solveWeightPuzzle.getDisplaySteps()));
		allSteps.add(new PanelDetails("???", List.of(goUpToThaeriskWithNotes)));
		return allSteps;
	}
}
