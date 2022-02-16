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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;

public class QuizSteps extends DetailedOwnerStep
{

	DetailedQuestStep answerQuestions, answerWeaponQuestion, answerRuneQuestion;

	ItemRequirement milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_OF_MILK);
	ItemRequirement flour = new ItemRequirement("Pot of flour", ItemID.POT_OF_FLOUR);
	ItemRequirement egg = new ItemRequirement("Egg", ItemID.EGG);

	String weaponChoice = "How many weapons were you shown?";
	String runeChoice = "How many types of rune were there?";


	public QuizSteps(QuestHelper questHelper)
	{
		super(questHelper, "Talk to Traiborn and complete his tests to enchant the ingredients.");
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateSteps();
	}

	@Override
	protected void updateSteps()
	{
		boolean eggEnchanted = client.getVarbitValue(1894) == 1;
		boolean flourEnchanted = client.getVarbitValue(1897) == 1;
		boolean milkEnchanted = client.getVarbitValue(1898) == 1;

		ArrayList<Requirement> items = new ArrayList<>();
		if (!eggEnchanted)
		{
			items.add(egg);
		}
		if (!milkEnchanted)
		{
			items.add(milk);
		}
		if (!flourEnchanted)
		{
			items.add(flour);
		}

		Widget currentDialog = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);

		if (currentDialog != null)
		{
			if (currentDialog.getText() != null)
			{
				if (currentDialog.getText().equals(weaponChoice))
				{
					answerWeaponQuestion.setRequirements(items);
					startUpStep(answerWeaponQuestion);
					return;
				}
				else if (currentDialog.getText().equals(runeChoice))
				{
					answerRuneQuestion.setRequirements(items);
					startUpStep(answerRuneQuestion);
					return;
				}
			}
		}

		answerQuestions.setRequirements(items);
		startUpStep(answerQuestions);
	}

	@Override
	protected void setupSteps()
	{
		answerQuestions = new NpcStep(getQuestHelper(), NpcID.TRAIBORN, new WorldPoint(3112, 3162, 1), "Talk to Traiborn.");
		answerQuestions.addDialogSteps("Ask about helping the Lumbridge Guide.", "Okay. Let's start!", "Okay. I'm ready!", "Quiz me!");
		answerQuestions.addDialogSteps("Unferth", "Gertrude", "King Lathas", "Pirate Pete", "Islwyn", "Hetty", "Professsor Gronigen", "Ali Morrisane", "Velorina", "Reldo");
		answerQuestions.addDialogSteps("Bandit camp", "Flour, Eggs and milk", "20", "Sand, bucket, soda ash, glass blowing pipe", "16", "North to South", "46", "2", "Keep Le Faye", "Dark Wizards'", "Catherby", "Legends'");
		answerQuestions.addDialogSteps("Trout", "Mind Talisman", "Guthix Prayer Book, Magic Logs, Pike");
		answerQuestions.addDialogStep(1, "10"); // Answer to bribe question
		answerQuestions.addDialogStep(3, "10"); // Answer to Fire runes question

		answerWeaponQuestion = new NpcStep(getQuestHelper(), NpcID.TRAIBORN, new WorldPoint(3112, 3162, 1), "Talk to Traiborn.");
		answerWeaponQuestion.addDialogStep("Three");
		answerRuneQuestion = new NpcStep(getQuestHelper(), NpcID.TRAIBORN, new WorldPoint(3112, 3162, 1), "Talk to Traiborn.");
		answerRuneQuestion.addDialogStep("Two");
	}

	@Override
	public Collection<QuestStep> getSteps()
	{
		return Arrays.asList(answerQuestions, answerRuneQuestion, answerWeaponQuestion);
	}
}
