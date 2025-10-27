/*
 * Copyright (c) 2022, scatter <https://github.com/scatter-dev>
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
package com.questhelper.helpers.quests.landofthegoblins;

import com.google.common.collect.ImmutableList;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.npc.FollowerRequirement;
import com.questhelper.requirements.npc.NoFollowerRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.*;

import java.util.*;
import java.util.regex.Pattern;

public class LandOfTheGoblins extends BasicQuestHelper
{
	Requirement noPet;
	ItemRequirement lightSource, toadflaxPotionUnf, goblinMail, yellowDye, blueDye, orangeDye, purpleDye, blackDye, fishingRod, rawSlimyEel, coins, combatGear;
	ItemRequirement tinderbox, dorgeshKaanSphereRec, dramenStaff, skillsNecklace, combatBracelet, lumbridgeTeleport, draynorTeleport, explorersRing, salveAmulet;
	CombatLevelRequirement recommendedCombatLevel;
	ItemRequirement pharmakosBerryHighlight, toadflaxUnfHighlight, goblinPotion, goblinPotionHighlight, noEquippedItems,
		dorgeshKaanSphere, blackGoblinMail, huzamogaarbKey, hemensterWhitefish, pestleAndMortar, vial,
		blackMushroom, whiteGoblinMail, yellowGoblinMail, blueGoblinMail, orangeGoblinMail, purpleGoblinMail,
		saragorgakKey, yurkolgokhKey, ekeleshuunKey, nargoshuunKey, horogothgarKey, anyGoblinMail;
	Zone basement, tunnels, mines, cityF0, cityF1, dorgeshKaan, goblinCave, guardArea, goblinTemple, northEastRoomSouth,
		northEastRoomNorth, hemenster, whitePriestRoom, yellowPriestRoom, bluePriestRoom, orangePriestRoom, purplePriestRoom,
		crypt, dorgeshKaanF1, dorgeshKaanCaveUpper, dorgeshKaanCaveLower;
	Requirement inMines, inTunnels, inBasement, inDorgeshKaanF0, inDorgeshKaanWithGrubfoot, invSpaceToUnequip, inGoblinCave, inGoblinCaveWithZanik,
		inGoblinCaveWithGoblinPotion, inFrontOfGuardsWithGoblinPotion, goblinSelectionActive, isAGoblin,
		hasBlackMushroomsOrDye, inGoblinTemple, blackGoblinMailEquipped, inNorthEastRoom, blackDyeOrBlackGoblinMail,
		knowsAboutWhitefish, inHemenster, inWhitePriestRoom, inYellowPriestRoom, inBluePriestRoom, inOrangePriestRoom, inPurplePriestRoom,
		hasAllGoblinKeys, inCrypt, snotheadAlive, snailfeetAlive, mosschinAlive, redeyesAlive, strongbonesAlive,
		snotheadDead, snailfeetDead, mosschinDead, redeyesDead, strongbonesDead, inDorgeshKaanF1, inDorgeshKaanCaveUpper,
		inDorgeshKaanCaveLower, learnedAboutMachine, firstGreater, firstLess, secondGreater, secondLess, thirdGreater, thirdLess, fairyRingMachineWidgetPresent,
		fairyRingMachineFixed, inYubiusk, unlockedDoor;
	Requirement grubfootFollowing;
	ConditionalStep goTalkToGrubfoot, goTalkToZanik, goReturnToDorg, returnToTempleWithDyes;
	QuestStep goDownIntoBasement, climbThroughHole, talkToKazgar;
	QuestStep talkToGrubfoot, enterDorgeshKaan, talkToZanik, enterGoblinCave, talkToZanikGoblinCave, talkToGuard,
		talkToMakeoverMage, pickPharmakosBerry, mixGoblinPotion, goBackToGoblinCave, goToGuards, drinkGoblinPotion,
		makeBlackDye, confirmGoblin, pickBlackMushrooms, talkToGuardAsGoblin, getGoblinMail, dyeGoblinMail, enterTempleDoorForThieving,
		enterNorthEastRoom, searchCrateForSphere, talkToZanikInCell, leaveNorthEastRoom, talkToPriestInTemple, enterNorthEastRoomForKey,
		goBackToGoblinCaveNoDye, goToGuardsNoDye, drinkGoblinPotionNoDye, talkToGuardAsGoblinNoDye, pickpocketPriest,
		talkToAggie, goToHemenster, catchWhitefish, talkToAggieWithFish, goToTempleWithDyes, enterGoblinCaveForKilling,
		passWhiteGuard, passYellowGuard, passBlueGuard, passOrangeGuard, passPurpleGuard,
		pickpocketWhitePriest, pickpocketYellowPriest, pickpocketBluePriest, pickpocketOrangePriest, pickpocketPurplePriest,
		dyeGoblinMailBlue, dyeGoblinMailYellow, dyeGoblinMailOrange, dyeGoblinMailPurple, unlockCrypt, enterCrypt,
		sayNameSnothead, sayNameSnailfeet, sayNameMosschin, sayNameRedeyes, sayNameStrongbones,
		defeatSnothead, defeatSnailfeet, defeatMosschin, defeatRedeyes, defeatStrongbones,
		learnSnailfeet, learnMosschin, learnRedeyes, learnStrongbones, learnYubiusk,
		talkToOldak, returnToDorgeshKaanOrFairyRing, climbDorgeshKaanStairsF0, climbDorgeshKaanStairsF1,
		climbLadderTop, talkToOldakAtMachine, inspectMachine, increaseFirst, decreaseFirst, increaseSecond, decreaseSecond, increaseThird, decreaseThird,
		confirmFixMachine, watchYubiuskCutscene, goToYubiusk, openBox;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, goTalkToGrubfoot);

		steps.put(2, goTalkToZanik);

		DetailedQuestStep watchTheCutscene = new DetailedQuestStep(this, "Watch the cutscene.");
		goTalkToZanik.addSubSteps(watchTheCutscene);

		steps.put(4, watchTheCutscene);

		steps.put(6, goTalkToZanik);

		steps.put(8, goTalkToZanik);

		ConditionalStep enterSecretTemple = new ConditionalStep(this, enterGoblinCave);
		enterSecretTemple.addStep(inGoblinCaveWithZanik, talkToGuard);
		enterSecretTemple.addStep(inGoblinCave, talkToZanikGoblinCave);
		steps.put(10, enterSecretTemple);

		steps.put(12, talkToMakeoverMage);
		steps.put(14, talkToMakeoverMage);

		ConditionalStep becomeGoblin = new ConditionalStep(this, pickPharmakosBerry);
		becomeGoblin.addStep(goblinSelectionActive, confirmGoblin);
		becomeGoblin.addStep(new Conditions(inFrontOfGuardsWithGoblinPotion, blackDyeOrBlackGoblinMail), drinkGoblinPotion);
		becomeGoblin.addStep(new Conditions(inFrontOfGuardsWithGoblinPotion, blackMushroom), makeBlackDye);
		becomeGoblin.addStep(inFrontOfGuardsWithGoblinPotion, pickBlackMushrooms);
		becomeGoblin.addStep(inGoblinCaveWithGoblinPotion, goToGuards);
		becomeGoblin.addStep(goblinPotion.alsoCheckBank(), goBackToGoblinCave);
		becomeGoblin.addStep(pharmakosBerryHighlight, mixGoblinPotion);

		steps.put(16, becomeGoblin);

		ConditionalStep enterTemple = new ConditionalStep(this, goBackToGoblinCave);
		enterTemple.addStep(new Conditions(isAGoblin, blackDyeOrBlackGoblinMail, new Conditions(LogicType.NOR, inGoblinTemple)), talkToGuardAsGoblin);
		enterTemple.addStep(goblinSelectionActive, confirmGoblin);
		enterTemple.addStep(new Conditions(inFrontOfGuardsWithGoblinPotion, blackDyeOrBlackGoblinMail), drinkGoblinPotion);
		enterTemple.addStep(new Conditions(blackDyeOrBlackGoblinMail, new Conditions(LogicType.NOR, isAGoblin)), goToGuards);
		enterTemple.addStep(inGoblinCaveWithGoblinPotion, pickBlackMushrooms);

		steps.put(18, enterTemple);
		steps.put(20, enterTemple);

		ConditionalStep freeZanik = new ConditionalStep(this, enterTemple);
		freeZanik.addStep(new Conditions(LogicType.AND, dorgeshKaanSphere, inNorthEastRoom), talkToZanikInCell);
		freeZanik.addStep(inNorthEastRoom, searchCrateForSphere);
		freeZanik.addStep(blackGoblinMail, enterNorthEastRoom);
		freeZanik.addStep(new Conditions(LogicType.AND, inGoblinTemple, goblinMail), dyeGoblinMail);
		freeZanik.addStep(inGoblinTemple, getGoblinMail);

		steps.put(22, freeZanik);
		steps.put(24, freeZanik);
		steps.put(26, freeZanik);

		ConditionalStep talkToPriest = new ConditionalStep(this, goBackToGoblinCaveNoDye);
		talkToPriest.addStep(inNorthEastRoom, leaveNorthEastRoom);
		talkToPriest.addStep(inGoblinTemple, talkToPriestInTemple);
		talkToPriest.addStep(isAGoblin, talkToGuardAsGoblinNoDye);
		talkToPriest.addStep(goblinSelectionActive, confirmGoblin);
		talkToPriest.addStep(inFrontOfGuardsWithGoblinPotion, drinkGoblinPotionNoDye);
		talkToPriest.addStep(inGoblinCaveWithGoblinPotion, goToGuardsNoDye);

		steps.put(28, talkToPriest);
		steps.put(30, talkToPriest);
		steps.put(32, talkToPriest);

		returnToTempleWithDyes = new ConditionalStep(this, goToTempleWithDyes, "Return to the goblin temple with all the dyes and some combat gear.");
		returnToTempleWithDyes.addStep(isAGoblin, enterTempleDoorForThieving);
		returnToTempleWithDyes.addStep(goblinSelectionActive, confirmGoblin);
		returnToTempleWithDyes.addStep(inFrontOfGuardsWithGoblinPotion, drinkGoblinPotion);
		returnToTempleWithDyes.addStep(inGoblinCaveWithGoblinPotion, goToGuards);
		returnToTempleWithDyes.setShouldConsiderSubStepsForSidebarHighlight(false);

		ConditionalStep dyeing = new ConditionalStep(this, goToHemenster);
		dyeing.addStep(hasAllGoblinKeys, unlockCrypt);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, inWhitePriestRoom, saragorgakKey)), passWhiteGuard);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, saragorgakKey)), pickpocketWhitePriest);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, yellowGoblinMail, yurkolgokhKey)), dyeGoblinMailYellow);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, inYellowPriestRoom, yurkolgokhKey)), passYellowGuard);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, yurkolgokhKey)), pickpocketYellowPriest);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, blueGoblinMail, ekeleshuunKey)), dyeGoblinMailBlue);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, inBluePriestRoom, ekeleshuunKey)), passBlueGuard);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, ekeleshuunKey)), pickpocketBluePriest);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, orangeGoblinMail, nargoshuunKey)), dyeGoblinMailOrange);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, inOrangePriestRoom, nargoshuunKey)), passOrangeGuard);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, nargoshuunKey)), pickpocketOrangePriest);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, purpleGoblinMail, horogothgarKey)), dyeGoblinMailPurple);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, inPurplePriestRoom, horogothgarKey)), passPurpleGuard);
		dyeing.addStep(new Conditions(inGoblinTemple, new Conditions(LogicType.NOR, horogothgarKey)), pickpocketPurplePriest);
		dyeing.addStep(whiteGoblinMail, returnToTempleWithDyes);
		dyeing.addStep(hemensterWhitefish, talkToAggieWithFish);
		dyeing.addStep(inHemenster, catchWhitefish);

		ConditionalStep getKeys = new ConditionalStep(this, enterTemple.copy());
		getKeys.addStep(knowsAboutWhitefish, dyeing);
		getKeys.addStep(huzamogaarbKey, talkToAggie);
		getKeys.addStep(inNorthEastRoom, pickpocketPriest);
		getKeys.addStep(inGoblinTemple, enterNorthEastRoomForKey);

		steps.put(34, getKeys);

		ConditionalStep goEnterCrypt = new ConditionalStep(this, enterGoblinCaveForKilling, "Return to the goblin temple with some combat gear.");
		goEnterCrypt.addStep(inGoblinTemple, enterCrypt);
		goEnterCrypt.addStep(isAGoblin, enterTempleDoorForThieving);
		goEnterCrypt.addStep(goblinSelectionActive, confirmGoblin);
		goEnterCrypt.addStep(new ZoneRequirement(guardArea), drinkGoblinPotion);
		goEnterCrypt.addStep(new Conditions(new Conditions(LogicType.NOR, isAGoblin), inGoblinCave), goToGuards);
		goEnterCrypt.setShouldConsiderSubStepsForSidebarHighlight(false);
		enterCrypt.addSubSteps(goEnterCrypt);

		ConditionalStep snothead = new ConditionalStep(this, goEnterCrypt);
		snothead.addStep(snotheadDead, learnSnailfeet);
		snothead.addStep(snotheadAlive, defeatSnothead);
		snothead.addStep(inCrypt, sayNameSnothead);

		steps.put(36, snothead);
		steps.put(38, snothead);

		ConditionalStep snailfeet = new ConditionalStep(this, goEnterCrypt);
		snailfeet.addStep(snailfeetDead, learnMosschin);
		snailfeet.addStep(snailfeetAlive, defeatSnailfeet);
		snailfeet.addStep(inCrypt, sayNameSnailfeet);

		steps.put(40, snailfeet);

		ConditionalStep mosschin = new ConditionalStep(this, goEnterCrypt);
		mosschin.addStep(mosschinDead, learnRedeyes);
		mosschin.addStep(mosschinAlive, defeatMosschin);
		mosschin.addStep(inCrypt, sayNameMosschin);

		steps.put(42, mosschin);

		ConditionalStep redeyes = new ConditionalStep(this, goEnterCrypt);
		redeyes.addStep(redeyesDead, learnStrongbones);
		redeyes.addStep(redeyesAlive, defeatRedeyes);
		redeyes.addStep(inCrypt, sayNameRedeyes);

		steps.put(44, redeyes);

		ConditionalStep strongbones = new ConditionalStep(this, goEnterCrypt);
		strongbones.addStep(strongbonesDead, learnYubiusk);
		strongbones.addStep(strongbonesAlive, defeatStrongbones);
		strongbones.addStep(inCrypt, sayNameStrongbones);

		steps.put(46, strongbones);

		ConditionalStep oldakInDorgeshKaan = new ConditionalStep(this, goReturnToDorg);
		oldakInDorgeshKaan.addStep(new ZoneRequirement(dorgeshKaan), talkToOldak);

		steps.put(48, oldakInDorgeshKaan);

		ConditionalStep fixMachine = new ConditionalStep(this, returnToDorgeshKaanOrFairyRing);
		fixMachine.addStep(fairyRingMachineFixed, watchYubiuskCutscene);
		fixMachine.addStep(new Conditions(fairyRingMachineWidgetPresent, firstGreater), decreaseFirst);
		fixMachine.addStep(new Conditions(fairyRingMachineWidgetPresent, firstLess), increaseFirst);
		fixMachine.addStep(new Conditions(fairyRingMachineWidgetPresent, secondGreater), decreaseSecond);
		fixMachine.addStep(new Conditions(fairyRingMachineWidgetPresent, secondLess), increaseSecond);
		fixMachine.addStep(new Conditions(fairyRingMachineWidgetPresent, thirdGreater), decreaseThird);
		fixMachine.addStep(new Conditions(fairyRingMachineWidgetPresent, thirdLess), increaseThird);
		fixMachine.addStep(fairyRingMachineWidgetPresent, confirmFixMachine);
		fixMachine.addStep(inDorgeshKaanF0, climbDorgeshKaanStairsF0);
		fixMachine.addStep(inDorgeshKaanF1, climbDorgeshKaanStairsF1);
		fixMachine.addStep(inDorgeshKaanCaveUpper, climbLadderTop);
		fixMachine.addStep(new Conditions(inDorgeshKaanCaveLower, new Conditions(LogicType.NOR, learnedAboutMachine)), talkToOldakAtMachine);
		fixMachine.addStep(inDorgeshKaanCaveLower, inspectMachine);

		steps.put(50, fixMachine);

		ConditionalStep finishQuest = new ConditionalStep(this, returnToDorgeshKaanOrFairyRing);
		finishQuest.addStep(inDorgeshKaanF0, climbDorgeshKaanStairsF0);
		finishQuest.addStep(inDorgeshKaanF1, climbDorgeshKaanStairsF1);
		finishQuest.addStep(inDorgeshKaanCaveUpper, climbLadderTop);
		finishQuest.addStep(inDorgeshKaanCaveLower, goToYubiusk);
		finishQuest.addStep(inYubiusk, openBox);

		steps.put(52, finishQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		basement = new Zone(new WorldPoint(3208, 9614, 0), new WorldPoint(3219, 9625, 0));
		tunnels = new Zone(new WorldPoint(3221, 9602, 0), new WorldPoint(3308, 9661, 0));
		mines = new Zone(new WorldPoint(3309, 9600, 0), new WorldPoint(3327, 9655, 0));
		cityF0 = new Zone(new WorldPoint(2688, 5248, 0), new WorldPoint(2750, 5375, 0));
		cityF1 = new Zone(new WorldPoint(2688, 5248, 1), new WorldPoint(2750, 5375, 1));

		dorgeshKaan = new Zone(10835, 0);
		goblinCave = new Zone(10393);
		guardArea = new Zone(new WorldPoint(2574, 9840, 0), new WorldPoint(2590, 9856, 0));
		goblinTemple = new Zone(14915);
		// irregularly shaped room so split in half
		northEastRoomSouth = new Zone(new WorldPoint(3754, 4329, 0), new WorldPoint(3757, 4337, 0));
		northEastRoomNorth = new Zone(new WorldPoint(3757, 4337, 0), new WorldPoint(3742, 4352, 0));
		hemenster = new Zone(new WorldPoint(2642, 3445, 0), new WorldPoint(2631, 3435, 0));

		whitePriestRoom = new Zone(new WorldPoint(3731, 4323, 0), new WorldPoint(3723, 4312, 0));
		yellowPriestRoom = new Zone(new WorldPoint(3721, 4328, 0), new WorldPoint(3735, 4333, 0));
		bluePriestRoom = new Zone(new WorldPoint(3757, 4327, 0), new WorldPoint(3764, 4313, 0));
		orangePriestRoom = new Zone(new WorldPoint(3766, 4310, 0), new WorldPoint(3753, 4306, 0));
		purplePriestRoom = new Zone(new WorldPoint(3735, 4298, 0), new WorldPoint(3728, 4311, 0));

		crypt = new Zone(14916);

		dorgeshKaanF1 = new Zone(new WorldPoint(2688, 5248, 1), new WorldPoint(2750, 5375, 1));
		dorgeshKaanCaveUpper = new Zone(new WorldPoint(2680, 5200, 3), new WorldPoint(2751,  5252, 3));
		dorgeshKaanCaveLower = new Zone(10833, 0);

	}

	@Override
	protected void setupRequirements()
	{
		inBasement = new ZoneRequirement(basement);
		inTunnels = new ZoneRequirement(tunnels);
		inMines = new ZoneRequirement(mines);
		inDorgeshKaanF0 = new ZoneRequirement(cityF0);
		grubfootFollowing = new Conditions(LogicType.OR, new FollowerRequirement("Grubfoot", NpcID.LOTG_GRUBFOOT_FOLLOWER),
			new VarbitRequirement(VarbitID.LOTG, 8, Operation.GREATER_EQUAL));
		inDorgeshKaanWithGrubfoot = new Conditions(LogicType.AND, inDorgeshKaanF0, grubfootFollowing);
		inGoblinCave = new ZoneRequirement(goblinCave);
		FollowerRequirement zanikFollowing = new FollowerRequirement("Zanik", NpcID.LOTG_ZANIK_FOLLOWER);
		inGoblinCaveWithZanik = new Conditions(LogicType.AND, inGoblinCave, zanikFollowing);
		unlockedDoor = new VarbitRequirement(QuestHelperQuest.LAND_OF_THE_GOBLINS.getId(), 36, Operation.GREATER_EQUAL);

		saragorgakKey = new ItemRequirement("Saragorgak key", ItemID.LOTG_KEY_WHITE).alsoCheckBank();
		yurkolgokhKey = new ItemRequirement("Yurkolgokh key", ItemID.LOTG_KEY_YELLOW).alsoCheckBank();
		ekeleshuunKey = new ItemRequirement("Ekeleshuun key", ItemID.LOTG_KEY_BLUE).alsoCheckBank();
		nargoshuunKey = new ItemRequirement("Narogoshuun key", ItemID.LOTG_KEY_ORANGE).alsoCheckBank();
		horogothgarKey = new ItemRequirement("Horogothgar key", ItemID.LOTG_KEY_PURPLE).alsoCheckBank();

		huzamogaarbKey = new ItemRequirement("Huzamogaarb key", ItemID.LOTG_KEY_BLACK).alsoCheckBank();

		// ItemRequirements
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES);
		toadflaxPotionUnf = new ItemRequirement("Toadflax potion (unf)", ItemID.TOADFLAXVIAL);
		goblinMail = new ItemRequirement("Goblin mail", ItemID.GOBLIN_ARMOUR).hideConditioned(unlockedDoor);
		goblinMail.canBeObtainedDuringQuest();
		yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOWDYE).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, yurkolgokhKey));
		blueDye = new ItemRequirement("Blue dye", ItemID.BLUEDYE).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, ekeleshuunKey));
		orangeDye = new ItemRequirement("Orange dye", ItemID.ORANGEDYE).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, nargoshuunKey));
		purpleDye = new ItemRequirement("Purple dye", ItemID.PURPLEDYE).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, horogothgarKey));
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, huzamogaarbKey));
		vial = new ItemRequirement("Vial", ItemID.VIAL_EMPTY).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, huzamogaarbKey));
		blackMushroom = new ItemRequirement("Black mushroom", ItemID.GOLEM_MUSHROOM).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, huzamogaarbKey));
		blackDye = new ItemRequirement("Black dye", ItemID.GOLEM_INK).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, huzamogaarbKey));
		blackDye.setTooltip("Black mushrooms obtainable during quest - bring empty vial and pestle and mortar");
		fishingRod = new ItemRequirement("Fishing rod", ItemID.FISHING_ROD).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, saragorgakKey));
		rawSlimyEel = new ItemRequirement("Raw slimy eel", ItemID.MORT_SLIMEY_EEL).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, saragorgakKey));
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 5).hideConditioned(new Conditions(LogicType.OR, unlockedDoor, saragorgakKey));
		noPet = new NoFollowerRequirement("No pet following you");
		combatGear = new ItemRequirement("Combat gear", -1, -1);

		invSpaceToUnequip = new ItemRequirement("Inventory space to unequip all your items", -1, -1);
		noEquippedItems = new NoItemRequirement("No equipped items", ItemSlots.ANY_EQUIPPED);

		recommendedCombatLevel = new CombatLevelRequirement(65);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		tinderbox.setTooltip("If using an extinguishable light source");
		dorgeshKaanSphereRec = new ItemRequirement("Dorgesh-Kaan sphere", ItemID.DORGESH_TELEPORT_ARTIFACT, 2);
		dramenStaff = new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF);
		dramenStaff.addAlternates(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		dramenStaff.setTooltip("For transportation via fairy rings");
		skillsNecklace = new ItemRequirement("Skills necklace", ItemCollections.SKILLS_NECKLACES, 3);
		skillsNecklace.setChargedItem(true);
		combatBracelet = new ItemRequirement("Combat bracelet", ItemCollections.COMBAT_BRACELETS);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", -1, 2);
		draynorTeleport = new ItemRequirement("Draynor Village teleport", ItemCollections.AMULET_OF_GLORIES, 2);
		draynorTeleport.setChargedItem(true);
		explorersRing = new ItemRequirement("Explorer's ring 3 or 4", Arrays.asList(ItemID.LUMBRIDGE_RING_HARD, ItemID.LUMBRIDGE_RING_ELITE));
		salveAmulet = new ItemRequirement("Salve amulet or Salve amulet (e)", ItemCollections.SALVE_AMULET);

		pharmakosBerryHighlight = new ItemRequirement("Pharmakos berries", ItemID.LOTG_PHARMAKOS_BERRY);
		pharmakosBerryHighlight.setHighlightInInventory(true);
		toadflaxUnfHighlight = new ItemRequirement("Toadflax potion (unf)", ItemID.TOADFLAXVIAL);
		toadflaxUnfHighlight.setHighlightInInventory(true);
		goblinPotion = new ItemRequirement("Goblin potion", Arrays.asList(ItemID.LOTG_1DOSEGOBLIN, ItemID.LOTG_2DOSEGOBLIN, ItemID.LOTG_3DOSEGOBLIN, ItemID.LOTG_4DOSEGOBLIN));
		goblinPotion.setTooltip("You can make another with a toadflax potion (unf) and some pharmakos berries from the bush outside the Makeover Mage's house");
		inGoblinCaveWithGoblinPotion = new Conditions(LogicType.AND, inGoblinCave, goblinPotion);
		inFrontOfGuardsWithGoblinPotion = new Conditions(LogicType.AND, new ZoneRequirement(guardArea), goblinPotion);
		goblinPotionHighlight = new ItemRequirement("Goblin potion", Arrays.asList(ItemID.LOTG_1DOSEGOBLIN, ItemID.LOTG_2DOSEGOBLIN, ItemID.LOTG_3DOSEGOBLIN, ItemID.LOTG_4DOSEGOBLIN));
		goblinPotionHighlight.setHighlightInInventory(true);
		goblinSelectionActive = new WidgetPresenceRequirement(InterfaceID.LotgMakeover.CONFIRM);
		hasBlackMushroomsOrDye = new Conditions(LogicType.OR, blackMushroom, blackDye);
		isAGoblin = new VarbitRequirement(VarbitID.LOTG_PLAYER_IS_A_GOBLIN, 1);

		inGoblinTemple = new ZoneRequirement(goblinTemple);
		blackGoblinMail = new ItemRequirement("Black goblin mail", ItemID.GOBLIN_ARMOUR_BLACK);
		blackGoblinMailEquipped = new ItemRequirement("Black goblin mail", ItemID.GOBLIN_ARMOUR_BLACK, 1, true);
		inNorthEastRoom = new Conditions(LogicType.OR, new ZoneRequirement(northEastRoomSouth), new ZoneRequirement(northEastRoomNorth));
		dorgeshKaanSphere = new ItemRequirement("Dorgesh-Kaan sphere", ItemID.DORGESH_TELEPORT_ARTIFACT);
		blackDyeOrBlackGoblinMail = new Conditions(LogicType.OR, blackDye, blackGoblinMail);

		knowsAboutWhitefish = new VarbitRequirement(VarbitID.LOTG_KNOW_ABOUT_FISH, 1);
		inHemenster = new ZoneRequirement(hemenster);
		hemensterWhitefish = new ItemRequirement("Whitefish", ItemID.LOTG_WHITEFISH);

		whiteGoblinMail = new ItemRequirement("White goblin mail", ItemID.GOBLIN_ARMOUR_WHITE);
		yellowGoblinMail = new ItemRequirement("Yellow goblin mail", ItemID.GOBLIN_ARMOUR_YELLOW);
		blueGoblinMail = new ItemRequirement("Blue goblin mail", ItemID.GOBLIN_ARMOUR_DARKBLUE);
		orangeGoblinMail = new ItemRequirement("Orange goblin mail", ItemID.GOBLIN_ARMOUR_ORANGE);
		purpleGoblinMail = new ItemRequirement("Purple goblin mail", ItemID.GOBLIN_ARMOUR_PURPLE);

		anyGoblinMail = new ItemRequirement("Goblin mail", ItemID.GOBLIN_ARMOUR);
		anyGoblinMail.addAlternates(ItemID.GOBLIN_ARMOUR_BLACK, ItemID.GOBLIN_ARMOUR_WHITE, ItemID.GOBLIN_ARMOUR_YELLOW, ItemID.GOBLIN_ARMOUR_DARKBLUE,
			ItemID.GOBLIN_ARMOUR_ORANGE, ItemID.GOBLIN_ARMOUR_PURPLE);

		hasAllGoblinKeys = new Conditions(huzamogaarbKey, saragorgakKey, yurkolgokhKey, ekeleshuunKey, nargoshuunKey, horogothgarKey);

		inWhitePriestRoom = new ZoneRequirement(whitePriestRoom);
		inYellowPriestRoom = new ZoneRequirement(yellowPriestRoom);
		inBluePriestRoom = new ZoneRequirement(bluePriestRoom);
		inOrangePriestRoom = new ZoneRequirement(orangePriestRoom);
		inPurplePriestRoom = new ZoneRequirement(purplePriestRoom);

		inCrypt = new ZoneRequirement(crypt);

		snotheadAlive = new NpcRequirement("Snothead", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST1);
		snailfeetAlive = new NpcRequirement("Snailfeet", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST2);
		mosschinAlive = new NpcRequirement("Mosschin", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST3);
		redeyesAlive = new NpcRequirement("Redeyes", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST4);
		strongbonesAlive = new NpcRequirement("Strongbones", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST5);

		snotheadDead = new NpcRequirement("Snothead", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST1_DEFEATED);
		snailfeetDead = new NpcRequirement("Snailfeet", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST2_DEFEATED);
		mosschinDead = new NpcRequirement("Mosschin", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST3_DEFEATED);
		redeyesDead = new NpcRequirement("Redeyes", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST4_DEFEATED);
		strongbonesDead = new NpcRequirement("Strongbones", NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST5_DEFEATED);

		inDorgeshKaanF1 = new ZoneRequirement(dorgeshKaanF1);
		inDorgeshKaanCaveUpper = new ZoneRequirement(dorgeshKaanCaveUpper);
		inDorgeshKaanCaveLower = new ZoneRequirement(dorgeshKaanCaveLower);
		learnedAboutMachine = new VarbitRequirement(VarbitID.LOTG_MACHINE_EXPLAINED, 1);

		firstGreater = new VarbitRequirement(VarbitID.LOTG_CONNECTORS_1, 9, Operation.GREATER);
		firstLess = new VarbitRequirement(VarbitID.LOTG_CONNECTORS_1, 9, Operation.LESS);
		secondGreater = new VarbitRequirement(VarbitID.LOTG_CONNECTORS_2, 4, Operation.GREATER);
		secondLess = new VarbitRequirement(VarbitID.LOTG_CONNECTORS_2, 4, Operation.LESS);
		thirdGreater = new VarbitRequirement(VarbitID.LOTG_CONNECTORS_3, 1, Operation.GREATER);
		thirdLess = new VarbitRequirement(VarbitID.LOTG_CONNECTORS_3, 1, Operation.LESS);
		fairyRingMachineWidgetPresent = new WidgetTextRequirement(738, 2, 1, "Fairy Ring Power Relay");
		fairyRingMachineFixed = new VarbitRequirement(VarbitID.LOTG_FAIRY_RING_ANIMATING, 0, Operation.GREATER);

		inYubiusk = new InInstanceRequirement();
	}

	public void setupSteps()
	{
		goDownIntoBasement = new ObjectStep(this, ObjectID.QIP_COOK_TRAPDOOR_OPEN, new WorldPoint(3209, 3216, 0), "Enter the Lumbridge Castle basement.");
		climbThroughHole = new ObjectStep(this, ObjectID.LOST_TRIBE_CELLAR_WALL, new WorldPoint(3219, 9618, 0), "");
		talkToKazgar = new NpcStep(this, NpcID.LOST_TRIBE_GUIDE_3OPS, new WorldPoint(3230, 9610, 0), "Travel with Kazgar to shortcut to Mistag.");
		talkToKazgar.addDialogStep("Can you show me the way to the mines?");

		talkToGrubfoot = new NpcStep(this, NpcID.LOTG_GRUBFOOT, new WorldPoint(3318, 9611, 0), "");
		talkToGrubfoot.addDialogStep("Yes.");
		talkToGrubfoot.addDialogStep("Follow me.");
		enterDorgeshKaan = new ObjectStep(this, ObjectID.CAVE_GOBLIN_CITY_DOORR, new WorldPoint(3317, 9601, 0), "Enter the city of Dorgesh-Kaan.");
		talkToZanik = new NpcStep(this, NpcID.LOTG_ZANIK, new WorldPoint(2704, 5365, 0),
			"Talk to Zanik in Oldak's lab.");
		talkToZanik.addDialogStep("So why have you come to talk to Zanik?");
		talkToZanik.addDialogStep("What was this new dream?");
		talkToZanik.addDialogSteps("It's just a dream. It doesn't mean anything.", "I think it must mean something.", "I don't know.");
		talkToZanik.addDialogStep("I'm ready.");

		enterGoblinCave = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Enter the Goblin Cave next to the Fishing Guild.");
		talkToZanikGoblinCave = new NpcStep(this, NpcID.LOTG_ZANIK, new WorldPoint(2617, 9797, 0), "Talk to Zanik and get her to follow you.");
		talkToZanikGoblinCave.addDialogStep("Follow me.");
		talkToGuard = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD1, new WorldPoint(2580, 9852, 0), "Talk to the goblin guard in the northwest of the cave.");
		talkToMakeoverMage = new NpcStep(this, new int[]{NpcID.MAKEOVER_MAGE, NpcID.MAKEOVER_MAGE_FEMALE}, new WorldPoint(2917, 3322, 0),
			"Talk to the Makeover Mage southwest of Falador.", toadflaxPotionUnf);
		((NpcStep) talkToMakeoverMage).addTeleport(skillsNecklace.quantity(1).named("Skills necklace (Crafting Guild [3])"));
		talkToMakeoverMage.addDialogSteps("Can you turn me into a goblin?", "I need to slip past some goblin guards.", "Can you turn me into a goblin or not?");
		pickPharmakosBerry = new ObjectStep(this, ObjectID.LOTG_PHARMAKOS_BUSH, "Pick some Pharmakos berries from the bushes outside.", toadflaxPotionUnf);
		mixGoblinPotion = new DetailedQuestStep(this, "Use the pharmakos berries on the unfinished toadflax potion.", pharmakosBerryHighlight, toadflaxUnfHighlight);
		goBackToGoblinCave = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Go back to the Goblin Cave outside the Fishing Guild.",
			goblinPotion, vial, pestleAndMortar, noEquippedItems);
		((ObjectStep) goBackToGoblinCave).addTeleport(skillsNecklace.quantity(1).named("Skills necklace (Fishing Guild [1])"));
		pickBlackMushrooms = new ObjectStep(this, ObjectID.GOLEM_BLACK_MUSHROOMS, new WorldPoint(2577, 9845, 0), "Pick some black mushrooms and use it on a vial to make black dye.",
			goblinPotion, vial, pestleAndMortar, noEquippedItems);
		makeBlackDye = new DetailedQuestStep(this, "Make black dye by using the blackmushrooms on a vial.", blackMushroom.highlighted(), vial.highlighted(), pestleAndMortar);
		goToGuards = new DetailedQuestStep(this, new WorldPoint(2580, 9850, 0), "Go to the area outside the temple near the guards.", goblinPotion, noEquippedItems);
		drinkGoblinPotion = new DetailedQuestStep(this, "Drink the goblin potion.", goblinPotionHighlight, noEquippedItems);
		confirmGoblin = new WidgetStep(this, "Confirm to become a goblin. Your selection doesn't matter.", 739, 31);
		talkToGuardAsGoblin = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD1, new WorldPoint(2580, 9852, 0),
			"Talk to the goblin guard to enter the temple. If you want, you can tell them to guess your name until you get one you like.", blackDye);
		talkToGuardAsGoblin.addDialogStep("Me want get into temple.");
		talkToGuardAsGoblin.addDialogStep(Pattern.compile("^Yes, me .*"));
		talkToGuardAsGoblin.addDialogStep("Yes.");

		getGoblinMail = new ObjectStep(this, ObjectID.LOTG_ARMOUR_CRATE, new WorldPoint(3747, 4309, 0), "Search the crate by the entrance for some goblin mail.", blackDye);
		dyeGoblinMail = new DetailedQuestStep(this, "Use the black dye on the goblin mail.", blackDye.highlighted(), goblinMail.highlighted());
		enterNorthEastRoom = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_BLACK, new WorldPoint(3753, 4329, 0), "Pass by the guard to the northeastern room. WARNING: If you teleport away with a full inventory and while wearing the goblin mail, it will be DESTROYED.", blackGoblinMail.highlighted().equipped());
		enterNorthEastRoomForKey = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_BLACK, new WorldPoint(3753, 4329, 0), "Pass by the guard to the northeastern room again.", blackGoblinMail.highlighted().equipped());

		searchCrateForSphere = new ObjectStep(this, ObjectID.LOTG_SPHERE_CRATE, new WorldPoint(3757, 4342, 0), "Search the crate for a Dorgesh-Kaan sphere.");
		talkToZanikInCell = new NpcStep(this, NpcID.LOTG_ZANIK, new WorldPoint(3751, 4343, 0), "Talk to Zanik in the cell.", dorgeshKaanSphere);
		leaveNorthEastRoom = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_BLACK, new WorldPoint(3753, 4329, 0), "Pass by the guard to leave the northeastern room.");
		talkToPriestInTemple = new NpcStep(this, NpcID.LOTG_GOBLIN_HIGH_PRIEST, new WorldPoint(3744, 4328, 0), "Talk to High Priest Bighead. When prompted, answer in this order: True, False, False.");
		talkToPriestInTemple.addDialogConsideringLastLineCondition("True or false: Those who do not believe in Big High War God, whether they goblins or other races, must die.",
			"True.");
		talkToPriestInTemple.addDialogConsideringLastLineCondition("Second question. True or false: Big High War God chose goblins to be his race because goblins mighty warriors.",
			"False.");
		talkToPriestInTemple.addDialogConsideringLastLineCondition("Third question. True or false: Goblin leaders should be good at planning in order to win battles.",
			"False.");
		talkToPriestInTemple.addDialogStep("Yes.");
		talkToPriestInTemple.addDialogStep("I understand Big High War God.");
		talkToPriestInTemple.addDialogStep("Big High War God commands it.");
		talkToPriestInTemple.addDialogStep("Goblins not mighty warriors before he chose us.");
		talkToPriestInTemple.addDialogStep("That one of the commandments.");
		talkToPriestInTemple.addDialogStep("Lead goblins to victory over whole world.");
		talkToPriestInTemple.addDialogStep("Me want to know about Yu'biusk.");
		talkToPriestInTemple.addDialogStep("Where is Yu'biusk?");
		talkToPriestInTemple.addDialogStep("Can I talk to old high priests?");

		goBackToGoblinCaveNoDye = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Go back to the Goblin Cave outside the Fishing Guild.",
			goblinPotion, noEquippedItems);

		enterGoblinCaveForKilling = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0), "Go back to the Goblin Cave outside the Fishing Guild, ready for fighting.",
			goblinPotion, noEquippedItems, combatGear);

		goToGuardsNoDye = new DetailedQuestStep(this, new WorldPoint(2580, 9850, 0), "Go to the area outside the temple near the guards.", goblinPotion, noEquippedItems);
		drinkGoblinPotionNoDye = new DetailedQuestStep(this, "Drink the goblin potion.", goblinPotionHighlight, noEquippedItems);
		talkToGuardAsGoblinNoDye = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD1, new WorldPoint(2580, 9852, 0), "Talk to the goblin guard to enter the temple. If you want, you can tell them to guess your name until you get one you like.");
		talkToGuardAsGoblin.addDialogStep("Me want get into temple.");
		talkToGuardAsGoblin.addDialogStep(Pattern.compile("^Yes, me .*"));
		talkToGuardAsGoblin.addDialogStep("Yes.");

		pickpocketPriest = new NpcStep(this, NpcID.LOTG_GOBLIN_PRIEST_BLACK_2OP, new WorldPoint(3754, 4340, 0), "Pickpocket a key from the priest.");
		talkToAggie = new NpcStep(this, NpcID.AGGIE, new WorldPoint(3086, 3258, 0), "Talk to Aggie in Draynor Village.");
		((NpcStep) talkToAggie).addAlternateNpcs(NpcID.AGGIE_1OP);
		((NpcStep) talkToAggie).addTeleport(draynorTeleport.quantity(1).named("Amulet of glory (Draynor Village [3])"));
		talkToAggie.addDialogSteps("Draynor Village", "Can you make dyes for me, please?", "Can you make black or white dye?", "Thanks.");
		goToHemenster = new ObjectStep(this, ObjectID.FISHINGGATECLOSEDR, new WorldPoint(2642, 3441, 0), "Go to Hemenster to catch a whitefish.", fishingRod, rawSlimyEel);
		((ObjectStep) goToHemenster).addTeleport(combatBracelet.named("Combat bracelet (Ranging Guild [4])"));
		goToHemenster.addDialogSteps("Ranging Guild", "I need to catch a Hemenster Whitefish.");
		catchWhitefish = new NpcStep(this, NpcID._0_41_53_SINISTERFISHSPOT, new WorldPoint(2637, 3444, 0), "Catch a Hemenster whitefish.", fishingRod, rawSlimyEel);
		talkToAggieWithFish = new NpcStep(this, NpcID.AGGIE, new WorldPoint(3086, 3258, 0), "Bring the whitefish and black goblin mail to Aggie.", coins, hemensterWhitefish, blackGoblinMail);
		((NpcStep) talkToAggieWithFish).addAlternateNpcs(NpcID.AGGIE_1OP);
		((NpcStep) talkToAggieWithFish).addTeleport(draynorTeleport.quantity(1).named("Amulet of glory (Draynor Village [3])"));
		talkToAggieWithFish.addDialogSteps("Draynor Village", "Can you make dyes for me please?", "Could you remove the dye from this goblin mail?");
		goToTempleWithDyes = new ObjectStep(this, ObjectID.MCANNONCAVE, new WorldPoint(2624, 3393, 0),
			"", whiteGoblinMail, goblinPotion, huzamogaarbKey, yellowDye, blueDye, orangeDye, purpleDye, noEquippedItems, combatGear);
		((ObjectStep) goToTempleWithDyes).addTeleport(skillsNecklace.quantity(1).named("Skills necklace (Fishing Guild [1])"));

		enterTempleDoorForThieving = new ObjectStep(this, ObjectID.LOTG_GOBLIN_STAIRCASE, new WorldPoint(2581, 9853, 0), "Enter the temple.");

		dyeGoblinMailYellow = new DetailedQuestStep(this, "Use the yellow dye on your goblin mail. It is safe to unequip the goblin mail inside the enclave, you will be thrown out to the center area.",
			yellowDye.highlighted(), anyGoblinMail.highlighted());
		dyeGoblinMailBlue = new DetailedQuestStep(this, "Use the blue dye on your goblin mail. It is safe to unequip the goblin mail inside the enclave, you will be thrown out to the center area.",
			blueDye.highlighted(), anyGoblinMail.highlighted());
		dyeGoblinMailOrange = new DetailedQuestStep(this, "Use the orange dye on your goblin mail. It is safe to unequip the goblin mail inside the enclave, you will be thrown out to the center area.",
			orangeDye.highlighted(), anyGoblinMail.highlighted());
		dyeGoblinMailPurple = new DetailedQuestStep(this, "Use the purple dye on your goblin mail. It is safe to unequip the goblin mail inside the enclave, you will be thrown out to the center area.",
			purpleDye.highlighted(), anyGoblinMail.highlighted());

		passWhiteGuard = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_WHITE, new WorldPoint(3732, 4320, 0), "Pass by the guard to enter the western area.", whiteGoblinMail.equipped().highlighted());
		passYellowGuard = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_YELLOW, new WorldPoint(3735, 4329, 0), "Pass by the guard to enter the north-western area.", yellowGoblinMail.equipped().highlighted());
		passBlueGuard = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_BLUE, new WorldPoint(3756, 4320, 0), "Pass by the guard to enter the eastern area.", blueGoblinMail.equipped().highlighted());
		passOrangeGuard = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_ORANGE, new WorldPoint(3753, 4311, 0), "Pass by the guard to enter the south-eastern area.", orangeGoblinMail.equipped().highlighted());
		passPurpleGuard = new NpcStep(this, NpcID.LOTG_GOBLIN_GUARD_PURPLE, new WorldPoint(3735, 4311, 0), "Pass by the guard to enter the south-western area.", purpleGoblinMail.equipped().highlighted());

		pickpocketWhitePriest = new NpcStep(this, NpcID.LOTG_GOBLIN_PRIEST_WHITE_2OP, new WorldPoint(3727, 4316, 0), "Pickpocket the priest in the western room for the Saragorak key.");
		pickpocketYellowPriest = new NpcStep(this, NpcID.LOTG_GOBLIN_PRIEST_YELLOW_2OP, new WorldPoint(3727, 4330, 0), "Pickpocket the priest in the north-western room for the Yurkolgokh key.");
		pickpocketBluePriest = new NpcStep(this, NpcID.LOTG_GOBLIN_PRIEST_BLUE_2OP, new WorldPoint(3760, 4320, 0), "Pickpocket the priest in the eastern room for the Ekeleshuun key.");
		pickpocketOrangePriest = new NpcStep(this, NpcID.LOTG_GOBLIN_PRIEST_ORANGE_2OP, new WorldPoint(3762, 4318, 0), "Pickpocket the priest in the south-eastern room for the Narogoshuun key.");
		pickpocketPurplePriest = new NpcStep(this, NpcID.LOTG_GOBLIN_PRIEST_PURPLE_2OP, new WorldPoint(3730, 4302, 0), "Pickpocket the priest in the south-western room for the Horogothgar key.");

		unlockCrypt = new ObjectStep(this, ObjectID.LOTG_TEMPLE_HUGE_DOOR, new WorldPoint(3744, 4332, 0), "Unlock the door using the 6 keys. You may leave to gear up after unlocking it.", huzamogaarbKey, saragorgakKey, yurkolgokhKey, ekeleshuunKey, nargoshuunKey, horogothgarKey);
		enterCrypt = new ObjectStep(this, ObjectID.LOTG_TEMPLE_HUGE_DOOR, new WorldPoint(3744, 4332, 0), "Enter the crypt.", combatGear);
		enterCrypt.addDialogStep("Yes.");

		sayNameSnothead = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE1, new WorldPoint(3738, 4385, 0), "Say Snothead's name at the south-west grave.");
		sayNameSnailfeet = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE2, new WorldPoint(3746, 4385, 0), "Say Snailfeet's name at the south-east grave.");
		sayNameSnailfeet.addDialogStep("Snailfeet.");
		sayNameMosschin = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE3, new WorldPoint(3738, 4389, 0), "Say Mosschin's name at the north-west grave.");
		sayNameMosschin.addDialogStep("Mosschin.");
		sayNameRedeyes = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE4, new WorldPoint(3746, 4389, 0), "Say Redeyes's name at the north-east grave.");
		sayNameRedeyes.addDialogStep("Redeyes.");
		sayNameStrongbones = new ObjectStep(this, ObjectID.LOTG_CRYPT_PRIEST_GRAVE5, new WorldPoint(3742, 4393, 0), "Say Strongbones's name at the north grave.");
		sayNameStrongbones.addDialogStep("Strongbones.");

		defeatSnothead = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST1, "Defeat Snothead. He attacks using melee.");
		defeatSnailfeet = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST2, "Defeat Snailfeet. He attacks using melee and range.");
		defeatMosschin = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST3, "Defeat Mosschin. He attacks using melee and magic.");
		defeatRedeyes = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST4, "Defeat Redeyes. He attacks using melee and magic, and lowers your attack, strength, and defence.");
		defeatStrongbones = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST5, "Defeat Strongbones. He attacks using all 3 combat styles, lowers your attack, strength and defence. Ignore the level 29 Skoblins he spawns.");

		learnSnailfeet = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST1_DEFEATED, "Talk to Snothead to learn the name of the next priest.");
		learnSnailfeet.addDialogStep("What was your predecessor's name?");
		learnMosschin = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST2_DEFEATED, "Talk to Snailfeet to learn the name of the next priest.");
		learnMosschin.addDialogStep("What was your predecessor's name?");
		learnRedeyes = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST3_DEFEATED, "Talk to Mosschin to learn the name of the next priest.");
		learnRedeyes.addDialogStep("What was your predecessor's name?");
		learnStrongbones = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST4_DEFEATED, "Talk to Redeyes to learn the name of the next priest.");
		learnStrongbones.addDialogStep("What was your predecessor's name?");
		learnYubiusk = new NpcStep(this, NpcID.LOTG_GOBLIN_SKELETON_HIGH_PRIEST5_DEFEATED, "Talk to Strongbones to learn where Yu'biusk is.");
		learnYubiusk.addDialogStep("Where is Yu'biusk?");

		talkToOldak = new NpcStep(this, NpcID.DORGESH_OLDAK_THERE, new WorldPoint(2703, 5366, 0), "Talk to Oldak in his workshop.");
		climbDorgeshKaanStairsF0 = new ObjectStep(this, ObjectID.DORGESH_1STAIRS, new WorldPoint(2714, 5283, 0), "If you have access to fairy rings, travel to AJQ. Otherwise, climb the stairs in south Dorgesh-Kaan.");
		climbDorgeshKaanStairsF1 = new ObjectStep(this, ObjectID.DORGESH_2STAIRS_POSH, new WorldPoint(2723, 5253, 1), "Climb up the stairs to the south to enter the Dorgesh-Kaan cave.");
		climbLadderTop = new ObjectStep(this, ObjectID.DORGESH_CAVES_LADDER_DOWN, new WorldPoint(2719, 5241, 3), "Climb down the ladder to the west.");
		talkToOldakAtMachine = new NpcStep(this, NpcID.DORGESH_OLDAK_THERE_1OP, new WorldPoint(2741, 5220, 0), "Talk to Oldak near the machine at the fairy ring " +
			"in the the caves south of Dorgesh-Kaan.");
		inspectMachine = new ObjectStep(this, ObjectID.LOTG_FAIRY_RING_MACHINE_SETUP_THERE, new WorldPoint(2740, 5219, 0), "Fix the machine.");

		increaseFirst = new WidgetStep(this, "Set the first value to 9.", 738, 21);
		decreaseFirst = new WidgetStep(this, "Set the first value to 9.", 738, 22);
		increaseSecond = new WidgetStep(this, "Set the second value to 4.", 738, 23);
		decreaseSecond = new WidgetStep(this, "Set the first value to 4.", 738, 24);
		increaseThird = new WidgetStep(this, "Set the first value to 1.", 738, 25);
		decreaseThird = new WidgetStep(this, "Set the first value to 1.", 738, 26);
		confirmFixMachine = new WidgetStep(this, "Fix the machine.", 738, 39);
		watchYubiuskCutscene = new DetailedQuestStep(this, "Watch the cutscene.");

		goToYubiusk = new NpcStep(this, NpcID.DORGESH_OLDAK_THERE_1OP, new WorldPoint(2741, 5220, 0),
			"Talk to Oldak to return to Yu'Biusk.");
		openBox = new ObjectStep(this, ObjectID.LOTG_BANDOS_SARCOPHAGUS, new WorldPoint(3537, 4389, 0),
			"Walk up to the strange box and watch the cutscene. Quest complete!");

		ConditionalStep getToMine = new ConditionalStep(this, goDownIntoBasement);
		getToMine.addStep(inTunnels, talkToKazgar);
		getToMine.addStep(inBasement, climbThroughHole);

		goTalkToGrubfoot = new ConditionalStep(this, getToMine, "Talk to Grubfoot in the Dorgeshuun Mines.");
		goTalkToGrubfoot.addStep(inMines, talkToGrubfoot);

		ConditionalStep enterCity = new ConditionalStep(this, getToMine);
		enterCity.addStep(inMines, enterDorgeshKaan);

		goTalkToZanik = new ConditionalStep(this, goTalkToGrubfoot);
		goTalkToZanik.addStep(inDorgeshKaanWithGrubfoot, talkToZanik);
		goTalkToZanik.addStep(grubfootFollowing, enterCity);

		// TODO: Add a teleport step to use one of the Dorgesh-Kaan spheres
		goReturnToDorg = new ConditionalStep(this, enterCity, "Return to Dorgesh-Kaan and speak to Oldak.");

		returnToDorgeshKaanOrFairyRing = new ConditionalStep(this, enterCity, "If you have access to fairy rings, travel to AJQ. Otherwise, return to Dorgesh-Kaan.");

		drinkGoblinPotion.addSubSteps(confirmGoblin);
		drinkGoblinPotionNoDye.addSubSteps(confirmGoblin);
		dyeGoblinMail.addSubSteps(getGoblinMail);
		talkToGuardAsGoblin.addSubSteps(talkToGuardAsGoblinNoDye, enterTempleDoorForThieving);
		pickpocketWhitePriest.addSubSteps(passWhiteGuard);
		pickpocketYellowPriest.addSubSteps(passYellowGuard);
		pickpocketBluePriest.addSubSteps(passBlueGuard);
		pickpocketOrangePriest.addSubSteps(passOrangeGuard);
		pickpocketPurplePriest.addSubSteps(passPurpleGuard);
		defeatSnothead.addSubSteps(learnSnailfeet);
		defeatSnailfeet.addSubSteps(learnMosschin);
		defeatMosschin.addSubSteps(learnRedeyes);
		defeatRedeyes.addSubSteps(learnStrongbones);
		talkToOldakAtMachine.addSubSteps(climbDorgeshKaanStairsF0, climbDorgeshKaanStairsF1, climbLadderTop);
		inspectMachine.addSubSteps(decreaseFirst, increaseFirst, decreaseSecond, increaseSecond, decreaseThird, increaseThird, confirmFixMachine);
		openBox.addSubSteps(returnToDorgeshKaanOrFairyRing);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(lightSource, toadflaxPotionUnf, goblinMail, yellowDye, blueDye, orangeDye,
			purpleDye, pestleAndMortar, vial, fishingRod, rawSlimyEel, coins, combatGear));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(tinderbox, dorgeshKaanSphereRec, dramenStaff, skillsNecklace, combatBracelet, lumbridgeTeleport, draynorTeleport, explorersRing, salveAmulet));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(ImmutableList.of("Snothead (level 32)",
			"Snailfeet (level 56)",
			"Mosschin (level 88)",
			"Redeyes (level 121)",
			"Strongbones (level 184)"));
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.ANOTHER_SLICE_OF_HAM, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.FISHING_CONTEST, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 38, false));
		req.add(new SkillRequirement(Skill.FISHING, 40, false));
		req.add(new SkillRequirement(Skill.THIEVING, 45, false));
		req.add(new SkillRequirement(Skill.HERBLORE, 48, false));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards() {
		return Arrays.asList(new ExperienceReward(Skill.AGILITY, 8000),
			new ExperienceReward(Skill.FISHING, 8000),
			new ExperienceReward(Skill.THIEVING, 8000),
			new ExperienceReward(Skill.HERBLORE, 8000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards() {
		return Arrays.asList(new UnlockReward("Access to the Goblin Temple"),
			new UnlockReward("Access to Yu'Biusk (with fairy ring code BLQ)"),
			new UnlockReward("Ability to purchase plain of mud spheres"),
			new UnlockReward("Ability to make goblin potions"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> panels = new ArrayList<>();
		panels.add(new PanelDetails("Grubfoot's Dream",
			Arrays.asList(goTalkToGrubfoot, enterDorgeshKaan, talkToZanik),
			lightSource, noPet));
		panels.add(new PanelDetails("Impostor Among Goblins",
			Arrays.asList(enterGoblinCave, talkToZanikGoblinCave, talkToGuard, talkToMakeoverMage, pickPharmakosBerry, mixGoblinPotion),
			Collections.singletonList(toadflaxPotionUnf),
			Arrays.asList(skillsNecklace, invSpaceToUnequip)));
		panels.add(new PanelDetails("The Temple of Tribes",
			Arrays.asList(goBackToGoblinCave, goToGuards, pickBlackMushrooms, makeBlackDye, drinkGoblinPotion, talkToGuardAsGoblin, dyeGoblinMail, enterNorthEastRoom, searchCrateForSphere, talkToZanikInCell,
				leaveNorthEastRoom, talkToPriestInTemple, enterNorthEastRoomForKey, pickpocketPriest),
			Arrays.asList(noPet, goblinPotion, vial, pestleAndMortar, goblinMail),
			Arrays.asList(skillsNecklace.quantity(1), invSpaceToUnequip, dorgeshKaanSphereRec)));
		panels.add(new PanelDetails("Keys to the Crypt",
			Arrays.asList(talkToAggie, goToHemenster, catchWhitefish, talkToAggieWithFish, returnToTempleWithDyes, pickpocketWhitePriest, dyeGoblinMailYellow,
				pickpocketYellowPriest, dyeGoblinMailBlue, pickpocketBluePriest, dyeGoblinMailOrange, pickpocketOrangePriest,
				dyeGoblinMailPurple, pickpocketPurplePriest, unlockCrypt),
			Arrays.asList(fishingRod, rawSlimyEel, coins, yellowDye, blueDye, orangeDye, purpleDye, blackGoblinMail, goblinPotion, huzamogaarbKey, combatGear),
			Arrays.asList(draynorTeleport, combatBracelet, skillsNecklace.quantity(1))));
		panels.add(new PanelDetails("High Priests of Ages Past",
			Arrays.asList(enterCrypt, sayNameSnothead, defeatSnothead, sayNameSnailfeet, defeatSnailfeet, sayNameMosschin, defeatMosschin, sayNameRedeyes, defeatRedeyes, sayNameStrongbones, defeatStrongbones, learnYubiusk),
			combatGear));
		panels.add(new PanelDetails("Path to Yu'biusk",
			Arrays.asList(goReturnToDorg, talkToOldak, talkToOldakAtMachine, inspectMachine, watchYubiuskCutscene, openBox),
			Collections.singletonList(lightSource),
			Arrays.asList(dorgeshKaanSphereRec, dramenStaff)));
		return panels;
	}
}
