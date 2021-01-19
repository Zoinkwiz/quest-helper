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
import com.questhelper.banktab.BankSlotIcons;
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
import com.questhelper.steps.TileStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.WidgetTextCondition;
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
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.FishingSpot;

@QuestDescriptor(
	quest = QuestHelperQuest.FISHING_CONTEST
)
public class FishingContest extends BasicQuestHelper
{
	// Required
	ItemRequirement coins, fishingPass, garlic, fishingRod, spade, redVineWorm, winningFish, trophy;

	// Recommended
	ItemRequirement combatBracelet, camelotTeleport, food;
	QuestRequirement fairyRingAccess;

	// Steps
	QuestStep talkToVestriStep, getGarlic, goToMcGruborWood, goToRedVine, grandpaJack, runToJack, goToHemenster, teleToHemenster;
	QuestStep putGarlicInPipe, speakToBonzo, fishNearPipes, speakToBonzoWithFish, speaktoVestri;

	ConditionalStep goToHemensterStep, getWorms;

	ItemRequirementCondition hasCombatBracelet, noCombatBracelet, noFishingRod, hasFishingRod, hasGarlic, noGarlic, hasWorms, noWorms, hasFishingPass;
	Conditions hasEverything, notNearWorms, inWoods, notInWoods, enteredContest, hasPutGarlicInPipe;
	WidgetTextCondition garlicInPipeScreen, confirmGarlicInPipe;

	// Zones
	Zone mcGruborWoodEntrance, cmbBraceletTeleportZone, nearRedVineWorms, contestGroundsEntrance;
	ZoneCondition passedThroughMcGruborEntrance, atCmbBraceletTeleportZone, onContestGrounds;

	public void setupZones()
	{
		mcGruborWoodEntrance = new Zone(new WorldPoint(2662, 3500, 0));
		passedThroughMcGruborEntrance = new ZoneCondition(mcGruborWoodEntrance);

		cmbBraceletTeleportZone = new Zone(new WorldPoint(2651, 3444, 0), new WorldPoint(2657, 3439, 0));
		atCmbBraceletTeleportZone = new ZoneCondition(cmbBraceletTeleportZone);

		nearRedVineWorms = new Zone(new WorldPoint(2634, 3491, 0), new WorldPoint(2626, 3500, 0));

		contestGroundsEntrance = new Zone(new WorldPoint(2642, 3442, 0), new WorldPoint(2642, 3441, 0));
		onContestGrounds = new ZoneCondition(contestGroundsEntrance);
	}

	public void setupItemRequirements()
	{
		coins = new ItemRequirement("Coins", ItemID.COINS_995, 5);
		coins.setTip("10 if you buy a fishing rod from Jack");
		fishingPass = new ItemRequirement("Fishing Pass", ItemID.FISHING_PASS);
		fishingPass.setTip("This can be obtained during the quest.");
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC);
		garlic.setTip("This can be obtained during the quest.");
		garlic.setHighlightInInventory(true);
		fishingRod = new ItemRequirement("Fishing Rod", ItemID.FISHING_ROD);
		fishingRod.setTip("This can be obtained during the quest.");
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		redVineWorm = new ItemRequirement("Red Vine Worm", ItemID.RED_VINE_WORM, 1);
		food = new ItemRequirement("Food for low levels", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
		winningFish = new ItemRequirement("Raw Giant Carp", ItemID.RAW_GIANT_CARP);
		winningFish.setHighlightInInventory(true);
		trophy = new ItemRequirement("Fishing Trophy", ItemID.FISHING_TROPHY);
		trophy.setHighlightInInventory(true);

		// Recommended
		combatBracelet = new ItemRequirement("Combat Bracelet", ItemCollections.getCombatBracelets());
		combatBracelet.setHighlightInInventory(true);
		camelotTeleport = new ItemRequirement("Camelot Teleport", ItemID.CAMELOT_TELEPORT);
		fairyRingAccess = new QuestRequirement(Quest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.IN_PROGRESS, "Fairy ring access");
		fairyRingAccess.setTip(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN.getName() + " is required to at least be started in order to use fairy rings");
	}

	public void setupSteps()
	{
		talkToVestriStep = new NpcStep(this, NpcID.VESTRI, new WorldPoint(2821, 3486, 0), "Talk to Vestri just north of Catherby.");
		talkToVestriStep.addDialogStep("I was wondering what was down those stairs?");
		talkToVestriStep.addDialogStep("Why not?");
		talkToVestriStep.addDialogStep("If you were my friend I wouldn't mind it.");
		talkToVestriStep.addDialogStep("Well, let's be friends!");
		talkToVestriStep.addDialogStep("And how am I meant to do that?");

		getGarlic = new ObjectStep(this, ObjectID.TABLE_25930, new WorldPoint(2714, 3478, 0), "");
		getGarlic.setText("Pick the garlic up on the table in Seers' Village.\nIf it is not there it spawns about every 30 seconds.");

		goToMcGruborWood = new ObjectStep(this, ObjectID.LOOSE_RAILING, new WorldPoint(2662, 3500, 0), "Enter McGrubor's Woods via the northern entrance.");

		goToRedVine = new ObjectStep(this, ObjectID.VINE_2990, new WorldPoint(2631, 3496, 0), "");
		goToRedVine.setText("Use your spade on the red vines to gather 1 Red Vine Worm");
		goToRedVine.addIcon(ItemID.SPADE);
		((ObjectStep)goToRedVine).addAlternateObjects(ObjectID.VINE_2013, ObjectID.VINE_2991, ObjectID.VINE_2992, ObjectID.VINE_2994);
		((ObjectStep)goToRedVine).addAlternateObjects(ObjectID.VINE, ObjectID.VINE_2993, ObjectID.VINE_2989);

		goToHemenster = new ObjectStep(this, ObjectID.GATE_48, new WorldPoint(2642, 3441, 0), "Enter Hemenster with your fishing pass.");
		((ObjectStep)goToHemenster).addAlternateObjects(ObjectID.GATE_47);
		grandpaJack = new NpcStep(this, NpcID.GRANDPA_JACK, new WorldPoint(2649, 3451, 0), "Talk to Grandpa Jack to get a fishing rod.");
		grandpaJack.addDialogStep("Can I buy one of your fishing rods?");
		grandpaJack.addDialogStep("Very fair, I'll buy that rod!");

		putGarlicInPipe = new ObjectStep(this, ObjectID.WALL_PIPE, new WorldPoint(2638, 3446, 0), "Put garlic in the pipes.", garlic);
		putGarlicInPipe.addIcon(ItemID.GARLIC);

		speakToBonzo = new NpcStep(this, NpcID.BONZO, new WorldPoint(2641, 3437, 0), "Speak to Bonzo to start the competition.");
		speakToBonzo.addDialogStep("I'll enter the competition please.");

		fishNearPipes = new NpcStep(this, NpcID.FISHING_SPOT_4080, new WorldPoint(2637, 3444, 0), "");
		fishNearPipes.setText("Fish near the pipes after the Sinister Stranger leaves.");

		speakToBonzoWithFish = new NpcStep(this, NpcID.BONZO, new WorldPoint(2641, 3437, 0), "", winningFish);
		speakToBonzoWithFish.setText("Speak to Bonzo again after you have caught the winning fish.");

		teleToHemenster = new NpcStep(this, NpcID.GRANDPA_JACK, "");
		teleToHemenster.addText("\nTeleport to Hemenster via the combat bracelet.\n\nSpeak to Grandpa Jack to buy a fishing rod.");
		teleToHemenster.addDialogStep("Ranging Guild");
		teleToHemenster.addDialogStep("Can I buy one of your fishing rods?");
		teleToHemenster.addDialogStep("Very fair, I'll buy that rod!");

		runToJack = new NpcStep(this, NpcID.GRANDPA_JACK, new WorldPoint(2649, 3451, 0), "");
		runToJack.setText("Speak to Grandpa Jack to get a fishing rod.\nYou can leave McGrubor's Woods via the northern entrance.");
		runToJack.addDialogStep("Can I buy one of your fishing rods?");
		runToJack.addDialogStep("Very fair, I'll buy that rod!");

		speaktoVestri = new NpcStep(this, NpcID.VESTRI, new WorldPoint(2821, 3486, 0), "Talk to Vestri just north of Catherby.", trophy);

		goToHemensterStep = new ConditionalStep(this, goToHemenster, "Enter Hemenster with your fishing pass.");
		goToHemensterStep.addDialogStep("Ranging Guild");

		getWorms = new ConditionalStep(this, goToRedVine, "Gather 1 Red Vine Worm in McGrubor's Woods.");
		getWorms.addStep(new Conditions(noWorms, notNearWorms, notInWoods), goToMcGruborWood);
		getWorms.addStep(inWoods, goToRedVine);
	}

	public void setupConditions()
	{
		hasCombatBracelet = new ItemRequirementCondition(combatBracelet);
		noCombatBracelet = new ItemRequirementCondition(LogicType.NOR, combatBracelet);
		noFishingRod = new ItemRequirementCondition(LogicType.NOR, fishingRod);
		hasFishingRod = new ItemRequirementCondition(fishingRod);
		hasGarlic = new ItemRequirementCondition(garlic);
		noGarlic = new ItemRequirementCondition(LogicType.NOR, garlic);
		hasWorms = new ItemRequirementCondition(redVineWorm);
		noWorms = new ItemRequirementCondition(LogicType.NOR, redVineWorm);
		hasFishingPass = new ItemRequirementCondition(fishingPass);

		// Conditions
		hasEverything = new Conditions(hasGarlic, hasWorms, hasFishingRod, hasFishingPass);
		notNearWorms = new Conditions(LogicType.NOR, new ZoneCondition(nearRedVineWorms));
		inWoods = new Conditions(true, passedThroughMcGruborEntrance); // passed through northern entrance
		notInWoods = new Conditions(LogicType.NOR, inWoods);

		enteredContest = new Conditions(true, LogicType.AND, hasEverything, onContestGrounds);
		garlicInPipeScreen = new WidgetTextCondition(WidgetInfo.DIALOG_SPRITE_TEXT, "You stash the garlic in the pipe.");
		confirmGarlicInPipe = new WidgetTextCondition(217, 4, "I shoved some garlic up here.");
		hasPutGarlicInPipe = new Conditions(true, garlicInPipeScreen, confirmGarlicInPipe);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupItemRequirements();
		setupSteps();
		setupConditions();

		Map<Integer, QuestStep> steps = new HashMap<>();

		goToHemensterStep.addStep(hasPutGarlicInPipe, speakToBonzo); // var changes to 3
		goToHemensterStep.addStep(new Conditions(enteredContest, hasGarlic, new Conditions(LogicType.NOR, hasPutGarlicInPipe)), putGarlicInPipe);
		goToHemensterStep.addStep(noGarlic, getGarlic);
		goToHemensterStep.addStep(noWorms, getWorms);
		goToHemensterStep.addStep(hasFishingRod, goToHemenster);
		goToHemensterStep.addStep(new Conditions(noCombatBracelet, hasWorms, noFishingRod), runToJack);
		goToHemensterStep.addStep(new Conditions(hasCombatBracelet, hasWorms, noFishingRod), teleToHemenster);
		goToHemensterStep.addStep(new Conditions(noFishingRod, hasWorms), grandpaJack);

		steps.put(0, talkToVestriStep);
		steps.put(1, goToHemensterStep);
		steps.put(3, fishNearPipes);
		steps.put(4, speaktoVestri);
		return steps;
	}


	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> panels = new ArrayList<>();
		ArrayList<QuestStep> steps = new ArrayList<>(Arrays.asList(talkToVestriStep, goToHemensterStep, fishNearPipes, speaktoVestri));
		PanelDetails fisingContest = new PanelDetails("Fishing Contest", steps, fishingPass, fishingRod, garlic, coins, redVineWorm, spade);
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
		return new ArrayList<>(Arrays.asList(coins, redVineWorm, garlic, spade, fishingRod, fishingPass));
	}


	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(combatBracelet, camelotTeleport));
	}
}
