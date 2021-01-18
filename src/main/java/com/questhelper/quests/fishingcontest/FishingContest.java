/*
 *
 *  * Copyright (c) 2021, Senmori
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.questhelper.quests.fishingcontest;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.SkillRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.FISHING_CONTEST
)
public class FishingContest extends BasicQuestHelper
{
	// Required
	ItemRequirement coins, fishingPass, garlic, fishingRod, spade, redVineWorm;

	// Recommended
	ItemRequirement combatBracelet, gamesNecklace, food;
	QuestRequirement fairyRingAccess;

	// Steps
	QuestStep talkToVestriToStart, getGarlic, vestriStep, goToMcGruborWood, goToRedVine, postVestriCollectionStep,
		hemensterStep, goToHemenster, grandpaJack;

	// Zones
	Zone mcGruborWoodEntrance, cmbBraceletTeleportZone, nearRedVineWorms;
	ZoneCondition passedThroughMcGruborEntrance, atCmbBraceletTeleportZone;

	public void setupZones()
	{
		mcGruborWoodEntrance = new Zone(new WorldPoint(2662, 3500, 0));
		passedThroughMcGruborEntrance = new ZoneCondition(mcGruborWoodEntrance);
		cmbBraceletTeleportZone = new Zone(new WorldPoint(2651, 3444, 0), new WorldPoint(2657, 3439, 0));
		atCmbBraceletTeleportZone = new ZoneCondition(cmbBraceletTeleportZone);
		nearRedVineWorms = new Zone(new WorldPoint(2634, 3491, 0), new WorldPoint(2626, 3500, 0));
	}

	public void setupItemRequirements()
	{
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 5);
		coins.setTip("10 if you buy a fishing rod from Jack");
		fishingPass = new ItemRequirement("Fishing Pass", ItemID.FISHING_PASS);
		fishingPass.setTip("This can be obtained during the quest.");
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC);
		garlic.setTip("This can be obtained during the quest.");
		fishingRod = new ItemRequirement("Fishing Rod", ItemID.FISHING_ROD);
		fishingRod.setTip("This can be obtained during the quest.");
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		redVineWorm = new ItemRequirement("Red Vine Worm", ItemID.RED_VINE_WORM);
		food = new ItemRequirement("Food for low levels", -1, 0);

		// Recommended
		combatBracelet = new ItemRequirement("Combat Bracelet", ItemCollections.getCombatBracelets());
		gamesNecklace = new ItemRequirement("Camelot Teleport", ItemID.CAMELOT_TELEPORT);
		fairyRingAccess = new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.IN_PROGRESS, "Fairy ring access");
		fairyRingAccess.setTip(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN.getName() + " is required to at least be started in order to use fairy rings");
	}

	public void setupSteps()
	{
		talkToVestriToStart = new NpcStep(this, NpcID.VESTRI, new WorldPoint(2821, 3486, 0), "Talk to Vestri just north of Catherby.");
		talkToVestriToStart.addDialogStep("I was wondering what was down that tunnel?");
		talkToVestriToStart.addDialogStep("Why not?");
		talkToVestriToStart.addDialogStep("If you were my friend I wouldn't mind it.");
		talkToVestriToStart.addDialogStep("Well, let's be friends!");
		talkToVestriToStart.addDialogStep("And how am I meant to do that?");

		getGarlic = new ObjectStep(this, ObjectID.TABLE_25930, new WorldPoint(2714, 3478, 0), "");
		getGarlic.setText("Pick the garlic up on the table in Seers' Village.\nIf it is not there it spawns about every 30 seconds.");

		goToMcGruborWood = new ObjectStep(this, ObjectID.LOOSE_RAILING, new WorldPoint(2662, 3500, 0), "Enter McGrubor's Woods via the northern entrance.");
		goToRedVine = new ObjectStep(this, ObjectID.VINE_2990, new WorldPoint(2631, 3496, 0), "");
		goToRedVine.setText("Use your spade on the red vines to gather 1 Red Vine Worm");
		goToRedVine.addIcon(ItemID.SPADE);
		((ObjectStep)goToRedVine).addAlternateObjects(ObjectID.VINE_2013, ObjectID.VINE_2991, ObjectID.VINE_2992, ObjectID.VINE_2994);
		((ObjectStep)goToRedVine).addAlternateObjects(ObjectID.VINE, ObjectID.VINE_2993, ObjectID.VINE_2989);

		ItemRequirementCondition hasRedVineWorm = new ItemRequirementCondition(redVineWorm);

		goToHemenster = new ObjectStep(this, ObjectID.GATE_48, new WorldPoint(2642, 3441, 0), "Enter Hemenster with your fishing pass.");
		((ObjectStep)goToHemenster).addAlternateObjects(ObjectID.GATE_47);
		grandpaJack = new NpcStep(this, NpcID.GRANDPA_JACK, new WorldPoint(2649, 3451, 0), "Talk to Grandpa Jack to get a fishing rod.");
		grandpaJack.addDialogStep("Can I buy one of your fishing rods?");
		grandpaJack.addDialogStep("Very fair, I'll buy that rod!");

		postVestriCollectionStep = new ConditionalStep(this, goToHemenster, "Gather Red Vine Worms in McGrubor's Woods.");
		postVestriCollectionStep.addDialogStep("Ranging Guild");
		Conditions inWoods = new Conditions(true, passedThroughMcGruborEntrance);
		Conditions notInWoods = new Conditions(LogicType.NOR, passedThroughMcGruborEntrance);
		ItemRequirementCondition noGarlic = new ItemRequirementCondition(LogicType.NOR, garlic);
		ZoneCondition isNearWorms = new ZoneCondition(nearRedVineWorms);

		ItemRequirementCondition noFishingRod = new ItemRequirementCondition(LogicType.NOR, fishingRod);
		Conditions didTeleport = new Conditions(true, atCmbBraceletTeleportZone);
		ItemRequirementCondition hasGarlic = new ItemRequirementCondition(garlic);
		ItemRequirementCondition hasWorms = new ItemRequirementCondition(redVineWorm);
		Conditions hasEverythingTele = new Conditions(LogicType.AND, hasGarlic, hasWorms, noFishingRod, didTeleport);
		Conditions needsFishingRod = new Conditions(LogicType.AND, hasGarlic, hasWorms, noFishingRod);

		Conditions hasWormNoTele = new Conditions(LogicType.AND, hasRedVineWorm, new ItemRequirementCondition(LogicType.NOR, combatBracelet));

		// if the user has everything and can teleport, teleport to hemenseter and talk to grandpa jack
		((ConditionalStep)postVestriCollectionStep).addStep(hasEverythingTele, goToHemenster);
		// if the only thing the player needs is the fishing rod, talk to jack
		((ConditionalStep)postVestriCollectionStep).addStep(needsFishingRod, grandpaJack);
		((ConditionalStep)postVestriCollectionStep).addStep(noGarlic, getGarlic);
		// player doesn't have teleport out of the woods, lead back to entrance
		((ConditionalStep)postVestriCollectionStep).addStep(hasWormNoTele, goToMcGruborWood);
		((ConditionalStep) postVestriCollectionStep).addStep(hasRedVineWorm, goToHemenster);
		((ConditionalStep) postVestriCollectionStep).addStep(isNearWorms, goToRedVine);
		((ConditionalStep) postVestriCollectionStep).addStep(inWoods, goToRedVine);
		((ConditionalStep) postVestriCollectionStep).addStep(notInWoods, goToMcGruborWood);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupItemRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, postVestriCollectionStep);

		return steps;
	}


	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> panels = new ArrayList<>();
		ArrayList<QuestStep> steps = new ArrayList<>();
		steps.addAll(Arrays.asList(postVestriCollectionStep));
		PanelDetails fisingContest = new PanelDetails("Fishing Contest", steps, fishingPass, fishingRod, garlic, coins, spade);
		panels.add(fisingContest);
		return panels;
	}

	@Override
	public ArrayList<Requirement> getGeneralRequirements()
	{
		return new ArrayList<>(Collections.singletonList(new SkillRequirement(Skill.FISHING, 10)));
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(coins, garlic, spade, fishingRod, fishingPass));
	}


	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(combatBracelet, gamesNecklace));
	}

	@Override
	public ArrayList<Requirement> getGeneralRecommended()
	{
		return new ArrayList<>(Arrays.asList(fairyRingAccess));
	}
}
