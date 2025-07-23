/*
 * Copyright (c) 2025, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.thefinaldawn;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.helpers.quests.deserttreasureii.ChestCodeStep;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;

import java.util.*;

import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import static com.questhelper.requirements.util.LogicHelper.*;

/**
 * The quest guide for the "The Final Dawn" OSRS quest
 */
public class TheFinalDawn extends BasicQuestHelper
{
	ItemRequirement emissaryRobesEquipped, emissaryRobes, bone, rangedGear;

	ItemRequirement combatGear, combatWeapon, food, prayerPotions, whistle, pendant;
	FreeInventorySlotRequirement freeInvSlots4;

	ItemRequirement drawerKey, canvasPiece, emissaryScroll, potatoes, knife, coinPurse, coinPurseFullOrEmpty, branch, coinPurseWithSand, coinPurseEmpty,
			emptySack, makeshiftBlackjack,	trapdoorKey;
	ItemRequirement steamforgedBrew, dwarvenStout, beer, emptyGlass, wizardsMindBomb, keystoneFragment;

	DetailedQuestStep startQuest, searchChestForEmissaryRobes, enterTwilightTemple, goDownStairsTemple, enterBackroom, searchBed, openDrawers, openDrawers2;
	DetailedQuestStep useCanvasPieceOnPicture, enterPassage, pickBlueChest, fightEnforcer, pickUpEmissaryScroll, readEmissaryScroll, talkToQueen,
	climbStairsF0ToF1Palace, climbStairsF1ToF2Palace;
	ChestCodeStep openDoorWithGusCode;
	DetailedQuestStep talkToCaptainVibia, inspectWindow, giveBonesOrMeatToDog, enterDoorCode, takePotato, removePotatoesFromSack, takeKnife, takeCoinPurse,
			emptyCoinPurse, goToF1Hideout, goDownFromF2Hideout, goToF0Hideout, goToF0HideoutEnd, goF2ToF1HideoutEnd;
	DetailedQuestStep goF1ToF2Hideout, useKnifeOnPottedFan, fillCoinPurse, useBranchOnCoinPurse, showSackToVibia, searchBodyForKey, enterTrapdoor, talkToQueenToGoCamTorum;
	DetailedQuestStep enterCamTorum, talkToAttala, talkToServiusInCamTorum, goUpstairsPub, takeBeer, useBeerOnGalna;
	DetailedQuestStep searchCabinetForDrinks, placeSteamforgedBrew, placeDwarvenStout, placeBeer, placeEmptyGlass, placeMindBomb, inspectFireplace, useHold;
	DetailedQuestStep returnToServius, enterNeypotzli, talkToEyatalli, defeatCultists, talkToServiusAtTalTeklan, enterTonaliCavern, defeatFinalCultists;

	Zone templeBasement, eastTempleBasement, hiddenRoom, palaceF1, palaceF2, hideoutGroundFloor, hideoutMiddleFloor, hideoutTopFloor, hideoutBasement, camTorum;

	Requirement inTempleBasement, inEastTempleBasement, inHiddenRoom, inPalaceF1, inPalaceF2, inHideout, inHideoutF1, inHideoutF2, inHideoutBasement, inCamTorum;

	Requirement isSouthDrawer, hasDrawerKeyOrOpened, usedSigilOnCanvas, emissaryScrollNearby, inChestInterface;
	Requirement hasSackOfGivenSack;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		// TODO: Swap back
//		initializeRequirements();
		setupZones();
		setupRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, startQuest);
		steps.put(1, startQuest);

		ConditionalStep goEnterTemple = new ConditionalStep(this, searchChestForEmissaryRobes);
		goEnterTemple.addStep(emissaryRobes, enterTwilightTemple);
		steps.put(3, goEnterTemple);

		ConditionalStep goEnterTempleBasement = new ConditionalStep(this, searchChestForEmissaryRobes);
		goEnterTempleBasement.addStep(inHiddenRoom, pickBlueChest);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, usedSigilOnCanvas), enterPassage);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, canvasPiece), useCanvasPieceOnPicture);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, hasDrawerKeyOrOpened, isSouthDrawer), openDrawers2);
		goEnterTempleBasement.addStep(and(inEastTempleBasement, hasDrawerKeyOrOpened), openDrawers);
		goEnterTempleBasement.addStep(inEastTempleBasement, searchBed);
		goEnterTempleBasement.addStep(inTempleBasement, enterBackroom);
		goEnterTempleBasement.addStep(emissaryRobes, goDownStairsTemple);

		steps.put(4, goEnterTempleBasement);
		steps.put(5, goEnterTempleBasement);
		steps.put(6, goEnterTempleBasement);
		steps.put(7, goEnterTempleBasement);

		ConditionalStep goFightInBasement = new ConditionalStep(this, searchChestForEmissaryRobes);
		goFightInBasement.addStep(inEastTempleBasement, fightEnforcer);
		goFightInBasement.addStep(inTempleBasement, enterBackroom);
		goFightInBasement.addStep(emissaryRobes, goDownStairsTemple);
		steps.put(8, goFightInBasement);

		ConditionalStep goReadScroll = new ConditionalStep(this, searchChestForEmissaryRobes);
		goReadScroll.addStep(emissaryScroll, readEmissaryScroll);
		goReadScroll.addStep(and(or(inEastTempleBasement, inHiddenRoom), emissaryScrollNearby), pickUpEmissaryScroll);
		goReadScroll.addStep(inEastTempleBasement, enterPassage);
		goReadScroll.addStep(inHiddenRoom, pickBlueChest);
		goReadScroll.addStep(inTempleBasement, enterBackroom);
		goReadScroll.addStep(emissaryRobes, goDownStairsTemple);
		steps.put(9, goReadScroll);

		ConditionalStep goTalkToQueen = new ConditionalStep(this, climbStairsF0ToF1Palace);
		goTalkToQueen.addStep(inPalaceF2, talkToQueen);
		goTalkToQueen.addStep(inPalaceF1, climbStairsF1ToF2Palace);
		steps.put(10, goTalkToQueen);

		steps.put(11, talkToCaptainVibia);
		ConditionalStep goIntoHouse = new ConditionalStep(this, inspectWindow);
		goIntoHouse.addStep(inHideout, giveBonesOrMeatToDog);
		steps.put(12, goIntoHouse);
		steps.put(13, goIntoHouse);
		steps.put(14, goIntoHouse);
		steps.put(15, goIntoHouse);
		steps.put(16, goIntoHouse);

		ConditionalStep goPetDog = new ConditionalStep(this, inspectWindow);
		goPetDog.addStep(inChestInterface, openDoorWithGusCode);
		goPetDog.addStep(inHideout, enterDoorCode);
		steps.put(17, goPetDog);

		ConditionalStep goDoHideoutStuff = new ConditionalStep(this, inspectWindow);
		goDoHideoutStuff.addStep(and(inHideoutF1, hasSackOfGivenSack, makeshiftBlackjack), goToF0HideoutEnd);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, makeshiftBlackjack), goF2ToF1HideoutEnd);
		goDoHideoutStuff.addStep(and(inHideout, hasSackOfGivenSack, makeshiftBlackjack), showSackToVibia);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, coinPurseWithSand, branch), useBranchOnCoinPurse);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, knife, coinPurseWithSand), useKnifeOnPottedFan);
		goDoHideoutStuff.addStep(and(inHideoutF2, hasSackOfGivenSack, knife, coinPurseEmpty), fillCoinPurse);
		goDoHideoutStuff.addStep(inHideoutF2, goDownFromF2Hideout);
		goDoHideoutStuff.addStep(and(inHideoutF1, hasSackOfGivenSack, knife, coinPurseEmpty), goF1ToF2Hideout);
		goDoHideoutStuff.addStep(and(coinPurse), emptyCoinPurse);
		goDoHideoutStuff.addStep(and(inHideoutF1, hasSackOfGivenSack, knife), takeCoinPurse);
		goDoHideoutStuff.addStep(and(inHideoutF1), goToF0Hideout);
		goDoHideoutStuff.addStep(and(inHideout, hasSackOfGivenSack, knife), goToF1Hideout);
		goDoHideoutStuff.addStep(and(inHideout, hasSackOfGivenSack), takeKnife);
		goDoHideoutStuff.addStep(and(inHideout, potatoes), removePotatoesFromSack);
		goDoHideoutStuff.addStep(inHideout, takePotato);
		steps.put(18, goDoHideoutStuff);
		// 19 was took coin purse, 20 was emptied it first time
		steps.put(19, goDoHideoutStuff);
		steps.put(20, goDoHideoutStuff);
		steps.put(21, goDoHideoutStuff);
		steps.put(22, goDoHideoutStuff);

		ConditionalStep goSearchJanus = new ConditionalStep(this, inspectWindow);
		goSearchJanus.addStep(inHideout, searchBodyForKey);
		steps.put(23, goSearchJanus);

		ConditionalStep goEnterTrapdoor = new ConditionalStep(this, inspectWindow);
		goEnterTrapdoor.addStep(inHideoutBasement, talkToQueenToGoCamTorum);
		goEnterTrapdoor.addStep(inHideout, enterTrapdoor);
		steps.put(24, goEnterTrapdoor);
		steps.put(25, goEnterTrapdoor);

		ConditionalStep goTalkToAttala = new ConditionalStep(this, enterCamTorum);
		goTalkToAttala.addStep(inCamTorum, talkToAttala);
		steps.put(26, goTalkToAttala);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		templeBasement = new Zone(new WorldPoint(1660, 9680, 0), new WorldPoint(1725, 9720, 0));
		eastTempleBasement = new Zone(new WorldPoint(1707, 9696, 0), new WorldPoint(1718, 9715, 0));
		hiddenRoom = new Zone(new WorldPoint(1721, 9702, 0), new WorldPoint(1725, 9709, 0));
		palaceF1 = new Zone(new WorldPoint(1669, 3150, 1), new WorldPoint(1692, 3175, 1));
		palaceF2 = new Zone(new WorldPoint(1669, 3150, 2), new WorldPoint(1692, 3175, 2));
		hideoutGroundFloor = new Zone(new WorldPoint(1643, 3091, 0), new WorldPoint(1652, 3096, 0));
		hideoutMiddleFloor = new Zone(new WorldPoint(1643, 3091, 1), new WorldPoint(1652, 3096, 1));
		hideoutTopFloor = new Zone(new WorldPoint(1643, 3091, 2), new WorldPoint(1652, 3102, 2));
		hideoutBasement = new Zone(new WorldPoint(1643, 9486, 0), new WorldPoint(1657, 9500, 0));
		camTorum = new Zone(new WorldPoint(1378, 9502, 1), new WorldPoint(1524, 9600, 3));
	}

	@Override
	protected void setupRequirements()
	{
		var emissaryHood = new ItemRequirement("Emissary hood", ItemID.VMQ3_CULTIST_HOOD);
		var emissaryTop = new ItemRequirement("Emissary top", ItemID.VMQ3_CULTIST_ROBE_TOP);
		var emissaryBottom = new ItemRequirement("Emissary bottom", ItemID.VMQ3_CULTIST_ROBE_BOTTOM);
		var emissaryBoots = new ItemRequirement("Emissary sandals", ItemID.VMQ3_CULTIST_SANDALS);
		emissaryRobesEquipped = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom,
				emissaryBoots).equipped();
		emissaryRobes = new ItemRequirements("Emissary robes", emissaryHood, emissaryTop, emissaryBottom, emissaryBoots);

		// TODO: Add remaining bones
		var givenBoneToDog = new VarbitRequirement(VarbitID.VMQ4, 17, Operation.GREATER_EQUAL);
		bone = new ItemRequirement("Any type of bone", ItemID.BONES);
		bone.setConditionToHide(givenBoneToDog);
		bone.addAlternates(ItemID.BIG_BONES, ItemID.BONES_BURNT, ItemID.WOLF_BONES, ItemID.BAT_BONES, ItemID.DAGANNOTH_KING_BONES);

		rangedGear = new ItemRequirement("Ranged/Magic Combat gear", -1, -1).isNotConsumed();
		rangedGear.setDisplayItemId(BankSlotIcons.getRangedCombatGear());


		// Item Recommended
		combatWeapon = new ItemRequirement("Combat weapon", -1, -1).isNotConsumed();
		combatWeapon.setDisplayItemId(BankSlotIcons.getCombatGear());

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		food.setUrlSuffix("Food");

		prayerPotions = new ItemRequirement("Prayer potions", ItemCollections.PRAYER_POTIONS);

		whistle = new ItemRequirement("Quetzal whistle", ItemID.HG_QUETZALWHISTLE_BASIC);
		whistle.addAlternates(ItemID.HG_QUETZALWHISTLE_ENHANCED, ItemID.HG_QUETZALWHISTLE_PERFECTED);
		pendant = new ItemRequirement("Pendant of ates", ItemID.PENDANT_OF_ATES);

		// Quest items
		drawerKey = new ItemRequirement("Key", ItemID.VMQ4_DRAWER_KEY);
		canvasPiece = new ItemRequirement("Canvas piece", ItemID.VMQ4_PAINTING_SIGIL);
		emissaryScroll = new ItemRequirement("Emissary scroll", ItemID.VMQ4_CULT_MANIFEST);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		potatoes = new ItemRequirement("Potatoes (?)", ItemID.SACK_POTATO_3);
		potatoes.addAlternates(ItemID.SACK_POTATO_2, ItemID.SACK_POTATO_1);

		var givenSack = new VarbitRequirement(VarbitID.VMQ4_JANUS_SACK_GIVEN, 1, Operation.GREATER_EQUAL);
		emptySack = new ItemRequirement("Empty sack", ItemID.SACK_EMPTY);
		emptySack.setConditionToHide(givenSack);
		coinPurse = new ItemRequirement("Coin purse", ItemID.VMQ4_JANUS_PURSE);

		coinPurseFullOrEmpty = new ItemRequirement("Coin purse", ItemID.VMQ4_JANUS_PURSE);
		coinPurseFullOrEmpty.addAlternates(ItemID.VMQ4_JANUS_PURSE_EMPTY);
		coinPurseEmpty = new ItemRequirement("Empty coin purse", ItemID.VMQ4_JANUS_PURSE_EMPTY);
		coinPurseWithSand = new ItemRequirement("Sandy coin purse", ItemID.VMQ4_JANUS_PURSE_SAND);
		branch = new ItemRequirement("Branch", ItemID.VMQ4_JANUS_REED);
		makeshiftBlackjack = new ItemRequirement("Makeshift blackjack", ItemID.VMQ4_JANUS_SLAP);

		// Quest requirements
		inTempleBasement = new ZoneRequirement(templeBasement);
		inEastTempleBasement = new ZoneRequirement(eastTempleBasement);
		inHiddenRoom = new ZoneRequirement(hiddenRoom);
		inPalaceF1 = new ZoneRequirement(palaceF1);
		inPalaceF2 = new ZoneRequirement(palaceF2);
		inHideout = new ZoneRequirement(hideoutGroundFloor);
		inHideoutF1 = new ZoneRequirement(hideoutMiddleFloor);
		inHideoutF2 = new ZoneRequirement(hideoutTopFloor);
		inHideoutBasement = new ZoneRequirement(hideoutBasement);
		inCamTorum = new ZoneRequirement(camTorum);

		isSouthDrawer = new VarbitRequirement(VarbitID.VMQ4_CANVAS_DRAWER, 2);
		hasDrawerKeyOrOpened = or(drawerKey, new VarbitRequirement(VarbitID.VMQ4_TEMPLE_DRAW_UNLOCKED, 1, Operation.GREATER_EQUAL));
		usedSigilOnCanvas = new VarbitRequirement(VarbitID.VMQ4, 7, Operation.GREATER_EQUAL);
		emissaryScrollNearby = new ItemOnTileRequirement(emissaryScroll);
		inChestInterface = new WidgetTextRequirement(809, 5, 9, "Confirm");

		hasSackOfGivenSack = or(emptySack, givenSack);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.VMQ3_SERVIUS_PALACE, new WorldPoint(1681, 3168, 0), "Talk to Servius in the Sunrise Palace in Vicitas illa " +
				"Fortis to start the quest.");
		startQuest.addDialogStep("Yes.");

		freeInvSlots4 = new FreeInventorySlotRequirement(4);
		searchChestForEmissaryRobes = new ObjectStep(this, ObjectID.VMQ3_CULTIST_OUTFIT_CHEST, new WorldPoint(1638, 3217, 0), "Search the chest in the south of the tower " +
				"for some emissary robes.", freeInvSlots4);
		searchChestForEmissaryRobes.addTeleport(pendant);
		enterTwilightTemple = new DetailedQuestStep(this, new WorldPoint(1687, 3247, 0), "Enter the temple south-east of Salvager Overlook.",
				emissaryRobesEquipped);

		goDownStairsTemple = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_STAIRS, new WorldPoint(1677, 3248, 0), "Go down the stairs in the temple. The " +
				"passphrase is 'Final' and 'Dawn'.", List.of(emissaryRobesEquipped), List.of(combatWeapon, food));
		goDownStairsTemple.addDialogSteps("Final.", "Dawn.");

		enterBackroom = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_CHAMBER_ENTRY, new WorldPoint(1706, 9706, 0), "Enter the far eastern room. Avoid" +
				" the patrolling guard.");

		searchBed = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_BED_WITH_KEY, new WorldPoint(1713, 9698, 0), "Search the bed in he south room.");
		openDrawers = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_1_CLOSED, new WorldPoint(1713, 9714, 0), "Open the drawers in the north room.");
		((ObjectStep) openDrawers).addAlternateObjects(ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_1_OPEN);
		openDrawers.conditionToHideInSidebar(isSouthDrawer);

		openDrawers2 = new ObjectStep(this, ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_2_CLOSED, new WorldPoint(1709, 9700, 0), "Open the drawers in the same room.");
		((ObjectStep) openDrawers2).addAlternateObjects(ObjectID.VMQ4_TEMPLE_CANVAS_DRAW_2_OPEN);
		openDrawers2.conditionToHideInSidebar(not(isSouthDrawer));
		useCanvasPieceOnPicture = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_PAINTING, new WorldPoint(1719, 9706, 0), "Use canvas piece on the " +
				"painting in the east of the middle room of the eastern rooms.", canvasPiece.highlighted());
		useCanvasPieceOnPicture.addIcon(ItemID.VMQ4_PAINTING_SIGIL);
		enterPassage = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_PAINTING, new WorldPoint(1719, 9706, 0), "Enter the passage behind the painting.");
		enterPassage.addDialogSteps("Enter the passage.");
		pickBlueChest = new ObjectStep(this, ObjectID.TWILIGHT_TEMPLE_METZLI_CHAMBER_CHEST_CLOSED, new WorldPoint(1723, 9709, 0), "Picklock the chest in the " +
				"hidden room. Be ready for a fight afterwards.");
		fightEnforcer = new NpcStep(this, NpcID.VMQ4_TEMPLE_GUARD_BOSS_FIGHT, new WorldPoint(1712, 9706, 0), "Defeat the enforcer. You cannot use prayers" +
				". Step away each time he goes to attack, and step behind him if he says 'Traitor!' or 'Thief!'.");
		pickUpEmissaryScroll = new ItemStep(this, "Pick up the emissary scroll.", emissaryScroll);
		readEmissaryScroll = new DetailedQuestStep(this, "Read the emissary scroll.", emissaryScroll.highlighted());

		// Part 2
		climbStairsF0ToF1Palace = new ObjectStep(this, ObjectID.CIVITAS_PALACE_STAIRS_UP, new WorldPoint(1672, 3164, 0), "Climb up the stairs to the top of " +
				"the Sunrise Palace to talk to the queen.");
		climbStairsF1ToF2Palace = new ObjectStep(this, ObjectID.CIVITAS_PALACE_STAIRS_UP, new WorldPoint(1671, 3169, 1), "Climb up the stairs to the top of " +
				"the Sunrise Palace to talk to the queen.");
		talkToQueen = new NpcStep(this, NpcID.VMQ4_QUEEN_PALACE, new WorldPoint(1673, 3156, 2), "Talk to the queen on the top floor of the Sunrise Palace.");
		talkToQueen.addSubSteps(climbStairsF0ToF1Palace, climbStairsF1ToF2Palace);
		talkToCaptainVibia = new NpcStep(this, NpcID.VMQ4_CAPTAIN_VIBIA_OUTSIDE_HOUSE, new WorldPoint(1652, 3088, 0), "Talk to Captain Vibia south of Civitas" +
				" illa Fortis' west bank.");
		inspectWindow = new ObjectStep(this, ObjectID.VMQ4_CIVITAS_JANUS_WINDOW, new WorldPoint(1652, 3093, 0), "Inspect the window on the east side of the " +
				"house north of Captain Vibia, and then enter it.", bone);
		inspectWindow.addDialogSteps("Force open the window.");
		giveBonesOrMeatToDog = new NpcStep(this, NpcID.VMQ4_JANUS_DOG, new WorldPoint(1650, 3094, 0),  "Use bones or some raw meat on the dog to calm it down.");
		enterDoorCode = new ObjectStep(this, ObjectID.VMQ4_JANUS_HOUSE_PUZZLE_DOOR, new WorldPoint(1649, 3093, 0), "Pet the dog to see the code for the door." +
				" Open the door using the code 'GUS'.");
		openDoorWithGusCode = new ChestCodeStep(this, "GUS", 10, 0, 4, 0);
		enterDoorCode.addSubSteps(openDoorWithGusCode);
		takePotato = new ItemStep(this, "Pick up the sack of potatoes (3).", potatoes);
		removePotatoesFromSack = new DetailedQuestStep(this, "Empty the sack of potatoes.", potatoes.highlighted());
		takeKnife = new ItemStep(this, "Pick up the knife.", knife);
		takeCoinPurse = new ItemStep(this, "Pick up the coin purse.", coinPurseFullOrEmpty);

		goToF1Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_BOTTOM, new WorldPoint(1647, 3091, 0), "Go upstairs.");
		goDownFromF2Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_TOP, new WorldPoint(1647, 3091, 2), "Go downstairs.");
		goF1ToF2Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_MIDDLE, new WorldPoint(1647, 3091, 1), "Go to the top floor.");
		goF1ToF2Hideout.addDialogStep("Climb up.");
		useKnifeOnPottedFan = new ObjectStep(this, ObjectID.VMQ4_JANUS_HOUSE_PLANT, new WorldPoint(1650, 3095, 2), "Use the knife on the inspectable potted " +
				"fan.", knife.highlighted());
		useKnifeOnPottedFan.addIcon(ItemID.KNIFE);
		fillCoinPurse = new ObjectStep(this, ObjectID.VMQ4_JANUS_HOUSE_EMPTY_POT, new WorldPoint(1645, 3098, 2), "Use the empty coin purse on the plant pot " +
				"in the north-west of the roof with sand in it.", coinPurseEmpty.highlighted());
		fillCoinPurse.addIcon(ItemID.VMQ4_JANUS_PURSE_EMPTY);
		emptyCoinPurse = new DetailedQuestStep(this, "Empty the coin purse.", coinPurse.highlighted());
		useBranchOnCoinPurse = new DetailedQuestStep(this, "Use the branch on the coin purse.", branch.highlighted(), coinPurseWithSand.highlighted());

		goToF0Hideout = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_MIDDLE, new WorldPoint(1647, 3091, 1), "Go to the ground floor.");
		goToF0Hideout.addDialogStep("Climb down.");
		goToF0HideoutEnd = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_MIDDLE, new WorldPoint(1647, 3091, 1), "Go to the ground floor.");
		goToF0HideoutEnd.addDialogStep("Climb down.");

		goF2ToF1HideoutEnd = new ObjectStep(this, ObjectID.FORTIS_WOODEN_SPIRALSTAIRS_TOP, new WorldPoint(1647, 3091, 2), "Go downstairs back to Vibia.");
		showSackToVibia = new NpcStep(this, NpcID.VMQ4_CAPTAIN_VIBIA_INSIDE_HOUSE, new WorldPoint(1651, 3094, 0), "Show Captain Vibia the empty sack and " +
				"makeshift blackjack.", emptySack, makeshiftBlackjack);
		showSackToVibia.addSubSteps(goF2ToF1HideoutEnd, goToF0HideoutEnd);
		searchBodyForKey = new NpcStep(this, NpcID.VMQ4_JANUS_HOUSE_JANUS_UNCONSCIOUS, new WorldPoint(1647, 3093, 0), "Search Janus.");
		enterTrapdoor = new ObjectStep(this, ObjectID.VMQ4_JANUS_BASEMENT_ENTRY, new WorldPoint(1643, 3092, 0), "Enter the basement in the hideout.");

		talkToQueenToGoCamTorum = new NpcStep(this, NpcID.VMQ4_QUEEN_BASEMENT, new WorldPoint(1653, 9493, 0), "Talk to the queen in the hideout basement.");
		enterCamTorum = new ObjectStep(this, ObjectID.PMOON_TELEBOX, new WorldPoint(1436, 3129, 0), "Enter Cam Torum.");
		enterCamTorum.addDialogStep("Yes, let's go.");

		talkToAttala = new NpcStep(this, NpcID.CAM_TORUM_ATTALA, new WorldPoint(1439, 9563, 0), "Talk to Attala in Cam Torum.");
//		talkToServiusInCamTorum = ;
//		goUpstairsPub = ;
//		takeBeer = ;
//		useBeerOnGalna = ;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			emissaryRobes, bone, combatGear
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			rangedGear, food, prayerPotions, pendant, whistle
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
				new QuestRequirement(QuestHelperQuest.THE_HEART_OF_DARKNESS, QuestState.FINISHED),
				new QuestRequirement(QuestHelperQuest.PERILOUS_MOON, QuestState.FINISHED),
				new SkillRequirement(Skill.THIEVING, 66),
				new SkillRequirement(Skill.FLETCHING, 52),
				new SkillRequirement(Skill.RUNECRAFT, 52)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		// TODO: Add in rest
		return List.of(
			"Emissary Enforcer (lvl-196)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 55000),
			new ExperienceReward(Skill.FLETCHING, 25000),
			new ExperienceReward(Skill.RUNECRAFT, 25000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The Arkan Blade"),
			new UnlockReward("Access to Mokhaiotl"),
			new UnlockReward("Access to Crypt of Tonali")
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("55,000 Experience Lamps (Combat Skills)", ItemID.THOSF_REWARD_LAMP, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Starting off", List.of(
			startQuest, searchChestForEmissaryRobes, enterTwilightTemple, goDownStairsTemple, enterBackroom, searchBed, openDrawers, openDrawers2,
				useCanvasPieceOnPicture, enterPassage, pickBlueChest, fightEnforcer, pickUpEmissaryScroll, readEmissaryScroll
		), List.of(
			combatWeapon, food
		), List.of(
			// Recommended
		)));
		panels.add(new PanelDetails("The hideout", List.of(talkToQueen, talkToCaptainVibia, inspectWindow, giveBonesOrMeatToDog, enterDoorCode, takePotato,
				takeKnife, goToF1Hideout, takeCoinPurse, goF1ToF2Hideout, useKnifeOnPottedFan, fillCoinPurse, useBranchOnCoinPurse, showSackToVibia,
				searchBodyForKey, enterTrapdoor, talkToQueenToGoCamTorum),
				List.of(bone),
				List.of()));
		panels.add(new PanelDetails("The dwarves", List.of(enterCamTorum, talkToAttala),
				List.of(bone),
				List.of()));
		return panels;
	}
}
