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
package com.questhelper.helpers.quests.clocktower;

import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestVarPlayer;
import com.questhelper.requirements.ChatMessageRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.nor;
import static com.questhelper.requirements.util.LogicHelper.or;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
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
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

public class ClockTower extends BasicQuestHelper
{
	// Required items
	ItemRequirement bucketOfWater;

	// Recommended items
	ItemRequirement staminaPotions;
	ItemRequirement ardougneCloak;

	// Zones
	Zone basement;
	Zone firstFloor;
	Zone secondFloor;
	Zone groundFloor;
	Zone secretPath;
	Zone secretPath2;
	Zone secretPath3;
	Zone secretPath4;
	Zone secretPath5;
	Zone secretPath6;
	Zone cell;

	// Miscellaneous requirements
	ItemRequirement noteAboutWater;
	ItemRequirement redCog;
	ItemRequirement blueCog;
	ItemRequirement blackCog;
	ItemRequirement whiteCog;
	ItemRequirement ratPoison;

	ZoneRequirement inBasement;
	ZoneRequirement inGroundFloor;
	ZoneRequirement inFirstFloor;
	ZoneRequirement inSecondFloor;
	ZoneRequirement inSecretPath;
	ZoneRequirement inCell;
	Requirement startedQuestDuringSession;
	Requirement synced;
	ObjectCondition firstLeverDown;
	ObjectCondition pulledFirstLeverUp;
	ObjectCondition secondLeverUp;
	ChatMessageRequirement poisonedRats;
	Requirement placedRedCog;
	Requirement placedBlueCog;
	Requirement placedWhiteCog;
	Requirement placedBlackCog;

	// Steps
	NpcStep talkToKojo;
	QuestStep syncStep;

	ObjectStep enterBasement;
	DetailedQuestStep pickUpRedCog;
	ObjectStep climbToGroundFloorFromBasement;
	ObjectStep redCogOnRedSpindle;

	ObjectStep goToLadderCedric;
	ObjectStep pushWall;
	ObjectStep climbCellLadder;
	DetailedQuestStep pickUpBlueCog;
	ObjectStep climbToFirstFloor;
	ObjectStep blueCogOnBlueSpindle;

	ObjectStep climbFromFirstFloorToGround;
	DetailedQuestStep pickupBlackCog;
	ObjectStep blackCogOnBlackSpindle;

	ObjectStep northWesternDoor;
	DetailedQuestStep pickUpRatPoison;
	ObjectStep pullFirstLever;
	ObjectStep ratPoisonFood;
	ObjectStep westernGate;
	DetailedQuestStep pickUpWhiteCog;
	ObjectStep climbWhiteLadder;
	ObjectStep climbToSecondFloor;
	ObjectStep whiteCogOnWhiteSpindle;
	ObjectStep climbFromSecondFloorToFirst;

	NpcStep finishQuest;

	ConditionalStep getRedCog;
	ConditionalStep getBlueCog;
	ConditionalStep getBlackCog;
	ConditionalStep getWhiteCog;

	ConditionalStep goToBasementForRed;
	ConditionalStep goToBasementForBlue;
	ConditionalStep goToBasementForBlack;
	ConditionalStep goToBasementForWhite;
	ConditionalStep goToFirstFloorWithBlueCog;
	ConditionalStep goToGroundFloorWithRedCog;
	ConditionalStep goToBasementWithBlackCog;
	ConditionalStep goToSecondFloorWithWhiteCog;
	ConditionalStep goFinishQuest;

	@Override
	protected void setupZones()
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

	@Override
	protected void setupRequirements()
	{
		inSecondFloor = new ZoneRequirement(secondFloor);
		inFirstFloor = new ZoneRequirement(firstFloor);
		inGroundFloor = new ZoneRequirement(groundFloor);
		inBasement = new ZoneRequirement(basement);
		inCell = new ZoneRequirement(cell);
		inSecretPath = new ZoneRequirement(secretPath, secretPath2, secretPath3, secretPath4, secretPath5, secretPath6);

		startedQuestDuringSession = new Conditions(true, new VarplayerRequirement(QuestVarPlayer.QUEST_CLOCK_TOWER.getId(), 0));

		synced = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(InterfaceID.QuestjournalOverview.TITLE, "Clock Tower"),
			startedQuestDuringSession
		);

		firstLeverDown = new ObjectCondition(ObjectID.CTLEVERA, new WorldPoint(2591, 9661, 0));
		pulledFirstLeverUp = new ObjectCondition(ObjectID.CTLEVERA2, new WorldPoint(2591, 9661, 0));
		secondLeverUp = new ObjectCondition(ObjectID.CTLEVERB, new WorldPoint(2593, 9661, 0));
		poisonedRats = new ChatMessageRequirement("The rats swarm towards the poisoned food...");

		placedRedCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I have successfully placed the Red Cog on its spindle"),
			new ChatMessageRequirement(inGroundFloor, "The cog fits perfectly.")
		);
		placedBlueCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I have successfully placed the Blue Cog on its spindle"),
			new ChatMessageRequirement(inFirstFloor, "The cog fits perfectly.")
		);
		placedBlackCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I have successfully placed the Black Cog on its spindle"),
			new ChatMessageRequirement(inBasement, "The cog fits perfectly.")
		);
		placedWhiteCog = new Conditions(true, LogicType.OR,
			new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I have successfully placed the White Cog on its spindle"),
			new ChatMessageRequirement(inSecondFloor, "The cog fits perfectly.")
		);

		bucketOfWater = new ItemRequirement("Bucket of Water or a pair of ice gloves or smiths gloves(i)", ItemID.BUCKET_WATER);
		bucketOfWater.addAlternates(ItemID.ICE_GLOVES, ItemID.SMITHING_UNIFORM_GLOVES_ICE);
		bucketOfWater.setTooltip("There is a bucket spawn next to the well east of the Clock Tower. You can fill it on the well");
		noteAboutWater = new ItemRequirement("There's a bucket and a well and just next to brother cedric for the black cog", -1, -1);
		staminaPotions = new ItemRequirement("Stamina Potions", ItemCollections.STAMINA_POTIONS);
		ardougneCloak = new ItemRequirement("Ardougne Cloak to teleport to monastery", ItemID.ARDY_CAPE_EASY);
		ardougneCloak.addAlternates(ItemID.ARDY_CAPE_MEDIUM, ItemID.ARDY_CAPE_HARD, ItemID.ARDY_CAPE_ELITE);
		redCog = new ItemRequirement("Red Cog", ItemID.REDCOG);
		blueCog = new ItemRequirement("Blue Cog", ItemID.BLUECOG);
		whiteCog = new ItemRequirement("White Cog", ItemID.WHITECOG);
		blackCog = new ItemRequirement("Black Cog", ItemID.BLACKCOG);
		ratPoison = new ItemRequirement("Rat Poison", ItemID.RAT_POISON);
	}

	public void setupSteps()
	{
		// TODO: Need to determine to what degree PuzzleWrapperStep should be used in this quest
		talkToKojo = new NpcStep(this, NpcID.BROTHER_KOJO, new WorldPoint(2570, 3245, 0), "Talk to Brother Kojo at the Clock Tower.");
		talkToKojo.addDialogStep("OK old monk, what can I do?");

		syncStep = new DetailedQuestStep(this, "Open your Quest Journal to sync your current state.");

		pickUpRedCog = new DetailedQuestStep(this, new WorldPoint(2583, 9613, 0),
			"Go through the south east door and pick up the red cog.", redCog);
		redCogOnRedSpindle = new ObjectStep(this, ObjectID.BROKECLOCKPOLE_RED, new WorldPoint(2568, 3243, 0),
			"Use the red cog on the red spindle.", redCog.highlighted());
		redCogOnRedSpindle.addIcon(ItemID.REDCOG);

		goToLadderCedric = new ObjectStep(this, ObjectID.LADDER_CELLAR, new WorldPoint(2621, 3261, 0), "");
		pushWall = new ObjectStep(this, ObjectID.SECRETDOOR2, new WorldPoint(2575, 9631, 0), "Follow the tunnel and push the wall at the end.");
		pickUpBlueCog = new DetailedQuestStep(this, new WorldPoint(2574, 9633, 0), "Pick up the blue cog.", blueCog);
		climbCellLadder = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(2572, 9631, 0), "");
		blueCogOnBlueSpindle = new ObjectStep(this, ObjectID.BROKECLOCKPOLE_BLUE, new WorldPoint(2569, 3240, 1),
			"Use the blue cog on the blue spindle.", blueCog.highlighted());
		blueCogOnBlueSpindle.addIcon(ItemID.BLUECOG);

		pickupBlackCog = new DetailedQuestStep(this, new WorldPoint(2613, 9639, 0), "Enter the north east door, and pick up the black cog with a bucket of water, alternatively you can equip ice gloves or smith gloves(i).", bucketOfWater, blackCog);
		blackCogOnBlackSpindle = new ObjectStep(this, ObjectID.BROKECLOCKPOLE_BLACK, new WorldPoint(2570, 9642, 0),
			"", blackCog.highlighted());
		blackCogOnBlackSpindle.addIcon(ItemID.BLACKCOG);

		northWesternDoor = new ObjectStep(this, ObjectID.POORDOOR, new WorldPoint(2575, 9651, 0), "Go through the north-western door.");
		pickUpRatPoison = new DetailedQuestStep(this, new WorldPoint(2564, 9662, 0), "Pick up the rat poison in the north west of the dungeon.", ratPoison);
		pullFirstLever = new ObjectStep(this, ObjectID.CTLEVERA, new WorldPoint(2591, 9661, 0),
			"Pull the marked lever up.");
		ratPoisonFood = new ObjectStep(this, ObjectID.CTFOODTROUGH, new WorldPoint(2587, 9654, 0),
			"Use the rat poison on the food trough. Wait till the rats have died.", ratPoison.highlighted());
		ratPoisonFood.addIcon(ItemID.RAT_POISON);
		westernGate = new ObjectStep(this, ObjectID.CTRATGATEC, new WorldPoint(2579, 9656, 0), "Go through the gate.");
		pickUpWhiteCog = new DetailedQuestStep(this, new WorldPoint(2577, 9655, 0), "Pick up the white cog.", whiteCog);
		climbWhiteLadder = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(2575, 9655, 0), "Head up the ladder.");
		whiteCogOnWhiteSpindle = new ObjectStep(this, ObjectID.BROKECLOCKPOLE_WHITE, new WorldPoint(2567, 3241, 2),
			"", whiteCog.highlighted());
		whiteCogOnWhiteSpindle.addIcon(ItemID.WHITECOG);
		finishQuest = new NpcStep(this, NpcID.BROTHER_KOJO, new WorldPoint(2570, 3245, 0), "Talk to Brother Kojo for your reward.");

		enterBasement = new ObjectStep(this, ObjectID.LADDER_CELLAR, new WorldPoint(2566, 3242, 0), "");
		climbFromFirstFloorToGround = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(2573, 3241, 1), "");
		climbFromFirstFloorToGround.addDialogStep("Climb down the stairs.");
		climbFromSecondFloorToFirst = new ObjectStep(this, ObjectID.SPIRALSTAIRSTOP, new WorldPoint(2572, 3241, 2), "");

		climbToGroundFloorFromBasement = new ObjectStep(this, ObjectID.LADDER_FROM_CELLAR, new WorldPoint(2566, 9642, 0), "");
		climbToFirstFloor = new ObjectStep(this, ObjectID.SPIRALSTAIRS, new WorldPoint(2573, 3241, 0), "");
		climbToSecondFloor = new ObjectStep(this, ObjectID.SPIRALSTAIRSMIDDLE, new WorldPoint(2573, 3241, 1), "");
		climbToSecondFloor.addDialogStep("Climb up the stairs.");

		var goToBasementSteps = new ConditionalStep(this, enterBasement);
		goToBasementSteps.addStep(inSecondFloor, climbFromSecondFloorToFirst);
		goToBasementSteps.addStep(inFirstFloor, climbFromFirstFloorToGround);
		goToBasementSteps.addStep(or(inSecretPath, inCell), climbCellLadder);

		var goToCellSteps = new ConditionalStep(this, goToLadderCedric);
		goToCellSteps.addStep(inBasement, climbToGroundFloorFromBasement);
		goToCellSteps.addStep(inSecondFloor, climbFromSecondFloorToFirst);
		goToCellSteps.addStep(inFirstFloor, climbFromFirstFloorToGround);

		var goToGroundFloor = new ConditionalStep(this, new DetailedQuestStep(this, ""));
		goToGroundFloor.addStep(or(inSecretPath, inCell), climbCellLadder);
		goToGroundFloor.addStep(inBasement, climbToGroundFloorFromBasement);
		goToGroundFloor.addStep(inSecondFloor, climbFromSecondFloorToFirst);
		goToGroundFloor.addStep(inFirstFloor, climbFromFirstFloorToGround);

		var goToFirstFloor = new ConditionalStep(this, climbToFirstFloor);
		goToFirstFloor.addStep(or(inSecretPath, inCell), climbCellLadder);
		goToFirstFloor.addStep(inBasement, climbToGroundFloorFromBasement);
		goToFirstFloor.addStep(inSecondFloor, climbFromSecondFloorToFirst);

		var goToSecondFloor = new ConditionalStep(this, climbToFirstFloor);
		goToSecondFloor.addStep(or(inSecretPath, inCell), climbCellLadder);
		goToSecondFloor.addStep(inBasement, climbToGroundFloorFromBasement);
		goToSecondFloor.addStep(inFirstFloor, climbToSecondFloor);

		goToBasementForRed = new ConditionalStep(this, goToBasementSteps, "Enter the Clock Tower basement for the red cog.");
		goToBasementForBlue = new ConditionalStep(this, goToCellSteps, "Climb down the ladder to the east of the Clock Tower, next to Brother Cedric.");
		goToBasementForWhite = new ConditionalStep(this, goToBasementSteps, "Enter the Clock Tower basement for the white cog.");
		goToBasementForBlack = new ConditionalStep(this, goToBasementSteps, "Enter the Clock Tower basement for the black cog.");

		goToFirstFloorWithBlueCog = new ConditionalStep(this, goToFirstFloor, "Place the blue cog on the peg on the first floor.");
		goToFirstFloorWithBlueCog.addStep(inFirstFloor, blueCogOnBlueSpindle);

		goToGroundFloorWithRedCog = goToGroundFloor.copy();
		goToGroundFloorWithRedCog.setText("Place the red cog on the peg on the ground floor.");
		goToGroundFloorWithRedCog.addStep(null, redCogOnRedSpindle);

		goToBasementWithBlackCog = new ConditionalStep(this, goToBasementSteps, "Place the black cog on the peg in the basement.");
		goToBasementWithBlackCog.addStep(and(inBasement, nor(inSecretPath, inCell)),
			blackCogOnBlackSpindle);

		goToSecondFloorWithWhiteCog = new ConditionalStep(this, goToSecondFloor, "Place the white cog on the peg on the second floor.");
		goToSecondFloorWithWhiteCog.addStep(inSecondFloor, whiteCogOnWhiteSpindle);
		goToSecondFloorWithWhiteCog.addSubSteps(climbWhiteLadder);

		goFinishQuest = goToGroundFloor.copy();
		goFinishQuest.setText("Talk to Kojo for your reward.");
		goFinishQuest.addStep(null, finishQuest);
	}

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupSteps();

		var steps = new HashMap<Integer, QuestStep>();

		var goStart = new ConditionalStep(this, talkToKojo);
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
		getWhiteCog.addStep(and(whiteCog, inBasement), climbWhiteLadder);
		getWhiteCog.addStep(and(whiteCog), goToSecondFloorWithWhiteCog);
		getWhiteCog.addStep(and(inBasement, pulledFirstLeverUp, poisonedRats), pickUpWhiteCog);
		getWhiteCog.addStep(and(inBasement, ratPoison, pulledFirstLeverUp), ratPoisonFood);
		getWhiteCog.addStep(and(inBasement, ratPoison), pullFirstLever);
		getWhiteCog.addStep(inBasement, pickUpRatPoison);

		var doQuest = new ConditionalStep(this, syncStep);
		doQuest.addStep(and(placedRedCog, placedBlueCog, placedBlackCog, placedWhiteCog, synced), goFinishQuest);
		doQuest.addStep(and(placedRedCog, placedBlueCog, placedBlackCog, synced), getWhiteCog);
		doQuest.addStep(and(placedRedCog, placedBlueCog, synced), getBlackCog);
		doQuest.addStep(and(placedRedCog, synced), getBlueCog);
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
	public List<ItemRequirement> getItemRequirements()
	{
		return List.of(
			bucketOfWater
		);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return List.of(
			staminaPotions,
			ardougneCloak
		);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return List.of(
			"Able to survive a hit or 2 from an Ogre (level 53)"
		);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return List.of(
			new ItemReward("Coins", ItemID.COINS, 500)
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var sections = new ArrayList<PanelDetails>();

		sections.add(new PanelDetails("Getting Started", List.of(
			talkToKojo
		)));

		sections.add(new PanelDetails("Obtaining the red cog", List.of(
			goToBasementForRed,
			pickUpRedCog,
			goToGroundFloorWithRedCog
		)));

		sections.add(new PanelDetails("Obtaining the blue cog", List.of(
			goToBasementForBlue,
			pushWall,
			pickUpBlueCog,
			goToFirstFloorWithBlueCog
		), List.of(
			noteAboutWater
		)));

		sections.add(new PanelDetails("Obtaining the black cog", List.of(
			goToBasementForBlack,
			pickupBlackCog,
			goToBasementWithBlackCog
		), List.of(
			bucketOfWater
		)));

		sections.add(new PanelDetails("Obtaining the white cog", List.of(
			goToBasementForWhite,
			pickUpRatPoison,
			pullFirstLever,
			ratPoisonFood,
			westernGate,
			pickUpWhiteCog,
			goToSecondFloorWithWhiteCog
		)));

		sections.add(new PanelDetails("Finishing off", List.of(
			goFinishQuest
		)));

		return sections;
	}
}
