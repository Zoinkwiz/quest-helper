/*
 * Copyright (c) 2021, Lesteenman <https://github.com/lesteenman>
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
package com.questhelper.quests.wanted;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcHintArrowRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;

import java.util.*;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.WANTED
)
public class Wanted extends BasicQuestHelper
{
	private static final String TEXT_ASK_ABOUT_WANTED_QUEST = "Ask about the Wanted! Quest";

	ItemRequirement commorbComponents, tenThousandGp, commorbComponentsOrTenThousandGp, lightSource, spinyHelmet, rope,
		combatGear, commorb, highlightedCommorb, runeEssence, pureEssence, essence, amuletOfGlory, ringOfDueling, faladorTeleport,
		varrockTeleport, canifisTeleport;

	Zone whiteKnightsCastleF1, whiteKnightsCastleF2, taverleyDungeonP1, taverleyDungeonP2, blackKnightsBase, nearCanifis,
		canifis, championsGuild, essenceMine, dorgeshKaan, lumbridgeCellar, musaPoint, draynorMarket, goblinVillage,
		ardougneMarket, grandTree, scorpiusShrine, nearAliMorrisane, wizardsTower, brimhavenPub, castleWars, rellekka,
		mcGruborsWood1, mcGruborsWood2, slayerTower, yanillePub, lumbridgeCaves, endOfLumbridgeSwampCaves, chasmOfTears;

	Requirement isInWhiteKnightsCastleF1, isInWhiteKnightsCastleF2, becameSquire, isInTaverleyDungeon,
		isInCommorbWithGpConversation, isInCommorbWithComponentsConversation, isInBlackKnightsBase,
		hasKilledBlackKnight, startedEnterTheAbyss, talkedToSavantNearCanifis, isNearCanifis, isInCanifis,
		gotAssignmentDetailsFromSavant, talkedToLordDaquarius, investigatedLordDaquarius, talkedToMageOfZamorak,
		mustChaseToChampionsGuild, isInChampionsGuild, mustChaseToDorgeshKaan, isAtAliMorrisane, isAtCastleWars,
		isAtEndOfLumbridgeCaves, isAtScorpiusShrine, isInArdougneMarket, isInBrimhavenPub, isInChasmOfTears,
		isInDorgeshKaan, isInDraynorMarket, isInEssenceMine, isInGoblinVillage, isInGrandTree, isInLumbridgeCaves,
		isInLumbridgeCellarOrTunnels, isInMcGruborsWood, isInSlayerTower, isInYanillePub, isInMusaPoint, isInRellekka,
		isInWizardsTower, mustChaseToAliMorrisane, mustChaseToArdougneMarket, mustChaseToBrimhavenPub,
		mustChaseToCastleWars, mustChaseToDraynorMarket, mustChaseToEssenceMine, mustChaseToGoblinVillage,
		mustChaseToGrandTree, mustChaseToLumbridgeSwamp, mustChaseToMcGruborsWood, mustChaseToMusaPoint,
		mustChaseToRellekka, mustChaseToScorpiusShrine, mustChaseToSlayerTower, mustChaseToWizardsTower,
		mustChaseToYanillePub, solusHat, oneFreeInventorySlot, placedRope, blackKnightNearby;

	QuestStep talkToSirTiffy1, climbToWhiteKnightsCastleF1, climbToWhiteKnightsCastleF2,
		talkToSirTiffyAfterBecomingSquire, talkToSirAmik1, talkToSirTiffy2, talkToSirAmik2,
		talkToSirTiffy3, getCommorbFromSirTiffy, getCommorbWithComponents, getCommorbWithGp,
		doNotGetCommorbWithGp, talkToSavantAfterGettingCommorb, enterTaverleyDungeon, talkToLordDaquarius,
		killBlackKnight, talkToIntimidatedLordDaquarius, goToBlackKnightsBase, talkToMageOfZamorakInWilderness,
		talkToMageOfZamorak, giveEssenceToMageOfZamorak, scanWithCommorb, moveOutOfCanifisAgain, chaseToCanifis,
		enterCanifis, huntDownSolus, goToChampionsGuild, goToDorgeshKaan, goToEssenceMine, goToMusaPoint,
		goToDraynorMarket, goToGoblinVillage, goToArdougneMarket, goToGrandTree, goToScorpiusShrine, goToAliMorrisane,
		goToWizardsTower, goToBrimhavenPub, goToCastleWars, goToRellekka, goToMcGruborsWood, goToSlayerTower,
		goToYanillePub, enterLumbridgeSwampCavesFromTears, goToEndOfLumbridgeSwampCaves, goDownToLumbridgeSwampCaves,
		goDownToLumbridgeSwampCavesPlacedRope, killSolus, goTalkToSirAmikAfterFinalBattle, goDownToLumbridgeCellar, killBlackKnightFromScan;

	ConditionalStep goTalkToSirAmik1, goTalkToSirAmik2;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupOtherRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		// Getting started
		steps.put(0, talkToSirTiffy1);
		steps.put(1, talkToSirTiffy1);
		steps.put(2, talkToSirTiffy1);

		ConditionalStep goToSirAmik = new ConditionalStep(this, climbToWhiteKnightsCastleF1);
		goToSirAmik.addStep(isInWhiteKnightsCastleF1, climbToWhiteKnightsCastleF2);

		goTalkToSirAmik1 = new ConditionalStep(this, goToSirAmik, "Go tell Sir Amik Varze in the White Knights' " +
			"Castle you wish to join the White Knights, BUT refuse to be a Squire.");
		goTalkToSirAmik1.addStep(isInWhiteKnightsCastleF2, talkToSirAmik1);

		ConditionalStep goDoAmikP1 = new ConditionalStep(this, goTalkToSirAmik1);
		goDoAmikP1.addStep(becameSquire, talkToSirTiffyAfterBecomingSquire);
		steps.put(3, goDoAmikP1);

		steps.put(4, talkToSirTiffy2);

		goTalkToSirAmik2 = new ConditionalStep(this, goToSirAmik, "Go back to Sir Amik Varze in the White Knights' Castle.");
		goTalkToSirAmik2.addStep(isInWhiteKnightsCastleF2, talkToSirAmik2);
		steps.put(5, goTalkToSirAmik2);

		steps.put(6, talkToSirTiffy3);

		ConditionalStep goGetCommorb = new ConditionalStep(this, getCommorbFromSirTiffy, commorbComponentsOrTenThousandGp);
		goGetCommorb.addStep(new Conditions(tenThousandGp, commorbComponents, isInCommorbWithGpConversation), doNotGetCommorbWithGp);
		goGetCommorb.addStep(new Conditions(commorbComponents, isInCommorbWithComponentsConversation), getCommorbWithComponents);
		goGetCommorb.addStep(new Conditions(tenThousandGp, isInCommorbWithGpConversation), getCommorbWithGp);
		steps.put(7, goGetCommorb);

		// Daquarius and Mage of Zamorak
		ConditionalStep investigation = new ConditionalStep(this, talkToSavantAfterGettingCommorb);
		investigation.addStep(talkedToMageOfZamorak, giveEssenceToMageOfZamorak);
		investigation.addStep(new Conditions(startedEnterTheAbyss, investigatedLordDaquarius), talkToMageOfZamorak);
		investigation.addStep(investigatedLordDaquarius, talkToMageOfZamorakInWilderness);
		investigation.addStep(new Conditions(hasKilledBlackKnight, isInTaverleyDungeon), talkToIntimidatedLordDaquarius);
		investigation.addStep(hasKilledBlackKnight, enterTaverleyDungeon);
		investigation.addStep(new Conditions(talkedToLordDaquarius, isInBlackKnightsBase), killBlackKnight);
		investigation.addStep(talkedToLordDaquarius, goToBlackKnightsBase);
		investigation.addStep(new Conditions(gotAssignmentDetailsFromSavant, isInTaverleyDungeon), talkToLordDaquarius);
		investigation.addStep(gotAssignmentDetailsFromSavant, enterTaverleyDungeon);
		steps.put(8, investigation);

		// Hunt for Solus
		ConditionalStep goScanInCanifis = new ConditionalStep(this, chaseToCanifis);
		goScanInCanifis.addStep(new Conditions(isInCanifis, talkedToSavantNearCanifis), scanWithCommorb);
		goScanInCanifis.addStep(new Conditions(isNearCanifis, talkedToSavantNearCanifis), enterCanifis);
		goScanInCanifis.addStep(isNearCanifis, moveOutOfCanifisAgain);

		ConditionalStep goScanInChampionsGuild = new ConditionalStep(this, goToChampionsGuild);
		goScanInChampionsGuild.addStep(isInChampionsGuild, scanWithCommorb);

		ConditionalStep goScanInDorgeshKaan = new ConditionalStep(this, goDownToLumbridgeCellar, lightSource);
		goScanInDorgeshKaan.addStep(isInDorgeshKaan, scanWithCommorb);
		goScanInDorgeshKaan.addStep(isInLumbridgeCellarOrTunnels, goToDorgeshKaan);

		ConditionalStep goKillSolusInEssenceMine = new ConditionalStep(this, goToEssenceMine, combatGear);
		goKillSolusInEssenceMine.addStep(isInEssenceMine, killSolus);

		ConditionalStep goScanInMusaPoint = new ConditionalStep(this, goToMusaPoint);
		goScanInMusaPoint.addStep(isInMusaPoint, scanWithCommorb);

		ConditionalStep goScanInDraynorMarket = new ConditionalStep(this, goToDraynorMarket);
		goScanInDraynorMarket.addStep(isInDraynorMarket, scanWithCommorb);

		ConditionalStep goScanInGoblinVillage = new ConditionalStep(this, goToGoblinVillage);
		goScanInGoblinVillage.addStep(isInGoblinVillage, scanWithCommorb);

		ConditionalStep goScanInArdougneMarket = new ConditionalStep(this, goToArdougneMarket);
		goScanInArdougneMarket.addStep(isInArdougneMarket, scanWithCommorb);

		ConditionalStep goScanInGrandTree = new ConditionalStep(this, goToGrandTree);
		goScanInGrandTree.addStep(isInGrandTree, scanWithCommorb);

		ConditionalStep goScanAtScorpiusShrine = new ConditionalStep(this, goToScorpiusShrine);
		goScanAtScorpiusShrine.addStep(isAtScorpiusShrine, scanWithCommorb);

		ConditionalStep goScanAtAliMorrisane = new ConditionalStep(this, goToAliMorrisane);
		goScanAtAliMorrisane.addStep(isAtAliMorrisane, scanWithCommorb);

		ConditionalStep goScanInWizardsTower = new ConditionalStep(this, goToWizardsTower);
		goScanInWizardsTower.addStep(isInWizardsTower, scanWithCommorb);

		ConditionalStep goScanInBrimhavenPub = new ConditionalStep(this, goToBrimhavenPub);
		goScanInBrimhavenPub.addStep(isInBrimhavenPub, scanWithCommorb);

		ConditionalStep goScanAtCastleWars = new ConditionalStep(this, goToCastleWars);
		goScanAtCastleWars.addStep(isAtCastleWars, scanWithCommorb);

		ConditionalStep goScanInRellekka = new ConditionalStep(this, goToRellekka);
		goScanInRellekka.addStep(isInRellekka, scanWithCommorb);

		ConditionalStep goScanInMcGruborsWood = new ConditionalStep(this, goToMcGruborsWood);
		goScanInMcGruborsWood.addStep(isInMcGruborsWood, scanWithCommorb);

		ConditionalStep goScanInSlayerTower = new ConditionalStep(this, goToSlayerTower);
		goScanInSlayerTower.addStep(isInSlayerTower, scanWithCommorb);

		ConditionalStep goScanInYanillePub = new ConditionalStep(this, goToYanillePub);
		goScanInYanillePub.addStep(isInYanillePub, scanWithCommorb);

		ConditionalStep goScanInLumbridgeSwampCaves = new ConditionalStep(this, goDownToLumbridgeSwampCaves);
		goScanInLumbridgeSwampCaves.addStep(isAtEndOfLumbridgeCaves, scanWithCommorb);
		goScanInLumbridgeSwampCaves.addStep(isInChasmOfTears, enterLumbridgeSwampCavesFromTears);
		goScanInLumbridgeSwampCaves.addStep(isInLumbridgeCaves, goToEndOfLumbridgeSwampCaves);
		goScanInLumbridgeSwampCaves.addStep(placedRope, goDownToLumbridgeSwampCavesPlacedRope);

		ConditionalStep goHuntForSolus = new ConditionalStep(this, goScanInCanifis);
		goHuntForSolus.addStep(blackKnightNearby, killBlackKnightFromScan);
		goHuntForSolus.addStep(mustChaseToEssenceMine, goKillSolusInEssenceMine);
		goHuntForSolus.addStep(mustChaseToChampionsGuild, goScanInChampionsGuild);
		goHuntForSolus.addStep(mustChaseToDorgeshKaan, goScanInDorgeshKaan);
		goHuntForSolus.addStep(mustChaseToMusaPoint, goScanInMusaPoint);
		goHuntForSolus.addStep(mustChaseToDraynorMarket, goScanInDraynorMarket);
		goHuntForSolus.addStep(mustChaseToGoblinVillage, goScanInGoblinVillage);
		goHuntForSolus.addStep(mustChaseToArdougneMarket, goScanInArdougneMarket);
		goHuntForSolus.addStep(mustChaseToGrandTree, goScanInGrandTree);
		goHuntForSolus.addStep(mustChaseToScorpiusShrine, goScanAtScorpiusShrine);
		goHuntForSolus.addStep(mustChaseToAliMorrisane, goScanAtAliMorrisane);
		goHuntForSolus.addStep(mustChaseToWizardsTower, goScanInWizardsTower);
		goHuntForSolus.addStep(mustChaseToBrimhavenPub, goScanInBrimhavenPub);
		goHuntForSolus.addStep(mustChaseToCastleWars, goScanAtCastleWars);
		goHuntForSolus.addStep(mustChaseToRellekka, goScanInRellekka);
		goHuntForSolus.addStep(mustChaseToMcGruborsWood, goScanInMcGruborsWood);
		goHuntForSolus.addStep(mustChaseToSlayerTower, goScanInSlayerTower);
		goHuntForSolus.addStep(mustChaseToYanillePub, goScanInYanillePub);
		goHuntForSolus.addStep(mustChaseToLumbridgeSwamp, goScanInLumbridgeSwampCaves);
		steps.put(9, goHuntForSolus);

		steps.put(10, goTalkToSirAmikAfterFinalBattle);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		ItemRequirement lawRune = new ItemRequirement("A law rune", ItemID.LAW_RUNE, 1);
		ItemRequirement enchantedGem = new ItemRequirement("Enchanted gem", ItemID.ENCHANTED_GEM, 1);
		ItemRequirement moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS, 1);
		commorbComponents = new ItemRequirements("A law rune, an enchanted gem and some molten glass", lawRune, enchantedGem, moltenGlass);
		commorbComponents.setTooltip("Alternatively, you can bring 10k gp.");
		tenThousandGp = new ItemRequirement("10k gp", ItemCollections.COINS, 10000);
		commorbComponentsOrTenThousandGp = new ItemRequirements(LogicType.OR, "A law rune, an enchanted gem and some molten glass OR 10k gp", commorbComponents, tenThousandGp);
		
		runeEssence = new ItemRequirement("20 Rune Essence (UNNOTED)", ItemID.RUNE_ESSENCE, 20);
		pureEssence = new ItemRequirement("20 Pure Essence (UNNOTED)", ItemID.PURE_ESSENCE, 20);
		essence =  new ItemRequirements(LogicType.OR, "20 Rune or Pure Essence (UNNOTED)", runeEssence, pureEssence);
		lightSource = new ItemRequirement("A light source", ItemCollections.LIGHT_SOURCES);
		rope = new ItemRequirement("A rope", ItemID.ROPE);

		spinyHelmet = new ItemRequirement("A spiny helmet or slayer helm", ItemID.SPINY_HELMET);
		spinyHelmet.addAlternates(ItemCollections.SLAYER_HELMETS);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		amuletOfGlory = new ItemRequirement("Amulet of Glory", ItemCollections.AMULET_OF_GLORIES);
		ringOfDueling = new ItemRequirement("Ring of Dueling", ItemCollections.RING_OF_DUELINGS);
		faladorTeleport = new ItemRequirement("A teleport to Falador", ItemID.FALADOR_TELEPORT, -1);
		varrockTeleport = new ItemRequirement("A teleport to Varrock", ItemID.VARROCK_TELEPORT, -1);
		canifisTeleport = new ItemRequirement("A teleport to Canifis", ItemID.FENKENSTRAINS_CASTLE_TELEPORT, -1);

		solusHat = new ItemRequirement("Solus' hat", ItemID.SOLUSS_HAT);
		commorb = new ItemRequirement("Commorb", ItemID.COMMORB);
		highlightedCommorb = commorb.copy();
		highlightedCommorb.setHighlightInInventory(true);
	}

	public void setupZones()
	{
		taverleyDungeonP1 = new Zone(new WorldPoint(2814, 9854, 0), new WorldPoint(2944, 9670, 0));
		taverleyDungeonP2 = new Zone(new WorldPoint(2944, 9803, 0), new WorldPoint(2971, 9769, 0));
		whiteKnightsCastleF1 = new Zone(new WorldPoint(2954, 3353, 1), new WorldPoint(2998, 3327, 1));
		whiteKnightsCastleF2 = new Zone(new WorldPoint(2954, 3353, 2), new WorldPoint(2998, 3327, 2));
		blackKnightsBase = new Zone(new WorldPoint(2889, 9713, 0), new WorldPoint(2934, 9671, 0));
		nearCanifis = new Zone(new WorldPoint(3457, 3521, 0), new WorldPoint(3520, 3464, 0));
		canifis = new Zone(new WorldPoint(3477, 3485, 0), new WorldPoint(3489, 3471, 0));
		championsGuild = new Zone(new WorldPoint(3188, 3362, 0), new WorldPoint(3194, 3352, 0));
		dorgeshKaan = new Zone(new WorldPoint(3310, 9656, 0), new WorldPoint(3327, 9600, 0));
		lumbridgeCellar = new Zone(new WorldPoint(3207, 9651, 0), new WorldPoint(3327, 9601, 0));
		musaPoint = new Zone(new WorldPoint(2913, 1366, 0), new WorldPoint(2919, 3158, 0));
		draynorMarket = new Zone(new WorldPoint(3077, 3254, 0), new WorldPoint(3085, 3246, 0));
		goblinVillage = new Zone(new WorldPoint(2947, 3516, 0), new WorldPoint(2967, 3499, 0));
		ardougneMarket = new Zone(new WorldPoint(2655, 3313, 0), new WorldPoint(2668, 3301, 0));
		grandTree = new Zone(new WorldPoint(2463, 3499, 0), new WorldPoint(2469, 3493, 0));
		scorpiusShrine = new Zone(new WorldPoint(2461, 3231, 0), new WorldPoint(2470, 3225, 0));
		nearAliMorrisane = new Zone(new WorldPoint(3300, 3216, 0), new WorldPoint(3306, 3209, 0));
		wizardsTower = new Zone(new WorldPoint(3104, 3166, 0), new WorldPoint(3114, 3155, 0));
		brimhavenPub = new Zone(new WorldPoint(2791, 3170, 0), new WorldPoint(2800, 3154, 0));
		castleWars = new Zone(new WorldPoint(2435, 3099, 0), new WorldPoint(2447, 3081, 0));
		rellekka = new Zone(new WorldPoint(2654, 3665, 0), new WorldPoint(2664, 3650, 0));
		mcGruborsWood1 = new Zone(new WorldPoint(2624, 3502, 0), new WorldPoint(2654, 3470, 0));
		mcGruborsWood2 = new Zone(new WorldPoint(2662, 3504, 0), new WorldPoint(2677, 3484, 0));
		slayerTower = new Zone(new WorldPoint(3405, 3580, 0), new WorldPoint(3453, 3534, 0));
		yanillePub = new Zone(new WorldPoint(2548, 3082, 0), new WorldPoint(2557, 3077, 0));
		lumbridgeCaves = new Zone(new WorldPoint(3144, 9602, 0), new WorldPoint(3260, 9538, 0));
		chasmOfTears = new Zone(new WorldPoint(3208, 9533, 2), new WorldPoint(3261, 9486, 2));
		endOfLumbridgeSwampCaves = new Zone(new WorldPoint(3216, 9555, 0), new WorldPoint(3239, 9540, 0));
		essenceMine = new Zone(new WorldPoint(2880, 4806, 0), new WorldPoint(2938, 4861, 0));
	}

	public void setupOtherRequirements()
	{
		oneFreeInventorySlot = new FreeInventorySlotRequirement(InventoryID.INVENTORY, 1);

		startedEnterTheAbyss = new VarplayerRequirement(492, 1, Operation.GREATER_EQUAL);

		isInCommorbWithGpConversation = new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "You have that kind of money with you?");
		isInCommorbWithComponentsConversation = new WidgetTextRequirement(WidgetInfo.DIALOG_NPC_TEXT, "You wish me to make you one now then, what?");

		isInTaverleyDungeon = new ZoneRequirement(taverleyDungeonP1, taverleyDungeonP2);
		isInWhiteKnightsCastleF1 = new ZoneRequirement(whiteKnightsCastleF1);
		isInWhiteKnightsCastleF2 = new ZoneRequirement(whiteKnightsCastleF2);
		isInBlackKnightsBase = new ZoneRequirement(blackKnightsBase);
		isNearCanifis = new ZoneRequirement(nearCanifis);
		isInCanifis = new ZoneRequirement(canifis);
		isInChampionsGuild = new ZoneRequirement(championsGuild);
		isAtAliMorrisane = new ZoneRequirement(nearAliMorrisane);
		isAtCastleWars = new ZoneRequirement(castleWars);
		isAtEndOfLumbridgeCaves = new ZoneRequirement(endOfLumbridgeSwampCaves);
		isAtScorpiusShrine = new ZoneRequirement(scorpiusShrine);
		isInArdougneMarket = new ZoneRequirement(ardougneMarket);
		isInBrimhavenPub = new ZoneRequirement(brimhavenPub);
		isInDorgeshKaan = new ZoneRequirement(dorgeshKaan);
		isInDraynorMarket = new ZoneRequirement(draynorMarket);
		isInEssenceMine = new ZoneRequirement(essenceMine);
		isInGoblinVillage = new ZoneRequirement(goblinVillage);
		isInGrandTree = new ZoneRequirement(grandTree);
		isInChasmOfTears = new ZoneRequirement(chasmOfTears);
		isInLumbridgeCaves = new ZoneRequirement(lumbridgeCaves);
		isInLumbridgeCellarOrTunnels = new ZoneRequirement(lumbridgeCellar);
		isInMcGruborsWood = new ZoneRequirement(mcGruborsWood1, mcGruborsWood2);
		isInSlayerTower = new ZoneRequirement(slayerTower);
		isInYanillePub = new ZoneRequirement(yanillePub);
		isInMusaPoint = new ZoneRequirement(musaPoint);
		isInRellekka = new ZoneRequirement(rellekka);
		isInWizardsTower = new ZoneRequirement(wizardsTower);

		becameSquire = new VarbitRequirement(1052, 1);
		gotAssignmentDetailsFromSavant = new VarbitRequirement(1053, 1);
		talkedToLordDaquarius = new VarbitRequirement(1055, 1);
		hasKilledBlackKnight = new VarbitRequirement(1055, 2);
		investigatedLordDaquarius = new VarbitRequirement(1058, 1);
		talkedToMageOfZamorak = new VarbitRequirement(1056, 1);
		talkedToSavantNearCanifis = new VarbitRequirement(1065, 1);
		mustChaseToChampionsGuild = new VarbitRequirement(1067, 1);
		mustChaseToDorgeshKaan = new VarbitRequirement(1069, 1);
		mustChaseToEssenceMine = new VarbitRequirement(1071, 1);
		mustChaseToMusaPoint = new VarbitRequirement(1073, 1);
		mustChaseToDraynorMarket = new VarbitRequirement(1075, 1);
		mustChaseToGoblinVillage = new VarbitRequirement(1077, 1);
		mustChaseToArdougneMarket = new VarbitRequirement(1079, 1);
		mustChaseToGrandTree = new VarbitRequirement(1081, 1);
		mustChaseToScorpiusShrine = new VarbitRequirement(1083, 1);
		mustChaseToAliMorrisane = new VarbitRequirement(1085, 1);
		mustChaseToWizardsTower = new VarbitRequirement(1087, 1);
		mustChaseToBrimhavenPub = new VarbitRequirement(1089, 1);
		mustChaseToCastleWars = new VarbitRequirement(1091, 1);
		mustChaseToRellekka = new VarbitRequirement(1093, 1);
		mustChaseToMcGruborsWood = new VarbitRequirement(1095, 1);
		mustChaseToSlayerTower = new VarbitRequirement(1097, 1);
		mustChaseToYanillePub = new VarbitRequirement(1099, 1);
		mustChaseToLumbridgeSwamp = new VarbitRequirement(1101, 1);

		placedRope = new VarbitRequirement(279, 1);
		blackKnightNearby = new NpcHintArrowRequirement(NpcID.BLACK_KNIGHT_4959);
	}

	public void setupSteps()
	{
		WorldPoint locationSirTaffy = new WorldPoint(2997, 3373, 0);

		// Getting started
		talkToSirTiffy1 = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Talk to Sir Tiffy Cashien in Falador Park.");
		talkToSirTiffy1.addDialogSteps("Do you have any jobs for me yet?", "Yes, I'm interested.", TEXT_ASK_ABOUT_WANTED_QUEST, "How will all that help?");

		climbToWhiteKnightsCastleF1 = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2955, 3339, 0),
			"Climb to the second floor of the White Knights' castle.");
		climbToWhiteKnightsCastleF2 = new ObjectStep(this, ObjectID.STAIRCASE_24072, new WorldPoint(2961, 3339, 1),
			"Climb to the second floor of the White Knights' castle.");

		talkToSirAmik1 = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, "Talk to Sir Amik Varze.");
		talkToSirAmik1.addDialogStep("No, not right now...");

		talkToSirTiffyAfterBecomingSquire = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy,
			"Talk to Sir Tiffy Cashien about your new job as a Squire in Falador Park.");
		talkToSirTiffyAfterBecomingSquire.addDialogStep(TEXT_ASK_ABOUT_WANTED_QUEST);

		talkToSirTiffy2 = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Return to Sir Tiffy Cashien in Falador Park.");
		talkToSirTiffy2.addDialogStep(TEXT_ASK_ABOUT_WANTED_QUEST);

		talkToSirAmik2 = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, "Talk to Sir Amik Varze again.");
		talkToSirAmik2.addDialogStep("Sure, I'll help you!");

		talkToSirTiffy3 = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Report back to Sir Tiffy Cashien in Falador Park.");
		talkToSirTiffy3.addDialogStep(TEXT_ASK_ABOUT_WANTED_QUEST);

		getCommorbWithGp = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Get a Commorb from Sir Tiffy Cashien in Falador Park.");
		getCommorbWithGp.addDialogSteps(TEXT_ASK_ABOUT_WANTED_QUEST, "Buy One", "YES");

		getCommorbWithComponents = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Get a Commorb from Sir Tiffy Cashien in Falador Park.");
		getCommorbWithComponents.addDialogSteps(TEXT_ASK_ABOUT_WANTED_QUEST, "Have One Made", "YES");

		doNotGetCommorbWithGp = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Get a Commorb from Sir Tiffy Cashien in Falador Park.");
		doNotGetCommorbWithGp.addDialogSteps(TEXT_ASK_ABOUT_WANTED_QUEST, "NO");

		getCommorbFromSirTiffy = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, locationSirTaffy, "Get a Commorb from Sir Tiffy Cashien in Falador Park.");
		getCommorbFromSirTiffy.addDialogSteps(TEXT_ASK_ABOUT_WANTED_QUEST);
		getCommorbFromSirTiffy.addSubSteps(getCommorbWithGp, getCommorbWithComponents, doNotGetCommorbWithGp);

		talkToSavantAfterGettingCommorb = new DetailedQuestStep(this, "Right-click the Commorb and select 'contact' to contact Savant.", highlightedCommorb);
		talkToSavantAfterGettingCommorb.addDialogStep("Current Assignment");

		// Daquarius and Mage of Zamorak
		enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2884, 3397, 0), "Enter " +
			"Taverley Dungeon.", combatGear, commorb);
		talkToLordDaquarius = new NpcStep(this, NpcID.LORD_DAQUARIUS, new WorldPoint(2891, 9681, 0), "Talk to Lord " +
			"Daquarius in Taverley Dungeon.", commorb);
		talkToLordDaquarius.addSubSteps(enterTaverleyDungeon);

		goToBlackKnightsBase = new DetailedQuestStep(this, new WorldPoint(2896, 9681, 0), "Go to the Black Knights' Base in Taverley Dungeon.");
		killBlackKnight = new NpcStep(this, NpcID.BLACK_KNIGHT, new WorldPoint(2891, 9681, 0),
			"Kill a Black Knight near Daquarius.", true, combatGear);
		talkToIntimidatedLordDaquarius = new NpcStep(this, NpcID.LORD_DAQUARIUS, new WorldPoint(2891, 9681, 0), "Talk to Lord Daquarius in Taverley Dungeon again.");

		talkToMageOfZamorakInWilderness = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK, new WorldPoint(3106, 3558, 0), "Talk to the Mage of Zamorak in the wilderness just north of Edgeville.", commorb);
		talkToMageOfZamorak = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3388, 0),
			"Talk to the Mage of Zamorak in south east Varrock.", commorb);
		talkToMageOfZamorak.addDialogStep("Solus Dellagar");
		giveEssenceToMageOfZamorak = new NpcStep(this, NpcID.MAGE_OF_ZAMORAK_2582, new WorldPoint(3258, 3388, 0),
			"Bring the 20 rune or pure essence to the Mage of Zamorak in Varrock.", commorb, essence);
		giveEssenceToMageOfZamorak.addDialogStep("Solus Dellagar");

		// Hunt for Solus
		scanWithCommorb = new DetailedQuestStep(this, "Use the 'scan' option on the Commorb.", highlightedCommorb);
		moveOutOfCanifisAgain = new DetailedQuestStep(this, new WorldPoint(3450, 3488, 0), "Finish the conversation with Savant. Move out of Canifis again first to trigger it again if needed.");

		chaseToCanifis = new DetailedQuestStep(this, new WorldPoint(3485, 3481, 0), "Go to Canifis. Finish the conversation when Savant calls you. If you accidentally exit this conversation, move away and enter Canifis again.", commorb);
		enterCanifis = new DetailedQuestStep(this, new WorldPoint(3485, 3481, 0), "Go to Canifis.", commorb);
		enterCanifis.addSubSteps(chaseToCanifis, moveOutOfCanifisAgain);

		goToChampionsGuild = new DetailedQuestStep(this, new WorldPoint(3190, 3359, 0), "Go to the Champions' Guild.",
			commorb);
		goDownToLumbridgeCellar = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0), "Go down to the Lumbridge Cellars.", commorb);
		goToDorgeshKaan = new DetailedQuestStep(this, new WorldPoint(3317, 9612, 0), "Go to the mines near the entrance of Dorgesh Kaan.", commorb);
		goToMusaPoint = new DetailedQuestStep(this, new WorldPoint(2916, 3160, 0), "Go to Musa Point on Karamja.", commorb);
		goToDraynorMarket = new DetailedQuestStep(this, new WorldPoint(3082, 3249, 0), "Go to Draynor Market.", commorb);
		goToGoblinVillage = new DetailedQuestStep(this, new WorldPoint(2956, 3506, 0), "Go to goblin village north of Falador.", commorb);
		goToArdougneMarket = new DetailedQuestStep(this, new WorldPoint(2662, 3305, 0), "Go to Ardougne market.", commorb);
		goToGrandTree = new DetailedQuestStep(this, new WorldPoint(2465, 3496, 0), "Go to the Grand Tree.", commorb);
		goToScorpiusShrine = new DetailedQuestStep(this, new WorldPoint(2465, 3227, 0), "Go to the Shrine of Scorpius north of the Observatory.", commorb);
		goToAliMorrisane = new DetailedQuestStep(this, new WorldPoint(3304, 3211, 0), "Go to Ali Morrisane in Al Kharid.", commorb);
		goToWizardsTower = new DetailedQuestStep(this, new WorldPoint(3109, 3164, 0), "Go to the Wizards' Tower.", commorb);
		goToBrimhavenPub = new DetailedQuestStep(this, new WorldPoint(2795, 3162, 0), "Go to the pub in Brimhaven.", commorb);
		goToCastleWars = new DetailedQuestStep(this, new WorldPoint(2441, 3090, 0), "Go to Castle Wars.", commorb);
		goToRellekka = new DetailedQuestStep(this, new WorldPoint(2660, 3657, 0), "Go to Rellekka.", commorb);
		goToMcGruborsWood = new ObjectStep(this, ObjectID.LOOSE_RAILING, new WorldPoint(2662, 3500, 0), "Go to McGrubor's Wood. Fairy ring ALS.", commorb);
		goToSlayerTower = new DetailedQuestStep(this, new WorldPoint(3428, 3537, 0), "Go to the Slayer Tower in Morytania.", commorb);
		goToYanillePub = new DetailedQuestStep(this, new WorldPoint(2551, 3081, 0), "Go to the pub in Yanille.", commorb);
		enterLumbridgeSwampCavesFromTears = new ObjectStep(this, ObjectID.TUNNEL_6658, new WorldPoint(3219, 9534, 2), "Enter the Lumbridge Swamp caves.");
		goToEndOfLumbridgeSwampCaves = new DetailedQuestStep(this, new WorldPoint(3221, 9550, 0),
			"Go to the point indicated in the Lumbridge Swamp Caves.", commorb, lightSource, spinyHelmet.equipped());
		goDownToLumbridgeSwampCaves = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Enter the Lumbridge Swamp Caves. Use a Games Necklace teleport to Tears of Guthix for a faster route.",
			commorb, lightSource, rope.highlighted(), spinyHelmet);
		goDownToLumbridgeSwampCaves.addIcon(ItemID.ROPE);
		goDownToLumbridgeSwampCavesPlacedRope = new ObjectStep(this, ObjectID.DARK_HOLE, new WorldPoint(3169, 3172, 0),
			"Enter the Lumbridge Swamp Caves. Use a Games Necklace teleport to Tears of Guthix for a faster route.",
			commorb, lightSource, spinyHelmet);

		killBlackKnightFromScan = new NpcStep(this, NpcID.BLACK_KNIGHT_4959, "Kill the Black Knight, or run away.");

		huntDownSolus = new DetailedQuestStep(this, "Hunt down Solus Dellagar across Gielinor.");
		huntDownSolus.addSubSteps(goToChampionsGuild, goDownToLumbridgeCellar, goToDorgeshKaan, goToMusaPoint,
			goToDraynorMarket, goToGoblinVillage, goToArdougneMarket, goToGrandTree, goToScorpiusShrine,
			goToAliMorrisane, goToWizardsTower, goToBrimhavenPub, goToCastleWars, goToRellekka, goToMcGruborsWood,
			goToSlayerTower, goToYanillePub, goDownToLumbridgeSwampCaves, goDownToLumbridgeSwampCavesPlacedRope,
			goToEndOfLumbridgeSwampCaves, enterLumbridgeSwampCavesFromTears, scanWithCommorb);

		// Final battle
		goToEssenceMine = new NpcStep(this, NpcID.AUBURY, new WorldPoint(3253, 3402, 0), "Go to the Rune Essence mine by talking to any of the NPC's that can teleport you there.", commorb, combatGear);
		killSolus = new NpcStep(this, NpcID.SOLUS_DELLAGAR_4962, "Kill Solus Dellagar.", combatGear);

		// Wrapping up
		QuestStep talkToSirAmikAfterSolusFight = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, "Talk to Sir Amik Varze at the White Knights' Castle in Falador.");
		QuestStep getSolusHatFromSavant = new DetailedQuestStep(this, "Right click the Commorb and select the 'contact' option to get Solus' Hat from Savant.", highlightedCommorb, oneFreeInventorySlot);
		getSolusHatFromSavant.addDialogStep("Current Assignment");

		goTalkToSirAmikAfterFinalBattle = new ConditionalStep(this, getSolusHatFromSavant, "Show Solus' Hat to Sir " +
			"Amik Varze in Falador.", solusHat);
		((ConditionalStep) goTalkToSirAmikAfterFinalBattle).addStep(new Conditions(solusHat, isInWhiteKnightsCastleF2), talkToSirAmikAfterSolusFight);
		((ConditionalStep) goTalkToSirAmikAfterFinalBattle).addStep(isInWhiteKnightsCastleF1, climbToWhiteKnightsCastleF2);
		((ConditionalStep) goTalkToSirAmikAfterFinalBattle).addStep(solusHat, climbToWhiteKnightsCastleF1);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return QuestUtil.toArrayList(essence, lightSource, rope.hideConditioned(placedRope), combatGear,
			commorbComponentsOrTenThousandGp);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return QuestUtil.toArrayList(amuletOfGlory, ringOfDueling, faladorTeleport, varrockTeleport, canifisTeleport, spinyHelmet);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return QuestUtil.toArrayList(
			new QuestPointRequirement(32),
			new QuestRequirement(QuestHelperQuest.ENTER_THE_ABYSS, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.RECRUITMENT_DRIVE, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.THE_LOST_TRIBE, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.PRIEST_IN_PERIL, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Black Knight (level 33)", "Solus Dellagar (similar strength)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.SLAYER, 5000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Access to the White Knight's Armory"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Getting started", Arrays.asList(talkToSirTiffy1, goTalkToSirAmik1,
			talkToSirTiffy2, goTalkToSirAmik2, talkToSirTiffy3,
			getCommorbFromSirTiffy, talkToSavantAfterGettingCommorb), commorbComponentsOrTenThousandGp));

		allSteps.add(new PanelDetails("Daquarius", Arrays.asList(talkToLordDaquarius, killBlackKnight, talkToIntimidatedLordDaquarius), combatGear, commorb));

		allSteps.add(new PanelDetails("Mage of Zamorak", Arrays.asList(talkToMageOfZamorak, giveEssenceToMageOfZamorak), essence, commorb));

		allSteps.add(new PanelDetails("The Hunt for Solus", Arrays.asList(enterCanifis, huntDownSolus), lightSource,
			rope.hideConditioned(placedRope), spinyHelmet, commorb));

		allSteps.add(new PanelDetails("Final battle", Arrays.asList(goToEssenceMine, killSolus, goTalkToSirAmikAfterFinalBattle), combatGear, commorb));

		return allSteps;
	}
}
