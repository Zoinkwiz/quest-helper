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
package com.questhelper.quests.makinghistory;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.DigStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.MAKING_HISTORY
)
public class MakingHistory extends BasicQuestHelper
{
	ItemRequirement spade, saphAmulet, ghostSpeakAmulet, ardougneTeleport, ectophial, ringOfDueling, enchantedKey, chest, journal, scroll, letter;

	ConditionForStep hasEnchantedKey, hasChest, hasJournal, hasScroll, talkedtoBlanin, talkedToDron, talkedToMelina, talkedToDroalak,
		inCastle, gotKey, gotChest, gotScroll, handedInJournal, handedInScroll, finishedFrem, finishedKey, finishedGhost, handedInEverything;

	QuestStep talkToJorral, talkToSilverMerchant, dig, openChest, talkToBlanin, talkToDron, talkToDroalak,
		talkToMelina, returnToDroalak, returnToJorral, continueTalkingToJorral, goUpToLathas, talkToLathas, finishQuest;

	ConditionalStep dronSteps, ghostSteps, keySteps;

	Zone castle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToJorral);

		keySteps = new ConditionalStep(this, talkToSilverMerchant);
		keySteps.addStep(hasChest, openChest);
		keySteps.addStep(gotKey, dig);
		keySteps.setLockingCondition(finishedKey);

		dronSteps = new ConditionalStep(this, talkToBlanin);
		dronSteps.addStep(talkedtoBlanin, talkToDron);
		dronSteps.setLockingCondition(finishedFrem);

		ghostSteps = new ConditionalStep(this, talkToDroalak);
		ghostSteps.addStep(talkedToMelina, returnToDroalak);
		ghostSteps.addStep(talkedToDroalak, talkToMelina);
		ghostSteps.setLockingCondition(finishedGhost);

		ConditionalStep getItems = new ConditionalStep(this, keySteps);
		getItems.addStep(handedInEverything, continueTalkingToJorral);
		getItems.addStep(new Conditions(finishedKey, finishedFrem, finishedGhost), returnToJorral);
		getItems.addStep(new Conditions(finishedKey, finishedFrem), ghostSteps);
		getItems.addStep(finishedKey, dronSteps);

		steps.put(1, getItems);

		ConditionalStep goTalkToLathas = new ConditionalStep(this, goUpToLathas);
		goTalkToLathas.addStep(inCastle, talkToLathas);

		steps.put(2, goTalkToLathas);

		steps.put(3, finishQuest);

		return steps;
	}

	public void setupItemRequirements()
	{
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		saphAmulet = new ItemRequirement("Sapphire amulet", ItemID.SAPPHIRE_AMULET);
		ghostSpeakAmulet = new ItemRequirement("Ghostspeak amulet", ItemID.GHOSTSPEAK_AMULET, 1, true);
		ardougneTeleport = new ItemRequirement("A teleport to Ardougne", ItemID.ARDOUGNE_TELEPORT, -1);
		ectophial = new ItemRequirement("Ectophial, or method of getting to Port Phasmatys", ItemID.ECTOPHIAL);
		ringOfDueling = new ItemRequirement("Ring of Dueling", ItemID.RING_OF_DUELING8);
		enchantedKey = new ItemRequirement("Enchanted key", ItemID.ENCHANTED_KEY);
		enchantedKey.setTip("You can get another from the silver merchant in East Ardougne's market");
		journal = new ItemRequirement("Journal", ItemID.JOURNAL_6755);
		chest = new ItemRequirement("Chest", ItemID.CHEST);
		chest.setTip("You can dig up another from north of Castle Wars");
		chest.setHighlightInInventory(true);
		scroll = new ItemRequirement("Scroll", ItemID.SCROLL);
		scroll.setTip("You can get another from Droalak in Port Phasmatys");
		letter = new ItemRequirement("Letter", ItemID.LETTER_6756);
		letter.setTip("You can get another from King Lathas in East Ardougne castle");
	}

	public void loadZones()
	{
		castle = new Zone(new WorldPoint(2570, 3283, 1), new WorldPoint(2590, 3310, 1));
	}

	public void setupConditions()
	{
		hasEnchantedKey = new ItemRequirementCondition(enchantedKey);
		hasJournal = new ItemRequirementCondition(journal);
		hasScroll = new ItemRequirementCondition(scroll);
		hasChest = new ItemRequirementCondition(chest);
		talkedtoBlanin = new Conditions(LogicType.OR, new VarbitCondition(1385, 1), new VarbitCondition(1385, 2));
		talkedToDron = new VarbitCondition(1385, 3, Operation.GREATER_EQUAL);

		talkedToDroalak = new Conditions(LogicType.OR, new VarbitCondition(1386, 2), new VarbitCondition(1386, 1));
		talkedToMelina = new Conditions(LogicType.OR, new VarbitCondition(1386, 4), new VarbitCondition(1386, 3));
		gotScroll = new VarbitCondition(1386, 5);
		handedInScroll = new VarbitCondition(1386, 6);

		inCastle = new ZoneCondition(castle);
		gotKey = new VarbitCondition(1384, 1);
		gotChest = new VarbitCondition(1384, 2, Operation.GREATER_EQUAL);
		handedInJournal = new VarbitCondition(1384, 4);
		handedInEverything = new Conditions(handedInJournal, handedInScroll, talkedToDron);
		finishedFrem = talkedToDron;
		finishedGhost = new Conditions(LogicType.OR, handedInScroll, gotScroll);
		finishedKey = new Conditions(LogicType.OR, handedInJournal, gotChest);
	}

	public void setupSteps()
	{
		talkToJorral = new NpcStep(this, NpcID.JORRAL, new WorldPoint(2436, 3346, 0), "Talk to Jorral at the outpost north of West Ardougne.");
		talkToJorral.addDialogStep("Tell me more.");
		talkToJorral.addDialogStep("Ok, I'll make a stand for history!");
		talkToSilverMerchant = new NpcStep(this, NpcID.SILVER_MERCHANT_8722, new WorldPoint(2658, 3316, 0), "Talk to the Silver Merchant in the East Ardougne Market.");
		talkToSilverMerchant.addDialogStep("Ask about the outpost.");
		dig = new DigStep(this, new WorldPoint(2442, 3140, 0), "Dig at the marked spot north of Castle Wars.", enchantedKey);
		openChest = new DetailedQuestStep(this, "Use the enchanted key on the chest.", enchantedKey, chest);
		talkToBlanin = new NpcStep(this, NpcID.BLANIN, new WorldPoint(2673, 3660, 0), "Talk to Blanin in south east Rellekka.");
		talkToDron = new NpcStep(this, NpcID.DRON, new WorldPoint(2661, 3698, 0), "Talk to Dron in north Rellekka.");
		talkToDron.addDialogStep("I'm after important answers.");
		talkToDron.addDialogStep("Why, you're the famous warrior Dron!");
		talkToDron.addDialogStep("An iron mace");
		talkToDron.addDialogStep(1, "Breakfast");
		talkToDron.addDialogStep(2, "Lunch");
		talkToDron.addDialogStep("Bunnies");
		talkToDron.addDialogStep("Red");
		talkToDron.addDialogStep("36");
		talkToDron.addDialogStep("8");
		talkToDron.addDialogStep("Fifth and Fourth");
		talkToDron.addDialogStep("North East side of town");
		talkToDron.addDialogStep("Blanin");
		talkToDron.addDialogStep("Fluffy");
		talkToDron.addDialogStep("12, but what does that have to do with anything?");


		talkToDroalak = new NpcStep(this, NpcID.DROALAK_3494, new WorldPoint(3659, 3468, 0), "Enter Port Phasmatys and talk to Droalak outside the general store.", ghostSpeakAmulet);
		talkToMelina = new NpcStep(this, NpcID.MELINA_3492, new WorldPoint(3674, 3479, 0), "Give Melina a sapphire amulet in the house north east of the general store.", saphAmulet, ghostSpeakAmulet);
		returnToDroalak = new NpcStep(this, NpcID.DROALAK_3494, new WorldPoint(3659, 3468, 0), "Return to Droalak outside the general store.");
		returnToJorral = new NpcStep(this, NpcID.JORRAL, new WorldPoint(2436, 3346, 0), "Return to Jorral north of West Ardougne with the scroll and journal.", scroll, journal);
		continueTalkingToJorral = new NpcStep(this, NpcID.JORRAL, new WorldPoint(2436, 3346, 0), "Return to Jorral north of West Ardougne.");
		goUpToLathas = new ObjectStep(this, ObjectID.STAIRCASE_15645, new WorldPoint(2572, 3296, 0), "Talk to King Lathas in East Ardougne castle.");
		talkToLathas = new NpcStep(this, NpcID.KING_LATHAS_9005, new WorldPoint(2578, 3293, 1), "Talk to King Lathas in East Ardougne castle.");
		talkToLathas.addDialogStep("Talk about the outpost.");
		talkToLathas.addSubSteps(goUpToLathas);
		finishQuest = new NpcStep(this, NpcID.JORRAL, new WorldPoint(2436, 3346, 0), "Return to Jorral north of West Ardougne with the letter to finish the quest.", letter);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(spade);
		reqs.add(saphAmulet);
		reqs.add(ghostSpeakAmulet);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ardougneTeleport);
		reqs.add(ectophial);
		reqs.add(ringOfDueling);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToJorral))));
		PanelDetails chestPanel = new PanelDetails("Find the book", new ArrayList<>(Arrays.asList(talkToSilverMerchant, dig, openChest)), spade);
		chestPanel.setLockingStep(keySteps);
		PanelDetails fremPanel = new PanelDetails("Fremennik tale", new ArrayList<>(Arrays.asList(talkToBlanin, talkToDron)));
		fremPanel.setLockingStep(dronSteps);
		PanelDetails ghostPanel = new PanelDetails("Find the scroll", new ArrayList<>(Arrays.asList(talkToDroalak, talkToMelina, returnToDroalak)), saphAmulet, ghostSpeakAmulet);
		ghostPanel.setLockingStep(ghostSteps);

		PanelDetails finishingPanel = new PanelDetails("Finishing off", new ArrayList<>(Arrays.asList(returnToJorral, talkToLathas, finishQuest)));

		allSteps.add(chestPanel);
		allSteps.add(fremPanel);
		allSteps.add(ghostPanel);
		allSteps.add(finishingPanel);
		return allSteps;
	}
}
