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
package com.questhelper.quests.romeoandjuliet;

import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.requirements.conditional.Conditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ROMEO__JULIET
)
public class RomeoAndJuliet extends BasicQuestHelper
{
	//Items Required
	ItemRequirement cadavaBerry, letter, potion;

	Requirement inJulietRoom;

	QuestStep talkToRomeo, goUpToJuliet, talkToJuliet, giveLetterToRomeo, talkToLawrence, talkToApothecary, goUpToJuliet2, givePotionToJuliet, finishQuest;

	//Zones
	Zone julietRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToRomeo);

		ConditionalStep tellJulietAboutRomeo = new ConditionalStep(this, goUpToJuliet);
		tellJulietAboutRomeo.addStep(inJulietRoom, talkToJuliet);

		steps.put(10, tellJulietAboutRomeo);
		steps.put(20, giveLetterToRomeo);
		steps.put(30, talkToLawrence);
		steps.put(40, talkToApothecary);

		ConditionalStep bringPotionToJuliet = new ConditionalStep(this, talkToApothecary);
		bringPotionToJuliet.addStep(new Conditions(potion, inJulietRoom), givePotionToJuliet);
		bringPotionToJuliet.addStep(potion, goUpToJuliet2);

		steps.put(50, bringPotionToJuliet);
		steps.put(60, finishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		cadavaBerry = new ItemRequirement("Cadava berries", ItemID.CADAVA_BERRIES);
		cadavaBerry.setTooltip("You can pick some from bushes south east of Varrock");
		letter = new ItemRequirement("Message", ItemID.MESSAGE);
		letter.setTooltip("You can get another from Juliet");
		potion = new ItemRequirement("Cadava potion", ItemID.CADAVA_POTION);
	}

	public void setupConditions()
	{
		inJulietRoom = new ZoneRequirement(julietRoom);
	}

	public void setupZones()
	{
		julietRoom = new Zone(new WorldPoint(3147, 3425, 1), new WorldPoint(3166, 3443, 1));
	}

	public void setupSteps()
	{
		talkToRomeo = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Talk to Romeo in Varrock Square.");
		talkToRomeo.addDialogStep("Yes, I have seen her actually!");
		talkToRomeo.addDialogStep("Yes, ok, I'll let her know.");
		goUpToJuliet = new ObjectStep(this, ObjectID.STAIRCASE_11797, new WorldPoint(3157, 3436, 0), "Talk to Juliet in the house west of Varrock.");
		goUpToJuliet.addDialogStep("Ok, thanks.");
		talkToJuliet = new NpcStep(this, NpcID.JULIET, new WorldPoint(3158, 3427, 1), "Talk to Juliet in the house west of Varrock.");
		talkToJuliet.addSubSteps(goUpToJuliet);

		giveLetterToRomeo = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Bring the letter to Romeo in Varrock Square.", letter);
		talkToLawrence = new NpcStep(this, NpcID.FATHER_LAWRENCE, new WorldPoint(3254, 3483, 0), "Talk to Father Lawrence in north east Varrock.");
		talkToLawrence.addDialogStep("Ok, thanks.");
		talkToApothecary = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3405, 0), "Bring the Apothecary cadava berries in south west Varrock.", cadavaBerry);
		talkToApothecary.addDialogStep("Talk about something else.");
		talkToApothecary.addDialogStep("Talk about Romeo & Juliet.");
		goUpToJuliet2 = new ObjectStep(this, ObjectID.STAIRCASE_11797, new WorldPoint(3157, 3436, 0), "Bring the potion to Juliet in the house west of Varrock.", potion);
		givePotionToJuliet = new NpcStep(this, NpcID.JULIET, new WorldPoint(3158, 3427, 1), "Bring the potion to Juliet in the house west of Varrock.", potion);
		givePotionToJuliet.addSubSteps(goUpToJuliet2);

		finishQuest = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Talk to Romeo in Varrock Square to finish the quest.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(cadavaBerry);
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(5);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Helping Romeo", Arrays.asList(talkToRomeo, talkToJuliet, giveLetterToRomeo)));
		allSteps.add(new PanelDetails("Hatching a plan", Arrays.asList(talkToLawrence, talkToApothecary), cadavaBerry));
		allSteps.add(new PanelDetails("Enact the plan", Arrays.asList(givePotionToJuliet, finishQuest)));
		return allSteps;
	}
}
