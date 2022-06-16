/*
 * Copyright (c) 2021, Zoinkwiz
 * Copyright (c) 2020, Snoodelz
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
package com.questhelper.quests.clocktower;

import com.questhelper.ItemCollections;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarPlayer;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.QuestDescriptor;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.CLOCK_TOWER
)
public class ClockTower extends BasicQuestHelper
{
	ItemRequirement bucketOfWater, noteAboutWater, staminaPotions, ardougneCloak, redCog, blueCog, blackCog, whiteCog, ratPoison;

	Zone basement, firstFloor, secondFloor, groundFloor, secretPath, secretPath2, secretPath3, secretPath4,
		secretPath5, secretPath6, cell;

	Requirement inBasement, inGroundFloor, inFirstFloor, inSecondFloor, inSecretPath, inCell, startedQuestDuringSession,
		synced, firstLeverDown, pulledFirstLeverUp, secondLeverUp, poisonedRats, placedRedCog, placedBlueCog, placedWhiteCog,
		placedBlackCog;

	QuestStep talkToKojo, syncStep;

	QuestStep enterBasement, pickUpRedCog, climbToGroundFloorFromBasement, redCogOnRedSpindle;

	QuestStep goToLadderCedric, pushWall, climbCellLadder, pickUpBlueCog, climbToFirstFloor, blueCogOnBlueSpindle;

	QuestStep climbFromFirstFloorToGround, pickupBlackCog, blackCogOnBlackSpindle;

	QuestStep northWesternDoor, pickUpRatPoison, pullFirstLever, ratPoisonFood, westernGate, pickUpWhiteCog, climbWhiteLadder,
		climbToSecondFloor, whiteCogOnWhiteSpindle, climbFromSecondFloorToFirst;

	QuestStep kojoReward;

	ConditionalStep getRedCog, getBlueCog, getBlackCog, getWhiteCog;

	ConditionalStep goToBasementForRed, goToBasementForBlue, goToBasementForBlack, goToBasementForWhite,
		goToFirstFloorWithBlueCog, goToGroundFloorWithRedCog, goToBasementWithBlackCog, goToSecondFloorWithWhiteCog,
		goFinishQuest;


	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goStart = new ConditionalStep(this, talkToKojo);
		// This is so we can lock the startedQuestDuringSession to true, so we don't need to ask to sync after
		goStart.addStep(startedQuestDuringSession, talkToKojo);

		steps.put(0, talkToKojo);

		getRedCog = new ConditionalStep(this, goToBasementForRed);
		getRedCog.addStep(redCog, goToGroundFloorWithRedCog);
		getRedCog.addStep(inBasement, pickUpRedCog);

		getBlueCog = new ConditionalStep(this, goToBasementForBlue);
		getBlueCog.addStep(blueCog, goToFirstFloorWithBlueCog);
		getBlueCog.addStep(inCell, pickUpBlueCog);
		getBlueCog.addStep(inSecretPath, pushWall);

		getBlackCog = new ConditionalStep(this, goToBasementForBlack);
		getBlackCog.addStep(blackCog, goToBasementWithBlackCog);
		getBlackCog.addStep(inBasement, pickupBlackCog);

		getWhiteCog = new ConditionalStep(this, goToBasementForWhite);
		getWhiteCog.addStep(new Conditions(whiteCog, inBasement), climbWhiteLadder);
		getWhiteCog.addStep(new Conditions(whiteCog), goToSecondFloorWithWhiteCog);
		getWhiteCog.addStep(new Conditions(inBasement, pulledFirstLeverUp, poisonedRats), pickUpWhiteCog);
		getWhiteCog.addStep(new Conditions(inBasement, ratPoison, pulledFirstLeverUp), ratPoisonFood);
		getWhiteCog.addStep(new Conditions(inBasement, ratPoison), pullFirstLever);
		getWhiteCog.addStep(inBasement, pickUpRatPoison);

		ConditionalStep doQuest = new ConditionalStep(this, syncStep);
		doQuest.addStep(new Conditions(placedRedCog, placedBlueCog, placedBlackCog, placedWhiteCog, synced), goFinishQuest);
		doQuest.addStep(new Conditions(placedRedCog, placedBlueCog, placedBlackCog, synced), getWhiteCog);
		doQuest.addStep(new Conditions(placedRedCog, placedBlueCog, synced), getBlackCog);
		doQuest.addStep(new Conditions(placedRedCog, synced), getBlueCog);
		doQuest.addStep(synced, getRedCog);
		steps.put(1, doQuest);
		steps.put(2, doQuest);
		steps.put(3, doQuest);
		steps.put(4, doQuest);
		steps.put(5, goFinishQuest);
		steps.put(6, goFinishQuest);
		steps.put(7, goFinishQuest);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		bucketOfWater = new ItemRequirement("Bucket of Water or a pair of ice gloves or smiths gloves(i)", ItemID.BUCKET_OF_WATER);
		bucketOfWater.addAlternates(ItemID.ICE_GLOVES, ItemID.SMITHS_GLOVES_I);
		bucketOfWater.setTooltip("There is a bucket spawn next to the well east of the Clocktower. You can fill it on" +
			" the well");
		noteAboutWater = new ItemRequirement("There's a bucket and a well and just next to brother cedric for the black cog", -1, -1);
		staminaPotions = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS);
		ardougneCloak = new ItemRequirement("Ardougne Cloak to teleport to monastery", ItemID.ARDOUGNE_CLOAK_1);
		ardougneCloak.addAlternates(ItemID.ARDOUGNE_CLOAK_2, ItemID.ARDOUGNE_CLOAK_3, ItemID.ARDOUGNE_CLOAK_4);
		redCog = new ItemRequirement("Red Cog", ItemID.RED_COG);
		blueCog = new ItemRequirement("Blue Cog", ItemID.BLUE_COG);
		whiteCog = new ItemRequirement("White Cog", ItemID.WHITE_COG);
		blackCog = new ItemRequirement("Black Cog", ItemID.BLACK_COG);
		ratPoison = new ItemRequirement("Rat Poison", ItemID.RAT_POISON);
	}

	public void setupZones()
	{
		groundFloor = new Zone(new WorldPoint(2563, 3239, 0), new WorldPoint(2573, 3245, 0));
		secondFloor = new Zone(new WorldPoint(2563, 3239, 2), new WorldPoint(2573, 3245, 2));
		firstFloor = new Zone(new WorldPoint(2563, 3239, 1), new WorldPoint(2573, 3245, 1));
		basement = new Zone(new WorldPoint(2000, 9573, 0), new WorldPoint(3000, 9700, 0));
		secretPath = new Zone(new WorldPoint(2614, 9625, 0), new WorldPoint(2622, 9662, 0));
		secretPath2 = new Zone(new WorldPoint(2611, 9647, 0), new WorldPoint(2613, 9661, 0));
		secretPath3 = new Zone(new WorldPoint(2596, 9600, 0), new WorldPoint(2613, 9631, 0));
		secretPath4 = new Zone(new WorldPoint(2580, 9600, 0), new WorldPoint(9595, 9603, 0));
		secretPath5 = new Zone(new WorldPoint(2580, 9600, 0), new WorldPoint(2580, 9628, 0));
		secretPath6 = new Zone(new WorldPoint(2576, 9629, 0), new WorldPoint(2580, 9631, 0));
		cell = new Zone(new WorldPoint(2571, 9630, 0), new WorldPoint(2575, 9633, 0));
	}

	public void setupConditions()
	{
		inSecondFloor = new ZoneRequirement(secondFloor);
		inFirstFloor = new ZoneRequirement(firstFloor);
		inGroundFloor = new ZoneRequirement(groundFloor);
		inBasement = new ZoneRequirement(basement);
		inCell = new ZoneRequirement(cell);
		inSecretPath = new ZoneRequirement(secretPath, secretPath2, secretPath3, secretPath4, secretPath5, secretPath6);

		startedQuestDuringSession = new Conditions(true, new VarplayerRequirement(QuestVarPlayer.QUEST_CLOCK_TOWER.getId(), 0));

		synced = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(119, 2, "<col=7f0000>Clock Tower</col>"),
			startedQuestDuringSession
		);

		firstLeverDown = new ObjectCondition(ObjectID.LEVER, new WorldPoint(2591, 9661, 0));
		pulledFirstLeverUp = new ObjectCondition(ObjectID.LEVER_35, new WorldPoint(2591, 9661, 0));
		secondLeverUp = new ObjectCondition(ObjectID.LEVER_34, new WorldPoint(2593, 9661, 0));
		poisonedRats = new ChatMessageRequirement("The rats swarm towards the poisoned food...");

		placedRedCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(119, 3, true, "<str>I have successfully placed the Red Cog on its spindle"),
			new ChatMessageRequirement(inGroundFloor, "The cog fits perfectly.")
		);
		placedBlueCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(119, 3, true, "<str>I have successfully placed the Blue Cog on its spindle"),
			new ChatMessageRequirement(inFirstFloor, "The cog fits perfectly.")
		);
		placedBlackCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(119, 3, true, "<str>I have successfully placed the Black Cog on its spindle"),
			new ChatMessageRequirement(inBasement, "The cog fits perfectly.")
		);
		placedWhiteCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(119, 3, true, "<str>I have successfully placed the White Cog on its spindle"),
			new ChatMessageRequirement(inSecondFloor, "The cog fits perfectly.")
		);
	}

	public void setupSteps()
	{
		talkToKojo = new NpcStep(this, NpcID.BROTHER_KOJO, new WorldPoint(2570, 3245, 0), "Talk to Brother Kojo at the clock tower.");
		talkToKojo.addDialogStep("OK old monk, what can I do?");

		syncStep = new DetailedQuestStep(this, "Open your Quest Journal to sync your current state.");

		pickUpRedCog = new DetailedQuestStep(this, new WorldPoint(2583, 9613, 0),
			"Go through the south east door and pick up the red cog.", redCog);
		redCogOnRedSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_29, new WorldPoint(2568, 3243, 0),
			"Use the red cog on the red spindle.", redCog.highlighted());
		redCogOnRedSpindle.addIcon(ItemID.RED_COG);

		goToLadderCedric = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2621, 3261, 0), "");
		pushWall = new ObjectStep(this, ObjectID.WALL_1597, new WorldPoint(2575, 9631, 0), "Follow the tunnel and " +
			"push the wall at the end.");
		pickUpBlueCog = new DetailedQuestStep(this, new WorldPoint(2574, 9633, 0), "Pick up the blue cog.", blueCog);
		climbCellLadder = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2572, 9631, 0), "");
		blueCogOnBlueSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_32, new WorldPoint(2569, 3240, 1),
			"Use the blue cog on the blue spindle.", blueCog.highlighted());
		blueCogOnBlueSpindle.addIcon(ItemID.BLUE_COG);

		pickupBlackCog = new DetailedQuestStep(this, new WorldPoint(2613, 9639, 0), "Enter the north east door, and " +
			"pick up the black cog with a bucket of water, alternatively you can equip ice gloves or smith gloves(i).", bucketOfWater, blackCog);
		blackCogOnBlackSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_30, new WorldPoint(2570, 9642, 0),
			"", blackCog.highlighted());
		blackCogOnBlackSpindle.addIcon(ItemID.BLACK_COG);

		northWesternDoor = new ObjectStep(this, ObjectID.DOOR_1535, new WorldPoint(2575, 9651, 0), "Go through the north-western door.");
		pickUpRatPoison = new DetailedQuestStep(this, new WorldPoint(2564, 9662, 0), "Pick up the rat poison in the " +
			"north west of the dungeon.", ratPoison);
		pullFirstLever = new ObjectStep(this, ObjectID.LEVER, new WorldPoint(2591, 9661, 0),
			"Pull the marked lever up.");
		ratPoisonFood = new ObjectStep(this, ObjectID.FOOD_TROUGH, new WorldPoint(2587, 9654, 0),
			"Use the rat poison on the food trough. Wait till the rats have died.", ratPoison.highlighted());
		ratPoisonFood.addIcon(ItemID.RAT_POISON);
		westernGate = new ObjectStep(this, ObjectID.GATE_39, new WorldPoint(2579, 9656, 0), "Go through the gate.");
		pickUpWhiteCog = new DetailedQuestStep(this, new WorldPoint(2577, 9655, 0), "Pick up the white cog.", whiteCog);
		climbWhiteLadder = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2575, 9655, 0), "Head up the ladder.");
		whiteCogOnWhiteSpindle = new ObjectStep(this, ObjectID.CLOCK_SPINDLE_31, new WorldPoint(2567, 3241, 2),
			"", whiteCog.highlighted());
		whiteCogOnWhiteSpindle.addIcon(ItemID.WHITE_COG);
		kojoReward = new NpcStep(this, NpcID.BROTHER_KOJO, new WorldPoint(2570, 3245, 0), "Talk to Brother Kojo for your reward.");

		enterBasement = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2566, 3242, 0), "");
		climbFromFirstFloorToGround = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(2573, 3241, 1), "");
		climbFromFirstFloorToGround.addDialogStep("Climb down the stairs.");
		climbFromSecondFloorToFirst = new ObjectStep(this, ObjectID.STAIRCASE_16673, new WorldPoint(2572, 3241, 2), "");

		climbToGroundFloorFromBasement = new ObjectStep(this, ObjectID.LADDER_17385, new WorldPoint(2566, 9642, 0), "");
		climbToFirstFloor = new ObjectStep(this, ObjectID.STAIRCASE_16671, new WorldPoint(2573, 3241, 0), "");
		climbToSecondFloor = new ObjectStep(this, ObjectID.STAIRCASE_16672, new WorldPoint(2573, 3241, 1), "");
		climbToSecondFloor.addDialogStep("Climb up the stairs.");

		ConditionalStep goToBasementSteps = new ConditionalStep(this, enterBasement);
		goToBasementSteps.addStep(inSecondFloor, climbFromSecondFloorToFirst);
		goToBasementSteps.addStep(inFirstFloor, climbFromFirstFloorToGround);
		goToBasementSteps.addStep(new Conditions(LogicType.OR, inSecretPath, inCell), climbCellLadder);

		ConditionalStep goToCellSteps = new ConditionalStep(this, goToLadderCedric);
		goToCellSteps.addStep(inBasement, climbToGroundFloorFromBasement);
		goToCellSteps.addStep(inSecondFloor, climbFromSecondFloorToFirst);
		goToCellSteps.addStep(inFirstFloor, climbFromFirstFloorToGround);

		ConditionalStep goToGroundFloor = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		goToGroundFloor.addStep(new Conditions(LogicType.OR, inSecretPath, inCell), climbCellLadder);
		goToGroundFloor.addStep(inBasement, climbToGroundFloorFromBasement);
		goToGroundFloor.addStep(inSecondFloor, climbFromSecondFloorToFirst);
		goToGroundFloor.addStep(inFirstFloor, climbFromFirstFloorToGround);

		ConditionalStep goToFirstFloor = new ConditionalStep(this, climbToFirstFloor);
		goToFirstFloor.addStep(new Conditions(LogicType.OR, inSecretPath, inCell), climbCellLadder);
		goToFirstFloor.addStep(inBasement, climbToGroundFloorFromBasement);
		goToFirstFloor.addStep(inSecondFloor, climbFromSecondFloorToFirst);

		ConditionalStep goToSecondFloor = new ConditionalStep(this, climbToFirstFloor);
		goToSecondFloor.addStep(new Conditions(LogicType.OR, inSecretPath, inCell), climbCellLadder);
		goToSecondFloor.addStep(inBasement, climbToGroundFloorFromBasement);
		goToSecondFloor.addStep(inFirstFloor, climbToSecondFloor);

		goToBasementForRed = new ConditionalStep(this, goToBasementSteps, "Enter the clocktower's basement for the " +
			"red cog.");
		goToBasementForBlue = new ConditionalStep(this, goToCellSteps, "Climb down the ladder to the east of the " +
			"Clocktower, next to Brother Cedric.");
		goToBasementForWhite = new ConditionalStep(this, goToBasementSteps, "Enter the clocktower's basement for the " +
			"white cog.");
		goToBasementForBlack = new ConditionalStep(this, goToBasementSteps, "Enter the clocktower's basement for the " +
			"black cog.");

		goToFirstFloorWithBlueCog = new ConditionalStep(this, goToFirstFloor,
			"Place the blue cog on the peg on the first floor.");
		goToFirstFloorWithBlueCog.addStep(inFirstFloor, blueCogOnBlueSpindle);

		goToGroundFloorWithRedCog = goToGroundFloor.copy();
		goToGroundFloorWithRedCog.setText("Place the red cog on the peg on the ground floor.");
		goToGroundFloorWithRedCog.addStep(null, redCogOnRedSpindle);

		goToBasementWithBlackCog = new ConditionalStep(this, goToBasementSteps,
			"Place the black cog on the peg in the basement.");
		goToBasementWithBlackCog.addStep(new Conditions(inBasement, new Conditions(LogicType.NOR, inSecretPath, inCell)),
			blackCogOnBlackSpindle);

		goToSecondFloorWithWhiteCog = new ConditionalStep(this, goToSecondFloor,
			"Place the white cog on the peg on the second floor.");
		goToSecondFloorWithWhiteCog.addStep(inSecondFloor, whiteCogOnWhiteSpindle);
		goToSecondFloorWithWhiteCog.addSubSteps(climbWhiteLadder);

		goFinishQuest = goToGroundFloor.copy();
		goFinishQuest.setText("Talk to Kojo for your reward.");
		goFinishQuest.addStep(null, kojoReward);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Collections.singletonList(bucketOfWater);
	}

    @Override
    public List<ItemRequirement> getItemRecommended()
    {
        return Arrays.asList(staminaPotions, ardougneCloak);
    }

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("Able to survive a hit or 2 from an Ogre (level 53)");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Collections.singletonList(new ItemReward("500 Coins", ItemID.COINS_995, 500));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails gettingStarted = new PanelDetails("Getting Started", new ArrayList<>(Collections.singletonList(talkToKojo)));
		allSteps.add(gettingStarted);

		PanelDetails redCog = new PanelDetails("Obtaining the red cog", new ArrayList<>(Arrays.asList(goToBasementForRed,
			pickUpRedCog, goToGroundFloorWithRedCog)));
		allSteps.add(redCog);
		PanelDetails blueCog = new PanelDetails("Obtaining the blue cog",
			new ArrayList<>(Arrays.asList(goToBasementForBlue, pushWall, pickUpBlueCog, goToFirstFloorWithBlueCog)),
			noteAboutWater);
		allSteps.add(blueCog);
		PanelDetails blackCog = new PanelDetails("Obtaining the black cog",
			new ArrayList<>(Arrays.asList(goToBasementForBlack, pickupBlackCog, goToBasementWithBlackCog)),
			bucketOfWater);
		allSteps.add(blackCog);
		PanelDetails whiteCog = new PanelDetails("Obtaining the white cog",
			new ArrayList<>(Arrays.asList(goToBasementForWhite, pickUpRatPoison, pullFirstLever,
				ratPoisonFood, westernGate, pickUpWhiteCog, goToSecondFloorWithWhiteCog)));
		allSteps.add(whiteCog);
		PanelDetails finishingOff = new PanelDetails("Finishing off",
			new ArrayList<>(Collections.singletonList(goFinishQuest)));
		allSteps.add(finishingOff);

		return allSteps;
	}
}
