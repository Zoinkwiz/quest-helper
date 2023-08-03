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
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.DESERT_TREASURE_II
)
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
		shadowBurstRunes, smokeBurstRunes, allBursts, uncharedCells, chargedCells, nardahTeleport, xericTalisman;

	Zone vault, digsiteHole, golemRoom;
	Requirement inVault, inDigsiteHole, inGolemRoom;

	Requirement inspectedStatueSE, inspectedStatueSW, inspectedStatueNE, inspectedStatueNW, inspectedPlaque,
	inspectedGolem, inspectedAltar, bloodBeenCast, iceBeenCast, shadowBeenCast, smokeBeenCast, castAllSpells, inPuzzle;

	Requirement searchedVardorvis, searchedPerseriya, searchedSucellus, searchedWhisperer, askedAboutVardorvis,
		askedAboutPerseriya, askedAboutSucellus, askedAboutWhisperer;

	/* Vardovis */
	DetailedQuestStep talkToElissa, talkToBarus, searchDesk, readPotionNote, drinkPotion, boardBoat, runIntoStanglewood,
		talkToKasonde, enterEntry, defendKasonde, defendKasondeSidebar, leaveTowerDefenseRoom, talkToKasondeAfterTowerDefense,
		getBerry, getHerb, getHerbSidebar, goDownToKasonde, defendKasondeHerb, talkToKasondeWithHerbAndBerry, addHerb, addBerry,
		goToRitualSite;
	Requirement talkedToElissa, talkedToBarus, haveReadPotionNote, haveDrunkPotion, inStrangewood, finishedStranglewoodCutscene,
		talkedToKasonde, inTowerDefenseRoom, defendedKasonde, toldAboutHerbAndBerry, herbTaken, berryTaken,
		inStranglewoodPyramidRoom, defendedKasondeWithHerb, receivedSerum;
	ItemRequirement potionNote, potion, freezes, berry, herb, unfinishedSerum, serumWithHerb, completeSerum;
	Zone stranglewood, towerDefenseRoom, stranglewoodPyramidRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
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

		ConditionalStep findingVardorvis = new ConditionalStep(this, talkToElissa);
		findingVardorvis.addStep(new Conditions(inStranglewoodPyramidRoom, defendedKasondeWithHerb, herbTaken, berryTaken), talkToKasondeWithHerbAndBerry);
		findingVardorvis.addStep(new Conditions(inStranglewoodPyramidRoom, herbTaken, berryTaken), defendKasondeHerb);
		findingVardorvis.addStep(new Conditions(inStrangewood, herbTaken, berryTaken), goDownToKasonde);
		findingVardorvis.addStep(new Conditions(inStrangewood, herbTaken), getBerry);
		findingVardorvis.addStep(new Conditions(inStrangewood, toldAboutHerbAndBerry), getHerb);
		findingVardorvis.addStep(new Conditions(inTowerDefenseRoom, defendedKasonde), leaveTowerDefenseRoom);
		findingVardorvis.addStep(new Conditions(inStrangewood, defendedKasonde), talkToKasondeAfterTowerDefense);
		findingVardorvis.addStep(new Conditions(inTowerDefenseRoom), defendKasonde);
		findingVardorvis.addStep(new Conditions(inStrangewood, talkedToKasonde), enterEntry);
		findingVardorvis.addStep(new Conditions(inStrangewood, finishedStranglewoodCutscene), talkToKasonde);
		findingVardorvis.addStep(new Conditions(inStrangewood), runIntoStanglewood);
		findingVardorvis.addStep(new Conditions(haveDrunkPotion), boardBoat);
		findingVardorvis.addStep(new Conditions(haveReadPotionNote, potion.alsoCheckBank(questBank)), drinkPotion);
		findingVardorvis.addStep(new Conditions(nor(haveReadPotionNote), potionNote.alsoCheckBank(questBank)), readPotionNote);
		findingVardorvis.addStep(talkedToBarus, searchDesk);
		findingVardorvis.addStep(talkedToElissa, talkToBarus);

		ConditionalStep findingTheFour = new ConditionalStep(this, findingVardorvis);
		steps.put(42, findingTheFour);
		/* Entered stranglewood */
		steps.put(44, findingTheFour);
		return steps;
	}

	@Override
	public void setupRequirements()
	{
		waterSource = new ItemRequirement("Full waterskin, or any protection from the desert", ItemID.CIRCLET_OF_WATER);
		waterSource.addAlternates(ItemID.DESERT_AMULET_4, ItemID.WATERSKIN4, ItemID.WATERSKIN3, ItemID.WATERSKIN2, ItemID.WATERSKIN1);
		ancientMagicksActive = new SpellbookRequirement(Spellbook.ANCIENT);

		nardahTeleport = new ItemRequirement("Nardah teleport", ItemID.DESERT_AMULET_4);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_3, ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_2);

		ItemRequirement digsitePendant = new ItemRequirement("Digsite pendant", ItemID.DIGSITE_PENDANT_5);
		digsitePendant.addAlternates(ItemID.DIGSITE_PENDANT_4, ItemID.DIGSITE_PENDANT_3, ItemID.DIGSITE_PENDANT_2,
			ItemID.DIGSITE_PENDANT_1);

		senntistenTeleport = new ItemRequirements(LogicType.OR, "Senntisten teleport",
			new ItemRequirement("Senntisten teleport", ItemID.SENNTISTEN_TELEPORT),
			new ItemRequirements(
				new ItemRequirement("Law rune", ItemID.LAW_RUNE, 2),
				new ItemRequirement("Soul rune", ItemID.SOUL_RUNE)
			),
			digsitePendant
		);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);

		combatGear = new ItemRequirement("Combat gear", -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		bloodBurstRunes = new ItemRequirements("Blood burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2));
		iceBurstRunes = new ItemRequirements("ice burst runes",
		new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
		new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
		new ItemRequirement("Water runes", ItemID.WATER_RUNE, 4));
		shadowBurstRunes = new ItemRequirements("Shadow burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Blood runes", ItemID.SOUL_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 1));
		smokeBurstRunes = new ItemRequirements("Smoke burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 2));
		allBursts = new ItemRequirements("Runes for shadow, smoke, blood, and ice burst", bloodBurstRunes, iceBurstRunes, shadowBurstRunes, smokeBurstRunes);

		xericTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERICS_TALISMAN);

		/* Quest Items */
		uncharedCells = new ItemRequirement("Uncharged cells", ItemID.UNCHARGED_CELL_28402);
		chargedCells = new ItemRequirement("Charged cells", ItemID.CHARGED_CELL);

		potionNote = new ItemRequirement("Potion note", ItemID.POTION_NOTE);
		potion = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION_28383);
		freezes = new ItemRequirement("Freezing spells STRONGLY recommended", -1);
		berry = new ItemRequirement("Argian berries", ItemID.ARGIAN_BERRIES);
		herb = new ItemRequirement("Korbal herb", ItemID.KORBAL_HERB);
		unfinishedSerum = new ItemRequirement("Unfinished serum", ItemID.UNFINISHED_SERUM);
		serumWithHerb = new ItemRequirement("Unfinished serum (herb added)", ItemID.UNFINISHED_SERUM_28387);
		completeSerum = new ItemRequirement("Strangler serum", ItemID.STRANGLER_SERUM);
	}

	public void loadZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		digsiteHole = new Zone(new WorldPoint(3400, 9800, 0), new WorldPoint(3419, 9824, 0));
		golemRoom = new Zone(new WorldPoint(2767, 6425, 0), new WorldPoint(2807, 6459, 0));
		stranglewood = new Zone(new WorldPoint(1087, 3264, 0), new WorldPoint(1261, 3458, 0));
		towerDefenseRoom = new Zone(new WorldPoint(1160, 9740, 0), new WorldPoint(1210, 9780, 0));
		stranglewoodPyramidRoom = new Zone(new WorldPoint(1177, 9810, 0), new WorldPoint(1190, 9846, 0));
	}

	public void setupConditions()
	{
		inVault = new ZoneRequirement(vault);
		inDigsiteHole = new ZoneRequirement(digsiteHole);
		inGolemRoom = new ZoneRequirement(golemRoom);

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

		searchedVardorvis = new VarbitRequirement(15111, 1, Operation.GREATER_EQUAL);
		searchedPerseriya = new VarbitRequirement(15112, 1, Operation.GREATER_EQUAL);
		searchedSucellus = new VarbitRequirement(15113, 1, Operation.GREATER_EQUAL);
		searchedWhisperer = new VarbitRequirement(15114, 1, Operation.GREATER_EQUAL);

		askedAboutVardorvis = new VarbitRequirement(15111, 2, Operation.GREATER_EQUAL);
		askedAboutPerseriya = new VarbitRequirement(15112, 2, Operation.GREATER_EQUAL);
		askedAboutSucellus = new VarbitRequirement(15113, 2, Operation.GREATER_EQUAL);
		askedAboutWhisperer = new VarbitRequirement(15114, 2, Operation.GREATER_EQUAL);

		// After frostenhorn:
		// 40->42

		// 15125 0->2
		// 15126 0->2
		// 15127 0->2
		// 15128 0->2

		// 15130 0->1

		/* Vardorvis */
		talkedToElissa = new VarbitRequirement(15125, 4, Operation.GREATER_EQUAL);
		talkedToBarus = new VarbitRequirement(15125, 6, Operation.GREATER_EQUAL);
		haveReadPotionNote = new VarbitRequirement(15125, 8, Operation.GREATER_EQUAL);
		haveDrunkPotion = new VarbitRequirement(15125, 10, Operation.GREATER_EQUAL);
		inStrangewood = new ZoneRequirement(stranglewood);
//		seenStranglewoodCutscene = new VarbitRequirement(15125, 14, Operation.GREATER_EQUAL);
		finishedStranglewoodCutscene = new VarbitRequirement(15125, 16, Operation.GREATER_EQUAL);
		// Entered Stranglewood
		// 15125 10->12
		// 14862 42->44
		// 15160 0->3, happens whenever you enter the Stranglewood
		// 15099 = how infected you are

		// Cutscene upon entering area
		// 15160 3->2
		// 12139 0->1

		// After Cutscene, 15099 44->39
		// 12427 0->1
		// 12428 0->2
		talkedToKasonde = new VarbitRequirement(15125, 18, Operation.GREATER_EQUAL);

		// 15100, 0->400 (is a timer for tower defense)
		// 15125 18->20, gone into Entry
		// 15101 0->85 (Kasonde's health)

		// A, SW CHest
		// VarClientInt 1121 0->5
		// VarClientInt 1122 0->5
		// VarClientInt 1123 0->5
		inTowerDefenseRoom = new ZoneRequirement(towerDefenseRoom);

		defendedKasonde = new VarbitRequirement(15125, 22, Operation.GREATER_EQUAL);
		toldAboutHerbAndBerry = new VarbitRequirement(15125, 24, Operation.GREATER_EQUAL);
		// 15136 0->2 taken herb
		// 15125 24->26, herb taken
		herbTaken = new VarbitRequirement(15136, 2);
		// 15125 26->28, picked berry
		// 15137, 0->1 berry taken
		berryTaken = new VarbitRequirement(15137, 1);

		// 15125 28->30->32 when entering pyramid
		inStranglewoodPyramidRoom = new ZoneRequirement(stranglewoodPyramidRoom);
		defendedKasondeWithHerb = new VarbitRequirement(15125, 34, Operation.GREATER_EQUAL);
		receivedSerum = new VarbitRequirement(15125, 36, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		attemptToEnterVaultDoor = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north east of Nardah.", ancientMagicksActive);
		attemptToEnterVaultDoor.addDialogStep("Yes.");

		attemptToEnterVaultDoor2 = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north east of Nardah.");
		attemptToEnterVaultDoor.addSubSteps(attemptToEnterVaultDoor2);

		talkToAsgarnia = new NpcStep(this, NpcID.ASGARNIA_SMITH_12291,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		inspectPlaque = new ObjectStep(this, ObjectID.PLAQUE_48798,
			new WorldPoint(3944, 9631, 1), "Inspect the plaque.");

		inspectStatueNE = new ObjectStep(this, NullObjectID.NULL_49499, new WorldPoint(3942, 9636, 1),
			"Inspect the north east statue.");
		inspectStatueNW = new ObjectStep(this, NullObjectID.NULL_49501, new WorldPoint(3932, 9636, 1),
			"Inspect the north west statue.");
		inspectStatueSW = new ObjectStep(this, NullObjectID.NULL_49503, new WorldPoint(3932, 9626, 1),
			"Inspect the south west statue.");
		inspectStatueSE = new ObjectStep(this, NullObjectID.NULL_49505, new WorldPoint(3942, 9626, 1),
			"Inspect the south east statue.");

		talkToAsgarniaAgain = new NpcStep(this, NpcID.ASGARNIA_SMITH_12291,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		talkToBalando = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3359, 3334, 0),
			"Talk to the Terry Balando in the Exam Centre found south-east of Varrock, " +
				"directly south of the Digsite.", Arrays.asList(), Collections.singletonList(senntistenTeleport));

		operateWinch = new ObjectStep(this, ObjectID.WINCH_48918, new WorldPoint(3384, 3418, 0),
			"Enter the winch on the east side of the Digsite.", combatGear);
		talkToBanikan = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(3409, 9815, 0),
			"Talk to Dr Banikan.");

		getPickaxe = new ObjectStep(this, ObjectID.CRATE_48923, new WorldPoint(3414, 9819, 0),
			"Get a pickaxe from the crate in the north east of the cavern.");

		mineRocks = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Mine the rocks in the south east of the cavern.", pickaxe);

		enterDigsiteHole = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south east of the cavern, ready for a fight.", combatGear);

		enterDigsiteHoleAgain = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south east of the cavern.");

		killAncientGuardian = new NpcStep(this, NpcID.ANCIENT_GUARDIAN_12337, new WorldPoint(2783, 6431, 0)
		, "Kill the Ancient Guardian. The shield needs to be broken to hurt it, " +
			"and it will regenerate the shield unless you are using melee attacks with a pickaxe in the inventory.", combatGear);
		((NpcStep) killAncientGuardian).addAlternateNpcs(NpcID.ANCIENT_GUARDIAN_12336);

		talkToBanikanInGolemRoom = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6431, 0),
			"Talk to Dr Banikan in the room you defeated the Ancient Guardian.");
		talkToBanikanInGolemRoom.addSubSteps(enterDigsiteHoleAgain);

		inspectGolem = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Inspect the golem to the north.");
		inspectAltar = new ObjectStep(this, NullObjectID.NULL_49512, new WorldPoint(2773, 6442, 0),
			"Inspect the altar to the north west.");

		castOnBloodStatue = new NpcStep(this, NpcID.BLOOD_TOTEM, new WorldPoint(2775, 6445, 0),
			"Cast blood burst or higher on the blood totem.", bloodBurstRunes);
		castOnBloodStatue.addIcon(ItemID.BLOOD_DIAMOND);
		castOnBloodStatue.addWidgetHighlight(218, 82);

		castOnShadowStatue = new NpcStep(this, NpcID.SHADOW_TOTEM, new WorldPoint(2771, 6445, 0),
			"Cast shadow burst or higher on the shadow totem.", shadowBurstRunes);
		castOnShadowStatue.addIcon(ItemID.SHADOW_DIAMOND);
		castOnShadowStatue.addWidgetHighlight(218, 90);

		castOnSmokeStatue = new NpcStep(this, NpcID.SMOKE_TOTEM, new WorldPoint(2771, 6439, 0),
			"Cast smoke burst or higher on the smoke totem.", smokeBurstRunes);
		castOnSmokeStatue.addIcon(ItemID.SMOKE_DIAMOND);
		castOnSmokeStatue.addWidgetHighlight(218, 86);

		castOnIceStatue = new NpcStep(this, NpcID.ICE_TOTEM, new WorldPoint(2775, 6439, 0),
			"Cast ice burst or higher on the smoke totem.", iceBurstRunes);
		castOnIceStatue.addIcon(ItemID.ICE_DIAMOND);
		castOnIceStatue.addWidgetHighlight(218, 78);

		searchCrateForCharges = new ObjectStep(this, ObjectID.CRATE_48931,
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

		askAboutVardorvis = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Vardorvis.");
		askAboutVardorvis.addDialogStep("Any thoughts on Vardorvis?");
		askAboutPerseriya = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Perseriya.");
		askAboutPerseriya.addDialogStep("Any thoughts on Perseriya?");
		askAboutSucellus = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Sucellus.");
		askAboutSucellus.addDialogStep("Any thoughts on Vardorvis?");
		askAboutWhisperer = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Ask Dr Banikan about Whisperer.");
		askAboutWhisperer.addDialogStep("Any thoughts on Whisperer?");

		operateGolem.addSubSteps(searchVardorvis, searchPerseriya, searchSucellus, searchWhisperer, askAboutVardorvis,
			askAboutPerseriya, askAboutSucellus, askAboutWhisperer);

		talkToBanikanAfterGolem = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(2783, 6444, 0),
			"Talk to Banikan about what to do next.");

		operateGolemFrostenhorn = new ObjectStep(this, NullObjectID.NULL_49511, new WorldPoint(2783, 6444, 0),
			"Try operating the golem again until Banikan leaves, and the golem lets you know the last search term.");


		/* Vardovis */
		talkToElissa = new NpcStep(this, NpcID.ELISSA, new WorldPoint(3378, 3428, 0), "Talk to Elissa in the north east of the Digsite on the surface.");
		talkToElissa.addDialogStep("I hear you visited Lovakengj recently.");
		talkToBarus = new NpcStep(this, NpcID.BARUS, new WorldPoint(1459, 3782, 0), "Talk to Barus near the burning man in south west Lovakengj.");
		talkToBarus.addTeleport(xericTalisman.named("Xeric's talisman (Xeric's Inferno)"));

		searchDesk = new ObjectStep(this, NullObjectID.NULL_49490, new WorldPoint(1781, 3619, 0),
			"Search the desk in the house south of the Hosidius Estate Agent.");
		searchDesk.addTeleport(xericTalisman.named("Xeric's talisman (Xeric's Glade)"));

		readPotionNote = new ItemStep(this, "Read the potion note.", potionNote.highlighted());
		drinkPotion = new ItemStep(this, "Drink the strange potion.", potion.highlighted());
		boardBoat = new ObjectStep(this, NullObjectID.NULL_49491, new WorldPoint(1227, 3470, 0),
			"Board the boat south of Quidamortem into The Stranglewood.");
		runIntoStanglewood = new DetailedQuestStep(this, new WorldPoint(1194, 3394, 0), "Run deeper into Stranglewood. " +
			"Be careful of the Strangled, as they'll bind you and deal damage.");
		talkToKasonde = new NpcStep(this, NpcID.KASONDE, new WorldPoint(1191, 3404, 0), "Talk to Kasonde.");
		enterEntry = new ObjectStep(this, ObjectID.ENTRY_48722, new WorldPoint(1191, 3411, 0), "Hide away in the Entry in the north west of the room.");
		defendKasondeSidebar = new DetailedQuestStep(this, "Defend Kasonde! There are barricades in the stone chests you can set up to block routes. " +
			"There are also satchels you can place on the floor, and detonate using the Detonator. This will kill all of the Strangled in a 7x7 area, as well as damaging you or " +
			"Kasonde if either of you are in the blast radius.");
		defendKasondeSidebar.addText("Closed chests require you to guess the correct code to open, with correct numbers in the correct place being marked in green, " +
			"and correct numbers in the wrong places being marked with blue.");
		defendKasondeSidebar.addText("It's recommended to also use freezes if you have ancient magicks with you to keep them off of Kasonde. If you have freezes, you can largely ignore the barricading and explosives.");

		defendKasonde = new DetailedQuestStep(this, "Defend Kasonde! Read the sidebar for more details.");
		defendKasonde.addRecommended(freezes);
		defendKasondeSidebar.addSubSteps(defendKasonde);

		// TODO: Get actual coordinate and ladder ID!
		leaveTowerDefenseRoom = new ObjectStep(this, ObjectID.LADDER, new WorldPoint(1175, 9755, 0),
			"Leave the dungeon up the ladder.");
		talkToKasondeAfterTowerDefense = new NpcStep(this, NpcID.KASONDE, new WorldPoint(1191, 3404, 0),
			"Talk to Kasonde on the surface.");
		getHerb = new ObjectStep(this, ObjectID.KORBAL_HERBS, new WorldPoint(1155, 3447, 0),
			"Go get the herb from the north west corner of the area. " +
				"The stangled will attack you, so bring food and freezes to trap them. More info in the sidebar.");
		getHerb.setLinePoints(Arrays.asList(
			new WorldPoint(1193, 3403, 0),
			new WorldPoint(1193, 3395, 0),
			new WorldPoint(1186, 3395, 0),
			new WorldPoint(1186, 3416, 0),
			new WorldPoint(1165, 3415, 0),
			new WorldPoint(1161, 3415, 0),
			new WorldPoint(1161, 3426, 0),
			new WorldPoint(1159, 3426, 0),
			new WorldPoint(1159, 3428, 0),
			new WorldPoint(1161, 3441, 0)
		));

		getHerbSidebar = new ObjectStep(this, ObjectID.KORBAL_HERBS, new WorldPoint(1111, 3434, 0),
			"Go get the herb. The Strangled will attack you, so have food. If your infected bar reaches full, " +
				"you'll be teleported to the starting room again. ");
		getHerbSidebar.addText("You can get some stink bombs from the chest in the south east corner of Kasonde's room, " +
				"which when used attract the Strangled to the location. This can be useful for avoiding them.");
		getHerbSidebar.addText("Freezes and blood spells are useful for trapping them and healing up.");
		getHerbSidebar.addSubSteps(getHerb);

		getBerry = new ObjectStep(this, ObjectID.ARGIAN_BUSH, new WorldPoint(1126, 3323, 0),
			"Get the berry from the south west part of the area. Beware the Strangled still.");
		getBerry.setLinePoints(Arrays.asList(
			new WorldPoint(1161, 3441, 0),
			new WorldPoint(1159, 3428, 0),
			new WorldPoint(1159, 3426, 0),
			new WorldPoint(1161, 3426, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1144, 3342, 0),
			new WorldPoint(1126, 3323, 0)
		));

		goDownToKasonde = new ObjectStep(this, ObjectID.ENTRY_48723, new WorldPoint(1174, 3428, 0),
			"Go to Kasonde, who is inside the main pyramid of the area to the north. Be ready to fight a few Strangled.", combatGear, berry, herb);
		goDownToKasonde.setLinePoints(Arrays.asList(
			new WorldPoint(1126, 3323, 0),
			new WorldPoint(1144, 3342, 0),
			new WorldPoint(1144, 3357, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1174, 3416, 0),
			new WorldPoint(1174, 3427, 0)
		));
		defendKasondeHerb = new NpcStep(this, NpcID.STRANGLED_12280, new WorldPoint(1183, 9824, 0),
			"Defeat the Strangled attacking Kasonde.", true);
		((NpcStep) defendKasondeHerb).addAlternateNpcs(NpcID.STRANGLED_12279);

		talkToKasondeWithHerbAndBerry = new NpcStep(this, NpcID.KASONDE_12258, new WorldPoint(1183, 9824, 0),
			"Give Kasonde the berry and herb.", berry, herb);
		addHerb = new DetailedQuestStep(this, "Add the herb to unfinished serum", herb.highlighted(), unfinishedSerum.highlighted());
		addBerry = new DetailedQuestStep(this, "Add the berries to the serum.", serumWithHerb.highlighted(), berry.highlighted());
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, allBursts);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(waterSource, senntistenTeleport);
	}


	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Ancient Guardian (level 153)");
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
			new ItemReward("Ancient Lamp (100k exp in a combat skill)", ItemID.ANCIENT_LAMP)
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
			Arrays.asList(ancientMagicksActive),
			Arrays.asList(nardahTeleport, waterSource, senntistenTeleport)));

		allSteps.add(new PanelDetails("Learning of the Ancients",
			Arrays.asList(talkToBalando, operateWinch, talkToBanikan, getPickaxe, mineRocks,
				enterDigsiteHole, killAncientGuardian, talkToBanikanInGolemRoom, inspectGolem,
				inspectAltar, castOnBloodStatue, castOnShadowStatue, castOnSmokeStatue, castOnIceStatue,
				searchCrateForCharges, imbueAtAltar, chargeGolem, solveGolemPuzzle, operateGolem, talkToBanikanAfterGolem,
				operateGolemFrostenhorn),
			Arrays.asList(combatGear, allBursts),
			Arrays.asList(senntistenTeleport)));
		allSteps.add(new PanelDetails("Vardorvis",
			Arrays.asList(talkToElissa, talkToBarus, searchDesk, readPotionNote, drinkPotion, boardBoat,
				runIntoStanglewood, talkToKasonde, enterEntry, defendKasondeSidebar, leaveTowerDefenseRoom,
				talkToKasondeAfterTowerDefense, getHerbSidebar, getBerry, goDownToKasonde, defendKasondeHerb,
				talkToKasondeWithHerbAndBerry),
			Arrays.asList(combatGear),
			Arrays.asList(xericTalisman, freezes)));

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
