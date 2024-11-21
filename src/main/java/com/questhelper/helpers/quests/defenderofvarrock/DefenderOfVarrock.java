/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.defenderofvarrock;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

public class DefenderOfVarrock extends BasicQuestHelper
{
	DetailedQuestStep talkToElias, inspectPlant, inspectRock, inspectPlant2, inspectBush1, inspectBush2, inspectBush3, inspectTrapdoor,
		listenToElias, lookOverBalcony;

	DetailedQuestStep pickupBottles, killZombies, collectRedMist, openDoorToArrav, pickupBottlesAgain, killZombiesAgain,
		collectRedMistAgain, goThroughSecondGate, lookOverSecondBalcony;

	DetailedQuestStep talkToEliasInPalace, goUpToRovin, goUpToRovin2, talkToRovin, enterCamdozaal, talkToRamarno, mineBarronite, killChaosGolems, useCoreOnDeposit, useBarroniteOnForge;

	DetailedQuestStep goToF1ForRovinNonInstance, goToF1ForRovin, goToF2ForRovin, talkToRovinAfterForge, goToF1ForReldo, goToF0ForReldo, talkToReldo, searchScrolls, readList, readCensus, talkToRoald,
		talkToAeonisig, talkToPrysin, talkToRomeo, talkToHorvik, talkToHalen, talkToDimintheis, goToF1ToFinish, goToF2ToFinish, finishQuest;

	DetailedQuestStep talkToRoaldOutsideInstance, talkToAeonisigOutsideInstance, talkToPrysinOutsideInstance, talkToRomeoFromInstance, talkToHorvikFromInstance,
		talkToHalenFromInstance, talkToDimintheisFromInstance;

	Zone castleF1, castleF2, camdozaal, castleF1Invasion, castleF2Invasion;

	Requirement eliasFollowing, inspectedPlant1, inspectedRock1, inspectedPlant2, inspectedBush1, inspectedBush2,
		inspectedBush3, inspectedTrapdoor, inDungeon, redMistNearby, inCastleF1, inCastleF2, inCamdozaal, inVarrockInvasion, inCastleF1Invasion,
		inCastleF2Invasion;

	Requirement talkedToRoald, talkedToAeonisig, talkedToPrysin, talkedToRomeo, talkedToHorvik, talkedToHalen, givenShield;

	ItemRequirement combatGear, bottle, bottleOfMist, varrockTeleport, mindAltarOrLassarTeleport, lumberyardTeleport, barroniteDeposit, chaosCore, imbuedBarronite, pickaxe, listOfElders, shieldOfArrav;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToElias);
		steps.put(2, talkToElias);

		ConditionalStep searchWithElias = new ConditionalStep(this, inspectTrapdoor);
		searchWithElias.addStep(nor(inspectedPlant1), inspectPlant);
		searchWithElias.addStep(nor(inspectedRock1), inspectRock);
		searchWithElias.addStep(nor(inspectedPlant2), inspectPlant2);
		searchWithElias.addStep(nor(inspectedBush1), inspectBush1);
		searchWithElias.addStep(nor(inspectedBush2), inspectBush2);
		searchWithElias.addStep(nor(inspectedBush3), inspectBush3);
		searchWithElias.addStep(nor(inspectedTrapdoor), inspectTrapdoor);

		ConditionalStep findBase = new ConditionalStep(this, talkToElias);
		findBase.addStep(eliasFollowing, searchWithElias);
		// 4 is ready to go
		steps.put(4, findBase);
		steps.put(6, findBase);
		steps.put(8, findBase);
		steps.put(10, findBase);
		steps.put(12, findBase);
		// Searched last plant, next stop trapdoor
		// 9664 0->1

		// Entered dungeon
		// 9658 1->0->2
		// 9655 12->14
		// VARP 4066 14->46
		ConditionalStep exploreBase = new ConditionalStep(this, inspectTrapdoor);
		exploreBase.addStep(inDungeon, listenToElias);
		steps.put(14, exploreBase);

		ConditionalStep goLookOverBalcony = new ConditionalStep(this, inspectTrapdoor);
		goLookOverBalcony.addStep(and(inDungeon, eliasFollowing), lookOverBalcony);
		goLookOverBalcony.addStep(inDungeon, listenToElias);
		steps.put(16, goLookOverBalcony);

		ConditionalStep goSeeArrav = new ConditionalStep(this, inspectTrapdoor);
		goSeeArrav.addStep(and(inDungeon, bottleOfMist.quantity(3)), openDoorToArrav);
		goSeeArrav.addStep(and(inDungeon, bottle, redMistNearby), collectRedMist);
		goSeeArrav.addStep(and(inDungeon, bottle.quantity(3)), killZombies);
		goSeeArrav.addStep(inDungeon, pickupBottles);
		steps.put(18, goSeeArrav);
		steps.put(20, goSeeArrav);

		ConditionalStep talkToArrav = new ConditionalStep(this, inspectTrapdoor);
		talkToArrav.addStep(inDungeon, openDoorToArrav);
		steps.put(22, talkToArrav);

		ConditionalStep goToNextSection = new ConditionalStep(this, inspectTrapdoor);
		goToNextSection.addStep(and(inDungeon, bottleOfMist.quantity(3)), goThroughSecondGate);
		goToNextSection.addStep(and(inDungeon, bottle, redMistNearby), collectRedMistAgain);
		goToNextSection.addStep(and(inDungeon, bottle.quantity(3)), killZombiesAgain);
		goToNextSection.addStep(inDungeon, pickupBottlesAgain);
		steps.put(24, goToNextSection);

		ConditionalStep goLookOverSecondBalcony = new ConditionalStep(this, inspectTrapdoor);
		goLookOverSecondBalcony.addStep(inDungeon, lookOverSecondBalcony);
		steps.put(26, goLookOverSecondBalcony);

		steps.put(28, talkToEliasInPalace);

		ConditionalStep goTalkToRovin = new ConditionalStep(this, goUpToRovin);
		goTalkToRovin.addStep(inCastleF2, talkToRovin);
		goTalkToRovin.addStep(inCastleF1, goUpToRovin2);
		steps.put(30, goTalkToRovin);

		ConditionalStep goTalkToRamarno = new ConditionalStep(this, enterCamdozaal);
		goTalkToRamarno.addStep(inCamdozaal, talkToRamarno);
		steps.put(32, goTalkToRamarno);

		ConditionalStep goUseForge = new ConditionalStep(this, enterCamdozaal);
		goUseForge.addStep(and(inCamdozaal, imbuedBarronite), useBarroniteOnForge);
		goUseForge.addStep(and(chaosCore, barroniteDeposit), useCoreOnDeposit);
		goUseForge.addStep(and(inCamdozaal, barroniteDeposit), killChaosGolems);
		goUseForge.addStep(inCamdozaal, mineBarronite);
		steps.put(34, goUseForge);

		ConditionalStep goReturnToRovin = new ConditionalStep(this, goToF1ForRovinNonInstance);
		goReturnToRovin.addStep(inCastleF2Invasion, talkToRovinAfterForge);
		goReturnToRovin.addStep(inCastleF1Invasion, goToF2ForRovin);
		goReturnToRovin.addStep(inVarrockInvasion, goToF1ForRovin);
		steps.put(36, goReturnToRovin);
		steps.put(38, goReturnToRovin);

		ConditionalStep goTalkToReldo = new ConditionalStep(this, talkToReldo);
		goTalkToReldo.addStep(inCastleF2, goToF1ForReldo);
		goTalkToReldo.addStep(inCastleF1, goToF0ForReldo);
		steps.put(40, goTalkToReldo);

		ConditionalStep goReadList = new ConditionalStep(this, searchScrolls);
		goReadList.addStep(listOfElders, readList);
		steps.put(42, goReadList);
		steps.put(44, readCensus);
		// Read census, 9668 0->1
		// 9667 = page of census

		ConditionalStep talkToCandidates = new ConditionalStep(this, talkToHalen);
		talkToCandidates.addStep(and(nor(talkedToRoald), inVarrockInvasion), talkToRoald);
		talkToCandidates.addStep(nor(talkedToRoald), talkToRoaldOutsideInstance);

		talkToCandidates.addStep(and(nor(talkedToAeonisig), inVarrockInvasion), talkToAeonisig);
		talkToCandidates.addStep(nor(talkedToAeonisig), talkToAeonisigOutsideInstance);

		talkToCandidates.addStep(and(nor(talkedToPrysin), inVarrockInvasion), talkToPrysin);
		talkToCandidates.addStep(nor(talkedToPrysin), talkToPrysinOutsideInstance);

		talkToCandidates.addStep(and(nor(talkedToRomeo), inVarrockInvasion), talkToRomeoFromInstance);
		talkToCandidates.addStep(nor(talkedToRomeo), talkToRomeo);

		talkToCandidates.addStep(and(nor(talkedToHorvik), inVarrockInvasion), talkToHorvikFromInstance);
		talkToCandidates.addStep(nor(talkedToHorvik), talkToHorvik);

		talkToCandidates.addStep(and(nor(talkedToHalen), inVarrockInvasion), talkToHalenFromInstance);
		steps.put(46, talkToCandidates);

		ConditionalStep goTalkToDim = new ConditionalStep(this, talkToDimintheis);
		goTalkToDim.addStep(inVarrockInvasion, talkToDimintheisFromInstance);
		steps.put(48, goTalkToDim);
		steps.put(50, goTalkToDim);

		ConditionalStep goFinishQuest = new ConditionalStep(this, goToF1ToFinish);
		goFinishQuest.addStep(inCastleF2, finishQuest);
		goFinishQuest.addStep(inCastleF1, goToF2ToFinish);
		steps.put(52, goFinishQuest);
		steps.put(54, goFinishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		combatGear = new ItemRequirement("Combat gear and food", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		bottle = new ItemRequirement("Bottle", ItemID.BOTTLE);
		bottle.addAlternates(ItemID.BOTTLE_OF_MIST);
		bottleOfMist = new ItemRequirement("Bottle of mist", ItemID.BOTTLE_OF_MIST);

		varrockTeleport = new TeleportItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		mindAltarOrLassarTeleport = new TeleportItemRequirement("Mind Altar or Lassar teleport tablet or spell", ItemID.MIND_ALTAR_TELEPORT);
		mindAltarOrLassarTeleport.addAlternates(ItemID.LASSAR_TELEPORT);
		lumberyardTeleport = new TeleportItemRequirement("Teleport to the Lumberyard", ItemID.LUMBERYARD_TELEPORT);
		lumberyardTeleport.addAlternates(ItemID.RING_OF_THE_ELEMENTS, ItemID.RING_OF_THE_ELEMENTS_26818);

		barroniteDeposit = new ItemRequirement("Barronite deposit", ItemID.BARRONITE_DEPOSIT);
		chaosCore = new ItemRequirement("Chaos core", ItemID.CHAOS_CORE);
		imbuedBarronite = new ItemRequirement("Imbued barronite", ItemID.IMBUED_BARRONITE);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		listOfElders = new ItemRequirement("List of elders", ItemID.LIST_OF_ELDERS);
		shieldOfArrav = new ItemRequirement("Shield of arrav", ItemID.SHIELD_OF_ARRAV);
		shieldOfArrav.setTooltip("You can get another from Rovin upstairs in Varrock Castle");
	}

	@Override
	protected void setupZones()
	{
		castleF1 = new Zone(new WorldPoint(3200, 3490, 1), new WorldPoint(3206, 3500, 1));
		castleF2 = new Zone(new WorldPoint(3200, 3494, 2), new WorldPoint(3206, 3500, 2));
		camdozaal = new Zone(new WorldPoint(2897, 5757, 0), new WorldPoint(3047, 5869, 0));

		castleF1Invasion = new Zone(new WorldPoint(2899, 4934, 1), new WorldPoint(3934, 4975, 1));
		castleF2Invasion = new Zone(new WorldPoint(2899, 4934, 2), new WorldPoint(3934, 4975, 2));
	}

	public void setupConditions()
	{
		// TODO: Reported value is 12754, but uncertain why this'd be the case. Perhaps to do with different version of him?
		eliasFollowing = new VarplayerRequirement(447, List.of(NpcID.ELIAS_WHITE, NpcID.ELIAS_WHITE_12611, 12754), 16);

		// 9655 4->6
		// 9659 0->1
		inspectedPlant1 = new VarbitRequirement(9659, 1);
		inspectedRock1 = new VarbitRequirement(9660, 1);
		inspectedPlant2 = new VarbitRequirement(9661, 1);
		inspectedBush1 = new VarbitRequirement(9662, 1);
		inspectedBush2 = new VarbitRequirement(9663, 1);
		inspectedBush3 = new VarbitRequirement(9664, 1);
		inspectedTrapdoor = new VarbitRequirement(9665, 1);
		inDungeon = new ZoneRequirement(new Zone(14151));

		redMistNearby = new ObjectCondition(NullObjectID.NULL_50690);

		inCastleF1 = new ZoneRequirement(castleF1);
		inCastleF2 = new ZoneRequirement(castleF2);
		inCamdozaal = new ZoneRequirement(camdozaal);
		inVarrockInvasion = new ZoneRequirement(new Zone(15693));
		inCastleF1Invasion = new ZoneRequirement(castleF1Invasion);
		inCastleF2Invasion = new ZoneRequirement(castleF2Invasion);

		talkedToRoald = new VarbitRequirement(9669, 1);
		talkedToAeonisig = new VarbitRequirement(9670, 1);
		talkedToPrysin = new VarbitRequirement(9671, 1);
		talkedToHorvik = new VarbitRequirement(9672, 1);
		talkedToRomeo = new VarbitRequirement(9673, 1);
		// NOTE: Missing 74/75?
		talkedToHalen = new VarbitRequirement(9676, 1);

		givenShield = new VarbitRequirement(9655, 50, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		talkToElias = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3283, 3501, 0),
			"Talk to Elias White in the Jolly Boar Inn north-east of Varrock.", combatGear);
		talkToElias.addDialogSteps("Yes.", "Ready when you are.");
		talkToElias.addTeleport(lumberyardTeleport);

		inspectPlant = new ObjectStep(this, NullObjectID.NULL_50662, new WorldPoint(3280, 3516, 0), "Inspect the plant north of the pub.");
		inspectRock = new ObjectStep(this, NullObjectID.NULL_50664, new WorldPoint(3260, 3514, 0), "Inspect the small rocks to the west.");
		inspectPlant2 = new ObjectStep(this, NullObjectID.NULL_50669, new WorldPoint(3269, 3480, 0), "Follow the path to the south and inspect the plant south west of the Jolly Boar Inn.");
		inspectBush1 = new ObjectStep(this, NullObjectID.NULL_50670, new WorldPoint(3293, 3463, 0), "Continue south-east of the Saradomin statue, and inspect the bush there.");
		inspectBush2 = new ObjectStep(this, NullObjectID.NULL_50667, new WorldPoint(3325, 3470, 0), "Search the bush next to the gate to the east, by the gate to Silvarea.");
		inspectBush3 = new ObjectStep(this, NullObjectID.NULL_50668, new WorldPoint(3349, 3495, 0), "Inspect the small bush further to the east.");
		inspectTrapdoor = new ObjectStep(this, NullObjectID.NULL_50689, new WorldPoint(3343, 3515, 0),
			"Enter the trapdoor to the north, near to the wilderness ditch.", combatGear);
		inspectTrapdoor.addDialogStep("Let's do it.");

		listenToElias = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3560, 4551, 0), "Talk to Elias in the dungeon.");
		lookOverBalcony = new ObjectStep(this, ObjectID.BALCONY, new WorldPoint(3564, 4569, 0), "Look over the balcony to the north.");
		pickupBottles = new ItemStep(this, new WorldPoint(3537, 4572, 0), "Pick up 3 bottles nearby.", bottle.quantity(3));
		killZombies = new NpcStep(this, NpcID.ARMOURED_ZOMBIE, "Kill armoured zombies and fill the 3 bottles with the clouds they leave behind.", true);
		((NpcStep) killZombies).addAlternateNpcs(NpcID.ARMOURED_ZOMBIE_12721, NpcID.ARMOURED_ZOMBIE_12722, NpcID.ARMOURED_ZOMBIE_12723, NpcID.ARMOURED_ZOMBIE_12724, NpcID.ARMOURED_ZOMBIE_12725,
			NpcID.ARMOURED_ZOMBIE_12726, NpcID.ARMOURED_ZOMBIE_12727, NpcID.ARMOURED_ZOMBIE_12728, NpcID.ARMOURED_ZOMBIE_12729, NpcID.ARMOURED_ZOMBIE_12730, NpcID.ARMOURED_ZOMBIE_12731);
		collectRedMist = new ObjectStep(this, NullObjectID.NULL_50690, "Collect the red mist in a bottle.");
		killZombies.addSubSteps(collectRedMist);

		openDoorToArrav = new ObjectStep(this, ObjectID.GATE_50149, new WorldPoint(3536, 4571, 0),
			"Go through the gate, and finish the cutscene with Arrav. If you skip it, try re-entering the gate.");

		pickupBottlesAgain = new ItemStep(this, new WorldPoint(3537, 4572, 0), "Pick up 3 bottles nearby.", bottle.quantity(3));
		killZombiesAgain = new NpcStep(this, NpcID.ARMOURED_ZOMBIE, "Kill armoured zombies and fill 3 the bottles with the clouds they leave behind.", true);
		((NpcStep) killZombiesAgain).addAlternateNpcs(NpcID.ARMOURED_ZOMBIE_12721, NpcID.ARMOURED_ZOMBIE_12722, NpcID.ARMOURED_ZOMBIE_12723, NpcID.ARMOURED_ZOMBIE_12724, NpcID.ARMOURED_ZOMBIE_12725,
			NpcID.ARMOURED_ZOMBIE_12726, NpcID.ARMOURED_ZOMBIE_12727, NpcID.ARMOURED_ZOMBIE_12728, NpcID.ARMOURED_ZOMBIE_12729, NpcID.ARMOURED_ZOMBIE_12730, NpcID.ARMOURED_ZOMBIE_12731);
		collectRedMistAgain = new ObjectStep(this, NullObjectID.NULL_50690, "Collect the red mist in a bottle.");
		goThroughSecondGate = new ObjectStep(this, ObjectID.GATE_50150, new WorldPoint(3540, 4597, 0),
			"Kill zombies to fill 3 bottles with mist again, and continue deeper into the dungeon.",
			bottleOfMist.quantity(3));
		goThroughSecondGate.addSubSteps(pickupBottlesAgain, killZombiesAgain, collectRedMistAgain);

		lookOverSecondBalcony = new ObjectStep(this, ObjectID.BALCONY_50159, new WorldPoint(3562, 4591, 0), "Look over the balcony facing over the zombie army.");

		talkToEliasInPalace = new NpcStep(this, NpcID.ELIAS_WHITE, new WorldPoint(3208, 3475, 0), "Report back to Elias White in the Varrock Palace.");
		talkToEliasInPalace.addTeleport(varrockTeleport);

		goUpToRovin = new ObjectStep(this, ObjectID.STAIRCASE_11790, new WorldPoint(3203, 3498, 0), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goUpToRovin.addTeleport(varrockTeleport);
		goUpToRovin2 = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3203, 3498, 1), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goUpToRovin2.addDialogStep("Climb up");
		talkToRovin = new NpcStep(this, NpcID.CAPTAIN_ROVIN, new WorldPoint(3205, 3498, 2), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		talkToRovin.addSubSteps(goUpToRovin, goUpToRovin2);

		// Forge
		enterCamdozaal = new ObjectStep(this, NullObjectID.NULL_41357, new WorldPoint(3000, 3494, 0),
			"Enter Camdozaal, west of Ice Mountain.", pickaxe, combatGear);
		enterCamdozaal.addTeleport(mindAltarOrLassarTeleport);
		talkToRamarno = new NpcStep(this, NpcID.RAMARNO_10685, new WorldPoint(2959, 5809, 0),
			"Talk to Ramarno to the north by the sacred forge.");
		((NpcStep) talkToRamarno).addAlternateNpcs(NpcID.RAMARNO, NpcID.RAMARNO_10684);
		talkToRamarno.addDialogStep("I need your help with a shield.");
		mineBarronite = new ObjectStep(this, ObjectID.BARRONITE_ROCKS, new WorldPoint(2941, 5810, 0), "Mine a barronite deposit.", true, pickaxe);
		killChaosGolems = new NpcStep(this, NpcID.CHAOS_GOLEM, new WorldPoint(3022, 5782, 0),
			"Kill chaos golems in the eastern cavern for a chaos core.", true, chaosCore);
		((NpcStep) killChaosGolems).addAlternateNpcs(NpcID.RUBBLE_10690);
		useCoreOnDeposit = new DetailedQuestStep(this, "Use a chaos core on a barronite deposit.", chaosCore.highlighted(), barroniteDeposit.highlighted());
		useBarroniteOnForge = new ObjectStep(this, NullObjectID.NULL_41411, new WorldPoint(2957, 5811, 0), "Use the imbued barronite on the sacred forge.", imbuedBarronite.highlighted());
		useBarroniteOnForge.addIcon(ItemID.IMBUED_BARRONITE);

		// Invasion
		goToF1ForRovinNonInstance = new ObjectStep(this, ObjectID.STAIRCASE_11790, new WorldPoint(3203, 3498, 0), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goToF1ForRovinNonInstance.addTeleport(varrockTeleport);
		goToF1ForRovin = new ObjectStep(this, ObjectID.STAIRCASE_11790, new WorldPoint(3906, 4969, 0), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goToF2ForRovin = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3906, 4969, 1), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		goToF2ForRovin.addDialogStep("Climb up");
		talkToRovinAfterForge = new NpcStep(this, NpcID.CAPTAIN_ROVIN_12627, new WorldPoint(3906, 4969, 2), "Talk to Captain Rovin upstairs in the north west of Varrock Castle.");
		talkToRovinAfterForge.addSubSteps(goToF1ForRovinNonInstance, goToF1ForRovin, goToF2ForRovin);

		// FUTURE
		talkToReldo = new NpcStep(this, NpcID.RELDO_12626, new WorldPoint(3914, 4966, 0), "Talk to Reldo in the Varrock Castle library.");
		goToF1ForReldo = new ObjectStep(this, ObjectID.STAIRCASE_11793, new WorldPoint(3906, 4969, 2),
			"Go to the bottom floor and talk to Reldo in the castle library.");
		goToF0ForReldo = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3906, 4969, 1),
			"Go to the bottom floor and talk to Reldo in the castle library.");
		goToF0ForReldo.addDialogStep("Climb down");
		talkToReldo.addSubSteps(goToF1ForReldo, goToF0ForReldo);

		searchScrolls = new ObjectStep(this, ObjectID.SCROLLS_50118, new WorldPoint(3920, 4968, 0), "Search the scrolls in the north-east of the library.");
		readList = new ItemStep(this, "Read the list of elders.", listOfElders.highlighted());
		readCensus = new ObjectStep(this, ObjectID.VARROCK_CENSUS, new WorldPoint(3918, 4969, 0), "Read the Varrock Census.");

		talkToRoald = new NpcStep(this, NpcID.KING_ROALD_12621, new WorldPoint(3926, 4945, 0),
			"Talk to King Roald.", shieldOfArrav);
		talkToRoaldOutsideInstance = new NpcStep(this, NpcID.KING_ROALD_5215, new WorldPoint(3221, 3473, 0), "Talk to King Roald.", shieldOfArrav);
		talkToRoald.addSubSteps(talkToRoaldOutsideInstance);

		talkToAeonisig = new NpcStep(this, NpcID.AEONISIG_RAISPHER_12620, new WorldPoint(3926, 4945, 0),
			"Talk to Aeonisig Raispher next to King Roald.", shieldOfArrav);
		talkToAeonisigOutsideInstance = new NpcStep(this, NpcID.AEONISIG_RAISPHER, new WorldPoint(3221, 3473, 0),
			"Talk to Aeonisig Raispher next to King Roald.", shieldOfArrav);
		talkToAeonisig.addSubSteps(talkToAeonisigOutsideInstance);

		talkToPrysin = new NpcStep(this, NpcID.SIR_PRYSIN_12622, new WorldPoint(3908, 4944, 0),
			"Talk to Sir Prysin in the south west corner of Varrock Castle.", shieldOfArrav);
		talkToPrysinOutsideInstance = new NpcStep(this, NpcID.SIR_PRYSIN, new WorldPoint(3203, 3472, 0),
			"Talk to Sir Prysin in the south west corner of Varrock Castle.", shieldOfArrav);
		talkToPrysin.addSubSteps(talkToPrysinOutsideInstance);

		talkToRomeo = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Talk to Romeo in Varrock Square.", shieldOfArrav);
		talkToRomeoFromInstance = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3915, 4900, 0), "Talk to Romeo in Varrock Square", shieldOfArrav);
		talkToRomeo.addSubSteps(talkToRomeoFromInstance);

		talkToHorvik = new NpcStep(this, NpcID.HORVIK, new WorldPoint(3229, 3436, 0), "Talk to Horvik north-east of Varrock Square.", shieldOfArrav);
		talkToHorvik.addDialogStep("I need your help with a shield.");
		talkToHorvikFromInstance = new NpcStep(this, NpcID.HORVIK, new WorldPoint(3933, 4908, 0), "Talk to Horvik north-east of Varrock Square.", shieldOfArrav);
		talkToHorvik.addSubSteps(talkToHorvikFromInstance);

		talkToHalen = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3257, 3448, 0),
			"Talk to Curator Haig in the Varrock Museum.", shieldOfArrav);
		talkToHalen.addDialogStep("I need your help with the zombie invasion.");
		talkToHalenFromInstance = new NpcStep(this, NpcID.CURATOR_HAIG_HALEN, new WorldPoint(3964, 4920, 0),
			"Talk to Curator Haig in the Varrock Museum.", shieldOfArrav);
		talkToHalen.addSubSteps(talkToHalenFromInstance);

		talkToDimintheis = new NpcStep(this, NpcID.DIMINTHEIS, new WorldPoint(3280, 3404, 0),
			"Talk to Dimintheis in the south-east of Varrock.", shieldOfArrav.hideConditioned(givenShield));
		talkToDimintheis.addDialogSteps("Other", "I need your help with the zombie invasion.");
		talkToDimintheisFromInstance = new NpcStep(this, NpcID.DIMINTHEIS, new WorldPoint(3952, 4897, 0),
			"Talk to Dimintheis in the south-east of Varrock.", shieldOfArrav.hideConditioned(givenShield));
		talkToDimintheis.addSubSteps(talkToDimintheisFromInstance);

		goToF1ToFinish = new ObjectStep(this, ObjectID.STAIRCASE_11790, new WorldPoint(3203, 3498, 0), "Talk to Captain Rovin upstairs in the north west of Varrock Castle to finish the quest.");
		goToF2ToFinish = new ObjectStep(this, ObjectID.STAIRCASE_11792, new WorldPoint(3203, 3498, 1), "Talk to Captain Rovin upstairs in the north west of Varrock Castle to finish the quest.");
		finishQuest = new NpcStep(this, NpcID.CAPTAIN_ROVIN, new WorldPoint(3205, 3498, 2), "Talk to Captain Rovin upstairs in the north west of Varrock Castle to finish the quest.");
		finishQuest.addSubSteps(goToF1ToFinish, goToF2ToFinish);
	}

	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return Arrays.asList(combatGear, pickaxe);
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return Arrays.asList(
			lumberyardTeleport,
			varrockTeleport.quantity(2),
			mindAltarOrLassarTeleport
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("At least six armoured zombies (level 85)", "Multiple Chaos Golems (level 70)");
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
			new ExperienceReward(Skill.SMITHING, 15000),
			new ExperienceReward(Skill.HUNTER, 15000)
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();

		//Skill Requirements
		req.add(new SkillRequirement(Skill.SMITHING, 55));
		req.add(new SkillRequirement(Skill.HUNTER, 52));

		//Quest Requirements
		req.add(new QuestRequirement(QuestHelperQuest.SHIELD_OF_ARRAV_BLACK_ARM_GANG, QuestState.FINISHED, "Finished Shield of Arrav"));
		req.add(new QuestRequirement(QuestHelperQuest.TEMPLE_OF_IKOV, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.BELOW_ICE_MOUNTAIN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.FAMILY_CREST, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.GARDEN_OF_TRANQUILLITY, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.WHAT_LIES_BELOW, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ROMEO__JULIET, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.DEMON_SLAYER, QuestState.FINISHED));

		return req;
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Zemouregal's Base"),
			new UnlockReward("5000 exp lamp and 5 kudos from Historian Minas")
		);
	}

	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Intrigue", Arrays.asList(talkToElias, inspectPlant, inspectRock, inspectPlant2, inspectBush1,
			inspectBush2, inspectBush3, inspectTrapdoor, listenToElias, lookOverBalcony, pickupBottles, killZombies, collectRedMist, openDoorToArrav,
			goThroughSecondGate, lookOverSecondBalcony), combatGear, lumberyardTeleport, varrockTeleport));
		allSteps.add(new PanelDetails("Crisis", Arrays.asList(talkToEliasInPalace, talkToRovin, enterCamdozaal, talkToRamarno, mineBarronite, killChaosGolems,
			useCoreOnDeposit, useBarroniteOnForge), combatGear, pickaxe, mindAltarOrLassarTeleport));

		allSteps.add(new PanelDetails("Invasion", Arrays.asList(talkToRovinAfterForge, talkToReldo, searchScrolls, readList, readCensus, talkToRoald,
			talkToAeonisig, talkToPrysin, talkToRomeo, talkToHorvik, talkToHalen, talkToDimintheis, finishQuest), varrockTeleport));
		return allSteps;
	}
}
