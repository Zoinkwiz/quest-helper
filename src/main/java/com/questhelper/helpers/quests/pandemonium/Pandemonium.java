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
package com.questhelper.helpers.quests.pandemonium;

import com.questhelper.collections.ItemCollections;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.TileStep;
import net.runelite.api.coords.WorldPoint;

public class Pandemonium extends BasicQuestHelper
{
	NpcStep talkToWill, completeInterview, boardWAShip, explainJob, explainJob2, learnWhereYouAre, learnWhoWAare, talkToRibs, findLocationWA, enterShipyard, informAboutShip, showCup, getLogBook, getNewJob, talkToJim, meetGrog, finishQuest;
	ObjectStep buildCargoHold, embarkShipSY, disembarkShipSY, leaveShipyard, embarkShipP, disembarkShipPS, embarkShipPS, deliverCargo, disembarkShipP;
	ItemStep getHammer, getSaw;
	TileStep dockAtPortSarim, dockAtPandemonium;
	DetailedQuestStep navigateShip, takeHelm, takeHelm2, takeHelm3, raiseSails, raiseSails2, raiseSails3, salvageShipwreck, sailToPortSarim, pickupCargo, dropCargoInCargoHold, pickupCargoShip, sailToPandemonium, letGoOfHelm;
	Requirement onboardShip, onboardShipBuild, takenHelm, setSails, sailSpeed, gustOfWind, sailing, atShipwreck, canSalvage, hammerAndSaw, atShipyard, notAtShipyard, notOnboardShipBuild, atPortSarimDock, atPandemoniumDock, holdingCargo, notHoldingCargo, notOnboardShip, holdingCargoOnShip, holdingCargoNotOnShip;
	ItemRequirement hammer, saw;

	private static final class AlphaNpcID
	{
		public static final int WILL = 122;
		public static final int ANNE = 14177;
		public static final int STEVE = 14181;
		public static final int GROG = 14188;
		public static final int RIBS = 14183;
		public static final int JIM = 14185;
		public static final int PORT_MASTER = 14329;
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToWill);
		steps.put(2, completeInterview);
		steps.put(4, boardWAShip);
		steps.put(6, explainJob);
		ConditionalStep cNavigateToShipwreck = new ConditionalStep(this, boardWAShip);
		cNavigateToShipwreck.addStep(canSalvage, salvageShipwreck);
		cNavigateToShipwreck.addStep(atShipwreck, explainJob2);
		cNavigateToShipwreck.addStep(sailing, navigateShip);
		cNavigateToShipwreck.addStep(takenHelm, raiseSails);
		cNavigateToShipwreck.addStep(onboardShip, takeHelm);
		steps.put(8, cNavigateToShipwreck);
		steps.put(10, cNavigateToShipwreck);
		steps.put(12, cNavigateToShipwreck);
		steps.put(14, cNavigateToShipwreck);

		steps.put(16, learnWhereYouAre);
		steps.put(18, learnWhoWAare);
		steps.put(20, talkToRibs);
		steps.put(22, findLocationWA);
		steps.put(24, informAboutShip);
		steps.put(26, showCup);

		ConditionalStep cBuildCargoHold = new ConditionalStep(this, enterShipyard);
		cBuildCargoHold.addStep(onboardShipBuild, buildCargoHold);
		cBuildCargoHold.addStep(hammerAndSaw, embarkShipSY);
		cBuildCargoHold.addStep(saw, getHammer);
		cBuildCargoHold.addStep(atShipyard, getSaw);
		steps.put(28, cBuildCargoHold);
		ConditionalStep cGetLogBook = new ConditionalStep(this, disembarkShipSY);
		cGetLogBook.addStep(notAtShipyard, getLogBook);
		cGetLogBook.addStep(notOnboardShipBuild, leaveShipyard);
		steps.put(30, cGetLogBook); // Jim

		steps.put(32, getNewJob); // Jim
		steps.put(34, embarkShipP);
		ConditionalStep cSailToPortSarim = new ConditionalStep(this, embarkShipP);
		cSailToPortSarim.addStep(sailing, sailToPortSarim);
		cSailToPortSarim.addStep(takenHelm, raiseSails2);
		cSailToPortSarim.addStep(onboardShip, takeHelm2);
		steps.put(36, cSailToPortSarim);
		ConditionalStep cCheckPortMaster = new ConditionalStep(this, sailToPortSarim);
		cCheckPortMaster.addStep(holdingCargo, embarkShipPS);
		cCheckPortMaster.addStep(notOnboardShip, pickupCargo);
		cCheckPortMaster.addStep(takenHelm, letGoOfHelm);
		cCheckPortMaster.addStep(onboardShip, disembarkShipPS);
		cCheckPortMaster.addStep(new Conditions(sailing, atPortSarimDock), dockAtPortSarim);
		steps.put(38, cCheckPortMaster);
		ConditionalStep cSailToPandemonium = new ConditionalStep(this, pickupCargo);
		cSailToPandemonium.addStep(sailing, sailToPandemonium);
		cSailToPandemonium.addStep(takenHelm, raiseSails3);
		cSailToPandemonium.addStep(notHoldingCargo, takeHelm3);
		cSailToPandemonium.addStep(onboardShip, dropCargoInCargoHold);
		steps.put(40, cSailToPandemonium);

		ConditionalStep cDeliverCargo = new ConditionalStep(this, sailToPandemonium);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, holdingCargoNotOnShip), deliverCargo);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, holdingCargoOnShip), disembarkShipP);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, takenHelm), letGoOfHelm);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, onboardShip), pickupCargoShip);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, sailing), dockAtPandemonium);
		steps.put(42, cDeliverCargo);
		steps.put(44, talkToJim);
		steps.put(46, meetGrog);
		steps.put(48, finishQuest);

		return steps;
	}

	public void setupConditions()
	{
		onboardShip = new VarbitRequirement(16342, 1);
		notOnboardShip = new Conditions(LogicType.NAND, onboardShip);
		takenHelm = new VarbitRequirement(16343, 3);
		setSails = new VarbitRequirement(16562, 1);
		sailSpeed = new VarbitRequirement(16563, 1); // Unused, checks the sail speed
		gustOfWind = new VarbitRequirement(14192, 1); // Unused, can be used to see if there is a gust of wind in the sails
		canSalvage = new Conditions(LogicType.AND, atShipwreck, new VarbitRequirement(16187, 12, Operation.GREATER_EQUAL)); // At wreck, with explanation about salvaging
		sailing = new Conditions(LogicType.AND, takenHelm, setSails, onboardShip);

		atShipyard = new VarbitRequirement(16183, 1);
		onboardShipBuild = new VarbitRequirement(16345, 1);
		notOnboardShipBuild = new Conditions(LogicType.NAND, onboardShipBuild);
		notAtShipyard = new Conditions(LogicType.NAND, atShipyard);

		holdingCargo = new VarbitRequirement(16547, 1); //Other contenders: 17090, 17224
		notHoldingCargo = new Conditions(LogicType.NAND, holdingCargo);
		holdingCargoOnShip = new Conditions(LogicType.AND, holdingCargo, onboardShip);
		holdingCargoNotOnShip = new Conditions(LogicType.AND, holdingCargo, notOnboardShip);
	}

	@Override
	protected void setupZones()
	{
		//Note: These don't seem to work when sailing. (Different plane?)
		atShipwreck = new ZoneRequirement(new Zone(new WorldPoint(3040, 3050, 0), new WorldPoint(3000, 3052, 0)));
		atPandemoniumDock = new ZoneRequirement(new Zone(new WorldPoint(3065, 2974, 0), new WorldPoint(3084, 2998, 0)));
		atPortSarimDock = new ZoneRequirement(new Zone(new WorldPoint(3045, 3183, 0), new WorldPoint(3061, 3208, 0)));
	}

	@Override
	protected void setupRequirements()
	{
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed();
		hammer.setTooltip("You can pick this up at the shipyard.");
		saw = new ItemRequirement("Saw", ItemCollections.SAW).isNotConsumed();
		saw.setTooltip("You can pick this up at the shipyard.");
		hammerAndSaw = new Conditions(LogicType.AND, hammer, saw);
	}

	public void setupSteps()
	{
		//You got the Job!
		talkToWill = new NpcStep(this, new int[]{AlphaNpcID.WILL, AlphaNpcID.ANNE}, new WorldPoint(3025, 3208, 0), "Talk to Will or Anne to start the quest", true);
		talkToWill.addDialogStep("Yes");
		completeInterview = new NpcStep(this, new int[]{AlphaNpcID.WILL, AlphaNpcID.ANNE}, new WorldPoint(3025, 3208, 0), "Finish your interview with Will and Anne", true);
		boardWAShip = new NpcStep(this, new int[]{AlphaNpcID.WILL, AlphaNpcID.ANNE}, new WorldPoint(3025, 3208, 0), "Board the ship.", true);
		boardWAShip.addDialogStep(Pattern.compile("(.*I guess.*)|(Okay.)"));

		// What is the job?
		explainJob = new NpcStep(this, new int[]{AlphaNpcID.WILL, AlphaNpcID.ANNE}, new WorldPoint(3056, 3190, 0), "Listen to the job explanation.", true);
		takeHelm = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails = new DetailedQuestStep(this, "Raise your sails.");
		navigateShip = new DetailedQuestStep(this, "Sail south!");
		explainJob2 = new NpcStep(this, new int[]{AlphaNpcID.WILL, AlphaNpcID.ANNE}, new WorldPoint(3027, 3048, 0), "Learn about the other part of your job.", true);
		salvageShipwreck = new DetailedQuestStep(this, "Deploy the Salvaging Hook.");

		// You lost the Job!
		learnWhereYouAre = new NpcStep(this, AlphaNpcID.STEVE, new WorldPoint(3050, 2966, 0), "Learn where you are.", true);
		learnWhoWAare = new NpcStep(this, AlphaNpcID.STEVE, new WorldPoint(3050, 2966, 0), "Learn who Will and Anne are.", true);
		talkToRibs = new NpcStep(this, AlphaNpcID.RIBS, new WorldPoint(3051, 2973, 0), "Talk to Ribs to learn about the map.", true);
		talkToRibs.addDialogStep("No, tell me about him.");
		findLocationWA = new NpcStep(this, AlphaNpcID.STEVE, new WorldPoint(3050, 2966, 0), "Ask Steve about Will and Anne's location.", true);
		findLocationWA.addDialogStep("About Will and Anne...");
		findLocationWA.addDialogStep("Do you have any idea where Will and Anne might have gone?");
		informAboutShip = new NpcStep(this, AlphaNpcID.JIM, new WorldPoint(3059, 2979, 0), "Ask Jim about getting a boat.", true);
		showCup = new NpcStep(this, AlphaNpcID.JIM, new WorldPoint(3059, 2979, 0), "Hand over the cup to Jim and retrieve your mighty vessel!", true);

		// Your mighty vessel
		enterShipyard = new NpcStep(this, AlphaNpcID.JIM, new WorldPoint(3059, 2979, 0), "Enter the shipyard.");
		getHammer = new ItemStep(this, new WorldPoint(1364, 4736, 0), "Pick up the Hammer.", hammer);
		getSaw = new ItemStep(this, new WorldPoint(1364, 4735, 0), "Pick up the Saw.", saw);
		embarkShipSY = new ObjectStep(this, 56429, "Board your vessel.");
		buildCargoHold = new ObjectStep(this, 56421, "Build the Cargo Hold.");
		disembarkShipSY = new ObjectStep(this, 56429, "Disembark your vessel.");
		leaveShipyard = new ObjectStep(this, 56413, "Leave the shipyard using the portal.");
		getLogBook = new NpcStep(this, AlphaNpcID.JIM, new WorldPoint(3059, 2979, 0), "Get Jim to give you a log book.");

		// You got the job!
		getNewJob = new NpcStep(this, AlphaNpcID.JIM, new WorldPoint(3059, 2979, 0), "Get Jim to give you a new job.");
		embarkShipP = new ObjectStep(this, 56429, new WorldPoint(3070, 2987, 0), "Board your vessel.");
		takeHelm2 = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails2 = new DetailedQuestStep(this, "Raise your sails.");
		sailToPortSarim = new DetailedQuestStep(this, "Sail to Port Sarim.");
		dockAtPortSarim = new TileStep(this, new WorldPoint(3048, 3187, 0), "Dock at the Port Sarim buoy.");
		letGoOfHelm = new DetailedQuestStep(this, "Let go of the helm.");
		disembarkShipPS = new ObjectStep(this, 56429, new WorldPoint(3151, 3193, 0), "Leave your vessel.");

		pickupCargo = new ObjectStep(this, 56866, new WorldPoint(3028, 3194, 0), "Pick up the cargo from the table.");
		embarkShipPS = new ObjectStep(this, 56429, new WorldPoint(3151, 3193, 0), "Board your vessel.");
		dropCargoInCargoHold = new DetailedQuestStep(this, "Drop your crate in your cargo hold.");
		takeHelm3 = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails3 = new DetailedQuestStep(this, "Raise your sails.");
		sailToPandemonium = new DetailedQuestStep(this, "Sail to the Pandemonium.");
		dockAtPandemonium = new TileStep(this, new WorldPoint(3075, 2994, 0), "Dock at the Pandemonium buoy.");
		pickupCargoShip = new DetailedQuestStep(this, "Pick up your cargo from your ship.");
		disembarkShipP = new ObjectStep(this, 56429, new WorldPoint(3070, 2987, 0), "Leave your vessel.");
		deliverCargo = new ObjectStep(this, 56872, new WorldPoint(3061, 2985, 0), "Deliver the cargo at the table.");
		//Finish up
		talkToJim = new NpcStep(this, AlphaNpcID.JIM, new WorldPoint(3059, 2979, 0), "Tell Jim about your delivery.");
		meetGrog = new NpcStep(this, new int[]{AlphaNpcID.STEVE, AlphaNpcID.GROG}, new WorldPoint(3050, 2966, 0), "Meet Grog at the bar.", true);
		finishQuest = new NpcStep(this, AlphaNpcID.STEVE, new WorldPoint(3050, 2966, 0), "Talk to Steve to finish the quest.");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(hammer, saw);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return new ArrayList<>();
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return new ArrayList<>();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(new UnlockReward("Access to the Sailing Skill"), new UnlockReward("Access to Pandemonium"), new UnlockReward("300 Sailing XP"), new UnlockReward("Your very own mighty ship!"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("You got the job!", Arrays.asList(talkToWill, completeInterview, boardWAShip)));
		allSteps.add(new PanelDetails("What's the job?", Arrays.asList(explainJob, takeHelm, raiseSails, navigateShip, explainJob2, salvageShipwreck)));
		allSteps.add(new PanelDetails("You lost the job!", Arrays.asList(learnWhereYouAre, learnWhoWAare, talkToRibs, findLocationWA, informAboutShip, showCup)));
		allSteps.add(new PanelDetails("Your mighty vessel", Arrays.asList(enterShipyard, getSaw, getHammer, embarkShipSY, buildCargoHold, disembarkShipSY, leaveShipyard, getLogBook)));
		allSteps.add(new PanelDetails("You got the job... again!", Arrays.asList(getNewJob, embarkShipP, takeHelm2, raiseSails2, sailToPortSarim, dockAtPortSarim, letGoOfHelm, disembarkShipPS, pickupCargo, embarkShipPS)));
		allSteps.add(new PanelDetails("Deliver the cargo.", Arrays.asList(dropCargoInCargoHold, takeHelm3, raiseSails3, sailToPandemonium, dockAtPandemonium, pickupCargoShip, disembarkShipP, deliverCargo)));
		allSteps.add(new PanelDetails("Finishing up", Arrays.asList(talkToJim, meetGrog, finishQuest)));
		return allSteps;
	}
}
