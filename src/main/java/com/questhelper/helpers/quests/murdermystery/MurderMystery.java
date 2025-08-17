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
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

public class MurderMystery extends BasicQuestHelper
{
	// Required items
	ItemRequirement pot;

	// Mid-quest requirements
	ItemRequirement pungentPot;
	ItemRequirement criminalsDaggerAny;
	ItemRequirement criminalsDagger;
	ItemRequirement criminalsDaggerFlour;
	ItemRequirement criminalsThread;
	ItemRequirement criminalsThread1;
	ItemRequirement criminalsThread2;
	ItemRequirement criminalsThread3;
	ItemRequirement twoFlypaper;
	ItemRequirement potOfFlourHighlighted;
	ItemRequirement flypaper;
	ItemRequirement unknownPrint;
	ItemRequirement silverNecklace;
	ItemRequirement silverBook;
	ItemRequirement silverBookFlour;
	ItemRequirement silverNecklaceFlour;
	ItemRequirement annaPrint;
	ItemRequirement davidPrint;
	ItemRequirement killersPrint;
	ItemRequirement silverNeedle;
	ItemRequirement silverPot;
	ItemRequirement silverNeedleFlour;
	ItemRequirement silverPotFlour;
	ItemRequirement elizabethPrint;
	ItemRequirement frankPrint;
	ItemRequirement criminalsDaggerHighlighted;
	ItemRequirement criminalsDaggerFlourHighlighted;
	ItemRequirement silverCup;
	ItemRequirement silverCupFlour;
	ItemRequirement silverBottle;
	ItemRequirement silverBottleFlour;
	ItemRequirement bobPrint;
	ItemRequirement carolPrint;

	// Zones
	Zone upstairs;

	// Miscellaneous requirements
	RuneliteRequirement heardAboutPoisonSalesman;
	RuneliteRequirement talkedToPoisonSalesman;
	RuneliteRequirement hadThread;
	RuneliteRequirement hadPot;
	RuneliteRequirement hadKillerPrint;

	VarplayerRequirement annaGuilty;
	VarplayerRequirement bobGuilty;
	VarplayerRequirement carolGuilty;
	VarplayerRequirement davidGuilty;
	VarplayerRequirement elizabethGuilty;
	VarplayerRequirement frankGuilty;
	Conditions hasCriminalSilverItem;
	ZoneRequirement isUpstairs;
	Requirement hasSuspectPrint;
	Requirement hasSilverItemFlour;

	Conditions talkedToAnna;
	Conditions talkedToBob;
	Conditions talkedToCarol;
	Conditions talkedToDavid;
	Conditions talkedToElizabeth;
	Conditions talkedToFrank;
	RuneliteRequirement talkedToSuspect;
	WidgetTextRequirement pleaseWaitRequirement;

	Requirement checkedAnna;
	Requirement checkedBob;
	Requirement checkedCarol;
	Requirement checkedDavid;
	Requirement checkedElizabeth;
	Requirement checkedFrank;
	Requirement checkedSuspect;

	// Steps
	QuestStep talkToGuard;
	QuestStep talkToGossip;
	QuestStep talkToPoisonSalesman;
	QuestStep pickUpDagger;
	QuestStep pickUpPungentPot;
	QuestStep searchWindowForThread;
	QuestStep fillPotWithFlour;
	QuestStep useFlourOnDagger;
	QuestStep collectTwoFlypaper;
	QuestStep useFlypaperOnDagger;
	QuestStep searchAnnasBarrel;
	QuestStep searchDavidsBarrel;
	QuestStep searchFranksBarrel;
	QuestStep searchElizabethsBarrel;
	QuestStep searchBobsBarrel;
	QuestStep searchCarolsBarrel;
	QuestStep remainingSteps;
	QuestStep finishQuest;

	QuestStep useFlourOnNecklace;
	QuestStep useFlourOnCup;
	QuestStep useFlourOnBottle;
	QuestStep useFlourOnBook;
	QuestStep useFlourOnNeedle;
	QuestStep useFlourOnPot;

	QuestStep useFlypaperOnNecklace;
	QuestStep useFlypaperOnCup;
	QuestStep useFlypaperOnBottle;
	QuestStep useFlypaperOnBook;
	QuestStep useFlypaperOnNeedle;
	QuestStep useFlypaperOnPot;

	QuestStep collectFlypaper;
	QuestStep fillPotWithFlourForSilver;

	QuestStep compareAnna;
	QuestStep compareBob;
	QuestStep compareCarol;
	QuestStep compareDavid;
	QuestStep compareElizabeth;
	QuestStep compareFrank;

	QuestStep talkToAnna;
	QuestStep talkToBob;
	QuestStep talkToFrank;
	QuestStep talkToDavid;
	QuestStep talkToCarol;
	QuestStep talkToElizabeth;

	QuestStep searchAnna;
	QuestStep searchBob;
	QuestStep searchCarol;
	QuestStep searchDavid;
	QuestStep searchElizabeth;
	QuestStep searchFrank;

	ConditionalStep useFlour;
	ConditionalStep searchBarrel;
	ConditionalStep useFlypaper;
	ConditionalStep comparePrints;
	ConditionalStep talkToSuspect;
	ConditionalStep searchSuspectItem;

	@Override
	protected void setupZones()
	{
		upstairs = new Zone(new WorldPoint(2727, 3571, 1), new WorldPoint(2752, 3585, 1));
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

		/* Thread 1 item */
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

	public void setupConditions()
	{
		isUpstairs = new ZoneRequirement(upstairs);

		hadThread = new RuneliteRequirement(configManager, "murdermysteryhadthread", criminalsThread.alsoCheckBank(questBank));
		hadPot = new RuneliteRequirement(configManager, "murdermysteryhadpot", pungentPot.alsoCheckBank(questBank));
		heardAboutPoisonSalesman = new RuneliteRequirement(configManager, "murdermysterytalkedtogossip",
			new Conditions(true, new DialogRequirement("Especially as I heard that the poison salesman in the Seers' village made a big sale to one of the family the other day."))
		);
		talkedToPoisonSalesman = new RuneliteRequirement(configManager, "murdermysterytalkedtopoisonsalesman",
			new Conditions(true, LogicType.OR,
				new DialogRequirement(questHelperPlugin.getPlayerStateManager().getPlayerName(), "Uh... no, it's ok.", false),
				new DialogRequirement("Anna, Bob, Carol, David, Elizabeth and Frank all bought a bottle! " +
					"In fact they bought the last of my supplies!")
			));

		annaGuilty = new VarplayerRequirement(VarPlayerID.MURDERSUS, 1);
		bobGuilty = new VarplayerRequirement(VarPlayerID.MURDERSUS, 2);
		carolGuilty = new VarplayerRequirement(VarPlayerID.MURDERSUS, 3);
		davidGuilty = new VarplayerRequirement(VarPlayerID.MURDERSUS, 4);
		elizabethGuilty = new VarplayerRequirement(VarPlayerID.MURDERSUS, 5);
		frankGuilty = new VarplayerRequirement(VarPlayerID.MURDERSUS, 6);

		hasCriminalSilverItem = or(
			new Conditions(annaGuilty, silverNecklace),
			new Conditions(bobGuilty, silverCup),
			new Conditions(carolGuilty, silverBottle),
			new Conditions(davidGuilty, silverBook),
			new Conditions(elizabethGuilty, silverNeedle),
			new Conditions(frankGuilty, silverPot)
		);

		hasSuspectPrint = or(
			new Conditions(annaGuilty, annaPrint),
			new Conditions(bobGuilty, bobPrint),
			new Conditions(carolGuilty, carolPrint),
			new Conditions(davidGuilty, davidPrint),
			new Conditions(elizabethGuilty, elizabethPrint),
			new Conditions(frankGuilty, frankPrint)
		);

		hadKillerPrint = new RuneliteRequirement(configManager, "murdermysteryhadkillerprint",
			new Conditions(killersPrint)
		);

		hasSilverItemFlour = new Conditions(
			LogicType.OR,
			and(annaGuilty, silverNecklaceFlour),
			and(bobGuilty, silverCupFlour),
			and(carolGuilty, silverBottleFlour),
			and(davidGuilty, silverBookFlour),
			and(elizabethGuilty, silverNeedleFlour),
			and(frankGuilty, silverPotFlour)
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
		talkedToSuspect = new RuneliteRequirement(configManager, "murdermysterytalkedtosuspect",
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
		checkedSuspect = new RuneliteRequirement(configManager, "murdermysterydisprovedsuspect",
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
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToGuard);

		var doesntNeedMoreFlypaper = new Conditions(LogicType.OR, twoFlypaper, and(flypaper, unknownPrint));

		var hadPotAndThread = and(hadThread, hadPot);

		var checkingPrintSteps = new ConditionalStep(this, pickUpDagger);
		/* compare prints */
		checkingPrintSteps.addStep(and(unknownPrint, hasSuspectPrint), comparePrints);
		checkingPrintSteps.addStep(and(unknownPrint, hasSilverItemFlour, flypaper), useFlypaper);
		checkingPrintSteps.addStep(and(unknownPrint, hasCriminalSilverItem, flypaper, potOfFlourHighlighted), useFlour);
		checkingPrintSteps.addStep(and(unknownPrint, hasCriminalSilverItem, flypaper), fillPotWithFlourForSilver);
		checkingPrintSteps.addStep(and(unknownPrint, hasCriminalSilverItem), collectFlypaper);
		/* Unknown print */
		checkingPrintSteps.addStep(and(criminalsDaggerFlour, hasCriminalSilverItem), useFlypaperOnDagger);
		checkingPrintSteps.addStep(and(criminalsDaggerAny, hasCriminalSilverItem, potOfFlourHighlighted), useFlourOnDagger);
		checkingPrintSteps.addStep(and(criminalsDaggerAny, doesntNeedMoreFlypaper, hasCriminalSilverItem), fillPotWithFlour);
		checkingPrintSteps.addStep(and(criminalsDaggerAny, doesntNeedMoreFlypaper), searchBarrel);
		checkingPrintSteps.addStep(criminalsDaggerAny, collectTwoFlypaper);

		var investigating = new ConditionalStep(this, searchWindowForThread);
		investigating.addStep(and(hadPotAndThread, hadKillerPrint, checkedSuspect), finishQuest);
		investigating.addStep(and(hadPotAndThread, hadKillerPrint, talkedToSuspect), searchSuspectItem);
		investigating.addStep(and(hadPotAndThread, hadKillerPrint, talkedToPoisonSalesman), talkToSuspect);
		investigating.addStep(and(hadPotAndThread, hadKillerPrint, heardAboutPoisonSalesman), talkToPoisonSalesman);
		investigating.addStep(and(hadPotAndThread, hadKillerPrint), talkToGossip);
		investigating.addStep(hadPotAndThread, checkingPrintSteps); /* Get dagger print */
		investigating.addStep(hadThread, pickUpPungentPot);

		steps.put(1, investigating);

		return steps;
	}

	public void setupSteps()
	{
		talkToGuard = new NpcStep(this, NpcID.MURDERGUARD, new WorldPoint(2741, 3561, 0), "Talk to the Guard in the Sinclair Manor north of Camelot.");
		talkToGuard.addDialogSteps("Yes.");

		pickUpPungentPot = new ItemStep(this, new WorldPoint(2747, 3579, 0), "Enter the mansion and pick up the pungent pot inside the east room.", pungentPot);
		pickUpDagger = new ItemStep(this, new WorldPoint(2746, 3578, 0), "Pick up the criminal's dagger.", criminalsDaggerAny);
		searchWindowForThread = new ObjectStep(this, ObjectID.MURDERWINDOW, new WorldPoint(2748, 3577, 0), "Search the window for a clothing thread. The colour of the thread will match the killer's trousers.", criminalsThread);
		var actuallyFillPotWithFlour = new ObjectStep(this, ObjectID.FLOURBARREL, new WorldPoint(2733, 3582, 0), "", pot);
		var goDownstairs = new ObjectStep(this, ObjectID.MURDER_QIP_SPIRALSTAIRSTOP, new WorldPoint(2736, 3581, 1), "Head downstairs to the ground floor.");
		fillPotWithFlour = new ConditionalStep(this, actuallyFillPotWithFlour, "Fill your pot with flour from the barrel in the mansion's kitchen.");
		((ConditionalStep) fillPotWithFlour).addStep(isUpstairs, goDownstairs);
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
		talkToGossip.addDialogStep("Who do you think was responsible?");

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
		((ConditionalStep) searchDavid).addStep(isUpstairs, new ObjectStep(this, ObjectID.MURDERWEB, new WorldPoint(2740, 3574, 1), ""));
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

		var useFlourFallback = new DetailedQuestStep(this, "Use flour on the suspect's item.");
		useFlour = new ConditionalStep(this, useFlourFallback);
		useFlour.setShouldPassthroughText(true);
		useFlour.addStep(elizabethGuilty, useFlourOnNeedle);
		useFlour.addStep(annaGuilty, useFlourOnNecklace);
		useFlour.addStep(carolGuilty, useFlourOnBottle);
		useFlour.addStep(davidGuilty, useFlourOnBook);
		useFlour.addStep(frankGuilty, useFlourOnPot);
		useFlour.addStep(bobGuilty, useFlourOnCup);

		var searchBarrelFallback = new DetailedQuestStep(this, "Search the suspect's barrel.");
		searchBarrel = new ConditionalStep(this, searchBarrelFallback);
		searchBarrel.setShouldPassthroughText(true);
		searchBarrel.addStep(elizabethGuilty, searchElizabethsBarrel);
		searchBarrel.addStep(annaGuilty, searchAnnasBarrel);
		searchBarrel.addStep(carolGuilty, searchCarolsBarrel);
		searchBarrel.addStep(davidGuilty, searchDavidsBarrel);
		searchBarrel.addStep(frankGuilty, searchFranksBarrel);
		searchBarrel.addStep(bobGuilty, searchBobsBarrel);

		var useFlypaperFallback = new DetailedQuestStep(this, "Use flypaper on the suspect's item.");
		useFlypaper = new ConditionalStep(this, useFlypaperFallback);
		useFlypaper.setShouldPassthroughText(true);
		useFlypaper.addStep(elizabethGuilty, useFlypaperOnNeedle);
		useFlypaper.addStep(annaGuilty, useFlypaperOnNecklace);
		useFlypaper.addStep(carolGuilty, useFlypaperOnBottle);
		useFlypaper.addStep(davidGuilty, useFlypaperOnBook);
		useFlypaper.addStep(frankGuilty, useFlypaperOnPot);
		useFlypaper.addStep(bobGuilty, useFlypaperOnCup);

		var comparePrintsFallback = new DetailedQuestStep(this, "Compare prints with the suspect.");
		comparePrints = new ConditionalStep(this, comparePrintsFallback);
		comparePrints.setShouldPassthroughText(true);
		comparePrints.addStep(annaGuilty, compareAnna);
		comparePrints.addStep(bobGuilty, compareBob);
		comparePrints.addStep(carolGuilty, compareCarol);
		comparePrints.addStep(davidGuilty, compareDavid);
		comparePrints.addStep(elizabethGuilty, compareElizabeth);
		comparePrints.addStep(frankGuilty, compareFrank);

		var talkToSuspectFallback = new DetailedQuestStep(this, "Talk to the suspect.");
		talkToSuspect = new ConditionalStep(this, talkToSuspectFallback);
		talkToSuspect.setShouldPassthroughText(true);
		talkToSuspect.addStep(elizabethGuilty, talkToElizabeth);
		talkToSuspect.addStep(carolGuilty, talkToCarol);
		talkToSuspect.addStep(davidGuilty, talkToDavid);
		talkToSuspect.addStep(frankGuilty, talkToFrank);
		talkToSuspect.addStep(bobGuilty, talkToBob);
		talkToSuspect.addStep(annaGuilty, talkToAnna);
		talkToSuspect.addDialogStep("Why'd you buy poison the other day?");

		var searchSuspectItemFallback = new DetailedQuestStep(this, "Disprove the suspect's alibi by checking the thing they claimed to clean.");
		searchSuspectItem = new ConditionalStep(this, searchSuspectItemFallback);
		searchSuspectItem.setShouldPassthroughText(true);
		searchSuspectItem.addStep(elizabethGuilty, searchElizabeth);
		searchSuspectItem.addStep(carolGuilty, searchCarol);
		searchSuspectItem.addStep(davidGuilty, searchDavid);
		searchSuspectItem.addStep(frankGuilty, searchFrank);
		searchSuspectItem.addStep(bobGuilty, searchBob);
		searchSuspectItem.addStep(annaGuilty, searchAnna);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			pot
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 1406)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 2000)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var allSteps = new ArrayList<PanelDetails>();

		allSteps.add(new PanelDetails("Go to the Sinclair Manor", List.of(
			talkToGuard
		), List.of(
			pot
		)));

		allSteps.add(new PanelDetails("Collect evidence", List.of(
			searchWindowForThread,
			pickUpPungentPot,
			pickUpDagger
		)));

		allSteps.add(new PanelDetails("Collect fingerprints", List.of(
			collectTwoFlypaper,
			searchBarrel,
			fillPotWithFlour,
			useFlourOnDagger,
			useFlypaperOnDagger,
			fillPotWithFlourForSilver,
			useFlour,
			useFlypaper,
			comparePrints
		)));

		allSteps.add(new PanelDetails("Finishing off", List.of(
			talkToGossip,
			talkToPoisonSalesman,
			talkToSuspect,
			searchSuspectItem,
			finishQuest
		)));

		return allSteps;
	}
}
