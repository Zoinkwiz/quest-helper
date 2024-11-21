/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
package com.questhelper.helpers.quests.childrenofthesun;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.npc.NpcRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.MultiNpcStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.PuzzleWrapperStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

public class ChildrenOfTheSun extends BasicQuestHelper
{

	DetailedQuestStep talkToAlina, followGuard, attemptToEnterHouse, talkToTobyn,
		reportBackToTobyn, goUpVarrockF0ToF1, goUpVarrockF1toF2, finishQuest;

	PuzzleWrapperStep followGuardPuzzleWrapper, markGuard1, markGuard2, markGuard3, markGuard4, unmarkWrongGuard1, unmarkWrongGuard2, unmarkWrongGuard3, unmarkWrongGuard4,
		unmarkWrongGuard5, unmarkWrongGuard6;

	Zone castleF1, castleF2;

	Requirement isFollowing, markedGuard1, markedGuard2, markedGuard3, markedGuard4, markedWrongGuard1,
		markedWrongGuard2, markedWrongGuard3, markedWrongGuard4, markedWrongGuard5, markedWrongGuard6, inCastleF1, inCastleF2;

	final int GUARD_1_CHANGE_VARBIT = 9633;
	final int GUARD_2_CHANGE_VARBIT = 9634;
	final int GUARD_3_CHANGE_VARBIT = 9635;
	final int GUARD_4_CHANGE_VARBIT = 9636;
	final int WRONG_GUARD_1_CHANGE_VARBIT = 9637;
	final int WRONG_GUARD_2_CHANGE_VARBIT = 9640;
	final int WRONG_GUARD_3_CHANGE_VARBIT = 9641;
	final int WRONG_GUARD_4_CHANGE_VARBIT = 9642;
	final int WRONG_GUARD_5_CHANGE_VARBIT = 9643;
	final int WRONG_GUARD_6_CHANGE_VARBIT = 9644;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		// Before start, 9646 0->1, talked a bit to Alina
		steps.put(0, talkToAlina);
		steps.put(2, talkToAlina);
		steps.put(4, talkToAlina);
		ConditionalStep goFollowGuard = new ConditionalStep(this, talkToAlina);
		goFollowGuard.addStep(isFollowing, followGuardPuzzleWrapper);
		steps.put(6, goFollowGuard);
		steps.put(8, attemptToEnterHouse);
		steps.put(10, talkToTobyn);
		ConditionalStep markGuards = new ConditionalStep(this, markGuard1);
		markGuards.addStep(markedWrongGuard1, unmarkWrongGuard1);
		markGuards.addStep(markedWrongGuard2, unmarkWrongGuard2);
		markGuards.addStep(markedWrongGuard3, unmarkWrongGuard3);
		markGuards.addStep(markedWrongGuard4, unmarkWrongGuard4);
		markGuards.addStep(markedWrongGuard5, unmarkWrongGuard5);
		markGuards.addStep(markedWrongGuard6, unmarkWrongGuard6);
		markGuards.addStep(and(markedGuard1, markedGuard2, markedGuard3, markedGuard4), reportBackToTobyn);
		markGuards.addStep(and(markedGuard1, markedGuard2, markedGuard4), markGuard3);
		markGuards.addStep(and(markedGuard1, markedGuard2), markGuard4);
		markGuards.addStep(markedGuard1, markGuard2);
		steps.put(12, markGuards);
		steps.put(14, reportBackToTobyn);
		ConditionalStep goFinishQuest = new ConditionalStep(this, goUpVarrockF0ToF1);
		goFinishQuest.addStep(inCastleF2, finishQuest);
		goFinishQuest.addStep(inCastleF1, goUpVarrockF1toF2);
		steps.put(16, goFinishQuest);
		steps.put(18, goFinishQuest);
		steps.put(20, goFinishQuest);
		steps.put(22, goFinishQuest);
		return steps;
	}

	@Override
	protected void setupRequirements()
	{

	}

	public void setupConditions()
	{
		isFollowing = new NpcRequirement("Guard", NpcID.GUARD_12661);
		markedGuard1 = new VarbitRequirement(GUARD_1_CHANGE_VARBIT, 2);
		markedGuard2 = new VarbitRequirement(GUARD_2_CHANGE_VARBIT, 2);
		markedGuard3 = new VarbitRequirement(GUARD_3_CHANGE_VARBIT, 2);
		markedGuard4 = new VarbitRequirement(GUARD_4_CHANGE_VARBIT, 2);

		markedWrongGuard1 = new VarbitRequirement(WRONG_GUARD_1_CHANGE_VARBIT, 2);
		markedWrongGuard2 = new VarbitRequirement(WRONG_GUARD_2_CHANGE_VARBIT, 2);
		markedWrongGuard3 = new VarbitRequirement(WRONG_GUARD_3_CHANGE_VARBIT, 2);
		markedWrongGuard4 = new VarbitRequirement(WRONG_GUARD_4_CHANGE_VARBIT, 2);
		markedWrongGuard5 = new VarbitRequirement(WRONG_GUARD_5_CHANGE_VARBIT, 2);
		markedWrongGuard6 = new VarbitRequirement(WRONG_GUARD_6_CHANGE_VARBIT, 2);

		castleF1 = new Zone(new WorldPoint(3199, 3465, 1), new WorldPoint(3227, 3500, 1));
		castleF2 = new Zone(new WorldPoint(3199, 3465, 2), new WorldPoint(3227, 3500, 2));
		inCastleF1 = new ZoneRequirement(castleF1);
		inCastleF2 = new ZoneRequirement(castleF2);
	}

	public void setupSteps()
	{
		talkToAlina = new NpcStep(this, NpcID.ALINA, new WorldPoint(3225, 3426, 0), "Talk to Alina east of Varrock Square.");
		talkToAlina.addDialogSteps("When will this delegation arrive?", "Yes.");
		followGuard = new NpcStep(this, NpcID.GUARD_12661,
			"Follow the guard slowly behind them, hiding in the marked locations to avoid him spotting you. Do not let him get too far from you or you'll fail.");
		followGuard.setLinePoints(Arrays.asList(
			new WorldPoint(3225, 3429, 0),
			new WorldPoint(3233, 3429, 0),
			new WorldPoint(3233, 3427, 0),
			null,
			new WorldPoint(3235, 3429, 0),
			new WorldPoint(3240, 3429, 0),
			new WorldPoint(3240, 3417, 0),
			null,
			new WorldPoint(3242, 3417, 0),
			new WorldPoint(3242, 3403, 0),
			new WorldPoint(3241, 3403, 0),
			null,
			new WorldPoint(3239, 3401, 0),
			new WorldPoint(3236, 3397, 0),
			new WorldPoint(3236, 3392, 0),
			null,
			new WorldPoint(3238, 3390, 0),
			new WorldPoint(3248, 3390, 0),
			new WorldPoint(3248, 3396, 0),
			new WorldPoint(3247, 3396, 0),
			new WorldPoint(3247, 3397, 0)
		));
		followGuard.addTileMarkers(
			new WorldPoint(3233, 3427, 0),
			new WorldPoint(3240, 3417, 0),
			new WorldPoint(3241, 3403, 0),
			new WorldPoint(3236, 3392, 0),
			new WorldPoint(3247, 3397, 0)
		);
		followGuardPuzzleWrapper = new PuzzleWrapperStep(this, followGuard, "Follow the guard, keeping out of sight whilst also not letting them get too far away.");

		final int BASE_GUARD_ID = 6923;
		attemptToEnterHouse = new ObjectStep(this, ObjectID.DOOR_50048, new WorldPoint(3259, 3400, 0),
			"Attempt to enter the house in the south-east of Varrock, north of the Zamorak Temple, and watch the cutscene.");
		talkToTobyn = new NpcStep(this, NpcID.SERGEANT_TOBYN, new WorldPoint(3211, 3437, 0), "Talk to Sergeant Tobyn in Varrock Square.");
		markGuard1 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12668, new WorldPoint(3208, 3422, 0),
			"Mark the guard outside Aris's tent.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");
		markGuard2 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12671, new WorldPoint(3221, 3430, 0),
			"Mark the guard south east of Benny's news stand, who isn't wearing a helmet and has long hair.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.").withNoHelpHiddenInSidebar(true);
		markGuard3 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12674, new WorldPoint(3246, 3429, 0),
				"Mark the guard with a mace north-west of the Varrock East Bank.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.").withNoHelpHiddenInSidebar(true);
		markGuard4 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12677, new WorldPoint(3237, 3427, 0),
				"Mark the guard leaning on the north wall of Lowe's Archery Emporium, east of the square.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.").withNoHelpHiddenInSidebar(true);

		unmarkWrongGuard1 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12681, new WorldPoint(3227, 3424, 0),
			"Unmark the guard east of ELiza.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");
		unmarkWrongGuard2 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12684, new WorldPoint(3218, 3424, 0),
			"Unmark the guard next to Eliza.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");
		unmarkWrongGuard3 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12687, new WorldPoint(3230, 3430, 0),
			"Unmark the guard roaming south of Horvik.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");
		unmarkWrongGuard4 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12690, new WorldPoint(3206, 3431, 0),
			"Unmark the guard outside Zaff's Superior Staffs.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");
		unmarkWrongGuard5 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12693, new WorldPoint(3239, 3433, 0),
			"Unmark the guard next to the small fountain east of Horvik.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");
		unmarkWrongGuard6 = new PuzzleWrapperStep(this,
			new MultiNpcStep(this, NpcID.GUARD_12696, new WorldPoint(3218, 3433, 0),
			"Unmark the guard next to Baraek.", GUARD_1_CHANGE_VARBIT, BASE_GUARD_ID),
			"Mark the suspect guards.");

		reportBackToTobyn = new NpcStep(this, NpcID.SERGEANT_TOBYN, new WorldPoint(3211, 3437, 0),
			"Report back to Sergeant Tobyn.");
		goUpVarrockF0ToF1 = new ObjectStep(this, ObjectID.STAIRCASE_11807, new WorldPoint(3212, 3474, 0),
			"Climb up to the roof of Varrock Castle and talk to Tobyn there.");
		goUpVarrockF1toF2 = new ObjectStep(this, ObjectID.LADDER_11801, new WorldPoint(3224, 3472, 1),
			"Climb up to the roof of Varrock Castle and talk to Tobyn there.");
		finishQuest = new NpcStep(this, NpcID.SERGEANT_TOBYN, new WorldPoint(3202, 3473, 2),
			"Talk to Tobyn on the Varrock Castle's roof to finish the quest.");
		finishQuest.addSubSteps(goUpVarrockF1toF2, goUpVarrockF0ToF1);
	}

	@Override
	protected List<ItemRequirement> generateItemRequirements()
	{
		return null;
	}

	@Override
    protected List<ItemRequirement> generateItemRecommended()
	{
		return null;
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
			new UnlockReward("Access to Varlamore to the east of Varrock with a Quetzal.")
		);
	}

	@Override
    protected List<PanelDetails> setupPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Intrigue", Arrays.asList(talkToAlina, followGuardPuzzleWrapper,
			attemptToEnterHouse, talkToTobyn, markGuard1, markGuard2, markGuard4, markGuard3, reportBackToTobyn, finishQuest)));
		return allSteps;
	}
}
