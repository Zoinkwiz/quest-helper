package com.questhelper.helpers.quests.blackknightfortress;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestPointRequirement;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ItemReward;
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

public class BlackKnightFortress extends BasicQuestHelper
{
	// Required items
	ItemRequirement ironChainbody;
	ItemRequirement cabbage;
	ItemRequirement bronzeMed;

	// Recommended items
	ItemRequirement teleportFalador;
	ItemRequirement armour;
	ItemRequirement food;

	// Zones
	Zone secretRoomFloor0;
	Zone secretRoomFloor1;
	Zone secretRoomFloor2;
	Zone secretRoomFloor3;
	Zone secretBasement;
	Zone mainEntrance1;
	Zone mainEntrance2;
	Zone mainEntrance3;
	Zone mainEntrance4;
	Zone westFloor1;
	Zone eastRoom1Floor0;
	Zone eastRoom2Floor0;
	Zone listeningRoom1;
	Zone listeningRoom2;
	Zone eastRoomFloor2;
	Zone centralArea1Floor1;
	Zone centralArea2Floor1;
	Zone centralArea3Floor1;
	Zone northPathToCabbageHole1;
	Zone northPathToCabbageHole2;
	Zone northPathToCabbageHole3;
	Zone cabbageHoleRoom;
	Zone eastRoom1Floor1;
	Zone eastRoom2Floor1;
	Zone eastRoom3Floor1;
	Zone eastRoom4Floor1;
	Zone eastTurret;
	Zone whiteKnightsCastleF1;
	Zone whiteKnightsCastleF2;

	// Miscellaneous requirements
	ZoneRequirement onTopOfFortress;
	ZoneRequirement inBasement;
	ZoneRequirement inSecretRoomGroundFloor;
	ZoneRequirement inSecretRoomFirstFloor;
	ZoneRequirement inSecretRoomSecondFloor;
	ZoneRequirement inCentralAreaFloor1;
	ZoneRequirement inMainEntrance;
	ZoneRequirement inWestRoomFloor1;
	ZoneRequirement inEastRoomFloor0;
	ZoneRequirement inEastRoomFloor1;
	ZoneRequirement inEastRoomFloor2;
	ZoneRequirement inListeningRoom;
	ZoneRequirement inCabbageHoleRoom;
	ZoneRequirement inPathToCabbageRoom;
	ZoneRequirement inEastTurret;
	ZoneRequirement inFaladorF1;
	ZoneRequirement inFaladorF2;

	// Steps
	NpcStep speakToAmik;
	ObjectStep enterFortress;
	ObjectStep pushWall;
	ObjectStep climbUpLadder1;
	ObjectStep climbUpLadder2;
	ObjectStep climbUpLadder3;
	ObjectStep climbUpLadder4;
	ObjectStep climbUpLadder5;
	ObjectStep climbUpLadder6;
	ObjectStep climbDownLadder1;
	ObjectStep climbDownLadder2;
	ObjectStep climbDownLadder3;
	ObjectStep climbDownLadder4;
	ObjectStep climbDownLadder5;
	ObjectStep climbDownLadder6;
	ObjectStep listenAtGrill;
	ObjectStep pushWall2;
	NpcStep returnToAmik;
	ObjectStep exitBasement;
	ObjectStep exitTopOfFortress;
	ObjectStep exitEastTurret;
	ObjectStep exitWestRoomFirstFloor;
	ObjectStep goBackDownFromCabbageZone;
	ObjectStep goUpLadderToCabbageZone;
	ObjectStep climbToWhiteKnightsCastleF1;
	ObjectStep climbToWhiteKnightsCastleF2;
	ObjectStep climbToWhiteKnightsCastleF1ToFinish;
	ObjectStep climbToWhiteKnightsCastleF2ToFinish;
	ObjectStep useCabbageOnHole;

	@Override
	protected void setupZones()
	{
		whiteKnightsCastleF1 = new Zone(new WorldPoint(2954, 3353, 1), new WorldPoint(2998, 3327, 1));
		whiteKnightsCastleF2 = new Zone(new WorldPoint(2954, 3353, 2), new WorldPoint(2998, 3327, 2));

		secretRoomFloor0 = new Zone(new WorldPoint(3015, 3517, 0), new WorldPoint(3016, 3519, 0));
		secretRoomFloor1 = new Zone(new WorldPoint(3015, 3517, 1), new WorldPoint(3016, 3519, 1));
		secretRoomFloor2 = new Zone(new WorldPoint(3007, 3513, 2), new WorldPoint(3018, 3519, 2));
		secretRoomFloor3 = new Zone(new WorldPoint(3009, 3514, 3), new WorldPoint(3012, 3517, 3));

		secretBasement = new Zone(new WorldPoint(1862, 4264, 0), new WorldPoint(1873, 4229, 0));
		mainEntrance1 = new Zone(new WorldPoint(3008, 3513, 0), new WorldPoint(3012, 3518, 0));
		mainEntrance2 = new Zone(new WorldPoint(3012, 3514, 0), new WorldPoint(3014, 3516, 0));
		mainEntrance3 = new Zone(new WorldPoint(3015, 3515, 0), new WorldPoint(3019, 3516, 0));
		mainEntrance4 = new Zone(new WorldPoint(3019, 3513, 0), new WorldPoint(3019, 3517, 0));
		eastRoom1Floor0 = new Zone(new WorldPoint(3020, 3513, 0), new WorldPoint(3030, 3518, 0));
		eastRoom2Floor0 = new Zone(new WorldPoint(3021, 3511, 0), new WorldPoint(3030, 3512, 0));

		eastRoom1Floor1 = new Zone(new WorldPoint(3021, 3506, 1), new WorldPoint(3025, 3512, 1));
		eastRoom2Floor1 = new Zone(new WorldPoint(3021, 3513, 1), new WorldPoint(3021, 3513, 1));
		eastRoom3Floor1 = new Zone(new WorldPoint(3026, 3510, 1), new WorldPoint(3029, 3515, 1));
		eastRoom4Floor1 = new Zone(new WorldPoint(3025, 3513, 1), new WorldPoint(3025, 3516, 1));

		eastRoomFloor2 = new Zone(new WorldPoint(3021, 3504, 2), new WorldPoint(3031, 3516, 2));

		listeningRoom1 = new Zone(new WorldPoint(3021, 3510, 0), new WorldPoint(3026, 3510, 0));
		listeningRoom2 = new Zone(new WorldPoint(3025, 3506, 0), new WorldPoint(3026, 3509, 0));

		westFloor1 = new Zone(new WorldPoint(3007, 3513, 1), new WorldPoint(3014, 3519, 1));

		centralArea1Floor1 = new Zone(new WorldPoint(3015, 3514, 1), new WorldPoint(3024, 3516, 1));
		centralArea2Floor1 = new Zone(new WorldPoint(3019, 3517, 1), new WorldPoint(3021, 3518, 1));
		centralArea3Floor1 = new Zone(new WorldPoint(3022, 3513, 1), new WorldPoint(3024, 3513, 1));

		northPathToCabbageHole1 = new Zone(new WorldPoint(3022, 3517, 1), new WorldPoint(3030, 3518, 1));
		northPathToCabbageHole2 = new Zone(new WorldPoint(3030, 3510, 1), new WorldPoint(3030, 3516, 1));
		northPathToCabbageHole3 = new Zone(new WorldPoint(3029, 3516, 1), new WorldPoint(3029, 3516, 1));

		cabbageHoleRoom = new Zone(new WorldPoint(3026, 3504, 1), new WorldPoint(3032, 3509, 1));

		eastTurret = new Zone(new WorldPoint(3027, 3505, 3), new WorldPoint(3031, 3508, 3));
	}

	@Override
	protected void setupRequirements()
	{
		ironChainbody = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY, 1, true).isNotConsumed();
		ironChainbody.setTooltip("You can buy one from the Chainmail Shop in south Falador");
		cabbage = new ItemRequirement("Cabbage (NOT from Draynor Manor)", ItemID.CABBAGE);
		cabbage.setTooltip("You can get one from the Edgeville Monastery east of the Black Knights' Fortress.");
		bronzeMed = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM, 1, true).isNotConsumed();
		bronzeMed.setTooltip("You can get one from the helmet shop in Barbarian Village.");

		teleportFalador = new ItemRequirement("Teleport to Falador", ItemID.POH_TABLET_FALADORTELEPORT);
		armour = new ItemRequirement("Armour", -1, -1).isNotConsumed();
		armour.setDisplayItemId(BankSlotIcons.getArmour());
		food = new ItemRequirement("Food", ItemCollections.GOOD_EATING_FOOD, -1);

		inFaladorF1 = new ZoneRequirement(whiteKnightsCastleF1);
		inFaladorF2 = new ZoneRequirement(whiteKnightsCastleF2);
		onTopOfFortress = new ZoneRequirement(secretRoomFloor3);
		inBasement = new ZoneRequirement(secretBasement);
		inEastTurret = new ZoneRequirement(eastTurret);
		inSecretRoomGroundFloor = new ZoneRequirement(secretRoomFloor0);
		inSecretRoomFirstFloor = new ZoneRequirement(secretRoomFloor1);
		inSecretRoomSecondFloor = new ZoneRequirement(secretRoomFloor2);
		inMainEntrance = new ZoneRequirement(mainEntrance1, mainEntrance2, mainEntrance3, mainEntrance4);
		inCentralAreaFloor1 = new ZoneRequirement(centralArea1Floor1, centralArea2Floor1, centralArea3Floor1);
		inWestRoomFloor1 = new ZoneRequirement(westFloor1);
		inEastRoomFloor0 = new ZoneRequirement(eastRoom1Floor0, eastRoom2Floor0);
		inEastRoomFloor1 = new ZoneRequirement(eastRoom1Floor1, eastRoom2Floor1, eastRoom3Floor1, eastRoom4Floor1);
		inEastRoomFloor2 = new ZoneRequirement(eastRoomFloor2);
		inListeningRoom = new ZoneRequirement(listeningRoom1, listeningRoom2);
		inPathToCabbageRoom = new ZoneRequirement(northPathToCabbageHole1, northPathToCabbageHole2, northPathToCabbageHole3);
		inCabbageHoleRoom = new ZoneRequirement(cabbageHoleRoom);
	}

	private void setupSteps()
	{
		climbToWhiteKnightsCastleF1 = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2955, 3339, 0),
			"Speak to Sir Amik Varze on the 2nd floor of Falador Castle.");
		climbToWhiteKnightsCastleF2 = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2961, 3339, 1),
			"Speak to Sir Amik Varze on the 2nd floor of Falador Castle.");

		speakToAmik = new NpcStep(this, NpcID.SIR_AMIK_VARZE, new WorldPoint(2959, 3339, 2),
			"Speak to Sir Amik Varze on the 2nd floor of Falador Castle.");
		speakToAmik.addDialogStep("I seek a quest!");
		speakToAmik.addDialogStep("I laugh in the face of danger!");
		speakToAmik.addDialogStep("Yes.");
		speakToAmik.addSubSteps(climbToWhiteKnightsCastleF1, climbToWhiteKnightsCastleF2);

		/* Path to grill */
		enterFortress = new ObjectStep(this, ObjectID.BKFORTRESSDOOR1, new WorldPoint(3016, 3514, 0), "Enter the Black Knights' Fortress. Be prepared for multiple level 33 Black Knights to attack you.",
			ironChainbody, bronzeMed, cabbage);
		pushWall = new ObjectStep(this, ObjectID.BKSECRETDOOR, new WorldPoint(3016, 3517, 0), "Push the wall to enter a secret room.");
		climbUpLadder1 = new ObjectStep(this, ObjectID.DK_LADDER, new WorldPoint(3015, 3519, 0),
			"Climb up the ladder.");
		climbUpLadder2 = new ObjectStep(this, ObjectID.DK_LADDER, new WorldPoint(3016, 3519, 1),
			"Climb up the next ladder.");
		climbDownLadder3 = new ObjectStep(this, ObjectID.DK_LADDERTOP, new WorldPoint(3017, 3516, 2),
			"Climb down the ladder south of you.");
		climbUpLadder4 = new ObjectStep(this, ObjectID.DK_LADDER, new WorldPoint(3023, 3513, 1),
			"Climb up the ladder east of you.");
		climbDownLadder5 = new ObjectStep(this, ObjectID.DK_LADDERTOP, new WorldPoint(3025, 3513, 2),
			"Climb down the next ladder east of you.");
		climbDownLadder6 = new ObjectStep(this, ObjectID.DK_LADDERTOP, new WorldPoint(3021, 3510, 1),
			"Climb down the ladder west of you.");
		listenAtGrill = new ObjectStep(this, ObjectID.WITCHGRILL, new WorldPoint(3026, 3507, 0),
			"Listen at the grill.");

		/* Path to cabbage hole */
		climbUpLadder6 = new ObjectStep(this, ObjectID.DK_LADDER, new WorldPoint(3021, 3510, 0),
			"Climb back up the ladder from the listening room.", cabbage);
		climbUpLadder5 = new ObjectStep(this, ObjectID.DK_LADDER, new WorldPoint(3025, 3513, 1),
			"Climb up the ladder in the chapel room east of you.", cabbage);
		climbDownLadder4 = new ObjectStep(this, ObjectID.DK_LADDERTOP, new WorldPoint(3023, 3513, 2),
			"Climb down the ladder west of you.", cabbage);
		climbUpLadder3 = new ObjectStep(this, ObjectID.DK_LADDER, new WorldPoint(3017, 3516, 1),
			"Climb up the ladder west of you.", cabbage);
		climbDownLadder2 = new ObjectStep(this, ObjectID.DK_LADDERTOP, new WorldPoint(3016, 3519, 2),
			"Climb down the ladder north of you.", cabbage);
		climbDownLadder1 = new ObjectStep(this, ObjectID.DK_LADDERTOP, new WorldPoint(3015, 3519, 1),
			"Climb down the next ladder.", cabbage);
		goUpLadderToCabbageZone = new ObjectStep(this, ObjectID.DK_MEETING_LADDER, new WorldPoint(3022, 3518, 0),
			"Go into the east room and climb the ladder there. When trying to go through the door to the room, you'll have to go through some dialog. Select option 2.", cabbage);
		goUpLadderToCabbageZone.addDialogStep("I don't care. I'm going in anyway.");
		pushWall2 = new ObjectStep(this, ObjectID.BKSECRETDOOR, new WorldPoint(3030, 3510, 1),
			"Push the wall to enter the storage room", cabbage);
		useCabbageOnHole = new ObjectStep(this, ObjectID.BLACKKNIGHTHOLE, new WorldPoint(3031, 3507, 1),
			"USE the cabbage on the hole here. Be careful not to eat it.", cabbage.highlighted());
		useCabbageOnHole.addIcon(ItemID.CABBAGE);

		goBackDownFromCabbageZone = new ObjectStep(this, ObjectID.DK_MEETING_LADDERTOP, new WorldPoint(3022, 3518, 1),
			"Climb back down the ladder to continue.");
		exitEastTurret = new ObjectStep(this, ObjectID.DK_SPIRALSTAIRSTOP, new WorldPoint(3029, 3507, 3), "Go back downstairs to continue.");
		exitBasement = new ObjectStep(this, ObjectID.KR_BKF_BASEMENT_LADDER, new WorldPoint(1867, 4244, 0), "Leave the basement to continue.");
		exitTopOfFortress = new ObjectStep(this, ObjectID.DK_SPIRALSTAIRSTOP, new WorldPoint(3010, 3516, 3), "Leave the basement to continue.");
		exitWestRoomFirstFloor = new ObjectStep(this, ObjectID.DK_SPIRALSTAIRSTOP, new WorldPoint(3011, 3515, 1), "Go back downstairs to continue");

		climbToWhiteKnightsCastleF1ToFinish = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2955, 3339,
			0),
			"Return to Sir Amik Varze in Falador Castle to complete the quest.");
		climbToWhiteKnightsCastleF2ToFinish = new ObjectStep(this, ObjectID.FAI_FALADOR_CASTLE_SPIRALSTAIRS, new WorldPoint(2961, 3339,
			1),
			"Return to Sir Amik Varze in Falador Castle to complete the quest.");

		returnToAmik = new NpcStep(this, NpcID.SIR_AMIK_VARZE, new WorldPoint(2959, 3339, 2),
			"Return to Sir Amik Varze in Falador Castle to complete the quest.");
		returnToAmik.addSubSteps(climbToWhiteKnightsCastleF1ToFinish, climbToWhiteKnightsCastleF2ToFinish);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		var steps = new HashMap<Integer, QuestStep>();

		initializeRequirements();
		setupSteps();

		var goStartQuest = new ConditionalStep(this, climbToWhiteKnightsCastleF1);
		goStartQuest.addStep(inFaladorF2, speakToAmik);
		goStartQuest.addStep(inFaladorF1, climbToWhiteKnightsCastleF2);
		steps.put(0, goStartQuest);

		var infiltrateTheFortress = new ConditionalStep(this, enterFortress);
		infiltrateTheFortress.addStep(inListeningRoom, listenAtGrill);
		infiltrateTheFortress.addStep(inEastRoomFloor1, climbDownLadder6);
		infiltrateTheFortress.addStep(inEastRoomFloor2, climbDownLadder5);
		infiltrateTheFortress.addStep(inCentralAreaFloor1, climbUpLadder4);
		infiltrateTheFortress.addStep(inSecretRoomSecondFloor, climbDownLadder3);
		infiltrateTheFortress.addStep(inSecretRoomFirstFloor, climbUpLadder2);
		infiltrateTheFortress.addStep(inSecretRoomGroundFloor, climbUpLadder1);
		infiltrateTheFortress.addStep(or(inMainEntrance, inEastRoomFloor0), pushWall);
		infiltrateTheFortress.addStep(inWestRoomFloor1, exitWestRoomFirstFloor);
		infiltrateTheFortress.addStep(inBasement, exitBasement);
		infiltrateTheFortress.addStep(onTopOfFortress, exitTopOfFortress);
		infiltrateTheFortress.addStep(inEastTurret, exitEastTurret);
		infiltrateTheFortress.addStep(or(inCabbageHoleRoom, inPathToCabbageRoom), goBackDownFromCabbageZone);

		steps.put(1, infiltrateTheFortress);

		var sabotageThePotion = new ConditionalStep(this, enterFortress);
		sabotageThePotion.addStep(or(inSecretRoomGroundFloor, inMainEntrance, inEastRoomFloor0), goUpLadderToCabbageZone);
		sabotageThePotion.addStep(inCabbageHoleRoom, useCabbageOnHole);
		sabotageThePotion.addStep(inPathToCabbageRoom, pushWall2);
		sabotageThePotion.addStep(inSecretRoomFirstFloor, climbDownLadder1);
		sabotageThePotion.addStep(inSecretRoomSecondFloor, climbDownLadder2);
		sabotageThePotion.addStep(inCentralAreaFloor1, climbUpLadder3);
		sabotageThePotion.addStep(inEastRoomFloor2, climbDownLadder4);
		sabotageThePotion.addStep(inEastRoomFloor1, climbUpLadder5);
		sabotageThePotion.addStep(inListeningRoom, climbUpLadder6);
		sabotageThePotion.addStep(inWestRoomFloor1, exitWestRoomFirstFloor);
		sabotageThePotion.addStep(inBasement, exitBasement);
		sabotageThePotion.addStep(onTopOfFortress, exitTopOfFortress);
		sabotageThePotion.addStep(inEastTurret, exitEastTurret);

		steps.put(2, sabotageThePotion);

		var goFinishQuest = new ConditionalStep(this, climbToWhiteKnightsCastleF1ToFinish);
		goFinishQuest.addStep(inFaladorF2, returnToAmik);
		goFinishQuest.addStep(inFaladorF1, climbToWhiteKnightsCastleF2ToFinish);
		steps.put(3, goFinishQuest);

		return steps;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return List.of(
			new QuestPointRequirement(12)
		);
	}


	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			bronzeMed,
			ironChainbody,
			cabbage
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			teleportFalador,
			food,
			armour
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Able to survive being attacked by multiple level 33 Black Knights"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(3);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 2500)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Talk to Sir Amik Varze", List.of(
			speakToAmik
		)));

		sections.add(new PanelDetails("Infiltrate the fortress", List.of(
			enterFortress,
			pushWall,
			climbUpLadder1,
			climbUpLadder2,
			climbDownLadder3,
			climbUpLadder4,
			climbDownLadder5,
			climbDownLadder6,
			listenAtGrill
		), List.of(
			bronzeMed,
			ironChainbody,
			cabbage
		)));

		sections.add(new PanelDetails("Sabotage the potion", List.of(
			climbUpLadder6,
			climbUpLadder5,
			climbDownLadder4,
			climbUpLadder3,
			climbDownLadder2,
			climbDownLadder1,
			goUpLadderToCabbageZone,
			pushWall2,
			useCabbageOnHole
		)));

		sections.add(new PanelDetails("Return to Sir Amik Varze", List.of(
			returnToAmik
		)));

		return sections;
	}
}
