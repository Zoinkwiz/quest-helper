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
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
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
	ItemRequirement magnet;
	ItemRequirement aceticAcid;
	ItemRequirement vialOfLiquid;
	ItemRequirement cupricSulfate;
	ItemRequirement gypsum;
	ItemRequirement sodiumChloride;
	ItemRequirement bronzeWire;
	ItemRequirement tin;
	ItemRequirement shears;
	ItemRequirement chisel;
	ItemRequirement nitrousOxide;
	ItemRequirement tinOrePowder;
	ItemRequirement cupricOrePowder;
	ItemRequirement knife;
	ItemRequirement metalSpade;
	ItemRequirement metalSpadeHead;
	ItemRequirement ashes;
	ItemRequirement gypsumTin;
	ItemRequirement tinKeyPrint;
	ItemRequirement tinWithCupricOre;
	ItemRequirement tinWithTinOre;
	ItemRequirement tinWithAllOre;
	ItemRequirement bronzeKey;

	VarbitRequirement hasRetrievedThreeVials;
	VarbitRequirement hasSpadeHeadOnDoor;
	VarbitRequirement hasCupricSulfateOnDoor;
	VarbitRequirement hasVialOfLiquidOnDoor;
	VarbitRequirement hasFirstDoorOpen;
	VarbitRequirement hasLiquidInTin;

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

	PuzzleWrapperStep pwGetMagnet;
	PuzzleWrapperStep pwGetTwoVials;
	PuzzleWrapperStep pwGetCupricSulfate;
	PuzzleWrapperStep pwGetGypsum;
	PuzzleWrapperStep pwGetSodiumChloride;
	PuzzleWrapperStep pwGetWire;
	PuzzleWrapperStep pwGetTin;
	PuzzleWrapperStep pwGetShears;
	PuzzleWrapperStep pwGetChisel;
	PuzzleWrapperStep pwGetNitrousOxide;
	PuzzleWrapperStep pwGetTinOrePowder;
	PuzzleWrapperStep pwGetCupricOrePowder;
	PuzzleWrapperStep pwGetThreeVials;
	PuzzleWrapperStep pwGetKnife;
	PuzzleWrapperStep pwGetMetalSpade;
	PuzzleWrapperStep pwUseSpadeOnBunsenBurner;
	PuzzleWrapperStep pwUseSpadeHeadOnDoor;
	PuzzleWrapperStep pwUseCupricSulfateOnDoor;
	PuzzleWrapperStep pwUseVialOfLiquidOnDoor;
	PuzzleWrapperStep pwOpenDoor;
	PuzzleWrapperStep pwUseVialOfLiquidOnCakeTin;
	PuzzleWrapperStep pwUseGypsumOnTin;
	PuzzleWrapperStep pwUseTinOnKey;
	PuzzleWrapperStep pwUseCupricOrePowderOnTin;
	PuzzleWrapperStep pwUseTinOrePowderOnTin;
	PuzzleWrapperStep pwUseTinOnBunsenBurner;
	PuzzleWrapperStep pwUseEquipmentOnTin;

	public MissCheeversStep(QuestHelper questHelper, Requirement... requirements)
	{
		// null safety - addSteps sets the null conditional step
		super(questHelper, null, requirements);

		setupRequirements();
		setupSteps();
		addSteps();
	}

	public void setupRequirements()
	{
		magnet = new ItemRequirement("Magnet", ItemID.RD_MAGNET);

		aceticAcid = new ItemRequirement("Acetic Acid", ItemID.RD_ACETIC_ACID);

		vialOfLiquid = new ItemRequirement(true, "Vial of Liquid", ItemID.RD_DIHYDROGEN_MONOXIDE);
		vialOfLiquid.setTooltip("Take from the shelf on the north side or the south side.");

		cupricSulfate = new ItemRequirement(true, "Cupric Sulfate", ItemID.RD_CUPRIC_SULPHATE);
		cupricSulfate.setTooltip("Take from the shelves on the north side");

		gypsum = new ItemRequirement(true, "Gypsum", ItemID.RD_GYPSUM);

		sodiumChloride = new ItemRequirement("Sodium Chloride", ItemID.RD_SODIUM_CHLORIDE);

		bronzeWire = new ItemRequirement(true, "Bronze Wire", ItemID.RD_WIRE);

		tin = new ItemRequirement(true, "Tin", ItemID.RD_TIN);
		// TODO set tip

		shears = new ItemRequirement("Shears", ItemID.RD_SHEARS);

		chisel = new ItemRequirement(true, "Chisel", ItemID.RD_CHISEL);

		nitrousOxide = new ItemRequirement("Nitrous Oxide", ItemID.RD_NITORUS_OXIDE);

		tinOrePowder = new ItemRequirement(true, "Tin Ore Powder", ItemID.RD_TIN_ORE_POWDER);

		cupricOrePowder = new ItemRequirement(true, "Cupric Ore Powder", ItemID.RD_COPPER_ORE_POWDER);

		knife = new ItemRequirement(true, "Knife", ItemID.RD_KNIFE);

		metalSpade = new ItemRequirement(true, "Metal Spade", ItemID.RD_METAL_SPADE);
		metalSpade.setTooltip("If you are missing this item pick another up off the table.");

		metalSpadeHead = new ItemRequirement(true, "Metal Spade", ItemID.RD_METAL_SPADE_NO_HANDLE);
		metalSpadeHead.setTooltip("If you are missing this item pick up a metal spade off the table and use it on the bunsen burner.");
		ashes = new ItemRequirement(true, "Ashes", ItemID.ASHES);
		ashes.setTooltip("If you are missing this item pick up a metal spade off the table and use it on the bunsen burner.");

		gypsumTin = new ItemRequirement(true, "Tin", ItemID.RD_TINFULL);

		tinKeyPrint = new ItemRequirement(true, "Tin", ItemID.RD_KEYMOULD);

		tinWithCupricOre = new ItemRequirement(true, "Tin", ItemID.RD_FULL_KEYMOULD_COPPER);

		tinWithTinOre = new ItemRequirement(true, "Tin", ItemID.RD_FULL_KEYMOULD_UNHEATED);

		tinWithAllOre = new ItemRequirement(true, "Tin", ItemID.RD_FULL_KEYMOULD_COMPLETE);

		bronzeKey = new ItemRequirement("Bronze Key", ItemID.RD_PUZZLEROOM_KEY);

		hasRetrievedThreeVials = new VarbitRequirement(VarbitID.RD_SPARE_WATER, 3);
		hasSpadeHeadOnDoor = new VarbitRequirement(VarbitID.RD_ROOM6_STONE_DOOR, 1);
		hasCupricSulfateOnDoor = new VarbitRequirement(VarbitID.RD_REACT_ON_SPADE, 1);
		hasVialOfLiquidOnDoor = new VarbitRequirement(VarbitID.RD_ROOM6_STONE_DOOR, 2);
		hasFirstDoorOpen = new VarbitRequirement(VarbitID.RD_ROOM6_STONE_DOOR, 3);

		hasLiquidInTin = new VarbitRequirement(VarbitID.RD_WATER_IN_TIN, 1);
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

		pwGetMagnet = getMagnet.puzzleWrapStep(true);
		pwGetTwoVials = getTwoVials.puzzleWrapStep(true);
		pwGetCupricSulfate = getCupricSulfate.puzzleWrapStep(true);
		pwGetGypsum = getGypsum.puzzleWrapStep(true);
		pwGetSodiumChloride = getSodiumChloride.puzzleWrapStep(true);
		pwGetWire = getWire.puzzleWrapStep(true);
		pwGetTin = getTin.puzzleWrapStep(true);
		pwGetShears = getShears.puzzleWrapStep(true);
		pwGetChisel = getChisel.puzzleWrapStep(true);
		pwGetNitrousOxide = getNitrousOxide.puzzleWrapStep(true);
		pwGetTinOrePowder = getTinOrePowder.puzzleWrapStep(true);
		pwGetCupricOrePowder = getCupricOrePowder.puzzleWrapStep(true);
		pwGetThreeVials = getThreeVials.puzzleWrapStep(true);
		pwGetKnife = getKnife.puzzleWrapStep(true);
		pwGetMetalSpade = getMetalSpade.puzzleWrapStep(true);
		pwUseSpadeOnBunsenBurner = useSpadeOnBunsenBurner.puzzleWrapStep(true);
		pwUseSpadeHeadOnDoor = useSpadeHeadOnDoor.puzzleWrapStep(true);
		pwUseCupricSulfateOnDoor = useCupricSulfateOnDoor.puzzleWrapStep(true);
		pwUseVialOfLiquidOnDoor = useVialOfLiquidOnDoor.puzzleWrapStep(true);
		pwOpenDoor = openDoor.puzzleWrapStep(true);
		pwUseVialOfLiquidOnCakeTin = useVialOfLiquidOnCakeTin.puzzleWrapStep(true);
		pwUseGypsumOnTin = useGypsumOnTin.puzzleWrapStep(true);
		pwUseTinOnKey = useTinOnKey.puzzleWrapStep(true);
		pwUseCupricOrePowderOnTin = useCupricOrePowderOnTin.puzzleWrapStep(true);
		pwUseTinOrePowderOnTin = useTinOrePowderOnTin.puzzleWrapStep(true);
		pwUseTinOnBunsenBurner = useTinOnBunsenBurner.puzzleWrapStep(true);
		pwUseEquipmentOnTin = useEquipmentOnTin.puzzleWrapStep(true);
	}

	private void addSteps()
	{
		this.addStep(null, pwGetMagnet);
		setupSecondDoorKeyStep();
		destroyFirstDoorSteps();
		retrieveItemSteps();
	}

	/***
	 * 	Steps and conditions required to create the key for the second door.
	 */
	private void setupSecondDoorKeyStep()
	{
		this.addStep(and(hasFirstDoorOpen, tinWithAllOre), pwUseEquipmentOnTin);
		this.addStep(and(hasFirstDoorOpen, tinWithTinOre), pwUseTinOnBunsenBurner);
		this.addStep(and(hasFirstDoorOpen, tinWithCupricOre), pwUseTinOrePowderOnTin);
		this.addStep(and(hasFirstDoorOpen, tinKeyPrint), pwUseCupricOrePowderOnTin);
		this.addStep(and(hasFirstDoorOpen, gypsumTin), pwUseTinOnKey);
		this.addStep(and(hasFirstDoorOpen, hasLiquidInTin), pwUseGypsumOnTin);
		this.addStep(hasFirstDoorOpen, pwUseVialOfLiquidOnCakeTin);
	}

	/***
	 * 	Steps and conditions required to destroy the first door.
	 */
	private void destroyFirstDoorSteps()
	{
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials, knife, ashes, hasVialOfLiquidOnDoor), pwOpenDoor);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials, knife, ashes, hasCupricSulfateOnDoor), pwUseVialOfLiquidOnDoor);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials, knife, ashes, hasSpadeHeadOnDoor), pwUseCupricSulfateOnDoor);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials, knife, metalSpadeHead, ashes), pwUseSpadeHeadOnDoor);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials, knife, metalSpade), pwUseSpadeOnBunsenBurner);
	}

	/**
	 * First stage to retrieve all required items to pass the room.
	 */
	private void retrieveItemSteps()
	{
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials, knife), pwGetMetalSpade);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder, hasRetrievedThreeVials), pwGetKnife);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder, cupricOrePowder), pwGetThreeVials);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide, tinOrePowder), pwGetCupricOrePowder);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, nitrousOxide, nitrousOxide), pwGetTinOrePowder);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears, chisel), pwGetNitrousOxide);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin, shears), pwGetChisel);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire, tin), pwGetShears);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride, bronzeWire), pwGetTin);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum, sodiumChloride), pwGetWire);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate, gypsum), pwGetSodiumChloride);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid, cupricSulfate), pwGetGypsum);
		this.addStep(and(magnet, aceticAcid, vialOfLiquid), pwGetCupricSulfate);
		this.addStep(magnet, pwGetTwoVials);
	}

	public List<QuestStep> getPanelSteps()
	{
		return List.of(
			pwGetMagnet,
			pwGetTwoVials,
			pwGetCupricSulfate,
			pwGetGypsum,
			pwGetSodiumChloride,
			pwGetWire,
			pwGetTin,
			pwGetShears,
			pwGetChisel,
			pwGetNitrousOxide,
			pwGetTinOrePowder,
			pwGetCupricOrePowder,
			pwGetThreeVials,
			pwGetKnife,
			pwGetMetalSpade,
			pwUseSpadeOnBunsenBurner,
			pwUseSpadeHeadOnDoor,
			pwUseCupricSulfateOnDoor,
			pwUseVialOfLiquidOnDoor,
			pwOpenDoor,
			pwUseVialOfLiquidOnCakeTin,
			pwUseGypsumOnTin,
			pwUseTinOnKey,
			pwUseCupricOrePowderOnTin,
			pwUseTinOrePowderOnTin,
			pwUseTinOnBunsenBurner,
			pwUseEquipmentOnTin
		);
	}
}
