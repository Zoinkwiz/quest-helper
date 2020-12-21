package com.questhelper.quests.blackknightfortress;

import com.questhelper.BankSlotIcons;
import com.questhelper.QuestHelperQuest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.ZoneCondition;

@QuestDescriptor(
	quest = QuestHelperQuest.BLACK_KNIGHTS_FORTRESS
)
public class BlackKnightFortress extends BasicQuestHelper
{
	ItemRequirement ironChainbody, cabbage, bronzeMed, food;

	ConditionForStep onTopOfFortress, inBasement, inSecretRoomGroundFloor, inSecretRoomFirstFloor, inSecretRoomSecondFloor, inCentralAreaFloor1, inMainEntrance, inWestRoomFloor1,
		inEastRoomFloor0, inEastRoomFloor1, inEastRoomFloor2, inListeningRoom, inCabbageHoleRoom, inPathToCabbageRoom, inEastTurret;

	Zone secretRoomFloor0, secretRoomFloor1, secretRoomFloor2, secretRoomFloor3, secretBasement, mainEntrance1, mainEntrance2, mainEntrance3, mainEntrance4, westFloor1,
		eastRoom1Floor0, eastRoom2Floor0, listeningRoom1, listeningRoom2, eastRoomFloor2, centralArea1Floor1, centralArea2Floor1, centralArea3Floor1,
		northPathToCabbageHole1, northPathToCabbageHole2, northPathToCabbageHole3, cabbageHoleRoom, eastRoom1Floor1, eastRoom2Floor1, eastRoom3Floor1, eastRoom4Floor1, eastTurret;

	QuestStep speakToAmik, enterFortress, pushWall, climbUpLadder1, climbUpLadder2, climbUpLadder3, climbUpLadder4, climbUpLadder5, climbUpLadder6,
		climbDownLadder1, climbDownLadder2, climbDownLadder3, climbDownLadder4, climbDownLadder5, climbDownLadder6, listenAtGrill, pushWall2,
		returnToAmik, exitBasement, exitTopOfFortress, exitEastTurret, exitWestRoomFirstFloor, goBackDownFromCabbageZone, goUpLadderToCabbageZone;
	ObjectStep useCabbageOnHole;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, speakToAmik);

		ConditionalStep infiltrateTheFortress = new ConditionalStep(this, enterFortress);
		infiltrateTheFortress.addStep(inListeningRoom, listenAtGrill);
		infiltrateTheFortress.addStep(inEastRoomFloor1, climbDownLadder6);
		infiltrateTheFortress.addStep(inEastRoomFloor2, climbDownLadder5);
		infiltrateTheFortress.addStep(inCentralAreaFloor1, climbUpLadder4);
		infiltrateTheFortress.addStep(inSecretRoomSecondFloor, climbDownLadder3);
		infiltrateTheFortress.addStep(inSecretRoomFirstFloor, climbUpLadder2);
		infiltrateTheFortress.addStep(inSecretRoomGroundFloor, climbUpLadder1);
		infiltrateTheFortress.addStep(new Conditions(false, LogicType.OR, inMainEntrance, inEastRoomFloor0), pushWall);
		infiltrateTheFortress.addStep(inWestRoomFloor1, exitWestRoomFirstFloor);
		infiltrateTheFortress.addStep(inBasement, exitBasement);
		infiltrateTheFortress.addStep(onTopOfFortress, exitTopOfFortress);
		infiltrateTheFortress.addStep(inEastTurret, exitEastTurret);
		infiltrateTheFortress.addStep(new Conditions(false, LogicType.OR, inCabbageHoleRoom, inPathToCabbageRoom), goBackDownFromCabbageZone);

		steps.put(1, infiltrateTheFortress);

		ConditionalStep sabotageThePotion = new ConditionalStep(this, enterFortress);
		sabotageThePotion.addStep(new Conditions(false, LogicType.OR, inSecretRoomGroundFloor, inMainEntrance, inEastRoomFloor0), goUpLadderToCabbageZone);
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

		steps.put(3, returnToAmik);

		return steps;
	}

	private void setupZones()
	{
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

	private void setupItemRequirements()
	{
		ironChainbody = new ItemRequirement("Iron chainbody", ItemID.IRON_CHAINBODY, 1, true);
		ironChainbody.setTip("You can buy one from the Chainmail Shop in south Falador");
		cabbage = new ItemRequirement("Cabbage (NOT from Draynor Manor)", ItemID.CABBAGE);
		cabbage.setTip("You can get one from the Edgeville Monastary east of the Black Knights; Fortress.");
		bronzeMed = new ItemRequirement("Bronze med helm", ItemID.BRONZE_MED_HELM, 1, true);
		bronzeMed.setTip("You can get one from the helmet shop in Barbarian Village.");

		food = new ItemRequirement("Food", -1);
		food.setDisplayItemId(BankSlotIcons.getFood());
	}

	private void setupConditions()
	{
		onTopOfFortress = new ZoneCondition(secretRoomFloor3);
		inBasement = new ZoneCondition(secretBasement);
		inEastTurret = new ZoneCondition(eastTurret);
		inSecretRoomGroundFloor = new ZoneCondition(secretRoomFloor0);
		inSecretRoomFirstFloor = new ZoneCondition(secretRoomFloor1);
		inSecretRoomSecondFloor = new ZoneCondition(secretRoomFloor2);
		inMainEntrance = new ZoneCondition(mainEntrance1, mainEntrance2, mainEntrance3, mainEntrance4);
		inCentralAreaFloor1 = new ZoneCondition(centralArea1Floor1, centralArea2Floor1, centralArea3Floor1);
		inWestRoomFloor1 = new ZoneCondition(westFloor1);
		inEastRoomFloor0 = new ZoneCondition(eastRoom1Floor0, eastRoom2Floor0);
		inEastRoomFloor1 = new ZoneCondition(eastRoom1Floor1, eastRoom2Floor1, eastRoom3Floor1, eastRoom4Floor1);
		inEastRoomFloor2 = new ZoneCondition(eastRoomFloor2);
		inListeningRoom = new ZoneCondition(listeningRoom1, listeningRoom2);
		inPathToCabbageRoom = new ZoneCondition(northPathToCabbageHole1, northPathToCabbageHole2, northPathToCabbageHole3);
		inCabbageHoleRoom = new ZoneCondition(cabbageHoleRoom);
	}

	private void setupSteps()
	{
		speakToAmik = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, new WorldPoint(2959, 3339, 2), "Speak to Sir Amik Varze on the 2nd floor of Falador Castle.");
		speakToAmik.addDialogStep("I seek a quest!");
		speakToAmik.addDialogStep("I laugh in the face of danger!");
		speakToAmik.addDialogStep("Ok, I'll do my best.");

		/* Path to grill */
		enterFortress = new ObjectStep(this, ObjectID.STURDY_DOOR, new WorldPoint(3016, 3514, 0), "Enter the Black Knights' Fortress. Be prepared for multiple level 33 Black Knights to attack you.",
			ironChainbody, bronzeMed, cabbage);
		pushWall = new ObjectStep(this, ObjectID.WALL_2341, new WorldPoint(3016, 3517, 0), "Push the wall to enter a secret room.");
		climbUpLadder1 = new ObjectStep(this, ObjectID.LADDER_17148, new WorldPoint(3015, 3519, 0),
			"Climb up the ladder.");
		climbUpLadder2 = new ObjectStep(this, ObjectID.LADDER_17148, new WorldPoint(3016, 3519, 1),
			"Climb up the next ladder.");
		climbDownLadder3 = new ObjectStep(this, ObjectID.LADDER_17149, new WorldPoint(3017, 3516, 2),
			"Climb down the ladder south of you.");
		climbUpLadder4 = new ObjectStep(this, ObjectID.LADDER_17148, new WorldPoint(3023, 3513, 1),
			"Climb up the ladder east of you.");
		climbDownLadder5 = new ObjectStep(this, ObjectID.LADDER_17149, new WorldPoint(3025, 3513, 2),
			"Climb down the next ladder east of you.");
		climbDownLadder6 = new ObjectStep(this, ObjectID.LADDER_17149, new WorldPoint(3021, 3510, 1),
			"Climb down the ladder west of you.");
		listenAtGrill = new ObjectStep(this, ObjectID.GRILL, new WorldPoint(3026, 3507, 0),
			"Listen at the grill.");

		/* Path to cabbage hole */
		climbUpLadder6 = new ObjectStep(this, ObjectID.LADDER_17148, new WorldPoint(3021, 3510, 0),
			"Climb back up the ladder from the listening room.", cabbage);
		climbUpLadder5 = new ObjectStep(this, ObjectID.LADDER_17148, new WorldPoint(3025, 3513, 1),
			"Climb up the ladder in the chapel room east of you.", cabbage);
		climbDownLadder4 = new ObjectStep(this, ObjectID.LADDER_17149, new WorldPoint(3023, 3513, 2),
			"Climb down the ladder west of you.", cabbage);
		climbUpLadder3 = new ObjectStep(this, ObjectID.LADDER_17148, new WorldPoint(3017, 3516, 1),
			"Climb up the ladder west of you.", cabbage);
		climbDownLadder2 = new ObjectStep(this, ObjectID.LADDER_17149, new WorldPoint(3016, 3519, 2),
			"Climb down the ladder north of you.", cabbage);
		climbDownLadder1 = new ObjectStep(this, ObjectID.LADDER_17149, new WorldPoint(3015, 3519, 1),
			"Climb down the next ladder.", cabbage);
		goUpLadderToCabbageZone = new ObjectStep(this, ObjectID.LADDER_17159, new WorldPoint(3022, 3518, 0),
			"Go into the east room and climb the ladder there. When trying to go through the door to the room, you'll have to go through some dialog. Select option 2.", cabbage);
		goUpLadderToCabbageZone.addDialogStep("I don't care. I'm going in anyway.");
		pushWall2 = new ObjectStep(this, ObjectID.WALL_2341, new WorldPoint(3030, 3510, 1),
			"Push the wall to enter the storage room", cabbage);
		useCabbageOnHole = new ObjectStep(this, ObjectID.HOLE_2336, new WorldPoint(3031, 3507, 1), "USE the cabbage on the hole here. Be careful not to eat it.", cabbage);
		useCabbageOnHole.addIcon(ItemID.CABBAGE);

		goBackDownFromCabbageZone = new ObjectStep(this, ObjectID.LADDER_17160, new WorldPoint(3022, 3518, 1),
			"Climb back down the ladder to continue.");
		exitEastTurret = new ObjectStep(this, ObjectID.STAIRCASE_17155, new WorldPoint(3029, 3507, 3), "Go back downstairs to continue.");
		exitBasement = new ObjectStep(this, ObjectID.LADDER_25844, new WorldPoint(1867, 4244, 0), "Leave the basement to continue.");
		exitTopOfFortress = new ObjectStep(this, ObjectID.STAIRCASE_17155, new WorldPoint(3010, 3516, 3), "Leave the basement to continue.");
		exitWestRoomFirstFloor = new ObjectStep(this, ObjectID.STAIRCASE_17155, new WorldPoint(3011, 3515, 1), "Go back downstairs to continue");

		returnToAmik = new NpcStep(this, NpcID.SIR_AMIK_VARZE_4771, new WorldPoint(2959, 3339, 2), "Return to Sir Amik Varze in Falador Castle to complete the quest.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(bronzeMed);
		reqs.add(ironChainbody);
		reqs.add(cabbage);

		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(new ItemRequirement("Teleports to Falador", ItemID.FALADOR_TELEPORT));
		reqs.add(food);

		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Able to survive being attacked by multiple level 33 Black Knights"));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Talk to Sir Amik Varze", new ArrayList<>(Collections.singletonList(speakToAmik))));
		allSteps.add(new PanelDetails("Infiltrate the fortress",
			new ArrayList<>(Arrays.asList(enterFortress, pushWall, climbUpLadder1, climbUpLadder2, climbDownLadder3,
				climbUpLadder4, climbDownLadder5, climbDownLadder6, listenAtGrill)),
			bronzeMed, ironChainbody, cabbage));
		allSteps.add(new PanelDetails("Sabotage the potion",
			new ArrayList<>(Arrays.asList(climbUpLadder6, climbUpLadder5, climbDownLadder4, climbUpLadder3, climbDownLadder2, climbDownLadder1,
				goUpLadderToCabbageZone, pushWall2, useCabbageOnHole))));
		allSteps.add(new PanelDetails("Return to Sir Amik Varze", new ArrayList<>(Collections.singletonList(returnToAmik))));
		return allSteps;
	}
}
