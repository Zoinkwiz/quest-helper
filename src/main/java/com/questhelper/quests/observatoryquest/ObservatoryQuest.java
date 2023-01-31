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
package com.questhelper.quests.observatoryquest;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
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

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.OBSERVATORY_QUEST
)
public class ObservatoryQuest extends BasicQuestHelper
{
	//Items Required
	ItemRequirement plank, bronzeBar, moltenGlass;

	//Items Recommended
	ItemRequirement food, duelingRing, antipoison;

	ItemRequirement mould, lens, key;

	Requirement inObservatoryDungeon, inObservatoryF1, inObservatoryF2, usedKey, sleepingGuardNearby, hasMould,
		lookedThroughTelescope;

	DetailedQuestStep talkToProfessor, giveProfessorPlanks, giveProfessorBar, giveProfessorGlass, talkToAssistant,
		enterDungeon, searchChests, prodGuard, inspectStove, leaveDungeon, giveProfessorMould, useGlassOnMould,
		giveProfessorLensAndMould, enterDungeonAgain, enterObservatory, goToF2Observatory, viewTelescope,
		tellProfessorConstellation;

	//Zones
	Zone observatoryDungeon, observatoryF1, observatoryF2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToProfessor);
		steps.put(1, giveProfessorPlanks);
		steps.put(2, giveProfessorBar);
		steps.put(3, giveProfessorGlass);

		ConditionalStep goGetLens = new ConditionalStep(this, enterDungeon);
		goGetLens.addStep(new Conditions(inObservatoryDungeon, hasMould), leaveDungeon);
		goGetLens.addStep(hasMould, giveProfessorMould);
		goGetLens.addStep(new Conditions(inObservatoryDungeon, new Conditions(LogicType.OR,
			key.alsoCheckBank(questBank), usedKey), sleepingGuardNearby), prodGuard);
		goGetLens.addStep(new Conditions(inObservatoryDungeon, new Conditions(LogicType.OR,
				key.alsoCheckBank(questBank), usedKey)), inspectStove);
		goGetLens.addStep(inObservatoryDungeon, searchChests);
		steps.put(4, goGetLens);

		ConditionalStep makeLens = new ConditionalStep(this, enterDungeon);
		makeLens.addStep(lens, giveProfessorLensAndMould);
		makeLens.addStep(new Conditions(inObservatoryDungeon, hasMould), leaveDungeon);
		makeLens.addStep(hasMould, useGlassOnMould);
		makeLens.addStep(new Conditions(inObservatoryDungeon, usedKey, sleepingGuardNearby), prodGuard);
		makeLens.addStep(new Conditions(inObservatoryDungeon, usedKey), inspectStove);
		steps.put(5, makeLens);

		ConditionalStep goLookInTelescope = new ConditionalStep(this, enterDungeonAgain);
		goLookInTelescope.addStep(new Conditions(lookedThroughTelescope, inObservatoryF2), tellProfessorConstellation);
		goLookInTelescope.addStep(inObservatoryF2, viewTelescope);
		goLookInTelescope.addStep(inObservatoryF1, goToF2Observatory);
		goLookInTelescope.addStep(inObservatoryDungeon, enterObservatory);
		steps.put(6, goLookInTelescope);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		plank = new ItemRequirement("Plank", ItemID.PLANK);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		moltenGlass = new ItemRequirement("Molten glass", ItemID.MOLTEN_GLASS);

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		duelingRing = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS);
		antipoison = new ItemRequirement("Antipoison (there is a spawn near the Observatory of superantipoison)",
			ItemCollections.ANTIPOISONS);

		mould = new ItemRequirement("Lens mould", ItemID.LENS_MOULD).isNotConsumed();
		lens = new ItemRequirement("Observatory lens", ItemID.OBSERVATORY_LENS).isNotConsumed();
		key = new ItemRequirement("Goblin kitchen key", ItemID.GOBLIN_KITCHEN_KEY).isNotConsumed();
	}

	public void setupConditions()
	{
		inObservatoryDungeon = new ZoneRequirement(observatoryDungeon);
		inObservatoryF1 = new ZoneRequirement(observatoryF1);
		inObservatoryF2 = new ZoneRequirement(observatoryF2);

		// Started quest
		// 3828 = 9
		// 3827 = 1
		usedKey = new VarbitRequirement(3826, 1);
		sleepingGuardNearby = new NpcCondition(NpcID.SLEEPING_GUARD);
		hasMould = new VarbitRequirement(3837, 1);
		// Watched cutscene, 3838 = 1
		lookedThroughTelescope = new VarbitRequirement(3836, 1);
	}

	public void loadZones()
	{
		observatoryDungeon = new Zone(new WorldPoint(2295, 9340, 0), new WorldPoint(2370, 9410, 0));
		observatoryF1 = new Zone(new WorldPoint(2433, 3154, 0), new WorldPoint(2448, 3169, 0));
		observatoryF2 = new Zone(new WorldPoint(2433, 3154, 1), new WorldPoint(2448, 3169, 1));
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
		talkToAssistant = new NpcStep(this, NpcID.OBSERVATORY_ASSISTANT, new WorldPoint(2443, 3189, 0),
			"Talk to the observatory assistant.");
		enterDungeon = new ObjectStep(this, ObjectID.STAIRS_25432, new WorldPoint(2458, 3186, 0),
			"Enter the dungeon east of the Professor.");
		searchChests = new ObjectStep(this, ObjectID.CHEST_25385, "Search only the marked chests in the dungeon. " +
			"Unmarked chests contain monsters and may poison you.");
		((ObjectStep) searchChests).addAlternateObjects(ObjectID.CHEST_25386, ObjectID.CHEST_25387,
			ObjectID.CHEST_25388, ObjectID.CHEST_25389, ObjectID.CHEST_25390);
		prodGuard = new NpcStep(this, NpcID.SLEEPING_GUARD, new WorldPoint(2327, 9394, 0),
			"Prod the sleeping guard in the north of the dungeon. He'll attack you. You need to then either kill him," +
				" or get him in the marked spot to the north of the gate.");
		prodGuard.addTileMarker(new WorldPoint(2327, 9399, 0), SpriteID.BARBARIAN_ASSAULT_HORN_FOR_HEALER_ICON);
		inspectStove = new ObjectStep(this, NullObjectID.NULL_25442, new WorldPoint(2327, 9389, 0),
			"Either kill or trap the guard on the marked tile to the north, then search the goblin stove.");
		inspectStove.addTileMarker(new WorldPoint(2327, 9399, 0), SpriteID.BARBARIAN_ASSAULT_HORN_FOR_HEALER_ICON);
		leaveDungeon = new ObjectStep(this, ObjectID.STAIRS_25429, new WorldPoint(2355, 9396, 0),
			"Climb the stairs back to the surface.");
		giveProfessorMould = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor the lens mould. If you don't have it, check your bank.", mould);
		giveProfessorMould.addDialogSteps("Talk about the Observatory quest.");
		useGlassOnMould = new DetailedQuestStep(this, "Use the molten glass on the mould.", moltenGlass.highlighted()
			, mould.highlighted());
		giveProfessorLensAndMould = new NpcStep(this, NpcID.OBSERVATORY_PROFESSOR, new WorldPoint(2442, 3186, 0),
			"Give the professor the lens mould and lens.", lens);
		giveProfessorLensAndMould.addDialogSteps("Talk about the Observatory quest.");
		enterDungeonAgain = new ObjectStep(this, ObjectID.STAIRS_25432, new WorldPoint(2458, 3186, 0),
			"Enter the dungeon east of the Professor.");
		enterObservatory = new ObjectStep(this, ObjectID.STAIRS_25429, new WorldPoint(2335, 9352, 0),
			"Follow the dungeon around anti-clockwise to a staircase, then climb it.");
		goToF2Observatory = new ObjectStep(this, ObjectID.STAIRS_25431, new WorldPoint(2444, 3160, 0),
			"Climb up the stairs in the observatory.");
		viewTelescope = new ObjectStep(this, NullObjectID.NULL_25591, new WorldPoint(2441, 3162, 1),
			"Use the telescope.");
		tellProfessorConstellation = new StarSignAnswer(this);
		tellProfessorConstellation.addDialogSteps("Talk about the Observatory quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(plank.quantity(3), bronzeBar, moltenGlass);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(duelingRing, antipoison, food);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Goblin Guard (level 42, or you can lure it/have someone else kill it)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.CRAFTING, 2250));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("A reward depending on which constellation you observed."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Helping the Professor",
			Arrays.asList(talkToProfessor, giveProfessorPlanks, giveProfessorBar, giveProfessorGlass,
				enterDungeon, searchChests, prodGuard, inspectStove, leaveDungeon, giveProfessorMould, useGlassOnMould,
				giveProfessorLensAndMould, enterDungeonAgain, enterObservatory, goToF2Observatory, viewTelescope,
				tellProfessorConstellation), plank.quantity(3), bronzeBar, moltenGlass));


		return allSteps;
	}
}
