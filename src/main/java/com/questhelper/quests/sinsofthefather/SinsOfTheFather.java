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
package com.questhelper.quests.sinsofthefather;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.item.ItemRequirements;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.requirements.quest.QuestRequirement;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.player.InInstanceRequirement;
import com.questhelper.requirements.var.VarbitRequirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.widget.WidgetTextRequirement;
import com.questhelper.rewards.ItemReward;
import com.questhelper.rewards.QuestPointReward;
import com.questhelper.rewards.UnlockReward;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.conditional.NpcCondition;
import com.questhelper.requirements.conditional.ObjectCondition;
import com.questhelper.requirements.util.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;

@QuestDescriptor(
        quest = QuestHelperQuest.SINS_OF_THE_FATHER
)
public class SinsOfTheFather extends BasicQuestHelper
{
	//Items Required
	ItemRequirement haemBook, unscentedTop, unscentedLegs, unscentedShoes, vyreTop, vyreLegs, vyreShoes, ivandisFlailEquipped, blisterwoodFlail, scentedTop, scentedLegs,
			scentedShoes, blisterwood8, axe, knife, vine3, longVine, log1, log2, log3, axeEquipped, oldNote, ruby, sickle, enchantedSickle, enchantRubyRunesOrTablet, fireRune5,
			fireStaff, cosmicRune, enchantRunes, enchantTablet, rubySickle, blisterwoodLog, blisterwoodSickle, ivandisFlailHighlighted, chisel, rubyHighlighted, ivandisFlail,
			combatGear, rangedWeaponForBloodveld, vyrewatchOutfitOrCoins;

	//Items Recommended
	ItemRequirement antipoison, pickaxe, drakanMedallion, moryLegs3;

	QuestStep startQuest, talkToHameln, talkToCarl, inspectBarrel, followCarl, killKroy, destroyLab, talkToVeliafAfterKroy, talkToVeliafInPater,
		talkToIvan, listenToMeeting, talkToIvanAfterMeeting, talkToIvanAfterTrek, talkToVeliafInBoatHouse, travelToGraveyard, talkToVeliafInGraveyard, talkToVanescula,
		talkToVanesculaAfterPuzzle, talkToVanesculaAfterTeam, talkToSafalaanInLab, killBloodveld, talkToSafalaanInDeepLab, searchLabBookcase,
		takeBookToSafalaan, talkToVanesculaAfterLab, talkToPolmafi, talkToPolmafiMore, bringUnscentedToVanescula, talkToVeliafForFight, killDamien, talkToVeliafAfterDamien,
		talkToVanesculaAfterDamien, enterDarkmeyer, talkToDesmodus, talkToMordan, talkToMaria, talkToDesmodusAgain, getNote, bringVanesculaLogs, bringVertidaLogs,
		talkToVertidaForFlail, talkToVanesculaWithFlail, talkToSafalaanWithFlail, talkToVanesculaBeforeFight, talkToVanesculaForFight, talkToVeliafAfterFight,
		finishQuest, templeTrek, talkToTeamSteps, valveStep, createFlailSteps, doDoorPuzzle, cutLogs, goDownToKroy, destroyLab2, enterPater, killJuvinates,
		leaveJuvinateArea, openPuzzleDoor, goToLab, enterDeepLab, goDownToPolmafi, goDownToPolmafiNoItems, talkToVeliafForFinalFight, readNote,
		goDownToVerditaWithLogs, fightVanstrom, fightVanstromSidebar, enterBaseToFinish;

	DetailedQuestStep searchForKnife, cutVines, combineVines, useVineOnBranch, swingOnVine, leaveSwingArea, killNailBeasts, leaveNailBeastArea, killZombieForAxe,
		get3LogsForBridge, repairBridge1, repairBridge2, repairBridge3, crossBridge, leaveBridgeArea;

	DetailedQuestStep convinceKael, convinceVertida, convincePolmafi, convinceRadigad, convinceIvan, convinceVeliaf;

	DetailedQuestStep goDownToMakeFlail, getSickle, addRubyToSickle, enchantRubySickle, useLogOnSickle, useFlailOnSickle;

	Requirement inFollowingCarlArea, inKroyArea, destroyedLabTable1, inPater, inSwingArea, inSwingExitArea, vineAdded, inNailBeastArea,
		inBridgeArea, nailBeastNearby, hasRepairedBridge1, hasRepairedBridge2, hasRepairedBridge3, inBridgeExitArea, inJuvinateArea,
		juvinateNearby, inPuzzleInterface, talkedToKael, talkedToVertida, talkedToPolmafi, talkedToRadigad, talkedToIvan, inLab, inDeepLab,
		inNewBase, inDamienRoom, inFinalFightArea;

	//Zones
	Zone followingCarlArea, kroyArea, pater, swingArea, swingExitArea, nailBeastArea, bridgeArea, bridgeExitArea, juvinateArea, lab, newBase, damienRoom, finalFightArea;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();
		setupZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		steps.put(0, startQuest);
		steps.put(2, startQuest);
		steps.put(4, talkToHameln);
		steps.put(6, talkToCarl);

		steps.put(8, inspectBarrel);
		steps.put(10, inspectBarrel);

		ConditionalStep followCarlSteps = new ConditionalStep(this, inspectBarrel);
		followCarlSteps.addStep(inFollowingCarlArea, followCarl);
		steps.put(12, followCarlSteps);

		ConditionalStep goKillCarl = new ConditionalStep(this, goDownToKroy);
		goKillCarl.addStep(inKroyArea, killKroy);

		steps.put(14, goKillCarl);
		steps.put(16, goKillCarl);

		ConditionalStep goDestroyTables = new ConditionalStep(this, goDownToKroy);
		goDestroyTables.addStep(new Conditions(inKroyArea, destroyedLabTable1), destroyLab2);
		goDestroyTables.addStep(inKroyArea, destroyLab);

		steps.put(18, goDestroyTables);

		steps.put(20, talkToVeliafAfterKroy);


		ConditionalStep goToVeliafInPater = new ConditionalStep(this, enterPater);
		goToVeliafInPater.addStep(inPater, talkToVeliafInPater);
		steps.put(22, goToVeliafInPater);
		steps.put(24, goToVeliafInPater);
		steps.put(26, goToVeliafInPater);

		steps.put(28, talkToIvan);

		steps.put(30, listenToMeeting);
		steps.put(32, listenToMeeting);

		steps.put(34, talkToIvanAfterMeeting);

		ConditionalStep goTempleTrekking = new ConditionalStep(this, talkToIvanAfterMeeting);
		goTempleTrekking.addStep(new Conditions(inJuvinateArea, juvinateNearby), killJuvinates);
		goTempleTrekking.addStep(new Conditions(inJuvinateArea), leaveJuvinateArea);
		goTempleTrekking.addStep(new Conditions(inBridgeExitArea), leaveBridgeArea);
		goTempleTrekking.addStep(new Conditions(inBridgeArea, hasRepairedBridge3), crossBridge);
		goTempleTrekking.addStep(new Conditions(inBridgeArea, log1, hasRepairedBridge2), repairBridge3);
		goTempleTrekking.addStep(new Conditions(inBridgeArea, log2, hasRepairedBridge1), repairBridge2);
		goTempleTrekking.addStep(new Conditions(inBridgeArea, log3), repairBridge1);
		goTempleTrekking.addStep(new Conditions(inBridgeArea, axe), get3LogsForBridge);
		goTempleTrekking.addStep(new Conditions(inBridgeArea), killZombieForAxe);
		goTempleTrekking.addStep(new Conditions(inNailBeastArea, nailBeastNearby), killNailBeasts);
		goTempleTrekking.addStep(new Conditions(inNailBeastArea), leaveNailBeastArea);
		goTempleTrekking.addStep(new Conditions(inSwingExitArea), leaveSwingArea);
		goTempleTrekking.addStep(new Conditions(inSwingArea, vineAdded), swingOnVine);
		goTempleTrekking.addStep(new Conditions(inSwingArea, longVine), useVineOnBranch);
		goTempleTrekking.addStep(new Conditions(inSwingArea, vine3), combineVines);
		goTempleTrekking.addStep(new Conditions(inSwingArea, knife), cutVines);
		goTempleTrekking.addStep(inSwingArea, searchForKnife);
		steps.put(36, goTempleTrekking);

		steps.put(38, talkToIvanAfterTrek);

		steps.put(40, talkToVeliafInBoatHouse);

		steps.put(42, travelToGraveyard);

		steps.put(44, talkToVeliafInGraveyard);

		steps.put(46, talkToVeliafInGraveyard);

		steps.put(48, talkToVanescula);

		ConditionalStep goSolveDoor = new ConditionalStep(this, openPuzzleDoor);
		goSolveDoor.addStep(inPuzzleInterface, doDoorPuzzle);
		steps.put(50, goSolveDoor);

		steps.put(52, talkToVanesculaAfterPuzzle);


		ConditionalStep convinceTheTeam = new ConditionalStep(this, convinceVertida);
		convinceTheTeam.addStep(talkedToIvan, convinceVeliaf);
		convinceTheTeam.addStep(talkedToPolmafi, convinceIvan);
		convinceTheTeam.addStep(talkedToRadigad, convincePolmafi);
		convinceTheTeam.addStep(talkedToKael, convinceRadigad);
		convinceTheTeam.addStep(talkedToVertida, convinceKael);
		steps.put(54, convinceTheTeam);

		steps.put(56, talkToVanesculaAfterTeam);

		ConditionalStep goToLabSteps = new ConditionalStep(this, goToLab);
		goToLabSteps.addStep(inLab, talkToSafalaanInLab);
		steps.put(58, goToLabSteps);
		steps.put(60, goToLabSteps);

		ConditionalStep defeatBloodveld = new ConditionalStep(this, goToLab);
		defeatBloodveld.addStep(inDeepLab, killBloodveld);
		defeatBloodveld.addStep(inLab, enterDeepLab);
		steps.put(62, defeatBloodveld);

		ConditionalStep talkInDeepLabs = new ConditionalStep(this, goToLab);
		talkInDeepLabs.addStep(inLab, talkToSafalaanInDeepLab);
		steps.put(64, talkInDeepLabs);

		ConditionalStep getBookSteps = new ConditionalStep(this, goToLab);
		getBookSteps.addStep(inLab, searchLabBookcase);
		steps.put(66, getBookSteps);

		ConditionalStep bringBookToSafalaanSteps = new ConditionalStep(this, goToLab);
		bringBookToSafalaanSteps.addStep(inLab, takeBookToSafalaan);
		steps.put(68, bringBookToSafalaanSteps);

		steps.put(70, talkToVanesculaAfterLab);

		ConditionalStep bringPolmafiItems = new ConditionalStep(this, goDownToPolmafi);
		bringPolmafiItems.addStep(inNewBase, talkToPolmafi);
		steps.put(72, bringPolmafiItems);
		steps.put(74, bringPolmafiItems);

		steps.put(76, talkToPolmafiMore);

		steps.put(78, bringUnscentedToVanescula);

		steps.put(80, talkToVeliafForFight);
		steps.put(82, talkToVeliafForFight);

		ConditionalStep goKillDamien = new ConditionalStep(this, talkToVeliafForFight);
		goKillDamien.addStep(inDamienRoom, killDamien);
		steps.put(84, goKillDamien);

		steps.put(86, talkToVeliafAfterDamien);

		steps.put(88, talkToVanesculaAfterDamien);

		steps.put(90, enterDarkmeyer);

		steps.put(92, talkToDesmodus);

		steps.put(94, talkToMordan);

		steps.put(96, talkToMaria);

		steps.put(98, talkToMaria);

		steps.put(100, talkToDesmodusAgain);

		steps.put(102, getNote);

		ConditionalStep goReadNote = new ConditionalStep(this, getNote);
		goReadNote.addStep(oldNote, readNote);
		steps.put(104, getNote);

		steps.put(106, valveStep);
		steps.put(108, valveStep);
		steps.put(110, valveStep);

		ConditionalStep getLogs = new ConditionalStep(this, cutLogs);
		getLogs.addStep(blisterwood8.alsoCheckBank(questBank), bringVanesculaLogs);

		steps.put(112, getLogs);

		ConditionalStep bringItemsToVertida = new ConditionalStep(this, cutLogs);
		bringItemsToVertida.addStep(new Conditions(inNewBase, blisterwood8), bringVertidaLogs);
		bringItemsToVertida.addStep(blisterwood8.alsoCheckBank(questBank), goDownToVerditaWithLogs);
		steps.put(114, bringItemsToVertida);

		steps.put(116, talkToVertidaForFlail);

		ConditionalStep createFlail = new ConditionalStep(this, goDownToMakeFlail);
		createFlail.addStep(blisterwoodSickle, useFlailOnSickle);
		createFlail.addStep(enchantedSickle, useLogOnSickle);
		createFlail.addStep(rubySickle, enchantRubySickle);
		createFlail.addStep(sickle, addRubyToSickle);
		createFlail.addStep(inNewBase, getSickle);
		steps.put(118, createFlail);

		steps.put(120, talkToVanesculaWithFlail);

		steps.put(122, talkToSafalaanWithFlail);

		steps.put(124, talkToVanesculaBeforeFight);

		steps.put(126, talkToVanesculaForFight);

		steps.put(128, talkToVeliafForFinalFight);

		ConditionalStep goFightVanstrom = new ConditionalStep(this, talkToVeliafForFinalFight);
		goFightVanstrom.addStep(inFinalFightArea, fightVanstrom);
		steps.put(130, goFightVanstrom);

		steps.put(132, talkToVeliafAfterFight);

		ConditionalStep goFinishQuest = new ConditionalStep(this, enterBaseToFinish);
		goFinishQuest.addStep(inNewBase, finishQuest);
		steps.put(134, goFinishQuest);
		steps.put(136, goFinishQuest);

		return steps;
	}

	private void setupZones()
	{
		followingCarlArea = new Zone(new WorldPoint(3676, 3264, 0), new WorldPoint(3782, 3399, 0));
		kroyArea = new Zone(new WorldPoint(3724, 9664, 1), new WorldPoint(3903, 9855, 1));
		pater = new Zone(new WorldPoint(3402, 9880, 0), new WorldPoint(3443, 9907, 0));
		swingArea = new Zone(new WorldPoint(2080, 5000, 0), new WorldPoint(2100, 5025, 0));
		swingExitArea = new Zone(new WorldPoint(2081, 5014, 0), new WorldPoint(2100, 5025, 0));
		nailBeastArea = new Zone(new WorldPoint(2320, 5000, 0), new WorldPoint(2345, 5024, 0));
		bridgeArea = new Zone(new WorldPoint(2080, 5030, 0), new WorldPoint(2104, 5048, 0));
		bridgeExitArea = new Zone(new WorldPoint(2091, 5032, 0), new WorldPoint(2104, 5048, 0));
		juvinateArea = new Zone(new WorldPoint(2390, 5002, 0), new WorldPoint(2403, 5023, 0));
		lab = new Zone(new WorldPoint(3500, 9672, 0), new WorldPoint(3638, 9850, 0));
		newBase = new Zone(new WorldPoint(3588, 9609, 0), new WorldPoint(3606, 9619, 0));
		damienRoom = new Zone(new WorldPoint(3712, 3352, 1), new WorldPoint(3728, 3370, 1));
		finalFightArea = new Zone(new WorldPoint(3560, 3348, 0), new WorldPoint(3584, 3371, 0));
	}

	public void setupConditions()
	{
		destroyedLabTable1 = new VarbitRequirement(10353, 2, Operation.GREATER_EQUAL);
		vineAdded = new ObjectCondition(ObjectID.SWAMP_TREE_BRANCH_38005, new WorldPoint(2093, 5015, 0));
		nailBeastNearby = new Conditions(LogicType.OR, new NpcCondition(NpcID.NAIL_BEAST_9612), new NpcCondition(NpcID.NAIL_BEAST_9613));
		juvinateNearby = new Conditions(LogicType.OR, new NpcCondition(NpcID.VAMPYRE_JUVINATE_9615), new NpcCondition(NpcID.VAMPYRE_JUVINATE_9614));

		inFollowingCarlArea = new Conditions(new InInstanceRequirement(), new ZoneRequirement(followingCarlArea));
		inKroyArea = new ZoneRequirement(kroyArea);
		inPater = new ZoneRequirement(pater);
		inSwingArea = new ZoneRequirement(swingArea);
		inSwingExitArea = new ZoneRequirement(swingExitArea);
		inNailBeastArea = new ZoneRequirement(nailBeastArea);
		inBridgeArea = new ZoneRequirement(bridgeArea);
		inBridgeExitArea = new ZoneRequirement(bridgeExitArea);
		inJuvinateArea = new ZoneRequirement(juvinateArea);
		inLab = new ZoneRequirement(lab);
		inDeepLab = new Conditions(inLab, new InInstanceRequirement());
		inNewBase = new ZoneRequirement(newBase);
		inDamienRoom = new ZoneRequirement(damienRoom);
		inFinalFightArea = new ZoneRequirement(finalFightArea);

		hasRepairedBridge1 = new ObjectCondition(ObjectID.PARTIALLY_BROKEN_BRIDGE, new WorldPoint(2090, 5039, 0));
		hasRepairedBridge2 = new ObjectCondition(ObjectID.SLIGHTLY_BROKEN_BRIDGE, new WorldPoint(2090, 5039, 0));
		hasRepairedBridge3 = new ObjectCondition(ObjectID.FIXED_BRIDGE, new WorldPoint(2090, 5039, 0));

		inPuzzleInterface = new WidgetTextRequirement(665, 7, "1");

		talkedToKael = new VarbitRequirement(10347, 1);
		talkedToVertida = new VarbitRequirement(10348, 1);
		talkedToPolmafi = new VarbitRequirement(10350, 2);
		talkedToRadigad = new VarbitRequirement(10351, 1);
		talkedToIvan = new VarbitRequirement(10349, 1);
	}

	@Override
	public void setupRequirements()
	{
		haemBook = new ItemRequirement("Haemalchemy volume 2", ItemID.HAEMALCHEMY_VOLUME_2);
		haemBook.setTooltip("If you lost the book, search the bookshelf in the room west of Safalaan to get it back");

		chisel = new ItemRequirement("Chisel", ItemID.CHISEL);

		vyreTop = new ItemRequirement("Vyrewatch top", ItemID.VYREWATCH_TOP);
		vyreTop.setTooltip("You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp");
		vyreLegs = new ItemRequirement("Vyrewatch legs", ItemID.VYREWATCH_LEGS);
		vyreLegs.setTooltip("You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp");
		vyreShoes = new ItemRequirement("Vyrewatch shoes", ItemID.VYREWATCH_SHOES);
		vyreShoes.setTooltip("You can get this from Trader Sven in southern Meiyerditch near Old Man Ral's house for 650gp");

		unscentedTop = new ItemRequirement("Vyre noble top unscented", ItemID.VYRE_NOBLE_TOP_UNSCENTED);
		unscentedTop.setTooltip("You can get a replacement from a chest in Old Man Ral's basement");
		unscentedLegs = new ItemRequirement("Vyre noble legs unscented", ItemID.VYRE_NOBLE_LEGS_UNSCENTED);
		unscentedLegs.setTooltip("You can get a replacement from a chest in Old Man Ral's basement");
		unscentedShoes = new ItemRequirement("Vyre noble shoes unscented", ItemID.VYRE_NOBLE_SHOES_UNSCENTED);
		unscentedShoes.setTooltip("You can get a replacement from a chest in Old Man Ral's basement");

		ivandisFlail = new ItemRequirement("Ivandis flail", ItemID.IVANDIS_FLAIL);
		ivandisFlail.setTooltip("You can buy another from Vertida in the Myreque base for 20k");

		ivandisFlailEquipped = new ItemRequirement("Ivandis flail", ItemID.IVANDIS_FLAIL, 1, true);
		ivandisFlailEquipped.setTooltip("You can buy another from Vertida in the Myreque base for 20k");

		ivandisFlailHighlighted = new ItemRequirement("Ivandis flail", ItemID.IVANDIS_FLAIL);
		ivandisFlailHighlighted.setHighlightInInventory(true);
		ivandisFlailHighlighted.setTooltip("You can buy another from Vertida in the Myreque base for 20k");

		scentedTop = new ItemRequirement("Vyre noble top", ItemID.VYRE_NOBLE_TOP, 1, true);
		scentedTop.setTooltip("You can get a replacement from a chest in Old Man Ral's basement");
		scentedLegs = new ItemRequirement("Vyre noble legs", ItemID.VYRE_NOBLE_LEGS, 1, true);
		scentedLegs.setTooltip("You can get a replacement from a chest in Old Man Ral's basement");
		scentedShoes = new ItemRequirement("Vyre noble shoes", ItemID.VYRE_NOBLE_SHOES, 1, true);
		scentedShoes.setTooltip("You can get a replacement from a chest in Old Man Ral's basement");

		blisterwood8 = new ItemRequirement("Blisterwood logs", ItemID.BLISTERWOOD_LOGS, 8);

		blisterwoodFlail = new ItemRequirement("Blisterwood flail", ItemID.BLISTERWOOD_FLAIL);
		blisterwoodFlail.setTooltip("You can get another Blisterwood Flail from Vertida in the Myreque Hideout in Old" +
			" Man Ral's basement or Veliaf Hurtz at the Icyene Graveyard");

		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		axeEquipped = new ItemRequirement("Any axe", ItemCollections.AXES, 1, true);

		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		vine3 = new ItemRequirement("Short vine", ItemID.SHORT_VINE, 3);
		vine3.setHighlightInInventory(true);
		longVine = new ItemRequirement("Long vine", ItemID.LONG_VINE);
		longVine.setHighlightInInventory(true);

		log1 = new ItemRequirement("Logs", ItemID.LOGS);
		log1.setHighlightInInventory(true);
		log2 = new ItemRequirement("Logs", ItemID.LOGS, 2);
		log2.setHighlightInInventory(true);
		log3 = new ItemRequirement("Logs", ItemID.LOGS, 3);
		log3.setHighlightInInventory(true);

		oldNote = new ItemRequirement("Old note", ItemID.OLD_NOTE);
		oldNote.setHighlightInInventory(true);

		ruby = new ItemRequirement("Ruby", ItemID.RUBY);

		rubyHighlighted = new ItemRequirement("Ruby", ItemID.RUBY);
		rubyHighlighted.setHighlightInInventory(true);
		sickle = new ItemRequirement("Silver sickle (b)", ItemID.SILVER_SICKLE_B);
		sickle.setHighlightInInventory(true);

		fireRune5 = new ItemRequirement("Fire rune", ItemCollections.FIRE_RUNE, 5);
		fireStaff = new ItemRequirement("Fire staff", ItemCollections.FIRE_STAFF);
		cosmicRune = new ItemRequirement("Cosmic rune", ItemID.COSMIC_RUNE);

		enchantRunes = new ItemRequirements("Ruby enchant runes", new ItemRequirements(LogicType.OR, "3 air runes", fireRune5, fireStaff), cosmicRune);
		enchantTablet = new ItemRequirement("Ruby enchant tablet", ItemID.ENCHANT_RUBY_OR_TOPAZ);
		enchantRubyRunesOrTablet = new ItemRequirements(LogicType.OR, "Runes or tablet for Enchant Ruby", enchantRunes, enchantTablet);

		rubySickle = new ItemRequirement("Ruby sickle", ItemID.RUBY_SICKLE_B);
		rubySickle.setHighlightInInventory(true);
		enchantedSickle = new ItemRequirement("Enchanted ruby sickle (b)", ItemID.ENCHANTED_RUBY_SICKLE_B);
		enchantedSickle.setHighlightInInventory(true);
		blisterwoodLog = new ItemRequirement("Blisterwood log", ItemID.BLISTERWOOD_LOGS);
		blisterwoodLog.setHighlightInInventory(true);
		blisterwoodSickle = new ItemRequirement("Blisterwood sickle", ItemID.BLISTERWOOD_SICKLE);
		blisterwoodSickle.setHighlightInInventory(true);

		combatGear = new ItemRequirement("Combat gear + food", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		rangedWeaponForBloodveld = new ItemRequirement("Anything to range the bloodveld during bossfight", -1, -1);
		rangedWeaponForBloodveld.setDisplayItemId(BankSlotIcons.getRangedCombatGear());
		antipoison = new ItemRequirement("Antipoison", ItemCollections.ANTIPOISONS);
		pickaxe = new ItemRequirement("Any pickaxe", ItemCollections.PICKAXES);
		pickaxe.setTooltip("You can get one from one of the miners in the mine");
		ItemRequirements vyrewatchOutfit = new ItemRequirements("Vyrewatch outfit",
			new ItemRequirement("Vyrewatch top", ItemID.VYREWATCH_TOP),
			new ItemRequirement("Vyrewatch legs", ItemID.VYREWATCH_LEGS),
			new ItemRequirement("Vyrewatch shoes", ItemID.VYREWATCH_SHOES));

		vyrewatchOutfitOrCoins = new ItemRequirements(LogicType.OR, "Vyrewatch outfit or 1950 coins", vyrewatchOutfit,
			new ItemRequirement("Coins", ItemCollections.COINS, 1950));

		drakanMedallion = new ItemRequirement("Drakan's medallion", ItemID.DRAKANS_MEDALLION);
		moryLegs3 = new ItemRequirement("Morytania legs 3/4", ItemID.MORYTANIA_LEGS_3);
		moryLegs3.addAlternates(ItemID.MORYTANIA_LEGS_4);
	}

	public void setupSteps()
	{
		startQuest = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Talk to Veliaf in Slepe.");
		startQuest.addDialogStep("Yes.");

		talkToHameln = new NpcStep(this, NpcID.HAMELN_THE_JESTER_9505, new WorldPoint(3736, 3316, 0),
			"Go to the church and talk Hameln the Jester about how his friend fell ill.");
		talkToHameln.addDialogStep("I'd better get going.");
		talkToHameln.addDialogStep("Do you know how he fell ill?");

		talkToCarl = new NpcStep(this, NpcID.CARL_9767, new WorldPoint(3750, 3296, 0),
			"Go to The Rat & Bat pub south east of the church and speak to Carl.");
		talkToCarl.addDialogStep("Where do you get your Bloody Bracers from?");

		inspectBarrel = new ObjectStep(this, NullObjectID.NULL_39515, new WorldPoint(3749, 3291, 0),
			"Inspect the barrel south of the Rat & Bat Pub.");

		followCarl = new NpcStep(this, NpcID.CARL_9558, new WorldPoint(3714, 3328, 0),
			"Follow Carl, hiding behind objects when he turns around.");
		((NpcStep) (followCarl)).setMaxRoamRange(200);
		((NpcStep) (followCarl)).setLinePoints(Arrays.asList(
			new WorldPoint(3750, 3308, 0),
			new WorldPoint(3751, 3315, 0),
			new WorldPoint(0, 0, 2),
			new WorldPoint(3747, 3316, 0),
			new WorldPoint(3740, 3324, 0),
			new WorldPoint(0, 0, 2),
			new WorldPoint(3740, 3328, 0),
			new WorldPoint(3718, 3325, 0),
			new WorldPoint(0, 0, 2),
			new WorldPoint(3718, 3328, 0),
			new WorldPoint(3700, 3323, 0),
			new WorldPoint(0, 0, 2),
			new WorldPoint(3703, 3320, 0),
			new WorldPoint(3705, 3305, 0),
			new WorldPoint(0, 0, 2),
			new WorldPoint(3707, 3310, 0),
			new WorldPoint(3719, 3312, 0),
			new WorldPoint(0, 0, 2),
			new WorldPoint(3718, 3316, 0),
			new WorldPoint(3731, 3318, 0),
			new WorldPoint(3731, 3307, 0)
		));

		goDownToKroy = new ObjectStep(this, ObjectID.STAIRS_32637, new WorldPoint(3728, 3301, 0), "Go down the stairs to fight Kroy.");
		goDownToKroy.addDialogStep("Continue the Sins of the Father quest.");

		killKroy = new NpcStep(this, NpcID.KROY_9560, new WorldPoint(3734, 9763, 1),
			"Kill Kroy.");
		killKroy.addSubSteps(goDownToKroy);

		destroyLab = new ObjectStep(this, NullObjectID.NULL_39516, new WorldPoint(3730, 9760, 1),
			"Destroy both the Lab tables.");

		destroyLab2 = new ObjectStep(this, NullObjectID.NULL_39517, new WorldPoint(3725, 9760, 1),
			"Destroy both the Lab tables.");
		destroyLab.addSubSteps(destroyLab2);

		talkToVeliafAfterKroy = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Return to Veliaf in Slepe.");
		talkToVeliafAfterKroy.addDialogStep("Fair enough, so what now?");

		enterPater = new ObjectStep(this, ObjectID.TRAPDOOR_3433, new WorldPoint(3422, 3485, 0), "Talk to Veliaf in Paterdomus.");
		((ObjectStep) (enterPater)).addAlternateObjects(ObjectID.TRAPDOOR_3432);

		talkToVeliafInPater = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3438, 9897, 0),
			"Talk to Veliaf in Paterdomus.");
		talkToVeliafInPater.addDialogStep("I see. So that's why you want to keep him safe, Veliaf?");
		talkToVeliafInPater.addSubSteps(enterPater);

		talkToIvan = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
			"Speak to Ivan Strom south of Fenkenstrain's Castle.");

		listenToMeeting = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3548, 3516, 0),
			"Listen to the meeting.");

		talkToIvanAfterMeeting = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3444, 3485, 0),
			"Speak to Ivan Strom outside the east entrance of Paterdomus.");
		talkToIvanAfterMeeting.addDialogStep("I'm ready.");

		/* Vine swing section */
		searchForKnife = new ObjectStep(this, ObjectID.BACKPACK, "Search the nearby backpack for a knife.");
		cutVines = new ObjectStep(this, ObjectID.SWAMP_TREE_13847, new WorldPoint(2096, 5011, 0), "Cut 3 vines from the nearby tree.", knife);
		combineVines = new DetailedQuestStep(this, "Combine the vines together.", vine3);
		useVineOnBranch = new ObjectStep(this, ObjectID.SWAMP_TREE_BRANCH_38004, new WorldPoint(2093, 5015, 0), "Use the long vine on the nearby branch.", longVine);
		useVineOnBranch.addIcon(ItemID.LONG_VINE);
		swingOnVine = new ObjectStep(this, ObjectID.SWAMP_TREE_BRANCH_38005, new WorldPoint(2093, 5015, 0), "Swing on the vine.");
		leaveSwingArea = new ObjectStep(this, ObjectID.PATH_38008, new WorldPoint(2091, 5024, 0), "Continue the trek.");
		leaveSwingArea.addDialogStep("Yes.");

		/* Nail beast section */
		killNailBeasts = new NpcStep(this, NpcID.NAIL_BEAST_9612, new WorldPoint(2332, 5012, 0), "Kill the nail beasts.", true);
		((NpcStep) (killNailBeasts)).addAlternateNpcs(NpcID.NAIL_BEAST_9613);
		leaveNailBeastArea = new ObjectStep(this, ObjectID.PATH_38009, new WorldPoint(2334, 5021, 0), "Continue the trek.");
		leaveNailBeastArea.addDialogStep("Yes.");

		/* Bridge section */
		killZombieForAxe = new NpcStep(this, NpcID.ZOMBIE_5647, new WorldPoint(2084, 5040, 0), "Kill the nearby zombie for an axe.", axe);
		get3LogsForBridge = new ObjectStep(this, ObjectID.DEAD_TREE_1365, "Chop the nearby dead trees for 3 logs.", axe);
		repairBridge1 = new ObjectStep(this, ObjectID.BROKEN_BRIDGE_13834, new WorldPoint(2090, 5039, 0), "Repair the bridge.", axeEquipped, log3);
		repairBridge1.addIcon(ItemID.LOGS);
		repairBridge2 = new ObjectStep(this, ObjectID.PARTIALLY_BROKEN_BRIDGE, new WorldPoint(2090, 5039, 0), "Repair the bridge.", axeEquipped, log2);
		repairBridge2.addIcon(ItemID.LOGS);
		repairBridge3 = new ObjectStep(this, ObjectID.SLIGHTLY_BROKEN_BRIDGE, new WorldPoint(2090, 5039, 0), "Repair the bridge.", axeEquipped, log1);
		repairBridge3.addIcon(ItemID.LOGS);
		repairBridge1.addSubSteps(repairBridge2, repairBridge3);
		crossBridge = new ObjectStep(this, ObjectID.FIXED_BRIDGE, new WorldPoint(2090, 5039, 0), "Cross the bridge.");
		leaveBridgeArea = new ObjectStep(this, ObjectID.PATH_38010, new WorldPoint(2103, 5038, 0), "Continue the trek.");
		leaveBridgeArea.addDialogStep("Yes.");

		/* Vampyre Juvinate section */
		killJuvinates = new NpcStep(this, NpcID.VAMPYRE_JUVINATE_9615, new WorldPoint(2396, 5011, 0), "Kill the juvinates with the Ivandis Flail. Try to attack the one near Ivan before it can attack him.", true, ivandisFlailEquipped);
		((NpcStep) (killJuvinates)).addAlternateNpcs(NpcID.VAMPYRE_JUVINATE_9614);
		leaveJuvinateArea = new ObjectStep(this, ObjectID.PATH_38011, new WorldPoint(2397, 5023, 0), "Continue the trek.");
		leaveJuvinateArea.addDialogStep("Yes.");

		/* Post trek section */
		talkToIvanAfterTrek = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3486, 3241, 0),
			"Finish speaking to Ivan Strom.");

		talkToVeliafInBoatHouse = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3529, 3168, 0),
			"Talk to Veliaf at the boat house in the south of Burgh de Rott.");

		travelToGraveyard = new ObjectStep(this, NullObjectID.NULL_12945, new WorldPoint(3523, 3170, 0),
			"Get into the boat to the Icyene Graveyard.");
		travelToGraveyard.addDialogStep("Icyene Graveyard.");

		talkToVeliafInGraveyard = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3684, 3181, 0),
			"Finish talking to Veliaf and Safalaan.");
		talkToVeliafInGraveyard.addDialogStep("Icyene Graveyard.");

		talkToVanescula = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3705, 3188, 0),
			"Talk with Vanescula Drakan.");
		talkToVanescula.addDialogStep("Icyene Graveyard.");

		openPuzzleDoor = new ObjectStep(this, NullObjectID.NULL_39519, new WorldPoint(3704, 3190, 0), "Try opening the door to the mausoleum.");

		talkToVanesculaAfterPuzzle = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3705, 3188, 0),
			"Finish the cutscene in the Icyene Graveyard.");
		talkToVanesculaAfterPuzzle.addDialogStep("Icyene Graveyard.");
		openPuzzleDoor.addSubSteps(talkToVanesculaAfterPuzzle);

		convinceKael = new NpcStep(this, NpcID.KAEL_FORSHAW_9543, new WorldPoint(3685, 3184, 0),
			"Speak to Kael Forshaw in the Icyene Graveyard. If you already have, open the quest journal to re-sync.");
		convinceVertida = new NpcStep(this, NpcID.VERTIDA_SEFALATIS_9547, new WorldPoint(3694, 3185, 0),
			"Speak to Verdita Sefalatis in the Icyene Graveyard.");
		convincePolmafi = new NpcStep(this, NpcID.POLMAFI_FERDYGRIS_9554, new WorldPoint(3704, 3182, 0),
			"Speak to Polmafi Ferdygris in the Icyene Graveyard.");
		convinceRadigad = new NpcStep(this, NpcID.RADIGAD_PONFIT_9551, new WorldPoint(3689, 3188, 0),
			"Speak to Radigad Ponfit in the Icyene Graveyard.");
		convinceIvan = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3696, 3182, 0),
			"Speak to Ivan Strom in the Icyene Graveyard.");
		convinceVeliaf = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3690, 3183, 0),
			"Speak to Veliaf Hurtz in the Icyene Graveyard.");

		talkToVanesculaAfterTeam = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Speak to Vanescula in the Icyene Graveyard.");
		talkToVanesculaAfterTeam.addDialogStep("Icyene Graveyard.");

		goToLab = new ObjectStep(this, ObjectID.STAIRCASE_18049, new WorldPoint(3643, 3305, 0), "Enter the Meiyerditch lab. The fastest way here is to have a Vyrewatch take you to the mines, mine 15 Daeylt Ore, then leave.");
		goToLab.addDialogStep("Meiyerditch.");
		talkToSafalaanInLab = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3635, 9689, 0),
			"Speak to Safalaan at the Lab.");
		talkToSafalaanInLab.addDialogStep("Shall we get going then?");
		talkToSafalaanInLab.addDialogStep("Let's go.");
		enterDeepLab = new ObjectStep(this, ObjectID.DOOR_17911, new WorldPoint(3629, 9680, 0), "Enter the door to the deeper labs.");
		killBloodveld = new NpcStep(this, NpcID.MUTATED_BLOODVELD_9611, new WorldPoint(3611, 9737, 0),
			"Defeat the Mutated Bloodveld (lvl-123).");
		talkToSafalaanInDeepLab = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3611, 9737, 0),
			"Finish speaking with Safalaan in the Lab.");
		searchLabBookcase = new ObjectStep(this, ObjectID.BOOKSHELF_38017, new WorldPoint(3589, 9745, 0),
			"Search the bookshelf in the room west of Safalaan in the Lab.");
		searchLabBookcase.addDialogStep("I'll be back soon.");
		takeBookToSafalaan = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3611, 9737, 0),
			"Go show Safalaan the Haemalchemy book 2 you found.", haemBook);
		takeBookToSafalaan.addDialogStep("Sure. Here you go.");

		talkToVanesculaAfterLab = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Speak to Vanescula in the Icyene Graveyard. Take the boat south of Burgh de Rott to get there.");
		talkToVanesculaAfterLab.addDialogStep("Okay. I'll see you soon.");
		talkToVanesculaAfterLab.addDialogStep("Icyene Graveyard.");

		goDownToPolmafi = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0), "Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.",
			vyreTop, vyreLegs, vyreShoes);
		((ObjectStep) (goDownToPolmafi)).addAlternateObjects(ObjectID.TRAPDOOR_32577);
		goDownToPolmafi.addDialogStep("Meiyerditch.");
		goDownToPolmafiNoItems = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0), "Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.",
			vyreTop, vyreLegs, vyreShoes);
		((ObjectStep) (goDownToPolmafiNoItems)).addAlternateObjects(ObjectID.TRAPDOOR_32577);
		goDownToPolmafiNoItems.addDialogStep("Meiyerditch.");
		talkToPolmafi = new NpcStep(this, NpcID.POLMAFI_FERDYGRIS_9554, new WorldPoint(3599, 9612, 0),
			"Bring a Vyrewatch disguise to Polmafi in the Meiyerditch hideout in Old Man Ral's basement.", vyreTop, vyreLegs, vyreShoes);
		talkToPolmafi.addDialogStep("Here you go.");
		talkToPolmafiMore = new NpcStep(this, 9554, new WorldPoint(3599, 9612, 0),
			"Finish speaking to Polmafi in the Meiyerditch hideout.");
		talkToPolmafiMore.addDialogStep("Here you go.");
		talkToPolmafi.addSubSteps(talkToPolmafiMore, goDownToPolmafi, goDownToPolmafiNoItems);

		bringUnscentedToVanescula = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the Vyre noble outfit.",
			unscentedTop, unscentedLegs, unscentedShoes);
		bringUnscentedToVanescula.addDialogStep("Icyene Graveyard.");

		talkToVeliafForFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3729, 3318, 0),
			"Talk to Veliaf outside the church in Slepe.",
			unscentedTop, unscentedLegs, unscentedShoes, ivandisFlailEquipped);
		talkToVeliafForFight.addDialogSteps("Slepe.", "Let's do this.");

		killDamien = new NpcStep(this, NpcID.DAMIEN_LEUCURTE_9564, new WorldPoint(3717, 3358, 1),
			"Kill Damien Leucurte (lvl-204). He can poison you. Stomp out any of the fires he spawns.",
			unscentedTop, unscentedLegs, unscentedShoes, ivandisFlailEquipped);
		killDamien.addDialogSteps("Slepe.", "Let's do this.");

		talkToVeliafAfterDamien = new NpcStep(this, NpcID.VELIAF_HURTZ_9523, new WorldPoint(3720, 3357, 1),
			"Talk to Veliaf in Slepe.");

		talkToVanesculaAfterDamien = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the Vyre noble outfit.",
			scentedTop, scentedLegs, scentedShoes);
		talkToVanesculaAfterDamien.addDialogSteps("I'll see you there.", "Icyene Graveyard.");

		enterDarkmeyer = new ObjectStep(this, ObjectID.CRACKED_WALL, new WorldPoint(3627, 3329, 0),
			"Go to Darkmeyer. You can take the boat to Meiyerditch, talk to a Vyrewatch to be sent to the mines, get out and you'll be right next to the entrance. If you're wearing the outfit you won't need to mine anything.",
			scentedTop, scentedLegs, scentedShoes);
		enterDarkmeyer.addDialogSteps("Meiyerditch.", "Send me to the mines.");

		talkToDesmodus = new NpcStep(this, NpcID.DESMODUS_LASIURUS, new WorldPoint(3612, 3362, 0),
			"Speak to Desmodus Lasiurus outside the Aboretum in Darkmeyer.",
			scentedTop, scentedLegs, scentedShoes);

		talkToMordan = new NpcStep(this, NpcID.MORDAN_NIKAZSI, new WorldPoint(3662, 3347, 0),
			"Speak to Mordan Mikazsi in lower Darkmeyer.", scentedTop, scentedLegs, scentedShoes);

		talkToMaria = new NpcStep(this, NpcID.MARIA_GADDERANKS, new WorldPoint(3618, 3378, 0),
			"Speak to Maria Gadderanks in the jail north of the Aboretum in Darkmeyer.",
			scentedTop, scentedLegs, scentedShoes);

		talkToDesmodusAgain = new NpcStep(this, NpcID.DESMODUS_LASIURUS, new WorldPoint(3612, 3362, 0),
			"Speak to Desmodus Lasiurus outside the Aboretum in Darkmeyer.",
			scentedTop, scentedLegs, scentedShoes);

		getNote = new ObjectStep(this, ObjectID.SHELVES_37999, new WorldPoint(3625, 3358, 0),
			"Search the shelves in the Aboretum in Darkmeyer. Read the Old Note you get.",
			scentedTop, scentedLegs, scentedShoes);

		readNote = new DetailedQuestStep(this, "Read the note.", oldNote);

		bringVanesculaLogs = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the 8 Blisterwood logs.",
			blisterwood8);
		bringVanesculaLogs.addDialogStep("Icyene Graveyard.");

		goDownToVerditaWithLogs = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0),
			"Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.", blisterwood8);
		((ObjectStep) (goDownToVerditaWithLogs)).addAlternateObjects(ObjectID.TRAPDOOR_32577);
		goDownToVerditaWithLogs.addDialogStep("Meiyerditch.");

		bringVertidaLogs = new NpcStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3598, 9612, 0),
			"Go speak to Vertida in Old Man Ral's basement with the 8 Blisterwood logs.",
			blisterwood8);
		bringVertidaLogs.addDialogSteps("Meiyerditch.", "Here you go.");
		bringVertidaLogs.addSubSteps(goDownToVerditaWithLogs);

		talkToVertidaForFlail = new NpcStep(this, NpcID.VERTIDA_SEFALATIS, new WorldPoint(3598, 9612, 0),
			"Speak to Vertida Sefalatis.");

		goDownToMakeFlail = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0),
			"Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.", blisterwoodLog, ruby, chisel, knife, ivandisFlail, enchantRubyRunesOrTablet);
		((ObjectStep) (goDownToMakeFlail)).addAlternateObjects(ObjectID.TRAPDOOR_32577);
		goDownToMakeFlail.addDialogStep("Meiyerditch.");

		getSickle = new ObjectStep(this, ObjectID.CRATE_32575, new WorldPoint(3597, 9615, 0), "Search the nearby crate for a silver sickle.");
		addRubyToSickle = new DetailedQuestStep(this, "Use a ruby on the sickle.", rubyHighlighted, sickle, chisel);
		addRubyToSickle.addDialogStep("Yes.");
		enchantRubySickle = new DetailedQuestStep(this, "Cast enchant ruby on the sickle.", enchantRubyRunesOrTablet, rubySickle);
		useLogOnSickle = new DetailedQuestStep(this, "Use a blisterwood log on the enchanted sickle.", blisterwoodLog, enchantedSickle, knife);
		useLogOnSickle.addDialogStep("Yes.");
		useFlailOnSickle = new DetailedQuestStep(this, "Use the flail on the blisterwood sickle.", ivandisFlailHighlighted, blisterwoodSickle);
		useFlailOnSickle.addDialogStep("Yes.");

		talkToVanesculaWithFlail = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Return to Vanescula in the Icyene Graveyard with the Blisterwood Flail.", blisterwoodFlail);
		talkToVanesculaWithFlail.addDialogStep("Icyene Graveyard.");

		talkToSafalaanWithFlail = new NpcStep(this, NpcID.SAFALAAN_HALLOW_9537, new WorldPoint(3719, 3215, 0),
			"Speak to Safalaan north of the Icyene Graveyard.", blisterwoodFlail);
		talkToSafalaanWithFlail.addDialogStep("Icyene Graveyard.");

		talkToVanesculaBeforeFight = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Speak to Vanescula in the Icyene Graveyard.", blisterwoodFlail);
		talkToVanesculaBeforeFight.addDialogStep("Icyene Graveyard.");

		talkToVanesculaForFight = new NpcStep(this, NpcID.VANESCULA_DRAKAN_9574, new WorldPoint(3708, 3187, 0),
			"Prepare for a challenging fight. Speak to Vanescula to enter the fight.", blisterwoodFlail);
		talkToVanesculaForFight.addDialogSteps("Icyene Graveyard.", "I'm ready.");

		talkToVeliafForFinalFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9489, new WorldPoint(3707, 3188, 0),
			"Prepare for a challenging fight. Speak to Veliaf to enter the fight.", blisterwoodFlail);
		((NpcStep) (talkToVeliafForFinalFight)).addAlternateNpcs(NpcID.VELIAF_HURTZ_9521);
		talkToVeliafForFinalFight.addDialogSteps("Icyene Graveyard.", "Let's go.");

		fightVanstrom = new NpcStep(this, NpcID.VANSTROM_KLAUSE_9569, new WorldPoint(3567, 3358, 0),
			"Fight Vanstrom Klause. Check the sidebar for more details.", blisterwoodFlail);
		((NpcStep) (fightVanstrom)).addAlternateNpcs(NpcID.VANSTROM_KLAUSE_9570, NpcID.VANSTROM_KLAUSE_9571);


		fightVanstromSidebar = new DetailedQuestStep(this,
			"Fight Vanstrom Klause. Protect from Magic. He has various special attacks indicated by what he says:",
			blisterwoodFlail);
		fightVanstromSidebar.addText("My pets will feast on your corpse: A bloodveld will appear which you need to kill from a distance.");
		fightVanstromSidebar.addText("Blood will be my strength: An orb appears that heals Vanstrom. Lure Vanstrom onto it.");
		fightVanstromSidebar.addText("Stare into the darkness: Face away from Vanstrom.");
		fightVanstromSidebar.addText("Once he hits 0hp, he'll heal to 200 and start summoning lightning bolts. Move off " +
			"tiles where they appear.");
		fightVanstromSidebar.addSubSteps(fightVanstrom);

		talkToVeliafAfterFight = new NpcStep(this, NpcID.VELIAF_HURTZ_9521, new WorldPoint(3707, 3188, 0),
			"Speak to Veliaf in the Icyene graveyard to re-enter the cutscene.");
		fightVanstrom.addSubSteps(talkToVeliafAfterFight);

		talkToVeliafAfterFight.addDialogSteps("Icyene Graveyard.", "Let's go.");
		talkToVanesculaBeforeFight.addSubSteps(talkToVanesculaForFight, talkToVeliafForFinalFight);

		enterBaseToFinish = new ObjectStep(this, ObjectID.TRAPDOOR_32578, new WorldPoint(3605, 3215, 0), "Climb down the trapdoor in Old Man Ral's house in south west Meiyerditch.");
		((ObjectStep) (enterBaseToFinish)).addAlternateObjects(ObjectID.TRAPDOOR_32577);

		finishQuest = new NpcStep(this, NpcID.VELIAF_HURTZ_9522, new WorldPoint(3598, 9613, 0),
			"Speak to Veliaf in the Myreque Hideout to complete the quest.");
		finishQuest.addSubSteps(enterBaseToFinish);

		cutLogs = new ObjectStep(this, ObjectID.BLISTERWOOD_TREE, new WorldPoint(3635, 3362, 0),
			"Gather 8 logs from the Blisterwood tree.", scentedTop, scentedLegs, scentedShoes, blisterwood8, axe);

		templeTrek = new NpcStep(this, NpcID.IVAN_STROM_9530, new WorldPoint(3444, 3485, 0),
			"Speak to Ivan Strom outside the east entrance of Paterdomus to go temple treking with him.");
		talkToTeamSteps = new DetailedQuestStep(this, "Convince the Myreque to take on Drakan.");
		valveStep = new ValveStep(this);
		createFlailSteps = new DetailedQuestStep(this, "Create the blisterwood flail.", blisterwoodFlail);
		doDoorPuzzle = new DoorPuzzleStep(this);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(combatGear, vyrewatchOutfitOrCoins, ivandisFlail, axe, ruby, enchantRubyRunesOrTablet, knife, chisel);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(pickaxe, antipoison, drakanMedallion, moryLegs3);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("Kroy (level 133)", "Vampyre juveniles (level 122 and 119)", "Nail beasts (level 143 and 67)", "Mutated bloodveld (level 123)", "Damien Leucurte (level 204)", "Vanstrom Klause (level 413)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.VAMPYRE_SLAYER, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.A_TASTE_OF_HOPE, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.WOODCUTTING, 62));
		req.add(new SkillRequirement(Skill.FLETCHING, 60));
		req.add(new SkillRequirement(Skill.CRAFTING, 56));
		req.add(new SkillRequirement(Skill.AGILITY, 52));
		req.add(new SkillRequirement(Skill.ATTACK, 50));
		req.add(new SkillRequirement(Skill.SLAYER, 50));
		req.add(new SkillRequirement(Skill.MAGIC, 49));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(2);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("3 x 15,000 Experince Tomes (Any skill above 60)", ItemID.ANTIQUE_LAMP, 3), //4447 is placeholder for filter
				new ItemReward("A Blisterwood Flail", ItemID.BLISTERWOOD_FLAIL, 1));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
				new UnlockReward("Access to Darkmeyer and the Daeyalt Essence Mine"),
				new UnlockReward("Darkmeyer teleport via Drakan's Medallion."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Investigating Slepe",
			Arrays.asList(startQuest, talkToHameln, talkToCarl, inspectBarrel, followCarl, killKroy, destroyLab, talkToVeliafAfterKroy), combatGear));
		allSteps.add(new PanelDetails("Helping the Myreque",
			Arrays.asList(talkToVeliafInPater, talkToIvan, listenToMeeting)));
		allSteps.add(new PanelDetails("Escorting Ivan",
			Arrays.asList(talkToIvanAfterMeeting, searchForKnife, cutVines, combineVines, useVineOnBranch, swingOnVine, leaveSwingArea, killNailBeasts,
				leaveNailBeastArea, killZombieForAxe, get3LogsForBridge, repairBridge1, crossBridge, leaveBridgeArea, killJuvinates, leaveJuvinateArea), combatGear, ivandisFlail));
		allSteps.add(new PanelDetails("Learning of the Icyene",
			Arrays.asList(talkToIvanAfterTrek, talkToVeliafInBoatHouse, travelToGraveyard, talkToVeliafInGraveyard, talkToVanescula, doDoorPuzzle,
				convinceVertida, convinceKael, convinceRadigad, convincePolmafi, convinceIvan, convinceVeliaf, talkToVanesculaAfterTeam)));
		allSteps.add(new PanelDetails("Investigating the lab",
			Arrays.asList(goToLab, talkToSafalaanInLab, killBloodveld,
				talkToSafalaanInDeepLab, searchLabBookcase, takeBookToSafalaan), combatGear, pickaxe));
		allSteps.add(new PanelDetails("Making a disguise",
			Arrays.asList(talkToVanesculaAfterLab, talkToPolmafi, bringUnscentedToVanescula, talkToVeliafForFight, killDamien, talkToVeliafAfterDamien,
				talkToVanesculaAfterDamien), combatGear, vyrewatchOutfitOrCoins, ivandisFlail, antipoison));
		allSteps.add(new PanelDetails("Infiltrating Darkmeyer",
			Arrays.asList(enterDarkmeyer, talkToDesmodus, talkToMordan, talkToMaria, talkToDesmodusAgain, getNote, valveStep, cutLogs, bringVanesculaLogs), axe));
		allSteps.add(new PanelDetails("Creating a weapon",
			Arrays.asList(bringVertidaLogs, talkToVertidaForFlail, getSickle, addRubyToSickle, enchantRubySickle, useLogOnSickle, useFlailOnSickle), blisterwood8, chisel, knife, ruby, ivandisFlail, enchantRubyRunesOrTablet));
		allSteps.add(new PanelDetails("Taking on Drakan",
			Arrays.asList(talkToVanesculaWithFlail, talkToSafalaanWithFlail, talkToVanesculaBeforeFight,
				fightVanstromSidebar, finishQuest), combatGear, rangedWeaponForBloodveld, blisterwoodFlail));

		return allSteps;
	}
}
