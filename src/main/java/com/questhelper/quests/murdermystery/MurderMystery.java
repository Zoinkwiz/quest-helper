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
package com.questhelper.quests.murdermystery;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.conditional.WidgetTextCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import net.runelite.api.widgets.WidgetInfo;

@QuestDescriptor(
	quest = QuestHelperQuest.MURDER_MYSTERY
)
public class MurderMystery extends BasicQuestHelper
{
	private ItemRequirement pot, pot3, pungentPot, criminalsDaggerAny, criminalsDagger, criminalsDaggerFlour, criminalsThread, criminalsThread1, criminalsThread2, criminalsThread3,
		threeFlypaper, potOfFlourHighlighted, flypaper, unknownPrint, silverNecklace, silverBook, silverBookFlour, silverNecklaceFlour, annasPrint, davidsPrint, killersPrint, silverNeedle,
		silverPot, silverNeedleFlour, silverPotFlour, elizabethPrint, frankPrint, criminalsDaggerHighlighted, criminalsDaggerFlourHighlighted, silverCup, silverCupFlour, silverBottle,
		silverBottleFlour, bobPrint, carolPrint;

	private ConditionForStep hasCriminalsThreadAny, hasCriminalsDagger, hasPungentPot, hasThreeFlypaper, hasCriminalsDaggerNoFlour, hasCriminalsDaggerWithflour,
		hasCriminalsThread1, hasCriminalsThread2, hasCriminalsThread3, hasPotOfFlour, hasUnknownPrint, hasSilverNecklace, hasSilverBook, hasAnyThread2Item,
		hasKillersPrint, hasSilverNeedle, hasSilverPot, hasAnyThread1Item, hasAnyThread3Item, heardAboutPoisonSalesman, talkedToPoisonSalesman, hasSilverBottle,
		hasSilverCup;

	private QuestStep talkToGuard, talkToGossip, talkToPoisonSalesman, pickUpDagger, pickUpPungentPot, searchWindowForThread, fillPotWithFlour, useFlourOnDagger, collectThreeFlypaper,
		useFlypaperOnDagger, getSilverItems, searchAnnasBarrel, searchDavidsBarrel, compareSilverToMurdererPrint, getAndComparePrintsOfNecklaceOrBook, remainingSteps,
		talkToTheSuspect, disproveSuspectStory, finishQuest, searchFranksBarrel, searchElizabethsBarrel, getAndComparePrintsOfNeeedleOrPot, searchBobsBarrel,
		searchCarolsBarrel, getAndComparePrintsOfCupOrBottle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToGuard);

		/*
		  Starting quest, 195 0->1

		  173 0->1, may be unrelated. Had it at 1 for flypaper on dagger

		*/

		// Thread2, get Anna or David's items

		ConditionalStep investigating = new ConditionalStep(this, collectThreeFlypaper);
		investigating.addStep(new Conditions(hasKillersPrint), new SolvingTheCrimeStep(this, remainingSteps));

		/* compare prints */
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread1, hasUnknownPrint, hasAnyThread1Item), getAndComparePrintsOfCupOrBottle);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread2, hasUnknownPrint, hasAnyThread2Item), getAndComparePrintsOfNecklaceOrBook);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread3, hasUnknownPrint, hasAnyThread3Item), getAndComparePrintsOfNeeedleOrPot);

		/* Get dagger print */
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerWithflour, hasCriminalsThreadAny), useFlypaperOnDagger);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerNoFlour, hasCriminalsThreadAny, hasPotOfFlour), useFlourOnDagger);

		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerNoFlour, hasCriminalsThread1, hasSilverCup, hasSilverBottle), fillPotWithFlour);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerNoFlour, hasCriminalsThread2, hasSilverNecklace, hasSilverBook), fillPotWithFlour);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerNoFlour, hasCriminalsThread3, hasSilverNeedle, hasSilverPot), fillPotWithFlour);

		/* Getting silver items for thread 1 */
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread1, hasSilverBottle), searchBobsBarrel);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread1), searchCarolsBarrel);
		/* Getting silver items for thread 2 */
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread3, hasSilverNeedle), searchFranksBarrel);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread3), searchElizabethsBarrel);
		/* Getting silver items for thread 3 */
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread2, hasSilverNecklace), searchDavidsBarrel);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread2), searchAnnasBarrel);

		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger), searchWindowForThread);
		investigating.addStep(hasCriminalsDagger, pickUpPungentPot);
		investigating.addStep(new Conditions(LogicType.OR, hasThreeFlypaper, hasCriminalsThreadAny, hasPungentPot), pickUpDagger);

		steps.put(1, investigating);

		// 195 0->4
		// Green thread
		// David responsible

		// 195 0->5
		// Blue thread
		// Elizabeth is responsible

		return steps;
	}

	public void setupConditions()
	{
		hasCriminalsThreadAny = new ItemRequirementCondition(criminalsThread);
		hasCriminalsThread1 = new ItemRequirementCondition(criminalsThread1);
		hasCriminalsThread2 = new ItemRequirementCondition(criminalsThread2);
		hasCriminalsThread3 = new ItemRequirementCondition(criminalsThread3);
		hasCriminalsDagger = new ItemRequirementCondition(criminalsDaggerAny);
		hasCriminalsDaggerNoFlour = new ItemRequirementCondition(criminalsDagger);
		hasCriminalsDaggerWithflour = new ItemRequirementCondition(criminalsDaggerFlour);
		hasPungentPot = new ItemRequirementCondition(pungentPot);
		hasPotOfFlour = new ItemRequirementCondition(potOfFlourHighlighted);

		hasUnknownPrint = new ItemRequirementCondition(unknownPrint);

		hasSilverBottle = new ItemRequirementCondition(silverBottle);
		hasSilverCup = new ItemRequirementCondition(silverCup);

		hasSilverBook = new ItemRequirementCondition(silverBook);
		hasSilverNecklace = new ItemRequirementCondition(silverNecklace);

		hasSilverNeedle = new ItemRequirementCondition(silverNeedle);
		hasSilverPot = new ItemRequirementCondition(silverPot);

		hasAnyThread1Item = new ItemRequirementCondition(LogicType.OR, silverCupFlour, silverCup, silverBottleFlour, silverBottle, bobPrint, carolPrint);
		hasAnyThread2Item = new ItemRequirementCondition(LogicType.OR, silverBookFlour, silverBook, silverNecklaceFlour, silverNecklace, annasPrint, davidsPrint);
		hasAnyThread3Item = new ItemRequirementCondition(LogicType.OR, silverNeedleFlour, silverNeedle, silverPotFlour, silverPot, elizabethPrint, frankPrint);

		hasThreeFlypaper = new ItemRequirementCondition(threeFlypaper);

		hasKillersPrint = new ItemRequirementCondition(killersPrint);

		heardAboutPoisonSalesman = new Conditions(true, new WidgetTextCondition(WidgetInfo.DIALOG_NPC_TEXT, "Especially as I heard that the poison salesman in the<br>Seers' village made a big sale to one of the family the<br>other day."));
		talkedToPoisonSalesman = new Conditions(true, new WidgetTextCondition(217, 4, "Uh... no, it's ok."));
	}

	public void setupItemRequirements()
	{
		pot = new ItemRequirement("Pot", ItemID.POT);
		pot3 = new ItemRequirement("Pot", ItemID.POT, 3);
		pungentPot = new ItemRequirement("Pungent pot", ItemID.PUNGENT_POT);

		criminalsDaggerAny = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER);
		criminalsDaggerAny.addAlternates(ItemID.CRIMINALS_DAGGER_1814);

		criminalsDagger = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER);
		criminalsDaggerHighlighted = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER);
		criminalsDaggerHighlighted.setHighlightInInventory(true);

		criminalsDaggerFlour = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER_1814);
		criminalsDaggerFlourHighlighted = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER_1814);
		criminalsDaggerFlourHighlighted.setHighlightInInventory(true);

		criminalsThread = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD);
		criminalsThread.addAlternates(ItemID.CRIMINALS_THREAD_1809, ItemID.CRIMINALS_THREAD_1810);
		criminalsThread1 = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD);
		criminalsThread2 = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD_1809);
		criminalsThread3 = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD_1810);
		threeFlypaper = new ItemRequirement("Flypaper", ItemID.FLYPAPER, 3);
		flypaper = new ItemRequirement("Flypaper", ItemID.FLYPAPER);
		flypaper.setHighlightInInventory(true);
		flypaper.setTip("You can get more from the sack in the shed on the west of the Sinclair Mansion");
		potOfFlourHighlighted = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
		potOfFlourHighlighted.setHighlightInInventory(true);
		unknownPrint = new ItemRequirement("Unknown print", ItemID.UNKNOWN_PRINT);

		/* Thread 1 items */
		bobPrint = new ItemRequirement("Bob's print", ItemID.BOBS_PRINT);
		carolPrint = new ItemRequirement("Carol's print", ItemID.CAROLS_PRINT);
		silverCup = new ItemRequirement("Silver cup", ItemID.SILVER_CUP);
		silverCupFlour = new ItemRequirement("Silver cup", ItemID.SILVER_CUP_1799);
		silverBottle = new ItemRequirement("Silver bottle", ItemID.SILVER_BOTTLE);
		silverBottleFlour = new ItemRequirement("Silver bottle", ItemID.SILVER_BOTTLE_1801);

		/* Thread 2 items */
		annasPrint = new ItemRequirement("Anna's print", ItemID.ANNAS_PRINT);
		davidsPrint = new ItemRequirement("David's print", ItemID.DAVIDS_PRINT);
		silverNecklace = new ItemRequirement("Silver necklace", ItemID.SILVER_NECKLACE);
		silverNecklaceFlour = new ItemRequirement("Silver necklace", ItemID.SILVER_NECKLACE_1797);
		silverBook = new ItemRequirement("Silver book", ItemID.SILVER_BOOK);
		silverBookFlour = new ItemRequirement("Silver book", ItemID.SILVER_BOOK_1803);

		/* Thread 3 items */
		elizabethPrint = new ItemRequirement("Elizabeth's print", ItemID.ELIZABETHS_PRINT);
		frankPrint = new ItemRequirement("Frank's print", ItemID.FRANKS_PRINT);
		silverNeedle = new ItemRequirement("Silver needle", ItemID.SILVER_NEEDLE);
		silverNeedleFlour = new ItemRequirement("Silver needle", ItemID.SILVER_NEEDLE_1805);
		silverPot = new ItemRequirement("Silver needle", ItemID.SILVER_POT);
		silverPotFlour = new ItemRequirement("Silver needle", ItemID.SILVER_POT_1807);

		killersPrint = new ItemRequirement("Killer's print", ItemID.KILLERS_PRINT);
	}

	public void setupSteps()
	{
		talkToGuard = new NpcStep(this, NpcID.GUARD_4218, new WorldPoint(2741, 3561, 0), "Talk to the Guard in the Sinclair Manor north of Camelot.");
		talkToGuard.addDialogStep("Sure, I'll help.");

		pickUpPungentPot = new DetailedQuestStep(this, new WorldPoint(2747, 3579, 0), "Enter the mansion and pick up the pungent pot inside the east room.", pungentPot);
		pickUpDagger = new DetailedQuestStep(this, new WorldPoint(2746, 3578, 0), "Pick up the criminal's dagger.", criminalsDaggerAny);
		searchWindowForThread = new ObjectStep(this, NullObjectID.NULL_26123, new WorldPoint(2748, 3577, 0), "Search the window for a clothing thread. The colour of the thread will match the killer's trousers.", criminalsThread);
		fillPotWithFlour = new ObjectStep(this, ObjectID.BARREL_OF_FLOUR_26122, new WorldPoint(2733, 3582, 0), "Fill your pot with flour from the barrel in the mansion's kitchen.", pot);
		useFlourOnDagger = new DetailedQuestStep(this, "Use the pot of flour on the Criminal's dagger.", potOfFlourHighlighted, criminalsDaggerHighlighted);
		collectThreeFlypaper = new ObjectStep(this, ObjectID.SACKS_2663, new WorldPoint(2731, 3582, 0), "Investigate the sacks in the shed for flypaper. Get 3 pieces.", threeFlypaper);
		collectThreeFlypaper.addDialogStep("Yes, it might be useful.");

		// Option 1 (red thread)
		searchBobsBarrel = new ObjectStep(this, ObjectID.BOBS_BARREL, new WorldPoint(2735, 3579, 0), "Search Bob's barrel on the ground floor.");
		searchCarolsBarrel = new ObjectStep(this, ObjectID.CAROLS_BARREL, new WorldPoint(2733, 3580, 1), "Search Carol's barrel upstairs.");

		// Option 2 (green thread)
		searchAnnasBarrel = new ObjectStep(this, ObjectID.ANNAS_BARREL, new WorldPoint(2733, 3575, 0), "Search Anna's barrel on the ground floor.");
		searchDavidsBarrel = new ObjectStep(this, ObjectID.DAVIDS_BARREL, new WorldPoint(2733, 3577, 1), "Search David's barrel upstairs.");

		// Option 3 (blue thread)
		searchFranksBarrel = new ObjectStep(this, ObjectID.FRANKS_BARREL, new WorldPoint(2747, 3577, 1), "Search Frank's barrel upstairs.");
		searchElizabethsBarrel = new ObjectStep(this, ObjectID.ELIZABETHS_BARREL, new WorldPoint(2747, 3581, 1), "Search Elizabeth's barrel upstairs.");


		getSilverItems = new DetailedQuestStep(this, "Search the barrel of both the potential suspects for their respective silver items.");
		getSilverItems.addSubSteps(searchAnnasBarrel, searchDavidsBarrel, searchElizabethsBarrel, searchFranksBarrel);

		useFlypaperOnDagger = new DetailedQuestStep(this, "Use the flypaper on the dagger.", flypaper, criminalsDaggerFlourHighlighted);


		getAndComparePrintsOfCupOrBottle = new DetailedQuestStep(this,
			"Use flour on the silver cup and silver bottle, and use the flypaper on them to get prints. Use these prints on the unknown print to identify the murderer.",
			flypaper);

		getAndComparePrintsOfNecklaceOrBook = new DetailedQuestStep(this,
			"Use flour on the silver book and silver necklace, and use the flypaper on them to get prints. Use these prints on the unknown print to identify the murderer.",
			flypaper);

		getAndComparePrintsOfNeeedleOrPot = new DetailedQuestStep(this,
			"Use flour on the silver needle and silver pot, and use the flypaper on them to get prints. Use these prints on the unknown print to identify the murderer.",
			flypaper);


		compareSilverToMurdererPrint = new DetailedQuestStep(this, "Use flour on both the silver items, then use flypaper on them. Use the prints you get on the Murderer's print to identify the murderer.");
		compareSilverToMurdererPrint.addSubSteps(getAndComparePrintsOfNecklaceOrBook);


		talkToGossip = new NpcStep(this, NpcID.GOSSIP, new WorldPoint(2741, 3557, 0), "Talk to Gossip, just south of the Sinclair Mansion.");
		talkToGossip.addDialogStep(2,"Who do you think was responsible?");

		talkToPoisonSalesman = new NpcStep(this, NpcID.POISON_SALESMAN, new WorldPoint(2694, 3493, 0), "Talk to the Posion Salesman in the Seer's Village pub.");
		talkToPoisonSalesman.addDialogStep("Who did you sell Poison to at the house?");
		talkToPoisonSalesman.addDialogStep("Talk about the Murder Mystery Quest");
		talkToTheSuspect = new DetailedQuestStep(this, "Talk to the person who you matched prints to and ask what they did with the poison.");

		disproveSuspectStory = new DetailedQuestStep(this, "Search the item they say they used the poison on.");
		disproveSuspectStory.addDialogStep("Why'd you buy poison the other day?");

		finishQuest = new DetailedQuestStep(this, "Return to the guard outside the Sinclair Mansion and tell him your findings.");

		remainingSteps = new DetailedQuestStep(this, "Follow the steps in the Quest Helper sidebar for the rest of the quest.");
		remainingSteps.addDialogStep(2, "Who do you think was responsible?");
		remainingSteps.addDialogStep("Why'd you buy poison the other day?");
		remainingSteps.addDialogStep("Who did you sell Poison to at the house?");
		remainingSteps.addDialogStep("I know who did it!");
		remainingSteps.setShowInSidebar(false);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> required = new ArrayList<>();
		required.add(pot3);
		return required;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Go to the Sinclair Manor", new ArrayList<>(Collections.singletonList(talkToGuard)), pot3));
		allSteps.add(new PanelDetails("Collect evidence", new ArrayList<>(Arrays.asList(pickUpPungentPot, pickUpDagger, searchWindowForThread))));
		allSteps.add(new PanelDetails("Collect fingerprints", new ArrayList<>(Arrays.asList(collectThreeFlypaper, getSilverItems, fillPotWithFlour, useFlourOnDagger, useFlypaperOnDagger, compareSilverToMurdererPrint))));
		allSteps.add(new PanelDetails("Finishing off", new ArrayList<>(Arrays.asList(remainingSteps, talkToGossip, talkToPoisonSalesman, talkToTheSuspect, disproveSuspectStory, finishQuest))));
		return allSteps;
	}
}
