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
package com.questhelper.helpers.quests.observatoryquest;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.VarbitID;

public class ObservatoryQuest extends BasicQuestHelper
{
	// Required items
	ItemRequirement plank;
	ItemRequirement bronzeBar;
	ItemRequirement moltenGlass;

	// Recommended items
	ItemRequirement food;
	ItemRequirement duelingRing;
	ItemRequirement antipoison;

	// Mid-quest item requirements
	ItemRequirement mould;
	ItemRequirement lens;
	ItemRequirement key;

	// Zones
	Zone observatoryDungeon;
	Zone observatoryF1;
	Zone observatoryF2;

	// Miscllaneous requirements
	ZoneRequirement inObservatoryDungeon;
	ZoneRequirement inObservatoryF1;
	ZoneRequirement inObservatoryF2;
	VarbitRequirement usedKey;
	NpcCondition sleepingGuardNearby;
	VarbitRequirement hasMould;
	VarbitRequirement lookedThroughTelescope;

	// Steps
	NpcStep talkToProfessor;

	NpcStep giveProfessorPlanks;

	NpcStep giveProfessorBar;

	NpcStep giveProfessorGlass;

	NpcStep talkToAssistant;
	ObjectStep enterDungeon;
	ObjectStep searchChests;
	NpcStep prodGuard;
	ObjectStep inspectStove;
	ObjectStep leaveDungeon;
	NpcStep giveProfessorMould;
	DetailedQuestStep useGlassOnMould;
	NpcStep giveProfessorLensAndMould;
	ObjectStep enterDungeonAgain;
	ObjectStep enterObservatory;
	ObjectStep goToF2Observatory;
	ObjectStep viewTelescope;
	NpcStep tellProfessorConstellation;

	@Override
	protected void setupZones()
	{
		observatoryDungeon = new Zone(new WorldPoint(2295, 9340, 0), new WorldPoint(2370, 9410, 0));
		observatoryF1 = new Zone(new WorldPoint(2433, 3154, 0), new WorldPoint(2448, 3169, 0));
		observatoryF2 = new Zone(new WorldPoint(2433, 3154, 1), new WorldPoint(2448, 3169, 1));
	}

	@Override
	protected void setupRequirements()
	{
		plank = new ItemRequirement("Plank", ItemID.WOODPLANK);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		duelingRing = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS);
		antipoison = new ItemRequirement("Antipoison (there is a spawn of superantipoison near the Observatory)", ItemCollections.ANTIPOISONS);

		mould = new ItemRequirement("Lens mould", ItemID.LENS_MOULD).isNotConsumed();
		lens = new ItemRequirement("Observatory lens", ItemID.LENS).isNotConsumed();
		key = new ItemRequirement("Goblin kitchen key", ItemID.KEEP_KEY).isNotConsumed();

		inObservatoryDungeon = new ZoneRequirement(observatoryDungeon);
		inObservatoryF1 = new ZoneRequirement(observatoryF1);
		inObservatoryF2 = new ZoneRequirement(observatoryF2);

		usedKey = new VarbitRequirement(VarbitID.OBSERVATORY_GATELOCK, 1);
		sleepingGuardNearby = new NpcCondition(NpcID.QIP_OBS_GOBLIN_GUARD);
		hasMould = new VarbitRequirement(VarbitID.OBSERVATORY_MOULD_PRES, 1);
		lookedThroughTelescope = new VarbitRequirement(VarbitID.OBSERVATORY_SCOPELOOKED, 1);
	}

	public void setupSteps()
	{
		talkToProfessor = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Talk to the Observatory professor north of Castle Wars.", plank.quantity(3), moltenGlass, bronzeBar);
		talkToProfessor.addDialogSteps("Talk about the Observatory quest.", "An Observatory?", "Yes.");

		giveProfessorPlanks = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor 3 planks.", plank.quantity(3));
		giveProfessorPlanks.addDialogSteps("Talk about the Observatory quest.");

		giveProfessorBar = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor a bronze bar.", bronzeBar);
		giveProfessorBar.addDialogSteps("Talk about the Observatory quest.");

		giveProfessorGlass = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor some molten glass.", moltenGlass);
		giveProfessorGlass.addDialogSteps("Talk about the Observatory quest.");

		talkToAssistant = new NpcStep(this, NpcID.QIP_OBS_PROFFESORS_ASSISTANT, new WorldPoint(2443, 3189, 0),
			"Talk to the observatory assistant.");
		enterDungeon = new ObjectStep(this, ObjectID.QIP_OBS_VSTAIRS2, new WorldPoint(2458, 3186, 0),
			"Enter the dungeon east of the Professor.");
		searchChests = new ObjectStep(this, ObjectID.QIP_OBS_DUNGEON_CHEST_CLOSED, "Search only the marked chests in the dungeon. " +
			"Unmarked chests contain monsters and may poison you.");
		searchChests.addAlternateObjects(ObjectID.QIP_OBS_DUNGEON_CHEST_OPEN, ObjectID.QIP_OBS_DUNGEON_CHEST_CLOSED2,
			ObjectID.QIP_OBS_DUNGEON_CHEST_OPEN2, ObjectID.QIP_OBS_DUNGEON_CHEST_CLOSED3, ObjectID.QIP_OBS_DUNGEON_CHEST_OPEN3);
		prodGuard = new NpcStep(this, NpcID.QIP_OBS_GOBLIN_GUARD, new WorldPoint(2327, 9394, 0),
			"Prod the sleeping guard in the north of the dungeon. He'll attack you. You need to then either kill him," +
				" or get him in the marked spot to the north of the gate.");
		prodGuard.addTileMarker(new WorldPoint(2327, 9399, 0), SpriteID.BarbassaultIcons.HORN_FOR_HEALER);
		inspectStove = new ObjectStep(this, ObjectID.QIP_OBS_DUNGEON_STOVE_TOP_MULTI, new WorldPoint(2327, 9389, 0),
			"Either kill or trap the guard on the marked tile to the north, then search the goblin stove.");
		inspectStove.addTileMarker(new WorldPoint(2327, 9399, 0), SpriteID.BarbassaultIcons.HORN_FOR_HEALER);
		leaveDungeon = new ObjectStep(this, ObjectID.QIP_OBS_STAIRS1_DUNGEON, new WorldPoint(2355, 9396, 0),
			"Climb the stairs back to the surface.");
		giveProfessorMould = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor the lens mould. If you don't have it, check your bank.", mould);
		giveProfessorMould.addDialogSteps("Talk about the Observatory quest.");
		useGlassOnMould = new DetailedQuestStep(this, "Use the molten glass on the mould.", moltenGlass.highlighted()
			, mould.highlighted());
		giveProfessorLensAndMould = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor the lens mould and lens.", lens);
		giveProfessorLensAndMould.addDialogSteps("Talk about the Observatory quest.");
		enterDungeonAgain = new ObjectStep(this, ObjectID.QIP_OBS_VSTAIRS2, new WorldPoint(2458, 3186, 0),
			"Enter the dungeon east of the Professor.");
		enterObservatory = new ObjectStep(this, ObjectID.QIP_OBS_STAIRS1_DUNGEON, new WorldPoint(2335, 9352, 0),
			"Follow the dungeon around anti-clockwise to a staircase, then climb it.");
		goToF2Observatory = new ObjectStep(this, ObjectID.QIP_OBS_STAIRS1, new WorldPoint(2444, 3160, 0),
			"Climb up the stairs in the observatory.");
		viewTelescope = new ObjectStep(this, ObjectID.QIP_OBS_TELE_GEAR_UPPER_MULTI, new WorldPoint(2441, 3162, 1),
			"Use the telescope.");
		tellProfessorConstellation = new StarSignAnswer(this);
		tellProfessorConstellation.addDialogSteps("Talk about the Observatory quest.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToProfessor);
		steps.put(1, giveProfessorPlanks);
		steps.put(2, giveProfessorBar);
		steps.put(3, giveProfessorGlass);

		var goGetLens = new ConditionalStep(this, enterDungeon);
		goGetLens.addStep(and(inObservatoryDungeon, hasMould), leaveDungeon);
		goGetLens.addStep(hasMould, giveProfessorMould);
		goGetLens.addStep(and(inObservatoryDungeon, or(key.alsoCheckBank(), usedKey), sleepingGuardNearby), prodGuard);
		goGetLens.addStep(and(inObservatoryDungeon, or(key.alsoCheckBank(), usedKey)), inspectStove);
		goGetLens.addStep(inObservatoryDungeon, searchChests);
		steps.put(4, goGetLens);

		var makeLens = new ConditionalStep(this, enterDungeon);
		makeLens.addStep(lens, giveProfessorLensAndMould);
		makeLens.addStep(and(inObservatoryDungeon, hasMould), leaveDungeon);
		makeLens.addStep(hasMould, useGlassOnMould);
		makeLens.addStep(and(inObservatoryDungeon, usedKey, sleepingGuardNearby), prodGuard);
		makeLens.addStep(and(inObservatoryDungeon, usedKey), inspectStove);
		steps.put(5, makeLens);

		var goLookInTelescope = new ConditionalStep(this, enterDungeonAgain);
		goLookInTelescope.addStep(and(lookedThroughTelescope, inObservatoryF2), tellProfessorConstellation);
		goLookInTelescope.addStep(inObservatoryF2, viewTelescope);
		goLookInTelescope.addStep(inObservatoryF1, goToF2Observatory);
		goLookInTelescope.addStep(inObservatoryDungeon, enterObservatory);
		steps.put(6, goLookInTelescope);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			plank.quantity(3),
			bronzeBar,
			moltenGlass
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			duelingRing,
			antipoison,
			food
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Goblin Guard (level 42, or you can lure it/have someone else kill it)"
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
			new ExperienceReward(Skill.CRAFTING, 2250)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("A reward depending on which constellation you observed.")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Helping the Professor", List.of(
			talkToProfessor,
			giveProfessorPlanks,
			giveProfessorBar,
			giveProfessorGlass,
			enterDungeon,
			searchChests,
			prodGuard,
			inspectStove,
			leaveDungeon,
			giveProfessorMould,
			useGlassOnMould,
			giveProfessorLensAndMould,
			enterDungeonAgain,
			enterObservatory,
			goToF2Observatory,
			viewTelescope,
			tellProfessorConstellation
		), List.of(
			plank.quantity(3),
			bronzeBar,
			moltenGlass
		)));

		return sections;
	}
}
