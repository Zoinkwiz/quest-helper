/*
 * Copyright (c) 2020, Patyfatycake <https://github.com/Patyfatycake/>
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
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.helpers.quests.recruitmentdrive;

import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.ConfigRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.ItemSlots;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.not;
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
import com.questhelper.steps.PuzzleWrapperStep;
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

public class RecruitmentDrive extends BasicQuestHelper
{
	// Mid-quest requirements
	NoItemRequirement noItemRequirement;

	// Zones
	Zone firstFloorZone;
	Zone secondFloorZone;
	Zone missCheeversZone;
	Zone sirTinleyRoomZone;
	Zone msHynnRoomZone;
	Zone sirKuamRoomZone;
	Zone sirSphishyusZone;
	Zone sirRenItchoodZone;
	Zone ladyTableZone;

	// Miscellaneous requirements
	ZoneRequirement isFirstFloorCastle;
	ZoneRequirement isSecondFloorCastle;
	ZoneRequirement isInMissCheeversRoom;
	ZoneRequirement isInSirTinleysRoom;
	ZoneRequirement isInMsHynnRoom;
	ZoneRequirement isInSirKuamsRoom;
	ZoneRequirement isInSirSpishyusRoom;
	ZoneRequirement isInSirRenItchood;
	ZoneRequirement isInladyTableRoom;

	// Steps
	ConditionalStep cTalkToSirAmikVarze;
	ObjectStep climbBottomSteps;
	ObjectStep climbSecondSteps;
	NpcStep talkToSirAmikVarze;

	ObjectStep climbDownSecondFloorStaircase;
	ObjectStep climbDownfirstFloorStaircase;
	QuestStep talkToSirTiffy;

	// Sir Tinley steps
	QuestStep doNothingStep;
	QuestStep talkToSirTinley;
	QuestStep leaveSirTinleyRoom;
	ConditionalStep sirTinleyStep;

	// Sir Kuam Ferentse steps
	QuestStep talkToSirKuam;
	QuestStep killSirLeye;
	QuestStep leaveSirKuamRoom;
	ConditionalStep sirKuamStep;

	// Sir Spishyus
	QuestStep moveChickenOnRightToLeft;
	QuestStep moveFoxOnRightToLeft;
	QuestStep moveChickenOnLeftToRight;
	QuestStep moveGrainOnRightToLeft;
	QuestStep moveChickenOnRightToLeftAgain;
	QuestStep leaveSirSpishyusRoom;
	ConditionalStep sirSpishyusPuzzle;
	ConditionalStep cSirSpishyus;
	PuzzleWrapperStep pwSirSpishyusStep;

	// Sir Ren
	SirRenItchoodStep sirRenStep;

	// Lady Table
	LadyTableStep ladyTableStep;

	// Ms Hynn
	MsHynnAnswerDialogQuizStep msHynnDialogQuiz;
	PuzzleWrapperStep pwMsHynnTerprett;
	ObjectStep leaveMsHynnTerprettRoom;
	ConditionalStep msHynnTerprettStep;

	// Miss Cheevers
	MissCheeversStep missCheeversStep;
	PuzzleWrapperStep pwMissCheeversStep;
	ObjectStep leaveMissCheeversRoom;
	ConditionalStep cMissCheevers;

	@Override
	protected void setupZones()
	{
		firstFloorZone = new Zone(new WorldPoint(2954, 3335, 1),
			new WorldPoint(2966, 3343, 1));
		secondFloorZone = new Zone(new WorldPoint(2955, 3334, 2),
			new WorldPoint(2964, 3342, 2));

		missCheeversZone = new Zone(new WorldPoint(2466, 4936, 0),
			new WorldPoint(2480, 4947, 0));
		sirTinleyRoomZone = new Zone(new WorldPoint(2471, 4954, 0),
			new WorldPoint(2481, 4960, 0));
		msHynnRoomZone = new Zone(new WorldPoint(2446, 4934, 0),
			new WorldPoint(2457, 4946, 0));
		sirKuamRoomZone = new Zone(new WorldPoint(2453, 4958, 0),
			new WorldPoint(2466, 4970, 0));
		sirSphishyusZone = new Zone(new WorldPoint(2469, 4968, 0),
			new WorldPoint(2492, 4980, 0));
		sirRenItchoodZone = new Zone(new WorldPoint(2438, 4952, 0),
			new WorldPoint(2448, 4962, 0));
		ladyTableZone = new Zone(new WorldPoint(2445, 4974, 0),
			new WorldPoint(2461, 4987, 0));
	}

	@Override
	protected void setupRequirements()
	{
		noItemRequirement = new NoItemRequirement("No items or equipment carried", ItemSlots.ANY_EQUIPPED_AND_INVENTORY);

		isFirstFloorCastle = new ZoneRequirement(firstFloorZone);
		isSecondFloorCastle = new ZoneRequirement(secondFloorZone);
		isInMissCheeversRoom = new ZoneRequirement(missCheeversZone);
		isInSirTinleysRoom = new ZoneRequirement(sirTinleyRoomZone);
		isInMsHynnRoom = new ZoneRequirement(msHynnRoomZone);
		isInSirKuamsRoom = new ZoneRequirement(sirKuamRoomZone);
		isInSirSpishyusRoom = new ZoneRequirement(sirSphishyusZone);
		isInSirRenItchood = new ZoneRequirement(sirRenItchoodZone);
		isInladyTableRoom = new ZoneRequirement(ladyTableZone);
	}

	public void setupSteps()
	{
		climbBottomSteps = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2955, 3339, 0),
			"Climb up the stairs to the first floor on the Falador Castle.");

		climbSecondSteps = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2961, 3339, 1),
			"Climb up the stairs to talk to Sir Amik Varze.");
		talkToSirAmikVarze = new NpcStep(this, NpcID.SIR_AMIK_VARZE, "");
		talkToSirAmikVarze.addDialogStep("Yes.");

		climbDownSecondFloorStaircase = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRSTOP, new WorldPoint(2960, 3339, 2), "Climb down the stairs from the second floor.");
		climbDownfirstFloorStaircase = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRSTOP, new WorldPoint(2955, 3338, 1), "Climb down the stairs from the first floor.");

		talkToSirTiffy = new NpcStep(this, NpcID.RD_TELEPORTER_GUY, new WorldPoint(2997, 3373, 0), "Talk to Sir Tiffy Cashien in Falador Park.", noItemRequirement);
		talkToSirTiffy.addDialogStep("Yes, let's go!");
		talkToSirTiffy.addSubSteps(climbDownfirstFloorStaircase, climbDownSecondFloorStaircase);

		// Testing grounds
		// Miss Cheevers
		{
			missCheeversStep = new MissCheeversStep(this);
			pwMissCheeversStep = missCheeversStep.puzzleWrapStepWithDefaultText("Solve Miss Cheevers' puzzle.");
			pwMissCheeversStep.conditionToHideInSidebar(new ConfigRequirement(this.getConfig()::solvePuzzles));

			leaveMissCheeversRoom = new ObjectStep(this, ObjectID.RD_ROOM6_EXITDOOR, new WorldPoint(2478, 4940, 0), "Leave the room by the second door to enter the portal.");

			var finishedRoom = new VarbitRequirement(VarbitID.RD_ROOM6_COMPLETE, 1);

			cMissCheevers = new ConditionalStep(this, pwMissCheeversStep);
			cMissCheevers.addStep(and(missCheeversStep.hasFirstDoorOpen, missCheeversStep.bronzeKey, finishedRoom), leaveMissCheeversRoom);
		}

		// Sir Tinley
		{
			talkToSirTinley = new NpcStep(this, NpcID.RD_OBSERVER_ROOM_4, "Talk to Sir Tinley. Once you have pressed continue do not do anything or you will fail.");
			doNothingStep = new DetailedQuestStep(this, "Press Continue and do nothing. Sir Tinley will eventually talk to you and let you pass.");
			leaveSirTinleyRoom = new ObjectStep(this, ObjectID.RD_ROOM4_EXITDOOR, "Leave through the portal to continue.");

			var waitForCondition = new VarbitRequirement(VarbitID.RD_TEMPLOCK_2, 1, Operation.GREATER_EQUAL);
			var finishedRoom = new VarbitRequirement(VarbitID.RD_ROOM4_COMPLETE, 1);

			sirTinleyStep = new ConditionalStep(this, talkToSirTinley);
			sirTinleyStep.addStep(finishedRoom, leaveSirTinleyRoom);
			sirTinleyStep.addStep(waitForCondition, doNothingStep);
		}

		// Ms Hynn Terprett
		{
			var finishedRoom = new VarbitRequirement(VarbitID.RD_ROOM7_COMPLETE, 1);

			msHynnDialogQuiz = new MsHynnAnswerDialogQuizStep(this);
			pwMsHynnTerprett = msHynnDialogQuiz.puzzleWrapStepWithDefaultText("Talk to Ms Hynn Terprett and answer the riddle.");

			leaveMsHynnTerprettRoom = new ObjectStep(this, ObjectID.RD_ROOM7_EXITDOOR, "Leave through the door to enter the portal and continue.");

			msHynnTerprettStep = new ConditionalStep(this, pwMsHynnTerprett);
			msHynnTerprettStep.addStep(finishedRoom, leaveMsHynnTerprettRoom);
		}

		// Sir Ren Itchood
		{
			var talkToSirRenItchood = new NpcStep(this, NpcID.RD_OBSERVER_ROOM_5, "Talk to Sir Ren Itchood to receive the clue.");
			talkToSirRenItchood.addDialogSteps("Can I have the clue for the door?");

			sirRenStep = new SirRenItchoodStep(this, talkToSirRenItchood);
		}

		// Lady Table
		{
			ladyTableStep = new LadyTableStep(this);
		}

		// Sir Spishyus
		{
			var chickenOnLeftPoint = new WorldPoint(2473, 4970, 0);
			var chickenOnRightPoint = new WorldPoint(2487, 4974, 0);
			var foxOnRightPoint = new WorldPoint(2485, 4974, 0);
			var grainOnRightPoint = new WorldPoint(2486, 4974, 0);

			var foxOnRightSide = new VarbitRequirement(VarbitID.RD_FOXLEFT, 0);
			var foxOnLeftSide = new VarbitRequirement(VarbitID.RD_FOXRIGHT, 1);
			var foxNotOnRightSide = new VarbitRequirement(VarbitID.RD_FOXLEFT, 1);
			var foxNotOnLeftSide = new VarbitRequirement(VarbitID.RD_FOXRIGHT, 0);
			var chickenOnRightSide = new VarbitRequirement(VarbitID.RD_CHICKLEFT, 0);
			var chickenOnLeftSide = new VarbitRequirement(VarbitID.RD_CHICKRIGHT, 1);
			var chickenNotOnRightSide = new VarbitRequirement(VarbitID.RD_CHICKLEFT, 1);
			var chickenNotOnLeftSide = new VarbitRequirement(VarbitID.RD_CHICKRIGHT, 0);
			var grainOnRightSide = new VarbitRequirement(VarbitID.RD_GRAINLEFT, 0);
			var grainOnLeftSide = new VarbitRequirement(VarbitID.RD_GRAINRIGHT, 1);
			var grainNotOnRightSide = new VarbitRequirement(VarbitID.RD_GRAINLEFT, 1);
			var grainNotOnLeftSide = new VarbitRequirement(VarbitID.RD_GRAINRIGHT, 0);
			var finishedSpishyus = new VarbitRequirement(VarbitID.RD_ROOM1_COMPLETE, 1);

			var westSide = new Zone(new WorldPoint(2472, 4976, 0), new WorldPoint(2479, 4968, 0));
			var playerOnWestSide = new ZoneRequirement(westSide);
			var playerOnEastSide = not(playerOnWestSide);

			var foxPickedUp = and(foxNotOnLeftSide, foxNotOnRightSide);
			var chickenPickedUp = and(chickenNotOnRightSide, chickenNotOnLeftSide);
			var grainPickedUp = and(grainNotOnLeftSide, grainNotOnRightSide);

			moveChickenOnRightToLeft = new ObjectStep(this, ObjectID.RD_ROOM2_CHICKEN_MULTI, chickenOnRightPoint, getSpishyusPickupText("Chicken", true)).puzzleWrapStep(true);

			moveFoxOnRightToLeft = new ObjectStep(this, ObjectID.RD_ROOM2_FOX_MULTI, foxOnRightPoint, getSpishyusPickupText("Fox", true)).puzzleWrapStep(true);

			var moveChickenToLeft = new ObjectStep(this, ObjectID.RD_BRIDGE_LEFT, new WorldPoint(2483, 4972, 0), getSpishyusMoveText("Chicken", false));
			moveChickenToLeft.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveChickenOnRightToLeft).getSolvingStep().addSubSteps(moveChickenToLeft);

			var moveFoxToWest = new ObjectStep(this, ObjectID.RD_BRIDGE_LEFT, new WorldPoint(2483, 4972, 0), getSpishyusMoveText("Fox", false));
			moveFoxToWest.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveFoxOnRightToLeft).getSolvingStep().addSubSteps(moveFoxToWest);

			moveChickenOnLeftToRight = new ObjectStep(this, ObjectID.RD_ROOM2_CHICKEN_MULTI_RIGHT, chickenOnLeftPoint, getSpishyusPickupText("Chicken", false)).puzzleWrapStep(true);
			var moveChickenToRight = new ObjectStep(this, ObjectID.RD_BRIDGE_RIGHT, new WorldPoint(2477, 4972, 0), getSpishyusMoveText("Chicken", true));
			moveChickenToRight.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveChickenOnLeftToRight).getSolvingStep().addSubSteps(moveChickenToRight);

			moveGrainOnRightToLeft = new ObjectStep(this, ObjectID.RD_ROOM2_GRAIN_MULTI, grainOnRightPoint, getSpishyusPickupText("Grain", true)).puzzleWrapStep(true);
			var moveGrainToLeft = new ObjectStep(this, ObjectID.RD_BRIDGE_LEFT, new WorldPoint(2483, 4972, 0), getSpishyusMoveText("Grain", false));
			moveGrainToLeft.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveGrainOnRightToLeft).getSolvingStep().addSubSteps(moveGrainToLeft);

			moveChickenOnRightToLeftAgain = new ObjectStep(this, ObjectID.RD_ROOM2_CHICKEN_MULTI, chickenOnRightPoint, getSpishyusPickupText("Chicken", true)).puzzleWrapStep(true);
			var moveChickenToLeftAgain = new ObjectStep(this, ObjectID.RD_BRIDGE_LEFT, new WorldPoint(2483, 4972, 0), getSpishyusMoveText("Chicken", false));
			moveChickenToLeftAgain.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveChickenOnRightToLeftAgain).getSolvingStep().addSubSteps(moveChickenToLeftAgain);

			sirSpishyusPuzzle = new ConditionalStep(this, moveChickenOnRightToLeft, "Solve Sir Spishyus' riddle.");

			var dropChickenWest = new DetailedQuestStep(this, "Drop the Chicken on the west side from your equipped items.");
			((PuzzleWrapperStep) moveChickenOnRightToLeft).getSolvingStep().addSubSteps(dropChickenWest);

			// 1. Move Chicken from east to west
			sirSpishyusPuzzle.addStep(and(chickenOnRightSide, foxOnRightSide, grainOnRightSide), moveChickenOnRightToLeft);
			sirSpishyusPuzzle.addStep(and(chickenPickedUp, foxOnRightSide, grainOnRightSide, playerOnEastSide), moveChickenToLeft);
			sirSpishyusPuzzle.addStep(and(chickenPickedUp, foxOnRightSide, grainOnRightSide), dropChickenWest);

			var moveToEastSide1 = new ObjectStep(this, ObjectID.RD_BRIDGE_RIGHT, new WorldPoint(2477, 4972, 0), "Cross the bridge to the east side.");
			moveToEastSide1.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveFoxOnRightToLeft).getSolvingStep().addSubSteps(moveToEastSide1);

			var dropFoxWest = new DetailedQuestStep(this, "Drop the Fox on the west side from your equipped items.");
			((PuzzleWrapperStep) moveFoxOnRightToLeft).getSolvingStep().addSubSteps(dropFoxWest);

			// 2. Return to east side, then move Fox from east to west
			sirSpishyusPuzzle.addStep(and(chickenOnLeftSide, foxOnRightSide, grainOnRightSide, playerOnWestSide), moveToEastSide1);
			sirSpishyusPuzzle.addStep(and(chickenOnLeftSide, foxOnRightSide, grainOnRightSide), moveFoxOnRightToLeft);
			sirSpishyusPuzzle.addStep(and(chickenOnLeftSide, foxPickedUp, grainOnRightSide, playerOnEastSide), moveFoxToWest);
			sirSpishyusPuzzle.addStep(and(chickenOnLeftSide, foxPickedUp, grainOnRightSide), dropFoxWest);

			var dropChickenEast = new DetailedQuestStep(this, "Drop the Chicken on the east side from your equipped items.");
			((PuzzleWrapperStep) moveChickenOnLeftToRight).getSolvingStep().addSubSteps(dropChickenEast);

			// 3. Move chicken from west to east
			sirSpishyusPuzzle.addStep(and(chickenOnLeftSide, foxOnLeftSide, grainOnRightSide), moveChickenOnLeftToRight);
			sirSpishyusPuzzle.addStep(and(chickenPickedUp, foxOnLeftSide, grainOnRightSide, playerOnWestSide), moveChickenToRight);
			sirSpishyusPuzzle.addStep(and(chickenPickedUp, foxOnLeftSide, grainOnRightSide), dropChickenEast);

			var dropGrainWest = new DetailedQuestStep(this, "Drop the Grain on the west side from your equipped items.");
			((PuzzleWrapperStep) moveGrainOnRightToLeft).getSolvingStep().addSubSteps(dropGrainWest);

			// 4. Move grain from east to west
			sirSpishyusPuzzle.addStep(and(chickenOnRightSide, foxOnLeftSide, grainOnRightSide), moveGrainOnRightToLeft);
			sirSpishyusPuzzle.addStep(and(chickenOnRightSide, foxOnLeftSide, grainPickedUp, playerOnEastSide), moveGrainToLeft);
			sirSpishyusPuzzle.addStep(and(chickenOnRightSide, foxOnLeftSide, grainPickedUp), dropGrainWest);

			var moveToEastSide2 = new ObjectStep(this, ObjectID.RD_BRIDGE_RIGHT, new WorldPoint(2477, 4972, 0), "Cross the bridge to the east side.");
			moveToEastSide2.setForceClickboxHighlight(true);
			((PuzzleWrapperStep) moveChickenOnRightToLeftAgain).getSolvingStep().addSubSteps(moveToEastSide2);

			var dropChickenWest2 = new DetailedQuestStep(this, "Drop the Chicken on the west side from your equipped items.");
			((PuzzleWrapperStep) moveChickenOnRightToLeftAgain).getSolvingStep().addSubSteps(dropChickenWest2);

			// 5. Cross the bridge to the east, then move the chicken from east to west
			sirSpishyusPuzzle.addStep(and(chickenOnRightSide, foxOnLeftSide, grainOnLeftSide, playerOnWestSide), moveToEastSide2);
			sirSpishyusPuzzle.addStep(and(chickenOnRightSide, foxOnLeftSide, grainOnLeftSide), moveChickenOnRightToLeftAgain);
			sirSpishyusPuzzle.addStep(and(chickenPickedUp, foxOnLeftSide, grainOnLeftSide, playerOnEastSide), moveChickenToLeftAgain);
			sirSpishyusPuzzle.addStep(and(chickenPickedUp, foxOnLeftSide, grainOnLeftSide), dropChickenWest2);

			pwSirSpishyusStep = sirSpishyusPuzzle.puzzleWrapStepWithDefaultText("Solve Sir Spishyus' riddle.");
			pwSirSpishyusStep.conditionToHideInSidebar(new ConfigRequirement(this.getConfig()::solvePuzzles));

			leaveSirSpishyusRoom = new ObjectStep(this, ObjectID.RD_ROOM1_EXITDOOR, "Leave through the portal to continue.");

			cSirSpishyus = new ConditionalStep(this, pwSirSpishyusStep);
			cSirSpishyus.addStep(finishedSpishyus, leaveSirSpishyusRoom);
		}

		// Sir Kuam
		{
			var finishedRoom = new VarbitRequirement(VarbitID.RD_ROOM3_COMPLETE, 1);

			talkToSirKuam = new NpcStep(this, NpcID.RD_OBSERVER_ROOM_3, "Talk to Sir Kuam Ferentse to have him spawn Sir Leye.");
			killSirLeye = new NpcStep(this, NpcID.RD_COMBAT_NPC_ROOM_3, "Defeat Sir Leye to win this challenge. You must use the steel warhammer or your bare hands to deal the final hit on him.", true);

			leaveSirKuamRoom = new ObjectStep(this, ObjectID.RD_ROOM3_EXITDOOR, "Leave through the portal to continue.");
			var npcCondition = new NpcCondition(NpcID.RD_COMBAT_NPC_ROOM_3);

			sirKuamStep = new ConditionalStep(this, talkToSirKuam);

			sirKuamStep.addStep(finishedRoom, leaveSirKuamRoom);
			sirKuamStep.addStep(npcCondition, killSirLeye);
		}
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		cTalkToSirAmikVarze = new ConditionalStep(this, climbBottomSteps, "Talk to Sir Amik Varze.");
		cTalkToSirAmikVarze.addStep(isFirstFloorCastle, climbSecondSteps);
		cTalkToSirAmikVarze.addStep(isSecondFloorCastle, talkToSirAmikVarze);
		steps.put(0, cTalkToSirAmikVarze);

		var cTestingGrounds = new ConditionalStep(this, talkToSirTiffy);
		cTestingGrounds.addStep(isSecondFloorCastle, climbDownSecondFloorStaircase);
		cTestingGrounds.addStep(isFirstFloorCastle, climbDownfirstFloorStaircase);

		// Testing steps below
		cTestingGrounds.addStep(isInMissCheeversRoom, cMissCheevers);
		cTestingGrounds.addStep(isInSirTinleysRoom, sirTinleyStep);
		cTestingGrounds.addStep(isInMsHynnRoom, msHynnTerprettStep);
		cTestingGrounds.addStep(isInSirRenItchood, sirRenStep);
		cTestingGrounds.addStep(isInladyTableRoom, ladyTableStep);
		cTestingGrounds.addStep(isInSirSpishyusRoom, cSirSpishyus);
		cTestingGrounds.addStep(isInSirKuamsRoom, sirKuamStep);

		steps.put(1, cTestingGrounds);

		return steps;
	}

	private String getSpishyusPickupText(String itemName, boolean moveRightToLeft)
	{
		String firstSide = moveRightToLeft ? "east" : "west";
		String secondSide = moveRightToLeft ? "west" : "east";
		return "Pickup the " + itemName + " on the " + firstSide + " and move it to the "
			+ secondSide + " side by crossing the bridge";
	}

	private String getSpishyusMoveText(String itemName, boolean rightSide)
	{
		String dropSide = rightSide ? "east" : "west";
		return "Cross the bridge to the " + dropSide + " and drop the " + itemName + " from your equipped items.";
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestRequirement(QuestHelperQuest.BLACK_KNIGHTS_FORTRESS, QuestState.FINISHED),
			new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED)
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Sir Leye (level 20) with no items"
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
			new ExperienceReward(Skill.PRAYER, 1000),
			new ExperienceReward(Skill.AGILITY, 1000),
			new ExperienceReward(Skill.HERBLORE, 1000)
		);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Initiate Helm", ItemID.BASIC_TK_HELM, 1),
			new ItemReward("Coins", ItemID.COINS, 3000)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return List.of(
			new UnlockReward("Ability to respawn in Falador"),
			new UnlockReward("Access to Initiate Armor")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Starting out", List.of(
			cTalkToSirAmikVarze
		)));

		sections.add(new PanelDetails("Start the testing", List.of(
			talkToSirTiffy
		), List.of(
			noItemRequirement
		)));

		sections.add(new PanelDetails("Sir Tinley", List.of(
			talkToSirTinley,
			doNothingStep,
			leaveSirTinleyRoom
		)));

		sections.add(new PanelDetails("Ms. Hynn Terprett", List.of(
			pwMsHynnTerprett,
			leaveMsHynnTerprettRoom
		)));

		sections.add(new PanelDetails("Sir Kuam", List.of(
			talkToSirKuam,
			killSirLeye,
			leaveSirKuamRoom
		)));

		sections.add(new PanelDetails("Sir Spishyus", List.of(
			pwSirSpishyusStep,
			moveChickenOnRightToLeft,
			moveFoxOnRightToLeft,
			moveChickenOnLeftToRight,
			moveGrainOnRightToLeft,
			moveChickenOnRightToLeftAgain,
			leaveSirSpishyusRoom
		)));

		sections.add(new PanelDetails("Sir Ren Itchood",
			sirRenStep.getPanelSteps()
		));

		var missCheeversSection = new PanelDetails("Miss Cheevers", pwMissCheeversStep);
		missCheeversSection.addSteps(missCheeversStep.getPanelSteps());
		missCheeversSection.addSteps(leaveMissCheeversRoom);
		sections.add(missCheeversSection);

		sections.add(new PanelDetails("Lady Table",
			ladyTableStep.getPanelSteps()
		));

		return sections;
	}
}
