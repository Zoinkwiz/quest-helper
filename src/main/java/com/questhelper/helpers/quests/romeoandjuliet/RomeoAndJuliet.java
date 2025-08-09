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
package com.questhelper.helpers.quests.romeoandjuliet;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
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

public class RomeoAndJuliet extends BasicQuestHelper
{
	// Required items
	ItemRequirement cadavaBerry;

	// Zones
	Zone julietRoom;

	// Miscellaneous requirements
	ItemRequirement letter;
	ItemRequirement cadavaPotion;
	ZoneRequirement inJulietRoom;

	// Steps
	QuestStep talkToRomeo;

	QuestStep goUpToJuliet;
	QuestStep talkToJuliet;

	ObjectStep goDownstairsToGiveLetterToRomeo;
	NpcStep giveLetterToRomeo;

	QuestStep talkToLawrence;
	QuestStep talkToApothecary;
	QuestStep goUpToJuliet2;
	QuestStep givePotionToJuliet;

	ObjectStep goDownstairsToFinishQuest;
	QuestStep finishQuest;

	@Override
	protected void setupZones()
	{
		julietRoom = new Zone(new WorldPoint(3147, 3425, 1), new WorldPoint(3166, 3443, 1));
	}

	@Override
	protected void setupRequirements()
	{
		inJulietRoom = new ZoneRequirement(julietRoom);

		cadavaBerry = new ItemRequirement("Cadava berries", ItemID.CADAVABERRIES);
		cadavaBerry.setTooltip("You can pick some from bushes south east of Varrock");
		letter = new ItemRequirement("Message", ItemID.JULIETMESSAGE);
		letter.setTooltip("You can get another from Juliet");
		cadavaPotion = new ItemRequirement("Cadava potion", ItemID.CADAVA);
	}

	public void setupSteps()
	{
		talkToRomeo = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Talk to Romeo in Varrock Square.");
		talkToRomeo.addDialogStep("Yes, I have seen her actually!");
		talkToRomeo.addDialogStep("Yes, ok, I'll let her know.");
		talkToRomeo.addDialogStep("Yes.");
		goUpToJuliet = new ObjectStep(this, ObjectID.FAI_VARROCK_STAIRS_TALLER, new WorldPoint(3157, 3436, 0), "Talk to Juliet in the house west of Varrock.");
		goUpToJuliet.addDialogStep("Ok, thanks.");
		talkToJuliet = new NpcStep(this, NpcID.JULIET, new WorldPoint(3158, 3427, 1), "Talk to Juliet in the house west of Varrock.");
		talkToJuliet.addSubSteps(goUpToJuliet);

		goDownstairsToGiveLetterToRomeo = new ObjectStep(this, ObjectID.FAI_VARROCK_STAIRS_TOP, new WorldPoint(3156, 3435, 1), "Bring the letter to Romeo in Varrock Square.", letter);
		giveLetterToRomeo = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Bring the letter to Romeo in Varrock Square.", letter);
		giveLetterToRomeo.addSubSteps(goDownstairsToGiveLetterToRomeo);

		talkToLawrence = new NpcStep(this, NpcID.FATHER_LAWRENCE, new WorldPoint(3254, 3483, 0), "Talk to Father Lawrence in north east Varrock.");
		talkToLawrence.addDialogStep("Ok, thanks.");
		talkToApothecary = new NpcStep(this, NpcID.APOTHECARY, new WorldPoint(3195, 3405, 0), "Bring the cadava berries to the Apothecary in south west Varrock.", cadavaBerry);
		talkToApothecary.addDialogStep("Talk about something else.");
		talkToApothecary.addDialogStep("Talk about Romeo & Juliet.");
		goUpToJuliet2 = new ObjectStep(this, ObjectID.FAI_VARROCK_STAIRS_TALLER, new WorldPoint(3157, 3436, 0), "Bring the potion to Juliet in the house west of Varrock.", cadavaPotion);
		givePotionToJuliet = new NpcStep(this, NpcID.JULIET, new WorldPoint(3158, 3427, 1), "Bring the potion to Juliet in the house west of Varrock.", cadavaPotion);
		givePotionToJuliet.addSubSteps(goUpToJuliet2);

		goDownstairsToFinishQuest = new ObjectStep(this, ObjectID.FAI_VARROCK_STAIRS_TOP, new WorldPoint(3156, 3435, 1), "Talk to Romeo in Varrock Square to finish the quest.");
		finishQuest = new NpcStep(this, NpcID.ROMEO, new WorldPoint(3211, 3422, 0), "Talk to Romeo in Varrock Square to finish the quest.");
		finishQuest.addSubSteps(goDownstairsToFinishQuest);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		steps.put(0, talkToRomeo);

		var tellJulietAboutRomeo = new ConditionalStep(this, goUpToJuliet);
		tellJulietAboutRomeo.addStep(inJulietRoom, talkToJuliet);

		steps.put(10, tellJulietAboutRomeo);

		var giveLetterToRomeo = new ConditionalStep(this, this.giveLetterToRomeo);
		giveLetterToRomeo.addStep(inJulietRoom, goDownstairsToGiveLetterToRomeo);
		steps.put(20, giveLetterToRomeo);
		steps.put(30, talkToLawrence);
		steps.put(40, talkToApothecary);

		var bringPotionToJuliet = new ConditionalStep(this, talkToApothecary);
		bringPotionToJuliet.addStep(and(cadavaPotion, inJulietRoom), givePotionToJuliet);
		bringPotionToJuliet.addStep(cadavaPotion, goUpToJuliet2);

		steps.put(50, bringPotionToJuliet);

		var cFinishQuest = new ConditionalStep(this, finishQuest);
		giveLetterToRomeo.addStep(inJulietRoom, goDownstairsToFinishQuest);
		steps.put(60, cFinishQuest);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			cadavaBerry
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(5);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Helping Romeo", List.of(
			talkToRomeo,
			talkToJuliet,
			giveLetterToRomeo
		)));

		sections.add(new PanelDetails("Hatching a plan", List.of(
			talkToLawrence,
			talkToApothecary
		), List.of(
			cadavaBerry
		)));

		sections.add(new PanelDetails("Enact the plan", List.of(
			givePotionToJuliet,
			finishQuest
		)));

		return sections;
	}
}
