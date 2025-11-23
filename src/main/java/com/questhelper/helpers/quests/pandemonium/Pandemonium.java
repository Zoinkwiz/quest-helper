/*
 * Copyright (c) 2025, TTvanWillegen
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
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.player.ShipInPortRequirement;
import com.questhelper.requirements.util.ItemSlots;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.util.Port;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.BoardShipStep;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class Pandemonium extends BasicQuestHelper
{
	NpcStep getToPandemonium, talkToWill, boardWAShip, explainJob, explainJob2, learnWhereYouAre, learnWhoWAare, talkToRibs, findLocationWA, enterShipyard, informAboutShip, showCup, getLogBook, getNewJob, talkToJim, meetGrog, finishQuest;
	ObjectStep buildCargoHold, embarkShipSY, disembarkShipSY, leaveShipyard,dropCargoInCargoHold, disembarkShipPS, deliverCargo, disembarkShipP, getHammer, getSaw;
	Zone pandemonium, portSarim, pandemoniumDockZone, portSarimDockZone, shipWreckZone;
	DetailedQuestStep navigateShip, takeHelm, takeHelm2, takeHelm3, raiseSails, raiseSails2, raiseSails3, salvageShipwreck, sailToPortSarim, pickupCargo, pickupCargoShip, sailToPandemonium, letGoOfHelm, letGoOfHelm2;
	BoardShipStep boardShip, boardShip2, boardShip3;
	Requirement onPandemonium, atPortSarim, onboardShip, takenHelm, setSails, sailing, atShipwreck, notAtShipwreck, canSalvage, hammerAndSaw, atShipyard, atPortSarimDock, atPandemoniumDock, holdingCargo, cargoPickedUp, cargoNotPickedUp, cargoInCargoHold;
	ItemRequirement hammer, saw;
	NoItemRequirement nothingInHands;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToWill);
		steps.put(2, talkToWill);
		steps.put(4, boardWAShip);
		steps.put(6, explainJob);
		ConditionalStep cNavigateToShipwreck = new ConditionalStep(this, boardWAShip);
		cNavigateToShipwreck.addStep(atShipwreck, explainJob2);
		cNavigateToShipwreck.addStep(and(notAtShipwreck, sailing), navigateShip);
		cNavigateToShipwreck.addStep(and(notAtShipwreck, takenHelm), raiseSails);
		cNavigateToShipwreck.addStep(and(notAtShipwreck, onboardShip), takeHelm);
		steps.put(8, cNavigateToShipwreck);
		steps.put(10, cNavigateToShipwreck);
		steps.put(12, cNavigateToShipwreck);
		ConditionalStep cSalvage = cNavigateToShipwreck.copy();
		cSalvage.addStep(canSalvage, salvageShipwreck);
		steps.put(14, cSalvage);


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
		cBuildCargoHold.addStep(and(atShipyard, onboardShip), buildCargoHold);
		cBuildCargoHold.addStep(and(atShipyard, hammerAndSaw), embarkShipSY);
		cBuildCargoHold.addStep(and(atShipyard, saw), getHammer);
		cBuildCargoHold.addStep(atShipyard, getSaw);
		steps.put(28, cBuildCargoHold);
		ConditionalStep cGetLogBook = new ConditionalStep(this, disembarkShipSY);
		cGetLogBook.addStep(not(atShipyard), getLogBook);
		cGetLogBook.addStep(not(onboardShip), leaveShipyard);
		steps.put(30, cGetLogBook); // Jim

		steps.put(32, getNewJob); // Jim
		steps.put(34, boardShip);
		ConditionalStep cSailToPortSarim = new ConditionalStep(this, boardShip);
		cSailToPortSarim.addStep(and(not(atPortSarimDock), sailing), sailToPortSarim);
		cSailToPortSarim.addStep(and(not(atPortSarimDock), takenHelm), raiseSails2);
		cSailToPortSarim.addStep(and(not(atPortSarimDock), onboardShip), takeHelm2);
		steps.put(36, cSailToPortSarim);
		ConditionalStep cCheckPortMaster = new ConditionalStep(this, cSailToPortSarim);
		cCheckPortMaster.addStep(and(atPortSarim, holdingCargo, not(onboardShip)), boardShip2);
		cCheckPortMaster.addStep(and(atPortSarim, cargoNotPickedUp, not(onboardShip)), pickupCargo);
		cCheckPortMaster.addStep(and(atPortSarimDock, cargoNotPickedUp, takenHelm), letGoOfHelm);
		cCheckPortMaster.addStep(and(atPortSarimDock, cargoNotPickedUp, onboardShip), disembarkShipPS);
		steps.put(38, cCheckPortMaster);
		ConditionalStep cSailToPandemonium = new ConditionalStep(this, cCheckPortMaster);
		cSailToPandemonium.addStep(and(not(atPandemoniumDock), cargoInCargoHold, onboardShip, takenHelm, sailing), sailToPandemonium);
		cSailToPandemonium.addStep(and(not(atPandemoniumDock), cargoInCargoHold, onboardShip, takenHelm), raiseSails3);
		cSailToPandemonium.addStep(and(not(atPandemoniumDock), cargoInCargoHold, onboardShip), takeHelm3);
		cSailToPandemonium.addStep(and(not(atPandemoniumDock), cargoPickedUp, holdingCargo, onboardShip), dropCargoInCargoHold);

		ConditionalStep cDeliverCargo = new ConditionalStep(this, cSailToPandemonium);
		cDeliverCargo.addStep(and(onPandemonium, not(onboardShip), holdingCargo), deliverCargo);
		cDeliverCargo.addStep(and(atPandemoniumDock, onboardShip, holdingCargo), disembarkShipP);
		cDeliverCargo.addStep(and(atPandemoniumDock, onboardShip, nor(takenHelm, setSails), cargoInCargoHold), pickupCargoShip);
		cDeliverCargo.addStep(and(onPandemonium, not(onboardShip), cargoInCargoHold), boardShip3);
		cDeliverCargo.addStep(and(atPandemoniumDock, cargoInCargoHold, takenHelm), letGoOfHelm2);
		steps.put(40, cDeliverCargo);
		steps.put(42, cDeliverCargo);
		steps.put(44, talkToJim);
		steps.put(46, meetGrog);
		steps.put(48, finishQuest);

		return steps;
	}

	public void setupConditions()
	{
		onboardShip = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);
		takenHelm = new VarbitRequirement(VarbitID.SAILING_SIDEPANEL_PLAYER_AT_HELM, 1);
		setSails = new VarbitRequirement(VarbitID.SAILING_SIDEPANEL_BOAT_MOVE_MODE, 0, Operation.GREATER);
		canSalvage = and(atShipwreck, new VarbitRequirement(VarbitID.SAILING_INTRO, 14, Operation.GREATER_EQUAL)); // At wreck, with explanation about salvaging
		sailing = and(takenHelm, setSails, onboardShip);

		atShipyard = new VarbitRequirement(VarbitID.SAILING_SIDEPANEL_SHIPYARD_MODE, 1);
		holdingCargo = new VarbitRequirement(VarbitID.SAILING_CARRYING_CARGO, 1);

		cargoPickedUp = new VarbitRequirement(VarbitID.PORT_TASK_SLOT_0_CARGO_TAKEN, 1);
		cargoNotPickedUp = not(cargoPickedUp);
		cargoInCargoHold = and(cargoPickedUp, not(holdingCargo));

		nothingInHands = new NoItemRequirement("Nothing equipped in your hands.",ItemSlots.WEAPON, ItemSlots.SHIELD);
	}

	@Override
	protected void setupZones()
	{
		shipWreckZone = new Zone(new WorldPoint(3018, 3030, 0), new WorldPoint(3040, 3052, 0));
		pandemonium = new Zone(new WorldPoint(3069, 3005, 0), new WorldPoint(3028, 2963, 0));
		portSarim = new Zone(new WorldPoint(3027, 3192, 0), new WorldPoint(3050, 3204, 0));
		pandemoniumDockZone = new Zone(new WorldPoint(3065, 2974, 0), new WorldPoint(3084, 2998, 0));
		portSarimDockZone = new Zone(new WorldPoint(3045, 3183, 0), new WorldPoint(3061, 3208, 0));
		atShipwreck = new ZoneRequirement(shipWreckZone);
		notAtShipwreck = not(atShipwreck);
		onPandemonium = new ZoneRequirement(pandemonium);
		atPortSarim = new ZoneRequirement(portSarim);
		atPandemoniumDock = new ZoneRequirement(pandemoniumDockZone);
		atPortSarimDock = new ZoneRequirement(portSarimDockZone);
	}

	@Override
	protected void setupRequirements()
	{
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER).isNotConsumed().canBeObtainedDuringQuest();
		hammer.setTooltip("You can pick this up at the shipyard.");
		saw = new ItemRequirement("Saw", ItemCollections.SAW).isNotConsumed().canBeObtainedDuringQuest();
		saw.setTooltip("You can pick this up at the shipyard.");
		hammerAndSaw = and(hammer, saw);
	}

	public void setupSteps()
	{
		//guardrails
		getToPandemonium = new NpcStep(this, new int[]{NpcID.SEAMAN_LORRIS, NpcID.CAPTAIN_TOBIAS, NpcID.SEAMAN_THRESNOR}, new WorldPoint(3027, 3218, 0), "Get back to the Pandemonium.", true);
		getToPandemonium.addDialogStep("Yes please.");
		getToPandemonium.addDialogStep("I'd like to go to the Pandemonium.");
		getToPandemonium.setHighlightZone(pandemonium);


		//You got the Job!
		talkToWill = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_SARIM, NpcID.SAILING_INTRO_ANNE_SARIM}, new WorldPoint(3025, 3208, 0), "Talk to Will or Anne on the docks of Port Sarim to start the quest.", true);
		talkToWill.addDialogStep("Yes.");
		boardWAShip = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_SARIM, NpcID.SAILING_INTRO_ANNE_SARIM}, new WorldPoint(3025, 3208, 0), "Board the ship.", true);
		boardWAShip.addDialogStep(Pattern.compile("(I guess I'm ready\\.\\.\\.)|(I guess so\\.\\.\\.)|(Okay\\.)"));

		// What is the job?
		explainJob = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_VIS, NpcID.SAILING_INTRO_ANNE_VIS}, new WorldPoint(3056, 3190, 0), "Listen to the job explanation.", true);
		takeHelm = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails = new DetailedQuestStep(this, "Raise your sails.");
		navigateShip = new DetailedQuestStep(this, "Sail south!");
		navigateShip.setHighlightZone(shipWreckZone);
		explainJob2 = new NpcStep(this, new int[]{NpcID.SAILING_INTRO_WILL_VIS, NpcID.SAILING_INTRO_ANNE_VIS}, new WorldPoint(3027, 3048, 0), "Learn about the other part of your job.", true);
		salvageShipwreck = new DetailedQuestStep(this, "Deploy the Salvaging Hook.");

		// You lost the Job!
		learnWhereYouAre = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Talk to 'Squawking' Steve Beanie behind the bar to learn about where you are.", true);
		learnWhoWAare = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Talk to 'Squawking' Steve Beanie about Will and Anne.", true);
		talkToRibs = new NpcStep(this, NpcID.SAILING_INTRO_RIBS, new WorldPoint(3051, 2973, 0), "Talk to Ribs near the door to learn about the map.", true);
		talkToRibs.addDialogStep(Pattern.compile("(No, tell me about him\\.)|(Now's not really the time for tales\\.)"));
		findLocationWA = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Ask 'Squawking' Steve Beanie behind the bar about Will and Anne's location.", true);
		findLocationWA.addDialogStep("About Will and Anne...");
		findLocationWA.addDialogStep("Do you have any idea where Will and Anne might have gone?");
		informAboutShip = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Ask Junior Jim by the docks about getting a ship.", true);
		showCup = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Hand over the cup to Junior Jim and receive your mighty vessel!", true);

		// Your mighty vessel
		enterShipyard = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Talk to Junior Jim to enter the shipyard.");
		getHammer = new ObjectStep(this, ObjectID.CRATE_HAMMERS, "Pick up a hammer from the crate of hammers.");
		getSaw = new ObjectStep(this, ObjectID.CRATE_SAWS, "Pick up a from the crate of saws.");
		embarkShipSY = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_PROXY_WIDE, "Board your vessel.");
		buildCargoHold = new ObjectStep(this, ObjectID.SAILING_BOAT_FACILITY_PLACEHOLDER_RAFT_0, "Build the Cargo Hold.");
		disembarkShipSY = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_SHIPYARD_DISEMBARK, "Disembark your vessel.");
		leaveShipyard = new ObjectStep(this, ObjectID.SAILING_SHIPYARD_PORTAL_EXIT, "Leave the shipyard using the portal.");
		getLogBook = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Get Junior Jim to give you a log book.");

		// You got the job!
		getNewJob = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Get Junior Jim to give you a new job.");
		boardShip = new BoardShipStep(this);
		takeHelm2 = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails2 = new DetailedQuestStep(this, "Raise your sails.");
		sailToPortSarim = new DetailedQuestStep(this, "Sail to Port Sarim.");
		sailToPortSarim.setHighlightZone(portSarimDockZone);
		letGoOfHelm = new DetailedQuestStep(this, "Let go of the helm.");
		disembarkShipPS = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(3151, 3193, 0), "Leave your ship.");

		pickupCargo = new ObjectStep(this, ObjectID.DOCK_LOADING_BAY_LEDGER_TABLE_WITHDRAW, new WorldPoint(3028, 3194, 0), "Pick up the cargo from the ledger table on the docks.", nothingInHands, atPortSarim);
		boardShip2 = new BoardShipStep(this);
		dropCargoInCargoHold = new ObjectStep(this, ObjectID.SAILING_BOAT_CARGO_HOLD_REGULAR_RAFT, "Drop the crate in your cargo hold.");
		takeHelm3 = new DetailedQuestStep(this, "Navigate using the helm.");
		raiseSails3 = new DetailedQuestStep(this, "Raise your sails.");
		sailToPandemonium = new DetailedQuestStep(this, "Sail to the Pandemonium.");
		sailToPandemonium.setHighlightZone(pandemoniumDockZone);
		letGoOfHelm2 = new DetailedQuestStep(this, "Let go of the helm.");
		pickupCargoShip = new ObjectStep(this, ObjectID.SAILING_BOAT_CARGO_HOLD_REGULAR_RAFT, "Pick up the cargo from your ship.", nothingInHands);
		boardShip3 = new BoardShipStep(this);
		disembarkShipP = new ObjectStep(this, ObjectID.SAILING_GANGPLANK_DISEMBARK, new WorldPoint(3070, 2987, 0), "Leave your ship.");
		deliverCargo = new ObjectStep(this, ObjectID.DOCK_LOADING_BAY_LEDGER_TABLE_DEPOSIT, new WorldPoint(3061, 2985, 0), "Deliver the cargo at the ledger table on the docks.");
		//Finish up
		talkToJim = new NpcStep(this, NpcID.JUNIOR_JIM, new WorldPoint(3059, 2979, 0), "Tell Junior Jim about your delivery.");
		meetGrog = new NpcStep(this, new int[]{NpcID.STEVE_BEANIE, NpcID.SAILING_INTRO_GROG_VIS}, new WorldPoint(3050, 2966, 0), "Meet 'Squawking' Steve Beanie and Old Grog at the bar.", true);
		finishQuest = new NpcStep(this, NpcID.STEVE_BEANIE, new WorldPoint(3050, 2966, 0), "Talk to 'Squawking' Steve Beanie to finish the quest.");
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
		return Arrays.asList(
			new ItemReward("Sawmill Coupon (wood plank)", ItemID.SAWMILL_COUPON, 25),
			new ItemReward("Repair kits", ItemID.BOAT_REPAIR_KIT, 2),
			new ItemReward("Spyglass", ItemID.SAILING_CHARTING_SPYGLASS, 1));
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
		allSteps.add(new PanelDetails("You got the job!", Arrays.asList(talkToWill, boardWAShip)));
		allSteps.add(new PanelDetails("What's the job?", Arrays.asList(explainJob, takeHelm, raiseSails, navigateShip, explainJob2, salvageShipwreck)));
		allSteps.add(new PanelDetails("You lost the job!", Arrays.asList(learnWhereYouAre, learnWhoWAare, talkToRibs, findLocationWA, informAboutShip, showCup)));
		allSteps.add(new PanelDetails("Your mighty vessel", Arrays.asList(enterShipyard, getSaw, getHammer, embarkShipSY, buildCargoHold, disembarkShipSY, leaveShipyard, getLogBook)));
		allSteps.add(new PanelDetails("You got the job... again!", Arrays.asList(getNewJob, boardShip, takeHelm2, raiseSails2, sailToPortSarim, letGoOfHelm, disembarkShipPS, pickupCargo, boardShip2)));
		allSteps.add(new PanelDetails("Deliver the cargo.", Arrays.asList(dropCargoInCargoHold, takeHelm3, raiseSails3, sailToPandemonium, letGoOfHelm2, pickupCargoShip, disembarkShipP, deliverCargo)));
		allSteps.add(new PanelDetails("Finishing up", Arrays.asList(talkToJim, meetGrog, finishQuest)));
		return allSteps;
	}
}
