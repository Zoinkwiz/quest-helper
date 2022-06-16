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
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
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
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

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
	QuestStep putGarlicInPipe, speakToBonzo, speakToBonzoWithFish, speaktoVestri;

	ConditionalStep goToHemensterStep, getWorms, fishNearPipes;

	ItemRequirement noCombatBracelet, noFishingRod, noGarlic, noWorms;
	Requirement hasEverything, notNearWorms, inWoods, notInWoods, enteredContest, hasPutGarlicInPipe;
	VarbitRequirement garlicInPipeVarbit;
	WidgetTextRequirement garlicInPipeScreen, confirmGarlicInPipe;

	// Zones
	Zone mcGruborWoodEntrance, cmbBraceletTeleportZone, nearRedVineWorms, contestGroundsEntrance;
	ZoneRequirement passedThroughMcGruborEntrance, atCmbBraceletTeleportZone, onContestGrounds;

	public void setupZones()
	{
		mcGruborWoodEntrance = new Zone(new WorldPoint(2662, 3500, 0));
		passedThroughMcGruborEntrance = new ZoneRequirement(mcGruborWoodEntrance);

		cmbBraceletTeleportZone = new Zone(new WorldPoint(2651, 3444, 0), new WorldPoint(2657, 3439, 0));
		atCmbBraceletTeleportZone = new ZoneRequirement(cmbBraceletTeleportZone);

		nearRedVineWorms = new Zone(new WorldPoint(2634, 3491, 0), new WorldPoint(2626, 3500, 0));

		// the contest grounds are from where the fence turns north-westa, to the square before the willow tree on the coast
		contestGroundsEntrance = new Zone(new WorldPoint(2642, 3445, 0), new WorldPoint(2631, 3434, 0));
		// the 3 tiles east of the pipe, to ensure we have the whole area
		Zone tilesEastOfPipe = new Zone(new WorldPoint(2641, 3446, 0), new WorldPoint(2638, 3446, 0));
		onContestGrounds = new ZoneRequirement(contestGroundsEntrance, tilesEastOfPipe);
	}

	@Override
	public void setupRequirements()
	{
		coins = new ItemRequirement("Coins", ItemCollections.COINS, 5);
		coins.setTooltip("10 if you buy a fishing rod from Jack");
		fishingPass = new ItemRequirement("Fishing Pass", ItemID.FISHING_PASS);
		fishingPass.setTooltip("<html>This can be obtained during the quest.<br>If you lose this you can get another from Vestri.</html>");
		garlic = new ItemRequirement("Garlic", ItemID.GARLIC);
		garlic.setTooltip("This can be obtained during the quest.");
		garlic.setHighlightInInventory(true);
		fishingRod = new ItemRequirement("Fishing Rod", ItemID.FISHING_ROD);
		fishingRod.setTooltip("This can be obtained during the quest.");
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		redVineWorm = new ItemRequirement("Red Vine Worm", ItemID.RED_VINE_WORM, 1);
		redVineWorm.setTooltip("This can be obtained during the quest.");
		food = new ItemRequirement("Food for low levels", ItemCollections.GOOD_EATING_FOOD, -1);
		winningFish = new ItemRequirement("Raw Giant Carp", ItemID.RAW_GIANT_CARP);
		winningFish.setHighlightInInventory(true);
		trophy = new ItemRequirement("Fishing Trophy", ItemID.FISHING_TROPHY);
		trophy.setHighlightInInventory(true);
		trophy.setTooltip("You can get another from Bonzo in Hemenster if you lost this.");

		// Recommended
		combatBracelet = new ItemRequirement("Combat Bracelet", ItemCollections.COMBAT_BRACELETS);
		combatBracelet.setHighlightInInventory(true);
		combatBracelet.setTooltip("Highly recommended!");
		camelotTeleport = new ItemRequirement("Camelot Teleport", ItemID.CAMELOT_TELEPORT);
		fairyRingAccess = new QuestRequirement(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN, QuestState.IN_PROGRESS,
			"Fairy ring access");
		fairyRingAccess.setTooltip(QuestHelperQuest.FAIRYTALE_II__CURE_A_QUEEN.getName() + " is required to at least be started in order to use fairy rings");
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

		goToMcGruborWood = new ObjectStep(this, ObjectID.LOOSE_RAILING, new WorldPoint(2662, 3500, 0), "", spade);
		goToMcGruborWood.setText("Enter McGrubor's Woods via the northern entrance.\nBe careful of the Guard Dogs (level 44). They are aggressive!");

		goToRedVine = new ObjectStep(this, ObjectID.VINE_2990, new WorldPoint(2631, 3496, 0), "", spade);
		goToRedVine.setText("Use your spade on the red vines to gather 1 Red Vine Worm.");
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

		speakToBonzo = new NpcStep(this, NpcID.BONZO, new WorldPoint(2641, 3437, 0), "Speak to Bonzo to start the competition.", coins);
		speakToBonzo.addDialogStep("I'll enter the competition please.");

		speakToBonzoWithFish = new NpcStep(this, NpcID.BONZO, new WorldPoint(2641, 3437, 0), "", winningFish);
		speakToBonzoWithFish.setText("Speak to Bonzo again after you have caught the winning fish.");
		speakToBonzoWithFish.addDialogStep("I have this big fish. Is it enough to win?");

		NpcStep fishingSpot = new NpcStep(this, NpcID.FISHING_SPOT_4080, new WorldPoint(2637, 3444, 0), "");
		fishingSpot.setText("Fish near the pipes after the Sinister Stranger leaves.");
		fishNearPipes = new ConditionalStep(this, fishingSpot, "Catch the winning fish at the fishing spot near the pipes.", fishingRod, redVineWorm);
		fishNearPipes.addStep(winningFish, speakToBonzoWithFish);

		teleToHemenster = new NpcStep(this, NpcID.GRANDPA_JACK, "", coins);
		teleToHemenster.addText("\nTeleport to Hemenster via the combat bracelet.\n\nSpeak to Grandpa Jack to buy a fishing rod.");
		teleToHemenster.addDialogStep("Ranging Guild");
		teleToHemenster.addDialogStep("Can I buy one of your fishing rods?");
		teleToHemenster.addDialogStep("Very fair, I'll buy that rod!");

		runToJack = new NpcStep(this, NpcID.GRANDPA_JACK, new WorldPoint(2649, 3451, 0), "", coins);
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
		noCombatBracelet = new ItemRequirements(LogicType.NOR, "", combatBracelet);
		noFishingRod = new ItemRequirements(LogicType.NOR, "", fishingRod);

		noGarlic = new ItemRequirements(LogicType.NOR, "", garlic);
		noWorms = new ItemRequirements(LogicType.NOR, "", redVineWorm);

		// Conditions
		hasEverything = new Conditions(garlic, redVineWorm, fishingRod, fishingPass);
		notNearWorms = new Conditions(LogicType.NOR, new ZoneRequirement(nearRedVineWorms));
		inWoods = new Conditions(true, passedThroughMcGruborEntrance); // passed through northern entrance
		notInWoods = new Conditions(LogicType.NOR, inWoods);

		garlicInPipeVarbit = new VarbitRequirement(2054, 1);
		enteredContest = new Conditions(true, LogicType.AND, hasEverything, onContestGrounds);
		garlicInPipeScreen = new WidgetTextRequirement(WidgetInfo.DIALOG_SPRITE_TEXT, "You stash the garlic in the pipe.");
		confirmGarlicInPipe = new WidgetTextRequirement(WidgetInfo.DIALOG_PLAYER_TEXT, "I shoved some garlic up here.");
		hasPutGarlicInPipe = new Conditions(true, LogicType.OR, garlicInPipeVarbit, garlicInPipeScreen, confirmGarlicInPipe);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		goToHemensterStep.addStep(hasPutGarlicInPipe, speakToBonzo); // var changes to 3
		goToHemensterStep.addStep(enteredContest, putGarlicInPipe); // enteredContest already checks for if the player has garlic
		goToHemensterStep.addStep(noGarlic, getGarlic);
		goToHemensterStep.addStep(noWorms, getWorms);
		goToHemensterStep.addStep(fishingRod, goToHemenster);
		goToHemensterStep.addStep(new Conditions(noCombatBracelet, redVineWorm, noFishingRod), runToJack);
		goToHemensterStep.addStep(new Conditions(combatBracelet, redVineWorm, noFishingRod), teleToHemenster);
		goToHemensterStep.addStep(new Conditions(noFishingRod, redVineWorm), grandpaJack);

		steps.put(0, talkToVestriStep);
		steps.put(1, goToHemensterStep);
		steps.put(2, goToHemensterStep);
		steps.put(3, fishNearPipes);
		steps.put(4, speaktoVestri);
		return steps;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.FISHING, 2437));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the underground White Wolf Mountain passage"),
				new UnlockReward("Ability to catch minnows in The Fishing Guild."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> panels = new ArrayList<>();
		List<QuestStep> steps = Arrays.asList(talkToVestriStep, goToHemensterStep, fishNearPipes, speaktoVestri);
		PanelDetails fisingContest = new PanelDetails("Fishing Contest", steps, fishingRod, garlic, coins, redVineWorm, spade);
		panels.add(fisingContest);
		return panels;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return Collections.singletonList(new SkillRequirement(Skill.FISHING, 10));
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins, redVineWorm, garlic, spade, fishingRod);
	}


	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatBracelet, camelotTeleport);
	}
}
