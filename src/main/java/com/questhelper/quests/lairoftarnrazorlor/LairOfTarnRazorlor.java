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
package com.questhelper.quests.lairoftarnrazorlor;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import com.questhelper.requirements.PrayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.ObjectCondition;
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
import net.runelite.api.Prayer;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.LAIR_OF_TARN_RAZORLOR
)
public class LairOfTarnRazorlor extends BasicQuestHelper
{
	ItemRequirement combatGear, diary;
	PrayerRequirement protectFromMagic;

	ConditionForStep inHauntedMine, inRoom1, inRoom1PastTrap1, inRoom1PastTrap2, inRoom2, inRoom3, inRoom4, inRoom5, inRoom6P1, inRoom6P2,
		onPillar1, onPillar2, onPillar3, onPillar4, onPillar5, onPillar6, atSwitch1, switchPressed, inRoom6PastTrap1, inRoom6PastTrap2,
		inExtraRoom1, inExtraRoom2, inRoom7, inRoom8, inBossRoom, tarnInSecondForm, killedTarn, inFinalRoom;

	DetailedQuestStep enterHauntedMine, enterLair, searchWallRoom1, searchWall2Room1, goThroughRoom1, goThroughRoom2, goThroughRoom3,
		goThroughRoom4, goThroughRoom5, jumpToPillar1, jumpToPillar2, jumpToPillar3, jumpToPillar4, jumpToPillar5, jumpToPillar6,
		jumpToSwitch, pressSwitch, jumpBackToPillar4, jumpBackToPillar3, jumpToNorthLedge, searchWallRoom6, searchWall2Room6, goThroughRoom6,
		leaveExtraRoom1, leaveExtraRoom2, goThroughRoom7, enterBossRoom, killTarn1, killTarn2, enterFinalRoom, pickUpDiary;

	Zone hauntedMine, room1, room1PastTrap1, room1PastTrap2, room2, room3, room4, room5P1, room5P2, room6P1, room6P2, room6P3, pillar1, pillar2, pillar3,
		pillar4, switch1, pillar5, pillar6, room6PastTrap1, room6PastTrap2P1, room6PastTrap2P2, extraRoom1, extraRoom2, room7, room8, bossRoom, finalRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep fullQuest = new ConditionalStep(this, enterHauntedMine);
		fullQuest.addStep(inFinalRoom, pickUpDiary);
		fullQuest.addStep(new Conditions(inBossRoom, killedTarn), enterFinalRoom);
		fullQuest.addStep(new Conditions(inBossRoom, tarnInSecondForm), killTarn2);
		fullQuest.addStep(inBossRoom, killTarn1);
		fullQuest.addStep(inRoom8, enterBossRoom);
		fullQuest.addStep(inRoom7, goThroughRoom7);
		fullQuest.addStep(inRoom6PastTrap2, goThroughRoom6);
		fullQuest.addStep(inRoom6PastTrap1, searchWall2Room6);
		fullQuest.addStep(inRoom6P2, searchWallRoom6);
		fullQuest.addStep(onPillar6, jumpToNorthLedge);
		fullQuest.addStep(onPillar5, jumpToPillar6);
		fullQuest.addStep(new Conditions(onPillar3, switchPressed), jumpToPillar5);
		fullQuest.addStep(new Conditions(onPillar4, switchPressed), jumpBackToPillar3);
		fullQuest.addStep(new Conditions(atSwitch1, switchPressed), jumpBackToPillar4);
		fullQuest.addStep(atSwitch1, pressSwitch);
		fullQuest.addStep(onPillar4, jumpToSwitch);
		fullQuest.addStep(onPillar3, jumpToPillar4);
		fullQuest.addStep(onPillar2, jumpToPillar3);
		fullQuest.addStep(onPillar1, jumpToPillar2);
		fullQuest.addStep(inRoom6P1, jumpToPillar1);
		fullQuest.addStep(inRoom5, goThroughRoom5);
		fullQuest.addStep(inRoom4, goThroughRoom4);
		fullQuest.addStep(inRoom3, goThroughRoom3);
		fullQuest.addStep(inRoom2, goThroughRoom2);
		fullQuest.addStep(inRoom1PastTrap2, goThroughRoom1);
		fullQuest.addStep(inRoom1PastTrap1, searchWall2Room1);
		fullQuest.addStep(inRoom1, searchWallRoom1);
		fullQuest.addStep(inHauntedMine, enterLair);

		steps.put(0, fullQuest);
		steps.put(1, fullQuest);
		steps.put(2, fullQuest);
		return steps;
	}

	public void setupItemRequirements()
	{
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		diary = new ItemRequirement("Tarn's diary", ItemID.TARNS_DIARY);
		diary.setHighlightInInventory(true);
		protectFromMagic = new PrayerRequirement("Activate Protect from Magic", Prayer.PROTECT_FROM_MAGIC);
	}

	public void loadZones()
	{
		hauntedMine = new Zone(new WorldPoint(3390, 9600, 0), new WorldPoint(3452, 9668, 0));
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
		bossRoom = new Zone(new WorldPoint(3176, 4611, 0), new WorldPoint(3196, 4626, 0));
		finalRoom = new Zone(new WorldPoint(3181, 4632, 0), new WorldPoint(3191, 4637, 0));
	}

	public void setupConditions()
	{
		inHauntedMine = new ZoneCondition(hauntedMine);
		inRoom1 = new ZoneCondition(room1);
		inRoom1PastTrap1 = new ZoneCondition(room1PastTrap1);
		inRoom1PastTrap2 = new ZoneCondition(room1PastTrap2);
		inRoom2 = new ZoneCondition(room2);
		inRoom3 = new ZoneCondition(room3);
		inRoom4 = new ZoneCondition(room4);
		inRoom5 = new ZoneCondition(room5P1, room5P2);
		inRoom6P1 = new ZoneCondition(room6P1);
		inRoom6P2 = new ZoneCondition(room6P2, room6P3);
		inRoom7 = new ZoneCondition(room7);
		onPillar1 = new ZoneCondition(pillar1);
		onPillar2 = new ZoneCondition(pillar2);
		onPillar3 = new ZoneCondition(pillar3);
		onPillar4 = new ZoneCondition(pillar4);
		onPillar5 = new ZoneCondition(pillar5);
		onPillar6 = new ZoneCondition(pillar6);
		atSwitch1 = new ZoneCondition(switch1);
		switchPressed = new ObjectCondition(ObjectID.FLOOR_20635, new WorldPoint(3138, 4595, 1));
		inRoom6PastTrap1 = new ZoneCondition(room6PastTrap1);
		inRoom6PastTrap2 = new ZoneCondition(room6PastTrap2P1, room6PastTrap2P2);
		inExtraRoom1 = new ZoneCondition(extraRoom1);
		inExtraRoom2 = new ZoneCondition(extraRoom2);
		inRoom8 = new ZoneCondition(room8);
		inBossRoom = new ZoneCondition(bossRoom);
		inFinalRoom = new ZoneCondition(finalRoom);

		tarnInSecondForm = new NpcCondition(NpcID.TARN);
		killedTarn = new VarbitCondition(3290, 2, Operation.GREATER_EQUAL);
	}

	public void setupSteps()
	{
		enterHauntedMine = new ObjectStep(this, ObjectID.CART_TUNNEL, new WorldPoint(3440, 3232, 0), "Enter the Haunted Mine. If you have a Slayer Ring, instead teleport to Tarn's Lair to skip most of the quest.");
		enterLair = new ObjectStep(this, ObjectID.ENTRANCE_15833, new WorldPoint(3424, 9661, 0), "Enter the entrance to the north.");

		searchWallRoom1 = new ObjectStep(this, ObjectID.WALL_20588, new WorldPoint(3196, 4557, 0), "Follow the path west then north, and go through the door you reach.");
		searchWallRoom1.setLinePoints(new ArrayList<>(Arrays.asList(
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
		)));

		searchWall2Room1 = new ObjectStep(this, ObjectID.WALL_20588, new WorldPoint(3197, 4562, 0), "Follow the path west then north, and go through the door you reach.");

	    goThroughRoom1 = new ObjectStep(this, ObjectID.PASSAGEWAY_20517, new WorldPoint(3195, 4571, 0), "Follow the path west then north, and go through the door you reach.");
	    goThroughRoom1.addSubSteps(searchWallRoom1, searchWall2Room1);

	    goThroughRoom2 = new ObjectStep(this, ObjectID.PASSAGEWAY_20513, new WorldPoint(3174, 4577, 1), "Continue out the west of this room.");
	    goThroughRoom2.setLinePoints(new ArrayList<>(Arrays.asList(
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
		)));

	    goThroughRoom3 = new ObjectStep(this, ObjectID.PASSAGEWAY_20523, new WorldPoint(3168, 4580, 0), "Go through the north door.");
	    goThroughRoom4 = new ObjectStep(this, ObjectID.PASSAGEWAY_20525, new WorldPoint(3165, 4589, 0), "Go through the west door.");

		leaveExtraRoom1 = new ObjectStep(this, ObjectID.PASSAGEWAY_20531, new WorldPoint(3168, 4596, 0), "Go into the south door.");
		leaveExtraRoom2 = new ObjectStep(this, ObjectID.PASSAGEWAY_20529, new WorldPoint(3150, 4598, 0), "Go into the east passageway.");

		goThroughRoom5 = new ObjectStep(this, ObjectID.PASSAGEWAY_20533, new WorldPoint(3154, 4597, 1), "Go through the north door.");
		goThroughRoom5.addSubSteps(leaveExtraRoom1, leaveExtraRoom2);

	    jumpToPillar1 = new ObjectStep(this, ObjectID.PILLAR_20543, new WorldPoint(3148, 4595, 1), "Jump across the pillars to the west ledge.");
		jumpToPillar2 = new ObjectStep(this, ObjectID.PILLAR_20544, new WorldPoint(3146, 4595, 1), "Jump across the pillars.");
		jumpToPillar3 = new ObjectStep(this, ObjectID.PILLAR_20545, new WorldPoint(3144, 4595, 1), "Jump across the pillars.");
		jumpToPillar4 = new ObjectStep(this, ObjectID.PILLAR_20546, new WorldPoint(3142, 4595, 1), "Jump across the pillars.");

		jumpToSwitch = new ObjectStep(this, ObjectID.LEDGE_20562, new WorldPoint(3140, 4595, 1), "Jump to the ledge.");
		jumpToPillar1.addSubSteps(jumpToPillar2, jumpToPillar3, jumpToPillar4, jumpToSwitch);

		pressSwitch = new ObjectStep(this, ObjectID.FLOOR_20634, new WorldPoint(3138, 4595, 1), "Search the floor.");

		jumpBackToPillar4 = new ObjectStep(this, ObjectID.PILLAR_20546, new WorldPoint(3142, 4595, 1), "Jump across the pillars to the north side.");
		jumpBackToPillar3 = new ObjectStep(this, ObjectID.PILLAR_20545, new WorldPoint(3144, 4595, 1), "Jump across the pillars to the north side.");
		jumpToPillar5 = new ObjectStep(this, ObjectID.PILLAR_20547, new WorldPoint(3144, 4597, 1), "Jump across the pillars to the north side.");
		jumpToPillar6 = new ObjectStep(this, ObjectID.PILLAR_20548, new WorldPoint(3144, 4599, 1), "Jump across the pillars to the north side.");

		jumpToNorthLedge = new ObjectStep(this, ObjectID.LEDGE_20563, new WorldPoint(3144, 4601, 1), "Jump across the pillars to the north side.");
		jumpToNorthLedge.addSubSteps(jumpBackToPillar3, jumpBackToPillar4, jumpToPillar5, jumpToPillar6);

		searchWallRoom6 = new ObjectStep(this, ObjectID.WALL_20590, new WorldPoint(3149, 4604, 1), "Follow the path to the east.");
		searchWall2Room6 = new ObjectStep(this, ObjectID.WALL_20590, new WorldPoint(3154, 4605, 1), "Follow the path to the east.");

		goThroughRoom6 = new ObjectStep(this, ObjectID.PASSAGEWAY_20535, new WorldPoint(3176, 4598, 1), "Follow the path to the east.");
		goThroughRoom6.addSubSteps(searchWallRoom6, searchWall2Room6);

		goThroughRoom7 = new ObjectStep(this, ObjectID.STAIRS_17098, new WorldPoint(3193, 4598, 1), "Activate Protect from Magic and jump across the pillars. Go down the stairs.", protectFromMagic);

		enterBossRoom = new ObjectStep(this, ObjectID.PASSAGEWAY_20539, new WorldPoint(3185, 4602, 0), "Enter the north passageway, and be prepared to fight.");

		killTarn1 = new NpcStep(this, NpcID.MUTANT_TARN, new WorldPoint(3186, 4619, 0), "Kill Mutatant Tarn.");
		killTarn2 = new NpcStep(this, NpcID.TARN, new WorldPoint(3186, 4619, 0), "Kill Ghost Tarn.");

		enterFinalRoom = new ObjectStep(this, ObjectID.PASSAGEWAY_15774, new WorldPoint(3186, 4627, 0), "Go into the north passageway.");
		pickUpDiary = new ItemStep(this, "Pick up Tarn's diary. Quest complete!", diary);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Collections.singletonList(combatGear));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Collections.singletonList("Tarn (level 69) twice"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Traversing the dungeon", new ArrayList<>(Arrays.asList(enterHauntedMine, enterLair, goThroughRoom1, goThroughRoom2, goThroughRoom3,
			goThroughRoom4, goThroughRoom5, jumpToPillar1, pressSwitch, jumpToNorthLedge, goThroughRoom6, goThroughRoom7, enterBossRoom, killTarn1, killTarn2, enterFinalRoom, pickUpDiary)), combatGear));
		return allSteps;
	}
}

