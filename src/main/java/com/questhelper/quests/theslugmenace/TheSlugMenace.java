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

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetModelRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.ItemStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_SLUG_MENACE
)
public class TheSlugMenace extends BasicQuestHelper
{
	//Items Required
	ItemRequirement commorb, commorb2, deadSeaSlug, swampPaste, page1, page2, page3, glue, doorTranscript, pageFragment1, pageFragment2, pageFragment3,
		essence, blankAir, blankEarth, blankWater, blankFire, blankMind, chisel, airRune, waterRune, earthRune, fireRune, mindRune, meleeGear, essence5, accessToAltars;

	//Items Recommended
	ItemRequirement ardougneTeleports, necklaceOfPassage, airAltarTeleport, earthAltarTeleport, fireAltarTeleport, waterAltarTeleport, mindAltarTeleport;

	Requirement talkedToMaledict, talkedToHobb, talkedToHolgart, talkedToAllImportantPeople, inHobgoblinDungeon, inSeaSlugDungeon, openedWall, receivedFragments,
		onPlatform, puzzleUp, repairedPage, pickedUpSlug, hasOrUsedAirRune, hasOrUsedWaterRune, hasOrUsedEarthRune, hasOrUsedFireRune, hasOrUsedMindRune,
		hasAllRunes, usedAirRune, usedWaterRune, usedEarthRune, usedFireRune, usedMindRune, usedAllRunes;

	QuestStep talkToTiffy, talkToNiall, talkToMaledict, talkToHobb, talkToHolgart, talkToNiall2, enterDungeon, pushFalseWall, enterWall, tryToOpenImposingDoor, scanWithComm, pickUpDeadSlug, talkToJorral,
		talkToNiall3, talkToMaledict2, talkToMaledict3, searchMayorsDesk, talkToLovecraft, talkToNiall4, useSwampPasteOnFragments, talkToJeb, talkToBailey, useGlueOnFragment, solvePuzzle, useEmptyRunes,
		enterDungeonAgain, enterWallAgain, useEmptyRunesOnDoor, killSlugPrince, reportBackToTiffy, enterDungeonAgainUsedRunes;

	//Zones
	Zone hobgoblinDungeon, seaSlugDungeon, platform;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
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
		getSlugAndTalkJorral.addStep(new Conditions(LogicType.OR, deadSeaSlug, pickedUpSlug), talkToJorral);

		steps.put(4, getSlugAndTalkJorral);

		steps.put(5, talkToNiall3);
		steps.put(6, talkToNiall3);
		steps.put(7, talkToMaledict2);
		steps.put(8, talkToMaledict3);

		ConditionalStep findPages = new ConditionalStep(this, searchMayorsDesk);
		findPages.addStep(receivedFragments, useSwampPasteOnFragments);
		findPages.addStep(new Conditions(page1, page2), talkToNiall4);
		findPages.addStep(page1, talkToLovecraft);

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

	@Override
	public void setupRequirements()
	{
		commorb = new ItemRequirement("Commorb (can get another from Sir Tiffy)", ItemID.COMMORB).isNotConsumed();
		commorb.addAlternates(ItemID.COMMORB_V2);

		commorb2 = new ItemRequirement("Commorb v2", ItemID.COMMORB_V2).isNotConsumed();
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

		receivedFragments = new VarbitRequirement(2619, 1);
		glue = new ItemRequirement("Sea slug glue", ItemID.SEA_SLUG_GLUE);
		glue.setHighlightInInventory(true);

		blankAir = new ItemRequirement("Blank air rune", ItemID.BLANK_AIR_RUNE);
		blankEarth = new ItemRequirement("Blank earth rune", ItemID.BLANK_EARTH_RUNE);
		blankWater = new ItemRequirement("Blank water rune", ItemID.BLANK_WATER_RUNE);
		blankFire = new ItemRequirement("Blank fire rune", ItemID.BLANK_FIRE_RUNE);
		blankMind = new ItemRequirement("Blank mind rune", ItemID.BLANK_MIND_RUNE);

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed();

		airRune = new ItemRequirement("Air rune", ItemID.AIR_RUNE_9693);
		earthRune = new ItemRequirement("Earth rune", ItemID.EARTH_RUNE_9695);
		waterRune = new ItemRequirement("Water rune", ItemID.WATER_RUNE_9691);
		fireRune = new ItemRequirement("Fire rune", ItemID.FIRE_RUNE_9699);
		mindRune = new ItemRequirement("Mind rune", ItemID.MIND_RUNE_9697);

		meleeGear = new ItemRequirement("Melee weapon to fight the Slug Prince", -1, -1).isNotConsumed();
		meleeGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		doorTranscript = new ItemRequirement("Door transcription", ItemID.DOOR_TRANSCRIPTION);

		ardougneTeleports = new ItemRequirement("Ardougne teleports", ItemID.ARDOUGNE_TELEPORT, 3);
		essence5 = new ItemRequirement("Rune/pure essence, 15 to be safe", ItemID.PURE_ESSENCE, 5);
		essence5.addAlternates(ItemID.RUNE_ESSENCE);

		ItemRequirement airTalisman = new ItemRequirement("Air talisman", ItemID.AIR_TALISMAN);
		airTalisman.addAlternates(ItemID.AIR_TIARA);

		ItemRequirement waterTalisman = new ItemRequirement("Water talisman", ItemID.WATER_TALISMAN);
		waterTalisman.addAlternates(ItemID.WATER_TIARA);

		ItemRequirement earthTalisman = new ItemRequirement("Air talisman", ItemID.EARTH_TALISMAN);
		earthTalisman.addAlternates(ItemID.EARTH_TIARA);

		ItemRequirement fireTalisman = new ItemRequirement("Fire talisman", ItemID.FIRE_TALISMAN);
		fireTalisman.addAlternates(ItemID.FIRE_TIARA);

		ItemRequirement mindTalisman = new ItemRequirement("Mind talisman", ItemID.MIND_TALISMAN);
		mindTalisman.addAlternates(ItemID.MIND_TIARA);

		accessToAltars = new ItemRequirements("Access to air, water, earth, fire, and mind runecrafting altars",
			airTalisman, waterTalisman, earthTalisman, fireTalisman, mindTalisman).isNotConsumed();

		necklaceOfPassage = new ItemRequirement("Necklace of Passage", ItemCollections.NECKLACE_OF_PASSAGES);

		airAltarTeleport = new ItemRequirement("Teleport near Air Altar", ItemCollections.SKILLS_NECKLACES);
		airAltarTeleport.addAlternates(ItemID.FALADOR_TELEPORT, ItemID.RIMMINGTON_TELEPORT);
		airAltarTeleport.setDisplayMatchedItemName(true);
		airAltarTeleport.setTooltip("The best items for this are (in order):");
		airAltarTeleport.appendToTooltip("Skills Necklace (to Crafting Guild)");
		airAltarTeleport.appendToTooltip("Falador Teleport");
		airAltarTeleport.appendToTooltip("Rimmington/House Teleport");

		earthAltarTeleport = new ItemRequirement("Teleport near Earth Altar", ItemCollections.DIGSITE_PENDANTS);
		earthAltarTeleport.addAlternates(ItemID.VARROCK_TELEPORT, ItemID.LUMBERYARD_TELEPORT, ItemID.DIGSITE_TELEPORT);
		earthAltarTeleport.setDisplayMatchedItemName(true);
		earthAltarTeleport.setTooltip("The best items for this are (in order):");
		earthAltarTeleport.appendToTooltip("Lumberyard Teleport");
		earthAltarTeleport.appendToTooltip("Digsite Pendant(s)");
		earthAltarTeleport.appendToTooltip("Digsite Teleport");
		earthAltarTeleport.appendToTooltip("Varrock Teleports");

		fireAltarTeleport = new ItemRequirement("Teleport near Fire Altar", ItemCollections.RING_OF_DUELINGS);
		fireAltarTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		fireAltarTeleport.setTooltip("The best items for this are (in order):");
		fireAltarTeleport.appendToTooltip("Ring of Dueling");
		fireAltarTeleport.appendToTooltip("Amulet of Glory (to Al Kharid)");

		waterAltarTeleport = new ItemRequirement("Teleport near Water Altar", ItemID.LUMBRIDGE_TELEPORT);
		waterAltarTeleport.setTooltip("The best items for this are (in order):");
		waterAltarTeleport.appendToTooltip("Lumbridge Graveyard Teleport");
		waterAltarTeleport.appendToTooltip("Lumbridge Teleport");

		mindAltarTeleport = new ItemRequirement("Teleport near Mind Altar", ItemID.MIND_ALTAR_TELEPORT);
		mindAltarTeleport.addAlternates(ItemID.FALADOR_TELEPORT, ItemID.LASSAR_TELEPORT, ItemID.TAVERLEY_TELEPORT);
		mindAltarTeleport.addAlternates(ItemCollections.COMBAT_BRACELETS);
		mindAltarTeleport.setTooltip("The best items for this are (in order):");
		mindAltarTeleport.appendToTooltip("Mind Altar Teleport (highly recommended)");
		mindAltarTeleport.appendToTooltip("Lassar Teleport (Ice Mountain)");
		mindAltarTeleport.appendToTooltip("Combat Bracelet to Monastery");
		mindAltarTeleport.appendToTooltip("Falador Teleport");
		mindAltarTeleport.appendToTooltip("Taverley Teleport");
	}

	public void loadZones()
	{
		hobgoblinDungeon = new Zone(new WorldPoint(2691, 9665, 0), new WorldPoint(2749, 9720, 0));
		seaSlugDungeon = new Zone(new WorldPoint(2304, 5059, 0), new WorldPoint(2377, 5124, 0));
		platform = new Zone(new WorldPoint(2760, 3271, 0), new WorldPoint(2795, 3293, 1));
	}

	public void setupConditions()
	{
		talkedToHolgart = new VarbitRequirement(2614, 1);
		talkedToHobb = new VarbitRequirement(2615, 1);
		talkedToMaledict = new VarbitRequirement(2616, 1);
		talkedToAllImportantPeople = new VarbitRequirement(2617, 7);
		inHobgoblinDungeon = new ZoneRequirement(hobgoblinDungeon);
		inSeaSlugDungeon = new ZoneRequirement(seaSlugDungeon);
		openedWall = new VarbitRequirement(2618, 1);

		onPlatform = new ZoneRequirement(platform);

		puzzleUp = new WidgetModelRequirement(460, 4, 18393);

		repairedPage = new VarbitRequirement(2611, 1);

		pickedUpSlug = new VarbitRequirement(2631, 1);

		usedAirRune = new VarbitRequirement(2623, 1);
		usedEarthRune = new VarbitRequirement(2622, 1);
		usedWaterRune = new VarbitRequirement(2625, 1);
		usedFireRune = new VarbitRequirement(2624, 1);
		usedMindRune = new VarbitRequirement(2626, 1);

		hasOrUsedAirRune = new Conditions(LogicType.OR, airRune, usedAirRune);
		hasOrUsedWaterRune = new Conditions(LogicType.OR, waterRune, usedWaterRune);
		hasOrUsedEarthRune = new Conditions(LogicType.OR, earthRune, usedEarthRune);
		hasOrUsedFireRune = new Conditions(LogicType.OR, fireRune, usedFireRune);
		hasOrUsedMindRune = new Conditions(LogicType.OR, mindRune, usedMindRune);

		hasAllRunes = new Conditions(hasOrUsedAirRune, hasOrUsedEarthRune, hasOrUsedFireRune, hasOrUsedMindRune, hasOrUsedWaterRune);

		usedAllRunes = new VarbitRequirement(2627, 31);
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
		talkToJorral = new NpcStep(this, NpcID.JORRAL, new WorldPoint(2436, 3347, 0), "Talk to Jorral north of West Ardougne.", necklaceOfPassage);
		talkToJorral.addDialogStep("Translations");
		talkToNiall3 = new NpcStep(this, NpcID.COL_ONIALL, new WorldPoint(2739, 3311, 0), "Return to Col. O'Niall on the pier in Witchaven.");
		talkToMaledict2 = new NpcStep(this, NpcID.BROTHER_MALEDICT, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		talkToMaledict3 = new NpcStep(this, NpcID.BROTHER_MALEDICT_4788, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		searchMayorsDesk = new ObjectStep(this, ObjectID.STUDY_DESK_18224, new WorldPoint(2709, 3294, 0), "Search the study desk in the Mayor's house for a page.");
		talkToLovecraft = new NpcStep(this, NpcID.EZEKIAL_LOVECRAFT, new WorldPoint(2734, 3291, 0), "Talk to Ezekial Lovecraft in the fishing shop in east Witchaven for a page.");
		talkToNiall4 = new NpcStep(this, NpcID.COL_ONIALL_4782, new WorldPoint(2739, 3311, 0), "Talk to Col. O'Niall on the pier in Witchaven for a page.");
		useSwampPasteOnFragments = new DetailedQuestStep(this, "Use some swamp paste on one of the page fragments.", swampPaste, pageFragment1, pageFragment2, pageFragment3);
		talkToJeb = new NpcStep(this, NpcID.JEB_4803, new WorldPoint(2721, 3304, 0), "Talk to Jeb north of Witchaven to travel to the Fishing Platform.", deadSeaSlug);
		talkToBailey = new NpcStep(this, NpcID.BAILEY, new WorldPoint(2764, 3275, 0), "Talk to Bailey on the Fishing Platform.", deadSeaSlug);
		useGlueOnFragment = new DetailedQuestStep(this, "Use the slug glue on one of the fragments.", glue, pageFragment1);

		solvePuzzle = new PuzzleStep(this);

		useEmptyRunes = new DetailedQuestStep(this, "Right-click each page to turn rune/pure essence into empty runes. Take each empty rune and use it on its respective Runecrafting Altar. Bring extra essence (~10 extra) as it is possible to accidentally destroy the essence upon creation.", page1, page2, page3, essence, chisel);

		enterDungeonAgain = new ObjectStep(this, ObjectID.OLD_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Prepare to fight the Slug Prince (level 62). Only melee can hurt it. Then, enter the old ruin entrance west of Witchaven.", meleeGear, airRune, waterRune, earthRune, fireRune, mindRune);
		enterDungeonAgainUsedRunes = new ObjectStep(this, ObjectID.OLD_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Prepare to fight the Slug Prince (level 62). Only melee can hurt it. Then, enter the old ruin entrance west of Witchaven.", meleeGear);

		enterWallAgain = new ObjectStep(this, NullObjectID.NULL_19124, new WorldPoint(2701, 9688, 0), "Enter the wall just east of where you come down.");
		useEmptyRunesOnDoor = new ObjectStep(this, ObjectID.IMPOSING_DOORS, new WorldPoint(2351, 5093, 0), "Use the runes on the imposing doors at the end of the path.", airRune, waterRune, earthRune, fireRune, mindRune);
		killSlugPrince = new NpcStep(this, NpcID.SLUG_PRINCE, new WorldPoint(2351, 5093, 0), "Kill the Slug Prince. Only melee can hurt it.");

		reportBackToTiffy = new NpcStep(this, NpcID.SIR_TIFFY_CASHIEN, new WorldPoint(2996, 3373, 0), "Report back to Sir Tiffy Cashien in Falador Park.");
		reportBackToTiffy.addDialogStep("Slug Menace.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
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
	public List<ItemRequirement> getItemRecommended()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(ardougneTeleports);
		reqs.add(necklaceOfPassage);
		reqs.addAll(Arrays.asList(airAltarTeleport, earthAltarTeleport, fireAltarTeleport, waterAltarTeleport, mindAltarTeleport));
		return reqs;
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Slug Prince (level 62) (can only be hurt by melee)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.WANTED, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.SEA_SLUG, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.CRAFTING, 30));
		req.add(new SkillRequirement(Skill.RUNECRAFT, 30));
		req.add(new SkillRequirement(Skill.SLAYER, 30));
		req.add(new SkillRequirement(Skill.THIEVING, 30));
		return req;
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
				new ExperienceReward(Skill.RUNECRAFT, 3500),
				new ExperienceReward(Skill.CRAFTING, 3500),
				new ExperienceReward(Skill.THIEVING, 3500));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Ability to purchase and equip Proselyte equipment."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Collections.singletonList(talkToTiffy),
			commorb));
		allSteps.add(new PanelDetails("Investigating", Arrays.asList(talkToNiall,
			talkToMaledict, talkToHobb, talkToHolgart, talkToNiall2, enterDungeon,
			pushFalseWall, tryToOpenImposingDoor, scanWithComm, pickUpDeadSlug), commorb2));
		allSteps.add(new PanelDetails("Uncovering the truth", Arrays.asList(talkToJorral,
			talkToNiall3, talkToMaledict2, searchMayorsDesk, talkToLovecraft, talkToNiall4,
			useSwampPasteOnFragments, talkToJeb, talkToBailey, useGlueOnFragment, solvePuzzle,
			useEmptyRunes), commorb2, deadSeaSlug, swampPaste, chisel, essence5, accessToAltars));
		allSteps.add(new PanelDetails("Facing the prince", Arrays.asList(enterDungeonAgain,
			enterWallAgain, useEmptyRunesOnDoor, killSlugPrince, reportBackToTiffy),
			meleeGear, airRune, waterRune, earthRune, fireRune, mindRune, commorb2));
		return allSteps;
	}
}
