/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.morytania;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.player.PrayerRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.MORYTANIA_MEDIUM
)
public class MorytaniaMedium extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, rope, smallFishingNet, axe, ectoToken, ghostspeakAmulet, steelBar, ammoMould,
		slayerGloves, ectophial, restorePot, garlic, silverDust;

	// Items recommended
	ItemRequirement food, slayerRing, fairyAccess;

	// Quests required
	Requirement lairOfTarnRaz, cabinFever, natureSpirit, dwarfCannon, rumDeal, ghostsAhoy,
		inAidOfMyreque, switchPressed, protectFromMagic;

	Requirement notSwampLizard, notCanifisAgi, notHollowTree, notDragontoothIsland, notTerrorDog, notTroubleBrewing,
		notSwampBoaty, notCannonBall, notFeverSpider, notEctophialTP, notGuthBalance;

	QuestStep claimReward, swampLizard, canifisAgi, hollowTree, dragontoothIsland, troubleBrewing,
		swampBoaty, cannonBall, feverSpider, ectophialTP, guthBalance, enterHauntedMine, enterLair, searchWall2Room1,
		goThroughRoom1, goThroughRoom3, goThroughRoom4, goThroughRoom5, jumpToPillar1, jumpToPillar2, jumpToPillar3,
		jumpToPillar4, jumpToPillar5, jumpToPillar6, jumpToSwitch, pressSwitch, jumpBackToPillar4, jumpBackToPillar3,
		jumpToNorthLedge, searchWallRoom6, searchWall2Room6, goThroughRoom6, leaveExtraRoom1, leaveExtraRoom2,
		goThroughRoom7, enterBossRoom, moveToMine, moveToBrainDeath, moveToDownstairs, moveToCapt, moveToMos;

	NpcStep terrorDog;

	DetailedQuestStep searchWallRoom1, goThroughRoom2;

	Zone hauntedMineZone, room1, room1PastTrap1, room1PastTrap2, room2, room3, room4, room5P1, room5P2, room6P1,
		room6P2, room6P3, pillar1, pillar2, pillar3, pillar4, switch1, pillar5, pillar6, room6PastTrap1,
		room6PastTrap2P1, room6PastTrap2P2, extraRoom1, extraRoom2, room7, room8, bossRoom, brainDeath1, brainDeath2,
		boat, mos;

	ZoneRequirement inHauntedMineZone, inRoom1, inRoom1PastTrap1, inRoom1PastTrap2, inRoom2, inRoom3, inRoom4, inRoom5,
		inRoom6P1, inRoom6P2, onPillar1, onPillar2, onPillar3, onPillar4, onPillar5, onPillar6, atSwitch1,
		inRoom6PastTrap1, inRoom6PastTrap2, inExtraRoom1, inExtraRoom2, inRoom7, inRoom8, inBossRoom, inBrainDeath1,
		inBrainDeath2, inBoat, inMos;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doMedium = new ConditionalStep(this, claimReward);
		doMedium.addStep(notSwampBoaty, swampBoaty);
		doMedium.addStep(new Conditions(notTerrorDog, inBossRoom), terrorDog);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom8), enterBossRoom);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom7), goThroughRoom7);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom6PastTrap2), goThroughRoom6);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom6PastTrap1), searchWall2Room6);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom6P2), searchWallRoom6);
		doMedium.addStep(new Conditions(notTerrorDog, onPillar6), jumpToNorthLedge);
		doMedium.addStep(new Conditions(notTerrorDog, onPillar5), jumpToPillar6);
		doMedium.addStep(new Conditions(onPillar3, switchPressed), jumpToPillar5);
		doMedium.addStep(new Conditions(onPillar4, switchPressed), jumpBackToPillar3);
		doMedium.addStep(new Conditions(atSwitch1, switchPressed), jumpBackToPillar4);
		doMedium.addStep(new Conditions(notTerrorDog, atSwitch1), pressSwitch);
		doMedium.addStep(new Conditions(notTerrorDog, onPillar4), jumpToSwitch);
		doMedium.addStep(new Conditions(notTerrorDog, onPillar3), jumpToPillar4);
		doMedium.addStep(new Conditions(notTerrorDog, onPillar2), jumpToPillar3);
		doMedium.addStep(new Conditions(notTerrorDog, onPillar1), jumpToPillar2);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom6P1), jumpToPillar1);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom5), goThroughRoom5);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom4), goThroughRoom4);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom3), goThroughRoom3);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom2), goThroughRoom2);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom1PastTrap2), goThroughRoom1);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom1PastTrap1), searchWall2Room1);
		doMedium.addStep(new Conditions(notTerrorDog, inRoom1), searchWallRoom1);
		doMedium.addStep(new Conditions(notTerrorDog, inHauntedMineZone), enterLair);
		doMedium.addStep(notTerrorDog, moveToMine);
		doMedium.addStep(notCanifisAgi, canifisAgi);
		doMedium.addStep(notSwampLizard, swampLizard);
		doMedium.addStep(notEctophialTP, ectophialTP);
		doMedium.addStep(notCannonBall, cannonBall);
		doMedium.addStep(notGuthBalance, guthBalance);
		doMedium.addStep(notHollowTree, hollowTree);
		doMedium.addStep(notDragontoothIsland, dragontoothIsland);
		doMedium.addStep(new Conditions(notFeverSpider, inBrainDeath1), feverSpider);
		doMedium.addStep(new Conditions(notFeverSpider, inBrainDeath2), moveToDownstairs);
		doMedium.addStep(notFeverSpider, moveToBrainDeath);
		doMedium.addStep(new Conditions(notTroubleBrewing, inMos), troubleBrewing);
		doMedium.addStep(new Conditions(notTroubleBrewing, inBoat), moveToMos);
		doMedium.addStep(notTroubleBrewing, moveToCapt);

		return doMedium;
	}

	public void setupRequirements()
	{
		notSwampLizard = new VarplayerRequirement(1180, false, 12);
		notCanifisAgi = new VarplayerRequirement(1180, false, 13);
		notHollowTree = new VarplayerRequirement(1180, false, 14);
		notDragontoothIsland = new VarplayerRequirement(1180, false, 15);
		notTerrorDog = new VarplayerRequirement(1180, false, 16);
		notTroubleBrewing = new VarplayerRequirement(1180, false, 17);
		notSwampBoaty = new VarplayerRequirement(1180, false, 18);
		notCannonBall = new VarplayerRequirement(1180, false, 19);
		notFeverSpider = new VarplayerRequirement(1180, false, 20);
		notEctophialTP = new VarplayerRequirement(1180, false, 21);
		notGuthBalance = new VarplayerRequirement(1180, false, 22);

		protectFromMagic = new PrayerRequirement("Activate Protect from Magic", Prayer.PROTECT_FROM_MAGIC);

		rope = new ItemRequirement("Rope", ItemID.ROPE).showConditioned(notSwampLizard);
		smallFishingNet = new ItemRequirement("Small fishing net", ItemID.SMALL_FISHING_NET)
			.showConditioned(notSwampLizard);
		axe = new ItemRequirement("Axe", ItemCollections.getAxes()).showConditioned(notHollowTree);
		ectoToken = new ItemRequirement("Ecto-token", ItemID.ECTOTOKEN).showConditioned(notDragontoothIsland);
		ghostspeakAmulet = new ItemRequirement("Ghostspeak Amulet", ItemID.GHOSTSPEAK_AMULET)
			.showConditioned(notDragontoothIsland);
		steelBar = new ItemRequirement("Steel bar", ItemID.STEEL_BAR).showConditioned(notCannonBall);
		ammoMould = new ItemRequirement("Ammo mould", ItemID.AMMO_MOULD).showConditioned(notCannonBall);
		slayerGloves = new ItemRequirement("Slayer gloves", ItemID.SLAYER_GLOVES).showConditioned(notFeverSpider);
		ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL).showConditioned(notEctophialTP);
		restorePot = new ItemRequirement("Restore potion (4)", ItemID.RESTORE_POTION4).showConditioned(notGuthBalance);
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC).showConditioned(notGuthBalance);
		silverDust = new ItemRequirement("Silver dust", ItemID.SILVER_DUST).showConditioned(notGuthBalance);
		silverDust.setTooltip("Created by grinding a silver bar in the ectofuntus bone grinder.");

		slayerRing = new ItemRequirement("Slayer ring", ItemCollections.getSlayerRings()).showConditioned(notTerrorDog);
		fairyAccess = new ItemRequirement("Access to the fairy ring system", ItemCollections.getFairyStaff())
			.showConditioned(new Conditions(LogicType.OR, notSwampBoaty, notHollowTree));

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inHauntedMineZone = new ZoneRequirement(hauntedMineZone);
		inRoom1 = new ZoneRequirement(room1);
		inRoom1PastTrap1 = new ZoneRequirement(room1PastTrap1);
		inRoom1PastTrap2 = new ZoneRequirement(room1PastTrap2);
		inRoom2 = new ZoneRequirement(room2);
		inRoom3 = new ZoneRequirement(room3);
		inRoom4 = new ZoneRequirement(room4);
		inRoom5 = new ZoneRequirement(room5P1, room5P2);
		inRoom6P1 = new ZoneRequirement(room6P1);
		inRoom6P2 = new ZoneRequirement(room6P2, room6P3);
		inRoom7 = new ZoneRequirement(room7);
		onPillar1 = new ZoneRequirement(pillar1);
		onPillar2 = new ZoneRequirement(pillar2);
		onPillar3 = new ZoneRequirement(pillar3);
		onPillar4 = new ZoneRequirement(pillar4);
		onPillar5 = new ZoneRequirement(pillar5);
		onPillar6 = new ZoneRequirement(pillar6);
		atSwitch1 = new ZoneRequirement(switch1);
		switchPressed = new ObjectCondition(ObjectID.FLOOR_20635, new WorldPoint(3138, 4595, 1));
		inRoom6PastTrap1 = new ZoneRequirement(room6PastTrap1);
		inRoom6PastTrap2 = new ZoneRequirement(room6PastTrap2P1, room6PastTrap2P2);
		inExtraRoom1 = new ZoneRequirement(extraRoom1);
		inExtraRoom2 = new ZoneRequirement(extraRoom2);
		inRoom8 = new ZoneRequirement(room8);
		inBossRoom = new ZoneRequirement(bossRoom);
		inBrainDeath1 = new ZoneRequirement(brainDeath1);
		inBrainDeath2 = new ZoneRequirement(brainDeath2);
		inBoat = new ZoneRequirement(boat);
		inMos = new ZoneRequirement(mos);


		lairOfTarnRaz = new QuestRequirement(QuestHelperQuest.LAIR_OF_TARN_RAZORLOR, QuestState.FINISHED);
		cabinFever = new QuestRequirement(QuestHelperQuest.CABIN_FEVER, QuestState.FINISHED);
		natureSpirit = new QuestRequirement(QuestHelperQuest.NATURE_SPIRIT, QuestState.FINISHED);
		dwarfCannon = new QuestRequirement(QuestHelperQuest.DWARF_CANNON, QuestState.FINISHED);
		rumDeal = new QuestRequirement(QuestHelperQuest.RUM_DEAL, QuestState.IN_PROGRESS);
		ghostsAhoy = new QuestRequirement(QuestHelperQuest.GHOSTS_AHOY, QuestState.FINISHED);
		inAidOfMyreque = new QuestRequirement(QuestHelperQuest.IN_AID_OF_THE_MYREQUE, QuestState.IN_PROGRESS,
			"Defeated Gadderanks in In Aid of The Myreque");

	}

	public void loadZones()
	{
		hauntedMineZone = new Zone(new WorldPoint(3390, 9600, 0), new WorldPoint(3452, 9668, 0));
		room1 = new Zone(new WorldPoint(3136, 4544, 0), new WorldPoint(3199, 4570, 0));
		room1PastTrap1 = new Zone(new WorldPoint(3196, 4558, 0), new WorldPoint(3196, 4562, 0));
		room1PastTrap2 = new Zone(new WorldPoint(3193, 4563, 0), new WorldPoint(3196, 4570, 0));
		room2 = new Zone(new WorldPoint(3172, 4574, 1), new WorldPoint(3197, 4589, 1));
		room3 = new Zone(new WorldPoint(3166, 4575, 0), new WorldPoint(3170, 4579, 0));
		room4 = new Zone(new WorldPoint(3166, 4586, 0), new WorldPoint(3170, 4592, 0));
		room5P1 = new Zone(new WorldPoint(3143, 4589, 1), new WorldPoint(3162, 4590, 1));
		room5P2 = new Zone(new WorldPoint(3154, 4590, 1), new WorldPoint(3157, 4598, 1));
		room6P1 = new Zone(new WorldPoint(3136, 4592, 1), new WorldPoint(3151, 4600, 1));
		room6P2 = new Zone(new WorldPoint(3141, 4601, 1), new WorldPoint(3163, 4607, 1));
		room6P3 = new Zone(new WorldPoint(3159, 4596, 1), new WorldPoint(3175, 4602, 1));
		pillar1 = new Zone(new WorldPoint(3148, 4595, 1), new WorldPoint(3148, 4595, 1));
		pillar2 = new Zone(new WorldPoint(3146, 4595, 1), new WorldPoint(3146, 4595, 1));
		pillar3 = new Zone(new WorldPoint(3144, 4595, 1), new WorldPoint(3144, 4595, 1));
		pillar4 = new Zone(new WorldPoint(3142, 4595, 1), new WorldPoint(3142, 4595, 1));
		pillar5 = new Zone(new WorldPoint(3144, 4597, 1), new WorldPoint(3144, 4597, 1));
		pillar6 = new Zone(new WorldPoint(3144, 4599, 1), new WorldPoint(3144, 4599, 1));
		switch1 = new Zone(new WorldPoint(3137, 4593, 1), new WorldPoint(3140, 4601, 1));
		room6PastTrap1 = new Zone(new WorldPoint(3150, 4604, 1), new WorldPoint(3153, 4604, 1));
		room6PastTrap2P1 = new Zone(new WorldPoint(3154, 4604, 1), new WorldPoint(3160, 4604, 1));
		room6PastTrap2P2 = new Zone(new WorldPoint(3159, 4596, 1), new WorldPoint(3175, 4602, 1));
		extraRoom1 = new Zone(new WorldPoint(3160, 4597, 0), new WorldPoint(3173, 4599, 0));
		extraRoom2 = new Zone(new WorldPoint(3140, 4594, 0), new WorldPoint(3150, 4601, 0));
		room7 = new Zone(new WorldPoint(3179, 4593, 1), new WorldPoint(3195, 4602, 1));
		room8 = new Zone(new WorldPoint(3181, 4595, 0), new WorldPoint(3189, 4601, 0));
		bossRoom = new Zone(new WorldPoint(3138, 4670, 0), new WorldPoint(3160, 4643, 0));
		brainDeath2 = new Zone(new WorldPoint(2129, 5116, 1), new WorldPoint(2170, 5085, 1));
		brainDeath1 = new Zone(new WorldPoint(2111, 5184, 0), new WorldPoint(2177, 5040, 0));
		boat = new Zone(new WorldPoint(3709, 3508, 1), new WorldPoint(3721, 3489, 1));
		mos = new Zone(new WorldPoint(3642, 3077, 0), new WorldPoint(3855, 2924, 1));
	}

	public void setupSteps()
	{
		// Shoutout to zoinkwiz for the route below from the lair of tarn razorlor miniquest
		enterHauntedMine = new ObjectStep(this, ObjectID.CART_TUNNEL, new WorldPoint(3440, 3232, 0),
			"Enter the Haunted Mine. If you have a Slayer Ring, instead teleport to Tarn's Lair to skip most of the quest.");
		enterLair = new ObjectStep(this, ObjectID.ENTRANCE_15833, new WorldPoint(3424, 9661, 0),
			"Enter the entrance to the north.");
		searchWallRoom1 = new ObjectStep(this, ObjectID.WALL_20588, new WorldPoint(3196, 4557, 0),
			"Follow the path west then north, and go through the door you reach.");
		searchWallRoom1.setLinePoints(Arrays.asList(
			new WorldPoint(3166, 4547, 0),
			new WorldPoint(3166, 4550, 0),
			new WorldPoint(3175, 4550, 0),
			new WorldPoint(3175, 4549, 0),
			new WorldPoint(3184, 4549, 0),
			new WorldPoint(3184, 4548, 0),
			new WorldPoint(3189, 4548, 0),
			new WorldPoint(3190, 4547, 0),
			new WorldPoint(3191, 4548, 0),
			new WorldPoint(3190, 4550, 0),
			new WorldPoint(3190, 4555, 0),
			new WorldPoint(3196, 4555, 0),
			new WorldPoint(3196, 4556, 0)
		));
		searchWall2Room1 = new ObjectStep(this, ObjectID.WALL_20588, new WorldPoint(3197, 4562, 0),
			"Follow the path west then north, and go through the door you reach.");
		goThroughRoom1 = new ObjectStep(this, ObjectID.PASSAGEWAY_20517, new WorldPoint(3195, 4571, 0),
			"Follow the path west then north, and go through the door you reach.");
		goThroughRoom1.addSubSteps(searchWallRoom1, searchWall2Room1);
		goThroughRoom2 = new ObjectStep(this, ObjectID.PASSAGEWAY_20513, new WorldPoint(3174, 4577, 1),
			"Continue out the west of this room.");
		goThroughRoom2.setLinePoints(Arrays.asList(
			new WorldPoint(3195, 4575, 1),
			new WorldPoint(3195, 4579, 1),
			new WorldPoint(3196, 4580, 1),
			new WorldPoint(3195, 4581, 1),
			new WorldPoint(3195, 4585, 1),
			new WorldPoint(3182, 4585, 1),
			new WorldPoint(3181, 4586, 1),
			new WorldPoint(3176, 4586, 1),
			new WorldPoint(3176, 4583, 1),
			new WorldPoint(3175, 4582, 1),
			new WorldPoint(3175, 4577, 1)
		));
		goThroughRoom3 = new ObjectStep(this, ObjectID.PASSAGEWAY_20523, new WorldPoint(3168, 4580, 0),
			"Go through the north door.");
		goThroughRoom4 = new ObjectStep(this, ObjectID.PASSAGEWAY_20525, new WorldPoint(3165, 4589, 0),
			"Go through the west door.");
		leaveExtraRoom1 = new ObjectStep(this, ObjectID.PASSAGEWAY_20531, new WorldPoint(3168, 4596, 0),
			"Go into the south door.");
		leaveExtraRoom2 = new ObjectStep(this, ObjectID.PASSAGEWAY_20529, new WorldPoint(3150, 4598, 0),
			"Go into the east passageway.");
		goThroughRoom5 = new ObjectStep(this, ObjectID.PASSAGEWAY_20533, new WorldPoint(3154, 4597, 1),
			"Go through the north door.");
		goThroughRoom5.addSubSteps(leaveExtraRoom1, leaveExtraRoom2);
		jumpToPillar1 = new ObjectStep(this, ObjectID.PILLAR_20543, new WorldPoint(3148, 4595, 1),
			"Jump across the pillars to the west ledge.");
		jumpToPillar2 = new ObjectStep(this, ObjectID.PILLAR_20544, new WorldPoint(3146, 4595, 1),
			"Jump across the pillars.");
		jumpToPillar3 = new ObjectStep(this, ObjectID.PILLAR_20545, new WorldPoint(3144, 4595, 1),
			"Jump across the pillars.");
		jumpToPillar4 = new ObjectStep(this, ObjectID.PILLAR_20546, new WorldPoint(3142, 4595, 1),
			"Jump across the pillars.");
		jumpToSwitch = new ObjectStep(this, ObjectID.LEDGE_20562, new WorldPoint(3140, 4595, 1),
			"Jump to the ledge.");
		jumpToPillar1.addSubSteps(jumpToPillar2, jumpToPillar3, jumpToPillar4, jumpToSwitch);
		pressSwitch = new ObjectStep(this, ObjectID.FLOOR_20634, new WorldPoint(3138, 4595, 1),
			"Search the floor.");
		jumpBackToPillar4 = new ObjectStep(this, ObjectID.PILLAR_20546, new WorldPoint(3142, 4595, 1),
			"Jump across the pillars to the north side.");
		jumpBackToPillar3 = new ObjectStep(this, ObjectID.PILLAR_20545, new WorldPoint(3144, 4595, 1),
			"Jump across the pillars to the north side.");
		jumpToPillar5 = new ObjectStep(this, ObjectID.PILLAR_20547, new WorldPoint(3144, 4597, 1),
			"Jump across the pillars to the north side.");
		jumpToPillar6 = new ObjectStep(this, ObjectID.PILLAR_20548, new WorldPoint(3144, 4599, 1),
			"Jump across the pillars to the north side.");
		jumpToNorthLedge = new ObjectStep(this, ObjectID.LEDGE_20563, new WorldPoint(3144, 4601, 1),
			"Jump across the pillars to the north side.");
		jumpToNorthLedge.addSubSteps(jumpBackToPillar3, jumpBackToPillar4, jumpToPillar5, jumpToPillar6);
		searchWallRoom6 = new ObjectStep(this, ObjectID.WALL_20590, new WorldPoint(3149, 4604, 1),
			"Follow the path to the east.");
		searchWall2Room6 = new ObjectStep(this, ObjectID.WALL_20590, new WorldPoint(3154, 4605, 1),
			"Follow the path to the east.");
		goThroughRoom6 = new ObjectStep(this, ObjectID.PASSAGEWAY_20535, new WorldPoint(3176, 4598, 1),
			"Follow the path to the east.");
		goThroughRoom6.addSubSteps(searchWallRoom6, searchWall2Room6);
		goThroughRoom7 = new ObjectStep(this, ObjectID.STAIRS_17098, new WorldPoint(3193, 4598, 1),
			"Activate Protect from Magic and jump across the pillars. Go down the stairs.", protectFromMagic);
		enterBossRoom = new ObjectStep(this, ObjectID.PASSAGEWAY_20539, new WorldPoint(3185, 4602, 0),
			"Enter the north passageway, and be prepared to fight.");
		moveToMine = new ObjectStep(this, ObjectID.CART_TUNNEL, new WorldPoint(3440, 3232, 0),
			"Enter the haunted mine or use slayer ring to teleport directly to Tarn's Lair.");
		terrorDog = new NpcStep(this, NpcID.TERROR_DOG, new WorldPoint(3149, 4652, 0),
			"Kill a terror dog. You can enter the room with the diary to safe zone.", true);
		terrorDog.addAlternateNpcs(NpcID.TERROR_DOG_6474);

		canifisAgi = new ObjectStep(this, ObjectID.TALL_TREE_14843, new WorldPoint(3507, 3489, 0),
			"Complete a lap of the Canifis Rooftop Course.");

		swampLizard = new ObjectStep(this, ObjectID.YOUNG_TREE_9341, new WorldPoint(3532, 3447, 0),
			"Catch a swamp lizard.");

		swampBoaty = new ObjectStep(this, ObjectID.SWAMP_BOATY_6970, new WorldPoint(3499, 3378, 0),
			"Board the Swamp boaty at the Hollows.");

		ectophialTP = new ItemStep(this, "Use your Ectophial to teleport to Port Phasmatys.", ectophial.highlighted());

		guthBalance = new ItemStep(this, "Mix a Guthix balance potion while in Morytania.", restorePot.highlighted(),
			garlic.highlighted(), silverDust.highlighted());

		hollowTree = new ObjectStep(this, ObjectID.HOLLOW_TREE_10830, new WorldPoint(3663, 3451, 0),
			"Chop some bark off the hollow tree south of Port phasmatys.", axe);

		cannonBall = new ObjectStep(this, ObjectID.FURNACE_24009, new WorldPoint(3689, 3479, 0),
			"Make a batch of cannonballs at Port Phasmatys.", ammoMould, steelBar);

		dragontoothIsland = new NpcStep(this, NpcID.GHOST_CAPTAIN, new WorldPoint(3703, 3487, 0),
			"Talk to the Ghost captain at Port Phasmatys to travel to Dragontooth Island.", ectoToken.quantity(25),
			ghostspeakAmulet);

		moveToBrainDeath = new NpcStep(this, NpcID.PIRATE_PETE, new WorldPoint(3681, 3536, 0),
			"Talk to Pirate Pete to travel to Braindeath Island.");
		moveToBrainDeath.addDialogStep("Okay!");
		moveToDownstairs = new ObjectStep(this, ObjectID.LADDER_10168, new WorldPoint(2139, 5105, 1),
			"Climb down the ladder.");
		feverSpider = new NpcStep(this, NpcID.FEVER_SPIDER, new WorldPoint(2141, 5103, 0),
			"Kill a Fever spider.", slayerGloves, combatGear, food);

		moveToCapt = new ObjectStep(this, ObjectID.GANGPLANK_11209, new WorldPoint(3710, 3496, 0),
			"Cross the gangplank to Bill Teach's ship. Alternatively use the Group finder to teleport directly there.");
		moveToMos = new NpcStep(this, NpcID.BILL_TEACH_4016, new WorldPoint(3714, 3497, 1),
			"Talk to Bill Teach to travel to Mos Le'Harmless.");
		troubleBrewing = new NpcStep(this, NpcID.HONEST_JIMMY, new WorldPoint(3811, 3021, 0),
			"Start playing Trouble brewing. You don't need to win. You will need to empty your inventory and unequip " +
				"any helmets.");

		claimReward = new NpcStep(this, NpcID.LESABR, new WorldPoint(3464, 3480, 0),
			"Talk to Le-Sabre near Canifis to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, rope, smallFishingNet, axe, ectoToken.quantity(25), ghostspeakAmulet,
			steelBar, ammoMould, slayerGloves, ectophial, restorePot, garlic, silverDust);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, slayerRing, fairyAccess);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 40));
		reqs.add(new SkillRequirement(Skill.COOKING, 40));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 22));
		reqs.add(new SkillRequirement(Skill.HUNTER, 29));
		reqs.add(new SkillRequirement(Skill.SLAYER, 42));
		reqs.add(new SkillRequirement(Skill.SMITHING, 35));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 45));

		reqs.add(dwarfCannon);
		reqs.add(ghostsAhoy);
		reqs.add(lairOfTarnRaz);
		reqs.add(cabinFever);
		reqs.add(inAidOfMyreque);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("a Terror dog (lvl 100) and a Fever spider (lvl 49)");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails swampBoatySteps = new PanelDetails("Swamp Boaty", Collections.singletonList(swampBoaty));
		swampBoatySteps.setDisplayCondition(notSwampBoaty);
		allSteps.add(swampBoatySteps);

		PanelDetails terrorDogSteps = new PanelDetails("Terror Dog", Collections.singletonList(terrorDog),
			lairOfTarnRaz, combatGear, food);
		terrorDogSteps.setDisplayCondition(notTerrorDog);
		allSteps.add(terrorDogSteps);

		PanelDetails canifisSteps = new PanelDetails("Canifis Rooftop Course", Collections.singletonList(canifisAgi),
			new SkillRequirement(Skill.AGILITY, 40));
		canifisSteps.setDisplayCondition(notCanifisAgi);
		allSteps.add(canifisSteps);

		PanelDetails swampLizardSteps = new PanelDetails("Swamp Lizard", Collections.singletonList(swampLizard),
			new SkillRequirement(Skill.HUNTER, 29), rope, smallFishingNet);
		swampLizardSteps.setDisplayCondition(notSwampLizard);
		allSteps.add(swampLizardSteps);

		PanelDetails ectophialSteps = new PanelDetails("Ectophial", Collections.singletonList(ectophialTP),
			ghostsAhoy, ectophial);
		ectophialSteps.setDisplayCondition(notEctophialTP);
		allSteps.add(ectophialSteps);

		PanelDetails guthSteps = new PanelDetails("Guthix Balance", Collections.singletonList(guthBalance),
			new SkillRequirement(Skill.HERBLORE, 22), inAidOfMyreque, restorePot, garlic, silverDust);
		guthSteps.setDisplayCondition(notGuthBalance);
		allSteps.add(guthSteps);

		PanelDetails hollowSteps = new PanelDetails("Hollow Tree", Collections.singletonList(hollowTree),
			new SkillRequirement(Skill.WOODCUTTING, 45), axe);
		hollowSteps.setDisplayCondition(notHollowTree);
		allSteps.add(hollowSteps);

		PanelDetails cannonballsSteps = new PanelDetails("Cannonballs", Collections.singletonList(cannonBall),
			new SkillRequirement(Skill.SMITHING, 35), dwarfCannon, ammoMould, steelBar);
		cannonballsSteps.setDisplayCondition(notCannonBall);
		allSteps.add(cannonballsSteps);

		PanelDetails dragSteps = new PanelDetails("Dragontooth Island", Collections.singletonList(dragontoothIsland),
			ectoToken.quantity(25), ghostspeakAmulet);
		dragSteps.setDisplayCondition(notDragontoothIsland);
		allSteps.add(dragSteps);

		PanelDetails feverSteps = new PanelDetails("Fever Spider", Arrays.asList(moveToBrainDeath,
			moveToDownstairs, feverSpider),
			new SkillRequirement(Skill.SLAYER, 42), rumDeal, slayerGloves);
		feverSteps.setDisplayCondition(notFeverSpider);
		allSteps.add(feverSteps);

		PanelDetails troubleSteps = new PanelDetails("Trouble Brewing", Arrays.asList(moveToCapt, moveToMos, troubleBrewing),
			new SkillRequirement(Skill.COOKING, 40), cabinFever);
		troubleSteps.setDisplayCondition(notTroubleBrewing);
		allSteps.add(troubleSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}