/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.forgettabletale;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.WidgetTextRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.WidgetStep;
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
	quest = QuestHelperQuest.FORGETTABLE_TALE
)
public class ForgettableTale extends BasicQuestHelper
{
	// Required
	ItemRequirement coins500, barleyMalt2, bucketOfWater2, spade, dibber, rake, yeast, kebab, beer, dwarvenStout,
		beerGlass, randomItem, coins, pot;

	// Recommended
	ItemRequirement grandExchangeTeleport;

	// Quest items
	ItemRequirement keldaSeed, keldaHop, keldaStout, ticketToKelda, ticketToWWM;

	Requirement inKelgdagrim, inWolfUnderground, inPubUpstairs, inConsortium, inPuzzleRoom, givenBeerToDrunkenDwarf,
		rowdyDwarfMadeRequest, gotRowdySeed, gotKhorvakSeed, gotGaussSeed, plotRaked, keldaGrowing, keldaGrown,
		addedWater, addedYeast, addedHop, addedMalt, keldaBrewed, keldaInBarrel, handsFree, shieldFree;

	Requirement inPurple, inYellow, inBlue, inGreen, inSilver, inWhite, inBrown;

	Requirement inPuzzle, searchedBox1, donePuzzle1P1, donePuzzle1P2, inPuzzle1Room, searchedPuzzle1Box;

	Requirement donePuzzle2P1, donePuzzle2P2, donePuzzle2P3, inPuzzle2Room, searchedPuzzle258Box;

	Requirement donePuzzle3P1, donePuzzle3P2, donePuzzle3P3, donePuzzle3P4;

	Requirement inListeningRoom1;

	Requirement inRoom2PuzzleWidget, donePuzzle4P1, donePuzzle4P2, donePuzzle4P3, searchedPuzzle147Box;
	Requirement donePuzzle5P1, donePuzzle5P2, donePuzzle5P3, donePuzzle5P4, inPuzzle5Room;
	Requirement donePuzzle6P1, donePuzzle6P2, donePuzzle6P3, donePuzzle6P4, donePuzzle6P5;

	Requirement inLibrary, readBook, readCrate1, readCrate2;

	Requirement inRoom3PuzzleWidget, donePuzzle7P1, donePuzzle7P2, donePuzzle7P3, donePuzzle7P4, inPuzzle7Room;
	Requirement donePuzzle8P1, donePuzzle8P2, donePuzzle8P3, donePuzzle8P4, donePuzzle8P5, donePuzzle8P6, inPuzzle8Room;
	Requirement donePuzzle9P1, donePuzzle9P2, donePuzzle9P3, donePuzzle9P4, donePuzzle9P5, donePuzzle9P6,
		donePuzzle9P7, donePuzzle9P8;

	QuestStep travelToKeldagrim, talkToVeldaban, talkToDrunkDwarf;

	QuestStep talkToRowdyDwarf, giveRowdyDwarfItems, getWWMTicket, travelToWWM, talkToKhorvak, useStoutOnKhorvak,
		getKeldaTicket, takeCartFromWWMToKelda, talkToGauss;

	QuestStep talkToRind, rakeKelda, plantKelda, waitForKelda, harvestHops, goUpstairsPub, buyYeast, addWater,
		pickupPot, addMalts, addKelda, addYeast, waitBrewing, turnValve, useGlassOnBarrel, goDownFromPub;

	QuestStep talkToCartConductor, goUpToDirector, talkToDirector, goDownFromDirector, takeSecretCart;

	QuestStep searchBox1, startPuzzle1, puzzle1P1, puzzle1P2, puzzle1Ok, takePuzzle1Cart, searchPuzzle1Box,
		returnFromPuzzle1;
	QuestStep startPuzzle2, puzzle2P1, puzzle2P2, puzzle2P3, puzzle2Ok, takePuzzle2Cart, searchPuzzle2Box,
		returnFromPuzzle2;
	QuestStep startPuzzle3, puzzle3P1, puzzle3P2, puzzle3P3, puzzle3P4, puzzle3Ok, takePuzzle3Cart;

	QuestStep leaveListeningRoom1;

	QuestStep searchBox2, startPuzzle4, puzzle4P1, puzzle4P2, puzzle4P3, puzzle4Ok, takePuzzle4Cart,
		searchPuzzle4Box, returnFromPuzzle4;
	QuestStep startPuzzle5, puzzle5P1, puzzle5P2, puzzle5P3, puzzle5P4, puzzle5Ok, takePuzzle5Cart,
		searchPuzzle5Box, returnFromPuzzle5;
	QuestStep startPuzzle6, puzzle6P1, puzzle6P2, puzzle6P3, puzzle6P4, puzzle6P5, puzzle6Ok, takePuzzle6Cart;

	QuestStep searchBookcase, searchCrate1, searchCrate2, leaveLibrary;

	QuestStep searchBox3, startPuzzle7, puzzle7P1, puzzle7P2, puzzle7P3, puzzle7P4, puzzle7Ok, takePuzzle7Cart,
		searchPuzzle7Box, returnFromPuzzle7;
	QuestStep startPuzzle8, puzzle8P1, puzzle8P2, puzzle8P3, puzzle8P4, puzzle8P5, puzzle8P6, puzzle8Ok,
		takePuzzle8Cart, searchPuzzle8Box, returnFromPuzzle8;
	QuestStep startPuzzle9, puzzle9P1, puzzle9P2, puzzle9P3, puzzle9P4, puzzle9P5, puzzle9P6, puzzle9P7, puzzle9P8,
		puzzle9Ok, takePuzzle9Cart;

	QuestStep watchCutscene, eatKebab;

	ConditionalStep goTalkVeldaban, goTalkDrunkDwarf, goTalkDrunkDwarfAgain, goTalkDrunkDwarfAfterGivingBeer;

	ConditionalStep goTalkRowdyDwarf, goTalkToKhorvak, goGiveKhorvakBeer, goTalkToGuass;

	ConditionalStep goTalkToRind, goPlantKelda, goHarvestKelda, goBrew,
		goTurnValve, goUseGlass, goGiveDrunkenDwarfKelda, goTalkToDrunkenDwarfAfterKelda;

	ConditionalStep goTalkToConductor, goTalkToDirector, goTakeSecretCart;

	ConditionalStep goReturnToVeldaban, goEatKebab;

	Zone keldagrim, wolfUnderground, pubUpstairs, consortium, puzzleRoom, puzzleSmallPlatform,
		puzzleMediumPlatform, listeningRoom1, puzzle5Room, library;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, goTalkVeldaban);
		steps.put(10, goTalkDrunkDwarf);

		ConditionalStep drunkenDwarfBeering = new ConditionalStep(this, goTalkDrunkDwarfAgain);
		drunkenDwarfBeering.addStep(givenBeerToDrunkenDwarf, goTalkDrunkDwarfAfterGivingBeer);
		steps.put(20, drunkenDwarfBeering);

		ConditionalStep goGetSeeds = new ConditionalStep(this, goTalkRowdyDwarf);
		goGetSeeds.addStep(new Conditions(gotRowdySeed, gotKhorvakSeed, gotGaussSeed), goTalkToRind);
		goGetSeeds.addStep(new Conditions(gotRowdySeed, gotKhorvakSeed), goTalkToGuass);
		goGetSeeds.addStep(gotRowdySeed, goTalkToKhorvak);
		steps.put(30, goGetSeeds);

		ConditionalStep growKelda = new ConditionalStep(this, goPlantKelda);
		growKelda.addStep(keldaGrown, goHarvestKelda);
		growKelda.addStep(keldaGrowing, waitForKelda);
		steps.put(40, growKelda);

		ConditionalStep brewing = new ConditionalStep(this, goBrew);
		brewing.addStep(new Conditions(keldaInBarrel), goUseGlass);
		brewing.addStep(new Conditions(keldaBrewed), goTurnValve);
		brewing.addStep(new Conditions(addedYeast), waitBrewing);
		brewing.addStep(new Conditions(inPubUpstairs, yeast, addedHop), addYeast);
		brewing.addStep(new Conditions(inPubUpstairs, yeast, addedMalt), addKelda);
		brewing.addStep(new Conditions(inPubUpstairs, yeast, addedWater), addMalts);
		brewing.addStep(new Conditions(inPubUpstairs, yeast), addWater);
		brewing.addStep(new Conditions(inPubUpstairs, pot), buyYeast);
		brewing.addStep(inPubUpstairs, pickupPot);
		steps.put(50, brewing);

		steps.put(60, goGiveDrunkenDwarfKelda);
		steps.put(65, goTalkToDrunkenDwarfAfterKelda);
		steps.put(70, goTalkToConductor);
		steps.put(80, goTalkToDirector);
		// 90 skipped?

		ConditionalStep goDoPuzzle1 = new ConditionalStep(this, goTakeSecretCart);
		goDoPuzzle1.addStep(new Conditions(inPuzzle1Room), searchPuzzle1Box);
		goDoPuzzle1.addStep(new Conditions(inPuzzle, donePuzzle1P1, donePuzzle1P2), puzzle1Ok);
		goDoPuzzle1.addStep(new Conditions(donePuzzle1P1, donePuzzle1P2), takePuzzle1Cart);
		goDoPuzzle1.addStep(new Conditions(inPuzzle, donePuzzle1P1), puzzle1P2);
		goDoPuzzle1.addStep(new Conditions(inPuzzle), puzzle1P1);
		goDoPuzzle1.addStep(new Conditions(inPuzzleRoom, searchedBox1), startPuzzle1);
		goDoPuzzle1.addStep(inPuzzleRoom, searchBox1);

		ConditionalStep goDoPuzzle2 = new ConditionalStep(this, startPuzzle2);
		goDoPuzzle2.addStep(new Conditions(inPuzzle2Room), searchPuzzle2Box);
		goDoPuzzle2.addStep(new Conditions(inPuzzle, donePuzzle2P1, donePuzzle2P2, donePuzzle2P3), puzzle2Ok);
		goDoPuzzle2.addStep(new Conditions(donePuzzle2P1, donePuzzle2P2, donePuzzle2P3), takePuzzle2Cart);
		goDoPuzzle2.addStep(new Conditions(inPuzzle, donePuzzle2P1, donePuzzle2P2), puzzle2P3);
		goDoPuzzle2.addStep(new Conditions(inPuzzle, donePuzzle2P1), puzzle2P2);
		goDoPuzzle2.addStep(inPuzzle, puzzle2P1);
		goDoPuzzle2.addStep(inPuzzle1Room, returnFromPuzzle1);

		ConditionalStep goDoPuzzle3 = new ConditionalStep(this, startPuzzle3);
		goDoPuzzle3.addStep(new Conditions(inPuzzle, donePuzzle3P1, donePuzzle3P2, donePuzzle3P3, donePuzzle3P4),
			puzzle3Ok);
		goDoPuzzle3.addStep(new Conditions(donePuzzle3P1, donePuzzle3P2, donePuzzle3P3, donePuzzle3P4),
			takePuzzle3Cart);
		goDoPuzzle3.addStep(new Conditions(inPuzzle, donePuzzle3P1, donePuzzle3P2, donePuzzle3P3), puzzle3P4);
		goDoPuzzle3.addStep(new Conditions(inPuzzle, donePuzzle3P1, donePuzzle3P2), puzzle3P3);
		goDoPuzzle3.addStep(new Conditions(inPuzzle, donePuzzle3P1), puzzle3P2);
		goDoPuzzle3.addStep(inPuzzle, puzzle3P1);
		goDoPuzzle3.addStep(new Conditions(inPuzzle2Room), returnFromPuzzle2);

		ConditionalStep puzzles = new ConditionalStep(this, goDoPuzzle1);
		puzzles.addStep(searchedPuzzle258Box, goDoPuzzle3);
		puzzles.addStep(searchedPuzzle1Box, goDoPuzzle2);
		steps.put(100, puzzles);

		ConditionalStep listening1 = new ConditionalStep(this, goTakeSecretCart);
		listening1.addStep(inListeningRoom1, leaveListeningRoom1);
		steps.put(105, listening1);

		ConditionalStep goDoPuzzle4 = new ConditionalStep(this, searchBox2);
		goDoPuzzle4.addStep(inPuzzle1Room, searchPuzzle4Box);
		goDoPuzzle4.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle4P1, donePuzzle4P2, donePuzzle4P3),
			puzzle4Ok);
		goDoPuzzle4.addStep(new Conditions(donePuzzle4P1, donePuzzle4P2, donePuzzle4P3), takePuzzle4Cart);
		goDoPuzzle4.addStep(new Conditions(inRoom2PuzzleWidget, searchedBox1, donePuzzle4P1, donePuzzle4P2), puzzle4P3);
		goDoPuzzle4.addStep(new Conditions(inRoom2PuzzleWidget, searchedBox1, donePuzzle4P1), puzzle4P2);
		goDoPuzzle4.addStep(new Conditions(inRoom2PuzzleWidget, searchedBox1), puzzle4P1);
		goDoPuzzle4.addStep(searchedBox1, startPuzzle4);

		ConditionalStep goDoPuzzle5 = new ConditionalStep(this, startPuzzle5);
		goDoPuzzle5.addStep(inPuzzle1Room, returnFromPuzzle4);

		goDoPuzzle5.addStep(inPuzzle5Room, searchPuzzle5Box);
		goDoPuzzle5.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle5P1, donePuzzle5P2, donePuzzle5P3, donePuzzle5P4),
			puzzle5Ok);
		goDoPuzzle5.addStep(new Conditions(donePuzzle5P1, donePuzzle5P2, donePuzzle5P3, donePuzzle5P4),
			takePuzzle5Cart);
		goDoPuzzle5.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle5P1, donePuzzle5P2, donePuzzle5P3), puzzle5P4);
		goDoPuzzle5.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle5P1, donePuzzle5P2), puzzle5P3);
		goDoPuzzle5.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle5P1), puzzle5P2);
		goDoPuzzle5.addStep(new Conditions(inRoom2PuzzleWidget), puzzle5P1);

		ConditionalStep goDoPuzzle6 = new ConditionalStep(this, startPuzzle6);
		goDoPuzzle6.addStep(inPuzzle5Room, returnFromPuzzle5);

		goDoPuzzle6.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle6P1, donePuzzle6P2, donePuzzle6P3, donePuzzle6P4,
				donePuzzle6P5), puzzle6Ok);
		goDoPuzzle6.addStep(new Conditions(donePuzzle6P1, donePuzzle6P2, donePuzzle6P3, donePuzzle6P4, donePuzzle6P5),
			takePuzzle6Cart);
		goDoPuzzle6.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle6P1, donePuzzle6P2, donePuzzle6P3, donePuzzle6P4),
			puzzle6P5);
		goDoPuzzle6.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle6P1, donePuzzle6P2, donePuzzle6P3), puzzle6P4);
		goDoPuzzle6.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle6P1, donePuzzle6P2), puzzle6P3);
		goDoPuzzle6.addStep(new Conditions(inRoom2PuzzleWidget, donePuzzle6P1), puzzle6P2);
		goDoPuzzle6.addStep(new Conditions(inRoom2PuzzleWidget), puzzle6P1);

		ConditionalStep puzzles2 = new ConditionalStep(this, goTakeSecretCart);
		puzzles2.addStep(new Conditions(inPuzzleRoom, searchedPuzzle258Box), goDoPuzzle6);
		puzzles2.addStep(new Conditions(inPuzzleRoom, searchedPuzzle147Box), goDoPuzzle5);
		puzzles2.addStep(inPuzzleRoom, goDoPuzzle4);
		puzzles2.addStep(inListeningRoom1, leaveListeningRoom1);
		steps.put(110, puzzles2);

		ConditionalStep librarySteps = new ConditionalStep(this, goTakeSecretCart);
		librarySteps.addStep(new Conditions(inLibrary, readBook, readCrate1, readCrate2), leaveLibrary);
		librarySteps.addStep(new Conditions(inLibrary, readBook, readCrate1), searchCrate2);
		librarySteps.addStep(new Conditions(inLibrary, readBook), searchCrate1);
		librarySteps.addStep(inLibrary, searchBookcase);
		steps.put(115, librarySteps);

		ConditionalStep goDoPuzzle7 = new ConditionalStep(this, searchBox3);
		goDoPuzzle7.addStep(inPuzzle7Room, searchPuzzle7Box);
		goDoPuzzle7.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle7P1, donePuzzle7P2, donePuzzle7P3,
			donePuzzle7P4),	puzzle7Ok);
		goDoPuzzle7.addStep(new Conditions(donePuzzle7P1, donePuzzle7P2, donePuzzle7P3,
			donePuzzle7P4),	takePuzzle7Cart);
		goDoPuzzle7.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle7P1, donePuzzle7P2, donePuzzle7P3),
			puzzle7P4);
		goDoPuzzle7.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle7P1, donePuzzle7P2), puzzle7P3);
		goDoPuzzle7.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle7P1), puzzle7P2);
		goDoPuzzle7.addStep(new Conditions(inRoom3PuzzleWidget), puzzle7P1);
		goDoPuzzle7.addStep(new Conditions(searchedBox1), startPuzzle7);

		ConditionalStep goDoPuzzle8 = new ConditionalStep(this, startPuzzle8);
		goDoPuzzle8.addStep(inPuzzle7Room, returnFromPuzzle7);

		goDoPuzzle8.addStep(inPuzzle8Room, searchPuzzle8Box);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle8P1, donePuzzle8P2, donePuzzle8P3,
				donePuzzle8P4, donePuzzle8P5, donePuzzle8P6), puzzle8Ok);
		goDoPuzzle8.addStep(new Conditions(donePuzzle8P1, donePuzzle8P2, donePuzzle8P3, donePuzzle8P4, donePuzzle8P5,
				donePuzzle8P6), takePuzzle8Cart);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle8P1, donePuzzle8P2, donePuzzle8P3,
			donePuzzle8P4, donePuzzle8P5), puzzle8P6);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle8P1, donePuzzle8P2, donePuzzle8P3,
				donePuzzle8P4), puzzle8P5);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle8P1, donePuzzle8P2, donePuzzle8P3),
			puzzle8P4);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle8P1, donePuzzle8P2), puzzle8P3);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle8P1), puzzle8P2);
		goDoPuzzle8.addStep(new Conditions(inRoom3PuzzleWidget), puzzle8P1);

		ConditionalStep goDoPuzzle9 = new ConditionalStep(this, startPuzzle9);
		goDoPuzzle9.addStep(inPuzzle8Room, returnFromPuzzle8);

		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2, donePuzzle9P3,
			donePuzzle9P4, donePuzzle9P5, donePuzzle9P6, donePuzzle9P7, donePuzzle9P8), puzzle9Ok);
		goDoPuzzle9.addStep(new Conditions(donePuzzle9P1, donePuzzle9P2, donePuzzle9P3, donePuzzle9P4, donePuzzle9P5,
			donePuzzle9P6, donePuzzle9P7, donePuzzle9P8), takePuzzle9Cart);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2, donePuzzle9P3,
			donePuzzle9P4, donePuzzle9P5, donePuzzle9P6, donePuzzle9P7), puzzle9P8);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2, donePuzzle9P3,
			donePuzzle9P4, donePuzzle9P5, donePuzzle9P6), puzzle9P7);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2, donePuzzle9P3,
			donePuzzle9P4, donePuzzle9P5), puzzle9P6);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2, donePuzzle9P3,
			donePuzzle9P4), puzzle9P5);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2, donePuzzle9P3),
			puzzle9P4);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1, donePuzzle9P2), puzzle9P3);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget, donePuzzle9P1), puzzle9P2);
		goDoPuzzle9.addStep(new Conditions(inRoom3PuzzleWidget), puzzle9P1);

		ConditionalStep puzzles3 = new ConditionalStep(this, goTakeSecretCart);
		puzzles3.addStep(new Conditions(inPuzzleRoom, searchedPuzzle258Box), goDoPuzzle9);
		puzzles3.addStep(new Conditions(inPuzzleRoom, searchedPuzzle147Box), goDoPuzzle8);
		puzzles3.addStep(inPuzzleRoom, goDoPuzzle7);
		steps.put(118, puzzles3);

		steps.put(119, watchCutscene);

		steps.put(120, goReturnToVeldaban);

		steps.put(130, goEatKebab);

		return steps;
	}

	public void setupRequirements()
	{
		coins500 = new ItemRequirement("Coins", ItemID.COINS_995, 500);
		coins = new ItemRequirement("Coins", ItemID.COINS_995);
		barleyMalt2 = new ItemRequirement("Barley malt", ItemID.BARLEY_MALT, 2);
		bucketOfWater2 = new ItemRequirement("Bucket of water", ItemID.BUCKET_OF_WATER, 2);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		dibber = new ItemRequirement("Seed dibber", ItemID.SEED_DIBBER);
		rake = new ItemRequirement("Rake", ItemID.RAKE);
		yeast = new ItemRequirement("Ale yeast", ItemID.ALE_YEAST);
		kebab = new ItemRequirement("Kebab", ItemID.KEBAB);
		kebab.setTooltip("You can buy one for 1 coin in the food shop northeast in Keldagrim");
		beer = new ItemRequirement("Beer", ItemID.BEER);
		beer.setTooltip("You can buy these from either of the bars in Keldagrim for 2 coins");
		dwarvenStout = new ItemRequirement("Dwarven stout", ItemID.DWARVEN_STOUT);
		beerGlass = new ItemRequirement("Beer glass", ItemID.BEER_GLASS);
		randomItem = new ItemRequirement("A random item per player", -1, -1);
		pot = new ItemRequirement("Pot", ItemID.POT);
		pot.setTooltip("You can pick up the pot next to the vat");

		grandExchangeTeleport = new ItemRequirement("Grand exchange teleport to access the Mine Cart from there",
			ItemID.VARROCK_TELEPORT);

		keldaSeed = new ItemRequirement("Kelda seed", ItemID.KELDA_SEED);
		keldaSeed.setTooltip("You can get any missing seeds from the Drunken Dwarf in north east Keldagrim");
		keldaHop = new ItemRequirement("Kelda hops", ItemID.KELDA_HOPS);
		keldaHop.setTooltip("You can get another from Rind in Keldagrim");
		keldaStout = new ItemRequirement("Kelda stout", ItemID.KELDA_STOUT);
		keldaStout.setTooltip("You can get another from the barmaid in Keldagrim's east pub");
		ticketToKelda = new ItemRequirement("Minecart ticket", ItemID.MINECART_TICKET_5023);
		ticketToWWM = new ItemRequirement("Minecart ticket", ItemID.MINECART_TICKET_5022);
	}

	public void loadZones()
	{
		keldagrim = new Zone(new WorldPoint(2816, 10112, 0), new WorldPoint(2950, 10239, 3));
		pubUpstairs = new Zone(new WorldPoint(2907, 10187, 1), new WorldPoint(2919, 10197, 1));
		wolfUnderground = new Zone(new WorldPoint(2816, 9856, 0), new WorldPoint(2880, 9890, 0));
		consortium = new Zone(new WorldPoint(2861, 10186, 1), new WorldPoint(2897, 10212, 1));
		puzzleRoom = new Zone(new WorldPoint(1859, 4931, 0), new WorldPoint(1918, 4989, 3));
		puzzleSmallPlatform = new Zone(new WorldPoint(1896, 4984, 3), new WorldPoint(1899, 4987, 3));
		puzzleMediumPlatform = new Zone(new WorldPoint(1904, 4984, 3), new WorldPoint(1908, 4988, 3));

		listeningRoom1 = new Zone(new WorldPoint(1865, 4975, 2), new WorldPoint(1890, 4991, 2));
		puzzle5Room = new Zone(new WorldPoint(1904, 4984, 3), new WorldPoint(1908, 4988, 3));
		library = new Zone(new WorldPoint(1897, 4958, 2), new WorldPoint(1913, 4970, 2));
	}

	public void setupConditions()
	{
		inKelgdagrim = new ZoneRequirement(keldagrim);
		inPubUpstairs = new ZoneRequirement(pubUpstairs);
		inWolfUnderground = new ZoneRequirement(wolfUnderground);
		inConsortium = new ZoneRequirement(consortium);
		inPuzzleRoom = new ZoneRequirement(puzzleRoom);
		inListeningRoom1 = new ZoneRequirement(listeningRoom1);

		ObjectCondition box1Nearby = new ObjectCondition(ObjectID.BOX, new WorldPoint(1898, 4985, 3));
		box1Nearby.setMaxDistanceFromPlayer(5);
		inPuzzle1Room = new Conditions(
			new ZoneRequirement(puzzleSmallPlatform),
			box1Nearby
		);

		ObjectCondition box2Nearby = new ObjectCondition(ObjectID.BOX, new WorldPoint(1897, 4986, 3));
		box2Nearby.setMaxDistanceFromPlayer(5);
		inPuzzle2Room = new Conditions(
			new ZoneRequirement(puzzleSmallPlatform),
			box2Nearby
		);

		ObjectCondition box7Nearby = new ObjectCondition(ObjectID.BOX, new WorldPoint(1906, 4987, 3));
		box7Nearby.setMaxDistanceFromPlayer(5);
		inPuzzle7Room = new Conditions(
			new ZoneRequirement(puzzleMediumPlatform),
			box7Nearby
		);

		ObjectCondition box8Nearby = new ObjectCondition(ObjectID.BOX, new WorldPoint(1898, 4986, 3));
		box8Nearby.setMaxDistanceFromPlayer(5);
		inPuzzle8Room = new Conditions(
			new ZoneRequirement(puzzleSmallPlatform),
			box8Nearby
		);

		// Part way through veldeban dialog, told about drunken dwarf:
		// 824 = 1

		givenBeerToDrunkenDwarf = new VarbitRequirement(838, 1);
		rowdyDwarfMadeRequest = new VarbitRequirement(829, 1);
		gotRowdySeed = new VarbitRequirement(826, 1);
		gotGaussSeed = new VarbitRequirement(827, 1);
		gotKhorvakSeed = new VarbitRequirement(828, 1);
		// 830 = 1, talked a bit to gauss
		// 831 = 1, asked about seed from khorvak

		plotRaked = new VarbitRequirement(823, 3, Operation.GREATER_EQUAL);
		keldaGrowing = new VarbitRequirement(823, 4, Operation.GREATER_EQUAL);
		keldaGrown = new VarbitRequirement(823, 8, Operation.GREATER_EQUAL);
		// 832 = 1, agreed to deliver letter for farmer

		addedWater = new VarbitRequirement(736, 1, Operation.GREATER_EQUAL);
		addedMalt = new VarbitRequirement(736, 2, Operation.GREATER_EQUAL);
		addedHop = new VarbitRequirement(736, 68, Operation.GREATER_EQUAL);
		addedYeast = new VarbitRequirement(736, 69, Operation.GREATER_EQUAL);
		keldaBrewed = new VarbitRequirement(736, 71, Operation.GREATER_EQUAL);
		keldaInBarrel = new VarbitRequirement(738, 3, Operation.GREATER_EQUAL);

		inPurple = new VarbitRequirement(578, 1); // Purple Pewter
		inYellow = new VarbitRequirement(578, 2); // Yellow Fortune
		inBlue = new VarbitRequirement(578, 3); // Blue Opal
		inGreen = new VarbitRequirement(578, 4); // Green Gem
		inWhite = new VarbitRequirement(578, 5); // White Chisel
		inSilver = new VarbitRequirement(578, 6); // Silver Cog
		inBrown = new VarbitRequirement(578, 7); // Brown Engine

		handsFree = new NoItemRequirement("No weapon equipped", ItemSlots.WEAPON);
		shieldFree = new NoItemRequirement("No shield equipped", ItemSlots.SHIELD);

		// 837 = 1, entered first puzzle
		// 839 = 1, cutscene entering done
		// 863 = 1, Searched box

		//
		searchedBox1 = new VarbitRequirement(863, 1);

		// 862, 861 are number of green/yellow
		inPuzzle = new WidgetTextRequirement(248, 53, "Ok");
		donePuzzle1P1 = new VarbitRequirement(842, 2);
		donePuzzle1P2 = new VarbitRequirement(844, 1);
		searchedPuzzle1Box = new VarbitRequirement(864, 1);

		// Puzzle 2
		donePuzzle2P1 = new VarbitRequirement(842, 1);
		donePuzzle2P2 = new VarbitRequirement(843, 2);
		donePuzzle2P3 = new VarbitRequirement(846, 1);
		searchedPuzzle258Box = new VarbitRequirement(865, 1);

		// Puzzle 3
		donePuzzle3P1 = new VarbitRequirement(842, 2);
		donePuzzle3P2 = new VarbitRequirement(844, 2);
		donePuzzle3P3 = new VarbitRequirement(847, 1);
		donePuzzle3P4 = new VarbitRequirement(848, 1);

		// Puzzle 4
		inRoom2PuzzleWidget = new WidgetTextRequirement(244, 73, "Ok");
		donePuzzle4P1 = new VarbitRequirement(842, 1);
		donePuzzle4P2 = new VarbitRequirement(843, 2);
		donePuzzle4P3 = new VarbitRequirement(846, 2);
		searchedPuzzle147Box = new VarbitRequirement(864, 1);

		// Puzzle 5
		donePuzzle5P1 = new VarbitRequirement(842, 2);
		donePuzzle5P2 = new VarbitRequirement(844, 2);
		donePuzzle5P3 = new VarbitRequirement(847, 1);
		donePuzzle5P4 = new VarbitRequirement(852, 1);
		inPuzzle5Room = new ZoneRequirement(puzzle5Room);

		// Puzzle 6
		donePuzzle6P1 = new VarbitRequirement(842, 1);
		donePuzzle6P2 = new VarbitRequirement(843, 1);
		donePuzzle6P3 = new VarbitRequirement(845, 2);
		donePuzzle6P4 = new VarbitRequirement(851, 1);
		donePuzzle6P5 = new VarbitRequirement(853, 2);

		inLibrary = new ZoneRequirement(library);
		readBook = new VarbitRequirement(833, 1);
		readCrate1 = new VarbitRequirement(834, 1);
		readCrate2 = new VarbitRequirement(835, 1);

		// Puzzle 7
		inRoom3PuzzleWidget = new WidgetTextRequirement(247, 108, "Ok");
		donePuzzle7P1 = new VarbitRequirement(842, 1);
		donePuzzle7P2 = new VarbitRequirement(843, 1);
		donePuzzle7P3 = new VarbitRequirement(845, 2);
		donePuzzle7P4 = new VarbitRequirement(847, 2);

		// Puzzle 8
		donePuzzle8P1 = new VarbitRequirement(842, 2);
		donePuzzle8P2 = new VarbitRequirement(844, 2);
		donePuzzle8P3 = new VarbitRequirement(846, 2);
		donePuzzle8P4 = new VarbitRequirement(848, 1);
		donePuzzle8P5 = new VarbitRequirement(850, 1);
		donePuzzle8P6 = new VarbitRequirement(853, 1);

		// Puzzle 9
		donePuzzle9P1 = new VarbitRequirement(842, 1);
		donePuzzle9P2 = new VarbitRequirement(843, 1);
		donePuzzle9P3 = new VarbitRequirement(845, 2);
		donePuzzle9P4 = new VarbitRequirement(847, 1);
		donePuzzle9P5 = new VarbitRequirement(849, 2);
		donePuzzle9P6 = new VarbitRequirement(852, 1);
		donePuzzle9P7 = new VarbitRequirement(855, 2);
		donePuzzle9P8 = new VarbitRequirement(858, 2);
	}

	public void setupSteps()
	{
		travelToKeldagrim = new ObjectStep(this, ObjectID.TRAPDOOR_16168, new WorldPoint(3140, 3504, 0),
			"Travel to Keldagrim.");
		talkToVeldaban = new NpcStep(this, NpcID.COMMANDER_VELDABAN_6045, new WorldPoint(2827, 10214, 0),
			"");
		talkToVeldaban.addDialogSteps("Very interested!", "Yes.", "Sounds like just the job for me!");
		talkToDrunkDwarf = new NpcStep(this, NpcID.DRUNKEN_DWARF_2408, new WorldPoint(2913, 10221, 0),
			"");
		talkToDrunkDwarf.addDialogStep("I need to know about the Red Axe...");

		talkToRowdyDwarf = new NpcStep(this, NpcID.ROWDY_DWARF, new WorldPoint(2914, 10198, 0),
			"");
		giveRowdyDwarfItems = new NpcStep(this, NpcID.ROWDY_DWARF, new WorldPoint(2914, 10198, 0),
			"");
		getWWMTicket = new NpcStep(this, NpcID.CART_CONDUCTOR_2388, new WorldPoint(2906, 10172, 0),
			"Get a ticket to White Wolf Mountain from a cart conductor.");
		getWWMTicket.addDialogSteps("I'd like to buy a ticket.", "To White Wolf Mountain.", "Buy.");
		travelToWWM = new ObjectStep(this, ObjectID.TRAIN_CART_7028, new WorldPoint(2919, 10169, 0),
			"Travel to White Wolf Mountain.");
		talkToKhorvak = new NpcStep(this, NpcID.KHORVAK_A_DWARVEN_ENGINEER, new WorldPoint(2864, 9878, 0),
			"");
		talkToKhorvak.addDialogStep("What if I offer you a drink?");
		useStoutOnKhorvak = new NpcStep(this, NpcID.KHORVAK_A_DWARVEN_ENGINEER, new WorldPoint(2864, 9878, 0),
			"", dwarvenStout.highlighted());
		useStoutOnKhorvak.addIcon(ItemID.DWARVEN_STOUT);
		getKeldaTicket = new NpcStep(this, NpcID.CART_CONDUCTOR_2390, new WorldPoint(2875, 9871, 0),
			"Get a ticket and travel back to Keldagrim.");
		getKeldaTicket.addDialogSteps("I'd like to buy a ticket.", "Buy.");
		takeCartFromWWMToKelda = new ObjectStep(this, ObjectID.TRAIN_CART_7030, new WorldPoint(2875, 9868, 0),
			"Take the cart back to White Wolf Mountain.");
		talkToGauss = new NpcStep(this, NpcID.GAUSS, new WorldPoint(2839, 10196, 0),
			"");
		talkToRind = new NpcStep(this, NpcID.RIND_THE_GARDENER, new WorldPoint(2854, 10196, 0),
			"");
		plantKelda = new ObjectStep(this, NullObjectID.NULL_8877, new WorldPoint(2854, 10203, 0),
			"");
		plantKelda.addIcon(ItemID.KELDA_SEED);
		rakeKelda = new ObjectStep(this, NullObjectID.NULL_8877, new WorldPoint(2854, 10203, 0),
			"");
		rakeKelda.addIcon(ItemID.RAKE);
		waitForKelda = new DetailedQuestStep(this, "Wait for the kelda hops to grow. This'll take 15-20 minutes.");
		harvestHops = new ObjectStep(this, NullObjectID.NULL_8877, new WorldPoint(2854, 10203, 0),
			"");
		goUpstairsPub = new ObjectStep(this, ObjectID.STAIRS_6085, new WorldPoint(2916, 10196, 0),
			"Go upstairs in Keldagrim's east pub.");

		pickupPot = new ItemStep(this, "Pick up the pot in the brewing room.", pot);

		buyYeast = new NpcStep(this, NpcID.BLANDEBIR, new WorldPoint(2916, 10193, 1),
			"Buy yeast from Blandebir. You can get a pot from the table in the brewing room.", coins.quantity(25), pot);
		buyYeast.addDialogSteps("Do you have any spare ale yeast?", "That's a good deal - please fill my pot with ale" +
			" yeast for 25GP.");
		addWater = new ObjectStep(this, NullObjectID.NULL_11670, new WorldPoint(2918, 10195, 1),
			"Add 2 buckets of water to the vat.", bucketOfWater2.highlighted());
		addWater.addIcon(ItemID.BUCKET_OF_WATER);
		addMalts = new ObjectStep(this, NullObjectID.NULL_11670, new WorldPoint(2918, 10195, 1),
			"Add 2 barley malts to the vat.", barleyMalt2.highlighted());
		addMalts.addIcon(ItemID.BARLEY_MALT);
		addKelda = new ObjectStep(this, NullObjectID.NULL_11670, new WorldPoint(2918, 10195, 1),
			"Add the kelda hops to the vat.", keldaHop.highlighted());
		addKelda.addIcon(ItemID.KELDA_HOPS);
		addYeast = new ObjectStep(this, NullObjectID.NULL_11670, new WorldPoint(2918, 10195, 1),
			"Add the yeast to the vat.", yeast.highlighted());
		addYeast.addIcon(ItemID.ALE_YEAST);
		waitBrewing = new DetailedQuestStep(this, "Wait for the kelda to brew. This'll take 15-20 minutes.");

		turnValve = new ObjectStep(this, ObjectID.VALVE_23936, new WorldPoint(2918, 10193, 1),
			"");
		useGlassOnBarrel = new ObjectStep(this, NullObjectID.NULL_24957, new WorldPoint(2917, 10193, 1),
			"", beerGlass.highlighted());
		useGlassOnBarrel.addIcon(ItemID.BEER_GLASS);
		goDownFromPub = new ObjectStep(this, ObjectID.STAIRS_6086, new WorldPoint(2915, 10196, 1),
			"Go downstairs.");

		talkToCartConductor = new NpcStep(this, NpcID.CART_CONDUCTOR_2392, new WorldPoint(2922, 10166, 0),
			"");
		talkToCartConductor.addDialogStep("Ask about closed off tunnel.");
		goUpToDirector = new ObjectStep(this, ObjectID.STAIRS_6087, new WorldPoint(2895, 10210, 0),
			"Go up to the Consortium.");

		if (inPurple.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.PURPLE_PEWTER_DIRECTOR_5998, new WorldPoint(2869, 10195, 1),
				"Talk to the Purple Pewter Director.");
		}
		else if (inYellow.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.YELLOW_FORTUNE_DIRECTOR_6000, new WorldPoint(2869, 10208, 1),
				"Talk to the Yellow Fortune Director.");
		}
		else if (inBlue.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.BLUE_OPAL_DIRECTOR_5999, new WorldPoint(2869, 10203, 1),
				"Talk to the Blue Opal Director.");
		}
		else if (inGreen.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.GREEN_GEMSTONE_DIRECTOR_6021, new WorldPoint(2890, 10209, 1),
				"Talk to the Green Gemstone Director.");
		}
		else if (inWhite.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.WHITE_CHISEL_DIRECTOR_6022, new WorldPoint(2890, 10204, 1),
				"Talk to the White Chisel Director.");
		}
		else if (inSilver.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.SILVER_COG_DIRECTOR_6023, new WorldPoint(2890, 10194, 1),
				"Talk to the Silver Cog Director.");
		}
		else if (inBrown.check(client))
		{
			talkToDirector = new NpcStep(this, NpcID.BROWN_ENGINE_DIRECTOR_6024, new WorldPoint(2890, 10189, 1),
				"Talk to the Brown Engine Director.");
		}
		else
		{
			talkToDirector = new DetailedQuestStep(this, "Talk to the director of the company you joined in The Giant" +
				" Dwarf.");
		}
		talkToDirector.addDialogStep("Can you help me with a boarded up tunnel?");
		goDownFromDirector = new ObjectStep(this, ObjectID.STAIRS_6088, new WorldPoint(2895, 10210, 1),
			"Go downstairs.");
		takeSecretCart = new ObjectStep(this, ObjectID.TRAIN_CART_7028, new WorldPoint(2919, 10164, 0),
			"", handsFree, shieldFree);

		searchBox1 = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1862, 4954, 1),
			"Search the box.");

		startPuzzle1 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1860, 4955, 1),
			"Use the dwarven machinery.");
		puzzle1P1 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 248, 35);
		puzzle1P2 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 248, 37);
		puzzle1Ok = new WidgetStep(this, "Click the Ok button.", 248, 53);

		takePuzzle1Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");

		searchPuzzle1Box = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1898, 4985, 3),
			"Search the box.");

		returnFromPuzzle1 = new ObjectStep(this, ObjectID.TRAIN_CART_8925, new WorldPoint(1897, 4987, 3),
			"Take the cart back.");

		startPuzzle2 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1860, 4955, 1),
			"Use the dwarven machinery.");
		puzzle2P1 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 248, 35);
		puzzle2P2 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 248, 36);
		puzzle2P3 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 248, 39);
		puzzle2Ok = new WidgetStep(this, "Click the Ok button.", 248, 53);
		takePuzzle2Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");
		searchPuzzle2Box = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1897, 4986, 3),
			"Search the box.");
		returnFromPuzzle2 = new ObjectStep(this, ObjectID.TRAIN_CART_8925, new WorldPoint(1897, 4984, 3),
			"Take the cart back.");

		// Puzzle 3
		startPuzzle3 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1860, 4955, 1),
			"Use the dwarven machinery.");
		puzzle3P1 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 248, 35);
		puzzle3P2 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 248, 37);
		puzzle3P3 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 248, 40);

		puzzle3P4 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 248, 41);
		puzzle3Ok = new WidgetStep(this, "Click the Ok button.", 248, 53);

		takePuzzle3Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");

		leaveListeningRoom1 = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_8884, new WorldPoint(1889, 4982, 2),
			"Leave the east exit once you finish listening to the conversation.");

		// Puzzle 4
		searchBox2 = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1860, 4955, 1),
			"Search the box.");

		startPuzzle4 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1862, 4954, 1),
			"Use the dwarven machinery.");
		puzzle4P1 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 244, 47);
		puzzle4P2 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 244, 48);
		puzzle4P3 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 244, 51);
		puzzle4Ok = new WidgetStep(this, "Click the Ok button.", 244, 73);
		takePuzzle4Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");
		searchPuzzle4Box = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1898, 4985, 3),
			"Search the box.");
		returnFromPuzzle4 = new ObjectStep(this, ObjectID.TRAIN_CART_8925, new WorldPoint(1898, 4987, 3),
			"Take the cart back.");

		// Puzzle 5
		startPuzzle5 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1862, 4954, 1),
			"Use the dwarven machinery.");
		puzzle5P1 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 244, 47);
		puzzle5P2 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 244, 49);
		puzzle5P3 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 244, 52);
		puzzle5P4 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 244, 57);
		puzzle5Ok = new WidgetStep(this, "Click the Ok button.", 244, 73);
		takePuzzle5Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");
		searchPuzzle5Box = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1906, 4985, 3),
			"Search the box.");
		returnFromPuzzle5 = new ObjectStep(this, ObjectID.TRAIN_CART_8925, new WorldPoint(1906, 4987, 3),
			"Take the cart back.");

		// Puzzle 6
		startPuzzle6 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1862, 4954, 1),
			"Use the dwarven machinery.");
		puzzle6P1 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 244, 47);
		puzzle6P2 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 244, 48);
		puzzle6P3 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 244, 50);
		puzzle6P4 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 244, 56);
		puzzle6P5 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 244, 58);
		puzzle6Ok = new WidgetStep(this, "Click the Ok button.", 244, 73);
		takePuzzle6Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");

		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_8910, new WorldPoint(1904, 4967, 2),
			"Search the nearby bookcase.");
		searchCrate1 = new ObjectStep(this, ObjectID.CRATE_8914, new WorldPoint(1906, 4965, 2),
			"Search the crates with paper on them.");
		searchCrate2 = new ObjectStep(this, ObjectID.CRATE_8915, new WorldPoint(1910, 4966, 2),
			"Search the crates with paper on them.");
		searchCrate1.addSubSteps(searchCrate2);
		leaveLibrary = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_8884, new WorldPoint(1914, 4967, 2),
			"Leave the library.");
		leaveLibrary.addDialogStep("Yes.");

		// Puzzle 7
		searchBox3 = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1862, 4954, 1),
			"Search the box.");

		startPuzzle7 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1860, 4955, 1),
			"Use the dwarven machinery.");
		puzzle7P1 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 68);
		puzzle7P2 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 69);
		puzzle7P3 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 71);
		puzzle7P4 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 73);
		puzzle7Ok = new WidgetStep(this, "Click the Ok button.", 247, 108);
		takePuzzle7Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");
		searchPuzzle7Box = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1906, 4987, 3),
			"Search the box.");
		returnFromPuzzle7 = new ObjectStep(this, ObjectID.TRAIN_CART_8925, new WorldPoint(1906, 4984, 3),
			"Take the cart back.");

		// Puzzle 8
		startPuzzle8 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1860, 4955, 1),
			"Use the dwarven machinery.");
		puzzle8P1 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 68);
		puzzle8P2 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 70);
		puzzle8P3 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 72);
		puzzle8P4 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 74);
		puzzle8P5 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 76);
		puzzle8P6 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 79);
		puzzle8Ok = new WidgetStep(this, "Click the Ok button.", 247, 108);
		takePuzzle8Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");
		searchPuzzle8Box = new ObjectStep(this, ObjectID.BOX, new WorldPoint(1898, 4986, 3),
			"Search the box.");
		returnFromPuzzle8 = new ObjectStep(this, ObjectID.TRAIN_CART_8925, new WorldPoint(1897, 4984, 3),
			"Take the cart back.");

		// Puzzle 9
		startPuzzle9 = new ObjectStep(this, ObjectID.DWARVEN_MACHINERY, new WorldPoint(1860, 4955, 1),
			"Use the dwarven machinery.");

		puzzle9P1 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 68);
		puzzle9P2 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 69);
		puzzle9P3 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 71);
		puzzle9P4 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 73);
		puzzle9P5 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 75);
		puzzle9P6 = new WidgetStep(this, "Click the marked junction until it's got the green piece in.", 247, 78);
		puzzle9P7 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 81);
		puzzle9P8 = new WidgetStep(this, "Click the marked junction until it's got the yellow piece in.", 247, 84);
		puzzle9Ok = new WidgetStep(this, "Click the Ok button.", 247, 108);
		takePuzzle9Cart = new ObjectStep(this, ObjectID.TRAIN_CART_8924, new WorldPoint(1864, 4957, 1),
			"Take the cart.");

		watchCutscene = new DetailedQuestStep(this, "Watch the cutscene.");

		eatKebab = new DetailedQuestStep(this, new WorldPoint(2915, 10193, 0), "");
		eatKebab.addIcon(ItemID.KEBAB);

		setupConditionalSteps();
	}

	public void setupConditionalSteps()
	{
		goTalkVeldaban = new ConditionalStep(this, travelToKeldagrim, "Talk to Veldaban in Keldagrim.");
		goTalkVeldaban.addStep(inKelgdagrim, talkToVeldaban);

		goTalkDrunkDwarf = new ConditionalStep(this, travelToKeldagrim, "Talk to the Drunken Dwarf in Keldagrim.");
		goTalkDrunkDwarf.addStep(inKelgdagrim, talkToDrunkDwarf);
		goTalkDrunkDwarfAgain = new ConditionalStep(this, travelToKeldagrim, "Give the Drunken Dwarf some beer.", beer);
		goTalkDrunkDwarfAgain.addStep(inKelgdagrim, talkToDrunkDwarf);

		goTalkDrunkDwarfAfterGivingBeer = new ConditionalStep(this, travelToKeldagrim, "Talk to the Drunken Dwarf.");
		goTalkDrunkDwarfAfterGivingBeer.addStep(inKelgdagrim, talkToDrunkDwarf);
		goTalkDrunkDwarfAgain.addSubSteps(goTalkDrunkDwarfAfterGivingBeer);

		goTalkRowdyDwarf = new ConditionalStep(this, travelToKeldagrim, "Talk to the Rowdy Dwarf roaming the east " +
			"Keldagrim pub, then give him the item he requests.");
		goTalkRowdyDwarf.addStep(inKelgdagrim, talkToRowdyDwarf);

		goTalkToKhorvak = new ConditionalStep(this, travelToKeldagrim,
			"Talk to Khorvak under White Wolf Mountain.", coins.quantity(200), dwarvenStout);
		goTalkToKhorvak.addStep(new Conditions(inWolfUnderground), talkToKhorvak);
		goTalkToKhorvak.addStep(new Conditions(inKelgdagrim, ticketToWWM), travelToWWM);
		goTalkToKhorvak.addStep(inKelgdagrim, getWWMTicket);

		goGiveKhorvakBeer = new ConditionalStep(this, travelToKeldagrim,
			"Give Khorvak under White Wolf Mountain a dwarven stout.", dwarvenStout.highlighted());
		goGiveKhorvakBeer.addStep(new Conditions(inWolfUnderground), talkToKhorvak);
		goGiveKhorvakBeer.addStep(new Conditions(inKelgdagrim, ticketToWWM), travelToWWM);
		goGiveKhorvakBeer.addStep(inKelgdagrim, getWWMTicket);

		goTalkToGuass = new ConditionalStep(this, travelToKeldagrim,
			"Talk to Gauss in west Keldagrim.", beer.highlighted());
		goTalkToGuass.addStep(inKelgdagrim, talkToGauss);
		goTalkToGuass.addStep(new Conditions(inWolfUnderground, ticketToKelda), takeCartFromWWMToKelda);
		goTalkToGuass.addStep(new Conditions(inWolfUnderground), getKeldaTicket);

		goTalkToRind = new ConditionalStep(this, travelToKeldagrim,
			"Talk to Rind in west Keldagrim.", keldaSeed.quantity(4), rake, dibber);
		goTalkToRind.addStep(inKelgdagrim, talkToRind);
		goPlantKelda = new ConditionalStep(this, travelToKeldagrim,
			"Grow the kelda seed near Rind.", keldaSeed.quantity(4).highlighted(), rake, dibber);
		goPlantKelda.addStep(new Conditions(inKelgdagrim, plotRaked), plantKelda);
		goPlantKelda.addStep(inKelgdagrim, rakeKelda);

		goHarvestKelda = new ConditionalStep(this, travelToKeldagrim,
			"Harvest the kelda hops.", spade);
		goHarvestKelda.addStep(inKelgdagrim, harvestHops);

		goBrew = new ConditionalStep(this, travelToKeldagrim,
			"Go brew the hops.", keldaHop.hideConditioned(addedHop),
			coins.quantity(25).hideConditioned(addedYeast),
			bucketOfWater2.hideConditioned(addedWater),
			barleyMalt2.hideConditioned(addedMalt),
			beerGlass);
		goBrew.addStep(inKelgdagrim, goUpstairsPub);

		goTurnValve = new ConditionalStep(this, travelToKeldagrim,
			"Turn the valve on the vat.", beerGlass);
		goTurnValve.addStep(inPubUpstairs, turnValve);
		goTurnValve.addStep(inKelgdagrim, goUpstairsPub);

		goUseGlass = new ConditionalStep(this, travelToKeldagrim,
			"Use a beer glass on the vat's barrel.");
		goUseGlass.addStep(inPubUpstairs, useGlassOnBarrel);
		goUseGlass.addStep(inKelgdagrim, goUpstairsPub);

		goGiveDrunkenDwarfKelda = new ConditionalStep(this, travelToKeldagrim,
			"Give the Drunken Dwarf the kelda stout.", keldaStout);
		goGiveDrunkenDwarfKelda.addStep(inPubUpstairs, goDownFromPub);
		goGiveDrunkenDwarfKelda.addStep(inKelgdagrim, talkToDrunkDwarf);

		goTalkToDrunkenDwarfAfterKelda = new ConditionalStep(this, travelToKeldagrim,
			"Talk to the Drunken Dwarf.");
		goTalkToDrunkenDwarfAfterKelda.addStep(inPubUpstairs, goDownFromPub);
		goTalkToDrunkenDwarfAfterKelda.addStep(inKelgdagrim, talkToDrunkDwarf);

		goGiveDrunkenDwarfKelda.addSubSteps(goTalkToDrunkenDwarfAfterKelda);

		goTalkToConductor = new ConditionalStep(this, travelToKeldagrim,
			"Talk to the south most cart conductor in south east Keldagrim.");
		goTalkToConductor.addStep(inKelgdagrim, talkToCartConductor);

		goTalkToDirector = new ConditionalStep(this, travelToKeldagrim,
			"Talk to the Director of the company you joined in The Giant Dwarf.");
		goTalkToDirector.addStep(inConsortium, talkToDirector);
		goTalkToDirector.addStep(inKelgdagrim, goUpToDirector);
		goTakeSecretCart = new ConditionalStep(this, travelToKeldagrim, "Take the cart in the south of the cart " +
			"station.");
		goTakeSecretCart.addStep(inConsortium, goDownFromDirector);
		goTakeSecretCart.addStep(inKelgdagrim, takeSecretCart);

		goReturnToVeldaban = new ConditionalStep(this, travelToKeldagrim, "Report back to Commander Veldaban.");
		goReturnToVeldaban.addStep(inKelgdagrim, talkToVeldaban);

		goEatKebab = new ConditionalStep(this, travelToKeldagrim, "Eat a kebab in the pub in east Keldagrim.", beer,
			kebab.highlighted());
		goEatKebab.addStep(inKelgdagrim, eatKebab);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins500, barleyMalt2, bucketOfWater2, spade, dibber, rake, kebab, beer.quantity(3),
			dwarvenStout, beerGlass, randomItem);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(grandExchangeTeleport);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new SkillRequirement(Skill.COOKING, 22, true));
		req.add(new SkillRequirement(Skill.FARMING, 17, true));
		req.add(new QuestRequirement(QuestHelperQuest.THE_GIANT_DWARF, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.FISHING_CONTEST, QuestState.FINISHED));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.COOKING, 5000),
				new ExperienceReward(Skill.FARMING, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Dwarven Stout (m)", ItemID.DWARVEN_STOUTM, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Brewing",
			Arrays.asList(goTalkVeldaban, goTalkDrunkDwarf, goTalkDrunkDwarfAgain,
				goTalkRowdyDwarf, goTalkToKhorvak, goGiveKhorvakBeer, goTalkToGuass, goTalkToRind,
				goPlantKelda, waitForKelda, goHarvestKelda, goBrew, pickupPot, buyYeast, addWater, addMalts, addKelda, addYeast,
				waitBrewing, goTurnValve, goUseGlass, goGiveDrunkenDwarfKelda), coins500, barleyMalt2, bucketOfWater2,
			spade,	dibber, rake, beer.quantity(2), dwarvenStout, beerGlass, randomItem));
		allSteps.add(new PanelDetails("Unlocking the tunnels", Arrays.asList(goTalkToConductor, goTalkToDirector,
			goTakeSecretCart)));
		allSteps.add(new PanelDetails("Puzzle 1", Arrays.asList(searchBox1, startPuzzle1, puzzle1P1, puzzle1P2,
			puzzle1Ok, takePuzzle1Cart, searchPuzzle1Box, returnFromPuzzle1)));
		allSteps.add(new PanelDetails("Puzzle 2", Arrays.asList(startPuzzle2, puzzle2P1, puzzle2P2,
			puzzle2P3, puzzle2Ok, takePuzzle2Cart, searchPuzzle2Box, returnFromPuzzle2)));
		allSteps.add(new PanelDetails("Puzzle 3", Arrays.asList(startPuzzle3, puzzle3P1, puzzle3P2,
			puzzle3P3, puzzle3P4, puzzle3Ok, takePuzzle3Cart)));
		allSteps.add(new PanelDetails("Overhear plans", Collections.singletonList(leaveListeningRoom1)));
		allSteps.add(new PanelDetails("Puzzle 4", Arrays.asList(searchBox2, startPuzzle4, puzzle4P1, puzzle4P2,
			puzzle4P3, puzzle4Ok, takePuzzle4Cart, searchPuzzle4Box, returnFromPuzzle4)));
		allSteps.add(new PanelDetails("Puzzle 5", Arrays.asList(startPuzzle5, puzzle5P1, puzzle5P2,
			puzzle5P3, puzzle5P4, puzzle5Ok, takePuzzle5Cart, searchPuzzle5Box, returnFromPuzzle5)));
		allSteps.add(new PanelDetails("Puzzle 6", Arrays.asList(startPuzzle6, puzzle6P1, puzzle6P2,
			puzzle6P3, puzzle6P4, puzzle6P5, puzzle6Ok, takePuzzle6Cart)));
		allSteps.add(new PanelDetails("The Library", Arrays.asList(searchBookcase, searchCrate1, leaveLibrary)));
		allSteps.add(new PanelDetails("Puzzle 7", Arrays.asList(searchBox3, startPuzzle7, puzzle7P1, puzzle7P2,
			puzzle7P3, puzzle7P4, puzzle7Ok, takePuzzle7Cart, searchPuzzle7Box, returnFromPuzzle7)));
		allSteps.add(new PanelDetails("Puzzle 8", Arrays.asList(startPuzzle8, puzzle8P1, puzzle8P2, puzzle8P3,
			puzzle8P4, puzzle8P5, puzzle8P6, puzzle8Ok, takePuzzle8Cart, searchPuzzle8Box, returnFromPuzzle8)));
		allSteps.add(new PanelDetails("Puzzle 9", Arrays.asList(startPuzzle9, puzzle9P1, puzzle9P2, puzzle9P3,
			puzzle9P4, puzzle9P5, puzzle9P6, puzzle9P7, puzzle9P8, puzzle9Ok, takePuzzle9Cart, watchCutscene)));

		allSteps.add(new PanelDetails("A Drunken Tale", Arrays.asList(goReturnToVeldaban, goEatKebab), beer, kebab));


		return allSteps;
	}
}
