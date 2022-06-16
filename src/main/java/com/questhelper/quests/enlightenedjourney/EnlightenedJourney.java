/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.quests.enlightenedjourney;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedOwnerStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.ENLIGHTENED_JOURNEY
)
public class EnlightenedJourney extends BasicQuestHelper
{
	ItemRequirement papyrus3, ballOfWool, sackOfPotatoes, emptySack8, unlitCandle, yellowDye, redDye, silk10, bowl,
		logs10, tinderbox, willowBranches12, papyrus, papyrus2;

	ItemRequirement draynorTeleport;

	ItemRequirement balloonStructure, origamiBalloon, sandbag8;

	Requirement onEntrana, hasSandbags, flying;

	Zone entrana;

	QuestStep travelToEntrana, talkToAuguste, usePapyrusOnWool, useCandleOnBalloon, talkToAugusteAgain,
		talkToAugusteWithPapyrus, talkToAugusteAfterMob;

	QuestStep fillSacks, talkToAugusteWithDye, talkToAugusteWithBranches,
		talkToAugusteWithLogsAndTinderbox, talkToAugusteToFinish;

	DetailedOwnerStep doPuzzle;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep startingOff = new ConditionalStep(this, travelToEntrana);
		startingOff.addStep(onEntrana, talkToAuguste);
		steps.put(0, startingOff);
		steps.put(5, startingOff);
		steps.put(6, startingOff);
		steps.put(10, startingOff);

		ConditionalStep makingPrototype = new ConditionalStep(this, usePapyrusOnWool);
		makingPrototype.addStep(origamiBalloon, talkToAugusteAgain);
		makingPrototype.addStep(balloonStructure, useCandleOnBalloon);
		steps.put(20, makingPrototype);

		steps.put(40, talkToAugusteWithPapyrus);
		steps.put(60, talkToAugusteAfterMob);

		ConditionalStep gettingFinalMaterials = new ConditionalStep(this, fillSacks);
		gettingFinalMaterials.addStep(hasSandbags, talkToAugusteWithDye);
		steps.put(70, gettingFinalMaterials);

		steps.put(80, talkToAugusteWithBranches);

		ConditionalStep flight = new ConditionalStep(this, talkToAugusteWithLogsAndTinderbox);
		flight.addStep(flying, doPuzzle);
		steps.put(90, flight);

		steps.put(100, talkToAugusteToFinish);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		papyrus3 = new ItemRequirement("Papyrus", ItemID.PAPYRUS, 3);
		papyrus2 = new ItemRequirement("Papyrus", ItemID.PAPYRUS, 2);
		papyrus = new ItemRequirement("Papyrus", ItemID.PAPYRUS);
		ballOfWool = new ItemRequirement("Ball of wool", ItemID.BALL_OF_WOOL);
		sackOfPotatoes = new ItemRequirement("Sack of potatoes (10)", ItemID.POTATOES10);
		emptySack8 = new ItemRequirement("Empty sack", ItemID.EMPTY_SACK, 8);
		emptySack8.addAlternates(ItemID.SANDBAG);
		unlitCandle = new ItemRequirement("Unlit candle", ItemID.CANDLE);
		unlitCandle.addAlternates(ItemID.BLACK_CANDLE);
		yellowDye = new ItemRequirement("Yellow dye", ItemID.YELLOW_DYE);
		redDye = new ItemRequirement("Red dye", ItemID.RED_DYE);
		silk10 = new ItemRequirement("Silk", ItemID.SILK, 10);
		bowl = new ItemRequirement("Bowl", ItemID.BOWL);
		logs10 = new ItemRequirement("Logs", ItemID.LOGS, 10);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		willowBranches12 = new ItemRequirement("Willow branches", ItemID.WILLOW_BRANCH, 12);
		willowBranches12.setTooltip("You can get these by using secateurs on a willow tree you've grown. Auguste will" +
			" give you a sapling to grow during the quest if you need one");

		draynorTeleport = new ItemRequirement("Draynor/Port Sarim teleport", ItemID.EXPLORERS_RING_3);
		draynorTeleport.addAlternates(ItemID.EXPLORERS_RING_4, ItemID.DRAYNOR_MANOR_TELEPORT);
		draynorTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);

		balloonStructure = new ItemRequirement("Balloon structure", ItemID.BALLOON_STRUCTURE);
		origamiBalloon = new ItemRequirement("Origami balloon", ItemID.ORIGAMI_BALLOON);
		sandbag8 = new ItemRequirement("Sandbag", ItemID.SANDBAG, 8);
	}

	public void setupZones()
	{
		entrana = new Zone(new WorldPoint(2798, 3327,0), new WorldPoint(2878, 3394,1));
	}

	public void setupConditions()
	{
		onEntrana = new ZoneRequirement(entrana);

		hasSandbags = new Conditions(LogicType.OR,
			new VarbitRequirement(2875, 1),
			sandbag8);

		flying = new WidgetTextRequirement(471, 1, "Balloon Controls");
		// Finished flight, 2868 = 1
	}

	public void setupSteps()
	{
		travelToEntrana = new NpcStep(this, NpcID.MONK_OF_ENTRANA_1167, new WorldPoint(3047, 3236, 0),
			"Bank all weapons and armour you have, and go to Port Sarim to get a boat to Entrana.");

		talkToAuguste = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2809, 3354, 0), "Talk to Auguste on Entrana 3" +
			" times.", papyrus3, ballOfWool);
		talkToAuguste.addDialogSteps("Yes! Sign me up.", "Umm, yes. What's your point?", "Yes.");

		usePapyrusOnWool = new DetailedQuestStep(this, "Use papyrus on a ball of wool.", papyrus.highlighted(),
			ballOfWool.highlighted());

		useCandleOnBalloon = new DetailedQuestStep(this, "Use a candle on the balloon.", unlitCandle.highlighted(),
			balloonStructure.highlighted());

		talkToAugusteAgain = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2809, 3354, 0), "Talk to Auguste again.",
			origamiBalloon);
		talkToAugusteAgain.addDialogSteps("Yes, I have them here.");

		talkToAugusteWithPapyrus = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2809, 3354, 0),
			"Talk to Auguste with 2 papyrus and a sack of potatoes.", papyrus2, sackOfPotatoes);

		talkToAugusteAfterMob = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2809, 3354, 0),
			"Talk to Auguste after the flash mob.");

		fillSacks = new ObjectStep(this, ObjectID.SAND_PIT, new WorldPoint(2817, 3342, 0),
			"Fill your empty sacks on the sand pit south of Auguste.", emptySack8.highlighted());
		fillSacks.addIcon(ItemID.EMPTY_SACK);

		talkToAugusteWithDye = new GiveAugusteItems(this);
		talkToAugusteWithDye.addDialogSteps("Yes, I want to give you some items.", "Dye.", "Sandbags.", "Silk.",
			"Bowl.");

		talkToAugusteWithBranches = new ObjectStep(this, NullObjectID.NULL_19133, new WorldPoint(2807, 3356, 0),
			"Get 12 willow branches and use them to make the basket.", willowBranches12.highlighted());
		talkToAugusteWithBranches.addIcon(ItemID.WILLOW_BRANCH);

		talkToAugusteWithLogsAndTinderbox = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2809, 3354, 0),
			"Talk to Auguste to fly.", logs10, tinderbox);
		talkToAugusteWithLogsAndTinderbox.addDialogSteps("Okay.");

		doPuzzle = new BalloonFlight1(this);

		talkToAugusteToFinish = new NpcStep(this, NpcID.AUGUSTE, new WorldPoint(2937, 3421, 0),
			"Talk to Auguste in Taverley to finish the quest.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(papyrus3, ballOfWool, sackOfPotatoes, emptySack8, unlitCandle, yellowDye, redDye, silk10, bowl,
			logs10, tinderbox, willowBranches12);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(draynorTeleport);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestPointRequirement(20));
		reqs.add(new SkillRequirement(Skill.FIREMAKING, 20));
		reqs.add(new SkillRequirement(Skill.FARMING, 30, true));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 36, true));
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.CRAFTING, 2000),
				new ExperienceReward(Skill.FARMING, 3000),
				new ExperienceReward(Skill.WOODCUTTING, 1500),
				new ExperienceReward(Skill.FIREMAKING, 4000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Bomber Jacket", ItemID.BOMBER_JACKET, 1),
				new ItemReward("Bomber Cap", ItemID.BOMBER_CAP, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to the Hot Air Balloon transport system."),
				new UnlockReward("Ability to make origami balloons."));
	}


	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Making a balloon", Arrays.asList(travelToEntrana, talkToAuguste,
			usePapyrusOnWool,
			useCandleOnBalloon, talkToAugusteAgain, talkToAugusteWithPapyrus, talkToAugusteAfterMob, fillSacks,
			talkToAugusteWithDye),	papyrus3, ballOfWool, unlitCandle, sackOfPotatoes, emptySack8, yellowDye,redDye,
			silk10, bowl));

		allSteps.add(new PanelDetails("Flying", Arrays.asList(talkToAugusteWithBranches,
			talkToAugusteWithLogsAndTinderbox, doPuzzle, talkToAugusteToFinish),
			willowBranches12, logs10, tinderbox));

		return allSteps;
	}
}
