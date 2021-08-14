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
package com.questhelper.achievementdiaries.lumbridgeanddraynor;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.LUMBRIDGE_EASY
)
public class lumbEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear;

	// Items recommended
	ItemRequirement food;

	// Quests required
	Requirement runeMysteries, cooksAssistant;

	Requirement notDrayAgi, notKillCavebug, notSedridor, notWaterRune, notHans, notPickpocket, notOak, notKillZombie,
	notFishAnchovies, notBread, notIron, notEnterHAM;

	QuestStep claimReward, drayAgi, killCavebug, moveToDarkHole, sedridor, moveToSed, enterWaterAltar, waterRune, hans,
		pickpocket, oakChopandBurn, moveToDraySewer, fishAnchovies, bread, mineIron, enterHAM;

	NpcStep killZombie;

	Zone cave, sewer, water, mageTower;

	ZoneRequirement inCave, inSewer, inWater, inMageTower;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);
		doEasy.addStep(notDrayAgi, drayAgi);
		doEasy.addStep(new Conditions(notKillZombie, inSewer), killZombie);
		doEasy.addStep(notKillZombie, moveToDraySewer);

		return doEasy;
	}

	public void setupRequirements()
	{
		notDrayAgi = new VarplayerRequirement(1194, false, 1);
		notKillCavebug = new VarplayerRequirement(1194, false, 2);
		notSedridor = new VarplayerRequirement(1194, false, 3);
		notWaterRune = new VarplayerRequirement(1194, false, 4);
		notHans = new VarplayerRequirement(1194, false, 5);
		notPickpocket = new VarplayerRequirement(1194, false, 6);
		notOak = new VarplayerRequirement(1194, false, 7);
		notKillZombie = new VarplayerRequirement(1194, false, 8);
		notFishAnchovies = new VarplayerRequirement(1194, false, 9);
		notBread = new VarplayerRequirement(1194, false, 10);
		notIron = new VarplayerRequirement(1194, false, 11);
		notEnterHAM = new VarplayerRequirement(1194, false, 12);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.getGoodEatingFood(), -1);

		inCave = new ZoneRequirement(cave);
		inSewer = new ZoneRequirement(sewer);
		inMageTower = new ZoneRequirement(mageTower);

		runeMysteries = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
		cooksAssistant = new QuestRequirement(QuestHelperQuest.COOKS_ASSISTANT, QuestState.FINISHED);
	}

	public void loadZones()
	{
		cave = new Zone(new WorldPoint(3140, 9602, 0), new WorldPoint(3261,9537, 0));
		sewer = new Zone(new WorldPoint(3077, 9699, 0), new WorldPoint(3132,9641, 0));
		mageTower = new Zone(new WorldPoint(3095, 9578, 0), new WorldPoint(3122,9554, 0));
	}

	public void setupSteps()
	{
		drayAgi = new ObjectStep(this, 11404, new WorldPoint(3103, 3279, 0),
			"Complete a lap of the Draynor Rooftop Course.");

		moveToDraySewer = new ObjectStep(this, ObjectID.TRAPDOOR_6435, new WorldPoint(3118, 3244, 0),
		"Climb down into the Draynor Sewer.");
		((ObjectStep) moveToDraySewer).addAlternateObjects(ObjectID.TRAPDOOR_6434);
		killZombie = new NpcStep(this, NpcID.ZOMBIE_38, new WorldPoint(3123, 9648, 0),
			"Kill a zombie.");
		killZombie.addAlternateNpcs(NpcID.ZOMBIE_40, NpcID.ZOMBIE_57, NpcID.ZOMBIE_55, NpcID.ZOMBIE_56);

		claimReward = new NpcStep(this, NpcID.PIRATE_JACKIE_THE_FRUIT, new WorldPoint(2810, 3192, 0),
			"Talk to Pirate Jackie the Fruit in Brimhaven to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 10));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 15));
		reqs.add(new SkillRequirement(Skill.FISHING, 15));
		reqs.add(new SkillRequirement(Skill.MINING, 15));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 5));
		reqs.add(new SkillRequirement(Skill.SLAYER, 7));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 15));

		reqs.add(runeMysteries);
		reqs.add(cooksAssistant);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Zombie (lvl 13) and cave bug (lvl 6)");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails draynorRooftopsSteps = new PanelDetails("Draynor Rooftops", Collections.singletonList( ));
		draynorRooftopsSteps.setDisplayCondition(notDrayAgi);
		allSteps.add(draynorRooftopsSteps);

		PanelDetails zombieSteps = new PanelDetails("Kill Zombie in Draynor Sewers", Collections.singletonList( ));
		zombieSteps.setDisplayCondition(notKillZombie);
		allSteps.add(zombieSteps);



		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
