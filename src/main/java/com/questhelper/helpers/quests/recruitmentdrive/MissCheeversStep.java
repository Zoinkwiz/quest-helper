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
package com.questhelper.helpers.quests.recruitmentdrive;

import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.List;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

@SuppressWarnings("FieldCanBeLocal")
public class MissCheeversStep extends ConditionalStep
{
	// Requirements
	ItemRequirement hasGypsumTin;
	ItemRequirement hasTinKeyPrint;
	ItemRequirement hasTinCupricOre;
	ItemRequirement hasTinWithTinOre;
	ItemRequirement hasTinWithAllOre;
	ItemRequirement hasMagnet;
	ItemRequirement hasAceticAcid;
	ItemRequirement hasOneVialOfLiquid;
	ItemRequirement hasCupricSulfate;
	ItemRequirement hasGypsum;
	ItemRequirement hasSodiumChloride;
	ItemRequirement hasWire;
	ItemRequirement hasTin;
	ItemRequirement hasShears;
	ItemRequirement hasChisel;
	ItemRequirement hasNitrousOxide;
	ItemRequirement hasTinOrePowder;
	ItemRequirement hasCupricOrePowder;
	ItemRequirement hasKnife;
	ItemRequirement hasMetalSpade;
	ItemRequirement hasMetalSpadeHead;
	ItemRequirement hasAshes;
	ItemRequirement hasBronzeKey;

	ItemRequirement metalSpade;
	ItemRequirement metalSpadeHead;
	ItemRequirement ashes;
	ItemRequirement cupricSulfate;
	ItemRequirement vialOfLiquid;
	VarbitRequirement hasRetrievedThreeVials;
	VarbitRequirement hasSpadeHeadOnDoor;
	VarbitRequirement hasCupricSulfateOnDoor;
	VarbitRequirement hasVialOfLiquidOnDoor;
	VarbitRequirement hasFirstDoorOpen;

	ItemRequirement tin;
	ItemRequirement gypsumTin;
	ItemRequirement gypsum;
	ItemRequirement tinKeyPrint;
	ItemRequirement cupricOrePowder;
	ItemRequirement tinOrePowder;
	ItemRequirement tinWithCupricOre;
	ItemRequirement tinWithTinOre;
	ItemRequirement tinWithAllOre;
	ItemRequirement bronzeKey;
	ItemRequirement knife;
	ItemRequirement chisel;
	ItemRequirement bronzeWire;
	VarbitRequirement hasLiquidInTin;

	VarbitRequirement finishedRoom;

	// Steps
	ObjectStep getMagnet;
	ObjectStep getTwoVials;
	ObjectStep getCupricSulfate;
	ObjectStep getGypsum;
	ObjectStep getSodiumChloride;
	ObjectStep getWire;
	ObjectStep getTin;
	ObjectStep getShears;
	ObjectStep getChisel;
	ObjectStep getNitrousOxide;
	ObjectStep getTinOrePowder;
	ObjectStep getCupricOrePowder;
	ObjectStep getThreeVials;
	ObjectStep getKnife;
	ObjectStep leaveRoom;
	ItemStep getMetalSpade;

	// Destroying first door steps
	QuestStep useSpadeOnBunsenBurner;
	QuestStep useSpadeHeadOnDoor;
	QuestStep useCupricSulfateOnDoor;
	QuestStep useVialOfLiquidOnDoor;
	QuestStep openDoor;
	// Creating second door key steps
	QuestStep useVialOfLiquidOnCakeTin;
	QuestStep useGypsumOnTin;
	QuestStep useTinOnKey;
	QuestStep useCupricOrePowderOnTin;
	QuestStep useTinOrePowderOnTin;
	QuestStep useTinOnBunsenBurner;
	QuestStep useEquipmentOnTin;

	public MissCheeversStep(QuestHelper questHelper, Requirement... requirements)
	{
		// null safety - addSteps sets the null conditional step
		super(questHelper, null, requirements);

		setupRequirements();
		setupSteps();
		addSteps();
	}

	private void setupSteps()
	{
		getMagnet = new ObjectStep(questHelper, ObjectID.RD_BOOKSHELF_OLD_TALL, "Get the magnet from the old bookshelf.");
		getTwoVials = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_1, "Get two vials from the shelves");
		getTwoVials.addDialogStep("Take both vials.");
		getCupricSulfate = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_2, "Get Cupric Sulfate from the shelves.");
		getCupricSulfate.addDialogStep("YES");
		getGypsum = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_3, "Get Gypsum from the shelves.");
		getGypsum.addDialogStep("YES");
		getSodiumChloride = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_4, "Get Sodium Chloride from the shelves.");
		getSodiumChloride.addDialogStep("YES");
		getWire = new ObjectStep(questHelper, ObjectID.RD_SMALL_CRATES, new WorldPoint(2475, 4943, 0), "Get Wire from the crate.");
		getTin = new ObjectStep(questHelper, ObjectID.RD_LARGE_CRATE, new WorldPoint(2476, 4943, 0), "Get Tin from the crate.");
		getShears = new ObjectStep(questHelper, ObjectID.RD_CHEST_CLOSED, "Get Shears from the chest.");
		getShears.addAlternateObjects(ObjectID.RD_CHEST_OPEN);
		getChisel = new ObjectStep(questHelper, ObjectID.RD_LARGE_CRATES, new WorldPoint(2476, 4937, 0), "Get a Chisel from the crate.");
		getNitrousOxide = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_5, "Get Nitrous Oxide from the Shelves.");
		getNitrousOxide.addDialogStep("YES");
		getTinOrePowder = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_6, "Get Tin Ore Powder from the Shelves.");
		getTinOrePowder.addDialogStep("YES");
		getCupricOrePowder = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_7, "Get Cupric Ore Powder from the Shelves.");
		getCupricOrePowder.addDialogStep("YES");
		getThreeVials = new ObjectStep(questHelper, ObjectID.RD_SHELVES_CHEMICALS_8, "Get Three Vials Of Liquid from the Shelves.");
		getThreeVials.addDialogStep("Take all three vials");
		getKnife = new ObjectStep(questHelper, ObjectID.RD_BOOKSHELF_OLD_TALL3, "Get a Knife from the old bookshelf.");
		getMetalSpade = new ItemStep(questHelper, "Get the metal spade off the table", metalSpade);

		useSpadeOnBunsenBurner = new ObjectStep(questHelper, ObjectID.RD_WOODEN_TABLE_BUNSEN_BURNER, "Use the spade in your inventory on the bunsen burner", metalSpade);
		useSpadeOnBunsenBurner.addIcon(ItemID.RD_METAL_SPADE);

		useSpadeHeadOnDoor = new ObjectStep(questHelper, ObjectID.RD_STONE_DOOR, "Use the spade in your inventory on the door.", metalSpadeHead);
		useSpadeHeadOnDoor.addIcon(ItemID.RD_METAL_SPADE_NO_HANDLE);

		useCupricSulfateOnDoor = new ObjectStep(questHelper, ObjectID.RD_STONE_DOOR, "Use Cupric Sulfate in your inventory on the door.", cupricSulfate);
		useCupricSulfateOnDoor.addIcon(ItemID.RD_CUPRIC_SULPHATE);

		useVialOfLiquidOnDoor = new ObjectStep(questHelper, ObjectID.RD_STONE_DOOR, "Use vial of liquid in your inventory on the door.", vialOfLiquid);
		useVialOfLiquidOnDoor.addIcon(ItemID.RD_DIHYDROGEN_MONOXIDE);

		openDoor = new ObjectStep(questHelper, ObjectID.RD_STONE_DOOR, "Open the door.");

		useVialOfLiquidOnCakeTin = new DetailedQuestStep(questHelper, "Use a vial of liquid on the tin in your inventory.", tin, vialOfLiquid);

		useGypsumOnTin = new DetailedQuestStep(questHelper, "Use a vial of Gypsum on the tin in your inventory.", gypsum, tin);

		useTinOnKey = new ObjectStep(questHelper, ObjectID.RD_KEY_CHAINED, "Use tin full with Gypsum on the key on the ground.", gypsumTin);
		useTinOnKey.addIcon(ItemID.RD_TINFULL);

		useCupricOrePowderOnTin = new DetailedQuestStep(questHelper, "Use Tin on the cupric ore powder in your inventory.", tinKeyPrint, cupricOrePowder);

		useTinOrePowderOnTin = new DetailedQuestStep(questHelper, "Use Tin on the tin ore powder in your inventory.", tinWithCupricOre, tinOrePowder);

		useTinOnBunsenBurner = new ObjectStep(questHelper, ObjectID.RD_WOODEN_TABLE_BUNSEN_BURNER, "Use your tin with the bunsen burner to create a bronze key.", tinWithTinOre);
		useTinOnBunsenBurner.addIcon(ItemID.RD_FULL_KEYMOULD_UNHEATED);

		useEquipmentOnTin = new DetailedQuestStep(questHelper, "Use your chisel, knife or bronze wires on your tin in your inventory.", tinWithAllOre, chisel, knife, bronzeWire);

		leaveRoom = new ObjectStep(questHelper, ObjectID.RD_ROOM6_EXITDOOR, new WorldPoint(2478, 4940, 0), "Leave the room by the second door to enter the portal");
	}

	private void addSteps()
	{
		this.addStep(null, getMagnet);
		this.addStep(new Conditions(hasFirstDoorOpen, hasBronzeKey, finishedRoom), leaveRoom);
		setupSecondDoorKeyStep();
		destroyFirstDoorSteps();
		retrieveItemSteps();
	}


	/***
	 * 	Steps and conditions required to create the key for the second door.
	 */
	private void setupSecondDoorKeyStep()
	{
		this.addStep(new Conditions(hasFirstDoorOpen, hasTinWithAllOre), useEquipmentOnTin);
		this.addStep(new Conditions(hasFirstDoorOpen, hasTinWithTinOre), useTinOnBunsenBurner);
		this.addStep(new Conditions(hasFirstDoorOpen, hasTinCupricOre), useTinOrePowderOnTin);
		this.addStep(new Conditions(hasFirstDoorOpen, hasTinKeyPrint), useCupricOrePowderOnTin);
		this.addStep(new Conditions(hasFirstDoorOpen, hasGypsumTin), useTinOnKey);
		this.addStep(new Conditions(hasFirstDoorOpen, hasLiquidInTin), useGypsumOnTin);
		this.addStep(new Conditions(hasFirstDoorOpen), useVialOfLiquidOnCakeTin);
	}

	/***
	 * 	Steps and conditions required to destroy the first door.
	 */
	private void destroyFirstDoorSteps()
	{
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasAshes, hasVialOfLiquidOnDoor), openDoor);

		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasAshes, hasCupricSulfateOnDoor), useVialOfLiquidOnDoor);

		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasAshes, hasSpadeHeadOnDoor), useCupricSulfateOnDoor);

		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasMetalSpadeHead, hasAshes), useSpadeHeadOnDoor);

		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife, hasMetalSpade), useSpadeOnBunsenBurner);
	}

	/**
	 * First stage to retrieve all required items to pass the room.
	 */
	private void retrieveItemSteps()
	{
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials, hasKnife), getMetalSpade);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder,
			hasRetrievedThreeVials), getKnife);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
				hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder, hasCupricOrePowder)
			, getThreeVials);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide, hasTinOrePowder), getCupricOrePowder);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasNitrousOxide, hasNitrousOxide), getTinOrePowder);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears, hasChisel), getNitrousOxide);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin, hasShears), getChisel);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire, hasTin), getShears);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride, hasWire), getTin);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum,
			hasSodiumChloride), getWire);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate, hasGypsum),
			getSodiumChloride);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid, hasCupricSulfate), getGypsum);
		this.addStep(new Conditions(hasMagnet, hasAceticAcid, hasOneVialOfLiquid), getCupricSulfate);
		this.addStep(hasMagnet, getTwoVials);
	}

	public void setupRequirements()
	{
		metalSpade = new ItemRequirement("Metal Spade", ItemID.RD_METAL_SPADE);
		metalSpade.setTooltip("If you are missing this item pick another up off the table.");
		metalSpade.setHighlightInInventory(true);
		metalSpadeHead = new ItemRequirement("Metal Spade", ItemID.RD_METAL_SPADE_NO_HANDLE);
		metalSpadeHead.setHighlightInInventory(true);
		metalSpadeHead.setTooltip("If you are missing this item pick up a metal spade off the table and use it on the bunsen burner.");
		ashes = new ItemRequirement("Ashes", ItemID.ASHES);
		ashes.setHighlightInInventory(true);
		ashes.setTooltip("If you are missing this item pick up a metal spade off the table and use it on the bunsen burner.");
		cupricSulfate = new ItemRequirement("Cupric Sulfate", ItemID.RD_CUPRIC_SULPHATE);
		cupricSulfate.setHighlightInInventory(true);
		cupricSulfate.setTooltip("Take from the shelves on the north side");

		vialOfLiquid = new ItemRequirement("Vial of Liquid", ItemID.RD_DIHYDROGEN_MONOXIDE);
		vialOfLiquid.setHighlightInInventory(true);
		vialOfLiquid.setTooltip("Take from the shelf on the north side or the south side.");

		tin = new ItemRequirement("Tin", ItemID.RD_TIN);
		tin.setHighlightInInventory(true);
		//TODO set tip

		gypsumTin = new ItemRequirement("Tin", ItemID.RD_TINFULL);
		gypsumTin.setHighlightInInventory(true);

		gypsum = new ItemRequirement("Gypsum", ItemID.RD_GYPSUM);
		gypsum.setHighlightInInventory(true);

		tinKeyPrint = new ItemRequirement("Tin", ItemID.RD_KEYMOULD);
		tinKeyPrint.setHighlightInInventory(true);

		tinWithCupricOre = new ItemRequirement("Tin", ItemID.RD_FULL_KEYMOULD_COPPER);
		tinWithCupricOre.setHighlightInInventory(true);

		cupricOrePowder = new ItemRequirement("Cupric Ore Powder", ItemID.RD_COPPER_ORE_POWDER);
		cupricOrePowder.setHighlightInInventory(true);

		tinOrePowder = new ItemRequirement("Tin Ore Powder", ItemID.RD_TIN_ORE_POWDER);
		tinOrePowder.setHighlightInInventory(true);

		tinWithTinOre = new ItemRequirement("Tin", ItemID.RD_FULL_KEYMOULD_UNHEATED);
		tinWithTinOre.setHighlightInInventory(true);
		//		duplicateBronzeKey = new ItemRequirement("Duplicate bronze key", ItemID.PRINCESKEY);

		tinWithAllOre = new ItemRequirement("Tin", ItemID.RD_FULL_KEYMOULD_COMPLETE);
		tinWithAllOre.setHighlightInInventory(true);

		chisel = new ItemRequirement("Chisel", ItemID.RD_CHISEL);
		chisel.setHighlightInInventory(true);

		bronzeWire = new ItemRequirement("Bronze Wire", ItemID.RD_WIRE);
		bronzeWire.setHighlightInInventory(true);

		knife = new ItemRequirement("Knife", ItemID.RD_KNIFE);
		knife.setHighlightInInventory(true);

		bronzeKey = new ItemRequirement("Bronze Key", ItemID.RD_PUZZLEROOM_KEY);

		hasMagnet = new ItemRequirements(new ItemRequirement("Magnet", ItemID.RD_MAGNET));
		hasAceticAcid = new ItemRequirements(new ItemRequirement("Acetic Acid", ItemID.RD_ACETIC_ACID));
		hasOneVialOfLiquid = vialOfLiquid;
		hasCupricSulfate = cupricSulfate;
		hasGypsum = gypsum;
		hasSodiumChloride = new ItemRequirements(new ItemRequirement("Sodium Chloride", ItemID.RD_SODIUM_CHLORIDE));
		hasTin = tin;
		hasWire = bronzeWire;
		hasShears = new ItemRequirements(new ItemRequirement("Shears", ItemID.RD_SHEARS));
		hasChisel = chisel;
		hasNitrousOxide = new ItemRequirements(new ItemRequirement("Nitrous Oxide", ItemID.RD_NITORUS_OXIDE));
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

		hasRetrievedThreeVials = new VarbitRequirement(VarbitID.RD_SPARE_WATER, 3);
		hasSpadeHeadOnDoor = new VarbitRequirement(VarbitID.RD_ROOM6_STONE_DOOR, 1);
		hasCupricSulfateOnDoor = new VarbitRequirement(VarbitID.RD_REACT_ON_SPADE, 1);
		hasVialOfLiquidOnDoor = new VarbitRequirement(VarbitID.RD_ROOM6_STONE_DOOR, 2);
		hasFirstDoorOpen = new VarbitRequirement(VarbitID.RD_ROOM6_STONE_DOOR, 3);
		finishedRoom = new VarbitRequirement(VarbitID.RD_ROOM6_COMPLETE, 1);

		hasLiquidInTin = new VarbitRequirement(VarbitID.RD_WATER_IN_TIN, 1);
	}

	public List<QuestStep> getPanelSteps()
	{
		return List.of(
			getMagnet,
			getTwoVials,
			getCupricSulfate,
			getGypsum,
			getSodiumChloride,
			getWire,
			getTin,
			getShears,
			getChisel,
			getNitrousOxide,
			getTinOrePowder,
			getCupricOrePowder,
			getThreeVials,
			getKnife,
			getMetalSpade,
			useSpadeOnBunsenBurner,
			useSpadeHeadOnDoor,
			useCupricSulfateOnDoor,
			useVialOfLiquidOnDoor,
			openDoor,
			useVialOfLiquidOnCakeTin,
			useGypsumOnTin,
			useTinOnKey,
			useCupricOrePowderOnTin,
			useTinOrePowderOnTin,
			useTinOnBunsenBurner,
			useEquipmentOnTin,
			leaveRoom
		);
	}
}
