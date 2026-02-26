/*
 * Copyright (c) 2025, adamsbytes <https://github.com/adamsbytes>
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
package com.questhelper.helpers.quests.theidesofmilk;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.CombatLevelRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

public class TheIdesOfMilk extends BasicQuestHelper
{
	// Mid-quest item requirements
	ItemRequirement husbandryBook;
	ItemRequirement milkSample1;
	ItemRequirement milkSample2;

	// Miscellaneous requirements
	VarbitRequirement gillieInformed;

	// Steps
	NpcStep talkToCassius;
	NpcStep talkToGillie;
	NpcStep talkToSeth;
	ObjectStep searchShelves;
	NpcStep returnToCassiusWithBook;
	DetailedQuestStep drinkMilkSample1;
	NpcStep talkToCassiusAfterDrink;
	NpcStep talkToDuke;
	NpcStep talkToGillieAgain;
	DetailedQuestStep drinkMilkSample2;
	NpcStep talkToGillieAfterDrink;
	ObjectStep openBullPen;
	NpcStep talkToGillieAfterFight;
	NpcStep finishQuest;

	@Override
	protected void setupRequirements()
	{
		husbandryBook = new ItemRequirement("The groats principles", ItemID.COWQUEST_HUSBANDRY_BOOK);
		husbandryBook.canBeObtainedDuringQuest();

		milkSample1 = new ItemRequirement("Milk sample", ItemID.COWQUEST_MILK_SAMPLE_1);
		milkSample1.canBeObtainedDuringQuest();

		milkSample2 = new ItemRequirement("Milk sample", ItemID.COWQUEST_MILK_SAMPLE_2);
		milkSample2.canBeObtainedDuringQuest();

		gillieInformed = new VarbitRequirement(VarbitID.COWQUEST_GILLIE_INFORMATION, 1);
	}

	public void setupSteps()
	{
		talkToCassius = new NpcStep(this, NpcID.COWBOSS_FARMER,
			new WorldPoint(3171, 3277, 0),
			"Speak to Cassius by the lake north-west of Lumbridge to start the quest.", true);
		talkToCassius.addDialogStep("Yes.");

		talkToGillie = new NpcStep(this, NpcID.GILLIE_THE_MILKMAID,
			new WorldPoint(3253, 3270, 0),
			"Speak to Gillie Groats at the cow field east of Lumbridge.");
		talkToGillie.addDialogStep("Can you share what makes your cows so productive?");

		talkToSeth = new NpcStep(this, NpcID.FAVOUR_SETH_GROATS,
			new WorldPoint(3228, 3291, 0),
			"Speak to Seth Groats at his farmhouse east of the River Lum.");

		searchShelves = new ObjectStep(this, ObjectID.COWQUEST_SETH_SHELF,
			new WorldPoint(3227, 3287, 0),
			"Search the shelves in Seth's farmhouse for 'The groats principles'.");

		returnToCassiusWithBook = new NpcStep(this, NpcID.COWBOSS_FARMER,
			new WorldPoint(3171, 3277, 0),
			"Return to Cassius with the book.", true, husbandryBook);

		drinkMilkSample1 = new DetailedQuestStep(this,
			"Drink the milk sample near Cassius.", milkSample1.highlighted());

		talkToCassiusAfterDrink = new NpcStep(this, NpcID.COWBOSS_FARMER,
			new WorldPoint(3171, 3277, 0),
			"Speak to Cassius after drinking the milk sample.", true);

		talkToDuke = new NpcStep(this, NpcID.DUKE_OF_LUMBRIDGE,
			new WorldPoint(3210, 3220, 1),
			"Speak to Duke Horacio on the first floor of Lumbridge Castle.", milkSample2);

		talkToGillieAgain = new NpcStep(this, NpcID.GILLIE_THE_MILKMAID,
			new WorldPoint(3253, 3270, 0),
			"Speak to Gillie Groats at the cow field.");

		drinkMilkSample2 = new DetailedQuestStep(this,
			"Drink the milk sample near Gillie.", milkSample2.highlighted());

		talkToGillieAfterDrink = new NpcStep(this, NpcID.GILLIE_THE_MILKMAID,
			new WorldPoint(3253, 3270, 0),
			"Speak to Gillie Groats after drinking the milk sample.");

		openBullPen = new ObjectStep(this, ObjectID.FENCEGATE_L_COWBOSS_START,
			new WorldPoint(3262, 3294, 0),
			"Open the bull pen gate in the north-east corner of the cow field and defeat Brutus (level 30).");
		openBullPen.addDialogStep("Yes.");

		talkToGillieAfterFight = new NpcStep(this, NpcID.GILLIE_THE_MILKMAID,
			new WorldPoint(3253, 3270, 0),
			"Speak to Gillie Groats after defeating Brutus.");

		finishQuest = new NpcStep(this, NpcID.COWBOSS_FARMER,
			new WorldPoint(3171, 3277, 0),
			"Return to Cassius to complete the quest.", true);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToCassius);
		steps.put(1, talkToCassius);
		steps.put(2, talkToCassius);

		var cInvestigate = new ConditionalStep(this, talkToGillie);
		cInvestigate.addStep(gillieInformed, talkToSeth);
		steps.put(3, cInvestigate);

		var cGetBook = new ConditionalStep(this, searchShelves);
		cGetBook.addStep(husbandryBook, returnToCassiusWithBook);
		steps.put(4, cGetBook);
		steps.put(5, returnToCassiusWithBook);

		steps.put(6, drinkMilkSample1);
		steps.put(8, talkToCassiusAfterDrink);
		steps.put(10, talkToDuke);
		steps.put(12, talkToGillieAgain);
		steps.put(14, drinkMilkSample2);
		steps.put(16, talkToGillieAfterDrink);
		steps.put(18, openBullPen);
		steps.put(20, talkToGillieAfterFight);
		steps.put(21, finishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			new CombatLevelRequirement(15)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of("Brutus the bull (level 30)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to Brutus, the cow boss"),
			new UnlockReward("Cow bell amulet and magic lamp (1,000 XP combat/Prayer) from Gillie Groats")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting off", List.of(
			talkToCassius
		)));

		sections.add(new PanelDetails("Investigation", List.of(
			talkToGillie,
			talkToSeth,
			searchShelves
		)));

		sections.add(new PanelDetails("Milk tasting", List.of(
			returnToCassiusWithBook,
			drinkMilkSample1,
			talkToCassiusAfterDrink
		)));

		sections.add(new PanelDetails("The Duke's opinion", List.of(
			talkToDuke,
			talkToGillieAgain,
			drinkMilkSample2,
			talkToGillieAfterDrink
		)));

		sections.add(new PanelDetails("The bull fight", List.of(
			openBullPen,
			talkToGillieAfterFight
		)));

		sections.add(new PanelDetails("Finishing up", List.of(
			finishQuest
		)));

		return sections;
	}
}
