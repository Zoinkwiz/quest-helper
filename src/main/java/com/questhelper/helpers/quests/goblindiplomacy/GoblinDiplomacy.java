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
package com.questhelper.helpers.quests.goblindiplomacy;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
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
import net.runelite.api.gameval.VarbitID;

public class GoblinDiplomacy extends BasicQuestHelper
{
	// Required items
	ItemRequirement goblinMailThree;
	ItemRequirement orangeDye;
	ItemRequirement blueDye;

	// Mid-quest item requirements
	ItemRequirement goblinMail;
	ItemRequirement goblinMailTwo;
	ItemRequirement blueArmour;
	ItemRequirement orangeArmour;
	ItemRequirement mailReq;

	// Zones
	Zone upstairs;

	// Miscellaneous requirements
	ZoneRequirement isUpstairs;
	VarbitRequirement hasUpstairsArmour;
	VarbitRequirement hasWestArmour;
	VarbitRequirement hasNorthArmour;

	// Steps
	NpcStep talkToGeneral1;
	NpcStep talkToGeneral2;
	NpcStep talkToGeneral3;
	ObjectStep goUpLadder;
	ObjectStep searchUpLadder;
	ObjectStep goDownLadder;
	ObjectStep searchWestHut;
	ObjectStep searchBehindGenerals;
	DetailedQuestStep dyeOrange;
	DetailedQuestStep dyeBlue;
	DetailedQuestStep getCrate2;
	DetailedQuestStep getCrate3;

	@Override
	protected void setupZones()
	{
		upstairs = new Zone(new WorldPoint(2952, 3495, 2), new WorldPoint(2959, 3498, 2));
	}

	@Override
	protected void setupRequirements()
	{
		blueDye = new ItemRequirement("Blue dye", ItemID.BLUEDYE);
		blueDye.setTooltip("You can have Aggie in Draynor Village make you some for 2 woad leaves (bought from Wyson in Falador Park for 20 coins) and 5 coins.");
		blueDye.setHighlightInInventory(true);
		orangeDye = new ItemRequirement("Orange dye", ItemID.ORANGEDYE);
		orangeDye.setTooltip("This is made from red dye and yellow dye. Bring Aggie in Draynor Village 3 redberries and 5 coins for red dye, then 2 onions and 5 coins for yellow dye.");
		orangeDye.setHighlightInInventory(true);
		goblinMailThree = new ItemRequirement("Goblin mail", ItemID.GOBLIN_ARMOUR, 3);
		mailReq = new ItemRequirement("Goblin mail", ItemID.GOBLIN_ARMOUR, 3);
		mailReq.canBeObtainedDuringQuest();
		goblinMailTwo = new ItemRequirement("Goblin mail", ItemID.GOBLIN_ARMOUR, 2);

		goblinMail = new ItemRequirement("Goblin mail", ItemID.GOBLIN_ARMOUR);
		goblinMail.canBeObtainedDuringQuest();
		goblinMail.setTooltip("You can get goblin mail by killing goblins around goblin village.");
		goblinMail.setHighlightInInventory(true);

		blueArmour = new ItemRequirement("Blue goblin mail", ItemID.GOBLIN_ARMOUR_DARKBLUE);
		orangeArmour = new ItemRequirement("Orange goblin mail", ItemID.GOBLIN_ARMOUR_ORANGE);

		isUpstairs = new ZoneRequirement(upstairs);
		hasUpstairsArmour = new VarbitRequirement(VarbitID.GOBDIP_CRATE3_SEARCHED, 1);
		hasWestArmour = new VarbitRequirement(VarbitID.GOBDIP_CRATE2_SEARCHED, 1);
		hasNorthArmour = new VarbitRequirement(VarbitID.GOBDIP_CRATE1_SEARCHED, 1);
	}

	public void setupSteps()
	{
		goUpLadder = new ObjectStep(this, ObjectID.GOBLIN_LADDER_BOTTOM, new WorldPoint(2954, 3497, 0), "You need three goblin mails, which you can find around the Goblin Village. The first is up the ladder in a crate in the south of the village.");
		searchUpLadder = new ObjectStep(this, ObjectID.GOBLIN_OUTPOST_LARGE_CRATE_ARMOUR3, new WorldPoint(2955, 3498, 2), "Search the crate up the ladder.");
		goUpLadder.addSubSteps(searchUpLadder);
		goDownLadder = new ObjectStep(this, ObjectID.GOBLIN_LADDER_TOP, new WorldPoint(2954, 3497, 2), "Go back down the ladder.");
		searchWestHut = new ObjectStep(this, ObjectID.GOBLIN_OUTPOST_LARGE_CRATE_ARMOUR2, new WorldPoint(2951, 3508, 0), "Search the crate in the west of Goblin Village for Goblin Mail.");
		getCrate2 = new DetailedQuestStep(this, "The second goblin mail can be found in the west hut in a crate.");
		getCrate2.addSubSteps(goDownLadder, searchWestHut);

		searchBehindGenerals = new ObjectStep(this, ObjectID.GOBLIN_OUTPOST_LARGE_CRATE_ARMOUR1, new WorldPoint(2959, 3514, 0), "Search the crate north of the General's hut in Goblin Village.");
		getCrate3 = new DetailedQuestStep(this, "The last goblin mail is north of the generals' hut in a crate.");
		getCrate3.addSubSteps(searchBehindGenerals);

		dyeBlue = new DetailedQuestStep(this, "Use the blue dye on one of the goblin mail.", blueDye, goblinMail);
		dyeOrange = new DetailedQuestStep(this, "Use the orange dye on one of the goblin mail.", orangeDye, goblinMail);

		talkToGeneral1 = new NpcStep(this, NpcID.GENERAL_BENTNOZE_RED, new WorldPoint(2958, 3512, 0), "Talk to one of the Goblin Generals in Goblin Village.", orangeArmour);
		talkToGeneral1.addDialogStep("Do you want me to pick an armour colour for you?");
		talkToGeneral1.addDialogStep("What about a different colour?");
		talkToGeneral1.addDialogStep("So how is life for the goblins?");
		talkToGeneral1.addDialogStep("Yes, Wartface looks fat");
		talkToGeneral1.addDialogStep("I have some orange armour here.");

		talkToGeneral2 = new NpcStep(this, NpcID.GENERAL_BENTNOZE_RED, new WorldPoint(2958, 3512, 0), "Talk to one of the Goblin Generals in Goblin Village again.", blueArmour);
		talkToGeneral2.addDialogStep("So how is life for the goblins?");
		talkToGeneral2.addDialogStep("I have some blue armour here.");

		talkToGeneral3 = new NpcStep(this, NpcID.GENERAL_BENTNOZE_RED, new WorldPoint(2958, 3512, 0), "Talk to one of the Goblin Generals in Goblin Village once more.", goblinMail);
		talkToGeneral3.addDialogStep("So how is life for the goblins?");
		talkToGeneral3.addDialogStep("Yes, Wartface looks fat");
		talkToGeneral3.addDialogStep("I have some brown armour here.");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var lootArmour = new ConditionalStep(this, goUpLadder);
		lootArmour.addStep(and(isUpstairs, hasUpstairsArmour), goDownLadder);
		lootArmour.addStep(and(hasUpstairsArmour, hasWestArmour), searchBehindGenerals);
		lootArmour.addStep(hasUpstairsArmour, searchWestHut);
		lootArmour.addStep(isUpstairs, searchUpLadder);

		var prepareForQuest = new ConditionalStep(this, lootArmour);
		prepareForQuest.addStep(and(goblinMail, blueArmour), dyeOrange);
		prepareForQuest.addStep(or(goblinMailThree, and(hasUpstairsArmour, hasWestArmour, hasNorthArmour)), dyeBlue);

		var step1 = new ConditionalStep(this, prepareForQuest);
		step1.addStep(and(goblinMail, blueArmour, orangeArmour), talkToGeneral1);

		steps.put(0, step1);
		steps.put(3, step1);

		var prepareBlueArmour = new ConditionalStep(this, lootArmour);
		prepareBlueArmour.addStep(or(goblinMailTwo, and(hasUpstairsArmour, hasWestArmour, hasNorthArmour)), dyeBlue);

		var step2 = new ConditionalStep(this, prepareBlueArmour);
		step2.addStep(blueArmour, talkToGeneral2);

		steps.put(4, step2);

		var step3 = new ConditionalStep(this, lootArmour);
		step3.addStep(or(goblinMail, and(hasUpstairsArmour, hasWestArmour, hasNorthArmour)), talkToGeneral3);

		steps.put(5, step3);

		return steps;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			blueDye,
			orangeDye,
			mailReq
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(5);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.CRAFTING, 200)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("A Gold Bar", ItemID.GOLD_BAR, 1)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Prepare goblin mail", List.of(
			goUpLadder,
			getCrate2,
			getCrate3,
			dyeBlue,
			dyeOrange
		), List.of(
			blueDye,
			orangeDye,
			goblinMailThree
		)));

		sections.add(new PanelDetails("Present the armours", List.of(
			talkToGeneral1,
			talkToGeneral2,
			talkToGeneral3
		)));

		return sections;
	}
}
