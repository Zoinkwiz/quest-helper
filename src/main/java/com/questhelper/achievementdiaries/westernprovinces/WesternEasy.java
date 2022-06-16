/*
 * Copyright (c) 2022, Obasill <https://github.com/Obasill>
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
package com.questhelper.achievementdiaries.westernprovinces;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.UnlockReward;
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
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.WESTERN_EASY
)
public class WesternEasy extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement combatGear, birdSnare, pickaxe, oakShortU, bowString, ogreBellows, ogreBow, ogreArrows, swampToad;

	// Items recommended
	ItemRequirement food, fairyAccess;

	// Quests required
	Requirement bigChompy, runeMysteries;

	Requirement notCopperLongtail, notNovicePest, notMineIron, notGnomeAgi, notGnomeBall, notChompyHat,
		notTPPest, notSwampToadCollect, notBrimstailEssence, notOakShortbow, notTerrorbird;

	QuestStep copperLongtail, novicePest, gnomeAgi, gnomeBall, chompyHat, tpPest, swampToadCollect, brimstailEssence,
		oakShortbow, claimReward, moveToBrim, moveToPest, moveToStronghold;

	ObjectStep mineIron;

	NpcStep terrorbird;

	Zone brimstailCave, stronghold, pest;

	ZoneRequirement inBrimstailCave, inStronghold, inPest;

	ConditionalStep copperLongtailTask, novicePestTask, gnomeAgiTask, gnomeBallTask, chompyHatTask, tpPestTask,
		swampToadCollectTask, brimstailEssenceTask, oakShortbowTask, terrorbirdTask, mineIronTask;

	@Override
	public QuestStep loadStep()
	{
		loadZones();
		setupRequirements();
		setupSteps();

		ConditionalStep doEasy = new ConditionalStep(this, claimReward);

		gnomeAgiTask = new ConditionalStep(this, gnomeAgi);
		doEasy.addStep(notGnomeAgi, gnomeAgiTask);

		oakShortbowTask = new ConditionalStep(this, moveToStronghold);
		oakShortbowTask.addStep(inStronghold, oakShortbow);
		doEasy.addStep(notOakShortbow, oakShortbowTask);

		brimstailEssenceTask = new ConditionalStep(this, moveToBrim);
		brimstailEssenceTask.addStep(inBrimstailCave, brimstailEssence);
		doEasy.addStep(notBrimstailEssence, brimstailEssenceTask);

		terrorbirdTask = new ConditionalStep(this, terrorbird);
		doEasy.addStep(notTerrorbird, terrorbirdTask);

		gnomeBallTask = new ConditionalStep(this, gnomeBall);
		doEasy.addStep(notGnomeBall, gnomeBallTask);

		swampToadCollectTask = new ConditionalStep(this, swampToadCollect);
		doEasy.addStep(notSwampToadCollect, swampToadCollectTask);

		copperLongtailTask = new ConditionalStep(this, copperLongtail);
		doEasy.addStep(notCopperLongtail, copperLongtailTask);

		mineIronTask = new ConditionalStep(this, mineIron);
		doEasy.addStep(notMineIron, mineIronTask);

		tpPestTask = new ConditionalStep(this, tpPest);
		doEasy.addStep(notTPPest, tpPestTask);

		novicePestTask = new ConditionalStep(this, moveToPest);
		novicePestTask.addStep(inPest, novicePest);
		doEasy.addStep(notNovicePest, novicePestTask);

		chompyHatTask = new ConditionalStep(this, chompyHat);
		doEasy.addStep(notChompyHat, chompyHatTask);

		return doEasy;
	}

	@Override
	public void setupRequirements()
	{
		notCopperLongtail = new VarplayerRequirement(1182, false, 1);
		notNovicePest = new VarplayerRequirement(1182, false, 2);
		notMineIron = new VarplayerRequirement(1182, false, 3);
		notGnomeAgi = new VarplayerRequirement(1182, false, 4);
		notGnomeBall = new VarplayerRequirement(1182, false, 5);
		notChompyHat = new VarplayerRequirement(1182, false, 6);
		notTPPest = new VarplayerRequirement(1182, false, 7);
		notSwampToadCollect = new VarplayerRequirement(1182, false, 8);
		notBrimstailEssence = new VarplayerRequirement(1182, false, 9);
		notOakShortbow = new VarplayerRequirement(1182, false, 10);
		notTerrorbird = new VarplayerRequirement(1182, false, 11);

		//todo find way to track chompy kills

		birdSnare = new ItemRequirement("Bird snare", ItemID.BIRD_SNARE).showConditioned(notCopperLongtail);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES).showConditioned(notMineIron);
		oakShortU = new ItemRequirement("Oak shortbow (u)", ItemID.OAK_SHORTBOW_U).showConditioned(notOakShortbow);
		bowString = new ItemRequirement("Bow string", ItemID.BOW_STRING).showConditioned(notOakShortbow);
		ogreBellows = new ItemRequirement("Ogre bellows", ItemCollections.OGRE_BELLOWS).showConditioned(notChompyHat);
		ogreBow = new ItemRequirement("Ogre bow", ItemCollections.OGRE_BOW).showConditioned(notChompyHat);
		ogreArrows = new ItemRequirement("Ogre / brutal arrows", ItemCollections.OGRE_BRUTAL_ARROWS).showConditioned(notChompyHat);
		swampToad = new ItemRequirement("Swamp toad", ItemID.SWAMP_TOAD);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);
		fairyAccess = new ItemRequirement("Dramen or Lunar staff", ItemCollections.FAIRY_STAFF);

		inBrimstailCave = new ZoneRequirement(brimstailCave);
		inPest = new ZoneRequirement(pest);
		inStronghold = new ZoneRequirement(stronghold);

		bigChompy = new QuestRequirement(QuestHelperQuest.BIG_CHOMPY_BIRD_HUNTING, QuestState.FINISHED);
		runeMysteries = new QuestRequirement(QuestHelperQuest.RUNE_MYSTERIES, QuestState.FINISHED);
	}

	public void loadZones()
	{
		stronghold = new Zone(new WorldPoint(2376, 3523, 0), new WorldPoint(2498, 3391, 0));
		pest = new Zone(new WorldPoint(2631, 2681, 0), new WorldPoint(2683, 2626, 0));
		brimstailCave = new Zone(new WorldPoint(2376, 9835, 0), new WorldPoint(2416, 9802, 0));
	}

	public void setupSteps()
	{
		gnomeAgi = new ObjectStep(this, ObjectID.LOG_BALANCE_23145, new WorldPoint(2474, 3435, 0),
			"Complete a lap of the Gnome Agility Course.");

		moveToStronghold = new DetailedQuestStep(this, new WorldPoint(2450, 3433, 0),
			"Enter the Gnome Stronghold.");
		oakShortbow = new DetailedQuestStep(this, "Fletch an oak shortbow in the Gnome Stronghold.",
			oakShortU.highlighted(), bowString.highlighted());

		moveToBrim = new ObjectStep(this, ObjectID.CAVE_ENTRANCE_17209, new WorldPoint(2403, 3419, 0),
			"Enter Brimstail's cave.");
		brimstailEssence = new NpcStep(this, NpcID.BRIMSTAIL, new WorldPoint(2409, 9817, 0),
			"Have Brimstail teleport you to the Essence mine");
		brimstailEssence.addDialogStep("I need to mine some rune essence.");

		terrorbird = new NpcStep(this, NpcID.TERRORBIRD, new WorldPoint(2379, 3432, 0),
			"Kill a terrorbird in the terrorbird enclosure.", true, combatGear);
		terrorbird.addAlternateNpcs(NpcID.TERRORBIRD_2065, NpcID.TERRORBIRD_2066);

		gnomeBall = new NpcStep(this, NpcID.GNOME_BALL_REFEREE, new WorldPoint(2385, 3487, 0),
			"Score a goal in a Gnome Ball match. Talk to the Referee to start the match.");
		gnomeBall.addDialogStep("Okay then, I'll have a go.");

		swampToadCollect = new ItemStep(this, new WorldPoint(2422, 3507, 0),
			"Collect a swamp toad at the Gnome Stronghold.", swampToad.highlighted());

		mineIron = new ObjectStep(this, ObjectID.ROCKS_11364, new WorldPoint(2338, 3640, 0),
			"Mine some iron ore near Piscatoris.", true, pickaxe);
		mineIron.addAlternateObjects(ObjectID.ROCKS_11365);

		copperLongtail = new ObjectStep(this, ObjectID.BIRD_SNARE_9379, new WorldPoint(2341, 3598, 0),
			"Catch a copper longtail.", true, birdSnare.highlighted());

		chompyHat = new NpcStep(this, NpcID.RANTZ, new WorldPoint(2628, 2979, 0),
			"Claim any Chompy bird hat from Rantz. Kill chompy birds until you have 30 kills. \n \nYou can check " +
				"your kill count by right-clicking 'Check Kills' on an ogre bow.",
			ogreBow, ogreArrows, ogreBellows);
		chompyHat.addDialogStep("Can I have a hat please?");

		tpPest = new DetailedQuestStep(this, "Teleport to Pest Control using the minigame teleport.");

		moveToPest = new NpcStep(this, NpcID.SQUIRE_1770, new WorldPoint(3041, 3202, 0),
			"Talk to the squire in Port Sarim to travel to the Void Knights' Outpost. Alternatively, use the pest " +
				"control minigame teleport.");
		moveToPest.addDialogStep("I'd like to go to your outpost.");
		novicePest = new ObjectStep(this, ObjectID.GANGPLANK_14315, new WorldPoint(2658, 2639, 0),
			"Complete a novice game of Pest Control.");

		claimReward = new NpcStep(this, NpcID.ELDER_GNOME_CHILD, new WorldPoint(2466, 3460, 0),
			"Talk to the Elder gnome child in Gnome Stronghold to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(birdSnare, pickaxe, oakShortU, bowString, ogreBellows, ogreBow, ogreArrows, combatGear);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(food, fairyAccess);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new CombatLevelRequirement(40));
		reqs.add(new SkillRequirement(Skill.FLETCHING, 20));
		reqs.add(new SkillRequirement(Skill.HUNTER, 9));
		reqs.add(new SkillRequirement(Skill.MINING, 15));

		reqs.add(bigChompy);
		reqs.add(runeMysteries);

		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Terror bird (lvl 28)", "Complete a Novice Pest Control game");
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Western Banner 1", ItemID.WESTERN_BANNER_1),
			new ItemReward("2,500 Exp. Lamp (Any skill over 30)", ItemID.ANTIQUE_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("25% chance of 2 chompy birds appearing when Chompy bird hunting"),
			new UnlockReward("25 free ogre arrows every day from Rantz")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails agiSteps = new PanelDetails("Gnome Agility Course", Collections.singletonList(gnomeAgi));
		agiSteps.setDisplayCondition(notGnomeAgi);
		agiSteps.setLockingStep(gnomeAgiTask);
		allSteps.add(agiSteps);

		PanelDetails shortbowSteps = new PanelDetails("Fletch Oak Shortbow in Gnome Stronghold",
			Arrays.asList(moveToStronghold, oakShortbow), new SkillRequirement(Skill.FLETCHING, 20),
			oakShortU, bowString);
		shortbowSteps.setDisplayCondition(notOakShortbow);
		shortbowSteps.setLockingStep(oakShortbowTask);
		allSteps.add(shortbowSteps);

		PanelDetails essSteps = new PanelDetails("Brimstail Essence Teleport", Arrays.asList(moveToBrim,
			brimstailEssence), runeMysteries);
		essSteps.setDisplayCondition(notBrimstailEssence);
		essSteps.setLockingStep(brimstailEssenceTask);
		allSteps.add(essSteps);

		PanelDetails birdSteps = new PanelDetails("Kill Terrorbird", Collections.singletonList(terrorbird),
			combatGear, food);
		birdSteps.setDisplayCondition(notTerrorbird);
		birdSteps.setLockingStep(terrorbirdTask);
		allSteps.add(birdSteps);

		PanelDetails ballSteps = new PanelDetails("Gnome Ball Goal", Collections.singletonList(gnomeBall));
		ballSteps.setDisplayCondition(notGnomeBall);
		ballSteps.setLockingStep(gnomeBallTask);
		allSteps.add(ballSteps);

		PanelDetails toadSteps = new PanelDetails("Collect Swamp Toad", Collections.singletonList(swampToadCollect));
		toadSteps.setDisplayCondition(notSwampToadCollect);
		toadSteps.setLockingStep(swampToadCollectTask);
		allSteps.add(toadSteps);

		PanelDetails copperSteps = new PanelDetails("Copper Longtail", Collections.singletonList(copperLongtail),
			new SkillRequirement(Skill.HUNTER, 9), birdSnare);
		copperSteps.setDisplayCondition(notCopperLongtail);
		copperSteps.setLockingStep(copperLongtailTask);
		allSteps.add(copperSteps);

		PanelDetails ironSteps = new PanelDetails("Mine Iron", Collections.singletonList(mineIron),
			new SkillRequirement(Skill.MINING, 15), pickaxe);
		ironSteps.setDisplayCondition(notMineIron);
		ironSteps.setLockingStep(mineIronTask);
		allSteps.add(ironSteps);

		PanelDetails tpSteps = new PanelDetails("Teleport to Pest Control", Collections.singletonList(tpPest),
			new CombatLevelRequirement(40));
		tpSteps.setDisplayCondition(notTPPest);
		tpSteps.setLockingStep(tpPestTask);
		allSteps.add(tpSteps);

		PanelDetails pestSteps = new PanelDetails("Novice Pest Control", Arrays.asList(moveToPest,
			novicePest), new CombatLevelRequirement(40), combatGear);
		pestSteps.setDisplayCondition(notNovicePest);
		pestSteps.setLockingStep(novicePestTask);
		allSteps.add(pestSteps);

		PanelDetails hatSteps = new PanelDetails("Chompy Bird Hat", Collections.singletonList(chompyHat),
			new SkillRequirement(Skill.RANGED, 30), bigChompy, ogreBellows, ogreBow, ogreArrows);
		hatSteps.setDisplayCondition(notChompyHat);
		hatSteps.setLockingStep(chompyHatTask);
		allSteps.add(hatSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
