/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.hauntedmine;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.VarplayerCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.HAUNTED_MINE
)
public class HauntedMine extends BasicQuestHelper
{
	ItemRequirement zealotsKey, chisel, glowingFungus, glowingFungusHighlight, crystalMineKey, combatGear;

	ConditionForStep askedAboutKey, hasKey, inLevel1South, valveOpened, valveOpen, hasKeyOrOpenedValve, hasGlowingFungus, hasChisel,
		inLiftRoom, inLevel2North, inLevel3North, inLevel2South, inLevel3South, inCartRoom, inCollectRoom, leverAWrong, leverBWrong,
		leverCWrong, leverDWrong, leverEWrong, leverFWrong, leverGWrong, leverHWrong, fungusInCart, fungusOnOtherSide, inLevel1North,
		inFloodedRoom, daythNearby, inDaythRoom, inCrystalRoom, inCrystalEntrance, killedDayth, hasCrystalMineKey, inCrystalOrCrystalEntranceRoom,
		inDarkDaythRoom, inDarkCrystalRoom;

	QuestStep talkToZealot, pickpocketZealot, enterMine, goDownFromLevel1South, goDownFromLevel2North, goDownFromLevel3NorthEast,
		useKeyOnValve, openValve, goDownLift, pickUpChisel, goUpFromLiftRoom, goUpFromCollectRoom, goDownToCollectFungus, collectFungus,
		goDownFromLevel2South, goDownToFungusRoom, pickFungus, pullLeverA, pullLeverB, pullLeverC, pullLeverD, pullLeverE, pullLeverF,
		pullLeverG, pullLeverH, readPanel, putFungusInCart, goUpFromFungusRoom, goUpFromLevel3South, goUpFromLevel2South, leaveLevel1South,
		enterMineNorth, goDownLevel1North, goDownLevel2North, goDownToDayth, goDownToCrystals, tryToPickUpKey, killDayth, pickUpKey, goUpFromDayth,
		cutCrystal, leaveCrystalRoom, goBackUpLift, leaveDarkCrystalRoom, leaveDarkDaythRoom, solvePuzzle;

	Zone entryRoom1, level1South, liftRoom, level2South, level2North, level2North2, level3North1, level3North2, level3North3, level3North4,
		level3South1, level3South2, level3South3, cartRoom, collectRoom, level1North, floodedRoom, daythRoom1, daythRoom2, crystalRoom1,
		crystalRoom2, crystalRoom3, crystalEntrance, crystalEntranceDark, daythRoomDark;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToZealot);

		ConditionalStep solveMineCarts = new ConditionalStep(this, readPanel);
		solveMineCarts.addStep(leverAWrong, pullLeverA);
		solveMineCarts.addStep(leverBWrong, pullLeverB);
		solveMineCarts.addStep(leverCWrong, pullLeverC);
		solveMineCarts.addStep(leverDWrong, pullLeverD);
		solveMineCarts.addStep(leverEWrong, pullLeverE);
		solveMineCarts.addStep(leverFWrong, pullLeverF);
		solveMineCarts.addStep(leverGWrong, pullLeverG);
		solveMineCarts.addStep(leverHWrong, pullLeverH);

		ConditionalStep exploreMine = new ConditionalStep(this, talkToZealot);
		exploreMine.addStep(inCrystalRoom, cutCrystal);
		exploreMine.addStep(inDarkCrystalRoom, leaveDarkCrystalRoom);
		exploreMine.addStep(inDarkDaythRoom, leaveDarkDaythRoom);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inCrystalEntrance, hasCrystalMineKey, hasChisel), cutCrystal);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inFloodedRoom, hasCrystalMineKey, hasChisel), goDownToCrystals);
		exploreMine.addStep(new Conditions(inFloodedRoom, hasCrystalMineKey), goBackUpLift);
		exploreMine.addStep(new Conditions(inDaythRoom, hasCrystalMineKey), goUpFromDayth);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inDaythRoom, killedDayth), pickUpKey);
		exploreMine.addStep(new Conditions(daythNearby), killDayth);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inDaythRoom), tryToPickUpKey);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inFloodedRoom), goDownToDayth);
		exploreMine.addStep(new Conditions(inCrystalEntrance), leaveCrystalRoom);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLiftRoom, valveOpen, hasChisel), goDownLift);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLiftRoom, valveOpened, hasChisel), openValve);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLiftRoom, hasKey, hasChisel), useKeyOnValve);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLiftRoom, hasKeyOrOpenedValve), pickUpChisel);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inCollectRoom, hasKeyOrOpenedValve), goUpFromCollectRoom);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLevel3North, hasKeyOrOpenedValve), goDownFromLevel3NorthEast);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLevel2North, hasKeyOrOpenedValve), goDownLevel2North);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inLevel1North, hasKeyOrOpenedValve), goDownLevel1North);

		exploreMine.addStep(new Conditions(fungusOnOtherSide, inCollectRoom, hasKeyOrOpenedValve), collectFungus);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLevel3North, hasKeyOrOpenedValve), goDownToCollectFungus);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLiftRoom, hasKeyOrOpenedValve), goUpFromLiftRoom); // Wrong way catch
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLevel2North, hasKeyOrOpenedValve), goDownLevel2North);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLevel1North, hasKeyOrOpenedValve), goDownLevel1North);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLevel1South, hasKeyOrOpenedValve), leaveLevel1South);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLevel2South, hasKeyOrOpenedValve), goUpFromLevel2South);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inLevel3South, hasKeyOrOpenedValve), goUpFromLevel3South);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, inCartRoom, hasKeyOrOpenedValve), goUpFromFungusRoom);
		exploreMine.addStep(new Conditions(fungusOnOtherSide, hasKeyOrOpenedValve), enterMineNorth);

		exploreMine.addStep(new Conditions(fungusInCart, inCartRoom), solveMineCarts);
		exploreMine.addStep(new Conditions(hasGlowingFungus, inCartRoom), putFungusInCart);
		exploreMine.addStep(inCartRoom, pickFungus);

		exploreMine.addStep(inLevel3South, goDownToFungusRoom);
		exploreMine.addStep(inLevel2South, goDownFromLevel2South);
		exploreMine.addStep(inLevel1South, goDownFromLevel1South);
		exploreMine.addStep(hasKeyOrOpenedValve, enterMine);
		exploreMine.addStep(askedAboutKey, pickpocketZealot);

		steps.put(1, exploreMine);
		steps.put(2, exploreMine);
		steps.put(3, exploreMine);
		steps.put(4, exploreMine);
		steps.put(5, exploreMine);
		steps.put(6, exploreMine);
		steps.put(7, exploreMine);
		steps.put(8, exploreMine);
		steps.put(9, exploreMine);
		steps.put(10, exploreMine);
		return steps;
	}

	public void setupItemRequirements()
	{
		zealotsKey = new ItemRequirement("Zealot's key", ItemID.ZEALOTS_KEY);
		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);
		glowingFungus = new ItemRequirement("Glowing fungus", ItemID.GLOWING_FUNGUS);
		glowingFungusHighlight = new ItemRequirement("Glowing fungus", ItemID.GLOWING_FUNGUS);
		glowingFungusHighlight.setHighlightInInventory(true);

		crystalMineKey = new ItemRequirement("Crystal-mine key", ItemID.CRYSTALMINE_KEY);

		combatGear = new ItemRequirement("Combat gear and food", -1, -1);
	}

	public void loadZones()
	{
		entryRoom1 = new Zone(new WorldPoint(2647, 9803, 0), new WorldPoint(2680, 9814, 0));

		level1North = new Zone(new WorldPoint(3404, 9628, 0), new WorldPoint(3439, 9662, 0));
		level1South = new Zone(new WorldPoint(3409, 9616, 0), new WorldPoint(3431, 9627, 0));

		level2South = new Zone(new WorldPoint(2780, 4558, 0), new WorldPoint(2815, 4576, 0));
		level2North = new Zone(new WorldPoint(2765, 4577, 0), new WorldPoint(2814, 4605, 0));
		level2North2 = new Zone(new WorldPoint(2770, 4575, 0), new WorldPoint(2775, 4576, 0));

		level3North1 = new Zone(new WorldPoint(2709, 4518, 0), new WorldPoint(2744, 4543, 0));
		level3North2 = new Zone(new WorldPoint(2693, 4496, 0), new WorldPoint(2724, 4517, 0));
		level3North3 = new Zone(new WorldPoint(2694, 4493, 0), new WorldPoint(2698, 4495, 0));
		level3North4 = new Zone(new WorldPoint(2721, 4492, 0), new WorldPoint(2724, 4496, 0));

		level3South1 = new Zone(new WorldPoint(2725, 4485, 0), new WorldPoint(2741, 4517, 0));
		level3South2 = new Zone(new WorldPoint(2718, 4484, 0), new WorldPoint(2729, 4490, 0));
		level3South3 = new Zone(new WorldPoint(2710, 4491, 0), new WorldPoint(2718, 4495, 0));

		liftRoom = new Zone(new WorldPoint(2794, 4489, 0), new WorldPoint(2812, 4532, 0));
		cartRoom = new Zone(new WorldPoint(2757, 4483, 0), new WorldPoint(2794, 4545, 0));

		collectRoom = new Zone(new WorldPoint(2772, 4535, 0), new WorldPoint(2776, 4542, 0));

		floodedRoom = new Zone(new WorldPoint(2688, 4432, 0), new WorldPoint(2753, 4472, 0));

		daythRoom1 = new Zone(new WorldPoint(2779, 4441, 0), new WorldPoint(2815, 4474, 0));
		daythRoom2 = new Zone(new WorldPoint(2774, 4457, 0), new WorldPoint(2779, 4465, 0));
		daythRoomDark = new Zone(new WorldPoint(2716, 4559, 0), new WorldPoint(2734, 4569, 0));

		crystalRoom1 = new Zone(new WorldPoint(2762, 4418, 0), new WorldPoint(2780, 4449, 0));
		crystalRoom2 = new Zone(new WorldPoint(2762, 4421, 0), new WorldPoint(2810, 4439, 0));
		crystalRoom3 = new Zone(new WorldPoint(2797, 4440, 0), new WorldPoint(2808, 4450, 0));

		crystalEntrance = new Zone(new WorldPoint(2758, 4450, 0), new WorldPoint(2776, 4456, 0));
		crystalEntranceDark = new Zone(new WorldPoint(2708, 4588, 0), new WorldPoint(2736, 5499, 0));
	}

	public void setupConditions()
	{
		askedAboutKey = new VarbitCondition(2397, 1);
		hasKey = new ItemRequirementCondition(zealotsKey);
		inLevel1North = new ZoneCondition(level1North);
		inLevel1South = new ZoneCondition(level1South);
		inLevel2South = new ZoneCondition(level2South);
		inLevel2North = new ZoneCondition(level2North, level2North2);

		inLevel3South = new ZoneCondition(level3South1, level3South2, level3South3);
		inLevel3North = new ZoneCondition(level3North1, level3North2, level3North3, level3North4);
		inLiftRoom = new ZoneCondition(liftRoom);
		inCartRoom = new ZoneCondition(cartRoom);
		inCollectRoom = new ZoneCondition(collectRoom);
		inFloodedRoom = new ZoneCondition(floodedRoom);
		inDaythRoom = new ZoneCondition(daythRoom1, daythRoom2);
		inCrystalRoom = new ZoneCondition(crystalRoom1, crystalRoom2, crystalRoom3);
		inCrystalEntrance = new ZoneCondition(crystalEntrance);
		inCrystalOrCrystalEntranceRoom = new ZoneCondition(crystalRoom1, crystalRoom2, crystalRoom3, crystalEntrance);

		valveOpened = new VarbitCondition(2393, 1);
		valveOpen = new VarbitCondition(2394, 1);
		hasGlowingFungus = new ItemRequirementCondition(glowingFungus);
		hasChisel = new ItemRequirementCondition(chisel);

		hasKeyOrOpenedValve = new Conditions(LogicType.OR, hasKey, valveOpened);

		leverAWrong = new VarbitCondition(2385, 0);
		leverBWrong = new VarbitCondition(2386, 0);
		leverCWrong = new VarbitCondition(2387, 1);
		leverDWrong = new VarbitCondition(2388, 1);
		leverEWrong = new VarbitCondition(2389, 0);
		leverFWrong = new VarbitCondition(2390, 0);
		leverGWrong = new VarbitCondition(2391, 1);
		leverHWrong = new VarbitCondition(2392, 1);

		fungusInCart = new VarbitCondition(2395, 1);
		fungusOnOtherSide = new VarbitCondition(2396, 1);

		daythNearby = new NpcCondition(NpcID.TREUS_DAYTH);

		killedDayth = new VarplayerCondition(382, 9, Operation.GREATER_EQUAL);

		hasCrystalMineKey = new ItemRequirementCondition(crystalMineKey);

		inDarkCrystalRoom = new ZoneCondition(crystalEntranceDark);
		inDarkDaythRoom = new ZoneCondition(daythRoomDark);
	}

	public void setupSteps()
	{
		talkToZealot = new NpcStep(this, NpcID.ZEALOT, new WorldPoint(3443, 3258, 0), "Talk to the Zealot outside the Abandoned Mine in south west Morytania.");
		talkToZealot.addDialogStep("Is there any other way into the mines?");
		pickpocketZealot =  new NpcStep(this, NpcID.ZEALOT, new WorldPoint(3443, 3258, 0), "Pickpocket the Zealot outside the Abandoned Mine in south west Morytania.");

		enterMine = new ObjectStep(this, ObjectID.CART_TUNNEL_4915, new WorldPoint(3429, 3225, 0), "Enter the south cart tunnel around the back of the mine.");

		goDownFromLevel1South = new ObjectStep(this, ObjectID.LADDER_4965, new WorldPoint(3422, 9625, 0), "Climb down the ladder to the east.");

		goDownFromLevel2South = new ObjectStep(this, ObjectID.LADDER_4969, new WorldPoint(2798, 4567, 0), "Climb down another ladder to the east.");

		goDownFromLevel2North = new ObjectStep(this, ObjectID.LADDER_4969, new WorldPoint(2797, 4599, 0), "Climb down the ladder to the north east.");

		goDownFromLevel3NorthEast = new ObjectStep(this, ObjectID.LADDER_4967, new WorldPoint(2732, 4529, 0), "Climb down the ladder in the room in the north east.");

		goDownToFungusRoom = new ObjectStep(this, ObjectID.LADDER_4967, new WorldPoint(2725, 4486, 0), "Go down the ladder to the south, making sure to avoid the moving mine cart.");

		readPanel = new ObjectStep(this, ObjectID.POINTS_SETTINGS, new WorldPoint(2770, 4522, 0), "Check the Points Settings panel in the centre of the room. Click the 'Start' button in the interface.");

		pullLeverA = new ObjectStep(this, ObjectID.LEVER_4950, new WorldPoint(2785, 4517, 0), "Pull the marked lever.");
		pullLeverB = new ObjectStep(this, ObjectID.LEVER_4951, new WorldPoint(2784, 4517, 0), "Pull the marked lever.");
		pullLeverC = new ObjectStep(this, ObjectID.LEVER_4952, new WorldPoint(2786, 4517, 0), "Pull the marked lever.");
		pullLeverD = new ObjectStep(this, ObjectID.LEVER_4953, new WorldPoint(2786, 4515, 0), "Pull the marked lever.");
		pullLeverE = new ObjectStep(this, ObjectID.LEVER_4954, new WorldPoint(2785, 4515, 0), "Pull the marked lever.");

		pullLeverF = new ObjectStep(this, ObjectID.LEVER_4955, new WorldPoint(2768, 4533, 0), "Pull the marked lever.");
		pullLeverG = new ObjectStep(this, ObjectID.LEVER_4956, new WorldPoint(2769, 4533, 0), "Pull the marked lever.");
		pullLeverH = new ObjectStep(this, ObjectID.LEVER_4957, new WorldPoint(2770, 4533, 0), "Pull the marked lever.");

		solvePuzzle = new DetailedQuestStep(this, "Pull levers until the tracks are lined up to take the cart to the north east corner.");
		solvePuzzle.addSubSteps(pullLeverA, pullLeverB, pullLeverC, pullLeverD, pullLeverE, pullLeverF, pullLeverG, pullLeverH);

		useKeyOnValve = new ObjectStep(this, ObjectID.WATER_VALVE, new WorldPoint(2808, 4496, 0),
			"Use the Zealot's key on the water valve. Make sure you have some energy as you'll need to race to the lift afterwards.");

		openValve = new ObjectStep(this, ObjectID.WATER_VALVE, new WorldPoint(2808, 4496, 0),
			"Turn the valve. Make sure you have some energy as you'll need to race to the lift afterwards.");

		useKeyOnValve.addSubSteps(openValve);

		goDownLift = new ObjectStep(this, ObjectID.LIFT_4938, new WorldPoint(2807, 4492, 0), "Race to the lift before the ghost turns off the valve.");

		goDownToCollectFungus = new ObjectStep(this, ObjectID.LADDER_4967, new WorldPoint(2710, 4540, 0), "Go down the north west ladder to collect the glowing fungus.");
		collectFungus = new ObjectStep(this, ObjectID.MINE_CART_4974, new WorldPoint(2774, 4537, 0), "Search the mine cart for the glowing fungus.");
		collectFungus.addDialogStep("Take it.");
		goUpFromCollectRoom = new ObjectStep(this, ObjectID.LADDER_4968, new WorldPoint(2774, 4540, 0), "Take the fungus back upstairs.");

		pickUpChisel = new DetailedQuestStep(this, new WorldPoint(2800, 4500, 0), "Pick up the chisel nearby.", chisel);

		pickFungus = new ObjectStep(this, ObjectID.GLOWING_FUNGUS_4933, new WorldPoint(2793, 4493, 0), "Pick a glowing fungus.");

		goUpFromLiftRoom = new ObjectStep(this, ObjectID.LADDER_4968, new WorldPoint(2796, 4529, 0), "Go back up the ladder.");

		putFungusInCart = new ObjectStep(this, ObjectID.MINE_CART_4974, new WorldPoint(2778, 4506, 0),
			"Put the glowing fungus into the mine cart north west of the ladder.", glowingFungusHighlight);
		putFungusInCart.addIcon(ItemID.GLOWING_FUNGUS);

		goUpFromFungusRoom = new ObjectStep(this, ObjectID.LADDER_4968, new WorldPoint(2789, 4486, 0), "Climb back up to the surface.");
		goUpFromLevel3South = new ObjectStep(this, ObjectID.LADDER_4970, new WorldPoint(2734, 4503, 0), "Climb back up to the surface.");

		goUpFromLevel2South = new ObjectStep(this, ObjectID.LADDER_4966, new WorldPoint(2782, 4569, 0), "Climb back up to the surface.");

		leaveLevel1South = new ObjectStep(this, ObjectID.CART_TUNNEL_15830, new WorldPoint(3408, 9623, 0), "Leave through the cart tunnel.");

		goUpFromFungusRoom.addSubSteps(goUpFromLevel3South, goUpFromLevel2South, leaveLevel1South);

		enterMineNorth = new ObjectStep(this, ObjectID.CART_TUNNEL_4914, new WorldPoint(3430, 3233, 0),
			"Make sure you're prepared to fight Treus Dayth (level 95), then enter the north area of the Haunted Mine.");

		goDownLevel1North = new ObjectStep(this, ObjectID.LADDER_4965, new WorldPoint(3413, 9633, 0), "Climb down the west ladder.");
		goDownLevel2North = new ObjectStep(this, ObjectID.LADDER_4969, new WorldPoint(2797, 4599, 0), "Climb down the north east ladder.");

		goDownToDayth = new ObjectStep(this, ObjectID.STAIRS_4971, new WorldPoint(2748, 4437, 0), "Go down the east stairs.");

		goDownToCrystals = new ObjectStep(this, ObjectID.STAIRS_4971, new WorldPoint(2694, 4437, 0), "Go down the west stairs.");

		tryToPickUpKey = new NpcStep(this, NpcID.INNOCENTLOOKING_KEY, new WorldPoint(2788, 4455, 0), "Attempt to pick up the innocent-looking key. Treus Dayth (level 95) will spawn. Kill him.");

		killDayth = new NpcStep(this, NpcID.TREUS_DAYTH, new WorldPoint(2788, 4450, 0), "Kill Treus Dayth.");

		tryToPickUpKey.addSubSteps(killDayth);

		pickUpKey = new NpcStep(this, NpcID.INNOCENTLOOKING_KEY, new WorldPoint(2788, 4455, 0), "Pick up the innocent-looking key.");

		goUpFromDayth = new ObjectStep(this, ObjectID.STAIRS_4973, new WorldPoint(2813, 4454, 0), "Go back up to the flooded area.");

		cutCrystal = new ObjectStep(this, ObjectID.CRYSTAL_OUTCROP, new WorldPoint(2787, 4428, 0),
			"Cut from a crystal outcrop with a chisel in the south room to finish the quest.", chisel);

		leaveCrystalRoom = new ObjectStep(this, ObjectID.STAIRS_4973, new WorldPoint(2756, 4454, 0), "Go back up to the flooded area.");

		goBackUpLift = new ObjectStep(this, ObjectID.LIFT_4942, new WorldPoint(2726, 4456, 0), "Go back up the lift to get a chisel.");

		leaveDarkCrystalRoom = new ObjectStep(this, ObjectID.STAIRS_4972, new WorldPoint(2710, 4593, 0), "You need a glowing fungus. Go back up to the flooded area.");
		leaveDarkDaythRoom = new ObjectStep(this, ObjectID.STAIRS_4972, new WorldPoint(2732, 4563, 0), "You need a glowing fungus. Go back up to the flooded area.");
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(combatGear);
		return reqs;
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Treus Dayth (level 95)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Arrays.asList(talkToZealot, pickpocketZealot)), combatGear));
		allSteps.add(new PanelDetails("Getting a light source",
			new ArrayList<>(Arrays.asList(enterMine, goDownFromLevel1South, goDownFromLevel2South, goDownToFungusRoom, pickFungus, putFungusInCart, solvePuzzle, readPanel, goUpFromFungusRoom))));
		allSteps.add(new PanelDetails("Getting the salve crystals",
			new ArrayList<>(Arrays.asList(enterMineNorth, goDownLevel1North, goDownLevel2North, goDownToCollectFungus,
				collectFungus, goUpFromCollectRoom, goDownFromLevel3NorthEast, pickUpChisel, useKeyOnValve, goDownLift, goDownToDayth, tryToPickUpKey, pickUpKey, goUpFromDayth, goDownToCrystals, cutCrystal))));
		return allSteps;
	}
}
