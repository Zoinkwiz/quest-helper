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
package com.questhelper.quests.ghostsahoy;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemCondition;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.GHOSTS_AHOY
)
public class GhostsAhoy extends BasicQuestHelper
{
	ItemRequirement ghostspeak, coins400, milk, silk, dyes, spade, oakLongbow, knife, needle, thread, bucketOfSlime, nettleTea, ectoToken2, ectoToken4, chestKey,
		nettleTeaHighlighted, milkHighlighted, milkyTea, cup, cupWithMilkyTea, cupWithTea, modelShip, repairedShip, ectoToken12, ectoToken27, charos, map, signedOakBow,
		ectoToken10, ectoToken25, ectoSheets, bedsheet, petition, boneKey, boneKeyHighlighted, robes, book, manual, mapPiece1, mapPiece2, mapPiece3, silkHighlighted,
		ectoTokensCharos, ectoTokensNoCharos, ectoSheetsEquipped, enchantedGhostspeakEquipped;

	ConditionForStep inPhas, onDragontooth, hasCupOfMilkyTea, hasCupOfTea, hasModelShip, hasRepairedShip, hasPiece1, hasPiece2, hasPiece3, hasMap, lobsterNearby,
		hasCup, killedLobster, hadChestKey, onDeck, onTopOfShip, onRocks, unlockedChest2, hasBook, hasSheet, hasEctoSheet, hasMysticalRobes, hasManual, boneKeyNearby,
		hasBoneKey, talkedToAkHaranu, hasSignedOakBow, hasPetition, hasSignatures, givenPetitionToNecro, inUpstairsEcto, doorUnlocked;

	QuestStep enterPhas, talkToVelorina, talkToNecrovarus, enterPhasAfterNecro, talkToVelorinaAfterNecro, talkToCrone, useTeaOnCup, useMilkOnTea,
		talkToCroneAgain, repairShip, goUpToDeck, useKeyOnChest, goDownFromMast, searchChestForLobster, killLobster, searchChestAfterLobster, goAcrossPlank,
		openSecondChest, openThirdChest, useMapsTogether, enterPhasForDigging, takeRowingBoat, digForBook, returnToPhas, talkToAkHaranu, talkToInnkeeper, useSlimeOnSheet,
		talkToGravingas, talkToVillagers, showPetitionToNecro, talkToNecroForKey, takeKey, goUpFromNecro, useKeyOnDoor, takeRobes, returnToCrone, bringCroneAmulet,
		talkToNecroAfterCurse, enterPhasFinal, talkToVelorinaFinal, talkToCroneAgainForShip, enterPhasForManual, enterPhasForRobe, talkToRobin, bringBowToAkHaranu;

	ConditionalStep getBookSteps, getManualSteps, getRobesSteps;

	DyeShipSteps dyeFlags;

	Zone phas1, phas2, phas3, phas4, phas5, phas6, phas7, phas8, dragontooth, deck, topOfShip, rocks, upstairsEcto;

	boolean canUseCharos;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		// TODO: Verify specific step which unlocks the ring of charos
		canUseCharos = client.getVarbitValue(QuestVarbits.QUEST_GARDEN_OF_TRANQUILLITY.getId()) > 2;
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startQuest = new ConditionalStep(this, enterPhas);
		startQuest.addStep(inPhas, talkToVelorina);
		steps.put(0, startQuest);

		steps.put(1, talkToNecrovarus);

		ConditionalStep goReturnToVelorina = new ConditionalStep(this, enterPhasAfterNecro);
		goReturnToVelorina.addStep(inPhas, talkToVelorinaAfterNecro);
		steps.put(2, goReturnToVelorina);

		ConditionalStep makeCroneTea = new ConditionalStep(this, talkToCrone);
		makeCroneTea.addStep(hasCupOfMilkyTea, talkToCroneAgain);
		makeCroneTea.addStep(hasCupOfTea, useMilkOnTea);
		makeCroneTea.addStep(hasCup, useTeaOnCup);
		steps.put(3, makeCroneTea);

		getBookSteps = new ConditionalStep(this, talkToCroneAgainForShip);
		getBookSteps.addStep(new Conditions(hasBook, inPhas), returnToPhas);
		getBookSteps.addStep(new Conditions(hasBook, onDragontooth), returnToPhas);
		getBookSteps.addStep(new Conditions(hasBook, onDragontooth), returnToPhas);
		getBookSteps.addStep(new Conditions(hasMap, onDragontooth), digForBook);
		getBookSteps.addStep(new Conditions(hasMap, inPhas), takeRowingBoat);
		getBookSteps.addStep(new Conditions(hasMap), enterPhasForDigging);
		getBookSteps.addStep(new Conditions(hasPiece1, hasPiece2, hasPiece3), useMapsTogether);
		getBookSteps.addStep(new Conditions(hasPiece1, hasPiece2, onRocks), openThirdChest);
		getBookSteps.addStep(new Conditions(hasPiece1, hasPiece2, onDeck), goAcrossPlank);
		getBookSteps.addStep(new Conditions(hasPiece1, hasPiece2, onTopOfShip), goDownFromMast);
		getBookSteps.addStep(new Conditions(hasPiece1, hasPiece2), goUpToDeck);
		getBookSteps.addStep(new Conditions(hasPiece1, unlockedChest2, onDeck), openSecondChest);
		getBookSteps.addStep(new Conditions(hasPiece1, hadChestKey, onDeck), useKeyOnChest);
		getBookSteps.addStep(new Conditions(hasPiece1, hadChestKey, onTopOfShip), goDownFromMast);
		getBookSteps.addStep(new Conditions(hasPiece1, hadChestKey), goUpToDeck);
		getBookSteps.addStep(new Conditions(hasRepairedShip, hasPiece1), dyeFlags);
		getBookSteps.addStep(new Conditions(hasRepairedShip, killedLobster), searchChestAfterLobster);
		getBookSteps.addStep(new Conditions(hasRepairedShip, lobsterNearby), killLobster);
		getBookSteps.addStep(hasRepairedShip, searchChestForLobster);
		getBookSteps.addStep(hasModelShip, repairShip);
		getBookSteps.setLockingCondition(hasBook);

		getManualSteps = new ConditionalStep(this, enterPhasForManual);
		getManualSteps.addStep(onDragontooth, returnToPhas);
		getManualSteps.addStep(new Conditions(inPhas, hasSignedOakBow), bringBowToAkHaranu);
		getManualSteps.addStep(new Conditions(inPhas, talkedToAkHaranu), talkToRobin);
		getManualSteps.addStep(inPhas, talkToAkHaranu);
		getManualSteps.setLockingCondition(hasManual);

		getRobesSteps = new ConditionalStep(this, enterPhasForRobe);
		getRobesSteps.addStep(onDragontooth, returnToPhas);
		getRobesSteps.addStep(new Conditions(inUpstairsEcto, doorUnlocked), takeRobes);
		getRobesSteps.addStep(new Conditions(inUpstairsEcto, hasBoneKey), useKeyOnDoor);
		getRobesSteps.addStep(new Conditions(hasBoneKey), goUpFromNecro);
		getRobesSteps.addStep(new Conditions(boneKeyNearby), takeKey);
		getRobesSteps.addStep(new Conditions(givenPetitionToNecro), talkToNecroForKey);
		getRobesSteps.addStep(new Conditions(hasSignatures), showPetitionToNecro);
		getRobesSteps.addStep(new Conditions(hasEctoSheet, inPhas, hasPetition), talkToVillagers);
		getRobesSteps.addStep(new Conditions(hasEctoSheet, inPhas), talkToGravingas);
		getRobesSteps.addStep(hasSheet, useSlimeOnSheet);
		getRobesSteps.addStep(inPhas, talkToInnkeeper);
		getRobesSteps.setLockingCondition(hasMysticalRobes);

		ConditionalStep getItemSteps = new ConditionalStep(this, getBookSteps);
		getItemSteps.addStep(new Conditions(hasBook, hasManual, hasMysticalRobes), returnToCrone);
		getItemSteps.addStep(new Conditions(hasBook, hasManual), getRobesSteps);
		getItemSteps.addStep(hasBook, getManualSteps);
		steps.put(4, getItemSteps);

		steps.put(5, bringCroneAmulet);

		steps.put(6, talkToNecroAfterCurse);

		ConditionalStep finishQuest = new ConditionalStep(this, enterPhasFinal);
		finishQuest.addStep(inPhas, talkToVelorinaFinal);
		steps.put(7, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		ectoToken2 = new ItemRequirement("Ecto-token, or travel by Charter Ship", ItemID.ECTOTOKEN, 2);
		ectoTokensCharos = new ItemRequirement("20 Ecto-token, OR 10 Ecto-Tokens and coins to travel by Charter Ship", ItemID.ECTOTOKEN, -1);
		ectoTokensNoCharos = new ItemRequirement("31 Ecto-token, OR 25 Ecto-Tokens and coins to travel by Charter Ship", ItemID.ECTOTOKEN, -1);
		ectoToken4 = new ItemRequirement("Ecto-token, or travel by Charter Ship", ItemID.ECTOTOKEN, 4);
		ectoToken12 = new ItemRequirement("Ecto-token, or travel by Charter Ship and 10 ecto-tokens", ItemID.ECTOTOKEN, 12);
		ectoToken27 = new ItemRequirement("Ecto-token, or travel by Charter Ship and 25 ecto-tokens", ItemID.ECTOTOKEN, 27);
		ectoToken10 = new ItemRequirement("Ecto-token", ItemID.ECTOTOKEN, 10);
		ectoToken25 = new ItemRequirement("Ecto-token", ItemID.ECTOTOKEN, 25);
		chestKey = new ItemRequirement("Chest key", ItemID.CHEST_KEY_4273);
		chestKey.setTip("You can get another from the Old man on the abandoned ship");
		chestKey.setHighlightInInventory(true);

		nettleTea = new ItemRequirement("Nettle tea", ItemID.NETTLE_TEA);
		nettleTeaHighlighted = new ItemRequirement("Nettle tea", ItemID.NETTLE_TEA);
		nettleTeaHighlighted.setHighlightInInventory(true);

		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milkHighlighted = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
		milkHighlighted.setHighlightInInventory(true);

		milkyTea = new ItemRequirement("Nettle tea", ItemID.NETTLE_TEA_4240);
		milkyTea.setHighlightInInventory(true);

		cup = new ItemRequirement("Porcelain cup", ItemID.PORCELAIN_CUP);
		cup.setHighlightInInventory(true);

		cupWithTea = new ItemRequirement("Cup of tea", ItemID.CUP_OF_TEA_4245);
		cupWithTea.setHighlightInInventory(true);

		cupWithMilkyTea = new ItemRequirement("Cup of tea", ItemID.CUP_OF_TEA_4246);

		ghostspeak = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true);
		enchantedGhostspeakEquipped = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET_4250, 1, true);
		coins400 = new ItemRequirement("400+ coins", ItemID.COINS_995, -1);
		silk = new ItemRequirement("Silk", ItemID.SILK);
		silkHighlighted = new ItemRequirement("Silk", ItemID.SILK);
		silkHighlighted.setHighlightInInventory(true);
		dyes = new ItemRequirement("3 colours of dyes. Which you'll need is random. To be prepared, bring 3 red/blue/yellow dyes", -1, -1);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		oakLongbow = new ItemRequirement("Oak longbow", ItemID.OAK_LONGBOW);

		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		needle = new ItemRequirement("Needle", ItemID.NEEDLE);
		thread = new ItemRequirement("Thread", ItemID.THREAD);
		bucketOfSlime = new ItemRequirement("Bucket of slime", ItemID.BUCKET_OF_SLIME);
		bucketOfSlime.setTip("You can buy one from the Charter Ship crew");
		bucketOfSlime.setHighlightInInventory(true);

		modelShip = new ItemRequirement("Model ship", ItemID.MODEL_SHIP);
		modelShip.setHighlightInInventory(true);
		repairedShip = new ItemRequirement("Model ship", ItemID.MODEL_SHIP_4254);
		// Lobster piece
		mapPiece1 = new ItemRequirement("Map scrap", ItemID.MAP_SCRAP_4275);
		mapPiece2 = new ItemRequirement("Map scrap", ItemID.MAP_SCRAP);
		mapPiece3 = new ItemRequirement("Map scrap", ItemID.MAP_SCRAP_4276);
		map = new ItemRequirement("Treasure map", ItemID.TREASURE_MAP);
		bedsheet = new ItemRequirement("Bedsheet", ItemID.BEDSHEET);
		bedsheet.setHighlightInInventory(true);
		ectoSheets = new ItemRequirement("Bedsheet", ItemID.BEDSHEET_4285);
		ectoSheetsEquipped = new ItemRequirement("Bedsheet", ItemID.BEDSHEET_4285, 1, true);
		petition = new ItemRequirement("Petition form", ItemID.PETITION_FORM);
		boneKey = new ItemRequirement("Bone key", ItemID.BONE_KEY_4272);
		boneKeyHighlighted = new ItemRequirement("Bone key", ItemID.BONE_KEY_4272);
		boneKeyHighlighted.setHighlightInInventory(true);

		robes = new ItemRequirement("Mystical robes", ItemID.MYSTICAL_ROBES);
		book = new ItemRequirement("Book of haricanto", ItemID.BOOK_OF_HARICANTO);
		manual = new ItemRequirement("Translation manual", ItemID.TRANSLATION_MANUAL);
		manual.setTip("You can get another from Ak-Haranu");
		signedOakBow = new ItemRequirement("Signed oak bow", ItemID.SIGNED_OAK_BOW);
	}

	public void loadZones()
	{
		phas1 = new Zone(new WorldPoint(3653, 3457, 0), new WorldPoint(3710, 3507, 0));
		phas2 = new Zone(new WorldPoint(3669, 3508, 0), new WorldPoint(3710, 3510, 0));
		phas3 = new Zone(new WorldPoint(3672, 3511, 0), new WorldPoint(3710, 3513, 0));
		phas4 = new Zone(new WorldPoint(3675, 3514, 0), new WorldPoint(3710, 3516, 0));
		phas5 = new Zone(new WorldPoint(3684, 3517, 0), new WorldPoint(3693, 3532, 0));
		phas6 = new Zone(new WorldPoint(3661, 3454, 0), new WorldPoint(3674, 3456, 0));
		phas7 = new Zone(new WorldPoint(3675, 3456, 0), new WorldPoint(3709, 3456, 0));
		phas8 = new Zone(new WorldPoint(3694, 3454, 0), new WorldPoint(3706, 3455, 0));

		upstairsEcto = new Zone(new WorldPoint(3652, 3512, 1), new WorldPoint(3667, 3527, 1));

		topOfShip = new Zone(new WorldPoint(3616, 3541, 2), new WorldPoint(3622, 3545, 2));
		deck = new Zone(new WorldPoint(3600, 3541, 1), new WorldPoint(3623, 3545, 1));
		rocks = new Zone(new WorldPoint(3592, 3547, 0), new WorldPoint(3609, 3571, 0));
		dragontooth = new Zone(new WorldPoint(3775, 3520, 0), new WorldPoint(3836, 3581, 0));
	}

	public void setupConditions()
	{
		inPhas = new ZoneCondition(phas1, phas2, phas3, phas4, phas5, phas6, phas7, phas8);
		inUpstairsEcto = new ZoneCondition(upstairsEcto);
		onTopOfShip = new ZoneCondition(topOfShip);
		onDeck = new ZoneCondition(deck);
		onRocks = new ZoneCondition(rocks);
		onDragontooth = new ZoneCondition(dragontooth);

		hasCupOfMilkyTea = new ItemRequirementCondition(cupWithMilkyTea);
		hasCupOfTea = new ItemRequirementCondition(cupWithTea);
		hasMap = new ItemRequirementCondition(map);
		hasPiece1 = new ItemRequirementCondition(mapPiece1);
		hasPiece2 = new ItemRequirementCondition(mapPiece2);
		hasPiece3 = new ItemRequirementCondition(mapPiece3);
		hasModelShip = new ItemRequirementCondition(modelShip);
		hasRepairedShip = new ItemRequirementCondition(repairedShip);
		hasCup = new ItemRequirementCondition(cup);
		hasBook = new Conditions(LogicType.OR, new VarbitCondition(208, 1), new ItemRequirementCondition(book));
		hasManual = new Conditions(LogicType.OR, new VarbitCondition(206, 1), new VarbitCondition(212, 8));
		hasSheet = new ItemRequirementCondition(bedsheet);
		hasEctoSheet = new ItemRequirementCondition(ectoSheets);
		hasMysticalRobes = new Conditions(LogicType.OR, new VarbitCondition(207, 1), new ItemRequirementCondition(robes));
		hasSignedOakBow = new ItemRequirementCondition(signedOakBow);
		hasPetition = new ItemRequirementCondition(petition);
		hasSignatures = new VarbitCondition(209, 11, Operation.GREATER_EQUAL);
		givenPetitionToNecro = new VarbitCondition(209, 31, Operation.GREATER_EQUAL);
		hadChestKey = new Conditions(LogicType.OR, new ItemRequirementCondition(chestKey), new VarbitCondition(214, 2, Operation.GREATER_EQUAL));
		unlockedChest2 = new VarbitCondition(214, 3, Operation.GREATER_EQUAL);
		doorUnlocked = new VarbitCondition(213, 1);

		lobsterNearby = new NpcCondition(NpcID.GIANT_LOBSTER);
		killedLobster = new VarbitCondition(215, 1);

		boneKeyNearby = new ItemCondition(boneKey);
		hasBoneKey = new Conditions(LogicType.OR, new ItemRequirementCondition(boneKey), doorUnlocked);

		talkedToAkHaranu = new VarbitCondition(212, 1, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		enterPhas = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys.", ectoToken2, ghostspeak);
		talkToVelorina = new NpcStep(this, NpcID.VELORINA, new WorldPoint(3677, 3508, 0), "Talk to Velorina in east Port Phasmatys.", ghostspeak);
		talkToVelorina.addDialogSteps("Why, what is the matter?", "Yes, I do. It is a very sad story.", "Yes.");
		talkToNecrovarus = new NpcStep(this, NpcID.NECROVARUS, new WorldPoint(3660, 3516, 0), "Talk to Necrovarus at the Ectofuntus.", ghostspeak);
		enterPhasAfterNecro = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys.", ectoToken2, ghostspeak);
		talkToVelorinaAfterNecro = new NpcStep(this, NpcID.VELORINA, new WorldPoint(3677, 3508, 0), "Return to Velorina in east Port Phasmatys.", ghostspeak);
		talkToVelorinaAfterNecro.addSubSteps(enterPhasAfterNecro);
		talkToCrone = new NpcStep(this, NpcID.OLD_CRONE, new WorldPoint(3462, 3558, 0), "Talk to the Old Crone east of the Slayer Tower.", nettleTea, milk);
		useTeaOnCup = new DetailedQuestStep(this, "Use the nettle tea on the porcelain cup.", nettleTeaHighlighted, cup);
		useMilkOnTea = new DetailedQuestStep(this, "Use the bucket of milk on the porcelain cup.", milkHighlighted, cupWithTea);
		talkToCroneAgain = new NpcStep(this, NpcID.OLD_CRONE, new WorldPoint(3462, 3558, 0), "Talk to the Old Crone east of the Slayer Tower.", cupWithMilkyTea);
		talkToCroneAgainForShip = new NpcStep(this, NpcID.OLD_CRONE, new WorldPoint(3462, 3558, 0), "Talk to the Old Crone and offer to help.");
		talkToCroneAgainForShip.addDialogSteps("You are doing so much for me - is there anything I can do for you?", "I am afraid I have lost the boat you gave to me.");
		repairShip = new DetailedQuestStep(this, "Use the silk on the model ship.", silkHighlighted, needle, thread, knife, modelShip);

		searchChestForLobster = new ObjectStep(this, ObjectID.CLOSED_CHEST_16118, new WorldPoint(3618, 3542, 0), "Attempt to search the chest in the east of the hull of the ship west of Port Phasmatys. A giant lobster will spawn you need to kill.");
		((ObjectStep)(searchChestForLobster)).addAlternateObjects(ObjectID.OPEN_CHEST_16119);
		killLobster = new NpcStep(this, NpcID.GIANT_LOBSTER, "Kill the Giant Lobster.");
		searchChestAfterLobster = new ObjectStep(this, ObjectID.CLOSED_CHEST_16118, new WorldPoint(3618, 3542, 0), "Search the chest in the east of the hull again.");
		((ObjectStep)(searchChestAfterLobster)).addAlternateObjects(ObjectID.OPEN_CHEST_16119);

		dyeFlags = new DyeShipSteps(this);
		useKeyOnChest = new ObjectStep(this, ObjectID.CLOSED_CHEST_16116, new WorldPoint(3619, 3545, 1), "Use the key on the chest in the Captain's Room, then search it.", chestKey);
		useKeyOnChest.addIcon(ItemID.CHEST_KEY);
		openSecondChest = new ObjectStep(this, ObjectID.CLOSED_CHEST_16116, new WorldPoint(3619, 3545, 1), "Search the chest in the Captain's Room.");
		useKeyOnChest.addSubSteps(openSecondChest);

		goDownFromMast = new ObjectStep(this, ObjectID.SHIPS_LADDER_16112, new WorldPoint(3615, 3541, 2), "Go to the main deck of the ship.");
		goUpToDeck = new ObjectStep(this, ObjectID.SHIPS_LADDER_16111, new WorldPoint(3613, 3543, 0), "Go up the ladder in the ship west of Port Phasmatys.");
		goAcrossPlank = new ObjectStep(this, ObjectID.GANGPLANK_16651, new WorldPoint(3605, 3546, 1), "Go across the gangplank to the north.");
		goAcrossPlank.addSubSteps(goDownFromMast, goUpToDeck);

		openThirdChest = new ObjectStep(this, ObjectID.CLOSED_CHEST_16118, new WorldPoint(3606, 3564, 0), "Jump across the rocks to the chest and search it for a map piece.");
		((ObjectStep)(openThirdChest)).addAlternateObjects(ObjectID.OPEN_CHEST_16119);
		((ObjectStep)(openThirdChest)).setLinePoints(new ArrayList<>(Arrays.asList(
			new WorldPoint(3604, 3550, 0),
			new WorldPoint(3601, 3550, 0),
			new WorldPoint(3601, 3552, 0),
			new WorldPoint(3595, 3552, 0),
			new WorldPoint(3595, 3557, 0),
			new WorldPoint(3597, 3557, 0),
			new WorldPoint(3597, 3564, 0),
			new WorldPoint(3605, 3564, 0)
		)));

		useMapsTogether = new DetailedQuestStep(this, "Use the map pieces together.", mapPiece1, mapPiece2, mapPiece3);

		if (canUseCharos)
		{
			enterPhasForDigging = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys.", charos, ectoToken12, spade, map, ghostspeak);
			takeRowingBoat = new NpcStep(this, NpcID.GHOST_CAPTAIN, new WorldPoint(3703, 3487, 0), "Talk to the Ghost captain at the rowing boat on the docks with your Ring of Charos (e) equipped.", charos, ectoToken10, spade, map);
		}
		else
		{
			enterPhasForDigging = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys.", ectoToken27, spade, map, ghostspeak);
			takeRowingBoat = new NpcStep(this, NpcID.GHOST_CAPTAIN, new WorldPoint(3703, 3487, 0), "Travel on the rowing boat on the docks.", ectoToken25, spade, map, ghostspeak);
		}

		digForBook = new DigStep(this, new WorldPoint(3803, 3530, 0), "Dig on the south of the island for a book.", map);
		returnToPhas = new NpcStep(this, NpcID.GHOST_CAPTAIN, new WorldPoint(3791, 3559, 0), "Return to Port Phasmatys with the Ghost Captain.", ghostspeak);

		enterPhasForManual = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys.", ectoToken2, ghostspeak);
		talkToAkHaranu = new NpcStep(this, NpcID.AKHARANU, new WorldPoint(3689, 3499, 0), "Talk to Ak-Haranu.");
		talkToAkHaranu.addDialogStep("Okay, wait here - I'll get you your bow.");
		talkToAkHaranu.addSubSteps(enterPhasForManual);
		talkToRobin = new NpcStep(this, NpcID.ROBIN, new WorldPoint(3675, 3495, 0), "Talk to Robin the the Port Phasmatys pub. Keep playing runedraw until he agrees to sign your bow.", oakLongbow, coins400);
		talkToRobin.addDialogStep("Yes, I'll give you a game.");
		bringBowToAkHaranu = new NpcStep(this, NpcID.AKHARANU, new WorldPoint(3689, 3499, 0), "Bring the signed bow to Ak-Haranu.", signedOakBow);

		enterPhasForRobe = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys.", ectoToken2, ghostspeak);
		talkToInnkeeper = new NpcStep(this, NpcID.GHOST_INNKEEPER, new WorldPoint(3681, 3495, 0), "Talk to the Ghost Innkeeper.", ghostspeak);
		talkToInnkeeper.addDialogSteps("Do you have any jobs I can do?", "Yes, I'd be delighted.");
		talkToInnkeeper.addSubSteps(enterPhasForRobe);
		useSlimeOnSheet = new DetailedQuestStep(this, "Use a bucket of slime on the bedsheets.", bucketOfSlime, bedsheet);
		talkToGravingas = new NpcStep(this, NpcID.GRAVINGAS, new WorldPoint(3660, 3499, 0), "Talk to Gravingas wearing the bedsheet.", ectoSheetsEquipped, ghostspeak);
		talkToGravingas.addDialogStep("After hearing Velorina's story I will be happy to help out.");
		talkToVillagers = new NpcStep(this, NpcID.GHOST_VILLAGER, new WorldPoint(3662, 3497, 0), "Alternate talking with the ghost villagers until you have 10 signatures.", true, ghostspeak);
		showPetitionToNecro = new NpcStep(this, NpcID.NECROVARUS, new WorldPoint(3660, 3516, 0), "Bring the petition to Necrovarus at the Ectofuntus to cause him to drop a bone key.", petition, ghostspeak);
		talkToNecroForKey = new NpcStep(this, NpcID.NECROVARUS, new WorldPoint(3660, 3516, 0), "Talk to Necrovarus at the Ectofuntus until he drops a bone key.", ghostspeak);
		showPetitionToNecro.addSubSteps(talkToNecroForKey);
		takeKey = new ItemStep(this, "Pick up the bone key.", boneKey);
		goUpFromNecro = new ObjectStep(this, ObjectID.STAIRCASE_16646, new WorldPoint(3667, 3520, 0), "Go upstairs at the Ectofuntus.");
		useKeyOnDoor = new ObjectStep(this, ObjectID.DOOR_5244, new WorldPoint(3656, 3514, 1), "Use the key on the south room's door.", boneKeyHighlighted);
		useKeyOnDoor.addIcon(ItemID.BONE_KEY_4272);
		takeRobes = new ObjectStep(this, ObjectID.COFFIN_16644, new WorldPoint(3660, 3514, 1), "Search the coffin.");
		((ObjectStep)(takeRobes)).addAlternateObjects(ObjectID.COFFIN_16645);
		returnToCrone = new NpcStep(this, NpcID.OLD_CRONE, new WorldPoint(3462, 3558, 0), "Bring the items to the Old Crone east of the Slayer Tower.", robes, book, manual, ghostspeak);
		bringCroneAmulet = new NpcStep(this, NpcID.OLD_CRONE, new WorldPoint(3462, 3558, 0), "Bring a ghostspeak amulet to the Old Crone east of the Slayer Tower.", ghostspeak);
		returnToCrone.addSubSteps(bringCroneAmulet);
		talkToNecroAfterCurse = new NpcStep(this, NpcID.NECROVARUS, new WorldPoint(3660, 3516, 0), "Talk to Necrovarus at the Ectofuntus.", enchantedGhostspeakEquipped);
		talkToNecroAfterCurse.addDialogStep("Let any ghost who so wishes pass on into the next world.");
		enterPhasFinal = new ObjectStep(this, ObjectID.ENERGY_BARRIER_16105, new WorldPoint(3660, 3508, 0), "Enter Port Phasmatys. You don't need ecto-tokens for this any more.", ghostspeak);
		talkToVelorinaFinal = new NpcStep(this, NpcID.VELORINA, new WorldPoint(3677, 3508, 0), "Talk to Velorina in east Port Phasmatys to finish!", ghostspeak);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		if (canUseCharos)
		{
			return new ArrayList<>(Arrays.asList(ghostspeak, charos, ectoTokensCharos, coins400, nettleTea, milk, silk, knife, needle, thread, dyes, spade, oakLongbow, bucketOfSlime));
		}
		else
		{
			return new ArrayList<>(Arrays.asList(ghostspeak, ectoTokensNoCharos, coins400, nettleTea, milk, silk, knife, needle, thread, dyes, spade, oakLongbow, bucketOfSlime));
		}
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Giant lobster (level 32) (safespottable)"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Start the quest", new ArrayList<>(Arrays.asList(enterPhas, talkToVelorina, talkToNecrovarus, talkToVelorinaAfterNecro)), ghostspeak, ectoToken4));
		allSteps.add(new PanelDetails("Getting help", new ArrayList<>(Arrays.asList(talkToCrone, useTeaOnCup, useMilkOnTea, talkToCroneAgain, talkToCroneAgainForShip)), ghostspeak, nettleTea, milk));

		ArrayList<QuestStep> mapSteps = new ArrayList<>(Arrays.asList(repairShip, searchChestForLobster, killLobster, searchChestAfterLobster));
		mapSteps.addAll(dyeFlags.getDisplaySteps());
		mapSteps.addAll(new ArrayList<>(Arrays.asList(useKeyOnChest, goAcrossPlank, openThirdChest, useMapsTogether, enterPhasForDigging, takeRowingBoat, digForBook, returnToPhas)));
		PanelDetails bookPanel;
		if (canUseCharos)
		{
			bookPanel = new PanelDetails("Finding the book", mapSteps, ghostspeak, silk, needle, thread, knife, dyes, spade, ectoToken12, charos);
		}
		else
		{
			bookPanel = new PanelDetails("Finding the book", mapSteps, ghostspeak, silk, needle, thread, knife, dyes, spade, ectoToken27);
		}
		bookPanel.setLockingStep(getBookSteps);
		allSteps.add(bookPanel);

		PanelDetails manualPanel = new PanelDetails("Finding the manual", new ArrayList<>(Arrays.asList(talkToAkHaranu, talkToRobin, bringBowToAkHaranu)), coins400, oakLongbow);
		manualPanel.setLockingStep(getManualSteps);
		allSteps.add(manualPanel);

		PanelDetails robePanel = new PanelDetails("Finding the robes",
			new ArrayList<>(Arrays.asList(talkToInnkeeper, useSlimeOnSheet, talkToGravingas, talkToVillagers, showPetitionToNecro, goUpFromNecro, useKeyOnDoor, takeRobes)), ghostspeak, bucketOfSlime);
		robePanel.setLockingStep(getRobesSteps);
		allSteps.add(robePanel);

		allSteps.add(new PanelDetails("Undoing the curse", new ArrayList<>(Arrays.asList(returnToCrone, talkToNecroAfterCurse, talkToVelorinaFinal)), ghostspeak, book, manual, robes));

		return allSteps;
	}
}
