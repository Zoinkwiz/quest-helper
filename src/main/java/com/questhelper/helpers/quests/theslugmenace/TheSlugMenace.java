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
package com.questhelper.helpers.quests.theslugmenace;

import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.questinfo.QuestHelperQuest;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.widget.WidgetPresenceRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.rewards.ExperienceReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

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
		initializeRequirements();
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
	protected void setupRequirements()
	{
		commorb = new ItemRequirement("Commorb (can get another from Sir Tiffy)", ItemID.WANTED_CRYSTAL_BALL).isNotConsumed();
		commorb.addAlternates(ItemID.SLUG2_CRYSTAL_BALL);

		commorb2 = new ItemRequirement("Commorb v2", ItemID.SLUG2_CRYSTAL_BALL).isNotConsumed();
		commorb2.setHighlightInInventory(true);

		deadSeaSlug = new ItemRequirement("Dead sea slug", ItemID.SLUG2_SEASLUG_YOUNG);

		swampPaste = new ItemRequirement("Swamp paste", ItemID.SWAMPPASTE);
		swampPaste.setHighlightInInventory(true);

		essence = new ItemRequirement("Rune or Pure Essence", ItemID.BLANKRUNE_HIGH);
		essence.addAlternates(ItemID.BLANKRUNE);

		page1 = new ItemRequirement("Page 1", ItemID.SLUG2_PAGE1);
		page1.setHighlightInInventory(true);
		page2 = new ItemRequirement("Page 2", ItemID.SLUG2_PAGE2);
		page2.setHighlightInInventory(true);
		page3 = new ItemRequirement("Page 3", ItemID.SLUG2_PAGE3);
		page3.setHighlightInInventory(true);

		pageFragment1 = new ItemRequirement("Fragment 1", ItemID.SLUG2_PAGE4A);
		pageFragment1.setHighlightInInventory(true);
		pageFragment2 = new ItemRequirement("Fragment 2", ItemID.SLUG2_PAGE4B);
		pageFragment2.setHighlightInInventory(true);
		pageFragment3 = new ItemRequirement("Fragment 3", ItemID.SLUG2_PAGE4C);
		pageFragment3.setHighlightInInventory(true);

		receivedFragments = new VarbitRequirement(2619, 1);
		glue = new ItemRequirement("Sea slug glue", ItemID.SLUG2_SLUG_PASTE);
		glue.setHighlightInInventory(true);

		blankAir = new ItemRequirement("Blank air rune", ItemID.SLUG2_RUNE_AIR_BLANK);
		blankEarth = new ItemRequirement("Blank earth rune", ItemID.SLUG2_RUNE_EARTH_BLANK);
		blankWater = new ItemRequirement("Blank water rune", ItemID.SLUG2_RUNE_WATER_BLANK);
		blankFire = new ItemRequirement("Blank fire rune", ItemID.SLUG2_RUNE_FIRE_BLANK);
		blankMind = new ItemRequirement("Blank mind rune", ItemID.SLUG2_RUNE_MIND_BLANK);

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL).isNotConsumed();

		usedAirRune = new VarbitRequirement(2623, 1);
		usedEarthRune = new VarbitRequirement(2622, 1);
		usedWaterRune = new VarbitRequirement(2625, 1);
		usedFireRune = new VarbitRequirement(2624, 1);
		usedMindRune = new VarbitRequirement(2626, 1);

		airRune = new ItemRequirement("Air rune", ItemID.SLUG2_RUNE_AIR).hideConditioned(usedAirRune);
		earthRune = new ItemRequirement("Earth rune", ItemID.SLUG2_RUNE_EARTH).hideConditioned(usedEarthRune);
		waterRune = new ItemRequirement("Water rune", ItemID.SLUG2_RUNE_WATER).hideConditioned(usedWaterRune);
		fireRune = new ItemRequirement("Fire rune", ItemID.SLUG2_RUNE_FIRE).hideConditioned(usedFireRune);
		mindRune = new ItemRequirement("Mind rune", ItemID.SLUG2_RUNE_MIND).hideConditioned(usedMindRune);

		meleeGear = new ItemRequirement("Melee weapon to fight the Slug Prince", -1, -1).isNotConsumed();
		meleeGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		doorTranscript = new ItemRequirement("Door transcription", ItemID.SLUG2_TRANSCRIPT);

		ardougneTeleports = new ItemRequirement("Ardougne teleports", ItemID.POH_TABLET_ARDOUGNETELEPORT, 3);
		essence5 = new ItemRequirement("Rune/pure essence, 15 to be safe", ItemID.BLANKRUNE_HIGH, 5);
		essence5.addAlternates(ItemID.BLANKRUNE);

		ItemRequirement airTalisman = new ItemRequirement("Access to the Air Altar", ItemCollections.AIR_ALTAR).isNotConsumed();
		airTalisman.setTooltip("Air Talisman/Tiara, Elemental Talisman/Tiara, RC-skill cape or via Abyss.");

		ItemRequirement waterTalisman = new ItemRequirement("Access to the Water Altar", ItemCollections.WATER_ALTAR).isNotConsumed();
		waterTalisman.setTooltip("Water Talisman/Tiara, Elemental Talisman/Tiara, RC-skill cape or via Abyss.");

		ItemRequirement earthTalisman = new ItemRequirement("Access to the Earth Altar", ItemCollections.EARTH_ALTAR).isNotConsumed();
		earthTalisman.setTooltip("Earth Talisman/Tiara, Elemental Talisman/Tiara, RC-skill cape or via Abyss.");

		ItemRequirement fireTalisman = new ItemRequirement("Access to the Fire Altar", ItemCollections.FIRE_ALTAR).isNotConsumed();
		fireTalisman.setTooltip("Fire Talisman/Tiara, Elemental Talisman/Tiara, RC-skill cape or via Abyss.");

		ItemRequirement mindTalisman = new ItemRequirement("Access to the Mind Altar", ItemCollections.MIND_ALTAR).isNotConsumed();
		mindTalisman.setTooltip("Mind Talisman/Tiara, Catalytic Talisman/Tiara, RC-skill cape or via Abyss.");

		accessToAltars = new ItemRequirements("Access to the Air, Water, Earth, Fire, and Mind runecrafting altars",
			airTalisman, waterTalisman, earthTalisman, fireTalisman, mindTalisman).isNotConsumed();

		necklaceOfPassage = new ItemRequirement("Necklace of Passage", ItemCollections.NECKLACE_OF_PASSAGES);

		airAltarTeleport = new ItemRequirement("Teleport near Air Altar", ItemCollections.SKILLS_NECKLACES);
		airAltarTeleport.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT, ItemID.NZONE_TELETAB_RIMMINGTON);
		airAltarTeleport.setDisplayMatchedItemName(true);
		airAltarTeleport.setTooltip("The best items for this are (in order):");
		airAltarTeleport.appendToTooltip("Ring Of The Elements");
		airAltarTeleport.appendToTooltip("Skills Necklace (to Crafting Guild)");
		airAltarTeleport.appendToTooltip("Falador Teleport");
		airAltarTeleport.appendToTooltip("Rimmington/House Teleport");

		earthAltarTeleport = new ItemRequirement("Teleport near Earth Altar", ItemCollections.DIGSITE_PENDANTS);
		earthAltarTeleport.addAlternates(ItemID.POH_TABLET_VARROCKTELEPORT, ItemID.TELEPORTSCROLL_LUMBERYARD, ItemID.TELEPORTSCROLL_DIGSITE);
		earthAltarTeleport.setDisplayMatchedItemName(true);
		earthAltarTeleport.setTooltip("The best items for this are (in order):");
		earthAltarTeleport.appendToTooltip("Ring Of The Elements");
		earthAltarTeleport.appendToTooltip("Lumberyard Teleport");
		earthAltarTeleport.appendToTooltip("Digsite Pendant(s)");
		earthAltarTeleport.appendToTooltip("Digsite Teleport");
		earthAltarTeleport.appendToTooltip("Varrock Teleports");

		fireAltarTeleport = new ItemRequirement("Teleport near Fire Altar", ItemCollections.RING_OF_DUELINGS);
		fireAltarTeleport.addAlternates(ItemCollections.AMULET_OF_GLORIES);
		fireAltarTeleport.setTooltip("The best items for this are (in order):");
		fireAltarTeleport.appendToTooltip("Ring Of The Elements");
		fireAltarTeleport.appendToTooltip("Ring of Dueling");
		fireAltarTeleport.appendToTooltip("Amulet of Glory (to Al Kharid)");

		waterAltarTeleport = new ItemRequirement("Teleport near Water Altar", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		waterAltarTeleport.setTooltip("The best items for this are (in order):");
		waterAltarTeleport.appendToTooltip("Ring Of The Elements");
		waterAltarTeleport.appendToTooltip("Lumbridge Teleport");

		mindAltarTeleport = new ItemRequirement("Teleport near Mind Altar", ItemID.TELETAB_MIND_ALTAR);
		mindAltarTeleport.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT, ItemID.TABLET_LASSAR, ItemID.NZONE_TELETAB_TAVERLEY);
		mindAltarTeleport.addAlternates(ItemCollections.COMBAT_BRACELETS);
		mindAltarTeleport.setTooltip("The best items for this are (in order):");
		mindAltarTeleport.appendToTooltip("Mind Altar Teleport (highly recommended)");
		mindAltarTeleport.appendToTooltip("Lassar Teleport (Ice Mountain)");
		mindAltarTeleport.appendToTooltip("Combat Bracelet to Monastery");
		mindAltarTeleport.appendToTooltip("Falador Teleport");
		mindAltarTeleport.appendToTooltip("Taverley Teleport");
	}

	@Override
	protected void setupZones()
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

		puzzleUp = new WidgetPresenceRequirement(460, 8);

		repairedPage = new VarbitRequirement(2611, 1);

		pickedUpSlug = new VarbitRequirement(2631, 1);

		hasOrUsedAirRune = new Conditions(LogicType.OR, airRune.alsoCheckBank(questBank), usedAirRune);
		hasOrUsedWaterRune = new Conditions(LogicType.OR, waterRune.alsoCheckBank(questBank), usedWaterRune);
		hasOrUsedEarthRune = new Conditions(LogicType.OR, earthRune.alsoCheckBank(questBank), usedEarthRune);
		hasOrUsedFireRune = new Conditions(LogicType.OR, fireRune.alsoCheckBank(questBank), usedFireRune);
		hasOrUsedMindRune = new Conditions(LogicType.OR, mindRune.alsoCheckBank(questBank), usedMindRune);

		hasAllRunes = new Conditions(hasOrUsedAirRune, hasOrUsedEarthRune, hasOrUsedFireRune, hasOrUsedMindRune, hasOrUsedWaterRune);

		usedAllRunes = new VarbitRequirement(2627, 31);
	}

	public void setupSteps()
	{
		talkToTiffy = new NpcStep(this, NpcID.RD_TELEPORTER_GUY, new WorldPoint(2996, 3373, 0), "Talk to Sir Tiffy Cashien in Falador Park.", commorb);
		talkToTiffy.addDialogStep("Do you have any jobs for me yet?");
		talkToNiall = new NpcStep(this, NpcID.SLUG2_ONIALL_STAGE1, new WorldPoint(2739, 3311, 0), "Talk to Col. O'Niall on the pier in Witchaven, east of Ardougne.");
		talkToNiall.addDialogStep("Who are the important people in Witchaven?");
		talkToNiall.addDialogStep("Nothing at the moment thanks.");

		talkToMaledict = new NpcStep(this, NpcID.SLUG2_MALEDICT_STAGE1, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		talkToMaledict.addDialogStep("That's enough for now.");
		talkToHobb = new NpcStep(this, NpcID.SLUG2_HOBB_STAGE2, new WorldPoint(2709, 3291, 0), "Talk to Mayor Hobb in north west Witchaven.");
		talkToHobb.addDialogStep("I'm just looking around.");
		talkToHobb.addDialogStep("Nothing at the moment thanks.");

		// Asked to scan Hobb, 2621, 0->1
		// Scanned, 1->2

		talkToHolgart = new NpcStep(this, NpcID.HOLGARTLANDTRAVEL, new WorldPoint(2721, 3304, 0), "Talk to Holgart north of Witchaven.");
		talkToHolgart.addDialogStep("Nothing at the moment thanks.");

		talkToNiall2 = new NpcStep(this, NpcID.SLUG2_ONIALL_STAGE1, new WorldPoint(2739, 3311, 0), "Return to Col. O'Niall on the pier in Witchaven.");
		enterDungeon = new ObjectStep(this, ObjectID.SLUG2_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Enter the old ruin entrance west of Witchaven.");
		enterDungeon.addDialogStep("That's enough for now.");

		pushFalseWall = new ObjectStep(this, ObjectID.SLUG2_HIDDEN_ENTRANCE, new WorldPoint(2701, 9688, 0), "Push the wall just east of where you come down.");
		enterWall = new ObjectStep(this, ObjectID.SLUG2_HIDDEN_ENTRANCE, new WorldPoint(2701, 9688, 0), "Enter the secret room.");
		tryToOpenImposingDoor = new ObjectStep(this, ObjectID.SLUG2_CAVE_DOORS_CLOSED, new WorldPoint(2351, 5093, 0), "Follow the path until you reach an imposing door, and try opening it. After, try scanning with the commorb v2.", commorb2);
		scanWithComm = new DetailedQuestStep(this, "Try scanning with the commorb.", commorb);
		pickUpDeadSlug = new ItemStep(this, "Pick up the dead sea slug next to the imposing door.", deadSeaSlug);
		talkToJorral = new NpcStep(this, NpcID.MAKINGHISTORY_JORRAL, new WorldPoint(2436, 3347, 0), "Talk to Jorral north of West Ardougne.", Collections.singletonList(doorTranscript),
			Collections.singletonList(necklaceOfPassage));
		talkToJorral.addDialogStep("Translations");
		talkToNiall3 = new NpcStep(this, NpcID.SLUG2_ONIALL_STAGE1, new WorldPoint(2739, 3311, 0), "Return to Col. O'Niall on the pier in Witchaven.");
		talkToMaledict2 = new NpcStep(this, NpcID.SLUG2_MALEDICT_STAGE1, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		talkToMaledict3 = new NpcStep(this, NpcID.SLUG2_MALEDICT_STAGE2, new WorldPoint(2724, 3283, 0), "Talk to Brother Maledict in the church.");
		searchMayorsDesk = new ObjectStep(this, ObjectID.SLUG2_MAYORS_DESK, new WorldPoint(2709, 3294, 0), "Search the study desk in the Mayor's house for a page.");
		talkToLovecraft = new NpcStep(this, NpcID.SLUG2_LOVECRAFT, new WorldPoint(2734, 3291, 0), "Talk to Ezekial Lovecraft in the fishing shop in east Witchaven for a page.");
		talkToNiall4 = new NpcStep(this, NpcID.SLUG2_ONIALL_STAGE2, new WorldPoint(2739, 3311, 0), "Talk to Col. O'Niall on the pier in Witchaven for a page.");
		useSwampPasteOnFragments = new DetailedQuestStep(this, "Use some swamp paste on one of the page fragments.", swampPaste, pageFragment1, pageFragment2, pageFragment3);
		talkToJeb = new NpcStep(this, NpcID.SLUG2_JEB_STAGE2, new WorldPoint(2721, 3304, 0), "Talk to Jeb north of Witchaven to travel to the Fishing Platform.", deadSeaSlug);
		talkToBailey = new NpcStep(this, NpcID.BAILEY, new WorldPoint(2764, 3275, 0), "Talk to Bailey on the Fishing Platform.", deadSeaSlug);
		useGlueOnFragment = new DetailedQuestStep(this, "Use the slug glue on one of the fragments.", glue, pageFragment1);

		solvePuzzle = new PuzzleWrapperStep(this, new PuzzleStep(this), "Combine the fragments.");

		// TODO: Expand out this section to be more descriptive and guiding
		useEmptyRunes = new DetailedQuestStep(this, "Right-click each page to turn rune/pure essence into empty runes. Take each empty rune and use it on its respective Runecrafting Altar. Bring extra essence (~10 extra) as it is possible to accidentally destroy the essence upon creation.", page1, page2, page3, essence, chisel);

		enterDungeonAgain = new ObjectStep(this, ObjectID.SLUG2_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Prepare to fight the Slug Prince (level 62). Only melee can hurt it. Then, enter the old ruin entrance west of Witchaven.", meleeGear, airRune, waterRune, earthRune, fireRune, mindRune);
		enterDungeonAgainUsedRunes = new ObjectStep(this, ObjectID.SLUG2_RUIN_ENTRANCE, new WorldPoint(2696, 3283, 0), "Prepare to fight the Slug Prince (level 62). Only melee can hurt it. Then, enter the old ruin entrance west of Witchaven.", meleeGear);

		enterWallAgain = new ObjectStep(this, ObjectID.SLUG2_HIDDEN_ENTRANCE, new WorldPoint(2701, 9688, 0), "Enter the wall just east of where you come down.");
		useEmptyRunesOnDoor = new ObjectStep(this, ObjectID.SLUG2_CAVE_DOORS_CLOSED, new WorldPoint(2351, 5093, 0), "Use the runes on the imposing doors at the end of the path.", airRune, waterRune, earthRune, fireRune, mindRune);
		killSlugPrince = new NpcStep(this, NpcID.SLUG2_THE_SLUG_PRINCE, new WorldPoint(2351, 5093, 0), "Kill the Slug Prince. Only melee can hurt it.");

		reportBackToTiffy = new NpcStep(this, NpcID.RD_TELEPORTER_GUY, new WorldPoint(2996, 3373, 0), "Report back to Sir Tiffy Cashien in Falador Park.");
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
			useEmptyRunes), doorTranscript, commorb2, deadSeaSlug, swampPaste, chisel, essence5, accessToAltars));
		allSteps.add(new PanelDetails("Facing the prince", Arrays.asList(enterDungeonAgain,
			enterWallAgain, useEmptyRunesOnDoor, killSlugPrince, reportBackToTiffy),
			meleeGear, airRune, waterRune, earthRune, fireRune, mindRune, commorb2));
		return allSteps;
	}
}
