/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
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
package com.questhelper.helpers.quests.deathontheisle;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.item.TeleportItemRequirement;
import com.questhelper.requirements.player.FreeInventorySlotRequirement;
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
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;

/**
 * The quest guide for the "Death on the Isle" OSRS quest
 * <p>
 * <a href="https://www.youtube.com/watch?v=3gnq9so1r80">Slayermusiq1's video guide</a> was referenced for this guide
 * <a href="https://oldschool.runescape.wiki/w/Death_on_the_Isle">The OSRS wiki guide</a> was referenced for this guide
 */
public class DeathOnTheIsle extends BasicQuestHelper
{
	// TODO: Fix npc ID, I can't find it in runelite atm
	final static private int CUSTOMER_NPC_ID = 13837;

	/// Recommended items
	private FreeInventorySlotRequirement emptyInvSlots;

	/// Steps
	private NpcStep talkToPatziToStartQuest;
	private ObjectStep enterUniformHouse;
	private ObjectStep stealUniformFromWardrobe;
	private ObjectStep leaveUniformHouse;
	private NpcStep talkToPatziAfterStealingUniform;
	private ConditionalStep stealButlerUniform;
	private NpcStep continueTalkingToPatzi;
	private NpcStep equipButlersOutfitAndHeadInside;
	private ConditionalStep introduceYourself;
	private ConditionalStep enterTheCellarStep;
	private ConditionalStep getWineStep;
	private ConditionalStep investigateManStep;
	private ConditionalStep beInterrogatedByThePoliceStep;
	private ConditionalStep investigateMurder;
	private ConditionalStep investigateMurder2;
	private ConditionalStep talkToGuardsAgainToTellThemYouAreReadyStep;
	private ConditionalStep speakToSuspects;
	private ConditionalStep getAdalasConfessionStep;
	private ConditionalStep talkToGuardsAboutAdalaStep;
	private ConditionalStep getToTheGuardsAtTheatreStep;
	private ConditionalStep investigateTheatreStep;
	private ConditionalStep investigateTheatreCellar;
	private ConditionalStep snitchToGuardsStep;
	private ConditionalStep confrontNaiatliStep;
	private NpcStep talkToGuardsToFinishTheQuest;
	private NpcStep headInsideAndTalkToPatzi;
	private NpcStep introduceYourselfToConstantinius;
	private NpcStep introduceYourselfToCozyac;
	private NpcStep introduceYourselfToPavo;
	private NpcStep introduceYourselfToXocotla;
	private NpcStep returnToPatzi;
	private ObjectStep getWine;
	private NpcStep investigateMan;
	private NpcStep beInterrogatedByThePolice;
	private ObjectStep enterTheCellarAgain;
	private ObjectStep investigateJug;
	private ObjectStep investigateSmallBoxInSouthRoom;
	private ObjectStep investigateBrokenStoolInSouthRoom;
	private ObjectStep investigateWineStorageInEastRoom;
	private ObjectStep investigateBrokenPotteryInEastRoom;
	private NpcStep investigateLiviusInEastRoom;
	private ObjectStep leaveCellar;
	private NpcStep investigateConstantinius;
	private NpcStep investigateCozyac;
	private NpcStep investigatePavo;
	private NpcStep investigateXocotla;
	private NpcStep interrogatePatziAndAdala;
	private NpcStep returnToTheGuards;
	private NpcStep pickpocketAdala;
	private NpcStep pickpocketCozyac;
	private NpcStep pickpocketPavo;
	private NpcStep pickpocketXocotla;
	private DetailedQuestStep inspectWineLabels;
	private DetailedQuestStep inspectThreateningNote;
	private DetailedQuestStep inspectDrinkingFlask;
	private DetailedQuestStep inspectShippingContract;
	private NpcStep returnStolenItemsToTheGuards;
	private NpcStep talkToGuardsAgainToTellThemYouAreReady;
	private NpcStep interrogateConstantiniusAgain;
	private NpcStep interrogateXocotlaAgain;
	private NpcStep interrogateCozyacAgain;
	private NpcStep interrogatePavoAgain;
	private NpcStep accuseAdala;
	private NpcStep getAdalasConfession;
	private NpcStep talkToGuardsAboutAdala;
	private NpcStep talkToGuardsAtTheatre;
	private NpcStep talkToCostumer;
	private ObjectStep searchCrateNextToStairs;
	private ObjectStep searchBookshelf;
	private ObjectStep searchCostumeRack;
	private NpcStep talkToCostumerAgain;
	private NpcStep speakToGuards;
	private NpcStep talkToStradiusToEnterTheTheatre;
	private NpcStep confrontNaiatli;
	private NpcStep talkToNaiatli;

	/// Zones
	private Zone butlerCostumeHouse1;
	private Zone butlerCostumeHouse2;
	private Zone butlerCostumeHouse3;
	private Zone butlerCostumeHouse4;
	private Zone villaOutsideTheatre1;
	private Zone villaOutsideTheatre2;
	private Zone villaPlatueOnTheWayToTheatre;
	private Zone outsideTheatreZone1;
	private Zone outsideTheatreZone2;
	private Zone villaCellar;
	private Zone villaTopFloor;
	private Zone villaMiddleFloor;
	private Zone theatreCellar;

	/// Requirements
	private ZoneRequirement inButlerCostumeHouse;
	private ZoneRequirement aroundVilla;
	private ZoneRequirement outsideVillaByLooseRocks;
	private ZoneRequirement outsideTheatre;

	private TeleportItemRequirement startTeleport;
	private ItemRequirements uniform;
	private ItemRequirement uniformEquipped;
	private VarbitRequirement inVilla;
	private ConditionalStep headInsideAndTalkToPatziStep;
	private VarbitRequirement introducedYourselfToConstantinius;
	private VarbitRequirement introducedYourselfToCozyac;
	private VarbitRequirement introducedYourselfToPavo;
	private VarbitRequirement introducedYourselfToXocotla;
	private VarbitRequirement investigatedJug;
	private VarbitRequirement investigatedSmallBoxInSouthRoom;
	private VarbitRequirement investigatedBrokenStoolInSouthRoom;
	private VarbitRequirement investigatedWineStorageInEastRoom;
	private VarbitRequirement investigatedBrokenPotteryInEastRoom;
	private VarbitRequirement investigatedLiviusInEastRoom;
	private VarbitRequirement investigatedConstantinius;
	private VarbitRequirement investigatedCozyac;
	private VarbitRequirement investigatedPavo;
	private VarbitRequirement investigatedXocotla;
	private VarbitRequirement interrogatedPatziAndAdala;
	private ZoneRequirement inVillaCellar;
	private ZoneRequirement inVillaTopFloor;
	private ZoneRequirement inVillaMiddleFloor;
	private ZoneRequirement inTheatreCellar;
	private ItemRequirement wineLabels;
	private ItemRequirement threateningNote;
	private ItemRequirement drinkingFlask;
	private ItemRequirement shippingContract;
	private VarbitRequirement inspectedWineLabels;
	private VarbitRequirement inspectedThreateningNote;
	private VarbitRequirement inspectedDrinkingFlask;
	private VarbitRequirement inspectedShippingContract;
	private VarbitRequirement interrogatedConstantiniusAgain;
	private VarbitRequirement interrogatedXocotlaAgain;
	private VarbitRequirement interrogatedCozyacAgain;
	private VarbitRequirement interrogatedPavoAgain;
	private VarbitRequirement talkedtoGuardsAtTheatre;
	private VarbitRequirement searchedCrateNextToStairs;
	private VarbitRequirement searchedBookshelf;
	private VarbitRequirement searchedCostumeRack;
	private VarbitRequirement trapSprung;
	private VarbitRequirement trapFailed;
	private VarbitRequirement naiatliDowned;
	private VarbitRequirement handedOverCluesToGuards;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToPatziToStartQuest);
		steps.put(2, talkToPatziToStartQuest);
		steps.put(4, stealButlerUniform);
		steps.put(6, stealButlerUniform);
		steps.put(8, continueTalkingToPatzi);
		steps.put(10, equipButlersOutfitAndHeadInside);
		steps.put(12, equipButlersOutfitAndHeadInside);
		steps.put(14, headInsideAndTalkToPatziStep);
		steps.put(15, introduceYourself);
		steps.put(16, enterTheCellarStep);
		steps.put(18, getWineStep);
		steps.put(19, investigateManStep);
		steps.put(20, beInterrogatedByThePoliceStep);
		steps.put(21, beInterrogatedByThePoliceStep);
		steps.put(22, investigateMurder);
		steps.put(24, investigateMurder);
		steps.put(26, investigateMurder2);
		steps.put(27, talkToGuardsAgainToTellThemYouAreReadyStep);
		steps.put(28, speakToSuspects);
		steps.put(30, speakToSuspects);
		steps.put(32, getAdalasConfessionStep);
		steps.put(33, talkToGuardsAboutAdalaStep);
		steps.put(34, getToTheGuardsAtTheatreStep);
		steps.put(36, investigateTheatreStep);
		steps.put(38, investigateTheatreCellar);
		steps.put(40, snitchToGuardsStep);
		steps.put(42, confrontNaiatliStep);
		steps.put(45, confrontNaiatliStep);
		steps.put(49, talkToGuardsToFinishTheQuest);

		return steps;
	}

	@Override
	protected void setupZones()
	{
		butlerCostumeHouse1 = new Zone(new WorldPoint(1399, 2967, 0), new WorldPoint(1402, 2974, 0));
		butlerCostumeHouse2 = new Zone(new WorldPoint(1397, 2970, 0), new WorldPoint(1399, 2977, 0));
		butlerCostumeHouse3 = new Zone(new WorldPoint(1396, 2974, 0), new WorldPoint(1392, 2977, 0));
		butlerCostumeHouse4 = new Zone(new WorldPoint(1392, 2973, 0), new WorldPoint(1394, 2973, 0));

		villaOutsideTheatre1 = new Zone(new WorldPoint(1427, 2945, 0), new WorldPoint(1454, 2899, 0));
		villaOutsideTheatre2 = new Zone(new WorldPoint(1454, 2921, 0), new WorldPoint(1468, 2899, 0));

		villaPlatueOnTheWayToTheatre = new Zone(new WorldPoint(1471, 2917, 0), new WorldPoint(1477, 2922, 0));

		outsideTheatreZone1 = new Zone(new WorldPoint(1472, 2924, 0), new WorldPoint(1481, 2926, 0));
		outsideTheatreZone2 = new Zone(new WorldPoint(1477, 2927, 0), new WorldPoint(1482, 2944, 0));

		villaCellar = new Zone(new WorldPoint(1432, 9312, 0), new WorldPoint(1461, 9342, 0));
		villaTopFloor = new Zone(new WorldPoint(1437, 2926, 2), new WorldPoint(1449, 2941, 2));
		villaMiddleFloor = new Zone(new WorldPoint(1437, 2926, 1), new WorldPoint(1449, 2941, 1));

		theatreCellar = new Zone(new WorldPoint(1461, 9338, 0), new WorldPoint(1469, 9325, 0));
	}

	@Override
	protected void setupRequirements()
	{
		/// Recommended items
		emptyInvSlots = new FreeInventorySlotRequirement(2);

		/// Start of quest recommendation
		startTeleport = new TeleportItemRequirement("Aldarin Teleport (or fairy ring CKQ)", ItemID.ALDARIN_TELEPORT);
		startTeleport.addAlternates(ItemCollections.FAIRY_STAFF);
		// TODO: Add alternates

		/// Mid-quest item requirements
		var uniformTop = new ItemRequirement("Butler's uniform shirt", ItemID.BUTLERS_UNIFORM_29916);
		var uniformBottom = new ItemRequirement("Butler's uniform pants", ItemID.BUTLERS_UNIFORM_29918);
		uniform = new ItemRequirements("Butler's uniform", uniformTop, uniformBottom);

		var uniformTopEquipped = uniformTop.equipped().highlighted();
		var uniformBottomEquipped = uniformBottom.equipped().highlighted();
		uniformEquipped = new ItemRequirements("Butler's uniform (equipped)", uniformTopEquipped, uniformBottomEquipped).highlighted();
		uniformEquipped.setTooltip("This can be obtained from the wardrobe north of Villa Lucens.");

		wineLabels = new ItemRequirement("Wine labels", ItemID.WINE_LABELS);
		threateningNote = new ItemRequirement("Threatening note", ItemID.THREATENING_NOTE);
		drinkingFlask = new ItemRequirement("Drinking flask", ItemID.DRINKING_FLASK);
		shippingContract = new ItemRequirement("Shipping contract", ItemID.SHIPPING_CONTRACT);

		handedOverCluesToGuards = new VarbitRequirement(11233, 1);

		/// Zones
		inButlerCostumeHouse = new ZoneRequirement(butlerCostumeHouse1, butlerCostumeHouse2, butlerCostumeHouse3, butlerCostumeHouse4);
		inVilla = new VarbitRequirement(14283, 5);
		aroundVilla = new ZoneRequirement(villaOutsideTheatre1, villaOutsideTheatre2);
		outsideVillaByLooseRocks = new ZoneRequirement(
			villaPlatueOnTheWayToTheatre
		);
		outsideTheatre = new ZoneRequirement(outsideTheatreZone1, outsideTheatreZone2);
		inVillaCellar = new ZoneRequirement(villaCellar);
		inVillaTopFloor = new ZoneRequirement(villaTopFloor);
		inVillaMiddleFloor = new ZoneRequirement(villaMiddleFloor);
		inTheatreCellar = new ZoneRequirement(theatreCellar);

		/// States
		introducedYourselfToConstantinius = new VarbitRequirement(11214, 1);
		introducedYourselfToCozyac = new VarbitRequirement(11212, 1);
		introducedYourselfToPavo = new VarbitRequirement(11213, 1);
		introducedYourselfToXocotla = new VarbitRequirement(11211, 1);

		investigatedJug = new VarbitRequirement(11218, 1);
		investigatedSmallBoxInSouthRoom = new VarbitRequirement(11221, 1);
		investigatedBrokenStoolInSouthRoom = new VarbitRequirement(11222, 1);
		investigatedWineStorageInEastRoom = new VarbitRequirement(11220, 1);
		investigatedBrokenPotteryInEastRoom = new VarbitRequirement(11219, 1);
		investigatedLiviusInEastRoom = new VarbitRequirement(11223, 1);

		investigatedConstantinius = new VarbitRequirement(11227, 1);
		investigatedCozyac = new VarbitRequirement(11225, 1);
		investigatedPavo = new VarbitRequirement(11226, 1);
		investigatedXocotla = new VarbitRequirement(11224, 1);
		interrogatedPatziAndAdala = new VarbitRequirement(11234, 1);

		// pickpocketedAdala = new VarbitRequirement(11228, 1);
		// pickpocketedCozyac = new VarbitRequirement(11216, 1);
		// pickpocketedPavo = new VarbitRequirement(11217, 1);
		// pickpocketedXocotla = new VarbitRequirement(11215, 1);

		inspectedWineLabels = new VarbitRequirement(11236, 1);
		inspectedThreateningNote = new VarbitRequirement(11237, 1);
		inspectedDrinkingFlask = new VarbitRequirement(11235, 1);
		inspectedShippingContract = new VarbitRequirement(11238, 1);

		interrogatedConstantiniusAgain = new VarbitRequirement(11244, 1);
		interrogatedXocotlaAgain = new VarbitRequirement(11245, 1);
		interrogatedCozyacAgain = new VarbitRequirement(11243, 1);
		interrogatedPavoAgain = new VarbitRequirement(11246, 1);

		talkedtoGuardsAtTheatre = new VarbitRequirement(11249, 1);

		searchedCrateNextToStairs = new VarbitRequirement(11251, 1);
		searchedBookshelf = new VarbitRequirement(11250, 1);
		searchedCostumeRack = new VarbitRequirement(11252, 1);

		trapSprung = new VarbitRequirement(11258, 2);
		trapFailed = new VarbitRequirement(11258, 3, Operation.GREATER_EQUAL);
		naiatliDowned = new VarbitRequirement(11258, 6, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		// TODO: puzzle step wrapper over everything? xd

		// This is a return step in case the user leaves mid-way through
		var returnToButlerAndHeadInside = new NpcStep(this, NpcID.HEAD_BUTLER, new WorldPoint(1426, 2919, 0), "Talk to the Head Butler outside the at the entrance to Villa Lucens on the island of Aldarin.", uniformEquipped);
		returnToButlerAndHeadInside.addDialogStep("Yes.");

		/// 0 + 2
		talkToPatziToStartQuest = new NpcStep(this, NpcID.PATZI, new WorldPoint(1414, 2937, 0), "Talk to Patzi at the entrance to Villa Lucens on the island of Aldarin to start the quest.");
		talkToPatziToStartQuest.addDialogStep("Yes.");
		talkToPatziToStartQuest.addTeleport(startTeleport);

		/// 4 + 6
		enterUniformHouse = new ObjectStep(this, ObjectID.HOUSE_WINDOW, new WorldPoint(1401, 2967, 0), "Enter the house north of Patzi through the window to steal a butler's uniform. If the Wandering Guard bothers you, wait until he steps away before entering.");
		stealUniformFromWardrobe = new ObjectStep(this, ObjectID.WARDROBE_54723, new WorldPoint(1399, 2969, 0), "Steal the butler's uniform from the wardrobe.");
		leaveUniformHouse = new ObjectStep(this, ObjectID.HOUSE_WINDOW, new WorldPoint(1401, 2967, 0), "Leave the house with the butler's uniform.", uniform);
		talkToPatziAfterStealingUniform = new NpcStep(this, NpcID.PATZI, new WorldPoint(1414, 2937, 0), "Return to Patzi after stealing the butler's uniform.", uniform);
		stealButlerUniform = new ConditionalStep(this, enterUniformHouse);
		stealButlerUniform.addStep(and(inButlerCostumeHouse, uniform), leaveUniformHouse);
		stealButlerUniform.addStep(uniform, talkToPatziAfterStealingUniform);
		stealButlerUniform.addStep(inButlerCostumeHouse, stealUniformFromWardrobe);

		/// 8
		continueTalkingToPatzi = new NpcStep(this, NpcID.PATZI, new WorldPoint(1414, 2937, 0), "Continue talking to Patzi", uniform);
		talkToPatziAfterStealingUniform.addSubSteps(continueTalkingToPatzi);

		/// 10 + 12
		equipButlersOutfitAndHeadInside = new NpcStep(this, NpcID.HEAD_BUTLER, new WorldPoint(1426, 2919, 0), "Equip the Butler's uniform pieces and talk to the Head Butler up the stone stairs and to the south.", uniformEquipped);
		equipButlersOutfitAndHeadInside.addDialogStep("I am.");
		equipButlersOutfitAndHeadInside.addSubSteps(returnToButlerAndHeadInside);

		/// 14
		headInsideAndTalkToPatzi = new NpcStep(this, NpcID.PATZI, new WorldPoint(1447, 2936, 0), "Head inside the villa and talk to Patzi.");
		headInsideAndTalkToPatziStep = new ConditionalStep(this, headInsideAndTalkToPatzi);
		headInsideAndTalkToPatziStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 15
		introduceYourselfToConstantinius = new NpcStep(this, NpcID.CONSTANTINIUS, new WorldPoint(1447, 2933, 0), "Introduce yourself to Constantinius.");
		introduceYourselfToCozyac = new NpcStep(this, NpcID.COZYAC, new WorldPoint(1447, 2933, 0), "Introduce yourself to Cozyac.");
		introduceYourselfToPavo = new NpcStep(this, NpcID.PAVO, new WorldPoint(1447, 2933, 0), "Introduce yourself to Pavo.");
		introduceYourselfToXocotla = new NpcStep(this, NpcID.XOCOTLA, new WorldPoint(1441, 2930, 0), "Introduce yourself to Xocotla.");
		returnToPatzi = new NpcStep(this, NpcID.PATZI, new WorldPoint(1447, 2936, 0), "Return to Patzi with the information you've gathered.");
		introduceYourself = new ConditionalStep(this, returnToPatzi);
		introduceYourself.addStep(not(inVilla), returnToButlerAndHeadInside);
		introduceYourself.addStep(not(introducedYourselfToConstantinius), introduceYourselfToConstantinius);
		introduceYourself.addStep(not(introducedYourselfToCozyac), introduceYourselfToCozyac);
		introduceYourself.addStep(not(introducedYourselfToPavo), introduceYourselfToPavo);
		introduceYourself.addStep(not(introducedYourselfToXocotla), introduceYourselfToXocotla);

		/// 16
		var enterTheCellar = new ObjectStep(this, ObjectID.CELLAR_ENTRANCE, new WorldPoint(1449, 2938, 0), "Enter the cellar.");
		enterTheCellarStep = new ConditionalStep(this, enterTheCellar);
		enterTheCellarStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 18
		getWine = new ObjectStep(this, NullObjectID.NULL_55379, new WorldPoint(1439, 9314, 0), "Investigate the Antique wine in the southern part of the cellar.");
		getWine.addSubSteps(enterTheCellar);
		getWineStep = new ConditionalStep(this, getWine);
		getWineStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 19
		investigateMan = new NpcStep(this, NpcID.MAN_13872, new WorldPoint(1454, 9322, 0), "Check on the man in the south-eastern part of the cellar.");
		investigateManStep = new ConditionalStep(this, investigateMan);
		investigateManStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 20 + 21
		beInterrogatedByThePolice = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1439, 2938, 0), "Explain your innocence to the guards Stradius and Hutza.");
		beInterrogatedByThePoliceStep = new ConditionalStep(this, beInterrogatedByThePolice);
		beInterrogatedByThePoliceStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 22 + 24
		enterTheCellarAgain = new ObjectStep(this, ObjectID.CELLAR_ENTRANCE, new WorldPoint(1449, 2938, 0), "Enter the cellar again to begin your investigation.");
		investigateJug = new ObjectStep(this, ObjectID.JUG, new WorldPoint(1445, 9336, 0), "Investigate the Jug by the entrance stairs.");
		investigateSmallBoxInSouthRoom = new ObjectStep(this, ObjectID.SMALL_BOX, new WorldPoint(1439, 9321, 0), "Investigate the Small box in the south room.");
		investigateBrokenStoolInSouthRoom = new ObjectStep(this, ObjectID.BROKEN_STOOL_54712, new WorldPoint(1446, 9315, 0), "Investigate the Broken stool in the south room.");
		investigateWineStorageInEastRoom = new ObjectStep(this, ObjectID.WINE_STORAGE, new WorldPoint(1451, 9335, 0), "Investigate the Wine storage in the east room.");
		investigateBrokenPotteryInEastRoom = new ObjectStep(this, ObjectID.BROKEN_POTTERY, new WorldPoint(1456, 9328, 0), "Investigate the Broken pottery in the east room.");
		investigateLiviusInEastRoom = new NpcStep(this, NpcID.LIVIUS, new WorldPoint(1454, 9322, 0), "Investigate Livius in the east room.");
		var investigatedCellarItems = and(investigatedJug, investigatedSmallBoxInSouthRoom, investigatedBrokenStoolInSouthRoom, investigatedWineStorageInEastRoom, investigatedBrokenPotteryInEastRoom, investigatedLiviusInEastRoom);
		leaveCellar = new ObjectStep(this, ObjectID.STAIRS_54716, new WorldPoint(1447, 9338, 0), "Leave the cellar.");

		investigateConstantinius = new NpcStep(this, NpcID.CONSTANTINIUS, new WorldPoint(1446, 2933, 0), "Interrogate Constantinius.");
		investigateCozyac = new NpcStep(this, NpcID.COZYAC, new WorldPoint(1446, 2933, 0), "Interrogate Cozyac.");
		investigatePavo = new NpcStep(this, NpcID.PAVO, new WorldPoint(1446, 2933, 0), "Interrogate Pavo.");
		investigateXocotla = new NpcStep(this, NpcID.XOCOTLA, new WorldPoint(1446, 2933, 0), "Interrogate Xocotla.");
		interrogatePatziAndAdala = new NpcStep(this, NpcID.PATZI, new WorldPoint(1447, 2936, 0), "Interrogate Patzi and Adala.");

		returnToTheGuards = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1443, 2935, 0), "Return to the guards with the information you've found so far.");
		returnToTheGuards.addDialogStep("I think I've found everything there is to find so far. What now?");

		investigateMurder = new ConditionalStep(this, returnToTheGuards);
		investigateMurder.addStep(not(inVilla), returnToButlerAndHeadInside);
		investigateMurder.addStep(and(investigatedCellarItems, not(inVillaCellar), not(investigatedConstantinius)), investigateConstantinius);
		investigateMurder.addStep(and(investigatedCellarItems, not(inVillaCellar), not(investigatedCozyac)), investigateCozyac);
		investigateMurder.addStep(and(investigatedCellarItems, not(inVillaCellar), not(investigatedPavo)), investigatePavo);
		investigateMurder.addStep(and(investigatedCellarItems, not(inVillaCellar), not(investigatedXocotla)), investigateXocotla);
		investigateMurder.addStep(and(investigatedCellarItems, not(inVillaCellar), not(interrogatedPatziAndAdala)), interrogatePatziAndAdala);
		investigateMurder.addStep(and(inVillaCellar, investigatedCellarItems), leaveCellar);
		investigateMurder.addStep(and(not(inVillaCellar), not(investigatedCellarItems)), enterTheCellarAgain);
		investigateMurder.addStep(not(investigatedJug), investigateJug);
		investigateMurder.addStep(not(investigatedSmallBoxInSouthRoom), investigateSmallBoxInSouthRoom);
		investigateMurder.addStep(not(investigatedBrokenStoolInSouthRoom), investigateBrokenStoolInSouthRoom);
		investigateMurder.addStep(not(investigatedWineStorageInEastRoom), investigateWineStorageInEastRoom);
		investigateMurder.addStep(not(investigatedBrokenPotteryInEastRoom), investigateBrokenPotteryInEastRoom);
		investigateMurder.addStep(not(investigatedLiviusInEastRoom), investigateLiviusInEastRoom);

		/// 26
		pickpocketAdala = new NpcStep(this, NpcID.ADALA_13823, new WorldPoint(1446, 2936, 0), "Pickpocket Adala until you find Wine labels.", wineLabels);
		pickpocketCozyac = new NpcStep(this, NpcID.COZYAC_13828, new WorldPoint(1445, 2934, 0), "Pickpocket Cozyac until you find a threatening note.", threateningNote);
		pickpocketPavo = new NpcStep(this, NpcID.PAVO_13832, new WorldPoint(1445, 2934, 0), "Pickpocket Pavo until you find a drinking flask.", drinkingFlask);
		pickpocketXocotla = new NpcStep(this, NpcID.XOCOTLA_13830, new WorldPoint(1445, 2934, 0), "Pickpocket Xocotla until you find a shipping contract.", shippingContract);

		inspectWineLabels = new DetailedQuestStep(this, "Inspect the Wine labels in your inventory.", wineLabels.highlighted());
		inspectThreateningNote = new DetailedQuestStep(this, "Inspect the Threatening note in your inventory.", threateningNote.highlighted());
		inspectDrinkingFlask = new DetailedQuestStep(this, "Inspect the Drinking flask in your inventory.", drinkingFlask.highlighted());
		inspectShippingContract = new DetailedQuestStep(this, "Inspect the Shipping contract in your inventory.", shippingContract.highlighted());

		returnStolenItemsToTheGuards = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1443, 2935, 0), "Return to the guards with the stolen items.", wineLabels, threateningNote, drinkingFlask, shippingContract);
		returnStolenItemsToTheGuards.addDialogStep("I think I've found everything there is to find so far. What now?");

		talkToGuardsAgainToTellThemYouAreReady = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1433, 2935, 0), "Talk to the guards to tell them you're ready to make an accusation.");
		talkToGuardsAgainToTellThemYouAreReady.addDialogStep("I'm ready.");

		investigateMurder2 = new ConditionalStep(this, returnStolenItemsToTheGuards);
		investigateMurder2.addStep(not(inVilla), returnToButlerAndHeadInside);
		investigateMurder2.addStep(handedOverCluesToGuards, talkToGuardsAgainToTellThemYouAreReady);
		investigateMurder2.addStep(not(wineLabels), pickpocketAdala);
		investigateMurder2.addStep(not(threateningNote), pickpocketCozyac);
		investigateMurder2.addStep(not(drinkingFlask), pickpocketPavo);
		investigateMurder2.addStep(not(shippingContract), pickpocketXocotla);
		investigateMurder2.addStep(not(inspectedWineLabels), inspectWineLabels);
		investigateMurder2.addStep(not(inspectedThreateningNote), inspectThreateningNote);
		investigateMurder2.addStep(not(inspectedDrinkingFlask), inspectDrinkingFlask);
		investigateMurder2.addStep(not(inspectedShippingContract), inspectShippingContract);

		/// 27
		talkToGuardsAgainToTellThemYouAreReadyStep = new ConditionalStep(this, talkToGuardsAgainToTellThemYouAreReady);
		talkToGuardsAgainToTellThemYouAreReadyStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 28 + 30
		interrogateConstantiniusAgain = new NpcStep(this, NpcID.CONSTANTINIUS, new WorldPoint(1446, 2931, 2), "Interrogate Constantinius.");
		interrogateConstantiniusAgain.addDialogStep("No.");
		interrogateXocotlaAgain = new NpcStep(this, NpcID.XOCOTLA, new WorldPoint(1444, 2928, 2), "Interrogate Xocotla.");
		interrogateXocotlaAgain.addDialogStep("No.");
		interrogateCozyacAgain = new NpcStep(this, NpcID.COZYAC, new WorldPoint(1442, 2929, 2), "Interrogate Cozyac.");
		interrogateCozyacAgain.addDialogStep("No.");
		interrogatePavoAgain = new NpcStep(this, NpcID.PAVO, new WorldPoint(1442, 2931, 2), "Interrogate Pavo.");
		interrogatePavoAgain.addDialogStep("No.");

		// 11248 0->1 accused Patzi
		// 11240 0->1 accused Constantinius
		// 11241 0->1 accused Xocotla
		// 11239 0->1 accused Cozyac
		// 11242 0->1 accused Pavo

		accuseAdala = new NpcStep(this, NpcID.ADALA, new WorldPoint(1446, 2933, 2), "Accuse Adala of the crime, ready for a fight you cannot lose.");
		accuseAdala.addDialogStep("Accuse Adala.");

		speakToSuspects = new ConditionalStep(this, accuseAdala);
		speakToSuspects.addStep(not(inVilla), returnToButlerAndHeadInside);
		speakToSuspects.addStep(not(interrogatedConstantiniusAgain), interrogateConstantiniusAgain);
		speakToSuspects.addStep(not(interrogatedXocotlaAgain), interrogateXocotlaAgain);
		speakToSuspects.addStep(not(interrogatedCozyacAgain), interrogateCozyacAgain);
		speakToSuspects.addStep(not(interrogatedPavoAgain), interrogatePavoAgain);

		/// 32
		getAdalasConfession = new NpcStep(this, NpcID.ADALA_13821, new WorldPoint(1446, 2933, 2), "Talk to Adala to get her confession.");
		getAdalasConfessionStep = new ConditionalStep(this, getAdalasConfession);
		getAdalasConfessionStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 33
		talkToGuardsAboutAdala = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1442, 2933, 2), "Talk to the guards about Adala.");
		talkToGuardsAboutAdalaStep = new ConditionalStep(this, talkToGuardsAboutAdala);
		talkToGuardsAboutAdalaStep.addStep(not(inVilla), returnToButlerAndHeadInside);

		/// 34
		var headDownFromTopFloor = new ObjectStep(this, ObjectID.STAIRCASE_54714, new WorldPoint(1445, 2939, 2), "Climb down the staircase.");
		var headDownFromMiddleFloor = new ObjectStep(this, ObjectID.STAIRCASE_54714, new WorldPoint(1442, 2936, 1), "Climb down the staircase.");
		var climbFirstLooseRocksToTheatre = new ObjectStep(this, ObjectID.LOOSE_ROCKS_54720, new WorldPoint(1469, 2918, 0), "Climb the Loose rocks south-east of the villa on your way to the theatre.");

		getToTheGuardsAtTheatreStep = new ConditionalStep(this, climbFirstLooseRocksToTheatre);
		getToTheGuardsAtTheatreStep.addStep(not(inVilla), returnToButlerAndHeadInside);
		getToTheGuardsAtTheatreStep.addStep(inVillaTopFloor, headDownFromTopFloor);
		getToTheGuardsAtTheatreStep.addStep(inVillaMiddleFloor, headDownFromMiddleFloor);

		/// 36
		var climbSecondLooseRocksToTheatre = new ObjectStep(this, ObjectID.LOOSE_ROCKS_54722, new WorldPoint(1474, 2923, 0), "Climb down the loose rocks in your way to the guards outside the theatre.");

		talkToGuardsAtTheatre = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1472, 2925, 0), "Talk to Stradius at the theatre backstage of the theatre.");
		talkToGuardsAtTheatre.addSubSteps(headDownFromTopFloor, headDownFromMiddleFloor, climbFirstLooseRocksToTheatre, climbSecondLooseRocksToTheatre);
		talkToGuardsAtTheatre.addSubSteps(headDownFromTopFloor);

		var enterBackstage = new ObjectStep(this, ObjectID.BACKSTAGE_ENTRANCE, new WorldPoint(1477, 2927, 0), "Enter the theatre through the backstage entrance.");
		talkToCostumer = new NpcStep(this, CUSTOMER_NPC_ID, new WorldPoint(1466, 9330, 0), "Talk to the Costumer in the theatre cellar.");
		talkToCostumer.addSubSteps(enterBackstage);

		var investigateTheatre = new ConditionalStep(this, talkToCostumer);
		investigateTheatre.addStep(not(inVilla), returnToButlerAndHeadInside);
		investigateTheatre.addStep(aroundVilla, climbFirstLooseRocksToTheatre);
		investigateTheatre.addStep(outsideVillaByLooseRocks, climbSecondLooseRocksToTheatre);
		investigateTheatre.addStep(not(talkedtoGuardsAtTheatre), talkToGuardsAtTheatre);
		investigateTheatre.addStep(not(inTheatreCellar), enterBackstage);

		investigateTheatreStep = new ConditionalStep(this, investigateTheatre);

		/// 38
		searchCrateNextToStairs = new ObjectStep(this, ObjectID.CRATE_54731, new WorldPoint(1469, 9330, 0), "Search the crate next to the stairs.");
		searchBookshelf = new ObjectStep(this, ObjectID.BOOKSHELF_54726, new WorldPoint(1461, 9331, 0), "Search the bookshelf on the west wall.");
		searchCostumeRack = new ObjectStep(this, ObjectID.COSTUME_RACK, new WorldPoint(1464, 9337, 0), "Search the costume rack to the north.");
		talkToCostumerAgain = new NpcStep(this, CUSTOMER_NPC_ID, new WorldPoint(1466, 9330, 0), "Talk to the Costumer about what you found.");

		var talkToCostumerAboutActors = talkToCostumerAgain.copy();
		talkToCostumerAboutActors.addDialogStep("What can you tell me about the actors?");
		talkToCostumerAgain.addSubSteps(talkToCostumerAboutActors);
		var talkedAboutActors = new VarbitRequirement(11253, 1);

		var talkToCostumerAboutPoison = talkToCostumerAgain.copy();
		talkToCostumerAboutPoison.addDialogStep("What's the crate of poison for?");
		talkToCostumerAgain.addSubSteps(talkToCostumerAboutPoison);
		var talkedAboutPoison = new VarbitRequirement(11255, 1);

		var talkToCostumerAboutStainedCostume = talkToCostumerAgain.copy();
		talkToCostumerAboutStainedCostume.addDialogStep("It seems one of the costumes has a stain on it.");
		talkToCostumerAgain.addSubSteps(talkToCostumerAboutStainedCostume);
		var talkedAboutStainedCostume = new VarbitRequirement(11256, 1);

		var talkToCostumerAboutHiddenPassage = talkToCostumerAgain.copy();
		talkToCostumerAboutHiddenPassage.addDialogStep("Did you know there was a hidden passage in here?");
		talkToCostumerAgain.addSubSteps(talkToCostumerAboutHiddenPassage);
		// var talkedAboutHiddenPassage = new VarbitRequirement(11254, 1);

		investigateTheatreCellar = new ConditionalStep(this, talkToCostumerAboutHiddenPassage);
		investigateTheatreCellar.addStep(not(inVilla), returnToButlerAndHeadInside);
		investigateTheatreCellar.addStep(aroundVilla, climbFirstLooseRocksToTheatre);
		investigateTheatreCellar.addStep(outsideVillaByLooseRocks, climbSecondLooseRocksToTheatre);
		investigateTheatreCellar.addStep(not(inTheatreCellar), enterBackstage);
		investigateTheatreCellar.addStep(not(searchedCrateNextToStairs), searchCrateNextToStairs);
		investigateTheatreCellar.addStep(not(searchedBookshelf), searchBookshelf);
		investigateTheatreCellar.addStep(not(searchedCostumeRack), searchCostumeRack);
		investigateTheatreCellar.addStep(not(talkedAboutActors), talkToCostumerAboutActors);
		investigateTheatreCellar.addStep(not(talkedAboutPoison), talkToCostumerAboutPoison);
		investigateTheatreCellar.addStep(not(talkedAboutStainedCostume), talkToCostumerAboutStainedCostume);

		/// 40
		var climbUpFromTheatreCellar = new ObjectStep(this, ObjectID.STAIRS_54717, new WorldPoint(1469, 9328, 0), "Climb up the stairs of the theatre cellar.");

		speakToGuards = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1472, 2925, 0), "Report back to Stradius near the theatre and accuse Naiatli.");
		speakToGuards.addDialogStepWithExclusion("More options...", "Naiatli.");
		speakToGuards.addDialogStep("Naiatli.");
		speakToGuards.addSubSteps(climbUpFromTheatreCellar);

		// varbit 11257=1 if spoken to stradius a bit

		snitchToGuardsStep = new ConditionalStep(this, speakToGuards);
		snitchToGuardsStep.addStep(not(inVilla), returnToButlerAndHeadInside);
		snitchToGuardsStep.addStep(aroundVilla, climbFirstLooseRocksToTheatre);
		snitchToGuardsStep.addStep(outsideVillaByLooseRocks, climbSecondLooseRocksToTheatre);
		snitchToGuardsStep.addStep(inTheatreCellar, climbUpFromTheatreCellar);

		// 42 + 45
		talkToStradiusToEnterTheTheatre = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1472, 2925, 0), "Talk to Stradius to enter the theatre.");
		confrontNaiatli = new NpcStep(this, NpcID.NAIATLI, new WorldPoint(1465, 2932, 0), "Confront Naiatli, attacking her when she runs away. When Clodius appears, attack him instead.");
		var attackClodius = new NpcStep(this, NpcID.CLODIUS, new WorldPoint(1469, 2937, 0), "Attack Clodius.");
		var killNaiatli = new NpcStep(this, NpcID.NAIATLI, new WorldPoint(1469, 2937, 0), "Kill Naiatli.");

		talkToNaiatli = new NpcStep(this, NpcID.NAIATLI, new WorldPoint(1472, 2931, 0), "Talk to Naiatli.");

		confrontNaiatli.addSubSteps(attackClodius, killNaiatli);

		confrontNaiatliStep = new ConditionalStep(this, confrontNaiatli);
		confrontNaiatliStep.addStep(not(inVilla), returnToButlerAndHeadInside);
		confrontNaiatliStep.addStep(aroundVilla, climbFirstLooseRocksToTheatre);
		confrontNaiatliStep.addStep(outsideVillaByLooseRocks, climbSecondLooseRocksToTheatre);
		confrontNaiatliStep.addStep(inTheatreCellar, climbUpFromTheatreCellar);
		confrontNaiatliStep.addStep(outsideTheatre, talkToStradiusToEnterTheTheatre);
		confrontNaiatliStep.addStep(naiatliDowned, talkToNaiatli);
		confrontNaiatliStep.addStep(trapFailed, killNaiatli);
		confrontNaiatliStep.addStep(trapSprung, attackClodius);

		/// 49
		talkToGuardsToFinishTheQuest = new NpcStep(this, NpcID.STRADIUS, new WorldPoint(1443, 2932, 0), "Talk to Stradius and Hutza to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of();
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of();
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			emptyInvSlots
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.CHILDREN_OF_THE_SUN, QuestState.FINISHED),
			new SkillRequirement(Skill.THIEVING, 34),
			new SkillRequirement(Skill.AGILITY, 32)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"2 unlosable fights"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.THIEVING, 10000),
			new ExperienceReward(Skill.AGILITY, 7500),
			new ExperienceReward(Skill.CRAFTING, 5000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		// You *can* get an assortment of masks, or a custome needle, but they are not direct rewards.
		return List.of();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("The ability to use a Costume Needle (obtainable by speaking to the Costumer)"),
			new UnlockReward("The ability to unlock the North Aldarin teleport destination via the pendant of ates")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Getting the right fit", List.of(talkToPatziToStartQuest, enterUniformHouse, stealUniformFromWardrobe, leaveUniformHouse, talkToPatziAfterStealingUniform, equipButlersOutfitAndHeadInside), List.of(emptyInvSlots)));
		panels.add(new PanelDetails("Inside Villa Lucens", List.of(headInsideAndTalkToPatzi, introduceYourselfToConstantinius, introduceYourselfToCozyac, introduceYourselfToPavo, introduceYourselfToXocotla, returnToPatzi, getWine, investigateMan, beInterrogatedByThePolice), List.of()));
		panels.add(new PanelDetails("Playing detective", List.of(enterTheCellarAgain, investigateJug, investigateSmallBoxInSouthRoom, investigateBrokenStoolInSouthRoom, investigateWineStorageInEastRoom, investigateBrokenPotteryInEastRoom, investigateLiviusInEastRoom, leaveCellar, investigateConstantinius, investigateCozyac, investigatePavo, investigateXocotla, interrogatePatziAndAdala, returnToTheGuards, pickpocketAdala, pickpocketCozyac, pickpocketPavo, pickpocketXocotla, inspectWineLabels, inspectThreateningNote, inspectDrinkingFlask, inspectShippingContract, returnStolenItemsToTheGuards, talkToGuardsAgainToTellThemYouAreReady, interrogateConstantiniusAgain, interrogateXocotlaAgain, interrogateCozyacAgain, interrogatePavoAgain, accuseAdala, getAdalasConfession, talkToGuardsAboutAdala), List.of()));
		panels.add(new PanelDetails("The show must go on", List.of(talkToGuardsAtTheatre, talkToCostumer, searchCrateNextToStairs, searchBookshelf, searchCostumeRack, talkToCostumerAgain, speakToGuards, talkToStradiusToEnterTheTheatre, confrontNaiatli, talkToNaiatli, talkToGuardsToFinishTheQuest), List.of()));

		return panels;
	}
}
