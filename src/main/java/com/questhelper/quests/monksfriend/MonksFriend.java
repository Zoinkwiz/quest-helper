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
package com.questhelper.quests.monksfriend;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
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
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.MONKS_FRIEND
)
public class MonksFriend extends BasicQuestHelper
{
	ItemRequirement jugOfWater, log, blanket, ardougneCloak;

	ConditionForStep inDungeon, hasBlanket;

	QuestStep talkToOmad, goDownLadder, grabBlanket, goUpLadder, returnToOmadWithBlanket, talkToOmadAgain, talkToCedric, talkToCedricWithJug,
		continueTalkingToCedric, talkToCedricWithLog, finishQuest;

	Zone dungeon;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToOmad);

		ConditionalStep getBlanket = new ConditionalStep(this, goDownLadder);
		getBlanket.addStep(new Conditions(inDungeon, hasBlanket), goUpLadder);
		getBlanket.addStep(hasBlanket, returnToOmadWithBlanket);
		getBlanket.addStep(inDungeon, grabBlanket);

		steps.put(10, getBlanket);

		steps.put(20, talkToOmadAgain);
		steps.put(30, talkToCedric);
		steps.put(40, talkToCedricWithJug);
		steps.put(50, continueTalkingToCedric);
		steps.put(60, talkToCedricWithLog);
		steps.put(70, finishQuest);

		return steps;
	}

	public void setupItemRequirements() {
		log = new ItemRequirement("Logs", ItemID.LOGS);
		jugOfWater = new ItemRequirement("Jug of Water", ItemID.JUG_OF_WATER);
		blanket = new ItemRequirement("Child's blanket", ItemID.CHILDS_BLANKET);
		ardougneCloak = new ItemRequirement("Ardougne cloak 1 or higher for teleports to the monastery", ItemID.ARDOUGNE_CLOAK);
	}

	public void loadZones() {
		dungeon = new Zone(new WorldPoint(2559, 9597, 0), new WorldPoint(2582, 9623, 0));
	}

	public void setupConditions() {
		inDungeon = new ZoneCondition(dungeon);
		hasBlanket = new ItemRequirementCondition(blanket);
	}

	public void setupSteps() {
		talkToOmad = new NpcStep(this, NpcID.BROTHER_OMAD, new WorldPoint(2607, 3211, 0), "Talk to Brother Omad in the monastery south of West Ardougne.");
		talkToOmad.addDialogStep("Why can't you sleep, what's wrong?");
		talkToOmad.addDialogStep("Can I help at all?");
		goDownLadder = new ObjectStep(this, ObjectID.LADDER_16680, new WorldPoint(2561, 3222, 0), "Go down the ladder in a circle of stones west of the monastery.");
		grabBlanket = new DetailedQuestStep(this, new WorldPoint(2570, 9604, 0), "Pick up the Child's blanket in the room to the south.", blanket);
		goUpLadder = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2561, 9622, 0), "Go back up the ladder.");
		returnToOmadWithBlanket = new NpcStep(this, NpcID.BROTHER_OMAD, new WorldPoint(2607, 3211, 0), "Bring the blanket back to Brother Omad.", blanket);
		talkToOmadAgain = new NpcStep(this, NpcID.BROTHER_OMAD, new WorldPoint(2607, 3211, 0), "Talk to Brother Omad again.");
		talkToOmadAgain.addDialogStep("Is there anything else I can help with?");
		talkToOmadAgain.addDialogStep("Who's Brother Cedric?");
		talkToOmadAgain.addDialogStep("Where should I look?");
		talkToCedric = new NpcStep(this, NpcID.BROTHER_CEDRIC, new WorldPoint(2614, 3258, 0), "Talk to Brother Cedric north of the monastery.");
		talkToCedricWithJug = new NpcStep(this, NpcID.BROTHER_CEDRIC, new WorldPoint(2614, 3258, 0), "Talk to Brother Cedric again.", jugOfWater);
		talkToCedricWithJug.addDialogStep("Yes, I'd be happy to!");
		continueTalkingToCedric = new NpcStep(this, NpcID.BROTHER_CEDRIC, new WorldPoint(2614, 3258, 0), "Talk to Brother Cedric again.");
		talkToCedricWithJug.addSubSteps(continueTalkingToCedric);
		talkToCedricWithLog = new NpcStep(this, NpcID.BROTHER_CEDRIC, new WorldPoint(2614, 3258, 0), "Talk to Brother Cedric once again with logs.", log);
		finishQuest = new NpcStep(this, NpcID.BROTHER_OMAD, new WorldPoint(2607, 3211, 0), "Return to Brother Omad to finish the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(jugOfWater);
		reqs.add(log);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ardougneCloak);
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToOmad)), jugOfWater, log));
		allSteps.add(new PanelDetails("Finding the blanket", new ArrayList<>(Arrays.asList(goDownLadder, grabBlanket, goUpLadder, returnToOmadWithBlanket))));
		allSteps.add(new PanelDetails("Help Cedric", new ArrayList<>(Arrays.asList(talkToOmadAgain, talkToCedric, talkToCedricWithJug, talkToCedricWithLog, finishQuest))));

		return allSteps;
	}
}
