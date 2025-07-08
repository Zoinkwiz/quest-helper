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

import com.questhelper.bank.QuestBank;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileConsideringSceneLoadRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.location.TileIsLoadedRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.Arrays;
import java.util.List;

import static com.questhelper.requirements.util.LogicHelper.*;

public class WhispererSteps extends ConditionalStep
{
	QuestBank questBank;

	QuestStep enterRuinsOfCamdozaal, talkToRamarno, talkToPrescott, attachRope, descendDownRope,
		activateTeleporter1, activateTeleporter2, activateTeleporter3, activateTeleporter4, activateTeleporter5,
		activateTeleporter6, activateTeleporter7, recallShadowBlocker, useTeleporterToKetla, useTeleporterToScienceDistrict,
		useTeleporterToKetlaFromScienceDistrict,
		takeShadowBlockerSchematic, takeGreenShadowKey, takePurpleShadowKey, tryToEnterSunkenCathedral, talkToKetla,
		giveKetlaBlockerSchematic, claimShadowBlocker, enterSciencePuddle, retrieveShadowBlocker, placeBlockerInFurnaceBuilding,
		unlockDoor, takeShadowTorchSchematic, activateBlackstoneFragment, bringKetlaTheBasicTorchSchematic, claimShadowTorch,
		enterResidentialPuddle, destroyTentacles, activateBlackstoneFragment2, takeRevitalisingIdolSchematic, bringKetlaTheIdolSchematic,
		claimRevitalisingIdol, placeIdol, enterResidentialPuddleAgain, destroyTentacles2, getBlueShadowKeyShadowRealm,
		getBlueShadowKeyRealRealm, activateBlackstoneFragment3, recallDevices, placeShadowBlockerWestResidential,
		enterResidentialWestPuddle, openPubDoor, takeSuperiorTorchSchematic, bringKetlaTheSuperiorTorchSchematic,
		activateBlackstoneFragment4, takeSuperiorTorchSchematicRealWorld, claimSuperiorShadowTorch, enterSciencePuddle2,
		getAnimaPortalSchematic, getAnimaPortalSchematicRealWorld, activateBlackstoneFragment5, bringKetlaTheAnimaPortalSchematic;

	QuestStep pickUpIdol, enterPubRealWorld;

	QuestStep claimAnimaPortal, enterPlazaPuddle, destroyTentacles4, activateBlackstoneFragment6, takeWhiteShadowKey,
		placeBlockerWhiteChest, placeAnimaWhiteChest, placeIdolWhiteChest, enterPlazaPuddle2, lightBraziers, openFinalChest,
		activateBlackstoneFragment7, openFinalChestRealWorld, bringKetlaThePerfectSchematic, talkToKetlaAfterPerfectGiven;

	// Silent choir
	QuestStep enterPuddleNearPub, goUpstairsPub, touchPubRemnant, activateBlackstoneFragment8, enterPuddleNearPub2, destroyTentacles5,
		destroyTentacles6, activateBlackstoneFragment9, getRedShadowKey, placeBlockerInRedRoom, enterPuddleNearPub3, openRedChest, activateBlackstoneFragment10,
		openRedChestRealWorld, openGreenChest, openGreenChestRealWorld, enterSciencePuddle3, destroyTentacles7, activateBlackstoneFragment11, makeIcon,
		enterDrain, useIconInDrain, goDownDrainLadder, inspectPillar, talkToMe, talkToKetlaAfterVision, claimPerfectShadowTorch, enterPuddleNearPub4,
		destroyCathedralTentacles, activateBlackstoneFragment12, enterRuinsOfCamdozaalFight, descendDownRopeFight, enterTheCathedral, fightWhispererSidebar, searchEntrails,
			returnToDesertWithWhisperersMedallion, useWhisperersMedallionOnStatue, restoreSanity;

	ItemRequirement magicCombatGear, food, prayerPotions, staminaPotions, nardahTeleport, ringOfVisibility, lassarTeleport;

	ItemRequirement whisperersMedallion, veryLongRope, shadowBlockerSchematic, greenShadowKey, purpleShadowKey, shadowBlocker,
		basicShadowTorchSchematic, blackstoneFragment, basicShadowTorch, revitalisingIdolSchematic, revitalisingIdol, blueShadowKey,
		superiorTorchSchematic, animaPortalSchematic, animaPortal, superiorTorch, whiteShadowKey, perfectShadowTorchSchematic,
		redShadowKey, iconSegment1, iconSegment2, strangeIcon, perfectShadowTorch;

	FreeInventorySlotRequirement freeSlot;

	Requirement inVault, inCamdozaal, talkedToRamarno, talkedToPrescott, ropeAttached, inLassar,
		activatedTeleporter1, activatedTeleporter2, activatedTeleporter3, activatedTeleporter4,
		activatedTeleporter5, activatedTeleporter6, activatedTeleporter7, passedOutAtCathedral, finishedTalkingToKetla,
		givenShadowBlockerSchematic, blockerNearby, blockerPlacedAtDoor, inLassarShadowRealm, purpleKeyTaken, inFurnaceHouse,
		givenTorchSchematic, destroyedTentacles, givenIdolSchematic, idolPlaced, destroyedTentacles2, blockerPlacedAtPub,
		inPubShadowRealm, usedBlueKey, givenSuperiorTorchSchematic, destroyedTentacles3, givenAnimaPortalSchematic,
		destroyedTentacles4, braziersLit, obtainedPerfectedSchematic, perfectSchematicGiven, learntAboutSilentChoir,
		killedWhisperer, idolNearby, idolShadowRealmFullNearby, lowSanity;

	Requirement hadGreenShadowKey, hadPurpleKey, hadShadowBlockerSchematic, placedBlockerWhiteChest, placedAnimaWhiteChest,
		placedIdolWhiteChest, inPubUpstairsShadowRealm, touchedPubRemnant, destroyedTentacles5, destroyedTentacles6,
		blockerPlacedInRedRoom, hadRedShadowKey, redKeyUsed, inLassarShadowRealmSW, usedGreenKey, iconUsed, inDrainF0,
		inDrainF1, inVision, escapedVision, unlockedPerfectShadowTorch, destroyedCathedralTentacles, enteredCathedral,
		completedOtherMedallions, inStartingRoom, inScienceDistrict, inResidentialDistrict, inEastShadowRealm, inRealPub;
	Requirement destroyedTentacles7;

	RuneliteRequirement blockerNotInBenchOrInventory;

	Zone vault, camdozaal, lassar, lassarShadowRealm, furnaceHouse, externalFurnaceHouse, shadowPub, pubUpstairsShadowRealm,
		lassarShadowRealmSW, drainF0, drainF1, visionRegion, startingRoom, scienceDistrict, residentialDistrict,
		eastShadowRealm, realPub;

	public WhispererSteps(QuestHelper questHelper, QuestStep defaultStep, QuestBank questBank)
	{
		super(questHelper, defaultStep);
		this.questBank = questBank;
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Conditions activatedFirst6Teles = new Conditions(activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, activatedTeleporter5, activatedTeleporter6);

		/* Locked door steps */
		ConditionalStep lockedDoorSteps = new ConditionalStep(getQuestHelper(), takePurpleShadowKey);
		lockedDoorSteps.addStep(and(inEastShadowRealm, blueShadowKey), activateBlackstoneFragment3);
		lockedDoorSteps.addStep(and(inLassarShadowRealm, basicShadowTorchSchematic), activateBlackstoneFragment);
		lockedDoorSteps.addStep(and(basicShadowTorchSchematic), bringKetlaTheBasicTorchSchematic);
		lockedDoorSteps.addStep(and(inFurnaceHouse, nor(givenTorchSchematic)), takeShadowTorchSchematic);
		lockedDoorSteps.addStep(and(inLassarShadowRealm, hadPurpleKey, blockerPlacedAtDoor), unlockDoor);
		lockedDoorSteps.addStep(and(hadPurpleKey, blockerPlacedAtDoor), enterSciencePuddle);
		lockedDoorSteps.addStep(and(hadPurpleKey, blockerNearby), retrieveShadowBlocker);
		lockedDoorSteps.addStep(and(hadPurpleKey, blockerNotInBenchOrInventory), recallShadowBlocker);
		lockedDoorSteps.addStep(and(hadPurpleKey, shadowBlocker, inStartingRoom), useTeleporterToScienceDistrict);
		lockedDoorSteps.addStep(and(hadPurpleKey, shadowBlocker), placeBlockerInFurnaceBuilding);
		lockedDoorSteps.addStep(and(hadPurpleKey, inStartingRoom), useTeleporterToKetla);
		lockedDoorSteps.addStep(and(hadPurpleKey), claimShadowBlocker);

		ConditionalStep blueKeySteps = new ConditionalStep(getQuestHelper(), claimRevitalisingIdol);
		blueKeySteps.addStep(and(inLassarShadowRealm, destroyedTentacles2), activateBlackstoneFragment3);
		blueKeySteps.addStep(and(inLassar, destroyedTentacles2), getBlueShadowKeyRealRealm);
		blueKeySteps.addStep(and(inLassarShadowRealm), destroyTentacles2);
		blueKeySteps.addStep(and(idolPlaced, basicShadowTorch), enterResidentialPuddleAgain);
		blueKeySteps.addStep(and(idolNearby, basicShadowTorch, activatedTeleporter7), pickUpIdol);
		blueKeySteps.addStep(and(revitalisingIdol, basicShadowTorch, activatedTeleporter7), placeIdol);
		blueKeySteps.addStep(and(revitalisingIdol, basicShadowTorch), activateTeleporter7);

		ConditionalStep pubSteps = new ConditionalStep(getQuestHelper(), recallDevices);
		pubSteps.addStep(new Conditions(inLassar, superiorTorchSchematic), bringKetlaTheSuperiorTorchSchematic);
		pubSteps.addStep(new Conditions(inLassarShadowRealm, superiorTorchSchematic), activateBlackstoneFragment4);
		pubSteps.addStep(new Conditions(inRealPub, usedBlueKey), takeSuperiorTorchSchematicRealWorld);
		pubSteps.addStep(new Conditions(inLassar, usedBlueKey), enterPubRealWorld);
		pubSteps.addStep(new Conditions(inLassarShadowRealm, usedBlueKey), takeSuperiorTorchSchematic);
		pubSteps.addStep(new Conditions(inLassarShadowRealm, blockerPlacedAtPub), openPubDoor);
		pubSteps.addStep(blockerPlacedAtPub, enterResidentialWestPuddle);
		pubSteps.addStep(shadowBlocker, placeShadowBlockerWestResidential);

		ConditionalStep getAnimaPortalSteps = new ConditionalStep(getQuestHelper(), claimAnimaPortal);
		getAnimaPortalSteps.addStep(and(animaPortalSchematic, inLassar), bringKetlaTheAnimaPortalSchematic);
		getAnimaPortalSteps.addStep(and(animaPortalSchematic, inLassarShadowRealm), activateBlackstoneFragment5);
//		getAnimaPortalSteps.addStep(and(superiorTorch, inLassar, destroyedTentacles3), getAnimaPortalSchematicRealWorld);
		getAnimaPortalSteps.addStep(and(superiorTorch, inLassarShadowRealm), getAnimaPortalSchematic);
		getAnimaPortalSteps.addStep(superiorTorch, enterSciencePuddle2);

		ConditionalStep getWhiteKeySteps = new ConditionalStep(getQuestHelper(), claimSuperiorShadowTorch);
		getWhiteKeySteps.addStep(and(inLassar, destroyedTentacles4), takeWhiteShadowKey);
		getWhiteKeySteps.addStep(and(inLassarShadowRealm, destroyedTentacles4), activateBlackstoneFragment6);
		getWhiteKeySteps.addStep(and(inLassarShadowRealm, superiorTorch), destroyTentacles4);
		getWhiteKeySteps.addStep(and(superiorTorch, animaPortal), enterPlazaPuddle);
		getWhiteKeySteps.addStep(animaPortal, claimAnimaPortal);

		ConditionalStep getPerfectedSchematicSteps = new ConditionalStep(getQuestHelper(), placeBlockerWhiteChest);
		getPerfectedSchematicSteps.addStep(and(inLassarShadowRealm, braziersLit), openFinalChest);
		getPerfectedSchematicSteps.addStep(and(inLassarShadowRealm, lowSanity, idolShadowRealmFullNearby), restoreSanity);
		getPerfectedSchematicSteps.addStep(and(inLassarShadowRealm), lightBraziers);
		getPerfectedSchematicSteps.addStep(and(placedBlockerWhiteChest, placedAnimaWhiteChest, placedIdolWhiteChest), enterPlazaPuddle2);
		getPerfectedSchematicSteps.addStep(and(placedBlockerWhiteChest, placedAnimaWhiteChest), placeIdolWhiteChest);
		getPerfectedSchematicSteps.addStep(placedBlockerWhiteChest, placeAnimaWhiteChest);

		ConditionalStep silentChoirSteps = new ConditionalStep(getQuestHelper(), enterPuddleNearPub);
		silentChoirSteps.addStep(and(inPubShadowRealm, touchedPubRemnant), activateBlackstoneFragment8);

		silentChoirSteps.addStep(and(inDrainF0), inspectPillar);
		silentChoirSteps.addStep(and(inDrainF1, iconUsed), goDownDrainLadder);
		silentChoirSteps.addStep(and(inDrainF1, strangeIcon), useIconInDrain);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, or(strangeIcon, iconUsed)), enterDrain);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, iconSegment1, iconSegment2), makeIcon);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, iconSegment1, usedGreenKey), openGreenChestRealWorld); // when is this ever used?
		silentChoirSteps.addStep(and(inLassarShadowRealm, touchedPubRemnant, iconSegment1, iconSegment2), activateBlackstoneFragment11);
		silentChoirSteps.addStep(and(inLassarShadowRealmSW, touchedPubRemnant, iconSegment1, greenShadowKey, destroyedTentacles7), openGreenChest);
		silentChoirSteps.addStep(and(inLassarShadowRealmSW, touchedPubRemnant, iconSegment1, greenShadowKey), destroyTentacles7);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, iconSegment1, greenShadowKey), enterSciencePuddle3);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, iconSegment1), takeGreenShadowKey);
		silentChoirSteps.addStep(and(inLassarShadowRealm, touchedPubRemnant, iconSegment1), activateBlackstoneFragment10);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, redKeyUsed), openRedChestRealWorld);
		silentChoirSteps.addStep(and(inLassarShadowRealm, touchedPubRemnant, hadRedShadowKey, blockerPlacedInRedRoom), openRedChest);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, hadRedShadowKey, blockerPlacedInRedRoom), enterPuddleNearPub3);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, destroyedTentacles5, hadRedShadowKey), placeBlockerInRedRoom);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant, destroyedTentacles5, destroyedTentacles6), getRedShadowKey);
		silentChoirSteps.addStep(and(inLassarShadowRealm, touchedPubRemnant, destroyedTentacles5, destroyedTentacles6), activateBlackstoneFragment9);
		silentChoirSteps.addStep(and(inLassarShadowRealm, touchedPubRemnant, destroyedTentacles5), destroyTentacles6);
		silentChoirSteps.addStep(and(inLassarShadowRealm, touchedPubRemnant), destroyTentacles5);
		silentChoirSteps.addStep(and(inLassar, touchedPubRemnant), enterPuddleNearPub2);
		silentChoirSteps.addStep(inPubUpstairsShadowRealm, touchPubRemnant);
		silentChoirSteps.addStep(inLassarShadowRealm, goUpstairsPub);


		/* Top steps */
		addStep(and(inLassar, killedWhisperer), searchEntrails);
		addStep(and(inLassar, unlockedPerfectShadowTorch, or(destroyedCathedralTentacles, enteredCathedral)), enterTheCathedral);
		addStep(and(inLassarShadowRealm, unlockedPerfectShadowTorch, destroyedCathedralTentacles), activateBlackstoneFragment12);
		addStep(and(inLassarShadowRealm, unlockedPerfectShadowTorch, perfectShadowTorch), destroyCathedralTentacles);
		addStep(and(inLassar, unlockedPerfectShadowTorch, perfectShadowTorch), enterPuddleNearPub4);
		addStep(and(inLassar, unlockedPerfectShadowTorch), claimPerfectShadowTorch); // NOTE: I was not told to do this

		addStep(and(inCamdozaal, unlockedPerfectShadowTorch), descendDownRopeFight);
		addStep(and(unlockedPerfectShadowTorch), enterRuinsOfCamdozaalFight);

		addStep(and(inLassar, escapedVision), talkToKetlaAfterVision);
		addStep(inVision, talkToMe);
		addStep(and(or(inLassar, inLassarShadowRealm, inDrainF0, inDrainF1), learntAboutSilentChoir), silentChoirSteps);
		addStep(and(inLassar, perfectSchematicGiven), talkToKetlaAfterPerfectGiven);
		addStep(and(inLassar, perfectShadowTorchSchematic), bringKetlaThePerfectSchematic);
		addStep(and(inLassarShadowRealm, perfectShadowTorchSchematic), activateBlackstoneFragment7);
		addStep(and(inLassar, obtainedPerfectedSchematic), openFinalChestRealWorld);
		addStep(and(or(inLassar, inLassarShadowRealm), whiteShadowKey), getPerfectedSchematicSteps);
		addStep(and(or(inLassar, inLassarShadowRealm), givenAnimaPortalSchematic), getWhiteKeySteps);
		addStep(and(or(inLassar, inLassarShadowRealm), givenSuperiorTorchSchematic), getAnimaPortalSteps);
		addStep(and(or(inLassar, inLassarShadowRealm), givenIdolSchematic, or(blueShadowKey, usedBlueKey, superiorTorchSchematic)), pubSteps);
		addStep(and(or(inLassar, inLassarShadowRealm), givenIdolSchematic), blueKeySteps);

		addStep(and(inLassar, revitalisingIdolSchematic), bringKetlaTheIdolSchematic);
		addStep(and(inLassar, basicShadowTorch, destroyedTentacles), takeRevitalisingIdolSchematic);
		addStep(and(inLassarShadowRealm, basicShadowTorch, destroyedTentacles), activateBlackstoneFragment2);
		addStep(and(inLassarShadowRealm, basicShadowTorch), destroyTentacles);
		addStep(and(inLassar, basicShadowTorch), enterResidentialPuddle);
		addStep(and(inLassar, givenTorchSchematic), claimShadowTorch);

		addStep(and(or(inLassar, inLassarShadowRealm), activatedFirst6Teles, givenShadowBlockerSchematic), lockedDoorSteps);


		addStep(and(inLassar, activatedFirst6Teles, hadShadowBlockerSchematic, hadGreenShadowKey, hadPurpleKey, finishedTalkingToKetla),
			giveKetlaBlockerSchematic);
		addStep(and(inLassar, activatedFirst6Teles, hadShadowBlockerSchematic, hadGreenShadowKey, hadPurpleKey, passedOutAtCathedral),
			talkToKetla);
		addStep(and(inLassar, activatedFirst6Teles, hadShadowBlockerSchematic, hadGreenShadowKey, hadPurpleKey), tryToEnterSunkenCathedral);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, activatedTeleporter5, hadShadowBlockerSchematic, hadGreenShadowKey, hadPurpleKey), activateTeleporter6);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, hadShadowBlockerSchematic, hadGreenShadowKey, hadPurpleKey), activateTeleporter5);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			hadShadowBlockerSchematic, hadGreenShadowKey, hadPurpleKey), activateTeleporter4);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			hadShadowBlockerSchematic, hadGreenShadowKey), takePurpleShadowKey);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			hadShadowBlockerSchematic), takeGreenShadowKey);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3), takeShadowBlockerSchematic);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2), activateTeleporter3);
		addStep(and(inLassar, activatedTeleporter1), activateTeleporter2);
		addStep(and(inLassar), activateTeleporter1);
		addStep(and(inCamdozaal, ropeAttached), descendDownRope);
		addStep(and(inCamdozaal, talkedToPrescott, veryLongRope), attachRope);
		addStep(and(inCamdozaal, talkedToRamarno), talkToPrescott);
		addStep(inCamdozaal, talkToRamarno);
		addStep(null, enterRuinsOfCamdozaal);
	}

	protected void setupItemRequirements()
	{
		ringOfVisibility = new ItemRequirement("Ring of visibility", ItemID.FD_RING_VISIBILITY);
		magicCombatGear = new ItemRequirement("Magic combat gear", -1, -1);
		magicCombatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		food = new ItemRequirement("Food, as much as you can bring", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		lassarTeleport = new ItemRequirement("Mind altar or lassar teleport", ItemID.TELETAB_MIND_ALTAR);
		lassarTeleport.addAlternates(ItemID.TABLET_LASSAR);

		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(VarbitID.BOOKOFSCROLLS_NARDAH, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_ELITE);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_HARD, ItemID.TELEPORTSCROLL_NARDAH, ItemID.DESERT_AMULET_MEDIUM);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		freeSlot = new FreeInventorySlotRequirement(1);

		/* Quest items */
		whisperersMedallion = new ItemRequirement("Whisperer's medallion", ItemID.DT2_MEDALLION_WHISPERER)
			.alsoCheckBank(questBank);
		veryLongRope = new ItemRequirement("Very long rope", ItemID.DT2_LASSAR_ROPE).alsoCheckBank(questBank);
		shadowBlockerSchematic = new ItemRequirement("Shadow blocker schematic", ItemID.DT2_LASSAR_SHADOW_BLOCKER_SCHEMATIC);
		greenShadowKey = new ItemRequirement("Shadow key", ItemID.DT2_LASSAR_KEY_5);
		purpleShadowKey = new ItemRequirement("Shadow key", ItemID.DT2_LASSAR_KEY_1);
		shadowBlocker = new ItemRequirement("Shadow blocker", ItemID.DT2_SHADOW_BLOCKER);
		shadowBlocker.setTooltip("You can claim another from the table next to Ketla, or by using 'recall' on the blackstone fragment if you've already done so");
		basicShadowTorchSchematic = new ItemRequirement("Basic shadow torch schematic",
			ItemID.DT2_LASSAR_SHADOW_TORCH_T1_SCHEMATIC);
		blackstoneFragment = new ItemRequirement("Blackstone fragment", ItemID.BLACKSTONE_FRAGMENT);
		blackstoneFragment.addAlternates(ItemID.BLACKSTONE_FRAGMENT_ACTIVE, ItemID.BLACKSTONE_FRAGMENT_INITIAL);
		blackstoneFragment.setTooltip("You can get another from Ketla");
		basicShadowTorch = new ItemRequirement("Basic shadow torch", ItemID.DT2_SHADOW_TORCH_T1);
		basicShadowTorch.setTooltip("You can claim another from the table next to Ketla, or by using 'recall' on the blackstone fragment if you've already done so");
		revitalisingIdolSchematic = new ItemRequirement("Revitalising idol schematic", ItemID.DT2_LASSAR_REVITALISING_IDOL_SCHEMATIC);
		revitalisingIdol = new ItemRequirement("Revitalising idol", ItemID.DT2_REVITALISING_IDOL);
		revitalisingIdol.setTooltip("You can claim another from the table next to Ketla, or by using 'recall' on the blackstone fragment if you've already done so");
		blueShadowKey = new ItemRequirement("Shadow key", ItemID.DT2_LASSAR_KEY_2);
		superiorTorchSchematic = new ItemRequirement("Superior shadow torch schematic",
			ItemID.DT2_LASSAR_SHADOW_TORCH_T2_SCHEMATIC);
		superiorTorch = new ItemRequirement("Superior shadow torch", ItemID.DT2_SHADOW_TORCH_T2);
		superiorTorch.setTooltip("You can claim another from the table next to Ketla, or by using 'recall' on the blackstone fragment if you've already done so");

		animaPortalSchematic = new ItemRequirement("Anima portal schematic", ItemID.DT2_LASSAR_ANIMA_PORTAL_SCHEMATIC);
		animaPortal = new ItemRequirement("Anima portal", ItemID.DT2_ANIMA_PORTAL);
		animaPortal.setTooltip("You can claim another from the table next to Ketla, or by using 'recall' on the blackstone fragment if you've already done so");
		whiteShadowKey = new ItemRequirement("Shadow key", ItemID.DT2_LASSAR_KEY_3);
		perfectShadowTorchSchematic = new ItemRequirement("Perfected shadow torch schematic", ItemID.DT2_LASSAR_SHADOW_TORCH_T3_SCHEMATIC_FAKE);
		redShadowKey = new ItemRequirement("Shadow key", ItemID.DT2_LASSAR_KEY_4);
		iconSegment1 = new ItemRequirement("Icon segment", ItemID.DT2_LASSAR_ICON_PART_1);
		iconSegment2 = new ItemRequirement("Icon segment", ItemID.DT2_LASSAR_ICON_PART_2);
		strangeIcon = new ItemRequirement("Strange icon", ItemID.DT2_LASSAR_ICON);
		perfectShadowTorch = new ItemRequirement("Perfect shadow torch", ItemID.DT2_SHADOW_TORCH_T3);
	}

	protected void setupZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		camdozaal = new Zone(new WorldPoint(2897, 5757, 0), new WorldPoint(3047, 5869, 0));
		lassar = new Zone(new WorldPoint(2558, 6269, 0), new WorldPoint(2755, 6475, 3));
		lassarShadowRealm = new Zone(new WorldPoint(2284, 6262, 0), new WorldPoint(2501, 6476, 3));
		furnaceHouse = new Zone(new WorldPoint(2345, 6353, 0), new WorldPoint(2360, 6366, 3));
		externalFurnaceHouse = new Zone(new WorldPoint(2345, 6357, 0), new WorldPoint(2350, 6362, 0));
		shadowPub = new Zone(new WorldPoint(2371, 6421, 0), new WorldPoint(2384, 6435, 3));
		realPub = new Zone(new WorldPoint(2627, 6421, 0), new WorldPoint(2640, 6435, 3));
		pubUpstairsShadowRealm = new Zone(new WorldPoint(2371, 6421, 1), new WorldPoint(2384, 6435, 3));
		lassarShadowRealmSW = new Zone(new WorldPoint(2299, 6290, 0), new WorldPoint(2372, 6408, 3));
		drainF0 = new Zone(new WorldPoint(2750, 6270, 0), new WorldPoint(2815, 6340, 0));
		drainF1 = new Zone(new WorldPoint(2750, 6270, 1), new WorldPoint(2815, 6340, 1));
		visionRegion = new Zone(new WorldPoint(3222, 6294, 0), new WorldPoint(3239, 6313, 0));
		startingRoom = new Zone(new WorldPoint(2558, 6413, 0), new WorldPoint(2609, 6468, 0));
		scienceDistrict = new Zone(new WorldPoint(2554, 6295, 0), new WorldPoint(2629, 6397, 3));
		residentialDistrict = new Zone(new WorldPoint(2624, 6397, 0), new WorldPoint(2730, 6468, 3));
		eastShadowRealm = new Zone(new WorldPoint(2414, 6396, 0), new WorldPoint(2453, 6454, 0));
	}

	protected void setupConditions()
	{
		inVault = new ZoneRequirement(vault);
		inCamdozaal = new ZoneRequirement(camdozaal);
		inLassar = new ZoneRequirement(lassar);
		inLassarShadowRealm = new ZoneRequirement(lassarShadowRealm);
		inFurnaceHouse = new Conditions(nor(new ZoneRequirement(externalFurnaceHouse)), new ZoneRequirement(furnaceHouse));
		inPubShadowRealm = new ZoneRequirement(shadowPub);
		inRealPub = new ZoneRequirement(realPub);
		inPubUpstairsShadowRealm = new ZoneRequirement(pubUpstairsShadowRealm);
		inLassarShadowRealmSW = new ZoneRequirement(lassarShadowRealmSW);
		inDrainF0 = new ZoneRequirement(drainF0);
		inDrainF1 = new ZoneRequirement(drainF1);
		inVision = new ZoneRequirement(visionRegion);
		inStartingRoom = new ZoneRequirement(startingRoom);
		inScienceDistrict = new ZoneRequirement(scienceDistrict);
		inResidentialDistrict = new ZoneRequirement(residentialDistrict);
		inEastShadowRealm = new ZoneRequirement(eastShadowRealm);

		int WHISPERER_VARBIT = 15126;

		// Varbit is for learning about the area from the previous quest
		talkedToRamarno = new VarbitRequirement(VarbitID.CAMDOZAAL_RAMARNO_INTRO, 2, Operation.GREATER_EQUAL);

		talkedToPrescott = new VarbitRequirement(WHISPERER_VARBIT, 4, Operation.GREATER_EQUAL);
		ropeAttached = new VarbitRequirement(WHISPERER_VARBIT, 6, Operation.GREATER_EQUAL);
		// Entered undercity:
		// 15064 0->100
		// 15126 6->8
		// 14862 78->80
		// varplayer 3575 36691712 -> 36699904

		activatedTeleporter1 = new VarbitRequirement(15088, 1);
		activatedTeleporter2 = new VarbitRequirement(15089, 1);
		activatedTeleporter3 = new VarbitRequirement(15091, 1);
		activatedTeleporter4 = new VarbitRequirement(15092, 1);
		activatedTeleporter5 = new VarbitRequirement(15093, 1);
		activatedTeleporter6 = new VarbitRequirement(15090, 1);
		activatedTeleporter7 = new VarbitRequirement(15094, 1);

		passedOutAtCathedral = new VarbitRequirement(WHISPERER_VARBIT, 10, Operation.GREATER_EQUAL);
		// 10->12, ketla wants to see the ring
		// 12->14, fragment is now safe
		// 14->16, tried to give me fragment

		finishedTalkingToKetla = new VarbitRequirement(WHISPERER_VARBIT, 16, Operation.GREATER_EQUAL);
		givenShadowBlockerSchematic = new VarbitRequirement(15082, 1);
		// Entered science puddle
		// 15162 0->1 (probably just 'is in shadow realm'?)
		// 15163 0->3->13->14->15->16->17...100, ticks up to 100
		// 15064 = sanity, 0->100
		// 15069 0->453704->05->...09, left
		blockerNotInBenchOrInventory = new RuneliteRequirement(getQuestHelper().getConfigManager(), "dt2Blockerinbench", "true");
		blockerNotInBenchOrInventory.initWithValue("false");

		blockerNearby = new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_NORMAL);
		blockerPlacedAtDoor = new Conditions(LogicType.OR,
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_NORMAL,
				new Zone(new WorldPoint(2606, 6359, 0),
					new WorldPoint(2606, 6360, 0))
			),
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_SHADOW,
				new Zone(new WorldPoint(2350, 6359, 0),
					new WorldPoint(2350, 6360, 0)))
		);

		// TODO: Work out a way to determine if the door is unlocked?

		idolNearby = new ObjectCondition(ObjectID.DT2_LASSAR_REVITALISING_IDOL_NORMAL);

		idolShadowRealmFullNearby = new ObjectCondition(ObjectID.DT2_LASSAR_REVITALISING_IDOL_SHADOW);

		purpleKeyTaken = not(new ItemOnTileConsideringSceneLoadRequirement(ItemID.DT2_LASSAR_KEY_1, new WorldPoint(2593, 6352, 0)));

		hadPurpleKey = new Conditions(or(
			purpleShadowKey,
			purpleKeyTaken
		));

		usedBlueKey = new Conditions(
			LogicType.OR,
			inPubShadowRealm,
			new Conditions(
				new TileIsLoadedRequirement(new WorldPoint(2672, 6443, 0)),
				not(new ItemOnTileConsideringSceneLoadRequirement(blueShadowKey, new WorldPoint(2672, 6443, 0))),
				not(blueShadowKey)
			)
		);

		usedGreenKey = and(
			not(greenShadowKey),
			not(new ItemOnTileConsideringSceneLoadRequirement(ItemID.DT2_LASSAR_KEY_5, new WorldPoint(2581, 6387, 0)))
		);

		hadGreenShadowKey = new Conditions(or(
			greenShadowKey,
			usedGreenKey
		));

		hadShadowBlockerSchematic = new Conditions(or(shadowBlockerSchematic, givenShadowBlockerSchematic));

		// Given basic torch schematic in
		// 15085 0->1
		// 15126 18->20
		// Unsure when 16->18 happened

		givenTorchSchematic = new VarbitRequirement(15085, 1);

		ObjectCondition realWorldTentacleExists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T1,
			new Zone(new WorldPoint(2673, 6413, 0),
				new WorldPoint(2673, 6414, 0))
		);

		Conditions withinRangeOfTentacle = new Conditions(
			or(
				new TileIsLoadedRequirement(new WorldPoint(2673, 6413, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2417, 6413, 0))
			)
		);

		destroyedTentacles = new Conditions(
			withinRangeOfTentacle,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1,
					new Zone(new WorldPoint(2417, 6413, 0),
						new WorldPoint(2417, 6414, 0))
				),
				realWorldTentacleExists)
		);

		givenIdolSchematic = new VarbitRequirement(15083, 1);

		idolPlaced = new ObjectCondition(ObjectID.DT2_LASSAR_REVITALISING_IDOL_NORMAL,
			new Zone(new WorldPoint(2685, 6414, 0), new WorldPoint(2700, 6427, 0)));

		ObjectCondition realWorldTentacle2Exists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T1,
			new Zone(new WorldPoint(2679, 6439, 0), new WorldPoint(2680, 6439, 0))
		);

		Conditions withinRangeOfTentacle2 = new Conditions(
			or(
				new TileIsLoadedRequirement(new WorldPoint(2679, 6439, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2423, 6439, 0))
			)
		);

		destroyedTentacles2 = new Conditions(
			withinRangeOfTentacle2,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1,
					new Zone(new WorldPoint(2423, 6439, 0), new WorldPoint(2424, 6439, 0))
				),
				realWorldTentacle2Exists
			)
		);

		blockerPlacedAtPub = new Conditions(LogicType.OR,
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_NORMAL,
				new Zone(new WorldPoint(2641, 6428, 0),
					new WorldPoint(2641, 6427, 0))
			),
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_SHADOW,
				new Zone(new WorldPoint(2385, 6427, 0),
					new WorldPoint(2385, 6428, 0)))
		);

		usedBlueKey = new Conditions(
			LogicType.OR,
			inPubShadowRealm,
			new Conditions(
				new TileIsLoadedRequirement(new WorldPoint(2672, 6443, 0)),
				not(new ItemOnTileConsideringSceneLoadRequirement(blueShadowKey, new WorldPoint(2672, 6443, 0))),
				not(blueShadowKey)
			)
		);

		// 6443 is tile key is on
		// 6416 is the tile it rends within


		// Picked up superior schematics
		// 15126 20->22

		givenSuperiorTorchSchematic = new VarbitRequirement(15086, 1);
		// 22->24

		destroyedTentacles3 = new Conditions(
			new TileIsLoadedRequirement(new WorldPoint(2578, 63983, 0)),
			new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T2, new WorldPoint(2578, 6398, 0))
		);
		// Anima portal real world block:
		// 48207, new W(2578, y=6398, 0) Shadow Realm: ObjectID.DT2_LASSAR_BARRIER_SHADOW_T2

		givenAnimaPortalSchematic = new VarbitRequirement(15084, 1);

		/* Tentacle 4 */
		Conditions withinRangeOfTentacle4 = new Conditions(
			or(
				new TileIsLoadedRequirement(new WorldPoint(2351, 6386, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2607, 6386, 0))
			)
		);

		ObjectCondition realWorldTentacle4Exists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T2,
			new Zone(new WorldPoint(2607, 6386, 0), new WorldPoint(2607, 6385, 0))
		);

		destroyedTentacles4 = new Conditions(
			withinRangeOfTentacle4,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T2,
					new Zone(new WorldPoint(2351, 6386, 0), new WorldPoint(2351, 6385, 0))
				),
				realWorldTentacle4Exists
			)
		);

		placedBlockerWhiteChest = or(
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_NORMAL, new WorldPoint(2574, 6449, 0)),
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_SHADOW, new WorldPoint(2318, 6449, 0))
		);

		placedAnimaWhiteChest = or(
			new ObjectCondition(ObjectID.DT2_LASSAR_ANIMA_PORTAL_NORMAL, new WorldPoint(2576, 6447, 0)),
			new ObjectCondition(ObjectID.DT2_LASSAR_ANIMA_PORTAL_SHADOW, new WorldPoint(2320, 6447, 0))
		);

		placedIdolWhiteChest = or(
			new ObjectCondition(ObjectID.DT2_LASSAR_REVITALISING_IDOL_NORMAL, new WorldPoint(2578, 6445, 0)),
			new ObjectCondition(ObjectID.DT2_LASSAR_REVITALISING_IDOL_SHADOW, new WorldPoint(2322, 6445, 0)),
			new ObjectCondition(ObjectID.DT2_LASSAR_REVITALISING_IDOL_SHADOW_USED, new WorldPoint(2322, 6445, 0))
		);

		braziersLit = or(
			and(
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2318, 6446, 0)),
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2312, 6444, 0)),
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2321, 6449, 0)),
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2323, 6455, 0))
			),
			and(
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2574, 6446, 0)),
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2568, 6444, 0)),
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2577, 6449, 0)),
				new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BRAZIER_LIT_PERMANENTLY, new WorldPoint(2579, 6455, 0))
			));

		// Schematic got
		obtainedPerfectedSchematic = new VarbitRequirement(WHISPERER_VARBIT, 26, Operation.GREATER_EQUAL);
		// 15126 24->26
		perfectSchematicGiven = new VarbitRequirement(WHISPERER_VARBIT, 28, Operation.GREATER_EQUAL);

		learntAboutSilentChoir = new VarbitRequirement(WHISPERER_VARBIT, 30, Operation.GREATER_EQUAL);

		touchedPubRemnant = new VarbitRequirement(WHISPERER_VARBIT, 32, Operation.GREATER_EQUAL);

		/* Tentacle 5 */
		Conditions withinRangeOfTentacle5 = new Conditions(
			or(
				new TileIsLoadedRequirement(new WorldPoint(2392, 6411, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2648, 6411, 0))
			)
		);

		ObjectCondition realWorldTentacle5Exists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T2,
			new Zone(new WorldPoint(2647, 6411, 0), new WorldPoint(2648, 6411, 0))
		);

		destroyedTentacles5 = new Conditions(
			withinRangeOfTentacle5,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T2,
					new Zone(new WorldPoint(2391, 6411, 0), new WorldPoint(2392, 6411, 0))
				),
				realWorldTentacle5Exists
			)
		);

		Conditions withinRangeOfTentacle6 = new Conditions(
			or(
				new TileIsLoadedRequirement(new WorldPoint(2383, 6408, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2639, 6408, 0))
			)
		);

		ObjectCondition realWorldTentacle6Exists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T1,
			new Zone(new WorldPoint(2383, 6407, 0), new WorldPoint(2383, 6408, 0))
		);

		destroyedTentacles6 = new Conditions(
			withinRangeOfTentacle6,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1,
					new Zone(new WorldPoint(2383, 6407, 0), new WorldPoint(2383, 6408, 0))
				),
				realWorldTentacle6Exists
			)
		);

		var withinRangeOfTentacle7 =
			or(
				new TileIsLoadedRequirement(new WorldPoint(2349, 6343, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2605, 6343, 0))
			);

		ObjectCondition realWorldTentacle7Exists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T1,
			new Zone(new WorldPoint(2605, 6343, 0), new WorldPoint(2605, 6344, 0))
		);

		destroyedTentacles7 = new Conditions(
			withinRangeOfTentacle7,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1,
					new Zone(new WorldPoint(2349, 6343, 0), new WorldPoint(2349, 6344, 0))
				),
				realWorldTentacle7Exists
			)
		);

		blockerPlacedInRedRoom = new Conditions(LogicType.OR,
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_NORMAL,
				new WorldPoint(2649, 6406, 0)
			),
			new ObjectCondition(ObjectID.DT2_LASSAR_SHADOW_BLOCKER_SHADOW,
				new WorldPoint(2393, 6406, 0)
			)
		);

		redKeyUsed = and(
			not(redShadowKey),
			not(new ItemOnTileConsideringSceneLoadRequirement(ItemID.DT2_LASSAR_KEY_4, new WorldPoint(2638, 6405, 0)))
		);

		hadRedShadowKey = new Conditions(or(
			redShadowKey,
			redKeyUsed
		));

		// 32->34, maybe when made idol?

		iconUsed = new VarbitRequirement(WHISPERER_VARBIT, 36, Operation.GREATER_EQUAL);

		// animation 2757, fade-in
		escapedVision = new VarbitRequirement(WHISPERER_VARBIT, 38, Operation.GREATER_EQUAL);

		// 38->40, unlocked shadow torch
		unlockedPerfectShadowTorch = new VarbitRequirement(15087, 1);

		Conditions withinRangeOfCathedralTentacle = new Conditions(
			or(
				new TileIsLoadedRequirement(new WorldPoint(2656, 6393, 0)),
				new TileIsLoadedRequirement(new WorldPoint(2400, 6393, 0))
			)
		);

		ObjectCondition realWorldCathedralTentacleExists = new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_NORMAL_T3,
			new WorldPoint(2656, 6393, 0)
		);

		destroyedCathedralTentacles = new Conditions(
			withinRangeOfCathedralTentacle,
			nor(
				new ObjectCondition(ObjectID.DT2_LASSAR_BARRIER_SHADOW_T3,
					new WorldPoint(2400, 6393, 0)
				),
				realWorldCathedralTentacleExists
			)
		);

		// 14862 80->82
		enteredCathedral = new VarbitRequirement(WHISPERER_VARBIT, 42, Operation.GREATER_EQUAL);

		// Killed Whisperer:
		// 15064 0->100 (insanity?)
		// Varplayer 3969 0->1
		// 15126 42->44
		// 14862 82->84
		//
		killedWhisperer = new VarbitRequirement(WHISPERER_VARBIT, 44, Operation.GREATER_EQUAL);
		// Obtained medallion
		// 15126 44->46
		// 14862 84->86
		// 15161 1->0

		completedOtherMedallions = and(
			new VarbitRequirement(VarbitID.DT2_SCAR, 50, Operation.GREATER_EQUAL),
			new VarbitRequirement(VarbitID.DT2_GHORROCK, 70, Operation.GREATER_EQUAL),
			new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 56, Operation.GREATER_EQUAL)
		);
		// Told Ramarno about Ketla, 15149 0->1
		// Told Ramarno about Ketla being fake, 15150, 0->1
		// Told Ramarno about Thurgo, 15222 0->1


		// LISTENED TO UPSTAIRS EAST RESIDENTIAL DISTRICT PORTAL
		// 15097 0->1

		// Demon dies
		// 15164 0->3->2->1

		// Upstairs Science District north
		// 15098 0->2

		// West near water
		// 15095 0->3

		// East near water
		// 15096 0->4

		// new WorldPoint(2689, 6415, 0)
		// new WorldPoint(2672, 6443, 0)
		// 6415 - (6443 + 1) = 29

		lowSanity = new VarbitRequirement(VarbitID.SANITY, 20, Operation.LESS_EQUAL);
	}

	@Override
	public void onGameTick(GameTick event)
	{
		super.onGameTick(event);
		WidgetTextRequirement benchEmpty = new WidgetTextRequirement(229, 1,
			"You search the workbench, but find nothing of interest.");
		if (benchEmpty.check(client) && !shadowBlocker.check(client))
		{
			blockerNotInBenchOrInventory.setConfigValue("true");
		}
		else if (shadowBlocker.check(client))
		{
			blockerNotInBenchOrInventory.setConfigValue("false");
		}
	}

	protected void setupSteps()
	{
		enterRuinsOfCamdozaal = new ObjectStep(getQuestHelper(), ObjectID.BIM_ENTRANCE, new WorldPoint(3000, 3494, 0),
			"Enter Camdozaal, west of Ice Mountain.", ringOfVisibility.hideConditioned(finishedTalkingToKetla));
		((ObjectStep) enterRuinsOfCamdozaal).addTeleport(lassarTeleport);
		talkToRamarno = new NpcStep(getQuestHelper(), NpcID.CAMZODAAL_RAMARNO_ENTRANCE, new WorldPoint(2959, 5809, 0),
			"Talk to Ramarno to the north by the sacred forge.");
		talkToRamarno.addDialogStep("Have you seen any archeologists around here?");
		talkToPrescott = new NpcStep(getQuestHelper(), NpcID.DT2_CAMDOZAAL_ARCHAEOLOGIST_2_VIS, new WorldPoint(2975, 5794, 0),
			"Talk to Prescott to the east.");

		attachRope = new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_ENTRY_ROCK, new WorldPoint(2922, 5827, 0),
			"Tie the rope to a rock in the north west of the cavern, in the mine by the sinkhole.", veryLongRope.highlighted());
		attachRope.addIcon(ItemID.DT2_LASSAR_ROPE);

		descendDownRope = new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_ENTRY_ROCK, new WorldPoint(2922, 5827, 0),
			"Descend into the sinkhole.");
		descendDownRope.addDialogStep("Yes.");

		activateTeleporter1 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_PALACE, new WorldPoint(2593, 6424, 0),
			"Activate the teleporter to the south-east of the rope. You can use teleporters you've activated to go to other activated teleporters."),
			"Delve into the shadows.");
		activateTeleporter2 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_PLAZA, new WorldPoint(2617, 6417, 0),
			"Activate the teleporter further south-east."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateTeleporter3 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_SCIENCE_NORTH, new WorldPoint(2611, 6379, 0),
			"Activate the teleporter to the south in the Science District."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateTeleporter4 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_SCIENCE_SOUTH, new WorldPoint(2599, 6341, 0),
			"Activate the teleporter in the far south of the Science District."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateTeleporter5 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_RESIDENTIAL_WEST, new WorldPoint(2643, 6434, 0),
			"Teleport back to the Plaza, then run east. Activate the teleporter in the north-west of the residential area."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateTeleporter5.addDialogStep("The Plaza.");
		activateTeleporter6 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_CATHEDRAL, new WorldPoint(2652, 6405, 0),
			"Activate the teleporter in the south of the residential area."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateTeleporter7 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_RESIDENTIAL_EAST, new WorldPoint(2691, 6415, 0),
			"Activate the teleporter in the far east of the residential area, through the building you unlocked for the idol."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		useTeleporterToKetla = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_PALACE, new WorldPoint(2593, 6424, 0),
			"Use the teleporter to the Western Residential District."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		useTeleporterToKetla.addDialogSteps("Western Residential District.", "More options...", "Take the Shadow Torch.");
		takeShadowBlockerSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2590, 6380, 0),
			"Enter the north-western building in the Science District, and take the Shadow Schematic which is on top of a barrel in there.",
			shadowBlockerSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		takeShadowBlockerSchematic.addDialogSteps("More options...", "Northern Science District.");
		takeShadowBlockerSchematic.addSubSteps(useTeleporterToKetla);
		takeGreenShadowKey = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2581, 6387, 0),
			"Take the shadow key from the north-western room of the Science District.", greenShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		takeGreenShadowKey.addDialogStep("Northern Science District.");
		takePurpleShadowKey = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2593, 6352, 0),
			"Take the shadow key from the building to the south.",
			purpleShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		takePurpleShadowKey.addDialogStep("Northern Science District.");

		tryToEnterSunkenCathedral = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), new WorldPoint(2656, 6386, 0),
			"Run south-east to try and enter the Sunken Cathedral."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		tryToEnterSunkenCathedral.addDialogStep("Western Residential District.");

		talkToKetla = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Talk to Ketla, next to the Western Residential District teleport.", ringOfVisibility, freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		talkToKetla.addDialogStep("Western Residential District.");

		giveKetlaBlockerSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Give the blocker schematic to Ketla, next to the Western Residential District teleport.", shadowBlockerSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		giveKetlaBlockerSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");

		claimShadowBlocker = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_KETLA_WORKBENCH, new WorldPoint(2645, 6440, 0),
			"Get the Shadow Blocker from the workbench next to Ketla, or use 'Recall' on your Blackstone fragment to retrieve it.", freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		claimShadowBlocker.addDialogSteps("Take it.", "Western Residential District.");

		placeBlockerInFurnaceBuilding = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), new WorldPoint(2606, 6359, 0),
			"Return to the Science District, and enter the building with furnaces in it. Place the Shadow Blocker next to the locked doors inside.",
			shadowBlocker.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeBlockerInFurnaceBuilding.addDialogSteps("More options...", "Southern Science District.");
		placeBlockerInFurnaceBuilding.addIcon(ItemID.DT2_SHADOW_BLOCKER);

		useTeleporterToScienceDistrict = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_TELEPORTER_PALACE, new WorldPoint(2593, 6424, 0),
		"Teleport to the Southern Science District."), "Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		useTeleporterToScienceDistrict.addDialogSteps("More options...", "Southern Science District.");
		placeBlockerInFurnaceBuilding.addSubSteps(useTeleporterToScienceDistrict);

		retrieveShadowBlocker = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_BLOCKER_NORMAL, "Pick up the shadow blocker."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		recallShadowBlocker = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Right-click and select 'Recall' on the blackstone fragment to recall everything.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		recallShadowBlocker.addDialogStep("Yes.");
		retrieveShadowBlocker.addSubSteps(recallShadowBlocker);
		placeBlockerInFurnaceBuilding.addSubSteps(retrieveShadowBlocker);

		enterSciencePuddle = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2598, 6365, 0),
			"Enter the puddle outside of the building with furnaces in it. When you're in the Shadow Realm, you will slowly lose sanity. " +
				"If you're low on Sanity, you can escape using the Blackstone fragment."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterSciencePuddle.addDialogStep("Southern Science District.");

		// Unlocking door uses up purple key
		unlockDoor = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.LASSAR_LOCKED_DOOR_SHADOW_CLOSED_L, new WorldPoint(2350, 6360, 0),
			"Unlock the door inside the furnace building. " +
			"As long as you're directly next to the Shadow Blocker you placed you will be safe from lost souls."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		takeShadowTorchSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2353, 6367, 0),
			"Take the basic shadow torch schematic.",
			basicShadowTorchSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		activateBlackstoneFragment = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave.",
			blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		bringKetlaTheBasicTorchSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Bring the basic shadow torch schematic to Ketla, next to the Western Residential District teleport.",
			basicShadowTorchSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaTheBasicTorchSchematic.addDialogSteps("More options...", "Western Residential District.", "I have a schematic here.");

//		useTeleporterToKetlaFromScienceDistrict = new ObjectStep(getQuestHelper()),

		claimShadowTorch = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_KETLA_WORKBENCH, new WorldPoint(2645, 6440, 0),
			"Get the Shadow Torch from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		claimShadowTorch.addDialogSteps("Take it.", "Western Residential District.", "Take the Shadow Torch.", "Take everything.");
		enterResidentialPuddle = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		destroyTentacles = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1, new WorldPoint(2417, 6413, 0),
			"Destroy the tentacles outside the building to the east."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateBlackstoneFragment2 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		takeRevitalisingIdolSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2677, 6422, 0),
			"Take the revitalising idol schematic from inside the house.", revitalisingIdolSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		bringKetlaTheIdolSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Bring the basic revitalising idol schematic to Ketla, next to the Western Residential District teleport.",
			revitalisingIdolSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaTheIdolSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");
		claimRevitalisingIdol = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_KETLA_WORKBENCH, new WorldPoint(2645, 6440, 0),
			"Get the revitalising idol from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		claimRevitalisingIdol.addDialogSteps("Take it.", "Western Residential District.", "Take the Revitalising Idol.", "Take everything.");
		DetailedQuestStep placeIdolRealStep = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2689, 6415, 0), "Place the idol by the teleporter.",
			revitalisingIdol.highlighted());
		// NOTE: These line points showed up too late
		placeIdolRealStep.setLinePoints(Arrays.asList(
			new WorldPoint(2668, 6414, 0),
			new WorldPoint(2682, 6414, 0),
			new WorldPoint(2682, 6407, 0),
			new WorldPoint(2688, 6407, 0),
			new WorldPoint(2688, 6419, 0)
		));
		placeIdol = new PuzzleWrapperStep(getQuestHelper(),
			new PuzzleWrapperStep(getQuestHelper(), placeIdolRealStep, "Delve into the shadows."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		pickUpIdol = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_REVITALISING_IDOL_NORMAL, "Pick up the revitalising idol."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeIdol.addSubSteps(pickUpIdol);

		enterResidentialPuddleAgain = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		ObjectStep destroyTentacles2RealStep = new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1, new WorldPoint(2424, 6439, 0),
			"Run around through the building and up north, making sure to RESTORE SANITY AT THE IDOL." +
				" Destroy the tentacles blocking the door at the end of the path.", basicShadowTorch);
		destroyTentacles2RealStep.setLinePoints(Arrays.asList(
			new WorldPoint(2412, 6414, 0),
			new WorldPoint(2412, 6414, 0),
			new WorldPoint(2426, 6407, 0),
			new WorldPoint(2432, 6407, 0),
			new WorldPoint(2432, 6419, 0),
			new WorldPoint(2441, 6427, 0),
			new WorldPoint(2439, 6437, 0),
			new WorldPoint(2431, 6437, 0),
			new WorldPoint(2431, 6431, 0),
			new WorldPoint(2424, 6431, 0),
			new WorldPoint(2424, 6437, 0)
		));
		destroyTentacles2 = new PuzzleWrapperStep(getQuestHelper(),
			new PuzzleWrapperStep(getQuestHelper(), destroyTentacles2RealStep, "Delve into the shadows."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);


		getBlueShadowKeyRealRealm = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2672, 6443, 0), "Grab the shadow key in the room you've just unlocked.", blueShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		getBlueShadowKeyShadowRealm = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2416, 6443, 0), "Grab the shadow key in the room you've just unlocked.", blueShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		getBlueShadowKeyRealRealm.addSubSteps(getBlueShadowKeyShadowRealm);

		activateBlackstoneFragment3 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		recallDevices = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Right-click and select 'Recall' on the blackstone fragment to recall all your devices.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		recallDevices.addDialogStep("Yes.");

		placeShadowBlockerWestResidential = new PuzzleWrapperStep(getQuestHelper(),
			new TileStep(getQuestHelper(), new WorldPoint(2641, 6428, 0),
			"Return to the west residential area, and place the shadow blocker at the pub's entrance.", shadowBlocker.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeShadowBlockerWestResidential.addIcon(ItemID.DT2_SHADOW_BLOCKER);
		placeShadowBlockerWestResidential.addDialogSteps("More options...", "Western Residential District.");
		enterResidentialWestPuddle = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterResidentialWestPuddle.addDialogSteps("More options...", "Western Residential District.");

		// Can't open, DOORS_48255
		openPubDoor = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.LASSAR_LOCKED_DOOR_SHADOW_CLOSED_L, new WorldPoint(2385, 6427, 0),
			"Open the pub's doors."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		// 'You unlock the doors.', DIALOG_SPRITE_TEXT
		// DIALOG_SPRITE_SPRITE, itemID 28371
		takeSuperiorTorchSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2381, 6423, 0),
			"Take the superior torch schematic.", superiorTorchSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		takeSuperiorTorchSchematicRealWorld = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2637, 6423, 0),
			"Take the superior torch schematic.", superiorTorchSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPubRealWorld = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.LASSAR_LOCKED_DOOR_NORMAL_CLOSED_L, new WorldPoint(2641, 6427, 0),
			"Enter the pub and claim the superior torch schematic."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		takeSuperiorTorchSchematic.addSubSteps(takeSuperiorTorchSchematicRealWorld, enterPubRealWorld);
		activateBlackstoneFragment4 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaTheSuperiorTorchSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Bring the superior shadow torch schematic to Ketla, next to the Western Residential District teleport.",
			superiorTorchSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaTheSuperiorTorchSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");

		claimSuperiorShadowTorch = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_KETLA_WORKBENCH, new WorldPoint(2645, 6440, 0),
			"Get the superior shadow torch from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		claimSuperiorShadowTorch.addDialogSteps("Take it.", "Western Residential District.", "Take the Superior Shadow Torch.", "Take everything.");

		enterSciencePuddle2 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2598, 6365, 0),
		"Enter the puddle outside of the building with furnaces in it in the Science District."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterSciencePuddle2.addDialogStep("Northern Science District.");
		getAnimaPortalSchematicRealWorld = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2580, 6402, 0), "Go to the north-west of the science district," +
			" and burn the tentacles there to grab the Anima portal schematic.", animaPortalSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		ItemStep getAnimaPortalSchematicRealStep = new ItemStep(getQuestHelper(), new WorldPoint(2324, 6402, 0), "Go to the north-west of the science district," +
			" and burn the tentacles there to grab the Anima portal schematic.", animaPortalSchematic);
		getAnimaPortalSchematicRealStep.setLinePoints(Arrays.asList(
			new WorldPoint(2338, 6368, 0),
			new WorldPoint(2323, 6368, 0),
			new WorldPoint(2323, 6376, 0),
			new WorldPoint(2317, 6381, 0),
			new WorldPoint(2317, 6390, 0),
			new WorldPoint(2322, 6393, 0),
			new WorldPoint(2322, 6399, 0)
		));
		getAnimaPortalSchematic = new PuzzleWrapperStep(getQuestHelper(), getAnimaPortalSchematicRealStep, "Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		getAnimaPortalSchematic.addSubSteps(getAnimaPortalSchematicRealWorld);

		activateBlackstoneFragment5 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		bringKetlaTheAnimaPortalSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Bring the anima portal schematic to Ketla, next to the Western Residential District teleport.",
			animaPortalSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaTheAnimaPortalSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");
		bringKetlaTheAnimaPortalSchematic.addDialogStepWithExclusion("More options...", "How did you end up down here?");
		claimAnimaPortal = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_KETLA_WORKBENCH, new WorldPoint(2645, 6440, 0),
			"Get the anima portal from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		claimAnimaPortal.addDialogSteps("Take it.", "Western Residential District.", "Take the Anima Portal.", "Take everything.");

		enterPlazaPuddle = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2618, 6404, 0),
			"Enter the puddle on the Plaza.", superiorTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPlazaPuddle.addDialogSteps("Plaza.");

		destroyTentacles4 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T2, new WorldPoint(2351, 6386, 0),
			"Destroy the tentacles in front of the building to the south of the Plaza.", superiorTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateBlackstoneFragment6 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		takeWhiteShadowKey = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2597, 6392, 0),
			"Get the shadow key from the house.",
			whiteShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		placeBlockerWhiteChest = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), new WorldPoint(2574, 6449, 0),
			"Return to the palace in the north-west. Face the marked tile and place the shadow blocker to place it onto the marked tile.",
				shadowBlocker.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeBlockerWhiteChest.addDialogSteps("Palace.");
		placeBlockerWhiteChest.addIcon(ItemID.DT2_SHADOW_BLOCKER);

		placeAnimaWhiteChest = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), new WorldPoint(2576, 6447, 0),
			"Face the marked tile and place the anima portal to place it onto the marked tile.",
				animaPortal.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeAnimaWhiteChest.addIcon(ItemID.DT2_ANIMA_PORTAL);

		placeIdolWhiteChest = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), new WorldPoint(2578, 6445, 0),
			"Face the marked tile and place the idol to place it into the marked tile.",
				revitalisingIdol.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeIdolWhiteChest.addIcon(ItemID.DT2_REVITALISING_IDOL);

		enterPlazaPuddle2 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2618, 6404, 0),
			"Enter the puddle on the Plaza."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPlazaPuddle2.addDialogSteps("Plaza.");
		lightBraziers = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_BRAZIER_UNLIT, new WorldPoint(2319, 6448, 0),
			"Run to the chest in the shadow realm, and use the revitalising idol to increase your Sanity. Light the 4 braziers near the chest.",
			true),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		restoreSanity = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_REVITALISING_IDOL_SHADOW, "Restore sanity with the revitalizing idol."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		lightBraziers.addSubSteps(restoreSanity);
		openFinalChest = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHEST_SHADOW_4, new WorldPoint(2316, 6450, 0), "Open the chest in the palace."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openFinalChestRealWorld = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHEST_NORMAL_4, new WorldPoint(2572, 6450, 0), "Open the chest in the palace."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openFinalChest.addSubSteps(openFinalChestRealWorld);

		activateBlackstoneFragment7 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaThePerfectSchematic = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Bring the perfected shadow torch schematic to Ketla, next to the Western Residential District teleport.",
			perfectShadowTorchSchematic),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		bringKetlaThePerfectSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");
		bringKetlaThePerfectSchematic.addDialogStepWithExclusion("More options...", "How did you end up down here?");
		talkToKetlaAfterPerfectGiven = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Talk to Ketla, next to the Western Residential District teleport."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		talkToKetlaAfterPerfectGiven.addDialogSteps("More options...", "Western Residential District.", "I have a schematic here.");
		bringKetlaThePerfectSchematic.addSubSteps(talkToKetlaAfterPerfectGiven);

		enterPuddleNearPub = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPuddleNearPub.addDialogStep("The Cathedral.");
		goUpstairsPub = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.STEPS_STONE01_LASSAR01_BOTTOM01_SHADOW, new WorldPoint(2372, 6430, 0),
			"Go upstairs in the pub."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		touchPubRemnant = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_REMNANT_STRONG, new WorldPoint(2380, 6430, 1), "Touch the remnant in the north-east room."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		activateBlackstoneFragment8 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		enterPuddleNearPub2 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla again once your Sanity has returned.", superiorTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPuddleNearPub2.addDialogStep("The Cathedral.");
		destroyTentacles5 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T2, new WorldPoint(2392, 6411, 0),
			"Burn the tentacles outside the house to the south-west.", superiorTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		destroyTentacles6 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1, new WorldPoint(2383, 6408, 0),
			"Burn the tentacles outside the house to the west.", superiorTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		activateBlackstoneFragment9 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		getRedShadowKey = new PuzzleWrapperStep(getQuestHelper(),
			new ItemStep(getQuestHelper(), new WorldPoint(2638, 6405, 0),
			"Get the shadow key from the south-west house you can now enter.", redShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		placeBlockerInRedRoom = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), new WorldPoint(2649, 6406, 0),
			"Place the shadow blocker in the square building you made accessible, next to the chest.", shadowBlocker.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		placeBlockerInRedRoom.addIcon(ItemID.DT2_SHADOW_BLOCKER);
		enterPuddleNearPub3 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla again.", superiorTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPuddleNearPub3.addDialogStep("The Cathedral.");
		openRedChest = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHEST_SHADOW_1, new WorldPoint(2392, 6405, 0),
			"Open the chest in the square building."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openRedChestRealWorld = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHEST_NORMAL_1, new WorldPoint(2648, 6405, 0),
			"Open the chest in the square building."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openRedChest.addSubSteps(openRedChestRealWorld);

		activateBlackstoneFragment10 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		enterSciencePuddle3 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2598, 6365, 0),
			"Go to the Southern Science District and enter the puddle there."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterSciencePuddle3.addDialogStep("Southern Science District.");
		destroyTentacles7 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T1, new WorldPoint(2349, 6343, 0),
				"Destroy the tentacles outside the small house to the south.", superiorTorch, greenShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openGreenChest = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHEST_SHADOW_1, new WorldPoint(2354, 6339, 0),
			"Open the chest in the small house to the south.", superiorTorch, greenShadowKey),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openGreenChestRealWorld = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHEST_NORMAL_1, new WorldPoint(2610, 6339, 0),
			"Open the chest in the small house to the south."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		openGreenChest.addSubSteps(openGreenChestRealWorld);

		activateBlackstoneFragment11 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		makeIcon = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(), "Combine the icon halves.", iconSegment1.highlighted(), iconSegment2.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterDrain = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHURCH_ENTRY, new WorldPoint(2656, 6424, 0),
			"Enter the drain in the middle of the Residential District.", strangeIcon.hideConditioned(iconUsed)),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterDrain.addDialogStep("The Cathedral.");

		useIconInDrain = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHURCH_GATE, new WorldPoint(2784, 6287, 1),
			"Go through the gate.", strangeIcon.hideConditioned(iconUsed)),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		goDownDrainLadder = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_CHURCH_LADDER_TOP, new WorldPoint(2806, 6303, 1),
			"Go down the ladder to the east."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		inspectPillar = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.LASSAR_SEWER_BLACKSTONE_PILLAR, new WorldPoint(2784, 6304, 0),
			"Inspect the Ancient Pillar."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		talkToMe = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_LASSAR_FAKE_PLAYER_OP, new WorldPoint(3232, 6308, 0), "Talk to yourself."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		talkToKetlaAfterVision = new PuzzleWrapperStep(getQuestHelper(),
			new NpcStep(getQuestHelper(), NpcID.DT2_KETLA_VIS, new WorldPoint(2648, 6442, 0),
			"Talk to Ketla, next to the Western Residential District teleport."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		talkToKetlaAfterVision.addDialogSteps("More options...", "Western Residential District.");
		// Varp 3679 -1>2343 ???

		claimPerfectShadowTorch = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_KETLA_WORKBENCH, new WorldPoint(2645, 6440, 0),
			"Get the perfect shadow torch from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		claimPerfectShadowTorch.addDialogSteps("More options...", "Take it.", "Western Residential District.",
			"Take the Shadow Torch.");

		enterPuddleNearPub4 = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_SHADOW_PUDDLE, new WorldPoint(2665, 6418, 0),
		"Enter the puddle south-east of Ketla for the final time.", perfectShadowTorch),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);
		enterPuddleNearPub4.addDialogStep("The Cathedral.");

		destroyCathedralTentacles = new PuzzleWrapperStep(getQuestHelper(),
			new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_BARRIER_SHADOW_T3, new WorldPoint(2400, 6393, 0),
			"Destroy the tentacles at the Cathedral to the south."),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		activateBlackstoneFragment12 = new PuzzleWrapperStep(getQuestHelper(),
			new DetailedQuestStep(getQuestHelper(),
			"Activate the blackstone fragment to leave the Shadow Realm.", blackstoneFragment.highlighted()),
			"Delve into the shadows.").withNoHelpHiddenInSidebar(true);

		enterTheCathedral = new NpcStep(getQuestHelper(), NpcID.WHISPERER_SPAWN, new WorldPoint(2656, 6370, 0),
			"Gear up for the boss fight, and then enter the Cathedral. Disturb the Odd Figure to start the fight. " +
				"View the sidebar for more details.", blackstoneFragment, magicCombatGear, food, prayerPotions);
		enterTheCathedral.addDialogStep("The Cathedral.");
		((NpcStep) enterTheCathedral).addAlternateNpcs(NpcID.WHISPERER_CUTSCENE, NpcID.WHISPERER, NpcID.WHISPERER_MELEE,
			NpcID.WHISPERER_QUEST, NpcID.WHISPERER_MELEE_QUEST);


		enterRuinsOfCamdozaalFight = new ObjectStep(getQuestHelper(), ObjectID.BIM_ENTRANCE, new WorldPoint(3000, 3494, 0),
				"Enter Camdozaal, west of Ice Mountain, ready to fight The Whisperer.", magicCombatGear, prayerPotions, blackstoneFragment);
		((ObjectStep) enterRuinsOfCamdozaalFight).addTeleport(lassarTeleport);

		descendDownRopeFight = new ObjectStep(getQuestHelper(), ObjectID.DT2_LASSAR_ENTRY_ROCK, new WorldPoint(2922, 5827, 0),
				"Descend into the sinkhole.");
		descendDownRopeFight.addDialogStep("Yes.");
		fightWhispererSidebar = new DetailedQuestStep(getQuestHelper(),
			"Gear up, then disturb the Odd Figure in the Cathedral. The Whisperer will attack with magic and ranged attacks, and use a homing tentacle attack after each one. Protect appropriately, and move to avoid the tentacle splashes.");
		fightWhispererSidebar.addText("At 1/3rd health, she will cast a special attack:");
		fightWhispererSidebar.addText("Soul Siphon: Activate the blackstone fragment in your inventory. 12 souls spawn. Kill them to avoid the Whisperer dealing a massive hit.");
		fightWhispererSidebar.addText("Corrupted seeds: Activate the blackstone fragment in your inventory. Avoid the dark green seeds, and step on the light green ones.");
		fightWhispererSidebar.addText("Screech: Pillars appear, which you must hide behind to avoid damage.");
		fightWhispererSidebar.addText("After each special attack, the Whisperer fire out a binding spell if you are within 10 tiles of her, dealing Melee damage.");
		fightWhispererSidebar.addText("When she hits 0 health, she will heal back to 100, and start attacking rapidly with random ranged and magic attacks. Finish her off.");
		fightWhispererSidebar.addSubSteps(enterTheCathedral, enterRuinsOfCamdozaalFight, descendDownRopeFight);

		searchEntrails = new ObjectStep(getQuestHelper(), ObjectID.WHISPERER_MEDALLION_LOC, new WorldPoint(2656, 6370, 0),
			"Search the entrails dropped by the Whisperer.", true);
		searchEntrails.addDialogStep("The Cathedral.");

		returnToDesertWithWhisperersMedallion = new ObjectStep(getQuestHelper(), ObjectID.DT2_DESERT_VAULT_DOOR,
			new WorldPoint(3511, 2971, 0),
			"Return to the Vault door north-east of Nardah. Be wary of an assassin coming to kill you! They can run, freeze, and teleblock you.",
			whisperersMedallion);
		((ObjectStep) returnToDesertWithWhisperersMedallion).addTeleport(nardahTeleport);
		returnToDesertWithWhisperersMedallion.conditionToHideInSidebar(completedOtherMedallions);

		useWhisperersMedallionOnStatue = new ObjectStep(getQuestHelper(), ObjectID.DT2_VAULT_WHISPERER_STATUE, new WorldPoint(3932, 9636, 1),
			"Use the medallion on the north-west statue.", whisperersMedallion.highlighted());
		useWhisperersMedallionOnStatue.addIcon(ItemID.DT2_MEDALLION_WHISPERER);
		useWhisperersMedallionOnStatue.conditionToHideInSidebar(completedOtherMedallions);
	}

	protected List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(enterRuinsOfCamdozaal, talkToRamarno, talkToPrescott, attachRope, descendDownRope,
			activateTeleporter1, activateTeleporter2, activateTeleporter3, takeShadowBlockerSchematic, takeGreenShadowKey,
			takePurpleShadowKey, activateTeleporter4, activateTeleporter5, activateTeleporter6, tryToEnterSunkenCathedral, talkToKetla,
			giveKetlaBlockerSchematic, claimShadowBlocker, placeBlockerInFurnaceBuilding, enterSciencePuddle, unlockDoor,
			takeShadowTorchSchematic, activateBlackstoneFragment, bringKetlaTheBasicTorchSchematic, claimShadowTorch,
			enterResidentialPuddle, destroyTentacles, activateBlackstoneFragment2, takeRevitalisingIdolSchematic,
			bringKetlaTheIdolSchematic, takeRevitalisingIdolSchematic, bringKetlaTheIdolSchematic, claimRevitalisingIdol,
			placeIdol, enterResidentialPuddleAgain, destroyTentacles2, activateBlackstoneFragment3, getBlueShadowKeyRealRealm, recallDevices,
			placeShadowBlockerWestResidential, enterResidentialWestPuddle, openPubDoor, takeSuperiorTorchSchematic,
			activateBlackstoneFragment4, bringKetlaTheSuperiorTorchSchematic, claimSuperiorShadowTorch, enterSciencePuddle2,
			getAnimaPortalSchematic, activateBlackstoneFragment5, bringKetlaTheAnimaPortalSchematic, claimAnimaPortal, enterPlazaPuddle,
			destroyTentacles4, activateBlackstoneFragment6, takeWhiteShadowKey, placeBlockerWhiteChest, placeAnimaWhiteChest,
			placeIdolWhiteChest, enterPlazaPuddle2, lightBraziers, openFinalChest, activateBlackstoneFragment7,
			bringKetlaThePerfectSchematic);
	}

	protected List<QuestStep> getDisplayStepsSilentChoir()
	{
		return QuestUtil.toArrayList(enterPuddleNearPub, goUpstairsPub, touchPubRemnant, activateBlackstoneFragment8, enterPuddleNearPub2,
			destroyTentacles5, destroyTentacles6, activateBlackstoneFragment9, getRedShadowKey, placeBlockerInRedRoom, enterPuddleNearPub3, openRedChest,
			activateBlackstoneFragment10, enterSciencePuddle3, destroyTentacles7, openGreenChest, activateBlackstoneFragment11, makeIcon, enterDrain, useIconInDrain,
			goDownDrainLadder, inspectPillar, talkToMe, talkToKetlaAfterVision, claimPerfectShadowTorch, enterPuddleNearPub4, destroyCathedralTentacles,
			activateBlackstoneFragment12, fightWhispererSidebar, searchEntrails, returnToDesertWithWhisperersMedallion, useWhisperersMedallionOnStatue);
	}
}
