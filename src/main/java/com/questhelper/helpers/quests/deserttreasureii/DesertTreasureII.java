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
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemOnTileRequirement;
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
import net.runelite.api.SpriteID;
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
		shadowBurstRunes, smokeBurstRunes, allBursts, uncharedCells, chargedCells, nardahTeleport, xericTalisman,
		facemask, staminaPotions, eyeTeleport;

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
		drinkStranglewoodPotion, goToRitualSite, fightVardorvis, fightVardorvisSidebar, pickUpTempleKey, getTempleKeyFromRocks,
		returnToDesertWithVardorvisMedallion, useVardorviaMedallionOnStatue;

	ConditionalStep returnToKasondeWithTempleKey, defeatKasonde, goTalkToKasondeAfterFight, goGetVardorvisMedallion;
	Requirement talkedToElissa, talkedToBarus, haveReadPotionNote, haveDrunkPotion, inStrangewood, finishedStranglewoodCutscene,
		talkedToKasonde, inTowerDefenseRoom, defendedKasonde, toldAboutHerbAndBerry, herbTaken, berryTaken,
		inStranglewoodPyramidRoom, defendedKasondeWithHerb, receivedSerum, addedHerb, addedBerry, drankPotion,
		inAnyStranglewood, inVardorvisArea, defeatedVardorvis, templeKeyNearby, kasondeAggressive, givenKasondeKey, defeatedKasonde,
		kasondeRevealedMedallion, gotVardorvisMedallion, finishedVardorvis;
	ItemRequirement potionNote, strangePotion, freezes, berry, herb, unfinishedSerum, serumWithHerb, stranglerSerum, templeKey,
		vardorvisMedallion;
	Zone stranglewood, towerDefenseRoom, stranglewoodPyramidRoom, vardorvisArea;

	/* Perseriya */
	DetailedQuestStep enterWizardBasement, enterPortalToTempleOfTheEye, killDemons, hopOverSteppingStone, talkToPersten, enterPassage1,
		enterPathfinderRoom;

	ConditionalStep goTalkToCatalyticGuardian, goKillDemons, goBoardBoat, goTalkToPersten, goDoPassage1;

	Requirement inWizardBasement, inTempleOfTheEye, inDemonArea, inTentArea, defeatedDemons, attemptedToBoardBoat, talkedToPersten,
		inAbyssRoom1, inAbyssRoom2, inAbyssRoom3, destroyedTether;

	Zone wizardBasement, templeOfTheEye, templeOfTheEye2, demonArea, tentArea, abyssRoom1, abyssRoom2, abyssRoom3, growthRoom;

	Zone path1, path1P2, path2, path3, path4, path5, path6, nearCatalystRoom1, nearCatalystRoom2, catalystRoom, nearGrowth1,
		nearGrowth2, boatRoom1;
	Requirement onPath1, onPath2, onPath3, onPath4, onPath5, onPath6, isNearCatalystRoom, inCatalystRoom, completedCatalystRoom,
		isNearGrowthRoom, growthRepairedSE, growthRepairedSW, growthRepairedNE, growthRepairedNW, inGrowthRoom, repairedGrowths,
		solvedGrowthRoom, inBoatRoom1, haveReadDampTablet;
	DetailedQuestStep doPath1, doPath2, doPath3, doPath4, doPath5, doPath6, enterGreenTeleporter1, enterCatalystRoom, solveCatalystRoom,
		enterBlueTeleporter1, enterGrowthRoom, repairGrowths, growthPuzzle, returnThroughBlueNeuralTeleporter, enterBoatRoom1, getTinderbox,
		burnBoat1, searchSkeletonForKey, searchSkeletonForGunpowder, openChestForDampTablet, readOldTablet;
	ItemRequirement oldTablet, slimyKey, gunpowder, tinderbox;

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
		findingVardorvis.addStep(new Conditions(gotVardorvisMedallion, inVault), useVardorviaMedallionOnStatue);
		findingVardorvis.addStep(gotVardorvisMedallion, returnToDesertWithVardorvisMedallion);
		findingVardorvis.addStep(kasondeRevealedMedallion, goGetVardorvisMedallion);
		findingVardorvis.addStep(defeatedKasonde, goTalkToKasondeAfterFight);
		findingVardorvis.addStep(kasondeAggressive, defeatKasonde);
		findingVardorvis.addStep(defeatedVardorvis, returnToKasondeWithTempleKey);
		findingVardorvis.addStep(new Conditions(inVardorvisArea, defeatedVardorvis), pickUpTempleKey);
		findingVardorvis.addStep(new Conditions(inVardorvisArea, drankPotion), fightVardorvis);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, drankPotion), goToRitualSite);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, addedBerry), drinkStranglewoodPotion);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, addedHerb), addBerry);
		findingVardorvis.addStep(new Conditions(inAnyStranglewood, receivedSerum), addHerb);
		findingVardorvis.addStep(new Conditions(inStranglewoodPyramidRoom, defendedKasondeWithHerb, herbTaken, berryTaken),
			talkToKasondeWithHerbAndBerry);
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
		findingVardorvis.addStep(new Conditions(haveReadPotionNote, strangePotion.alsoCheckBank(questBank)), drinkPotion);
		findingVardorvis.addStep(new Conditions(nor(haveReadPotionNote), potionNote.alsoCheckBank(questBank)), readPotionNote);
		findingVardorvis.addStep(talkedToBarus, searchDesk);
		findingVardorvis.addStep(talkedToElissa, talkToBarus);

		ConditionalStep doPathfinderRoom = new ConditionalStep(this, enterPathfinderRoom);
		doPathfinderRoom.addStep(onPath6, doPath6);
		doPathfinderRoom.addStep(onPath5, doPath5);
		doPathfinderRoom.addStep(onPath4, doPath4);
		doPathfinderRoom.addStep(onPath3, doPath3);
		doPathfinderRoom.addStep(onPath2, doPath2);
		doPathfinderRoom.addStep(onPath1, doPath1);

		Conditions doingPathfinderRoom = new Conditions(LogicType.OR, onPath1, onPath2, onPath3, onPath4, onPath5, onPath6);

		ConditionalStep findingPerseriya = new ConditionalStep(this, goTalkToCatalyticGuardian);
		findingPerseriya.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom,
			haveReadDampTablet, gunpowder, tinderbox), burnBoat1);
		findingPerseriya.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom,
				haveReadDampTablet, gunpowder), getTinderbox);
		findingPerseriya.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, haveReadDampTablet),
			searchSkeletonForGunpowder);
		findingPerseriya.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, oldTablet),
			readOldTablet);
		findingPerseriya.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom, slimyKey),
			openChestForDampTablet);
		findingPerseriya.addStep(new Conditions(inBoatRoom1, completedCatalystRoom, destroyedTether, solvedGrowthRoom),
			searchSkeletonForKey);
		findingPerseriya.addStep(new Conditions(new Conditions(LogicType.OR, inGrowthRoom, isNearGrowthRoom),
			completedCatalystRoom, destroyedTether, solvedGrowthRoom), returnThroughBlueNeuralTeleporter);
		findingPerseriya.addStep(new Conditions(inAbyssRoom1, completedCatalystRoom, destroyedTether), enterBoatRoom1);
		findingPerseriya.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether, repairedGrowths), growthPuzzle);
		findingPerseriya.addStep(new Conditions(inGrowthRoom, completedCatalystRoom, destroyedTether), repairGrowths);
		findingPerseriya.addStep(new Conditions(isNearGrowthRoom, completedCatalystRoom, destroyedTether), enterGrowthRoom);
		findingPerseriya.addStep(new Conditions(inAbyssRoom1, completedCatalystRoom, destroyedTether), enterBlueTeleporter1);
		findingPerseriya.addStep(new Conditions(inCatalystRoom, destroyedTether), solveCatalystRoom);
		findingPerseriya.addStep(new Conditions(isNearCatalystRoom, destroyedTether), enterCatalystRoom);
		findingPerseriya.addStep(new Conditions(inAbyssRoom1, destroyedTether), enterGreenTeleporter1);
		findingPerseriya.addStep(doingPathfinderRoom, doPathfinderRoom);
		findingPerseriya.addStep(inAbyssRoom1, enterPathfinderRoom);
		findingPerseriya.addStep(talkedToPersten, goDoPassage1);
		findingPerseriya.addStep(attemptedToBoardBoat, goTalkToPersten);
		findingPerseriya.addStep(defeatedDemons, goBoardBoat);
		findingPerseriya.addStep(inDemonArea, goKillDemons);

		ConditionalStep findingTheFour = new ConditionalStep(this, findingVardorvis);
		findingTheFour.addStep(finishedVardorvis, findingPerseriya);
		steps.put(42, findingTheFour);
		/* Entered stranglewood */
		steps.put(44, findingTheFour);
		/* Entered vardovis arena */
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
		staminaPotions = new ItemRequirement("Stamina potions", ItemCollections.STAMINA_POTIONS);
		facemask = new ItemRequirement("Facemask", ItemID.FACEMASK);
		facemask.addAlternates(ItemCollections.SLAYER_HELMETS);

		eyeTeleport = new ItemRequirement("Teleport to Temple of the Eye via minigame teleport or Amulet of the Eye", ItemID.AMULET_OF_THE_EYE_26990);
		eyeTeleport.addAlternates(ItemID.AMULET_OF_THE_EYE, ItemID.AMULET_OF_THE_EYE_26992, ItemID.AMULET_OF_THE_EYE_26994);

		/* Quest Items */
		uncharedCells = new ItemRequirement("Uncharged cells", ItemID.UNCHARGED_CELL_28402);
		chargedCells = new ItemRequirement("Charged cells", ItemID.CHARGED_CELL);

		potionNote = new ItemRequirement("Potion note", ItemID.POTION_NOTE);
		strangePotion = new ItemRequirement("Strange potion", ItemID.STRANGE_POTION_28383);
		freezes = new ItemRequirement("Freezing spells STRONGLY recommended", -1);
		berry = new ItemRequirement("Argian berries", ItemID.ARGIAN_BERRIES);
		berry.setTooltip("You can get another from the south west corner of The Stranglewood");
		herb = new ItemRequirement("Korbal herb", ItemID.KORBAL_HERB);
		herb.setTooltip("You can get another from the north west corner of The Stranglewood");
		unfinishedSerum = new ItemRequirement("Unfinished serum", ItemID.UNFINISHED_SERUM);
		serumWithHerb = new ItemRequirement("Unfinished serum (herb added)", ItemID.UNFINISHED_SERUM_28387);
		stranglerSerum = new ItemRequirement("Strangler serum", ItemID.STRANGLER_SERUM);
		templeKey = new ItemRequirement("Temple key", ItemID.TEMPLE_KEY_28389);
		vardorvisMedallion = new ItemRequirement("Vardorvis' medallion", ItemID.VARDORVIS_MEDALLION);
		vardorvisMedallion.setTooltip("You can get another from Kasonde's hideout");

		oldTablet = new ItemRequirement("Old tablet", ItemID.OLD_TABLET);
		slimyKey = new ItemRequirement("Slimy key", ItemID.SLIMY_KEY);
		gunpowder = new ItemRequirement("Gunpowder", ItemID.GUNPOWDER_28442);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
	}

	public void loadZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		digsiteHole = new Zone(new WorldPoint(3400, 9800, 0), new WorldPoint(3419, 9824, 0));
		golemRoom = new Zone(new WorldPoint(2767, 6425, 0), new WorldPoint(2807, 6459, 0));
		stranglewood = new Zone(new WorldPoint(1087, 3264, 0), new WorldPoint(1261, 3458, 0));
		towerDefenseRoom = new Zone(new WorldPoint(1160, 9740, 0), new WorldPoint(1210, 9780, 0));
		stranglewoodPyramidRoom = new Zone(new WorldPoint(1177, 9810, 0), new WorldPoint(1190, 9846, 0));
		vardorvisArea = new Zone(new WorldPoint(1119, 3405, 0), new WorldPoint(1140, 3430, 0));

		/* Perseriya */
		wizardBasement = new Zone(new WorldPoint(3094, 9553, 0), new WorldPoint(3125, 9582, 0));
		templeOfTheEye = new Zone(new WorldPoint(2370, 5627, 0), new WorldPoint(2425, 5682, 0));
		templeOfTheEye2 = new Zone(new WorldPoint(2433, 5698, 0), new WorldPoint(3648, 9523, 0));
		demonArea = new Zone(new WorldPoint(2006, 6424, 0), new WorldPoint(2030, 6442, 0));
		tentArea = new Zone(new WorldPoint(2031, 6424, 0), new WorldPoint(2065, 6448, 0));

		abyssRoom1 = new Zone(new WorldPoint(2175, 6400, 0), new WorldPoint(2244, 6467, 0));
		abyssRoom2 = new Zone(new WorldPoint(1856, 6400, 0), new WorldPoint(1926, 6467, 0));
		abyssRoom2 = new Zone(new WorldPoint(1725, 6400, 0), new WorldPoint(1794, 6467, 0));

		path1 = new Zone(new WorldPoint(2194, 6443, 0), new WorldPoint(2196, 6453, 0));
		path1P2 = new Zone(new WorldPoint(2197, 6452, 0), new WorldPoint(2197, 6453, 0));
		path2 = new Zone(new WorldPoint(2197, 6443, 0), new WorldPoint(2206, 6445, 0));
		path3 = new Zone(new WorldPoint(2201, 6437, 0), new WorldPoint(2203, 6442, 0));
		path4 = new Zone(new WorldPoint(2193, 6437, 0), new WorldPoint(2200, 6439, 0));
		path5 = new Zone(new WorldPoint(2195, 6434, 0), new WorldPoint(2205, 6437, 0));
		path6 = new Zone(new WorldPoint(2206, 6433, 0), new WorldPoint(2210, 6438, 0));

		nearCatalystRoom1 = new Zone(new WorldPoint(2177, 6401, 0), new WorldPoint(2237, 6431, 0));
		nearCatalystRoom2 = new Zone(new WorldPoint(2177, 6431, 0), new WorldPoint(2192, 6463, 0));

		catalystRoom = new Zone(new WorldPoint(2192, 6405, 0), new WorldPoint(2207, 6420, 0));

		nearGrowth1 = new Zone(new WorldPoint(2225, 6417, 0), new WorldPoint(2237, 6439, 0));
		nearGrowth2 = new Zone(new WorldPoint(2231, 6438, 0), new WorldPoint(2238, 6450, 0));

		growthRoom = new Zone(new WorldPoint(2218, 6422, 0), new WorldPoint(2233, 6437, 0));
		boatRoom1 = new Zone(new WorldPoint(2220, 6402, 0), new WorldPoint(2238, 6415, 0));
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
		addedHerb = serumWithHerb.alsoCheckBank(questBank);
		addedBerry = stranglerSerum.alsoCheckBank(questBank);
		drankPotion = new VarbitRequirement(15125, 38, Operation.GREATER_EQUAL);
		inAnyStranglewood = new Conditions(LogicType.OR, inStranglewoodPyramidRoom, inStrangewood);

		// Vardorvis arena entered
		// 15125 38->40
		// 14862 44->46

		defeatedVardorvis = new VarbitRequirement(15125, 42, Operation.GREATER_EQUAL);
		inVardorvisArea = new ZoneRequirement(vardorvisArea);
		templeKeyNearby = new ItemOnTileRequirement(templeKey);
		givenKasondeKey = new VarbitRequirement(15125, 46, Operation.GREATER_EQUAL);
		kasondeAggressive = new VarbitRequirement(15125, 48, Operation.GREATER_EQUAL);
		defeatedKasonde = new VarbitRequirement(15125, 50, Operation.GREATER_EQUAL);
		kasondeRevealedMedallion = new VarbitRequirement(15125, 52, Operation.GREATER_EQUAL);
		gotVardorvisMedallion = new VarbitRequirement(15125, 54, Operation.GREATER_EQUAL);

		// Probably 15132 0->1 for placed medallion works too
		finishedVardorvis = new VarbitRequirement(15125, 56, Operation.GREATER_EQUAL);
		// 15122 0->1, talked to Asgarnia with medallion first time?

		/* Perseriya */
		inWizardBasement = new ZoneRequirement(wizardBasement);
		inTempleOfTheEye = new ZoneRequirement(templeOfTheEye, templeOfTheEye2);
		inDemonArea = new ZoneRequirement(demonArea);
		inTentArea = new ZoneRequirement(tentArea);
		inAbyssRoom1 = new ZoneRequirement(abyssRoom1);
		inAbyssRoom2 = new ZoneRequirement(abyssRoom2);
		inAbyssRoom3 = new ZoneRequirement(abyssRoom3);
		// 15128 2->4, talked to guardian

		// Entered rift, 15128 4->6, 14862 54->56
		// 13092 0->100
		// 13095 0->100
		// 5934 0->1->2->3->4???

		final int PERSERIYA_VARBIT = 15128;

		defeatedDemons = new VarbitRequirement(PERSERIYA_VARBIT, 8, Operation.GREATER_EQUAL);

		attemptedToBoardBoat = new VarbitRequirement(PERSERIYA_VARBIT, 10, Operation.GREATER_EQUAL);
		// 12139 1->0 after boat attempt
		talkedToPersten = new VarbitRequirement(PERSERIYA_VARBIT, 14, Operation.GREATER_EQUAL);

		// In room 1
		// 15128 14->16
		// 15193 0->1
		// 15192 0->2
		// 15194 0->1

		onPath1 = new ZoneRequirement(path1, path1P2);
		onPath2 = new ZoneRequirement(path2);
		onPath3 = new ZoneRequirement(path3);
		onPath4 = new ZoneRequirement(path4);
		onPath5 = new ZoneRequirement(path5);
		onPath6 = new ZoneRequirement(path6);

		destroyedTether = new VarbitRequirement(15258, 1);

		isNearCatalystRoom = new ZoneRequirement(nearCatalystRoom1, nearCatalystRoom2);
		inCatalystRoom = new ZoneRequirement(catalystRoom);

		completedCatalystRoom = new VarbitRequirement(15259, 1);
		isNearGrowthRoom = new ZoneRequirement(nearGrowth1, nearGrowth2);
		// On login to main area, 12164 0->1
		// 13989 0->1


		// In growth room:
		// 15203 0->2
		// 15204 0->1
		// 15205 0->3
		// 15206 0->6
		// 15207 0->4
		// 15208 0->5

		// 15210 0->1 repaired SW corner
		growthRepairedNE = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2232, 6435, 0));
		growthRepairedNW = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2219, 6435, 0));
		growthRepairedSE = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2232, 6435, 0));
		growthRepairedSW = new ObjectCondition(ObjectID.LUMINESCENT_GROWTH, new WorldPoint(2231, 6423, 0));

		// All repaired:
		// 15197 0->1
		// 15198 0->1
		// 15199 0->1
		// 200
		// 201
		// 202
		inGrowthRoom = new ZoneRequirement(growthRoom);

		repairedGrowths = new VarbitRequirement(15210, 4);
		solvedGrowthRoom = new VarbitRequirement(15260, 1);

		// Entered boat room, 15261 0->1
		inBoatRoom1 = new ZoneRequirement(boatRoom1);
		// TODO: Verify if order is random for this stuff, and thus variable needs to consider some shift based on area?
		haveReadDampTablet = new VarbitRequirement(PERSERIYA_VARBIT, 18, Operation.GREATER_EQUAL);

		// 18->20, burned ship
		// TODO: Verify if this is a required progression
		// 15128 20->22, talked to Persten
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
				"directly south of the Digsite.");
		talkToBalando.addTeleport(senntistenTeleport);

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
		drinkPotion = new ItemStep(this, "Drink the strange potion.", strangePotion.highlighted());
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
			"Talk to Kasonde.", berry, herb);
		addHerb = new DetailedQuestStep(this, "Add the herb to unfinished serum.", herb.highlighted(), unfinishedSerum.highlighted());
		addBerry = new DetailedQuestStep(this, "Add the berries to the serum.", serumWithHerb.highlighted(), berry.highlighted());
		drinkStranglewoodPotion = new DetailedQuestStep(this, "Drink the strangler serum.", stranglerSerum.highlighted());

		goToRitualSite = new ObjectStep(this, NullObjectID.NULL_49495, new WorldPoint(1118, 3428, 0), "Go to the ritual site to the west, ready to fight the boss of the area.", combatGear);
		goToRitualSite.setLinePoints(Arrays.asList(
			new WorldPoint(1174, 3427, 0),
			new WorldPoint(1174, 3416, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1144, 3357, 0),
			new WorldPoint(1144, 3342, 0),
			/* Bridge to west */
			new WorldPoint(1126, 3344, 0),
			new WorldPoint(1116, 3344, 0),
			new WorldPoint(1115, 3355, 0),
			new WorldPoint(1109, 3356, 0),
			/* Bridge to north */
			new WorldPoint(1109, 3383, 0),
			new WorldPoint(1111, 3400, 0),
			new WorldPoint(1106, 3410, 0),
			new WorldPoint(1106, 3434, 0),
			new WorldPoint(1112, 3435, 0),
			new WorldPoint(1124, 3423, 0),
			new WorldPoint(0, 0, 0),
			/* From boat */
			new WorldPoint(1196, 3450, 0),
			new WorldPoint(1197, 3428, 0),
			new WorldPoint(1216, 3412, 0),
			new WorldPoint(1215, 3395, 0),
			new WorldPoint(1188, 3395, 0),
			new WorldPoint(1163, 3395, 0)
		));

		fightVardorvis = new NpcStep(this, NpcID.VARDORVIS, new WorldPoint(1129, 3419, 0), "Defeat Vardorvis. Look at the sidebar for more details. Protect from Melee when he's not using a special attack.");
		((NpcStep) fightVardorvis).addAlternateNpcs(NpcID.VARDORVIS_12224, NpcID.VARDORVIS_12228, NpcID.VARDORVIS_12425, NpcID.VARDORVIS_12426);
		fightVardorvisSidebar = new DetailedQuestStep(this, "Defeat Vardorvis. It's recommended to watch a video to get an understanding of his abilities.");
		fightVardorvisSidebar.addText("Swinging axes: He will spawn axes around the arena, which will go to the opposite corner to which they appear. Avoid them.");
		fightVardorvisSidebar.addText("Homing spikes: Vardorvis hits the ground, causing a spike to appear under you. Move off the tile to avoid.");
		fightVardorvisSidebar.addText("Head projectile: A tentacle will appear with a head on it, firing a green projectile. Protect from Missiles should be flicked to.");
		fightVardorvisSidebar.addText("Virus cells: Red splotches appear on the screen. Click them all within the time limit to avoid damage.");
		fightVardorvisSidebar.addSubSteps(fightVardorvis);

		pickUpTempleKey = new ItemStep(this,  "Pick up the Temple Key in the Vardorvis arena.", templeKey);
		getTempleKeyFromRocks = new ObjectStep(this, NullObjectID.NULL_49495, new WorldPoint(1118, 3428, 0),
			"Go to the ritual site to the west, and search the rocks to get another Temple Key.");

		DetailedQuestStep enterKasondeWithKey = new ObjectStep(this, ObjectID.ENTRY_48723, new WorldPoint(1174, 3428, 0),
		"");
		DetailedQuestStep giveKasondeKey =  new NpcStep(this, NpcID.KASONDE_12258, new WorldPoint(1183, 9824, 0),
			"");
		returnToKasondeWithTempleKey = new ConditionalStep(this, boardBoat,
			"Return to Kasonde with the temple key, who is inside the main pyramid of the area to the north. " +
			"Be ready for another fight.", combatGear, templeKey);
		returnToKasondeWithTempleKey.addStep(new Conditions(inStranglewoodPyramidRoom, templeKey.alsoCheckBank(questBank)
			.hideConditioned(givenKasondeKey)), giveKasondeKey);
		returnToKasondeWithTempleKey.addStep(new Conditions(inAnyStranglewood, templeKey.alsoCheckBank(questBank)), enterKasondeWithKey);
		returnToKasondeWithTempleKey.addStep(templeKeyNearby, pickUpTempleKey);
		returnToKasondeWithTempleKey.addStep(inAnyStranglewood, getTempleKeyFromRocks);

		DetailedQuestStep kasondeFight = new NpcStep(this, NpcID.KASONDE_12263, new WorldPoint(1183, 9824, 0),
			"");
		defeatKasonde = new ConditionalStep(this, boardBoat,
			"Defeat Kasonde in the pyramid. Avoid the potions he throws. If he throws his hands up, hide behind a pillar.", combatGear);
		defeatKasonde.addStep(new Conditions(inStranglewoodPyramidRoom), kasondeFight);
		defeatKasonde.addStep(new Conditions(inAnyStranglewood), enterKasondeWithKey);

		/* Post-fight Kasonde */
		NpcStep talkToKasondeAfterFight = new NpcStep(this, NpcID.KASONDE_12264, new WorldPoint(1183, 9824, 0), "");

		goTalkToKasondeAfterFight = new ConditionalStep(this, boardBoat,
			"Talk to Kasonde again in the pyramid.");
		goTalkToKasondeAfterFight.addStep(new Conditions(inStranglewoodPyramidRoom), talkToKasondeAfterFight);
		goTalkToKasondeAfterFight.addStep(new Conditions(inAnyStranglewood), enterKasondeWithKey);

		ObjectStep searchChestForVardorvisMedallion = new ObjectStep(this, NullObjectID.NULL_49496, new WorldPoint(1196, 3411, 0), "");
		/* Getting the medallion */
		ObjectStep leavePyramid = new ObjectStep(this, ObjectID.STAIRS_48730, new WorldPoint(1183, 9809, 0), "");
		goGetVardorvisMedallion = new ConditionalStep(this, boardBoat,
			"Search the north east chest in Kadsone's initial hideout for a medallion.");
		goGetVardorvisMedallion.addStep(inStranglewoodPyramidRoom, leavePyramid);
		goGetVardorvisMedallion.addStep(new Conditions(inAnyStranglewood), searchChestForVardorvisMedallion);

		returnToDesertWithVardorvisMedallion = new ObjectStep(this, ObjectID.VAULT_DOOR_46743,
			new WorldPoint(3511, 2971, 0), "Return to the Vault door north east of Nardah.", vardorvisMedallion);
		returnToDesertWithVardorvisMedallion.addTeleport(nardahTeleport);

		useVardorviaMedallionOnStatue = new ObjectStep(this, NullObjectID.NULL_49499, new WorldPoint(3942, 9636, 1),
			"Use the medallion on the north east statue.", vardorvisMedallion.highlighted());
		useVardorviaMedallionOnStatue.addIcon(ItemID.VARDORVIS_MEDALLION);


		/* Perseriya */
		enterWizardBasement = new ObjectStep(this, ObjectID.LADDER_2147, new WorldPoint(3104, 3162, 0),
			"");
		enterWizardBasement.addTeleport(eyeTeleport);
		enterPortalToTempleOfTheEye = new ObjectStep(this, ObjectID.PORTAL_43765, new WorldPoint(3104, 9574, 0),
			"Enter the portal to the Temple of the Eye.");
		NpcStep talkToCatalyticGuardian = new NpcStep(this, NpcID.CATALYTIC_GUARDIAN, new WorldPoint(3611, 9473, 0),
			"Talk to the Catalytic Guardian to travel into the Rift.");
		talkToCatalyticGuardian.addAlternateNpcs(NpcID.CATALYTIC_GUARDIAN_12384);
		talkToCatalyticGuardian.addDialogStep("Yes.");

		killDemons = new NpcStep(this, NpcID.SCARRED_IMP, new WorldPoint(2021, 6433, 0),
			"", true);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.ABYSSAL_WALKER_12392, NpcID.ABYSSAL_WALKER_12394);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.ABYSSAL_LEECH_12391, NpcID.ABYSSAL_LEECH_12393);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.GREATER_DEMON_12387, NpcID.LESSER_DEMON_12389);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_BLACK_DEMON, NpcID.BLACK_DEMON_12385);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_GREATER_DEMON_12388, NpcID.SCARRED_LESSER_DEMON_12390);
		((NpcStep) killDemons).addAlternateNpcs(NpcID.SCARRED_BLACK_DEMON, NpcID.SCARRED_LESSER_DEMON_12378);

		ConditionalStep goToAbyss = new ConditionalStep(this, enterWizardBasement);
		goToAbyss.addStep(inTempleOfTheEye, talkToCatalyticGuardian);
		goToAbyss.addStep(inWizardBasement, enterPortalToTempleOfTheEye);

		goTalkToCatalyticGuardian = new ConditionalStep(this, goToAbyss,
			"Talk to the Catalytic Guardian by the portal in the Temple of the Eye.", combatGear, facemask);

		goKillDemons = new ConditionalStep(this, goToAbyss, "Kill all the demons and abyssal entities in the rift.", combatGear);
		goKillDemons.addStep(inDemonArea, killDemons);

		hopOverSteppingStone = new ObjectStep(this, ObjectID.STEPPING_STONE_49209, new WorldPoint(2031, 6430, 0),
			"Hop across the stepping stone.");

		ObjectStep boardAbyssBoat = new ObjectStep(this, ObjectID.ROWBOAT_49212, new WorldPoint(2065, 6436, 0),
			"");

		goBoardBoat = new ConditionalStep(this, goToAbyss, "Attempt to board the boat to the east.");
		goBoardBoat.addStep(inTentArea, boardAbyssBoat);
		goBoardBoat.addStep(inDemonArea, hopOverSteppingStone);


		talkToPersten = new NpcStep(this, NpcID.WIZARD_PERSTEN_12380, new WorldPoint(2051, 6443, 0), "");
		goTalkToPersten = new ConditionalStep(this, goToAbyss, "Talk to Wizard Persten in the Rift.");
		goTalkToPersten.addStep(inTentArea, talkToPersten);
		goTalkToPersten.addStep(inDemonArea, hopOverSteppingStone);

		enterPassage1 = new ObjectStep(this, NullObjectID.NULL_49526, new WorldPoint(2043, 6441, 0),
			"Enter the north west passage.");

		goDoPassage1 = new ConditionalStep(this, goToAbyss);
		goDoPassage1.addStep(inTentArea, enterPassage1);
		goDoPassage1.addStep(inDemonArea, hopOverSteppingStone);

		enterPathfinderRoom = new ObjectStep(this, ObjectID.PASSAGE_49410, new WorldPoint(2196, 6454, 0),
			"Enter the pathfinder room to the east of the entrance.");
		enterPathfinderRoom.setLinePoints(Arrays.asList(
			new WorldPoint(2178, 6459, 0),
			new WorldPoint(2192, 6461, 0),
			new WorldPoint(2215, 6458, 0),
			new WorldPoint(2215, 6452, 0),
			new WorldPoint(2210, 6452, 0),
			new WorldPoint(2206, 6456, 0),
			new WorldPoint(2197, 6456, 0)
		));

		doPath1 = new DetailedQuestStep(this, new WorldPoint(2197, 6444, 0),
			"Move the nearest pathfinder from the north, and follow it within a 3x3 area until the next pathfinder.");
		doPath1.addTileMarker(new WorldPoint(2195, 6451, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath1.setLinePoints(Arrays.asList(
			new WorldPoint(2195, 6450, 0),
			new WorldPoint(2195, 6444, 0),
			new WorldPoint(2197, 6444, 0)
		));

		doPath2 = new DetailedQuestStep(this, new WorldPoint(2202, 6442, 0),
			"Move the next pathfinder from the west, and step off when safe to the south pathfinder.");
		doPath2.addTileMarker(new WorldPoint(2197, 6444, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath2.setLinePoints(Arrays.asList(
			new WorldPoint(2199, 6444, 0),
			new WorldPoint(2202, 6444, 0),
			new WorldPoint(2202, 6442, 0)
		));

		doPath3 = new DetailedQuestStep(this, new WorldPoint(2198, 6438, 0),
			"Move the next pathbreaker from the north, and step off when safe to the west pathbreaker.");
		doPath3.addTileMarker(new WorldPoint(2202, 6442, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath3.setLinePoints(Arrays.asList(
			new WorldPoint(2202, 6440, 0),
			new WorldPoint(2201, 6439, 0),
			new WorldPoint(2198, 6438, 0)
		));

		doPath4 = new DetailedQuestStep(this, new WorldPoint(2196, 6435, 0),
			"Move the next pathbreaker from the west, and step off when safe to the south-west pathbreaker.");
		doPath4.addTileMarker(new WorldPoint(2198, 6438, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath4.setLinePoints(Arrays.asList(
			new WorldPoint(2197, 6438, 0),
			new WorldPoint(2196, 6435, 0)
		));

		doPath5 = new DetailedQuestStep(this, new WorldPoint(2207, 6436, 0),
			"Move the next pathbreaker from the east, and step off when safe to the east pathbreaker.");
		doPath5.addTileMarker(new WorldPoint(2196, 6435, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath5.setLinePoints(Arrays.asList(
			new WorldPoint(2196, 6435, 0),
			new WorldPoint(2207, 6436, 0)
		));

		doPath6 = new ObjectStep(this, ObjectID.ABYSSAL_TETHER, new WorldPoint(2210, 6433, 0),
			"Move the next pathbreaker from the south, and step off to destroy the abyssal tether.");
		doPath6.addTileMarker(new WorldPoint(2207, 6436, 0), SpriteID.RS2_SWORD_POINTED_LEFT);
		doPath6.setLinePoints(Arrays.asList(
			new WorldPoint(2207, 6436, 0),
			new WorldPoint(2207, 6433, 0),
			new WorldPoint(2209, 6433, 0)
		));

		enterGreenTeleporter1 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49415, new WorldPoint(2234, 6461, 0),
			"Enter the neural teleporter in the north east corner.");

		enterCatalystRoom = new ObjectStep(this, ObjectID.PASSAGE_49411, new WorldPoint(2190, 6412, 0),
			"Enter the catalyst room in the south west corner of the area.");

		solveCatalystRoom = new DetailedQuestStep(this, "You need to use the rune nerves on the catalyst which match the current symbol until the walkers die.");
		solveCatalystRoom.addText("You can make additional rune nerves by combining nerves:");
		solveCatalystRoom.addText("Mind + mind = soul.");
		solveCatalystRoom.addText("Water + earth = nature.");
		solveCatalystRoom.addText("Soul + nature = cosmic.");
		solveCatalystRoom.addText("Cosmic + earth = astral.");
		solveCatalystRoom.addText("Fire + air = smoke.");
		solveCatalystRoom.addText("Mind + water = blood.");
		solveCatalystRoom.addText("Smoke + blood = wrath.");

		enterBlueTeleporter1 = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER_49414, new WorldPoint(2181, 6425, 0),
			"Enter the blue teleporter to the west of the area.");

		enterGrowthRoom = new ObjectStep(this, ObjectID.PASSAGE_49412, new WorldPoint(2225, 6421, 0),
			"Enter the damage growth room to the south.");

		repairGrowths = new ObjectStep(this, ObjectID.DAMAGED_GROWTH, "Take lures from the light leeches to repair the growths. Protect from Melee to not take damage from them.", true);
		growthPuzzle = new GrowthPuzzleStep(this);

		returnThroughBlueNeuralTeleporter = new ObjectStep(this, ObjectID.NEURAL_TELEPORTER,
			new WorldPoint(2237, 6444, 0), "Head to the south east room via the blue neural transmitter.");
		enterBoatRoom1 = new ObjectStep(this, ObjectID.PASSAGE_49418, new WorldPoint(2220, 6402, 0),
			"Enter the south east room via the southern corridor.");

		getTinderbox = new ObjectStep(this, ObjectID.CRATE_49425, new WorldPoint(2236, 6402, 0), "Search the south eastern crate for a tinderbox.");
		burnBoat1 = new ObjectStep(this, ObjectID.SHIPWRECK, new WorldPoint(2235, 6409, 0), "Burn the boat.");
		searchSkeletonForKey = new ObjectStep(this, ObjectID.SKELETON_49426, new WorldPoint(2221, 6414, 0),
			"Search the north west skeleton for a key.");
		searchSkeletonForGunpowder = new ObjectStep(this, ObjectID.SKELETON_49427, new WorldPoint(2223, 6402, 0),
			"Search the south west skeleton for gunpowder.");
		openChestForDampTablet = new ObjectStep(this, ObjectID.CHEST_49420, new WorldPoint(2231, 6413, 0),
			"Open the chest in the north of the room for a damp tablet.", slimyKey);
		((ObjectStep) openChestForDampTablet).addAlternateObjects(ObjectID.CHEST_49422);
		readOldTablet = new DetailedQuestStep(this, "Read the old tablet.", oldTablet.highlighted());
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, allBursts, facemask);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(waterSource, senntistenTeleport, staminaPotions);
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
				talkToKasondeWithHerbAndBerry, addHerb, addBerry, drinkPotion, goToRitualSite, fightVardorvisSidebar,
				pickUpTempleKey, returnToKasondeWithTempleKey, defeatKasonde, goTalkToKasondeAfterFight,
				goGetVardorvisMedallion, returnToDesertWithVardorvisMedallion, useVardorviaMedallionOnStatue),
			Arrays.asList(combatGear),
			Arrays.asList(xericTalisman, freezes)));
		allSteps.add(new PanelDetails("Perseriya",
			Arrays.asList(goTalkToCatalyticGuardian, goKillDemons, hopOverSteppingStone, goBoardBoat, goTalkToPersten, enterPassage1,
				enterPathfinderRoom, doPath1, doPath2, doPath3, doPath4, doPath5, doPath6, enterGreenTeleporter1, enterCatalystRoom,
				solveCatalystRoom, enterBlueTeleporter1, enterGrowthRoom, repairGrowths, growthPuzzle, returnThroughBlueNeuralTeleporter,
				enterBoatRoom1, searchSkeletonForKey, openChestForDampTablet, readOldTablet, searchSkeletonForGunpowder, getTinderbox, burnBoat1),
			Arrays.asList(facemask),
			Arrays.asList(eyeTeleport, staminaPotions)));

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
