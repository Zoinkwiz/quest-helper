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

import com.questhelper.Icon;
import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperPlugin;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import static com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan.showBlueHitsplatUntilTick;
import static com.questhelper.helpers.quests.deserttreasureii.FakeLeviathan.showRedHitsplatUntilTick;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.ItemSlots;
import static com.questhelper.requirements.util.LogicHelper.and;
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
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

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
		shadowBurstRunes, smokeBurstRunes, allBursts, uncharedCells, chargedCells, xericTalisman,
		facemask, staminaPotions, eyeTeleport, rangedCombatGear, food, prayerPotions, nardahTeleport,
		arclight, freezes;

	Zone vault, digsiteHole, golemRoom;
	Requirement inVault, inDigsiteHole, inGolemRoom;

	Requirement inspectedStatueSE, inspectedStatueSW, inspectedStatueNE, inspectedStatueNW, inspectedPlaque,
	inspectedGolem, inspectedAltar, bloodBeenCast, iceBeenCast, shadowBeenCast, smokeBeenCast, castAllSpells, inPuzzle;

	Requirement searchedVardorvis, searchedPerseriya, searchedSucellus, searchedWhisperer, askedAboutVardorvis,
		askedAboutPerseriya, askedAboutSucellus, askedAboutWhisperer;

	Requirement finishedVardorvis, finishedPerseriya;

	VardorvisSteps vardorvisSteps;

	PerseriyaSteps perseriyaSteps;

	DetailedQuestStep moreComingSoon;

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

		ConditionalStep findingTheFour = new ConditionalStep(this, vardorvisSteps);
		findingTheFour.addStep(and(finishedVardorvis, finishedPerseriya), moreComingSoon);
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

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		waterSource = new ItemRequirement("Full waterskin, or any protection from the desert", ItemID.CIRCLET_OF_WATER);
		waterSource.addAlternates(ItemID.DESERT_AMULET_4, ItemID.WATERSKIN4, ItemID.WATERSKIN3, ItemID.WATERSKIN2, ItemID.WATERSKIN1);
		ancientMagicksActive = new SpellbookRequirement(Spellbook.ANCIENT);

		// TODO: Add check where IF you have nardah teleport in scroll box, show scroll box as requirement
		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(5672, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_4);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_3, ItemID.NARDAH_TELEPORT, ItemID.DESERT_AMULET_2);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		ItemRequirement digsitePendant = new ItemRequirement("Digsite pendant", ItemID.DIGSITE_PENDANT_5);
		digsitePendant.addAlternates(ItemID.DIGSITE_PENDANT_4, ItemID.DIGSITE_PENDANT_3, ItemID.DIGSITE_PENDANT_2,
			ItemID.DIGSITE_PENDANT_1);

		// TODO: Make a spell SpellRequirement, which will change the highlight to be for the spellbook widget not runes
		// Check if have requirement + runes for spell
		senntistenTeleport = new ItemRequirements(LogicType.OR, "Senntisten teleport",
			new ItemRequirement("Senntisten teleport", ItemID.SENNTISTEN_TELEPORT),
			new ItemRequirements(
				new ItemRequirement("Law rune", ItemID.LAW_RUNE, 2),
				new ItemRequirement("Soul rune", ItemID.SOUL_RUNE)
			),
			digsitePendant
		);

		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		bloodBurstRunes = new ItemRequirements("Blood burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2));
		iceBurstRunes = new ItemRequirements("Ice burst runes",
		new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
		new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
		new ItemRequirement("Water runes", ItemID.WATER_RUNE, 4));
		shadowBurstRunes = new ItemRequirements("Shadow burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 1));
		smokeBurstRunes = new ItemRequirements("Smoke burst runes",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 2),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 4),
			new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 2));
		allBursts = new ItemRequirements("Runes for shadow, smoke, blood, and ice burst",
			new ItemRequirement("Death runes", ItemID.DEATH_RUNE, 8),
			new ItemRequirement("Chaos runes", ItemID.CHAOS_RUNE, 16),
			new ItemRequirement("Blood runes", ItemID.BLOOD_RUNE, 2),
			new ItemRequirement("Water runes", ItemID.WATER_RUNE, 4),
			new ItemRequirement("Soul runes", ItemID.SOUL_RUNE, 2),
			new ItemRequirement("Air runes", ItemID.AIR_RUNE, 3),
			new ItemRequirement("Fire runes", ItemID.FIRE_RUNE, 2));

		freezes = new ItemRequirement("Freezing spells STRONGLY recommended + reasonable mage accuracy", -1);

		rangedCombatGear = new ItemRequirement("Ranged combat gear", -1);
		rangedCombatGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD);
		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		xericTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERICS_TALISMAN);
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS, 20);
		facemask = new ItemRequirement("Facemask", ItemCollections.SLAYER_HELMETS);
		facemask.addAlternates(ItemID.FACEMASK, ItemID.GAS_MASK);

		eyeTeleport = new ItemRequirement("Teleport to Temple of the Eye via minigame teleport or Amulet of the Eye", ItemID.AMULET_OF_THE_EYE_26990);
		eyeTeleport.addAlternates(ItemID.AMULET_OF_THE_EYE, ItemID.AMULET_OF_THE_EYE_26992, ItemID.AMULET_OF_THE_EYE_26994);

		arclight = new ItemRequirement("Arclight", ItemID.ARCLIGHT);

		/* Quest Items */
		uncharedCells = new ItemRequirement("Uncharged cells", ItemID.UNCHARGED_CELL_28402);
		chargedCells = new ItemRequirement("Charged cells", ItemID.CHARGED_CELL);
	}

	public void loadZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		digsiteHole = new Zone(new WorldPoint(3400, 9800, 0), new WorldPoint(3419, 9824, 0));
		golemRoom = new Zone(new WorldPoint(2767, 6425, 0), new WorldPoint(2807, 6459, 0));
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

		// Probably 15132 0->1 for placed medallion works too
		finishedVardorvis = new VarbitRequirement(15125, 56, Operation.GREATER_EQUAL);
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

		finishedPerseriya = new VarbitRequirement(15128, 50, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		attemptToEnterVaultDoor = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north-east of Nardah.", ancientMagicksActive);
		attemptToEnterVaultDoor.addTeleport(nardahTeleport);
		attemptToEnterVaultDoor.addDialogStep("Yes.");

		attemptToEnterVaultDoor2 = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0),
			"Attempt to enter the Vault door north-east of Nardah.");
		attemptToEnterVaultDoor.addSubSteps(attemptToEnterVaultDoor2);

		talkToAsgarnia = new NpcStep(this, NpcID.ASGARNIA_SMITH_12291,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		inspectPlaque = new ObjectStep(this, ObjectID.PLAQUE_48798,
			new WorldPoint(3944, 9631, 1), "Inspect the plaque.");

		inspectStatueNE = new ObjectStep(this, NullObjectID.NULL_49499, new WorldPoint(3942, 9636, 1),
			"Inspect the north-east statue.");
		inspectStatueNW = new ObjectStep(this, NullObjectID.NULL_49501, new WorldPoint(3932, 9636, 1),
			"Inspect the north-west statue.");
		inspectStatueSW = new ObjectStep(this, NullObjectID.NULL_49503, new WorldPoint(3932, 9626, 1),
			"Inspect the south-west statue.");
		inspectStatueSE = new ObjectStep(this, NullObjectID.NULL_49505, new WorldPoint(3942, 9626, 1),
			"Inspect the south-east statue.");

		talkToAsgarniaAgain = new NpcStep(this, NpcID.ASGARNIA_SMITH_12291,
			new WorldPoint(3932, 9631, 1), "Talk to Asgarnia Smith inside the Vault.");

		talkToBalando = new NpcStep(this, NpcID.TERRY_BALANDO, new WorldPoint(3359, 3334, 0),
			"Talk to the Terry Balando in the Exam Centre found south-east of Varrock, " +
				"directly south of the Digsite.");
		talkToBalando.addTeleport(senntistenTeleport);

		operateWinch = new ObjectStep(this, ObjectID.WINCH_48918, new WorldPoint(3384, 3418, 0),
			"Enter the winch on the east side of the Digsite.", combatGear);
		talkToBanikan = new NpcStep(this, NpcID.DR_BANIKAN, new WorldPoint(3409, 9815, 0),
			"Talk to Dr Banikan.");

		getPickaxe = new ObjectStep(this, ObjectID.CRATE_48923, new WorldPoint(3414, 9819, 0),
			"Get a pickaxe from the crate in the north-east of the cavern.");

		mineRocks = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Mine the rocks in the south-east of the cavern.", pickaxe);

		enterDigsiteHole = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south-east of the cavern, ready for a fight.", combatGear);

		enterDigsiteHoleAgain = new ObjectStep(this, NullObjectID.NULL_49510, new WorldPoint(3411, 9811, 0),
			"Enter the hole in the south-east of the cavern.");

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
			"Inspect the altar to the north-west.");

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


		/* Vardorvis */
		NpcStep talkToElissa = new NpcStep(this, NpcID.ELISSA, new WorldPoint(3378, 3428, 0), "Talk to Elissa in the north-east of the Digsite on the surface.");
		talkToElissa.addDialogStep("I hear you visited Lovakengj recently.");
		talkToElissa.addTeleport(senntistenTeleport);

		vardorvisSteps = new VardorvisSteps(this, talkToElissa, questBank);

		perseriyaSteps = new PerseriyaSteps(this, new DetailedQuestStep(this, "WOOPS"), runeliteObjectManager);

		moreComingSoon = new DetailedQuestStep(this, "The rest of the Quest Helper will come out once it's ready.");
	}

	final BufferedImage missIcon = Icon.BLUE_HITSPLAT.getImage();
	final BufferedImage hitIcon = Icon.MAX_HITSPLAT.getImage();

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		Point imageLocation = client.getLocalPlayer().getCanvasImageLocation(missIcon, client.getLocalPlayer().getLogicalHeight() / 2);
		if (imageLocation != null)
		{
			if (showBlueHitsplatUntilTick >= client.getTickCount())
			{
				OverlayUtil.renderImageLocation(graphics, imageLocation, missIcon);
			}
			if (showRedHitsplatUntilTick >= client.getTickCount())
			{
				OverlayUtil.renderImageLocation(graphics, imageLocation, hitIcon);
			}
		}
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, allBursts, facemask);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(nardahTeleport, waterSource, senntistenTeleport, staminaPotions, freezes, xericTalisman, eyeTeleport);
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
			new ItemReward("Ancient Lamp (100k exp in a combat skill)", ItemID.ANCIENT_LAMP, 3)
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
			vardorvisSteps.getDisplaySteps(),
			Arrays.asList(combatGear),
			Arrays.asList(xericTalisman, freezes)));
		allSteps.add(new PanelDetails("Perseriya",
			perseriyaSteps.getStartSteps(),
			Arrays.asList(combatGear, facemask, arclight),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - Room 1",
			perseriyaSteps.getRoom1Steps(),
			Arrays.asList(facemask, arclight),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - Room 2",
			perseriyaSteps.getRoom2Steps(),
			Arrays.asList(facemask, arclight),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - Room 3",
			perseriyaSteps.getRoom3Steps(),
			Arrays.asList(facemask, arclight),
			Arrays.asList(eyeTeleport, staminaPotions)));
		allSteps.add(new PanelDetails("Perseriya - The battle",
			perseriyaSteps.getBattleSteps(),
			Arrays.asList(rangedCombatGear, shadowBurstRunes),
			Arrays.asList(eyeTeleport, staminaPotions, food, prayerPotions)));
		allSteps.add(new PanelDetails("More to come...", Arrays.asList(moreComingSoon)));
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
