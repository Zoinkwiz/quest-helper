package com.questhelper.quests.murdermystery;

import com.questhelper.QuestHelperQuest;
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

@QuestDescriptor(
	quest = QuestHelperQuest.MURDER_MYSTERY
)
public class MurderMystery extends BasicQuestHelper
{
	private ItemRequirement pot, pot3, pungentPot, criminalsDagger, criminalsDagger1, criminalsDagger2, criminalsThread, criminalsThread1, criminalsThread2, criminalsThread3,
		threeFlypaper, potOfFlour, flypaper, unknownPrint, silverNecklace, silverBook, silverBookFlour, silverNecklaceFlour, annasPrint, davidsPrint, killersPrint;

	private ConditionForStep hasPot, hasCriminalsThread, hasCriminalsDagger, hasPungentPot, hasThreeFlypaper, hasCriminalsDaggerNoFlour, hasCriminalsDaggerWithflour,
		hasCriminalsThread1, hasCriminalsThread2, hasCriminalsThread3, hasPotOfFlour, hasFlypaper, hasUnknownPrint, hasSilverNecklace,
		hasSilverBook, hasAnyThread2Item, hasKillersPrint;

	private QuestStep talkToGuard, talkToGossip, talkToPoisonSalesman, pickUpDagger, pickUpPungentPot, searchWindowForThread, fillPotWithFlour, useFlourOnDagger, collectThreeFlypaper,
	useFlypaperOnDagger, getSilverItems, searchAnnasBarrel, searchDavidsBarrel, compareSilverToMurdererPrint, getAndComparePrintsOfNecklaceOrBook, remainingSteps, talkToTheSuspect, disproveSuspectStory, finishQuest;

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

		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread3, hasUnknownPrint, hasSilver?), useFlourOnSilver?);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread3, hasUnknownPrint, hasSilver?), useFlourOnSilver?);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread1, hasUnknownPrint, hasSilver?), useFlourOnSilver?);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread1, hasUnknownPrint, hasSilver?), useFlourOnSilver?);

		investigating.addStep(new Conditions(hasKillersPrint), new SolvingTheCrimeStep(this, remainingSteps));

		/* Get silver book/necklace print */

		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsThread2, hasUnknownPrint, hasAnyThread2Item), getAndComparePrintsOfNecklaceOrBook);

		/* Get dagger print */
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerWithflour, hasCriminalsThread), useFlypaperOnDagger);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerNoFlour, hasCriminalsThread, hasPotOfFlour), useFlourOnDagger);

		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDaggerNoFlour, hasCriminalsThread2, hasSilverNecklace, hasSilverBook), fillPotWithFlour);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger1, hasCriminalsThread1, hasSilver?, hasSilver?), fillPotWithFlour);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger1, hasCriminalsThread3, hasSilver?, hasSilver?), fillPotWithFlour);

		/* These to be other thread options */
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread1, hasSilver?), search?sBarrel);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread1), search?sBarrel);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread3, hasSilver?), search?sBarrel);
		// investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread3), search?sBarrel);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread2, hasSilverNecklace), searchDavidsBarrel);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread2), searchAnnasBarrel);

		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread2), talkToPoisonSalesman);
		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger, hasCriminalsThread2), talkToGossip);

		investigating.addStep(new Conditions(hasPungentPot, hasCriminalsDagger), searchWindowForThread);
		investigating.addStep(hasCriminalsDagger, pickUpPungentPot);
		investigating.addStep(new Conditions(LogicType.OR, hasThreeFlypaper, hasCriminalsThread, hasPungentPot), pickUpDagger);

		steps.put(1, investigating);

		return steps;
	}

	public void setupConditions() {
		hasPot = new ItemRequirementCondition(pot);
		hasCriminalsThread = new ItemRequirementCondition(criminalsThread);
		hasCriminalsThread1 = new ItemRequirementCondition(criminalsThread1);
		hasCriminalsThread2 = new ItemRequirementCondition(criminalsThread2);
		hasCriminalsThread3 = new ItemRequirementCondition(criminalsThread3);
		hasCriminalsDagger = new ItemRequirementCondition(criminalsDagger);
		hasCriminalsDaggerNoFlour = new ItemRequirementCondition(criminalsDagger1);
		hasCriminalsDaggerWithflour = new ItemRequirementCondition(criminalsDagger2);
		hasPungentPot = new ItemRequirementCondition(pungentPot);
		hasFlypaper = new ItemRequirementCondition(flypaper);
		hasPotOfFlour = new ItemRequirementCondition(potOfFlour);

		hasUnknownPrint = new ItemRequirementCondition(unknownPrint);

		hasSilverBook = new ItemRequirementCondition(silverBook);
		hasSilverNecklace = new ItemRequirementCondition(silverNecklace);
		hasAnyThread2Item = new ItemRequirementCondition(LogicType.OR, silverBookFlour, silverBook, silverNecklaceFlour, silverNecklace, annasPrint, davidsPrint);

		hasThreeFlypaper = new ItemRequirementCondition(threeFlypaper);

		hasKillersPrint = new ItemRequirementCondition(killersPrint);
	}

	public void setupItemRequirements() {
		pot = new ItemRequirement("Pot", ItemID.POT);
		pot3 = new ItemRequirement("Pot", ItemID.POT, 3);
		pungentPot = new ItemRequirement("Pungent pot", ItemID.PUNGENT_POT);
		// I have dagger 1
		criminalsDagger = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER);
		criminalsDagger.addAlternates(ItemID.CRIMINALS_DAGGER_1814);
		criminalsDagger1 = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER);
		criminalsDagger2 = new ItemRequirement("Criminal's dagger", ItemID.CRIMINALS_DAGGER_1814);
		criminalsThread = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD);
		criminalsThread.addAlternates(ItemID.CRIMINALS_THREAD_1809, ItemID.CRIMINALS_THREAD_1810);
		criminalsThread1 = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD);
		// I have 2. Green thread, so David/Anna
		criminalsThread2 = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD_1809);
		criminalsThread3 = new ItemRequirement("Criminal's thread", ItemID.CRIMINALS_THREAD_1810);
		threeFlypaper = new ItemRequirement("Flypaper", ItemID.FLYPAPER, 3);
		flypaper = new ItemRequirement("Flypaper", ItemID.FLYPAPER);
		flypaper.setTip("You can get more from the sack in the shed on the west of the Sinclair Mansion");
		potOfFlour = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
		unknownPrint = new ItemRequirement("Unknown print", ItemID.UNKNOWN_PRINT);
		silverNecklace = new ItemRequirement("Silver necklace", ItemID.SILVER_NECKLACE);
		silverNecklaceFlour = new ItemRequirement("Silver necklace", ItemID.SILVER_NECKLACE_1797);
		silverBook = new ItemRequirement("Silver book", ItemID.SILVER_BOOK);
		silverBookFlour = new ItemRequirement("Silver book", ItemID.SILVER_BOOK_1803);
		killersPrint = new ItemRequirement("Killer's print", ItemID.KILLERS_PRINT);
		annasPrint = new ItemRequirement("Anna's print", ItemID.ANNAS_PRINT);
		davidsPrint = new ItemRequirement("David's print", ItemID.DAVIDS_PRINT);
	}

	public void setupSteps()
	{
		talkToGuard = new NpcStep(this, NpcID.GUARD_4218, new WorldPoint(2741, 3561, 0), "Talk to the Guard in the Sinclair Manor north of Camelot.");
		talkToGuard.addDialogStep("Sure, I'll help.");

		pickUpPungentPot = new DetailedQuestStep(this, new WorldPoint(2747, 3579, 0), "Enter the mansion and pick up the pungent pot inside the east room.", pungentPot);
		pickUpDagger = new DetailedQuestStep(this, new WorldPoint(2746, 3578, 0), "Pick up the criminal's dagger.", criminalsDagger);
		searchWindowForThread = new ObjectStep(this, NullObjectID.NULL_26123, new WorldPoint(2748, 3577, 0), "Search the window for a clothing thread. The colour of the thread will match the killer's trousers.", criminalsThread);
		fillPotWithFlour = new ObjectStep(this, ObjectID.BARREL_OF_FLOUR_26122, new WorldPoint(2733, 3582, 0), "Fill your pot with flour.", pot);
		useFlourOnDagger = new DetailedQuestStep(this, "Use the pot of flour on the Criminal's dagger.");
		collectThreeFlypaper = new ObjectStep(this, ObjectID.SACKS_2663, new WorldPoint(2731, 3582, 0), "Investigate the sacks in the shed for flypaper. Get 3 pieces.", threeFlypaper);
		collectThreeFlypaper.addDialogStep("Yes, it might be useful.");


		searchAnnasBarrel = new ObjectStep(this, ObjectID.ANNAS_BARREL, new WorldPoint(2733, 3575, 0), "Search Anna's barrel on the ground floor.");
		searchDavidsBarrel = new ObjectStep(this, ObjectID.DAVIDS_BARREL, new WorldPoint(2733, 3577, 1), "Search David's barrel upstairs.");

		getSilverItems = new DetailedQuestStep(this, "Search the barrel of both the potential suspects for their respective silver items.");
		getSilverItems.addSubSteps(searchAnnasBarrel, searchDavidsBarrel);

		useFlypaperOnDagger = new DetailedQuestStep(this, "Use the flypaper on the dagger.", flypaper);

		getAndComparePrintsOfNecklaceOrBook = new DetailedQuestStep(this,
			"Use flour on the silver book and silver necklace, and use the flypaper on them to get prints. Use these prints on the unknown print to identify the murderer.",
			flypaper);

		compareSilverToMurdererPrint = new DetailedQuestStep(this, "Use flour on both the silver items, then use flypaper on them. Use the prints you get on the Murderer's print to identify the murderer.");
		compareSilverToMurdererPrint.addSubSteps(getAndComparePrintsOfNecklaceOrBook);


		talkToGossip = new NpcStep(this, NpcID.GOSSIP, new WorldPoint(2741, 3557, 0), "Talk to Gossip, just south of the Sinclair Mansion.");
		talkToGossip.addDialogStep("Who do you think was responsible?");

		talkToPoisonSalesman = new NpcStep(this, NpcID.POISON_SALESMAN, new WorldPoint(2694, 3493, 0), "Talk to the Posion Salesman in the Seer's Village pub.");
		talkToPoisonSalesman.addDialogStep("Who did you sell Poison to at the house?");
		talkToPoisonSalesman.addDialogStep("Talk about the Murder Mystery Quest");
		talkToTheSuspect = new DetailedQuestStep(this, "Talk to the person who you matched prints to and ask what they did with the poison.");

		disproveSuspectStory = new DetailedQuestStep(this, "Search the item they say they used the poison on.");
		disproveSuspectStory.addDialogStep("Why'd you buy poison the other day?");

		finishQuest = new DetailedQuestStep(this, "Return to the guard outside the Sinclair Mansion and tell him your findings.");

		remainingSteps = new DetailedQuestStep(this, "Follow the steps in the Quest Helper sidebar for the rest of the quest.");
		remainingSteps.addDialogStep("Who do you think was responsible?");
		remainingSteps.addDialogStep("Why'd you buy poison the other day?");
		remainingSteps.addDialogStep("I have conclusive proof who the killer was.");
		remainingSteps.setShowInSidebar(false);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements() {
		ArrayList<ItemRequirement> required = new ArrayList<>();
		required.add(pot3);
		return required;
	}

	@Override
	public ArrayList<PanelDetails> getPanels() {
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Go to the Sinclair Manor", new ArrayList<>(Collections.singletonList(talkToGuard)), pot3));
		allSteps.add(new PanelDetails("Collect evidence", new ArrayList<>(Arrays.asList(pickUpPungentPot, pickUpDagger, searchWindowForThread))));
		allSteps.add(new PanelDetails("Collect fingerprints", new ArrayList<>(Arrays.asList(collectThreeFlypaper, getSilverItems, fillPotWithFlour, useFlourOnDagger, useFlypaperOnDagger, compareSilverToMurdererPrint))));
		allSteps.add(new PanelDetails("Finishing off", new ArrayList<>(Arrays.asList(remainingSteps, talkToGossip, talkToPoisonSalesman, talkToTheSuspect, disproveSuspectStory, finishQuest))));
		return allSteps;
	}
}
