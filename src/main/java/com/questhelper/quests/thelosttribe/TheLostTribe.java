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
package com.questhelper.quests.thelosttribe;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcEmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import com.questhelper.steps.emote.QuestEmote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_LOST_TRIBE
)
public class TheLostTribe extends BasicQuestHelper
{
	ItemRequirement pickaxe, lightSource, brooch, book, key, silverware, treaty, varrockTeleport, faladorTeleport, lumbridgeTeleports;

	ConditionForStep inBasement, inLumbridgeF0, inLumbridgeF1, inLumbridgeF2, inTunnels, hasBrooch, hasBook, inMines,
		hasKey, foundRobes, inHamBase, foundSilverware, bobKnows, hansKnows;

	DetailedQuestStep goDownFromF2, talkToSigmund, talkToDuke, goDownFromF1, talkToHans, goUpToF1,
		goDownIntoBasement, usePickaxeOnRubble, climbThroughHole, grabBrooch, goUpFromBasement, showBroochToDuke,
		searchBookcase, readBook, talkToGenerals, walkToMistag, emoteAtMistag, pickpocketSigmund, unlockChest,
		enterHamLair, searchHamCrates, talkToKazgar, talkToMistagForEnd, talkToCook, talkToBob, talkToAereck, talkToAllAboutCellar;

	ConditionalStep goToF1Steps, goDownToBasement, goTalkToSigmundToStart, findGoblinWitnessSteps, goTalkToDukeAfterHans,
		goMineRubble, enterTunnels, goShowBroochToDuke, goTalkToDukeAfterEmote, goTravelToMistag, goGetKey, goOpenRobeChest,
		goIntoHamLair, goToDukeWithSilverware, travelToMakePeace;

	Zone basement, lumbridgeF0, lumbridgeF1, lumbridgeF2, tunnels, mines, hamBase;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, goTalkToSigmundToStart);
		steps.put(1, findGoblinWitnessSteps);
		steps.put(2, goTalkToDukeAfterHans);
		steps.put(3, goMineRubble);

		ConditionalStep goGrabBrooch = new ConditionalStep(this, enterTunnels);
		goGrabBrooch.addStep(hasBrooch, goShowBroochToDuke);
		goGrabBrooch.addStep(inTunnels, grabBrooch);
		steps.put(4, goGrabBrooch);

		ConditionalStep getBook = new ConditionalStep(this, searchBookcase);
		getBook.addStep(hasBook, readBook);
		steps.put(5, getBook);

		steps.put(6, talkToGenerals);

		ConditionalStep makeContactSteps = new ConditionalStep(this, goTravelToMistag);
		makeContactSteps.addStep(inMines, emoteAtMistag);
		makeContactSteps.addStep(inTunnels, walkToMistag);

		steps.put(7, makeContactSteps);
		steps.put(8, goTalkToDukeAfterEmote);

		ConditionalStep revealSigmund = new ConditionalStep(this, goGetKey);
		revealSigmund.addStep(foundSilverware, goToDukeWithSilverware);
		revealSigmund.addStep(foundRobes, goIntoHamLair);
		revealSigmund.addStep(hasKey, goOpenRobeChest);
		steps.put(9, revealSigmund);

		steps.put(10, travelToMakePeace);

		return steps;
	}

	public void setupItemRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		lightSource = new ItemRequirement("A light source", ItemCollections.getLightSources());
		brooch = new ItemRequirement("Brooch", ItemID.BROOCH);
		book = new ItemRequirement("Goblin symbol book", ItemID.GOBLIN_SYMBOL_BOOK);
		book.setHighlightInInventory(true);
		key = new ItemRequirement("Key", ItemID.KEY_5010);
		silverware = new ItemRequirement("Silverware", ItemID.SILVERWARE);
		silverware.setTip("You can get another from the crate in the entrance of the H.A.M. hideout");

		treaty = new ItemRequirement("Peace treaty", ItemID.PEACE_TREATY);
		treaty.setTip("You can get another from Duke Horacio");

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.LUMBRIDGE_TELEPORT);
		faladorTeleport = new ItemRequirement("Falador teleports", ItemID.FALADOR_TELEPORT);
	}

	public void loadZones()
	{
		basement = new Zone(new WorldPoint(3208, 9614, 0), new WorldPoint(3219, 9625, 0));
		lumbridgeF0 = new Zone(new WorldPoint(3136, 3136, 0), new WorldPoint(3328, 3328, 0));
		lumbridgeF1 = new Zone(new WorldPoint(3203, 3206, 1), new WorldPoint(3217, 3231, 1));
		lumbridgeF2 = new Zone(new WorldPoint(3203, 3206, 2), new WorldPoint(3217, 3231, 2));
		tunnels = new Zone(new WorldPoint(3221, 9602, 0), new WorldPoint(3308, 9661, 0));
		mines = new Zone(new WorldPoint(3309, 9600, 0), new WorldPoint(3327, 9655, 0));
		hamBase = new Zone(new WorldPoint(3140, 9600, 0), new WorldPoint(3190, 9655, 0));
	}

	public void setupConditions()
	{
		inBasement = new ZoneCondition(basement);
		inLumbridgeF0 = new ZoneCondition(lumbridgeF0);
		inLumbridgeF1 = new ZoneCondition(lumbridgeF1);
		inLumbridgeF2 = new ZoneCondition(lumbridgeF2);
		inTunnels = new ZoneCondition(tunnels);
		inMines = new ZoneCondition(mines);
		inHamBase = new ZoneCondition(hamBase);

		hasBrooch = new ItemRequirementCondition(brooch);
		hasBook = new ItemRequirementCondition(book);
		hasKey = new ItemRequirementCondition(key);

		foundRobes = new VarbitCondition(534, 1, Operation.GREATER_EQUAL);
		foundSilverware = new VarbitCondition(534, 3, Operation.GREATER_EQUAL);

		hansKnows = new VarbitCondition(537, 0);
		bobKnows = new VarbitCondition(537, 1);

		// 537 0->2->0, Hans
		// 537 0->1, Bob
	}

	public void setupSteps()
	{
		goDownIntoBasement = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0), "Enter the Lumbridge Castle basement.");
		goDownFromF1 = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(3205, 3208, 1), "Go down the staircase.");
		goDownFromF1.addDialogStep("Climb down the stairs.");
		goUpToF1 = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(3205, 3208, 0), "Go up to the first floor of Lumbridge Castle.");
		goUpFromBasement = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(3209, 9616, 0), "Go up to the surface.");
		goDownFromF2 = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(3205, 3208, 2), "Go downstairs.");
		talkToSigmund = new NpcStep(this, NpcID.SIGMUND_5322, new WorldPoint(3210, 3222, 1), "");
		talkToSigmund.addDialogSteps("Do you have any quests for me?", "Yes.");

		// This isn't just talk to Hans, it's a random one of the NPCs to chat to
		talkToHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3222, 3218, 0), "Talk to Hans who is roaming around the castle.");
		talkToHans.addDialogStep("Do you know what happened in the cellar?");

		talkToBob = new NpcStep(this, NpcID.BOB_2812, new WorldPoint(3231, 3203, 0), "Talk to Bob in the south of Lumbridge.");
		talkToBob.addDialogStep("Do you know what happened in the castle cellar?");

		talkToAllAboutCellar = new NpcStep(this, NpcID.COOK_4626, "Talk to the Cook, Hans, Father Aereck, and Bob in Lumbridge until one tells you about seeing a goblin.");
		((NpcStep)(talkToAllAboutCellar)).addAlternateNpcs(NpcID.FATHER_AERECK);
		talkToAllAboutCellar.addDialogSteps("Do you know what happened in the castle cellar?");
		talkToAllAboutCellar.addSubSteps(talkToHans, talkToBob);

		talkToDuke = new NpcStep(this, NpcID.DUKE_HORACIO, new WorldPoint(3210, 3222, 1), "");
		// Name of person who said they saw something changes
		usePickaxeOnRubble = new ObjectStep(this, NullObjectID.NULL_6898, new WorldPoint(3219, 9618, 0), "");
		usePickaxeOnRubble.addIcon(ItemID.BRONZE_PICKAXE);

		climbThroughHole = new ObjectStep(this, NullObjectID.NULL_6898, new WorldPoint(3219, 9618, 0), "");

		grabBrooch = new DetailedQuestStep(this, new WorldPoint(3230, 9610, 0), "Pick up the brooch on the floor.", brooch);

		showBroochToDuke = new NpcStep(this, NpcID.DUKE_HORACIO, new WorldPoint(3210, 3222, 1), "");
		showBroochToDuke.addDialogStep("I dug through the rubble...");

		searchBookcase = new ObjectStep(this, ObjectID.BOOKCASE_6916, new WorldPoint(3207, 3496, 0), "Search the north west bookcase in the Varrock Castle Library.");
		readBook = new DetailedQuestStep(this, "Read the entire goblin symbol book.", book);

		talkToGenerals = new NpcStep(this, NpcID.GENERAL_WARTFACE, new WorldPoint(2957, 3512, 0), "Talk to the Goblin Generals in the Goblin Village.");
		talkToGenerals.addDialogSteps("Have you ever heard of the Dorgeshuun?", "It doesn't really matter",
			"Well either way they refused to fight", "Well I found a brooch underground...", "Well why not show me both greetings?");

		ArrayList<WorldPoint> travelLine = new ArrayList<>(Arrays.asList(
			new WorldPoint(3222, 9618, 0),
			new WorldPoint(3224, 9618, 0),
			new WorldPoint(3229, 9610, 0),
			new WorldPoint(3238, 9610, 0),
			new WorldPoint(3241, 9612, 0),
			new WorldPoint(3246, 9612, 0),
			new WorldPoint(3249, 9619, 0),
			new WorldPoint(3254, 9625, 0),
			new WorldPoint(3252, 9631, 0),
			new WorldPoint(3234, 9631, 0),
			new WorldPoint(3230, 9634, 0),
			new WorldPoint(3230, 9643, 0),
			new WorldPoint(3237, 9648, 0),
			new WorldPoint(3244, 9648, 0),
			new WorldPoint(3246, 9645, 0),
			new WorldPoint(3240, 9641, 0),
			new WorldPoint(3244, 9637, 0),
			new WorldPoint(3252, 9642, 0),
			new WorldPoint(3252, 9646, 0),
			new WorldPoint(3257, 9656, 0),
			new WorldPoint(3269, 9656, 0),
			new WorldPoint(3277, 9652, 0),
			new WorldPoint(3277, 9647, 0),
			new WorldPoint(3267, 9643, 0),
			new WorldPoint(3267, 9638, 0),
			new WorldPoint(3276, 9637, 0),
			new WorldPoint(3290, 9645, 0),
			new WorldPoint(3300, 9641, 0),
			new WorldPoint(3307, 9631, 0),
			new WorldPoint(3297, 9627, 0),
			new WorldPoint(3295, 9622, 0),
			new WorldPoint(3290, 9617, 0),
			new WorldPoint(3297, 9606, 0),
			new WorldPoint(3303, 9606, 0),
			new WorldPoint(3309, 9612, 0),
			new WorldPoint(3317, 9612, 0)
		));

		walkToMistag = new NpcEmoteStep(this, NpcID.MISTAG_7297, QuestEmote.GOBLIN_BOW, new WorldPoint(3319, 9615, 0), "Travel through the tunnels. Make sure you follow the marked path, or you'll be dropped into a hole and your light source extinguished!", lightSource);
		walkToMistag.setLinePoints(travelLine);

		emoteAtMistag = new NpcEmoteStep(this, NpcID.MISTAG_7297, QuestEmote.GOBLIN_BOW, new WorldPoint(3319, 9615, 0), "Perform the Goblin Bow emote next to Mistag and talk to him.", lightSource);

		pickpocketSigmund = new NpcStep(this, NpcID.SIGMUND_5322, new WorldPoint(3210, 3222, 1), "");
		unlockChest = new ObjectStep(this, ObjectID.CHEST_6910, new WorldPoint(3209, 3217, 1), "");

		enterHamLair = new ObjectStep(this, NullObjectID.NULL_5492, new WorldPoint(3166, 3252, 0), "");

		searchHamCrates = new ObjectStep(this, ObjectID.CRATE_6911, new WorldPoint(3152, 9645, 0), "");

		talkToKazgar = new NpcStep(this, NpcID.KAZGAR, new WorldPoint(3230, 9610, 0), "Travel with Kazgar to shortcut to Mistag.");
		talkToMistagForEnd = new NpcStep(this, NpcID.MISTAG_7298, new WorldPoint(3319, 9615, 0), "");
	}

	private void setupConditionalSteps()
	{
		goToF1Steps = new ConditionalStep(this, goUpToF1);
		goToF1Steps.addStep(inLumbridgeF2, goDownFromF2);
		goToF1Steps.addStep(inBasement, goUpFromBasement);

		goDownToBasement = new ConditionalStep(this, goDownIntoBasement);
		goDownToBasement.addStep(inLumbridgeF2, goDownFromF2);
		goDownToBasement.addStep(inLumbridgeF1, goDownFromF1);

		goTalkToSigmundToStart = new ConditionalStep(this, goToF1Steps, "Talk to Sigmund in Lumbridge Castle.");
		goTalkToSigmundToStart.addStep(inLumbridgeF1, talkToSigmund);

		findGoblinWitnessSteps = new ConditionalStep(this, talkToAllAboutCellar);
		findGoblinWitnessSteps.addStep(inLumbridgeF2, goDownFromF2);
		findGoblinWitnessSteps.addStep(inLumbridgeF1, goDownFromF1);
		findGoblinWitnessSteps.addStep(inBasement, goUpFromBasement);
		findGoblinWitnessSteps.addStep(hansKnows, talkToHans);
		findGoblinWitnessSteps.addStep(bobKnows, talkToBob);

		goTalkToDukeAfterHans = new ConditionalStep(this, goToF1Steps, "Talk to Duke Horacio in Lumbridge Castle.");
		goTalkToDukeAfterHans.addDialogSteps("Hans says he saw something in the cellar", "Bob says he saw something in the cellar",
			"Father Aereck says he saw something in the cellar", "The Cook says he saw something in the cellar");
		goTalkToDukeAfterHans.addStep(inLumbridgeF1, talkToDuke);

		goMineRubble = new ConditionalStep(this, goDownToBasement, "Go use a pickaxe on the rubble in the Lumbridge Castle basement.", pickaxe, lightSource);
		goMineRubble.addStep(inBasement, usePickaxeOnRubble);

		enterTunnels = new ConditionalStep(this, goDownToBasement, "Enter the hole in Lumbridge Castle's basement.", lightSource);
		enterTunnels.addStep(inBasement, climbThroughHole);

		goShowBroochToDuke = new ConditionalStep(this, goToF1Steps, "Show the brooch to Duke Horacio in Lumbridge Castle.", brooch);
		goShowBroochToDuke.addStep(inLumbridgeF1, showBroochToDuke);

		goTravelToMistag = new ConditionalStep(this, goDownToBasement, "Travel through the tunnels under Lumbridge until you reach Mistag.", lightSource);
		goTravelToMistag.addStep(inBasement, climbThroughHole);
		goTravelToMistag.addSubSteps(walkToMistag);

		goTalkToDukeAfterEmote = new ConditionalStep(this, goToF1Steps, "Talk to Duke Horacio in Lumbridge Castle. You can fast-travel with Mistag back to Lumbridge.");
		goTalkToDukeAfterEmote.addDialogSteps("I've made contact with the cave goblins...");
		goTalkToDukeAfterEmote.addStep(inLumbridgeF1, talkToDuke);

		goGetKey = new ConditionalStep(this, goToF1Steps, "Pickpocket Sigmund for a key.");
		goGetKey.addStep(inLumbridgeF1, pickpocketSigmund);

		goOpenRobeChest = new ConditionalStep(this, goToF1Steps, "Open the chest in the room next to the Duke's room.", key);
		goOpenRobeChest.addStep(inLumbridgeF1, unlockChest);

		goIntoHamLair = new ConditionalStep(this, enterHamLair, "Enter the H.A.M lair west of Lumbridge and search a crate in its entrance for the silverware.");
		goIntoHamLair.addStep(inHamBase, searchHamCrates);
		goIntoHamLair.addStep(inLumbridgeF2, goDownFromF2);
		goIntoHamLair.addStep(inLumbridgeF1, goDownFromF1);
		goIntoHamLair.addStep(inBasement, goUpFromBasement);

		goToDukeWithSilverware = new ConditionalStep(this, goToF1Steps, "Take the silverware to Duke Horacio.", silverware);
		goToDukeWithSilverware.addDialogStep("I found the missing silverware in the HAM cave!");
		goToDukeWithSilverware.addStep(inLumbridgeF1, talkToDuke);

		travelToMakePeace = new ConditionalStep(this, goDownToBasement, "Travel through the tunnels until you reach Mistag, and give him the treaty.", lightSource, treaty);
		travelToMakePeace.addStep(inMines, talkToMistagForEnd);
		travelToMakePeace.addStep(inTunnels, talkToKazgar);
		travelToMakePeace.addStep(inBasement, climbThroughHole);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(pickaxe, lightSource));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(lumbridgeTeleports, varrockTeleport, faladorTeleport));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(goTalkToSigmundToStart, talkToAllAboutCellar, goTalkToDukeAfterHans))));
		allSteps.add(new PanelDetails("Investigating", new ArrayList<>(Arrays.asList(goMineRubble, enterTunnels, grabBrooch, goShowBroochToDuke)), pickaxe, lightSource));
		allSteps.add(new PanelDetails("Learning about goblins", new ArrayList<>(Arrays.asList(searchBookcase, readBook, talkToGenerals))));
		allSteps.add(new PanelDetails("Making contact", new ArrayList<>(Arrays.asList(goTravelToMistag, emoteAtMistag, goTalkToDukeAfterEmote)), lightSource));
		allSteps.add(new PanelDetails("Resolving tensions", new ArrayList<>(Arrays.asList(goGetKey, goOpenRobeChest, goIntoHamLair, goToDukeWithSilverware, travelToMakePeace)), lightSource));

		return allSteps;
	}
}
