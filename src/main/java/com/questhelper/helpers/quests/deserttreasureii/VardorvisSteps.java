/*
 * Copyright (c) 2023, Zoinkwiz
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
package com.questhelper.helpers.quests.deserttreasureii;

import com.questhelper.bank.QuestBank;
import com.questhelper.bank.banktab.BankSlotIcons;
import com.questhelper.collections.ItemCollections;
import com.questhelper.questhelpers.QuestHelper;
import com.questhelper.questhelpers.QuestUtil;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.item.ItemOnTileRequirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Operation;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.zone.Zone;
import com.questhelper.requirements.zone.ZoneRequirement;
import com.questhelper.steps.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;

import java.util.Arrays;
import java.util.List;

import static com.questhelper.requirements.util.LogicHelper.and;
import static com.questhelper.requirements.util.LogicHelper.or;

public class VardorvisSteps extends ConditionalStep
{
	ItemRequirement xericTalisman, combatGear, nardahTeleport;
	DetailedQuestStep talkToElissa, talkToBarus, searchDesk, readPotionNote, drinkPotion, boardBoat, runIntoStanglewood,
		talkToKasonde, enterEntry, defendKasonde, defendKasondeSidebar, leaveTowerDefenseRoom, talkToKasondeAfterTowerDefense,
		getBerry, getHerb, getHerbSidebar, goDownToKasonde, defendKasondeHerb, talkToKasondeWithHerbAndBerry, addHerb, addBerry,
		drinkStranglewoodPotion, goToRitualSite, goToRitualSiteShortcut, fightVardorvis, fightVardorvisSidebar, pickUpTempleKey, getTempleKeyFromRocks,
		returnToDesertWithVardorvisMedallion, useVardorvisMedallionOnStatue;

	ConditionalStep returnToKasondeWithTempleKey, defeatKasonde, goTalkToKasondeAfterFight, goGetVardorvisMedallion;
	Requirement talkedToElissa, talkedToBarus, haveReadPotionNote, haveDrunkPotion, inStrangewood, finishedStranglewoodCutscene,
		talkedToKasonde, inTowerDefenseRoom, defendedKasonde, toldAboutHerbAndBerry, herbTaken, berryTaken,
		inStranglewoodPyramidRoom, defendedKasondeWithHerb, receivedSerum, addedHerb, addedBerry, drankPotion,
		inAnyStranglewood, inVardorvisArea, unlockedShortcut, defeatedVardorvis, templeKeyNearby, kasondeAggressive, givenKasondeKey, defeatedKasonde,
		kasondeRevealedMedallion, gotVardorvisMedallion, inVault;
	ItemRequirement potionNote, strangePotion, freezes, berry, herb, unfinishedSerum, serumWithHerb, stranglerSerum, templeKey,
		vardorvisMedallion, food;
	Zone stranglewood, towerDefenseRoom, stranglewoodPyramidRoom, vardorvisArea, vault;

	QuestBank questBank;

	public VardorvisSteps(QuestHelper questHelper, NpcStep defaultStep, QuestBank questBank) // talkToElissa
	{
		super(questHelper, defaultStep);
		this.questBank = questBank;
		talkToElissa = defaultStep;

		setupItemRequirements();
		setupZones();
		setupConditions();
		setupSteps();

		addStep(new Conditions(gotVardorvisMedallion, inVault), useVardorvisMedallionOnStatue);
		addStep(gotVardorvisMedallion, returnToDesertWithVardorvisMedallion);
		addStep(kasondeRevealedMedallion, goGetVardorvisMedallion);
		addStep(defeatedKasonde, goTalkToKasondeAfterFight);
		addStep(kasondeAggressive, defeatKasonde);
		addStep(defeatedVardorvis, returnToKasondeWithTempleKey);
		addStep(new Conditions(inVardorvisArea, defeatedVardorvis), pickUpTempleKey);
		addStep(new Conditions(inVardorvisArea, drankPotion), fightVardorvis);
		addStep(and(inAnyStranglewood, drankPotion, unlockedShortcut), goToRitualSiteShortcut);
		addStep(new Conditions(inAnyStranglewood, drankPotion), goToRitualSite);
		addStep(new Conditions(inAnyStranglewood, addedBerry), drinkStranglewoodPotion);
		addStep(new Conditions(inAnyStranglewood, addedHerb), addBerry);
		addStep(new Conditions(inAnyStranglewood, receivedSerum), addHerb);
		addStep(new Conditions(inStranglewoodPyramidRoom, defendedKasondeWithHerb, herbTaken, berryTaken),
			talkToKasondeWithHerbAndBerry);
		addStep(new Conditions(inStranglewoodPyramidRoom, herbTaken, berryTaken), defendKasondeHerb);
		addStep(new Conditions(inStrangewood, herbTaken, berryTaken), goDownToKasonde);
		addStep(new Conditions(inStrangewood, herbTaken), getBerry);
		addStep(new Conditions(inStrangewood, toldAboutHerbAndBerry), getHerb);
		addStep(new Conditions(inTowerDefenseRoom, defendedKasonde), leaveTowerDefenseRoom);
		addStep(new Conditions(inStrangewood, defendedKasonde), talkToKasondeAfterTowerDefense);
		addStep(new Conditions(inTowerDefenseRoom), defendKasonde);
		addStep(new Conditions(inStrangewood, talkedToKasonde), enterEntry);
		addStep(new Conditions(inStrangewood, finishedStranglewoodCutscene), talkToKasonde);
		addStep(new Conditions(inStrangewood), runIntoStanglewood);
		addStep(new Conditions(haveDrunkPotion), boardBoat);
		addStep(new Conditions(haveReadPotionNote, strangePotion.alsoCheckBank(questBank)), drinkPotion);
		addStep(new Conditions(new Conditions(LogicType.NOR, haveReadPotionNote), potionNote.alsoCheckBank(questBank)), readPotionNote);
		addStep(talkedToBarus, searchDesk);
		addStep(talkedToElissa, talkToBarus);
	}

	protected void setupZones()
	{
		vault = new Zone(new WorldPoint(3925, 9620, 1), new WorldPoint(3949, 9643, 1));
		stranglewood = new Zone(new WorldPoint(1087, 3264, 0), new WorldPoint(1261, 3458, 0));
		towerDefenseRoom = new Zone(new WorldPoint(1160, 9740, 0), new WorldPoint(1210, 9780, 0));
		stranglewoodPyramidRoom = new Zone(new WorldPoint(1177, 9810, 0), new WorldPoint(1190, 9846, 0));
		vardorvisArea = new Zone(new WorldPoint(1119, 3405, 0), new WorldPoint(1140, 3430, 0));
	}

	protected void setupItemRequirements()
	{
		VarbitRequirement nardahTeleportInBook = new VarbitRequirement(VarbitID.BOOKOFSCROLLS_NARDAH, 1, Operation.GREATER_EQUAL);
		nardahTeleport = new ItemRequirement("Nardah teleport, or Fairy Ring to DLQ", ItemID.DESERT_AMULET_ELITE);
		nardahTeleport.setAdditionalOptions(nardahTeleportInBook);
		nardahTeleport.addAlternates(ItemID.DESERT_AMULET_HARD, ItemID.TELEPORTSCROLL_NARDAH, ItemID.DESERT_AMULET_MEDIUM);
		nardahTeleport.addAlternates(ItemCollections.FAIRY_STAFF);

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());

		xericTalisman = new ItemRequirement("Xeric's talisman", ItemID.XERIC_TALISMAN);

		potionNote = new ItemRequirement("Potion note", ItemID.DT2_KASONDE_NOTE);
		strangePotion = new ItemRequirement("Strange potion", ItemID.DT2_KASONDE_POTION);
		food = new ItemRequirement("Bring high healing food to tank the infected.", -1);
		freezes = new ItemRequirement("Freezing spells STRONGLY recommended + reasonable mage accuracy", -1);
		berry = new ItemRequirement("Argian berries", ItemID.DT2_STRANGLEWOOD_BERRIES);
		berry.setTooltip("You can get another from the south-west corner of The Stranglewood");
		herb = new ItemRequirement("Korbal herb", ItemID.DT2_STRANGLEWOOD_HERB);
		herb.setTooltip("You can get another from the north-west corner of The Stranglewood");
		unfinishedSerum = new ItemRequirement("Unfinished serum", ItemID.DT2_STRANGLEWOOD_POTION_UNFINISHED_1);
		serumWithHerb = new ItemRequirement("Unfinished serum (herb added)", ItemID.DT2_STRANGLEWOOD_POTION_UNFINISHED_2);
		stranglerSerum = new ItemRequirement("Strangler serum", ItemID.DT2_STRANGLEWOOD_POTION);
		templeKey = new ItemRequirement("Temple key", ItemID.DT2_STRANGLEWOOD_KEY);
		vardorvisMedallion = new ItemRequirement("Vardorvis' medallion", ItemID.DT2_MEDALLION_VARDORVIS);
		vardorvisMedallion.setTooltip("You can get another from Kasonde's hideout");
	}

	protected void setupConditions()
	{
		inVault = new ZoneRequirement(vault);

		/* Vardorvis */
		talkedToElissa = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 4, Operation.GREATER_EQUAL);
		talkedToBarus = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 6, Operation.GREATER_EQUAL);
		haveReadPotionNote = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 8, Operation.GREATER_EQUAL);
		haveDrunkPotion = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 10, Operation.GREATER_EQUAL);
		inStrangewood = new ZoneRequirement(stranglewood);
//		seenStranglewoodCutscene = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 14, Operation.GREATER_EQUAL);
		finishedStranglewoodCutscene = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 16, Operation.GREATER_EQUAL);
		// Entered Stranglewood
		// 15125 10->12
		// 14862 42->44
		// 15160 0->3, happens whenever you enter the Stranglewood
		// 15099 = how infected you are

		// Cutscene upon entering area
		// 15160 3->2
		// 12139 0->1

		// After Cutscene, 15099 44->39
		// 12427 0->1
		// 12428 0->2
		talkedToKasonde = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 18, Operation.GREATER_EQUAL);

		// 15100, 0->400 (is a timer for tower defense)
		// 15125 18->20, gone into Entry
		// 15101 0->85 (Kasonde's health)

		// A, SW CHest
		// VarClientInt 1121 0->5
		// VarClientInt 1122 0->5
		// VarClientInt 1123 0->5
		inTowerDefenseRoom = new ZoneRequirement(towerDefenseRoom);

		defendedKasonde = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 22, Operation.GREATER_EQUAL);
		toldAboutHerbAndBerry = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 24, Operation.GREATER_EQUAL);
		// 15136 0->2 taken herb
		// 15125 24->26, herb taken
		herbTaken = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD_INGREDIENT_1, 2);
		// 15125 26->28, picked berry
		// 15137, 0->1 berry taken
		berryTaken = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD_INGREDIENT_2, 1);

		// 15125 28->30->32 when entering pyramid
		inStranglewoodPyramidRoom = new ZoneRequirement(stranglewoodPyramidRoom);
		defendedKasondeWithHerb = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 34, Operation.GREATER_EQUAL);
		receivedSerum = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 36, Operation.GREATER_EQUAL);
		addedHerb = serumWithHerb.alsoCheckBank(questBank);
		addedBerry = stranglerSerum.alsoCheckBank(questBank);
		drankPotion = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 38, Operation.GREATER_EQUAL);
		inAnyStranglewood = new Conditions(LogicType.OR, inStranglewoodPyramidRoom, inStrangewood);

		// Vardorvis arena entered
		// 15125 38->40
		// 14862 44->46
		unlockedShortcut = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 40, Operation.GREATER_EQUAL);
		defeatedVardorvis = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 42, Operation.GREATER_EQUAL);
		inVardorvisArea = new ZoneRequirement(vardorvisArea);
		templeKeyNearby = new ItemOnTileRequirement(templeKey);
		givenKasondeKey = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 46, Operation.GREATER_EQUAL);
		kasondeAggressive = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 48, Operation.GREATER_EQUAL);
		defeatedKasonde = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 50, Operation.GREATER_EQUAL);
		kasondeRevealedMedallion = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 52, Operation.GREATER_EQUAL);
		gotVardorvisMedallion = new VarbitRequirement(VarbitID.DT2_STRANGLEWOOD, 54, Operation.GREATER_EQUAL);
	}

	protected void setupSteps()
	{
		talkToBarus = new NpcStep(getQuestHelper(), NpcID.DT2_LOVAKENGJ_HISTORIAN, new WorldPoint(1459, 3782, 0), "Talk to Barus near the burning man in south-west Lovakengj.");
		// TODO: Highlight the widget
		talkToBarus.addTeleport(xericTalisman.named("Xeric's talisman ([3] Xeric's Inferno)"));

		searchDesk = new ObjectStep(getQuestHelper(), ObjectID.DT2_KASONDE_DESK, new WorldPoint(1781, 3619, 0),
			"Search the desk in the house south of the Hosidius Estate Agent.");
		searchDesk.addTeleport(xericTalisman.named("Xeric's talisman ([2] Xeric's Glade)"));

		readPotionNote = new ItemStep(getQuestHelper(), "Read the potion note.", potionNote.highlighted());
		drinkPotion = new ItemStep(getQuestHelper(), "Drink the strange potion.", strangePotion.highlighted());
		boardBoat = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_BOAT_IN, new WorldPoint(1227, 3470, 0),
			"Board the boat south of Quidamortem into The Stranglewood. You can use the Fairy Ring BLS to get nearby, or travel with the Mountain Guide.");
		runIntoStanglewood = new DetailedQuestStep(getQuestHelper(), new WorldPoint(1194, 3394, 0), "Run deeper into Stranglewood. " +
			"Be careful of the Strangled, as they'll bind you and deal damage.");
		talkToKasonde = new NpcStep(getQuestHelper(), NpcID.DT2_KASONDE_VIS, new WorldPoint(1191, 3404, 0), "Talk to Kasonde.");
		enterEntry = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_HIDEOUT_ENTRY, new WorldPoint(1191, 3411, 0), "Hide away in the Entry in the north-west of the room.");
		defendKasondeSidebar = new NpcStep(getQuestHelper(), NpcID.DT2_STRANGLED_SURVIVAL_T1_1, "Defend Kasonde! There are barricades in the stone chests you can set up to block routes. " +
			"There are also satchels you can place on the floor, and detonate using the Detonator. This will kill all of the Strangled in a 7x7 area, as well as damaging you or " +
			"Kasonde if either of you are in the blast radius.", true);
		defendKasondeSidebar.addText("Closed chests require you to guess the correct code to open, with correct numbers in the correct place being marked in green, " +
			"and correct numbers in the wrong places being marked with blue.");
		defendKasondeSidebar.addText("It's recommended to also use freezes if you have ancient magicks with you to keep them off of Kasonde. If you have freezes, you can largely ignore the barricading and explosives.");
		((NpcStep) defendKasondeSidebar).addAlternateNpcs(NpcID.DT2_STRANGLED_SURVIVAL_T1_2, NpcID.DT2_STRANGLED_SURVIVAL_T1_3, NpcID.DT2_STRANGLED_SURVIVAL_T2_1);

		defendKasonde = new DetailedQuestStep(getQuestHelper(), "Defend Kasonde! Read the sidebar for more details.");
		defendKasonde.addRecommended(freezes);
		defendKasonde.addRecommended(food);
		defendKasondeSidebar.addSubSteps(defendKasonde);

		// TODO: Get actual coordinate and ladder ID!
		leaveTowerDefenseRoom = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_HIDEOUT_EXIT, new WorldPoint(1187, 9764, 0),
			"Leave the dungeon up the ladder.");
		talkToKasondeAfterTowerDefense = new NpcStep(getQuestHelper(), NpcID.DT2_KASONDE_VIS, new WorldPoint(1191, 3404, 0),
			"Talk to Kasonde on the surface.");
		getHerb = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_HERBS_OP, new WorldPoint(1155, 3447, 0),
			"Go get the herb from the north-west corner of the area. " +
				"The stangled will attack you, so bring food and freezes to trap them. More info in the sidebar.");
		getHerb.setLinePoints(Arrays.asList(
			new WorldPoint(1193, 3403, 0),
			new WorldPoint(1193, 3395, 0),
			new WorldPoint(1186, 3395, 0),
			new WorldPoint(1186, 3416, 0),
			new WorldPoint(1165, 3415, 0),
			new WorldPoint(1161, 3415, 0),
			new WorldPoint(1161, 3426, 0),
			new WorldPoint(1159, 3426, 0),
			new WorldPoint(1159, 3428, 0),
			new WorldPoint(1161, 3441, 0)
		));

		getHerbSidebar = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_HERBS_OP, new WorldPoint(1111, 3434, 0),
			"Go get the herb. The Strangled will attack you, so have food. If your infected bar reaches full, " +
				"you'll be teleported to the starting room again. ");
		getHerbSidebar.addText("You can get some stink bombs from the chest in the south-east corner of Kasonde's room, " +
			"which when used attract the Strangled to the location. This can be useful for avoiding them.");
		getHerbSidebar.addText("Freezes and blood spells are useful for trapping them and healing up.");
		getHerbSidebar.addSubSteps(getHerb);

		getBerry = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_BUSH_OP, new WorldPoint(1126, 3323, 0),
			"Get the berry from the south-west part of the area. Beware the Strangled still.");
		getBerry.setLinePoints(Arrays.asList(
			new WorldPoint(1161, 3441, 0),
			new WorldPoint(1159, 3428, 0),
			new WorldPoint(1159, 3426, 0),
			new WorldPoint(1161, 3426, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1144, 3342, 0),
			new WorldPoint(1126, 3323, 0)
		));

		goDownToKasonde = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_TEMPLE_ENTRY, new WorldPoint(1174, 3428, 0),
			"Go to Kasonde, who is inside the main pyramid of the area to the north. Be ready to fight a few Strangled.", combatGear, berry, herb);
		goDownToKasonde.setLinePoints(Arrays.asList(
			new WorldPoint(1126, 3323, 0),
			new WorldPoint(1144, 3342, 0),
			new WorldPoint(1144, 3357, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1174, 3416, 0),
			new WorldPoint(1174, 3427, 0)
		));
		defendKasondeHerb = new NpcStep(getQuestHelper(), NpcID.DT2_STRANGLED_TEMPLE_2, new WorldPoint(1183, 9824, 0),
			"Defeat the Strangled attacking Kasonde.", true);
		((NpcStep) defendKasondeHerb).addAlternateNpcs(NpcID.DT2_STRANGLED_TEMPLE_1);

		talkToKasondeWithHerbAndBerry = new NpcStep(getQuestHelper(), NpcID.DT2_KASONDE_TEMPLE, new WorldPoint(1183, 9824, 0),
			"Talk to Kasonde.", berry, herb);
		addHerb = new DetailedQuestStep(getQuestHelper(), "Add the herb to unfinished serum.", herb.highlighted(), unfinishedSerum.highlighted());
		addBerry = new DetailedQuestStep(getQuestHelper(), "Add the berries to the serum.", serumWithHerb.highlighted(), berry.highlighted());
		drinkStranglewoodPotion = new DetailedQuestStep(getQuestHelper(), "Drink the strangler serum.", stranglerSerum.highlighted());

		goToRitualSite = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_BOSS_ENTRY, new WorldPoint(1118, 3428, 0), "Go to the ritual site to the west, ready to fight the boss of the area.", combatGear);
		goToRitualSite.setLinePoints(Arrays.asList(
			new WorldPoint(1174, 3427, 0),
			new WorldPoint(1174, 3416, 0),
			new WorldPoint(1162, 3415, 0),
			new WorldPoint(1162, 3404, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1162, 3399, 0),
			new WorldPoint(1163, 3399, 0),
			new WorldPoint(1163, 3381, 0),
			new WorldPoint(1148, 3378, 0),
			new WorldPoint(1145, 3373, 0),
			new WorldPoint(1144, 3357, 0),
			new WorldPoint(1144, 3342, 0),
			/* Bridge to west */
			new WorldPoint(1126, 3344, 0),
			new WorldPoint(1116, 3344, 0),
			new WorldPoint(1115, 3355, 0),
			new WorldPoint(1109, 3356, 0),
			/* Bridge to north */
			new WorldPoint(1109, 3383, 0),
			new WorldPoint(1111, 3400, 0),
			new WorldPoint(1106, 3410, 0),
			new WorldPoint(1106, 3434, 0),
			new WorldPoint(1112, 3435, 0),
			new WorldPoint(1124, 3423, 0),
			new WorldPoint(0, 0, 0),
			/* From boat */
			new WorldPoint(1196, 3450, 0),
			new WorldPoint(1197, 3428, 0),
			new WorldPoint(1216, 3412, 0),
			new WorldPoint(1215, 3395, 0),
			new WorldPoint(1188, 3395, 0),
			new WorldPoint(1163, 3395, 0)
		));

		fightVardorvis = new NpcStep(getQuestHelper(), NpcID.VARDORVIS, new WorldPoint(1129, 3419, 0), "Defeat Vardorvis, who's weak to slash. Look at the sidebar for more details. Protect from Melee when he's not using a special attack.");
		((NpcStep) fightVardorvis).addAlternateNpcs(NpcID.VARDORVIS_QUEST, NpcID.VARDORVIS_CUTSCENE, NpcID.VARDORVIS_BASE_QUEST, NpcID.VARDORVIS_BASE_POSTQUEST);
		fightVardorvisSidebar = new DetailedQuestStep(getQuestHelper(), "Defeat Vardorvis. He's weak to slash weapons, and uses Melee. It's recommended to watch a video to get an understanding of his abilities.");
		fightVardorvisSidebar.addText("Swinging axes: He will spawn axes around the arena, which will go to the opposite corner to which they appear. Avoid them.");
		fightVardorvisSidebar.addText("Homing spikes: Vardorvis hits the ground, causing a spike to appear under you. Move off the tile to avoid.");
		fightVardorvisSidebar.addText("Head projectile: A tentacle will appear with a head on it, firing a green projectile. Protect from Missiles should be flicked to.");
		fightVardorvisSidebar.addText("Virus cells: Red splotches appear on the screen. Click them all within the time limit to avoid damage.");
		fightVardorvisSidebar.addSubSteps(fightVardorvis);

		pickUpTempleKey = new ItemStep(getQuestHelper(),  "Pick up the Temple Key in the Vardorvis arena.", templeKey);
		getTempleKeyFromRocks = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_BOSS_ENTRY, new WorldPoint(1118, 3428, 0),
			"Go to the ritual site to the west, and search the rocks to get another Temple Key.");

		// TODO: Leave area step
		// 48742, 1118, 3428, 0

		// TODO: Add shortcut as well
		// 48746, 1147, 3433, 0

		goToRitualSiteShortcut = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_BOSS_ENTRY, new WorldPoint(1118, 3428, 0),
				"Go to the ritual site to the west via the shortcut hole north-east of main pyramid, ready to fight the boss of the area.", combatGear);
		goToRitualSiteShortcut.setLinePoints(Arrays.asList(
				new WorldPoint(1196, 3450, 0),
				new WorldPoint(1189, 3451, 0),
				new WorldPoint(1186, 3453, 0),
				new WorldPoint(1178, 3451, 0),
				new WorldPoint(1178, 3448, 0),
				new WorldPoint(1184, 3448, 0),
				new WorldPoint(1184, 3442, 0),
				new WorldPoint(1159, 3442, 0),
				new WorldPoint(1151, 3445, 0),
				null,
				new WorldPoint(1144, 3434, 0),
				new WorldPoint(1129, 3437, 0),
				new WorldPoint(1120, 3438,0),
				new WorldPoint(1115, 3438,0),
				new WorldPoint(1117, 3428, 0)
		));
		goToRitualSite.addSubSteps(goToRitualSiteShortcut);

		DetailedQuestStep enterKasondeWithKey = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_TEMPLE_ENTRY, new WorldPoint(1174, 3428, 0),
			"");
		DetailedQuestStep giveKasondeKey =  new NpcStep(getQuestHelper(), NpcID.DT2_KASONDE_TEMPLE, new WorldPoint(1183, 9824, 0),
			"");
		returnToKasondeWithTempleKey = new ConditionalStep(getQuestHelper(), boardBoat,
			"Return to Kasonde with the temple key, who is inside the main pyramid of the area to the north. " +
				"Be ready for another fight.", combatGear, templeKey.hideConditioned(givenKasondeKey));
		returnToKasondeWithTempleKey.addStep(new Conditions(inStranglewoodPyramidRoom, templeKey.alsoCheckBank(questBank)), giveKasondeKey);
		returnToKasondeWithTempleKey.addStep(new Conditions(inAnyStranglewood, or(templeKey.alsoCheckBank(questBank), givenKasondeKey)), enterKasondeWithKey);
		returnToKasondeWithTempleKey.addStep(templeKeyNearby, pickUpTempleKey);
		returnToKasondeWithTempleKey.addStep(inAnyStranglewood, getTempleKeyFromRocks);

		NpcStep kasondeFight = new NpcStep(getQuestHelper(), NpcID.DT2_KASONDE_COMBAT, new WorldPoint(1183, 9824, 0),
			"");
		kasondeFight.addAlternateNpcs(NpcID.DT2_KASONDE_COMBAT_OPBREAK);
		defeatKasonde = new ConditionalStep(getQuestHelper(), boardBoat,
			"Defeat Kasonde in the pyramid. Avoid the potions he throws. If he throws his hands up, hide behind a pillar.", combatGear);
		defeatKasonde.addStep(new Conditions(inStranglewoodPyramidRoom), kasondeFight);
		defeatKasonde.addStep(new Conditions(inAnyStranglewood), enterKasondeWithKey);

		/* Post-fight Kasonde */
		NpcStep talkToKasondeAfterFight = new NpcStep(getQuestHelper(), NpcID.DT2_KASONDE_INJURED, new WorldPoint(1183, 9824, 0), "");

		goTalkToKasondeAfterFight = new ConditionalStep(getQuestHelper(), boardBoat,
			"Talk to Kasonde again in the pyramid.");
		goTalkToKasondeAfterFight.addStep(new Conditions(inStranglewoodPyramidRoom), talkToKasondeAfterFight);
		goTalkToKasondeAfterFight.addStep(new Conditions(inAnyStranglewood), enterKasondeWithKey);

		ObjectStep searchChestForVardorvisMedallion = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_MEDALLION_CHEST, new WorldPoint(1196, 3411, 0), "");
		/* Getting the medallion */
		ObjectStep leavePyramid = new ObjectStep(getQuestHelper(), ObjectID.DT2_STRANGLEWOOD_TEMPLE_EXIT, new WorldPoint(1183, 9809, 0), "");
		goGetVardorvisMedallion = new ConditionalStep(getQuestHelper(), boardBoat,
			"Search the north-east chest in Kasonde's initial hideout for a medallion.");
		goGetVardorvisMedallion.addStep(inStranglewoodPyramidRoom, leavePyramid);
		goGetVardorvisMedallion.addStep(new Conditions(inAnyStranglewood), searchChestForVardorvisMedallion);

		returnToDesertWithVardorvisMedallion = new ObjectStep(getQuestHelper(), ObjectID.DT2_DESERT_VAULT_DOOR,
			new WorldPoint(3511, 2971, 0), "Return to the Vault door north-east of Nardah.", vardorvisMedallion);
		returnToDesertWithVardorvisMedallion.addTeleport(nardahTeleport);

		useVardorvisMedallionOnStatue = new ObjectStep(getQuestHelper(), ObjectID.DT2_VAULT_VARDORVIS_STATUE, new WorldPoint(3942, 9636, 1),
			"Use the medallion on the north-east statue.", vardorvisMedallion.highlighted());
		useVardorvisMedallionOnStatue.addIcon(ItemID.DT2_MEDALLION_VARDORVIS);
	}

	public List<QuestStep> getDisplaySteps()
	{
		return QuestUtil.toArrayList(talkToElissa, talkToBarus, searchDesk, readPotionNote, drinkPotion, boardBoat,
			runIntoStanglewood, talkToKasonde, enterEntry, defendKasondeSidebar, leaveTowerDefenseRoom,
			talkToKasondeAfterTowerDefense, getHerbSidebar, getBerry, goDownToKasonde, defendKasondeHerb,
			talkToKasondeWithHerbAndBerry, addHerb, addBerry, drinkStranglewoodPotion, goToRitualSite, fightVardorvisSidebar,
			pickUpTempleKey, returnToKasondeWithTempleKey, defeatKasonde, goTalkToKasondeAfterFight,
			goGetVardorvisMedallion, returnToDesertWithVardorvisMedallion, useVardorvisMedallionOnStatue);
	}
}
