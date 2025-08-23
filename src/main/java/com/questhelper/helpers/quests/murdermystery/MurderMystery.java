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
package com.questhelper.helpers.quests.murdermystery;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.DialogRequirement;
import com.questhelper.requirements.runelite.RuneliteRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;

public class MurderMystery extends BasicQuestHelper
{
	//Items Required
	ItemRequirement pot, pungentPot, criminalsDaggerAny, criminalsDagger, criminalsDaggerFlour, criminalsThread, criminalsThread1, criminalsThread2, criminalsThread3,
		twoFlypaper, potOfFlourHighlighted, flypaper, unknownPrint, silverNecklace, silverBook, silverBookFlour, silverNecklaceFlour, annaPrint, davidPrint, killersPrint, silverNeedle,
		silverPot, silverNeedleFlour, silverPotFlour, elizabethPrint, frankPrint, criminalsDaggerHighlighted, criminalsDaggerFlourHighlighted, silverCup, silverCupFlour, silverBottle,
		silverBottleFlour, bobPrint, carolPrint;

	RuneliteRequirement  heardAboutPoisonSalesman, talkedToPoisonSalesman, hadThread, hadPot, hadKillerPrint;

	Requirement annaGuilty, bobGuilty, carolGuilty, davidGuilty, elizabethGuilty, frankGuilty, hasCriminalSilverItem, isUpstairs,
		hasSuspectPrint, hasSilverItemFlour;

	Requirement talkedToAnna, talkedToBob, talkedToCarol, talkedToDavid, talkedToElizabeth, talkedToFrank, talkedToSuspect, pleaseWaitRequirement;
	Requirement checkedAnna, checkedBob, checkedCarol, checkedDavid, checkedElizabeth, checkedFrank, checkedSuspect;

	Zone upstairs;

	QuestStep talkToGuard, talkToGossip, talkToPoisonSalesman, pickUpDagger, pickUpPungentPot, searchWindowForThread, fillPotWithFlour, useFlourOnDagger,
		collectTwoFlypaper, useFlypaperOnDagger, searchAnnasBarrel, searchDavidsBarrel, searchFranksBarrel, searchElizabethsBarrel,
		searchBobsBarrel, searchCarolsBarrel, remainingSteps, finishQuest;

	QuestStep useFlourOnNecklace, useFlourOnCup, useFlourOnBottle, useFlourOnBook, useFlourOnNeedle, useFlourOnPot;
	QuestStep useFlypaperOnNecklace, useFlypaperOnCup, useFlypaperOnBottle, useFlypaperOnBook, useFlypaperOnNeedle, useFlypaperOnPot;
	QuestStep collectFlypaper, fillPotWithFlourForSilver;
	QuestStep compareAnna, compareBob, compareCarol, compareDavid, compareElizabeth, compareFrank;
	QuestStep talkToAnna, talkToBob, talkToFrank, talkToDavid, talkToCarol, talkToElizabeth;
	QuestStep searchAnna, searchBob, searchCarol, searchDavid, searchElizabeth, searchFrank;

	ConditionalStep useFlour, searchBarrel, useFlypaper, comparePrints, talkToSuspect, searchSuspectItem;
	DetailedQuestStep useFlourSidebar, searchBarrelSidebar, useFlypaperSidebar, comparePrintsSidebar, talkToSuspectSidebar, searchSuspectItemSidebar;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupZone();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGuard);

		Requirement doesntNeedMoreFlypaper = new Conditions(LogicType.OR, twoFlypaper, new Conditions(flypaper, unknownPrint));

		Conditions hadPotAndThread = new Conditions(hadThread, hadPot);

		ConditionalStep checkingPrintSteps = new ConditionalStep(this, pickUpDagger);
		/* compare prints */
		checkingPrintSteps.addStep(new Conditions(unknownPrint, hasSuspectPrint), comparePrints);
		checkingPrintSteps.addStep(new Conditions(unknownPrint, hasSilverItemFlour, flypaper), useFlypaper);
		checkingPrintSteps.addStep(new Conditions(unknownPrint, hasCriminalSilverItem, flypaper, potOfFlourHighlighted), useFlour);
		checkingPrintSteps.addStep(new Conditions(unknownPrint, hasCriminalSilverItem, flypaper), fillPotWithFlourForSilver);
		checkingPrintSteps.addStep(new Conditions(unknownPrint, hasCriminalSilverItem), collectFlypaper);
		/* Unknown print */
		checkingPrintSteps.addStep(new Conditions(criminalsDaggerFlour, hasCriminalSilverItem), useFlypaperOnDagger);
		checkingPrintSteps.addStep(new Conditions(criminalsDaggerAny, hasCriminalSilverItem, potOfFlourHighlighted), useFlourOnDagger);
		checkingPrintSteps.addStep(new Conditions(criminalsDaggerAny, doesntNeedMoreFlypaper, hasCriminalSilverItem), fillPotWithFlour);
		checkingPrintSteps.addStep(new Conditions(criminalsDaggerAny, doesntNeedMoreFlypaper), searchBarrel);
		checkingPrintSteps.addStep(new Conditions(criminalsDaggerAny), collectTwoFlypaper);

		ConditionalStep investigating = new ConditionalStep(this, searchWindowForThread);
		investigating.addStep(new Conditions(hadPotAndThread, hadKillerPrint, checkedSuspect), finishQuest);
		investigating.addStep(new Conditions(hadPotAndThread, hadKillerPrint, talkedToSuspect), searchSuspectItem);
		investigating.addStep(new Conditions(hadPotAndThread, hadKillerPrint, talkedToPoisonSalesman), talkToSuspect);
		investigating.addStep(new Conditions(hadPotAndThread, hadKillerPrint, heardAboutPoisonSalesman), talkToPoisonSalesman);
		investigating.addStep(new Conditions(hadPotAndThread, hadKillerPrint), talkToGossip);
		investigating.addStep(hadPotAndThread, checkingPrintSteps); /* Get dagger print */
		investigating.addStep(hadThread, pickUpPungentPot);

		steps.put(1, investigating);

		if (client.getVarpValue(195) > 0) updateSuspect();

		return steps;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		if (varbitChanged.getVarpId() != 195) return;

		updateSuspect();
	}

	private void updateSuspect()
	{
		// 5 for me
		int suspect = client.getVarpValue(195);
		if (suspect == 1)
		{
			useFlourSidebar.getText().set(0, useFlourOnNecklace.getText().get(0));
			searchBarrelSidebar.getText().set(0, searchAnnasBarrel.getText().get(0));
			useFlypaperSidebar.getText().set(0, useFlypaperOnNecklace.getText().get(0));
			comparePrintsSidebar.getText().set(0, compareAnna.getText().get(0));
			talkToSuspectSidebar.getText().set(0, talkToAnna.getText().get(0));
			searchSuspectItemSidebar.getText().set(0, searchAnna.getText().get(0));
		}
		else if (suspect == 2)
		{
			useFlourSidebar.getText().set(0, useFlourOnCup.getText().get(0));
			searchBarrelSidebar.getText().set(0, searchBobsBarrel.getText().get(0));
			useFlypaperSidebar.getText().set(0, useFlypaperOnCup.getText().get(0));
			comparePrintsSidebar.getText().set(0, compareBob.getText().get(0));
			talkToSuspectSidebar.getText().set(0, talkToBob.getText().get(0));
			searchSuspectItemSidebar.getText().set(0, searchBob.getText().get(0));
		}
		else if (suspect == 3)
		{
			useFlourSidebar.getText().set(0, useFlourOnBottle.getText().get(0));
			searchBarrelSidebar.getText().set(0, searchCarolsBarrel.getText().get(0));
			useFlypaperSidebar.getText().set(0, useFlypaperOnBottle.getText().get(0));
			comparePrintsSidebar.getText().set(0, compareCarol.getText().get(0));
			talkToSuspectSidebar.getText().set(0, talkToCarol.getText().get(0));
			searchSuspectItemSidebar.getText().set(0, searchCarol.getText().get(0));
		}
		else if (suspect == 4)
		{
			useFlourSidebar.getText().set(0, useFlourOnBook.getText().get(0));
			searchBarrelSidebar.getText().set(0, searchDavidsBarrel.getText().get(0));
			useFlypaperSidebar.getText().set(0, useFlypaperOnBook.getText().get(0));
			comparePrintsSidebar.getText().set(0, compareDavid.getText().get(0));
			talkToSuspectSidebar.getText().set(0, talkToDavid.getText().get(0));
			searchSuspectItemSidebar.getText().set(0, searchDavid.getText().get(0));
		}
		else if (suspect == 5)
		{
			useFlourSidebar.getText().set(0, useFlourOnNeedle.getText().get(0));
			searchBarrelSidebar.getText().set(0, searchElizabethsBarrel.getText().get(0));
			useFlypaperSidebar.getText().set(0, useFlypaperOnNeedle.getText().get(0));
			comparePrintsSidebar.getText().set(0, compareElizabeth.getText().get(0));
			talkToSuspectSidebar.getText().set(0, talkToElizabeth.getText().get(0));
			searchSuspectItemSidebar.getText().set(0, searchElizabeth.getText().get(0));
		}
		else if (suspect == 6)
		{
			useFlourSidebar.getText().set(0, useFlourOnPot.getText().get(0));
			searchBarrelSidebar.getText().set(0, searchFranksBarrel.getText().get(0));
			useFlypaperSidebar.getText().set(0, useFlypaperOnPot.getText().get(0));
			comparePrintsSidebar.getText().set(0, compareFrank.getText().get(0));
			talkToSuspectSidebar.getText().set(0, talkToFrank.getText().get(0));
			searchSuspectItemSidebar.getText().set(0, searchFrank.getText().get(0));
		}
	}

	public void setupZone()
	{
		upstairs = new Zone(new WorldPoint(2727, 3571, 1), new WorldPoint(2752, 3585, 1));
	}

	public void setupConditions()
	{
		isUpstairs = new ZoneRequirement(upstairs);

		hadThread = new RuneliteRequirement(getConfigManager(), "murdermysteryhadthread", criminalsThread.alsoCheckBank(questBank));
		hadPot = new RuneliteRequirement(getConfigManager(), "murdermysteryhadpot", pungentPot.alsoCheckBank(questBank));
		heardAboutPoisonSalesman = new RuneliteRequirement(getConfigManager(), "murdermysterytalkedtogossip",
			new Conditions(true, new DialogRequirement( "Especially as I heard that the poison salesman in the Seers' village made a big sale to one of the family the other day."))
		);
		talkedToPoisonSalesman = new RuneliteRequirement(getConfigManager(), "murdermysterytalkedtopoisonsalesman",
			new Conditions(true, LogicType.OR,
		    	new DialogRequirement(questHelperPlugin.getPlayerStateManager().getPlayerName(),  "Uh... no, it's ok.", false),
				new DialogRequirement("Anna, Bob, Carol, David, Elizabeth and Frank all bought a bottle! " +
				"In fact they bought the last of my supplies!")
		));

		annaGuilty = new VarplayerRequirement(195, 1);
		bobGuilty = new VarplayerRequirement(195, 2);
		carolGuilty = new VarplayerRequirement(195, 3);
		davidGuilty = new VarplayerRequirement(195, 4);
		elizabethGuilty = new VarplayerRequirement(195, 5);
		frankGuilty = new VarplayerRequirement(195, 6);

		hasCriminalSilverItem = new Conditions(
			LogicType.OR,
			new Conditions(annaGuilty, silverNecklace),
			new Conditions(bobGuilty, silverCup),
			new Conditions(carolGuilty, silverBottle),
			new Conditions(davidGuilty, silverBook),
			new Conditions(elizabethGuilty, silverNeedle),
			new Conditions(frankGuilty, silverPot)
		);

		hasSuspectPrint = new Conditions(
			LogicType.OR,
			new Conditions(annaGuilty, annaPrint),
			new Conditions(bobGuilty, bobPrint),
			new Conditions(carolGuilty, carolPrint),
			new Conditions(davidGuilty, davidPrint),
			new Conditions(elizabethGuilty, elizabethPrint),
			new Conditions(frankGuilty, frankPrint)
		);

		hadKillerPrint = new RuneliteRequirement(getConfigManager(), "murdermysteryhadkillerprint",
			new Conditions(killersPrint)
		);

		hasSilverItemFlour = new Conditions(
			LogicType.OR,
			new Conditions(annaGuilty, silverNecklaceFlour),
			new Conditions(bobGuilty, silverCupFlour),
			new Conditions(carolGuilty, silverBottleFlour),
			new Conditions(davidGuilty, silverBookFlour),
			new Conditions(elizabethGuilty, silverNeedleFlour),
			new Conditions(frankGuilty, silverPotFlour)
		);

		// TODO: This currently can't be used as checks are on each tick, but the text change + removal can occur within a tick
		pleaseWaitRequirement = new WidgetTextRequirement(231, 5, "Please wait...");

		talkedToAnna = new Conditions(
			true,
			new DialogRequirement("Anna", "That useless Gardener Stanford has let his compost", false)
		);
		talkedToBob = new Conditions(
			true,
			new DialogRequirement("Bob", "What's it to you anyway? If you absolutely", false)
		);
		talkedToCarol = new Conditions(
			true,
			new DialogRequirement("Carol", "I felt I had to do it myself.", false)
		);
		talkedToDavid = new Conditions(
			true,
			new DialogRequirement("David", "fire the whole workshy lot", false)
		);
		talkedToElizabeth = new Conditions(
			true,
			new DialogRequirement("Elizabeth", "Doesn't everyone?", false)
		);
		talkedToFrank = new Conditions(
			true,
			new DialogRequirement("Frank", "clean that family crest", false)
		);
		talkedToSuspect = new RuneliteRequirement(getConfigManager(), "murdermysterytalkedtosuspect",
			new Conditions(
				LogicType.OR,
				new Conditions(annaGuilty, talkedToAnna),
				new Conditions(bobGuilty, talkedToBob),
				new Conditions(carolGuilty, talkedToCarol),
				new Conditions(davidGuilty, talkedToDavid),
				new Conditions(elizabethGuilty, talkedToElizabeth),
				new Conditions(frankGuilty, talkedToFrank)
			)
		);

		checkedAnna = new Conditions(
			true,
			new WidgetTextRequirement(229, 1, "The compost is teeming with maggots.")
		);
		checkedBob = new Conditions(
			true,
			new WidgetTextRequirement(229, 1, "The beehive buzzes with activity.")
		);
		checkedCarol = new Conditions(
			true,
			new WidgetTextRequirement(229, 1, "The drain is totally blocked.")
		);
		checkedDavid = new Conditions(
			true,
			new WidgetTextRequirement(229, 1, "few hundred spiders ready to hatch.")
		);
		checkedElizabeth = new Conditions(
			true,
			new WidgetTextRequirement(229, 1, "The fountain is swarming")
		);
		checkedFrank = new Conditions(
			true,
			new WidgetTextRequirement(229, 1, "crest but it is very dirty")
		);
		checkedSuspect = new RuneliteRequirement(getConfigManager(), "murdermysterydisprovedsuspect",
			new Conditions(
				LogicType.OR,
				checkedAnna,
				checkedBob,
				checkedCarol,
				checkedDavid,
				checkedElizabeth,
				checkedFrank
			)
		);
	}

	@Override
	protected void setupRequirements()
	{
		pot = new ItemRequirement("Pot", ItemID.POT_EMPTY);
		pungentPot = new ItemRequirement("Pungent pot", ItemID.MURDERPOT2);

		criminalsDaggerAny = new ItemRequirement("Criminal's dagger", ItemID.MURDERWEAPON);
		criminalsDaggerAny.addAlternates(ItemID.MURDERWEAPONDUST, ItemID.MURDERFINGERPRINT);

		criminalsDagger = new ItemRequirement("Criminal's dagger", ItemID.MURDERWEAPON);
		criminalsDaggerHighlighted = new ItemRequirement("Criminal's dagger", ItemID.MURDERWEAPON);
		criminalsDaggerHighlighted.setHighlightInInventory(true);

		criminalsDaggerFlour = new ItemRequirement("Criminal's dagger", ItemID.MURDERWEAPONDUST);
		criminalsDaggerFlourHighlighted = new ItemRequirement("Criminal's dagger", ItemID.MURDERWEAPONDUST);
		criminalsDaggerFlourHighlighted.setHighlightInInventory(true);

		criminalsThread = new ItemRequirement("Criminal's thread", ItemID.MURDERTHREADR);
		criminalsThread.addAlternates(ItemID.MURDERTHREADG, ItemID.MURDERTHREADB);
		criminalsThread1 = new ItemRequirement("Criminal's thread", ItemID.MURDERTHREADR);
		criminalsThread2 = new ItemRequirement("Criminal's thread", ItemID.MURDERTHREADG);
		criminalsThread3 = new ItemRequirement("Criminal's thread", ItemID.MURDERTHREADB);
		twoFlypaper = new ItemRequirement("Flypaper", ItemID.MURDERPAPER, 2);
		flypaper = new ItemRequirement("Flypaper", ItemID.MURDERPAPER);
		flypaper.setHighlightInInventory(true);
		flypaper.setTooltip("You can get more from the sack in the shed on the west of the Sinclair Mansion");
		potOfFlourHighlighted = new ItemRequirement("Pot of flour", ItemID.POT_FLOUR);
		potOfFlourHighlighted.setHighlightInInventory(true);
		unknownPrint = new ItemRequirement("Unknown print", ItemID.MURDERFINGERPRINT1);

		/* Thread 1 items */
		bobPrint = new ItemRequirement("Bob's print", ItemID.MURDERFINGERPRINTB);
		carolPrint = new ItemRequirement("Carol's print", ItemID.MURDERFINGERPRINTC);
		silverCup = new ItemRequirement("Silver cup", ItemID.MURDERCUP);
		silverCupFlour = new ItemRequirement("Silver cup", ItemID.MURDERCUPDUST);
		silverBottle = new ItemRequirement("Silver bottle", ItemID.MURDERBOTTLE);
		silverBottleFlour = new ItemRequirement("Silver bottle", ItemID.MURDERBOTTLEDUST);

		/* Thread 2 items */
		annaPrint = new ItemRequirement("Anna's print", ItemID.MURDERFINGERPRINTA);
		davidPrint = new ItemRequirement("David's print", ItemID.MURDERFINGERPRINTD);
		silverNecklace = new ItemRequirement("Silver necklace", ItemID.MURDERNECKLACE);
		silverNecklaceFlour = new ItemRequirement("Silver necklace", ItemID.MURDERNECKLACEDUST);
		silverBook = new ItemRequirement("Silver book", ItemID.MURDERBOOK);
		silverBookFlour = new ItemRequirement("Silver book", ItemID.MURDERBOOKDUST);

		/* Thread 3 items */
		elizabethPrint = new ItemRequirement("Elizabeth's print", ItemID.MURDERFINGERPRINTE);
		frankPrint = new ItemRequirement("Frank's print", ItemID.MURDERFINGERPRINTF);
		silverNeedle = new ItemRequirement("Silver needle", ItemID.MURDERNEEDLE);
		silverNeedleFlour = new ItemRequirement("Silver needle", ItemID.MURDERNEEDLEDUST);
		silverPot = new ItemRequirement("Silver needle", ItemID.MURDERPOT);
		silverPotFlour = new ItemRequirement("Silver needle", ItemID.MURDERPOTDUST);

		killersPrint = new ItemRequirement("Killer's print", ItemID.MURDERFINGERPRINT);
	}

	public void setupSteps()
	{
		talkToGuard = new NpcStep(this, NpcID.MURDERGUARD, new WorldPoint(2741, 3561, 0), "Talk to the Guard in the Sinclair Manor north of Camelot.");
		talkToGuard.addDialogSteps("Yes.", "Sure, I'll help.");

		pickUpPungentPot = new DetailedQuestStep(this, new WorldPoint(2747, 3579, 0), "Enter the mansion and pick up the pungent pot inside the east room.", pungentPot);
		pickUpDagger = new DetailedQuestStep(this, new WorldPoint(2746, 3578, 0), "Pick up the criminal's dagger.", criminalsDaggerAny);
		searchWindowForThread = new ObjectStep(this, ObjectID.MURDERWINDOW, new WorldPoint(2748, 3577, 0), "Search the window for a clothing thread. The colour of the thread will match the killer's trousers.", criminalsThread);
		fillPotWithFlour = new ObjectStep(this, ObjectID.FLOURBARREL, new WorldPoint(2733, 3582, 0), "Fill your pot with flour from the barrel in the mansion's kitchen.", pot);
		useFlourOnDagger = new DetailedQuestStep(this, "Use the pot of flour on the Criminal's dagger.", potOfFlourHighlighted, criminalsDaggerHighlighted);
		collectTwoFlypaper = new ObjectStep(this, ObjectID.MURDERSACKS, new WorldPoint(2731, 3582, 0), "Investigate the sacks in the shed for flypaper. Get 2 pieces.", twoFlypaper);
		collectTwoFlypaper.addDialogStep("Yes, it might be useful.");

		QuestStep goUpstairs = new ObjectStep(this, ObjectID.MURDER_QIP_SPIRALSTAIRS, new WorldPoint(2736, 3581, 0), "Climb up the mansion staircase.");

		// Option 1 (red thread)
		searchBobsBarrel = new ObjectStep(this, ObjectID.MURDERBARRELB, new WorldPoint(2735, 3579, 0), "Search Bob's barrel on the ground floor.");
		searchCarolsBarrel = new ConditionalStep(this, goUpstairs, "Search Carol's barrel upstairs.");
		((ConditionalStep) searchCarolsBarrel).addStep(isUpstairs, new ObjectStep(this, ObjectID.MURDERBARRELC, new WorldPoint(2733, 3580, 1), ""));

		// Option 2 (green thread)
		searchAnnasBarrel = new ObjectStep(this, ObjectID.MURDERBARRELA, new WorldPoint(2733, 3575, 0), "Search Anna's barrel on the ground floor.");
		searchDavidsBarrel = new ConditionalStep(this, goUpstairs, "Search David's barrel upstairs.");
		((ConditionalStep) searchDavidsBarrel).addStep(isUpstairs, new ObjectStep(this, ObjectID.MURDERBARRELD, new WorldPoint(2733, 3577, 1), ""));

		// Option 3 (blue thread)
		searchFranksBarrel = new ConditionalStep(this, goUpstairs, "Search Frank's barrel upstairs.");
		((ConditionalStep) searchFranksBarrel).addStep(isUpstairs, new ObjectStep(this, ObjectID.MURDERBARRELF, new WorldPoint(2747, 3577, 1), ""));

		searchElizabethsBarrel = new ConditionalStep(this, goUpstairs, "Search Elizabeth's barrel upstairs.");
		((ConditionalStep) searchElizabethsBarrel).addStep(isUpstairs, new ObjectStep(this, ObjectID.MURDERBARRELE, new WorldPoint(2747, 3581, 1), ""));

		useFlypaperOnDagger = new DetailedQuestStep(this, "Use the flypaper on the dagger.", flypaper, criminalsDaggerFlourHighlighted);

		collectFlypaper = new ObjectStep(this, ObjectID.MURDERSACKS, new WorldPoint(2731, 3582, 0), "Investigate the sacks in the shed for 1 flypaper.", flypaper);
		collectFlypaper.addDialogStep("Yes, it might be useful.");
		fillPotWithFlourForSilver = new ObjectStep(this, ObjectID.FLOURBARREL, new WorldPoint(2733, 3582, 0), "Fill your pot with flour from the barrel in the mansion's kitchen.", pot);
		fillPotWithFlourForSilver.addSubSteps(collectFlypaper);

		useFlourOnNecklace = new DetailedQuestStep(this, "Use flour on the silver necklace.", silverNecklace.highlighted(), potOfFlourHighlighted);
		useFlourOnCup = new DetailedQuestStep(this, "Use flour on the silver cup.", silverCup.highlighted(), potOfFlourHighlighted);
		useFlourOnBottle = new DetailedQuestStep(this, "Use flour on the silver bottle.", silverBottle.highlighted(), potOfFlourHighlighted);
		useFlourOnBook = new DetailedQuestStep(this, "Use flour on the silver book.", silverBook.highlighted(), potOfFlourHighlighted);
		useFlourOnNeedle = new DetailedQuestStep(this, "Use flour on the silver needle.", silverNeedle.highlighted(), potOfFlourHighlighted);
		useFlourOnPot = new DetailedQuestStep(this, "Use flour on the silver pot.", silverPot.highlighted(), potOfFlourHighlighted);

		useFlypaperOnNecklace = new DetailedQuestStep(this, "Use flypaper on the silver necklace.", silverNecklaceFlour.highlighted(), flypaper.highlighted());
		useFlypaperOnCup = new DetailedQuestStep(this, "Use flypaper on the silver cup.", silverCupFlour.highlighted(), flypaper.highlighted());
		useFlypaperOnBottle = new DetailedQuestStep(this, "Use flypaper on the silver bottle.", silverBottleFlour.highlighted(), flypaper.highlighted());
		useFlypaperOnBook = new DetailedQuestStep(this, "Use flypaper on the silver book.", silverBookFlour.highlighted(), flypaper.highlighted());
		useFlypaperOnNeedle = new DetailedQuestStep(this, "Use flypaper on the silver needle.", silverNeedleFlour.highlighted(), flypaper.highlighted());
		useFlypaperOnPot = new DetailedQuestStep(this, "Use flypaper on the silver pot.", silverPotFlour.highlighted(), flypaper.highlighted());

		compareAnna = new DetailedQuestStep(this, "Use Anna's prints on the killer's print to compare them.", unknownPrint.highlighted(), annaPrint.highlighted());
		compareBob = new DetailedQuestStep(this, "Use Bob's prints on the killer's print to compare them.", unknownPrint.highlighted(), bobPrint.highlighted());
		compareCarol = new DetailedQuestStep(this, "Use Carol's prints on the killer's print to compare them.", unknownPrint.highlighted(), carolPrint.highlighted());
		compareDavid = new DetailedQuestStep(this, "Use David's prints on the killer's print to compare them.", unknownPrint.highlighted(), davidPrint.highlighted());
		compareElizabeth = new DetailedQuestStep(this, "Use Elizabeth's prints on the killer's print to compare them.", unknownPrint.highlighted(), elizabethPrint.highlighted());
		compareFrank = new DetailedQuestStep(this, "Use Frank's prints on the killer's print to compare them.", unknownPrint.highlighted(), frankPrint.highlighted());

		talkToGossip = new NpcStep(this, NpcID.GOSSIPY_MAN, new WorldPoint(2741, 3557, 0), "Talk to Gossip, just south of the Sinclair Mansion.");
		talkToGossip.addDialogStep(2, "Who do you think was responsible?");

		talkToPoisonSalesman = new NpcStep(this, NpcID.POISON_SALESMAN, new WorldPoint(2694, 3493, 0), "Talk to the " +
			"Poison Salesman in the Seers' Village pub.");
		talkToPoisonSalesman.addDialogStep("Who did you sell Poison to at the house?");
		talkToPoisonSalesman.addDialogStep("Talk about the Murder Mystery Quest");

		talkToAnna = new NpcStep(this, NpcID.ANNA_SINCLAIR, new WorldPoint(2734, 3575, 0), "Talk to Anna in the mansion about what she used the poison for. Make sure to finish the dialog.");
		talkToBob = new NpcStep(this, NpcID.BOB_SINCLAIR, new WorldPoint(2748, 3559, 0), "Talk to Bob south of the mansion about what he used the poison for. Make sure to finish the dialog.");
		talkToFrank = new NpcStep(this, NpcID.FRANK_SINCLAIR, new WorldPoint(2742, 3577, 0), "Talk to Frank in the mansion about what he used the poison for. Make sure to finish the dialog.");
		talkToDavid = new NpcStep(this, NpcID.DAVID_SINCLAIR, new WorldPoint(2739, 3581, 0), "Talk to David in the mansion about what he used the poison for. Make sure to finish the dialog.");
		talkToCarol = new ConditionalStep(this, goUpstairs, "Talk to Carol upstairs in the mansion about what she used her poison for. Make sure to finish the dialog.");
		((ConditionalStep) talkToCarol).addStep(isUpstairs, new NpcStep(this, NpcID.CAROL_SINCLAIR, new WorldPoint(2734, 3581, 1), ""));
		talkToElizabeth = new ConditionalStep(this, goUpstairs, "Talk to Elizabeth upstairs in the mansion about what she used her poison for.");
		((ConditionalStep) talkToElizabeth).addStep(isUpstairs, new NpcStep(this, NpcID.ELIZABETH_SINCLAIR, new WorldPoint(2746, 3581, 1), ""));

		searchAnna = new ObjectStep(this, ObjectID.MURDERCOMPOST, new WorldPoint(2730, 3572, 0), "Search the compost heap south west of the mansion. If a dialog box doesn't come up, go back to Anna to ask about poison, and COMPLETE THE DIALOG.");
		searchBob = new ObjectStep(this, ObjectID.MURDERHIVE, new WorldPoint(2730, 3559, 0), "Search the beehive south west of the mansion. If a dialog box doesn't come up, go back to Bob to ask about poison, and COMPLETE THE DIALOG.");
		searchCarol = new ObjectStep(this, ObjectID.MURDERDRAIN, new WorldPoint(2736, 3573, 0), "Search the drain south of the mansion. If a dialog box doesn't come up, go back to Carol to ask about poison, and COMPLETE THE DIALOG.");
		searchDavid = new ConditionalStep(this, goUpstairs, "Search the spider's nest, upstairs in the mansion to the south. If a dialog box doesn't come up, go back to David to ask about poison, and COMPLETE THE DIALOG.");
		((ConditionalStep)searchDavid).addStep(isUpstairs, new ObjectStep(this, ObjectID.MURDERWEB, new WorldPoint(2740, 3574, 1), ""));
		searchElizabeth = new ObjectStep(this, ObjectID.MURDERFOUNTAIN, new WorldPoint(2747, 3563, 0), "Search the fountain south east of the mansion. If a dialog box doesn't come up, go back to Elizabeth to ask about poison, and COMPLETE THE DIALOG.");
		searchFrank = new ObjectStep(this, ObjectID.MURDERSIGN, new WorldPoint(2746, 3573, 0), "Search the family crest attached to the south side of the mansion to the east. If a dialog box doesn't come up, go back to Frank to ask about poison, and COMPLETE THE DIALOG.");

		finishQuest = new NpcStep(this, NpcID.MURDERGUARD, new WorldPoint(2741, 3561, 0), "Return to the guard outside the Sinclair Mansion and tell him your findings.");
		finishQuest.addDialogStep("I know who did it!");

		remainingSteps = new DetailedQuestStep(this, "Follow the steps in the Quest Helper sidebar for the rest of the quest.");
		remainingSteps.addDialogStep(2, "Who do you think was responsible?");
		remainingSteps.addDialogStep("Why'd you buy poison the other day?");
		remainingSteps.addDialogStep("Who did you sell Poison to at the house?");
		remainingSteps.addDialogStep("I know who did it!");
		remainingSteps.setShowInSidebar(false);

		useFlour = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		useFlour.addStep(new Conditions(elizabethGuilty), useFlourOnNeedle);
		useFlour.addStep(new Conditions(annaGuilty), useFlourOnNecklace);
		useFlour.addStep(new Conditions(carolGuilty), useFlourOnBottle);
		useFlour.addStep(new Conditions(davidGuilty), useFlourOnBook);
		useFlour.addStep(new Conditions(frankGuilty), useFlourOnPot);
		useFlour.addStep(new Conditions(bobGuilty), useFlourOnCup);
		useFlourSidebar = new DetailedQuestStep(this, "Use flour on the suspect's item.");
		useFlourSidebar.addSubSteps(useFlourOnNeedle, useFlourOnNecklace, useFlourOnBottle, useFlourOnBook, useFlourOnPot, useFlourOnCup);

		searchBarrel = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		searchBarrel.addStep(new Conditions(elizabethGuilty), searchElizabethsBarrel);
		searchBarrel.addStep(new Conditions(annaGuilty), searchAnnasBarrel);
		searchBarrel.addStep(new Conditions(carolGuilty), searchCarolsBarrel);
		searchBarrel.addStep(new Conditions(davidGuilty), searchDavidsBarrel);
		searchBarrel.addStep(new Conditions(frankGuilty), searchFranksBarrel);
		searchBarrel.addStep(new Conditions(bobGuilty), searchBobsBarrel);
		searchBarrelSidebar = new DetailedQuestStep(this, "Search the suspect's barrel.");
		searchBarrelSidebar.addSubSteps(searchElizabethsBarrel, searchAnnasBarrel, searchCarolsBarrel, searchDavidsBarrel, searchFranksBarrel, searchBobsBarrel);

		useFlypaper = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		useFlypaper.addStep(new Conditions(elizabethGuilty), useFlypaperOnNeedle);
		useFlypaper.addStep(new Conditions(annaGuilty), useFlypaperOnNecklace);
		useFlypaper.addStep(new Conditions(carolGuilty), useFlypaperOnBottle);
		useFlypaper.addStep(new Conditions(davidGuilty), useFlypaperOnBook);
		useFlypaper.addStep(new Conditions(frankGuilty), useFlypaperOnPot);
		useFlypaper.addStep(new Conditions(bobGuilty), useFlypaperOnCup);
		useFlypaperSidebar = new DetailedQuestStep(this, "Use flypaper on the floured item.");
		useFlypaperSidebar.addSubSteps(useFlypaperOnNeedle, useFlypaperOnNecklace, useFlypaperOnBottle, useFlypaperOnBook, useFlypaperOnPot, useFlypaperOnCup);

		comparePrints = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		comparePrints.addStep(new Conditions(annaGuilty), compareAnna);
		comparePrints.addStep(new Conditions(bobGuilty), compareBob);
		comparePrints.addStep(new Conditions(carolGuilty), compareCarol);
		comparePrints.addStep(new Conditions(davidGuilty), compareDavid);
		comparePrints.addStep(new Conditions(elizabethGuilty), compareElizabeth);
		comparePrints.addStep(new Conditions(frankGuilty), compareFrank);
		comparePrintsSidebar = new DetailedQuestStep(this, "Compare the suspect's prints to the unknown prints.");
		comparePrintsSidebar.addSubSteps(compareAnna, compareBob, compareCarol, compareDavid, compareElizabeth, compareFrank);

		talkToSuspect = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		talkToSuspect.addStep(new Conditions(elizabethGuilty), talkToElizabeth);
		talkToSuspect.addStep(new Conditions(carolGuilty), talkToCarol);
		talkToSuspect.addStep(new Conditions(davidGuilty), talkToDavid);
		talkToSuspect.addStep(new Conditions(frankGuilty), talkToFrank);
		talkToSuspect.addStep(new Conditions(bobGuilty), talkToBob);
		talkToSuspect.addStep(new Conditions(annaGuilty), talkToAnna);
		talkToSuspect.addDialogStep("Why'd you buy poison the other day?");
		talkToSuspectSidebar = new DetailedQuestStep(this, "Talk to the suspect.");
		talkToSuspectSidebar.addSubSteps(talkToElizabeth, talkToCarol, talkToDavid, talkToFrank, talkToBob, talkToAnna);

		searchSuspectItem = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		searchSuspectItem.addStep(new Conditions(elizabethGuilty), searchElizabeth);
		searchSuspectItem.addStep(new Conditions(carolGuilty), searchCarol);
		searchSuspectItem.addStep(new Conditions(davidGuilty), searchDavid);
		searchSuspectItem.addStep(new Conditions(frankGuilty), searchFrank);
		searchSuspectItem.addStep(new Conditions(bobGuilty), searchBob);
		searchSuspectItem.addStep(new Conditions(annaGuilty), searchAnna);
		searchSuspectItemSidebar = new DetailedQuestStep(this, "Disprove the suspect's alibi by checking the thing they claimed to clean.");
		searchSuspectItemSidebar.addSubSteps(searchElizabeth, searchCarol, searchDavid, searchFrank, searchBob, searchAnna);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> required = new ArrayList<>();
		required.add(pot);
		return required;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.CRAFTING, 1406));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("Coins", ItemID.COINS, 2000));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Go to the Sinclair Manor", Collections.singletonList(talkToGuard), pot));
		allSteps.add(new PanelDetails("Collect evidence", Arrays.asList(pickUpPungentPot, pickUpDagger, searchWindowForThread)));
		allSteps.add(new PanelDetails("Collect fingerprints", Arrays.asList(collectTwoFlypaper, searchBarrelSidebar, fillPotWithFlour,
			useFlourOnDagger, useFlypaperOnDagger, fillPotWithFlourForSilver, useFlourSidebar, useFlypaperSidebar, comparePrintsSidebar)));
		allSteps.add(new PanelDetails("Finishing off", Arrays.asList(talkToGossip, talkToPoisonSalesman, talkToSuspectSidebar, searchSuspectItemSidebar, finishQuest)));
		return allSteps;
	}
}
