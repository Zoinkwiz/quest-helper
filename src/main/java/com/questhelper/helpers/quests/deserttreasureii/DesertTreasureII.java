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

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.questinfo.QuestVarbits;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import com.questhelper.steps.widget.AncientSpells;
import net.runelite.api.NullObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.*;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;

public class DesertTreasureII extends BasicQuestHelper
{
	DetailedQuestStep attemptToEnterVaultDoor, attemptToEnterVaultDoor2, talkToAsgarnia,
		inspectStatueSE, inspectStatueSW, inspectStatueNE, inspectStatueNW, inspectPlaque,
		talkToAsgarniaAgain, talkToBalando, operateWinch, talkToBanikan, getPickaxe, mineRocks,
		enterDigsiteHole, killAncientGuardian, enterDigsiteHoleAgain, talkToBanikanInGolemRoom,
		inspectGolem, inspectAltar, castOnBloodStatue, castOnIceStatue, castOnShadowStatue,
		castOnSmokeStatue, searchCrateForCharges, imbueAtAltar, chargeGolem, operateGolem, searchVardorvis,
		searchPerseriya, searchSucellus, searchWhisperer, askAboutVardorvis, askAboutPerseriya, askAboutSucellus,
		askAboutWhisperer, talkToBanikanAfterGolem, operateGolemFrostenhorn;


	GolemPuzzleStep solveGolemPuzzle;

	SpellbookRequirement ancientMagicksActive;

	ItemRequirement waterSource, senntistenTeleport, pickaxe, combatGear, bloodBurstRunes, iceBurstRunes,
		shadowBurstRunes, smokeBurstRunes, allBursts, uncharedCells, chargedCells, xericTalisman,
		facemask, staminaPotions, eyeTeleport, rangedCombatGear, food, prayerPotions, nardahTeleport,
		arclight, freezes, icyBasalt, meleeCombatGear, ringOfVisibility, lassarTeleport, magicCombatGear;

	PrayerRequirement protectFromMagic;

	ItemRequirement whisperersMedallion, vardorvisMedallion, sucellusMedallion, perseriyaMedallion, hairClip, medallion;

	Zone vault, vault2, digsiteHole, golemRoom;
	Requirement inVault, inDigsiteHole, inGolemRoom;

	Requirement inspectedStatueSE, inspectedStatueSW, inspectedStatueNE, inspectedStatueNW, inspectedPlaque,
		inspectedGolem, inspectedAltar, bloodBeenCast, iceBeenCast, shadowBeenCast, smokeBeenCast, castAllSpells, inPuzzle;

	Requirement searchedVardorvis, searchedPerseriya, searchedSucellus, searchedWhisperer, askedAboutVardorvis,
		askedAboutPerseriya, askedAboutSucellus, askedAboutWhisperer;

	Requirement finishedVardorvis, finishedPerseriya, finishedSucellus, finishedWhisperer;

	VardorvisSteps vardorvisSteps;

	PerseriyaSteps perseriyaSteps;

	SucellusSteps sucellusSteps;

	WhispererSteps whispererSteps;

	DetailedQuestStep returnToDesertWithFinalMedallion, searchBedForHairClip, unlockCell, getItemsFromCell,
		investigateAltar, returnToMysteriousFigure, fightMysteriousFigure, enterAncientVault, returnToPickUpMedallion,
		pickUpMedallion, getMedallionFromChest, goDownSteps, enterVaultFinalSteps, defeatAssassin, defeatKetla,
		defeatKasonde, defeatPersten, cutsceneThenWights, talkToAzzanandra;

	Requirement cellUnlocked, itemsRetrieved, inCellRegion, defeatedStranger, medallionNearby, inFinalRoom,
		assassinAttacking, ketlaAttacking, kasondeAttacking, perstenAttacking, defeatedWights;

	Zone cellRegion, finalRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, attemptToEnterVaultDoor);
		steps.put(2, attemptToEnterVaultDoor);

		ConditionalStep goTalkToAsgarnia = new ConditionalStep(this, attemptToEnterVaultDoor2);
		goTalkToAsgarnia.addStep(inVault, talkToAsgarnia);
		steps.put(4, goTalkToAsgarnia);

		ConditionalStep inspectTheVault = new ConditionalStep(this, attemptToEnterVaultDoor2);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque, inspectedStatueNE,
			inspectedStatueNW, inspectedStatueSW), inspectStatueSE);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque, inspectedStatueNE,
			inspectedStatueNW), inspectStatueSW);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque, inspectedStatueNE), inspectStatueNW);
		inspectTheVault.addStep(new Conditions(inVault, inspectedPlaque), inspectStatueNE);
		inspectTheVault.addStep(inVault, inspectPlaque);
		steps.put(6, inspectTheVault);

		ConditionalStep goTalkToAsgarniaAgain = new ConditionalStep(this, attemptToEnterVaultDoor2);
		goTalkToAsgarniaAgain.addStep(inVault, talkToAsgarniaAgain);
		steps.put(8, goTalkToAsgarniaAgain);

		steps.put(10, talkToBalando);

		ConditionalStep goTalkToBanikan = new ConditionalStep(this, operateWinch);
		goTalkToBanikan.addStep(inDigsiteHole, talkToBanikan);

		steps.put(12, goTalkToBanikan);
		steps.put(14, goTalkToBanikan);
		steps.put(16, goTalkToBanikan);

		ConditionalStep goMineRocks = new ConditionalStep(this, operateWinch);
		goMineRocks.addStep(new Conditions(inDigsiteHole, pickaxe), mineRocks);
		goMineRocks.addStep(inDigsiteHole, getPickaxe);
		steps.put(18, goMineRocks);

		ConditionalStep goKillGolem = new ConditionalStep(this, operateWinch);
		goKillGolem.addStep(inGolemRoom, killAncientGuardian);
		goKillGolem.addStep(inDigsiteHole, enterDigsiteHole);
		steps.put(20, goKillGolem);
		steps.put(22, goKillGolem);

		ConditionalStep goTalkToBanikanAfterGolem = new ConditionalStep(this, operateWinch);
		goTalkToBanikanAfterGolem.addStep(inGolemRoom, talkToBanikanInGolemRoom);
		goTalkToBanikanAfterGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(24, goTalkToBanikanAfterGolem);
		steps.put(26, goTalkToBanikanAfterGolem);

		ConditionalStep goInspectAltarAndGolem = new ConditionalStep(this, operateWinch);
		// Go cast magic on pillars
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, chargedCells), chargeGolem);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, castAllSpells, uncharedCells),
			imbueAtAltar);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, castAllSpells), searchCrateForCharges);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, bloodBeenCast, shadowBeenCast,
			smokeBeenCast), castOnIceStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, bloodBeenCast, shadowBeenCast),
			castOnSmokeStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, bloodBeenCast),
			castOnShadowStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem, inspectedAltar),
			castOnBloodStatue);
		goInspectAltarAndGolem.addStep(new Conditions(inGolemRoom, inspectedGolem), inspectAltar);
		goInspectAltarAndGolem.addStep(inGolemRoom, inspectGolem);
		goInspectAltarAndGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(28, goInspectAltarAndGolem);
		steps.put(30, goInspectAltarAndGolem);

		ConditionalStep goDoGolemPuzzle = new ConditionalStep(this, operateWinch);
		goDoGolemPuzzle.addStep(new Conditions(inGolemRoom, inPuzzle), solveGolemPuzzle);
		goDoGolemPuzzle.addStep(inGolemRoom, chargeGolem);
		goDoGolemPuzzle.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(32, goDoGolemPuzzle);
		// Post-golem, 15110 0->1

		ConditionalStep goOperateGolem = new ConditionalStep(this, operateWinch);
		goOperateGolem.addStep(inGolemRoom, operateGolem);
		goOperateGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(34, goOperateGolem);

		ConditionalStep goSearchGolem = new ConditionalStep(this, operateWinch);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, askedAboutSucellus,
			askedAboutWhisperer), talkToBanikanAfterGolem);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, askedAboutSucellus,
			searchedWhisperer), askAboutWhisperer);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, askedAboutSucellus), searchWhisperer);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya, searchedSucellus), askAboutSucellus);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, askedAboutPerseriya), searchSucellus);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis, searchedPerseriya), askAboutPerseriya);
		goSearchGolem.addStep(new Conditions(inGolemRoom, askedAboutVardorvis), searchPerseriya);
		goSearchGolem.addStep(new Conditions(inGolemRoom, searchedVardorvis), askAboutVardorvis);
		goSearchGolem.addStep(inGolemRoom, searchVardorvis);
		goSearchGolem.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(36, goSearchGolem);

		ConditionalStep goOperateGolemLastTime = new ConditionalStep(this, operateWinch);
		goOperateGolemLastTime.addStep(inGolemRoom, operateGolemFrostenhorn);
		goOperateGolemLastTime.addStep(inDigsiteHole, enterDigsiteHoleAgain);
		steps.put(38, goOperateGolemLastTime);
		steps.put(40, goOperateGolemLastTime);

		vardorvisSteps.setLockingCondition(finishedVardorvis);
		whispererSteps.setLockingCondition(finishedWhisperer);
		sucellusSteps.setLockingCondition(finishedSucellus);
		perseriyaSteps.setLockingCondition(finishedPerseriya);

		ConditionalStep findingTheFour = new ConditionalStep(this, vardorvisSteps);
		findingTheFour.addStep(and(finishedVardorvis, finishedPerseriya, finishedSucellus), whispererSteps);
		findingTheFour.addStep(and(finishedVardorvis, finishedPerseriya), sucellusSteps);
		findingTheFour.addStep(finishedVardorvis, perseriyaSteps);
		steps.put(42, findingTheFour);
		/* Entered stranglewood */
		steps.put(44, findingTheFour);
		/* Entered vardorvis arena */
		steps.put(46, findingTheFour);
		/* Defeated vardorvis */
		steps.put(48, findingTheFour);
		/* Obtained the vardorvis medallion */
		steps.put(50, findingTheFour);
		/* After talk with stranger */
		steps.put(52, findingTheFour);
		/* Placed vardorvis medallion */
		steps.put(54, findingTheFour);
		steps.put(56, findingTheFour);
		steps.put(58, findingTheFour);
		steps.put(60, findingTheFour);
		steps.put(62, findingTheFour);
		steps.put(64, findingTheFour);
		steps.put(66, findingTheFour);
		steps.put(68, findingTheFour);
		steps.put(70, findingTheFour);
		steps.put(72, findingTheFour);
		steps.put(74, findingTheFour);
		steps.put(76, findingTheFour);
		steps.put(78, findingTheFour);
		steps.put(80, findingTheFour);
		steps.put(82, findingTheFour);
		steps.put(84, findingTheFour);

		steps.put(86, returnToDesertWithFinalMedallion);

		ConditionalStep escapeCell = new ConditionalStep(this, searchBedForHairClip);
		escapeCell.addStep(itemsRetrieved, investigateAltar);
		escapeCell.addStep(cellUnlocked, getItemsFromCell);
		escapeCell.addStep(hairClip, unlockCell);
		steps.put(88, escapeCell);
		steps.put(90, escapeCell);

		ConditionalStep defeatStranger = new ConditionalStep(this, returnToMysteriousFigure);
		defeatStranger.addStep(inCellRegion, fightMysteriousFigure);
		steps.put(92, defeatStranger);

		ConditionalStep goPlaceFinalMedallion = new ConditionalStep(this, returnToPickUpMedallion);
		goPlaceFinalMedallion.addStep(medallion, enterAncientVault);
		goPlaceFinalMedallion.addStep(medallionNearby, pickUpMedallion);
		goPlaceFinalMedallion.addStep(inCellRegion, getMedallionFromChest);
		steps.put(94, goPlaceFinalMedallion);
		steps.put(96, goPlaceFinalMedallion);
//		steps.put(98, defeatWights);
		ConditionalStep finalBit = new ConditionalStep(this, enterVaultFinalSteps);
		finalBit.addStep(and(inFinalRoom, defeatedWights), talkToAzzanandra);
		finalBit.addStep(and(inFinalRoom, perstenAttacking), defeatPersten);
		finalBit.addStep(and(inFinalRoom, kasondeAttacking), defeatKasonde);
		finalBit.addStep(and(inFinalRoom, ketlaAttacking), defeatKetla);
		finalBit.addStep(and(inFinalRoom, assassinAttacking), defeatAssassin);
		finalBit.addStep(inVault, goDownSteps);
		steps.put(100, finalBit);
		steps.put(102, finalBit);
		steps.put(104, finalBit);
		steps.put(106, finalBit);
		steps.put(108, finalBit);
		steps.put(110, finalBit);
		/* Post-fight */
		steps.put(112, finalBit);
		steps.put(114, finalBit);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		waterSource = new ItemRequirement("Full waterskin, or any protection from the desert", ItemID.WATER_CIRCLET_CHARGED);
		waterSource.addAlternates(ItemID.DESERT_AMULET_ELITE, ItemID.WATER_SKIN4, ItemID.WATER_SKIN3, ItemID.WATER_SKIN2, ItemID.WATER_SKIN1);
		ancientMagicksActive = new SpellbookRequirement(Spellbook.ANCIENT);

		// TODO: Add check where IF you have nardah teleport in scroll box, show scroll box as requirement
		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(VarbitID.BOOKOFSCROLLS_NARDAH, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_ELITE);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_HARD, ItemID.TELEPORTSCROLL_NARDAH, ItemID.DESERT_AMULET_MEDIUM);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		ItemRequirement digsitePendant = new ItemRequirement("Digsite pendant", ItemID.NECKLACE_OF_DIGSITE_5);
		digsitePendant.addAlternates(ItemID.NECKLACE_OF_DIGSITE_4, ItemID.NECKLACE_OF_DIGSITE_3, ItemID.NECKLACE_OF_DIGSITE_2,
			ItemID.NECKLACE_OF_DIGSITE_1);

		// TODO: Make a spell SpellRequirement, which will change the highlight to be for the spellbook widget not runes
		// Check if have requirement + runes for spell
		senntistenTeleport = new ItemRequirements(LogicType.OR, "Senntisten teleport",
			new ItemRequirement("Senntisten teleport", ItemID.TABLET_SENNTISTEN),
			new ItemRequirements(
				new ItemRequirement("Law rune", ItemID.LAWRUNE, 2),
				new ItemRequirement("Soul rune", ItemID.SOULRUNE)
			),
			digsitePendant
		);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		bloodBurstRunes = new ItemRequirements("Blood burst runes",
			new ItemRequirement("Death runes", ItemID.DEATHRUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOSRUNE, 4),
			new ItemRequirement("Blood runes", ItemID.BLOODRUNE, 2));
		iceBurstRunes = new ItemRequirements("Ice burst runes",
			new ItemRequirement("Death runes", ItemID.DEATHRUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOSRUNE, 4),
			new ItemRequirement("Water runes", ItemID.WATERRUNE, 4));
		shadowBurstRunes = new ItemRequirements("Shadow burst runes",
			new ItemRequirement("Death runes", ItemID.DEATHRUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOSRUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOULRUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIRRUNE, 1));
		smokeBurstRunes = new ItemRequirements("Smoke burst runes",
			new ItemRequirement("Death runes", ItemID.DEATHRUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOSRUNE, 4),
			new ItemRequirement("Fire runes", ItemID.FIRERUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIRRUNE, 2));
		allBursts = new ItemRequirements("Runes for shadow, smoke, blood, and ice burst",
			new ItemRequirement("Death runes", ItemID.DEATHRUNE, 8),
			new ItemRequirement("Chaos runes", ItemID.CHAOSRUNE, 16),
			new ItemRequirement("Blood runes", ItemID.BLOODRUNE, 2),
			new ItemRequirement("Water runes", ItemID.WATERRUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOULRUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIRRUNE, 3),
			new ItemRequirement("Fire runes", ItemID.FIRERUNE, 2));

		freezes = new ItemRequirement("Freezing spells STRONGLY recommended + reasonable mage accuracy", -1);

		rangedCombatGear = new ItemRequirement("Ranged combat gear", -1);
		rangedCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		xericTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERIC_TALISMAN);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS, 4);
		facemask = new ItemRequirement("Facemask", ItemCollections.SLAYER_HELMETS);
		facemask.addAlternates(ItemID.SLAYER_FACEMASK, ItemID.GASMASK);

		eyeTeleport = new ItemRequirement("Teleport to Temple of the Eye via minigame teleport or Amulet of the Eye", ItemID.GOTR_AMULET_OF_THE_EYE_RED);
		eyeTeleport.addAlternates(ItemID.GOTR_AMULET_OF_THE_EYE, ItemID.GOTR_AMULET_OF_THE_EYE_GREEN, ItemID.GOTR_AMULET_OF_THE_EYE_BLUE);

		arclight = new ItemRequirement("Arclight", ItemID.ARCLIGHT);

		icyBasalt = new ItemRequirement("Icy basalt", ItemID.WEISS_TELEPORT_BASALT);
		meleeCombatGear = new ItemRequirement("Melee combat gear", -1, -1);
		meleeCombatGear.setDisplayItemId(BankSlotIcons.getMeleeCombatGear());
		magicCombatGear = new ItemRequirement("Magic combat gear", -1, -1);
		magicCombatGear.setDisplayItemId(BankSlotIcons.getMagicCombatGear());
		lassarTeleport = new ItemRequirement("Mind altar or lassar teleport", ItemID.TELETAB_MIND_ALTAR);
		lassarTeleport.addAlternates(ItemID.TABLET_LASSAR);

		ringOfVisibility = new ItemRequirement("Ring of visibility", ItemID.FD_RING_VISIBILITY);

		protectFromMagic = new PrayerRequirement("Protect from Magic", Prayer.PROTECT_FROM_MAGIC);

		/* Quest Items */
		uncharedCells = new ItemRequirement("Uncharged cells", ItemID.DT2_UNCHARGED_CELL);
		chargedCells = new ItemRequirement("Charged cells", ItemID.DT2_CHARGED_CELL);

		perseriyaMedallion = new ItemRequirement("Perseriya's medallion", ItemID.DT2_MEDALLION_PERSERIYA);
		vardorvisMedallion = new ItemRequirement("Vardorvis' medallion", ItemID.DT2_MEDALLION_VARDORVIS);
		sucellusMedallion = new ItemRequirement("Sucellus' medallion", ItemID.DT2_MEDALLION_SUCELLUS);
		whisperersMedallion = new ItemRequirement("Whisperer's medallion", ItemID.DT2_MEDALLION_WHISPERER);
		hairClip = new ItemRequirement("Hair clip", ItemID.DT2_PURSUER_HAIRCLIP);

		medallion = new ItemRequirement("Medallion", ItemID.DT2_MEDALLION_WHISPERER);
		medallion.addAlternates(ItemID.DT2_MEDALLION_SUCELLUS, ItemID.DT2_MEDALLION_PERSERIYA, ItemID.DT2_MEDALLION_VARDORVIS);
	}

	@Override
	protected void setupZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		vault2 = new Zone(new WorldPoint(3159, 6421, 1), new WorldPoint(3181, 6442, 1));
		digsiteHole = new Zone(new WorldPoint(3400, 9800, 0), new WorldPoint(3419, 9824, 0));
		golemRoom = new Zone(new WorldPoint(2767, 6425, 0), new WorldPoint(2807, 6459, 0));
		cellRegion = new Zone(new WorldPoint(3082, 9243, 0), new WorldPoint(3122, 9271, 0));
		finalRoom = new Zone(new WorldPoint(3275, 6397, 0), new WorldPoint(3314, 6463, 0));
	}

	public void setupConditions()
	{
		inVault = new ZoneRequirement(vault, vault2);
		inDigsiteHole = new ZoneRequirement(digsiteHole);
		inGolemRoom = new ZoneRequirement(golemRoom);
		inFinalRoom = new ZoneRequirement(finalRoom);

		inspectedPlaque = new VarbitRequirement(15105, 1);
		inspectedStatueNE = new VarbitRequirement(15106, 1);
		inspectedStatueSE = new VarbitRequirement(15107, 1);
		inspectedStatueSW = new VarbitRequirement(15108, 1);
		inspectedStatueNW = new VarbitRequirement(15109, 1);
		// 12139 0->1 (cutscene specific ID)
		// VarPlayer 3575 3840 -> 7936

		inspectedGolem = new VarbitRequirement(QuestVarbits.QUEST_DESERT_TREASURE_II.getId(), 30,
			Operation.GREATER_EQUAL);
		// TODO: FIX CHECK FOR INSPECTED ALTAR, THOUGHT IT WAS 15111 BUT IT WASN'T
		inspectedAltar = new VarbitRequirement(15109, 1);

		// CAST BLOOD BARRAGE
		// 15116 0->4
		// 15119 0->1
		smokeBeenCast = new VarbitRequirement(15117, 1);
		shadowBeenCast = new VarbitRequirement(15118, 1);
		bloodBeenCast = new VarbitRequirement(15119, 1);
		iceBeenCast = new VarbitRequirement(15120, 1);

		castAllSpells = new VarbitRequirement(15116, 15);

		inPuzzle = new WidgetTextRequirement(838, 10, "One cell per row!");

		searchedVardorvis = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_VARDORVIS, 1, Operation.GREATER_EQUAL);
		searchedPerseriya = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_PERSERIYA, 1, Operation.GREATER_EQUAL);
		searchedSucellus = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_SUCELLUS, 1, Operation.GREATER_EQUAL);
		searchedWhisperer = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_WHISPERER, 1, Operation.GREATER_EQUAL);

		askedAboutVardorvis = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_VARDORVIS, 2, Operation.GREATER_EQUAL);
		askedAboutPerseriya = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_PERSERIYA, 2, Operation.GREATER_EQUAL);
		askedAboutSucellus = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_SUCELLUS, 2, Operation.GREATER_EQUAL);
		askedAboutWhisperer = new VarbitRequirement(VarbitID.DT2_WAR_ROOM_SEARCH_WHISPERER, 2, Operation.GREATER_EQUAL);

		// After frostenhorn:
		// 40->42

		// 15125 0->2
		// 15126 0->2
		// 15127 0->2
		// 15128 0->2

		// 15130 0->1

		// Probably 15132 0->1 for placed medallion works too

		// 15122 0->1, talked to Asgarnia with medallion first time?

		/* Perseriya */

		// Entered the Eye area:
		// 3575 36347648 -> 36413184
		// 14862 62->64
		// 15161 10->0
		// 14192 0->7

		// Enter Desert through Shanty Pass: 13137 0->50
		// With clothes on, 50->160
		// No top, 140
		// Updates upon damage taken

		// On login, 12164 0->1
		// 933 0->1
		// 13989 0->1

		// Spawned at 3326, 2896, 0:
		// 14192 0->7
		// Teled out of dessert, 13140 17->0?

		// At house:...
		//

		// Outside vault
		// 14192 0->7
		// Only got invaded once the counter hit 10

		// Placed perseriya's medallion
		// 15128 48->50
		// 14862 64->66
		// 15133 0->4

		finishedVardorvis = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 56, Operation.GREATER_EQUAL);
		finishedPerseriya = new VarbitRequirement(VarbitID.DT2_SCAR, 50, Operation.GREATER_EQUAL);
		finishedSucellus = new VarbitRequirement(VarbitID.DT2_GHORROCK, 70, Operation.GREATER_EQUAL);
		finishedWhisperer = new VarbitRequirement(VarbitID.DT2_LASSAR, 48, Operation.GREATER_EQUAL);

		// Items removed
		// 14283 0->3

		// In jail
		// 14862 86->88
		// 15129 0->1

		cellUnlocked = new VarbitRequirement(15124, 1);
		// Left cell, 88->90

		itemsRetrieved = new VarbitRequirement(14283, 0);
		// Stranger cutscene
		// 4606 0->3
		// 12139 0->1

		// Fight started
		// 14862 90->92

		inCellRegion = new ZoneRequirement(cellRegion);

		defeatedStranger = new VarbitRequirement(VarbitID.DT2, 94, Operation.GREATER_EQUAL);
		medallionNearby = new ItemOnTileRequirement(medallion);

		// Whisperer done / all done
		// 15126 46->48
		// TODO: Skipped step???
		// 14862 96->100
		// 15159 1->0

		// Cutscene with horn
		// 15174 0->1, ->2 during it?
		// 15174 2->3, Azzanadra transform

		// Sliske revealed
		// 3575 54525696 -> 58720000

		assassinAttacking = new NpcInteractingRequirement(NpcID.DT2_ASSASSIN_WIGHT_COMBAT);
		perstenAttacking = new NpcInteractingRequirement(NpcID.DT2_PERSTEN_WIGHT_COMBAT);
		ketlaAttacking = or(new NpcInteractingRequirement(NpcID.DT2_KETLA_WIGHT_COMBAT),
			new NpcInteractingRequirement(NpcID.DT2_KETLA_WIGHT_COMBAT_CLONE));
		kasondeAttacking = or(new NpcInteractingRequirement(NpcID.DT2_KASONDE_WIGHT_COMBAT),
			new NpcInteractingRequirement(NpcID.DT2_KASONDE_WIGHT_COMBAT_OPBREAK));

		defeatedWights = new VarbitRequirement(VarbitID.DT2, 112, Operation.GREATER_EQUAL);

		// Into dungeon, 15158 0->1

		// QUEST COMPLETED
		// 14862 114->118
		// 15175 0->1
		// 15138 0->1
	}

	public void setupSteps()
	{
		attemptToEnterVaultDoor = new ObjectStep(this, ObjectID.DT2_DESERT_VAULT_DOOR,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north-east of Nardah.", ancientMagicksActive);
		attemptToEnterVaultDoor.addTeleport(nardahTeleport);
		attemptToEnterVaultDoor.addDialogStep("Yes.");

		attemptToEnterVaultDoor2 = new ObjectStep(this, ObjectID.DT2_DESERT_VAULT_DOOR,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north-east of Nardah.");
		attemptToEnterVaultDoor.addSubSteps(attemptToEnterVaultDoor2);

		talkToAsgarnia = new NpcStep(this, NpcID.DT2_ASGARNIA_SMITH_VIS,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		inspectPlaque = new ObjectStep(this, ObjectID.DT2_VAULT_PLAQUE,
			new WorldPoint(3944, 9631, 1), "Inspect the plaque.");

		inspectStatueNE = new ObjectStep(this, NullObjectID.NULL_49499, new WorldPoint(3942, 9636, 1),
			"Inspect the north-east statue.");
		inspectStatueNW = new ObjectStep(this, NullObjectID.NULL_49501, new WorldPoint(3932, 9636, 1),
			"Inspect the north-west statue.");
		inspectStatueSW = new ObjectStep(this, NullObjectID.NULL_49503, new WorldPoint(3932, 9626, 1),
			"Inspect the south-west statue.");
		inspectStatueSE = new ObjectStep(this, NullObjectID.NULL_49505, new WorldPoint(3942, 9626, 1),
			"Inspect the south-east statue.");

		talkToAsgarniaAgain = new NpcStep(this, NpcID.DT2_ASGARNIA_SMITH_VIS,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		talkToBalando = new NpcStep(this, NpcID.ARCHAEOLOGICAL_EXPERT, new WorldPoint(3359, 3334, 0),
			"Talk to the Terry Balando in the Exam Centre found south-east of Varrock, " +
				"directly south of the Digsite.");
		talkToBalando.addTeleport(senntistenTeleport);

		operateWinch = new ObjectStep(this, ObjectID.DT2_DIGSITE_WINCH, new WorldPoint(3384, 3418, 0),
			"Enter the winch on the east side of the Digsite.", combatGear);
		talkToBanikan = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(3409, 9815, 0),
			"Talk to Dr Banikan.");

		getPickaxe = new ObjectStep(this, ObjectID.DT2_DIGSITE_PICKAXE_CRATE, new WorldPoint(3414, 9819, 0),
			"Get a pickaxe from the crate in the north-east of the cavern.");

		mineRocks = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Mine the rocks in the south-east of the cavern.", pickaxe);

		enterDigsiteHole = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south-east of the cavern, ready for a fight.", combatGear);

		enterDigsiteHoleAgain = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south-east of the cavern.");

		killAncientGuardian = new NpcStep(this, NpcID.DT2_ANCIENT_GUARDIAN_SHIELD, new WorldPoint(2783, 6431, 0)
			, "Kill the Ancient Guardian. The shield needs to be broken to hurt it, " +
			"and it will regenerate the shield unless you are using melee attacks with a pickaxe in the inventory.", combatGear);
		((NpcStep) killAncientGuardian).addAlternateNpcs(NpcID.DT2_ANCIENT_GUARDIAN);

		talkToBanikanInGolemRoom = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(2783, 6431, 0),
			"Talk to Dr Banikan in the room you defeated the Ancient Guardian.");
		talkToBanikanInGolemRoom.addSubSteps(enterDigsiteHoleAgain);

		inspectGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Inspect the golem to the north.");
		inspectAltar = new ObjectStep(this, NullObjectID.NULL_49512, new WorldPoint(2773, 6442, 0),
			"Inspect the altar to the north-west.");

		castOnBloodStatue = new NpcStep(this, NpcID.DT2_WAR_ROOM_BLOOD_TOTEM_INACTIVE, new WorldPoint(2775, 6445, 0),
			"Cast blood burst or higher on the blood totem.", bloodBurstRunes);
		castOnBloodStatue.addIcon(ItemID.FD_BLOOD_DIAMOND);
		castOnBloodStatue.addSpellHighlight(AncientSpells.BLOOD_BURST);
		castOnBloodStatue.addSpellHighlight(AncientSpells.BLOOD_BLITZ);
		castOnBloodStatue.addSpellHighlight(AncientSpells.BLOOD_BARRAGE);

		castOnShadowStatue = new NpcStep(this, NpcID.DT2_WAR_ROOM_SHADOW_TOTEM_INACTIVE, new WorldPoint(2771, 6445, 0),
			"Cast shadow burst or higher on the shadow totem.", shadowBurstRunes);
		castOnShadowStatue.addIcon(ItemID.FD_DARK_DIAMOND);
		castOnShadowStatue.addSpellHighlight(AncientSpells.SHADOW_BURST);
		castOnShadowStatue.addSpellHighlight(AncientSpells.SHADOW_BLITZ);
		castOnShadowStatue.addSpellHighlight(AncientSpells.SHADOW_BARRAGE);

		castOnSmokeStatue = new NpcStep(this, NpcID.DT2_WAR_ROOM_SMOKE_TOTEM_INACTIVE, new WorldPoint(2771, 6439, 0),
			"Cast smoke burst or higher on the smoke totem.", smokeBurstRunes);
		castOnSmokeStatue.addIcon(ItemID.FD_DIAMOND_FIRE);
		castOnSmokeStatue.addSpellHighlight(AncientSpells.SMOKE_BURST);
		castOnSmokeStatue.addSpellHighlight(AncientSpells.SMOKE_BLITZ);
		castOnSmokeStatue.addSpellHighlight(AncientSpells.SMOKE_BARRAGE);

		castOnIceStatue = new NpcStep(this, NpcID.DT2_WAR_ROOM_ICE_TOTEM_INACTIVE, new WorldPoint(2775, 6439, 0),
			"Cast ice burst or higher on the smoke totem.", iceBurstRunes);
		castOnIceStatue.addIcon(ItemID.FD_ICEDIAMOND);
		castOnIceStatue.addSpellHighlight(AncientSpells.ICE_BURST);
		castOnIceStatue.addSpellHighlight(AncientSpells.ICE_BLITZ);
		castOnIceStatue.addSpellHighlight(AncientSpells.ICE_BARRAGE);

		searchCrateForCharges = new ObjectStep(this, ObjectID.DT2_WAR_ROOM_CELL_CRATE,
			new WorldPoint(2780, 6440, 0), "Search the crate in the room with the golem.");
		imbueAtAltar = new ObjectStep(this, NullObjectID.NULL_49512, new WorldPoint(2773, 6442, 0),
			"Imbue the cells at the altar.", uncharedCells);

		chargeGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Inspect the golem again.");

		solveGolemPuzzle = new GolemPuzzleStep(this);

		operateGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Vardorvis\", \"Perseriya\", \"Sucellus\", and \"Whisperer\". " +
				"Make sure to talk to Banikan about each one!");
		operateGolem.addDialogStep("Yes.");

		searchVardorvis = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the term : \"Vardorvis\".");
		searchVardorvis.addDialogStep("Yes.");
		searchPerseriya = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Perseriya\".");
		searchPerseriya.addDialogStep("Yes.");
		searchSucellus = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Sucellus\".");
		searchSucellus.addDialogStep("Yes.");
		searchWhisperer = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Operate the golem, and search for the terms : \"Whisperer\".");
		searchWhisperer.addDialogStep("Yes.");

		askAboutVardorvis = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Vardorvis.");
		askAboutVardorvis.addDialogStep("Any thoughts on Vardorvis?");
		askAboutPerseriya = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Perseriya.");
		askAboutPerseriya.addDialogStep("Any thoughts on Perseriya?");
		askAboutSucellus = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Sucellus.");
		askAboutSucellus.addDialogStep("Any thoughts on Vardorvis?");
		askAboutWhisperer = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Whisperer.");
		askAboutWhisperer.addDialogStep("Any thoughts on Whisperer?");

		operateGolem.addSubSteps(searchVardorvis, searchPerseriya, searchSucellus, searchWhisperer, askAboutVardorvis,
			askAboutPerseriya, askAboutSucellus, askAboutWhisperer);

		talkToBanikanAfterGolem = new NpcStep(this, NpcID.DT2_BANIKAN_VIS, new WorldPoint(2783, 6444, 0),
			"Talk to Banikan about what to do next.");

		operateGolemFrostenhorn = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Try operating the golem again until Banikan leaves, and the golem lets you know the last search term.");

		/* Vardorvis */
		NpcStep talkToElissa = new NpcStep(this, NpcID.GOLEM_ELISSA, new WorldPoint(3378, 3428, 0), "Talk to Elissa in the north-east of the Digsite on the surface.");
		talkToElissa.addDialogStep("I hear you visited Lovakengj recently.");
		talkToElissa.addTeleport(senntistenTeleport);

		vardorvisSteps = new VardorvisSteps(this, talkToElissa, questBank);
		perseriyaSteps = new PerseriyaSteps(this, new DetailedQuestStep(this, "Do Perseriya steps."));
		sucellusSteps = new SucellusSteps(this, new DetailedQuestStep(this, "Do Sucellus steps."));
		whispererSteps = new WhispererSteps(this, new DetailedQuestStep(this, "Do Whisperer steps."), questBank);

		returnToDesertWithFinalMedallion = new ObjectStep(this, ObjectID.DT2_DESERT_VAULT_DOOR,
			new WorldPoint(3511, 2971, 0),
			"Return to the Vault door north-east of Nardah with the final medallion. Come equipped with two combat styles.",
			whisperersMedallion.hideConditioned(finishedWhisperer),
			vardorvisMedallion.hideConditioned(finishedVardorvis),
			sucellusMedallion.hideConditioned(finishedSucellus),
			perseriyaMedallion.hideConditioned(finishedPerseriya),
			meleeCombatGear, rangedCombatGear, food, prayerPotions);
		returnToDesertWithFinalMedallion.addTeleport(nardahTeleport);

		searchBedForHairClip = new ObjectStep(this, ObjectID.DT2_HIDEOUT_BED, new WorldPoint(3103, 9246, 0),
			"Search the bed for a hairclip.");
		unlockCell = new ObjectStep(this, ObjectID.DT2_HIDEOUT_PRISONGATE, new WorldPoint(3107, 9248, 0),
			"Escape the cell. Guess the correct code to open, with correct numbers in the correct place being marked in green, and correct numbers in the wrong places being marked with blue.", hairClip);
		getItemsFromCell = new ObjectStep(this, ObjectID.DT2_HIDEOUT_CHEST, new WorldPoint(3115, 9263, 0),
			"Retrieve your items from the chest in the north-east room.");
		investigateAltar = new ObjectStep(this, ObjectID.DT2_HIDEOUT_ALTAR_OP, new WorldPoint(3086, 9260, 0),
			"Inspect the altar in the north-west room.");
		fightMysteriousFigure = new NpcStep(this, NpcID.DT2_PURSUER_HIDEOUT_COMBAT, "Fight the Mysterious Figure. " +
			"When frozen, spam-click to move away to avoid taking damage. Use Protect from Magic for the entire fight.", protectFromMagic);
		enterAncientVault = new ObjectStep(this, ObjectID.DT2_DESERT_VAULT_DOOR, new WorldPoint(3511, 2971, 0),
			"Enter to the Vault door north-east of Nardah with the final medallion, ready to fight.",
			whisperersMedallion.hideConditioned(finishedWhisperer),
			vardorvisMedallion.hideConditioned(finishedVardorvis),
			sucellusMedallion.hideConditioned(finishedSucellus),
			perseriyaMedallion.hideConditioned(finishedPerseriya),
			combatGear, food, prayerPotions);
		enterAncientVault.addTeleport(nardahTeleport);

		returnToMysteriousFigure = new ObjectStep(this, NullObjectID.NULL_49497, new WorldPoint(3175, 2887, 0),
			"Return to fight the Mysterious Figure, through the portal south of the Quarry in the desert.", meleeCombatGear, rangedCombatGear,
			food, prayerPotions);
		fightMysteriousFigure.addSubSteps(returnToMysteriousFigure);

		returnToPickUpMedallion = new ObjectStep(this, NullObjectID.NULL_49497, new WorldPoint(3175, 2887, 0),
			"Retrieve the medallion from the portal south of the Quarry.");
		getMedallionFromChest = new ObjectStep(this, ObjectID.DT2_HIDEOUT_CHEST, new WorldPoint(3115, 9263, 0),
			"Retrieve the medallion from the chest in the north-east room.");

		pickUpMedallion = new ItemStep(this, "Pick up the medallion.", medallion);
		pickUpMedallion.addSubSteps(returnToPickUpMedallion, getMedallionFromChest);

		enterVaultFinalSteps = new ObjectStep(this, ObjectID.DT2_DESERT_VAULT_DOOR, new WorldPoint(3511, 2971, 0),
			"Enter to the Vault door north-east of Nardah, ready to fight.",
			combatGear, food, prayerPotions);
		enterVaultFinalSteps.addTeleport(nardahTeleport);
		enterAncientVault.addSubSteps(enterVaultFinalSteps);

		goDownSteps = new ObjectStep(this, ObjectID.DT2_VAULT_MAIN_STAIRCASE, new WorldPoint(3169, 6431, 1),
			"Go deeper into the Vault down the steps.", combatGear);

		defeatAssassin = new NpcStep(this, NpcID.DT2_ASSASSIN_WIGHT_COMBAT, new WorldPoint(3296, 6444, 0),
			"Defeat The Forsaken Assassin. Protect from ranged, and lure him into the white smoke. He will sometimes throw 3 vials, avoid them, as they will poison you and deal large damage.");
		defeatKetla = new NpcStep(this, NpcID.DT2_KETLA_WIGHT_COMBAT, new WorldPoint(3296, 6444, 0),
			"Defeat Ketla the Unworthy. Protect from ranged. She will spawn shadow clones, which you should hide behind whenever she has a skull above her.");
		((NpcStep) defeatKetla).addAlternateNpcs(NpcID.DT2_KETLA_WIGHT_COMBAT_CLONE);
		defeatKasonde = new NpcStep(this, NpcID.DT2_KASONDE_WIGHT_COMBAT, new WorldPoint(3296, 6444, 0),
			"Defeat Kasonde the Craven. Protect from ranged if at a distance or melee if up close. The fight is similar to earlier in the quest.");
		((NpcStep) defeatKasonde).addAlternateNpcs(NpcID.DT2_KASONDE_WIGHT_COMBAT_OPBREAK);
		defeatPersten = new NpcStep(this, NpcID.DT2_PERSTEN_WIGHT_COMBAT, new WorldPoint(3296, 6444, 0),
			"Defeat Persten the Deceitful. Protect from Magic. Avoid the lighting strikes by moving. Destroy any portals she spawns, or run from corner to corner avoiding the leeches that spawn from the portal.");

		cutsceneThenWights = new DetailedQuestStep(this, "Watch the cutscene, then defeat the wights.");

		talkToAzzanandra = new NpcStep(this, NpcID.DT2_AZZANADRA_VIS, new WorldPoint(3298, 6441, 0),
			"Talk to Azzanadra.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, allBursts, facemask, ringOfVisibility);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(nardahTeleport, waterSource, senntistenTeleport, staminaPotions, freezes, xericTalisman, eyeTeleport,
			icyBasalt, lassarTeleport);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Ancient Guardian (level 153)");
		reqs.add("Vardorvis (level 553)");
		reqs.add("Kasonde (level 193)");
		reqs.add("A number of lesser demons, black demons, and abyssal creatures");
		reqs.add("The Leviathan (level 577)");
		reqs.add("Jhallan (level 491)");
		reqs.add("Duke Sucellus (level 519)");
		reqs.add("The Whisperer (level 568)");
		reqs.add("Mysterious Figure (level 271)");
		reqs.add("The Forsaken Assassin (level 252)");
		reqs.add("Ketla the Unworthy (level 236)");
		reqs.add("Kasonde the Craven (level 221)");
		reqs.add("Persten the Deceitful (level 264)");
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(5);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Ring of Shadows", ItemID.RING_OF_SHADOWS),
			new ItemReward("Ancient Lamp (100k exp in a combat skill)", ItemID.DT2_REWARD_LAMP, 3)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("Ability to use Ancient Rings."),
			new UnlockReward("Access to four new bosses"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("The Vault",
			Arrays.asList(attemptToEnterVaultDoor, talkToAsgarnia, inspectPlaque, inspectStatueNE,
				inspectStatueNW, inspectStatueSW, inspectStatueSE, talkToAsgarniaAgain),
			List.of(ancientMagicksActive),
			Arrays.asList(nardahTeleport, waterSource, senntistenTeleport)));

		allSteps.add(new PanelDetails("Learning of the Ancients",
			Arrays.asList(talkToBalando, operateWinch, talkToBanikan, getPickaxe, mineRocks,
				enterDigsiteHole, killAncientGuardian, talkToBanikanInGolemRoom, inspectGolem,
				inspectAltar, castOnBloodStatue, castOnShadowStatue, castOnSmokeStatue, castOnIceStatue,
				searchCrateForCharges, imbueAtAltar, chargeGolem, solveGolemPuzzle, operateGolem, talkToBanikanAfterGolem,
				operateGolemFrostenhorn),
			Arrays.asList(combatGear, allBursts),
			List.of(senntistenTeleport)));

		PanelDetails vardorvisPanel = new PanelDetails("Vardorvis",
			vardorvisSteps.getDisplaySteps(),
			Collections.singletonList(combatGear),
			Arrays.asList(xericTalisman, freezes));
		vardorvisPanel.setLockingStep(vardorvisSteps);
		allSteps.add(vardorvisPanel);

		PanelDetails perseriyaPanel = new PanelDetails("Perseriya",
			perseriyaSteps.getStartSteps(),
			Arrays.asList(combatGear, facemask),
			Arrays.asList(eyeTeleport, staminaPotions, arclight));
		perseriyaPanel.setLockingStep(perseriyaSteps);
		allSteps.add(perseriyaPanel);
		allSteps.add(new PanelDetails("Perseriya - Room 1",
			perseriyaSteps.getRoom1Steps(),
			Collections.singletonList(facemask),
			Arrays.asList(eyeTeleport, staminaPotions, arclight)));
		allSteps.add(new PanelDetails("Perseriya - Room 2",
			perseriyaSteps.getRoom2Steps(),
			List.of(facemask),
			Arrays.asList(eyeTeleport, staminaPotions, arclight)));
		allSteps.add(new PanelDetails("Perseriya - Room 3",
			perseriyaSteps.getRoom3Steps(),
			List.of(facemask),
			Arrays.asList(eyeTeleport, staminaPotions, arclight)));
		allSteps.add(new PanelDetails("Perseriya - The battle",
			perseriyaSteps.getBattleSteps(),
			Arrays.asList(rangedCombatGear, shadowBurstRunes),
			Arrays.asList(eyeTeleport, staminaPotions, food, prayerPotions)));

		PanelDetails sucellusPanel = new PanelDetails("Sucellus",
			sucellusSteps.getDisplaySteps(),
			Arrays.asList(meleeCombatGear, food),
			Arrays.asList(prayerPotions, staminaPotions, icyBasalt));
		sucellusPanel.setLockingStep(sucellusSteps);
		allSteps.add(sucellusPanel);

		PanelDetails whispererPanel = new PanelDetails("The Whisperer",
			whispererSteps.getDisplaySteps(),
			Arrays.asList(magicCombatGear, ringOfVisibility, food),
			Arrays.asList(prayerPotions, staminaPotions, lassarTeleport));
		whispererPanel.setLockingStep(whispererSteps);
		allSteps.add(whispererPanel);
		allSteps.add(new PanelDetails("The Whisperer - Choir",
			whispererSteps.getDisplayStepsSilentChoir(),
			Arrays.asList(magicCombatGear, ringOfVisibility, food),
			Arrays.asList(prayerPotions, staminaPotions, lassarTeleport)));

		allSteps.add(new PanelDetails("Secrets",
			Arrays.asList(returnToDesertWithFinalMedallion, searchBedForHairClip, unlockCell,
				getItemsFromCell, investigateAltar, fightMysteriousFigure, pickUpMedallion, enterAncientVault,
				goDownSteps, cutsceneThenWights, defeatAssassin, defeatKetla, defeatKasonde, defeatPersten,
				talkToAzzanandra),
			Arrays.asList(meleeCombatGear, rangedCombatGear, food, prayerPotions),
			Arrays.asList(nardahTeleport, staminaPotions)));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.DESERT_TREASURE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SECRETS_OF_THE_NORTH, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.ENAKHRAS_LAMENT, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.TEMPLE_OF_THE_EYE, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.THE_GARDEN_OF_DEATH, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.BELOW_ICE_MOUNTAIN, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.HIS_FAITHFUL_SERVANTS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.MAGIC, 75));
		req.add(new SkillRequirement(Skill.FIREMAKING, 75));
		req.add(new SkillRequirement(Skill.THIEVING, 70));
		req.add(new SkillRequirement(Skill.HERBLORE, 62));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 60));
		req.add(new SkillRequirement(Skill.CONSTRUCTION, 60));
		req.add(ancientMagicksActive);

		return req;
	}
}
