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
package com.questhelper.helpers.quests.thelosttribe;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcEmoteStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.emote.QuestEmote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class TheLostTribe extends BasicQuestHelper
{
	// Required items
	ItemRequirement pickaxe;
	ItemRequirement lightSource;

	// Recommended item
	ItemRequirement varrockTeleport;
	ItemRequirement faladorTeleport;
	ItemRequirement lumbridgeTeleports;

	// Mid-quest item requirements
	ItemRequirement brooch;
	ItemRequirement book;
	ItemRequirement key;
	ItemRequirement silverware;
	ItemRequirement treaty;

	// Zones
	Zone basement;
	Zone lumbridgeF0;
	Zone lumbridgeF1;
	Zone lumbridgeF2;
	Zone tunnels;
	Zone mines;
	Zone hamBase;

	// Miscellaneous requirements
	ZoneRequirement inBasement;
	ZoneRequirement inLumbridgeF0;
	ZoneRequirement inLumbridgeF1;
	ZoneRequirement inLumbridgeF2;
	ZoneRequirement inTunnels;
	ZoneRequirement inMines;
	VarbitRequirement foundRobes;
	ZoneRequirement inHamBase;
	VarbitRequirement foundSilverwareOrToldOnSigmund;
	VarbitRequirement bobKnows;
	VarbitRequirement hansKnows;

	// Steps
	ObjectStep goDownIntoBasement;
	ObjectStep goDownFromF1;
	ObjectStep goUpToF1;
	ObjectStep goUpFromBasement;
	ObjectStep goDownFromF2;
	ObjectStep climbOutThroughHole;
	ConditionalStep goToF1Steps;

	NpcStep talkToSigmund;
	ConditionalStep goTalkToSigmundToStart;

	NpcStep talkToHans;
	NpcStep talkToBob;
	NpcStep talkToAllAboutCellar;
	ConditionalStep findGoblinWitnessSteps;

	NpcStep talkToDuke;
	ConditionalStep goTalkToDukeAfterHans;

	ConditionalStep goDownToBasement;
	ObjectStep usePickaxeOnRubble;
	ConditionalStep goMineRubble;

	ObjectStep climbThroughHole;
	ConditionalStep enterTunnels;
	DetailedQuestStep grabBrooch;
	NpcStep showBroochToDuke;
	ConditionalStep goShowBroochToDuke;

	ObjectStep searchBookcase;
	DetailedQuestStep readBook;

	NpcStep talkToGenerals;

	NpcEmoteStep walkToMistag;
	NpcEmoteStep emoteAtMistag;
	ConditionalStep goTravelToMistag;

	ConditionalStep goTalkToDukeAfterEmote;

	NpcStep pickpocketSigmund;
	ConditionalStep goGetKey;
	ObjectStep unlockChest;
	ConditionalStep goOpenRobeChest;
	ObjectStep enterHamLair;
	ObjectStep searchHamCrates;
	ConditionalStep goIntoHamLair;
	ConditionalStep goToDukeWithSilverware;

	NpcStep talkToKazgar;
	NpcStep talkToMistagForEnd;
	ConditionalStep travelToMakePeace;

	@Override
	protected void setupZones()
	{
		basement = new Zone(new WorldPoint(3208, 9614, 0), new WorldPoint(3219, 9625, 0));
		lumbridgeF0 = new Zone(new WorldPoint(3136, 3136, 0), new WorldPoint(3328, 3328, 0));
		lumbridgeF1 = new Zone(new WorldPoint(3203, 3206, 1), new WorldPoint(3217, 3231, 1));
		lumbridgeF2 = new Zone(new WorldPoint(3203, 3206, 2), new WorldPoint(3217, 3231, 2));
		tunnels = new Zone(new WorldPoint(3221, 9602, 0), new WorldPoint(3308, 9661, 0));
		mines = new Zone(new WorldPoint(3309, 9600, 0), new WorldPoint(3327, 9655, 0));
		hamBase = new Zone(new WorldPoint(3140, 9600, 0), new WorldPoint(3190, 9655, 0));
	}

	@Override
	protected void setupRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).isNotConsumed();
		lightSource = new ItemRequirement("A light source", ItemCollections.LIGHT_SOURCES).isNotConsumed();
		brooch = new ItemRequirement("Brooch", ItemID.LOST_TRIBE_BROOCH);
		book = new ItemRequirement("Goblin symbol book", ItemID.LOST_TRIBE_BOOK);
		book.setHighlightInInventory(true);
		key = new ItemRequirement("Key", ItemID.LOST_TRIBE_CHEST_KEY);
		silverware = new ItemRequirement("Silverware", ItemID.LOST_TRIBE_SILVERWARE);
		silverware.setTooltip("You can get another from the crate in the entrance of the H.A.M. hideout");

		treaty = new ItemRequirement("Peace treaty", ItemID.LOST_TRIBE_TREATY);
		treaty.setTooltip("You can get another from Duke Horacio");

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.POH_TABLET_LUMBRIDGETELEPORT, 3);
		faladorTeleport = new ItemRequirement("Falador teleport", ItemID.POH_TABLET_FALADORTELEPORT);

		inBasement = new ZoneRequirement(basement);
		inLumbridgeF0 = new ZoneRequirement(lumbridgeF0);
		inLumbridgeF1 = new ZoneRequirement(lumbridgeF1);
		inLumbridgeF2 = new ZoneRequirement(lumbridgeF2);
		inTunnels = new ZoneRequirement(tunnels);
		inMines = new ZoneRequirement(mines);
		inHamBase = new ZoneRequirement(hamBase);

		foundRobes = new VarbitRequirement(VarbitID.LOST_TRIBE_HAM, 1, Operation.GREATER_EQUAL);
		foundSilverwareOrToldOnSigmund = new VarbitRequirement(VarbitID.LOST_TRIBE_HAM, 3, Operation.GREATER_EQUAL);

		hansKnows = new VarbitRequirement(VarbitID.LOST_TRIBE_CONTACT, 0);
		bobKnows = new VarbitRequirement(VarbitID.LOST_TRIBE_CONTACT, 1);

		// 537 0->2->0, Hans
		// 537 0->1, Bob
	}

	public void setupSteps()
	{
		goDownIntoBasement = new ObjectStep(this, ObjectID.QIP_COOK_TRAPDOOR_OPEN, new WorldPoint(3209, 3216, 0), "Enter the Lumbridge Castle basement.");
		goDownFromF1 = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(3205, 3208, 1), "Go down the staircase.");
		goDownFromF1.addDialogStep("Climb down the stairs.");
		goUpToF1 = new ObjectStep(this, ObjectID.SPIRALSTAIRSBOTTOM_3, new WorldPoint(3205, 3208, 0), "Go up to the first floor of Lumbridge Castle.");
		goUpToF1.addDialogStep("Can you show me the way out of the mines?");
		goUpFromBasement = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(3209, 9616, 0), "Go up to the surface.");
		goDownFromF2 = new ObjectStep(this, ObjectID.SPIRALSTAIRSTOP_3, new WorldPoint(3205, 3208, 2), "Go downstairs.");
		climbOutThroughHole = new ObjectStep(this, ObjectID.LOST_TRIBE_CAVEWALL_HOLE_WALLDECOR, new WorldPoint(3221, 9618, 0), "");
		climbOutThroughHole.setForceClickboxHighlight(true);

		goToF1Steps = new ConditionalStep(this, goUpToF1);
		goToF1Steps.addStep(inLumbridgeF2, goDownFromF2);
		goToF1Steps.addStep(inBasement, goUpFromBasement);
		goToF1Steps.addStep(inTunnels, climbOutThroughHole);

		talkToSigmund = new NpcStep(this, NpcID.LOST_TRIBE_SIGMUND_THERE, new WorldPoint(3210, 3222, 1), "");
		talkToSigmund.addDialogSteps("Do you have any quests for me?", "Yes.");

		goTalkToSigmundToStart = new ConditionalStep(this, goToF1Steps, "Talk to Sigmund in Lumbridge Castle.");
		goTalkToSigmundToStart.addStep(inLumbridgeF1, talkToSigmund);

		// This isn't just talk to Hans, it's a random one of the NPCs to chat to
		talkToHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3222, 3218, 0), "Talk to Hans who is roaming around the castle.");
		talkToHans.addDialogStep("Do you know what happened in the cellar?");

		talkToBob = new NpcStep(this, NpcID.BOB, new WorldPoint(3231, 3203, 0), "Talk to Bob in the south of Lumbridge.");
		talkToBob.addDialogStep("Do you know what happened in the castle cellar?");

		talkToAllAboutCellar = new NpcStep(this, NpcID.COOK, "");
		talkToAllAboutCellar.addAlternateNpcs(NpcID.FATHER_AERECK);
		talkToAllAboutCellar.addDialogSteps("Do you know what happened in the castle cellar?");
		talkToAllAboutCellar.addSubSteps(talkToHans, talkToBob);

		findGoblinWitnessSteps = new ConditionalStep(this, talkToAllAboutCellar, "Talk to the Cook, Hans, Father Aereck, and Bob in Lumbridge until one tells you about seeing a goblin.");
		findGoblinWitnessSteps.addStep(inLumbridgeF2, goDownFromF2);
		findGoblinWitnessSteps.addStep(inLumbridgeF1, goDownFromF1);
		findGoblinWitnessSteps.addStep(inBasement, goUpFromBasement);
		findGoblinWitnessSteps.addStep(hansKnows, talkToHans);
		findGoblinWitnessSteps.addStep(bobKnows, talkToBob);

		talkToDuke = new NpcStep(this, NpcID.DUKE_OF_LUMBRIDGE, new WorldPoint(3210, 3222, 1), "");

		goTalkToDukeAfterHans = new ConditionalStep(this, goToF1Steps, "Talk to Duke Horacio in Lumbridge Castle.");
		// TODO: Move these dialog steps to talkToDuke instead
		goTalkToDukeAfterHans.addDialogSteps("Hans says he saw something in the cellar", "Bob says he saw something in the cellar", "Father Aereck says he saw something in the cellar", "The cook says he saw something in the cellar");
		goTalkToDukeAfterHans.addStep(inLumbridgeF1, talkToDuke);

		goDownToBasement = new ConditionalStep(this, goDownIntoBasement);
		goDownToBasement.addStep(inLumbridgeF2, goDownFromF2);
		goDownToBasement.addStep(inLumbridgeF1, goDownFromF1);

		// Name of person who said they saw something changes
		usePickaxeOnRubble = new ObjectStep(this, ObjectID.LOST_TRIBE_CELLAR_WALL, new WorldPoint(3219, 9618, 0), "");
		usePickaxeOnRubble.addIcon(ItemID.BRONZE_PICKAXE);

		goMineRubble = new ConditionalStep(this, goDownToBasement, "Use a pickaxe on the rubble in the Lumbridge Castle basement.", pickaxe.highlighted(), lightSource);
		goMineRubble.addStep(inBasement, usePickaxeOnRubble);

		climbThroughHole = new ObjectStep(this, ObjectID.LOST_TRIBE_CELLAR_WALL, new WorldPoint(3219, 9618, 0), "");
		climbThroughHole.setForceClickboxHighlight(true);

		enterTunnels = new ConditionalStep(this, goDownToBasement, "Enter the hole in Lumbridge Castle's basement.", lightSource);
		enterTunnels.addStep(inBasement, climbThroughHole);

		grabBrooch = new DetailedQuestStep(this, new WorldPoint(3230, 9610, 0), "Pick up the brooch on the floor.", brooch);

		showBroochToDuke = new NpcStep(this, NpcID.DUKE_OF_LUMBRIDGE, new WorldPoint(3210, 3222, 1), "");
		showBroochToDuke.addDialogStep("I dug through the rubble...");

		goShowBroochToDuke = new ConditionalStep(this, goToF1Steps, "Show the brooch to Duke Horacio in Lumbridge Castle.", brooch);
		goShowBroochToDuke.addStep(inLumbridgeF1, showBroochToDuke);

		searchBookcase = new ObjectStep(this, ObjectID.LOST_TRIBE_BOOKCASE, new WorldPoint(3207, 3496, 0), "Search the north west bookcase in the Varrock Castle Library.");
		searchBookcase.addTeleport(varrockTeleport);

		readBook = new DetailedQuestStep(this, "Read the entire goblin symbol book.", book);
		readBook.addWidgetHighlight(InterfaceID.LostTribeSymbolBook.LOST_TRIBE_RIGHT_ARROW);

		talkToGenerals = new NpcStep(this, NpcID.GENERAL_WARTFACE_GREEN, new WorldPoint(2957, 3512, 0), "Talk to the Goblin Generals in the Goblin Village.");
		talkToGenerals.addTeleport(faladorTeleport);
		talkToGenerals.addDialogSteps("Have you ever heard of the Dorgeshuun?", "It doesn't really matter", "Well either way they refused to fight", "Well I found a brooch underground...", "Well why not show me both greetings?");

		List<WorldPoint> travelLine = Arrays.asList(
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
		);

		walkToMistag = new NpcEmoteStep(this, NpcID.LOST_TRIBE_MISTAG_1OP, QuestEmote.GOBLIN_BOW, new WorldPoint(3319, 9615, 0), "Travel through the tunnels. Make sure you follow the marked path, or you'll be dropped into a hole and your light source extinguished!", lightSource);
		walkToMistag.setLinePoints(travelLine);

		emoteAtMistag = new NpcEmoteStep(this, NpcID.LOST_TRIBE_MISTAG_1OP, QuestEmote.GOBLIN_BOW, new WorldPoint(3319, 9615, 0), "Perform the Goblin Bow emote next to Mistag and talk to him.", lightSource);

		goTravelToMistag = new ConditionalStep(this, goDownToBasement, "Travel through the tunnels under Lumbridge until you reach Mistag.", lightSource);
		goTravelToMistag.addStep(inBasement, climbThroughHole);
		goTravelToMistag.addSubSteps(walkToMistag);

		goTalkToDukeAfterEmote = new ConditionalStep(this, goToF1Steps, "Talk to Duke Horacio in Lumbridge Castle. You can fast-travel with Mistag back to Lumbridge.");
		// TODO: Move dialog steps to npc step
		goTalkToDukeAfterEmote.addDialogSteps("I've made contact with the cave goblins...");
		goTalkToDukeAfterEmote.addStep(inLumbridgeF1, talkToDuke);

		pickpocketSigmund = new NpcStep(this, NpcID.LOST_TRIBE_SIGMUND_THERE, new WorldPoint(3210, 3222, 1), "");

		goGetKey = new ConditionalStep(this, goToF1Steps, "Pickpocket Sigmund for a key.");
		goGetKey.addStep(inLumbridgeF1, pickpocketSigmund);

		unlockChest = new ObjectStep(this, ObjectID.LOST_TRIBE_CHEST, new WorldPoint(3209, 3217, 1), "");

		goOpenRobeChest = new ConditionalStep(this, goToF1Steps, "Open the chest in the room next to the Duke's room.", key);
		goOpenRobeChest.addStep(inLumbridgeF1, unlockChest);

		enterHamLair = new ObjectStep(this, ObjectID.HAM_MULTI_TRAPDOOR, new WorldPoint(3166, 3252, 0), "");

		searchHamCrates = new ObjectStep(this, ObjectID.LOST_TRIBE_CRATE, new WorldPoint(3152, 9645, 0), "");

		goIntoHamLair = new ConditionalStep(this, enterHamLair, "Enter the H.A.M lair west of Lumbridge and search a crate in its entrance for the silverware.");
		goIntoHamLair.addStep(inHamBase, searchHamCrates);
		goIntoHamLair.addStep(inLumbridgeF2, goDownFromF2);
		goIntoHamLair.addStep(inLumbridgeF1, goDownFromF1);
		goIntoHamLair.addStep(inBasement, goUpFromBasement);

		goToDukeWithSilverware = new ConditionalStep(this, goToF1Steps, "Take the silverware to Duke Horacio.", silverware);
		goToDukeWithSilverware.addDialogStep("I found the missing silverware in the HAM cave!");
		goToDukeWithSilverware.addStep(inLumbridgeF1, talkToDuke);

		talkToKazgar = new NpcStep(this, NpcID.LOST_TRIBE_GUIDE_2OPS, new WorldPoint(3230, 9610, 0), "Travel with Kazgar to shortcut to Mistag.");
		talkToMistagForEnd = new NpcStep(this, NpcID.LOST_TRIBE_MISTAG_2OPS, new WorldPoint(3319, 9615, 0), "");

		travelToMakePeace = new ConditionalStep(this, goDownToBasement, "Travel through the tunnels until you reach Mistag, and give him the treaty.", lightSource, treaty);
		travelToMakePeace.addStep(inMines, talkToMistagForEnd);
		travelToMakePeace.addStep(inTunnels, talkToKazgar);
		travelToMakePeace.addStep(inBasement, climbThroughHole);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, goTalkToSigmundToStart);

		steps.put(1, findGoblinWitnessSteps);

		steps.put(2, goTalkToDukeAfterHans);

		steps.put(3, goMineRubble);

		var goGrabBrooch = new ConditionalStep(this, enterTunnels);
		goGrabBrooch.addStep(brooch, goShowBroochToDuke);
		goGrabBrooch.addStep(inTunnels, grabBrooch);
		steps.put(4, goGrabBrooch);

		var getBook = new ConditionalStep(this, searchBookcase);
		getBook.addStep(book, readBook);
		steps.put(5, getBook);

		steps.put(6, talkToGenerals);

		var makeContactSteps = new ConditionalStep(this, goTravelToMistag);
		makeContactSteps.addStep(inMines, emoteAtMistag);
		makeContactSteps.addStep(inTunnels, walkToMistag);
		steps.put(7, makeContactSteps);

		steps.put(8, goTalkToDukeAfterEmote);

		var revealSigmund = new ConditionalStep(this, goGetKey);
		revealSigmund.addStep(silverware.alsoCheckBank(), goToDukeWithSilverware);
		revealSigmund.addStep(foundRobes, goIntoHamLair);
		revealSigmund.addStep(key, goOpenRobeChest);
		steps.put(9, revealSigmund);

		steps.put(10, travelToMakePeace);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			pickaxe,
			lightSource
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			lumbridgeTeleports,
			varrockTeleport,
			faladorTeleport
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.GOBLIN_DIPLOMACY, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED),
			new SkillRequirement(Skill.AGILITY, 13, true),
			new SkillRequirement(Skill.THIEVING, 13, true),
			new SkillRequirement(Skill.MINING, 17, true)
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.MINING, 3000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Ring of Life", ItemID.RING_OF_LIFE, 1)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to the Dorgesh-Kaan mine."),
			new UnlockReward("Access to Nardok's Bone Weapon Store"),
			new UnlockReward("2 new goblin emotes.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			goTalkToSigmundToStart,
			findGoblinWitnessSteps,
			goTalkToDukeAfterHans
		)));

		sections.add(new PanelDetails("Investigating", List.of(
			goMineRubble,
			enterTunnels,
			grabBrooch,
			goShowBroochToDuke
		), List.of(
			pickaxe,
			lightSource
		)));

		sections.add(new PanelDetails("Learning about goblins", List.of(
			searchBookcase,
			readBook,
			talkToGenerals
		)));

		sections.add(new PanelDetails("Making contact", List.of(
			goTravelToMistag,
			emoteAtMistag,
			goTalkToDukeAfterEmote
		), List.of(
			lightSource
		)));

		sections.add(new PanelDetails("Resolving tensions", List.of(
			goGetKey,
			goOpenRobeChest,
			goIntoHamLair,
			goToDukeWithSilverware,
			travelToMakePeace
		), List.of(
			lightSource
		)));

		return sections;
	}
}
