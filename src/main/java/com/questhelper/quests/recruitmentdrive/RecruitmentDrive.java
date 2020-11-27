package com.questhelper.quests.recruitmentdrive;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECRUITMENT_DRIVE
)
public class RecruitmentDrive extends BasicQuestHelper
{
	private ItemRequirement coinsRequirement, emptyInventory, wearingNothing;

	private ZoneCondition isFirstFloorCastle, isSecondFloorCastle,
		isInSirTinleysRoom, isInMsHynnRoom, isInSirKuamsRoom,
		isInSirSpishyusRoom, isInSirRenItchood, isInladyTableRoom,
		isInMsCheeversRoom;

	private ConditionalStep conditionalTalkToSirAmikVarze;

	private QuestStep talkToSirTiffany;

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
	ConditionalStep tableStep;

	// Ms Hynn
	private QuestStep talkToMsHynnTerprett;
	private MsHynnAnswerDialogQuizStep msHynnDialogQuiz;

	// Ms Cheeves
	private MsCheevesSetup msCheevesSetup;

	private int chickenOnRightId = 7279;
	private int chickenOnLeftId = 7280;
	private int grainOnRightId = 7282;
	private int foxOnRightId = 7275;

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
		steps.put(1, TalkToSirTiffanyInFaladorPark());

		return steps;
	}

	public void setupItemRequirements()
	{
		coinsRequirement = new ItemRequirement("Coins(If you are male)", ItemID.COINS, 3000);
		emptyInventory = new ItemRequirement("Empty Inventory", -1, 28);
		wearingNothing = new ItemRequirement("Have nothing equipped", -1, -1);
	}

	public void SetupZones()
	{
		Zone firstFloorZone = new Zone(new WorldPoint(2954, 3335, 1),
			new WorldPoint(2966, 3343, 1));
		Zone secondFloorZone = new Zone(new WorldPoint(2955, 3334, 1),
			new WorldPoint(2964, 3342, 2));

		Zone sirTinleyRoomZone = new Zone(new WorldPoint(2471,4954,0),
			new WorldPoint(2481,4960, 0));
		Zone msHynnRoomZone = new Zone(new WorldPoint(2446, 4934, 0),
			new WorldPoint(2457, 4946, 0));
		Zone sirKuamRoomZone = new Zone(new WorldPoint(2453,4958, 0),
			new WorldPoint(2466,4970, 0));
		Zone sirSphishyusZone = new Zone(new WorldPoint(2469,4968,0),
			new WorldPoint(2492, 4980, 0));
		Zone sirRenItchoodZone = new Zone(new WorldPoint(2438,4952, 0),
			new WorldPoint(2448,4962, 0));
		Zone ladyTableZone = new Zone(new WorldPoint(2445, 4974, 0),
			new WorldPoint(2461,4987, 0));

		isFirstFloorCastle = new ZoneCondition(firstFloorZone);
		isSecondFloorCastle = new ZoneCondition(secondFloorZone);
		isInSirTinleysRoom = new ZoneCondition(sirTinleyRoomZone);
		isInMsHynnRoom = new ZoneCondition(msHynnRoomZone);
		isInSirKuamsRoom = new ZoneCondition(sirKuamRoomZone);
		isInSirSpishyusRoom = new ZoneCondition(sirSphishyusZone);
		isInSirRenItchood = new ZoneCondition(sirRenItchoodZone);
		isInladyTableRoom = new ZoneCondition(ladyTableZone);
	}

	private QuestStep TalkToSirTiffanyInFaladorPark()
	{
		WorldPoint firstFloorStairsPosition = new WorldPoint(2955, 3338, 1);
		WorldPoint secondFloorStairsPosition = new WorldPoint(2960, 3339, 2);

		ObjectStep climbDownSecondFloorStaircase = new ObjectStep(this, ObjectID.STAIRCASE_24074,
			secondFloorStairsPosition, "Climb down the stairs from the second floor.");
		ObjectStep climbDownfirstFloorStaircase = new ObjectStep(this, ObjectID.STAIRCASE_24074,
			firstFloorStairsPosition, "Climb down the stairs from the first floor.");

		talkToSirTiffany = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, "Talk to Sir Tiffany Cashien in Falador Park. Ensure you are a female character as one of the tests require you to be.",
			emptyInventory, wearingNothing);

		ConditionalStep conditionalTalkToSirTiffany = new ConditionalStep(this, talkToSirTiffany);
		conditionalTalkToSirTiffany.addStep(isSecondFloorCastle, climbDownSecondFloorStaircase);
		conditionalTalkToSirTiffany.addStep(isFirstFloorCastle, climbDownfirstFloorStaircase);
		talkToSirTiffany.addDialogStep("Yes, let's go!");
		talkToSirTiffany.addSubSteps(climbDownfirstFloorStaircase,
			climbDownSecondFloorStaircase);
		getMsCheeves();

		// Testing steps below
		conditionalTalkToSirTiffany.addStep(isInSirTinleysRoom, getSirTinley());
		conditionalTalkToSirTiffany.addStep(isInMsHynnRoom, getMsHynnTerprett());
		conditionalTalkToSirTiffany.addStep(isInSirRenItchood, getSirRenItchood());
		conditionalTalkToSirTiffany.addStep(isInladyTableRoom, getTableStep());
		conditionalTalkToSirTiffany.addStep(isInSirSpishyusRoom, getSirSpishyus());
		conditionalTalkToSirTiffany.addStep(isInSirKuamsRoom, getSirKuam());

		conditionalTalkToSirTiffany.addStep(msCheevesSetup.getIsInMsCheeversRoom(), msCheevesSetup.getConditionalStep());

		//	conditionalTalkToSirTiffany.addStep(isInSirKuamsRoom, getSirKuam());
		return conditionalTalkToSirTiffany;
	}

	private ConditionalStep getTableStep()
	{
		LadyTableStep ladyTableStep = new LadyTableStep(this, -1, "TEST");
		// TODO find out when thing dissapears
		tableStep = new ConditionalStep(this, ladyTableStep) ;
		return tableStep;
	}

	private QuestStep getSirKuam()
	{
		VarbitCondition finishedRoom = new VarbitCondition(661, 1);

		talkToSirKuam = new NpcStep(this, NpcID.SIR_KUAM_FERENTSE, "Talk to Sir Kuam Ferentse to have him spawn Sir Leye");
		killSirLeye = new NpcStep(this, NpcID.SIR_LEYE,
			"Kill Sir Leye to win this challenge. You must be a female character or you can't kill him.", true);

		leaveSirKuamRoom = new ObjectStep(this, 7317, "Leaves through the portal to continue.");
		NpcCondition npcCondition = new NpcCondition(NpcID.SIR_LEYE);

		ConditionalStep sirKuamConditional = new ConditionalStep(this,talkToSirKuam );

		sirKuamConditional.addStep(finishedRoom, leaveSirKuamRoom);
		sirKuamConditional.addStep(npcCondition, killSirLeye);
		return sirKuamConditional;
	}

	private MsCheevesSetup getMsCheeves()
	{
		msCheevesSetup = new MsCheevesSetup(this);
		return msCheevesSetup;
	}

	private QuestStep getSirSpishyus()
	{
		WorldPoint chickenOnLeftPoint = new WorldPoint(2473, 4970, 0);
		WorldPoint chickenOnRightPoint = new WorldPoint(2487, 4974, 0);
		WorldPoint foxOnRightPoint = new WorldPoint(2485, 4974, 0);
		WorldPoint grainOnRightPoint = new WorldPoint(2486, 4974, 0);

		VarbitCondition foxOnRightSide = new VarbitCondition(680, 0);
		VarbitCondition foxOnLeftSide = new VarbitCondition(681, 1);
		VarbitCondition foxNotOnRightSide = new VarbitCondition(680, 1);
		VarbitCondition foxNotOnLeftSide = new VarbitCondition(681, 0);
		VarbitCondition chickenOnRightSide = new VarbitCondition(682, 0);
		VarbitCondition chickenOnLeftSide = new VarbitCondition(683, 1);
		VarbitCondition chickenNotOnRightSide = new VarbitCondition(682, 1);
		VarbitCondition chickenNotOnLeftSide = new VarbitCondition(683, 0);
		VarbitCondition grainOnRightSide = new VarbitCondition(684, 0);
		VarbitCondition grainOnLeftSide = new VarbitCondition(685, 1);
		VarbitCondition grainNotOnRightSide = new VarbitCondition(684, 1);
		VarbitCondition grainNotOnLeftSide = new VarbitCondition(685, 0);
		VarbitCondition finishedSpishyus = new VarbitCondition(659, 1);

		Conditions foxPickedUp = new Conditions(LogicType.AND, foxNotOnLeftSide, foxNotOnRightSide);
		Conditions chickenPickedUp = new Conditions(LogicType.AND, chickenNotOnRightSide, chickenNotOnLeftSide);
		Conditions grainPickedUp = new Conditions(LogicType.AND, grainNotOnLeftSide, grainNotOnRightSide);

		moveChickenOnRightToLeft = new ObjectStep(this, chickenOnRightId, chickenOnRightPoint,
			getSpishyusPickupText("Chicken", true));
		finishedSpishyusRoom =  new ObjectStep(this, 7274, "Leaves through the portal to continue.");

		moveFoxOnRightToLeft = new ObjectStep(this, foxOnRightId, foxOnRightPoint,
			getSpishyusPickupText("Fox", true));

		DetailedQuestStep moveChickenToLeft = new DetailedQuestStep(this , getSpishyusMoveText("Chicken", false));
		moveChickenOnRightToLeft.addSubSteps(moveChickenToLeft);

		DetailedQuestStep moveFoxToLeft = new DetailedQuestStep(this, getSpishyusMoveText("Fox", false));
		moveFoxOnRightToLeft.addSubSteps(moveFoxToLeft);

		moveChickenOnLeftToRight = new ObjectStep(this, chickenOnLeftId, chickenOnLeftPoint,
			getSpishyusPickupText("Chicken", false));
		DetailedQuestStep moveChickenToRight = new DetailedQuestStep(this, getSpishyusMoveText("Chicken", true));
		moveChickenOnLeftToRight.addSubSteps(moveChickenToRight);

		moveGrainOnRightToLeft = new ObjectStep(this, grainOnRightId, grainOnRightPoint,
			getSpishyusPickupText("Grain", true));
		DetailedQuestStep moveGrainToLeft = new DetailedQuestStep(this, getSpishyusMoveText("Grain", false));
		moveGrainOnRightToLeft.addSubSteps(moveGrainToLeft);

		moveChickenOnRightToLeftAgain = new ObjectStep(this, chickenOnRightId, chickenOnRightPoint,
			getSpishyusPickupText("Chicken", true));
		DetailedQuestStep moveChickenToLeftAgain = new DetailedQuestStep(this , getSpishyusMoveText("Chicken", false));
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
		String firstSide = moveRightToLeft ? "right" : "left";
		String secondSide = moveRightToLeft ? "left" : "right";
		return "Pickup the " + itemName + " on the " + firstSide + " and move it to the "
			+ secondSide +" side by crossing the bridge";
	}

	private String getSpishyusMoveText(String itemName, boolean rightSide)
	{
		String dropSide = rightSide ? "right" : "left";
		return "Move to the " + dropSide + " and drop the "	+ itemName;
	}

	private QuestStep getSirRenItchood()
	{
		NpcStep talkToSirRenItchood = new NpcStep(this, NpcID.SIR_REN_ITCHOOD, "Talk to Sir Ren Itchood to recieve the clue");
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
		leaveSirTinleyRoom = new ObjectStep(this, 7320, "Leaves through the portal to continue.");

		VarbitCondition waitForCondition = new VarbitCondition(667, 1, Operation.GREATER_EQUAL);
		VarbitCondition finishedRoom = new VarbitCondition(662, 1);

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
			"Climb up the stairs to the first floor.");

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
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(coinsRequirement));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Defeat a level 20 monsters with no gear"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> steps = new ArrayList<>();

		PanelDetails startingPanel = new PanelDetails("Starting out",
			new ArrayList<>(Arrays.asList(conditionalTalkToSirAmikVarze)));

		PanelDetails testing = new PanelDetails("Start the testing",
			new ArrayList<>(Arrays.asList(talkToSirTiffany)), emptyInventory, wearingNothing);

		PanelDetails sirTinleysRoom = new PanelDetails("Sir Tinley",
			new ArrayList<>(Arrays.asList(talkToSirTinley, doNothingStep, leaveSirTinleyRoom)));

		ArrayList<QuestStep> hynnSteps = new ArrayList<>();
		hynnSteps.add(talkToMsHynnTerprett);
		hynnSteps.addAll(msHynnDialogQuiz.getPanelSteps());
		PanelDetails msHynnsRoom = new PanelDetails("Ms HynnTerprett", hynnSteps);

		PanelDetails sirKuamRoom = new PanelDetails("Sir Kuam",
			new ArrayList<>(Arrays.asList(talkToSirKuam, killSirLeye, leaveSirKuamRoom)));

		PanelDetails sirSpishyusRoom = new PanelDetails("Sir Spishyus",
			new ArrayList<>(Arrays.asList(moveChickenOnRightToLeft, moveFoxOnRightToLeft,
				moveChickenOnLeftToRight, moveGrainOnRightToLeft, moveChickenOnRightToLeftAgain)));

		PanelDetails sirRensRoom = new PanelDetails("Sir Ren Itchood", sirRenStep.getPanelSteps());

		PanelDetails missCheeversRoom = new PanelDetails("Mis Cheevers", msCheevesSetup.GetPanelSteps());

		steps.add(startingPanel);
		steps.add(testing);
		steps.add(sirKuamRoom);
		steps.add(sirSpishyusRoom);
		steps.add(msHynnsRoom);
		steps.add(sirTinleysRoom);
		steps.add(sirRensRoom);
		steps.add(missCheeversRoom);
		return steps;
	}
}
