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
package com.questhelper.quests.templeofikov;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.NpcCondition;
import com.questhelper.steps.conditional.ObjectCondition;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.WeightCondition;
import com.questhelper.steps.conditional.WidgetTextCondition;
import com.questhelper.steps.conditional.ZoneCondition;
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
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;

@QuestDescriptor(
	quest = QuestHelperQuest.TEMPLE_OF_IKOV
)
public class TempleOfIkov extends BasicQuestHelper
{
	ItemRequirement pendantOfLucien, bootsOfLightness, limpwurt20, yewOrBetterBow, knife, lightSource, lever, iceArrows20, iceArrows, shinyKey, armadylPendant, staffOfArmadyl;

	ConditionForStep hasPendantOfLucien, hasBootsOfLightness, belowMinus1Weight, below4Weight, hasYewBow, hasLimpwurts,
		inEntryRoom, inNorthRoom, inBootsRoom, dontHaveBoots, inMainOrNorthRoom, hasLever, leverNearby, pulledLever, inArrowRoom,
		hasEnoughArrows, lesNearby, inLesRoom, inWitchRoom, hasShinyKey, inDemonArea, inArmaRoom, hasStaffOfArmadyl;

	QuestStep talkToLucien, prepare, prepareBelow0, enterDungeonForBoots, enterDungeon, goDownToBoots, getBoots, goUpFromBoots, pickUpLever,
		useLeverOnHole, pullLever, enterArrowRoom, returnToMainRoom, goSearchThievingLever, goPullThievingLever, fightLes, tryToEnterWitchRoom,
		enterDungeonKilledLes, enterLesDoor, giveWineldaLimps, talkToWinelda, enterDungeonGivenLimps, enterFromMcgrubbors, pickUpKey, pushWall,
		makeChoice, killLucien, bringStaffToLucien, returnToLucien;

	ObjectStep collectArrows;

	Zone entryRoom1, entryRoom2, northRoom1, northRoom2, bootsRoom, arrowRoom1, arrowRoom2, arrowRoom3, lesRoom, witchRoom, demonArea1, demonArea2, demonArea3,
		demonArea4, armaRoom1, armaRoom2;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToLucien);

		ConditionalStep getLeverPiece = new ConditionalStep(this, prepare);
		getLeverPiece.addStep(new Conditions(hasEnoughArrows, inMainOrNorthRoom), goSearchThievingLever);
		getLeverPiece.addStep(new Conditions(hasEnoughArrows, inArrowRoom), returnToMainRoom);
		getLeverPiece.addStep(inArrowRoom, collectArrows);
		getLeverPiece.addStep(leverNearby, pullLever);
		getLeverPiece.addStep(new Conditions(inMainOrNorthRoom, hasLever), useLeverOnHole);
		getLeverPiece.addStep(new Conditions(inBootsRoom, hasLever), goUpFromBoots);
		getLeverPiece.addStep(hasLever, enterDungeon);

		getLeverPiece.addStep(new Conditions(inMainOrNorthRoom, belowMinus1Weight), pickUpLever);
		getLeverPiece.addStep(new Conditions(inBootsRoom, new Conditions(LogicType.OR, belowMinus1Weight, hasBootsOfLightness)), goUpFromBoots);
		getLeverPiece.addStep(inBootsRoom, getBoots);
		getLeverPiece.addStep(new Conditions(inMainOrNorthRoom, below4Weight, dontHaveBoots), goDownToBoots);
		getLeverPiece.addStep(belowMinus1Weight, enterDungeon);
		getLeverPiece.addStep(new Conditions(below4Weight, hasBootsOfLightness), prepareBelow0);
		getLeverPiece.addStep(below4Weight, enterDungeonForBoots);

		steps.put(10, getLeverPiece);

		ConditionalStep pullLeverForLes = new ConditionalStep(this, prepare);
		pullLeverForLes.addStep(new Conditions(hasEnoughArrows, inMainOrNorthRoom), goPullThievingLever);
		pullLeverForLes.addStep(new Conditions(hasEnoughArrows, inArrowRoom), returnToMainRoom);
		pullLeverForLes.addStep(inArrowRoom, collectArrows);
		pullLeverForLes.addStep(leverNearby, pullLever);
		pullLeverForLes.addStep(new Conditions(inMainOrNorthRoom, hasLever), useLeverOnHole);
		pullLeverForLes.addStep(new Conditions(inBootsRoom, hasLever), goUpFromBoots);
		pullLeverForLes.addStep(hasLever, enterDungeon);

		pullLeverForLes.addStep(new Conditions(inMainOrNorthRoom, belowMinus1Weight), pickUpLever);
		pullLeverForLes.addStep(new Conditions(inBootsRoom, new Conditions(LogicType.OR, belowMinus1Weight, hasBootsOfLightness)), goUpFromBoots);
		pullLeverForLes.addStep(inBootsRoom, getBoots);
		pullLeverForLes.addStep(new Conditions(inMainOrNorthRoom, below4Weight, dontHaveBoots), goDownToBoots);
		pullLeverForLes.addStep(belowMinus1Weight, enterDungeon);
		pullLeverForLes.addStep(new Conditions(below4Weight, hasBootsOfLightness), prepareBelow0);
		pullLeverForLes.addStep(below4Weight, enterDungeonForBoots);

		steps.put(20, pullLeverForLes);

		ConditionalStep goFightLes = new ConditionalStep(this, prepare);
		goFightLes.addStep(new Conditions(hasEnoughArrows, inLesRoom, lesNearby), fightLes);
		goFightLes.addStep(new Conditions(hasEnoughArrows, inMainOrNorthRoom), tryToEnterWitchRoom);
		goFightLes.addStep(new Conditions(hasEnoughArrows, inArrowRoom), returnToMainRoom);
		goFightLes.addStep(inArrowRoom, collectArrows);
		goFightLes.addStep(leverNearby, pullLever);
		goFightLes.addStep(new Conditions(inMainOrNorthRoom, hasLever), useLeverOnHole);
		goFightLes.addStep(new Conditions(inBootsRoom, hasLever), goUpFromBoots);
		goFightLes.addStep(hasLever, enterDungeon);

		goFightLes.addStep(new Conditions(inMainOrNorthRoom, belowMinus1Weight), pickUpLever);
		goFightLes.addStep(new Conditions(inBootsRoom, new Conditions(LogicType.OR, belowMinus1Weight, hasBootsOfLightness)), goUpFromBoots);
		goFightLes.addStep(inBootsRoom, getBoots);
		goFightLes.addStep(new Conditions(inMainOrNorthRoom, below4Weight, dontHaveBoots), goDownToBoots);
		goFightLes.addStep(belowMinus1Weight, enterDungeon);
		goFightLes.addStep(new Conditions(below4Weight, hasBootsOfLightness), prepareBelow0);
		goFightLes.addStep(below4Weight, enterDungeonForBoots);

		steps.put(30, goFightLes);

		ConditionalStep goToWitch = new ConditionalStep(this, enterDungeonKilledLes);
		goToWitch.addStep(inWitchRoom, giveWineldaLimps);
		goToWitch.addStep(inMainOrNorthRoom, enterLesDoor);
		steps.put(40, goToWitch);
		steps.put(50, goToWitch);

		// TODO: Verify taking staff doesn't progress quest beyond varp 26 = 60
		ConditionalStep goodOrBadPath = new ConditionalStep(this, enterDungeonGivenLimps);
		goodOrBadPath.addStep(hasStaffOfArmadyl, bringStaffToLucien);
		goodOrBadPath.addStep(new Conditions(inArmaRoom, hasShinyKey), makeChoice);
		goodOrBadPath.addStep(new Conditions(inDemonArea, hasShinyKey), pushWall);
		goodOrBadPath.addStep(new Conditions(LogicType.OR, inArmaRoom, inDemonArea), pickUpKey);
		goodOrBadPath.addStep(new Conditions(LogicType.OR, inMainOrNorthRoom, inWitchRoom), talkToWinelda);
		goodOrBadPath.addStep(hasShinyKey, enterFromMcgrubbors);

		steps.put(60, goodOrBadPath);

		steps.put(70, killLucien);
		// Sided against Lucien, quest ends at varp 80

		return steps;
	}

	public void setupItemRequirements()
	{
		pendantOfLucien = new ItemRequirement("Pendant of lucien", ItemID.PENDANT_OF_LUCIEN, 1, true);
		bootsOfLightness = new ItemRequirement("Boots of lightness", ItemID.BOOTS_OF_LIGHTNESS, 1, true);
		limpwurt20 = new ItemRequirement("Limpwurt (unnoted)", ItemID.LIMPWURT_ROOT, 20);
		yewOrBetterBow = new ItemRequirement("Yew, magic, or dark bow", ItemID.YEW_SHORTBOW);
		yewOrBetterBow.addAlternates(ItemID.YEW_LONGBOW, ItemID.YEW_COMP_BOW, ItemID.MAGIC_SHORTBOW, ItemID.MAGIC_SHORTBOW_I, ItemID.MAGIC_LONGBOW, ItemID.DARK_BOW);
		knife = new ItemRequirement("Knife to get the boots of lightness", ItemID.KNIFE);
		lightSource = new ItemRequirement("A light source to get the boots of lightness", -1, -1);

		iceArrows20 = new ItemRequirement("Ice arrows", ItemID.ICE_ARROWS, 20);

		iceArrows = new ItemRequirement("Ice arrows", ItemID.ICE_ARROWS, 1, true);

		lever = new ItemRequirement("Lever", ItemID.LEVER);
		lever.setHighlightInInventory(true);

		shinyKey = new ItemRequirement("Shiny key", ItemID.SHINY_KEY);

		armadylPendant = new ItemRequirement("Armadyl pendant", ItemID.ARMADYL_PENDANT, 1, true);
		armadylPendant.setHighlightInInventory(true);

		staffOfArmadyl = new ItemRequirement("Staff of Armadyl", ItemID.STAFF_OF_ARMADYL);
	}

	public void loadZones()
	{
		entryRoom1 = new Zone(new WorldPoint(2647, 9803, 0), new WorldPoint(2680, 9814, 0));
		entryRoom2 = new Zone(new WorldPoint(2670, 9801, 0), new WorldPoint(2680, 9804, 0));
		northRoom1 = new Zone(new WorldPoint(2634, 9815, 0), new WorldPoint(2674, 9857, 0));
		northRoom2 = new Zone(new WorldPoint(2634, 9804, 0), new WorldPoint(2647, 9815, 0));
		bootsRoom = new Zone(new WorldPoint(2637, 9759, 0), new WorldPoint(2654, 9767, 0));
		arrowRoom1 = new Zone(new WorldPoint(2657, 9785, 0), new WorldPoint(2692, 9800, 0));
		arrowRoom2 = new Zone(new WorldPoint(2657, 9799, 0), new WorldPoint(2666, 9802, 0));
		arrowRoom3 = new Zone(new WorldPoint(2682, 9799, 0), new WorldPoint(2749, 9852, 0));
		lesRoom = new Zone(new WorldPoint(2639, 9858, 0), new WorldPoint(2651, 9870, 0));
		witchRoom = new Zone(new WorldPoint(2642, 9871, 0), new WorldPoint(2659, 9879, 0));

		demonArea1 = new Zone(new WorldPoint(2625, 9856, 0), new WorldPoint(2638, 9893, 0));
		demonArea2 = new Zone(new WorldPoint(2639, 9880, 0), new WorldPoint(2664, 9892, 0));
		demonArea3 = new Zone(new WorldPoint(2654, 9893, 0), new WorldPoint(2661, 9896, 0));
		demonArea4 = new Zone(new WorldPoint(2659, 9871, 0), new WorldPoint(2665, 9879, 0));

		armaRoom1 = new Zone(new WorldPoint(2633, 9894, 0), new WorldPoint(2651, 9914, 0));
		armaRoom2 = new Zone(new WorldPoint(2642, 9893, 0), new WorldPoint(2645, 9893, 0));
	}

	public void setupConditions()
	{
		hasBootsOfLightness = new ItemRequirementCondition(bootsOfLightness);
		dontHaveBoots = new ItemRequirementCondition(LogicType.NOR, bootsOfLightness);
		hasPendantOfLucien = new ItemRequirementCondition(pendantOfLucien);
		hasYewBow = new ItemRequirementCondition(yewOrBetterBow);
		hasLimpwurts = new ItemRequirementCondition(limpwurt20);
		hasLever = new ItemRequirementCondition(lever);

		below4Weight = new WeightCondition(4, Operation.LESS_EQUAL);
		belowMinus1Weight = new WeightCondition(-1, Operation.LESS_EQUAL);
		inEntryRoom = new ZoneCondition(entryRoom1, entryRoom2);
		inNorthRoom = new ZoneCondition(northRoom1, northRoom2);
		inLesRoom = new ZoneCondition(lesRoom);
		inBootsRoom = new ZoneCondition(bootsRoom);
		inMainOrNorthRoom = new Conditions(LogicType.OR, inEntryRoom, inNorthRoom, inLesRoom);

		pulledLever = new WidgetTextCondition(229, 1, "You hear the clunking of some hidden machinery.");
		leverNearby = new ObjectCondition(ObjectID.LEVER_87, new WorldPoint(2671, 9804, 0));
		inArrowRoom = new ZoneCondition(arrowRoom1, arrowRoom2, arrowRoom3);
		hasEnoughArrows = new Conditions(true, LogicType.OR, new ItemRequirementCondition(iceArrows20));
		lesNearby = new NpcCondition(NpcID.FIRE_WARRIOR_OF_LESARKUS);
		inWitchRoom = new ZoneCondition(witchRoom);
		hasShinyKey = new ItemRequirementCondition(shinyKey);

		inDemonArea = new ZoneCondition(demonArea1, demonArea2, demonArea3, demonArea4);
		inArmaRoom = new ZoneCondition(armaRoom1, armaRoom2);

		hasStaffOfArmadyl = new ItemRequirementCondition(staffOfArmadyl);
	}

	public void setupSteps()
	{
		talkToLucien = new NpcStep(this, NpcID.LUCIEN_3444, new WorldPoint(2573, 3321, 0), "Talk to Lucien in the pub north of East Ardougne castle.");
		prepare = new DetailedQuestStep(this,
			"Get your weight below 0kg. You can get boots of lightness from the Temple of Ikov north of East Ardougne for -4.5kg.",
			pendantOfLucien, limpwurt20, yewOrBetterBow);

		prepareBelow0 = new DetailedQuestStep(this,
			"Get your weight below 0kg.",
			pendantOfLucien, limpwurt20, yewOrBetterBow);

		prepare.addSubSteps(prepareBelow0);

		enterDungeonForBoots = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2677, 3405, 0),
			"Enter the Temple of Ikov. You can get Boots of Lightness inside to get -4.5kg.",
			pendantOfLucien, knife, lightSource, limpwurt20, yewOrBetterBow);

		enterDungeon = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2677, 3405, 0),
			"Enter the Temple of Ikov north of East Ardougne.", pendantOfLucien, yewOrBetterBow, limpwurt20);

		enterDungeon.addSubSteps(enterDungeonForBoots);

		goDownToBoots = new ObjectStep(this, ObjectID.STAIRS_98, new WorldPoint(2651, 9805, 0), "Go down the west stairs to get boots of lightness.", lightSource, knife);
		getBoots = new ItemStep(this, "Get the boots of lightness in the north east corner.", bootsOfLightness);
		goUpFromBoots = new ObjectStep(this, ObjectID.STAIRS_96, new WorldPoint(2639, 9764, 0), "Go back upstairs.");

		pickUpLever = new DetailedQuestStep(this, new WorldPoint(2637, 9819, 0), "Cross the bridge in the north room and pick up the lever whilst weighing less than 0kg.", lever);
		useLeverOnHole = new ObjectStep(this, ObjectID.LEVER_BRACKET, new WorldPoint(2671, 9804, 0), "Use the lever on the lever bracket in the entrance room.", lever);

		pullLever = new ObjectStep(this, ObjectID.LEVER_87, new WorldPoint(2671, 9804, 0), "Pull the lever.");

		enterArrowRoom = new ObjectStep(this, ObjectID.GATE_89, new WorldPoint(2662, 9803, 0), "Enter the south gate.");
		collectArrows = new ObjectStep(this, ObjectID.CLOSED_CHEST, "Search the chests in the ice area of this room until you have at least 20 Ice Arrows or more to be safe. A random chest has the arrows each time.", iceArrows20);
		collectArrows.showArrow(false);
		collectArrows.addAlternateObjects(ObjectID.OPEN_CHEST);

		returnToMainRoom = new ObjectStep(this, ObjectID.GATE_89, new WorldPoint(2662, 9803, 0), "Return back to the entry room when you have enough arrows.");
		//gate 89

		goSearchThievingLever = new ObjectStep(this, ObjectID.LEVER_91, new WorldPoint(2665, 9855, 0),
			"Go into the north room, then on the north side right-click search the lever there for traps, then pull it.");

		goPullThievingLever = new ObjectStep(this, ObjectID.LEVER_91, new WorldPoint(2665, 9855, 0),
			"Go into the north room, then on the north side pull the lever.");

		goSearchThievingLever.addSubSteps(goPullThievingLever);

		tryToEnterWitchRoom = new ObjectStep(this, ObjectID.DOOR_93, new WorldPoint(2646, 9870, 0),
			"Try to enter the far north door. Be prepared to fight Lesarkus, who can only be hurt by ice arrows.", yewOrBetterBow, iceArrows);

		fightLes = new NpcStep(this, NpcID.FIRE_WARRIOR_OF_LESARKUS, new WorldPoint(2646, 9866, 0),
			"Kill the Fire Warrior of Lesarkus. He can only be hurt by the ice arrows.", yewOrBetterBow, iceArrows);

		enterDungeonKilledLes = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2677, 3405, 0),
			"Enter the Temple of Ikov north of East Ardougne.", pendantOfLucien, limpwurt20);

		enterLesDoor = new ObjectStep(this, ObjectID.DOOR_93, new WorldPoint(2646, 9870, 0),
			"Go all the way north and talk to Winelda there.", pendantOfLucien, limpwurt20);

		enterLesDoor.addSubSteps(enterDungeonKilledLes);

		giveWineldaLimps = new NpcStep(this, NpcID.WINELDA, new WorldPoint(2655, 9876, 0), "Give Winelda 20 unnoted limpwurts.", limpwurt20);

		enterDungeonGivenLimps = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2677, 3405, 0),
			"Enter the Temple of Ikov north of East Ardougne. If you got the shiny key, get it and enter via Mcgrubber's wood west of Seers' Village.", pendantOfLucien);

		enterFromMcgrubbors = new ObjectStep(this, ObjectID.LADDER_17384, new WorldPoint(2659, 3492, 0), "Enter the house at Mcgrubbor's wood and go down the ladder.", shinyKey);


		talkToWinelda = new NpcStep(this, NpcID.WINELDA, new WorldPoint(2655, 9876, 0),
			"Talk to Winelda in the far north of the dungeon.");

		pickUpKey = new DetailedQuestStep(this, new WorldPoint(2628, 9859, 0), "Follow the path around past the skeletons and lesser demons until you find a shiny key. Pick it up.", shinyKey);

		pushWall = new ObjectStep(this, ObjectID.WALL_1597, new WorldPoint(2643, 9892, 0), "Push the wall on the north side of this area.");
		pushWall.addSubSteps(enterDungeonGivenLimps, enterFromMcgrubbors);

		makeChoice = new DetailedQuestStep(this,
			"You can now choose to either help Lucien or help the Guardians of Armadyl. To help Lucien, simply pick up the Staff of Armadyl. To help the guardians, remove the pendant of lucien and talk to one of them.");
		makeChoice.addDialogStep("I seek the Staff of Armadyl.");
		makeChoice.addDialogStep("Lucien will give me a grand reward for it!");
		makeChoice.addDialogStep("You're right, it's time for my yearly bath.");
		makeChoice.addDialogStep("Ok! I'll help!");

		killLucien = new NpcStep(this, NpcID.LUCIEN, new WorldPoint(3122, 3484, 0), "Equip the Armadyl Pendant and kill Lucien in the house west of the Grand Exchange.", armadylPendant);
		bringStaffToLucien = new NpcStep(this, NpcID.LUCIEN, new WorldPoint(3122, 3484, 0), "Bring the Staff of Armadyl to Lucien in the house west of the Grand Exchange.", staffOfArmadyl);

		returnToLucien = new DetailedQuestStep(this, "Either return to Lucien west of the Grand Exchange with the Staff of Armadyl, or kill him whilst wearing the Pendant of Armadyl.");
		returnToLucien.addSubSteps(killLucien, bringStaffToLucien);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(yewOrBetterBow);
		reqs.add(limpwurt20);
		reqs.add(knife);
		reqs.add(lightSource);
		return reqs;
	}


	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Fire Warrior of Lesarkus (level 84)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToLucien))));
		allSteps.add(new PanelDetails("Defeat Lesarkus", new ArrayList<>(
			Arrays.asList(prepare, enterDungeon, goDownToBoots, getBoots, goUpFromBoots, pickUpLever, useLeverOnHole,
				pullLever, enterArrowRoom, collectArrows, returnToMainRoom, goSearchThievingLever, tryToEnterWitchRoom, fightLes)), yewOrBetterBow, knife, lightSource, limpwurt20));
		allSteps.add(new PanelDetails("Explore deeper", new ArrayList<>(Arrays.asList(enterLesDoor, giveWineldaLimps, pickUpKey, pushWall, makeChoice, returnToLucien))));
		return allSteps;
	}
}
