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
package com.questhelper.quests.piratestreasure;

import java.util.ArrayList;
import java.util.Arrays;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ChatMessageCondition;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.WidgetTextCondition;
import com.questhelper.steps.conditional.ZoneCondition;

public class RumSmugglingStep extends ConditionalStep
{
	private Zone karamjaZone1, karamjaZone2, karamjaBoat;

	private ItemRequirement karamjanRum, tenBananas, whiteApron, whiteApronHanging;

	private ConditionForStep onKaramja;
	private ConditionForStep atStart;
	private ConditionForStep employed;
	private ConditionForStep stashedRum;
	private ConditionForStep haveShippedRum;
	private ConditionForStep verifiedAState;
	private ConditionForStep hasWhiteApron;
	private ConditionForStep hasRum;
	private ConditionForStep hasRumOffKaramja;
	private ConditionForStep hadRumOffKaramja;
	private ConditionForStep lostRum;
	private ConditionForStep filledCrateWithBananasAndRum;
	private ChatMessageCondition crateSent;
	private ChatMessageCondition fillCrateWithBananasChat;

	private QuestStep talkToCustomsOfficer, getRumFromCrate, getWhiteApron, addBananasToCrate, addRumToCrate, talkToZambo, talkToLuthas, talkToLuthasAgain, goToKaramja, bringRumToRedbeard;

	public RumSmugglingStep(QuestHelper questHelper)
	{
		super(questHelper, new DetailedQuestStep(questHelper, "Please open Pirate Treasure's Quest Journal to sync the current quest state."));
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		addSteps();
	}

	private void addSteps() {
		this.addStep(new Conditions(hasRumOffKaramja), bringRumToRedbeard);
		this.addStep(new Conditions(verifiedAState, haveShippedRum, onKaramja), talkToCustomsOfficer);
		this.addStep(new Conditions(verifiedAState, haveShippedRum, hasWhiteApron), getRumFromCrate);
		this.addStep(new Conditions(verifiedAState, haveShippedRum), getWhiteApron);
		this.addStep(new Conditions(verifiedAState, filledCrateWithBananasAndRum, onKaramja), talkToLuthasAgain);
		this.addStep(new Conditions(verifiedAState, stashedRum, onKaramja), addBananasToCrate);
		this.addStep(new Conditions(verifiedAState, employed, hasRum, onKaramja), addRumToCrate);
		this.addStep(new Conditions(verifiedAState, employed, onKaramja), talkToZambo);
		this.addStep(new Conditions(verifiedAState, atStart, hasRum, onKaramja), talkToLuthas);
		this.addStep(new Conditions(verifiedAState, atStart, onKaramja), talkToZambo);
		this.addStep(verifiedAState, goToKaramja);
	}

	@Override
	protected void updateSteps()
	{
		if((hadRumOffKaramja.checkCondition(client) && !hasRumOffKaramja.checkCondition(client))
			|| lostRum.checkCondition(client))
		{
			haveShippedRum.setHasPassed(false);
			stashedRum.setHasPassed(false);
			atStart.setHasPassed(true);
			hadRumOffKaramja.setHasPassed(false);
			lostRum.setHasPassed(false);
		}

		if (crateSent.checkCondition(client))
		{
			haveShippedRum.checkCondition(client);
			employed.setHasPassed(false);
			fillCrateWithBananasChat.setHasReceivedChatMessage(false);
			filledCrateWithBananasAndRum.setHasPassed(false);
			crateSent.setHasReceivedChatMessage(false);
		}

		super.updateSteps();
	}

	private void setupZones() {
		karamjaZone1 = new Zone(new WorldPoint(2688, 3235, 0), new WorldPoint(2903, 2879, 0));
		karamjaZone2 = new Zone(new WorldPoint(2903, 2879, 0), new WorldPoint(2964, 3187, 0));
		karamjaBoat = new Zone(new WorldPoint(2964, 3138, 0), new WorldPoint(2951, 3144, 1));
	}

	private void setupItemRequirements() {
		karamjanRum = new ItemRequirement("Karamjan rum", ItemID.KARAMJAN_RUM);
		tenBananas = new ItemRequirement("Banana", ItemID.BANANA, 10);
		whiteApron = new ItemRequirement("White apron", ItemID.WHITE_APRON, 1, true);
		whiteApronHanging = new ItemRequirement("White apron", ItemID.WHITE_APRON_7957);
		whiteApronHanging.addAlternates(ItemID.WHITE_APRON);
	}

	private void setupConditions() {
		onKaramja = new ZoneCondition(karamjaZone1, karamjaZone2, karamjaBoat);
		ConditionForStep offKaramja = new ZoneCondition(false, karamjaZone1, karamjaZone2, karamjaBoat);
		ConditionForStep inPirateTreasureMenu = new WidgetTextCondition(WidgetInfo.DIARY_QUEST_WIDGET_TITLE, getQuestHelper().getQuest().getName());

		hasRum = new ItemRequirementCondition(new ItemRequirement("Karamjan Rum", ItemID.KARAMJAN_RUM));
		hasWhiteApron = new ItemRequirementCondition(whiteApron);
		hasRumOffKaramja = new Conditions(LogicType.AND, hasRum, offKaramja);
		hadRumOffKaramja = new Conditions(true, LogicType.AND, hasRum, offKaramja);
		lostRum = new Conditions(LogicType.AND, inPirateTreasureMenu, new WidgetTextCondition(119, 8, "I seem to have lost it."));

		ConditionForStep haveRumFromWidget = new Conditions(inPirateTreasureMenu, new WidgetTextCondition(119, 8, "I should take it to"));

		ConditionForStep agreedToGetRum = new WidgetTextCondition(217, 4, "Ok, I will bring you some rum.");
		ConditionForStep atStartFromWidget = new Conditions(inPirateTreasureMenu, new WidgetTextCondition(119, 8, "I need to go to"));
		atStart = new Conditions(true, LogicType.OR, agreedToGetRum, atStartFromWidget, lostRum, hadRumOffKaramja, haveRumFromWidget);

		crateSent = new ChatMessageCondition("Luthas hands you 30 coins.");

		ConditionForStep employedFromWidget = new Conditions(inPirateTreasureMenu, new WidgetTextCondition(119, 8, "I have taken employment"));

		/* Filled crate but not sent it and employed */
		ConditionForStep employedByWydinFromWidget = new Conditions(inPirateTreasureMenu, new WidgetTextCondition(119, 8, "I have taken a job at"));

		ConditionForStep employedFromDialog = new Conditions(new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, "If you could fill it up with bananas, I'll pay you 30<br>gold.", "Have you completed your task yet?", "you should see the old crate"));
		employed = new Conditions(true, LogicType.OR, employedFromDialog, employedFromWidget, employedByWydinFromWidget);

		ConditionForStep stashedRumFromWidget = new Conditions(inPirateTreasureMenu, new WidgetTextCondition(119, 12, "I have hidden my"));
		ConditionForStep stashedRumFromDialog = new Conditions(new WidgetTextCondition(229, 1, "You stash the rum in the crate."));
		ConditionForStep stashedRumFromChat = new Conditions(new ChatMessageCondition("There is also some rum stashed in here too.", "There's already some rum in here...",
			"There is some rum in here, although with no bananas to cover it. It is a little obvious."));
		stashedRum = new Conditions(true,  LogicType.OR, stashedRumFromDialog, stashedRumFromWidget, stashedRumFromChat, employedByWydinFromWidget);

		ConditionForStep fillCrateBananas = new Conditions(new WidgetTextCondition(229, 1, "You fill the crate with bananas."));
		fillCrateWithBananasChat = new ChatMessageCondition("The crate is full of bananas.", "The crate is already full.");
		ConditionForStep filledCrateWithBananas = new Conditions(false, LogicType.OR, fillCrateWithBananasChat, fillCrateBananas);
		filledCrateWithBananasAndRum = new Conditions(true, LogicType.AND, filledCrateWithBananas, stashedRum);

		ConditionForStep shippedRumFromWidget = new Conditions(inPirateTreasureMenu, new WidgetTextCondition(119, 15, "the crate has been shipped"));
		ConditionForStep shippedRumFromDialog = new Conditions(stashedRum, crateSent);
		ConditionForStep shippedRumFromChat = new ChatMessageCondition("There is already some rum in Wydin's store, I should go and get that first.");
		haveShippedRum = new Conditions(true,  LogicType.OR, shippedRumFromWidget, shippedRumFromDialog, shippedRumFromChat);

		verifiedAState = new Conditions(true,  LogicType.OR, atStart, employedFromWidget,  employedByWydinFromWidget, stashedRumFromWidget, shippedRumFromWidget, lostRum, hadRumOffKaramja, haveRumFromWidget);
	}

	private void setupSteps() {
		goToKaramja = new NpcStep(getQuestHelper(), NpcID.SEAMAN_LORRIS, new WorldPoint(3027, 3222, 0),
			"Talk to one of the Seamen on the docks in Port Sarim to go to Karamja.", new ItemRequirement("Coins", ItemID.COINS_995, 60));
		goToKaramja.addDialogStep("Yes please.");

		talkToZambo = new NpcStep(getQuestHelper(), NpcID.ZAMBO, new WorldPoint(2929, 3145, 0),
			"Talk to Zambo in the Karamja Wines, Spirits and Beers bar. Buy one Karamjan rum.", new ItemRequirement("Coins", ItemID.COINS_995, 30));
		talkToZambo.addDialogStep("Yes please.");

		talkToLuthas = new NpcStep(getQuestHelper(), NpcID.LUTHAS, new WorldPoint(2938, 3154, 0),
			"Pick 10 bananas nearby, and then talk to Luthas about working for him.",
			new ItemRequirement("Karamjan rum", ItemID.KARAMJAN_RUM), new ItemRequirement("Banana", ItemID.BANANA, 10));
		talkToLuthas.addDialogStep("Could you offer me employment on your plantation?");
		talkToLuthas.addDialogStep("Will you pay me for another crate full?");

		addRumToCrate = new ObjectStep(getQuestHelper(), ObjectID.CRATE_2072, new WorldPoint(2943, 3151, 0),
			"Put the Karamjan rum into the crate.",
			karamjanRum, tenBananas);

		addBananasToCrate = new ObjectStep(getQuestHelper(), ObjectID.CRATE_2072, new WorldPoint(2943, 3151, 0),
			"Fill the rest of the crate with bananas, then talk to Luthas.",
			new ItemRequirement("Banana", ItemID.BANANA, 10));

		talkToLuthasAgain = new NpcStep(getQuestHelper(), NpcID.LUTHAS, new WorldPoint(2938, 3154, 0),
			"Talk to Luthas and tell him you finished filling the crate.");

		talkToCustomsOfficer = new NpcStep(getQuestHelper(), NpcID.CUSTOMS_OFFICER, new WorldPoint(2955, 3146, 0),
			"Head back to Port Sarim. Pay the Customs Officer to sail there.", new ItemRequirement("Coins", ItemID.COINS_995, 30));
		talkToCustomsOfficer.addDialogStep("Thank you, I'll be on my way");
		talkToCustomsOfficer.addDialogStep("Can I journey on this ship?");
		talkToCustomsOfficer.addDialogStep("Search away, I have nothing to hide.");
		talkToCustomsOfficer.addDialogStep("Ok.");

		getWhiteApron = new DetailedQuestStep(getQuestHelper(), new WorldPoint(3016, 3229, 0),
			"Grab the white apron from the Fishing Shop.", whiteApronHanging);

		getRumFromCrate = new ObjectStep(getQuestHelper(), ObjectID.CRATE_2071, new WorldPoint(3009, 3207, 0),
			"Search the crate in the back room of the Port Sarim food shop. Make sure you're wearing your white apron.", whiteApron);
		getRumFromCrate.addDialogStep("Well, can I get a job here?");

		bringRumToRedbeard = new NpcStep(getQuestHelper(), NpcID.REDBEARD_FRANK, new WorldPoint(3053, 3251, 0),
			"Bring the Karamjan rum to Redbeard Frank in Port Sarim.",
			karamjanRum);
	}

	public ArrayList<PanelDetails> panelDetails() {
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Rum smuggling", new ArrayList<>(Arrays.asList(goToKaramja, talkToZambo, talkToLuthas, addRumToCrate, addBananasToCrate, talkToLuthas))));
		allSteps.add(new PanelDetails("Back to Port Sarim", new ArrayList<>(Arrays.asList(talkToCustomsOfficer, getWhiteApron, getRumFromCrate, bringRumToRedbeard))));
		return allSteps;
	}
}
