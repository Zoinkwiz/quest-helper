/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.regicide;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.REGICIDE
)
public class Regicide extends BasicQuestHelper
{
	//Items Required
	ItemRequirement rope1, rope2, ropeHighlight, bow, arrows, arrowsHighlight, spade, spadeHighlight, plank,
		plankHighlight, bucket, tinderbox, tinderboxHighlight, combatEquipment, agilityPotions, oilyCloth,
		oilyClothHighlight, fireArrow, litArrow, litArrowEquipped, bucketHighlight;

	ItemRequirement coal20, limestone, stripOfCloth, pestle, gloves, pot, cookedRabbit, crystalPendant, barrel2,
	coalBarrel2, sulphur, bigBookOfBangs, quicklime, groundQuicklime, groundSulphur, naphtha, naphthaMix,
		barrelBombFused, barrelBombUnfused, iorwerthsMessage;

	//Items Recommended
	ItemRequirement food, staminaPotions, coins, antipoisons, faladorTeleport, westArdougneTeleport, summerPie, axe;

	Requirement inCastleFloor2, inWestArdougne, isBeforeRockslide1, isBeforeRockslide2, isBeforeRockslide3,
		isBeforeBridge, isNorthEastOfBridge,
		isBeforeThePit, isAfterThePit, isBeforeTheGrid, isAtTheGrid, isAfterTheGrid, isBeforeTrap1,
		isBeforeTrap2, isBeforeTrap3, isBeforeTrap4, isBeforeTrap5, isInWellArea, isAtOrb1, isInsideCell, isBeforeLedge,
		isAfterMaze, isInUnicornArea, isInUnicornArea2, isInKnightsArea, isBeforeIbansDoor, isInDwarfCavern, isInFinalArea,
	    isInFallArea, isInUndergroundSection2, isInUndergroundSection3, isInMaze, isInTemple, isInPostIbanArea,
		isInIbanRoom, isInWellEntrance, isInElvenLands, inForestNearCave, inForestSectionAfterCave, inWestForestPath,
		inSouthOfLog, inIorwerthCamp, inGuardArea, inTyrasCamp;

	Requirement idrisNearby, guardNearby, hasReadBook, knowHowToMakeFuse, askedAboutBarrel, askedAboutQuicklime,
		askedAboutSulphur, askedAboutNaptha, knowHowToMakeBomb, hadGroundQuicklime, hadGroundSulphur, talkedToChemist,
		coalInStill, givenRabbit, arianwynNearby;

	DetailedQuestStep goToArdougneCastleFloor2, talkToKingLathas, goDownCastleStairs, enterWestArdougne,
		enterTheDungeon, climbOverRockslide1, climbOverRockslide2, climbOverRockslide3, talkToKoftikAtBridge,
		useClothOnArrow, lightArrow, walkNorthEastOfBridge, shootBridgeRope, collectPlank, searchBagForCloth,
		crossThePit, climbOverRockslide4, climbOverRockslide5, crossTheGrid, pullLeverAfterGrid, passTrap1,
		passTrap2, passTrap3, passTrap4, passTrap5, climbDownWell, pickCellLock, digMud, crossLedge, navigateMaze,
		goThroughPipe, leaveUnicornArea, openIbansDoor, goBackUpToIbansCavern, enterTemple, enterWell, leaveWellCave;

	DetailedQuestStep goFromCaveToLeaves, goFromLeavesToStickTrap, goUpToLeafTowardsLog, goCrossLogToCamp,
		crossLogFromCamp, goFromCampToLeavesSouth, goFromTyrasToTrap;

	DetailedQuestStep talkToIdris, talkToIorwerth, talkToTracker, talkToIorwerthAgain, talkToTrackerAgain,
		clickTracks, climbThroughForest, killGuard, crossTripwire, enterTyrasCamp, goKillGuardAtSecondForest,
		take2Barrels, fill2Barrels, getSulphur, readBigBookOfBangs;

	DetailedQuestStep useLimestoneOnFurnace, usePestleOnQuicklime, usePestleOnSulphur;

	DetailedQuestStep talkToChemist, useTarOnFractionalisingStill, operateStill, useQuicklimeOnNaphtha,
		useGroundSulphurOnNaphtha, useClothOnBarrelBomb;

	DetailedQuestStep useRabbitOnGuard, useBombOnCatapult, leaveFromCatapult, talkToArianwyn;

	//Zones
	Zone castleFloor2, westArdougne, beforeRockslide1, beforeRockslide2, beforeRockslide3, beforeBridge,
		northEastOfBridge, westOfBridge, beforeThePit, afterThePit, beforeTheGrid, atTheGrid, afterTheGrid,
		beforeTrap1, beforeTrap2, beforeTrap3, beforeTrap4, beforeTrap5, wellArea, beforePlank2, beforePlank3,
		atOrb1, insideCell, beforeLedge, afterMaze, afterMazeShortcut, inUnicornArea, inUnicornArea2, inKnightsArea1,
		inKnightsArea2, inKnightsArea3, beforeIbansDoor, inDwarfCavern, inFinalArea, inTemple, inFallArea,
		inUndergroundSection2P1, inUndergroundSection2P2, inUndergroundSection2P3, inUndergroundSection2P4,
		inUndergroundSection3, inMaze1, inMaze2, inPostIbanArea, wellEntrance, ibanRoom, elvenLands, forestNearCave,
		forestSectionAfterCave, forestSectionAfterCave2, westForestPath, westForestPath2, westForestPath3,
		southOfLog2, southOfLog, iorwerthCamp, iorwerthCamp2, guardArea, tyrasCampArea, tyrasCampArea2, tyrasCampArea3;

	ConditionalStep goThroughUndergroundPass, goTalkToIorwerth, goTalkToTracker, goReturnToIorwerth, goReturnToTracker,
		goClickTracks, goTalkToTrackerAfterTracks, goDefeatGuard, goToIorwerthAfterCamp, goIntoTyrasCamp,
		goLearnAboutBomb, goThroughUndergroundPassAgain, goGiveRabbitToGuard, goUseBombOnCatapult,
		goTalkToIorwerthAfterRegicide, goTalkToLathasToFinish;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		ConditionalStep beginWithLathas = new ConditionalStep(this, goToArdougneCastleFloor2);
		beginWithLathas.addStep(inCastleFloor2, talkToKingLathas);

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, beginWithLathas);
		steps.put(1, beginWithLathas);

		ConditionalStep goToElvenLands = new ConditionalStep(this, goThroughUndergroundPass);
		goToElvenLands.addStep(idrisNearby, talkToIdris);
		goToElvenLands.addStep(isInElvenLands, goTalkToIorwerth);
		goToElvenLands.addStep(inCastleFloor2, goDownCastleStairs);
		steps.put(2, goToElvenLands);
		steps.put(3, goToElvenLands);

		ConditionalStep goToScout = new ConditionalStep(this, goThroughUndergroundPass);
		goToScout.addStep(isInElvenLands, goTalkToTracker);
		steps.put(4, goToScout);

		ConditionalStep goShowScoutLoyalty = new ConditionalStep(this, goThroughUndergroundPass);
		goShowScoutLoyalty.addStep(new Conditions(isInElvenLands, crystalPendant), goReturnToTracker);
		goShowScoutLoyalty.addStep(isInElvenLands, goReturnToIorwerth);
		steps.put(5, goShowScoutLoyalty);

		ConditionalStep goFindTracks = new ConditionalStep(this, goThroughUndergroundPass);
		goFindTracks.addStep(isInElvenLands, goClickTracks);
		steps.put(6, goFindTracks);

		ConditionalStep goToTrackerAfterTracks = new ConditionalStep(this, goThroughUndergroundPass);
		goToTrackerAfterTracks.addStep(isInElvenLands, goTalkToTrackerAfterTracks);
		steps.put(7, goToTrackerAfterTracks);

		ConditionalStep goAndDefeatGuard = new ConditionalStep(this, goThroughUndergroundPass);
		goAndDefeatGuard.addStep(isInElvenLands, goDefeatGuard);
		steps.put(8, goAndDefeatGuard);

		ConditionalStep goFindTyrasCamp = new ConditionalStep(this, goThroughUndergroundPass);
		goFindTyrasCamp.addStep(isInElvenLands, goIntoTyrasCamp);
		steps.put(9, goFindTyrasCamp);

		ConditionalStep goAndReportToIorwerth = new ConditionalStep(this, goThroughUndergroundPass);
		goAndReportToIorwerth.addStep(isInElvenLands, goToIorwerthAfterCamp);
		steps.put(10, goAndReportToIorwerth);

		ConditionalStep goMakeQuicklime = new ConditionalStep(this, useLimestoneOnFurnace);
		goMakeQuicklime.addStep(quicklime, usePestleOnQuicklime);

		ConditionalStep goMakeBomb = new ConditionalStep(this, readBigBookOfBangs);
		goMakeBomb.addStep(new Conditions(barrelBombFused, isInElvenLands, givenRabbit), useBombOnCatapult);
		goMakeBomb.addStep(new Conditions(barrelBombFused, isInElvenLands), goGiveRabbitToGuard);
		goMakeBomb.addStep(new Conditions(barrelBombFused), goThroughUndergroundPassAgain);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, barrelBombUnfused), useClothOnBarrelBomb);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, naphthaMix), useGroundSulphurOnNaphtha);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, naphtha), useQuicklimeOnNaphtha);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, coalInStill), operateStill);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, hadGroundQuicklime, talkedToChemist), useTarOnFractionalisingStill);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, hadGroundQuicklime, hadGroundSulphur), talkToChemist);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb, hadGroundQuicklime), usePestleOnSulphur);
		goMakeBomb.addStep(new Conditions(knowHowToMakeBomb), goMakeQuicklime);
		goMakeBomb.addStep(new Conditions(hasReadBook), goLearnAboutBomb);
		steps.put(11, goMakeBomb);

		ConditionalStep goBackToIorwerth = new ConditionalStep(this, goThroughUndergroundPass);
		goBackToIorwerth.addStep(isInElvenLands, goTalkToIorwerthAfterRegicide);
		steps.put(12, goBackToIorwerth);

		steps.put(13, talkToArianwyn);

		steps.put(14, goTalkToLathasToFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		rope1 = new ItemRequirement("Rope", ItemID.ROPE);
		rope1.setTooltip("Bring extras as you can fail");
		rope2 = rope1.quantity(2);
		ropeHighlight = rope1.highlighted();
		bow = new ItemRequirement("Bow (not crossbow)", ItemCollections.BOWS, 1, true).isNotConsumed();
		arrows = new ItemRequirement("Arrows (metal, unpoisoned)", ItemCollections.METAL_ARROWS);
		arrowsHighlight = arrows.highlighted();
		spade = new ItemRequirement("Spade", ItemID.SPADE).isNotConsumed();
		spadeHighlight = spade.highlighted().isNotConsumed();
		plank = new ItemRequirement("Plank", ItemID.PLANK).isNotConsumed();
		plankHighlight = plank.highlighted();
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET).isNotConsumed();
		bucketHighlight = bucket.highlighted();
		bucketHighlight.setTooltip("You can grab a bucket from the southwest corner of the large dwarf encampment building.");
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		tinderboxHighlight = tinderbox.highlighted();
		combatEquipment = new ItemRequirement("Combat Equipment", -1, -1).isNotConsumed();
		combatEquipment.setDisplayItemId(BankSlotIcons.getCombatGear());
		food = new ItemRequirement("Food", -1, -1);
		food.setDisplayItemId(ItemID.SHARK);
		staminaPotions = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS);
		coins = new ItemRequirement("Coins (to buy food, 75 ea)", ItemCollections.COINS, 750);
		agilityPotions = new ItemRequirement("Agility boosting items like Summer Pie (+5) or Agility potion (+3)",
			ItemID.SUMMER_PIE, 5).hideConditioned(new SkillRequirement(Skill.AGILITY, 56));
		agilityPotions.addAlternates(ItemID.PART_SUMMER_PIE);
		agilityPotions.addAlternates(ItemCollections.AGILITY_POTIONS);
		oilyCloth = new ItemRequirement("Oily Cloth", ItemID.OILY_CLOTH);
		oilyCloth.setTooltip("You can get another by searching the equipment by the fireplace beside Koftik.");
		oilyClothHighlight = oilyCloth.highlighted();
		fireArrow = new ItemRequirement("Fire Arrow", ItemID.BRONZE_FIRE_ARROW);
		fireArrow.setHighlightInInventory(true);
		fireArrow.addAlternates(ItemID.IRON_FIRE_ARROW, ItemID.STEEL_FIRE_ARROW, ItemID.MITHRIL_FIRE_ARROW, ItemID.ADAMANT_FIRE_ARROW, ItemID.RUNE_FIRE_ARROW);
		litArrow = new ItemRequirement("Lit Arrow", ItemID.BRONZE_FIRE_ARROW_LIT);
		litArrow.setHighlightInInventory(true);
		litArrow.addAlternates(ItemID.IRON_FIRE_ARROW_LIT, ItemID.STEEL_FIRE_ARROW_LIT, ItemID.MITHRIL_FIRE_ARROW_LIT, ItemID.ADAMANT_FIRE_ARROW_LIT, ItemID.RUNE_FIRE_ARROW_LIT);
		litArrowEquipped = new ItemRequirement("Lit Arrow", ItemID.BRONZE_FIRE_ARROW_LIT, 1, true);
		litArrowEquipped.addAlternates(ItemID.IRON_FIRE_ARROW_LIT, ItemID.STEEL_FIRE_ARROW_LIT, ItemID.MITHRIL_FIRE_ARROW_LIT, ItemID.ADAMANT_FIRE_ARROW_LIT, ItemID.RUNE_FIRE_ARROW_LIT);

		coal20 = new ItemRequirement("Coal", ItemID.COAL, 20);
		limestone = new ItemRequirement("Limestone", ItemID.LIMESTONE);
		limestone.setTooltip("You can mine some in the mine north of the Digsite");
		stripOfCloth = new ItemRequirement("Strip of cloth", ItemID.STRIP_OF_CLOTH);
		stripOfCloth.setTooltip("Made on a loom with 4 balls of wool.");
		stripOfCloth.appendToTooltip("There is a loom available at the Falador Farm");
		pestle = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).isNotConsumed();

		gloves = new ItemRequirement("Gloves which fully cover your hand", ItemCollections.QUICKLIME_GLOVES).isNotConsumed();
		gloves.addAlternates(ItemCollections.GRACEFUL_GLOVES);
		gloves.setUrlSuffix("Quicklime#Gloves");
		gloves.setTooltip("'Go to wiki..' to see valid options for handling Quicklime");

		pot = new ItemRequirement("Pot", ItemID.POT);
		cookedRabbit = new ItemRequirement("Cooked rabbit", ItemID.COOKED_RABBIT);
		cookedRabbit.setTooltip("Raw Rabbit can be killed around Isafdar or purchased from the");
		cookedRabbit.appendToTooltip(" Charter Ship near the Tyras Camp for 50gp.");
		cookedRabbit.appendToTooltip(" You can cook it on a fire which spawns in the clearing east of Port Tyras.");

		antipoisons = new ItemRequirement("Antidotes/antipoisons", ItemCollections.ANTIPOISONS, -1);
		antipoisons.setTooltip("No amount specified. Bring as many doses as you feel comfortable bringing.");
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.FALADOR_TELEPORT);
		westArdougneTeleport = new ItemRequirement("West Ardougne teleport", ItemID.WEST_ARDOUGNE_TELEPORT, 4);
		summerPie = new ItemRequirement("Summer pie as food + agility boost", ItemID.SUMMER_PIE, -1);
		summerPie.addAlternates(ItemID.HALF_A_SUMMER_PIE);
		summerPie.setTooltip("This is, most likely, not needed if you have 70+ Agility. Bring more if you have lower Agility.");
		axe = new ItemRequirement("An Axe to cook raw rabbit in case you fail too many obstacles.", ItemCollections.AXES).isNotConsumed();

		crystalPendant = new ItemRequirement("Crystal pendant", ItemID.CRYSTAL_PENDANT);
		crystalPendant.setTooltip("You can get another from Lord Iorwerth");

		barrel2 = new ItemRequirement("Barrel", ItemID.BARREL_3216, 2);
		barrel2.addAlternates(ItemID.BARREL_OF_COAL_TAR, ItemID.BARREL_OF_NAPHTHA);
		coalBarrel2 = new ItemRequirement("Barrel of coal tar", ItemID.BARREL_OF_COAL_TAR, 2);
		coalBarrel2.addAlternates(ItemID.BARREL_OF_COAL_TAR);
		sulphur = new ItemRequirement("Sulphur", ItemID.SULPHUR);
		bigBookOfBangs = new ItemRequirement("Big book of bangs", ItemID.BIG_BOOK_OF_BANGS);
		bigBookOfBangs.setTooltip("You can get another from Lord Iorwerth");

		groundQuicklime = new ItemRequirement("Pot of quicklime", ItemID.POT_OF_QUICKLIME);
		quicklime = new ItemRequirement("Quicklime", ItemID.QUICKLIME);
		groundSulphur = new ItemRequirement("Ground sulphur", ItemID.GROUND_SULPHUR);
		naphtha = new ItemRequirement("Barrel of naphtha", ItemID.BARREL_OF_NAPHTHA);
		naphtha.addAlternates(ItemID.NAPHTHA_MIX);
		naphthaMix = new ItemRequirement("Naphtha mix", ItemID.NAPHTHA_MIX_3223);
		barrelBombFused = new ItemRequirement("Barrel bomb (fused)", ItemID.BARREL_BOMB_3219);
		barrelBombUnfused = new ItemRequirement("Barrel bomb (unfused)", ItemID.BARREL_BOMB);

		iorwerthsMessage = new ItemRequirement("Iorwerth's message", ItemID.IORWERTHS_MESSAGE);
		iorwerthsMessage.setTooltip("You can get another from Lord Iorwerth. You can return to the elven lands " +
			"through Arandar now");
	}

	private void setupZones()
	{
		castleFloor2 = new Zone(new WorldPoint(2568, 3283, 1), new WorldPoint(2591, 3310, 1));
		westArdougne = new Zone(new WorldPoint(2433, 3264, 0), new WorldPoint(2557, 3337, 2));

		// Section 1
		beforeRockslide1 = new Zone(new WorldPoint(2473, 9713, 0), new WorldPoint(2500, 9720, 0));
		beforeRockslide2 = new Zone(new WorldPoint(2471, 9705, 0), new WorldPoint(2481, 9712, 0));
		beforeRockslide3 = new Zone(new WorldPoint(2457, 9704, 0), new WorldPoint(2470, 9711, 0));
		beforeBridge = new Zone(new WorldPoint(2445, 9713, 0), new WorldPoint(2467, 9719, 0));
		northEastOfBridge = new Zone(new WorldPoint(2445, 9719, 0), new WorldPoint(2456, 9727, 0));
		inFallArea = new Zone(new WorldPoint(2440, 9628, 0), new WorldPoint(2486, 9657, 0));

		// Section 2
		inUndergroundSection2P1 = new Zone(new WorldPoint(2431, 9660, 0), new WorldPoint(2443, 9728, 0));
		inUndergroundSection2P2 = new Zone(new WorldPoint(2444, 9660, 0), new WorldPoint(2494, 9703, 0));
		inUndergroundSection2P3 = new Zone(new WorldPoint(2485, 9704, 0), new WorldPoint(2494, 9707, 0));
		inUndergroundSection2P4 = new Zone(new WorldPoint(2380, 9663, 0), new WorldPoint(2430, 9698, 0));
		inUndergroundSection3 = new Zone(new WorldPoint(2365, 9590, 0), new WorldPoint(2430, 9661, 0));
		westOfBridge = new Zone(new WorldPoint(2431, 9684, 0), new WorldPoint(2443, 9728, 0));
		beforeThePit = new Zone(new WorldPoint(2439, 9691, 0), new WorldPoint(2463, 9703, 0));
		afterThePit = new Zone(new WorldPoint(2463, 9691, 0), new WorldPoint(2493, 9709, 0));
		beforeTheGrid = new Zone(new WorldPoint(2482, 9668, 0), new WorldPoint(2494, 9690, 0));
		atTheGrid = new Zone(new WorldPoint(2467, 9672, 0), new WorldPoint(2482, 9684, 0));
		afterTheGrid = new Zone(new WorldPoint(2466, 9672, 0), new WorldPoint(2466, 9683, 0));
		beforeTrap1 = new Zone(new WorldPoint(2444, 9668, 0), new WorldPoint(2465, 9690, 0));
		beforeTrap2 = new Zone(new WorldPoint(2441, 9676, 0), new WorldPoint(2442, 9678, 0));
		beforeTrap3 = new Zone(new WorldPoint(2436, 9675, 0), new WorldPoint(2439, 9683, 0));
		beforeTrap4 = new Zone(new WorldPoint(2433, 9675, 0), new WorldPoint(2434, 9677, 0));
		beforeTrap5 = new Zone(new WorldPoint(2431, 9675, 0), new WorldPoint(2431, 9677, 0));
		wellArea = new Zone(new WorldPoint(2409, 9663, 0), new WorldPoint(2430, 9680, 0));
		beforePlank2 = new Zone(new WorldPoint(2416, 9682, 0), new WorldPoint(2420, 9684, 0));
		beforePlank3 = new Zone(new WorldPoint(2414, 9686, 0), new WorldPoint(2420, 9688, 0));
		atOrb1 = new Zone(new WorldPoint(2412, 9690, 0), new WorldPoint(2422, 9698, 0));

		insideCell = new Zone(new WorldPoint(2392, 9650, 0), new WorldPoint(2394, 9654, 0));
		beforeLedge = new Zone(new WorldPoint(2374, 9639, 0), new WorldPoint(2395, 9647, 0));
		inMaze1 = new Zone(new WorldPoint(2369, 9616, 0), new WorldPoint(2415, 9638, 0));
		inMaze2 = new Zone(new WorldPoint(2398, 9639, 0), new WorldPoint(2404, 9646, 0));
		afterMaze = new Zone(new WorldPoint(2416, 9597, 0), new WorldPoint(2432, 9645, 0));
		afterMazeShortcut = new Zone(new WorldPoint(2404, 9615, 0), new WorldPoint(2420, 9624, 0));
		inUnicornArea = new Zone(new WorldPoint(2391, 9586, 0), new WorldPoint(2415, 9612, 0));
		inUnicornArea2 = new Zone(new WorldPoint(2366, 9586, 0), new WorldPoint(2390, 9612, 0));
		inKnightsArea1 = new Zone(new WorldPoint(2369, 9666, 0), new WorldPoint(2379, 9728, 0));
		inKnightsArea2 = new Zone(new WorldPoint(2379, 9693, 0), new WorldPoint(2413, 9728, 0));
		inKnightsArea3 = new Zone(new WorldPoint(2414, 9705, 0), new WorldPoint(2429, 9728, 0));
		beforeIbansDoor = new Zone(new WorldPoint(2367, 9709, 0), new WorldPoint(2386, 9727, 0));

		inFinalArea = new Zone(new WorldPoint(2110, 4544, 1), new WorldPoint(2177, 4739, 1));
		inDwarfCavern = new Zone(new WorldPoint(2304, 9789, 0), new WorldPoint(2365, 9921, 0));

		inTemple = new Zone(new WorldPoint(2132, 4641, 1), new WorldPoint(2143, 4654, 1));
		inPostIbanArea = new Zone(new WorldPoint(2439, 9603, 0), new WorldPoint(2491, 9615, 0));

		wellEntrance = new Zone(new WorldPoint(2311, 9608, 0), new WorldPoint(2354, 9637, 0));
		ibanRoom = new Zone(new WorldPoint(1999, 4704, 1), new WorldPoint(2015, 4717, 1));
		elvenLands = new Zone(new WorldPoint(2113, 3076, 0), new WorldPoint(2390, 3334, 0));
		forestNearCave = new Zone(new WorldPoint(2265, 3203, 0), new WorldPoint(2318, 3237, 0));
		forestSectionAfterCave = new Zone(new WorldPoint(2236, 3173, 0), new WorldPoint(2275, 3202, 0));
		forestSectionAfterCave2 = new Zone(new WorldPoint(2233, 3203, 0), new WorldPoint(2263, 3272, 0));
		westForestPath = new Zone(new WorldPoint(2232, 3111, 0), new WorldPoint(2317, 3173, 0));
		westForestPath2 = new Zone(new WorldPoint(2221, 3174, 0), new WorldPoint(2235, 3190, 0));
		westForestPath3 = new Zone(new WorldPoint(2196, 3184, 0), new WorldPoint(2234, 3202, 0));
		southOfLog2 = new Zone(new WorldPoint(2201, 3203, 0), new WorldPoint(2225, 3238, 0));
		southOfLog = new Zone(new WorldPoint(2164, 3194, 0), new WorldPoint(2200, 3232, 0));

		iorwerthCamp = new Zone(new WorldPoint(2187, 3233, 0), new WorldPoint(2200, 3267, 0));
		iorwerthCamp2 = new Zone(new WorldPoint(2201, 3241, 0), new WorldPoint(2232, 3267, 0));
		guardArea = new Zone(new WorldPoint(2205, 3115, 0), new WorldPoint(2236, 3154, 0));
		tyrasCampArea = new Zone(new WorldPoint(2161, 3155, 0), new WorldPoint(2206, 3193, 0));
		tyrasCampArea2 = new Zone(new WorldPoint(2207, 3155, 0), new WorldPoint(2227, 3179, 0));
		tyrasCampArea3 = new Zone(new WorldPoint(2126, 3039, 0), new WorldPoint(2204 ,3154, 0));
	}

	private void setupConditions()
	{
		inCastleFloor2 = new ZoneRequirement(castleFloor2);
		inWestArdougne = new ZoneRequirement(westArdougne);
		isBeforeRockslide1 = new ZoneRequirement(beforeRockslide1);
		isBeforeRockslide2 = new ZoneRequirement(beforeRockslide2);
		isBeforeRockslide3 = new ZoneRequirement(beforeRockslide3);
		isInFallArea = new ZoneRequirement(inFallArea);
		isBeforeBridge = new ZoneRequirement(beforeBridge);
		isNorthEastOfBridge = new ZoneRequirement(northEastOfBridge);
		isBeforeThePit = new ZoneRequirement(westOfBridge, beforeThePit);
		isAfterThePit = new ZoneRequirement(afterThePit);
		isBeforeTheGrid = new ZoneRequirement(beforeTheGrid);
		isAtTheGrid = new ZoneRequirement(atTheGrid);
		isAfterTheGrid = new ZoneRequirement(afterTheGrid);
		isBeforeTrap1 = new ZoneRequirement(beforeTrap1);
		isBeforeTrap2 = new ZoneRequirement(beforeTrap2);
		isBeforeTrap3 = new ZoneRequirement(beforeTrap3);
		isBeforeTrap4 = new ZoneRequirement(beforeTrap4);
		isBeforeTrap5 = new ZoneRequirement(beforeTrap5);

		isInWellArea = new ZoneRequirement(wellArea);
		isInUndergroundSection2 = new ZoneRequirement(inUndergroundSection2P1, inUndergroundSection2P2, inUndergroundSection2P3, inUndergroundSection2P4);
		isInUndergroundSection3 = new ZoneRequirement(inUndergroundSection3);
		isAtOrb1 = new ZoneRequirement(atOrb1);
		isInsideCell = new ZoneRequirement(insideCell);
		isBeforeLedge = new ZoneRequirement(beforeLedge);
		isInMaze = new ZoneRequirement(inMaze1, inMaze2);
		isAfterMaze = new ZoneRequirement(afterMaze, afterMazeShortcut);
		isInUnicornArea = new ZoneRequirement(inUnicornArea);
		isInUnicornArea2 = new ZoneRequirement(inUnicornArea2);
		isInKnightsArea = new ZoneRequirement(inKnightsArea1, inKnightsArea2, inKnightsArea3);

		isBeforeIbansDoor = new ZoneRequirement(inKnightsArea3, beforeIbansDoor);
		isInFinalArea = new ZoneRequirement(inFinalArea);
		isInDwarfCavern = new ZoneRequirement(inDwarfCavern);

		isInTemple = new ZoneRequirement(inTemple);
		isInPostIbanArea = new ZoneRequirement(inPostIbanArea);

		isInWellEntrance = new ZoneRequirement(wellEntrance);
		isInIbanRoom = new ZoneRequirement(ibanRoom);

		isInElvenLands = new ZoneRequirement(elvenLands);
		inForestNearCave = new ZoneRequirement(forestNearCave);
		inForestSectionAfterCave = new ZoneRequirement(forestSectionAfterCave, forestSectionAfterCave2);
		inWestForestPath = new ZoneRequirement(westForestPath, westForestPath2, westForestPath3);
		inSouthOfLog = new ZoneRequirement(southOfLog, southOfLog2);
		inIorwerthCamp = new ZoneRequirement(iorwerthCamp, iorwerthCamp2);
		inGuardArea = new ZoneRequirement(guardArea);
		inTyrasCamp = new ZoneRequirement(tyrasCampArea, tyrasCampArea2, tyrasCampArea3);
		// Entered well first time, 8445, 0->1
		idrisNearby = new NpcInteractingRequirement(NpcID.IDRIS);
		guardNearby = new NpcInteractingRequirement(NpcID.TYRAS_GUARD, NpcID.TYRAS_GUARD_3433);
		// Guard appeared, 8446 0->1
		// Gone through dart trap, 8450 0->1
		hasReadBook = new VarbitRequirement(8453, 1);
		knowHowToMakeFuse = new VarbitRequirement(8455, 1);
		askedAboutBarrel = new VarbitRequirement(8456, 1);
		askedAboutSulphur = new VarbitRequirement(8457, 1);
		askedAboutQuicklime = new VarbitRequirement(8458, 1);
		askedAboutNaptha = new VarbitRequirement(8459, 1);
		knowHowToMakeBomb = new Conditions(hasReadBook, knowHowToMakeFuse, askedAboutBarrel, askedAboutQuicklime,
			askedAboutSulphur, askedAboutNaptha);
		hadGroundQuicklime = new Conditions(true, groundQuicklime);
		hadGroundSulphur = new Conditions(true, groundSulphur);
		talkedToChemist = new VarbitRequirement(8449, 1);
		coalInStill = new VarbitRequirement(8448, 1);
		givenRabbit = new VarbitRequirement(8447, 1);

		arianwynNearby = new NpcInteractingRequirement(NpcID.ARIANWYN);

		// Regular valve - Pressure Valve - Heat - Pressure
		// varp 331 = 603987969 starting with coal
		// 001 001 0000000000001 0000000000001
		// Pressure middle, 331 = ->536879105->671096833
		// 001 000 0000000000001 0000000000001
		// 001 010 0000000000001 0000000000001
		// PRessure right, 331 -> 536879105->805314561
		// 001 000 0000000000001 0000000000001
		// 001 100 0000000000001 0000000000001
		// Tar regulator middle, -> 268443649 -> 1342185473
		// 000 100 0000000000001 0000000000001
		// 010 100 0000000000001 0000000000001
		// Tar right, -> -1879039999
		// 100 100 0000000000001 0000000000001
		// Pressure building:
		// 010 001 0000000000001 0000000000010, 1140858882
		// Pressure entered green
		// 001 001 0000000000001 0000010000000, 603988096
		// Pressured at top of green
		// 001 001 0000000000001 0001000000000, 603988480
		// Left middle, right right, in green:
		// 100 010 0000000000001 0000100000000, -2013257472
		// Added a coal:
		// 001 001 0000000000010 0000000000001, 603996161
		// varp 330 0->26 represents distillation progress
	}

	public void setupSteps()
	{
		goToArdougneCastleFloor2 = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0), "Go to the second floor of Ardougne Castle.");
		talkToKingLathas = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Talk to King Lathas.");
		talkToKingLathas.addDialogSteps("I assume you have a plan?", "I can handle it.", "Yes.");
		talkToKingLathas.addSubSteps(goToArdougneCastleFloor2);

		goDownCastleStairs = new ObjectStep(this, ObjectID.STAIRCASE_15648, new WorldPoint(2572, 3296, 1), "Enter the Underground Pass.");
		enterWestArdougne = new ObjectStep(this, ObjectID.ARDOUGNE_WALL_DOOR_8739, new WorldPoint(2558, 3300, 0),
			"Enter the Underground Pass.");

		enterTheDungeon = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_3213, new WorldPoint(2434, 3315, 0),
			"Enter the Underground Pass.",	bow, arrows, rope1, spade);
		climbOverRockslide1 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2480, 9713, 0), "Climb-over rockslide.");
		climbOverRockslide2 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2471, 9706, 0), "Climb-over rockslide.");
		climbOverRockslide3 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2458, 9712, 0), "Climb-over rockslide.");
		talkToKoftikAtBridge = new NpcStep(this, NpcID.KOFTIK_8976, "Talk to Koftik inside the cave.");
		talkToKoftikAtBridge.addSubSteps(enterTheDungeon, climbOverRockslide1, climbOverRockslide2, climbOverRockslide3);

		searchBagForCloth = new ObjectStep(this, ObjectID.ABANDONED_EQUIPMENT, new WorldPoint(2452, 9715, 0), "Search the abandoned equipment for an oily cloth.");
		useClothOnArrow = new DetailedQuestStep(this, "Use the oily cloth on an arrow.", oilyClothHighlight, arrowsHighlight);
		lightArrow = new ObjectStep(this, ObjectID.FIRE_26185, new WorldPoint(2451, 9715, 0), "Light the fire arrow.",
			fireArrow);
		walkNorthEastOfBridge = new DetailedQuestStep(this, new WorldPoint(2447, 9722, 0), "Walk to the room north of Koftik.",
			litArrowEquipped);
		shootBridgeRope = new ObjectStep(this, ObjectID.GUIDE_ROPE, "Wield a lit arrow and shoot the guide-rope.", bow, litArrowEquipped);
		shootBridgeRope.addSubSteps(searchBagForCloth, useClothOnArrow, lightArrow, walkNorthEastOfBridge);

		collectPlank = new DetailedQuestStep(this, new WorldPoint(2435, 9726, 0), "Pick up the plank in the north room.", plank);
		crossThePit = new ObjectStep(this, ObjectID.ROCK_23125, new WorldPoint(2463, 9699, 0), "Swing across the pit " +
			"with a rope.", ropeHighlight);
		crossThePit.addIcon(ItemID.ROPE);
		crossThePit.addSubSteps(collectPlank);
		climbOverRockslide4 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2491, 9691, 0), "Climb-over rockslide");
		climbOverRockslide5 = new ObjectStep(this, ObjectID.ROCKSLIDE, new WorldPoint(2482, 9679, 0), "Climb-over rockslide");
		crossTheGrid = new DetailedQuestStep(this, "Cross the grid by trial and error. The correct path is the same " +
			"as it was during Underground Pass.");
		pullLeverAfterGrid = new ObjectStep(this, ObjectID.LEVER_3337, "Pull the lever to raise the gate.");
		crossTheGrid.addSubSteps(climbOverRockslide4, climbOverRockslide5, pullLeverAfterGrid);

		passTrap1 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2443, 9678, 0), "Either disable or walk past the traps.");
		passTrap2 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2440, 9678, 0), "Either disable or walk past the traps.");
		passTrap3 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2435, 9676, 0), "Either disable or walk past the traps.");
		passTrap4 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2432, 9676, 0), "Either disable or walk past the traps.");
		passTrap5 = new ObjectStep(this, ObjectID.ODD_MARKINGS, new WorldPoint(2430, 9676, 0), "Either disable or walk past the traps.");
		passTrap1.addSubSteps(passTrap2, passTrap3, passTrap4, passTrap5);

		climbDownWell = new ObjectStep(this, ObjectID.WELL_3264, new WorldPoint(2417, 9675, 0), "Climb down the well.");

		navigateMaze = new DetailedQuestStep(this, "Navigate the maze, or use the shortcut to the south with 50 Thieving.");
		pickCellLock = new ObjectStep(this, ObjectID.GATE_3266, new WorldPoint(2393, 9655, 0), "Pick the lock to enter the cell.");
		digMud = new ObjectStep(this, ObjectID.LOOSE_MUD, new WorldPoint(2393, 9650, 0), "Dig the loose mud.", spadeHighlight);
		digMud.addIcon(ItemID.SPADE);
		crossLedge = new ObjectStep(this, ObjectID.LEDGE_3238, new WorldPoint(2374, 9644, 0), "Cross the ledge.");
		goThroughPipe = new ObjectStep(this, ObjectID.PIPE_3237, new WorldPoint(2418, 9605, 0), "Enter the pipe to the next section.");
		navigateMaze.addSubSteps(pickCellLock, digMud, crossLedge, goThroughPipe);

		leaveUnicornArea = new ObjectStep(this, ObjectID.TUNNEL_3219, new WorldPoint(2375, 9611, 0), "Enter the tunnel.");
		openIbansDoor = new ObjectStep(this, ObjectID.DOOR_3221, new WorldPoint(2369, 9720, 0), "Open the door at the end of the path.");

		goBackUpToIbansCavern = new ObjectStep(this, ObjectID.CAVE_3223, new WorldPoint(2336, 9793, 0), "Return up to Iban's Cavern.");

		enterTemple = new ObjectStep(this, ObjectID.DOOR_3333, new WorldPoint(2144, 4648, 1),
			"Enter the central area from the north east corner of the area.");
		enterTemple.setLinePoints(Arrays.asList(
			new WorldPoint(2172, 4723, 1),
			new WorldPoint(2172, 4686, 1),
			new WorldPoint(2161, 4686, 1),
			new WorldPoint(2161, 4699, 1),
			new WorldPoint(2157, 4699, 1),
			new WorldPoint(2154, 4697, 1),
			new WorldPoint(2154, 4686, 1),
			new WorldPoint(2152, 4685, 1),
			new WorldPoint(2153, 4682, 1),
			new WorldPoint(2153, 4678, 1),
			new WorldPoint(2154, 4676, 1),
			new WorldPoint(2160, 4676, 1),
			new WorldPoint(2160, 4670, 1),
			new WorldPoint(2165, 4670, 1),
			new WorldPoint(2165, 4667, 1),
			new WorldPoint(2162, 4667, 1),
			new WorldPoint(2162, 4660, 1),
			new WorldPoint(2161, 4659, 1),
			new WorldPoint(2161, 4654, 1)
		));

		enterWell = new ObjectStep(this, ObjectID.WELL_4004, new WorldPoint(2009, 4712, 1),
			"Enter the well.");

		leaveWellCave = new ObjectStep(this, ObjectID.CAVE_EXIT_4007, new WorldPoint(2313, 9624, 0),
			"Leave the cave.");

		talkToIdris = new NpcStep(this, NpcID.IDRIS, "Talk to Idris.");
		talkToIorwerth = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"");

		goFromCaveToLeaves = new ObjectStep(this, ObjectID.LEAVES, new WorldPoint(2268, 3204, 0),
			"Follow the path along and hop over the leaves.");
		goFromCaveToLeaves.setLinePoints(Arrays.asList(
			new WorldPoint(2309, 3212, 0),
			new WorldPoint(2303, 3212, 0),
			new WorldPoint(2294, 3206, 0),
			new WorldPoint(2283, 3213, 0),
			new WorldPoint(2269, 3208, 0),
			new WorldPoint(2268, 3205, 0)
		));
		goFromLeavesToStickTrap = new ObjectStep(this, ObjectID.STICKS, new WorldPoint(2236, 3181, 0),
			"Head west, and cross the sticks there. To do so without failing, stand next to them and spam-click it " +
				"until your cross it.");
		goFromLeavesToStickTrap.setLinePoints(Arrays.asList(
			new WorldPoint(2267, 3201, 0),
			new WorldPoint(2258, 3182, 0),
			new WorldPoint(2238, 3181, 0)
		));

		goUpToLeafTowardsLog = new ObjectStep(this, ObjectID.LEAVES_3925, new WorldPoint(2209, 3202, 0),
			"Go north, and jump over the leaves.");
		goUpToLeafTowardsLog.setLinePoints(Arrays.asList(
			new WorldPoint(2234, 3181, 0),
			new WorldPoint(2224, 3180, 0),
			new WorldPoint(2209, 3201, 0)
		));

		goCrossLogToCamp = new ObjectStep(this, ObjectID.LOG_BALANCE_3931, new WorldPoint(2201, 3237, 0),
			"Cross the log to the north to the Iorwerth Camp.");

		crossLogFromCamp = new ObjectStep(this, ObjectID.LOG_BALANCE_3931, new WorldPoint(2197, 3237, 0),
			"Cross the log from the camp.");
		goFromCampToLeavesSouth = new ObjectStep(this, ObjectID.LEAVES_3925, new WorldPoint(2209, 3204, 0),
			"Go south, and jump over the leaves.");

		talkToTracker = new NpcStep(this, NpcID.ELF_TRACKER, new WorldPoint(2258, 3148, 0),
			"");
		talkToIorwerthAgain = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"");
		talkToTrackerAgain = new NpcStep(this, NpcID.ELF_TRACKER, new WorldPoint(2258, 3148, 0),
			"");
		clickTracks = new ObjectStep(this, NullObjectID.NULL_2004, new WorldPoint(2241, 3150, 0),
			"");
		climbThroughForest = new ObjectStep(this, ObjectID.DENSE_FOREST_3939, new WorldPoint(2239, 3149, 0),
			"");
		killGuard = new NpcStep(this, NpcID.TYRAS_GUARD, "Kill the guard.");
		crossTripwire = new ObjectStep(this, ObjectID.TRIPWIRE_3921, new WorldPoint(2220, 3154, 0),
			"Cross the tripwire to the north. It may poison you if you fail it.");
		goKillGuardAtSecondForest = new NpcStep(this, NpcID.TYRAS_GUARD_3433, new WorldPoint(2188, 3170, 0),
			"Go through the dense forest north then to the west, and fight the guard there.");
		goKillGuardAtSecondForest.setLinePoints(Arrays.asList(
			new WorldPoint(2217, 3158, 0),
			new WorldPoint(2217, 3172, 0),
			new WorldPoint(2203, 3180, 0),
			new WorldPoint(2188, 3180, 0),
			new WorldPoint(2188, 3172, 0)
		));

		List<WorldPoint> pathToTyrasCamp = Arrays.asList(
			new WorldPoint(2217, 3158, 0),
			new WorldPoint(2217, 3172, 0),
			new WorldPoint(2203, 3180, 0),
			new WorldPoint(2188, 3180, 0),
			new WorldPoint(2188, 3172, 0),
			new WorldPoint(2188, 3147, 0)
		);

		List<WorldPoint> pathFromTyrasCamp = new ArrayList<>(pathToTyrasCamp);
		Collections.reverse(pathFromTyrasCamp);

		enterTyrasCamp = new DetailedQuestStep(this, new WorldPoint(2190, 3144, 0),
			"Enter the Tyras Camp.");
		enterTyrasCamp.setLinePoints(pathToTyrasCamp);

		take2Barrels = new DetailedQuestStep(this, new WorldPoint(2190, 3144, 0),
			"Take 2 barrels from the Tyras Camp.", barrel2);
		take2Barrels.setLinePoints(pathToTyrasCamp);

		goFromTyrasToTrap = new ObjectStep(this, ObjectID.TRIPWIRE_3921, new WorldPoint(2220, 3154, 0),
			"Return back out the camp, to the east, then south to the traps and cross them.");
		goFromTyrasToTrap.setLinePoints(pathFromTyrasCamp);
		fill2Barrels = new ObjectStep(this, ObjectID.COAL_TAR, new WorldPoint(2263, 3127, 0),
			"Fill the barrels from the waste to the south.");
		getSulphur = new ObjectStep(this, ObjectID.SULPHUR_3963, new WorldPoint(2261, 3130, 0),
			"Pick up some sulphur.");

		readBigBookOfBangs = new DetailedQuestStep(this, "Read the big book of bangs.", bigBookOfBangs.highlighted());

		useLimestoneOnFurnace = new DetailedQuestStep(this, "Use some limestone on a furnace whilst wearing gloves.",
			limestone.highlighted(), gloves.equipped());
		usePestleOnQuicklime = new DetailedQuestStep(this, "Use a pestle and mortar on the quicklime with a pot in " +
			"your inventory.", pestle.highlighted(), quicklime.highlighted(), pot);
		usePestleOnSulphur = new DetailedQuestStep(this, "Use a pestle and mortar on some sulphur.",
			pestle.highlighted(),	sulphur.highlighted());

		talkToChemist = new NpcStep(this, NpcID.CHEMIST, new WorldPoint(2933, 3210, 0),
			"Talk to the Chemist in Rimmington.", coalBarrel2, coal20);
		talkToChemist.addDialogStep("Your quest.");
		useTarOnFractionalisingStill = new ObjectStep(this, ObjectID.FRACTIONALISING_STILL,
			new WorldPoint(2927, 3212, 0), "Use a barrel of coal on the still outside the Chemist's house.",
			coalBarrel2.quantity(1).highlighted());
		useTarOnFractionalisingStill.addIcon(ItemID.BARREL_OF_COAL_TAR);
		operateStill = new ObjectStep(this, ObjectID.FRACTIONALISING_STILL, new WorldPoint(2927, 3212, 0),
			"Operate the still. To properly distill the barrel: ");
		operateStill.addText("1. Turn the tar regulator valve all the way to the right.");
		operateStill.addText("2. Wait until the pressure is in the green, then turn the pressure valve to the middle.");
		operateStill.addText("3. SLOWLY add coal to keep the heat in the green section until the tar is distilled.");
		useQuicklimeOnNaphtha = new DetailedQuestStep(this, "Make another barrel of naphtha for Mourning's End if " +
			"you'd like, then add the quicklime to the naphtha.",
			groundQuicklime.highlighted(), naphtha.highlighted());
		useGroundSulphurOnNaphtha = new DetailedQuestStep(this, "Add the ground sulphur to the naphtha mix.",
			groundSulphur.highlighted(), naphthaMix.highlighted());
		useClothOnBarrelBomb = new DetailedQuestStep(this, "Use the cloth on the barrel bomb.",
			stripOfCloth.highlighted(), barrelBombUnfused.highlighted());

		useRabbitOnGuard = new NpcStep(this, NpcID.TYRAS_GUARD_8762, new WorldPoint(2183, 3185, 0),
			"", cookedRabbit.highlighted());
		useRabbitOnGuard.addIcon(ItemID.COOKED_RABBIT);
		useBombOnCatapult = new ObjectStep(this, ObjectID.CATAPULTLEVER, new WorldPoint(2184, 3185, 0),
			"Use the barrel bomb on the catapult.", barrelBombFused.highlighted());
		useBombOnCatapult.addIcon(ItemID.BARREL_BOMB_3219);

		List<WorldPoint> pathFromCatapult = Arrays.asList(
			new WorldPoint(2188, 3172, 0),
			new WorldPoint(2188, 3180, 0),
			new WorldPoint(2188, 3180, 0),
			new WorldPoint(2203, 3180, 0),
			new WorldPoint(2217, 3172, 0),
			new WorldPoint(2217, 3158, 0)
		);

		leaveFromCatapult = new ObjectStep(this, ObjectID.TRIPWIRE_3921, new WorldPoint(2220, 3154, 0),
			"Go to the east, then south to the traps and cross them.");
		leaveFromCatapult.setLinePoints(pathFromCatapult);

		talkToArianwyn = new NpcStep(this, NpcID.ARIANWYN, new WorldPoint(2582, 3298, 0),
			"Talk to Arianwyn outside Ardougne Castle.", iorwerthsMessage);

		// Go through dungeon
		ConditionalStep startDungeon = new ConditionalStep(this, enterWestArdougne);
		startDungeon.addStep(inCastleFloor2, goDownCastleStairs);
		startDungeon.addStep(inWestArdougne, enterTheDungeon);

		ConditionalStep crossTheBridge = new ConditionalStep(this, enterTheDungeon);
		crossTheBridge.addStep(isBeforeRockslide1, climbOverRockslide1);
		crossTheBridge.addStep(isBeforeRockslide2, climbOverRockslide2);
		crossTheBridge.addStep(isBeforeRockslide3, climbOverRockslide3);
		crossTheBridge.addStep(new Conditions(isNorthEastOfBridge, litArrowEquipped), shootBridgeRope);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), litArrow),
			walkNorthEastOfBridge);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), fireArrow), lightArrow);
		crossTheBridge.addStep(new Conditions(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), oilyCloth),
			useClothOnArrow);
		crossTheBridge.addStep(new Conditions(LogicType.OR, isBeforeBridge, isNorthEastOfBridge), searchBagForCloth);

		ConditionalStep theUndergroundPass = new ConditionalStep(this, climbDownWell);
		theUndergroundPass.addStep(new Conditions(LogicType.NOR, plank), collectPlank);
		theUndergroundPass.addStep(isBeforeThePit, crossThePit);
		theUndergroundPass.addStep(isAfterThePit, climbOverRockslide4);
		theUndergroundPass.addStep(isBeforeTheGrid, climbOverRockslide5);
		theUndergroundPass.addStep(isAtTheGrid, crossTheGrid);
		theUndergroundPass.addStep(isAfterTheGrid, pullLeverAfterGrid);
		theUndergroundPass.addStep(isBeforeTrap1, passTrap1);
		theUndergroundPass.addStep(isBeforeTrap2, passTrap2);
		theUndergroundPass.addStep(isBeforeTrap3, passTrap3);
		theUndergroundPass.addStep(isBeforeTrap4, passTrap4);
		theUndergroundPass.addStep(isBeforeTrap5, passTrap5);

		ConditionalStep travelThroughPassSection2 = new ConditionalStep(this, crossTheBridge);
		travelThroughPassSection2.addStep(isInUndergroundSection2, theUndergroundPass);

		ConditionalStep descendingDeeper = new ConditionalStep(this, pickCellLock);
		descendingDeeper.addStep(isAfterMaze, goThroughPipe);
		descendingDeeper.addStep(isInMaze, navigateMaze);
		descendingDeeper.addStep(isBeforeLedge, crossLedge);
		descendingDeeper.addStep(isInsideCell, digMud);

		ConditionalStep travelThroughPassSection3 = new ConditionalStep(this, crossTheBridge);
		travelThroughPassSection3.addStep(isInDwarfCavern, goBackUpToIbansCavern);
		travelThroughPassSection3.addStep(isInWellEntrance, leaveWellCave);
		travelThroughPassSection3.addStep(isInIbanRoom, enterWell);
		travelThroughPassSection3.addStep(isInFinalArea, enterTemple);
		travelThroughPassSection3.addStep(isInKnightsArea, openIbansDoor);
		travelThroughPassSection3.addStep(isInUnicornArea2, leaveUnicornArea);
		travelThroughPassSection3.addStep(isInUndergroundSection3, descendingDeeper);
		travelThroughPassSection3.addStep(isInUndergroundSection2, theUndergroundPass);

		goThroughUndergroundPass = new ConditionalStep(this, travelThroughPassSection3,
			"Traverse the Underground Pass.");

		ConditionalStep pathToIorwerth = new ConditionalStep(this, talkToIorwerth);
		pathToIorwerth.addStep(inSouthOfLog, goCrossLogToCamp);
		pathToIorwerth.addStep(inWestForestPath, goUpToLeafTowardsLog);
		pathToIorwerth.addStep(inGuardArea, climbThroughForest);
		pathToIorwerth.addStep(inTyrasCamp, goFromTyrasToTrap);
		pathToIorwerth.addStep(inForestSectionAfterCave, goFromLeavesToStickTrap);
		pathToIorwerth.addStep(inForestNearCave, goFromCaveToLeaves);

		goTalkToIorwerth = new ConditionalStep(this, pathToIorwerth, "WAIT OUTSIDE AROUND the cave entrance for Idris to appear " +
			"and talk to her (If Idris does not appear in the couple minutes enter and exit the cave). Afterwards, Go talk to Lord Iorwerth in the north west of the elven forest.");
		goTalkToIorwerth.addSubSteps(talkToIdris);

		goReturnToIorwerth = new ConditionalStep(this, pathToIorwerth, "Return to Lord Iorwerth in the north west of " +
			"the elven forest.");

		ConditionalStep pathToTracker = new ConditionalStep(this, talkToTracker);
		pathToTracker.addStep(inWestForestPath, talkToTracker);
		pathToTracker.addStep(inForestSectionAfterCave, goFromLeavesToStickTrap);
		pathToTracker.addStep(inForestNearCave, goFromCaveToLeaves);
		pathToTracker.addStep(inSouthOfLog, goFromCampToLeavesSouth);
		pathToTracker.addStep(inIorwerthCamp, crossLogFromCamp);

		goTalkToTracker = new ConditionalStep(this, pathToTracker,
			"Go talk to the Elven Tracker in the south of the forest.");
		goReturnToTracker = new ConditionalStep(this, pathToTracker,
			"Return to the Elven Tracker in the south of the forest.", crystalPendant);

		goClickTracks = new ConditionalStep(this, clickTracks, "Click the tracks near the Tracker.");
		goClickTracks.addStep(inWestForestPath, clickTracks);
		goClickTracks.addStep(inForestSectionAfterCave, goFromLeavesToStickTrap);
		goClickTracks.addStep(inForestNearCave, goFromCaveToLeaves);
		goClickTracks.addStep(inSouthOfLog, goFromCampToLeavesSouth);
		goClickTracks.addStep(inIorwerthCamp, crossLogFromCamp);

		goTalkToTrackerAfterTracks = new ConditionalStep(this, pathToTracker,
			"Talk to the Elven Tracker again.");

		ConditionalStep goToTyrasCampEntrance = new ConditionalStep(this, enterTyrasCamp);
		goToTyrasCampEntrance.addStep(inGuardArea, crossTripwire);
		goToTyrasCampEntrance.addStep(inWestForestPath, climbThroughForest);
		goToTyrasCampEntrance.addStep(inForestSectionAfterCave, goFromLeavesToStickTrap);
		goToTyrasCampEntrance.addStep(inForestNearCave, goFromCaveToLeaves);
		goToTyrasCampEntrance.addStep(inSouthOfLog, goFromCampToLeavesSouth);
		goToTyrasCampEntrance.addStep(inIorwerthCamp, crossLogFromCamp);

		goDefeatGuard = new ConditionalStep(this, goToTyrasCampEntrance,
			"Climb through the dense forest near the Tracker, and defeat the guard you find. This requires 56 " +
				"Agility.");
		goDefeatGuard.addStep(guardNearby, killGuard);
		goDefeatGuard.addStep(inTyrasCamp, goKillGuardAtSecondForest);

		goIntoTyrasCamp = new ConditionalStep(this, goToTyrasCampEntrance, "Enter the Tyras Camp.");

		goToIorwerthAfterCamp = new ConditionalStep(this, goToTyrasCampEntrance,
			"Fill some barrels with tar, pick up some sulphur there, and then return to Lord Iorwerth.");
		goToIorwerthAfterCamp.addStep(new Conditions(coalBarrel2, sulphur), pathToIorwerth);
		goToIorwerthAfterCamp.addStep(new Conditions(inWestForestPath, coalBarrel2), getSulphur);
		goToIorwerthAfterCamp.addStep(new Conditions(inWestForestPath, barrel2), fill2Barrels);
		goToIorwerthAfterCamp.addStep(new Conditions(inGuardArea, barrel2), climbThroughForest);
		goToIorwerthAfterCamp.addStep(new Conditions(inTyrasCamp, barrel2), goFromTyrasToTrap);
		goToIorwerthAfterCamp.addStep(inTyrasCamp, take2Barrels);


		NpcStep askAboutQuicklime, askAboutNaphtha, askAboutSulphur, askAboutBarrel, askAboutFuse;
		askAboutQuicklime = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"Ask Iorwerth about quicklime.");
		askAboutQuicklime.addDialogSteps("Previous options...", "I need some quicklime.");

		askAboutNaphtha = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"Ask Iorwerth about naphtha.");
		askAboutNaphtha.addDialogSteps("Previous options...", "I need some naphtha.");

		askAboutSulphur = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"Ask Iorwerth about sulphur.");
		askAboutSulphur.addDialogSteps("Previous options...", "I need some sulphur.");

		askAboutBarrel = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"Ask Iorwerth about a barrel.");
		askAboutBarrel.addDialogSteps("More options...", "I need a barrel.");

		askAboutFuse = new NpcStep(this, NpcID.LORD_IORWERTH, new WorldPoint(2203, 3255, 0),
			"Ask Iorwerth about a fuse.");
		askAboutFuse.addDialogSteps("More options...", "I need a fuse.");

		goLearnAboutBomb = new ConditionalStep(this, pathToIorwerth, "Ask Iorwerth about all the parts needed for" +
			" the barrel bomb.");
		goLearnAboutBomb.addStep(new Conditions(inIorwerthCamp, askedAboutQuicklime, askedAboutNaptha, askedAboutSulphur, askedAboutBarrel),
			askAboutFuse);
		goLearnAboutBomb.addStep(new Conditions(inIorwerthCamp, askedAboutQuicklime, askedAboutNaptha, askedAboutSulphur), askAboutBarrel);
		goLearnAboutBomb.addStep(new Conditions(inIorwerthCamp, askedAboutQuicklime, askedAboutNaptha), askAboutSulphur);
		goLearnAboutBomb.addStep(new Conditions(inIorwerthCamp, askedAboutQuicklime), askAboutNaphtha);
		goLearnAboutBomb.addStep(inIorwerthCamp, askAboutQuicklime);

		goThroughUndergroundPassAgain = new ConditionalStep(this, travelThroughPassSection3, "Traverse the " +
			"Underground Pass again.", cookedRabbit, barrelBombFused, tinderbox);

		goGiveRabbitToGuard = new ConditionalStep(this, goToTyrasCampEntrance,
			"Go give a cooked rabbit to the guard north of the Tyras Camp.");
		goGiveRabbitToGuard.addStep(inTyrasCamp, useRabbitOnGuard);
		goUseBombOnCatapult = new ConditionalStep(this, goToTyrasCampEntrance,
			"Use the barrel bomb on the catapult north of the Tyras Camp.");
		goTalkToIorwerthAfterRegicide = new ConditionalStep(this, pathToIorwerth,
			"Report back to Lord Iorwerth.");
		goTalkToIorwerthAfterRegicide.addStep(inTyrasCamp, leaveFromCatapult);

		goTalkToLathasToFinish = new ConditionalStep(this, goToArdougneCastleFloor2,
			"Report back to King Lathas to finish the quest.", iorwerthsMessage);
		goTalkToLathasToFinish.addStep(inCastleFloor2, talkToKingLathas);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(bow, arrows, rope2, spade, limestone, tinderbox, stripOfCloth, pestle, gloves, pot,
			coal20, cookedRabbit);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(staminaPotions, antipoisons, summerPie, plank, faladorTeleport, combatEquipment,
			westArdougneTeleport);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Tyras guard (level 110)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.UNDERGROUND_PASS, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 10));
		req.add(new SkillRequirement(Skill.AGILITY, 56, true));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.AGILITY, 13750));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("15,000 Coins", ItemID.COINS_995, 15000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Tirannwn & Arandar"),
				new UnlockReward("Ability to wield the Dragon Halberd"),
				new UnlockReward("Ability to charter a ship to Port Tyras."),
				new UnlockReward("Ability to use Iorwerth Camp teleport scrolls."),
				new UnlockReward("Ability to use Zul-Andra teleport scrolls and battle Zulrah."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting out", Collections.singletonList(talkToKingLathas)));

		allSteps.add(new PanelDetails("To the Elven Lands",
			Arrays.asList(goThroughUndergroundPass, goTalkToIorwerth, goTalkToTracker,
				goReturnToIorwerth, goReturnToTracker, goClickTracks, goTalkToTrackerAfterTracks, climbThroughForest,
				killGuard, goToIorwerthAfterCamp, readBigBookOfBangs, goLearnAboutBomb),
			Arrays.asList(bow, arrows, rope1, spade, antipoisons, combatEquipment, agilityPotions),
			Collections.singletonList(staminaPotions)));
		allSteps.add(new PanelDetails("Making a bomb", Arrays.asList(useLimestoneOnFurnace, usePestleOnQuicklime,
			usePestleOnSulphur, talkToChemist, useTarOnFractionalisingStill, operateStill, useQuicklimeOnNaphtha,
			useGroundSulphurOnNaphtha, useClothOnBarrelBomb),
			limestone, gloves, sulphur, pot, pestle, stripOfCloth, coalBarrel2, coal20));

		allSteps.add(new PanelDetails("Regicide", Arrays.asList(goThroughUndergroundPassAgain, goGiveRabbitToGuard,
			useBombOnCatapult, goTalkToIorwerthAfterRegicide, talkToArianwyn, goTalkToLathasToFinish),
			bow, arrows, rope1, tinderbox, spade, antipoisons, barrelBombFused, cookedRabbit, agilityPotions));
		return allSteps;
	}
}
