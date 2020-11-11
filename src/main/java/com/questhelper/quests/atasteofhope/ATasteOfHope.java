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
package com.questhelper.quests.atasteofhope;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.ItemRequirements;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
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
	quest = QuestHelperQuest.A_TASTE_OF_HOPE
)
public class ATasteOfHope extends BasicQuestHelper
{
	ItemRequirement coins1000, knife, emerald, chisel, enchantEmeraldRunesOrTablet, rodOfIvandis, pestleAndMortarHighlighted, vialOfWater, combatGear, airRune3, airStaff,
		cosmicRune, enchantTablet, enchantRunes, vial, herb, meatHighlighted, crushedMeat, unfinishedPotion, unfinishedBloodPotion, potion, bloodPotion, bloodVial, oldNotes,
		flaygianNotes, sickleB, chain, emeraldSickleB, enchantedEmeraldSickleB, ivandisFlail, rodOfIvandisHighlighted, ivandisFlailEquipped, emeraldHighlighted;

	ConditionForStep inMyrequeBase, inTheatreP1, inTheatreP2, inTheatreP3, inTheatreP4, inTheatreP5, inTheatreP6, inSerafinaHouse, hasHerb, hasMeat, hasPestle, hasVialOrVialOfWater,
		hasVialOfWater, hasCrushedMeat, hasUnfinishedPotion, hasUnfinishedBloodPotion, hasBloodVial, hasBloodPotion, hasPotion, hasVial, hasOldNotes, hasSickle, hasEmeraldSickle,
		hasEnchantedEmeraldSickle, hasChain, hasFlail, inNewBase, hasFlaygianNotes, inRanisFight;

	DetailedQuestStep talkToGarth, enterBase, talkToSafalaan, climbRubbleAtBank, talkToHarpert, climbRubbleAfterHarpert, climbSteamVent, jumpOffRoof,
		climbSecondVent, climbUpToRoof, climbDownFromRoof, lookThroughWindow, returnToBase, talkToSafalaanAfterSpying, talkToFlaygian, talkToSafalaanAfterFlaygian,
		goUpToSerafinaHouse, enterSerafinaHouse, talkToSafalaanInSerafinaHouse, searchForMeat, searchForHerb, searchForVial, searchForPestle, useHerbOnVial, usePestleOnMeat,
		useMeatOnPotion, usePotionOnDoor, talkToSafalaanAfterPotion, useHerbOnBlood, usePestleOnMeatAgain, useMeatOnBlood, useBloodOnDoor, getOldNotes, talkToSafalaanWithNotes,
		enterBaseAfterSerafina, killAbomination, enterOldManRalBasement, talkToSafalaanInRalBasement, talkToVertidaInRalBasement, readFlaygianNotes, getSickle, getChain,
		useEmeraldOnSickle, enchantSickle, addSickleToRod, talkToSafalaanAfterFlail, talkToKael, killRanis, talkToKaelAgain, enterRalForEnd, talkToSafalaanForEnd,
		talkToSafalaanForAbominationFight, talkToSafalaanAfterAbominationFight, enterRalWithFlail, talkToKaelSidebar;

	Zone myrequeBase, theatreP1, theatreP2, theatreP3, theatreP4, theatreP5, theatreP6, serafinaHouse, newBase, ranisFight;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGarth);
		steps.put(5, talkToGarth);
		steps.put(10, talkToGarth);

		ConditionalStep goTalkToSafalaan = new ConditionalStep(this, enterBase);
		goTalkToSafalaan.addStep(inMyrequeBase, talkToSafalaan);
		steps.put(15, goTalkToSafalaan);

		steps.put(20, climbRubbleAtBank);
		steps.put(25, climbRubbleAtBank);
		steps.put(30, talkToHarpert);
		steps.put(35, talkToHarpert);

		ConditionalStep spy = new ConditionalStep(this, climbRubbleAfterHarpert);
		spy.addStep(inTheatreP6, lookThroughWindow);
		spy.addStep(inTheatreP5, climbDownFromRoof);
		spy.addStep(inTheatreP4, climbUpToRoof);
		spy.addStep(inTheatreP3, climbSecondVent);
		spy.addStep(inTheatreP2, jumpOffRoof);
		spy.addStep(inTheatreP1, climbSteamVent);

		steps.put(40, spy);

		ConditionalStep goReturnToSafalaan = new ConditionalStep(this, returnToBase);
		goReturnToSafalaan.addStep(inMyrequeBase, talkToSafalaanAfterSpying);
		steps.put(45, goReturnToSafalaan);
		steps.put(50, goReturnToSafalaan);

		ConditionalStep goTalkToFlaygian = new ConditionalStep(this, returnToBase);
		goTalkToFlaygian.addStep(inMyrequeBase, talkToFlaygian);
		steps.put(55, goTalkToFlaygian);
		steps.put(60, goTalkToFlaygian);

		ConditionalStep goTalkToSaflaanAfterFlaygian = new ConditionalStep(this, returnToBase);
		goTalkToSaflaanAfterFlaygian.addStep(inMyrequeBase, talkToSafalaanAfterFlaygian);
		steps.put(65, goTalkToSaflaanAfterFlaygian);
		steps.put(70, goTalkToSaflaanAfterFlaygian);

		ConditionalStep goToSerafinaHouse = new ConditionalStep(this, enterSerafinaHouse);
		goToSerafinaHouse.addStep(inSerafinaHouse, talkToSafalaanInSerafinaHouse);
		goToSerafinaHouse.addStep(inMyrequeBase, goUpToSerafinaHouse);
		steps.put(75, goToSerafinaHouse);

		ConditionalStep tryFirstPotion = new ConditionalStep(this, enterSerafinaHouse);
		tryFirstPotion.addStep(new Conditions(inSerafinaHouse, hasPotion), usePotionOnDoor);
		tryFirstPotion.addStep(new Conditions(hasPotion), enterSerafinaHouse);
		tryFirstPotion.addStep(new Conditions(hasCrushedMeat, hasUnfinishedPotion), useMeatOnPotion);
		tryFirstPotion.addStep(new Conditions(hasMeat, hasPestle, hasUnfinishedPotion), usePestleOnMeat);
		tryFirstPotion.addStep(new Conditions(hasHerb, hasMeat, hasPestle, hasVialOrVialOfWater), useHerbOnVial);
		tryFirstPotion.addStep(new Conditions(inSerafinaHouse, hasHerb, hasMeat, hasPestle), searchForVial);
		tryFirstPotion.addStep(new Conditions(inSerafinaHouse, hasHerb, hasMeat), searchForPestle);
		tryFirstPotion.addStep(new Conditions(inSerafinaHouse, hasHerb), searchForMeat);
		tryFirstPotion.addStep(inSerafinaHouse, searchForHerb);
		steps.put(80, tryFirstPotion);
		steps.put(81, tryFirstPotion);

		ConditionalStep trySecondPotion = new ConditionalStep(this, enterSerafinaHouse);
		trySecondPotion.addStep(new Conditions(inSerafinaHouse, hasBloodPotion), useBloodOnDoor);
		trySecondPotion.addStep(new Conditions(hasBloodPotion), enterSerafinaHouse);
		trySecondPotion.addStep(new Conditions(hasCrushedMeat, hasUnfinishedBloodPotion), useMeatOnBlood);
		trySecondPotion.addStep(new Conditions(hasMeat, hasPestle, hasUnfinishedBloodPotion), usePestleOnMeat);
		trySecondPotion.addStep(new Conditions(hasHerb, hasMeat, hasPestle, hasBloodVial), useHerbOnBlood);
		trySecondPotion.addStep(new Conditions(inSerafinaHouse, hasHerb, hasMeat, hasBloodVial), searchForPestle);
		trySecondPotion.addStep(new Conditions(inSerafinaHouse, hasHerb, hasBloodVial), searchForMeat);
		trySecondPotion.addStep(new Conditions(inSerafinaHouse, hasBloodVial), searchForHerb);
		trySecondPotion.addStep(new Conditions(inSerafinaHouse, hasVial), talkToSafalaanAfterPotion);
		trySecondPotion.addStep(new Conditions(inSerafinaHouse), searchForVial);
		steps.put(82, trySecondPotion);
		steps.put(83, trySecondPotion);
		steps.put(84, trySecondPotion);

		ConditionalStep goGetNotes = new ConditionalStep(this, enterSerafinaHouse);
		goGetNotes.addStep(new Conditions(inSerafinaHouse, hasOldNotes), talkToSafalaanWithNotes);
		goGetNotes.addStep(inSerafinaHouse, getOldNotes);
		steps.put(85, goGetNotes);
		steps.put(86, goGetNotes);

		ConditionalStep goStartAbominationFight = new ConditionalStep(this, enterBaseAfterSerafina);
		goStartAbominationFight.addStep(inMyrequeBase, talkToSafalaanForAbominationFight);
		steps.put(90, goStartAbominationFight);

		ConditionalStep goKillAbomination = new ConditionalStep(this, enterBaseAfterSerafina);
		goKillAbomination.addStep(inMyrequeBase, killAbomination);
		steps.put(95, goKillAbomination);
		steps.put(100, goKillAbomination);

		ConditionalStep goTalkToSafalaanAfterAbomination = new ConditionalStep(this, enterBaseAfterSerafina);
		goTalkToSafalaanAfterAbomination.addStep(inMyrequeBase, talkToSafalaanAfterAbominationFight);
		steps.put(105, goTalkToSafalaanAfterAbomination);

		ConditionalStep goToNewBase = new ConditionalStep(this, enterOldManRalBasement);
		goToNewBase.addStep(inNewBase, talkToSafalaanInRalBasement);
		steps.put(110, goToNewBase);
		steps.put(115, goToNewBase);

		ConditionalStep goTalkToVertida = new ConditionalStep(this, enterOldManRalBasement);
		goTalkToVertida.addStep(hasFlaygianNotes, readFlaygianNotes);
		goTalkToVertida.addStep(inNewBase, talkToVertidaInRalBasement);
		steps.put(120, goTalkToVertida);

		ConditionalStep makeFlail = new ConditionalStep(this, enterOldManRalBasement);
		makeFlail.addStep(new Conditions(inNewBase, hasFlail), talkToSafalaanAfterFlail);
		makeFlail.addStep(new Conditions(hasFlail), enterRalWithFlail);
		makeFlail.addStep(new Conditions(hasEnchantedEmeraldSickle, hasChain), addSickleToRod);
		makeFlail.addStep(new Conditions(hasEmeraldSickle, hasChain), enchantSickle);
		makeFlail.addStep(new Conditions(hasSickle, hasChain), useEmeraldOnSickle);
		makeFlail.addStep(new Conditions(inNewBase, hasSickle), getChain);
		makeFlail.addStep(inNewBase, getSickle);
		steps.put(125, makeFlail);

		ConditionalStep goTalkToSafalaanAfterFlail = new ConditionalStep(this, enterRalWithFlail);
		goTalkToSafalaanAfterFlail.addStep(inNewBase, talkToSafalaanAfterFlail);
		steps.put(130, makeFlail);

		steps.put(135, talkToKael);

		ConditionalStep goFightRanis = new ConditionalStep(this, talkToKael);
		goFightRanis.addStep(inRanisFight, killRanis);
		steps.put(140, goFightRanis);

		steps.put(145, talkToKaelAgain);

		ConditionalStep finishQuest = new ConditionalStep(this, enterRalForEnd);
		finishQuest.addStep(inNewBase, talkToSafalaanForEnd);
		steps.put(150, finishQuest);
		steps.put(155, finishQuest);
		steps.put(160, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		coins1000 = new ItemRequirement("Coins", ItemID.COINS_995, 1000);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		emerald = new ItemRequirement("Emerald", ItemID.EMERALD);
		emeraldHighlighted = new ItemRequirement("Emerald", ItemID.EMERALD);
		emeraldHighlighted.setHighlightInInventory(true);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		airRune3 = new ItemRequirement("Air rune", ItemCollections.getAirRune(), 3);
		airStaff = new ItemRequirement("Air staff", ItemCollections.getAirStaff());
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE);
		enchantRunes = new ItemRequirements("Emerald enchant runes", new ItemRequirements(LogicType.OR, "3 air runes", airRune3, airStaff), cosmicRune);
		enchantTablet = new ItemRequirement("Emerald enchant tablet", ItemID.ENCHANT_EMERALD_OR_JADE);
		enchantEmeraldRunesOrTablet = new ItemRequirements(LogicType.OR, "Runes or tablet for Enchant Emerald", enchantRunes, enchantTablet);
		rodOfIvandis = new ItemRequirement("Rod of Ivandis", ItemID.ROD_OF_IVANDIS_10);
		rodOfIvandis.addAlternates(ItemID.ROD_OF_IVANDIS_1, ItemID.ROD_OF_IVANDIS_2, ItemID.ROD_OF_IVANDIS_3, ItemID.ROD_OF_IVANDIS_4, ItemID.ROD_OF_IVANDIS_5,
			ItemID.ROD_OF_IVANDIS_6, ItemID.ROD_OF_IVANDIS_7, ItemID.ROD_OF_IVANDIS_8, ItemID.ROD_OF_IVANDIS_9);
		rodOfIvandis.setTip("You can get another from Veliaf Hurtz in Burgh de Rott");

		rodOfIvandisHighlighted = new ItemRequirement("Rod of Ivandis", ItemID.ROD_OF_IVANDIS_10);
		rodOfIvandisHighlighted.addAlternates(ItemID.ROD_OF_IVANDIS_1, ItemID.ROD_OF_IVANDIS_2, ItemID.ROD_OF_IVANDIS_3, ItemID.ROD_OF_IVANDIS_4, ItemID.ROD_OF_IVANDIS_5,
			ItemID.ROD_OF_IVANDIS_6, ItemID.ROD_OF_IVANDIS_7, ItemID.ROD_OF_IVANDIS_8, ItemID.ROD_OF_IVANDIS_9);
		rodOfIvandisHighlighted.setTip("You can get another from Veliaf Hurtz in Burgh de Rott");
		rodOfIvandisHighlighted.setHighlightInInventory(true);

		pestleAndMortarHighlighted = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortarHighlighted.setHighlightInInventory(true);
		vialOfWater = new ItemRequirement("Vial of water", ItemID.VIAL_OF_WATER);
		vialOfWater.setHighlightInInventory(true);
		vialOfWater.setTip("You can fill the vial upstairs on the broken fountain");
		combatGear = new ItemRequirement("Combat gear + food", -1, -1);
		vial = new ItemRequirement("Vial", ItemID.VIAL);
		herb = new ItemRequirement("Mysterious herb", ItemID.MYSTERIOUS_HERB);
		herb.setHighlightInInventory(true);
		meatHighlighted = new ItemRequirement("Mysterious meat", ItemID.MYSTERIOUS_MEAT);
		meatHighlighted.setHighlightInInventory(true);
		crushedMeat = new ItemRequirement("Mysterious meat", ItemID.MYSTERIOUS_CRUSHED_MEAT);
		crushedMeat.setHighlightInInventory(true);
		unfinishedPotion = new ItemRequirement("Unfinished potion", ItemID.UNFINISHED_POTION_22408);
		unfinishedPotion.setHighlightInInventory(true);

		unfinishedBloodPotion = new ItemRequirement("Unfinished blood potion", ItemID.UNFINISHED_BLOOD_POTION);
		unfinishedBloodPotion.setHighlightInInventory(true);

		potion = new ItemRequirement("Potion", ItemID.POTION_22409);
		potion.setHighlightInInventory(true);
		bloodPotion = new ItemRequirement("Blood potion", ItemID.BLOOD_POTION);
		bloodPotion.setHighlightInInventory(true);

		bloodVial = new ItemRequirement("Vial of blood", ItemID.VIAL_OF_BLOOD);
		bloodVial.setHighlightInInventory(true);

		oldNotes = new ItemRequirement("Old notes", ItemID.OLD_NOTES_22410);

		flaygianNotes = new ItemRequirement("Flaygian's notes", ItemID.FLAYGIANS_NOTES);
		flaygianNotes.setHighlightInInventory(true);
		sickleB = new ItemRequirement("Silver sickle (b)", ItemID.SILVER_SICKLE_B);
		sickleB.setHighlightInInventory(true);
		chain = new ItemRequirement("Silver chain", ItemID.CHAIN);
		chain.setHighlightInInventory(true);
		emeraldSickleB = new ItemRequirement("Emerald sickle (b)", ItemID.EMERALD_SICKLE_B);
		emeraldSickleB.setHighlightInInventory(true);
		enchantedEmeraldSickleB = new ItemRequirement("Enchanted emerald sickle (b)", ItemID.ENCHANTED_EMERALD_SICKLE_B);
		enchantedEmeraldSickleB.setHighlightInInventory(true);

		ivandisFlail = new ItemRequirement("Ivandis flail", ItemID.IVANDIS_FLAIL);
		ivandisFlailEquipped = new ItemRequirement("Ivandis flail", ItemID.IVANDIS_FLAIL, 1, true);
	}

	public void loadZones()
	{
		myrequeBase = new Zone(new WorldPoint(3616, 9616, 0), new WorldPoint(3640, 9647, 0));
		theatreP1 = new Zone(new WorldPoint(3638, 3202, 1), new WorldPoint(3646, 3214, 1));
		theatreP2 = new Zone(new WorldPoint(3642, 3214, 2), new WorldPoint(3645, 3224, 2));
		theatreP3 = new Zone(new WorldPoint(3638, 3224, 1), new WorldPoint(3647, 3235, 1));
		theatreP4 = new Zone(new WorldPoint(3641, 3234, 2), new WorldPoint(3661, 3240, 2));
		theatreP5 = new Zone(new WorldPoint(3661, 3237, 3), new WorldPoint(3664, 3237, 3));
		theatreP6 = new Zone(new WorldPoint(3665, 3222, 2), new WorldPoint(3697, 3240, 2));
		serafinaHouse = new Zone(new WorldPoint(3592, 9671, 0), new WorldPoint(3600, 9683, 0));
		newBase = new Zone(new WorldPoint(3588, 9609, 0), new WorldPoint(3606, 9619, 0));
		ranisFight = new Zone(new WorldPoint(2075, 4888, 0), new WorldPoint(2085, 4895, 0));
	}

	public void setupConditions()
	{
		inMyrequeBase = new ZoneCondition(myrequeBase);
		inTheatreP1 = new ZoneCondition(theatreP1);
		inTheatreP2 = new ZoneCondition(theatreP2);
		inTheatreP3 = new ZoneCondition(theatreP3);
		inTheatreP4 = new ZoneCondition(theatreP4);
		inTheatreP5 = new ZoneCondition(theatreP5);
		inTheatreP6 = new ZoneCondition(theatreP6);
		inSerafinaHouse = new ZoneCondition(serafinaHouse);
		inNewBase = new ZoneCondition(newBase);

		hasCrushedMeat = new ItemRequirementCondition(crushedMeat);
		hasMeat = new ItemRequirementCondition(meatHighlighted);
		hasHerb = new ItemRequirementCondition(herb);
		hasVialOfWater = new ItemRequirementCondition(vialOfWater);
		hasVial = new ItemRequirementCondition(vial);
		hasVialOrVialOfWater = new Conditions(LogicType.OR, hasVialOfWater, hasVial);
		hasBloodVial = new ItemRequirementCondition(bloodVial);
		hasPestle = new ItemRequirementCondition(pestleAndMortarHighlighted);
		hasUnfinishedPotion = new ItemRequirementCondition(unfinishedPotion);
		hasUnfinishedBloodPotion = new ItemRequirementCondition(unfinishedBloodPotion);
		hasBloodPotion = new ItemRequirementCondition(bloodPotion);
		hasPotion = new ItemRequirementCondition(potion);
		hasOldNotes = new ItemRequirementCondition(oldNotes);
		hasSickle = new ItemRequirementCondition(sickleB);
		hasEmeraldSickle = new ItemRequirementCondition(emeraldSickleB);

		hasEnchantedEmeraldSickle = new ItemRequirementCondition(enchantedEmeraldSickleB);
		hasChain = new ItemRequirementCondition(chain);
		hasFlail = new ItemRequirementCondition(ivandisFlail);
		hasFlaygianNotes = new ItemRequirementCondition(flaygianNotes);
		inRanisFight = new ZoneCondition(ranisFight);
	}

	public void setupSteps()
	{
		ArrayList<WorldPoint> pathToBase = new ArrayList<>(Arrays.asList(
			new WorldPoint(3623, 3324, 0),
			new WorldPoint(3631, 3324, 0),
			new WorldPoint(3631, 3303, 0),
			new WorldPoint(3628, 3300, 0),
			new WorldPoint(3628, 3294, 0),
			new WorldPoint(3633, 3294, 0),
			new WorldPoint(3633, 3288, 0),
			new WorldPoint(3635, 3288, 0),
			new WorldPoint(3635, 3284, 0),
			new WorldPoint(3632, 3284, 0),
			new WorldPoint(3632, 3277, 0),
			new WorldPoint(3634, 3277, 0),
			new WorldPoint(3634, 3267, 0),
			new WorldPoint(3631, 3267, 0),
			new WorldPoint(3631, 3258, 0),
			new WorldPoint(3631, 3258, 1),
			new WorldPoint(3633, 3256, 1),
			new WorldPoint(3639, 3256, 1),
			new WorldPoint(3639, 3256, 0),
			new WorldPoint(3640, 3256, 0),
			new WorldPoint(3640, 3250, 0)
		));

		ArrayList<WorldPoint> pathToSerafina = new ArrayList<>(Arrays.asList(
			new WorldPoint(3640, 3250, 0),
			new WorldPoint(3640, 3256, 0),
			new WorldPoint(3639, 3256, 0),
			new WorldPoint(3639, 3256, 1),
			new WorldPoint(3633, 3256, 1),
			new WorldPoint(3631, 3258, 1),
			new WorldPoint(3631, 3258, 0),
			new WorldPoint(3631, 3267, 0),
			new WorldPoint(3634, 3267, 0),
			new WorldPoint(3634, 3277, 0),
			new WorldPoint(3632, 3277, 0),
			new WorldPoint(3632, 3280, 0),
			new WorldPoint(3623, 3280, 0),
			new WorldPoint(3623, 3283, 0),
			new WorldPoint(3596, 3283, 0),
			new WorldPoint(3596, 3277, 0)
		));

		talkToGarth = new NpcStep(this, NpcID.GARTH_8206, new WorldPoint(3668, 3217, 0), "Talk to Garth outside the Theatre of Blood.");
		talkToGarth.addDialogStep("Yes.");

		enterBase = new ObjectStep(this, NullObjectID.NULL_18120, new WorldPoint(3639, 3249, 0), "Enter the Meiyerditch Myreque base. The easiest way to get there is to have a Vyrewatch take you to the mines, escape by mining 15 rocks, then head south from there.");
		enterBase.setLinePoints(pathToBase);

		talkToSafalaan = new NpcStep(this, NpcID.SAFALAAN_HALLOW, new WorldPoint(3627, 9644, 0), "Talk to Safalaan in the north room.");

		climbRubbleAtBank = new ObjectStep(this, NullObjectID.NULL_32650, new WorldPoint(3642, 3207, 0), "Return to the Theatre of Blood and attempt to climb rubble in its south west corner.");
		talkToHarpert = new NpcStep(this, NpcID.HARPERT, new WorldPoint(3644, 3211, 0), "Talk to Harpert near the rubble.", coins1000);
		talkToHarpert.addDialogStep("Fine, here's the money.");
		climbRubbleAfterHarpert = new ObjectStep(this, NullObjectID.NULL_32650, new WorldPoint(3642, 3207, 0), "Attempt to climb the rubble again.");
		climbSteamVent = new ObjectStep(this, ObjectID.VENT_32551, new WorldPoint(3644, 3214, 1), "Climb the vent to the north when the steam stops coming out.");
		jumpOffRoof = new ObjectStep(this, ObjectID.ROOF_32553, new WorldPoint(3644, 3225, 2), "Jump off the roof to the north.");
		climbSecondVent = new ObjectStep(this, ObjectID.VENT_32551, new WorldPoint(3641, 3235, 1), "Climb the vent to the north when steam stops coming out.");
		climbUpToRoof = new ObjectStep(this, ObjectID.ROOF_32554, new WorldPoint(3660, 3237, 2), "Climb the roof to the east.");
		climbDownFromRoof = new ObjectStep(this, ObjectID.ROOF_32555, new WorldPoint(3665, 3237, 3), "Climb down to the east.");
		lookThroughWindow = new ObjectStep(this, ObjectID.WINDOW_32548, new WorldPoint(3687, 3221, 2), "Look through the window at the end of the path.");

		returnToBase = new ObjectStep(this, NullObjectID.NULL_18120, new WorldPoint(3639, 3249, 0), "Return to the Meiyerditch Myreque base. The easiest way to get there is to have a Vyrewatch take you to the mines, escape by mining 15 rocks, then head south from there.");
		returnToBase.setLinePoints(pathToBase);

		talkToSafalaanAfterSpying = new NpcStep(this, NpcID.SAFALAAN_HALLOW, new WorldPoint(3627, 9644, 0), "Talk to Safalaan in the north room.");
		talkToSafalaanAfterSpying.addDialogStep("I do.");

		talkToFlaygian = new NpcStep(this, NpcID.FLAYGIAN_SCREWTE, new WorldPoint(3627, 9644, 0), "Talk to Flaygian.");
		talkToFlaygian.addDialogSteps("Anything to report?", "Why?");
		talkToSafalaanAfterFlaygian = new NpcStep(this, NpcID.SAFALAAN_HALLOW, new WorldPoint(3627, 9644, 0), "Talk to Safalaan again.");

		goUpToSerafinaHouse = new ObjectStep(this, ObjectID.LADDER_17986, new WorldPoint(3626, 9617, 0), "Return to the surface.");
		enterSerafinaHouse = new ObjectStep(this, ObjectID.STAIRS_32560, new WorldPoint(3593, 3274, 0), "Enter Serafina's house in west Meiyerditch.");
		enterSerafinaHouse.setLinePoints(pathToSerafina);
		enterSerafinaHouse.addSubSteps(goUpToSerafinaHouse);

		talkToSafalaanInSerafinaHouse = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8216, new WorldPoint(3596, 9675, 0), "Talk to Safalaan.");
		// Talked to Safalaan, 2592 2->0

		searchForHerb = new ObjectStep(this, ObjectID.BARREL_32564, new WorldPoint(3599, 9678, 0), "Search the barrel in the north east corner.");
		searchForMeat = new ObjectStep(this, ObjectID.CRATE_32567, new WorldPoint(3593, 9677, 0), "Search the crate in the west of the room for mysterious meat.");
		searchForVial = new ObjectStep(this, ObjectID.CRATE_32566, new WorldPoint(3598, 9671, 0), "Search a crate in the south east corner for a vial.");
		searchForPestle = new ObjectStep(this, ObjectID.CRATE_32568, new WorldPoint(3594, 9671, 0), "Search the crate near the staircase for a pestle and mortar.");

		useHerbOnVial = new DetailedQuestStep(this, "Add the herb to a vial of water.", herb, vialOfWater);
		usePestleOnMeat = new DetailedQuestStep(this, "Use the pestle and mortar on the meat.", pestleAndMortarHighlighted, meatHighlighted);
		useMeatOnPotion = new DetailedQuestStep(this, "Add the crushed meat to the potion.", crushedMeat, unfinishedPotion);
		usePotionOnDoor = new ObjectStep(this, ObjectID.DOOR_32562, new WorldPoint(3596, 9680, 0), "Use the potion on the door.", potion);
		usePotionOnDoor.addDialogStep("Yes.");
		usePotionOnDoor.addIcon(ItemID.POTION_22409);
		talkToSafalaanAfterPotion = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8216, new WorldPoint(3596, 9675, 0), "Talk to Safalaan for his blood.", vial);
		talkToSafalaanAfterPotion.addDialogStep("Yes.");

		useHerbOnBlood = new DetailedQuestStep(this, "Add the herb to a vial of blood.", herb, bloodVial);
		usePestleOnMeatAgain = new DetailedQuestStep(this, "Use the pestle and mortar on the meat.", pestleAndMortarHighlighted, meatHighlighted);
		useMeatOnBlood = new DetailedQuestStep(this, "Add the crushed meat to the unfinished potion.", crushedMeat, unfinishedBloodPotion);
		useBloodOnDoor = new ObjectStep(this, ObjectID.DOOR_32562, new WorldPoint(3596, 9680, 0), "Use the blood potion on the door.", bloodPotion);
		useBloodOnDoor.addDialogStep("Yes.");
		useBloodOnDoor.addIcon(ItemID.BLOOD_POTION);
		getOldNotes = new ObjectStep(this, ObjectID.CHEST_32572, new WorldPoint(3596, 9683, 0), "Search the chest for some notes.");
		((ObjectStep)(getOldNotes)).addAlternateObjects(ObjectID.CHEST_32573);
		talkToSafalaanWithNotes = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8216, new WorldPoint(3596, 9675, 0), "Talk to Safalaan with the notes.", oldNotes);
		talkToSafalaanWithNotes.addDialogStep("Here you go.");
		enterBaseAfterSerafina = new ObjectStep(this, NullObjectID.NULL_18120, new WorldPoint(3639, 3249, 0), "Prepare for a fight, then return to the Meiyerditch Myreque base.");
		enterBaseAfterSerafina.setLinePoints(pathToBase);

		talkToSafalaanForAbominationFight = new NpcStep(this, NpcID.SAFALAAN_HALLOW, new WorldPoint(3627, 9644, 0), "Talk to Safalaan, ready for a fight.");
		killAbomination = new NpcStep(this, NpcID.ABOMINATION, new WorldPoint(3627, 9644, 0), "Kill the abomination. It can be safe spotted with a long-ranged weapon.");
		((NpcStep)(killAbomination)).addAlternateNpcs(NpcID.ABOMINATION_8261, NpcID.ABOMINATION_8262);
		talkToSafalaanAfterAbominationFight = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8219, new WorldPoint(3627, 9644, 0), "Talk to Safalaan.");

		enterOldManRalBasement = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0), "Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.",
			rodOfIvandis, emerald, chisel, enchantEmeraldRunesOrTablet);
		((ObjectStep)(enterOldManRalBasement)).addAlternateObjects(ObjectID.TRAPDOOR_32577);

		enterRalWithFlail = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0), "Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.",
			ivandisFlail);
		((ObjectStep)(enterRalWithFlail)).addAlternateObjects(ObjectID.TRAPDOOR_32577);
		talkToSafalaanInRalBasement = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8216, new WorldPoint(3598, 9614, 0), "Talk to Safalaan.");
		talkToVertidaInRalBasement = new NpcStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3598, 9614, 0), "Talk to Vertida.");
		readFlaygianNotes = new DetailedQuestStep(this, "Read Flaygian's notes.", flaygianNotes);
		getSickle = new ObjectStep(this, ObjectID.CRATE_32575, new WorldPoint(3597, 9615, 0), "Search the north west crate for a blessed sickle.");
		getChain = new ObjectStep(this, ObjectID.CRATE_32576, new WorldPoint(3601, 9610, 0), "Search the south east crate for a chain.");
		useEmeraldOnSickle = new DetailedQuestStep(this, "Use an emerald on the blessed sickle.", emeraldHighlighted, sickleB);
		useEmeraldOnSickle.addDialogStep("Yes.");
		enchantSickle = new DetailedQuestStep(this, "Cast enchant emerald on the emerald sickle.", emeraldSickleB, enchantEmeraldRunesOrTablet);
		addSickleToRod = new DetailedQuestStep(this, "Add the enchanted emerald sickle to the rod of ivandis.", enchantedEmeraldSickleB, rodOfIvandisHighlighted);
		addSickleToRod.addDialogStep("Yes.");
		talkToSafalaanAfterFlail = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8216, new WorldPoint(3598, 9614, 0), "Talk to Safalaan again.");
		talkToSafalaanAfterFlail.addSubSteps(enterRalWithFlail);
		talkToKael = new NpcStep(this, NpcID.KAEL_FORSHAW_8231, new WorldPoint(3659, 3218, 0), "Talk to Kael outside the Theatre of Blood, prepared to fight Ranis.", ivandisFlailEquipped);
		talkToKael.addText("He can only be hurt by the flail, and uses magic and melee attacks.");
		talkToKael.addText("He will occasionally charge an attack and explode, dealing damage close to him. Just run away for this attack.");
		talkToKael.addText("During the fight he will spawn vyrewatch which you'll need to kill. Whilst fighting them Ranis will be throwing blood bombs at your current location, so make sure to move around.");
		talkToKael.addText("In his last phase he will only attack with melee, so make sure to use protect from melee!");
		talkToKael.addDialogStep("I'm ready.");

		talkToKaelSidebar = new NpcStep(this, NpcID.KAEL_FORSHAW_8231, new WorldPoint(3659, 3218, 0), "Talk to Kael outside the Theatre of Blood, prepared to fight Ranis.", ivandisFlailEquipped);
		talkToKaelSidebar.addSubSteps(talkToKael);

		killRanis = new NpcStep(this, NpcID.RANIS_DRAKAN_8244, new WorldPoint(2082, 4891, 0), "Defeat Ranis.", ivandisFlailEquipped);
		((NpcStep)(killRanis)).addAlternateNpcs(NpcID.RANIS_DRAKAN_8245, NpcID.RANIS_DRAKAN_8246, NpcID.RANIS_DRAKAN_8247, NpcID.RANIS_DRAKAN_8248);
		killRanis.addText("He can only be hurt by the flail, and uses magic and melee attacks.");
		killRanis.addText("He will occasionally charge an attack and explode, dealing damage close to him. Just run away for this attack.");
		killRanis.addText("During the fight he will spawn vyrewatch which you'll need to kill. Whilst fighting them Ranis will be throwing blood bombs at your current location, so make sure to move around.");
		killRanis.addText("In his last phase he will only attack with melee, so make sure to use protect from melee!");

		talkToKaelAgain = new NpcStep(this, NpcID.KAEL_FORSHAW_8231, new WorldPoint(3659, 3218, 0), "Talk to Kael outside the Theatre of Blood again.");
		enterRalForEnd = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0), "Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.");
		((ObjectStep)(enterRalForEnd)).addAlternateObjects(ObjectID.TRAPDOOR_32577);
		talkToSafalaanForEnd = new NpcStep(this, NpcID.SAFALAAN_HALLOW_8216, new WorldPoint(3598, 9614, 0), "Talk to Safalaan to complete the quest!");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(coins1000, vialOfWater, knife, emerald, chisel, enchantEmeraldRunesOrTablet, combatGear));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Abomination (level 149, safespottable)", "Ranis Drakan (level 233, melee only)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToGarth, enterBase, talkToSafalaan))));
		allSteps.add(new PanelDetails("Spying", new ArrayList<>(Arrays.asList(climbRubbleAtBank, talkToHarpert, climbRubbleAfterHarpert, climbSteamVent, jumpOffRoof, climbSecondVent, climbUpToRoof, climbDownFromRoof, lookThroughWindow))));
		allSteps.add(new PanelDetails("Investigating", new ArrayList<>(Arrays.asList(returnToBase, talkToSafalaanAfterSpying, talkToFlaygian, enterSerafinaHouse, talkToSafalaanInSerafinaHouse, searchForHerb, searchForMeat,
			searchForPestle, useHerbOnVial, usePestleOnMeat, useMeatOnPotion, usePotionOnDoor, talkToSafalaanAfterPotion, useHerbOnBlood, usePestleOnMeatAgain, useMeatOnBlood, useBloodOnDoor, getOldNotes, talkToSafalaanWithNotes,
			enterBaseAfterSerafina, talkToSafalaanForAbominationFight, killAbomination, talkToSafalaanAfterAbominationFight)), combatGear, vialOfWater));
		allSteps.add(new PanelDetails("Plotting revenge", new ArrayList<>(Arrays.asList(enterOldManRalBasement, talkToSafalaanInRalBasement, talkToVertidaInRalBasement, readFlaygianNotes, getSickle, getChain, useEmeraldOnSickle,
			enchantSickle, addSickleToRod, talkToSafalaanAfterFlail)), emerald, chisel, enchantEmeraldRunesOrTablet));
		allSteps.add(new PanelDetails("Rising up", new ArrayList<>(Arrays.asList(talkToKaelSidebar, killRanis, talkToKaelAgain, enterRalForEnd, talkToSafalaanForEnd)), combatGear, ivandisFlail));
		return allSteps;
	}
}
