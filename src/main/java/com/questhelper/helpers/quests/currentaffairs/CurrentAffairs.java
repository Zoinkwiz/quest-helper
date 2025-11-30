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
package com.questhelper.helpers.quests.currentaffairs;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.BoardShipStep;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.SailStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class CurrentAffairs extends BasicQuestHelper
{
	ItemRequirement charcoalRequirement, coinsRequirement, hasFormCr4p, hasTinyNet, hasMayoralFishbowl, hasMayor, hasForm7r45h, hasForm7r45hSigned, hasDuck;
	Requirement filledFormCr4p, formCr4pGiven, boughtFishbowl, auditStarted, onboardShip, duckCanBeFollowed, catherbyCharted;
	NpcStep startQuest, talkToCouncillor, handOverFormCr4p, talkAfterFormHandedIn, talkToArhein, talkToHarry, getNewFishbowl, showArheinMayor, getNewMayor, showCatherineMayor, doAudit, getForm7r45h, showCatherineForm, giveArheimNews, getDuck, showCurrentsArhein;
	ObjectStep grabCharcoal, fishInAquarium;
	DetailedQuestStep fillFormCr4p, signForm7r45h, followThatDuck;
	ConditionalStep cGetMayor, cShowCatherineMayor, cSign7r45h, cBoardShip, cSailToStart;
	BoardShipStep boardShip;
	SailStep sailToStart;

	private static final Map<Integer, String> q1Answers = Map.of(1, "Pleasure.", 2, "Trade.", 3, "Piracy.");
	private static final Map<Integer, String> q2Answers = Map.of(1, "0", 2, "1", 3, "2+");
	private static final Map<Integer, String> q3Answers = Map.of(1, "Cargo spillage.", 2, "Theft.", 3, "Both.");
	private static final Map<Integer, String> q4Answers = Map.of(1, "No.", 2, "Yes.", 3, "Partial.");
	private static final Map<Integer, String> q5Answers = Map.of(1, "Partial.", 2, "No.", 3, "Yes.");
	private static final Map<Integer, String> q6Answers = Map.of(1, "Yes.", 2, "Partial.", 3, "No.");
	private static final Map<Integer, String> q7Answers = Map.of(1, "Less than a month.", 2, "Less than a year.", 3, "A year or more.");
	private static final Map<Integer, String> q8Answers = Map.of(1, "Varrock.", 2, "Falador.", 3, "Edgeville.");

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();
		steps.put(0, startQuest);
		steps.put(5, talkToCouncillor);
		ConditionalStep cFillForm = new ConditionalStep(this, talkToCouncillor);
		cFillForm.addStep(formCr4pGiven, talkAfterFormHandedIn);
		cFillForm.addStep(and(not(formCr4pGiven), hasFormCr4p, filledFormCr4p), handOverFormCr4p);
		cFillForm.addStep(and(not(formCr4pGiven), charcoalRequirement, not(filledFormCr4p), hasFormCr4p.highlighted()), fillFormCr4p);
		cFillForm.addStep(and(not(formCr4pGiven), hasFormCr4p, not(charcoalRequirement)), grabCharcoal);
		steps.put(10, cFillForm);
		steps.put(15, talkToArhein);
		cGetMayor = new ConditionalStep(this, talkToHarry);
		cGetMayor.setShouldPassthroughText(true);
		cGetMayor.addStep(hasMayor, showArheinMayor);
		cGetMayor.addStep(and(hasMayoralFishbowl, hasTinyNet), fishInAquarium);
		cGetMayor.addStep(boughtFishbowl, getNewFishbowl);
		steps.put(20, cGetMayor);
		cShowCatherineMayor = new ConditionalStep(this, getNewMayor);
		cShowCatherineMayor.addStep(and(auditStarted), doAudit);
		cShowCatherineMayor.addStep(and(not(auditStarted), hasMayor), showCatherineMayor);
		steps.put(25, cShowCatherineMayor);
		cSign7r45h = new ConditionalStep(this, getNewMayor);
		cSign7r45h.addStep(and(hasForm7r45hSigned), showCatherineForm);
		cSign7r45h.addStep(and(hasForm7r45h, hasMayor), signForm7r45h);
		cSign7r45h.addStep(and(hasMayor, not(hasForm7r45h)), getForm7r45h);
		steps.put(30, cSign7r45h);
		steps.put(35, giveArheimNews);

		cBoardShip = new ConditionalStep(this, getDuck);
		cBoardShip.setShouldPassthroughText(true);
		cBoardShip.addStep(and(not(catherbyCharted), hasDuck, not(onboardShip)), boardShip);
		cSailToStart = new ConditionalStep(this, cBoardShip);
		cSailToStart.addStep(and(not(catherbyCharted), hasDuck, onboardShip), sailToStart);
		cSailToStart.addStep(duckCanBeFollowed, followThatDuck);
		cSailToStart.addStep(catherbyCharted, showCurrentsArhein);
		steps.put(40, cSailToStart);
		return steps;
	}

	public void setupConditions()
	{
		filledFormCr4p = and(
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q1, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q2, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q3, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q4, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q5, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q6, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q7, 0, Operation.GREATER),
			new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_Q8, 0, Operation.GREATER));
		formCr4pGiven = new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_FORM_GIVEN, 1);
		boughtFishbowl = new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_KIT_PURCHASED, 1);
		auditStarted = new VarbitRequirement(VarbitID.CURRENT_AFFAIRS_AUDIT_START, 1);
		onboardShip = new VarbitRequirement(VarbitID.SAILING_BOARDED_BOAT, 1);
		catherbyCharted = new VarbitRequirement(VarbitID.SAILING_CHARTING_CURRENT_DUCK_CATHERBY_BAY_COMPLETE, 1);

		charcoalRequirement.setConditionToHide(filledFormCr4p);
		coinsRequirement.setConditionToHide(boughtFishbowl);
	}

	@Override
	protected void setupRequirements()
	{
		//Quest Requirements
		charcoalRequirement = new ItemRequirement("Charcoal", ItemID.CHARCOAL).isNotConsumed().canBeObtainedDuringQuest();
		charcoalRequirement.setTooltip("You can pick this up in the cabinet near Councillor Catherine in the north-east of Catherby.");
		coinsRequirement = new ItemRequirement("Coins", ItemID.COINS, 50);

		hasFormCr4p = new ItemRequirement("Form cr-4p", ItemID.CURRENT_AFFAIRS_FORM);
		hasFormCr4p.setTooltip("You can receive a new one from Councillor Catherine in the north-east of Catherby.");
		hasForm7r45h = new ItemRequirement("Form 7r4-5h", ItemID.CURRENT_AFFAIRS_FORM_2);
		hasForm7r45h.setTooltip("You can receive a new one from Councillor Catherine in the north-east of Catherby.");
		hasForm7r45hSigned = new ItemRequirement("Form 7r4-5h", ItemID.CURRENT_AFFAIRS_FORM_2_SIGNED);
		hasForm7r45hSigned.setTooltip("You can receive a new one from Councillor Catherine in the north-east of Catherby.");

		hasTinyNet = new ItemRequirement("Tiny net", ItemID.TINY_NET).isNotConsumed().canBeObtainedDuringQuest();
		hasTinyNet.setTooltip("You can receive a new one in the fish shop in the south-east of Catherby.");
		hasMayoralFishbowl = new ItemRequirement("Mayoral Fishbowl", ItemID.CURRENT_AFFAIRS_MAYORAL_FISHBOWL).canBeObtainedDuringQuest().isNotConsumed();
		hasMayoralFishbowl.setTooltip("You can receive a new one in the fish shop in the south-east of Catherby.");
		hasMayor = new ItemRequirement("Mayor of Catherby", ItemID.CURRENT_AFFAIRS_MAYOR_OF_CATHERBY).canBeObtainedDuringQuest().isNotConsumed();
		hasMayor.setTooltip("You can receive a new one in the fish shop in the south-east of Catherby.");

		boardShip = new BoardShipStep(this, hasDuck);
		sailToStart = new SailStep(this, new WorldPoint(2835, 3418, 0), hasDuck);

		hasDuck = new ItemRequirement("Current Duck", ItemID.SAILING_CHARTING_CURRENT_DUCK).canBeObtainedDuringQuest().isNotConsumed();
		hasForm7r45hSigned.setTooltip("You can receive a new one from Arheim on the Catherby Docks.");

		duckCanBeFollowed = new NpcRequirement(NpcID.SAILING_CHARTING_CURRENT_DUCK_MOVING);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Talk to Arhein on the northern part of the Catherby Docks to start the quest.", true);
		startQuest.addDialogStep("What's with the duck?");
		startQuest.addDialogStep("Yes.");

		talkToCouncillor = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Talk to Councillor Catherine in the north-east of Catherby.");
		talkAfterFormHandedIn = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Finish talking to Councillor Catherine in the north-east of Catherby.");
		grabCharcoal = new ObjectStep(this, ObjectID.CURRENT_AFFAIRS_CABINET, new WorldPoint(2827, 3453, 0), "Grab a piece of charcoal from the cabinet.");
		fillFormCr4p = new DetailedQuestStep(this, "Fill form cr-4p. The answers you provide do not matter.", hasFormCr4p.highlighted(), charcoalRequirement);

		//Answer values range from 1-3
		fillFormCr4p.addDialogSteps("Pleasure.", "Trade.", "Piracy.");
		fillFormCr4p.addDialogSteps("0", "1", "2+");
		fillFormCr4p.addDialogSteps("Cargo spillage.", "Theft.", "Both.");
		fillFormCr4p.addDialogSteps("No.", "Yes.", "Partial.");
		fillFormCr4p.addDialogSteps("Partial.", "No.", "Yes.");
		fillFormCr4p.addDialogSteps("Yes.", "Partial.", "No.");
		fillFormCr4p.addDialogSteps("Less than a month.", "Less than a year.", "A year or more.");
		fillFormCr4p.addDialogSteps("Varrock.", "Falador.", "Edgeville.");

		handOverFormCr4p = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Hand over the filled form to Councillor Catherine.", filledFormCr4p, hasFormCr4p);
		handOverFormCr4p.addDialogStep("Yes, I have it here.");

		talkToArhein = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Talk to Arhein on the docks about the mayor.", true);
		talkToArhein.addDialogStep("I need to find the Mayor of Catherby.");

		talkToHarry = new NpcStep(this, NpcID.HARRY, new WorldPoint(2831, 3444, 0), "Talk to Harry in the fish shop in south-east Catherby about a new mayor.", true, coinsRequirement);
		talkToHarry.addDialogStep("I'm here about the mayor.");
		talkToHarry.addDialogStep("Yes.");
		fishInAquarium = new ObjectStep(this, ObjectID.AQUARIUM, new WorldPoint(2831, 3444, 0), "Fish a new mayor from the aquarium.", true, hasTinyNet, hasMayoralFishbowl);
		getNewFishbowl = new NpcStep(this, NpcID.HARRY, new WorldPoint(2831, 3444, 0), "Talk to Harry in the fish shop in south-east Catherby about a new mayoral fishbowl.", true);
		getNewFishbowl.addDialogStep("I'm here about the mayor.");
		talkToHarry.addSubSteps(getNewFishbowl);
		showArheinMayor = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Introduce Arhein on the Catherby docks to the new Mayor of Catherby.", true, hasMayor);
		showArheinMayor.addDialogStep("About the mayor...");
		showCatherineMayor = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Talk to Councillor Catherine about changing the by-laws.", hasMayor);
		showCatherineMayor.addDialogStep("Yes, I have it here.");
		getNewMayor = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Talk to Arhein on the Catherby docks for a new mayor.", true);
		getNewMayor.addDialogStep("About the mayor...");

		doAudit = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Talk to Councillor Catherine to complete the audit.");
		doAudit.addDialogConsideringLastLineAndVarbit("What is your main reason for making port at Catherby?", VarbitID.CURRENT_AFFAIRS_FORM_Q1, q1Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("How many hulls does your ship have?", VarbitID.CURRENT_AFFAIRS_FORM_Q2, q2Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("Is your ship insured against cargo spillage and theft?", VarbitID.CURRENT_AFFAIRS_FORM_Q3, q3Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("Does your first mate have first aid training?", VarbitID.CURRENT_AFFAIRS_FORM_Q4, q4Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("Does your second mate have second aid training?", VarbitID.CURRENT_AFFAIRS_FORM_Q5, q5Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("Have you experienced symptoms of plague in the last two weeks?", VarbitID.CURRENT_AFFAIRS_FORM_Q6, q6Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("How much experience do you have sailing?", VarbitID.CURRENT_AFFAIRS_FORM_Q7, q7Answers);
		doAudit.addDialogConsideringLastLineAndVarbit("Where would you say your home port is?", VarbitID.CURRENT_AFFAIRS_FORM_Q8, q8Answers);

		getForm7r45h = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Talk to Councillor Catherine in the north-east of Catherby to receive a new form 7r4-5h.");
		signForm7r45h = new DetailedQuestStep(this, "Use form 7r4-5h on the Catherby Mayor.", hasForm7r45h.highlighted(), hasMayor.highlighted());
		showCatherineForm = new NpcStep(this, NpcID.CURRENT_AFFAIRS_COUNCILLOR, new WorldPoint(2825, 3454, 0), "Show Councillor Catherine signed form 7r4-5h.", hasForm7r45hSigned);

		giveArheimNews = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Give Arhein on the Catherby docks the good news.", true);
		giveArheimNews.addDialogStep("The by-law has been changed!");

		getDuck = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Get a new duck from Arhein on the Catherby docks.", true);
		getDuck.addDialogStep("About that duck...");

		followThatDuck = new DetailedQuestStep(this, new WorldPoint(2802, 3322, 0), "Follow that Duck! It'll end up south-west of Entrana."); //It ends up at (2802,3322,0)
		showCurrentsArhein = new NpcStep(this, NpcID.ARHEIN, new WorldPoint(2803, 3430, 0), "Share what you've learned with Arhein on the Catherby docks.", true);
		showCurrentsArhein.addDialogStep("I've charted the currents!");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			charcoalRequirement,
			coinsRequirement
		);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 22, false),
			new SkillRequirement(Skill.FISHING, 10, false)
		);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SAILING, 1400),
			new ExperienceReward(Skill.FISHING, 1000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Current Duck", ItemID.SAILING_CHARTING_CURRENT_DUCK),
			new ItemReward("Mayor of Catherby", ItemID.CURRENT_AFFAIRS_MAYOR_OF_CATHERBY),
			new ItemReward("Sawmill Coupon (oak plank)", ItemID.SAWMILL_COUPON, 25)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to charting currents")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Arhein's employee", List.of(
			startQuest,
			talkToCouncillor,
			fillFormCr4p,
			handOverFormCr4p,
			talkAfterFormHandedIn
		), List.of(
			charcoalRequirement
		)));

		sections.add(new PanelDetails("A new mayor", List.of(
			talkToArhein,
			talkToHarry,
			fishInAquarium,
			showArheinMayor,
			showCatherineMayor,
			doAudit,
			getForm7r45h,
			signForm7r45h,
			showCatherineForm,
			giveArheimNews
		), List.of(
			coinsRequirement
		)));

		sections.add(new PanelDetails("Map the currents!", List.of(
			cBoardShip,
			sailToStart,
			followThatDuck,
			showCurrentsArhein
		), List.of(
			hasDuck
		)));

		return sections;
	}
}
