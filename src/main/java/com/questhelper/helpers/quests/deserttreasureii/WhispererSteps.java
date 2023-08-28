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
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.location.TileIsLoadedRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;


public class WhispererSteps extends ConditionalStep
{
	DetailedQuestStep enterRuinsOfCamdozaal, talkToRamarno, talkToPrescott, attachRope, descendDownRope,
		activateTeleporter1, activateTeleporter2, activateTeleporter3, activateTeleporter4, activateTeleporter5,
		activateTeleporter6, activateTeleporter7,
		takeShadowBlockerSchematic, takeGreenShadowKey, takePurpleShadowKey, tryToEnterSunkenCathedral, talkToKetla,
		giveKetlaBlockerSchematic, claimShadowBlocker, enterSciencePuddle, retrieveShadowBlocker, placeBlockerInFurnaceBuilding,
		unlockDoor, takeShadowTorchSchematic, activateBlackstoneFragment, bringKetlaTheBasicTorchSchematic, claimShadowTorch,
		enterResedentialPuddle, destroyTentacles, activateBlackstoneFragment2, takeRevitalisingIdolSchematic, bringKetlaTheIdolSchematic,
		claimRevitalisingIdol, placeIdol, enterResedentialPuddleAgain, destroyTentacles2, getBlueShadowKeyShadowRealm,
		getBlueShadowKeyRealRealm, activateBlackstoneFragment3, recallDevices, placeShadowBlockerWestResidential,
		enterResedentialWestPuddle, openPubDoor, takeSuperiorTorchSchematic, bringKetlaTheSuperiorTorchSchematic,
		activateBlackstoneFragment4, takeSuperiorTorchSchematicRealWorld, claimSuperiorShadowTorch, enterSciencePuddle2,
		getAnimaPortalSchematic;

	ItemRequirement magicCombatGear, food, prayerPotions, staminaPotions, nardahTeleport, ringOfVisibility, lassarTeleport;

	ItemRequirement whisperersMedallion, veryLongRope, shadowBlockerSchematic, greenShadowKey, purpleShadowKey, shadowBlocker,
		basicShadowTorchSchematic, blackstoneFragment, basicShadowTorch, revitalisingIdolSchematic, revitalisingIdol, blueShadowKey,
		superiorTorchSchematic, animaPortalSchematic, animaPortal, superiorTorch;

	FreeInventorySlotRequirement freeSlot;

	Requirement inVault, inCamdozaal, talkedToRamarno, talkedToPrescott, ropeAttached, inLassar,
		activatedTeleporter1, activatedTeleporter2, activatedTeleporter3, activatedTeleporter4,
		activatedTeleporter5, activatedTeleporter6, activatedTeleporter7, passedOutAtCathedral, finishedTalkingToKetla,
		givenShadowBlockerSchematic, blockerNearby, blockerPlacedAtDoor, inLassarShadowRealm, purpleKeyTaken, inFurnaceHouse,
		givenTorchSchematic, destroyedTentacles, givenIdolSchematic, idolPlaced, destroyedTentacles2, blockerPlacedAtPub,
		inPubShadowRealm, usedBlueKey, givenSuperiorTorchSchematic;

	Requirement hadGreenKey, hadPurpleKey, hadShadowBlockerSchematic;

	Zone vault, camdozaal, lassar, lassarShadowRealm, furnaceHouse, externalFurnaceHouse, pub;

	public WhispererSteps(QuestHelper questHelper, QuestStep defaultStep)
	{
		super(questHelper, defaultStep);
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Conditions activatedFirst6Teles = new Conditions(activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, activatedTeleporter5, activatedTeleporter6);

		/* Locked door steps */
		ConditionalStep lockedDoorSteps = new ConditionalStep(getQuestHelper(), takePurpleShadowKey);
		lockedDoorSteps.addStep(and(basicShadowTorchSchematic),
			bringKetlaTheBasicTorchSchematic);
		lockedDoorSteps.addStep(and(inLassarShadowRealm, basicShadowTorchSchematic),
			activateBlackstoneFragment);
		lockedDoorSteps.addStep(and(inFurnaceHouse, nor(givenTorchSchematic)), takeShadowTorchSchematic);
		lockedDoorSteps.addStep(and(inLassarShadowRealm, blockerPlacedAtDoor), unlockDoor);
		lockedDoorSteps.addStep(and(hadPurpleKey, blockerPlacedAtDoor), enterSciencePuddle);
		lockedDoorSteps.addStep(and(hadPurpleKey, blockerNearby), retrieveShadowBlocker);
		lockedDoorSteps.addStep(and(hadPurpleKey, shadowBlocker), placeBlockerInFurnaceBuilding);
		lockedDoorSteps.addStep(and(hadPurpleKey), claimShadowBlocker);

		ConditionalStep blueKeySteps = new ConditionalStep(getQuestHelper(), claimRevitalisingIdol);
		blueKeySteps.addStep(and(inLassarShadowRealm, blueShadowKey), activateBlackstoneFragment3);
		blueKeySteps.addStep(and(inLassarShadowRealm, destroyedTentacles2), getBlueShadowKeyShadowRealm);
		blueKeySteps.addStep(and(inLassar, destroyedTentacles2), getBlueShadowKeyRealRealm);
		blueKeySteps.addStep(and(inLassarShadowRealm), destroyTentacles2);
		blueKeySteps.addStep(and(idolPlaced, basicShadowTorch), enterResedentialPuddleAgain);
		blueKeySteps.addStep(and(revitalisingIdol, basicShadowTorch, activatedTeleporter7), placeIdol);
		blueKeySteps.addStep(and(revitalisingIdol, basicShadowTorch), activateTeleporter7);

		ConditionalStep pubSteps = new ConditionalStep(getQuestHelper(), recallDevices);
		pubSteps.addStep(new Conditions(inLassar, superiorTorchSchematic), bringKetlaTheSuperiorTorchSchematic);
		pubSteps.addStep(new Conditions(inLassarShadowRealm, superiorTorchSchematic), activateBlackstoneFragment4);
		pubSteps.addStep(new Conditions(inLassar, usedBlueKey), takeSuperiorTorchSchematicRealWorld);
		pubSteps.addStep(new Conditions(inLassarShadowRealm, usedBlueKey), takeSuperiorTorchSchematic);
		pubSteps.addStep(new Conditions(inLassarShadowRealm, blockerPlacedAtPub), openPubDoor);
		pubSteps.addStep(blockerPlacedAtPub, enterResedentialWestPuddle);
		pubSteps.addStep(shadowBlocker, placeShadowBlockerWestResidential);

		ConditionalStep getAnimaPortalSteps = new ConditionalStep(getQuestHelper(), claimSuperiorShadowTorch);
		getAnimaPortalSteps.addStep(and(superiorTorch, inLassarShadowRealm), getAnimaPortalSchematic);
		getAnimaPortalSteps.addStep(superiorTorch, enterSciencePuddle2);

		/* Top steps */
		addStep(and(or(inLassar, inLassarShadowRealm), givenSuperiorTorchSchematic), getAnimaPortalSteps);
		addStep(and(or(inLassar, inLassarShadowRealm), or(blueShadowKey, usedBlueKey)), pubSteps);
		addStep(and(or(inLassar, inLassarShadowRealm), givenIdolSchematic), blueKeySteps);

		addStep(and(inLassar, revitalisingIdolSchematic), bringKetlaTheIdolSchematic);
		addStep(and(inLassar, basicShadowTorch, destroyedTentacles), takeRevitalisingIdolSchematic);
		addStep(and(inLassarShadowRealm, basicShadowTorch, destroyedTentacles), activateBlackstoneFragment2);
		addStep(and(inLassarShadowRealm, basicShadowTorch), destroyTentacles);
		addStep(and(inLassar, basicShadowTorch), enterResedentialPuddle);
		addStep(and(inLassar, givenTorchSchematic), claimShadowTorch);

		addStep(and(or(inLassar, inLassarShadowRealm), activatedFirst6Teles, givenShadowBlockerSchematic), lockedDoorSteps);


		addStep(and(inLassar, activatedFirst6Teles, hadShadowBlockerSchematic, hadGreenKey, hadPurpleKey, finishedTalkingToKetla),
			giveKetlaBlockerSchematic);
		addStep(and(inLassar, activatedFirst6Teles, hadShadowBlockerSchematic, hadGreenKey, hadPurpleKey, passedOutAtCathedral),
			talkToKetla);
		addStep(and(inLassar, activatedFirst6Teles, hadShadowBlockerSchematic, hadGreenKey, hadPurpleKey), tryToEnterSunkenCathedral);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, activatedTeleporter5, hadShadowBlockerSchematic, hadGreenKey, hadPurpleKey), activateTeleporter6);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			activatedTeleporter4, hadShadowBlockerSchematic, hadGreenKey, hadPurpleKey), activateTeleporter5);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			hadShadowBlockerSchematic, hadGreenKey, hadPurpleKey), activateTeleporter4);
		addStep(and(inLassar, activatedTeleporter1, activatedTeleporter2, activatedTeleporter3,
			hadShadowBlockerSchematic, hadGreenKey), takePurpleShadowKey);
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
		ringOfVisibility = new ItemRequirement("Ring of visibility", ItemID.RING_OF_VISIBILITY);
		magicCombatGear = new ItemRequirement("Magic combat gear", -1, -1);
		magicCombatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		food = new ItemRequirement("Food, as much as you can bring", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);

		lassarTeleport = new ItemRequirement("Mind altar or lassar teleport", ItemID.MIND_ALTAR_TELEPORT);
		lassarTeleport.addAlternates(ItemID.LASSAR_TELEPORT);

		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(5672, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_4);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_3, ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_2);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		freeSlot = new FreeInventorySlotRequirement(1);

		/* Quest items */
		whisperersMedallion = new ItemRequirement("Whisperer's medallion", ItemID.WHISPERERS_MEDALLION);
		veryLongRope = new ItemRequirement("Very long rope", ItemID.VERY_LONG_ROPE);
		shadowBlockerSchematic = new ItemRequirement("Shadow blocker schematic", ItemID.SHADOW_BLOCKER_SCHEMATIC);
		greenShadowKey = new ItemRequirement("Shadow key", ItemID.SHADOW_KEY_28374);
		purpleShadowKey = new ItemRequirement("Shadow key", ItemID.SHADOW_KEY);
		shadowBlocker = new ItemRequirement("Shadow blocker", ItemID.SHADOW_BLOCKER);
		basicShadowTorchSchematic = new ItemRequirement("Basic shadow torch schematic", ItemID.BASIC_SHADOW_TORCH_SCHEMATIC);
		blackstoneFragment = new ItemRequirement("Blackstone fragment", ItemID.BLACKSTONE_FRAGMENT_28357);
		basicShadowTorch = new ItemRequirement("Basic shadow torch", ItemID.BASIC_SHADOW_TORCH);
		revitalisingIdolSchematic = new ItemRequirement("Revitalising idol schematic", ItemID.REVITALISING_IDOL_SCHEMATIC);
		revitalisingIdol = new ItemRequirement("Revitalising idol", ItemID.REVITALISING_IDOL);
		blueShadowKey = new ItemRequirement("Shadow key", ItemID.SHADOW_KEY_28371);
		superiorTorchSchematic = new ItemRequirement("Superior shadow torch schematic", ItemID.SUPERIOR_SHADOW_TORCH_SCHEMATIC);
		superiorTorch = new ItemRequirement("Superior shadow torch", ItemID.SUPERIOR_SHADOW_TORCH);

		animaPortalSchematic = new ItemRequirement("Anima portal schematic", ItemID.ANIMA_PORTAL_SCHEMATIC);
		animaPortal = new ItemRequirement("Anima portal", ItemID.ANIMA_PORTAL);
	}

	protected void setupZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		camdozaal = new Zone(new WorldPoint(2897, 5757, 0), new WorldPoint(3047, 5869, 0));
		lassar = new Zone(new WorldPoint(2558, 6269, 0), new WorldPoint(2755, 6475, 3));
		lassarShadowRealm = new Zone(new WorldPoint(2284, 6262, 0), new WorldPoint(2501, 6476, 3));
		furnaceHouse = new Zone(new WorldPoint(2345, 6353, 0), new WorldPoint(2360, 6366, 3));
		externalFurnaceHouse = new Zone(new WorldPoint(2345, 6357, 0), new WorldPoint(2350, 6362, 0));
		pub = new Zone(new WorldPoint(2371, 6421, 0), new WorldPoint(2384, 6435, 3));
	}

	protected void setupConditions()
	{
		inVault = new ZoneRequirement(vault);
		inCamdozaal = new ZoneRequirement(camdozaal);
		inLassar = new ZoneRequirement(lassar);
		inLassarShadowRealm = new ZoneRequirement(lassarShadowRealm);
		inFurnaceHouse = new Conditions(nor(new ZoneRequirement(externalFurnaceHouse)), new ZoneRequirement(furnaceHouse));
		inPubShadowRealm = new ZoneRequirement(pub);

		int WHISPERER_VARBIT = 15126;

		// Varbit is for learning about the area from the previous quest
		talkedToRamarno = new VarbitRequirement(12068, 2, Operation.GREATER_EQUAL);

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
		blockerNearby = new ObjectCondition(ObjectID.SHADOW_BLOCKER);
		blockerPlacedAtDoor = new Conditions(LogicType.OR,
			new ObjectCondition(ObjectID.SHADOW_BLOCKER,
				new Zone(new WorldPoint(2606, 6359, 0),
					new WorldPoint(2606, 6360, 0))
			),
			new ObjectCondition(ObjectID.SHADOW_BLOCKER_48214,
				new Zone(new WorldPoint(2350, 6359, 0),
					new WorldPoint(2350, 6360, 0)))
		);

		// TODO: Work out a way to determine if the door is unlocked?
		purpleKeyTaken = nor(new ItemOnTileRequirement(ItemID.SHADOW_KEY, new WorldPoint(2593, 6352, 0)));

		hadGreenKey = new Conditions(or(greenShadowKey));
		hadPurpleKey = new Conditions(or(
			purpleShadowKey,
			purpleKeyTaken
		));
		hadShadowBlockerSchematic = new Conditions(or(shadowBlockerSchematic, givenShadowBlockerSchematic));

		// Given basic torch schematic in
		// 15085 0->1
		// 15126 18->20
		// Unsure when 16->18 happened

		givenTorchSchematic = new VarbitRequirement(15085, 1);

		ObjectCondition realWorldTentacleExists = new ObjectCondition(NullObjectID.NULL_48204,
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
				new ObjectCondition(ObjectID.TENTACLE_48205,
					new Zone(new WorldPoint(2417, 6413, 0),
						new WorldPoint(2417, 6414, 0))
				),
			realWorldTentacleExists)
		);

		givenIdolSchematic = new VarbitRequirement(15083, 1);

		idolPlaced = new ObjectCondition(ObjectID.REVITALISING_IDOL, new WorldPoint(2689, 6415, 0));

		ObjectCondition realWorldTentacle2Exists = new ObjectCondition(NullObjectID.NULL_48204,
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
				new ObjectCondition(ObjectID.TENTACLE_48205,
					new Zone(new WorldPoint(2423, 6439, 0), new WorldPoint(2424, 6439, 0))
				),
				realWorldTentacle2Exists
			)
		);

		blockerPlacedAtDoor = new Conditions(LogicType.OR,
			new ObjectCondition(ObjectID.SHADOW_BLOCKER,
				new Zone(new WorldPoint(2641, 6428, 0),
					new WorldPoint(2641, 6427, 0))
			),
			new ObjectCondition(ObjectID.SHADOW_BLOCKER_48214,
				new Zone(new WorldPoint(2385, 6427, 0),
					new WorldPoint(2385, 6428, 0)))
		);

		usedBlueKey = new Conditions(
			true, LogicType.OR,
			inPubShadowRealm,
			new Conditions(
				new TileIsLoadedRequirement(new WorldPoint(2672, 6443, 0)),
				nor(
					new ItemOnTileRequirement(blueShadowKey, new WorldPoint(2672, 6443, 0)),
					blueShadowKey
				)
			)
		);

		// Picked up superior schematics
		// 15126 20->22

		givenSuperiorTorchSchematic = new VarbitRequirement(15086, 1);
		// 22->24

		// Anima portal real world block:
		// 48207, new W(2578, y=6398, 0)
	}

	protected void setupSteps()
	{
		enterRuinsOfCamdozaal = new ObjectStep(getQuestHelper(), NullObjectID.NULL_41357, new WorldPoint(3000, 3494, 0),
			"Enter Camdozaal, west of Ice Mountain.", ringOfVisibility);
		talkToRamarno = new NpcStep(getQuestHelper(), NpcID.RAMARNO_10685, new WorldPoint(2959, 5809, 0),
			"Talk to Ramarno to the north by the sacred forge.");
		talkToRamarno.addDialogStep("Have you seen any archeologists around here?");
		talkToPrescott = new NpcStep(getQuestHelper(), NpcID.PRESCOTT, new WorldPoint(2975, 5794, 0),
			"Talk to Prescott to the east.");

		attachRope = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49477, new WorldPoint(2922, 5827, 0),
			"Tie the rope to a rock in the north west of the cavern, in the mine by the sinkhole.", veryLongRope.highlighted());
		attachRope.addIcon(ItemID.VERY_LONG_ROPE);

		descendDownRope = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49477, new WorldPoint(2922, 5827, 0),
			"Descend into the sinkhole.");

		activateTeleporter1 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49479, new WorldPoint(2593, 6424, 0),
			"Activate the teleporter to the south-east of the rope. You can use teleporters you've activated to go to other activated teleporters.");
		activateTeleporter2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49480, new WorldPoint(2617, 6417, 0),
			"Activate the teleporter further south-east.");
		activateTeleporter3 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49482, new WorldPoint(2611, 6379, 0),
			"Activate the teleporter to the south in the Science District.");
		activateTeleporter4 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49483, new WorldPoint(2599, 6341, 0),
			"Activate the teleporter in the far south of the Science District.");
		activateTeleporter5 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49484, new WorldPoint(2643, 6434, 0),
			"Teleport back to the Plaza, then run east. Activate the teleporter in the north-west of the resedential area.");
		activateTeleporter5.addDialogStep("The Plaza.");
		activateTeleporter6 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49481, new WorldPoint(2652, 6405, 0),
			"Activate the teleporter in the south of the resedential area.");
		activateTeleporter7 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49485, new WorldPoint(2691, 6415, 0),
			"Activate the teleporter in the far east of the resedential area, through the building you unlocked for the idol.");


		takeShadowBlockerSchematic = new ItemStep(getQuestHelper(), new WorldPoint(2590, 6380, 0),
			"Enter the north-western building in the Science District, and take the Shadow Schematic which is on top of a barrel in there.",
			shadowBlockerSchematic);
		takeShadowBlockerSchematic.addDialogStep("Northern Science District.");
		takeGreenShadowKey = new ItemStep(getQuestHelper(), new WorldPoint(2581, 6387, 0),
			"Take the shadow key from the north-western room.", greenShadowKey);
		takeGreenShadowKey.addDialogStep("Northern Science District.");
		takePurpleShadowKey = new ItemStep(getQuestHelper(), new WorldPoint(2593, 6352, 0),
			"Take the shadow key from the building to the south.",
			purpleShadowKey);
		takePurpleShadowKey.addDialogStep("Northern Science District.");

		tryToEnterSunkenCathedral = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2656, 6386, 0),
			"Run south-east to try and enter the Sunken Cathedral.");
		tryToEnterSunkenCathedral.addDialogStep("Western Residential District.");

		talkToKetla = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Talk to Ketla, next to the Western Residential District teleport.", ringOfVisibility, freeSlot);
		talkToKetla.addDialogStep("Western Residential District.");

		giveKetlaBlockerSchematic = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Give the blocker schematic to Ketla, next to the Western Residential District teleport.", shadowBlockerSchematic);
		giveKetlaBlockerSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");

		claimShadowBlocker = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49486, new WorldPoint(2645, 6440, 0),
			"Get the Shadow Blocker from the workbench next to Ketla, or use 'Recall' on your Blackstone fragment to retrieve it.", freeSlot);
		claimShadowBlocker.addDialogSteps("Take it.", "Western Residential District.");

		placeBlockerInFurnaceBuilding = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2606, 6359, 0),
			"Return to the Science District, and enter the building with furnaces in it. Place the Shadow Blocker next to the locked doors inside.",
			shadowBlocker.highlighted());
		placeBlockerInFurnaceBuilding.addDialogStep("Southern Science District.");
		placeBlockerInFurnaceBuilding.addIcon(ItemID.SHADOW_BLOCKER);

		retrieveShadowBlocker = new ObjectStep(getQuestHelper(), ObjectID.SHADOW_BLOCKER, "Pick up the shadow blocker.");
		placeBlockerInFurnaceBuilding.addSubSteps(retrieveShadowBlocker);

		enterSciencePuddle = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49478, new WorldPoint(2598, 6365, 0),
			"Enter the puddle outside of the building with furnaces in it. When you're in the Shadow Realm, you will slowly lose sanity. " +
				"If you're low on Sanity, you can escape using the Blackstone fragment.");
		enterSciencePuddle.addDialogStep("Southern Science District.");

		// Unlocking door uses up purple key
		unlockDoor = new ObjectStep(getQuestHelper(), ObjectID.DOORS_48258, new WorldPoint(2350, 6360, 0),
			"Unlock the door inside the furnace building. " +
			"As long as you're near the Shadow Blocker you placed you will be safe from lost souls.");

		takeShadowTorchSchematic = new ItemStep(getQuestHelper(), new WorldPoint(2353, 6367, 0),
			"Take the basic shadow torch schematic.",
			basicShadowTorchSchematic);

		activateBlackstoneFragment = new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave.",
			blackstoneFragment.highlighted());

		bringKetlaTheBasicTorchSchematic = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Bring the basic shadow torch schematic to Ketla, next to the Western Residential District teleport.",
			basicShadowTorchSchematic);
		bringKetlaTheBasicTorchSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");
		claimShadowTorch = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49486, new WorldPoint(2645, 6440, 0),
			"Get the Shadow Torch from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot);
		claimShadowTorch.addDialogSteps("Take it.", "Western Residential District.", "Take the Shadow Torch.", "Take everything.");
		enterResedentialPuddle = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49478, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla.");
		destroyTentacles = new ObjectStep(getQuestHelper(), ObjectID.TENTACLE_48205, new WorldPoint(2417, 6413, 0),
			"Destroy the tentacles outside the building to the east.");
		activateBlackstoneFragment2 = new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted());

		takeRevitalisingIdolSchematic = new ItemStep(getQuestHelper(), new WorldPoint(2677, 6422, 0),
			"Take the revitalising idol schematic from inside the house.", revitalisingIdolSchematic);

		bringKetlaTheIdolSchematic = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Bring the basic revitalising idol schematic to Ketla, next to the Western Residential District teleport.",
			revitalisingIdolSchematic);
		bringKetlaTheIdolSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");
		claimRevitalisingIdol = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49486, new WorldPoint(2645, 6440, 0),
			"Get the revitalising idol from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot);
		claimRevitalisingIdol.addDialogSteps("Take it.", "Western Residential District.", "Take the Revitalising Idol.", "Take everything.");
		placeIdol = new DetailedQuestStep(getQuestHelper(), new WorldPoint(2689, 6415, 0), "Place the idol by the teleporter.",
			revitalisingIdol.highlighted());

		enterResedentialPuddleAgain = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49478, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla.");

		destroyTentacles2 = new ObjectStep(getQuestHelper(), ObjectID.TENTACLE_48205, new WorldPoint(2424, 6439, 0), "Run around through the building and up north, making sure to restore sanity at the idol." +
			" Destroy the tentacles blocking the door at the end of the path.", basicShadowTorch);

		getBlueShadowKeyRealRealm = new ItemStep(getQuestHelper(), new WorldPoint(2672, 6443, 0), "Grab the shadow key in the room you've just unlocked.", blueShadowKey);
		getBlueShadowKeyShadowRealm = new ItemStep(getQuestHelper(), new WorldPoint(2416, 6443, 0), "Grab the shadow key in the room you've just unlocked.", blueShadowKey);
		getBlueShadowKeyRealRealm.addSubSteps(getBlueShadowKeyShadowRealm);

		activateBlackstoneFragment3 = new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted());
		recallDevices = new DetailedQuestStep(getQuestHelper(), "Use the blackstone fragment to recall all your devices.", blackstoneFragment.highlighted());
		recallDevices.addDialogStep("Yes.");

		placeShadowBlockerWestResidential = new TileStep(getQuestHelper(), new WorldPoint(2641, 6428, 0),
			"Return to the west residential area, and place the shadow blocker at the pub's entrance.", shadowBlocker.highlighted());
		placeShadowBlockerWestResidential.addIcon(ItemID.SHADOW_BLOCKER);
		placeShadowBlockerWestResidential.addDialogSteps("More options...", "Western Residential District.");
		enterResedentialWestPuddle = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49478, new WorldPoint(2665, 6418, 0),
			"Enter the puddle south-east of Ketla.");
		enterResedentialWestPuddle.addDialogSteps("More options...", "Western Residential District.");

		// Can't open, DOORS_48255
		openPubDoor = new ObjectStep(getQuestHelper(), ObjectID.DOORS_48258, new WorldPoint(2385, 6427, 0),
			"Open the pub's doors.");
		// 'You unlock the doors.', DIALOG_SPRITE_TEXT
		// DIALOG_SPRITE_SPRITE, itemID 28371
		takeSuperiorTorchSchematic = new ItemStep(getQuestHelper(), new WorldPoint(2381, 6423, 0),
			"Take the superior torch schematic.", superiorTorchSchematic);
		takeSuperiorTorchSchematicRealWorld = new ItemStep(getQuestHelper(), new WorldPoint(2637, 6423, 0),
			"Take the superior torch schematic.", superiorTorchSchematic);
		takeSuperiorTorchSchematic.addSubSteps(takeSuperiorTorchSchematicRealWorld);
		activateBlackstoneFragment4 = new DetailedQuestStep(getQuestHelper(), "Activate the blackstone fragment to leave the Shadow Realm.",
			blackstoneFragment.highlighted());
		bringKetlaTheSuperiorTorchSchematic = new NpcStep(getQuestHelper(), NpcID.KETLA, new WorldPoint(2648, 6442, 0),
			"Bring the basic superior shadow torch schematic to Ketla, next to the Western Residential District teleport.",
			superiorTorchSchematic);
		bringKetlaTheSuperiorTorchSchematic.addDialogSteps("Western Residential District.", "I have a schematic here.");

		claimSuperiorShadowTorch = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49486, new WorldPoint(2645, 6440, 0),
			"Get the superior shadow torch from the workbench next to Ketla, or recall it with the blackstone fragment.", freeSlot);
		claimSuperiorShadowTorch.addDialogSteps("Take it.", "Western Residential District.", "Take the Superior Shadow Torch.", "Take everything.");

		enterSciencePuddle2 = new ObjectStep(getQuestHelper(), NullObjectID.NULL_49478, new WorldPoint(2598, 6365, 0),
		"Enter the puddle outside of the building with furnaces in it in the Science District.");
		enterSciencePuddle2.addDialogStep("Northern Science District.");
		getAnimaPortalSchematic = new ItemStep(getQuestHelper(), new WorldPoint(2580, 6402, 0), "Go to the north-west of the science district," +
			" and burn the tentacles there to grab the Anima portal schematic.", animaPortalSchematic);
	}

	protected List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(enterRuinsOfCamdozaal, talkToRamarno, talkToPrescott, attachRope, descendDownRope,
			activateTeleporter1, activateTeleporter2, activateTeleporter3, takeShadowBlockerSchematic, takeGreenShadowKey,
			takePurpleShadowKey, activateTeleporter4, activateTeleporter5, activateTeleporter6, tryToEnterSunkenCathedral, talkToKetla,
			giveKetlaBlockerSchematic, claimShadowBlocker, placeBlockerInFurnaceBuilding, enterSciencePuddle, unlockDoor,
			takeShadowTorchSchematic, activateBlackstoneFragment, bringKetlaTheBasicTorchSchematic, claimShadowTorch,
			enterResedentialPuddle, destroyTentacles, activateBlackstoneFragment2, takeRevitalisingIdolSchematic,
			bringKetlaTheIdolSchematic, takeRevitalisingIdolSchematic, bringKetlaTheIdolSchematic, claimRevitalisingIdol, claimShadowTorch,
			placeIdol, enterResedentialPuddleAgain, destroyTentacles2, getBlueShadowKeyRealRealm, recallDevices,
			placeShadowBlockerWestResidential, enterResedentialWestPuddle, openPubDoor, takeSuperiorTorchSchematic,
			activateBlackstoneFragment4, bringKetlaTheSuperiorTorchSchematic, claimSuperiorShadowTorch, enterSciencePuddle2,
			getAnimaPortalSchematic);
	}
}
