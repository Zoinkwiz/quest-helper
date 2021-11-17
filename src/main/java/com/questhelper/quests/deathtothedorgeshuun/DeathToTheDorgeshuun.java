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
package com.questhelper.quests.deathtothedorgeshuun;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.npc.FollowerRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.npc.NpcInteractingRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
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
	quest = QuestHelperQuest.DEATH_TO_THE_DORGESHUUN
)
public class DeathToTheDorgeshuun extends BasicQuestHelper
{
	//Items Required
	ItemRequirement pickaxe, lightSource, brooch, book, key, silverware, treaty, varrockTeleport, faladorTeleport, lumbridgeTeleports,
		hamShirt2, hamRobe2, hamHood2, hamBoot2, hamGloves2, hamLogo2, hamCloak2, hamSet2, hamShirt, hamRobe, hamHood, hamBoot, hamGloves,
		hamLogo, hamCloak, hamSet, zanik, pickaxeHighlighted, tinderbox, crate, combatGear, gamesNecklace;

	FollowerRequirement zanikFollower;

	Requirement inBasement, inLumbridgeF0, inLumbridgeF1, inLumbridgeF2, inTunnels, inMines, inHamBase, zanikIsFollowing,
		talkedToShopkeeper, talkedToWoman, talkedToDuke, talkedToAereck, talkedToGoblins, goneOutside, heardSpeaker, isBehindGuard1,
		killedGuard1, isNearGuard4, isNearGuard5, inStoreroom, killedGuard2, killedGuard3, killedGuard4, killedGuard5, zanikWaitingFor4,
		zanikWaitingFor5, isDisguisedZanikFollowing, zanikPickedUp, ropeAddedToHole, minedRocks, inSwamp, inJunaRoom, inMill, killedGuards,
		talkedToJohn;

	DetailedQuestStep goDownFromF2, talkToMistag, talkToZanik, talkToMistagToTravel, talkToCook, talkToDuke, talkToHans,
		talkToWoman, talkToBob, talkToAereck, talkToGuide, approachGoblins, talkToShopkeeper, goOutside, talkToZanikAboutOrigin,
		listenToSpeaker, standNearTrapdoor, goDownTrapdoor, standBehindGuard1, talkToGuard1, talkToGuard2, tellZanikToKillGuard3, talkToJohanhus,
		standNearGuard4, tellZanikToWaitForGuard4, runSouthToLureGuard4, standNearGuard5, tellZanikToWaitForGuard5, lureGuard5, listenToDoor,
		checkZanikCorpse, mineRocks, climbIntoSwamp, enterJunaArea, talkToJuna, talkToJunaMore, searchCrate, enterMill, killGuards, killSigmund,
		smashDrill, enterExit, goDownFromF1, goUpToF1, goDownIntoBasement, climbThroughHole, goUpFromBasement, enterHamLair, talkToKazgar;

	ConditionalStep goTalkToMistag, goTalkToZanik, goHaveZanikFollow, goTalkToCook, goTalkToDuke, goTalkToHans, goTalkToWoman, goTalkToBob,
		goTalkToAereck, goTalkToGuide, goNearGoblins, goTalkToShopkeeper, goOutsideSteps, goIntoHamLair, goClearRocks, goToJunaSteps,
		learnZanikStory, goGetZanikForMill;

	//Zones
	Zone basement, lumbridgeF0, lumbridgeF1, lumbridgeF2, tunnels, mines, hamBase, behindGuard1, nearGuard4, nearGuard5, storeRoom, swamp, junaRoom,
		mill1, mill2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, goTalkToMistag);
		steps.put(1, goTalkToZanik);
		steps.put(2, goTalkToZanik);

		ConditionalStep takeZanikAbout = new ConditionalStep(this, goHaveZanikFollow);
		takeZanikAbout.addStep(new Conditions(zanikIsFollowing, goneOutside, talkedToDuke, talkedToWoman, talkedToAereck, talkedToGoblins, talkedToShopkeeper), talkToZanikAboutOrigin);
		takeZanikAbout.addStep(new Conditions(zanikIsFollowing, goneOutside, talkedToDuke, talkedToWoman, talkedToAereck, talkedToGoblins), goTalkToShopkeeper);
		takeZanikAbout.addStep(new Conditions(zanikIsFollowing, goneOutside, talkedToDuke, talkedToWoman, talkedToAereck), goNearGoblins);
		takeZanikAbout.addStep(new Conditions(zanikIsFollowing, goneOutside, talkedToDuke, talkedToWoman), goTalkToAereck);
		takeZanikAbout.addStep(new Conditions(zanikIsFollowing, goneOutside, talkedToDuke), goTalkToWoman);
		takeZanikAbout.addStep(new Conditions(zanikIsFollowing, talkedToDuke), goOutsideSteps);
		takeZanikAbout.addStep(zanikIsFollowing, goTalkToDuke);
		steps.put(3, takeZanikAbout);

		ConditionalStep infiltrateTheHam = new ConditionalStep(this, goHaveZanikFollow);
		infiltrateTheHam.addStep(new Conditions(zanikIsFollowing, inHamBase, talkedToJohn, heardSpeaker), standNearTrapdoor);
		infiltrateTheHam.addStep(new Conditions(zanikIsFollowing, inHamBase, talkedToJohn), listenToSpeaker);
		infiltrateTheHam.addStep(new Conditions(zanikIsFollowing, inHamBase), talkToJohanhus);
		infiltrateTheHam.addStep(zanikIsFollowing, goIntoHamLair);
		steps.put(4, infiltrateTheHam);

		ConditionalStep findingTheHamMeeting = new ConditionalStep(this, goHaveZanikFollow);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard5), listenToDoor);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard4, zanikWaitingFor5), lureGuard5);
		findingTheHamMeeting.addStep(new Conditions(isNearGuard5, killedGuard4), tellZanikToWaitForGuard5);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard4), standNearGuard5);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard3, zanikWaitingFor4), runSouthToLureGuard4);
		findingTheHamMeeting.addStep(new Conditions(isNearGuard4, killedGuard3), tellZanikToWaitForGuard4);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard3), standNearGuard4);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard2), tellZanikToKillGuard3);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom, killedGuard1), talkToGuard2);
		findingTheHamMeeting.addStep(new Conditions(isBehindGuard1), talkToGuard1);
		findingTheHamMeeting.addStep(new Conditions(inStoreroom), standBehindGuard1);
		findingTheHamMeeting.addStep(new Conditions(zanikIsFollowing, inHamBase), goDownTrapdoor);
		findingTheHamMeeting.addStep(zanikIsFollowing, goIntoHamLair);
		steps.put(5, findingTheHamMeeting);

		ConditionalStep savingZanik = new ConditionalStep(this, checkZanikCorpse);
		savingZanik.addStep(new Conditions(zanikPickedUp, inJunaRoom), talkToJuna);
		savingZanik.addStep(new Conditions(zanikPickedUp, inSwamp), enterJunaArea);
		savingZanik.addStep(new Conditions(zanikPickedUp, minedRocks), goToJunaSteps);
		savingZanik.addStep(new Conditions(zanikPickedUp, inTunnels), mineRocks);
		savingZanik.addStep(zanikPickedUp, goClearRocks);
		steps.put(6, savingZanik);

		steps.put(7, talkToJunaMore);

		steps.put(8, learnZanikStory);

		ConditionalStep infiltrateMill = new ConditionalStep(this, goGetZanikForMill);
		infiltrateMill.addStep(inMill, killGuards);
		infiltrateMill.addStep(crate, enterMill);
		infiltrateMill.addStep(zanikIsFollowing, searchCrate);
		steps.put(9, infiltrateMill);

		ConditionalStep defeatSigmund = new ConditionalStep(this, enterMill);
		defeatSigmund.addStep(new Conditions(inMill, killedGuards), killSigmund);
		defeatSigmund.addStep(inMill, killGuards);
		steps.put(10, defeatSigmund);

		ConditionalStep goSmashMachine = new ConditionalStep(this, enterMill);
		goSmashMachine.addStep(inMill, smashDrill);
		steps.put(11, goSmashMachine);

		ConditionalStep goFinishQuest = new ConditionalStep(this, enterMill);
		goFinishQuest.addStep(inMill, enterExit);
		steps.put(12, goFinishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		pickaxeHighlighted = new ItemRequirement("Any pickaxe", ItemCollections.getPickaxes());
		pickaxeHighlighted.setHighlightInInventory(true);
		lightSource = new ItemRequirement("A light source", ItemCollections.getLightSources());
		brooch = new ItemRequirement("Brooch", ItemID.BROOCH);
		book = new ItemRequirement("Goblin symbol book", ItemID.GOBLIN_SYMBOL_BOOK);
		book.setHighlightInInventory(true);
		key = new ItemRequirement("Key", ItemID.KEY_5010);
		silverware = new ItemRequirement("Silverware", ItemID.SILVERWARE);
		silverware.setTooltip("You can get another from the crate in the entrance of the H.A.M. hideout");

		treaty = new ItemRequirement("Peace treaty", ItemID.PEACE_TREATY);
		treaty.setTooltip("You can get another from Duke Horacio");

		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.VARROCK_TELEPORT);
		lumbridgeTeleports = new ItemRequirement("Lumbridge teleports", ItemID.LUMBRIDGE_TELEPORT, 3);
		faladorTeleport = new ItemRequirement("Falador teleports", ItemID.FALADOR_TELEPORT);

		hamShirt = new ItemRequirement("Ham shirt", ItemID.HAM_SHIRT, 1, true);
		hamRobe = new ItemRequirement("Ham robe", ItemID.HAM_ROBE, 1, true);
		hamHood = new ItemRequirement("Ham hood", ItemID.HAM_HOOD, 1, true);
		hamBoot = new ItemRequirement("Ham boots", ItemID.HAM_BOOTS, 1, true);
		hamGloves = new ItemRequirement("Ham gloves", ItemID.HAM_GLOVES, 1, true);
		hamLogo = new ItemRequirement("Ham logo", ItemID.HAM_LOGO, 1, true);
		hamCloak = new ItemRequirement("Ham cloak", ItemID.HAM_CLOAK, 1, true);

		hamShirt2 = new ItemRequirement("Ham shirt", ItemID.HAM_SHIRT, 2);
		hamRobe2 = new ItemRequirement("Ham robe", ItemID.HAM_ROBE, 2);
		hamHood2 = new ItemRequirement("Ham hood", ItemID.HAM_HOOD, 2);
		hamBoot2 = new ItemRequirement("Ham boots", ItemID.HAM_BOOTS, 2);
		hamGloves2 = new ItemRequirement("Ham gloves", ItemID.HAM_GLOVES, 2);
		hamLogo2 = new ItemRequirement("Ham logo", ItemID.HAM_LOGO, 2);
		hamCloak2 = new ItemRequirement("Ham cloak", ItemID.HAM_CLOAK, 2);

		hamSet = new ItemRequirements("Full ham robe sets (7 pieces)(equipped)", hamShirt, hamRobe, hamHood, hamBoot, hamGloves, hamLogo, hamCloak);
		hamSet2 = new ItemRequirements("2 full ham robe sets (7 pieces/set)", hamShirt2, hamRobe2, hamHood2, hamBoot2, hamGloves2, hamLogo2, hamCloak2);
		hamSet2.setTooltip("The chance of thieving a ham clothing piece increases massively AFTER starting the quest");

		zanik = new ItemRequirement("Zanik", ItemID.ZANIK);
		zanikFollower = new FollowerRequirement("Zanik following you. If she's not, retrieve her from Lumbridge Basement", NpcID.ZANIK_4508);

		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);

		crate = new ItemRequirement("Crate with Zanik", ItemID.CRATE_WITH_ZANIK, 1, true);

		combatGear = new ItemRequirement("Magic or melee combat gear", -1, -1);
		gamesNecklace = new ItemRequirement("Games necklace (requires Tears of Guthix to teleport to Juna)", ItemID.GAMES_NECKLACE8);
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
		behindGuard1 = new Zone(new WorldPoint(2569, 5189, 0), new WorldPoint(2569, 5189, 0));
		nearGuard4 = new Zone(new WorldPoint(2576, 5195, 0), new WorldPoint(2576, 5195, 0));
		nearGuard5 = new Zone(new WorldPoint(2577, 5200, 0), new WorldPoint(2577, 5200, 0));
		storeRoom = new Zone(new WorldPoint(2566, 5185, 0), new WorldPoint(2577, 5203, 0));
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));
		mill1 = new Zone(new WorldPoint(3204, 9661, 0), new WorldPoint(3246, 9663, 0));
		mill2 = new Zone(new WorldPoint(1991, 5056, 0), new WorldPoint(2030, 5097, 0));
	}

	public void setupConditions()
	{
		inBasement = new ZoneRequirement(basement);
		inLumbridgeF0 = new ZoneRequirement(lumbridgeF0);
		inLumbridgeF1 = new ZoneRequirement(lumbridgeF1);
		inLumbridgeF2 = new ZoneRequirement(lumbridgeF2);
		inTunnels = new ZoneRequirement(tunnels);
		inMines = new ZoneRequirement(mines);
		inHamBase = new ZoneRequirement(hamBase);
		isBehindGuard1 = new ZoneRequirement(behindGuard1);
		isNearGuard4 = new ZoneRequirement(nearGuard4);
		isNearGuard5 = new ZoneRequirement(nearGuard5);
		inStoreroom = new ZoneRequirement(storeRoom);
		inSwamp = new ZoneRequirement(swamp);
		inJunaRoom = new ZoneRequirement(junaRoom);
		inMill = new ZoneRequirement(mill1, mill2);


		talkedToDuke = new VarbitRequirement(2259, 1);
		talkedToAereck = new VarbitRequirement(2260, 1);
		talkedToGoblins = new VarbitRequirement(2261, 1);
		talkedToWoman = new VarbitRequirement(2262, 1);
		goneOutside = new VarbitRequirement(2263, 1);
		zanikIsFollowing = new Conditions(LogicType.OR, new VarbitRequirement(2264, 0));
		talkedToShopkeeper = new VarbitRequirement(2265, 1);
		heardSpeaker = new VarbitRequirement(2268, 1);
		talkedToJohn = new VarbitRequirement(2269, 1);

		killedGuard1 = new VarbitRequirement(2275, 1);
		killedGuard2 = new VarbitRequirement(2277, 1);
		killedGuard3 = new VarbitRequirement(2278, 1);
		killedGuard4 = new VarbitRequirement(2280, 1);
		killedGuard5 = new VarbitRequirement(2282, 1);

		isDisguisedZanikFollowing = new NpcInteractingRequirement(NpcID.ZANIK_4509);

		zanikWaitingFor4 = new Conditions(new Conditions(LogicType.NOR, isDisguisedZanikFollowing), new NpcCondition(NpcID.ZANIK_4509, new Zone(new WorldPoint(2575, 5195, 0), new WorldPoint(2576, 5195, 0))));
		zanikWaitingFor5 = new Conditions(new Conditions(LogicType.NOR, isDisguisedZanikFollowing), new NpcCondition(NpcID.ZANIK_4509, new Zone(new WorldPoint(2577, 5199, 0), new WorldPoint(2577, 5200, 0))));

		zanikPickedUp = new VarbitRequirement(2271, 0);

		ropeAddedToHole = new VarbitRequirement(279, 1);
		minedRocks = new VarbitRequirement(538, 1);

		killedGuards = new VarbitRequirement(2283, 3);
	}

	public void setupSteps()
	{
		goDownIntoBasement = new ObjectStep(this, ObjectID.TRAPDOOR_14880, new WorldPoint(3209, 3216, 0), "Enter the Lumbridge Castle basement.");
		goDownFromF1 = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(3205, 3208, 1), "Go down the staircase.");
		goDownFromF1.addDialogStep("Climb down the stairs.");
		goUpToF1 = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(3205, 3208, 0), "Go up to the first floor of Lumbridge Castle.");
		goUpFromBasement = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(3209, 9616, 0), "Go up to the surface.");
		goDownFromF2 = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(3205, 3208, 2), "Go downstairs.");

		talkToKazgar = new NpcStep(this, NpcID.KAZGAR, new WorldPoint(3230, 9610, 0), "Travel with Kazgar to shortcut to Mistag.");
		talkToMistag = new NpcStep(this, NpcID.MISTAG_7298, new WorldPoint(3319, 9615, 0), "");
		talkToMistagToTravel = new NpcStep(this, NpcID.MISTAG_7298, new WorldPoint(3319, 9615, 0), "Travel with Mistag back to Lumbridge.");
		talkToZanik = new NpcStep(this, NpcID.ZANIK_4506, new WorldPoint(3212, 9620, 0), "");

		talkToCook = new NpcStep(this, NpcID.COOK_4626, new WorldPoint(3209, 3215, 0), "");
		talkToDuke = new NpcStep(this, NpcID.DUKE_HORACIO, new WorldPoint(3210, 3222, 1), "");
		talkToHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3222, 3218, 0), "");
		talkToWoman = new NpcStep(this, NpcID.WOMAN, new WorldPoint(3224, 3218, 0), "", true);
		((NpcStep) (talkToWoman)).addAlternateNpcs(NpcID.MAN_3108, NpcID.MAN_3106, NpcID.WOMAN_3111);
		talkToGuide = new NpcStep(this, NpcID.LUMBRIDGE_GUIDE, new WorldPoint(3238, 3220, 0), "");
		talkToBob = new NpcStep(this, NpcID.BOB_10619, new WorldPoint(3231, 3208, 0), "");
		talkToAereck = new NpcStep(this, NpcID.FATHER_AERECK, new WorldPoint(3244, 3210, 0), "");
		approachGoblins = new DetailedQuestStep(this, new WorldPoint(3247, 3235, 0), "");
		talkToShopkeeper = new NpcStep(this, NpcID.SHOP_KEEPER, new WorldPoint(3211, 3247, 0), "");

		talkToZanikAboutOrigin = new NpcStep(this, NpcID.ZANIK_4508, "Talk to Zanik about her head sign.");
		talkToZanikAboutOrigin.addDialogSteps("Have you seen enough of Lumbridge yet?", "Yes please!");

		goOutside = new DetailedQuestStep(this, new WorldPoint(3217, 3219, 0), "");

		climbThroughHole = new ObjectStep(this, NullObjectID.NULL_6898, new WorldPoint(3219, 9618, 0), "");

		enterHamLair = new ObjectStep(this, NullObjectID.NULL_5492, new WorldPoint(3166, 3252, 0), "");

		talkToJohanhus = new NpcStep(this, NpcID.JOHANHUS_ULSBRECHT, new WorldPoint(3173, 9619, 0), "Talk to Johanhus in the south east of the base.");
		talkToJohanhus.addDialogStep("Are you planning to do anything about the cave goblins?");

		listenToSpeaker = new DetailedQuestStep(this, new WorldPoint(3166, 9623, 0), "Go stand at the south of the stage until Zanik talks about the speaker. Don't skip the dialog.");

		standNearTrapdoor = new DetailedQuestStep(this, new WorldPoint(3166, 9623, 0), "Go stand next to the rubble south of the stage until Zanik notices it.");

		goDownTrapdoor = new ObjectStep(this, NullObjectID.NULL_15766, new WorldPoint(3166, 9622, 0), "Picklock the trapdoor south of the stage and go down it.");

		standBehindGuard1 = new DetailedQuestStep(this, new WorldPoint(2569, 5189, 0), "Stand behind the guard and talk to them so they turn their back to Zanik.");
		talkToGuard1 = new NpcStep(this, NpcID.GUARD_4516, new WorldPoint(2570, 5189, 0), "Talk to them so they turn their back to Zanik.");
		standBehindGuard1.addSubSteps(talkToGuard1);

		talkToGuard2 = new NpcStep(this, NpcID.GUARD_4517, new WorldPoint(2566, 5192, 0), "Go through the nearby crack, then out the other side. Talk to the second guard to turn them around so Zanik can kill them.");
		talkToGuard2.setLinePoints(Arrays.asList(
			new WorldPoint(2569, 5189, 0),
			new WorldPoint(2569, 5195, 0),
			new WorldPoint(2566, 5195, 0),
			new WorldPoint(2566, 5193, 0)
		));

		tellZanikToKillGuard3 = new NpcStep(this, NpcID.ZANIK_4509, "Wait for the third guard to be walking away, then tell Zanik to kill them.");
		tellZanikToKillGuard3.addDialogStep("Now!");
		standNearGuard4 = new DetailedQuestStep(this, new WorldPoint(2576, 5195, 0), "Stand near to the next guard, then tell Zanik to wait there.");
		tellZanikToWaitForGuard4 = new NpcStep(this, NpcID.ZANIK_4509, "Tell Zanik to wait.");
		tellZanikToWaitForGuard4.addDialogStep("Wait here.");
		runSouthToLureGuard4 = new DetailedQuestStep(this, new WorldPoint(2577, 5191, 0), "Run east then south to lure the guard past Zanik.");
		standNearGuard5 = new DetailedQuestStep(this, new WorldPoint(2577, 5200, 0), "Stand in the north east, just out of sight of the last guard, and tell Zanik to wait there.");
		tellZanikToWaitForGuard5 = new NpcStep(this, NpcID.ZANIK_4509, "Tell Zanik to wait.");
		tellZanikToWaitForGuard5.addDialogStep("Wait here.");
		lureGuard5 = new DetailedQuestStep(this, new WorldPoint(2566, 5201, 0), "Approach the final guard from the west so Zanik can kill them.");
		lureGuard5.setLinePoints(Arrays.asList(
			new WorldPoint(2577, 5199, 0),
			new WorldPoint(2577, 5195, 0),
			new WorldPoint(2566, 5195, 0),
			new WorldPoint(2566, 5201, 0)
		));

		checkZanikCorpse = new ObjectStep(this, NullObjectID.NULL_15712, new WorldPoint(3161, 3246, 0), "Inspect Zanik outside the H.A.M base.");
		listenToDoor = new ObjectStep(this, ObjectID.LARGE_DOOR_15757, new WorldPoint(2571, 5204, 0), "Listen to the large door.");

		mineRocks = new ObjectStep(this, ObjectID.HOLE_6912, new WorldPoint(3224, 9602, 0), "Use a pickaxe on the rocks to the south.", pickaxeHighlighted, lightSource);
		mineRocks.addIcon(ItemID.BRONZE_PICKAXE);

		climbIntoSwamp = new ObjectStep(this, ObjectID.HOLE_6912, new WorldPoint(3224, 9602, 0), "Climb into the Lumbridge Swamp.");

		enterJunaArea = new ObjectStep(this, ObjectID.TUNNEL_6659, new WorldPoint(3226, 9540, 0), "Enter the cave in the south east corner of the swamp.", lightSource, zanik);
		talkToJuna = new ObjectStep(this, NullObjectID.NULL_3193, new WorldPoint(3252, 9517, 2), "Talk to Juna.", lightSource, zanik);
		talkToJunaMore = new ObjectStep(this, NullObjectID.NULL_3193, new WorldPoint(3252, 9517, 2), "Talk to Juna with both hands free. Collect 20 tears of Guthix.", lightSource);
		talkToJunaMore.addDialogStep("Yes.");

		searchCrate = new ObjectStep(this, ObjectID.CRATE_15704, new WorldPoint(3228, 3280, 0), "Search a crate south of the farm east of the Lumbridge.", zanikFollower, combatGear, hamSet);
		searchCrate.addDialogSteps("I don't know, what are you thinking?", "Good idea.");
		enterMill = new ObjectStep(this, NullObjectID.NULL_15765, new WorldPoint(3230, 3286, 0), "Enter the trapdoor outside the farm.", combatGear, hamSet);
		killGuards = new NpcStep(this, NpcID.GUARD, new WorldPoint(2000, 5087, 0), "Kill the guards to the west.", true);
		killSigmund = new NpcStep(this, NpcID.SIGMUND_991, new WorldPoint(2000, 5087, 0), "Defeat Sigmund. You need to use magic or melee to hurt him.", combatGear);
		smashDrill = new ObjectStep(this, ObjectID.DRILLING_MACHINE, new WorldPoint(1998, 5088, 0), "Destroy the drilling machine.");
		enterExit = new ObjectStep(this, NullObjectID.NULL_15764, new WorldPoint(3245, 9661, 0), "Leave the area via the exit to the south to finish the quest.");
	}

	private void setupConditionalSteps()
	{
		ConditionalStep goToF1Steps = new ConditionalStep(this, goUpToF1);
		goToF1Steps.addStep(inLumbridgeF2, goDownFromF2);
		goToF1Steps.addStep(inBasement, goUpFromBasement);

		ConditionalStep goDownToBasement = new ConditionalStep(this, goDownIntoBasement);
		goDownToBasement.addStep(inLumbridgeF2, goDownFromF2);
		goDownToBasement.addStep(inLumbridgeF1, goDownFromF1);

		goTalkToMistag = new ConditionalStep(this, goDownToBasement, "Travel through the tunnels under Lumbridge Castle until you reach Mistag, then talk to him.", lightSource);
		goTalkToMistag.addStep(inMines, talkToMistag);
		goTalkToMistag.addStep(inTunnels, talkToKazgar);
		goTalkToMistag.addStep(inBasement, climbThroughHole);
		goTalkToMistag.addDialogSteps("What is this favour?", "I'll act as a guide.");

		goTalkToZanik = new ConditionalStep(this, goDownToBasement, "Talk to Zanik in Lumbridge Castle's basement.", hamHood2, hamShirt2, hamRobe2, hamBoot2, hamGloves2, hamCloak2, hamLogo2);
		goTalkToZanik.addStep(inMines, talkToMistagToTravel);
		goTalkToZanik.addStep(inTunnels, climbThroughHole);
		goTalkToZanik.addStep(inBasement, talkToZanik);
		if (client.getLocalPlayer() != null)
		{
			goTalkToZanik.addDialogStep("Yes, I'm " + client.getLocalPlayer().getName() + "!");
		}

		goHaveZanikFollow = new ConditionalStep(this, goDownToBasement, "Talk to Zanik in Lumbridge Castle's basement.");
		goHaveZanikFollow.addStep(inBasement, talkToZanik);
		goHaveZanikFollow.addDialogSteps("Yes.", "Yes please!");

		goTalkToCook = new ConditionalStep(this, talkToCook, "Talk to the Lumbridge Cook.", zanikFollower);
		goTalkToCook.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToCook.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToCook.addStep(inBasement, goUpFromBasement);

		goTalkToDuke = new ConditionalStep(this, goUpToF1, "Talk to the Duke.", zanikFollower);
		goTalkToDuke.addStep(inLumbridgeF1, talkToDuke);

		goOutsideSteps = new ConditionalStep(this, goOutside, "Leave the castle, and go through the entire leaving cutscene. If you cut it short, enter/leave again.", zanikFollower);
		goOutsideSteps.addStep(inLumbridgeF2, goDownFromF2);
		goOutsideSteps.addStep(inLumbridgeF1, goDownFromF1);
		goOutsideSteps.addStep(inBasement, goUpFromBasement);

		goTalkToHans = new ConditionalStep(this, talkToHans, "Talk to Hans.", zanikFollower);
		goTalkToHans.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToHans.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToHans.addStep(inBasement, goUpFromBasement);

		goTalkToWoman = new ConditionalStep(this, talkToWoman, "Talk to any man or woman around Lumbridge.", zanikFollower);
		goTalkToWoman.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToWoman.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToWoman.addStep(inBasement, goUpFromBasement);

		goTalkToGuide = new ConditionalStep(this, talkToGuide, "Talk to the Lumbridge Guide.", zanikFollower);
		goTalkToGuide.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToGuide.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToGuide.addStep(inBasement, goUpFromBasement);

		goTalkToBob = new ConditionalStep(this, talkToBob, "Talk to Bob in south Lumbridge.", zanikFollower);
		goTalkToBob.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToBob.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToBob.addStep(inBasement, goUpFromBasement);

		goTalkToAereck = new ConditionalStep(this, talkToAereck, "Talk to Father Aereck.", zanikFollower);
		goTalkToAereck.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToAereck.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToAereck.addStep(inBasement, goUpFromBasement);

		goNearGoblins = new ConditionalStep(this, approachGoblins, "Approach the goblins east of the River Lum.", zanikFollower);
		goNearGoblins.addStep(inLumbridgeF2, goDownFromF2);
		goNearGoblins.addStep(inLumbridgeF1, goDownFromF1);
		goNearGoblins.addStep(inBasement, goUpFromBasement);

		goTalkToShopkeeper = new ConditionalStep(this, talkToShopkeeper, "Talk to the Lumbridge General Store shopkeeper.", zanikFollower);
		goTalkToShopkeeper.addStep(inLumbridgeF2, goDownFromF2);
		goTalkToShopkeeper.addStep(inLumbridgeF1, goDownFromF1);
		goTalkToShopkeeper.addStep(inBasement, goUpFromBasement);

		goIntoHamLair = new ConditionalStep(this, enterHamLair, "Enter the H.A.M lair west of Lumbridge.", hamHood, hamShirt, hamRobe, hamBoot, hamGloves, hamCloak, hamLogo);
		goIntoHamLair.addStep(inLumbridgeF2, goDownFromF2);
		goIntoHamLair.addStep(inLumbridgeF1, goDownFromF1);
		goIntoHamLair.addStep(inBasement, goUpFromBasement);

		goClearRocks = new ConditionalStep(this, goDownToBasement, "Take Zanik to Juna in the Lumbridge Swamp. Using a games necklace is recommended.", pickaxe, lightSource, tinderbox, zanik);
		goClearRocks.addStep(inBasement, climbThroughHole);

		goToJunaSteps = new ConditionalStep(this, goDownToBasement, "Take Zanik to Juna in the Lumbridge Swamp. Using a games necklace is recommended.", lightSource, tinderbox, zanik);
		goToJunaSteps.addStep(inTunnels, climbIntoSwamp);
		goToJunaSteps.addStep(inBasement, climbThroughHole);
		goToJunaSteps.addSubSteps(goClearRocks, mineRocks, enterJunaArea, talkToJuna, talkToJunaMore);

		learnZanikStory = new ConditionalStep(this, goHaveZanikFollow, "Talk to Zanik about what happened to her.");
		learnZanikStory.addStep(zanikIsFollowing, talkToZanik);

		goGetZanikForMill = new ConditionalStep(this, goHaveZanikFollow, "Talk to Zanik under the Lumbridge Castle.");
		goGetZanikForMill.addDialogStep("Let's go!");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(lightSource, hamSet2, tinderbox, pickaxe, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(lumbridgeTeleports, gamesNecklace);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("3 H.A.M. Guards (level 22)", "Sigmund (level 50, melee or magic only)");
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("If you plan on getting the H.A.M. robes yourself rather than buying them, make sure to do so after starting the quest. The drop rate for robes is considerably increased during the quest.");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.THIEVING, 2000),
				new ExperienceReward(Skill.RANGED, 2000));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to use Dorgeshuun Special Attacks"),
				new UnlockReward("Access to H.A.M. Store Rooms."),
				new UnlockReward("Access to Dorgesh-Kann."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(goTalkToMistag, goTalkToZanik), lightSource, hamSet2));
		allSteps.add(new PanelDetails("Exploring Lumbridge", Arrays.asList(goTalkToCook, goTalkToDuke, goOutsideSteps, goTalkToWoman, goTalkToGuide, goTalkToBob, goTalkToAereck, goNearGoblins,
			goTalkToShopkeeper, talkToZanikAboutOrigin), lightSource, hamSet2));
		allSteps.add(new PanelDetails("Infiltrate the H.A.M", Arrays.asList(goIntoHamLair, talkToJohanhus, listenToSpeaker, standNearTrapdoor, goDownTrapdoor), lightSource, hamSet));
		allSteps.add(new PanelDetails("Reaching the meeting", Arrays.asList(standBehindGuard1, talkToGuard2, tellZanikToKillGuard3, standNearGuard4,
			tellZanikToWaitForGuard4, runSouthToLureGuard4, standNearGuard5, tellZanikToWaitForGuard5, lureGuard5, listenToDoor)));
		allSteps.add(new PanelDetails("Saving Zanik", Arrays.asList(checkZanikCorpse, goToJunaSteps, learnZanikStory), lightSource, tinderbox, pickaxe));
		allSteps.add(new PanelDetails("Foiling H.A.M", Arrays.asList(goGetZanikForMill, searchCrate, enterMill, killGuards, killSigmund, smashDrill, enterExit), hamSet, combatGear));
		return allSteps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.THE_LOST_TRIBE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.AGILITY, 23, true));
		req.add(new SkillRequirement(Skill.THIEVING, 23));
		return req;
	}
}
