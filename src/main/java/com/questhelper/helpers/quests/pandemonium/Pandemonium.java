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
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarbitID;

public class Pandemonium extends BasicQuestHelper
{
	NpcStep getToPandemonium, talkToWill, completeInterview, boardWAShip, explainJob, explainJob2, learnWhereYouAre, learnWhoWAare, talkToRibs, findLocationWA, enterShipyard, informAboutShip, showCup, getLogBook, getNewJob, talkToJim, meetGrog, finishQuest;
	ObjectStep buildCargoHold, embarkShipSY, disembarkShipSY, leaveShipyard, embarkShipP, dropCargoInCargoHold, disembarkShipPS, embarkShipPS, deliverCargo, disembarkShipP, getHammer, getSaw;
	TileStep dockAtPortSarim, dockAtPandemonium;
	Zone pandemonium, pandemoniumDockZone, portSarimDockZone, shipWreckZone;
	DetailedQuestStep navigateShip, takeHelm, takeHelm2, takeHelm3, raiseSails, raiseSails2, raiseSails3, salvageShipwreck, sailToPortSarim, pickupCargo, pickupCargoShip, sailToPandemonium, letGoOfHelm;
	Requirement onPandemonium, onboardShip, takenHelm, setSails, sailing, atShipwreck, notAtShipwreck, canSalvage, hammerAndSaw, atShipyard, notAtShipyard, atPortSarimDock, atPandemoniumDock, holdingCargo, cargoPickedUp, cargoNotPickedUp, notHoldingCargo, cargoInCargoHold, notOnboardShip, holdingCargoOnShip, holdingCargoNotOnShip;
	ItemRequirement hammer, saw;

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
		cNavigateToShipwreck.addStep(notAtShipwreck, explainJob2);
		cNavigateToShipwreck.addStep(new Conditions(LogicType.AND, notAtShipwreck, sailing), navigateShip);
		cNavigateToShipwreck.addStep(new Conditions(LogicType.AND, notAtShipwreck, takenHelm), raiseSails);
		cNavigateToShipwreck.addStep(new Conditions(LogicType.AND, notAtShipwreck, onboardShip), takeHelm);
		steps.put(8, cNavigateToShipwreck);
		steps.put(10, cNavigateToShipwreck);
		steps.put(12, cNavigateToShipwreck);
		steps.put(14, cNavigateToShipwreck);


		ConditionalStep cLearnWhereYouAre = new ConditionalStep(this, getToPandemonium);
		cLearnWhereYouAre.addStep(onPandemonium, learnWhereYouAre);
		steps.put(16, cLearnWhereYouAre);
		ConditionalStep cLearnWhoWAare = new ConditionalStep(this, getToPandemonium);
		cLearnWhoWAare.addStep(onPandemonium, learnWhoWAare);
		steps.put(18, cLearnWhoWAare);
		ConditionalStep cTalkToRibs = new ConditionalStep(this, getToPandemonium);
		cTalkToRibs.addStep(onPandemonium, talkToRibs);
		steps.put(20, cTalkToRibs);
		ConditionalStep cFindLocationWA = new ConditionalStep(this, getToPandemonium);
		cFindLocationWA.addStep(onPandemonium, findLocationWA);
		steps.put(22, cFindLocationWA);
		ConditionalStep cInformAboutShip = new ConditionalStep(this, getToPandemonium);
		cInformAboutShip.addStep(onPandemonium, informAboutShip);
		steps.put(24, cInformAboutShip);
		ConditionalStep cShowCup = new ConditionalStep(this, getToPandemonium);
		cShowCup.addStep(onPandemonium, showCup);
		steps.put(26, cShowCup);

		ConditionalStep cBuildCargoHold = new ConditionalStep(this, enterShipyard);
		cBuildCargoHold.addStep(new Conditions(LogicType.AND, atShipyard, onboardShip), buildCargoHold);
		cBuildCargoHold.addStep(new Conditions(LogicType.AND, atShipyard, hammerAndSaw), embarkShipSY);
		cBuildCargoHold.addStep(new Conditions(LogicType.AND, atShipyard, saw), getHammer);
		cBuildCargoHold.addStep(atShipyard, getSaw);
		steps.put(28, cBuildCargoHold);
		ConditionalStep cGetLogBook = new ConditionalStep(this, disembarkShipSY);
		cGetLogBook.addStep(notAtShipyard, getLogBook);
		cGetLogBook.addStep(notOnboardShip, leaveShipyard);
		steps.put(30, cGetLogBook); // Jim

		steps.put(32, getNewJob); // Jim
		steps.put(34, embarkShipP);
		ConditionalStep cSailToPortSarim = new ConditionalStep(this, embarkShipP);
		cSailToPortSarim.addStep(sailing, sailToPortSarim);
		cSailToPortSarim.addStep(takenHelm, raiseSails2);
		cSailToPortSarim.addStep(onboardShip, takeHelm2);
		steps.put(36, cSailToPortSarim);
		ConditionalStep cCheckPortMaster = new ConditionalStep(this, sailToPortSarim);
		cCheckPortMaster.addStep(new Conditions(holdingCargo, notOnboardShip), embarkShipPS);
		cCheckPortMaster.addStep(new Conditions(cargoNotPickedUp, notOnboardShip), pickupCargo);
		cCheckPortMaster.addStep(new Conditions(cargoNotPickedUp, takenHelm), letGoOfHelm);
		cCheckPortMaster.addStep(new Conditions(cargoNotPickedUp, onboardShip), disembarkShipPS);
		cCheckPortMaster.addStep(new Conditions(cargoNotPickedUp, sailing, atPortSarimDock), dockAtPortSarim);
		steps.put(38, cCheckPortMaster);
		ConditionalStep cSailToPandemonium = new ConditionalStep(this, cCheckPortMaster);
		cSailToPandemonium.addStep(new Conditions(new Conditions(LogicType.NAND, atPandemoniumDock), cargoInCargoHold, onboardShip, takenHelm, sailing), sailToPandemonium);
		cSailToPandemonium.addStep(new Conditions(new Conditions(LogicType.NAND, atPandemoniumDock), cargoInCargoHold, onboardShip, takenHelm), raiseSails3);
		cSailToPandemonium.addStep(new Conditions(new Conditions(LogicType.NAND, atPandemoniumDock), cargoInCargoHold, onboardShip), takeHelm3);
		cSailToPandemonium.addStep(new Conditions(new Conditions(LogicType.NAND, atPandemoniumDock), cargoPickedUp, holdingCargo, onboardShip), dropCargoInCargoHold);
		steps.put(40, cSailToPandemonium);

		ConditionalStep cDeliverCargo = new ConditionalStep(this, cSailToPandemonium);
		cDeliverCargo.addStep(new Conditions(onPandemonium, notOnboardShip, holdingCargo), deliverCargo);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, onboardShip, holdingCargo), disembarkShipP);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, onboardShip, new Conditions(LogicType.NOR, takenHelm, setSails), cargoInCargoHold), pickupCargoShip);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, notOnboardShip, cargoInCargoHold), embarkShipP);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, cargoInCargoHold, takenHelm), letGoOfHelm);
		cDeliverCargo.addStep(new Conditions(atPandemoniumDock, cargoInCargoHold, sailing), dockAtPandemonium);
		steps.put(42, cDeliverCargo);
		steps.put(44, talkToJim);
		steps.put(46, meetGrog);
		steps.put(48, finishQuest);

		return steps;
	}

	public void setupConditions()
	{
		onboardShip = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);
		notOnboardShip = new Conditions(LogicType.NAND, onboardShip);
		takenHelm = new VarbitRequirement(VarbitID.SAILING_SIDEPANEL_PLAYER_AT_HELM, 1);
		setSails = new VarbitRequirement(VarbitID.SAILING_SIDEPANEL_BOAT_MOVE_MODE, 0, Operation.GREATER);
		canSalvage = new Conditions(LogicType.AND, atShipwreck, new VarbitRequirement(VarbitID.SAILING_INTRO, 12, Operation.GREATER_EQUAL)); // At wreck, with explanation about salvaging
		sailing = new Conditions(LogicType.AND, takenHelm, setSails, onboardShip);

		atShipyard = new VarbitRequirement(VarbitID.SAILING_SIDEPANEL_SHIPYARD_MODE, 1);
		notAtShipyard = new Conditions(LogicType.NAND, atShipyard);

		holdingCargo = new VarbitRequirement(VarbitID.SAILING_CARRYING_CARGO, 1);
		notHoldingCargo = new Conditions(LogicType.NAND, holdingCargo);

		cargoPickedUp = new VarbitRequirement(VarbitID.PORT_TASK_SLOT_0_CARGO_TAKEN, 1);
		cargoNotPickedUp = new Conditions(LogicType.NAND, cargoPickedUp);
		cargoInCargoHold = new Conditions(cargoPickedUp, notHoldingCargo);
	}

	@Override
	protected void setupZones()
	{
		shipWreckZone = new Zone(new WorldPoint(3018, 3030, 0), new WorldPoint(3040, 3052, 0));
		pandemonium = new Zone(new WorldPoint(3069, 3005, 0), new WorldPoint(3028, 2963, 0));
		pandemoniumDockZone = new Zone(new WorldPoint(3065, 2974, 0), new WorldPoint(3084, 2998, 0));
		portSarimDockZone = new Zone(new WorldPoint(3045, 3183, 0), new WorldPoint(3061, 3208, 0));
		atShipwreck = new ZoneRequirement(shipWreckZone);
		notAtShipwreck = new Conditions(LogicType.NAND, atShipwreck);
		onPandemonium = new ZoneRequirement(pandemonium);
		atPandemoniumDock = new ZoneRequirement(pandemoniumDockZone);
		atPortSarimDock = new ZoneRequirement(portSarimDockZone);
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
		//guardrails
		getToPandemonium = new NpcStep(this, new int[]{NpcID.SEAMAN_LORRIS, NpcID.CAPTAIN_TOBIAS, NpcID.SEAMAN_THRESNOR}, new WorldPoint(3027, 3218, 0), "Get back to the Pandemonium.", true);
		getToPandemonium.addDialogStep("Yes please.");
		getToPandemonium.addDialogStep("I'd like to go to the Pandemonium.");
		getToPandemonium.setHighlightZone(pandemonium);


		//You got the Job!
		talkToWill = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_SARIM, NpcID.SAILING_INTRO_ANNE_SARIM}, new WorldPoint(3025, 3208, 0), "Talk to Will or Anne to start the quest", true);
		talkToWill.addDialogStep("Yes");
		completeInterview = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_SARIM, NpcID.SAILING_INTRO_ANNE_SARIM}, new WorldPoint(3025, 3208, 0), "Finish your interview with Will and Anne", true);
		boardWAShip = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_SARIM, NpcID.SAILING_INTRO_ANNE_SARIM}, new WorldPoint(3025, 3208, 0), "Board the ship.", true);
		boardWAShip.addDialogStep(Pattern.compile("(.*I guess.*)|(Okay.)"));

		// What is the job?
		explainJob = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_VIS, NpcID.SAILING_INTRO_ANNE_VIS}, new WorldPoint(3056, 3190, 0), "Listen to the job explanation.", true);
		takeHelm = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails = new DetailedQuestStep(this, "Raise your sails.");
		navigateShip = new DetailedQuestStep(this, "Sail south!");
		navigateShip.setHighlightZone(shipWreckZone);
		explainJob2 = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_VIS, NpcID.SAILING_INTRO_ANNE_VIS}, new WorldPoint(3027, 3048, 0), "Learn about the other part of your job.", true);
		salvageShipwreck = new DetailedQuestStep(this, "Deploy the Salvaging Hook.");

		// You lost the Job!
		learnWhereYouAre = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Learn where you are.", true);
		learnWhoWAare = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Learn who Will and Anne are.", true);
		talkToRibs = new NpcStep(this, NpcID.SAILING_INTRO_RIBS, new WorldPoint(3051, 2973, 0), "Talk to Ribs to learn about the map.", true);
		talkToRibs.addDialogStep("No, tell me about him.");
		findLocationWA = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Ask Steve about Will and Anne's location.", true);
		findLocationWA.addDialogStep("About Will and Anne...");
		findLocationWA.addDialogStep("Do you have any idea where Will and Anne might have gone?");
		informAboutShip = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Ask Jim about getting a boat.", true);
		showCup = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Hand over the cup to Jim and receive your mighty vessel!", true);

		// Your mighty vessel
		enterShipyard = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Enter the shipyard.");
		getHammer = new ObjectStep(this, 59732, "Pick up a Hammer.");
		getSaw = new ObjectStep(this, 59733, "Pick up a Saw.");
		embarkShipSY = new ObjectStep(this, 59829, "Board your vessel.");
		buildCargoHold = new ObjectStep(this, 59663, "Build the Cargo Hold.");
		disembarkShipSY = new ObjectStep(this, 59720, "Disembark your vessel.");
		leaveShipyard = new ObjectStep(this, 59722, "Leave the shipyard using the portal.");
		getLogBook = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Get Jim to give you a log book.");

		// You got the job!
		getNewJob = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Get Jim to give you a new job.");
		embarkShipP = new ObjectStep(this, 59832, new WorldPoint(3070, 2987, 0), "Board your vessel.");
		takeHelm2 = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails2 = new DetailedQuestStep(this, "Raise your sails.");
		sailToPortSarim = new DetailedQuestStep(this, "Sail to Port Sarim.");
		sailToPortSarim.setHighlightZone(portSarimDockZone);
		dockAtPortSarim = new TileStep(this, new WorldPoint(3048, 3187, 0), "Dock at the Port Sarim buoy.");
		letGoOfHelm = new DetailedQuestStep(this, "Let go of the helm.");
		disembarkShipPS = new ObjectStep(this, 59831, new WorldPoint(3151, 3193, 0), "Leave your vessel.");

		pickupCargo = new ObjectStep(this, 60320, new WorldPoint(3028, 3194, 0), "Pick up the cargo from the table.");
		embarkShipPS = new ObjectStep(this, 59832, new WorldPoint(3051, 3193, 0), "Board your vessel.");
		dropCargoInCargoHold = new ObjectStep(this, 60245, "Drop your crate in your cargo hold.");
		takeHelm3 = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails3 = new DetailedQuestStep(this, "Raise your sails.");
		sailToPandemonium = new DetailedQuestStep(this, "Sail to the Pandemonium.");
		sailToPandemonium.setHighlightZone(pandemoniumDockZone);
		dockAtPandemonium = new TileStep(this, new WorldPoint(3075, 2994, 0), "Dock at the Pandemonium buoy and let go of the helm.");
		pickupCargoShip = new DetailedQuestStep(this, "Pick up your cargo from your ship.");
		disembarkShipP = new ObjectStep(this, 59831, new WorldPoint(3070, 2987, 0), "Leave your vessel.");
		deliverCargo = new ObjectStep(this, 60321, new WorldPoint(3061, 2985, 0), "Deliver the cargo at the table.");
		//Finish up
		talkToJim = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Tell Jim about your delivery.");
		meetGrog = new NpcStep(this, new int[]{NpcID.STEVE_BEANIE, NpcID.SAILING_INTRO_GROG_VIS}, new WorldPoint(3050, 2966, 0), "Meet Grog at the bar.", true);
		finishQuest = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Talk to Steve to finish the quest.");
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
		return Arrays.asList(new ExperienceReward(Skill.SAILING, 300));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return new ArrayList<>();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(new UnlockReward("Access to the Sailing Skill"), new UnlockReward("Access to Pandemonium"), new UnlockReward("Your very own mighty ship!"));
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
