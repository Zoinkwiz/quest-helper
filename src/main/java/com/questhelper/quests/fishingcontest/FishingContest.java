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
	ItemRequirement coins, fishingPass, garlic, fishingRod, spade, redVineWorm, winningFish;

	// Recommended
	ItemRequirement combatBracelet, camelotTeleport, food;
	QuestRequirement fairyRingAccess;

	// Steps
	QuestStep talkToVestriStep, getGarlic, goToMcGruborWood, goToRedVine, goToHemenster, grandpaJack, leaveMcGruborWood;
	QuestStep putGarlicInPipe, speakToBonzo, fishNearPipes, speakToBonzoWithFish, speaktoVestri;
	ConditionalStep collectionStep;

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
		garlic.setHighlightInInventory(true);
		fishingRod = new ItemRequirement("Fishing Rod", ItemID.FISHING_ROD);
		fishingRod.setTip("This can be obtained during the quest.");
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		redVineWorm = new ItemRequirement("Red Vine Worm", ItemID.RED_VINE_WORM, 3);
		food = new ItemRequirement("Food for low levels", -1, -1);
		food.setDisplayItemId(BankSlotIcons.getFood());

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
		leaveMcGruborWood = new ObjectStep(this, ObjectID.LOOSE_RAILING, new WorldPoint(2662, 3500, 0), "Leave McGrubor's Woods via the northern entrance.");

		goToRedVine = new ObjectStep(this, ObjectID.VINE_2990, new WorldPoint(2631, 3496, 0), "");
		goToRedVine.setText("Use your spade on the red vines to gather 3 Red Vine Worms");
		goToRedVine.addIcon(ItemID.SPADE);
		((ObjectStep)goToRedVine).addAlternateObjects(ObjectID.VINE_2013, ObjectID.VINE_2991, ObjectID.VINE_2992, ObjectID.VINE_2994);
		((ObjectStep)goToRedVine).addAlternateObjects(ObjectID.VINE, ObjectID.VINE_2993, ObjectID.VINE_2989);

		goToHemenster = new ObjectStep(this, ObjectID.GATE_48, new WorldPoint(2642, 3441, 0), "Enter Hemenster with your fishing pass.");
		((ObjectStep)goToHemenster).addAlternateObjects(ObjectID.GATE_47);
		grandpaJack = new NpcStep(this, NpcID.GRANDPA_JACK, new WorldPoint(2649, 3451, 0), "Talk to Grandpa Jack to get a fishing rod.");
		grandpaJack.addDialogStep("Can I buy one of your fishing rods?");
		grandpaJack.addDialogStep("Very fair, I'll buy that rod!");

		putGarlicInPipe = new ObjectStep(this, ObjectID.WALL_PIPE,new WorldPoint(2638, 3446, 0), "Put garlic in the pipes", garlic);
		putGarlicInPipe.addIcon(ItemID.GARLIC);

		speakToBonzo = new NpcStep(this, NpcID.BONZO, new WorldPoint(2641, 3437, 0), "Speak to Bonzo to start the competition");

		fishNearPipes = new TileStep(this, new WorldPoint(2637, 3444, 0), "Fish near the pipes after the Sinister Stranger leaves.");

		winningFish = new ItemRequirement("Giant Carp", ItemID.RAW_GIANT_CARP);
		speakToBonzoWithFish = new NpcStep(this, NpcID.BONZO,new WorldPoint(2641, 3437, 0), "", winningFish);
		speakToBonzoWithFish.setText("Speak to Bonzo again after you have caught the winning fish.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupZones();
		setupItemRequirements();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		collectionStep = new ConditionalStep(this, goToHemenster, "Enter Hemenster with your fishing pass.");
		collectionStep.addDialogStep("Ranging Guild");

		ItemRequirementCondition hasCombatBracelet = new ItemRequirementCondition(combatBracelet);
		ItemRequirementCondition noCombatBracelet = new ItemRequirementCondition(LogicType.NOR, combatBracelet);
		ItemRequirementCondition noFishingRod = new ItemRequirementCondition(LogicType.NOR, fishingRod);
		ItemRequirementCondition hasFishingRod = new ItemRequirementCondition(fishingRod);
		ItemRequirementCondition hasGarlic = new ItemRequirementCondition(garlic);
		ItemRequirementCondition noGarlic = new ItemRequirementCondition(LogicType.NOR, garlic);
		ItemRequirementCondition hasWorms = new ItemRequirementCondition(redVineWorm);
		ItemRequirementCondition noWorms = new ItemRequirementCondition(LogicType.NOR, redVineWorm);
		ItemRequirementCondition hasFishingPass = new ItemRequirementCondition(fishingPass);
		Conditions hasEverything = new Conditions(hasGarlic, hasWorms, hasFishingRod, hasFishingPass);

		Conditions notNearWorms = new Conditions(LogicType.NOR, new ZoneCondition(nearRedVineWorms));
		Conditions inWoods = new Conditions(true, passedThroughMcGruborEntrance); // passed through northern entrance
		Conditions notInWoods = new Conditions(LogicType.NOR, inWoods);

		ConditionalStep getWorms = new ConditionalStep(this, goToRedVine, "Gather 3 Red Vine Worms in McGrubor's Woods.");
		getWorms.addStep(new Conditions(noWorms, notNearWorms, notInWoods), goToMcGruborWood);
		getWorms.addStep(inWoods, goToRedVine);

		ObjectStep teleToHemenster = new ObjectStep(this, ObjectID.GATE_48, new WorldPoint(2642, 3441, 0), "Teleport to Hemenster with your fishing pass.");
		teleToHemenster.addAlternateObjects(ObjectID.GATE_47);
		teleToHemenster.addDialogStep("Ranging Guild");
		ObjectStep leaveMcGruberWood = new ObjectStep(this, ObjectID.LOOSE_RAILING, new WorldPoint(2662, 3500, 0), "");
		leaveMcGruberWood.setText("Enter McGrubor's Woods via the northern entrance.");
		NpcStep runToJack = new NpcStep(this, NpcID.GRANDPA_JACK, new WorldPoint(2649, 3451, 0), "");
		runToJack.setText("Speak to Grandpa Jack to get a fishing rod.");
		runToJack.addDialogStep("Can I buy one of your fishing rods?");
		runToJack.addDialogStep("Very fair, I'll buy that rod!");
		leaveMcGruberWood.addSubSteps(runToJack);


		ConditionalStep enterContestStep = new ConditionalStep(this, goToHemenster);

		Zone contestGroundsEntrance = new Zone(new WorldPoint(2642, 3442, 0), new WorldPoint(2642, 3441, 0));
		ZoneCondition onContestGrounds = new ZoneCondition(contestGroundsEntrance);
		Conditions enteredContest = new Conditions(true, LogicType.AND, hasEverything, onContestGrounds);
		Conditions notEnteredContest = new Conditions(LogicType.NOR, enteredContest);
		Zone pipe = new Zone(new WorldPoint(2638, 3445, 0));
		ZoneCondition nearPipe = new ZoneCondition(pipe);
		Conditions wasNearPipe = new Conditions(true, nearPipe);

//		collectionStep.addStep(new Conditions(enteredContest, nearPipe, noGarlic), speakToBonzo);
//		collectionStep.addStep(enteredContest, putGarlicInPipe);
		goToHemenster.addSubSteps(putGarlicInPipe, speakToBonzo, fishNearPipes, speakToBonzoWithFish, talkToVestriStep);

		// pre-contest
		collectionStep.addStep(new Conditions(true, enteredContest, noGarlic),  speakToBonzo);
		collectionStep.addStep(new Conditions(enteredContest, hasGarlic), putGarlicInPipe);
		collectionStep.addStep(hasEverything, goToHemenster);
		collectionStep.addStep(noGarlic, getGarlic);
		collectionStep.addStep(noWorms, getWorms);
		collectionStep.addStep(new Conditions(hasCombatBracelet, hasWorms), teleToHemenster);
		collectionStep.addStep(new Conditions(noCombatBracelet, hasWorms, noFishingRod), runToJack);
		collectionStep.addStep(new Conditions(noFishingRod, hasWorms, notNearWorms), grandpaJack);
		//collectionStep.addStep(hasFishingRod, goToHemenster);



//		collectionStep.addStep(new Conditions(enteredContest, noGarlic), speakToBonzo);
//		collectionStep.addStep(goToHemensterCond, goToHemenster);
//		collectionStep.addStep(enteredContest, contestGroundsStep);
//		collectionStep.addStep(noGarlic, getGarlic);
//		collectionStep.addStep(retrievedWorms, grandpaJack);
//		collectionStep.addStep(needFishingRodWithTeleport, teleToHemenster);
//		collectionStep.addStep(new Conditions(true, needRod, retrievedWorms), grandpaJack);
//		collectionStep.addStep(new Conditions(true, needRod, atCmbBraceletTeleportZone), grandpaJack);
//		collectionStep.addStep(new Conditions(hasWormNoTele, new Conditions(true, LogicType.NOR, retrievedWorms)), leaveWoods);
//		collectionStep.addStep(isNearWorms, goToRedVine);
//		collectionStep.addStep(inWoods, goToRedVine);
//		collectionStep.addStep(notInWoods, goToMcGruborWood);

		steps.put(0, talkToVestriStep);
		steps.put(1, collectionStep);

		return steps;
	}


	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> panels = new ArrayList<>();
		ArrayList<QuestStep> steps = new ArrayList<>();
		steps.addAll(Arrays.asList(talkToVestriStep, collectionStep));
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
