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
package com.questhelper.quests.recruitmentdrive;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.NoItemRequirement;
import com.questhelper.requirements.player.PlayerModelRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.util.ItemSlots;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECRUITMENT_DRIVE
)
public class RecruitmentDrive extends BasicQuestHelper
{
	private ItemRequirement coinsRequirement;
	private NoItemRequirement noItemRequirement;
	private Requirement femaleReq;
	private ZoneRequirement isFirstFloorCastle, isSecondFloorCastle,
		isInSirTinleysRoom, isInMsHynnRoom, isInSirKuamsRoom,
		isInSirSpishyusRoom, isInSirRenItchood, isInladyTableRoom;

	private ConditionalStep conditionalTalkToSirAmikVarze;

	private QuestStep talkToSirTiffy;

	//Sir Tinsley steps
	QuestStep doNothingStep, talkToSirTinley, leaveSirTinleyRoom;

	// Sir Kuam Ferentse steps
	QuestStep talkToSirKuam, killSirLeye, leaveSirKuamRoom;

	// Sir Spishyus
	QuestStep moveChickenOnRightToLeft, moveFoxOnRightToLeft, moveChickenOnLeftToRight,
		moveGrainOnRightToLeft, moveChickenOnRightToLeftAgain, finishedSpishyusRoom;

	// Sir Ren
	SirRenItchoodStep sirRenStep;

	// Lady Table
	LadyTableStep ladyTableStep;

	// Ms Hynn
	private QuestStep talkToMsHynnTerprett;
	private MsHynnAnswerDialogQuizStep msHynnDialogQuiz;

	// Ms Cheeves
	private MsCheevesSetup msCheevesSetup;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupItemRequirements();
		SetupZones();

		return getSteps();
	}

	private Map<Integer, QuestStep> getSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, TalkToSirAmikVarze());
		steps.put(1, TalkToSirTiffyInFaladorPark());

		return steps;
	}

	public void setupItemRequirements()
	{
		coinsRequirement = new ItemRequirement("Coins(If you are male)", ItemCollections.getCoins(), 3000);
		noItemRequirement = new NoItemRequirement("No items or equipment carried", ItemSlots.ANY_EQUIPPED_AND_INVENTORY);

		femaleReq = new PlayerModelRequirement(true);
	}

	public void SetupZones()
	{
		Zone firstFloorZone = new Zone(new WorldPoint(2954, 3335, 1),
			new WorldPoint(2966, 3343, 1));
		Zone secondFloorZone = new Zone(new WorldPoint(2955, 3334, 2),
			new WorldPoint(2964, 3342, 2));

		Zone sirTinleyRoomZone = new Zone(new WorldPoint(2471, 4954, 0),
			new WorldPoint(2481, 4960, 0));
		Zone msHynnRoomZone = new Zone(new WorldPoint(2446, 4934, 0),
			new WorldPoint(2457, 4946, 0));
		Zone sirKuamRoomZone = new Zone(new WorldPoint(2453, 4958, 0),
			new WorldPoint(2466, 4970, 0));
		Zone sirSphishyusZone = new Zone(new WorldPoint(2469, 4968, 0),
			new WorldPoint(2492, 4980, 0));
		Zone sirRenItchoodZone = new Zone(new WorldPoint(2438, 4952, 0),
			new WorldPoint(2448, 4962, 0));
		Zone ladyTableZone = new Zone(new WorldPoint(2445, 4974, 0),
			new WorldPoint(2461, 4987, 0));

		isFirstFloorCastle = new ZoneRequirement(firstFloorZone);
		isSecondFloorCastle = new ZoneRequirement(secondFloorZone);
		isInSirTinleysRoom = new ZoneRequirement(sirTinleyRoomZone);
		isInMsHynnRoom = new ZoneRequirement(msHynnRoomZone);
		isInSirKuamsRoom = new ZoneRequirement(sirKuamRoomZone);
		isInSirSpishyusRoom = new ZoneRequirement(sirSphishyusZone);
		isInSirRenItchood = new ZoneRequirement(sirRenItchoodZone);
		isInladyTableRoom = new ZoneRequirement(ladyTableZone);
	}

	private QuestStep TalkToSirTiffyInFaladorPark()
	{
		WorldPoint firstFloorStairsPosition = new WorldPoint(2955, 3338, 1);
		WorldPoint secondFloorStairsPosition = new WorldPoint(2960, 3339, 2);

		ObjectStep climbDownSecondFloorStaircase = new ObjectStep(this, ObjectID.STAIRCASE_24074,
			secondFloorStairsPosition, "Climb down the stairs from the second floor.");
		ObjectStep climbDownfirstFloorStaircase = new ObjectStep(this, ObjectID.STAIRCASE_24074,
			firstFloorStairsPosition, "Climb down the stairs from the first floor.");

		talkToSirTiffy = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN,
			"Talk to Sir Tiffy Cashien in Falador Park.", noItemRequirement);

		ConditionalStep conditionalTalkToSirTiffy = new ConditionalStep(this, talkToSirTiffy);
		conditionalTalkToSirTiffy.addStep(isSecondFloorCastle, climbDownSecondFloorStaircase);
		conditionalTalkToSirTiffy.addStep(isFirstFloorCastle, climbDownfirstFloorStaircase);
		talkToSirTiffy.addDialogStep("Yes, let's go!");
		talkToSirTiffy.addSubSteps(climbDownfirstFloorStaircase,
			climbDownSecondFloorStaircase);
		getMsCheeves();

		// Testing steps below
		conditionalTalkToSirTiffy.addStep(isInSirTinleysRoom, getSirTinley());
		conditionalTalkToSirTiffy.addStep(isInMsHynnRoom, getMsHynnTerprett());
		conditionalTalkToSirTiffy.addStep(isInSirRenItchood, getSirRenItchood());
		conditionalTalkToSirTiffy.addStep(isInladyTableRoom, getLadyTableStep());
		conditionalTalkToSirTiffy.addStep(isInSirSpishyusRoom, getSirSpishyus());
		conditionalTalkToSirTiffy.addStep(isInSirKuamsRoom, getSirKuam());

		conditionalTalkToSirTiffy.addStep(msCheevesSetup.getIsInMsCheeversRoom(), msCheevesSetup.getConditionalStep());
		return conditionalTalkToSirTiffy;
	}

	private LadyTableStep getLadyTableStep()
	{
		this.ladyTableStep = new LadyTableStep(this);
		return this.ladyTableStep;
	}

	private QuestStep getSirKuam()
	{
		VarbitRequirement finishedRoom = new VarbitRequirement(661, 1);

		talkToSirKuam = new NpcStep(this, NpcID.SIR_KUAM_FERENTSE, "Talk to Sir Kuam Ferentse to have him spawn Sir Leye");
		killSirLeye = new NpcStep(this, NpcID.SIR_LEYE,
			"Kill Sir Leye to win this challenge. You must be a female character or you can't kill him.", true,
			femaleReq);

		leaveSirKuamRoom = new ObjectStep(this, 7317, "Leave through the portal to continue.");
		NpcCondition npcCondition = new NpcCondition(NpcID.SIR_LEYE);

		ConditionalStep sirKuamConditional = new ConditionalStep(this, talkToSirKuam);

		sirKuamConditional.addStep(finishedRoom, leaveSirKuamRoom);
		sirKuamConditional.addStep(npcCondition, killSirLeye);
		return sirKuamConditional;
	}

	private void getMsCheeves()
	{
		msCheevesSetup = new MsCheevesSetup(this);
	}

	private QuestStep getSirSpishyus()
	{
		WorldPoint chickenOnLeftPoint = new WorldPoint(2473, 4970, 0);
		WorldPoint chickenOnRightPoint = new WorldPoint(2487, 4974, 0);
		WorldPoint foxOnRightPoint = new WorldPoint(2485, 4974, 0);
		WorldPoint grainOnRightPoint = new WorldPoint(2486, 4974, 0);

		VarbitRequirement foxOnRightSide = new VarbitRequirement(680, 0);
		VarbitRequirement foxOnLeftSide = new VarbitRequirement(681, 1);
		VarbitRequirement foxNotOnRightSide = new VarbitRequirement(680, 1);
		VarbitRequirement foxNotOnLeftSide = new VarbitRequirement(681, 0);
		VarbitRequirement chickenOnRightSide = new VarbitRequirement(682, 0);
		VarbitRequirement chickenOnLeftSide = new VarbitRequirement(683, 1);
		VarbitRequirement chickenNotOnRightSide = new VarbitRequirement(682, 1);
		VarbitRequirement chickenNotOnLeftSide = new VarbitRequirement(683, 0);
		VarbitRequirement grainOnRightSide = new VarbitRequirement(684, 0);
		VarbitRequirement grainOnLeftSide = new VarbitRequirement(685, 1);
		VarbitRequirement grainNotOnRightSide = new VarbitRequirement(684, 1);
		VarbitRequirement grainNotOnLeftSide = new VarbitRequirement(685, 0);
		VarbitRequirement finishedSpishyus = new VarbitRequirement(659, 1);

		Conditions foxPickedUp = new Conditions(LogicType.AND, foxNotOnLeftSide, foxNotOnRightSide);
		Conditions chickenPickedUp = new Conditions(LogicType.AND, chickenNotOnRightSide, chickenNotOnLeftSide);
		Conditions grainPickedUp = new Conditions(LogicType.AND, grainNotOnLeftSide, grainNotOnRightSide);

		int chickenOnRightId = 7279;
		moveChickenOnRightToLeft = new ObjectStep(this, chickenOnRightId, chickenOnRightPoint,
			getSpishyusPickupText("Chicken", true));
		finishedSpishyusRoom = new ObjectStep(this, 7274, "Leave through the portal to continue.");

		int foxOnRightId = 7275;
		moveFoxOnRightToLeft = new ObjectStep(this, foxOnRightId, foxOnRightPoint,
			getSpishyusPickupText("Fox", true));

		DetailedQuestStep moveChickenToLeft = new DetailedQuestStep(this, getSpishyusMoveText("Chicken", false));
		moveChickenOnRightToLeft.addSubSteps(moveChickenToLeft);

		DetailedQuestStep moveFoxToLeft = new DetailedQuestStep(this, getSpishyusMoveText("Fox", false));
		moveFoxOnRightToLeft.addSubSteps(moveFoxToLeft);

		int chickenOnLeftId = 7280;
		moveChickenOnLeftToRight = new ObjectStep(this, chickenOnLeftId, chickenOnLeftPoint,
			getSpishyusPickupText("Chicken", false));
		DetailedQuestStep moveChickenToRight = new DetailedQuestStep(this, getSpishyusMoveText("Chicken", true));
		moveChickenOnLeftToRight.addSubSteps(moveChickenToRight);

		int grainOnRightId = 7282;
		moveGrainOnRightToLeft = new ObjectStep(this, grainOnRightId, grainOnRightPoint,
			getSpishyusPickupText("Grain", true));
		DetailedQuestStep moveGrainToLeft = new DetailedQuestStep(this, getSpishyusMoveText("Grain", false));
		moveGrainOnRightToLeft.addSubSteps(moveGrainToLeft);

		moveChickenOnRightToLeftAgain = new ObjectStep(this, chickenOnRightId, chickenOnRightPoint,
			getSpishyusPickupText("Chicken", true));
		DetailedQuestStep moveChickenToLeftAgain = new DetailedQuestStep(this, getSpishyusMoveText("Chicken", false));
		moveChickenOnRightToLeftAgain.addSubSteps(moveChickenToLeftAgain);

		ConditionalStep sirSpishyus = new ConditionalStep(this, moveChickenOnRightToLeft);
		sirSpishyus.addStep(finishedSpishyus, finishedSpishyusRoom);

		sirSpishyus.addStep(new Conditions(chickenOnRightSide, foxOnRightSide, grainOnRightSide), moveChickenOnRightToLeft);
		sirSpishyus.addStep(new Conditions(chickenPickedUp, foxOnRightSide, grainOnRightSide), moveChickenToLeft);
		sirSpishyus.addStep(new Conditions(chickenOnLeftSide, foxOnRightSide, grainOnRightSide), moveFoxOnRightToLeft);
		sirSpishyus.addStep(new Conditions(chickenOnLeftSide, foxPickedUp, grainOnRightSide), moveFoxToLeft);
		sirSpishyus.addStep(new Conditions(chickenOnLeftSide, foxOnLeftSide, grainOnRightSide), moveChickenOnLeftToRight);
		sirSpishyus.addStep(new Conditions(chickenPickedUp, foxOnLeftSide, grainOnRightSide), moveChickenToRight);

		sirSpishyus.addStep(new Conditions(chickenOnRightSide, foxOnLeftSide, grainOnRightSide), moveGrainOnRightToLeft);
		sirSpishyus.addStep(new Conditions(chickenOnRightSide, foxOnLeftSide, grainPickedUp), moveGrainToLeft);
		sirSpishyus.addStep(new Conditions(chickenOnRightSide, foxOnLeftSide, grainOnLeftSide), moveChickenOnRightToLeftAgain);
		sirSpishyus.addStep(new Conditions(chickenPickedUp, foxOnLeftSide, grainOnLeftSide), moveChickenToLeftAgain);

		return sirSpishyus;
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

	private QuestStep getSirRenItchood()
	{
		NpcStep talkToSirRenItchood = new NpcStep(this, NpcID.SIR_REN_ITCHOOD, "Talk to Sir Ren Itchood to recieve the clue.");
		talkToSirRenItchood.addDialogSteps("Can I have the clue for the door?");

		sirRenStep = new SirRenItchoodStep(this, talkToSirRenItchood);
		return sirRenStep;
	}

	private QuestStep getSirTinley()
	{
		talkToSirTinley = new NpcStep(this, NpcID.SIR_TINLEY,
			"Talk to Sir Tinley. \n Once you have pressed continue do not do anything or you will fail.");
		doNothingStep = new DetailedQuestStep(this,
			"Press Continue and do nothing. Sir Tinley will eventually talk to you and let you pass.");
		leaveSirTinleyRoom = new ObjectStep(this, 7320, "Leave through the portal to continue.");

		VarbitRequirement waitForCondition = new VarbitRequirement(667, 1, Operation.GREATER_EQUAL);
		VarbitRequirement finishedRoom = new VarbitRequirement(662, 1);

		ConditionalStep sirTinleyStep = new ConditionalStep(this, talkToSirTinley);
		sirTinleyStep.addStep(finishedRoom, leaveSirTinleyRoom);
		sirTinleyStep.addStep(waitForCondition, doNothingStep);

		return sirTinleyStep;
	}

	private QuestStep getMsHynnTerprett()
	{
		talkToMsHynnTerprett = new NpcStep(this, NpcID.MS_HYNN_TERPRETT,
			"Talk to Ms Hynn Terprett and answer the riddle.");

		msHynnDialogQuiz = new MsHynnAnswerDialogQuizStep(this, talkToMsHynnTerprett);
		return msHynnDialogQuiz;
	}

	private QuestStep TalkToSirAmikVarze()
	{
		WorldPoint bottomStairsPosition = new WorldPoint(2955, 3339, 0);
		WorldPoint secondStairsPosition = new WorldPoint(2961, 3339, 1);
		ObjectStep climbBottomSteps = new ObjectStep(this, ObjectID.STAIRCASE_24072, bottomStairsPosition,
			"Climb up the stairs to the first floor on the Falador Castle.");

		ObjectStep climbSecondSteps = new ObjectStep(this, ObjectID.STAIRCASE_24072, secondStairsPosition,
			"Climb up the stairs to talk to Sir Amik Vaze.");
		NpcStep talkToSirAmikVarze = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, "");
		talkToSirAmikVarze.addDialogStep("Yes please");

		conditionalTalkToSirAmikVarze = new ConditionalStep(this, climbBottomSteps,
			"Talk to Sir Amik Varze.");
		conditionalTalkToSirAmikVarze.addStep(isFirstFloorCastle, climbSecondSteps);
		conditionalTalkToSirAmikVarze.addStep(isSecondFloorCastle, talkToSirAmikVarze);
		conditionalTalkToSirAmikVarze.addSubSteps(climbSecondSteps, climbBottomSteps, talkToSirAmikVarze);
		return conditionalTalkToSirAmikVarze;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(coinsRequirement);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Sir Leye (level 20) with no items");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.BLACK_KNIGHTS_FORTRESS, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED));
		reqs.add(new PlayerModelRequirement(true));
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
				new ExperienceReward(Skill.PRAYER, 1000),
				new ExperienceReward(Skill.AGILITY, 1000),
				new ExperienceReward(Skill.HERBLORE, 1000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Initiate Helm", ItemID.INITIATE_SALLET, 1),
				new ItemReward("3000 Coins", ItemID.COINS_995, 3000),
				new ItemReward("Makeover Voucher (If male when starting quest)", ItemID.MAKEOVER_VOUCHER, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Ability to respawn in Falador"),
				new UnlockReward("Access to Initiate Armor"));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting out",
			new ArrayList<>(Collections.singletonList(conditionalTalkToSirAmikVarze)));

		PanelDetails testing = new PanelDetails("Start the testing",
			Collections.singletonList(talkToSirTiffy), noItemRequirement);

		PanelDetails sirTinleysRoom = new PanelDetails("Sir Tinley",
			Arrays.asList(talkToSirTinley, doNothingStep, leaveSirTinleyRoom));

		List<QuestStep> hynnSteps = new ArrayList<>();
		hynnSteps.add(talkToMsHynnTerprett);
		hynnSteps.addAll(msHynnDialogQuiz.getPanelSteps());
		PanelDetails msHynnsRoom = new PanelDetails("Ms Hynn Terprett", hynnSteps);

		PanelDetails sirKuamRoom = new PanelDetails("Sir Kuam",
			Arrays.asList(talkToSirKuam, killSirLeye, leaveSirKuamRoom));

		PanelDetails sirSpishyusRoom = new PanelDetails("Sir Spishyus",
			Arrays.asList(moveChickenOnRightToLeft, moveFoxOnRightToLeft,
				moveChickenOnLeftToRight, moveGrainOnRightToLeft, moveChickenOnRightToLeftAgain));

		PanelDetails sirRensRoom = new PanelDetails("Sir Ren Itchood", sirRenStep.getPanelSteps());

		PanelDetails missCheeversRoom = new PanelDetails("Mis Cheevers", msCheevesSetup.GetPanelSteps());

		PanelDetails ladyTable = new PanelDetails("Lady Table", ladyTableStep.getPanelSteps());

		steps.add(startingPanel);
		steps.add(testing);
		steps.add(sirKuamRoom);
		steps.add(sirSpishyusRoom);
		steps.add(msHynnsRoom);
		steps.add(sirTinleysRoom);
		steps.add(sirRensRoom);
		steps.add(missCheeversRoom);
		steps.add(ladyTable);
		return steps;
	}
}
