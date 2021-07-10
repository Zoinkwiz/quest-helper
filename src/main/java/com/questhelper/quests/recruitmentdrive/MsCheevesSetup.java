/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.quests.recruitmentdrive;

import com.questhelper.Zone;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class MsCheevesSetup
{
	private QuestHelper questHelper;

	@Getter
	private ZoneRequirement isInMsCheeversRoom;

	@Getter
	private ConditionalStep conditionalStep;

	private ObjectStep getMagnetStep, getTwoVials, getCupricSulfate,
		getGypsum, getSodiumChloride, getWire, getTin, getShears,
		getChisel, getNitrousOxide, getTinOrePowder, getCupricOrePowder, getThreeVials,
		getKnife, leaveRoom;
	private ItemStep getMetalSpade;

	// TODO: Clean up to just use ItemRequirement
	private Requirement hasMagnet, hasAceticAcid, hasOneVialOfLiquid,
		hasCupricSulfate, hasGypsum, hasSodiumChloride, hasWire, hasTin,
		hasShears, hasChisel, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
		hasKnife, hasMetalSpade, hasMetalSpadeHead, hasAshes, hasBronzeKey;

	private VarbitRequirement finishedRoom;

	// Destroying first door steps
	private QuestStep useSpadeOnBunsenBurner, useSpadeHeadOnDoor, useCupricSulfateOnDoor, useVialOfLiquidOnDoor, openDoor;
	private VarbitRequirement hasRetrievedThreeVials, hasSpadeHeadOnDoor, hasCupricSulfateOnDoor, hasVialOfLiquidOnDoor, hasFirstDoorOpen;
	private ItemRequirement metalSpade, metalSpadeHead, ashes, cupricSulfate, vialOfLiquid;

	// Creating second door key steps
	private QuestStep useVialOfLiquidOnCakeTin, useGypsumOnTin, useTinOnKey, useCupricOrePowderOnTin, useTinOrePowderOnTin, useTinOnBunsenBurner, useEquipmentOnTin;
	private ItemRequirement tin, gypsumTin, gypsum, tinKeyPrint, cupricOrePowder, tinOrePowder, tinWithCupricOre,
		tinWithTinOre, tinWithAllOre, bronzeKey, knife, chisel, bronzeWire;
	private VarbitRequirement hasLiquidInTin;
	ItemRequirement hasGypsumTin, hasTinKeyPrint, hasTinCupricOre, hasTinWithTinOre, hasTinWithAllOre;

	private final int VARBIT_NITROUS_OXIDE = 5581;
	private final int VARBIT_VIAL_OF_LIQUID = 671;
	private final int VARBIT_ACETIC_ACIDE = 672;
	private final int VARBIT_CUPRIC_SULFATE = 673;
	private final int VARBIT_GYPSUM = 674;
	private final int VARBIT_SODIUM_CHLORIDE = 675;
	private final int VARBIT_NITROUS_OXIDE_RETRIEVED = 676;
	private final int VARBIT_TIN_ORE_POWDER = 677;
	private final int VARBIT_CUPRIC_ORE_POWDER = 678;
	private final int VARBIT_THREE_VIALS = 679; //(0 -> 3)

	private final int VARBIT_SPADEHEAD_ON_DOOR = 686; //1
	private final int VARBIT_USE_CUPRIC_SULFATE_ON_DOOR = 687;
	private final int VARBIT_VIAL_OF_LIQUID_ON_DOOR = 686; // 2
	private final int VARBIT_FIRST_DOOR_OPEN = 686; //3

	private final int VARBIT_LIQUID_IN_TIN = 689;

	private final int VARBIT_COMPLETE_ROOM = 664;

	public MsCheevesSetup(QuestHelper questHelper)
	{
		this.questHelper = questHelper;
		setupRequirements();
		setupZoneCondition();
		setupConditions();
		setupSteps();
		addSteps();
	}

	private void setupSteps()
	{
		getMagnetStep = new ObjectStep(questHelper, ObjectID.OLD_BOOKSHELF_7327, "Get the magnet from the old bookshelf.");
		getTwoVials = new ObjectStep(questHelper, ObjectID.SHELVES_7333, "Get two vials from the shelves");
		getTwoVials.addDialogStep("Take both vials.");
		getCupricSulfate = new ObjectStep(questHelper, ObjectID.SHELVES_7334, "Get Cupric Sulfate from the shelves.");
		getCupricSulfate.addDialogStep("YES");
		getGypsum = new ObjectStep(questHelper, ObjectID.SHELVES_7335, "Get Gypsum from the shelves.");
		getGypsum.addDialogStep("YES");
		getSodiumChloride = new ObjectStep(questHelper, ObjectID.SHELVES_7336, "Get Sodium Chloride from the shelves.");
		getSodiumChloride.addDialogStep("YES");
		getWire = new ObjectStep(questHelper, ObjectID.CRATE_7349, new WorldPoint(2475, 4943, 0), "Get Wire from the crate.");
		getTin = new ObjectStep(questHelper, ObjectID.CRATE_7347, new WorldPoint(2476, 4943, 0), "Get Tin from the crate.");
		getShears = new ObjectStep(questHelper, ObjectID.CLOSED_CHEST_7350, "Get Shears from the chest.");
		getShears.addAlternateObjects(ObjectID.OPEN_CHEST_7351);
		getChisel = new ObjectStep(questHelper, ObjectID.CRATE_7348, new WorldPoint(2476, 4937, 0), "Get a Chisel from the crate.");
		getNitrousOxide = new ObjectStep(questHelper, ObjectID.SHELVES_7337, "Get Nitrous Oxide from the Shelves.");
		getNitrousOxide.addDialogStep("YES");
		getTinOrePowder = new ObjectStep(questHelper, ObjectID.SHELVES_7338, "Get Tin Ore Powder from the Shelves.");
		getTinOrePowder.addDialogStep("YES");
		getCupricOrePowder = new ObjectStep(questHelper, ObjectID.SHELVES_7339, "Get Curpic Ore Powder from the Shelves.");
		getCupricOrePowder.addDialogStep("YES");
		getThreeVials = new ObjectStep(questHelper, ObjectID.SHELVES_7340, "Get Three Vials Of Liquid from the Shelves.");
		getThreeVials.addDialogStep("Take all three vials");
		getKnife = new ObjectStep(questHelper, ObjectID.OLD_BOOKSHELF_7329, "Get a Knife from the old bookshelf.");
		getMetalSpade = new ItemStep(questHelper, "Get the metal spade off the table", metalSpade);

		useSpadeOnBunsenBurner = new ObjectStep(questHelper, ObjectID.BUNSEN_BURNER, "Use the spade in your inventory on the bunsen burner"
			, metalSpade);
		useSpadeOnBunsenBurner.addIcon(ItemID.METAL_SPADE);

		useSpadeHeadOnDoor = new ObjectStep(questHelper, 7342, "Use the spade in your inventory on the door.",
			metalSpadeHead);
		useSpadeHeadOnDoor.addIcon(ItemID.METAL_SPADE_5587);

		useCupricSulfateOnDoor = new ObjectStep(questHelper, 7342, "Use Cupric Sulfate in your inventory on the door.",
			cupricSulfate);
		useCupricSulfateOnDoor.addIcon(ItemID.CUPRIC_SULFATE);

		useVialOfLiquidOnDoor = new ObjectStep(questHelper, 7342, "Use vial of liquid in your inventory on the door.",
			vialOfLiquid);
		useVialOfLiquidOnDoor.addIcon(ItemID.VIAL_OF_LIQUID);

		openDoor = new ObjectStep(questHelper, 7342, "Open the door.");

		useVialOfLiquidOnCakeTin = new DetailedQuestStep(questHelper, "Use a vial of liquid on the tin in your inventory.",
			tin, vialOfLiquid);

		useGypsumOnTin = new DetailedQuestStep(questHelper, "Use a vial of Gypsum on the tin in your inventory.",
			gypsum, tin);

		useTinOnKey = new ObjectStep(questHelper, ObjectID.KEY, "Use tin full with Gypsum on the key on the ground.",
			gypsumTin);
		useTinOnKey.addIcon(ItemID.TIN_5593);

		useCupricOrePowderOnTin = new DetailedQuestStep(questHelper, "Use Tin on the cupric ore powder in your inventory.",
			tinKeyPrint, cupricOrePowder);

		useTinOrePowderOnTin = new DetailedQuestStep(questHelper, "Use Tin on the tin ore powder in your inventory.",
			tinWithCupricOre, tinOrePowder);

		useTinOnBunsenBurner = new ObjectStep(questHelper, ObjectID.BUNSEN_BURNER,
			"Use your tin with the bunsen burner to create a bronze key.", tinWithTinOre);
		useTinOnBunsenBurner.addIcon(ItemID.TIN_5597);

		useEquipmentOnTin = new DetailedQuestStep(questHelper, "Use your chisel,knife or bronze wires on your tin in your inventory.",
			tinWithAllOre, chisel, knife, bronzeWire);

		leaveRoom = new ObjectStep(questHelper, 7326, new WorldPoint(2478, 4940, 0), "Leave the room by the second door to enter the portal");
	}

	private void addSteps()
	{
		conditionalStep = new ConditionalStep(questHelper, getMagnetStep);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasBronzeKey, finishedRoom), leaveRoom);
		setupSecondDoorKeyStep();
		destroyFirstDoorSteps();
		retrieveItemSteps();
	}

	public List<QuestStep> GetPanelSteps()
	{
		List<QuestStep> steps = new ArrayList<>();
		steps.add(getMagnetStep);
		steps.add(getTwoVials);
		steps.add(getCupricSulfate);
		steps.add(getGypsum);
		steps.add(getSodiumChloride);
		steps.add(getWire);
		steps.add(getTin);
		steps.add(getShears);
		steps.add(getChisel);
		steps.add(getNitrousOxide);
		steps.add(getTinOrePowder);
		steps.add(getCupricOrePowder);
		steps.add(getThreeVials);
		steps.add(getKnife);
		steps.add(getMetalSpade);
		steps.add(useSpadeOnBunsenBurner);
		steps.add(useSpadeHeadOnDoor);
		steps.add(useCupricSulfateOnDoor);
		steps.add(useVialOfLiquidOnDoor);
		steps.add(openDoor);
		steps.add(useVialOfLiquidOnCakeTin);
		steps.add(useGypsumOnTin);
		steps.add(useTinOnKey);
		steps.add(useCupricOrePowderOnTin);
		steps.add(useTinOrePowderOnTin);
		steps.add(useTinOnBunsenBurner);
		steps.add(useEquipmentOnTin);
		steps.add(leaveRoom);
		return steps;
	}

	/***
	 * 	Steps and conditions required to create the key for the second door.
	 */
	private void setupSecondDoorKeyStep()
	{
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasTinWithAllOre), useEquipmentOnTin);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasTinWithTinOre), useTinOnBunsenBurner);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasTinCupricOre), useTinOrePowderOnTin);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasTinKeyPrint), useCupricOrePowderOnTin);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasGypsumTin), useTinOnKey);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen, hasLiquidInTin), useGypsumOnTin);
		conditionalStep.addStep(new Conditions(hasFirstDoorOpen), useVialOfLiquidOnCakeTin);
	}

	/***
	 * 	Steps and conditions required to destroy the first door.
	 */
	private void destroyFirstDoorSteps()
	{
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasAshes, hasVialOfLiquidOnDoor), openDoor);

		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasAshes, hasCupricSulfateOnDoor), useVialOfLiquidOnDoor);

		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasAshes, hasSpadeHeadOnDoor), useCupricSulfateOnDoor);

		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasMetalSpadeHead, hasAshes), useSpadeHeadOnDoor);

		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasMetalSpade), useSpadeOnBunsenBurner);
	}

	/**
	 * First stage to retrieve all required items to pass the room.
	 */
	private void retrieveItemSteps()
	{
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife), getMetalSpade);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials), getKnife);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
				hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder)
			, getThreeVials);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder), getCupricOrePowder);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide), getTinOrePowder);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasChisel), getNitrousOxide);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears), getChisel);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin), getShears);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire), getTin);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride), getWire);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum),
			getSodiumChloride);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate), getGypsum);
		conditionalStep.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid), getCupricSulfate);
		conditionalStep.addStep(hasMagnet, getTwoVials);
	}

	private void setupZoneCondition()
	{
		Zone missCheevesZone = new Zone(new WorldPoint(2466, 4936, 0),
			new WorldPoint(2480, 4947, 0));
		isInMsCheeversRoom = new ZoneRequirement(missCheevesZone);
	}

	private void setupRequirements()
	{
		metalSpade = new ItemRequirement("Metal Spade", ItemID.METAL_SPADE);
		metalSpade.setTooltip("If you are missing this item pick another up off the table.");
		metalSpade.setHighlightInInventory(true);
		metalSpadeHead = new ItemRequirement("Metal Spade", ItemID.METAL_SPADE_5587);
		metalSpadeHead.setHighlightInInventory(true);
		metalSpadeHead.setTooltip("If you are missing this item pick up a metal spade off the table and use it on the bunsen burner.");
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		ashes.setHighlightInInventory(true);
		ashes.setTooltip("If you are missing this item pick up a metal spade off the table and use it on the bunsen burner.");
		cupricSulfate = new ItemRequirement("Cupric Sulfate", ItemID.CUPRIC_SULFATE);
		cupricSulfate.setHighlightInInventory(true);
		cupricSulfate.setTooltip("Take from the shelves on the north side");

		vialOfLiquid = new ItemRequirement("Vial of Liquid", ItemID.VIAL_OF_LIQUID);
		vialOfLiquid.setHighlightInInventory(true);
		vialOfLiquid.setTooltip("Take from the shelf on the north side or the south side.");

		tin = new ItemRequirement("Tin", ItemID.TIN);
		tin.setHighlightInInventory(true);
		//TODO set tip

		gypsumTin = new ItemRequirement("Tin", ItemID.TIN_5593);
		gypsumTin.setHighlightInInventory(true);

		gypsum = new ItemRequirement("Gypsum", ItemID.GYPSUM);
		gypsum.setHighlightInInventory(true);

		tinKeyPrint = new ItemRequirement("Tin", ItemID.TIN_5594);
		tinKeyPrint.setHighlightInInventory(true);

		tinWithCupricOre = new ItemRequirement("Tin", ItemID.TIN_5596);
		tinWithCupricOre.setHighlightInInventory(true);

		cupricOrePowder = new ItemRequirement("Cupric Ore Powder", ItemID.CUPRIC_ORE_POWDER);
		cupricOrePowder.setHighlightInInventory(true);

		tinOrePowder = new ItemRequirement("Tin Ore Powder", ItemID.TIN_ORE_POWDER);
		tinOrePowder.setHighlightInInventory(true);

		tinWithTinOre = new ItemRequirement("Tin", ItemID.TIN_5597);
		tinWithTinOre.setHighlightInInventory(true);
		//		duplicateBronzeKey = new ItemRequirement("Duplicate bronze key", ItemID.BRONZE_KEY);

		tinWithAllOre = new ItemRequirement("Tin", ItemID.TIN_5598);
		tinWithAllOre.setHighlightInInventory(true);

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL_5601);
		chisel.setHighlightInInventory(true);

		bronzeWire = new ItemRequirement("Bronze Wire", ItemID.BRONZE_WIRE_5602);
		bronzeWire.setHighlightInInventory(true);

		knife = new ItemRequirement("Knife", ItemID.KNIFE_5605);
		knife.setHighlightInInventory(true);

		bronzeKey = new ItemRequirement("Bronze Key", ItemID.BRONZE_KEY_5585);
	}

	private void setupConditions()
	{
		hasMagnet = new ItemRequirements(new ItemRequirement("Magnet", ItemID.MAGNET_5604));
		hasAceticAcid = new ItemRequirements(new ItemRequirement("Acetic Acid", ItemID.ACETIC_ACID));
		hasOneVialOfLiquid = vialOfLiquid;
		hasCupricSulfate = cupricSulfate;
		hasGypsum = gypsum;
		hasSodiumChloride = new ItemRequirements(new ItemRequirement("Sodium Chloride", ItemID.SODIUM_CHLORIDE));
		hasTin = tin;
		hasWire = bronzeWire;
		hasShears = new ItemRequirements(new ItemRequirement("Shears", ItemID.SHEARS_5603));
		hasChisel = chisel;
		hasNitrousOxide = new ItemRequirements(new ItemRequirement("Nitrous Oxide", ItemID.NITROUS_OXIDE));
		hasTinOrePowder = tinOrePowder;
		hasCupricOrePowder = cupricOrePowder;
		hasKnife = knife;
		hasMetalSpade = metalSpade;
		hasMetalSpadeHead = metalSpadeHead;
		hasAshes = ashes;

		hasGypsumTin = gypsumTin;
		hasTinKeyPrint = tinKeyPrint;
		hasTinCupricOre = tinWithCupricOre;
		hasTinWithTinOre = tinWithTinOre;
		hasTinWithAllOre = tinWithAllOre;

		hasBronzeKey = bronzeKey;

		hasRetrievedThreeVials = new VarbitRequirement(VARBIT_THREE_VIALS, 3);
		hasSpadeHeadOnDoor = new VarbitRequirement(VARBIT_SPADEHEAD_ON_DOOR, 1);
		hasCupricSulfateOnDoor = new VarbitRequirement(VARBIT_USE_CUPRIC_SULFATE_ON_DOOR, 1);
		hasVialOfLiquidOnDoor = new VarbitRequirement(VARBIT_VIAL_OF_LIQUID_ON_DOOR, 2);
		hasFirstDoorOpen = new VarbitRequirement(VARBIT_FIRST_DOOR_OPEN, 3);
		finishedRoom = new VarbitRequirement(VARBIT_COMPLETE_ROOM, 1);

		hasLiquidInTin = new VarbitRequirement(VARBIT_LIQUID_IN_TIN, 1);
	}
}
