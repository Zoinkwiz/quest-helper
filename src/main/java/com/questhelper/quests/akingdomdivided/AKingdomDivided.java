/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.akingdomdivided;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.SpellbookRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.util.Spellbook;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;


@QuestDescriptor(
	quest = QuestHelperQuest.A_KINGDOM_DIVIDED
)

public class AKingdomDivided extends BasicQuestHelper
{
	QuestStep talkToMartinHolt, talkToCommanderFullore, talkToTomasLawry, goToLovakengjPub,
		talkToCabinBoyHerbert, fightJudgeofYama, talkToCommanderFullore2, talkToMartinHolt2,
		teleportArcheio, pickpocketIstoria, openRosesDiaryCase, talkToMartinHolt3, readRosesNote,
		talkToMartinHolt4, enterHomeForClueSearch, inspectReceipt, getReceipt, goDownCouncillorsHomeF3toF2,
		goDownCouncillorsHomeF2toF1, goUpCouncillorsHomeF1toF2, goUpCouncillorsHomeF2toF3, judgeOfYamaDetailedStep,
		enterJudgeOfYamaFightPortal, talkToMartinHoltForthosRuins, chopVines, squeezeThroughVines, checkPanel, solvePanelPuzzle,
		readRosesNote2, talkToMartinHoltForthosRuins2, talkToMartinHoltSettlementRuins, killAssassin, talkToMartinHoltSettlementRuins2,
		castFireSpellOnIce, searchIce, openSettlementRuinsPanel, readRosesNote3, talkToMartinHoltSettlementRuins3, talkToMartinHoltLeglessFaun,
		climbUpPillarLeglessFaun, checkLeglessFaunPanel, climbDownLeglessFaun, solveStatuesPuzzle, readRosesNote4, inspectCratesInShack, watchCutscene,
		enterLizardTemple, talkToKahtbalam, exitLizardTemple, goToEggArea, collectEgg, returntoKahtBalam, talkToKahtbalamAgain, openDoorNearKaht, openXamphurGate,
		openDoorNearKahtNoKey, fightXamphurSidebar, fightXamphur, searchTableAfterXamphur, returnToFulloreAgainSidebar, inspectCratesInShack2,
		talkToLordArceuusSidebar, talkToLordArceuus, climbTowerOfMagicStairs, talkToLordHosidius, talkToLadyLova, talkToLadyPiscSidebar, talkToLadyPisc, climbDownSewerLadyPisc,
		talkToLordShayzienSidebar, talkToLordShayzien, climbUpLadderLordShayzien, talkToFulloreXericsLookout, talkToAllMembersInXericsLookoutSidebar,
		talkToPiscLookout, talkToArceuusLookout, talkToLovaLookout, talkToShayzienLookout, talkToHosidiusLookout;

	Requirement hasTalkedToTomasLowry, hasBluishKey, inArceuusLibraryHistoricalArchive, inCouncillorsHouseF1, inCouncillorsHouseF2,
		inCouncillorsHouseF3, hasReceipt, hasInspectedReceipt, judgeOfYamaNearby, inPanelZone, assassinNearby,
		hasColdKey, inLeglessFaunF1, inLizardTemple, hasKahtEgg, inEggArea, xamphurNearby, xamphurGateNearby,
		inTowerOfMagic, inWarrens, inShayzienRoom, inLookoutBasement, inLookoutF0, inLookoutF1, inLookoutF2, inLookoutF3;

	ItemRequirement meleeCombatGear, food, freeInventorySlots, bluishKey, rosesDiary, rosesNote, receipt, kharedstsMemoirs, anyAxe, rosesNote2, combatGear, fireSpellGear,
		coldKey, rosesNote3, gamesNecklace, rosesNote4, fairyRingStaff, combatGearforXamphur, kahtEgg, dampKey;

	Zone arceuusLibraryHistoricalArchive, councillorsHouseF1, councillorsHouseF2, councillorsHouseF3, panelArea1, panelArea2, leglessFaunF1, lizardTemple, eggArea, towerOfMagic,
		warrens, shayzienRoom, lookoutBasement, lookoutF0, lookoutF1, lookoutF2, lookoutF3;

	QuestStep notYetImplemented;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupItemRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMartinHolt);
		steps.put(2, talkToMartinHolt);
		steps.put(4, talkToCommanderFullore);
		steps.put(6, talkToCommanderFullore);
		steps.put(8, talkToCommanderFullore);

		ConditionalStep homeSearch = new ConditionalStep(this, enterHomeForClueSearch);

		homeSearch.addStep(hasTalkedToTomasLowry, goToLovakengjPub);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF3, hasInspectedReceipt), goDownCouncillorsHomeF3toF2);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF2, hasInspectedReceipt), goDownCouncillorsHomeF2toF1);
		homeSearch.addStep(hasInspectedReceipt, talkToTomasLawry);

		homeSearch.addStep(new Conditions(inCouncillorsHouseF3, new Conditions(LogicType.NAND, hasReceipt)), goDownCouncillorsHomeF3toF2);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF1, new Conditions(LogicType.NAND, hasReceipt)), goUpCouncillorsHomeF1toF2);
		homeSearch.addStep(new Conditions(hasReceipt, new Conditions(LogicType.NAND, hasInspectedReceipt)), inspectReceipt);
		homeSearch.addStep(new Conditions(inCouncillorsHouseF2, new Conditions(LogicType.NAND, hasReceipt)), getReceipt);

		steps.put(10, homeSearch);
		steps.put(12, homeSearch);
		steps.put(14, talkToCabinBoyHerbert);

		ConditionalStep doTheFightWithYama = new ConditionalStep(this, fightJudgeofYama);
		doTheFightWithYama.addStep(new Conditions(LogicType.NAND, judgeOfYamaNearby), enterJudgeOfYamaFightPortal);

		steps.put(16, doTheFightWithYama);
		steps.put(18, doTheFightWithYama);
		// after yama fight
		steps.put(20, talkToCommanderFullore2);
		steps.put(22, talkToCommanderFullore2);
		steps.put(24, talkToCommanderFullore2);
		steps.put(26, talkToMartinHolt2);

		ConditionalStep gettingRosesDiary = new ConditionalStep(this, teleportArcheio);
		gettingRosesDiary.addStep(new Conditions(inArceuusLibraryHistoricalArchive, hasBluishKey), openRosesDiaryCase);
		gettingRosesDiary.addStep(inArceuusLibraryHistoricalArchive, pickpocketIstoria);

		steps.put(28, gettingRosesDiary);
		steps.put(30, gettingRosesDiary);
		steps.put(32, gettingRosesDiary);

		// after getting roses diary
		steps.put(34, talkToMartinHolt3);
		steps.put(36, talkToMartinHolt3);
		steps.put(38, readRosesNote);
		steps.put(40, talkToMartinHolt4);

		steps.put(42, talkToMartinHoltForthosRuins);
		steps.put(44, chopVines);

		steps.put(46, solvePanelPuzzle);
		steps.put(48, solvePanelPuzzle);
		steps.put(50, readRosesNote2);

		ConditionalStep talkToMartinHoltAgain = new ConditionalStep(this, talkToMartinHoltForthosRuins2);
		talkToMartinHoltAgain.addStep(new Conditions(inPanelZone), squeezeThroughVines);

		steps.put(52, talkToMartinHoltAgain);

		ConditionalStep talkToMartinHoltSettlementRuinFight = new ConditionalStep(this, talkToMartinHoltSettlementRuins);
		talkToMartinHoltSettlementRuinFight.addStep(assassinNearby, killAssassin);

		steps.put(54, talkToMartinHoltSettlementRuinFight);
		steps.put(56, talkToMartinHoltSettlementRuinFight);
		steps.put(58, talkToMartinHoltSettlementRuins2);
		steps.put(60, castFireSpellOnIce);

		ConditionalStep openSettlementRuinsPanelConditional = new ConditionalStep(this, searchIce);
		openSettlementRuinsPanelConditional.addStep(hasColdKey, openSettlementRuinsPanel);

		steps.put(62, openSettlementRuinsPanelConditional);
		steps.put(64, readRosesNote3);
		steps.put(66, talkToMartinHoltSettlementRuins3);
		steps.put(68, talkToMartinHoltLeglessFaun);

		steps.put(70, solveStatuesPuzzle);
		steps.put(72, solveStatuesPuzzle);

		ConditionalStep openLeglessFaunPanelConditional = new ConditionalStep(this, climbUpPillarLeglessFaun);
		openLeglessFaunPanelConditional.addStep(new Conditions(inLeglessFaunF1), checkLeglessFaunPanel);

		steps.put(74, openLeglessFaunPanelConditional);
		steps.put(76, readRosesNote4);

		steps.put(78, inspectCratesInShack);
		steps.put(80, watchCutscene);
		steps.put(82, watchCutscene);

		ConditionalStep talkToKaht = new ConditionalStep(this, enterLizardTemple);
		talkToKaht.addStep(new Conditions(inLizardTemple), talkToKahtbalam);

		steps.put(84, talkToKaht);
		steps.put(86, talkToKaht);

		ConditionalStep collectTheEgg = new ConditionalStep(this, goToEggArea);
		collectTheEgg.addStep(new Conditions(inEggArea), collectEgg);
		collectTheEgg.addStep(new Conditions(inLizardTemple), exitLizardTemple);

		steps.put(88, collectTheEgg);

		ConditionalStep returnToKaht = new ConditionalStep(this, returntoKahtBalam, kahtEgg);
		returnToKaht.addStep(new Conditions(LogicType.NAND, inLizardTemple), enterLizardTemple);
		returnToKaht.addStep(new Conditions(LogicType.NAND, hasKahtEgg), collectTheEgg);
		returnToKaht.addStep(new Conditions(inLizardTemple), talkToKahtbalam);

		steps.put(90, returnToKaht);
		steps.put(92, openDoorNearKaht);
		steps.put(94, openDoorNearKaht);

		ConditionalStep xamphurFightConditional = new ConditionalStep(this, fightXamphur);
		xamphurFightConditional.addStep(inLizardTemple, openDoorNearKahtNoKey);
		xamphurFightConditional.addStep(xamphurGateNearby, openXamphurGate);
		xamphurFightConditional.addStep(xamphurNearby, fightXamphur);
		xamphurFightConditional.addStep(new Conditions(LogicType.NAND, inLizardTemple), enterLizardTemple);

		steps.put(96, xamphurFightConditional);
		steps.put(98, xamphurFightConditional);
		steps.put(100, xamphurFightConditional);
		steps.put(102, openDoorNearKahtNoKey);
		steps.put(104, searchTableAfterXamphur);

		steps.put(106, inspectCratesInShack2);
		steps.put(108, watchCutscene);

		ConditionalStep talkingToArceuus = new ConditionalStep(this, climbTowerOfMagicStairs);
		talkingToArceuus.addStep(inTowerOfMagic, talkToLordArceuus);

		steps.put(110, talkingToArceuus);
		steps.put(112, talkToLordHosidius);
		steps.put(114, talkToLadyLova);

		ConditionalStep talkingToLadyPisc = new ConditionalStep(this, climbDownSewerLadyPisc);
		talkingToLadyPisc.addStep(inWarrens, talkToLadyPisc);

		steps.put(116, talkingToLadyPisc);

		ConditionalStep talkingToLordShayzien = new ConditionalStep(this, climbUpLadderLordShayzien);
		talkingToLordShayzien.addStep(inShayzienRoom, talkToLordShayzien);

		steps.put(118, talkingToLordShayzien);
		steps.put(120, talkToFulloreXericsLookout);
		steps.put(122, talkToFulloreXericsLookout);

//		ConditionalStep talkToAllLeadersLookout = new ConditionalStep(this, talkToArceuusLookout);

		steps.put(124, notYetImplemented);
		steps.put(126, notYetImplemented);
		steps.put(128, notYetImplemented);
		steps.put(130, notYetImplemented);


		return steps;
	}

	public void loadZones()
	{
		arceuusLibraryHistoricalArchive = new Zone(new WorldPoint(1535, 10236, 0), new WorldPoint(1571, 10213, 0));
		councillorsHouseF1 = new Zone(new WorldPoint(1670, 3684, 0), new WorldPoint(1683, 3677, 0));
		councillorsHouseF2 = new Zone(new WorldPoint(1670, 3684, 1), new WorldPoint(1683, 3677, 1));
		councillorsHouseF3 = new Zone(new WorldPoint(1670, 3684, 2), new WorldPoint(1683, 3677, 2));
		panelArea1 = new Zone(new WorldPoint(1670, 3577, 0), new WorldPoint(1671, 3576, 0));
		panelArea2 = new Zone(new WorldPoint(1669, 3581, 0), new WorldPoint(1672, 3578, 0));
		leglessFaunF1 = new Zone(new WorldPoint(1766, 3686, 1), new WorldPoint(1773, 3679, 1));
		lizardTemple = new Zone(new WorldPoint(1281, 10109, 0), new WorldPoint(1341, 10051, 0));
		eggArea = new Zone(new WorldPoint(1232, 3624, 0), new WorldPoint(1244, 3612, 0));
		towerOfMagic = new Zone(new WorldPoint(1563, 3802, 1), new WorldPoint(1595, 3836, 1));
		warrens = new Zone(new WorldPoint(1728, 10115, 0), new WorldPoint(1814, 10177, 0));
		shayzienRoom = new Zone(new WorldPoint(1480, 3640, 1), new WorldPoint(1489, 3631, 1));
		lookoutF3 = new Zone(new WorldPoint(1589, 3533, 3), new WorldPoint(1595, 3527, 3));
		lookoutF2 = new Zone(new WorldPoint(1589, 3533, 2), new WorldPoint(1595, 3527, 2));
		lookoutF1 = new Zone(new WorldPoint(1589, 3533, 1), new WorldPoint(1595, 3527, 1));
		lookoutF0 = new Zone(new WorldPoint(1576, 3536, 0), new WorldPoint(1599, 3524, 0));
		lookoutBasement = new Zone(new WorldPoint(1558, 9960, 0), new WorldPoint(1572, 9945, 0));
	}

	public void setupItemRequirements()
	{
		meleeCombatGear = new ItemRequirement("Melee combat gear", -1, -1);
		bluishKey = new ItemRequirement("Bluish Key", ItemID.BLUISH_KEY);
		rosesDiary = new ItemRequirement("Rose's diary", ItemID.ROSES_DIARY);
		rosesNote = new ItemRequirement("Rose's note", ItemID.ROSES_NOTE);
		receipt = new ItemRequirement("Receipt", ItemID.RECEIPT_25793);
		freeInventorySlots = new ItemRequirement("Free inventory slot", -1, 1);
		kharedstsMemoirs = new ItemRequirement("Kharedst's Memoirs for teleports", ItemID.KHAREDSTS_MEMOIRS);
		anyAxe = new ItemRequirement("Any axe", ItemCollections.getAxes());
		rosesNote2 = new ItemRequirement("Rose's note", ItemID.ROSES_NOTE_25806);
		combatGear = new ItemRequirement("Combat gear", -1, -1);
		fireSpellGear = new ItemRequirement("Runes/equipment to cast FIRE bolt or better", -1, 1);
		coldKey = new ItemRequirement("Cold key", ItemID.COLD_KEY);
		rosesNote3 = new ItemRequirement("Rose's note", ItemID.ROSES_NOTE_25807);
		food = new ItemRequirement("Decent food", -1, -1);
		gamesNecklace = new ItemRequirement("Games necklace for Wintertodt camp teleport", ItemCollections.getGamesNecklaces());
		rosesNote4 = new ItemRequirement("Rose's note", ItemID.ROSES_NOTE_25808);
		fairyRingStaff = new ItemRequirement("Staff for Fairy rings or a Skills Necklace", ItemCollections.getFairyStaff());
		fairyRingStaff.addAlternates(ItemCollections.getSkillsNecklaces());
		combatGearforXamphur = new ItemRequirement("Melee or range gear to fight Xamphur.", -1, -1);
		combatGearforXamphur.setTooltip("Xamphur is immune to magic attacks.");

		kahtEgg = new ItemRequirement("Lizardman Egg.", ItemID.LIZARDMAN_EGG);
		kahtEgg.setTooltip("Received during quest.");
		dampKey = new ItemRequirement("Damp Key.", ItemID.DAMP_KEY);
		dampKey.setTooltip("Received during quest.");

	}

	public void setupConditions()
	{
		// after speaking with tomas lawry
		// 12302: 0 > 1 (I'm guessing its this one)
		// 12144: 2 > 0
		hasTalkedToTomasLowry = new VarbitRequirement(12302, 1);

		inArceuusLibraryHistoricalArchive = new ZoneRequirement(arceuusLibraryHistoricalArchive);
		judgeOfYamaNearby = new NpcCondition(NpcID.JUDGE_OF_YAMA_10938);
		hasBluishKey = new ItemRequirements(bluishKey);
		inCouncillorsHouseF1 = new ZoneRequirement(councillorsHouseF1);
		inCouncillorsHouseF2 = new ZoneRequirement(councillorsHouseF2);
		inCouncillorsHouseF3 = new ZoneRequirement(councillorsHouseF3);
		hasReceipt = new ItemRequirements(receipt);
		hasInspectedReceipt = new VarbitRequirement(12298, 1);
		inPanelZone = new ZoneRequirement(panelArea1, panelArea2);
		assassinNearby = new NpcCondition(NpcID.ASSASSIN_10940);
		hasColdKey = new ItemRequirements(coldKey);
		inLeglessFaunF1 = new ZoneRequirement(leglessFaunF1);
		inLizardTemple = new ZoneRequirement(lizardTemple);
		hasKahtEgg = new ItemRequirements(kahtEgg);
		inEggArea = new ZoneRequirement(eggArea);
		xamphurGateNearby = new ObjectCondition(ObjectID.GATE_41877);
		xamphurNearby = new NpcCondition(NpcID.XAMPHUR_10955);
		inTowerOfMagic = new ZoneRequirement(towerOfMagic);
		inWarrens = new ZoneRequirement(warrens);
		inShayzienRoom = new ZoneRequirement(shayzienRoom);
		inLookoutBasement = new ZoneRequirement(lookoutBasement);
		inLookoutF0 = new ZoneRequirement(lookoutF0);
		inLookoutF1 = new ZoneRequirement(lookoutF1);
		inLookoutF2 = new ZoneRequirement(lookoutF2);
		inLookoutF3 = new ZoneRequirement(lookoutF3);

		// lady pisc 12318: 0 > 2
	}

	public void setupSteps()
	{
		notYetImplemented = new DetailedQuestStep(this, "Quest step not yet implemented.");

		talkToMartinHolt = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt east of Kourend Castle.");
		talkToMartinHolt.addDialogStep("Yes.");
		talkToCommanderFullore = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1614, 3668, 0), "Talk to Commander Fullore in Kourend Castle.");
		talkToCommanderFullore.addDialogStep("Let's get going.");

		goUpCouncillorsHomeF1toF2 = new ObjectStep(this, ObjectID.STAIRCASE_11796, new WorldPoint(1671, 3681, 0), "Climb up the stairs of the Councillors home.");
		goUpCouncillorsHomeF2toF3 = new ObjectStep(this, ObjectID.STAIRCASE_41806, new WorldPoint(1676, 3679, 1), "Climb up the stairs of the Councillors home.");
		goDownCouncillorsHomeF2toF1 = new ObjectStep(this, ObjectID.STAIRCASE_11799, new WorldPoint(1671, 3681, 1), "Climb down the stairs of the Councillors home.");
		goDownCouncillorsHomeF3toF2 = new ObjectStep(this, ObjectID.STAIRCASE_11793, new WorldPoint(1676, 3679, 2), "Climb down the stairs of the Councillors home.");

		getReceipt = new ObjectStep(this, ObjectID.DRAWERS_41795, new WorldPoint(1679, 3680, 1), "Search the drawers for the Receipt.");
		inspectReceipt = new DetailedQuestStep(this, "Inspect the Receipt.", receipt.highlighted());

		talkToTomasLawry = new NpcStep(this, NpcID.TOMAS_LAWRY, new WorldPoint(1677, 3682, 0), "Speak with Tomas Lawry on the ground floor of the Councillors home.");
		talkToTomasLawry.addDialogStep("I've found something that might be useful.");
		goToLovakengjPub = new NpcStep(this, NpcID.FUGGY, new WorldPoint(1569, 3758, 0), "Talk to Fuggy in the pub in southeast Lovakengj.");
		goToLovakengjPub.addDialogStep("Had any councillors stay here recently?");

		enterHomeForClueSearch = new DetailedQuestStep(this, new WorldPoint(1676, 3680, 0), "Searcg the Councillors home east of Kourend castle for clues.");
		enterHomeForClueSearch.addSubSteps(getReceipt, inspectReceipt, goDownCouncillorsHomeF2toF1,
			goDownCouncillorsHomeF3toF2, goUpCouncillorsHomeF1toF2, goUpCouncillorsHomeF2toF3);

		talkToCabinBoyHerbert = new NpcStep(this, NpcID.CABIN_BOY_HERBERT, new WorldPoint(1826, 3691, 0),
			"Prepare to fight the Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the " +
				"gaps of the fire waves to approach the boss.\n\nTalk to Cabin Boy Herbert next to Veos's ship in Port" +
				"Piscarilius to initiate the fight.",
			meleeCombatGear, food);

		talkToCabinBoyHerbert.addDialogStep("I'm looking for a councillor.");

		fightJudgeofYama = new NpcStep(this, NpcID.JUDGE_OF_YAMA_10938,
			"Fight the Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the gaps of " +
				"the fire waves to approach the boss.", meleeCombatGear, food);

		enterJudgeOfYamaFightPortal = new ObjectStep(this, ObjectID.PORTAL_41808, new WorldPoint(1823, 3686, 0),
			"Prepare to fight the Judge of Yama. This boss uses magic + range prayer so melee is required. Run in the " +
				"gaps of the fire waves to approach the boss.\n\nEnter the Portal near Veos's ship to fight the Judge of Yama.",
			meleeCombatGear, food);

		judgeOfYamaDetailedStep = new DetailedQuestStep(this, "Fight the Judge of Yama. This boss uses magic + range " +
			"prayer so melee is required. Run in the gaps of the fire waves to approach the boss.", meleeCombatGear, food);

		judgeOfYamaDetailedStep.addSubSteps(talkToCabinBoyHerbert, fightJudgeofYama, enterJudgeOfYamaFightPortal);

		talkToCommanderFullore2 = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1604, 3654, 0), "Talk to Commander Fullore just south of the Kourend Castle.");
		talkToMartinHolt2 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again east of the Kourend Castle.");

		teleportArcheio = new NpcStep(this, NpcID.ARCHEIO, new WorldPoint(1625, 3808, 0), "Have Archeio in the Arceuus library teleport you to the Historical Archives.");
		pickpocketIstoria = new NpcStep(this, NpcID.ISTORIA_11113, new WorldPoint(1551, 10222, 0), "Pickpocket Istoria for the bluish key.");
		openRosesDiaryCase = new ObjectStep(this, ObjectID.DISPLAY_CASE_41811, "Search the display case in the south east corner of the room to get Rose's diary", bluishKey);

		talkToMartinHolt3 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again east of the Kourend Castle.", rosesDiary);
		readRosesNote = new DetailedQuestStep(this, "Read Rose's note that fell out of the diary.", rosesNote.highlighted());
		talkToMartinHolt4 = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1664, 3670, 0), "Talk to Martin Holt again east of the Kourend Castle.", rosesNote);

		talkToMartinHoltForthosRuins = new NpcStep(this, NpcID.MARTIN_HOLT, new WorldPoint(1673, 3580, 0), "Talk to Martin Holt on the north side of the Forthos Ruins.");
		chopVines = new ObjectStep(this, ObjectID.VINES_41815, new WorldPoint(1671, 3577, 0), "Chop the vines south of Martin Holt.", anyAxe);
		squeezeThroughVines = new ObjectStep(this, ObjectID.VINES_41816, new WorldPoint(1671, 3577, 0), "Squeeze through the vines.");
		checkPanel = new ObjectStep(this, ObjectID.PANEL_41822, new WorldPoint(1672, 3579, 0), "Check the panel on the wall.");
		solvePanelPuzzle = new StonePuzzleStep(this);
		readRosesNote2 = new DetailedQuestStep(this, "Read Rose's note from the panel on the wall.", rosesNote2.highlighted());
		talkToMartinHoltForthosRuins2 = new NpcStep(this, NpcID.MARTIN_HOLT_10891, new WorldPoint(1673, 3580, 0), "Talk to Martin Holt again on the north side of the Forthos Ruins.");
		talkToMartinHoltForthosRuins2.addSubSteps(squeezeThroughVines);
		talkToMartinHoltSettlementRuins = new NpcStep(this, NpcID.MARTIN_HOLT_10891, new WorldPoint(1545, 3895, 0), "Talk to Martin Holt again in the Settlement Ruins south west of the Wintertodt camp. " +
			"Be prepared to fight a level 132 assassin who uses a dragon dagger and dragon darts.", combatGear, food);
		killAssassin = new NpcStep(this, NpcID.ASSASSIN_10940, "Kill the Assassin. He uses a dragon dagger and dragon darts.", combatGear, food);
		talkToMartinHoltSettlementRuins.addSubSteps(killAssassin);
		talkToMartinHoltSettlementRuins2 = new NpcStep(this, NpcID.MARTIN_HOLT_10891, new WorldPoint(1545, 3895, 0), "Talk to Martin Holt again in the Settlement Ruins south west of the Wintertodt camp.");
		castFireSpellOnIce = new NpcStep(this, NpcID.ICE_CHUNKS_11029, new WorldPoint(1541, 3886, 0), "Cast FIRE bolt or stronger on the ice chunks south west of Martin Holt in the Settlement Ruins.", fireSpellGear);
		castFireSpellOnIce.addIcon(ItemID.FIRE_RUNE);
		searchIce = new NpcStep(this, NpcID.ICE_CHUNKS_11030, new WorldPoint(1541, 3886, 0), "Search the melted ice chunks to get the Cold key.");
		openSettlementRuinsPanel = new ObjectStep(this, ObjectID.PANEL_41829, new WorldPoint(1543, 3892, 0), "Open the panel on the wall in the Settlement Ruins.");
		readRosesNote3 = new DetailedQuestStep(this, "Read Rose's note from the panel on the wall.", rosesNote3.highlighted());
		talkToMartinHoltSettlementRuins3 = new NpcStep(this, NpcID.MARTIN_HOLT_10891, new WorldPoint(1545, 3895, 0), "Talk to Martin Holt again.");
		talkToMartinHoltLeglessFaun = new NpcStep(this, NpcID.MARTIN_HOLT_10891, new WorldPoint(1775, 3681, 0), "Talk to Martin Holt at the Legless Faun pub in the south west corner of Port Piscarilius.");
		climbUpPillarLeglessFaun = new ObjectStep(this, ObjectID.PILLAR_41836, new WorldPoint(1772, 3680, 0), "Climb up the pillar west of Martin Holt again.");
		checkLeglessFaunPanel = new ObjectStep(this, ObjectID.PANEL_41834, new WorldPoint(1768, 3686, 1), "Check the panel on the wall again.");
		checkLeglessFaunPanel.addSubSteps(climbUpPillarLeglessFaun);
		climbDownLeglessFaun = new ObjectStep(this, ObjectID.WALL_41839, new WorldPoint(1772, 3679, 1), "Climb down the wall.");
		solveStatuesPuzzle = new StatuePuzzle(this);
		readRosesNote4 = new DetailedQuestStep(this, "Read Rose's note from the panel on the wall.", rosesNote4.highlighted());

		inspectCratesInShack = new ObjectStep(this, ObjectID.CRATES_41851, new WorldPoint(1281, 3763, 0), "Inspect the crates in the north west corner of the shack located north east of the farming guild.  Use fairy ring CIR or a skill necklace to get there quickly. " +
			"If the crates do not have an inspect option, search the bed in the shack and read the note you find.", fairyRingStaff);
		inspectCratesInShack.addDialogSteps("Climb through it.");
		watchCutscene = new DetailedQuestStep(this, "Watch the cutscene.");
		enterLizardTemple = new ObjectStep(this, ObjectID.LIZARD_DWELLING, new WorldPoint(1292, 3657, 0), "Enter the Lizard Dwelling south east of the Farming guild.");
		talkToKahtbalam = new NpcStep(this, NpcID.KAHT_BALAM, new WorldPoint(1330, 10084, 0), "Talk to Kaht B'alam located on the east side of the middle tunnel.");
		talkToKahtbalam.addDialogSteps("Do you think you could help me find a mage?");
		exitLizardTemple = new ObjectStep(this, ObjectID.STRANGE_HOLE, new WorldPoint(1292, 10077, 0), "Exit the Lizard Temple by jumping down the hole to the west.");
		goToEggArea = new DetailedQuestStep(this, new WorldPoint(1238, 3618, 0), "Search the Lizardman eggs until you find Kahts egg. You will be attacked by a level 75 lizardman but you do not need to kill it.");
		collectEgg = new ObjectStep(this, ObjectID.LIZARDMAN_EGGS_41874, "Search the Lizardman eggs until you find Kahts egg. You will be attacked by a level 75 lizardman but you do not need to kill it.");
		((ObjectStep) collectEgg).setHideWorldArrow(true);
		((ObjectStep) collectEgg).addAlternateObjects(ObjectID.LIZARDMAN_EGGS_41876);
		((ObjectStep) collectEgg).setWorldMapPoint(new WorldPoint(1238, 3618, 0));

		collectEgg.addSubSteps(exitLizardTemple);
		collectEgg.addSubSteps(goToEggArea);

		returntoKahtBalam = new DetailedQuestStep(this, "Return to Kaht B'alam with the Lizardman egg.");
		talkToKahtbalamAgain = new NpcStep(this, NpcID.KAHT_BALAM, new WorldPoint(1330, 10084, 0), "Talk to Kaht B'alam located on the east side of the middle tunnel.");
		talkToKahtbalamAgain.addDialogSteps("So, about the key to that door...");

		returntoKahtBalam.addSubSteps(collectEgg, talkToKahtbalamAgain, enterLizardTemple);
		openDoorNearKaht = new ObjectStep(this, ObjectID.DOOR_41870, new WorldPoint(1323, 10080, 0), "Open the door near Kaht B'alam with the damp key", dampKey);
		openDoorNearKaht.addDialogSteps("Yes.");

		openDoorNearKahtNoKey = new ObjectStep(this, ObjectID.DOORWAY_41871, new WorldPoint(1323, 10080, 0), "Enter the door near Kaht B'alam.");
		openDoorNearKahtNoKey.addDialogSteps("Yes.");
		openXamphurGate = new ObjectStep(this, ObjectID.GATE_41877, "Open the gate once you are ready to fight.", combatGearforXamphur);

		fightXamphurSidebar = new DetailedQuestStep(this, "Fight Xamphur.", combatGearforXamphur);
		fightXamphurSidebar.addText("Xamphur uses several special mechanics. It is recommended to read or watch a guide on the fight.");
		fightXamphur = new NpcStep(this, NpcID.XAMPHUR_10955, "Fight Xamphur.", combatGearforXamphur);
		fightXamphurSidebar.addSubSteps(openXamphurGate, openDoorNearKahtNoKey, fightXamphur, enterLizardTemple);

		searchTableAfterXamphur = new ObjectStep(this, ObjectID.TABLE_41880, "Search the northern table to find some notes.");
		returnToFulloreAgainSidebar = new DetailedQuestStep(this, "Return to Commandore Fullore in the shack basement.");
		inspectCratesInShack2 = new ObjectStep(this, ObjectID.CRATES_41851, new WorldPoint(1281, 3763, 0), "Inspect the crates in the north west corner of the shack located north east of the farming guild.  " +
			"Use fairy ring CIR or a skill necklace to get there quickly. ", fairyRingStaff);
		returnToFulloreAgainSidebar.addSubSteps(inspectCratesInShack2);

		talkToLordArceuusSidebar = new NpcStep(this, NpcID.LORD_TROBIN_ARCEUUS_8505, "Speak to Lord Arceuus in the Tower of Magic, north west of Arceuus.", kharedstsMemoirs);
		talkToLordArceuus = new NpcStep(this, NpcID.LORD_TROBIN_ARCEUUS_8505, "Speak to Lord Arceuus in the Tower of Magic, north west of Arceuus.", kharedstsMemoirs);
		climbTowerOfMagicStairs = new ObjectStep(this, ObjectID.STAIRS_33575, new WorldPoint(1585, 3821, 0), "Climb up the stairs in the Tower of Magic.", kharedstsMemoirs);
		talkToLordArceuusSidebar.addSubSteps(talkToLordArceuus, climbTowerOfMagicStairs);

		talkToLordHosidius = new NpcStep(this, NpcID.LORD_KANDUR_HOSIDIUS_11033, new WorldPoint(1782, 3572, 0), "Speak to Lord Hosidius in his home on the south east side of Hosidius.", kharedstsMemoirs);
		talkToLadyLova = new NpcStep(this, NpcID.LADY_VULCANA_LOVAKENGJ_11035, new WorldPoint(1484, 3748, 0), "Speak to Lady Lovakengj in the Lovakengj Assembly.", kharedstsMemoirs);

		talkToLadyPiscSidebar = new NpcStep(this, NpcID.LADY_SHAUNA_PISCARILIUS, "Speak to Lady Piscarilius in the sewers under Port Piscarilius.", kharedstsMemoirs);
		talkToLadyPisc = new NpcStep(this, NpcID.LADY_SHAUNA_PISCARILIUS, new WorldPoint(1764, 10158, 0), "Speak to Lady Piscarilius in the sewers under Port Piscarilius.", kharedstsMemoirs);
		climbDownSewerLadyPisc = new ObjectStep(this, ObjectID.MANHOLE_31706, new WorldPoint(1813, 3745, 0), "Climb down the man hole in Port Piscarilius.", kharedstsMemoirs);
		((ObjectStep) climbDownSewerLadyPisc).addAlternateObjects(ObjectID.MANHOLE_31707);
		talkToLadyPiscSidebar.addSubSteps(climbDownSewerLadyPisc, talkToLadyPisc);

		talkToLordShayzienSidebar = new NpcStep(this, NpcID.LORD_SHIRO_SHAYZIEN_11038, "Speak to Lord Shayzien upstairs of the War Tent in the Shayzien Encampment.", kharedstsMemoirs);
		talkToLordShayzien = new NpcStep(this, NpcID.LORD_SHIRO_SHAYZIEN_11038, new WorldPoint(1484, 3634, 1), "Speak to Lord Shayzien upstairs of the War Tent in the Shayzien Encampment.", kharedstsMemoirs);
		climbUpLadderLordShayzien = new ObjectStep(this, ObjectID.LADDER_42207, new WorldPoint(1481, 3633, 0), "Climb up the ladder in the War Tent in the Shayzien Encampment.", kharedstsMemoirs);
		talkToLordShayzienSidebar.addSubSteps(talkToLordShayzien, climbUpLadderLordShayzien);

		talkToFulloreXericsLookout = new NpcStep(this, NpcID.COMMANDER_FULLORE, new WorldPoint(1591, 3528, 0), "Speak to Commander Fullore at Xeric's Lookout located south east of Shayzien.");

		talkToAllMembersInXericsLookoutSidebar = new DetailedQuestStep(this, "Speak with all the leaders again on each floor of Xeric's Lookout.");
		talkToArceuusLookout = new NpcStep(this, NpcID.LORD_TROBIN_ARCEUUS_10962, new WorldPoint(1579, 3528, 0), "Talk to Lord Arceeus in Xeric's Lookout.");

	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(anyAxe, freeInventorySlots, meleeCombatGear, combatGear, food);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(new ItemRequirement("Kharedst's Memoirs for teleports", ItemID.KHAREDSTS_MEMOIRS), fairyRingStaff);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		ArrayList<String> reqs = new ArrayList<>();
		reqs.add("Judge of Yama (level 168)");
		reqs.add("Assassin (level 132)");
		reqs.add("Xamphur (level 239)");
		return reqs;
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 54));
		reqs.add(new SkillRequirement(Skill.THIEVING, 52));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 52));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 50));
		reqs.add(new SkillRequirement(Skill.MINING, 42));
		reqs.add(new SkillRequirement(Skill.CRAFTING, 38));
		reqs.add(new SkillRequirement(Skill.MAGIC, 35));

		reqs.add(new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_QUEEN_OF_THIEVES, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_DEPTHS_OF_DESPAIR, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_ASCENT_OF_ARCEUUS, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.THE_FORSAKEN_TOWER, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.TALE_OF_THE_RIGHTEOUS, QuestState.FINISHED));
		reqs.add(new QuestRequirement(QuestHelperQuest.ARCHITECTURAL_ALLIANCE, QuestState.FINISHED));
		reqs.add(new SpellbookRequirement(Spellbook.NORMAL));

		return reqs;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("The Disgraced Councillor's Escape", Arrays.asList(talkToMartinHolt, talkToCommanderFullore, enterHomeForClueSearch,
			talkToTomasLawry, goToLovakengjPub, judgeOfYamaDetailedStep, talkToCommanderFullore2, talkToMartinHolt2, teleportArcheio, pickpocketIstoria,
			openRosesDiaryCase, talkToMartinHolt3, readRosesNote, talkToMartinHolt4), meleeCombatGear, food, kharedstsMemoirs, freeInventorySlots));

		allSteps.add(new PanelDetails("Kourend's Last Princess", Arrays.asList(talkToMartinHoltForthosRuins, chopVines, solvePanelPuzzle,
			readRosesNote2, talkToMartinHoltForthosRuins2, talkToMartinHoltSettlementRuins, killAssassin, talkToMartinHoltSettlementRuins2, castFireSpellOnIce, searchIce,
			openSettlementRuinsPanel, readRosesNote3, talkToMartinHoltSettlementRuins3, talkToMartinHoltLeglessFaun, solveStatuesPuzzle,
			checkLeglessFaunPanel, readRosesNote4, inspectCratesInShack), anyAxe, fireSpellGear, food, kharedstsMemoirs, gamesNecklace, fairyRingStaff));

		allSteps.add(new PanelDetails("The Mysterious Mage", Arrays.asList(enterLizardTemple, talkToKahtbalam, collectEgg, returntoKahtBalam, openDoorNearKaht, fightXamphurSidebar, returnToFulloreAgainSidebar),
			fairyRingStaff, combatGearforXamphur, food, dampKey, kahtEgg));

		allSteps.add(new PanelDetails("The Houses of Kourend", Arrays.asList(talkToLordArceuusSidebar, talkToLordHosidius, talkToLadyLova, talkToLadyPiscSidebar, talkToLordShayzienSidebar, talkToFulloreXericsLookout,
			talkToAllMembersInXericsLookoutSidebar),
			kharedstsMemoirs));

		return allSteps;
	}
}
