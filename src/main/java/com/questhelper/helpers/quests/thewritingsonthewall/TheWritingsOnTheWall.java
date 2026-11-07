/*
 * Copyright (c) 2026, pajlada <rasmus.karlsson@pajlada.com>
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
package com.questhelper.helpers.quests.thewritingsonthewall;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ManualRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
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
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import static com.questhelper.requirements.util.LogicHelper.and;

public class TheWritingsOnTheWall extends BasicQuestHelper
{
	// Required items
	ItemRequirement sampleRequirement;

	// Recommended items
	ItemRequirement energyRestorePotion;

	// Mid-quest item requirements
	ItemRequirement itemAcquiredDuringQuest;

	// Zones
	Zone sampleZone;

	// Miscellaneous requirements
	VarbitRequirement hasFinishedCertainTask;

	// Steps
	NpcStep talkToNpc;
	ObjectStep climbUpLadder;

	@Override
	protected void setupZones()
	{
		sampleZone = new Zone(new WorldPoint(10, 10, 0), new WorldPoint(20, 20, 0));
	}

	@Override
	protected void setupRequirements()
	{
		sampleRequirement = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);

		energyRestorePotion = new ItemRequirement("Stamina potion", ItemCollections.STAMINA_POTIONS);

		itemAcquiredDuringQuest = new ItemRequirement("Book of Lore", ItemID.BOOK_OF_ASTROLOGY);

		hasFinishedCertainTask = new VarbitRequirement(VarbitID.PORT_TASK_SLOT_2_ID, 2);
	}

	void setupSteps()
	{
		talkToNpc = new NpcStep(this, NpcID.HANS, "Talk to Hans in Lumbridge", sampleRequirement);
		climbUpLadder = new ObjectStep(this, ObjectID.LADDER, "Climb up the ladder");
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var TODO = new DetailedQuestStep(this, "TODO");

		for (var i = 0; i < 500; ++i) {
			steps.put(i, TODO);
		}

		var startQuest = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1518, 3437, 0), "Talk to Betty in Land's End to start the quest.");
		startQuest.addDialogStep("Yes.");

		steps.put(0, startQuest);

		// talk to sergeant hudson
		var talkToSergeantHudson = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1521, 3441, 0), "Talk to Sergeant Hudson and ask him about the investigation.");
		steps.put(2, talkToSergeantHudson);

		var needToReadNews = new VarbitRequirement(VarbitID.WOTW_LANDS_END_NEWS, Operation.LESS, 2, "need to read news");
		var newspaperCutting = new ItemRequirement("Newspaper cutting", ItemID.WOTW_NEWS_LANDS_END);
		var searchShelves = new ObjectStep(this, ObjectID.WOTW_SHELVES_LANDS_END, "Search the shelves inside Betty's house.");
		var readNews = new DetailedQuestStep(this, "Read the newspaper cutting.", newspaperCutting.highlighted());
		var cReadNews = new ConditionalStep(this, TODO);

		var needWallInfo = new VarbitRequirement(VarbitID.WOTW_LANDS_END_WALL, 0);
		var needHandsInfo = new VarbitRequirement(VarbitID.WOTW_LANDS_END_HANDS, 0);
		var needLetter = new VarbitRequirement(VarbitID.WOTW_LANDS_END_LETTER, Operation.LESS, 2, "Need to read the letter");

		var inspectBodyWall = new ObjectStep(this, ObjectID.WOTW_BED_LANDS_END_BODY_INSPECT, new WorldPoint(1514, 3438, 0), "Inspect the dead body inside Betty's house.");
		inspectBodyWall.addDialogStep("Inspect the head.");

		var inspectBodyHands = new ObjectStep(this, ObjectID.WOTW_BED_LANDS_END_BODY_INSPECT, new WorldPoint(1514, 3438, 0), "Inspect the dead body inside Betty's house.");
		inspectBodyHands.addDialogStep("Inspect the arms.");

		var inspectBodyTorso = new ObjectStep(this, ObjectID.WOTW_BED_LANDS_END_BODY_INSPECT, new WorldPoint(1514, 3438, 0), "Inspect the dead body inside Betty's house.");
		inspectBodyTorso.addDialogStep("Inspect the torso.");
		inspectBodyTorso.addDialogStep("Yes.");

		var letter = new ItemRequirement("Letter", ItemID.WOTW_LETTER_LANDS_END);
		var readLetter = new DetailedQuestStep(this, "Read the letter you picked up from the body.", letter.highlighted());

		var returnToSergeantHudson = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1521, 3441, 0), "Return to Sergeant Hudson and fill him in on what you've found.");
		var cInvestigate = new ConditionalStep(this, returnToSergeantHudson);
		// Shelves
		cInvestigate.addStep(and(newspaperCutting, needToReadNews), readNews);
		cInvestigate.addStep(needToReadNews, searchShelves);

		// Body
		cInvestigate.addStep(needWallInfo, inspectBodyWall);
		cInvestigate.addStep(needHandsInfo, inspectBodyHands);
		cInvestigate.addStep(and(needLetter, letter), readLetter);
		cInvestigate.addStep(needLetter, inspectBodyTorso);

		var t = new ManualRequirement();
		t.setShouldPass(true);

		// Blood on the wall
		var needToInspectBloodOnWall = new VarbitRequirement(VarbitID.WOTW_LANDS_END_WALL, 1);
		var inspectBloodOnWall = new ObjectStep(this, ObjectID.WOTW_BLOOD_LANDS_END_INSPECT, new WorldPoint(1517, 3439, 0), "Inspect the blood on the wall near the entrance.");
		cInvestigate.addStep(needToInspectBloodOnWall, inspectBloodOnWall);

		// Speak to Lan the Butcher
		var questionButcher = new NpcStep(this, NpcID.KW_BUTCHER_LAN_VIS, new WorldPoint(1498, 3422, 0), "Talk to Lan the Butcher in the south-western house of Land's end.");
		questionButcher.addDialogStep("What do you know about the victim?");
		var questionButcherAboutKnife = new NpcStep(this, NpcID.KW_BUTCHER_LAN_VIS, new WorldPoint(1498, 3422, 0), "Talk to Lan the Butcher in the south-western house of Land's end.");
		questionButcherAboutKnife.addDialogStep("I found this knife under your bed...");
		var needToQuestionLanAboutVictim = new VarbitRequirement(VarbitID.WOTW_LANDS_END_BUTCHER_QUESTIONED, 0);

		var needToSearchButcherBed = new VarbitRequirement(VarbitID.WOTW_LANDS_END_KNIFE, 0);
		var searchButcherBed = new ObjectStep(this, ObjectID.WOTW_BUTCHER_BED_LANDS_END, new WorldPoint(1496, 3419, 0), "Search Lan the Butcher's bed.");
		var bloodyKnife = new ItemRequirement("Bloody knife", ItemID.WOTW_KNIFE_LANDS_END);

		cInvestigate.addStep(needToQuestionLanAboutVictim, questionButcher);
		cInvestigate.addStep(needToSearchButcherBed, searchButcherBed);
		// TODO: Is this step actually necessary?
		// cInvestigate.addStep(bloodyKnife, questionButcherAboutKnife);

		// Speak to Hugor
		var needToQuestionHugor = new VarbitRequirement(VarbitID.WOTW_LANDS_END_HUGOR_ALIBI, 0);
		var questionHugor = new NpcStep(this, NpcID.KW_HUGOR, new WorldPoint(1510, 3420, 0), "Talk to Hugor in Land's End");
		questionHugor.addDialogStep("Did you see or hear anything around the time of the murder?");
		cInvestigate.addStep(needToQuestionHugor, questionHugor);

		steps.put(4, cInvestigate);
		steps.put(6, cInvestigate);

		// TODO: STEP 6 IS MISSING THE ALIBI PART FROM BETTY, AND MAYBE THE HISTORY IS REQUIRED TOO
		var gotBettysAlibi = new VarbitRequirement(VarbitID.WOTW_LANDS_END_BETTY_ALIBI, 1);
		var gotHistoryFromBetty = new VarbitRequirement(VarbitID.WOTW_LANDS_END_HISTORY, 1);

		/// 8: You have given Hudson the information, and there's a cutscene!
		var returnToSergeantHudsonForCutscene = new NpcStep(this, NpcID.WOTW_HUDSON_CHILD, new WorldPoint(1521, 3441, 0), "Return to Sergeant Hudson and fill him in on what you've found.");
		steps.put(8, returnToSergeantHudsonForCutscene);

		/// 10: Cutscene is finished, we are talking to Betty
		var talkToBettyAfterSergeantHudsonLeft = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1518, 3437, 0), "Talk to Betty outside of her house in Land's end.");
		steps.put(10, talkToBettyAfterSergeantHudsonLeft);

		/// 12: Agreed to meet Betty at her uncle's house, north-west of the Tithe Farm
		var talkToBettyAtUnclesHouse = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1784, 3519, 0), "Talk to Betty outside of her uncles house, north-west of the Tithe Farm.");
		steps.put(12, talkToBettyAtUnclesHouse);

		/// 14: Curiously, the uncle isn't home :O. Let's break into the house and see if he's there.
		var inUnclesHouse = new ZoneRequirement(new Zone(new WorldPoint(1784, 3526, 0), new WorldPoint(1789, 3521, 0)));
		var enterUnclesHouseThroughWindow = new ObjectStep(this, ObjectID.WOTW_HOSIDIUS_WINDOW, new WorldPoint(1783, 3525, 0), "Enter Betty's uncle's house through the open window on the western wall.");
		var cBreakIntoUnclesHouse = new ConditionalStep(this, enterUnclesHouseThroughWindow);
		var openDoorForBetty = new ObjectStep(this, ObjectID.WOTW_HOSIDIUS_FRONT_DOOR, new WorldPoint(1785, 3520, 0), "Open the front door for Betty.");
		cBreakIntoUnclesHouse.addStep(inUnclesHouse, openDoorForBetty);
		steps.put(14, cBreakIntoUnclesHouse);

		/// 16: Betty walked down by herself and found her Uncle dead.
		var inBasement = ZoneRequirement.square(1745, 9938, 0, 1750, 9933, 0);
		var climbDownLadder = new ObjectStep(this, ObjectID.HOSIDIUS_BASEMENT_LADDER_ENTRY, new WorldPoint(1789, 3521, 0), "Talk to Betty in her uncle's basement.");
		var talkToBettyInUnclesBasement = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Talk to Betty in her uncle's basement.");
		var cTalkToBettyInUnclesBasement = new ConditionalStep(this, climbDownLadder);
		cTalkToBettyInUnclesBasement.addStep(inBasement, talkToBettyInUnclesBasement);
		steps.put(16, cTalkToBettyInUnclesBasement);

		/// 18: Before talking to the authorities, I will just look through the crimescene. Clueless
		var climbDownLadder2 = new ObjectStep(this, ObjectID.HOSIDIUS_BASEMENT_LADDER_ENTRY, new WorldPoint(1789, 3521, 0), "Climb down to Betty's uncle's basement.");

		var needToInvestigateHead = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_HEAD, 0);
		var investigateHead = new ObjectStep(this, ObjectID.WOTW_TABLE_HOSIDIUS_BODY, new WorldPoint(1747, 9935, 0), "Investigate the dead body on the table.");
		investigateHead.addDialogStep("Inspect the head.");

		var needToInvestigateArms = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_UNFINISHED_DRINK, 0);
		var investigateArms = new ObjectStep(this, ObjectID.WOTW_TABLE_HOSIDIUS_BODY, new WorldPoint(1747, 9935, 0), "Investigate the dead body on the table.");
		investigateArms.addDialogStep("Inspect the arms.");

		var needToInvestigateShelves = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_DRINK_SHELVES, 0);
		var investigateShelves = new ObjectStep(this, ObjectID.WOTW_DRINK_SHELVES_HOSIDIUS, new WorldPoint(1747, 9938, 0), "Investigate the shelves at the back of the basement.");

		var needToAskBettyAboutUnclesDrinkingHabits = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_UNCLE_DRINKING_HABITS, 0);
		var askAboutUnclesDrinkingHabits = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Ask Betty about her uncle's drinking habits.");
		askAboutUnclesDrinkingHabits.addDialogStep("Was your uncle a big drinker?");

		var needToAskBettyAboutUnclesJob = new VarbitRequirement(VarbitID.WOTW_HOSIDIUS_UNCLE_JOB, 0);
		var askAboutUncle = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Ask Betty about her uncle's potential enemies.");
		askAboutUncle.addDialogStep("Do you know why someone would want your uncle dead?");

		var talkToBettyInUnclesBasement2 = new NpcStep(this, NpcID.WOTW_BETTY_CHILD, new WorldPoint(1749, 9934, 0), "Return to Betty in her uncle's basement with the information you've found.");
		var cInvestigateUnclesBasement = new ConditionalStep(this, climbDownLadder2);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToInvestigateHead), investigateHead);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToInvestigateArms), investigateArms);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToInvestigateShelves), investigateShelves);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToAskBettyAboutUnclesDrinkingHabits), askAboutUnclesDrinkingHabits);
		cInvestigateUnclesBasement.addStep(and(inBasement, needToAskBettyAboutUnclesJob), askAboutUncle);
		cInvestigateUnclesBasement.addStep(inBasement, talkToBettyInUnclesBasement2);
		steps.put(18, cInvestigateUnclesBasement);


		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.PANDEMONIUM, QuestState.FINISHED),
			new SkillRequirement(Skill.SAILING, 45, false)
		);
	}

	@Override
	public List<Requirement> getGeneralRecommended()
	{
		return List.of(
			//
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			sampleRequirement
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			energyRestorePotion
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Big scary monster (lvl 200)"
		);
	}

	@Override
	public List<String> getNotes()
	{
		return List.of(
			"This is a note to appear in the sidebar"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return List.of(
			new ExperienceReward(Skill.SAILING, 10000),
			new ExperienceReward(Skill.SLAYER, 8000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of();
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Access to something grand")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("The section's header!", List.of(
			talkToNpc,
			climbUpLadder
		), List.of(
			sampleRequirement
		)));

		return sections;
	}
}
