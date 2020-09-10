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
package com.questhelper.quests.goblindiplomacy;

import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.QuestStep;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.GOBLIN_DIPLOMACY
)
public class GoblinDiplomacy extends BasicQuestHelper
{
	ItemRequirement goblinMailThree, orangeDye, blueDye, goblinMail, goblinMailTwo, blueArmour, orangeArmour, mailReq;

	ConditionForStep hasThreeGoblinMail, isUpstairs, hasUpstairsArmour, hasWestArmour, hasNorthArmour, hasOrangeArmour, hasBlueArmour, hasThreeMail, hasTwoMail, hasMail;

	QuestStep talkToGeneral1, talkToGeneral2, talkToGeneral3, goUpLadder, searchUpLadder, goDownLadder, searchWestHut, searchBehindGenerals,
		dyeOrange, dyeBlue, getCrate1, getCrate2, getCrate3;

	Zone upstairs;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep lootArmour = new ConditionalStep(this, goUpLadder);
		lootArmour.addStep(new Conditions(isUpstairs, hasUpstairsArmour), goDownLadder);
		lootArmour.addStep(new Conditions(hasUpstairsArmour, hasWestArmour), searchBehindGenerals);
		lootArmour.addStep(hasUpstairsArmour, searchWestHut);
		lootArmour.addStep(isUpstairs, searchUpLadder);

		ConditionalStep prepareForQuest = new ConditionalStep(this, lootArmour);
		prepareForQuest.addStep(new Conditions(hasUpstairsArmour, hasWestArmour, hasNorthArmour, hasBlueArmour), dyeOrange);
		prepareForQuest.addStep(new Conditions(LogicType.OR, hasThreeMail, new Conditions(hasUpstairsArmour, hasWestArmour, hasNorthArmour)), dyeBlue);

		ConditionalStep step1 = new ConditionalStep(this, prepareForQuest);
		step1.addStep(new Conditions(hasMail, hasBlueArmour, hasOrangeArmour), talkToGeneral1);

		steps.put(0, step1);
		steps.put(3, step1);

		ConditionalStep prepareBlueArmour = new ConditionalStep(this, lootArmour);
		prepareBlueArmour.addStep(new Conditions(LogicType.OR, hasTwoMail, new Conditions(hasUpstairsArmour, hasWestArmour, hasNorthArmour)), dyeBlue);

		ConditionalStep step2 = new ConditionalStep(this, prepareBlueArmour);
		step2.addStep(hasBlueArmour, talkToGeneral2);

		steps.put(4, step2);

		ConditionalStep step3 = new ConditionalStep(this, lootArmour);
		step3.addStep(new Conditions(LogicType.OR, hasMail, new Conditions(hasUpstairsArmour, hasWestArmour, hasNorthArmour)), talkToGeneral3);

		steps.put(5, step3);

		return steps;
	}

	public void setupItemRequirements()
	{
		blueDye = new ItemRequirement("Blue dye", ItemID.BLUE_DYE);
		blueDye.setTip("You can have Aggie in Draynor Village make you some for 2 woad leaves (bought from Wyson in Falador Park for 20 coins) and 5 coins.");
		blueDye.setHighlightInInventory(true);
		orangeDye = new ItemRequirement("Orange dye", ItemID.ORANGE_DYE);
		orangeDye.setTip("This is made from red dye and yellow dye. Bring Aggie in Draynor Village 3 redberries and 5 coins for red dye, then 2 onions and 5 coins for yellow dye.");
		orangeDye.setHighlightInInventory(true);
		goblinMailThree = new ItemRequirement("Goblin mail", ItemID.GOBLIN_MAIL, 3);
		mailReq = new ItemRequirement("Goblin mail (obtainable during quest)", ItemID.GOBLIN_MAIL, 3);
		goblinMailTwo = new ItemRequirement("Goblin mail", ItemID.GOBLIN_MAIL, 2);

		goblinMail = new ItemRequirement("Goblin mail", ItemID.GOBLIN_MAIL);
		goblinMail.setTip("You can get goblin mail by killing goblins around goblin village.");
		goblinMail.setHighlightInInventory(true);

		blueArmour = new ItemRequirement("Blue goblin mail", ItemID.BLUE_GOBLIN_MAIL);
		orangeArmour = new ItemRequirement("Orange goblin mail", ItemID.ORANGE_GOBLIN_MAIL);
	}

	public void setupConditions()
	{
		isUpstairs = new ZoneCondition(upstairs);
		hasThreeGoblinMail = new ItemRequirementCondition(goblinMailThree);
		hasUpstairsArmour = new VarbitCondition(2381, 1);
		hasWestArmour = new VarbitCondition(2380, 1);
		hasNorthArmour = new VarbitCondition(2379, 1);
		hasOrangeArmour = new ItemRequirementCondition(orangeArmour);
		hasBlueArmour = new ItemRequirementCondition(blueArmour);
		hasThreeMail = new ItemRequirementCondition(goblinMailThree);
		hasTwoMail = new ItemRequirementCondition(goblinMailTwo);
		hasMail = new ItemRequirementCondition(goblinMail);
	}

	public void setupZones()
	{
		upstairs = new Zone(new WorldPoint(2952, 3495, 2), new WorldPoint(2959, 3498, 2));
	}

	public void setupSteps()
	{
		getCrate1 = new DetailedQuestStep(this, "You need three goblin mails, which you can find around the goblin village. The first is up the ladder in a crate in the south of the village.");
		goUpLadder = new ObjectStep(this, ObjectID.LADDER_16450, new WorldPoint(2954, 3497, 0), "Go up the ladder in the south of Goblin Village.");
		searchUpLadder = new ObjectStep(this, ObjectID.CRATE_16561, new WorldPoint(2955, 3498, 2), "Search the crate up the ladder.");
		getCrate1.addSubSteps(goUpLadder, searchUpLadder);

		goDownLadder = new ObjectStep(this, ObjectID.LADDER_16556, new WorldPoint(2954, 3497, 2), "Go back down the ladder.");
		searchWestHut = new ObjectStep(this, ObjectID.CRATE_16560, new WorldPoint(2951, 3508, 0), "Search the crate in the west of Goblin Village for Goblin Mail.");
		getCrate2 = new DetailedQuestStep(this, "The second goblin mail can be found in the west hut in a crate.");
		getCrate2.addSubSteps(goDownLadder, searchWestHut);

		searchBehindGenerals = new ObjectStep(this, ObjectID.CRATE_16559, new WorldPoint(2959, 3514, 0), "Search the crate north of the General's hut in Goblin Village.");
		getCrate3 = new DetailedQuestStep(this, "The last goblin mail is north of the generals' hut in a crate.");
		getCrate3.addSubSteps(searchBehindGenerals);

		dyeBlue = new DetailedQuestStep(this, "Use the blue dye on one of the goblin mail.", blueDye, goblinMail);
		dyeOrange = new DetailedQuestStep(this, "Use the orange dye on one of the goblin mail.", orangeDye, goblinMail);

		talkToGeneral1 = new NpcStep(this, NpcID.GENERAL_BENTNOZE, new WorldPoint(2958, 3512, 0), "Talk to one of the Goblin Generals in Goblin Village.", orangeArmour);
		talkToGeneral1.addDialogStep("Yes, Wartface looks fat");
		talkToGeneral1.addDialogStep("Do you want me to pick an armour colour for you?");
		talkToGeneral1.addDialogStep("What about a different colour?");
		talkToGeneral1.addDialogStep("I have some orange armour here");

		talkToGeneral2 = new NpcStep(this, NpcID.GENERAL_BENTNOZE, new WorldPoint(2958, 3512, 0), "Talk to one of the Goblin Generals in Goblin Village again.", blueArmour);
		talkToGeneral2.addDialogStep("I have some blue armour here");
		talkToGeneral3 = new NpcStep(this, NpcID.GENERAL_BENTNOZE, new WorldPoint(2958, 3512, 0), "Talk to one of the Goblin Generals in Goblin Village once more.", goblinMail);
		talkToGeneral3.addDialogStep("I have some brown armour here");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(blueDye);
		reqs.add(orangeDye);
		reqs.add(mailReq);
		return reqs;
	}


	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		PanelDetails getArmours = new PanelDetails("Prepare goblin mail",
			new ArrayList<>(Arrays.asList(getCrate1, getCrate2, getCrate3, dyeBlue, dyeOrange)), blueDye, orangeDye);
		allSteps.add(getArmours);

		allSteps.add(new PanelDetails("Present the armours", new ArrayList<>(Arrays.asList(talkToGeneral1, talkToGeneral2, talkToGeneral3))));
		return allSteps;
	}
}
