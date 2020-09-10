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
package com.questhelper.quests.theslugmenace;

import com.questhelper.QuestHelperQuest;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.WidgetModelCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
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
	quest = QuestHelperQuest.THE_SLUG_MENACE
)
public class TheSlugMenace extends BasicQuestHelper
{
	ItemRequirement commorb, commorb2, deadSeaSlug, swampPaste, page1, page2, page3, glue, doorTranscript, pageFragment1, pageFragment2, pageFragment3,
		essence, blankAir, blankEarth, blankWater, blankFire, blankMind, chisel, airRune, waterRune, earthRune, fireRune, mindRune, meleeGear, ardougneTeleports,
		essence5, accessToAltars;

	ConditionForStep talkedToMaledict, talkedToHobb, talkedToHolgart, talkedToAllImportantPeople, inHobgoblinDungeon, inSeaSlugDungeon, openedWall, hasDeadSlug, hasPage1, hasPage2, hasPage3, receivedFragments,
		onPlatform, puzzleUp, repairedPage, pickedUpSlug, hasAirRune, hasWaterRune, hasEarthRune, hasFireRune, hasMindRune, hasOrUsedAirRune, hasOrUsedWaterRune, hasOrUsedEarthRune, hasOrUsedFireRune, hasOrUsedMindRune,
		hasAllRunes, usedAirRune, usedWaterRune, usedEarthRune, usedFireRune, usedMindRune, usedAllRunes;

	QuestStep talkToTiffy, talkToNiall, talkToMaledict, talkToHobb, talkToHolgart, talkToNiall2, enterDungeon, pushFalseWall, enterWall, tryToOpenImposingDoor, scanWithComm, pickUpDeadSlug, talkToJorral,
		talkToNiall3, talkToMaledict2, talkToMaledict3, searchMayorsDesk, talkToLovecraft, talkToNiall4, useSwampPasteOnFragments, talkToJeb, talkToBailey, useGlueOnFragment, solvePuzzle, useEmptyRunes,
		enterDungeonAgain, enterWallAgain, useEmptyRunesOnDoor, killSlugPrince, reportBackToTiffy, enterDungeonAgainUsedRunes;

	Zone hobgoblinDungeon, seaSlugDungeon, platform;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToTiffy);
		steps.put(1, talkToNiall);

		ConditionalStep talkToThreePeople = new ConditionalStep(this, talkToMaledict);
		talkToThreePeople.addStep(talkedToAllImportantPeople, talkToNiall2);
		talkToThreePeople.addStep(new Conditions(talkedToMaledict, talkedToHobb), talkToHolgart);
		talkToThreePeople.addStep(talkedToMaledict, talkToHobb);

		steps.put(2, talkToThreePeople);

		ConditionalStep goToDungeon = new ConditionalStep(this, enterDungeon);
		goToDungeon.addStep(inSeaSlugDungeon, tryToOpenImposingDoor);
		goToDungeon.addStep(new Conditions(inHobgoblinDungeon, openedWall), enterWall);
		goToDungeon.addStep(inHobgoblinDungeon, pushFalseWall);

		steps.put(3, goToDungeon);

		ConditionalStep getSlugAndTalkJorral = new ConditionalStep(this, pickUpDeadSlug);
		getSlugAndTalkJorral.addStep(new Conditions(LogicType.OR, hasDeadSlug, pickedUpSlug), talkToJorral);

		steps.put(4, getSlugAndTalkJorral);

		steps.put(5, talkToNiall3);
		steps.put(6, talkToNiall3);
		steps.put(7, talkToMaledict2);
		steps.put(8, talkToMaledict3);

		ConditionalStep findPages = new ConditionalStep(this, searchMayorsDesk);
		findPages.addStep(receivedFragments, useSwampPasteOnFragments);
		findPages.addStep(new Conditions(hasPage1, hasPage2), talkToNiall4);
		findPages.addStep(hasPage1, talkToLovecraft);

		steps.put(9, findPages);

		ConditionalStep goMakePaste = new ConditionalStep(this, talkToJeb);
		goMakePaste.addStep(onPlatform, talkToBailey);

		steps.put(10, goMakePaste);

		ConditionalStep glueTogether = new ConditionalStep(this, useGlueOnFragment);
		glueTogether.addStep(new Conditions(inSeaSlugDungeon, usedAllRunes), killSlugPrince);
		glueTogether.addStep(new Conditions(hasAllRunes, inSeaSlugDungeon), useEmptyRunesOnDoor);
		glueTogether.addStep(new Conditions(hasAllRunes, inHobgoblinDungeon), enterWallAgain);
		glueTogether.addStep(usedAllRunes, enterDungeonAgainUsedRunes);
		glueTogether.addStep(hasAllRunes, enterDungeonAgain);
		glueTogether.addStep(repairedPage, useEmptyRunes);
		glueTogether.addStep(puzzleUp, solvePuzzle);

		steps.put(11, glueTogether);

		steps.put(12, reportBackToTiffy);

		return steps;
	}

	public void setupItemRequirements()
	{
		commorb = new ItemRequirement("Commorb (can get another from Sir Tiffy)", ItemID.COMMORB);

		commorb2 = new ItemRequirement("Commorb v2", ItemID.COMMORB_V2);
		commorb2.setHighlightInInventory(true);

		deadSeaSlug = new ItemRequirement("Dead sea slug", ItemID.DEAD_SEA_SLUG);

		swampPaste = new ItemRequirement("Swamp paste", ItemID.SWAMP_PASTE);
		swampPaste.setHighlightInInventory(true);

		essence = new ItemRequirement("Rune or Pure Essence", ItemID.PURE_ESSENCE);
		essence.addAlternates(ItemID.RUNE_ESSENCE);

		page1 = new ItemRequirement("Page 1", ItemID.PAGE_1);
		page1.setHighlightInInventory(true);
		page2 = new ItemRequirement("Page 2", ItemID.PAGE_2);
		page2.setHighlightInInventory(true);
		page3 = new ItemRequirement("Page 3", ItemID.PAGE_3);
		page3.setHighlightInInventory(true);

		pageFragment1 = new ItemRequirement("Fragment 1", ItemID.FRAGMENT_1);
		pageFragment1.setHighlightInInventory(true);
		pageFragment2 = new ItemRequirement("Fragment 2", ItemID.FRAGMENT_2);
		pageFragment2.setHighlightInInventory(true);
		pageFragment3 = new ItemRequirement("Fragment 3", ItemID.FRAGMENT_3);
		pageFragment3.setHighlightInInventory(true);

		receivedFragments = new VarbitCondition(2619, 1);
		glue = new ItemRequirement("Sea slug glue", ItemID.SEA_SLUG_GLUE);
		glue.setHighlightInInventory(true);

		blankAir = new ItemRequirement("Blank air rune", ItemID.BLANK_AIR_RUNE);
		blankEarth = new ItemRequirement("Blank earth rune", ItemID.BLANK_EARTH_RUNE);
		blankWater = new ItemRequirement("Blank water rune", ItemID.BLANK_WATER_RUNE);
		blankFire = new ItemRequirement("Blank fire rune", ItemID.BLANK_FIRE_RUNE);
		blankMind = new ItemRequirement("Blank mind rune", ItemID.BLANK_MIND_RUNE);

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);

		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE_9693);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE_9695);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE_9691);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE_9699);
		mindRune = new ItemRequirement("Mind rune", ItemID.MIND_RUNE_9697);

		meleeGear = new ItemRequirement("Melee weapon to fight the Slug Prince", -1, -1);

		doorTranscript = new ItemRequirement("Door transcription", ItemID.DOOR_TRANSCRIPTION);

		ardougneTeleports = new ItemRequirement("Ardougne teleports", ItemID.ARDOUGNE_TELEPORT);
		essence5 = new ItemRequirement("At least 5 rune/pure essence, 15 to be safe", ItemID.PURE_ESSENCE, -1);
		essence5.addAlternates(ItemID.RUNE_ESSENCE);

		accessToAltars = new ItemRequirement("Access to air, water, earth, fire, and mind runecrafting altars", -1, -1);
	}

	public void loadZones()
	{
		hobgoblinDungeon = new Zone(new WorldPoint(2691, 9665, 0), new WorldPoint(2749, 9720, 0));
		seaSlugDungeon = new Zone(new WorldPoint(2304, 5059, 0), new WorldPoint(2377, 5124, 0));
		platform = new Zone(new WorldPoint(2760, 3271, 0), new WorldPoint(2795, 3293, 1));
	}

	public void setupConditions()
	{
		talkedToHolgart = new VarbitCondition(2614, 1);
		talkedToHobb = new VarbitCondition(2615, 1);
		talkedToMaledict = new VarbitCondition(2616, 1);
		talkedToAllImportantPeople = new VarbitCondition(2617, 7);
		inHobgoblinDungeon = new ZoneCondition(hobgoblinDungeon);
		inSeaSlugDungeon = new ZoneCondition(seaSlugDungeon);
		openedWall = new VarbitCondition(2618, 1);

		hasDeadSlug = new ItemRequirementCondition(deadSeaSlug);
		hasPage1 = new ItemRequirementCondition(page1);
		hasPage2 = new ItemRequirementCondition(page2);
		hasPage3 = new ItemRequirementCondition(page3);

		onPlatform = new ZoneCondition(platform);

		puzzleUp = new WidgetModelCondition(460, 4, 18393);

		repairedPage = new VarbitCondition(2611, 1);

		pickedUpSlug = new VarbitCondition(2631, 1);

		hasAirRune = new ItemRequirementCondition(airRune);
		hasEarthRune = new ItemRequirementCondition(earthRune);
		hasWaterRune = new ItemRequirementCondition(waterRune);
		hasFireRune = new ItemRequirementCondition(fireRune);
		hasMindRune = new ItemRequirementCondition(mindRune);

		usedAirRune = new VarbitCondition(2623, 1);
		usedEarthRune = new VarbitCondition(2622, 1);
		usedWaterRune = new VarbitCondition(2625, 1);
		usedFireRune = new VarbitCondition(2624, 1);
		usedMindRune = new VarbitCondition(2626, 1);

		hasOrUsedAirRune = new Conditions(LogicType.OR, hasAirRune, usedAirRune);
		hasOrUsedWaterRune = new Conditions(LogicType.OR, hasWaterRune, usedWaterRune);
		hasOrUsedEarthRune = new Conditions(LogicType.OR, hasEarthRune, usedEarthRune);
		hasOrUsedFireRune = new Conditions(LogicType.OR, hasFireRune, usedFireRune);
		hasOrUsedMindRune = new Conditions(LogicType.OR, hasMindRune, usedMindRune);

		hasAllRunes = new Conditions(hasOrUsedAirRune, hasOrUsedEarthRune, hasOrUsedFireRune, hasOrUsedMindRune, hasOrUsedWaterRune);

		usedAllRunes = new VarbitCondition(2627, 31);
	}

	public void setupSteps()
	{
		talkToTiffy = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, new WorldPoint(2996, 3373, 0), "Talk to Sir Tiffy Cashien in Falador Park.", commorb);
		talkToTiffy.addDialogStep("Do you have any jobs for me yet?");
		talkToNiall = new NpcStep(this, NpcID.COL_ONIALL, new WorldPoint(2739, 3311, 0), "Talk to Col. O'Niall on the pier in Witchaven, east of Ardougne.");
		talkToNiall.addDialogStep("Who are the important people in Witchaven?");
		talkToNiall.addDialogStep("Nothing at the moment thanks.");

		talkToMaledict = new NpcStep(this, NpcID.BROTHER_MALEDICT, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		talkToMaledict.addDialogStep("That's enough for now.");
		talkToHobb = new NpcStep(this, NpcID.MAYOR_HOBB, new WorldPoint(2709, 3291, 0), "Talk to Mayor Hobb in north west Witchaven.");
		talkToHobb.addDialogStep("I'm just looking around.");
		talkToHobb.addDialogStep("Nothing at the moment thanks.");

		// Asked to scan Hobb, 2621, 0->1
		// Scanned, 1->2

		talkToHolgart = new NpcStep(this, NpcID.HOLGART_7789, new WorldPoint(2721, 3304, 0), "Talk to Holgart north of Witchaven.");
		talkToHolgart.addDialogStep("Nothing at the moment thanks.");

		talkToNiall2 = new NpcStep(this, NpcID.COL_ONIALL, new WorldPoint(2739, 3311, 0), "Return to Col. O'Niall on the pier in Witchaven.");
		enterDungeon = new ObjectStep(this, ObjectID.OLD_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Enter the old ruin entrance west of Witchaven.");
		enterDungeon.addDialogStep("That's enough for now.");

		pushFalseWall = new ObjectStep(this, NullObjectID.NULL_19124, new WorldPoint(2701, 9688, 0), "Push the wall just east of where you come down.");
		enterWall = new ObjectStep(this, NullObjectID.NULL_19124, new WorldPoint(2701, 9688, 0), "Enter the secret room.");
		tryToOpenImposingDoor = new ObjectStep(this, ObjectID.IMPOSING_DOORS, new WorldPoint(2351, 5093, 0), "Follow the path until you reach an imposing door, and try opening it. After, try scanning with the commorb v2.", commorb2);
		scanWithComm = new DetailedQuestStep(this, "Try scanning with the commorb.", commorb);
		pickUpDeadSlug = new ItemStep(this, "Pick up the dead sea slug next to the imposing door.", deadSeaSlug);
		talkToJorral = new NpcStep(this, NpcID.JORRAL, new WorldPoint(2436, 3347, 0), "Talk to Jorral north of West Ardougne.");
		talkToNiall3 = new NpcStep(this, NpcID.COL_ONIALL, new WorldPoint(2739, 3311, 0), "Return to Col. O'Niall on the pier in Witchaven.");
		talkToMaledict2 = new NpcStep(this, NpcID.BROTHER_MALEDICT, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		talkToMaledict3 = new NpcStep(this, NpcID.BROTHER_MALEDICT_4788, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		searchMayorsDesk = new ObjectStep(this, ObjectID.STUDY_DESK_18224, new WorldPoint(2709, 3294, 0), "Search the study desk in the Mayor's house for a page.");
		talkToLovecraft = new NpcStep(this, NpcID.EZEKIAL_LOVECRAFT, new WorldPoint(2734, 3291, 0), "Talk to Ezekial Lovecraft in the fishing shop in east Witchaven for a page.");
		talkToNiall4 = new NpcStep(this, NpcID.COL_ONIALL_4782, new WorldPoint(2739, 3311, 0), "Talk to Col. O'Niall on the pier in Witchaven for a page.");
		useSwampPasteOnFragments = new DetailedQuestStep(this, "Use some swamp paste on one of the page fragments.", swampPaste, pageFragment1, pageFragment2, pageFragment3);
		talkToJeb = new NpcStep(this, NpcID.JEB_4803, new WorldPoint(2721, 3304, 0), "Talk to Jeb north of Witchaven to travel to the Fishing Platform.", deadSeaSlug);
		talkToBailey = new NpcStep(this, NpcID.BAILEY, new WorldPoint(2764, 3275, 0), "Talk to Bailey on the Fishing Platform.", deadSeaSlug);
		useGlueOnFragment = new DetailedQuestStep(this, "Us the slug glue on one of the fragments.", glue, pageFragment1);

		solvePuzzle = new PuzzleStep(this);

		useEmptyRunes = new DetailedQuestStep(this, "Right-click each page to turn rune/pure essence into empty runes. Take each empty rune and use it on its respective Runecrafting Altar.", page1, page2, page3 ,essence, chisel);

		enterDungeonAgain = new ObjectStep(this, ObjectID.OLD_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Prepare to fight the Slug Prince (level 62). Only melee can hurt it. Then, enter the old ruin entrance west of Witchaven.", meleeGear, airRune, waterRune, earthRune, fireRune, mindRune);
		enterDungeonAgainUsedRunes = new ObjectStep(this, ObjectID.OLD_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Prepare to fight the Slug Prince (level 62). Only melee can hurt it. Then, enter the old ruin entrance west of Witchaven.", meleeGear);

		enterWallAgain = new ObjectStep(this, NullObjectID.NULL_19124, new WorldPoint(2701, 9688, 0), "Enter the wall just east of where you come down.");
		useEmptyRunesOnDoor = new ObjectStep(this, ObjectID.IMPOSING_DOORS, new WorldPoint(2351, 5093, 0), "Use the runes on the imposing doors at the end of the path.", airRune, waterRune, earthRune, fireRune, mindRune);
		killSlugPrince = new NpcStep(this, NpcID.SLUG_PRINCE, new WorldPoint(2351, 5093, 0), "Kill the Slug Prince. Only melee can hurt it.");

		reportBackToTiffy = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, new WorldPoint(2996, 3373, 0), "Report back to Sir Tiffy Cashien in Falador Park.");
		reportBackToTiffy.addDialogStep("Slug Menace.");

	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(commorb);
		reqs.add(swampPaste);
		reqs.add(essence5);
		reqs.add(chisel);
		reqs.add(accessToAltars);
		return reqs;
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ardougneTeleports);
		return reqs;
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Slug Prince (level 62) (can only be hurt by melee)");
		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(talkToTiffy)), commorb));
		allSteps.add(new PanelDetails("Investigating", new ArrayList<>(Arrays.asList(talkToNiall, talkToMaledict, talkToHobb, talkToHolgart, talkToNiall2, enterDungeon, pushFalseWall, tryToOpenImposingDoor,
			scanWithComm, pickUpDeadSlug)), commorb2));
		allSteps.add(new PanelDetails("Uncovering the truth", new ArrayList<>(Arrays.asList(talkToJorral, talkToNiall3, talkToMaledict2, searchMayorsDesk, talkToLovecraft, talkToNiall4, useSwampPasteOnFragments, talkToJeb, talkToBailey, useGlueOnFragment, solvePuzzle, useEmptyRunes)), commorb2, deadSeaSlug, swampPaste, chisel, essence5, accessToAltars));
		allSteps.add(new PanelDetails("Facing the prince", new ArrayList<>(Arrays.asList(enterDungeonAgain, enterWallAgain, useEmptyRunesOnDoor, killSlugPrince, reportBackToTiffy)), meleeGear, airRune, waterRune, earthRune, fireRune, mindRune));
		return allSteps;
	}
}
